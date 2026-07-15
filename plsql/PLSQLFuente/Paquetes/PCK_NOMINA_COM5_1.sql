
create or replace PACKAGE BODY "PCK_NOMINA_COM5" AS

  FUNCTION FC_MIMESADA
  /*
    NAME              : FC_MIMESADA  --> EN ACCESS MiMesada  -
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : ADRIANA MARITZA CACERES BONILLA
    DATE MIGRATION    : 28/07/2015
    TIME              : 09:11 PM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : DIEGO ALFREDO SUESCA RODRÃ¿GUEZ        /
                        (06/09/2017) PABLO ANDRES ESPITIA CUCA /
    DATE MODIFIED     : 09/07/2015
    TIMEUN_       : 2:30 PM
    DESCRIPTION       : RETORNA EL CONCEPTO RELACIONADO TABLA CONCEPTOS A PARTIR DE SU ID_DE_CONCEPTO /
                        (06/09/2017) Aplicar estandar de programacion PLSQL.
                                     Manejo de excepciones.
    @NAME:   obtenerMiMesada
  */
  (  
    UN_COMPANIA   IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_IDEMPLEADO IN  PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  ) 
  RETURN PCK_SUBTIPOS.TI_DOBLE
  AS
    MI_MESADA   PCK_SUBTIPOS.TI_DOBLE;
    MI_MSGERROR PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    BEGIN
      BEGIN
        SELECT MESADA_PENSIONAL
        INTO MI_MESADA
        FROM PERSONAL 
        WHERE COMPANIA       = UN_COMPANIA
          AND ID_DE_EMPLEADO = UN_IDEMPLEADO;

       EXCEPTION WHEN NO_DATA_FOUND THEN
         RAISE PCK_EXCEPCIONES.EXC_NOMINA;
       END; 

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
      MI_MSGERROR(1).CLAVE := 'CODIGO';
      MI_MSGERROR(1).VALOR := UN_IDEMPLEADO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_N_FM_NDF_VERIFICAPERSONAL
                                ,UN_TABLAERROR => 'PERSONAL'
                                ,UN_REEMPLAZOS => MI_MSGERROR);
    END;

    RETURN MI_MESADA;
  END FC_MIMESADA;

  --2
  FUNCTION FC_ESFACTOR_SS 
  /*
    NAME              : FC_ESFACTOR_SS  --> EN ACCESS ESFACTOR_SS  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRATION  : SANDRA MILENA DAZA LEGUIZAMÃ¿N
    DATE MIGRATION    : 15/08/2015
    TIME              : 03:15 PM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIED BY       : (06/09/2017) PABLO ANDRES ESPITIA CUCA /
    DESCRIPTION       :  0  -> El concepto no es usado para reporte de eps
                        -1 -> El concepto es usado para reporte de eps.
                        (06/09/2017) Aplicar estandar de programacion PLSQL.
                                     Manejo de excepciones.
    @NAME   : verificarConceptoFactorSS
  */
  (
    UN_COMPANIA  IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_CONCEPTO  IN  PCK_SUBTIPOS.TI_ID_DE_CONCEPTO    
  )
  RETURN PCK_SUBTIPOS.TI_LOGICO 
  AS 
    MI_CANT PCK_SUBTIPOS.TI_LOGICO;
  BEGIN
    SELECT COUNT(1) 
    INTO MI_CANT
    FROM CONCEPTOS 
    WHERE COMPANIA       = UN_COMPANIA
      AND ID_DE_CONCEPTO = UN_CONCEPTO
      AND REPORTEEPS NOT IN(0);

    RETURN CASE WHEN MI_CANT > 0 THEN -1 ELSE 0 END;
  END FC_ESFACTOR_SS;

  PROCEDURE PR_OPRIMIRSUMARRETROACTIVOS05 
  /*
    NAME              : PR_OPRIMIRSUMARRETROACTIVOS05
    AUTHORS           : STEFANINI SYSMAN  
    AUTHOR MIGRATION  : AURA LILIANA MONROY GARCIA
    DATE MIGRATION    : 21/03/2017
    TIME              : 17:30 PM
    SOURCE MODULE     : NOMINA(6)
    MODIFIED BY       : (06/09/2017) PABLO ANDRES ESPITIA CUCA
    DESCRIPTION       : Realiza la limpieza de la tabla temporal TEMP_SUMA_RETROACTIVO y la pobla con la informaciÂ¿btenida de los 
                        histÂ¿os teniendo en cuenta un rango de meses comprendidos en el aÂ¿ue ingresa por parÂ¿tro      						
    MODIFICATIONS     : (06/09/2017) Aplicar estandar de programacion PLSQL.
    PARAMETERS        : UN_COMPANIA => CompaÂ¿de ingreso a la aplicaciÂ¿                       UN_ANIO     => AÂ¿n el cual se desea realizar la suma de los histÂ¿os
    @NAME  :  sumarRetroactivosCinco
  */ 
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,  
    UN_ANIO     IN PCK_SUBTIPOS.TI_ANIO
  )
  AS 
    MI_MESFINAL   VARCHAR2(2 CHAR);
    MI_TABLA_T    PCK_SUBTIPOS.TI_TABLA := 'TEMP_SUMA_RETROACTIVO'; /*Tabla: TEMP_SUMA_RETROACTIVO*/
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;  
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA        PCK_SUBTIPOS.TI_RTA_ACME;
  BEGIN
    BEGIN
      MI_MESFINAL := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                          ,UN_NOMBRE    => 'HASTA QUE MES REALIZAR RETROACTIVO'
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE);

      IF MI_MESFINAL IS NULL THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
      END IF;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD 	 =>	SQLCODE
                                ,UN_ERROR_COD	 =>	PCK_ERRORES.ERRR_NOMINA_PARMESFINAL
                                ,UN_TABLAERROR =>	'PARAMETRO');
    END;

    BEGIN
      BEGIN
        MI_CONDICION := 'COMPANIA IS NOT NULL';

        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA_T
                                    ,UN_ACCION    => 'E'
                                    ,UN_CONDICION => MI_CONDICION);   

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;                                                                                                              
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD 		=>	SQLCODE
                                ,UN_ERROR_COD	  =>	PCK_ERRORES.ERRR_NOMINA_DELTMPSUMARETRO
                                ,UN_TABLAERROR 	=>	MI_TABLA_T);
    END;    

    BEGIN
      BEGIN
        MI_CAMPOS  := 'COMPANIA
                      ,ID_DE_PROCESO
                      ,ANO
                      ,PERIODO
                      ,ID_DE_EMPLEADO
                      ,ID_DE_CONCEPTO
                      ,VALOR';

        MI_VALORES := 'SELECT 
                          COMPANIA
                         ,ID_DE_PROCESO
                         ,ANO
                         ,PERIODO
                         ,ID_DE_EMPLEADO
                         ,ID_DE_CONCEPTO
                         ,SUM(VALOR) VALOR 
                       FROM HISTORICOS  
                       WHERE COMPANIA = '''||UN_COMPANIA||''' 
                         AND ANO      =   '||UN_ANIO    ||'
                         AND MES BETWEEN 1 AND '||MI_MESFINAL||'
                         AND PERIODO  = 5 
                       GROUP BY
                          COMPANIA
                         ,ID_DE_PROCESO
                         ,ANO
                         ,PERIODO
                         ,ID_DE_EMPLEADO
                         ,ID_DE_CONCEPTO ';

        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA_T
                                    ,UN_ACCION    => 'IS'
                                    ,UN_CAMPOS    => MI_CAMPOS
                                    ,UN_VALORES   => MI_VALORES);       

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD 		=>	SQLCODE
                                ,UN_ERROR_COD	  =>	PCK_ERRORES.ERRR_NOMINA_INSTMPSUMARETRO
                                ,UN_TABLAERROR 	=>	MI_TABLA_T);
    END;
  END PR_OPRIMIRSUMARRETROACTIVOS05;

PROCEDURE PR_ACTCIIU
/*
    NAME              : PR_ACTCIIU
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDWIN CABRERA
    DESCRIPTION       : ACTUALIZA EL CAMPO DE ACTIVIDAD ECONOMICA CIIU DE PERSONAL A PERSONAL_HISTORICO, SI EN LA ULTIMA ESTA NULO
                        14/04/2015 - SE ADICIONA EL FONDO DE CESANTIAS QUE NO ESTA DENTRO DE LA RUTINA DE LA NOMINA UNIFICADA
                        24/10/2015 - SE INCLUYE EL MERGE Y ADEMAS SE ACTUALIZAN TODOS LOS CAMPOS
                        26/10/2017 - MODIFICACION MANEJO DE EXCEPCIONES, SE AGREGARON CAMPOS DE AUDITORIA
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO        IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES            IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO        IN PCK_SUBTIPOS.TI_PERIODO_NOMI
  )
  AS
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_USING          PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE    VARCHAR2(32000 CHAR);
    MI_MERGENOEXIS    PCK_SUBTIPOS.TI_MERGENOEXISTE;
BEGIN
	UPDATE  PERSONAL_HISTORICO
	SET 	ID_CIIU = (
						SELECT 	ID_CIIU 
						FROM 	PERSONAL 
						WHERE 	PERSONAL.COMPANIA = PERSONAL_HISTORICO.COMPANIA
								AND PERSONAL.ID_DE_EMPLEADO = PERSONAL_HISTORICO.ID_DE_EMPLEADO
						)
	WHERE 	COMPANIA = UN_COMPANIA
			AND ID_DE_PROCESO = UN_PROCESO
			AND ANO = UN_ANIO
			AND MES = UN_MES
			AND PERIODO = UN_PERIODO
			AND ID_CIIU IS NULL
			;
	
	COMMIT;
END PR_ACTCIIU;

  --6
FUNCTION FC_GENERARDISCO
/*
  NAME              : FC_GENERARDISCO
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : LEYDI MILENA CORTÂ¿ FORERO
  DATE MIGRATION    : 22/03/2017
  TIME              : 11:37 AM
  SOURCE MODULE     : NOMINA (6)
  MODIFIED BY       : (07/09/2017) PABLO ANDRES ESPITIA CUCA
  MODIFICATIONS     : (07/09/2017) Aplicar estandar de programacion PLSQL.
                                   Adicion de campos de auditoria.
  DESCRIPTION       : FunciÂ¿ue permite llamar las funciones que construyen los discos de autoliquidaciÂ¿                     e integrado, que se retornarÂ¿para generar el archivo integrado electrÂ¿o.
  PARAMETERS        : UN_COMPANIA        => CompaÂ¿de ingreso a la aplicaciÂ¿                      UN_TIPOLIQUIDACION => Tipo de liquidaciÂ¿e nÂ¿a seleccionado en el Formulario
                                            Integrado ElectrÂ¿o (Ruta: Panel Principal\NÂ¿a\Utilidades\
                                            GeneraciÂ¿e autoliquidaciones integrada\AutoliquidaciÂ¿ctivos).
                      UN_PROCESONOMINA   => CÂ¿o del proceso seleccionado al establecer el periodo de trabajo al
                                            ingresar al mÂ¿o de nÂ¿a.
                      UN_ANIONOMINA      => Anio seleccionado al establecer el periodo de trabajo al ingresar
                                            al mÂ¿o de nÂ¿a.
                      UN_MESNOMINA       => Mes seleccionado al establecer el periodo de trabajo al ingresar
                                            al mÂ¿o de nÂ¿a.
                      UN_PERIODONOMINA   => Periodo seleccionado al establecer el periodo de trabajo al ingresar
                                            al mÂ¿o de nÂ¿a.
                      UN_ESTRUCTURA      => Tipo de estructura de nÂ¿a seleccionado en el formulario.
                      UN_PLANILLA        => Tipo de planilla, se define de la lista de tipo de estructura generada.
                      UN_CORRECCION      => Valor que identifica si el archivo a generar incluye correciÂ¿-1 SÂ¿cluye, 0 No incluye).
                      UN_NUMCORRECCION   => NÂ¿o de la planilla a corregir.
                      UN_FECHACORRECCION => Fecha Pago de la planilla corregida.
                      UN_ANIOCORRECCION  => Anio de la correcciÂ¿                      UN_MESCORRECCION   => Mes de la correcciÂ¿                      UN_NITCOMPANIA     => CÂ¿o Nit de la compaÂ¿de ingreso a la aplicaciÂ¿                      UN_RETROACTIVO     => Valor que identifica si el archivo a generar incluye retroactivo (-1 SÂ¿cluye, 0 No incluye).
                      UN_PERIODORETRO    => CÂ¿o del periodo retroactivo.
                      UN_FECHAAUTO       => Fecha de autoliquidaciÂ¿signada como el Â¿mo dÂ¿del mes del anio del periodo
                                            de trabajo seleccionado al ingresar al mÂ¿o.
                      UN_NUMRADICACION   => NÂ¿o de radicaciÂ¿ndicado en el formulario.
                      UN_ORDEN           => Orden de salida del plano
                      UN_IDEMPLEADO      => Por defecto es 0 sino se crea el plano solo para un empleado
                      UN_USUARIO         => Codigo del usuario que desencadena el proceso.
 @Name: generarDisco
*/
(
  UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOLIQUIDACION      IN PCK_SUBTIPOS.TI_ENTERO,
  UN_PROCESONOMINA        IN PCK_SUBTIPOS.TI_ENTERO,
  UN_ANIONOMINA           IN PCK_SUBTIPOS.TI_ANIO,
  UN_MESNOMINA            IN PCK_SUBTIPOS.TI_ENTERO,
  UN_PERIODONOMINA        IN PCK_SUBTIPOS.TI_ENTERO,
  UN_ESTRUCTURA           IN VARCHAR2,
  UN_PLANILLA             IN VARCHAR2,
  UN_CORRECCION           IN PCK_SUBTIPOS.TI_ENTERO,
  UN_NUMCORRECCION        IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 0,
  UN_FECHACORRECCION      IN DATE DEFAULT TO_DATE('01/01/1900','DD/MM/YYYY'),
  UN_ANIOCORRECCION       IN PCK_SUBTIPOS.TI_ANIO,
  UN_MESCORRECCION        IN PCK_SUBTIPOS.TI_ENTERO,
  UN_NITCOMPANIA          IN COMPANIA.NITCOMPANIA%TYPE,
  UN_RETROACTIVO          IN PCK_SUBTIPOS.TI_ENTERO,
  UN_PERIODORETRO         IN PCK_SUBTIPOS.TI_ENTERO,
  UN_FECHAAUTO            IN DATE,
  UN_NUMRADICACION        IN PCK_SUBTIPOS.TI_ENTERO,
  UN_ORDEN                IN PCK_SUBTIPOS.TI_LOGICO :=1,
  UN_IDEMPLEADO           IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO :=0,
  UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB
AS
  MI_RTADISCOAUTOLIQ CLOB;
  MI_RTADISCOINT     CLOB;
  MI_RESPUESTA       CLOB;
BEGIN
  --(MZANGUNA:24/11/2018)-Permite caracteres especiales plano
  GL_PERMITECARACTERESP :=  CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                              ,UN_NOMBRE    => 'PERMITIR CARACTERES ESPECIALES EN PLANO PILA'
                              ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                              ,UN_FECHA_PAR => SYSDATE),'NO') = 'NO' THEN FALSE ELSE TRUE END;

  PCK_NOMINA.GL_PROCESOACTUAL:= UN_PROCESONOMINA;
  PCK_NOMINA.GL_ANOACTUAL    := UN_ANIONOMINA;
  PCK_NOMINA.GL_MESACTUAL    := UN_MESNOMINA;
  PCK_NOMINA.GL_PERIODOACTUAL:= UN_PERIODONOMINA;
  PCK_CONEXION.PR_SETUSER(UN_USUARIO);
  PCK_NOMINA_COM5.GL_DISCOLIQUIDACION :='';
  --(APINEDA:26/06/2019)-Se agrega tipo de liquidaciÂ¿ (Retroactivo).
  IF UN_TIPOLIQUIDACION = 1 OR UN_TIPOLIQUIDACION = 2 THEN
    PCK_NOMINA_COM5.PR_SISTEMAINTEGRADOELECTRONICO(UN_COMPANIA         => UN_COMPANIA
                                                  ,UN_PROCESO          => UN_PROCESONOMINA
                                                  ,UN_ANIO             => UN_ANIONOMINA
                                                  ,UN_MES              => UN_MESNOMINA
                                                  ,UN_PERIODO          => UN_PERIODONOMINA
                                                  ,UN_TIPOESTRUCTURA   => UN_ESTRUCTURA
                                                  ,UN_TIPOPLANILLA     => UN_PLANILLA
                                                  ,UN_CORRECCION       => UN_CORRECCION
                                                  ,UN_NUMCORRECCION    => UN_NUMCORRECCION
                                                  ,UN_FECHACORRECCION  => UN_FECHACORRECCION
                                                  ,UN_ANIOCORRECCION   => UN_ANIOCORRECCION
                                                  ,UN_MESCORRECCION    => UN_MESCORRECCION
                                                  ,UN_NITCOMPANIA      => UN_NITCOMPANIA
                                                  ,UN_RETROACTIVO      => UN_RETROACTIVO
                                                  ,UN_PERIODORETRO     => UN_PERIODORETRO
                                                  ,UN_FECHALIQUIDACION => UN_FECHAAUTO
                                                  ,UN_NUMRADICACION    => UN_NUMRADICACION
                                                  ,UN_ORDEN            => UN_ORDEN
                                                  ,UN_IDEMPLEADO       => UN_IDEMPLEADO
                                                  ,UN_USUARIO          => UN_USUARIO);

  ELSIF UN_TIPOLIQUIDACION = 3 THEN
    PCK_NOMINA_COM5.PR_SISTEMAINTEGRADOELECTRO_P(UN_COMPANIA         => UN_COMPANIA
                                                  ,UN_PROCESO          => UN_PROCESONOMINA
                                                  ,UN_ANIO             => UN_ANIONOMINA
                                                  ,UN_MES              => UN_MESNOMINA
                                                  ,UN_PERIODO          => UN_PERIODONOMINA
                                                  ,UN_TIPOESTRUCTURA   => UN_ESTRUCTURA
                                                  ,UN_TIPOPLANILLA     => UN_PLANILLA
                                                  ,UN_CORRECCION       => UN_CORRECCION
                                                  ,UN_NUMCORRECCION    => UN_NUMCORRECCION
                                                  ,UN_FECHACORRECCION  => UN_FECHACORRECCION
                                                  ,UN_ANIOCORRECCION   => UN_ANIOCORRECCION
                                                  ,UN_MESCORRECCION    => UN_MESCORRECCION
                                                  ,UN_NITCOMPANIA      => UN_NITCOMPANIA
                                                  ,UN_RETROACTIVO      => UN_RETROACTIVO
                                                  ,UN_PERIODORETRO     => UN_PERIODORETRO
                                                  ,UN_FECHALIQUIDACION => UN_FECHAAUTO
                                                  ,UN_NUMRADICACION    => UN_NUMRADICACION
                                                  ,UN_ORDEN            => UN_ORDEN
                                                  ,UN_IDEMPLEADO       => UN_IDEMPLEADO
                                                  ,UN_USUARIO          => UN_USUARIO);
  END IF;

  MI_RTADISCOAUTOLIQ := GL_DISCOLIQUIDACION ;
  MI_RTADISCOINT := GL_DISCOINTEGRADO;
  MI_RESPUESTA := MI_RTADISCOAUTOLIQ || '<siguiente>' || MI_RTADISCOINT;

  RETURN MI_RESPUESTA;
END FC_GENERARDISCO;

PROCEDURE PR_SISTEMAINTEGRADOELECTRONICO
(
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROCESO         IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_ANIO            IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES             IN PCK_SUBTIPOS.TI_MES,
  UN_PERIODO         IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
  UN_TIPOESTRUCTURA  IN VARCHAR2,
  UN_TIPOPLANILLA    IN VARCHAR2,
  UN_CORRECCION      IN PCK_SUBTIPOS.TI_LOGICO,
  UN_NUMCORRECCION   IN PCK_SUBTIPOS.TI_ENTERO_LARGO :=0,
  UN_FECHACORRECCION IN DATE  DEFAULT TO_DATE('01/01/1900','DD/MM/YYYY') ,
  UN_ANIOCORRECCION  IN PCK_SUBTIPOS.TI_ANIO,
  UN_MESCORRECCION   IN PCK_SUBTIPOS.TI_MES,
  UN_NITCOMPANIA     IN VARCHAR2,
  UN_RETROACTIVO     IN PCK_SUBTIPOS.TI_LOGICO,
  UN_PERIODORETRO    IN PCK_SUBTIPOS.TI_LOGICO,
  UN_FECHALIQUIDACION IN DATE,
  UN_NUMRADICACION    IN PCK_SUBTIPOS.TI_ENTERO :='',
  UN_ORDEN            IN PCK_SUBTIPOS.TI_LOGICO ,
  UN_IDEMPLEADO       IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO ,
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
  MI_STRSQL     PCK_SUBTIPOS.TI_STRSQL;

  MI_TXTCORRECION VARCHAR2(10);
  MI_FECHAPAGOCORRECCIONPLANILLA  VARCHAR2(10);

  MI_TOTALPENSIONES11       NUMBER:=0;
  MI_TOTALSALUD11           NUMBER:=0;
  MI_TOTALRIESGOS11         NUMBER:=0;
  MI_TOTALCAJAS11           NUMBER:=0;
  MI_TOTALSENA11            NUMBER:=0;
  MI_TOTALICBF11            NUMBER:=0;
  MI_TOTALESAP11            NUMBER:=0;
  MI_TOTALINSTITUTOS11      NUMBER:=0;
  MI_TOTSALUD               NUMBER:=0;
  MI_TOTPENSION             NUMBER:=0;
  MI_TOTRIESGOS             NUMBER:=0;
  MI_TOTIBC                 NUMBER:=0;
  MI_TOTSUELDO              NUMBER:=0;
  MI_TOTNETO                NUMBER:=0;
  MI_TOTDIAS                NUMBER:=0;
  MI_TOTAPVOL               NUMBER:=0;
  MI_NUMADMPENSIONES        NUMBER:=0;
  MI_NUMADMSALUD            NUMBER:=0;
  MI_NUMADMARP              NUMBER:=0;
  MI_NUMADMCAJAS            NUMBER:=0;
  MI_NUMEMPLEADOS           NUMBER:=0;
  MI_TOTALPAGO              NUMBER:=0;
  MI_TOTALBASEPARAFISCAL    NUMBER:=0;
  MI_SALARIOMINIMO          NUMBER:=0;
  MI_NUMRADICA              VARCHAR2(10);
  MI_NITCOMPANIA            VARCHAR2(20);
  MI_NITF                   VARCHAR2(20);
  MI_NIT                    VARCHAR2(20);
  MI_PERCOTIZ               VARCHAR2(20);
  MI_RAZONSOCIAL            VARCHAR2(200);
  MI_TIPONIT                VARCHAR2(100);
  MI_FORMAPRESENTACION      VARCHAR2(20);
  MI_SUCURSALAPORTANTE      VARCHAR2(10);
  MI_NOMBRESUCURSAL         VARCHAR2(40);
  MI_TIPOAPORTANTE          VARCHAR2(20);
  MI_PARA                   VARCHAR2(20);
  MI_NUMEMP                 PCK_SUBTIPOS.TI_ENTERO;
  MI_RESG                   PCK_SUBTIPOS.TI_ENTERO;
  MI_INCAPE                 NUMBER:=0;
  MI_AC                     NUMBER:=0;
  MI_FECHACAMBIORIEGO  DATE;
  MI_ADS              NUMBER:=0;
  MI_NUMINCAP         VARCHAR2(9);
  MI_NUMLICMATER      VARCHAR2(9);
  MI_LINN             NUMBER:=0;

  MI_DIASPARAFISCALES VARCHAR2(30);
  MI_GENERARNOVEDADESPILA2388   NUMBER:=0;
  MI_CAMPOSNUEVOS2388N  CLOB;
  MI_CAMPOSNUEVOS2388 CLOB;
  MI_TIPODCTO         VARCHAR2(3);
  MI_GRANTOTAL       NUMBER:=0;
  MI_MSGERROR PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CUENTA     PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  UN_PERIODO_AUX        NUMBER:=0; -- JM 7736030
  MI_PERIODO_RETRO  NUMBER := 0;--(CC:3708_CFBARRERA)
  MI_ACUM               NUMBER:=0; -- JM CC 4258


BEGIN
  --CARGA EL VECTOR DE PARAMETROS DE ENTRADA
  PCK_PARENTR.PR_CARGAR_PARAMETROSENTRADA(UN_COMPANIA => UN_COMPANIA);
  PCK_NOMINA_COM5.PR_INICIARPARAMETROSPLANOSOI(UN_COMPANIA => UN_COMPANIA);

    -- TICKET 7722577 ECABRERA: ACTUALIZACION CAMPO ID_CIIU DE PERSONAL HISTORICO
  PCK_NOMINA_COM5.PR_ACTCIIU     (
                                  UN_COMPANIA => UN_COMPANIA,
                                  UN_PROCESO => UN_PROCESO,
                                  UN_ANIO => UN_ANIO,
                                  UN_MES => UN_MES,
                                  UN_PERIODO => UN_PERIODO
                                  ) ;
  -- TICKET 7722577 ECABRERA 

  GL_DISCOINTEGRADO:='';
  MI_LINN:=0;
  BEGIN
    MI_CONDICION := 'COMPANIA=''' || UN_COMPANIA || '''';
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'ERRORES',
                                          UN_ACCION    => 'E',
                                          UN_CONDICION => MI_CONDICION);
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
    PCK_NOMINA.GL_STRETAPAGLOBAL:='Al elminar los Errores';
    RAISE PCK_EXCEPCIONES.EXC_NOMINA;
  END;
  PCK_NOMINA.GL_STRETAPAGLOBAL   := '1-Inicio';
  MI_TXTCORRECION                := UN_TIPOPLANILLA;
  PCK_NOMINA_COM5.GL_PLANILLAACORREGIR           := RPAD(' ',10,' ');
  MI_FECHAPAGOCORRECCIONPLANILLA := RPAD(' ',10,' ');
  IF UN_CORRECCION <> 0 THEN
    MI_TXTCORRECION := 'X';
    IF UN_NUMCORRECCION=0 THEN
      --MSGBOX "SI TIENE INDICADOR DE CORRECCION, DEBE DIGITAR NÃ‚Â¿ERO DE PLANILLA A CORREGIR"
      PCK_NOMINA_COM5.GL_PLANILLAACORREGIR := RPAD(' ',10,' ');
    ELSE
      PCK_NOMINA_COM5.GL_PLANILLAACORREGIR         := RPAD(UN_NUMCORRECCION, 10);
    END IF;
    IF UN_FECHACORRECCION= TO_DATE('01/01/1900','DD/MM/YYYY') THEN
      MI_FECHAPAGOCORRECCIONPLANILLA := TO_CHAR(LAST_DAY(TO_DATE('01/' || UN_MESCORRECCION || '/' || UN_ANIOCORRECCION, 'DD/MM/YYYY')), 'YYYY-MM-DD');
    ELSE
      MI_FECHAPAGOCORRECCIONPLANILLA := TO_CHAR(UN_FECHACORRECCION, 'YYYY-MM-DD');
    END IF;
  END IF;

  PCK_NOMINA_COM5.GL_CODAICC :=RPAD(' ', 6, ' ');
  PCK_NOMINA_COM5.GL_CODAIFS :=RPAD(' ', 6, ' ');
  PCK_NOMINA_COM5.GL_CODAIFP :=RPAD(' ', 6, ' ');
  PCK_NOMINA_COM5.GL_SALARIOINTEGRAL := ' ';
  PCK_NOMINA_COM5.GL_PR := PCK_PARENTR.PARAMETRO39 - TRUNC(PCK_PARENTR.PARAMETRO39);
  IF PCK_NOMINA_COM5.GL_PR >0 AND PCK_NOMINA_COM5.GL_PR < 1 THEN
    PCK_NOMINA_COM5.GL_PR := 10;
  ELSE
    PCK_NOMINA_COM5.GL_PR :=1;
  END IF;

  MI_TOTALPENSIONES11 :=0;
  MI_TOTALSALUD11     :=0;
  MI_TOTALRIESGOS11   :=0;
  MI_TOTALCAJAS11     :=0;
  MI_TOTALSENA11      :=0;
  MI_TOTALICBF11      :=0;
  MI_TOTALESAP11      :=0;
  MI_TOTALINSTITUTOS11:=0;
  MI_NUMADMPENSIONES  :=0;
  MI_NUMADMSALUD      :=0;
  MI_NUMADMARP        :=0;
  MI_NUMADMCAJAS      :=0;

  MI_TOTSALUD         :=0;
  MI_TOTPENSION       :=0;
  MI_TOTRIESGOS       :=0;
  MI_TOTIBC           :=0;
  MI_TOTSUELDO        :=0;
  MI_TOTNETO          :=0;
  MI_TOTDIAS          :=0;
  MI_TOTAPVOL         :=0;
  GL_CONTADOR         :=0;
  GL_SUELDO           :=0;
  GL_SUELDOP          :=0;


  MI_NITF := PCK_PARENTR.PARAMETRO31;
  MI_NITF :=REPLACE(MI_NITF, '.', '');
  MI_NITF :=REPLACE(MI_NITF, '-', '');
  MI_NITF :=SUBSTR(MI_NITF,1,9);
  MI_NIT  :=MI_NITF;
  MI_TIPOAPORTANTE :=PCK_PARENTR.PARAMETRO32;
  PCK_NOMINA.GL_STRETAPAGLOBAL := '2 parametros de entrada';


  MI_PERCOTIZ := UN_ANIO || PCK_SYSMAN_UTL.FC_STRZERO(UN_MES, 2);
  --(APINEDA:08/10/2019)-Se agrega funciÃƒÂ³n para eliminar caracteres especiales en la razÃƒÂ³n social TAR 1000094968 
  MI_RAZONSOCIAL:= PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(PCK_PARENTR.PARAMETRO30, ''));  
  MI_RAZONSOCIAL:= CASE WHEN INSTR(MI_RAZONSOCIAL, '-', 1, 3) <= 0
                        THEN MI_RAZONSOCIAL
                        ELSE SUBSTR(MI_RAZONSOCIAL,1, INSTR(MI_RAZONSOCIAL, '-', 1, 3)-1) END;
  MI_RAZONSOCIAL:= RPAD(UPPER(MI_RAZONSOCIAL),200, ' ');
  MI_TIPONIT:=NVL(PCK_PARENTR.PARAMETRO60, 'NI');
  MI_TIPONIT:=CASE WHEN MI_TIPONIT = 'C'  THEN 'CC' ELSE MI_TIPONIT END;
  MI_TIPONIT:=CASE WHEN MI_TIPONIT IN('','N') THEN 'NI' ELSE MI_TIPONIT END;

  MI_FORMAPRESENTACION:=NVL(PCK_PARENTR.PARAMETRO33, 'U');
  IF MI_FORMAPRESENTACION IN('S','D') THEN
    MI_SUCURSALAPORTANTE := '' || PCK_PARENTR.PARAMETRO34;
    MI_NOMBRESUCURSAL    := '' || PCK_PARENTR.PARAMETRO65;
  END IF;
  --NumCORr := IIF(IsNull(UN_NUMCORRECCION), Space(10), Pad(UN_NUMCORRECCION, 10));
  PCK_NOMINA.GL_STRETAPAGLOBAL := '3-paramtros de entrada';

  PCK_NOMINA.GL_STRETAPAGLOBAL := '4. consulta datos';
  IF PCK_NOMINA.GL_MANSGP AND NVL(UN_TIPOPLANILLA, 'E') = 'T' THEN
    PCK_NOMINA.GL_SGP_Y_TIPO_T := -1;
  END IF;

    BEGIN 
    SELECT COUNT(*) INTO MI_ACUM 
    FROM HISTORICOS
    WHERE COMPANIA = UN_COMPANIA
    AND ANO = UN_ANIO
    AND MES = UN_MES
    AND PERIODO = 5
    AND ID_DE_PROCESO = 1
    AND ID_DE_CONCEPTO IN (306);
  EXCEPTION WHEN OTHERS THEN
    MI_ACUM := 0;
  END;

  IF MI_ACUM <> 0 THEN 
  
        MI_STRSQL := FC_ARMARVSOIACUM
    (
      UN_COMPANIA         => UN_COMPANIA,
        UN_EMPLEADO         => UN_IDEMPLEADO,
        UN_REDONDEO         => NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'REDONDEAR SEGURIDAD SOCIAL',6,SYSDATE),'SI'),
        UN_ANIO             => UN_ANIO,
        UN_MES              => UN_MES,
        UN_PERIODO          => UN_PERIODO,
        UN_NOMINA           => NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'TIPO DE NOMINA ACTIVOS O PENSIONADOS',6,SYSDATE,-1),'ACTIVOS'),
        UN_PAGAR_DIAS       => NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PAGAR DIAS TRABAJADOS EN LIQUIDACION DEFINITIVA',6,SYSDATE,-1),'NO'),
        UN_FC_SGP_Y_TIPO_T  => PCK_NOMINA.GL_SGP_Y_TIPO_T,
        UN_RETROACTIVO      => UN_RETROACTIVO,
        UN_TIPOPLANILLA     => UN_TIPOPLANILLA,
        UN_ORDEN            => UN_ORDEN
    ); -- para cuando pagan el retro acumulado 
  
  ELSE 

      MI_STRSQL := FC_ARMARVSOI
    (
      UN_COMPANIA         => UN_COMPANIA,
        UN_EMPLEADO         => UN_IDEMPLEADO,
        UN_REDONDEO         => NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'REDONDEAR SEGURIDAD SOCIAL',6,SYSDATE),'SI'),
        UN_ANIO             => UN_ANIO,
        UN_MES              => UN_MES,
        UN_PERIODO          => UN_PERIODO,
        UN_NOMINA           => NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'TIPO DE NOMINA ACTIVOS O PENSIONADOS',6,SYSDATE,-1),'ACTIVOS'),
        UN_PAGAR_DIAS       => NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PAGAR DIAS TRABAJADOS EN LIQUIDACION DEFINITIVA',6,SYSDATE,-1),'NO'),
        UN_FC_SGP_Y_TIPO_T  => PCK_NOMINA.GL_SGP_Y_TIPO_T,
        UN_RETROACTIVO      => UN_RETROACTIVO,
        UN_TIPOPLANILLA     => UN_TIPOPLANILLA,
        UN_ORDEN            => UN_ORDEN
    ); -- JM CC4235 OPTIMIZAR TIEMPO EN LA CONSULTA evitando llamar la vista directamente 

  END IF;
  
  /* --(APINEDA:11/07/2019)-Se modificÃƒÂ³ vista V_SOI y la consulta sobre esta, para tener en cuenta los datos de retroactivo.
  MI_STRSQL :='SELECT *
              FROM V_SOI
              WHERE COMPANIA     =''' || UN_COMPANIA || '''
               AND ID_DE_PROCESO NOT IN(98)
               AND ANO           =' || UN_ANIO || '
               AND MES           =' || UN_MES ||
               CASE WHEN UN_IDEMPLEADO=0
               THEN ' AND ID_DE_EMPLEADO <>0'
               ELSE ' AND ID_DE_EMPLEADO=' || UN_IDEMPLEADO END;
  --(APINEDA:16/07/2019)-Se agrega secciÃƒÂ³n para especificar filtros que se aplican cuando el plano a generar es de Retroactivo.               
  IF UN_RETROACTIVO <> 0 THEN
    --(APINEDA:13/04/2020)-Se excluyen empleados SENA (Tipo vinculaciÃƒÂ³n 12 y 19) del plano de retroactivo.
    MI_STRSQL :=MI_STRSQL ||' AND TIPOVINCULACION NOT IN (12,19) AND NOT (NOVEDAD_TIPONOVEDAD = 3 AND (SUBSTR(NOVEDAD_LLAVENOVEDAD, INSTR(NOVEDAD_LLAVENOVEDAD,''--'',1,4) + 2, LENGTH(NOVEDAD_LLAVENOVEDAD)) NOT IN (''01'',''02'',''03'',''04'',''05'',''10'',''12'')))';
  ELSE    
    MI_STRSQL :=MI_STRSQL ||' AND ID_DE_PROCESO NOT IN(10)';
  END IF;

  IF NVL(UN_TIPOPLANILLA, 'E') = 'K' THEN
    MI_STRSQL :=MI_STRSQL ||' AND TIPOVINCULACION = 23';
  ELSE
    MI_STRSQL :=MI_STRSQL ||' AND TIPOVINCULACION <> 23';
  END IF;
  IF PCK_NOMINA.GL_SGP_Y_TIPO_T <>0 THEN
    MI_STRSQL :=MI_STRSQL ||' AND TIPOVINCULACION =47';
  END IF;
  IF UN_ORDEN = 1 THEN
    MI_STRSQL := MI_STRSQL || ' ORDER BY APELLIDO1, APELLIDO2, FECHA_DE_INGRESO, ID_DE_EMPLEADO, NOMBRES';
  ELSE
    MI_STRSQL := MI_STRSQL || ' ORDER BY NUMERO_DCTO, FECHA_DE_INGRESO, ID_DE_EMPLEADO, NOMBRES';
  END IF;
  --(APINEDA:11/07/2019)-Se agrega orden por tipo de novedad y proceso para mostrar registro del periodo original y del periodo nÃƒÂ³mina RETROACTIVO en el plano.
  IF UN_RETROACTIVO <> 0 THEN
    MI_STRSQL := MI_STRSQL || ', NOVEDAD_TIPONOVEDAD, NOVEDAD_CONSECUTIVO, ID_DE_PROCESO';
  END IF; */  -- comentado por JM CC4235   
  EXECUTE IMMEDIATE MI_STRSQL BULK  COLLECT INTO PCK_NOMINA_COM5.CSOI;
  IF PCK_NOMINA_COM5.CSOI.COUNT<=0 THEN
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD =>SQLCODE,
          UN_ERROR_COD=>PCK_ERRORES.ERR_NOEXISTENEMPLEADOS
      );
    END;
  END IF;
  MI_NUMEMPLEADOS := 0;
  MI_TOTALPAGO    := 0;
  MI_TOTALBASEPARAFISCAL := 0;

  FOR I IN PCK_NOMINA_COM5.CSOI.FIRST .. PCK_NOMINA_COM5.CSOI.LAST
  LOOP
    PCK_NOMINA.P:= I;
    PCK_NOMINA_COM5.GL_CODIGOFONDORIESGOS:= RPAD(PCK_NOMINA_COM5.CSOI(I).FONDO_RIESGOS_SOI, 6,' ');
    IF PCK_NOMINA_COM5.CSOI(I).PAGOS > 0 OR PCK_NOMINA_COM5.CSOI(I).ESTADO_ACTUAL = 6 THEN
      IF PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION = '23' AND UN_TIPOPLANILLA = 'K' THEN
        MI_NUMEMPLEADOS := MI_NUMEMPLEADOS + 1 ;
        PCK_NOMINA.GL_STRETAPAGLOBAL := '6 sumando totales 2';
        MI_TOTALPAGO := MI_TOTALPAGO + PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     =>PCK_NOMINA_COM5.CSOI(I).PAGOS
                                                              ,UN_PRECISION =>   0);
        --MI_TOTALBASEPARAFISCAL := MI_TOTALBASEPARAFISCAL + PCK_NOMINA_COM5.CSOI(I).PAGOS; -- JM 7731689 SOLO EL CONCEPTO 108 
      ELSIF PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION <> '23' AND UN_TIPOPLANILLA <> 'K' THEN
        MI_NUMEMPLEADOS := MI_NUMEMPLEADOS + 1 ;
        MI_TOTALPAGO := MI_TOTALPAGO + PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     =>PCK_NOMINA_COM5.CSOI(I).PAGOS
                                                              ,UN_PRECISION =>   0)
                      + CASE WHEN PCK_NOMINA_COM5.CSOI(I).ESTADO_ACTUAL = 6
                        THEN PCK_NOMINA_COM5.CSOI(I).BASE ELSE 0 END;
        --MI_TOTALBASEPARAFISCAL := MI_TOTALBASEPARAFISCAL + PCK_NOMINA_COM5.CSOI(I).PAGOS; -- JM 7731689 SOLO EL CONCEPTO 108
        MI_TOTALBASEPARAFISCAL := MI_TOTALBASEPARAFISCAL
                                + CASE WHEN PCK_NOMINA_COM5.CSOI(I).DIASLNR <> 0
                                        AND PCK_NOMINA_COM5.CSOI(I).BASEPARAFISCAL = 0
                                  THEN PCK_NOMINA_COM5.CSOI(I).BASESALUD
                                  ELSE PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(I).BASEPARAFISCAL
                                                              ,UN_PRECISION =>   PCK_NOMINA.GL_RBASE1990)
                                  END
                                + CASE WHEN PCK_NOMINA_COM5.CSOI(I).ESTADO_ACTUAL = 6
                                        AND PCK_NOMINA_COM5.CSOI(I).BASESALUD = 0
                                  THEN PCK_NOMINA_COM5.CSOI(I).BASE
                                  ELSE 0
                                  END;
        PCK_NOMINA.GL_STRETAPAGLOBAL := '6 sumando totales';
      END IF;
    ELSE
      IF PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION = '23' AND UN_TIPOPLANILLA = 'K' THEN
        MI_NUMEMPLEADOS := MI_NUMEMPLEADOS + 1 ;
        PCK_NOMINA.GL_STRETAPAGLOBAL := '6 sumando totales 2';
        MI_TOTALPAGO := MI_TOTALPAGO + PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     =>PCK_NOMINA_COM5.CSOI(I).PAGOS
                                                              ,UN_PRECISION =>   0);
        --MI_TOTALBASEPARAFISCAL := MI_TOTALBASEPARAFISCAL + PCK_NOMINA_COM5.CSOI(I).PAGOS; -- JM 7731689 SOLO EL CONCEPTO 108
      ELSIF PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION <> '23' THEN
        IF PCK_NOMINA_COM5.CSOI(I).DIASINCAPCIDADES <> 0 THEN
          MI_NUMEMPLEADOS := MI_NUMEMPLEADOS + 1 ;
        END IF;
        PCK_NOMINA.GL_STRETAPAGLOBAL := '6 sumando totales 2';
        MI_TOTALPAGO := MI_TOTALPAGO + PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(I).PAGOS
                                                              ,UN_PRECISION => 0);
        MI_TOTALBASEPARAFISCAL := MI_TOTALBASEPARAFISCAL
                                + CASE WHEN PCK_NOMINA_COM5.CSOI(I).DIASLNR <> 0
                                        AND PCK_NOMINA_COM5.CSOI(I).BASEPARAFISCAL = 0
                                  THEN PCK_NOMINA_COM5.CSOI(I).BASESALUD
                                  ELSE PCK_NOMINA_COM5.CSOI(I).BASEPARAFISCAL
                                  END;
      END IF;
    END IF;
  END LOOP;
  /*
  IF UN_TIPOESTRUCTURA = 2388 THEN
    actualizahfacamposnuevos := True;
  END IF;
  */
  --GL_CODIGOFONDORIESGOS := RPAD(' ',6,' ');
  --GL_CODIGOFONDORIESGOS := RPAD(GL_CODIGOFONDORIESGOS, 6,' ');
  GL_CODIGOFONDORIESGOS := CASE WHEN GL_CODIGOFONDORIESGOS IN('',RPAD(' ',6,' '))
                           THEN RPAD(' ',6,' ')
                           ELSE GL_CODIGOFONDORIESGOS END;
  PCK_NOMINA.GL_STRETAPAGLOBAL := '7 imprime registro 01';

    PCK_NOMINA_COM5.GL_DISCOLIQUIDACION :=
                    /*CAMPO1*/    '01'
                    /*CAMPO2*/ || '0'
                    /*CAMPO3*/ || '0001'
                    /*CAMPO4*/ ||  MI_RAZONSOCIAL
                    /*CAMPO5*/ ||  MI_TIPONIT
                    /*CAMPO6*/ ||  RPAD(MI_NIT, 16, ' ')
                    /*CAMPO7*/ ||  CASE WHEN MI_NIT = ''
                                        THEN  ' '
                                        ELSE CASE WHEN MI_TIPONIT = 'NI'
                                                  THEN '' || PCK_SYSMAN_UTL.FC_DCH(MI_NITF)
                                                  ELSE ' '
                                                  END
                                        END
                    /*CAMPO8*/ ||  UN_TIPOPLANILLA
                    /*CAMPO9*/ ||  PCK_NOMINA_COM5.GL_PLANILLAACORREGIR
                    /*CAMPO11*/ ||  MI_FECHAPAGOCORRECCIONPLANILLA
                    /*CAMPO10*/ ||  MI_FORMAPRESENTACION
                    /*CAMPO11*/ ||  RPAD(NVL(MI_SUCURSALAPORTANTE,' '),10,' ')
                    /*CAMPO12*/ ||  RPAD(NVL(MI_NOMBRESUCURSAL   ,' '),40,' ')
                    /*CAMPO13*/ ||  GL_CODIGOFONDORIESGOS;
    PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION
                    /*CAMPO14*/ ||  UN_ANIO || '-' || PCK_SYSMAN_UTL.FC_STRZERO((UN_MES),2)
                    /*CAMPO15*/ ||  CASE WHEN UN_TIPOPLANILLA = 'K' THEN
                                        UN_ANIO || '-' || PCK_SYSMAN_UTL.FC_STRZERO((UN_MES),2)
                                    ELSE                                   
                    CASE WHEN UN_MES = 12 THEN '' || (UN_ANIO + 1) ELSE '' || UN_ANIO END
                    /*CAMPO16*/ ||    '-' || CASE WHEN UN_MES = 12 THEN  '01' ELSE PCK_SYSMAN_UTL.FC_STRZERO((UN_MES + 1), 2) END
                            END;
    PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION
                    /*CAMPO17*/ ||  RPAD(TO_CHAR(NVL(TO_CHAR(UN_NUMRADICACION),' ')), 10)
                    /*CAMPO18*/ ||  RPAD(' ',10,' ')
                    /*CAMPO19*/ ||  PCK_SYSMAN_UTL.FC_STRZERO(MI_NUMEMPLEADOS, 5)
                    /*CAMPO20*/ ||  PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN MI_TOTALBASEPARAFISCAL = 0
                                          THEN 0
                                          ELSE MI_TOTALBASEPARAFISCAL END, 12)
                    /*CAMPO21*/ ||  PCK_SYSMAN_UTL.FC_STRZERO(GL_TIPOPAGADOR,
                                CASE WHEN UN_TIPOESTRUCTURA = '2388' THEN 2 ELSE 1 END)
                   /*CAMPO22*/ ||   '00';

  GL_DISCOINTEGRADO:= GL_DISCOINTEGRADO || --CHR(13) ||CHR(10) ||
                 PCK_NOMINA_COM5.FC_ENCABEZADOINTEGRADO(UN_RAZONSOCIAL  => MI_RAZONSOCIAL
                                                       ,UN_PERCOTIZA    => MI_PERCOTIZ
                                                       ,UN_INICIAL      =>1);

  MI_NUMEMP := 0;
  MI_SALARIOMINIMO := PCK_PARENTR.PARAMETRO20;
  PCK_NOMINA_COM5.GL_BENEFICIOT := 0;

  <<RECORRIDOPRINCIPAL>>
  FOR I IN PCK_NOMINA_COM5.CSOI.FIRST .. PCK_NOMINA_COM5.CSOI.LAST
  LOOP
    PCK_NOMINA.P:= I;
    PCK_NOMINA_COM5.GL_APVOL := 0;
    MI_INCAPE := 0;
    GL_SUELDO := 0;
    GL_SUELDOP:= 0;
    --FR := IIF(IsNull(PCK_NOMINA_COM5.CSOI(I).FECHA_DE_RETIRO), 0, PCK_NOMINA_COM5.CSOI(I).FECHA_DE_RETIRO - 1);
    --FCS := IIF(IsNull(PCK_NOMINA_COM5.CSOI(I).Fecha_UltSueldo), 0, PCK_NOMINA_COM5.CSOI(I).Fecha_UltSueldo);
    MI_AC:=PCK_NOMINA.FC_ACUM(UN_COMPANIA  => UN_COMPANIA,
                              UN_ANO1      => UN_ANIO,
                              UN_MES1      => UN_MES,
                              UN_PERIODO1  => CASE WHEN UN_RETROACTIVO <> 0 THEN UN_PERIODORETRO ELSE 1 END,
                              UN_ANO2      => UN_ANIO,
                              UN_MES2      => UN_MES,
                              UN_PERIODO2  => CASE WHEN UN_RETROACTIVO <> 0 THEN UN_PERIODORETRO ELSE 99 END,
                              UN_IDEMPLEADO=> PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO);
    MI_AC:=PCK_NOMINA.FC_ACUM1(UN_COMPANIA  => UN_COMPANIA,
                              UN_ANO1      => UN_ANIO,
                              UN_MES1      => UN_MES,
                              UN_PERIODO1  => UN_PERIODO,
                              UN_IDEMPLEADO=> PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO);

    GL_NING := CASE WHEN TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(I).FECHA_DE_INGRESO,'MM')) = UN_MES
                     AND TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(I).FECHA_DE_INGRESO,'YYYY')) = UN_ANIO
              THEN 'X' ELSE ' '
              END;
    GL_NRET := CASE WHEN PCK_NOMINA_COM5.CSOI(I).FECHATERCONTRATO IS NULL
               THEN ' '
               ELSE CASE WHEN TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(I).FECHATERCONTRATO,'MM')) = UN_MES
                          AND TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(I).FECHATERCONTRATO,'YYYY')) = UN_ANIO
                    THEN 'X' ELSE ' ' END
               END;
    PCK_NOMINA_COM5.GL_BENEFICIOT := NVL(PCK_NOMINA.FC_CNA(302), 0);
    GL_NTDA := CASE WHEN PCK_NOMINA.FC_CNA(340) IN(1,4) THEN 'X' ELSE ' ' END;
    GL_NTAA := CASE WHEN PCK_NOMINA.FC_CNA(341) IN(1,4) THEN 'X' ELSE ' ' END;
    IF GL_NTAA = 'X' AND PCK_NOMINA.FC_CNA(341) IN(1,4) THEN
      IF PCK_NOMINA.FC_CNA(340) = 0 THEN
        GL_CODIGOTRASLADOSALUD := PCK_NOMINA_COM6.FC_TRAERDATOSENTIDADESEMPLEADO(
                                                   UN_COMPANIA    => UN_COMPANIA,
                                                   UN_EMPLEADO    => PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO,
                                                   UN_FONDOACTUAL => PCK_NOMINA_COM5.CSOI(I).FONDO_SALUD,
                                                   UN_TIPOFONDO   => 'EPS',
                                                   UN_PAR         => 3);
      END IF;
    ELSE
      GL_CODIGOTRASLADOSALUD := LPAD(' ',6,' ');
    END IF;
    GL_CODIGOTRASLADOSALUD := LPAD(NVL(GL_CODIGOTRASLADOSALUD,' '),6,' ');
    GL_NTDAP := CASE WHEN PCK_NOMINA.FC_CNA(340) IN(2,4) THEN 'X' ELSE ' ' END;
    GL_NTAAP := CASE WHEN PCK_NOMINA.FC_CNA(341) IN(2,4) THEN 'X' ELSE ' ' END;
    --TRASLADO DE FONDO PENSION
    IF GL_NTAAP = 'X' AND PCK_NOMINA.FC_CNA(341) IN(2,4) THEN
      GL_CODIGOTRASLADOPENSION := PCK_NOMINA_COM6.FC_TRAERDATOSENTIDADESEMPLEADO(
                                                   UN_COMPANIA    => UN_COMPANIA,
                                                   UN_EMPLEADO    => PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO,
                                                   UN_FONDOACTUAL => PCK_NOMINA_COM5.CSOI(I).ID_DEL_FONDO,
                                                   UN_TIPOFONDO   => 'AFP',
                                                   UN_PAR         => 3);
    ELSE
      GL_CODIGOTRASLADOPENSION := LPAD(' ',6,' ');
    END IF;
    GL_CODIGOTRASLADOPENSION := LPAD(NVL(GL_CODIGOTRASLADOPENSION,' '),6, ' ');
    GL_NVSP := CASE WHEN PCK_NOMINA_COM5.CSOI(I).FECHA_ULTSUELDO IS NULL
               THEN ' '
               ELSE CASE WHEN (TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(I).FECHA_ULTSUELDO,'MM')) = UN_MES
                           AND TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(I).FECHA_ULTSUELDO,'YYYY')) = UN_ANIO)
                    THEN 'X' ELSE ' ' END
               END;
    GL_VTE := ' ';
    PR_CALCULAR_NVST(UN_COMPANIA    => UN_COMPANIA
                    ,UN_NITCOMPANIA => MI_PARA);
    GL_NSLN := CASE WHEN PCK_NOMINA.FC_CNA(356) <> 0 OR PCK_NOMINA.FC_CNA(357) <> 0 OR PCK_NOMINA.FC_CNA(359) <> 0 THEN 'X' ELSE ' ' END;
    GL_NIGE := CASE WHEN PCK_NOMINA.FC_CNA(350) <> 0 OR PCK_NOMINA.FC_CNA(351) <> 0 OR PCK_NOMINA.FC_CNA(358) <> 0 OR PCK_NOMINA.FC_CNA(352) <> 0 OR PCK_NOMINA.FC_CNA(354) <> 0 OR PCK_NOMINA.FC_CNA(355) <> 0 OR PCK_NOMINA.FC_CNA(360) <> 0 OR PCK_NOMINA.FC_CNA(336) <> 0 THEN 'X' ELSE ' ' END ;
    GL_NLMA := CASE WHEN PCK_NOMINA.FC_CNA(353) <> 0 THEN 'X' ELSE ' ' END;
    IF UN_TIPOESTRUCTURA = 2388 AND PCK_NOMINA.FC_CNA(35) <> 0 THEN
      GL_NVAC := CASE WHEN PCK_NOMINA.FC_CNA(35) <> 0 THEN 'X' ELSE ' ' END;
    ELSIF UN_TIPOESTRUCTURA = 2388 AND (PCK_NOMINA.FC_CNA(363) + PCK_NOMINA.FC_CNA(364) + PCK_NOMINA.FC_CNA(365) + PCK_NOMINA.FC_CNA(390)) <> 0 AND GL_NVAC = ' ' THEN
      GL_NVAC := CASE WHEN (PCK_NOMINA.FC_CNA(363) + PCK_NOMINA.FC_CNA(364) + PCK_NOMINA.FC_CNA(365) + PCK_NOMINA.FC_CNA(390)) <> 0 THEN 'L' ELSE ' ' END;
    ELSE
      GL_NVAC := CASE WHEN PCK_NOMINA.FC_CNA(35) <> 0 OR (PCK_NOMINA.FC_CNA(363) + PCK_NOMINA.FC_CNA(364) + PCK_NOMINA.FC_CNA(365) + PCK_NOMINA.FC_CNA(390)) <> 0 THEN 'X' ELSE ' ' END;
    END IF;
    IF GL_PAGARAPORTESVOLUNPILA = 'SI' AND ((PCK_NOMINA.FC_CNA(124) + PCK_NOMINA.FC_CNA(127)) <> 0) AND PCK_NOMINA_COM5.CSOI(I).NITFONDOPENSION = PCK_NOMINA_COM5.CSOI(I).NITFONDOPENSION_VOL THEN  --(MZANGUNA:22/03/2019)-Se agrega condiciÃ‚Â¿ara que solo los que tengan fondos iguales pasen al campo
      GL_NAVP  :=  CASE WHEN PCK_NOMINA_COM5.CSOI(I).APVOL <> 0 OR PCK_NOMINA_COM5.CSOI(I).APAFC <> 0 THEN 'X' ELSE ' ' END;
      GL_APVOL := PCK_NOMINA_COM5.CSOI(I).APVOL;
      GL_NAVP  :=  CASE WHEN PCK_NOMINA.FC_CNA(124) <> 0 OR PCK_NOMINA.FC_CNA(127) <> 0THEN 'X' ELSE ' ' END;
    ELSE
      GL_NAVP := ' ';
      GL_APVOL:= 0;
      GL_NAVP := ' ' ;
      PCK_NOMINA_COM5.GL_BENEFICIOT := 0;
    END IF;
    GL_NVCT :=  CASE WHEN PCK_NOMINA.FC_CNA(451) <> 0 THEN 'X' ELSE ' ' END ;
    MI_FECHACAMBIORIEGO:= PCK_NOMINA_COM6.FC_FECHACAMBIORIESGO(UN_COMPANIA => UN_COMPANIA,
                                                               UN_EMPLEADO => PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO);

    IF MI_FECHACAMBIORIEGO >= TO_DATE('01/' || TO_CHAR(UN_FECHALIQUIDACION, 'MM') || '/' || TO_CHAR(UN_FECHALIQUIDACION,'YYYY'),'DD/MM/YYYY')
      AND MI_FECHACAMBIORIEGO <= UN_FECHALIQUIDACION AND GL_NVCT = ' ' THEN
      IF MI_FECHACAMBIORIEGO = PCK_NOMINA_COM5.CSOI(I).FECHA_DE_INGRESO THEN
        GL_NVCT := ' ';
        GL_NVSP := ' ';
      ELSE
        GL_NVCT := 'X';
      END IF;
    ELSE
      GL_NVCT := ' ';
    END IF;
    GL_NIRP :='00';
    IF PCK_NOMINA_COM5.CSOI(I).DIASATEP <> 0  THEN
      IF (PCK_NOMINA_COM5.CSOI(I).DIASATEP
                       + CASE WHEN PCK_NOMINA.FC_CNA(358) > 0
                              THEN 1
                              ELSE 0 END
                              ) >30 THEN
        GL_NIRP := '30';
      ELSE
        GL_NIRP :=  PCK_SYSMAN_UTL.FC_STRZERO(NVL(PCK_NOMINA_COM5.CSOI(I).DIASATEP
                    + CASE WHEN PCK_NOMINA.FC_CNA(358) > 0
                              THEN 1
                              ELSE 0 END,0)
                      , 2) ;
      END IF;
    END IF;

    GL_NIRP :=  PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN GL_NIRP='  ' OR GL_NIRP IS NULL THEN '00' ELSE GL_NIRP END, 2);

    PCK_NOMINA_COM5.PR_REVISARSUELDO (UN_COMPANIA   => UN_COMPANIA
                    ,UN_I          => I
                    ,UN_ANIO       => UN_ANIO
                    ,UN_MES        => UN_MES
                    ,UN_RETROACTIVO=> UN_RETROACTIVO);
    MI_INCAPE := PCK_NOMINA.FC_SUMACONA(370, 372) + PCK_NOMINA.FC_SUMACONA(374, 377);
    MI_ADS   := PCK_NOMINA.FC_CNA(113) + PCK_NOMINA.FC_CNA(116) + PCK_NOMINA.FC_CNA(114) + PCK_NOMINA.FC_CNA(119);

    IF(MI_INCAPE > 0) THEN
      BEGIN
        SELECT SUBSTR(MIN(INCAPACIDADES.AUTO) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM),1,9)
        INTO MI_NUMINCAP
        FROM INCAPACIDADES
        WHERE COMPANIA      = UN_COMPANIA
          AND ID_DE_EMPLEADO= PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO
          AND ANO           = UN_ANIO
          AND MES           = UN_MES;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_NUMINCAP:=' ';
      END;
    ELSE
      MI_NUMINCAP:=' ';
    END IF;
    MI_NUMINCAP := RPAD(MI_NUMINCAP,9,' ');
    IF(PCK_NOMINA.FC_CNA(UN_CONCEPTO => 373) > 0) THEN
      BEGIN
        SELECT MIN(INCAPACIDADES.AUTO) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM)
        INTO MI_NUMLICMATER
        FROM INCAPACIDADES
        WHERE COMPANIA       = UN_COMPANIA
          AND ID_DE_EMPLEADO = PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO
          AND ANO            = UN_ANIO
          AND MES            = UN_MES
          AND INCAPACIDAD    ='04';
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_NUMLICMATER:=' ';
      END;
    ELSE
      MI_NUMLICMATER:=' ';
    END IF;
    MI_NUMLICMATER:=RPAD(MI_NUMLICMATER,9,' ');

    IF PCK_NOMINA_COM5.CSOI(I).PAGOS > 0
        OR PCK_NOMINA_COM5.CSOI(I).DIASINCAPCIDADES <> 0
        OR PCK_NOMINA_COM5.CSOI(I).DIASRIESGOS <> 0
        OR PCK_NOMINA_COM5.CSOI(I).ESTADO_ACTUAL = 6 THEN
      IF MI_LINN >= 45 THEN
        GL_DISCOINTEGRADO:= GL_DISCOINTEGRADO || --CHR(13) ||CHR(10) ||
                       PCK_NOMINA_COM5.FC_ENCABEZADOINTEGRADO(UN_RAZONSOCIAL  => MI_RAZONSOCIAL
                                                             ,UN_PERCOTIZA    => MI_PERCOTIZ
                                                             ,UN_INICIAL      =>0);
        MI_LINN := 0;
      END IF;
      MI_LINN := MI_LINN + 1;
      MI_NUMEMP := MI_NUMEMP + 1;

      IF (TO_NUMBER(GL_NUMDIAS) < 30)
         AND GL_NING = ' ' AND GL_NRET = ' ' AND GL_NTDA = ' '
         AND GL_NTAA = ' ' AND GL_NTDAP = ' '
         AND GL_NTAAP = ' ' AND GL_NSLN = ' '
         AND PCK_NOMINA.FC_CNA(336) = 0 THEN
        MI_MSGERROR(1).CLAVE := 'EMPLEADO';
        MI_MSGERROR(1).VALOR := PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO || ' ' || PCK_NOMINA_COM5.CSOI(I).APELLIDO1 || ' ' || PCK_NOMINA_COM5.CSOI(I).APELLIDO2 || ' ' || PCK_NOMINA_COM5.CSOI(I).NOMBRES;
        PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => UN_COMPANIA,
                                   UN_MENSAJE_COD => PCK_ERRORES.ALER_MENOR30_SIN_NOVEDAD,
                                   UN_REEMPLAZOS  => MI_MSGERROR);
      END IF;
      IF (TO_NUMBER(GL_NUMDIAS) > '30') THEN
        GL_NUMDIAS := '30';
      END IF;
      /*GL_CTAR := PCK_NOMINA_SEGSOCI.FC_PORCENRIESGO(UN_COMPANIA => UN_COMPANIA
                                                   ,UN_IDEMPLEADO => PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO) / 100;*/
      --(APINEDA:17/07/2019)-Se utiliza campo FACTOR_RIESGO de la vista V_SOI en vez de la funciÃƒÂ³n FC_PORCENRIESGO, porque para el cÃƒÂ¡lculo de Retroactivo el dato debe proceder de PERSONAL_HISTORICO y no de PERSONAL ya que pudo haber cambiado entre un mes y otro.                                                   
      GL_CTAR := PCK_NOMINA_COM5.CSOI(I).FACTOR_RIESGO / 100;

      IF TO_NUMBER(GL_NUMDIAS) < 30
        AND GL_NING = ' ' AND GL_NRET = ' '
        AND GL_NTDA = ' ' AND GL_NTAA = ' ' AND GL_NSLN = ' '
        AND PCK_NOMINA.FC_CNA(336) = 0  THEN
        MI_MSGERROR(1).CLAVE := 'EMPLEADO';
        MI_MSGERROR(1).VALOR := PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO || ' ' || PCK_NOMINA_COM5.CSOI(I).APELLIDO1 || ' ' || PCK_NOMINA_COM5.CSOI(I).APELLIDO2 || ' ' || PCK_NOMINA_COM5.CSOI(I).NOMBRES;
        PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => UN_COMPANIA,
                                   UN_MENSAJE_COD => PCK_ERRORES.ALER_MENOR30_SIN_NOVEDAD_ING,
                                   UN_REEMPLAZOS  => MI_MSGERROR);
      END IF;
      PCK_NOMINA_COM5.GL_CODAIFP := RPAD(NVL(PCK_NOMINA_COM5.CSOI(I).ID_DEL_FONDO_SOI,' '), 6, ' ');
      IF PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION IN('17','1')
        AND PCK_NOMINA_COM5.CSOI(I).SUBTIPOCOTIZANTE = '4'
        OR (PCK_NOMINA_COM5.CSOI(I).ID_DEL_FONDO = 'AFP99'
        AND PCK_NOMINA.GL_OPERADOR <> 'ASOCAJAS') THEN
        PCK_NOMINA_COM5.GL_CODAIFP := RPAD(' ',6,' ');
      END IF;
      PCK_NOMINA_COM5.GL_CODAIFS := RPAD(NVL(PCK_NOMINA_COM5.CSOI(I).FONDO_SALUD_SOI,' '), 6, ' ');
      PCK_NOMINA_COM5.GL_CODAICC := RPAD(' ', 6, ' ');
      IF NOT PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION IN('12', '19') THEN
        PCK_NOMINA_COM5.GL_CODAICC := RPAD(PCK_NOMINA_COM5.CSOI(I).CAJA_COMPENSACION_SOI, 6,' ');
      END IF;
      GL_PR := PCK_PARENTR.PARAMETRO39 - TRUNC(PCK_PARENTR.PARAMETRO39);
      IF GL_PR >0 AND GL_PR < 1 THEN
        GL_PR := 10;
      ELSE
        GL_PR :=1;
      END IF;
      MI_TIPODCTO   := PCK_NOMINA_COM5.FC_DCTIDENTIDAD(UN_TIPO => NVL(PCK_NOMINA_COM5.CSOI(I).DCTO_IDENTIDAD, ''));
      GL_SALARIOINTEGRAL := ' ';
      IF PCK_NOMINA_COM5.CSOI(I).ID_DE_TIPO = '02' THEN
        GL_SALARIOINTEGRAL := 'X';
      END IF;
      GL_FSPPLANILLA := 0;
      GL_FSPADICPLANILLA := 0;
      PR_REVISARFSPYADICONAL(UN_COMPANIA => UN_COMPANIA
                            ,UN_I        => I
                            ,UN_ANIO     => UN_ANIO
                            ,UN_MES      => UN_MES);
      IF PCK_NOMINA_COM5.CSOI(I).ID_DE_TIPO = '95' THEN
        GL_PESPECIAL_PENSION := GL_APORTEPENSIONBOMBERO;
      END IF;
      --VP := IIF(PCK_NOMINA_COM5.CSOI(I).PENSION = 0, True, False);
      GL_BASESALUD := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(I).BASESALUD, 9);
      IF PCK_NOMINA_COM5.CSOI(I).DIASSALUD <> 0 THEN
        GL_NUMDIASS := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(I).DIASSALUD, 2);
      END IF;
      IF GL_NUMDIASS > '30' THEN
        GL_NUMDIASS := '30';
      END IF;
      IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359)) > 0 THEN
        IF PCK_NOMINA_COM5.CSOI(I).DIASSALUD >= 30
          AND TO_NUMBER(GL_BASESALUD) < PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_PARENTR.PARAMETRO20
                                                               ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990) THEN
          GL_BASESALUD := PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_PARENTR.PARAMETRO20
                                                                           ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990), 9);
        END IF;
      END IF;
      IF GL_GENERANOVIGECONDIASARL = 'NO' AND TO_NUMBER(GL_NIRP) > 1 THEN
        GL_NIGE := ' ';
      END IF;
      GL_EXONERADO := NVL(PCK_PARENTR.PARAMETRO70, 'N') = 'S'
                  AND PCK_NOMINA_COM5.CSOI(I).ICBF = 0
                  AND PCK_NOMINA_COM5.CSOI(I).SENA = 0;
      GL_OBLIGAR := FALSE;
      IF CASE WHEN GL_CTAR = '0' OR PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION IN('12','19')
                                 OR PCK_NOMINA_COM5.CSOI(I).RIESGOS = 0
         THEN 0
         ELSE GL_CTAR
         END = 0
         AND PCK_NOMINA.GL_OBLIGAPORARP = 'SI' THEN
        GL_OBLIGAR := TRUE;
      END IF;
      IF PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION IN('12','19') THEN
        GL_NVST := ' ';
      END IF;
      IF PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION = '51' THEN
        GL_NVSP := ' ';
        GL_NVST := ' ';
        GL_NLMA := ' ';
        GL_NIGE := ' ';
      END IF;
      IF (PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION IN('12','19')) THEN
        GL_NVST := ' ';
        --GL_INVST := ' ';
      END IF;
      --GL_TOTDIASNOV := 0;
      --TOTBASENOV = 0;
      MI_GENERARNOVEDADESPILA2388 := 0;
      IF UN_TIPOESTRUCTURA = 2388 THEN
        --(MZANGUNA:09/11/2018)-Se implementa funciÃ‚Â¿C_NOVEDADESPILA2388_TABLASEGS.
        --(MZANGUNA:24/11/2018)-Se agrega el periodo.
        --(APINEDA:27/06/2019-Se agrega a la consulta sobre Bases novedades y a FC_NOVEDADESPILA2388_TABLASEGS validaciÃ‚Â¿ara tener en cuenta periodo de nÃ‚Â¿a retroactivo)
        --(APINEDA:09/10/2019)-Se elimina dependencia de periodo 5 cuando se va a generar plano para el retroactivo.
        BEGIN

      IF UN_PERIODO = 2  OR  UN_PERIODO = 1 THEN--(CFBARRERA_INI_3708-HU3)
            UN_PERIODO_AUX := 2;
        ELSIF PCK_NOMINA_COM5.CSOI(I).PERIODO = 52 AND UN_PERIODO = 33 AND UN_PERIODORETRO = 2
              AND NVL(
                    PCK_SYSMAN_UTL.FC_PAR(
                        UN_COMPANIA    => UN_COMPANIA,
                        UN_NOMBRE      => 'NOMINA MENSUAL',
                        UN_MODULO      => PCK_DATOS.FC_MODULONOMINA,
                        UN_FECHA_PAR   => SYSDATE,
                        UN_IND_MAYUS   => -1
                    ), 'SI'
                  ) = 'NO' THEN
              MI_PERIODO_RETRO := 53;
         ELSE
            UN_PERIODO_AUX:= UN_PERIODO;--(CFBARRERA_FIN_3708-HU3)
        END IF; --JM 7736030  se ajusta para que en el caso de ser quincenal tome el periodo 3 que esta en basenovedades (Bucaramanga) y otras entidades que manejen nomina quincenal 


            SELECT COUNT(0)
            INTO   MI_CUENTA
            FROM   BASESNOVEDADES
            WHERE  COMPANIA       = UN_COMPANIA
              AND  ID_DE_EMPLEADO = PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO
              AND  DIAS NOT IN (0)
              AND  ANO = UN_ANIO
              AND  MES = UN_MES
              AND PERIODO = CASE--(CFBARRERA_INI_3708-HU3)
                WHEN UN_RETROACTIVO <> 0 AND MI_PERIODO_RETRO <> 0
                     THEN MI_PERIODO_RETRO        
                WHEN UN_RETROACTIVO <> 0
                     THEN PCK_NOMINA_COM5.CSOI(I).PERIODO  
                ELSE UN_PERIODO_AUX               
              END;--(CFBARRERA_FIN_3708-HU3)
              
              --AND  TIPONOVEDAD NOT IN (4);  --(MZANGUNA:23/04/2019)-Se quita condiciÃ‚Â¿ara enviar todo por el nuevo modelo.
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CUENTA := 0;
        END;
        IF MI_CUENTA > 0 THEN
            --(APINEDA:09/10/2019)-Se elimina dependencia de periodo 5 cuando se va a generar plano para el retroactivo.
            MI_GENERARNOVEDADESPILA2388 := PCK_NOMINA_COM5.FC_NOVEDADESPILA2388_TABLASEGS(UN_COMPANIA    => UN_COMPANIA
                                     ,UN_I           => I
                                     ,UN_PROCESO     => UN_PROCESO
                                     ,UN_ANIO        => UN_ANIO
                                     ,UN_MES         => UN_MES
                                     ,UN_PERIODO     => CASE WHEN UN_RETROACTIVO <> 0 THEN (PCK_NOMINA_COM5.CSOI(I).PERIODO - 50) ELSE UN_PERIODO END
                                     ,UN_TIPOESTRUCTURA => UN_TIPOESTRUCTURA
                                     ,UN_TIPOPLANILLA   => UN_TIPOPLANILLA
                                     ,UN_RETROACTIVO    => UN_RETROACTIVO
                                     ,UN_USUARIO        => UN_USUARIO);
        ELSE
            MI_GENERARNOVEDADESPILA2388 := PCK_NOMINA_COM5.FC_GENERAR_NOVEDADES_PILA_2388(UN_COMPANIA    => UN_COMPANIA
                                     ,UN_I           => I
                                     ,UN_PROCESO     => UN_PROCESO
                                     ,UN_ANIO        => UN_ANIO
                                     ,UN_MES         => UN_MES
                                     ,UN_PERIODO     => UN_PERIODO
                                     ,UN_TIPOESTRUCTURA => UN_TIPOESTRUCTURA
                                     ,UN_TIPOPLANILLA   => UN_TIPOPLANILLA
                                     ,UN_RETROACTIVO    => UN_RETROACTIVO
                                     ,UN_USUARIO        => UN_USUARIO);
        END IF;
        IF MI_GENERARNOVEDADESPILA2388 = 0 THEN
          MI_CAMPOSNUEVOS2388N := PCK_NOMINA_COM5.FC_CAMPOSNUEVOS2388INGRET(UN_ANNO     => UN_ANIO
                                               ,UN_MMES     => UN_MES
                                               ,UN_PNING    => GL_NING
                                               ,UN_PNRET    => GL_NRET
                                               ,UN_PNVST    => GL_NVST
                                               ,UN_DIASTRABAJADOS => PCK_NOMINA.FC_CNA(9) +PCK_NOMINA.FC_CNA(11)
                                               ,UN_I              => I
                                               ,UN_RBASE1990      => PCK_NOMINA.GL_RBASE1990
                                               ,UN_HORASMENSUALES => PCK_NOMINA.GL_HORASMENSUALES
                                               ,UN_DIASSEMANALES  => PCK_NOMINA.GL_DIASSEMANALES
                                               );
        ELSE
          MI_CAMPOSNUEVOS2388N := '';
        END IF;
      END IF;
      IF ( PCK_NOMINA.GL_PAGOUPC = 'SI' OR PCK_NOMINA_COM5.GL_UPCADICIONALPILA = 'SI')
       AND CASE WHEN  PCK_NOMINA_COM5.GL_UPCPATRONO ='SI' THEN PCK_NOMINA_COM5.CSOI(I).UPCPATRONO ELSE PCK_NOMINA_COM5.CSOI(I).UPC END > 0 THEN
                       GL_CAMPO80:=  RPAD(' ',10,' ');
                          GL_CAMPO81:=  RPAD(' ',10,' ');
                          GL_CAMPO82:=  RPAD(' ',10,' ');
                          GL_CAMPO83:=  RPAD(' ',10,' ');
                          GL_CAMPO84:=  RPAD(' ',10,' ');
                          GL_CAMPO85:=  RPAD(' ',10,' ');
                          GL_CAMPO86:=  RPAD(' ',10,' ');
                          GL_CAMPO87:=  RPAD(' ',10,' ');
                          GL_CAMPO88:=  RPAD(' ',10,' ');
                          GL_CAMPO89:=  RPAD(' ',10,' ');
                          GL_CAMPO90:=  RPAD(' ',10,' ');
                          GL_CAMPO91:=  RPAD(' ',10,' ');
                          GL_CAMPO92:=  RPAD(' ',10,' ');
                          GL_CAMPO93:=  RPAD(' ',10,' ');
                          GL_CAMPO94:=  RPAD(' ',10,' ');
                          GL_CAMPO95:=  GL_BASESALUD;
                          GL_CAMPO96:=  240;
                          GL_CAMPO97:=  RPAD(' ',10,' ');
                          MI_CAMPOSNUEVOS2388N := FC_CONCATENARCAMPSO80A97;
        PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                               FC_REGISTROPLANO (UN_COMPANIA         => UN_COMPANIA
                                                                ,UN_I                => I
                                                                ,UN_TIPOESTRUCTURA   => UN_TIPOESTRUCTURA
                                                                ,UN_CLASEREGISTRO    => 'UPC'
                                                                ,UN_TIPOPLANILLA     => UN_TIPOPLANILLA
                                                                ,UN_CAMPOSNUEVOS2388 => MI_CAMPOSNUEVOS2388N);
        IF PCK_NOMINA.GL_MANSGP AND UN_TIPOPLANILLA = 'T' THEN
          PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                                 FC_REGISTROPLANO (UN_COMPANIA         => UN_COMPANIA
                                                                  ,UN_I                => I
                                                                  ,UN_TIPOESTRUCTURA   => UN_TIPOESTRUCTURA
                                                                  ,UN_CLASEREGISTRO    => 'SGP'
                                                                  ,UN_TIPOPLANILLA     => UN_TIPOPLANILLA
                                                                  ,UN_CAMPOSNUEVOS2388 => MI_CAMPOSNUEVOS2388N);
        END IF;
        /*
        IF GENERARNOVEDADESPILA2388 = 0 THEN
          Print #1, PART1 & PART2
          INCLUIR_NOVEDADES_PILAPARTES UN_COMPANIA, GetProceso(), UN_ANIOCORRECCION, UN_MESCORRECCION, "03", PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO, Val(ContadOR), PART1 & PART2
        END IF;
        */
      ELSE
        GL_DIASPA := PCK_SYSMAN_UTL.FC_STRZERO(
                    CASE WHEN PCK_NOMINA_COM5.CSOI(I).BASEPARAFISCAL <> 0
                    THEN CASE WHEN PCK_NOMINA.GL_OPERADOR IN('NUEVOSOI','SOI')
                         THEN PCK_NOMINA_COM5.CSOI(I).DIASRIESGOS
                         ELSE PCK_NOMINA_COM5.GL_NUMDIASP END
                    ELSE '00'
                    END
                    , 2);
        IF GL_SUMARDIASVACAAPARAFISCALES = 'SI' THEN
          IF PCK_NOMINA.FC_CNA(35) <> 0 AND TO_NUMBER(GL_DIASPA) < 30 THEN
            GL_DIASPA := PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN PCK_NOMINA_COM5.CSOI(I).BASEPARAFISCAL <> 0
                                                THEN CASE WHEN PCK_NOMINA.GL_OPERADOR = 'NUEVOSOI'
                                                     THEN PCK_NOMINA_COM5.CSOI(I).DIASRIESGOS + PCK_NOMINA.FC_CNA(35)
                                                     ELSE PCK_NOMINA_COM5.GL_NUMDIASP
                                                     END
                                                ELSE '00'
                                                END
                                                ,2);
          END IF;
        END IF;
        --(MZANGUNA:14/11/2018)--Con la nueva tabla no debe entrar por este lado, dado que las todas las licencias estÃ‚Â¿incluidas.
        IF PCK_NOMINA.FC_CNA(356) <> 0 OR PCK_NOMINA.FC_CNA(357) <> 0 OR PCK_NOMINA.FC_CNA(359) <> 0 THEN
            IF MI_CUENTA = 0 THEN   --(MZANGUNA:14/03/2019)-Ajuste para no entrar en licencias cuando pasa por la tabla BASESNOVEDADES
                PCK_NOMINA_COM5.PR_REGISTROS_LNR(UN_COMPANIA       => UN_COMPANIA
                                       ,UN_I              => I
                                       ,UN_ANIO           => UN_ANIO
                                       ,UN_MES            => UN_MES
                                       ,UN_PNSLN          => GL_NSLN
                                       ,UN_TIPOESTRUCTURA => UN_TIPOESTRUCTURA
                                       ,UN_TIPOPLANILLA  => UN_TIPOPLANILLA);
            END IF;
        ELSE
          IF PCK_NOMINA_COM5.CSOI(I).ESTADO_ACTUAL = 6 AND MI_CUENTA = 0 THEN --(MZANGUNA:14/03/2019)-Se agrega condicion, dado que con la nueva tabla no debe entrar por este lado.
            IF PCK_NOMINA_COM5.CSOI(I).PAGOS > 0 THEN
              IF TO_NUMBER(PCK_NOMINA_COM5.GL_NUMDIAS) >= 30 AND PCK_NOMINA.FC_CNA(339) > 0 THEN
                PCK_NOMINA_COM5.GL_NUMDIAS := PCK_NOMINA_COM5.GL_NUMDIAS - PCK_NOMINA.FC_CNA(339);
              END IF;
              PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                                     FC_REGISTROPLANO (UN_COMPANIA         => UN_COMPANIA
                                                                      ,UN_I                => I
                                                                      ,UN_TIPOESTRUCTURA   => UN_TIPOESTRUCTURA
                                                                      ,UN_CLASEREGISTRO    => 'EST'
                                                                      ,UN_TIPOPLANILLA     => UN_TIPOPLANILLA
                                                                      ,UN_CAMPOSNUEVOS2388 => MI_CAMPOSNUEVOS2388N);
              --INCLUIR_NOVEDADES_PILAPARTES UN_COMPANIA, GetProceso(), UN_ANIOCORRECCION, UN_MESCORRECCION, "03", PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO, Val(ContadOR), PART1 & PART2
            END IF;
            PCK_NOMINA_COM5.PR_COMISION_SOI(UN_COMPANIA       => UN_COMPANIA
                                           ,UN_I              => I
                                           ,UN_ANIO           => UN_ANIO
                                           ,UN_MES            => UN_MES
                                           ,UN_PNSLN          => GL_NSLN
                                           ,UN_TIPOESTRUCTURA => UN_TIPOESTRUCTURA
                                           ,UN_TIPOPLANILLA  => UN_TIPOPLANILLA);
          ELSE
            IF PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION IN('12','19')
             AND PCK_NOMINA_COM5.CSOI(I).RIESGOS = 0
             AND PCK_NOMINA.GL_OBLIGAPORARP = 'NO' THEN
              GL_OBLIGAR := FALSE;
              GL_CTAR    := 0;
            END IF;
            IF PCK_NOMINA.GL_MANSGP AND UN_TIPOPLANILLA = 'T' THEN
              PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                                     FC_REGISTROPLANO (UN_COMPANIA         => UN_COMPANIA
                                                                      ,UN_I                => I
                                                                      ,UN_TIPOESTRUCTURA   => UN_TIPOESTRUCTURA
                                                                      ,UN_CLASEREGISTRO    => 'SGP'
                                                                      ,UN_TIPOPLANILLA     => UN_TIPOPLANILLA
                                                                      ,UN_CAMPOSNUEVOS2388 => MI_CAMPOSNUEVOS2388N);
            END IF;
            IF MI_GENERARNOVEDADESPILA2388 = 0 THEN
              PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                                     FC_REGISTROPLANO (UN_COMPANIA         => UN_COMPANIA
                                                                      ,UN_I                => I
                                                                      ,UN_TIPOESTRUCTURA   => UN_TIPOESTRUCTURA
                                                                      ,UN_CLASEREGISTRO    => 'PRI'
                                                                      ,UN_TIPOPLANILLA     => UN_TIPOPLANILLA
                                                                      ,UN_CAMPOSNUEVOS2388 => MI_CAMPOSNUEVOS2388N);
            END IF;
            /*
            IF GENERARNOVEDADESPILA2388 = 0 THEN
              Print #1, PART1 & PART2
              INCLUIR_NOVEDADES_PILAPARTES UN_COMPANIA, GetProceso(), UN_ANIOCORRECCION, UN_MESCORRECCION, "03", PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO, Val(ContadOR), PART1 & PART2
            END IF;
            */
          END IF;
        END IF;
      END IF;

       GL_DISCOINTEGRADO := GL_DISCOINTEGRADO || CHR(13) || CHR(10)
                                      || PCK_SYSMAN_UTL.FC_STRZERO(GL_CONTADOR, 5)
                                      || 'CC'
                                      || LPAD(PCK_NOMINA_COM5.CSOI(I).NUMERO_DCTO, 16,' ') || ' '
                                      || RPAD(UPPER(PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(PCK_NOMINA_COM5.CSOI(I).APELLIDO1, ''))) || ' '
                                      || UPPER(PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(PCK_NOMINA_COM5.CSOI(I).APELLIDO2, ''))) || ' '
                                      || UPPER(PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(PCK_NOMINA_COM5.CSOI(I).NOMBRES, ''))),32,' ')
                                      || '|' || GL_NING
                                      || '|' || GL_NRET
                                      || '|' || GL_NTDA
                                      || '|' || GL_NTAA
                                      || '|' || GL_NTDAP
                                      || '|' || GL_NTAAP
                                      || '|' || GL_NVSP
                                      || '|' || GL_VTE
                                      || '|' || GL_NVST
                                      || '|' || GL_NSLN
                                      || '|' || GL_NIGE
                                      || '|' || GL_NLMA
                                      || '|' || GL_NVAC
                                      || '|' || GL_NAVP
                                      || '|' || GL_NVCT
                                      || '|' || GL_NIRP
                                      || '|' || PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.GL_NUMDIAS,2)
                                      || '|' || PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.GL_NUMDIAS,2)
                                      || '|' || PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(I).DIASRIESGOS,2)
                                      || '|' || LPAD(GL_SUELDO,11, ' ')
                                      || '|' || LPAD(CASE WHEN PCK_NOMINA_COM5.CSOI(I).PENSION + PCK_NOMINA_COM5.CSOI(I).SALUD > 0 THEN  PCK_NOMINA_COM5.CSOI(I).BASE ELSE 0 END, 11, ' ')
                                      || '|' || LPAD(CASE WHEN PCK_NOMINA_COM5.CSOI(I).RIESGOS = 0 THEN 0 ELSE PCK_NOMINA_COM5.CSOI(I).BASE END, 11, ' ')
                                      || '|' || LPAD(PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(I).BASEPARAFISCAL
                                                                            ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990)
                                                , 11, ' ')
                                      || '|' || LPAD(PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(I).SALARIO_BASE_IBC
                                                                            ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990)
                                                , 11, ' ')
                                      ;

      --Print #2,&  Padl(FORmat(SUELDO, "###,###"), 11, ' ') & Padl(FORmat(IIF((PCK_NOMINA_COM5.CSOI(I).PENSION + PCK_NOMINA_COM5.CSOI(I).Salud) > 0, PCK_NOMINA_COM5.CSOI(I).BASE, 0), "###,###"), 11, ' ')
      --          & Padl(FORmat(IIF(PCK_NOMINA_COM5.CSOI(I).Riesgos = 0, 0, PCK_NOMINA_COM5.CSOI(I).BASERIESGOS), "###,###"), 11, ' ')
      --          & Padl(FORmat(Round(PCK_NOMINA_COM5.CSOI(I).BASEPARAFISCAL, Val(PCK_NOMINA.GL_RBASE1990)), "###,###"), 11, ' ')
      --          & Padl(FORmat(Round(PCK_NOMINA_COM5.CSOI(I).SALARIO_BASE_IBC, Val(PCK_NOMINA.GL_RBASE1990)), "###,###"), 11, ' ')
      --ACTUALIZARNOVEDADES_HFA
      IF CASE WHEN  PCK_NOMINA_COM5.GL_UPCPATRONO ='SI' THEN PCK_NOMINA_COM5.CSOI(I).UPCPATRONO ELSE PCK_NOMINA_COM5.CSOI(I).UPC END > 0 THEN
        PR_UPCADICIONAL(UN_COMPANIA  => UN_COMPANIA
                       ,UN_EMPLEADO  => PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO
                       ,UN_VALORUPC  => CASE WHEN  PCK_NOMINA_COM5.GL_UPCPATRONO ='SI' THEN PCK_NOMINA_COM5.CSOI(I).UPCPATRONO ELSE PCK_NOMINA_COM5.CSOI(I).UPC END
                       ,UN_CODAIFS   => PCK_NOMINA_COM5.GL_CODAIFS
                       ,UN_I         => I);
      END IF;
      MI_TOTDIAS   := MI_TOTDIAS   + GL_NUMDIAS;
      MI_TOTSUELDO := MI_TOTSUELDO + GL_SUELDO;
      MI_TOTIBC    := MI_TOTIBC    + PCK_NOMINA.FC_CNA(112);
      MI_TOTNETO   := 0 ;
      MI_TOTSALUD  := MI_TOTSALUD + MI_ADS;
      --TOTUPC := TOTUPC + PCK_NOMINA.FC_CNA(123);
    END IF;
  END LOOP;
  IF NOT (PCK_NOMINA.GL_MANSGP AND UN_TIPOPLANILLA = 'T') THEN
    PCK_NOMINA.GL_STRETAPAGLOBAL := 'Archivos Finales 4 al 11';

    PCK_NOMINA_COM5.PR_REGISTROS_FINALES(UN_COMPANIA       => UN_COMPANIA
                                        ,UN_ANIO           => UN_ANIO
                                        ,UN_MES            => UN_MES
                                        ,UN_PERIODO        => UN_PERIODO
                                        ,UN_PERIODO_RETRO  => UN_PERIODORETRO
                                        ,UN_TIPOESTRUCTURA => UN_TIPOESTRUCTURA);

    MI_GRANTOTAL := GL_TOTALPENSIONES11 + GL_TOTALSALUD11 + GL_TOTALRIESGOS11 + GL_TOTALCAJAS11 + GL_TOTALSENA11 + GL_TOTALICBF11 + GL_TOTALESAP11 + GL_TOTALINSTITUTOS11;
    IF UN_TIPOESTRUCTURA <> 2388 THEN
      IF PCK_NOMINA_COM5.GL_GENERAREGISTRO367891011 = 'SI' THEN
          PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10)
                               /*CAMPO1*/ || '11'
                               /*CAMPO2*/ || PCK_SYSMAN_UTL.FC_STRZERO(GL_TOTALPENSIONES11 , 10)
                               /*CAMPO3*/ || PCK_SYSMAN_UTL.FC_STRZERO(GL_TOTALSALUD11     , 10)
                               /*CAMPO4*/ || PCK_SYSMAN_UTL.FC_STRZERO(GL_TOTALRIESGOS11   , 10)
                               /*CAMPO5*/ || PCK_SYSMAN_UTL.FC_STRZERO(GL_TOTALCAJAS11     , 10)
                               /*CAMPO6*/ || PCK_SYSMAN_UTL.FC_STRZERO(GL_TOTALSENA11      , 10)
                               /*CAMPO7*/ || PCK_SYSMAN_UTL.FC_STRZERO(GL_TOTALICBF11      , 10)
                               /*CAMPO8*/ || PCK_SYSMAN_UTL.FC_STRZERO(GL_TOTALESAP11      , 10)
                               /*CAMPO9*/ || PCK_SYSMAN_UTL.FC_STRZERO(GL_TOTALINSTITUTOS11, 10)
                               /*CAMPO10*/ || PCK_SYSMAN_UTL.FC_STRZERO(MI_GRANTOTAL       , 10)
                               /*CAMPO11*/ || PCK_SYSMAN_UTL.FC_STRZERO(GL_NUMADMPENSIONES , 10)
                               /*CAMPO12*/ || PCK_SYSMAN_UTL.FC_STRZERO(GL_NUMADMSALUD     , 10)
                               /*CAMPO13*/ || PCK_SYSMAN_UTL.FC_STRZERO(GL_NUMADMARP       , 10)
                               /*CAMPO14*/ || PCK_SYSMAN_UTL.FC_STRZERO(GL_NUMADMCAJAS     , 10);
      END IF;
    END IF;
  END IF;
  PR_GENERARTEXGTOFINAL;
  IF SUBSTR(PCK_NOMINA_COM5.GL_DISCOLIQUIDACION,1,2) = CHR(13) || CHR(10)  THEN
    PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := SUBSTR(PCK_NOMINA_COM5.GL_DISCOLIQUIDACION,3);
  END IF;
END PR_SISTEMAINTEGRADOELECTRONICO;



PROCEDURE PR_CALCULAR_NVST(
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_NITCOMPANIA     IN VARCHAR2
)
--TRAERNOVEDADNVST
AS 

BEGIN
  IF UN_NITCOMPANIA = '800.103.923-8' THEN 
      GL_NVST := CASE WHEN PCK_NOMINA.FC_CNA(10)           <> 0 
                        OR PCK_NOMINA.FC_CNA(70)           <> 0 
                        OR PCK_NOMINA.FC_CNA(506)          <> 0 
                        OR PCK_NOMINA.FC_CNA(170)          <> 0 
                        OR PCK_NOMINA.FC_CNA(155)          <> 0 
                        OR PCK_NOMINA.FC_SUMACONA(500,598) <> 0 
                 THEN 'X' 
                 ELSE ' ' END;
    ELSIF UN_NITCOMPANIA IN('800.093.257-6' ,'8000932576') THEN 
      GL_NVST := CASE WHEN PCK_NOMINA.FC_CNA(10)  <> 0 
                        OR PCK_NOMINA.FC_CNA(70)  <> 0 
                        OR PCK_NOMINA.FC_CNA(150) <> 0 
                        OR PCK_NOMINA.FC_CNA(506) <> 0 
                        OR PCK_NOMINA.FC_CNA(180) <> 0 
                        OR PCK_NOMINA.FC_CNA(62)  <> 0 
                        OR PCK_NOMINA.FC_CNA(79)  <> 0 
                        OR PCK_NOMINA.FC_CNA(170) <> 0 
                 THEN 'X' 
                 ELSE ' ' END;
    ELSIF UN_NITCOMPANIA IN('832001512-2' ,'832.001.512-2') THEN 
      GL_NVST := CASE WHEN PCK_NOMINA.FC_CNA(10)  <> 0 
                        OR PCK_NOMINA.FC_CNA(70)  <> 0 
                        OR PCK_NOMINA.FC_CNA(150) <> 0 
                        OR PCK_NOMINA.FC_CNA(506) <> 0 
                        OR PCK_NOMINA.FC_CNA(180) <> 0 
                        OR PCK_NOMINA.FC_CNA(170) <> 0 
                 THEN 'X' 
                 ELSE ' ' END;
    ELSIF UN_NITCOMPANIA IN('844.000.755-4') THEN 
      GL_NVST := CASE WHEN PCK_NOMINA.FC_CNA(10)  <> 0 
                        OR PCK_NOMINA.FC_CNA(70)  <> 0 
                        OR PCK_NOMINA.FC_CNA(150) <> 0 
                        OR PCK_NOMINA.FC_CNA(506) <> 0 
                        OR PCK_NOMINA.FC_CNA(180) <> 0 
                        OR PCK_NOMINA.FC_CNA(362) <> 0 
                        OR PCK_NOMINA.FC_CNA(170) <> 0 
                        OR PCK_NOMINA.FC_CNA(79)  <> 0 
                        OR PCK_NOMINA.FC_CNA(527) <> 0 
                 THEN 'X' 
                 ELSE ' ' END;
    ELSE
      GL_NVST := CASE WHEN PCK_NOMINA.FC_CNA(10)  <> 0 
                        OR PCK_NOMINA.FC_CNA(70)  <> 0 
                        OR PCK_NOMINA.FC_CNA(150) <> 0 
                        OR PCK_NOMINA.FC_CNA(506) <> 0 
                        OR PCK_NOMINA.FC_CNA(180) <> 0 
                        OR PCK_NOMINA.FC_CNA(362) <> 0 
                        OR PCK_NOMINA.FC_CNA(170) <> 0 
                        OR PCK_NOMINA.FC_CNA(175) <> 0 
                 THEN 'X' 
                 ELSE ' ' END;
      IF GL_NVST = ' ' 
        AND ((PCK_NOMINA_COM5.FC_ESFACTOR_PARAFISCAL(UN_COMPANIA => UN_COMPANIA, 
                                                     UN_CONCEPTO =>79)<>0 
          OR PCK_NOMINA_COM5.FC_ESFACTOR_PARAFISCAL(UN_COMPANIA => UN_COMPANIA, 
                                                    UN_CONCEPTO =>8)<>0) 
        AND (PCK_NOMINA.FC_CNA(79) + PCK_NOMINA.FC_CNA(80)) > 0) 
          OR ((PCK_NOMINA_COM5.FC_ESFACTOR_PARAFISCAL(UN_COMPANIA => UN_COMPANIA, 
                                                      UN_CONCEPTO =>155)<>0 
           AND PCK_NOMINA.FC_CNA(155) > 0) 
          AND PCK_NOMINA.FC_CNA(155) >= 1) 
          OR ((PCK_NOMINA_COM5.FC_ESFACTOR_PARAFISCAL(UN_COMPANIA => UN_COMPANIA, 
                                                      UN_CONCEPTO =>174)<>0 
           AND PCK_NOMINA.FC_CNA(174) > 0) 
          AND PCK_NOMINA.FC_CNA(174) >= 1) THEN   
        GL_NVST := 'X';
      END IF;
      IF PCK_NOMINA_COM5.CSOI(PCK_NOMINA.P).TIPOVINCULACION IN('12', '19') THEN
        GL_NVST := ' ';
      END IF;
    END IF;
    IF UN_NITCOMPANIA IN('830.065.741-1') 
     AND GL_NVST = ' ' 
     AND PCK_NOMINA.FC_CNA(188) <> 0 THEN 
      GL_NVST := 'X';
    END IF;
    IF UN_NITCOMPANIA IN('60.028.093-7') 
     AND GL_NVST = ' ' 
     AND PCK_NOMINA.FC_CNA(570) + PCK_NOMINA.FC_CNA(590) +
         PCK_NOMINA.FC_SUMACONA(500,559) <> 0 THEN 
      GL_NVST := 'X';
    END IF;
END PR_CALCULAR_NVST; 

FUNCTION FC_ESFACTOR_PARAFISCAL(

  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CONCEPTO        IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO
  )
      /*
        NAME              : Se separa de InterfazContableH
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 28/12/2017
        TIME              : 11:43 AM
        SOURCE MODULE     : 
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : 
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:
        @METHOD:  POST
    */
RETURN NUMBER
AS
  MI_RETORNO NUMBER;
BEGIN
  MI_RETORNO:=0;
  SELECT COUNT(COMPANIA)
  INTO MI_RETORNO
  FROM CONCEPTOS
  WHERE COMPANIA       = UN_COMPANIA
    AND ID_DE_CONCEPTO = UN_CONCEPTO;
  RETURN MI_RETORNO;
END FC_ESFACTOR_PARAFISCAL;

FUNCTION FC_ENCABEZADOINTEGRADO(
  UN_RAZONSOCIAL     IN VARCHAR2,
  UN_PERCOTIZA       IN VARCHAR2,
  UN_INICIAL         IN PCK_SUBTIPOS.TI_LOGICO
  ) 
        /*
        NAME              : Se separa de ENCABEZADOINTEGRADO
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 28/12/2017
        TIME              : 11:43 AM
        SOURCE MODULE     : 
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Llamado em el archivo plano
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:
        @METHOD:  POST
    */
  RETURN CLOB
  AS
    MI_INTEGRADO CLOB;
    MI_I NUMBER;
  BEGIN
    MI_INTEGRADO:=  CHR(13) || CHR(10);
    IF UN_INICIAL=0 THEN
      FOR MI_I IN 1 .. 6
      LOOP
        MI_INTEGRADO:= MI_INTEGRADO || CHR(13) || CHR(10);
      END LOOP;
    END IF;
    MI_INTEGRADO:= MI_INTEGRADO ||  PCK_SYSMAN_UTL.FC_PADC(UN_RAZONSOCIAL,150)
              || CHR(13) || CHR(10) 
              || PCK_SYSMAN_UTL.FC_PADC('Informe de Autoliquidacion Aportes INTEGRADO para el ciclo '||UN_PERCOTIZA, 150)
              || CHR(13) || CHR(10) 
              || RPAD('-',158,'-')
              || CHR(13) || CHR(10)
              || RPAD(' ',17,' ') || 'CEDULA' || RPAD(' ',7,' ') || 'NOMBRE' || RPAD(' ',21,' ') 
              || 'I R T T D A V V V S I L V A V I  D  D  D' || RPAD(' ',5,' ') 
              || 'SUELDO' || RPAD(' ',8,' ') 
              || 'BASE'   || RPAD(' ',6,' ')
              || 'BASE'   || RPAD(' ',8,' ')
              || 'BASE'   || RPAD(' ',9,' ')
              || 'BASE'
              || CHR(13) || CHR(10)
              || RPAD(' ',57,' ') || 'G E D A A A S T S L G M A V C R  I  I  I' || RPAD(' ',17,' ')
              || 'PENSION' || RPAD(' ',5,' ')
              || 'RIESGOS' || RPAD(' ',2,' ')
              || 'PARAFISCAL' || RPAD(' ',3,' ')
              || 'REFERENCIA'
              || CHR(13) || CHR(10)
              || RPAD(' ',57,' ') || 'R T A A P P P E T N E A C P T P  A  A  A' || RPAD(' ',17,' ')
              || 'Y SALUD'
              || CHR(13) || CHR(10)
              || RPAD('-',158,'-');
  RETURN MI_INTEGRADO;
END FC_ENCABEZADOINTEGRADO; 

FUNCTION FC_QUITAESPECIALES(
UN_STRSQL          IN CLOB
) RETURN CLOB
AS
    MI_TEXTO CLOB;
BEGIN
    MI_TEXTO:= REPLACE(UN_STRSQL, '(RET', ' ');
    MI_TEXTO:= REPLACE(MI_TEXTO, '(R',' ');
    MI_TEXTO:= REPLACE(MI_TEXTO, '(2)', ' ');
    MI_TEXTO:= REPLACE(MI_TEXTO, '(3)', ' ');
    MI_TEXTO:= REPLACE(MI_TEXTO, '(1)', ' ');
    MI_TEXTO:= REPLACE(MI_TEXTO, '(', ' ');
    MI_TEXTO:= REPLACE(MI_TEXTO, ')', ' ');
    --MI_TEXTO:= REPLACE(MI_TEXTO, '2', ' '); --<INI_CC_7752276_NOMINA (17/02/2025 CPEREZ)> se comenta por que quita el numero 2 en el plano de nomina lo cual es error 

    --(MZANGUNA:24/11/2018)-Permite caracteres especiales plano
    IF NOT GL_PERMITECARACTERESP THEN        
        MI_TEXTO:= REPLACE(MI_TEXTO, 'Ã±', 'n');
        MI_TEXTO:= REPLACE(MI_TEXTO, 'Ã‘', 'N');
        MI_TEXTO:= REPLACE(MI_TEXTO, 'Ñ', 'N'); 
        MI_TEXTO:= REPLACE(MI_TEXTO, 'ñ', 'n');
        MI_TEXTO:= REPLACE(MI_TEXTO, 'Ñ', 'N');
        MI_TEXTO:= REPLACE(MI_TEXTO, 'Ñ', 'N');       
    END IF;

    MI_TEXTO:= REPLACE(MI_TEXTO, 'Ã€', 'A');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'Ã�', 'A');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'ÃŒ', 'I');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'Ã�', 'I');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'Ãˆ', 'E');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'Ã‰', 'E');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'Ã’', 'O');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'Ã“', 'O');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'Ã™', 'U');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'Ãš', 'U');

    MI_TEXTO:= REPLACE(MI_TEXTO, 'á', 'A');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'Á', 'A');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'í', 'I');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'Í', 'I');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'é', 'E');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'É', 'E');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'ó', 'O');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'Ó', 'O');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'ú', 'U');
    MI_TEXTO:= REPLACE(MI_TEXTO, 'Ú', 'U');

    MI_TEXTO:= REPLACE(MI_TEXTO, '.', '');

    --MOD JM CC 3812 (uso esta fc para armar un json y se esta rompiendo)
    MI_TEXTO:= REPLACE(MI_TEXTO, '%', '');  
    MI_TEXTO:= REPLACE(MI_TEXTO, '#', '');   
    MI_TEXTO:= REPLACE(MI_TEXTO, 'ª', '');   
    MI_TEXTO:= REPLACE(MI_TEXTO, 'º', '');   
    MI_TEXTO:= REPLACE(MI_TEXTO, '°', '');  
    MI_TEXTO:= REPLACE(MI_TEXTO, '&', '');  
    MI_TEXTO:= REPLACE(MI_TEXTO, '´', '');   
    MI_TEXTO:= REPLACE(MI_TEXTO, '`', '');   
    MI_TEXTO:= REPLACE(MI_TEXTO, '/', '');   
    MI_TEXTO:= REPLACE(MI_TEXTO, '|', '');   
    MI_TEXTO:= REPLACE(MI_TEXTO, '+', '');  
    MI_TEXTO:= REPLACE(MI_TEXTO, '=', '');  
    MI_TEXTO:= REPLACE(MI_TEXTO, '$', '');   
    MI_TEXTO:= REPLACE(MI_TEXTO, '-', '');  
    --por las dudad estos tambien los nombres no llevan nada de esto, bueno... no deberian 
    MI_TEXTO:= REPLACE(MI_TEXTO, '(', '');  
    MI_TEXTO:= REPLACE(MI_TEXTO, ')', '');      
    MI_TEXTO:= REPLACE(MI_TEXTO, ';', '');   
    MI_TEXTO:= REPLACE(MI_TEXTO, '"', '');  
    MI_TEXTO:= REPLACE(MI_TEXTO, '\', '');   
    MI_TEXTO:= REPLACE(MI_TEXTO, ':', '');   
    MI_TEXTO:= REPLACE(MI_TEXTO, ',', '');   
    --FIN JM CC 3812
    
    RETURN MI_TEXTO;
END FC_QUITAESPECIALES;

FUNCTION FC_DCTIDENTIDAD(
  UN_TIPO      IN VARCHAR2
) RETURN VARCHAR2
--REVISAR_TIPODCTOPILA
AS
  MI_TIPO VARCHAR2(3);
BEGIN
  IF UN_TIPO IN('01','C') THEN
      MI_TIPO:='CC';
  ELSIF UN_TIPO IN('02', 'E') THEN
      MI_TIPO:='CE';
  ELSIF UN_TIPO IN('04', 'T') THEN
      MI_TIPO:='TI';
  ELSIF UN_TIPO IN('03','P') THEN
      MI_TIPO:='PA';
  ELSIF UN_TIPO IN('05', '09', 'O','R') THEN
      MI_TIPO:='RC';
  ELSIF UN_TIPO IN('07', 'N') THEN
      MI_TIPO:='NI';
  ELSIF UN_TIPO IN('11') THEN
      MI_TIPO:='CD';
  ELSIF UN_TIPO IN('10', 'D') THEN
      MI_TIPO:='PD';
  ELSIF UN_TIPO IN('14') THEN
      MI_TIPO:='PE';
  ELSE
      MI_TIPO:='CC';
  END IF;
  RETURN MI_TIPO;
END FC_DCTIDENTIDAD;

FUNCTION FC_DCTIDENTIDAD(
  UN_TIPO      IN VARCHAR2,
  UN_DOCUMENTO IN VARCHAR2,
  UN_COMPANIA IN VARCHAR2
) RETURN VARCHAR2
AS
  MI_TIPO VARCHAR2(3);
BEGIN
    IF (UN_TIPO = '' OR UN_TIPO IS NULL) AND (UN_DOCUMENTO <> ''  OR UN_DOCUMENTO <> '999999999999999999') THEN 
         MI_TIPO:='CC'; --SI TRAE INFORMACION DEL DOCUMENTO PERO NO DEL TIPO DEFAULT CC
         RETURN MI_TIPO;
    ELSIF  (UN_DOCUMENTO = '' OR UN_DOCUMENTO = '999999999999999999') THEN 
         MI_TIPO:='  '; --SI NO TARE INFORMACION EN TIPO NI EN DOC; NO MUESTRA NADA 
         RETURN MI_TIPO;
    ELSE --EN PERSONAL HISTORICOS NO SIEMPRE GUARDA LAS SIGLAS AVECES GUARDA EL CODIGO (no se de donde toma ese codigo xd)
             IF UN_TIPO IN('01') THEN
                  MI_TIPO:='CC';
                  RETURN MI_TIPO;
              ELSIF UN_TIPO IN('02') THEN
                  MI_TIPO:='CE';
                  RETURN MI_TIPO;
              ELSIF UN_TIPO IN('04') THEN
                  MI_TIPO:='TI';
                  RETURN MI_TIPO;
              ELSIF UN_TIPO IN('03') THEN
                  MI_TIPO:='PA';
                  RETURN MI_TIPO;
              ELSIF UN_TIPO IN('05', '09') THEN
                  MI_TIPO:='RC';
                  RETURN MI_TIPO;
              ELSIF UN_TIPO IN('07') THEN
                  MI_TIPO:='NI';
                  RETURN MI_TIPO;
              ELSIF UN_TIPO IN('11') THEN
                  MI_TIPO:='CD';
                  RETURN MI_TIPO;
              ELSIF UN_TIPO IN('10') THEN
                  MI_TIPO:='PD';
                  RETURN MI_TIPO;
              ELSIF UN_TIPO IN('14') THEN
                  MI_TIPO:='PE';
                  RETURN MI_TIPO;
             END IF;
            --en caso de que venga por sigla que lo busque en la tabla 
            BEGIN
                    SELECT LPAD(NVL(SIGLA2,'  '),2) 
                    INTO MI_TIPO 
                    FROM TIPOS_DOCUMENTOS 
                    WHERE COMPANIA = UN_COMPANIA 
                    AND SIGLA IN (LPAD(UN_TIPO,1)) GROUP BY SIGLA2;
                    
             EXCEPTION WHEN TOO_MANY_ROWS THEN 
                    MI_TIPO:='CC';
                    RETURN MI_TIPO;
                WHEN  NO_DATA_FOUND THEN
                    MI_TIPO:='  ';
                    RETURN MI_TIPO;
            END;
         RETURN MI_TIPO;
    END IF;
END FC_DCTIDENTIDAD;

FUNCTION FC_CAMPOSNUEVOS2388INGRET(
		UN_ANNO           IN PCK_SUBTIPOS.TI_ANIO,
		UN_MMES           IN PCK_SUBTIPOS.TI_MES,
		UN_PNING          IN VARCHAR2,
		UN_PNRET          IN VARCHAR2,
    UN_PNVST          IN VARCHAR2,
		UN_DIASTRABAJADOS IN INTEGER,
		UN_I              IN NUMBER,
    UN_RBASE1990      IN VARCHAR2,
    UN_HORASMENSUALES IN NUMBER,
    UN_DIASSEMANALES  IN NUMBER
    )
    /*
    NAME              : TRAERNOVEDADES_CAMPOSNUEVOS2388INGRET  --> EN ACCESS Relacionado  -
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : JOSE PASCUAL GOMEZ
    DATE MIGRATION    : 29/12/2017
    TIME              : 09:11 AM
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:
  */
    --UN_I es la posiciÂ¿el vector para consultar PCK_NOMINA_COM5.CSOI;
    --     que se debe enviar desde la funciÂ¿R_SISTEMAINTEGRADOELECTRONICO
	RETURN CLOB
	AS
	BEGIN
    PR_LIMPIARCAMPSO80A97();

    IF UN_PNING <> ' '  THEN
      GL_CAMPO80:= TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_DE_INGRESO, 'YYYY-MM-DD');
    END IF;
    IF UN_PNRET <> ' ' THEN
      GL_CAMPO81 :=  TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHATERCONTRATO, 'YYYY-MM-DD');
    END IF;
    IF UN_PNVST <> ' ' AND PCK_NOMINA_COM5.CSOI(UN_I).FECHA_ULTSUELDO IS NOT NULL THEN
      IF     TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_ULTSUELDO,'MM'))   = UN_MMES
         AND TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_ULTSUELDO,'YYYY')) = UN_ANNO THEN
        GL_CAMPO82 :=  TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_ULTSUELDO, 'YYYY-MM-DD');
      END IF;
    END IF;

	IF PCK_NOMINA_COM5.CSOI(UN_I).ESTADO_ACTUAL = 6 AND PCK_NOMINA.FC_CNA(339) >= 30 THEN --(MZANGUNA:14/03/2019),Se agrega campos para comisiones de 30 dÂ¿.
		GL_CAMPO83 := TO_CHAR(TO_DATE('01/' || UN_MMES || '/' || UN_ANNO, 'DD/MM/YYYY'), 'YYYY-MM-DD');
		GL_CAMPO84 := TO_CHAR(LAST_DAY(TO_DATE('01/' || UN_MMES || '/' || UN_ANNO, 'DD/MM/YYYY')), 'YYYY-MM-DD');
	END IF;


    IF (PCK_NOMINA_COM5.CSOI(UN_I).SENA + PCK_NOMINA_COM5.CSOI(UN_I).ICBF
      + PCK_NOMINA_COM5.CSOI(UN_I).ESAP + PCK_NOMINA_COM5.CSOI(UN_I).INSTITUTOS) = 0 THEN
      GL_CAMPO95 := 0;
    ELSE
      GL_CAMPO95 := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     =>PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL
                                                          + FC_SUMARORSTAR1PESO(UN_VALOR1 => PCK_NOMINA_COM5.GL_SUELDO
                                                                               ,UN_VALOR2 => PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL)
                                          ,UN_PRECISION => UN_RBASE1990);
    END IF;
    GL_CAMPO96 := '' || PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => (UN_HORASMENSUALES/5) / UN_DIASSEMANALES * UN_DIASTRABAJADOS
                                         ,UN_PRECISION => 0);
	---<TAR:7702448 FECHA:07/02/2022 AUTOR:CP>
    IF GL_EXONERADO AND PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION NOT IN('12', '19', '23', '51') THEN 
       GL_CAMPO95 := 0;
    END IF;
    ---</TAR>
  IF GL_COMISION30 THEN --(MZANGUNA:02/05/2019)-Se agrega condiciÂ¿ara no enviar al plano si se tienen comisiones de 30 dÂ¿.
		GL_CAMPO95 := 0;
	END IF;

    RETURN FC_CONCATENARCAMPSO80A97;
END FC_CAMPOSNUEVOS2388INGRET;



FUNCTION FC_REGISTROPLANO(
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_I                IN NUMBER,
    UN_TIPOESTRUCTURA   IN VARCHAR2,
    UN_CLASEREGISTRO    IN VARCHAR2,
    UN_TIPOPLANILLA     IN VARCHAR2,
    UN_CAMPOSNUEVOS2388 IN CLOB,
    UN_NUEVATABLA       IN PCK_SUBTIPOS.TI_LOGICO := 0
    )
    /*
    NAME              : TRAERNOVEDADES_CAMPOSNUEVOS2388INGRET  --> EN ACCESS Relacionado  -
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : JOSE PASCUAL GOMEZ
    DATE MIGRATION    : 29/12/2017
    TIME              : 09:11 AM
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:
  */
    --UN_I es la posiciÂ¿el vector para consultar PCK_NOMINA_COM5.CSOI;
    --     que se debe enviar desde la funciÂ¿R_SISTEMAINTEGRADOELECTRONICO
RETURN CLOB
AS
  MI_PART1            CLOB;
  MI_PART2            CLOB;
  MI_AAPELLIDO1       VARCHAR2(30);
  MI_AAPELLIDO2       VARCHAR2(30);
  MI_NOMBRES          VARCHAR2(255);
  MI_PNOMBRE          VARCHAR2(255);
  MI_SNOMBRE          VARCHAR2(255);
  MI_TIPODCTO         VARCHAR2(3);
  MI_SALARIOMINIMO    NUMBER;
  MI_CAMPO36          VARCHAR2(2);
  MI_CAMPO38          VARCHAR2(2);
  MI_CAMPO39          VARCHAR2(2);
  MI_CAMPO42          VARCHAR2(9);
  MI_CAMPO46          VARCHAR2(7);
  MI_CAMPO54          VARCHAR2(7);
  MI_CAMPO61          VARCHAR2(9);
  MI_BASEPENSION      NUMBER;
  MI_PORCPENSION      NUMBER;
  MI_ACTECOCIIU       VARCHAR2(17);
  MI_VALAUX           NUMBER;
  MI_FONDO_ACCAI      VARCHAR2(20);
  MI_REGIMEN_TRANSICION NUMBER;
BEGIN

  GL_CONTADOR := GL_CONTADOR+1;
  MI_SALARIOMINIMO := PCK_PARENTR.PARAMETRO20;
  MI_TIPODCTO   := PCK_NOMINA_COM5.FC_DCTIDENTIDAD(UN_TIPO => NVL(PCK_NOMINA_COM5.CSOI(UN_I).DCTO_IDENTIDAD, ''));
  MI_AAPELLIDO1 := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(PCK_NOMINA_COM5.CSOI(UN_I).APELLIDO1, ''));
  MI_AAPELLIDO2 := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(PCK_NOMINA_COM5.CSOI(UN_I).APELLIDO2, ''));
  MI_NOMBRES := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(PCK_NOMINA_COM5.CSOI(UN_I).NOMBRES, ''));
  MI_PNOMBRE := '';
  MI_SNOMBRE := '';
  MI_PNOMBRE := CASE WHEN INSTR(MI_NOMBRES,' ',1) = 0
                THEN MI_NOMBRES
                ELSE TRIM(SUBSTR(MI_NOMBRES,1, INSTR(MI_NOMBRES,' ',1)))
                END;
  MI_SNOMBRE := CASE WHEN INSTR(MI_NOMBRES,' ',1)  = 0
                THEN ' '
                ELSE SUBSTR(MI_NOMBRES,INSTR(MI_NOMBRES, ' ', 1, 1)+1, LENGTH(MI_NOMBRES))
                END;
  
  -- TICKET 7722577 ECABRERA: ADICION CAMPO 98 ACTIVIDAD ECONOMICA CIIU AL ARCHIVO PLANO            
    IF (GL_CIIU2012) THEN 
        MI_ACTECOCIIU := RPAD(PCK_NOMINA_COM5.CSOI(UN_I).ID_CIIU,7,' ');
    ELSE
        MI_ACTECOCIIU := NULL;
    END IF;
    -- TICKET 7722577 ECABRERA
    
       BEGIN
    SELECT VFP.CODAIFP,
      P.REGIMEN_TRANSICION
         INTO MI_FONDO_ACCAI,
         MI_REGIMEN_TRANSICION
         FROM PERSONAL P
         INNER JOIN V_FONDO_DE_PENSIONES VFP 
           ON P.COMPANIA = VFP.COMPANIA 
          AND P.FONDO_ACCAI = VFP.ID_DEL_FONDO
         WHERE P.COMPANIA = UN_COMPANIA
           AND P.ID_DE_EMPLEADO = PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_FONDO_ACCAI := '';
       MI_REGIMEN_TRANSICION := 0;
      END;

  --(APINEDA:03/09/2019)-En la FederaciÃ³n Nacional de Departamentos el valor no retenido y el aporte a Fondo de Pension Voluntario no se reportan en el plano para ProtecciÃ³n S.A TAR1000094026.
  --(APINEDA:19/09/2019)-No debe llevar la marcacion de novedad AVP (campo 28) en el plano cuando el fondo corresponde a ProtecciÃ³n TAR1000094399. 
  IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                          ,UN_NOMBRE    => 'ENTIDAD PUBLICA O PRIVADA'
                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                          ,UN_FECHA_PAR => SYSDATE) = 'PRIVADA' AND PCK_NOMINA_COM5.CSOI(UN_I).NITFONDOPENSION_VOL = '800229739' THEN
      GL_APVOL := 0;
      GL_BENEFICIOT := 0;
      GL_SNAVP := ' ';
      GL_NAVP := ' ';
  END IF;

  MI_PART1 := /*CAMPO1*/ '02'
                /*CAMPO2*/ || PCK_SYSMAN_UTL.FC_STRZERO(GL_CONTADOR, 5)
                /*CAMPO3*/ || MI_TIPODCTO
                /*CAMPO4*/ || RPAD(PCK_NOMINA_COM5.CSOI(UN_I).NUMERO_DCTO, 16,' ')
                /*CAMPO5*/ || NVL(PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION, 2), '01')
                /*CAMPO6*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN UN_CLASEREGISTRO ='UPC'
                                                        THEN 0
                                                        ELSE NVL(PCK_NOMINA_COM5.CSOI(UN_I).SUBTIPOCOTIZANTE,0)
                                                        END
                                                        , 2)
                /*CAMPO7*/ || RPAD(' ', 1,' ')
                /*CAMPO8*/ || RPAD(' ', 1,' ')
                /*CAMPO9*/ || PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).DPTO_LAB, 2)
                /*CAMPO10*/ || PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).CIUDAD_LAB, 3)
                /*CAMPO11*/ || RPAD(UPPER(NVL(MI_AAPELLIDO1, MI_AAPELLIDO2)), 20,' ')
                /*CAMPO12*/ || RPAD(CASE WHEN MI_AAPELLIDO2 IS NULL
                                    THEN ' '
                                    ELSE UPPER(MI_AAPELLIDO2)
                                    END, 30,' ')
                /*CAMPO13*/ || RPAD(UPPER(MI_PNOMBRE), 20,' ')
                /*CAMPO14*/ || RPAD(UPPER(NVL(MI_SNOMBRE,' ')), 30,' ') --(APINEDA:29/05/2019)-Se adiciona NVL para que cuÂ¿o el segundo nombre este vacÂ¿tome correctamente RPAD
                /*CAMPO15*/ || CASE WHEN UN_CLASEREGISTRO IN('LNF', 'COM')
                                    THEN ' '
                                    ELSE CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_SNING  ELSE GL_NING  END
                               END
                /*CAMPO16*/ || CASE WHEN UN_CLASEREGISTRO = 'COM'
                                    THEN ' '
                                    ELSE CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_SNRET  ELSE GL_NRET  END
                               END
                /*CAMPO17*/ || CASE WHEN UN_CLASEREGISTRO IN('LNF', 'COM')
                                    THEN ' '
                                    ELSE CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_SNTDA  ELSE GL_NTDA  END
                               END
                /*CAMPO18*/ || CASE WHEN UN_CLASEREGISTRO IN('LNF', 'COM')
                                    THEN ' '
                                    ELSE CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_SNTAA  ELSE GL_NTAA  END
                               END
                /*CAMPO19*/ || CASE WHEN UN_CLASEREGISTRO IN('LNF', 'COM')
                                    THEN ' '
                                    ELSE CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_SNTDAP ELSE GL_NTDAP END
                               END
                /*CAMPO20*/ || CASE WHEN UN_CLASEREGISTRO IN('LNF', 'COM')
                                    THEN ' '
                                    ELSE CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_SNTAAP ELSE GL_NTAAP END
                               END
                /*CAMPO21*/ || CASE WHEN UN_CLASEREGISTRO IN('LNF', 'COM')
                                    THEN ' '
                                    ELSE CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_SNVSP  ELSE GL_NVSP  END
                               END
                /*CAMPO22*/ || CASE WHEN UN_CLASEREGISTRO IN('LNF', 'COM')
                                    THEN ' '
                                    ELSE CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_SVTE   ELSE GL_VTE   END
                               END
                /*CAMPO23*/ || CASE WHEN UN_CLASEREGISTRO IN('LNF', 'COM')
                                    THEN ' '
                                    ELSE CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_SNVST  ELSE GL_NVST  END
                               END
                /*CAMPO24*/ || CASE WHEN UN_CLASEREGISTRO = 'LNR'
                               THEN ' '
                               ELSE CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_SNSLN  ELSE GL_NSLN  END
                               END
                /*CAMPO25*/ || CASE WHEN UN_CLASEREGISTRO IN('LNF', 'COM')
                                    THEN ' '
                                    ELSE CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_SNIGE  ELSE GL_NIGE  END
                               END
                /*CAMPO26*/ || CASE WHEN UN_CLASEREGISTRO IN('LNF', 'COM')
                                    THEN ' '
                                    ELSE CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_SNLMA  ELSE GL_NLMA  END
                               END
                /*CAMPO27*/ || CASE WHEN UN_CLASEREGISTRO IN('LNF', 'COM')
                                    THEN ' '
                                    ELSE CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_SNVAC  ELSE GL_NVAC  END
                               END
                /*CAMPO28*/ || CASE WHEN UN_CLASEREGISTRO IN('LNF', 'COM')
                                    THEN ' '
                                    ELSE CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_SNAVP  ELSE GL_NAVP  END
                               END
                /*CAMPO29*/ || CASE WHEN UN_CLASEREGISTRO IN('LNF', 'COM')
                                    THEN ' '
                                    ELSE CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_SNVCT  ELSE GL_NVCT  END
                               END
                /*CAMPO30*/ || CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_SNIRP  ELSE GL_NIRP  END
                /*CAMPO31*/ || CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).ID_DEL_FONDO = 'AFP99'
                              THEN RPAD(' ', 6,' ')
                              ELSE CASE WHEN (PCK_NOMINA_COM5.CSOI(UN_I).ANO || LPAD(PCK_NOMINA_COM5.CSOI(UN_I).MES,2,0) >= '202507'  AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'APLICAR REFORMA PENSIONAL ADICIONAL FSPYFS',PCK_DATOS.FC_MODULONOMINA,SYSDATE),'NO') = 'SI' AND MI_REGIMEN_TRANSICION = 0) THEN
                               RPAD(NVL(MI_FONDO_ACCAI,' '), 6, ' ')
                                 ELSE GL_CODAIFP END
                              END
                /*CAMPO32*/ || GL_CODIGOTRASLADOPENSION
                /*CAMPO33*/ || CASE WHEN TRIM(GL_CODAIFS) = '99' AND UN_TIPOPLANILLA = 'K' THEN RPAD(' ', 6,' ') ELSE RPAD(GL_CODAIFS, 6,' ') END                 			   
                /*CAMPO34*/ || CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12', '19')
                               THEN RPAD(' ', 6,' ')
                               ELSE GL_CODIGOTRASLADOSALUD END
                /*CAMPO35 || CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12', '19')
                               THEN RPAD(' ', 6,' ')
                               ELSE RPAD(GL_CODAICC, 6) END;
                               */
                           || CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('23') AND UN_TIPOPLANILLA = 'K'
                               THEN RPAD(' ', 6,' ')
                               ELSE RPAD(GL_CODAICC, 6,' ') END;
                /*CAMPO36*/
                              IF UN_CLASEREGISTRO IN('NOV') THEN
                                 IF PCK_NOMINA_COM5.CSOI(UN_I).SUBTIPOCOTIZANTE <> '00' THEN--(CC:765_CFBARRERA_Ajuste cuando es SubTipo Cotizante  0, no genere dias de pension)
                                MI_CAMPO36 := PCK_SYSMAN_UTL.FC_STRZERO(0,2);
                                ELSE
                                    MI_CAMPO36 := PCK_SYSMAN_UTL.FC_STRZERO(GL_DIASP,2);--(CC:765_FIN)
                                END IF;
                              ELSIF  UN_CLASEREGISTRO IN ('COM') AND PCK_NOMINA_COM5.CSOI(UN_I).ESTADO_ACTUAL = 6 AND PCK_NOMINA.FC_CNA(339) >= 30 THEN --(MZANGUNA:14/03/2019)-CondiciÂ¿ara comisiones de 30 dÂ¿.
                                MI_CAMPO36 := PCK_SYSMAN_UTL.FC_STRZERO(GL_DIASPA,2);
                              ELSIF PCK_NOMINA_COM5.CSOI(UN_I).PENSION = 0
                              OR PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12', '19') THEN
                                MI_CAMPO36 := PCK_SYSMAN_UTL.FC_STRZERO(0,2);
                              ELSIF GL_CODAIFP = RPAD(' ', 6,' ')
                                 OR PCK_NOMINA_COM5.CSOI(UN_I).ID_DEL_FONDO = 'AFP99' THEN
                                MI_CAMPO36 := PCK_SYSMAN_UTL.FC_STRZERO(
                                                   CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('17')
                                                         AND GL_FSPPLANILLA <> 0
                                                         AND PCK_NOMINA_COM5.CSOI(UN_I).PENSION > 0
                                                   THEN '30'
                                                   ELSE '00'
                                                   END
                                              ,2);
                              ELSE
                                MI_CAMPO36 := PCK_SYSMAN_UTL.FC_STRZERO(GL_NUMDIAS,2);
                              END IF;
    --(APINEDA:28/11/2019)-Se cambia variable GL_DIASP por GL_DIASS debido a que esta dejando los dÃ­as de pensiÃ³n y para empleados SENA no aplica.
    --(GPORTILLA:31/01/2022)-7721354_NOMINA se adiciona validacion del tipo de vinculacion 23, ya que este campo no aplica para estudiantes del SENA
    MI_PART1 := MI_PART1 || MI_CAMPO36
                /*CAMPO37*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN UN_CLASEREGISTRO IN('LNR')
                                                         THEN GL_NUMDIAS
                                                         WHEN PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('23') AND UN_TIPOPLANILLA = 'K' THEN '00'
                                                         ELSE CASE WHEN UN_CLASEREGISTRO IN('NOV')
                                                              THEN GL_DIASS
                                                              ELSE GL_NUMDIASS
                                                              END
                                                         END, 2);
    MI_CAMPO38 := '00';
    IF UN_CLASEREGISTRO IN('NOV') THEN
        MI_CAMPO38:= PCK_SYSMAN_UTL.FC_STRZERO(GL_DIASR,2);
    ELSIF UN_CLASEREGISTRO IN('LNF', 'COM') THEN
        IF PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS = 0 AND GL_NUMDIASS = 0 THEN
            MI_CAMPO38:= '00';
        ELSE
            MI_CAMPO38:= PCK_SYSMAN_UTL.FC_STRZERO(GL_NUMDIASS, 2);
        END IF;
    ELSE
        IF PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS = 0 THEN
            MI_CAMPO38:= '00';
        ELSIF PCK_NOMINA_COM5.CSOI(UN_I).RIESGOS > 0 AND PCK_NOMINA_COM5.CSOI(UN_I).DIASRIESGOS = 0 THEN
            MI_CAMPO38:= '30';
        ELSE
            --(JORDUZ: 25/08/2020)-- Se hace validacion para que los dias a reportar en el plano en riesgos no supere 30 dias
            IF PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).DIASRIESGOS, 2) > '30' THEN
                MI_CAMPO38:= '30';
            ELSE
                MI_CAMPO38:= PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).DIASRIESGOS, 2);
            END IF;    
        END IF;
    END IF;

    MI_PART1 := MI_PART1 || /*CAMPO38*/  MI_CAMPO38;

    MI_CAMPO39 := '00';
    IF UN_CLASEREGISTRO = 'NOV' THEN
        MI_CAMPO39 := PCK_SYSMAN_UTL.FC_STRZERO(GL_DIASC,2);
    ELSIF UN_CLASEREGISTRO IN('LNF', 'COM') THEN
        MI_CAMPO39 := PCK_SYSMAN_UTL.FC_STRZERO(GL_DIASPA,2);
    ELSIF UN_CLASEREGISTRO = 'UPC' THEN
        IF PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL <>0 THEN
            MI_CAMPO39 := PCK_SYSMAN_UTL.FC_STRZERO(GL_NUMDIASP,2);
        END IF;
    ELSIF PCK_NOMINA_COM5.CSOI(UN_I).CAJAS<>0 THEN
        MI_CAMPO39 := PCK_SYSMAN_UTL.FC_STRZERO(GL_DIASPA,2);
    END IF;
    
/*   
    --(JALFONSO:30/09/2020)-TAR 1000101466 se valida el tipo de vinculacion, si es 12 (SENA) el campo 41 debe ir vacio
    IF PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12') THEN
     GL_SALARIOINTEGRAL := ' ';
    
    --(APINEDA:24/06/2020)-TAR 1000099931 ValidaciÃ³n campo 41 tipo de salario resolucion 454 de 2020
    ELSIF GL_SALARIOINTEGRAL <> 'X' AND (GL_SNVST = 'X' OR GL_NVST = 'X') THEN
        GL_SALARIOINTEGRAL := 'V';
    ELSE
        GL_SALARIOINTEGRAL := 'F';
    END IF;
 */   
 --<TAR:7702448 FECHA:07/02/2022 AUTOR:CP>
    IF (PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12','19')) OR (PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('23') AND UN_TIPOPLANILLA = 'K') THEN
      GL_SALARIOINTEGRAL := ' ';
    ELSE 
      GL_SALARIOINTEGRAL := PCK_NOMINA_COM5.CSOI(UN_I).TIPO_SALARIO;
    END IF;
--</TAR>

    MI_PART1 := MI_PART1 ||
                /*CAMPO39*/ MI_CAMPO39
                /*CAMPO40*/ || PCK_SYSMAN_UTL.FC_STRZERO(
                                CASE WHEN UN_CLASEREGISTRO = 'NOV'
                                THEN GL_SUELDOP
                                ELSE
                                  CASE WHEN GL_SUELDO < MI_SALARIOMINIMO
                                  THEN CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC = PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_SALARIOMINIMO
                                                                                                                      ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990)
                                             AND PCK_NOMINA_COM5.CSOI(UN_I).BASE  = MI_SALARIOMINIMO
                                       THEN MI_SALARIOMINIMO
                                       ELSE PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC
                                       END
                                  ELSE PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC
                                  END
                                END
                               , 9)
                /*CAMPO41*/ || GL_SALARIOINTEGRAL;
    IF UN_CLASEREGISTRO IN('NOV', 'NOVF') THEN
        MI_BASEPENSION := GL_BASEPENSIONP;
    ELSE
        MI_BASEPENSION := PCK_NOMINA_COM5.CSOI(UN_I).BASE
            + FC_SUMARORSTAR1PESO(UN_VALOR1 => GL_SUELDO
                     ,UN_VALOR2 => PCK_NOMINA_COM5.CSOI(UN_I).BASE);
    END IF;

     --JM INI 05/12/2024 CC427
     IF (PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE < MI_SALARIOMINIMO AND PCK_PARENTR.PARAMETRO31 =  '860061110' AND UN_CLASEREGISTRO <> 'COM' ) THEN 
        IF  MI_BASEPENSION <  MI_SALARIOMINIMO AND MI_BASEPENSION > 0 THEN 
            MI_BASEPENSION := MI_SALARIOMINIMO;
        END IF;
        IF  GL_BASESALUDP <  MI_SALARIOMINIMO AND GL_BASESALUDP > 0 THEN 
            GL_BASESALUDP := MI_SALARIOMINIMO;
        END IF;
         IF  GL_BASESALUD <  MI_SALARIOMINIMO AND GL_BASESALUD > 0 THEN 
            GL_BASESALUD := MI_SALARIOMINIMO;
        END IF;
        IF  GL_BASECAJASP <  MI_SALARIOMINIMO AND GL_BASECAJASP > 0 THEN 
            GL_BASECAJASP := MI_SALARIOMINIMO;
        END IF;    
        IF  GL_BASERIESGOSP <  MI_SALARIOMINIMO AND GL_BASERIESGOSP > 0 THEN 
            GL_BASERIESGOSP := MI_SALARIOMINIMO;
        END IF;  
    END IF; --JM FIN 05/12/2024 CC427

    MI_CAMPO42:=PCK_SYSMAN_UTL.FC_STRZERO(0,9);
    IF UN_CLASEREGISTRO IN('COM') THEN
        MI_CAMPO42:=PCK_SYSMAN_UTL.FC_STRZERO(GL_BASESALUD,9);
    ELSIF MI_BASEPENSION>0 AND  PCK_NOMINA_COM5.CSOI(UN_I).PENSION = 0 THEN
        IF UN_CLASEREGISTRO IN('NOV', 'NOVF') THEN
           IF PCK_NOMINA_COM5.CSOI(UN_I).SUBTIPOCOTIZANTE <> '00' THEN--(CC:765_CFBARRERA_Ajuste cuando es SubTipo Cotizante  0,genere tome basepensional en cero para el archivo plano)
                MI_CAMPO42:=PCK_SYSMAN_UTL.FC_STRZERO(0,9);
           ELSE
                MI_CAMPO42:=PCK_SYSMAN_UTL.FC_STRZERO(MI_BASEPENSION,9);
          END IF;--(CC:765_CFBARRERA_FIN)  
        ELSIF UN_CLASEREGISTRO IN('UPC') THEN
            IF PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12','19')  THEN
                MI_CAMPO42:=PCK_SYSMAN_UTL.FC_STRZERO(MI_BASEPENSION,9);
            END IF;
        ELSIF PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('17') AND GL_FSPPLANILLA <> 0 THEN
            MI_CAMPO42:=PCK_SYSMAN_UTL.FC_STRZERO(MI_BASEPENSION,9);
        END IF;
    ELSE
        MI_CAMPO42:=PCK_SYSMAN_UTL.FC_STRZERO(MI_BASEPENSION,9);
    END IF;
    --(APINEDA:02/09/2019)-Para los empleados SENA etapa lectiva y productiva no aplica base pensiÃ³n TAR1000094026 
    --(GPORTILLA:31/01/2022)-7721354_NOMINA se adiciona validacion del tipo de vinculacion 23, ya que este campo no aplica para estudiantes del SENA
    IF PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12','19','23') THEN
        MI_CAMPO42 := PCK_SYSMAN_UTL.FC_STRZERO(0,9);
    END IF;

    --(GPORTILLA:31/01/2022)-7721354_NOMINA se adiciona validacion del tipo de vinculacion 23, ya que el campo 43 no aplica para estudiantes del SENA
    MI_PART1 := MI_PART1
                /*CAMPO42*/ || MI_CAMPO42
                /*CAMPO43*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('23') AND UN_TIPOPLANILLA = 'K'
                                                         THEN '0'
                										 WHEN UN_CLASEREGISTRO IN('NOV', 'NOVF')
                                                         THEN GL_BASESALUDP
                                                         ELSE GL_BASESALUD
                                                            + FC_SUMARORSTAR1PESO(UN_VALOR1 => GL_SUELDO
                                                                                 ,UN_VALOR2 => GL_BASESALUD)
                                                         END
                               ,9)
                /*CAMPO44*/ || PCK_SYSMAN_UTL.FC_STRZERO(--(APINEDA:02/09/2019)-Cuando el empleado es SENA y se encuentra en etapa lectiva no aplica base de riesgos TAR 1000094026
                                                         CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12') THEN 0
                                                         ELSE
                                                             CASE WHEN UN_CLASEREGISTRO IN('NOV', 'NOVF')
                                                             THEN GL_BASERIESGOSP
                                                             ELSE CASE WHEN UN_CLASEREGISTRO IN('LNF') OR (GL_BASESALUD > 0 AND UN_CLASEREGISTRO IN('COM')) --MOD JM CC_2005 10/07/2025
                                                                  THEN GL_BASESALUD
                                                                      + FC_SUMARORSTAR1PESO(UN_VALOR1 => GL_SUELDO
                                                                                           ,UN_VALOR2 => GL_BASESALUD)
                                                                  ELSE  CASE WHEN PCK_NOMINA.FC_CNA(339) >= 30  --(MZANGUNA:25/04/2019)-Se agrega Case para comisiones de 30 dÂ¿.
                                                                        THEN PCK_NOMINA_COM5.CSOI(UN_I).BASE
                                                                             + FC_SUMARORSTAR1PESO(UN_VALOR1 => GL_SUELDO
                                                                                                  ,UN_VALOR2 => PCK_NOMINA_COM5.CSOI(UN_I).BASE)
                                                                        ELSE PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS
                                                                             + FC_SUMARORSTAR1PESO(UN_VALOR1 => GL_SUELDO
                                                                                                  ,UN_VALOR2 => PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS)
                                                                        END
                                                                  END
                                                             END
                                                         END
                               , 9)
                /*CAMPO45*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN UN_CLASEREGISTRO = 'NOV'
                                                         THEN GL_BASECAJASP
                                                         ELSE CASE WHEN UN_CLASEREGISTRO = 'LNF'
                                                              THEN 0
                                                              ELSE CASE WHEN UN_CLASEREGISTRO = 'COM'
                                                                   THEN
                                                                       CASE WHEN GL_COMISION30  --(MZANGUNA:02/05/2019)-Se agrega condiciÂ¿ara comisiones de 30 dÂ¿.
                                                                       THEN 0
                                                                       ELSE PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => GL_BASESALUD + FC_SUMARORSTAR1PESO(UN_VALOR1 => GL_SUELDO,UN_VALOR2 => GL_BASESALUD)
                                                                                                   ,UN_PRECISION  => PCK_NOMINA.GL_RBASE1990)
                                                                       END
                                                                   ELSE PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR      => PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL
                                                                                         + FC_SUMARORSTAR1PESO(UN_VALOR1 => GL_SUELDO
                                                                                                              ,UN_VALOR2 => PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL)
                                                                                         ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990)
                                                                  END
                                                             END
                                                         END
                               , 9);

    --CAMPO46
    IF UN_CLASEREGISTRO = 'LNF' AND GL_APORTEPENSIONENLNRSOLOPATR= 'SI' THEN
        MI_CAMPO46 := TRIM(TO_CHAR(NVL(PCK_PARENTR.PARAMETRO35 / 100, 0), '0.00000'));
    ELSIF UN_TIPOPLANILLA='T' THEN
        MI_CAMPO46 := TRIM(TO_CHAR(NVL(PCK_PARENTR.PARAMETRO36 / 100, 0), '0.00000'));
    ELSIF UN_NUEVATABLA <> 0 THEN   --(MZANGUNA:10/12/2018)-Tome los porcentajes desde la tabla BASESNOVEDADES
        MI_CAMPO46 := TRIM(TO_CHAR(NVL(GL_PORCPENSION / 100, 0), '0.00000'));
        --TICKET 7739484: PORCENTAJE DE PENSION ACTIVIDADES ALTO RIESGO
        IF ( PCK_NOMINA_COM5.CSOI(UN_I).ACTIVIDADES_ALTO_RIESGO IS NOT NULL ) THEN
            MI_VALAUX := GL_PORCPENSION + TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PORCENTAJE ADICIONAL PENSION ALTO RIESGO 1',PCK_DATOS.FC_MODULONOMINA,SYSDATE),0));
            MI_CAMPO46 := TRIM(TO_CHAR(NVL(MI_VALAUX / 100, 0), '0.00000'));
        END IF;
        --TICKET 7739484 FIN --
    ELSE
        MI_CAMPO46 := TRIM(TO_CHAR(NVL(PCK_PARENTR.PARAMETRO39 / 100, 0), '0.00000'));
        /*MROSERO CC_1028 30/04/2025*/
        IF UN_CLASEREGISTRO='COM'THEN
          MI_CAMPO46:= '0.00000' ;
       END IF; 
    END IF;

    IF PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12','17','19', '23')
    --OR PCK_NOMINA_COM5.CSOI(UN_I).SUBTIPOCOTIZANTE IN('2','6')
    OR PCK_NOMINA_COM5.CSOI(UN_I).PENSION = 0 THEN
        MI_CAMPO46 := '0.00000' ;
    --(APINEDA:20/11/2019)-Se corrige asignaciÃ³n de valor de 9 caracteres sobre variable con longitud de 7 CAMPO46.
    ELSIF PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_TIPO=95 THEN
       --MI_CAMPO46 := PCK_SYSMAN_UTL.FC_STRZERO(GL_PESPECIAL_PENSION,7); RMEDINA 21/11/2019 para que tome el porcentaje de pension de bomberos para que lo tome el separador (.) e imprima el la columna  correcta del plano
          MI_CAMPO46 :=TRIM(TO_CHAR(NVL( GL_PESPECIAL_PENSION / 100, 0), '0.00000'));
    END IF;

    MI_PART1 := MI_PART1  /*CAMPO46*/ || MI_CAMPO46
                /*CAMPO47*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN UN_CLASEREGISTRO IN('LNR')
                                                         THEN CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).PENSION<PCK_NOMINA.FC_CNA(213)
                                                              THEN 0
                                                              ELSE PCK_NOMINA_COM5.CSOI(UN_I).PENSION -PCK_NOMINA.FC_CNA(213)
                                                              END
                                                         ELSE CASE WHEN UN_CLASEREGISTRO IN('LNF', 'COM')
                                                              THEN PCK_NOMINA.FC_CNA(213)
                                                              ELSE CASE WHEN UN_CLASEREGISTRO IN('NOV', 'NOVF')
                                                                   THEN 
                                                                        CASE WHEN (PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE < MI_SALARIOMINIMO AND PCK_PARENTR.PARAMETRO31 =  '860061110' AND UN_CLASEREGISTRO <> 'COM' ) THEN  --JM 05/12/2024
                                                                            PCK_NOMINA_COM5.CSOI(UN_I).PENSION 
                                                                        ELSE
                                                                            GL_PENSIONAP
                                                                        END
                                                                   ELSE PCK_NOMINA_COM5.CSOI(UN_I).PENSION
                                                                   END
                                                              END
                                                         END
                               , 9)
                /*CAMPO48*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN UN_CLASEREGISTRO NOT IN('SGP', 'UPC', 'LNR', 'LNF', 'COM')
                                                         THEN GL_APVOL
                                                         ELSE 0
                                                         END
                               , 9)
                /*CAMPO49*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9)
                /*CAMPO50*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN UN_CLASEREGISTRO = 'NOV'
                                                         THEN GL_PENSIONAP
                                                         ELSE CASE WHEN UN_CLASEREGISTRO = 'NOVF'
                                                              THEN 
                                                                  CASE WHEN (PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE < MI_SALARIOMINIMO AND PCK_PARENTR.PARAMETRO31 =  '860061110' AND UN_CLASEREGISTRO <> 'COM' ) THEN  --JM 05/12/2024
                                                                            PCK_NOMINA_COM5.CSOI(UN_I).PENSION + GL_APVOL
                                                                        ELSE 
                                                                            GL_PENSIONAP + GL_APVOL 
                                                                  END
                                                              ELSE CASE WHEN UN_CLASEREGISTRO IN('LNR')
                                                                   THEN CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).PENSION<PCK_NOMINA.FC_CNA(213)
                                                                        THEN 0
                                                                        ELSE PCK_NOMINA_COM5.CSOI(UN_I).PENSION - PCK_NOMINA.FC_CNA(213)
                                                                        END
                                                                   ELSE CASE WHEN UN_CLASEREGISTRO IN('LNF', 'COM')
                                                                        THEN PCK_NOMINA.FC_CNA(213)
                                                                        ELSE CASE WHEN UN_CLASEREGISTRO IN('SGP', 'UPC')
                                                                             THEN PCK_NOMINA_COM5.CSOI(UN_I).PENSION
                                                                             ELSE PCK_NOMINA_COM5.CSOI(UN_I).PENSION
                                                                                + GL_APVOL
                                                                             END
                                                                        END
                                                                   END
                                                              END
                                                         END
                               , 9)
                /*CAMPO51*/ || PCK_SYSMAN_UTL.FC_STRZERO(GL_FSPPLANILLA, 9)
                /*CAMPO52*/ || PCK_SYSMAN_UTL.FC_STRZERO(GL_FSPADICPLANILLA, 9)
                /*CAMPO53*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN UN_CLASEREGISTRO IN('SGP', 'UPC', 'LNR', 'LNF', 'COM')
                                                         THEN 0
                                                         ELSE GL_BENEFICIOT
                                                         END
                                                        , 9);

    /*CAMPO54*/
    IF UN_CLASEREGISTRO ='COM' THEN
        IF GL_NSLN = 'C' THEN
            MI_CAMPO54 := '0.00000';
        ELSE
            MI_CAMPO54 := '0.08500';
        END IF;
    ELSIF UN_CLASEREGISTRO ='LNF' THEN
        IF PCK_NOMINA.FC_CNA(206) = 0 OR UN_TIPOPLANILLA ='T' THEN
            MI_CAMPO54 := '0.00000';
        ELSE
            MI_CAMPO54 := '0.08500';
        END IF;
    ELSIF UN_CLASEREGISTRO ='LNR' AND UN_TIPOPLANILLA ='T' THEN
        MI_CAMPO54 := RPAD('0.04000',7,' ');
    ELSIF GL_EXONERACIONSALUD ='SI' AND
        GL_EXONERADO AND
        PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12', '19', '23') THEN
			--Ticket_7702419(14/07/2022 jcrojas): Se ajusta valor del campo 54 en el plano para que tome el dato configurado en el parametro PORCENTAJE APORTE SALUD SENA para los empleados Sena.
			MI_CAMPO54 := TRIM(TO_CHAR(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
																	,UN_NOMBRE    => 'PORCENTAJE APORTE SALUD SENA'
																	,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
																	,UN_FECHA_PAR => SYSDATE) 
						  / 100, 0), '0.00000'));																													  
            --MI_CAMPO54:=TRIM(TO_CHAR(NVL(PCK_PARENTR.PARAMETRO41 / 100,0)/POWER(10, LENGTH(TRUNC(NVL(PCK_PARENTR.PARAMETRO41 / 100,0)))),'0.00000'));
			IF PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('23') THEN
               MI_CAMPO54 :='0.00000';
            END IF;															   							 
    ELSIF UN_NUEVATABLA <> 0 THEN   --(MZANGUNA:10/12/2018)-Tome los porcentajes desde la tabla BASESNOVEDADES
		IF PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12', '19', '23') THEN
			MI_CAMPO54 := TRIM(TO_CHAR(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
																	,UN_NOMBRE    => 'PORCENTAJE APORTE SALUD SENA'
																	,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
																	,UN_FECHA_PAR => SYSDATE) 
						  / 100, 0), '0.00000'));
        ELSE																		   
			MI_CAMPO54 := TRIM(TO_CHAR(NVL(GL_PORCSALUD / 100, 0), '0.00000'));
        END IF;																						   
    ELSE
        MI_CAMPO54 := TRIM(TO_CHAR(NVL(CASE WHEN PCK_NOMINA.GL_CALCULARSALUDCONDECRETO1122 = 'SI'
                                      THEN NVL(PCK_NOMINA.GL_PORCSALUDCONDECRETO1122,0)/POWER(10, LENGTH(TRUNC(NVL(PCK_NOMINA.GL_PORCSALUDCONDECRETO1122,0))))
                                      ELSE NVL(PCK_PARENTR.PARAMETRO43 / 100,0)/POWER(10, LENGTH(TRUNC(NVL(PCK_PARENTR.PARAMETRO43 / 100,0))))
                                      END
                                    ,0)
                        , '0.00000'));
    END IF;

    MI_PART1 := MI_PART1  /*CAMPO54*/ || MI_CAMPO54
                /*CAMPO55*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN UN_TIPOPLANILLA <>'K'
                                                         THEN CASE WHEN UN_CLASEREGISTRO IN('NOV', 'NOVF')
                                                              THEN 
                                                                CASE WHEN (PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE < MI_SALARIOMINIMO AND PCK_PARENTR.PARAMETRO31 =  '860061110' AND UN_CLASEREGISTRO <> 'COM' ) THEN  --JM 05/12/2024
                                                                        PCK_NOMINA_COM5.CSOI(UN_I).SALUD 
                                                                    ELSE 
                                                                        GL_SALUDAP 
                                                                END
                                                              ELSE CASE WHEN UN_CLASEREGISTRO IN('LNF', 'COM')
                                                                   THEN PCK_NOMINA.FC_CNA(206)
                                                                   ELSE PCK_NOMINA_COM5.CSOI(UN_I).SALUD
                                                                       + CASE WHEN UN_CLASEREGISTRO ='LNR'
                                                                         THEN PCK_NOMINA.FC_CNA(206)
                                                                         ELSE 0
                                                                         END
                                                                   END
                                                              END
                                                         ELSE 0
                                                         END
                               , 9)
                /*CAMPO56*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN UN_CLASEREGISTRO IN('UPC', 'LNF', 'COM')
                                                         THEN 0
                                                         ELSE CASE WHEN  PCK_NOMINA_COM5.GL_UPCPATRONO ='SI' THEN PCK_NOMINA_COM5.CSOI(UN_I).UPCPATRONO ELSE PCK_NOMINA_COM5.CSOI(UN_I).UPC END
                                                         END
                               , 9)
                /*CAMPO57*/ || RPAD(CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).NUM_AUTO_INCAP IS NULL
                                           OR UN_TIPOPLANILLA = 'K'
                                    THEN ' '
                                    ELSE PCK_NOMINA_COM5.CSOI(UN_I).NUM_AUTO_INCAP END
                               , 15, ' ')
                /*CAMPO58*/ || PCK_SYSMAN_UTL.FC_STRZERO(
                                  CASE WHEN UN_TIPOPLANILLA = 'K'
                                  THEN 0
                                  ELSE NVL(PCK_NOMINA_COM5.CSOI(UN_I).VR_INCAP, 0)
                                  END
                               , 9)
                /*CAMPO59*/ || RPAD(CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).NUM_AUTO_MAT IS NULL
                                           OR UN_TIPOPLANILLA = 'K'
                                           OR UN_CLASEREGISTRO IN('LNF')
                                   THEN ' '
                                   ELSE PCK_NOMINA_COM5.CSOI(UN_I).NUM_AUTO_MAT
                                   END
                               , 15,' ')
                /*CAMPO60*/ || PCK_SYSMAN_UTL.FC_STRZERO(
                                  CASE WHEN UN_TIPOPLANILLA = 'K'
                                         OR UN_CLASEREGISTRO IN('LNF', 'COM')
                                  THEN 0
                                  ELSE NVL(PCK_NOMINA_COM5.CSOI(UN_I).VR_MAT, 0)
                                  END
                               , 9);

    --MI_CAMPO61
    IF (CASE WHEN UN_CLASEREGISTRO = 'NOV' THEN GL_CTARP = 0 ELSE GL_CTAR  = 0 END OR PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION = '12')
    OR (PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS = 0 AND GL_NIRP <>'00'   AND NOT GL_OBLIGAR)    THEN
        MI_CAMPO61 :='0.0000000';
    ELSIF UN_CLASEREGISTRO = 'NOV' THEN
        MI_CAMPO61 := TRIM(TO_CHAR(NVL(GL_CTARP, 0), '0.0000000'));
    ELSE
        MI_CAMPO61 := TRIM(TO_CHAR(NVL(GL_CTAR, 0), '0.0000000'));
    END IF;

    MI_PART1 := MI_PART1  /*CAMPO61*/ || MI_CAMPO61
                /*CAMPO62*/ || PCK_SYSMAN_UTL.FC_STRZERO(
                                CASE WHEN UN_CLASEREGISTRO IN ('COM')
                                THEN 1
                                ELSE  CASE WHEN GL_PLANOSUCURIESGO = 'SI'
                                            AND UN_CLASEREGISTRO NOT IN ('LNF')
                                      THEN
                                      --(APINEDA:17/07/2019)-Se agregan parametros a llamado de funciÃ³n FC_SUCURSALRIESGON
                                      PCK_NOMINA_COM5.FC_SUCURSALRIESGON(UN_COMPANIA   => UN_COMPANIA
                                                                            ,UN_EMPLEADO   => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
                                                                            ,UN_NUMEROPATRONAL => PCK_NOMINA_COM5.CSOI(UN_I).NUMEROPATRONAL
                                                                            ,UN_FACTOR_RIESGO => PCK_NOMINA_COM5.CSOI(UN_I).FACTOR_RIESGO
                                                                            ,UN_PAR        => 0)
                                      ELSE 0
                                      END
                                END
                                ,9)
                /*CAMPO63*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN UN_CLASEREGISTRO ='NOV' AND GL_INCAPACIDAD30 THEN --JM 31/07/2024 7741502 --MOD JM CC 2121 (regalo de mi mismo para mi mismo, con la nueva variable GL_INCAPACIDAD30 soluciono todo) 
                                                          GL_RIESGOSAP
                                                         ELSE CASE WHEN UN_CLASEREGISTRO IN ('LNF', 'COM') OR PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD IN (1,2,3)--CFBARRERA CC:767_11/02/2025
                                                              THEN 0
                                                              ELSE PCK_NOMINA_COM5.CSOI(UN_I).RIESGOS
                                                              END
                                                         END
                               , 9)
                /*CAMPO64*/ || FC_CAMPOPARAFISCAL(UN_I             => UN_I
                                                 ,UN_TIPO          => 'CAJAS'
                                                 ,UN_CLASEREGISTRO => UN_CLASEREGISTRO
                                                 ,UN_TIPOPLANILLA => UN_TIPOPLANILLA
                                                 )
                /*CAMPO65*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN UN_CLASEREGISTRO ='NOV' AND GL_INCAPACIDAD30 THEN --JM 31/07/2024 7741502 --MOD JM CC 2121 (regalo de mi mismo para mi mismo, con la nueva variable GL_INCAPACIDAD30 soluciono todo) 
                                                          GL_CN101AP
                                                         ELSE CASE WHEN UN_CLASEREGISTRO IN ('LNF', 'COM') OR PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD IN (1,2,3)--CFBARRERA CC:767_11/02/2025
                                                              THEN 0
                                                              ELSE PCK_NOMINA_COM5.CSOI(UN_I).CAJAS
                                                              END
                                                         END
                               , 9)
                /*CAMPO66*/ || FC_CAMPOPARAFISCAL(UN_I             => UN_I
                                                 ,UN_TIPO          => 'SENA'
                                                 ,UN_CLASEREGISTRO => UN_CLASEREGISTRO
                                                 ,UN_TIPOPLANILLA => UN_TIPOPLANILLA
                                                 )
                /*CAMPO67*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN UN_CLASEREGISTRO IN('NOV', 'NOVF') AND GL_INCAPACIDAD30 THEN --JM 31/07/2024 7741502 --MOD JM CC 2121 (regalo de mi mismo para mi mismo, con la nueva variable GL_INCAPACIDAD30 soluciono todo) 
                                                          GL_CN103AP
                                                         ELSE CASE WHEN UN_CLASEREGISTRO  IN ('LNF', 'COM') OR PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD IN (1,2,3)--CFBARRERA CC:767_11/02/2025
                                                              THEN 0
                                                              ELSE PCK_NOMINA_COM5.CSOI(UN_I).SENA
                                                              END
                                                         END
                               , 9)
                /*CAMPO68*/ || FC_CAMPOPARAFISCAL(UN_I             => UN_I
                                                 ,UN_TIPO          => 'ICBF'
                                                 ,UN_CLASEREGISTRO => UN_CLASEREGISTRO
                                                 ,UN_TIPOPLANILLA => UN_TIPOPLANILLA
                                                 )
                /*CAMPO69*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN UN_CLASEREGISTRO IN('NOV', 'NOVF') AND GL_INCAPACIDAD30 THEN --JM 31/07/2024 7741502 --MOD JM CC 2121 (regalo de mi mismo para mi mismo, con la nueva variable GL_INCAPACIDAD30 soluciono todo) 
                                                          GL_CN102AP
                                                         ELSE CASE WHEN UN_CLASEREGISTRO  IN ('LNF', 'COM') OR PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD IN (1,2,3)--CFBARRERA CC:767_11/02/2025
                                                              THEN 0
                                                              ELSE PCK_NOMINA_COM5.CSOI(UN_I).ICBF
                                                              END
                                                         END
                               , 9)
                /*CAMPO70*/ || FC_CAMPOPARAFISCAL(UN_I             => UN_I
                                                 ,UN_TIPO          => 'ESAP'
                                                 ,UN_CLASEREGISTRO => UN_CLASEREGISTRO
                                                 ,UN_TIPOPLANILLA => UN_TIPOPLANILLA
                                                 )
                /*CAMPO71*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN UN_CLASEREGISTRO IN('NOV', 'NOVF') AND GL_INCAPACIDAD30 THEN --JM 31/07/2024 7741502 --MOD JM CC 2121 (regalo de mi mismo para mi mismo, con la nueva variable GL_INCAPACIDAD30 soluciono todo) 
                                                          GL_CN104AP
                                                         ELSE CASE WHEN UN_CLASEREGISTRO  IN ('LNF', 'COM') OR PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD IN (1,2,3)--CFBARRERA CC:767_11/02/2025
                                                              THEN 0
                                                              ELSE PCK_NOMINA_COM5.CSOI(UN_I).ESAP
                                                              END
                                                         END
                               , 9)
                /*CAMPO72*/ || FC_CAMPOPARAFISCAL(UN_I             => UN_I
                                                 ,UN_TIPO          => 'INSTITUTOS'
                                                 ,UN_CLASEREGISTRO => UN_CLASEREGISTRO
                                                 ,UN_TIPOPLANILLA => UN_TIPOPLANILLA
                                                 )
                /*CAMPO73*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN UN_CLASEREGISTRO IN('NOV', 'NOVF') AND GL_INCAPACIDAD30 THEN --JM 31/07/2024 7741502 --MOD JM CC 2121 (regalo de mi mismo para mi mismo, con la nueva variable GL_INCAPACIDAD30 soluciono todo) 
                                                          GL_CN105AP
                                                         ELSE CASE WHEN UN_CLASEREGISTRO  IN ('LNF', 'COM') OR PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD IN (1,2,3)--CFBARRERA CC:767_11/02/2025
                                                              THEN 0
                                                              ELSE PCK_NOMINA_COM5.CSOI(UN_I).INSTITUTOS
                                                              END
                                                         END
                               , 9)
                /*CAMPO74*/ || RPAD(' ', 2,' ')
                /*CAMPO75*/ || RPAD(' ', 16,' ')
                /*CAMPO76*/ || CASE WHEN UN_CLASEREGISTRO IN('LNR','LNF') AND UN_TIPOPLANILLA ='T'
                               THEN ''
                               ELSE CASE WHEN GL_EXONERADO
                                          AND PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION NOT IN('12', '19', '23', '51')
                                    THEN  'S' ELSE 'N' END
                               END
                /*CAMPO77*/ || CASE WHEN (PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_TIPO = '95'
                                      OR PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12'))
                                      AND PCK_NOMINA.GL_OPERADOR NOT IN('APORTESENLINEA')
                               THEN
                                   --(APINEDA:02/09/2019)-Se modifica porque estaba enviando por defecto 1, cuando el tipo de vinculaciÃ³n es SENA etapa lectiva lo cual genera error al subir el plano TAR1000094026 
                                   RPAD(' ', 6,' ')
                               ELSE GL_CODIGOFONDORIESGOS
                               END
                /*CAMPO78*/ || LPAD(CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_TIPO = '95'
                                          OR PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12')
                                          AND PCK_NOMINA.GL_OPERADOR NOT IN('APORTESENLINEA')
                                   THEN ' '
                                   ELSE 
                                   --(APINEDA:17/07/2019)-Se agregan parametros a llamado de funciÃ³n FC_SUCURSALRIESGON para el campo 78
                                   PCK_NOMINA_COM5.FC_SUCURSALRIESGON(UN_COMPANIA   => UN_COMPANIA
                                                                      ,UN_EMPLEADO   => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
                                                                      ,UN_NUMEROPATRONAL => PCK_NOMINA_COM5.CSOI(UN_I).NUMEROPATRONAL
                                                                      ,UN_FACTOR_RIESGO => PCK_NOMINA_COM5.CSOI(UN_I).FACTOR_RIESGO
                                                                      ,UN_PAR        => 1)                                                                       
                                   END
                               ,1, ' ')
                /*CAMPO79*/ || RPAD(NVL(PCK_NOMINA_COM5.CSOI(UN_I).ACTIVIDADES_ALTO_RIESGO,' '),1,' ')
                            || UN_CAMPOSNUEVOS2388
                /*CAMPO98*/ || MI_ACTECOCIIU            
                            ;

    RETURN MI_PART1;
END FC_REGISTROPLANO;




PROCEDURE PR_INICIARPARAMETROSPLANOSOI
    /*
    NAME              : Nuevo para unificar el llamado de parametros una sola vez
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : JOSE PASCUAL GOMEZ
    DATE MIGRATION    : 03/01/2018
    TIME              : 09:11 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    @NAME:   
  */
    (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
    )
AS
  MI_PARAMETRO    PCK_SUBTIPOS.TI_PARAMETRO;
  MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
  MI_PARAMETRO := 'MANEJA SISTEMA GENERAL DE PARTICIPACIONES PILA';
  IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA 
                          ,UN_NOMBRE    => MI_PARAMETRO
                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                          ,UN_FECHA_PAR => SYSDATE) = 'SI' THEN
    PCK_NOMINA.GL_MANSGP := TRUE;
  ELSE
    PCK_NOMINA.GL_MANSGP := FALSE;
  END IF;
  MI_PARAMETRO := 'DECIMALES REDONDEO BASE DECRETO 1990';
  PCK_NOMINA.GL_RBASE1990 := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                       ,UN_NOMBRE    => MI_PARAMETRO
                                       ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                       ,UN_FECHA_PAR => SYSDATE),0));
  MI_PARAMETRO := 'DECIMALES REDONDEO APORTES DECRETO 1990';
  PCK_NOMINA.GL_RAPORTES1990 := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                       ,UN_NOMBRE    => MI_PARAMETRO
                                       ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                       ,UN_FECHA_PAR => SYSDATE),0));                                     
  MI_PARAMETRO := 'SUMAR A SS DECRETO 1990';
  PCK_NOMINA.GL_SUMARSS1990 := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                         ,UN_NOMBRE    => MI_PARAMETRO
                                         ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                         ,UN_FECHA_PAR => SYSDATE),0));
	MI_PARAMETRO := 'DECIMALES REDONDEO MINIMO DECRETO 1990';
  PCK_NOMINA.GL_RMINIMO1990 := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),0));
  MI_PARAMETRO := 'HORAS TRABAJADAS MENSUALES';
  PCK_NOMINA.GL_HORASMENSUALES := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),240));
  MI_PARAMETRO := 'DIAS TRABAJADOS SEMANALES';
  PCK_NOMINA.GL_DIASSEMANALES := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),5));
  MI_PARAMETRO := 'TIPO DE NOMINA ACTIVOS O PENSIONADOS';                                        
  PCK_NOMINA.GL_TIPONOMINA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE);
  MI_PARAMETRO := 'CALCULAR FSP Y SUBSISTENCIA DECRETO 1317/2006';
  PCK_NOMINA.GL_CALCULAFSP := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                        ,UN_NOMBRE    => MI_PARAMETRO
                                        ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                        ,UN_FECHA_PAR => SYSDATE);
  MI_PARAMETRO := 'GENERAR REGISTROS ADICIONALES POR UPC';
  PCK_NOMINA.GL_PAGOUPC := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                         ,UN_NOMBRE    => MI_PARAMETRO
                                         ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                         ,UN_FECHA_PAR => SYSDATE),'NO');
  MI_PARAMETRO := 'GENERA DIAS PARAFISCALES IGUAL A LABORADOS';
  PCK_NOMINA.GL_DIASIGUALPARA := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                               ,UN_NOMBRE    => MI_PARAMETRO
                                               ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                               ,UN_FECHA_PAR => SYSDATE),'NO');
  MI_PARAMETRO := 'CALCULAR SALUD CON DECRETO 1122';
  PCK_NOMINA.GL_CALCULARSALUDCONDECRETO1122 := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'NO');
  MI_PARAMETRO := 'PORCENTAJE SALUD DECRETO 1122';
  PCK_NOMINA.GL_PORCSALUDCONDECRETO1122 := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'0'));
  MI_PARAMETRO := 'OBLIGA GENERAR PORCENTAJE ARP SIN APORTES';
  PCK_NOMINA.GL_OBLIGAPORARP := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'NO');                                        
  MI_PARAMETRO := 'APLICAR EXONERACION APORTES SALUD PATRONO 8.5%';
  GL_EXONERACIONSALUD:=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, 
                                              UN_NOMBRE   => MI_PARAMETRO, 
                                              UN_MODULO   => PCK_DATOS.FC_MODULONOMINA, 
                                              UN_FECHA_PAR=> SYSDATE);

  MI_PARAMETRO := 'PAGAR APORTES VOLUNTARIOS EN PLANILLA PILA';
  GL_PAGARAPORTESVOLUNPILA:= PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, 
                                               UN_NOMBRE   => MI_PARAMETRO, 
                                               UN_MODULO   => PCK_DATOS.FC_MODULONOMINA, 
                                               UN_FECHA_PAR=> SYSDATE);

  --MI_PARAMETRO := 'OPERADOR PARA EL PAGO DE SEGURIDAD SOCIAL';
  MI_PARAMETRO := 'NOMBRE OPERADOR VALIDACION PILA';
  PCK_NOMINA.GL_OPERADOR := UPPER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE));
  MI_PARAMETRO := 'GENERAR PLANO CON SUCURSAL DE RIESGO DE PERSONAL';
  GL_PLANOSUCURIESGO := UPPER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE));

  MI_PARAMETRO       := 'GENERAR FSP EN CADA REGISTRO PILA';
  GL_PLANOFSPPILA    := UPPER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE));
  /*
  MI_PARAMETRO       := 'GENERAR REGISTROS ADICIONALES POR UPC';
  GL_REGISTROADIUPC  := UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'NO'));    
  */
  MI_PARAMETRO       := 'UPC ADICIONAL PILA REGISTRO INDEPENDIENTE';
  GL_UPCADICIONALPILA := UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'NO')); 

  MI_PARAMETRO       := 'PORCENTAJE APORTES PENSION BOMBEROS';
  GL_APORTEPENSIONBOMBERO := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'16'));    
  MI_PARAMETRO       := 'GENERAR NOVEDAD DE IGE CON DIAS DE ARL';
  GL_GENERANOVIGECONDIASARL := UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'NO'));  
  MI_PARAMETRO      := 'DIAS A RECONOCER EN INCAPACIDAD INICIAL';
  GL_DIASARECONINCAPAINI := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                    ,UN_NOMBRE    => MI_PARAMETRO
                                    ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                    ,UN_FECHA_PAR => SYSDATE),'2')); 
  MI_PARAMETRO      := 'LIMITE EXONERAR PARAFISCALES';
  GL_LIMITEEXONERARPARA := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                    ,UN_NOMBRE    => MI_PARAMETRO
                                    ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                    ,UN_FECHA_PAR => SYSDATE),'0')); 
  MI_PARAMETRO       := 'SUMAR DIAS VACACIONES A DIAS PARAFISCALES';
  GL_SUMARDIASVACAAPARAFISCALES := UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'NO'));  
  MI_PARAMETRO       := 'REALIZAR APORTES PENSION EN LNR';
  GL_APORTEPENSIONENLNR := UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'SI'));  
  MI_PARAMETRO       := 'REALIZAR APORTES PENSION EN LNR SOLO PATRONAL';
  GL_APORTEPENSIONENLNRSOLOPATR := UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'NO'));  
  MI_PARAMETRO       := 'GENERAR ARCHIVO PLANO REGISTROS 3/6/7/8/9/10/11';
  GL_GENERAREGISTRO367891011 := UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'NO'));  
  MI_PARAMETRO       := 'TIPO DE PAGADOR DE PENSIONES';
  GL_TIPOPAGADOR := UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'1'));
  MI_PARAMETRO       := 'NOMINA MENSUAL';
  PCK_NOMINA.GL_NOMINAMENSUAL := UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'SI'));

  MI_PARAMETRO       := 'GENERAR IBC PARAFISCALES REGISTRO VACACIONES';
  GL_GENERAPARAFISCALESENVACA := UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'SI'));
  MI_PARAMETRO       := 'GENERAR IBC OTROS PARAFISCALES VACACIONES';
  GL_GENERAOTPARAFISCALESENVACA := UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'SI')); 
  --(APINEDA:13/05/2019)-Se obtiene parametro que contiene concepto configurado para almacenar aporte del pensionado a caja de compensaciÂ¿                                       
  MI_PARAMETRO       := 'CODIGO CONCEPTO APORTE PENSIONADO A CAJA COMPENSACION';                                          
  GL_CN_APORTECCFPENSION        := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'0'));                                            
  MI_PARAMETRO       := 'APORTE UPC PATRONO';
  GL_UPCPATRONO := UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                                          ,UN_NOMBRE    => MI_PARAMETRO
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                          ,UN_FECHA_PAR => SYSDATE),'NO')); 
    
    -- TICKET 7722577 ECABRERA: ADICION CAMPO 98 ACTIVIDAD ECONOMICA CIIU AL ARCHIVO PLANO                                        
   	MI_PARAMETRO       := 'APLICA RES 2012 DE 2022 EN ARCHIVO PLANO';
    IF UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA  
                      ,UN_NOMBRE    => MI_PARAMETRO
                      ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                      ,UN_FECHA_PAR => SYSDATE),'NO')) = 'SI' THEN 
        GL_CIIU2012 := TRUE;
    ELSE 
        GL_CIIU2012 := FALSE;
    END IF;   
    -- TICKET 7722577 ECABRERA                                      
                                          
EXCEPTION WHEN OTHERS THEN
  MI_MSGERROR(1).CLAVE := 'PARAMETRO';
  MI_MSGERROR(1).VALOR := MI_PARAMETRO;
  PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>-20000,
        UN_ERROR_COD=>PCK_ERRORES.ERR_INICIARPARAMETROSOI,
        UN_TABLAERROR =>'PR_INICIARPARAMETROSPLANOSOI',
        UN_REEMPLAZOS => MI_MSGERROR
      ); 
END PR_INICIARPARAMETROSPLANOSOI;


FUNCTION FC_GENERAR_NOVEDADES_PILA_2388
    /*
    NAME              : GENERAR_NOVEDADES_PILA_2388
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : JOSE PASCUAL GOMEZ
    DATE MIGRATION    : 03/01/2018
    TIME              : 09:11 AM
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:
  */
    --UN_I es la posiciÂ¿el vector para consultar PCK_NOMINA_COM5.CSOI;
    --     que se debe enviar desde la funciÂ¿R_SISTEMAINTEGRADOELECTRONICO
    (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_I              IN NUMBER,
    UN_PROCESO        IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES            IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO        IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_TIPOESTRUCTURA IN VARCHAR2,
    UN_TIPOPLANILLA   IN VARCHAR2,
    UN_RETROACTIVO    IN PCK_SUBTIPOS.TI_LOGICO,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
    )
RETURN PCK_SUBTIPOS.TI_ENTERO
AS
  MI_RETORNO PCK_SUBTIPOS.TI_ENTERO;
  MI_FFINAL  DATE;
  MI_FINICIO DATE;
  MI_FFINALVAR  VARCHAR2(10);
  MI_FINICIOVAR VARCHAR2(10);
  MI_CANTENC          PCK_SUBTIPOS.TI_ENTERO;
  MI_PRIMERREG        PCK_SUBTIPOS.TI_ENTERO;
  MI_IBCMESANTERIOR   PCK_SUBTIPOS.TI_DOBLE;
  MI_TDIAS            PCK_SUBTIPOS.TI_ENTERO;
  MI_DD               PCK_SUBTIPOS.TI_ENTERO;
  MI_BASEPROPORCIONAL PCK_SUBTIPOS.TI_DOBLE;
  MI_BASEPROPIIF      PCK_SUBTIPOS.TI_DOBLE;
  MI_TIPOINCAP        VARCHAR2(30);
  MI_TEMP             PCK_SUBTIPOS.TI_DOBLE;
  MI_NING               VARCHAR2(1);
  MI_NRET               VARCHAR2(1);
  MI_NTDA               VARCHAR2(1);
  MI_NTAA               VARCHAR2(1);
  MI_NTDAP              VARCHAR2(1);
  MI_NTAAP              VARCHAR2(1);
  MI_NVSP               VARCHAR2(1);
  MI_VTE                VARCHAR2(1);
  MI_NVST               VARCHAR2(1);
  MI_NSLN               VARCHAR2(1);
  MI_NIGE               VARCHAR2(1);
  MI_NLMA               VARCHAR2(1);
  MI_NVAC               VARCHAR2(1);
  MI_NAVP               VARCHAR2(1);
  MI_NVCT               VARCHAR2(1);
  MI_NIRP               VARCHAR2(2);

  MI_PORCSALUD          NUMBER;
  MI_PORCPENSION        NUMBER;
  MI_CAMPOSNUEVOS2388N  CLOB;
  MI_TOTALBASEPARAFISCALN NUMBER;
  MI_TOTALBASEPENSIONN    NUMBER;
  MI_TOTALBASESALUDN      NUMBER;
  MI_SUMAAPORTESPENSIONN  NUMBER;
  MI_SUMAAPORTESSALUDN    NUMBER;
  MI_SUMAAPORTESRIESGOSN  NUMBER;
  MI_SUMAAPORTESFSPN      NUMBER;
  MI_APORTEPARA           NUMBER;
  MI_TOTDIASNOV           NUMBER;
  MI_DIASTRABAJADOS       NUMBER;
  MI_AC                   NUMBER:=0;
  MI_PARAMETRO1990APLICA          PARAMETRO.VALOR%TYPE;
BEGIN
  MI_PARAMETRO1990APLICA := UPPER(PCK_PARST.FC_PAR('APLICAR NUEVO REDONDEO DECRETO 1990 FC_ROUND_100', 'NO')) ;
  MI_RETORNO :=0;
  MI_CANTENC :=0;
  --se guardan los datos en variables de tal forma que cuando salga se pueda reasignen
  MI_NING   := GL_NING   ;
  MI_NRET   := GL_NRET   ;
  MI_NTDA   := GL_NTDA   ;
  MI_NTAA   := GL_NTAA   ;
  MI_NTDAP  := GL_NTDAP  ;
  MI_NTAAP  := GL_NTAAP  ;
  MI_NVSP   := GL_NVSP   ;
  MI_VTE    := GL_VTE    ;
  MI_NVST   := GL_NVST   ;
  MI_NSLN   := GL_NSLN   ;
  MI_NIGE   := GL_NIGE   ;
  MI_NLMA   := GL_NLMA   ;
  MI_NVAC   := GL_NVAC   ;
  MI_NAVP   := GL_NAVP   ;
  MI_NVCT   := GL_NVCT   ;
  MI_NIRP   := GL_NIRP   ;
  MI_TOTALBASEPARAFISCALN := 0;
  MI_TOTALBASEPENSIONN    := 0;
  MI_TOTALBASESALUDN      := 0;
  MI_SUMAAPORTESPENSIONN  := 0;
  MI_SUMAAPORTESSALUDN    := 0;
  MI_SUMAAPORTESRIESGOSN  := 0;
  MI_SUMAAPORTESFSPN      := 0;
  GL_BASECAJASP           := 0;
  MI_TOTDIASNOV           := 0;
  MI_PRIMERREG            := 0;
  MI_IBCMESANTERIOR       := 0;
  MI_FINICIO := TO_DATE('01/' || PCK_SYSMAN_UTL.FC_STRZERO(UN_MES,2) || '/' || PCK_SYSMAN_UTL.FC_STRZERO(UN_ANIO,4),'DD/MM/YYYY');
  MI_FFINAL := LAST_DAY(MI_FINICIO);
  MI_DIASTRABAJADOS := (PCK_NOMINA.FC_CNA(9) +PCK_NOMINA.FC_CNA(11));
  --10/04/2018 Para inicializar
  GL_SUELDOP :=0;
  MI_AC:=PCK_NOMINA.FC_ACUM1(UN_COMPANIA  => UN_COMPANIA,
                              UN_ANO1      => UN_ANIO,
                              UN_MES1      => UN_MES,
                              UN_PERIODO1  => UN_PERIODO,
                              UN_IDEMPLEADO=> PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO);
  <<NOVEDADES>>
  FOR RS IN (
      SELECT FECHA_INICIO  F1,
             FECHA_FIN     F2,
             CASE WHEN INCAPACIDAD='04'
             THEN 'NLMA'
             ELSE CASE WHEN INCAPACIDAD IN('05','09')
                  THEN 'NIRP'
                  ELSE 'NIGE'
                  END
             END NOVEDAD
      FROM INCAPACIDADES
      WHERE COMPANIA       = UN_COMPANIA
        AND ID_DE_EMPLEADO = PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
        AND FECHA_INICIO  <= MI_FFINAL
        AND FECHA_FIN     >= MI_FINICIO
      UNION
      SELECT FECHA_INICIO    F1,
             FECHA_FINAL     F2,
             CASE WHEN LICENCIA IN('01','02','03')
             THEN 'NSLN'
             ELSE CASE WHEN LICENCIA IN('04','05','10','12')
                  THEN 'LREM'
                  ELSE 'COMIS'
                  END
             END  NOVEDAD
      FROM LICENCIAS
      WHERE COMPANIA       = UN_COMPANIA
        AND ID_DE_EMPLEADO = PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
        AND LICENCIA NOT IN('01','02','03')
        AND FECHA_INICIO  <= MI_FFINAL
        AND FECHA_FINAL   >= MI_FINICIO
      UNION
      SELECT INICIO_DISFRUTE F1,
             FINAL_DISFRUTE  F2,
             'NVAC'                     NOVEDAD
      FROM VACACIONES
      WHERE COMPANIA       = UN_COMPANIA
        AND ID_DE_EMPLEADO = PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
        AND DIAS       NOT IN(0)
        AND QUITARPILA     IN(0)
        --AND NOT (TO_NUMBER(TO_CHAR(INICIO_DISFRUTE,'DD')) = 31
        --     AND TO_NUMBER(TO_CHAR(INICIO_DISFRUTE,'MM')) = TO_NUMBER(TO_CHAR(MI_FINICIO,'MM')))
        AND INICIO_DISFRUTE  <= MI_FFINAL
        AND FINAL_DISFRUTE   >= MI_FINICIO
      --nuevo para ver como se maneja las interrupciones
      UNION
      SELECT FECHAINTERRUPCION F1,
       FECHAINTERRUPCION + PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN   => FECHAINTERRUPCION
                                          ,UN_FECHAFIN  => FECHAINTERRUPCION + DIASINTERRUPCION)  F2,
             'NINT'                     NOVEDAD
      FROM INTERRUPCION_VACACIONES
      WHERE COMPANIA       = UN_COMPANIA
        AND ID_DE_EMPLEADO = PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
        AND DIASINTERRUPCION  NOT IN(0)
        AND QUITARPILA     IN(0)
        --AND NOT (TO_NUMBER(TO_CHAR(INICIO_DISFRUTE,'DD')) = 31
        --     AND TO_NUMBER(TO_CHAR(INICIO_DISFRUTE,'MM')) = TO_NUMBER(TO_CHAR(MI_FINICIO,'MM')))
        AND FECHAINTERRUPCION  <= MI_FFINAL
        AND FECHAINTERRUPCION
          + PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN   => FECHAINTERRUPCION
                                              ,UN_FECHAFIN  => FECHAINTERRUPCION + DIASINTERRUPCION)   >= MI_FINICIO
  )
  LOOP
    --NO SE MONTA EL DELETE A HISTORICOS_FONDOS_PILA
    MI_IBCMESANTERIOR := PCK_NOMINA.FC_CNP(348);
    IF MI_IBCMESANTERIOR = 0 THEN
      MI_IBCMESANTERIOR := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC
                                                  ,UN_PRECISION => 0);
    END IF;
    MI_CANTENC := MI_CANTENC + 1;
    MI_TIPOINCAP := PCK_NOMINA_COM5.FC_TIPO_INCAPACIDAD_FECHAS(UN_COMPANIA  => UN_COMPANIA
                                                              ,UN_IDEMPLEADO=> PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
                                                              ,UN_FECHAINI  => RS.F1
                                                              ,UN_FECHAFIN  => RS.F2);

    MI_FINICIO := TO_DATE('01/' || PCK_SYSMAN_UTL.FC_STRZERO(UN_MES,2) || '/' || PCK_SYSMAN_UTL.FC_STRZERO(UN_ANIO,4),'DD/MM/YYYY');
    MI_FFINAL := LAST_DAY(MI_FINICIO);
    --MI_TIPOINCAP := TIPO_Incapacidad_FECHAS(Getcompany(), Empleado, RS.F1, RS.F2, 1);
    IF RS.F1 >= MI_FINICIO AND RS.F1 <= MI_FFINAL THEN
      MI_FINICIO := RS.F1;
    ELSIF RS.F1 <= MI_FINICIO THEN
      MI_FINICIO := MI_FINICIO;
    ELSE
      MI_FINICIO := RS.F1;
    END IF;

    IF RS.F2 >= MI_FINICIO AND RS.F2 <= MI_FFINAL THEN
      MI_FFINAL := RS.F2;
    ELSIF RS.F2 > MI_FFINAL THEN
      MI_FFINAL := MI_FFINAL;
    ELSE
      MI_FFINAL := RS.F2;
    END IF;
    MI_DD := 0;
    MI_TDIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN   => MI_FINICIO
                                                  ,UN_FECHAFIN  => MI_FFINAL);
    MI_BASEPROPORCIONAL := PCK_NOMINA_SEGSOCI.FC_BASE(UN_IBC       => MI_IBCMESANTERIOR
                                                     ,UN_DIAS      => MI_TDIAS
                                                     ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                     ,UN_FRACCION  => FALSE
                                                     ,UN_PLANO_SOI => TRUE);

    IF PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_IBCMESANTERIOR
                              ,UN_PRECISION => -3) <> PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC
                                                                             ,UN_PRECISION => -3)
      AND MI_TIPOINCAP = '' AND RS.NOVEDAD = 'NVAC' THEN
      IF PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC / 30 * PCK_NOMINA.FC_CNP(9)
                                ,UN_PRECISION => -3) = PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS
                                                                              ,UN_PRECISION => -3)
        AND PCK_NOMINA.FC_CNP(347) = 0 THEN
        MI_IBCMESANTERIOR := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC
                                                    ,UN_PRECISION => 0);
        --Alerta "Revisar concepto 348 en vacaciones, si es el IBC mes anterior o SALARIO_BASE_IBC: si genera error en el segundo registro posiblemente deba ser el cn(348) = cn(1), --> " & AApellido1 & " " & AApellido2 & " " & PNOMBRE & " " & sNombre & TIPODCTO & Pad(PCK_NOMINA_COM5.CSOI(UN_I).Numero_Dcto, 16)
      END IF;
    END IF;
    IF (PCK_NOMINA.FC_CNA(355) + PCK_NOMINA.FC_CNA(339)) >= 30 THEN
      MI_IBCMESANTERIOR := PCK_NOMINA.FC_CNA(112);
    END IF;

    IF MI_TIPOINCAP = '01' THEN
      MI_DD := CASE WHEN PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN   => MI_FINICIO
                                                           ,UN_FECHAFIN  => MI_FFINAL) >= GL_DIASARECONINCAPAINI
               THEN GL_DIASARECONINCAPAINI
               ELSE 1 END;
      MI_TDIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN   => MI_FINICIO
                                                    ,UN_FECHAFIN  => MI_FFINAL) - MI_DD;
      IF RS.F1 <= MI_FINICIO - 2 THEN
         MI_DD := 0 ;
         MI_TDIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN   => MI_FINICIO
                                                       ,UN_FECHAFIN  => MI_FFINAL) + MI_DD;
      END IF;
      IF PCK_PARENTR.PARAMETRO31 = '800.231.969-4' THEN
        IF PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC = PCK_PARENTR.PARAMETRO20 THEN
          MI_BASEPROPORCIONAL := FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                                    ,UN_DIAS      => MI_TDIAS
                                                    ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                    ,UN_ROUND     => 0);
          MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASEPROPORCIONAL
                                                        ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
          MI_BASEPROPORCIONAL := MI_BASEPROPORCIONAL
                               + FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                                    ,UN_DIAS      => MI_DD
                                                    ,UN_DECIMALES => 0
                                                    ,UN_ROUND     => PCK_NOMINA.GL_RBASE1990);
        ELSE
          MI_BASEPROPORCIONAL := FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                                    ,UN_DIAS      => MI_TDIAS
                                                    ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                    ,UN_ROUND     => 0
                                                    ,UN_FACTOR    => 'VAL3*2+5');
          MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASEPROPORCIONAL
                                                        ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
          MI_BASEPROPORCIONAL := MI_BASEPROPORCIONAL
                               + FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                                    ,UN_DIAS      => MI_DD
                                                    ,UN_DECIMALES => 0
                                                    ,UN_ROUND     => PCK_NOMINA.GL_RBASE1990);
        END IF;
      ELSE
        MI_BASEPROPIIF := FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                                    ,UN_DIAS      => MI_TDIAS
                                                    ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                    ,UN_ROUND     => 0
                                                    ,UN_FACTOR    => 'VAL3*2+0.05');
        MI_BASEPROPIIF := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASEPROPIIF
                                                 ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
        MI_BASEPROPIIF := MI_BASEPROPIIF
                             + FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                                  ,UN_DIAS      => MI_DD
                                                  ,UN_DECIMALES => 0
                                                  ,UN_ROUND     => PCK_NOMINA.GL_RBASE1990);
        IF MI_BASEPROPIIF < PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_PARENTR.PARAMETRO20 / 30 * (MI_TDIAS + MI_DD)
                                                   ,UN_PRECISION => 0) THEN
          MI_BASEPROPORCIONAL := FC_BASEPROPORCIONAL(UN_IBC       => PCK_PARENTR.PARAMETRO20
                                                    ,UN_DIAS      => MI_TDIAS
                                                    ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                    ,UN_ROUND     => 0
                                                    ,UN_FACTOR    => 'VAL0.05');
          MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASEPROPORCIONAL
                                                        ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
          MI_BASEPROPORCIONAL := MI_BASEPROPORCIONAL
                               + PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC / 30 * MI_DD
                                                        ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
        ELSE
          MI_BASEPROPIIF := FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                               ,UN_DIAS      => MI_DD
                                               ,UN_DECIMALES => 0
                                               ,UN_ROUND     => PCK_NOMINA.GL_RBASE1990);
          IF MI_BASEPROPIIF  - PCK_NOMINA.FC_CNA(378) < 10000 AND PCK_NOMINA.FC_CNA(358) = MI_DD THEN
            MI_BASEPROPIIF := FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                                 ,UN_DIAS      => MI_TDIAS
                                                 ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                 ,UN_ROUND     => 0
                                                 ,UN_FACTOR    => 'VAL3*2+0.05');
            MI_BASEPROPIIF := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASEPROPIIF
                                                     ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
            IF PCK_NOMINA.FC_CNA(379) - PCK_NOMINA.FC_CNA(378) - MI_BASEPROPIIF <10000
               AND PCK_NOMINA.FC_CNA(378)>0 THEN
              MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA.FC_CNA(379) - PCK_NOMINA.FC_CNA(378)
                                                            ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990)
                                   + PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA.FC_CNA(378)
                                                            ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
            ELSIF PCK_NOMINA.FC_CNA(379) - PCK_NOMINA.FC_CNA(378) - MI_BASEPROPIIF < 10000
              AND PCK_NOMINA.FC_CNA(378)  = 0
              AND  PCK_NOMINA.FC_CNA(347) - PCK_NOMINA.FC_CNA(9) = 30 THEN
              MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA.FC_CNA(112) - PCK_NOMINA.FC_CNA(212)
                                                            ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
            ELSE
              MI_BASEPROPORCIONAL := MI_BASEPROPIIF
                                   + PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA.FC_CNA(378)
                                                            ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
              MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASEPROPORCIONAL
                                                            ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
            END IF;
          ELSIF PCK_NOMINA.FC_CNA(370) < FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                                            ,UN_DIAS      => MI_DD
                                                            ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                            ,UN_ROUND     => 0
                                                            ,UN_FACTOR    => 'VAL3*2+0.05')
           AND PCK_NOMINA.FC_CNA(358) = 0 AND PCK_NOMINA.FC_CNA(378) = 0 THEN
            IF PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA.FC_CNA(370)
                                      ,UN_PRECISION => 2)
             = PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_PARENTR.PARAMETRO20 / 30*(MI_TDIAS - MI_DD)
                                      ,UN_PRECISION => 0) THEN
              MI_BASEPROPORCIONAL:= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_PARENTR.PARAMETRO20 / 30*(MI_TDIAS - MI_DD) +0.5
                                                           ,UN_PRECISION => 0);
            ELSE
              MI_BASEPROPORCIONAL := FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                                        ,UN_DIAS      => MI_TDIAS
                                                        ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                        ,UN_ROUND     => 0
                                                        ,UN_FACTOR    => 'VAL3*2+0.05');
            END IF;
          ELSE
            MI_BASEPROPIIF := FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                                 ,UN_DIAS      => MI_TDIAS
                                                 ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                 ,UN_ROUND     => 0
                                                 ,UN_FACTOR    => 'VAL3*2+0.05');
            MI_BASEPROPIIF := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASEPROPIIF
                                                     ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
            IF MI_BASEPROPIIF < PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => FC_BASEPROPORCIONAL(UN_IBC       => PCK_PARENTR.PARAMETRO20
                                                                                           ,UN_DIAS      => MI_TDIAS
                                                                                           ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                                                           ,UN_ROUND     => 0
                                                                                           ,UN_FACTOR    => 'VAL30.05')
                                                       ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990) THEN
              MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => FC_BASEPROPORCIONAL(UN_IBC       => PCK_PARENTR.PARAMETRO20
                                                                                                ,UN_DIAS      => MI_TDIAS
                                                                                                ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                                                                ,UN_ROUND     => 0
                                                                                                ,UN_FACTOR    => 'VAL30.05')
                                                            ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
              MI_BASEPROPORCIONAL := MI_BASEPROPORCIONAL
                                   + FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                                        ,UN_DIAS      => MI_DD
                                                        ,UN_DECIMALES => 0
                                                        ,UN_ROUND     => PCK_NOMINA.GL_RBASE1990
                                                        ,UN_FACTOR    => 'VAL30.05');
              --SE COMENTA UNA ALERTA SIN SENTIDO
            ELSE
              MI_BASEPROPORCIONAL := FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                                        ,UN_DIAS      => MI_TDIAS
                                                        ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                        ,UN_ROUND     => 0
                                                        ,UN_FACTOR    => 'VAL3*2+0.05');
              MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASEPROPORCIONAL
                                                            ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
              MI_BASEPROPORCIONAL := MI_BASEPROPORCIONAL
                                   + FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                                        ,UN_DIAS      => MI_DD
                                                        ,UN_DECIMALES => 0
                                                        ,UN_ROUND     => PCK_NOMINA.GL_RBASE1990);
            END IF;
          END IF;
        END IF;
      END IF;
      MI_TDIAS := MI_TDIAS + MI_DD;
    ELSIF MI_TIPOINCAP = '02' THEN
      MI_BASEPROPORCIONAL := FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                                 ,UN_DIAS      => MI_TDIAS
                                                 ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                 ,UN_ROUND     => 0
                                                 ,UN_FACTOR    => 'VAL3*2+0.05');
      MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASEPROPORCIONAL
                                                    ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
    ELSIF MI_TIPOINCAP = '05' THEN
      --AQUI SUMABA CON DD PERO A ESTA ALTURA EL MI_DD ES 0
      MI_BASEPROPORCIONAL := FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                                 ,UN_DIAS      => MI_TDIAS
                                                 ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                 ,UN_ROUND     => 0);
    ELSE
      MI_BASEPROPORCIONAL := FC_BASEPROPORCIONAL(UN_IBC       => MI_IBCMESANTERIOR
                                                 ,UN_DIAS      => MI_TDIAS
                                                 ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                 ,UN_ROUND     => 0);
    END IF;

    --INCLUIR_NOVEDADES_PILA Compania, GetProceso(), anno, MMES, "03", Empleado, Val(Contador), RS.NOVEDAD, MI_IBCMESANTERIOR, MI_FINICIO, MI_FFINAL, MI_TDIAS, MI_BASEPROPORCIONAL
    MI_TOTDIASNOV := MI_TOTDIASNOV + MI_TDIAS;
    PR_LIMPIARCAMPSO80A97();
    GL_SNING  := ' ';
    GL_SNRET  := ' ';
    GL_SNTDA  := ' ';
    GL_SNTAA  := ' ';
    GL_SNTDAP := ' ';
    GL_SNTAAP := ' ';
    GL_SNVSP  := ' ';
    GL_SVTE   := ' ';      
    GL_SNVST  := ' ';
    GL_SNSLN  := ' ';
    GL_SNIGE  := ' ';
    GL_SNLMA  := ' ';
    GL_SNVAC  := ' ';
    GL_SNAVP  := ' ';
    GL_SNVCT  := ' ';
    GL_SNIRP  := '00';

    GL_DIASP := PCK_SYSMAN_UTL.FC_STRZERO(MI_TDIAS, 2);
    /*
    MI_DIASS := PCK_SYSMAN_UTL.FC_STRZERO(MI_TDIAS, 2);

    */
    GL_DIASR := PCK_SYSMAN_UTL.FC_STRZERO(MI_TDIAS, 2);
    GL_DIASC := PCK_SYSMAN_UTL.FC_STRZERO(MI_TDIAS, 2);


    GL_BASECAJASP := MI_BASEPROPORCIONAL;
    GL_CAMPO95 := PCK_SYSMAN_UTL.FC_STRZERO(GL_BASECAJASP,9);
    MI_FFINALVAR := TO_CHAR(MI_FFINAL, 'YYYY-MM-DD');
    MI_FINICIOVAR := TO_CHAR(MI_FINICIO, 'YYYY-MM-DD');
    IF RS.NOVEDAD ='NSLN' THEN
      GL_SNSLN   := 'X';
			GL_CAMPO83 := MI_FINICIOVAR;
      GL_CAMPO84 := MI_FFINALVAR;
    ELSIF RS.NOVEDAD = 'NIGE' THEN
      GL_SNIGE   := 'X';
      GL_CAMPO85 := MI_FINICIOVAR;
      GL_CAMPO86 := MI_FFINALVAR;
    ELSIF RS.NOVEDAD = 'NLMA' THEN
      GL_SNLMA   := 'X';
      GL_CAMPO87 := MI_FINICIOVAR;
      GL_CAMPO88 := MI_FFINALVAR;
    ELSIF RS.NOVEDAD = 'LREM' THEN
      GL_SNVAC   := 'L';
      GL_CAMPO89 := MI_FINICIOVAR;
      GL_CAMPO90 := MI_FFINALVAR;
    ELSIF RS.NOVEDAD = 'NIRP' THEN
      GL_SNIRP   := PCK_SYSMAN_UTL.FC_STRZERO(MI_TDIAS, 2);
      GL_CAMPO93 := MI_FINICIOVAR;
      GL_CAMPO94 := MI_FFINALVAR;
    ELSIF RS.NOVEDAD = 'NVAC' THEN
      GL_SNVAC   := 'X';
      GL_CAMPO89 := MI_FINICIOVAR;
      GL_CAMPO90 := MI_FFINALVAR;
    ELSIF RS.NOVEDAD ='NINT' THEN
      --GL_SNINT   := 'X';
      GL_CAMPO89 := MI_FINICIOVAR;
      GL_CAMPO90 := MI_FFINALVAR;
    END IF;
    IF (PCK_NOMINA.FC_CNA(347) + PCK_NOMINA.FC_CNA(355)) >= 30 AND PCK_NOMINA.FC_CNA(170) > 0 THEN
        GL_SNVSt := 'X';
    ELSIF RS.NOVEDAD = 'NVAC' AND (GL_GENERAPARAFISCALESENVACA = 'SI' OR GL_GENERAOTPARAFISCALESENVACA='SI') THEN
        GL_BASECAJASP := MI_BASEPROPORCIONAL;
        GL_CAMPO95 := PCK_SYSMAN_UTL.FC_STRZERO(GL_BASECAJASP,9);
    ELSE
        GL_BASECAJASP := 0;
        GL_CAMPO95 := PCK_SYSMAN_UTL.FC_STRZERO(GL_BASECAJASP,9);
    END IF;
    GL_SUELDOP      := PCK_SYSMAN_UTL.FC_STRZERO(MI_IBCMESANTERIOR, 9);
    GL_BASEPENSIONP := MI_BASEPROPORCIONAL;
    GL_BASESALUDP   := MI_BASEPROPORCIONAL;
    GL_BASERIESGOSP := MI_BASEPROPORCIONAL;
    IF GL_EXONERACIONSALUD = 'SI' THEN
       MI_PORCSALUD := TO_NUMBER(PCK_PARENTR.PARAMETRO41);
       --PCK_NOMINA.GL_PORCSALUDCONDECRETO1122 := MI_PORCSALUD;
    ELSE
       MI_PORCSALUD := CASE WHEN PCK_NOMINA.GL_CALCULARSALUDCONDECRETO1122 = 'SI'
                       THEN PCK_NOMINA.GL_PORCSALUDCONDECRETO1122
                       ELSE PCK_PARENTR.PARAMETRO43 END;
    END IF;
    MI_PORCPENSION := TO_NUMBER(NVL(PCK_PARENTR.PARAMETRO39, 0));
    GL_PENSIONAP   := PCK_NOMINA_SEGSOCI.FC_APORTE(UN_IBC        => GL_BASEPENSIONP,
                                                   UN_DIAS      => MI_TDIAS,
                                                   UN_DECIMALES => PCK_NOMINA.GL_RAPORTES1990,
                                                   UN_PORCENTAJE => MI_PORCPENSION);
    IF PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12', '19') THEN
        MI_PORCSALUD   := PCK_PARENTR.PARAMETRO43;
        MI_PORCPENSION := 0;
        GL_PENSIONAP      := 0;
        PCK_NOMINA.GL_CALCULARSALUDCONDECRETO1122 := PCK_PARENTR.PARAMETRO43;
        GL_EXONERACIONSALUD := 'NO';
        GL_DIASP := PCK_SYSMAN_UTL.FC_STRZERO(0, 2);
        MI_TDIAS := 0;
    END IF;
    GL_SALUDAP   := PCK_NOMINA_SEGSOCI.FC_APORTE(UN_IBC       => GL_BASESALUDP,
                                                   UN_DIAS      => MI_TDIAS,
                                                   UN_DECIMALES => PCK_NOMINA.GL_RAPORTES1990,
                                                   UN_PORCENTAJE => MI_PORCSALUD);
    GL_CAMPO96 := 0;
    MI_TOTALBASEPARAFISCALN := MI_TOTALBASEPARAFISCALN + GL_BASECAJASP;
    MI_TOTALBASEPENSIONN    := MI_TOTALBASEPENSIONN    + GL_BASEPENSIONP;
    MI_TOTALBASESALUDN      := MI_TOTALBASESALUDN      + GL_BASESALUDP;

    MI_SUMAAPORTESPENSIONN := MI_SUMAAPORTESPENSIONN + GL_PENSIONAP;
    MI_SUMAAPORTESSALUDN   := MI_SUMAAPORTESSALUDN   + GL_SALUDAP;
    MI_SUMAAPORTESRIESGOSN := MI_SUMAAPORTESRIESGOSN + 0;
    GL_FSPPLANILLA     := 0;
    GL_FSPADICPLANILLA := 0;
    IF PCK_NOMINA.FC_CNA(353) >= 30 THEN
      GL_FSPPLANILLA := (PCK_NOMINA_COM5.CSOI(UN_I).FSP / 2);
      GL_FSPADICPLANILLA := (PCK_NOMINA_COM5.CSOI(UN_I).FSP / 2) + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL;
    END IF;

    IF GL_PLANOFSPPILA = 'SI' AND PCK_NOMINA_COM5.CSOI(UN_I).FSP > 0 THEN
      GL_FSPPLANILLA     := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASEPENSIONP * 0.005  , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                                   , PCK_NOMINA.GL_RAPORTES1990);
      GL_FSPADICPLANILLA := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASEPENSIONP * 0.005  , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                                   , PCK_NOMINA.GL_RAPORTES1990);
    END IF;
    MI_SUMAAPORTESFSPN := MI_SUMAAPORTESFSPN + GL_FSPPLANILLA + GL_FSPADICPLANILLA;
    GL_RIESGOSAP := 0;
    GL_CTARP     := 0 ;

    GL_CN101AP := 0 ;
    GL_CN102AP := 0 ;
    GL_CN103AP := 0 ;
    GL_CN104AP := 0 ;
    GL_CN105AP := 0 ;
    IF GL_EXONERACIONSALUD <> 'SI' THEN
      GL_CN101AP := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA.GL_SUMARSS1990
                                           ,UN_PRECISION => PCK_NOMINA.GL_RAPORTES1990) ;
      GL_CN102AP := GL_CN101AP ;
      GL_CN103AP := GL_CN101AP ;
      GL_CN104AP := GL_CN101AP ;
      GL_CN105AP := GL_CN101AP;
    END IF;
    IF (GL_CN102AP + GL_CN103AP + GL_CN104AP + GL_CN105AP) = 0
      AND ((PCK_NOMINA.FC_CNA(347) + PCK_NOMINA.FC_CNA(355)) < 30 AND PCK_NOMINA.FC_CNA(170) <> 0) THEN
      GL_CAMPO95 := '0';
    END IF;
    MI_CAMPOSNUEVOS2388N := FC_CONCATENARCAMPSO80A97;
    PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                          FC_REGISTROPLANO (UN_COMPANIA         => UN_COMPANIA,
                                             UN_I                => UN_I,
                                             UN_TIPOESTRUCTURA   => UN_TIPOESTRUCTURA,
                                             UN_CLASEREGISTRO    => 'NOV',
                                             UN_TIPOPLANILLA    => UN_TIPOPLANILLA,
                                             UN_CAMPOSNUEVOS2388 => MI_CAMPOSNUEVOS2388N);
    MI_PRIMERREG := 1;
  END LOOP NOVEDADES;

  IF MI_PRIMERREG>0 THEN
    PR_LIMPIARCAMPSO80A97();
    --REVISARSUELDO SECOMENTA PUES HACE OPERACIONES QUE NO APORTAN AL PROCESO
    PR_REVISARSUELDO (UN_COMPANIA   => UN_COMPANIA
                    ,UN_I          => UN_I
                    ,UN_ANIO       => UN_ANIO
                    ,UN_MES        => UN_MES
                    ,UN_RETROACTIVO=> UN_RETROACTIVO);
    IF PCK_NOMINA.FC_CNA(10)>0 AND PCK_NOMINA.GL_NOMINAMENSUAL='SI' THEN
      GL_SUELDOP := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA.FC_CNA(10), 9);
    END IF;
    IF PCK_NOMINA.FC_CNA(10)>0  THEN
      IF PCK_NOMINA.FC_CNA(10) = MI_IBCMESANTERIOR OR PCK_NOMINA.FC_CNA(10) = MI_IBCMESANTERIOR +1 THEN
        GL_SUELDOP := PCK_SYSMAN_UTL.FC_STRZERO(MI_IBCMESANTERIOR, 9);
      ELSE
  			GL_SUELDO := PCK_SYSMAN_UTL.FC_STRZERO(MI_IBCMESANTERIOR, 9);
      END IF;
    ELSE
      IF GL_SNVAC ='X' OR MI_IBCMESANTERIOR> TO_NUMBER(GL_SUELDO) THEN
        GL_SUELDOP :=PCK_SYSMAN_UTL.FC_STRZERO(GL_SUELDO, 9);
      END IF;
    END IF;
    GL_BASEPENSIONP := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(UN_I).BASE - MI_TOTALBASEPENSIONN
                                              ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
    GL_BASESALUDP := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(UN_I).BASE - MI_TOTALBASESALUDN
                                            ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
    GL_BASERIESGOSP := PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS;
    GL_BASECAJASP := PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL;
    --REVISAR ESTO PUES ES EL DEL REGISTRO PRINCIPAL
    GL_NIGE := ' ';
    GL_NLMA := ' ';
    GL_NVAC := ' ';
    IF   (PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS - GL_BASESALUDP) > -600
     AND (PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS - GL_BASESALUDP) < 600 THEN
      GL_BASEPENSIONP := PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS;
      GL_BASESALUDP   := PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS;
    END IF;
    GL_NUMDIAS  := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).DIASRIESGOS, 2);
    GL_NUMDIASS := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).DIASRIESGOS, 2);
    GL_DIASPA   := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).DIASRIESGOS, 2);

    MI_PORCSALUD := CASE WHEN PCK_NOMINA.GL_CALCULARSALUDCONDECRETO1122 = 'SI'
                         THEN PCK_NOMINA.GL_PORCSALUDCONDECRETO1122
                         ELSE PCK_PARENTR.PARAMETRO43 END;
    MI_PORCPENSION := TO_NUMBER(NVL(PCK_PARENTR.PARAMETRO39, 0));
    IF PCK_NOMINA_COM5.CSOI(UN_I).SUBTIPOCOTIZANTE IS NOT NULL THEN
      IF PCK_NOMINA_COM5.CSOI(UN_I).SUBTIPOCOTIZANTE>=1 AND PCK_NOMINA_COM5.CSOI(UN_I).PENSION =0 THEN
        MI_PORCSALUD    :=0;
        GL_BASEPENSIONP :=0;
      END IF;
    END IF;

    IF   PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS - GL_BASEPENSIONP > 0
     AND PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS - GL_BASEPENSIONP < 10
     AND PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS - GL_BASEPENSIONP <> 0 THEN
      GL_BASEPENSIONP := PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS;
      GL_BASESALUDP   := PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS;
    END IF;

    GL_PENSIONAP   := PCK_NOMINA_SEGSOCI.FC_APORTE(UN_IBC        => GL_BASEPENSIONP,
                                                   UN_DIAS       => GL_NUMDIASS,
                                                   UN_DECIMALES  => PCK_NOMINA.GL_RAPORTES1990,
                                                   UN_PORCENTAJE => MI_PORCPENSION);
    GL_SALUDAP   := PCK_NOMINA_SEGSOCI.FC_APORTE(UN_IBC        => GL_BASESALUDP,
                                                UN_DIAS       => GL_NUMDIASS,
                                                UN_DECIMALES  => PCK_NOMINA.GL_RAPORTES1990,
                                                UN_PORCENTAJE => MI_PORCSALUD);
    --GL_BASECAJASP := PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL;
    GL_BASECAJASP := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL
                                            ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
    IF PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS - GL_BASECAJASP > -100 AND
       PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS - GL_BASECAJASP <  100 THEN
      GL_BASECAJASP := PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS;
    END IF;
    GL_CAMPO95    := PCK_SYSMAN_UTL.FC_STRZERO(GL_BASECAJASP,9);

    IF PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12', '19') THEN
      GL_PENSIONAP := 0;
      GL_DIASPA    := '00';
    END IF;
    IF GL_PENSIONAP>0 AND GL_NUMDIASS='00' AND GL_NUMDIAS='00' THEN
      GL_NUMDIAS  := PCK_SYSMAN_UTL.FC_STRZERO(MI_DIASTRABAJADOS,2);
      GL_NUMDIASS := PCK_SYSMAN_UTL.FC_STRZERO(MI_DIASTRABAJADOS,2);
      GL_DIASPA   := PCK_SYSMAN_UTL.FC_STRZERO(MI_DIASTRABAJADOS,2);
    END IF;

    IF PCK_NOMINA.GL_DIASSEMANALES * (PCK_NOMINA.FC_CNA(9) +PCK_NOMINA.FC_CNA(11)) <>0 THEN
      GL_CAMPO96 :=PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => (PCK_NOMINA.GL_HORASMENSUALES/5) / PCK_NOMINA.GL_DIASSEMANALES * MI_DIASTRABAJADOS
                                          ,UN_PRECISION => 0);
    END IF;
    IF GL_NING <> ' ' THEN
      GL_CAMPO80 := TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_DE_INGRESO, 'YYYY-MM-DD');
    END IF;
    IF GL_NRET <> ' ' THEN
      GL_CAMPO81 := TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHATERCONTRATO, 'YYYY-MM-DD');
    END IF;
    IF GL_NVST <> ' ' AND PCK_NOMINA_COM5.CSOI(UN_I).FECHA_ULTSUELDO IS NOT NULL THEN
      IF     TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_ULTSUELDO,'MM'))   = UN_MES
         AND TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_ULTSUELDO,'YYYY')) = UN_ANIO  THEN
        GL_CAMPO82 :=  TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_ULTSUELDO, 'YYYY-MM-DD');
      END IF;
    END IF;

    GL_CTAR := PCK_NOMINA_SEGSOCI.FC_PORCENRIESGO(UN_COMPANIA => UN_COMPANIA
                                                 ,UN_IDEMPLEADO => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO) / 100;
    MI_TDIAS   := GL_NUMDIASS;
    MI_TOTALBASEPARAFISCALN := 0;
    GL_BASECAJASP := CASE WHEN GL_BASECAJASP <0 THEN 0 ELSE GL_BASECAJASP  END;
    MI_APORTEPARA:= PCK_NOMINA_SEGSOCI.FC_APORTE(UN_IBC        => GL_BASECAJASP,
                                                 UN_DIAS       => MI_TDIAS,
                                                 UN_DECIMALES  => -2,
                                                 UN_PORCENTAJE => PCK_PARENTR.PARAMETRO47);
    IF GL_EXONERACIONSALUD = 'SI' THEN
      GL_CN101AP := PCK_NOMINA_SEGSOCI.FC_APORTE(UN_IBC        => GL_BASECAJASP,
                                                   UN_DIAS      => MI_TDIAS,
                                                   UN_DECIMALES => -2,
                                                   UN_PORCENTAJE => PCK_PARENTR.PARAMETRO47);
      GL_CN102AP := 0;
      GL_CN103AP := 0;
      GL_CN104AP := 0;
      GL_CN105AP := 0;
    ELSE
      IF MI_APORTEPARA = PCK_NOMINA.FC_CNA(101)
        OR MI_APORTEPARA < PCK_NOMINA.FC_CNA(101) - 200
        OR MI_APORTEPARA < PCK_NOMINA.FC_CNA(101) + 200 THEN
        GL_CN101AP := PCK_NOMINA.FC_CNA(101);
        GL_CN102AP := PCK_NOMINA.FC_CNA(102);
        GL_CN103AP := PCK_NOMINA.FC_CNA(103);
        GL_CN104AP := PCK_NOMINA.FC_CNA(104);
        GL_CN105AP := PCK_NOMINA.FC_CNA(105);
      ELSE
        GL_CN101AP := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASECAJASP * PCK_PARENTR.PARAMETRO47 /100
                                                             , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                             , PCK_NOMINA.GL_RAPORTES1990);
        GL_CN102AP := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASECAJASP * PCK_PARENTR.PARAMETRO45 /100
                                                             , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                             , PCK_NOMINA.GL_RAPORTES1990);
        GL_CN103AP := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASECAJASP * PCK_PARENTR.PARAMETRO46 /100
                                                             , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                             , PCK_NOMINA.GL_RAPORTES1990);
        GL_CN104AP := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASECAJASP * PCK_PARENTR.PARAMETRO48 /100
                                                             , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                             ,PCK_NOMINA.GL_RAPORTES1990);
        GL_CN105AP := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASECAJASP * PCK_PARENTR.PARAMETRO49 /100
                                                             , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                             , PCK_NOMINA.GL_RAPORTES1990);
      END IF;
    END IF;
    GL_NVST :='X';

    IF (GL_CN102AP + GL_CN103AP + GL_CN104AP + GL_CN105AP) = 0
      AND (PCK_NOMINA.GL_OPERADOR IN('APORTESENLINEA', 'MIPLANILLA', 'ASOPAGOS'))
       OR (PCK_NOMINA.GL_OPERADOR = 'SOI.COM.CO' AND GL_EXONERACIONSALUD = 'SI') THEN
        GL_CAMPO95 := 0;
    END IF;

    GL_FSPPLANILLA     := 0;
    GL_FSPADICPLANILLA := 0;
    IF PCK_NOMINA_COM5.CSOI(UN_I).FSP <> 0 THEN
      GL_FSPPLANILLA := (PCK_NOMINA_COM5.CSOI(UN_I).FSP / 2);
      GL_FSPADICPLANILLA := (PCK_NOMINA_COM5.CSOI(UN_I).FSP / 2) + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL;
    END IF;
    IF GL_PLANOFSPPILA = 'SI' AND PCK_NOMINA_COM5.CSOI(UN_I).FSP > 0 THEN
      GL_FSPPLANILLA     := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASEPENSIONP * 0.005  , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                                   , PCK_NOMINA.GL_RAPORTES1990);
      GL_FSPADICPLANILLA := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASEPENSIONP * 0.005  , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                                   ,PCK_NOMINA.GL_RAPORTES1990) + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL;
    END IF;
    MI_SUMAAPORTESFSPN := MI_SUMAAPORTESFSPN + GL_FSPPLANILLA + GL_FSPADICPLANILLA;
    MI_SUMAAPORTESPENSIONN := MI_SUMAAPORTESPENSIONN + GL_PENSIONAP;
    MI_SUMAAPORTESSALUDN   := MI_SUMAAPORTESSALUDN   + GL_SALUDAP;
    MI_SUMAAPORTESRIESGOSN := MI_SUMAAPORTESRIESGOSN + PCK_NOMINA_COM5.CSOI(UN_I).RIESGOS;
    --MI_SUMAAPORTESPARAFISCALN := MI_SUMAAPORTESPARAFISCALN + GL_CN101AP + GL_CN102AP + GL_CN103AP + GL_CN104AP + GL_CN105AP;

    IF NOT((MI_TOTDIASNOV >=30 AND GL_BASEPENSIONP = 0)
     AND (PCK_NOMINA.FC_CNA(347)>=30
       OR PCK_NOMINA.FC_CNA(2) + PCK_NOMINA.FC_CNA(150) = 0)) THEN

      MI_CAMPOSNUEVOS2388N := FC_CONCATENARCAMPSO80A97;
      PCK_NOMINA_COM5.GL_DISCOLIQUIDACION :=  PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                               FC_REGISTROPLANO (UN_COMPANIA         => UN_COMPANIA,
                                                 UN_I                => UN_I,
                                                 UN_TIPOESTRUCTURA   => UN_TIPOESTRUCTURA,
                                                 UN_TIPOPLANILLA     => UN_TIPOPLANILLA,
                                                 UN_CLASEREGISTRO    => 'NOVF',
                                                 UN_CAMPOSNUEVOS2388 => MI_CAMPOSNUEVOS2388N);
      --INCLUIR_NOVEDADES_PILAPARTES Getcompany(), GetProceso(), anno, MMES, "03", PCK_NOMINA_COM5.CSOI(UN_I).ID_de_Empleado, Val(Contador), PART1 & PART2
    END IF;


    DECLARE
      MI_MSG      PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN
      MI_MSG(1).CLAVE := 'EMPLEADO';
      MI_MSG(1).VALOR := PCK_NOMINA_COM5.CSOI(UN_I).NOMBRECOMPLETO || '. ' || LPAD(PCK_NOMINA_COM5.CSOI(UN_I).NUMERO_DCTO, 16);
      IF MI_SUMAAPORTESPENSIONN <> PCK_NOMINA_COM5.CSOI(UN_I).PENSION THEN
        IF MI_SUMAAPORTESPENSIONN - PCK_NOMINA_COM5.CSOI(UN_I).PENSION >= -200 AND
           MI_SUMAAPORTESPENSIONN - PCK_NOMINA_COM5.CSOI(UN_I).PENSION <= 200 THEN
          MI_MSG(2).CLAVE := 'TIPOVALOR';
          MI_MSG(2).VALOR := 'PensiÂ¿' || TO_CHAR(MI_SUMAAPORTESPENSIONN - PCK_NOMINA_COM5.CSOI(UN_I).PENSION);
          PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA  => UN_COMPANIA,
                                     UN_MENSAJE_COD => PCK_ERRORES.ALER_SUMAORESTADELPLANO,
                                     UN_REEMPLAZOS  => MI_MSG);
          PCK_NOMINA.PR_INCLUIRHISTORICOF( UN_COMPANIA   => UN_COMPANIA
                                          ,UN_PROCESO    => UN_PROCESO
                                          ,UN_ANIO       => UN_ANIO
                                          ,UN_MES        => UN_MES
                                          ,UN_PERIODO    => UN_PERIODO
                                          ,UN_IDEMPLEADO => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
                                          ,UN_IDCONCEPTO => 117
                                          ,UN_VALOR      => MI_SUMAAPORTESPENSIONN - PCK_NOMINA_COM5.CSOI(UN_I).PENSION
                                          ,UN_FECHAC     => SYSDATE()
                                          ,UN_OBS        => 'Por efectos de redondeo se ajusta en :' || TO_CHAR(MI_SUMAAPORTESPENSIONN - PCK_NOMINA_COM5.CSOI(UN_I).PENSION)
                                          ,UN_ACCION     => '+'
                                          ,UN_USUARIO    => UN_USUARIO);
        END IF;
      END IF;
      IF MI_SUMAAPORTESSALUDN <> (PCK_NOMINA_COM5.CSOI(UN_I).SALUD) THEN
        IF MI_SUMAAPORTESSALUDN - (PCK_NOMINA_COM5.CSOI(UN_I).SALUD) >= -200 AND
           MI_SUMAAPORTESSALUDN - (PCK_NOMINA_COM5.CSOI(UN_I).SALUD) <= 200 Then
          IF PCK_PARENTR.PARAMETRO70 = 'S' Then

            PCK_NOMINA.PR_INCLUIRHISTORICOF( UN_COMPANIA   => UN_COMPANIA
                                            ,UN_PROCESO    => UN_PROCESO
                                            ,UN_ANIO       => UN_ANIO
                                            ,UN_MES        => UN_MES
                                            ,UN_PERIODO    => UN_PERIODO
                                            ,UN_IDEMPLEADO => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
                                            ,UN_IDCONCEPTO => 130
                                            ,UN_VALOR      => MI_SUMAAPORTESSALUDN - PCK_NOMINA_COM5.CSOI(UN_I).SALUD
                                            ,UN_FECHAC     => SYSDATE()
                                            ,UN_OBS        => 'Por efectos de redondeo se ajusta en :' || TO_CHAR(MI_SUMAAPORTESSALUDN - PCK_NOMINA_COM5.CSOI(UN_I).SALUD)
                                            ,UN_ACCION     => '+'
                                            ,UN_USUARIO    => UN_USUARIO);


            PCK_NOMINA.PR_INCLUIRHISTORICOF( UN_COMPANIA   => UN_COMPANIA
                                            ,UN_PROCESO    => UN_PROCESO
                                            ,UN_ANIO       => UN_ANIO
                                            ,UN_MES        => UN_MES
                                            ,UN_PERIODO    => UN_PERIODO
                                            ,UN_IDEMPLEADO => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
                                            ,UN_IDCONCEPTO => 113
                                            ,UN_VALOR      => MI_SUMAAPORTESSALUDN - PCK_NOMINA_COM5.CSOI(UN_I).SALUD
                                            ,UN_FECHAC     => SYSDATE()
                                            ,UN_OBS        => 'Por efectos de redondeo se ajusta en :' || TO_CHAR(MI_SUMAAPORTESSALUDN - PCK_NOMINA_COM5.CSOI(UN_I).SALUD)
                                            ,UN_ACCION     => '+'
                                            ,UN_USUARIO    => UN_USUARIO);
            PCK_NOMINA.PR_INCLUIRHISTORICOF( UN_COMPANIA   => UN_COMPANIA
                                            ,UN_PROCESO    => UN_PROCESO
                                            ,UN_ANIO       => UN_ANIO
                                            ,UN_MES        => UN_MES
                                            ,UN_PERIODO    => UN_PERIODO
                                            ,UN_IDEMPLEADO => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
                                            ,UN_IDCONCEPTO => 140
                                            ,UN_VALOR      => MI_SUMAAPORTESSALUDN - PCK_NOMINA_COM5.CSOI(UN_I).SALUD
                                            ,UN_FECHAC     => SYSDATE()
                                            ,UN_OBS        => 'Por efectos de redondeo se ajusta en :' || TO_CHAR(MI_SUMAAPORTESSALUDN - PCK_NOMINA_COM5.CSOI(UN_I).SALUD)
                                            ,UN_ACCION     => '+'
                                            ,UN_USUARIO    => UN_USUARIO);
            PCK_NOMINA.PR_INCLUIRHISTORICOF( UN_COMPANIA   => UN_COMPANIA
                                            ,UN_PROCESO    => UN_PROCESO
                                            ,UN_ANIO       => UN_ANIO
                                            ,UN_MES        => UN_MES
                                            ,UN_PERIODO    => UN_PERIODO
                                            ,UN_IDEMPLEADO => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
                                            ,UN_IDCONCEPTO => 144
                                            ,UN_VALOR      => (MI_SUMAAPORTESSALUDN - PCK_NOMINA_COM5.CSOI(UN_I).SALUD)*-1
                                            ,UN_FECHAC     => SYSDATE()
                                            ,UN_OBS        => 'Por efectos de redondeo se ajusta en :' || TO_CHAR(MI_SUMAAPORTESSALUDN - PCK_NOMINA_COM5.CSOI(UN_I).SALUD)
                                            ,UN_ACCION     => '+'
                                            ,UN_USUARIO    => UN_USUARIO);

          ELSE
            PCK_NOMINA.PR_INCLUIRHISTORICOF( UN_COMPANIA   => UN_COMPANIA
                                            ,UN_PROCESO    => UN_PROCESO
                                            ,UN_ANIO       => UN_ANIO
                                            ,UN_MES        => UN_MES
                                            ,UN_PERIODO    => UN_PERIODO
                                            ,UN_IDEMPLEADO => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
                                            ,UN_IDCONCEPTO => 116
                                            ,UN_VALOR      => MI_SUMAAPORTESSALUDN - PCK_NOMINA_COM5.CSOI(UN_I).SALUD
                                            ,UN_FECHAC     => SYSDATE()
                                            ,UN_OBS        => 'Por efectos de redondeo se ajusta en :' || TO_CHAR(MI_SUMAAPORTESSALUDN - PCK_NOMINA_COM5.CSOI(UN_I).SALUD)
                                            ,UN_ACCION     => '+'
                                            ,UN_USUARIO    => UN_USUARIO);
          END IF;
          MI_MSG(2).CLAVE := 'TIPOVALOR';
				  MI_MSG(2).VALOR := 'Salud $' || TO_CHAR(MI_SUMAAPORTESSALUDN - PCK_NOMINA_COM5.CSOI(UN_I).SALUD);
          PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA  => UN_COMPANIA,
                                     UN_MENSAJE_COD => PCK_ERRORES.ALER_SUMAORESTADELPLANO,
                                     UN_REEMPLAZOS  => MI_MSG);

        END IF;
      END IF;
      IF MI_SUMAAPORTESRIESGOSN <> (PCK_NOMINA_COM5.CSOI(UN_I).RIESGOS) THEN
        IF MI_SUMAAPORTESRIESGOSN - (PCK_NOMINA_COM5.CSOI(UN_I).RIESGOS) >= -200 AND
           MI_SUMAAPORTESRIESGOSN - (PCK_NOMINA_COM5.CSOI(UN_I).RIESGOS) <= 200 THEN
          MI_MSG(2).CLAVE := 'TIPOVALOR';
				  MI_MSG(2).VALOR := 'Riesgos $' || TO_CHAR(MI_SUMAAPORTESRIESGOSN - PCK_NOMINA_COM5.CSOI(UN_I).RIESGOS);
          PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA  => UN_COMPANIA,
                                     UN_MENSAJE_COD => PCK_ERRORES.ALER_SUMAORESTADELPLANO,
                                     UN_REEMPLAZOS  => MI_MSG);
          PCK_NOMINA.PR_INCLUIRHISTORICOF( UN_COMPANIA   => UN_COMPANIA
                                          ,UN_PROCESO    => UN_PROCESO
                                          ,UN_ANIO       => UN_ANIO
                                          ,UN_MES        => UN_MES
                                          ,UN_PERIODO    => UN_PERIODO
                                          ,UN_IDEMPLEADO => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
                                          ,UN_IDCONCEPTO => 111
                                          ,UN_VALOR      => MI_SUMAAPORTESRIESGOSN - PCK_NOMINA_COM5.CSOI(UN_I).RIESGOS
                                          ,UN_FECHAC     => SYSDATE()
                                          ,UN_OBS        => 'Por efectos de redondeo se ajusta en :' || TO_CHAR(MI_SUMAAPORTESRIESGOSN - PCK_NOMINA_COM5.CSOI(UN_I).RIESGOS)
                                          ,UN_ACCION     => '+'
                                          ,UN_USUARIO    => UN_USUARIO);
        END IF;
      END IF;
      IF MI_SUMAAPORTESFSPN <> (PCK_NOMINA_COM5.CSOI(UN_I).FSP + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL) AND
       ((MI_SUMAAPORTESFSPN  - (PCK_NOMINA_COM5.CSOI(UN_I).FSP + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL)) >= -200 AND
        (MI_SUMAAPORTESFSPN  - (PCK_NOMINA_COM5.CSOI(UN_I).FSP + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL)) <= 200) Then
        MI_MSG(2).CLAVE := 'TIPOVALOR';
				MI_MSG(2).VALOR := 'FSP $' || TO_CHAR(MI_SUMAAPORTESFSPN  - (PCK_NOMINA_COM5.CSOI(UN_I).FSP + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL));
        PCK_NOMINA.PR_INCLUIRHISTORICOF( UN_COMPANIA   => UN_COMPANIA
                                        ,UN_PROCESO    => UN_PROCESO
                                        ,UN_ANIO       => UN_ANIO
                                        ,UN_MES        => UN_MES
                                        ,UN_PERIODO    => UN_PERIODO
                                        ,UN_IDEMPLEADO => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
                                        ,UN_IDCONCEPTO => 115
                                        ,UN_VALOR      => MI_SUMAAPORTESFSPN  - (PCK_NOMINA_COM5.CSOI(UN_I).FSP + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL)
                                        ,UN_FECHAC     => SYSDATE()
                                        ,UN_OBS        => 'Por efectos de redondeo se ajusta en :' || TO_CHAR(MI_SUMAAPORTESFSPN  - (PCK_NOMINA_COM5.CSOI(UN_I).FSP + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL))
                                        ,UN_ACCION     => '+'
                                        ,UN_USUARIO    => UN_USUARIO);
        IF MI_SUMAAPORTESFSPN - PCK_NOMINA.FC_CNA(132) >= -200 AND
           MI_SUMAAPORTESFSPN - PCK_NOMINA.FC_CNA(132) <= 200 Then
          PCK_NOMINA.PR_INCLUIRHISTORICOF( UN_COMPANIA   => UN_COMPANIA
                                          ,UN_PROCESO    => UN_PROCESO
                                          ,UN_ANIO       => UN_ANIO
                                          ,UN_MES        => UN_MES
                                          ,UN_PERIODO    => UN_PERIODO
                                          ,UN_IDEMPLEADO => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
                                          ,UN_IDCONCEPTO => 132
                                          ,UN_VALOR      => MI_SUMAAPORTESFSPN  - PCK_NOMINA.FC_CNA(132)
                                          ,UN_FECHAC     => SYSDATE()
                                          ,UN_OBS        => 'Por efectos de redondeo se ajusta en :' || TO_CHAR(MI_SUMAAPORTESFSPN  - PCK_NOMINA.FC_CNA(132))
                                          ,UN_ACCION     => '+'
                                          ,UN_USUARIO    => UN_USUARIO);
          PCK_NOMINA.PR_INCLUIRHISTORICOF( UN_COMPANIA   => UN_COMPANIA
                                          ,UN_PROCESO    => UN_PROCESO
                                          ,UN_ANIO       => UN_ANIO
                                          ,UN_MES        => UN_MES
                                          ,UN_PERIODO    => UN_PERIODO
                                          ,UN_IDEMPLEADO => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
                                          ,UN_IDCONCEPTO => 140
                                          ,UN_VALOR      => MI_SUMAAPORTESFSPN  - PCK_NOMINA.FC_CNA(132)
                                          ,UN_FECHAC     => SYSDATE()
                                          ,UN_OBS        => 'Por efectos de redondeo se ajusta en :' || TO_CHAR(MI_SUMAAPORTESFSPN  - PCK_NOMINA.FC_CNA(132))
                                          ,UN_ACCION     => '+'
                                          ,UN_USUARIO    => UN_USUARIO);
          PCK_NOMINA.PR_INCLUIRHISTORICOF( UN_COMPANIA   => UN_COMPANIA
                                          ,UN_PROCESO    => UN_PROCESO
                                          ,UN_ANIO       => UN_ANIO
                                          ,UN_MES        => UN_MES
                                          ,UN_PERIODO    => UN_PERIODO
                                          ,UN_IDEMPLEADO => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
                                          ,UN_IDCONCEPTO => 144
                                          ,UN_VALOR      => (MI_SUMAAPORTESFSPN  - PCK_NOMINA.FC_CNA(132))*-1
                                          ,UN_FECHAC     => SYSDATE()
                                          ,UN_OBS        => 'Por efectos de redondeo se ajusta en :' || TO_CHAR((MI_SUMAAPORTESFSPN  - PCK_NOMINA.FC_CNA(132))*-1)
                                          ,UN_ACCION     => '+'
                                          ,UN_USUARIO    => UN_USUARIO);

        END IF;
      END IF;
    END;
  END IF;

  RETURN MI_CANTENC;
END FC_GENERAR_NOVEDADES_PILA_2388;


FUNCTION FC_BASEPROPORCIONAL(
    UN_IBC   		  NUMBER,
    UN_DIAS 		  NUMBER,
    UN_DECIMALES 	NUMBER,
    UN_ROUND    	NUMBER,
    UN_FACTOR    	VARCHAR2 DEFAULT ' '
    )
RETURN PCK_SUBTIPOS.TI_DOBLE
AS 
  MI_RETORNO NUMBER;
  MI_BASE  NUMBER;
BEGIN 
  MI_BASE :=PCK_NOMINA_SEGSOCI.FC_BASE(UN_IBC       => UN_IBC 
                                      ,UN_DIAS      => UN_DIAS
                                      ,UN_DECIMALES => UN_DECIMALES
                                      ,UN_FRACCION  => FALSE
                                      ,UN_PLANO_SOI => TRUE);
  IF MI_BASE = 0 THEN
    RETURN 0;
  END IF;
  IF UN_FACTOR = 'VAL3*2+0.05' THEN
    MI_BASE := MI_BASE /3*2 +0.05;
  ELSIF UN_FACTOR = 'PARINC' AND UN_IBC > PCK_NOMINA.FC_CN(201) THEN --JM 03/03/2025 CC 677
    MI_BASE := (MI_BASE * TO_NUMBER(PCK_PARST.FC_PAR('PORCENTAJE A PAGAR SOBRE INCAPACIDADES','0.6666666667')))+0.05; --JM 03/03/2025 CC 677
  ELSIF UN_FACTOR = 'VAL3*2' THEN
    MI_BASE := MI_BASE /3*2;
  ELSIF UN_FACTOR = 'VAL0.05' THEN
    MI_BASE := MI_BASE +0.05;
   ELSIF UN_FACTOR = 'VAL*0.5' THEN
    --TICKET 7743926(30/04/2024 JCROJAS) 
    MI_BASE := MI_BASE*0.5;	
  END IF;
  MI_RETORNO:=PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASE
                                     ,UN_PRECISION => UN_ROUND);
  RETURN MI_RETORNO;
END FC_BASEPROPORCIONAL;

PROCEDURE PR_LIMPIARCAMPSO80A97
AS 
BEGIN
  GL_CAMPO80:=  RPAD(' ',10,' ');
  GL_CAMPO81:=  RPAD(' ',10,' ');
  GL_CAMPO82:=  RPAD(' ',10,' ');
  GL_CAMPO83:=  RPAD(' ',10,' ');
  GL_CAMPO84:=  RPAD(' ',10,' ');
  GL_CAMPO85:=  RPAD(' ',10,' ');
  GL_CAMPO86:=  RPAD(' ',10,' ');
  GL_CAMPO87:=  RPAD(' ',10,' ');
  GL_CAMPO88:=  RPAD(' ',10,' ');
  GL_CAMPO89:=  RPAD(' ',10,' ');
  GL_CAMPO90:=  RPAD(' ',10,' ');
  GL_CAMPO91:=  RPAD(' ',10,' ');
  GL_CAMPO92:=  RPAD(' ',10,' ');
  GL_CAMPO93:=  RPAD(' ',10,' ');
  GL_CAMPO94:=  RPAD(' ',10,' ');
  GL_CAMPO95:=  0;
  GL_CAMPO96:=  0;
  GL_CAMPO97:=  RPAD(' ',10,' ');
END PR_LIMPIARCAMPSO80A97;

  FUNCTION FC_CONCATENARCAMPSO80A97
  RETURN VARCHAR2
  AS 
  BEGIN
    RETURN GL_CAMPO80 || 
          GL_CAMPO81 || 
          GL_CAMPO82 || 
          GL_CAMPO83 || 
          GL_CAMPO84 || 
          GL_CAMPO85 || 
          GL_CAMPO86 ||
          GL_CAMPO87 ||
          GL_CAMPO88 || 
          GL_CAMPO89 || 
          GL_CAMPO90 || 
          GL_CAMPO91 || 
          GL_CAMPO92 || 
          GL_CAMPO93 || 
          GL_CAMPO94 || 
          PCK_SYSMAN_UTL.FC_STRZERO(GL_CAMPO95, 9) ||
          PCK_SYSMAN_UTL.FC_STRZERO(GL_CAMPO96, 3) || 
          GL_CAMPO97;
  END FC_CONCATENARCAMPSO80A97;

  FUNCTION FC_CAMPOPARAFISCAL(
      UN_I              IN NUMBER,
      UN_TIPO           IN VARCHAR2,
      UN_CLASEREGISTRO  IN VARCHAR2,
      UN_TIPOPLANILLA  IN VARCHAR2
      )
  RETURN VARCHAR2
  AS
    MI_BASE NUMBER;
    MI_PARAMETRO NUMBER;
  BEGIN
    IF UN_CLASEREGISTRO  IN ('LNF', 'COM')  THEN
      RETURN '0.00000';
    END IF;
    IF UN_TIPO = 'CAJAS' THEN
      MI_PARAMETRO := PCK_PARENTR.PARAMETRO47;
      IF UN_CLASEREGISTRO IN('NOV') AND GL_INCAPACIDAD30 THEN --JM 31/07/2024 7741502 --MOD JM CC 2121 (regalo de mi mismo para mi mismo, con la nueva variable GL_INCAPACIDAD30 soluciono todo) 
        MI_BASE      := GL_CN101AP;
      ELSE
        IF  PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD IN (1,2,3) THEN --CFBARRERA CC:767_11/02/2025
            RETURN '0.00000';
        ELSE 
          MI_BASE      := PCK_NOMINA_COM5.CSOI(UN_I).CAJAS;
        END IF;
      END IF;
    ELSIF UN_TIPO = 'SENA' THEN
      MI_PARAMETRO := PCK_PARENTR.PARAMETRO46;
      IF UN_CLASEREGISTRO IN('NOV', 'NOVF') AND GL_INCAPACIDAD30 THEN --JM 31/07/2024 7741502 --MOD JM CC 2121 (regalo de mi mismo para mi mismo, con la nueva variable GL_INCAPACIDAD30 soluciono todo) 
        MI_BASE      := GL_CN103AP;
      ELSE
        IF  PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD IN (1,2,3) THEN --CFBARRERA CC:767_11/02/2025
            RETURN '0.00000';
        ELSE 
            MI_BASE      := PCK_NOMINA_COM5.CSOI(UN_I).SENA;
        END IF;
      END IF;
    ELSIF UN_TIPO = 'ICBF' THEN
      MI_PARAMETRO := PCK_PARENTR.PARAMETRO45;
      IF UN_CLASEREGISTRO IN('NOV', 'NOVF') AND GL_INCAPACIDAD30 THEN --JM 31/07/2024 7741502 --MOD JM CC 2121 (regalo de mi mismo para mi mismo, con la nueva variable GL_INCAPACIDAD30 soluciono todo) 
        MI_BASE      := GL_CN102AP;
      ELSE
        IF  PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD IN (1,2,3) THEN --CFBARRERA CC:767_11/02/2025
            RETURN '0.00000';
        ELSE 
          MI_BASE      := PCK_NOMINA_COM5.CSOI(UN_I).ICBF;
        END IF;
      END IF;
    ELSIF UN_TIPO = 'ESAP' THEN
      MI_PARAMETRO := PCK_PARENTR.PARAMETRO48;
      IF UN_CLASEREGISTRO IN('NOV', 'NOVF') AND GL_INCAPACIDAD30 THEN --JM 31/07/2024 7741502 --MOD JM CC 2121 (regalo de mi mismo para mi mismo, con la nueva variable GL_INCAPACIDAD30 soluciono todo) 
        MI_BASE      := GL_CN104AP;
      ELSE
        IF  PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD IN (1,2,3) THEN --CFBARRERA CC:767_11/02/2025
            RETURN '0.00000';
        ELSE 
          MI_BASE      := PCK_NOMINA_COM5.CSOI(UN_I).ESAP;
        END IF;
      END IF;
    ELSIF UN_TIPO = 'INSTITUTOS' THEN
      MI_PARAMETRO := PCK_PARENTR.PARAMETRO49;
      IF UN_CLASEREGISTRO IN('NOV', 'NOVF') AND GL_INCAPACIDAD30 THEN --JM 31/07/2024 7741502 --MOD JM CC 2121 (regalo de mi mismo para mi mismo, con la nueva variable GL_INCAPACIDAD30 soluciono todo) 
        MI_BASE      := GL_CN105AP;
      ELSE
        IF  PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD IN (1,2,3) THEN --CFBARRERA CC:767_11/02/2025
            RETURN '0.00000';
        ELSE 
          MI_BASE      := PCK_NOMINA_COM5.CSOI(UN_I).INSTITUTOS;
        END IF;
      END IF;
    END IF;
	--IF (PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION  IN('12', '19') 
    --OR MI_BASE    = 0  
    --OR UN_TIPOPLANILLA = 'K'
    --OR GL_EXONERADO)
    --AND PCK_NOMINA.GL_OPERADOR <> 'NUEVOSOI'
    --THEN	
	---<TAR:7702452 FECHA:24/01/2022 AUTOR:CP> -- se quita condicion exonerados 
    IF (PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION  IN('12', '19') 
     OR MI_BASE    = 0  
     OR UN_TIPOPLANILLA = 'K')
					 
      AND PCK_NOMINA.GL_OPERADOR <> 'NUEVOSOI'
    THEN
        IF PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION NOT IN('12', '19') 
        THEN
          IF  GL_EXONERADO = FALSE AND UN_TIPO  IN('SENA', 'ICBF') AND UN_CLASEREGISTRO NOT IN('NOV') THEN
            RETURN TRIM(TO_CHAR(NVL(MI_PARAMETRO, 4) / 100, '0.00000'));
          ELSE
            RETURN '0.00000';
          END IF;
        ELSE 
            RETURN '0.00000';
        END IF;
    ELSE
      RETURN TRIM(TO_CHAR(NVL(MI_PARAMETRO, 4) / 100, '0.00000'));
    END IF;
  END FC_CAMPOPARAFISCAL;

PROCEDURE PR_REVISARFSPYADICONAL(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_I              IN NUMBER,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES            IN PCK_SUBTIPOS.TI_MES
    )
AS
  MI_RETIRADOS NUMBER;
  MI_PARAMETRO1990APLICA          PARAMETRO.VALOR%TYPE;
BEGIN
  MI_PARAMETRO1990APLICA := UPPER(PCK_PARST.FC_PAR('APLICAR NUEVO REDONDEO DECRETO 1990 FC_ROUND_100', 'NO')) ;
  IF PCK_NOMINA.GL_CALCULAFSP = 'SI' THEN
      IF PCK_NOMINA_COM5.CSOI(UN_I).FSP <> 0 THEN
        GL_FSPPLANILLA := (PCK_NOMINA_COM5.CSOI(UN_I).FSP / 2);
        GL_FSPADICPLANILLA := (PCK_NOMINA_COM5.CSOI(UN_I).FSP / 2) + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL;
        IF (PCK_NOMINA_COM5.CSOI(UN_I).FSP / 2) <> PCK_SYSMAN_UTL.FC_ROUND_100(((PCK_NOMINA_COM5.CSOI(UN_I).BASE * 1 / 100) / 2) , MI_PARAMETRO1990APLICA,(- 0.49 + PCK_NOMINA.GL_SUMARSS1990)
                                                                      , PCK_NOMINA.GL_RAPORTES1990) THEN
          IF PCK_NOMINA.GL_OPERADOR IN('COMCAJA', 'ASOPAGOS') THEN
            GL_FSPPLANILLA := PCK_SYSMAN_UTL.FC_ROUND_100(((PCK_NOMINA_COM5.CSOI(UN_I).BASE * 1 / 100) / 2) , MI_PARAMETRO1990APLICA, 0.49 + PCK_NOMINA.GL_SUMARSS1990
                                                     , PCK_NOMINA.GL_RAPORTES1990);
            GL_FSPADICPLANILLA := PCK_SYSMAN_UTL.FC_ROUND_100( ((PCK_NOMINA_COM5.CSOI(UN_I).BASE * 1 / 100) / 2) , MI_PARAMETRO1990APLICA, 0.49 + PCK_NOMINA.GL_SUMARSS1990
                                                         , PCK_NOMINA.GL_RAPORTES1990) + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL;
          END IF;
          IF PCK_NOMINA_COM5.CSOI(UN_I).ESTADO_ACTUAL =1 THEN
            SELECT COUNT(HISTORICOS.COMPANIA)
            INTO MI_RETIRADOS
            FROM PERSONAL INNER JOIN HISTORICOS
             ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA 
             AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
            INNER JOIN PERIODOS 
              ON HISTORICOS.COMPANIA      = PERIODOS.COMPANIA
             AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO 
             AND HISTORICOS.ANO           = PERIODOS.ANO 
             AND HISTORICOS.MES           = PERIODOS.MES 
             AND HISTORICOS.PERIODO       = PERIODOS.PERIODO
            WHERE PERSONAL.COMPANIA       = UN_COMPANIA 
              AND PERSONAL.ID_DE_EMPLEADO <> PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
              AND PERSONAL.NUMERO_DCTO    = PCK_NOMINA_COM5.CSOI(UN_I).NUMERO_DCTO
              AND HISTORICOS.ANO          = UN_ANIO 
              AND HISTORICOS.MES          = UN_MES
              AND HISTORICOS.ID_DE_CONCEPTO IN('112', '132','115')
              AND PERIODOS.ACUMULADO      NOT IN(0);
            IF MI_RETIRADOS>0 THEN
              GL_FSPPLANILLA := PCK_SYSMAN_UTL.FC_ROUND_100((PCK_NOMINA_COM5.CSOI(UN_I).FSP / 2) , MI_PARAMETRO1990APLICA, 0.49 + PCK_NOMINA.GL_SUMARSS1990
                                                       ,  PCK_NOMINA.GL_RAPORTES1990);
              GL_FSPADICPLANILLA := GL_FSPPLANILLA + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL;
            END IF;
          END IF;
        END IF;
      ELSE
        GL_FSPPLANILLA     := PCK_NOMINA_COM5.CSOI(UN_I).BASE * PCK_NOMINA_COM5.CSOI(UN_I).FSP;
        GL_FSPADICPLANILLA := PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL;
      END IF;
    ELSE
      IF PCK_NOMINA_COM5.CSOI(UN_I).FSP <> 0 THEN
        GL_FSPPLANILLA := PCK_SYSMAN_UTL.FC_ROUND_100(((PCK_NOMINA_COM5.CSOI(UN_I).BASE * 1 / 100) / 2) , MI_PARAMETRO1990APLICA,(- 0.49 + PCK_NOMINA.GL_SUMARSS1990)
                                                 , PCK_NOMINA.GL_RAPORTES1990);
        GL_FSPADICPLANILLA := PCK_SYSMAN_UTL.FC_ROUND_100(((PCK_NOMINA_COM5.CSOI(UN_I).BASE * 1 / 100) / 2) , MI_PARAMETRO1990APLICA,(- 0.49 + PCK_NOMINA.GL_SUMARSS1990)
                                                     , PCK_NOMINA.GL_RAPORTES1990) + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL;
      ELSE
        GL_FSPPLANILLA     := PCK_NOMINA_COM5.CSOI(UN_I).BASE * PCK_NOMINA_COM5.CSOI(UN_I).FSP;
        GL_FSPADICPLANILLA := PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL;
      END IF;
  END IF;
END;

FUNCTION FC_TIPO_INCAPACIDAD_FECHAS(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_IDEMPLEADO     IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
  UN_FECHAINI       IN DATE,
  UN_FECHAFIN       IN DATE
)
RETURN VARCHAR2
AS
  MI_RETORNO VARCHAR2(30);
BEGIN 

  SELECT TABLA.INCAPACIDAD
  INTO MI_RETORNO
  FROM (
    SELECT INCAPACIDAD     
    FROM INCAPACIDADES
    WHERE COMPANIA       = UN_COMPANIA
      AND FECHA_INICIO   = UN_FECHAINI
      AND FECHA_FIN      = UN_FECHAFIN 
      AND ID_DE_EMPLEADO = UN_IDEMPLEADO    
  ) TABLA
  WHERE ROWNUM =1;
  RETURN MI_RETORNO;
  EXCEPTION WHEN OTHERS THEN
  RETURN '';
END;

PROCEDURE PR_REVISARSUELDO(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_I              IN NUMBER,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES            IN PCK_SUBTIPOS.TI_MES,
    UN_RETROACTIVO    IN PCK_SUBTIPOS.TI_LOGICO
    )
AS
  MI_MSGERROR PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
  IF PCK_NOMINA.GL_TIPONOMINA IN('ACTIVOS','') THEN
    GL_SUELDO := PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE;
    IF GL_SUELDO=0 AND PCK_NOMINA_COM5.CSOI(UN_I).ESTADO_ACTUAL =2 THEN
      GL_SUELDO := PCK_NOMINA_COM5.CSOI(UN_I).MESADA_PENSIONAL;
    END IF;
  ELSE
    GL_SUELDO := PCK_NOMINA_COM5.CSOI(UN_I).MESADA_PENSIONAL;
  END IF;
  GL_SUELDOP := PCK_SYSMAN_UTL.FC_STRZERO(GL_SUELDO, 9);
  GL_NUMDIASP := 0;
  GL_NUMDIAS  := PCK_SYSMAN_UTL.FC_STRZERO(
                  PCK_NOMINA.FC_CNA(9)   + PCK_NOMINA.FC_CNA(11)  + PCK_NOMINA.FC_CNA(35) 
                + PCK_NOMINA.FC_CNA(350) + PCK_NOMINA.FC_CNA(351) + PCK_NOMINA.FC_CNA(352) 
                + PCK_NOMINA.FC_CNA(353) + PCK_NOMINA.FC_CNA(354) + PCK_NOMINA.FC_CNA(355) 
                + PCK_NOMINA.FC_CNA(358) + PCK_NOMINA.FC_CNA(368) + PCK_NOMINA.FC_CNA(360) 
                + PCK_NOMINA.FC_CNA(336) + PCK_NOMINA.FC_CNA(363) + PCK_NOMINA.FC_CNA(364) 
                + PCK_NOMINA.FC_CNA(365) + PCK_NOMINA.FC_CNA(390) + PCK_NOMINA.FC_CNA(339)
                ,2) ;
  GL_NUMDIAS  := CASE WHEN TO_NUMBER(GL_NUMDIAS) < 0
                 THEN '0'
                 ELSE GL_NUMDIAS
                 END;
  GL_NUMDIAS  := CASE WHEN TO_NUMBER(GL_NUMDIAS) > 30
                 THEN '30'
                 ELSE GL_NUMDIAS
                 END;
  GL_NUMDIASS := GL_NUMDIAS ;
  IF  TO_NUMBER(GL_NUMDIAS) > 0 AND PCK_NOMINA_COM5.CSOI(UN_I).DIASRIESGOS > TO_NUMBER(GL_NUMDIAS) Then
    GL_NUMDIAS := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).DIASRIESGOS, 2);
    MI_MSGERROR(1).CLAVE := 'EMPLEADO';
    MI_MSGERROR(1).VALOR := PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO || ' ' || PCK_NOMINA_COM5.CSOI(UN_I).APELLIDO1 || ' ' || PCK_NOMINA_COM5.CSOI(UN_I).APELLIDO2 || ' ' || PCK_NOMINA_COM5.CSOI(UN_I).NOMBRES; 
    PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => UN_COMPANIA,
                               UN_MENSAJE_COD => PCK_ERRORES.ALER_MENOR30_SIN_NOVEDAD,
                               UN_REEMPLAZOS  => MI_MSGERROR);    
  END IF;
  GL_NUMDIAS := CASE WHEN TO_NUMBER(GL_NUMDIAS) = 0 
                  AND (UN_RETROACTIVO= -1 OR PCK_NOMINA.FC_CNA(306) <> 0)
                THEN 30
                ELSE GL_NUMDIAS END;
  GL_NUMDIAS  := CASE WHEN TO_NUMBER(GL_NUMDIAS) > 30
                 THEN '30'
                 ELSE GL_NUMDIAS
                 END;
  GL_NUMDIASP := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA.FC_CNA(9)   + PCK_NOMINA.FC_CNA(11) 
                                         + PCK_NOMINA.FC_CNA(35)  + PCK_NOMINA.FC_CNA(350) 
                                         + PCK_NOMINA.FC_CNA(351) + PCK_NOMINA.FC_CNA(352) 
                                         + PCK_NOMINA.FC_CNA(353) + PCK_NOMINA.FC_CNA(354) 
                                         + PCK_NOMINA.FC_CNA(355) + PCK_NOMINA.FC_CNA(358) 
                                         + PCK_NOMINA.FC_CNA(368) + PCK_NOMINA.FC_CNA(363) 
                                         + PCK_NOMINA.FC_CNA(364) + PCK_NOMINA.FC_CNA(365) 
                                         + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CNA(390)
                                          , 2);
  IF PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL > 0 AND 
     PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL < 
     PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     =>GL_SUELDO
                            ,UN_PRECISION => PCK_NOMINA.GL_RAPORTES1990) THEN
    GL_NUMDIASP := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA.FC_CNA(9)  + PCK_NOMINA.FC_CNA(11) 
                                           + PCK_NOMINA.FC_CNA(363) + PCK_NOMINA.FC_CNA(364) 
                                           + PCK_NOMINA.FC_CNA(365)
                                           , 2);
  END IF;
  GL_NUMDIASP  := CASE WHEN GL_NUMDIASP < 0
                  THEN 0
                  ELSE PCK_SYSMAN_UTL.FC_STRZERO(GL_NUMDIASP, 2)
                  END;
  GL_NUMDIASP  := CASE WHEN TO_NUMBER(GL_NUMDIASP) > 30
                  THEN '30'
                  ELSE GL_NUMDIASP
                  END;
  IF GL_NUMDIASP < 30 AND GL_NVAC = 'X' Then
    GL_NUMDIASP := CASE WHEN GL_NUMDIASP < 0
                   THEN 0
                   ELSE PCK_SYSMAN_UTL.FC_STRZERO(GL_NUMDIAS, 2)
                   END ;
  END IF;
  GL_DIASPARAFISCALES := GL_NUMDIAS;
  IF PCK_NOMINA.GL_DIASIGUALPARA = 'SI' Then
    GL_NUMDIASP := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA.FC_CNA(9) + PCK_NOMINA.FC_CNA(11) 
                                           + PCK_NOMINA.FC_CNA(363) + PCK_NOMINA.FC_CNA(364) 
                                           + PCK_NOMINA.FC_CNA(365) + PCK_NOMINA.FC_CNA(339)
                                          , 2);
    GL_NUMDIASP  := CASE WHEN TO_NUMBER(GL_NUMDIASP) > 30
                  THEN '30'
                  ELSE GL_NUMDIASP
                  END;
    IF TO_NUMBER(GL_NUMDIASP) = 0 AND 
       TO_NUMBER(GL_NUMDIAS)  > 0 AND 
       PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL > 0 AND 
       PCK_NOMINA_COM5.CSOI(UN_I).CAJAS > 0 Then
         GL_NUMDIASP := GL_NUMDIAS;
    END IF;
  ELSE
    GL_NUMDIASP := PCK_SYSMAN_UTL.FC_STRZERO(GL_NUMDIASP, 2);
  END IF;
  IF PCK_NOMINA.FC_CNA(35) >= 30 AND 
     TO_NUMBER(GL_DIASPARAFISCALES) <= 0 AND 
     PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL > 0 AND 
     PCK_NOMINA_COM5.CSOI(UN_I).CAJAS > 0 Then
    GL_NUMDIASP         := GL_NUMDIAS;
    GL_DIASPARAFISCALES := GL_NUMDIAS;
  END IF;
END PR_REVISARSUELDO;

FUNCTION FC_SUMARORSTAR1PESO(
    UN_VALOR1   IN NUMBER,
    UN_VALOR2  IN NUMBER)
RETURN NUMBER
AS
BEGIN
  IF (UN_VALOR1 - UN_VALOR2) = 1 THEN
    RETURN 1;
  ELSIF (UN_VALOR1 - UN_VALOR2) = -1 THEN
    RETURN -1;
  ELSE
    RETURN 0;
  END IF;
END FC_SUMARORSTAR1PESO;

PROCEDURE PR_REGISTROS_LNR(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_I              IN NUMBER,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES            IN PCK_SUBTIPOS.TI_MES,
    UN_PNSLN          IN VARCHAR2, 
    UN_TIPOESTRUCTURA IN VARCHAR2,
    UN_TIPOPLANILLA  IN VARCHAR2
    )
AS
  MI_CAMPOSNUEVOS2388 CLOB;
BEGIN
  GL_BASESALUD := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).BASE, 9);
  IF PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION = '51' Then
    GL_NVSP := ' ';
    GL_NVST := ' ';
    GL_NLMA := ' ';
    GL_NIGE := ' ';
  END IF;

  MI_CAMPOSNUEVOS2388:='';
  IF UN_TIPOESTRUCTURA= '2388' THEN
    MI_CAMPOSNUEVOS2388 := PCK_NOMINA_COM5.FC_CAMPOSNUEVOS2388INGRET(UN_ANNO     => UN_ANIO
                                                         ,UN_MMES     => UN_MES
                                                         ,UN_PNING    => GL_NING
                                                         ,UN_PNRET    => GL_NRET
                                                         ,UN_PNVST    => GL_NVST
                                                         ,UN_DIASTRABAJADOS => PCK_NOMINA.FC_CNA(9) +PCK_NOMINA.FC_CNA(11) 
                                                         ,UN_I              => UN_I
                                                         ,UN_RBASE1990      => PCK_NOMINA.GL_RBASE1990 
                                                         ,UN_HORASMENSUALES => PCK_NOMINA.GL_HORASMENSUALES
                                                         ,UN_DIASSEMANALES  => PCK_NOMINA.GL_DIASSEMANALES
                                                         );  
  END IF;  
  GL_CTAR := PCK_NOMINA_SEGSOCI.FC_PORCENRIESGO(UN_COMPANIA => UN_COMPANIA
                                               ,UN_IDEMPLEADO => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO) / 100;
  PCK_NOMINA_COM5.GL_DISCOLIQUIDACION :=  PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                         FC_REGISTROPLANO (UN_COMPANIA         => UN_COMPANIA,
                                           UN_I                => UN_I,
                                           UN_TIPOESTRUCTURA   => UN_TIPOESTRUCTURA,
                                           UN_CLASEREGISTRO    => 'LNR',
                                           UN_TIPOPLANILLA    => UN_TIPOPLANILLA,
                                           UN_CAMPOSNUEVOS2388 => MI_CAMPOSNUEVOS2388);
  /*
  IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) 
    + PCK_NOMINA.FC_CNA(359)) >= 30 
    OR (dr = PCK_NOMINA.FC_CNA(208) 
    OR PCK_NOMINA_COM5.CSOI(UN_I).ESTADOACTUAL = 3 
    AND PCK_NOMINA.FC_CNA(9) = 0) THEN 
  ELSE
    IF GENERARNOVEDADESPILA2388 = 0 THEN
      Print #1, PART1 & PART2
      INCLUIR_NOVEDADES_PILAPARTES Getcompany(), GetProceso(), [Forms]![FORMULARIO_INTEGRADO_ELECTRONICO]![Ano1], [Forms]![FORMULARIO_INTEGRADO_ELECTRONICO]![Mes1], "03", PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO, Val(Contador), PART1 & PART2
      Contador := Contador + 1;
    END IF;
  END IF;
  */
  PCK_NOMINA.GL_STRETAPAGLOBAL := '7 imprime registro 02 lnr';
  GL_DIASPA := '00';
  GL_NUMDIAS:= '00';
  GL_BASESALUD := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).BASESALUD, 9);
  IF PCK_NOMINA_COM5.CSOI(UN_I).DIASSALUD <> 0 THEN
    GL_NUMDIASS:= PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).DIASLNR, 2);
  END IF;
  IF TO_NUMBER(GL_NUMDIASS) > 30 THEN
    GL_NUMDIASS := '30';
  END IF;
  GL_CTAR := 0;

  IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359)) >= 30  THEN
    IF PCK_NOMINA_COM5.CSOI(UN_I).DIASSALUD >= 30
       AND TO_NUMBER(GL_BASESALUD) < PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_PARENTR.PARAMETRO20
                                                            ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990) THEN
      GL_BASESALUD := PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_PARENTR.PARAMETRO20
                                                                       ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990), 9);
    END IF;
    GL_DIASPA  := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).DIASLNR, 2) ;
    GL_NUMDIAS := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).DIASLNR, 2) ;
  END IF;
  IF GL_DIASPA < GL_NUMDIAS Then 
     GL_DIASPA := GL_NUMDIAS;
  END IF;
  GL_FSPPLANILLA     := 0;
  GL_FSPADICPLANILLA := 0;    
  GL_NIRP :='00';
  GL_NSLN := CASE WHEN PCK_NOMINA.FC_CNA(356) <> 0 OR PCK_NOMINA.FC_CNA(357) <> 0 OR PCK_NOMINA.FC_CNA(359) <> 0 THEN 'X' ELSE ' ' END;
  IF UN_TIPOESTRUCTURA= '2388' THEN
    MI_CAMPOSNUEVOS2388 := FC_CAMPOSNUEVOS2388_LNR(UN_COMPANIA       => UN_COMPANIA
                                                  ,UN_I              => UN_I
                                                  ,UN_ANNO           => UN_ANIO
                                                  ,UN_MMES           => UN_MES
                                                  ,UN_PNSLN          => UN_PNSLN);
  END IF;
  PCK_NOMINA_COM5.GL_DISCOLIQUIDACION :=  PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                         FC_REGISTROPLANO (UN_COMPANIA         => UN_COMPANIA,
                                           UN_I                => UN_I,
                                           UN_TIPOESTRUCTURA   => UN_TIPOESTRUCTURA,
                                           UN_CLASEREGISTRO    => 'LNF',
                                           UN_TIPOPLANILLA    => UN_TIPOPLANILLA,
                                           UN_CAMPOSNUEVOS2388 => MI_CAMPOSNUEVOS2388);


END PR_REGISTROS_LNR;

FUNCTION FC_CAMPOSNUEVOS2388_LNR(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
		UN_ANNO           IN PCK_SUBTIPOS.TI_ANIO,
		UN_MMES           IN PCK_SUBTIPOS.TI_MES, 
		UN_PNSLN          IN VARCHAR2, 
		UN_I              IN NUMBER
    )
RETURN CLOB
 /*
    NAME              : TRAERNOVEDADES_CAMPOSNUEVOS2388_LNR  --> EN ACCESS Relacionado  -
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : JOSE PASCUAL GOMEZ
    DATE MIGRATION    : 29/12/2017
    TIME              : 09:11 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    @NAME:   
  */
    --UN_I es la posiciÂ¿el vector para consultar PCK_NOMINA_COM5.CSOI;
    --     que se debe enviar desde la funciÂ¿R_SISTEMAINTEGRADOELECTRONICO
	AS
  MI_FINICIO DATE;
  MI_FFINAL  DATE;
BEGIN
  PR_LIMPIARCAMPSO80A97();
  IF UN_PNSLN<> ' ' THEN
    MI_FINICIO := TO_DATE('01/' || PCK_SYSMAN_UTL.FC_STRZERO(UN_MMES,2) || '/' || PCK_SYSMAN_UTL.FC_STRZERO(UN_ANNO,2),'DD/MM/YYYY');
    MI_FFINAL := LAST_DAY(MI_FINICIO);   
    FOR RS IN(SELECT FECHA_INICIO, 
                     FECHA_FINAL
              FROM LICENCIAS 
              WHERE COMPANIA       = UN_COMPANIA 
                AND ID_DE_EMPLEADO = PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
                AND LICENCIA IN('01','02','03')
                AND FECHA_INICIO  <= MI_FFINAL 
                AND FECHA_FINAL   >= MI_FINICIO)
    LOOP 
      IF RS.FECHA_INICIO >= MI_FINICIO THEN
        MI_FINICIO := RS.FECHA_INICIO;
      END IF;
      IF RS.FECHA_FINAL <= MI_FFINAL THEN
        MI_FFINAL := RS.FECHA_FINAL;
      END IF;
    END LOOP;
    GL_CAMPO83 := TO_CHAR(MI_FINICIO, 'YYYY-MM-DD');
    GL_CAMPO84 := TO_CHAR(MI_FFINAL, 'YYYY-MM-DD');
  END IF;
  IF PCK_NOMINA.GL_OPERADOR ='SOI.COM.CO' THEN
    GL_CAMPO96 := '240';
  END IF;
  RETURN FC_CONCATENARCAMPSO80A97;
END FC_CAMPOSNUEVOS2388_LNR;

PROCEDURE PR_COMISION_SOI(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_I              IN NUMBER,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES            IN PCK_SUBTIPOS.TI_MES,
    UN_PNSLN          IN VARCHAR2, 
    UN_TIPOESTRUCTURA IN VARCHAR2,
    UN_TIPOPLANILLA  IN VARCHAR2
    )
AS
  MI_CAMPOSNUEVOS2388 CLOB;
BEGIN
  GL_BASESALUD := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).BASE, 9);
  GL_NUMDIAS   := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA.FC_CNA(339), 2);
  IF TO_NUMBER(GL_NUMDIAS) = 0 THEN
    GL_NUMDIAS := '30';
  END IF;
  IF TO_NUMBER(GL_NUMDIASS) = 0 THEN
    GL_NUMDIASS := '30';
  END IF;
  GL_NSLN :='C';
  GL_DIASPA := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA.FC_CNA(339), 2) ;
  GL_BASESALUD := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).BASESALUD, 9);
  GL_BASESALUD := CASE WHEN GL_BASESALUD = 0
                  THEN PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC
                                                            ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990)
                  ELSE GL_BASESALUD
                  END;
  IF PCK_NOMINA_COM5.CSOI(UN_I).DIASSALUD <> 0 THEN
     GL_NUMDIASS := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).DIASSALUD, 2);
     GL_NUMDIASS := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA.FC_CNA(339), 2);
  END IF;
  IF TO_NUMBER(GL_NUMDIASS) > 30 THEN
     GL_NUMDIASS := '30';
  END IF;
  GL_CTAR :=0;
  GL_FSPPLANILLA := 0;
  /*
  IF PCK_NOMINA_COM5.CSOI(UN_I).ESTADO_ACTUAL = 6 THEN
     NSln := "C";
  END IF;
  */
  IF PCK_NOMINA.GL_OPERADOR IN('SOI.COM.CO') THEN  
    GL_NUMDIASS := '30';
    GL_DIASPA   := '30';
	  GL_NUMDIASS := '30';
  END IF;

  MI_CAMPOSNUEVOS2388:='';
  IF UN_TIPOESTRUCTURA= '2388' THEN
    GL_CAMPO83 := TO_CHAR(TO_DATE('01/' || UN_MES || '/' || UN_ANIO, 'DD/MM/YYYY'), 'YYYY-MM-DD');
    GL_CAMPO84 := TO_CHAR(LAST_DAY(TO_DATE('01/' || UN_MES || '/' || UN_ANIO, 'DD/MM/YYYY')), 'YYYY-MM-DD');
    IF PCK_NOMINA.GL_OPERADOR IN('SOI.COM.CO') THEN  
      GL_CAMPO95 := GL_BASESALUD;
    END IF;
    MI_CAMPOSNUEVOS2388 := PCK_NOMINA_COM5.FC_CAMPOSNUEVOS2388INGRET(UN_ANNO     => UN_ANIO
                                                         ,UN_MMES     => UN_MES
                                                         ,UN_PNING    => GL_NING
                                                         ,UN_PNRET    => GL_NRET
                                                         ,UN_PNVST    => GL_NVST
                                                         ,UN_DIASTRABAJADOS => PCK_NOMINA.FC_CNA(9) +PCK_NOMINA.FC_CNA(11) 
                                                         ,UN_I              => UN_I
                                                         ,UN_RBASE1990      => PCK_NOMINA.GL_RBASE1990 
                                                         ,UN_HORASMENSUALES => PCK_NOMINA.GL_HORASMENSUALES
                                                         ,UN_DIASSEMANALES  => PCK_NOMINA.GL_DIASSEMANALES
                                                         );      
  END IF;  
  PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                          FC_REGISTROPLANO (UN_COMPANIA         => UN_COMPANIA,
                                           UN_I                => UN_I,
                                           UN_TIPOESTRUCTURA   => UN_TIPOESTRUCTURA,
                                           UN_CLASEREGISTRO    => 'COM',
                                           UN_TIPOPLANILLA    => UN_TIPOPLANILLA,
                                           UN_CAMPOSNUEVOS2388 => MI_CAMPOSNUEVOS2388);
END PR_COMISION_SOI;

PROCEDURE PR_UPCADICIONAL(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_EMPLEADO       IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_VALORUPC       IN NUMBER ,
    UN_CODAIFS        IN VARCHAR2,
    UN_I              IN NUMBER
    )
    /*
    NAME              : TRAERNOVEDADES_CAMPOSNUEVOS2388INGRET  --> EN ACCESS Relacionado  -
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : JOSE PASCUAL GOMEZ
    DATE MIGRATION    : 29/12/2017
    TIME              : 09:11 AM
    SOURCE MODULE     : 
    MODIFIER          : JOSÂ¿PASCUAL GÂ¿EZ BLANCO
    DATE MODIFIED     : 15/08/2018
    TIME              : 10:51
    DESCRIPTION       : Se ajusta los siguientes campos:
                        El CAMPO77 reporte quemado 6 espacios en blanco                        
                        El CAMPO36 reporte quemado 00                        
                        El CAMPO74 reporte el tipo de identidad del empleado al cual esta asociado el beneficiario                        
                        El CAMPO75 reporte la identidad del empleado al cual esta asociado el beneficiario
    @NAME:   
  */
AS
  MI_PART1      CLOB;
  MI_TIPODCTO   VARCHAR2(2);
  MI_TIPODCTOEMPL VARCHAR2(2);
  MI_AAPELLIDO1 VARCHAR2(20);
  MI_AAPELLIDO2 VARCHAR2(30);
  MI_NOMBRE1    VARCHAR2(20);
  MI_NOMBRE2    VARCHAR2(30);
BEGIN 
  IF (PCK_NOMINA.GL_PAGOUPC='SI' OR PCK_NOMINA_COM5.GL_UPCADICIONALPILA = 'SI') AND UN_VALORUPC>0 THEN
    SELECT *
    BULK  COLLECT INTO CBENEFICIARIOS_UPC
    FROM BENEFICIARIOS_UPC
    WHERE COMPANIA       = UN_COMPANIA
      AND ID_DE_EMPLEADO = UN_EMPLEADO
      AND ESTADO_ACTUALUPC <> 3;
    IF PCK_NOMINA_COM5.CBENEFICIARIOS_UPC.COUNT>0 THEN
      FOR I IN  CBENEFICIARIOS_UPC.FIRST .. CBENEFICIARIOS_UPC.LAST 
      LOOP 
        GL_CONTADOR    := GL_CONTADOR + 1;
        MI_TIPODCTO   := PCK_NOMINA_COM5.FC_DCTIDENTIDAD(UN_TIPO => NVL(CBENEFICIARIOS_UPC(I).TIPO_DCTO_IDENTIDAD, ''));

        MI_TIPODCTOEMPL := PCK_NOMINA_COM5.FC_DCTIDENTIDAD(UN_TIPO => NVL(PCK_NOMINA_COM5.CSOI(UN_I).DCTO_IDENTIDAD, ''));

        MI_AAPELLIDO1 := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(CBENEFICIARIOS_UPC(I).APELLIDO1B, ' '));
        MI_AAPELLIDO2 := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(CBENEFICIARIOS_UPC(I).APELLIDO2B, ' '));
        MI_NOMBRE1 := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(CBENEFICIARIOS_UPC(I).NOMBRE1B, ' '));
        MI_NOMBRE2 := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(CBENEFICIARIOS_UPC(I).NOMBRE2B, ' '));
        PR_LIMPIARCAMPSO80A97;
        PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10)
                    /*CAMPO1*/ || '02' 
                    /*CAMPO2*/ || PCK_SYSMAN_UTL.FC_STRZERO(GL_CONTADOR, 5) 
                    /*CAMPO3*/ || MI_TIPODCTO 
                    /*CAMPO4*/ || RPAD(CBENEFICIARIOS_UPC(I).IDENTIFICACION, 16,' ') 
                    /*CAMPO5*/ || '40'
                    /*CAMPO6*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 2) 
                    /*CAMPO7*/ || RPAD(' ', 1,' ') 
                    /*CAMPO8*/ || RPAD(' ', 1,' ') 
                    /*CAMPO9*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 2) 
                    /*CAMPO10*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 3) 
                    /*CAMPO11*/ || RPAD(UPPER(NVL(MI_AAPELLIDO1, MI_AAPELLIDO2)), 20,' ') 
                    /*CAMPO12*/ || RPAD(CASE WHEN MI_AAPELLIDO2 IS NULL 
                                        THEN ' ' 
                                        ELSE UPPER(MI_AAPELLIDO2) 
                                        END, 30,' ') 
                    /*CAMPO13*/ || RPAD(UPPER(MI_NOMBRE1), 20) 
                    /*CAMPO14*/ || RPAD(UPPER(MI_NOMBRE2), 30) 
                    /*CAMPO15*/ || RPAD(' ', 1,' ') 
                    /*CAMPO16*/ || RPAD(' ', 1,' ')  
                    /*CAMPO17*/ || RPAD(' ', 1,' ') 
                    /*CAMPO18*/ || RPAD(' ', 1,' ') 
                    /*CAMPO19*/ || RPAD(' ', 1,' ') 
                    /*CAMPO20*/ || RPAD(' ', 1,' ') 
                    /*CAMPO21*/ || RPAD(' ', 1,' ') 
                    /*CAMPO22*/ || RPAD(' ', 1,' ') 
                    /*CAMPO23*/ || RPAD(' ', 1,' ') 
                    /*CAMPO24*/ || RPAD(' ', 1,' ') 
                    /*CAMPO25*/ || RPAD(' ', 1,' ') 
                    /*CAMPO26*/ || RPAD(' ', 1,' ') 
                    /*CAMPO27*/ || RPAD(' ', 1,' ') 
                    /*CAMPO28*/ || RPAD(' ', 1,' ') 
                    /*CAMPO29*/ || RPAD(' ', 1,' ') 
                    /*CAMPO30*/ || GL_NIRP  
                    /*CAMPO31*/ || RPAD(' ', 6,' ')
                    /*CAMPO32*/ || RPAD(' ', 6,' ') 
                    /*CAMPO33*/ || RPAD(UN_CODAIFS, 6,' ') 
                    /*CAMPO34*/ || RPAD(' ', 6,' ')
                    /*CAMPO35*/ || RPAD(' ', 6,' ') 
                    /*CAMPO35*/ || PCK_SYSMAN_UTL.FC_STRZERO(0,2)
                    /*CAMPO36 || CASE WHEN UN_CODAIFS  = RPAD(' ', 6,' ') 
                                          OR PCK_NOMINA_COM5.CSOI(UN_I).PENSION = 0  
                                          OR PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12', '19') 
                                          OR PCK_NOMINA_COM5.CSOI(UN_I).ID_DEL_FONDO = 'AFP99' 
                                  THEN PCK_SYSMAN_UTL.FC_STRZERO(0,2)
                                  ELSE PCK_SYSMAN_UTL.FC_STRZERO(GL_NUMDIAS,2)
                                  END
                     */             
                    /*CAMPO37*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 2) --(JORDUZ 21/08/2020) Se Cambia de 30 a 0 ya que los beneficiarios no deben mostrar dias cotizados
                    /*CAMPO38*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 2)                                
                    /*CAMPO39*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 2) 
                    /*CAMPO40*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9) 
                    /*CAMPO41*/ || RPAD(' ', 1,' ')  
                    /*CAMPO42*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9)
                    /*CAMPO43*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9)
                    /*CAMPO44*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9)
                    /*CAMPO45*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9)
                    /*CAMPO46*/ || '0.00000'
                    /*CAMPO47*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9) 
                    /*CAMPO48*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9)
                    /*CAMPO49*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9) 
                    /*CAMPO50*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9) 
                    /*CAMPO51*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9) 
                    /*CAMPO52*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9) 
                    /*CAMPO53*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9)
                    /*CAMPO54*/ || '0.00000'
                    /*CAMPO55*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9)
                    /*CAMPO56*/ || PCK_SYSMAN_UTL.FC_STRZERO(CBENEFICIARIOS_UPC(I).VALORUPC, 9) 
                    /*CAMPO57*/ || RPAD( ' ', 15, ' ') 
                    /*CAMPO58*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9) 
                    /*CAMPO59*/ || RPAD( ' ', 15, ' ')  
                    /*CAMPO60*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9)
                    /*CAMPO61*/ || '0.0000000'
                    /*CAMPO62*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9)
                    /*CAMPO63*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9) 
                    /*CAMPO64*/ || '0.00000'
                    /*CAMPO65*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9) 
                    /*CAMPO66*/ || '0.00000'
                    /*CAMPO67*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9)                               
                    /*CAMPO68*/ || '0.00000'
                    /*CAMPO69*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9) 
                    /*CAMPO70*/ || '0.00000'
                    /*CAMPO71*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9) 
                    /*CAMPO72*/ || '0.00000'
                    /*CAMPO73*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9) 
                    /*CAMPO74*/ || RPAD(MI_TIPODCTOEMPL, 2,' ')
                    /*CAMPO75*/ || RPAD(PCK_NOMINA_COM5.CSOI(UN_I).NUMERO_DCTO, 16,' ') 
                    /*CAMPO76*/ || CASE WHEN GL_EXONERADO 
                                              AND PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION NOT IN('12', '19', '23', '51')
                                        THEN  'S' ELSE 'N' END 
                    /*CAMPO77*/ || RPAD(' ', 6,' ')
                    /*CAMPO77 || CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_TIPO = '95' 
                                          OR PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12')
                                   THEN RPAD('1', 6,' ')
                                   ELSE GL_CODIGOFONDORIESGOS 
                                   END
                    */
                    /*CAMPO78*/ || RPAD(CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_TIPO = '95' 
                                                OR PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12') 
                                         THEN ' '
                                         ELSE 
                                         --(APINEDA:17/07/2019)-Se agregan parametros a llamado de funciÃ³n FC_SUCURSALRIESGON para PR_UPCADICIONAL
                                         PCK_NOMINA_COM5.FC_SUCURSALRIESGON(UN_COMPANIA   => UN_COMPANIA
                                                                               ,UN_EMPLEADO   => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO
                                                                               ,UN_NUMEROPATRONAL => NULL
                                                                               ,UN_FACTOR_RIESGO => NULL
                                                                               ,UN_PAR        => 1)                                                                            
                                         END
                                   ,1,' ')
                    /*CAMPO79*/ || RPAD(NVL(PCK_NOMINA_COM5.CSOI(UN_I).ACTIVIDADES_ALTO_RIESGO,' '),1,' ')
                                || FC_CONCATENARCAMPSO80A97
                                || PCK_SYSMAN_UTL.FC_STRZERO(0, 7) ;
      END LOOP;
    END IF;
  END IF;
END PR_UPCADICIONAL;

PROCEDURE PR_REGISTROS_FINALES_AUX(
    /*
    NAME              : REGISTROS_FINALES  --> EN ACCESS Relacionado  -
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : JOSE PASCUAL GOMEZ
    DATE MIGRATION    : 25/01/2018
    TIME              : 09:11 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    @NAME:   
  */
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES            IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO        IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_PERIODO_RETRO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ADMINISTRADORA IN VARCHAR2,
    UN_TIPOESTRUCTURA IN VARCHAR2  
    )
AS
  MI_CONTADOR NUMBER;
  MI_CODA VARCHAR2(6);
BEGIN
  MI_CONTADOR :=0;
  SELECT *
    BULK  COLLECT INTO CNOVEDADES_AUTO
    FROM NOVEDADES_AUTOLIQUIDACION
    WHERE COMPANIA       = UN_COMPANIA
      AND ANO            = UN_ANIO
      AND MES            = UN_MES
      AND PERIODO        = CASE WHEN UN_PERIODO_RETRO = 5 
                           THEN CASE WHEN UN_PERIODO = 2 
                                THEN 3 
                                ELSE UN_PERIODO
                                END
                           ELSE 3 END
      AND TIPO_ADMINISTRADORA = UN_ADMINISTRADORA;

    IF PCK_NOMINA_COM5.CNOVEDADES_AUTO.COUNT>0 THEN
      FOR I IN  CNOVEDADES_AUTO.FIRST .. CNOVEDADES_AUTO.LAST 
      LOOP 
        MI_CONTADOR := MI_CONTADOR + 1;
        SELECT CODA
        INTO MI_CODA
        FROM V_FONDOS
        WHERE COMPANIA  = UN_COMPANIA
          AND COD_FONDO = CNOVEDADES_AUTO(I).CODIGO_FONDO;
        IF UN_ADMINISTRADORA = '01' THEN --PENSION
          PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                 /*CAMPO1*/ '03' 
                                 /*CAMPO2*/ || PCK_SYSMAN_UTL.FC_STRZERO(MI_CONTADOR, 5) 
                                 /*CAMPO3*/ || RPAD(MI_CODA, 6, ' ') 
                                 /*CAMPO4*/ || PCK_SYSMAN_UTL.FC_STRZERO(CNOVEDADES_AUTO(I).NIT,16)         
                                 /*CAMPO5*/ || TO_CHAR(PCK_SYSMAN_UTL.FC_DCH(CNOVEDADES_AUTO(I).NIT))
                                 /*CAMPO6*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_PENSION_OBLIGATORIA,0),10) 
                                 /*CAMPO7*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_AP_VOL_AFILIADO,0),10) 
                                 /*CAMPO8*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_AP_VOL_APORTANTE,0),10)
                                 /*CAMPO9*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_TOTAL_FSP,0),10) 
                                 /*CAMPO10*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_TOTAL_FSP_ADIC,0),10) 
                                 /*CAMPO11*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).DIAS_MORA_PENSIONES,0),4) 
                                 /*CAMPO12*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_INTERESES_MORA_PENSIONES,0),10) 
                                 /*CAMPO13*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_INT_MORA_PENSIONESFSP,0),10)
                                 /*CAMPO14*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALORINTMORAPENSIONESFSPADIC,0),10)
                                 /*CAMPO15*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).TOTAL_PAGO_PENSION,0),10)
                                 /*CAMPO16*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).TOTAL_AFILIADOS_PENSION,0),6);
          GL_TOTALPENSIONES11 := GL_TOTALPENSIONES11 + CNOVEDADES_AUTO(I).TOTAL_PAGO_PENSION;
          GL_NUMADMPENSIONES  := GL_NUMADMPENSIONES  + 1;
        ELSIF UN_ADMINISTRADORA = '02' THEN --SALUD
          PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                 /*CAMPO1*/ '04' 
                                 /*CAMPO2*/ || PCK_SYSMAN_UTL.FC_STRZERO(MI_CONTADOR, 5) 
                                 /*CAMPO3*/ || RPAD(MI_CODA, 6, ' ') 
                                 /*CAMPO4*/ || PCK_SYSMAN_UTL.FC_STRZERO(CNOVEDADES_AUTO(I).NIT,16) 
                                 /*CAMPO5*/ || TO_CHAR(PCK_SYSMAN_UTL.FC_DCH(CNOVEDADES_AUTO(I).NIT))
                                 /*CAMPO6*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_SALUD_OBLIGATORIA,0),10) 
                                 /*CAMPO7*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_TOTAL_UPC_ADICIONALES,0),10) 
                                 /*CAMPO8*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).NUM_AUTO_INCAP,0),15) 
                                 /*CAMPO9*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VR_INCAP,0),10) 
                                 /*CAMPO10*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).NUM_AUTO_MAT,0),15) 
                                 /*CAMPO11*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VR_MAT,0),10) 
                                 /*CAMPO12*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_NETO_APORTES_SALUD,0),10) 
                                 /*CAMPO13*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).DIAS_MORA_SALUD,0),4) 
                                 /*CAMPO14*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_INTERESES_MORA_SALUD,0),10)
                                 /*CAMPO15*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_INTERESES_MORA_UPC,0),10)
                                 /*CAMPO16*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).SUBTOTAL_APORTES_COTIZACION,0),10)
                                 /*CAMPO17*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).SUBTOTAL_APORTES_UPC_ADICIONAL,0),10)
                                 /*CAMPO18*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).NUMERO_RADICACION_SALDO_FAVOR,0),10)
                                 /*CAMPO19*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_SALDO_FAVOR,0),10)
                                 /*CAMPO20*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_SALDO_FAVORUPC,0),10)
                                 /*CAMPO21*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).TOTAL_COTIZACION_OBLIGAT_SALUD,0),10)
                                 /*CAMPO22*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).TOTAL_UPC_ADICIONALES,0),10)
                                 /*CAMPO23*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).TOTAL_PAGO_SALUD,0),10)
                                 /*CAMPO24*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_FSGS,0),10)
                                 /*CAMPO25*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).TOTAL_AFILIADOS_SALUD,0),6)
                                 ;
          GL_TOTALSALUD11 := GL_TOTALSALUD11 + CNOVEDADES_AUTO(I).TOTAL_PAGO_SALUD;
          GL_NUMADMSALUD  := GL_NUMADMSALUD  + 1;
        ELSIF UN_ADMINISTRADORA = '03' THEN --RIESGOS
          PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                 /*CAMPO1*/ CASE WHEN UN_TIPOESTRUCTURA <> 2388 THEN '05' ELSE '06' END
                                 /*CAMPO2*/ || PCK_SYSMAN_UTL.FC_STRZERO(MI_CONTADOR, 5) 
                                 /*CAMPO3*/ || RPAD(MI_CODA, 6, ' ') 
                                 /*CAMPO4*/ || PCK_SYSMAN_UTL.FC_STRZERO(CNOVEDADES_AUTO(I).NIT,16) 
                                 /*CAMPO5*/ || TO_CHAR(PCK_SYSMAN_UTL.FC_DCH(CNOVEDADES_AUTO(I).NIT))
                                 /*CAMPO6*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).TOTAL_APORTES_ARP,0),13) 
                                 /*CAMPO7*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).NUMERO_AUT_PAGO_INCAPARP,0),15) 
                                 /*CAMPO8*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_TOTAL_INCAP_PAGADASARP,0),13) 
                                 /*CAMPO9*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_AP_PAGADOS_OTROS_RIESGOS,0),13) 
                                 /*CAMPO10*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_TOTAL_COTIZACION,0),13) 
                                 /*CAMPO11*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).DIAS_MORA_ARP,0),4) 
                                 /*CAMPO12*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_INTERESES_MORA_ARP,0),11) 
                                 /*CAMPO13*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).SUBTOTAL_APORTES_ARP,0),13) 
                                 /*CAMPO14*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).NUMERO_RAD_AUT_SALDO_FAVORARP,0),10)
                                 /*CAMPO15*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_SALDOPERIODOANTERIORARP,0),11)
                                 /*CAMPO16*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_TOTAL_NETO_ARP,0),13)
                                 /*CAMPO17*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_FONDO_SOLIDARIDAD_ARP,0),11)
                                 /*CAMPO18*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).TOTAL_AFILIADOS_RIESGOS,0),8);
          GL_TOTALRIESGOS11 := GL_TOTALRIESGOS11 + CNOVEDADES_AUTO(I).VALOR_TOTAL_NETO_ARP;
          GL_NUMADMARP  := GL_NUMADMARP  + 1;
        ELSIF UN_ADMINISTRADORA = '04' THEN --CAJAS
          PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                 /*CAMPO1*/ '06'
                                 /*CAMPO2*/ || PCK_SYSMAN_UTL.FC_STRZERO(MI_CONTADOR, 5) 
                                 /*CAMPO3*/ || RPAD(MI_CODA, 6, ' ') 
                                 /*CAMPO4*/ || PCK_SYSMAN_UTL.FC_STRZERO(CNOVEDADES_AUTO(I).NIT,16) 
                                 /*CAMPO5*/ || TO_CHAR(PCK_SYSMAN_UTL.FC_DCH(CNOVEDADES_AUTO(I).NIT))
                                 /*CAMPO6*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_APORTES_CAJAS,0),10) 
                                 /*CAMPO7*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).DIAS_MORA_CAJAS,0),4) 
                                 /*CAMPO8*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_INTERESES_MORA_CAJAS,0),10) 
                                 /*CAMPO9*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).TOTAL_NETO_CAJAS,0),10) 
                                 /*CAMPO10*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).TOTAL_AFILIADOS_CAJAS,0),6);
          GL_TOTALCAJAS11 := GL_TOTALCAJAS11 + CNOVEDADES_AUTO(I).TOTAL_NETO_CAJAS;
          GL_NUMADMCAJAS  := GL_NUMADMCAJAS  + 1;
        ELSIF UN_ADMINISTRADORA = '05' THEN --SENA
          PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                 /*CAMPO1*/ '07'
                                 /*CAMPO2*/ || PCK_SYSMAN_UTL.FC_STRZERO(MI_CONTADOR, 5) 
                                 /*CAMPO3*/ || PCK_SYSMAN_UTL.FC_STRZERO(CNOVEDADES_AUTO(I).NIT,16) 
                                 /*CAMPO4*/ || TO_CHAR(PCK_SYSMAN_UTL.FC_DCH(CNOVEDADES_AUTO(I).NIT))
                                 /*CAMPO5*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_APORTES_SENA,0),10) 
                                 /*CAMPO6*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).DIAS_MORA_SENA,0),4) 
                                 /*CAMPO7*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_INTERESES_MORA_SENA,0),10) 
                                 /*CAMPO8*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_NETO_SENA,0),10) 
                                 /*CAMPO9*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).TOTAL_AFILIADOS_SENA,0),6);
          GL_TOTALSENA11 := GL_TOTALSENA11 + CNOVEDADES_AUTO(I).VALOR_NETO_SENA;
        ELSIF UN_ADMINISTRADORA = '06' THEN --ICBF
          PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                 /*CAMPO1*/ '08'
                                 /*CAMPO2*/ || PCK_SYSMAN_UTL.FC_STRZERO(MI_CONTADOR, 5) 
                                 /*CAMPO3*/ || PCK_SYSMAN_UTL.FC_STRZERO(CNOVEDADES_AUTO(I).NIT,16) 
                                 /*CAMPO4*/ || TO_CHAR(PCK_SYSMAN_UTL.FC_DCH(CNOVEDADES_AUTO(I).NIT))
                                 /*CAMPO5*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_APORTES_ICBF,0),10) 
                                 /*CAMPO6*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).DIAS_MORA_ICBF,0),4) 
                                 /*CAMPO7*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_INTERESES_MORA_ICBF,0),10) 
                                 /*CAMPO8*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_NETO_ICBF,0),10) 
                                 /*CAMPO9*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).TOTAL_AFILIADOS_ICBF,0),6);
          GL_TOTALICBF11 := GL_TOTALICBF11 + CNOVEDADES_AUTO(I).VALOR_NETO_ICBF;
        ELSIF UN_ADMINISTRADORA = '07' THEN --ESAP
          PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                 /*CAMPO1*/ '09'
                                 /*CAMPO2*/ || PCK_SYSMAN_UTL.FC_STRZERO(MI_CONTADOR, 5) 
                                 /*CAMPO3*/ || PCK_SYSMAN_UTL.FC_STRZERO(CNOVEDADES_AUTO(I).NIT,16) 
                                 /*CAMPO4*/ || TO_CHAR(PCK_SYSMAN_UTL.FC_DCH(CNOVEDADES_AUTO(I).NIT))
                                 /*CAMPO5*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_APORTES_ESAP,0),10) 
                                 /*CAMPO6*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).DIAS_MORA_ESAP,0),4) 
                                 /*CAMPO7*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_INTERESES_MORA_ESAP,0),10) 
                                 /*CAMPO8*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_NETO_ESAP,0),10);
          GL_TOTALESAP11 := GL_TOTALESAP11 + CNOVEDADES_AUTO(I).VALOR_NETO_ESAP;
        ELSIF UN_ADMINISTRADORA = '08' THEN --INSTITUTOS
          PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                 /*CAMPO1*/ '10'
                                 /*CAMPO2*/ || PCK_SYSMAN_UTL.FC_STRZERO(MI_CONTADOR, 5) 
                                 /*CAMPO3*/ || PCK_SYSMAN_UTL.FC_STRZERO(CNOVEDADES_AUTO(I).NIT,16) 
                                 /*CAMPO4*/ || TO_CHAR(PCK_SYSMAN_UTL.FC_DCH(CNOVEDADES_AUTO(I).NIT))
                                 /*CAMPO5*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_APORTES_INSTITUTOS,0),10) 
                                 /*CAMPO6*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).DIAS_MORA_INSTITUTOS,0),4) 
                                 /*CAMPO7*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_INTERES_MORA_INSTITUTOS,0),10) 
                                 /*CAMPO8*/ || PCK_SYSMAN_UTL.FC_STRZERO(NVL(CNOVEDADES_AUTO(I).VALOR_NETO_INSTITUTOS,0),10);
          GL_TOTALINSTITUTOS11 := GL_TOTALINSTITUTOS11 + CNOVEDADES_AUTO(I).VALOR_NETO_INSTITUTOS;
        END IF;
      END LOOP;
    END IF;
END PR_REGISTROS_FINALES_AUX;



PROCEDURE PR_REGISTROS_FINALES(
    /*
    NAME              : REGISTROS_FINALES  --> EN ACCESS Relacionado  -
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : JOSE PASCUAL GOMEZ
    DATE MIGRATION    : 25/01/2018
    TIME              : 09:11 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    @NAME:   
  */
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES            IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO        IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_PERIODO_RETRO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_TIPOESTRUCTURA IN VARCHAR2  
    )
AS
  MI_CONTADOR NUMBER;
BEGIN
  IF GL_GENERAREGISTRO367891011 = 'NO' OR PCK_NOMINA.GL_OPERADOR='SOI.COM.CO' THEN
    RETURN;
  END IF;
  IF PCK_NOMINA.GL_OPERADOR NOT IN('BANCOAGRARIO','ASOCAJAS') AND UN_TIPOESTRUCTURA <> 2388 THEN
    PCK_NOMINA.GL_STRETAPAGLOBAL  := 'Generando Archivos Finales 03';
    PR_REGISTROS_FINALES_AUX(UN_COMPANIA       => UN_COMPANIA
                            ,UN_ANIO           => UN_ANIO
                            ,UN_MES            => UN_MES
                            ,UN_PERIODO        => UN_PERIODO
                            ,UN_PERIODO_RETRO  => UN_PERIODO_RETRO
                            ,UN_ADMINISTRADORA => '01'
                            ,UN_TIPOESTRUCTURA => UN_TIPOESTRUCTURA);
  END IF;
  IF UN_TIPOESTRUCTURA <> 2388 THEN
    PCK_NOMINA.GL_STRETAPAGLOBAL  := 'Generando Archivos Finales 04';
    PR_REGISTROS_FINALES_AUX(UN_COMPANIA       => UN_COMPANIA
                            ,UN_ANIO           => UN_ANIO
                            ,UN_MES            => UN_MES
                            ,UN_PERIODO        => UN_PERIODO
                            ,UN_PERIODO_RETRO  => UN_PERIODO_RETRO
                            ,UN_ADMINISTRADORA => '02'
                            ,UN_TIPOESTRUCTURA => UN_TIPOESTRUCTURA);
  END IF;
  PCK_NOMINA.GL_STRETAPAGLOBAL  := 'Generando Archivos Finales 05';
  PR_REGISTROS_FINALES_AUX(UN_COMPANIA       => UN_COMPANIA
                          ,UN_ANIO           => UN_ANIO
                          ,UN_MES            => UN_MES
                          ,UN_PERIODO        => UN_PERIODO
                          ,UN_PERIODO_RETRO  => UN_PERIODO_RETRO
                          ,UN_ADMINISTRADORA => '03'
                          ,UN_TIPOESTRUCTURA => UN_TIPOESTRUCTURA);
  IF UN_TIPOESTRUCTURA = 2388 OR PCK_NOMINA.GL_OPERADOR IN('BANCOAGRARIO','ASOCAJAS') THEN
    RETURN;
  END IF;
  PCK_NOMINA.GL_STRETAPAGLOBAL  := 'Generando Archivos Finales 06';
  PR_REGISTROS_FINALES_AUX(UN_COMPANIA       => UN_COMPANIA
                          ,UN_ANIO           => UN_ANIO
                          ,UN_MES            => UN_MES
                          ,UN_PERIODO        => UN_PERIODO
                          ,UN_PERIODO_RETRO  => UN_PERIODO_RETRO
                          ,UN_ADMINISTRADORA => '04'
                          ,UN_TIPOESTRUCTURA => UN_TIPOESTRUCTURA);
  PCK_NOMINA.GL_STRETAPAGLOBAL  := 'Generando Archivos Finales 07';
  PR_REGISTROS_FINALES_AUX(UN_COMPANIA       => UN_COMPANIA
                          ,UN_ANIO           => UN_ANIO
                          ,UN_MES            => UN_MES
                          ,UN_PERIODO        => UN_PERIODO
                          ,UN_PERIODO_RETRO  => UN_PERIODO_RETRO
                          ,UN_ADMINISTRADORA => '05'
                          ,UN_TIPOESTRUCTURA => UN_TIPOESTRUCTURA);
  PCK_NOMINA.GL_STRETAPAGLOBAL  := 'Generando Archivos Finales 08';
  PR_REGISTROS_FINALES_AUX(UN_COMPANIA       => UN_COMPANIA
                          ,UN_ANIO           => UN_ANIO
                          ,UN_MES            => UN_MES
                          ,UN_PERIODO        => UN_PERIODO
                          ,UN_PERIODO_RETRO  => UN_PERIODO_RETRO
                          ,UN_ADMINISTRADORA => '06'
                          ,UN_TIPOESTRUCTURA => UN_TIPOESTRUCTURA);
  PCK_NOMINA.GL_STRETAPAGLOBAL  := 'Generando Archivos Finales 09';
  PR_REGISTROS_FINALES_AUX(UN_COMPANIA       => UN_COMPANIA
                          ,UN_ANIO           => UN_ANIO
                          ,UN_MES            => UN_MES
                          ,UN_PERIODO        => UN_PERIODO
                          ,UN_PERIODO_RETRO  => UN_PERIODO_RETRO
                          ,UN_ADMINISTRADORA => '07'
                          ,UN_TIPOESTRUCTURA => UN_TIPOESTRUCTURA);
  PCK_NOMINA.GL_STRETAPAGLOBAL  := 'Generando Archivos Finales 10';
  PR_REGISTROS_FINALES_AUX(UN_COMPANIA       => UN_COMPANIA
                          ,UN_ANIO           => UN_ANIO
                          ,UN_MES            => UN_MES
                          ,UN_PERIODO        => UN_PERIODO
                          ,UN_PERIODO_RETRO  => UN_PERIODO_RETRO
                          ,UN_ADMINISTRADORA => '08'
                          ,UN_TIPOESTRUCTURA => UN_TIPOESTRUCTURA);
END PR_REGISTROS_FINALES;

PROCEDURE PR_GENERARTEXGTOFINAL
AS
  MI_RETORNO CLOB;
BEGIN
  GL_DISCOINTEGRADO := GL_DISCOINTEGRADO || CHR(13) || CHR(10) || CHR(13) || CHR(10) || CHR(13) || CHR(10) ||
                'IGR= INGRESO ,RET= RETIRO, TDE= Traslado desde otra EPS o EOC, TAE= Traslado a otra EPS o EOC, TDP= Traslado desde otra Administradora de Pensiones, TAP: Traslado a otra Administradora de Pensiones' || CHR(13) || CHR(10) ||
                'VSP= VARIACION PERMANENTE DE SALARIO, VTE= CORRECCIONES,  VST= VARIACION TRANSITORIA DE SALARIO, SLN= SuspensiÂ¿emporal del contrato de trabajo o licencia no remunerada o comisiÂ¿e servicios, IGE= INCAPACIDAD O ENFERMEDAD GENERAL' || CHR(13) || CHR(10) ||
                'LMA= Licencia de Maternidad o de paternidad., VAC= LR: Vacaciones, Licencia Remunerada, AVP= APORTES VOLUNTARIOS EN PENSION, VCT= VARIACION CENTRO DE TRABAJO., IRP INCAPACIDAD POR ACCIDENTE DE TRABAJO O ENFERMEDAD PROFESIONAL.' || CHR(13) || CHR(10) ||
                '' || CHR(13) || CHR(10) ||
                '   Nuevos Campos en la estructura del archivo plano RESOLUCION 2388/2016: Se agregan nuevos campos que amplÂ¿ la estructura de los archivos planos. Este cambio tiene alto impacto en la generaciÂ¿e liquidaciones. Los nuevos campos son:' || CHR(13) || CHR(10) ||
                '80. Fecha de Ingreso                          515 HASTA 524' || CHR(13) || CHR(10) ||
                '81. Fecha de Retiro                           525 HASTA 534' || CHR(13) || CHR(10) ||
                '82. Fecha Inicio VSP                          535 HASTA 544' || CHR(13) || CHR(10) ||
                '83. Fecha Inicio SLN                          545 HASTA 554' || CHR(13) || CHR(10) ||
                '84. Fecha Fin SLN                             555 HASTA 564' || CHR(13) || CHR(10) ||
                '85. Fecha Inicio IGE                          565 HASTA 574' || CHR(13) || CHR(10) ||
                '86. Fecha Fin IGE                             575 HASTA 584' || CHR(13) || CHR(10) ||
                '87. Fecha Inicio LMA                          585 HASTA 594' || CHR(13) || CHR(10) ||
                '88. Fecha Fin LMA                             595 HASTA 604' || CHR(13) || CHR(10) ||
                '89. Fecha Inicio VAC-LR                       605 HASTA 614' || CHR(13) || CHR(10) ||
                '90. Fecha Fin VAC-LR                          615 HASTA 624' || CHR(13) || CHR(10) ||
                '91. Fecha Inicio VCT                          625 HASTA 634' || CHR(13) || CHR(10) ||
                '92. Fecha Fin VCT                             635 HASTA 644' || CHR(13) || CHR(10) ||
                '93. Fecha Inicio IRL                          645 HASTA 654' || CHR(13) || CHR(10) ||
                '94. Fecha Fin IRL                             655 HASTA 664' || CHR(13) || CHR(10) ||
                '95. IBC Otros Parafiscales diferentes a CCF   665 HASTA 673' || CHR(13) || CHR(10) ||
                '96. NÂ¿o de horas laboradas                 674 HASTA 676' || CHR(13) || CHR(10) ||
                '97. Fecha Radicado en el Exterior             677 HASTA 686' || CHR(13) || CHR(10);

END PR_GENERARTEXGTOFINAL;

FUNCTION FC_SUCURSALRIESGON
 /*
    NAME              : FC_SUCURSALRIESGON
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ
    DATE MIGRADOR     : 23/02/2018
    TIME              : 10:23 AM
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : RETORNA LA SUCURSAL EQUIVALENTE PARA EL PLANO DE RIESGO DE LA TABLA PATRONAL 
    @Name: sucursalRiesgon
  */
(
	UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_EMPLEADO       IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,    
    UN_NUMEROPATRONAL IN VARCHAR2,
    UN_FACTOR_RIESGO  IN NUMBER,
	UN_PAR			  IN PCK_SUBTIPOS.TI_LOGICO
) RETURN VARCHAR2 AS
  MI_RETORNO			VARCHAR2(50) := '0';
  MI_FACTOR  			NUMBER := 0;
BEGIN
  --(APINEDA:17/07/2019)-Se reciben valores de Numero patronal y Factor riesgo, tomados de la vista V_SOI para la generaciÃ³n de plano de Retroactivo.
  IF UN_NUMEROPATRONAL IS NULL THEN
      BEGIN
                    SELECT PATRONALES.SUCURSAL,PATRONALES.FACTOR 
                            INTO MI_RETORNO, MI_FACTOR 
                    FROM PERSONAL 
                        INNER JOIN PATRONALES ON 
                                PERSONAL.COMPANIA = PATRONALES.COMPANIA 
                            AND PERSONAL.NUMEROPATRONAL = PATRONALES.SUCURSAL 
                    WHERE PERSONAL.COMPANIA=UN_COMPANIA
                        AND PERSONAL.ID_DE_EMPLEADO=UN_EMPLEADO; --) LOOP
            EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN 
            MI_RETORNO :=0;
      END;
  ELSE
     MI_RETORNO := UN_NUMEROPATRONAL;
     MI_FACTOR  := UN_FACTOR_RIESGO;  
  END IF;
    IF(UN_PAR=0) THEN
      MI_RETORNO:=MI_RETORNO;
    ELSIF(MI_FACTOR <= 0.5222) THEN
      MI_RETORNO:='1';
    ELSIF(MI_FACTOR > 0.5222 AND MI_FACTOR <=1.044) THEN
      MI_RETORNO:='2';
    ELSIF(MI_FACTOR > 1.044 AND MI_FACTOR <= 2.436) THEN
      MI_RETORNO:='3';
    ELSIF(MI_FACTOR > 2.436 AND MI_FACTOR <= 4.35) THEN
      MI_RETORNO:='4';
    ELSIF(MI_FACTOR > 4.35 AND MI_FACTOR <= 6.96) THEN
      MI_RETORNO:='5';
    ELSE
      MI_RETORNO:='1';
    END IF;

	RETURN MI_RETORNO;   
END FC_SUCURSALRIESGON;

PROCEDURE PR_SISTEMAINTEGRADOELECTRO_P
(
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROCESO         IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_ANIO            IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES             IN PCK_SUBTIPOS.TI_MES,
  UN_PERIODO         IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
  UN_TIPOESTRUCTURA  IN VARCHAR2,
  UN_TIPOPLANILLA    IN VARCHAR2,
  UN_CORRECCION      IN PCK_SUBTIPOS.TI_LOGICO,
  UN_NUMCORRECCION   IN PCK_SUBTIPOS.TI_ENTERO_LARGO :=0,
  UN_FECHACORRECCION IN DATE  DEFAULT TO_DATE('01/01/1900','DD/MM/YYYY') ,
  UN_ANIOCORRECCION  IN PCK_SUBTIPOS.TI_ANIO,
  UN_MESCORRECCION   IN PCK_SUBTIPOS.TI_MES,
  UN_NITCOMPANIA     IN VARCHAR2,
  UN_RETROACTIVO     IN PCK_SUBTIPOS.TI_LOGICO,
  UN_PERIODORETRO    IN PCK_SUBTIPOS.TI_LOGICO,
  UN_FECHALIQUIDACION IN DATE,
  UN_NUMRADICACION    IN PCK_SUBTIPOS.TI_ENTERO :='',
  UN_ORDEN            IN PCK_SUBTIPOS.TI_LOGICO ,  
  UN_IDEMPLEADO       IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO ,
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
  MI_STRSQL     PCK_SUBTIPOS.TI_STRSQL;

  MI_TXTCORRECION VARCHAR2(10); 
  MI_FECHAPAGOCORRECCIONPLANILLA  VARCHAR2(10); 

  MI_TOTALPENSIONES11       NUMBER:=0;                                             
  MI_TOTALSALUD11           NUMBER:=0;
  MI_TOTALRIESGOS11         NUMBER:=0;                                             
  MI_TOTALCAJAS11           NUMBER:=0;
  MI_TOTALSENA11            NUMBER:=0;                                            
  MI_TOTALICBF11            NUMBER:=0;
  MI_TOTALESAP11            NUMBER:=0;                                             
  MI_TOTALINSTITUTOS11      NUMBER:=0;
  MI_TOTSALUD               NUMBER:=0;                                             
  MI_TOTPENSION             NUMBER:=0;
  MI_TOTRIESGOS             NUMBER:=0;                                             
  MI_TOTIBC                 NUMBER:=0;
  MI_TOTSUELDO              NUMBER:=0;                                             
  MI_TOTNETO                NUMBER:=0;
  MI_TOTDIAS                NUMBER:=0;                                             
  MI_TOTAPVOL               NUMBER:=0;
  MI_NUMADMPENSIONES        NUMBER:=0;   
  MI_NUMADMSALUD            NUMBER:=0;
  MI_NUMADMARP              NUMBER:=0;   
  MI_NUMADMCAJAS            NUMBER:=0;
  MI_NUMEMPLEADOS           NUMBER:=0;
  MI_TOTALPAGO              NUMBER:=0;
  MI_TOTALBASEPARAFISCAL    NUMBER:=0;
  MI_SALARIOMINIMO          NUMBER:=0;     
  MI_NUMRADICA              VARCHAR2(10);
  MI_NITCOMPANIA            VARCHAR2(20);
  MI_NITF                   VARCHAR2(20);  
  MI_NIT                    VARCHAR2(20); 
  MI_PERCOTIZ               VARCHAR2(20);  
  MI_RAZONSOCIAL            VARCHAR2(200); 
  MI_TIPONIT                VARCHAR2(100); 
  MI_FORMAPRESENTACION      VARCHAR2(20); 
  MI_SUCURSALAPORTANTE      VARCHAR2(10); 
  MI_NOMBRESUCURSAL         VARCHAR2(40); 
  MI_TIPOAPORTANTE          VARCHAR2(20); 
  MI_PARA                   VARCHAR2(20); 
  MI_NUMEMP                 PCK_SUBTIPOS.TI_ENTERO;
  MI_RESG                   PCK_SUBTIPOS.TI_ENTERO;
  MI_INCAPE                 NUMBER:=0;                                
  MI_AC                     NUMBER:=0; 
  MI_FECHACAMBIORIEGO       DATE;
  MI_ADS                    NUMBER:=0;
  MI_NUMINCAP               VARCHAR2(9);    
  MI_NUMLICMATER            VARCHAR2(9);
  MI_LINN                   NUMBER:=0;

  MI_DIASPARAFISCALES       VARCHAR2(30); 
  MI_GENERARNOVEDADESPILA2388   NUMBER:=0;
  MI_CAMPOSNUEVOS2388N      CLOB;
  MI_CAMPOSNUEVOS2388       CLOB;
  MI_TIPODCTO               VARCHAR2(3); 
  MI_GRANTOTAL              NUMBER:=0;
  MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;  
  MI_MESADA                 NUMBER:=0;
  MI_DOBLEPENSION           PCK_SUBTIPOS.TI_DOBLE :=0;  
  MI_PARAMETRO1990APLICA          PARAMETRO.VALOR%TYPE;

BEGIN 
  MI_PARAMETRO1990APLICA := UPPER(PCK_PARST.FC_PAR('APLICAR NUEVO REDONDEO DECRETO 1990 FC_ROUND_100', 'NO')) ;
  --CARGA EL VECTOR DE PARAMETROS DE ENTRADA
  PCK_PARENTR.PR_CARGAR_PARAMETROSENTRADA(UN_COMPANIA => UN_COMPANIA);
  PCK_NOMINA_COM5.PR_INICIARPARAMETROSPLANOSOI(UN_COMPANIA => UN_COMPANIA);
  PCK_NOMINA_COM5.GL_DISCOINTEGRADO:='';
  MI_LINN:=0;
  BEGIN
    MI_CONDICION := 'COMPANIA=''' || UN_COMPANIA || '''';
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'ERRORES', 
                                          UN_ACCION    => 'E',
                                          UN_CONDICION => MI_CONDICION);
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
    PCK_NOMINA.GL_STRETAPAGLOBAL:='Al elminar los Errores';
    RAISE PCK_EXCEPCIONES.EXC_NOMINA;
  END;
  PCK_NOMINA.GL_STRETAPAGLOBAL   := '1-Inicio';
  MI_TXTCORRECION                := UN_TIPOPLANILLA;
  PCK_NOMINA_COM5.GL_PLANILLAACORREGIR           := RPAD(' ',10,' ');
  MI_FECHAPAGOCORRECCIONPLANILLA := RPAD(' ',10,' ');
  IF UN_CORRECCION <> 0 THEN
    MI_TXTCORRECION := 'X';
    IF UN_NUMCORRECCION=0 THEN
      --MSGBOX "SI TIENE INDICADOR DE CORRECCION, DEBE DIGITAR NÂ¿ERO DE PLANILLA A CORREGIR"
      PCK_NOMINA_COM5.GL_PLANILLAACORREGIR := RPAD(' ',10,' ');
    ELSE
      PCK_NOMINA_COM5.GL_PLANILLAACORREGIR         := RPAD(UN_NUMCORRECCION, 10);
    END IF;
    IF UN_FECHACORRECCION= TO_DATE('01/01/1900','DD/MM/YYYY') THEN
      MI_FECHAPAGOCORRECCIONPLANILLA := TO_CHAR(LAST_DAY(TO_DATE('01/' || UN_MESCORRECCION || '/' || UN_ANIOCORRECCION, 'DD/MM/YYYY')), 'YYYY-MM-DD');
    ELSE
      MI_FECHAPAGOCORRECCIONPLANILLA := TO_CHAR(UN_FECHACORRECCION, 'YYYY-MM-DD');
    END IF;
  END IF;

  PCK_NOMINA_COM5.GL_CODAICC :=RPAD(' ', 6, ' '); 
  PCK_NOMINA_COM5.GL_CODAIFS :=RPAD(' ', 6, ' '); 
  PCK_NOMINA_COM5.GL_CODAIFP :=RPAD(' ', 6, ' '); 
  PCK_NOMINA_COM5.GL_SALARIOINTEGRAL := ' ';
  PCK_NOMINA_COM5.GL_PR := PCK_PARENTR.PARAMETRO39 - TRUNC(PCK_PARENTR.PARAMETRO39);
  IF PCK_NOMINA_COM5.GL_PR >0 AND PCK_NOMINA_COM5.GL_PR < 1 THEN
    PCK_NOMINA_COM5.GL_PR := 10;
  ELSE
    PCK_NOMINA_COM5.GL_PR :=1;
  END IF;

  MI_TOTALPENSIONES11 :=0;      
  MI_TOTALSALUD11     :=0;
  MI_TOTALRIESGOS11   :=0;      
  MI_TOTALCAJAS11     :=0;
  MI_TOTALSENA11      :=0;      
  MI_TOTALICBF11      :=0;
  MI_TOTALESAP11      :=0;      
  MI_TOTALINSTITUTOS11:=0;
  MI_NUMADMPENSIONES  :=0;      
  MI_NUMADMSALUD      :=0;
  MI_NUMADMARP        :=0;      
  MI_NUMADMCAJAS      :=0;

  MI_TOTSALUD         :=0;      
  MI_TOTPENSION       :=0;
  MI_TOTRIESGOS       :=0;      
  MI_TOTIBC           :=0;
  MI_TOTSUELDO        :=0;      
  MI_TOTNETO          :=0;
  MI_TOTDIAS          :=0;      
  MI_TOTAPVOL         :=0;
  PCK_NOMINA_COM5.GL_CONTADOR         :=0;
  PCK_NOMINA_COM5.GL_SUELDO           :=0;
  PCK_NOMINA_COM5.GL_SUELDOP          :=0;

  MI_NITF := PCK_PARENTR.PARAMETRO31;
  MI_NITF :=REPLACE(MI_NITF, '.', '');
  MI_NITF :=REPLACE(MI_NITF, '-', '');
  MI_NITF :=SUBSTR(MI_NITF,1,9);  
  MI_NIT  :=MI_NITF;
  MI_TIPOAPORTANTE :=PCK_PARENTR.PARAMETRO32;  
  PCK_NOMINA.GL_STRETAPAGLOBAL := '2 parametros de entrada';


  MI_PERCOTIZ := UN_ANIO || PCK_SYSMAN_UTL.FC_STRZERO(UN_MES, 2);
  MI_RAZONSOCIAL:= PCK_PARENTR.PARAMETRO30;
  MI_RAZONSOCIAL:= CASE WHEN INSTR(MI_RAZONSOCIAL, '-', 1, 3) <= 0 
                        THEN MI_RAZONSOCIAL
                        ELSE SUBSTR(MI_RAZONSOCIAL,1, INSTR(MI_RAZONSOCIAL, '-', 1, 3)-1) END;
  MI_RAZONSOCIAL:= RPAD(UPPER(MI_RAZONSOCIAL),150, ' ');
  MI_TIPONIT:=NVL(PCK_PARENTR.PARAMETRO60, 'NI');
  MI_TIPONIT:=CASE WHEN MI_TIPONIT = 'C'  THEN 'CC' ELSE MI_TIPONIT END;
  MI_TIPONIT:=CASE WHEN MI_TIPONIT IN('','N') THEN 'NI' ELSE MI_TIPONIT END;

  MI_FORMAPRESENTACION:=NVL(PCK_PARENTR.PARAMETRO33, 'U');
  IF MI_FORMAPRESENTACION IN('S','D') THEN
    MI_SUCURSALAPORTANTE := '' || PCK_PARENTR.PARAMETRO34;
    MI_NOMBRESUCURSAL    := '' || PCK_PARENTR.PARAMETRO65;
  END IF;
  PCK_NOMINA.GL_STRETAPAGLOBAL := '3-paramtros de entrada';

  PCK_NOMINA.GL_STRETAPAGLOBAL := '4. consulta datos';
  MI_STRSQL :='SELECT *
              FROM V_SOI
              WHERE COMPANIA     =''' || UN_COMPANIA || '''
               AND ID_DE_PROCESO NOT IN(98)
               AND ID_DE_TIPO IN (99)
               AND ANO           =' || UN_ANIO || '
               AND MES           =' || UN_MES ||
               CASE WHEN UN_IDEMPLEADO=0 
               THEN ' AND ID_DE_EMPLEADO <>0'
               ELSE ' AND ID_DE_EMPLEADO=' || UN_IDEMPLEADO END;

  IF UN_ORDEN = 1 THEN
    MI_STRSQL := MI_STRSQL || ' ORDER BY APELLIDO1, APELLIDO2, ID_DE_EMPLEADO, NOMBRES';
  ELSE
    MI_STRSQL := MI_STRSQL || ' ORDER BY NUMERO_DCTO';
  END IF;
  EXECUTE IMMEDIATE MI_STRSQL BULK  COLLECT INTO PCK_NOMINA_COM5.CSOI;
  IF PCK_NOMINA_COM5.CSOI.COUNT<=0 THEN
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD =>SQLCODE,
          UN_ERROR_COD=>PCK_ERRORES.ERR_NOEXISTENEMPLEADOS
      );
    END;
  END IF;     

  MI_NUMEMPLEADOS := 0;
  MI_TOTALPAGO    := 0;
  MI_TOTALBASEPARAFISCAL := 0;   

  FOR I IN PCK_NOMINA_COM5.CSOI.FIRST .. PCK_NOMINA_COM5.CSOI.LAST
  LOOP
    PCK_NOMINA.P:= I;    

    MI_AC:=PCK_NOMINA.FC_ACUM(UN_COMPANIA  => UN_COMPANIA, 
                              UN_ANO1      => UN_ANIO, 
                              UN_MES1      => UN_MES, 
                              UN_PERIODO1  => CASE WHEN UN_RETROACTIVO <> 0 THEN UN_PERIODORETRO ELSE 1 END, 
                              UN_ANO2      => UN_ANIO, 
                              UN_MES2      => UN_MES, 
                              UN_PERIODO2  => CASE WHEN UN_RETROACTIVO <> 0 THEN UN_PERIODORETRO ELSE 99 END, 
                              UN_IDEMPLEADO=> PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO);    
    MI_MESADA := PCK_NOMINA.FC_CNA(2);          

    IF PCK_NOMINA_COM5.CSOI(I).PAGOS > 0 THEN 
        MI_NUMEMPLEADOS := MI_NUMEMPLEADOS + 1 ;
        MI_TOTALPAGO := MI_TOTALPAGO + PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     =>PCK_NOMINA_COM5.CSOI(I).PAGOS 
                                                              ,UN_PRECISION =>   0);        
      IF PCK_NOMINA_COM5.CSOI(I).ESTADO_ACTUAL = 2 THEN        
        MI_TOTALBASEPARAFISCAL := MI_TOTALBASEPARAFISCAL + PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(I).SALARIO_BASE_IBC 
                                                                                  ,UN_PRECISION => CASE WHEN (PCK_NOMINA_COM5.CSOI(I).PENSIONCOMPARTIDA = 0) AND (PCK_NOMINA_COM5.CSOI(I).SALARIO_BASE_IBC > PCK_PARENTR.PARAMETRO20) THEN PCK_NOMINA.GL_RBASE1990 ELSE 0 END);
      ELSE       
        MI_TOTALBASEPARAFISCAL := MI_TOTALBASEPARAFISCAL + PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_MESADA + 1
                                                                                  ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);     
      END IF;    
    END IF;
  END LOOP;

  PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := 
                    /*CAMPO1*/    '01' 
                    /*CAMPO2*/ || '00001' 
                    /*CAMPO3*/ ||  MI_RAZONSOCIAL
                    /*CAMPO4*/ ||  MI_TIPONIT 
                    /*CAMPO5*/ ||  RPAD(MI_NIT, 16, ' ') 
                    /*CAMPO6*/ ||  CASE WHEN MI_NIT = '' 
                                        THEN  ' ' 
                                        ELSE CASE WHEN MI_TIPONIT = 'NI' 
                                                  THEN '' || PCK_SYSMAN_UTL.FC_DCH(MI_NITF) 
                                                  ELSE ' ' 
                                                  END 
                                        END                    
                    /*CAMPO7*/ ||  MI_FORMAPRESENTACION    
                    /*CAMPO8*/ ||  RPAD(NVL(MI_SUCURSALAPORTANTE,' '),10,' ')
                    /*CAMPO9*/ ||  RPAD(NVL(MI_NOMBRESUCURSAL   ,' '),40,' ')                        
                    /*CAMPO10*/ ||  UN_ANIO || '-' || PCK_SYSMAN_UTL.FC_STRZERO((UN_MES),2)                    
                    /*CAMPO11*/ ||  CASE WHEN UN_MES = 12 THEN '' || (UN_ANIO + 1) ELSE '' || UN_ANIO END || '-' || CASE WHEN UN_MES = 12 THEN  '01' ELSE PCK_SYSMAN_UTL.FC_STRZERO((UN_MES + 1), 2) END;                                     

  PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION                                        
                    /*CAMPO12*/ ||  RPAD(TO_CHAR(NVL(TO_CHAR(UN_NUMRADICACION),' ')), 10)
                    /*CAMPO13*/ ||  RPAD(' ',10,' ')  
                    /*CAMPO14*/ ||  PCK_SYSMAN_UTL.FC_STRZERO(MI_NUMEMPLEADOS, 7)    
                    /*CAMPO15*/ ||  PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN MI_TOTALBASEPARAFISCAL = 0 
                                          THEN PCK_PARENTR.PARAMETRO20
                                          ELSE MI_TOTALBASEPARAFISCAL END, 12)  
                    /*CAMPO16*/ ||  PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.GL_TIPOPAGADOR, 
                                CASE WHEN UN_TIPOESTRUCTURA = '2388' THEN 2 ELSE 1 END)    
                    /*CAMPO17*/ ||   '00'       
                    /*CAMPO18*/ ||  UN_TIPOPLANILLA;

  PCK_NOMINA_COM5.GL_DISCOINTEGRADO:= PCK_NOMINA_COM5.GL_DISCOINTEGRADO || --CHR(13) ||CHR(10) ||
                 PCK_NOMINA_COM5.FC_ENCABEZADOINTEGRADO(UN_RAZONSOCIAL  => MI_RAZONSOCIAL
                                                       ,UN_PERCOTIZA    => MI_PERCOTIZ
                                                       ,UN_INICIAL      =>1);

  MI_NUMEMP := 0;
  MI_SALARIOMINIMO := PCK_PARENTR.PARAMETRO20;

  <<RECORRIDOPRINCIPAL>>
  FOR I IN PCK_NOMINA_COM5.CSOI.FIRST .. PCK_NOMINA_COM5.CSOI.LAST
  LOOP
    PCK_NOMINA.P:= I;
    PCK_NOMINA_COM5.GL_APVOL := 0; 
    MI_INCAPE := 0;
    PCK_NOMINA_COM5.GL_SUELDO := 0;
    PCK_NOMINA_COM5.GL_SUELDOP:= 0;
    --FR = IIf(IsNull(rs2!FECHA_DE_RETIRO), 0, rs2!FECHA_DE_RETIRO - 1)
    --FCS = IIf(IsNull(rs2!Fecha_Cambio_Mesada), 0, rs2!Fecha_Cambio_Mesada)
    --(JORDUZ: 27/07/2020)--Se realiza la validacion que la validacion para que cuando la planilla para el disco sea de tipo 'P' pensionados traiga unicamete el acumulado de la mesada 14 'periodo 4'
    IF UN_TIPOPLANILLA = 'P'  AND UN_COMPANIA = '002' AND UN_PERIODO = 4 THEN 
    MI_AC:=PCK_NOMINA.FC_ACUM(UN_COMPANIA  => UN_COMPANIA, 
                              UN_ANO1      => UN_ANIO, 
                              UN_MES1      => UN_MES, 
                              UN_PERIODO1  => CASE WHEN UN_RETROACTIVO <> 0 THEN UN_PERIODORETRO ELSE 4 END, 
                              UN_ANO2      => UN_ANIO, 
                              UN_MES2      => UN_MES, 
                              UN_PERIODO2  => CASE WHEN UN_RETROACTIVO <> 0 THEN UN_PERIODORETRO ELSE 4 END, 
                              UN_IDEMPLEADO=> PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO);
    ELSE
    MI_AC:=PCK_NOMINA.FC_ACUM(UN_COMPANIA  => UN_COMPANIA, 
                              UN_ANO1      => UN_ANIO, 
                              UN_MES1      => UN_MES, 
                              UN_PERIODO1  => CASE WHEN UN_RETROACTIVO <> 0 THEN UN_PERIODORETRO ELSE 1 END, 
                              UN_ANO2      => UN_ANIO, 
                              UN_MES2      => UN_MES, 
                              UN_PERIODO2  => CASE WHEN UN_RETROACTIVO <> 0 THEN UN_PERIODORETRO ELSE 99 END, 
                              UN_IDEMPLEADO=> PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO);
    END IF;
    --(APINEDA:13/09/2019)-Se agrega el valor de la mesada, debido a que no esta cargandose en el plano el valor correspondiente por empleado.
    MI_MESADA := PCK_NOMINA.FC_CNA(2);          

    MI_AC:=PCK_NOMINA.FC_ACUM1(UN_COMPANIA  => UN_COMPANIA, 
                              UN_ANO1      => UN_ANIO, 
                              UN_MES1      => UN_MES, 
                              UN_PERIODO1  => UN_PERIODO, 
                              UN_IDEMPLEADO=> PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO);

    PCK_NOMINA_COM5.GL_NING := CASE WHEN TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(I).FECHA_DE_INGRESO,'MM')) = UN_MES 
                     AND TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(I).FECHA_DE_INGRESO,'YYYY')) = UN_ANIO
              THEN 'X' ELSE ' ' 
              END;
    PCK_NOMINA_COM5.GL_NRET := CASE WHEN PCK_NOMINA_COM5.CSOI(I).FECHA_DE_RETIRO IS NULL
               THEN ' '
               ELSE CASE WHEN TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(I).FECHA_DE_RETIRO,'MM')) = UN_MES 
                          AND TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(I).FECHA_DE_RETIRO,'YYYY')) = UN_ANIO
                    THEN 'X' ELSE ' ' END 
               END;    
    PCK_NOMINA_COM5.GL_NTDA := CASE WHEN PCK_NOMINA.FC_CNA(340) IN(1,4) THEN 'X' ELSE ' ' END;
    PCK_NOMINA_COM5.GL_NTAA := CASE WHEN PCK_NOMINA.FC_CNA(341) IN(1,4) THEN 'X' ELSE ' ' END;
    IF PCK_NOMINA_COM5.GL_NTAA = 'X' AND PCK_NOMINA.FC_CNA(341) IN(1,4) THEN
      IF PCK_NOMINA.FC_CNA(340) = 0 THEN
        PCK_NOMINA_COM5.GL_CODIGOTRASLADOSALUD := PCK_NOMINA_COM6.FC_TRAERDATOSENTIDADESEMPLEADO(
                                                   UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_EMPLEADO    => PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO, 
                                                   UN_FONDOACTUAL => PCK_NOMINA_COM5.CSOI(I).FONDO_SALUD, 
                                                   UN_TIPOFONDO   => 'EPS', 
                                                   UN_PAR         => 3);
      END IF;
    ELSE
      PCK_NOMINA_COM5.GL_CODIGOTRASLADOSALUD := LPAD(' ',6,' ');
    END IF;
    PCK_NOMINA_COM5.GL_CODIGOTRASLADOSALUD := LPAD(NVL(PCK_NOMINA_COM5.GL_CODIGOTRASLADOSALUD,' '),6,' ');

    PCK_NOMINA_COM5.GL_NTDAP := CASE WHEN PCK_NOMINA.FC_CNA(340) IN(2,4) THEN 'X' ELSE ' ' END;
    PCK_NOMINA_COM5.GL_NTAAP := CASE WHEN PCK_NOMINA.FC_CNA(341) IN(2,4) THEN 'X' ELSE ' ' END;
    --TRASLADO DE FONDO PENSION 
    IF PCK_NOMINA_COM5.GL_NTAAP = 'X' AND PCK_NOMINA.FC_CNA(341) IN(2,4) THEN
      PCK_NOMINA_COM5.GL_CODIGOTRASLADOPENSION := PCK_NOMINA_COM6.FC_TRAERDATOSENTIDADESEMPLEADO(
                                                   UN_COMPANIA    => UN_COMPANIA, 
                                                   UN_EMPLEADO    => PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO, 
                                                   UN_FONDOACTUAL => PCK_NOMINA_COM5.CSOI(I).ID_DEL_FONDO, 
                                                   UN_TIPOFONDO   => 'AFP', 
                                                   UN_PAR         => 3);
    ELSE
      PCK_NOMINA_COM5.GL_CODIGOTRASLADOPENSION := LPAD(' ',6,' ');
    END IF;
    PCK_NOMINA_COM5.GL_CODIGOTRASLADOPENSION := LPAD(NVL(PCK_NOMINA_COM5.GL_CODIGOTRASLADOPENSION,' '),6, ' ');

    PCK_NOMINA_COM5.GL_NVSP := CASE WHEN PCK_NOMINA_COM5.CSOI(I).FECHA_ULTSUELDO IS NULL AND PCK_NOMINA.FC_CNA(306) = 0
               THEN ' ' 
               ELSE CASE WHEN (TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(I).FECHA_ULTSUELDO,'MM')) = UN_MES 
                           AND TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(I).FECHA_ULTSUELDO,'YYYY')) = UN_ANIO
                           OR PCK_NOMINA.FC_CNA(306) <> 0)
                    THEN 'X' ELSE ' ' END 
               END;
    PCK_NOMINA_COM5.GL_VTE := ' ';	    
    PCK_NOMINA_COM5.GL_NVST := CASE WHEN PCK_NOMINA.FC_CNA(10) <> 0 
                                      OR PCK_NOMINA.FC_CNA(70) <> 0 
                                      OR PCK_NOMINA.FC_CNA(150) <> 0 
                                      OR PCK_NOMINA.FC_CNA(506) <> 0
                               THEN 'X' ELSE ' ' END;
    PCK_NOMINA_COM5.GL_NSLN := CASE WHEN PCK_NOMINA.FC_CNA(356) <> 0 OR PCK_NOMINA.FC_CNA(357) <> 0 OR PCK_NOMINA.FC_CNA(359) <> 0 THEN 'X' ELSE ' ' END;                               
    PCK_NOMINA_COM5.GL_NIGE := CASE WHEN PCK_NOMINA.FC_CNA(350) <> 0 OR PCK_NOMINA.FC_CNA(351) <> 0 OR PCK_NOMINA.FC_CNA(358) <> 0 OR PCK_NOMINA.FC_CNA(352) <> 0 OR PCK_NOMINA.FC_CNA(354) <> 0 OR PCK_NOMINA.FC_CNA(355) <> 0 THEN 'X' ELSE ' ' END;
    PCK_NOMINA_COM5.GL_NLMA := CASE WHEN PCK_NOMINA.FC_CNA(353) <> 0 THEN 'X' ELSE ' ' END;     
    PCK_NOMINA_COM5.GL_NVAC := CASE WHEN PCK_NOMINA.FC_CNA(35) <> 0 OR (PCK_NOMINA.FC_CNA(363) + PCK_NOMINA.FC_CNA(364) + PCK_NOMINA.FC_CNA(365)) <> 0 THEN 'X' ELSE ' ' END;  
    PCK_NOMINA_COM5.GL_NAVP := CASE WHEN PCK_NOMINA_COM5.CSOI(I).APVOL <> 0 OR PCK_NOMINA_COM5.CSOI(I).APAFC <> 0 THEN 'X' ELSE ' ' END;
    PCK_NOMINA_COM5.GL_NVCT := ' ';    
    PCK_NOMINA_COM5.GL_NAVP := CASE WHEN PCK_NOMINA.FC_CNA(124) <> 0 OR PCK_NOMINA.FC_CNA(127) <> 0 THEN 'X' ELSE ' ' END;      

    PCK_NOMINA_COM5.GL_NIRP :='00';
    IF PCK_NOMINA_COM5.CSOI(I).DIASATEP <> 0  THEN
      IF (PCK_NOMINA_COM5.CSOI(I).DIASATEP
                       + CASE WHEN PCK_NOMINA.FC_CNA(358) > 0 
                              THEN 1 
                              ELSE 0 END
                              ) >30 THEN
        PCK_NOMINA_COM5.GL_NIRP := '30';
      ELSE
        PCK_NOMINA_COM5.GL_NIRP :=  PCK_SYSMAN_UTL.FC_STRZERO(NVL(PCK_NOMINA_COM5.CSOI(I).DIASATEP
                    + CASE WHEN PCK_NOMINA.FC_CNA(358) > 0 
                              THEN 1 
                              ELSE 0 END,0)
                      , 2) ;
      END IF;
    END IF;         
    PCK_NOMINA_COM5.GL_NIRP := PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN PCK_NOMINA_COM5.GL_NIRP='  ' OR PCK_NOMINA_COM5.GL_NIRP IS NULL THEN '00' ELSE PCK_NOMINA_COM5.GL_NIRP END, 2);        
    PCK_NOMINA_COM5.GL_SUELDO := PCK_NOMINA_COM5.CSOI(I).MESADA_PENSIONAL;

    IF PCK_NOMINA_COM5.GL_SUELDO <= PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_PARENTR.PARAMETRO20
                                                           ,UN_PRECISION => 0)  
    AND (PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_PARENTR.PARAMETRO20
                                ,UN_PRECISION => 0) - PCK_NOMINA_COM5.GL_SUELDO) <= 500 THEN                                      
        PCK_NOMINA_COM5.GL_SUELDO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    => PCK_PARENTR.PARAMETRO20 + CASE WHEN (PCK_NOMINA_COM5.CSOI(I).PENSIONCOMPARTIDA = 0) AND PCK_NOMINA_COM5.CSOI(I).SALARIO_BASE_IBC > PCK_PARENTR.PARAMETRO20 THEN 1 ELSE 0 END
                                                           ,UN_PRECISION => CASE WHEN PCK_NOMINA_COM5.CSOI(I).PENSIONCOMPARTIDA = 0 AND PCK_NOMINA_COM5.CSOI(I).SALARIO_BASE_IBC > PCK_PARENTR.PARAMETRO20 THEN PCK_NOMINA.GL_RBASE1990 ELSE 0 END);                                            
    END IF;
    IF PCK_NOMINA_COM5.GL_SUELDO > PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_PARENTR.PARAMETRO20 + CASE WHEN (PCK_NOMINA_COM5.CSOI(I).PENSIONCOMPARTIDA = 0) AND PCK_NOMINA_COM5.CSOI(I).SALARIO_BASE_IBC > PCK_PARENTR.PARAMETRO20 THEN 1 ELSE 0 END
                                                          ,UN_PRECISION => CASE WHEN PCK_NOMINA_COM5.CSOI(I).PENSIONCOMPARTIDA = 0 AND PCK_NOMINA_COM5.CSOI(I).SALARIO_BASE_IBC > PCK_PARENTR.PARAMETRO20 THEN PCK_NOMINA.GL_RBASE1990 ELSE 0 END)
    AND (PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_PARENTR.PARAMETRO20
                                                           ,UN_PRECISION => CASE WHEN PCK_NOMINA_COM5.CSOI(I).PENSIONCOMPARTIDA = 0 AND PCK_NOMINA_COM5.CSOI(I).SALARIO_BASE_IBC > PCK_PARENTR.PARAMETRO20 THEN PCK_NOMINA.GL_RBASE1990 ELSE 0 END) - PCK_NOMINA_COM5.GL_SUELDO) <= 500 THEN   
       PCK_NOMINA_COM5.GL_SUELDO := ROUND(PCK_NOMINA_COM5.CSOI(I).SALARIO_BASE_IBC + CASE WHEN PCK_NOMINA_COM5.CSOI(I).PENSIONCOMPARTIDA = 0 AND PCK_NOMINA_COM5.CSOI(I).SALARIO_BASE_IBC > PCK_PARENTR.PARAMETRO20 THEN 1 ELSE 0 END, CASE WHEN PCK_NOMINA_COM5.CSOI(I).PENSIONCOMPARTIDA = 0 AND PCK_NOMINA_COM5.CSOI(I).SALARIO_BASE_IBC > PCK_PARENTR.PARAMETRO20 THEN PCK_NOMINA.GL_RBASE1990 ELSE 0 END);
    END IF;     

    IF PCK_NOMINA.GL_OPERADOR IN('NUEVOSOI','SOI','SOI.','NUEVOSOI.') THEN        
      MI_DOBLEPENSION := PCK_NOMINA_COM5.FC_BUSCARSUMAIBCDOBLEPENSION(
                                             UN_COMPANIA    => UN_COMPANIA, 
                                             UN_IDEMPLEADO  => PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO, 
                                             UN_NUMERO_DCTO => PCK_NOMINA_COM5.CSOI(I).NUMERO_DCTO, 
                                             UN_TIPOVINCULACION => PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION);
      IF MI_DOBLEPENSION <> 0 THEN
         PCK_NOMINA_COM5.GL_SUELDO := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_DOBLEPENSION
                                                             ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
         MI_DOBLEPENSION := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_DOBLEPENSION
                                                   ,UN_PRECISION => 0);                                                             
      END IF;
    END IF;

   PCK_NOMINA_COM5.GL_NUMDIAS := TO_CHAR(PCK_NOMINA.FC_CNA(9) + PCK_NOMINA.FC_CNA(11) + PCK_NOMINA.FC_CNA(35) + PCK_NOMINA.FC_CNA(350) + PCK_NOMINA.FC_CNA(351) + PCK_NOMINA.FC_CNA(352) 
                 + PCK_NOMINA.FC_CNA(353) + PCK_NOMINA.FC_CNA(354) + PCK_NOMINA.FC_CNA(355) + PCK_NOMINA.FC_CNA(358) + PCK_NOMINA.FC_CNA(368) + PCK_NOMINA.FC_CNA(363) + PCK_NOMINA.FC_CNA(364) + PCK_NOMINA.FC_CNA(365));

   PCK_NOMINA_COM5.GL_NUMDIAS := CASE WHEN TO_NUMBER(PCK_NOMINA_COM5.GL_NUMDIAS) < 0 THEN '0' ELSE PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.GL_NUMDIAS, 2) END;
   PCK_NOMINA_COM5.GL_NUMDIAS := CASE WHEN TO_NUMBER(PCK_NOMINA_COM5.GL_NUMDIAS) > 30 THEN '30' ELSE PCK_NOMINA_COM5.GL_NUMDIAS END;               
   IF TO_NUMBER(PCK_NOMINA_COM5.GL_NUMDIAS) > 0 AND PCK_NOMINA_COM5.CSOI(I).DIASRIESGOS > TO_NUMBER(PCK_NOMINA_COM5.GL_NUMDIAS) THEN
    PCK_NOMINA_COM5.GL_NUMDIAS := PCK_SYSMAN_UTL.FC_STRZERO(TO_CHAR(PCK_NOMINA_COM5.CSOI(I).DIASRIESGOS), 2);
   END IF; 
   PCK_NOMINA_COM5.GL_NUMDIAS := CASE WHEN TO_NUMBER(PCK_NOMINA_COM5.GL_NUMDIAS) = 0 AND (UN_RETROACTIVO <> 0 OR PCK_NOMINA.FC_CNA(306) <> 0) THEN '30' ELSE PCK_NOMINA_COM5.GL_NUMDIAS END;               

    MI_INCAPE := PCK_NOMINA.FC_SUMACONA(370, 372) + PCK_NOMINA.FC_SUMACONA(374, 377);
    MI_ADS   := PCK_NOMINA.FC_CNA(113) + PCK_NOMINA.FC_CNA(116) + PCK_NOMINA.FC_CNA(114) + PCK_NOMINA.FC_CNA(119); 

    IF(MI_INCAPE > 0) THEN
      BEGIN
        SELECT SUBSTR(MIN(INCAPACIDADES.AUTO) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM),1,9)
        INTO MI_NUMINCAP
        FROM INCAPACIDADES 
        WHERE COMPANIA      = UN_COMPANIA
          AND ID_DE_EMPLEADO= PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO
          AND ANO           = UN_ANIO
          AND MES           = UN_MES;
      EXCEPTION WHEN NO_DATA_FOUND THEN  
        MI_NUMINCAP:=' ';
      END;
    ELSE
      MI_NUMINCAP:=' ';
    END IF;
    MI_NUMINCAP := RPAD(MI_NUMINCAP,9,' ');
    IF(PCK_NOMINA.FC_CNA(UN_CONCEPTO => 373) > 0) THEN
      BEGIN
        SELECT MIN(INCAPACIDADES.AUTO) KEEP (DENSE_RANK FIRST ORDER BY ROWNUM)
        INTO MI_NUMLICMATER
        FROM INCAPACIDADES 
        WHERE COMPANIA       = UN_COMPANIA
          AND ID_DE_EMPLEADO = PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO
          AND ANO            = UN_ANIO
          AND MES            = UN_MES
          AND INCAPACIDAD    ='04';
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_NUMLICMATER:=' ';
      END;
    ELSE
      MI_NUMLICMATER:=' ';
    END IF;
    MI_NUMLICMATER:=RPAD(MI_NUMLICMATER,9,' ');

    IF PCK_NOMINA_COM5.CSOI(I).PAGOS > 0 
        OR PCK_NOMINA_COM5.CSOI(I).DIASINCAPCIDADES <> 0 
        OR PCK_NOMINA_COM5.CSOI(I).DIASRIESGOS <> 0 THEN
      IF MI_LINN >= 45 THEN
        PCK_NOMINA_COM5.GL_DISCOINTEGRADO:= PCK_NOMINA_COM5.GL_DISCOINTEGRADO || --CHR(13) ||CHR(10) ||
                       PCK_NOMINA_COM5.FC_ENCABEZADOINTEGRADO(UN_RAZONSOCIAL  => MI_RAZONSOCIAL
                                                             ,UN_PERCOTIZA    => MI_PERCOTIZ
                                                             ,UN_INICIAL      =>0);
        MI_LINN := 0;
      END IF;
      MI_LINN := MI_LINN + 1;
      MI_NUMEMP := MI_NUMEMP + 1;

      IF (TO_NUMBER(PCK_NOMINA_COM5.GL_NUMDIAS) < 30) 
         AND PCK_NOMINA_COM5.GL_NING = ' ' AND PCK_NOMINA_COM5.GL_NRET = ' ' AND PCK_NOMINA_COM5.GL_NTDA = ' ' 
         AND PCK_NOMINA_COM5.GL_NTAA = ' ' AND PCK_NOMINA_COM5.GL_NTDAP = ' ' 
         AND PCK_NOMINA_COM5.GL_NTAAP = ' ' AND PCK_NOMINA_COM5.GL_NSLN = ' ' 
         AND PCK_NOMINA.FC_CNA(336) = 0 THEN
        MI_MSGERROR(1).CLAVE := 'EMPLEADO';
        MI_MSGERROR(1).VALOR := PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO || ' ' || PCK_NOMINA_COM5.CSOI(I).APELLIDO1 || ' ' || PCK_NOMINA_COM5.CSOI(I).APELLIDO2 || ' ' || PCK_NOMINA_COM5.CSOI(I).NOMBRES; 
        PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => UN_COMPANIA,
                                   UN_MENSAJE_COD => PCK_ERRORES.ALER_MENOR30_SIN_NOVEDAD,
                                   UN_REEMPLAZOS  => MI_MSGERROR);    
      END IF;
      PCK_NOMINA_COM5.GL_CTAR := PCK_NOMINA_SEGSOCI.FC_PORCENRIESGO(UN_COMPANIA => UN_COMPANIA
                                                   ,UN_IDEMPLEADO => PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO) / 100;
      IF TO_NUMBER(PCK_NOMINA_COM5.GL_NUMDIAS) < 30 
        AND PCK_NOMINA_COM5.GL_NING = ' ' OR PCK_NOMINA_COM5.GL_NRET = ' ' 
        OR PCK_NOMINA_COM5.GL_NTDA = ' ' OR PCK_NOMINA_COM5.GL_NTAA = ' ' 
        OR PCK_NOMINA_COM5.GL_NSLN = ' ' THEN
        MI_MSGERROR(1).CLAVE := 'EMPLEADO';
        MI_MSGERROR(1).VALOR := PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO || ' ' || PCK_NOMINA_COM5.CSOI(I).APELLIDO1 || ' ' || PCK_NOMINA_COM5.CSOI(I).APELLIDO2 || ' ' || PCK_NOMINA_COM5.CSOI(I).NOMBRES; 
        PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => UN_COMPANIA,
                                   UN_MENSAJE_COD => PCK_ERRORES.ALER_MENOR30_SIN_NOVEDAD_ING,
                                   UN_REEMPLAZOS  => MI_MSGERROR);    
      END IF; 
      PCK_NOMINA_COM5.GL_CODAIFP := RPAD(' ', 6, ' ');

      IF PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION IN('4','7','11','14') THEN          
        PCK_NOMINA_COM5.GL_CODAIFP := RPAD(NVL(PCK_NOMINA_COM5.CSOI(I).ID_DEL_FONDO_SOI,' '), 6, ' ');
        IF PCK_NOMINA_COM5.GL_CODAIFP = '      ' THEN
          MI_MSGERROR(1).CLAVE := 'EMPLEADO';
          MI_MSGERROR(1).VALOR := PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO || ' ' || PCK_NOMINA_COM5.CSOI(I).APELLIDO1 || ' ' || PCK_NOMINA_COM5.CSOI(I).APELLIDO2 || ' ' || PCK_NOMINA_COM5.CSOI(I).NOMBRES; 
          PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => UN_COMPANIA,
                                     UN_MENSAJE_COD => PCK_ERRORES.ALER_TIPOVICULACIONCODNOVALIDO,
                                     UN_REEMPLAZOS  => MI_MSGERROR);    
        END IF;                
      END IF; 

      PCK_NOMINA_COM5.GL_CODAIFS := RPAD(NVL(PCK_NOMINA_COM5.CSOI(I).FONDO_SALUD_SOI,' '), 6, ' ');
      PCK_NOMINA_COM5.GL_CODAICC := RPAD(' ', 6, ' ');

      --(APINEDA:18/02/2020)- se asigna valor del aporte por parte del pensionado a caja de compensaciÃ³n a la variable GL_APORTECCFPENSION      
      PCK_NOMINA_COM5.GL_APORTECCFPENSION := PCK_NOMINA.FC_CNA(GL_CN_APORTECCFPENSION);
      --(APINEDA:13/05/2019)- Se agrega validaciÂ¿ara imprimir en plano el identificador de la caja de compensaciÂ¿uando el pensionado aporta un porcentaje diferente al estÂ¿ar.
      IF PCK_NOMINA_COM5.CSOI(I).CAJAS <> 0 OR PCK_NOMINA_COM5.GL_APORTECCFPENSION <> 0 THEN
         PCK_NOMINA_COM5.GL_CODAICC := RPAD(PCK_NOMINA_COM5.CSOI(I).CAJA_COMPENSACION_SOI, 6,' ');
      END IF;

      PCK_NOMINA_COM5.GL_PR := PCK_PARENTR.PARAMETRO39 - TRUNC(PCK_PARENTR.PARAMETRO39);
		  IF PCK_NOMINA_COM5.GL_PR < 1 THEN
        PCK_NOMINA_COM5.GL_PR := 10;
		  ELSE
        PCK_NOMINA_COM5.GL_PR :=1;
		  END IF;      

      MI_TIPODCTO := PCK_NOMINA_COM5.FC_DCTIDENTIDAD(UN_TIPO => NVL(PCK_NOMINA_COM5.CSOI(I).DCTO_IDENTIDAD, ''));           
      PCK_NOMINA_COM5.GL_SALARIOINTEGRAL := ' ';
      IF PCK_NOMINA_COM5.CSOI(I).ID_DE_TIPO = '02' THEN 
        PCK_NOMINA_COM5.GL_SALARIOINTEGRAL := 'X';
      END IF;
      PCK_NOMINA_COM5.GL_FSPPLANILLA := 0;
      PCK_NOMINA_COM5.GL_FSPADICPLANILLA := 0;

         IF PCK_NOMINA.GL_CALCULAFSP = 'SI' THEN
              IF PCK_NOMINA_COM5.CSOI(I).FSP <> 0 THEN
                  IF (PCK_NOMINA_COM5.CSOI(I).FSP / 2) = PCK_SYSMAN_UTL.FC_ROUND_100(((PCK_NOMINA_COM5.CSOI(I).BASE * 1 / 100) / 2)  , MI_PARAMETRO1990APLICA,(- 0.49 + PCK_NOMINA.GL_SUMARSS1990)
                                                                                   , PCK_NOMINA.GL_RAPORTES1990) THEN                      
                      PCK_NOMINA_COM5.GL_FSPPLANILLA := (PCK_NOMINA_COM5.CSOI(I).FSP / 2);
                      PCK_NOMINA_COM5.GL_FSPADICPLANILLA := (PCK_NOMINA_COM5.CSOI(I).FSP / 2) + PCK_NOMINA_COM5.CSOI(I).FSPADICIONAL;
                  ELSE
                      IF PCK_NOMINA_COM5.CSOI(I).BASE > (20 * PCK_PARENTR.PARAMETRO20) THEN
                          PCK_NOMINA_COM5.GL_FSPPLANILLA := PCK_SYSMAN_UTL.FC_ROUND_100(((PCK_NOMINA_COM5.CSOI(I).BASE * 2 / 100) / 2)  , MI_PARAMETRO1990APLICA,(- 0.49 + PCK_NOMINA.GL_SUMARSS1990)
                                                                                   ,PCK_NOMINA.GL_RAPORTES1990);
                          PCK_NOMINA_COM5.GL_FSPADICPLANILLA := PCK_SYSMAN_UTL.FC_ROUND_100( ((PCK_NOMINA_COM5.CSOI(I).BASE * 2 / 100) / 2)  , MI_PARAMETRO1990APLICA,(- 0.49 + PCK_NOMINA.GL_SUMARSS1990)
                                                                                   ,PCK_NOMINA.GL_RAPORTES1990) + PCK_NOMINA_COM5.CSOI(I).FSPADICIONAL;                                                                          
                          PCK_NOMINA_COM5.GL_FSPADICPLANILLA := PCK_NOMINA_COM5.CSOI(I).FSP - PCK_NOMINA_COM5.GL_FSPPLANILLA; -- CUANDO TIENE DIFERENCIA
                      ELSE
                          PCK_NOMINA_COM5.GL_FSPPLANILLA := PCK_SYSMAN_UTL.FC_ROUND_100(((PCK_NOMINA_COM5.CSOI(I).BASE * 1 / 100) / 2)  , MI_PARAMETRO1990APLICA,(- 0.49 + PCK_NOMINA.GL_SUMARSS1990)
                                                                                   ,PCK_NOMINA.GL_RAPORTES1990);                         
                          PCK_NOMINA_COM5.GL_FSPADICPLANILLA := PCK_SYSMAN_UTL.FC_ROUND_100( ((PCK_NOMINA_COM5.CSOI(I).BASE * 1 / 100) / 2)  , MI_PARAMETRO1990APLICA, (- 0.49 + PCK_NOMINA.GL_SUMARSS1990)
                                                                                   , PCK_NOMINA.GL_RAPORTES1990) + PCK_NOMINA_COM5.CSOI(I).FSPADICIONAL;
                          PCK_NOMINA_COM5.GL_FSPADICPLANILLA := PCK_NOMINA_COM5.CSOI(I).FSP - PCK_NOMINA_COM5.GL_FSPADICPLANILLA;
                      END IF;
                  END IF;                    
              ELSE
                  PCK_NOMINA_COM5.GL_FSPPLANILLA := PCK_NOMINA_COM5.CSOI(I).FSP;
                  PCK_NOMINA_COM5.GL_FSPADICPLANILLA := PCK_NOMINA_COM5.CSOI(I).FSPADICIONAL;
              END IF;
        ELSE             
              PCK_NOMINA_COM5.GL_FSPPLANILLA := PCK_NOMINA_COM5.CSOI(I).BASE * PCK_NOMINA_COM5.CSOI(I).FSPADICIONAL;
              PCK_NOMINA_COM5.GL_FSPADICPLANILLA := PCK_NOMINA_COM5.CSOI(I).FSP;             
        END IF;
        IF PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION = '17' AND PCK_NOMINA_COM5.CSOI(I).PENSION = 0 THEN -- PARA CUANDO NO APORTA EN PENSION 15/08/06
            PCK_NOMINA_COM5.GL_FSPPLANILLA := 0;
            PCK_NOMINA_COM5.GL_FSPADICPLANILLA := 0;
        END IF;
        IF (PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION <> '4' AND PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION <> '7' And PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION <> '11' AND PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION <> '14') AND PCK_NOMINA_COM5.GL_FSPADICPLANILLA <> 0 THEN -- PARA tipo pensionado una sola columna
           PCK_NOMINA_COM5.GL_FSPADICPLANILLA := PCK_NOMINA_COM5.GL_FSPADICPLANILLA + PCK_NOMINA_COM5.GL_FSPPLANILLA;
           PCK_NOMINA_COM5.GL_FSPPLANILLA := 0;
        END IF;


        IF PCK_NOMINA_COM5.CSOI(I).TIPOVINCULACION IN ('2','3','4','5','6','7') THEN
           IF NVL(PCK_NOMINA_COM5.CSOI(I).APELLIDO1CAUSANTE, '') = '' THEN
              MI_MSGERROR(1).CLAVE := 'EMPLEADO';
              MI_MSGERROR(1).VALOR := PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO || ' ' || PCK_NOMINA_COM5.CSOI(I).APELLIDO1 || ' ' || PCK_NOMINA_COM5.CSOI(I).APELLIDO2 || ' ' || PCK_NOMINA_COM5.CSOI(I).NOMBRES; 
              PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => UN_COMPANIA,
                                         UN_MENSAJE_COD => PCK_ERRORES.ALER_FALTAAPELLIDOCAUSANTE,
                                         UN_REEMPLAZOS  => MI_MSGERROR);               
           END IF;
           IF NVL(PCK_NOMINA_COM5.CSOI(I).NOMBRE1CAUSANTE, '') = '' THEN
              MI_MSGERROR(1).CLAVE := 'EMPLEADO';
              MI_MSGERROR(1).VALOR := PCK_NOMINA_COM5.CSOI(I).ID_DE_EMPLEADO || ' ' || PCK_NOMINA_COM5.CSOI(I).APELLIDO1 || ' ' || PCK_NOMINA_COM5.CSOI(I).APELLIDO2 || ' ' || PCK_NOMINA_COM5.CSOI(I).NOMBRES; 
              PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => UN_COMPANIA,
                                         UN_MENSAJE_COD => PCK_ERRORES.ALER_FALTANOMBRECAUSANTE,
                                         UN_REEMPLAZOS  => MI_MSGERROR);                         
           END IF;
        END IF;

        IF UN_TIPOESTRUCTURA = '2388' THEN         
          MI_CAMPOSNUEVOS2388N := PCK_NOMINA_COM5.FC_CAMPOSNUEVOS2388_P(UN_ANNO     => UN_ANIO
                                               ,UN_MMES     => UN_MES
                                               ,UN_PNING    => PCK_NOMINA_COM5.GL_NING
                                               ,UN_PNRET    => PCK_NOMINA_COM5.GL_NRET
                                               ,UN_PNVSP    => PCK_NOMINA_COM5.GL_NVSP                                               
                                               ,UN_I        => I
                                               );                                                                
        END IF;        

      IF (PCK_NOMINA.GL_PAGOUPC = 'SI' OR PCK_NOMINA_COM5.GL_UPCADICIONALPILA = 'SI') 
      AND CASE WHEN  PCK_NOMINA_COM5.GL_UPCPATRONO ='SI' THEN PCK_NOMINA_COM5.CSOI(I).UPCPATRONO ELSE PCK_NOMINA_COM5.CSOI(I).UPC END > 0 THEN
          PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                           PCK_NOMINA_COM5.FC_REGISTROPLANO_P (UN_COMPANIA         => UN_COMPANIA
                                                            ,UN_I                => I
                                                            ,UN_TIPOESTRUCTURA   => UN_TIPOESTRUCTURA
                                                            ,UN_CLASEREGISTRO    => 'UPC'
                                                            ,UN_TIPOPLANILLA     => UN_TIPOPLANILLA
                                                            ,UN_CAMPOSNUEVOS2388 => MI_CAMPOSNUEVOS2388N
                                                            ,UN_MESADA => MI_MESADA
                                                            ,UN_DOBLEPENSION => MI_DOBLEPENSION);      
      ELSE  
          PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                           PCK_NOMINA_COM5.FC_REGISTROPLANO_P (UN_COMPANIA         => UN_COMPANIA
                                                            ,UN_I                => I
                                                            ,UN_TIPOESTRUCTURA   => UN_TIPOESTRUCTURA
                                                            ,UN_CLASEREGISTRO    => 'OTRO'
                                                            ,UN_TIPOPLANILLA     => UN_TIPOPLANILLA
                                                            ,UN_CAMPOSNUEVOS2388 => MI_CAMPOSNUEVOS2388N
                                                            ,UN_MESADA => MI_MESADA
                                                            ,UN_DOBLEPENSION => MI_DOBLEPENSION);                                                         
      END IF;                  

    END IF; --IF PCK_NOMINA_COM5.CSOI(I).PAGOS > 0 
  END LOOP;

END PR_SISTEMAINTEGRADOELECTRO_P;

FUNCTION FC_BUSCARSUMAIBCDOBLEPENSION 
/*
      NAME              : FC_BUSCARSUMAIBCDOBLEPENSION MIGRADO DE ACCESS BUSCAR_SUMA_IBC_DOBLEPENSION
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
      DATE MIGRADOR     : 14/08/2018
      TIME              : 05:00 PM
      SOURCE MODULE     : NominaH2018.07.01 MPV UNIFICADAS 13072018 - 2388-1990 ALCTOC_ESPBOY_CMI
      DESCRIPTION       : Suma el valor de SALARIO_BASE_IBC de la tabla PERSONAL_HISTORICO para validar si se tiene doble pensiÂ¿     MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : 
*/
(
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_IDEMPLEADO      IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
  UN_NUMERO_DCTO     IN PCK_SUBTIPOS.TI_TERCERO,
  UN_TIPOVINCULACION IN PCK_SUBTIPOS.TI_ENTERO
) RETURN PCK_SUBTIPOS.TI_DOBLE
AS
 	MI_DOBLEPENSION  PCK_SUBTIPOS.TI_DOBLE;
 	MI_MSGERROR PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN	
	-- TICKET 7719342 ECABRERA: SE CAMBIA CONSULTA OPTIMIZANDOLA SEGUN RECOMENDACION EN CONJUNTO CON CESAR CEBALLOS
	BEGIN
		SELECT 	NVL(SUM(PERSONAL_HISTORICO.SALARIO_BASE_IBC),0)
    	  INTO 	MI_DOBLEPENSION
  		  FROM 	PERSONAL_HISTORICO 
 		  WHERE PERSONAL_HISTORICO.COMPANIA = UN_COMPANIA 
  				AND PERSONAL_HISTORICO.ID_DE_PROCESO=PCK_NOMINA.GL_PROCESOACTUAL
   				AND PERSONAL_HISTORICO.ANO=PCK_NOMINA.GL_ANOACTUAL
   				AND PERSONAL_HISTORICO.MES=PCK_NOMINA.GL_MESACTUAL
  				AND PERSONAL_HISTORICO.ID_DE_EMPLEADO <> UN_IDEMPLEADO 
   				AND PERSONAL_HISTORICO.NUMERO_DCTO = UN_NUMERO_DCTO 
   				AND (PERSONAL_HISTORICO.ESTADO_ACTUAL = 1 OR PERSONAL_HISTORICO.ESTADO_ACTUAL = 2) 
   				AND PERSONAL_HISTORICO.FECHA_DE_RETIRO IS NULL 
   				AND PERSONAL_HISTORICO.TIPOVINCULACION = UN_TIPOVINCULACION; 
   	 --<TAR:1000105732 FECHA:13/05/2021 AUTOR:CP>-- Se quita la excepcion por que no debe generar error si no devolver 0 en la funcion eso se realiza en reunion con el Ingeniero Mauricio Puerto
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_DOBLEPENSION := 0;
         --RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;
  	RETURN MI_DOBLEPENSION;
  	-- FIN 7719342
END FC_BUSCARSUMAIBCDOBLEPENSION;

FUNCTION FC_CAMPOSNUEVOS2388_P(
		UN_ANNO           IN PCK_SUBTIPOS.TI_ANIO,
		UN_MMES           IN PCK_SUBTIPOS.TI_MES, 
		UN_PNING          IN VARCHAR2, 
		UN_PNRET          IN VARCHAR2,
    UN_PNVSP          IN VARCHAR2,  
		UN_I              IN NUMBER
    )
    /*
    NAME              : TRAERNOVEDADES_CAMPOSNUEVOS2388_P  --> EN ACCESS 
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : ANDREA PINEDA OVALLE
    DATE MIGRATION    : 15/08/2018
    TIME              : 12:47 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    @NAME:   
  */
    --UN_I es la posiciÂ¿el vector para consultar PCK_NOMINA_COM5.CSOI;
    --     que se debe enviar desde la funciÂ¿R_SISTEMAINTEGRADOELECTRO_P
	RETURN CLOB
	AS
	BEGIN
    PCK_NOMINA_COM5.PR_LIMPIARCAMPSO80A97();

    IF UN_PNING <> ' '  THEN --NOVEDAD DE INGRESO
      PCK_NOMINA_COM5.GL_CAMPO80:= TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_DE_INGRESO, 'YYYY-MM-DD');
    END IF;
    IF UN_PNRET <> ' ' THEN --NOVEDAD DE RETIRO
      PCK_NOMINA_COM5.GL_CAMPO81 :=  TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_DE_RETIRO, 'YYYY-MM-DD');
    END IF;
    IF UN_PNVSP <> ' ' THEN --NOVEDAD DE SALARIO PERMANENTE      
      IF TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_ULTSUELDO,'MM')) = UN_MMES 
        AND TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_ULTSUELDO,'YYYY')) = UN_ANNO THEN
        PCK_NOMINA_COM5.GL_CAMPO82 := TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_CAMBIO_MESADA, 'YYYY-MM-DD');
      END IF;
    END IF;

    RETURN PCK_NOMINA_COM5.GL_CAMPO80 || 
           PCK_NOMINA_COM5.GL_CAMPO81 || 
           PCK_NOMINA_COM5.GL_CAMPO82;
END FC_CAMPOSNUEVOS2388_P;

FUNCTION FC_REGISTROPLANO_P(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_I              IN NUMBER,
    UN_TIPOESTRUCTURA IN VARCHAR2,
    UN_CLASEREGISTRO  IN VARCHAR2,
    UN_TIPOPLANILLA  IN VARCHAR2,
    UN_CAMPOSNUEVOS2388 IN CLOB,
    UN_MESADA        IN NUMBER,
    UN_DOBLEPENSION  IN PCK_SUBTIPOS.TI_DOBLE
    )
    /*
    NAME              : PART1 de FC PR_SISTEMAINTEGRADOELECTRO_P  --> EN ACCESS Relacionado  -
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : ANDREA PINEDA OVALLE
    DATE MIGRATION    : 15/08/2018
    TIME              : 03:08 PM
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:
  */
    --UN_I es la posiciÂ¿el vector para consultar PCK_NOMINA_COM5.CSOI;
    --     que se debe enviar desde la funciÂ¿R_SISTEMAINTEGRADOELECTRONICO
RETURN CLOB
AS
    MI_PART1            CLOB;
    MI_PART2            CLOB;
    MI_AAPELLIDO1       VARCHAR2(30);
    MI_AAPELLIDO2       VARCHAR2(30);
    MI_NOMBRES          VARCHAR2(255);
    MI_PNOMBRE          VARCHAR2(255);
    MI_SNOMBRE          VARCHAR2(255);
    MI_TIPODCTO         VARCHAR2(3);
    MI_SALARIOMINIMO    NUMBER;
    MI_CAMPO36          VARCHAR2(2);
    MI_CAMPO38          VARCHAR2(2);
    MI_CAMPO39          VARCHAR2(2);
    MI_CAMPO42          VARCHAR2(9);
    MI_CAMPO46          VARCHAR2(7);
    MI_CAMPO54          VARCHAR2(7);
    MI_CAMPO61          VARCHAR2(9);
    MI_BASEPENSION      NUMBER;
    MI_PORCPENSION      NUMBER;
    MI_APELLIDO1CAUSANTE       VARCHAR2(40);
    MI_APELLIDO2CAUSANTE       VARCHAR2(40);
    MI_NOMBRE1CAUSANTE         VARCHAR2(40);
    MI_NOMBRE2CAUSANTE         VARCHAR2(40);
    MI_TIPODCTOCAUSANTE        VARCHAR2(3);
    MI_COLOMBIANARESIDENTEEXTERIOR VARCHAR2(3);
    MI_SUS              VARCHAR2(3) := ' ';
    MI_IND_REDONDEAR    PCK_SUBTIPOS.TI_LOGICO;
    MI_PRUEBA         VARCHAR2(300);
    MI_MESPEN2421     NUMBER;--VARCHAR2(9);
    MI_MESPEN         NUMBER;--VARCHAR2(9);
    MI_PORCENTAJEMAX    NUMBER(20,2);
    MI_CN132            NUMBER;
    MI_PARAMETRO1990APLICA          PARAMETRO.VALOR%TYPE;
BEGIN
    MI_PARAMETRO1990APLICA := UPPER(PCK_PARST.FC_PAR('APLICAR NUEVO REDONDEO DECRETO 1990 FC_ROUND_100', 'NO')) ;
    BEGIN    --Se obtiene el valor de la mesada pensional Res 2421 TAR 10000106296
       SELECT VR_MESPENSION2421, MESADA_PENSIONAL
            INTO MI_MESPEN2421, MI_MESPEN
            FROM PERSONAL
            WHERE COMPANIA      = UN_COMPANIA
            AND ID_DE_EMPLEADO  = PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO;
        EXCEPTION WHEN NO_DATA_FOUND THEN  
        MI_MESPEN2421:='0';
      END;
     --(EAMAYA:26/03/2021)Tar No 1000099832 Se consulta el porcentaje de acuerdo a los rangos de la mesada pensional
    BEGIN
      SELECT PORCENTAJE_MAX
      INTO MI_PORCENTAJEMAX
      FROM SALUD_PENSIONADOS
      WHERE COMPANIA = UN_COMPANIA
       AND ANO = PCK_NOMINA_COM5.CSOI(UN_I).ANO
       AND LIMITE_INFERIOR <= MI_MESPEN2421--PCK_NOMINA_COM5.CSOI(UN_I).MESADA_PENSIONAL
       AND LIMITE_SUPERIOR >= MI_MESPEN2421;--PCK_NOMINA_COM5.CSOI(UN_I).MESADA_PENSIONAL;     
    
    	EXCEPTION WHEN NO_DATA_FOUND THEN
		    MI_PORCENTAJEMAX :=0;
	END;    
    
    PCK_NOMINA_COM5.GL_CONTADOR := PCK_NOMINA_COM5.GL_CONTADOR+1;
    MI_SALARIOMINIMO := PCK_PARENTR.PARAMETRO20;
    MI_TIPODCTO   := PCK_NOMINA_COM5.FC_DCTIDENTIDAD(UN_TIPO => NVL(PCK_NOMINA_COM5.CSOI(UN_I).DCTO_IDENTIDAD, ''));
    MI_AAPELLIDO1 := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(PCK_NOMINA_COM5.CSOI(UN_I).APELLIDO1, ''));
    MI_AAPELLIDO2 := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(PCK_NOMINA_COM5.CSOI(UN_I).APELLIDO2, ''));
    MI_NOMBRES := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(PCK_NOMINA_COM5.CSOI(UN_I).NOMBRES, ''));
    MI_APELLIDO1CAUSANTE := UPPER(PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(PCK_NOMINA_COM5.CSOI(UN_I).APELLIDO1CAUSANTE, '')));
    MI_APELLIDO2CAUSANTE := UPPER(PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(PCK_NOMINA_COM5.CSOI(UN_I).APELLIDO2CAUSANTE, '')));
    MI_NOMBRE1CAUSANTE := UPPER(PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(PCK_NOMINA_COM5.CSOI(UN_I).NOMBRE1CAUSANTE, '')));
    MI_NOMBRE2CAUSANTE := UPPER(PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(PCK_NOMINA_COM5.CSOI(UN_I).NOMBRE2CAUSANTE, '')));
    --JM 7743925 se modifica la funcion para tomar en cuenta los datos de la tabla tipodocumento 
    MI_TIPODCTOCAUSANTE := PCK_NOMINA_COM5.FC_DCTIDENTIDAD(UN_TIPO => NVL(PCK_NOMINA_COM5.CSOI(UN_I).DCTO_IDENTIDADCAUSANTE, ''), UN_DOCUMENTO => NVL(PCK_NOMINA_COM5.CSOI(UN_I).NUMERO_DCTOCAUSANTE, ''), UN_COMPANIA => UN_COMPANIA);
    IF (PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION = '1' OR PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION > 7) AND  PCK_NOMINA_COM5.CSOI(UN_I).ESTADO_ACTUAL <> 2 THEN -- solo entre 2 y 7
        MI_TIPODCTOCAUSANTE := '  ';
    END IF; --JM 7743925  se agrega PCK_NOMINA_COM5.CSOI(UN_I).ESTADO_ACTUAL <> 2 para que no tome en cuenta los pensionados 

    MI_PNOMBRE := '';
    MI_SNOMBRE := '';
    MI_PNOMBRE := CASE WHEN INSTR(MI_NOMBRES,' ',1) = 0
                THEN MI_NOMBRES
                ELSE TRIM(SUBSTR(MI_NOMBRES,1, INSTR(MI_NOMBRES,' ',1)))
                END;
    MI_SNOMBRE := CASE WHEN INSTR(MI_NOMBRES,' ',1)  = 0
                THEN ' '
                ELSE SUBSTR(MI_NOMBRES,INSTR(MI_NOMBRES, ' ', 1, 1)+1, LENGTH(MI_NOMBRES))
                END;

    MI_COLOMBIANARESIDENTEEXTERIOR := 'N';
    MI_COLOMBIANARESIDENTEEXTERIOR := CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).COLOMBIANO_RESIDENTE = 0 THEN 'N' ELSE 'S' END;
    IF UN_TIPOESTRUCTURA = '2388' THEN
        MI_COLOMBIANARESIDENTEEXTERIOR := CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).COLOMBIANO_RESIDENTE = 0 THEN 'N' ELSE 'X' END;
    END IF;

    MI_IND_REDONDEAR := 0;
    IF PCK_NOMINA.GL_OPERADOR IN('MIPLANILLA','MIPLANILLA.COM') THEN
        MI_IND_REDONDEAR := -1;
    END IF;
    IF PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC > PCK_PARENTR.PARAMETRO20 THEN
        MI_IND_REDONDEAR := -1;
    END IF;
    IF PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC <= PCK_PARENTR.PARAMETRO20 THEN
        MI_IND_REDONDEAR := 0;
    END IF;
    IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA,UN_NOMBRE=>'CALCULO PENSIONADOS CONCEPTO 132/115',UN_MODULO=>'6',UN_FECHA_PAR=>(SYSDATE), UN_IND_MAYUS=>'-1') = 'SI' THEN
        IF MI_MESPEN2421 BETWEEN (MI_SALARIOMINIMO * 10) AND (MI_SALARIOMINIMO * 20) THEN 
            MI_CN132 := PCK_SYSMAN_UTL.FC_ROUND_100((MI_MESPEN * 1/100)  , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990,-2);
        ELSIF MI_MESPEN2421 > ((MI_SALARIOMINIMO * 20) + 1) THEN 
            MI_CN132 := PCK_SYSMAN_UTL.FC_ROUND_100((MI_MESPEN * 2/100)  , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990,-2);
        ELSE
            MI_CN132 := 0;
        END IF;
    ELSE 
        MI_CN132 := 0;
    END IF;

    MI_PART1 := /*CAMPO1*/ '02'
                /*CAMPO2*/ || PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.GL_CONTADOR, 7)
                /*CAMPO3*/ || RPAD(UPPER(NVL(MI_AAPELLIDO1, MI_AAPELLIDO2)), 20,' ')
                /*CAMPO4*/ || RPAD(CASE WHEN MI_AAPELLIDO2 IS NULL
                                    THEN ' '
                                    ELSE UPPER(NVL(MI_AAPELLIDO2,' '))
                                    END, 30,' ')
                /*CAMPO5*/ || RPAD(UPPER(MI_PNOMBRE), 20,' ')
                /*CAMPO6*/ || RPAD(UPPER(NVL(MI_SNOMBRE,' ')), 30,' ')--RM 29/03/2019 SE ADICIONA NVL PARACUANDO EL SEGUNDO NOMBRE ES VACIO TOME CORRECTAMENTE EL RPAD
                /*CAMPO7*/ || RPAD(NVL(MI_TIPODCTO, ' '),2, ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                /*CAMPO8*/ || RPAD(PCK_NOMINA_COM5.CSOI(UN_I).NUMERO_DCTO, 16,' ')
                /*CAMPO9*/ || RPAD(NVL(PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION, 2), '01'), 2,' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                /*CAMPO10*/ || RPAD(NVL(CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).PENSIONCOMPARTIDA = 0 THEN 'N' ELSE 'S' END, 'N'), 1, ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                /*CAMPO11*/ || RPAD(UPPER(NVL(MI_APELLIDO1CAUSANTE, NVL(MI_APELLIDO2CAUSANTE,' '))), 20,' ');
    MI_PART1 := MI_PART1 ||
                /*CAMPO12*/ RPAD(CASE WHEN MI_APELLIDO2CAUSANTE IS NULL
                                    THEN ' '
                                    ELSE UPPER(NVL(MI_APELLIDO2CAUSANTE,' '))
                                    END, 30,' ')
                /*CAMPO13*/ || RPAD(UPPER(NVL(MI_NOMBRE1CAUSANTE,' ')), 20,' ')
                /*CAMPO14*/ || RPAD(UPPER(NVL(MI_NOMBRE2CAUSANTE,' ')), 30,' ')
                /*CAMPO15*/ || RPAD(MI_TIPODCTOCAUSANTE, 2, ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                /*CAMPO16*/ || RPAD(CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).NUMERO_DCTOCAUSANTE = PCK_DATOS.CONS_TERCERO THEN ' ' ELSE PCK_NOMINA_COM5.CSOI(UN_I).NUMERO_DCTOCAUSANTE END, 16,' ')
                /*CAMPO17*/ || RPAD(NVL(TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).TIPOPENSIONADO),' '), 1, ' '); --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
    MI_PART1 := MI_PART1 ||
                /*CAMPO18*/ RPAD(MI_COLOMBIANARESIDENTEEXTERIOR, 1 , ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                /*CAMPO19*/ || RPAD(UPPER(PCK_NOMINA_COM5.CSOI(UN_I).DEPARTAMENTO_HAB), 2,' ')
                /*CAMPO20*/ || RPAD(UPPER(PCK_NOMINA_COM5.CSOI(UN_I).CIUDAD_HAB), 3,' ')
                /*CAMPO21*/ || RPAD(MI_COLOMBIANARESIDENTEEXTERIOR, 1, ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                /*CAMPO22*/ || RPAD(PCK_NOMINA_COM5.GL_NING, 1, ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                /*CAMPO23*/ || RPAD(PCK_NOMINA_COM5.GL_NRET, 1, ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                /*CAMPO24*/ || RPAD(PCK_NOMINA_COM5.GL_NTDA, 1, ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                /*CAMPO25*/ || RPAD(PCK_NOMINA_COM5.GL_NTAA, 1, ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                /*CAMPO26*/ || RPAD(PCK_NOMINA_COM5.GL_NTDAP, 1, ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                /*CAMPO27*/ || RPAD(PCK_NOMINA_COM5.GL_NTAAP, 1, ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                /*CAMPO28*/ || RPAD(PCK_NOMINA_COM5.GL_NVSP, 1, ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                /*CAMPO29*/ || RPAD(MI_SUS, 1 , ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                /*CAMPO30*/ || RPAD(CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).ID_DEL_FONDO = 'AFP99'
                                    THEN ' '
                                    ELSE PCK_NOMINA_COM5.GL_CODAIFP
                                    END , 6,' ')
                /*CAMPO31*/ || RPAD(PCK_NOMINA_COM5.GL_CODIGOTRASLADOPENSION, 6,' ')
                /*CAMPO32*/ || RPAD(PCK_NOMINA_COM5.GL_CODAIFS, 6,' ')
                /*CAMPO33*/ || RPAD(PCK_NOMINA_COM5.GL_CODIGOTRASLADOSALUD, 6,' ');
    MI_PART1 := MI_PART1 ||
                /*CAMPO34*/ RPAD(PCK_NOMINA_COM5.GL_CODAICC, 6,' ')
                /*CAMPO35*/ || RPAD(CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('4','7','11','14') THEN PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.GL_NUMDIAS,2) ELSE '00' END, 2, ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                /*CAMPO36*/ || RPAD(CASE WHEN UN_CLASEREGISTRO = 'UPC' THEN
                                        CASE WHEN MI_SUS <> ' ' THEN '00' ELSE PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.GL_NUMDIAS, 2) END
                                    ELSE
                                        CASE WHEN MI_SUS <> ' ' OR PCK_NOMINA_COM5.GL_FSPPLANILLA = 0 AND PCK_NOMINA_COM5.CSOI(UN_I).FSP = 0 THEN '00' ELSE PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.GL_NUMDIAS, 2) END
                                    END, 2, ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                /*CAMPO37*/ || RPAD(PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.GL_NUMDIAS, 2), 2, ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura
                               --(APINEDA:13/05/2019)-Se modifica condiciÂ¿ebido a que no se estaban mostrando los dias cotizados a caja de compensaciÂ¿n el plano. 
                /*CAMPO38*/ || RPAD(CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).CAJAS <> 0 OR GL_APORTECCFPENSION <> 0 THEN PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.GL_NUMDIAS, 2) ELSE '00' END, 2 , ' ') --(MZANGUNA:22/04/2019)-Se adicionan RPAD segun estructura

                /*CAMPO39*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN UN_CLASEREGISTRO = 'UPC' THEN
                                    PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC
                                                                                ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990)
                                ELSE
                                    PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR    => CASE WHEN PCK_NOMINA_COM5.GL_NING = 'X' OR PCK_NOMINA_COM5.GL_NRET = 'X' THEN PCK_NOMINA_COM5.CSOI(UN_I).BASE ELSE PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC + UN_DOBLEPENSION END
                                                                                ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990)
                                END
                               , 9)
                /*CAMPO40*/ || CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).PENSION <> 0 OR (PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN ('4','7','11','14')) THEN
                                    RPAD(NVL('0.' || TO_CHAR((PCK_PARENTR.PARAMETRO39 * PCK_NOMINA_COM5.GL_PR)), '0.155'),7,'0')
                               ELSE '0.00000'
                               END
                /*CAMPO41*/ || PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).PENSION, 9)
                /*CAMPO42*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9)
                /*CAMPO43*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9)
                /*CAMPO44*/ || PCK_SYSMAN_UTL.FC_STRZERO(0, 9)
                /*CAMPO45*/ || CASE WHEN UN_CLASEREGISTRO = 'UPC' THEN
                                PCK_SYSMAN_UTL.FC_STRZERO(0, CASE WHEN UN_TIPOESTRUCTURA = '2388' THEN 10 ELSE 7 END)
                               ELSE
                                PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN /*PCK_NOMINA.CPARENTRADA(1).NIT = '800.091.594-4' AND*/ PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION = '14' THEN --(16/03/2021 JORDUZ) Se agrega validacion para el aporte patronal segun TAR 1000099384
                                (PCK_NOMINA_COM5.CSOI(UN_I).FSPPENSIONCAQ)/2 ELSE PCK_NOMINA_COM5.GL_FSPPLANILLA END, CASE WHEN UN_TIPOESTRUCTURA = '2388' THEN 10 ELSE 7 END)

                            END;
     MI_PART1 := MI_PART1 ||
                /*CAMPO46*/ CASE WHEN UN_CLASEREGISTRO = 'UPC' THEN
                              PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.GL_FSPPLANILLA, CASE WHEN UN_TIPOESTRUCTURA = '2388' THEN 10 ELSE 7 END)
                            ELSE
                            PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN /*PCK_NOMINA.CPARENTRADA(1).NIT = '800.091.594-4' AND*/ PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION = '14' THEN --(16/03/2021 JORDUZ) Se agrega validacion para el aporte patronal segun TAR 1000099384 
                                (PCK_NOMINA_COM5.CSOI(UN_I).FSPPENSIONCAQ)/2 ELSE (CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN ('4','7','11','14') THEN PCK_NOMINA_COM5.GL_FSPADICPLANILLA ELSE '00' END) END, CASE WHEN UN_TIPOESTRUCTURA = '2388' THEN 10 ELSE 7 END)
                            END
                /*CAMPO47*/ || CASE WHEN UN_CLASEREGISTRO = 'UPC' THEN
                                PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.GL_FSPADICPLANILLA, 7)
                               ELSE
                                PCK_SYSMAN_UTL.FC_STRZERO(MI_CN132, 7)
                               END
                --(EAMAYA:26/03/2021)Tar No 1000099832 Se cambia el CAMPO48 por el porcentaje sugerido en el tar, se deja en comentario el anterior valor de dicho campo
                /*CAMPO48*/ /*|| RPAD('0.' || CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).RIESGO_PENSION IS NULL THEN
                                                     CASE WHEN PCK_NOMINA.GL_CALCULARSALUDCONDECRETO1122 = 'SI' 
                                                          THEN TO_CHAR(PCK_NOMINA.GL_PORCSALUDCONDECRETO1122 * 10) 
                                                          ELSE TO_CHAR(PCK_PARENTR.PARAMETRO43) 
                                                     END
                                                 ELSE
                                                    CASE WHEN TO_NUMBER(PCK_NOMINA_COM5.CSOI(UN_I).RIESGO_PENSION) < 10 
                                                         THEN '0' || TO_CHAR(TO_NUMBER(PCK_NOMINA_COM5.CSOI(UN_I).RIESGO_PENSION))
                                                         ELSE TO_CHAR(TO_NUMBER(PCK_NOMINA_COM5.CSOI(UN_I).RIESGO_PENSION))
                                                    END                                           
                                                 END 
                                    ,7,'0')*/
                /*CAMPO48*/ || RPAD('0.' || LPAD(MI_PORCENTAJEMAX,2,'00')
                                    ,7,'0')
                /*CAMPO49*/ || PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).SALUD, 9)
                /*CAMPO50*/ || CASE WHEN UN_CLASEREGISTRO = 'UPC' THEN
                                PCK_SYSMAN_UTL.FC_STRZERO(0, 9)
                               ELSE
                                PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN  PCK_NOMINA_COM5.GL_UPCPATRONO ='SI' THEN PCK_NOMINA_COM5.CSOI(UN_I).UPCPATRONO ELSE PCK_NOMINA_COM5.CSOI(UN_I).UPC END, 9)
                               END
                               --(APINEDA:10/05/2019)-Se agrega validaciÂ¿ara tomar porcentaje cuando el aporte a CCF es diferente por pensionado TAR 1000091277                               
                /*CAMPO51*/ || RPAD(CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).CAJAS <> 0 THEN 
                                        TRIM(TO_CHAR(NVL(PCK_PARENTR.PARAMETRO47, 4) / 100, '0.00000'))
                                    ELSE 
                                        CASE WHEN GL_CN_APORTECCFPENSION <> 0 AND PCK_NOMINA_COM5.CSOI(UN_I).PORCENTAJE_APORTECAJA IS NOT NULL THEN
                                             TRIM(TO_CHAR(TO_NUMBER(PCK_NOMINA_COM5.CSOI(UN_I).PORCENTAJE_APORTECAJA) / 100, '0.00000'))
                                        ELSE
                                            '0.00000' 
                                        END                                    
                                    END,7,'0')
                               --(APINEDA:10/05/2019)-Se agrega validaciÂ¿ara tomar valor del aporte a CCF del pensionado TAR 1000091277                                    
                /*CAMPO52*/ || PCK_SYSMAN_UTL.FC_STRZERO(CASE WHEN GL_APORTECCFPENSION <> 0 THEN GL_APORTECCFPENSION ELSE PCK_NOMINA_COM5.CSOI(UN_I).CAJAS END, 9)
                /*CAMPO53 y CAMPO54*/ || RPAD(' ', 18, ' ') --TIPOESTRUCTURA = '3214' Or TIPOESTRUCTURA = '2388'
                /*CAMPO55, CAMPO56 y CAMPO57*/ || CASE WHEN UN_TIPOESTRUCTURA = '2388' THEN UN_CAMPOSNUEVOS2388 ELSE  RPAD(' ', 18, ' ') END
                /*CAMPO58*/ || CASE WHEN UN_TIPOESTRUCTURA = '2388' THEN PCK_SYSMAN_UTL.FC_STRZERO(UN_MESADA, 9) ELSE '' END
                /*CAMPO59, CAMPO60 y CAMPO61*/ || RPAD(' ', 30, ' ') 
                --(JORDUZ 22/03/2021)  Se aÃ±ade el valor de la mesada pensional Res 2421 TAR 10000106296
                /*CAMPO63*/ ||CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'GENERAR PLANO PENSIONADOS CAMPO 63',6,SYSDATE,-1),'NO') = 'SI' THEN
                                CASE WHEN PCK_NOMINA_COM5.GL_NING = 'X' OR PCK_NOMINA_COM5.GL_NRET = 'X' THEN
                                  ' ' || LPAD(UN_MESADA,9,'0')
                                ELSE
                                  ' ' || LPAD(MI_MESPEN2421,9,'0')
                                END                               
                               ELSE '' END;
                
  RETURN MI_PART1;

END FC_REGISTROPLANO_P;


FUNCTION FC_NOVEDADESPILA2388_TABLASEGS
    /*
    NAME              : FC_NOVEDADESPILA2388_TABLASEGS
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : MIGUEL ANGEL ZANGUÂ¿ HURTADO
    DATE MIGRATION    : 08/11/2018
    TIME              : 11:15 AM
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:
  */
    --UN_I es la posiciÂ¿el vector para consultar PCK_NOMINA_COM5.CSOI;
    --     que se debe enviar desde la funciÂ¿R_SISTEMAINTEGRADOELECTRONICO
    (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_I              IN NUMBER,
    UN_PROCESO        IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES            IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO        IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_TIPOESTRUCTURA IN VARCHAR2,
    UN_TIPOPLANILLA   IN VARCHAR2,
    UN_RETROACTIVO    IN PCK_SUBTIPOS.TI_LOGICO,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
    )
RETURN PCK_SUBTIPOS.TI_ENTERO
AS
    MI_RETORNO PCK_SUBTIPOS.TI_ENTERO;
    MI_FFINAL  DATE;
    MI_FINICIO DATE;
    MI_FFINALVAR  VARCHAR2(10);
    MI_FINICIOVAR VARCHAR2(10);
    MI_CANTENC          PCK_SUBTIPOS.TI_ENTERO;
    MI_PRIMERREG        PCK_SUBTIPOS.TI_ENTERO;
    MI_IBCMESANTERIOR   PCK_SUBTIPOS.TI_DOBLE;
    MI_TDIAS            PCK_SUBTIPOS.TI_ENTERO;
    MI_BASEPROPORCIONAL PCK_SUBTIPOS.TI_DOBLE;

    MI_NING               VARCHAR2(1);
    MI_NRET               VARCHAR2(1);
    MI_NTDA               VARCHAR2(1);
    MI_NTAA               VARCHAR2(1);
    MI_NTDAP              VARCHAR2(1);
    MI_NTAAP              VARCHAR2(1);
    MI_NVSP               VARCHAR2(1);
    MI_VTE                VARCHAR2(1);
    MI_NVST               VARCHAR2(1);
    MI_NSLN               VARCHAR2(1);
    MI_NIGE               VARCHAR2(1);
    MI_NLMA               VARCHAR2(1);
    MI_NVAC               VARCHAR2(1);
    MI_NAVP               VARCHAR2(1);
    MI_NVCT               VARCHAR2(1);
    MI_NIRP               VARCHAR2(2);

    MI_PORCSALUD          NUMBER;
    MI_PORCPENSION        NUMBER;
    MI_CAMPOSNUEVOS2388N  CLOB;
    MI_CAMPOSNUEVOS2388   CLOB;
    MI_TOTALBASEPARAFISCALN NUMBER;
    MI_TOTALBASEPENSIONN    NUMBER;
    MI_TOTALBASESALUDN      NUMBER;
    MI_SUMAAPORTESPENSIONN  NUMBER;
    MI_SUMAAPORTESSALUDN    NUMBER;
    MI_SUMAAPORTESRIESGOSN  NUMBER;
    MI_SUMAAPORTESFSPN      NUMBER;
    MI_APORTEPARA           NUMBER;
    MI_TOTDIASNOV           NUMBER;
    MI_DIASTRABAJADOS       NUMBER;
    MI_AC                   NUMBER:=0;
    MI_TIPONOVEDAD          VARCHAR2(100 CHAR);
    MI_SALARIO              NUMBER:=0;
    MI_BASEPENSION          NUMBER:=0;
    MI_VALOREXO             NUMBER := 0;
    MI_VALOREXOT            VARCHAR2(100) :='';

    MI_MSGERROR PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_PARAMETRO1990APLICA          PARAMETRO.VALOR%TYPE;
BEGIN
    MI_RETORNO :=0;
    MI_CANTENC :=0;
    --se guardan los datos en variables de tal forma que cuando salga se pueda reasignen
    MI_NING := GL_NING   ;
    MI_NRET := GL_NRET   ;
    MI_NTDA := GL_NTDA   ;
    MI_NTAA := GL_NTAA   ;
    MI_NTDAP := GL_NTDAP  ;
    MI_NTAAP := GL_NTAAP  ;
    MI_NVSP := GL_NVSP   ;
    MI_VTE := GL_VTE    ;
    MI_NVST := GL_NVST   ;
    MI_NSLN := GL_NSLN   ;
    MI_NIGE := GL_NIGE   ;
    MI_NLMA := GL_NLMA   ;
    MI_NVAC := GL_NVAC   ;
    MI_NAVP := GL_NAVP   ;
    MI_NVCT := GL_NVCT   ;
    MI_NIRP := GL_NIRP   ;
    MI_TOTALBASEPARAFISCALN := 0;
    MI_TOTALBASEPENSIONN := 0;
    MI_TOTALBASESALUDN := 0;
    MI_SUMAAPORTESPENSIONN := 0;
    MI_SUMAAPORTESSALUDN := 0;
    MI_SUMAAPORTESRIESGOSN := 0;
    MI_SUMAAPORTESFSPN := 0;
    GL_BASECAJASP := 0;
    MI_TOTDIASNOV := 0;
    MI_PRIMERREG := 0;
    MI_IBCMESANTERIOR := 0;
    MI_PARAMETRO1990APLICA := UPPER(PCK_PARST.FC_PAR('APLICAR NUEVO REDONDEO DECRETO 1990 FC_ROUND_100', 'NO')) ;

    MI_DIASTRABAJADOS := (PCK_NOMINA.FC_CNA(9) +PCK_NOMINA.FC_CNA(11));
    --INI JM CC 1404 (10/06/2025)
    MI_DIASTRABAJADOS := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PAGAR MESES DE 31 EN NOMINA QUINCENAL',6,SYSDATE),'NO') = 'SI' AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'NOMINA MENSUAL',6,SYSDATE),'SI') = 'NO' AND MI_DIASTRABAJADOS > 30 THEN 30 ELSE MI_DIASTRABAJADOS END;
    PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_DIAS := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PAGAR MESES DE 31 EN NOMINA QUINCENAL',6,SYSDATE),'NO') = 'SI' AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'NOMINA MENSUAL',6,SYSDATE),'SI') = 'NO' AND PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_DIAS > 30 THEN 30 ELSE PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_DIAS END;
    --FIN JM CC 1404 (10/06/2025)
    --INI JM CC 2001 (01/09/2025)
    GL_ID_ANTERIOR :=  CASE WHEN UN_I = 1 THEN 0 ELSE GL_ID_ACTUAL END;
    GL_ID_ACTUAL := PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO;
    --FIN JM CC 2001 (01/09/2025)
    --INI JM CC 2001 (10/07/2025) MOD (01/09/2025)
     GL_INCAPACIDAD30 := CASE WHEN (PCK_NOMINA.FC_CNA(160) > 0 OR PCK_NOMINA.FC_CNA(150) > 0)  AND PCK_NOMINA.FC_CNA(9) = 0 AND GL_ID_ACTUAL <> GL_ID_ANTERIOR THEN TRUE ELSE FALSE END;
    --FIN JM CC 2001 (10/07/2025)
    --10/04/2018 Para inicializar
    GL_SUELDOP :=0;
    MI_AC:=PCK_NOMINA.FC_ACUM1(UN_COMPANIA  => UN_COMPANIA,
                              UN_ANO1      => UN_ANIO,
                              UN_MES1      => UN_MES,
                              UN_PERIODO1  => UN_PERIODO,
                              UN_IDEMPLEADO=> PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO);
        --JM INI CC 2410
    GL_EXO_OTR_PAR := TRUE;
    GL_EXO_OTR_PAR := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'APLICA EXONERACION EN BASE TOTAL INGRESOS 097',6,SYSDATE),'NO') = 'SI' AND  PCK_NOMINA.FC_CNA(97) >= (4 * PCK_PARENTR.PARAMETRO20) AND PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD NOT IN (1,2,3) THEN FALSE ELSE TRUE END;
    --JM FIN CC 2410
    <<NOVEDADES>>
    --(MZANGUNA:24/11/2018)-Se agrega el periodo.    
    --(APINEDA:11/07/2019)-Se elimina consulta de NOVEDADES sobre la tabla base novedades, debido a que esta informaciÃ³n se integrÃ³ en la vista V_SOI, se cambia por la siguiente condiciÃ³n.
    IF PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_DIAS NOT IN (0) AND PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD NOT IN (4) THEN
        GL_COMISION30 := FALSE; --(MZANGUNA:02/05/2019)
        MI_CANTENC := MI_CANTENC + 1; ----(MZANGUNA:14/03/2019),Se sube variable a principio de Loop.
        /*MROSERO CC1028 30/04/2025 Se agrega el estado 3 y se modifica la condicion para el concepto 339*/
        IF PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD = 3 AND PCK_NOMINA_COM5.CSOI(UN_I).ESTADO_ACTUAL IN(3, 6) AND PCK_NOMINA.FC_CNA(339) >= 1 THEN --(MZANGUNA:14/03/2019),Se agrega campos para comisiones de 30 dÂ¿.
            --(MZANGUNA:14/03/2019)-----------------------
            GL_COMISION30 := TRUE;  --(MZANGUNA:02/05/2019)-Global de ComisiÂ¿e 30 dÂ¿.
            GL_BASESALUD := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_BASEPROPORCIONAL;
            GL_NUMDIAS   := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_DIAS, 2);
            GL_NUMDIASS := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_DIAS, 2);

            GL_NSLN :='C';
            GL_DIASPA := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_DIAS, 2) ;
            GL_FSPADICPLANILLA := 0;
            IF TO_NUMBER(GL_NUMDIASS) > 30 THEN
                GL_NUMDIASS := '30';
            END IF;
            GL_CTAR :=0;
            GL_FSPPLANILLA := 0;

            IF PCK_NOMINA.GL_OPERADOR IN('SOI.COM.CO') THEN
                GL_NUMDIASS := '30';
                GL_DIASPA   := '30';
        	       GL_NUMDIASS := '30';
            END IF;

            MI_CAMPOSNUEVOS2388:='';
            IF UN_TIPOESTRUCTURA= '2388' THEN
                GL_CAMPO83 := TO_CHAR(TO_DATE('01/' || UN_MES || '/' || UN_ANIO, 'DD/MM/YYYY'), 'YYYY-MM-DD');
                GL_CAMPO84 := TO_CHAR(LAST_DAY(TO_DATE('01/' || UN_MES || '/' || UN_ANIO, 'DD/MM/YYYY')), 'YYYY-MM-DD');
                IF PCK_NOMINA.GL_OPERADOR IN('SOI.COM.CO') THEN
                    GL_CAMPO95 := GL_BASESALUD;
                END IF;
                MI_CAMPOSNUEVOS2388 := PCK_NOMINA_COM5.FC_CAMPOSNUEVOS2388INGRET(UN_ANNO     => UN_ANIO
                                                                 ,UN_MMES     => UN_MES
                                                                 ,UN_PNING    => GL_NING
                                                                 ,UN_PNRET    => GL_NRET
                                                                 ,UN_PNVST    => GL_NVST
                                                                 ,UN_DIASTRABAJADOS => PCK_NOMINA.FC_CNA(9) +PCK_NOMINA.FC_CNA(11)
                                                                 ,UN_I              => UN_I
                                                                 ,UN_RBASE1990      => PCK_NOMINA.GL_RBASE1990
                                                                 ,UN_HORASMENSUALES => PCK_NOMINA.GL_HORASMENSUALES
                                                                 ,UN_DIASSEMANALES  => PCK_NOMINA.GL_DIASSEMANALES
                                                                 );
            END IF;
            PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                  FC_REGISTROPLANO (UN_COMPANIA         => UN_COMPANIA,
                                                   UN_I                => UN_I,
                                                   UN_TIPOESTRUCTURA   => UN_TIPOESTRUCTURA,
                                                   UN_CLASEREGISTRO    => 'COM',
                                                   UN_TIPOPLANILLA    => UN_TIPOPLANILLA,
                                                   UN_CAMPOSNUEVOS2388 => MI_CAMPOSNUEVOS2388);
            --(MZANGUNA:14/03/2019)-----------------------            -----------------------
        ELSE
            MI_IBCMESANTERIOR := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_BASE;
            MI_TDIAS := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_DIAS;
            MI_BASEPROPORCIONAL := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_BASEPROPORCIONAL;
            MI_BASEPENSION := MI_BASEPENSION + MI_BASEPROPORCIONAL;
            MI_FFINAL := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_FECHAFINAL;
            MI_FINICIO := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_FECHAINICIAL;

            MI_TOTDIASNOV := MI_TOTDIASNOV + MI_TDIAS;
            PR_LIMPIARCAMPSO80A97();
            GL_SNING := ' ';
            GL_SNRET := ' ';
            GL_SNTDA := ' ';
            GL_SNTAA := ' ';
            GL_SNTDAP := ' ';
            GL_SNTAAP := ' ';
            GL_SNVSP := ' ';
            --(APINEDA:27/06/2019)-Se agrega valor al campo 22 de Registro tipo 2 para el caso de Retroactivo con las novedades. 
            IF UN_RETROACTIVO <> 0 THEN
                GL_SVTE := CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_PROCESO = 10 THEN 'C' ELSE 'A' END;
            ELSE
                GL_SVTE   := ' ';
            END IF;             
            GL_SNVST := ' ';
            GL_SNSLN := ' ';
            GL_SNIGE := ' ';
            GL_SNLMA := ' ';
            GL_SNVAC := ' ';
            GL_SNAVP := ' ';
            GL_SNVCT := ' ';
            GL_SNIRP := '00';

            GL_DIASP := PCK_SYSMAN_UTL.FC_STRZERO(MI_TDIAS, 2);
            GL_DIASR := PCK_SYSMAN_UTL.FC_STRZERO(MI_TDIAS, 2);
            GL_DIASC := PCK_SYSMAN_UTL.FC_STRZERO(MI_TDIAS, 2);
            --(APINEDA:28/11/2019)-NÃºmero de dÃ­as cotizados a salud TAR 1000095980
            GL_DIASS := PCK_SYSMAN_UTL.FC_STRZERO(MI_TDIAS, 2);


            GL_BASECAJASP := MI_BASEPROPORCIONAL;
            GL_CAMPO95 := PCK_SYSMAN_UTL.FC_STRZERO(GL_BASECAJASP,9);
            MI_FFINALVAR := TO_CHAR(MI_FFINAL, 'YYYY-MM-DD');
            MI_FINICIOVAR := TO_CHAR(MI_FINICIO, 'YYYY-MM-DD');

            IF PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD = 1 THEN  --Vacaciones.
                MI_TIPONOVEDAD := 'NVAC';
            ELSIF PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD = 2 THEN  --Incapacidades.
                MI_TIPONOVEDAD := SUBSTR(PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_LLAVENOVEDAD, INSTR(PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_LLAVENOVEDAD,'--',1,3) + 2, INSTR(PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_LLAVENOVEDAD,'--',1,4) - 2 - INSTR(PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_LLAVENOVEDAD,'--',1,3) );
                MI_TIPONOVEDAD := CASE WHEN MI_TIPONOVEDAD ='04'
                                  THEN 'NLMA'
                                  ELSE CASE WHEN MI_TIPONOVEDAD IN('05','09')
                                       THEN 'NIRP'
                                       ELSE 'NIGE'
                                       END
                                  END;
            ELSIF PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD = 3 THEN   --Licencias
                MI_TIPONOVEDAD := SUBSTR(PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_LLAVENOVEDAD, INSTR(PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_LLAVENOVEDAD,'--',1,4) + 2, LENGTH(PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_LLAVENOVEDAD));
                MI_TIPONOVEDAD := CASE WHEN MI_TIPONOVEDAD IN('01','02','03')
                                  THEN 'NSLN'
                                  ELSE CASE WHEN MI_TIPONOVEDAD IN('04','05','10','12')
                                       THEN 'LREM'
                                       ELSE 'COMIS'
                                       END
                                  END;
            ELSE
                MI_TIPONOVEDAD := ' ';
            END IF;

            IF MI_TIPONOVEDAD ='NSLN' THEN
                GL_SNSLN := 'X';
                GL_CAMPO83 := MI_FINICIOVAR;
                GL_CAMPO84 := MI_FFINALVAR;
            ELSIF MI_TIPONOVEDAD = 'NIGE' THEN
                GL_SNIGE := 'X';
                GL_CAMPO85 := MI_FINICIOVAR;
                GL_CAMPO86 := MI_FFINALVAR;
            ELSIF MI_TIPONOVEDAD = 'NLMA' THEN
                GL_SNLMA := 'X';
                GL_CAMPO87 := MI_FINICIOVAR;
                GL_CAMPO88 := MI_FFINALVAR;
            ELSIF MI_TIPONOVEDAD = 'LREM' THEN
                GL_SNVAC := 'L';
                GL_CAMPO89 := MI_FINICIOVAR;
                GL_CAMPO90 := MI_FFINALVAR;
            ELSIF MI_TIPONOVEDAD = 'NIRP' THEN
                GL_SNIRP := PCK_SYSMAN_UTL.FC_STRZERO(MI_TDIAS, 2);
                GL_CAMPO93 := MI_FINICIOVAR;
                GL_CAMPO94 := MI_FFINALVAR;
            ELSIF MI_TIPONOVEDAD = 'NVAC' THEN
                GL_SNVAC := 'X';
                GL_CAMPO89 := MI_FINICIOVAR;
                GL_CAMPO90 := MI_FFINALVAR;
            ELSIF MI_TIPONOVEDAD = 'COMIS' AND PCK_NOMINA.FC_CNA(339) > 0 THEN  --(MZANGUNA:25/04/2019)-Se agrega campo para comisiones CAMPO24.
                GL_SNSLN := 'C';
                -- TICKET 7740192 EFCM: SE ADICIONAN FECHAS EN EL ARCHIVO PLANO PARA COMISIONES
                GL_CAMPO83 := MI_FINICIOVAR;
                GL_CAMPO84 := MI_FFINALVAR;
                -- TICKET 7740192 FIN --
            END IF;

            IF (PCK_NOMINA.FC_CNA(347) + PCK_NOMINA.FC_CNA(355)) >= 30 AND PCK_NOMINA.FC_CNA(170) > 0 THEN
                GL_SNVSt := 'X';
            ELSIF MI_TIPONOVEDAD = 'NVAC' AND (GL_GENERAPARAFISCALESENVACA = 'SI' OR GL_GENERAOTPARAFISCALESENVACA='SI') THEN
                GL_BASECAJASP := MI_BASEPROPORCIONAL;
                GL_CAMPO95 := PCK_SYSMAN_UTL.FC_STRZERO(GL_BASECAJASP,9);
            ELSE --MOD JM CC 2410
                GL_BASECAJASP := CASE WHEN GL_INCAPACIDAD30 THEN PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL ELSE 0 END; --JM 31/07/2024 7741502 MOD JM CC2001 10/07/2025 01/09/2025
                GL_CAMPO95 := PCK_SYSMAN_UTL.FC_STRZERO(GL_BASECAJASP,9);
            END IF;

            GL_SUELDOP := PCK_SYSMAN_UTL.FC_STRZERO(MI_IBCMESANTERIOR, 9);
            GL_BASEPENSIONP := MI_BASEPROPORCIONAL;
            GL_BASESALUDP := MI_BASEPROPORCIONAL;
            GL_BASERIESGOSP := MI_BASEPROPORCIONAL;

            IF GL_EXONERACIONSALUD = 'SI' THEN
                MI_PORCSALUD := TO_NUMBER(PCK_PARENTR.PARAMETRO41);
                --PCK_NOMINA.GL_PORCSALUDCONDECRETO1122 := MI_PORCSALUD;
            ELSE
                MI_PORCSALUD := CASE WHEN PCK_NOMINA.GL_CALCULARSALUDCONDECRETO1122 = 'SI'
                               THEN PCK_NOMINA.GL_PORCSALUDCONDECRETO1122
                               ELSE PCK_PARENTR.PARAMETRO43 END;
            END IF;
            MI_PORCPENSION := TO_NUMBER(NVL(PCK_PARENTR.PARAMETRO39, 0));
            /*GL_PENSIONAP := PCK_NOMINA_SEGSOCI.FC_APORTE(UN_IBC        => GL_BASEPENSIONP,
                                                         UN_DIAS      => MI_TDIAS,
                                                         UN_DECIMALES => PCK_NOMINA.GL_RAPORTES1990,
                                                         UN_PORCENTAJE => MI_PORCPENSION);*/
            --(MZANGUNA:06/12/2018)-Se deja el campo 47 del plano con el valor calculado en la tabla.
            GL_PENSIONAP := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_APORTEPATRONALPENSION + PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_APORTEEMPLEADOPENSION;
            GL_SALUDAP := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_APORTEPATRONALSALUD + PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_APORTEEMPLEADOSALUD;

            IF PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12', '19') THEN
                MI_PORCSALUD := PCK_PARENTR.PARAMETRO43;
                MI_PORCPENSION := 0;
                GL_PENSIONAP := 0;
                PCK_NOMINA.GL_CALCULARSALUDCONDECRETO1122 := PCK_PARENTR.PARAMETRO43;
                GL_EXONERACIONSALUD := 'NO';
                --(APINEDA:28/11/2019)-Se agrega condiciÃ³n para respetar nÃºmero de dÃ­as cotizados a salud cuando el funcionario es SENA ETAPA PRODUCTIVA 
                GL_DIASS := CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION = 19 THEN GL_DIASS ELSE PCK_SYSMAN_UTL.FC_STRZERO(0, 2) END;
                GL_DIASP := PCK_SYSMAN_UTL.FC_STRZERO(0, 2);
                MI_TDIAS := 0;
            END IF;




            GL_CAMPO96 := 0;
            MI_TOTALBASEPARAFISCALN := MI_TOTALBASEPARAFISCALN + GL_BASECAJASP;
            MI_TOTALBASEPENSIONN := MI_TOTALBASEPENSIONN    + GL_BASEPENSIONP;
            MI_TOTALBASESALUDN := MI_TOTALBASESALUDN      + GL_BASESALUDP;

            MI_SUMAAPORTESPENSIONN := MI_SUMAAPORTESPENSIONN + GL_PENSIONAP;
            MI_SUMAAPORTESSALUDN := MI_SUMAAPORTESSALUDN   + GL_SALUDAP;
            MI_SUMAAPORTESRIESGOSN := MI_SUMAAPORTESRIESGOSN + 0;
            GL_FSPPLANILLA := 0;
            GL_FSPADICPLANILLA := 0;

            --(MZANGUNA:01/03/2019)- Se ajusta la columna 51 y 52
            GL_FSPPLANILLA := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_FSP;
            GL_FSPADICPLANILLA := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_FSPADICIONAL + PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_FSPSUBSISTENCIA;

            MI_SUMAAPORTESFSPN := MI_SUMAAPORTESFSPN + GL_FSPPLANILLA + GL_FSPADICPLANILLA;
            GL_RIESGOSAP := 0;
            GL_CTARP := 0 ;

            GL_CN101AP := 0 ;
            GL_CN102AP := 0 ;
            GL_CN103AP := 0 ;
            GL_CN104AP := 0 ;
            GL_CN105AP := 0 ;
            IF GL_EXONERACIONSALUD <> 'SI' THEN
                GL_CN101AP := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA.GL_SUMARSS1990
                                                   ,UN_PRECISION => PCK_NOMINA.GL_RAPORTES1990) ;
                GL_CN102AP := GL_CN101AP ;
                GL_CN103AP := GL_CN101AP ;
                GL_CN104AP := GL_CN101AP ;
                GL_CN105AP := GL_CN101AP;
            END IF;
            --JM INI CC 2001 10/07/2025
            IF GL_INCAPACIDAD30 THEN 
                GL_CN101AP := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASECAJASP * PCK_PARENTR.PARAMETRO47 /100
                                                                     , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                                     , PCK_NOMINA.GL_RAPORTES1990);
                GL_CN102AP := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASECAJASP * PCK_PARENTR.PARAMETRO45 /100
                                                                     , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                                     , PCK_NOMINA.GL_RAPORTES1990);
                GL_CN103AP := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASECAJASP * PCK_PARENTR.PARAMETRO46 /100
                                                                     , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                                     , PCK_NOMINA.GL_RAPORTES1990);
                GL_CN104AP := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASECAJASP * PCK_PARENTR.PARAMETRO48 /100
                                                                     , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                                     ,PCK_NOMINA.GL_RAPORTES1990);
                GL_CN105AP := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASECAJASP * PCK_PARENTR.PARAMETRO49 /100
                                                                     , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                                     , PCK_NOMINA.GL_RAPORTES1990);
            END IF;
            --JM FIN CC 2001 10/07/2025
            IF (GL_CN102AP + GL_CN103AP + GL_CN104AP + GL_CN105AP) = 0 AND ((PCK_NOMINA.FC_CNA(347) + PCK_NOMINA.FC_CNA(355)) < 30 AND PCK_NOMINA.FC_CNA(170) <> 0) THEN
                GL_CAMPO95 := '0';
            END IF;
            ---<TAR:7702448 FECHA:07/02/2022 AUTOR:CP>
            IF GL_EXONERADO AND PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION NOT IN('12', '19', '23', '51') AND GL_EXO_OTR_PAR THEN  --JM CC 2410
                 GL_CAMPO95 := 0;
            END IF;
            ---</TAR>
            MI_CAMPOSNUEVOS2388N := FC_CONCATENARCAMPSO80A97;

            --(MZANGUNA:10/12/2018)-Envio de parÂ¿tro UN_NUEVATABLA
            ---<TAR:7702452 FECHA:24/01/2022 AUTOR:CP>
            IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'APLICA EXONERACION EN BASE TOTAL INGRESOS 097',6,SYSDATE,-1),'NO') = 'SI' THEN --18122018
                PCK_NOMINA.GL_IBLREXONERACION :=  PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_APORTEPATRONALSALUD; --CONCEPTO 116
                MI_VALOREXO := PCK_NOMINA.GL_IBLREXONERACION;
                IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'APLICAR EXONERACION APORTES SALUD PATRONO 8.5%',6,SYSDATE,-1),'NO') = 'SI'  AND PCK_NOMINA.GL_IBLREXONERACION > 0 THEN
                     GL_PORCSALUD := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_PORC_SALUDTOTAL;  
                ELSE 
                    GL_PORCSALUD := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_PORC_SALUDE;   
                END IF;
            ELSE
                 --(MZANGUNA:10/12/2018)-Envio de parÃƒâ€šÃ‚Â¿tro UN_NUEVATABLA
                GL_PORCSALUD := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_PORC_SALUDE + PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_PORC_SALUDP;  
            END IF;
            --</TAR>
            GL_PORCPENSION := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_PORC_PENSIONE + PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_PORC_PENSIONP;
            PCK_NOMINA_COM5.GL_DISCOLIQUIDACION := PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                  FC_REGISTROPLANO (UN_COMPANIA          => UN_COMPANIA,
                                                     UN_I                => UN_I,
                                                     UN_TIPOESTRUCTURA   => UN_TIPOESTRUCTURA,
                                                     UN_CLASEREGISTRO    => 'NOV',
                                                     UN_TIPOPLANILLA     => UN_TIPOPLANILLA,
                                                     UN_CAMPOSNUEVOS2388 => MI_CAMPOSNUEVOS2388N,
                                                     UN_NUEVATABLA       => -1);
        END IF;
        MI_PRIMERREG := 1;
    END IF; -- CIERRE IF NOVEDADES

        --(APINEDA:11/07/2019)-Se elimina consulta de SUELDO sobre la tabla base novedades, debido a que esta informaciÃ³n se integrÃ³ en la vista V_SOI, se cambia por la siguiente condiciÃ³n.
        IF PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_DIAS NOT IN (0) AND PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_TIPONOVEDAD IN (4) AND PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_DIAS > 0 THEN              
            --(APINEDA:27/06/2019)-Se agrega valor al campo 22 de Registro tipo 2 para el caso de Retroactivo. 
            IF UN_RETROACTIVO <> 0 THEN
                GL_VTE := CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_PROCESO = 10 THEN 'C' ELSE 'A' END;
            ELSE
                GL_VTE := ' ';
            END IF;         
            --(MZANGUNA:06/12/2018)-Se quita campo 24 para la novedad de dÂ¿ laborados.
            MI_CANTENC := MI_CANTENC + 1;
            GL_NSLN := ' ';
            --(MZANGUNA:15/03/2019)-Se quita campo 30 para la novedad de dÂ¿ laborados.
            GL_NIRP := '00';

            PR_LIMPIARCAMPSO80A97();
            --REVISARSUELDO SECOMENTA PUES HACE OPERACIONES QUE NO APORTAN AL PROCESO
            GL_SUELDO := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_BASE;
            IF PCK_NOMINA.GL_TIPONOMINA IN('ACTIVOS','') AND GL_SUELDO = 0 AND PCK_NOMINA_COM5.CSOI(UN_I).ESTADO_ACTUAL = 2 THEN
                GL_SUELDO := PCK_NOMINA_COM5.CSOI(UN_I).MESADA_PENSIONAL;
            END IF;

            GL_SUELDOP := PCK_SYSMAN_UTL.FC_STRZERO(GL_SUELDO, 9);
            GL_NUMDIASP := 0;
            GL_NUMDIAS  := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_DIAS, 2);

            GL_NUMDIAS  := CASE WHEN TO_NUMBER(GL_NUMDIAS) < 0
                           THEN '0'
                           ELSE GL_NUMDIAS
                           END;
            GL_NUMDIAS  := CASE WHEN TO_NUMBER(GL_NUMDIAS) > 30
                           THEN '30'
                           ELSE GL_NUMDIAS
                           END;

            GL_NUMDIASS := GL_NUMDIAS;
            /*IF  TO_NUMBER(GL_NUMDIAS) > 0 AND PCK_NOMINA_COM5.CSOI(UN_I).DIASRIESGOS > TO_NUMBER(GL_NUMDIAS) Then
                GL_NUMDIAS := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).DIASRIESGOS, 2);
                MI_MSGERROR(1).CLAVE := 'EMPLEADO';
                MI_MSGERROR(1).VALOR := PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO || ' ' || PCK_NOMINA_COM5.CSOI(UN_I).APELLIDO1 || ' ' || PCK_NOMINA_COM5.CSOI(UN_I).APELLIDO2 || ' ' || PCK_NOMINA_COM5.CSOI(UN_I).NOMBRES;
                PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => UN_COMPANIA,
                                           UN_MENSAJE_COD => PCK_ERRORES.ALER_MENOR30_SIN_NOVEDAD,
                                           UN_REEMPLAZOS  => MI_MSGERROR);
            END IF;*/

            GL_NUMDIAS := CASE WHEN TO_NUMBER(GL_NUMDIAS) = 0
                            AND (UN_RETROACTIVO= -1 OR PCK_NOMINA.FC_CNA(306) <> 0)
                          THEN 30
                          ELSE GL_NUMDIAS END;
            GL_NUMDIAS  := CASE WHEN TO_NUMBER(GL_NUMDIAS) > 30
                           THEN '30'
                           ELSE GL_NUMDIAS
                           END;
            GL_NUMDIASP := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_DIAS, 2);
            --(MZANGUNA:07/12/2018)-Se colocan los dias de sueldos calculados en la tabla.
            PCK_NOMINA_COM5.CSOI(UN_I).DIASRIESGOS := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_DIAS;
            GL_DIASPARAFISCALES := GL_NUMDIAS;
            --REVISARSUELDO

            IF PCK_NOMINA.FC_CNA(10) > 0 AND PCK_NOMINA.GL_NOMINAMENSUAL='SI' THEN
                GL_SUELDOP := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA.FC_CNA(10), 9);
            END IF;
            IF PCK_NOMINA.FC_CNA(10) > 0  THEN
                IF PCK_NOMINA.FC_CNA(10) = MI_IBCMESANTERIOR OR PCK_NOMINA.FC_CNA(10) = MI_IBCMESANTERIOR +1 THEN
                    GL_SUELDOP := PCK_SYSMAN_UTL.FC_STRZERO(MI_IBCMESANTERIOR, 9);
                ELSE
            		GL_SUELDO := PCK_SYSMAN_UTL.FC_STRZERO(MI_IBCMESANTERIOR, 9);
                END IF;
            ELSE
                IF GL_SNVAC ='X' OR MI_IBCMESANTERIOR> TO_NUMBER(GL_SUELDO) THEN
                    GL_SUELDOP :=PCK_SYSMAN_UTL.FC_STRZERO(GL_SUELDO, 9);
                END IF;
            END IF;

            GL_BASEPENSIONP := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_BASEPROPORCIONAL; --(MZANGUNA:25/04/2019)-Se deja al cÂ¿ulado en la tabla.
            GL_BASESALUDP := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_BASEPROPORCIONAL; --(MZANGUNA:25/04/2019)-Se deja al cÂ¿ulado en la tabla.
            GL_BASERIESGOSP := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_BASEPROPORCIONAL; --(MZANGUNA:25/04/2019)-Se deja al cÂ¿ulado en la tabla.
            GL_BASECAJASP := PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL;
            --REVISAR ESTO PUES ES EL DEL REGISTRO PRINCIPAL
            GL_NIGE := ' ';
            GL_NLMA := ' ';
            GL_NVAC := ' ';
            /*IF (PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS - GL_BASESALUDP) > -600 AND (PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS - GL_BASESALUDP) < 600 THEN
                GL_BASEPENSIONP := RS.BASEPROPORCIONAL; --(MZANGUNA:25/04/2019)-Se deja al cÂ¿ulado en la tabla.
                GL_BASESALUDP := GL_BASESALUDP := RS.BASEPROPORCIONAL; --(MZANGUNA:25/04/2019)-Se deja al cÂ¿ulado en la tabla.
            END IF;*/ --(MZANGUNA:25/04/2019)-Se quita esta parte dado que los redondeos se controlan desde la tabla.
            --(MZANGUNA:14/11/2018)-Se dejan los dias de sueldo calcualdos en la tabla
            GL_NUMDIAS := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_DIAS, 2);
            GL_NUMDIASS := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_DIAS, 2);
            GL_DIASPA := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_DIAS, 2);

            MI_PORCSALUD := CASE WHEN PCK_NOMINA.GL_CALCULARSALUDCONDECRETO1122 = 'SI'
                                 THEN PCK_NOMINA.GL_PORCSALUDCONDECRETO1122
                                 ELSE PCK_PARENTR.PARAMETRO43 END;
            MI_PORCPENSION := TO_NUMBER(NVL(PCK_PARENTR.PARAMETRO39, 0));

            IF PCK_NOMINA_COM5.CSOI(UN_I).SUBTIPOCOTIZANTE IS NOT NULL THEN
                IF PCK_NOMINA_COM5.CSOI(UN_I).SUBTIPOCOTIZANTE>=1 AND PCK_NOMINA_COM5.CSOI(UN_I).PENSION =0 THEN
                    MI_PORCSALUD :=0;
                    GL_BASEPENSIONP :=0;
                END IF;
            END IF;

            /*IF PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS - GL_BASEPENSIONP > 0 AND PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS - GL_BASEPENSIONP < 10 AND PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS - GL_BASEPENSIONP <> 0 THEN
                GL_BASEPENSIONP := RS.BASEPROPORCIONAL; --(MZANGUNA:25/04/2019)-Se deja al cÂ¿ulado en la tabla.
                GL_BASESALUDP := PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS;
            END IF; */  --(MZANGUNA:25/04/2019)-Se quita esta parte dado que los redondeos se controlan desde la tabla.

            /*GL_PENSIONAP := PCK_NOMINA_SEGSOCI.FC_APORTE(UN_IBC        => GL_BASEPENSIONP,
                                                           UN_DIAS       => GL_NUMDIASS,
                                                           UN_DECIMALES  => PCK_NOMINA.GL_RAPORTES1990,
                                                           UN_PORCENTAJE => MI_PORCPENSION);*/

            /*GL_SALUDAP := PCK_NOMINA_SEGSOCI.FC_APORTE(UN_IBC        => GL_BASESALUDP,
                                                        UN_DIAS       => GL_NUMDIASS,
                                                        UN_DECIMALES  => PCK_NOMINA.GL_RAPORTES1990,
                                                        UN_PORCENTAJE => MI_PORCSALUD);*/

            --(MZANGUNA:06/12/2018)-Se deja el campo 47 del plano con el valor calculado en la tabla.
            GL_PENSIONAP := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_APORTEPATRONALPENSION + PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_APORTEEMPLEADOPENSION;
            GL_SALUDAP := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_APORTEPATRONALSALUD + PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_APORTEEMPLEADOSALUD;

            --GL_BASECAJASP := PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL;
            GL_BASECAJASP := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL
                                                    ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);

            IF PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS - GL_BASECAJASP > -100 AND PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS - GL_BASECAJASP <  100 THEN
                --GL_BASECAJASP := PCK_NOMINA_COM5.CSOI(UN_I).BASERIESGOS;
                --(EAMAYA 20/05/2019) Se cambia BASERIESGOS por BASECAJAS (concepto 220)
                GL_BASECAJASP := PCK_NOMINA_COM5.CSOI(UN_I).BASECAJAS; 
            END IF;
            GL_CAMPO95 := PCK_SYSMAN_UTL.FC_STRZERO(GL_BASECAJASP,9);

            IF NVL(PCK_PARENTR.PARAMETRO70, 'N') = 'S' AND NOT(GL_EXO_OTR_PAR) THEN --JM CC 2410
                GL_CAMPO95 := PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA_COM5.CSOI(UN_I).BASEPARAFISCAL,9);
            END IF;

            IF PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION IN('12', '19') THEN
                GL_PENSIONAP := 0;
                GL_DIASPA := '00';
            END IF;
            IF GL_PENSIONAP > 0 AND GL_NUMDIASS='00' AND GL_NUMDIAS='00' THEN
                GL_NUMDIAS := PCK_SYSMAN_UTL.FC_STRZERO(MI_DIASTRABAJADOS,2);
                GL_NUMDIASS := PCK_SYSMAN_UTL.FC_STRZERO(MI_DIASTRABAJADOS,2);
                GL_DIASPA := PCK_SYSMAN_UTL.FC_STRZERO(MI_DIASTRABAJADOS,2);
            END IF;

            IF PCK_NOMINA.GL_DIASSEMANALES * (PCK_NOMINA.FC_CNA(9) +PCK_NOMINA.FC_CNA(11)) <> 0 THEN
                GL_CAMPO96 :=PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => (PCK_NOMINA.GL_HORASMENSUALES/5) / PCK_NOMINA.GL_DIASSEMANALES * MI_DIASTRABAJADOS
                                                    ,UN_PRECISION => 0);
            END IF;
            IF GL_NING <> ' ' THEN
                GL_CAMPO80 := TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_DE_INGRESO, 'YYYY-MM-DD');
            END IF;
            IF GL_NRET <> ' ' THEN
                GL_CAMPO81 := TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHATERCONTRATO, 'YYYY-MM-DD');
            END IF;
            IF GL_NVST <> ' ' AND PCK_NOMINA_COM5.CSOI(UN_I).FECHA_ULTSUELDO IS NOT NULL THEN
                IF TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_ULTSUELDO,'MM'))   = UN_MES
                AND TO_NUMBER(TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_ULTSUELDO,'YYYY')) = UN_ANIO  THEN
                    GL_CAMPO82 :=  TO_CHAR(PCK_NOMINA_COM5.CSOI(UN_I).FECHA_ULTSUELDO, 'YYYY-MM-DD');
                END IF;
            END IF;

            /*GL_CTAR := PCK_NOMINA_SEGSOCI.FC_PORCENRIESGO(UN_COMPANIA => UN_COMPANIA
                                                         ,UN_IDEMPLEADO => PCK_NOMINA_COM5.CSOI(UN_I).ID_DE_EMPLEADO) / 100;*/
            --(APINEDA:17/07/2019)-Se utiliza campo FACTOR_RIESGO de la vista V_SOI en vez de la funciÃ³n FC_PORCENRIESGO, porque para el cÃ¡lculo de Retroactivo el dato debe proceder de PERSONAL_HISTORICO y no de PERSONAL ya que pudo haber cambiado entre un mes y otro.                                                          
            GL_CTAR := PCK_NOMINA_COM5.CSOI(UN_I).FACTOR_RIESGO / 100;                                                                     

            MI_TDIAS := GL_NUMDIASS;
            MI_TOTALBASEPARAFISCALN := 0;
            GL_BASECAJASP := CASE WHEN GL_BASECAJASP <0 THEN 0 ELSE GL_BASECAJASP  END;
            MI_APORTEPARA:= PCK_NOMINA_SEGSOCI.FC_APORTE(UN_IBC        => GL_BASECAJASP,
                                                         UN_DIAS       => MI_TDIAS,
                                                         UN_DECIMALES  => -2,
                                                         UN_PORCENTAJE => PCK_PARENTR.PARAMETRO47);
            IF GL_EXONERACIONSALUD = 'SI' THEN
                GL_CN101AP := PCK_NOMINA_SEGSOCI.FC_APORTE(UN_IBC        => GL_BASECAJASP,
                                                           UN_DIAS      => MI_TDIAS,
                                                           UN_DECIMALES => -2,
                                                           UN_PORCENTAJE => PCK_PARENTR.PARAMETRO47);
                                                           
                -- TICKET 7736122 EFCM : PARA EXONERADOS EN CASO DE APORTAR SALUD PATRONO (FUNCIONARIOS CON MAS DE 10 SALARIOS MINIMOS)
                --                       SE REPORTA TAMBIEN SENA E ICBF
                IF ( PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_APORTEPATRONALSALUD > 0) THEN
                    GL_CN102AP := PCK_NOMINA.FC_CNA(102);
                    GL_CN103AP := PCK_NOMINA.FC_CNA(103);
                ELSE
                    GL_CN102AP := 0;
                    GL_CN103AP := 0;
                END IF;
                -- TICKET 7736122 FIN --
                
                GL_CN104AP := 0;
                GL_CN105AP := 0;
            ELSE
                IF MI_APORTEPARA = PCK_NOMINA.FC_CNA(101)
                OR MI_APORTEPARA < PCK_NOMINA.FC_CNA(101) - 200
                OR MI_APORTEPARA < PCK_NOMINA.FC_CNA(101) + 200 THEN
                    GL_CN101AP := PCK_NOMINA.FC_CNA(101);
                    GL_CN102AP := PCK_NOMINA.FC_CNA(102);
                    GL_CN103AP := PCK_NOMINA.FC_CNA(103);
                    GL_CN104AP := PCK_NOMINA.FC_CNA(104);
                    GL_CN105AP := PCK_NOMINA.FC_CNA(105);
                ELSE
                    GL_CN101AP := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASECAJASP * PCK_PARENTR.PARAMETRO47 /100
                                                                        , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                                         , PCK_NOMINA.GL_RAPORTES1990);
                    GL_CN102AP := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASECAJASP * PCK_PARENTR.PARAMETRO45 /100
                                                                        , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                                         , PCK_NOMINA.GL_RAPORTES1990);
                    GL_CN103AP := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASECAJASP * PCK_PARENTR.PARAMETRO46 /100
                                                                        , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                                         , PCK_NOMINA.GL_RAPORTES1990);
                    GL_CN104AP := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASECAJASP * PCK_PARENTR.PARAMETRO48 /100
                                                                        , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                                         , PCK_NOMINA.GL_RAPORTES1990);
                    GL_CN105AP := PCK_SYSMAN_UTL.FC_ROUND_100( GL_BASECAJASP * PCK_PARENTR.PARAMETRO49 /100
                                                                        , MI_PARAMETRO1990APLICA, PCK_NOMINA.GL_SUMARSS1990
                                                         , PCK_NOMINA.GL_RAPORTES1990);
                END IF;
            END IF;

            --(MZANGUNA:07/03/2019)-Se valida variaciÂ¿emporal de sueldo
            --(APINEDA:02/09/2019)-Se elimina condiciÃ³n exclusiva del operador SOI y se deja a nivel general la validaciÃ³n de VariaciÃ³n Temporal de Salario TAR 1000094026
            MI_SALARIO := CASE WHEN GL_SUELDO < PCK_PARENTR.PARAMETRO20
                          THEN CASE WHEN PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC = PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => PCK_PARENTR.PARAMETRO20
                                                                                                              ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990)
                                     AND PCK_NOMINA_COM5.CSOI(UN_I).BASE  = PCK_PARENTR.PARAMETRO20
                               THEN PCK_PARENTR.PARAMETRO20
                               ELSE PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC
                               END
                          ELSE PCK_NOMINA_COM5.CSOI(UN_I).SALARIO_BASE_IBC
                          END;
            IF ABS(PCK_SYSMAN_UTL.FC_ROUND((MI_SALARIO * GL_NUMDIAS) / 30, 0) - (PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_BASEPROPORCIONAL))  BETWEEN 0 AND 100 THEN --(MZANGUNA:07/03/2019)- Se valida solo la lÂ¿a de sueldo
                GL_NVST := ' ';
            ELSE
                GL_NVST :='X';
            END IF;
            --(MZANGUNA:07/03/2019)-Se valida variaciÂ¿emporal de sueldo

            IF (GL_CN102AP + GL_CN103AP + GL_CN104AP + GL_CN105AP) = 0
            AND (PCK_NOMINA.GL_OPERADOR IN('APORTESENLINEA', 'MIPLANILLA', 'ASOPAGOS'))
            OR (PCK_NOMINA.GL_OPERADOR = 'SOI.COM.CO' AND GL_EXONERACIONSALUD = 'SI') THEN
              ---<TAR:7702448 FECHA:07/02/2022 AUTOR:CP>
              IF GL_EXONERADO AND PCK_NOMINA_COM5.CSOI(UN_I).TIPOVINCULACION NOT IN('12', '19', '23', '51') AND GL_EXO_OTR_PAR THEN  --JM CC 2410
                 GL_CAMPO95 := 0;
              END IF;
              --</TAR>
            END IF;

            GL_FSPPLANILLA := 0;
            GL_FSPADICPLANILLA := 0;
            --(MZANGUNA:01/03/2019)- Se ajusta la columna 51 y 52
            GL_FSPPLANILLA := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_FSP;
            GL_FSPADICPLANILLA := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_FSPADICIONAL + PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_FSPSUBSISTENCIA;

            MI_SUMAAPORTESFSPN := MI_SUMAAPORTESFSPN + GL_FSPPLANILLA + GL_FSPADICPLANILLA;
            MI_SUMAAPORTESPENSIONN := MI_SUMAAPORTESPENSIONN + GL_PENSIONAP;
            MI_SUMAAPORTESSALUDN := MI_SUMAAPORTESSALUDN   + GL_SALUDAP;
            MI_SUMAAPORTESRIESGOSN := MI_SUMAAPORTESRIESGOSN + PCK_NOMINA_COM5.CSOI(UN_I).RIESGOS;

            IF NOT((MI_TOTDIASNOV >=30 AND GL_BASEPENSIONP = 0)
            AND (PCK_NOMINA.FC_CNA(347)>=30
            OR PCK_NOMINA.FC_CNA(2) + PCK_NOMINA.FC_CNA(150) = 0)) THEN

                MI_CAMPOSNUEVOS2388N := FC_CONCATENARCAMPSO80A97;
                        
                --<TAR:7702452 FECHA:24/01/2022 AUTOR:CP>
                IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'APLICA EXONERACION EN BASE TOTAL INGRESOS 097',6,SYSDATE,-1),'NO') = 'SI' THEN --18122018
                    PCK_NOMINA.GL_IBLREXONERACION :=  PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_APORTEPATRONALSALUD; --CONCEPTO 116
                    MI_VALOREXO := PCK_NOMINA.GL_IBLREXONERACION;
                    IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'APLICAR EXONERACION APORTES SALUD PATRONO 8.5%',6,SYSDATE,-1),'NO') = 'SI'  AND PCK_NOMINA.GL_IBLREXONERACION > 0 THEN
                         GL_PORCSALUD := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_PORC_SALUDTOTAL;  
                    ELSE 
                        GL_PORCSALUD := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_PORC_SALUDE;   
                    END IF;
                ELSE
	                 --(MZANGUNA:10/12/2018)-Envio de parÃƒâ€šÃ‚Â¿tro UN_NUEVATABLA
	                 -- TICKET 7736122 EFCM : PARA EXONERADOS APORTES SALUD 1607 EN CASO DE APORTAR SALUD PATRONO (FUNCIONARIOS CON MAS DE 10 SALARIOS MINIMOS)
	                 --                       SE REPORTA PORCENTAJE SALUD TOTAL, DE LO CONTRARIO PORCENTAJE SALUD EMPLEADO
	                 IF ( PCK_PARENTR.PARAMETRO70 = 'S' ) THEN
	                    PCK_NOMINA.GL_IBLREXONERACION :=  PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_APORTEPATRONALSALUD; --CONCEPTO 116
	                    MI_VALOREXO := PCK_NOMINA.GL_IBLREXONERACION;
	                    IF ( PCK_NOMINA.GL_IBLREXONERACION > 0 ) THEN
	                        GL_PORCSALUD := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_PORC_SALUDTOTAL;  
	                    ELSE 
	                        GL_PORCSALUD := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_PORC_SALUDE;   
	                    END IF;  
	                 ELSE
	                    GL_PORCSALUD := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_PORC_SALUDE + PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_PORC_SALUDP;  
	                 END IF;
	                 -- TICKET 7736122 EFCM --
                END IF;
                --</TAR>
                GL_PORCPENSION := PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_PORC_PENSIONE + PCK_NOMINA_COM5.CSOI(UN_I).NOVEDAD_PORC_PENSIONP;
            IF  NOT( PCK_NOMINA.GL_PAGOUPC = 'SI' OR PCK_NOMINA_COM5.GL_UPCADICIONALPILA = 'SI') THEN
                PCK_NOMINA_COM5.GL_DISCOLIQUIDACION :=  PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                       FC_REGISTROPLANO (UN_COMPANIA         => UN_COMPANIA,
                                                         UN_I                => UN_I,
                                                         UN_TIPOESTRUCTURA   => UN_TIPOESTRUCTURA,
                                                         UN_TIPOPLANILLA     => UN_TIPOPLANILLA,
                                                         UN_CLASEREGISTRO    => 'NOVF',
                                                         UN_CAMPOSNUEVOS2388 => MI_CAMPOSNUEVOS2388N,
                                                         UN_NUEVATABLA       => -1);            
            ELSIF  CASE WHEN  PCK_NOMINA_COM5.GL_UPCPATRONO ='SI' THEN PCK_NOMINA_COM5.CSOI(UN_I).UPCPATRONO ELSE PCK_NOMINA_COM5.CSOI(UN_I).UPC END <= 0 THEN
                PCK_NOMINA_COM5.GL_DISCOLIQUIDACION :=  PCK_NOMINA_COM5.GL_DISCOLIQUIDACION || CHR(13) || CHR(10) ||
                                       FC_REGISTROPLANO (UN_COMPANIA         => UN_COMPANIA,
                                                         UN_I                => UN_I,
                                                         UN_TIPOESTRUCTURA   => UN_TIPOESTRUCTURA,
                                                         UN_TIPOPLANILLA     => UN_TIPOPLANILLA,
                                                         UN_CLASEREGISTRO    => 'NOVF',
                                                         UN_CAMPOSNUEVOS2388 => MI_CAMPOSNUEVOS2388N,
                                                         UN_NUEVATABLA       => -1);
                   
            END IF;

            END IF;

            DECLARE
            MI_MSG      PCK_SUBTIPOS.TI_CLAVEVALOR;
            BEGIN
                MI_MSG(1).CLAVE := 'EMPLEADO';
                MI_MSG(1).VALOR := PCK_NOMINA_COM5.CSOI(UN_I).NOMBRECOMPLETO || '. ' || LPAD(PCK_NOMINA_COM5.CSOI(UN_I).NUMERO_DCTO, 16);
                IF MI_SUMAAPORTESPENSIONN <> PCK_NOMINA_COM5.CSOI(UN_I).PENSION THEN
                    IF MI_SUMAAPORTESPENSIONN - PCK_NOMINA_COM5.CSOI(UN_I).PENSION >= -200
                    AND MI_SUMAAPORTESPENSIONN - PCK_NOMINA_COM5.CSOI(UN_I).PENSION <= 200 THEN
                        MI_MSG(2).CLAVE := 'TIPOVALOR';
                        MI_MSG(2).VALOR := 'PensiÂ¿' || TO_CHAR(MI_SUMAAPORTESPENSIONN - PCK_NOMINA_COM5.CSOI(UN_I).PENSION);
                        PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA  => UN_COMPANIA,
                                UN_MENSAJE_COD => PCK_ERRORES.ALER_SUMAORESTADELPLANO,
                                UN_REEMPLAZOS  => MI_MSG);
                    END IF;
                END IF;
                IF MI_SUMAAPORTESSALUDN <> (PCK_NOMINA_COM5.CSOI(UN_I).SALUD) THEN
                    IF MI_SUMAAPORTESSALUDN - (PCK_NOMINA_COM5.CSOI(UN_I).SALUD) >= -200 AND
                        MI_SUMAAPORTESSALUDN - (PCK_NOMINA_COM5.CSOI(UN_I).SALUD) <= 200 Then
                        MI_MSG(2).CLAVE := 'TIPOVALOR';
                        MI_MSG(2).VALOR := 'Salud $' || TO_CHAR(MI_SUMAAPORTESSALUDN - PCK_NOMINA_COM5.CSOI(UN_I).SALUD);
                        PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA  => UN_COMPANIA,
                        UN_MENSAJE_COD => PCK_ERRORES.ALER_SUMAORESTADELPLANO,
                        UN_REEMPLAZOS  => MI_MSG);
                    END IF;
                END IF;
                IF MI_SUMAAPORTESRIESGOSN <> (PCK_NOMINA_COM5.CSOI(UN_I).RIESGOS) THEN
                    IF MI_SUMAAPORTESRIESGOSN - (PCK_NOMINA_COM5.CSOI(UN_I).RIESGOS) >= -200
                    AND MI_SUMAAPORTESRIESGOSN - (PCK_NOMINA_COM5.CSOI(UN_I).RIESGOS) <= 200 THEN
                        MI_MSG(2).CLAVE := 'TIPOVALOR';
                        MI_MSG(2).VALOR := 'Riesgos $' || TO_CHAR(MI_SUMAAPORTESRIESGOSN - PCK_NOMINA_COM5.CSOI(UN_I).RIESGOS);
                        PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA  => UN_COMPANIA,
                            UN_MENSAJE_COD => PCK_ERRORES.ALER_SUMAORESTADELPLANO,
                            UN_REEMPLAZOS  => MI_MSG);

                    END IF;
                END IF;
                IF MI_SUMAAPORTESFSPN <> (PCK_NOMINA_COM5.CSOI(UN_I).FSP + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL)
                AND ((MI_SUMAAPORTESFSPN  - (PCK_NOMINA_COM5.CSOI(UN_I).FSP + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL)) >= -200
                AND (MI_SUMAAPORTESFSPN  - (PCK_NOMINA_COM5.CSOI(UN_I).FSP + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL)) <= 200) Then
                    MI_MSG(2).CLAVE := 'TIPOVALOR';
                    MI_MSG(2).VALOR := 'FSP $' || TO_CHAR(MI_SUMAAPORTESFSPN  - (PCK_NOMINA_COM5.CSOI(UN_I).FSP + PCK_NOMINA_COM5.CSOI(UN_I).FSPADICIONAL));

                END IF;
            END;
        END IF; -- CIERRE IF SUELDO
    --END IF;

    RETURN MI_CANTENC;
END FC_NOVEDADESPILA2388_TABLASEGS;

FUNCTION FC_DISCOFAVIDI 
/*
  NAME              : FC_DISCOFAVIDI
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : LUIS MAURICIO MOSQUERA C
  DATE MIGRATION    : 02/09/2020
  TIME              : 10:15 AM
  SOURCE MODULE     : NOMINA (6)
  MODIFIED BY       : 
  MODIFICATIONS     : 
  DESCRIPTION       : FunciÃ³n que permite generar archivo plano para Foncep-Favidi
  PARAMETERS        : 
  @Name             :generarDiscoFoncepFavidi
  @Method           :GET
*/
(
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO      IN  PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    /*UN_ID_EMPLEADO  IN  PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,*/
    UN_USUARIO      IN  PCK_SUBTIPOS.TI_USUARIO,
    UN_PERIODO      IN  PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_OPCION       IN  VARCHAR2,
    UN_MES          IN  PCK_SUBTIPOS.TI_MES,
    UN_ANO          IN  PCK_SUBTIPOS.TI_ANIO
)
RETURN CLOB AS 

MI_ENCABEZADO_DISCO     VARCHAR2(250);
MI_DATOS_DISCO          CLOB;
MI_FINAL_DISCO          VARCHAR2(250);
MI_DIASLAB              VARCHAR2(250);
MI_CANTIDAD             NUMBER := 0;
MI_VALOR                PCK_SUBTIPOS.TI_LONG := 0;
MI_VALOR1               VARCHAR(50);
MI_PERIODO              NUMBER := 0;

BEGIN
    MI_PERIODO :=   CASE UN_PERIODO
                        WHEN    3    THEN    1
                        WHEN    2    THEN    2
                        WHEN    5    THEN    3
                    END;
    MI_ENCABEZADO_DISCO := MI_ENCABEZADO_DISCO || '3230/' || LPAD(UN_MES,2,'0') || '/' || UN_ANO || MI_PERIODO || CHR(13) || CHR(10) ;
    
    -- TICKET 7739825 EFCM: SE ADICIONA A LA CONSULTA LA RESTRICCION PARA FUNCIONARIOS SOLO AFILIADOS A FONDO CESANTIAS FONCEP (CES286)
    --						SE CAMBIA CONSULTA A TABLA PERSONAL_HISTORICO
    FOR MI_RS IN (
       SELECT
            PERSONAL_HISTORICO.NUMERO_DCTO AS DOC_PERSONAL,
            CONCEPTOS_FAVIDI.CODIGO AS CONCEPTO,
            LPAD(TRIM(TO_CHAR(MAX(HISTORICOS.VALOR)*100, '9999999999,99')),13,'0') AS VALOR,
            MAX(HISTORICOS.VALOR) TOTAL
        FROM
            HISTORICOS
            INNER JOIN PERSONAL_HISTORICO
                ON HISTORICOS.COMPANIA = PERSONAL_HISTORICO.COMPANIA
                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL_HISTORICO.ID_DE_EMPLEADO
                AND HISTORICOS.ID_DE_PROCESO = PERSONAL_HISTORICO.ID_DE_PROCESO
                AND HISTORICOS.ANO = PERSONAL_HISTORICO.ANO
                AND HISTORICOS.MES = PERSONAL_HISTORICO.MES
                AND HISTORICOS.PERIODO = PERSONAL_HISTORICO.PERIODO
            INNER JOIN CONCEPTOS
                ON HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA
                AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
            RIGHT JOIN CONCEPTOS_FAVIDI
                ON CONCEPTOS.ID_DE_CONCEPTO = CONCEPTOS_FAVIDI.ID_DE_CONCEPTO
                AND CONCEPTOS.COMPANIA = CONCEPTOS_FAVIDI.COMPANIA
        WHERE
            HISTORICOS.ANO = UN_ANO
            AND HISTORICOS.MES = UN_MES
            AND HISTORICOS.COMPANIA = UN_COMPANIA
            AND HISTORICOS.ID_DE_PROCESO = UN_PROCESO
            AND HISTORICOS.PERIODO IN (UN_PERIODO,4)
            --AND PERSONAL_HISTORICO.ESTADO_ACTUAL = 1
            AND PERSONAL_HISTORICO.FONDO_CESANTIAS = 'CES286'
            AND HISTORICOS.VALOR > 0
        GROUP  BY
            PERSONAL_HISTORICO.NUMERO_DCTO,
            CONCEPTOS_FAVIDI.CODIGO
        ORDER BY
            PERSONAL_HISTORICO.NUMERO_DCTO,
            CONCEPTOS_FAVIDI.CODIGO
        ) LOOP
        BEGIN

            BEGIN
            SELECT
                CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 9 THEN HISTORICOS.VALOR ELSE 0 END AS DIAS
                INTO MI_DIASLAB
            FROM
                HISTORICOS
                INNER JOIN PERSONAL
                    ON HISTORICOS.COMPANIA = PERSONAL.COMPANIA
                    AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                INNER JOIN CONCEPTOS
                    ON HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA
                    AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
            WHERE
                PERSONAL.NUMERO_DCTO = MI_RS.DOC_PERSONAL
                AND PERSONAL.ESTADO_ACTUAL = 1
                AND HISTORICOS.ID_DE_CONCEPTO = 9
                AND HISTORICOS.ANO = UN_ANO
                AND HISTORICOS.MES = UN_MES
                AND HISTORICOS.COMPANIA = UN_COMPANIA;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_DIASLAB := '00';
            END;

            MI_VALOR := MI_VALOR + PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     =>MI_RS.TOTAL
                                                  ,UN_PRECISION =>   0);

           -- MI_VALOR := MI_VALOR + MI_RS.VALOR;
            MI_CANTIDAD := MI_CANTIDAD + 1; 
            MI_DATOS_DISCO := MI_DATOS_DISCO || LPAD(MI_RS.DOC_PERSONAL,12,'0') || LPAD(MI_RS.CONCEPTO,4,'0') || MI_RS.VALOR ||LPAD(MI_DIASLAB,2,'0') || CHR(13) || CHR(10) ;


        END;
        END LOOP;
        MI_VALOR := (MI_VALOR * 100);
        MI_VALOR1 := LPAD(TRIM(TO_CHAR(MI_VALOR, '999999999999,99')),15,'0');
        MI_DATOS_DISCO := MI_ENCABEZADO_DISCO || MI_DATOS_DISCO || LPAD(MI_CANTIDAD,6,'0') || LPAD(MI_VALOR1,15,'0');

  RETURN MI_DATOS_DISCO;
END FC_DISCOFAVIDI;

FUNCTION FC_DSIP_BANCOBOGOTA 

/*
      NAME              : FC_DSIP_BANCOBOGOTA ---->EnviarBANCObogotaEXCEL
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JUAN DANILO ORDUZ RIVERO
      DATE MIGRADOR     : 27/08/2021
      TIME              : 02 :00 PM
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : Funcion que crea retorna los datos base para el archivo excel de la dipersion del banco bogota
      @Name: generarDisPlanoBBogota
    */
    
      (
      UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANO          IN  PCK_SUBTIPOS.TI_ANIO,
      UN_MES          IN  PCK_SUBTIPOS.TI_MES,
      UN_PERIODO      IN  PCK_SUBTIPOS.TI_PERIODO,
      UN_REFERENCIA   IN  VARCHAR2
      
      ) RETURN CLOB
    
    AS
    
        MI_DATOS_DISCO          CLOB;
        MI_CUENTA_EMP           VARCHAR(50);
        MI_CANTIDAD             NUMBER := 0;
        MI_NOMBRE               VARCHAR(50);
    
    BEGIN
        FOR MI_RS IN (
                SELECT
                    PERSONAL.NOMBRECOMPLETO,
                    HISTORICOS.COMPANIA,
                    HISTORICOS.ID_DE_PROCESO,
                    HISTORICOS.ANO,
                    HISTORICOS.MES,
                    HISTORICOS.PERIODO,
                    HISTORICOS.ID_DE_EMPLEADO,
                    HISTORICOS.ID_DE_CONCEPTO,
                    HISTORICOS.VALOR,
                    PERSONAL.BANCO,
                    PERSONAL.TIPOCUENTA,
                    PERSONAL.CUENTA           AS CUENTAEMPLEADO,
                    BANCOS_NOMINA.CUENTA      AS CUENTAORIGEN,
                    BANCOS_NOMINA.CODIGO_ENTIDAD,
                    PERSONAL.NUMERO_DCTO,
                    BANCOS_NOMINA.PAGOBANCOBOGOTA,
                    PERSONAL.DCTO_IDENTIDAD,
                    PERSONAL.EMAIL_CORPORATIVO,
                    PERSONAL.EMAIL_PERSONAL
                FROM HISTORICOS 
                LEFT JOIN PERSONAL 
                ON HISTORICOS.COMPANIA = PERSONAL.COMPANIA
                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                LEFT JOIN BANCOS_NOMINA 
                ON PERSONAL.BANCO = BANCOS_NOMINA.BANCO 
                AND PERSONAL.COMPANIA = BANCOS_NOMINA.COMPANIA
                WHERE 
                HISTORICOS.COMPANIA = UN_COMPANIA
                AND HISTORICOS.ID_DE_PROCESO = 1
                AND HISTORICOS.ANO = UN_ANO
                AND HISTORICOS.MES = UN_MES
                AND HISTORICOS.PERIODO = UN_PERIODO
                AND HISTORICOS.ID_DE_CONCEPTO = '144'
                AND PERSONAL.BANCO<> '00' 
                AND PERSONAL.BANCO<> '99' 
                AND BANCOS_NOMINA.PAGOBANCOBOGOTA <> 0  
                ORDER BY PERSONAL.NOMBRECOMPLETO
            ) LOOP
            BEGIN
               MI_CUENTA_EMP := REPLACE(MI_RS.CUENTAEMPLEADO,'.','');
               MI_CUENTA_EMP := REPLACE(MI_CUENTA_EMP,'-','');
               MI_CANTIDAD := MI_CANTIDAD + 1;
               MI_NOMBRE := SUBSTR(MI_RS.NOMBRECOMPLETO,0,40);
               MI_DATOS_DISCO := MI_DATOS_DISCO || 
                                LPAD(MI_RS.DCTO_IDENTIDAD,1,'C')                 || ';' || 
                                SUBSTR(MI_NOMBRE,0,40)                                        || ';' || 
                                MI_RS.NUMERO_DCTO                                || ';' ||
                                MI_RS.TIPOCUENTA                                 || ';' ||
                                MI_CUENTA_EMP                                    || ';' ||
                                MI_RS.VALOR                                      || ';' ||
                                TO_CHAR(SUBSTR(MI_RS.CODIGO_ENTIDAD,0,3))                             || ';' ||
                                UN_REFERENCIA                                    || ';' ||
                                MI_RS.EMAIL_PERSONAL                             || '#';
    
            END;
            END LOOP;
      RETURN MI_DATOS_DISCO;
    
    END FC_DSIP_BANCOBOGOTA;

PROCEDURE PR_CALCESANTIASFNAITACACIAS
/*
    NAME              : PR_CALCESANTIASFNAITACACIAS  -> EN ACCESS calcularcesantiasFNAALCTOCANCIPA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 28/08/2025
    TIME              : 02:37 PM
    SOURCE MODULE     : NOMINA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  CALCULARCESANTIASFNAITTACACIAS

  */

  AS

    MI_CONCEPTODOCEAVASFNA  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIAS                 PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASINT              PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_APLICADAS            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;

    MI_PS                   PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PVFNA                PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PNFNA                PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_HEFNA                PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PROMFAC              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CESANTIA1            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_ANTIC_ENC            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PROM_ENC             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIASCES_ENC          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_ANTICIPOS            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_BASPFNA              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CONCEPTOAJUSTEBASP   PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
BEGIN

    MI_CONCEPTODOCEAVASFNA := TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FNA', '483'));
    MI_CONCEPTOAJUSTEBASP := TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO AJUSTES B.A.S.P.', '0'));

    IF (PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.CPARENTRADA(1).NIT = '844.000.755-4') OR (PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.CPARENTRADA(1).NIT = '899.999.428-8') THEN
        RETURN;
    END IF;

    PCK_NOMINA.GL_LICENCIAS := 0;
    PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
    PCK_NOMINA.GL_BASCES := 0;
    MI_PROMFAC := 0;

    PCK_NOMINA.GL_SBM := CASE WHEN PCK_NOMINA.FC_CN(900) = 0 THEN CASE WHEN PCK_NOMINA.FC_CN(10) > 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END ELSE PCK_NOMINA.FC_CN(900) END;
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
        PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM; -- + COMISIONES;
    ELSE
        PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM;
    END IF;

    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
        PCK_NOMINA.GL_FECHAIC := PCK_NOMINA.GL_FECHAIR ;
    ELSE
        PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO ,'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO ,'DD/MM/YYYY') END;
    END IF;

    IF PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 7 THEN
        MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')) - PCK_NOMINA.GL_LICENCIAS;
        MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN => CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END
            ,UN_FECHAFIN => TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'));
    ELSE
        MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
        MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END, PCK_NOMINA.GL_FECHAFIN);

        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > TO_DATE('01/01' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
            MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > TO_DATE('01/01' ||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA ELSE TO_DATE('01/01' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END, PCK_NOMINA.GL_FECHAFIN1);
        END IF;
    END IF;
    MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
    PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155);

    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION ;
        MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_PARST.FC_PAR('DESCONTAR SOLO ANTICIPOS Y NO APLICADAS', ' ') = 'SI' THEN
            MI_APLICADAS := 0;
            PCK_NOMINA.CN(915) := 0;
        ELSE
            MI_APLICADAS := PCK_NOMINA.FC_CNA(277) + PCK_NOMINA.FC_CNA(483);
            PCK_NOMINA.CN(915) := MI_APLICADAS;
        END IF;

    ELSE
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

        IF PCK_PARST.FC_PAR('DESCONTAR SOLO ANTICIPOS Y NO APLICADAS', ' ') = 'SI' THEN
            MI_APLICADAS := 0;
            PCK_NOMINA.CN(915) := 0;
        ELSE
            MI_APLICADAS := PCK_NOMINA.FC_CNA(277) + PCK_NOMINA.FC_CNA(483);
            PCK_NOMINA.CN(915) := MI_APLICADAS;
        END IF;

        PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION ;
        MI_DIAS := MI_DIAS - PCK_NOMINA.GL_LICENCIAS;
    END IF;
    IF PCK_NOMINA.GL_SPER = 8 THEN
        PCK_NOMINA.CN(2) := PCK_NOMINA.FC_CN(1);
    END IF;

    MI_HEFNA := 0;
    MI_PS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(543)) / 12, 0) ;
    MI_PVFNA := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CNA(155) = PCK_NOMINA.FC_CN(155) THEN PCK_NOMINA.FC_CN(155) ELSE PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) END + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CN(541) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CNA(541)) / 12, 0) ;
    MI_PNFNA := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CNA(158) = PCK_NOMINA.FC_CN(158) THEN PCK_NOMINA.FC_CN(158) ELSE PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) END + PCK_NOMINA.FC_CN(504) + PCK_NOMINA.FC_CNA(504)) / 12, 0) ;

    MI_HEFNA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CNA(528)) / 12, 0);
    MI_BASPFNA := PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(MI_CONCEPTOAJUSTEBASP);

    MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(515) + PCK_NOMINA.FC_CN(62) + PCK_NOMINA.FC_CN(506) + PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CN(525) + PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CN(524)
                    + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.GL_PVAC + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CN(504) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(543) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538) END
                    + MI_PS + MI_PVFNA + MI_PNFNA + CASE WHEN PCK_NOMINA.FC_CN(70) = 0 THEN MI_HEFNA ELSE 0 END;

    IF PCK_NOMINA.GL_VPA = 0 AND PCK_NOMINA.CPARENTRADA(1).NIT = '811036609-2' THEN
        PCK_NOMINA.GL_VPA := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_VALORULTIMOQUINQUENIO(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
    END IF;

    PCK_NOMINA_CALCULO.PR_SUMACESANTIASCONSOLIDADASCN;

    IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FNA', ' ') = 'SI' THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, (PCK_NOMINA.GL_SANO), PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    END IF;

    IF PCK_NOMINA.GL_SPER = 8 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,  PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 7, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

        MI_PS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(543)) / 12, 0) ;
        PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(543)) / 12, 0) ;

        PCK_NOMINA.CN(902) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CNA(528)) / 12, 0);
        MI_PROMFAC := 0;

        MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_NOMINA.GL_SBM + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_VPA + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CNA(528)) / 12, 0) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA ;

        MI_PROMFAC := MI_PROMFAC + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0) + MI_PS + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_CALCULO.FC_VLRULTIMPRIMAEXTRASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(155) > 0 THEN (PCK_NOMINA.FC_CN(155) / 12) ELSE (PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12 END, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_CALCULO.FC_VALORULTIMAPRIMANAVIDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
        MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC, 0) END;
        MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
        PCK_NOMINA.CN(MI_CONCEPTODOCEAVASFNA) := 0;

        MI_DIASCES_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
        IF PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) <> 0 THEN
            MI_PROM_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'V', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
            MI_ANTIC_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'R', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
            MI_DIAS := MI_DIAS - PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);

            IF MI_DIAS < 0 THEN
                MI_DIAS := 0;
            END IF;
        ELSE
            MI_PROM_ENC := 0;
            MI_ANTIC_ENC := 0;
        END IF;
        PCK_NOMINA.CN(MI_CONCEPTODOCEAVASFNA) := CASE WHEN PCK_NOMINA.FC_CN(MI_CONCEPTODOCEAVASFNA) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((MI_CESANTIA1 / 360 * MI_DIAS) + CASE WHEN MI_DIASCES_ENC > 0 THEN MI_ANTIC_ENC ELSE 0 END - MI_APLICADAS - MI_ANTICIPOS, 0) ELSE PCK_NOMINA.FC_CN(MI_CONCEPTODOCEAVASFNA) END;
        PCK_NOMINA.CN(910) := MI_DIAS;
        PCK_NOMINA.CN(912) := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) ;

        PCK_NOMINA.CN(900) := PCK_NOMINA.GL_SBM ;
        PCK_NOMINA.CN(903) := PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CNA(525);
        PCK_NOMINA.CN(904) := PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CNA(524);
        PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(155) > 0 THEN (PCK_NOMINA.FC_CN(155) / 12) ELSE (PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(501)) / 12 END, 0);
        PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_CALCULO.FC_VALORULTIMAPRIMANAVIDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(504)) / 12, 0);

        PCK_NOMINA.CN(908) := PCK_NOMINA.GL_GRPNGV;
        PCK_NOMINA.CN(909) := 0;
        PCK_NOMINA.CN(911) := MI_APLICADAS + MI_ANTICIPOS ;
        PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0) ;
        PCK_NOMINA.CN(914) := 0 ;
        PCK_NOMINA.CN(915) := MI_APLICADAS + MI_ANTICIPOS ;
        PCK_NOMINA.CN(901) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(514)) / 12, 0);
        IF PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 7 THEN
            PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'));
        ELSE
            PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1);
        END IF;
    ELSE
            IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FNA', ' ') = 'SI' THEN
                MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0) END;
                IF (PCK_NOMINA.FC_CN(94) + PCK_NOMINA.FC_CN(347) + PCK_NOMINA.FC_CN(35) + PCK_NOMINA.FC_CN(336)) > 0 AND PCK_NOMINA.GL_PROMCONSOLIDADASCN < PCK_NOMINA.FC_CN(1) THEN
                    MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT ;
                    IF PCK_NOMINA.GL_SPER = '01' OR PCK_NOMINA.GL_SPER = '02' THEN
                        MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC / 2, 0);
                    END IF;
                    --ALERTA 'EL EMPLEADO ' CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' CPERSONAL(PCK_NOMINA.P).NOMBRES & ', INGRESOS MENORES AL SMB. SE AJUSTARA BASE PARA APORTES FNA. ' & ', CEDULA NO.' CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO & ',TIPO: ' CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO
                END IF;
            ELSE
                IF PCK_NOMINA.GL_SPER = 1 THEN
                    PCK_NOMINA.CN(969) := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + MI_PS + MI_PVFNA + MI_PNFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0) END;
                    MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + MI_PS + MI_PVFNA + MI_PNFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0) END;
                ELSE
                    MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PNFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0) END;
                END IF;
            END IF;
            IF PCK_NOMINA.GL_SPER = '07' THEN
                MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + MI_PS + MI_PVFNA + MI_PNFNA + MI_HEFNA + PCK_NOMINA.GL_VPA + MI_BASPFNA, 0);
            END IF;

            IF PCK_NOMINA.GL_SPER = '01' THEN
                PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(1)  ;
            ELSE
                PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(506) + PCK_NOMINA.FC_CN(515) ;
            END IF;

            PCK_NOMINA.CN(904) := PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CN(524);
            PCK_NOMINA.CN(903) := PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CN(525);

            IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FNA', ' ') = 'SI' THEN
                PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0) ;

                MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
                MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0)  ;
                IF (PCK_NOMINA.FC_CN(94) + PCK_NOMINA.FC_CN(347) + PCK_NOMINA.FC_CN(35) + PCK_NOMINA.FC_CN(336)) > 0 AND PCK_NOMINA.GL_PROMCONSOLIDADASCN < PCK_NOMINA.FC_CN(1) THEN
                    MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT ;
                    IF PCK_NOMINA.GL_SPER = '01' OR PCK_NOMINA.GL_SPER = '02' THEN
                        MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC / 2, 0);
                    END IF;
                    MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
                    --ALERTA 'EL EMPLEADO ' CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' CPERSONAL(PCK_NOMINA.P).NOMBRES & ', INGRESOS MENORES AL SMB. SE AJUSTARA BASE PARA APORTES FNA. ' & ', CEDULA NO.' CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO & ',TIPO: ' CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO
                END IF;
                IF PCK_NOMINA.GL_SPER = '07' THEN
                    MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + MI_PS + MI_PVFNA + MI_PNFNA + PCK_NOMINA.GL_VPA, 0) ;
                    MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + MI_PS + MI_PVFNA + MI_PNFNA + PCK_NOMINA.GL_VPA, 0) ;
                END IF;
            ELSE
                IF PCK_NOMINA.GL_SPER = '02' OR PCK_NOMINA.GL_SPER = '03' THEN
                    IF PCK_NOMINA.GL_PROMCONSOLIDADASCN < PCK_NOMINA.FC_CN(1) AND PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FNA', ' ') = 'SI' THEN
                        PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_NOMINA.FC_CN(1);
                    ELSE
                        PCK_NOMINA.GL_PROMCONSOLIDADASCN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 AND PCK_NOMINA.FC_CN(11) > 15 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA END;
                    END IF;

                    IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FNA', ' ') = 'SI' THEN
                        PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PNFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0);

                        MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0) END;
                    ELSE
                        MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PNFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0) END;
                    END IF;
                    MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
                    MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PNFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0);
                ELSE
                    PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + MI_PS + MI_PVFNA + MI_PNFNA + PCK_NOMINA.GL_VPA, 0);
                    MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
                    MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PNFNA + PCK_NOMINA.GL_VPA, 0);
                END IF;
            END IF;
            IF PCK_NOMINA.GL_SPER = 4 THEN
                PCK_NOMINA_CALCULO.PR_SUMACESANTIASCONSOLIDADASCN;
                MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0) END;
                PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(1);
                PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
                PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
            END IF;
        --END IF;

        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA,913, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CN(MI_CONCEPTODOCEAVASFNA) = 0 THEN
            IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FNA', ' ') = 'SI' AND PCK_NOMINA.GL_SPER <> 7 THEN
                IF PCK_NOMINA.GL_SPER = 2 THEN
                    PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0)   ;
                    PCK_NOMINA.CN(MI_CONCEPTODOCEAVASFNA) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC / 12), 0);
                ELSE
                    PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC) - PCK_NOMINA.FC_CNP(913), 0) ;
                    PCK_NOMINA.CN(MI_CONCEPTODOCEAVASFNA) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC / 12) - PCK_NOMINA.FC_CNA(MI_CONCEPTODOCEAVASFNA), 0);
                END IF;
            ELSE
                IF PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3 THEN
                    PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC) - PCK_NOMINA.FC_CNP(913), 0) ;
                ELSE
                    PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0) ;
                END IF;

                MI_DIASCES_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
                IF PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) <> 0 THEN
                    MI_PROM_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'V', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
                    MI_ANTIC_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'R', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
                    MI_DIAS := MI_DIAS - PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
                    IF MI_DIAS < 0 THEN
                        MI_DIAS := 0;
                    END IF;
                ELSE
                    MI_PROM_ENC := 0;
                    MI_ANTIC_ENC := 0;
                END IF;
                PCK_NOMINA.CN(MI_CONCEPTODOCEAVASFNA) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC / 360 * MI_DIAS) + CASE WHEN MI_DIASCES_ENC > 0 THEN MI_ANTIC_ENC ELSE 0 END - MI_APLICADAS - MI_ANTICIPOS, 0);
                IF PCK_NOMINA.GL_SMES < 12 OR (PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.GL_SPER = 3) THEN
                    PCK_NOMINA.CN(MI_CONCEPTODOCEAVASFNA) := CASE WHEN PCK_NOMINA.FC_CN(MI_CONCEPTODOCEAVASFNA) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(MI_CONCEPTODOCEAVASFNA) END;
                END IF;
            END IF;
        ELSE
            PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(MI_CONCEPTODOCEAVASFNA)) * 12, 0) ;
        END IF;

        PCK_NOMINA.CN(901) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(150) / 12, 0);
        PCK_NOMINA.CN(902) := MI_HEFNA ;
        PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(504)) / 12, 0);
        PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(543)) / 12, 0);
        IF PCK_NOMINA.GL_SMES = 6 AND PCK_NOMINA.GL_SANO = 2010 THEN
            PCK_NOMINA.CN(907) := 0 ;
        ELSE
            PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0);
        END IF;
        PCK_NOMINA.CN(908) := PCK_NOMINA.GL_GRPNGV;
        PCK_NOMINA.CN(909) := 0;

        PCK_NOMINA.CN(910) := PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CN(11) ;
        IF PCK_NOMINA.GL_SPER = 1 OR PCK_NOMINA.GL_SPER = 2 AND PCK_NOMINA.FC_CN(910) > 15 THEN
            PCK_NOMINA.CN(910) := CASE WHEN (PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CN(11)) = 30 OR (PCK_NOMINA.FC_CN(9) = PCK_NOMINA.FC_CN(11)) THEN 15 ELSE PCK_NOMINA.FC_CN(9) END  ;
        END IF;
        PCK_NOMINA.CN(911) := MI_ANTICIPOS ;
        PCK_NOMINA.CN(912) := PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) ;
        PCK_NOMINA.CN(914) := 0 ;
        IF PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 7 THEN
            PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'));
            PCK_NOMINA.CN(MI_CONCEPTODOCEAVASFNA) := 0;
            PCK_NOMINA.CN(913) := 0;
        ELSE
            PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1);
        END IF;
    END IF;

END PR_CALCESANTIASFNAITACACIAS;

PROCEDURE PR_CALCESANTIASFTPITACACIAS
/*
  NAME              : PR_CALCESANTIASFTPITACACIAS
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
  DATE MIGRADOR     : 28/08/2025
  TIME              : 04:00 PM
  SOURCE MODULE     : NOMINA
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       :
  @NAME:  CALCULARCESANTIASFTPITTACACIAS
*/
AS

  MI_ALIMRET        PCK_SUBTIPOS.TI_DOBLE :=0 ;
  MI_SBM          PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_PROMFAC        PCK_SUBTIPOS.TI_DOBLE :=0 ;
  MI_COMISIONES     PCK_SUBTIPOS.TI_DOBLE :=0 ;
  MI_FECHAIC        DATE;
  MI_DIAS         PCK_SUBTIPOS.TI_ENTERO :=0;
  MI_DIASINT        PCK_SUBTIPOS.TI_ENTERO :=0;
  MI_ANTICIPOS      PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_APLICADAS      PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_PS           PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_PVFNA        PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_HEFNA        PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_CESANTIA1      PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_PROM_ENC       PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_ANTIC_ENC      PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_DIASCES_ENC      PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_MSG              PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN

  PCK_NOMINA.GL_LICENCIAS := 0;
  PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION ;
  MI_PROMFAC := 0;
  MI_SBM := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(900) = 0, PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) > 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)), PCK_NOMINA.FC_CN(900));
  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
    MI_SBM := MI_SBM + MI_COMISIONES;
  ELSE
    MI_SBM := MI_SBM;
  END IF;

  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
    MI_FECHAIC := PCK_NOMINA.GL_FECHAIR;
  ELSE
    MI_FECHAIC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), PCK_NOMINA.GL_FECHAIR, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'));
  END IF;
  IF PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 7 THEN
    MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIC, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')) - PCK_NOMINA.GL_LICENCIAS;
    MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'), PCK_NOMINA.GL_FECHAIR, TO_DATE('01/01' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY')), TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'));
  ELSE
    MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
    MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'), PCK_NOMINA.GL_FECHAIR, TO_DATE('01/01' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY')), PCK_NOMINA.GL_FECHAFIN1);

    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > TO_DATE('01/01' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY') THEN
      MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
      MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > TO_DATE('01/01' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY') THEN  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA ELSE TO_DATE('01/01' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY') END, PCK_NOMINA.GL_FECHAFIN1);
    END IF;
  END IF;
  MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) ;
  PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155);

  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR), PCK_SYSMAN_UTL.FC_IIF(PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16, 1, 2), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
    PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
    MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_PARST.FC_PAR('DESCONTAR SOLO ANTICIPOS Y NO MI_APLICADAS', 'NO') = 'SI' THEN
      MI_APLICADAS := 0;
      PCK_NOMINA.CN(915) := 0;
    ELSE
      MI_APLICADAS := PCK_NOMINA.FC_CN(277) + PCK_NOMINA.FC_CN(483) + PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0')));
      PCK_NOMINA.CN(915) := MI_APLICADAS;
    END IF;

  ELSE
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

    IF PCK_PARST.FC_PAR('DESCONTAR SOLO ANTICIPOS Y NO MI_APLICADAS', 'NO') = 'SI' THEN
      MI_APLICADAS := 0;
      PCK_NOMINA.CN(915) := 0;
    ELSE
      MI_APLICADAS := PCK_NOMINA.FC_CN(277) + PCK_NOMINA.FC_CN(483) + PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0')));
      PCK_NOMINA.CN(915) := MI_APLICADAS;
    END IF;

    PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
    MI_DIAS := MI_DIAS - PCK_NOMINA.GL_LICENCIAS;
  END IF;
  IF PCK_NOMINA.GL_SPER = 8 THEN
    PCK_NOMINA.CN(2) := PCK_NOMINA.FC_CN(1);
  END IF;

  MI_PS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(543) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(543)) / 12, 0) ;
  MI_PVFNA := PCK_SYSMAN_UTL.FC_ROUND((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(155) = PCK_NOMINA.FC_CN(155), PCK_NOMINA.FC_CN(155), PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(155)) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CN(541) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CN(541)) / 12, 0) ;
  MI_PVFNA := PCK_SYSMAN_UTL.FC_ROUND((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(158) = PCK_NOMINA.FC_CN(158), PCK_NOMINA.FC_CN(158), PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(158)) + PCK_NOMINA.FC_CN(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) ;

  MI_HEFNA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CN(528)) / 12, 0);

  MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(515) + PCK_NOMINA.FC_CN(62) + PCK_NOMINA.FC_CN(506) + PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CN(525) + PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CN(524) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.GL_PVAC + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CN(504) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(543) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538)) + MI_PS + MI_PVFNA + MI_PVFNA + PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(70) = 0, MI_HEFNA, 0);
  IF PCK_NOMINA.GL_VPA = 0 AND PCK_NOMINA.CPARENTRADA(1).NIT = '811036609-2' THEN
    PCK_NOMINA.GL_VPA := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_VALORULTIMOQUINQUENIO(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
  END IF;
  PCK_NOMINA_CALCULO.PR_SUMACESANTIASCONSOLIDADASCN;
  IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FTP', 'NO') = 'SI' THEN
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
  END IF;
  IF PCK_NOMINA.GL_SPER = 8 THEN
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 7, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.CN(902) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CN(528)) / 12, 0);
    MI_PROMFAC := 0;
    MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), MI_SBM + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_VPA + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CN(528)) / 12, 0)) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA ;
    MI_PROMFAC := MI_PROMFAC + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0) + MI_PS + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_CALCULO.FC_VLRULTIMPRIMAEXTRASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(155) > 0, PCK_NOMINA.FC_CN(155) / 12, (PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_CALCULO.FC_VALORULTIMAPRIMANAVIDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
    MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC, 0));
    MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
    PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) := 0;
    MI_DIASCES_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
    IF PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) <> 0 THEN
      MI_PROM_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'V', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
      MI_ANTIC_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'R', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
      MI_DIAS := MI_DIAS - PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
      IF MI_DIAS < 0 THEN
        MI_DIAS := 0;
      END IF;
    ELSE
      MI_PROM_ENC := 0;
      MI_ANTIC_ENC := 0;
    END IF;
    PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) = 0, PCK_SYSMAN_UTL.FC_ROUND((MI_CESANTIA1 / 360 * MI_DIAS) + PCK_SYSMAN_UTL.FC_IIF(MI_DIASCES_ENC > 0, MI_ANTIC_ENC, 0) - MI_APLICADAS - MI_ANTICIPOS, 0), PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))));
    IF PCK_NOMINA.FC_CN(477) < 0 THEN
      
      MI_MSG(1).CLAVE := 'NOMEMPLEADO';
      MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
      MI_MSG(2).CLAVE := 'CEDULA';
      MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
      MI_MSG(3).CLAVE := 'TIPO';
      MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

      PCK_NOMINA_COM7.PR_ALERTA
          (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
          ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_ALERSALDONEGATIVO13
          ,UN_REEMPLAZOS   => MI_MSG
          ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
          ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
          ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
          ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
          ,UN_USER         => PCK_CONEXION.FC_GETUSER
          );

    END IF;

    PCK_NOMINA.CN(910) := MI_DIAS;
    PCK_NOMINA.CN(912) := PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) ;

    PCK_NOMINA.CN(900) := MI_SBM ;
    PCK_NOMINA.CN(903) := PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CN(525);
    PCK_NOMINA.CN(904) := PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CN(524);
    PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(155) > 0, PCK_NOMINA.FC_CN(155) / 12, (PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(501)) / 12), 0);
    PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_CALCULO.FC_VALORULTIMAPRIMANAVIDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(504)) / 12, 0);
    PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(503)) / 12 + (PCK_NOMINA_CALCULO.FC_VLRULTIMPRIMAEXTRASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
    PCK_NOMINA.CN(908) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(909) := 0;
    PCK_NOMINA.CN(911) := MI_APLICADAS + MI_ANTICIPOS;
    PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0);
    PCK_NOMINA.CN(914) := 0;
    PCK_NOMINA.CN(915) := MI_APLICADAS + MI_ANTICIPOS;
    PCK_NOMINA.CN(901) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(514)) / 12, 0);
    IF PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 7 THEN
      PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'));
    ELSE
      PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1);
    END IF;
  ELSE

    IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FTP', 'NO') = 'SI' THEN
      MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0));
      IF (PCK_NOMINA.FC_CN(94) + PCK_NOMINA.FC_CN(347) + PCK_NOMINA.FC_CN(35) + PCK_NOMINA.FC_CN(336)) > 0 AND PCK_NOMINA.GL_PROMCONSOLIDADASCN < PCK_NOMINA.FC_CN(1) THEN
        MI_PROMFAC := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT;
        
        MI_MSG(1).CLAVE := 'NOMEMPLEADO';
        MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
        MI_MSG(2).CLAVE := 'CEDULA';
        MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
        MI_MSG(3).CLAVE := 'TIPO';
        MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

        PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_ALERINGMENORSMB_FNA
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );

      END IF;
    ELSE
      IF PCK_NOMINA.GL_SPER = 1 THEN
        PCK_NOMINA.CN(969) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + MI_PS + MI_PVFNA + MI_PVFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0));
        MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + MI_PS + MI_PVFNA + MI_PVFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0));
      ELSE
        MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PVFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0));
      END IF;
    END IF;
    IF PCK_NOMINA.GL_SPER = 7 THEN
      MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + MI_PS + MI_PVFNA + MI_PVFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0);
    END IF;
    IF PCK_NOMINA.GL_SPER = 1 THEN
      PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(1)  ;
    ELSE
      PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(506) + PCK_NOMINA.FC_CN(515) ;
    END IF;
    PCK_NOMINA.CN(904) := PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CN(524);
    PCK_NOMINA.CN(903) := PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CN(525);
    IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FTP', 'NO') = 'SI' THEN
      PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0) ;

      MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
      MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0)  ;
      IF (PCK_NOMINA.FC_CN(94) + PCK_NOMINA.FC_CN(347) + PCK_NOMINA.FC_CN(35) + PCK_NOMINA.FC_CN(336)) > 0 AND PCK_NOMINA.GL_PROMCONSOLIDADASCN < PCK_NOMINA.FC_CN(1) THEN
        MI_PROMFAC := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT;
        MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
        --ALERTA 'EL EMPLEADO ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES || ', INGRESOS MENORES AL SMB. SE AJUSTARA BASE PARA APORTES FNA. ' || ', CEDULA NO. --CEDULA-- ,TIPO: --TIPO--

        MI_MSG(1).CLAVE := 'NOMEMPLEADO';
        MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
        MI_MSG(2).CLAVE := 'CEDULA';
        MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
        MI_MSG(3).CLAVE := 'TIPO';
        MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

        PCK_NOMINA_COM7.PR_ALERTA
            (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
            ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_ALERINGMENORSMB_FNA
            ,UN_REEMPLAZOS   => MI_MSG
            ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
            ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
            ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
            ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
            ,UN_USER         => PCK_CONEXION.FC_GETUSER
            );

      END IF;
      IF PCK_NOMINA.GL_SPER = 7 THEN
        MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + MI_PS + MI_PVFNA + MI_PVFNA + PCK_NOMINA.GL_VPA, 0);
        MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + MI_PS + MI_PVFNA + MI_PVFNA + PCK_NOMINA.GL_VPA, 0);
      END IF;
    ELSE
      IF PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3 THEN
        IF PCK_NOMINA.GL_PROMCONSOLIDADASCN < PCK_NOMINA.FC_CN(1) AND PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FTP', 'NO') = 'SI' THEN
          PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_NOMINA.FC_CN(1);
        ELSE
          PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0 AND PCK_NOMINA.FC_CN(11) > 15, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA) ;
        END IF;

        IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FTP', 'NO') = 'SI' THEN
          PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PVFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0);
          MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0));
        ELSE
          MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PVFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0));
        END IF;
        MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
        MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PVFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0);
      ELSE
        PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + MI_PS + MI_PVFNA + MI_PVFNA + PCK_NOMINA.GL_VPA, 0);
        MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
        MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PVFNA + PCK_NOMINA.GL_VPA, 0);
      END IF;
    END IF;
    IF PCK_NOMINA.GL_SPER = 4 THEN
      PCK_NOMINA_CALCULO.PR_SUMACESANTIASCONSOLIDADASCN;
      MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0));
      PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(1);
      PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
      PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
    END IF;

    PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA,913, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) = 0 THEN
      IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FTP', 'NO') = 'SI' AND PCK_NOMINA.GL_SPER <> 7 THEN
        PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC) - PCK_NOMINA.FC_CNP(913), 0) ;
        PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC / 12) - PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))), 0);
      ELSE
        IF PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3 THEN
          PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC) - PCK_NOMINA.FC_CNP(913), 0) ;
        ELSE
          PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0) ;
        END IF;

        MI_DIASCES_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
        IF PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) <> 0 THEN
          MI_PROM_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'V', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
          MI_ANTIC_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'R', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
          MI_DIAS := MI_DIAS - PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
          IF MI_DIAS < 0 THEN
            MI_DIAS := 0;
          END IF;
        ELSE
          MI_PROM_ENC := 0;
          MI_ANTIC_ENC := 0;
        END IF;
        PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC / 360 * MI_DIAS) + PCK_SYSMAN_UTL.FC_IIF(MI_DIASCES_ENC > 0, MI_ANTIC_ENC, 0) - MI_APLICADAS - MI_ANTICIPOS, 0);
        IF PCK_NOMINA.GL_SMES < 12 OR (PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.GL_SPER = 3) THEN
          PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) < 0, 0, PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))));
        END IF;
      END IF;
    ELSE
      PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FNA', '0')))) * 12, 0) ;
    END IF;

    PCK_NOMINA.CN(901) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(150) / 12, 0);
    PCK_NOMINA.CN(902) := MI_HEFNA ;
    PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(504)) / 12, 0);
    PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(543)) / 12, 0);
    IF PCK_NOMINA.GL_SMES = 6 AND PCK_NOMINA.GL_SANO = 2010 THEN
      PCK_NOMINA.CN(907) := 0 ;
    ELSE
      PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0);
    END IF;
    PCK_NOMINA.CN(908) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(909) := 0;
    PCK_NOMINA.CN(910) := PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CN(11) ;
    PCK_NOMINA.CN(911) := MI_ANTICIPOS ;
    PCK_NOMINA.CN(912) := PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) ;
    PCK_NOMINA.CN(914) := 0 ;
    IF PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 7 THEN
      PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'));
      PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) := 0;
      PCK_NOMINA.CN(913) := 0;
    ELSE
      PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1);
    END IF;
  END IF;
END PR_CALCESANTIASFTPITACACIAS;

PROCEDURE PR_CALCESANTIASITTACACIAS
(
    /*
    NAME              : PR_CALCESANTIASITTACACIAS ->  En access = calcularcesantiasALCTOCANCIPA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 28/08/2025
    TIME              : 4:00 PM
    SOURCE MODULE     : NOMINA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  CALCULARCESANTIASITTACACIAS
    */
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_DIASCES_ENC          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PROM_ENC             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_ANTIC_ENC            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PROMFAC              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIAS                 PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASINT              PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASPROMEDIO         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_ANTICIPOS            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CESANTIA1            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_FECHA                DATE;
    MI_CONT                 NUMBER(1);
    MI_CNP150               PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CNP155               PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CNP158               PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CNP160               PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN

    PCK_NOMINA.GL_BASCES := 0;
    MI_FECHA := PCK_NOMINA.GL_FECHAINI;
    IF NOT(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO < PCK_NOMINA.GL_FECHAINI) OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN --(MZANGUNA:17/12/2018)-Se agrega condicion si la fecha de retiro es nula
        PCK_NOMINA.GL_SBM := CASE WHEN PCK_NOMINA.FC_CN(900) = 0 THEN PCK_NOMINA.FC_CN(1) ELSE PCK_NOMINA.FC_CN(900) END;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN1) THEN
                PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
                PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
                PCK_NOMINA.GL_PERA := CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) < 16 THEN 1 ELSE 2 END;
                MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFIN1);
            ELSE
                PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
                PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
                PCK_NOMINA.GL_PERA := CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN)) < 16 THEN 1 ELSE 2 END;
                MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN), PCK_NOMINA.GL_FECHAFIN1);
            END IF;

            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES - 1, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_COMISIONES := (PCK_NOMINA.FC_CNA(188) / MI_DIASPROMEDIO) * 30;
            PCK_NOMINA.CN(971) := PCK_NOMINA.GL_COMISIONES;
        END IF;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
            PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM + PCK_NOMINA.GL_COMISIONES;
        ELSE
            PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM;
        END IF;
        PCK_NOMINA.GL_BONPAGADA := 0;

        IF PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996', 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
            PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1);
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_FECHAIC := PCK_NOMINA.GL_FECHAIR;
        ELSE
            PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
            PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > PCK_NOMINA.GL_FECHAIC AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA ELSE PCK_NOMINA.GL_FECHAIC END;

            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIC) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
            PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1 - 10);
        END IF;
        MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
        IF PCK_NOMINA.FC_CNA(155) = 0 THEN
          PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0);
        ELSE
            IF PCK_NOMINA.FC_CNA(164) > 1 THEN
              PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0)/PCK_NOMINA.FC_CNA(164);
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501);
            END IF;
        END IF;
        PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0) ;
        PCK_NOMINA.GL_BONPAGADA := CASE WHEN PCK_NOMINA.GL_BONPAGADA > 0 THEN PCK_NOMINA.GL_BONPAGADA ELSE PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538)) / 12, 0) END;
        IF PCK_NOMINA.GL_SPER <> 7 THEN 
          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        ELSE 
          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO - 1 , PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        END IF;        
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 160, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        MI_CNP160 := PCK_NOMINA.FC_CNP(160);
        PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 503, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.CN(906) := CASE WHEN MI_CNP160 > 0 THEN PCK_SYSMAN_UTL.FC_ROUND((MI_CNP160 + PCK_NOMINA.FC_CNP(503))/ 12, 0) ELSE PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0) END;
        PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);       
        PCK_NOMINA.CN(902) := CASE WHEN PCK_NOMINA.FC_CN(902) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_SUMACONA(49,60) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(519)) / 12, 0) ELSE PCK_NOMINA.FC_CN(902) END ;
        PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 158, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        MI_CNP158 := PCK_NOMINA.FC_CNP(158);
        PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 504, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.CN(905) := CASE WHEN MI_CNP158 > 0 THEN PCK_SYSMAN_UTL.FC_ROUND((MI_CNP158 + PCK_NOMINA.FC_CNP(504))/ 12, 0) ELSE PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(504)) / 12, 0) END;

        MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA
                    + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_AUXT + PCK_NOMINA.FC_CN(906) + PCK_NOMINA.FC_CN(905) + PCK_NOMINA.FC_CN(907) + PCK_NOMINA.FC_CN(902) + PCK_NOMINA.GL_BONPAGADA END;
        MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC, 0);
        PCK_NOMINA.CN(969) := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC, 0);
        MI_DIASCES_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
        IF PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) <> 0 THEN
            MI_PROM_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'V', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
            MI_ANTIC_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'R', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
            MI_DIAS := MI_DIAS - PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
            IF MI_DIAS < 0 THEN
                MI_DIAS := 0;
            END IF;
        ELSE
            MI_PROM_ENC := 0;
            MI_ANTIC_ENC := 0;
        END IF;
        IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHACS, PCK_NOMINA.GL_FECHAFIN1) <= 90 AND PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) < 360 THEN
            MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND((((MI_PROMFAC)) * MI_DIAS / 360), 0) + CASE WHEN MI_DIASCES_ENC > 0 THEN MI_ANTIC_ENC ELSE 0 END - MI_ANTICIPOS;
        ELSE
            IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) < 360 THEN
                MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND((((MI_PROMFAC)) * MI_DIAS / 360), 0) + CASE WHEN MI_DIASCES_ENC > 0 THEN MI_ANTIC_ENC ELSE 0 END - MI_ANTICIPOS;
            ELSE
                MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)) * MI_DIAS / 360, 0) + CASE WHEN MI_DIASCES_ENC > 0 THEN MI_ANTIC_ENC ELSE 0 END - MI_ANTICIPOS;
            END IF;
        END IF;
        IF PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.FC_CN(411) <> 0 THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 2 THEN
                PCK_NOMINA.CN(169) := CASE WHEN PCK_NOMINA.FC_CN(169) = 0 THEN CASE WHEN MI_CESANTIA1 < 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) END ELSE PCK_NOMINA.FC_CN(169) END;
            END IF;
            PCK_NOMINA.CN(177) := CASE WHEN PCK_NOMINA.FC_CN(177) = 0 THEN MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(177) END;
        ELSIF PCK_NOMINA.FC_CN(412) <> 0 THEN          
            MI_CONT := 0;
            IF ( PCK_NOMINA.GL_SMES = 12 ) THEN
                BEGIN
                    SELECT  DISTINCT 1
                    INTO    MI_CONT
                    FROM    NOVEDADES
                    WHERE   COMPANIA = PCK_NOMINA.GL_COMPANIA
                            AND ID_DE_PROCESO = PCK_NOMINA.GL_SPRC
                            AND ANO = PCK_NOMINA.GL_SANO
                            AND MES = PCK_NOMINA.GL_SMES
                            AND PERIODO = 7
                            AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                            AND ID_DE_CONCEPTO = 404
                            AND VALOR != 0;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CONT := 0;
                END;
            END IF;
            IF ( MI_CONT = 0 ) THEN
              IF NOT(PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996', 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1) THEN
                  PCK_NOMINA.CN(269) := CASE WHEN PCK_NOMINA.FC_CN(269) = 0 THEN CASE WHEN MI_CESANTIA1 < 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) END ELSE PCK_NOMINA.FC_CN(269) END;
              END IF;
              PCK_NOMINA.CN(277) := CASE WHEN PCK_NOMINA.FC_CN(277) = 0 THEN MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(277) END;
              IF PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = '12' THEN
                  PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA, 1, (PCK_NOMINA.GL_SANO + 1), 1, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 169, PCK_NOMINA.FC_CN(269));
              END IF;
      END IF;       
        END IF;

        PCK_NOMINA.CN(900) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
        PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
        PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
        PCK_NOMINA.CN(901) := PCK_NOMINA.GL_GRPNGV ;
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.CN(910) := MI_DIAS;
        PCK_NOMINA.CN(911) := MI_ANTICIPOS;
        PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
        PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0);
        PCK_NOMINA.CN(914) := PCK_NOMINA.GL_VPT ;
        PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1) + 1 ;
    END IF;

END PR_CALCESANTIASITTACACIAS;

FUNCTION FC_CARGAR_BASESNOVEDADES  
/*
    NAME              : FC_CARGAR_BASESNOVEDADES
    AUTHOR MIGRACION  :
    DATE MIGRADOR     : 05/08/2025
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PROCEDIMIENTO QUE CARGA LAS BASES NOVEDADES
    --NAME:    cargarBasesNovedades
    --METHOD:  POST
    */
(
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES          IN PCK_SUBTIPOS.TI_MES,
  UN_CADENA       IN CLOB,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
)RETURN CLOB
AS
MI_DATOS_FILA           PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS       PCK_SYSMAN_UTL.T_SPLIT;
MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
MI_T_REGISTRO           PCK_SUBTIPOS.TI_VALORES;
MI_SECUENCIA            PCK_SUBTIPOS.TI_VALORES;
MI_T_DOCUMENTO          PCK_SUBTIPOS.TI_VALORES;
MI_N_DOCUMENTO          PCK_SUBTIPOS.TI_VALORES;
MI_T_COTIZANTE          PCK_SUBTIPOS.TI_VALORES;
MI_S_COTIZANTE          PCK_SUBTIPOS.TI_VALORES;
MI_EXTRANJERO           PCK_SUBTIPOS.TI_VALORES;
MI_RE_COLOMBIANO        PCK_SUBTIPOS.TI_VALORES;
MI_DEPARTAMENTO_UL      PCK_SUBTIPOS.TI_VALORES;
MI_MUNICIPIO_UL         PCK_SUBTIPOS.TI_VALORES;
MI_P_APELLIDO           PCK_SUBTIPOS.TI_VALORES;
MI_S_APELLIDO           PCK_SUBTIPOS.TI_VALORES;
MI_P_NOMBRE             PCK_SUBTIPOS.TI_VALORES;
MI_S_NOMBRE             PCK_SUBTIPOS.TI_VALORES;
MI_ING                  PCK_SUBTIPOS.TI_VALORES;
MI_RET                  PCK_SUBTIPOS.TI_VALORES;
MI_TDE                  PCK_SUBTIPOS.TI_VALORES;
MI_TAE                  PCK_SUBTIPOS.TI_VALORES;
MI_TDP                  PCK_SUBTIPOS.TI_VALORES;
MI_TAP                  PCK_SUBTIPOS.TI_VALORES;
MI_VSP                  PCK_SUBTIPOS.TI_VALORES;
MI_CORRECIONES          PCK_SUBTIPOS.TI_VALORES;
MI_VST                  PCK_SUBTIPOS.TI_VALORES;
MI_SLN                  PCK_SUBTIPOS.TI_VALORES;
MI_IGE                  PCK_SUBTIPOS.TI_VALORES;
MI_LMA                  PCK_SUBTIPOS.TI_VALORES;
MI_VAC                  PCK_SUBTIPOS.TI_VALORES;
MI_AVP                  PCK_SUBTIPOS.TI_VALORES;
MI_VCT                  PCK_SUBTIPOS.TI_VALORES;
MI_IRL                  PCK_SUBTIPOS.TI_VALORES;
MI_PENSION_PERT         PCK_SUBTIPOS.TI_VALORES;
MI_PENSION_TRALS        PCK_SUBTIPOS.TI_VALORES;
MI_EPS_PERT             PCK_SUBTIPOS.TI_VALORES;
MI_EPS_TRASL            PCK_SUBTIPOS.TI_VALORES;
MI_CCF                  PCK_SUBTIPOS.TI_VALORES;
MI_DIAS_PENSION         PCK_SUBTIPOS.TI_VALORES;
MI_DIAS_SALUD           PCK_SUBTIPOS.TI_VALORES;
MI_DIAS_RIESGO          PCK_SUBTIPOS.TI_VALORES;
MI_DIAS_C_COMPENSACION  PCK_SUBTIPOS.TI_VALORES;
MI_SALARIO_BASICO       PCK_SUBTIPOS.TI_VALORES;
MI_SALARIO_INTEGRAL     PCK_SUBTIPOS.TI_VALORES;
MI_IBC_PENSION          PCK_SUBTIPOS.TI_VALORES;
MI_IBC_SALUD            PCK_SUBTIPOS.TI_VALORES;
MI_IBC_RIESGO           PCK_SUBTIPOS.TI_VALORES;
MI_IBC_CCF              PCK_SUBTIPOS.TI_VALORES;
MI_TARIFA_PENSION       PCK_SUBTIPOS.TI_DOBLE;
MI_COTIZACION_OBL_P     PCK_SUBTIPOS.TI_VALORES;
MI_AVAFPO               PCK_SUBTIPOS.TI_VALORES;
MI_CVAFPO               PCK_SUBTIPOS.TI_VALORES;
MI_TOTAL_COTIZACION     PCK_SUBTIPOS.TI_VALORES;
MI_AFSP                 PCK_SUBTIPOS.TI_VALORES;
MI_AFSP1                PCK_SUBTIPOS.TI_VALORES;
MI_VNRPV                PCK_SUBTIPOS.TI_VALORES;
MI_TARIFA_SALUD         PCK_SUBTIPOS.TI_DOBLE;
MI_COTIZACION_OBL_S     PCK_SUBTIPOS.TI_VALORES;
MI_VALOR_UPC            PCK_SUBTIPOS.TI_VALORES;
MI_NAIEG                PCK_SUBTIPOS.TI_VALORES;
MI_VAIEG                PCK_SUBTIPOS.TI_VALORES;
MI_LICENCIA_MATER       PCK_SUBTIPOS.TI_VALORES;
MI_VLR_LICENCIA_MATER   PCK_SUBTIPOS.TI_VALORES;
MI_TARIFA_RIEGOS        PCK_SUBTIPOS.TI_VALORES;
MI_CENTRO_TRABAJO       PCK_SUBTIPOS.TI_VALORES;
MI_COSRL                PCK_SUBTIPOS.TI_VALORES;
MI_TARIFA_CCF           PCK_SUBTIPOS.TI_VALORES;
MI_VALOR_CCF            PCK_SUBTIPOS.TI_VALORES;
MI_TARIFA_SENA          PCK_SUBTIPOS.TI_VALORES;
MI_VALOR_SENA           PCK_SUBTIPOS.TI_VALORES;
MI_TARIFA_ICBF          PCK_SUBTIPOS.TI_VALORES;
MI_VALOR_ICBF           PCK_SUBTIPOS.TI_VALORES;
MI_TARIFA_ESAP          PCK_SUBTIPOS.TI_VALORES;
MI_VALOR_ESAP           PCK_SUBTIPOS.TI_VALORES;
MI_TARIFA_MEN           PCK_SUBTIPOS.TI_VALORES;
MI_VALOR_MEN            PCK_SUBTIPOS.TI_VALORES;
MI_T_IDEN_COTI          PCK_SUBTIPOS.TI_VALORES;
MI_N_IDEN_COTI          PCK_SUBTIPOS.TI_VALORES;
MI_CEPAPS               PCK_SUBTIPOS.TI_VALORES;
MI_CARLPA               PCK_SUBTIPOS.TI_VALORES;
MI_CLASE_RIESGO         PCK_SUBTIPOS.TI_VALORES;
MI_TARIFA_ESP_PENS      PCK_SUBTIPOS.TI_VALORES;
MI_FECHA_INGRESO        PCK_SUBTIPOS.TI_VALORES;
MI_FECHA_RETIRO         PCK_SUBTIPOS.TI_VALORES;
MI_FECHA_INICIO_VSP     PCK_SUBTIPOS.TI_VALORES;
MI_FECHA_INI_SLN        PCK_SUBTIPOS.TI_VALORES;
MI_FECHA_FIN_SLN        PCK_SUBTIPOS.TI_VALORES;
MI_FECHA_INI_IGE        PCK_SUBTIPOS.TI_VALORES;
MI_FECHA_FIN_IGE        PCK_SUBTIPOS.TI_VALORES;
MI_FECHA_INI_LMA        PCK_SUBTIPOS.TI_VALORES;
MI_FECHA_FIN_LMA        PCK_SUBTIPOS.TI_VALORES;
MI_FECHA_INI_VAC        PCK_SUBTIPOS.TI_VALORES;
MI_FECHA_FIN_VAC        PCK_SUBTIPOS.TI_VALORES;
MI_FECHA_INI_VCT        PCK_SUBTIPOS.TI_VALORES;
MI_FECHA_FIN_VCT        PCK_SUBTIPOS.TI_VALORES;
MI_FECHA_INI_IRL        PCK_SUBTIPOS.TI_VALORES;
MI_FECHA_FIN_IRL        PCK_SUBTIPOS.TI_VALORES;
MI_IBC                  PCK_SUBTIPOS.TI_VALORES;
MI_HORAS_LAB            PCK_SUBTIPOS.TI_VALORES;
MI_FECHA_RADICACION     PCK_SUBTIPOS.TI_VALORES;

MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
MI_MERGEUSING            PCK_SUBTIPOS.TI_MERGEUSING;
MI_MERGEENLACE           PCK_SUBTIPOS.TI_MERGEENLACE;
MI_MERGEEXISTE           PCK_SUBTIPOS.TI_MERGEEXISTE;
MI_MERGENOEXIS           PCK_SUBTIPOS.TI_MERGENOEXISTE;
MI_RTA                   PCK_SUBTIPOS.TI_RTA_ACME;
MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;

MI_ID_EMPLEADO          PERSONAL.ID_DE_EMPLEADO%TYPE;
MI_LLAVE                VARCHAR2(100 CHAR);
MI_CONSECUTIVO          PCK_SUBTIPOS.TI_ENTERO;

MI_TIPONOVEDAD          PCK_SUBTIPOS.TI_ENTERO;
MI_PORC_EMPLEADO_EPS    PCK_SUBTIPOS.TI_ENTERO; 
MI_PORC_EMPLEADO_AFP    PCK_SUBTIPOS.TI_ENTERO;
MI_APORTEEMPLEADOSALUD   PCK_SUBTIPOS.TI_ENTERO;
MI_APORTEEMPLEADOPENSION PCK_SUBTIPOS.TI_ENTERO;
MI_PORC_PATRON_AFP       PCK_SUBTIPOS.TI_ENTERO;
MI_PORC_PATRON_EPS       PCK_SUBTIPOS.TI_ENTERO;
MI_APORTEPATRONALSALUD   PCK_SUBTIPOS.TI_ENTERO;
MI_APORTEPATRONALPENSION PCK_SUBTIPOS.TI_ENTERO;

MI_ERRORES               PCK_SUBTIPOS.TI_ENTERO:=0;
MI_MENSAJE               CLOB:='';
MI_CUENTA           PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
BEGIN

    PCK_NOMINA.GL_SPER := 3;
    PCK_NOMINA.GL_PROCESOACTUAL := 1;
    BEGIN 
     SELECT PORC_EMPLEADO_EPS, PORC_EMPLEADO_AFP, PORC_PATRON_AFP, PORC_PATRON_EPS
     INTO  MI_PORC_EMPLEADO_EPS, MI_PORC_EMPLEADO_AFP,
     MI_PORC_PATRON_AFP, MI_PORC_PATRON_EPS
     FROM PARAMETROS_DE_ENTRADA 
     WHERE COMPANIA = UN_COMPANIA; 
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_MENSAJE := '';
        MI_MENSAJE := MI_MENSAJE || '*** INFORME DE CONFIGURACIÓN FALTANTE ***' || CHR(10) || CHR(13);
        MI_MENSAJE := MI_MENSAJE || 'No se encuentran configurados los porcentajes de salud y pensión para la compañía ''' || UN_COMPANIA || '''.' || CHR(10) || CHR(13);
        MI_MENSAJE := MI_MENSAJE || 'Verifique la tabla PARAMETROS_DE_ENTRADA.' || CHR(10) || CHR(13);
        MI_ERRORES := MI_ERRORES + 1;
    END;
      MI_MENSAJE:= MI_MENSAJE||'*** INFORME DE EMPLEADOS QUE NO ESTAN REGISTRADOS ***';
      MI_MENSAJE:= MI_MENSAJE||CHR(10)||CHR(13);
      MI_MENSAJE:= MI_MENSAJE||CHR(10)||CHR(13);
      MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENA,
                                                   UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
                                                  

    FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST
       LOOP
          IF RS = 1 THEN 
            MI_T_REGISTRO := SUBSTR(MI_DATOS_FILA(RS),1,2);
          ELSE
            MI_T_REGISTRO := SUBSTR(MI_DATOS_FILA(RS),1,2);
            MI_SECUENCIA := SUBSTR(MI_DATOS_FILA(RS),3,5);
            MI_T_DOCUMENTO := SUBSTR(MI_DATOS_FILA(RS), 8, 2);
            MI_N_DOCUMENTO := TRIM(SUBSTR(MI_DATOS_FILA(RS), 10, 16));
            MI_T_COTIZANTE := SUBSTR(MI_DATOS_FILA(RS), 26, 2);
            MI_S_COTIZANTE := SUBSTR(MI_DATOS_FILA(RS), 28, 2);
            MI_EXTRANJERO := SUBSTR(MI_DATOS_FILA(RS), 30, 1);
            MI_RE_COLOMBIANO := SUBSTR(MI_DATOS_FILA(RS), 31, 1);
            MI_DEPARTAMENTO_UL := SUBSTR(MI_DATOS_FILA(RS), 32, 2);
            MI_MUNICIPIO_UL := SUBSTR(MI_DATOS_FILA(RS), 34, 3);
            MI_P_APELLIDO := SUBSTR(MI_DATOS_FILA(RS), 37, 20);
            MI_S_APELLIDO := SUBSTR(MI_DATOS_FILA(RS), 57, 30);
            MI_P_NOMBRE := SUBSTR(MI_DATOS_FILA(RS), 87, 20);
            MI_S_NOMBRE := SUBSTR(MI_DATOS_FILA(RS), 107, 30);
            MI_ING := SUBSTR(MI_DATOS_FILA(RS), 137, 1);
            MI_RET := SUBSTR(MI_DATOS_FILA(RS), 138, 1);
            MI_TDE := SUBSTR(MI_DATOS_FILA(RS), 139, 1);
            MI_TAE := SUBSTR(MI_DATOS_FILA(RS), 140, 1);
            MI_TDP := SUBSTR(MI_DATOS_FILA(RS), 141, 1);
            MI_TAP := SUBSTR(MI_DATOS_FILA(RS), 142, 1);
            MI_VSP := SUBSTR(MI_DATOS_FILA(RS), 143, 1);
            MI_CORRECIONES := SUBSTR(MI_DATOS_FILA(RS), 144, 1);
            MI_VST := SUBSTR(MI_DATOS_FILA(RS), 145, 1);
            MI_SLN := SUBSTR(MI_DATOS_FILA(RS), 146, 1);
            MI_IGE := SUBSTR(MI_DATOS_FILA(RS), 147, 1);
            MI_LMA := SUBSTR(MI_DATOS_FILA(RS), 148, 1);
            MI_VAC := SUBSTR(MI_DATOS_FILA(RS), 149, 1);
            MI_AVP := SUBSTR(MI_DATOS_FILA(RS), 150, 1);
            MI_VCT := SUBSTR(MI_DATOS_FILA(RS), 151, 1);
            MI_IRL := SUBSTR(MI_DATOS_FILA(RS), 152, 2);
            MI_PENSION_PERT := SUBSTR(MI_DATOS_FILA(RS), 154, 6);
            MI_PENSION_TRALS := SUBSTR(MI_DATOS_FILA(RS), 160, 6);
            MI_EPS_PERT := SUBSTR(MI_DATOS_FILA(RS), 166, 6);
            MI_EPS_TRASL := SUBSTR(MI_DATOS_FILA(RS), 172, 6);
            MI_CCF := SUBSTR(MI_DATOS_FILA(RS), 178, 6);
            MI_DIAS_PENSION := SUBSTR(MI_DATOS_FILA(RS), 184, 2);
            MI_DIAS_SALUD := SUBSTR(MI_DATOS_FILA(RS), 186, 2);
            MI_DIAS_RIESGO := SUBSTR(MI_DATOS_FILA(RS), 188, 2);
            MI_DIAS_C_COMPENSACION := SUBSTR(MI_DATOS_FILA(RS), 190, 2);
            MI_SALARIO_BASICO := SUBSTR(MI_DATOS_FILA(RS), 192, 9);
            MI_SALARIO_INTEGRAL := SUBSTR(MI_DATOS_FILA(RS), 201, 1);
            MI_IBC_PENSION := SUBSTR(MI_DATOS_FILA(RS), 202, 9);
            MI_IBC_SALUD := SUBSTR(MI_DATOS_FILA(RS), 211, 9);
            MI_IBC_RIESGO := SUBSTR(MI_DATOS_FILA(RS), 220, 9);
            MI_IBC_CCF := SUBSTR(MI_DATOS_FILA(RS), 229, 9);
            MI_TARIFA_PENSION := (TRIM(SUBSTR(MI_DATOS_FILA(RS), 238, 7)) * 100);
            MI_COTIZACION_OBL_P := SUBSTR(MI_DATOS_FILA(RS), 245, 9);
            MI_AVAFPO := SUBSTR(MI_DATOS_FILA(RS), 254, 9);
            MI_CVAFPO := SUBSTR(MI_DATOS_FILA(RS), 263, 9);
            MI_TOTAL_COTIZACION := SUBSTR(MI_DATOS_FILA(RS), 272, 9);
            MI_AFSP := SUBSTR(MI_DATOS_FILA(RS), 281, 9);
            MI_AFSP1 := SUBSTR(MI_DATOS_FILA(RS), 290, 9);
            MI_VNRPV := SUBSTR(MI_DATOS_FILA(RS), 299, 9);
            MI_TARIFA_SALUD := (TRIM(SUBSTR(MI_DATOS_FILA(RS), 308, 7)) * 100);
            MI_COTIZACION_OBL_S := SUBSTR(MI_DATOS_FILA(RS), 315, 9);
            MI_VALOR_UPC := SUBSTR(MI_DATOS_FILA(RS), 324, 9);
            MI_NAIEG := SUBSTR(MI_DATOS_FILA(RS), 333, 15);
            MI_VAIEG := SUBSTR(MI_DATOS_FILA(RS), 348, 9);
            MI_LICENCIA_MATER := SUBSTR(MI_DATOS_FILA(RS), 357, 15);
            MI_VLR_LICENCIA_MATER := SUBSTR(MI_DATOS_FILA(RS), 372, 9);
            MI_TARIFA_RIEGOS := SUBSTR(MI_DATOS_FILA(RS), 381, 9);
            MI_CENTRO_TRABAJO := SUBSTR(MI_DATOS_FILA(RS), 390, 9);
            MI_COSRL := SUBSTR(MI_DATOS_FILA(RS), 399, 9);
            MI_TARIFA_CCF := SUBSTR(MI_DATOS_FILA(RS), 408, 7);
            MI_VALOR_CCF := SUBSTR(MI_DATOS_FILA(RS), 415, 9);
            MI_TARIFA_SENA := SUBSTR(MI_DATOS_FILA(RS), 424, 7);
            MI_VALOR_SENA := SUBSTR(MI_DATOS_FILA(RS), 431, 9);
            MI_TARIFA_ICBF := SUBSTR(MI_DATOS_FILA(RS), 440, 7);
            MI_VALOR_ICBF := SUBSTR(MI_DATOS_FILA(RS), 447, 9);
            MI_TARIFA_ESAP := SUBSTR(MI_DATOS_FILA(RS), 456, 7);
            MI_VALOR_ESAP := SUBSTR(MI_DATOS_FILA(RS), 463, 9);
            MI_TARIFA_MEN := SUBSTR(MI_DATOS_FILA(RS), 472, 7);
            MI_VALOR_MEN := SUBSTR(MI_DATOS_FILA(RS), 479, 9);
            MI_T_IDEN_COTI := SUBSTR(MI_DATOS_FILA(RS), 488, 2);
            MI_N_IDEN_COTI := SUBSTR(MI_DATOS_FILA(RS), 490, 16);
            MI_CEPAPS := SUBSTR(MI_DATOS_FILA(RS), 506, 1);
            MI_CARLPA := SUBSTR(MI_DATOS_FILA(RS), 507, 6);
            MI_CLASE_RIESGO := SUBSTR(MI_DATOS_FILA(RS), 513, 1);
            MI_TARIFA_ESP_PENS := SUBSTR(MI_DATOS_FILA(RS), 514, 1);
            MI_FECHA_INGRESO := TRIM(SUBSTR(MI_DATOS_FILA(RS), 515, 10));
            MI_FECHA_RETIRO := TRIM(SUBSTR(MI_DATOS_FILA(RS), 525, 10));
            MI_FECHA_INICIO_VSP := TRIM(SUBSTR(MI_DATOS_FILA(RS), 535, 10));
            MI_FECHA_INI_SLN := TRIM(SUBSTR(MI_DATOS_FILA(RS), 545, 10));
            MI_FECHA_FIN_SLN := TRIM(SUBSTR(MI_DATOS_FILA(RS), 555, 10));
            MI_FECHA_INI_IGE := TRIM(SUBSTR(MI_DATOS_FILA(RS), 565, 10));
            MI_FECHA_FIN_IGE := TRIM(SUBSTR(MI_DATOS_FILA(RS), 575, 10));
            MI_FECHA_INI_LMA := TRIM(SUBSTR(MI_DATOS_FILA(RS), 585, 10));
            MI_FECHA_FIN_LMA := TRIM(SUBSTR(MI_DATOS_FILA(RS), 595, 10));
            MI_FECHA_INI_VAC := TRIM(SUBSTR(MI_DATOS_FILA(RS), 605, 10));
            MI_FECHA_FIN_VAC := TRIM(SUBSTR(MI_DATOS_FILA(RS), 615, 10));
            MI_FECHA_INI_VCT := TRIM(SUBSTR(MI_DATOS_FILA(RS), 625, 10));
            MI_FECHA_FIN_VCT := TRIM(SUBSTR(MI_DATOS_FILA(RS), 635, 10));
            MI_FECHA_INI_IRL := TRIM(SUBSTR(MI_DATOS_FILA(RS), 645, 10));
            MI_FECHA_FIN_IRL := TRIM(SUBSTR(MI_DATOS_FILA(RS), 655, 10));
            MI_IBC := SUBSTR(MI_DATOS_FILA(RS), 665, 9);
            MI_HORAS_LAB := SUBSTR(MI_DATOS_FILA(RS), 674, 3);
            MI_FECHA_RADICACION := SUBSTR(MI_DATOS_FILA(RS), 677, 10);
            
            MI_TIPONOVEDAD := CASE WHEN MI_VAC = 'X' THEN 1 ELSE
                              CASE WHEN MI_IGE = 'X' THEN 2 ELSE 4 END END;
            BEGIN
                SELECT ID_DE_EMPLEADO
                INTO MI_ID_EMPLEADO
                FROM PERSONAL
                WHERE COMPANIA = UN_COMPANIA
                  AND NUMERO_DCTO = MI_N_DOCUMENTO
                  AND (ESTADO_ACTUAL = 1
                        OR FECHA_DE_RETIRO >= TO_DATE('01/' || LPAD(UN_MES, 2, '0') || '/' || UN_ANIO, 'DD/MM/YYYY'));
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_MENSAJE:= MI_MENSAJE|| 'El empleado con tipo de documento "' || MI_T_DOCUMENTO || '" y número "' || MI_N_DOCUMENTO || '", ' || '(' || TRIM(MI_P_APELLIDO) || ' ' || TRIM(MI_S_APELLIDO) || ' ' || TRIM(MI_P_NOMBRE) || ' ' || TRIM(MI_S_NOMBRE) || ') ' ||
                                          'no se encuentra registrado, no tiene estado "Activo", o su fecha de retiro es posterior al periodo ejecutado (' || UN_MES || '/' || UN_ANIO || ').' || CHR(10);
                MI_ERRORES:= MI_ERRORES+1;
                MI_ID_EMPLEADO := 0;
                CONTINUE;
            END;
            
            MI_APORTEEMPLEADOSALUD := PCK_SYSMAN_UTL.FC_ROUND_100((MI_COTIZACION_OBL_S * MI_PORC_EMPLEADO_EPS) / MI_TARIFA_SALUD, 'SI',0.49 + PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990);
            MI_APORTEEMPLEADOPENSION := CASE WHEN MI_COTIZACION_OBL_P IN ('000000000') THEN '0' ELSE PCK_SYSMAN_UTL.FC_ROUND_100((MI_COTIZACION_OBL_P * MI_PORC_EMPLEADO_AFP) / MI_TARIFA_PENSION, 'SI',0.49 + PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990) END;
            MI_APORTEPATRONALSALUD := MI_COTIZACION_OBL_S - MI_APORTEEMPLEADOSALUD;
            MI_APORTEPATRONALPENSION := CASE WHEN MI_COTIZACION_OBL_P IN ('000000000') THEN '0' ELSE MI_COTIZACION_OBL_P - MI_APORTEEMPLEADOPENSION END;

            MI_LLAVE := CASE MI_TIPONOVEDAD WHEN '4' THEN UN_COMPANIA || '--' || MI_ID_EMPLEADO || '--' || UN_ANIO || '--' || UN_MES 
                                            WHEN '1' THEN UN_COMPANIA || '--' || PCK_NOMINA.GL_PROCESOACTUAL || '--' || UN_ANIO || '--' || UN_MES || '--' || PCK_NOMINA.GL_SPER || '--' || MI_ID_EMPLEADO || '--' || MI_TIPONOVEDAD || '--' || TO_CHAR(TO_DATE( MI_FECHA_INI_VAC , 'YYYY-MM-DD'), 'DD/MM/YYYY')
                                            WHEN '2' THEN UN_COMPANIA || '--' || MI_ID_EMPLEADO || '--' || PCK_NOMINA.GL_PROCESOACTUAL || '--' || '01' || '--' || UN_ANIO || '--' || UN_MES  || '--' || PCK_NOMINA.GL_SPER || '--' || MI_TIPONOVEDAD END;
            
            
            BEGIN
                SELECT CONSECUTIVO
                INTO   MI_CONSECUTIVO
                FROM   BASESNOVEDADES
                WHERE  COMPANIA = UN_COMPANIA
                  AND  LLAVENOVEDAD = MI_LLAVE
                  AND  TIPONOVEDAD = MI_TIPONOVEDAD
                  AND  ID_DE_EMPLEADO = MI_ID_EMPLEADO
                  AND  ANO = UN_ANIO
                  AND  MES = UN_MES
                  AND  PERIODO = PCK_NOMINA.GL_SPER
                  AND  MANUAL <> 0;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_CONSECUTIVO := 0;
            END;
            IF MI_CONSECUTIVO = 0 THEN --Si no existe manual
                MI_TABLA := 'BASESNOVEDADES';

                    MI_CONSECUTIVO :=  PCK_SYSMAN_UTL.FC_GENCONSECUTIVO
                        (UN_TABLA    => MI_TABLA,
                         UN_CRITERIO => 'COMPANIA = '''|| UN_COMPANIA ||'''
                                    AND  ID_DE_EMPLEADO = '|| MI_ID_EMPLEADO ||'
                                    AND  ANO = '|| UN_ANIO ||'
                                    AND  MES = '|| UN_MES ||'
                                    AND  PERIODO = '|| PCK_NOMINA.GL_SPER  ||' ',
                         UN_CAMPO    => 'CONSECUTIVO');
                END IF;
 
            MI_TABLA := 'BASESNOVEDADES';
            MI_MERGEUSING := ' SELECT 
                              '''|| UN_COMPANIA || ''' COMPANIA, 
                              '''|| MI_LLAVE || ''' LLAVENOVEDAD, 
                              '''|| MI_TIPONOVEDAD || ''' TIPONOVEDAD,
                              '''|| MI_CONSECUTIVO || ''' CONSECUTIVO, 
                              '''|| MI_ID_EMPLEADO || ''' ID_DE_EMPLEADO, 
                              '''|| UN_ANIO || ''' ANO, 
                              '''|| UN_MES  || ''' MES, 
                              '''|| PCK_NOMINA.GL_SPER || ''' PERIODO, 
                              '''|| PCK_NOMINA.GL_PROCESOACTUAL || ''' ID_DE_PROCESO,
                              CASE WHEN ''' || MI_FECHA_INGRESO || ''' IS NOT NULL THEN TO_DATE(''' || MI_FECHA_INGRESO || ''', ''YYYY-MM-DD'')  
                                   WHEN ''' || MI_FECHA_INICIO_VSP || ''' IS NOT NULL THEN TO_DATE(''' || MI_FECHA_INICIO_VSP || ''', ''YYYY-MM-DD'')
                                   WHEN ''' || MI_FECHA_INI_SLN || ''' IS NOT NULL THEN TO_DATE(''' || MI_FECHA_INI_SLN || ''', ''YYYY-MM-DD'')
                                   WHEN ''' || MI_FECHA_INI_IGE || ''' IS NOT NULL THEN TO_DATE(''' || MI_FECHA_INI_IGE || ''', ''YYYY-MM-DD'')
                                   WHEN ''' || MI_FECHA_INI_LMA || ''' IS NOT NULL THEN TO_DATE(''' || MI_FECHA_INI_LMA || ''', ''YYYY-MM-DD'')
                                   WHEN ''' || MI_FECHA_INI_VAC || ''' IS NOT NULL THEN TO_DATE(''' || MI_FECHA_INI_VAC || ''', ''YYYY-MM-DD'')
                                   WHEN ''' || MI_FECHA_INI_VCT || ''' IS NOT NULL THEN TO_DATE(''' || MI_FECHA_INI_VCT || ''', ''YYYY-MM-DD'')
                                   WHEN ''' || MI_FECHA_INI_IRL || ''' IS NOT NULL THEN TO_DATE(''' || MI_FECHA_INI_IRL || ''', ''YYYY-MM-DD'')
                                   ELSE NULL 
                              END AS FECHAINICIAL,
                              CASE WHEN ''' || MI_FECHA_RETIRO || ''' IS NOT NULL THEN TO_DATE(''' || MI_FECHA_RETIRO || ''', ''YYYY-MM-DD'')
                                   WHEN ''' || MI_FECHA_FIN_SLN || ''' IS NOT NULL THEN TO_DATE(''' || MI_FECHA_FIN_SLN || ''', ''YYYY-MM-DD'')
                                   WHEN ''' || MI_FECHA_FIN_IGE || ''' IS NOT NULL THEN TO_DATE(''' || MI_FECHA_FIN_IGE || ''', ''YYYY-MM-DD'')
                                   WHEN ''' || MI_FECHA_FIN_LMA || ''' IS NOT NULL THEN TO_DATE(''' || MI_FECHA_FIN_LMA || ''', ''YYYY-MM-DD'')
                                   WHEN ''' || MI_FECHA_FIN_VAC || ''' IS NOT NULL THEN TO_DATE(''' || MI_FECHA_FIN_VAC || ''', ''YYYY-MM-DD'')
                                   WHEN ''' || MI_FECHA_FIN_VCT || ''' IS NOT NULL THEN TO_DATE(''' || MI_FECHA_FIN_VCT || ''', ''YYYY-MM-DD'')
                                   WHEN ''' || MI_FECHA_FIN_IRL || ''' IS NOT NULL THEN TO_DATE(''' || MI_FECHA_FIN_IRL || ''', ''YYYY-MM-DD'')
                                   ELSE NULL
                              END AS FECHAFINAL,
                              '|| MI_SALARIO_BASICO || ' BASE, 
                              '|| MI_DIAS_SALUD ||' DIAS, 
                              '|| MI_IBC_PENSION ||' BASEPROPORCIONAL, 
                              ''-1'' MANUAL, 
                              SYSDATE FECHACAMBIOMANUAL, 
                              '||  MI_APORTEPATRONALSALUD ||' APORTEPATRONALSALUD, 
                              '|| MI_APORTEEMPLEADOSALUD ||' APORTEEMPLEADOSALUD, 
                              '|| MI_APORTEPATRONALPENSION ||' APORTEPATRONALPENSION, 
                              '|| MI_APORTEEMPLEADOPENSION ||' APORTEEMPLEADOPENSION, 
                              '|| MI_AFSP ||' FSP, 
                              '|| MI_AFSP1 ||' FSPSUBSISTENCIA, 
                              0 FSPADICIONAL, 
                              '''|| MI_TARIFA_PENSION ||''' PORC_PENSIONTOTAL, 
                              '''|| MI_TARIFA_SALUD ||''' PORC_SALUDTOTAL, 
                              '''|| CASE WHEN MI_AFSP > 0 THEN '0.5' ELSE 0 END ||''' PORC_FSP, 
                              '''|| CASE WHEN MI_AFSP > 0 THEN '0.5' ELSE 0 END ||''' PORC_FSP_ADICIONAL,
                              '''|| CASE WHEN MI_AFSP > 0 THEN '0.5' ELSE 0 END ||''' PORC_FSP_SUBSISTENCIA,
                              '''|| MI_PORC_EMPLEADO_AFP ||''' PORC_PENSIONE, 
                              '''|| MI_PORC_PATRON_AFP ||''' PORC_PENSIONP, 
                              '''|| MI_PORC_EMPLEADO_EPS ||''' PORC_SALUDE, 
                              '''|| MI_PORC_PATRON_EPS ||''' PORC_SALUDP, 
                              '''|| MI_COTIZACION_OBL_P ||''' PENSION_TOTAL, 
                              '''|| MI_COTIZACION_OBL_S ||''' SALUD_TOTAL, 
                              0 DIASCOMPLETOSRECONOCIDOS,  
                              '''|| UN_USUARIO || ''' CREATED_BY ,
                              SYSDATE DATE_CREATED                 
                              FROM DUAL';

            MI_MERGEENLACE := '    TABLA.COMPANIA       = VISTA.COMPANIA 
                               AND TABLA.LLAVENOVEDAD   = VISTA.LLAVENOVEDAD 
                               AND TABLA.TIPONOVEDAD    = VISTA.TIPONOVEDAD 
                               AND TABLA.CONSECUTIVO    = VISTA.CONSECUTIVO 
                               AND TABLA.ID_DE_EMPLEADO = VISTA.ID_DE_EMPLEADO 
                               AND TABLA.ANO            = VISTA.ANO 
                               AND TABLA.MES            = VISTA.MES 
                               AND TABLA.PERIODO        = VISTA.PERIODO';
            
            MI_MERGEEXISTE := 'UPDATE SET TABLA.FECHAINICIAL = VISTA.FECHAINICIAL,
                               TABLA.FECHAFINAL = VISTA.FECHAFINAL,
                               TABLA.BASE = VISTA.BASE,
                               TABLA.DIAS = VISTA.DIAS,
                               TABLA.BASEPROPORCIONAL = VISTA.BASEPROPORCIONAL,
                               TABLA.MANUAL = VISTA.MANUAL,
                               TABLA.FECHACAMBIOMANUAL = VISTA.FECHACAMBIOMANUAL,
                               TABLA.APORTEPATRONALSALUD = VISTA.APORTEPATRONALSALUD,
                               TABLA.APORTEEMPLEADOSALUD = VISTA.APORTEEMPLEADOSALUD,
                               TABLA.APORTEPATRONALPENSION = VISTA.APORTEPATRONALPENSION,
                               TABLA.APORTEEMPLEADOPENSION = VISTA.APORTEEMPLEADOPENSION,
                               TABLA.FSP = VISTA.FSP,
                               TABLA.FSPSUBSISTENCIA = VISTA.FSPSUBSISTENCIA,
                               TABLA.FSPADICIONAL = VISTA.FSPADICIONAL,
                               TABLA.PORC_PENSIONTOTAL = VISTA.PORC_PENSIONTOTAL,
                               TABLA.PORC_SALUDTOTAL = VISTA.PORC_SALUDTOTAL,
                               TABLA.PORC_FSP = VISTA.PORC_FSP,
                               TABLA.PORC_FSP_ADICIONAL = VISTA.PORC_FSP_ADICIONAL,
                               TABLA.PORC_PENSIONE = VISTA.PORC_PENSIONE,
                               TABLA.PORC_PENSIONP = VISTA.PORC_PENSIONP,
                               TABLA.PORC_SALUDE = VISTA.PORC_SALUDE,
                               TABLA.PORC_SALUDP = VISTA.PORC_SALUDP,
                               TABLA.PENSION_TOTAL = VISTA.PENSION_TOTAL,
                               TABLA.SALUD_TOTAL = VISTA.SALUD_TOTAL,
                               TABLA.ID_DE_PROCESO = VISTA.ID_DE_PROCESO,
                               TABLA.DIASCOMPLETOSRECONOCIDOS = VISTA.DIASCOMPLETOSRECONOCIDOS,
                               TABLA.PORC_FSP_SUBSISTENCIA = VISTA.PORC_FSP_SUBSISTENCIA,
                               TABLA.MODIFIED_BY = VISTA.CREATED_BY,
                               TABLA.DATE_MODIFIED = VISTA.DATE_CREATED';

            MI_MERGENOEXIS := 'INSERT ( COMPANIA, LLAVENOVEDAD, TIPONOVEDAD, CONSECUTIVO, ID_DE_EMPLEADO, ANO, MES, PERIODO,
                               FECHAINICIAL, FECHAFINAL, BASE, DIAS, BASEPROPORCIONAL, MANUAL, FECHACAMBIOMANUAL,
                               APORTEPATRONALSALUD, APORTEEMPLEADOSALUD, APORTEPATRONALPENSION, APORTEEMPLEADOPENSION,
                               FSP, FSPSUBSISTENCIA, FSPADICIONAL, PORC_PENSIONTOTAL, PORC_SALUDTOTAL, PORC_FSP,
                               PORC_FSP_ADICIONAL, PORC_PENSIONE, PORC_PENSIONP, PORC_SALUDE, PORC_SALUDP,
                               PENSION_TOTAL, SALUD_TOTAL, ID_DE_PROCESO, DIASCOMPLETOSRECONOCIDOS, PORC_FSP_SUBSISTENCIA,
                               CREATED_BY, DATE_CREATED)
                               VALUES ( VISTA.COMPANIA, VISTA.LLAVENOVEDAD, VISTA.TIPONOVEDAD, VISTA.CONSECUTIVO, VISTA.ID_DE_EMPLEADO, VISTA.ANO, VISTA.MES, VISTA.PERIODO,
                               VISTA.FECHAINICIAL, VISTA.FECHAFINAL, VISTA.BASE, VISTA.DIAS, VISTA.BASEPROPORCIONAL, VISTA.MANUAL, VISTA.FECHACAMBIOMANUAL,
                               VISTA.APORTEPATRONALSALUD, VISTA.APORTEEMPLEADOSALUD, VISTA.APORTEPATRONALPENSION, VISTA.APORTEEMPLEADOPENSION,
                               VISTA.FSP, VISTA.FSPSUBSISTENCIA, VISTA.FSPADICIONAL, VISTA.PORC_PENSIONTOTAL, VISTA.PORC_SALUDTOTAL, VISTA.PORC_FSP,
                               VISTA.PORC_FSP_ADICIONAL, VISTA.PORC_PENSIONE, VISTA.PORC_PENSIONP, VISTA.PORC_SALUDE, VISTA.PORC_SALUDP,
                               VISTA.PENSION_TOTAL, VISTA.SALUD_TOTAL, VISTA.ID_DE_PROCESO, VISTA.DIASCOMPLETOSRECONOCIDOS, VISTA.PORC_FSP_SUBSISTENCIA,
                               VISTA.CREATED_BY, VISTA.DATE_CREATED)';
                
            BEGIN
              BEGIN
                    PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA       => MI_TABLA,
                                                         UN_ACCION      => 'IM',
                                                         UN_MERGEUSING  => MI_MERGEUSING,
                                                         UN_MERGEENLACE => MI_MERGEENLACE,
                                                         UN_MERGEEXISTE => MI_MERGEEXISTE,
                                                         UN_MERGENOEXIS => MI_MERGENOEXIS);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    MI_MSGERROR(1).CLAVE := 'LLAVENOVEDAD';
                    MI_MSGERROR(1).VALOR := '<<VALOR DINÁMICO>>'; -- puedes colocar UN_LLANOVEDAD si tienes ese valor
                    RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACT_MERGEPROP,
                                         UN_TABLAERROR => MI_TABLA,
                                         UN_REEMPLAZOS => MI_MSGERROR);
            END;
          
          END IF;
      
    END LOOP;
    IF MI_ERRORES > 0 THEN
    MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
    MI_MENSAJE:=MI_MENSAJE||'********************* FIN DEL INFORME *********************'||CHR(10)||CHR(13);
    END IF;
RETURN MI_MENSAJE;
END FC_CARGAR_BASESNOVEDADES;

PROCEDURE PR_CALCESANTIASFNAESPFUNZA
/*
    NAME              : PR_CALCESANTIASFNAESPFUNZA  -> EN ACCESS calcularcesantiasFNAALCTOCANCIPA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 03/09/2025
    TIME              : 02:37 PM
    SOURCE MODULE     : NOMINA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  CALCULARCESANTIASFNAESPFUNZA

  */

  AS

    MI_CONCEPTODOCEAVASFNA  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIAS                 PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASINT              PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_APLICADAS            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;

    MI_PS                   PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PVFNA                PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PNFNA                PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_HEFNA                PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PROMFAC              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CESANTIA1            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_ANTIC_ENC            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PROM_ENC             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIASCES_ENC          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_ANTICIPOS            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_BASPFNA              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CONCEPTOAJUSTEBASP   PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
BEGIN

    MI_CONCEPTODOCEAVASFNA := TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FNA', '483'));
    MI_CONCEPTOAJUSTEBASP := TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO AJUSTES B.A.S.P.', '0'));

    IF (PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.CPARENTRADA(1).NIT = '844.000.755-4') OR (PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.CPARENTRADA(1).NIT = '899.999.428-8') THEN
        RETURN;
    END IF;

    PCK_NOMINA.GL_LICENCIAS := 0;
    PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
    PCK_NOMINA.GL_BASCES := 0;
    MI_PROMFAC := 0;

    PCK_NOMINA.GL_SBM := CASE WHEN PCK_NOMINA.FC_CN(900) = 0 THEN CASE WHEN PCK_NOMINA.FC_CN(10) > 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END ELSE PCK_NOMINA.FC_CN(900) END;
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
        PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM; -- + COMISIONES;
    ELSE
        PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM;
    END IF;

    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
        PCK_NOMINA.GL_FECHAIC := PCK_NOMINA.GL_FECHAIR ;
    ELSE
        PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO ,'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO ,'DD/MM/YYYY') END;
    END IF;

    IF PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 7 THEN
        MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')) - PCK_NOMINA.GL_LICENCIAS;
        MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN => CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END
            ,UN_FECHAFIN => TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'));
    ELSE
        MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
        MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END, PCK_NOMINA.GL_FECHAFIN);

        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > TO_DATE('01/01' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
            MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > TO_DATE('01/01' ||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA ELSE TO_DATE('01/01' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END, PCK_NOMINA.GL_FECHAFIN1);
        END IF;
    END IF;
    MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
    PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155);

    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION ;
        MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_PARST.FC_PAR('DESCONTAR SOLO ANTICIPOS Y NO APLICADAS', ' ') = 'SI' THEN
            MI_APLICADAS := 0;
            PCK_NOMINA.CN(915) := 0;
        ELSE
            MI_APLICADAS := PCK_NOMINA.FC_CNA(277) + PCK_NOMINA.FC_CNA(483);
            PCK_NOMINA.CN(915) := MI_APLICADAS;
        END IF;

    ELSE
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

        IF PCK_PARST.FC_PAR('DESCONTAR SOLO ANTICIPOS Y NO APLICADAS', ' ') = 'SI' THEN
            MI_APLICADAS := 0;
            PCK_NOMINA.CN(915) := 0;
        ELSE
            MI_APLICADAS := PCK_NOMINA.FC_CNA(277) + PCK_NOMINA.FC_CNA(483);
            PCK_NOMINA.CN(915) := MI_APLICADAS;
        END IF;

        PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION ;
        MI_DIAS := MI_DIAS - PCK_NOMINA.GL_LICENCIAS;
    END IF;
    IF PCK_NOMINA.GL_SPER = 8 THEN
        PCK_NOMINA.CN(2) := PCK_NOMINA.FC_CN(1);
    END IF;

    MI_HEFNA := 0;
    MI_PS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(543)) / 12, 0) ;
    MI_PVFNA := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CNA(155) = PCK_NOMINA.FC_CN(155) THEN PCK_NOMINA.FC_CN(155) ELSE PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) END + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CN(541) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CNA(541)) / 12, 0) ;
    MI_PNFNA := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CNA(158) = PCK_NOMINA.FC_CN(158) THEN PCK_NOMINA.FC_CN(158) ELSE PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) END + PCK_NOMINA.FC_CN(504) + PCK_NOMINA.FC_CNA(504)) / 12, 0) ;

    MI_HEFNA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CNA(528)) / 12, 0);
    MI_BASPFNA := PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(MI_CONCEPTOAJUSTEBASP);

    MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(515) + PCK_NOMINA.FC_CN(62) + PCK_NOMINA.FC_CN(506) + PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CN(525) + PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CN(524)
                    + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.GL_PVAC + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CN(504) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(543) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538) END
                    + MI_PS + MI_PVFNA + MI_PNFNA + CASE WHEN PCK_NOMINA.FC_CN(70) = 0 THEN MI_HEFNA ELSE 0 END;

    IF PCK_NOMINA.GL_VPA = 0 AND PCK_NOMINA.CPARENTRADA(1).NIT = '811036609-2' THEN
        PCK_NOMINA.GL_VPA := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_VALORULTIMOQUINQUENIO(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
    END IF;

    PCK_NOMINA_CALCULO.PR_SUMACESANTIASCONSOLIDADASCN;

    IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FNA', ' ') = 'SI' THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, (PCK_NOMINA.GL_SANO), PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    END IF;

    IF PCK_NOMINA.GL_SPER = 8 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,  PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 7, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

        MI_PS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(543)) / 12, 0) ;
        PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(543)) / 12, 0) ;

        PCK_NOMINA.CN(902) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CNA(528)) / 12, 0);
        MI_PROMFAC := 0;

        MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_NOMINA.GL_SBM + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_VPA + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CNA(528)) / 12, 0) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA ;

        MI_PROMFAC := MI_PROMFAC + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0) + MI_PS + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_CALCULO.FC_VLRULTIMPRIMAEXTRASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(155) > 0 THEN (PCK_NOMINA.FC_CN(155) / 12) ELSE (PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12 END, 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_CALCULO.FC_VALORULTIMAPRIMANAVIDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
        MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC, 0) END;
        MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
        PCK_NOMINA.CN(MI_CONCEPTODOCEAVASFNA) := 0;

        MI_DIASCES_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
        IF PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) <> 0 THEN
            MI_PROM_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'V', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
            MI_ANTIC_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'R', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
            MI_DIAS := MI_DIAS - PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);

            IF MI_DIAS < 0 THEN
                MI_DIAS := 0;
            END IF;
        ELSE
            MI_PROM_ENC := 0;
            MI_ANTIC_ENC := 0;
        END IF;
        PCK_NOMINA.CN(MI_CONCEPTODOCEAVASFNA) := CASE WHEN PCK_NOMINA.FC_CN(MI_CONCEPTODOCEAVASFNA) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((MI_CESANTIA1 / 360 * MI_DIAS) + CASE WHEN MI_DIASCES_ENC > 0 THEN MI_ANTIC_ENC ELSE 0 END - MI_APLICADAS - MI_ANTICIPOS, 0) ELSE PCK_NOMINA.FC_CN(MI_CONCEPTODOCEAVASFNA) END;
        PCK_NOMINA.CN(910) := MI_DIAS;
        PCK_NOMINA.CN(912) := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) ;

        PCK_NOMINA.CN(900) := PCK_NOMINA.GL_SBM ;
        PCK_NOMINA.CN(903) := PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CNA(525);
        PCK_NOMINA.CN(904) := PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CNA(524);
        PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(155) > 0 THEN (PCK_NOMINA.FC_CN(155) / 12) ELSE (PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(501)) / 12 END, 0);
        PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_CALCULO.FC_VALORULTIMAPRIMANAVIDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(504)) / 12, 0);

        PCK_NOMINA.CN(908) := PCK_NOMINA.GL_GRPNGV;
        PCK_NOMINA.CN(909) := 0;
        PCK_NOMINA.CN(911) := MI_APLICADAS + MI_ANTICIPOS ;
        PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0) ;
        PCK_NOMINA.CN(914) := 0 ;
        PCK_NOMINA.CN(915) := MI_APLICADAS + MI_ANTICIPOS ;
        PCK_NOMINA.CN(901) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(514)) / 12, 0);
        IF PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 7 THEN
            PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'));
        ELSE
            PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1);
        END IF;
    ELSE
            IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FNA', ' ') = 'SI' THEN
                MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0) END;
                IF (PCK_NOMINA.FC_CN(94) + PCK_NOMINA.FC_CN(347) + PCK_NOMINA.FC_CN(35) + PCK_NOMINA.FC_CN(336)) > 0 AND PCK_NOMINA.GL_PROMCONSOLIDADASCN < PCK_NOMINA.FC_CN(1) THEN
                    MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT ;
                    IF PCK_NOMINA.GL_SPER = '01' OR PCK_NOMINA.GL_SPER = '02' THEN
                        MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC / 2, 0);
                    END IF;
                    --ALERTA 'EL EMPLEADO ' CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' CPERSONAL(PCK_NOMINA.P).NOMBRES & ', INGRESOS MENORES AL SMB. SE AJUSTARA BASE PARA APORTES FNA. ' & ', CEDULA NO.' CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO & ',TIPO: ' CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO
                END IF;
            ELSE
                IF PCK_NOMINA.GL_SPER = 1 THEN
                    PCK_NOMINA.CN(969) := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + MI_PS + MI_PVFNA + MI_PNFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0) END;
                    MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + MI_PS + MI_PVFNA + MI_PNFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0) END;
                ELSE
                    MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PNFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0) END;
                END IF;
            END IF;
            IF PCK_NOMINA.GL_SPER = '07' THEN
                MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + MI_PS + MI_PVFNA + MI_PNFNA + MI_HEFNA + PCK_NOMINA.GL_VPA + MI_BASPFNA, 0);
            END IF;

            IF PCK_NOMINA.GL_SPER = '01' THEN
                PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(1)  ;
            ELSE
                PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(506) + PCK_NOMINA.FC_CN(515) ;
            END IF;

            PCK_NOMINA.CN(904) := PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CN(524);
            PCK_NOMINA.CN(903) := PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CN(525);

            IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FNA', ' ') = 'SI' THEN
                PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0) ;

                MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
                MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0)  ;
                IF (PCK_NOMINA.FC_CN(94) + PCK_NOMINA.FC_CN(347) + PCK_NOMINA.FC_CN(35) + PCK_NOMINA.FC_CN(336)) > 0 AND PCK_NOMINA.GL_PROMCONSOLIDADASCN < PCK_NOMINA.FC_CN(1) THEN
                    MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT ;
                    IF PCK_NOMINA.GL_SPER = '01' OR PCK_NOMINA.GL_SPER = '02' THEN
                        MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC / 2, 0);
                    END IF;
                    MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
                    --ALERTA 'EL EMPLEADO ' CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' CPERSONAL(PCK_NOMINA.P).NOMBRES & ', INGRESOS MENORES AL SMB. SE AJUSTARA BASE PARA APORTES FNA. ' & ', CEDULA NO.' CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO & ',TIPO: ' CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO
                END IF;
                IF PCK_NOMINA.GL_SPER = '07' THEN
                    MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + MI_PS + MI_PVFNA + MI_PNFNA + PCK_NOMINA.GL_VPA, 0) ;
                    MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + MI_PS + MI_PVFNA + MI_PNFNA + PCK_NOMINA.GL_VPA, 0) ;
                END IF;
            ELSE
                IF PCK_NOMINA.GL_SPER = '02' OR PCK_NOMINA.GL_SPER = '03' THEN
                    IF PCK_NOMINA.GL_PROMCONSOLIDADASCN < PCK_NOMINA.FC_CN(1) AND PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FNA', ' ') = 'SI' THEN
                        PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_NOMINA.FC_CN(1);
                    ELSE
                        PCK_NOMINA.GL_PROMCONSOLIDADASCN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 AND PCK_NOMINA.FC_CN(11) > 15 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA END;
                    END IF;

                    IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FNA', ' ') = 'SI' THEN
                        PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PNFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0);

                        MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0) END;
                    ELSE
                        MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PNFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0) END;
                    END IF;
                    MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
                    MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PNFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0);
                ELSE
                    PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + MI_PS + MI_PVFNA + MI_PNFNA + PCK_NOMINA.GL_VPA, 0);
                    MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
                    MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PNFNA + PCK_NOMINA.GL_VPA, 0);
                END IF;
            END IF;
            IF PCK_NOMINA.GL_SPER = 4 THEN
                PCK_NOMINA_CALCULO.PR_SUMACESANTIASCONSOLIDADASCN;
                MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0) END;
                PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(1);
                PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
                PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
            END IF;
        --END IF;

        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA,913, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CN(MI_CONCEPTODOCEAVASFNA) = 0 THEN
            IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FNA', ' ') = 'SI' AND PCK_NOMINA.GL_SPER <> 7 THEN
                IF PCK_NOMINA.GL_SPER = 2 THEN
                    PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0)   ;
                    PCK_NOMINA.CN(MI_CONCEPTODOCEAVASFNA) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC / 12), 0);
                ELSE
                    PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC) - PCK_NOMINA.FC_CNP(913), 0) ;
                    PCK_NOMINA.CN(MI_CONCEPTODOCEAVASFNA) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC / 12) - PCK_NOMINA.FC_CNA(MI_CONCEPTODOCEAVASFNA), 0);
                END IF;
            ELSE
                IF PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3 THEN
                    PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC) - PCK_NOMINA.FC_CNP(913), 0) ;
                ELSE
                    PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0) ;
                END IF;

                MI_DIASCES_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
                IF PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) <> 0 THEN
                    MI_PROM_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'V', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
                    MI_ANTIC_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'R', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
                    MI_DIAS := MI_DIAS - PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
                    IF MI_DIAS < 0 THEN
                        MI_DIAS := 0;
                    END IF;
                ELSE
                    MI_PROM_ENC := 0;
                    MI_ANTIC_ENC := 0;
                END IF;
                PCK_NOMINA.CN(MI_CONCEPTODOCEAVASFNA) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC / 360 * MI_DIAS) + CASE WHEN MI_DIASCES_ENC > 0 THEN MI_ANTIC_ENC ELSE 0 END - MI_APLICADAS - MI_ANTICIPOS, 0);
                IF PCK_NOMINA.GL_SMES < 12 OR (PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.GL_SPER = 3) THEN
                    PCK_NOMINA.CN(MI_CONCEPTODOCEAVASFNA) := CASE WHEN PCK_NOMINA.FC_CN(MI_CONCEPTODOCEAVASFNA) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(MI_CONCEPTODOCEAVASFNA) END;
                END IF;
            END IF;
        ELSE
            PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(MI_CONCEPTODOCEAVASFNA)) * 12, 0) ;
        END IF;

        PCK_NOMINA.CN(901) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(150) / 12, 0);
        PCK_NOMINA.CN(902) := MI_HEFNA ;
        PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(504)) / 12, 0);
        PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(543)) / 12, 0);
        IF PCK_NOMINA.GL_SMES = 6 AND PCK_NOMINA.GL_SANO = 2010 THEN
            PCK_NOMINA.CN(907) := 0 ;
        ELSE
            PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0);
        END IF;
        PCK_NOMINA.CN(908) := PCK_NOMINA.GL_GRPNGV;
        PCK_NOMINA.CN(909) := 0;

        PCK_NOMINA.CN(910) := PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CN(11) ;
        IF PCK_NOMINA.GL_SPER = 1 OR PCK_NOMINA.GL_SPER = 2 AND PCK_NOMINA.FC_CN(910) > 15 THEN
            PCK_NOMINA.CN(910) := CASE WHEN (PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CN(11)) = 30 OR (PCK_NOMINA.FC_CN(9) = PCK_NOMINA.FC_CN(11)) THEN 15 ELSE PCK_NOMINA.FC_CN(9) END  ;
        END IF;
        PCK_NOMINA.CN(911) := MI_ANTICIPOS ;
        PCK_NOMINA.CN(912) := PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) ;
        PCK_NOMINA.CN(914) := 0 ;
        IF PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 7 THEN
            PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'));
            PCK_NOMINA.CN(MI_CONCEPTODOCEAVASFNA) := 0;
            PCK_NOMINA.CN(913) := 0;
        ELSE
            PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1);
        END IF;
    END IF;

END PR_CALCESANTIASFNAESPFUNZA;

PROCEDURE PR_CALCESANTIASFTPESPFUNZA
/*
  NAME              : PR_CALCESANTIASFTPESPFUNZA ->  En access = calcularcesantias_FTP_STR
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
  DATE MIGRADOR     : 03/09/2025
  TIME              : 04:00 PM
  SOURCE MODULE     : NOMINA
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       :
  @NAME:  CALCULARCESANTIASFTPESPFUNZA
*/
AS

  MI_ALIMRET        PCK_SUBTIPOS.TI_DOBLE :=0 ;
  MI_SBM          PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_PROMFAC        PCK_SUBTIPOS.TI_DOBLE :=0 ;
  MI_COMISIONES     PCK_SUBTIPOS.TI_DOBLE :=0 ;
  MI_FECHAIC        DATE;
  MI_DIAS         PCK_SUBTIPOS.TI_ENTERO :=0;
  MI_DIASINT        PCK_SUBTIPOS.TI_ENTERO :=0;
  MI_ANTICIPOS      PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_APLICADAS      PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_PS           PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_PVFNA        PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_HEFNA        PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_CESANTIA1      PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_PROM_ENC       PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_ANTIC_ENC      PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_DIASCES_ENC      PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_MSG              PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN

  PCK_NOMINA.GL_LICENCIAS := 0;
  PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION ;
  MI_PROMFAC := 0;
  MI_SBM := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(900) = 0, PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) > 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)), PCK_NOMINA.FC_CN(900));
  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
    MI_SBM := MI_SBM + MI_COMISIONES;
  ELSE
    MI_SBM := MI_SBM;
  END IF;

  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
    MI_FECHAIC := PCK_NOMINA.GL_FECHAIR;
  ELSE
    MI_FECHAIC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), PCK_NOMINA.GL_FECHAIR, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'));
  END IF;
  IF PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 7 THEN
    MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIC, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')) - PCK_NOMINA.GL_LICENCIAS;
    MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'), PCK_NOMINA.GL_FECHAIR, TO_DATE('01/01' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY')), TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'));
  ELSE
    MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
    MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'), PCK_NOMINA.GL_FECHAIR, TO_DATE('01/01' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY')), PCK_NOMINA.GL_FECHAFIN1);

    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > TO_DATE('01/01' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY') THEN
      MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
      MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > TO_DATE('01/01' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY') THEN  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA ELSE TO_DATE('01/01' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY') END, PCK_NOMINA.GL_FECHAFIN1);
    END IF;
  END IF;
  MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) ;
  PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155);

  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR), PCK_SYSMAN_UTL.FC_IIF(PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16, 1, 2), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
    PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
    MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_PARST.FC_PAR('DESCONTAR SOLO ANTICIPOS Y NO MI_APLICADAS', 'NO') = 'SI' THEN
      MI_APLICADAS := 0;
      PCK_NOMINA.CN(915) := 0;
    ELSE
      MI_APLICADAS := PCK_NOMINA.FC_CN(277) + PCK_NOMINA.FC_CN(483) + PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0')));
      PCK_NOMINA.CN(915) := MI_APLICADAS;
    END IF;

  ELSE
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

    IF PCK_PARST.FC_PAR('DESCONTAR SOLO ANTICIPOS Y NO MI_APLICADAS', 'NO') = 'SI' THEN
      MI_APLICADAS := 0;
      PCK_NOMINA.CN(915) := 0;
    ELSE
      MI_APLICADAS := PCK_NOMINA.FC_CN(277) + PCK_NOMINA.FC_CN(483) + PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0')));
      PCK_NOMINA.CN(915) := MI_APLICADAS;
    END IF;

    PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
    MI_DIAS := MI_DIAS - PCK_NOMINA.GL_LICENCIAS;
  END IF;
  IF PCK_NOMINA.GL_SPER = 8 THEN
    PCK_NOMINA.CN(2) := PCK_NOMINA.FC_CN(1);
  END IF;

  MI_PS := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(543) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(543)) / 12, 0) ;
  MI_PVFNA := PCK_SYSMAN_UTL.FC_ROUND((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(155) = PCK_NOMINA.FC_CN(155), PCK_NOMINA.FC_CN(155), PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(155)) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CN(541) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CN(541)) / 12, 0) ;
  MI_PVFNA := PCK_SYSMAN_UTL.FC_ROUND((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(158) = PCK_NOMINA.FC_CN(158), PCK_NOMINA.FC_CN(158), PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(158)) + PCK_NOMINA.FC_CN(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) ;

  MI_HEFNA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CN(528)) / 12, 0);

  MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(515) + PCK_NOMINA.FC_CN(62) + PCK_NOMINA.FC_CN(506) + PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CN(525) + PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CN(524) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.GL_PVAC + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CN(504) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(543) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538)) + MI_PS + MI_PVFNA + MI_PVFNA + PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(70) = 0, MI_HEFNA, 0);
  IF PCK_NOMINA.GL_VPA = 0 AND PCK_NOMINA.CPARENTRADA(1).NIT = '811036609-2' THEN
    PCK_NOMINA.GL_VPA := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_VALORULTIMOQUINQUENIO(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
  END IF;
  PCK_NOMINA_CALCULO.PR_SUMACESANTIASCONSOLIDADASCN;
  IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FTP', 'NO') = 'SI' THEN
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
  END IF;
  IF PCK_NOMINA.GL_SPER = 8 THEN
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 7, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.CN(902) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CN(528)) / 12, 0);
    MI_PROMFAC := 0;
    MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), MI_SBM + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_VPA + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CN(528)) / 12, 0)) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA ;
    MI_PROMFAC := MI_PROMFAC + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0) + MI_PS + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_CALCULO.FC_VLRULTIMPRIMAEXTRASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(155) > 0, PCK_NOMINA.FC_CN(155) / 12, (PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_CALCULO.FC_VALORULTIMAPRIMANAVIDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
    MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC, 0));
    MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
    PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) := 0;
    MI_DIASCES_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
    IF PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) <> 0 THEN
      MI_PROM_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'V', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
      MI_ANTIC_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'R', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
      MI_DIAS := MI_DIAS - PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
      IF MI_DIAS < 0 THEN
        MI_DIAS := 0;
      END IF;
    ELSE
      MI_PROM_ENC := 0;
      MI_ANTIC_ENC := 0;
    END IF;
    PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) = 0, PCK_SYSMAN_UTL.FC_ROUND((MI_CESANTIA1 / 360 * MI_DIAS) + PCK_SYSMAN_UTL.FC_IIF(MI_DIASCES_ENC > 0, MI_ANTIC_ENC, 0) - MI_APLICADAS - MI_ANTICIPOS, 0), PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))));
    IF PCK_NOMINA.FC_CN(477) < 0 THEN
      
      MI_MSG(1).CLAVE := 'NOMEMPLEADO';
      MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
      MI_MSG(2).CLAVE := 'CEDULA';
      MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
      MI_MSG(3).CLAVE := 'TIPO';
      MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

      PCK_NOMINA_COM7.PR_ALERTA
          (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
          ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_ALERSALDONEGATIVO13
          ,UN_REEMPLAZOS   => MI_MSG
          ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
          ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
          ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
          ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
          ,UN_USER         => PCK_CONEXION.FC_GETUSER
          );

    END IF;

    PCK_NOMINA.CN(910) := MI_DIAS;
    PCK_NOMINA.CN(912) := PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) ;

    PCK_NOMINA.CN(900) := MI_SBM ;
    PCK_NOMINA.CN(903) := PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CN(525);
    PCK_NOMINA.CN(904) := PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CN(524);
    PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(155) > 0, PCK_NOMINA.FC_CN(155) / 12, (PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(501)) / 12), 0);
    PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_CALCULO.FC_VALORULTIMAPRIMANAVIDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(504)) / 12, 0);
    PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(503)) / 12 + (PCK_NOMINA_CALCULO.FC_VLRULTIMPRIMAEXTRASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
    PCK_NOMINA.CN(908) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(909) := 0;
    PCK_NOMINA.CN(911) := MI_APLICADAS + MI_ANTICIPOS;
    PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0);
    PCK_NOMINA.CN(914) := 0;
    PCK_NOMINA.CN(915) := MI_APLICADAS + MI_ANTICIPOS;
    PCK_NOMINA.CN(901) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(514)) / 12, 0);
    IF PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 7 THEN
      PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'));
    ELSE
      PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1);
    END IF;
  ELSE

    IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FTP', 'NO') = 'SI' THEN
      MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0));
      IF (PCK_NOMINA.FC_CN(94) + PCK_NOMINA.FC_CN(347) + PCK_NOMINA.FC_CN(35) + PCK_NOMINA.FC_CN(336)) > 0 AND PCK_NOMINA.GL_PROMCONSOLIDADASCN < PCK_NOMINA.FC_CN(1) THEN
        MI_PROMFAC := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT;
        
        MI_MSG(1).CLAVE := 'NOMEMPLEADO';
        MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
        MI_MSG(2).CLAVE := 'CEDULA';
        MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
        MI_MSG(3).CLAVE := 'TIPO';
        MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

        PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_ALERINGMENORSMB_FNA
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );

      END IF;
    ELSE
      IF PCK_NOMINA.GL_SPER = 1 THEN
        PCK_NOMINA.CN(969) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + MI_PS + MI_PVFNA + MI_PVFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0));
        MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + MI_PS + MI_PVFNA + MI_PVFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0));
      ELSE
        MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PVFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0));
      END IF;
    END IF;
    IF PCK_NOMINA.GL_SPER = 7 THEN
      MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + MI_PS + MI_PVFNA + MI_PVFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0);
    END IF;
    IF PCK_NOMINA.GL_SPER = 1 THEN
      PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(1)  ;
    ELSE
      PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(506) + PCK_NOMINA.FC_CN(515) ;
    END IF;
    PCK_NOMINA.CN(904) := PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CN(524);
    PCK_NOMINA.CN(903) := PCK_NOMINA.FC_CN(80) + PCK_NOMINA.FC_CN(525);
    IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FTP', 'NO') = 'SI' THEN
      PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0) ;

      MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
      MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0)  ;
      IF (PCK_NOMINA.FC_CN(94) + PCK_NOMINA.FC_CN(347) + PCK_NOMINA.FC_CN(35) + PCK_NOMINA.FC_CN(336)) > 0 AND PCK_NOMINA.GL_PROMCONSOLIDADASCN < PCK_NOMINA.FC_CN(1) THEN
        MI_PROMFAC := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT;
        MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
        --ALERTA 'EL EMPLEADO ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES || ', INGRESOS MENORES AL SMB. SE AJUSTARA BASE PARA APORTES FNA. ' || ', CEDULA NO. --CEDULA-- ,TIPO: --TIPO--

        MI_MSG(1).CLAVE := 'NOMEMPLEADO';
        MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
        MI_MSG(2).CLAVE := 'CEDULA';
        MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
        MI_MSG(3).CLAVE := 'TIPO';
        MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

        PCK_NOMINA_COM7.PR_ALERTA
            (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
            ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_ALERINGMENORSMB_FNA
            ,UN_REEMPLAZOS   => MI_MSG
            ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
            ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
            ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
            ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
            ,UN_USER         => PCK_CONEXION.FC_GETUSER
            );

      END IF;
      IF PCK_NOMINA.GL_SPER = 7 THEN
        MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + MI_PS + MI_PVFNA + MI_PVFNA + PCK_NOMINA.GL_VPA, 0);
        MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + MI_PS + MI_PVFNA + MI_PVFNA + PCK_NOMINA.GL_VPA, 0);
      END IF;
    ELSE
      IF PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3 THEN
        IF PCK_NOMINA.GL_PROMCONSOLIDADASCN < PCK_NOMINA.FC_CN(1) AND PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FTP', 'NO') = 'SI' THEN
          PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_NOMINA.FC_CN(1);
        ELSE
          PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0 AND PCK_NOMINA.FC_CN(11) > 15, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA) ;
        END IF;

        IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FTP', 'NO') = 'SI' THEN
          PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PVFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0);
          MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0));
        ELSE
          MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PVFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0));
        END IF;
        MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
        MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PVFNA + MI_HEFNA + PCK_NOMINA.GL_VPA, 0);
      ELSE
        PCK_NOMINA.GL_PROMCONSOLIDADASCN := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + MI_PS + MI_PVFNA + MI_PVFNA + PCK_NOMINA.GL_VPA, 0);
        MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)), 0);
        MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN + MI_PS + MI_PVFNA + MI_PVFNA + PCK_NOMINA.GL_VPA, 0);
      END IF;
    END IF;
    IF PCK_NOMINA.GL_SPER = 4 THEN
      PCK_NOMINA_CALCULO.PR_SUMACESANTIASCONSOLIDADASCN;
      MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PROMCONSOLIDADASCN, 0));
      PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(1);
      PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
      PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
    END IF;

    PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA,913, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) = 0 THEN
      IF PCK_PARST.FC_PAR('CALCULAR SOLO DOCEAVAS MENSUALES FTP', 'NO') = 'SI' AND PCK_NOMINA.GL_SPER <> 7 THEN
        PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC) - PCK_NOMINA.FC_CNP(913), 0) ;
        PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC / 12) - PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))), 0);
      ELSE
        IF PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3 THEN
          PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC) - PCK_NOMINA.FC_CNP(913), 0) ;
        ELSE
          PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0) ;
        END IF;

        MI_DIASCES_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
        IF PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) <> 0 THEN
          MI_PROM_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'V', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
          MI_ANTIC_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'R', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
          MI_DIAS := MI_DIAS - PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
          IF MI_DIAS < 0 THEN
            MI_DIAS := 0;
          END IF;
        ELSE
          MI_PROM_ENC := 0;
          MI_ANTIC_ENC := 0;
        END IF;
        PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC / 360 * MI_DIAS) + PCK_SYSMAN_UTL.FC_IIF(MI_DIASCES_ENC > 0, MI_ANTIC_ENC, 0) - MI_APLICADAS - MI_ANTICIPOS, 0);
        IF PCK_NOMINA.GL_SMES < 12 OR (PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.GL_SPER = 3) THEN
          PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) < 0, 0, PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))));
        END IF;
      END IF;
    ELSE
      PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FNA', '0')))) * 12, 0) ;
    END IF;

    PCK_NOMINA.CN(901) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(150) / 12, 0);
    PCK_NOMINA.CN(902) := MI_HEFNA ;
    PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(504)) / 12, 0);
    PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(543)) / 12, 0);
    IF PCK_NOMINA.GL_SMES = 6 AND PCK_NOMINA.GL_SANO = 2010 THEN
      PCK_NOMINA.CN(907) := 0 ;
    ELSE
      PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501)) / 12, 0);
    END IF;
    PCK_NOMINA.CN(908) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(909) := 0;
    PCK_NOMINA.CN(910) := PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CN(11) ;
    PCK_NOMINA.CN(911) := MI_ANTICIPOS ;
    PCK_NOMINA.CN(912) := PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) ;
    PCK_NOMINA.CN(914) := 0 ;
    IF PCK_NOMINA.GL_SPER = 4 AND PCK_NOMINA.GL_SMES = 7 THEN
      PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'));
      PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CONCEPTO DOCEAVAS FTP', '0'))) := 0;
      PCK_NOMINA.CN(913) := 0;
    ELSE
      PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1);
    END IF;
  END IF;
END PR_CALCESANTIASFTPESPFUNZA;

PROCEDURE PR_CALCESANTIASESPFUNZA
(
    /*
    NAME              : PR_CALCESANTIASESPFUNZA ->  En access = calcularcesantiasCVALLE
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 03/09/2025
    TIME              : 4:00 PM
    SOURCE MODULE     : NOMINA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  CALCULARCESANTIASESPFUNZA
    */
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_DIASCES_ENC          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PROM_ENC             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_ANTIC_ENC            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PROMFAC              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIAS                 PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASINT              PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASPROMEDIO         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_ANTICIPOS            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CESANTIA1            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_FECHA    DATE;
    MI_CONT                 NUMBER(1);
BEGIN

    PCK_NOMINA.GL_BASCES := 0;
    MI_FECHA := PCK_NOMINA.GL_FECHAINI;
    IF NOT(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO < PCK_NOMINA.GL_FECHAINI) OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN --(MZANGUNA:17/12/2018)-Se agrega condicion si la fecha de retiro es nula
        PCK_NOMINA.GL_SBM := CASE WHEN PCK_NOMINA.FC_CN(900) = 0 THEN PCK_NOMINA.FC_CN(1) ELSE PCK_NOMINA.FC_CN(900) END;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN1) THEN
                PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
                PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
                PCK_NOMINA.GL_PERA := CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) < 16 THEN 1 ELSE 2 END;
                MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFIN1);
            ELSE
                PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
                PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
                PCK_NOMINA.GL_PERA := CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN)) < 16 THEN 1 ELSE 2 END;
                MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN), PCK_NOMINA.GL_FECHAFIN1);
            END IF;

            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES - 1, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_COMISIONES := (PCK_NOMINA.FC_CNA(188) / MI_DIASPROMEDIO) * 30;
            PCK_NOMINA.CN(971) := PCK_NOMINA.GL_COMISIONES;
        END IF;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
            PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM + PCK_NOMINA.GL_COMISIONES;
        ELSE
            PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM;
        END IF;
        PCK_NOMINA.GL_BONPAGADA := 0;

        IF PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996', 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
            PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1);
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_FECHAIC := PCK_NOMINA.GL_FECHAIR;
        ELSE
            PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
            PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > PCK_NOMINA.GL_FECHAIC AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA ELSE PCK_NOMINA.GL_FECHAIC END;

            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIC) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
            PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1 - 10);
        END IF;
        MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
        IF PCK_NOMINA.FC_CNA(155) = 0 THEN
            PCK_NOMINA.GL_PVAC := PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        ELSE
            IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)), 0);
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501);
            END IF;
        END IF;
        IF ( PCK_NOMINA.GL_SMES <= 6 ) THEN
          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO - 1, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        ELSE 
          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        END IF;
        PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(159)) / 12, 0);
        PCK_NOMINA.GL_BONPAGADA := 0;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(538) > 0 THEN
            PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) - PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538)) / 12, 0);
        ELSIF PCK_NOMINA.FC_CN(150) > 0 AND PCK_NOMINA.GL_SPRC <> 99  THEN
            PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_BONPAGADA + (CASE WHEN PCK_NOMINA.GL_SPER = 3 THEN ((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)+ PCK_NOMINA.FC_CN(538)) / 12) ELSE 0 END),0);
           -- PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538)) / 12, 0);
        END IF;
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
        PCK_NOMINA.CN(902) := CASE WHEN PCK_NOMINA.FC_CN(902) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) + (CASE WHEN PCK_NOMINA.GL_SPRC = 99 AND PCK_NOMINA.GL_SMES < 12 THEN 0 ELSE PCK_NOMINA.FC_CN(70) END)) / 12, 0) ELSE PCK_NOMINA.FC_CN(902) END ;
        PCK_NOMINA.CN(905) := PCK_NOMINA_CALCULO.FC_VALORULTIMAPRIMANAVIDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        --PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        --PCK_NOMINA.CN(905) := PCK_NOMINA.FC_CN(905) + (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 2 THEN PCK_NOMINA.FC_CN(158) ELSE 0 END);
        PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.CN(905)/12,0);
        IF PCK_NOMINA.FC_CN(404) <> 0 THEN
            PCK_NOMINA.GL_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), (PCK_NOMINA.GL_SMES+1), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.CN(902) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_SUMACONA(45, 60) + PCK_NOMINA.FC_SUMACON(45, 60) + PCK_NOMINA.FC_CN(511) + PCK_NOMINA.FC_CN(523) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CNA(511) + PCK_NOMINA.FC_CNA(523) + PCK_NOMINA.FC_CNA(528)) / 12, 0);
        END IF;
        MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA
                    + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_AUXT + PCK_NOMINA.FC_CN(906) + PCK_NOMINA.FC_CN(905) + PCK_NOMINA.FC_CN(907) + PCK_NOMINA.FC_CN(902) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_BONPAGADA / 12),0) END;
        MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC, 0);
        PCK_NOMINA.CN(969) := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC, 0);
        MI_DIASCES_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
        IF PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) <> 0 THEN
            MI_PROM_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'V', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
            MI_ANTIC_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'R', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
            MI_DIAS := MI_DIAS - PCK_NOMINA_CALCULO2.FC_CESANTIA_C(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 'D', PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
            IF MI_DIAS < 0 THEN
                MI_DIAS := 0;
            END IF;
        ELSE
            MI_PROM_ENC := 0;
            MI_ANTIC_ENC := 0;
        END IF;
        IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHACS, PCK_NOMINA.GL_FECHAFIN1) <= 90 AND PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) < 360 THEN
            MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND((((MI_PROMFAC)) * MI_DIAS / 360), 0) + CASE WHEN MI_DIASCES_ENC > 0 THEN MI_ANTIC_ENC ELSE 0 END - MI_ANTICIPOS;
        ELSE
            IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) < 360 THEN
                MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND((((MI_PROMFAC)) * MI_DIAS / 360), 0) + CASE WHEN MI_DIASCES_ENC > 0 THEN MI_ANTIC_ENC ELSE 0 END - MI_ANTICIPOS;
            ELSE
                MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)) * MI_DIAS / 360, 0) + CASE WHEN MI_DIASCES_ENC > 0 THEN MI_ANTIC_ENC ELSE 0 END - MI_ANTICIPOS;
            END IF;
        END IF;
        IF PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.FC_CN(411) <> 0 THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 2 THEN
                PCK_NOMINA.CN(169) := CASE WHEN PCK_NOMINA.FC_CN(169) = 0 THEN CASE WHEN MI_CESANTIA1 < 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) END ELSE PCK_NOMINA.FC_CN(169) END;
            END IF;
            PCK_NOMINA.CN(177) := CASE WHEN PCK_NOMINA.FC_CN(177) = 0 THEN MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(177) END;
        ELSIF PCK_NOMINA.FC_CN(412) <> 0 THEN
            MI_CONT := 0;
            IF ( PCK_NOMINA.GL_SMES = 12 ) THEN
                BEGIN
                    SELECT  DISTINCT 1
                    INTO    MI_CONT
                    FROM    NOVEDADES
                    WHERE   COMPANIA = PCK_NOMINA.GL_COMPANIA
                            AND ID_DE_PROCESO = PCK_NOMINA.GL_SPRC
                            AND ANO = PCK_NOMINA.GL_SANO
                            AND MES = PCK_NOMINA.GL_SMES
                            AND PERIODO = 7
                            AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                            AND ID_DE_CONCEPTO = 404
                            AND VALOR != 0;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CONT := 0;
                END;
            END IF;
            
            IF ( MI_CONT = 0 ) THEN
              IF NOT(PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996', 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1) THEN
                  PCK_NOMINA.CN(269) := CASE WHEN PCK_NOMINA.FC_CN(269) = 0 THEN CASE WHEN MI_CESANTIA1 < 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) END ELSE PCK_NOMINA.FC_CN(269) END;
              END IF;
              PCK_NOMINA.CN(277) := CASE WHEN PCK_NOMINA.FC_CN(277) = 0 THEN MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(277) END;
              IF PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = '12' THEN
                  PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA, 1, (PCK_NOMINA.GL_SANO + 1), 1, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 169, PCK_NOMINA.FC_CN(269));
              END IF;
      END IF;       
        END IF;

        PCK_NOMINA.CN(900) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
        PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
        PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
        PCK_NOMINA.CN(901) := PCK_NOMINA.GL_GRPNGV ;
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.CN(910) := MI_DIAS                                                    ;
        PCK_NOMINA.CN(911) := MI_ANTICIPOS                                               ;
        PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION                   ;
        PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0)                            ;
        PCK_NOMINA.CN(914) := PCK_NOMINA.GL_VPT ;
        PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1) + 1 ;
    END IF;

END PR_CALCESANTIASESPFUNZA;

FUNCTION FC_ARMARVSOI
(
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_EMPLEADO         IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_REDONDEO         IN VARCHAR2,
    UN_ANIO             IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES              IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO          IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_NOMINA           IN VARCHAR2,
    UN_PAGAR_DIAS       IN VARCHAR2,
    UN_FC_SGP_Y_TIPO_T  IN NUMBER,
    UN_RETROACTIVO      IN NUMBER,
    UN_TIPOPLANILLA     IN VARCHAR2,
    UN_ORDEN            IN NUMBER
) RETURN VARCHAR2  
AS  
    V_SQL VARCHAR2(32767); 
BEGIN  

    V_SQL := 'SELECT HISTORICOS.COMPANIA,
              HISTORICOS.ID_DE_PROCESO,
              HISTORICOS.ANO,
              HISTORICOS.MES,
              PERSONAL_HISTORICO.TIPO_SALARIO,
              PERSONAL_HISTORICO.PERIODO,
              HISTORICOS.ID_DE_EMPLEADO,
              PERSONAL_HISTORICO.ID_DE_TIPO,
              PERSONAL_HISTORICO.NUMEROPATRONAL,
              PERSONAL_HISTORICO.APLICA_0862,
              PERSONAL_HISTORICO.NUMERO_DCTO,
              PERSONAL_HISTORICO.DCTO_IDENTIDAD,
              PERSONAL_HISTORICO.APELLIDO1,
              PERSONAL_HISTORICO.APELLIDO2,
              PERSONAL_HISTORICO.NOMBRES,
              PERSONAL_HISTORICO.NOMBRECOMPLETO,
              PERSONAL_HISTORICO.TIPOVINCULACION,
              PERSONAL_HISTORICO.FECHA_ULTSUELDO,
              PERSONAL_HISTORICO.SALARIO_BASE_CATE SALARIO_BASE,
              PERSONAL_HISTORICO.SALARIO_BASE_IBC  SALARIO_BASE_IBC,
              PERSONAL_HISTORICO.FECHA_DE_INGRESO ,
              PERSONAL_HISTORICO.FECHA_DE_RETIRO  ,
              PERSONAL_HISTORICO.FECHATERCONTRATO,
              PERSONAL_HISTORICO.SUBTIPOCOTIZANTE ,
              PERSONAL_HISTORICO.MESADA_PENSIONAL,
              PERSONAL_HISTORICO.ESTADO_ACTUAL,
              PERSONAL_HISTORICO.FONDO_SALUD,
              PERSONAL_HISTORICO.ID_DEL_FONDO,
              PERSONAL_HISTORICO.FONDO_RIESGOS,
              PERSONAL_HISTORICO.CAJA_COMPENSACION,
              PERSONAL_HISTORICO.PENSIONCOMPARTIDA,
              PERSONAL_HISTORICO.COLOMBIANO_RESIDENTE,
              PERSONAL_HISTORICO.APELLIDO1CAUSANTE,
              PERSONAL_HISTORICO.APELLIDO2CAUSANTE,
              PERSONAL_HISTORICO.NOMBRE1CAUSANTE,
              PERSONAL_HISTORICO.NOMBRE2CAUSANTE,
              PERSONAL_HISTORICO.DCTO_IDENTIDADCAUSANTE,
              PERSONAL_HISTORICO.FECHA_CAMBIO_MESADA,
              PERSONAL_HISTORICO.NUMERO_DCTOCAUSANTE,
              PERSONAL_HISTORICO.TIPOPENSIONADO,
              PERSONAL_HISTORICO.DEPARTAMENTO_HAB,
              PERSONAL_HISTORICO.CIUDAD_HAB,
              CASE WHEN PERSONAL_HISTORICO.DEPARTAMENTO_LABORA IS NULL
                     OR PERSONAL_HISTORICO.DEPARTAMENTO_LABORA =''00''
              THEN PERSONAL_HISTORICO.DEPARTAMENTO_HAB
              ELSE PERSONAL_HISTORICO.DEPARTAMENTO_LABORA
              END DPTO_LAB,
              CASE WHEN PERSONAL_HISTORICO.CIUDAD_LABORA IS NULL
                     OR PERSONAL_HISTORICO.CIUDAD_LABORA =''00''
              THEN PERSONAL_HISTORICO.CIUDAD_HAB
              ELSE PERSONAL_HISTORICO.CIUDAD_LABORA
              END CIUDAD_LAB,
              V_FONDO_DE_RIESGOS.CODAIARP    FONDO_RIESGOS_SOI ,
              V_FONDO_DE_SALUD.CODAIFS       FONDO_SALUD_SOI,
              V_FONDO_DE_PENSIONES.CODAIFP   ID_DEL_FONDO_SOI,
              V_CAJA_COMPENSACION.CODAICC    CAJA_COMPENSACION_SOI,
              V_CAJA_COMPENSACION.DEPARTAMENTOC,
              V_CAJA_COMPENSACION.CIUDADC,
              NULL NUM_AUTO_INCAP,
              0 VR_INCAP,
              NULL NUM_AUTO_MAT,
              0 VR_MAT,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 212 THEN HISTORICOS.VALOR ELSE 0 END) BASERIESGOS,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 209 THEN HISTORICOS.VALOR ELSE 0 END) DIASRIESGOS,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 220 THEN HISTORICOS.VALOR ELSE 0 END) BASECAJAS,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 347 THEN HISTORICOS.VALOR ELSE 0 END) DIASINCAPCIDADES,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(354,368) THEN HISTORICOS.VALOR ELSE 0 END) DIASATEP,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(356,357,359) THEN HISTORICOS.VALOR ELSE 0 END) DIASLNR,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(113,116,117,118,115,123) THEN HISTORICOS.VALOR ELSE 0 END
                - CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(116,117)
                        AND '|| UN_FC_SGP_Y_TIPO_T ||' NOT IN (0) THEN HISTORICOS.VALOR ELSE 0 END ) PAGOS,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 112 THEN HISTORICOS.VALOR ELSE 0 END) BASE,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 207 THEN HISTORICOS.VALOR ELSE 0 END) BASESALUD,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 208 THEN HISTORICOS.VALOR ELSE 0 END) DIASSALUD,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 339 THEN HISTORICOS.VALOR ELSE 0 END) DIASCOMISION,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 214 THEN HISTORICOS.VALOR ELSE 0 END) BASECOMISION,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 108 THEN HISTORICOS.VALOR ELSE 0 END) BASEPARAFISCAL,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(9,11) THEN HISTORICOS.VALOR ELSE 0 END) DIAS,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 35  THEN HISTORICOS.VALOR ELSE 0 END) DIASVAC,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 123 THEN HISTORICOS.VALOR ELSE 0 END) UPC,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 486 THEN HISTORICOS.VALOR ELSE 0 END) UPCPATRONO,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(113, 114, 116, 119) THEN HISTORICOS.VALOR ELSE 0 END
                - CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(116,119)
                        AND '|| UN_FC_SGP_Y_TIPO_T ||' NOT IN (0) THEN HISTORICOS.VALOR ELSE 0 END) SALUD,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(117, 118) THEN HISTORICOS.VALOR ELSE 0 END
                - CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(117)
                        AND '|| UN_FC_SGP_Y_TIPO_T ||' NOT IN (0) THEN HISTORICOS.VALOR ELSE 0 END) PENSION,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 115 THEN HISTORICOS.VALOR ELSE 0 END) FSP,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 120 THEN HISTORICOS.VALOR ELSE 0 END) FSPADICIONAL,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 119 THEN HISTORICOS.VALOR ELSE 0 END) FSPPENSIONCAQ,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 124 AND '|| UN_FC_SGP_Y_TIPO_T ||' IN(0) THEN HISTORICOS.VALOR ELSE 0 END) APVOL,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 127 AND '|| UN_FC_SGP_Y_TIPO_T ||' IN(0) THEN HISTORICOS.VALOR ELSE 0 END) APAFC,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 111 AND '|| UN_FC_SGP_Y_TIPO_T ||' IN(0) THEN HISTORICOS.VALOR ELSE 0 END) RIESGOS,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 101 THEN HISTORICOS.VALOR ELSE 0 END) CAJAS,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 102 THEN HISTORICOS.VALOR ELSE 0 END) ICBF,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 103 THEN HISTORICOS.VALOR ELSE 0 END) SENA,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 104 THEN HISTORICOS.VALOR ELSE 0 END) ESAP,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 105 THEN HISTORICOS.VALOR ELSE 0 END) INSTITUTOS,
              V_FONDO_DE_PENSIONES.NIT  NITFONDOPENSION,
              V_FONDO_DE_PENSION_VOL.NIT   NITFONDOPENSION_VOL,
              PERSONAL_HISTORICO.PORCENTAJE_APORTECAJA,
              BASESNOVEDADES.TIPONOVEDAD NOVEDAD_TIPONOVEDAD,
              BASESNOVEDADES.CONSECUTIVO NOVEDAD_CONSECUTIVO,
              BASESNOVEDADES.LLAVENOVEDAD NOVEDAD_LLAVENOVEDAD,
              BASESNOVEDADES.BASE NOVEDAD_BASE,
              BASESNOVEDADES.DIAS NOVEDAD_DIAS,
              BASESNOVEDADES.BASEPROPORCIONAL NOVEDAD_BASEPROPORCIONAL,
              BASESNOVEDADES.FECHAFINAL NOVEDAD_FECHAFINAL,
              BASESNOVEDADES.FECHAINICIAL NOVEDAD_FECHAINICIAL,
            BASESNOVEDADES.APORTEPATRONALSALUD NOVEDAD_APORTEPATRONALSALUD,
            BASESNOVEDADES.APORTEPATRONALPENSION NOVEDAD_APORTEPATRONALPENSION,
            CASE WHEN '''|| UN_REDONDEO ||''' = ''SI'' THEN (BASESNOVEDADES.APORTEEMPLEADOPENSION) ELSE ROUND(BASESNOVEDADES.APORTEEMPLEADOPENSION,-2) END NOVEDAD_APORTEEMPLEADOPENSION,
            CASE WHEN '''|| UN_REDONDEO ||''' = ''SI'' THEN (BASESNOVEDADES.APORTEEMPLEADOSALUD) ELSE ROUND(BASESNOVEDADES.APORTEEMPLEADOSALUD + 49,-2) END NOVEDAD_APORTEEMPLEADOSALUD,
            BASESNOVEDADES.PORC_PENSIONE NOVEDAD_PORC_PENSIONE,
            BASESNOVEDADES.PORC_PENSIONP NOVEDAD_PORC_PENSIONP,
            BASESNOVEDADES.PORC_SALUDE NOVEDAD_PORC_SALUDE,
            BASESNOVEDADES.PORC_SALUDP NOVEDAD_PORC_SALUDP,
            BASESNOVEDADES.PORC_SALUDTOTAL NOVEDAD_PORC_SALUDTOTAL,
            BASESNOVEDADES.FSP NOVEDAD_FSP,
            BASESNOVEDADES.FSPADICIONAL NOVEDAD_FSPADICIONAL,
            BASESNOVEDADES.FSPSUBSISTENCIA NOVEDAD_FSPSUBSISTENCIA,
            PERSONAL_HISTORICO.FACTOR_RIESGO,
              PERSONAL_HISTORICO.RIESGO_PENSION,
              PERSONAL_HISTORICO.ID_CIIU ID_CIIU,
              DECODE(PERSONAL_HISTORICO.ACTIVIDADES_ALTO_RIESGO,0,NULL,PERSONAL_HISTORICO.ACTIVIDADES_ALTO_RIESGO) ACTIVIDADES_ALTO_RIESGO
        FROM HISTORICOS INNER JOIN PERIODOS
          ON HISTORICOS.COMPANIA      = PERIODOS.COMPANIA
         AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
         AND HISTORICOS.ANO            = PERIODOS.ANO
         AND HISTORICOS.MES            = PERIODOS.MES
         AND HISTORICOS.PERIODO        = PERIODOS.PERIODO
        INNER JOIN PERSONAL_HISTORICO
           ON HISTORICOS.COMPANIA        = PERSONAL_HISTORICO.COMPANIA
          AND HISTORICOS.ID_DE_PROCESO  = PERSONAL_HISTORICO.ID_DE_PROCESO
          AND HISTORICOS.ANO             = PERSONAL_HISTORICO.ANO
          AND HISTORICOS.MES             = PERSONAL_HISTORICO.MES
          AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL_HISTORICO.ID_DE_EMPLEADO
        INNER JOIN (
        SELECT         PERIODOS.COMPANIA, PERIODOS.ID_DE_PROCESO, PERIODOS.ANO, PERIODOS.MES, PERIODOS.PERIODO 
            FROM   PERIODOS INNER JOIN BASESNOVEDADES
            ON   PERIODOS.COMPANIA  =  BASESNOVEDADES.COMPANIA
            AND  PERIODOS.ID_DE_PROCESO  =  BASESNOVEDADES.ID_DE_PROCESO
            AND  PERIODOS.ANO  =  BASESNOVEDADES.ANO
            AND  PERIODOS.MES =  BASESNOVEDADES.MES
            AND  PERIODOS.PERIODO  =  BASESNOVEDADES.PERIODO
            AND '''|| UN_NOMINA ||''' = ''ACTIVOS''
                      WHERE  PERIODOS.ACUMULADO NOT IN (0) 
        GROUP BY  PERIODOS.COMPANIA, PERIODOS.ID_DE_PROCESO, PERIODOS.ANO, PERIODOS.MES, PERIODOS.PERIODO 
            UNION
        SELECT         PERIODOS.COMPANIA,10 AS ID_DE_PROCESO, PERIODOS.ANO, PERIODOS.MES, (PERIODOS.PERIODO +50) AS  PERIODO
            FROM   PERIODOS INNER JOIN BASESNOVEDADES
            ON   PERIODOS.COMPANIA  =  BASESNOVEDADES.COMPANIA
            AND  PERIODOS.ID_DE_PROCESO  =  BASESNOVEDADES.ID_DE_PROCESO
            AND  PERIODOS.ANO  =  BASESNOVEDADES.ANO
            AND  PERIODOS.MES =  BASESNOVEDADES.MES
            AND  PERIODOS.PERIODO  =  BASESNOVEDADES.PERIODO
            AND '''|| UN_NOMINA ||''' = ''ACTIVOS''
                     WHERE  PERIODOS.ACUMULADO NOT IN (0) 
        GROUP BY  PERIODOS.COMPANIA, PERIODOS.ID_DE_PROCESO, PERIODOS.ANO, PERIODOS.MES, (PERIODOS.PERIODO +50)
            UNION (SELECT COMPANIA, ID_DE_PROCESO, ANO, MES, MAX(PERIODO) PERIODO
                    FROM (
                    SELECT COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO
                      FROM PERIODOS
                      WHERE DIFERIDOS NOT IN(0) AND ACUMULADO NOT IN(0) 
                      AND '''|| UN_NOMINA ||''' = ''PENSIONADOS''
                      GROUP BY COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO
                    UNION 
                    SELECT COMPANIA, 10 ID_DE_PROCESO, ANO, MES, (PERIODO + 50) PERIODO
                      FROM PERIODOS
                      WHERE DIFERIDOS NOT IN(0) AND ACUMULADO NOT IN(0)      
                      AND '''|| UN_NOMINA ||''' = ''PENSIONADOS''
                      GROUP BY COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO
                    UNION
                    SELECT COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO
                      FROM PERIODOS
                      WHERE ACUMULADO NOT IN(0) 
                      AND DIFERIDOS IN(0)
                AND DIFERENCIASRETROACTIVO IN(0)
                      AND '''|| UN_PAGAR_DIAS ||''' = ''SI''
                      AND '''|| UN_NOMINA ||''' = ''PENSIONADOS''
                      GROUP BY COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO)
                      GROUP BY COMPANIA, ID_DE_PROCESO, ANO, MES) 
                    ) UNICO
          ON PERSONAL_HISTORICO.COMPANIA      = UNICO.COMPANIA
         AND PERSONAL_HISTORICO.ID_DE_PROCESO = UNICO.ID_DE_PROCESO
         AND PERSONAL_HISTORICO.ANO            = UNICO.ANO
         AND PERSONAL_HISTORICO.MES            = UNICO.MES
         AND PERSONAL_HISTORICO.PERIODO        = UNICO.PERIODO
        LEFT JOIN V_FONDO_DE_RIESGOS
          ON PERSONAL_HISTORICO.COMPANIA      = V_FONDO_DE_RIESGOS.COMPANIA
         AND PERSONAL_HISTORICO.FONDO_RIESGOS = V_FONDO_DE_RIESGOS.FONDO_RIESGOS
        LEFT JOIN V_FONDO_DE_SALUD
          ON PERSONAL_HISTORICO.COMPANIA      = V_FONDO_DE_SALUD.COMPANIA
         AND PERSONAL_HISTORICO.FONDO_SALUD   = V_FONDO_DE_SALUD.FONDO_SALUD
        LEFT JOIN V_FONDO_DE_PENSIONES
          ON PERSONAL_HISTORICO.COMPANIA      = V_FONDO_DE_PENSIONES.COMPANIA
         AND PERSONAL_HISTORICO.ID_DEL_FONDO  = V_FONDO_DE_PENSIONES.ID_DEL_FONDO
        INNER JOIN V_FONDO_DE_PENSION_VOL
          ON PERSONAL_HISTORICO.COMPANIA = V_FONDO_DE_PENSION_VOL.COMPANIA
         AND PERSONAL_HISTORICO.FONDO_PENSION_VOL = V_FONDO_DE_PENSION_VOL.ID_DEL_FONDO
        LEFT JOIN V_CAJA_COMPENSACION
          ON PERSONAL_HISTORICO.COMPANIA          = V_CAJA_COMPENSACION.COMPANIA
         AND PERSONAL_HISTORICO.CAJA_COMPENSACION = V_CAJA_COMPENSACION.CAJA_COMPENSACION
        LEFT JOIN COMPANIA
        ON COMPANIA.CODIGO = PERSONAL_HISTORICO.COMPANIA
        LEFT JOIN BASESNOVEDADES
        ON (BASESNOVEDADES.COMPANIA = PERSONAL_HISTORICO.COMPANIA)
        AND (BASESNOVEDADES.ID_DE_EMPLEADO = PERSONAL_HISTORICO.ID_DE_EMPLEADO)
        AND (BASESNOVEDADES.ID_DE_PROCESO = PERSONAL_HISTORICO.ID_DE_PROCESO)
        AND (BASESNOVEDADES.PERIODO = CASE WHEN PERSONAL_HISTORICO.ID_DE_PROCESO = 10 THEN 53 ELSE UNICO.PERIODO END)
        AND (BASESNOVEDADES.ANO = PERSONAL_HISTORICO.ANO)
        AND (BASESNOVEDADES.MES = PERSONAL_HISTORICO.MES)
        WHERE ((PERIODOS.ACUMULADO NOT IN(0) AND PERIODOS.PERIODO < 10)
                OR PERIODOS.ID_DE_PROCESO = 10)
        AND PERIODOS.PERIODO <> 5
        AND (CASE WHEN BASESNOVEDADES.DIAS IS NOT NULL THEN BASESNOVEDADES.DIAS ELSE (CASE WHEN PERSONAL_HISTORICO.ESTADO_ACTUAL = 2 THEN 1 ELSE 0 END) END) > 0
        AND HISTORICOS.COMPANIA = '''|| UN_COMPANIA ||'''
        AND HISTORICOS.ID_DE_PROCESO NOT IN(98)
        AND HISTORICOS.ANO = '|| UN_ANIO ||'
        AND HISTORICOS.MES = '|| UN_MES ||'
        AND (('|| UN_EMPLEADO ||' = 0 AND HISTORICOS.ID_DE_EMPLEADO <> 0) OR ( '|| UN_EMPLEADO ||' <> 0 AND HISTORICOS.ID_DE_EMPLEADO = '|| UN_EMPLEADO ||')) ';


    IF UN_RETROACTIVO <> 0 THEN
        V_SQL := V_SQL || ' AND PERSONAL_HISTORICO.TIPOVINCULACION NOT IN (12,19)  AND NOT (BASESNOVEDADES.TIPONOVEDAD = 3 AND (SUBSTR(BASESNOVEDADES.LLAVENOVEDAD, INSTR(BASESNOVEDADES.LLAVENOVEDAD,''--'',1,4) + 2, LENGTH(BASESNOVEDADES.LLAVENOVEDAD)) NOT IN (''01'',''02'',''03'',''04'',''05'',''10'',''12''))) ';
    ELSE
        V_SQL := V_SQL || ' AND HISTORICOS.ID_DE_PROCESO NOT IN(10) ';
    END IF;

    V_SQL := V_SQL || ' AND (('|| UN_FC_SGP_Y_TIPO_T ||' <> 0 AND PERSONAL_HISTORICO.TIPOVINCULACION = 47) 
         OR (('''|| UN_TIPOPLANILLA ||''' = ''K'' AND PERSONAL_HISTORICO.TIPOVINCULACION = 23 ) OR ('''|| UN_TIPOPLANILLA ||''' <> ''K'' AND PERSONAL_HISTORICO.TIPOVINCULACION <> 23 ))) ';


    V_SQL := V_SQL || ' GROUP BY HISTORICOS.COMPANIA, HISTORICOS.ID_DE_PROCESO, HISTORICOS.ANO, HISTORICOS.MES, PERSONAL_HISTORICO.PERIODO, HISTORICOS.ID_DE_EMPLEADO, PERSONAL_HISTORICO.TIPO_SALARIO, PERSONAL_HISTORICO.ID_DE_TIPO, PERSONAL_HISTORICO.NUMEROPATRONAL, PERSONAL_HISTORICO.APLICA_0862, PERSONAL_HISTORICO.NUMERO_DCTO, PERSONAL_HISTORICO.DCTO_IDENTIDAD, PERSONAL_HISTORICO.APELLIDO1, PERSONAL_HISTORICO.APELLIDO2, PERSONAL_HISTORICO.NOMBRES, PERSONAL_HISTORICO.NOMBRECOMPLETO, PERSONAL_HISTORICO.TIPOVINCULACION, PERSONAL_HISTORICO.FECHA_ULTSUELDO, PERSONAL_HISTORICO.CAJA_COMPENSACION, PERSONAL_HISTORICO.SALARIO_BASE_CATE , PERSONAL_HISTORICO.SALARIO_BASE_IBC , PERSONAL_HISTORICO.FECHA_DE_INGRESO , PERSONAL_HISTORICO.FECHA_DE_RETIRO , PERSONAL_HISTORICO.FECHATERCONTRATO, PERSONAL_HISTORICO.SUBTIPOCOTIZANTE, PERSONAL_HISTORICO.MESADA_PENSIONAL, PERSONAL_HISTORICO.ESTADO_ACTUAL, PERSONAL_HISTORICO.FONDO_SALUD, PERSONAL_HISTORICO.ID_DEL_FONDO, PERSONAL_HISTORICO.FONDO_RIESGOS, PERSONAL_HISTORICO.CAJA_COMPENSACION, PERSONAL_HISTORICO.PENSIONCOMPARTIDA, PERSONAL_HISTORICO.COLOMBIANO_RESIDENTE, PERSONAL_HISTORICO.APELLIDO1CAUSANTE, PERSONAL_HISTORICO.APELLIDO2CAUSANTE, PERSONAL_HISTORICO.NOMBRE1CAUSANTE, PERSONAL_HISTORICO.NOMBRE2CAUSANTE, PERSONAL_HISTORICO.DCTO_IDENTIDADCAUSANTE, PERSONAL_HISTORICO.FECHA_CAMBIO_MESADA, PERSONAL_HISTORICO.NUMERO_DCTOCAUSANTE, PERSONAL_HISTORICO.TIPOPENSIONADO, PERSONAL_HISTORICO.DEPARTAMENTO_HAB, PERSONAL_HISTORICO.CIUDAD_HAB, V_FONDO_DE_RIESGOS.CODAIARP , V_FONDO_DE_SALUD.CODAIFS, V_FONDO_DE_PENSIONES.CODAIFP, V_CAJA_COMPENSACION.CODAICC, V_CAJA_COMPENSACION.DEPARTAMENTOC, V_CAJA_COMPENSACION.CIUDADC, 
              CASE WHEN PERSONAL_HISTORICO.DEPARTAMENTO_LABORA IS NULL OR PERSONAL_HISTORICO.DEPARTAMENTO_LABORA =''00'' THEN PERSONAL_HISTORICO.DEPARTAMENTO_HAB ELSE PERSONAL_HISTORICO.DEPARTAMENTO_LABORA END,
              CASE WHEN PERSONAL_HISTORICO.CIUDAD_LABORA IS NULL OR PERSONAL_HISTORICO.CIUDAD_LABORA =''00'' THEN PERSONAL_HISTORICO.CIUDAD_HAB ELSE PERSONAL_HISTORICO.CIUDAD_LABORA END,
              V_FONDO_DE_PENSIONES.NIT, V_FONDO_DE_PENSION_VOL.NIT, PERSONAL_HISTORICO.PORCENTAJE_APORTECAJA, BASESNOVEDADES.TIPONOVEDAD, BASESNOVEDADES.CONSECUTIVO, BASESNOVEDADES.LLAVENOVEDAD, BASESNOVEDADES.BASE, BASESNOVEDADES.DIAS, BASESNOVEDADES.BASEPROPORCIONAL, BASESNOVEDADES.FECHAFINAL, BASESNOVEDADES.FECHAINICIAL, BASESNOVEDADES.APORTEPATRONALSALUD, BASESNOVEDADES.APORTEPATRONALPENSION, 
              CASE WHEN '''|| UN_REDONDEO ||''' = ''SI'' THEN (BASESNOVEDADES.APORTEEMPLEADOPENSION) ELSE ROUND(BASESNOVEDADES.APORTEEMPLEADOPENSION,-2) END,
              CASE WHEN '''|| UN_REDONDEO ||''' = ''SI'' THEN (BASESNOVEDADES.APORTEEMPLEADOSALUD) ELSE ROUND(BASESNOVEDADES.APORTEEMPLEADOSALUD + 49,-2) END, 
              BASESNOVEDADES.PORC_PENSIONE, BASESNOVEDADES.PORC_PENSIONP, BASESNOVEDADES.PORC_SALUDE, BASESNOVEDADES.PORC_SALUDP, BASESNOVEDADES.PORC_SALUDTOTAL, BASESNOVEDADES.FSP, BASESNOVEDADES.FSPADICIONAL, BASESNOVEDADES.FSPSUBSISTENCIA, PERSONAL_HISTORICO.FACTOR_RIESGO, PERSONAL_HISTORICO.RIESGO_PENSION, PERSONAL_HISTORICO.ID_CIIU, PERSONAL_HISTORICO.ACTIVIDADES_ALTO_RIESGO ';


    IF UN_ORDEN = 1 THEN
        V_SQL := V_SQL || ' ORDER BY APELLIDO1, APELLIDO2, FECHA_DE_INGRESO, ID_DE_EMPLEADO, NOMBRES ';
    ELSE
        V_SQL := V_SQL || ' ORDER BY NUMERO_DCTO, FECHA_DE_INGRESO, ID_DE_EMPLEADO, NOMBRES ';
    END IF;

    IF UN_RETROACTIVO <> 0 THEN
        V_SQL := V_SQL || ' , NOVEDAD_TIPONOVEDAD, NOVEDAD_CONSECUTIVO, ID_DE_PROCESO ';
    END IF;

    RETURN V_SQL;

END FC_ARMARVSOI;

FUNCTION FC_ARMARVSOIACUM
(
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_EMPLEADO         IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_REDONDEO         IN VARCHAR2,
    UN_ANIO             IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES              IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO          IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_NOMINA           IN VARCHAR2,
    UN_PAGAR_DIAS       IN VARCHAR2,
    UN_FC_SGP_Y_TIPO_T  IN NUMBER,
    UN_RETROACTIVO      IN NUMBER,
    UN_TIPOPLANILLA     IN VARCHAR2,
    UN_ORDEN            IN NUMBER
) RETURN VARCHAR2  
AS  
    V_SQL VARCHAR2(32767); 
BEGIN  

    V_SQL := 'SELECT HISTORICOS.COMPANIA,
              HISTORICOS.ID_DE_PROCESO,
              HISTORICOS.ANO,
              HISTORICOS.MES,
              PERSONAL_HISTORICO.TIPO_SALARIO,
              PERSONAL_HISTORICO.PERIODO,
              HISTORICOS.ID_DE_EMPLEADO,
              PERSONAL_HISTORICO.ID_DE_TIPO,
              PERSONAL_HISTORICO.NUMEROPATRONAL,
              PERSONAL_HISTORICO.APLICA_0862,
              PERSONAL_HISTORICO.NUMERO_DCTO,
              PERSONAL_HISTORICO.DCTO_IDENTIDAD,
              PERSONAL_HISTORICO.APELLIDO1,
              PERSONAL_HISTORICO.APELLIDO2,
              PERSONAL_HISTORICO.NOMBRES,
              PERSONAL_HISTORICO.NOMBRECOMPLETO,
              PERSONAL_HISTORICO.TIPOVINCULACION,
              PERSONAL_HISTORICO.FECHA_ULTSUELDO,
              PERSONAL_HISTORICO.SALARIO_BASE_CATE SALARIO_BASE,
              PERSONAL_HISTORICO.SALARIO_BASE_IBC  SALARIO_BASE_IBC,
              PERSONAL_HISTORICO.FECHA_DE_INGRESO ,
              PERSONAL_HISTORICO.FECHA_DE_RETIRO  ,
              PERSONAL_HISTORICO.FECHATERCONTRATO,
              PERSONAL_HISTORICO.SUBTIPOCOTIZANTE ,
              PERSONAL_HISTORICO.MESADA_PENSIONAL,
              PERSONAL_HISTORICO.ESTADO_ACTUAL,
              PERSONAL_HISTORICO.FONDO_SALUD,
              PERSONAL_HISTORICO.ID_DEL_FONDO,
              PERSONAL_HISTORICO.FONDO_RIESGOS,
              PERSONAL_HISTORICO.CAJA_COMPENSACION,
              PERSONAL_HISTORICO.PENSIONCOMPARTIDA,
              PERSONAL_HISTORICO.COLOMBIANO_RESIDENTE,
              PERSONAL_HISTORICO.APELLIDO1CAUSANTE,
              PERSONAL_HISTORICO.APELLIDO2CAUSANTE,
              PERSONAL_HISTORICO.NOMBRE1CAUSANTE,
              PERSONAL_HISTORICO.NOMBRE2CAUSANTE,
              PERSONAL_HISTORICO.DCTO_IDENTIDADCAUSANTE,
              PERSONAL_HISTORICO.FECHA_CAMBIO_MESADA,
              PERSONAL_HISTORICO.NUMERO_DCTOCAUSANTE,
              PERSONAL_HISTORICO.TIPOPENSIONADO,
              PERSONAL_HISTORICO.DEPARTAMENTO_HAB,
              PERSONAL_HISTORICO.CIUDAD_HAB,
              CASE WHEN PERSONAL_HISTORICO.DEPARTAMENTO_LABORA IS NULL
                     OR PERSONAL_HISTORICO.DEPARTAMENTO_LABORA =''00''
              THEN PERSONAL_HISTORICO.DEPARTAMENTO_HAB
              ELSE PERSONAL_HISTORICO.DEPARTAMENTO_LABORA
              END DPTO_LAB,
              CASE WHEN PERSONAL_HISTORICO.CIUDAD_LABORA IS NULL
                     OR PERSONAL_HISTORICO.CIUDAD_LABORA =''00''
              THEN PERSONAL_HISTORICO.CIUDAD_HAB
              ELSE PERSONAL_HISTORICO.CIUDAD_LABORA
              END CIUDAD_LAB,
              V_FONDO_DE_RIESGOS.CODAIARP    FONDO_RIESGOS_SOI ,
              V_FONDO_DE_SALUD.CODAIFS       FONDO_SALUD_SOI,
              V_FONDO_DE_PENSIONES.CODAIFP   ID_DEL_FONDO_SOI,
              V_CAJA_COMPENSACION.CODAICC    CAJA_COMPENSACION_SOI,
              V_CAJA_COMPENSACION.DEPARTAMENTOC,
              V_CAJA_COMPENSACION.CIUDADC,
              NULL NUM_AUTO_INCAP,
              0 VR_INCAP,
              NULL NUM_AUTO_MAT,
              0 VR_MAT,
              CASE WHEN SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =212 THEN HISTORICOS.VALOR ELSE 0 END) >= NVL(SUM(PERIODORETRO.BASERIESGOS),0) THEN SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =212 THEN HISTORICOS.VALOR ELSE 0 END)  ELSE NVL(SUM(PERIODORETRO.BASERIESGOS),0) END BASERIESGOS,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 209 THEN HISTORICOS.VALOR ELSE 0 END) DIASRIESGOS,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =220 THEN HISTORICOS.VALOR ELSE 0 END) + NVL(SUM(PERIODORETRO.BASECAJAS),0) BASECAJAS,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 347 THEN HISTORICOS.VALOR ELSE 0 END) DIASINCAPCIDADES,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(354,368) THEN HISTORICOS.VALOR ELSE 0 END) DIASATEP,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(356,357,359) THEN HISTORICOS.VALOR ELSE 0 END) DIASLNR,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(113,116,117,118,115,123) THEN HISTORICOS.VALOR ELSE 0 END
                - CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(116,117)
                        AND '|| UN_FC_SGP_Y_TIPO_T ||' NOT IN (0) THEN HISTORICOS.VALOR ELSE 0 END ) PAGOS,
              NVL(SUM(PERIODORETRO.BASE),0) BASE,
              NVL(SUM(PERIODORETRO.BASESALUD),0) BASESALUD,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 208 THEN HISTORICOS.VALOR ELSE 0 END) DIASSALUD,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 339 THEN HISTORICOS.VALOR ELSE 0 END) DIASCOMISION,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 214 THEN HISTORICOS.VALOR ELSE 0 END) BASECOMISION,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =108 THEN HISTORICOS.VALOR ELSE 0 END)+ NVL(SUM(PERIODORETRO.BASEPARAFISCAL),0)   BASEPARAFISCAL, 
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(9,11) THEN HISTORICOS.VALOR ELSE 0 END) DIAS,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 35  THEN HISTORICOS.VALOR ELSE 0 END) DIASVAC,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 123 THEN HISTORICOS.VALOR ELSE 0 END) UPC,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 486 THEN HISTORICOS.VALOR ELSE 0 END) UPCPATRONO,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(113, 114, 116, 119) THEN HISTORICOS.VALOR ELSE 0 END
                - CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(116,119)
                        AND '|| UN_FC_SGP_Y_TIPO_T ||' NOT IN (0) THEN HISTORICOS.VALOR ELSE 0 END)  + NVL(SUM(PERIODORETRO.SALUD),0) SALUD,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(117, 118) THEN HISTORICOS.VALOR ELSE 0 END
                - CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(117)
                        AND '|| UN_FC_SGP_Y_TIPO_T ||' NOT IN (0) THEN HISTORICOS.VALOR ELSE 0 END) + NVL(SUM(PERIODORETRO.PENSION),0) PENSION,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 115 THEN HISTORICOS.VALOR ELSE 0 END) + NVL(SUM(PERIODORETRO.FSP),0) FSP,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 120 THEN HISTORICOS.VALOR ELSE 0 END) FSPADICIONAL,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 119 THEN HISTORICOS.VALOR ELSE 0 END) FSPPENSIONCAQ,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 124 AND '|| UN_FC_SGP_Y_TIPO_T ||' IN(0) THEN HISTORICOS.VALOR ELSE 0 END) APVOL,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 127 AND '|| UN_FC_SGP_Y_TIPO_T ||' IN(0) THEN HISTORICOS.VALOR ELSE 0 END) APAFC,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 111 AND '|| UN_FC_SGP_Y_TIPO_T ||' IN(0) THEN HISTORICOS.VALOR ELSE 0 END) + NVL(SUM(PERIODORETRO.RIESGOS),0) RIESGOS,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =101 THEN HISTORICOS.VALOR ELSE 0 END)+ NVL(SUM(PERIODORETRO.CAJA),0) CAJAS,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =102 THEN HISTORICOS.VALOR ELSE 0 END) + NVL(SUM(PERIODORETRO.ICBF),0) ICBF,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =103 THEN HISTORICOS.VALOR ELSE 0 END) + NVL(SUM(PERIODORETRO.SENA),0) SENA,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =104 THEN HISTORICOS.VALOR ELSE 0 END) +  NVL(SUM(PERIODORETRO.ESAP),0) ESAP,
              SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =105 THEN HISTORICOS.VALOR ELSE 0 END) + NVL(SUM(PERIODORETRO.INSTITUTOS),0) INSTITUTOS,
              V_FONDO_DE_PENSIONES.NIT  NITFONDOPENSION,
              V_FONDO_DE_PENSION_VOL.NIT   NITFONDOPENSION_VOL,
              PERSONAL_HISTORICO.PORCENTAJE_APORTECAJA,
              BASESNOVEDADES.TIPONOVEDAD NOVEDAD_TIPONOVEDAD,
              BASESNOVEDADES.CONSECUTIVO NOVEDAD_CONSECUTIVO,
              BASESNOVEDADES.LLAVENOVEDAD NOVEDAD_LLAVENOVEDAD,
              BASESNOVEDADES.BASE NOVEDAD_BASE,
              BASESNOVEDADES.DIAS NOVEDAD_DIAS,
              BASESNOVEDADES.BASEPROPORCIONAL NOVEDAD_BASEPROPORCIONAL,
              BASESNOVEDADES.FECHAFINAL NOVEDAD_FECHAFINAL,
              BASESNOVEDADES.FECHAINICIAL NOVEDAD_FECHAINICIAL,
            BASESNOVEDADES.APORTEPATRONALSALUD NOVEDAD_APORTEPATRONALSALUD,
            BASESNOVEDADES.APORTEPATRONALPENSION NOVEDAD_APORTEPATRONALPENSION,
            CASE WHEN '''|| UN_REDONDEO ||''' = ''SI'' THEN (BASESNOVEDADES.APORTEEMPLEADOPENSION) ELSE ROUND(BASESNOVEDADES.APORTEEMPLEADOPENSION,-2) END NOVEDAD_APORTEEMPLEADOPENSION,
            CASE WHEN '''|| UN_REDONDEO ||''' = ''SI'' THEN (BASESNOVEDADES.APORTEEMPLEADOSALUD) ELSE ROUND(BASESNOVEDADES.APORTEEMPLEADOSALUD + 49,-2) END NOVEDAD_APORTEEMPLEADOSALUD,
            BASESNOVEDADES.PORC_PENSIONE NOVEDAD_PORC_PENSIONE,
            BASESNOVEDADES.PORC_PENSIONP NOVEDAD_PORC_PENSIONP,
            BASESNOVEDADES.PORC_SALUDE NOVEDAD_PORC_SALUDE,
            BASESNOVEDADES.PORC_SALUDP NOVEDAD_PORC_SALUDP,
            BASESNOVEDADES.PORC_SALUDTOTAL NOVEDAD_PORC_SALUDTOTAL,
            BASESNOVEDADES.FSP NOVEDAD_FSP,
            BASESNOVEDADES.FSPADICIONAL NOVEDAD_FSPADICIONAL,
            BASESNOVEDADES.FSPSUBSISTENCIA NOVEDAD_FSPSUBSISTENCIA,
            PERSONAL_HISTORICO.FACTOR_RIESGO,
              PERSONAL_HISTORICO.RIESGO_PENSION,
              PERSONAL_HISTORICO.ID_CIIU ID_CIIU,
              DECODE(PERSONAL_HISTORICO.ACTIVIDADES_ALTO_RIESGO,0,NULL,PERSONAL_HISTORICO.ACTIVIDADES_ALTO_RIESGO) ACTIVIDADES_ALTO_RIESGO
        FROM HISTORICOS INNER JOIN PERIODOS
          ON HISTORICOS.COMPANIA      = PERIODOS.COMPANIA
         AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
         AND HISTORICOS.ANO            = PERIODOS.ANO
         AND HISTORICOS.MES            = PERIODOS.MES
         AND HISTORICOS.PERIODO        = PERIODOS.PERIODO
        INNER JOIN PERSONAL_HISTORICO
           ON HISTORICOS.COMPANIA        = PERSONAL_HISTORICO.COMPANIA
          AND HISTORICOS.ID_DE_PROCESO  = PERSONAL_HISTORICO.ID_DE_PROCESO
          AND HISTORICOS.ANO             = PERSONAL_HISTORICO.ANO
          AND HISTORICOS.MES             = PERSONAL_HISTORICO.MES
          AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL_HISTORICO.ID_DE_EMPLEADO
          LEFT JOIN ((SELECT    
      HISTORICOS.ID_DE_CONCEPTO,
      NVL(SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =108 THEN HISTORICOS.VALOR ELSE 0 END),0) BASEPARAFISCAL,
      NVL(SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =101 THEN HISTORICOS.VALOR ELSE 0 END),0) CAJA,
      NVL(SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =207 THEN HISTORICOS.VALOR ELSE 0 END),0) BASESALUD,
      NVL(SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =112 THEN HISTORICOS.VALOR ELSE 0 END),0) BASE, 
      NVL(SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =212 THEN HISTORICOS.VALOR ELSE 0 END),0) BASERIESGOS,
      NVL(SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =220 THEN HISTORICOS.VALOR ELSE 0 END),0) BASECAJAS,
      NVL(SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =111 THEN HISTORICOS.VALOR ELSE 0 END),0) RIESGOS,
      NVL(SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(113, 114, 116, 119) THEN HISTORICOS.VALOR ELSE 0 END
        - CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(116,119)
                AND PCK_NOMINA.FC_SGP_Y_TIPO_T NOT IN (0) THEN HISTORICOS.VALOR ELSE 0 END),0) SALUD,
      NVL(SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(117, 118)     THEN HISTORICOS.VALOR ELSE 0 END
        - CASE WHEN HISTORICOS.ID_DE_CONCEPTO IN(117)
                AND PCK_NOMINA.FC_SGP_Y_TIPO_T NOT IN (0) THEN HISTORICOS.VALOR ELSE 0 END),0) PENSION,
      NVL(SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =115 THEN HISTORICOS.VALOR ELSE 0 END),0) FSP,
            SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =102 THEN HISTORICOS.VALOR ELSE 0 END) ICBF,
      SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =103 THEN HISTORICOS.VALOR ELSE 0 END) SENA,
      SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =104 THEN HISTORICOS.VALOR ELSE 0 END) ESAP,
      SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO =105 THEN HISTORICOS.VALOR ELSE 0 END) INSTITUTOS,
      HISTORICOS.COMPANIA       , HISTORICOS.ID_DE_PROCESO  , 
      HISTORICOS.ANO            , HISTORICOS.MES            , 
      HISTORICOS.ID_DE_EMPLEADO 
    FROM   PERIODOS inner join PERSONAL_HISTORICO
     ON PERSONAL_HISTORICO.COMPANIA      = PERIODOS.COMPANIA
 AND PERSONAL_HISTORICO.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
 AND PERSONAL_HISTORICO.ANO           = PERIODOS.ANO
 AND PERSONAL_HISTORICO.MES           = PERIODOS.MES
 AND PERSONAL_HISTORICO.PERIODO       = PERIODOS.PERIODO
 INNER JOIN HISTORICOS
ON HISTORICOS.COMPANIA       = PERSONAL_HISTORICO.COMPANIA
AND HISTORICOS.ID_DE_PROCESO  = PERSONAL_HISTORICO.ID_DE_PROCESO
AND HISTORICOS.ANO            = PERSONAL_HISTORICO.ANO
AND HISTORICOS.MES            = PERSONAL_HISTORICO.MES
AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL_HISTORICO.ID_DE_EMPLEADO
AND HISTORICOS.PERIODO = PERIODOS.PERIODO
    WHERE  PERIODOS.ACUMULADO NOT IN (0)
    AND PERIODOS.PERIODO = 5 
    AND '''|| UN_NOMINA ||''' = ''ACTIVOS''
GROUP BY  PERIODOS.COMPANIA, PERIODOS.ID_DE_PROCESO, PERIODOS.ANO, PERIODOS.MES, PERIODOS.PERIODO, PERSONAL_HISTORICO.ID_DE_EMPLEADO,HISTORICOS.COMPANIA , HISTORICOS.ID_DE_PROCESO , 
      HISTORICOS.ANO , HISTORICOS.MES  , HISTORICOS.ID_DE_EMPLEADO , HISTORICOS.PERIODO, HISTORICOS.ID_DE_CONCEPTO) ) PERIODORETRO
      ON HISTORICOS.COMPANIA       = PERIODORETRO.COMPANIA
AND HISTORICOS.ID_DE_PROCESO  = PERIODORETRO.ID_DE_PROCESO
AND HISTORICOS.ANO            = PERIODORETRO.ANO
AND HISTORICOS.MES            = PERIODORETRO.MES
AND HISTORICOS.ID_DE_EMPLEADO = PERIODORETRO.ID_DE_EMPLEADO
AND HISTORICOS.PERIODO = PERIODOS.PERIODO
AND HISTORICOS.ID_DE_CONCEPTO = PERIODORETRO.ID_DE_CONCEPTO
        INNER JOIN (
        SELECT         PERIODOS.COMPANIA, PERIODOS.ID_DE_PROCESO, PERIODOS.ANO, PERIODOS.MES, PERIODOS.PERIODO 
            FROM   PERIODOS INNER JOIN BASESNOVEDADES
            ON   PERIODOS.COMPANIA  =  BASESNOVEDADES.COMPANIA
            AND  PERIODOS.ID_DE_PROCESO  =  BASESNOVEDADES.ID_DE_PROCESO
            AND  PERIODOS.ANO  =  BASESNOVEDADES.ANO
            AND  PERIODOS.MES =  BASESNOVEDADES.MES
            AND  PERIODOS.PERIODO  =  BASESNOVEDADES.PERIODO
            AND '''|| UN_NOMINA ||''' = ''ACTIVOS''
                      WHERE  PERIODOS.ACUMULADO NOT IN (0) 
        GROUP BY  PERIODOS.COMPANIA, PERIODOS.ID_DE_PROCESO, PERIODOS.ANO, PERIODOS.MES, PERIODOS.PERIODO 
            UNION
        SELECT         PERIODOS.COMPANIA,10 AS ID_DE_PROCESO, PERIODOS.ANO, PERIODOS.MES, (PERIODOS.PERIODO +50) AS  PERIODO
            FROM   PERIODOS INNER JOIN BASESNOVEDADES
            ON   PERIODOS.COMPANIA  =  BASESNOVEDADES.COMPANIA
            AND  PERIODOS.ID_DE_PROCESO  =  BASESNOVEDADES.ID_DE_PROCESO
            AND  PERIODOS.ANO  =  BASESNOVEDADES.ANO
            AND  PERIODOS.MES =  BASESNOVEDADES.MES
            AND  PERIODOS.PERIODO  =  BASESNOVEDADES.PERIODO
            AND '''|| UN_NOMINA ||''' = ''ACTIVOS''
                     WHERE  PERIODOS.ACUMULADO NOT IN (0) 
        GROUP BY  PERIODOS.COMPANIA, PERIODOS.ID_DE_PROCESO, PERIODOS.ANO, PERIODOS.MES, (PERIODOS.PERIODO +50)
            UNION (SELECT COMPANIA, ID_DE_PROCESO, ANO, MES, MAX(PERIODO) PERIODO
                    FROM (
                    SELECT COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO
                      FROM PERIODOS
                      WHERE DIFERIDOS NOT IN(0) AND ACUMULADO NOT IN(0) 
                      AND '''|| UN_NOMINA ||''' = ''PENSIONADOS''
                      GROUP BY COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO
                    UNION 
                    SELECT COMPANIA, 10 ID_DE_PROCESO, ANO, MES, (PERIODO + 50) PERIODO
                      FROM PERIODOS
                      WHERE DIFERIDOS NOT IN(0) AND ACUMULADO NOT IN(0)      
                      AND '''|| UN_NOMINA ||''' = ''PENSIONADOS''
                      GROUP BY COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO
                    UNION
                    SELECT COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO
                      FROM PERIODOS
                      WHERE ACUMULADO NOT IN(0) 
                      AND DIFERIDOS IN(0)
                AND DIFERENCIASRETROACTIVO IN(0)
                      AND '''|| UN_PAGAR_DIAS ||''' = ''SI''
                      AND '''|| UN_NOMINA ||''' = ''PENSIONADOS''
                      GROUP BY COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO)
                      GROUP BY COMPANIA, ID_DE_PROCESO, ANO, MES) 
                    ) UNICO
          ON PERSONAL_HISTORICO.COMPANIA      = UNICO.COMPANIA
         AND PERSONAL_HISTORICO.ID_DE_PROCESO = UNICO.ID_DE_PROCESO
         AND PERSONAL_HISTORICO.ANO            = UNICO.ANO
         AND PERSONAL_HISTORICO.MES            = UNICO.MES
         AND PERSONAL_HISTORICO.PERIODO        = UNICO.PERIODO
        LEFT JOIN V_FONDO_DE_RIESGOS
          ON PERSONAL_HISTORICO.COMPANIA      = V_FONDO_DE_RIESGOS.COMPANIA
         AND PERSONAL_HISTORICO.FONDO_RIESGOS = V_FONDO_DE_RIESGOS.FONDO_RIESGOS
        LEFT JOIN V_FONDO_DE_SALUD
          ON PERSONAL_HISTORICO.COMPANIA      = V_FONDO_DE_SALUD.COMPANIA
         AND PERSONAL_HISTORICO.FONDO_SALUD   = V_FONDO_DE_SALUD.FONDO_SALUD
        LEFT JOIN V_FONDO_DE_PENSIONES
          ON PERSONAL_HISTORICO.COMPANIA      = V_FONDO_DE_PENSIONES.COMPANIA
         AND PERSONAL_HISTORICO.ID_DEL_FONDO  = V_FONDO_DE_PENSIONES.ID_DEL_FONDO
        INNER JOIN V_FONDO_DE_PENSION_VOL
          ON PERSONAL_HISTORICO.COMPANIA = V_FONDO_DE_PENSION_VOL.COMPANIA
         AND PERSONAL_HISTORICO.FONDO_PENSION_VOL = V_FONDO_DE_PENSION_VOL.ID_DEL_FONDO
        LEFT JOIN V_CAJA_COMPENSACION
          ON PERSONAL_HISTORICO.COMPANIA          = V_CAJA_COMPENSACION.COMPANIA
         AND PERSONAL_HISTORICO.CAJA_COMPENSACION = V_CAJA_COMPENSACION.CAJA_COMPENSACION
        LEFT JOIN COMPANIA
        ON COMPANIA.CODIGO = PERSONAL_HISTORICO.COMPANIA
        LEFT JOIN BASESNOVEDADES
        ON (BASESNOVEDADES.COMPANIA = PERSONAL_HISTORICO.COMPANIA)
        AND (BASESNOVEDADES.ID_DE_EMPLEADO = PERSONAL_HISTORICO.ID_DE_EMPLEADO)
        AND (BASESNOVEDADES.ID_DE_PROCESO = PERSONAL_HISTORICO.ID_DE_PROCESO)
        AND (BASESNOVEDADES.PERIODO = CASE WHEN PERSONAL_HISTORICO.ID_DE_PROCESO = 10 THEN 53 ELSE UNICO.PERIODO END)
        AND (BASESNOVEDADES.ANO = PERSONAL_HISTORICO.ANO)
        AND (BASESNOVEDADES.MES = PERSONAL_HISTORICO.MES)
        WHERE ((PERIODOS.ACUMULADO NOT IN(0) AND PERIODOS.PERIODO < 10)
                OR PERIODOS.ID_DE_PROCESO = 10)
        AND PERIODOS.PERIODO <> 5
        AND (CASE WHEN BASESNOVEDADES.DIAS IS NOT NULL THEN BASESNOVEDADES.DIAS ELSE (CASE WHEN PERSONAL_HISTORICO.ESTADO_ACTUAL = 2 THEN 1 ELSE 0 END) END) > 0
        AND HISTORICOS.COMPANIA = '''|| UN_COMPANIA ||'''
        AND HISTORICOS.ID_DE_PROCESO NOT IN(98)
        AND HISTORICOS.ANO = '|| UN_ANIO ||'
        AND HISTORICOS.MES = '|| UN_MES ||'
        AND (('|| UN_EMPLEADO ||' = 0 AND HISTORICOS.ID_DE_EMPLEADO <> 0) OR ( '|| UN_EMPLEADO ||' <> 0 AND HISTORICOS.ID_DE_EMPLEADO = '|| UN_EMPLEADO ||')) ';


    IF UN_RETROACTIVO <> 0 THEN
        V_SQL := V_SQL || ' AND PERSONAL_HISTORICO.TIPOVINCULACION NOT IN (12,19)  AND NOT (BASESNOVEDADES.TIPONOVEDAD = 3 AND (SUBSTR(BASESNOVEDADES.LLAVENOVEDAD, INSTR(BASESNOVEDADES.LLAVENOVEDAD,''--'',1,4) + 2, LENGTH(BASESNOVEDADES.LLAVENOVEDAD)) NOT IN (''01'',''02'',''03'',''04'',''05'',''10'',''12''))) ';
    ELSE
        V_SQL := V_SQL || ' AND HISTORICOS.ID_DE_PROCESO NOT IN(10) ';
    END IF;

    V_SQL := V_SQL || ' AND (('|| UN_FC_SGP_Y_TIPO_T ||' <> 0 AND PERSONAL_HISTORICO.TIPOVINCULACION = 47) 
         OR (('''|| UN_TIPOPLANILLA ||''' = ''K'' AND PERSONAL_HISTORICO.TIPOVINCULACION = 23 ) OR ('''|| UN_TIPOPLANILLA ||''' <> ''K'' AND PERSONAL_HISTORICO.TIPOVINCULACION <> 23 ))) ';


    V_SQL := V_SQL || ' GROUP BY HISTORICOS.COMPANIA, HISTORICOS.ID_DE_PROCESO, HISTORICOS.ANO, HISTORICOS.MES, PERSONAL_HISTORICO.PERIODO, HISTORICOS.ID_DE_EMPLEADO, PERSONAL_HISTORICO.TIPO_SALARIO, PERSONAL_HISTORICO.ID_DE_TIPO, PERSONAL_HISTORICO.NUMEROPATRONAL, PERSONAL_HISTORICO.APLICA_0862, PERSONAL_HISTORICO.NUMERO_DCTO, PERSONAL_HISTORICO.DCTO_IDENTIDAD, PERSONAL_HISTORICO.APELLIDO1, PERSONAL_HISTORICO.APELLIDO2, PERSONAL_HISTORICO.NOMBRES, PERSONAL_HISTORICO.NOMBRECOMPLETO, PERSONAL_HISTORICO.TIPOVINCULACION, PERSONAL_HISTORICO.FECHA_ULTSUELDO, PERSONAL_HISTORICO.CAJA_COMPENSACION, PERSONAL_HISTORICO.SALARIO_BASE_CATE , PERSONAL_HISTORICO.SALARIO_BASE_IBC , PERSONAL_HISTORICO.FECHA_DE_INGRESO , PERSONAL_HISTORICO.FECHA_DE_RETIRO , PERSONAL_HISTORICO.FECHATERCONTRATO, PERSONAL_HISTORICO.SUBTIPOCOTIZANTE, PERSONAL_HISTORICO.MESADA_PENSIONAL, PERSONAL_HISTORICO.ESTADO_ACTUAL, PERSONAL_HISTORICO.FONDO_SALUD, PERSONAL_HISTORICO.ID_DEL_FONDO, PERSONAL_HISTORICO.FONDO_RIESGOS, PERSONAL_HISTORICO.CAJA_COMPENSACION, PERSONAL_HISTORICO.PENSIONCOMPARTIDA, PERSONAL_HISTORICO.COLOMBIANO_RESIDENTE, PERSONAL_HISTORICO.APELLIDO1CAUSANTE, PERSONAL_HISTORICO.APELLIDO2CAUSANTE, PERSONAL_HISTORICO.NOMBRE1CAUSANTE, PERSONAL_HISTORICO.NOMBRE2CAUSANTE, PERSONAL_HISTORICO.DCTO_IDENTIDADCAUSANTE, PERSONAL_HISTORICO.FECHA_CAMBIO_MESADA, PERSONAL_HISTORICO.NUMERO_DCTOCAUSANTE, PERSONAL_HISTORICO.TIPOPENSIONADO, PERSONAL_HISTORICO.DEPARTAMENTO_HAB, PERSONAL_HISTORICO.CIUDAD_HAB, V_FONDO_DE_RIESGOS.CODAIARP , V_FONDO_DE_SALUD.CODAIFS, V_FONDO_DE_PENSIONES.CODAIFP, V_CAJA_COMPENSACION.CODAICC, V_CAJA_COMPENSACION.DEPARTAMENTOC, V_CAJA_COMPENSACION.CIUDADC, 
              CASE WHEN PERSONAL_HISTORICO.DEPARTAMENTO_LABORA IS NULL OR PERSONAL_HISTORICO.DEPARTAMENTO_LABORA =''00'' THEN PERSONAL_HISTORICO.DEPARTAMENTO_HAB ELSE PERSONAL_HISTORICO.DEPARTAMENTO_LABORA END,
              CASE WHEN PERSONAL_HISTORICO.CIUDAD_LABORA IS NULL OR PERSONAL_HISTORICO.CIUDAD_LABORA =''00'' THEN PERSONAL_HISTORICO.CIUDAD_HAB ELSE PERSONAL_HISTORICO.CIUDAD_LABORA END,
              V_FONDO_DE_PENSIONES.NIT, V_FONDO_DE_PENSION_VOL.NIT, PERSONAL_HISTORICO.PORCENTAJE_APORTECAJA, BASESNOVEDADES.TIPONOVEDAD, BASESNOVEDADES.CONSECUTIVO, BASESNOVEDADES.LLAVENOVEDAD, BASESNOVEDADES.BASE, BASESNOVEDADES.DIAS, BASESNOVEDADES.BASEPROPORCIONAL, BASESNOVEDADES.FECHAFINAL, BASESNOVEDADES.FECHAINICIAL, BASESNOVEDADES.APORTEPATRONALSALUD, BASESNOVEDADES.APORTEPATRONALPENSION, 
              CASE WHEN '''|| UN_REDONDEO ||''' = ''SI'' THEN (BASESNOVEDADES.APORTEEMPLEADOPENSION) ELSE ROUND(BASESNOVEDADES.APORTEEMPLEADOPENSION,-2) END,
              CASE WHEN '''|| UN_REDONDEO ||''' = ''SI'' THEN (BASESNOVEDADES.APORTEEMPLEADOSALUD) ELSE ROUND(BASESNOVEDADES.APORTEEMPLEADOSALUD + 49,-2) END, 
              BASESNOVEDADES.PORC_PENSIONE, BASESNOVEDADES.PORC_PENSIONP, BASESNOVEDADES.PORC_SALUDE, BASESNOVEDADES.PORC_SALUDP, BASESNOVEDADES.PORC_SALUDTOTAL, BASESNOVEDADES.FSP, BASESNOVEDADES.FSPADICIONAL, BASESNOVEDADES.FSPSUBSISTENCIA, PERSONAL_HISTORICO.FACTOR_RIESGO, PERSONAL_HISTORICO.RIESGO_PENSION, PERSONAL_HISTORICO.ID_CIIU, PERSONAL_HISTORICO.ACTIVIDADES_ALTO_RIESGO ';


    IF UN_ORDEN = 1 THEN
        V_SQL := V_SQL || ' ORDER BY APELLIDO1, APELLIDO2, FECHA_DE_INGRESO, ID_DE_EMPLEADO, NOMBRES ';
    ELSE
        V_SQL := V_SQL || ' ORDER BY NUMERO_DCTO, FECHA_DE_INGRESO, ID_DE_EMPLEADO, NOMBRES ';
    END IF;

    IF UN_RETROACTIVO <> 0 THEN
        V_SQL := V_SQL || ' , NOVEDAD_TIPONOVEDAD, NOVEDAD_CONSECUTIVO, ID_DE_PROCESO ';
    END IF;

    RETURN V_SQL;

END FC_ARMARVSOIACUM;

END PCK_NOMINA_COM5;