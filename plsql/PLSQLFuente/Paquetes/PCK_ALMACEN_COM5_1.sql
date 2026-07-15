CREATE OR REPLACE PACKAGE BODY "PCK_ALMACEN_COM5" AS

PROCEDURE CARGARERROR (
    IN_ID_PROCESO   IN PCK_SUBTIPOS.TI_ENTERO,
    IN_USUARIO      IN VARCHAR2
) IS
    MI_TABLA     PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS     CLOB;
    MI_CONDICION  PCK_SUBTIPOS.TI_CAMPOS;
BEGIN

    MI_TABLA := 'CONTROL_PROCESOS';

    MI_CAMPOS :=
          'ESTADO_EJECUCION = ''ERRORES'','
        || 'FECHA_FINAL = SYSDATE,'
        || 'LOG_RESULTADO = ''' ||'ERROR_DATOS'|| ''','
        || 'DATE_MODIFIED = SYSDATE,'
        || 'MODIFIED_BY = ''' || IN_USUARIO || '''';

    MI_CONDICION := 'ID_PROCESO = ''' || IN_ID_PROCESO || '''';

    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                           UN_TABLA     => MI_TABLA,
                           UN_ACCION    => 'M',
                           UN_CAMPOS    => MI_CAMPOS,
                           UN_CONDICION => MI_CONDICION );

EXCEPTION
    WHEN OTHERS THEN
        NULL;
END CARGARERROR;

-- 1
FUNCTION FC_CALDEPRECIARH_NIIF
  (
    /*
      NAME             : FC_CALDEPRECIARH_NIIF
      AUTHORS          : SYSMAN SAS
      AUTHOR MIGRACION : JAVIER RICARDO VILLATE MERCHÁN
      DATE MIGRADOR    : 18/06/2018
      TIME             : 08:00 AM
      MODULO ORIGEN    : SysmanALHC2015.11.01
      MODIFIER         : AURA LILIANA MONROY GARCIA
      DATE MODIFIED    : 26/10/2018, 30/10/2018
      TIME             : 10:30 AM , 04:11 PM 
      DESCRIPTION      : Función que calcula la depreciación mensual de cada placa utilizando la norma niif. Se utilizo el estándar de codificación
      MODIFICATIONS    : - Se adiciona una condición en el Segundo InsertSelect que se realiza a la tabla DEPRECIAR y se cambia el campo 
                           FECHASALIDASERVICIO a NIIF_FECHAFUNCIONAMIENTO para realizar el cálculo de los valores de Depreciacion.
                         - Se adiciona el WITH BODEGA_DEPRECIAR en el primer Insert-Select a la tabla DEPRECIAR, para tener en cuenta la bodega en la
                         cual se encuentra el elemento en el periodo a depreciar. En la parte del cálculo, se agrega la validación de la FECHAADQUISICION junto
                         con el valor del parámetro "CALCULAR DEPRECIACIONES NIIF CON MES COMPLETO EN FECHA DE ADQUISICION"
     MODIFIER         : JOSE PASCUAL GOMEZ BLANCO
     DATE MODIFIED    : 29/11/2019
     MODIFICATIONS    : Se replantea totalmente para que la misma sea entendible unificando el calculo de la depreciación y luego asignandola a los campos que sea necesario

     @NAME:calcularDepreciacionHNiif  

    */
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,  -- Código de la compañia
    UN_ANOINICIAL          IN PCK_SUBTIPOS.TI_ANIO,    -- Código del elemento
    UN_MESINICIAL          IN PCK_SUBTIPOS.TI_MES,    -- Código del elemento
    UN_ANOFINAL            IN PCK_SUBTIPOS.TI_ANIO,    -- Código de la placa
    UN_MESFINAL            IN PCK_SUBTIPOS.TI_MES,    -- Código de la placa
    UN_STRELEMENTOINICIAL  IN VARCHAR2,                  -- Código del elemento
    UN_STRELEMENTOFINAL    IN VARCHAR2,                  -- Código del elemento
    UN_PLACAINICIAL        IN PCK_SUBTIPOS.TI_ENTERO_LARGO,    -- Código de la placa
    UN_PLACAFINAL          IN PCK_SUBTIPOS.TI_ENTERO_LARGO,     -- Código de la placa
    UN_USUARIO             IN PCK_SUBTIPOS.TI_USUARIO DEFAULT PCK_CONEXION.FC_GETUSER() 
  )
  RETURN CLOB
  AS
    MI_MENSAJE                CLOB:='';
    MI_MENSAJE_INT            CLOB:='';
    MI_MENSAJE_G1             CLOB := '';
    MI_MENSAJE_G2             CLOB := '';
    MI_MENSAJE_G3             CLOB := '';
    MI_ERRORES                PCK_SUBTIPOS.TI_ENTERO:=0;
    MI_ERRORES_G1             PCK_SUBTIPOS.TI_ENTERO:=0;
    MI_ERRORES_G2             PCK_SUBTIPOS.TI_ENTERO:=0;
    MI_ERRORES_G3             PCK_SUBTIPOS.TI_ENTERO:=0;
    MI_RTA                    CLOB:='';
    MI_PARCORTE               VARCHAR2(320 CHAR);
    MI_PARMESODIAS            VARCHAR2(10 CHAR);
    MI_DEPCOMODATO            VARCHAR2(10 CHAR);
    MI_PERIODO                VARCHAR2(10 CHAR);
    MI_PERIODO_ANT            VARCHAR2(10 CHAR);
    MI_FECHA_CORTE            DATE;
    MI_CONDICION              CLOB; --PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                CLOB;
    MI_A                      PCK_SUBTIPOS.TI_ENTERO;
    MI_M                      PCK_SUBTIPOS.TI_ENTERO;
    MI_RS                     SYS_REFCURSOR;
    MI_BODEGA_COMODATO        VARCHAR2(10);
    MI_CAL_DEPRECIA           CLOB;
    MI_DEPRECIA30DIAS         VARCHAR2(10);
    MI_DETERIORO              VARCHAR2(10);
    MI_ULTIMODIA              NUMBER;
    MI_VLRULTMOV              NUMBER;
    MI_ULTMOV                 CLOB;
    MI_BODEGA_INSERV          VARCHAR2(10);
    MI_BODEGA_RESPON          VARCHAR2(10);
    MI_DEPINSERVIBLE          VARCHAR2(10 CHAR);
    MI_DEPRESPONSABILIDAD     VARCHAR2(10 CHAR);
    MI_VLRDEPRECIACION        NUMBER(20,2);
    MI_BASECALDEP             NUMBER(20,2);
    MI_STRSQL                 CLOB;
    MI_ANO                    NUMBER;
    MI_MES                    NUMBER;
	MI_PARCOLGAAPNIIF         PARAMETRO.VALOR%TYPE;
    MI_PARMANEJANIIF          PARAMETRO.VALOR%TYPE;
    MI_INTERFACE_CENTRO       PARAMETRO.VALOR%TYPE;
    MI_INTERFAZ_CENTROS       PARAMETRO.VALOR%TYPE;											   
	MI_PARDIASDESDEINGRESO    PARAMETRO.VALOR%TYPE;	
  MI_ID_PROCESO                 PCK_SUBTIPOS.TI_ENTERO;
    MI_USUARIO                    VARCHAR2(200 CHAR);
    MI_TABLA                      PCK_SUBTIPOS.TI_TABLA;
	--CC_2968(29/12/2025 JCROJAS)
	MIPARFECHASV              PARAMETRO.VALOR%TYPE;									   
	MIPARINICIAFECHASV        PCK_SUBTIPOS.TI_PARAMETRO;													
  BEGIN
    BEGIN
        BEGIN
            SELECT MAX(ID_PROCESO), CREATED_BY 
            INTO MI_ID_PROCESO, MI_USUARIO
            FROM CONTROL_PROCESOS
            WHERE ESTADO_EJECUCION IN ('INICIADO') AND COMPANIA =  UN_COMPANIA AND  NOMBRE_PROCESO = 'CALCULAR DEPRECIACIONES'
            GROUP BY CREATED_BY;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_ID_PROCESO := 0;
            END;


      MI_PARCORTE := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                          ,UN_NOMBRE    =>'FECHA DE CORTE PARA INICIO DEL ALMACEN'
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                          ,UN_FECHA_PAR => SYSDATE);
  	MI_PARCOLGAAPNIIF := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                    ,UN_NOMBRE    => 'EJECUTA COLGAAP y NIIF'
                                                    ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                                    ,UN_FECHA_PAR => SYSDATE), 'NO');     
                                                    
      MI_PARMANEJANIIF  := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                    ,UN_NOMBRE    => 'MANEJA NIIF EN ALMACEN'
                                                    ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                                    ,UN_FECHA_PAR => SYSDATE), 'NO');                                                   																															  

      MI_FECHA_CORTE := TO_DATE(MI_PARCORTE,'DD/MM/YYYY'); 

      IF UN_ANOINICIAL< TO_NUMBER(TO_CHAR(MI_FECHA_CORTE,'YYYY')) 
       OR (UN_ANOINICIAL = TO_NUMBER(TO_CHAR(MI_FECHA_CORTE,'YYYY')) AND UN_MESINICIAL <= TO_NUMBER(TO_CHAR(MI_FECHA_CORTE,'MM'))) THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => -20001
            ,UN_ERROR_COD  =>  PCK_ERRORES.ERR_ALMACEN_DEPRECIARCORTE);
      END IF;
      MI_PARMESODIAS := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                          ,UN_NOMBRE    =>'CALCULAR DEPRECIACIONES NIIF CON MES COMPLETO EN FECHA DE ADQUISICION'
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                          ,UN_FECHA_PAR => SYSDATE);
      
      MI_DEPCOMODATO  := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                          ,UN_NOMBRE    =>'DEPRECIAR ELEMENTOS ENTREGADOS EN COMODATO'
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                          ,UN_FECHA_PAR => SYSDATE);                                     
      MI_BODEGA_COMODATO :=  CASE WHEN MI_DEPCOMODATO ='SI' THEN ',90 ' ELSE ' ' END;
      
      MI_DEPINSERVIBLE  := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                          ,UN_NOMBRE    =>'DEPRECIAR PLACAS EN BODEGA DE INSERVIBLES'
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                          ,UN_FECHA_PAR => SYSDATE); 
                                          
      MI_BODEGA_INSERV :=  CASE WHEN MI_DEPINSERVIBLE ='SI' THEN ',40 ' ELSE ' ' END;
      
      MI_DEPRESPONSABILIDAD  := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                          ,UN_NOMBRE    =>'DEPRECIAR PLACAS EN BODEGA DE RESPONSABILIDADES'
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                          ,UN_FECHA_PAR => SYSDATE); 
                                          
      MI_BODEGA_RESPON :=  CASE WHEN MI_DEPRESPONSABILIDAD ='SI' THEN ',50 ' ELSE ' ' END;
      
      MI_DETERIORO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                          ,UN_NOMBRE    =>'APLICAR DETERIORO ANTES DE DEPRECIACION'
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                          ,UN_FECHA_PAR => SYSDATE)
                                          , 'NO');
      MI_INTERFACE_CENTRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                          ,UN_NOMBRE    =>'MANEJA INTERFACE MENSUAL NIIF POR CENTRO DE COSTO'
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                          ,UN_FECHA_PAR => SYSDATE)
                                          , 'NO');
      MI_INTERFAZ_CENTROS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                          ,UN_NOMBRE    =>'MANEJA INTERFAZ DE ALMACEN POR CENTRO DE COSTO'
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                          ,UN_FECHA_PAR => SYSDATE)
                                          , 'NO');
      MI_PARDIASDESDEINGRESO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA
                                          ,UN_NOMBRE =>'CALCULAR DIAS A DEPRECIAR DESDE EL PRIMER DIA DE INGRESO'
                                          ,UN_MODULO => PCK_DATOS.FC_MODULOALMACEN()
                                          ,UN_FECHA_PAR => SYSDATE),'NO');
  	  --CC_2968(29/12/2025 JCROJAS)
	  MIPARFECHASV := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA
                                          ,UN_NOMBRE => 'INICIA DEPRECIACION EN PRIMERA SALIDA AL SERVICIO'
                                          ,UN_MODULO => PCK_DATOS.MODULOALMACEN
                                          ,UN_FECHA_PAR => SYSDATE),'NO');		
	  MIPARINICIAFECHASV := TO_DATE(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA
                                                        ,UN_NOMBRE => 'FECHA INICIO CALCULO FECHA DEPRECIACION CON SALIDA AL SERVICIO'
                                                        ,UN_MODULO => PCK_DATOS.MODULOALMACEN
                                                        ,UN_FECHA_PAR => SYSDATE),'DD/MM/YYYY');																														
  	IF MI_PARCOLGAAPNIIF = 'NO' THEN								
  		--ELIMINO TODAS LAS DEPRECIACIONES POSTERIORES A LA FECHA INICIAL DE EJECUCION DEL PROCESO
  		BEGIN
  		  BEGIN
  			 MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''
  							 AND ELEMENTO BETWEEN '''||UN_STRELEMENTOINICIAL||''' AND '''||UN_STRELEMENTOFINAL||'''
  							 AND SERIE    BETWEEN '|| UN_PLACAINICIAL||' AND '||UN_PLACAFINAL ||'
  							 AND PERIODO  BETWEEN TO_DATE(''' || TO_CHAR(LAST_DAY('01/'|| UN_MESINICIAL || '/' || UN_ANOINICIAL), 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'') 
  											  AND TO_DATE(''' || TO_CHAR(LAST_DAY('01/'|| UN_MESFINAL   || '/' || UN_ANOFINAL), 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                               AND NOT EXISTS (SELECT 1
                                            FROM DEPRECIACION_ACUMULADA A
                                            WHERE A.COMPANIA = ''' || UN_COMPANIA || ''' 
                                            AND A.SERIE BETWEEN '|| UN_PLACAINICIAL||' AND '||UN_PLACAFINAL ||'
                                              AND A.ELEMENTO BETWEEN '''||UN_STRELEMENTOINICIAL||''' AND '''||UN_STRELEMENTOFINAL||'''
                                              AND PERIODO  BETWEEN TO_DATE(''' || TRUNC(TO_DATE('01/'|| UN_MESINICIAL || '/' || UN_ANOINICIAL, 'DD/MM/YYYY'), 'MM') || ''', ''DD/MM/YYYY'')
  											  AND TO_DATE(''' || TO_CHAR(LAST_DAY('01/'|| UN_MESFINAL   || '/' || UN_ANOFINAL), 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')
                                              AND A.ELEMENTO = DEPRECIAR.ELEMENTO
                                              AND A.SERIE = DEPRECIAR.SERIE)';   

  			PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'DEPRECIAR'
  												  ,UN_ACCION    =>  'E'
  												  ,UN_CONDICION => MI_CONDICION);
  			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
  			  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
  		  END;

  		  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
  			PCK_ERR_MSG.RAISE_WITH_MSG(
  			  UN_EXC_COD    => SQLCODE
  			  ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_DELETE_DEPRECIAR);

  		END;
  	END IF;

      --INSERTA LOS ELEMENTOS EN LA TABLA DEPRECIAR QUE ESTAN EN LA TABLA D_MOVIMIENTO
      --DE CADA PERIODO QUE SE ESTA DEPRECIANDO ES DECIR SI DEPRECIO ENERO Y FEBRERO
      --INSERTA EN LA TABLA DEPRECIAR LOS ELEMENTOS QUE TUVIERON MOVIMIENTOS EN ESOS MESES
      --Y EN EL PERIODO INSERTA LA FECHA DEL ULTIMO DIA DEL MES.
      FOR MI_A IN UN_ANOINICIAL .. UN_ANOFINAL LOOP
        FOR MI_M IN 1 .. 12 LOOP
          IF NOT(MI_A = UN_ANOINICIAL AND MI_M < UN_MESINICIAL OR MI_A = UN_ANOFINAL AND MI_M > UN_MESFINAL ) THEN
                MI_PERIODO := TO_CHAR(LAST_DAY('01/'|| MI_M || '/' || MI_A), 'DD/MM/YYYY');
                MI_PERIODO_ANT := TO_CHAR(TO_DATE('01/'|| MI_M || '/' || MI_A)-1, 'DD/MM/YYYY') ;
  			  
  			  --(AMONROY:10/12/2018) En el SELECT principal se modifica el envio de los campos ANO y MES, cambiando de FECHAFUNCIONAMIENTO a NIIF_FECHAFUNCIONAMIENTO
  			  MI_VALORES := 'WITH  BODEGA_DEPRECIAR AS (
                           SELECT
                              D_MOVIMIENTO.COMPANIA
                              , D_MOVIMIENTO.ELEMENTO
                              , D_MOVIMIENTO.SERIE
                              , TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'||'''DD/MM/YYYY'''||') || '' '' || TO_CHAR(D_MOVIMIENTO.HORA ,'||'''HH24:MI:SS'''||'),'||'''DD/MM/YYYY HH24:MI:SS'''||') FECHA
                              , TIPOMOVIMIENTO.CLASE_BODEGA_DESTINO BODEGA
                              , D_MOVIMIENTO.TIPOMOVIMIENTO
                              , D_MOVIMIENTO.MOVIMIENTO    
                              , MOVIMIENTO.DEPENDENCIA_DESTINO DEPENDENCIA
                              , CASE WHEN (''' || MI_INTERFACE_CENTRO || '''=''NO''' || ' AND ''' || MI_INTERFAZ_CENTROS || '''=''NO''' || ') THEN PCK_DATOS.FC_CONS_CENTRO ELSE D_MOVIMIENTO.CENTRODECOSTO END CENTRODECOSTO
                              , D_MOVIMIENTO.FUENTEDERECURSO
                              , D_MOVIMIENTO.REFERENCIA_CNT REFERENCIA                            
                            FROM D_MOVIMIENTO
                              INNER JOIN MOVIMIENTO
                                 ON MOVIMIENTO.COMPANIA      = D_MOVIMIENTO.COMPANIA
                                AND MOVIMIENTO.TIPOMOVIMIENTO = D_MOVIMIENTO.TIPOMOVIMIENTO
                                AND MOVIMIENTO.NUMERO         = D_MOVIMIENTO.MOVIMIENTO
                              INNER JOIN TIPOMOVIMIENTO
                                 ON D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
                                AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                              WHERE D_MOVIMIENTO.COMPANIA     =  ''' || UN_COMPANIA || '''
                                AND D_MOVIMIENTO.ELEMENTO BETWEEN '''||UN_STRELEMENTOINICIAL||''' AND '''||UN_STRELEMENTOFINAL||''' 
                                AND D_MOVIMIENTO.SERIE  BETWEEN '|| UN_PLACAINICIAL||' AND '||UN_PLACAFINAL ||' 
                                AND ( D_MOVIMIENTO.COMPANIA
                                      , D_MOVIMIENTO.ELEMENTO
                                      , D_MOVIMIENTO.SERIE
                                      , TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'||'''DD/MM/YYYY'''||') || '' '' || TO_CHAR(D_MOVIMIENTO.HORA,'||'''HH24:MI:SS'''||' ),'||'''DD/MM/YYYY HH24:MI:SS'''||')) 
                                 IN (SELECT 
                                      COMPANIA 
                                      , ELEMENTO
                                      , SERIE 
                                      , MAX(FECHA) FEHA
                                      FROM (
                                          SELECT
                                              D_MOVIMIENTO.COMPANIA
                                              , D_MOVIMIENTO.ELEMENTO
                                              , D_MOVIMIENTO.SERIE
                                              , TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'||'''DD/MM/YYYY'''||') || '' '' || TO_CHAR(D_MOVIMIENTO.HORA,'||'''HH24:MI:SS'''||'),'||'''DD/MM/YYYY HH24:MI:SS'''||') FECHA 
                                              , TIPOMOVIMIENTO.CLASE_BODEGA_DESTINO BODEGA
                                              , D_MOVIMIENTO.TIPOMOVIMIENTO
                                              , D_MOVIMIENTO.MOVIMIENTO    
                                          FROM D_MOVIMIENTO
                                          INNER JOIN TIPOMOVIMIENTO
                                             ON D_MOVIMIENTO.COMPANIA     = TIPOMOVIMIENTO.COMPANIA
                                            AND D_MOVIMIENTO.TIPOMOVIMIENTO   = TIPOMOVIMIENTO.CODIGO
                                          WHERE D_MOVIMIENTO.COMPANIA = ''' || UN_COMPANIA || '''
                                            AND D_MOVIMIENTO.ELEMENTO BETWEEN '''||UN_STRELEMENTOINICIAL||''' AND '''||UN_STRELEMENTOFINAL||''' 
                                            AND D_MOVIMIENTO.SERIE BETWEEN '|| UN_PLACAINICIAL||' AND '||UN_PLACAFINAL ||' 
                                          ORDER BY TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'||'''DD/MM/YYYY'''||') || '' '' || TO_CHAR(D_MOVIMIENTO.HORA,'||'''HH24:MI:SS'''||' ),'||'''DD/MM/YYYY HH24:MI:SS'''||') DESC                         
                                      )
                                      WHERE TRUNC(FECHA) <= TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'')
                                      GROUP BY COMPANIA 
                                      , ELEMENTO
                                      , SERIE)
                              ORDER BY TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'||'''DD/MM/YYYY'''||') || '' '' || TO_CHAR(D_MOVIMIENTO.HORA,'||'''HH24:MI:SS'''||' ),'||'''DD/MM/YYYY HH24:MI:SS'''||') DESC
                          )
                          SELECT DEVOLUTIVO.COMPANIA,
                                     DEVOLUTIVO.ELEMENTO,
                                     DEVOLUTIVO.SERIE,
                                     TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'') AS PERIODO,
                                     BODEGA_DEPRECIAR.DEPENDENCIA,
                                     DEVOLUTIVO.VALOR,
                                     ' || MI_A || ' AS ANO,
                                     ' || MI_M || ' AS MES,
                                      0 NIIF_ACUMDEPAJ,
                                     0 NIIF_ACUMAJUSTESAJ,
                                     BODEGA_DEPRECIAR.BODEGA,
                                     BODEGA_DEPRECIAR.FECHA,
                                     DEVOLUTIVO.NIIF_FECHAFUNCIONAMIENTO,
                                     DEVOLUTIVO.NIIF_TIPO_ACTIVO,
                                     BODEGA_DEPRECIAR.CENTRODECOSTO,
                                     BODEGA_DEPRECIAR.FUENTEDERECURSO,
                                     BODEGA_DEPRECIAR.REFERENCIA,
                                     ''' || UN_USUARIO || ''',
                                     SYSDATE
                             FROM DEVOLUTIVO
                                LEFT JOIN DEPRECIAR
                                       ON DEVOLUTIVO.COMPANIA = DEPRECIAR.COMPANIA
                                      AND DEVOLUTIVO.ELEMENTO = DEPRECIAR.ELEMENTO
                                      AND DEVOLUTIVO.SERIE    = DEPRECIAR.SERIE
                                      AND TO_DATE('''|| MI_PERIODO ||''','||'''DD/MM/YYYY'''||') = DEPRECIAR.PERIODO  
                                INNER JOIN TIPO_ACTIVO
                                      ON DEVOLUTIVO.COMPANIA          = TIPO_ACTIVO.COMPANIA
                                      AND DEVOLUTIVO.NIIF_TIPO_ACTIVO = TIPO_ACTIVO.CODIGO_TIPOACTIVO
                                INNER JOIN BODEGA_DEPRECIAR
                                      ON DEVOLUTIVO.COMPANIA  = BODEGA_DEPRECIAR.COMPANIA
                                      AND DEVOLUTIVO.ELEMENTO = BODEGA_DEPRECIAR.ELEMENTO  
                                      AND DEVOLUTIVO.SERIE    = BODEGA_DEPRECIAR.SERIE
                            WHERE DEVOLUTIVO.COMPANIA        ='''|| UN_COMPANIA || '''
                              AND DEVOLUTIVO.ELEMENTO BETWEEN '''|| UN_STRELEMENTOINICIAL ||''' AND '''|| UN_STRELEMENTOFINAL||'''
                              AND DEVOLUTIVO.SERIE    BETWEEN '  || UN_PLACAINICIAL       ||  ' AND '  || UN_PLACAFINAL ||'
                              AND DEVOLUTIVO.APLICA_NIIF NOT IN (0)
                              AND TIPO_ACTIVO.DEPRECIA   NOT IN (0)                            
                              AND DEPRECIAR.COMPANIA      IS NULL                            
                              AND TO_NUMBER(TO_CHAR(DEVOLUTIVO.NIIF_FECHAADQUISICION,''YYYY'')) = '|| MI_A ||'
                              AND TO_NUMBER(TO_CHAR(DEVOLUTIVO.NIIF_FECHAADQUISICION,''MM''  )) = '|| MI_M ;

  			IF MI_PARCOLGAAPNIIF = 'SI' AND MI_PARMANEJANIIF = 'SI' THEN
                BEGIN
                BEGIN
                  MI_CONDICION := MI_VALORES;
                  MI_CAMPOS    := 'TABLA.COMPANIA     = VISTA.COMPANIA
                                   AND TABLA.ELEMENTO = VISTA.ELEMENTO
                                   AND TABLA.SERIE    = VISTA.SERIE';
                                   
                  MI_VALORES   := 'UPDATE SET TABLA.DEPENDENCIA = VISTA.DEPENDENCIA,
                                              TABLA.NIIF_VLRELEMENTO = VISTA.VALOR,
                                              TABLA.NIIF_ACUMDEPAJ = 0,
                                              TABLA.NIIF_ACUMAJUSTESAJ = 0,
                                              TABLA.BODEGA = VISTA.BODEGA,
                                              TABLA.FECHAULTMOV = VISTA.FECHA,
                                              TABLA.NIIF_FECHAFUNCIONAMIENTO = VISTA.NIIF_FECHAFUNCIONAMIENTO,
                                              TABLA.CENTRO_COSTO = VISTA.CENTRODECOSTO,
                                              TABLA.FUENTE_RECURSO = VISTA.FUENTEDERECURSO,
                                              TABLA.REFERENCIA = VISTA.REFERENCIA,
                                              TABLA.MODIFIED_BY = ''' || UN_USUARIO || ''',
                                              TABLA.DATE_MODIFIED = SYSDATE
                                    WHERE TABLA.COMPANIA = '''||UN_COMPANIA||'''
                                      AND TABLA.ELEMENTO BETWEEN '''||UN_STRELEMENTOINICIAL||''' AND '''||UN_STRELEMENTOFINAL||'''
                                      AND TABLA.SERIE BETWEEN '||UN_PLACAINICIAL||' AND '||UN_PLACAFINAL||'
                                      AND TABLA.PERIODO  = TO_DATE('''|| MI_PERIODO ||''','||'''DD/MM/YYYY'''||')';
                                      
                   MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => 'DEPRECIAR'
                                               ,UN_ACCION      => 'MM'
                                               ,UN_MERGEUSING  => MI_CONDICION
                                               ,UN_MERGEENLACE => MI_CAMPOS
                                               ,UN_MERGEEXISTE => MI_VALORES);
      
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                       RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                   END;
      
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                       PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                 ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_MERGE_DEPRECIAR);
                   END;
                ELSE
                BEGIN
                BEGIN
                   MI_CAMPOS:= ' COMPANIA
                              ,ELEMENTO
                              ,SERIE
                              ,PERIODO
                              ,DEPENDENCIA
                              ,NIIF_VLRELEMENTO
                              ,ANO
                              ,MES
                              ,NIIF_ACUMDEPAJ
                              ,NIIF_ACUMAJUSTESAJ
                              ,BODEGA
                              ,FECHAULTMOV
                              ,NIIF_FECHAFUNCIONAMIENTO
                              ,NIIF_TIPO_ACTIVO
                              ,CENTRO_COSTO
                              ,FUENTE_RECURSO
                              ,REFERENCIA
                              ,CREATED_BY
                              ,DATE_CREATED';
                              --AGREGUE EL CAMPO FECHAULTMOV 
                              
                   MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'DEPRECIAR'
                                               ,UN_ACCION  => 'IS'
                                               ,UN_CAMPOS  => MI_CAMPOS
                                               ,UN_VALORES => MI_VALORES);

                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                   END;

                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
  										
                                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_INSERT_DEPRECIAR);
                  END;
                END IF;	 
          END IF;
        END LOOP;
      END LOOP;

       --ESTA CONSULTA VERIFICA SI EXISTEN PLACAS CON FECHA DE ADQUISICION NULAS.
       FOR MI_RS IN( SELECT DEVOLUTIVO.COMPANIA
                              ,DEVOLUTIVO.ELEMENTO
                              ,DEVOLUTIVO.SERIE
                              ,DEVOLUTIVO.NIIF_FECHAADQUISICION
                              ,DEVOLUTIVO.FECHAANULADA
                              ,DEVOLUTIVO.FECHASALIDASERVICIO
                              ,PLACAANULADA
                       FROM  DEVOLUTIVO
                       WHERE DEVOLUTIVO.COMPANIA = UN_COMPANIA
                         AND DEVOLUTIVO.SERIE    BETWEEN UN_PLACAINICIAL       AND UN_PLACAFINAL
                         AND DEVOLUTIVO.ELEMENTO BETWEEN UN_STRELEMENTOINICIAL AND UN_STRELEMENTOFINAL
                         AND DEVOLUTIVO.SERIE    NOT IN (SELECT SERIE
                                                         FROM DEPRECIAR
                                                         WHERE DEPRECIAR.COMPANIA=UN_COMPANIA
                                                           AND DEPRECIAR.ELEMENTO BETWEEN UN_STRELEMENTOINICIAL AND UN_STRELEMENTOFINAL
                                                           AND DEPRECIAR.SERIE    BETWEEN UN_PLACAINICIAL       AND UN_PLACAFINAL
                                                           AND DEPRECIAR.ANO=DECODE(UN_MESINICIAL,1   ,UN_ANOINICIAL-1,UN_ANOINICIAL)
                                                           AND DEPRECIAR.MES=DECODE(UN_MESINICIAL,1,12,UN_MESINICIAL-1))
                        AND DEVOLUTIVO.NIIF_FECHAADQUISICION IS NULL
                        AND DEVOLUTIVO.PLACAANULADA IN(0))
        LOOP
          IF MI_RS.SERIE<>0 THEN
             MI_MENSAJE_INT:=MI_MENSAJE_INT||'Elemento: '||MI_RS.ELEMENTO||' Con Placa: '||TO_CHAR (MI_RS.SERIE)||CHR(10)||CHR(13);
             MI_ERRORES:=MI_ERRORES+1;
          END IF;
        END LOOP;
         IF MI_ERRORES>0 THEN
            MI_MENSAJE:=MI_MENSAJE||'*** INFORME DE ELEMENTOS DEVOLUTIVOS CON FECHA DE ADQUISICION NULA ***';
            MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
            MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13) || MI_MENSAJE_INT;
            MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
            MI_MENSAJE:=MI_MENSAJE||'Proceso terminado ';
            MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
            MI_MENSAJE:=MI_MENSAJE||'********************* FIN DEL INFORME *********************'||CHR(10)||CHR(13);
            CARGARERROR(MI_ID_PROCESO, MI_USUARIO);
            RETURN MI_MENSAJE;
         END IF;
         
        --ESTA CONSULTA VERIFICA SI EXISTEN PLACAS QUE NO FUERÓN DEPRECIADAS EL MES PASADO.
        --CREO UNA TABLA TEMPORAL
          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'TEMP_SERIE'
                                                    ,UN_ACCION    =>  'E'
                                                    ,UN_CONDICION => '1 = 1');
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE
                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_DELETE_DEPRECIAR);

          END;

           MI_CAMPOS:= 'SERIE';
           MI_VALORES:= 'SELECT SERIE
                         FROM DEPRECIAR
                         WHERE DEPRECIAR.COMPANIA=''' || UN_COMPANIA || '''
                         AND DEPRECIAR.ELEMENTO BETWEEN '''||UN_STRELEMENTOINICIAL||''' AND '''||UN_STRELEMENTOFINAL||'''
                         AND DEPRECIAR.ANO=DECODE('||UN_MESINICIAL||',1,'||UN_ANOINICIAL||'-1,'||UN_ANOINICIAL||')
                         AND DEPRECIAR.MES=DECODE('||UN_MESINICIAL||',1,12,'||UN_MESINICIAL||'-1)
                         AND DEPRECIAR.SERIE    BETWEEN '  || UN_PLACAINICIAL       ||  ' AND '  || UN_PLACAFINAL;

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'TEMP_SERIE'
                                                ,UN_ACCION  => 'IS'
                                                ,UN_CAMPOS  => MI_CAMPOS
                                                ,UN_VALORES => MI_VALORES);

         FOR MI_RS IN( SELECT DEVOLUTIVO.COMPANIA
                              ,DEVOLUTIVO.ELEMENTO
                              ,DEVOLUTIVO.SERIE
                       FROM  DEVOLUTIVO
                       INNER JOIN TIPO_ACTIVO
                       ON DEVOLUTIVO.COMPANIA = TIPO_ACTIVO.COMPANIA
                       AND DEVOLUTIVO.NIIF_TIPO_ACTIVO = TIPO_ACTIVO.CODIGO_TIPOACTIVO 
                      WHERE DEVOLUTIVO.COMPANIA = UN_COMPANIA
                        AND DEVOLUTIVO.ELEMENTO BETWEEN UN_STRELEMENTOINICIAL AND UN_STRELEMENTOFINAL
                        AND DEVOLUTIVO.SERIE BETWEEN UN_PLACAINICIAL AND UN_PLACAFINAL
                        AND DEVOLUTIVO.APLICA_NIIF NOT IN (0)
                        AND TIPO_ACTIVO.DEPRECIA NOT IN(0)
                        AND DEVOLUTIVO.SERIE     NOT IN (SELECT SERIE
                                                         FROM TEMP_SERIE)
                        AND DEVOLUTIVO.NIIF_FECHAADQUISICION<TO_DATE('01/'|| UN_MESINICIAL || '/' || UN_ANOINICIAL, 'DD/MM/YYYY')
                        AND DEVOLUTIVO.NIIF_FECHAADQUISICION>=MI_FECHA_CORTE
                        AND ((DEVOLUTIVO.PLACAANULADA IN (0))
                          OR (DEVOLUTIVO.PLACAANULADA NOT IN (0)
                        AND TO_DATE('01/'|| UN_MESINICIAL || '/' || UN_ANOINICIAL, 'DD/MM/YYYY')<DEVOLUTIVO.FECHAANULADA
                        AND DEVOLUTIVO.NIIF_FECHAADQUISICION<TO_DATE('01/'|| UN_MESINICIAL || '/' || UN_ANOINICIAL, 'DD/MM/YYYY')))
                      )
           LOOP
        IF MI_RS.SERIE<>0 THEN
           MI_MENSAJE_G2 := MI_MENSAJE_G2||'Elemento: '||MI_RS.ELEMENTO||' Con Placa: '||TO_CHAR (MI_RS.SERIE)||CHR(10)||CHR(13);
           MI_ERRORES_G2 := MI_ERRORES_G2 + 1;
        END IF;

        END LOOP;
         IF MI_ERRORES_G2>0 THEN
          MI_MENSAJE:=MI_MENSAJE||'*** INFORME DE ELEMENTOS NO DEPRECIADOS EL MES ANTERIOR ***';
          MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
          MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13)||MI_MENSAJE_G2;
          MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
          MI_MENSAJE:=MI_MENSAJE||'Proceso terminado ';
          MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
          MI_MENSAJE:=MI_MENSAJE||' *** FIN DEL INFORME *** '||CHR(10)||CHR(13);
          CARGARERROR(MI_ID_PROCESO, MI_USUARIO);
          RETURN MI_MENSAJE;
       END IF;

        --ESTA CONSULTA VERIFICA SI EXISTEN REGISTROS EN LA TABLA DEPRECIACION_ACUMULADA EN EL PERIODO.
       FOR MI_RS IN(SELECT ELEMENTO, SERIE
                      FROM DEPRECIACION_ACUMULADA A
                      WHERE A.COMPANIA = UN_COMPANIA
                      AND A.SERIE BETWEEN UN_PLACAINICIAL AND UN_PLACAFINAL
                        AND A.ELEMENTO BETWEEN UN_STRELEMENTOINICIAL AND UN_STRELEMENTOFINAL
                        AND PERIODO  BETWEEN TO_DATE(TRUNC(TO_DATE('01/'|| UN_MESINICIAL || '/' || UN_ANOINICIAL, 'DD/MM/YYYY'), 'MM') , 'DD/MM/YYYY')
                        AND TO_DATE(TO_CHAR(LAST_DAY('01/'|| UN_MESFINAL   || '/' || UN_ANOFINAL), 'DD/MM/YYYY') , 'DD/MM/YYYY'))
         LOOP
        IF MI_RS.SERIE<>0 THEN
           MI_MENSAJE_G3:=MI_MENSAJE_G3||'Elemento: '||MI_RS.ELEMENTO||' Con Placa: '||TO_CHAR (MI_RS.SERIE)||CHR(10)||CHR(13);
           MI_ERRORES_G3:=MI_ERRORES_G3+1;
        END IF;
      END LOOP;
       IF MI_ERRORES_G3>0 THEN
          MI_MENSAJE:=MI_MENSAJE||'*** INFORME DE ELEMENTOS CON DEPRECIACION NO ACUMULADA ***';
          MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
          MI_MENSAJE:=MI_MENSAJE||'Los devolutivos (relación de número de placa) con registro de Depreciación No Calculada no serán procesados en este periodo.'||CHR(10)||CHR(13);
          MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13); 
          MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13)||MI_MENSAJE_G3;
          MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
          MI_MENSAJE:=MI_MENSAJE||'Proceso terminado ';
          MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
          MI_MENSAJE:=MI_MENSAJE||' *** FIN DEL INFORME *** '||CHR(10)||CHR(13);
          CARGARERROR(MI_ID_PROCESO, MI_USUARIO);
       END IF;
         
         --ESTA CONSULTA VERIFICA LOS DEVOLUTIVOS QUE NO ESTAN MARCADOS CON APLICA NIFF.
         MI_MENSAJE:='';
       FOR MI_RS IN( SELECT DEVOLUTIVO.COMPANIA
                              ,DEVOLUTIVO.ELEMENTO
                              ,DEVOLUTIVO.SERIE
                       FROM  DEVOLUTIVO
                      WHERE DEVOLUTIVO.COMPANIA = UN_COMPANIA
                        AND DEVOLUTIVO.ELEMENTO BETWEEN UN_STRELEMENTOINICIAL AND UN_STRELEMENTOFINAL
                        AND DEVOLUTIVO.SERIE BETWEEN UN_PLACAINICIAL AND UN_PLACAFINAL
                        AND DEVOLUTIVO.APLICA_NIIF IN (0))
        LOOP
          IF MI_RS.SERIE<>0 THEN
             MI_MENSAJE_G1 := MI_MENSAJE_G1 ||'Elemento: '||MI_RS.ELEMENTO||' Con Placa: '||TO_CHAR(MI_RS.SERIE)||' Devolutivo no se encuentra marcado como Aplica NIIF.'||CHR(10)||CHR(13);
             MI_ERRORES_G1 := MI_ERRORES_G1 + 1;
          END IF;
        END LOOP;
         IF MI_ERRORES_G1>0 THEN
            MI_MENSAJE:=MI_MENSAJE||'*** INFORME DE ELEMENTOS NO DEPRECIADOS EN EL PERIODO ***';
            MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
            MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13)||MI_MENSAJE_G1;
            MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
            MI_MENSAJE:=MI_MENSAJE||'Proceso terminado ';
            MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
            MI_MENSAJE:=MI_MENSAJE||'********************* FIN DEL INFORME *********************'||CHR(10)||CHR(13);
            CARGARERROR(MI_ID_PROCESO, MI_USUARIO);
         END IF;
         
      --INSERTA LOS DATOS EN LA TABLA DEPRECIAR SIN TENER EN CUENTA LOS QUE YA EXISTEN
      FOR MI_A IN UN_ANOINICIAL .. UN_ANOFINAL LOOP
        FOR MI_M IN 1 .. 12 LOOP
          IF NOT(MI_A = UN_ANOINICIAL AND MI_M < UN_MESINICIAL OR MI_A = UN_ANOFINAL AND MI_M > UN_MESFINAL ) THEN
                MI_PERIODO     := TO_CHAR(LAST_DAY(TO_DATE('01/'|| MI_M || '/' || MI_A,'DD/MM/YYYY')), 'DD/MM/YYYY');
                MI_PERIODO_ANT := TO_CHAR(TO_DATE('01/'|| MI_M || '/' || MI_A)-1, 'DD/MM/YYYY') ;
                MI_ULTIMODIA   := TO_NUMBER(TO_CHAR(LAST_DAY(TO_DATE('01/'|| MI_M || '/' || MI_A,'DD/MM/YYYY')), 'DD'));
                MI_DEPRECIA30DIAS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                          ,UN_NOMBRE    =>'DEPRECIAR CON MESES DE 30 DIAS'
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                          ,UN_FECHA_PAR => TO_DATE(MI_PERIODO,'DD/MM/YYYY'))
                                        ,'NO');

               --(AMONROY:26/10/2018) Se agrega la condicion de la fecha anulada en el periodo que se esta depreciando
               MI_VALORES:= ' WITH  BODEGA_DEPRECIAR AS (
                           SELECT
                              D_MOVIMIENTO.COMPANIA
                              , D_MOVIMIENTO.ELEMENTO
                              , D_MOVIMIENTO.SERIE
                              , TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'||'''DD/MM/YYYY'''||') || '' '' || TO_CHAR(D_MOVIMIENTO.HORA ,'||'''HH24:MI:SS'''||'),'||'''DD/MM/YYYY HH24:MI:SS'''||') FECHA
                              , TIPOMOVIMIENTO.CLASE_BODEGA_DESTINO BODEGA
                              , D_MOVIMIENTO.TIPOMOVIMIENTO
                              , D_MOVIMIENTO.MOVIMIENTO    
                              , MOVIMIENTO.DEPENDENCIA_DESTINO DEPENDENCIA
                              , CASE WHEN (''' || MI_INTERFACE_CENTRO || '''=''NO''' || ' AND ''' || MI_INTERFAZ_CENTROS || '''=''NO''' || ') THEN PCK_DATOS.FC_CONS_CENTRO ELSE D_MOVIMIENTO.CENTRODECOSTO END CENTRODECOSTO
                              , MOVIMIENTO.FUENTEDERECURSO
                              , MOVIMIENTO.REFERENCIA                            
                            FROM D_MOVIMIENTO
                              INNER JOIN MOVIMIENTO
                                 ON MOVIMIENTO.COMPANIA      = D_MOVIMIENTO.COMPANIA
                                AND MOVIMIENTO.TIPOMOVIMIENTO = D_MOVIMIENTO.TIPOMOVIMIENTO
                                AND MOVIMIENTO.NUMERO         = D_MOVIMIENTO.MOVIMIENTO
                              INNER JOIN TIPOMOVIMIENTO
                                 ON D_MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
                                AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
                              WHERE D_MOVIMIENTO.COMPANIA     =  ''' || UN_COMPANIA || '''
                                AND D_MOVIMIENTO.ELEMENTO BETWEEN '''||UN_STRELEMENTOINICIAL||''' AND '''||UN_STRELEMENTOFINAL||''' 
                                AND D_MOVIMIENTO.SERIE  BETWEEN '|| UN_PLACAINICIAL||' AND '||UN_PLACAFINAL ||' 
                                AND ( D_MOVIMIENTO.COMPANIA
                                      , D_MOVIMIENTO.ELEMENTO
                                      , D_MOVIMIENTO.SERIE
                                      , TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'||'''DD/MM/YYYY'''||') || '' '' || TO_CHAR(D_MOVIMIENTO.HORA,'||'''HH24:MI:SS'''||' ),'||'''DD/MM/YYYY HH24:MI:SS'''||')) 
                                 IN (SELECT 
                                      COMPANIA 
                                      , ELEMENTO
                                      , SERIE 
                                      , MAX(FECHA) FEHA
                                      FROM (
                                          SELECT
                                              D_MOVIMIENTO.COMPANIA
                                              , D_MOVIMIENTO.ELEMENTO
                                              , D_MOVIMIENTO.SERIE
                                              , TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'||'''DD/MM/YYYY'''||') || '' '' || TO_CHAR(D_MOVIMIENTO.HORA,'||'''HH24:MI:SS'''||'),'||'''DD/MM/YYYY HH24:MI:SS'''||') FECHA 
                                              , TIPOMOVIMIENTO.CLASE_BODEGA_DESTINO BODEGA
                                              , D_MOVIMIENTO.TIPOMOVIMIENTO
                                              , D_MOVIMIENTO.MOVIMIENTO    
                                          FROM D_MOVIMIENTO
                                          INNER JOIN TIPOMOVIMIENTO
                                             ON D_MOVIMIENTO.COMPANIA     = TIPOMOVIMIENTO.COMPANIA
                                            AND D_MOVIMIENTO.TIPOMOVIMIENTO   = TIPOMOVIMIENTO.CODIGO
                                          WHERE D_MOVIMIENTO.COMPANIA = ''' || UN_COMPANIA || '''
                                            AND D_MOVIMIENTO.ELEMENTO BETWEEN '''||UN_STRELEMENTOINICIAL||''' AND '''||UN_STRELEMENTOFINAL||''' 
                                            AND D_MOVIMIENTO.SERIE BETWEEN '|| UN_PLACAINICIAL||' AND '||UN_PLACAFINAL ||' 
                                          ORDER BY TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'||'''DD/MM/YYYY'''||') || '' '' || TO_CHAR(D_MOVIMIENTO.HORA,'||'''HH24:MI:SS'''||' ),'||'''DD/MM/YYYY HH24:MI:SS'''||') DESC                         
                                      )
                                      WHERE TRUNC(FECHA) <= TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'')
                                      GROUP BY COMPANIA 
                                      , ELEMENTO
                                      , SERIE)
                              ORDER BY TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'||'''DD/MM/YYYY'''||') || '' '' || TO_CHAR(D_MOVIMIENTO.HORA,'||'''HH24:MI:SS'''||' ),'||'''DD/MM/YYYY HH24:MI:SS'''||') DESC
                          )             
                          SELECT DEPRECIAR.COMPANIA
                                ,DEPRECIAR.ELEMENTO
                                ,DEPRECIAR.SERIE
                                ,TO_DATE('''|| MI_PERIODO ||''','||'''DD/MM/YYYY'''||') AS PERIODO
                                ,BODEGA_DEPRECIAR.DEPENDENCIA
                                ,DEPRECIAR.NIIF_VLRELEMENTO
                                ,'|| MI_A ||', '|| MI_M ||'
                                ,DEPRECIAR.NIIF_ACUMDEPAJ
                                ,DEPRECIAR.NIIF_ACUMAJUSTESAJ
                                ,DEPRECIAR.NIIF_VLRACUMULADO
                                ,DEPRECIAR.NIIF_VLRLIBROS
                                ,DEPRECIAR.NIIF_DEPACUMULADAAJ
                                ,DEPRECIAR.NIIF_SALDOAJ
                                ,DEPRECIAR.NIIF_DEPACUMULADA
                                ,BODEGA_DEPRECIAR.BODEGA 
                                ,DEPRECIAR.FECHASALIDASERVICIO
                                ,BODEGA_DEPRECIAR.FECHA 
                                ,DEPRECIAR.NIIF_COSTOAJUSTADO 
                                ,CASE WHEN DEPRECIAR.NIIF_FECHAFUNCIONAMIENTO IS NULL THEN DEVOLUTIVO.NIIF_FECHAFUNCIONAMIENTO ELSE DEPRECIAR.NIIF_FECHAFUNCIONAMIENTO END NIIF_FECHAFUNCIONAMIENTO
                                ,DEPRECIAR.NIIF_TIPO_ACTIVO
                                ,BODEGA_DEPRECIAR.CENTRODECOSTO
                                ,BODEGA_DEPRECIAR.FUENTEDERECURSO
                                ,BODEGA_DEPRECIAR.REFERENCIA  
                                ,''' || UN_USUARIO || '''
                                ,SYSDATE
                            FROM DEVOLUTIVO
                            INNER JOIN DEPRECIAR
                                   ON DEVOLUTIVO.COMPANIA = DEPRECIAR.COMPANIA
                                  AND DEVOLUTIVO.ELEMENTO = DEPRECIAR.ELEMENTO
                                  AND DEVOLUTIVO.SERIE    = DEPRECIAR.SERIE
                                  AND TO_DATE('''|| MI_PERIODO_ANT ||''','||'''DD/MM/YYYY'''||')  = DEPRECIAR.PERIODO
                            INNER JOIN BODEGA_DEPRECIAR
                                   ON DEVOLUTIVO.COMPANIA  = BODEGA_DEPRECIAR.COMPANIA
                                  AND DEVOLUTIVO.ELEMENTO = BODEGA_DEPRECIAR.ELEMENTO  
                                  AND DEVOLUTIVO.SERIE    = BODEGA_DEPRECIAR.SERIE                 
                            LEFT JOIN DEPRECIAR DEPRECIAR_REPETIDO
                                   ON DEPRECIAR.COMPANIA  = DEPRECIAR_REPETIDO.COMPANIA
                                  AND DEPRECIAR.ELEMENTO  = DEPRECIAR_REPETIDO.ELEMENTO  
                                  AND DEPRECIAR.SERIE     = DEPRECIAR_REPETIDO.SERIE 
                                  AND DEPRECIAR.PERIODO   = TO_DATE('''|| MI_PERIODO ||''','||'''DD/MM/YYYY'''||')                                        
                          WHERE DEPRECIAR.COMPANIA = ''' || UN_COMPANIA || '''
                            AND DEPRECIAR.ELEMENTO BETWEEN '''||UN_STRELEMENTOINICIAL||''' AND '''||UN_STRELEMENTOFINAL||'''
                            AND DEPRECIAR.SERIE    BETWEEN '|| UN_PLACAINICIAL||' AND '||UN_PLACAFINAL ||'
                            AND (DEVOLUTIVO.FECHAANULADA >= TO_DATE(''01/'|| MI_M ||'/' || MI_A || ''',''DD/MM/YYYY HH24:MI:SS'') 
                              OR DEVOLUTIVO.FECHAANULADA IS NULL)
                           AND DEPRECIAR.ANO      =' || TO_CHAR(CASE WHEN MI_M = 1 THEN MI_A - 1 ELSE MI_A     END) || '
                           AND DEPRECIAR.MES      =' || TO_CHAR(CASE WHEN MI_M = 1 THEN 12       ELSE MI_M -1  END) || ' 
                           AND DEPRECIAR_REPETIDO.COMPANIA IS NULL
  						 AND NOT EXISTS (SELECT 1
                                            FROM DEPRECIACION_ACUMULADA A
                                            WHERE A.COMPANIA = ''' || UN_COMPANIA || ''' 
                                            AND A.SERIE BETWEEN '|| UN_PLACAINICIAL||' AND '||UN_PLACAFINAL ||'
                                              AND A.ELEMENTO BETWEEN '''||UN_STRELEMENTOINICIAL||''' AND '''||UN_STRELEMENTOFINAL||'''
                                              AND TO_CHAR(PERIODO,''YYYY'') = ' || MI_A ||'
                                              AND TO_CHAR(PERIODO,''MM'') = ' || MI_M ||'
                                              AND A.ELEMENTO = DEPRECIAR.ELEMENTO
                                              AND A.SERIE = DEPRECIAR.SERIE)';                                                   
  		IF MI_PARCOLGAAPNIIF = 'SI' AND MI_PARMANEJANIIF = 'SI' THEN
              BEGIN
              BEGIN  
                  MI_CONDICION := MI_VALORES;
                  MI_CAMPOS    := 'TABLA.COMPANIA     = VISTA.COMPANIA
                                   AND TABLA.ELEMENTO = VISTA.ELEMENTO
                                   AND TABLA.SERIE    = VISTA.SERIE';
                            
                  MI_VALORES   := 'UPDATE SET TABLA.DEPENDENCIA = VISTA.DEPENDENCIA,
                                              TABLA.NIIF_VLRELEMENTO = VISTA.NIIF_VLRELEMENTO,
                                              TABLA.NIIF_ACUMDEPAJ = VISTA.NIIF_ACUMDEPAJ,
                                              TABLA.NIIF_ACUMAJUSTESAJ = VISTA.NIIF_ACUMAJUSTESAJ,
                                              TABLA.NIIF_VLRACUMULADO = VISTA.NIIF_VLRACUMULADO,
                                              TABLA.NIIF_VLRLIBROS = VISTA.NIIF_VLRLIBROS,
                                              TABLA.NIIF_DEPACUMULADAAJ = VISTA.NIIF_DEPACUMULADAAJ,
                                              TABLA.NIIF_SALDOAJ = VISTA.NIIF_SALDOAJ,
                                              TABLA.NIIF_DEPACUMULADA = VISTA.NIIF_DEPACUMULADA,
                                              TABLA.BODEGA = VISTA.BODEGA,
                                              TABLA.FECHASALIDASERVICIO = VISTA.FECHASALIDASERVICIO,
                                              TABLA.FECHAULTMOV = VISTA.FECHA,
                                              TABLA.NIIF_COSTOAJUSTADO = VISTA.NIIF_COSTOAJUSTADO,
                                              TABLA.NIIF_FECHAFUNCIONAMIENTO = VISTA.NIIF_FECHAFUNCIONAMIENTO,
                                              TABLA.NIIF_TIPO_ACTIVO = VISTA.NIIF_TIPO_ACTIVO,
                                              TABLA.CENTRO_COSTO = VISTA.CENTRODECOSTO,
                                              TABLA.FUENTE_RECURSO = VISTA.FUENTEDERECURSO,
                                              TABLA.REFERENCIA = VISTA.REFERENCIA,
                                              TABLA.MODIFIED_BY = ''' || UN_USUARIO || ''',
                                              TABLA.DATE_MODIFIED = SYSDATE
                                    WHERE TABLA.COMPANIA = '''||UN_COMPANIA||'''
                                      AND TABLA.ELEMENTO BETWEEN '''||UN_STRELEMENTOINICIAL||''' AND '''||UN_STRELEMENTOFINAL||'''
                                      AND TABLA.SERIE BETWEEN '||UN_PLACAINICIAL||' AND '||UN_PLACAFINAL||'
                                      AND TABLA.PERIODO  = TO_DATE('''|| MI_PERIODO ||''','||'''DD/MM/YYYY'''||')';   
                  
                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => 'DEPRECIAR'
                                              ,UN_ACCION      => 'MM'
                                              ,UN_MERGEUSING  => MI_CONDICION
                                              ,UN_MERGEENLACE => MI_CAMPOS
                                              ,UN_MERGEEXISTE => MI_VALORES);
      
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                      RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                  END;
      
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                       PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                 ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_MERGE_DEPRECIAR);
                  END;
            ELSE
              BEGIN
              BEGIN
                  MI_CAMPOS:= ' COMPANIA
                             ,ELEMENTO
                             ,SERIE
                             ,PERIODO
                             ,DEPENDENCIA
                             ,NIIF_VLRELEMENTO
                             ,ANO
                             ,MES
                             ,NIIF_ACUMDEPAJ
                             ,NIIF_ACUMAJUSTESAJ
                             ,NIIF_VLRACUMULADO
                             ,NIIF_VLRLIBROS
                             ,NIIF_DEPACUMULADAAJ
                             ,NIIF_SALDOAJ
                             ,NIIF_DEPACUMULADA
                             ,BODEGA
                             ,FECHASALIDASERVICIO
                             ,FECHAULTMOV
                             ,NIIF_COSTOAJUSTADO
                             ,NIIF_FECHAFUNCIONAMIENTO
                             ,NIIF_TIPO_ACTIVO
                             ,CENTRO_COSTO
                             ,FUENTE_RECURSO
                             ,REFERENCIA
                             ,CREATED_BY
                             ,DATE_CREATED';

                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'DEPRECIAR'
                                              ,UN_ACCION  => 'IS'
                                              ,UN_CAMPOS  => MI_CAMPOS
                                              ,UN_VALORES => MI_VALORES);
  										 
  										
  																			
  			  

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                  END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_INSERT_DEPRECIAR);
                  END;
            END IF;						 
            --ACTUALIZA LA TABLA DEPRECIAR CON EL CALCULO DE LA DEPRECIACIÓN
            --(EAMAYA:28/04/2020) Se agrega la union con la tabla REEXPRESARVIDAUTIL para el calculo del deterioro segun migracion sql server
            --ECABRERA 7715500 16/06/2022 Se adiciona filtro para movimientos antes del ultimo dia del periodo y se toma el valor de este movimiento,
            --el ultimo, para calculo de la depreciacion, se adiciona validacion del movimiento, si es por cambio de valor(concepto = 'CR') se toma
            --el campo  VALORSALDO sino el campo VALORTOTAL

            --ECABRERA 7715500 22/07/2022 Se adiciona consulta del ultimo movimiento que no sea de correccion, para sumarlo en caso 
            --que la consulta general (MI_CONDICION) retorne como ultimo movimiento uno de correccion, ya que, el valor de este movimiento
            --seria el valor de la correccion, no el valor de la placa
            MI_ULTMOV :=      '
                              (
                              SELECT  NVL(MAX(DM2.VALORTOTAL),0) VALORTOTAL   
                              FROM    D_MOVIMIENTO DM2
                                      INNER JOIN TIPOMOVIMIENTO TM2
                                          ON DM2.COMPANIA = TM2.COMPANIA AND DM2.TIPOMOVIMIENTO = TM2.CODIGO
                              WHERE   DM2.COMPANIA  = D_MOVIMIENTO.COMPANIA
                                      AND DM2.ELEMENTO = D_MOVIMIENTO.ELEMENTO
                                      AND DM2.SERIE = D_MOVIMIENTO.SERIE
                                      AND TM2.CONCEPTO != ''CR''
                                      AND ( DM2.COMPANIA
                                            , DM2.ELEMENTO
                                            , DM2.SERIE
                                            , TO_DATE(TO_CHAR(DM2.FECHA,''DD/MM/YYYY'') || '' '' || TO_CHAR(DM2.HORA,''HH24:MI:SS''),''DD/MM/YYYY HH24:MI:SS'') 
                                          )
                                          IN 
                                          (
                                          SELECT   COMPANIA 
                                                   , ELEMENTO
                                                   , SERIE 
                                                   , MAX(FECHA) FECHA
                                          FROM    (
                                                  SELECT     DM3.COMPANIA
                                                              , DM3.ELEMENTO
                                                              , DM3.SERIE
                                                              , TO_DATE(TO_CHAR(DM3.FECHA,''DD/MM/YYYY'') || '' '' || TO_CHAR(DM3.HORA,''HH24:MI:SS''),''DD/MM/YYYY HH24:MI:SS'') FECHA 
                                                              , TM3.CLASE_BODEGA_DESTINO BODEGA
                                                              , DM3.TIPOMOVIMIENTO
                                                              , DM3.MOVIMIENTO    
                                                  FROM       D_MOVIMIENTO DM3
                                                              INNER JOIN TIPOMOVIMIENTO TM3
                                                                 ON DM3.COMPANIA = TM3.COMPANIA AND DM3.TIPOMOVIMIENTO = TM3.CODIGO
                                                  WHERE       DM3.COMPANIA = D_MOVIMIENTO.COMPANIA
                                                              AND DM3.ELEMENTO = D_MOVIMIENTO.ELEMENTO
                                                              AND DM3.SERIE = D_MOVIMIENTO.SERIE
                                                              AND TM3.CONCEPTO != ''CR''
                                                              AND NOT (TM3.CLASE_BODEGA_ORIGEN IN(40, 50) AND TM3.CLASE_BODEGA_DESTINO IN(60))
                                                              AND DM3.FECHA <= TO_DATE(''' || MI_PERIODO || ''', ''DD/MM/YYYY'')
                                                  )
                                          WHERE   TRUNC(FECHA) <= TO_DATE(''' || MI_PERIODO || ''', ''DD/MM/YYYY'')
                                          GROUP BY COMPANIA, ELEMENTO, SERIE
                                          )
                              ) '
                          ;
            --EFCM 22/07/2022

            BEGIN
              BEGIN
              ----JM FT JGUERRERO & APRIETO & SGARZON CC  920 19/02/2025
              -- Se ajustan las condiciones para que el parametro  MI_DETERIORO funcione como incialmente estaba planeado 
              -- se toman en consideracion el valor en libros (JOIN LIBROS) y el deterioro del periodo (JOIN DETERPER)
              --- y el/los deterioro/s de periodo(s) Anterior(es) (JOIN REEXPVUTIL) y valor de salvamento 
              -- tanto para el BASECALDEP (se utiliza para la depresiacion del periodo)
              -- y BASECALAJUSTADO (se utiliza para el costo ajustado del periodo)
              -- JM FT JGUERRERO & NGAMEZ CC 920 17/03/2025 se añaden BASEDETERIORO, BASEVALORBASE, BASESALVAMENTO para el
              -- calculo del VLR en Libros 
              --CC_2968(29/12/2025 JCROJAS): Se agrega campo PRIMSALIDASERVICIO
                 MI_CONDICION := 'WITH  BODEGA_DEPRECIAR AS (
                                   SELECT
                                      D_MOVIMIENTO.COMPANIA
                                      , D_MOVIMIENTO.ELEMENTO
                                      , D_MOVIMIENTO.SERIE
                                      , TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'||'''DD/MM/YYYY'''||') || '' '' || TO_CHAR(D_MOVIMIENTO.HORA ,'||'''HH24:MI:SS'''||'),'||'''DD/MM/YYYY HH24:MI:SS'''||') FECHA
                                      , TIPOMOVIMIENTO.CLASE_BODEGA_ORIGEN  BODEGA_ORIGEN
                                      , TIPOMOVIMIENTO.CLASE_BODEGA_DESTINO BODEGA
                                      , D_MOVIMIENTO.TIPOMOVIMIENTO
                                      , D_MOVIMIENTO.MOVIMIENTO
                                      , TIPOMOVIMIENTO.CONCEPTO CPT_TIPOMOV
                                      , CASE WHEN TIPOMOVIMIENTO.CONCEPTO = ''CR'' THEN D_MOVIMIENTO.VALORTOTAL + ' || MI_ULTMOV || ' ELSE D_MOVIMIENTO.VALORTOTAL END VALORTOTAL
                                       FROM D_MOVIMIENTO
                                      INNER JOIN TIPOMOVIMIENTO
                                         ON D_MOVIMIENTO.COMPANIA         = TIPOMOVIMIENTO.COMPANIA
                                        AND D_MOVIMIENTO.TIPOMOVIMIENTO   = TIPOMOVIMIENTO.CODIGO
                                      WHERE D_MOVIMIENTO.COMPANIA         = ''' || UN_COMPANIA || '''
                                        AND D_MOVIMIENTO.ELEMENTO BETWEEN '''|| UN_STRELEMENTOINICIAL ||''' AND '''|| UN_STRELEMENTOFINAL ||'''
                                        AND D_MOVIMIENTO.SERIE  BETWEEN '|| UN_PLACAINICIAL ||' AND '|| UN_PLACAFINAL ||'
                                        AND ( D_MOVIMIENTO.COMPANIA
                                              , D_MOVIMIENTO.ELEMENTO
                                              , D_MOVIMIENTO.SERIE
                                              , TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'||'''DD/MM/YYYY'''||') || '' '' || TO_CHAR(D_MOVIMIENTO.HORA,'||'''HH24:MI:SS'''||' ),'||'''DD/MM/YYYY HH24:MI:SS'''||'))
                                         IN (SELECT
                                              COMPANIA
                                              , ELEMENTO
                                              , SERIE
                                              , MAX(FECHA) FECHA
                                              FROM (
                                                  SELECT
                                                      D_MOVIMIENTO.COMPANIA
                                                      , D_MOVIMIENTO.ELEMENTO
                                                      , D_MOVIMIENTO.SERIE
                                                      , TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'||'''DD/MM/YYYY'''||') || '' '' || TO_CHAR(D_MOVIMIENTO.HORA,'||'''HH24:MI:SS'''||'),'||'''DD/MM/YYYY HH24:MI:SS'''||') FECHA
                                                      , TIPOMOVIMIENTO.CLASE_BODEGA_DESTINO BODEGA
                                                      , D_MOVIMIENTO.TIPOMOVIMIENTO
                                                      , D_MOVIMIENTO.MOVIMIENTO
                                                  FROM D_MOVIMIENTO
                                                  INNER JOIN TIPOMOVIMIENTO
                                                     ON D_MOVIMIENTO.COMPANIA     = TIPOMOVIMIENTO.COMPANIA
                                                    AND D_MOVIMIENTO.TIPOMOVIMIENTO   = TIPOMOVIMIENTO.CODIGO
                                                  WHERE D_MOVIMIENTO.COMPANIA = ''' || UN_COMPANIA || '''
                                                    AND D_MOVIMIENTO.ELEMENTO BETWEEN '''||UN_STRELEMENTOINICIAL||''' AND '''||UN_STRELEMENTOFINAL||'''
                                                    AND D_MOVIMIENTO.SERIE BETWEEN '|| UN_PLACAINICIAL||' AND '||UN_PLACAFINAL ||'
                                                    AND NOT (TIPOMOVIMIENTO.CLASE_BODEGA_ORIGEN IN(40, 50) AND TIPOMOVIMIENTO.CLASE_BODEGA_DESTINO IN(60))
                                                    AND D_MOVIMIENTO.FECHA <= TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'')
                                                  ORDER BY TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'||'''DD/MM/YYYY'''||') || '' '' || TO_CHAR(D_MOVIMIENTO.HORA,'||'''HH24:MI:SS'''||' ),'||'''DD/MM/YYYY HH24:MI:SS'''||') DESC
                                              )
                                              WHERE TRUNC(FECHA) <= TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'')
                                              GROUP BY COMPANIA
                                              , ELEMENTO
                                              , SERIE)
                                      ORDER BY TO_DATE(TO_CHAR(D_MOVIMIENTO.FECHA,'||'''DD/MM/YYYY'''||') || '' '' || TO_CHAR(D_MOVIMIENTO.HORA,'||'''HH24:MI:SS'''||' ),'||'''DD/MM/YYYY HH24:MI:SS'''||') DESC
                                  )
                                  SELECT   DEVOLUTIVO.COMPANIA,
                                           BODEGA_DEPRECIAR.CPT_TIPOMOV,
                                           DEVOLUTIVO.ELEMENTO,
                                           DEVOLUTIVO.SERIE,
                                           DEVOLUTIVO.CUANTIAMIN,
                                           CASE WHEN BODEGA_DEPRECIAR.CPT_TIPOMOV = ''CR'' THEN BODEGA_DEPRECIAR.VALORTOTAL ELSE DEVOLUTIVO.NIIF_VALOR_TOTAL END NIIF_VALOR_TOTAL,
                                           NVL(REEXPRESARVIDAUTIL.DETERIORO,0) DETERIOROACUMULADO,
                                           NVL(REEXPRESARVIDAUTIL.NIIF_COSTOAJUSTADO,0) NIIF_COSTOAJUSTADO,
                                           NVL(REEXPRESARVIDAUTIL.NIIF_DEPACUMULADA,0) NIIF_DEPACUMULADA ,
                                           DEVOLUTIVO.NIIF_VALORBASE NIIF_VALORBASE,
                                           CASE WHEN TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'') = LAST_DAY(REEXPRESARVIDAUTIL.FECHACAMBIO)
                                           THEN -1 ELSE 0 END PER_CON_DETERIORO,
                                           CASE WHEN TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'') >  LAST_DAY(REEXPRESARVIDAUTIL.FECHACAMBIO) THEN
                                           ' || CASE WHEN  MI_DETERIORO = 'NO'  THEN '
                                                          CASE WHEN PCK_SYSMAN_UTL.FC_MESESCOMPLETOS( LAST_DAY(REEXPRESARVIDAUTIL.FECHACAMBIO),  TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'')) < 2 THEN  
                                                              (REEXPRESARVIDAUTIL.NIIF_COSTOAJUSTADO - NVL(DETERPER.DETPER,0) - REEXPRESARVIDAUTIL.NIIF_DEPACUMULADA)
                                                              ELSE 
                                                              REEXPRESARVIDAUTIL.NIIF_COSTOAJUSTADO
                                                          END '
                                                  ELSE 
                                                      'REEXPRESARVIDAUTIL.NIIF_COSTOAJUSTADO'
                                                   END
                                                 ||'         
                                                          
                                           ELSE CASE WHEN TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'') = LAST_DAY(REEXPRESARVIDAUTIL.FECHACAMBIO) THEN
                                                      CASE WHEN REEXPRESARVIDAUTIL.VIDAUTIL_ANT IS NULL
                                                           THEN ' || CASE WHEN  MI_DETERIORO = 'NO'
                                                                    THEN 'CASE WHEN BODEGA_DEPRECIAR.CPT_TIPOMOV = ''CR'' THEN  BODEGA_DEPRECIAR.VALORTOTAL  ELSE (DEVOLUTIVO.NIIF_VALORBASE - DEVOLUTIVO.SALVAMENTO ) END '
                                                                    --THEN 'DEVOLUTIVO.NIIF_VALORBASE '
                                                                    ELSE 'CASE WHEN BODEGA_DEPRECIAR.CPT_TIPOMOV = ''CR''
                                                                          THEN  BODEGA_DEPRECIAR.VALORTOTAL - REEXPRESARVIDAUTIL.DETERIORO - REEXPRESARVIDAUTIL.NIIF_DEPACUMULADA
                                                                          ELSE DEVOLUTIVO.NIIF_VALORBASE - REEXPRESARVIDAUTIL.DETERIORO - REEXPRESARVIDAUTIL.NIIF_DEPACUMULADA END '
                                                                    --ELSE 'BODEGA_DEPRECIAR.VALORTOTAL - REEXPRESARVIDAUTIL.DETERIORO - REEXPRESARVIDAUTIL.NIIF_DEPACUMULADA  '
                                                                    --ELSE 'DEVOLUTIVO.NIIF_VALORBASE - REEXPRESARVIDAUTIL.DETERIORO - REEXPRESARVIDAUTIL.NIIF_DEPACUMULADA  '
                                                                    END
                                                                 || '
                                                          ELSE ' || CASE WHEN  MI_DETERIORO = 'NO'
                                                                    THEN 'CASE WHEN BODEGA_DEPRECIAR.CPT_TIPOMOV = ''CR'' THEN BODEGA_DEPRECIAR.VALORTOTAL ELSE REEXPRESARVIDAUTIL.NIIF_COSTOAJUSTADO END  ' --REEXPRESARVIDAUTIL.NIIF_COSTOAJUSTADO'
                                                                    ELSE 'CASE WHEN BODEGA_DEPRECIAR.CPT_TIPOMOV = ''CR''
                                                                          THEN  BODEGA_DEPRECIAR.VALORTOTAL - REEXPRESARVIDAUTIL.DETERIORO - REEXPRESARVIDAUTIL.NIIF_DEPACUMULADA
                                                                          ELSE  (NVL(LIBROS.NIIF_VLRLIBROS,0) - NVL(DETERPER.DETPER,0)) END '
                                                                    --ELSE 'BODEGA_DEPRECIAR.VALORTOTAL - REEXPRESARVIDAUTIL.DETERIORO - REEXPRESARVIDAUTIL.NIIF_DEPACUMULADA'
                                                                    --ELSE 'DEVOLUTIVO.NIIF_VALORBASE - REEXPRESARVIDAUTIL.DETERIORO - REEXPRESARVIDAUTIL.NIIF_DEPACUMULADA'
                                                                    END
                                                  || ' END '
                                            || ' ELSE CASE WHEN DEVOLUTIVO.SALVAMENTO > 0 THEN
                                                    BODEGA_DEPRECIAR.VALORTOTAL - DEVOLUTIVO.SALVAMENTO
                                                  ELSE BODEGA_DEPRECIAR.VALORTOTAL --DEVOLUTIVO.NIIF_VALORBASE
                                                  END END
                                            END BASECALDEP,
                                           CASE WHEN TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'') >  LAST_DAY(REEXPRESARVIDAUTIL.FECHACAMBIO) THEN
                                              '|| CASE WHEN  MI_DETERIORO = 'NO' 
                                                   THEN ' CASE WHEN PCK_SYSMAN_UTL.FC_MESESCOMPLETOS( LAST_DAY(REEXPRESARVIDAUTIL.FECHACAMBIO),  TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'')) < 2 THEN  
                                                              (REEXPRESARVIDAUTIL.NIIF_COSTOAJUSTADO - NVL(DETERPER.DETPER,0) - REEXPRESARVIDAUTIL.NIIF_DEPACUMULADA)
                                                              ELSE 
                                                              REEXPRESARVIDAUTIL.NIIF_COSTOAJUSTADO
                                                          END '
                                                   ELSE 
                                                      'REEXPRESARVIDAUTIL.NIIF_COSTOAJUSTADO'
                                                   END 
                                               || '  
                                           ELSE CASE WHEN TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'') = LAST_DAY(REEXPRESARVIDAUTIL.FECHACAMBIO) THEN
                                                      CASE WHEN REEXPRESARVIDAUTIL.VIDAUTIL_ANT IS NULL
                                                           THEN ' || CASE WHEN  MI_DETERIORO = 'NO'
                                                                    THEN 'CASE WHEN REEXPVUTIL.ANTERIOR > 1 THEN 
                                                                                  DEVOLUTIVO.NIIF_VALORBASE - REEXPVUTIL.DETEANT - REEXPRESARVIDAUTIL.NIIF_DEPACUMULADA - DEVOLUTIVO.SALVAMENTO 
                                                                              ELSE 
                                                                                  DEVOLUTIVO.NIIF_VALORBASE - DEVOLUTIVO.SALVAMENTO
                                                                          END'
                                                                    ELSE 
                                                                    'DEVOLUTIVO.NIIF_VALORBASE - REEXPRESARVIDAUTIL.DETERIORO - REEXPRESARVIDAUTIL.NIIF_DEPACUMULADA - DEVOLUTIVO.SALVAMENTO'
                                                                    END
                                                                 || '
                                                          ELSE ' || CASE WHEN  MI_DETERIORO = 'NO'
                                                                    THEN 'CASE WHEN REEXPVUTIL.ANTERIOR > 1 THEN 
                                                                                  REEXPRESARVIDAUTIL.NIIF_COSTOAJUSTADO
                                                                              ELSE 
                                                                                  DEVOLUTIVO.NIIF_VALORBASE - DEVOLUTIVO.SALVAMENTO
                                                                              END'
                                                                    ELSE 
                                                                      '(NVL(LIBROS.NIIF_VLRLIBROS,0) - NVL(DETERPER.DETPER,0))'
                                                                    END
                                                  || ' END '
                                            || ' ELSE DEVOLUTIVO.NIIF_VALORBASE - DEVOLUTIVO.SALVAMENTO
                                                END
                                            END  BASECALAJUSTADO,
                                            
                                            CASE WHEN TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'') >  LAST_DAY(REEXPRESARVIDAUTIL.FECHACAMBIO) THEN
                                              '|| CASE WHEN  MI_DETERIORO = 'NO' 
                                                   THEN  'DETER.ACUM'
                                                   ELSE 
                                                      'REEXPVUTIL.DETEANT'
                                                   END 
                                               || '  
                                              ELSE CASE WHEN TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'') = LAST_DAY(REEXPRESARVIDAUTIL.FECHACAMBIO) THEN
                                                      CASE WHEN REEXPRESARVIDAUTIL.VIDAUTIL_ANT IS NULL
                                                           THEN ' || CASE WHEN  MI_DETERIORO = 'NO'
                                                                    THEN 'DETER.ACUM'
                                                                    ELSE 
                                                                      'REEXPVUTIL.DETEANT'
                                                                    END
                                                                 || '
                                                          ELSE ' || CASE WHEN  MI_DETERIORO = 'NO'
                                                                    THEN 'DETER.ACUM'
                                                                    ELSE 
                                                                      'REEXPVUTIL.DETEANT'
                                                                    END
                                                  || ' END '
                                            || ' ELSE REEXPVUTIL.DETEANT
                                                END
                                            END BASEDETERIORO, 
                                            DEVOLUTIVO.NIIF_VALORBASE  BASEVALORBASE,
                                            DEVOLUTIVO.SALVAMENTO BASESALVAMENTO,
                                           CASE WHEN TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'') >  LAST_DAY(REEXPRESARVIDAUTIL.FECHACAMBIO) THEN
                                                 REEXPRESARVIDAUTIL.VIDAUTIL
                                           ELSE CASE WHEN TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'') = LAST_DAY(REEXPRESARVIDAUTIL.FECHACAMBIO) THEN
                                                      CASE WHEN REEXPRESARVIDAUTIL.VIDAUTIL_ANT IS NULL
                                                          THEN ' || CASE WHEN  MI_DETERIORO = 'NO'
                                                                    THEN 'DEVOLUTIVO.NIIF_VIDA_UTIL '
                                                                    ELSE 'REEXPRESARVIDAUTIL.VIDAUTIL '
                                                                    END
                                                                 || '
                                                          ELSE ' || CASE WHEN  MI_DETERIORO = 'NO'
                                                                    THEN 'REEXPRESARVIDAUTIL.VIDAUTIL_ANT'
                                                                    ELSE 'REEXPRESARVIDAUTIL.VIDAUTIL'
                                                                    END
                                                  || ' END '
                                            || ' ELSE DEVOLUTIVO.NIIF_VIDA_UTIL
                                                END
                                            END NIIF_VIDA_UTIL,
                                           DEVOLUTIVO.NIIF_FECHAFUNCIONAMIENTO,
                                           BODEGA_DEPRECIAR.BODEGA_ORIGEN CLASE_BODEGA_ORIGEN,
                                           BODEGA_DEPRECIAR.BODEGA CLASE_BODEGA,
                                           DEVOLUTIVO.FECHAADQUISICION,
                                           DEVOLUTIVO.FECHAANULADA,
                                           DEVOLUTIVO.PLACAANULADA,
                                           BODEGA_DEPRECIAR.FECHA,
                                           DETERPER.DETPER DETAPLICAPER,
										   DEVOLUTIVO.PRIMSALIDASERVICIO
                                  FROM DEVOLUTIVO
                                  INNER JOIN BODEGA_DEPRECIAR
                                         ON DEVOLUTIVO.COMPANIA  = BODEGA_DEPRECIAR.COMPANIA
                                        AND DEVOLUTIVO.ELEMENTO = BODEGA_DEPRECIAR.ELEMENTO
                                        AND DEVOLUTIVO.SERIE    = BODEGA_DEPRECIAR.SERIE
                                  LEFT JOIN ( SELECT RV.COMPANIA,
                                               RV.ELEMENTO,
                                               RV.SERIE,
                                               RV.FECHACAMBIO,
                                               REEXVIDA.VIDAUTIL,
                                               RV.NIIF_COSTOAJUSTADO,
                                               RV.DETERIORO,
                                               RV.NIIF_DEPACUMULADA,
                                               REEXVIDA.VIDAUTIL_ANT
                                           FROM
                                               (SELECT REEXPRESARVIDAUTIL.COMPANIA,
                                                       REEXPRESARVIDAUTIL.ELEMENTO,
                                                       REEXPRESARVIDAUTIL.SERIE,
                                                       MAX(REEXPRESARVIDAUTIL.FECHACAMBIO) FECHACAMBIO,
                                                       NVL(DEPRECIAR.NIIF_COSTOAJUSTADO,0) NIIF_COSTOAJUSTADO,
                                                       SUM(REEXPRESARVIDAUTIL.DETERIORO) DETERIORO,
                                                       NVL(DEPRECIAR.NIIF_DEPACUMULADA,0) NIIF_DEPACUMULADA
                                                   FROM REEXPRESARVIDAUTIL
                                                    LEFT JOIN DEPRECIAR ON REEXPRESARVIDAUTIL.COMPANIA = DEPRECIAR.COMPANIA
                                                                         AND REEXPRESARVIDAUTIL.ELEMENTO = DEPRECIAR.ELEMENTO
                                                                         AND REEXPRESARVIDAUTIL.SERIE = DEPRECIAR.SERIE
                                                                         AND TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'') = DEPRECIAR.PERIODO
                                                   WHERE REEXPRESARVIDAUTIL.FECHACAMBIO <= TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'')
                                                   GROUP BY REEXPRESARVIDAUTIL.COMPANIA,
                                                       REEXPRESARVIDAUTIL.ELEMENTO,
                                                       REEXPRESARVIDAUTIL.SERIE,
                                                       NVL(DEPRECIAR.NIIF_COSTOAJUSTADO,0),
                                                       NVL(DEPRECIAR.NIIF_DEPACUMULADA,0)
                                               ) RV
                                               INNER JOIN  (SELECT COMPANIA, ELEMENTO, SERIE, FECHACAMBIO, VIDAUTIL,
                                                           LAG(REEXPRESARVIDAUTIL.VIDAUTIL) OVER (PARTITION BY REEXPRESARVIDAUTIL.COMPANIA,
                                                                                                       REEXPRESARVIDAUTIL.ELEMENTO,
                                                                                                       REEXPRESARVIDAUTIL.SERIE
                                                                                                       ORDER BY REEXPRESARVIDAUTIL.COMPANIA,
                                                                                                       REEXPRESARVIDAUTIL.ELEMENTO,
                                                                                                       REEXPRESARVIDAUTIL.SERIE,
                                                                                                       REEXPRESARVIDAUTIL.FECHACAMBIO) VIDAUTIL_ANT
                                                           FROM REEXPRESARVIDAUTIL) REEXVIDA ON RV.COMPANIA = REEXVIDA.COMPANIA
                                                 AND RV.ELEMENTO = REEXVIDA.ELEMENTO
                                                 AND RV.SERIE = REEXVIDA.SERIE
                                                 AND RV.FECHACAMBIO = REEXVIDA.FECHACAMBIO
                                           )  REEXPRESARVIDAUTIL      
                                  ON  DEVOLUTIVO.COMPANIA = REEXPRESARVIDAUTIL.COMPANIA
                                  AND DEVOLUTIVO.ELEMENTO = REEXPRESARVIDAUTIL.ELEMENTO
                                  AND DEVOLUTIVO.SERIE    =  REEXPRESARVIDAUTIL.SERIE
                                  LEFT JOIN  (SELECT COMPANIA,ELEMENTO,SERIE, COUNT(FECHACAMBIO) ANTERIOR,  
                                              SUM(REEXPRESARVIDAUTIL.DETERIORO) DETEANT
                                                           FROM REEXPRESARVIDAUTIL
                                                           WHERE REEXPRESARVIDAUTIL.FECHACAMBIO <= TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'')
                                                  GROUP BY COMPANIA,ELEMENTO,SERIE
                                           )  REEXPVUTIL 
                                  ON  DEVOLUTIVO.COMPANIA = REEXPVUTIL.COMPANIA
                                  AND DEVOLUTIVO.ELEMENTO = REEXPVUTIL.ELEMENTO
                                  AND DEVOLUTIVO.SERIE    =  REEXPVUTIL.SERIE
                                  LEFT JOIN  (SELECT COMPANIA,ELEMENTO,SERIE, 
                                              SUM(REEXPRESARVIDAUTIL.DETERIORO) DETPER
                                                           FROM REEXPRESARVIDAUTIL
                                                           WHERE LAST_DAY(REEXPRESARVIDAUTIL.FECHACAMBIO) = 
                                                           ' || CASE WHEN  MI_DETERIORO = 'NO'
                                                                    THEN 'ADD_MONTHS(TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY''),-1)'
                                                                    ELSE 'TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'')'
                                                                    END
                                                          || '
                                                  GROUP BY COMPANIA,ELEMENTO,SERIE
                                           )  DETERPER   
                                  ON  DEVOLUTIVO.COMPANIA = DETERPER.COMPANIA
                                  AND DEVOLUTIVO.ELEMENTO = DETERPER.ELEMENTO
                                  AND DEVOLUTIVO.SERIE    =  DETERPER.SERIE
                                  LEFT JOIN  (SELECT COMPANIA,ELEMENTO,SERIE, 
                                              SUM(REEXPRESARVIDAUTIL.DETERIORO) ACUM
                                                           FROM REEXPRESARVIDAUTIL
                                                           WHERE LAST_DAY(REEXPRESARVIDAUTIL.FECHACAMBIO) <= 
                                                           ' || CASE WHEN  MI_DETERIORO = 'NO'
                                                                    THEN 'ADD_MONTHS(TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY''),-1)'
                                                                    ELSE 'TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'')'
                                                                    END
                                                          || '
                                                  GROUP BY COMPANIA,ELEMENTO,SERIE
                                           )  DETER   
                                  ON  DEVOLUTIVO.COMPANIA = DETER.COMPANIA
                                  AND DEVOLUTIVO.ELEMENTO = DETER.ELEMENTO
                                  AND DEVOLUTIVO.SERIE    =  DETER.SERIE
                                  LEFT JOIN ( SELECT COMPANIA,ELEMENTO,SERIE, 
                                              NIIF_VLRLIBROS  
                                              FROM DEPRECIAR  
                                              WHERE TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'') = DEPRECIAR.PERIODO
                                  GROUP BY COMPANIA,ELEMENTO,SERIE,NIIF_VLRLIBROS) LIBROS
                                  ON DEVOLUTIVO.COMPANIA = LIBROS.COMPANIA
                                  AND DEVOLUTIVO.ELEMENTO = LIBROS.ELEMENTO
                                  AND DEVOLUTIVO.SERIE = LIBROS.SERIE';


                --JP Se ajusta para que la formula de la depreciación quede en un solo lado y no un reguero que no entiende nadie       
                --CC_2968(29/12/2025 JCROJAS): Se agrega validacion para cuando el parametro MIPARFECHASV sea SI y el campo PRIMSALIDASERVICIO no este nulo
				MI_CAL_DEPRECIA := '  CASE WHEN ''' || MI_DEPRESPONSABILIDAD || ''' = ''NO'' AND ''' || MI_DEPINSERVIBLE || ''' = ''NO'' AND VISTA.CLASE_BODEGA IN (40,50) AND TO_NUMBER(TO_CHAR(VISTA.FECHA,'||'''MMYYYY'''||')) <> '|| MI_M || MI_A ||' 
                                      THEN 0
                                      ELSE
                                          CASE WHEN NVL(VISTA.NIIF_VIDA_UTIL,0) = 0 ' ||
                                                  ' OR (VISTA.CLASE_BODEGA NOT IN (20,30' || MI_BODEGA_COMODATO ||' ' || MI_BODEGA_INSERV ||' ' || MI_BODEGA_RESPON ||' ) ' ||
                                                  '    AND TO_NUMBER(TO_CHAR(VISTA.FECHA,'||'''YYYY'''||')) <> '|| MI_A || 
                                                  '     AND TO_NUMBER(TO_CHAR(VISTA.FECHA,'||'''MM'''||'))   <> '|| MI_M ||') ' ||
                                        ' THEN 0  ' ||
                                        ' ELSE  VISTA.BASECALDEP/VISTA.NIIF_VIDA_UTIL ' ||
                                            '  /30* ' ||
                                            '  CASE WHEN VISTA.CLASE_BODEGA NOT IN (20,30' || MI_BODEGA_COMODATO ||' ' || MI_BODEGA_INSERV ||' ' || MI_BODEGA_RESPON ||') ' || 
                                            '       AND TO_NUMBER(TO_CHAR(VISTA.FECHA,'||'''YYYY'''||')) = '|| MI_A ||  
                                            '       AND TO_NUMBER(TO_CHAR(VISTA.FECHA,'||'''MM'''||'))   = '|| MI_M ||  
                                            '  THEN CASE WHEN VISTA.FECHAADQUISICION>= TO_DATE(''01/' || TO_CHAR(TO_DATE(MI_PERIODO, 'DD/MM/YYYY'),'MM/YYYY') || ''',''DD/MM/YYYY'') ' ||
                                            '             AND VISTA.FECHAADQUISICION<= TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'')' ||
                                            '            THEN CASE WHEN VISTA.FECHAADQUISICION >= TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'') ' ||
                                            '                      AND ''' || MI_DEPRECIA30DIAS || '''=''SI''' ||
                                            '                 THEN 30' ||
                                            '                 ELSE TO_NUMBER(TO_CHAR(VISTA.FECHAADQUISICION, ''DD''))' ||
                                            '                 END' ||
                                            '            ELSE TO_NUMBER(TO_CHAR(VISTA.FECHA, ''DD''))' ||
                                            '            END' ||
                                            '       - 1' ||
                                            '  ELSE CASE WHEN TO_NUMBER(TO_CHAR(VISTA.FECHAANULADA,'||'''YYYY'''||')) = '|| MI_A ||
                                            '             AND TO_NUMBER(TO_CHAR(VISTA.FECHAANULADA,'||'''MM'''||'))   = '|| MI_M||
                                            '             AND VISTA.PLACAANULADA NOT IN(0) ' ||
                                            '       THEN CASE WHEN VISTA.CLASE_BODEGA_ORIGEN IN(20) AND VISTA.CLASE_BODEGA IN (60,80)' || 
                                            '            THEN CASE WHEN VISTA.FECHAANULADA >= TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'')' ||
                                            '                      AND ''' || MI_DEPRECIA30DIAS || '''=''SI''' ||
                                            '                      THEN 30' ||
                                            '                      ELSE TO_NUMBER(TO_CHAR(VISTA.FECHAANULADA, ''DD''))' ||
                                            '                      END' ||
                                            '                      - 1' ||
                                            '            ELSE 0' ||
                                            '            END' ||
											'       ELSE CASE WHEN '''||MIPARFECHASV||'''=''SI'' AND VISTA.FECHAADQUISICION >= '''||MIPARINICIAFECHASV||''' ' ||
                                            '         THEN CASE WHEN VISTA.PRIMSALIDASERVICIO IS NOT NULL AND ('|| MI_A ||' > TO_NUMBER(TO_CHAR(VISTA.PRIMSALIDASERVICIO,'||'''YYYY'''||')) ' ||
                                            '             OR ('|| MI_A ||' = TO_NUMBER(TO_CHAR(VISTA.PRIMSALIDASERVICIO,'||'''YYYY'''||')) AND '|| MI_M||' >= TO_NUMBER(TO_CHAR(VISTA.PRIMSALIDASERVICIO,'||'''MM'''||')) )) ' ||
                                            '             THEN CASE WHEN TO_NUMBER(TO_CHAR(VISTA.PRIMSALIDASERVICIO,'||'''YYYY'''||')) = '|| MI_A ||
                                            '                   AND TO_NUMBER(TO_CHAR(VISTA.PRIMSALIDASERVICIO,'||'''MM'''||'))   = '|| MI_M||
                                            '                   AND '''|| MI_PARMESODIAS ||'''=''NO'' ' ||
                                            '                   THEN CASE WHEN VISTA.CUANTIAMIN NOT IN (0) THEN 30 ELSE ' ||
                                                                    CASE WHEN MI_DEPRECIA30DIAS='SI' 
                                                                        THEN 30
                                                                        ELSE MI_ULTIMODIA
                                                                    END
                                                                    || 
                                            '                       -' ||
                                            '                       CASE WHEN VISTA.PRIMSALIDASERVICIO >= TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'')' ||
                                            '                        AND ''' || MI_DEPRECIA30DIAS || '''=''SI''' ||
                                            '                           THEN 30' ||
                                            '                           ELSE TO_NUMBER(TO_CHAR(VISTA.PRIMSALIDASERVICIO, ''DD''))' ||
                                            '                       END END +' || CASE WHEN MI_PARDIASDESDEINGRESO = 'SI' THEN 1 ELSE 0 END ||
                                            '                   ELSE ' ||
                                            '                   30 ' ||
                                            '                  END ' ||				
											'             ELSE 0 END ' ||
                                            '       ELSE CASE WHEN TO_NUMBER(TO_CHAR(VISTA.FECHAADQUISICION,'||'''YYYY'''||')) = '|| MI_A ||
                                            '                  AND TO_NUMBER(TO_CHAR(VISTA.FECHAADQUISICION,'||'''MM'''||'))   = '|| MI_M||
                                            '                  AND '''|| MI_PARMESODIAS ||'''=''NO'' ' ||
                                            '            THEN CASE WHEN VISTA.CUANTIAMIN NOT IN (0) THEN 30 ELSE ' ||
                                                              CASE WHEN MI_DEPRECIA30DIAS='SI' 
                                                                   THEN 30
                                                                   ELSE MI_ULTIMODIA
                                                                   END
                                                             || 
                                            '                 -' ||
                                            '                CASE WHEN VISTA.FECHAADQUISICION >= TO_DATE('''|| MI_PERIODO || ''', ''DD/MM/YYYY'')' ||
                                            '                      AND ''' || MI_DEPRECIA30DIAS || '''=''SI''' ||
                                            '                 THEN 30' ||
                                            '                 ELSE TO_NUMBER(TO_CHAR(VISTA.FECHAADQUISICION, ''DD''))' ||
                                            '                 END END +' || CASE WHEN MI_PARDIASDESDEINGRESO = 'SI' THEN 1 ELSE 0 END ||
                                            '            ELSE ' ||
                                            '              30 ' ||
                                            '            END ' ||
											'           END ' ||																
                                            '       END ' ||
                                            '  END ' ||
                                         ' END END ';

                MI_CAMPOS:=   'TABLA.COMPANIA = VISTA.COMPANIA
                                 AND TABLA.ELEMENTO = VISTA.ELEMENTO
                                 AND TABLA.SERIE    = VISTA.SERIE';  
                                 
                                 
                --MI_STRSQL := ' SELECT ' || MI_CAL_DEPRECIA || ' FROM (' || MI_CONDICION ||') VISTA';
                --DBMS_OUTPUT.PUT_LINE('CONSULTA--> ' || MI_STRSQL);
                --EXECUTE IMMEDIATE MI_STRSQL INTO MI_VLRDEPRECIACION;
                --DBMS_OUTPUT.PUT_LINE('VLR DEPRECIACION--> ' || MI_VLRDEPRECIACION);
                --MI_STRSQL := ' SELECT  BASECALDEP  FROM (' || MI_CONDICION ||') VISTA';
                --DBMS_OUTPUT.PUT_LINE('CONSULTA--> ' || MI_STRSQL);
                --EXECUTE IMMEDIATE MI_STRSQL INTO MI_BASECALDEP;
                --DBMS_OUTPUT.PUT_LINE('VLR BASE DEP--> ' || MI_BASECALDEP);
                
                -- (AMONROY:26/10/2018) Se cambia el campo FECHASALIDASERVICIO a NIIF_FECHAFUNCIONAMIENTO para realizar el cálculo de los valores de Depreciacion
                -- (YBECERRA:10/04/2019) Se actualizan los CASE WHEN, porque las placas que se encuentran en comodato les esta sumando en la depreciacion acumulada el valor de la depreciacion del periiodo, y estas placas no deberian depreciarse 
                -- (EAMAYA:28/04/2020) Se cambiar el valor del campo NIIF_VLRLIBROS, segun migracion de sql server para que siempre reste  el NIIF_VALOR_TOTAL menos  el valor del deterioro cumulado - NIIF_VLRACUMULADO la suma se deja igual como estaba 
                -- (EAMAYA:06/05/2020) Se ajusta la resta entre campos DATE
                -- ECABRERA 7715500 16/06/2022 Se toma base calculo de la depreciacion el valor del ultimo movimientos
                -- JM FT JGUERRERO & APRIETO & SGARZON CC  920 19/02/2025
    			      -- se modifica  (TABLA.NIIF_VLRLIBROS,NIIF_SALDOAJ) y TABLA.NIIF_COSTOAJUSTADO
                -- JM FT JGUERRERO & NGAMEZ CC 920 17/03/2025 se modifica formula NIIF_VLRLIBROS
  			   -- CC:3968 - NCARDENAS - Se aplica redondeado en MI_CAL_DEPRECIA - fecha:1/04/2026
              MI_VALORES:= ' UPDATE  SET ' ||
                                ' TABLA.NIIF_VLRELEMENTO       = NVL(VISTA.NIIF_VALOR_TOTAL ,0),' ||
                                ' TABLA.NIIF_VLRDEPRECIACION   =  ROUND(' || MI_CAL_DEPRECIA || ',2), ' ||
                                ' TABLA.NIIF_VLRACUMULADO      = NVL(TABLA.NIIF_VLRACUMULADO,0) ' ||
                                                             ' + ROUND(' || MI_CAL_DEPRECIA || ',2) ,    ' ||
                                ' TABLA.NIIF_COSTOAJUSTADO     = VISTA.BASECALAJUSTADO '
                                                             -- || ' - CASE WHEN ''' || MI_DETERIORO || '''= ''NO'' AND VISTA.PER_CON_DETERIORO <>0 THEN  (' || MI_CAL_DEPRECIA || ')  ELSE 0 END '
                                                              || ',' ||
                                ' TABLA.NIIF_VLRLIBROS         =   VISTA.BASEVALORBASE -  NVL(VISTA.BASEDETERIORO,0)-  ( NVL(TABLA.NIIF_VLRACUMULADO,0) +  ROUND( '  || MI_CAL_DEPRECIA || ',2)),' ||
                                ' TABLA.NIIF_VLRDEPRECIACIONAJ = ROUND(' || MI_CAL_DEPRECIA || ',2) ,    ' ||
                                ' TABLA.NIIF_DEPACUMULADAAJ    = NVL(TABLA.NIIF_VLRACUMULADO,0) ' ||
                                                             ' + ROUND(' || MI_CAL_DEPRECIA || ',2) ,    ' ||
                                ' TABLA.NIIF_DEPACUMULADA      = NVL(TABLA.NIIF_VLRACUMULADO,0) ' ||
                                                             ' + ROUND(' || MI_CAL_DEPRECIA || ',2) ,    ' ||
                                ' TABLA.NIIF_ACUMDEPAJ         = NVL(TABLA.NIIF_VLRACUMULADO,0) ' ||
                                                             ' + ROUND(' || MI_CAL_DEPRECIA || ',2) ,    ' ||
                                ' TABLA.BODEGA                 = VISTA.CLASE_BODEGA, ' ||
                                ' TABLA.DETERIORO              = CASE WHEN VISTA.PER_CON_DETERIORO <>0 THEN NVL(VISTA.DETERIOROACUMULADO,0) ELSE 0 END, ' ||
                                ' TABLA.DATE_MODIFIED          = SYSDATE, ' ||
                                ' TABLA.MODIFIED_BY            = '''||UN_USUARIO||'''' ||
                             ' WHERE TABLA.COMPANIA ='''|| UN_COMPANIA ||'''' ||
                               ' AND TABLA.ELEMENTO BETWEEN ''' || UN_STRELEMENTOINICIAL || ''' AND '''|| UN_STRELEMENTOFINAL || '''' ||
                               ' AND TABLA.SERIE    BETWEEN '   || UN_PLACAINICIAL       ||   ' AND '  || UN_PLACAFINAL       ||
                               ' AND TABLA.PERIODO = TO_DATE('''|| MI_PERIODO || ''',''DD/MM/YYYY'')';

               MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => 'DEPRECIAR'
                                            ,UN_ACCION      => 'MM'
                                            ,UN_MERGEUSING  => MI_CONDICION
                                            ,UN_MERGEENLACE => MI_CAMPOS
                                            ,UN_MERGEEXISTE => MI_VALORES);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
              END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                  ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_MERGE_DEPRECIAR);


            END;


             -- JM FT JGUERRERO & APRIETO & SGARZON CC  920 19/02/2025 INI 
             -- toco enviarlo por aqui, arriba da error          
            BEGIN          
                               MI_CAMPOS := 'NIIF_SALDOAJ  = NIIF_VLRLIBROS';
            
                               MI_CONDICION:= 'COMPANIA ='''|| UN_COMPANIA ||'''' ||
                               ' AND ELEMENTO BETWEEN ''' || UN_STRELEMENTOINICIAL || ''' AND '''|| UN_STRELEMENTOFINAL || '''' ||
                               ' AND SERIE    BETWEEN '   || UN_PLACAINICIAL       ||   ' AND '  || UN_PLACAFINAL       ||
                               ' AND PERIODO = TO_DATE('''|| MI_PERIODO || ''',''DD/MM/YYYY'')';
                               
                               PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'DEPRECIAR'
                                                      ,UN_ACCION    => 'M'
                                                      ,UN_CAMPOS    => MI_CAMPOS
                                                      ,UN_CONDICION => MI_CONDICION);
                                                      
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
              END;
               -- JM FT JGUERRERO & APRIETO & SGARZON CC  920 19/02/2025 FIN      

            --VERIFICA SI YA LLEGO AL LIMITE DE LA DEPRECIACION Y ACTUALIZA EL VALOR DE LA DEPRECIACION AL SALDO QUE LE QUEDABA
            --QUEDANDO EL SALDO EN LIBROS EN CERO - SE MODIFICO CON EL TICKET 7729189 
            --MOD JM 05/02/2025 CC 92, para tomar en cuenta el clr de Salvamento y no depreciar de mas 
            --Solicion temporal mientras se estructura el historico de salvamentos (depreciar hacia atras)
            --MOD JM 11-04-2025 CC 92 debido al ajuste de arriba del CC 920 se realizan nuevos ajustes 
            BEGIN
              BEGIN
              
              FOR  RS1 IN (SELECT  DEPRECIAR.ELEMENTO, DEPRECIAR.SERIE, SALVAMENTO, DEPRECIAR.NIIF_VLRELEMENTO, DEPRECIAR.NIIF_VLRDEPRECIACION, DEPRECIAR.NIIF_VLRACUMULADO, DEPRECIAR.NIIF_VLRAJUSTADO, DEPRECIAR.NIIF_COSTOAJUSTADO, 
                                      DEPRECIAR.NIIF_VLRLIBROS, DEPRECIAR.NIIF_VLRDEPRECIACIONAJ, DEPRECIAR.NIIF_DEPACUMULADAAJ, DEPRECIAR.NIIF_SALDOAJ, DEPRECIAR.NIIF_DEPACUMULADA 
                                  FROM DEPRECIAR 
                                  INNER JOIN DEVOLUTIVO
                                  ON DEPRECIAR.COMPANIA = DEVOLUTIVO.COMPANIA
                                  AND DEPRECIAR.ELEMENTO = DEVOLUTIVO.ELEMENTO
                                  AND DEPRECIAR.SERIE = DEVOLUTIVO.SERIE
                                  WHERE DEPRECIAR.COMPANIA         = UN_COMPANIA
                                  AND DEPRECIAR.ANO                = MI_A
                                  AND MES                          = MI_M
                                  AND DEVOLUTIVO.APLICA_DETERIORO  = 0
                                  AND (DEPRECIAR.NIIF_VLRLIBROS <= 0 OR DEPRECIAR.NIIF_VLRLIBROS < CASE WHEN SALVAMENTO > 0 THEN SALVAMENTO ELSE DEPRECIAR.NIIF_VLRLIBROS END )
                                  AND (DEVOLUTIVO.PLACAANULADA = 0 OR (DEVOLUTIVO.PLACAANULADA = -1 AND DEVOLUTIVO.FECHAANULADA >= TO_DATE('01/'|| MI_M ||'/' || MI_A || '','DD/MM/YYYY HH24:MI:SS')))
                                  AND DEVOLUTIVO.ELEMENTO BETWEEN UN_STRELEMENTOINICIAL AND UN_STRELEMENTOFINAL 
                                  AND DEVOLUTIVO.SERIE    BETWEEN UN_PLACAINICIAL AND UN_PLACAFINAL)
                                  
                LOOP
                
                --JM MOD CC 1569 05-15-2025 en caso de no tener Salvamento 
                MI_CAMPOS:= 'NIIF_VLRDEPRECIACION   = ' || CASE WHEN NVL(RS1.SALVAMENTO,0) > 0 THEN 
                                  CASE WHEN (RS1.NIIF_VLRLIBROS - NVL(RS1.SALVAMENTO,0)) < 0 THEN RS1.NIIF_VLRDEPRECIACION + (RS1.NIIF_VLRLIBROS - NVL(RS1.SALVAMENTO,0)) 
                                              ELSE 0 END ELSE RS1.NIIF_VLRDEPRECIACION + RS1.NIIF_VLRLIBROS END ||'
                              ,NIIF_VLRACUMULADO      = (NIIF_VLRELEMENTO - NVL( '|| RS1.SALVAMENTO ||',0))
                              ,NIIF_VLRLIBROS         =  NVL( '|| RS1.SALVAMENTO ||',0)
                              ,NIIF_VLRDEPRECIACIONAJ = ' || CASE WHEN NVL(RS1.SALVAMENTO,0) > 0 THEN 
                                  CASE WHEN (RS1.NIIF_VLRLIBROS - NVL(RS1.SALVAMENTO,0)) < 0 THEN RS1.NIIF_VLRDEPRECIACION + (RS1.NIIF_VLRLIBROS - NVL(RS1.SALVAMENTO,0)) 
                                              ELSE 0 END ELSE RS1.NIIF_VLRDEPRECIACION + RS1.NIIF_VLRLIBROS END ||'
                              ,NIIF_DEPACUMULADAAJ    = (NIIF_VLRELEMENTO - NVL( '|| RS1.SALVAMENTO ||',0))
                              ,NIIF_SALDOAJ           = 0
                              ,NIIF_ACUMDEPAJ         = (NIIF_VLRELEMENTO - NVL( '|| RS1.SALVAMENTO ||',0))
                              ,NIIF_DEPACUMULADA      = (NIIF_VLRELEMENTO - NVL( '|| RS1.SALVAMENTO ||',0))';

                MI_CONDICION:='COMPANIA ='''|| UN_COMPANIA ||'''
                           AND ELEMENTO ='|| RS1.ELEMENTO ||'
                           AND SERIE    ='|| RS1.SERIE ||'
                           AND ANO      ='|| MI_A ||'
                           AND MES      ='|| MI_M ||'
                          ';

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'DEPRECIAR'
                                                      ,UN_ACCION    => 'M'
                                                      ,UN_CAMPOS    => MI_CAMPOS
                                                      ,UN_CONDICION => MI_CONDICION);

              END LOOP DEPREACU;
              
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
              
              END;

              --VERIFICA SI YA LLEGO AL LIMITE DE LA DEPRECIACION Y APLICA DETERIORO
              --QUEDANDO EL SALDO EN LIBROS EN CERO Y EL ACUMULADO CON EL DEL MES ANTERIOR - TICKET - 7721550

              BEGIN

              IF UN_MESFINAL = 1 THEN
              MI_ANO := UN_ANOFINAL - 1;
              MI_MES := 12;
              ELSE
              MI_ANO := UN_ANOFINAL;
              MI_MES := UN_MESFINAL - 1;
              END IF;

              FOR  RS IN (SELECT  DEPRECIAR.ELEMENTO, DEPRECIAR.SERIE
                                  FROM DEPRECIAR
                                  INNER JOIN DEVOLUTIVO
                                  ON DEPRECIAR.COMPANIA = DEVOLUTIVO.COMPANIA
                                  AND DEPRECIAR.ELEMENTO = DEVOLUTIVO.ELEMENTO
                                  AND DEPRECIAR.SERIE = DEVOLUTIVO.SERIE
                                  WHERE DEPRECIAR.COMPANIA         = UN_COMPANIA
                                  AND DEPRECIAR.ANO                = MI_ANO
                                  AND MES                          = MI_MES
                                  AND DEVOLUTIVO.APLICA_DETERIORO  = -1
                                  AND DEPRECIAR.NIIF_VLRLIBROS <= 2
                                  AND DEVOLUTIVO.ELEMENTO BETWEEN UN_STRELEMENTOINICIAL AND UN_STRELEMENTOFINAL
                                  AND DEVOLUTIVO.SERIE    BETWEEN UN_PLACAINICIAL AND UN_PLACAFINAL
                                  AND (DEVOLUTIVO.PLACAANULADA = 0 OR (DEVOLUTIVO.PLACAANULADA = -1 AND DEVOLUTIVO.FECHAANULADA >= TO_DATE('01/'|| MI_M ||'/' || MI_A || '','DD/MM/YYYY HH24:MI:SS'))))

              LOOP

              SELECT NIIF_VLRACUMULADO
              INTO MI_VLRDEPRECIACION
              FROM DEPRECIAR 
              WHERE DEPRECIAR.COMPANIA = UN_COMPANIA
              AND DEPRECIAR.ELEMENTO = RS.ELEMENTO
              AND DEPRECIAR.SERIE = RS.SERIE
              AND DEPRECIAR.ANO = MI_ANO
              AND DEPRECIAR.MES = MI_MES;

                MI_CAMPOS:= 'NIIF_VLRDEPRECIACION     = NIIF_VLRDEPRECIACION+NIIF_VLRLIBROS
                            ,NIIF_VLRACUMULADO        = NIIF_VLRDEPRECIACION+NIIF_VLRLIBROS + '|| MI_VLRDEPRECIACION ||'
                            ,NIIF_DEPACUMULADAAJ      = NIIF_VLRDEPRECIACION+NIIF_VLRLIBROS + '|| MI_VLRDEPRECIACION ||'
                            ,NIIF_DEPACUMULADA        = NIIF_VLRDEPRECIACION+NIIF_VLRLIBROS + '|| MI_VLRDEPRECIACION ||'
                            ,NIIF_ACUMDEPAJ           = NIIF_VLRDEPRECIACION+NIIF_VLRLIBROS + '|| MI_VLRDEPRECIACION ||'
                            ,NIIF_VLRDEPRECIACIONAJ   = 0
                            ,NIIF_VLRLIBROS           = 0
                            ,NIIF_SALDOAJ             = 0';

                MI_CONDICION:='COMPANIA ='''|| UN_COMPANIA ||'''
                           AND ELEMENTO ='|| RS.ELEMENTO ||'
                           AND SERIE    ='|| RS.SERIE ||'
                           AND ANO      ='|| UN_ANOFINAL ||'
                           AND MES      ='|| UN_MESFINAL ||'
                          ';

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'DEPRECIAR'
                                                      ,UN_ACCION    => 'M'
                                                      ,UN_CAMPOS    => MI_CAMPOS
                                                      ,UN_CONDICION => MI_CONDICION);

              END LOOP DEPRE;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

              END;
              
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE
                ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DEPRECIAR);
            END;
            --CC_1714 GROJAS: SE ACTUALIZA LA TABLA DEVOLUTIVO CON EL RESULTADO DE LA TABLA DEPRECIAR
            BEGIN
                BEGIN
                  MI_CONDICION := 'SELECT  SERIE
                                  ,ELEMENTO
                                  ,COMPANIA
                                  ,NIIF_COSTOAJUSTADO
                                  ,NIIF_VLRDEPRECIACION
                                  ,NIIF_VLRLIBROS
                                  ,NIIF_DEPACUMULADA
                                  ,NIIF_SALDOAJ
                                  ,NIIF_VLRDEPRECIACIONAJ
                                  ,NIIF_DEPACUMULADAAJ
                            FROM  DEPRECIAR
                            WHERE DEPRECIAR.COMPANIA ='''|| UN_COMPANIA ||'''
                              AND DEPRECIAR.PERIODO  =TO_DATE('''|| MI_PERIODO ||''',''DD/MM/YYYY HH24:MI:SS'')
                              AND DEPRECIAR.ELEMENTO BETWEEN '''|| UN_STRELEMENTOINICIAL ||''' AND '''|| UN_STRELEMENTOFINAL ||'''
                              AND DEPRECIAR.SERIE    BETWEEN '|| UN_PLACAINICIAL ||' AND '|| UN_PLACAFINAL ||'';
                  MI_CAMPOS :=  'TABLA.COMPANIA = VISTA.COMPANIA
                             AND TABLA.ELEMENTO = VISTA.ELEMENTO
                             AND TABLA.SERIE    = VISTA.SERIE';
                  MI_VALORES:='UPDATE SET  TABLA.NIIF_COSTOAJUSTADO      = VISTA.NIIF_COSTOAJUSTADO
                                          ,TABLA.NIIF_VLRDEPRECIACION   = VISTA.NIIF_VLRDEPRECIACION
                                          ,TABLA.NIIF_VLRLIBROS         = VISTA.NIIF_VLRLIBROS
                                          ,TABLA.NIIF_DEPACUMULADA      = VISTA.NIIF_DEPACUMULADA
                                          ,TABLA.NIIF_SALDOAJ           = VISTA.NIIF_SALDOAJ
                                          ,TABLA.NIIF_VLRDEPRECIACIONAJ = VISTA.NIIF_VLRDEPRECIACIONAJ
                                          ,TABLA.NIIF_DEPACUMULADAAJ    = VISTA.NIIF_DEPACUMULADAAJ';
                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => 'DEVOLUTIVO'
                                              ,UN_ACCION      => 'MM'
                                              ,UN_MERGEUSING  => MI_CONDICION
                                              ,UN_MERGEENLACE => MI_CAMPOS
                                              ,UN_MERGEEXISTE => MI_VALORES);
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                    ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_MERGE_DEVOLUTIVO);
           END;
          END IF;
        END LOOP;
      END LOOP;

      MI_RTA:='TRUE';
      BEGIN
       MI_TABLA  := 'CONTROL_PROCESOS';
       MI_CAMPOS := 'ESTADO_EJECUCION = '|| '''FINALIZADO''' ||',
                     FECHA_FINAL = SYSDATE,
                     LOG_RESULTADO = '''|| 'OK' ||''',
                     DATE_MODIFIED = SYSDATE,
                    MODIFIED_BY = ''' || MI_USUARIO ||''' ';
                  MI_CONDICION := 'ID_PROCESO = ''' || MI_ID_PROCESO || ''' ';
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                                         UN_ACCION    => 'M',
                                                         UN_CAMPOS    => MI_CAMPOS,
                                                         UN_CONDICION => MI_CONDICION);
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;
        
        
    EXCEPTION
   WHEN OTHERS THEN
      MI_TABLA  := 'CONTROL_PROCESOS';
                MI_CAMPOS := 'ESTADO_EJECUCION = '||'''ERRORES'''||',
                              FECHA_FINAL = SYSDATE,
                              LOG_RESULTADO = '''||SQLCODE || SQLERRM ||''',
                              DATE_MODIFIED = SYSDATE,
                              MODIFIED_BY = ''' || MI_USUARIO ||''' ';
                MI_CONDICION := 'ID_PROCESO = ''' || MI_ID_PROCESO || ''' ';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                                       UN_ACCION    => 'M',
                                                       UN_CAMPOS    => MI_CAMPOS,
                                                       UN_CONDICION => MI_CONDICION);
      MI_RTA := SQLCODE ||  
      SQLERRM;
    END;
    IF MI_ERRORES_G1 > 0 OR MI_ERRORES_G3 > 0 THEN
           RETURN MI_MENSAJE;
     END IF;
    RETURN MI_RTA;

END FC_CALDEPRECIARH_NIIF;

-- 2
FUNCTION FC_PLANTILLACOMPONENTES
(   

  /*
      NAME 			       : FC_PLANTILLACOMPONENTES
      AUTHORS 			   : SYSMAN SAS
      AUTHOR MIGRACION : ANA YESSICA SANA
      DATE MIGRADOR	   : 12/09/2018
      TIME				     : 05:00 PM
      DESCRIPTION		   : Retorna clob con los datos de plantilla de elementos componentes
      MODIFICATIONS	   : 

     @NAME: generarPlantComponentes
     @METHOD: GET
  */

    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA ,
    UN_ESTACION     IN DETALLE_ELEMENTOSCOMPONENTES.CODIGO_COMPONENTE%TYPE,
    UN_BODEGA       IN DEVOLUTIVO.BODEGA%TYPE,
    UN_RESPONSABLE  IN DEVOLUTIVO.RESPONSABLE%TYPE,
    UN_INFORME      IN PCK_SUBTIPOS.TI_LOGICO,
    UN_DEPENDENCIA  IN DEVOLUTIVO.DEPENDENCIA%TYPE
)
RETURN CLOB AS 
    MI_CONSULTA   PCK_SUBTIPOS.TI_STRSQL;
    MI_CONCAT     VARCHAR2(50 CHAR);
    MI_SERIEANT   VARCHAR2(20 CHAR);
    MI_SERIE      VARCHAR2(20 CHAR);
    MI_DESCRIPCION  VARCHAR2(500 CHAR);
    MI_MARCA      VARCHAR2(150 CHAR);
    MI_UBICACION  VARCHAR2(80 CHAR);
    MI_VALORLIBROS  NUMBER(20,2);
    MI_ESTBUENO   VARCHAR2(20 CHAR);
    MI_ESTMALO    VARCHAR2(20 CHAR);
    MI_ESTREGULAR VARCHAR2(20 CHAR);
    MI_CADENA     CLOB;
    MI_RS         SYS_REFCURSOR;
    MI_DEPENDENCIA DEPENDENCIA.NOMBRE%TYPE;
    MI_RESPONSABLE TERCERO.NOMBRE%TYPE;
    MI_CARGO       DEPENDENCIA_RESPONSABLE.CARGO%TYPE;
    MI_CONTADOR    PCK_SUBTIPOS.TI_ENTERO := 1;
    MI_FECHA       VARCHAR2(50 CHAR);
    MI_MODELO      DEVOLUTIVO.MODELO%TYPE;
    MI_VAR         NUMBER;
    MI_RESPONSABLESER    VARCHAR2(100 CHAR);
    MI_CAMBIORESPONS    VARCHAR2(100 CHAR);
    MI_RESPONSABLES   CLOB;
BEGIN
    MI_VAR := 0; 
    MI_CAMBIORESPONS := ' ';
    BEGIN

        MI_CONSULTA := ' WITH CONSULTA AS (                      
                            SELECT D.COMPANIA, D.CODIGO_COMPONENTE ELEMENTO,                      
                                   D.SERIE_COMPONENTE SERIE, C.NOMBRE NOMBRECIUDAD, 
                                   E.DIRECCION, E.CONSECUTIVO           
                              FROM DETALLE_ELEMENTOSCOMPONENTES D                       
                             INNER JOIN ELEMENTOSCOMPONENTES E                       
                                ON D.COMPANIA = E.COMPANIA                      
                               AND D.CODIGO_ELEMENTO = E.CONSECUTIVO                      
                             INNER JOIN DEVOLUTIVO DE                      
                                ON D.COMPANIA = DE.COMPANIA                      
                               AND D.CODIGO_COMPONENTE = DE.ELEMENTO                      
                               AND D.SERIE_COMPONENTE = DE.SERIE                      
                              LEFT JOIN CIUDAD C                      
                                ON E.PAIS          = C.PAIS                      
                               AND E.DEPARTAMENTO = C.DEPARTAMENTO                      
                               AND E.CIUDAD       = C.CODIGO                       
                              LEFT JOIN DEPRECIAR                      
                                ON DE.COMPANIA                              = DEPRECIAR.COMPANIA                      
                               AND DE.ELEMENTO                             = DEPRECIAR.ELEMENTO                      
                               AND DE.SERIE                                = DEPRECIAR.SERIE                      
                               AND DE.SERIE                                = DEPRECIAR.SERIE                      
                               AND TO_CHAR(LAST_DAY(SYSDATE),''DD/MM/YYYY'') = TO_CHAR(DEPRECIAR.PERIODO,''DD/MM/YYYY'')                      
                             WHERE DE.COMPANIA = '''  || UN_COMPANIA || ''' AND ' || 
                                   CASE WHEN UN_RESPONSABLE IS NOT NULL THEN ' DE.RESPONSABLE = ''' || UN_RESPONSABLE || '''' ELSE '' END ||  
                                   CASE WHEN UN_ESTACION IS NOT NULL THEN ' E.CONSECUTIVO = ''' || UN_ESTACION || '''' ELSE '' END ||  
                                   CASE WHEN UN_BODEGA IS NOT NULL THEN ' DE.BODEGA = ''' || UN_BODEGA || '''' ELSE '' END ||  
                                   CASE WHEN UN_DEPENDENCIA IS NOT NULL THEN ' DE.DEPENDENCIA = ''' || UN_DEPENDENCIA || '''' ELSE '' END || '
                              )
                      SELECT  DEPENDENCIA.NOMBRE NOMBREDEPENDENCIA,
                              CASE WHEN TRESPONSABLE.NOMBRE IS NULL THEN '' '' ELSE TRESPONSABLE.NOMBRE END NOMBRE,
                              CASE WHEN DP.CARGO IS NULL THEN '' '' ELSE DP.CARGO END CARGO,
                              SUBSTR(D.RESPONSABLE || '' - '' || CASE
                                WHEN  TRESPONSABLE.NOMBRE IS NULL
                                THEN '' ''
                                ELSE  TRESPONSABLE.NOMBRE 
                              END, 0, 70)   RESPONSABLE,
                              TO_CHAR(D.SERIEANTERIOR)  ETIQUETANTERIOR,
                              D.SERIE,
                              CASE WHEN D.DESCRIPCION IS NULL THEN ''.'' ELSE SUBSTR(D.DESCRIPCION, 0,80) END DESCRIPCION,
                              CASE WHEN D.MARCA IS NULL THEN '' '' ELSE D.MARCA END MARCA,
                              CASE WHEN D.MODELO IS NULL THEN '' '' ELSE D.MODELO END MODELO,   
                              SUBSTR(CASE WHEN CIUDADDEV.NOMBRE IS NOT NULL THEN CIUDADDEV.NOMBRE || '', '' || D.DIRECCION ELSE '' '' END, 0, 70) UBICACION,
                              CASE WHEN D.VALOR IS NULL THEN 0 ELSE D.VALOR END VLRLIBROS,
                              CASE WHEN D.ESTADO IN (''B'') THEN ''X'' ELSE '' '' END BUENO,
                              CASE WHEN D.ESTADO IN (''M'') THEN ''X'' ELSE '' '' END MALO,
                              CASE WHEN D.ESTADO IN (''R'') THEN ''X'' ELSE '' '' END REGULAR                              
                        FROM DEVOLUTIVO D ' ||
                              CASE WHEN TRIM(UN_ESTACION) IS NULL 
                              THEN ' LEFT '
                              ELSE ' INNER '
                              END || ' 
                             JOIN CONSULTA C
                          ON D.COMPANIA = C.COMPANIA
                         AND D.ELEMENTO = C.ELEMENTO
                         AND D.SERIE = C.SERIE
                       INNER JOIN TERCERO TRESPONSABLE
                          ON D.COMPANIA              = TRESPONSABLE.COMPANIA
                         AND D.RESPONSABLE          = TRESPONSABLE.NIT
                         AND D.SUCURSAL_RESPONSABLE = TRESPONSABLE.SUCURSAL
                        LEFT JOIN DEPENDENCIA
                          ON D.COMPANIA = DEPENDENCIA.COMPANIA
                         AND D.DEPENDENCIA = DEPENDENCIA.CODIGO
                        LEFT JOIN DEPENDENCIA_RESPONSABLE DP
                          ON D.COMPANIA = DP.COMPANIA
                         AND D.DEPENDENCIA = DP.DEPENDENCIA
                         AND D.RESPONSABLE = DP.RESPONSABLE
                         AND D.SUCURSAL_RESPONSABLE = DP.SUCURSAL
                        LEFT JOIN CIUDAD CIUDADDEV
                          ON D.PAIS          = CIUDADDEV.PAIS
                         AND D.DEPARTAMENTO = CIUDADDEV.DEPARTAMENTO
                         AND D.CIUDAD       = CIUDADDEV.CODIGO
                       WHERE D.COMPANIA = ''' || UN_COMPANIA || ''' AND ' ||
                             CASE WHEN UN_RESPONSABLE IS NOT NULL THEN ' D.RESPONSABLE = ''' || UN_RESPONSABLE || '''' ELSE '' END ||  
                             CASE WHEN UN_ESTACION IS NOT NULL THEN ' C.CONSECUTIVO = ''' || UN_ESTACION || '''' ELSE '' END ||  
                             CASE WHEN UN_BODEGA IS NOT NULL THEN ' D.BODEGA = ''' || UN_BODEGA || '''' ELSE '' END ||  
                             CASE WHEN UN_DEPENDENCIA IS NOT NULL THEN ' D.DEPENDENCIA = ''' || UN_DEPENDENCIA || '''' ELSE '' END
                        || CASE WHEN UN_DEPENDENCIA IS NOT NULL THEN ' ORDER BY D.RESPONSABLE ' ELSE '' END;




         OPEN MI_RS FOR MI_CONSULTA;

         LOOP

            FETCH MI_RS INTO MI_DEPENDENCIA,MI_RESPONSABLE,MI_CARGO, MI_RESPONSABLESER,
                             MI_SERIEANT, MI_SERIE, MI_DESCRIPCION, MI_MARCA, MI_MODELO, 
                             MI_UBICACION
                             , MI_VALORLIBROS, MI_ESTBUENO, 
                             MI_ESTMALO, MI_ESTREGULAR;

                             --DBMS_OUTPUT.PUT_LINE (MI_SERIE);
            --IF MI_DEPENDENCIA IS NULL THEN              
            --RETURN NULL; 
            --END IF;

            EXIT WHEN MI_RS%NOTFOUND;

                IF MI_CONTADOR = 1 THEN 


                    MI_CADENA := MI_CADENA || TO_CLOB(MI_DEPENDENCIA || PCK_DATOS.GL_SEPARADOR_COL || 
                                 MI_RESPONSABLE || PCK_DATOS.GL_SEPARADOR_COL || 
                                 MI_CARGO       || PCK_DATOS.GL_SEPARADOR_REG);

                    --MI_CAMBIORESPONS := MI_RESPONSABLESER;
                    --MI_RESPONSABLES := TO_CLOB(MI_RESPONSABLES || PCK_DATOS.GL_SEPARADOR_COL || MI_RESPONSABLESER ) ;

                END IF;

               IF UN_DEPENDENCIA IS NOT NULL THEN
                  IF MI_CAMBIORESPONS <> MI_RESPONSABLESER THEN
                      MI_CAMBIORESPONS := MI_RESPONSABLESER;
                      MI_CADENA := MI_CADENA || TO_CLOB( ',.RESP.,' || MI_RESPONSABLESER || PCK_DATOS.GL_SEPARADOR_REG) ;

                  END IF;
               END IF;

                MI_CADENA := MI_CADENA || TO_CLOB( MI_SERIEANT || PCK_DATOS.GL_SEPARADOR_COL ||
                             MI_SERIE || PCK_DATOS.GL_SEPARADOR_COL ||
                             MI_DESCRIPCION || PCK_DATOS.GL_SEPARADOR_COL ||
                             MI_MARCA || PCK_DATOS.GL_SEPARADOR_COL ||
                             MI_MODELO || PCK_DATOS.GL_SEPARADOR_COL ||
                             SUBSTR(MI_UBICACION, 0 ,80) ) ;
               IF UN_INFORME NOT IN (0) THEN 
                  MI_CADENA := MI_CADENA || TO_CLOB( PCK_DATOS.GL_SEPARADOR_COL ||
                  MI_VALORLIBROS || PCK_DATOS.GL_SEPARADOR_COL ||
                  MI_ESTBUENO || PCK_DATOS.GL_SEPARADOR_COL ||
                  MI_ESTMALO || PCK_DATOS.GL_SEPARADOR_COL ||
                  MI_ESTREGULAR ) ; 
               END IF; 




               MI_CADENA :=  MI_CADENA || TO_CLOB( PCK_DATOS.GL_SEPARADOR_REG) ;

               MI_CONTADOR := MI_CONTADOR + 1;
               MI_VAR := 1;
         END LOOP;

         IF MI_VAR = 0 THEN 
            MI_CADENA := NULL; 
         END IF;
      -- 20180917_649:@asana
         IF MI_CONTADOR = 1 THEN 
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                    ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_NODATA);
        END; 
         END IF;
    END;

  RETURN MI_CADENA;
END FC_PLANTILLACOMPONENTES;

-- 3
PROCEDURE FC_VIDAUTILCOMPONENTE(

/*
      NAME            : FC_VIDAUTILCOMPONENTE
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR CREATION   : YESSICA SANA
    DATE CREATION     : 19/09/2018
    TIME              : 05:00 PM
    SOURCE MODULE     : ALMACEN (10)
    DESCRIPTION       : Actualiza los meses de vida util desde el formulario Componentes a la tabla Devolutivo. 
    MODIFIED BY       : 

    @NAME  : vidaUtilComponente
    @METHOD: PUT

    */
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ELEMENTO     IN DEVOLUTIVO.ELEMENTO%TYPE,
    UN_SERIE        IN DEVOLUTIVO.SERIE%TYPE,
    UN_MES          IN DEVOLUTIVO.MESESVIDAUTILPLACA%TYPE
) AS 

MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
MI_RTA          NUMBER;

BEGIN
    BEGIN
        BEGIN 
            MI_TABLA := 'DEVOLUTIVO';
            MI_CAMPOS  := ' MESESVIDAUTILPLACA = ' || UN_MES || ', NIIF_VIDA_UTIL = ' || UN_MES ;
            MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || '''
                        AND ELEMENTO = ''' || UN_ELEMENTO || ''' 
                        AND    SERIE =   ' || UN_SERIE ;

            MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA      => MI_TABLA,
                                          UN_ACCION     => 'M',
                                          UN_CAMPOS      => MI_CAMPOS,
                                          UN_CONDICION  => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                   PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                             ,UN_ERROR_COD => PCK_ERRORES.ERR_ALM_MESESVIDAUTILCOMPONENT);
    END;

END FC_VIDAUTILCOMPONENTE;

-- 4
PROCEDURE PR_ACT_COMPONENTE(

/*
      NAME            : PR_ACT_COMPONENTE
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR CREATION   : YESSICA SANA
    DATE CREATION     : 19/09/2018
    TIME              : 05:00 PM
    SOURCE MODULE     : ALMACEN (10)
    DESCRIPTION       : Agrega el componente si se configuraal elemento 
    MODIFIED BY       : 

    @NAME  : InsertarComponente
    @METHOD: POST

    */

 UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
 UN_TIPO          IN D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
 UN_MOVIMIENTO    IN D_MOVIMIENTO.MOVIMIENTO%TYPE,
 UN_CODIGO        IN D_MOVIMIENTO.CODIGO%TYPE    

 ) AS 

MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
MI_RTA          NUMBER;
MI_ELEMENTO     D_MOVIMIENTO.ELEMENTO%TYPE;
MI_SERIE        D_MOVIMIENTO.SERIE%TYPE;
MI_COMPONENTE   D_MOVIMIENTO.COMPONENTE%TYPE;
MI_EXISTE       NUMBER;
MI_PAIS         D_MOVIMIENTO.PAIS%TYPE;
MI_DEPARTAMENTO D_MOVIMIENTO.DEPARTAMENTO%TYPE;
MI_CIUDAD       D_MOVIMIENTO.CIUDAD%TYPE;
MI_UBICACION    D_MOVIMIENTO.DIRECCION%TYPE;

BEGIN
    BEGIN
        SELECT ELEMENTO, SERIE, COMPONENTE 
          INTO MI_ELEMENTO, MI_SERIE, MI_COMPONENTE
          FROM D_MOVIMIENTO
         WHERE COMPANIA = UN_COMPANIA
           AND TIPOMOVIMIENTO = UN_TIPO
           AND MOVIMIENTO = UN_MOVIMIENTO
           AND CODIGO = UN_CODIGO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_ELEMENTO := NULL;
        MI_SERIE := NULL;
        MI_COMPONENTE := NULL;
    END;

    BEGIN
        SELECT COUNT(1)
          INTO MI_EXISTE
          FROM DETALLE_ELEMENTOSCOMPONENTES
         WHERE COMPANIA = UN_COMPANIA
           AND CODIGO_COMPONENTE = MI_ELEMENTO
           AND SERIE_COMPONENTE = MI_SERIE;
    END;

    IF MI_COMPONENTE IS NOT NULL THEN

        BEGIN
            SELECT PAIS, DEPARTAMENTO, CIUDAD, DIRECCION
              INTO MI_PAIS, MI_DEPARTAMENTO, MI_CIUDAD, MI_UBICACION
              FROM ELEMENTOSCOMPONENTES
             WHERE COMPANIA = UN_COMPANIA
               AND CONSECUTIVO = MI_COMPONENTE;
        EXCEPTION WHEN NO_DATA_FOUND THEN
               MI_PAIS := NULL;
               MI_DEPARTAMENTO := NULL; 
               MI_CIUDAD := NULL; 
               MI_UBICACION := NULL;
        END;

        IF MI_EXISTE NOT IN (0) THEN -- EXISTE
            BEGIN
                BEGIN 
                    MI_TABLA := 'DETALLE_ELEMENTOSCOMPONENTES';
                    MI_CAMPOS  := ' CODIGO_ELEMENTO = ' || MI_COMPONENTE ;
                    MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || 
                                    ''' AND CODIGO_COMPONENTE = '''  ||  MI_ELEMENTO || 
                                    ''' AND SERIE_COMPONENTE = ' || MI_SERIE; 

                    MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA      => MI_TABLA,
                                                  UN_ACCION     => 'M',
                                                  UN_CAMPOS     => MI_CAMPOS,
                                                  UN_CONDICION  => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                       PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                 ,UN_ERROR_COD => PCK_ERRORES.ERR_ALM_MESESVIDAUTILCOMPONENT);
            END;
        ELSE 
            BEGIN
                BEGIN 
                    MI_TABLA := 'DETALLE_ELEMENTOSCOMPONENTES';
                    MI_CAMPOS  := ' COMPANIA, CODIGO_ELEMENTO, CODIGO_COMPONENTE, SERIE_COMPONENTE' ;
                    MI_VALORES := '''' || UN_COMPANIA || ''',' || MI_COMPONENTE || ',''' || MI_ELEMENTO || ''',' || MI_SERIE ;  

                    MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA      => MI_TABLA,
                                                  UN_ACCION     => 'I',
                                                  UN_CAMPOS     => MI_CAMPOS,
                                                  UN_VALORES    => MI_VALORES);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                           PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                     ,UN_ERROR_COD => PCK_ERRORES.ERR_ALM_MESESVIDAUTILCOMPONENT);
            END;
        END IF;

        BEGIN
              BEGIN 
                  MI_TABLA := 'D_MOVIMIENTO';
                  MI_CAMPOS  := ' PAIS = ''' || MI_PAIS || 
                              ''', DEPARTAMENTO = ''' || MI_DEPARTAMENTO || 
                              ''', CIUDAD = ''' || MI_CIUDAD || 
                              ''', DIRECCION = ''' || MI_UBICACION || '''';
                  MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || 
                                  ''' AND TIPOMOVIMIENTO = '''  ||  UN_TIPO || 
                                  ''' AND MOVIMIENTO = ' || UN_MOVIMIENTO || 
                                  ' AND CODIGO = ' || UN_CODIGO  ;

                  MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA      => MI_TABLA,
                                                UN_ACCION     => 'M',
                                                UN_CAMPOS     => MI_CAMPOS,
                                                UN_CONDICION  => MI_CONDICION);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

              END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                               ,UN_ERROR_COD => PCK_ERRORES.ERR_ALM_MESESVIDAUTILCOMPONENT);
          END;

           BEGIN
              BEGIN 
                  MI_TABLA := 'DEVOLUTIVO';
                  MI_CAMPOS  := ' PAIS = ''' || MI_PAIS || 
                              ''', DEPARTAMENTO = ''' || MI_DEPARTAMENTO || 
                              ''', CIUDAD = ''' || MI_CIUDAD || 
                              ''', DIRECCION = ''' || MI_UBICACION || '''';
                  MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || 
                                  ''' AND ELEMENTO = '''  ||  MI_ELEMENTO || 
                                  ''' AND SERIE = ' || MI_SERIE  ;

                  MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA      => MI_TABLA,
                                                UN_ACCION     => 'M',
                                                UN_CAMPOS     => MI_CAMPOS,
                                                UN_CONDICION  => MI_CONDICION);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;

              END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                               ,UN_ERROR_COD => PCK_ERRORES.ERR_ALM_MESESVIDAUTILCOMPONENT);
          END;

    END IF;
END PR_ACT_COMPONENTE;

-- 5
PROCEDURE PR_EVALUARCONSUMOCONTROLADO 
  /*
    NAME              : PR_EVALUARCONSUMOCONTROLADO
    AUTHORS           : STEFANINI SYSMAN SAS
    AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
    DATE MIGRADOR     : 17/10/2018
    TIME              : 03:10 PM
    SOURCE MODULE     : 
    MODIFIER          : AURA LILIANA MONROY GARCIA
    DATE MODIFIED     : 12/03/2019 
    TIME              : 10:05 AM   
    DESCRIPTION       : Valida si el valor de un elemento Devolutivo sobrepasa la cantidad de SMLVM definidos en el parámetro "SMLVM CONSUMO CONTROLADO"
    MODIFICATIONS     : Se adiciona la validación del concepto del movimiento que se está trabajando, para que no se tengan en cuenta los movimientos de Corrección de Valor
    PARAMETERS        : UN_COMPANIA         => Compania de ingreso a la aplicacion
                        UN_TIPO_MOVIMIENTO  => Tipo de Movimiento en el que se está trabajando
                        UN_MOVIMIENTO       => Número del Movimiento en el que se está trabajando  
                        UN_ELEMENTO         => Elemento que se va a registrar o modificar
                        UN_PLACA            => Placa del elemento que se va a registrar o modificar 
                        UN_VALOR_UNITARIO   => Valor Unitario del elemento que se va a registrar o modificar 
    @NAME:  evaluarConsumoControlado 
    @METHOD:  POST     
  */ 
(
  UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPO_MOVIMIENTO      IN PCK_SUBTIPOS.TI_NUMORDEN,
  UN_MOVIMIENTO           IN D_MOVIMIENTO.MOVIMIENTO%TYPE,
  UN_ELEMENTO             IN PCK_SUBTIPOS.TI_ELEMENTO,
  UN_PLACA                IN PCK_SUBTIPOS.TI_SERIE,
  UN_VALOR_UNITARIO       IN PCK_SUBTIPOS.TI_DOBLE
)
AS 
  MI_CLASE_MOVIMIENTO     PCK_SUBTIPOS.TI_TEXTO1;
  MI_TIPO_ELEMENTO_MOV    PCK_SUBTIPOS.TI_TEXTO1;
  MI_PAR_CONS_CONTROLADO  PCK_SUBTIPOS.TI_PARAMETRO;
  MI_PAR_SMLVM_CONTROL    PCK_SUBTIPOS.TI_PARAMETRO;
  MI_ANO_MOV              PCK_SUBTIPOS.TI_ANIO;
  MI_FECHA_MOV            DATE;
  MI_SALARIOMINIMO        PCK_SUBTIPOS.TI_DOBLE;
  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CONCEPTO_MOV         CONCEPTO.CODIGO%TYPE;

BEGIN
  BEGIN
    SELECT 
        CLASE
      , TIPOELEMENTO
      , CONCEPTO
    INTO 
        MI_CLASE_MOVIMIENTO
      , MI_TIPO_ELEMENTO_MOV
      , MI_CONCEPTO_MOV
     FROM TIPOMOVIMIENTO
    WHERE COMPANIA = UN_COMPANIA
      AND CODIGO   = UN_TIPO_MOVIMIENTO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CLASE_MOVIMIENTO  := ' ';
        MI_TIPO_ELEMENTO_MOV := ' ';
        MI_CONCEPTO_MOV      := ' ';
  END;      

  IF ( MI_CLASE_MOVIMIENTO = 'E' AND MI_TIPO_ELEMENTO_MOV = 'D' AND MI_CONCEPTO_MOV NOT IN ('CR') ) THEN 
      SELECT EXTRACT(YEAR FROM FECHA) ANIO, FECHA
        INTO MI_ANO_MOV, MI_FECHA_MOV
        FROM MOVIMIENTO
       WHERE COMPANIA       = UN_COMPANIA
         AND TIPOMOVIMIENTO = UN_TIPO_MOVIMIENTO
         AND NUMERO         = UN_MOVIMIENTO;  

      MI_PAR_CONS_CONTROLADO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                         ,UN_NOMBRE    =>'CONTROLAR CONSUMO CONTROLADO'
                                                         ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                                         ,UN_FECHA_PAR => MI_FECHA_MOV),'NO');

      IF MI_PAR_CONS_CONTROLADO = 'SI' THEN                                                   
        MI_PAR_SMLVM_CONTROL := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                           ,UN_NOMBRE    =>'SMLVM CONSUMO CONTROLADO'
                                                           ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                                           ,UN_FECHA_PAR => MI_FECHA_MOV),'0');   

        SELECT SALARIOMINIMO
        INTO MI_SALARIOMINIMO
        FROM ANO
        WHERE COMPANIA = UN_COMPANIA
          AND NUMERO   = MI_ANO_MOV;

        IF UN_VALOR_UNITARIO <= (MI_PAR_SMLVM_CONTROL * MI_SALARIOMINIMO) THEN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
              MI_MSGERROR (1).CLAVE := 'ELEMENTO';
              MI_MSGERROR (1).VALOR := UN_ELEMENTO;
              MI_MSGERROR (2).CLAVE := 'PLACA';
              MI_MSGERROR (2).VALOR := UN_PLACA;
              MI_MSGERROR (3).CLAVE := 'SALARIOS';
              MI_MSGERROR (3).VALOR := MI_PAR_SMLVM_CONTROL;
              PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_DEVOLUTIVOACONTROLADO
                                         ,UN_REEMPLAZOS => MI_MSGERROR);
          END;
        END IF;
      END IF;
   END IF;
END PR_EVALUARCONSUMOCONTROLADO;

-- 6
PROCEDURE PR_CLASIFICACIONMATERIAL 
  /*
    NAME              : PR_CLASIFICACIONMATERIAL
    AUTHORS           : STEFANINI SYSMAN SAS
    AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
    DATE MIGRADOR     : 08/11/2018
    TIME              : 12:19 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :    
    DESCRIPTION       : Evalúa el valor de un elemento y lo compara con n cantidad de SMMLV, con el fin 
                        de definir la materialidad del elemento; 
                        n se define en el parámetro: SMMLV MATERIALIDAD POLITICA CONTABLE
    PARAMETERS        : UN_COMPANIA         => Compania de ingreso a la aplicacion
                        UN_USUARIO          => Usuario que está ejecutando el proceso
    @NAME:  clasificacionMaterialComponentes 
    @METHOD:  PUT     
  */ 
(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
)
AS 
  MI_PAR_MATERIALIDAD   PCK_SUBTIPOS.TI_PARAMETRO;
  MI_SMMLV_ACTUAL       PCK_SUBTIPOS.TI_DOBLE;
  MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_MERGEUSING         PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE        PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE        PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_RTA_ACME           PCK_SUBTIPOS.TI_RTA_ACME;  
  MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

  MI_PAR_MATERIALIDAD := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                  ,UN_NOMBRE    =>'SMMLV MATERIALIDAD POLITICA CONTABLE'
                                                  ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                                  ,UN_FECHA_PAR => SYSDATE),-1);

  IF MI_PAR_MATERIALIDAD IN (-1,0) THEN 
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_PAR_MATERIALIDAD); 
    END;  
  END IF;

  SELECT SALARIOMINIMO
   INTO MI_SMMLV_ACTUAL
   FROM ANO
  WHERE COMPANIA = UN_COMPANIA
    AND NUMERO   = EXTRACT(YEAR FROM SYSDATE);

  IF MI_SMMLV_ACTUAL = 0 THEN 
    BEGIN
      RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
        MI_MSGERROR (1).CLAVE := 'ANIO';
        MI_MSGERROR (1).VALOR := EXTRACT(YEAR FROM SYSDATE);
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_SMMLV_NOCONFIGURADO
                                   ,UN_REEMPLAZOS => MI_MSGERROR); 
    END;  
  END IF;

  -- Actualizo a cero la materialidad en todos los componentes
  BEGIN
    MI_TABLA     := 'DETALLE_ELEMENTOSCOMPONENTES';
    MI_CAMPOS    := 'MATERIAL      = 0
                    ,MODIFIED_BY   = ''' || UN_USUARIO || '''   
                    ,DATE_MODIFIED = SYSDATE ';
    MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || ''' ';
    BEGIN   
      MI_RTA_ACME := PCK_DATOS.FC_ACME(
        UN_TABLA     => MI_TABLA,
        UN_ACCION    => 'M',
        UN_CAMPOS    => MI_CAMPOS,
        UN_CONDICION => MI_CONDICION      
      );
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN ;
    END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_TABLAERROR => MI_TABLA,
          UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_IND_MATERIALIDAD_COMP); 

  END;

  -- Se actualiza el indicador MATERIAL cuando el valor del elemento cumple las condiciones 
  BEGIN
    MI_TABLA     := 'DETALLE_ELEMENTOSCOMPONENTES';
    MI_MERGEUSING := 'SELECT
                        D.COMPANIA,
                        D.ELEMENTO,
                        D.SERIE, 
                        D.VALOR
                      FROM
                        DEVOLUTIVO D';

    MI_MERGEENLACE := '    TABLA.COMPANIA          = VISTA.COMPANIA
                       AND TABLA.CODIGO_COMPONENTE = VISTA.ELEMENTO
                       AND TABLA.SERIE_COMPONENTE  = VISTA.SERIE';

    MI_MERGEEXISTE := 'UPDATE SET MATERIAL = CASE WHEN VISTA.VALOR > (' || MI_SMMLV_ACTUAL ||' * '|| MI_PAR_MATERIALIDAD ||')
                                                  THEN -1
                                                  ELSE 0
                                            END 
                                  ,MODIFIED_BY   = ''' || UN_USUARIO || '''   
                                  ,DATE_MODIFIED = SYSDATE
                       WHERE COMPANIA = ''' || UN_COMPANIA || ''' ';  
    BEGIN
              MI_RTA_ACME := PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA
                                                ,UN_ACCION      => 'MM'
                                                ,UN_MERGEUSING  => MI_MERGEUSING
                                                ,UN_MERGEENLACE => MI_MERGEENLACE
                                                ,UN_MERGEEXISTE => MI_MERGEEXISTE);


              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD     => SQLCODE
                ,UN_TABLAERROR => MI_TABLA 
                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_IND_MATERIALIDAD_COMP);

  END;

END PR_CLASIFICACIONMATERIAL;

PROCEDURE PR_ACT_CANTIDADAFECTADA
  /*
    NAME              : PR_ACT_CANTIDADAFECTADA
    AUTHORS           : STEFANINI SYSMAN SAS
    AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA 
    DATE MIGRADOR     : 04/01/2019
    TIME              : 03:10 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :    
    DESCRIPTION       : Actualiza la cantidad afectada del detalle de movimiento asociado  
    PARAMETERS        : UN_COMPANIA         => Compania de ingreso a la aplicacion
                        UN_TIPOMOV          => Tipo de Movimiento en el que se está trabajando
                        UN_NUMERO           => Número del Movimiento en el que se está trabajando  
                        UN_CONSECUTIVO      => Identificador del detalle de movimiento con el que se está trabajando
                        UN_CANTIDAD         => Cantidad que se está eliminando del movimiento asociado 
                        UN_ELEMENTO         => Elemento que se va a afectar 
                        UN_SERIE            => Serie que se tendrá en cuenta cuando se está trabajamdo con Devolutivos
    @NAME:   actualizarCntAfectada
    @METHOD: PUT      
  */ 
(
      UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_TIPOMOV               IN MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
      UN_NUMERO                IN MOVIMIENTO.NUMERO%TYPE,
      UN_CANTIDAD              IN PCK_SUBTIPOS.TI_DOBLE,
      UN_ELEMENTO              IN PCK_SUBTIPOS.TI_ELEMENTO,
      UN_SERIE                 IN PCK_SUBTIPOS.TI_SERIE,
      UN_CONSECUTIVO_AFECT     IN VARCHAR2,
	  UN_CANTANTERIOR          IN PCK_SUBTIPOS.TI_DOBLE,
      UN_ACCION                IN VARCHAR2
)
AS 

      MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
      MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
      MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
      MI_RTA            PCK_SUBTIPOS.TI_RTA_ACME;
      MI_TIPOMOVASOC    MOVIMIENTO.TIPOMOVIMIENTO%TYPE;
      MI_NROMOVASOC     MOVIMIENTO.NUMERO%TYPE;
      MI_TIPOELEMENTO   TIPOMOVIMIENTO.TIPOELEMENTO%TYPE;

BEGIN
      BEGIN 
        SELECT 
              MOVIMIENTO.TIPOMOVASOCIADO
             ,MOVIMIENTO.MOVASOCIADO 
             ,TIPOMOVIMIENTO.TIPOELEMENTO
         INTO 
              MI_TIPOMOVASOC
             ,MI_NROMOVASOC 
             ,MI_TIPOELEMENTO
         FROM MOVIMIENTO
        INNER JOIN TIPOMOVIMIENTO
           ON MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
          AND MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO 
        WHERE MOVIMIENTO.COMPANIA       = UN_COMPANIA
          AND MOVIMIENTO.TIPOMOVIMIENTO = UN_TIPOMOV
          AND MOVIMIENTO.NUMERO         = UN_NUMERO;
        EXCEPTION WHEN NO_DATA_FOUND THEN
              MI_TIPOMOVASOC  := '0';
              MI_NROMOVASOC   := 0;
              MI_TIPOELEMENTO := '';
      END;

      IF MI_NROMOVASOC <> 0 THEN
            BEGIN
                  BEGIN 
                        MI_TABLA     := 'D_MOVIMIENTO';         
                        MI_CAMPOS    := ' CANTIDADAFECTADA  = CASE WHEN '''||UN_ACCION||'''=''INSERT'' 
                                                                 THEN CANTIDADAFECTADA + '||UN_CANTIDAD||'
                                                                 ELSE CASE WHEN '''||UN_ACCION||'''=''UPDATE''
                                                                         THEN CANTIDADAFECTADA + ('||UN_CANTIDAD||'-'||UN_CANTANTERIOR||')
                                                                         ELSE CASE WHEN '''||UN_ACCION||'''=''DELETE''
                                                                                 THEN CANTIDADAFECTADA -'||UN_CANTIDAD||'
                                                                                 ELSE 0
                                                                              END
                                                                      END
                                                              END';
                        MI_CONDICION := ' COMPANIA          = ''' || UN_COMPANIA    || ''' 
                                      AND TIPOMOVIMIENTO    = ''' || MI_TIPOMOVASOC || ''' 
                                      AND MOVIMIENTO        =   ' || MI_NROMOVASOC  || '
                                      AND ELEMENTO          = ''' || UN_ELEMENTO    || '''
                                      AND CODIGO      = ' || CASE WHEN UN_CONSECUTIVO_AFECT IS NULL
                                                              THEN  'CODIGO_AFECT' 
                                                              ELSE UN_CONSECUTIVO_AFECT
                                                              END || 
                                      CASE WHEN MI_TIPOELEMENTO NOT IN ('C')
                                           THEN ' AND SERIE   = ' || UN_SERIE    || ' '
                                           ELSE ''
                                      END;--AMONROY (30/01/2019) Se modifica la definicion de la variable MI_CONDICION teniendo en cuenta el elemento que maneja el movimiento

                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA,
                                                               UN_ACCION    =>  'M',
                                                               UN_CAMPOS    =>  MI_CAMPOS, 
                                                               UN_CONDICION =>  MI_CONDICION);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                  END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN    
                                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_ACT_SALDOCANTIDAD,
                                    UN_TABLAERROR => MI_TABLA
                                  );
            END;
      END IF;   

END PR_ACT_CANTIDADAFECTADA;


--8
PROCEDURE PR_COPIARDE_MOVIMIENTO
/*
    NAME              : PR_COPIARDE_MOVIMIENTO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 20/03/2019
    TIME              : 08:00 AM  
    SOURCE MODULE     : SysmanAl2018.09.05
    MODIFIER          : 

    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Procedimiento que copia los detalles de un movimiento a otro

      @NAME:    copiarDeMovimiento 
      @METHOD:  POST
  */

(
    UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOMOV          IN  D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
    UN_NUMERO           IN  D_MOVIMIENTO.MOVIMIENTO%TYPE,
    UN_TIPOMOVCOP       IN  D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
    UN_NUMEROCOP        IN  D_MOVIMIENTO.MOVIMIENTO%TYPE,
    UN_FECHA            IN  DATE,
    UN_TERCERO          IN  D_MOVIMIENTO.TERCERO%TYPE,
    UN_SUCURSAL         IN  D_MOVIMIENTO.SUCURSAL%TYPE,
    UN_AUXILIAR         IN  D_MOVIMIENTO.AUXILIAR%TYPE,
    UN_USUARIO          IN  PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_CANTIDAD           PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
BEGIN

    SELECT COUNT(1)
    INTO MI_CANTIDAD
    FROM D_MOVIMIENTO 
    WHERE COMPANIA = UN_COMPANIA
      AND TIPOMOVIMIENTO  = UN_TIPOMOV
      AND MOVIMIENTO      =  UN_NUMERO
    ORDER BY COMPANIA, TIPOMOVIMIENTO, MOVIMIENTO;

    IF MI_CANTIDAD > 0 THEN 
      BEGIN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 
          END;
          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ALMACEN THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD =>SQLCODE,
                            UN_ERROR_COD=>PCK_ERRORES.ERR_ALM_DETMOVIMIENTO
                          );
      END;
    END IF;

    SELECT COUNT(1)
    INTO MI_CANTIDAD
    FROM D_MOVIMIENTO 
    WHERE COMPANIA = UN_COMPANIA  
       AND TIPOMOVIMIENTO = UN_TIPOMOVCOP
       AND MOVIMIENTO     =  UN_NUMEROCOP; 

    IF MI_CANTIDAD = 0 THEN 
      BEGIN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 
          END;
          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ALMACEN THEN    
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD =>SQLCODE,
                            UN_ERROR_COD=>PCK_ERRORES.ERR_ALM_DETMOVIMIENTOCOP 
                          );
      END;
    END IF;

     MI_CAMPOS :='COMPANIA,
                  TIPOMOVIMIENTO,
                  MOVIMIENTO,
                  CODIGO,
                  ELEMENTO,
                  SERIE,
                  ORDENDESUMINISTRO,
                  ESPECIFICACION,
                  CENTRODECOSTO,
                  CANTIDAD,
                  SALDOCANT,
                  VALORUNITARIO,
                  PORCDESCUENTO,
                  PORCIVA,
                  VALORTOTAL,
                  FECHAADEPRECIAR,
                  CUENTADEBITO,
                  CUENTACREDITO,
                  SALDOELEMENTO,
                  VALORSALDO,
                  VLRUNITARIOPROM,
                  FECHA,
                  HORA,
                  IND_REG,
                  VALORANTERIOR,
                  CANTANTERIOR,
                  VLRUNITANTERIOR,
                  VLRAJUSTADO,
                  SALDOKARDEX,
                  INCREMENAJUS,
                  PEDIDO,
                  ITEM,
                  FECHAENTRADA,
                  IDENTIFICADOR,
                  MARCA,
                  SERIEDEVOLUTIVO,
                  ESTADO,
                  AJUSTECREDITO,
                  AJUSTEDEBITO,
                  COSTOSALIDA,
                  COSTOSALIDAAJ,
                  MESESBODEGA,
                  AJUSTECENTAVOS,
                  AJUSTECENTAVOS1,
                  CANTIDADDOCAS,
                  VALORDOCAS,
                  TERCERO,
                  SUCURSAL,
                  AUXILIAR,
                  PLACAANTERIOR,
                  REGISTRADOBI,
                  VALORBASE,
                  VALORIVA,
                  PORCIVAUNI,
                  PROVEEDORCA,
                  SUCURSALCA,
                  FECHAMOVIMIENTO,
                  VALORTOTALCONIVA,
                  FECHA_MODIFICA_PROMEDIO,
                  CANTIDADAFECTADA,
                  ESCRITURA,
                  NUMEROCATASTRAL,
                  MATINMOBILIARIA,
                  COD_CHIP,
                  AREA,
                  MODIFICA_PROMEDIO,
                  SALDO_PEPS,
                  PREPARACION,
                  ENTREGA,
                  INSTALACION,
                  COMPROBACION,
                  REVELACIONES,
                  SALVAMENTO,
                  DETERIORO,
                  NIIF_TIPO_ACTIVO,
                  NIIF_VALOR_TOTAL,
                  DATE_CREATED,
                  CREATED_BY
                  ';

     MI_VALORES := 'SELECT '''||UN_COMPANIA||''' COMPANIA,
                          ''' ||UN_TIPOMOV||'''  TIPOMOVIMIENTO,
                          '   ||UN_NUMERO||' MOVIMIENTO,
                          D.CODIGO,
                          D.ELEMENTO,
                          D.SERIE,
                          D.ORDENDESUMINISTRO,
                          D.ESPECIFICACION,
                          D.CENTRODECOSTO,
                          D.CANTIDAD,
                          D.SALDOCANT,
                          D.VALORUNITARIO,
                          D.PORCDESCUENTO,
                          D.PORCIVA,
                          D.VALORTOTAL,
                          D.FECHAADEPRECIAR,
                          D.CUENTADEBITO,
                          D.CUENTACREDITO,
                          D.SALDOELEMENTO,
                          D.VALORSALDO,
                          D.VLRUNITARIOPROM,
                          TO_DATE('''||UN_FECHA||''', ''DD/MM/YYYY HH24:MI;SS'')  FECHA,
                          TO_DATE (''30/12/1899 ''|| TO_CHAR(SYSDATE, ''HH24:MI;SS''), ''DD/MM/YYYY HH24:MI;SS'') HORA,
                          0               IND_REG,
                          D.VALORANTERIOR,
                          D.CANTANTERIOR,
                          D.VLRUNITANTERIOR,
                          D.VLRAJUSTADO,
                          D.SALDOKARDEX,
                          D.INCREMENAJUS,
                          D.PEDIDO,
                          D.ITEM,
                          D.FECHAENTRADA,
                          D.IDENTIFICADOR,
                          D.MARCA,
                          D.SERIEDEVOLUTIVO,
                          D.ESTADO,
                          D.AJUSTECREDITO,
                          D.AJUSTEDEBITO,
                          D.COSTOSALIDA,
                          D.COSTOSALIDAAJ,
                          D.MESESBODEGA,
                          D.AJUSTECENTAVOS,
                          D.AJUSTECENTAVOS1,
                          D.CANTIDADDOCAS,
                          D.VALORDOCAS,
                          '''|| UN_TERCERO  ||'''  TERCERO,
                          '''|| UN_SUCURSAL ||'''  SUCURSAL,
                          '''|| UN_AUXILIAR ||'''  AUXILIAR,
                          D.PLACAANTERIOR,
                          D.REGISTRADOBI,
                          D.VALORBASE,
                          D.VALORIVA,
                          D.PORCIVAUNI,
                          D.PROVEEDORCA,
                          D.SUCURSALCA,
                          D.FECHAMOVIMIENTO,
                          D.VALORTOTALCONIVA,
                          D.FECHA_MODIFICA_PROMEDIO,
                          D.CANTIDADAFECTADA,
                          D.ESCRITURA,
                          D.NUMEROCATASTRAL,
                          D.MATINMOBILIARIA,
                          D.COD_CHIP,
                          D.AREA,
                          D.MODIFICA_PROMEDIO,
                          D.SALDO_PEPS,
                          D.PREPARACION,
                          D.ENTREGA,
                          D.INSTALACION,
                          D.COMPROBACION,
                          D.REVELACIONES,
                          D.SALVAMENTO,
                          D.DETERIORO,
                          D.NIIF_TIPO_ACTIVO,
                          D.NIIF_VALOR_TOTAL,
                          SYSDATE,
                          '''||UN_USUARIO||'''
                        FROM D_MOVIMIENTO D
                        WHERE D.COMPANIA      =  '''||UN_COMPANIA||'''
                        AND D.TIPOMOVIMIENTO  =  '''||UN_TIPOMOVCOP||'''
                        AND D.MOVIMIENTO      =  '||UN_NUMEROCOP||'';

     BEGIN 
        BEGIN 
            PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(	UN_TABLA    => 'D_MOVIMIENTO',
                                            UN_ACCION   => 'IS',
                                            UN_CAMPOS   => MI_CAMPOS,
                                            UN_VALORES  => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;                                         
        END ;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN    
                              PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD   => SQLCODE,
                              UN_ERROR_COD => PCK_ERRORES.ERR_ALM_INSDMOVIMIENTOCOP
                            );
    END;


    FOR RS IN (SELECT TIPOMOVIMIENTO,
                      MOVIMIENTO,
                      CODIGO,
                      ELEMENTO
                FROM D_MOVIMIENTO 
                WHERE COMPANIA = UN_COMPANIA  
                   AND TIPOMOVIMIENTO = UN_TIPOMOV
                   AND MOVIMIENTO     =  UN_NUMERO)
    LOOP
        MI_CAMPOS := 'HORA = (HORA + INTERVAL ''1'' SECOND),
                      DATE_MODIFIED = SYSDATE,
                      MODIFIED_BY = '''||UN_USUARIO||'''';
        MI_CONDICION := '     COMPANIA        =  '''|| UN_COMPANIA||'''
                         AND  TIPOMOVIMIENTO  =  '''|| RS.TIPOMOVIMIENTO||'''
                         AND  MOVIMIENTO      =  '''|| RS.MOVIMIENTO||'''
                         AND  CODIGO          =  '''|| RS.CODIGO||'''
                         AND  ELEMENTO        =  '''|| RS.ELEMENTO||'''
                         AND  IND_REG = 0';


        BEGIN 
          BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'D_MOVIMIENTO', 
                                                   UN_ACCION    =>  'M', 
                                                   UN_CAMPOS    =>  MI_CAMPOS,
                                                   UN_CONDICION =>  MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERR_PREDIAL_ACT_FACT_ACU
                      );
        END; 

    END LOOP;
END PR_COPIARDE_MOVIMIENTO;


PROCEDURE PR_INSERT_INVENTARIO  

/*
    NAME              : PR_INSERT_INVENTARIO
    AUTHORS           : BRAYAN SEBASTIAN CARDENAS AGUILAR
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 14/05/2019
    TIME              : 03:00 AM  
    SOURCE MODULE     : 
    MODIFIER          : 

    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Procedimiento que inserta el inventario fisico

      @NAME:    insertInventarioFisico 
      @METHOD:  POST
  */
   (
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHA_LECTURA       IN INVENTARIO_FISICO.FECHA_LECTURA%TYPE,
    UN_DEPENDENCIA         IN INVENTARIO_FISICO.DEPENDENCIA%TYPE,
    UN_RESPONSABLE         IN INVENTARIO_FISICO.RESPONSABLE%TYPE,
    UN_SUCURSAL            IN INVENTARIO_FISICO.SUCURSAL%TYPE,
    UN_REFERENCIA          IN INVENTARIO_FISICO.CODIGOBARRAS%TYPE,
    UN_USUARIO             IN PCK_SUBTIPOS.TI_USUARIO
   )
   AS

    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;   
    MI_PLACA            INVENTARIO_FISICO.PLACA%TYPE;
    MI_ELEMENTO         INVENTARIO_FISICO.CODIGOELEMENTO%TYPE;

BEGIN
  BEGIN

      MI_PLACA := SUBSTR(UN_REFERENCIA,0,6);
    BEGIN  
        BEGIN
            SELECT DISTINCT 
                   ELEMENTO
                INTO MI_ELEMENTO
              FROM D_MOVIMIENTO
              WHERE COMPANIA = UN_COMPANIA
              AND SERIE      = MI_PLACA;
          EXCEPTION WHEN NO_DATA_FOUND THEN

           RAISE PCK_EXCEPCIONES.EXC_ALMACEN;                      
        END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN    
                MI_MSGERROR(1).CLAVE := 'PLACA';
                MI_MSGERROR(1).VALOR := MI_PLACA;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_ALM_NO_EXISTE_PLACA,
                UN_REEMPLAZOS  => MI_MSGERROR 
              );
    END;


        MI_CAMPOS  := 'COMPANIA,
                       PLACA,
                       CODIGOELEMENTO,
                       FECHA_LECTURA,
                       DEPENDENCIA,
                       RESPONSABLE,
                       SUCURSAL,
                       CODIGOBARRAS,
                       CREATED_BY,
                       DATE_CREATED';


        MI_VALORES := ' '''|| UN_COMPANIA     ||''',
                        '''|| MI_PLACA        ||''',
                        '''|| MI_ELEMENTO     ||''',
                        '''|| UN_FECHA_LECTURA ||''',
                        '''|| UN_DEPENDENCIA  ||''',
                        '''|| UN_RESPONSABLE  ||''',
                        '''|| UN_SUCURSAL     ||''',
                        '''|| UN_REFERENCIA   ||''',
                        '''|| UN_USUARIO      ||''',
                        '''|| SYSDATE         ||''' ';


        BEGIN

                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'INVENTARIO_FISICO'
                                                     ,UN_ACCION  => 'I'
                                                     ,UN_CAMPOS  => MI_CAMPOS
                                                     ,UN_VALORES => MI_VALORES
                                                      );

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                  END;


                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                MI_MSGERROR(1).CLAVE := 'ELEMENTO';
                MI_MSGERROR(1).VALOR := MI_ELEMENTO;
                MI_MSGERROR(2).CLAVE := 'PLACA';
                MI_MSGERROR(2).VALOR := MI_PLACA;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ERR_ALM_INSINVENTARIOFISICO
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );

    END;
END PR_INSERT_INVENTARIO;


PROCEDURE PR_ELIMINARLOTE_DMOVIMIENTO 
/*
    NAME              : PR_ELIMINARLOTE_DMOVIMIENTO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Omar Leonardo Barragan Peña
    DATE MIGRADOR     : 02/08/2019
    TIME              : 04:00 PM  
    SOURCE MODULE     : 
    MODIFIER          : 

    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Procedimiento que elimina en lote los registros y actualiza el saldo cantidad de los detalles de la orden de compra

      @NAME:    eliminarLoteAlmacen
      @METHOD:  DELETE
*/

   (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOMOVIMIENTO  IN D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
    UN_MOVIMIENTO      IN D_MOVIMIENTO.MOVIMIENTO%TYPE  
   )
   AS

  MI_RTA  VARCHAR2(32000);
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION; 

BEGIN

    FOR RS IN(SELECT 
             D_MOVIMIENTO.MOVIMIENTO,   
             D_MOVIMIENTO.TIPOMOVIMIENTO,
             D_MOVIMIENTO.CODIGO,
             D_MOVIMIENTO.ELEMENTO,
             D_MOVIMIENTO.CANTIDAD,
             TIPOMOVIMIENTO.CLASE,
             MOVIMIENTO.TIPOMOVASOCIADO,
             MOVIMIENTO.MOVASOCIADO,
             D_MOVIMIENTO.SERIE,
             D_MOVIMIENTO.CODIGO_AFECT,
             MOVIMIENTO.BODEGA_ORIGEN
            FROM D_MOVIMIENTO
                INNER JOIN MOVIMIENTO
                 ON D_MOVIMIENTO.COMPANIA       = MOVIMIENTO.COMPANIA
                AND D_MOVIMIENTO.TIPOMOVIMIENTO = MOVIMIENTO.TIPOMOVIMIENTO
                AND D_MOVIMIENTO.MOVIMIENTO     = MOVIMIENTO.NUMERO
                INNER JOIN TIPOMOVIMIENTO
                 ON MOVIMIENTO.COMPANIA       = TIPOMOVIMIENTO.COMPANIA
                AND MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
               WHERE   D_MOVIMIENTO.COMPANIA    = UN_COMPANIA
                AND D_MOVIMIENTO.TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
                AND D_MOVIMIENTO.MOVIMIENTO     = UN_MOVIMIENTO
            ORDER BY D_MOVIMIENTO.FECHA DESC, D_MOVIMIENTO.HORA DESC
                   )
    LOOP  
        DELETE 
        FROM     D_MOVIMIENTO 
            WHERE     COMPANIA = UN_COMPANIA     
            AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO     
            AND MOVIMIENTO = UN_MOVIMIENTO
            AND CODIGO = RS.CODIGO;

           IF RS.CLASE IN ('E') THEN 

          MI_RTA := PCK_ALMACEN_COM2.FC_REVERSADOCUMENTOAS(UN_COMPANIA         => UN_COMPANIA, 
                                                          UN_TIPOMOVASOCIADO  => RS.TIPOMOVASOCIADO,
                                                          UN_MOVASOCIADO      => RS.MOVASOCIADO,
                                                          UN_CODIGOELEMENTO   => RS.ELEMENTO,
                                                          UN_CANTIDAD         => RS.CANTIDAD);

           ELSIF RS.CLASE IN ('S') THEN
                PR_ACT_CANTIDADAFECTADA(UN_COMPANIA          => UN_COMPANIA, 
                                        UN_TIPOMOV           => RS.TIPOMOVIMIENTO, 
                                        UN_NUMERO            => RS.MOVIMIENTO, 
                                        UN_CANTIDAD          => RS.CANTIDAD, 
                                        UN_ELEMENTO          => RS.ELEMENTO, 
                                        UN_SERIE             => RS.SERIE, 
                                        UN_CONSECUTIVO_AFECT => RS.CODIGO_AFECT, 
                                        UN_CANTANTERIOR      => 0, 
                                        UN_ACCION            => 'DELETE');
           END IF;
           
           IF RS.BODEGA_ORIGEN IN ('30', '20') THEN
                BEGIN
                    MI_CAMPOS := 'ESTADO = ''B''';
                
                    MI_CONDICION :=
                           'COMPANIA = ''' || UN_COMPANIA || ''''
                        || ' AND ELEMENTO = ''' || RS.ELEMENTO || ''''
                        || ' AND SERIE = ' || RS.SERIE;
                
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                            UN_TABLA      => 'DEVOLUTIVO',
                                            UN_ACCION     => 'M',
                                            UN_CAMPOS     => MI_CAMPOS,
                                            UN_CONDICION  => MI_CONDICION
                                        );
                
                EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                END;
            END IF;

    END LOOP;


END PR_ELIMINARLOTE_DMOVIMIENTO;

PROCEDURE PR_REGISTRA_MOVIMIENTO 

(
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOMOVIMIENTO   IN VARCHAR2,
    UN_MOVIMIENTO       IN PCK_SUBTIPOS.TI_ENTERO_LARGO
)
AS 
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
    
 BEGIN   

 BEGIN
      BEGIN
        MI_CAMPOS        := 'IND_REG = -1';
        
        MI_CONDICION     :='D_MOVIMIENTO.COMPANIA           = '''||UN_COMPANIA ||'''
          AND D_MOVIMIENTO.TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||'''
          AND D_MOVIMIENTO.MOVIMIENTO     = '||UN_MOVIMIENTO||'';
          
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'D_MOVIMIENTO' ,
                                               UN_ACCION => 'M',
                                               UN_CAMPOS => MI_CAMPOS ,
                                               UN_CONDICION => MI_CONDICION);
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
      END;
    EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE, 
                                 UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_UPDATE_DMOV );
    END;
    
    
END PR_REGISTRA_MOVIMIENTO;

PROCEDURE PR_COPIARPLACASLOTE
  /*
    NAME              : PR_COPIARPLACASLOTE
    AUTHORS           : STEFANINI SYSMAN SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 16/01/2020
    TIME              : 12:10 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :    
    DESCRIPTION       : Inserta en la tabla D_MOVIMIENTO todas las placas provenientes de un tercero a otro
    PARAMETERS        : 

    @NAME:   copiarPlacasLote
    @METHOD: PUT      
  */ 
(
   UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,
   UN_TIPOMOVIMIENTO        IN D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
   UN_MOVIMIENTO            IN D_MOVIMIENTO.MOVIMIENTO%TYPE,
   UN_DEPENDENCIA           IN DEVOLUTIVO.DEPENDENCIA%TYPE,
   UN_RESPONSABLE           IN DEVOLUTIVO.RESPONSABLE%TYPE,
   UN_SUCURSAL              IN DEVOLUTIVO.SUCURSAL_RESPONSABLE%TYPE,
   UN_CLASE                 IN TIPOMOVIMIENTO.CLASE%TYPE,
   UN_FECHA                 IN D_MOVIMIENTO.FECHA%TYPE,
   UN_USUARIO               IN PCK_SUBTIPOS.TI_USUARIO       
)
AS 

   MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
   MI_VALORES         PCK_SUBTIPOS.TI_VALORES;   
   MI_STRSQL          PCK_SUBTIPOS.TI_STRSQL;   
   MI_RSPLACAS        SYS_REFCURSOR;
   MI_CONSECUTIVO     PCK_SUBTIPOS.TI_ENTERO_LARGO;
   MI_ELEMENTO        DEVOLUTIVO.ELEMENTO%TYPE;
   MI_SERIE           DEVOLUTIVO.SERIE%TYPE;
   MI_VALOR           DEVOLUTIVO.VALOR%TYPE;   
   MI_FECHA           D_MOVIMIENTO.FECHA%TYPE;
   MI_HORA            D_MOVIMIENTO.HORA%TYPE;
   MI_CODIGO          D_MOVIMIENTO.CODIGO%TYPE;
   MI_NOMBRELARGO     INVENTARIO.NOMBRELARGO%TYPE;
   MI_MARCA           DEVOLUTIVO.MARCA%TYPE; 
   MI_MODELO          DEVOLUTIVO.MODELO%TYPE;  
   MI_SERIEDEVOLUTIVO DEVOLUTIVO.SERIEDEVOLUTIVO%TYPE;  
   MI_ESTADO          D_MOVIMIENTO.ESTADO%TYPE;
   MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR; 
   MI_ESPECIFICACION  D_MOVIMIENTO.ESPECIFICACION%TYPE;
   MI_CONCEPTO        TIPOMOVIMIENTO.CONCEPTO%TYPE;

BEGIN      

    IF UN_CLASE IN ('D') THEN

        MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    =>  'D_MOVIMIENTO',
                                         UN_CRITERIO => ' COMPANIA       = '''||UN_COMPANIA||''' 
                                                      AND TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||''' 
                                                      AND MOVIMIENTO     = '||UN_MOVIMIENTO||'',
                                         UN_CAMPO    => 'CODIGO');

        MI_STRSQL := 'SELECT 
                        DEVOLUTIVO.ELEMENTO,
                        DEVOLUTIVO.SERIE,
                        DEVOLUTIVO.VALOR,                     
                        TO_DATE('''||TO_CHAR(UN_FECHA,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS''),
                        TO_DATE(''30/12/1899 '' || TO_CHAR(CURRENT_DATE,''HH24:MI:SS''),''DD/MM/YYYY HH24:MI:SS'') AS HORA,
                        '||MI_CONSECUTIVO||' + ROWNUM,
                        INVENTARIO.NOMBRELARGO,
                        DEVOLUTIVO.MARCA,
                        DEVOLUTIVO.MODELO,
                        DEVOLUTIVO.SERIEDEVOLUTIVO,
						DEVOLUTIVO.DESCRIPCION
                    FROM DEVOLUTIVO INNER JOIN INVENTARIO 
                        ON DEVOLUTIVO.COMPANIA = INVENTARIO.COMPANIA
                       AND DEVOLUTIVO.ELEMENTO = INVENTARIO.CODIGOELEMENTO
                    WHERE DEVOLUTIVO.COMPANIA             ='''||UN_COMPANIA||'''
                      AND DEVOLUTIVO.DEPENDENCIA          ='''||UN_DEPENDENCIA||'''
                      AND DEVOLUTIVO.RESPONSABLE          ='''||UN_RESPONSABLE||'''
                      AND DEVOLUTIVO.SUCURSAL_RESPONSABLE ='''||UN_SUCURSAL||'''
                      AND DEVOLUTIVO.PLACAANULADA         IN (0)';

       <<PLACAS>>
       OPEN MI_RSPLACAS FOR MI_STRSQL;
       LOOP 
       FETCH MI_RSPLACAS INTO  MI_ELEMENTO, MI_SERIE, MI_VALOR, MI_FECHA, MI_HORA,MI_CODIGO,MI_NOMBRELARGO,MI_MARCA,MI_MODELO,MI_SERIEDEVOLUTIVO,MI_ESPECIFICACION;
           EXIT WHEN MI_RSPLACAS%NOTFOUND; 


           MI_CAMPOS := ' COMPANIA,
                        TIPOMOVIMIENTO,
                        MOVIMIENTO,
                        ELEMENTO,
                        SERIE,
                        IND_REG,
                        VALORUNITARIO,
                        VALORTOTAL,
                        VLRUNITARIO_ANTESIVA,
                        FECHA,
                        HORA,
                        CODIGO,
                        CANTIDAD,
                        SALDOCANT,
                        ESPECIFICACION,
                        MARCA,
                        MODELO,
                        SERIEDEVOLUTIVO,
                        CREATED_BY,
                        DATE_CREATED';

            MI_VALORES := ''''||UN_COMPANIA||''',
                           '''||UN_TIPOMOVIMIENTO||''',
                             '||UN_MOVIMIENTO||',
                             '||MI_ELEMENTO||',
                             '||MI_SERIE||',
                             0,
                             '||MI_VALOR||',
                             '||MI_VALOR||',
                             '||MI_VALOR||',
                             '''||MI_FECHA||''',
                             '''||MI_HORA||''',
                             '''||MI_CODIGO||''',
                             1,
                             0,
                             '''||MI_ESPECIFICACION||''',
                             '''||MI_MARCA||''',
                             '''||MI_MODELO||''',
                             '''||MI_SERIEDEVOLUTIVO||''',
                             '''||UN_USUARIO||''',
                             CURRENT_DATE';       
               BEGIN
                    BEGIN
                      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'D_MOVIMIENTO',
                                         UN_ACCION   =>  'I', 
                                         UN_CAMPOS   =>  MI_CAMPOS, 
                                         UN_VALORES  =>  MI_VALORES);                             

                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                 RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                    END;
                       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN  
                                MI_MSGERROR(1).CLAVE := 'TABLA';
                                MI_MSGERROR(1).VALOR := 'D_MOVIMIENTO';
                                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD   => SQLCODE,
                                        UN_ERROR_COD => PCK_ERRORES.ERRR_ALMACEN_INSERT_TABLA,
                                        UN_REEMPLAZOS  => MI_MSGERROR 
                                      );
              END;            

       END LOOP PLACAS;     
    
   
    
    ELSE
        
        MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    =>  'D_MOVIMIENTO',
                                         UN_CRITERIO => ' COMPANIA       = '''||UN_COMPANIA||''' 
                                                      AND TIPOMOVIMIENTO = '''||UN_TIPOMOVIMIENTO||''' 
                                                      AND MOVIMIENTO     = '||UN_MOVIMIENTO||'',
                                         UN_CAMPO    => 'CODIGO');  
                                         
          --Ticket#7736969: Se ajusta proceso para que el estado sea malo solo en los casos que sea un traspaso a inservibles o baja.
             BEGIN 
                SELECT CONCEPTO 
                    INTO MI_CONCEPTO
                    FROM TIPOMOVIMIENTO
                    WHERE COMPANIA = UN_COMPANIA
                    AND CODIGO = UN_TIPOMOVIMIENTO;
                 EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_CONCEPTO := '';
             END;
             
             IF (UN_CLASE = 'T' AND MI_CONCEPTO = 'DS') OR (UN_CLASE = 'D' AND MI_CONCEPTO = 'DT')  THEN 
                 MI_ESTADO := 'M';
             ELSE 
                 MI_ESTADO := 'B';
                                
             END IF;                                           
        
        MI_STRSQL :='SELECT ('||MI_CONSECUTIVO||' + ROWNUM ),
                        DEVOLUTIVO.ELEMENTO,
                        DEVOLUTIVO.SERIE,
                        INVENTARIO.NOMBRELARGO,
                        DEVOLUTIVO.VALOR,
                        DEVOLUTIVO.MARCA,
                        DEVOLUTIVO.MODELO,
                        DEVOLUTIVO.SERIEDEVOLUTIVO,
						DEVOLUTIVO.DESCRIPCION,
                        TO_DATE('''||UN_FECHA||''',''DD/MM/YYYY HH24:MI:SS''),
                        TO_DATE(''30/12/1899'' || TO_CHAR(CURRENT_DATE,'' HH24:MI:SS''),''DD/MM/YYYY HH24:MI:SS'') HORA                        
                    FROM DEVOLUTIVO INNER JOIN INVENTARIO 
                            ON DEVOLUTIVO.COMPANIA = INVENTARIO.COMPANIA
                           AND DEVOLUTIVO.ELEMENTO = INVENTARIO.CODIGOELEMENTO
                    WHERE DEVOLUTIVO.COMPANIA               = '''||UN_COMPANIA||'''
                        AND DEVOLUTIVO.DEPENDENCIA          = '''||UN_DEPENDENCIA||'''
                        AND DEVOLUTIVO.RESPONSABLE          = '''||UN_RESPONSABLE||'''
                        AND DEVOLUTIVO.SUCURSAL_RESPONSABLE = '''||UN_SUCURSAL||'''
                        AND DEVOLUTIVO.PLACAANULADA         IN (0)
                    ORDER BY DEVOLUTIVO.SERIE DESC';    
                    
       <<PLACAS>>
       OPEN MI_RSPLACAS FOR MI_STRSQL;
       LOOP 
       FETCH MI_RSPLACAS INTO  MI_CODIGO,MI_ELEMENTO,MI_SERIE,MI_NOMBRELARGO, MI_VALOR,MI_MARCA,MI_MODELO,MI_SERIEDEVOLUTIVO,MI_ESPECIFICACION, MI_FECHA, MI_HORA;
           EXIT WHEN MI_RSPLACAS%NOTFOUND;
           
           MI_CAMPOS :='COMPANIA,
                        TIPOMOVIMIENTO,
                        MOVIMIENTO,
                        CODIGO,
                        ELEMENTO,
                        SERIE,
                        ESPECIFICACION,
                        CANTIDAD,
                        ESTADO,
                        VALORUNITARIO,
                        VLRUNITARIO_ANTESIVA,
                        VALORTOTAL,
                        FECHA,
                        HORA,
                        AUXILIAR,
                        VALORBASE,
                        TERCERO,
                        SUCURSAL,
                        ORDENDESUMINISTRO,
                        CANTANTERIOR,
                        MESESBODEGA,
                        VALORIVA,
                        PORCIVAUNI,
                        MARCA,
                        MODELO,
                        SERIEDEVOLUTIVO,
                        CREATED_BY,
                        DATE_CREATED';

                         
           MI_VALORES :=''''||UN_COMPANIA||''',
                         '''||UN_TIPOMOVIMIENTO||''',
                         '||UN_MOVIMIENTO||',
                         '''||MI_CODIGO||''',
                         '''||MI_ELEMENTO||''',
                         '''||MI_SERIE||''',
                         '''||MI_ESPECIFICACION||''',
                         1,
                         '''||MI_ESTADO||''',
                         '||MI_VALOR||',
                         '||MI_VALOR||',
                         '||MI_VALOR||',
                         '''||MI_FECHA||''',
                         '''||MI_HORA||''',
                         ''9999999999999999'',
                         '||MI_VALOR||',
                         '''||UN_RESPONSABLE||''',
                         '''||UN_SUCURSAL||''',
                         0,
                         0,
                         3,
                         0,
                         0,
                         '''||MI_MARCA||''',
                         '''||MI_MODELO||''',
                         '''||MI_SERIEDEVOLUTIVO||''',
                         '''||UN_USUARIO||''',
                         CURRENT_DATE';  
                         
               BEGIN
                    BEGIN
                      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'D_MOVIMIENTO',
                                         UN_ACCION   =>  'I', 
                                         UN_CAMPOS   =>  MI_CAMPOS, 
                                         UN_VALORES  =>  MI_VALORES);                             

                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                 RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                    END;
                       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN  
                                MI_MSGERROR(1).CLAVE := 'TABLA';
                                MI_MSGERROR(1).VALOR := 'D_MOVIMIENTO';
                                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD   => SQLCODE,
                                        UN_ERROR_COD => PCK_ERRORES.ERRR_ALMACEN_INSERT_TABLA,
                                        UN_REEMPLAZOS  => MI_MSGERROR 
                                      );
              END;                            
                          
       END LOOP PLACAS;    
        
    END IF; 

END PR_COPIARPLACASLOTE;

FUNCTION FC_PLANTILLA_IDCBIS 
(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOMOVIMIENTO     IN MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
  UN_NUMERO             IN MOVIMIENTO.NUMERO%TYPE
)
RETURN CLOB
AS
MI_RETORNO    CLOB;
BEGIN

      FOR RS IN (SELECT
                D.COMPANIA,
                D.TIPOMOVIMIENTO,
                D.MOVIMIENTO,
                D.CODIGO,
                D.ELEMENTO,
                I.NOMBRELARGO,
                U.NOMBRE UNIDAD,
                D.CANTIDAD,
                D.LOTE,
                D.TEMPERATURA,
                TO_CHAR(D.FECHA_VENCIMIENTO,'DD/MM/YYYY') FECHA_VENCIMIENTO,
                D.REGISTRO_SANITARIO
            FROM MOVIMIENTO M
            INNER JOIN D_MOVIMIENTO D
             ON D.COMPANIA       = M.COMPANIA
            AND D.TIPOMOVIMIENTO = M.TIPOMOVIMIENTO
            AND D.MOVIMIENTO     = M.NUMERO
            INNER JOIN INVENTARIO I 
             ON I.COMPANIA       = D.COMPANIA
            AND I.CODIGOELEMENTO = D.ELEMENTO
            LEFT JOIN UNIDAD U
            ON I.UNIDAD = U.UNIDAD
            WHERE M.COMPANIA = UN_COMPANIA
            AND M.TIPOMOVIMIENTO= UN_TIPOMOVIMIENTO
            AND M.NUMERO = UN_NUMERO
            ORDER BY D.CODIGO)
      LOOP
        MI_RETORNO := MI_RETORNO  ||
                      TO_CLOB(RS.CODIGO       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                              RS.ELEMENTO     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                              RS.NOMBRELARGO  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                              RS.UNIDAD       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                              RS.CANTIDAD     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                              RS.LOTE         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                              RS.TEMPERATURA  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                              RS.FECHA_VENCIMIENTO  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                              RS.REGISTRO_SANITARIO ||  PCK_DATOS.GL_SEPARADOR_REG);
      END LOOP;

RETURN MI_RETORNO;
END FC_PLANTILLA_IDCBIS;

PROCEDURE PR_DESAGREGAR_ELMT_ENTR
/*
    NAME              : PR_DESAGREGAR_ELMT_ENTR
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 14/02/2024
    TIME              :
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :    
    DESCRIPTION       : Validad las cantidades y valores al momento de afectar un contrato.
    PARAMETERS        : 
     
  */ 
(
  UN_COMPANIA IN VARCHAR2,
  UN_TIPO_CONTRATO IN VARCHAR2,
  UN_CONTRATO IN NUMBER,
  UN_ELEMENTO IN NUMBER,
  UN_CANTIDAD IN NUMBER,
  UN_VALOR   IN D_MOVIMIENTO.VALORTOTAL%TYPE,
  UN_TIPO_MOV IN D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
  UN_VALORUNITARIODI IN NUMBER
) AS
    MI_CANT_MOV NUMBER;
    MI_CANT_CONT NUMBER;
    MI_VALOR_CONT D_MOVIMIENTO.VALORTOTAL%TYPE;
    MI_VALOR_MOV D_MOVIMIENTO.VALORTOTAL%TYPE;
    MI_RTA2 CLOB;
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TIPO_CONTRATO PCK_SUBTIPOS.TI_PARAMETRO;
    MI_ELEM_LOTE PCK_SUBTIPOS.TI_PARAMETRO;
    MI_VALOR VARCHAR(5 CHAR);
    MI_CLASE VARCHAR(5 CHAR) DEFAULT 'S';
    MI_CONCEPTO VARCHAR(5 CHAR) DEFAULT 'C';
    
BEGIN

         MI_TIPO_CONTRATO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                   ,UN_NOMBRE    => 'TIPO CONTRATO DE SUMINISTRO'
                                                   ,UN_MODULO    => 10
                                                   ,UN_FECHA_PAR => SYSDATE);
                                                                                               
        MI_ELEM_LOTE := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                                   ,UN_NOMBRE    => 'DESAGREGAR ELEMENTOS EN ENTRADA SEGUN LOTE'
                                                   ,UN_MODULO    => 10
                                                   ,UN_FECHA_PAR => SYSDATE);
    

SELECT
      CLASE, CONCEPTO
  INTO MI_CLASE, MI_CONCEPTO
  FROM
      TIPOMOVIMIENTO
  WHERE
      COMPANIA = UN_COMPANIA
      AND CODIGO = UN_TIPO_MOV;

  IF MI_CLASE = 'E' AND (MI_CONCEPTO = 'C' OR MI_CONCEPTO = 'CM') AND UN_CANTIDAD <> 0 THEN --MOD JM 26/02/2025 CC 1013
    IF MI_ELEM_LOTE = 'SI' THEN

   IF  MI_TIPO_CONTRATO <> UN_TIPO_CONTRATO THEN
    -- JM INI 7747628 13/06/2024
    SELECT SALDOCANT 
        INTO MI_CANT_CONT
        FROM D_ORDENDECOMPRA
        WHERE COMPANIA = UN_COMPANIA
          AND CLASEORDEN = UN_TIPO_CONTRATO
          AND ORDENDECOMPRA = UN_CONTRATO
          AND ELEMENTO = UN_ELEMENTO
          AND VALORUNITARIODI = UN_VALORUNITARIODI;
    -- JM FIN 7747628 13/06/2024
    IF UN_CANTIDAD > MI_CANT_CONT THEN

        BEGIN
         MI_RTA2 := ('La cantidad total del movimiento supera la cantidad total especificada en la orden de compra.');
         MI_REEMPLAZOS(1).CLAVE := 'MENSAJE';
         MI_REEMPLAZOS(1).VALOR := SUBSTR(MI_RTA2,1,1000);
       RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 
        EXCEPTION  WHEN OTHERS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                                       UN_EXC_COD => SQLCODE,
                                       UN_ERROR_COD => PCK_ERRORES.ERRR_PERSONALIZADO,
                                       UN_REEMPLAZOS => MI_REEMPLAZOS
                                       );    
          END;
    END IF;
    END IF;

   IF MI_TIPO_CONTRATO = UN_TIPO_CONTRATO THEN

     SELECT (NVL(SUM(VLR_NOTA_CREDITO.VALOR),0) + ORDENDECOMPRA.VALORTOTAL) 
     INTO MI_VALOR_CONT  FROM ORDENDECOMPRA
		LEFT JOIN VLR_NOTA_CREDITO  
		ON VLR_NOTA_CREDITO.COMPANIA = ORDENDECOMPRA.COMPANIA               
		AND VLR_NOTA_CREDITO.CLASEORDEN = ORDENDECOMPRA.CLASEORDEN                        
		AND VLR_NOTA_CREDITO.ORDENDECOMPRA = ORDENDECOMPRA.NUMERO                         
		WHERE ORDENDECOMPRA.COMPANIA = UN_COMPANIA              
		AND ORDENDECOMPRA.CLASEORDEN = UN_TIPO_CONTRATO              
		AND ORDENDECOMPRA.NUMERO = UN_CONTRATO             
		GROUP BY VALORTOTAL;

    SELECT NVL(SUM(VALORTOTAL),0)
    INTO MI_VALOR_MOV
    FROM D_MOVIMIENTO
    WHERE COMPANIA = UN_COMPANIA
      AND TIPOMOVASOCIADO = UN_TIPO_CONTRATO
      AND MOVASOCIADO = UN_CONTRATO;

      IF MI_VALOR_MOV > MI_VALOR_CONT THEN

        BEGIN
         MI_RTA2 := ('El valor total de los detalles del movimiento excede el valor total de la orden de compra.');
         MI_REEMPLAZOS(1).CLAVE := 'MENSAJE';
         MI_REEMPLAZOS(1).VALOR := SUBSTR(MI_RTA2,1,1000);
       RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 
        EXCEPTION  WHEN OTHERS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                                       UN_EXC_COD => SQLCODE,
                                       UN_ERROR_COD => PCK_ERRORES.ERRR_PERSONALIZADO,
                                       UN_REEMPLAZOS => MI_REEMPLAZOS
                                       );    
          END;
      END IF;

    END IF;

    END IF;
    END IF;

END PR_DESAGREGAR_ELMT_ENTR;

PROCEDURE PR_SALDOS_INVENTBODEGA
/*
    NAME              : PR_SALDOS_INVENTBODEGA
    AUTHORS           : JEIMMY CAROLINA ROJAS GUERRERO  
    DESCRIPTION       : Inserta o actualiza los saldos en la tabla INVENTARIO_BODEGA de acuerdo a los auxiliares y la bodega.
*/ 
(
    UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOMOVIMIENTO        IN D_MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
    UN_MOVIMIENTO            IN D_MOVIMIENTO.MOVIMIENTO%TYPE,
    UN_ELEMENTO              IN PCK_SUBTIPOS.TI_ELEMENTO,
    UN_BODEGAO               IN VARCHAR2,
    UN_BODEGAD               IN VARCHAR2,
    UN_FUENTER               IN VARCHAR2,
    UN_REFERENCIA            IN VARCHAR2,
    UN_AUXILIAR              IN VARCHAR2,
    UN_PROYECTO              IN VARCHAR2,
    UN_CCOSTO                IN VARCHAR2,
    UN_LOTE                  IN VARCHAR2,
    UN_CANTIDAD              IN PCK_SUBTIPOS.TI_DOBLE,
    UN_CANTANTERIOR          IN PCK_SUBTIPOS.TI_DOBLE,									  
    UN_ACCION                IN VARCHAR2,
    UN_USUARIO               IN PCK_SUBTIPOS.TI_USUARIO
)
AS 
    MI_CLASE          VARCHAR2(200 CHAR);
    MI_CONCEPTO       VARCHAR2(200 CHAR);
    MI_EXISTEMOV      NUMBER := 0;
    MI_EXISTEE        NUMBER := 0;
    MI_CANTIDADE      NUMBER := 0;
    MI_EXISTES        NUMBER := 0;
    MI_CANTIDADS      NUMBER := 0;
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_CANTPROM       NUMBER := 0;
    MI_PROM           NUMBER := 0;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICIONE     PCK_SUBTIPOS.TI_CONDICION;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA            PCK_SUBTIPOS.TI_RTA_ACME;
    MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR;  
BEGIN
    BEGIN
        SELECT CLASE,CONCEPTO
        INTO MI_CLASE, MI_CONCEPTO
        FROM TIPOMOVIMIENTO								   
        WHERE COMPANIA = UN_COMPANIA
            AND CODIGO = UN_TIPOMOVIMIENTO;  
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CLASE    := '';
        MI_CONCEPTO := '';
    END;
    
    --Verifica si existe el registro en la D_MOVIMIENTO
    BEGIN
        SELECT COUNT(*)
        INTO MI_EXISTEMOV
        FROM D_MOVIMIENTO 
        INNER JOIN MOVIMIENTO
        ON D_MOVIMIENTO.COMPANIA = MOVIMIENTO.COMPANIA
        AND D_MOVIMIENTO.TIPOMOVIMIENTO = MOVIMIENTO.TIPOMOVIMIENTO
        AND D_MOVIMIENTO.MOVIMIENTO = MOVIMIENTO.NUMERO
        WHERE D_MOVIMIENTO.COMPANIA = UN_COMPANIA
            AND D_MOVIMIENTO.ELEMENTO = UN_ELEMENTO
            AND (MOVIMIENTO.BODEGA_DESTINO = UN_BODEGAD
                OR MOVIMIENTO.BODEGA_ORIGEN = UN_BODEGAO)
            AND D_MOVIMIENTO.FUENTEDERECURSO = UN_FUENTER
            AND D_MOVIMIENTO.REFERENCIA_CNT = UN_REFERENCIA
            AND D_MOVIMIENTO.AUXILIAR = UN_AUXILIAR
            AND D_MOVIMIENTO.CODIGOPROYECTO = UN_PROYECTO
            AND D_MOVIMIENTO.CENTRODECOSTO = UN_CCOSTO
            AND D_MOVIMIENTO.LOTE = UN_LOTE;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTEMOV:= 0;	 
    END;
		
    --Valores entrada
    BEGIN
        IF MI_CLASE = 'E' OR MI_CONCEPTO = 'TB' THEN
            SELECT COUNT('X'),COALESCE(SUM(CANTIDAD),0)
            INTO MI_EXISTEE, MI_CANTIDADE
            FROM INVENTARIO_BODEGA 
            WHERE COMPANIA = UN_COMPANIA
                AND ELEMENTO = UN_ELEMENTO
                AND BODEGA = UN_BODEGAD
                AND FUENTEDERECURSO = UN_FUENTER
                AND REFERENCIA = UN_REFERENCIA
                AND AUXILIAR = UN_AUXILIAR
                AND PROYECTO = UN_PROYECTO
                AND CENTRODECOSTO = UN_CCOSTO
                AND LOTE = UN_LOTE;
        END IF;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTEE   := 0;
        MI_CANTIDADE := 0;
    END;
    
    --Valores salida
    BEGIN
        IF MI_CLASE = 'S' OR MI_CONCEPTO = 'TB' THEN
            SELECT COUNT('X'),SUM(CANTIDAD)
            INTO MI_EXISTES, MI_CANTIDADS
            FROM INVENTARIO_BODEGA 
            WHERE COMPANIA = UN_COMPANIA
                AND ELEMENTO = UN_ELEMENTO
                AND BODEGA = UN_BODEGAO
                AND FUENTEDERECURSO = UN_FUENTER
                AND REFERENCIA = UN_REFERENCIA
                AND AUXILIAR = UN_AUXILIAR
                AND PROYECTO = UN_PROYECTO
                AND CENTRODECOSTO = UN_CCOSTO
                AND LOTE = UN_LOTE;
        END IF;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTES   := 0;
        MI_CANTIDADS := 0;
    END;
    
    MI_TABLA := 'INVENTARIO_BODEGA';
        
    --Condicion para entradas
    MI_CONDICIONE := 'COMPANIA            = '''||UN_COMPANIA||'''
                      AND ELEMENTO        = '||UN_ELEMENTO|| '
                      AND BODEGA          = '''||UN_BODEGAD||'''
                      AND FUENTEDERECURSO = '''||UN_FUENTER||'''
                      AND REFERENCIA      = '''||UN_REFERENCIA||'''
                      AND AUXILIAR        = '''||UN_AUXILIAR||'''
                      AND PROYECTO        = '''||UN_PROYECTO||'''
                      AND CENTRODECOSTO   = '''||UN_CCOSTO||'''
                      AND LOTE            = '''||UN_LOTE||''' ';
    
    IF MI_EXISTEMOV > 0 THEN
        MI_CANTPROM := 0;
        MI_PROM := 0;																			
        
        <<MOVIMIENTOS>>
        FOR RSD_MOVIMIENTO IN (
            SELECT CANTIDAD, VALORUNITARIO
            FROM (
                SELECT 
                    D_MOVIMIENTO.CANTIDAD,
                    D_MOVIMIENTO.VALORUNITARIO,
                    ROW_NUMBER() OVER (
                        ORDER BY 
                            TO_CHAR(D_MOVIMIENTO.FECHA,'RRRR/MM/DD') DESC,
                            TO_CHAR(D_MOVIMIENTO.HORA,'hh24:mi:ss') DESC
                    ) AS RN
            FROM D_MOVIMIENTO
            INNER JOIN TIPOMOVIMIENTO
            ON D_MOVIMIENTO.COMPANIA = TIPOMOVIMIENTO.COMPANIA 
            AND D_MOVIMIENTO.TIPOMOVIMIENTO = TIPOMOVIMIENTO.CODIGO
            INNER JOIN MOVIMIENTO
            ON D_MOVIMIENTO.COMPANIA = MOVIMIENTO.COMPANIA
            AND D_MOVIMIENTO.TIPOMOVIMIENTO = MOVIMIENTO.TIPOMOVIMIENTO
            AND D_MOVIMIENTO.MOVIMIENTO = MOVIMIENTO.NUMERO
            WHERE D_MOVIMIENTO.COMPANIA = UN_COMPANIA
                AND ELEMENTO = UN_ELEMENTO
                AND IND_REG NOT IN(0)
                AND D_MOVIMIENTO.FUENTEDERECURSO = UN_FUENTER
                AND REFERENCIA_CNT = UN_REFERENCIA
                AND D_MOVIMIENTO.AUXILIAR = UN_AUXILIAR
                AND D_MOVIMIENTO.CODIGOPROYECTO = UN_PROYECTO
                AND D_MOVIMIENTO.CENTRODECOSTO = UN_CCOSTO
                AND LOTE = UN_LOTE
                AND (BODEGA_DESTINO = CASE WHEN MI_CLASE = 'E' THEN UN_BODEGAD ELSE UN_BODEGAO END))
            WHERE ((MI_CANTIDADE > 0) OR (MI_CANTIDADE = 0 AND RN = 1))
            ORDER BY RN)
        LOOP
            MI_PROM := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => (MI_CANTPROM * MI_PROM + RSD_MOVIMIENTO.CANTIDAD * RSD_MOVIMIENTO.VALORUNITARIO) / (MI_CANTPROM + RSD_MOVIMIENTO.CANTIDAD),
                                               UN_PRECISION => 2);
            MI_CANTPROM := MI_CANTPROM + RSD_MOVIMIENTO.CANTIDAD;  
        END LOOP MOVIMIENTOS;
        
        IF MI_EXISTEE = 0 AND (MI_CLASE = 'E' OR MI_CONCEPTO = 'TB') THEN
        --Inserta entrada
            BEGIN
                MI_CAMPOS := 'COMPANIA
                             ,ELEMENTO
                             ,BODEGA
                             ,FUENTEDERECURSO
                             ,REFERENCIA
                             ,AUXILIAR
                             ,PROYECTO
                             ,CENTRODECOSTO
                             ,LOTE
                             ,CANTIDAD
                             ,VALORTOTAL
                             ,VLRUNITARIO
                             ,CREATED_BY
                             ,DATE_CREATED';
                MI_VALORES := ''''||UN_COMPANIA||'''
                             ,'||UN_ELEMENTO||'
                             ,'''||UN_BODEGAD||'''
                             ,'''||UN_FUENTER||'''
                             ,'''||UN_REFERENCIA||'''
                             ,'''||UN_AUXILIAR||'''
                             ,'''||UN_PROYECTO||'''
                             ,'''||UN_CCOSTO||'''
                             ,'''||UN_LOTE||'''
                             ,'||MI_CANTPROM||'
                             ,'||MI_PROM*MI_CANTPROM||'
                             ,'||MI_PROM||'
                             ,'''||UN_USUARIO||'''
                             ,SYSDATE';
                BEGIN
                    MI_RTA:=PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA
                                             ,UN_ACCION  => 'I'
                                             ,UN_CAMPOS  => MI_CAMPOS
                                             ,UN_VALORES => MI_VALORES );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_INSERT_DMOVIMIENTO
                                          ,UN_TABLAERROR => MI_TABLA
                                          ,UN_REEMPLAZOS => MI_REEMPLAZOS);
            END;
        END IF;
        
        IF MI_EXISTEE > 0 THEN
        --Modifica entrada
            IF UN_ACCION = 'INSERT' THEN
                MI_CANTIDADE := MI_CANTIDADE + UN_CANTIDAD;
            ELSIF UN_ACCION = 'UPDATE' THEN
                MI_CANTIDADE := MI_CANTIDADE + (UN_CANTIDAD - UN_CANTANTERIOR);
            ELSIF UN_ACCION = 'DELETE' THEN
                MI_CANTIDADE := MI_CANTIDADE - UN_CANTIDAD;
            END IF;
            
            BEGIN
                MI_CAMPOS := 'CANTIDAD      = '||MI_CANTIDADE||',
                              VALORTOTAL    = '||MI_PROM*MI_CANTIDADE||',
                              VLRUNITARIO   = '||CASE WHEN MI_CANTIDADE=0 THEN 0 ELSE MI_PROM END||',
                              DATE_MODIFIED = SYSDATE,
                              MODIFIED_BY   = '''||UN_USUARIO|| ''' ';
                                
                BEGIN
                    MI_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICIONE);
        
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_ALMACEN_ACTUALIZA_REGIST);
            END;            
        END IF;
        
        IF MI_EXISTES > 0 THEN
        --Modifica salida
            IF UN_ACCION = 'INSERT' THEN
                MI_CANTIDADS := MI_CANTIDADS - UN_CANTIDAD;
            ELSIF UN_ACCION = 'UPDATE' THEN
                MI_CANTIDADS := MI_CANTIDADS - (UN_CANTIDAD - UN_CANTANTERIOR);
            ELSIF UN_ACCION = 'DELETE' THEN
                MI_CANTIDADS := MI_CANTIDADS + UN_CANTIDAD;
            END IF;
            
            BEGIN
                MI_CAMPOS := 'CANTIDAD      = '||MI_CANTIDADS||',
                              VALORTOTAL    = '||MI_PROM*MI_CANTIDADS||',
                              VLRUNITARIO   = '||CASE WHEN MI_CANTIDADS=0 THEN 0 ELSE MI_PROM END||',
                              DATE_MODIFIED = SYSDATE,
                              MODIFIED_BY   = '''||UN_USUARIO|| ''' ';
                              
                MI_CONDICION := 'COMPANIA            = '''||UN_COMPANIA||'''
                                 AND ELEMENTO        = '||UN_ELEMENTO|| '
                                 AND BODEGA          = '''||UN_BODEGAO||'''
                                 AND FUENTEDERECURSO = '''||UN_FUENTER||'''
                                 AND REFERENCIA      = '''||UN_REFERENCIA||'''
                                 AND AUXILIAR        = '''||UN_AUXILIAR||'''
                                 AND PROYECTO        = '''||UN_PROYECTO||'''
                                 AND CENTRODECOSTO   = '''||UN_CCOSTO||'''
                                 AND LOTE            = '''||UN_LOTE||''' ';                              
            
                BEGIN
                    MI_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION);
        
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                END;
		
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_ALMACEN_ACTUALIZA_REGIST);
            END;
        END IF;
    ELSIF MI_EXISTEE > 0 AND (MI_CLASE = 'E' OR MI_CONCEPTO = 'TB') THEN
    --Elimina el registro de la tabla INVENTARIO_BODEGA cuando no exista en la D_MOVIMIENTO
        BEGIN
            BEGIN      
                MI_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                         ,UN_ACCION    => 'E'
                                         ,UN_CONDICION => MI_CONDICIONE);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_DELETE_DMOV);
        END;
    END IF;
END PR_SALDOS_INVENTBODEGA;

FUNCTION FC_CREAR_CAUSACION 
(
   UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
   UN_TIPOMOVIMIENTO     IN MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
   UN_NUMERO             IN MOVIMIENTO.NUMERO%TYPE,
   UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB
AS
MI_RETORNO       CLOB;
MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES       PCK_SUBTIPOS.TI_VALORES;
MI_CONDICION     PCK_SUBTIPOS.TI_CONDICION;
MI_NUMERO        PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_CONTAR        PCK_SUBTIPOS.TI_ENTERO_LARGO;
    
    BEGIN
        FOR RS IN (SELECT MOVIMIENTO.COMPANIA, TIPOMOVIMIENTO.NOMBRE,
                        MOVIMIENTO.TIPOMOVIMIENTO, MOVIMIENTO.NUMERO, 
                        MOVIMIENTO.TERCERO, MOVIMIENTO.SUCURSAL, 
                        MOVIMIENTO.VALORTOTAL, 
                        MOVIMIENTO.VALORTOTALCONIVA, 
                        (MOVIMIENTO.VALORTOTALCONIVA - MOVIMIENTO.VALORTOTAL) VLR_IVA,
                        MOVIMIENTO.TIPOMOVIMIENTO || ' ' || TIPOMOVIMIENTO.NOMBRE || ' ' || MOVIMIENTO.NUMERO || ' ,' || MOVIMIENTO.DESCRIPCION DESCRIPCION, 
                        MOVIMIENTO.FECHA, MOVIMIENTO.ANO,
                        TIPOMOVIMIENTO.COM_RELACIONADO, MOVIMIENTO.TIPO_CPTE_CONTABLE, MOVIMIENTO.CPTE_CONTABLE
                        FROM MOVIMIENTO
                        INNER JOIN TIPOMOVIMIENTO
                        ON TIPOMOVIMIENTO.COMPANIA = MOVIMIENTO.COMPANIA
                        AND TIPOMOVIMIENTO.CODIGO = MOVIMIENTO.TIPOMOVIMIENTO
                        WHERE MOVIMIENTO.COMPANIA = UN_COMPANIA
                        AND MOVIMIENTO.TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
                        AND MOVIMIENTO.NUMERO = UN_NUMERO)
          LOOP
            
            IF RS.COM_RELACIONADO IS NULL THEN 
            
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                    MI_RETORNO := 'El movimiento ' || RS.TIPOMOVIMIENTO || ' no tiene configurado comprobante contable';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'TIPOMOVIMIENTO',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
            
            END IF;
            
            IF RS.TIPO_CPTE_CONTABLE IS NOT NULL AND RS.CPTE_CONTABLE IS NOT NULL THEN 
            
                BEGIN
                    SELECT COUNT(NUMERO)
                        INTO MI_CONTAR 
                        FROM COMPROBANTE_CNT
                        WHERE COMPANIA = UN_COMPANIA
                        AND TIPO = RS.TIPO_CPTE_CONTABLE
                        AND NUMERO = RS.CPTE_CONTABLE;
                END;
                
                IF MI_CONTAR = 0 THEN 
                
                       BEGIN
                        MI_CAMPOS := 'TIPO_CPTE_CONTABLE  = '''',
                                      CPTE_CONTABLE       = '''' ';
                                      
                        MI_CONDICION := 'COMPANIA           = '''||UN_COMPANIA||'''
                                        AND TIPOMOVIMIENTO  = '''||UN_TIPOMOVIMIENTO||'''
                                        AND NUMERO          = '||UN_NUMERO||'';
        
                        BEGIN
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'MOVIMIENTO'
                                                                 ,UN_ACCION    => 'M'
                                                                 ,UN_CAMPOS    => MI_CAMPOS
                                                                 ,UN_CONDICION => MI_CONDICION);
        
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                  ,UN_ERROR_COD  => PCK_ERRORES.ERRR_ALMACEN_ACTUALIZA_REGIST);
                    END;
                
                ELSE
                
                    BEGIN
                        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                        MI_RETORNO := 'El movimiento ' || RS.TIPOMOVIMIENTO || ' ' || RS.NUMERO || ' tiene interfaz contable con el comprobante ' 
                        || RS.TIPO_CPTE_CONTABLE || ' ' || RS.CPTE_CONTABLE || '.';
                        MI_MSGERROR (1).CLAVE := 'MENSAJE';
                        MI_MSGERROR (1).VALOR := MI_RETORNO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                                UN_TABLAERROR => 'TIPOMOVIMIENTO',
                                                UN_REEMPLAZOS => MI_MSGERROR);
                    END;
                
                END IF;
            
            END IF;
            
            BEGIN
            SELECT MAX(NUMERO)
                INTO MI_NUMERO
                FROM COMPROBANTE_CNT
                WHERE COMPANIA = UN_COMPANIA
                AND TIPO = RS.COM_RELACIONADO
                AND ANO = TO_NUMBER(TO_CHAR(RS.FECHA,'YYYY')); --MOD JM CC 3521
            END;
            
            IF MI_NUMERO IS NULL THEN
                 
                 BEGIN --INI JM CC 3521 
                    SELECT CONSECUTIVO
                      INTO MI_NUMERO
                      FROM CONSECUTIVOTC
                     WHERE COMPANIA = UN_COMPANIA
                       AND TIPOCOMPROBANTE = RS.COM_RELACIONADO
                       AND ANO = TO_NUMBER(TO_CHAR(RS.FECHA,'YYYY'));
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_NUMERO := TO_NUMBER(TO_CHAR(RS.FECHA,'YYYY') || '000001');
                END;-- FIN JM 3521
                
            ELSE
                MI_NUMERO := MI_NUMERO + 1;
            END IF;
             
            MI_CAMPOS := 'COMPANIA
                          ,ANO
                          ,TIPO
                          ,NUMERO
                          ,FECHA
                          ,TERCERO
                          ,SUCURSAL
                          ,DESCRIPCION
                          ,VLR_DOCUMENTO
                          ,VLR_BASE
                          ,VLR_BASEIVA
                          ,CREATED_BY
                          ,DATE_CREATED';

            MI_VALORES := ''''|| RS.COMPANIA ||'''
                          ,'  || RS.ANO ||'
                          ,'''|| RS.COM_RELACIONADO ||'''
                          ,'  || MI_NUMERO ||'
                          ,'''|| RS.FECHA ||'''
                          ,'''|| RS.TERCERO ||'''
                          ,'''|| RS.SUCURSAL ||'''
                          ,'''|| RS.DESCRIPCION ||'''
                          ,'  || RS.VALORTOTALCONIVA ||'
                          ,'  || RS.VALORTOTAL ||'
                          ,'  || RS.VLR_IVA ||'
                          ,'''|| UN_USUARIO ||'''
                          ,SYSDATE';

            BEGIN
              
                BEGIN

                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'COMPROBANTE_CNT'
                                                        ,UN_ACCION  => 'I'
                                                        ,UN_CAMPOS  => MI_CAMPOS
                                                        ,UN_VALORES => MI_VALORES);

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                END;
              
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                  MI_MSGERROR(1).CLAVE := 'NUMERO';
                  MI_MSGERROR(1).VALOR := MI_NUMERO;
                  MI_MSGERROR(2).CLAVE := 'TIPO';
                  MI_MSGERROR(2).VALOR := RS.COM_RELACIONADO;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_SYSMANSF_INSCOMPCNT, 
                                             UN_TABLAERROR => 'COMPROBANTE_CNT', UN_REEMPLAZOS => MI_MSGERROR );
          END;
          
          BEGIN
                MI_CAMPOS := 'TIPO_CPTE_CONTABLE  = '''||RS.COM_RELACIONADO||''',
                              CPTE_CONTABLE       = '||MI_NUMERO||' ';
                              
                MI_CONDICION := 'COMPANIA           = '''||UN_COMPANIA||'''
                                AND TIPOMOVIMIENTO  = '''||UN_TIPOMOVIMIENTO||'''
                                AND NUMERO          = '||UN_NUMERO||'';

                BEGIN
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'MOVIMIENTO'
                                                         ,UN_ACCION    => 'M'
                                                         ,UN_CAMPOS    => MI_CAMPOS
                                                         ,UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_ALMACEN_ACTUALIZA_REGIST);
            END;
          
          MI_RETORNO := RS.COM_RELACIONADO || ',' || MI_NUMERO || ',' || RS.ANO;
            
          END LOOP;
    
    RETURN MI_RETORNO;
END FC_CREAR_CAUSACION;

PROCEDURE PR_KARDEO_PROGRAMADO
/*
      NAME            : PR_KARDEO_PROGRAMADO
    AUTHORS           : PAOLA VEGA
    DATE CREATION     : 27/01/2025
    TIME              : 08:00 AM
    SOURCE MODULE     : ALMACEN (10)
    DESCRIPTION       : Actualiza los elementos que estan descuadrados por medio del kardeo programado todos los dias a las 11:00 PM 
    MODIFIED BY       : CAMILO ANDRES PEREZ DUEÑAS
    DATE MODIFIED     : 06/02/2025
    --NAME  : Kardeo_programado
    --METHOD: PUT
    */
 AS
MI_FECHA       PCK_SUBTIPOS.TI_FECHA;
MI_PAR        PCK_SUBTIPOS.TI_FECHA;
MI_STRSQL       PCK_SUBTIPOS.TI_CAMPOS;
MI_RS                     SYS_REFCURSOR;
MI_RTA                    VARCHAR2(32000 CHAR);
BEGIN
 BEGIN 
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,''';
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_TIMESTAMP_FORMAT =''DD/MM/YYYY''';
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_DATE_FORMAT =''DD/MM/YYYY''';
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_TIMESTAMP_TZ_FORMAT =''DD/MM/YYYY''';
 END;

        BEGIN
            BEGIN
                FOR MI_RS IN(SELECT DISTINCT D_MOVIMIENTO.ELEMENTO,D_MOVIMIENTO.COMPANIA
                        FROM D_MOVIMIENTO
                                    INNER JOIN TIPOMOVIMIENTO ON TIPOMOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA
                                                                 AND TIPOMOVIMIENTO.CODIGO = D_MOVIMIENTO.TIPOMOVIMIENTO
                         WHERE
                            TIPOMOVIMIENTO.CLASE IN ('S')
                            AND TIPOMOVIMIENTO.TIPOELEMENTO IN ('C')
                            AND D_MOVIMIENTO.ELEMENTO || '-' || NVL(D_MOVIMIENTO.LOTE, '-1.-') || '-' || NVL(D_MOVIMIENTO.FUENTEDERECURSO, '-1.-') NOT IN (  SELECT
                            D_MOVIMIENTO.ELEMENTO || '-' ||   NVL(D_MOVIMIENTO.LOTE, '-1.-') || '-' ||    NVL(D_MOVIMIENTO.FUENTEDERECURSO, '-1.-') ELEMLOTEFUENTE
                        FROM
                            D_MOVIMIENTO
                            INNER JOIN TIPOMOVIMIENTO ON TIPOMOVIMIENTO.COMPANIA = D_MOVIMIENTO.COMPANIA
                                                         AND TIPOMOVIMIENTO.CODIGO = D_MOVIMIENTO.TIPOMOVIMIENTO
                        WHERE
                            TIPOMOVIMIENTO.CLASE IN ('E')
                            AND TIPOMOVIMIENTO.TIPOELEMENTO IN ('C')
                        GROUP BY
                            D_MOVIMIENTO.COMPANIA,
                            D_MOVIMIENTO.ELEMENTO,
                            D_MOVIMIENTO.LOTE,
                            D_MOVIMIENTO.FUENTEDERECURSO)
                        GROUP BY
                            D_MOVIMIENTO.COMPANIA,
                            D_MOVIMIENTO.ELEMENTO,
                            NVL(D_MOVIMIENTO.LOTE, '-1.-'),
                            NVL(D_MOVIMIENTO.FUENTEDERECURSO, '-1.-'))
                        LOOP
                              IF MI_RS.ELEMENTO<>0 THEN
                                    MI_FECHA := TO_DATE(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => MI_RS.COMPANIA
                                        ,UN_NOMBRE    =>'FECHA DE CORTE PARA INICIO DEL ALMACEN'
                                        ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                        ,UN_FECHA_PAR => SYSDATE),'DD/MM/YYYY');

                             MI_RTA :=  PCK_ALMACEN_COM3.FC_KARDEXELEMENTOTODOSHALM (
                                                                         UN_COMPANIA          => MI_RS.COMPANIA
                                                                       , UN_INTANOINICIAL     => EXTRACT(YEAR FROM MI_FECHA )
                                                                       , UN_INTMESINICIAL     => EXTRACT(MONTH FROM MI_FECHA)
                                                                       , UN_INTANOFINAL       => EXTRACT(YEAR FROM SYSDATE)
                                                                       , UN_INTMESFINAL       => EXTRACT(MONTH FROM SYSDATE)
                                                                       , UN_STRELEMENTOINICIAL =>MI_RS.ELEMENTO
                                                                       , UN_STRELEMENTOFINAL  => MI_RS.ELEMENTO
                                                                       , UN_KARDEXGENERAL     =>0
                                                                       , UN_FECHAINICIAL      =>MI_FECHA
                                                                       , UN_FECHAFINAL        =>TO_DATE(SYSDATE,'DD/MM/YYYY')
                                                                       , UN_TIPOMOVIMIENTO    =>''
                                                                       , UN_MOVIMIENTO        =>0);

                              END IF;
                        END LOOP;
                      
                            
                -- EXECUTE IMMEDIATE MI_STRSQL INTO MI_VLRDEPRECIACION;

            END;
        END;
END PR_KARDEO_PROGRAMADO;

PROCEDURE PR_DEPRECIACION_ACUMULADA
 /*
    NAME              : PR_DEPRECIACION_ACUMULADA
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 21/07/2025
    TIME              : 
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : 
    PARAMETERS        :
    --NAME:   depreciacionAcumulada
    --METHOD: PUT
  */
(
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ELEMENTO           IN VARCHAR2,
    UN_SERIE              IN VARCHAR2,
    UN_MES                IN VARCHAR2,
    UN_ANIO               IN VARCHAR2,
    UN_ACCION             IN VARCHAR2,
    UN_USUARIO            IN  PCK_SUBTIPOS.TI_USUARIO)
 AS
    MI_RTA          CLOB:='';
    MI_CAMPOS       VARCHAR2(32000 CHAR):='';
    MI_VALORES      VARCHAR2(32000 CHAR):='';
    MI_VLR_ACUM     PCK_SUBTIPOS.TI_DOBLE;
    MI_VLR_LIBROS   PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_LIBROS PCK_SUBTIPOS.TI_DOBLE;
    MI_TABLA        VARCHAR2(200 CHAR);
    MI_CONDICION    VARCHAR2(3200 CHAR);
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
      
    BEGIN    
    SELECT VALOR_ACUMULADO, VALOR_LIBROS, NIIF_VLRLIBROS
       INTO MI_VLR_ACUM, MI_VALOR_LIBROS, MI_VLR_LIBROS
    FROM DEPRECIACION_ACUMULADA
    WHERE COMPANIA = UN_COMPANIA
    AND ELEMENTO = UN_ELEMENTO
    AND SERIE = UN_SERIE;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VLR_ACUM := 0;
        MI_VLR_LIBROS := 0;
    END;
    
    IF UN_ACCION IN ('E') THEN
      MI_VLR_ACUM := 0;
      MI_VALOR_LIBROS := MI_VLR_LIBROS;
    END IF;
    
  BEGIN
    BEGIN
       MI_TABLA := 'DEPRECIAR';
       MI_CAMPOS := 'NIIF_VLRACUMULADO   = '''|| MI_VLR_ACUM || '''
                    ,NIIF_VLRLIBROS      = '''|| MI_VALOR_LIBROS || '''
                    ,NIIF_DEPACUMULADAAJ = '''|| MI_VLR_ACUM || '''
                    ,NIIF_SALDOAJ        = '''|| MI_VALOR_LIBROS || '''
                    ,NIIF_DEPACUMULADA   = '''|| MI_VLR_ACUM || '''
                    ,NIIF_ACUMDEPAJ      = '''|| MI_VLR_ACUM || ''' ';
                    
       MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                    AND ELEMENTO = '''||UN_ELEMENTO||'''
                    AND SERIE    = '||UN_SERIE||' 
                    AND ANO = ' || UN_ANIO ||'
                    AND MES = ' || UN_MES ||'';
                    
       MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION);
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
    END;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
              MI_MSGERROR (1).CLAVE := 'ELEMENTO';
              MI_MSGERROR (1).VALOR := UN_ELEMENTO;
              MI_MSGERROR (2).CLAVE := 'PLACA';
              MI_MSGERROR (2).VALOR := UN_SERIE;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_DEP_ACUM_NO_CALCULADA
                                        ,UN_REEMPLAZOS => MI_MSGERROR);
       END;
   
   BEGIN
    BEGIN
       MI_TABLA := 'DEVOLUTIVO';
       MI_CAMPOS := 'NIIF_DEPACUMULADA   = '''|| MI_VLR_ACUM || '''
                    ,NIIF_DEPACUMULADAAJ = '''|| MI_VLR_ACUM || '''
                    ,NIIF_SALDOAJ        = '''|| MI_VALOR_LIBROS || '''
                    ,NIIF_VLRLIBROS      = '''|| MI_VALOR_LIBROS || ''' ';
                    
       MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                    AND ELEMENTO = '''||UN_ELEMENTO||'''
                    AND SERIE    = '||UN_SERIE||' ';
                    
       MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION);
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
     END;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
              MI_MSGERROR (1).CLAVE := 'ELEMENTO';
              MI_MSGERROR (1).VALOR := UN_ELEMENTO;
              MI_MSGERROR (2).CLAVE := 'PLACA';
              MI_MSGERROR (2).VALOR := UN_SERIE;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_DEP_ACUM_NO_CALCULADA
                                        ,UN_REEMPLAZOS => MI_MSGERROR);
       END;

END PR_DEPRECIACION_ACUMULADA;

PROCEDURE PR_DEPRECIACION_ACUM_INI
 /*
    NAME              : PR_DEPRECIACION_ACUMULADA
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 21/07/2025
    TIME              : 
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : 
    PARAMETERS        :
    --NAME:   depreciacionAcumuladaIni
    --METHOD: PUT
  */
(
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ELEMENTO           IN VARCHAR2,
    UN_SERIE              IN VARCHAR2,
    UN_MES                IN VARCHAR2,
    UN_ANIO               IN VARCHAR2,
    UN_ACCION             IN VARCHAR2,
    UN_USUARIO            IN  PCK_SUBTIPOS.TI_USUARIO)
 AS
    MI_RTA            CLOB:='';
    MI_CAMPOS         VARCHAR2(32000 CHAR):='';
    MI_VALORES        VARCHAR2(32000 CHAR):='';
    MI_VLR_ACUM       PCK_SUBTIPOS.TI_DOBLE;
    MI_VLR_LIBROS     PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_LIBROS   PCK_SUBTIPOS.TI_DOBLE;
    MI_VIDA_UTIL      PCK_SUBTIPOS.TI_DOBLE;
    MI_NIIF_VIDA_UTIL PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_PERIODO  PCK_SUBTIPOS.TI_DOBLE;
    MI_TABLA          VARCHAR2(200 CHAR);
    MI_CONDICION      VARCHAR2(3200 CHAR);
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_NIIF_VLRDEPRECIACION  PCK_SUBTIPOS.TI_DOBLE;
    MI_NIIF_VLRACUMULADO PCK_SUBTIPOS.TI_DOBLE;
  BEGIN
      
    BEGIN    
    SELECT VALOR_ACUMULADO, VALOR_DEPRECIAR, NIIF_VLRLIBROS, VIDA_UTIL, NIIF_VIDA_UTIL, VALOR_PERIODO, NIIF_VLRDEPRECIACION, NIIF_VLRACUMULADO
       INTO MI_VLR_ACUM, MI_VALOR_LIBROS, MI_VLR_LIBROS, MI_VIDA_UTIL, MI_NIIF_VIDA_UTIL, MI_VALOR_PERIODO, MI_NIIF_VLRDEPRECIACION, MI_NIIF_VLRACUMULADO
    FROM DEPRECIACION_ACUMULADA_INICIAL
    WHERE COMPANIA = UN_COMPANIA
    AND ELEMENTO = UN_ELEMENTO
    AND SERIE = UN_SERIE;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VLR_ACUM := 0;
        MI_VLR_LIBROS := 0;
        MI_VIDA_UTIL := 0;
    END;
    
    IF UN_ACCION IN ('E') THEN
      MI_VLR_ACUM := MI_NIIF_VLRACUMULADO;
      MI_VALOR_LIBROS := MI_VLR_LIBROS;
      MI_VIDA_UTIL := MI_NIIF_VIDA_UTIL;
      MI_VALOR_PERIODO := MI_NIIF_VLRDEPRECIACION;
    END IF;
    
  BEGIN
    BEGIN
       MI_TABLA := 'DEPRECIAR';
       MI_CAMPOS := 'NIIF_VLRACUMULADO    = '''|| MI_VLR_ACUM || '''
                    ,NIIF_VLRDEPRECIACION = '''|| MI_VALOR_PERIODO || '''
                    ,NIIF_VLRLIBROS       = '''|| MI_VALOR_LIBROS || '''
                    ,NIIF_DEPACUMULADAAJ  = '''|| MI_VLR_ACUM || '''
                    ,NIIF_SALDOAJ         = '''|| MI_VALOR_LIBROS || '''
                    ,NIIF_DEPACUMULADA    = '''|| MI_VLR_ACUM || '''
                    ,NIIF_ACUMDEPAJ       = '''|| MI_VLR_ACUM || ''' ';
                    
       MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                    AND ELEMENTO = '''||UN_ELEMENTO||'''
                    AND SERIE    = '||UN_SERIE||' 
                    AND ANO = ' || UN_ANIO ||'
                    AND MES = ' || UN_MES ||'';
                    
       MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION);
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
    END;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
              MI_MSGERROR (1).CLAVE := 'ELEMENTO';
              MI_MSGERROR (1).VALOR := UN_ELEMENTO;
              MI_MSGERROR (2).CLAVE := 'PLACA';
              MI_MSGERROR (2).VALOR := UN_SERIE;
         /*     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_DEP_ACUM_NO_CALCULADA
                                        ,UN_REEMPLAZOS => MI_MSGERROR);*/
       END;
   
   BEGIN
    BEGIN
       MI_TABLA := 'DEVOLUTIVO';
       MI_CAMPOS := 'NIIF_DEPACUMULADA   = '''|| MI_VLR_ACUM || '''
                    ,NIIF_VIDA_UTIL      = '''|| MI_VIDA_UTIL || '''
                    ,NIIF_DEPACUMULADAAJ = '''|| MI_VLR_ACUM || '''
                    ,NIIF_SALDOAJ        = '''|| MI_VALOR_LIBROS || '''
                    ,NIIF_VLRLIBROS      = '''|| MI_VALOR_LIBROS || ''' ';
                    
       MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                    AND ELEMENTO = '''||UN_ELEMENTO||'''
                    AND SERIE    = '||UN_SERIE||' ';
                    
       MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION);
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
     END;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
              MI_MSGERROR (1).CLAVE := 'ELEMENTO';
              MI_MSGERROR (1).VALOR := UN_ELEMENTO;
              MI_MSGERROR (2).CLAVE := 'PLACA';
              MI_MSGERROR (2).VALOR := UN_SERIE;
            /*  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_DEP_ACUM_NO_CALCULADA
                                        ,UN_REEMPLAZOS => MI_MSGERROR);*/
       END;

END PR_DEPRECIACION_ACUM_INI;

FUNCTION FC_CARGAR_DEPRECIACION_INI 
(
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CADENA       IN CLOB,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
)RETURN CLOB AS 
MI_ERRORES              PCK_SUBTIPOS.TI_ENTERO:=0;
MI_MENSAJE              CLOB:='';
MI_TABLA                VARCHAR2(200 CHAR);
MI_CONDICION            VARCHAR2(3200 CHAR);
MI_CAMPOS               VARCHAR2(32000 CHAR):='';
MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_MERGEUSING           PCK_SUBTIPOS.TI_MERGEUSING;
MI_MERGEENLACE          PCK_SUBTIPOS.TI_MERGEENLACE;
MI_MERGEEXISTE          PCK_SUBTIPOS.TI_MERGEEXISTE;
MI_MERGENOEXISTE        PCK_SUBTIPOS.TI_MERGENOEXISTE;
MI_DATOS_FILA           PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS       PCK_SYSMAN_UTL.T_SPLIT;
MI_VALOR_HISTORICO      PCK_SUBTIPOS.TI_DOBLE;
MI_VIDA_UTIL            PCK_SUBTIPOS.TI_DOBLE;
MI_FECHA_ADQUISICION    DATE;
MI_PERIODO_AFECTAR      DATE;
MI_VALOR_PERIODO        PCK_SUBTIPOS.TI_DOBLE;
MI_VALOR_ACUMULADO      PCK_SUBTIPOS.TI_DOBLE;
MI_VALOR_DEPRECIAR      PCK_SUBTIPOS.TI_DOBLE;
MI_CAMPOS_INVALIDOS     CLOB;
MI_FECHA                PCK_SUBTIPOS.TI_FECHA;
MI_NIIF_VLRDEPRECIACION PCK_SUBTIPOS.TI_DOBLE;
MI_NIIF_VLRLIBROS       PCK_SUBTIPOS.TI_DOBLE;
MI_NIIF_VLRACUMULADO    PCK_SUBTIPOS.TI_DOBLE;
MI_NIIF_VIDA_UTIL       PCK_SUBTIPOS.TI_DOBLE;
MI_FECHA_PERIODO        VARCHAR2(200 CHAR);
BEGIN

    MI_FECHA := TO_DATE(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                            ,UN_NOMBRE    =>'FECHA DE CORTE PARA INICIO DEL ALMACEN'
                                            ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                            ,UN_FECHA_PAR => SYSDATE),'DD/MM/YYYY');
    
    MI_FECHA_PERIODO := TO_CHAR(LAST_DAY(MI_FECHA), 'DD/MM/YYYY');
                                        
    MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENA,
                                                   UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
    FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST
       LOOP
       MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                     UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);

    IF NVL(TRIM(MI_DATOS_COLUMNAS(1)), '0') = '0' THEN
        MI_CAMPOS_INVALIDOS := MI_CAMPOS_INVALIDOS || 'Elemento, ';
    END IF;
    IF NVL(TRIM(MI_DATOS_COLUMNAS(2)), '0') = '0' THEN
        MI_CAMPOS_INVALIDOS := MI_CAMPOS_INVALIDOS || 'Serie, ';
    END IF;
    IF NVL(TRIM(MI_DATOS_COLUMNAS(4)), '0') = '0' THEN
        MI_CAMPOS_INVALIDOS := MI_CAMPOS_INVALIDOS || 'Valor Acumulado, ';
    END IF;
    IF NVL(TRIM(MI_DATOS_COLUMNAS(5)), '0') = '0' THEN
        MI_CAMPOS_INVALIDOS := MI_CAMPOS_INVALIDOS || 'Observaciones, ';
    END IF;
    IF MI_CAMPOS_INVALIDOS IS NOT NULL THEN
        MI_CAMPOS_INVALIDOS := RTRIM(MI_CAMPOS_INVALIDOS, ', ');
        IF MI_ERRORES = 0 THEN
                  MI_MENSAJE := MI_MENSAJE || '*** INFORME DE INCONSISTENCIAS ***' 
                                           ||CHR(10)||CHR(13)||CHR(10)||CHR(13);
        END IF;
        MI_MENSAJE := MI_MENSAJE ||
                      'El registro tiene los siguientes campos inválidos (en 0 o vacíos): ' ||
                      MI_CAMPOS_INVALIDOS ||'.' || CHR(10);
        MI_ERRORES := MI_ERRORES + 1;
        MI_CAMPOS_INVALIDOS := NULL;
        CONTINUE;
    END IF;
          BEGIN              
              SELECT DISTINCT
                    D.VALOR,
                    D.NIIF_VIDA_UTIL,
                    D.NIIF_FECHAADQUISICION,
                    DP.NIIF_VLRDEPRECIACION,
                    DP.NIIF_VLRLIBROS,
                    DP.NIIF_VLRACUMULADO,
                    DAI.VIDA_UTIL,
                    DAI.VALOR_PERIODO
                    INTO MI_VALOR_HISTORICO,
                    MI_NIIF_VIDA_UTIL,
                    MI_FECHA_ADQUISICION,
                    MI_NIIF_VLRDEPRECIACION,
                    MI_NIIF_VLRLIBROS,
                    MI_NIIF_VLRACUMULADO,
                    MI_VIDA_UTIL,
                    MI_VALOR_PERIODO
                FROM
                    DEVOLUTIVO D
                    INNER JOIN DEPRECIAR DP 
                     ON D.COMPANIA = DP.COMPANIA
                    AND D.ELEMENTO = DP.ELEMENTO
                    AND D.SERIE = DP.SERIE
                    LEFT JOIN DEPRECIACION_ACUMULADA_INICIAL DAI
                     ON D.COMPANIA = DAI.COMPANIA
                    AND D.ELEMENTO = DAI.ELEMENTO
                    AND D.SERIE = DAI.SERIE
                WHERE
                    D.COMPANIA = UN_COMPANIA
                    AND D.ELEMENTO = MI_DATOS_COLUMNAS(1)
                    AND D.SERIE = MI_DATOS_COLUMNAS(2)
                    AND D.PLACAANULADA IN (0)
                    AND DP.MES = TO_CHAR(MI_FECHA,'MM')
                    AND DP.ANO = TO_CHAR(MI_FECHA,'YYYY')
                    AND D.NIIF_FECHAADQUISICION <= MI_FECHA;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                IF MI_ERRORES = 0 THEN
                  MI_MENSAJE := MI_MENSAJE || '*** INFORME DE INCONSISTENCIAS ***' 
                                           ||CHR(10)||CHR(13)||CHR(10)||CHR(13);
                END IF;
                MI_MENSAJE := MI_MENSAJE || 'El elemento "' || MI_DATOS_COLUMNAS(1) || '" con placa "' || MI_DATOS_COLUMNAS(2) ||
                                            '" no se encuentra un registro en la tabla DEPRECIAR para el periodo: ' || MI_FECHA_PERIODO || CHR(10);
                MI_ERRORES := MI_ERRORES+1;
                CONTINUE;
            END;
         IF MI_DATOS_COLUMNAS(3) > 0 THEN 
            MI_VALOR_PERIODO :=  MI_VALOR_HISTORICO / MI_DATOS_COLUMNAS(3);
         END IF;
         IF MI_DATOS_COLUMNAS(4) < 0 THEN 
             IF MI_ERRORES = 0 THEN
                          MI_MENSAJE := MI_MENSAJE || '*** INFORME DE INCONSISTENCIAS ***' 
                                                   ||CHR(10)||CHR(13)||CHR(10)||CHR(13);
                    END IF;
                    MI_MENSAJE := MI_MENSAJE || 'El elemento "' || MI_DATOS_COLUMNAS(1) || '" con placa "' || MI_DATOS_COLUMNAS(2) || 
                  '" tiene un valor de depreciación acumulada negativo ("' || MI_DATOS_COLUMNAS(4) || '"), por lo que no se actualizará o se insertara el registro.' || CHR(10);
                    MI_ERRORES := MI_ERRORES+1;
                    CONTINUE;
         END IF;
         MI_VALOR_DEPRECIAR := MI_VALOR_HISTORICO - MI_DATOS_COLUMNAS(4);
         IF MI_VALOR_DEPRECIAR < 0 THEN      
               IF MI_ERRORES = 0 THEN
                      MI_MENSAJE := MI_MENSAJE || '*** INFORME DE INCONSISTENCIAS ***' 
                                               ||CHR(10)||CHR(13)||CHR(10)||CHR(13);
                END IF;
                MI_MENSAJE := MI_MENSAJE || 'El elemento "' || MI_DATOS_COLUMNAS(1) || '" con placa "' || MI_DATOS_COLUMNAS(2) || 
              '" tiene un valor a depreciar negativo ("' || MI_VALOR_DEPRECIAR || '"), por lo que no se actualizará o se insertara el registro.' || CHR(10);
                MI_ERRORES := MI_ERRORES+1;
                CONTINUE;
         END IF;
         
         
    BEGIN     
     BEGIN
        MI_TABLA := 'DEPRECIACION_ACUMULADA_INICIAL';
        MI_MERGEUSING := 'SELECT
                             '''||UN_COMPANIA||''' COMPANIA,
                             '''||MI_DATOS_COLUMNAS(1)||''' ELEMENTO,
                             '''||MI_DATOS_COLUMNAS(2)||''' SERIE,
                             '''|| MI_VALOR_HISTORICO ||''' VALOR_HISTORICO,
                             '''||CASE WHEN MI_DATOS_COLUMNAS(3) = '0' THEN MI_VIDA_UTIL ELSE MI_DATOS_COLUMNAS(3) END||''' VIDA_UTIL,
                             TO_DATE('''|| MI_FECHA_ADQUISICION ||''', ''DD/MM/YYYY'') FECHA_ADQUISICION,
                             TO_DATE('''|| MI_FECHA_PERIODO ||''', ''DD/MM/YYYY'') PERIODO_AFECTAR,
                             '''||MI_VALOR_PERIODO||''' VALOR_PERIODO,
                             '''||MI_DATOS_COLUMNAS(4)||''' VALOR_ACUMULADO,
                             '''||MI_VALOR_DEPRECIAR||''' VALOR_DEPRECIAR,
                             '''||MI_DATOS_COLUMNAS(5)||''' OBSERVACIONES,
                             SYSDATE DATE_CREATED,
                             '''|| UN_USUARIO ||''' CREATED_BY,
                             '''|| MI_NIIF_VLRLIBROS ||''' NIIF_VLRLIBROS,
                             '''|| MI_NIIF_VIDA_UTIL ||''' NIIF_VIDA_UTIL,
                             '''|| MI_NIIF_VLRDEPRECIACION ||''' NIIF_VLRDEPRECIACION,
                             '''|| MI_NIIF_VLRACUMULADO ||''' NIIF_VLRACUMULADO
                           FROM DUAL';
        
        MI_MERGEENLACE := ' TABLA.COMPANIA = VISTA.COMPANIA
                            AND TABLA.ELEMENTO = VISTA.ELEMENTO
                            AND TABLA.SERIE    = VISTA.SERIE';
        
        MI_MERGEEXISTE := 'UPDATE SET TABLA.VIDA_UTIL       = VISTA.VIDA_UTIL,
                                        TABLA.VALOR_PERIODO   = VISTA.VALOR_PERIODO,
                                        TABLA.VALOR_ACUMULADO = VISTA.VALOR_ACUMULADO,
                                        TABLA.VALOR_DEPRECIAR = VISTA.VALOR_DEPRECIAR,
                                        TABLA.OBSERVACIONES   = VISTA.OBSERVACIONES';
        
        MI_MERGENOEXISTE := 'INSERT (COMPANIA, ELEMENTO, SERIE, VALOR_HISTORICO, VIDA_UTIL, FECHA_ADQUISICION, PERIODO_AFECTAR, VALOR_PERIODO, VALOR_ACUMULADO, VALOR_DEPRECIAR, OBSERVACIONES, DATE_CREATED, CREATED_BY, NIIF_VLRLIBROS, NIIF_VIDA_UTIL, NIIF_VLRDEPRECIACION, NIIF_VLRACUMULADO)
                             VALUES (VISTA.COMPANIA, VISTA.ELEMENTO, VISTA.SERIE, VISTA.VALOR_HISTORICO, VISTA.VIDA_UTIL, VISTA.FECHA_ADQUISICION, 
                             VISTA.PERIODO_AFECTAR, VISTA.VALOR_PERIODO, VISTA.VALOR_ACUMULADO, VISTA.VALOR_DEPRECIAR, VISTA.OBSERVACIONES, 
                             VISTA.DATE_CREATED, VISTA.CREATED_BY, VISTA.NIIF_VLRLIBROS, VISTA.NIIF_VIDA_UTIL, VISTA.NIIF_VLRDEPRECIACION, VISTA.NIIF_VLRACUMULADO)';
        
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA,
                                              UN_ACCION     => 'IM',
                                              UN_MERGEUSING => MI_MERGEUSING,
                                              UN_MERGEENLACE=> MI_MERGEENLACE,
                                              UN_MERGEEXISTE=> MI_MERGEEXISTE,
                                              UN_MERGENOEXIS=> MI_MERGENOEXISTE);
                    
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
      IF MI_ERRORES = 0 THEN
                  MI_MENSAJE := MI_MENSAJE || '*** INFORME DE INCONSISTENCIAS ***' 
                                           ||CHR(10)||CHR(13)||CHR(10)||CHR(13);
      END IF;
      MI_MENSAJE := MI_MENSAJE || 'Para elemento "' || MI_DATOS_COLUMNAS(1) || '" con placa "' || MI_DATOS_COLUMNAS(2) || 
      '" no se pudo actualizar o se insertar el registro en la tabla DEPRECIACION_ACUMULADA_INICIAL.' || CHR(10);
      MI_ERRORES := MI_ERRORES+1;
      CONTINUE;
    END;        
    PCK_ALMACEN_COM5.PR_DEPRECIACION_ACUM_INI(UN_COMPANIA, MI_DATOS_COLUMNAS(1), MI_DATOS_COLUMNAS(2),TO_CHAR(TO_DATE(MI_FECHA_PERIODO,'DD/MM/YYYY'),'MM'), TO_CHAR(TO_DATE(MI_FECHA_PERIODO,'DD/MM/YYYY'),'YYYY'), 'U', UN_USUARIO);
  
   END LOOP;

   IF MI_ERRORES > 0 THEN
    MI_MENSAJE := MI_MENSAJE||CHR(10)||CHR(13);
    MI_MENSAJE := MI_MENSAJE||'********************* FIN DEL INFORME *********************'||CHR(10)||CHR(13);
   END IF;
RETURN MI_MENSAJE;
END FC_CARGAR_DEPRECIACION_INI;

PROCEDURE PR_ACT_SALDOPEPS_PROGRAMADO
 AS
    MI_FECHA           PCK_SUBTIPOS.TI_FECHA;
    MI_MANEJA_PEPS     VARCHAR2(20);
    MI_STRSQL          PCK_SUBTIPOS.TI_CAMPOS;
    MI_ACT_PEPS        VARCHAR2(20);
    MI_ELEM_INICIAL    VARCHAR2(50);
    MI_ELEM_FINAL      VARCHAR2(50);
    MI_RESULTADO       VARCHAR2(4000);
    MI_COMPANIA        VARCHAR2(200);
    MI_ANO_INICIAL     PCK_SUBTIPOS.TI_ANIO;
    MI_MES_INICIAL     PCK_SUBTIPOS.TI_MES;
    MI_ANO_FINAL       PCK_SUBTIPOS.TI_ANIO;
    MI_MES_FINAL       PCK_SUBTIPOS.TI_MES;
    MI_FECHA_CORTE     DATE;
BEGIN
 BEGIN
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,''';
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_TIMESTAMP_FORMAT =''DD/MM/YYYY''';
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_DATE_FORMAT =''DD/MM/YYYY''';
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_TIMESTAMP_TZ_FORMAT =''DD/MM/YYYY''';
END;

BEGIN

 FOR C IN (SELECT CODIGO FROM COMPANIA) LOOP
        MI_COMPANIA := C.CODIGO;
        MI_FECHA_CORTE := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => MI_COMPANIA
                                                ,UN_NOMBRE    =>'FECHA DE CORTE PARA INICIO DEL ALMACEN'
                                                ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                                ,UN_FECHA_PAR => SYSDATE);

        MI_ANO_INICIAL := TO_NUMBER(TO_CHAR(MI_FECHA_CORTE, 'YYYY'));
        MI_MES_INICIAL := TO_NUMBER(TO_CHAR(MI_FECHA_CORTE, 'MM'));
        MI_ANO_FINAL   := TO_NUMBER(TO_CHAR(SYSDATE, 'YYYY'));
        MI_MES_FINAL   := TO_NUMBER(TO_CHAR(SYSDATE, 'MM'));

        
        MI_MANEJA_PEPS := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => MI_COMPANIA
                                                ,UN_NOMBRE    =>'MANEJA PEPS EN CONSUMO ALMACEN'
                                                ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                                ,UN_FECHA_PAR => SYSDATE);
        
        MI_ACT_PEPS := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => MI_COMPANIA
                                                ,UN_NOMBRE    =>'ACTUALIZAR POR MANTENIMIENTO PEPS EN CONSUMO ALMACEN'
                                                ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                                ,UN_FECHA_PAR => SYSDATE);
                                                
        IF MI_MANEJA_PEPS = 'SI' AND MI_ACT_PEPS = 'SI' THEN
            BEGIN
                SELECT MIN(CODIGOELEMENTO), MAX(CODIGOELEMENTO)
                  INTO MI_ELEM_INICIAL, MI_ELEM_FINAL
                  FROM INVENTARIO
                 WHERE COMPANIA = MI_COMPANIA;
    
                MI_RESULTADO := PCK_ALMACEN.FC_ACTUALIZAR_SALDOPEPS(
                                    UN_COMPANIA        => MI_COMPANIA,
                                    UN_ANOINICIAL      => MI_ANO_INICIAL,
                                    UN_MESINICIAL      => MI_MES_INICIAL,
                                    UN_ANOFINAL        => MI_ANO_FINAL,
                                    UN_MESFINAL        => MI_MES_FINAL,
                                    UN_ELEMENTOINICIAL => MI_ELEM_INICIAL,
                                    UN_ELEMENTOFINAL   => MI_ELEM_FINAL
                                );
    
                DBMS_OUTPUT.PUT_LINE('Procesada compañia: ' || MI_COMPANIA || ' -> ' || MI_RESULTADO);
    
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    DBMS_OUTPUT.PUT_LINE('Sin elementos en inventario para la compañía: ' || MI_COMPANIA);
                WHEN OTHERS THEN
                    DBMS_OUTPUT.PUT_LINE('Error procesando compañía ' || MI_COMPANIA || ': ' || SQLERRM);
            END;
        END IF;
    END LOOP;
END;
END PR_ACT_SALDOPEPS_PROGRAMADO;

PROCEDURE PR_CARGARELEMCONSUMOFISICO (
	/*
    NAME              : PR_CARGARELEMCONSUMOFISICO
    AUTHORS           : LVEGA
    DATE CREATION     : 09/12/2025
    DESCRIPTION       : carga masivamente elementos con saldos mayores a 0 desde el inventario hasta el inventario fisico de consumo, 
    					elementos activos, que tengan movimiento y que sean de tipo consumo
    --NAME:   cargarElemConsumoFisico
    --METHOD: INSERT
  */
    UN_COMPANIA     IN VARCHAR2,
    UN_BODEGA       IN VARCHAR2,
    UN_ANO          IN NUMBER,
    UN_FECHA_CORTE  IN DATE,
    UN_USUARIO      IN VARCHAR2
)
AS
  MI_CODIGO   VARCHAR2(200 CHAR);
  MI_TABLA     VARCHAR2(100 CHAR);
  MI_CAMPOS    VARCHAR2(4000 CHAR);
  MI_VALORES   VARCHAR2(4000 CHAR);
BEGIN      
     FOR RS IN (
        SELECT DISTINCT
            I.CODIGOELEMENTO,
            I.NOMBRELARGO,
            NVL(V.LOTE,'99999999999999999999') LOTE,
            NVL(V.FUENTEDERECURSO,'99999999999999999999') FUENTE,
            NVL(I.EXISTENCIA,0) SALDO,
            NVL(B.BODEGA,UN_BODEGA) BODEGA
        FROM INVENTARIO I
        LEFT JOIN INVENTARIO_BODEGA B
            ON I.COMPANIA = B.COMPANIA
           AND I.CODIGOELEMENTO = B.ELEMENTO
        LEFT JOIN (
            SELECT COMPANIA,
               ELEMENTO,
               LOTE,
               FUENTEDERECURSO,
               TIPOMOVIMIENTO,
               MOVIMIENTO,
               SALDOCANT
            FROM (
            SELECT D.*,
                   ROW_NUMBER() OVER (
                       PARTITION BY D.COMPANIA,
                                    D.ELEMENTO
                       ORDER BY D.FECHA DESC,
                                D.MOVIMIENTO DESC
                   ) RN
            FROM D_MOVIMIENTO D
            WHERE D.COMPANIA = UN_COMPANIA
              AND D.TIPOMOVIMIENTO LIKE 'S%'
        )
        WHERE RN = 1
        ) V
            ON I.COMPANIA = V.COMPANIA
           AND I.CODIGOELEMENTO = V.ELEMENTO
        WHERE 
            I.COMPANIA = UN_COMPANIA
            AND (B.BODEGA = UN_BODEGA OR B.BODEGA IS NULL)
            AND I.TIPO IN ('C')
            AND I.INACTIVO IN (0)
            AND I.TIENEMOVIMIENTO NOT IN (0)
            AND NVL(I.EXISTENCIA,0) > 0
            AND NOT EXISTS (
                SELECT 1 FROM INVENTARIO_FISICO_CONSUMO X
                 WHERE X.COMPANIA = UN_COMPANIA
                   AND TRUNC(X.FECHA_CORTE) = TRUNC(UN_FECHA_CORTE)
                   AND X.CODIGOELEMENTO = I.CODIGOELEMENTO
            )
    ) LOOP
         MI_TABLA := 'INVENTARIO_FISICO_CONSUMO';

        MI_CAMPOS := 'COMPANIA, ANO, CODIGOELEMENTO, NOMBREELEMENTO, '||
                     'LOTE, FUENTE, SALDO, CONTEO1, CONTEO2, CONTEO3, '||
                     'DEBITO, CREDITO, CREATED_BY, BODEGA, CONTEO, '||
                     'FECHA_CORTE, DATE_CREATED';

        MI_VALORES :=
            ''''||UN_COMPANIA||''','||
             UN_ANO||','||
            ''''||RS.CODIGOELEMENTO||''','||
            ''''||RS.NOMBRELARGO||''','||
            ''''||RS.LOTE||''','||
            ''''||RS.FUENTE||''','||
                  RS.SALDO||','||
                  '0,0,0,'||   -- CONTEO1, 2, 3
                  '0,0,'||     -- DEBITO, CREDITO
            ''''||UN_USUARIO||''','||
            ''''||RS.BODEGA||''','||
                  '0,'||       
            'TO_DATE('''||TO_CHAR(UN_FECHA_CORTE,'DD/MM/YYYY')||''',''DD/MM/YYYY''),'||
            'SYSDATE';

        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                UN_TABLA   => MI_TABLA,
                UN_ACCION  => 'I',
                UN_CAMPOS  => MI_CAMPOS,
                UN_VALORES => MI_VALORES
            );
        EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;

    END LOOP;
END PR_CARGARELEMCONSUMOFISICO;

FUNCTION FC_APLICAR_AJUSTE_INVENTARIO
/*
    NAME              : FC_APLICAR_AJUSTE_INVENTARIO
    AUTHORS           : LVEGA
    DATE CREATION     : 19/12/2025
    DESCRIPTION       : crea movimientos debito o credito de tipo AJD y AJC con los elementos que tenga diferencia entre el conteo 1 2 o 3
    y las existencias de inventario actuales, unicamente para elementos de consumo
    --NAME:   aplicarAjusteInventario
    --METHOD: INSERT
  */
(
    UN_COMPANIA             IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO                 IN  PCK_SUBTIPOS.TI_ANIO,
    UN_BODEGA_ORIGEN        IN  MOVIMIENTO.BODEGA_ORIGEN%TYPE,
    UN_BODEGA_DESTINO       IN  MOVIMIENTO.BODEGA_ORIGEN%TYPE,
    UN_TIPO_CREDITO         IN  MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
    UN_TIPO_DEBITO          IN  MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
    UN_DEPENDENCIA          IN  DEPENDENCIA.CODIGO%TYPE,
    UN_RESP_ORIGEN          IN  MOVIMIENTO.RESPONSABLE_ORIGEN%TYPE,
    UN_SUCURSAL_RESP        IN  MOVIMIENTO.SUCURSAL_RESDESTINO%TYPE,
    UN_RESP_DESTINO         IN  MOVIMIENTO.RESPONSABLE_ORIGEN%TYPE,
    UN_FUENTER              IN  MOVIMIENTO.FUENTEDERECURSO%TYPE,
    UN_REFERENCIA           IN  MOVIMIENTO.REFERENCIA%TYPE,
    UN_AUXILIAR             IN  MOVIMIENTO.AUXILIAR%TYPE,
    UN_CENTROCOSTO          IN  MOVIMIENTO.CENTRODECOSTO%TYPE,
    UN_OBSERVACIONES        IN  VARCHAR2,
    UN_FECHA_CORTE          IN  MOVIMIENTO.FECHA%TYPE,     
    UN_BODEGA_DEBITO        IN  MOVIMIENTO.BODEGA_ORIGEN%TYPE,
    UN_BODEGA_CREDITO       IN  MOVIMIENTO.BODEGA_ORIGEN%TYPE,
    UN_USUARIO              IN  VARCHAR2
)
RETURN VARCHAR2
AS
  MI_MSGRETORNO           VARCHAR2(4000) := '';
  MI_EXISTE_POST          VARCHAR2(1);
  MI_CODIGO_D             NUMBER;
  MI_CONSEC_DEBITO        PCK_SUBTIPOS.TI_ENTERO_LARGO := 0;
  MI_CONSEC_CREDITO       PCK_SUBTIPOS.TI_ENTERO_LARGO := 0;
  MI_HEADER_CREADO_DEB    VARCHAR2(1) := 'N';
  MI_HEADER_CREADO_CRE    VARCHAR2(1) := 'N';
  MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA_ACME             NUMBER;
  MI_NOW                  TIMESTAMP := SYSTIMESTAMP;
  MI_TEMP                 VARCHAR2(100);
  MI_FECHA_VENC           TIMESTAMP;
  MI_REG_SAN              VARCHAR2(200);
  MI_CONT_DEB_ITEMS       NUMBER := 0;
  MI_CONT_CRE_ITEMS       NUMBER := 0;
  MI_EXISTE_DEBITO        BOOLEAN := FALSE;
  MI_EXISTE_CREDITO       BOOLEAN := FALSE;
  MI_MANPEPS              VARCHAR2(10);
  MANFUENTE               VARCHAR2(10);
  MI_COSTO_UNITARIO       NUMBER := 0;
  MI_VLRUNIT_PROM         NUMBER := 0;
  MI_INICIAL              VARCHAR2(20);
  MI_CLASE_BODEGA_D       VARCHAR2(3);
  MI_CLASE_BODEGA_O       VARCHAR2(3);
  MI_APLICANIIF           NUMBER(1);
  MI_ELEMENTO              VARCHAR2(20);
  MI_TIPO_MOV              VARCHAR2(20);
  MI_MOVIMIENTO            VARCHAR2(20);
  MI_FECHA_MOV             TIMESTAMP;

BEGIN

  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,''';
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_TIMESTAMP_FORMAT =''DD/MM/YYYY HH24:MI:SS''';
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_DATE_FORMAT =''DD/MM/YYYY HH24:MI:SS''';
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_TIMESTAMP_TZ_FORMAT =''DD/MM/YYYY HH24:MI:SS''';
  EXECUTE IMMEDIATE 'ALTER SESSION SET TIME_ZONE=''-05:00''';
  
  
  MI_MANPEPS := PCK_SYSMAN_UTL.FC_PAR(
                  UN_COMPANIA  => UN_COMPANIA,
                  UN_NOMBRE    => 'MANEJA PEPS EN CONSUMO ALMACEN',
                  UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN(),
                  UN_FECHA_PAR => SYSDATE
                );
                
    MANFUENTE := PCK_SYSMAN_UTL.FC_PAR(
                  UN_COMPANIA  => UN_COMPANIA,
                  UN_NOMBRE    => 'MANEJA FUENTE DE RECURSOS PARA INTERFAZ ALMACEN',
                  UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN(),
                  UN_FECHA_PAR => SYSDATE
                );

  BEGIN
  SELECT A.CODIGOELEMENTO,
           D.TIPOMOVIMIENTO,
           D.MOVIMIENTO,
           D.FECHA
    INTO   MI_ELEMENTO,
           MI_TIPO_MOV,
           MI_MOVIMIENTO,
           MI_FECHA_MOV
    FROM   INVENTARIO_FISICO_CONSUMO A
    JOIN   D_MOVIMIENTO D
           ON D.COMPANIA = A.COMPANIA
          AND D.ELEMENTO = A.CODIGOELEMENTO
    WHERE  A.COMPANIA = UN_COMPANIA
      AND  TRUNC(A.FECHA_CORTE) = TRUNC(UN_FECHA_CORTE)
      AND  A.BODEGA = UN_BODEGA_ORIGEN
      AND  (NVL(A.DEBITO,0) > 0 OR NVL(A.CREDITO,0) > 0)
      AND  TRUNC(D.FECHA) > TRUNC(A.FECHA_CORTE)
      AND  ROWNUM = 1;
      MI_MSGRETORNO :=
     'Existen movimientos posteriores a la fecha de corte. ' ||
     'Elemento: ' || MI_ELEMENTO ||
     ', Tipo movimiento: ' || MI_TIPO_MOV ||
     ', Movimiento: ' || MI_MOVIMIENTO ||
     ', Fecha: ' || TO_CHAR(MI_FECHA_MOV,'YYYY-MM-DD');    
     RETURN MI_MSGRETORNO;
     
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      NULL; --Si no hay movimientos posteriores entonces continua con el proceso
  END;

        SELECT D.CLASE_BODEGA,
               O.CLASE_BODEGA
        INTO   MI_CLASE_BODEGA_D,
               MI_CLASE_BODEGA_O
        FROM   BODEGA D
               JOIN BODEGA O
               ON O.COMPANIA = D.COMPANIA
        WHERE  D.COMPANIA = UN_COMPANIA
        AND    D.CODIGO   = UN_BODEGA_DEBITO
        AND    O.CODIGO   = UN_BODEGA_ORIGEN;  
  ------------------------------------------------------------------
  -- 1) Procesar AJUSTES DEBITO (entradas) por cada FUENTE encontrada
  ------------------------------------------------------------------
  FOR MI_SRC IN (
    SELECT DISTINCT NVL(FUENTE,'') AS FUENTE
    FROM INVENTARIO_FISICO_CONSUMO A
    WHERE A.COMPANIA = UN_COMPANIA
      AND TRUNC(A.FECHA_CORTE) = TRUNC(UN_FECHA_CORTE)
      AND A.BODEGA = UN_BODEGA_ORIGEN
      AND NVL(A.DEBITO,0) > 0
  ) LOOP
    MI_EXISTE_DEBITO := TRUE;
    MI_HEADER_CREADO_DEB := 'N';
    
    BEGIN
    SELECT NUMEROINICIAL INTO MI_INICIAL FROM TIPOMOVIMIENTO WHERE COMPANIA = UN_COMPANIA AND CODIGO = UN_TIPO_DEBITO;
     EXCEPTION
    WHEN NO_DATA_FOUND THEN
      MI_INICIAL := 1;
    END;
  
    MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' AND TIPOMOVIMIENTO='''||UN_TIPO_DEBITO||'''';
    MI_CONSEC_DEBITO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                          UN_TABLA    => 'MOVIMIENTO',
                          UN_CRITERIO => MI_CONDICION,
                          UN_CAMPO    => 'NUMERO',
                          UN_INICIAL  => MI_INICIAL
                        );

        SELECT D.CLASE_BODEGA,
               O.CLASE_BODEGA
        INTO   MI_CLASE_BODEGA_D,
               MI_CLASE_BODEGA_O
        FROM   BODEGA D
               JOIN BODEGA O
               ON O.COMPANIA = D.COMPANIA
        WHERE  D.COMPANIA = UN_COMPANIA
        AND    D.CODIGO   = UN_BODEGA_DEBITO
        AND    O.CODIGO   = UN_BODEGA_ORIGEN;   
    
   
    -- HEADER DEL MOVIMIENTO DÉBITO (entrada) 
    MI_CAMPOS := '
       COMPANIA
      ,NUMERO
      ,TIPOMOVIMIENTO
      ,BODEGA_ORIGEN
      ,CLASE_BODEGA_ORIGEN
      ,BODEGA_DESTINO
      ,CLASE_BODEGA_DESTINO
      ,DEPENDENCIA_ORIGEN
      ,DEPENDENCIA_DESTINO
      ,RESPONSABLE_ORIGEN
      ,SUCURSAL_RESORIGEN
      ,RESPONSABLE_DESTINO
      ,SUCURSAL_RESDESTINO
      ,TERCERO
      ,FUENTEDERECURSO
      ,REFERENCIA
      ,AUXILIAR
      ,CENTRODECOSTO
      ,DESCRIPCION 
      ,FECHA
      ,HORA
      ,DATE_CREATED
      ,CREATED_BY';

    MI_VALORES := '
       '''|| UN_COMPANIA      ||''' ,
       '  || MI_CONSEC_DEBITO ||'   ,
       '''|| UN_TIPO_DEBITO   ||''' ,
       '''|| UN_BODEGA_DEBITO ||''' ,
       '''|| MI_CLASE_BODEGA_D ||''' ,
       '''|| UN_BODEGA_ORIGEN ||''' ,
       '''|| MI_CLASE_BODEGA_O ||''' ,
       '''|| '99999'          ||''' ,
       '''|| UN_DEPENDENCIA   ||''' ,
       '''|| '999999999999999999' ||''' ,
       '''|| '999'          ||''' ,
       '''|| UN_RESP_DESTINO  ||''' ,
       '''|| UN_SUCURSAL_RESP ||''',
       '''|| '999999999999999999' ||''' ,
       '''|| MI_SRC.FUENTE    ||''' ,
       '''|| UN_REFERENCIA    ||''' ,
       '''|| UN_AUXILIAR      ||''' ,
       '''|| UN_CENTROCOSTO   ||''' ,
       '''|| UN_OBSERVACIONES ||''' ,
       TO_DATE('''|| TO_CHAR(UN_FECHA_CORTE,'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
       TO_DATE('''|| TO_CHAR(UN_FECHA_CORTE,'DD/MM/YYYY HH24:MI:SS') || ''',''DD/MM/YYYY HH24:MI:SS''),
       SYSDATE,
       '''|| UN_USUARIO      ||''' ';

    BEGIN
      MI_RTA_ACME := PCK_DATOS.FC_ACME(
                         UN_TABLA   => 'MOVIMIENTO',
                         UN_ACCION  => 'I',
                         UN_CAMPOS  => MI_CAMPOS,
                         UN_VALORES => MI_VALORES
                     );
    EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
    END;

    FOR MI_RS_INV IN (
      SELECT COMPANIA, ANO, CODIGOELEMENTO, NOMBREELEMENTO, LOTE, FUENTE,
             SALDO, DEBITO, CREDITO, BODEGA, FECHA_CORTE
      FROM (
        SELECT A.*,
               ROW_NUMBER() OVER (PARTITION BY CODIGOELEMENTO, LOTE, BODEGA, FUENTE
                                  ORDER BY FECHA_CORTE DESC) RN
        FROM INVENTARIO_FISICO_CONSUMO A
        WHERE A.COMPANIA = UN_COMPANIA
          AND TRUNC(A.FECHA_CORTE) = TRUNC(UN_FECHA_CORTE)
          AND A.BODEGA = UN_BODEGA_ORIGEN
          AND NVL(A.DEBITO,0) > 0
          AND NVL(A.FUENTE,'') = NVL(MI_SRC.FUENTE,'')
          --AND AJUSTADO IN (0)
      )
      WHERE RN = 1
    ) LOOP
      BEGIN
        MI_TEMP := NULL;
        MI_FECHA_VENC := NULL;
        MI_REG_SAN := NULL;
        BEGIN
          SELECT TEMPERATURA, FECHA_VENCIMIENTO, REGISTRO_SANITARIO
          INTO   MI_TEMP, MI_FECHA_VENC, MI_REG_SAN
          FROM (
             SELECT D.TEMPERATURA, D.FECHA_VENCIMIENTO, D.REGISTRO_SANITARIO,
                   ROW_NUMBER() OVER (ORDER BY M.FECHA DESC, D.FECHA DESC) RN
            FROM D_MOVIMIENTO D
            JOIN MOVIMIENTO M ON M.COMPANIA = D.COMPANIA AND M.NUMERO = D.MOVIMIENTO
            JOIN TIPOMOVIMIENTO T ON M.COMPANIA = T.COMPANIA AND M.TIPOMOVIMIENTO = T.CODIGO 
            WHERE D.COMPANIA = MI_RS_INV.COMPANIA
              AND D.ELEMENTO = MI_RS_INV.CODIGOELEMENTO
              AND NVL(D.LOTE,'') = NVL(MI_RS_INV.LOTE,'')
              AND T.CLASE IN ('E')
          )
          WHERE RN = 1;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            MI_TEMP := NULL;
            MI_FECHA_VENC := NULL;
            MI_REG_SAN := NULL;
        END;

        MI_COSTO_UNITARIO := 0;
        MI_VLRUNIT_PROM := 0;
        BEGIN
          IF MI_MANPEPS = 'SI' THEN
            BEGIN
              SELECT VALORUNITARIO
              INTO   MI_COSTO_UNITARIO
              FROM (
                SELECT D.VALORUNITARIO, M.FECHA, D.DATE_CREATED
                FROM D_MOVIMIENTO D
                JOIN MOVIMIENTO M ON M.COMPANIA = D.COMPANIA AND M.NUMERO = D.MOVIMIENTO
                JOIN TIPOMOVIMIENTO T ON M.COMPANIA = T.COMPANIA AND M.TIPOMOVIMIENTO = T.CODIGO 
                WHERE D.COMPANIA = MI_RS_INV.COMPANIA
                  AND D.ELEMENTO = MI_RS_INV.CODIGOELEMENTO
                  AND T.CLASE IN ('E')
                ORDER BY M.FECHA ASC, D.DATE_CREATED ASC
              )
              WHERE ROWNUM = 1;
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                MI_COSTO_UNITARIO := 0;
            END;
            MI_VLRUNIT_PROM := NULL;
          ELSE
            BEGIN
              SELECT VLRUNITARIOPROM
              INTO   MI_VLRUNIT_PROM
              FROM (
                SELECT D.VLRUNITARIOPROM, M.FECHA, D.DATE_CREATED
                FROM D_MOVIMIENTO D
                JOIN MOVIMIENTO M ON M.COMPANIA = D.COMPANIA AND M.NUMERO = D.MOVIMIENTO
                WHERE D.COMPANIA = MI_RS_INV.COMPANIA
                  AND D.ELEMENTO = MI_RS_INV.CODIGOELEMENTO
                ORDER BY D.MOVIMIENTO DESC
              )
              WHERE ROWNUM = 1;
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                MI_VLRUNIT_PROM := 0;
            END;
            MI_COSTO_UNITARIO := MI_VLRUNIT_PROM;
          END IF;
        EXCEPTION
          WHEN OTHERS THEN
            MI_COSTO_UNITARIO := 0;
            MI_VLRUNIT_PROM := 0;
        END;

        MI_CODIGO_D := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                          UN_TABLA    => 'D_MOVIMIENTO',
                          UN_CRITERIO => 'COMPANIA='''||UN_COMPANIA||''' AND TIPOMOVIMIENTO='''||UN_TIPO_DEBITO||''' AND MOVIMIENTO='||MI_CONSEC_DEBITO,
                          UN_CAMPO    => 'CODIGO'
                       );

       MI_CAMPOS := '
           COMPANIA
          ,MOVIMIENTO
          ,TIPOMOVIMIENTO
          ,CODIGO
          ,ELEMENTO
          ,CANTIDAD
          ,CENTRODECOSTO
          ,AUXILIAR
          ,LOTE
          ,FUENTEDERECURSO
          ,TEMPERATURA
          ,FECHA_VENCIMIENTO
          ,REGISTRO_SANITARIO
          ,VALORUNITARIO
          ,VLRUNITARIOPROM
          ,VALORTOTAL
          ,FECHA
          ,HORA
          ,DATE_CREATED
          ,CREATED_BY';

        MI_VALORES := '
           '''|| MI_RS_INV.COMPANIA       ||''' ,
           '  || MI_CONSEC_DEBITO          ||'   ,
           '''|| UN_TIPO_DEBITO           ||''' ,
           '  || MI_CODIGO_D               ||'   ,
           '''|| MI_RS_INV.CODIGOELEMENTO ||''' ,
           '  || MI_RS_INV.DEBITO          ||'   ,
           '''|| UN_CENTROCOSTO           ||''' ,
           '''|| UN_AUXILIAR              ||''' ,
           '''|| CASE WHEN MANFUENTE = 'SI' AND MI_RS_INV.LOTE LIKE '999999999999999999%' THEN '' ELSE  MI_RS_INV.LOTE END  ||''' ,
           '''|| CASE WHEN MANFUENTE = 'SI' AND MI_SRC.FUENTE  LIKE '999999999999999999%' THEN '' ELSE  MI_SRC.FUENTE  END   ||''' ,
           '''|| NVL(MI_TEMP,'')          ||''' ,
           '  || CASE WHEN MI_FECHA_VENC IS NOT NULL THEN 'TO_DATE('''||TO_CHAR(MI_FECHA_VENC,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'')' ELSE 'NULL' END ||' ,
           '''|| NVL(MI_REG_SAN,'')       ||''' ,
          '  || REPLACE(TO_CHAR(MI_COSTO_UNITARIO), ',','.') ||' ,
           ' || REPLACE(TO_CHAR(CASE WHEN MI_VLRUNIT_PROM IS NULL THEN MI_COSTO_UNITARIO ELSE MI_VLRUNIT_PROM END ), ',', '.') ||' ,
           ' || REPLACE(TO_CHAR(CASE WHEN MI_VLRUNIT_PROM IS NULL THEN MI_COSTO_UNITARIO * MI_RS_INV.DEBITO ELSE MI_VLRUNIT_PROM * MI_RS_INV.DEBITO END),',','.') ||',
           TO_DATE('''|| TO_CHAR(MI_RS_INV.FECHA_CORTE,'DD/MM/YYYY HH24:MI:SS') || ''',''DD/MM/YYYY HH24:MI:SS''),
           TO_DATE('''|| TO_CHAR(MI_RS_INV.FECHA_CORTE,'DD/MM/YYYY HH24:MI:SS') || ''',''DD/MM/YYYY HH24:MI:SS''),
           SYSDATE,
           '''|| UN_USUARIO               ||''' ';


        BEGIN
          MI_RTA_ACME := PCK_DATOS.FC_ACME(
                             UN_TABLA   => 'D_MOVIMIENTO',
                             UN_ACCION  => 'I',
                             UN_CAMPOS  => MI_CAMPOS,
                             UN_VALORES => MI_VALORES
                         );
                         
           /* UPDATE INVENTARIO_FISICO_CONSUMO
            SET AJUSTADO = -1
            WHERE COMPANIA       = UN_COMPANIA
              AND ANO            = UN_ANIO
              AND CODIGOELEMENTO = MI_RS_INV.CODIGOELEMENTO
              AND NVL(LOTE,'999999999999999999')   = NVL(MI_RS_INV.LOTE,'999999999999999999')
              AND NVL(FUENTE,'999999999999999999') = NVL(MI_SRC.FUENTE,'999999999999999999')
              AND NVL(BODEGA,'') = NVL(UN_BODEGA_ORIGEN,'')
              AND FECHA_CORTE    = MI_RS_INV.FECHA_CORTE;
            */ 
  
        EXCEPTION
          WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
        MI_CONT_DEB_ITEMS := MI_CONT_DEB_ITEMS + 1;

      EXCEPTION
        WHEN OTHERS THEN
           RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
          RETURN MI_MSGRETORNO;
      END;
    END LOOP; -- MI_RS_INV detalle débito

  END LOOP; -- MI_SRC fuentes débito

  ------------------------------------------------------------------
  -- 2) Procesar AJUSTES CREDITO (salidas) por cada FUENTE encontrada
  ------------------------------------------------------------------
  FOR MI_SRC IN (
    SELECT DISTINCT NVL(FUENTE,'') AS FUENTE
    FROM INVENTARIO_FISICO_CONSUMO A
    WHERE A.COMPANIA = UN_COMPANIA
      AND TRUNC(A.FECHA_CORTE) = TRUNC(UN_FECHA_CORTE)
      AND A.BODEGA = UN_BODEGA_ORIGEN
      AND NVL(A.CREDITO,0) > 0
  ) LOOP
    MI_EXISTE_CREDITO := TRUE;
    MI_HEADER_CREADO_CRE := 'N';

 BEGIN
    SELECT NUMEROINICIAL INTO MI_INICIAL FROM TIPOMOVIMIENTO WHERE COMPANIA = UN_COMPANIA AND CODIGO = UN_TIPO_CREDITO;
     EXCEPTION
    WHEN NO_DATA_FOUND THEN
      MI_INICIAL := 1;
    END;
    
    MI_CONDICION := 'COMPANIA='''||UN_COMPANIA||''' AND TIPOMOVIMIENTO='''||UN_TIPO_CREDITO||'''';
    MI_CONSEC_CREDITO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                           UN_TABLA    => 'MOVIMIENTO',
                           UN_CRITERIO => MI_CONDICION,
                           UN_CAMPO    => 'NUMERO',
                           UN_INICIAL  => MI_INICIAL
                         );

    -- HEADER SALIDA (crédito)
    MI_CAMPOS := '
       COMPANIA
      ,NUMERO
      ,TIPOMOVIMIENTO
      ,BODEGA_ORIGEN
      ,CLASE_BODEGA_ORIGEN
      ,BODEGA_DESTINO
      ,CLASE_BODEGA_DESTINO
      ,DEPENDENCIA_ORIGEN
      ,DEPENDENCIA_DESTINO
      ,RESPONSABLE_ORIGEN
      ,SUCURSAL_RESORIGEN
      ,RESPONSABLE_DESTINO
      ,SUCURSAL_RESDESTINO
      ,TERCERO
      ,FUENTEDERECURSO
      ,REFERENCIA
      ,AUXILIAR
      ,CENTRODECOSTO
      ,DESCRIPCION
      ,FECHA
      ,HORA
      ,DATE_CREATED
      ,CREATED_BY';

    MI_VALORES := '
       '''|| UN_COMPANIA      ||''' ,
       '  || MI_CONSEC_CREDITO ||'   ,
       '''|| UN_TIPO_CREDITO  ||''' ,
       '''|| UN_BODEGA_ORIGEN ||''' ,
       '''|| MI_CLASE_BODEGA_O ||''' ,
       '''|| UN_BODEGA_CREDITO||''' ,
       '''|| MI_CLASE_BODEGA_D ||''' ,
       '''|| UN_DEPENDENCIA   ||''' ,
       '''|| '99999'          ||''' ,
       '''|| UN_RESP_ORIGEN   ||''' ,
       '''|| UN_SUCURSAL_RESP ||''',
       '''|| '999999999999999999' ||''' ,
       '''|| '999' ||''',
       '''|| '999999999999999999' ||''' ,
       '''|| MI_SRC.FUENTE    ||''' ,
       '''|| UN_REFERENCIA    ||''' ,
       '''|| UN_AUXILIAR      ||''' ,
       '''|| UN_CENTROCOSTO   ||''' ,
       '''|| UN_OBSERVACIONES ||''' ,
       TO_DATE('''|| TO_CHAR(UN_FECHA_CORTE,'DD/MM/YYYY HH24:MI:SS') || ''',''DD/MM/YYYY HH24:MI:SS''),
       TO_DATE('''|| TO_CHAR(UN_FECHA_CORTE,'DD/MM/YYYY HH24:MI:SS') || ''',''DD/MM/YYYY HH24:MI:SS''),
       SYSDATE,
       '''|| UN_USUARIO      ||''' ';

       
    BEGIN
      MI_RTA_ACME := PCK_DATOS.FC_ACME(
                         UN_TABLA   => 'MOVIMIENTO',
                         UN_ACCION  => 'I',
                         UN_CAMPOS  => MI_CAMPOS,
                         UN_VALORES => MI_VALORES
                     );
    EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
    END;

    -- Detalles de salida (CREDITO)
    FOR MI_RS_INV IN (
      SELECT COMPANIA, ANO, CODIGOELEMENTO, NOMBREELEMENTO, LOTE, FUENTE,
             SALDO, DEBITO, CREDITO, BODEGA, FECHA_CORTE
      FROM (
        SELECT A.*,
               ROW_NUMBER() OVER (PARTITION BY CODIGOELEMENTO, LOTE, BODEGA, FUENTE
                                  ORDER BY FECHA_CORTE DESC) RN
        FROM INVENTARIO_FISICO_CONSUMO A
        WHERE A.COMPANIA = UN_COMPANIA
          AND TRUNC(A.FECHA_CORTE) = TRUNC(UN_FECHA_CORTE)
          AND A.BODEGA = UN_BODEGA_ORIGEN
          AND NVL(A.CREDITO,0) > 0
          AND NVL(A.FUENTE,'') = NVL(MI_SRC.FUENTE,'')
          --AND AJUSTADO IN (0)
      )
      WHERE RN = 1
    ) LOOP
      BEGIN
        MI_TEMP := NULL;
        MI_FECHA_VENC := NULL;
        MI_REG_SAN := NULL;
        BEGIN
          SELECT TEMPERATURA, FECHA_VENCIMIENTO, REGISTRO_SANITARIO
          INTO   MI_TEMP, MI_FECHA_VENC, MI_REG_SAN
          FROM (
            SELECT D.TEMPERATURA, D.FECHA_VENCIMIENTO, D.REGISTRO_SANITARIO,
                   ROW_NUMBER() OVER (ORDER BY M.FECHA DESC, D.DATE_CREATED DESC) RN
            FROM D_MOVIMIENTO D
            JOIN MOVIMIENTO M ON M.COMPANIA = D.COMPANIA AND M.NUMERO = D.MOVIMIENTO
            JOIN TIPOMOVIMIENTO T ON M.COMPANIA = T.COMPANIA AND M.TIPOMOVIMIENTO = T.CODIGO 
            WHERE D.COMPANIA = MI_RS_INV.COMPANIA
              AND D.ELEMENTO = MI_RS_INV.CODIGOELEMENTO
              AND NVL(D.LOTE,'') = NVL(MI_RS_INV.LOTE,'')
               AND T.CLASE IN ('E')
          )
          WHERE RN = 1;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            MI_TEMP := NULL;
            MI_FECHA_VENC := NULL;
            MI_REG_SAN := NULL;
        END;

        MI_COSTO_UNITARIO := 0;
        MI_VLRUNIT_PROM := 0;
        BEGIN
          IF MI_MANPEPS = 'SI' THEN
            BEGIN
              SELECT VALORUNITARIO
              INTO   MI_COSTO_UNITARIO
              FROM (
                SELECT D.VALORUNITARIO, M.FECHA, D.DATE_CREATED
                FROM D_MOVIMIENTO D
                JOIN MOVIMIENTO M ON M.COMPANIA = D.COMPANIA AND M.NUMERO = D.MOVIMIENTO
                JOIN TIPOMOVIMIENTO T ON M.COMPANIA = T.COMPANIA AND M.TIPOMOVIMIENTO = T.CODIGO
                WHERE D.COMPANIA = MI_RS_INV.COMPANIA
                  AND D.ELEMENTO = MI_RS_INV.CODIGOELEMENTO
                   AND T.CLASE IN ('E')
                ORDER BY M.FECHA ASC, D.DATE_CREATED ASC
              )
              WHERE ROWNUM = 1;
              MI_APLICANIIF := -1;
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                MI_COSTO_UNITARIO := 0;
            END;
            MI_VLRUNIT_PROM := NULL; 
          ELSE
            BEGIN
              SELECT VLRUNITARIOPROM
              INTO   MI_VLRUNIT_PROM
              FROM (
                SELECT D.VLRUNITARIOPROM, M.FECHA, D.DATE_CREATED
                FROM D_MOVIMIENTO D
                JOIN MOVIMIENTO M ON M.COMPANIA = D.COMPANIA AND M.NUMERO = D.MOVIMIENTO
                WHERE D.COMPANIA = MI_RS_INV.COMPANIA
                  AND D.ELEMENTO = MI_RS_INV.CODIGOELEMENTO
                ORDER BY D.MOVIMIENTO DESC
              )
              WHERE ROWNUM = 1;
              MI_APLICANIIF := 0;
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                MI_VLRUNIT_PROM := 0;
            END;
            MI_COSTO_UNITARIO := MI_VLRUNIT_PROM;
          END IF;
        EXCEPTION
          WHEN OTHERS THEN
            MI_COSTO_UNITARIO := 0;
            MI_VLRUNIT_PROM := 0;
        END;

        MI_CODIGO_D := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                          UN_TABLA    => 'D_MOVIMIENTO',
                          UN_CRITERIO => 'COMPANIA='''||UN_COMPANIA||''' AND TIPOMOVIMIENTO='''||UN_TIPO_CREDITO||''' AND MOVIMIENTO='||MI_CONSEC_CREDITO,
                          UN_CAMPO    => 'CODIGO'
                       );

        MI_CAMPOS := '
           COMPANIA
          ,MOVIMIENTO
          ,TIPOMOVIMIENTO
          ,CODIGO
          ,ELEMENTO
          ,CANTIDAD
          ,CENTRODECOSTO
          ,AUXILIAR
          ,LOTE
          ,FUENTEDERECURSO
          ,TEMPERATURA
          ,FECHA_VENCIMIENTO
          ,REGISTRO_SANITARIO
          ,VALORUNITARIO
          ,VLRUNITARIOPROM
          ,VALORTOTAL
          ,APLICANIIF
          ,FECHA
          ,HORA
          ,DATE_CREATED
          ,CREATED_BY';

        MI_VALORES := '
           '''|| MI_RS_INV.COMPANIA       ||''' ,
           '  || MI_CONSEC_CREDITO         ||'   ,
           '''|| UN_TIPO_CREDITO          ||''' ,
           '  || MI_CODIGO_D               ||'   ,
           '''|| MI_RS_INV.CODIGOELEMENTO ||''' ,
           '  || MI_RS_INV.CREDITO         ||'   ,
           '''|| UN_CENTROCOSTO           ||''' ,
           '''|| UN_AUXILIAR              ||''' ,
           '''|| CASE WHEN MANFUENTE = 'SI' AND MI_RS_INV.LOTE LIKE '999999999999999999%' THEN '' ELSE  MI_RS_INV.LOTE END   ||''' ,
           '''|| CASE WHEN MANFUENTE = 'SI' AND MI_SRC.FUENTE  LIKE '999999999999999999%' THEN '' ELSE  MI_SRC.FUENTE  END   ||''' ,
           '''|| NVL(MI_TEMP,'')          ||''' ,
           '  || CASE WHEN MI_FECHA_VENC IS NOT NULL THEN 'TO_DATE('''||TO_CHAR(MI_FECHA_VENC,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'')' ELSE 'NULL' END ||' ,
           '''|| NVL(MI_REG_SAN,'')       ||''' ,
           '  || REPLACE(TO_CHAR(MI_COSTO_UNITARIO), ',','.') ||' ,
           ' || REPLACE(TO_CHAR(CASE WHEN MI_VLRUNIT_PROM IS NULL THEN MI_COSTO_UNITARIO ELSE MI_VLRUNIT_PROM END ), ',', '.') ||' ,
           ' || REPLACE(TO_CHAR(CASE WHEN MI_VLRUNIT_PROM IS NULL THEN MI_COSTO_UNITARIO * MI_RS_INV.CREDITO ELSE MI_VLRUNIT_PROM * MI_RS_INV.CREDITO END),',','.') ||',
           '''|| NVL(MI_APLICANIIF,0)     ||''' ,
           TO_DATE('''|| TO_CHAR(MI_RS_INV.FECHA_CORTE,'DD/MM/YYYY HH24:MI:SS') || ''',''DD/MM/YYYY HH24:MI:SS''),
           TO_DATE('''|| TO_CHAR(MI_RS_INV.FECHA_CORTE,'DD/MM/YYYY HH24:MI:SS') || ''',''DD/MM/YYYY HH24:MI:SS''),
           SYSDATE,
           '''|| UN_USUARIO               ||''' ';

        BEGIN
          MI_RTA_ACME := PCK_DATOS.FC_ACME(
                             UN_TABLA   => 'D_MOVIMIENTO',
                             UN_ACCION  => 'I',
                             UN_CAMPOS  => MI_CAMPOS,
                             UN_VALORES => MI_VALORES
                         );
                         
            /* UPDATE INVENTARIO_FISICO_CONSUMO
            SET AJUSTADO = -1
            WHERE COMPANIA       = UN_COMPANIA
              AND ANO            = UN_ANIO
              AND CODIGOELEMENTO = MI_RS_INV.CODIGOELEMENTO
              AND NVL(LOTE,'999999999999999999')   = NVL(MI_RS_INV.LOTE,'999999999999999999')
              AND NVL(FUENTE,'999999999999999999') = NVL(MI_SRC.FUENTE,'999999999999999999')
              AND NVL(BODEGA,'') = NVL(UN_BODEGA_ORIGEN,'')
              AND FECHA_CORTE    = MI_RS_INV.FECHA_CORTE;
            */  
        EXCEPTION
          WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;

        MI_CONT_CRE_ITEMS := MI_CONT_CRE_ITEMS + 1;

      EXCEPTION
        WHEN OTHERS THEN
          ROLLBACK;
          MI_MSGRETORNO := SQLERRM;
          RETURN MI_MSGRETORNO;
      END;
    END LOOP; -- MI_RS_INV detalle credito

  END LOOP; -- MI_SRC fuentes credito

  IF NOT MI_EXISTE_DEBITO AND NOT MI_EXISTE_CREDITO THEN
    MI_MSGRETORNO := 'No hay ajustes a aplicar para la fecha indicada';
    RETURN MI_MSGRETORNO;
  END IF;

  MI_MSGRETORNO := 'Comprobantes creados exitosamente';
  RETURN MI_MSGRETORNO;

EXCEPTION
  WHEN NO_DATA_FOUND THEN
    ROLLBACK;
    MI_MSGRETORNO := 'No se encuentra relación entre los auxiliares definidos en el proceso y el inventario, no se generarán movimientos.';
    RETURN MI_MSGRETORNO;
  WHEN OTHERS THEN
    ROLLBACK;
    MI_MSGRETORNO := SQLERRM;
    RETURN MI_MSGRETORNO;
END FC_APLICAR_AJUSTE_INVENTARIO;

PROCEDURE PR_DEPRECIACION_INICIAL
/*
    NAME              : PR_DEPRECIACION_INICIAL
    AUTHORS           : KATHERINE CARDENAS
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 10/04/2026
    TIME              : 
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : 
    PARAMETERS        :
    --NAME:   depreciacionInicial
    --METHOD: 
  */
(
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ELEMENTO           IN VARCHAR2,
    UN_SERIE              IN VARCHAR2,
    UN_MES                IN VARCHAR2,
    UN_ANIO               IN VARCHAR2,
    UN_USUARIO            IN  PCK_SUBTIPOS.TI_USUARIO)
 AS
    MI_ENLACE         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES              CLOB;
    MI_VLR_ACUM       PCK_SUBTIPOS.TI_DOBLE;
    MI_VLR_LIBROS     PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_LIBROS   PCK_SUBTIPOS.TI_DOBLE;
    MI_VALOR_PERIODO  PCK_SUBTIPOS.TI_DOBLE;
    MI_RTA                  PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA          VARCHAR2(200 CHAR);
    MI_CONDICION      VARCHAR2(3200 CHAR);
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
    
    MI_PERIODO          DATE;
 BEGIN
    MI_VLR_ACUM := 0;
    MI_VLR_LIBROS := 0;
    MI_VALOR_PERIODO := 0;
    MI_PERIODO :=  LAST_DAY(TO_DATE('01/' || UN_MES || '/' || UN_ANIO, 'DD/MM/YYYY'));
 
    MI_TABLA := 'DEPRECIAR';
    MI_VALORES := 'INSERT (COMPANIA,ELEMENTO,SERIE, ANO,MES, PERIODO,NIIF_VLRACUMULADO, NIIF_VLRDEPRECIACION, NIIF_VLRLIBROS,NIIF_DEPACUMULADAAJ,NIIF_SALDOAJ,NIIF_DEPACUMULADA,NIIF_ACUMDEPAJ)
                    VALUES (VISTA.COMPANIA, VISTA.ELEMENTO, VISTA.SERIE, VISTA.ANO, VISTA.MES,VISTA.PERIODO,VISTA.NIIF_VLRACUMULADO, VISTA.NIIF_VLRDEPRECIACION, VISTA.NIIF_VLRLIBROS,VISTA.NIIF_DEPACUMULADAAJ,VISTA.NIIF_SALDOAJ,VISTA.NIIF_DEPACUMULADA,VISTA.NIIF_ACUMDEPAJ)';

    MI_CONDICION := 'SELECT '''||UN_COMPANIA||''' COMPANIA, '''||UN_ELEMENTO||''' ELEMENTO,
                    '''||UN_SERIE||'''  SERIE, ''' || UN_ANIO ||''' ANO,
                    '''|| UN_MES || ''' MES,'''|| MI_PERIODO || ''' PERIODO,
                    '''|| MI_VLR_ACUM || ''' NIIF_VLRACUMULADO,
                    '''|| MI_VALOR_PERIODO || ''' NIIF_VLRDEPRECIACION,
                    '''|| MI_VLR_LIBROS || ''' NIIF_VLRLIBROS,
                    '''|| MI_VLR_ACUM || ''' NIIF_DEPACUMULADAAJ,
                    '''|| MI_VLR_LIBROS || ''' NIIF_SALDOAJ,
                    '''|| MI_VLR_ACUM || ''' NIIF_DEPACUMULADA,
                    '''|| MI_VLR_ACUM || ''' NIIF_ACUMDEPAJ FROM DUAL';
       

    BEGIN            
       MI_ENLACE   := 'TABLA.COMPANIA = VISTA.COMPANIA
                        AND TABLA.SERIE = VISTA.SERIE
                        AND TABLA.ELEMENTO = VISTA.ELEMENTO
                        AND TABLA.MES = VISTA.MES
                        AND TABLA.ANO = VISTA.ANO' ; 

        MI_RTA  := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA
                                     ,UN_ACCION      => 'IN'
                                     ,UN_MERGEUSING  =>  MI_CONDICION
                                     ,UN_MERGEENLACE =>  MI_ENLACE
                                         ,UN_MERGENOEXIS =>  MI_VALORES);
   
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
            MI_MSGERROR (1).CLAVE := 'ELEMENTO';
            MI_MSGERROR (1).VALOR := UN_ELEMENTO;
            MI_MSGERROR (2).CLAVE := 'PLACA';
            MI_MSGERROR (2).VALOR := UN_SERIE;
      
    END;

END PR_DEPRECIACION_INICIAL;

FUNCTION FC_CREARELEMENTOSC 
(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ELEMENTO           IN VARCHAR2,
  UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB
AS
    MI_RETORNO        CLOB;
    MI_EXISTE         NUMBER;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        CLOB;
    MI_CONDICION      CLOB;
    MI_ESTRUCTURA     VARCHAR2(320 CHAR);
    MI_PARTES         PCK_SYSMAN_UTL.T_SPLIT;
    MI_POSICION       NUMBER := 0;
    MI_CODIGO         VARCHAR2(100);
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;

    BEGIN
        MI_ESTRUCTURA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                          ,UN_NOMBRE    =>'ESTRUCTURA INVENTARIO'
                                          ,UN_MODULO    => PCK_DATOS.FC_MODULOALMACEN()
                                          ,UN_FECHA_PAR => SYSDATE);

        MI_PARTES := PCK_SYSMAN_UTL.FC_SPLIT_SYS(
                            UN_LISTA       => MI_ESTRUCTURA,
                            UN_DELIMITADOR => '-');
                            
        FOR RS IN (
            SELECT NITCOMPANIA, APLICA_ALM
            FROM CONSOLIDADA 
            WHERE COMPANIACON = UN_COMPANIA
            AND APLICA_ALM = -1
        )
        LOOP                    
                            
                FOR I IN MI_PARTES.FIRST .. MI_PARTES.LAST LOOP
                    
                        MI_POSICION := TO_NUMBER(MI_PARTES(I));
                    
                        MI_CODIGO := SUBSTR(UN_ELEMENTO, 1, MI_POSICION);
                        
                        SELECT COUNT(*)
                            INTO MI_EXISTE
                        FROM INVENTARIO
                        WHERE COMPANIA = RS.NITCOMPANIA
                        AND CODIGOELEMENTO = MI_CODIGO;
                
                        IF MI_EXISTE = 0 THEN
                
                                MI_CAMPOS:= 'COMPANIA, CODIGOELEMENTO, NOMBRELARGO, NOMBRECORTO, TIENEMOVIMIENTO, TIPO, REFERENCIA, UNIDAD, 
                                    MEDIDA, MARCA, LOCALIZACION, PORCIMPUESTO, PROVULTCOMPRA, PRECIOULTCOMPRA, EXISTENCIA, VALORTOTAL, 
                                    CANTRESERVADA, CANTCOMPORLLEGAR, CANTPEDENTREGA, CANTAPROXCOMPRA, VLRUNITARIOPROM, OPTIMOPEDIDO, 
                                    CANTIDADMINIMA, CANTIDADMAXIMA, CANTCONTEO1, CANTCONTEO2, CANTCONTEO3, CANTULTMOV, FACTOR, PROMEDIOVENTAS, 
                                    DIASPROMEDIO, DIASHUBOCONSUMO, DIASDEMORAPEDIDO, DIASPARAPEDIR, PRECIOREPOSICION, CUENTAACTIVO, PREDECESOR, 
                                    TIPOACTIVO, SERIE, CODIGOANTERIOR, DESCRIPCION, IDENTIFICADOR, CANTINSERVIBLE, CUENTAACTIVOI, CUENTAACTIVOS, 
                                    DURACIONCAMBIOTM, DURACIONCAMBIOKM, UNIDAD_TIEMPO, CUENTAACTIVOR, ESTADOS, CUBS, INACTIVO, CODIGOCUBS, 
                                    REQUIEREMANEJOAMBIENTAL, CONSECUTIVO_PC, IMG_ELEMENTO, COMERCIALIZADO, CANAL_DISTRIBUCION, CODIGO_CUM, 
                                    CREATED_BY, MODIFIED_BY, SUCURSAL, NIIF_CUENTAACTIVO, NIIF_CUENTAACTIVOI, NIIF_CUENTAACTIVOS, NIIF_CUENTAACTIVOR, 
                                    FECHAULTCOMPRA, FECHAVENCIMIENTO, FECHAULTMOV, DATE_CREATED, DATE_MODIFIED, CANTIDADPEDIDA, CANTIDADPORENTREGAR, 
                                    NUMERODEPEDIDOS, PRECIO1, PRECIO2, PRECIO3, PRECIOVENTA1, PRECIOVENTA2, PRECIOVENTA3, PORCPRECIO1, PORCPRECIO2, 
                                    PORCPRECIO3, VLRREDONDEO1, VLRREDONDEO2, VLRREDONDEO3, PRECIOBASELIQ, COMISIONCLIENTE, COMISIONCAJERO, IVADISCRIMINADO, 
                                    PIDEVALOR, PIDECANTIDAD, VALORMINIMO, VALORMAXIMO, GRADOS, IMPTARIFA, CAPACIDAD, IMPCONSUMO, CONSUBCOMPONENTES, 
                                    PRODUCTOPARAVENTA, CENTROCOSTO, REGISTROPORPESO, INVENTARIADO, FECHAINVENTARIADO, HORAINVENTARIADO, USUARIOINVENTARIADO, 
                                    CANTIDADINVENTARIADO, SALDOANT, PORCRETENCION, EXISTENCIAS_MAYORES, CONBOLETA, TIPOPRODUCTO, PORCDESCUENTO, HACERDESCUENTO, 
                                    CUENTAACTIVOFAC, NO_MOSTRAR_PRECIO_ENBOLETA, PEDIR_FECHA_DE_PROGRAMACION, SIN_SUBSIDIO, CANTXCAJA, TERCERO, SUCURSAL_TERCERO, 
                                    IVAPROPIO, IVATERCERO, PORETEIVA, PORCOMISION, AREARESPONSABLE, USUARIORESPONSABLE, UBICACIONRESPONSABLE, PISORESPONSABLE, 
                                    PORETECRE, VLROTRAMONEDA, CODBARRAS, CUENTAACTIVOC, IND_SST, AUXILIAR, ANO, CODIGO_ALMACEN, NIIF_CUENTAACTIVONEXP, 
                                    NIIF_CUENTAACTIVOMAN, CUENTAACTIVOMAN, CUENTAACTIVONEXP, PIDE_VENCIMIENTO, PORC_VALOR_RESIDUAL, DIGITOS_AGRUPACION_INVENTARIO';
                
                                MI_VALORES:= 'SELECT ''' || RS.NITCOMPANIA || ''' COMPANIA, CODIGOELEMENTO, NOMBRELARGO, NOMBRECORTO, TIENEMOVIMIENTO, TIPO, REFERENCIA, UNIDAD, 
                                        MEDIDA, MARCA, LOCALIZACION, PORCIMPUESTO, PROVULTCOMPRA, PRECIOULTCOMPRA, EXISTENCIA, VALORTOTAL, 
                                        CANTRESERVADA, CANTCOMPORLLEGAR, CANTPEDENTREGA, CANTAPROXCOMPRA, VLRUNITARIOPROM, OPTIMOPEDIDO, 
                                        CANTIDADMINIMA, CANTIDADMAXIMA, CANTCONTEO1, CANTCONTEO2, CANTCONTEO3, CANTULTMOV, FACTOR, PROMEDIOVENTAS, 
                                        DIASPROMEDIO, DIASHUBOCONSUMO, DIASDEMORAPEDIDO, DIASPARAPEDIR, PRECIOREPOSICION, CUENTAACTIVO, PREDECESOR, 
                                        TIPOACTIVO, SERIE, CODIGOANTERIOR, DESCRIPCION, IDENTIFICADOR, CANTINSERVIBLE, CUENTAACTIVOI, CUENTAACTIVOS, 
                                        DURACIONCAMBIOTM, DURACIONCAMBIOKM, UNIDAD_TIEMPO, CUENTAACTIVOR, ESTADOS, CUBS, INACTIVO, CODIGOCUBS, 
                                        REQUIEREMANEJOAMBIENTAL, CONSECUTIVO_PC, IMG_ELEMENTO, COMERCIALIZADO, CANAL_DISTRIBUCION, CODIGO_CUM, 
                                        CREATED_BY, MODIFIED_BY, SUCURSAL, NIIF_CUENTAACTIVO, NIIF_CUENTAACTIVOI, NIIF_CUENTAACTIVOS, NIIF_CUENTAACTIVOR, 
                                        FECHAULTCOMPRA, FECHAVENCIMIENTO, FECHAULTMOV, DATE_CREATED, DATE_MODIFIED, CANTIDADPEDIDA, CANTIDADPORENTREGAR, 
                                        NUMERODEPEDIDOS, PRECIO1, PRECIO2, PRECIO3, PRECIOVENTA1, PRECIOVENTA2, PRECIOVENTA3, PORCPRECIO1, PORCPRECIO2, 
                                        PORCPRECIO3, VLRREDONDEO1, VLRREDONDEO2, VLRREDONDEO3, PRECIOBASELIQ, COMISIONCLIENTE, COMISIONCAJERO, IVADISCRIMINADO, 
                                        PIDEVALOR, PIDECANTIDAD, VALORMINIMO, VALORMAXIMO, GRADOS, IMPTARIFA, CAPACIDAD, IMPCONSUMO, CONSUBCOMPONENTES, 
                                        PRODUCTOPARAVENTA, CENTROCOSTO, REGISTROPORPESO, INVENTARIADO, FECHAINVENTARIADO, HORAINVENTARIADO, USUARIOINVENTARIADO, 
                                        CANTIDADINVENTARIADO, SALDOANT, PORCRETENCION, EXISTENCIAS_MAYORES, CONBOLETA, TIPOPRODUCTO, PORCDESCUENTO, HACERDESCUENTO, 
                                        CUENTAACTIVOFAC, NO_MOSTRAR_PRECIO_ENBOLETA, PEDIR_FECHA_DE_PROGRAMACION, SIN_SUBSIDIO, CANTXCAJA, TERCERO, SUCURSAL_TERCERO, 
                                        IVAPROPIO, IVATERCERO, PORETEIVA, PORCOMISION, AREARESPONSABLE, USUARIORESPONSABLE, UBICACIONRESPONSABLE, PISORESPONSABLE, 
                                        PORETECRE, VLROTRAMONEDA, CODBARRAS, CUENTAACTIVOC, IND_SST, AUXILIAR, ANO, CODIGO_ALMACEN, NIIF_CUENTAACTIVONEXP, 
                                        NIIF_CUENTAACTIVOMAN, CUENTAACTIVOMAN, CUENTAACTIVONEXP, PIDE_VENCIMIENTO, PORC_VALOR_RESIDUAL, DIGITOS_AGRUPACION_INVENTARIO
                                FROM INVENTARIO
                                WHERE COMPANIA = ''' || UN_COMPANIA || '''
                                AND CODIGOELEMENTO = '''||MI_CODIGO||'''';
                
                                BEGIN
                                    BEGIN
                                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'INVENTARIO'
                                                                            ,UN_ACCION  => 'IS'
                                                                            ,UN_CAMPOS  => MI_CAMPOS
                                                                            ,UN_VALORES => MI_VALORES);
                                       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                          RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                                      END;
                            
                                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                    MI_MSGERROR (1).CLAVE := 'ELEMENTO';
                                    MI_MSGERROR (1).VALOR := UN_ELEMENTO;
                                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                      UN_EXC_COD    => SQLCODE,
                                      UN_REEMPLAZOS => MI_MSGERROR,
                                      UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_INSERT_BODEGA_ELEM);
                                END;                                        
                                                                        
                        ELSE
                        
                            FOR RS1 IN (SELECT *
                                FROM INVENTARIO
                                WHERE COMPANIA = UN_COMPANIA
                                AND CODIGOELEMENTO = MI_CODIGO)
                            LOOP
                            
                                BEGIN 
                                    BEGIN
                                      MI_CAMPOS := 'NOMBRELARGO    = ''' || RS1.NOMBRELARGO || ''',
                                                  NOMBRECORTO    = ''' || RS1.NOMBRECORTO || ''',
                                                  REFERENCIA     = ''' || RS1.REFERENCIA || ''',
                                                  UNIDAD         = ''' || RS1.UNIDAD || ''',
                                                  MEDIDA         = ''' || RS1.MEDIDA || ''',
                                                  INACTIVO       = ''' || NVL(RS1.MEDIDA,0) || ''',
                                                  CANTIDADMINIMA = ''' || NVL(RS1.CANTIDADMINIMA,0) || ''',
                                                  CANTIDADMAXIMA = ''' || NVL(RS1.CANTIDADMAXIMA,0) || ''',
                                                  OPTIMOPEDIDO   = ''' || RS1.OPTIMOPEDIDO || ''',
                                                  DATE_MODIFIED = SYSDATE,
                                                  MODIFIED_BY = ''' || UN_USUARIO ||'''';

                                      MI_CONDICION:= 'COMPANIA = ''' || RS.NITCOMPANIA ||'''' ||
                                          ' AND CODIGOELEMENTO = ''' || RS1.CODIGOELEMENTO || '''';

                                      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'INVENTARIO'
                                                              ,UN_ACCION    => 'M'
                                                              ,UN_CAMPOS    => MI_CAMPOS
                                                              ,UN_CONDICION => MI_CONDICION);

                                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                        RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                                    END;
                                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                                        MI_MSGERROR (1).CLAVE := 'ELEMENTO';
                                        MI_MSGERROR (1).VALOR := UN_ELEMENTO;
                                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                               UN_EXC_COD    => SQLCODE,
                                               UN_REEMPLAZOS => MI_MSGERROR,
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_ALMACEN_INSERT_BODEGA_ELEM
                                                             );
                            END;  
                            
                            END LOOP;
                        END IF;
                END LOOP;
        
        IF MI_RETORNO IS NULL OR MI_RETORNO = '' THEN
            MI_RETORNO := RS.NITCOMPANIA;
        ELSE
            MI_RETORNO := MI_RETORNO || ', ' || RS.NITCOMPANIA;
        END IF;
                
        END LOOP;
    
    IF MI_RETORNO IS NOT NULL THEN
        MI_RETORNO := 'Se actualizó el elemento en las compañías: ' || MI_RETORNO;
    END IF; 

    RETURN MI_RETORNO;
END FC_CREARELEMENTOSC;

PROCEDURE PR_UBICACION_FISICA
 /*
    NAME              : PR_UBICACION_FISICA
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  :
    DATE MIGRADOR     : 
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    PARAMETERS        :
    --NAME:   ubicacionfisica
    --METHOD: PUT
  */
(
    UN_ACCION             IN VARCHAR2,
    UN_IDHISTORIAL        IN NUMBER,
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ELEMENTO           IN VARCHAR2,
    UN_SERIE              IN VARCHAR2,
    UN_FECHA              IN DATE,
    UN_UBICACION          IN VARCHAR2,
    UN_OBSERVACIONES      IN VARCHAR2,
    UN_USUARIO            IN  PCK_SUBTIPOS.TI_USUARIO)
 AS
    MI_RTA          CLOB:='';
    MI_CAMPOS       VARCHAR2(32000 CHAR):='';
    MI_VALORES      VARCHAR2(32000 CHAR):='';
    MI_TABLA        VARCHAR2(200 CHAR);
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CONDICION    VARCHAR2(32000 CHAR):= '';
  
  BEGIN
    IF UN_ACCION IN ('I') THEN
        BEGIN
            MI_MSGERROR (1).CLAVE := 'ACCION';
            MI_MSGERROR (1).VALOR := 'INSERTAR';
            MI_TABLA := 'HISTORIAL_UBICACION';
            MI_CAMPOS := 'COMPANIA
                         ,ELEMENTO
                         ,SERIE
                         ,FECHA
                         ,UBICACION
                         ,OBSERVACIONES
                        ,CREATED_BY';
            MI_VALORES :=   ''''||UN_COMPANIA||'''
                            ,'''||UN_ELEMENTO||'''
                            ,'''||UN_SERIE||'''
                            ,'||PCK_SYSMAN_UTL.FC_SDATE(UN_FECHA)||'
                            ,'''||UN_UBICACION||'''
                            ,'''||UN_OBSERVACIONES||'''
                            ,'''||UN_USUARIO||'''';
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA
                                      ,UN_ACCION        => 'I'
                                      ,UN_CAMPOS        => MI_CAMPOS
                                      ,UN_VALORES       => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;              
    ELSIF UN_ACCION IN ('E') THEN
        BEGIN
            MI_MSGERROR (1).CLAVE := 'ACCION';
            MI_MSGERROR (1).VALOR := 'ELIMINAR';
            MI_TABLA :=     'HISTORIAL_UBICACION';
            MI_CONDICION := 'ID_HISTORIAL = ' || UN_IDHISTORIAL;
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA
                                        ,UN_ACCION      => 'E'
                                        ,UN_CONDICION   => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
    END IF;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
              MI_MSGERROR (2).CLAVE := 'ELEMENTO';
              MI_MSGERROR (2).VALOR := UN_ELEMENTO;
              MI_MSGERROR (3).CLAVE := 'PLACA';
              MI_MSGERROR (3).VALOR := UN_SERIE;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE
                                        ,UN_ERROR_COD  => PCK_ERRORES.ERR_ALM_HISTORIAL_UBICACION
                                        ,UN_REEMPLAZOS => MI_MSGERROR);
END PR_UBICACION_FISICA;


FUNCTION FC_CARGAR_DEP_NOCALCULADA
/*
    NAME              : FC_CARGAR_DEP_NOCALCULADA
    AUTHOR MIGRACION  : SEBASTIAN CARDENAS
    DESCRIPTION       : Carga masiva de depreciación no calculada desde Excel.
                        1. Valida campos, elemento en INVENTARIO y placa en DEVOLUTIVO/DEPRECIAR.
                        2. INSERT o UPDATE en DEPRECIACION_ACUMULADA.
                        3. Llama a PR_DEPRECIACION_ACUMULADA para propagar a DEPRECIAR y DEVOLUTIVO.
    --NAME:    cargarDepreciacionNoCalculada
    --METHOD:  POST
*/
(
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CADENA    IN CLOB,
    UN_USUARIO   IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB
AS

    MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
    MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
    MI_MENSAJE            CLOB := '';
    MI_ERRORES            CLOB := '';
    MI_INSERTADOS         NUMBER := 0;
    MI_ACTUALIZADOS       NUMBER := 0;
    MI_OMITIDOS           NUMBER := 0;
    CONTADOR              NUMBER := 0;
    MI_CODIGO_ELEMENTO    VARCHAR2(16 CHAR);
    MI_PLACA_STR          VARCHAR2(200 CHAR);
    MI_PERIODO_STR        VARCHAR2(200 CHAR);
    MI_DEP_ACUM_STR       VARCHAR2(200 CHAR);
    MI_OBSERVACIONES      VARCHAR2(500 CHAR);
    MI_PLACA_NUM          NUMBER(15,0);
    MI_PERIODO_TS         TIMESTAMP WITH LOCAL TIME ZONE;
    MI_DEP_ACUM_NUM       NUMBER(20,2);
    MI_VALOR_ADQ          NUMBER(20,2);
    MI_NIIF_VIDA_UTIL     NUMBER(20,2);
    MI_NIIF_VLRLIBROS_DEP NUMBER(20,2);
    MI_VALOR_LIBROS_CALC  NUMBER(20,2);
    MI_EXISTE_INV         NUMBER := 0;
    MI_TIENE_MOV          NUMBER := 0;
    MI_PLACA_COUNT        NUMBER := 0;
    MI_EXISTE_DEP_ACUM    NUMBER := 0;
    MI_EXISTE_DEPRECIAR   NUMBER := 0;
    MI_MES_PERIODO        VARCHAR2(2 CHAR);
    MI_ANIO_PERIODO       VARCHAR2(4 CHAR);
    MI_ACCION_PROC        VARCHAR2(1 CHAR);
    MI_CAMPOS       VARCHAR2(32000 CHAR):='';
    MI_VALORES      VARCHAR2(32000 CHAR):='';
    MI_TABLA        VARCHAR2(200 CHAR);
    MI_RTA          CLOB:='';
    CN_NODATA    CONSTANT VARCHAR2(6) := 'NoData';
    CN_FMT_FECHA CONSTANT VARCHAR2(20) := 'DD/MM/YYYY';
    MI_EXISTE_PERIODO     NUMBER := 0;
    MI_DEP_PERIODO_CERO   NUMBER := 0;

BEGIN
    IF UN_CADENA IS NULL OR TRIM(UN_CADENA) IS NULL THEN
        RETURN 'ERROR|No se recibió información para procesar.';
    END IF;
    
    MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(
                         UN_LISTA       => UN_CADENA,
                         UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_REG
                     );

    IF MI_DATOS_FILA IS NULL OR MI_DATOS_FILA.COUNT = 0 THEN
        RETURN 'ERROR|La cadena no contiene registros válidos.';
    END IF;

    <<DATOSDEPRECIACION>>
    FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST
    LOOP
        CONTADOR := CONTADOR + 1;

        IF TRIM(MI_DATOS_FILA(RS)) IS NULL THEN
            CONTINUE;
        END IF;

        MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(
                                 UN_LISTA       => MI_DATOS_FILA(RS),
                                 UN_DELIMITADOR => PCK_DATOS.GL_SEPARADOR_COL
                             );

        IF MI_DATOS_COLUMNAS.COUNT < 5 THEN
            MI_ERRORES := MI_ERRORES ||
                'FILA ' || CONTADOR || ': Número de columnas incorrecto. ' ||
                'Se esperaban 5, llegaron ' || MI_DATOS_COLUMNAS.COUNT || '.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;
        
        MI_CODIGO_ELEMENTO := TRIM(MI_DATOS_COLUMNAS(1));
        MI_PLACA_STR       := TRIM(MI_DATOS_COLUMNAS(2));
        MI_PERIODO_STR     := TRIM(MI_DATOS_COLUMNAS(3));
        MI_DEP_ACUM_STR    := TRIM(MI_DATOS_COLUMNAS(4));
        MI_OBSERVACIONES   := TRIM(MI_DATOS_COLUMNAS(5));

        MI_PLACA_NUM          := NULL;
        MI_PERIODO_TS         := NULL;
        MI_DEP_ACUM_NUM       := NULL;
        MI_VALOR_ADQ          := NULL;
        MI_NIIF_VIDA_UTIL     := NULL;
        MI_NIIF_VLRLIBROS_DEP := NULL;
        MI_VALOR_LIBROS_CALC  := NULL;
        MI_EXISTE_INV         := 0;
        MI_TIENE_MOV          := 0;
        MI_PLACA_COUNT        := 0;
        MI_EXISTE_DEP_ACUM    := 0;
        MI_EXISTE_DEPRECIAR   := 0;
        MI_MES_PERIODO        := NULL;
        MI_ANIO_PERIODO       := NULL;
        MI_ACCION_PROC        := 'I';

        IF MI_CODIGO_ELEMENTO IS NULL OR MI_CODIGO_ELEMENTO = CN_NODATA THEN
            MI_ERRORES := MI_ERRORES ||
                'FILA ' || CONTADOR || ': CODIGO_ELEMENTO es obligatorio y está vacío.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        IF MI_PLACA_STR IS NULL OR MI_PLACA_STR = CN_NODATA THEN
            MI_ERRORES := MI_ERRORES ||
                'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO || ']: ' ||
                'PLACA es obligatoria y está vacía.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        BEGIN
            MI_PLACA_NUM := TO_NUMBER(MI_PLACA_STR);
        EXCEPTION
            WHEN VALUE_ERROR THEN
                MI_ERRORES := MI_ERRORES ||
                    'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO || ']: ' ||
                    'PLACA "' || MI_PLACA_STR || '" no es un valor numérico válido.' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END;

        IF MI_PERIODO_STR IS NULL OR MI_PERIODO_STR = CN_NODATA THEN
            MI_ERRORES := MI_ERRORES ||
                'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO || ']: ' ||
                'PERIODO es obligatorio y está vacío.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        BEGIN
            MI_PERIODO_TS   := TO_TIMESTAMP_TZ(MI_PERIODO_STR, CN_FMT_FECHA);
            MI_MES_PERIODO  := TO_CHAR(MI_PERIODO_TS, 'MM');
            MI_ANIO_PERIODO := TO_CHAR(MI_PERIODO_TS, 'YYYY');
        EXCEPTION
            WHEN OTHERS THEN
                MI_ERRORES := MI_ERRORES ||
                    'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO || ']: ' ||
                    'PERIODO "' || MI_PERIODO_STR || '" no tiene el formato esperado (' ||
                    CN_FMT_FECHA || ').' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END;

        IF MI_DEP_ACUM_STR IS NULL OR MI_DEP_ACUM_STR = CN_NODATA THEN
            MI_ERRORES := MI_ERRORES ||
                'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO || ']: ' ||
                'DEPRECIACION_ACUMULADA es obligatoria y está vacía.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        BEGIN
            MI_DEP_ACUM_NUM := TO_NUMBER(REPLACE(MI_DEP_ACUM_STR, ',', '.'));
        EXCEPTION
            WHEN VALUE_ERROR THEN
                MI_ERRORES := MI_ERRORES ||
                    'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO || ']: ' ||
                    'DEPRECIACION_ACUMULADA "' || MI_DEP_ACUM_STR || '" no es un valor numérico válido.' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END;

        IF MI_DEP_ACUM_NUM <= 0 THEN
            MI_ERRORES := MI_ERRORES ||
                'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO || ']: ' ||
                'DEPRECIACION_ACUMULADA debe ser mayor a cero. Valor recibido: ' ||
                MI_DEP_ACUM_NUM || '.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        IF MI_OBSERVACIONES IS NULL OR MI_OBSERVACIONES = CN_NODATA THEN
            MI_ERRORES := MI_ERRORES ||
                'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO || ']: ' ||
                'OBSERVACIONES es obligatoria. Indique el motivo de la actualización.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        BEGIN
           SELECT
            COUNT(1),
            MAX(TIENEMOVIMIENTO)
            INTO MI_EXISTE_INV,
                 MI_TIENE_MOV
           FROM
            INVENTARIO
           WHERE COMPANIA = UN_COMPANIA
            AND CODIGOELEMENTO = MI_CODIGO_ELEMENTO
            AND TIPO IN ('D','N','M','E');
        EXCEPTION
            WHEN OTHERS THEN
                MI_ERRORES := MI_ERRORES ||
                    'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO || ']: ' ||
                    'Error al consultar INVENTARIO: ' || SQLERRM || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END;

        IF MI_EXISTE_INV = 0 THEN
            MI_ERRORES := MI_ERRORES ||
                'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO || ']: ' ||
                'El elemento no existe en INVENTARIO o está inactivo.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        IF NVL(MI_TIENE_MOV, 0) <> -1 THEN
            MI_ERRORES := MI_ERRORES ||
                'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO || ']: ' ||
                'El elemento no tiene marcada la opción "Tiene Movimiento" en INVENTARIO.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        BEGIN
            SELECT COUNT(1),
                   MAX(D.VALOR),
                   MAX(D.NIIF_VIDA_UTIL),
                   MAX(DP.NIIF_VLRLIBROS)
              INTO MI_PLACA_COUNT,
                   MI_VALOR_ADQ,
                   MI_NIIF_VIDA_UTIL,
                   MI_NIIF_VLRLIBROS_DEP
              FROM DEVOLUTIVO D
              LEFT JOIN DEPRECIAR DP
                     ON D.COMPANIA = DP.COMPANIA
                    AND D.ELEMENTO = DP.ELEMENTO
                    AND D.SERIE    = DP.SERIE
             WHERE D.COMPANIA      = UN_COMPANIA
               AND D.ELEMENTO      = MI_CODIGO_ELEMENTO
               AND D.SERIE         = MI_PLACA_NUM
               AND D.PLACAANULADA  = 0
               AND (
                     (DP.COMPANIA IS NOT NULL AND DP.NIIF_VLRACUMULADO = 0)
                     OR
                     (DP.COMPANIA IS NULL
                      AND D.NIIF_VIDA_UTIL >  0)
                   );
        EXCEPTION
            WHEN OTHERS THEN
                MI_ERRORES := MI_ERRORES ||
                    'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO ||
                    ' / Placa: ' || MI_PLACA_NUM || ']: ' ||
                    'Error al consultar DEVOLUTIVO/DEPRECIAR: ' || SQLERRM || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END;

        IF MI_PLACA_COUNT = 0 THEN
            MI_ERRORES := MI_ERRORES ||
                'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO ||
                ' / Placa: ' || MI_PLACA_NUM || ']: ' ||
                'La placa no existe, está anulada, ya tiene depreciación NIIF calculada ' ||
                'o no cumple condiciones (APLICA_NIIF / NIIF_VIDA_UTIL).' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        IF MI_DEP_ACUM_NUM > NVL(MI_VALOR_ADQ, 0) THEN
            MI_ERRORES := MI_ERRORES ||
                'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO ||
                ' / Placa: ' || MI_PLACA_NUM || ']: ' ||
                'DEPRECIACION_ACUMULADA (' || MI_DEP_ACUM_NUM ||
                ') supera el valor de adquisición del bien (' ||
                NVL(MI_VALOR_ADQ, 0) || ').' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;
        
        BEGIN
             SELECT COUNT(*),
                   SUM(CASE WHEN NIIF_VLRACUMULADO = 0 THEN 1 ELSE 0 END)
              INTO MI_EXISTE_PERIODO,
                   MI_DEP_PERIODO_CERO
              FROM DEPRECIAR
             WHERE COMPANIA = UN_COMPANIA
               AND SERIE    = MI_PLACA_NUM
               AND ELEMENTO = MI_CODIGO_ELEMENTO
               AND MES      = TO_NUMBER(MI_MES_PERIODO)
               AND ANO      = TO_NUMBER(MI_ANIO_PERIODO);
        EXCEPTION
            WHEN OTHERS THEN
                MI_ERRORES := MI_ERRORES ||
                    'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO ||
                    ' / Placa: ' || MI_PLACA_NUM || ']: ' ||
                    'Error al validar periodo en DEPRECIAR: ' || SQLERRM || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END;
        
         IF MI_EXISTE_PERIODO > 0 AND NVL(MI_DEP_PERIODO_CERO,0) = 0 THEN
            MI_ERRORES := MI_ERRORES ||
                'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO ||
                ' / Placa: ' || MI_PLACA_NUM || ']: ' ||
                'Ya existe una depreciación calculada para esta placa en el periodo ' ||
                MI_MES_PERIODO || '/' || MI_ANIO_PERIODO || '. No se permite la carga.' || CHR(10);
            MI_OMITIDOS := MI_OMITIDOS + 1;
            CONTINUE;
        END IF;

        MI_VALOR_LIBROS_CALC := NVL(MI_VALOR_ADQ, 0) - MI_DEP_ACUM_NUM;

        BEGIN
            SELECT COUNT(1)
              INTO MI_EXISTE_DEP_ACUM
              FROM DEPRECIACION_ACUMULADA
             WHERE COMPANIA = UN_COMPANIA
               AND ELEMENTO = MI_CODIGO_ELEMENTO
               AND SERIE    = MI_PLACA_NUM;
        EXCEPTION
            WHEN OTHERS THEN
                MI_ERRORES := MI_ERRORES ||
                    'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO ||
                    ' / Placa: ' || MI_PLACA_NUM || ']: ' ||
                    'Error al consultar DEPRECIACION_ACUMULADA: ' || SQLERRM || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END;

        BEGIN
            SELECT COUNT(1)
              INTO MI_EXISTE_DEPRECIAR
              FROM DEPRECIAR
             WHERE COMPANIA = UN_COMPANIA
               AND ELEMENTO = MI_CODIGO_ELEMENTO
               AND SERIE    = MI_PLACA_NUM
               AND ANO      = TO_NUMBER(MI_ANIO_PERIODO)
               AND MES      = TO_NUMBER(MI_MES_PERIODO);
        EXCEPTION
            WHEN OTHERS THEN
                MI_ERRORES := MI_ERRORES ||
                    'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO ||
                    ' / Placa: ' || MI_PLACA_NUM || ']: ' ||
                    'Error al consultar DEPRECIAR: ' || SQLERRM || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END;

        BEGIN
            IF MI_EXISTE_DEP_ACUM = 0 THEN
               MI_TABLA := 'DEPRECIACION_ACUMULADA';
               MI_CAMPOS := 'COMPANIA,
                              ELEMENTO,
                              SERIE,
                              PERIODO,
                              VALOR_ACUMULADO,
                              VALOR_LIBROS,
                              NIIF_VLRLIBROS,
                              OBSERVACIONES,
                              DATE_CREATED,
                              CREATED_BY';
               MI_VALORES := ' '''|| UN_COMPANIA || ''',
                              '''|| MI_CODIGO_ELEMENTO || ''',
                              '''|| MI_PLACA_NUM || ''',
                              '''|| MI_PERIODO_TS || ''',
                              '''|| MI_DEP_ACUM_NUM || ''',
                              '''|| MI_VALOR_LIBROS_CALC || ''',
                              '''|| NVL(MI_NIIF_VLRLIBROS_DEP, 0) || ''',
                              '''|| MI_OBSERVACIONES || ''',
                              SYSDATE,
                              '''|| UN_USUARIO || ''' ';
                            
               MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA
                                           ,UN_ACCION    => 'I'
                                           ,UN_CAMPOS    => MI_CAMPOS
                                           ,UN_VALORES   => MI_VALORES);
                MI_INSERTADOS  := MI_INSERTADOS + 1;
                MI_ACCION_PROC := 'I';
                  MI_ERRORES := MI_ERRORES ||
                    'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO ||
                    ' / Placa: ' || MI_PLACA_NUM || ']: ' ||
                    'Registro insertado correctamente en DEPRECIACION_ACUMULADA.' || CHR(10);

            ELSE
                MI_ERRORES := MI_ERRORES ||
                    'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO ||
                    ' / Placa: ' || MI_PLACA_NUM || ']: ' ||
                    'Ya existe un registro en DEPRECIACION_ACUMULADA para esta placa. ' ||
                    'No se realizó ninguna acción.' || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
            END IF;
        EXCEPTION
            WHEN OTHERS THEN
                MI_ERRORES := MI_ERRORES ||
                    'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO ||
                    ' / Placa: ' || MI_PLACA_NUM || ']: ' ||
                    'Error al operar en DEPRECIACION_ACUMULADA: ' || SQLERRM || CHR(10);
                MI_OMITIDOS := MI_OMITIDOS + 1;
                CONTINUE;
        END;
        
        IF MI_EXISTE_DEPRECIAR = 0 THEN
            PCK_ALMACEN_COM5.PR_DEPRECIACION_INICIAL(UN_COMPANIA, MI_CODIGO_ELEMENTO, MI_PLACA_NUM, MI_MES_PERIODO, MI_ANIO_PERIODO, UN_USUARIO);
        END IF;
            BEGIN
                PCK_ALMACEN_COM5.PR_DEPRECIACION_ACUMULADA(
                    UN_COMPANIA => UN_COMPANIA,
                    UN_ELEMENTO => MI_CODIGO_ELEMENTO,
                    UN_SERIE    => TO_CHAR(MI_PLACA_NUM),
                    UN_MES      => MI_MES_PERIODO,
                    UN_ANIO     => MI_ANIO_PERIODO,
                    UN_ACCION   => MI_ACCION_PROC,
                    UN_USUARIO  => UN_USUARIO
                );
            EXCEPTION
                WHEN OTHERS THEN
                    MI_ERRORES := MI_ERRORES ||
                        'FILA ' || CONTADOR || ' [Elemento: ' || MI_CODIGO_ELEMENTO ||
                        ' / Placa: ' || MI_PLACA_NUM || ']: ' ||
                        'Error al propagar a DEPRECIAR/DEVOLUTIVO: ' || SQLERRM || CHR(10) ||
                        '  >> Registro en DEPRECIACION_ACUMULADA OK. ' ||
                        'Requiere revisión manual en DEPRECIAR/DEVOLUTIVO.' || CHR(10);
            END;

    END LOOP DATOSDEPRECIACION;

    MI_MENSAJE :=
        'PROCESADOS:   ' || CONTADOR        || CHR(10) ||
        'INSERTADOS:   ' || MI_INSERTADOS   || CHR(10) ||
        'OMITIDOS:     ' || MI_OMITIDOS     || CHR(10);

    IF MI_ERRORES IS NOT NULL THEN
        MI_MENSAJE := MI_MENSAJE ||
            CHR(10) || '--- ALERTAS / ERRORES ---' || CHR(10) ||
            MI_ERRORES;
    END IF;

    RETURN MI_MENSAJE;

EXCEPTION
    WHEN OTHERS THEN
        RETURN 'ERROR_GENERAL|Fila ' || CONTADOR || ' - ' || SQLERRM;

END FC_CARGAR_DEP_NOCALCULADA;

FUNCTION FC_CREARMOVCOMPANIAD 
(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOMOVIMIENTO     IN MOVIMIENTO.TIPOMOVIMIENTO%TYPE,
  UN_MOVIMIENTO         IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_ANIO               IN PCK_SUBTIPOS.TI_ANIO,
  UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB
AS
    MI_RETORNO            CLOB;
    MI_COMPANIA_DES       PCK_SUBTIPOS.TI_COMPANIA;
    MI_MOVIMIENTO_DES     MOVIMIENTO.TIPOMOVIMIENTO%TYPE;
    MI_DEPENDENCIA        MOVIMIENTO.DEPENDENCIA_ORIGEN%TYPE;
    MI_RESPONSABLE        MOVIMIENTO.RESPONSABLE_ORIGEN%TYPE;
    MI_SUCURSALRES        MOVIMIENTO.SUCURSAL_RESORIGEN%TYPE;
    MI_DESCRIPCION        MOVIMIENTO.DESCRIPCION%TYPE;
    MI_FECHA              MOVIMIENTO.FECHA%TYPE;
    MI_CENTROCOSTO        MOVIMIENTO.CENTRODECOSTO%TYPE;
    MI_FUENTE_RECURSO     MOVIMIENTO.FUENTEDERECURSO%TYPE;
    MI_AUXILIAR           MOVIMIENTO.AUXILIAR%TYPE;
    MI_REFERENCIA         MOVIMIENTO.REFERENCIA%TYPE;
    MI_NUMEROCOMPR        MOVIMIENTO.NUMERO%TYPE;
    MI_CONSECUTIVO        D_MOVIMIENTO.CODIGO%TYPE := 0;
    MI_EXISTE             NUMBER;
    MI_EXISTENCIA         D_MOVIMIENTO.CANTIDAD%TYPE := 0;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_PARAMETRO          VARCHAR2(320 CHAR);

BEGIN

    MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                          ,UN_NOMBRE    =>'ALMACEN INTERCOMPANIAS'
                                          ,UN_MODULO    => -1
                                          ,UN_FECHA_PAR => SYSDATE);
                                          
    IF MI_PARAMETRO = 'SI' THEN                                          

    SELECT DEPENDENCIA_DESTINO,
           RESPONSABLE_DESTINO,
           SUCURSAL_RESDESTINO,
           DESCRIPCION,
           FECHA,
           FUENTEDERECURSO,
           REFERENCIA,
           AUXILIAR,
           CENTRODECOSTO
      INTO MI_DEPENDENCIA,
           MI_RESPONSABLE,
           MI_SUCURSALRES,
           MI_DESCRIPCION,
           MI_FECHA,
           MI_FUENTE_RECURSO,
           MI_REFERENCIA,
           MI_AUXILIAR,
           MI_CENTROCOSTO
      FROM MOVIMIENTO
     WHERE COMPANIA = UN_COMPANIA
       AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
       AND NUMERO = UN_MOVIMIENTO;

    SELECT COMP_DESTINO,
           MOV_DESTINO
      INTO MI_COMPANIA_DES,
           MI_MOVIMIENTO_DES
      FROM DEPENDENCIA
     WHERE COMPANIA = UN_COMPANIA
       AND CODIGO = MI_DEPENDENCIA;
       
    IF MI_COMPANIA_DES IS NULL OR MI_MOVIMIENTO_DES IS NULL THEN
        MI_RETORNO := 'La dependencia ' || MI_DEPENDENCIA || ' no tiene configurada compañía destino o movimiento destino';
        RETURN 'ERROR,' || MI_RETORNO;
    END IF;

    SELECT COUNT(*)
        INTO MI_EXISTE
    FROM CENTRO_COSTO
    WHERE COMPANIA = MI_COMPANIA_DES
      AND ANO = UN_ANIO
      AND CODIGO = MI_CENTROCOSTO;

    IF MI_EXISTE = 0 THEN
       MI_RETORNO := MI_RETORNO || CHR(10) || 'Centro de costo ' || MI_CENTROCOSTO || ' no existe.';
    END IF;

    SELECT COUNT(*)
        INTO MI_EXISTE
    FROM FUENTE_RECURSOS
    WHERE COMPANIA = MI_COMPANIA_DES
      AND ANO = UN_ANIO
      AND CODIGO = MI_FUENTE_RECURSO;

    IF MI_EXISTE = 0 THEN
       MI_RETORNO := MI_RETORNO || CHR(10) || 'Fuente ' || MI_FUENTE_RECURSO || ' no existe.';
    END IF;

    SELECT COUNT(*)
        INTO MI_EXISTE
    FROM AUXILIAR
    WHERE COMPANIA = MI_COMPANIA_DES
      AND ANO = UN_ANIO
      AND CODIGO = MI_AUXILIAR;

    IF MI_EXISTE = 0 THEN
       MI_RETORNO := MI_RETORNO || CHR(10) || 'Auxiliar ' || MI_AUXILIAR || ' no existe.';
    END IF;

    SELECT COUNT(*)
    INTO MI_EXISTE
    FROM REFERENCIA
    WHERE COMPANIA = MI_COMPANIA_DES
      AND ANO = UN_ANIO
      AND CODIGO = MI_REFERENCIA;

    IF MI_EXISTE = 0 THEN
       MI_RETORNO := MI_RETORNO || CHR(10) || 'Referencia ' || MI_REFERENCIA || ' no existe.';
    END IF; 
    
    FOR RS IN (
        SELECT DISTINCT ELEMENTO
        FROM D_MOVIMIENTO
        WHERE COMPANIA = UN_COMPANIA
          AND TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
          AND MOVIMIENTO = UN_MOVIMIENTO
    )
    LOOP

        SELECT COUNT(*)
            INTO MI_EXISTE
        FROM INVENTARIO
        WHERE COMPANIA = MI_COMPANIA_DES
          AND CODIGOELEMENTO = RS.ELEMENTO;

        IF MI_EXISTE = 0 THEN
            MI_RETORNO := MI_RETORNO || CHR(10) || 'Elemento ' || RS.ELEMENTO || ' no existe ';
        END IF;

    END LOOP;
    
    IF MI_RETORNO IS NOT NULL THEN
       MI_RETORNO := MI_RETORNO || CHR(10) || 'en compañia destino ' || MI_COMPANIA_DES;
       RETURN 'ERROR,' || MI_RETORNO;
    END IF;

    MI_NUMEROCOMPR := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                          UN_TABLA    => 'MOVIMIENTO',
                          UN_CRITERIO => 'COMPANIA='''||MI_COMPANIA_DES||''' AND TIPOMOVIMIENTO='''||MI_MOVIMIENTO_DES ||''' AND ANO = ' || UN_ANIO ||'',
                          UN_CAMPO    => 'NUMERO',
                          UN_INICIAL  => ''||UN_ANIO || '000001'
                       );
                       
    --Creación del encabezado
    BEGIN
          BEGIN
            MI_CAMPOS:=
                'COMPANIA
                ,ANO
                ,TIPOMOVIMIENTO
                ,NUMERO
                ,FECHA
                ,RESPONSABLE_DESTINO
                ,SUCURSAL_RESDESTINO
                ,DEPENDENCIA_DESTINO
                ,DEPENDENCIA_ORIGEN
                ,BODEGA_ORIGEN
                ,BODEGA_DESTINO
                ,DESCRIPCION
                ,CLASE_BODEGA_ORIGEN
                ,CLASE_BODEGA_DESTINO
                ,REGISTRADO
                ,CREATED_BY
                ,DATE_CREATED
                ';

            MI_VALORES:=
                ''''||MI_COMPANIA_DES||'''
                ,'  ||UN_ANIO||'
                ,'''||MI_MOVIMIENTO_DES||'''
                ,'  ||MI_NUMEROCOMPR||'
                ,'''||MI_FECHA ||'''
                ,'''||MI_RESPONSABLE||'''
                ,'''||MI_SUCURSALRES||'''
                ,''000000000000''
                ,''99999''
                ,10
                ,20
                ,'''||MI_DESCRIPCION||'''
                ,10
                ,20
                ,-1
                ,'''||UN_USUARIO||'''
                ,SYSDATE
                ';
				
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'MOVIMIENTO' ,UN_ACCION => 'I' ,UN_CAMPOS => MI_CAMPOS ,UN_VALORES => MI_VALORES);
            EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            END;
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_INSERT_DMOVIMIENTO );
    END;
    --Creación del detalle        
    
    FOR RS IN (SELECT D_MOVIMIENTO.COMPANIA, D_MOVIMIENTO.TIPOMOVIMIENTO, D_MOVIMIENTO.MOVIMIENTO, D_MOVIMIENTO.CODIGO, D_MOVIMIENTO.ELEMENTO,
                   D_MOVIMIENTO.CANTIDAD, D_MOVIMIENTO.VALORUNITARIO, D_MOVIMIENTO.VALORTOTAL,  
                   D_MOVIMIENTO.VALORBASE, D_MOVIMIENTO.VALORTOTALCONIVA, D_MOVIMIENTO.FECHA, D_MOVIMIENTO.VLRUNITARIO_ANTESIVA, D_MOVIMIENTO.APLICANIIF,
                   MOVIMIENTO.BODEGA_ORIGEN, MOVIMIENTO.BODEGA_DESTINO, MOVIMIENTO.FUENTEDERECURSO, MOVIMIENTO.REFERENCIA,
                   MOVIMIENTO.AUXILIAR, D_MOVIMIENTO.CODIGOPROYECTO, MOVIMIENTO.CENTRODECOSTO, D_MOVIMIENTO.LOTE 
               FROM D_MOVIMIENTO
               INNER JOIN MOVIMIENTO
                   ON D_MOVIMIENTO.COMPANIA = MOVIMIENTO.COMPANIA
                   AND D_MOVIMIENTO.TIPOMOVIMIENTO = MOVIMIENTO.TIPOMOVIMIENTO
                   AND D_MOVIMIENTO.MOVIMIENTO = MOVIMIENTO.NUMERO
               WHERE D_MOVIMIENTO.COMPANIA = UN_COMPANIA 
                   AND D_MOVIMIENTO.TIPOMOVIMIENTO = UN_TIPOMOVIMIENTO
                   AND D_MOVIMIENTO.MOVIMIENTO = UN_MOVIMIENTO)
               
    LOOP
        MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
        
        BEGIN
          BEGIN
            MI_CAMPOS:=
                'COMPANIA
                ,TIPOMOVIMIENTO
                ,MOVIMIENTO
                ,CODIGO
                ,ELEMENTO
                ,FECHA
                ,CANTIDAD
                ,VALORUNITARIO
                ,VALORTOTAL
                ,VALORBASE
                ,VALORTOTALCONIVA
                ,VLRUNITARIO_ANTESIVA
                ,APLICANIIF
                ,CENTRODECOSTO
                ,FUENTEDERECURSO
                ,REFERENCIA
                ,AUXILIAR
                ,CODIGOPROYECTO
                ,LOTE
                ,TIPOMOVASOCIADO
                ,MOVASOCIADO
                ,CREATED_BY
                ,DATE_CREATED
                ';

            MI_VALORES:=
                ''''||MI_COMPANIA_DES||'''
                ,'''||MI_MOVIMIENTO_DES||'''
                ,'  ||MI_NUMEROCOMPR||'
                ,'  ||MI_CONSECUTIVO||'
                ,'''||RS.ELEMENTO||'''
                ,'''||MI_FECHA ||'''
                ,'  ||RS.CANTIDAD||'
                ,'  ||RS.VALORUNITARIO||'
                ,'  ||RS.VALORTOTAL||'
                ,'  ||RS.VALORBASE||'
                ,'  ||RS.VALORTOTALCONIVA||'
                ,'  ||RS.VLRUNITARIO_ANTESIVA||'
                ,'  ||RS.APLICANIIF||'
                ,'''||RS.CENTRODECOSTO||'''
                ,'''||RS.FUENTEDERECURSO||'''
                ,'''||RS.REFERENCIA||'''
                ,'''||RS.AUXILIAR||'''
                ,'''||RS.CODIGOPROYECTO||'''
                ,'''||RS.LOTE||'''
                ,'''||UN_TIPOMOVIMIENTO||'''
                ,'''||UN_MOVIMIENTO||'''
                ,'''||UN_USUARIO||'''
                ,SYSDATE
                ';
				
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'D_MOVIMIENTO' ,UN_ACCION => 'I' ,UN_CAMPOS => MI_CAMPOS ,UN_VALORES => MI_VALORES);
            EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            END;
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_ALMACEN_INSERT_DMOVIMIENTO );
        END;
        
        PCK_ALMACEN_COM5.PR_SALDOS_INVENTBODEGA(UN_COMPANIA         => MI_COMPANIA_DES,
                                               UN_TIPOMOVIMIENTO    => MI_MOVIMIENTO_DES,
                                               UN_MOVIMIENTO        => MI_NUMEROCOMPR,
                                               UN_ELEMENTO          => RS.ELEMENTO,
                                               UN_BODEGAO           => '10',
                                               UN_BODEGAD           => RS.BODEGA_ORIGEN,
                                               UN_FUENTER           => RS.FUENTEDERECURSO,
                                               UN_REFERENCIA        => RS.REFERENCIA,
                                               UN_AUXILIAR          => RS.AUXILIAR,
                                               UN_PROYECTO          => RS.CODIGOPROYECTO,
                                               UN_CCOSTO            => RS.CENTRODECOSTO,
                                               UN_LOTE              => RS.LOTE,
                                               UN_CANTIDAD          => RS.CANTIDAD,
                                               UN_CANTANTERIOR      => 0,
                                               UN_ACCION            => 'INSERT',
                                               UN_USUARIO           => UN_USUARIO);
                                               
        PCK_ALMACEN_COM2.PR_KARDEXAUXINVBOD(UN_COMPANIA => MI_COMPANIA_DES, UN_ELEMENTO => RS.ELEMENTO);
        
        BEGIN
            SELECT CANTIDAD 
                INTO MI_EXISTENCIA
           FROM INVENTARIO_BODEGA
           WHERE COMPANIA = MI_COMPANIA_DES
               AND ELEMENTO =  RS.ELEMENTO
               AND BODEGA = RS.BODEGA_ORIGEN
               AND FUENTEDERECURSO = RS.FUENTEDERECURSO
               AND REFERENCIA = RS.REFERENCIA
               AND AUXILIAR = RS.AUXILIAR
               AND PROYECTO = RS.CODIGOPROYECTO
               AND CENTRODECOSTO = RS.CENTRODECOSTO
               AND LOTE = RS.LOTE;
        END;       
            
        BEGIN
                MI_CAMPOS := 'SALDOCANT = '||MI_EXISTENCIA||'
                            , SALDOKARDEX = '||MI_EXISTENCIA||' ';
                       
                MI_CONDICION := 'COMPANIA          = '''||MI_COMPANIA_DES  ||'''
                              AND TIPOMOVIMIENTO    = '''||MI_MOVIMIENTO_DES||'''
                              AND MOVIMIENTO        = '  ||MI_NUMEROCOMPR   || '
                              AND CODIGO            = '  ||MI_CONSECUTIVO   || '';
                         
               BEGIN
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'D_MOVIMIENTO'
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION);
        
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_ALMACEN_ACTUALIZA_REGIST);
            END; 
    
    END LOOP;
    
    MI_RETORNO := 'Se creo la entrada ' || MI_MOVIMIENTO_DES || '-' || MI_NUMEROCOMPR || ' en la compañia ' || MI_COMPANIA_DES;
    RETURN 'OK,' || MI_RETORNO;
    
    ELSE 
        RETURN '';
    END IF;
  
END FC_CREARMOVCOMPANIAD;

END PCK_ALMACEN_COM5;