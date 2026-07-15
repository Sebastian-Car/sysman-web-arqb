create or replace PACKAGE BODY               PCK_NOMINA_COM8 AS

--1
 PROCEDURE PR_ALERTA_INICIAL
 /*
    NAME              : PR_ALERTA_INICIAL   ACCES => AlertaINICIAL()
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 16/01/2018
    TIME              : 11:41 PM
    DESCRIPTION       : FUNCION ENCARGADA DE GUARDAR LOS ERRORES CUANDO SE REVISAN LAS CUENTAS CONTABLES DESDE NOMINA 
    PARAMETROS        : UN_COMPANIA    => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_ANIO        => ANO DE INGRESO AL MODULO DE NOMINA
                        UN_MES         => MES DE INGRESO AL MODULO DE NOMINA 
                        UN_PERIODO     => PERIODO DE INGRESO AL MODULO DE NOMINA
                        UN_PROCESO     => PROCESO DE INGRESO AL MODULO DE NOMINA
                        UN_USUARIO     => USUARIO DE INGRESO AL MODULO DE NOMINA
                        UN_DESCRIPCION => LA INCONSISTENCIA ENCONTRADA AL REALIZAR EL PROCESO DE PRENONIMA

  */
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ID_DE_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
  UN_MES            IN PCK_SUBTIPOS.TI_MES,
  UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
  UN_PERIODO        IN PCK_SUBTIPOS.TI_PERIODO,
  UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO,
  UN_DESCRIPCION    IN PCK_SUBTIPOS.TI_STRSQL
)
AS 
  MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
  MI_RTA              PCK_SUBTIPOS.TI_ENTERO;   
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;  
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;  
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;  
  MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CONSECUTIVO      PCK_SUBTIPOS.TI_STRSQL;
  MI_CRITERIO         PCK_SUBTIPOS.TI_STRSQL;


BEGIN
 MI_TABLA    := 'ERRORESINICIALES';
 MI_CRITERIO := 'COMPANIA        = '''||UN_COMPANIA||''''; 

 MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO (UN_TABLA    => 'ERRORESINICIALES',
                                                     UN_CRITERIO => MI_CRITERIO,
                                                     UN_CAMPO    => 'ERROR');
 IF  LENGTH(MI_CONSECUTIVO) < 6 THEN 
    MI_CONSECUTIVO := LPAD(MI_CONSECUTIVO,6,0);
 END IF;

 MI_CAMPOS := ' COMPANIA,         
                ID_DE_EMPLEADO,
                ERROR,            
                ANOE,              
                MESE,              
                PERIODOE,          
                DESCRIPCION,      
                CREATED_BY,       
                DATE_CREATED';

 MI_VALORES := ''''||UN_COMPANIA||''',
               '||UN_ID_DE_EMPLEADO||',
               '''||MI_CONSECUTIVO||''',
               '||UN_ANIO||',
               '||UN_MES||',
               '||UN_PERIODO||',
               '''||UN_DESCRIPCION||''',
               '''||UN_USUARIO||''',
               SYSDATE';

 BEGIN 
    BEGIN 
         MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA   =>  MI_TABLA, 
                                       UN_ACCION  =>  'I',
                                       UN_CAMPOS  =>  MI_CAMPOS, 
                                       UN_VALORES =>  MI_VALORES);

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   MI_MSGERROR(0).CLAVE:='ID_DE_EMPLEADO';
                   MI_MSGERROR(0).VALOR:= UN_ID_DE_EMPLEADO;

                   MI_MSGERROR(1).CLAVE:='DESCRIPCION';
                   MI_MSGERROR(1).VALOR:= UN_DESCRIPCION;

                   MI_MSGERROR(2).CLAVE:='ANIO';
                   MI_MSGERROR(2).VALOR:= UN_ANIO;

                   MI_MSGERROR(3).CLAVE:='MES';
                   MI_MSGERROR(3).VALOR:= UN_MES;

                   MI_MSGERROR(4).CLAVE:='PERIODO';
                   MI_MSGERROR(4).VALOR:= UN_PERIODO;
                   RAISE PCK_EXCEPCIONES.EXC_SYSMANSF;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_SYSMANSF THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_NOMINA_REGISTRAR_ERROR
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_MSGERROR);
 END;
END PR_ALERTA_INICIAL;

--2

PROCEDURE PR_ACUMNOVEDADES
/*
    NAME              : PR_ACUMNOVEDADES   ACCES => AcumNovedades()
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 16/01/2018
    TIME              : 11:41 PM
    DESCRIPTION       : FUNCION ENCARGADA DE GUARDAR LOS ERRORES CUANDO SE REVISAN LAS CUENTAS CONTABLES DESDE NOMINA 
    PARAMETROS        : UN_COMPANIA    => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_ANIO        => ANO DE INGRESO AL MODULO DE NOMINA
                        UN_MES         => MES DE INGRESO AL MODULO DE NOMINA 
                        UN_PERIODO     => PERIODO DE INGRESO AL MODULO DE NOMINA
                        UN_PROCESO     => PROCESO DE INGRESO AL MODULO DE NOMINA
                        UN_USUARIO     => USUARIO DE INGRESO AL MODULO DE NOMINA
                        UN_DESCRIPCION => LA INCONSISTENCIA ENCONTRADA AL REALIZAR EL PROCESO DE PRENONIMA

  */
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO1          IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES1           IN PCK_SUBTIPOS.TI_MES,    
  UN_PERIODO1       IN PCK_SUBTIPOS.TI_DIA,    
  UN_ANIO2          IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES2           IN PCK_SUBTIPOS.TI_MES,    
  UN_PERIODO2       IN PCK_SUBTIPOS.TI_DIA,
  UN_ID_DE_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
  UN_PROCESO        IN PCK_SUBTIPOS.TI_ID_DE_PROCESO
)

AS 
    MI_PROCESO     PCK_SUBTIPOS.TI_ID_DE_PROCESO  DEFAULT 0;
    MI_ACUMULADO   NUMBER := 0;     
BEGIN
  PCK_NOMINA.CNAN.DELETE;
  --(APINEDA:02/10/2019)-Se agregan condiciones a WHERE con el fin de tomar novedades configuradas en Proceso retroactivo.
  IF PCK_NOMINA.GL_NOMINARETROACTIVO THEN
    MI_PROCESO := 10;
    MI_ACUMULADO := -1;
  ELSE
    MI_PROCESO := 1;   
  END IF;    
  FOR RDS IN (SELECT DISTINCT NOVEDADES.COMPANIA,
                    SUM(NOVEDADES.VALOR) AS SUMACONCEPTO,
                    NOVEDADES.ID_DE_CONCEPTO
               FROM NOVEDADES
              INNER JOIN PERIODOS
                 ON NOVEDADES.COMPANIA      = PERIODOS.COMPANIA
                AND NOVEDADES.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                AND NOVEDADES.ANO           = PERIODOS.ANO
                AND NOVEDADES.MES           = PERIODOS.MES
                AND NOVEDADES.PERIODO       = PERIODOS.PERIODO
              WHERE NOVEDADES.COMPANIA        = UN_COMPANIA
                AND NOVEDADES.ANO|| NOVEDADES.MES|| NOVEDADES.PERIODO BETWEEN UN_ANIO1||UN_MES1||UN_PERIODO1 AND UN_ANIO2||UN_MES2||UN_PERIODO2
                AND NOVEDADES.ID_DE_EMPLEADO  = UN_ID_DE_EMPLEADO
                AND NOVEDADES.ID_DE_PROCESO   = MI_PROCESO
                AND PERIODOS.ACUMULADO    NOT IN (MI_ACUMULADO)
              GROUP BY NOVEDADES.COMPANIA,
                    NOVEDADES.ID_DE_CONCEPTO
              ORDER BY NOVEDADES.ID_DE_CONCEPTO)
  LOOP 

    IF RDS.ID_DE_CONCEPTO > 0 AND  RDS.ID_DE_CONCEPTO <= 1000 THEN 
        PCK_NOMINA.CNAN(RDS.ID_DE_CONCEPTO) := RDS.SUMACONCEPTO;
    END IF;

  END LOOP;
END PR_ACUMNOVEDADES;

--3

PROCEDURE PR_LOGICA_IDENTIFICAR_NOV
/*
    NAME              : PR_ALERTA_INICIAL   ACCES => AlertaINICIAL()
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 17/01/2018
    TIME              : 10:46 A
    DESCRIPTION       : FUNCION ENCARGADA 
    PARAMETROS        : UN_COMPANIA    => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_ANIO        => ANO DE INGRESO AL MODULO DE NOMINA
                        UN_MES         => MES DE INGRESO AL MODULO DE NOMINA 
                        UN_PERIODO     => PERIODO DE INGRESO AL MODULO DE NOMINA
                        UN_PROCESO     => PROCESO DE INGRESO AL MODULO DE NOMINA
                        UN_USUARIO     => USUARIO DE INGRESO AL MODULO DE NOMINA
                        UN_DESCRIPCION => LA INCONSISTENCIA ENCONTRADA AL REALIZAR EL PROCESO DE PRENONIMA

  */
(
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ID_DE_EMPLEADO   IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
  UN_TIPONOVEDAD      IN VARCHAR2,
  UN_DIASINC          IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
  UN_NOMBRE           IN PERSONAL.NOMBRECOMPLETO%TYPE,
  UN_NUMERO_DOC       IN PERSONAL.NUMERO_DCTO%TYPE,
  UN_CONCEPTO         IN VARCHAR2,  
  UN_MES              IN PCK_SUBTIPOS.TI_MES,
  UN_ANIO             IN PCK_SUBTIPOS.TI_ANIO,
  UN_PERIODO          IN PCK_SUBTIPOS.TI_PERIODO,
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)
AS 

MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
     MI_REEMPLAZOS(0).CLAVE :='TIPONOVEDAD';
     MI_REEMPLAZOS(0).VALOR := UN_TIPONOVEDAD;       
     MI_REEMPLAZOS(1).CLAVE := 'EMPLEADO';
     MI_REEMPLAZOS(1).VALOR := UN_ID_DE_EMPLEADO;                                         
     MI_REEMPLAZOS(2).CLAVE := 'DIASINC';
     MI_REEMPLAZOS(2).VALOR := UN_DIASINC;
     MI_REEMPLAZOS(3).CLAVE := 'DIASNOV';
     MI_REEMPLAZOS(3).VALOR := LPAD(PCK_NOMINA.FC_CNAN(UN_CONCEPTO), 2);
     MI_REEMPLAZOS(4).CLAVE := 'NOMBRE';
     MI_REEMPLAZOS(4).VALOR := UN_NOMBRE ;
     MI_REEMPLAZOS(5).CLAVE := 'CEDULA';
     MI_REEMPLAZOS(5).VALOR := UN_NUMERO_DOC;
     MI_REEMPLAZOS(6).CLAVE := 'EMPLEADO';
     MI_REEMPLAZOS(6).VALOR := UN_ID_DE_EMPLEADO;

  IF PCK_NOMINA.FC_CNINC(UN_CONCEPTO) <> PCK_NOMINA.FC_CNAN(UN_CONCEPTO) THEN 
    MI_REEMPLAZOS(6).CLAVE := 'CONCEPTO';
    MI_REEMPLAZOS(6).VALOR := UN_CONCEPTO;

    IF UN_DIASINC <> LPAD(PCK_NOMINA.FC_CNAN(UN_CONCEPTO), 2) THEN
      PCK_NOMINA_COM8.PR_ALERTA_INICIAL(UN_COMPANIA       => UN_COMPANIA,
                                        UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                        UN_MES            => UN_MES,
                                        UN_ANIO           => UN_ANIO,
                                        UN_PERIODO        => UN_PERIODO,
                                        UN_USUARIO        => UN_USUARIO,
                                        UN_DESCRIPCION    => PCK_ERR_MSG.FC_MENSAJE(  UN_MENSAJE_COD => 61000226,
                                                                                      UN_REEMPLAZOS  => MI_REEMPLAZOS  ));
    END IF;                                                                                  

   END IF;
END PR_LOGICA_IDENTIFICAR_NOV;

--4
PROCEDURE PR_IDENTIFICARNOVEDADESPERIODO
(
/*
    NAME              : PR_IDENTIFICARNOVEDADESPERIODO   ACCES => identificarnovedadesperiodo()
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 26/02/2018
    TIME              : 09:46 AM
    DESCRIPTION       : FUNCION ENCARGADA DE GUARDAR LOS ERRORES DE VACACIONES O LICENCIAS EN LA TABLA ERRORORESINICIALES
    PARAMETROS        : UN_COMPANIA    => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_ANIO        => ANO DE INGRESO AL MODULO DE NOMINA
                        UN_MES         => MES DE INGRESO AL MODULO DE NOMINA 
                        UN_PERIODO     => PERIODO DE INGRESO AL MODULO DE NOMINA
                        UN_PROCESO     => PROCESO DE INGRESO AL MODULO DE NOMINA
                        UN_USUARIO     => USUARIO DE INGRESO AL MODULO DE NOMINA
                        UN_DESCRIPCION => LA INCONSISTENCIA ENCONTRADA AL REALIZAR EL PROCESO DE PRENONIMA
   @NAME: identificarNovedadesPeriodos                    

  */
	UN_COMPANIA 		    IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_PROCESO 			    IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
	UN_ANO 				      IN PCK_SUBTIPOS.TI_ANIO,
	UN_MES 				      IN PCK_SUBTIPOS.TI_MES,
	UN_PERIODO          IN PCK_SUBTIPOS.TI_PERIODO,
	UN_ID_DE_EMPLEADO 	IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
	UN_USUARIO        	IN PCK_SUBTIPOS.TI_USUARIO
)
	AS
		MI_FECHA_INICIO   DATE;
		MI_FECHA_FINAL    DATE;
		MI_FINICIO     	  DATE;
		MI_FFINAL		      DATE;
		MI_TDIAS          PCK_SUBTIPOS.TI_ENTERO;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_NUMERO_DCTO    PCK_SUBTIPOS.TI_TERCERO;
    MI_NOMBRECOMPLETO VARCHAR2(72 CHAR);
    MI_DIASCALINC PCK_SUBTIPOS.TI_ENTERO_LARGO DEFAULT 0;
    MI_DIASCALLIC PCK_SUBTIPOS.TI_ENTERO_LARGO DEFAULT 0;
    MI_DIASCALVAC PCK_SUBTIPOS.TI_ENTERO_LARGO DEFAULT 0;

	BEGIN 
		MI_FECHA_INICIO  := TO_DATE('01/'||UN_MES||'/'||UN_ANO);
		MI_FECHA_FINAL   := LAST_DAY(TO_DATE('01/'||UN_MES||'/'||UN_ANO));

    SELECT NUMERO_DCTO,NOMBRECOMPLETO 
    INTO MI_NUMERO_DCTO,MI_NOMBRECOMPLETO
    FROM PERSONAL
    WHERE COMPANIA = UN_COMPANIA
      AND ID_DE_EMPLEADO = UN_ID_DE_EMPLEADO;


    FOR RS IN (	SELECT 
                  INCAPACIDADES.COMPANIA,
                  INCAPACIDADES.ID_DE_EMPLEADO,
                  INCAPACIDADES.FECHA_INICIO  F1,
                  INCAPACIDADES.FECHA_FIN     F2,
                  INCAPACIDADES.AUTO,
                  INCAPACIDADES.FECHA_AUTO,
                  INCAPACIDADES.DIAS ,
                  INCAPACIDADES.INCAPACIDAD,
                  TIPO_INCAPACIDAD.ID_DE_CONCEPTO,
                  PERSONAL.NOMBRECOMPLETO,
                  PERSONAL.NUMERO_DCTO
                FROM PERSONAL
                  INNER JOIN INCAPACIDADES
                    ON PERSONAL.COMPANIA        = INCAPACIDADES.COMPANIA
                    AND PERSONAL.ID_DE_EMPLEADO = INCAPACIDADES.ID_DE_EMPLEADO
                  INNER JOIN TIPO_INCAPACIDAD
                    ON INCAPACIDADES.COMPANIA        	= TIPO_INCAPACIDAD.COMPANIA 
                    AND INCAPACIDADES.INCAPACIDAD      	= TIPO_INCAPACIDAD.INCAPACIDAD
                WHERE INCAPACIDADES.COMPANIA   		= 	UN_COMPANIA
                  AND INCAPACIDADES.ID_DE_EMPLEADO=	UN_ID_DE_EMPLEADO
                  AND (	INCAPACIDADES.FECHA_INICIO BETWEEN MI_FECHA_INICIO AND MI_FECHA_FINAL
                      OR INCAPACIDADES.FECHA_FIN BETWEEN MI_FECHA_INICIO AND MI_FECHA_FINAL
                      OR (  INCAPACIDADES.FECHA_INICIO < MI_FECHA_INICIO
                        AND INCAPACIDADES.FECHA_FIN   	> MI_FECHA_FINAL)
                      )
                ORDER BY  	INCAPACIDADES.FECHA_INICIO,
                            INCAPACIDADES.FECHA_FIN)
    LOOP 

      MI_TDIAS := PCK_NOMINA_COM8.FC_RETORNARTOTALDIAS(	UN_FECHA_INICIO	=>	MI_FECHA_INICIO,
                                                        UN_FECHA_FINAL 	=>	MI_FECHA_FINAL,
                                                        UN_FEC_INICIO 	=>	RS.F1,
                                                        UN_FEC_FINAL 	  => 	RS.F2);
      MI_DIASCALINC := MI_DIASCALINC + MI_TDIAS;
      PCK_NOMINA.CNINC(RS.ID_DE_CONCEPTO) :=RS.ID_DE_CONCEPTO+MI_TDIAS;	
    END LOOP;
    PCK_NOMINA_COM8.PR_ACUMNOVEDADES(  UN_COMPANIA       => UN_COMPANIA,
                                       UN_ANIO1          => UN_ANO,
                                       UN_MES1           => UN_MES,    
                                       UN_PERIODO1       => '01',    
                                       UN_ANIO2          => UN_ANO,
                                       UN_MES2           => UN_MES,    
                                       UN_PERIODO2       => '99' ,
                                       UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO ,
                                       UN_PROCESO        => UN_PROCESO
                                    );	
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Incapacidades',
                                                UN_DIASINC        => MI_DIASCALINC,
                                                UN_NOMBRE         =>  MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 336 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO
                                               );	
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Incapacidades',
                                                UN_DIASINC        => MI_DIASCALINC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 350 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO 
                                              );															

    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Incapacidades',
                                                UN_DIASINC        => MI_DIASCALINC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 351 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO
                                              );					
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Incapacidades', 
                                                UN_DIASINC        => MI_DIASCALINC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 352 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO 
                                              );				
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Incapacidades',
                                                UN_DIASINC        => MI_DIASCALINC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 353 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO );				
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Incapacidades',
                                                UN_DIASINC        => MI_DIASCALINC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 354 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO 
                                               );	
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Incapacidades',
                                                UN_DIASINC        => MI_DIASCALINC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 355 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO 
                                               );			
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Incapacidades',
                                                UN_DIASINC        => MI_DIASCALINC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 367 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO 
                                              );	
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Incapacidades',
                                                UN_DIASINC        => MI_DIASCALINC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 368 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO 
                                              );	
    PCK_NOMINA.CNINC.DELETE;	

    FOR RS IN (	SELECT  
                  LICENCIAS.COMPANIA,
                  LICENCIAS.ID_DE_EMPLEADO,
                  LICENCIAS.FECHA_INICIO F1,
                  LICENCIAS.FECHA_FINAL  F2,
                  LICENCIAS.DIAS         DIAS,
                  TIPOS_LICENCIA.ID_DE_CONCEPTO,
                  PERSONAL.NOMBRECOMPLETO,
                  PERSONAL.NUMERO_DCTO
                FROM LICENCIAS
                  LEFT JOIN PERSONAL
                    ON LICENCIAS.COMPANIA      		= PERSONAL.COMPANIA 
                    AND LICENCIAS.ID_DE_EMPLEADO 	= PERSONAL.ID_DE_EMPLEADO
                  LEFT JOIN TIPOS_LICENCIA
                    ON LICENCIAS.COMPANIA         	= TIPOS_LICENCIA.COMPANIA
                    AND LICENCIAS.LICENCIA        	= TIPOS_LICENCIA.LICENCIA
                WHERE LICENCIAS.COMPANIA   =UN_COMPANIA
                  AND LICENCIAS.ID_DE_EMPLEADO=UN_ID_DE_EMPLEADO
                  AND (	  LICENCIAS.FECHA_INICIO BETWEEN MI_FECHA_INICIO AND MI_FECHA_FINAL
                     OR 	LICENCIAS.FECHA_FINAL BETWEEN 	MI_FECHA_INICIO AND MI_FECHA_FINAL
                     OR (LICENCIAS.FECHA_INICIO  <	MI_FECHA_INICIO
                        AND LICENCIAS.FECHA_FINAL   >	MI_FECHA_FINAL)))
    LOOP 
      MI_TDIAS := PCK_NOMINA_COM8.FC_RETORNARTOTALDIAS(	UN_FECHA_INICIO	=>	MI_FECHA_INICIO,
                                                        UN_FECHA_FINAL 	=>	MI_FECHA_FINAL,
                                                        UN_FEC_INICIO 	=>	RS.F1,
                                                        UN_FEC_FINAL 	=> 	RS.F2);
      MI_DIASCALLIC := MI_DIASCALLIC + MI_TDIAS;
      PCK_NOMINA.CNINC(RS.ID_DE_CONCEPTO) :=RS.ID_DE_CONCEPTO+MI_TDIAS;	
    END LOOP;  

    PCK_NOMINA_COM8.PR_ACUMNOVEDADES( 	UN_COMPANIA       => UN_COMPANIA,
                                        UN_ANIO1          => UN_ANO,
                                        UN_MES1           => UN_MES,    
                                        UN_PERIODO1       => '01',    
                                        UN_ANIO2          => UN_ANO,
                                        UN_MES2           => UN_MES,    
                                        UN_PERIODO2       => '99' ,
                                        UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO ,
                                        UN_PROCESO        => UN_PROCESO
                                     );	
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Licencias',
                                                UN_DIASINC        => MI_DIASCALLIC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 339 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO 
                                              );	
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Licencias',
                                                UN_DIASINC        => MI_DIASCALLIC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 356 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO
                                              );	
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Licencias',
                                                UN_DIASINC        => MI_DIASCALLIC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 357 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO
                                              );	
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Licencias',
                                                UN_DIASINC        => MI_DIASCALLIC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 359 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO 
                                              );	
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Licencias',
                                                UN_DIASINC        => MI_DIASCALLIC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 363 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO
                                              );	
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Licencias',
                                                UN_DIASINC        => MI_DIASCALLIC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 364 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO
                                              );	
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Licencias',
                                                UN_DIASINC        => MI_DIASCALLIC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 365 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO 
                                               );	
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Licencias',
                                                UN_DIASINC        => MI_DIASCALLIC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 382 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO 
                                               );	
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Licencias',
                                                UN_DIASINC        => MI_DIASCALLIC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 383 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO 
                                               );	
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Licencias',
                                                UN_DIASINC        => MI_DIASCALLIC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 384 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO 
                                               );	
    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Licencias',
                                                UN_DIASINC        => MI_DIASCALLIC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 390 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO
                                               );	
    PCK_NOMINA.CNINC.DELETE;
    FOR RS IN (	SELECT 
                  VACACIONES.COMPANIA,
                  VACACIONES.ID_DE_EMPLEADO,
                  VACACIONES.INICIO_DISFRUTE F1,
                  VACACIONES.FINAL_DISFRUTE  F2,
                  VACACIONES.DIAS,
                  PERSONAL.NOMBRECOMPLETO,
                  PERSONAL.NUMERO_DCTO 
                FROM VACACIONES
                  INNER JOIN PERSONAL
                    ON VACACIONES.COMPANIA        = PERSONAL.COMPANIA 
                    AND VACACIONES.ID_DE_EMPLEADO   = PERSONAL.ID_DE_EMPLEADO
                WHERE VACACIONES.COMPANIA       	=	UN_COMPANIA
                  AND VACACIONES.ID_DE_EMPLEADO 	= 	UN_ID_DE_EMPLEADO
                  AND (( VACACIONES.FINAL_DISFRUTE BETWEEN MI_FECHA_INICIO AND MI_FECHA_FINAL
                    AND VACACIONES.DIAS         NOT IN(0))
                  OR VACACIONES.INICIO_DISFRUTE BETWEEN MI_FECHA_INICIO AND MI_FECHA_FINAL
                  AND VACACIONES.DIAS       NOT IN(0)
                  AND EXTRACT (DAY  FROM VACACIONES.INICIO_DISFRUTE) NOT IN(31)))
    LOOP 
      MI_TDIAS := PCK_NOMINA_COM8.FC_RETORNARTOTALDIAS(	UN_FECHA_INICIO	=>	MI_FECHA_INICIO,
                                                        UN_FECHA_FINAL 	=>	MI_FECHA_FINAL,
                                                        UN_FEC_INICIO 	=>	RS.F1,
                                                        UN_FEC_FINAL 	  => 	RS.F2);

      MI_DIASCALVAC := MI_DIASCALVAC + MI_TDIAS;	
      PCK_NOMINA.CNINC(35) := 35 + MI_TDIAS;	
    END LOOP;   
    PCK_NOMINA_COM8.PR_ACUMNOVEDADES( 	UN_COMPANIA       => UN_COMPANIA,
                                        UN_ANIO1          => UN_ANO,
                                        UN_MES1           => UN_MES,    
                                        UN_PERIODO1       => '01',    
                                        UN_ANIO2          => UN_ANO,
                                        UN_MES2           => UN_MES,    
                                        UN_PERIODO2       => '99' ,
                                        UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO ,
                                        UN_PROCESO        => UN_PROCESO
                                     );	

    PCK_NOMINA_COM8.PR_LOGICA_IDENTIFICAR_NOV( 	UN_COMPANIA       => UN_COMPANIA,
                                                UN_ID_DE_EMPLEADO => UN_ID_DE_EMPLEADO,
                                                UN_TIPONOVEDAD    => 'Vacaciones',
                                                UN_DIASINC        => MI_DIASCALVAC,
                                                UN_NOMBRE         => MI_NOMBRECOMPLETO,
                                                UN_NUMERO_DOC     => MI_NUMERO_DCTO ,
                                                UN_CONCEPTO       => 35 ,  
                                                UN_MES            => UN_MES ,
                                                UN_ANIO           => UN_ANO,
                                                UN_PERIODO        => UN_PERIODO ,
                                                UN_USUARIO        => UN_USUARIO );	

    PCK_NOMINA.CNINC.DELETE;

END PR_IDENTIFICARNOVEDADESPERIODO;	

--5
PROCEDURE PR_NOVEDADESANTESDELIQUIDAR
(
/*
    NAME              : PR_NOVEDADESANTESDELIQUIDAR   
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 23/02/2018
    TIME              : 11:38 AM
    DESCRIPTION       : FUNCION ENCARGADA DE GUARDAR LOS ERRORES DE VACACIONES O LICENCIAS EN LA TABLA ERRORORESINICIALES
    PARAMETROS        : UN_COMPANIA    => COMPANIA DE INGRESO A LA COMPANIA 
                        UN_ANIO        => ANO DE INGRESO AL MODULO DE NOMINA
                        UN_MES         => MES DE INGRESO AL MODULO DE NOMINA 
                        UN_PERIODO     => PERIODO DE INGRESO AL MODULO DE NOMINA
                        UN_PROCESO     => PROCESO DE INGRESO AL MODULO DE NOMINA
                        UN_USUARIO     => USUARIO DE INGRESO AL MODULO DE NOMINA
                        UN_DESCRIPCION => LA INCONSISTENCIA ENCONTRADA AL REALIZAR EL PROCESO DE PRENONIMA
   @NAME: identificarNovedadesAntesDeLiquidar                    

  */
    UN_COMPANIA   IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO    IN  PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANO		    IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MES		    IN  PCK_SUBTIPOS.TI_MES,
    UN_PERIODO	  IN  PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_INICIAL    IN  VARCHAR2,
    UN_FINAL	    IN  VARCHAR2,
    UN_USUARIO    IN  PCK_SUBTIPOS.TI_USUARIO
)
	AS
		MI_FECHAINI_PER DATE;
		MI_FECHAFIN_PER DATE;
		MI_CONSULTA 	  PCK_SUBTIPOS.TI_CONSULTA;
		MI_CONDICION 	  PCK_SUBTIPOS.TI_CONDICION;
    MI_COND      	  PCK_SUBTIPOS.TI_CONDICION;
		MI_ESTADO		    NUMBER;
		MI_EMPFINAL     PCK_SUBTIPOS.TI_ID_DE_EMPLEADO;
		MI_RS	          SYS_REFCURSOR;
		MI_EMPLEADO     PCK_SUBTIPOS.TI_ID_DE_EMPLEADO;

	BEGIN 
		MI_FECHAINI_PER := PCK_NOMINA.FC_FECHAINIFINPERIODO(
                                    UN_COMPANIA  => UN_COMPANIA,
                                    UN_PROCESO   => UN_PROCESO,
                                    UN_ANIO      => UN_ANO,
                                    UN_MES       => UN_MES,
                                    UN_PERIODO   => UN_PERIODO,
                                    UN_FECHAINICIO => 1);
		MI_FECHAFIN_PER := PCK_NOMINA.FC_FECHAINIFINPERIODO(
                                    UN_COMPANIA  => UN_COMPANIA,
                                    UN_PROCESO   => UN_PROCESO,
                                    UN_ANIO      => UN_ANO,
                                    UN_MES       => UN_MES,
                                    UN_PERIODO   => UN_PERIODO,
                                    UN_FECHAINICIO => 2);

		MI_CONSULTA := 'SELECT ID_DE_EMPLEADO
						FROM PERSONAL';
		MI_CONDICION := ' WHERE COMPANIA = '''||UN_COMPANIA||'''
                        AND ((FECHA_DE_INGRESO <= TO_DATE(''' || MI_FECHAFIN_PER|| ''',''DD/MM/YYYY HH24:MI:SS'')
                          AND ESTADO_ACTUAL NOT IN(3))
                        OR (FECHA_DE_RETIRO >= 	TO_DATE(''' || MI_FECHAINI_PER|| ''',''DD/MM/YYYY HH24:MI:SS'') 
                          AND FECHA_DE_INGRESO <= TO_DATE(''' || MI_FECHAFIN_PER|| ''',''DD/MM/YYYY HH24:MI:SS'') 
                          AND ESTADO_ACTUAL IN(3)))' ;
		IF UN_INICIAL = 'EA' THEN --ESTADO ACTUAL
			MI_ESTADO := TO_NUMBER(UN_FINAL);
			MI_CONDICION := MI_CONDICION || ' AND ESTADO_ACTUAL IN('||MI_ESTADO||')';
		ELSIF UN_INICIAL = 'CC' THEN --CENTRO DE COSTO
			MI_CONDICION := MI_CONDICION || ' AND ID_CENTRO_DE_COSTO = '''||UN_FINAL||'''';
		ELSIF UN_INICIAL = 'TE' THEN --TIPO DE EMPLEADO
			MI_CONDICION := MI_CONDICION || ' AND ID_DE_TIPO = '''||UN_FINAL||'''';
		ELSE --TODOS LOS EMPLEADOS
			MI_EMPFINAL := TO_NUMBER(UN_FINAL);
			MI_CONDICION := MI_CONDICION || ' AND ID_DE_EMPLEADO BETWEEN '||UN_INICIAL||' AND '||MI_EMPFINAL||'';
		END IF;	
		MI_CONSULTA := MI_CONSULTA || MI_CONDICION || ' ORDER BY ID_DE_EMPLEADO';

    MI_COND    := ' COMPANIA        = '''||UN_COMPANIA||'''' ; 

    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA     => 'ERRORESINICIALES',
                                            UN_ACCION    => 'E',
                                            UN_CONDICION => MI_COND  ); 
		OPEN MI_RS FOR MI_CONSULTA;
			LOOP
			FETCH MI_RS
			INTO MI_EMPLEADO;
			EXIT WHEN MI_RS%NOTFOUND;
				PCK_NOMINA_COM8.PR_IDENTIFICARNOVEDADESPERIODO(
														UN_COMPANIA 		    => UN_COMPANIA,
														UN_ID_DE_EMPLEADO 	=> MI_EMPLEADO,
														UN_MES 				      => UN_MES,
														UN_ANO 			        => UN_ANO,
														UN_PERIODO 			    => UN_PERIODO,
														UN_PROCESO 			    => UN_PROCESO,
														UN_USUARIO 			    => UN_USUARIO);
			END LOOP;
		CLOSE MI_RS;	
END PR_NOVEDADESANTESDELIQUIDAR;	

FUNCTION FC_RETORNARTOTALDIAS
(
/*
    NAME              : FC_RETORNARTOTALDIAS   
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 26/02/2018
    TIME              : 09:06 AM
    DESCRIPTION       : FUNCION ENCARGADA DE RETORNAR EL NUMERO DE DIAS COMERCIALES SEGUN FECHAS RECIBIDAS POR PARAMETRO, 
                        SE LLAMA EN EL PROCEDIMIENTO PR_IDENTIFICARNOVEDADESPERIODO
    PARAMETROS        : 	UN_FECHA_INICIO 
                          UN_FECHA_FINAL	
                          UN_FEC_INICIO   
                          UN_FEC_FINAL	 
 */
	UN_FECHA_INICIO IN DATE, 
	UN_FECHA_FINAL	IN DATE,
	UN_FEC_INICIO   IN DATE,
	UN_FEC_FINAL	  IN DATE
)
RETURN PCK_SUBTIPOS.TI_ENTERO
AS
	MI_FINICIO     	DATE;
	MI_FFINAL		DATE;

BEGIN 

	IF UN_FEC_INICIO >= UN_FECHA_INICIO AND UN_FEC_INICIO <= UN_FECHA_FINAL THEN 
		MI_FINICIO := UN_FEC_INICIO;
	ELSIF UN_FEC_INICIO <= 	UN_FECHA_INICIO THEN 
		MI_FINICIO := UN_FECHA_INICIO;
	ELSE 
		MI_FINICIO := UN_FEC_INICIO;
	END IF;
	IF UN_FEC_FINAL >= UN_FECHA_INICIO AND UN_FEC_FINAL <= UN_FECHA_FINAL THEN 
		MI_FFINAL := UN_FEC_FINAL;
	ELSIF UN_FEC_FINAL > UN_FECHA_FINAL THEN 
		MI_FFINAL := UN_FECHA_FINAL;
	ELSE 
		MI_FFINAL := UN_FEC_FINAL;
	END IF;

	RETURN PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(	  UN_FECHAIN 	=> MI_FINICIO,
                                                UN_FECHAFIN => MI_FFINAL);

END FC_RETORNARTOTALDIAS;

FUNCTION FC_CALCULANETOS(

    /*NAME              : ACTUALIZAR_INCR_AUTOAVALUO -->
        AUTHORS           : SYSMAN SAS
        AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
        DATE MIGRADOR     : 27/02/2018
        TIME              : 04:00 PM
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Carga datos de autoavaluo en tabla temporal.
        MODIFICATIONS     :
        PARAMETERS        : UN_COMPANIA         => Compañia de ingreso a la aplicación
                            UN_ANIOFINAL        => Año Final seleccionado en formualario
                            UN_MESFINAL         => Mes Final seleccionado en formualario
                            UN_PERIODOFIN       => Periodo Final seleccionado en formualario
                            UN_PROCESO          => proceso seleccionado en formualario
                            UN_INDICADOR        => Indicador de check "Cuadre totales del periodo"
                            UN_USUARIO          => Usuario que realizar el procedimiento

        @NAME  :  calcularNetos
        @METHOD:  PUT
    */

    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO     IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIOFINAL   IN PCK_SUBTIPOS.TI_ANIO,
    UN_MESFINAL    IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODOFIN  IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_INDICADOR   IN PCK_SUBTIPOS.TI_LOGICO,
    UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO

    )RETURN PCK_SUBTIPOS.TI_LOGICO
AS
    MI_PERIODO      NUMBER(1) DEFAULT 0;
    MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
    MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
    MI_MERGEUSING   PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE  PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXIST   PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGENOEXIS  PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_RTA          PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CONSULTA     PCK_SUBTIPOS.TI_STRSQL;
    MI_FECHAINICIAL DATE;
    MI_FECHAFINAL   DATE;
    MI_FECHA        DATE;
    MI_VALORESBASE  PCK_SUBTIPOS.TI_VALORES;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
BEGIN
    IF PCK_NOMINA.FC_PERIODOACTIVADONOMINA(UN_COMPANIA,UN_PROCESO,UN_ANIOFINAL,UN_MESFINAL,UN_PERIODOFIN) IN(0) THEN
        BEGIN
            MI_REEMPLAZOS(0).CLAVE:='PERIODO';
            MI_REEMPLAZOS(0).VALOR:= UN_PERIODOFIN;
            MI_REEMPLAZOS(1).CLAVE:='ANIO';
            MI_REEMPLAZOS(1).VALOR:= UN_ANIOFINAL;
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE, 
                                        UN_ERROR_COD => PCK_ERRORES.ERR_PERIODOACTIVADO, 
                                        UN_REEMPLAZOS => MI_REEMPLAZOS );
        END;
    END IF;
    IF UN_INDICADOR IN(0) THEN
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE, 
                                        UN_ERROR_COD => PCK_ERRORES.ERR_CALCULO_NETOS);
        END;
    END IF;
    BEGIN
        BEGIN
            MI_TABLA      := ' CONCEPTOS ';
            MI_MERGEUSING := ' SELECT COMPANIA, ID_DE_CONCEPTO FROM CONCEPTOS ' ;
            MI_MERGEEXIST := ' UPDATE SET TABLA.VALOR = 0 WHERE TABLA.COMPANIA = ''' || 
                              UN_COMPANIA || ''' AND TABLA.ANO = ' || 
                              UN_ANIOFINAL     || '   AND TABLA.MES = ' ||  
                              UN_MESFINAL      || '   AND TABLA.VALOR BETWEEN 0 AND 0.01 ';
            MI_MERGEENLACE:= '    TABLA.COMPANIA        = VISTA.COMPANIA 
                              AND TABLA.ID_DE_CONCEPTO  = VISTA.ID_DE_CONCEPTO';
            MI_RTA  :=  PCK_DATOS.FC_ACME (   UN_TABLA        => ' HISTORICOS ', 
                                              UN_ACCION       => 'MM',
                                              UN_MERGEUSING   => MI_MERGEUSING, 
                                              UN_MERGEENLACE  => MI_MERGEENLACE,
                                              UN_MERGEEXISTE  => MI_MERGEEXIST);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE,
                          UN_ERROR_COD  => PCK_ERRORES.ERR_CALCULO_NETOS_ACTVALOR
                      );                                  

    END;
    BEGIN
        BEGIN
            MI_TABLA      := 'HISTORICOS';
            MI_CONDICION  := '    HISTORICOS.COMPANIA       ='''|| UN_COMPANIA   || '''
                              AND HISTORICOS.ID_DE_PROCESO  ='  || UN_PROCESO    || '
                              AND HISTORICOS.ANO            ='  || UN_ANIOFINAL  || '
                              AND HISTORICOS.MES            ='  || UN_MESFINAL   || '
                              AND HISTORICOS.PERIODO        ='  || UN_PERIODOFIN || '
                              AND HISTORICOS.ID_DE_CONCEPTO IN (97,140,144)';
            MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA      => MI_TABLA,
                                          UN_ACCION     => 'E',
                                          UN_CONDICION  => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
							PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERR_CALCULO_NETOELIMINAR,
                                          UN_TABLAERROR => MI_TABLA);	
    END;
    BEGIN
        SELECT  TO_CHAR(PERIODOS.FECHAFINAL,'DD/MM/YYYY') FECHAFINAL
          INTO  MI_FECHAFINAL
          FROM  PERIODOS
         WHERE  PERIODOS.COMPANIA     = UN_COMPANIA
           AND  PERIODOS.ID_DE_PROCESO= UN_PROCESO 
           AND  PERIODOS.ANO          = UN_ANIOFINAL  
           AND  PERIODOS.MES          = UN_MESFINAL
           AND  PERIODOS.PERIODO      = UN_PERIODOFIN
           AND  PERIODOS.ACUMULADO   NOT IN(0);
    EXCEPTION WHEN NO_DATA_FOUND THEN  
        MI_FECHAFINAL := NULL;
    END;
    BEGIN

        <<NETOS>>
        FOR RS IN (SELECT CONSULTA.*, CONSULTA.DEVENGOS -CONSULTA.DESCUENTOS 
                                AS NETO 
                        FROM (
                            SELECT HISTORICOS.ID_DE_PROCESO, 
                                   HISTORICOS.ANO,
                                   HISTORICOS.MES,     
                                   HISTORICOS.ID_DE_EMPLEADO, 
                                   HISTORICOS.PERIODO,
                                   HISTORICOS.FECHA,
                                   SUM(CASE WHEN CONCEPTOS.CLASE = 3 
                                           THEN HISTORICOS.VALOR 
                                           ELSE 0 END) AS DEVENGOS, 
                                   SUM(CASE WHEN CONCEPTOS.CLASE = 5 
                                           THEN  HISTORICOS.VALOR 
                                           ELSE 0 END) AS DESCUENTOS, 
                                   SUM( CASE WHEN CONCEPTOS.CLASE = 7 
                                           THEN HISTORICOS.VALOR 
                                          ELSE 0 END) AS NETOHIS
                            FROM HISTORICOS LEFT JOIN CONCEPTOS 
                                 ON  HISTORICOS.COMPANIA        = CONCEPTOS.COMPANIA
                                 AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
                            WHERE CONCEPTOS.CLASE        IN (3,5,7)
                                 AND HISTORICOS.COMPANIA       = UN_COMPANIA
                                 AND HISTORICOS.ANO            = UN_ANIOFINAL
                                 AND HISTORICOS.MES            = UN_MESFINAL
                                 AND HISTORICOS.PERIODO        = UN_PERIODOFIN
                                 AND HISTORICOS.ID_DE_PROCESO  = UN_PROCESO
                            GROUP BY HISTORICOS.ID_DE_PROCESO,
                                 HISTORICOS.ANO,
                                 HISTORICOS.MES,
                                 HISTORICOS.ID_DE_EMPLEADO,
                                 HISTORICOS.FECHA,
                                 HISTORICOS.PERIODO ) CONSULTA )


        LOOP
            BEGIN   
                    MI_TABLA      := ' HISTORICOS ';
                    MI_CAMPOS     := ' COMPANIA,    ID_DE_PROCESO,  
                                       ANO,         MES, 
                                       PERIODO,     ID_DE_EMPLEADO, 
                                       FECHA,       MODIFIED_BY, 
                                       DATE_MODIFIED, ID_DE_CONCEPTO, 
                                       VALOR';
                    MI_VALORESBASE:=         '''' || UN_COMPANIA|| ''',' || RS.ID_DE_PROCESO 
                                         || ',' || RS.ANO     ||   ',' || RS.MES
                                         || ',' || RS.PERIODO ||   ',' || RS.ID_DE_EMPLEADO 
                                         || ',''' || CASE WHEN RS.FECHA IS NULL THEN 
                                                         CASE WHEN MI_FECHAFINAL IS NULL 
                                                              THEN  SYSDATE 
                                                         ELSE MI_FECHAFINAL END
                                                   ELSE RS.FECHA END 
                                         || ''',''' || UN_USUARIO ||  ''',''' || SYSDATE || '''';

                    BEGIN
                        MI_VALORES := MI_VALORESBASE|| ', 097, ''' || RS.DEVENGOS || '''' ;
                        MI_RTA := PCK_DATOS.FC_ACME( 
                                        UN_TABLA   => MI_TABLA
                                       ,UN_ACCION  => 'I' 
                                       ,UN_CAMPOS  => MI_CAMPOS
                                       ,UN_VALORES => MI_VALORES); 
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        MI_REEMPLAZOS(0).CLAVE:= 'CONCEPTO';
                        MI_REEMPLAZOS(0).VALOR:=  '97';
                        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                    END;
                    BEGIN
                        MI_VALORES := MI_VALORESBASE || ', 140, ''' || RS.DESCUENTOS || '''' ;
                        MI_RTA := PCK_DATOS.FC_ACME( 
                                        UN_TABLA   => MI_TABLA
                                       ,UN_ACCION  => 'I' 
                                       ,UN_CAMPOS  => MI_CAMPOS
                                       ,UN_VALORES => MI_VALORES); 
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        MI_REEMPLAZOS(0).CLAVE:= 'CONCEPTO';
                        MI_REEMPLAZOS(0).VALOR:=  '140';
                        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                    END;
                    BEGIN
                        MI_VALORES := MI_VALORESBASE || ', 144, ''' || RS.NETO || '''' ;
                        MI_RTA := PCK_DATOS.FC_ACME( 
                                        UN_TABLA   => MI_TABLA
                                       ,UN_ACCION  => 'I' 
                                       ,UN_CAMPOS  => MI_CAMPOS
                                       ,UN_VALORES => MI_VALORES);                        
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        MI_REEMPLAZOS(0).CLAVE:= 'CONCEPTO';
                        MI_REEMPLAZOS(0).VALOR:=  '144';
                        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                    END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERR_CALCULO_CREARREGISTRO,
                                            UN_TABLAERROR => MI_TABLA,
                                            UN_REEMPLAZOS => MI_REEMPLAZOS);                                  

            END;    
        END LOOP NETOS;

    END;
RETURN -1;
END FC_CALCULANETOS;


FUNCTION FC_GENERAR_INFORMACION_SIIF
/*
    NAME              : FC_GENERAR_INFORMACION_SIIF
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 28/02/2018
    TIME              : 10:00 AM  
    SOURCE MODULE     : SYSMAN_SIIF_NOMINA_2017.12.01_UNIFICADAS_06122017_AGR_ANE
    MODIFIER          : JOSE PASCUAL GOMEZ
    DATE MODIFIED     : 17/07/2018
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : RETORNA EN UN CLOB LOS DATOS PARA GENERAR EL EXCEL ACERCA DE LA INFORMACION DE SIIF NOMINA
                        Se ajusta para que el llamado de las deducciones se realice una sola vez y se agrupen todas de una vez

      @NAME:    generarInformacionSiif
      @METHOD:  GET
  */
(
   UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
   UN_ANIOINICIAL       IN HISTORICOS.ANO%TYPE, 
   UN_MESINICIAL        IN HISTORICOS.MES%TYPE,
   UN_PERIODOINICIAL    IN HISTORICOS.PERIODO%TYPE,
   UN_ANIOFINAL         IN HISTORICOS.ANO%TYPE, 
   UN_MESFINAL          IN HISTORICOS.MES%TYPE,
   UN_PERIODOFINAL      IN HISTORICOS.PERIODO%TYPE,
   UN_FECHA             IN DATE,      
   UN_TIPODOC           IN VARCHAR2,
   UN_NUMERODOC         IN VARCHAR2,
   UN_CODIGOEXP         IN VARCHAR2, 
   UN_TEXTO             IN VARCHAR2,
   UN_NIVEL             IN VARCHAR2,
   UN_ESBENEFICIO              PCK_SUBTIPOS.TI_LOGICO
)
RETURN CLOB
AS
  MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
  MI_RESULTADO              CLOB;
  MI_RESULTADOCONCEPTOS     CLOB;
  MI_RESULTADODEDUCCIONES   CLOB;
  MI_CONSECUTIVO            PCK_SUBTIPOS.TI_ENTERO;
  MI_SEPARABENEFICIO        PCK_SUBTIPOS.TI_PARAMETRO;
  MI_PRORRATEABENEFICIO     PCK_SUBTIPOS.TI_PARAMETRO;
  MI_DEPENDENCIA            PCK_SUBTIPOS.TI_PARAMETRO;
  MI_TOTDEVENGO             PCK_SUBTIPOS.TI_DOBLE;  
  MI_TOTDESCUENTO           PCK_SUBTIPOS.TI_DOBLE;
  MI_DESCUENTO              PCK_SUBTIPOS.TI_DOBLE;
  MI_MESANIO                VARCHAR2(250); 

BEGIN
      MI_SEPARABENEFICIO:= NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                     UN_NOMBRE    => 'GENERA SIIF SEPARADO PARA BENEFICIOS DE EMPLEADOS',
                                                     UN_MODULO    => PCK_DATOS.FC_MODULONOMINA, 
                                                     UN_FECHA_PAR => SYSDATE), 'SI');
      MI_PRORRATEABENEFICIO:= NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                        UN_NOMBRE    => 'EN SIIF PRORRATEA LOS DESCUENTOS PROPORCIONAL A LOS BENEFICIOS',
                                                        UN_MODULO    => PCK_DATOS.FC_MODULONOMINA, 
                                                        UN_FECHA_PAR => SYSDATE), 'SI');
      MI_DEPENDENCIA:= NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                 UN_NOMBRE    => 'DEPENDENCIA ORIGEN NOMINA SIIF',
                                                 UN_MODULO    => PCK_DATOS.FC_MODULONOMINA, 
                                                 UN_FECHA_PAR => SYSDATE), '000');
      IF MI_SEPARABENEFICIO ='NO' THEN
        MI_PRORRATEABENEFICIO :='NO';
      END IF;

      MI_MESANIO := UPPER(PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MESINICIAL) || ' de ' || UN_ANIOINICIAL);

      MI_CONSECUTIVO := 1;
      FOR RS IN (SELECT HISTORICOS.COMPANIA,
            PERSONAL.NIVELSIIF,
            SUM(HISTORICOS.VALOR) AS SUMADEVALOR,
            HISTORICOS.ID_DE_EMPLEADO,
            PERSONAL.APELLIDO1 || ' ' || PERSONAL.APELLIDO2|| ' '|| PERSONAL.NOMBRES NOMBRECOMPLETO,
            HISTORICOS.ANO,
            HISTORICOS.MES,
            HISTORICOS.PERIODO,
            SUM(CASE CONCEPTOS.CLASE WHEN 3 THEN HISTORICOS.VALOR ELSE 0 END) DEVENGOS,
            SUM(CASE WHEN CONCEPTOS.CLASE IN(3) AND CONCEPTOS.DEVENGO_BENEFICIO NOT IN(0) THEN HISTORICOS.VALOR ELSE 0 END) DEVENGOSBENEFICIOS,
            BANCO.NIT_SIIF NIT,
            PERSONAL.MEDIO_PAGO_SIIF,
            TIPOS_DOCUMENTOS.DOC_SIIF,
            PERSONAL.NUMERO_DCTO,
            PERSONAL.TIPOCUENTA,
            TIPOS_DOCUMENTOS_BANCO.DOC_SIIF DOC_SIIF_BANCO,
            PERSONAL.CUENTA
          FROM HISTORICOS
            INNER JOIN CONCEPTOS
              ON HISTORICOS.COMPANIA        = CONCEPTOS.COMPANIA
              AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
            INNER JOIN PERIODOS
              ON HISTORICOS.COMPANIA       = PERIODOS.COMPANIA
              AND HISTORICOS.ANO           = PERIODOS.ANO
              AND HISTORICOS.MES           = PERIODOS.MES
              AND HISTORICOS.PERIODO       = PERIODOS.PERIODO
              AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
            INNER JOIN PERSONAL
              ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
              AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
            INNER JOIN BANCOS_NOMINA BANCO
              ON PERSONAL.COMPANIA = BANCO.COMPANIA
              AND PERSONAL.BANCO   = BANCO.BANCO
            INNER JOIN TIPOS_DOCUMENTOS
              ON PERSONAL.COMPANIA        = TIPOS_DOCUMENTOS.COMPANIA
              AND PERSONAL.DCTO_IDENTIDAD = TIPOS_DOCUMENTOS.DCTO_IDENTIDAD
            LEFT JOIN TIPOS_DOCUMENTOS TIPOS_DOCUMENTOS_BANCO
              ON BANCO.COMPANIA        = TIPOS_DOCUMENTOS_BANCO.COMPANIA
              AND BANCO.DOC_SIIF_BANCO = TIPOS_DOCUMENTOS_BANCO.DCTO_IDENTIDAD
          WHERE  PERSONAL.COMPANIA = UN_COMPANIA 
            AND PERSONAL.NIVELSIIF = CASE WHEN UN_NIVEL IS NULL THEN PERSONAL.NIVELSIIF ELSE UN_NIVEL END 
            AND CONCEPTOS.CLASE   IN (3,5,7)      
            AND PERIODOS.ACUMULADO NOT IN(0)
            AND       HISTORICOS.ANO || LPAD(HISTORICOS.MES,2, '0') || LPAD(HISTORICOS.PERIODO,2,'0') 
              BETWEEN UN_ANIOINICIAL || LPAD(UN_MESINICIAL ,2, '0') || LPAD(UN_PERIODOINICIAL ,2,'0') 
                  AND UN_ANIOFINAL   || LPAD(UN_MESFINAL   ,2, '0') || LPAD(UN_PERIODOFINAL   ,2,'0')
          GROUP BY 
          HISTORICOS.COMPANIA,
            PERSONAL.NIVELSIIF,
            HISTORICOS.ID_DE_EMPLEADO,
            PERSONAL.APELLIDO1 || ' ' || PERSONAL.APELLIDO2|| ' '|| PERSONAL.NOMBRES,
            HISTORICOS.ANO,
            HISTORICOS.MES,
            HISTORICOS.PERIODO,
            BANCO.NIT_SIIF,
            PERSONAL.MEDIO_PAGO_SIIF,
            TIPOS_DOCUMENTOS.DOC_SIIF,
            PERSONAL.NUMERO_DCTO,
            PERSONAL.TIPOCUENTA,
            TIPOS_DOCUMENTOS_BANCO.DOC_SIIF,
            PERSONAL.CUENTA
          HAVING SUM(HISTORICOS.VALOR) <> 0
          ORDER BY PERSONAL.APELLIDO1 || ' ' || PERSONAL.APELLIDO2|| ' '|| PERSONAL.NOMBRES)
      LOOP

          MI_TOTDEVENGO   :=0;
          MI_TOTDESCUENTO :=0;
          <<CONCEPTOSDEVENGOS>>               
          FOR RS_CONCEPTOS IN (SELECT HISTORICOS.COMPANIA,
                                      PERSONAL.NUMERO_DCTO,
                                      SUM(CASE WHEN MI_SEPARABENEFICIO ='NO' 
                                                 OR (CONCEPTOS.DEVENGO_BENEFICIO     IN(0) AND UN_ESBENEFICIO     IN(0))
                                                 OR (CONCEPTOS.DEVENGO_BENEFICIO NOT IN(0) AND UN_ESBENEFICIO NOT IN(0))
                                               THEN  HISTORICOS.VALOR
                                               ELSE 0 END) TOTAL,
                                      CONCEPTOS.RUBROS_SIIF
                                FROM HISTORICOS
                                  INNER JOIN CONCEPTOS
                                    ON HISTORICOS.COMPANIA        = CONCEPTOS.COMPANIA
                                    AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
                                  INNER JOIN PERIODOS
                                    ON HISTORICOS.COMPANIA = PERIODOS.COMPANIA
                                    AND HISTORICOS.PERIODO = PERIODOS.PERIODO
                                    AND HISTORICOS.MES     = PERIODOS.MES
                                    AND HISTORICOS.ANO     = PERIODOS.ANO
                                    AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                                  INNER JOIN PERSONAL
                                    ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                                    AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                                WHERE HISTORICOS.COMPANIA= UN_COMPANIA
                                  AND PERSONAL.NUMERO_DCTO = RS.NUMERO_DCTO
                                  AND CONCEPTOS.CLASE      = 3
                                  AND PERIODOS.ACUMULADO NOT IN(0)
                                  AND       HISTORICOS.ANO || LPAD(HISTORICOS.MES,2, '0') || LPAD(HISTORICOS.PERIODO,2,'0') 
                                    BETWEEN UN_ANIOINICIAL || LPAD(UN_MESINICIAL ,2, '0') || LPAD(UN_PERIODOINICIAL ,2,'0') 
                                        AND UN_ANIOFINAL   || LPAD(UN_MESFINAL   ,2, '0') || LPAD(UN_PERIODOFINAL   ,2,'0')
                                GROUP BY HISTORICOS.COMPANIA,
                                  PERSONAL.NUMERO_DCTO,
                                  CONCEPTOS.RUBROS_SIIF
                                HAVING  SUM(CASE WHEN MI_SEPARABENEFICIO ='NO' 
                                                 OR (CONCEPTOS.DEVENGO_BENEFICIO     IN(0) AND UN_ESBENEFICIO     IN(0))
                                                 OR (CONCEPTOS.DEVENGO_BENEFICIO NOT IN(0) AND UN_ESBENEFICIO NOT IN(0))
                                               THEN  HISTORICOS.VALOR
                                               ELSE 0 END) <>0) 
          LOOP 
            MI_RESULTADOCONCEPTOS := MI_RESULTADOCONCEPTOS || TO_CLOB( 
                         MI_CONSECUTIVO                    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         MI_DEPENDENCIA                    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         NVL(RS_CONCEPTOS.RUBROS_SIIF,'')  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         NVL(RS_CONCEPTOS.TOTAL,0)         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         PCK_DATOS.GL_SEPARADOR_REG);
            MI_TOTDEVENGO := MI_TOTDEVENGO + NVL(RS_CONCEPTOS.TOTAL,0); 
          END LOOP CONCEPTOSDEVENGOS;

          --DE ACUERDO A PETICIÓN DE LA ANE SE QUITA LA DISTRIBUCIÓN DE LOS CONCEPTOS 132, 120 POR FONDO SIN EMBARGO 
          --EL NIT SI DEBE TRAER DEL FONDO, NO SE TIENE EN CUENTA SI ES PUBLICO EL FONDO O NO PONE EL DEL CONCEPTO
          <<CONCEPTODESCUENTOS>>
          FOR RSDESCUENTO IN ( SELECT 
                    SUM(HISTORICOS.VALOR) TOTAL,
                    CASE WHEN V_FONDOS.COMPANIA IS NULL THEN CONCEPTOS.NITCONCEPTO     ELSE V_FONDOS.NIT          END NIT,
                    CASE WHEN V_FONDOS.COMPANIA IS NULL THEN TIPOS_DOCUMENTOS.DOC_SIIF ELSE TIPOFONDO.DOC_SIIF    END DOC_SIIF,

                    CASE WHEN NVL(CONCEPTOS.SIIF_ATRIBUTOEMPLEADO, ' ') <> ' '
                         THEN CONCEPTOS.SIIF_ATRIBUTOEMPLEADO 
                         ELSE V_FONDOS.RUBRO_SIIF_EMPLEADO  END SIIF_ATRIBUTOEMPLEADO


                FROM PERSONAL INNER JOIN HISTORICOS
                   ON HISTORICOS.COMPANIA       = PERSONAL.COMPANIA
                  AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                INNER JOIN CONCEPTOS
                   ON HISTORICOS.COMPANIA       = CONCEPTOS.COMPANIA
                  AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
                INNER JOIN PERIODOS
                   ON HISTORICOS.COMPANIA      = PERIODOS.COMPANIA
                  AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                  AND HISTORICOS.ANO           = PERIODOS.ANO
                  AND HISTORICOS.MES           = PERIODOS.MES
                  AND HISTORICOS.PERIODO       = PERIODOS.PERIODO
                LEFT JOIN TIPOS_DOCUMENTOS
                  ON  CONCEPTOS.COMPANIA            =   TIPOS_DOCUMENTOS.COMPANIA
                  AND CONCEPTOS.DOC_SIIF_CONCEPTO   =   TIPOS_DOCUMENTOS.DCTO_IDENTIDAD                
                LEFT JOIN  V_FONDOS
                    ON PERSONAL.COMPANIA    = V_FONDOS.COMPANIA
                   AND CASE WHEN CONCEPTOS.ID_DE_CONCEPTO IN(113, 130, 123)                                --SALUD
                            THEN PERSONAL.FONDO_SALUD 
                            ELSE CASE WHEN CONCEPTOS.ID_DE_CONCEPTO IN(120,131,132)                        --PENSION
                                      THEN PERSONAL.ID_DEL_FONDO 
                                      ELSE CASE WHEN CONCEPTOS.ID_DE_CONCEPTO IN(124)                       --PENSION VOLUNTARIA
                                                THEN PERSONAL.FONDO_PENSION_VOL 
                                                ELSE CASE WHEN CONCEPTOS.ID_DE_CONCEPTO IN(127)             --PENSION AFC
                                                          THEN PERSONAL.FONDO_AFC 
                                                          ELSE CASE WHEN CONCEPTOS.ID_DE_CONCEPTO IN(100, 200, 205)    --MEDICINAPREPAGADA
                                                                    THEN PERSONAL.MEDICINA_PREPAGADA 
                                                                    ELSE ' '
                                                               END 
                                                     END
                                           END         
                                  END
                        END = V_FONDOS.COD_FONDO
                LEFT JOIN TIPOS_DOCUMENTOS TIPOFONDO
                   ON V_FONDOS.COMPANIA   = TIPOFONDO.COMPANIA
                  AND V_FONDOS.DOC_SIIF   = TIPOFONDO.DCTO_IDENTIDAD
                WHERE PERSONAL.COMPANIA         = UN_COMPANIA   
                  AND PERSONAL.NUMERO_DCTO      = RS.NUMERO_DCTO
                  AND PERIODOS.ACUMULADO NOT IN(0)
                  AND CONCEPTOS.CLASE = 5
                  AND       HISTORICOS.ANO || LPAD(HISTORICOS.MES,2, '0') || LPAD(HISTORICOS.PERIODO,2,'0') 
                    BETWEEN UN_ANIOINICIAL || LPAD(UN_MESINICIAL ,2, '0') || LPAD(UN_PERIODOINICIAL ,2,'0') 
                        AND UN_ANIOFINAL   || LPAD(UN_MESFINAL   ,2, '0') || LPAD(UN_PERIODOFINAL   ,2,'0')     
                  AND (CONCEPTOS.ID_DE_CONCEPTO BETWEEN 600 AND 698        
                    OR CONCEPTOS.ID_DE_CONCEPTO BETWEEN 700 AND 798         
                    OR CONCEPTOS.ID_DE_CONCEPTO IN(113,130,123,            
                                                 120,131,132,            
                                                 125,126,225,            
                                                 124,                    
                                                 127,                   
                                                 100, 200, 205
                                                )) 
                GROUP BY CASE WHEN V_FONDOS.COMPANIA IS NULL THEN CONCEPTOS.NITCONCEPTO     ELSE V_FONDOS.NIT          END,
                         CASE WHEN V_FONDOS.COMPANIA IS NULL THEN TIPOS_DOCUMENTOS.DOC_SIIF ELSE TIPOFONDO.DOC_SIIF    END,
                         CASE WHEN NVL(CONCEPTOS.SIIF_ATRIBUTOEMPLEADO, ' ') <> ' '
                              THEN CONCEPTOS.SIIF_ATRIBUTOEMPLEADO 
                              ELSE V_FONDOS.RUBRO_SIIF_EMPLEADO  END
               )
          LOOP
            IF (MI_SEPARABENEFICIO ='NO') OR (MI_SEPARABENEFICIO ='SI' AND MI_PRORRATEABENEFICIO='NO') THEN
                MI_DESCUENTO := RSDESCUENTO.TOTAL;
            ELSE
                IF RS.DEVENGOS =0 THEN
                    MI_DESCUENTO := 0;
                ELSE
                    MI_DESCUENTO :=  PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => RSDESCUENTO.TOTAL*(RS.DEVENGOSBENEFICIOS/RS.DEVENGOS)
                                                            ,UN_PRECISION => 0);
                END IF;
                IF UN_ESBENEFICIO IN(0) THEN
                    MI_DESCUENTO := RSDESCUENTO.TOTAL - MI_DESCUENTO;
                END IF;
            END IF;    
            IF MI_DESCUENTO <> 0 THEN
                MI_RESULTADODEDUCCIONES := MI_RESULTADODEDUCCIONES || TO_CLOB( 
                                       MI_CONSECUTIVO                            || PCK_DATOS.GL_SEPARADOR_COL ||
                                       NVL(RSDESCUENTO.SIIF_ATRIBUTOEMPLEADO,'') || PCK_DATOS.GL_SEPARADOR_COL ||
                                       MI_DESCUENTO                              || PCK_DATOS.GL_SEPARADOR_COL ||
                                       NVL(RSDESCUENTO.DOC_SIIF   ,'')           || PCK_DATOS.GL_SEPARADOR_COL ||
                                       NVL(RSDESCUENTO.NIT        ,'')           || PCK_DATOS.GL_SEPARADOR_COL ||
                                       PCK_DATOS.GL_SEPARADOR_REG);
                 MI_TOTDESCUENTO := MI_TOTDESCUENTO + MI_DESCUENTO; 
            END IF;
          END LOOP CONCEPTODESCUENTOS;        

          IF MI_TOTDESCUENTO<>0 OR MI_TOTDEVENGO<>0 THEN 
              MI_RESULTADO:= MI_RESULTADO || TO_CLOB( 
                             MI_CONSECUTIVO                     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             NVL(RS.DOC_SIIF,3)                 ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             NVL(RS.NUMERO_DCTO,0)              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             NVL(RS.MEDIO_PAGO_SIIF,'AC')       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             NVL(RS.DOC_SIIF_BANCO,'01')        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             NVL(RS.NIT,'')                     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             NVL(CASE WHEN RS.TIPOCUENTA = 'C' 
                                      THEN 'CRR' ELSE 'AHR' 
                                      END,'AHR')                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             NVL(RS.CUENTA,'')                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             '11'                               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             TO_CHAR(MI_TOTDEVENGO)             ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             TO_CHAR(MI_TOTDESCUENTO)           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             TO_CHAR(MI_TOTDEVENGO -   MI_TOTDESCUENTO) ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             UN_FECHA                           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             UN_TIPODOC                         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             NVL(UN_NUMERODOC,'NÓMINA')  || ' ' || MI_MESANIO ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             UN_CODIGOEXP                       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             UN_TEXTO|| ' ' || MI_MESANIO||'-'||RS.NOMBRECOMPLETO   ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                             PCK_DATOS.GL_SEPARADOR_REG);
                MI_CONSECUTIVO :=  MI_CONSECUTIVO + 1;         
            END IF;
      END LOOP;
      RETURN  MI_RESULTADO          || ',.HOJ.,' ||
              MI_RESULTADOCONCEPTOS || ',.HOJ.,' ||
              MI_RESULTADODEDUCCIONES;
END FC_GENERAR_INFORMACION_SIIF;


FUNCTION FC_INFORMACIONPROVISIONES(

    /*NAME            : FC_INFORMACIONPROVISIONES -->
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
    DATE MIGRADOR     : 01/03/2018
    TIME              : 09:00 AM
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Devuelve un clob que muestra los valores de las provisiones por empleado
    MODIFICATIONS     :
    PARAMETERS        : UN_COMPANIA     => Compañia de ingreso a la aplicación
                        UN_ANIOINI      => Año Inicial seleccionado en formualario
                        UN_MESINI       => Mes Inicial seleccionado en formualario
                        UN_PERIODOINI   => Periodo Inicial seleccionado en formualario
                        UN_ANIOFIN      => Año Final seleccionado en formualario
                        UN_MESFIN       => Mes Final seleccionado en formualario
                        UN_PERIODOFIN   => Periodo Final seleccionado en formualario
                        UN_NIVEL        => Nivel a filtrar de tabla personal
                        UN_FECHASOLICIT => Fecha Solicitud formulario
                        UN_DOCSOPORTE   => Documento soporte formulario
                        UN_NUMSOPORTE   => Número de soporte formulario
                        UN_CODEXPEDIDOR => Código expedidor formulario
                        UN_TXTJUSTIF    => Texto justificacion formulario

    @NAME  :  informacionPatronal
    @METHOD:  GET
    */
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIOINI    IN PCK_SUBTIPOS.TI_ANIO,
    UN_MESINI     IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODOINI IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ANIOFIN    IN PCK_SUBTIPOS.TI_ANIO,
    UN_MESFIN     IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODOFIN IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_NIVEL      IN VARCHAR2,
    UN_FECHASOLICIT IN DATE,
    UN_DOCSOPORTE   IN VARCHAR2,  
    UN_NUMSOPORTE   IN VARCHAR2,  
    UN_CODEXPEDIDOR IN VARCHAR2,  
    UN_TXTJUSTIF    IN VARCHAR2    
 ) RETURN CLOB
AS
  MI_CADENA       CLOB;    
  MI_HOJADATOS    CLOB;
  MI_HOJADEDUCCIONES  CLOB;
  MI_HOJACONCEPTOS    CLOB;
  MI_CONSECUTIVO      PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN
    MI_CONSECUTIVO := 0;
    <<CONCEPTOS>>
    FOR RS IN (SELECT  CONCEPTOS.CLASE,
                       HISTORICOS.ID_DE_PROCESO,
                        HISTORICOS.ID_DE_CONCEPTO,
                        SUM(HISTORICOS.VALOR) AS SUMADEVALOR,
                        CONCEPTOS.NOMBRE_CONCEPTO,
                        HISTORICOS.COMPANIA
                FROM HISTORICOS INNER JOIN CONCEPTOS
                   ON HISTORICOS.COMPANIA       = CONCEPTOS.COMPANIA
                  AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
                WHERE       HISTORICOS.ANO || LPAD(HISTORICOS.MES,2, '0') || LPAD(HISTORICOS.PERIODO,2,'0') 
                    BETWEEN UN_ANIOINI     || LPAD(UN_MESINI     ,2, '0') || LPAD(UN_PERIODOINI     ,2,'0') 
                        AND UN_ANIOFIN     || LPAD(UN_MESFIN     ,2, '0') || LPAD(UN_PERIODOFIN     ,2,'0')
                  AND CONCEPTOS.CLASE        IN (8)
                  AND HISTORICOS.ID_DE_CONCEPTO BETWEEN 490 AND 499
                  AND HISTORICOS.COMPANIA       = UN_COMPANIA
                GROUP BY CONCEPTOS.CLASE,
                      HISTORICOS.ID_DE_PROCESO,
                      HISTORICOS.ID_DE_CONCEPTO,
                      CONCEPTOS.NOMBRE_CONCEPTO,
                      HISTORICOS.COMPANIA
                ORDER BY HISTORICOS.ID_DE_CONCEPTO)
    LOOP
            FOR RS2 IN (SELECT TIPOS_DOCUMENTOS.DOC_SIIF,
                        PERSONAL.NIVELSIIF,
                        SUM(HISTORICOS.VALOR)     TOTAL,
                        CONCEPTOS.NITCONCEPTO     NIT,
                        'AC'                      MEDIO_PAGO,
                        TIPOS_DOCUMENTOS.DOC_SIIF DOC_SIIF_BCO1,
                        BANCOS_NOMINA.NIT_SIIF    NIT_SIIF_BCO,
                        CONCEPTOS.C_TIPO_CUENTA    TIPO_CTA_ENTIDAD,
                        CONCEPTOS.NOMBRE_CONCEPTO CONCEPTO1,
                        CONCEPTOS.C_CUENTA        CUENTA,
                        CONCEPTOS.RUBROS_SIIF,
                        CONCEPTOS.SIIF_ATRIBUTOPATRON ,
                        TIPOS_DOCUMENTOS.DOC_SIIF DOC_SIIF_BCO
                      FROM HISTORICOS INNER JOIN CONCEPTOS
                        ON HISTORICOS.COMPANIA      = CONCEPTOS.COMPANIA
                       AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO 
                      INNER JOIN PERIODOS
                        ON HISTORICOS.COMPANIA     = PERIODOS.COMPANIA
                       AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                       AND HISTORICOS.ANO          = PERIODOS.ANO
                       AND HISTORICOS.MES          = PERIODOS.MES
                       AND HISTORICOS.PERIODO      = PERIODOS.PERIODO
                      INNER JOIN PERSONAL
                        ON HISTORICOS.COMPANIA      = PERSONAL.COMPANIA
                       AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO 
                      LEFT JOIN TIPOS_DOCUMENTOS
                        ON CONCEPTOS.COMPANIA         = TIPOS_DOCUMENTOS.COMPANIA
                       AND CONCEPTOS.DOC_SIIF_CONCEPTO = TIPOS_DOCUMENTOS.DCTO_IDENTIDAD
                      LEFT JOIN BANCOS_NOMINA
                        ON CONCEPTOS.COMPANIA           = BANCOS_NOMINA.COMPANIA 
                       AND CONCEPTOS.C_BANCO             = BANCOS_NOMINA.BANCO
                      LEFT JOIN TIPOS_DOCUMENTOS DOCUMENTO2
                        ON BANCOS_NOMINA.COMPANIA        = DOCUMENTO2.COMPANIA
                       AND BANCOS_NOMINA.DOC_SIIF_BANCO = DOCUMENTO2.DCTO_IDENTIDAD
                      WHERE CONCEPTOS.ID_DE_CONCEPTO  = RS.ID_DE_CONCEPTO  
                      AND      HISTORICOS.ANO || LPAD(HISTORICOS.MES,2, '0') || LPAD(HISTORICOS.PERIODO,2,'0') 
                       BETWEEN UN_ANIOINI     || LPAD(UN_MESINI     ,2, '0') || LPAD(UN_PERIODOINI     ,2,'0') 
                           AND UN_ANIOFIN     || LPAD(UN_MESFIN     ,2, '0') || LPAD(UN_PERIODOFIN     ,2,'0')
                      AND CONCEPTOS.CLASE    IN (8)
                      AND HISTORICOS.COMPANIA = UN_COMPANIA
                      AND PERSONAL.NIVELSIIF IN (CASE WHEN UN_NIVEL IS NOT NULL THEN UN_NIVEL ELSE PERSONAL.NIVELSIIF END)
                      GROUP BY PERSONAL.NIVELSIIF,
                        TIPOS_DOCUMENTOS.DOC_SIIF,
                        CONCEPTOS.NITCONCEPTO,
                        'AC',
                        TIPOS_DOCUMENTOS.DOC_SIIF,
                        BANCOS_NOMINA.NIT_SIIF,
                        CONCEPTOS.C_TIPO_CUENTA,
                        CONCEPTOS.RUBROS_SIIF,
                        CONCEPTOS.SIIF_ATRIBUTOPATRON ,
                        CONCEPTOS.NOMBRE_CONCEPTO,
                        CONCEPTOS.C_CUENTA,
                        TIPOS_DOCUMENTOS.DOC_SIIF
                      HAVING (SUM(HISTORICOS.VALOR)) <> 0)


        LOOP
                          MI_CONSECUTIVO := MI_CONSECUTIVO +1;
                          MI_HOJADATOS := MI_HOJADATOS    || 
                                          MI_CONSECUTIVO     || PCK_DATOS.GL_SEPARADOR_COL || 
                                          CASE WHEN RS2.DOC_SIIF IS NOT NULL THEN RS2.DOC_SIIF ELSE '03' END   || PCK_DATOS.GL_SEPARADOR_COL || 
                                          CASE WHEN RS2.NIT IS NOT NULL THEN RS2.NIT ELSE '0' END   || PCK_DATOS.GL_SEPARADOR_COL ||
                                          CASE WHEN RS2.MEDIO_PAGO IS NOT NULL THEN RS2.MEDIO_PAGO ELSE 'AC' END   || PCK_DATOS.GL_SEPARADOR_COL || 
                                          CASE WHEN RS2.DOC_SIIF_BCO IS NOT NULL THEN RS2.DOC_SIIF_BCO ELSE '01' END || PCK_DATOS.GL_SEPARADOR_COL || 
                                          RS2.NIT_SIIF_BCO|| PCK_DATOS.GL_SEPARADOR_COL || 
                                          CASE WHEN RS2.TIPO_CTA_ENTIDAD IN('C','CRR','AHR') THEN RS2.TIPO_CTA_ENTIDAD ELSE 'AHR' END || PCK_DATOS.GL_SEPARADOR_COL || 
                                          RS2.CUENTA      || PCK_DATOS.GL_SEPARADOR_COL || 
                                          ' '             || PCK_DATOS.GL_SEPARADOR_COL || 
                                          CASE WHEN RS2.TOTAL IS NOT NULL THEN RS2.TOTAL ELSE '0' END        || PCK_DATOS.GL_SEPARADOR_COL || 
                                          CASE WHEN RS2.TOTAL IS NOT NULL THEN RS2.TOTAL ELSE '0' END        || PCK_DATOS.GL_SEPARADOR_COL || 
                                          0               || PCK_DATOS.GL_SEPARADOR_COL || 
                                          UN_FECHASOLICIT || PCK_DATOS.GL_SEPARADOR_COL || 
                                          UN_DOCSOPORTE   || PCK_DATOS.GL_SEPARADOR_COL ||
                                          UN_NUMSOPORTE   || PCK_DATOS.GL_SEPARADOR_COL ||
                                          UN_CODEXPEDIDOR || PCK_DATOS.GL_SEPARADOR_COL ||
                                          UN_TXTJUSTIF    || PCK_DATOS.GL_SEPARADOR_REG ;
                          MI_HOJACONCEPTOS   := MI_HOJACONCEPTOS || 
                                          MI_CONSECUTIVO     || PCK_DATOS.GL_SEPARADOR_COL ||
                                          000             || PCK_DATOS.GL_SEPARADOR_COL || 
                                          CASE WHEN RS2.RUBROS_SIIF IS NOT NULL THEN RS2.RUBROS_SIIF ELSE ' ' END || PCK_DATOS.GL_SEPARADOR_COL || 
                                          CASE WHEN RS2.TOTAL IS NOT NULL THEN RS2.TOTAL ELSE 0 END               || PCK_DATOS.GL_SEPARADOR_REG ; 

                          MI_HOJADEDUCCIONES := MI_HOJADEDUCCIONES || 
                                          MI_CONSECUTIVO     || PCK_DATOS.GL_SEPARADOR_COL ||
                                          RS2.SIIF_ATRIBUTOPATRON|| PCK_DATOS.GL_SEPARADOR_COL || 
                                          CASE WHEN RS2.TOTAL IS NOT NULL THEN RS2.TOTAL ELSE 0 END  || PCK_DATOS.GL_SEPARADOR_COL || 
                                          CASE WHEN RS2.DOC_SIIF IS NOT NULL THEN RS2.DOC_SIIF ELSE '01' END      || PCK_DATOS.GL_SEPARADOR_COL || 
                                          RS2.NIT             || PCK_DATOS.GL_SEPARADOR_REG ;   
       END LOOP;  
    END LOOP CONCEPTOS; 

    MI_CADENA := MI_HOJADATOS || ',.HOJ.,' || MI_HOJACONCEPTOS || ',.HOJ.,' || MI_HOJADEDUCCIONES || ',.HOJ.,';

RETURN MI_CADENA;
END FC_INFORMACIONPROVISIONES;


FUNCTION FC_GENERARINFO_SIIF_PATRONAL
/*
    NAME              : FC_GENERAR_INFORMACION_SIIF
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 28/02/2018
    TIME              : 10:00 AM  
    SOURCE MODULE     : SYSMAN_SIIF_NOMINA_2017.12.01_UNIFICADAS_06122017_AGR_ANE
    MODIFIER          : JOSE PASCUAL GOMEZ
    DATE MODIFIED     : 17/07/2018
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : RETORNA EN UN CLOB LOS DATOS PARA GENERAR EL EXCEL ACERCA DE LA INFORMACION DE SIIF NOMINA
                        Se ajusta para que el llamado de las deducciones se realice una sola vez y se agrupen todas de una vez

      @NAME:    generarInformacionSiif
      @METHOD:  GET
  */
(
   UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
   UN_ANIOINICIAL       IN HISTORICOS.ANO%TYPE, 
   UN_MESINICIAL        IN HISTORICOS.MES%TYPE,
   UN_PERIODOINICIAL    IN HISTORICOS.PERIODO%TYPE,
   UN_ANIOFINAL         IN HISTORICOS.ANO%TYPE, 
   UN_MESFINAL          IN HISTORICOS.MES%TYPE,
   UN_PERIODOFINAL      IN HISTORICOS.PERIODO%TYPE,
   UN_FECHA             IN DATE,      
   UN_TIPODOC           IN VARCHAR2,
   UN_NUMERODOC         IN VARCHAR2,
   UN_CODIGOEXP         IN VARCHAR2, 
   UN_TEXTO             IN VARCHAR2,
   UN_NIVEL             IN VARCHAR2,
   UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB
AS
  MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
  MI_RESULTADO              CLOB;
  MI_RESULTADOCONCEPTOS     CLOB;
  MI_RESULTADODEDUCCIONES   CLOB;
  MI_CONSECUTIVO            PCK_SUBTIPOS.TI_ENTERO;
  MI_RUBROPUBLICO           PCK_SUBTIPOS.TI_PARAMETRO;
  MI_MESANIO                VARCHAR2(250); 
  MI_DEPENDENCIA            PCK_SUBTIPOS.TI_PARAMETRO;
BEGIN
      MI_RUBROPUBLICO:= NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                UN_NOMBRE    => 'RUBRO PRESUPUESTAL PENSION PUBLICA',
                                                UN_MODULO    => PCK_DATOS.FC_MODULONOMINA, 
                                                UN_FECHA_PAR => SYSDATE), ' ');
      MI_RUBROPUBLICO:= TRIM(REPLACE(REPLACE(MI_RUBROPUBLICO,'-',''),' ',''));

      MI_DEPENDENCIA:= NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                 UN_NOMBRE    => 'DEPENDENCIA ORIGEN NOMINA SIIF',
                                                 UN_MODULO    => PCK_DATOS.FC_MODULONOMINA, 
                                                 UN_FECHA_PAR => SYSDATE), '000');                                          
      MI_MESANIO := UPPER(PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MESINICIAL) || ' de ' || UN_ANIOINICIAL);
      MI_CONSECUTIVO := 0;
      FOR RS IN (SELECT  SUM(HISTORICOS.VALOR) TOTAL,
                        CASE WHEN V_FONDOS.COMPANIA IS NULL THEN CONCEPTOS.NITCONCEPTO     ELSE V_FONDOS.NIT          END NIT,
                        CASE WHEN V_FONDOS.COMPANIA IS NULL THEN TIPOS_DOCUMENTOS.DOC_SIIF ELSE TIPOFONDO.DOC_SIIF    END DOC_SIIF,
                        CASE WHEN CONCEPTOS.ID_DE_CONCEPTO IN(102, 103) THEN 'GR' ELSE 
                        CASE WHEN V_FONDOS.COMPANIA IS NULL THEN 'AC' ELSE 
                             CASE V_FONDOS.FORMA_PAGO WHEN 'G' THEN 'GR' 
                                                      WHEN 'A' THEN 'AC' 
                                                      WHEN 'I' THEN 'TT' 
                                                      WHEN 'H' THEN 'CH' ELSE
                                                                    'AC' END
                            END
                        END MEDIO_PAGO,    
                        CASE WHEN V_FONDOS.FORMA_PAGO IN('G') OR CONCEPTOS.ID_DE_CONCEPTO IN(102, 103) THEN NULL ELSE CASE WHEN V_FONDOS.COMPANIA   IS NULL THEN ' ' ELSE TIPOBANCO.DOC_SIIF END END     DOC_SIIF_BANCO,
                        CASE WHEN V_FONDOS.FORMA_PAGO IN('G') OR CONCEPTOS.ID_DE_CONCEPTO IN(102, 103)  THEN NULL ELSE BANCOFONDO.NIT_SIIF END NIT_BANCO,   
                        CASE WHEN V_FONDOS.FORMA_PAGO IN('G') OR CONCEPTOS.ID_DE_CONCEPTO IN(102, 103)  THEN NULL ELSE NVL(CASE WHEN V_FONDOS.TIPO_CUENTA = 'C'THEN 'CRR' ELSE 'AHR' END,'AHR') END  TIPOCUENTAFONDO,
                        CASE WHEN V_FONDOS.FORMA_PAGO IN('G') OR CONCEPTOS.ID_DE_CONCEPTO IN(102, 103)  THEN NULL ELSE V_FONDOS.CUENTA_CONSIGNAR END CUENTAFONDO,    
                        CASE WHEN NVL(V_FONDOS.PUBLICO,0) = 0 OR CONCEPTOS.ID_DE_CONCEPTO IN(120,132,127)
                             THEN CONCEPTOS.RUBROS_SIIF    
                             ELSE MI_RUBROPUBLICO END RUBROS_SIIF,
                        CASE WHEN V_FONDOS.COMPANIA IS NULL 
                             THEN CONCEPTOS.NOMBRE_CONCEPTO
                             ELSE V_FONDOS.NOMBRE_FONDO
                        END JUSTIFICACION,

                        CASE WHEN NVL(CONCEPTOS.SIIF_ATRIBUTOPATRON, ' ') <> ' '
                              THEN CONCEPTOS.SIIF_ATRIBUTOPATRON 
                              ELSE V_FONDOS.RUBRO_SIIF_PATRONO  END SIIF_ATRIBUTOPATRON
                    FROM PERSONAL INNER JOIN HISTORICOS
                       ON HISTORICOS.COMPANIA       = PERSONAL.COMPANIA
                      AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                    INNER JOIN CONCEPTOS
                       ON HISTORICOS.COMPANIA       = CONCEPTOS.COMPANIA
                      AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
                    INNER JOIN PERIODOS
                       ON HISTORICOS.COMPANIA      = PERIODOS.COMPANIA
                      AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                      AND HISTORICOS.ANO           = PERIODOS.ANO
                      AND HISTORICOS.MES           = PERIODOS.MES
                      AND HISTORICOS.PERIODO       = PERIODOS.PERIODO
                    LEFT JOIN TIPOS_DOCUMENTOS
                      ON  CONCEPTOS.COMPANIA            =   TIPOS_DOCUMENTOS.COMPANIA
                      AND CONCEPTOS.DOC_SIIF_CONCEPTO   =   TIPOS_DOCUMENTOS.DCTO_IDENTIDAD                
                    LEFT JOIN  V_FONDOS
                        ON PERSONAL.COMPANIA    = V_FONDOS.COMPANIA
                       AND CASE WHEN CONCEPTOS.ID_DE_CONCEPTO IN(116)                                           --SALUD
                                THEN PERSONAL.FONDO_SALUD   
                                ELSE CASE WHEN CONCEPTOS.ID_DE_CONCEPTO IN(117)                                 --PENSION
                                          THEN PERSONAL.ID_DEL_FONDO 
                                          ELSE CASE WHEN CONCEPTOS.ID_DE_CONCEPTO IN(111)                       --RIESGOS
                                                    THEN PERSONAL.FONDO_RIESGOS 
                                                    ELSE
                                                        CASE WHEN CONCEPTOS.ID_DE_CONCEPTO IN(483)                       --CESANTIAS
                                                        THEN PERSONAL.FONDO_CESANTIAS
                                                        ELSE CASE WHEN CONCEPTOS.ID_DE_CONCEPTO IN(101)--PARAFISCALES
                                                                  THEN PERSONAL.CAJA_COMPENSACION 
                                                                  ELSE ' ' 
                                                             END
                                                     END        
                                               END         
                                      END
                            END = V_FONDOS.COD_FONDO
                    LEFT JOIN TIPOS_DOCUMENTOS TIPOFONDO
                       ON V_FONDOS.COMPANIA   = TIPOFONDO.COMPANIA
                      AND V_FONDOS.DOC_SIIF   = TIPOFONDO.DCTO_IDENTIDAD
                    LEFT JOIN BANCOS_NOMINA BANCOFONDO
                      ON V_FONDOS.COMPANIA      = BANCOFONDO.COMPANIA
                      AND V_FONDOS.BANCO_PAGO   = BANCOFONDO.BANCO 
                    LEFT JOIN TIPOS_DOCUMENTOS TIPOBANCO
                       ON BANCOFONDO.COMPANIA         = TIPOFONDO.COMPANIA
                      AND BANCOFONDO.DOC_SIIF_BANCO   = TIPOFONDO.DCTO_IDENTIDAD

                    WHERE PERSONAL.COMPANIA         = UN_COMPANIA   
                      AND PERIODOS.ACUMULADO NOT IN(0)
                      AND CONCEPTOS.CLASE = 8
                      AND PERSONAL.NIVELSIIF IN (CASE WHEN UN_NIVEL IS NOT NULL THEN UN_NIVEL ELSE PERSONAL.NIVELSIIF END)
                      AND       HISTORICOS.ANO || LPAD(HISTORICOS.MES,2, '0') || LPAD(HISTORICOS.PERIODO,2,'0') 
                        BETWEEN UN_ANIOINICIAL || LPAD(UN_MESINICIAL ,2, '0') || LPAD(UN_PERIODOINICIAL ,2,'0') 
                            AND UN_ANIOFINAL   || LPAD(UN_MESFINAL   ,2, '0') || LPAD(UN_PERIODOFINAL   ,2,'0')     
                            AND CONCEPTOS.ID_DE_CONCEPTO < 490 
                            AND CONCEPTOS.ID_DE_CONCEPTO NOT IN(119, 483)
                                --IN(101,102,103,104,105,111,116,117,483) 
                    GROUP BY CASE WHEN V_FONDOS.COMPANIA IS NULL THEN CONCEPTOS.NITCONCEPTO     ELSE V_FONDOS.NIT          END ,
                        CASE WHEN V_FONDOS.COMPANIA IS NULL THEN TIPOS_DOCUMENTOS.DOC_SIIF ELSE TIPOFONDO.DOC_SIIF    END ,
                        CASE WHEN CONCEPTOS.ID_DE_CONCEPTO IN(102, 103) THEN 'GR' ELSE 
                        CASE WHEN V_FONDOS.COMPANIA IS NULL THEN 'AC' ELSE 
                             CASE V_FONDOS.FORMA_PAGO WHEN 'G' THEN 'GR' 
                                                      WHEN 'A' THEN 'AC' 
                                                      WHEN 'I' THEN 'TT' 
                                                      WHEN 'H' THEN 'CH' ELSE
                                                                    'AC' END
                            END
                        END ,    
                        CASE WHEN V_FONDOS.FORMA_PAGO IN('G') OR CONCEPTOS.ID_DE_CONCEPTO IN(102, 103) THEN NULL ELSE CASE WHEN V_FONDOS.COMPANIA   IS NULL THEN ' ' ELSE TIPOBANCO.DOC_SIIF END END     ,
                        CASE WHEN V_FONDOS.FORMA_PAGO IN('G') OR CONCEPTOS.ID_DE_CONCEPTO IN(102, 103)  THEN NULL ELSE BANCOFONDO.NIT_SIIF END ,   
                        CASE WHEN V_FONDOS.FORMA_PAGO IN('G') OR CONCEPTOS.ID_DE_CONCEPTO IN(102, 103)  THEN NULL ELSE NVL(CASE WHEN V_FONDOS.TIPO_CUENTA = 'C'THEN 'CRR' ELSE 'AHR' END,'AHR') END  ,
                        CASE WHEN V_FONDOS.FORMA_PAGO IN('G') OR CONCEPTOS.ID_DE_CONCEPTO IN(102, 103)  THEN NULL ELSE V_FONDOS.CUENTA_CONSIGNAR END ,    
                        CASE WHEN NVL(V_FONDOS.PUBLICO,0) = 0 OR CONCEPTOS.ID_DE_CONCEPTO IN(120,132,127)
                             THEN CONCEPTOS.RUBROS_SIIF    
                             ELSE MI_RUBROPUBLICO END,
                        CASE WHEN V_FONDOS.COMPANIA IS NULL 
                             THEN CONCEPTOS.NOMBRE_CONCEPTO
                             ELSE V_FONDOS.NOMBRE_FONDO
                        END ,
                        CASE WHEN NVL(CONCEPTOS.SIIF_ATRIBUTOPATRON, ' ') <> ' '
                              THEN CONCEPTOS.SIIF_ATRIBUTOPATRON 
                              ELSE V_FONDOS.RUBRO_SIIF_PATRONO  END
                      )                       
      LOOP
          MI_CONSECUTIVO :=  MI_CONSECUTIVO + 1;
          MI_RESULTADO:= MI_RESULTADO || TO_CLOB( 
                         MI_CONSECUTIVO                     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         NVL(RS.DOC_SIIF,3)                 ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         NVL(RS.NIT     ,0)                 ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         NVL(RS.MEDIO_PAGO    ,'')          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         NVL(RS.DOC_SIIF_BANCO,'')          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         NVL(RS.NIT_BANCO,'')               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         NVL(RS.TIPOCUENTAFONDO,'')         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         NVL(RS.CUENTAFONDO,'')             ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         UN_CODIGOEXP                       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         NVL(RS.TOTAL ,0 )                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         NVL(RS.TOTAL ,0 )                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         '0'                                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         UN_FECHA                           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         UN_TIPODOC                         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         NVL(UN_NUMERODOC,'NÓMINA')  || ' ' || MI_MESANIO ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         UN_CODIGOEXP                       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         UN_TEXTO|| ' ' || MI_MESANIO|| ' ' || TRIM(REPLACE(RS.JUSTIFICACION,' - AUTOLIQUIDACION','')) ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         PCK_DATOS.GL_SEPARADOR_REG); 

          MI_RESULTADOCONCEPTOS := MI_RESULTADOCONCEPTOS || TO_CLOB( 
                         MI_CONSECUTIVO                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         MI_DEPENDENCIA                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         NVL(RS.RUBROS_SIIF         ,'') ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         NVL(RS.TOTAL,0)                 ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                         PCK_DATOS.GL_SEPARADOR_REG);
          MI_RESULTADODEDUCCIONES := MI_RESULTADODEDUCCIONES || TO_CLOB( 
                                       MI_CONSECUTIVO                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                       NVL(RS.SIIF_ATRIBUTOPATRON,'')  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                       NVL(RS.TOTAL,0)                 ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                       NVL(RS.DOC_SIIF   ,'')          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                       NVL(RS.NIT        ,'')          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                       PCK_DATOS.GL_SEPARADOR_REG);
      END LOOP;
      RETURN  MI_RESULTADO          || ',.HOJ.,' ||
              MI_RESULTADOCONCEPTOS || ',.HOJ.,' ||
              MI_RESULTADODEDUCCIONES;
END FC_GENERARINFO_SIIF_PATRONAL;


PROCEDURE PR_CALCULARPRIMANAVIDADANE
(
/*
NAME              : PR_CALCULARPRIMANAVIDADANE
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
DATE MIGRADOR     : 12/03/2018
TIME              :
SOURCE MODULE     : NOMINAP2018.02.04 En access calcularprimadenavidadANE
MODIFIER          : ANDREA CAROLINA PINEDA
DATE MODIFIED     : 24/11/2018
TIME              : 11:36 AM
DESCRIPTION       : Función para el cálculo de la prima de navidad periodo 01/12/ANIOANTERIOR y 30/11/ANIOACTUAL
@NAME:  CALCULARPRIMANAVIDADANE
*/

    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
	MI_FECHAIPNFACTORES DATE;
    MI_FECHAFPNFACTORES DATE;
    MI_TRANSPORTELEGAL  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_RETEFUENTE       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
	MI_ANIOANTERIOR     NUMBER;
	MI_CONCEPTOS        PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_FECHAIPNDIAS     DATE;
    MI_FECHAFPNDIAS     DATE;
BEGIN
    --(APINEDA:27/11/2018)-Se replantea función completa debido a que la ANE solicitó cambio en el rango de fechas (01/12/añoAnterior - 30/11/añoActual) del periodo para el calculo de la prima de navidad TAR1000088289
    PCK_NOMINA.GL_PVAC := 0;
	PCK_NOMINA.GL_FACTORPN := 0;
	PCK_NOMINA.GL_DNT := 0;	
    --Fechas inicio y final factores prima de navidad
	MI_ANIOANTERIOR := PCK_NOMINA.GL_SANO - 1;
	MI_FECHAIPNFACTORES := TO_DATE('01/12/' || MI_ANIOANTERIOR, 'DD/MM/YYYY');   
	PCK_NOMINA.GL_FECHAIPN := CASE WHEN PCK_NOMINA.GL_FECHAI > MI_FECHAIPNFACTORES THEN PCK_NOMINA.GL_FECHAI ELSE MI_FECHAIPNFACTORES END;
	MI_FECHAFPNFACTORES := TO_DATE('30/11/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');

    MI_FECHAIPNDIAS := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');   
	MI_FECHAIPNDIAS := CASE WHEN PCK_NOMINA.GL_FECHAI > MI_FECHAIPNDIAS THEN PCK_NOMINA.GL_FECHAI ELSE MI_FECHAIPNDIAS END;
	MI_FECHAFPNDIAS := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    IF PCK_NOMINA.FC_CN(404) <> 0 THEN
		MI_FECHAFPNFACTORES := PCK_NOMINA.GL_FECHAR;
        MI_FECHAFPNDIAS := PCK_NOMINA.GL_FECHAR;	
	END IF;	    

	--(APINEDA:24/11/2018)-Se borran líneas de código que hacían referencia a los empleados con ID_DE_TIPO 10 y 11 debido a que en ANE no existe ninguno de este tipo.
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '99' THEN
        PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(1);
        PCK_NOMINA.CN(930) := PCK_NOMINA.FC_CN(1);
    ELSE                
		--Obtiendo doceava de la Prima de Servicios					
		MI_CONCEPTOS(1).CLAVE := 'PRIMA JUNIO';
		MI_CONCEPTOS(1).VALOR := 160;		
		MI_CONCEPTOS(2).CLAVE := 'CODIGO CONCEPTO AJUSTES PRIMA SEMESTRAL';
		MI_CONCEPTOS(2).VALOR := TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO AJUSTES PRIMA SEMESTRAL', '503'));				
		PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_COM4.FC_VALORACUMCONCEPTOYAJUSTE(UN_ANO1  => MI_ANIOANTERIOR, 
																								  UN_MES1 	=> PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), 
																								  UN_PER1  => 1, 
																								  UN_ANO2  => PCK_NOMINA.GL_SANO,
																								  UN_MES2  => PCK_SYSMAN_UTL.FC_MES(MI_FECHAFPNFACTORES),
																								  UN_PER2  => PCK_NOMINA.GL_SPER,
																								  UN_IDDEEMPLEADO  => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,
																								  UN_CONCEPTOS => MI_CONCEPTOS) 
																								  / 12 , 0);																										                                                                                                         
		--Obtiendo doceava de Bonificacion Anual por Servicios Prestados(BASP)
		MI_CONCEPTOS.DELETE;
		MI_CONCEPTOS(1).CLAVE := 'BONIFICACIÓN POR SERVICIOS PRESTADOS';
		MI_CONCEPTOS(1).VALOR := 150;
		MI_CONCEPTOS(2).CLAVE := 'CODIGO CONCEPTO AJUSTES B.A.S.P.';
		MI_CONCEPTOS(2).VALOR := TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO AJUSTES B.A.S.P.', '514'));	
		PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_COM4.FC_VALORACUMCONCEPTOYAJUSTE(UN_ANO1  => MI_ANIOANTERIOR, 
																								  UN_MES1 	=> PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), 
																								  UN_PER1  => 1, 
																								  UN_ANO2  => PCK_NOMINA.GL_SANO,
																								  UN_MES2  => PCK_SYSMAN_UTL.FC_MES(MI_FECHAFPNFACTORES),
																								  UN_PER2  => PCK_NOMINA.GL_SPER,
																								  UN_IDDEEMPLEADO  => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,
																								  UN_CONCEPTOS => MI_CONCEPTOS) 
																								  / 12 , 0);																									  

		--Obteniendo valor de la prima de vacaciones 																							
		PCK_NOMINA.GL_PVAC := 0;
		MI_CONCEPTOS.DELETE;
		MI_CONCEPTOS(1).CLAVE := 'PRIMA DE VACACIONES';
		MI_CONCEPTOS(1).VALOR := 155;
		MI_CONCEPTOS(2).CLAVE := 'AJUSTES PRIMA DE VACACIONES';
		MI_CONCEPTOS(2).VALOR := TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO AJUSTES PRIMA DE VACACIONES', '501'));	
		MI_CONCEPTOS(3).CLAVE := 'RETROACTIVO PRIMA DE VACACIONES';
		MI_CONCEPTOS(3).VALOR := 541;			
		PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_COM4.FC_VALORACUMCONCEPTOYAJUSTE(UN_ANO1  => MI_ANIOANTERIOR, 
																								  UN_MES1 	=> PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), 
																								  UN_PER1  => 1, 
																								  UN_ANO2  => PCK_NOMINA.GL_SANO,
																								  UN_MES2  => PCK_SYSMAN_UTL.FC_MES(MI_FECHAFPNFACTORES),
																								  UN_PER2  => PCK_NOMINA.GL_SPER,
																								  UN_IDDEEMPLEADO  => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,
																								  UN_CONCEPTOS => MI_CONCEPTOS) 
																								  , 0);                                                                                                     

		PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(MI_FECHAIPNDIAS), 1, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(MI_FECHAFPNDIAS), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
		PCK_NOMINA.GL_DNT := (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));										
		PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPNDIAS, MI_FECHAFPNDIAS) - PCK_NOMINA.GL_DNT;			
		PCK_NOMINA.GL_FACTORPN := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) > 1 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(931), 0) + PCK_NOMINA.FC_CN(939) + PCK_NOMINA.GL_VPT;
		PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);

        IF PCK_NOMINA.FC_CN(404) <> 0 THEN                                                
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPNFACTORES);
            FOR I IN 0..CASE WHEN PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO) = PCK_NOMINA.GL_SMES THEN PCK_NOMINA.GL_SMES - 1 ELSE PCK_NOMINA.GL_SMES END LOOP
				IF I = 0 THEN
					PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, MI_ANIOANTERIOR, 12, 1, MI_ANIOANTERIOR, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
				ELSE
					PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, I, 1, PCK_NOMINA.GL_SANO, I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
				END IF;
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                END IF;
            END LOOP;
        END IF;

		IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN                
			IF PCK_NOMINA.FC_CN(158) = 0 THEN
				PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
			END IF;
            --(APINEDA:20/12/2018)-Se agrega validación para no restar el acumulado del 158 en el beneficio, solo en la liquidación final.
			IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 AND NOT PCK_NOMINA.GL_ESBONIFICACION THEN
				PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
				PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
			END IF;
		ELSE
			IF PCK_NOMINA.FC_CN(158) = 0 THEN
				PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
			END IF;
            --(APINEDA:20/12/2018)-Se agrega validación para no restar el acumulado del 158 en el beneficio, solo en la liquidación final.
			IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_SPER = 7 AND NOT PCK_NOMINA.GL_ESBONIFICACION THEN
				PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

				PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
				PCK_NOMINA.CN(158) := CASE WHEN PCK_NOMINA.FC_CN(158) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(158) END;
			END IF;
		END IF;			
    END IF;
    PCK_NOMINA.GL_PRIMADIC := PCK_NOMINA.FC_CN(158);    
    MI_TRANSPORTELEGAL := PCK_NOMINA.FC_CN(254);
    MI_RETEFUENTE := PCK_NOMINA.FC_CN(125);
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR I IN 2 .. 599 LOOP
            IF ((I <> 125) AND (I <> 160) AND (I <> 67) AND (I <> 404)) AND (I < 599) OR (I >= 600 AND I <= 798) AND (PCK_NOMINA.FC_CN(I) > 0 AND PCK_NOMINA.FC_CN(I) < 1) THEN
                PCK_NOMINA.CN(I) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(125) := MI_RETEFUENTE;
    PCK_NOMINA.CN(254) := MI_TRANSPORTELEGAL;
    PCK_NOMINA.CN(158) := PCK_NOMINA.GL_PRIMADIC;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS ;
    PCK_NOMINA.CN(930) := PCK_NOMINA.FC_CN(1)   ;
    IF PCK_NOMINA.GL_SANO = 2003 THEN
        PCK_NOMINA.CN(933) := 0;
        PCK_NOMINA.CN(934) := 0;
    ELSE
        PCK_NOMINA.CN(933) := PCK_NOMINA.GL_AUXT ;
        PCK_NOMINA.CN(934) := PCK_NOMINA.GL_AUXA;
    END IF;
    PCK_NOMINA.CN(936) := PCK_NOMINA.FC_CN(67);
    PCK_NOMINA.CN(937) := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.CN(938) := PCK_NOMINA.GL_DNT;
    PCK_NOMINA.CN(940) := PCK_NOMINA.GL_VPT;
    --(APINEDA:20/09/2018)-Se asigna el valor de FACTORES PRIMA DE NAVIDAD al concepto 988 utilizado en informe resolución liquidación final ANE.
    PCK_NOMINA.CN(988) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPN), 0);
END PR_CALCULARPRIMANAVIDADANE;

FUNCTION FC_CARGAREXCELOCCIRED
  /*
      NAME              : FC_CARGAREXCELOCCIRED
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
      DATE MIGRADOR     : 12/04/2018
      TIME              : 12:40 PM
      SOURCE MODULE     : NOMINAP2018.03.03_UNIFICADAS MPV 26032018_MPV - 432_PYC
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PERMITE OBTENER LOS DATOS PARA LA PLANTILLA DE SISTEMAOCCIREDV25.xls
      PARAMETERS        : 
      MODIFICATIONS     : CORRECCIÃ“N SEGUN ESTANDAR DE PROGRAMACION Y OPTIMIZACION DEL MANEJO DE ERRORES.
     @NAME: cargarExcelOccred
    */
(
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO          IN HISTORICOS.ID_DE_PROCESO%TYPE,
    UN_ANIO             IN HISTORICOS.ANO%TYPE,
    UN_MES              IN HISTORICOS.MES%TYPE,
    UN_PERIODO          IN HISTORICOS.PERIODO%TYPE,
    UN_BANCO            IN BANCO.BANCO%TYPE,
    UN_CONSECUTIVO      IN PCK_SUBTIPOS.TI_TABLA,
    UN_FECHAREPORTE     IN DATE,
    UN_VERSION          IN PCK_SUBTIPOS.TI_LOGICO, 
    UN_INFORME          IN PCK_SUBTIPOS.TI_ENTERO,
    UN_COMPROBANTE      IN PCK_SUBTIPOS.TI_TABLA
)
RETURN CLOB
AS
MI_CADENA         CLOB;
MI_CUENTA         BANCOS_NOMINA.CUENTA%TYPE;
MI_NUM            PCK_SUBTIPOS.TI_ENTERO;
MI_VALORTOTAL     HISTORICOS.VALOR%TYPE;
MI_CANTIDAD       PCK_SUBTIPOS.TI_ENTERO;
MI_FORMAPAGO      PCK_SUBTIPOS.TI_ENTERO;
MI_CUENTAPOCRED   VARCHAR2(2); --JM_INI 18/102024 7800559 
MI_NROCUENTAPORED VARCHAR2(9);
BEGIN
    --JM_INI 18/102024 7800559 
    MI_NROCUENTAPORED := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                                     UN_NOMBRE    => 'CUENTA PRINCIPAL OCCIRED 9 DIGITOS',
                                                     UN_MODULO    => 6, 
                                                     UN_FECHA_PAR => SYSDATE), '');
                                                     
    MI_CUENTAPOCRED := CASE WHEN NVL(LENGTH(MI_NROCUENTAPORED),0) = 9 THEN 'SI' ELSE 'NO' END;
    --JM_FIN 18/102024 7800559        
    IF UN_INFORME = 0 OR UN_INFORME = 3 THEN 
        IF MI_CUENTAPOCRED = 'SI' THEN  --JM_INI 18/102024 7800559 
              BEGIN 
                SELECT DISTINCT BANCOS_NOMINA.CUENTA
                INTO MI_CUENTA 
                FROM BANCOS_NOMINA
                WHERE BANCOS_NOMINA.COMPANIA   = UN_COMPANIA AND (BANCO = '023' OR BANCO = '23');
                  EXCEPTION WHEN NO_DATA_FOUND THEN
                      MI_CUENTA:='';
              END;
          END IF; --JM_FIN 18/102024 7800559 

        FOR RS IN (SELECT CASE WHEN PERSONAL.APELLIDOS_TUTOR IS NOT NULL AND PERSONAL.ID_DE_TIPO = '99' THEN TRIM(PERSONAL.APELLIDOS_TUTOR || ' '|| PERSONAL.NOMBRES_TUTOR) else TRIM(PERSONAL.apellido1||' '|| PERSONAL.apellido2 ||' '|| PERSONAL.nombres) END  AS NOMBRECOMPLETO, --JM 10/25/2024
                          HISTORICOS.COMPANIA,
                          HISTORICOS.ID_DE_PROCESO,
                          HISTORICOS.ANO,
                          HISTORICOS.MES,
                          HISTORICOS.PERIODO,
                          TIPOS_DOCUMENTOS.SIGLA2,
                          TIPOS_DOCUMENTOS.DESCRIPCION,                          
                          HISTORICOS.ID_DE_EMPLEADO,
                          HISTORICOS.ID_DE_CONCEPTO,
                          HISTORICOS.VALOR,
                          BANCO.CODIGO_ENTIDAD||' - '||BANCO.NOMBRE BANCO, --7733365_NOMINA (12/12/2023 lvguaba) --JM 10/25/2024
                          BANCO.CODIGO_ENTIDAD CODIGOBANCO, --7733365_NOMINA (06/12/2023 lvguaba) --JM 10/25/2024
                          CASE WHEN PERSONAL.TIPOCUENTA = 'A' THEN 'Cuenta Ahorros' else 'Cuenta Corriente' end as TIPOCUENTA,
                          PERSONAL.CUENTA CUENTAEMPLEADO,
                          CASE WHEN PERSONAL.CEDULA_TUTOR <> 0 AND PERSONAL.ID_DE_TIPO = '99' THEN PERSONAL.CEDULA_TUTOR ELSE PERSONAL.NUMERO_DCTO END AS NUMERO_DCTO, --7733365_NOMINA (06/12/2023 lvguaba) --JM 10/25/2024
                          BANCO.BANCO ID_BANCO
                    FROM HISTORICOS
                      INNER JOIN PERSONAL
                        ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                        AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                      INNER JOIN BANCOS_NOMINA BANCO
                        ON PERSONAL.COMPANIA = BANCO.COMPANIA
                        AND PERSONAL.BANCO   = BANCO.BANCO
                      INNER JOIN TIPOS_DOCUMENTOS
                        ON TIPOS_DOCUMENTOS.COMPANIA = PERSONAL.COMPANIA
                        AND TIPOS_DOCUMENTOS.DCTO_IDENTIDAD  = PERSONAL.DCTO_IDENTIDAD
                    WHERE HISTORICOS.COMPANIA     = UN_COMPANIA
                      AND HISTORICOS.ID_DE_PROCESO  = UN_PROCESO
                      AND HISTORICOS.ANO            = UN_ANIO
                      AND HISTORICOS.MES            = UN_MES
                      AND HISTORICOS.PERIODO        = UN_PERIODO
                      AND HISTORICOS.ID_DE_CONCEPTO = '144'
                      AND PERSONAL.BANCO = CASE WHEN UN_BANCO <> ' ' THEN UN_BANCO ELSE PERSONAL.BANCO END
                    ORDER BY NOMBRECOMPLETO)
      LOOP
          MI_CADENA := MI_CADENA || ''  || PCK_DATOS.GL_SEPARADOR_COL ||
          TO_CHAR(EXTRACT(YEAR FROM   UN_FECHAREPORTE))          || PCK_DATOS.GL_SEPARADOR_COL ||
          TO_CHAR(EXTRACT(MONTH FROM  UN_FECHAREPORTE))          || PCK_DATOS.GL_SEPARADOR_COL ||
          TO_CHAR(EXTRACT(DAY FROM    UN_FECHAREPORTE))          || PCK_DATOS.GL_SEPARADOR_COL ||
          RS.NOMBRECOMPLETO || PCK_DATOS.GL_SEPARADOR_COL;                                     
          IF UN_INFORME = 0 AND MI_CUENTAPOCRED = 'NO' THEN --JM 18/102024 7800559 
            MI_CADENA := MI_CADENA || RS.SIGLA2 || PCK_DATOS.GL_SEPARADOR_COL || RS.DESCRIPCION || PCK_DATOS.GL_SEPARADOR_COL;
          END IF;
          MI_CADENA := MI_CADENA || RS.NUMERO_DCTO || PCK_DATOS.GL_SEPARADOR_COL || UN_CONSECUTIVO;

          MI_FORMAPAGO := CASE RS.CODIGOBANCO  --7733365_NOMINA (06/12/2023 lvguaba)
                            WHEN '99' THEN 1 
                            WHEN '23' THEN 2
                            WHEN '023' THEN 2 
                            ELSE 3  END;

          MI_CADENA := MI_CADENA || PCK_DATOS.GL_SEPARADOR_COL || CASE MI_FORMAPAGO WHEN 1 THEN 'PAGO EN CHEQUE' WHEN 2 THEN 'Abono cuenta Banco de Occidente' ELSE 'Abono cuenta otras entidades' END || PCK_DATOS.GL_SEPARADOR_COL;
          IF UN_INFORME = 0 AND MI_CUENTAPOCRED = 'NO' THEN --JM 18/102024 7800559 
            MI_CADENA := MI_CADENA || TO_CHAR(CASE MI_FORMAPAGO WHEN 1 THEN 'PAGO EN CHEQUE' WHEN 2 THEN 'ABONO A CUENTA DEL BANCO DE OCCIDENTE' ELSE 'ABONO A CUENTA DE OTRAS ENTIDADES' END) || PCK_DATOS.GL_SEPARADOR_COL;
          END IF;
          MI_CADENA := MI_CADENA || TO_CHAR(RS.VALOR) || PCK_DATOS.GL_SEPARADOR_COL ||
                                    CASE  RS.ID_BANCO WHEN '023' THEN 'PAGO DE NOMINA' ELSE 'PAGO DE NOMINA' END  || PCK_DATOS.GL_SEPARADOR_COL || 
                                    RS.BANCO          || PCK_DATOS.GL_SEPARADOR_COL ||   
                                    CASE RS.CODIGOBANCO 
                                      WHEN '99' THEN  ''
                                      ELSE REPLACE(REPLACE(RS.CUENTAEMPLEADO,'-',''), '.','') END     || PCK_DATOS.GL_SEPARADOR_COL ||
                                    CASE RS.CODIGOBANCO 
                                      WHEN '99' THEN  ''
                                      ELSE RS.TIPOCUENTA END                                          || PCK_DATOS.GL_SEPARADOR_REG;
      END LOOP;
    ELSIF UN_INFORME = 1 THEN  
      FOR RS IN (SELECT BANCOS_NOMINA.CUENTA CUENTAORIGEN,
                        EMBARGOS.DEMANDANTE,
                        EMBARGOS.DEMANDANTE_NOMBRES,
                        EMBARGOS.CEDULA,
                        EMBARGOS.BANCO,
                        CASE WHEN EMBARGOS.VR_CUOTA = 0 THEN  HISTORICOS.VALOR ELSE EMBARGOS.VR_CUOTA END VALOR_PAGAR, 
                        EMBARGOS.CUENTA,
                        BANCOS_NOMINA.CODIGO_ENTIDAD, 
                        PERSONAL.NOMBRECOMPLETO 
                  FROM EMBARGOS
                    LEFT JOIN PARAMETROS_DE_ENTRADA
                      ON EMBARGOS.COMPANIA = PARAMETROS_DE_ENTRADA.COMPANIA
                    LEFT JOIN PERSONAL
                      ON EMBARGOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                      AND EMBARGOS.COMPANIA      = PERSONAL.COMPANIA
                    LEFT JOIN BANCO
                      ON EMBARGOS.COMPANIA = BANCO.COMPANIA
                      AND EMBARGOS.BANCO   = BANCO.BANCO
                    LEFT JOIN BANCOS_NOMINA
                      ON BANCO.COMPANIA            = BANCOS_NOMINA.COMPANIA
                      AND BANCO.BANCO              = BANCOS_NOMINA.BANCO
                    LEFT JOIN HISTORICOS 
                      ON  HISTORICOS.COMPANIA = EMBARGOS.COMPANIA       
                     AND HISTORICOS.ID_DE_EMPLEADO = EMBARGOS.ID_DE_EMPLEADO   
                     AND HISTORICOS.ID_DE_PROCESO = EMBARGOS.ID_DE_PROCESO    
                     AND HISTORICOS.ANO = EMBARGOS.ANO             
                     AND HISTORICOS.MES = EMBARGOS.MES             
                     AND HISTORICOS.PERIODO =  EMBARGOS.PERIODO  
                     AND HISTORICOS.ID_DE_CONCEPTO = EMBARGOS.ID_DE_CONCEPTO 
                  WHERE EMBARGOS.COMPANIA       =   UN_COMPANIA
                    AND PERSONAL.ID_DE_EMPLEADO   <>  '00000'
                    AND EMBARGOS.ID_DE_PROCESO    =   UN_PROCESO
                    AND EMBARGOS.ANO              =   UN_ANIO
                    AND EMBARGOS.MES              =   UN_MES
                    AND EMBARGOS.PERIODO          =   UN_PERIODO
                    AND EMBARGOS.CUENTA IS NOT NULL)
      LOOP
      
          MI_CADENA := MI_CADENA || RS.CUENTAORIGEN                               || PCK_DATOS.GL_SEPARADOR_COL ||
                                    ltrim(rtrim(EXTRACT(YEAR FROM   UN_FECHAREPORTE)))          || PCK_DATOS.GL_SEPARADOR_COL ||
                                    ltrim(rtrim(to_char(TO_DATE(EXTRACT(MONTH FROM  UN_FECHAREPORTE), 'MM'),'MONTH','NLS_DATE_LANGUAGE=SPANISH'))) || PCK_DATOS.GL_SEPARADOR_COL ||
                                    EXTRACT(DAY FROM    UN_FECHAREPORTE)          || PCK_DATOS.GL_SEPARADOR_COL ||
                                    RS.DEMANDANTE|| ' ' || RS.DEMANDANTE_NOMBRES  || PCK_DATOS.GL_SEPARADOR_COL ||
                                    RS.CEDULA                                     || PCK_DATOS.GL_SEPARADOR_COL ||
                                    RS.VALOR_PAGAR                                || PCK_DATOS.GL_SEPARADOR_COL ||
                                    CASE WHEN RS.BANCO IN ('39') THEN '040'
                                         ELSE RS.BANCO END                        || PCK_DATOS.GL_SEPARADOR_COL ||
                                    REPLACE(REPLACE(RS.CUENTA,'-',''), '.','')    || PCK_DATOS.GL_SEPARADOR_COL ||
                                    CASE WHEN RS.BANCO IN ('23') THEN 'A'
                                         WHEN RS.BANCO IN ('00','99') THEN ' '
                                         ELSE 'A' END                             || PCK_DATOS.GL_SEPARADOR_REG;
      END LOOP;
  ELSIF UN_INFORME = 2 THEN  
      BEGIN --JM_INI 18/102024 7800559 
          IF MI_CUENTAPOCRED = 'SI' THEN 
              SELECT DISTINCT BANCOS_NOMINA.CUENTA
              INTO MI_CUENTA 
              FROM BANCOS_NOMINA
              WHERE BANCOS_NOMINA.COMPANIA   = UN_COMPANIA AND (BANCO = '023' OR BANCO = '23');
            ELSE --JM_FIN 18/102024 7800559 
              SELECT DISTINCT BANCOS_NOMINA.CUENTA
              INTO MI_CUENTA
              FROM BANCO
              INNER JOIN BANCOS_NOMINA
                ON BANCO.COMPANIA             = BANCOS_NOMINA.COMPANIA
                AND BANCO.BANCO               = BANCOS_NOMINA.BANCO
              LEFT JOIN PARAMETROS_DE_ENTRADA
                ON BANCOS_NOMINA.COMPANIA       = PARAMETROS_DE_ENTRADA.COMPANIA
              WHERE BANCOS_NOMINA.COMPANIA   = UN_COMPANIA
                AND BANCOS_NOMINA.BANCO      = UN_BANCO;
          END IF;
            EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CUENTA:='';
      END;
       SELECT SUM (HISTORICOS.VALOR),
              COUNT (1) NUMERO
       INTO MI_VALORTOTAL, 
              MI_CANTIDAD
       FROM HISTORICOS
          INNER JOIN PERSONAL
            ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
            AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
          INNER JOIN BANCO
            ON PERSONAL.COMPANIA = BANCO.COMPANIA
            AND PERSONAL.BANCO   = BANCO.BANCO
          INNER JOIN BANCOS_NOMINA
            ON BANCO.COMPANIA             = BANCOS_NOMINA.COMPANIA
            AND BANCO.BANCO               = BANCOS_NOMINA.BANCO
        WHERE HISTORICOS.COMPANIA     = UN_COMPANIA
          AND HISTORICOS.ID_DE_PROCESO  = UN_PROCESO
          AND HISTORICOS.ANO            = UN_ANIO
          AND HISTORICOS.MES            = UN_MES
          AND HISTORICOS.PERIODO        = UN_PERIODO
          AND HISTORICOS.ID_DE_CONCEPTO = '144'
          AND PERSONAL.BANCO            <> '00'
          AND PERSONAL.BANCO            < '98'
        ORDER BY PERSONAL.APELLIDO1
          || ' '
          || PERSONAL.APELLIDO2
          || ' '
          || PERSONAL.NOMBRES; 
    MI_CADENA :=  '10000' || TO_CHAR(UN_FECHAREPORTE, 'YYYYMMDD')         || 
                  PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CANTIDAD, 
                                            UN_LONGITUD => 4)             ||
                  PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_VALORTOTAL, 
                                            UN_LONGITUD => 16)            ||                  
                  '00'                                                    ||                                         
                  CASE WHEN MI_CUENTAPOCRED = 'SI' THEN --JM_INI 18/102024 7800559 
                            LPAD(NVL(MI_NROCUENTAPORED,'0'),16,0) 
                        ELSE --JM_FIN 18/102024 7800559 
                            PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => NVL(MI_CUENTA,'0'), 
                                            UN_LONGITUD => 16) END        || 
                  PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO => UN_CONSECUTIVO, 
                                            UN_LONGITUD =>6)              || 
                  LPAD('0',142,'0') || CHR(10);
    MI_NUM :=1;
    FOR RS IN (SELECT PERSONAL.APELLIDO1
                          || ' '
                          || PERSONAL.APELLIDO2
                          || ' '
                          || PERSONAL.NOMBRES NOMBRECOMPLETO,
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
                          PERSONAL.CUENTA CUENTAEMPLEADO,
                          BANCOS_NOMINA.CUENTA CUENTAORIGEN,
                          BANCOS_NOMINA.CODIGO_ENTIDAD,
                          PERSONAL.NUMERO_DCTO
                    FROM HISTORICOS
                      INNER JOIN PERSONAL
                        ON HISTORICOS.COMPANIA        = PERSONAL.COMPANIA
                        AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                      INNER JOIN BANCO
                        ON PERSONAL.COMPANIA = BANCO.COMPANIA
                        AND PERSONAL.BANCO   = BANCO.BANCO
                      INNER JOIN BANCOS_NOMINA
                        ON BANCO.COMPANIA             = BANCOS_NOMINA.COMPANIA
                        AND BANCO.BANCO               = BANCOS_NOMINA.BANCO
                    WHERE HISTORICOS.COMPANIA     = UN_COMPANIA
                      AND HISTORICOS.ID_DE_PROCESO  = UN_PROCESO
                      AND HISTORICOS.ANO            = UN_ANIO
                      AND HISTORICOS.MES            = UN_MES
                      AND HISTORICOS.PERIODO        = UN_PERIODO
                      AND HISTORICOS.ID_DE_CONCEPTO = '144'
                      AND PERSONAL.BANCO            <> '00'
                      AND PERSONAL.BANCO            < '98'
                    ORDER BY PERSONAL.APELLIDO1
                      || ' '
                      || PERSONAL.APELLIDO2
                      || ' '
                      || PERSONAL.NOMBRES)
      LOOP
           --(APINEDA:17/10/2018)-Se realiza ajuste por error de conversiÃ³n presentado en UPC
           MI_CADENA := MI_CADENA || '2' || 
                        PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_NUM, 
                                                  UN_LONGITUD => 4)                         ||
                        PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CUENTA, 
                                                  UN_LONGITUD => 16)                        || 
                        RPAD(PCK_NOMINA_COM5.FC_QUITAESPECIALES(RS.NOMBRECOMPLETO),30)      ||
                        PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.NUMERO_DCTO, 
                                                  UN_LONGITUD => 11)                        ||
                        PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => NVL(RS.CODIGO_ENTIDAD,''), 
                                                  UN_LONGITUD => 4)                         ||
                        TO_CHAR(UN_FECHAREPORTE, 'YYYYMMDD')                                || 
                        TO_CHAR(CASE RS.BANCO 
                          WHEN '99' THEN 1 
                          ELSE CASE WHEN RS.BANCO='23' THEN 2 ELSE 3 END 
                          END)                                                              ||
                        PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.VALOR, 
                                                  UN_LONGITUD => 13)                        ||
                        '00'                                                                ||
                        RPAD(REPLACE(REPLACE(NVL(RS.CUENTAEMPLEADO,''),'-',''), '.',''),16) ||
                        LPAD(UN_COMPROBANTE,12,' ')                                         ||
                        CASE WHEN RS.BANCO IN ('99') THEN 'A'
                             ELSE CASE WHEN RS.TIPOCUENTA IN ('A') THEN 'A' ELSE 'C' END 
                             END                                                            ||
                        RPAD('NOMINA',80,' ') || CHR(10); 
            MI_NUM := MI_NUM +1;
      END LOOP;
      MI_CADENA := MI_CADENA || '3' || '9999' ||  PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CANTIDAD, 
                                                                            UN_LONGITUD => 4)              ||
                                                  PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_VALORTOTAL, 
                                                                            UN_LONGITUD => 16)             ||
                                                  '00'                                                     ||
                                                  LPAD('0',172,'0');
  END IF;
  RETURN CASE WHEN MI_CUENTAPOCRED = 'SI' AND  (UN_INFORME = 0 OR UN_INFORME = 3)  THEN MI_CUENTA||PCK_DATOS.GL_SEPARADOR_REG||MI_CADENA ELSE MI_CADENA END;--JM 18/102024 7800559 
END FC_CARGAREXCELOCCIRED;

PROCEDURE PR_CALCULARBONIFICACIONES

/*
NAME              : PR_CALCULARBONIFICACIONES
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
DATE MIGRADOR     : 09/05/2018
TIME              : 03:42 PM
SOURCE MODULE     : SERVIDORNOMINA2010, Version UPC
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       :
@NAME:
*/

AS
    MI_GRPNGV1      PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_BASESUELDO   NUMBER DEFAULT 0;
BEGIN
    IF PCK_NOMINA.GL_SPER = 4 THEN
        MI_BASESUELDO := PCK_NOMINA.FC_CN(1);
    ELSE
        MI_BASESUELDO := PCK_NOMINA.FC_CN(2);
    END IF;
    IF PCK_PARST.FC_PAR('CALCULO BONIFICACIONES ESPECIALES', 'NO') = 'SI' THEN
        IF (PCK_PARST.FC_PAR('LIQUIDAN GASTOS DE REPRESENTACION', 'NO') = 'SI' OR PCK_NOMINA.FC_CN(210) > 0) AND  PCK_NOMINA.FC_CN(62) = 0  THEN
            MI_GRPNGV1 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) = 0 THEN MI_BASESUELDO * PCK_NOMINA.FC_CN(13) / 100 ELSE PCK_NOMINA.FC_CN(10) * PCK_NOMINA.FC_CN(15) / 100 END, 0);
        END IF;

        -- PRIMA DE COORDINACION
    	IF PCK_NOMINA.FC_CN(433) = 1 THEN
            IF PCK_NOMINA.FC_CN(166) = 0 THEN
                PCK_NOMINA.CN(166) := PCK_SYSMAN_UTL.FC_ROUND(MI_BASESUELDO * TO_NUMBER(PCK_PARST.FC_PAR('PORCENTAJE PRIMA DE COORDINACION', '20')) / 100, 0);
            END IF;
    	END IF;

        --BONIFICACION POR ESPECIALIZACION
    	IF PCK_NOMINA.FC_CN(430) <> 0 AND PCK_NOMINA.FC_CN(197) = 0 THEN
    		PCK_NOMINA.CN(197) := PCK_SYSMAN_UTL.FC_ROUND(((MI_BASESUELDO + MI_GRPNGV1 + PCK_NOMINA.FC_CN(515) + PCK_NOMINA.FC_CN(516) + NVL(PCK_NOMINA.FC_CN(529), 0) + NVL(PCK_NOMINA.FC_CN(560), 0) + PCK_NOMINA.FC_CN(379))
                * TO_NUMBER(PCK_PARST.FC_PAR('PORCENTAJE BONIFICACION POR ESPECIALIZACION', '10')) / 100), 0);
    		PCK_NOMINA.GL_BONESPMADOC := PCK_NOMINA.FC_CN(197);
    	END IF;

        --LIQUIDAR BONIFICACION POR MAESTRIA
    	IF PCK_NOMINA.FC_CN(431) <> 0 AND PCK_NOMINA.FC_CN(198) = 0 THEN
    		PCK_NOMINA.CN(198) := PCK_SYSMAN_UTL.FC_ROUND((MI_BASESUELDO + MI_GRPNGV1 + PCK_NOMINA.FC_CN(515) + NVL(PCK_NOMINA.FC_CN(529), 0) + NVL(PCK_NOMINA.FC_CN(560), 0) + PCK_NOMINA.FC_CN(516) + PCK_NOMINA.FC_CN(379))
                * TO_NUMBER(PCK_PARST.FC_PAR('PORCENTAJE BONIFICACION MAESTRIA', '15')) / 100, 0);
    		PCK_NOMINA.GL_BONESPMADOC := PCK_NOMINA.FC_CN(198);
    	END IF;

        --BONIFICACION POR DOCTORADO
        IF PCK_NOMINA.FC_CN(432) <> 0 AND PCK_NOMINA.FC_CN(199) = 0 THEN
            PCK_NOMINA.CN(199) := PCK_SYSMAN_UTL.FC_ROUND((MI_BASESUELDO + MI_GRPNGV1 + PCK_NOMINA.FC_CN(515) + NVL(PCK_NOMINA.FC_CN(529), 0) + NVL(PCK_NOMINA.FC_CN(560), 0) + PCK_NOMINA.FC_CN(516) + PCK_NOMINA.FC_CN(379))
                * TO_NUMBER(PCK_PARST.FC_PAR('PORCENTAJE BONIFICACION DOCTORADO', '20')) / 100, 0);
            PCK_NOMINA.GL_BONESPMADOC := PCK_NOMINA.FC_CN(199);
        END IF;

        --PRIMA TECNICA
        PCK_NOMINA.GL_VPT := 0;
        IF PCK_NOMINA.FC_CN(408) <> 0 AND PCK_NOMINA.FC_CN(186) = 0 THEN
            PCK_NOMINA.CN(85) := PCK_NOMINA.FC_CN(85) + CASE WHEN PCK_NOMINA.FC_CN(430) <> 0 THEN 3 ELSE 0 END + CASE WHEN PCK_NOMINA.FC_CN(431) <> 0 THEN 9 ELSE 0 END + CASE WHEN PCK_NOMINA.FC_CN(432) <> 0 THEN 15 ELSE 0 END;
            PCK_NOMINA.CN(186) := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE (MI_BASESUELDO + MI_GRPNGV1) END * PCK_NOMINA.FC_CN(85) / 100 , 0);
            PCK_NOMINA.GL_VPT := PCK_NOMINA.FC_CN(186);
        END IF;

        --CALCULO CONVENCION 3%

        IF INSTR(PCK_PARST.FC_PAR('CARGOS CALCULO CONVENCION 3%', ' '), PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CARGO) > 0 AND PCK_NOMINA.FC_CN(196) = 0 AND PCK_NOMINA.FC_CN(397) = 1 THEN
            PCK_NOMINA.CN(196) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) * 3 / 100, 0);
        END IF;

        --BONIFICACION BIENESTAR
    	IF PCK_NOMINA.FC_CN(421) = 1 THEN
            PCK_NOMINA.CN(193) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + NVL(PCK_NOMINA.FC_CN(529), 0) + NVL(PCK_NOMINA.FC_CN(560), 0)) * 2 / 100, 0);
      END IF;
    END IF;
END PR_CALCULARBONIFICACIONES;

PROCEDURE PR_CALCULARBONIFICACIONESUPC
/*
NAME              : PR_CALCULARBONIFICACIONESUPC
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : JUAN DANILO ORDUZ RIVERO
DATE MIGRADOR     : 23/04/2020
TIME              : 03:42 PM
SOURCE MODULE     : 
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       :
@NAME:
*/

AS
    MI_BASESUELDO   NUMBER DEFAULT 0;
BEGIN

    MI_BASESUELDO := PCK_NOMINA.FC_CN(201);

    IF PCK_PARST.FC_PAR('CALCULO BONIFICACIONES G.INVESTIGACION Y POSTGRADOS UPC','NO') = 'SI' THEN 
        --BONIFICACION POR ESPECIALIZACION
        IF PCK_NOMINA.FC_CN(460) = 1 AND PCK_NOMINA.FC_CN(197) = 0 THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '12' THEN
                PCK_NOMINA.CN(197) :=  PCK_SYSMAN_UTL.FC_ROUND((MI_BASESUELDO * TO_NUMBER(PCK_PARST.FC_PAR('PORCENTAJE BONIFICACION POR ESPECIALIZACION', '10')) / 100)* PCK_NOMINA.FC_CN(217) / 160 , 0); 
            ELSE
                PCK_NOMINA.CN(197) :=  PCK_SYSMAN_UTL.FC_ROUND(MI_BASESUELDO * TO_NUMBER(PCK_PARST.FC_PAR('PORCENTAJE BONIFICACION POR ESPECIALIZACION', '10')) / 100, 0);
            END IF;
            PCK_NOMINA.GL_BONESPMADOC := PCK_NOMINA.FC_CN(197);
        END IF;

        --LIQUIDAR BONIFICACION POR MAESTRIA
        IF PCK_NOMINA.FC_CN(460) = 2 AND PCK_NOMINA.FC_CN(198) = 0 THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '12' THEN
                PCK_NOMINA.CN(198) := PCK_SYSMAN_UTL.FC_ROUND((MI_BASESUELDO * TO_NUMBER(PCK_PARST.FC_PAR('PORCENTAJE BONIFICACION MAESTRIA', '45')) / 100)* PCK_NOMINA.FC_CN(217) / 160 , 0); 
            ELSE    
                PCK_NOMINA.CN(198) := PCK_SYSMAN_UTL.FC_ROUND(MI_BASESUELDO * TO_NUMBER(PCK_PARST.FC_PAR('PORCENTAJE BONIFICACION MAESTRIA', '45')) / 100, 0);
            END IF;
            PCK_NOMINA.GL_BONESPMADOC := PCK_NOMINA.FC_CN(198);
        END IF;

        --BONIFICACION POR DOCTORADO
        IF PCK_NOMINA.FC_CN(460) = 3 AND PCK_NOMINA.FC_CN(199) = 0 THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '12' THEN
                PCK_NOMINA.CN(199) := PCK_SYSMAN_UTL.FC_ROUND((MI_BASESUELDO * TO_NUMBER(PCK_PARST.FC_PAR('PORCENTAJE BONIFICACION DOCTORADO', '90')) / 100)* PCK_NOMINA.FC_CN(217) / 160 , 0); 
            ELSE
                PCK_NOMINA.CN(199) := PCK_SYSMAN_UTL.FC_ROUND(MI_BASESUELDO * TO_NUMBER(PCK_PARST.FC_PAR('PORCENTAJE BONIFICACION DOCTORADO', '90')) / 100, 0);
            END IF;    
            PCK_NOMINA.GL_BONESPMADOC := PCK_NOMINA.FC_CN(199);
        END IF;

        --BONIFICACION POR POSTDOCTORADO
        IF PCK_NOMINA.FC_CN(460) = 4 AND PCK_NOMINA.FC_CN(199) = 0 THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '12' THEN
                PCK_NOMINA.CN(199) := PCK_SYSMAN_UTL.FC_ROUND((MI_BASESUELDO * TO_NUMBER(PCK_PARST.FC_PAR('PORCENTAJE BONIFICACION POST DOCTORADO', '100')) / 100)* PCK_NOMINA.FC_CN(217) / 160 , 0); 
            ELSE
                PCK_NOMINA.CN(199) := PCK_SYSMAN_UTL.FC_ROUND(MI_BASESUELDO * TO_NUMBER(PCK_PARST.FC_PAR('PORCENTAJE BONIFICACION POST DOCTORADO', '100')) / 100, 0);
            END IF;
            PCK_NOMINA.GL_BONESPMADOC := PCK_NOMINA.FC_CN(199);
        END IF;

        -- BONIFICACIONES POR GRUPOS DE INVESTIGACION 

        --BONIFICACION GRUPO INVESTIGACION A1
        IF PCK_NOMINA.FC_CN(436) = 1 AND PCK_NOMINA.FC_CN(590) = 0 THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '12' THEN
                PCK_NOMINA.CN(590) :=  PCK_SYSMAN_UTL.FC_ROUND(((MI_BASESUELDO * 56) / 100)* PCK_NOMINA.FC_CN(217) / 160 , 0); 
            ELSE
                PCK_NOMINA.CN(590) :=  PCK_SYSMAN_UTL.FC_ROUND((MI_BASESUELDO * 56) / 100, 0);
            END IF;

        END IF;

              --BONIFICACION GRUPO INVESTIGACION A
        IF PCK_NOMINA.FC_CN(436) = 2 AND PCK_NOMINA.FC_CN(591) = 0 THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '12' THEN
                PCK_NOMINA.CN(591) :=  PCK_SYSMAN_UTL.FC_ROUND(((MI_BASESUELDO * 47) / 100)* PCK_NOMINA.FC_CN(217) / 160 , 0); 
            ELSE
                PCK_NOMINA.CN(591) :=  PCK_SYSMAN_UTL.FC_ROUND((MI_BASESUELDO * 47) / 100, 0);
            END IF;

        END IF;

        --BONIFICACION GRUPO INVESTIGACION B
        IF PCK_NOMINA.FC_CN(436) = 3 AND PCK_NOMINA.FC_CN(592) = 0 THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '12' THEN
                PCK_NOMINA.CN(592) :=  PCK_SYSMAN_UTL.FC_ROUND(((MI_BASESUELDO * 42) / 100)* PCK_NOMINA.FC_CN(217) / 160 , 0); 
            ELSE
                PCK_NOMINA.CN(592) :=  PCK_SYSMAN_UTL.FC_ROUND((MI_BASESUELDO * 42) / 100, 0);
            END IF;

        END IF;

        --BONIFICACION GRUPO INVESTIGACION C
        IF PCK_NOMINA.FC_CN(436) = 4 AND PCK_NOMINA.FC_CN(593) = 0 THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '12' THEN
                PCK_NOMINA.CN(593) :=  PCK_SYSMAN_UTL.FC_ROUND(((MI_BASESUELDO * 38) / 100)* PCK_NOMINA.FC_CN(217) / 160 , 0); 
            ELSE
                PCK_NOMINA.CN(593) :=  PCK_SYSMAN_UTL.FC_ROUND((MI_BASESUELDO * 38) / 100, 0);
            END IF;

        END IF;

               --BONIFICACION GRUPO INVESTIGACION D
        IF PCK_NOMINA.FC_CN(436) = 5 AND PCK_NOMINA.FC_CN(594) = 0 THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '12' THEN
                PCK_NOMINA.CN(594) :=  PCK_SYSMAN_UTL.FC_ROUND(((MI_BASESUELDO * 33) / 100)* PCK_NOMINA.FC_CN(217) / 160 , 0); 
            ELSE
                PCK_NOMINA.CN(594) :=  PCK_SYSMAN_UTL.FC_ROUND((MI_BASESUELDO * 33) / 100, 0);
            END IF;

        END IF;

            --BONIFICACION GRUPO INVESTIGACION SEMILLERO
        IF PCK_NOMINA.FC_CN(436) = 6 AND PCK_NOMINA.FC_CN(595) = 0 THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '12' THEN
                PCK_NOMINA.CN(595) :=  PCK_SYSMAN_UTL.FC_ROUND(((MI_BASESUELDO * 19) / 100)* PCK_NOMINA.FC_CN(217) / 160 , 0); 
            ELSE
                PCK_NOMINA.CN(595) :=  PCK_SYSMAN_UTL.FC_ROUND((MI_BASESUELDO * 19) / 100, 0);
            END IF;
        END IF;
    END IF;

END PR_CALCULARBONIFICACIONESUPC;

PROCEDURE PR_CALCULARCESANTIASSTR(
    /*
    NAME              : PR_CALCULARCESANTIASSTR
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDWIN FERANDO CABRERA MARTINEZ
    DATE MIGRADOR     : 22/12/2022
    TIME              :
    SOURCE MODULE     : NOMINAP2018.05.01_UNIFICADAS, En access = calcularcesantiasALCTOCANCIPA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : PROCEDIMIENTO PARA EL CALCULO DE CESANTAS ESTANDAR, TICKET 7722184, INCLUYE PRIMA DE ANTIGUEDAD
    @NAME:  PR_CALCULARCESANTIASSTR
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
	MI_VALMAYORBASP         PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CONT                 NUMBER(1) := 0;
	--CC_3096(21/01/2026 JCROJAS)
    MI_VALMAYORPS           PCK_SUBTIPOS.TI_PARAMETRO;
    MI_VALMAYORPV           PCK_SUBTIPOS.TI_PARAMETRO;
    MI_VALMAYORPN           PCK_SUBTIPOS.TI_PARAMETRO;
BEGIN
	MI_VALMAYORBASP := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA,
                                                 UN_NOMBRE => 'TOMA MAYOR BASP EN CESANTIAS EN RETIRO',
                                                 UN_MODULO => PCK_DATOS.FC_MODULONOMINA,
                                                 UN_FECHA_PAR => SYSDATE),'NO');
    --CC_3096(21/01/2026 JCROJAS)                                                        
    MI_VALMAYORPS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA,
                                                 UN_NOMBRE => 'TOMA MAYOR PS EN CESANTIAS EN RETIRO',
                                                 UN_MODULO => PCK_DATOS.FC_MODULONOMINA,
                                                 UN_FECHA_PAR => SYSDATE),'NO');
                                                 
    MI_VALMAYORPV := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA,
                                                 UN_NOMBRE => 'TOMA MAYOR PV EN CESANTIAS EN RETIRO',
                                                 UN_MODULO => PCK_DATOS.FC_MODULONOMINA,
                                                 UN_FECHA_PAR => SYSDATE),'NO');    
                                                 
    MI_VALMAYORPN := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA,
                                                 UN_NOMBRE => 'TOMA MAYOR PN EN CESANTIAS EN RETIRO',
                                                 UN_MODULO => PCK_DATOS.FC_MODULONOMINA,
                                                 UN_FECHA_PAR => SYSDATE),'NO');													
    PCK_NOMINA.GL_BASCES := 0;
    MI_FECHA := PCK_NOMINA.GL_FECHAINI;
    --(MZANGUNA:25/10/2018)-Se cambia GL_FECHAFIN a GL_FECHAINI
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
            --MI_DIAS := MI_DIAS + MASDIASOTRAENTIDAD;
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1);
            --DP := 360;
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
            PCK_NOMINA.GL_PVAC := 0 + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501) ;
        ELSE
            IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)), 0) + PCK_NOMINA.FC_CN(155) ;
            ELSE
				--CC_3096(21/01/2026 JCROJAS): Se agrega validacion con la variable MI_VALMAYORPV
                IF MI_VALMAYORPV = 'SI' THEN
                    IF (PCK_NOMINA.FC_CN(155)+PCK_NOMINA.FC_CN(501))>(PCK_NOMINA.FC_CNA(155)+PCK_NOMINA.FC_CNA(501)) THEN
                        PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501);
                    ELSE
                        PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501);
                    END IF;
                ELSE
                    PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501);
                END IF;   
            END IF;
        END IF;
        PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        --CC_1587(05/06/2025 JCROJAS)
        IF MI_VALMAYORBASP = 'SI' THEN
            IF (PCK_NOMINA.FC_CN(150)) > (PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) THEN
                PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150))/12,0);
            ELSE
                PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)/12),0);
            END IF;
        ELSE
			IF PCK_NOMINA.FC_CNA(150) > 0 THEN
				PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538)) / 12, 0);
			ELSIF PCK_NOMINA.FC_CN(150) > 0 THEN
				PCK_NOMINA.GL_BONPAGADA := ((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12)     ;
				PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538)) / 12, 0);
			END IF;
		END IF;
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
		--CC_3096(21/01/2026 JCROJAS): Se agrega validacion con la variable MI_VALMAYORPS
        IF MI_VALMAYORPS = 'SI' THEN
            IF (PCK_NOMINA.FC_CN(160)+PCK_NOMINA.FC_CN(503))>(PCK_NOMINA.FC_CNA(160)+PCK_NOMINA.FC_CNA(503)) THEN 
                PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) / 12, 0);
            ELSE
                PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12, 0);
            END IF;
        ELSE
            PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
        END IF;
        PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);       
        --(EAMAYA:09/09/2019)-Se cambia suma de concepto 70 por la sumatoria del rango de conceptos entre 49 y 60
        --JM 29-04-2025 CC 657  el rango ahora es desde el 46
        PCK_NOMINA.CN(902) := CASE WHEN PCK_NOMINA.FC_CN(902) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_SUMACONA(46,60) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(519)) / 12, 0) ELSE PCK_NOMINA.FC_CN(902) END ;
		--CC_3096(21/01/2026 JCROJAS): Se agrega validacion con la variable MI_VALMAYORPN
        IF MI_VALMAYORPN = 'SI' THEN
            IF (PCK_NOMINA.FC_CN(158)+PCK_NOMINA.FC_CN(504))>(PCK_NOMINA.FC_CNA(158)+PCK_NOMINA.FC_CNA(504)) THEN
                PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(504)) / 12, 0) ;
            ELSE
                PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CNA(504)) / 12, 0) ;
            END IF;
        ELSE
            PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) ;
        END IF;
		--CC_3672(13/03/2026 JCROJAS): Validacion para que sume la prima de antiguedad solo cuando la entidad si la maneje																												  
        MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA
                    + PCK_NOMINA.GL_VPT + CASE WHEN PCK_PARST.FC_PAR('CALCULAR PRIMA DE ANTIGUEDAD','NO') = 'SI' THEN PCK_NOMINA.GL_VPA ELSE 0 END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.FC_CN(906) + PCK_NOMINA.FC_CN(905) + PCK_NOMINA.FC_CN(907) + PCK_NOMINA.FC_CN(902) + PCK_NOMINA.GL_BONPAGADA END;
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
            IF NOT(PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996', 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1) THEN
                PCK_NOMINA.CN(269) := CASE WHEN PCK_NOMINA.FC_CN(269) = 0 THEN CASE WHEN MI_CESANTIA1 < 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) END ELSE PCK_NOMINA.FC_CN(269) END;
            END IF;
            PCK_NOMINA.CN(277) := CASE WHEN PCK_NOMINA.FC_CN(277) = 0 THEN MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(277) END;
            IF PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = '12' AND MI_CONT = 0 THEN
                PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA, 1, (PCK_NOMINA.GL_SANO + 1), 1, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 169, PCK_NOMINA.FC_CN(269));
            END IF;
        END IF;

        PCK_NOMINA.CN(900) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
        PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
        PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
        PCK_NOMINA.CN(901) := PCK_NOMINA.GL_GRPNGV ;
		PCK_NOMINA.CN(908) := PCK_NOMINA.GL_BONPAGADA ;											   
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.CN(910) := MI_DIAS                                                    ;
        PCK_NOMINA.CN(911) := MI_ANTICIPOS                                               ;
        PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION                   ;
        PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0)                            ;
        PCK_NOMINA.CN(914) := PCK_NOMINA.GL_VPT ;
		--CC_3672(13/03/2026 JCROJAS): Validacion para que tome la prima de antiguedad solo cuando la maneje																									
        PCK_NOMINA.CN(915) := CASE WHEN PCK_PARST.FC_PAR('CALCULAR PRIMA DE ANTIGUEDAD','NO') = 'SI' THEN PCK_NOMINA.GL_VPA ELSE 0 END ;
        PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1) + 1 ;
    END IF;

        --JM 29-04-2025 CC 657 NO CALCULAR DE NUEVO EL PERIODO 8 SI YA Se CALCULO EN EL 3 ó EN EL 7
    PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 177, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.FC_CNP(177) > 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 8 THEN         
        PCK_NOMINA.CN(269) := 0;
        PCK_NOMINA.CN(177) := 0;
        PCK_NOMINA.CN(169) := 0;
        PCK_NOMINA.CN(900) := 0;
        PCK_NOMINA.CN(903) := 0;
        PCK_NOMINA.CN(904) := 0;
        PCK_NOMINA.CN(905) := 0;
        PCK_NOMINA.CN(901) := 0;
        PCK_NOMINA.CN(908) := 0;
        PCK_NOMINA.CN(909) := 0;
        PCK_NOMINA.CN(910) := 0;
        PCK_NOMINA.CN(911) := 0;
        PCK_NOMINA.CN(912) := 0;
        PCK_NOMINA.CN(913) := 0;
        PCK_NOMINA.CN(914) := 0;
        PCK_NOMINA.CN(915) := 0;
        PCK_NOMINA.CN(973) := 0;
        PCK_NOMINA.CN(906) := 0;
        PCK_NOMINA.CN(907) := 0;
        PCK_NOMINA.CN(277) := 0;
        PCK_NOMINA.CN(490) := 0;
        PCK_NOMINA.CN(925) := 0;
        PCK_NOMINA.CN(946) := 0;
        PCK_NOMINA.CN(951) := 0;
        PCK_NOMINA.CN(960) := 0;
        PCK_NOMINA.CN(965) := 0;
        PCK_NOMINA.CN(966) := 0;
        PCK_NOMINA.CN(969) := 0;
        PCK_NOMINA.CN(973) := 0;
        PCK_NOMINA.CN(975) := 0;
        PCK_NOMINA.CN(981) := 0;
        PCK_NOMINA.CN(982) := 0;
        PCK_NOMINA.CN(985) := 0;
        IF PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = '12' AND MI_CONT = 0 THEN
                PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA, 1, (PCK_NOMINA.GL_SANO + 1), 1, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 169, 0);
        END IF;
    END IF; 

END PR_CALCULARCESANTIASSTR;


PROCEDURE PR_CALCULARCESANTIASALCTOCAN(
    /*
    NAME              : PR_CALCULARCESANTIASALCTOCAN
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 10/05/2018
    TIME              :
    SOURCE MODULE     : NOMINAP2018.05.01_UNIFICADAS, En access = calcularcesantiasALCTOCANCIPA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  CESANTIASALCTOC
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
    --(MZANGUNA:25/10/2018)-Se cambia GL_FECHAFIN a GL_FECHAINI
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
            --MI_DIAS := MI_DIAS + MASDIASOTRAENTIDAD;
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1);
            --DP := 360;
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
        -- bcardenas ticket 7742312  liquidación con sueldo de encargo
            PCK_NOMINA.GL_PVAC := 0 + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        ELSE
            IF PCK_NOMINA.FC_CNA(164) > 1 THEN
            -- (7804051_CFBARRERA: Se eliminó la suma con la función PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES, ya que los conceptos 155 y 501 acumulados se están sumando previamente, y la suma con la funcion duplicaba el valor).
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)), 0) + PCK_NOMINA.FC_CN(155);
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501);
            END IF;
            --(7804051_CFBARRERA:FIN)
        END IF;
        PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;
        -- bcardenas ticket 7742312  liquidación con sueldo de encargo
        IF PCK_NOMINA.GL_SPER <> 7 THEN 
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        ELSE 
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO - 1 , PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        END IF;
        IF PCK_NOMINA.FC_CNA(150) > 0 THEN
            PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538)) / 12, 0);
        ELSIF PCK_NOMINA.FC_CN(150) > 0 AND PCK_NOMINA.GL_SPER <> 7  THEN
            PCK_NOMINA.GL_BONPAGADA := ((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12)     ;
            PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538)) / 12, 0);
        END IF;
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
        PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);       
        --(EAMAYA:09/09/2019)-Se cambia suma de concepto 70 por la sumatoria del rango de conceptos entre 49 y 60
        --mod jm cc 3232 para que tome el concepto 47 y 48 en las horas extras 
        PCK_NOMINA.CN(902) := CASE WHEN PCK_NOMINA.FC_CN(902) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_SUMACONA(47,60) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(519)) / 12, 0) ELSE PCK_NOMINA.FC_CN(902) END ;
        PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) ;

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
        	-- TICKET 7706066 ECABRERA: SE VERIFICA SI EXISTE NOVEDAD DE RETIRO EN PERIODO 7 PARA NO LIQUIDAR EN PERIODO 8
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
            -- FIN 7706066
            
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
         --CC:3709 - Se comenta el valor mas 1 - ncardenas- 31/12/2025
        PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1);-- + 1 ;
    END IF;

END PR_CALCULARCESANTIASALCTOCAN;

PROCEDURE PR_CALCULARPRIMANAVIDADCORTO(
    /*
    NAME              : PR_CALCULARPRIMANAVIDADCORTO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDWIN FERNANDO CABRERA MARTINEZ
    DATE MIGRADOR     : 15/12/2022
    TIME              :
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : PROCEDIMIENTO PARA CALCULAR PRIMA DE NAVIDAD DE CORTOLIMA TICKET 7721186
    @NAME:  CALCULARPRIMANAVIDADTODOS
    */

UN_COMPANIA    IN  PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_FECHAFPN         DATE;
    MI_TRANSPORTELEGAL  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_RETEFUENTE       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN
    PCK_NOMINA.GL_PVAC := PCK_NOMINA_PROC01.FC_ULTPRIMA_VAC_CORTOLIMA (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);--(CFBARRERA:HU7802248-27-11-2024)
    IF NOT(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11') THEN
        PCK_NOMINA.GL_FACTORPN := 0;
        PCK_NOMINA.GL_DNT := 0;
        --DNT1 := 0;
        PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPN := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN END;
        PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPN1 := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN1 THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN1 END;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO < PCK_NOMINA.GL_FECHAIPN AND PCK_NOMINA.FC_CN(155) = 0 THEN
            PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/' || PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN) || '/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_FECHAI := PCK_NOMINA.GL_FECHAIPN;
        END IF;
        IF PCK_NOMINA.GL_SMES = 12 AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) AND PCK_NOMINA.FC_CN(155) = 0 THEN
            PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        END IF;
        IF PCK_NOMINA.GL_SPER = 4 THEN
            PCK_NOMINA.CN(150) := 0;
        END IF;

        IF PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_FECHAI <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0)  ;
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

            
            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := PCK_NOMINA_PROC01.FC_ULTPRIMA_VAC_CORTOLIMA (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);--(CFBARRERA:HU7802248-27-11-2024)
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) ;
            END IF;
            IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.GL_SPER = 3 AND PCK_NOMINA.FC_CN(155) > 0 THEN
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501);
            END IF;

            PCK_NOMINA.CN(942) := 0;

            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);

            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.FC_CN(942) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_VPA + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) ;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN
                IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                END IF;
            END IF;
            --DCC1 := 0;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(937) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                END IF;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.FC_CN(404) <> 0 THEN
            MI_FECHAFPN := PCK_NOMINA.GL_FECHAR;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.CN(942) := 0 ;
            /*IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := 0 + CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 THEN PCK_NOMINA.FC_CN(155) ELSE 0 END;
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155);
            END IF;*/
            IF (PCK_NOMINA.FC_CN(160)) > (PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) THEN
                PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160))/12, 0);
            ELSE
                PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO))/12, 0);    
            END IF;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12), 0);
            --(EAMAYA:25/09/2019)-Se adcionana el acumulado del concepto 514
            IF (PCK_NOMINA.FC_CN(150)+PCK_NOMINA.FC_CN(514)) > (PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) THEN
                PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150)+PCK_NOMINA.FC_CN(514))/12, 0);
            ELSE
                PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO))/12, 0);
            END IF;
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            --DCC1 := 0;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..(PCK_NOMINA.GL_SMES - CASE WHEN PCK_NOMINA.GL_SPER = 7 THEN 0 ELSE 1 END) LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    IF PCK_SYSMAN_UTL.FC_MES(MI_FECHAFPN) <> i AND PCK_SYSMAN_UTL.FC_DIA(MI_FECHAFPN) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(MI_FECHAFPN)) THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;

                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;

            PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - (PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357));
            PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS < 0 THEN 0 ELSE PCK_NOMINA.GL_DOCEAVAS END;
            PCK_NOMINA.GL_FACTORPN := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.FC_CN(942) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_VPA + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(931), 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(932), 0) + PCK_NOMINA.FC_CN(939), 0);
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(937) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                END IF;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAI <= TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
                IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                END IF;
            END IF;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := PCK_NOMINA_PROC01.FC_ULTPRIMA_VAC_CORTOLIMA (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);--(CFBARRERA:HU7802248-27-11-2024)
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501);
            END IF;
            PCK_NOMINA.CN(942) := 0 ;
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;

            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / 12 ;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + + PCK_NOMINA.GL_VPA + PCK_NOMINA.FC_CN(931)  + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            --DCC1 := 0;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;

                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(937) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                END IF;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;

        ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            /*IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
                IF PCK_NOMINA.GL_FECHAR > TO_DATE('30/11/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                END IF;
            END IF;*/
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := PCK_NOMINA_PROC01.FC_ULTPRIMA_VAC_CORTOLIMA (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);--(CFBARRERA:HU7802248-27-11-2024)
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155);
            END IF;
            PCK_NOMINA.CN(942) := 0 ;
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;

            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / 12 ;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);

            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_VPA + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(937) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                END IF;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        END IF;
    END IF;

    IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(155) > 0 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CN(158) > 0 AND PCK_NOMINA.FC_CN(504) = 0 AND PCK_NOMINA.FC_CNA(158) <> 0 THEN
            PCK_NOMINA.CN(504) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
            PCK_NOMINA.CN(158) := 0;
        END IF;
    END IF;

    PCK_NOMINA.GL_PRIMADIC := PCK_NOMINA.FC_CN(158);
    --DIASPRIMADIC := PCK_NOMINA.GL_DOCEAVAS ;
    MI_TRANSPORTELEGAL := PCK_NOMINA.FC_CN(254);
    MI_RETEFUENTE := PCK_NOMINA.FC_CN(125);
    PCK_NOMINA.CN(930) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..599 LOOP
            IF (i <> 125) AND (i < 599) AND (i <> 303) AND (i <> 301) AND (i <> 10) AND (i <> 300) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(125) := MI_RETEFUENTE;
    PCK_NOMINA.CN(254) := MI_TRANSPORTELEGAL;
    PCK_NOMINA.CN(158) := PCK_NOMINA.GL_PRIMADIC;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS ;
    PCK_NOMINA.CN(933) := PCK_NOMINA.GL_AUXT ;
    PCK_NOMINA.CN(934) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(936) := PCK_NOMINA.FC_CN(67) ;
    PCK_NOMINA.CN(937) := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.CN(935) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(940) := PCK_NOMINA.GL_VPT;
    PCK_NOMINA.CN(943) := PCK_NOMINA.GL_VPA;
    
    PCK_NOMINA.CN(988) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPN), 0)                            ;
    PCK_NOMINA.GL_PN_DIAS := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.GL_PN_BASE := PCK_NOMINA.GL_FACTORPN;
END PR_CALCULARPRIMANAVIDADCORTO;

PROCEDURE PR_CALCULARPRIMANAVIDADALCTOC(
    /*
    NAME              : PR_CALCULARPRIMANAVIDADALCTOC
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 15/05/2018
    TIME              :
    SOURCE MODULE     : NOMINAP2018.05.01_UNIFICADAS , En access calcularprimadenavidadALCTOCANCIPA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  CALCULARPRIMANAVIDADTODOS
    */

UN_COMPANIA    IN  PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_FECHAFPN         DATE;
    MI_TRANSPORTELEGAL  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_RETEFUENTE       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_FACTORSUELDO     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN
    PCK_NOMINA.GL_PVAC := 0;
    IF NOT(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11') THEN
        PCK_NOMINA.GL_FACTORPN := 0;
        PCK_NOMINA.GL_DNT := 0;
        --DNT1 := 0;
        PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPN := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN END;
        PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPN1 := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN1 THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN1 END;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO < PCK_NOMINA.GL_FECHAIPN AND PCK_NOMINA.FC_CN(155) = 0 THEN
            PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/' || PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN) || '/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_FECHAI := PCK_NOMINA.GL_FECHAIPN;
        END IF;
        IF PCK_NOMINA.GL_SMES = 12 AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) AND PCK_NOMINA.FC_CN(155) = 0 THEN
            PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        END IF;
        IF PCK_NOMINA.GL_SPER = 4 THEN
            PCK_NOMINA.CN(150) := 0;
        END IF;

        IF PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_FECHAI <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0)  ;
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

            PCK_NOMINA.GL_PVAC := 0;
            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
            	-- TICKET 7723484 EFCM: SE ADICIONA CONCEPTO DE RETROACTIVO A LA PRIMA DE VACACIONES
                PCK_NOMINA.GL_PVAC := 0 + PCK_NOMINA.FC_CNA(501);
                -- TICKET 7723484 FIN --
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) ;
            END IF;
            IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.GL_SPER = 3 AND PCK_NOMINA.FC_CN(155) > 0 THEN
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501);
            END IF;

            PCK_NOMINA.CN(942) := 0;

            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
            
            -- TICKET 7724172 EFCM: PARA EL PERIODO 3 DE DCIEMBRE SE RESPETA EL FACTOR SUELDO UTILIZADO 
            --                      EN LA LIQUIDACION INICIAL DE LA PRIMA DE NAVIDAD PERIODO 4
            IF ( PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.GL_SPER = 3 ) THEN
                BEGIN
                    SELECT  VALOR
                    INTO    MI_FACTORSUELDO 
                    FROM    HISTORICOS
                    WHERE   COMPANIA = UN_COMPANIA
                            AND ID_DE_PROCESO = PCK_NOMINA.GL_SPRC                       
                            AND ANO = PCK_NOMINA.GL_SANO
                            AND MES = PCK_NOMINA.GL_SMES
                            AND PERIODO = 4
                            AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO                        
                            AND ID_DE_CONCEPTO = '930'
                            AND NVL(VALOR,0) != 0;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_FACTORSUELDO := 0;
                END;
                
                IF ( NVL(MI_FACTORSUELDO,0) = 0 ) THEN
                    MI_FACTORSUELDO :=  CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
                END IF;
            ELSE
                MI_FACTORSUELDO :=  CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
            END IF;
            -- TICKET 7724172 FIN --
			
			PCK_NOMINA.GL_FACTORPN := MI_FACTORSUELDO + PCK_NOMINA.FC_CN(942) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            --PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.FC_CN(942) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) ;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN
                IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                END IF;
            END IF;
            --DCC1 := 0;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(937) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                END IF;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.FC_CN(404) <> 0 THEN
            MI_FECHAFPN := PCK_NOMINA.GL_FECHAR;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.CN(942) := 0 ;
            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := 0 + CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 THEN PCK_NOMINA.FC_CN(155) ELSE 0 END;
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155);
            END IF;
            IF PCK_NOMINA.FC_CNA(160) > 0 THEN
                PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(160) / 12, 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(160) / 12, 0);
            ELSIF PCK_NOMINA.FC_CN(160) > 0 THEN
                PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(160) / 12, 0);
            END IF;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
            --(EAMAYA:25/09/2019)-Se adcionana el acumulado del concepto 514
            IF PCK_NOMINA.FC_CN(150) > 0 THEN
                PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514)) / 12, 0);
            ELSIF PCK_NOMINA.FC_CNA(150) > 0 THEN
                PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CNA(514)) / 12, 0) ;
            END IF;
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            --DCC1 := 0;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..(PCK_NOMINA.GL_SMES - CASE WHEN PCK_NOMINA.GL_SPER = 7 THEN 0 ELSE 1 END) LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    IF PCK_SYSMAN_UTL.FC_MES(MI_FECHAFPN) <> i AND PCK_SYSMAN_UTL.FC_DIA(MI_FECHAFPN) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(MI_FECHAFPN)) THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;

                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;

            PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - (PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357));
            PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS < 0 THEN 0 ELSE PCK_NOMINA.GL_DOCEAVAS END;
            PCK_NOMINA.GL_FACTORPN := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.FC_CN(942) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(931), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_PVAC / 12), 0) + PCK_NOMINA.FC_CN(939), 0);
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(937) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                END IF;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAI <= TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
                IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                END IF;
            END IF;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := 0;
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501);
            END IF;
            PCK_NOMINA.CN(942) := 0 ;
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;

            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / 12 ;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            --DCC1 := 0;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;

                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(937) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                END IF;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;

        ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            /*IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
                IF PCK_NOMINA.GL_FECHAR > TO_DATE('30/11/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                END IF;
            END IF;*/
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := 0;
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155);
            END IF;
            PCK_NOMINA.CN(942) := 0 ;
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;

            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / 12 ;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);

            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(937) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                END IF;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        END IF;
    END IF;

    IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(155) > 0 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CN(158) > 0 AND PCK_NOMINA.FC_CN(504) = 0 AND PCK_NOMINA.FC_CNA(158) <> 0 THEN
            PCK_NOMINA.CN(504) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
            PCK_NOMINA.CN(158) := 0;
        END IF;
    END IF;

    PCK_NOMINA.GL_PRIMADIC := PCK_NOMINA.FC_CN(158);
    --DIASPRIMADIC := PCK_NOMINA.GL_DOCEAVAS ;
    MI_TRANSPORTELEGAL := PCK_NOMINA.FC_CN(254);
    MI_RETEFUENTE := PCK_NOMINA.FC_CN(125);
    
    
    -- TICKET 7724172 EFCM: PARA EL PERIODO 3 DE DCIEMBRE SE RESPETA EL FACTOR SUELDO UTILIZADO 
    --                      EN LA LIQUIDACION INICIAL DE LA PRIMA DE NAVIDAD PERIODO 4
    IF ( NVL(MI_FACTORSUELDO,0) != 0 ) THEN
        PCK_NOMINA.CN(930) := MI_FACTORSUELDO;
    ELSE
        PCK_NOMINA.CN(930) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    END IF;
     -- TICKET 7724172 FIN --
     
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..599 LOOP
            IF (i <> 125) AND (i < 599) AND (i <> 303) AND (i <> 301) AND (i <> 10) AND (i <> 300) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(125) := MI_RETEFUENTE;
    PCK_NOMINA.CN(254) := MI_TRANSPORTELEGAL;
    PCK_NOMINA.CN(158) := PCK_NOMINA.GL_PRIMADIC;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS ;
    PCK_NOMINA.CN(933) := PCK_NOMINA.GL_AUXT ;
    PCK_NOMINA.CN(934) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(936) := PCK_NOMINA.FC_CN(67) ;
    PCK_NOMINA.CN(937) := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.CN(935) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(940) := PCK_NOMINA.GL_VPT;
    PCK_NOMINA.CN(988) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPN), 0)                            ;
    PCK_NOMINA.GL_PN_DIAS := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.GL_PN_BASE := PCK_NOMINA.GL_FACTORPN;

END PR_CALCULARPRIMANAVIDADALCTOC;

PROCEDURE PR_CALCPRIMASEMESTRAL_STR(
/*
NAME              : PR_CALCPRIMASEMESTRAL_STR
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : JOHANNA LORENA ALFONSO
DATE MIGRADOR     : 28/09/2020
TIME              : 02:19 PM
SOURCE MODULE     : NOMINAP2018.05.01_UNIFICADAS 
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       :Calculo estandar para prima proporcional por dias laborados
@NAME:  CALCULARPRIMASEMESTRALPROPORCIONALSTR
*/
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)

AS
    MI_DOCEAVASMINIMASPS         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PSPAGADA                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PRIMAJUNIO                   PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_INDRETIR                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_MSG                          PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALOR                            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN
    MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1'));
    PCK_NOMINA.GL_FACTORPS := 0;
    PCK_NOMINA.GL_FACTORPS1 := 0;
    PCK_NOMINA.GL_DNT := 0;

	  --(APINEDA:22/03/2019)-Se agregan validaciones faltantes para la fecha de inicio de la prima semestral debido a que no esta calculando el concepto 160 para liquidaciones finales. TAR 1000090732
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
        --(APINEDA:01/12/2018)-TAR1000087154 Se toma la fecha de ingreso como fecha de inicio cuando un funcionario se retira y en julio del año anterior no habia cumplido 180 dias trabajados
        IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 THEN
            IF (PCK_NOMINA.GL_FECHAIPS > PCK_NOMINA.GL_FECHAI) AND (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAIPS) < 180) THEN
                PCK_NOMINA.GL_FECHAIPS := PCK_NOMINA.GL_FECHAI;
            END IF;
        END IF;
    END IF;
	  --(APINEDA:22/03/2019)-Se agregan validaciones faltantes para la fecha de inicio de la prima semestral debido a que no esta calculando el concepto 160 para liquidaciones finales. TAR 1000090732
    IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 AND PCK_NOMINA.GL_SMES = 7  THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
    END IF;

    IF PCK_NOMINA.GL_SMES > 7 THEN
        PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    END IF;
    --(APINEDA:26/07/2019)-Se modifica CASE para asignar valor a la fecha final de la prima semestral debido a que estaba presentando inconsistencias.
    IF PCK_NOMINA.GL_SMES = 7 THEN
        PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN CASE WHEN PCK_NOMINA.GL_FECHAFIN1 > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
    ELSE
        PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
    END IF;
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    END IF;
    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
    IF (PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 4) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO >= PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO <= PCK_NOMINA.GL_FECHAFIN) THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
    ELSE
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0);
    END IF;
    IF PCK_NOMINA.FC_CN(946) = 0 AND PCK_NOMINA.FC_CN(514) <> 0 THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(514) / 12, 0);
    END IF;

    PCK_NOMINA.GL_FACTORPS := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(946), 0);
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
    IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 7) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DOCEAVAS := 6;
        --DOCEAVASMINIMASPRIMASERVICIOS := 6;
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
    END IF;


    FOR i IN 7..12 LOOP
        PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
            IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
            END IF;
            PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
        END IF;
    END LOOP;
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
            PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                END IF;
                PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
            END IF;
        END LOOP;
    END IF;
    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));

    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
        PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC / 30);
    END IF;
    IF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('16/06/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAIPS <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 THEN
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
    ELSIF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_DOCEAVAS >= 1 THEN
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
    ELSIF PCK_NOMINA.FC_CN(404) = 1 THEN
        MI_PSPAGADA := 0;
        IF PCK_NOMINA.GL_SMES = 6 OR PCK_NOMINA.GL_SMES = 7 THEN
            PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 160, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, 7, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_PSPAGADA := PCK_NOMINA.FC_CNP(160);
        END IF;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
        IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 6) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DOCEAVAS := 6;
            --DOCEAVASMINIMASPRIMASERVICIOS := 6;
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
        ELSE
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        END IF;
        IF PCK_NOMINA.GL_SPRC <> 99 THEN
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF ((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0) THEN
                    IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFPS) <> i AND PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFPS) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(PCK_NOMINA.GL_FECHAFPS)) THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            FOR i IN 6..12 LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
        END IF;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        IF PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS > 12 THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;
        IF PCK_NOMINA.FC_CN(952) > 0 THEN
            PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
            PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953) / 30);
        END IF;
        MI_VALOR:= PCK_NOMINA.GL_DOCEAVAS;
        IF (PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS) OR (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS AND PCK_NOMINA.FC_CN(404) = 0 THEN
            IF PCK_NOMINA.GL_DOCEAVAS = 0 AND PCK_NOMINA.FC_CN(404) = 0 THEN
                PCK_NOMINA.GL_DCC := 0;
                PCK_NOMINA.GL_DOCEAVAS := 0;
                -- 'REVISAR PCK_NOMINA.GL_DOCEAVAS CAUSADAS, YA QUE NO CUMPLIO MÁS DE 6 MESES RADICADO 201520160102642 01/06/15  A: ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES
                --Revisar Doceavas Causadas, ya que no cumplio Más de 6 meses Radicado --RADICADO-- --> 01/06/15  a: --NOMBRES--.
                MI_MSG(1).CLAVE := 'NOMBRES';
                MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
                MI_MSG(2).CLAVE := 'RADICADO';
                MI_MSG(2).VALOR := '201520160102642';

                PCK_NOMINA_COM7.PR_ALERTA
                    (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                    ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_DOCEAVASCAUSADAS
                    ,UN_REEMPLAZOS   => MI_MSG
                    ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                    ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                    ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                    ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                    ,UN_USER         => PCK_CONEXION.FC_GETUSER
                    );
            END IF;
        END IF;
        --(APINEDA:22/03/2019)-Se agrega validación para que no se calcule prima semestral cuando el funcionario no ha cumplido 6 meses TAR 1000090732
        --(MZANGUNA:19/12/2019)-Se quita validación dado que ahora si se debe calcular con los días que el empleado lleve
        /*IF PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFPS) < 6 THEN
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;*/

        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            --(MZANGUNA:19/12/2019)-Se quita condición dado que tocancipa empieza a pagar la prima por días.
            --IF (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953)) >= 180 THEN
                MI_VALOR := PCK_NOMINA.FC_CN(160);
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
                MI_VALOR := PCK_NOMINA.FC_CN(160);
            --END IF;
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
        IF PCK_NOMINA.FC_CN(160) < 0 THEN
            PCK_NOMINA.CN(160) := 0;
        END IF;
        IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, '01', PCK_NOMINA.GL_SANO, i, '99', PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
            ELSE
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
            END IF;
        END IF;
        --(APINEDA:26/07/2019)-Se modifica condición debido a que esta descontando la prima semestral a persona que se retira en Julio
        IF MI_PSPAGADA > 0 AND PCK_NOMINA.FC_CN(160) > 0 AND PCK_NOMINA.GL_PROCESOACTUAL <> 99 AND PCK_NOMINA.GL_PERIODOACTUAL <> 7 THEN
            PCK_NOMINA.CN(160) := 0;
            --La prima Semestral ya debió ser pagada, según datos históricos, revise pagos. se eliminara éste concepto en el pago de liquidación a: --NOMEMPLEADO--, Cédula No.--CEDULA--, Tipo: --TIPO--.
            MI_MSG(1).CLAVE := 'NOMEMPLEADO';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(2).CLAVE := 'CEDULA';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
            MI_MSG(3).CLAVE := 'TIPO';
            MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_PRIMASEMESTRALPAGA
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
        END IF;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
    END IF;
    MI_PRIMAJUNIO := PCK_NOMINA.FC_CN(160);
    MI_INDRETIR := PCK_NOMINA.FC_CN(404);
    PCK_NOMINA.CN(945) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..699 LOOP
            IF (i <> 125) AND (i <> 303) AND (i <> 301) AND (i <> 300) AND (i < 599) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(404) := MI_INDRETIR;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS;

    PCK_NOMINA.CN(160) := MI_PRIMAJUNIO;

    PCK_NOMINA.CN(947) := PCK_NOMINA.GL_VPT   ;
    PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXT ;
    PCK_NOMINA.CN(949) := PCK_NOMINA.GL_AUXA ;
    PCK_NOMINA.CN(950) := PCK_NOMINA.GL_GRPNGV  ;
    PCK_NOMINA.CN(951) := PCK_NOMINA.FC_CN(67) ;
    PCK_NOMINA.CN(952) := PCK_NOMINA.GL_DCC ;

    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.CN(991) := PCK_NOMINA.FC_CNPR(994) ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.CN(992) := PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503);
        PCK_NOMINA.CN(993) := PCK_NOMINA.FC_CN(160) ;
        PCK_NOMINA.CN(994) := PCK_NOMINA.FC_CN(992) - PCK_NOMINA.FC_CN(991) + PCK_NOMINA.FC_CN(993) ;
    END IF;

    PCK_NOMINA.CN(985) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS), 0) ;

    PCK_NOMINA.GL_PS_BASE := PCK_NOMINA.GL_FACTORPS;
    PCK_NOMINA.GL_PS_DIAS := PCK_NOMINA.GL_DCC;

END PR_CALCPRIMASEMESTRAL_STR;

PROCEDURE PR_CALCPRIMASEMESTRAL_CORTO(
/*
NAME              : PR_CALCPRIMASEMESTRAL_CORTO
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : EDWIN FERNANDO CABRERA MARTINEZ
DATE MIGRADOR     : 14/12/2022
TIME              : 02:19 PM
SOURCE MODULE     : 
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       : PROCEDIMIENTO PARA CALCULO PRIMA DE SERVICIO PARA CORTOLIMA
                    TICKET 7721185
@NAME:  CALCULARPRIMASEMESTRALALCTOC
*/
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)

AS
    MI_DOCEAVASMINIMASPS         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PSPAGADA                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PRIMAJUNIO                   PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_INDRETIR                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_MSG                          PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALOR                            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN
    MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1'));
    PCK_NOMINA.GL_FACTORPS := 0;
    PCK_NOMINA.GL_FACTORPS1 := 0;
    PCK_NOMINA.GL_DNT := 0;

	  --(APINEDA:22/03/2019)-Se agregan validaciones faltantes para la fecha de inicio de la prima semestral debido a que no esta calculando el concepto 160 para liquidaciones finales. TAR 1000090732
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
        --(APINEDA:01/12/2018)-TAR1000087154 Se toma la fecha de ingreso como fecha de inicio cuando un funcionario se retira y en julio del aÃ±o anterior no habia cumplido 180 dias trabajados
        IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 THEN
            IF (PCK_NOMINA.GL_FECHAIPS > PCK_NOMINA.GL_FECHAI) AND (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAIPS) < 180) AND  PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) = 0 THEN --JM 27/03/2025 CC 1274
                PCK_NOMINA.GL_FECHAIPS := PCK_NOMINA.GL_FECHAI;
            END IF;
        END IF;
    END IF;
	  --(APINEDA:22/03/2019)-Se agregan validaciones faltantes para la fecha de inicio de la prima semestral debido a que no esta calculando el concepto 160 para liquidaciones finales. TAR 1000090732
    IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 AND PCK_NOMINA.GL_SMES = 7  THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
    END IF;

    IF PCK_NOMINA.GL_SMES > 7 THEN
        PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    END IF;
    --(APINEDA:26/07/2019)-Se modifica CASE para asignar valor a la fecha final de la prima semestral debido a que estaba presentando inconsistencias.
    IF PCK_NOMINA.GL_SMES = 7 THEN
        PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN CASE WHEN PCK_NOMINA.GL_FECHAFIN1 > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
    ELSE
        PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
    END IF;
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    END IF;
    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
    IF (PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 4) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO >= PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO <= PCK_NOMINA.GL_FECHAFIN) THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
    ELSE
		IF (PCK_NOMINA.FC_CN(150)+PCK_NOMINA.FC_CN(514)) > (PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) THEN
            PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150)+PCK_NOMINA.FC_CN(514))/12, 0);
        ELSE
            PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO))/12, 0);
        END IF;
    END IF;
    IF PCK_NOMINA.FC_CN(946) = 0 AND PCK_NOMINA.FC_CN(514) <> 0 THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(514) / 12, 0);
    END IF;

    PCK_NOMINA.GL_FACTORPS := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT_C + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_GRPNGV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(946), 0);
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
    IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 7) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DOCEAVAS := 6;
        --DOCEAVASMINIMASPRIMASERVICIOS := 6;
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
    END IF;


    FOR i IN 7..12 LOOP
        PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
            IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_SMES = 7  THEN PCK_NOMINA.GL_DOCEAVAS ELSE PCK_NOMINA.GL_DOCEAVAS - 1 END;
            END IF;
            PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
        END IF;
    END LOOP;
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
            PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                    PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_SMES = 7  THEN PCK_NOMINA.GL_DOCEAVAS ELSE PCK_NOMINA.GL_DOCEAVAS - 1 END;
                END IF;
                PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
            END IF;
        END LOOP;
    END IF;
    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));

    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
        PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC / 30);
    END IF;
    IF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('16/06/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAIPS <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 THEN
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
    ELSIF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_DOCEAVAS >= 1 THEN
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
    ELSIF PCK_NOMINA.FC_CN(404) = 1 THEN
        MI_PSPAGADA := 0;
        IF PCK_NOMINA.GL_SMES = 6 OR PCK_NOMINA.GL_SMES = 7 THEN
            PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 160, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, 7, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_PSPAGADA := PCK_NOMINA.FC_CNP(160);
        END IF;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
        IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 6) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DOCEAVAS := 6;
            --DOCEAVASMINIMASPRIMASERVICIOS := 6;
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
        ELSE
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        END IF;
        IF PCK_NOMINA.GL_SPRC <> 99 THEN
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF ((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0) THEN
                    IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFPS) <> i AND PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFPS) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(PCK_NOMINA.GL_FECHAFPS)) THEN
                        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_SMES = 7  THEN PCK_NOMINA.GL_DOCEAVAS ELSE PCK_NOMINA.GL_DOCEAVAS - 1 END;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            FOR i IN 6..12 LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_SMES = 7  THEN PCK_NOMINA.GL_DOCEAVAS ELSE PCK_NOMINA.GL_DOCEAVAS - 1 END;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
        END IF;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        IF PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS > 12 THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;
        IF PCK_NOMINA.FC_CN(952) > 0 THEN
            PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
            PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953) / 30);
        END IF;
        MI_VALOR:= PCK_NOMINA.GL_DOCEAVAS;
        IF (PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS) OR (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS AND PCK_NOMINA.FC_CN(404) = 0 THEN
            IF PCK_NOMINA.GL_DOCEAVAS = 0 AND PCK_NOMINA.FC_CN(404) = 0 THEN
                PCK_NOMINA.GL_DCC := 0;
                PCK_NOMINA.GL_DOCEAVAS := 0;
                -- 'REVISAR PCK_NOMINA.GL_DOCEAVAS CAUSADAS, YA QUE NO CUMPLIO MÃ¿S DE 6 MESES RADICADO 201520160102642 01/06/15  A: ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES
                --Revisar Doceavas Causadas, ya que no cumplio MÃ¡s de 6 meses Radicado --RADICADO-- --> 01/06/15  a: --NOMBRES--.
                MI_MSG(1).CLAVE := 'NOMBRES';
                MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
                MI_MSG(2).CLAVE := 'RADICADO';
                MI_MSG(2).VALOR := '201520160102642';

                PCK_NOMINA_COM7.PR_ALERTA
                    (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                    ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_DOCEAVASCAUSADAS
                    ,UN_REEMPLAZOS   => MI_MSG
                    ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                    ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                    ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                    ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                    ,UN_USER         => PCK_CONEXION.FC_GETUSER
                    );
            END IF;
        END IF;
        --(APINEDA:22/03/2019)-Se agrega validaciÃ³n para que no se calcule prima semestral cuando el funcionario no ha cumplido 6 meses TAR 1000090732
        --(MZANGUNA:19/12/2019)-Se quita validaciÃ³n dado que ahora si se debe calcular con los dÃ­as que el empleado lleve
        /*IF PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFPS) < 6 THEN
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;*/

        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN

         /*05/09/2023 7734153*/
        IF PCK_NOMINA.GL_SMES >= 7 THEN
        PCK_NOMINA.CN(953) := 0;
        FOR i IN 7..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF ((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0) THEN
                    IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFPS) <> i AND PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFPS) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(PCK_NOMINA.GL_FECHAFPS)) THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                    MI_VALOR := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
        END LOOP;
         END IF;
         /*05/09/2023 7734153*/

            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            --(MZANGUNA:19/12/2019)-Se quita condiciÃ³n dado que tocancipa empieza a pagar la prima por dÃ­as.
            --IF (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953)) >= 180 THEN
                MI_VALOR := PCK_NOMINA.FC_CN(160);
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.GL_SMES IN (7) OR PCK_NOMINA.GL_SMES  IN (6) AND PCK_NOMINA.FC_CNA(160) > 0 THEN CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END ELSE   CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160) END;
                MI_VALOR := PCK_NOMINA.FC_CN(160);
            --END IF;
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
        IF PCK_NOMINA.FC_CN(160) < 0 THEN
            PCK_NOMINA.CN(160) := 0;
        END IF;
        IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, '01', PCK_NOMINA.GL_SANO, i, '99', PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
            ELSE
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
            END IF;
        END IF;
        --(APINEDA:26/07/2019)-Se modifica condiciÃ³n debido a que esta descontando la prima semestral a persona que se retira en Julio
        IF MI_PSPAGADA > 0 AND PCK_NOMINA.FC_CN(160) > 0 AND PCK_NOMINA.GL_PROCESOACTUAL <> 99 AND PCK_NOMINA.GL_PERIODOACTUAL <> 7 THEN
            PCK_NOMINA.CN(160) := 0;
            --La prima Semestral ya debiÃ³ ser pagada, segÃºn datos histÃ³ricos, revise pagos. se eliminara Ã©ste concepto en el pago de liquidaciÃ³n a: --NOMEMPLEADO--, CÃ©dula No.--CEDULA--, Tipo: --TIPO--.
            MI_MSG(1).CLAVE := 'NOMEMPLEADO';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(2).CLAVE := 'CEDULA';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
            MI_MSG(3).CLAVE := 'TIPO';
            MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_PRIMASEMESTRALPAGA
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
        END IF;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
    END IF;
    MI_PRIMAJUNIO := PCK_NOMINA.FC_CN(160);
    MI_INDRETIR := PCK_NOMINA.FC_CN(404);
    PCK_NOMINA.CN(945) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..699 LOOP
            IF (i <> 125) AND (i <> 303) AND (i <> 301) AND (i <> 300) AND (i < 599) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(404) := MI_INDRETIR;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS;

    PCK_NOMINA.CN(160) := MI_PRIMAJUNIO;

    PCK_NOMINA.CN(947) := PCK_NOMINA.GL_VPT_C; --PCK_NOMINA.GL_VPT
    PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXT ;
    PCK_NOMINA.CN(949) := PCK_NOMINA.GL_AUXA ;
    PCK_NOMINA.CN(950) := PCK_NOMINA.GL_GRPNGV  ;
    PCK_NOMINA.CN(951) := PCK_NOMINA.FC_CN(67) ;
    PCK_NOMINA.CN(952) := PCK_NOMINA.GL_DCC ;
    PCK_NOMINA.CN(954) := PCK_NOMINA.GL_VPA   ;

    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.CN(991) := PCK_NOMINA.FC_CNPR(994) ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.CN(992) := PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503);
        PCK_NOMINA.CN(993) := PCK_NOMINA.FC_CN(160) ;
        PCK_NOMINA.CN(994) := PCK_NOMINA.FC_CN(992) - PCK_NOMINA.FC_CN(991) + PCK_NOMINA.FC_CN(993) ;
    END IF;

    PCK_NOMINA.CN(985) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS), 0) ;

    PCK_NOMINA.GL_PS_BASE := PCK_NOMINA.GL_FACTORPS;
    PCK_NOMINA.GL_PS_DIAS := PCK_NOMINA.GL_DCC;

END PR_CALCPRIMASEMESTRAL_CORTO;

PROCEDURE PR_CALCPRIMASEMESTRAL_ALCTOC(
/*
NAME              : PR_CALCPRIMASEMESTRAL_ALCTOC
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MIGUEL ANGEL VENEGAS RODRIGUEZ
DATE MIGRADOR     : 15/05/2018
TIME              : 02:19 PM
SOURCE MODULE     : NOMINAP2018.05.01_UNIFICADAS En access calcularprimasemestralALCTOCANCIPA
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       :
@NAME:  CALCULARPRIMASEMESTRALALCTOC
*/
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)

AS
    MI_DOCEAVASMINIMASPS         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PSPAGADA                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PRIMAJUNIO                   PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_INDRETIR                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_MSG                          PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALOR                            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN
    MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1'));
    PCK_NOMINA.GL_FACTORPS := 0;
    PCK_NOMINA.GL_FACTORPS1 := 0;
    PCK_NOMINA.GL_DNT := 0;

	  --(APINEDA:22/03/2019)-Se agregan validaciones faltantes para la fecha de inicio de la prima semestral debido a que no esta calculando el concepto 160 para liquidaciones finales. TAR 1000090732
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
        --(APINEDA:01/12/2018)-TAR1000087154 Se toma la fecha de ingreso como fecha de inicio cuando un funcionario se retira y en julio del año anterior no habia cumplido 180 dias trabajados
        IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 THEN
            IF (PCK_NOMINA.GL_FECHAIPS > PCK_NOMINA.GL_FECHAI) AND (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAIPS) < 180) THEN
                PCK_NOMINA.GL_FECHAIPS := PCK_NOMINA.GL_FECHAI;
            END IF;
        END IF;
    END IF;
	  --(APINEDA:22/03/2019)-Se agregan validaciones faltantes para la fecha de inicio de la prima semestral debido a que no esta calculando el concepto 160 para liquidaciones finales. TAR 1000090732
    IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 AND PCK_NOMINA.GL_SMES = 7  THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
    END IF;

    IF PCK_NOMINA.GL_SMES > 7 THEN
        PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    END IF;
    --(APINEDA:26/07/2019)-Se modifica CASE para asignar valor a la fecha final de la prima semestral debido a que estaba presentando inconsistencias.
    IF PCK_NOMINA.GL_SMES = 7 THEN
        PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN CASE WHEN PCK_NOMINA.GL_FECHAFIN1 > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
    ELSE
        PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
    END IF;
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    END IF;
    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
    IF (PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 4) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO >= PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO <= PCK_NOMINA.GL_FECHAFIN) THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
    ELSE
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0);
    END IF;
    IF PCK_NOMINA.FC_CN(946) = 0 AND PCK_NOMINA.FC_CN(514) <> 0 THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(514) / 12, 0);
    END IF;

    PCK_NOMINA.GL_FACTORPS := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(946), 0);
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
    IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 7) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DOCEAVAS := 6;
        --DOCEAVASMINIMASPRIMASERVICIOS := 6;
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
    END IF;


    FOR i IN 7..12 LOOP
        PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
            IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
            END IF;
            PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
        END IF;
    END LOOP;
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
            PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                END IF;
                PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
            END IF;
        END LOOP;
    END IF;
    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));

    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
        PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC / 30);
    END IF;
    IF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('16/06/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAIPS <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 THEN
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
    ELSIF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_DOCEAVAS >= 1 THEN
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
    ELSIF PCK_NOMINA.FC_CN(404) = 1 THEN
        MI_PSPAGADA := 0;
        IF PCK_NOMINA.GL_SMES = 6 OR PCK_NOMINA.GL_SMES = 7 THEN
            PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 160, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, 7, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_PSPAGADA := PCK_NOMINA.FC_CNP(160);
        END IF;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
        IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 6) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DOCEAVAS := 6;
            --DOCEAVASMINIMASPRIMASERVICIOS := 6;
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
        ELSE
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        END IF;
        IF PCK_NOMINA.GL_SPRC <> 99 THEN
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF ((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0) THEN
                    IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFPS) <> i AND PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFPS) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(PCK_NOMINA.GL_FECHAFPS)) THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            FOR i IN 6..12 LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
        END IF;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        IF PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS > 12 THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;
        IF PCK_NOMINA.FC_CN(952) > 0 THEN
            PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
            PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953) / 30);
        END IF;
        MI_VALOR:= PCK_NOMINA.GL_DOCEAVAS;
        IF (PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS) OR (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS AND PCK_NOMINA.FC_CN(404) = 0 THEN
            IF PCK_NOMINA.GL_DOCEAVAS = 0 AND PCK_NOMINA.FC_CN(404) = 0 THEN
                PCK_NOMINA.GL_DCC := 0;
                PCK_NOMINA.GL_DOCEAVAS := 0;
                -- 'REVISAR PCK_NOMINA.GL_DOCEAVAS CAUSADAS, YA QUE NO CUMPLIO MÁS DE 6 MESES RADICADO 201520160102642 01/06/15  A: ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES
                --Revisar Doceavas Causadas, ya que no cumplio Más de 6 meses Radicado --RADICADO-- --> 01/06/15  a: --NOMBRES--.
                MI_MSG(1).CLAVE := 'NOMBRES';
                MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
                MI_MSG(2).CLAVE := 'RADICADO';
                MI_MSG(2).VALOR := '201520160102642';

                PCK_NOMINA_COM7.PR_ALERTA
                    (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                    ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_DOCEAVASCAUSADAS
                    ,UN_REEMPLAZOS   => MI_MSG
                    ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                    ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                    ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                    ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                    ,UN_USER         => PCK_CONEXION.FC_GETUSER
                    );
            END IF;
        END IF;
        --(APINEDA:22/03/2019)-Se agrega validación para que no se calcule prima semestral cuando el funcionario no ha cumplido 6 meses TAR 1000090732
        --(MZANGUNA:19/12/2019)-Se quita validación dado que ahora si se debe calcular con los días que el empleado lleve
        /*IF PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFPS) < 6 THEN
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;*/

        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            --(MZANGUNA:19/12/2019)-Se quita condición dado que tocancipa empieza a pagar la prima por días.
            --IF (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953)) >= 180 THEN
                MI_VALOR := PCK_NOMINA.FC_CN(160);
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
                MI_VALOR := PCK_NOMINA.FC_CN(160);
            --END IF;
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
        IF PCK_NOMINA.FC_CN(160) < 0 THEN
            PCK_NOMINA.CN(160) := 0;
        END IF;
        IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, '01', PCK_NOMINA.GL_SANO, i, '99', PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
            ELSE
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
            END IF;
        END IF;
        --(APINEDA:26/07/2019)-Se modifica condición debido a que esta descontando la prima semestral a persona que se retira en Julio
        IF MI_PSPAGADA > 0 AND PCK_NOMINA.FC_CN(160) > 0 AND PCK_NOMINA.GL_PROCESOACTUAL <> 99 AND PCK_NOMINA.GL_PERIODOACTUAL <> 7 THEN
            PCK_NOMINA.CN(160) := 0;
            --La prima Semestral ya debió ser pagada, según datos históricos, revise pagos. se eliminara éste concepto en el pago de liquidación a: --NOMEMPLEADO--, Cédula No.--CEDULA--, Tipo: --TIPO--.
            MI_MSG(1).CLAVE := 'NOMEMPLEADO';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(2).CLAVE := 'CEDULA';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
            MI_MSG(3).CLAVE := 'TIPO';
            MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_PRIMASEMESTRALPAGA
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
        END IF;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
    END IF;
    MI_PRIMAJUNIO := PCK_NOMINA.FC_CN(160);
    MI_INDRETIR := PCK_NOMINA.FC_CN(404);
    PCK_NOMINA.CN(945) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..699 LOOP
            IF (i <> 125) AND (i <> 303) AND (i <> 301) AND (i <> 300) AND (i < 599) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(404) := MI_INDRETIR;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS;

    PCK_NOMINA.CN(160) := MI_PRIMAJUNIO;

    PCK_NOMINA.CN(947) := PCK_NOMINA.GL_VPT   ;
    PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXT ;
    PCK_NOMINA.CN(949) := PCK_NOMINA.GL_AUXA ;
    PCK_NOMINA.CN(950) := PCK_NOMINA.GL_GRPNGV  ;
    PCK_NOMINA.CN(951) := PCK_NOMINA.FC_CN(67) ;
    PCK_NOMINA.CN(952) := PCK_NOMINA.GL_DCC ;

    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.CN(991) := PCK_NOMINA.FC_CNPR(994) ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.CN(992) := PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503);
        PCK_NOMINA.CN(993) := PCK_NOMINA.FC_CN(160) ;
        PCK_NOMINA.CN(994) := PCK_NOMINA.FC_CN(992) - PCK_NOMINA.FC_CN(991) + PCK_NOMINA.FC_CN(993) ;
    END IF;

    PCK_NOMINA.CN(985) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS), 0) ;

    PCK_NOMINA.GL_PS_BASE := PCK_NOMINA.GL_FACTORPS;
    PCK_NOMINA.GL_PS_DIAS := PCK_NOMINA.GL_DCC;

END PR_CALCPRIMASEMESTRAL_ALCTOC;


PROCEDURE PR_CALCULARPRIMASEMESTRALANE(
    /*
    NAME              : PR_CALCPRIMASEMESTRAL_ALCTOC
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL VENEGAS RODRIGUEZ
    DATE MIGRADOR     : 16/05/2018
    TIME              : 10:29 AM
    SOURCE MODULE     : NOMINAP2018.05.01_UNIFICADAS En access CALCULARPRIMASEMESTRALANE
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  CALCULARPRIMASEMESTRALANE
    */

    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)

AS
    MI_DOCEAVASMINIMASPS            PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASPS_NOPAGADOS             PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASNLNRPS_ANOANTERIOR       PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PSPAGADA                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_INDRETIR                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_MSG                          PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALORCN2             NUMBER DEFAULT 0;
BEGIN

    MI_VALORCN2 := PCK_NOMINA.FC_CN(2);
    MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1'));
    PCK_NOMINA.GL_FACTORPS := 0;
    PCK_NOMINA.GL_FACTORPS1 := 0;
    PCK_NOMINA.GL_DNT := 0;
    MI_DIASPS_NOPAGADOS := 0;
    MI_DIASNLNRPS_ANOANTERIOR := 0;
    IF PCK_PARST.FC_PAR('PAGAR PROPORCIONALIDAD PRIMA DE SERVICIOS CONCEPTO DAFP 20166000164121', ' ') = 'SI' THEN
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO >= TO_DATE('01/01/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO <= TO_DATE('30/06/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') AND PCK_NOMINA.GL_SPER = 4 THEN
            MI_DIASPS_NOPAGADOS := 0;
            MI_DIASNLNRPS_ANOANTERIOR := PCK_NOMINA.FC_AUSENTISMOEMPLEADO(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'));
            MI_DIASPS_NOPAGADOS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, TO_DATE('30/06/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY')) - MI_DIASNLNRPS_ANOANTERIOR;

            --Se revisa pago de Prima de Servicios NO pagada de Enero a Junio año anterior, ya que no cumplió Más de 6 meses Radicado DAFP 20166000164121 a: --NOMEMPLEADO--
            MI_MSG(1).CLAVE := 'NOMEMPLEADO';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_PRIMASERVICIONOPAGA
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
        END IF;
    END IF;
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;   
        --(APINEDA:01/12/2018)-TAR1000087154 Se toma la fecha de ingreso como fecha de inicio cuando un funcionario se retira y en julio del año anterior no habia cumplido 180 dias trabajados
        IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 THEN
            IF (PCK_NOMINA.GL_FECHAIPS > PCK_NOMINA.GL_FECHAI) AND (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAIPS) < 180) THEN
                PCK_NOMINA.GL_FECHAIPS := PCK_NOMINA.GL_FECHAI;
            END IF;  
        END IF;        
    END IF;

    IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 AND PCK_NOMINA.GL_SMES = 7  THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
    END IF;    

    IF PCK_NOMINA.GL_SMES > 7 THEN
        PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    END IF;

    IF PCK_NOMINA.GL_SMES = 7 THEN
        PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN CASE WHEN PCK_NOMINA.GL_FECHAFIN1 > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
    ELSE
        PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
    END IF;

    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    END IF;
    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
    IF (PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = '04') AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO >= PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO <= PCK_NOMINA.GL_FECHAFIN) THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
    ELSE
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)  + PCK_NOMINA.FC_CN(514)) / 12, 0);  --PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0);
    END IF;
    IF PCK_NOMINA.FC_CN(946) = 0 AND PCK_NOMINA.FC_CN(514) <> 0 THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(514) / 12, 0);
    END IF;

    PCK_NOMINA.GL_FACTORPS := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(946), 0);
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT + MI_DIASPS_NOPAGADOS ;
    IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 7) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DOCEAVAS := 6;
        --DOCEAVASMINIMASPRIMASERVICIOS := 6;
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT + MI_DIASPS_NOPAGADOS ;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
    END IF;
    FOR i IN 7..12 LOOP
        PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
            PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
            PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
        END IF;
    END LOOP;
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
            PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
            END IF;
        END LOOP;
    END IF;
    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;

    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953)) + MI_DIASPS_NOPAGADOS ;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 180 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > (PCK_NOMINA.GL_FECHAFPS - 180) AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DOCEAVAS := 0;
        --Revisar Doceavas Causadas, ya que no cumplio Más de 6 meses Radicado --RADICADO-- --> 01/06/15  a: --NOMBRES--.
        MI_MSG(1).CLAVE := 'NOMBRES';
        MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
        MI_MSG(2).CLAVE := 'RADICADO';
        MI_MSG(2).VALOR := '201520160102642';

        PCK_NOMINA_COM7.PR_ALERTA
            (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
            ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_DOCEAVASCAUSADAS
            ,UN_REEMPLAZOS   => MI_MSG
            ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
            ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
            ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
            ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
            ,UN_USER         => PCK_CONEXION.FC_GETUSER
            );
    END IF;
    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
        PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC / 30);
    END IF;
    IF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('16/06/' || (PCK_NOMINA.GL_SANO - 1) , 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAIPS <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 THEN
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953)) + MI_DIASPS_NOPAGADOS ;
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;

    ELSIF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_DOCEAVAS >= 1 THEN

        IF PCK_NOMINA.GL_DCC < 180 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953)) + MI_DIASPS_NOPAGADOS ;
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;

    ELSIF PCK_NOMINA.FC_CN(404) = 1 THEN

        MI_PSPAGADA := 0;
        IF PCK_NOMINA.GL_SMES = 6 OR PCK_NOMINA.GL_SMES = 7 THEN
            PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 160, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, 7, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_PSPAGADA := PCK_NOMINA.FC_CNP(160);
        END IF;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;

        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT + MI_DIASPS_NOPAGADOS ;
        IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 6) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DOCEAVAS := 6;
            --DOCEAVASMINIMASPRIMASERVICIOS := 6;
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT + MI_DIASPS_NOPAGADOS ;
        ELSE
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        END IF;
        IF PCK_NOMINA.GL_SPRC <> 99 THEN
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF ((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0) THEN
                    IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFPS) <> i AND PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFPS) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(PCK_NOMINA.GL_FECHAFPS)) THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;
                    --(APINEDA:22/05/2019)-Se esta duplicando el concepto 953
                    PCK_NOMINA.CN(953) := (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            FOR i IN 6..12 LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    --(APINEDA:22/05/2019)-Se esta duplicando el concepto 953
                    PCK_NOMINA.CN(953) := (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
        END IF;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        IF PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS > 12 THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;
        IF PCK_NOMINA.FC_CN(952) > 0 THEN
            PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
            PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953) / 30);
        END IF;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > (PCK_NOMINA.GL_FECHAFPS - 180) AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DOCEAVAS := 0;

            --Revisar Doceavas Causadas, ya que no cumplio Más de 6 meses Radicado --RADICADO-- --> 01/06/15  a: --NOMBRES--.
            MI_MSG(1).CLAVE := 'NOMBRES';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(2).CLAVE := 'RADICADO';
            MI_MSG(2).VALOR := '201520160102642';

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_DOCEAVASCAUSADAS
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
        END IF;

        IF (PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS) OR (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;

            --Revisar Doceavas Causadas, ya que no cumplio Más de 6 meses Radicado --RADICADO-- --> 01/06/15  a: --NOMBRES--.
            MI_MSG(1).CLAVE := 'NOMBRES';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(2).CLAVE := 'RADICADO';
            MI_MSG(2).VALOR := '201520160102642';

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_DOCEAVASCAUSADAS
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
        END IF;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953)) + MI_DIASPS_NOPAGADOS ;
            IF (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953)) >= 180 THEN
                PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
            END IF;
        ELSE
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        END IF;

        IF PCK_NOMINA.FC_CN(160) < 0 THEN
            PCK_NOMINA.CN(160) := 0;
        END IF;
        IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT + MI_DIASPS_NOPAGADOS ;
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953)) + MI_DIASPS_NOPAGADOS ;
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
            ELSE
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
            END IF;
        END IF;

        IF MI_PSPAGADA > 0 AND PCK_NOMINA.FC_CN(160) > 0 AND PCK_NOMINA.GL_PROCESOACTUAL <> 99 AND PCK_NOMINA.GL_PERIODOACTUAL <> 7 THEN

            PCK_NOMINA.CN(160) := 0;
            --La prima Semestral ya debió ser pagada, según datos históricos, revise pagos. se eliminara éste concepto en el pago de liquidación a: --NOMEMPLEADO--, Cédula No.--CEDULA--, Tipo: --TIPO--.
            MI_MSG(1).CLAVE := 'NOMEMPLEADO';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(2).CLAVE := 'CEDULA';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
            MI_MSG(3).CLAVE := 'TIPO';
            MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_PRIMASEMESTRALPAGA
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );

        END IF;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > (PCK_NOMINA.GL_FECHAFPS - 180) AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
                PCK_NOMINA.GL_DOCEAVAS := 0;
                PCK_NOMINA.GL_DCC := 0;
            END IF;
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        END IF;
    END IF;
    --APINEDA 2018/09/05 
    IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL AND  PCK_NOMINA.GL_PERIODOACTUAL = 7 AND PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
        IF (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')) < 180) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO >= TO_DATE('03/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'))  THEN		
			IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO) >= 180 THEN						
                --(APINEDA:19/12/2018)-Se guarda fecha de ingreso como fecha de inicio de la prima semestral para el cálulo de beneficios
                PCK_NOMINA.GL_FECHAIPS := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO;            
				PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO) - (PCK_NOMINA.FC_CN(953)) + MI_DIASPS_NOPAGADOS;
				PCK_NOMINA.CN(160) := (PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0)) - PCK_NOMINA.FC_CNA(160);			                
			END IF;
		END IF;
	END IF;    

    PCK_NOMINA.GL_PRIMAJUN := PCK_NOMINA.FC_CN(160);
    --DIASPACTADOS := PCK_NOMINA.FC_CN(67);
    IF PCK_NOMINA.GL_DOCEAVAS = 0 THEN
        PCK_NOMINA.GL_FACTORPS1 := PCK_NOMINA.GL_PRIMAJUN;
    ELSE
        PCK_NOMINA.GL_FACTORPS1 := PCK_NOMINA.GL_PRIMAJUN - (CASE WHEN PCK_NOMINA.GL_AUXT = 0 THEN 0 ELSE (PCK_NOMINA.GL_AUXT / PCK_NOMINA.GL_DOCEAVAS) END);
    END IF;

    MI_INDRETIR := PCK_NOMINA.FC_CN(404);
    PCK_NOMINA.CN(945) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..699 LOOP
            IF (i <> 125) AND (i <> 303) AND (i <> 301) AND (i <> 300) AND (i < 599) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(404) := MI_INDRETIR;
  --(APINEDA:02/11/2018)-Se comenta asignación de doceavas al concepto 67 debido a que este corresponde a DIAS PACTADOS PARA PRIMAS SEMESTRALES
  --PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS;        

    PCK_NOMINA.CN(160) := PCK_NOMINA.GL_PRIMAJUN;

    PCK_NOMINA.CN(947) := PCK_NOMINA.GL_VPT;
    PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(949) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(950) := PCK_NOMINA.GL_GRPNGV  ;
    PCK_NOMINA.CN(951) := PCK_NOMINA.FC_CN(67) ;
    PCK_NOMINA.CN(952) := PCK_NOMINA.GL_DCC ;

    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.CN(991) := PCK_NOMINA.FC_CNPR(994) ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

        PCK_NOMINA.CN(992) := PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503);
        PCK_NOMINA.CN(993) := PCK_NOMINA.FC_CN(160) ;

        PCK_NOMINA.CN(994) := PCK_NOMINA.FC_CN(992) - PCK_NOMINA.FC_CN(991) + PCK_NOMINA.FC_CN(993) ;
    END IF;

    PCK_NOMINA.CN(985) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS), 0) ;

    PCK_NOMINA.GL_PS_BASE := PCK_NOMINA.GL_FACTORPS;
    PCK_NOMINA.GL_PS_DIAS := PCK_NOMINA.GL_DCC;
    MI_VALORCN2 := PCK_NOMINA.FC_CN(2);

END PR_CALCULARPRIMASEMESTRALANE;

PROCEDURE PR_CALCULARCESANTIASIDSN
(
/*
NAME              : PR_CALCULARCESANTIASIDSN
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
DATE MIGRADOR     : 21/05/2018
TIME              :
SOURCE MODULE     : NOMINAP2018.05.05, En access calcularcesantiasIDSN;
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       :
@NAME:  CALCULARCESANTIASIDSN
*/
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_PROMFAC              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIASPROMEDIO         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIAS                 PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASINT              PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_ANTICIPOS            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CESANTIA1            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN

    --BASCES := 0;
    --RECARGOSUELDO := 0;
    MI_PROMFAC := 0;
    IF NOT(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO < PCK_NOMINA.GL_FECHAINI) OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN --(MZANGUNA:17/12/2018)-Se agrega condicion si la fecha de retiro es nula
        PCK_NOMINA.GL_SBM := (CASE WHEN PCK_NOMINA.FC_CN(900) = 0 THEN PCK_NOMINA.FC_CN(1) ELSE PCK_NOMINA.FC_CN(900) END);
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN1) THEN
                PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
                PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
                PCK_NOMINA.GL_PERA := (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) < 16 THEN 1 ELSE 2 END);
                MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFIN1);
            ELSE
                PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
                PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
                PCK_NOMINA.GL_PERA := (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN)) < 16 THEN  1 ELSE 2 END);
                MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN), PCK_NOMINA.GL_FECHAFIN1);
            END IF;

            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_COMISIONES := (PCK_NOMINA.FC_CNA(188) / MI_DIASPROMEDIO) * 30;
            PCK_NOMINA.CN(971) := PCK_NOMINA.GL_COMISIONES;
        END IF;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
            PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM + PCK_NOMINA.GL_COMISIONES;
        ELSE
            PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM;
        END IF;
        PCK_NOMINA.GL_BONPAGADA := 0;

        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR),(CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
            PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
            --MI_DIAS := MI_DIAS + MASDIASOTRAENTIDAD;
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1);
            --DP := 360;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        ELSE
            PCK_NOMINA.GL_FECHAIC := (CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END);
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC), (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIC) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1 - 31);
        END IF;
        MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END), PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
        IF PCK_NOMINA.FC_CNA(155) = 0 THEN
            PCK_NOMINA.GL_PVAC := 0 + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501) ;
        ELSE
            IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)) / PCK_NOMINA.FC_CNA(164), 0) + PCK_NOMINA.FC_CN(155);
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501);
            END IF;
        END IF;
        PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CNA(150) > 0 THEN
            PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514)) / 12, 0);
        ELSIF PCK_NOMINA.FC_CN(150) > 0 THEN
            PCK_NOMINA.GL_BONPAGADA := PCK_NOMINA.FC_CN(150) ;
        END IF;
        PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
        PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
        PCK_NOMINA.CN(902) := (CASE WHEN PCK_NOMINA.FC_CN(902) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70)) / 12, 0) ELSE PCK_NOMINA.FC_CN(902) END);
        PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) ;
        IF PCK_NOMINA.FC_CN(905) = 0 AND PCK_NOMINA.FC_CN(411) <> 0 THEN
            PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_CALCULO.FC_VALORULTIMAPRIMANAVIDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
        END IF;

        MI_PROMFAC := (CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END)
                    + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_AUXT + PCK_NOMINA.FC_CN(906) + PCK_NOMINA.FC_CN(905) + PCK_NOMINA.FC_CN(907) + PCK_NOMINA.FC_CN(902) + PCK_NOMINA.GL_BONPAGADA END);
        MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC, 0);
        PCK_NOMINA.CN(969) := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC, 0);
        IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHACS, PCK_NOMINA.GL_FECHAFIN1) <= 90 AND PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) < 360 THEN
            MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND((((MI_PROMFAC)) * MI_DIAS / 360), 0) - MI_ANTICIPOS;
        ELSE
            IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) < 360 THEN
                MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND((((MI_PROMFAC)) * MI_DIAS / 360), 0) - MI_ANTICIPOS;
            ELSE
                MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)) * MI_DIAS / 360, 0) - MI_ANTICIPOS;
            END IF;
        END IF;
        IF PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.FC_CN(411) <> 0 THEN
            IF NOT(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1) THEN
                PCK_NOMINA.CN(169) := (CASE WHEN PCK_NOMINA.FC_CN(169) = 0 THEN (CASE WHEN MI_CESANTIA1 < 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) END) ELSE PCK_NOMINA.FC_CN(169) END);
            END IF;
            PCK_NOMINA.CN(177) := (CASE WHEN PCK_NOMINA.FC_CN(177) = 0 THEN MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(177) END);
        ELSIF PCK_NOMINA.FC_CN(412) <> 0 THEN
            IF NOT(PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996' , 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1) THEN
                PCK_NOMINA.CN(269) := (CASE WHEN PCK_NOMINA.FC_CN(269) = 0 THEN (CASE WHEN MI_CESANTIA1 < 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) END) ELSE PCK_NOMINA.FC_CN(269) END);
            END IF;
            PCK_NOMINA.CN(277) := (CASE WHEN PCK_NOMINA.FC_CN(277) = 0 THEN MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(277) END);
            IF PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = '12' THEN
                PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA, 1, (PCK_NOMINA.GL_SANO + 1), 1, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 169, PCK_NOMINA.FC_CN(269));
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
        PCK_NOMINA.CN(914) := PCK_NOMINA.GL_VPT;
        PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1);

    END IF; --TERM:

END PR_CALCULARCESANTIASIDSN;

PROCEDURE PR_CALCULARCESANTIASCAJICA(
/*
NAME              : PR_CALCULARCESANTIASCAJICA
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
DATE MIGRADOR     : 21/05/2018
TIME              :
SOURCE MODULE     : NOMINAP2018.05.05, En access calcularcesantiasIDSN;
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       :
@NAME:  CALCULARCESANTIASCAJICA
*/
UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_PROMFAC              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIASPROMEDIO         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIAS                 PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASINT              PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_ANTICIPOS            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CESANTIA1            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN

    --BASCES := 0;
    --RECARGOSUELDO := 0;
    MI_PROMFAC := 0;
    IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHACS, PCK_NOMINA.GL_FECHAFIN1) > 90 THEN
        PCK_NOMINA.GL_SBM := (CASE WHEN PCK_NOMINA.FC_CN(900) = 0 THEN PCK_NOMINA.FC_CN(1) ELSE PCK_NOMINA.FC_CN(900) END);
    ELSE
        PCK_NOMINA.GL_SBM := (CASE WHEN PCK_NOMINA.FC_CN(900) = 0 THEN PCK_NOMINA.FC_CN(1) ELSE PCK_NOMINA.FC_CN(900) END);
    END IF;
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN1) THEN
            PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
            PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
            PCK_NOMINA.GL_PERA := (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) < 16 THEN 1 ELSE 2 END);
            MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFIN1);
        ELSE
            PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
            PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
            PCK_NOMINA.GL_PERA := (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN)) < 16 THEN 1 ELSE 2 END);
            MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN), PCK_NOMINA.GL_FECHAFIN1);
        END IF;

        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.GL_COMISIONES := (PCK_NOMINA.FC_CNA(188) / MI_DIASPROMEDIO) * 30;
        PCK_NOMINA.CN(971) := PCK_NOMINA.GL_COMISIONES;
    END IF;
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
        PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM + PCK_NOMINA.GL_COMISIONES;
    ELSE
        PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM;
    END IF;


    IF PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996', 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR), (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
        MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
        --MI_DIAS := MI_DIAS + MASDIASOTRAENTIDAD - PCK_NOMINA.GL_LICENCIAS;
        MI_DIAS := MI_DIAS - PCK_NOMINA.GL_LICENCIAS;

        MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1);
        --DP := 360;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    ELSE
        PCK_NOMINA.GL_FECHAIC := (CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END);
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC), (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIC) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
        PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
        MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
        MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1 - 10);
    END IF;
    MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END), PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
    IF PCK_NOMINA.GL_SMES < '12' THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
    ELSE
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
    END IF;
    IF PCK_NOMINA.FC_CNA(155) = 0 THEN
        PCK_NOMINA.GL_PVAC := 0 ;
        IF PCK_NOMINA.FC_CN(155) > 0 THEN
            PCK_NOMINA.GL_PVAC := 0 ;
        END IF;
    ELSE
        IF PCK_NOMINA.FC_CNA(164) > 1 THEN
            PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)) / 12, 0) ;
        ELSE
            PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)) / 12, 0) ;
        END IF;
    END IF;
    PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(504) + PCK_NOMINA.FC_CNA(504)) / 12, 0);
    PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CNA(503)) / 12, 0);

    MI_PROMFAC := (CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) + PCK_NOMINA.GL_AUXA
                    + PCK_NOMINA.GL_AUXT + PCK_NOMINA.FC_CN(905) + PCK_NOMINA.GL_PVAC + PCK_NOMINA.FC_CNA(70) / 12 END) + PCK_NOMINA.FC_CN(906);
    PCK_NOMINA.CN(902) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(70) / 12, 0) ;
    PCK_NOMINA.CN(969) := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC, 0);
    MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC, 0);
    IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHACS, PCK_NOMINA.GL_FECHAFIN1) <= 90 AND PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) < 360 THEN
        MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND((((MI_PROMFAC)) * MI_DIAS / 360), 0) - MI_ANTICIPOS;
    ELSE
        IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) < 360 THEN
            MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND((((MI_PROMFAC)) * MI_DIAS / 360), 0) - MI_ANTICIPOS;
        ELSE
            MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)) * MI_DIAS / 360, 0) - MI_ANTICIPOS;
        END IF;
    END IF;
    IF PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.FC_CN(411) <> 0 THEN
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 2 THEN
            PCK_NOMINA.CN(169) := (CASE WHEN PCK_NOMINA.FC_CN(169) = 0 THEN (CASE WHEN MI_CESANTIA1 < 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) END) ELSE PCK_NOMINA.FC_CN(169) END);
        END IF;
        PCK_NOMINA.CN(177) := (CASE WHEN PCK_NOMINA.FC_CN(177) = 0 THEN MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(177) END);
    ELSIF PCK_NOMINA.FC_CN(412) <> 0 THEN
        IF NOT(PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996' ,'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1) THEN
            PCK_NOMINA.CN(269) := (CASE WHEN PCK_NOMINA.FC_CN(269) = 0 THEN (CASE WHEN MI_CESANTIA1 < 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) END) ELSE PCK_NOMINA.FC_CN(269) END);
        END IF;
        PCK_NOMINA.CN(277) := (CASE WHEN PCK_NOMINA.FC_CN(277) = 0 THEN MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(277) END);
        IF PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = '12' THEN
            PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA, 1, (PCK_NOMINA.GL_SANO + 1), 1, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 169, PCK_NOMINA.FC_CN(269));
        END IF;
    END IF;

    PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(1);
    PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;


    PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC, 0);
    PCK_NOMINA.CN(908) := 0;
    PCK_NOMINA.CN(909) := 0;
    PCK_NOMINA.CN(910) := MI_DIAS;
    PCK_NOMINA.CN(911) := MI_ANTICIPOS;
    PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
    PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0);
    PCK_NOMINA.CN(914) := 0;
    PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1);


END PR_CALCULARCESANTIASCAJICA;

PROCEDURE PR_CALCULARCESANTIASGOBCAQUETA(
/*
NAME              : PR_CALCULARCESANTIASCAJICA
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
DATE MIGRADOR     : 21/05/2018
TIME              :
SOURCE MODULE     : NOMINAP2018.05.05, En access calcularcesantiasGOBCAQUETA;
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       :
@NAME:  CALCULARCESANTIASGOBCAQUETA
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
BEGIN

    MI_DIASCES_ENC := 0;
    MI_PROM_ENC := 0;
    MI_ANTIC_ENC := 0;

    --ALIMRET := 0;
    --BASCES := 0;
    --RECARGOSUELDO := 0;
    MI_PROMFAC := 0;
    IF NOT(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO < PCK_NOMINA.GL_FECHAINI) OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN --(MZANGUNA:17/12/2018)-Se agrega condicion si la fecha de retiro es nula
        PCK_NOMINA.GL_SBM := (CASE WHEN PCK_NOMINA.FC_CN(900) = 0 THEN PCK_NOMINA.FC_CN(1) ELSE PCK_NOMINA.FC_CN(900) END);
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN1) THEN
                PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
                PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
                PCK_NOMINA.GL_PERA := (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) < 16 THEN 1 ELSE 2 END);
                MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFIN1);
            ELSE
                PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
                PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
                PCK_NOMINA.GL_PERA := (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN)) < 16 THEN 1 ELSE 2 END);
                MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN), PCK_NOMINA.GL_FECHAFIN1);
            END IF;


            PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_COMISIONES := (PCK_NOMINA.FC_CNA(188) / MI_DIASPROMEDIO) * 30;
            PCK_NOMINA.CN(971) := PCK_NOMINA.GL_COMISIONES;
        END IF;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
            PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM + PCK_NOMINA.GL_COMISIONES;
        ELSE
            PCK_NOMINA.GL_SBM := PCK_NOMINA.GL_SBM;
        END IF;
        PCK_NOMINA.GL_BONPAGADA := 0;


        IF PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996' ,'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
            PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR), (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359);
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
            --MI_DIAS := MI_DIAS + MASDIASOTRAENTIDAD;
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1);
            --DP := 360;
            PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_FECHAIC := PCK_NOMINA.GL_FECHAIR;
        ELSE
            PCK_NOMINA.GL_FECHAIC := (CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END);
            PCK_NOMINA.GL_FECHAIC := (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > PCK_NOMINA.GL_FECHAIC AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA ELSE PCK_NOMINA.GL_FECHAIC END);

            PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC), (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIC) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
            PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359);
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1 - 10);
        END IF;
        MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END), PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
        IF PCK_NOMINA.FC_CNA(155) = 0 THEN
            PCK_NOMINA.GL_PVAC := 0 + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501) ;
        ELSE
            IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)), 0) + PCK_NOMINA.FC_CN(155) ;
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501);
            END IF;
        END IF;

        PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;
        PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CNA(150) > 0 THEN
            PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538)) / 12, 0);
        ELSIF PCK_NOMINA.FC_CN(150) > 0 THEN
            PCK_NOMINA.GL_BONPAGADA := ((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12) ;
            PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538)) / 12, 0);
        END IF;
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
        PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
        PCK_NOMINA.CN(902) := (CASE WHEN PCK_NOMINA.FC_CN(902) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(519)) / 12, 0) ELSE PCK_NOMINA.FC_CN(902) END);
        PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) ;

        MI_PROMFAC := (CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPA
                            + PCK_NOMINA.GL_VPP + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_AUXT + PCK_NOMINA.FC_CN(906) + PCK_NOMINA.FC_CN(905) + PCK_NOMINA.FC_CN(907) + PCK_NOMINA.FC_CN(902) + PCK_NOMINA.GL_BONPAGADA END);
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
            MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND((((MI_PROMFAC)) * MI_DIAS / 360), 0) + (CASE WHEN MI_DIASCES_ENC > 0 THEN MI_ANTIC_ENC ELSE 0 END) - MI_ANTICIPOS;
        ELSE
            IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) < 360 THEN
                MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND((((MI_PROMFAC)) * MI_DIAS / 360), 0) + (CASE WHEN MI_DIASCES_ENC > 0 THEN MI_ANTIC_ENC ELSE 0 END) - MI_ANTICIPOS;
            ELSE
                MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND(((MI_PROMFAC)) * MI_DIAS / 360, 0) + (CASE WHEN MI_DIASCES_ENC > 0 THEN MI_ANTIC_ENC ELSE 0 END) - MI_ANTICIPOS;
            END IF;
        END IF;
        IF PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.FC_CN(411) <> 0 THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 2 THEN
                PCK_NOMINA.CN(169) := (CASE WHEN PCK_NOMINA.FC_CN(169) = 0 THEN (CASE WHEN MI_CESANTIA1 < 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) END) ELSE PCK_NOMINA.FC_CN(169) END);
            END IF;
            PCK_NOMINA.CN(177) := (CASE WHEN PCK_NOMINA.FC_CN(177) = 0 THEN MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(177) END);
        ELSIF PCK_NOMINA.FC_CN(412) <> 0 THEN
            IF NOT(PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996' ,'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1) THEN
                PCK_NOMINA.CN(269) := (CASE WHEN PCK_NOMINA.FC_CN(269) = 0 THEN (CASE WHEN MI_CESANTIA1 < 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) END) ELSE PCK_NOMINA.FC_CN(269) END);
            END IF;
            PCK_NOMINA.CN(277) := (CASE WHEN PCK_NOMINA.FC_CN(277) = 0 THEN MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(277) END);
            IF PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = '12' THEN
                PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA, 1, (PCK_NOMINA.GL_SANO + 1), 1, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 169, PCK_NOMINA.FC_CN(269));
            END IF;
        END IF;
        PCK_NOMINA.CN(900) := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END);
        PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
        PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
        PCK_NOMINA.CN(901) := PCK_NOMINA.GL_VPA;
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.CN(910) := MI_DIAS;
        PCK_NOMINA.CN(911) := MI_ANTICIPOS;
        PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
        PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0);
        PCK_NOMINA.CN(914) := PCK_NOMINA.GL_VPT;
        PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1) + 1;
        PCK_NOMINA.CN(908) := PCK_NOMINA.GL_VPP;

    END IF;
END PR_CALCULARCESANTIASGOBCAQUETA;

PROCEDURE PR_CALCPRIMASEMESTRALALCCAJICA(
/*
NAME              : PR_CALCPRIMASEMESTRALALCCAJICA
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
DATE MIGRADOR     : 16/05/2018
TIME              : 10:29 AM
SOURCE MODULE     : NOMINAP2018.05.05_UNIFICADAS En access calcularprimasemestralALCCAJICA
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       :
@NAME:  CALCULARPRIMASEMESTRALCCAJICA
*/
UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)

AS
    MI_DOCEAVASMINIMASPS            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_INDRETIR                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PSPAGADA                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_MSG                          PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

    /*IF ISNULL(PAR('PCK_NOMINA.GL_DOCEAVAS MINIMAS PRIMA SERVICIOS')) OR PAR('PCK_NOMINA.GL_DOCEAVAS MINIMAS PRIMA SERVICIOS') = '' THEN
        INSERTARPARAMETRO 'PCK_NOMINA.GL_DOCEAVAS MINIMAS PRIMA SERVICIOS', '6'
    END IF;*/
    MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1'));
    PCK_NOMINA.GL_FACTORPS := 0;
    PCK_NOMINA.GL_FACTORPS1 := 0;
    PCK_NOMINA.GL_DNT := 0;
    --DNT1 := 0;
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY' ) ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
        PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL < PCK_NOMINA.GL_FECHAI THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL ELSE PCK_NOMINA.GL_FECHAIPS END);
        IF PCK_NOMINA.GL_FECHAIPS = TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL, 'DD/MM/YYYY'), 'DD/MM/YYYY') THEN
            --La prima Semestral se calcula con la fecha de ingreso real o continuidad: --FECHADISTR-- a: --NOMEMPLEADO--, Cédula No. --CEDULA--, Tipo: --TIPO--
            MI_MSG(1).CLAVE := 'FECHADISTR';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL;
            MI_MSG(2).CLAVE := 'NOMEMPLEADO';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(3).CLAVE := 'CEDULA';
            MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
            MI_MSG(4).CLAVE := 'TIPO';
            MI_MSG(4).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_PRIMASEMESFECHACONTIN
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );

            IF PCK_NOMINA.GL_FECHAIPS < TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') THEN
                PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY' ) ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END;
            END IF;
        END IF;
    END IF;
    IF PCK_NOMINA.GL_SMES > 7 THEN
        PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    END IF;
    IF PCK_NOMINA.GL_SMES = 7 THEN
        IF PCK_NOMINA.GL_SPER = 4 THEN
            PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN (CASE WHEN PCK_NOMINA.GL_FECHAFIN1 > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAFIN1 END) ELSE TO_DATE('30/06/' || (PCK_NOMINA.GL_SANO), 'DD/MM/YYYY') END;
        ELSIF PCK_NOMINA.GL_SPER = 7 THEN
            IF PCK_NOMINA.GL_SMES = 7 THEN
                PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
                PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/0/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            END IF;
            PCK_NOMINA.GL_FECHAFPS := (CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO ELSE PCK_NOMINA.GL_FECHAFIN1 END);
        ELSE
            PCK_NOMINA.GL_FECHAFPS := (CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN (CASE WHEN PCK_NOMINA.GL_FECHAFIN1 > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAFIN1 END) ELSE TO_DATE('30/06/' || (PCK_NOMINA.GL_SANO), 'DD/MM/YYYY') END);
        END IF;
        PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL < PCK_NOMINA.GL_FECHAI THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL ELSE PCK_NOMINA.GL_FECHAIPS END);
        IF PCK_NOMINA.GL_FECHAIPS = TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL, 'DD/MM/YYYY'), 'DD/MM/YYYY') THEN
            --La prima Semestral se calcula con la fecha de ingreso real o continuidad: --FECHADISTR-- a: --NOMEMPLEADO--, Cédula No. --CEDULA--, Tipo: --TIPO--
            MI_MSG(1).CLAVE := 'FECHADISTR';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESODISTRITOREAL;
            MI_MSG(2).CLAVE := 'NOMEMPLEADO';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(3).CLAVE := 'CEDULA';
            MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
            MI_MSG(4).CLAVE := 'TIPO';
            MI_MSG(4).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_PRIMASEMESFECHACONTIN
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
        END IF;
    ELSE
        PCK_NOMINA.GL_FECHAFPS := (CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || (PCK_NOMINA.GL_SANO), 'DD/MM/YYYY') END);
    END IF;

    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    END IF;
    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
    PCK_NOMINA.CN(946) := 0;
    IF PCK_PARST.FC_PAR('SUMAR BASP A PRIMA SERVICIOS DECRETO 2351', 'NO') = 'SI' THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0) ;
        IF PCK_NOMINA.FC_CN(150) >= 1 THEN
            IF PCK_NOMINA.GL_SPER <> 7 THEN
                PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(150) / 12, 0);
            END IF;
        END IF;
    END IF;
    PCK_NOMINA.GL_FACTORPS := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(946), 0);
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;


    IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 7) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DOCEAVAS := 6;
        --TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1')) := 6;
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) / 30);
    END IF;
    FOR i IN 7..12 LOOP
        PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
            PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
            PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
        END IF;
    END LOOP;
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        FOR i IN 1..CASE WHEN PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO) = PCK_NOMINA.GL_SMES THEN  (PCK_NOMINA.GL_SMES - 1) ELSE PCK_NOMINA.GL_SMES END LOOP
            PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 9, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
            END IF;
        END LOOP;
    END IF;

    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 180 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > (PCK_NOMINA.GL_FECHAFPS - 180) AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DOCEAVAS := 0;

        --Revisar Doceavas Causadas, ya que no cumplio Más de 6 meses Radicado --RADICADO--  --> 01/06/15  a: --NOMBRES--.
        MI_MSG(1).CLAVE := 'RADICADO';
        MI_MSG(1).VALOR := '20152060102642';
        MI_MSG(2).CLAVE := 'NOMBRES';
        MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;

        PCK_NOMINA_COM7.PR_ALERTA
            (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
            ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_DOCEAVASCAUSADAS
            ,UN_REEMPLAZOS   => MI_MSG
            ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
            ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
            ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
            ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
            ,UN_USER         => PCK_CONEXION.FC_GETUSER
            );

    END IF;
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESTADO_ACTUAL = 6 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION > PCK_NOMINA.GL_FECHAIPS THEN TO_DATE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION) ELSE PCK_NOMINA.GL_FECHAIPS END) - (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION < PCK_NOMINA.GL_FECHAFPS THEN TO_DATE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION) ELSE PCK_NOMINA.GL_FECHAFPS END) + 1;
    END IF;
    IF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('16/06/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAIPS <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 THEN
        PCK_NOMINA.GL_DOCEAVAS := (CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END);
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESTADO_ACTUAL = 6 THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION > PCK_NOMINA.GL_FECHAIPS THEN TO_DATE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION) ELSE PCK_NOMINA.GL_FECHAIPS END) - (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION < PCK_NOMINA.GL_FECHAFPS THEN TO_DATE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION) ELSE PCK_NOMINA.GL_FECHAFPS END) + 1;
            ELSE
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
            END IF;
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        END IF;
    ELSIF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_DOCEAVAS >= 1 THEN
        IF PCK_NOMINA.GL_DCC < 180 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;
        PCK_NOMINA.GL_DOCEAVAS := (CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END);
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESTADO_ACTUAL = 6 THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - ((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION > PCK_NOMINA.GL_FECHAIPS THEN TO_DATE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION) ELSE PCK_NOMINA.GL_FECHAIPS END) - (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION < PCK_NOMINA.GL_FECHAFPS THEN TO_DATE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION) ELSE PCK_NOMINA.GL_FECHAFPS END)) + 1;
            ELSE
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
            END IF;
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        END IF;
    ELSIF PCK_NOMINA.FC_CN(404) = 1 THEN

        MI_PSPAGADA := 0;
        IF PCK_NOMINA.GL_SMES = 6 OR PCK_NOMINA.GL_SMES = 7 THEN
            PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 160, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, 7, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_PSPAGADA := PCK_NOMINA.FC_CNP(160);
        END IF;
        PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END);
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
        IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 6) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DOCEAVAS := 6;
            --TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1')) := 6;
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
        ELSE
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        END IF;
        IF PCK_NOMINA.GL_SPRC <> 99 THEN
            FOR i IN 1.. CASE WHEN PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO) = PCK_NOMINA.GL_SMES THEN (PCK_NOMINA.GL_SMES - 1) ELSE  PCK_NOMINA.GL_SMES END LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            FOR i IN 6..12 LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
        END IF;

        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        IF PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS > 12 THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;
        IF PCK_NOMINA.FC_CN(952) > 0 THEN
            PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
            PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953) / 30);
        END IF;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > (PCK_NOMINA.GL_FECHAFPS - 180) AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DOCEAVAS := 0;

            --Revisar Doceavas Causadas, ya que no cumplio Más de 6 meses Radicado --RADICADO--  --> 01/06/15  a: --NOMBRES--.
            MI_MSG(1).CLAVE := 'RADICADO';
            MI_MSG(1).VALOR := '201520160102642';
            MI_MSG(2).CLAVE := 'NOMBRES';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_DOCEAVASCAUSADAS
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
        END IF;

        IF (PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS) OR (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;

            --Revisar Doceavas Causadas, ya que no cumplio Más de 6 meses Radicado --RADICADO--  --> 01/06/15  a: --NOMBRES--.
            MI_MSG(1).CLAVE := 'RADICADO';
            MI_MSG(1).VALOR := '201520160102642';
            MI_MSG(2).CLAVE := 'NOMBRES';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_DOCEAVASCAUSADAS
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
        END IF;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESTADO_ACTUAL = 6 THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - ((CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION > PCK_NOMINA.GL_FECHAIPS THEN TO_DATE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_INICIO_COMISION) ELSE PCK_NOMINA.GL_FECHAIPS END) - (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION < PCK_NOMINA.GL_FECHAFPS THEN TO_DATE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL_COMISION) ELSE PCK_NOMINA.GL_FECHAFPS END)) + 1;
            ELSE
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
            END IF;
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        END IF;
        IF PCK_NOMINA.FC_CN(160) < 0 THEN
            PCK_NOMINA.CN(160) := 0;
        END IF;
        IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
            ELSE
                PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
            END IF;
        END IF;
        IF MI_PSPAGADA > 0 AND PCK_NOMINA.FC_CN(160) > 0 THEN
            PCK_NOMINA.CN(160) := 0;

            --La prima Semestral ya debió ser pagada, según datos históricos, revise pagos. se eliminara éste concepto en el pago de liquidación a: --NOMEMPLEADO--, Cédula No.--CEDULA--, Tipo: --TIPO--.
            MI_MSG(1).CLAVE := 'NOMEMPLEADO';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(2).CLAVE := 'CEDULA';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
            MI_MSG(3).CLAVE := 'TIPO';
            MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_PRIMASEMESTRALPAGA
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
        END IF;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := (CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END);
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > (PCK_NOMINA.GL_FECHAFPS - 180) AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
                PCK_NOMINA.GL_DOCEAVAS := 0;
                PCK_NOMINA.GL_DCC := 0;
            END IF;
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        END IF;
    END IF;
    PCK_NOMINA.GL_PRIMAJUN := PCK_NOMINA.FC_CN(160);
    --DIASPACTADOS := PCK_NOMINA.FC_CN(67);
    IF PCK_NOMINA.GL_DOCEAVAS=0 THEN
      PCK_NOMINA.GL_FACTORPS1 := PCK_NOMINA.GL_PRIMAJUN ;
     ELSE 
    PCK_NOMINA.GL_FACTORPS1 := PCK_NOMINA.GL_PRIMAJUN - (CASE WHEN PCK_NOMINA.GL_AUXT = 0 THEN 0 ELSE (PCK_NOMINA.GL_AUXT / PCK_NOMINA.GL_DOCEAVAS) END);
    END IF;
    MI_INDRETIR := PCK_NOMINA.FC_CN(404);
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..699 LOOP
            IF (i <> 125) AND (i <> 303) AND (i <> 301) AND (i <> 300) AND (i < 599) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;

    PCK_NOMINA.CN(404) := MI_INDRETIR;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS;

    PCK_NOMINA.CN(160) := PCK_NOMINA.GL_PRIMAJUN;
    PCK_NOMINA.CN(945) := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END);
    PCK_NOMINA.CN(947) := PCK_NOMINA.GL_VPT   ;
    PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXT ;
    PCK_NOMINA.CN(949) := PCK_NOMINA.GL_AUXA ;
    PCK_NOMINA.CN(950) := PCK_NOMINA.GL_GRPNGV  ;
    PCK_NOMINA.CN(951) := PCK_NOMINA.FC_CN(67) ;
    PCK_NOMINA.CN(952) := PCK_NOMINA.GL_DCC ;
    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.CN(991) := PCK_NOMINA.FC_CNPR(994) ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

        PCK_NOMINA.CN(992) := PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503);
        PCK_NOMINA.CN(993) := PCK_NOMINA.FC_CN(160) ;

        PCK_NOMINA.CN(994) := PCK_NOMINA.FC_CN(992) - PCK_NOMINA.FC_CN(991) + PCK_NOMINA.FC_CN(993) ;
    END IF;
    PCK_NOMINA.GL_PS_BASE := PCK_NOMINA.GL_FACTORPS;
    PCK_NOMINA.GL_PS_DIAS := PCK_NOMINA.GL_DCC;

END PR_CALCPRIMASEMESTRALALCCAJICA;

PROCEDURE PR_CALCULARPRIMASEMESTRALIDSN (
/*
NAME              : PR_CALCPRIMASEMESTRALALCCAJICA
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
DATE MIGRADOR     : 22/05/2018
TIME              : 04:04 AM
SOURCE MODULE     : NOMINAP2018.05.05_UNIFICADAS En access calcularprimasemestralIDSN
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       :
@NAME:  PR_CALCULARPRIMASEMESTRALIDSN
*/
 UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)

AS
    MI_DOCEAVASMINIMASPS            PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASPRIMAVAC                 PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_SINDICALIZADO                BOOLEAN DEFAULT FALSE;
    MI_PSPAGADA                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_MSG                          PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_INDRETIR                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN


    --PORSINDICATO2 := MISINDICATO2(GETCOMPANY(), PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 3);

    MI_SINDICALIZADO := FALSE;
    MI_DIASPRIMAVAC := PCK_NOMINA.FC_CN(67);
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO = -1 AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_SINDICATO2 = 'SIN001' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_SINDICATO = 'SIN001') THEN
        MI_DIASPRIMAVAC := 40;
        PCK_NOMINA.CN(67) := 40;
        MI_SINDICALIZADO := TRUE;
    END IF;
    MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1'));
    PCK_NOMINA.GL_FACTORPS := 0;
    PCK_NOMINA.GL_FACTORPS1 := 0;
    PCK_NOMINA.GL_DNT := 0;
    --DNT1 := 0;
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END);
        PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END);
    END IF;
    IF PCK_NOMINA.GL_SMES > 7 THEN
        PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    END IF;
    IF PCK_NOMINA.GL_SMES = 7 THEN
        PCK_NOMINA.GL_FECHAFPS := (CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN (CASE WHEN PCK_NOMINA.GL_FECHAFIN1 > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAFIN1 END) ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END);
    ELSE
        PCK_NOMINA.GL_FECHAFPS := (CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END);
    END IF;
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), (CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    END IF;
    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359);
    IF (PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 4) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO >= PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO <= PCK_NOMINA.GL_FECHAFIN) THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
    ELSE
       --- PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0);
      PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(514)) / 12, 0);
    END IF;
    IF PCK_NOMINA.FC_CN(946) = 0 AND PCK_NOMINA.FC_CN(514) <> 0 THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(514) / 12, 0);
    END IF;
    IF MI_SINDICALIZADO THEN
        PCK_NOMINA.CN(946) := 0;
    END IF;

    PCK_NOMINA.GL_FACTORPS := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(946), 0) + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_VPP;
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
    IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 7) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA,TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DOCEAVAS := 6;
        PCK_PARST.LSTPAR('DOCEAVAS MINIMAS PRIMA SERVICIOS') := 6;
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
    END IF;
    FOR i IN 7..12 LOOP
        PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359)) > 0 THEN
            PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
            PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359));
        END IF;
    END LOOP;
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
            PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359)) > 0 THEN
                PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359));
            END IF;
        END LOOP;
    END IF;
    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA,TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 180 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA,TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA,TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > (PCK_NOMINA.GL_FECHAFPS - 180) AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA,TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA,TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DOCEAVAS := 0;
        --Revisar Doceavas Causadas, ya que no cumplio Más de 6 meses Radicado --RADICADO--  --> 01/06/15  a: --NOMBRES--.
        MI_MSG(1).CLAVE := 'RADICADO';
        MI_MSG(1).VALOR := '20152060102642';
        MI_MSG(2).CLAVE := 'NOMBRES';
        MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;

        PCK_NOMINA_COM7.PR_ALERTA
            (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
            ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_DOCEAVASCAUSADAS
            ,UN_REEMPLAZOS   => MI_MSG
            ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
            ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
            ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
            ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
            ,UN_USER         => PCK_CONEXION.FC_GETUSER
            );
    END IF;
    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
        PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC / 30);
    END IF;
    IF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('16/06/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAIPS <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 THEN
        PCK_NOMINA.GL_DOCEAVAS := (CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END);
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        END IF;
    ELSIF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_DOCEAVAS >= 1 THEN

        IF PCK_NOMINA.GL_DCC < 180 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA,TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA,TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;
        PCK_NOMINA.GL_DOCEAVAS := (CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END);
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        END IF;
    ELSIF PCK_NOMINA.FC_CN(404) = 1 THEN

        MI_PSPAGADA := 0;
        PCK_NOMINA.CNA(160) := 0;
        IF PCK_NOMINA.GL_SMES = '06' OR PCK_NOMINA.GL_SMES = '07' THEN
            PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 160, PCK_NOMINA.GL_SANO, 06, 01, PCK_NOMINA.GL_SANO, 7, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_PSPAGADA := PCK_NOMINA.FC_CNP(160);
        END IF;
        PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END);
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
        IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= '06') OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA,TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DOCEAVAS := 6;
            PCK_PARST.LSTPAR('DOCEAVAS MINIMAS PRIMA SERVICIOS') := 6;
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
        ELSE
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        END IF;
        IF PCK_NOMINA.GL_SPRC <> 99 THEN
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF ((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359)) > 0) THEN
                    IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFPS) <> i AND PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFPS) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(PCK_NOMINA.GL_FECHAFPS)) THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359));
                END IF;
            END LOOP;
            FOR i IN 6..12 LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359));
                END IF;
            END LOOP;
        END IF;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        IF PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS > 12 THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;
        IF PCK_NOMINA.FC_CN(952) > 0 THEN
            PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
            PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953) / 30);
        END IF;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > (PCK_NOMINA.GL_FECHAFPS - 180) AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA,TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA,TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DOCEAVAS := 0;
            --Revisar Doceavas Causadas, ya que no cumplio Más de 6 meses Radicado --RADICADO--  --> 01/06/15  a: --NOMBRES--.
            MI_MSG(1).CLAVE := 'RADICADO';
            MI_MSG(1).VALOR := '201520160102642';
            MI_MSG(2).CLAVE := 'NOMBRES';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_DOCEAVASCAUSADAS
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
        END IF;

        IF (PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS) OR (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;
            --Revisar Doceavas Causadas, ya que no cumplio Más de 6 meses Radicado --RADICADO--  --> 01/06/15  a: --NOMBRES--.
            MI_MSG(1).CLAVE := 'RADICADO';
            MI_MSG(1).VALOR := '201520160102642';
            MI_MSG(2).CLAVE := 'NOMBRES';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_DOCEAVASCAUSADAS
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
        END IF;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            IF (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953)) >= 180 THEN
                PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
            END IF;
        ELSE
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        END IF;
        IF PCK_NOMINA.FC_CN(160) < 0 THEN
            PCK_NOMINA.CN(160) := 0;
        END IF;
        IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 01, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359)) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CNA(359);
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359));
                END IF;
            END LOOP;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
            ELSE
                PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
            END IF;
        END IF;
        IF MI_PSPAGADA > 0 AND PCK_NOMINA.FC_CN(160) > 0 THEN
            PCK_NOMINA.CN(160) := 0;
            --La prima Semestral ya debió ser pagada, según datos históricos, revise pagos. se eliminara éste concepto en el pago de liquidación a: --NOMEMPLEADO--, Cédula No.--CEDULA--, Tipo: --TIPO--.
            MI_MSG(1).CLAVE := 'NOMEMPLEADO';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(2).CLAVE := 'CEDULA';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
            MI_MSG(3).CLAVE := 'TIPO';
            MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_DOCEAVASCAUSADAS
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );

        END IF;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := (CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END);
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > (PCK_NOMINA.GL_FECHAFPS - 180) AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA,TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) AND PCK_NOMINA.GL_FECHAIPS < PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA,TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
                PCK_NOMINA.GL_DOCEAVAS := 0;
                PCK_NOMINA.GL_DCC := 0;
            END IF;
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := (CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END) - PCK_NOMINA.FC_CNA(160);
        END IF;
    END IF;
    PCK_NOMINA.GL_PRIMAJUN := PCK_NOMINA.FC_CN(160);
    --DIASPACTADOS := PCK_NOMINA.FC_CN(67);
    IF PCK_NOMINA.GL_DOCEAVAS > 0 THEN
      PCK_NOMINA.GL_FACTORPS1 := PCK_NOMINA.GL_PRIMAJUN - (CASE WHEN PCK_NOMINA.GL_AUXT = 0 THEN 0 ELSE (PCK_NOMINA.GL_AUXT / PCK_NOMINA.GL_DOCEAVAS) END);
    ELSE
      PCK_NOMINA.GL_FACTORPS1 := PCK_NOMINA.GL_PRIMAJUN;
    END IF;
    MI_INDRETIR := PCK_NOMINA.FC_CN(404);
    PCK_NOMINA.CN(945) := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END);
    IF PCK_NOMINA.GL_SPER = '04' THEN
        FOR i IN 2..699 LOOP
            IF (i <> 125) AND (i <> 67) AND (i <> 303) AND (i <> 301) AND (i <> 300) AND (i < 599) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(404) := MI_INDRETIR;


    PCK_NOMINA.CN(160) := PCK_NOMINA.GL_PRIMAJUN;

    PCK_NOMINA.CN(947) := PCK_NOMINA.GL_VPA;
    PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(949) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(950) := PCK_NOMINA.GL_VPP;
    PCK_NOMINA.CN(951) := PCK_NOMINA.GL_DOCEAVAS ;
    PCK_NOMINA.CN(952) := PCK_NOMINA.GL_DCC ;

    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.CN(991) := PCK_NOMINA.FC_CNPR(994) ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.CN(992) := PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503);
        PCK_NOMINA.CN(993) := PCK_NOMINA.FC_CN(160) ;
        PCK_NOMINA.CN(994) := PCK_NOMINA.FC_CN(992) - PCK_NOMINA.FC_CN(991) + PCK_NOMINA.FC_CN(993) ;
    END IF;

    PCK_NOMINA.CN(985) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS), 0) ;

    PCK_NOMINA.GL_PS_BASE := PCK_NOMINA.GL_FACTORPS;
    PCK_NOMINA.GL_PS_DIAS := PCK_NOMINA.GL_DCC;

    IF (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO = -1 AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_SINDICATO2 = '002' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_SINDICATO = 'SIN002') AND PCK_NOMINA.GL_SPER = '04') THEN
        PCK_NOMINA.CN(129) := (CASE WHEN PCK_NOMINA.FC_CN(129) = 0 THEN TO_NUMBER(PCK_PARST.FC_PAR('VALOR CUOTA ADICIONAL SINDICATO SUNET CAQUETA', '0')) ELSE PCK_NOMINA.FC_CN(129) END);
    END IF;




END PR_CALCULARPRIMASEMESTRALIDSN;

PROCEDURE PR_CALCPRIMASEMESTRALGOBCAQUET(
/*
NAME              : PR_CALCPRIMASEMESTRALGOBCAQUET
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
DATE MIGRADOR     : 01/06/2020
TIME              : 02:19 PM
SOURCE MODULE     : NOMINAP2018.05.01_UNIFICADAS En access calcularprimasemestralALCTOCANCIPA
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       :
*/
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)

AS
    MI_DOCEAVASMINIMASPS         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PSPAGADA                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PRIMAJUNIO                   PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_INDRETIR                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_MSG                          PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALOR                            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN
    MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1'));
    PCK_NOMINA.GL_FACTORPS := 0;
    PCK_NOMINA.GL_FACTORPS1 := 0;
    PCK_NOMINA.GL_DNT := 0;

	  --(APINEDA:22/03/2019)-Se agregan validaciones faltantes para la fecha de inicio de la prima semestral debido a que no esta calculando el concepto 160 para liquidaciones finales. TAR 1000090732
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
        --(APINEDA:01/12/2018)-TAR1000087154 Se toma la fecha de ingreso como fecha de inicio cuando un funcionario se retira y en julio del año anterior no habia cumplido 180 dias trabajados
        IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 THEN
            IF (PCK_NOMINA.GL_FECHAIPS > PCK_NOMINA.GL_FECHAI) AND (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAIPS) < 180) THEN
                PCK_NOMINA.GL_FECHAIPS := PCK_NOMINA.GL_FECHAI;
            END IF;
        END IF;
    END IF;
	  --(APINEDA:22/03/2019)-Se agregan validaciones faltantes para la fecha de inicio de la prima semestral debido a que no esta calculando el concepto 160 para liquidaciones finales. TAR 1000090732
    IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 AND PCK_NOMINA.GL_SMES = 7  THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
    END IF;

    IF PCK_NOMINA.GL_SMES > 7 THEN
        PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    END IF;

    --(APINEDA:26/07/2019)-Se modifica CASE para asignar valor a la fecha final de la prima semestral debido a que estaba presentando inconsistencias.
    IF PCK_NOMINA.GL_SMES = 7 THEN
        PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN CASE WHEN PCK_NOMINA.GL_FECHAFIN1 > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
    ELSE
        PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
    END IF;

    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    END IF;
    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
    IF (PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 4) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO >= PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO <= PCK_NOMINA.GL_FECHAFIN) THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
    ELSE
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0);
    END IF;
    IF PCK_NOMINA.FC_CN(946) = 0 AND PCK_NOMINA.FC_CN(514) <> 0 THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(514) / 12, 0);
    END IF;

    PCK_NOMINA.GL_FACTORPS := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(946), 0);

    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
    IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 7) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DOCEAVAS := 6;
        --DOCEAVASMINIMASPRIMASERVICIOS := 6;
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
    END IF;

    FOR i IN 7..12 LOOP
        PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
            IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
            END IF;
            PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
        END IF;
    END LOOP;

    IF PCK_NOMINA.GL_SMES <= 7 THEN
        FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
            PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                END IF;
                PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
            END IF;
        END LOOP;
    END IF;

    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));

    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
        PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC / 30);
    END IF;
    IF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('16/06/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAIPS <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 THEN
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
    ELSIF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_DOCEAVAS >= 1 THEN
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
    ELSIF PCK_NOMINA.FC_CN(404) = 1 THEN
        MI_PSPAGADA := 0;
        IF PCK_NOMINA.GL_SMES = 6 OR PCK_NOMINA.GL_SMES = 7 THEN
            PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 160, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, 7, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_PSPAGADA := PCK_NOMINA.FC_CNP(160);
        END IF;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
        IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 6) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DOCEAVAS := 6;
            --DOCEAVASMINIMASPRIMASERVICIOS := 6;
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
        ELSE
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        END IF;
        IF PCK_NOMINA.GL_SPRC <> 99 THEN
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF ((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0) THEN
                    IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFPS) <> i AND PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFPS) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(PCK_NOMINA.GL_FECHAFPS)) THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            FOR i IN 6..12 LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
        END IF;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        IF PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS > 12 THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;
        IF PCK_NOMINA.FC_CN(952) > 0 THEN
            PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
            PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953) / 30);
        END IF;
        MI_VALOR:= PCK_NOMINA.GL_DOCEAVAS;
        IF (PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS) OR (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS AND PCK_NOMINA.FC_CN(404) = 0 THEN
            IF PCK_NOMINA.GL_DOCEAVAS = 0 AND PCK_NOMINA.FC_CN(404) = 0 THEN
                PCK_NOMINA.GL_DCC := 0;
                PCK_NOMINA.GL_DOCEAVAS := 0;
                -- 'REVISAR PCK_NOMINA.GL_DOCEAVAS CAUSADAS, YA QUE NO CUMPLIO MÁS DE 6 MESES RADICADO 201520160102642 01/06/15  A: ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES
                --Revisar Doceavas Causadas, ya que no cumplio Más de 6 meses Radicado --RADICADO-- --> 01/06/15  a: --NOMBRES--.
                MI_MSG(1).CLAVE := 'NOMBRES';
                MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
                MI_MSG(2).CLAVE := 'RADICADO';
                MI_MSG(2).VALOR := '201520160102642';

                PCK_NOMINA_COM7.PR_ALERTA
                    (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                    ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_DOCEAVASCAUSADAS
                    ,UN_REEMPLAZOS   => MI_MSG
                    ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                    ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                    ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                    ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                    ,UN_USER         => PCK_CONEXION.FC_GETUSER
                    );
            END IF;
        END IF;
        --(APINEDA:22/03/2019)-Se agrega validación para que no se calcule prima semestral cuando el funcionario no ha cumplido 6 meses TAR 1000090732
        --(MZANGUNA:19/12/2019)-Se quita validación dado que ahora si se debe calcular con los días que el empleado lleve
        /*IF PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFPS) < 6 THEN
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;*/

        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            --(MZANGUNA:19/12/2019)-Se quita condición dado que tocancipa empieza a pagar la prima por días.
            --IF (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953)) >= 180 THEN
                MI_VALOR := PCK_NOMINA.FC_CN(160);
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
                MI_VALOR := PCK_NOMINA.FC_CN(160);
            --END IF;
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
        IF PCK_NOMINA.FC_CN(160) < 0 THEN
            PCK_NOMINA.CN(160) := 0;
        END IF;
        IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, '01', PCK_NOMINA.GL_SANO, i, '99', PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
            ELSE
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
            END IF;
        END IF;
        --(APINEDA:26/07/2019)-Se modifica condición debido a que esta descontando la prima semestral a persona que se retira en Julio
        IF MI_PSPAGADA > 0 AND PCK_NOMINA.FC_CN(160) > 0 AND PCK_NOMINA.GL_PROCESOACTUAL <> 99 AND PCK_NOMINA.GL_PERIODOACTUAL <> 7 THEN
            PCK_NOMINA.CN(160) := 0;
            --La prima Semestral ya debió ser pagada, según datos históricos, revise pagos. se eliminara éste concepto en el pago de liquidación a: --NOMEMPLEADO--, Cédula No.--CEDULA--, Tipo: --TIPO--.
            MI_MSG(1).CLAVE := 'NOMEMPLEADO';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(2).CLAVE := 'CEDULA';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
            MI_MSG(3).CLAVE := 'TIPO';
            MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_PRIMASEMESTRALPAGA
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
        END IF;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
    END IF;
    MI_PRIMAJUNIO := PCK_NOMINA.FC_CN(160);
    MI_INDRETIR := PCK_NOMINA.FC_CN(404);
    PCK_NOMINA.CN(945) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..699 LOOP
            IF (i <> 125) AND (i <> 303) AND (i <> 301) AND (i <> 300) AND (i < 599) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(404) := MI_INDRETIR;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS;

    PCK_NOMINA.CN(160) := MI_PRIMAJUNIO;

    PCK_NOMINA.CN(947) := PCK_NOMINA.GL_VPT   ;
    PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXT ;
    PCK_NOMINA.CN(949) := PCK_NOMINA.GL_AUXA ;
    PCK_NOMINA.CN(950) := PCK_NOMINA.GL_GRPNGV  ;
    PCK_NOMINA.CN(951) := PCK_NOMINA.FC_CN(67) ;
    PCK_NOMINA.CN(952) := PCK_NOMINA.GL_DCC ;

    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.CN(991) := PCK_NOMINA.FC_CNPR(994) ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.CN(992) := PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503);
        PCK_NOMINA.CN(993) := PCK_NOMINA.FC_CN(160) ;
        PCK_NOMINA.CN(994) := PCK_NOMINA.FC_CN(992) - PCK_NOMINA.FC_CN(991) + PCK_NOMINA.FC_CN(993) ;
    END IF;

    PCK_NOMINA.CN(985) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS), 0) ;

    PCK_NOMINA.GL_PS_BASE := PCK_NOMINA.GL_FACTORPS;
    PCK_NOMINA.GL_PS_DIAS := PCK_NOMINA.GL_DCC;

END PR_CALCPRIMASEMESTRALGOBCAQUET;


PROCEDURE PR_CALCULARPRIMASEMESTRALUPC (
/*
NAME              : LIQUIDAR
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
DATE MIGRADOR     : 30/05/2018
TIME              : 08:19 AM
SOURCE MODULE     : SERVIDORNOMINA2010_UPC, En access calcularprimasemestralUPC.
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       : PROCEDIMIENTO PRINCIPAL EN EL CUAL SE USAN LOS PROCEDIMIENTO Y FUNCIONES PARA EL CALCULO DE LA NOMINA
@Name: liquidarNomina
*/
    UN_COMPANIA   IN  PCK_SUBTIPOS.TI_COMPANIA
)

AS
    MI_DNT1                 NUMBER DEFAULT 0;
    MI_AJUSTES              NUMBER DEFAULT 0;
    MI_BONBIEN              NUMBER DEFAULT 0;
    MI_MSG                  PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CONTAD               PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_VALORCN140           NUMBER DEFAULT 0;
    MI_VALORCN2             NUMBER DEFAULT 0;
    MI_VALORCN160           NUMBER DEFAULT 0;
    MI_INDRETIR             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN
    PCK_NOMINA.CN(527) := 0;
    MI_VALORCN2 := PCK_NOMINA.FC_CN(2);
    MI_VALORCN160 := PCK_NOMINA.FC_CN(2);
    PCK_NOMINA.GL_FACTORPS := 0;
    PCK_NOMINA.GL_DNT := 0;
    MI_DNT1 := 0;

    FOR i IN PCK_NOMINA.CN.FIRST .. PCK_NOMINA.CN.LAST LOOP
        IF PCK_NOMINA.CCONCEPTOS.EXISTS(i) THEN
            IF PCK_NOMINA.CCONCEPTOS(i).CLASE = 5 AND SUBSTR(PCK_NOMINA.CCONCEPTOS(i).ID_DE_CONCEPTO, 1, 1) = 7 THEN
                MI_CONTAD := MI_CONTAD + 1;
            END IF;
        END IF;
    END LOOP;

    PCK_NOMINA.GL_FECHAIPS := CASE WHEN (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' THEN TO_DATE('01/' || LPAD(PCK_NOMINA.GL_SMES, 2, 0) || '/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAINI END)
                < TO_DATE('16/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE TO_DATE('01/06/' || PCK_NOMINA.GL_SANO) END;

    PCK_NOMINA.GL_FECHAIPS := (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE PCK_NOMINA.GL_FECHAIPS END);

    PCK_NOMINA.GL_FECHAIPS1 := (CASE WHEN (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' THEN TO_DATE('01/' || LPAD(PCK_NOMINA.GL_SMES, 2, 0) || '/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAINI END )
                < TO_DATE('16/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE TO_DATE('01/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END);

    PCK_NOMINA.GL_FECHAIPS1 := (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > PCK_NOMINA.GL_FECHAIPS1 THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE PCK_NOMINA.GL_FECHAIPS1 END);
    IF PCK_NOMINA.GL_SMES = 6 AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) THEN
        PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    END IF;
    PCK_NOMINA.GL_FECHAFPS := (CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 THEN (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' THEN TO_DATE('01/' || LPAD(PCK_NOMINA.GL_SMES, 2, 0) || '/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAINI END)
	                           ELSE TO_DATE('30/05/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END);

    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), (CASE WHEN PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, 5, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359);
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), 6, (CASE WHEN PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS1) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, 5, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    MI_DNT1 := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359);
    IF PCK_NOMINA.FC_CN(404) = 0 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (CASE WHEN PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) > 5 THEN (PCK_NOMINA.GL_SANO - 1) ELSE PCK_NOMINA.GL_SANO END), (CASE WHEN PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) > 5 THEN 6 ELSE PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS1) END)
            , (CASE WHEN PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS1) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, 5, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    ELSIF (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' THEN TO_DATE('01/' || LPAD(PCK_NOMINA.GL_SMES, 2, 0) || '/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAINI END) < TO_DATE('16/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), (CASE WHEN PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, 5, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    ELSE
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), (CASE WHEN PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END), PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    END IF;
    PCK_NOMINA.GL_DIASVAC := 0;
    PCK_NOMINA.GL_DIASPENDIENTES := 0;
    PCK_NOMINA.GL_PENDIENTES := 0;
    PCK_NOMINA.GL_LICENCIAS := 0;
    PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
    PCK_NOMINA.GL_DINEROPROPORCIONAL := 0;
    PCK_NOMINA.GL_FECHAUV := (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL END);
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 5, 3, PCK_NOMINA.GL_SANO, 5, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.FC_CN(210) > 0 THEN
        PCK_NOMINA.CN(1) := (CASE WHEN PCK_NOMINA.FC_CN(1) <> 0 THEN PCK_NOMINA.FC_CN(1) ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(211) * PCK_NOMINA.FC_CN(210), 0) END);
    ELSE
        PCK_NOMINA.CN(1) := (CASE WHEN PCK_NOMINA.FC_CN(1) <> 0 THEN PCK_NOMINA.FC_CN(1) ELSE PCK_NOMINA.FC_CNA(1) END);
    END IF;

    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUMNOVEDAD(UN_COMPANIA, 0, 0, 0, 0, 0, 0, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA_COM8.PR_CALCULARBONIFICACIONES;
    PCK_NOMINA.GL_BONESPMADOC := 0;    
    PCK_NOMINA.CN(166) := 0;
    PCK_NOMINA.CN(197) := 0;
    PCK_NOMINA.CN(198) := 0;
    PCK_NOMINA.CN(199) := 0;
    PCK_NOMINA.CN(85)  := 0;
    PCK_NOMINA.CN(186) := 0;
    PCK_NOMINA.CN(196)  := 0;
    PCK_NOMINA.CN(193) := 0;

    MI_AJUSTES := PCK_NOMINA.FC_CNA(515) + PCK_NOMINA.FC_CNA(529) + PCK_NOMINA.FC_CNA(560);

    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '04' THEN
        IF (PCK_NOMINA.FC_CN(1) + PCK_NOMINA.FC_CN(186)) > 2 * PCK_NOMINA.FC_CN(201) OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESCALAFON = '10' THEN
            PCK_NOMINA.GL_AUXT := 0;
        ELSE
            PCK_NOMINA.GL_AUXT := PCK_NOMINA.FC_CN(81);
        END IF;
    END IF;
    IF (PCK_NOMINA.FC_CN(1) + PCK_NOMINA.FC_CN(62) + PCK_NOMINA.FC_CN(186)) >= PCK_NOMINA.FC_CN(454) OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESCALAFON = '10' THEN
        PCK_NOMINA.GL_AUXA := 0;
    ELSE
        PCK_NOMINA.GL_AUXA := (CASE WHEN (PCK_NOMINA.FC_CN(1) + PCK_NOMINA.FC_CN(62) + PCK_NOMINA.FC_CN(186)) >= PCK_NOMINA.FC_CN(454) THEN 0 ELSE PCK_NOMINA.FC_CN(82) END);
    END IF;

    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUMCC(UN_COMPANIA, PCK_NOMINA.GL_SANO, 2, 3, PCK_NOMINA.GL_SANO, 4, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO);
    PCK_NOMINA.GL_PROMEDIOEXTRAS := 0 ;
    --(APINEDA:26/04/2019)-Se eliminan líneas debido a que se esta sumando dos veces el valor de la bonificación por bienestar TAR 1000084160.

    IF PCK_PARST.FC_PAR('LIQUIDAN BONIFICACION ANUAL POR SERV. PRESTADOS',' ') = 'SI' THEN
        PCK_NOMINA.GL_BAN := 0 ;
        PCK_NOMINA.GL_SANO5 := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
        PCK_NOMINA.GL_SMES5 := LPAD(PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO), 2, 0);
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUMCC(UN_COMPANIA, PCK_NOMINA.GL_SANO5, PCK_NOMINA.GL_SMES5, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO);
        PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
        PCK_NOMINA.GL_FECB := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO + PCK_NOMINA.FC_CNA(355) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CN(355) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(356);
        IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECB) = PCK_NOMINA.GL_SMES AND PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) <> PCK_NOMINA.GL_SANO AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESTADO_ACTUAL = 1) AND PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) <= CASE WHEN PCK_NOMINA.FC_CN(30) = 1 THEN 15 ELSE 31 END THEN
            PCK_NOMINA.GL_SUELDOBON := (CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END);
            IF PCK_NOMINA.GL_SUELDOBON + PCK_NOMINA.GL_GRPNV + PCK_NOMINA.GL_GRPGV > PCK_NOMINA.FC_CN(66) THEN
                IF PCK_NOMINA.FC_CN(210) = 0 THEN
                    PCK_NOMINA.GL_BAN := PCK_NOMINA.FC_CN(1) * 35 / 100;
                    PCK_NOMINA.CN(161) := 35;
                ELSE
                    PCK_NOMINA.GL_BAN := PCK_NOMINA.FC_CN(1) * 35 / 100;
                    PCK_NOMINA.CN(161) := 35;
                END IF;
            ELSE
                PCK_NOMINA.GL_BAN := PCK_NOMINA.FC_CN(1) * 50 / 100;
                PCK_NOMINA.CN(161) := 50;
            END IF;
        END IF;
    END IF;
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUMNOVEDAD(UN_COMPANIA, 0, 0, 0, 0, 0, 0, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    MI_AJUSTES := PCK_NOMINA.FC_CNA(515) + PCK_NOMINA.FC_CN(529) + PCK_NOMINA.FC_CNA(560);
    --(APINEDA:25/04/2019)-Se agrega acumulado de bonificaciones del mes TAR 1000084160
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 3, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.GL_BONESPMADOC := PCK_NOMINA.FC_CNA(197) + PCK_NOMINA.FC_CNA(198) + PCK_NOMINA.FC_CNA(199);    
    MI_BONBIEN := PCK_NOMINA.FC_CNA(193);

    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_ANO1 - 1), 6, 0, PCK_NOMINA.GL_ANO1, 5, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
	--(APINEDA:26/04/2019)-Se elimina de los factores la variable PCK_NOMINA.GL_PROMEDIOEXTRAS TAR 1000084160
    PCK_NOMINA.GL_FACTORPS := (PCK_NOMINA.FC_CN(1) + NVL(MI_AJUSTES, 0)) + (PCK_NOMINA.GL_AUXT) + (PCK_NOMINA.GL_AUXA) + MI_BONBIEN + PCK_NOMINA.GL_BONESPMADOC
        + PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CNA(150) <> 0 THEN PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) ELSE PCK_NOMINA.FC_CN(150) END) / 12, 0);

    PCK_NOMINA.CNA(150) := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CNA(150) <> 0 THEN PCK_NOMINA.FC_CNA(150) ELSE PCK_NOMINA.FC_CN(150) END), 0);
    PCK_NOMINA.CN(150) := 0;

    PCK_NOMINA.GL_DCC := (CASE WHEN PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFPS) > 360 THEN 360 ELSE PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFPS) END) - MI_DNT1;
    IF PCK_NOMINA.GL_DCC >= 90 THEN
        IF PCK_NOMINA.FC_CN(160) = 0 THEN
            PCK_NOMINA.CN(160) := PCK_SYSMAN_UTL.FC_ROUND((PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS, 0) * (CASE WHEN PCK_NOMINA.GL_DCC > 360 THEN 360 ELSE PCK_NOMINA.GL_DCC END) / 360 ), 0);
        END IF;
    ELSE
        PCK_NOMINA.CN(160) := 0;
        PCK_NOMINA.GL_DCC := 0;
    END IF;
    PCK_NOMINA.GL_IBR := 0;
    PCK_NOMINA.GL_IBAA := 0;
    PCK_NOMINA.GL_IBR := PCK_NOMINA_CALCULO.FC_SUMACONCEPTOSRETENCION;
    PCK_NOMINA.CN(110) := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.CCATEGORIA(1).GASTOSREPRESENTACION <> 0 THEN (PCK_NOMINA.GL_IBR / 2) ELSE PCK_NOMINA.GL_IBR END) * 0.75, 0);
    PCK_NOMINA.CN(303) := PCK_NOMINA.FC_RETEF(UN_COMPANIA, (CASE WHEN PCK_NOMINA.CCATEGORIA(1).GASTOSREPRESENTACION <> 0 THEN (PCK_NOMINA.GL_IBR / 2) ELSE PCK_NOMINA.GL_IBR END * 0.75)
            - (CASE WHEN PCK_NOMINA.FC_CN(301) >= PCK_NOMINA.FC_CN(110) * 15 / 100 THEN PCK_NOMINA.FC_CN(110) * 15 / 100 ELSE PCK_NOMINA.FC_CN(301) END) - (CASE WHEN PCK_NOMINA.FC_CN(300)
            >= PCK_NOMINA.FC_CN(110) * 15 / 100 THEN PCK_NOMINA.FC_CN(110) * 15 / 100 ELSE PCK_NOMINA.FC_CN(300) END) - (CASE WHEN PCK_NOMINA.FC_CN(302) >= PCK_NOMINA.FC_CN(110) * 30 / 100 THEN PCK_NOMINA.FC_CN(110) * 30 / 100 ELSE PCK_NOMINA.FC_CN(302) END), (PCK_NOMINA.GL_SANO), 1);
    PCK_NOMINA.CN(303) := (CASE WHEN PCK_NOMINA.FC_CN(303) = 0 THEN 0 ELSE PCK_NOMINA.FC_CN(303) END);


    PCK_NOMINA.CN(97) := PCK_NOMINA.FC_CN(160);
    PCK_NOMINA.PR_DESCUENTAEMBARGOS(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_PARST.FC_PAR('LIQUIDAN DESCUENTO DE ESTAMPILLAS', ' ') = 'SI' THEN
        IF PCK_NOMINA.FC_CN(610) = 0 AND PCK_NOMINA.GL_SPER <> 4 AND PCK_PARST.FC_PAR('DESCONTAR ESTAMPILLA PROCIUDADELA','NO') = 'SI' THEN
            PCK_NOMINA.CN(610) := (CASE WHEN PCK_NOMINA.FC_CN(610) = 0 THEN (PCK_NOMINA.FC_CN(2)) * TO_NUMBER(PCK_PARST.FC_PAR('ESTAMPILLA','0')) ELSE PCK_NOMINA.FC_CN(610) END);
            PCK_NOMINA.CN(610) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(610), 0);
        END IF;
    END IF;

    FOR i IN PCK_NOMINA.CCONCEPTOS.FIRST .. PCK_NOMINA.CCONCEPTOS.LAST LOOP
        IF PCK_NOMINA.CCONCEPTOS(i).CLASE = 5 THEN
            MI_VALORCN140 := MI_VALORCN140 + PCK_NOMINA.FC_CN(i);
        END IF;
    END LOOP;
    --(APINEDA:24/10/2019)-Se agregan nuevos conceptos de descuento del 1600 al 1698 para libranzas, se agrega llamado a función nueva FC_SUMACONRANGOS. 
    PCK_NOMINA.CN(699) := PCK_NOMINA_COM4.FC_SUMACONRANGOS(600, 698);
    PCK_NOMINA.CN(799) := PCK_NOMINA.FC_SUMACON(700, 749);
    PCK_NOMINA.CN(140) := MI_VALORCN140;

    PCK_NOMINA.CN(144) := PCK_NOMINA.FC_CN(97) - PCK_NOMINA.FC_CN(140);
    IF PCK_NOMINA.FC_CN(144) < 0 THEN
        --El empleado --EMPLEADO-- se encuentra con saldo en ceros, revise la novedad
        MI_MSG(1).CLAVE := 'EMPLEADO';
        MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;

        PCK_NOMINA_COM7.PR_ALERTA
           (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
           ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_EMPLEADOCONSALDOENCERO
           ,UN_REEMPLAZOS   => MI_MSG
           ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
           ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
           ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
           ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
           ,UN_USER         => PCK_CONEXION.FC_GETUSER);

    END IF;
    PCK_NOMINA.GL_PRIMAJUN := PCK_NOMINA.FC_CN(160);
    PCK_NOMINA.GL_DIASPRIMAJUNIO := PCK_NOMINA.FC_CN(67);

    --(MZANGUNA-09/06/2018)-Se adiciona para UPC dado que estaba calculando conceptos que no entran en periodo 4
    MI_INDRETIR := PCK_NOMINA.FC_CN(404);
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..699 LOOP
            IF (i <> 125) AND (i <> 303) AND (i <> 301) AND (i <> 300) AND (i < 599) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                IF PCK_NOMINA.FC_CN(i) <> 0.01 THEN
                    PCK_NOMINA.CN(i) := 0;
                END IF;
            END IF;
        END LOOP;
    END IF;

    PCK_NOMINA.CN(404) := MI_INDRETIR;
    PCK_NOMINA.CN(160) := PCK_NOMINA.GL_PRIMAJUN;

    PCK_NOMINA.CN(949) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(958) := PCK_NOMINA.GL_FACTORPS;
    PCK_NOMINA.CN(955) := MI_BONBIEN;
    PCK_NOMINA.CN(952) := (CASE WHEN PCK_NOMINA.FC_CN(952) = 0 THEN (CASE WHEN PCK_NOMINA.GL_DCC > 360 THEN 360 ELSE PCK_NOMINA.GL_DCC END) ELSE PCK_NOMINA.FC_CN(952) END);
    PCK_NOMINA.CN(953) := (CASE WHEN PCK_NOMINA.FC_CN(953) = 0 THEN MI_DNT1 ELSE PCK_NOMINA.FC_CN(953) END);
    PCK_NOMINA.CN(956) := (CASE WHEN PCK_NOMINA.FC_CN(430) = 1 THEN PCK_NOMINA.GL_BONESPMADOC ELSE 0 END);
    PCK_NOMINA.CN(959) := (CASE WHEN PCK_NOMINA.FC_CN(432) = 1 THEN PCK_NOMINA.GL_BONESPMADOC ELSE 0 END);
    PCK_NOMINA.CN(947) := PCK_NOMINA.GL_PROMEDIOEXTRAS;
    PCK_NOMINA.CN(957) := (CASE WHEN PCK_NOMINA.FC_CN(431) = 1 THEN PCK_NOMINA.GL_BONESPMADOC ELSE 0 END);
    PCK_NOMINA.CN(954) := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CNA(150) <> 0 THEN PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) ELSE PCK_NOMINA.FC_CN(150) END) / 12, 0);
    PCK_NOMINA.CN(945) := PCK_NOMINA.FC_CN(1) + MI_AJUSTES;
    PCK_NOMINA.CN(430) := 0;
    PCK_NOMINA.CN(431) := 0;
    PCK_NOMINA.CN(527) := 0;
    PCK_NOMINA.CN(527) := 0;
    PCK_NOMINA.CN(529) := 0;
    PCK_NOMINA.CN(170) := 0;
    PCK_NOMINA.CN(186) := 0;
    PCK_NOMINA.CN(197) := 0;
    PCK_NOMINA.CN(198) := 0;
    PCK_NOMINA.CN(527) := 0;
    PCK_NOMINA.CN(529) := 0;
    PCK_NOMINA.CN(560) := 0;

    MI_VALORCN2 := PCK_NOMINA.FC_CN(2);
    MI_VALORCN160 := PCK_NOMINA.FC_CN(160);
END PR_CALCULARPRIMASEMESTRALUPC;

FUNCTION FC_PLANOCONTABILIZARNOMINA(


/*
    NAME              : FC_PLANOCONTABILIZARNOMINA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 23/06/2018
    TIME              : 08:58 AM
    SOURCE MODULE     : NOMINAP2018.06.05, Formulario Interface_Nomina, Boton aceptar.
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    PARAMETERS        :

    MODIFICATIONS     :

    @NAME:planoContabilizarNomina
    @METHOD:  GET
*/
     UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_OPCIONPLANO         IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 1
    ,UN_PROCESO             IN PCK_SUBTIPOS.TI_ID_DE_PROCESO
    ,UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO
    ,UN_MES               IN PCK_SUBTIPOS.TI_MES
    ,UN_PERIODO             IN  PCK_SUBTIPOS.TI_PERIODO_NOMI
    ,UN_FECHAINTER          IN DATE
    ,UN_TIPOCOMPROBANTE     IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE DEFAULT 'NOM'
    ,UN_NUMEROCOMPROBANTE   IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT
    ,UN_MANEJACENTROCOSTO   IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0                     --FORMS!INTERFACE_NOMINA!MANEJACENTROS
    ,UN_AUXTERCERO          IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0                     --FORMS!INTERFACE_NOMINA!PRVAUXTER
    ,UN_PORCONTRIBUCION     IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0          --FORMS!INTERFACE_NOMINA!PORC
    ,UN_CUENTADBT           IN PCK_SUBTIPOS.TI_CODIGOCONTA DEFAULT ''               --FORMS!INTERFACE_NOMINA!DBT
    ,UN_CUENTACRD           IN PCK_SUBTIPOS.TI_CODIGOCONTA DEFAULT ''               --FORMS!INTERFACE_NOMINA!CRD
    ,UN_UNICOCOMPROBANTE    IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0           --FORMS!INTERFACE_NOMINA!UNCOMPROBANTE
    ,UN_PORPERIODO          IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0     --(MZANGUNA:26/12/2018)-Se adiciona parámetro por periodo.
    ,UN_USUARIO             IN COMPROBANTE_CNT.CREATED_BY%TYPE
) RETURN CLOB

AS
    MI_RTA    CLOB;
    MI_PAR    VARCHAR2(2 CHAR);
BEGIN

MI_PAR :=    NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'SEPARAR PARTE PATRONAL EN COMPROBANTE INDEPENDIENTE POR PLANO',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 2), 'NO');

    IF UN_OPCIONPLANO = 1 AND UN_UNICOCOMPROBANTE<>0 THEN
        MI_RTA := TO_CLOB(PCK_NOMINA_COM8.FC_PLANOCONTABILIZARSYSMAN
            (UN_COMPANIA            => UN_COMPANIA
            ,UN_PROCESO             => UN_PROCESO
            ,UN_ANO                 => UN_ANO
            ,UN_MES                 => UN_MES
            ,UN_PERIODO             => UN_PERIODO
            ,UN_FECHAINTER          => UN_FECHAINTER
            ,UN_TIPOCOMPROBANTE     => UN_TIPOCOMPROBANTE
            ,UN_NUMEROCOMPROBANTE   => UN_NUMEROCOMPROBANTE
            ,UN_MANEJACENTROCOSTO   => UN_MANEJACENTROCOSTO
            ,UN_USUARIO             => UN_USUARIO ));
    IF MI_PAR =  'NO' THEN        
         MI_RTA := MI_RTA  || TO_CLOB(PCK_NOMINA_COM8.FC_PLANOCONTASYSMANCONTABLECC
            (UN_COMPANIA        => UN_COMPANIA
            ,UN_PROCESO             => UN_PROCESO
            ,UN_ANO                 => UN_ANO
            ,UN_MES                 => UN_MES
            ,UN_PERIODO             => UN_PERIODO
            ,UN_FECHAINTER          => UN_FECHAINTER
            ,UN_TIPOCOMPROBANTE     => UN_TIPOCOMPROBANTE
            ,UN_NUMEROCOMPROBANTE   => UN_NUMEROCOMPROBANTE
            ,UN_AUXTERCERO          => UN_AUXTERCERO
            ,UN_MANEJACENTROCOSTO   => UN_MANEJACENTROCOSTO
            ,UN_PORCONTRIBUCION     => UN_PORCONTRIBUCION
            ,UN_CUENTADBT           => UN_CUENTADBT
            ,UN_CUENTACRD           => UN_CUENTACRD
            ,UN_PORPERIODO          => UN_PORPERIODO --(MZANGUNA:26/12/2018)-Se adiciona parámetro por periodo.
            ,UN_UNICOCOMPROBANTE     => UN_UNICOCOMPROBANTE));-- (GPORTILLA:23/02/2023): Se adiciona campo con el fin de controlar si se deben mostrar uno o mas consecutivos en el plano             
            
        MI_RTA := MI_RTA  ||
                TO_CLOB(PCK_NOMINA_COM8.FC_PLANOCONTABILIZAPROVICIONES
            (UN_COMPANIA            => UN_COMPANIA
            ,UN_PROCESO             => UN_PROCESO
            ,UN_ANO                 => UN_ANO
            ,UN_MES                 => UN_MES
            ,UN_PERIODO             => UN_PERIODO
            ,UN_FECHAINTER          => UN_FECHAINTER
            ,UN_TIPOCOMPROBANTE     => UN_TIPOCOMPROBANTE
            ,UN_NUMEROCOMPROBANTE   => UN_NUMEROCOMPROBANTE
            ,UN_AUXTERCERO          => UN_AUXTERCERO
            ,UN_MANEJACENTROCOSTO   => UN_MANEJACENTROCOSTO));-- (GPORTILLA:23/02/2023): Se adiciona funcion que retonar informacion para el archivo de Patronal        
 END IF ;
        /*MI_RTA := MI_RTA  ||
                TO_CLOB(PCK_NOMINA_COM8.FC_PLANOCONTASYSMANCONTABLE(
            UN_COMPANIA         => UN_COMPANIA
           ,UN_PROCESO              => UN_PROCESO
           ,UN_ANO                  => UN_ANO
           ,UN_MES                  => UN_MES
           ,UN_PERIODO              => UN_PERIODO
           ,UN_FECHAINTER           => UN_FECHAINTER
           ,UN_TIPOCOMPROBANTE      => UN_TIPOCOMPROBANTE
           ,UN_NUMEROCOMPROBANTE    => UN_NUMEROCOMPROBANTE
           ,UN_MANEJACENTROCOSTO    => UN_MANEJACENTROCOSTO
           ,UN_PORCONTRIBUCION      => UN_PORCONTRIBUCION
           ,UN_CUENTADBT            => UN_CUENTADBT
           ,UN_CUENTACRD            => UN_CUENTACRD
           ,UN_UNICOCOMPROBANTE     => UN_UNICOCOMPROBANTE ));*/
    ELSIF UN_OPCIONPLANO = 1 THEN

        MI_RTA := TO_CLOB(PCK_NOMINA_COM8.FC_PLANOCONTABILIZARSYSMAN
            (UN_COMPANIA            => UN_COMPANIA
            ,UN_PROCESO             => UN_PROCESO
            ,UN_ANO                 => UN_ANO
            ,UN_MES                 => UN_MES
            ,UN_PERIODO             => UN_PERIODO
            ,UN_FECHAINTER          => UN_FECHAINTER
            ,UN_TIPOCOMPROBANTE     => UN_TIPOCOMPROBANTE
            ,UN_NUMEROCOMPROBANTE   => UN_NUMEROCOMPROBANTE
            ,UN_MANEJACENTROCOSTO   => UN_MANEJACENTROCOSTO
            ,UN_USUARIO             => UN_USUARIO ));
    ELSIF UN_OPCIONPLANO = 3 THEN
        MI_RTA := TO_CLOB(PCK_NOMINA_COM8.FC_PLANOCONTABILIZAPROVICIONES
            (UN_COMPANIA            => UN_COMPANIA
            ,UN_PROCESO             => UN_PROCESO
            ,UN_ANO                 => UN_ANO
            ,UN_MES                 => UN_MES
            ,UN_PERIODO             => UN_PERIODO
            ,UN_FECHAINTER          => UN_FECHAINTER
            ,UN_TIPOCOMPROBANTE     => UN_TIPOCOMPROBANTE
            ,UN_NUMEROCOMPROBANTE   => UN_NUMEROCOMPROBANTE
            ,UN_AUXTERCERO          => UN_AUXTERCERO
            ,UN_MANEJACENTROCOSTO   => UN_MANEJACENTROCOSTO));

    ELSIF UN_OPCIONPLANO = 2 THEN -- (GPORTILLA:23/02/2023): Semodifica condicion para que al generar el plano por la Contabilizaci¿n Contable (Patronal), solo ingrese a la funcion Contabilizaci¿n Contable (Patronal)
        MI_RTA := TO_CLOB(PCK_NOMINA_COM8.FC_PLANOCONTASYSMANCONTABLECC
            (UN_COMPANIA        => UN_COMPANIA
            ,UN_PROCESO             => UN_PROCESO
            ,UN_ANO                 => UN_ANO
            ,UN_MES                 => UN_MES
            ,UN_PERIODO             => UN_PERIODO
            ,UN_FECHAINTER          => UN_FECHAINTER
            ,UN_TIPOCOMPROBANTE     => UN_TIPOCOMPROBANTE
            ,UN_NUMEROCOMPROBANTE   => UN_NUMEROCOMPROBANTE
            ,UN_AUXTERCERO          => UN_AUXTERCERO
            ,UN_MANEJACENTROCOSTO   => UN_MANEJACENTROCOSTO
            ,UN_PORCONTRIBUCION     => UN_PORCONTRIBUCION
            ,UN_CUENTADBT           => UN_CUENTADBT
            ,UN_CUENTACRD           => UN_CUENTACRD
            ,UN_PORPERIODO          => UN_PORPERIODO --(MZANGUNA:26/12/2018)-Se adiciona parámetro por periodo.
            ,UN_UNICOCOMPROBANTE     => UN_UNICOCOMPROBANTE));-- (GPORTILLA:23/02/2023): Se adiciona campo con el fin de controlar si se deben mostrar uno o mas consecutivos en el plano 
    /*ELSE
        MI_RTA := TO_CLOB(PCK_NOMINA_COM8.FC_PLANOCONTASYSMANCONTABLE(
            UN_COMPANIA         => UN_COMPANIA
           ,UN_PROCESO              => UN_PROCESO
           ,UN_ANO                  => UN_ANO
           ,UN_MES                  => UN_MES
           ,UN_PERIODO              => UN_PERIODO
           ,UN_FECHAINTER           => UN_FECHAINTER
           ,UN_TIPOCOMPROBANTE      => UN_TIPOCOMPROBANTE
           ,UN_NUMEROCOMPROBANTE    => UN_NUMEROCOMPROBANTE
           ,UN_MANEJACENTROCOSTO    => UN_MANEJACENTROCOSTO
           ,UN_PORCONTRIBUCION      => UN_PORCONTRIBUCION
           ,UN_CUENTADBT            => UN_CUENTADBT
           ,UN_CUENTACRD            => UN_CUENTACRD
           ,UN_UNICOCOMPROBANTE     => UN_UNICOCOMPROBANTE ));*/
    END IF;

    RETURN MI_RTA;

END FC_PLANOCONTABILIZARNOMINA;

FUNCTION FC_PLANOCONTABILIZARSYSMAN(


/*
    NAME              : FC_PLANOCONTABILIZARSYSMAN
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 23/06/2018
    TIME              : 08:58 AM
    SOURCE MODULE     : NOMINAP2018.06.05, Función Interfacesysman
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    PARAMETERS        :

    MODIFICATIONS     :

    @NAME:planoContabilizarSysman
    @METHOD:  GET
*/
     UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_PROCESO             IN PCK_SUBTIPOS.TI_ID_DE_PROCESO
    ,UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO
    ,UN_MES               IN PCK_SUBTIPOS.TI_MES
    ,UN_PERIODO             IN  PCK_SUBTIPOS.TI_PERIODO_NOMI
    ,UN_FECHAINTER          IN DATE
    ,UN_TIPOCOMPROBANTE     IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE DEFAULT 'NOM'
    ,UN_NUMEROCOMPROBANTE   IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT
    ,UN_MANEJACENTROCOSTO   IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0 --FORMS!INTERFACE_NOMINA!MANEJACENTROS
    ,UN_USUARIO             IN COMPROBANTE_CNT.CREATED_BY%TYPE
) RETURN CLOB
AS
    MI_RTAPLANO                CLOB;
    MI_TABLA                   PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICIONACME           PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS                  PCK_SUBTIPOS.TI_CAMPOS;
    MI_FILAS                   PCK_SUBTIPOS.TI_ENTERO;
    MI_CUENTAREGISTRO          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_RS                      SYS_REFCURSOR;
    MI_RS1                     SYS_REFCURSOR;
    MI_CUENTADEBITO            PCK_SUBTIPOS.TI_CODIGOCONTA;
    MI_CUENTACREDITO           PCK_SUBTIPOS.TI_CODIGOCONTA;
    MI_VALORD                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALORC                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_SUCURSAL                PCK_SUBTIPOS.TI_SUCURSAL;
    MI_TERCERO                 PCK_SUBTIPOS.TI_TERCERO;
    MI_CENTROCOSTO             PCK_SUBTIPOS.TI_CENTRO_COSTO;
    
    -- (GPORTILLA:23/02/2023)
    MI_CUENTA                  PCK_SUBTIPOS.TI_CUENTA;
    MI_REFERENCIA              PCK_SUBTIPOS.TI_REFERENCIA;
    MI_FUENTE_RECURSO          PCK_SUBTIPOS.TI_FUENTE_RECURSO;

    -- FIN (GPORTILLA:23/02/2023)

    MI_VRD                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VRC                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_REEMPLAZOS              PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_PAR_NOM_FUN_PLA         VARCHAR2(4);
    MI_NOMBRE                  FONDO.NOMBRE_FONDO%TYPE;
    
    MI_COD_AUXILIAR          PERSONAL.COD_AUXILIAR%TYPE;
    
    MI_CTA_CRD_ADMIN_CESANT_PCA     PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_DBT_ADMIN_CESANT_PCA     PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_CRD_OTRO_CESANT_PCA      PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_DBT_OTRO_CESANT_PCA      PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_CRD_PROD_CESANT_PCA      PCK_SUBTIPOS.TI_PARAMETRO; 
    MI_CTA_DBT_PROD_CESANT_PCA      PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_CRD_PUENTE_CESANT_PCA    PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_DBT_PUENTE_CESANT_PCA    PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_CRD_SUPERN_CESANT_PCA  PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_DBT_SUPERN_CESANT_PCA  PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_CRD_VENTAS_CESANT_PCA    PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_DBT_VENTAS_CESANT_PCA    PCK_SUBTIPOS.TI_PARAMETRO;
    MI_MANEJA_FONDO_PUBLICO_CES      PCK_SUBTIPOS.TI_PARAMETRO;
BEGIN

	MI_CTA_CRD_ADMIN_CESANT_PCA     := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_CRD_ADMINISTRACION_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_DBT_ADMIN_CESANT_PCA     := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_DBT_ADMINISTRACION_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_CRD_OTRO_CESANT_PCA      := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_CRD_OTRO_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_DBT_OTRO_CESANT_PCA      := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_DBT_OTRO_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_CRD_PROD_CESANT_PCA      := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_CRD_PRODUCCION_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_DBT_PROD_CESANT_PCA      := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_DBT_PRODUCCION_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_CRD_PUENTE_CESANT_PCA    := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_CRD_PUENTE_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_DBT_PUENTE_CESANT_PCA    := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_DBT_PUENTE_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_CRD_SUPERN_CESANT_PCA    := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_CRD_SUPERNUM_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_DBT_SUPERN_CESANT_PCA    := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_DBT_SUPERNUM_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_CRD_VENTAS_CESANT_PCA    := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_CRD_VENTAS_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_DBT_VENTAS_CESANT_PCA    := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_DBT_VENTAS_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_MANEJA_FONDO_PUBLICO_CES      := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'MANEJA FONDO PUBLICO PARA CESANTIAS', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), 'NO');
		
   BEGIN
      MI_PAR_NOM_FUN_PLA :=  NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(
                                  UN_COMPANIA  =>  UN_COMPANIA,
                                  UN_NOMBRE    =>  'MANEJA NOMBRE FUNCIONARIO EN PLANO CONTABILIZAR',
                                  UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                                  UN_FECHA_PAR =>  SYSDATE), 2), 'NO');
   END;

    BEGIN
        MI_CONDICIONACME := 'COMPANIA = ''' || UN_COMPANIA || ''' ';
        MI_FILAS := PCK_DATOS.FC_ACME
            (UN_TABLA     => 'ERRORES'
            ,UN_ACCION    => 'E'
            ,UN_CONDICION => MI_CONDICIONACME);
    END;

    BEGIN
        SELECT COUNT(0)
        INTO   MI_CUENTAREGISTRO
        FROM   PERIODOS
        WHERE  COMPANIA = UN_COMPANIA
          AND  ID_DE_PROCESO =UN_PROCESO
          AND  ANO = UN_ANO
          AND  MES = UN_MES
          AND  PERIODO = UN_PERIODO;

        IF MI_CUENTAREGISTRO = 0 THEN
            --MSGBOX 'PERIODO NO DEFINIDO EN NÓMINA.', VBOKONLY + VBINFORMATION
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
        --No existe el periodo --PERIODO--, Año: ---ANO-- En nómina.
        MI_REEMPLAZOS(1).CLAVE := 'PERIODO';
        MI_REEMPLAZOS(1).VALOR := UN_PERIODO;
        MI_REEMPLAZOS(2).CLAVE := 'ANO';
        MI_REEMPLAZOS(2).VALOR := UN_ANO;

        PCK_ERR_MSG.RAISE_WITH_MSG
                  ( UN_EXC_COD => SQLCODE
                  , UN_TABLAERROR => 'PERIODOS'
                  , UN_ERROR_COD  => PCK_ERRORES.ERRR_NOPERIODONOMINA
                  , UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;
    <<CNDEVENGOSAGRUPADO>> 
    FOR MI_RS IN
        (SELECT SUM(HIS.VALOR)  SUMADEVALOR, MAX(HIS.ID_DE_EMPLEADO)  ID_DE_EMPLEADO,
                 CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377  AND MI_PAR_NOM_FUN_PLA =  'SI'
                  THEN
                     CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                     THEN FR.NIT
                     ELSE FS.NIT
                     END 
                  ELSE  P.NUMERO_DCTO 
                  END NUMERO_DCTO,

                P.GRUPOCONTABLE  GRUPOCONTABLE, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION,
                CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS,
                CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO,
                MAX(CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377  AND MI_PAR_NOM_FUN_PLA =  'SI'
                      THEN CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374)
                             THEN RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FR.NOMBRE_FONDO)) , 20) 
                             ELSE RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FS.NOMBRE_FONDO)) , 20) 
                           END || RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES('_' || P.NUMERO_DCTO || ' - ' || P.APELLIDO1 || ' ' || P.APELLIDO2 || ' ' || P.NOMBRES)),44)
                      ELSE  RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(CN.NOMBRE_CONCEPTO)),64)
                    END
                    ) NOMBRE_CONCEPTO,
                CN.SUCURSAL, P.ID_CENTRO_DE_COSTO, 
                CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377  AND MI_PAR_NOM_FUN_PLA =  'SI'
                  THEN
                     CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                     THEN FR.NIT
                     ELSE FS.NIT
                     END 
                  ELSE  CN.TERCERO
                END   TERCERO,
                P.REFERENCIA  , P.FUENTE_DE_RECURSO, P.COD_AUXILIAR
        FROM HISTORICOS HIS INNER JOIN CONCEPTOS CN
       ON HIS.COMPANIA = CN.COMPANIA
      AND HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
    INNER JOIN PERSONAL P
       ON HIS.COMPANIA = P.COMPANIA
      AND HIS.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
    LEFT JOIN V_FONDOS FS 
      ON   P.COMPANIA     =  FS.COMPANIA
      AND  P.FONDO_SALUD  = FS.COD_FONDO
    LEFT JOIN V_FONDOS FR 
      ON  P.COMPANIA  = FR.COMPANIA 
      AND P.FONDO_RIESGOS =  FR.COD_FONDO 
    INNER JOIN PERIODOS 
            ON HIS.COMPANIA = PERIODOS.COMPANIA
            AND HIS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
            AND HIS.ANO = PERIODOS.ANO
            AND HIS.MES = PERIODOS.MES
            AND HIS.PERIODO = PERIODOS.PERIODO                      
        WHERE HIS.COMPANIA = UN_COMPANIA
          AND CN.NITDE ='E'
          AND CN.CLASE = 3
          AND HIS.ID_DE_PROCESO = UN_PROCESO
          AND HIS.ANO = UN_ANO
          AND HIS.MES = UN_MES
          AND HIS.PERIODO = UN_PERIODO
      AND PERIODOS.ACUMULADO NOT IN(0)                   
        GROUP BY CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377  AND MI_PAR_NOM_FUN_PLA =  'SI'
                  THEN
                     CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                     THEN FR.NIT
                     ELSE FS.NIT
                     END 
                  ELSE  P.NUMERO_DCTO 
                  END ,
                 CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                   THEN
                     CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                       THEN FR.NIT
                       ELSE FS.NIT
                     END 
                   ELSE  CN.TERCERO
                 END, P.GRUPOCONTABLE, P.ID_CENTRO_DE_COSTO, CN.SUCURSAL, CN.CTA_DBT_ADMINISTRACION,
                 CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS,
                 CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, HIS.COMPANIA, HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES, HIS.PERIODO, P.REFERENCIA, P.FUENTE_DE_RECURSO
                 ,P.COD_AUXILIAR
        HAVING SUM(HIS.VALOR) <> 0
        ORDER BY HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES, HIS.PERIODO
        )
    LOOP

        --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS DE DEVENGOS AGRUPADOS POR EMPLEADO.  || FORMAT(RS.PERCENTPOSITION, '000.00') || %'
        MI_CUENTADEBITO := RPAD(' ', 16);
        MI_CUENTACREDITO := RPAD(' ', 16);
        MI_VALORD := 0;
        MI_VALORC := 0;
        MI_SUCURSAL := '';
        MI_TERCERO := RPAD(' ', 11);
        MI_CENTROCOSTO := '9999999999';
        
        -- (GPORTILLA:23/02/2023)
        MI_CUENTA := '';
        MI_REFERENCIA := '';
        MI_FUENTE_RECURSO := '';
        -- FIN (GPORTILLA:23/02/2023)
        
        IF MI_RS.GRUPOCONTABLE = 'V' THEN
            MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_VENTAS, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_VENTAS, 16), RPAD(' ', 16));
        ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN
            MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_PRODUCCION, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_PRODUCCION, 16), RPAD(' ', 16));
        ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN
            MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_OTRO, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_OTRO, 16), RPAD(' ', 16));
        ELSE
            MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
        END IF;

        IF NVL(TRIM(MI_CUENTADEBITO), ' ') <> ' ' THEN
            MI_VALORD := MI_RS.SUMADEVALOR;
        END IF;
        --<TICKET: CC_436 AUTOR:CP FECHA:04/12/2024
        MI_COD_AUXILIAR := NVL(MI_RS.COD_AUXILIAR,'');
        IF MI_COD_AUXILIAR =  '' THEN
            MI_COD_AUXILIAR := '99999999999999999999';
        END IF;
        MI_COD_AUXILIAR := NVL(RPAD(MI_RS.COD_AUXILIAR, 16), '99999999999999999999');
        --<TICKET/>
        IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' ' THEN
            MI_VALORC := MI_RS.SUMADEVALOR;
        END IF;
        MI_TERCERO := NVL(RPAD(MI_RS.NUMERO_DCTO, 11), '99999999999');
        MI_TERCERO := REPLACE(MI_TERCERO, '-', '');
        MI_TERCERO := REPLACE(MI_TERCERO, '.', '');

        IF MI_TERCERO = '99999999999' THEN
            MI_SUCURSAL := PCK_DATOS.CONS_SUCURSAL;
        ELSIF MI_RS.SUCURSAL <> '' THEN
            MI_SUCURSAL := MI_RS.SUCURSAL;
        ELSE
            MI_SUCURSAL := '001';
        END IF;
        MI_CENTROCOSTO := NVL(RPAD(MI_RS.ID_CENTRO_DE_COSTO, 10), '9999999999');

        IF UN_MANEJACENTROCOSTO = 0 THEN
            MI_CENTROCOSTO := '9999999999';
        END IF;
         
        -- (GPORTILLA:23/02/2023)el campo cuenta se envia en espacios vacios ya que au no esta definido de donde se debe tomar el dato       
        MI_CUENTA := RPAD(' ', 16);
        MI_REFERENCIA := NVL(MI_RS.REFERENCIA,'');
        IF MI_REFERENCIA =  '' THEN
            MI_REFERENCIA := '99999999999999999999';
        END IF;
        MI_REFERENCIA := NVL(RPAD(MI_REFERENCIA, 20),'99999999999999999999');
        
        MI_FUENTE_RECURSO := NVL(MI_RS.FUENTE_DE_RECURSO,'');
        IF MI_FUENTE_RECURSO =  '' THEN
            MI_FUENTE_RECURSO := '99999999999999999999';
        END IF;
        MI_FUENTE_RECURSO := NVL(RPAD(MI_FUENTE_RECURSO, 20),'99999999999999999999');
        -- FIN (GPORTILLA:23/02/2023)

        IF MI_VALORD <> 0 AND MI_VALORC <> 0 THEN
            MI_VRD := MI_VALORD;
            MI_VRC := MI_VALORC;

            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTADEBITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VRD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_RS.NOMBRE_CONCEPTO, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTADEBITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VRC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_RS.NOMBRE_CONCEPTO, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
        ELSE
            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTADEBITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_RS.NOMBRE_CONCEPTO, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
        END IF;

    END LOOP CNDEVENGOSAGRUPADO;
    --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS DE DEVENGOS AGRUPADOS POR EMPLEADO.  || FORMAT(100, '000.00') || %'

    <<CNDEVENGOSNOAGRUPADOS>>
    FOR MI_RS IN
        (SELECT SUM(HIS.VALOR) AS SUMADEVALOR, MAX(HIS.ID_DE_EMPLEADO) AS ID_DE_EMPLEADO, P.ID_CENTRO_DE_COSTO, P.GRUPOCONTABLE AS GRUPOCONTABLE, CN.SUCURSAL, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, 
          MAX(CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377  AND MI_PAR_NOM_FUN_PLA =  'SI'
                      THEN  CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374)
                             THEN RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FR.NOMBRE_FONDO)) , 20) 
                             ELSE RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FS.NOMBRE_FONDO)) , 20) 
                           END || RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES('_' || P.NUMERO_DCTO || ' - ' || P.APELLIDO1 || ' ' || P.APELLIDO2 || ' ' || P.NOMBRES)),44)
                      ELSE  RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(CN.NOMBRE_CONCEPTO)),64)
                    END
                    ) NOMBRE_CONCEPTO,
          CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                  THEN
                     CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                     THEN FR.NIT
                     ELSE FS.NIT
                     END 
                  ELSE  CN.TERCERO
          END   TERCERO,
          P.REFERENCIA, P.FUENTE_DE_RECURSO, P.COD_AUXILIAR,CN.TIPODEFONDO, V_FONDO_DE_CESANTIAS.PUBLICO
        FROM HISTORICOS HIS LEFT JOIN CONCEPTOS CN
             ON HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
            AND HIS.COMPANIA = CN.COMPANIA
          LEFT JOIN PERSONAL P
             ON HIS.COMPANIA = P.COMPANIA
            AND HIS.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
          LEFT JOIN V_FONDO_DE_CESANTIAS  --CES
             ON P.COMPANIA = V_FONDO_DE_CESANTIAS.COMPANIA
            AND P.FONDO_CESANTIAS = V_FONDO_DE_CESANTIAS.FONDO_CESANTIAS
          LEFT JOIN V_FONDOS FS 
            ON   P.COMPANIA     =  FS.COMPANIA
            AND  P.FONDO_SALUD  = FS.COD_FONDO
          LEFT JOIN V_FONDOS FR 
            ON  P.COMPANIA  = FR.COMPANIA 
            AND P.FONDO_RIESGOS =  FR.COD_FONDO 
      INNER JOIN PERIODOS 
                ON HIS.COMPANIA = PERIODOS.COMPANIA
                AND HIS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                AND HIS.ANO = PERIODOS.ANO
                AND HIS.MES = PERIODOS.MES
                AND HIS.PERIODO = PERIODOS.PERIODO                      
        WHERE HIS.COMPANIA = UN_COMPANIA
          AND HIS.ID_DE_PROCESO = UN_PROCESO
          AND HIS.ANO = UN_ANO
          AND HIS.MES = UN_MES
          AND HIS.PERIODO = UN_PERIODO
          AND CN.NITDE <> 'E'
        AND CN.CLASE = 3
    AND PERIODOS.ACUMULADO NOT IN(0)                 
        GROUP BY P.ID_CENTRO_DE_COSTO, 
        CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
             THEN
                     CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                       THEN FR.NIT
                       ELSE FS.NIT
                     END 
             ELSE  CN.TERCERO
        END,
        P.GRUPOCONTABLE, CN.SUCURSAL, CN.CTA_DBT_ADMINISTRACION,
             CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS,
             CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, HIS.COMPANIA, HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES, HIS.PERIODO,
                 P.REFERENCIA, P.FUENTE_DE_RECURSO, P.COD_AUXILIAR,CN.TIPODEFONDO, V_FONDO_DE_CESANTIAS.PUBLICO
        HAVING SUM(HIS.VALOR) <>0
        ORDER BY MAX(HIS.ID_DE_EMPLEADO), HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES, HIS.PERIODO
        )
    LOOP

        --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS DE DEVENGOS NO AGRUPADOS POR EMPLEADO.  || FORMAT(RS.PERCENTPOSITION, '000.00') || %'
        MI_CUENTADEBITO := RPAD(' ', 16);
        MI_CUENTACREDITO := RPAD(' ', 16);
        MI_VALORD := 0;
        MI_VALORC := 0;
        MI_SUCURSAL := '';
        MI_TERCERO := RPAD(' ', 11);
        MI_CENTROCOSTO := '9999999999';
        
        -- (GPORTILLA:23/02/2023)
        MI_CUENTA := '';
        MI_REFERENCIA := '';
        MI_FUENTE_RECURSO := '';
        -- FIN (GPORTILLA:23/02/2023)
      
        --INI_1886_MPEREZ Se implementa manejo de fondo publico de casantias por grupos 
        IF(MI_MANEJA_FONDO_PUBLICO_CES = 'SI' AND MI_RS.TIPODEFONDO = 'CES' AND MI_RS.PUBLICO <> 0)THEN
          IF MI_RS.GRUPOCONTABLE = 'A' THEN
            MI_CUENTADEBITO := NVL(RPAD(MI_CTA_DBT_ADMIN_CESANT_PCA, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_CTA_CRD_ADMIN_CESANT_PCA, 16), RPAD(' ', 16));
          ELSIF MI_RS.GRUPOCONTABLE = 'V' THEN
            MI_CUENTADEBITO := NVL(RPAD(MI_CTA_DBT_VENTAS_CESANT_PCA, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_CTA_CRD_VENTAS_CESANT_PCA, 16), RPAD(' ', 16));
          ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN
            MI_CUENTADEBITO := NVL(RPAD(MI_CTA_DBT_PROD_CESANT_PCA, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_CTA_CRD_PROD_CESANT_PCA, 16), RPAD(' ', 16));
          ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN
            MI_CUENTADEBITO := NVL(RPAD(MI_CTA_DBT_OTRO_CESANT_PCA, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_CTA_CRD_OTRO_CESANT_PCA, 16), RPAD(' ', 16));
          ELSIF MI_RS.GRUPOCONTABLE = 'S' THEN
            MI_CUENTADEBITO := NVL(RPAD(MI_CTA_DBT_SUPERN_CESANT_PCA, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_CTA_CRD_SUPERN_CESANT_PCA, 16), RPAD(' ', 16));
          ELSE
            MI_CUENTADEBITO := NVL(RPAD(MI_CTA_DBT_PUENTE_CESANT_PCA, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_CTA_CRD_PUENTE_CESANT_PCA, 16), RPAD(' ', 16));
          END IF;
        ELSE
        	IF MI_RS.GRUPOCONTABLE = 'V' THEN
            	MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_VENTAS, 16), RPAD(' ', 16));
            	MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_VENTAS, 16), RPAD(' ', 16));
        	ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN
            	MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_PRODUCCION, 16), RPAD(' ', 16));
            	MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_PRODUCCION, 16), RPAD(' ', 16));
        	ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN
            	MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_OTRO, 16), RPAD(' ', 16));
            	MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_OTRO, 16), RPAD(' ', 16));
        	ELSE
            	MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
            	MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
        	END IF;
        END IF;
        IF NVL(TRIM(MI_CUENTADEBITO), ' ') <> ' ' THEN
            MI_VALORD := MI_RS.SUMADEVALOR;
        END IF;
        IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' ' THEN
            MI_VALORC := MI_RS.SUMADEVALOR;
        END IF;
        MI_TERCERO := NVL(RPAD(MI_RS.TERCERO, 11), '99999999999');
        MI_TERCERO := REPLACE(MI_TERCERO, '-', '');
        MI_TERCERO := REPLACE(MI_TERCERO, '.', '');

        IF MI_TERCERO = '99999999999' THEN
            MI_SUCURSAL := PCK_DATOS.CONS_SUCURSAL;
        ELSIF MI_TERCERO IS NULL OR MI_TERCERO = RPAD(' ', 11) THEN
            MI_TERCERO := '99999999999';
            MI_SUCURSAL := PCK_DATOS.CONS_SUCURSAL;
        ELSIF MI_RS.SUCURSAL <> '' THEN
            MI_SUCURSAL := MI_RS.SUCURSAL;
        ELSE
            MI_SUCURSAL := '001';
        END IF;
        MI_CENTROCOSTO := NVL(RPAD(MI_RS.ID_CENTRO_DE_COSTO, 10), '9999999999');
        IF UN_MANEJACENTROCOSTO = 0 THEN
            MI_CENTROCOSTO := '9999999999';
        END IF;
        
        -- (GPORTILLA:23/02/2023)el campo cuenta se envia en espacios vacios ya que au no esta definido de donde se debe tomar el dato
        MI_CUENTA := RPAD(' ', 16);
        --<TICKET: CC_436 AUTOR:CP FECHA:04/12/2024
        MI_COD_AUXILIAR := RPAD(' ', 16);
        MI_COD_AUXILIAR := NVL(MI_RS.COD_AUXILIAR,'');
        IF MI_COD_AUXILIAR =  '' THEN
            MI_COD_AUXILIAR := '9999999999999999';
        END IF;
        MI_COD_AUXILIAR := NVL(RPAD(MI_COD_AUXILIAR, 16), '9999999999999999');
        --<TICKET/>
        MI_REFERENCIA := NVL(MI_RS.REFERENCIA,'');
        IF MI_REFERENCIA =  '' THEN
            MI_REFERENCIA := '99999999999999999999';
        END IF;
        MI_REFERENCIA := NVL(RPAD(MI_REFERENCIA, 20),'99999999999999999999');
        
        MI_FUENTE_RECURSO := NVL(MI_RS.FUENTE_DE_RECURSO,'');
        IF MI_FUENTE_RECURSO =  '' THEN
            MI_FUENTE_RECURSO := '99999999999999999999';
        END IF;
        MI_FUENTE_RECURSO := NVL(RPAD(MI_FUENTE_RECURSO, 20),'99999999999999999999');
        --  FIN(GPORTILLA:23/02/2023)
        
        IF MI_VALORD <> 0 AND MI_VALORC <> 0 THEN

            MI_VRD := MI_VALORD;
            MI_VRC := CASE WHEN MI_VALORC > 0 THEN MI_VALORC ELSE MI_VALORC * -1 END;
            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTADEBITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VRD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_RS.NOMBRE_CONCEPTO, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTADEBITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VRC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_RS.NOMBRE_CONCEPTO, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
        ELSE
            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTADEBITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_RS.NOMBRE_CONCEPTO, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
        END IF;

    END LOOP CNDEVENGOSNOAGRUPADOS;

    <<DESCUENTOSPOREMPLEADO>>
    FOR MI_RS IN
        (SELECT SUM(HIS.VALOR) AS SUMADEVALOR, MAX(HIS.ID_DE_EMPLEADO) AS ID_DE_EMPLEADO, CN.TIPODEFONDO AS TIPODEFONDO, P.GRUPOCONTABLE AS GRUPOCONTABLE,
             CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS,
             CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, P.NUMERO_DCTO, MAX(HIS.ID_DE_CONCEPTO) AS ID_DE_CONCEPTO,
              MAX(CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377  AND MI_PAR_NOM_FUN_PLA =  'SI'
                      THEN  CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374)
                             THEN RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FR.NOMBRE_FONDO)) , 20) 
                             ELSE RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FS.NOMBRE_FONDO)) , 20) 
                            END || RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES('_' || P.NUMERO_DCTO || ' - ' || P.APELLIDO1 || ' ' || P.APELLIDO2 || ' ' || P.NOMBRES)),44)
                      ELSE  RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(CN.NOMBRE_CONCEPTO)),64)
                    END
                    ) NOMBRE_CONCEPTO,
             CN.SUCURSAL, P.ID_CENTRO_DE_COSTO, 
             CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                  THEN
                     CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                     THEN FR.NIT
                     ELSE FS.NIT
                     END 
                  ELSE  CN.TERCERO
             END   TERCERO,
             P.REFERENCIA, P.FUENTE_DE_RECURSO, P.COD_AUXILIAR
        FROM HISTORICOS HIS INNER JOIN CONCEPTOS CN
             ON HIS.COMPANIA = CN.COMPANIA
            AND HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
          INNER JOIN PERSONAL P
             ON HIS.COMPANIA = P.COMPANIA
            AND HIS.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
           LEFT JOIN V_FONDOS FS 
            ON   P.COMPANIA     =  FS.COMPANIA
            AND  P.FONDO_SALUD  = FS.COD_FONDO
          LEFT JOIN V_FONDOS FR 
            ON  P.COMPANIA  = FR.COMPANIA 
            AND P.FONDO_RIESGOS =  FR.COD_FONDO 
          INNER JOIN PERIODOS 
                ON HIS.COMPANIA = PERIODOS.COMPANIA
                AND HIS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                AND HIS.ANO = PERIODOS.ANO
                AND HIS.MES = PERIODOS.MES
                AND HIS.PERIODO = PERIODOS.PERIODO                      
        WHERE HIS.COMPANIA = UN_COMPANIA
          AND HIS.ID_DE_PROCESO = UN_PROCESO
          AND HIS.ANO = UN_ANO
          AND HIS.MES = UN_MES
          AND HIS.PERIODO = UN_PERIODO
          AND CN.NITDE = 'E'
          AND CN.CLASE IN (5,7) -- (GPORTILLA:23/02/2023)Se incluye la clase 7
      AND PERIODOS.ACUMULADO NOT IN(0)                   
        GROUP BY CN.TIPODEFONDO, P.GRUPOCONTABLE, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION,
               CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, P.NUMERO_DCTO, CN.SUCURSAL, P.ID_CENTRO_DE_COSTO, 
               CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                   THEN
                     CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                       THEN FR.NIT
                       ELSE FS.NIT
                     END 
                   ELSE  CN.TERCERO
               END,
             HIS.COMPANIA, HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES, HIS.PERIODO, P.REFERENCIA, P.FUENTE_DE_RECURSO, P.COD_AUXILIAR
        HAVING SUM(HIS.VALOR) <> 0
        ORDER BY CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS,
             CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, P.NUMERO_DCTO, HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES, HIS.PERIODO
        )
    LOOP
        --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS DE DESCUENTOS AGRUPADOS POR EMPLEADO.  || FORMAT(RS.PERCENTPOSITION, '000.00') || %'
        MI_CUENTADEBITO := RPAD(' ', 16);
        MI_CUENTACREDITO := RPAD(' ', 16);
        MI_VALORD := 0;
        MI_VALORC := 0;
        MI_SUCURSAL := '';
        MI_TERCERO := RPAD(' ', 11);
        MI_CENTROCOSTO := '          ';
        
        -- (GPORTILLA:23/02/2023)
        MI_CUENTA := '';
        MI_REFERENCIA := '';
        MI_FUENTE_RECURSO := '';
        -- FIN (GPORTILLA:23/02/2023)
        
        IF MI_RS.GRUPOCONTABLE = 'V' THEN
            MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_VENTAS, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_VENTAS, 16), RPAD(' ', 16));
        ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN
            MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_PRODUCCION, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_PRODUCCION, 16), RPAD(' ', 16));
        ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN
            MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_OTRO, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_OTRO, 16), RPAD(' ', 16));
        ELSE
            MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
        END IF;
        IF NVL(TRIM(MI_CUENTADEBITO), ' ') <> ' ' THEN
            MI_VALORD := MI_RS.SUMADEVALOR;
        END IF;
        IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' ' THEN
            MI_VALORC := MI_RS.SUMADEVALOR;
        END IF;
        MI_TERCERO := NVL(RPAD(MI_RS.TERCERO, 11), '99999999999');
        MI_TERCERO := REPLACE(MI_TERCERO, '-', '');
        MI_TERCERO := REPLACE(MI_TERCERO, '.', '');

        IF MI_TERCERO = '99999999999' THEN
            MI_SUCURSAL := PCK_DATOS.CONS_SUCURSAL;
            IF MI_TERCERO = '99999999999' AND MI_RS.NUMERO_DCTO IS NOT NULL THEN
                MI_TERCERO := NVL(RPAD(MI_RS.NUMERO_DCTO, 11), '99999999999');
                MI_SUCURSAL := '001' ;
            END IF;
        ELSIF MI_TERCERO IS NULL OR MI_TERCERO = RPAD(' ', 11) THEN
            MI_TERCERO := '99999999999';
            MI_SUCURSAL := PCK_DATOS.CONS_SUCURSAL;
        ELSIF MI_RS.SUCURSAL <> '' THEN
            MI_SUCURSAL := MI_RS.SUCURSAL;
        ELSE
            MI_SUCURSAL := '001';
        END IF;
        
        -- (GPORTILLA:23/02/2023)el campo cuenta se envia en espacios vacios ya que au no esta definido de donde se debe tomar el dato
        MI_CUENTA := RPAD(' ', 16);
        
       --<TICKET: CC_436 AUTOR:CP FECHA:04/12/2024
        MI_COD_AUXILIAR := RPAD(' ', 16);
        MI_COD_AUXILIAR := NVL(MI_RS.COD_AUXILIAR,'');
        IF MI_COD_AUXILIAR =  '' THEN
            MI_COD_AUXILIAR := '9999999999999999';
        END IF;
        MI_COD_AUXILIAR := NVL(RPAD(MI_COD_AUXILIAR, 16), '9999999999999999');
        --<TICKET/>
        MI_REFERENCIA := NVL(MI_RS.REFERENCIA,'');
        IF MI_REFERENCIA =  '' THEN
            MI_REFERENCIA := '99999999999999999999';
        END IF;
        MI_REFERENCIA := NVL(RPAD(MI_REFERENCIA, 20),'99999999999999999999');
        
        MI_FUENTE_RECURSO := NVL(MI_RS.FUENTE_DE_RECURSO,'');
        IF MI_FUENTE_RECURSO =  '' THEN
            MI_FUENTE_RECURSO := '99999999999999999999';
        END IF;
        MI_FUENTE_RECURSO := NVL(RPAD(MI_FUENTE_RECURSO, 20),'99999999999999999999');
        -- FIN (GPORTILLA:23/02/2023)

        IF NOT(MI_VALORD + MI_VALORC = 0) THEN
            --ALERTA 'EL CONCEPTO: || MI_RS.ID_DE_CONCEPTO || || MI_RS.NOMBRE_CONCEPTO ||, NO TIENE DEFINIDAS CUENTAS PARA EL GRUPO CONTABLE:  || MI_RS.GRUPOCONTABLE
            MI_CENTROCOSTO := NVL(RPAD(MI_RS.ID_CENTRO_DE_COSTO, 10), '9999999999');
            IF UN_MANEJACENTROCOSTO = 0 THEN
                MI_CENTROCOSTO := '9999999999';
            END IF;
            IF MI_VALORD <> 0 AND MI_VALORC <> 0 THEN
                MI_VRD := MI_VALORD;
                MI_VRC := MI_VALORC;
                MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VRD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_RS.NOMBRE_CONCEPTO, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
                MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VRC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_RS.NOMBRE_CONCEPTO, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
            ELSE
                MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_RS.NOMBRE_CONCEPTO, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
            END IF;
        END IF;
        --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS DE DESCUENTOS AGRUPADOS POR EMPLEADO.  || FORMAT(100, '000.00') || %'
    END LOOP DESCUENTOSPOREMPLEADO;

    <<DESCUENTOSPOROTROS>>
    
    IF UN_MANEJACENTROCOSTO = 0 THEN 
        FOR MI_RS IN
            (SELECT SUM(HIS.VALOR) AS SUMADEVALOR, MAX(HIS.ID_DE_EMPLEADO) AS ID_DE_EMPLEADO, CN.TIPODEFONDO AS TIPODEFONDO,
                   CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION,
                   CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, MAX(HIS.ID_DE_CONCEPTO) AS ID_DE_CONCEPTO,
                   MAX(CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377  AND MI_PAR_NOM_FUN_PLA =  'SI'
                      THEN  CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374)
                             THEN RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FR.NOMBRE_FONDO)) , 20) 
                             ELSE RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FS.NOMBRE_FONDO)) , 20) 
                            END || RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES('_' || P.NUMERO_DCTO || ' - ' || P.APELLIDO1 || ' ' || P.APELLIDO2 || ' ' || P.NOMBRES)),44)
                      ELSE  RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(CN.NOMBRE_CONCEPTO)),64)
                    END
                    ) NOMBRE_CONCEPTO,
                   CN.SUCURSAL, 
                   CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                    THEN
                     CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                      THEN FR.NIT
                      ELSE FS.NIT
                     END 
                    ELSE  CN.TERCERO
                   END   TERCERO,
                   P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR
            FROM HISTORICOS HIS LEFT JOIN CONCEPTOS CN
                         ON HIS.COMPANIA = CN.COMPANIA
                        AND HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
                    LEFT JOIN PERSONAL P
                         ON HIS.COMPANIA = P.COMPANIA
                        AND HIS.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
                    LEFT JOIN V_FONDOS FS 
                        ON   P.COMPANIA     =  FS.COMPANIA
                        AND  P.FONDO_SALUD  = FS.COD_FONDO
                      LEFT JOIN V_FONDOS FR 
                        ON  P.COMPANIA  = FR.COMPANIA 
                        AND P.FONDO_RIESGOS =  FR.COD_FONDO 
          INNER JOIN PERIODOS 
                        ON HIS.COMPANIA = PERIODOS.COMPANIA
                        AND HIS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                        AND HIS.ANO = PERIODOS.ANO
                        AND HIS.MES = PERIODOS.MES
                        AND HIS.PERIODO = PERIODOS.PERIODO                    
            WHERE HIS.COMPANIA = UN_COMPANIA
              AND HIS.ID_DE_PROCESO = UN_PROCESO
              AND HIS.ANO = UN_ANO
              AND HIS.MES = UN_MES
              AND HIS.PERIODO = UN_PERIODO
              AND CN.NITDE = 'O'
              AND CN.CLASE IN (5,7)
        AND PERIODOS.ACUMULADO NOT IN(0)                   
            GROUP BY CN.TIPODEFONDO, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION,
                     CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, CN.SUCURSAL, 
                     CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                       THEN
                         CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                           THEN FR.NIT
                           ELSE FS.NIT
                         END 
                       ELSE  CN.TERCERO
                     END,
                     HIS.COMPANIA, HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES, HIS.PERIODO, P.REFERENCIA, P.FUENTE_DE_RECURSO, P.COD_AUXILIAR
            HAVING SUM(HIS.VALOR) <> 0
            ORDER BY CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS,
                     CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES, HIS.PERIODO
            )
        LOOP
            --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS DE DESCUENTOS OTROS AGRUPADOS POR EMPLEADO.  || FORMAT(RS.PERCENTPOSITION, '000.00') || %'
            MI_CUENTADEBITO := RPAD(' ', 16);
            MI_CUENTACREDITO := RPAD(' ', 16);
            MI_VALORD := 0;
            MI_VALORC := 0;
            MI_SUCURSAL := '';
            MI_TERCERO := RPAD(' ', 11);
            MI_CENTROCOSTO := '          ';
            
            -- (GPORTILLA:23/02/2023)
            MI_CUENTA := '';
            MI_REFERENCIA := '';
            MI_FUENTE_RECURSO := '';
            
            -- (GPORTILLA:23/02/2023) Se comentan estas condiciones dbido a que las cuentas por pagar van todas a una unica cuenta y no depende del grupo contable
            /*IF MI_RS.GRUPOCONTABLE = 'V' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_VENTAS, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_VENTAS, 16), RPAD(' ', 16));
            ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_PRODUCCION, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_PRODUCCION, 16), RPAD(' ', 16));
            ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_OTRO, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_OTRO, 16), RPAD(' ', 16));
            ELSE
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
            END IF;*/
            MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
            -- FIN (GPORTILLA:23/02/2023)
            
            IF NVL(TRIM(MI_CUENTADEBITO), ' ') <> ' ' THEN
                MI_VALORD := MI_RS.SUMADEVALOR;
            END IF;
            IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' ' THEN
                MI_VALORC := MI_RS.SUMADEVALOR;
            END IF;
        
            MI_TERCERO := NVL(RPAD(MI_RS.TERCERO, 11), '99999999999');-- (GPORTILLA:23/02/2023) Se retira condicion del campo Tercero, ya que este campo no llega vacio
    
            MI_TERCERO := REPLACE(MI_TERCERO, '-', '');
            MI_TERCERO := REPLACE(MI_TERCERO, '.', '');
    
            IF MI_TERCERO = '99999999999' THEN
                MI_SUCURSAL := PCK_DATOS.CONS_SUCURSAL;
                ELSIF MI_TERCERO IS NULL OR MI_TERCERO = RPAD(' ', 11) THEN
                MI_TERCERO := '99999999999';
                MI_SUCURSAL := PCK_DATOS.CONS_SUCURSAL;
                ELSIF MI_RS.SUCURSAL <> '' THEN
                MI_SUCURSAL := MI_RS.SUCURSAL;
            ELSE
                MI_SUCURSAL := '001';
            END IF;
            
            -- (GPORTILLA:23/02/2023)el campo cuenta se envia en espacios vacios ya que au no esta definido de donde se debe tomar el dato
            MI_CUENTA := RPAD(' ', 16);
            
            --<TICKET: CC_436 AUTOR:CP FECHA:04/12/2024
        MI_COD_AUXILIAR := RPAD(' ', 16);
        MI_COD_AUXILIAR := NVL(MI_RS.COD_AUXILIAR,'');
        IF MI_COD_AUXILIAR =  '' THEN
            MI_COD_AUXILIAR := '9999999999999999';
        END IF;
        MI_COD_AUXILIAR := NVL(RPAD(MI_COD_AUXILIAR, 16), '9999999999999999');
        --<TICKET/>
        MI_REFERENCIA := NVL(MI_RS.REFERENCIA,'');
        IF MI_REFERENCIA =  '' THEN
            MI_REFERENCIA := '99999999999999999999';
        END IF;
        MI_REFERENCIA := NVL(RPAD(MI_REFERENCIA, 20),'99999999999999999999');
        
        MI_FUENTE_RECURSO := NVL(MI_RS.FUENTE_DE_RECURSO,'');
        IF MI_FUENTE_RECURSO =  '' THEN
            MI_FUENTE_RECURSO := '99999999999999999999';
        END IF;
        MI_FUENTE_RECURSO := NVL(RPAD(MI_FUENTE_RECURSO, 20),'99999999999999999999');
            -- FIN (GPORTILLA:23/02/2023)
            
            IF NOT(MI_VALORD + MI_VALORC = 0) THEN
                --ALERTA 'EL CONCEPTO: || MI_RS.ID_DE_CONCEPTO || || MI_RS.NOMBRE_CONCEPTO ||, NO TIENE DEFINIDAS CUENTAS PARA EL GRUPO CONTABLE:  || MI_RS.GRUPOCONTABLE
                
                MI_CENTROCOSTO := '9999999999';

                IF MI_VALORD <> 0 AND MI_VALORC <> 0 THEN
                    MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_RS.NOMBRE_CONCEPTO, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
                    MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_RS.NOMBRE_CONCEPTO, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
                ELSE
                    MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_RS.NOMBRE_CONCEPTO, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
                END IF;
            END IF;
            --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS DE DESCUENTOS OTROS AGRUPADOS POR EMPLEADO.  || FORMAT(100, '000.00') || %'
        END LOOP DESCUENTOSPOROTROS;
    ELSE
        FOR MI_RS IN
            (SELECT SUM(HIS.VALOR) AS SUMADEVALOR, MAX(HIS.ID_DE_EMPLEADO) AS ID_DE_EMPLEADO, CN.TIPODEFONDO AS TIPODEFONDO,
                   CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION,
                   CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, MAX(HIS.ID_DE_CONCEPTO) AS ID_DE_CONCEPTO,
                   MAX(CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377  AND MI_PAR_NOM_FUN_PLA =  'SI'
                      THEN  CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374)
                             THEN RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FR.NOMBRE_FONDO)) , 20) 
                             ELSE RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FS.NOMBRE_FONDO)) , 20) 
                            END || RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES('_' || P.NUMERO_DCTO || ' - ' || P.APELLIDO1 || ' ' || P.APELLIDO2 || ' ' || P.NOMBRES)),44)
                      ELSE  RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(CN.NOMBRE_CONCEPTO)),64)
                    END
                    ) NOMBRE_CONCEPTO,
                   CN.SUCURSAL, P.ID_CENTRO_DE_COSTO,
                   CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                     THEN
                       CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                         THEN FR.NIT
                         ELSE FS.NIT
                       END 
                     ELSE  CN.TERCERO
                   END   TERCERO,
                   P.REFERENCIA, P.FUENTE_DE_RECURSO, P.COD_AUXILIAR
            FROM HISTORICOS HIS LEFT JOIN CONCEPTOS CN
                         ON HIS.COMPANIA = CN.COMPANIA
                        AND HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
                    LEFT JOIN PERSONAL P
                         ON HIS.COMPANIA = P.COMPANIA
                        AND HIS.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
                    LEFT JOIN V_FONDOS FS 
                        ON   P.COMPANIA     =  FS.COMPANIA
                        AND  P.FONDO_SALUD  = FS.COD_FONDO
                      LEFT JOIN V_FONDOS FR 
                        ON  P.COMPANIA  = FR.COMPANIA 
                        AND P.FONDO_RIESGOS =  FR.COD_FONDO 
          INNER JOIN PERIODOS 
                        ON HIS.COMPANIA = PERIODOS.COMPANIA
                        AND HIS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                        AND HIS.ANO = PERIODOS.ANO
                        AND HIS.MES = PERIODOS.MES
                        AND HIS.PERIODO = PERIODOS.PERIODO                                    
            WHERE HIS.COMPANIA = UN_COMPANIA
              AND HIS.ID_DE_PROCESO = UN_PROCESO
              AND HIS.ANO = UN_ANO
              AND HIS.MES = UN_MES
              AND HIS.PERIODO = UN_PERIODO
              AND CN.NITDE = 'O'
              AND CN.CLASE IN (5,7)
        AND PERIODOS.ACUMULADO NOT IN(0)                   
            GROUP BY CN.TIPODEFONDO, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION,
                     CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, CN.SUCURSAL, P.ID_CENTRO_DE_COSTO, 
                     CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                        THEN
                            CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                               THEN FR.NIT
                               ELSE FS.NIT
                            END 
                        ELSE  CN.TERCERO
                     END,
                     HIS.COMPANIA, HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES, HIS.PERIODO, P.REFERENCIA, P.FUENTE_DE_RECURSO, P.COD_AUXILIAR
            HAVING SUM(HIS.VALOR) <> 0
            ORDER BY CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS,
                     CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES, HIS.PERIODO
            )
        LOOP
            --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS DE DESCUENTOS OTROS AGRUPADOS POR EMPLEADO.  || FORMAT(RS.PERCENTPOSITION, '000.00') || %'
            MI_CUENTADEBITO := RPAD(' ', 16);
            MI_CUENTACREDITO := RPAD(' ', 16);
            MI_VALORD := 0;
            MI_VALORC := 0;
            MI_SUCURSAL := '';
            MI_TERCERO := RPAD(' ', 11);
            MI_CENTROCOSTO := '          ';
            
            -- (GPORTILLA:23/02/2023)
            MI_CUENTA := '';
            MI_REFERENCIA := '';
            MI_FUENTE_RECURSO := '';
            
            -- (GPORTILLA:23/02/2023) Se comentan estas condiciones dbido a que las cuentas por pagar van todas a una unica cuenta y no depende del grupo contable
            /*IF MI_RS.GRUPOCONTABLE = 'V' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_VENTAS, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_VENTAS, 16), RPAD(' ', 16));
            ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_PRODUCCION, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_PRODUCCION, 16), RPAD(' ', 16));
            ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_OTRO, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_OTRO, 16), RPAD(' ', 16));
            ELSE
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
            END IF;*/
            MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
            -- FIN (GPORTILLA:23/02/2023)
            
            IF NVL(TRIM(MI_CUENTADEBITO), ' ') <> ' ' THEN
                MI_VALORD := MI_RS.SUMADEVALOR;
            END IF;
            IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' ' THEN
                MI_VALORC := MI_RS.SUMADEVALOR;
            END IF;
        
            MI_TERCERO := NVL(RPAD(MI_RS.TERCERO, 11), '99999999999');-- (GPORTILLA:23/02/2023) Se retira condicion del campo Tercero, ya que este campo no llega vacio
    
            MI_TERCERO := REPLACE(MI_TERCERO, '-', '');
            MI_TERCERO := REPLACE(MI_TERCERO, '.', '');
    
            IF MI_TERCERO = '99999999999' THEN
                MI_SUCURSAL := PCK_DATOS.CONS_SUCURSAL;
                ELSIF MI_TERCERO IS NULL OR MI_TERCERO = RPAD(' ', 11) THEN
                MI_TERCERO := '99999999999';
                MI_SUCURSAL := PCK_DATOS.CONS_SUCURSAL;
                ELSIF MI_RS.SUCURSAL <> '' THEN
                MI_SUCURSAL := MI_RS.SUCURSAL;
            ELSE
                MI_SUCURSAL := '001';
            END IF;
            
            -- (GPORTILLA:23/02/2023)el campo cuenta se envia en espacios vacios ya que au no esta definido de donde se debe tomar el dato
            MI_CUENTA := RPAD(' ', 16);
            
            --<TICKET: CC_436 AUTOR:CP FECHA:04/12/2024
        MI_COD_AUXILIAR := RPAD(' ', 16);
        MI_COD_AUXILIAR := NVL(MI_RS.COD_AUXILIAR,'');
        IF MI_COD_AUXILIAR =  '' THEN
            MI_COD_AUXILIAR := '9999999999999999';
        END IF;
        MI_COD_AUXILIAR := NVL(RPAD(MI_COD_AUXILIAR, 16), '9999999999999999');
        --<TICKET/>
        MI_REFERENCIA := NVL(MI_RS.REFERENCIA,'');
        IF MI_REFERENCIA =  '' THEN
            MI_REFERENCIA := '99999999999999999999';
        END IF;
        MI_REFERENCIA := NVL(RPAD(MI_REFERENCIA, 20),'99999999999999999999');
        
        MI_FUENTE_RECURSO := NVL(MI_RS.FUENTE_DE_RECURSO,'');
        IF MI_FUENTE_RECURSO =  '' THEN
            MI_FUENTE_RECURSO := '99999999999999999999';
        END IF;
        MI_FUENTE_RECURSO := NVL(RPAD(MI_FUENTE_RECURSO, 20),'99999999999999999999');
            -- FIN (GPORTILLA:23/02/2023)
            
            IF NOT(MI_VALORD + MI_VALORC = 0) THEN
                --ALERTA 'EL CONCEPTO: || MI_RS.ID_DE_CONCEPTO || || MI_RS.NOMBRE_CONCEPTO ||, NO TIENE DEFINIDAS CUENTAS PARA EL GRUPO CONTABLE:  || MI_RS.GRUPOCONTABLE
                MI_CENTROCOSTO := NVL(RPAD(MI_RS.ID_CENTRO_DE_COSTO, 10), '9999999999');
                
                IF MI_VALORD <> 0 AND MI_VALORC <> 0 THEN
                    MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_RS.NOMBRE_CONCEPTO, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
                    MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_RS.NOMBRE_CONCEPTO, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
                ELSE
                    MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_RS.NOMBRE_CONCEPTO, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
                END IF;
            END IF;
            --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS DE DESCUENTOS OTROS AGRUPADOS POR EMPLEADO.  || FORMAT(100, '000.00') || %'
        END LOOP DESCUENTOSPOROTROS;
    END IF;

    <<DESCUENTOSPORFONDOS>>
    IF UN_MANEJACENTROCOSTO = 0 THEN -- (GPORTILLA:23/02/2023) se agrega condicion para que genere el plano teniendo en cuenta el indicador de centro de costo
         FOR MI_RS IN
            (SELECT SUM(HIS.VALOR) AS SUMADEVALOR, CN.TIPODEFONDO AS TIPODEFONDO, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO,
                    MAX(HIS.ID_DE_CONCEPTO) AS ID_DE_CONCEPTO,
                    MAX(CN.NOMBRE_CONCEPTO) AS NOMBRE_CONCEPTO,
                    CN.SUCURSAL AS SUCURSAL,
                    CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377  AND MI_PAR_NOM_FUN_PLA =  'SI'
                    THEN
                      CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                        THEN FR.NIT
                        ELSE FS.NIT
                       END 
                      ELSE  CN.TERCERO
                    END   TERCERO
                    ,
                      CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                      THEN
                        CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                          THEN FR.NIT
                          ELSE FS.NIT
                        END 
                      ELSE
                        CASE CN.TIPODEFONDO
                          WHEN 'EPS' THEN V_FONDO_DE_SALUD.NIT
                          WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NIT
                          WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NIT
                          WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NIT
                          WHEN 'APV' THEN V_FONDO_DE_PENSION_VOL.NIT
                          WHEN 'CCF' THEN V_CAJA_COMPENSACION.NIT
                          WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NIT
                          WHEN 'SIN' THEN V_FONDO_DE_SINDICATOS.NIT
                          WHEN 'AFC' THEN V_FONDO_DE_PENSION_AFC.NIT
                          ELSE ''
                        END 
                      END NIT
                    ,

                    CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                     THEN  CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374)
                             THEN RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FR.NOMBRE_FONDO)) , 20) 
                             ELSE RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FS.NOMBRE_FONDO)) , 20) 
                           END || RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES('_' || P.NUMERO_DCTO || ' - ' || P.APELLIDO1 || ' ' || P.APELLIDO2 || ' ' || P.NOMBRES)),44)
                     ELSE  RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(
                          CASE CN.TIPODEFONDO
                            WHEN 'EPS' THEN V_FONDO_DE_SALUD.NOMBRE_FONDO_SALUD
                            WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NOMBRE_DEL_FONDO
                            WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NOMBRE_FONDO_RIESGOS
                            WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NOMBRE_FONDO_CESANTIAS
                            WHEN 'APV' THEN V_FONDO_DE_PENSION_VOL.NOMBRE_DEL_FONDO
                            WHEN 'CCF' THEN V_CAJA_COMPENSACION.NOMBRE_CAJA
                            WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NOMBRE_MEDICINA
                            WHEN 'SIN' THEN V_FONDO_DE_SINDICATOS.NOMBRE_FONDO_SINDICATO
                            WHEN 'AFC' THEN V_FONDO_DE_PENSION_AFC.NOMBRE_FONDO_AFC
                            ELSE ''
                          END)),64)
                  END  NOMBREFONDO,
                    P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR
            FROM HISTORICOS HIS INNER JOIN CONCEPTOS CN
                         ON HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
                        AND HIS.COMPANIA = CN.COMPANIA
                    INNER JOIN PERSONAL P
                         ON HIS.COMPANIA = P.COMPANIA
                        AND HIS.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
    
                    LEFT JOIN V_FONDO_DE_SALUD     --EPS
                         ON P.COMPANIA = V_FONDO_DE_SALUD.COMPANIA
                        AND P.FONDO_SALUD = V_FONDO_DE_SALUD.FONDO_SALUD
                    LEFT JOIN V_FONDO_DE_PENSIONES    --AFP
                         ON P.COMPANIA = V_FONDO_DE_PENSIONES.COMPANIA
                        AND P.ID_DEL_FONDO = V_FONDO_DE_PENSIONES.ID_DEL_FONDO
    
                    LEFT JOIN V_FONDO_DE_RIESGOS      --ARL
                        ON  P.COMPANIA = V_FONDO_DE_RIESGOS.COMPANIA
                        AND P.FONDO_RIESGOS = V_FONDO_DE_RIESGOS.FONDO_RIESGOS
                    LEFT JOIN V_FONDO_DE_CESANTIAS  --CES
                         ON P.COMPANIA = V_FONDO_DE_CESANTIAS.COMPANIA
                        AND P.FONDO_CESANTIAS = V_FONDO_DE_CESANTIAS.FONDO_CESANTIAS
                    LEFT JOIN V_FONDO_DE_PENSION_VOL --APV
                         ON P.COMPANIA = V_FONDO_DE_PENSION_VOL.COMPANIA
                        AND P.FONDO_PENSION_VOL = V_FONDO_DE_PENSION_VOL.ID_DEL_FONDO
                    LEFT JOIN V_CAJA_COMPENSACION     --CCF
                         ON P.COMPANIA = V_CAJA_COMPENSACION.COMPANIA
                        AND P.CAJA_COMPENSACION = V_CAJA_COMPENSACION.CAJA_COMPENSACION
                    LEFT JOIN V_MEDICINA_PREPAGADA  --FMP
                         ON P.COMPANIA = V_MEDICINA_PREPAGADA.COMPANIA
                        AND P.MEDICINA_PREPAGADA = V_MEDICINA_PREPAGADA.MEDICINA_PREPAGADA
                    LEFT JOIN V_FONDO_DE_SINDICATOS   --SIN
                         ON P.COMPANIA = V_FONDO_DE_SINDICATOS.COMPANIA
                        AND P.FONDO_SINDICATO = V_FONDO_DE_SINDICATOS.FONDO_SINDICATO
                    LEFT JOIN V_FONDO_DE_PENSION_AFC  --SIN
                         ON P.COMPANIA = V_FONDO_DE_PENSION_AFC.COMPANIA
                        AND P.FONDO_AFC = V_FONDO_DE_PENSION_AFC.FONDO_AFC   
                    LEFT JOIN V_FONDOS FS 
                      ON   P.COMPANIA     =  FS.COMPANIA
                      AND  P.FONDO_SALUD  = FS.COD_FONDO
                    LEFT JOIN V_FONDOS FR 
                      ON  P.COMPANIA  = FR.COMPANIA 
                      AND P.FONDO_RIESGOS =  FR.COD_FONDO             
          INNER JOIN PERIODOS 
                        ON HIS.COMPANIA = PERIODOS.COMPANIA
                        AND HIS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                        AND HIS.ANO = PERIODOS.ANO
                        AND HIS.MES = PERIODOS.MES
                        AND HIS.PERIODO = PERIODOS.PERIODO 
            WHERE HIS.COMPANIA = UN_COMPANIA
              AND HIS.ID_DE_PROCESO = UN_PROCESO
              AND HIS.ANO = UN_ANO
              AND HIS.MES = UN_MES
              AND HIS.PERIODO = UN_PERIODO
              AND CN.NITDE NOT IN ('O','E' )
              AND CN.CLASE = 5
              AND CN.TIPODEFONDO IN ('EPS','AFP','ARL','CES','APV','CCF','FMP','SIN','AFC')
        AND PERIODOS.ACUMULADO NOT IN(0) 
            GROUP BY CN.TIPODEFONDO, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION,
                     CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS,
                     CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, HIS.COMPANIA, HIS.ID_DE_PROCESO,
                     HIS.ANO, HIS.MES, HIS.PERIODO ,
                     CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                      THEN
                        CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                          THEN FR.NIT
                          ELSE FS.NIT
                        END 
                      ELSE
                        CASE CN.TIPODEFONDO
                          WHEN 'EPS' THEN V_FONDO_DE_SALUD.NIT
                          WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NIT
                          WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NIT
                          WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NIT
                          WHEN 'APV' THEN V_FONDO_DE_PENSION_VOL.NIT
                          WHEN 'CCF' THEN V_CAJA_COMPENSACION.NIT
                          WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NIT
                          WHEN 'SIN' THEN V_FONDO_DE_SINDICATOS.NIT
                          WHEN 'AFC' THEN V_FONDO_DE_PENSION_AFC.NIT
                          ELSE ''
                        END 
                      END 
                    ,

                    CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                      THEN  CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374)
                             THEN RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FR.NOMBRE_FONDO)) , 20) 
                             ELSE RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FS.NOMBRE_FONDO)) , 20) 
                            END || RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES('_' || P.NUMERO_DCTO || ' - ' || P.APELLIDO1 || ' ' || P.APELLIDO2 || ' ' || P.NOMBRES)),44)
                      ELSE RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(
                          CASE CN.TIPODEFONDO
                            WHEN 'EPS' THEN V_FONDO_DE_SALUD.NOMBRE_FONDO_SALUD
                            WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NOMBRE_DEL_FONDO
                            WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NOMBRE_FONDO_RIESGOS
                            WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NOMBRE_FONDO_CESANTIAS
                            WHEN 'APV' THEN V_FONDO_DE_PENSION_VOL.NOMBRE_DEL_FONDO
                            WHEN 'CCF' THEN V_CAJA_COMPENSACION.NOMBRE_CAJA
                            WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NOMBRE_MEDICINA
                            WHEN 'SIN' THEN V_FONDO_DE_SINDICATOS.NOMBRE_FONDO_SINDICATO
                            WHEN 'AFC' THEN V_FONDO_DE_PENSION_AFC.NOMBRE_FONDO_AFC
                            ELSE ''
                          END)),64)
                    END 

                    ,CN.SUCURSAL,
                    CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                      THEN
                        CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                          THEN FR.NIT
                          ELSE FS.NIT
                        END 
                      ELSE  CN.TERCERO
                    END,
                    P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR
            HAVING SUM(HIS.VALOR) <> 0
            ORDER BY CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS,
            CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES, HIS.PERIODO
            )
        LOOP
            --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS POR FONDOS. || MI_RS.NOMBREFONDO || || FORMAT(RS.PERCENTPOSITION, '000.00') || %'
            MI_CUENTADEBITO := RPAD(' ', 16);
            MI_CUENTACREDITO := RPAD(' ', 16);
            MI_VALORD := 0;
            MI_VALORC := 0;
            MI_SUCURSAL := '';
            MI_TERCERO := RPAD(' ', 11);
            MI_CENTROCOSTO := '          ';
            
            -- (GPORTILLA:23/02/2023)
            MI_CUENTA := '';
            MI_REFERENCIA := '';
            MI_FUENTE_RECURSO := '';
            -- FIN (GPORTILLA:23/02/2023)
            -- (GPORTILLA:23/02/2023) Se comentan estas condiciones dbido a que las cuentas por pagar van todas a una unica cuenta y no depende del grupo contable
            /*IF MI_RS.GRUPOCONTABLE = 'V' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_VENTAS, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_VENTAS, 16), RPAD(' ', 16));
                ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_PRODUCCION, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_PRODUCCION, 16), RPAD(' ', 16));
                ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_OTRO, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_OTRO, 16), RPAD(' ', 16));
            ELSE
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
            END IF;*/
            MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
            
            IF NVL(TRIM(MI_CUENTADEBITO), ' ') <> ' ' THEN
                MI_VALORD := MI_RS.SUMADEVALOR;
            END IF;
            IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' ' THEN
                MI_VALORC := MI_RS.SUMADEVALOR;
            END IF;
            
            -- (GPORTILLA:23/02/2023)el campo cuenta se envia en espacios vacios ya que au no esta definido de donde se debe tomar el dato
            MI_CUENTA := RPAD(' ', 16);
            
          --<TICKET: CC_436 AUTOR:CP FECHA:04/12/2024
        MI_COD_AUXILIAR := RPAD(' ', 16);
        MI_COD_AUXILIAR := NVL(MI_RS.COD_AUXILIAR,'');
        IF MI_COD_AUXILIAR =  '' THEN
            MI_COD_AUXILIAR := '9999999999999999';
        END IF;
        MI_COD_AUXILIAR := NVL(RPAD(MI_COD_AUXILIAR, 16), '9999999999999999');
        --<TICKET/>
        MI_REFERENCIA := NVL(MI_RS.REFERENCIA,'');
        IF MI_REFERENCIA =  '' THEN
            MI_REFERENCIA := '99999999999999999999';
        END IF;
        MI_REFERENCIA := NVL(RPAD(MI_REFERENCIA, 20),'99999999999999999999');
        
        MI_FUENTE_RECURSO := NVL(MI_RS.FUENTE_DE_RECURSO,'');
        IF MI_FUENTE_RECURSO =  '' THEN
            MI_FUENTE_RECURSO := '99999999999999999999';
        END IF;
        MI_FUENTE_RECURSO := NVL(RPAD(MI_FUENTE_RECURSO, 20),'99999999999999999999');
            -- FIN (GPORTILLA:23/02/2023)
            
            IF NOT(MI_VALORD + MI_VALORC = 0) THEN
                --ALERTA 'EL CONCEPTO: || MI_RS.ID_DE_CONCEPTO || || MI_RS.NOMBRE_CONCEPTO ||, NO TIENE DEFINIDAS CUENTAS PARA EL GRUPO CONTABLE:  || MI_RS.GRUPOCONTABLE
    
                MI_TERCERO := MI_RS.NIT;
                MI_TERCERO := REPLACE(MI_TERCERO, '-', '');
                MI_TERCERO := REPLACE(MI_TERCERO, '.', '');
                MI_TERCERO := NVL(RPAD(MI_TERCERO, 11), '99999999999');
                IF MI_TERCERO = '99999999999' THEN
                    MI_SUCURSAL := PCK_DATOS.CONS_SUCURSAL;
                ELSIF MI_TERCERO IS NULL OR MI_TERCERO = RPAD(' ', 11) THEN
                    MI_TERCERO := '99999999999';
                    MI_SUCURSAL := PCK_DATOS.CONS_SUCURSAL;
                ELSIF MI_RS.SUCURSAL <> '' THEN
                    MI_SUCURSAL := MI_RS.SUCURSAL;
                ELSE
                    MI_SUCURSAL := '001';
                END IF;
                MI_NOMBRE := PCK_NOMINA_COM5.FC_QUITAESPECIALES(MI_RS.NOMBREFONDO);
                
                MI_CENTROCOSTO := '9999999999';
                
                MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
            END IF; --CONTINUAR3:
    
            --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS POR FONDOS. || FORMAT(100, '000.00') || %'
    
        END LOOP DESCUENTOSPORFONDOS;

    ELSE
         FOR MI_RS IN
            (SELECT SUM(HIS.VALOR) AS SUMADEVALOR, CN.TIPODEFONDO AS TIPODEFONDO, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO,
                    MAX(HIS.ID_DE_CONCEPTO) AS ID_DE_CONCEPTO,
                    MAX(CN.NOMBRE_CONCEPTO) AS NOMBRE_CONCEPTO,
                    CN.SUCURSAL AS SUCURSAL, P.ID_CENTRO_DE_COSTO,
                    CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                    THEN
                      CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                        THEN FR.NIT
                        ELSE FS.NIT
                       END 
                      ELSE  CN.TERCERO
                    END   TERCERO
                    ,CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                      THEN
                        CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                          THEN FR.NIT
                          ELSE FS.NIT
                        END 
                      ELSE
                        CASE CN.TIPODEFONDO
                          WHEN 'EPS' THEN V_FONDO_DE_SALUD.NIT
                          WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NIT
                          WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NIT
                          WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NIT
                          WHEN 'APV' THEN V_FONDO_DE_PENSION_VOL.NIT
                          WHEN 'CCF' THEN V_CAJA_COMPENSACION.NIT
                          WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NIT
                          WHEN 'SIN' THEN V_FONDO_DE_SINDICATOS.NIT
                          WHEN 'AFC' THEN V_FONDO_DE_PENSION_AFC.NIT
                          ELSE ''
                        END 
                      END  NIT
                    ,CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                        THEN  CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374)
                               THEN RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FR.NOMBRE_FONDO)) , 20) 
                               ELSE RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FS.NOMBRE_FONDO)) , 20) 
                              END || RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES('_' || P.NUMERO_DCTO || ' - ' || P.APELLIDO1 || ' ' || P.APELLIDO2 || ' ' || P.NOMBRES)),44)
                        ELSE RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(
                          CASE CN.TIPODEFONDO
                            WHEN 'EPS' THEN V_FONDO_DE_SALUD.NOMBRE_FONDO_SALUD
                            WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NOMBRE_DEL_FONDO
                            WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NOMBRE_FONDO_RIESGOS
                            WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NOMBRE_FONDO_CESANTIAS
                            WHEN 'APV' THEN V_FONDO_DE_PENSION_VOL.NOMBRE_DEL_FONDO
                            WHEN 'CCF' THEN V_CAJA_COMPENSACION.NOMBRE_CAJA
                            WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NOMBRE_MEDICINA
                            WHEN 'SIN' THEN V_FONDO_DE_SINDICATOS.NOMBRE_FONDO_SINDICATO
                            WHEN 'AFC' THEN V_FONDO_DE_PENSION_AFC.NOMBRE_FONDO_AFC
                            ELSE ''
                          END)),64)
                    END  NOMBREFONDO,
                    P.REFERENCIA REFERENCIA, P.FUENTE_DE_RECURSO FUENTE_DE_RECURSO,  P.COD_AUXILIAR COD_AUXILIAR
            FROM HISTORICOS HIS INNER JOIN CONCEPTOS CN
                         ON HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
                        AND HIS.COMPANIA = CN.COMPANIA
                    INNER JOIN PERSONAL P
                         ON HIS.COMPANIA = P.COMPANIA
                        AND HIS.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
    
                    LEFT JOIN V_FONDO_DE_SALUD     --EPS
                         ON P.COMPANIA = V_FONDO_DE_SALUD.COMPANIA
                        AND P.FONDO_SALUD = V_FONDO_DE_SALUD.FONDO_SALUD
                    LEFT JOIN V_FONDO_DE_PENSIONES    --AFP
                         ON P.COMPANIA = V_FONDO_DE_PENSIONES.COMPANIA
                        AND P.ID_DEL_FONDO = V_FONDO_DE_PENSIONES.ID_DEL_FONDO
    
                    LEFT JOIN V_FONDO_DE_RIESGOS      --ARL
                        ON  P.COMPANIA = V_FONDO_DE_RIESGOS.COMPANIA
                        AND P.FONDO_RIESGOS = V_FONDO_DE_RIESGOS.FONDO_RIESGOS
                    LEFT JOIN V_FONDO_DE_CESANTIAS  --CES
                         ON P.COMPANIA = V_FONDO_DE_CESANTIAS.COMPANIA
                        AND P.FONDO_CESANTIAS = V_FONDO_DE_CESANTIAS.FONDO_CESANTIAS
                    LEFT JOIN V_FONDO_DE_PENSION_VOL --APV
                         ON P.COMPANIA = V_FONDO_DE_PENSION_VOL.COMPANIA
                        AND P.FONDO_PENSION_VOL = V_FONDO_DE_PENSION_VOL.ID_DEL_FONDO
                    LEFT JOIN V_CAJA_COMPENSACION     --CCF
                         ON P.COMPANIA = V_CAJA_COMPENSACION.COMPANIA
                        AND P.CAJA_COMPENSACION = V_CAJA_COMPENSACION.CAJA_COMPENSACION
                    LEFT JOIN V_MEDICINA_PREPAGADA  --FMP
                         ON P.COMPANIA = V_MEDICINA_PREPAGADA.COMPANIA
                        AND P.MEDICINA_PREPAGADA = V_MEDICINA_PREPAGADA.MEDICINA_PREPAGADA
                    LEFT JOIN V_FONDO_DE_SINDICATOS   --SIN
                         ON P.COMPANIA = V_FONDO_DE_SINDICATOS.COMPANIA
                        AND P.FONDO_SINDICATO = V_FONDO_DE_SINDICATOS.FONDO_SINDICATO
                    LEFT JOIN V_FONDO_DE_PENSION_AFC  --SIN
                         ON P.COMPANIA = V_FONDO_DE_PENSION_AFC.COMPANIA
                        AND P.FONDO_AFC = V_FONDO_DE_PENSION_AFC.FONDO_AFC   
                     LEFT JOIN V_FONDOS FS 
                        ON   P.COMPANIA     =  FS.COMPANIA
                        AND  P.FONDO_SALUD  = FS.COD_FONDO
                      LEFT JOIN V_FONDOS FR 
                        ON  P.COMPANIA  = FR.COMPANIA 
                        AND P.FONDO_RIESGOS =  FR.COD_FONDO              
          INNER JOIN PERIODOS 
                        ON HIS.COMPANIA = PERIODOS.COMPANIA
                        AND HIS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                        AND HIS.ANO = PERIODOS.ANO
                        AND HIS.MES = PERIODOS.MES
                        AND HIS.PERIODO = PERIODOS.PERIODO
            WHERE HIS.COMPANIA = UN_COMPANIA
              AND HIS.ID_DE_PROCESO = UN_PROCESO
              AND HIS.ANO = UN_ANO
              AND HIS.MES = UN_MES
              AND HIS.PERIODO = UN_PERIODO
              AND CN.NITDE NOT IN ('O','E' )
              AND CN.CLASE = 5
              AND CN.TIPODEFONDO IN ('EPS','AFP','ARL','CES','APV','CCF','FMP','SIN','AFC')
              AND PERIODOS.ACUMULADO NOT IN(0) 
            GROUP BY CN.TIPODEFONDO, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION,
                     CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS,
                     CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, HIS.COMPANIA, HIS.ID_DE_PROCESO, P.ID_CENTRO_DE_COSTO,
                     HIS.ANO, HIS.MES, HIS.PERIODO ,
                     CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                      THEN
                        CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                          THEN FR.NIT
                          ELSE FS.NIT
                        END 
                      ELSE
                        CASE CN.TIPODEFONDO
                          WHEN 'EPS' THEN V_FONDO_DE_SALUD.NIT
                          WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NIT
                          WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NIT
                          WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NIT
                          WHEN 'APV' THEN V_FONDO_DE_PENSION_VOL.NIT
                          WHEN 'CCF' THEN V_CAJA_COMPENSACION.NIT
                          WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NIT
                          WHEN 'SIN' THEN V_FONDO_DE_SINDICATOS.NIT
                          WHEN 'AFC' THEN V_FONDO_DE_PENSION_AFC.NIT
                          ELSE ''
                        END 
                      END 
                    ,
                    CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                      THEN  CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374)
                             THEN RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FR.NOMBRE_FONDO)) , 20) 
                             ELSE RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(FS.NOMBRE_FONDO)) , 20) 
                            END || RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES('_' || P.NUMERO_DCTO || ' - ' || P.APELLIDO1 || ' ' || P.APELLIDO2 || ' ' || P.NOMBRES)),44)
                      ELSE RPAD( TO_CHAR(PCK_NOMINA_COM5.FC_QUITAESPECIALES(
                          CASE CN.TIPODEFONDO
                            WHEN 'EPS' THEN V_FONDO_DE_SALUD.NOMBRE_FONDO_SALUD
                            WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NOMBRE_DEL_FONDO
                            WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NOMBRE_FONDO_RIESGOS
                            WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NOMBRE_FONDO_CESANTIAS
                            WHEN 'APV' THEN V_FONDO_DE_PENSION_VOL.NOMBRE_DEL_FONDO
                            WHEN 'CCF' THEN V_CAJA_COMPENSACION.NOMBRE_CAJA
                            WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NOMBRE_MEDICINA
                            WHEN 'SIN' THEN V_FONDO_DE_SINDICATOS.NOMBRE_FONDO_SINDICATO
                            WHEN 'AFC' THEN V_FONDO_DE_PENSION_AFC.NOMBRE_FONDO_AFC
                            ELSE ''
                          END)),64)
                    END 
                    ,CN.SUCURSAL,
                    CASE WHEN HIS.ID_DE_CONCEPTO BETWEEN 370 AND 377 AND MI_PAR_NOM_FUN_PLA =  'SI'
                      THEN
                        CASE WHEN  HIS.ID_DE_CONCEPTO IN(377,374) 
                          THEN FR.NIT
                          ELSE FS.NIT
                        END 
                      ELSE  CN.TERCERO
                    END,
                    P.ID_CENTRO_DE_COSTO, P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR
            HAVING SUM(HIS.VALOR) <> 0
            ORDER BY CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS,
            CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES, HIS.PERIODO
            )
        LOOP
            --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS POR FONDOS. || MI_RS.NOMBREFONDO || || FORMAT(RS.PERCENTPOSITION, '000.00') || %'
            MI_CUENTADEBITO := RPAD(' ', 16);
            MI_CUENTACREDITO := RPAD(' ', 16);
            MI_VALORD := 0;
            MI_VALORC := 0;
            MI_SUCURSAL := '';
            MI_TERCERO := RPAD(' ', 11);
            MI_CENTROCOSTO := '          ';
            
            -- (GPORTILLA:23/02/2023)
            MI_CUENTA := '';
            MI_REFERENCIA := '';
            MI_FUENTE_RECURSO := '';
            -- FIN (GPORTILLA:23/02/2023)
            -- (GPORTILLA:23/02/2023) Se comentan estas condiciones dbido a que las cuentas por pagar van todas a una unica cuenta y no depende del grupo contable
            /*IF MI_RS.GRUPOCONTABLE = 'V' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_VENTAS, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_VENTAS, 16), RPAD(' ', 16));
                ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_PRODUCCION, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_PRODUCCION, 16), RPAD(' ', 16));
                ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_OTRO, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_OTRO, 16), RPAD(' ', 16));
            ELSE
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
            END IF;*/
            MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
            MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
            
            IF NVL(TRIM(MI_CUENTADEBITO), ' ') <> ' ' THEN
                MI_VALORD := MI_RS.SUMADEVALOR;
            END IF;
            IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' ' THEN
                MI_VALORC := MI_RS.SUMADEVALOR;
            END IF;
            
            -- (GPORTILLA:23/02/2023)el campo cuenta se envia en espacios vacios ya que au no esta definido de donde se debe tomar el dato
            MI_CUENTA := RPAD(' ', 16);
            
            --<TICKET: CC_436 AUTOR:CP FECHA:04/12/2024
        MI_COD_AUXILIAR := RPAD(' ', 16);
        MI_COD_AUXILIAR := NVL(MI_RS.COD_AUXILIAR,'');
        IF MI_COD_AUXILIAR =  '' THEN
            MI_COD_AUXILIAR := '9999999999999999';
        END IF;
        MI_COD_AUXILIAR := NVL(RPAD(MI_COD_AUXILIAR, 16), '9999999999999999');
        --<TICKET/>
        MI_REFERENCIA := NVL(MI_RS.REFERENCIA,'');
        IF MI_REFERENCIA =  '' THEN
            MI_REFERENCIA := '99999999999999999999';
        END IF;
        MI_REFERENCIA := NVL(RPAD(MI_REFERENCIA, 20),'99999999999999999999');
        
        MI_FUENTE_RECURSO := NVL(MI_RS.FUENTE_DE_RECURSO,'');
        IF MI_FUENTE_RECURSO =  '' THEN
            MI_FUENTE_RECURSO := '99999999999999999999';
        END IF;
        MI_FUENTE_RECURSO := NVL(RPAD(MI_FUENTE_RECURSO, 20),'99999999999999999999');
            -- FIN (GPORTILLA:23/02/2023)
            
            IF NOT(MI_VALORD + MI_VALORC = 0) THEN
                --ALERTA 'EL CONCEPTO: || MI_RS.ID_DE_CONCEPTO || || MI_RS.NOMBRE_CONCEPTO ||, NO TIENE DEFINIDAS CUENTAS PARA EL GRUPO CONTABLE:  || MI_RS.GRUPOCONTABLE
    
                MI_TERCERO := MI_RS.NIT;
                MI_TERCERO := REPLACE(MI_TERCERO, '-', '');
                MI_TERCERO := REPLACE(MI_TERCERO, '.', '');
                MI_TERCERO := NVL(RPAD(MI_TERCERO, 11), '99999999999');
                IF MI_TERCERO = '99999999999' THEN
                    MI_SUCURSAL := PCK_DATOS.CONS_SUCURSAL;
                ELSIF MI_TERCERO IS NULL OR MI_TERCERO = RPAD(' ', 11) THEN
                    MI_TERCERO := '99999999999';
                    MI_SUCURSAL := PCK_DATOS.CONS_SUCURSAL;
                ELSIF MI_RS.SUCURSAL <> '' THEN
                    MI_SUCURSAL := MI_RS.SUCURSAL;
                ELSE
                    MI_SUCURSAL := '001';
                END IF;
                MI_NOMBRE := PCK_NOMINA_COM5.FC_QUITAESPECIALES(MI_RS.NOMBREFONDO);
                MI_CENTROCOSTO := NVL(RPAD(MI_RS.ID_CENTRO_DE_COSTO, 10), '9999999999');
                MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(UN_NUMEROCOMPROBANTE, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO ||MI_COD_AUXILIAR|| RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
            END IF; --CONTINUAR3:
    
            --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS POR FONDOS. || FORMAT(100, '000.00') || %'
    
        END LOOP DESCUENTOSPORFONDOS;

    END IF;
   
    RETURN MI_RTAPLANO;
END FC_PLANOCONTABILIZARSYSMAN;


FUNCTION FC_PLANOCONTABILIZAPROVICIONES(
/*
    NAME              : FC_PLANOCONTABILIZAPROVICIONES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 23/06/2018
    TIME              : 08:58 AM
    SOURCE MODULE     : NOMINAP2018.06.05, Función InterfacesysmanContableCCPROVISIONES
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    PARAMETERS        :

    MODIFICATIONS     :

    @NAME:planoContabilizarccprovisiones
    @METHOD:  GET
*/
     UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_PROCESO             IN PCK_SUBTIPOS.TI_ID_DE_PROCESO
    ,UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO
    ,UN_MES               IN PCK_SUBTIPOS.TI_MES
    ,UN_PERIODO             IN  PCK_SUBTIPOS.TI_PERIODO_NOMI
    ,UN_FECHAINTER          IN DATE
    ,UN_TIPOCOMPROBANTE     IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE DEFAULT 'NOM'
    ,UN_NUMEROCOMPROBANTE   IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT
    ,UN_AUXTERCERO          IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0         --FORMS!INTERFACE_NOMINA!PRVAUXTER
    ,UN_MANEJACENTROCOSTO   IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0 --FORMS!INTERFACE_NOMINA!MANEJACENTROS
) RETURN CLOB
AS
    MI_RS                      SYS_REFCURSOR;
    MI_NUMERO                  PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
    MI_CONDICIONACME           PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS                  PCK_SUBTIPOS.TI_CAMPOS;
    MI_FILAS                   PCK_SUBTIPOS.TI_ENTERO;
    MI_CUENTAREGISTRO          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_CUENTADEBITO            PCK_SUBTIPOS.TI_CODIGOCONTA;
    MI_CUENTACREDITO           PCK_SUBTIPOS.TI_CODIGOCONTA;
    MI_VALORD                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALORC                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_SUCURSAL                PCK_SUBTIPOS.TI_SUCURSAL;
    MI_TERCERO                 PCK_SUBTIPOS.TI_TERCERO;
    MI_CENTROCOSTO             PCK_SUBTIPOS.TI_CENTRO_COSTO;
    MI_NOMBRE                  VARCHAR2(250 CHAR);
    MI_RTAPLANO                CLOB;
    MI_EXISTEDATOS             BOOLEAN DEFAULT FALSE;
    MI_REEMPLAZOS              PCK_SUBTIPOS.TI_CLAVEVALOR;
    
    -- (GPORTILLA:23/02/2023)
    MI_CUENTA                  PCK_SUBTIPOS.TI_CUENTA;
    MI_REFERENCIA              PCK_SUBTIPOS.TI_REFERENCIA;
    MI_FUENTE_RECURSO          PCK_SUBTIPOS.TI_FUENTE_RECURSO;
    -- FIN (GPORTILLA:23/02/2023)
    MI_COD_AUXILIAR          PERSONAL.COD_AUXILIAR%TYPE;

    --INI_1886_MPEREZ Secrean parametros para manejo de fondos publicos para cesantias
    MI_CTA_CRD_ADMIN_CESANT_PCA     PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_DBT_ADMIN_CESANT_PCA     PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_CRD_OTRO_CESANT_PCA      PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_DBT_OTRO_CESANT_PCA      PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_CRD_PROD_CESANT_PCA      PCK_SUBTIPOS.TI_PARAMETRO; 
    MI_CTA_DBT_PROD_CESANT_PCA      PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_CRD_PUENTE_CESANT_PCA    PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_DBT_PUENTE_CESANT_PCA    PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_CRD_SUPERN_CESANT_PCA  PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_DBT_SUPERN_CESANT_PCA  PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_CRD_VENTAS_CESANT_PCA    PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CTA_DBT_VENTAS_CESANT_PCA    PCK_SUBTIPOS.TI_PARAMETRO;
    MI_MANEJA_FONDO_PUBLICO_CES      PCK_SUBTIPOS.TI_PARAMETRO;

BEGIN

    MI_CTA_CRD_ADMIN_CESANT_PCA     := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_CRD_ADMINISTRACION_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_DBT_ADMIN_CESANT_PCA     := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_DBT_ADMINISTRACION_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_CRD_OTRO_CESANT_PCA      := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_CRD_OTRO_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_DBT_OTRO_CESANT_PCA      := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_DBT_OTRO_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_CRD_PROD_CESANT_PCA      := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_CRD_PRODUCCION_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_DBT_PROD_CESANT_PCA      := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_DBT_PRODUCCION_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_CRD_PUENTE_CESANT_PCA    := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_CRD_PUENTE_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_DBT_PUENTE_CESANT_PCA    := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_DBT_PUENTE_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_CRD_SUPERN_CESANT_PCA  := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_CRD_SUPERNUM_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_DBT_SUPERN_CESANT_PCA  := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_DBT_SUPERNUM_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_CRD_VENTAS_CESANT_PCA    := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_CRD_VENTAS_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_CTA_DBT_VENTAS_CESANT_PCA    := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'CTA_DBT_VENTAS_CESANTIAS_PUBLICA', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), ' ');
    MI_MANEJA_FONDO_PUBLICO_CES      := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'MANEJA FONDO PUBLICO PARA CESANTIAS', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), 'NO');

    /*IF FORMS!INTERFACE_NOMINA!OPCION = 2 AND TIPO <> 'CDC' THEN
        MSGBOX 'REVIZAR EL TIPO DE COMPROBANTE DE INTERFASE PROVISIONES SI ES COM O CDC', VBCRITICAL, 'SYSMAN SOFTWARE'
    END IF;*/


    MI_NUMERO := RPAD(UN_NUMEROCOMPROBANTE, 10);
    BEGIN
        MI_CONDICIONACME := 'COMPANIA = ''' || UN_COMPANIA || ''' ';
        MI_FILAS := PCK_DATOS.FC_ACME
            (UN_TABLA     => 'ERRORES'
            ,UN_ACCION    => 'E'
            ,UN_CONDICION => MI_CONDICIONACME);
    END;


    BEGIN
        SELECT COUNT(0)
        INTO   MI_CUENTAREGISTRO
        FROM   PERIODOS
        WHERE  COMPANIA = UN_COMPANIA
          AND  ID_DE_PROCESO =UN_PROCESO
          AND  ANO = UN_ANO
          AND  MES = UN_MES
          AND  PERIODO = UN_PERIODO;

        IF MI_CUENTAREGISTRO = 0 THEN
            --MSGBOX 'PERIODO NO DEFINIDO EN NÓMINA.', VBOKONLY + VBINFORMATION
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
        --No existe el periodo --PERIODO--, Año: ---ANO-- En nómina.
        MI_REEMPLAZOS(1).CLAVE := 'PERIODO';
        MI_REEMPLAZOS(1).VALOR := UN_PERIODO;
        MI_REEMPLAZOS(2).CLAVE := 'ANO';
        MI_REEMPLAZOS(2).VALOR := UN_ANO;

        PCK_ERR_MSG.RAISE_WITH_MSG
                  ( UN_EXC_COD => SQLCODE
                  , UN_TABLAERROR => 'PERIODOS'
                  , UN_ERROR_COD  => PCK_ERRORES.ERRR_NOPERIODONOMINA
                  , UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    MI_NUMERO := RPAD(UN_NUMEROCOMPROBANTE, 10);
    
    FOR MI_RS IN
        (SELECT CASE WHEN UN_AUXTERCERO <> 0 THEN P.NUMERO_DCTO ELSE '' END NUMERO_DCTO,
            MAX(H.ID_DE_CONCEPTO)  ID_DE_CONCEPTO,
            MAX(CN.NOMBRE_CONCEPTO)  NOMBRE_CONCEPTO, P.GRUPOCONTABLE  GRUPOCONTABLE,
            SUM(H.VALOR) SUMADEVALOR, CN.TIPODEFONDO  TIPODEFONDO, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION,
            CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS,
            CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO,
            CN.SUCURSAL,CN.TERCERO,P.ID_CENTRO_DE_COSTO, P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR,MAX(CN.NOMBRE_CONCEPTO)  NOMBREFONDO
            ,CN.NITDE, V_FONDO_DE_CESANTIAS.PUBLICO
        FROM (HISTORICOS H LEFT JOIN CONCEPTOS CN
               ON (H.COMPANIA = CN.COMPANIA)
              AND (H.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO))
            LEFT JOIN PERSONAL P
               ON (H.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO)
              AND (H.COMPANIA = P.COMPANIA)
            LEFT JOIN V_FONDO_DE_CESANTIAS  --CES
               ON P.COMPANIA = V_FONDO_DE_CESANTIAS.COMPANIA
              AND P.FONDO_CESANTIAS = V_FONDO_DE_CESANTIAS.FONDO_CESANTIAS
        INNER JOIN PERIODOS 
                    ON H.COMPANIA = PERIODOS.COMPANIA
                    AND H.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                    AND H.ANO = PERIODOS.ANO
                    AND H.MES = PERIODOS.MES
                    AND H.PERIODO = PERIODOS.PERIODO                  
        WHERE H.COMPANIA = UN_COMPANIA
          AND H.ID_DE_PROCESO = UN_PROCESO
          AND H.ANO = UN_ANO
          AND H.MES = UN_MES
          AND H.PERIODO = UN_PERIODO
          AND CN.CLASE = 8
          AND H.ID_DE_CONCEPTO BETWEEN 490 AND 499
      AND PERIODOS.ACUMULADO NOT IN(0)                  
        GROUP BY CASE WHEN UN_AUXTERCERO <> 0 THEN P.NUMERO_DCTO ELSE '' END ,CASE WHEN UN_AUXTERCERO <> 0 THEN P.ID_DE_EMPLEADO ELSE 0 END,
          P.GRUPOCONTABLE, CN.TIPODEFONDO, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO,  CN.NOMBRE_CONCEPTO, H.COMPANIA, H.ID_DE_PROCESO
            , H.ANO, H.MES, H.PERIODO
            ,CN.SUCURSAL,CN.TERCERO,P.ID_CENTRO_DE_COSTO, P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR,CN.NITDE --JM 09/12/2024 agregar el ,CN.NITDE
            , V_FONDO_DE_CESANTIAS.PUBLICO --MPEREZ 1886 Se agrega el V_FONDO_DE_CESANTIAS.PUBLICO
        HAVING SUM(H.VALOR) <>0
        ORDER BY MAX(H.ID_DE_CONCEPTO), P.GRUPOCONTABLE, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, H.ID_DE_PROCESO, H.ANO, H.MES, H.PERIODO
        )
    LOOP
        --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTO. ' || MI_RS.NOMBREFONDO || ' ' || FORMAT(RS.PERCENTPOSITION, '000.00') || ' %'
        MI_EXISTEDATOS := TRUE;
        MI_CUENTADEBITO := RPAD(' ', 16);
        MI_CUENTACREDITO := RPAD(' ', 16);
        MI_VALORD := 0;
        MI_VALORC := 0;
        MI_SUCURSAL := '';
        MI_TERCERO := RPAD(' ', 11);
        MI_CENTROCOSTO := '          ';
        
        -- (GPORTILLA:23/02/2023)
        MI_CUENTA := '';
        MI_REFERENCIA := '';
        MI_FUENTE_RECURSO := '';
        -- FIN (GPORTILLA:23/02/2023)
        
        --INI_1886_MPEREZ Se implementa manejo de fondo publico de casantias por grupos 
        IF(MI_MANEJA_FONDO_PUBLICO_CES = 'SI' AND MI_RS.ID_DE_CONCEPTO = 499 AND MI_RS.PUBLICO <> 0)THEN
            IF MI_RS.GRUPOCONTABLE = 'A' THEN
              MI_CUENTADEBITO := NVL(RPAD(MI_CTA_DBT_ADMIN_CESANT_PCA, 16), RPAD(' ', 16));
              MI_CUENTACREDITO := NVL(RPAD(MI_CTA_CRD_ADMIN_CESANT_PCA, 16), RPAD(' ', 16));
            ELSIF MI_RS.GRUPOCONTABLE = 'V' THEN
              MI_CUENTADEBITO := NVL(RPAD(MI_CTA_DBT_VENTAS_CESANT_PCA, 16), RPAD(' ', 16));
              MI_CUENTACREDITO := NVL(RPAD(MI_CTA_CRD_VENTAS_CESANT_PCA, 16), RPAD(' ', 16));
            ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN
              MI_CUENTADEBITO := NVL(RPAD(MI_CTA_DBT_PROD_CESANT_PCA, 16), RPAD(' ', 16));
              MI_CUENTACREDITO := NVL(RPAD(MI_CTA_CRD_PROD_CESANT_PCA, 16), RPAD(' ', 16));
            ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN
              MI_CUENTADEBITO := NVL(RPAD(MI_CTA_DBT_OTRO_CESANT_PCA, 16), RPAD(' ', 16));
              MI_CUENTACREDITO := NVL(RPAD(MI_CTA_CRD_OTRO_CESANT_PCA, 16), RPAD(' ', 16));
            ELSIF MI_RS.GRUPOCONTABLE = 'S' THEN
              MI_CUENTADEBITO := NVL(RPAD(MI_CTA_DBT_SUPERN_CESANT_PCA, 16), RPAD(' ', 16));
              MI_CUENTACREDITO := NVL(RPAD(MI_CTA_CRD_SUPERN_CESANT_PCA, 16), RPAD(' ', 16));
            ELSE
              MI_CUENTADEBITO := NVL(RPAD(MI_CTA_DBT_PUENTE_CESANT_PCA, 16), RPAD(' ', 16));
              MI_CUENTACREDITO := NVL(RPAD(MI_CTA_CRD_PUENTE_CESANT_PCA, 16), RPAD(' ', 16));
            END IF;
        ELSE
            IF MI_RS.GRUPOCONTABLE = 'V' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_VENTAS, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_VENTAS, 16), RPAD(' ', 16));
            ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_PRODUCCION, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_PRODUCCION, 16), RPAD(' ', 16));
            ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_OTRO, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_OTRO, 16), RPAD(' ', 16));
            ELSE
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
            END IF;
        END IF;
        IF NVL(TRIM(MI_CUENTADEBITO), ' ') <> ' ' THEN
            MI_VALORD := MI_RS.SUMADEVALOR;
        END IF;
        IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' ' THEN
            MI_VALORC := MI_RS.SUMADEVALOR;
        END IF;
        
        -- (GPORTILLA:23/02/2023)el campo cuenta se envia en espacios vacios ya que au no esta definido de donde se debe tomar el dato
        MI_CUENTA := RPAD(' ', 16);
        
        --<TICKET: CC_436 AUTOR:CP FECHA:04/12/2024
        MI_COD_AUXILIAR := RPAD(' ', 16);
        MI_COD_AUXILIAR := NVL(MI_RS.COD_AUXILIAR,'');
        IF MI_COD_AUXILIAR =  '' THEN
            MI_COD_AUXILIAR := '9999999999999999';
        END IF;
        MI_COD_AUXILIAR := NVL(RPAD(MI_COD_AUXILIAR, 16), '9999999999999999');
        
        MI_REFERENCIA := NVL(MI_RS.REFERENCIA,'');
        IF MI_REFERENCIA =  '' THEN
            MI_REFERENCIA := '99999999999999999999';
        END IF;
        MI_REFERENCIA := NVL(RPAD(MI_REFERENCIA, 20),'99999999999999999999');
        
        MI_FUENTE_RECURSO := NVL(MI_RS.FUENTE_DE_RECURSO,'');
        IF MI_FUENTE_RECURSO =  '' THEN
            MI_FUENTE_RECURSO := '99999999999999999999';
        END IF;
        MI_FUENTE_RECURSO := NVL(RPAD(MI_FUENTE_RECURSO, 20),'99999999999999999999');
        --<TICKET/>
        -- FIN (GPORTILLA:23/02/2023)
        
        IF NOT(MI_VALORD + MI_VALORC = 0) THEN
            --ALERTA 'EL CONCEPTO: ' || MI_RS.ID_DE_CONCEPTO || ' ' || MI_RS.NOMBRE_CONCEPTO || ', NO TIENE DEFINIDAS CUENTAS PARA EL GRUPO CONTABLE:  ' || MI_RS.GRUPOCONTABLE
            --GOTO CONTINUAR33

            -- (GPORTILLA:23/02/2023) Se modifica para que tenga en cuenta el numero de documento del empleado cuando UN_AUXTERCERO <> 0
            IF MI_RS.TERCERO IS NOT NULL AND (MI_RS.NITDE = 'O' OR MI_RS.NITDE = 'N') THEN
                MI_TERCERO := MI_RS.TERCERO;
            ELSE
                
                IF MI_RS.NITDE = 'E' AND UN_AUXTERCERO <> 0 THEN
                    MI_TERCERO := MI_RS.NUMERO_DCTO;
                ELSE
                    MI_TERCERO := MI_RS.TERCERO;
                END IF;
                
            END IF;
            -- FIN (GPORTILLA:23/02/2023)
            
            MI_TERCERO := REPLACE(MI_TERCERO, '-', '');
            MI_TERCERO := REPLACE(MI_TERCERO, '.', '');

            MI_TERCERO := NVL(RPAD(MI_TERCERO, 11), '99999999999');
            IF MI_TERCERO = '99999999999' THEN
                MI_SUCURSAL := '999';
            ELSIF MI_TERCERO IS NULL OR MI_TERCERO = '           ' THEN
                MI_TERCERO := '99999999999';
                MI_SUCURSAL := '999';
            ELSIF NVL(MI_RS.SUCURSAL, ' ') <> ' ' THEN
                MI_SUCURSAL := MI_RS.SUCURSAL;
            ELSE
                --MI_VALORC := '001';
                MI_SUCURSAL := '001';
            END IF;
            
            -- (GPORTILLA:23/02/2023) Se adiciona condicionales para que la sucursal sea 001 cuando sea por empleado
            IF MI_RS.NITDE = 'E' AND UN_AUXTERCERO <> 0 THEN
                MI_SUCURSAL := '001';
            ELSE
                MI_SUCURSAL := MI_RS.SUCURSAL;
            END IF;
            -- FIN (GPORTILLA:23/02/2023)

            MI_NOMBRE := 'TRANSFERENCIAS ' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MES) || ' DE ' || UN_ANO || ' ' || PCK_NOMINA_COM5.FC_QUITAESPECIALES(MI_RS.NOMBREFONDO) || ' ' || MI_RS.ID_DE_CONCEPTO;
            MI_CENTROCOSTO := NVL(RPAD(MI_RS.ID_CENTRO_DE_COSTO, 10), '9999999999');
            IF UN_MANEJACENTROCOSTO = 0 THEN
                MI_CENTROCOSTO := '9999999999';
            END IF;
            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(MI_CUENTADEBITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || MI_COD_AUXILIAR || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || MI_COD_AUXILIAR || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );

        END IF; --CONTINUAR33:

    END LOOP;

    IF NOT MI_EXISTEDATOS THEN
        MI_RTAPLANO := 'No existen datos';
    END IF;

    RETURN MI_RTAPLANO;
END FC_PLANOCONTABILIZAPROVICIONES;

FUNCTION FC_PLANOCONTASYSMANCONTABLECC(
/*
    NAME              : FC_PLANOCONTASYSMANCONTABLECC
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÃ‘A HURTADO
    DATE MIGRADOR     : 26/06/2018
    TIME              : 12:00 PM
    SOURCE MODULE     : NOMINAP2018.06.05, FunciÃ³n InterfacesysmanContableCC
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    PARAMETERS        :

    MODIFICATIONS     :

    @NAME:planoContabilizarsysmancc
    @METHOD:  GET
*/
     UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_PROCESO             IN PCK_SUBTIPOS.TI_ID_DE_PROCESO
    ,UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO
    ,UN_MES               IN PCK_SUBTIPOS.TI_MES
    ,UN_PERIODO             IN  PCK_SUBTIPOS.TI_PERIODO_NOMI
    ,UN_FECHAINTER          IN DATE
    ,UN_TIPOCOMPROBANTE     IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE DEFAULT 'NOM'
    ,UN_NUMEROCOMPROBANTE   IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT
    ,UN_AUXTERCERO          IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0         --FORMS!INTERFACE_NOMINA!PRVAUXTER
    ,UN_MANEJACENTROCOSTO   IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0         --FORMS!INTERFACE_NOMINA!MANEJACENTROS
    ,UN_PORCONTRIBUCION     IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0          --FORMS!INTERFACE_NOMINA!PORC
    ,UN_CUENTADBT           IN PCK_SUBTIPOS.TI_CODIGOCONTA DEFAULT ''              --FORMS!INTERFACE_NOMINA!DBT
    ,UN_PORPERIODO          IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0
    ,UN_CUENTACRD           IN PCK_SUBTIPOS.TI_CODIGOCONTA DEFAULT ''              --FORMS!INTERFACE_NOMINA!CRD
    ,UN_UNICOCOMPROBANTE    IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0 -- (GPORTILLA:23/02/2023) Se adiciona campo con el fin de controlar si se deben mostrar uno o mas consecutivos en el plano
) RETURN CLOB
AS
    MI_RS                      SYS_REFCURSOR;
    MI_RS1                     SYS_REFCURSOR;
    MI_RTAPLANO                CLOB;
    MI_CONDICIONACME           PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS                  PCK_SUBTIPOS.TI_CAMPOS;
    MI_FILAS                   PCK_SUBTIPOS.TI_ENTERO;
    MI_NUMERO                  PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
    MI_NUMEROFIJO              PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;-- (GPORTILLA:23/02/2023)
    MI_VALP                    PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALORA                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTADEBITO            PCK_SUBTIPOS.TI_CODIGOCONTA;
    MI_CUENTACREDITO           PCK_SUBTIPOS.TI_CODIGOCONTA;
    MI_VALORD                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALORC                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALORCCF                PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;-- (GPORTILLA:23/02/2023)
    MI_SUCURSAL                PCK_SUBTIPOS.TI_SUCURSAL;
    MI_SUCURSAL_DEBITO         PCK_SUBTIPOS.TI_SUCURSAL;
    MI_TERCERO                 PCK_SUBTIPOS.TI_TERCERO;
    MI_TERCERO_DEBITO          PCK_SUBTIPOS.TI_TERCERO;-- (GPORTILLA:12/04/2023)
    MI_CENTROCOSTO             PCK_SUBTIPOS.TI_CENTRO_COSTO; 
    MI_NOMBRE                  VARCHAR2(250 CHAR);
    MI_CUENTAREGISTRO          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_REEMPLAZOS              PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_PLANOAUX                NUMBER:=0;
   
    -- (GPORTILLA:23/02/2023)
    MI_CUENTA                  PCK_SUBTIPOS.TI_CUENTA;
    MI_REFERENCIA              PCK_SUBTIPOS.TI_REFERENCIA;
    MI_FUENTE_RECURSO          PCK_SUBTIPOS.TI_FUENTE_RECURSO;
    MI_CONCEPTO                PCK_SUBTIPOS.TI_ID_DE_CONCEPTO;
    -- FIN (GPORTILLA:23/02/2023)

    MI_COD_AUXILIAR          PERSONAL.COD_AUXILIAR%TYPE;

BEGIN

    /*IF FORMS!INTERFACE_NOMINA!OPCION = 2 AND TIPO = 'NOM' THEN
        MI_RTAPLANO 'REVIZAR EL TIPO DE COMPROBANTE DE CONTABILIZAR PATRONAL SI ES COM O NOM';
        RETURN MI_RTAPLANO;
    END IF;*/

    MI_NUMERO := RPAD(UN_NUMEROCOMPROBANTE - 1, 10);
    MI_NUMEROFIJO := RPAD(UN_NUMEROCOMPROBANTE, 10);-- (GPORTILLA:23/02/2023)

    MI_PLANOAUX := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'GENERAR CUENTA PUENTE FONDOS DE SALUD Y PENSION',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE),'NO') = 'SI' THEN 1 ELSE 0 END;
    BEGIN
        MI_CONDICIONACME := 'COMPANIA = ''' || UN_COMPANIA || ''' ';
        MI_FILAS := PCK_DATOS.FC_ACME
            (UN_TABLA     => 'ERRORES'
            ,UN_ACCION    => 'E'
            ,UN_CONDICION => MI_CONDICIONACME);
    END;

    BEGIN
        SELECT COUNT(0)
        INTO   MI_CUENTAREGISTRO
        FROM   PERIODOS
        WHERE  COMPANIA = UN_COMPANIA
          AND  ID_DE_PROCESO =UN_PROCESO
          AND  ANO = UN_ANO
          AND  MES = UN_MES
          AND  PERIODO = UN_PERIODO;

        IF MI_CUENTAREGISTRO = 0 THEN
            --MSGBOX 'PERIODO NO DEFINIDO EN NOMINA.', VBOKONLY + VBINFORMATION
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
        --No existe el periodo --PERIODO--, Año: ---ANO-- En nomina.
        MI_REEMPLAZOS(1).CLAVE := 'PERIODO';
        MI_REEMPLAZOS(1).VALOR := UN_PERIODO;
        MI_REEMPLAZOS(2).CLAVE := 'ANO';
        MI_REEMPLAZOS(2).VALOR := UN_ANO;

        PCK_ERR_MSG.RAISE_WITH_MSG
                  ( UN_EXC_COD => SQLCODE
                  , UN_TABLAERROR => 'PERIODOS'
                  , UN_ERROR_COD  => PCK_ERRORES.ERRR_NOPERIODONOMINA
                  , UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    <<FONDOSNITAGRUPADOS>>
    FOR MI_RS1 IN
        (SELECT NITDE, TIPODEFONDO, NIT, CONCEPTO FROM( SELECT CN.NITDE, CN.TIPODEFONDO ,
        CASE CN.TIPODEFONDO
                WHEN 'EPS' THEN V_FONDO_DE_SALUD.NIT
                WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NIT
                WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NIT
                WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NIT
                WHEN 'CCF' THEN V_CAJA_COMPENSACION.NIT
                WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NOMBRE_MEDICINA
                WHEN 'NIN' THEN CN.TERCERO
            ELSE ''
            END NIT
        ,CASE WHEN CN.TIPODEFONDO = 'NIN' AND CN.NITDE = 'O' THEN HIS.ID_DE_CONCEPTO ELSE 0 END CONCEPTO
        FROM HISTORICOS HIS INNER JOIN CONCEPTOS CN
                ON HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
                AND HIS.COMPANIA = CN.COMPANIA
                INNER JOIN PERSONAL P
                ON HIS.COMPANIA = P.COMPANIA
                AND HIS.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
                LEFT JOIN V_FONDO_DE_SALUD     --EPS
                ON P.COMPANIA = V_FONDO_DE_SALUD.COMPANIA
                AND P.FONDO_SALUD = V_FONDO_DE_SALUD.FONDO_SALUD
                LEFT JOIN V_FONDO_DE_PENSIONES    --AFP
                ON P.COMPANIA = V_FONDO_DE_PENSIONES.COMPANIA
                AND P.ID_DEL_FONDO = V_FONDO_DE_PENSIONES.ID_DEL_FONDO
                LEFT JOIN V_FONDO_DE_RIESGOS      --ARL
                ON  P.COMPANIA = V_FONDO_DE_RIESGOS.COMPANIA
                AND P.FONDO_RIESGOS = V_FONDO_DE_RIESGOS.FONDO_RIESGOS
                LEFT JOIN V_FONDO_DE_CESANTIAS  --CES
                ON P.COMPANIA = V_FONDO_DE_CESANTIAS.COMPANIA
                AND P.FONDO_CESANTIAS = V_FONDO_DE_CESANTIAS.FONDO_CESANTIAS
                LEFT JOIN V_CAJA_COMPENSACION     --CCF
                ON P.COMPANIA = V_CAJA_COMPENSACION.COMPANIA
                AND P.CAJA_COMPENSACION = V_CAJA_COMPENSACION.CAJA_COMPENSACION
                LEFT JOIN V_MEDICINA_PREPAGADA  --FMP
                ON P.COMPANIA = V_MEDICINA_PREPAGADA.COMPANIA
                AND P.MEDICINA_PREPAGADA = V_MEDICINA_PREPAGADA.MEDICINA_PREPAGADA
        INNER JOIN PERIODOS 
                ON HIS.COMPANIA = PERIODOS.COMPANIA
                AND HIS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                AND HIS.ANO = PERIODOS.ANO
                AND HIS.MES = PERIODOS.MES
                AND HIS.PERIODO = PERIODOS.PERIODO                
        WHERE HIS.COMPANIA = UN_COMPANIA
          AND HIS.ID_DE_PROCESO = UN_PROCESO
          AND HIS.ANO = UN_ANO
          AND HIS.MES = UN_MES
          AND (   (1 = CASE WHEN UN_PORPERIODO = 0 THEN 1 ELSE 0 END )          --(MZANGUNA:26/12/2018)-Se adiciona condicion por periodo.
               OR (1 = CASE WHEN UN_PORPERIODO <> 0 THEN 1 ELSE 0 END AND HIS.PERIODO = UN_PERIODO)
              )
          AND CN.CLASE = 8
          AND CN.NITDE NOT IN ('E')
          AND CN.ID_DE_CONCEPTO NOT IN (490,491,492,493,494,495,496,497,498,499)
          AND (   (1 = CASE WHEN CN.TIPODEFONDO = 'EPS' THEN 1 ELSE 0 END AND (CN.ID_DE_CONCEPTO IN (116,119) OR 1=CASE WHEN MI_PLANOAUX<>0 AND CN.ID_DE_CONCEPTO IN (113,396,398,399) THEN 1 ELSE 0 END ))
                OR(1 = CASE WHEN CN.TIPODEFONDO = 'ARL' THEN 1 ELSE 0 END )
                OR(1 = CASE WHEN CN.TIPODEFONDO = 'AFP' THEN 1 ELSE 0 END )
                OR(1 = CASE WHEN CN.TIPODEFONDO = 'CCF' THEN 1 ELSE 0 END )
                OR(1 = CASE WHEN CN.TIPODEFONDO = 'CES' THEN 1 ELSE 0 END )
                OR(1 = CASE WHEN CN.TIPODEFONDO = 'FMP' THEN 1 ELSE 0 END )
                OR(1 = CASE WHEN CN.TIPODEFONDO  = 'NIN' THEN 1 ELSE 0 END )
                 )
      AND PERIODOS.ACUMULADO NOT IN(0)                              
        GROUP BY  CN.NITDE, CN.TIPODEFONDO ,
        CASE CN.TIPODEFONDO
                WHEN 'EPS' THEN V_FONDO_DE_SALUD.NIT
                WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NIT
                WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NIT
                WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NIT
                WHEN 'CCF' THEN V_CAJA_COMPENSACION.NIT
                WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NOMBRE_MEDICINA
                WHEN 'NIN' THEN CN.TERCERO
            ELSE ''
            END
        ,CASE WHEN CN.TIPODEFONDO = 'NIN' AND CN.NITDE = 'O' THEN HIS.ID_DE_CONCEPTO ELSE 0 END
        HAVING SUM(HIS.VALOR) <>0
        
        UNION ALL
        SELECT CN.NITDE, CN.TIPODEFONDO 
              ,CN.TERCERO  NIT   
              ,CASE WHEN CN.TIPODEFONDO = 'NIN' AND CN.NITDE = 'O' THEN HIS.ID_DE_CONCEPTO ELSE 0 END CONCEPTO
        FROM HISTORICOS HIS INNER JOIN CONCEPTOS CN
                ON HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
                AND HIS.COMPANIA = CN.COMPANIA
                INNER JOIN PERSONAL P
                ON HIS.COMPANIA = P.COMPANIA
                AND HIS.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
              
        INNER JOIN PERIODOS 
                ON HIS.COMPANIA = PERIODOS.COMPANIA
                AND HIS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                AND HIS.ANO = PERIODOS.ANO
                AND HIS.MES = PERIODOS.MES
                AND HIS.PERIODO = PERIODOS.PERIODO                
        WHERE HIS.COMPANIA = UN_COMPANIA
          AND HIS.ID_DE_PROCESO = UN_PROCESO
          AND HIS.ANO = UN_ANO
          AND HIS.MES = UN_MES
         AND (   (1 = CASE WHEN UN_PORPERIODO = 0 THEN 1 ELSE 0 END )          --(MZANGUNA:26/12/2018)-Se adiciona condicion por periodo.
               OR (1 = CASE WHEN UN_PORPERIODO <> 0 THEN 1 ELSE 0 END AND HIS.PERIODO = UN_PERIODO)
              )
          AND CN.CLASE = 8
          AND CN.TIPODEFONDO = 'NIN' 
          AND CN.NITDE = 'O'
          AND CN.ID_DE_CONCEPTO NOT IN (490,491,492,493,494,495,496,497,498,499)
         
          AND PERIODOS.ACUMULADO NOT IN(0)                              
        GROUP BY  CN.NITDE, CN.TIPODEFONDO 
              ,CN.TERCERO     
              ,CASE WHEN CN.TIPODEFONDO = 'NIN' AND CN.NITDE = 'O' THEN HIS.ID_DE_CONCEPTO ELSE 0 END 
        
        HAVING SUM(HIS.VALOR) <>0
        ORDER BY TIPODEFONDO ) GROUP BY NITDE, TIPODEFONDO, NIT, CONCEPTO ORDER BY TIPODEFONDO --JM 09/12/2024 ajuste consulta CC_436NOMINA
        )
    LOOP

        MI_VALP := 0;
        -- (GPORTILLA:23/02/2023)        
        MI_VALORC := 0;     
        MI_VALORCCF := 0;
        MI_CONCEPTO := 0;
        IF UN_UNICOCOMPROBANTE <> 0 THEN
            MI_NUMERO := MI_NUMEROFIJO;
        ELSE
            MI_NUMERO := (MI_NUMERO + 1);
        END IF;

        MI_CUENTA := '';
        MI_REFERENCIA := '';
        MI_FUENTE_RECURSO := '';
        -- FIN (GPORTILLA:23/02/2023)

        <<FONDOSPORNIT>>
        IF UN_MANEJACENTROCOSTO = 0 THEN -- (GPORTILLA:23/02/2023) Se agrega condicional para que genere el plano teniendo en cuenta el indicador de centro de costo
             FOR MI_RS IN
                (SELECT SUM(HIS.VALOR) AS SUMADEVALOR,
                        MAX(CN.TIPODEFONDO) AS TIPODEFONDO,
                        P.GRUPOCONTABLE AS GRUPOCONTABLE,
                        CN.CTA_DBT_ADMINISTRACION AS CTA_DBT_ADMINISTRACION,
                        CN.CTA_CRD_ADMINISTRACION AS CTA_CRD_ADMINISTRACION,
                        CN.CTA_DBT_PRODUCCION AS CTA_DBT_PRODUCCION,
                        CN.CTA_CRD_PRODUCCION AS CTA_CRD_PRODUCCION,
                        CN.CTA_DBT_VENTAS AS CTA_DBT_VENTAS,
                        CN.CTA_CRD_VENTAS AS CTA_CRD_VENTAS,
                        CN.CTA_DBT_OTRO AS CTA_DBT_OTRO,
                        CN.CTA_CRD_OTRO AS CTA_CRD_OTRO,
                        MAX(HIS.ID_DE_CONCEPTO) AS ID_DE_CONCEPTO,
                        MAX(CN.NOMBRE_CONCEPTO) AS NOMBRE_CONCEPTO,
                        CN.NITDE,
                        CASE CN.TIPODEFONDO
                        WHEN 'EPS' THEN V_FONDO_DE_SALUD.NIT
                        WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NIT
                        WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NIT
                        WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NIT
                        WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NIT
                        WHEN 'NIN' THEN CN.TERCERO
                    ELSE ''
                    END NIT,
                    CASE CN.TIPODEFONDO
                    WHEN 'EPS' THEN V_FONDO_DE_SALUD.NOMBRE_FONDO_SALUD
                    WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NOMBRE_DEL_FONDO
                    WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NOMBRE_FONDO_RIESGOS
                    WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NOMBRE_FONDO_CESANTIAS
                    WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NOMBRE_MEDICINA
                    ELSE ''
                    END NOMBREFONDO,
                    CASE CN.TIPODEFONDO
                    WHEN 'EPS' THEN V_FONDO_DE_SALUD.PUBLICO
                    WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.PUBLICO
                    WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.PUBLICO
                    WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.PUBLICO
                    WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.PUBLICO
                    ELSE 0
                    END PUBLICO,
                    CN.SUCURSAL,CN.TERCERO,P.ID_CENTRO_DE_COSTO, P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR
                FROM HISTORICOS HIS INNER JOIN CONCEPTOS CN
                         ON HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
                        AND HIS.COMPANIA = CN.COMPANIA
                    INNER JOIN PERSONAL P
                         ON HIS.COMPANIA = P.COMPANIA
                        AND HIS.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
                    LEFT JOIN V_FONDO_DE_SALUD     --EPS
                         ON P.COMPANIA = V_FONDO_DE_SALUD.COMPANIA
                        AND P.FONDO_SALUD = V_FONDO_DE_SALUD.FONDO_SALUD
                    LEFT JOIN V_FONDO_DE_PENSIONES    --AFP
                         ON P.COMPANIA = V_FONDO_DE_PENSIONES.COMPANIA
                        AND P.ID_DEL_FONDO = V_FONDO_DE_PENSIONES.ID_DEL_FONDO
                    LEFT JOIN V_FONDO_DE_RIESGOS      --ARL
                         ON  P.COMPANIA = V_FONDO_DE_RIESGOS.COMPANIA
                        AND P.FONDO_RIESGOS = V_FONDO_DE_RIESGOS.FONDO_RIESGOS
                    LEFT JOIN V_FONDO_DE_CESANTIAS  --CES
                         ON P.COMPANIA = V_FONDO_DE_CESANTIAS.COMPANIA
                        AND P.FONDO_CESANTIAS = V_FONDO_DE_CESANTIAS.FONDO_CESANTIAS
                    LEFT JOIN V_MEDICINA_PREPAGADA  --FMP
                         ON P.COMPANIA = V_MEDICINA_PREPAGADA.COMPANIA
                        AND P.MEDICINA_PREPAGADA = V_MEDICINA_PREPAGADA.MEDICINA_PREPAGADA
          INNER JOIN PERIODOS 
                        ON HIS.COMPANIA = PERIODOS.COMPANIA
                        AND HIS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                        AND HIS.ANO = PERIODOS.ANO
                        AND HIS.MES = PERIODOS.MES
                        AND HIS.PERIODO = PERIODOS.PERIODO                    
                WHERE HIS.COMPANIA = UN_COMPANIA
                  AND HIS.ID_DE_PROCESO = UN_PROCESO
                  AND HIS.ANO = UN_ANO
                  AND HIS.MES = UN_MES
                  AND (   (1 = CASE WHEN UN_PORPERIODO = 0 THEN 1 ELSE 0 END )      --(MZANGUNA:26/12/2018)-Se adiciona condiciÃ³n por periodo.
                       OR (1 = CASE WHEN UN_PORPERIODO <> 0 THEN 1 ELSE 0 END AND HIS.PERIODO = UN_PERIODO)
                      )
                  AND CN.CLASE = 8
                  AND CN.NITDE NOT IN ('O','E')
                  AND CN.TIPODEFONDO = MI_RS1.TIPODEFONDO
                  AND (   (1 = CASE WHEN MI_RS1.TIPODEFONDO = 'EPS' THEN 1 ELSE 0 END AND (CN.ID_DE_CONCEPTO IN (116,119) OR 1=CASE WHEN MI_PLANOAUX<>0 AND CN.ID_DE_CONCEPTO IN (113,396,398,399) THEN 1 ELSE 0 END )   AND V_FONDO_DE_SALUD.NIT = MI_RS1.NIT )
                        OR(1 = CASE WHEN MI_RS1.TIPODEFONDO = 'AFP' THEN 1 ELSE 0 END AND V_FONDO_DE_PENSIONES.NIT = MI_RS1.NIT  )
                        OR(1 = CASE WHEN MI_RS1.TIPODEFONDO = 'ARL' THEN 1 ELSE 0 END AND V_FONDO_DE_RIESGOS.NIT = MI_RS1.NIT)
                        OR(1 = CASE WHEN MI_RS1.TIPODEFONDO = 'CES' THEN 1 ELSE 0 END AND V_FONDO_DE_CESANTIAS.NIT = MI_RS1.NIT)
                        OR(1 = CASE WHEN MI_RS1.TIPODEFONDO = 'FMP' THEN 1 ELSE 0 END AND V_MEDICINA_PREPAGADA.NIT = MI_RS1.NIT)
                        OR(1 = CASE WHEN MI_RS1.TIPODEFONDO = 'NIN' THEN 1 ELSE 0 END AND CN.TERCERO  = MI_RS1.NIT )
                         )
          AND PERIODOS.ACUMULADO NOT IN(0)                           
                GROUP BY CASE CN.TIPODEFONDO
                        WHEN 'EPS' THEN V_FONDO_DE_SALUD.NIT
                        WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NIT
                        WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NIT
                        WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NIT
                        WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NIT
                        WHEN 'NIN' THEN CN.TERCERO
                        ELSE ''
                        END ,
                        CASE CN.TIPODEFONDO
                        WHEN 'EPS' THEN V_FONDO_DE_SALUD.NOMBRE_FONDO_SALUD
                        WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NOMBRE_DEL_FONDO
                        WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NOMBRE_FONDO_RIESGOS
                        WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NOMBRE_FONDO_CESANTIAS
                        WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NOMBRE_MEDICINA
                        ELSE ''
                        END,
                        CASE CN.TIPODEFONDO
                        WHEN 'EPS' THEN V_FONDO_DE_SALUD.PUBLICO
                        WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.PUBLICO
                        WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.PUBLICO
                        WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.PUBLICO
                        WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.PUBLICO
                        ELSE 0
                        END
                        ,CN.NITDE,  P.GRUPOCONTABLE, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION,
                        CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO,
                        CN.CTA_DBT_ADMINISTRACION, HIS.COMPANIA, HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES,  CN.TIPODEFONDO
                        ,CN.SUCURSAL,CN.TERCERO,P.ID_CENTRO_DE_COSTO, P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR
                HAVING SUM(HIS.VALOR) <>0
                ORDER BY CN.TIPODEFONDO
                )
            LOOP
                --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS POR FONDOS. || MI_RS.NOMBREFONDO || || FORMAT(RS.PERCENTPOSITION, '000.00') || %'
                MI_CUENTADEBITO := RPAD(' ', 16);
                MI_CUENTACREDITO := RPAD(' ', 16);
                MI_VALORD := 0;
                --MI_VALORC := 0;
                MI_SUCURSAL := '';
                MI_TERCERO := RPAD(' ', 11);
                MI_CENTROCOSTO := '          ';
                MI_CUENTA := '';
                MI_REFERENCIA := '';
                MI_FUENTE_RECURSO := '';

                -- (GPORTILLA:23/02/2023) Se agrega validacion para que si el tipo de fondo es de pension y es publico, las cuentas las tome de los parametros y no del concepto
                IF MI_RS.PUBLICO <> 0 AND MI_RS.TIPODEFONDO = 'AFP' THEN
                    IF MI_RS.GRUPOCONTABLE = 'V' THEN--VENTAS

                        MI_CUENTADEBITO := NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'CTA_DBT_VENTAS_PENSION_PUBLICA',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 16), RPAD(' ', 16));

                        MI_CUENTACREDITO := NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'CTA_CRD_VENTAS_PENSION_PUBLICA',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 16), RPAD(' ', 16));
                    ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN--PRODUCCION

                        MI_CUENTADEBITO := NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'CTA_DBT_PRODUCCION_PENSION_PUBLICA',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 16), RPAD(' ', 16));

                        MI_CUENTACREDITO := NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'CTA_CRD_PRODUCCION_PENSION_PUBLICA',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 16), RPAD(' ', 16));                                                  

                    ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN--OTROS
                        MI_CUENTADEBITO := NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'CTA_DBT_OTRO_PENSION_PUBLICA',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 16), RPAD(' ', 16));

                        MI_CUENTACREDITO := NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'CTA_CRD_OTRO_PENSION_PUBLICA',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 16), RPAD(' ', 16));                                                   

                    ELSE
                        MI_CUENTADEBITO := NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'CTA_DBT_ADMINISTRACION_PENSION_PUBLICA',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 16), RPAD(' ', 16));

                        MI_CUENTACREDITO := NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'CTA_CRD_ADMINISTRACION_PENSION_PUBLICA',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 16), RPAD(' ', 16));  
                    END IF;
                ELSE  
                    IF MI_RS.GRUPOCONTABLE = 'V' THEN
                        MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_VENTAS, 16), RPAD(' ', 16));
                        MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_VENTAS, 16), RPAD(' ', 16));
                    ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN
                        MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_PRODUCCION, 16), RPAD(' ', 16));
                        MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_PRODUCCION, 16), RPAD(' ', 16));
                    ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN
                        MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_OTRO, 16), RPAD(' ', 16));
                        MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_OTRO, 16), RPAD(' ', 16));
                    ELSE
                        MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
                        MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
                    END IF;
                END IF;


                IF NVL(TRIM(MI_CUENTADEBITO), ' ') <> ' ' THEN
                    MI_VALORD := MI_RS.SUMADEVALOR;
                END IF;

                IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' ' THEN
                    MI_VALORC := MI_VALORC + MI_RS.SUMADEVALOR;                                 
                END IF;

                IF NOT(MI_VALORD = 0) THEN
                    --ALERTA 'EL CONCEPTO: || MI_RS.ID_DE_CONCEPTO || || MI_RS.NOMBRE_CONCEPTO ||, NO TIENE DEFINIDAS CUENTAS PARA EL GRUPO CONTABLE:  || MI_RS.GRUPOCONTABLE
                    --GOTO CONTINUAR33

                    IF MI_RS1.TIPODEFONDO IS NULL AND MI_RS1.NITDE = 'O' THEN
                        MI_TERCERO := MI_RS.TERCERO;
                    ELSE
                        MI_TERCERO := MI_RS.NIT;
                    END IF;
                    MI_TERCERO := REPLACE(MI_TERCERO, '-', '');
                    MI_TERCERO := REPLACE(MI_TERCERO, '.', '');

                    MI_TERCERO := NVL(RPAD(MI_TERCERO, 11), '99999999999');
                    IF MI_TERCERO = '99999999999' THEN
                        MI_SUCURSAL := '999';
                    ELSIF MI_TERCERO IS NULL OR MI_TERCERO = '           ' THEN
                        MI_TERCERO := '99999999999';
                        MI_SUCURSAL := '999';
                    ELSIF MI_RS.SUCURSAL <> '' THEN
                        MI_SUCURSAL := MI_RS.SUCURSAL;
                    ELSE
                        MI_SUCURSAL := '001';
                    END IF;

                    -- (GPORTILLA:23/02/2023)el campo cuenta se envia en espacios vacios ya que au no esta definido de donde se debe tomar el dato
                    MI_CUENTA := RPAD(' ', 16);

                   --<TICKET: CC_436 AUTOR:CP FECHA:04/12/2024
                    MI_COD_AUXILIAR := RPAD(' ', 16);
                    MI_COD_AUXILIAR := NVL(MI_RS.COD_AUXILIAR,'');
                    IF MI_COD_AUXILIAR =  '' THEN
                        MI_COD_AUXILIAR := '9999999999999999';
                    END IF;
                    MI_COD_AUXILIAR := NVL(RPAD(MI_COD_AUXILIAR, 16), '9999999999999999');
                    
                    MI_REFERENCIA := NVL(MI_RS.REFERENCIA,'');
                    IF MI_REFERENCIA =  '' THEN
                        MI_REFERENCIA := '99999999999999999999';
                    END IF;
                    MI_REFERENCIA := NVL(RPAD(MI_REFERENCIA, 20),'99999999999999999999');
                    
                    MI_FUENTE_RECURSO := NVL(MI_RS.FUENTE_DE_RECURSO,'');
                    IF MI_FUENTE_RECURSO =  '' THEN
                        MI_FUENTE_RECURSO := '99999999999999999999';
                    END IF;
                    MI_FUENTE_RECURSO := NVL(RPAD(MI_FUENTE_RECURSO, 20),'99999999999999999999');
                    --<TICKET/>
                    -- FIN (GPORTILLA:23/02/2023)

                    MI_NOMBRE := 'TRANSFERENCIAS NOMINA MES DE ' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MES) || ' DE ' || UN_ANO || ' ' || PCK_NOMINA_COM5.FC_QUITAESPECIALES(MI_RS.NOMBREFONDO);

                    MI_CENTROCOSTO := '9999999999';

                    MI_VALP := MI_VALP + MI_VALORD;
                    MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(MI_CUENTADEBITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || MI_COD_AUXILIAR || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
                    --MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || MI_COD_AUXILIAR || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );                 

                END IF; --CONTINUAR33:

            END LOOP FONDOSPORNIT;
        ELSE
             FOR MI_RS IN
                (SELECT SUM(HIS.VALOR) AS SUMADEVALOR,
                        MAX(CN.TIPODEFONDO) AS TIPODEFONDO,
                        P.GRUPOCONTABLE AS GRUPOCONTABLE,
                        CN.CTA_DBT_ADMINISTRACION AS CTA_DBT_ADMINISTRACION,
                        CN.CTA_CRD_ADMINISTRACION AS CTA_CRD_ADMINISTRACION,
                        CN.CTA_DBT_PRODUCCION AS CTA_DBT_PRODUCCION,
                        CN.CTA_CRD_PRODUCCION AS CTA_CRD_PRODUCCION,
                        CN.CTA_DBT_VENTAS AS CTA_DBT_VENTAS,
                        CN.CTA_CRD_VENTAS AS CTA_CRD_VENTAS,
                        CN.CTA_DBT_OTRO AS CTA_DBT_OTRO,
                        CN.CTA_CRD_OTRO AS CTA_CRD_OTRO,
                        MAX(HIS.ID_DE_CONCEPTO) AS ID_DE_CONCEPTO,
                        MAX(CN.NOMBRE_CONCEPTO) AS NOMBRE_CONCEPTO,
                        CN.NITDE,
                        CASE CN.TIPODEFONDO
                        WHEN 'EPS' THEN V_FONDO_DE_SALUD.NIT
                        WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NIT
                        WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NIT
                        WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NIT
                        WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NIT
                        WHEN 'NIN' THEN CN.TERCERO
                    ELSE ''
                    END NIT,
                    CASE CN.TIPODEFONDO
                    WHEN 'EPS' THEN V_FONDO_DE_SALUD.NOMBRE_FONDO_SALUD
                    WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NOMBRE_DEL_FONDO
                    WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NOMBRE_FONDO_RIESGOS
                    WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NOMBRE_FONDO_CESANTIAS
                    WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NOMBRE_MEDICINA
                    ELSE ''
                    END NOMBREFONDO,
                    CASE CN.TIPODEFONDO
                    WHEN 'EPS' THEN V_FONDO_DE_SALUD.PUBLICO
                    WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.PUBLICO
                    WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.PUBLICO
                    WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.PUBLICO
                    WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.PUBLICO
                    ELSE 0
                    END PUBLICO,
                    CN.SUCURSAL,CN.TERCERO,P.ID_CENTRO_DE_COSTO, P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR
                FROM HISTORICOS HIS INNER JOIN CONCEPTOS CN
                         ON HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
                        AND HIS.COMPANIA = CN.COMPANIA
                    INNER JOIN PERSONAL P
                         ON HIS.COMPANIA = P.COMPANIA
                        AND HIS.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
                    LEFT JOIN V_FONDO_DE_SALUD     --EPS
                         ON P.COMPANIA = V_FONDO_DE_SALUD.COMPANIA
                        AND P.FONDO_SALUD = V_FONDO_DE_SALUD.FONDO_SALUD
                    LEFT JOIN V_FONDO_DE_PENSIONES    --AFP
                         ON P.COMPANIA = V_FONDO_DE_PENSIONES.COMPANIA
                        AND P.ID_DEL_FONDO = V_FONDO_DE_PENSIONES.ID_DEL_FONDO
                    LEFT JOIN V_FONDO_DE_RIESGOS      --ARL
                         ON  P.COMPANIA = V_FONDO_DE_RIESGOS.COMPANIA
                        AND P.FONDO_RIESGOS = V_FONDO_DE_RIESGOS.FONDO_RIESGOS
                    LEFT JOIN V_FONDO_DE_CESANTIAS  --CES
                         ON P.COMPANIA = V_FONDO_DE_CESANTIAS.COMPANIA
                        AND P.FONDO_CESANTIAS = V_FONDO_DE_CESANTIAS.FONDO_CESANTIAS
                    LEFT JOIN V_MEDICINA_PREPAGADA  --FMP
                         ON P.COMPANIA = V_MEDICINA_PREPAGADA.COMPANIA
                        AND P.MEDICINA_PREPAGADA = V_MEDICINA_PREPAGADA.MEDICINA_PREPAGADA
          INNER JOIN PERIODOS 
                        ON HIS.COMPANIA = PERIODOS.COMPANIA
                        AND HIS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                        AND HIS.ANO = PERIODOS.ANO
                        AND HIS.MES = PERIODOS.MES
                        AND HIS.PERIODO = PERIODOS.PERIODO                        
                WHERE HIS.COMPANIA = UN_COMPANIA
                  AND HIS.ID_DE_PROCESO = UN_PROCESO
                  AND HIS.ANO = UN_ANO
                  AND HIS.MES = UN_MES
                  AND (   (1 = CASE WHEN UN_PORPERIODO = 0 THEN 1 ELSE 0 END )      --(MZANGUNA:26/12/2018)-Se adiciona condiciÃ³n por periodo.
                       OR (1 = CASE WHEN UN_PORPERIODO <> 0 THEN 1 ELSE 0 END AND HIS.PERIODO = UN_PERIODO)
                      )
                  AND CN.CLASE = 8
                  AND CN.NITDE NOT IN ('E')
                  AND CN.TIPODEFONDO = MI_RS1.TIPODEFONDO
                  AND (   (1 = CASE WHEN MI_RS1.TIPODEFONDO = 'EPS' THEN 1 ELSE 0 END AND (CN.ID_DE_CONCEPTO IN (116,119) OR 1=CASE WHEN MI_PLANOAUX<>0 AND CN.ID_DE_CONCEPTO IN (113,396,398,399) THEN 1 ELSE 0 END )   AND V_FONDO_DE_SALUD.NIT = MI_RS1.NIT )
                        OR(1 = CASE WHEN MI_RS1.TIPODEFONDO = 'AFP' THEN 1 ELSE 0 END AND V_FONDO_DE_PENSIONES.NIT = MI_RS1.NIT  )
                        OR(1 = CASE WHEN MI_RS1.TIPODEFONDO = 'ARL' THEN 1 ELSE 0 END AND V_FONDO_DE_RIESGOS.NIT = MI_RS1.NIT)
                        OR(1 = CASE WHEN MI_RS1.TIPODEFONDO = 'CES' THEN 1 ELSE 0 END AND V_FONDO_DE_CESANTIAS.NIT = MI_RS1.NIT)
                        OR(1 = CASE WHEN MI_RS1.TIPODEFONDO = 'FMP' THEN 1 ELSE 0 END AND V_MEDICINA_PREPAGADA.NIT = MI_RS1.NIT)
                        OR(1 = CASE WHEN MI_RS1.TIPODEFONDO = 'NIN' THEN 1 ELSE 0 END AND CN.TERCERO  = MI_RS1.NIT)
                         )
          AND PERIODOS.ACUMULADO NOT IN(0)                              
                GROUP BY CASE CN.TIPODEFONDO
                        WHEN 'EPS' THEN V_FONDO_DE_SALUD.NIT
                        WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NIT
                        WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NIT
                        WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NIT
                        WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NIT
                        WHEN 'NIN' THEN CN.TERCERO
                        ELSE ''
                        END ,
                        CASE CN.TIPODEFONDO
                        WHEN 'EPS' THEN V_FONDO_DE_SALUD.NOMBRE_FONDO_SALUD
                        WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NOMBRE_DEL_FONDO
                        WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NOMBRE_FONDO_RIESGOS
                        WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NOMBRE_FONDO_CESANTIAS
                        WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.NOMBRE_MEDICINA
                        ELSE ''
                        END,
                        CASE CN.TIPODEFONDO
                        WHEN 'EPS' THEN V_FONDO_DE_SALUD.PUBLICO
                        WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.PUBLICO
                        WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.PUBLICO
                        WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.PUBLICO
                        WHEN 'FMP' THEN V_MEDICINA_PREPAGADA.PUBLICO
                        ELSE 0
                        END
                        ,CN.NITDE,  P.GRUPOCONTABLE, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION,
                        CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO,
                        CN.CTA_DBT_ADMINISTRACION, HIS.COMPANIA, HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES,  CN.TIPODEFONDO
                        ,CN.SUCURSAL,CN.TERCERO,P.ID_CENTRO_DE_COSTO, P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR
                HAVING SUM(HIS.VALOR) <>0
                ORDER BY CN.TIPODEFONDO
                )
            LOOP
                --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS POR FONDOS. || MI_RS.NOMBREFONDO || || FORMAT(RS.PERCENTPOSITION, '000.00') || %'
                MI_CUENTADEBITO := RPAD(' ', 16);
                MI_CUENTACREDITO := RPAD(' ', 16);
                MI_VALORD := 0;
                --MI_VALORC := 0;
                MI_SUCURSAL := '';
                MI_TERCERO := RPAD(' ', 11);
                MI_CENTROCOSTO := '          ';
                MI_CUENTA := '';
                MI_REFERENCIA := '';
                MI_FUENTE_RECURSO := '';

                -- (GPORTILLA:23/02/2023) Se agrega validacion para que si el tipo de fondo es de pension y es publico, las cuentas las tome de los parametros y no del concepto
                IF MI_RS.PUBLICO <> 0 AND MI_RS.TIPODEFONDO = 'AFP' THEN
                    IF MI_RS.GRUPOCONTABLE = 'V' THEN--VENTAS

                        MI_CUENTADEBITO := NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'CTA_DBT_VENTAS_PENSION_PUBLICA',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 16), RPAD(' ', 16));

                        MI_CUENTACREDITO := NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'CTA_CRD_VENTAS_PENSION_PUBLICA',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 16), RPAD(' ', 16));
                    ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN--PRODUCCION

                        MI_CUENTADEBITO := NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'CTA_DBT_PRODUCCION_PENSION_PUBLICA',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 16), RPAD(' ', 16));

                        MI_CUENTACREDITO := NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'CTA_CRD_PRODUCCION_PENSION_PUBLICA',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 16), RPAD(' ', 16));                                                  

                    ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN--OTROS
                        MI_CUENTADEBITO := NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'CTA_DBT_OTRO_PENSION_PUBLICA',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 16), RPAD(' ', 16));

                        MI_CUENTACREDITO := NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'CTA_CRD_OTRO_PENSION_PUBLICA',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 16), RPAD(' ', 16));                                                   

                    ELSE
                        MI_CUENTADEBITO := NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'CTA_DBT_ADMINISTRACION_PENSION_PUBLICA',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 16), RPAD(' ', 16));

                        MI_CUENTACREDITO := NVL(RPAD(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                            UN_NOMBRE    =>  'CTA_CRD_ADMINISTRACION_PENSION_PUBLICA',
                            UN_MODULO    =>  PCK_DATOS.MODULONOMINA,
                            UN_FECHA_PAR =>  SYSDATE), 16), RPAD(' ', 16));  
                    END IF;
                ELSE  
                    IF MI_RS.GRUPOCONTABLE = 'V' THEN
                        MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_VENTAS, 16), RPAD(' ', 16));
                        MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_VENTAS, 16), RPAD(' ', 16));
                    ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN
                        MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_PRODUCCION, 16), RPAD(' ', 16));
                        MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_PRODUCCION, 16), RPAD(' ', 16));
                    ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN
                        MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_OTRO, 16), RPAD(' ', 16));
                        MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_OTRO, 16), RPAD(' ', 16));
                    ELSE
                        MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
                        MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
                    END IF;
                END IF;

                IF NVL(TRIM(MI_CUENTADEBITO), ' ') <> ' ' THEN
                    MI_VALORD := MI_RS.SUMADEVALOR;
                END IF;

                IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' ' THEN
                    MI_VALORC := MI_VALORC + MI_RS.SUMADEVALOR;   
                END IF;

                IF NOT(MI_VALORD = 0) THEN
                    --ALERTA 'EL CONCEPTO: || MI_RS.ID_DE_CONCEPTO || || MI_RS.NOMBRE_CONCEPTO ||, NO TIENE DEFINIDAS CUENTAS PARA EL GRUPO CONTABLE:  || MI_RS.GRUPOCONTABLE
                    --GOTO CONTINUAR33

                    IF MI_RS1.TIPODEFONDO IS NULL AND MI_RS1.NITDE = 'O' THEN
                        MI_TERCERO := MI_RS.TERCERO;
                    ELSE
                        MI_TERCERO := MI_RS.NIT;
                    END IF;
                    MI_TERCERO := REPLACE(MI_TERCERO, '-', '');
                    MI_TERCERO := REPLACE(MI_TERCERO, '.', '');

                    MI_TERCERO := NVL(RPAD(MI_TERCERO, 11), '99999999999');
                    IF MI_TERCERO = '99999999999' THEN
                        MI_SUCURSAL := '999';
                    ELSIF MI_TERCERO IS NULL OR MI_TERCERO = '           ' THEN
                        MI_TERCERO := '99999999999';
                        MI_SUCURSAL := '999';
                    ELSIF MI_RS.SUCURSAL <> '' THEN
                        MI_SUCURSAL := MI_RS.SUCURSAL;
                    ELSE
                        MI_SUCURSAL := '001';
                    END IF;

                    -- (GPORTILLA:23/02/2023)el campo cuenta se envia en espacios vacios ya que au no esta definido de donde se debe tomar el dato
                    MI_CUENTA := RPAD(' ', 16);

                    --<TICKET: CC_436 AUTOR:CP FECHA:04/12/2024
                    MI_COD_AUXILIAR := RPAD(' ', 16);
                    MI_COD_AUXILIAR := NVL(MI_RS.COD_AUXILIAR,'');
                    IF MI_COD_AUXILIAR =  '' THEN
                        MI_COD_AUXILIAR := '9999999999999999';
                    END IF;
                    MI_COD_AUXILIAR := NVL(RPAD(MI_COD_AUXILIAR, 16), '9999999999999999');
                    
                    MI_REFERENCIA := NVL(MI_RS.REFERENCIA,'');
                    IF MI_REFERENCIA =  '' THEN
                        MI_REFERENCIA := '99999999999999999999';
                    END IF;
                    MI_REFERENCIA := NVL(RPAD(MI_REFERENCIA, 20),'99999999999999999999');
                    
                    MI_FUENTE_RECURSO := NVL(MI_RS.FUENTE_DE_RECURSO,'');
                    IF MI_FUENTE_RECURSO =  '' THEN
                        MI_FUENTE_RECURSO := '99999999999999999999';
                    END IF;
                    MI_FUENTE_RECURSO := NVL(RPAD(MI_FUENTE_RECURSO, 20),'99999999999999999999');
                    --<TICKET/>
                    -- FIN (GPORTILLA:23/02/2023)

                    MI_NOMBRE := 'TRANSFERENCIAS NOMINA MES DE ' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MES) || ' DE ' || UN_ANO || ' ' || PCK_NOMINA_COM5.FC_QUITAESPECIALES(MI_RS.NOMBREFONDO);
                    MI_CENTROCOSTO := NVL(RPAD(MI_RS.ID_CENTRO_DE_COSTO, 10), '9999999999');

                    MI_VALP := MI_VALP + MI_VALORD;
                    MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(MI_CUENTADEBITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || MI_COD_AUXILIAR || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
                    --MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || MI_COD_AUXILIAR || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );

                END IF; --CONTINUAR33:

            END LOOP FONDOSPORNIT;
        END IF;

        <<CCFPORNIT>>                 
        
   
                          
        IF UN_MANEJACENTROCOSTO = 0 THEN -- (GPORTILLA:23/02/2023) Se separa la generacion del plano para las cajas de compensacion familiar, con el fin de que genere linea de valor credito por grupo de concepto

             FOR MI_RS IN (
                   
                    SELECT SUM(HIS.VALOR) AS SUMADEVALOR,
                                            MAX(CN.TIPODEFONDO) AS TIPODEFONDO,
                                             CASE  WHEN  P.GRUPOCONTABLE= 'V' 
                                             THEN  CN.CTA_DBT_VENTAS
                                             ELSE CASE WHEN P.GRUPOCONTABLE= 'P' 
                                             THEN CN.CTA_DBT_PRODUCCION
                                             ELSE CASE WHEN P.GRUPOCONTABLE= 'O' 
                                             THEN  CN.CTA_DBT_OTRO
                                             ELSE CN.CTA_DBT_ADMINISTRACION END END END  AS CTA_DEBITO,
                                            CASE  WHEN  P.GRUPOCONTABLE= 'V' 
                                             THEN  CN.CTA_CRD_VENTAS
                                             ELSE CASE WHEN P.GRUPOCONTABLE= 'P' 
                                             THEN CN.CTA_CRD_PRODUCCION
                                             ELSE CASE WHEN P.GRUPOCONTABLE= 'O' 
                                             THEN  CN.CTA_CRD_OTRO
                                             ELSE CN.CTA_CRD_ADMINISTRACION END END END CTA_CREDITO,                                                 
                                            MAX(HIS.ID_DE_CONCEPTO) AS ID_DE_CONCEPTO,
                                            MAX(CN.NOMBRE_CONCEPTO) AS NOMBRE_CONCEPTO,
                                            CN.NITDE,
                                            CASE CN.TIPODEFONDO
                                            WHEN 'CCF' THEN V_CAJA_COMPENSACION.NIT
                                            ELSE ''
                                            END NIT,
                                            CASE CN.TIPODEFONDO
                                            WHEN 'CCF' THEN V_CAJA_COMPENSACION.NOMBRE_CAJA
                                            ELSE ''
                                            END NOMBREFONDO,
                                            CASE CN.TIPODEFONDO
                                            WHEN 'CCF' THEN V_CAJA_COMPENSACION.PUBLICO
                                            ELSE 0
                                            END PUBLICO
                                            ,CN.SUCURSAL,CN.TERCERO,P.ID_CENTRO_DE_COSTO, P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR
                                    FROM HISTORICOS HIS INNER JOIN CONCEPTOS CN
                                             ON HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
                                            AND HIS.COMPANIA = CN.COMPANIA
                                        INNER JOIN PERSONAL P
                                             ON HIS.COMPANIA = P.COMPANIA
                                            AND HIS.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
                                        LEFT JOIN V_CAJA_COMPENSACION     --CCF
                                             ON P.COMPANIA = V_CAJA_COMPENSACION.COMPANIA
                                            AND P.CAJA_COMPENSACION = V_CAJA_COMPENSACION.CAJA_COMPENSACION
                    INNER JOIN PERIODOS 
                                            ON HIS.COMPANIA = PERIODOS.COMPANIA
                                            AND HIS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                                            AND HIS.ANO = PERIODOS.ANO
                                            AND HIS.MES = PERIODOS.MES
                                            AND HIS.PERIODO = PERIODOS.PERIODO                        
                                    WHERE HIS.COMPANIA = UN_COMPANIA
                                      AND HIS.ID_DE_PROCESO = UN_PROCESO
                                      AND HIS.ANO = UN_ANO
                                      AND HIS.MES = UN_MES
                                      AND (   (1 = CASE WHEN UN_PORPERIODO = 0 THEN 1 ELSE 0 END )      --(MZANGUNA:26/12/2018)-Se adiciona condiciÃ³n por periodo.
                                           OR (1 = CASE WHEN UN_PORPERIODO <> 0 THEN 1 ELSE 0 END AND HIS.PERIODO = UN_PERIODO)
                                          )
                                      AND CN.CLASE = 8
                                      AND CN.NITDE NOT IN ('O','E')
                                      AND CN.TIPODEFONDO = MI_RS1.TIPODEFONDO
                                      AND ((1 = CASE WHEN MI_RS1.TIPODEFONDO = 'CCF' THEN 1 ELSE 0 END AND V_CAJA_COMPENSACION.NIT = MI_RS1.NIT))
                    AND PERIODOS.ACUMULADO NOT IN(0)                  
                                    GROUP BY CASE CN.TIPODEFONDO
                                            WHEN 'CCF' THEN V_CAJA_COMPENSACION.NIT
                                            ELSE ''
                                            END ,
                                            CASE CN.TIPODEFONDO                      
                                            WHEN 'CCF' THEN V_CAJA_COMPENSACION.NOMBRE_CAJA
                                            ELSE ''
                                            END,
                                            CASE CN.TIPODEFONDO                        
                                            WHEN 'CCF' THEN V_CAJA_COMPENSACION.PUBLICO
                                            ELSE 0
                                            END
                                            ,CN.NITDE,    CASE  WHEN  P.GRUPOCONTABLE= 'V' 
                                             THEN  CN.CTA_DBT_VENTAS
                                             ELSE CASE WHEN P.GRUPOCONTABLE= 'P' 
                                             THEN CN.CTA_DBT_PRODUCCION
                                             ELSE CASE WHEN P.GRUPOCONTABLE= 'O' 
                                             THEN  CN.CTA_DBT_OTRO
                                             ELSE CN.CTA_DBT_ADMINISTRACION END END END,
                                            CASE  WHEN  P.GRUPOCONTABLE= 'V' 
                                             THEN  CN.CTA_CRD_VENTAS
                                             ELSE CASE WHEN P.GRUPOCONTABLE= 'P' 
                                             THEN CN.CTA_CRD_PRODUCCION
                                             ELSE CASE WHEN P.GRUPOCONTABLE= 'O' 
                                             THEN  CN.CTA_CRD_OTRO
                                             ELSE CN.CTA_CRD_ADMINISTRACION END END END, HIS.COMPANIA, HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES,  CN.TIPODEFONDO
                                             ,CN.SUCURSAL,CN.TERCERO,P.ID_CENTRO_DE_COSTO, P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR
                                    HAVING SUM(HIS.VALOR) <>0                                    
                                   
                      ORDER BY TIPODEFONDO, ID_DE_CONCEPTO       
                             
                )
            LOOP
                --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS POR FONDOS. || MI_RS.NOMBREFONDO || || FORMAT(RS.PERCENTPOSITION, '000.00') || %'
                MI_CUENTADEBITO := RPAD(' ', 16);
                MI_CUENTACREDITO := RPAD(' ', 16);
                MI_VALORD := 0;
                --MI_VALORC := 0;
                MI_SUCURSAL := '';
                MI_TERCERO := RPAD(' ', 11);
                MI_SUCURSAL_DEBITO := '';
                MI_TERCERO_DEBITO := RPAD(' ', 11);
                MI_CENTROCOSTO := '          ';
                MI_CUENTA := '';
                MI_REFERENCIA := '';
                MI_FUENTE_RECURSO := '';    
                
                 MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DEBITO, 16), RPAD(' ', 16));
                 MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CREDITO, 16), RPAD(' ', 16));

                /*IF MI_RS.GRUPOCONTABLE = 'V'  THEN
                    MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_VENTAS, 16), RPAD(' ', 16));
                    MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_VENTAS, 16), RPAD(' ', 16));
                ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN
                    MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_PRODUCCION, 16), RPAD(' ', 16));
                    MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_PRODUCCION, 16), RPAD(' ', 16));
                ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN
                    MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_OTRO, 16), RPAD(' ', 16));
                    MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_OTRO, 16), RPAD(' ', 16));
                ELSE
                    MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
                    MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
                END IF;    */
    
                 IF MI_CONCEPTO = 0 THEN
                    MI_CONCEPTO := MI_RS.ID_DE_CONCEPTO;
                END IF;

                IF NVL(TRIM(MI_CUENTADEBITO), ' ') <> ' ' THEN
                    MI_VALORD := MI_RS.SUMADEVALOR;
                END IF;    
                              
                IF NOT(MI_VALORD + MI_VALORCCF = 0) THEN                                                        

                    MI_TERCERO := MI_RS.NIT;

                    MI_TERCERO := REPLACE(MI_TERCERO, '-', '');
                    MI_TERCERO := REPLACE(MI_TERCERO, '.', '');

                    MI_TERCERO := NVL(RPAD(MI_TERCERO, 11), '99999999999');
                    IF MI_TERCERO = '99999999999' THEN
                        MI_SUCURSAL := '999';
                    ELSIF MI_TERCERO IS NULL OR MI_TERCERO = '           ' THEN
                        MI_TERCERO := '99999999999';
                        MI_SUCURSAL := '999';
                    ELSIF MI_RS.SUCURSAL <> '' THEN
                        MI_SUCURSAL := MI_RS.SUCURSAL;
                    ELSE
                        MI_SUCURSAL := '001';
                    END IF;         

                    -- (GPORTILLA:12/04/2023) Se separa el calculo del tercero y sucursal parta el debito, debido a que debtomar el nit del tercero y no del fondo
                    MI_TERCERO_DEBITO := MI_RS.TERCERO;

                    MI_TERCERO_DEBITO := REPLACE(MI_TERCERO_DEBITO, '-', '');
                    MI_TERCERO_DEBITO := REPLACE(MI_TERCERO_DEBITO, '.', '');

                    MI_TERCERO_DEBITO := NVL(RPAD(MI_TERCERO_DEBITO, 11), '99999999999');
                    IF MI_TERCERO_DEBITO = '99999999999' THEN
                        MI_SUCURSAL_DEBITO := '999';
                    ELSIF MI_TERCERO_DEBITO IS NULL OR MI_TERCERO_DEBITO = '           ' THEN
                        MI_TERCERO_DEBITO := '99999999999';
                        MI_SUCURSAL_DEBITO := '999';
                    ELSIF MI_RS.SUCURSAL <> '' THEN
                        MI_SUCURSAL_DEBITO := MI_RS.SUCURSAL;
                    ELSE
                        MI_SUCURSAL_DEBITO := '001';
                    END IF;      
                    -- FIN (GPORTILLA:12/04/2023)

                    -- (GPORTILLA:23/02/2023)el campo cuenta se envia en espacios vacios ya que au no esta definido de donde se debe tomar el dato
                    MI_CUENTA := RPAD(' ', 16);

                    --<TICKET: CC_436 AUTOR:CP FECHA:04/12/2024
                    MI_COD_AUXILIAR := RPAD(' ', 16);
                    MI_COD_AUXILIAR := NVL(MI_RS.COD_AUXILIAR,'');
                    IF MI_COD_AUXILIAR =  '' THEN
                        MI_COD_AUXILIAR := '9999999999999999';
                    END IF;
                    MI_COD_AUXILIAR := NVL(RPAD(MI_COD_AUXILIAR, 16), '9999999999999999');
                    
                    MI_REFERENCIA := NVL(MI_RS.REFERENCIA,'');
                    IF MI_REFERENCIA =  '' THEN
                        MI_REFERENCIA := '99999999999999999999';
                    END IF;
                    MI_REFERENCIA := NVL(RPAD(MI_REFERENCIA, 20),'99999999999999999999');
                    
                    MI_FUENTE_RECURSO := NVL(MI_RS.FUENTE_DE_RECURSO,'');
                    IF MI_FUENTE_RECURSO =  '' THEN
                        MI_FUENTE_RECURSO := '99999999999999999999';
                    END IF;
                    MI_FUENTE_RECURSO := NVL(RPAD(MI_FUENTE_RECURSO, 20),'99999999999999999999');
                    --<TICKET/>
                    -- FIN (GPORTILLA:23/02/2023)

                    MI_NOMBRE := 'TRANSFERENCIAS NOMINA MES DE ' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MES) || ' DE ' || UN_ANO || ' ' || PCK_NOMINA_COM5.FC_QUITAESPECIALES(MI_RS.NOMBREFONDO);

                    MI_CENTROCOSTO := '9999999999';
                    
                   /* MI_VALORCCF := 0;   
             
                                IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' '  THEN
                                   MI_VALORCCF := MI_VALORCCF + MI_RS.SUMADEVALOR;
                                  END IF; 
               
                          IF MI_VALORCCF <> 0  THEN   
                            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD( MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORCCF, 18) || MI_TERCERO_DEBITO|| MI_SUCURSAL || MI_CENTROCOSTO || '9999999999999999' || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );    
                         END IF ;*/
                  
                    MI_VALP := MI_VALP + MI_VALORD;

                    IF MI_RS.ID_DE_CONCEPTO <> MI_CONCEPTO  THEN
                    
                     /*IF MI_VALORCCF <> 0  THEN
                     

                            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD( MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORCCF, 18) || MI_TERCERO_DEBITO || MI_SUCURSAL || MI_CENTROCOSTO || '9999999999999999' || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );    
                            
                        END IF;*/
                        

                        MI_VALORCCF := 0;

                        MI_CONCEPTO := MI_RS.ID_DE_CONCEPTO;

                        IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' ' THEN
                            MI_VALORCCF := MI_VALORCCF + MI_RS.SUMADEVALOR;
                        END IF;

                    ELSE

                        IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' ' THEN
                            MI_VALORCCF := MI_VALORCCF + MI_RS.SUMADEVALOR;                                 
                        END IF;

                    END IF;

                    --ALERTA 'EL CONCEPTO: || MI_RS.ID_DE_CONCEPTO || || MI_RS.NOMBRE_CONCEPTO ||, NO TIENE DEFINIDAS CUENTAS PARA EL GRUPO CONTABLE:  || MI_RS.GRUPOCONTABLE
                    --GOTO CONTINUAR33   
                    MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(MI_CUENTADEBITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || MI_TERCERO_DEBITO || MI_SUCURSAL_DEBITO || MI_CENTROCOSTO || MI_COD_AUXILIAR || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
                    --MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || MI_COD_AUXILIAR || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
                END IF; --CONTINUAR33:

            END LOOP CCFPORNIT;
        
        ELSE
             FOR MI_RS IN
                (SELECT SUM(HIS.VALOR) AS SUMADEVALOR,
                        MAX(CN.TIPODEFONDO) AS TIPODEFONDO,
                        P.GRUPOCONTABLE AS GRUPOCONTABLE,
                        CN.CTA_DBT_ADMINISTRACION AS CTA_DBT_ADMINISTRACION,
                        CN.CTA_CRD_ADMINISTRACION AS CTA_CRD_ADMINISTRACION,
                        CN.CTA_DBT_PRODUCCION AS CTA_DBT_PRODUCCION,
                        CN.CTA_CRD_PRODUCCION AS CTA_CRD_PRODUCCION,
                        CN.CTA_DBT_VENTAS AS CTA_DBT_VENTAS,
                        CN.CTA_CRD_VENTAS AS CTA_CRD_VENTAS,
                        CN.CTA_DBT_OTRO AS CTA_DBT_OTRO,
                        CN.CTA_CRD_OTRO AS CTA_CRD_OTRO,
                        MAX(HIS.ID_DE_CONCEPTO) AS ID_DE_CONCEPTO,
                        MAX(CN.NOMBRE_CONCEPTO) AS NOMBRE_CONCEPTO,                    
                        CN.NITDE,
                        CASE CN.TIPODEFONDO                       
                        WHEN 'CCF' THEN V_CAJA_COMPENSACION.NIT
                    ELSE ''
                    END NIT,
                    CASE CN.TIPODEFONDO                    
                    WHEN 'CCF' THEN V_CAJA_COMPENSACION.NOMBRE_CAJA
                    ELSE ''
                    END NOMBREFONDO,
                    CASE CN.TIPODEFONDO                   
                    WHEN 'CCF' THEN V_CAJA_COMPENSACION.PUBLICO
                    ELSE 0
                    END PUBLICO,
                    CN.SUCURSAL,CN.TERCERO,P.ID_CENTRO_DE_COSTO, P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR
                FROM HISTORICOS HIS INNER JOIN CONCEPTOS CN
                         ON HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
                        AND HIS.COMPANIA = CN.COMPANIA
                    INNER JOIN PERSONAL P
                         ON HIS.COMPANIA = P.COMPANIA
                        AND HIS.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO                
                    LEFT JOIN V_CAJA_COMPENSACION     --CCF
                         ON P.COMPANIA = V_CAJA_COMPENSACION.COMPANIA
                        AND P.CAJA_COMPENSACION = V_CAJA_COMPENSACION.CAJA_COMPENSACION
          INNER JOIN PERIODOS 
                        ON HIS.COMPANIA = PERIODOS.COMPANIA
                        AND HIS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                        AND HIS.ANO = PERIODOS.ANO
                        AND HIS.MES = PERIODOS.MES
                        AND HIS.PERIODO = PERIODOS.PERIODO                    
                WHERE HIS.COMPANIA = UN_COMPANIA
                  AND HIS.ID_DE_PROCESO = UN_PROCESO
                  AND HIS.ANO = UN_ANO
                  AND HIS.MES = UN_MES
                  AND (   (1 = CASE WHEN UN_PORPERIODO = 0 THEN 1 ELSE 0 END )      --(MZANGUNA:26/12/2018)-Se adiciona condiciÃ³n por periodo.
                       OR (1 = CASE WHEN UN_PORPERIODO <> 0 THEN 1 ELSE 0 END AND HIS.PERIODO = UN_PERIODO)
                      )
                  AND CN.CLASE = 8
                  AND CN.NITDE NOT IN ('O','E')
                  AND CN.TIPODEFONDO = MI_RS1.TIPODEFONDO
                  AND ((1 = CASE WHEN MI_RS1.TIPODEFONDO = 'CCF' THEN 1 ELSE 0 END AND V_CAJA_COMPENSACION.NIT = MI_RS1.NIT))
          AND PERIODOS.ACUMULADO NOT IN(0)                   
                GROUP BY CASE CN.TIPODEFONDO
                        WHEN 'CCF' THEN V_CAJA_COMPENSACION.NIT
                        ELSE ''
                        END ,
                        CASE CN.TIPODEFONDO
                        WHEN 'CCF' THEN V_CAJA_COMPENSACION.NOMBRE_CAJA
                        ELSE ''
                        END,
                        CASE CN.TIPODEFONDO
                        WHEN 'CCF' THEN V_CAJA_COMPENSACION.PUBLICO
                        ELSE 0
                        END
                        ,CN.NITDE,  P.GRUPOCONTABLE, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION,
                        CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO,
                        CN.CTA_DBT_ADMINISTRACION, HIS.COMPANIA, HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES,  CN.TIPODEFONDO
                        ,CN.SUCURSAL,CN.TERCERO,P.ID_CENTRO_DE_COSTO, P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR
                HAVING SUM(HIS.VALOR) <>0
                ORDER BY CN.TIPODEFONDO, ID_DE_CONCEPTO
                )
            LOOP
                --SYSCMD ACSYSCMDSETSTATUS, 'PROCESANDO CONCEPTOS POR FONDOS. || MI_RS.NOMBREFONDO || || FORMAT(RS.PERCENTPOSITION, '000.00') || %'
                MI_CUENTADEBITO := RPAD(' ', 16);
                MI_CUENTACREDITO := RPAD(' ', 16);
                MI_VALORD := 0;
                --MI_VALORC := 0;
                MI_SUCURSAL := '';
                MI_TERCERO := RPAD(' ', 11);
                MI_SUCURSAL_DEBITO := '';
                MI_TERCERO_DEBITO := RPAD(' ', 11);
                MI_CENTROCOSTO := '          ';
                MI_CUENTA := '';
                MI_REFERENCIA := '';
                MI_FUENTE_RECURSO := '';                

                IF MI_RS.GRUPOCONTABLE = 'V' THEN
                    MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_VENTAS, 16), RPAD(' ', 16));
                    MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_VENTAS, 16), RPAD(' ', 16));
                ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN
                    MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_PRODUCCION, 16), RPAD(' ', 16));
                    MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_PRODUCCION, 16), RPAD(' ', 16));
                ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN
                    MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_OTRO, 16), RPAD(' ', 16));
                    MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_OTRO, 16), RPAD(' ', 16));
                ELSE
                    MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
                    MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
                END IF;

                IF MI_CONCEPTO = 0 THEN                    
                    MI_CONCEPTO := MI_RS.ID_DE_CONCEPTO;                
                END IF;
                
                IF NVL(TRIM(MI_CUENTADEBITO), ' ') <> ' ' THEN
                    MI_VALORD := MI_RS.SUMADEVALOR;
                END IF;     
               
                  
                IF NOT(MI_VALORD + MI_VALORCCF = 0) THEN

                    MI_TERCERO := MI_RS.NIT;

                    MI_TERCERO := REPLACE(MI_TERCERO, '-', '');
                    MI_TERCERO := REPLACE(MI_TERCERO, '.', '');

                    MI_TERCERO := NVL(RPAD(MI_TERCERO, 11), '99999999999');
                    IF MI_TERCERO = '99999999999' THEN
                        MI_SUCURSAL := '999';
                    ELSIF MI_TERCERO IS NULL OR MI_TERCERO = '           ' THEN
                        MI_TERCERO := '99999999999';
                        MI_SUCURSAL := '999';
                    ELSIF MI_RS.SUCURSAL <> '' THEN
                        MI_SUCURSAL := MI_RS.SUCURSAL;
                    ELSE
                        MI_SUCURSAL := '001';
                    END IF;    

                    -- (GPORTILLA:12/04/2023) Se separa el calculo del tercero y sucursal parta el debito, debido a que debtomar el nit del tercero y no del fondo
                    MI_TERCERO_DEBITO := MI_RS.TERCERO;

                    MI_TERCERO_DEBITO := REPLACE(MI_TERCERO_DEBITO, '-', '');
                    MI_TERCERO_DEBITO := REPLACE(MI_TERCERO_DEBITO, '.', '');

                    MI_TERCERO_DEBITO := NVL(RPAD(MI_TERCERO_DEBITO, 11), '99999999999');
                    IF MI_TERCERO_DEBITO = '99999999999' THEN
                        MI_SUCURSAL_DEBITO := '999';
                    ELSIF MI_TERCERO_DEBITO IS NULL OR MI_TERCERO_DEBITO = '           ' THEN
                        MI_TERCERO_DEBITO := '99999999999';
                        MI_SUCURSAL_DEBITO := '999';
                    ELSIF MI_RS.SUCURSAL <> '' THEN
                        MI_SUCURSAL_DEBITO := MI_RS.SUCURSAL;
                    ELSE
                        MI_SUCURSAL_DEBITO := '001';
                    END IF; 
                    -- FIN (GPORTILLA:12/04/2023)

                    -- (GPORTILLA:23/02/2023)el campo cuenta se envia en espacios vacios ya que au no esta definido de donde se debe tomar el dato
                    MI_CUENTA := RPAD(' ', 16);

                    --<TICKET: CC_436 AUTOR:CP FECHA:04/12/2024
                    MI_COD_AUXILIAR := RPAD(' ', 16);
                    MI_COD_AUXILIAR := NVL(MI_RS.COD_AUXILIAR,'');
                    IF MI_COD_AUXILIAR =  '' THEN
                        MI_COD_AUXILIAR := '9999999999999999';
                    END IF;
                    MI_COD_AUXILIAR := NVL(RPAD(MI_COD_AUXILIAR, 16), '9999999999999999');
                    
                    MI_REFERENCIA := NVL(MI_RS.REFERENCIA,'');
                    IF MI_REFERENCIA =  '' THEN
                        MI_REFERENCIA := '99999999999999999999';
                    END IF;
                    MI_REFERENCIA := NVL(RPAD(MI_REFERENCIA, 20),'99999999999999999999');
                    
                    MI_FUENTE_RECURSO := NVL(MI_RS.FUENTE_DE_RECURSO,'');
                    IF MI_FUENTE_RECURSO =  '' THEN
                        MI_FUENTE_RECURSO := '99999999999999999999';
                    END IF;
                    MI_FUENTE_RECURSO := NVL(RPAD(MI_FUENTE_RECURSO, 20),'99999999999999999999');
                    --<TICKET/>
                    -- FIN (GPORTILLA:23/02/2023)

                    MI_NOMBRE := 'TRANSFERENCIAS NOMINA MES DE ' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MES) || ' DE ' || UN_ANO || ' ' || PCK_NOMINA_COM5.FC_QUITAESPECIALES(MI_RS.NOMBREFONDO);
                    MI_CENTROCOSTO := NVL(RPAD(MI_RS.ID_CENTRO_DE_COSTO, 10), '9999999999');

                    MI_VALP := MI_VALP + MI_VALORD;

                   IF MI_RS.ID_DE_CONCEPTO <> MI_CONCEPTO  THEN

                       IF MI_VALORCCF > 0 THEN

                            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORCCF, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || MI_COD_AUXILIAR || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );                            
                        END IF;

                        MI_VALORCCF := 0;

                        MI_CONCEPTO := MI_RS.ID_DE_CONCEPTO;

                        IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' ' THEN
                            MI_VALORCCF := MI_VALORCCF + MI_RS.SUMADEVALOR;
                        END IF;

                    ELSE

                        IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' ' THEN
                            MI_VALORCCF := MI_VALORCCF + MI_RS.SUMADEVALOR;                                 
                        END IF;

                    END IF;                            
                    --ALERTA 'EL CONCEPTO: || MI_RS.ID_DE_CONCEPTO || || MI_RS.NOMBRE_CONCEPTO ||, NO TIENE DEFINIDAS CUENTAS PARA EL GRUPO CONTABLE:  || MI_RS.GRUPOCONTABLE
                    --GOTO CONTINUAR33                        
                    MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(MI_CUENTADEBITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || MI_TERCERO_DEBITO || MI_SUCURSAL_DEBITO || MI_CENTROCOSTO || MI_COD_AUXILIAR || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
                    --MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || '9999999999999999' || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );

                END IF; --CONTINUAR33:

            END LOOP CCFPORNIT;
        END IF;       
-- Agrupa cuenta de parafiscales
FOR MI_RS IN (
   
    SELECT SUM(HIS.VALOR) AS SUMADEVALOR,
                            MAX(CN.TIPODEFONDO) AS TIPODEFONDO,
                             
                            CASE  WHEN  P.GRUPOCONTABLE= 'V' 
                             THEN  CN.CTA_CRD_VENTAS
                             ELSE CASE WHEN P.GRUPOCONTABLE= 'P' 
                             THEN CN.CTA_CRD_PRODUCCION
                             ELSE CASE WHEN P.GRUPOCONTABLE= 'O' 
                             THEN  CN.CTA_CRD_OTRO
                             ELSE CN.CTA_CRD_ADMINISTRACION END END END CTA_CREDITO,                                                 
                            MAX(HIS.ID_DE_CONCEPTO) AS ID_DE_CONCEPTO,
                            MAX(CN.NOMBRE_CONCEPTO) AS NOMBRE_CONCEPTO,
                            CN.NITDE,
                            CASE CN.TIPODEFONDO
                            WHEN 'CCF' THEN V_CAJA_COMPENSACION.NIT
                            ELSE ''
                            END NIT,
                            CASE CN.TIPODEFONDO
                            WHEN 'CCF' THEN V_CAJA_COMPENSACION.NOMBRE_CAJA
                            ELSE ''
                            END NOMBREFONDO,
                            CASE CN.TIPODEFONDO
                            WHEN 'CCF' THEN V_CAJA_COMPENSACION.PUBLICO
                            ELSE 0
                            END PUBLICO
                            ,CN.SUCURSAL,CN.TERCERO,P.ID_CENTRO_DE_COSTO, P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR
                    FROM HISTORICOS HIS INNER JOIN CONCEPTOS CN
                             ON HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
                            AND HIS.COMPANIA = CN.COMPANIA
                        INNER JOIN PERSONAL P
                             ON HIS.COMPANIA = P.COMPANIA
                            AND HIS.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
                        LEFT JOIN V_CAJA_COMPENSACION     --CCF
                             ON P.COMPANIA = V_CAJA_COMPENSACION.COMPANIA
                            AND P.CAJA_COMPENSACION = V_CAJA_COMPENSACION.CAJA_COMPENSACION
            INNER JOIN PERIODOS 
                            ON HIS.COMPANIA = PERIODOS.COMPANIA
                            AND HIS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                            AND HIS.ANO = PERIODOS.ANO
                            AND HIS.MES = PERIODOS.MES
                            AND HIS.PERIODO = PERIODOS.PERIODO                        
                    WHERE HIS.COMPANIA = UN_COMPANIA
                      AND HIS.ID_DE_PROCESO = UN_PROCESO
                      AND HIS.ANO = UN_ANO
                      AND HIS.MES = UN_MES
                      AND (   (1 = CASE WHEN UN_PORPERIODO = 0 THEN 1 ELSE 0 END )      --(MZANGUNA:26/12/2018)-Se adiciona condiciÃ³n por periodo.
                           OR (1 = CASE WHEN UN_PORPERIODO <> 0 THEN 1 ELSE 0 END AND HIS.PERIODO = UN_PERIODO)
                          )
                      AND CN.CLASE = 8
                      AND CN.NITDE NOT IN ('O','E')
                      AND CN.TIPODEFONDO = MI_RS1.TIPODEFONDO
                      AND ((1 = CASE WHEN MI_RS1.TIPODEFONDO = 'CCF' THEN 1 ELSE 0 END AND V_CAJA_COMPENSACION.NIT = MI_RS1.NIT))
            AND PERIODOS.ACUMULADO NOT IN(0)                   
                    GROUP BY CASE CN.TIPODEFONDO
                            WHEN 'CCF' THEN V_CAJA_COMPENSACION.NIT
                            ELSE ''
                            END ,
                            CASE CN.TIPODEFONDO                      
                            WHEN 'CCF' THEN V_CAJA_COMPENSACION.NOMBRE_CAJA
                            ELSE ''
                            END,
                            CASE CN.TIPODEFONDO                        
                            WHEN 'CCF' THEN V_CAJA_COMPENSACION.PUBLICO
                            ELSE 0
                            END
                            ,CN.NITDE, 
                            CASE  WHEN  P.GRUPOCONTABLE= 'V' 
                             THEN  CN.CTA_CRD_VENTAS
                             ELSE CASE WHEN P.GRUPOCONTABLE= 'P' 
                             THEN CN.CTA_CRD_PRODUCCION
                             ELSE CASE WHEN P.GRUPOCONTABLE= 'O' 
                             THEN  CN.CTA_CRD_OTRO
                             ELSE CN.CTA_CRD_ADMINISTRACION END END END, HIS.COMPANIA, HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES,  CN.TIPODEFONDO
                             ,CN.SUCURSAL,CN.TERCERO,P.ID_CENTRO_DE_COSTO, P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR
                    HAVING SUM(HIS.VALOR) <>0                                    
                   
      ORDER BY TIPODEFONDO, ID_DE_CONCEPTO) 
      
      LOOP
      MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CREDITO, 16), RPAD(' ', 16));
      MI_TERCERO := RPAD(MI_RS.NIT, 11);
      MI_VALORCCF := 0;   
             
                                IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' '  THEN
                                   MI_VALORCCF := MI_VALORCCF + MI_RS.SUMADEVALOR;
                                  END IF; 
               
                          IF MI_VALORCCF <> 0  THEN   
                            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD( MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORCCF, 18) || MI_TERCERO|| MI_SUCURSAL || MI_CENTROCOSTO || '9999999999999999' || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );    
                         END IF ;
            END LOOP;             
       /* IF MI_VALORCCF > 0 THEN                    
            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORCCF, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || '9999999999999999' || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );                            
        END IF;*/

       IF MI_VALORC > 0 THEN
            --MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || '9999999999999999' || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || '9999999999' || '9999999999999999' || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
        END IF;


        IF UN_PORCONTRIBUCION > 0 THEN
            MI_TERCERO := NVL(RPAD(MI_RS1.NIT, 11), '99999999999');
            IF MI_TERCERO = '99999999999' THEN
                MI_SUCURSAL := '999';
            ELSIF MI_TERCERO IS NULL OR MI_TERCERO = '           ' THEN
                MI_TERCERO := '99999999999';
                MI_SUCURSAL := '999';
            ELSE
                MI_SUCURSAL := '001';
            END IF;
            MI_CENTROCOSTO := NVL(RPAD('9999999999', 10), '9999999999');
            IF UN_MANEJACENTROCOSTO = 0 THEN
                MI_CENTROCOSTO := '9999999999';
            END IF;

            -- (GPORTILLA:23/02/2023)el campo cuenta se envia en espacios vacios ya que au no esta definido de donde se debe tomar el dato
            MI_CUENTA := RPAD(' ', 16);

            --<TICKET: CC_436 AUTOR:CP FECHA:04/12/2024
                    MI_COD_AUXILIAR := RPAD(' ', 16);
                    MI_COD_AUXILIAR := NVL('','');
                    IF MI_COD_AUXILIAR =  '' THEN
                        MI_COD_AUXILIAR := '9999999999999999';
                    END IF;
                    MI_COD_AUXILIAR := NVL(RPAD(MI_COD_AUXILIAR, 16), '9999999999999999');
                    
                    MI_REFERENCIA := NVL('','');
                    IF MI_REFERENCIA =  '' THEN
                        MI_REFERENCIA := '99999999999999999999';
                    END IF;
                    MI_REFERENCIA := NVL(RPAD(MI_REFERENCIA, 20),'99999999999999999999');
                    
                    MI_FUENTE_RECURSO := NVL('','');
                    IF MI_FUENTE_RECURSO =  '' THEN
                        MI_FUENTE_RECURSO := '99999999999999999999';
                    END IF;
                    MI_FUENTE_RECURSO := NVL(RPAD(MI_FUENTE_RECURSO, 20),'99999999999999999999');
                    --<TICKET/>
            -- FIN (GPORTILLA:23/02/2023)

            MI_VALORA := (PCK_SYSMAN_UTL.FC_ROUND(MI_VALP * (UN_PORCONTRIBUCION) / 1000, 2) * 100) / 100;
            --DIM VALORSTR AS STRING
            --VALORSTR := PCK_SYSMAN_UTL.FC_STRZERO(0, 18 - LEN(MI_VALORA)) || MI_VALORA;
            --VALORSTR := IIF(LEN(VALORSTR) < 18, PCK_SYSMAN_UTL.FC_STRZERO(0, 18 - LEN(VALORSTR)) || VALORSTR, VALORSTR);

            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(UN_CUENTADBT, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORA, 18) || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || MI_COD_AUXILIAR || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );
            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB( RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(UN_CUENTACRD, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORA, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || MI_COD_AUXILIAR || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64) || MI_CUENTA || MI_REFERENCIA || MI_FUENTE_RECURSO || CHR(13) || CHR(10) );

            MI_VALP := 0;
            MI_VALORA := 0;
        END IF;

    END LOOP FONDOSNITAGRUPADOS;

    RETURN MI_RTAPLANO;

END FC_PLANOCONTASYSMANCONTABLECC;


FUNCTION FC_PLANOCONTASYSMANCONTABLE(
/*
    NAME              : FC_PLANOCONTASYSMANCONTABLECC
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 27/06/2018
    TIME              : 02:21 PM
    SOURCE MODULE     : NOMINAP2018.06.05, Función InterfacesysmanContable
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    PARAMETERS        :

    MODIFICATIONS     :

    @NAME:planoContabilizarsysmancontable
    @METHOD:  GET
*/
     UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_PROCESO             IN PCK_SUBTIPOS.TI_ID_DE_PROCESO
    ,UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO
    ,UN_MES               IN PCK_SUBTIPOS.TI_MES
    ,UN_PERIODO             IN  PCK_SUBTIPOS.TI_PERIODO_NOMI
    ,UN_FECHAINTER          IN DATE
    ,UN_TIPOCOMPROBANTE     IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE DEFAULT 'NOM'
    ,UN_NUMEROCOMPROBANTE   IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT
    ,UN_MANEJACENTROCOSTO   IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0           --FORMS!INTERFACE_NOMINA!MANEJACENTROS
    ,UN_PORCONTRIBUCION     IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0            --FORMS!INTERFACE_NOMINA!PORC
    ,UN_CUENTADBT           IN PCK_SUBTIPOS.TI_CODIGOCONTA DEFAULT ''     --FORMS!INTERFACE_NOMINA!DBT
    ,UN_CUENTACRD           IN PCK_SUBTIPOS.TI_CODIGOCONTA DEFAULT ''     --FORMS!INTERFACE_NOMINA!CRD
    ,UN_UNICOCOMPROBANTE    IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0           --FORMS!INTERFACE_NOMINA!UNCOMPROBANTE
) RETURN CLOB

AS
    MI_CONDICIONACME           PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS                  PCK_SUBTIPOS.TI_CAMPOS;
    MI_FILAS                   PCK_SUBTIPOS.TI_ENTERO;
    MI_CUENTAREGISTRO          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_NUMERO                  PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
    MI_NUMEROFIJO              PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
    MI_CUENTADEBITO            PCK_SUBTIPOS.TI_CODIGOCONTA;
    MI_CUENTACREDITO           PCK_SUBTIPOS.TI_CODIGOCONTA;
    MI_VALORD                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALORC                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALP                    PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALORA                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_SUCURSAL                PCK_SUBTIPOS.TI_SUCURSAL;
    MI_TERCERO                 PCK_SUBTIPOS.TI_TERCERO;
    MI_CENTROCOSTO             PCK_SUBTIPOS.TI_CENTRO_COSTO;
    MI_NOMBRE                  VARCHAR2(250 CHAR);
    MI_RTAPLANO                CLOB;
    MI_REEMPLAZOS              PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_COD_AUXILIAR            PERSONAL.COD_AUXILIAR%TYPE;
BEGIN


    /*IF FORMS!INTERFACE_NOMINA!OPCION = 2 AND TIPO = 'NOM' THEN
       MSGBOX 'REVIZAR EL TIPO DE COMPROBANTE DE INTERFASE PATRONAL SI ES COM O NOM', VBCRITICAL, 'SYSMAN SOFTWARE'
    END IF; */

    MI_NUMERO := RPAD(UN_NUMEROCOMPROBANTE - 1, 10);
    MI_NUMEROFIJO := RPAD(UN_NUMEROCOMPROBANTE + 1, 10);

    BEGIN
        MI_CONDICIONACME := 'COMPANIA = ''' || UN_COMPANIA || ''' ';
        MI_FILAS := PCK_DATOS.FC_ACME
            (UN_TABLA     => 'ERRORES'
            ,UN_ACCION    => 'E'
            ,UN_CONDICION => MI_CONDICIONACME);
    END;

    BEGIN
        SELECT COUNT(0)
        INTO   MI_CUENTAREGISTRO
        FROM   PERIODOS
        WHERE  COMPANIA = UN_COMPANIA
          AND  ID_DE_PROCESO =UN_PROCESO
          AND  ANO = UN_ANO
          AND  MES = UN_MES
          AND  PERIODO = UN_PERIODO;

        IF MI_CUENTAREGISTRO = 0 THEN
            --MSGBOX 'PERIODO NO DEFINIDO EN NÓMINA.', VBOKONLY + VBINFORMATION
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
        END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
        --No existe el periodo --PERIODO--, Año: ---ANO-- En nómina.
        MI_REEMPLAZOS(1).CLAVE := 'PERIODO';
        MI_REEMPLAZOS(1).VALOR := UN_PERIODO;
        MI_REEMPLAZOS(2).CLAVE := 'ANO';
        MI_REEMPLAZOS(2).VALOR := UN_ANO;

        PCK_ERR_MSG.RAISE_WITH_MSG
                  ( UN_EXC_COD => SQLCODE
                  , UN_TABLAERROR => 'PERIODOS'
                  , UN_ERROR_COD  => PCK_ERRORES.ERRR_NOPERIODONOMINA
                  , UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;

    IF UN_UNICOCOMPROBANTE = 0 THEN
        MI_NUMERO := RPAD(MI_NUMEROFIJO, 10);
    ELSE
        MI_NUMERO := RPAD(UN_NUMEROCOMPROBANTE, 10);
    END IF;


    <<FONDOSNITAGRUPADOS>>
    FOR MI_RS1 IN
        (SELECT CN.NITDE, CN.TIPODEFONDO ,
            CASE CN.TIPODEFONDO
                WHEN 'EPS' THEN V_FONDO_DE_SALUD.NIT
                WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NIT
                WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NIT
                WHEN 'CCF' THEN V_CAJA_COMPENSACION.NIT
                WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NIT
            ELSE ''
            END NIT
            ,CASE WHEN CN.TIPODEFONDO = 'NIN' AND CN.NITDE = 'O' THEN HIS.ID_DE_CONCEPTO ELSE 0 END CONCEPTO
        FROM HISTORICOS HIS INNER JOIN CONCEPTOS CN
                 ON HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
                AND HIS.COMPANIA = CN.COMPANIA
            INNER JOIN PERSONAL P
                 ON HIS.COMPANIA = P.COMPANIA
                AND HIS.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
            LEFT JOIN V_FONDO_DE_SALUD     --EPS
                 ON P.COMPANIA = V_FONDO_DE_SALUD.COMPANIA
                AND P.FONDO_SALUD = V_FONDO_DE_SALUD.FONDO_SALUD
            LEFT JOIN V_FONDO_DE_RIESGOS      --ARL
                 ON  P.COMPANIA = V_FONDO_DE_RIESGOS.COMPANIA
                AND P.FONDO_RIESGOS = V_FONDO_DE_RIESGOS.FONDO_RIESGOS
            LEFT JOIN V_FONDO_DE_PENSIONES    --AFP
                 ON P.COMPANIA = V_FONDO_DE_PENSIONES.COMPANIA
                AND P.ID_DEL_FONDO = V_FONDO_DE_PENSIONES.ID_DEL_FONDO
            LEFT JOIN V_CAJA_COMPENSACION     --CCF
                 ON P.COMPANIA = V_CAJA_COMPENSACION.COMPANIA
                AND P.CAJA_COMPENSACION = V_CAJA_COMPENSACION.CAJA_COMPENSACION
            LEFT JOIN V_FONDO_DE_CESANTIAS  --CES
                 ON P.COMPANIA = V_FONDO_DE_CESANTIAS.COMPANIA
                AND P.FONDO_CESANTIAS = V_FONDO_DE_CESANTIAS.FONDO_CESANTIAS
        WHERE HIS.COMPANIA = UN_COMPANIA
          AND HIS.ID_DE_PROCESO = UN_PROCESO
          AND HIS.ANO = UN_ANO
          AND HIS.MES = UN_MES
          AND HIS.PERIODO = UN_PERIODO
          AND CN.CLASE = 8
          AND (   (1 = CASE WHEN CN.TIPODEFONDO = 'EPS' THEN 1 ELSE 0 END AND CN.ID_DE_CONCEPTO IN (116,119))
                OR(1 = CASE WHEN CN.TIPODEFONDO = 'ARL' THEN 1 ELSE 0 END AND CN.ID_DE_CONCEPTO IN (111) )
                OR(1 = CASE WHEN CN.TIPODEFONDO = 'AFP' THEN 1 ELSE 0 END AND CN.ID_DE_CONCEPTO IN (117) )
                OR(1 = CASE WHEN CN.TIPODEFONDO = 'CCF' THEN 1 ELSE 0 END )
                OR(1 = CASE WHEN CN.TIPODEFONDO = 'CES' THEN 1 ELSE 0 END )
                OR(1 = CASE WHEN CN.TIPODEFONDO = 'NIN' AND CN.NITDE = 'O' THEN 1 ELSE 0 END  ) )
          AND (   (1 = CASE WHEN CN.TIPODEFONDO = 'NIN' AND CN.NITDE = 'O' THEN 1 ELSE 0 END AND CN.NITDE = 'O' AND CN.NITDE <> 'E' )
               OR (1 = CASE WHEN CN.TIPODEFONDO <> 'NIN' THEN 1 ELSE 0 END AND CN.NITDE NOT IN ('O','E')) )
        GROUP BY  CN.NITDE, CN.TIPODEFONDO,
            CASE CN.TIPODEFONDO
                WHEN 'EPS' THEN V_FONDO_DE_SALUD.NIT
                WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NIT
                WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NIT
                WHEN 'CCF' THEN V_CAJA_COMPENSACION.NIT
                WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NIT
            ELSE ''
            END  ,CASE WHEN CN.TIPODEFONDO = 'NIN' AND CN.NITDE = 'O' THEN HIS.ID_DE_CONCEPTO ELSE 0 END
        HAVING SUM(HIS.VALOR) <>0
        ORDER BY CN.TIPODEFONDO
        )
    LOOP

        IF NOT(MI_RS1.TIPODEFONDO = 'NIN' AND MI_RS1.NITDE = 'O') THEN
            IF UN_UNICOCOMPROBANTE <> 0 THEN
                MI_NUMERO := RPAD(MI_NUMEROFIJO, 10);
            ELSE
                MI_NUMERO := RPAD(MI_NUMERO + 1, 10);
            END IF;
        END IF;

        MI_VALP := 0;

        <<FONDOSPORNIT>>
        FOR MI_RS IN
            (SELECT
              CASE CN.TIPODEFONDO
                WHEN 'EPS' THEN V_FONDO_DE_SALUD.NIT
                WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NIT
                WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NIT
                WHEN 'CCF' THEN V_CAJA_COMPENSACION.NIT
                WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NIT
              ELSE ''
              END NIT,
              CASE CN.TIPODEFONDO
              WHEN 'EPS' THEN V_FONDO_DE_SALUD.NOMBRE_FONDO_SALUD
              WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NOMBRE_FONDO_RIESGOS
              WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NOMBRE_DEL_FONDO
              WHEN 'CCF' THEN V_CAJA_COMPENSACION.NOMBRE_CAJA
              WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NOMBRE_FONDO_CESANTIAS
              ELSE ''
              END NOMBREFONDO,
              SUM(HIS.VALOR) AS SUMADEVALOR, MAX(CN.TIPODEFONDO) AS TIPODEFONDO, P.GRUPOCONTABLE AS GRUPOCONTABLE, CN.CTA_DBT_ADMINISTRACION AS CTA_DBT_ADMINISTRACION,
              CN.CTA_CRD_ADMINISTRACION AS CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION AS CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION AS CTA_CRD_PRODUCCION,
              CN.CTA_DBT_VENTAS AS CTA_DBT_VENTAS, CN.CTA_CRD_VENTAS AS CTA_CRD_VENTAS, CN.CTA_DBT_OTRO AS CTA_DBT_OTRO, CN.CTA_CRD_OTRO AS CTA_CRD_OTRO ,
              MAX(HIS.ID_DE_CONCEPTO) AS ID_DE_CONCEPTO, MAX(CN.NOMBRE_CONCEPTO) AS NOMBRE_CONCEPTO,  CN.CTA_CRE_PPTAL, CN.CTA_CONTRACRE_PPTAL 
                ,CN.SUCURSAL,CN.TERCERO,P.ID_CENTRO_DE_COSTO, P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR
            FROM HISTORICOS HIS INNER JOIN CONCEPTOS CN
                 ON HIS.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
                AND HIS.COMPANIA = CN.COMPANIA
              INNER JOIN PERIODOS PER
                 ON HIS.COMPANIA = PER.COMPANIA
                AND HIS.ID_DE_PROCESO = PER.ID_DE_PROCESO
                AND HIS.ANO = PER.ANO
                AND HIS.MES = PER.MES
                AND HIS.PERIODO = PER.PERIODO
              INNER JOIN PERSONAL P
                 ON HIS.COMPANIA = P.COMPANIA
                AND HIS.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
              LEFT JOIN V_FONDO_DE_SALUD     --EPS
                 ON P.COMPANIA = V_FONDO_DE_SALUD.COMPANIA
                AND P.FONDO_SALUD = V_FONDO_DE_SALUD.FONDO_SALUD
              LEFT JOIN V_FONDO_DE_RIESGOS      --ARL
                 ON  P.COMPANIA = V_FONDO_DE_RIESGOS.COMPANIA
                AND P.FONDO_RIESGOS = V_FONDO_DE_RIESGOS.FONDO_RIESGOS
              LEFT JOIN V_FONDO_DE_PENSIONES    --AFP
                 ON P.COMPANIA = V_FONDO_DE_PENSIONES.COMPANIA
                AND P.ID_DEL_FONDO = V_FONDO_DE_PENSIONES.ID_DEL_FONDO
              LEFT JOIN V_CAJA_COMPENSACION     --CCF
                 ON P.COMPANIA = V_CAJA_COMPENSACION.COMPANIA
                AND P.CAJA_COMPENSACION = V_CAJA_COMPENSACION.CAJA_COMPENSACION
              LEFT JOIN V_FONDO_DE_CESANTIAS  --CES
                 ON P.COMPANIA = V_FONDO_DE_CESANTIAS.COMPANIA
                AND P.FONDO_CESANTIAS = V_FONDO_DE_CESANTIAS.FONDO_CESANTIAS
            WHERE HIS.COMPANIA = UN_COMPANIA
              AND HIS.ID_DE_PROCESO = UN_PROCESO
              AND HIS.ANO = UN_ANO
              AND HIS.MES = UN_MES
              AND CN.CLASE = 8
              AND PER.ACUMULADO <> 0
              AND CN.TIPODEFONDO = MI_RS1.TIPODEFONDO
              AND (   (1 = CASE WHEN CN.TIPODEFONDO = 'EPS' THEN 1 ELSE 0 END AND V_FONDO_DE_SALUD.NIT = MI_RS1.NIT AND CN.ID_DE_CONCEPTO IN (116,399,398,119) )
                OR(1 = CASE WHEN MI_RS1.TIPODEFONDO = 'ARL' THEN 1 ELSE 0 END AND V_FONDO_DE_RIESGOS.NIT = MI_RS1.NIT )
                OR(1 = CASE WHEN MI_RS1.TIPODEFONDO = 'AFP' THEN 1 ELSE 0 END AND V_FONDO_DE_PENSIONES.NIT = MI_RS1.NIT )
                OR(1 = CASE WHEN MI_RS1.TIPODEFONDO = 'CCF' THEN 1 ELSE 0 END AND V_CAJA_COMPENSACION.NIT = MI_RS1.NIT )
                OR(1 = CASE WHEN MI_RS1.TIPODEFONDO = 'CES' THEN 1 ELSE 0 END AND V_FONDO_DE_CESANTIAS.NIT = MI_RS1.NIT)
                OR(1 = CASE WHEN MI_RS1.TIPODEFONDO = 'NIN' AND CN.NITDE = 'O' AND HIS.PERIODO = UN_PERIODO THEN 1 ELSE 0 END AND HIS.ID_DE_CONCEPTO = MI_RS1.CONCEPTO  ) )
              AND (   (1 = CASE WHEN MI_RS1.TIPODEFONDO = 'NIN' AND CN.NITDE = 'O' THEN 1 ELSE 0 END AND CN.NITDE = 'O' AND CN.NITDE <> 'E' )
                 OR (1 = CASE WHEN MI_RS1.TIPODEFONDO <> 'NIN' THEN 1 ELSE 0 END AND CN.NITDE NOT IN ('O','E')) )
            GROUP BY CASE CN.TIPODEFONDO
                  WHEN 'EPS' THEN V_FONDO_DE_SALUD.NIT
                  WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NIT
                  WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NIT
                  WHEN 'CCF' THEN V_CAJA_COMPENSACION.NIT
                  WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NIT
                ELSE ''
                END,
                CASE CN.TIPODEFONDO
                  WHEN 'EPS' THEN V_FONDO_DE_SALUD.NOMBRE_FONDO_SALUD
                  WHEN 'ARL' THEN V_FONDO_DE_RIESGOS.NOMBRE_FONDO_RIESGOS
                  WHEN 'AFP' THEN V_FONDO_DE_PENSIONES.NOMBRE_DEL_FONDO
                  WHEN 'CCF' THEN V_CAJA_COMPENSACION.NOMBRE_CAJA
                  WHEN 'CES' THEN V_FONDO_DE_CESANTIAS.NOMBRE_FONDO_CESANTIAS
                  ELSE ''
                END,
                P.GRUPOCONTABLE, CN.CTA_DBT_ADMINISTRACION, CN.CTA_CRD_ADMINISTRACION, CN.CTA_DBT_PRODUCCION, CN.CTA_CRD_PRODUCCION, CN.CTA_DBT_VENTAS,
                CN.CTA_CRD_VENTAS, CN.CTA_DBT_OTRO, CN.CTA_CRD_OTRO, CN.CTA_CRE_PPTAL, CN.CTA_CONTRACRE_PPTAL,  CN.CTA_DBT_ADMINISTRACION
                    ,CN.SUCURSAL,CN.TERCERO,P.ID_CENTRO_DE_COSTO, P.REFERENCIA, P.FUENTE_DE_RECURSO,  P.COD_AUXILIAR,
                HIS.COMPANIA, HIS.ID_DE_PROCESO, HIS.ANO, HIS.MES
            HAVING SUM(HIS.VALOR) <>0
            ORDER BY CN.TIPODEFONDO
            )
        LOOP
            MI_CUENTADEBITO := RPAD(' ', 16);
            MI_CUENTACREDITO := RPAD(' ', 16);
            MI_VALORD := 0;
            MI_VALORC := 0;
            MI_SUCURSAL := '';
            MI_TERCERO := RPAD(' ', 11);
            MI_CENTROCOSTO := '          ';
            IF MI_RS.GRUPOCONTABLE = 'V' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_VENTAS, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_VENTAS, 16), RPAD(' ', 16));

            ELSIF MI_RS.GRUPOCONTABLE = 'P' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_PRODUCCION, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_PRODUCCION, 16), RPAD(' ', 16));
            ELSIF MI_RS.GRUPOCONTABLE = 'O' THEN
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_OTRO, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_OTRO, 16), RPAD(' ', 16));

            ELSE
                MI_CUENTADEBITO := NVL(RPAD(MI_RS.CTA_DBT_ADMINISTRACION, 16), RPAD(' ', 16));
                MI_CUENTACREDITO := NVL(RPAD(MI_RS.CTA_CRD_ADMINISTRACION, 16), RPAD(' ', 16));
            END IF;
            IF NVL(TRIM(MI_CUENTADEBITO), ' ') <> ' ' THEN
                MI_VALORD := MI_RS.SUMADEVALOR;
            END IF;
            IF NVL(TRIM(MI_CUENTACREDITO), ' ') <> ' ' THEN
                MI_VALORC := MI_RS.SUMADEVALOR;
            END IF;
            IF MI_VALORD + MI_VALORC = 0 THEN
                --ALERTA 'EL CONCEPTO: ' || MI_RS.ID_DE_CONCEPTO || ' ' || MI_RS.NOMBRE_CONCEPTO || ', NO TIENE DEFINIDAS CUENTAS PARA EL GRUPO CONTABLE:  ' || MI_RS.GRUPOCONTABLE
                --GOTO CONTINUAR33

                IF MI_RS1.TIPODEFONDO = 'NIN' AND MI_RS1.NITDE = 'O' THEN
                    MI_TERCERO := MI_RS.TERCERO;
                ELSE
                    MI_TERCERO := MI_RS.NIT;
                END IF;

                MI_TERCERO := REPLACE(MI_TERCERO, '-', '');
                MI_TERCERO := REPLACE(MI_TERCERO, '.', '');

                MI_TERCERO := NVL(RPAD(MI_TERCERO, 11), '99999999999');
                IF MI_TERCERO = '99999999999' THEN
                   MI_SUCURSAL := '999';
               ELSIF MI_TERCERO IS NULL OR MI_TERCERO = '           ' THEN
                   MI_TERCERO := '99999999999';
                   MI_SUCURSAL := '999';
                ELSIF MI_RS.SUCURSAL <> '' THEN
                   MI_SUCURSAL := MI_RS.SUCURSAL;
                ELSE
                   MI_SUCURSAL := '001';
                END IF;

                MI_NOMBRE := 'TRANSFERENCIAS NOMINA MES DE ' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MES) || ' DE ' || UN_ANO || ' ' || MI_RS.NOMBREFONDO;
                MI_CENTROCOSTO := NVL(RPAD(MI_RS.ID_CENTRO_DE_COSTO, 10), '9999999999');
                IF UN_MANEJACENTROCOSTO = 0 THEN
                   MI_CENTROCOSTO := '9999999999';
                END IF;
                MI_VALP := MI_VALP + MI_VALORD;
                MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(MI_CUENTADEBITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORD, 18) || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || '9999999999999999' || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64));
                MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(MI_CUENTACREDITO, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORC, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || '9999999999999999' || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64));
            END IF; --CONTINUAR33:
        END LOOP FONDOSPORNIT;

        IF UN_PORCONTRIBUCION > 0 THEN
            MI_TERCERO := NVL(RPAD(MI_RS1.NIT, 11), '99999999999');
            IF MI_TERCERO = '99999999999' THEN
               MI_SUCURSAL := '999';
           ELSIF MI_TERCERO IS NULL OR MI_TERCERO = '           ' THEN
               MI_TERCERO := '99999999999';
               MI_SUCURSAL := '999';
            ELSE
               MI_SUCURSAL := '001';
            END IF;
            MI_CENTROCOSTO := NVL(RPAD('9999999999', 10), '9999999999');
            IF UN_MANEJACENTROCOSTO = 0 THEN
               MI_CENTROCOSTO := '9999999999';
            END IF;

            MI_VALORA := (PCK_SYSMAN_UTL.FC_ROUND(MI_VALP * (UN_PORCONTRIBUCION) / 1000, 2) * 100) / 100;
            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(UN_CUENTADBT, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORA ,18) || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || '9999999999999999' || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64));
            MI_RTAPLANO := MI_RTAPLANO || TO_CLOB(RPAD(UN_TIPOCOMPROBANTE, 3) || RPAD(MI_NUMERO, 10) || RPAD(UN_CUENTACRD, 16) || TO_CHAR(UN_FECHAINTER, 'DD/MM/YYYY') || PCK_SYSMAN_UTL.FC_STRZERO(0, 18) || PCK_SYSMAN_UTL.FC_STRZERO(MI_VALORA ,18) || MI_TERCERO || MI_SUCURSAL || MI_CENTROCOSTO || '9999999999999999' || RPAD(' ', 30) || RPAD(MI_NOMBRE, 64));

            MI_VALP := 0;
        END IF;
    END LOOP FONDOSNITAGRUPADOS;
    RETURN MI_RTAPLANO;
END FC_PLANOCONTASYSMANCONTABLE;

PROCEDURE PR_CALCULARCESANTIASBUCA(
    /*
    NAME              : PR_CALCULARCESANTIASALCTOCAN
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSÉ PASCUAL GÓMEZ BLANCO
    DATE MIGRADOR     : 19/12/2019
    TIME              :
    SOURCE MODULE     : NOMINAP2018.05.01_UNIFICADAS, En access = calcularcesantiasALCBUCA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  PR_CALCULARCESANTIASBUCA
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
    MI_EMPLEADOSENA         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
BEGIN

    PCK_NOMINA.GL_BASCES := 0;
    MI_FECHA := PCK_NOMINA.GL_FECHAINI;
     -- JM 7735009  05/04/2024 SE VERIFICA SI EL EMPLEADO ES SENA ANTES PARA EVITAR QUE SE LE INSERTE EL CONCEPTO 169 EN ENERO
    BEGIN
        SELECT COUNT(1)
        INTO   MI_EMPLEADOSENA
        FROM   
            ESCALAFON
        JOIN 
            PERSONAL 
        ON 
            ESCALAFON.COMPANIA = PERSONAL.COMPANIA AND
            ESCALAFON.CODIGO = PERSONAL.ESCALAFON
        WHERE  ESCALAFON.COMPANIA = UN_COMPANIA
        AND ESCALAFON.NOMBRE LIKE '%SENA%'
        AND PERSONAL.COMPANIA = UN_COMPANIA
        AND PERSONAL.ID_DE_EMPLEADO =  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;
    END;
    --(MZANGUNA:25/10/2018)-Se cambia GL_FECHAFIN a GL_FECHAINI
    IF NOT(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO < PCK_NOMINA.GL_FECHAINI) OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN --(MZANGUNA:17/12/2018)-Se agrega condicion si la fecha de retiro es nula
        PCK_NOMINA.GL_SBM := CASE WHEN PCK_NOMINA.FC_CN(900) = 0 THEN PCK_NOMINA.FC_CN(1) ELSE PCK_NOMINA.FC_CN(900) END;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN1) THEN
                PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
                PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
                PCK_NOMINA.GL_PERA := CASE WHEN PCK_SYSMAN_UTL.FC_DIA(UN_FECHA => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) < 16 
                                      THEN 1 ELSE 2 END;
                MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(UN_FECHAINI => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, 
                                                          UN_FECHAFIN => PCK_NOMINA.GL_FECHAFIN1);

            ELSE
                PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(UN_FECHA => PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
                PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(UN_FECHA => PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
                PCK_NOMINA.GL_PERA := CASE WHEN PCK_SYSMAN_UTL.FC_DIA(UN_FECHA => PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN)) < 16 THEN 1 ELSE 2 END;
                MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(UN_FECHAINI => PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN), 
                                                          UN_FECHAFIN => PCK_NOMINA.GL_FECHAFIN1);
            END IF;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA   => UN_COMPANIA, 
                                                   UN_ANO1       => PCK_NOMINA.GL_ANOA, 
                                                   UN_MES1       => PCK_NOMINA.GL_MESA, 
                                                   UN_PERIODO1   => 1, 
                                                   UN_ANO2       => PCK_NOMINA.GL_SANO,
                                                   UN_MES2       => PCK_NOMINA.GL_SMES - 1, 
                                                   UN_PERIODO2   => 99, 
                                                   UN_IDEMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
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
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA   => UN_COMPANIA, 
                                                   UN_ANO1       => PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR), 
                                                   UN_MES1       => PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR), 
                                                   UN_PERIODO1   => CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16 THEN 1 ELSE 2 END, 
                                                   UN_ANO2       => PCK_NOMINA.GL_SANO,
                                                   UN_MES2       => PCK_NOMINA.GL_SMES, 
                                                   UN_PERIODO2   => 99, 
                                                   UN_IDEMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) 
                                     + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) 
                                     + PCK_NOMINA.FC_CN(357)  + PCK_NOMINA.FC_CN(359) 
                                     + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN  => PCK_NOMINA.GL_FECHAIR, 
                                                          UN_FECHAFIN => PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
            --MI_DIAS := MI_DIAS + MASDIASOTRAENTIDAD;
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA  => UN_COMPANIA, 
                                                       UN_EMPLEADO  => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,
                                                       UN_FECHA1    => PCK_NOMINA.GL_FECHAIR, 
                                                       UN_FECHA2    => PCK_NOMINA.GL_FECHAFIN1);
            --DP := 360;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA   => UN_COMPANIA, 
                                                   UN_ANO1       => PCK_NOMINA.GL_SANO, 
                                                   UN_MES1       => 1, 
                                                   UN_PERIODO1   => 1, 
                                                   UN_ANO2       => PCK_NOMINA.GL_SANO,
                                                   UN_MES2       => 12, 
                                                   UN_PERIODO2   => 99, 
                                                   UN_IDEMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_FECHAIC := PCK_NOMINA.GL_FECHAIR;
        ELSE
            PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') 
                                          THEN PCK_NOMINA.GL_FECHAIR 
                                          ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
            PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > PCK_NOMINA.GL_FECHAIC 
                                           AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') 
                                          THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAFONDOCESANTIA 
                                          ELSE PCK_NOMINA.GL_FECHAIC END;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA   => UN_COMPANIA, 
                                                   UN_ANO1       => PCK_NOMINA.GL_SANO, 
                                                   UN_MES1       => PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC), 
                                                   UN_PERIODO1   => CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIC) < 16 THEN 1 ELSE 2 END, 
                                                   UN_ANO2       => PCK_NOMINA.GL_SANO,
                                                   UN_MES2       => PCK_NOMINA.GL_SMES, 
                                                   UN_PERIODO2   => 99, 
                                                   UN_IDEMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) 
                                     + PCK_NOMINA.FC_CN(356)  + PCK_NOMINA.FC_CN(357)  + PCK_NOMINA.FC_CN(359) 
                                     + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN  => PCK_NOMINA.GL_FECHAIC, 
                                                          UN_FECHAFIN => PCK_NOMINA.GL_FECHAFIN1) 
                     - PCK_NOMINA.GL_LICENCIAS;

            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA  => UN_COMPANIA, 
                                                       UN_EMPLEADO  => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,
                                                       UN_FECHA1    => PCK_NOMINA.GL_FECHAIC, 
                                                       UN_FECHA2    => PCK_NOMINA.GL_FECHAFIN1 - 10);
        END IF;
        MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN  => CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') 
                                                                             THEN PCK_NOMINA.GL_FECHAIR 
                                                                             ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END, 
                                                         UN_FECHAFIN => PCK_NOMINA.GL_FECHAFIN1) 
                     - PCK_NOMINA.GL_LICENCIAS ;
        IF PCK_NOMINA.FC_CNA(155) = 0 THEN
            PCK_NOMINA.GL_PVAC := 0 + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501) ;
        ELSE
            IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)), 0) + PCK_NOMINA.FC_CN(155);
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501);
            END IF;
        END IF;
        PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(UN_ID_EMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) 
                                 + PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) 
                                 + PCK_NOMINA.FC_CN(150)  + PCK_NOMINA.FC_CN(514)) / 12, 0) ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA   => UN_COMPANIA, 
                                                   UN_ANO1       => PCK_NOMINA.GL_SANO, 
                                                   UN_MES1       => 1, 
                                                   UN_PERIODO1   => 1, 
                                                   UN_ANO2       => PCK_NOMINA.GL_SANO,
                                                   UN_MES2       => PCK_NOMINA.GL_SMES, 
                                                   UN_PERIODO2   => 99, 
                                                   UN_IDEMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) 
                                                          + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(150) 
                                                          + PCK_NOMINA.FC_CN(514)  + PCK_NOMINA.FC_CN(538)) / 12, 0);

        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) 
                            + PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12,0);--(CFBARRERA_CC:5511_28-04-2025)   
        PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);       
        --(EAMAYA:09/09/2019)-Se cambia suma de concepto 70 por la sumatoria del rango de conceptos entre 49 y 60
        PCK_NOMINA.CN(902) := CASE WHEN PCK_NOMINA.FC_CN(902) = 0 
                                   THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_SUMACONA(49,60) 
                                                               + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(519) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CN(519)
                                                               + PCK_NOMINA.FC_CNA(508) + PCK_NOMINA.FC_CNA(546) + PCK_NOMINA.FC_CN(508) + PCK_NOMINA.FC_CN(546)) / 12, 0)                                
                                   ELSE PCK_NOMINA.FC_CN(902) 
                              END ;
        PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) 
                                                     + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) ;

        MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 
                           THEN PCK_NOMINA.FC_CN(969) 
                           ELSE CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 
                                     THEN PCK_NOMINA.FC_CN(10) 
                                     ELSE PCK_NOMINA.FC_CN(1) 
                                END 
                                + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA
                                + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_AUXT 
                                + PCK_NOMINA.FC_CN(906) + PCK_NOMINA.FC_CN(905) 
                                + PCK_NOMINA.FC_CN(907) + PCK_NOMINA.FC_CN(902) 
                                + PCK_NOMINA.GL_BONPAGADA 
                      END;
        MI_PROMFAC         := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC, 0);
        PCK_NOMINA.CN(969) := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC, 0);
        MI_DIASCES_ENC     := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(UN_CED       => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 
                                                                UN_PARAMETRO => 'D',
                                                                UN_FECHA_IC  => PCK_NOMINA.GL_FECHAIC, 
                                                                UN_FECHA_FC  => PCK_NOMINA.GL_FECHAFIN1);
        IF MI_DIASCES_ENC <> 0 THEN
            MI_PROM_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(UN_CED       => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 
                                                             UN_PARAMETRO => 'V', 
                                                             UN_FECHA_IC  => PCK_NOMINA.GL_FECHAIC, 
                                                             UN_FECHA_FC  => PCK_NOMINA.GL_FECHAFIN1);
            MI_ANTIC_ENC := PCK_NOMINA_CALCULO2.FC_CESANTIA_C(UN_CED       => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, 
                                                              UN_PARAMETRO => 'R', 
                                                              UN_FECHA_IC  => PCK_NOMINA.GL_FECHAIC, 
                                                              UN_FECHA_FC  => PCK_NOMINA.GL_FECHAFIN1);
            MI_DIAS := MI_DIAS - MI_DIASCES_ENC;
            IF MI_DIAS < 0 THEN
                MI_DIAS := 0;
            END IF;
        ELSE
            MI_PROM_ENC := 0;
            MI_ANTIC_ENC := 0;
        END IF;
        MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND((((MI_PROMFAC)) * MI_DIAS / 360), 0) 
                      + CASE WHEN MI_DIASCES_ENC > 0 
                             THEN MI_ANTIC_ENC 
                             ELSE 0 
                             END 
                      - MI_ANTICIPOS;
        IF PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.FC_CN(411) <> 0 THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 2 THEN
                PCK_NOMINA.CN(169) := CASE WHEN PCK_NOMINA.FC_CN(169) = 0 
                                           THEN CASE WHEN MI_CESANTIA1 < 0 
                                                     THEN 0 
                                                     ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) 
                                                END 
                                           ELSE PCK_NOMINA.FC_CN(169) 
                                      END;
            END IF;
            PCK_NOMINA.CN(177) := CASE WHEN PCK_NOMINA.FC_CN(177) = 0 
                                       THEN MI_CESANTIA1 
                                       ELSE PCK_NOMINA.FC_CN(177) 
                                  END;
        ELSIF PCK_NOMINA.FC_CN(412) <> 0 THEN
            IF NOT(PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996', 'DD/MM/YYYY') 
               AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1) THEN
                PCK_NOMINA.CN(269) := CASE WHEN PCK_NOMINA.FC_CN(269) = 0 
                                           THEN CASE WHEN MI_CESANTIA1 < 0 
                                                     THEN 0 
                                                     ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) 
                                                END 
                                           ELSE PCK_NOMINA.FC_CN(269) 
                                      END;
            END IF;
            PCK_NOMINA.CN(277) := CASE WHEN PCK_NOMINA.FC_CN(277) = 0 
                                       THEN MI_CESANTIA1 
                                       ELSE PCK_NOMINA.FC_CN(277) 
                                  END;
            IF PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = '12' AND MI_EMPLEADOSENA <> 1 THEN
                PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA => UN_COMPANIA, 
                                             UN_PROCESO  => 1, 
                                             UN_ANIO     => (PCK_NOMINA.GL_SANO + 1), 
                                             UN_MES      => 1, 
                                             UN_PERIODO  => 3, 
                                             UN_IDEMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,
                                             UN_IDCONCEPTO => 169, 
                                             UN_VALOR      => PCK_NOMINA.FC_CN(269),
                                             UN_USER       => PCK_CONEXION.FC_GETUSER());
            END IF;
        END IF;
        PCK_NOMINA.CN(900) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 
                                   THEN PCK_NOMINA.FC_CN(10) 
                                   ELSE PCK_NOMINA.FC_CN(1) 
                              END;
        PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
        PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
        PCK_NOMINA.CN(901) := PCK_NOMINA.GL_GRPNGV ;
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.CN(910) := MI_DIAS                                                    ;
        PCK_NOMINA.CN(911) := MI_ANTICIPOS                                               ;
        PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION                   ;
        PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0)                            ;
        PCK_NOMINA.CN(914) := PCK_NOMINA.GL_VPT ;
        PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN  => PCK_NOMINA.GL_FECHAI, 
                                                                 UN_FECHAFIN => PCK_NOMINA.GL_FECHAFIN1) + 1 ;
    END IF;
END PR_CALCULARCESANTIASBUCA;

FUNCTION FC_ACTACTIVIDADESCIIU(
/*
    NAME              : FC_ACTACTIVIDADESCIIU
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDWIN FERNANDO CABRERA MARTINEZ
    DATE MIGRADOR     : 23/06/2018
    TIME              : 08:58 AM
    SOURCE MODULE     : NOMINAP2018.06.05, Formulario Interface_Nomina, Boton aceptar.
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    PARAMETERS        :

    MODIFICATIONS     :

    @NAME:planoContabilizarNomina
    @METHOD:  GET
*/
     UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
     UN_ACTIVIDADES_CIIU    IN CLOB,
     UN_USUARIO             IN COMPROBANTE_CNT.CREATED_BY%TYPE
) RETURN CLOB

AS
    MI_RTA      CLOB;
    MI_AUX      CLOB;
    MI_POS      NUMBER;
    MI_SEPREG   VARCHAR2(10) := ',.REG.';
    MI_SEPCOL   VARCHAR2(10) := ',.COL.';
    MI_CONT     NUMBER;
    MI_CONT_OK  NUMBER := 0;
    MI_CONT_A   NUMBER(1);
    MI_CAD      VARCHAR2(4000);
    MI_NUMCOL   NUMBER(2) := 7;
    MI_ROWID    ROWID;
    
    TYPE MI_TYTABLE IS TABLE OF VARCHAR2(1000) INDEX BY BINARY_INTEGER;
    MI_TABLE    MI_TYTABLE;
    MI_MAXREG   NUMBER := 32000; -- POR SEGURIDAD PARA ARCHIVOS MANIPULADOS PARA EVITAR LOOP INFINITO
    
    MI_ID_CIIU  CIIU.ID_CIIU%TYPE;
BEGIN
    MI_AUX := UN_ACTIVIDADES_CIIU;
    MI_CONT := 0;
    LOOP
        MI_CONT := MI_CONT + 1;
        MI_POS := INSTR(MI_AUX,MI_SEPREG);
        MI_CAD := SUBSTR(MI_AUX,1,MI_POS-1);
        
        -- SEPARAR COLUMNAS DE CADA REGISTRO
        FOR I IN 1..MI_NUMCOL LOOP
            MI_TABLE(I) := SUBSTR(MI_CAD,1,(INSTR(MI_CAD,MI_SEPCOL))-1);
            MI_CAD := SUBSTR(MI_CAD,(INSTR(MI_CAD,MI_SEPCOL))+LENGTH(MI_SEPCOL)+1,LENGTH(MI_CAD));
        END LOOP;
        
        
        IF ( MI_TABLE(1) != '0' ) THEN
            IF ( UN_COMPANIA = MI_TABLE(1) ) THEN
                BEGIN
                    SELECT  ID_CIIU
                    INTO    MI_ID_CIIU
                    FROM    CIIU
                    WHERE   COMPANIA = UN_COMPANIA
                            AND ID_CIIU =  TO_NUMBER( MI_TABLE(5) || LPAD(MI_TABLE(6),4,'0') || LPAD(MI_TABLE(7),2,'0'))
                            AND ACTIVO != 0
                            ;
                            
                    BEGIN
                        SELECT  ROWID
                        INTO    MI_ROWID
                        FROM    PERSONAL
                        WHERE   COMPANIA = UN_COMPANIA
                                AND ID_DE_EMPLEADO = TO_NUMBER(MI_TABLE(2));
                                
                        BEGIN
                            BEGIN
                                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME   (
                                                                        UN_TABLA => 'PERSONAL', 
                                                                        UN_ACCION => 'M', 
                                                                        UN_CAMPOS => 'ID_CIIU=' || MI_ID_CIIU,
                                                                        UN_ROWID => MI_ROWID
                                                                        );
                                IF ( NVL(PCK_DATOS.GL_RTA,'0') != 0 ) THEN
                                    MI_CONT_OK := MI_CONT_OK + 1;
                                END IF;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                            END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
                            --MI_MSGERROR(1).CLAVE := 'PERIODO';
                            --MI_MSGERROR(1).VALOR := UN_ANO || '-' || UN_MES ;
                            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD =>SQLCODE, UN_ERROR_COD=>PCK_ERRORES.ER_NOMINA_DELETE_PHISTORICOS );
                        END;
                    EXCEPTION WHEN OTHERS THEN
                        MI_RTA := MI_RTA || 'LINEA ' || MI_CONT || ' - ' || 'Codigo de empleado no encontrado ' || MI_TABLE(2) || CHR(13) || CHR(10);
                    END;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_RTA := MI_RTA || 'LINEA ' || MI_CONT || ' - ' || 'Actividad CIIU no encontrada o inactiva ' || MI_TABLE(5) || LPAD(MI_TABLE(6),4,'0') || LPAD(MI_TABLE(7),2,'0') || CHR(13) || CHR(10);
                END;
            ELSE
                MI_RTA := MI_RTA || 'LINEA ' || MI_CONT || ' - ' || 'Companias no concuerdan ' || MI_TABLE(1) || CHR(13) || CHR(10);
            END IF;                
        END IF;
        MI_AUX := SUBSTR(MI_AUX,MI_POS+LENGTH(MI_SEPREG)+1,LENGTH(MI_AUX));
        
        IF ( MI_POS = 1 OR MI_POS = 0 OR MI_CONT >= MI_MAXREG) THEN
            MI_AUX := NULL;
        END IF;
        EXIT WHEN MI_AUX IS NULL;
    END LOOP;
    COMMIT;
    MI_RTA := 'REGISTROS ACTUALIZADOS CORRECTAMENTE : ' || MI_CONT_OK || CHR(13) || CHR(10) || CHR(13) || CHR(10)  || MI_RTA;
    RETURN MI_RTA;

END FC_ACTACTIVIDADESCIIU;

PROCEDURE PR_CALCPRIMANAVIDADTELEPAC(
  /*
    NAME              : PR_CALCULARPRIMANAVIDADTELEPAC
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : Brayan Sebastian Cardenas Aguilar
    DATE MIGRADOR     : 27/06/2023
    TIME              :
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  CALCULARPRIMANAVIDADTELEPACIFICO
    */

UN_COMPANIA    IN  PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_FECHAFPN         DATE;
    MI_TRANSPORTELEGAL  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_RETEFUENTE       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CLASIFICACION    VARCHAR2(10 CHAR);
    MI_SINDICATO        PCK_SUBTIPOS.TI_LOGICO;
    MI_SINDICATO2       PCK_SUBTIPOS.TI_LOGICO;
    MI_SINDICATO3       PCK_SUBTIPOS.TI_LOGICO;
    MI_VALOR            VARCHAR2(100 CHAR);
BEGIN
    PCK_NOMINA.GL_PVAC := 0;
    IF NOT(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11') THEN
        PCK_NOMINA.GL_FACTORPN := 0;
        PCK_NOMINA.GL_DNT := 0;
        --DNT1 := 0;
        PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPN := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN END;
        PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPN1 := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN1 THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN1 END;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO < PCK_NOMINA.GL_FECHAIPN AND PCK_NOMINA.FC_CN(155) = 0 THEN
            PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/' || PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN) || '/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_FECHAI := PCK_NOMINA.GL_FECHAIPN;
        END IF;
        IF PCK_NOMINA.GL_SMES = 12 AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) AND PCK_NOMINA.FC_CN(155) = 0 THEN
            PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        END IF;
        IF PCK_NOMINA.GL_SPER = 4 THEN
            PCK_NOMINA.CN(150) := 0;
        END IF;

        IF PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_FECHAI <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0)  ;
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO - 1, 12, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES - 1, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

            PCK_NOMINA.GL_PVAC := 0;
            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := 0;
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) ;
            END IF;
            IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.GL_SPER = 3 AND PCK_NOMINA.FC_CN(155) > 0 THEN
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501);
            END IF;

            PCK_NOMINA.CN(942) := 0;

            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);

            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.FC_CN(942) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) ;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN
                IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                END IF;
            END IF;
            --DCC1 := 0;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(937) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                END IF;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.FC_CN(404) <> 0 THEN
            MI_FECHAFPN := PCK_NOMINA.GL_FECHAR;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.CN(942) := 0 ;
            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := 0 + CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155)) ELSE 0 END;
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(155));
            END IF;
            --PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160)) / 12, 0); JM CC 4010
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503))/12,0); -- JM CC 410
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);            
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..(PCK_NOMINA.GL_SMES - CASE WHEN PCK_NOMINA.GL_SPER = 7 THEN 0 ELSE 1 END) LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    IF PCK_SYSMAN_UTL.FC_MES(MI_FECHAFPN) <> i AND PCK_SYSMAN_UTL.FC_DIA(MI_FECHAFPN) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(MI_FECHAFPN)) THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;

                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;

            PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - (PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357));
            PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS < 0 THEN 0 ELSE PCK_NOMINA.GL_DOCEAVAS END;
            PCK_NOMINA.GL_FACTORPN := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.FC_CN(942) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(931), 0) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_PVAC / 12), 0) + PCK_NOMINA.FC_CN(939), 0);
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(937) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                END IF;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAI <= TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
                IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                END IF;
            END IF;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := 0;
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501);
            END IF;
            PCK_NOMINA.CN(942) := 0 ;
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;

            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / 12 ;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            --DCC1 := 0;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;

                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(937) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                END IF;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;

        ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            /*IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
                IF PCK_NOMINA.GL_FECHAR > TO_DATE('30/11/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                END IF;
            END IF;*/
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := 0;
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155);
            END IF;
            PCK_NOMINA.CN(942) := 0 ;
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;
            IF PCK_NOMINA.FC_CN(155) > 0 THEN
               IF PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150) + PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
                    PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(155) / 12, 0);
                ELSE
                       PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(155) / 12, 0);
                END IF;
            END IF;
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / 12 ;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);

            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(937) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                END IF;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        END IF;
    END IF;

    IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(155) > 0 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CN(158) > 0 AND PCK_NOMINA.FC_CN(504) = 0 AND PCK_NOMINA.FC_CNA(158) <> 0 THEN
            PCK_NOMINA.CN(504) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
            PCK_NOMINA.CN(158) := 0;
        END IF;
    END IF;
    
    MI_CLASIFICACION := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;
    MI_SINDICATO := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO;
    MI_SINDICATO2 := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO2;
    MI_SINDICATO3 := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO3;
    
    IF MI_CLASIFICACION = '06' AND (MI_SINDICATO NOT IN (0) OR MI_SINDICATO2 NOT IN (0) OR MI_SINDICATO3 NOT IN (0)) AND PCK_NOMINA.FC_CN(404) = 0 THEN 
    PCK_NOMINA.CN(159) := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END / 30) * 3, 0);
    MI_VALOR := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END / 30) * 3;
    ELSE 
    PCK_NOMINA.CN(159) := 0;
    END IF;

    PCK_NOMINA.GL_PRIMADIC := PCK_NOMINA.FC_CN(158);
    --DIASPRIMADIC := PCK_NOMINA.GL_DOCEAVAS ;
    MI_TRANSPORTELEGAL := PCK_NOMINA.FC_CN(254);
    MI_RETEFUENTE := PCK_NOMINA.FC_CN(125);
    PCK_NOMINA.CN(930) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..599 LOOP
            IF (i <> 125) AND (i <> 159) AND (i < 599) AND (i <> 303) AND (i <> 301) AND (i <> 10) AND (i <> 300) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(125) := MI_RETEFUENTE;
    PCK_NOMINA.CN(254) := MI_TRANSPORTELEGAL;
    PCK_NOMINA.CN(158) := PCK_NOMINA.GL_PRIMADIC;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS ;
    PCK_NOMINA.CN(933) := PCK_NOMINA.GL_AUXT ;
    PCK_NOMINA.CN(934) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(936) := PCK_NOMINA.FC_CN(67) ;
    PCK_NOMINA.CN(937) := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.CN(935) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(940) := PCK_NOMINA.GL_VPT;
    PCK_NOMINA.CN(988) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPN), 0)                            ;
    PCK_NOMINA.GL_PN_DIAS := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.GL_PN_BASE := PCK_NOMINA.GL_FACTORPN;

END PR_CALCPRIMANAVIDADTELEPAC;

PROCEDURE PR_CALCULARCESANTIASMELGAR(
    /*
    NAME              : PR_CALCULARCESANTIASMELGAR
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 08/08/2025
    TIME              :
    SOURCE MODULE     : NOMINA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : PROCEDIMIENTO PARA EL CALCULO DE CESANTAS DE MELGAR CC1610
    @NAME:  CALCULARCESANTIASMELGAR
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
  MI_VALMAYORBASP         PCK_SUBTIPOS.TI_PARAMETRO;
BEGIN
  MI_VALMAYORBASP := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA,
                                                 UN_NOMBRE => 'TOMA MAYOR BASP EN CESANTIAS EN RETIRO',
                                                 UN_MODULO => PCK_DATOS.FC_MODULONOMINA,
                                                 UN_FECHA_PAR => SYSDATE),'NO');
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
            --DP := 360;
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
            PCK_NOMINA.GL_PVAC := 0 + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501) ;
        ELSE
            IF PCK_NOMINA.FC_CNA(164) > 1 THEN
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)), 0) + PCK_NOMINA.FC_CN(155) ;
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501);
            END IF;
        END IF;
        PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF MI_VALMAYORBASP = 'SI' THEN
            IF (PCK_NOMINA.FC_CN(150)) > (PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) THEN
                PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150))/12,0);
            ELSE
                PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)/12),0);
            END IF;
        ELSE
      IF PCK_NOMINA.FC_CNA(150) > 0 THEN
        PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538)) / 12, 0);
      ELSIF PCK_NOMINA.FC_CN(150) > 0 THEN
        PCK_NOMINA.GL_BONPAGADA := ((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12)     ;
        PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538)) / 12, 0);
      END IF;
    END IF;
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
        PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);       
        PCK_NOMINA.CN(902) := CASE WHEN PCK_NOMINA.FC_CN(902) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_SUMACONA(46,60) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(519)) / 12, 0) ELSE PCK_NOMINA.FC_CN(902) END ;
        PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) ;

        MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA
                    + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.FC_CN(906) + PCK_NOMINA.FC_CN(905) + PCK_NOMINA.FC_CN(907) + PCK_NOMINA.FC_CN(902) + PCK_NOMINA.GL_BONPAGADA END;
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
            IF NOT(PCK_NOMINA.GL_FECHAI < TO_DATE('21/11/1996', 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1) THEN
                PCK_NOMINA.CN(269) := CASE WHEN PCK_NOMINA.FC_CN(269) = 0 THEN CASE WHEN MI_CESANTIA1 < 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) END ELSE PCK_NOMINA.FC_CN(269) END;
            END IF;
            PCK_NOMINA.CN(277) := CASE WHEN PCK_NOMINA.FC_CN(277) = 0 THEN MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(277) END;
            IF PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = '12' THEN
                PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA, 1, (PCK_NOMINA.GL_SANO + 1), 1, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 169, PCK_NOMINA.FC_CN(269));
            END IF;
        END IF;

        PCK_NOMINA.CN(900) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
        PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
        PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
        PCK_NOMINA.CN(901) := PCK_NOMINA.GL_GRPNGV ;
    PCK_NOMINA.CN(908) := PCK_NOMINA.GL_BONPAGADA ;                        
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.CN(910) := MI_DIAS                                                    ;
        PCK_NOMINA.CN(911) := MI_ANTICIPOS                                               ;
        PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION                   ;
        PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0)                            ;
        PCK_NOMINA.CN(914) := PCK_NOMINA.GL_VPT ;
        PCK_NOMINA.CN(915) := PCK_NOMINA.GL_VPA ;
        PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1) + 1 ;
    END IF;

        --JM 29-04-2025 CC 657 NO CALCULAR DE NUEVO EL PERIODO 8 SI YA Se CALCULO EN EL 3 ó EN EL 7
    PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 177, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.FC_CNP(177) > 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 8 THEN 
        PCK_NOMINA.CN(269) := 0;
        PCK_NOMINA.CN(177) := 0;
        PCK_NOMINA.CN(169) := 0;
        PCK_NOMINA.CN(900) := 0;
        PCK_NOMINA.CN(903) := 0;
        PCK_NOMINA.CN(904) := 0;
        PCK_NOMINA.CN(905) := 0;
        PCK_NOMINA.CN(901) := 0;
        PCK_NOMINA.CN(908) := 0;
        PCK_NOMINA.CN(909) := 0;
        PCK_NOMINA.CN(910) := 0;
        PCK_NOMINA.CN(911) := 0;
        PCK_NOMINA.CN(912) := 0;
        PCK_NOMINA.CN(913) := 0;
        PCK_NOMINA.CN(914) := 0;
        PCK_NOMINA.CN(915) := 0;
        PCK_NOMINA.CN(973) := 0;
        PCK_NOMINA.CN(906) := 0;
        PCK_NOMINA.CN(907) := 0;
        PCK_NOMINA.CN(277) := 0;
        PCK_NOMINA.CN(490) := 0;
        PCK_NOMINA.CN(925) := 0;
        PCK_NOMINA.CN(946) := 0;
        PCK_NOMINA.CN(951) := 0;
        PCK_NOMINA.CN(960) := 0;
        PCK_NOMINA.CN(965) := 0;
        PCK_NOMINA.CN(966) := 0;
        PCK_NOMINA.CN(969) := 0;
        PCK_NOMINA.CN(973) := 0;
        PCK_NOMINA.CN(975) := 0;
        PCK_NOMINA.CN(981) := 0;
        PCK_NOMINA.CN(982) := 0;
        PCK_NOMINA.CN(985) := 0;
        IF PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = '12' THEN
                PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA, 1, (PCK_NOMINA.GL_SANO + 1), 1, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 169, 0);
        END IF;
    END IF; 

END PR_CALCULARCESANTIASMELGAR;

PROCEDURE PR_CALCPRIMASEMESTRAL_MELGAR(
/*
NAME              : PR_CALCPRIMASEMESTRAL_MELGAR
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
DATE MIGRADOR     : 08/08/2025
TIME              : 02:19 PM
SOURCE MODULE     : 
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       : PROCEDIMIENTO PARA CALCULO PRIMA DE SERVICIO PARA MELGAR
                    CC1610
@NAME:  CALCULARPRIMASEMESTRALALCMELGAR
*/
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)

AS
    MI_DOCEAVASMINIMASPS         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PSPAGADA                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PRIMAJUNIO                   PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_INDRETIR                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_MSG                          PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALOR                            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN
    MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1'));
    PCK_NOMINA.GL_FACTORPS := 0;
    PCK_NOMINA.GL_FACTORPS1 := 0;
    PCK_NOMINA.GL_DNT := 0;

    --(APINEDA:22/03/2019)-Se agregan validaciones faltantes para la fecha de inicio de la prima semestral debido a que no esta calculando el concepto 160 para liquidaciones finales. TAR 1000090732
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
        --(APINEDA:01/12/2018)-TAR1000087154 Se toma la fecha de ingreso como fecha de inicio cuando un funcionario se retira y en julio del aÃ±o anterior no habia cumplido 180 dias trabajados
        IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 THEN
            IF (PCK_NOMINA.GL_FECHAIPS > PCK_NOMINA.GL_FECHAI) AND (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAIPS) < 180) AND  PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) = 0 THEN --JM 27/03/2025 CC 1274
                PCK_NOMINA.GL_FECHAIPS := PCK_NOMINA.GL_FECHAI;
            END IF;
        END IF;
    END IF;
    --(APINEDA:22/03/2019)-Se agregan validaciones faltantes para la fecha de inicio de la prima semestral debido a que no esta calculando el concepto 160 para liquidaciones finales. TAR 1000090732
    IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 AND PCK_NOMINA.GL_SMES = 7  THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
    END IF;

    IF PCK_NOMINA.GL_SMES > 7 THEN
        PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    END IF;
    --(APINEDA:26/07/2019)-Se modifica CASE para asignar valor a la fecha final de la prima semestral debido a que estaba presentando inconsistencias.
    IF PCK_NOMINA.GL_SMES = 7 THEN
        PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN CASE WHEN PCK_NOMINA.GL_FECHAFIN1 > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
    ELSE
        PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
    END IF;
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    END IF;
    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
    IF (PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 4) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO >= PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO <= PCK_NOMINA.GL_FECHAFIN) THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
    ELSE
    IF (PCK_NOMINA.FC_CN(150)+PCK_NOMINA.FC_CN(514)) > (PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) THEN
            PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150)+PCK_NOMINA.FC_CN(514))/12, 0);
        ELSE
            PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO))/12, 0);
        END IF;
    END IF;
    IF PCK_NOMINA.FC_CN(946) = 0 AND PCK_NOMINA.FC_CN(514) <> 0 THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(514) / 12, 0);
    END IF;

    PCK_NOMINA.GL_FACTORPS := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT_C + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_GRPNGV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(946), 0);
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
    IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 7) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DOCEAVAS := 6;
        --DOCEAVASMINIMASPRIMASERVICIOS := 6;
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
    END IF;


    FOR i IN 7..12 LOOP
        PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
            IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_SMES = 7  THEN PCK_NOMINA.GL_DOCEAVAS ELSE PCK_NOMINA.GL_DOCEAVAS - 1 END;
            END IF;
            PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
        END IF;
    END LOOP;
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
            PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                    PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_SMES = 7  THEN PCK_NOMINA.GL_DOCEAVAS ELSE PCK_NOMINA.GL_DOCEAVAS - 1 END;
                END IF;
                PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
            END IF;
        END LOOP;
    END IF;
    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    IF (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_FECHAIPS > PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DCC := 0;
        PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));

    IF PCK_NOMINA.FC_CN(952) > 0 THEN
        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
        PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC / 30);
    END IF;
    IF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('16/06/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAIPS <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 THEN
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
    ELSIF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_DOCEAVAS >= 1 THEN
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
    ELSIF PCK_NOMINA.FC_CN(404) = 1 THEN
        MI_PSPAGADA := 0;
        IF PCK_NOMINA.GL_SMES = 6 OR PCK_NOMINA.GL_SMES = 7 THEN
            PCK_NOMINA.GL_ACS := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 160, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, 7, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_PSPAGADA := PCK_NOMINA.FC_CNP(160);
        END IF;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
        IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 6) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
            PCK_NOMINA.GL_DOCEAVAS := 6;
            --DOCEAVASMINIMASPRIMASERVICIOS := 6;
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
        ELSE
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
        END IF;
        IF PCK_NOMINA.GL_SPRC <> 99 THEN
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF ((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0) THEN
                    IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFPS) <> i AND PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFPS) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(PCK_NOMINA.GL_FECHAFPS)) THEN
                        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_SMES = 7  THEN PCK_NOMINA.GL_DOCEAVAS ELSE PCK_NOMINA.GL_DOCEAVAS - 1 END;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            FOR i IN 6..12 LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), i, 1, (PCK_NOMINA.GL_SANO - 1), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  --(MZANGUNA:25/10/2018)-Respete el proporcional de la prima cuando se retira.
                        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_SMES = 7  THEN PCK_NOMINA.GL_DOCEAVAS ELSE PCK_NOMINA.GL_DOCEAVAS - 1 END;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
        END IF;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(953);
        IF PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS > 12 THEN
            PCK_NOMINA.GL_DCC := 0;
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;
        IF PCK_NOMINA.FC_CN(952) > 0 THEN
            PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
            PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953) / 30);
        END IF;
        MI_VALOR:= PCK_NOMINA.GL_DOCEAVAS;
        IF (PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS) OR (PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CN(953)) < 179 AND PCK_NOMINA.GL_DOCEAVAS < MI_DOCEAVASMINIMASPS AND PCK_NOMINA.FC_CN(404) = 0 THEN
            IF PCK_NOMINA.GL_DOCEAVAS = 0 AND PCK_NOMINA.FC_CN(404) = 0 THEN
                PCK_NOMINA.GL_DCC := 0;
                PCK_NOMINA.GL_DOCEAVAS := 0;
                -- 'REVISAR PCK_NOMINA.GL_DOCEAVAS CAUSADAS, YA QUE NO CUMPLIO MÃ¿S DE 6 MESES RADICADO 201520160102642 01/06/15  A: ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES
                --Revisar Doceavas Causadas, ya que no cumplio MÃ¡s de 6 meses Radicado --RADICADO-- --> 01/06/15  a: --NOMBRES--.
                MI_MSG(1).CLAVE := 'NOMBRES';
                MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
                MI_MSG(2).CLAVE := 'RADICADO';
                MI_MSG(2).VALOR := '201520160102642';

                PCK_NOMINA_COM7.PR_ALERTA
                    (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                    ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_DOCEAVASCAUSADAS
                    ,UN_REEMPLAZOS   => MI_MSG
                    ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                    ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                    ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                    ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                    ,UN_USER         => PCK_CONEXION.FC_GETUSER
                    );
            END IF;
        END IF;
        --(APINEDA:22/03/2019)-Se agrega validaciÃ³n para que no se calcule prima semestral cuando el funcionario no ha cumplido 6 meses TAR 1000090732
        --(MZANGUNA:19/12/2019)-Se quita validaciÃ³n dado que ahora si se debe calcular con los dÃ­as que el empleado lleve
        /*IF PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFPS) < 6 THEN
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;*/

        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN

         /*05/09/2023 7734153*/
        IF PCK_NOMINA.GL_SMES >= 7 THEN
        PCK_NOMINA.CN(953) := 0;
        FOR i IN 7..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF ((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0) THEN
                    IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFPS) <> i AND PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFPS) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(PCK_NOMINA.GL_FECHAFPS)) THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                    MI_VALOR := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
        END LOOP;
         END IF;
         /*05/09/2023 7734153*/

            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            --(MZANGUNA:19/12/2019)-Se quita condiciÃ³n dado que tocancipa empieza a pagar la prima por dÃ­as.
            --IF (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.FC_CN(953)) >= 180 THEN
                MI_VALOR := PCK_NOMINA.FC_CN(160);
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.GL_SMES IN (7) OR PCK_NOMINA.GL_SMES  IN (6) AND PCK_NOMINA.FC_CNA(160) > 0 THEN CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END ELSE   CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160) END;
                MI_VALOR := PCK_NOMINA.FC_CN(160);
            --END IF;
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
        IF PCK_NOMINA.FC_CN(160) < 0 THEN
            PCK_NOMINA.CN(160) := 0;
        END IF;
        IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, '01', PCK_NOMINA.GL_SANO, i, '99', PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC - PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
            ELSE
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
            END IF;
        END IF;
        --(APINEDA:26/07/2019)-Se modifica condiciÃ³n debido a que esta descontando la prima semestral a persona que se retira en Julio
        IF MI_PSPAGADA > 0 AND PCK_NOMINA.FC_CN(160) > 0 AND PCK_NOMINA.GL_PROCESOACTUAL <> 99 AND PCK_NOMINA.GL_PERIODOACTUAL <> 7 THEN
            PCK_NOMINA.CN(160) := 0;
            --La prima Semestral ya debiÃ³ ser pagada, segÃºn datos histÃ³ricos, revise pagos. se eliminara Ã©ste concepto en el pago de liquidaciÃ³n a: --NOMEMPLEADO--, CÃ©dula No.--CEDULA--, Tipo: --TIPO--.
            MI_MSG(1).CLAVE := 'NOMEMPLEADO';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(2).CLAVE := 'CEDULA';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
            MI_MSG(3).CLAVE := 'TIPO';
            MI_MSG(3).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO;

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_PRIMASEMESTRALPAGA
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
        END IF;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS >= MI_DOCEAVASMINIMASPS THEN PCK_NOMINA.GL_DOCEAVAS ELSE 0 END;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        ELSE
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 12 * PCK_NOMINA.GL_DOCEAVAS / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
        END IF;
    END IF;
    MI_PRIMAJUNIO := PCK_NOMINA.FC_CN(160);
    MI_INDRETIR := PCK_NOMINA.FC_CN(404);
    PCK_NOMINA.CN(945) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..699 LOOP
            IF (i <> 125) AND (i <> 303) AND (i <> 301) AND (i <> 300) AND (i < 599) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(404) := MI_INDRETIR;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS;

    PCK_NOMINA.CN(160) := MI_PRIMAJUNIO;

    PCK_NOMINA.CN(947) := PCK_NOMINA.GL_VPT_C; --PCK_NOMINA.GL_VPT
    PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXT ;
    PCK_NOMINA.CN(949) := PCK_NOMINA.GL_AUXA ;
    PCK_NOMINA.CN(950) := PCK_NOMINA.GL_GRPNGV  ;
    PCK_NOMINA.CN(951) := PCK_NOMINA.FC_CN(67) ;
    PCK_NOMINA.CN(952) := PCK_NOMINA.GL_DCC ;
    PCK_NOMINA.CN(954) := PCK_NOMINA.GL_VPA   ;

    IF PCK_NOMINA.GL_SPRC = 99 THEN
        PCK_NOMINA.CN(991) := PCK_NOMINA.FC_CNPR(994) ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.CN(992) := PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503);
        PCK_NOMINA.CN(993) := PCK_NOMINA.FC_CN(160) ;
        PCK_NOMINA.CN(994) := PCK_NOMINA.FC_CN(992) - PCK_NOMINA.FC_CN(991) + PCK_NOMINA.FC_CN(993) ;
    END IF;

    PCK_NOMINA.CN(985) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS), 0) ;

    PCK_NOMINA.GL_PS_BASE := PCK_NOMINA.GL_FACTORPS;
    PCK_NOMINA.GL_PS_DIAS := PCK_NOMINA.GL_DCC;

END PR_CALCPRIMASEMESTRAL_MELGAR;

PROCEDURE PR_CALCULARPRIMANAVIDADMELG(
    /*
    NAME              : PR_CALCULARPRIMANAVIDADMELG
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 08/08/2025
    TIME              :
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : PROCEDIMIENTO PARA CALCULAR PRIMA DE NAVIDAD DE MELGAR CC1610
    @NAME:  CALCULARPRIMANAVIDADMELGAR
    */

UN_COMPANIA    IN  PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_FECHAFPN         DATE;
    MI_TRANSPORTELEGAL  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_RETEFUENTE       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN
    PCK_NOMINA.GL_PVAC := PCK_NOMINA_PROC01.FC_ULTPRIMA_VAC_CORTOLIMA (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);--(CFBARRERA:HU7802248-27-11-2024)
    IF NOT(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11') THEN
        PCK_NOMINA.GL_FACTORPN := 0;
        PCK_NOMINA.GL_DNT := 0;
        --DNT1 := 0;
        PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPN := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN END;
        PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPN1 := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN1 THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN1 END;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO < PCK_NOMINA.GL_FECHAIPN AND PCK_NOMINA.FC_CN(155) = 0 THEN
            PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/' || PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN) || '/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_FECHAI := PCK_NOMINA.GL_FECHAIPN;
        END IF;
        IF PCK_NOMINA.GL_SMES = 12 AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) AND PCK_NOMINA.FC_CN(155) = 0 THEN
            PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        END IF;
        IF PCK_NOMINA.GL_SPER = 4 THEN
            PCK_NOMINA.CN(150) := 0;
        END IF;

        IF PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_FECHAI <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0)  ;
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

            
            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := PCK_NOMINA_PROC01.FC_ULTPRIMA_VAC_CORTOLIMA (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);--(CFBARRERA:HU7802248-27-11-2024)
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) ;
            END IF;
            IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.GL_SPER = 3 AND PCK_NOMINA.FC_CN(155) > 0 THEN
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501);
            END IF;

            PCK_NOMINA.CN(942) := 0;

            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);

            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.FC_CN(942) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_VPA + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) ;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN
                IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                END IF;
            END IF;
            --DCC1 := 0;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(937) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                END IF;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.FC_CN(404) <> 0 THEN
            MI_FECHAFPN := PCK_NOMINA.GL_FECHAR;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.CN(942) := 0 ;
            /*IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := 0 + CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 THEN PCK_NOMINA.FC_CN(155) ELSE 0 END;
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155);
            END IF;*/
            IF (PCK_NOMINA.FC_CN(160)) > (PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) THEN
                PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160))/12, 0);
            ELSE
                PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO))/12, 0);    
            END IF;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12), 0);
            --(EAMAYA:25/09/2019)-Se adcionana el acumulado del concepto 514
            IF (PCK_NOMINA.FC_CN(150)+PCK_NOMINA.FC_CN(514)) > (PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) THEN
                PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150)+PCK_NOMINA.FC_CN(514))/12, 0);
            ELSE
                PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO))/12, 0);
            END IF;
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            --DCC1 := 0;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..(PCK_NOMINA.GL_SMES - CASE WHEN PCK_NOMINA.GL_SPER = 7 THEN 0 ELSE 1 END) LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    IF PCK_SYSMAN_UTL.FC_MES(MI_FECHAFPN) <> i AND PCK_SYSMAN_UTL.FC_DIA(MI_FECHAFPN) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(MI_FECHAFPN)) THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;

                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;

            PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - (PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357));
            PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS < 0 THEN 0 ELSE PCK_NOMINA.GL_DOCEAVAS END;
            PCK_NOMINA.GL_FACTORPN := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.FC_CN(942) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_VPA + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(931), 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(932), 0) + PCK_NOMINA.FC_CN(939), 0);
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(937) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                END IF;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAI <= TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
                IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                END IF;
            END IF;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := PCK_NOMINA_PROC01.FC_ULTPRIMA_VAC_CORTOLIMA (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);--(CFBARRERA:HU7802248-27-11-2024)
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501);
            END IF;
            PCK_NOMINA.CN(942) := 0 ;
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;

            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / 12 ;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + + PCK_NOMINA.GL_VPA + PCK_NOMINA.FC_CN(931)  + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            --DCC1 := 0;
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;

                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(937) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                END IF;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;

        ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
            MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

            IF PCK_NOMINA.FC_CNA(155) = 0 THEN
                PCK_NOMINA.GL_PVAC := PCK_NOMINA_PROC01.FC_ULTPRIMA_VAC_CORTOLIMA (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);--(CFBARRERA:HU7802248-27-11-2024)
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155);
            END IF;
            PCK_NOMINA.CN(942) := 0 ;
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;

            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0) / 12 ;
            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);

            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_VPA + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(937) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                    PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                END IF;
                IF PCK_NOMINA.FC_CN(158) = 0 THEN
                    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                END IF;
            END IF;
        END IF;
    END IF;

    IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(155) > 0 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CN(158) > 0 AND PCK_NOMINA.FC_CN(504) = 0 AND PCK_NOMINA.FC_CNA(158) <> 0 THEN
            PCK_NOMINA.CN(504) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
            PCK_NOMINA.CN(158) := 0;
        END IF;
    END IF;

    PCK_NOMINA.GL_PRIMADIC := PCK_NOMINA.FC_CN(158);
    MI_TRANSPORTELEGAL := PCK_NOMINA.FC_CN(254);
    MI_RETEFUENTE := PCK_NOMINA.FC_CN(125);
    PCK_NOMINA.CN(930) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..599 LOOP
            IF (i <> 125) AND (i < 599) AND (i <> 303) AND (i <> 301) AND (i <> 10) AND (i <> 300) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;
    PCK_NOMINA.CN(125) := MI_RETEFUENTE;
    PCK_NOMINA.CN(254) := MI_TRANSPORTELEGAL;
    PCK_NOMINA.CN(158) := PCK_NOMINA.GL_PRIMADIC;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS ;
    PCK_NOMINA.CN(933) := PCK_NOMINA.GL_AUXT ;
    PCK_NOMINA.CN(934) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(936) := PCK_NOMINA.FC_CN(67) ;
    PCK_NOMINA.CN(937) := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.CN(935) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(940) := PCK_NOMINA.GL_VPT;
    PCK_NOMINA.CN(943) := PCK_NOMINA.GL_VPA;
    
    PCK_NOMINA.CN(988) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPN), 0)                            ;
    PCK_NOMINA.GL_PN_DIAS := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.GL_PN_BASE := PCK_NOMINA.GL_FACTORPN;
END PR_CALCULARPRIMANAVIDADMELG;

END PCK_NOMINA_COM8;