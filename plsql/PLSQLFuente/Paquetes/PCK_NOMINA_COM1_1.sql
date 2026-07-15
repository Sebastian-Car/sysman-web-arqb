create or replace PACKAGE BODY "PCK_NOMINA_COM1" AS

--1
FUNCTION FC_RELACIONADO 
/*
    NAME              : FC_RELACIONADO  --> EN ACCESS Relacionado  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ�GUEZ
    DATE MIGRADOR     : 09/07/2015
    TIME              : 09:11 PM 
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : DIEGO ALFREDO SUESCA RODRIGUEZ \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : 09/07/2015\ 07/02/2017
    TIME              : 2:30 PM
    DESCRIPTION       : RETORNA EL CONCEPTO RELACIONADO TABLA CONCEPTOS A PARTIR DE SU ID_DE_CONCEPTO
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getConceptoRelacionado
    @METHOD           : GET
  */
(
  UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA       ,  
  UN_CONCEPTO IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO
)
RETURN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO AS
  RESULTADO      NUMBER:= 0;
BEGIN
  BEGIN
    SELECT NVL(CONCEPTOS.RELACIONADO,0)  
      INTO RESULTADO 
      FROM CONCEPTOS 
     WHERE CONCEPTOS.COMPANIA = UN_COMPANIA 
       AND ID_DE_CONCEPTO     = UN_CONCEPTO;
  EXCEPTION WHEN NO_DATA_FOUND THEN
      RESULTADO:=0;
  END;  
  RETURN RESULTADO;
END FC_RELACIONADO; 

--2
FUNCTION FC_CONCEPTORELACIONADO  
/*
    NAME              : FC_CONCEPTORELACIONADO  --> EN ACCESS ConceptoRelacionado  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRIGUEZ
    DATE MIGRADOR     : 09/07/2015
    TIME              : 09:11 PM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : DIEGO ALFREDO SUESCA RODRÃ�GUEZ\ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : 09/07/2015\ 07/02/2017
    TIME              : 4:30 PM
    DESCRIPTION       : RETORNA EL VALOR DEL CONCEPTOS A PARTIR DE SU ID_DE_CONCEPTO
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getValorConcepto
    @METHOD           : GET
*/
   (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA       DEFAULT GL_COMPANIA,
    UN_PROCESO    IN PCK_SUBTIPOS.TI_ID_DE_PROCESO  DEFAULT GL_PROCESOACTUAL, 
    UN_ANO        IN PCK_SUBTIPOS.TI_ANIO           DEFAULT GL_ANOACTUAL, 
    UN_MES        IN PCK_SUBTIPOS.TI_MES            DEFAULT GL_MESACTUAL,
    UN_PERIODO    IN PCK_SUBTIPOS.TI_PERIODO_NOMI   DEFAULT GL_PERIODOACTUAL,
    UN_EMPLEADO   IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_CONCEPTO   IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO
  )
RETURN NUMBER AS
   -- MI_ERROR_FUN  NUMBER:= GL_ERROR_NUM + 2; 
    MI_RESULTADO  NUMBER:= 0;
BEGIN
 BEGIN
   SELECT NVL(HISTORICOS.VALOR,0) 
     INTO MI_RESULTADO 
     FROM HISTORICOS 
    WHERE HISTORICOS.COMPANIA = UN_COMPANIA 
      AND ID_DE_PROCESO       = UN_PROCESO
      AND HISTORICOS.ANO      = UN_ANO 
      AND HISTORICOS.MES      = UN_MES 
      AND HISTORICOS.PERIODO  = UN_PERIODO
      AND ID_DE_EMPLEADO      = UN_EMPLEADO 
      AND ID_DE_CONCEPTO      = LPAD(FC_RELACIONADO(UN_COMPANIA,UN_CONCEPTO),3,'0');
 EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_RESULTADO:=0;
 END; 
 RETURN MI_RESULTADO;
 END  FC_CONCEPTORELACIONADO;

--3
FUNCTION FC_SALARIOBASEHISTORICO
/*
    NAME              : FC_SALARIOBASEHISTORICO  --> EN ACCESS SALARIOBASEHISTORICO  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ�GUEZ
    DATE MIGRADOR     : 09/07/2015
    TIME              : 09:11 PM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : DIEGO ALFREDO SUESCA RODRÃ�GUEZ\ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : 09/07/2015\ 07/02/2017
    TIME              : 4:30 PM
    DESCRIPTION       : RETORNA EL VALOR DEL CONCEPTOS A PARTIR DE SU ID_DE_CONCEPTO
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getSalarioBasico
    @METHOD           : GET
*/
  ( 
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA       DEFAULT GL_COMPANIA, 
    UN_PROCESO  IN PCK_SUBTIPOS.TI_ID_DE_PROCESO  DEFAULT GL_PROCESOACTUAL, 
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO           DEFAULT GL_ANOACTUAL,
    UN_MES      IN PCK_SUBTIPOS.TI_MES            DEFAULT GL_MESACTUAL,
    UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI   DEFAULT GL_PERIODOACTUAL,
    UN_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  )
RETURN NUMBER
AS
  MI_RESULTADO PCK_SUBTIPOS.TI_ENTERO_LARGO:=0;
BEGIN
 BEGIN
  SELECT NVL(HISTORICOS.VALOR,0) 
    INTO MI_RESULTADO 
    FROM HISTORICOS 
   WHERE HISTORICOS.COMPANIA       = UN_COMPANIA 
     AND HISTORICOS.ID_DE_PROCESO  = UN_PROCESO 
     AND HISTORICOS.ANO            = UN_ANO 
     AND HISTORICOS.MES            = UN_MES 
     AND HISTORICOS.PERIODO        = UN_PERIODO 
     AND HISTORICOS.ID_DE_EMPLEADO = UN_EMPLEADO
     AND HISTORICOS.ID_DE_CONCEPTO = '001';
  EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_RESULTADO:=0; 
  END;
  RETURN MI_RESULTADO;
END  FC_SALARIOBASEHISTORICO;

--4
FUNCTION FC_MINOVEDAD
/*
    NAME              : FC_MINOVEDAD  --> EN ACCESS miNovedad  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ�GUEZ
    DATE MIGRADOR     : 10/07/2015
    TIME              : 2:11 PM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : DIEGO ALFREDO SUESCA RODRÃ�GUEZ\ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : Retorna un String para desprendibles de nomina que especifica los datoos de un empleado en palabras.
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getMiNovedad
    @METHOD           : GET
*/
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA       DEFAULT GL_COMPANIA,
    UN_PROCESO  IN PCK_SUBTIPOS.TI_ID_DE_PROCESO  DEFAULT GL_PROCESOACTUAL, 
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO           DEFAULT GL_ANOACTUAL,
    UN_MES      IN PCK_SUBTIPOS.TI_MES            DEFAULT GL_MESACTUAL,
    UN_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
	UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI   DEFAULT GL_PERIODOACTUAL																	  
  )
RETURN VARCHAR2
  AS
    MI_RESULTADO  VARCHAR2(200);
    MI_NOVEDAD    VARCHAR(2000);
    MI_CARGO_ENCARGO VARCHAR2(100);
BEGIN
  <<RECORRENOVEDADES>>
  FOR RS IN (SELECT *
             FROM (SELECT  HISTORICOS.ID_DE_EMPLEADO
                          ,HISTORICOS.ID_DE_CONCEPTO CN
                          ,NVL(HISTORICOS.VALOR,0)   VALOR
                   FROM HISTORICOS 
                     INNER JOIN PERIODOS 
                       ON  HISTORICOS.COMPANIA      = PERIODOS.COMPANIA
                       AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                       AND HISTORICOS.ANO           = PERIODOS.ANO
                       AND HISTORICOS.MES           = PERIODOS.MES
                       AND HISTORICOS.PERIODO       = PERIODOS.PERIODO
                   WHERE  HISTORICOS.COMPANIA       = UN_COMPANIA
                     AND  HISTORICOS.ID_DE_PROCESO  = UN_PROCESO
                     AND  HISTORICOS.ANO            = UN_ANO
                     AND  HISTORICOS.MES            = UN_MES
                     AND  HISTORICOS.PERIODO        BETWEEN '01' AND '99'  
                     AND  HISTORICOS.ID_DE_EMPLEADO = UN_EMPLEADO
                     AND  HISTORICOS.ID_DE_CONCEPTO IN (  35,350
                                                        ,353,356
                                                        ,357,359
                                                        ,98,99
                                                        ,480,482
                                                        ,134,112
                                                        ,108,110
                                                        ,125,212
                                                        ,111,302
                                                        ,304,348
                                                        ,351,352
                                                        ,354,355
                                                        ,343,300
                                                        ,301,209)

                     AND  PERIODOS.ACUMULADO        NOT IN (0)  
                   ) H
             PIVOT (SUM(VALOR) 
              FOR CN IN (  35 AS C35 ,350 AS C350
                         ,353 AS C353,356 AS C356
                         ,357 AS C357,359 AS C359
                         , 98 AS C98,  99 AS C99
                         ,480 AS C480,482 AS C482
                         ,134 AS C134,112 AS C112
                         ,108 AS C108,110 AS C110
                         ,125 AS C125,212 AS C212
                         ,111 AS C111,302 AS C302
                         ,304 AS C304,348 AS C348
                         ,351 AS C351,352 AS C352
                         ,354 AS C354,355 AS C355
                         ,343 AS C343,300 AS C300
                         ,301 AS C301,209 AS C209)))
  LOOP
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => FC_SALARIOBASE(
                                                               UN_COMPANIA => UN_COMPANIA
                                                              ,UN_EMPLEADO => UN_EMPLEADO)>0
                                             ,UN_SI        =>'Salario Base COT: ' || 
                                                             TO_CHAR(FC_SALARIOBASE(
                                                                      UN_COMPANIA => UN_COMPANIA
                                                                     ,UN_EMPLEADO => UN_EMPLEADO)
                                                                     ,'999,999,999.00')
                                             ,UN_NO        =>' ');
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF( 
                                              UN_CONDICION => RS.C35 > 0
                                             ,UN_SI        => ' Vacaciones, ' || RS.C35 
                                                              || ' días entre: ' 
                                                              || FC_MISVACACIONES(
                                                                   UN_COMPANIA => UN_COMPANIA
                                                                  ,UN_PROCESO  => UN_PROCESO
                                                                  ,UN_ANO      => UN_ANO
                                                                  ,UN_MES      => UN_MES
                                                                  ,UN_EMPLEADO => UN_EMPLEADO
                                                                  ,UN_OPCION   => 'FI'
																  ,UN_PERIODO  => UN_PERIODO)
                                                              ||' y ' 
                                                              || FC_MISVACACIONES(
                                                                   UN_COMPANIA => UN_COMPANIA
                                                                  ,UN_PROCESO  => UN_PROCESO
                                                                  ,UN_ANO      => UN_ANO
                                                                  ,UN_MES      => UN_MES
                                                                  ,UN_EMPLEADO => UN_EMPLEADO
                                                                  ,UN_OPCION   => 'FF'
																  ,UN_PERIODO  => UN_PERIODO)
                                             ,UN_NO        => ' ');
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION =>   NVL(RS.C350,0) + NVL(RS.C351,0) 
                                                              + NVL(RS.C352,0) + NVL(RS.C354,0) 
                                                              + NVL(RS.C355,0) > 0
                                             ,UN_SI        => '/Incapacidad, IBC Incap.= ' 
                                                              || TO_CHAR(RS.C348, '999,999,999.90')
                                             ,UN_NO        => ' ');
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => RS.C353 > 0
                                             ,UN_SI        => ' IBC Licencia de Maternidad = ' 
                                                              || TO_CHAR(RS.C343, '999,999,999.90')
                                             ,UN_NO        => ' ');
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => RS.C356 + RS.C357 + RS.C359 > 0
                                             ,UN_SI        => '/Licencia No Remunerada, '
                                             ,UN_NO        => ' ');
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => RS.C98 > 0
                                             ,UN_SI        => '/Interrupción de Vacaciones, '
                                             ,UN_NO        => ' ');
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => RS.C99 > 0
                                             ,UN_SI        => '/Disfrutando de ' || RS.C99 
                                                              || ' días Pendiente de Vacaciones, '
                                             ,UN_NO        => ' ');
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => RS.C480 > 0
                                             ,UN_SI        => '/Saldo Plan Vivienda = ' 
                                                              || TO_CHAR(RS.C480, '999,999,999.90') || ' '
                                             ,UN_NO        => ' ');
    /*MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => RS.C482 > 0
                                             ,UN_SI        => '/Saldo Servicio Médico = ' 
                                                              || TO_CHAR(RS.C482, '999,999,999.90') || ' '
                                             ,UN_NO        => ' '); */ --JM 23/07/2024
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => RS.C134 > 0
                                             ,UN_SI        => '/IBL: ' 
                                                              || TO_CHAR(RS.C134, '999,999,999.90') || ' '
                                             ,UN_NO        => ' ');
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => RS.C112 > 0
                                             ,UN_SI        => '/IBC: ' 
                                                              || TO_CHAR(RS.C112, '999,999,999.90') || ' '
                                             ,UN_NO        => ' ');
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => FC_PARAMETRO(
                                                                UN_COMPANIA => UN_COMPANIA
                                                               ,UN_OPCION   => 31) = '890701933-4' 
                                                              AND UN_COMPANIA = '002'
                                             ,UN_SI        => ' Si es Pensionado y gana menos del mínimo se descuentos 66.732 del 12% al empleado '
                                             ,UN_NO        => ' ');
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => RS.C108 > 0
                                             ,UN_SI        => '/IBP: ' 
                                                              || TO_CHAR(RS.C108, '999,999,999.90') || ''
                                             ,UN_NO        => ' ');
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => RS.C125 > 0
                                             ,UN_SI        => '/IBR: ' 
                                                              || TO_CHAR(RS.C110, '999,999,999.90') || ''
                                             ,UN_NO        => ' ');
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => RS.C125 > 0 
                                                              AND (RS.C300 + RS.C301) > 0
                                             ,UN_SI        => '/DED. APLICADO: ' 
                                                              || TO_CHAR(RS.C300 + RS.C301, '999,999,999.90') 
                                                              || ''
                                             ,UN_NO        => ' ');
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => RS.C212 > 0
                                             ,UN_SI        => '/IBC ARP: ' 
                                                              || TO_CHAR(RS.C212, '999,999,999.90') 
                                                              || ' ' 
                                                              || RS.C209 
                                                              || ' días '
                                             ,UN_NO        => ' ');
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => RS.C111 > 0
                                             ,UN_SI        => '/Aporte Riegos Prof.: ' 
                                                              || TO_CHAR(RS.C111, '999,999,999.90') 
                                                              || ' = ' 
                                                              || TO_CHAR(PCK_NOMINA_SEGSOCI.FC_PORCENRIESGO(
                                                                                     UN_COMPANIA   => UN_COMPANIA
                                                                                    ,UN_IDEMPLEADO => UN_EMPLEADO),'999.999') 
                                                              || '% '
                                             ,UN_NO        => ' ');
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => RS.C302 > 0
                                             ,UN_SI        => '/Beneficio Tributario.: ' 
                                                              || TO_CHAR(RS.C302, '999,999,999.90')
                                             ,UN_NO        => ' ');
    MI_NOVEDAD:= MI_NOVEDAD || PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => RS.C304 > 0
                                             ,UN_SI        => '/Deducible Salud Aplicado.: ' 
                                                              || TO_CHAR(RS.C304, '999,999,999')
                                             ,UN_NO        => ' ');
    MI_NOVEDAD:= MI_NOVEDAD 
                 || ' RESOLUCION INGRESO No. ' 
                 || FC_MIRESOLUCION(
                      UN_COMPANIA => UN_COMPANIA
                     ,UN_EMPLEADO => UN_EMPLEADO 
                     ,UN_OPCION   => 3);
    BEGIN
         SELECT PERSONAL.CARGO_ENCARGO 
         INTO MI_CARGO_ENCARGO 
         FROM PERSONAL 
         WHERE PERSONAL.COMPANIA      = UN_COMPANIA 
          AND PERSONAL.ID_DE_EMPLEADO = UN_EMPLEADO;
        MI_NOVEDAD:= MI_NOVEDAD || MI_CARGO_ENCARGO;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_CARGO_ENCARGO :='';
    END;             
  END LOOP RECORRENOVEDADES;

  RETURN MI_NOVEDAD;
END FC_MINOVEDAD;

--5
FUNCTION FC_MISVACACIONES
/*
    NAME              : FC_MISVACACIONES  --> EN ACCESS misVacaciones  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ�GUEZ
    DATE MIGRADOR     : 09/07/2015
    TIME              : 09:11 PM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : DIEGO ALFREDO SUESCA RODRÃ�GUEZ\ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : 09/07/2015\ 07/02/2017
    TIME              : 4:30 PM
    DESCRIPTION       : Rertorna un campo de la tabla Vacaciones de acuerdo al parametro UN_OPCION
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getMisVacaciones
    @METHOD           : GET
  */
  (  
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA       DEFAULT GL_COMPANIA, 
    UN_PROCESO  IN PCK_SUBTIPOS.TI_ID_DE_PROCESO  DEFAULT GL_PROCESOACTUAL, 
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO           DEFAULT GL_ANOACTUAL,
    UN_MES      IN PCK_SUBTIPOS.TI_MES            DEFAULT GL_MESACTUAL,
    UN_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_OPCION   IN VARCHAR2,
    UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI   DEFAULT GL_PERIODOACTUAL																		  
  )
RETURN VARCHAR2
AS 
  MI_RESULTADO       VARCHAR2(30 CHAR):=' ';
  MI_DIAS            VACACIONES.DIAS%TYPE;
  MI_DIASHABILES     VACACIONES.DIASHABILES%TYPE;
  MI_DIASDINERO      VACACIONES.DIASDINERO%TYPE;
  MI_INICIO_DISFRUTE VACACIONES.INICIO_DISFRUTE%TYPE;
  MI_FINAL_DISFRUTE  VACACIONES.FINAL_DISFRUTE%TYPE;
  CURSOR MI_CURSOR IS
    SELECT DIAS
          ,DIASHABILES
          ,DIASDINERO
          ,INICIO_DISFRUTE
          ,FINAL_DISFRUTE 
    FROM VACACIONES
    WHERE VACACIONES.ID_DE_PROCESO = UN_PROCESO 
		  AND INICIO_DISFRUTE <= TO_DATE(PCK_NOMINA.FC_FECHAINIFINPERIODO(UN_COMPANIA  => UN_COMPANIA
                                                                         ,UN_PROCESO   => UN_PROCESO
                                                                         ,UN_ANIO      => UN_ANO
                                                                         ,UN_MES       => UN_MES
                                                                         ,UN_PERIODO   => UN_PERIODO
                                                                         ,UN_FECHAINICIO => 2), 'DD/MM/YYYY HH24:MI:SS')
          AND FINAL_DISFRUTE >= TO_DATE(PCK_NOMINA.FC_FECHAINIFINPERIODO(UN_COMPANIA  => UN_COMPANIA
                                                                        ,UN_PROCESO   => UN_PROCESO
                                                                        ,UN_ANIO      => UN_ANO
                                                                        ,UN_MES       => UN_MES
                                                                        ,UN_PERIODO   => UN_PERIODO
                                                                        ,UN_FECHAINICIO => 1), 'DD/MM/YYYY HH24:MI:SS')
          AND ID_DE_EMPLEADO = UN_EMPLEADO
          AND COMPANIA = UN_COMPANIA
    ORDER BY ANO DESC, MES DESC;
	
BEGIN  
	OPEN MI_CURSOR;
	FETCH MI_CURSOR INTO MI_DIAS
                        ,MI_DIASHABILES
                        ,MI_DIASDINERO
                        ,MI_INICIO_DISFRUTE
                        ,MI_FINAL_DISFRUTE ;
	CLOSE MI_CURSOR;

	IF UN_OPCION      = 'C'  THEN
		MI_RESULTADO:=  NVL(MI_DIAS, 0);
	ELSIF UN_OPCION   = 'H'  THEN
		MI_RESULTADO:=  NVL(MI_DIASHABILES, 0);
	ElSIF UN_OPCION   = 'D'  THEN
		MI_RESULTADO:=  NVL(MI_DIASDINERO, 0);
	ElSIF UN_OPCION   = 'V'  THEN
		MI_RESULTADO:=  NVL(MI_DIASDINERO, 0) + NVL(MI_DIASHABILES, 0);
	ElSIF UN_OPCION   = 'FI' THEN
		MI_RESULTADO:=  TO_CHAR(NVL(TO_CHAR(MI_INICIO_DISFRUTE,'DD/MM/YYYY'),0));
	ELSIF UN_OPCION   = 'FF' THEN
		MI_RESULTADO:=  TO_CHAR(NVL(TO_CHAR(MI_FINAL_DISFRUTE,'DD/MM/YYYY'),0));
	ELSE  MI_RESULTADO:= 'ERROR';
	END IF;
	RETURN MI_RESULTADO;
END FC_MISVACACIONES;

--6
FUNCTION FC_SALARIOBASE
/*
    NAME              : FC_SALARIOBASE  --> EN ACCESS MisalarioBase  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ�GUEZ
    DATE MIGRADOR     : 13/07/2015
    TIME              : 11:17 AM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : Rertorna el valor del campo SALARIO_BASE_IBC de la tabla PERSONAL de un empleado.
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getSalarioBase
    @METHOD           : GET
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA       DEFAULT GL_COMPANIA,
    UN_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  )
RETURN NUMBER
AS 
    UN_RESULTADO  PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN 
  BEGIN 
    SELECT NVL(PERSONAL.SALARIO_BASE_IBC,0) 
      INTO UN_RESULTADO
      FROM PERSONAL 
     WHERE PERSONAL.COMPANIA         = UN_COMPANIA
       AND PERSONAL.ID_DE_EMPLEADO   = UN_EMPLEADO;
  EXCEPTION WHEN NO_DATA_FOUND THEN 
    RAISE PCK_EXCEPCIONES.EXC_NOMINA;
  END;  

   RETURN   UN_RESULTADO;

EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD    => SQLCODE,
        UN_ERROR_COD  => PCK_ERRORES.ERRR_NOMINA_SALARIO_BASE_IBC,-- InterrupciÃ³n Obteniendo SALARIO_BASE_IBC
        UN_TABLAERROR => 'PERSONAL'
      ); 


END FC_SALARIOBASE;

--7
FUNCTION FC_PARAMETRO    ---- ESTA FUNCION SE DEBE ELIMINAR
/*
    NAME              : FC_PARAMETRO  --> EN ACCESS parametro  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ�GUEZ
    DATE MIGRADOR     : 14/07/2015
    TIME              : 11:17 AM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : Retorna el valor de un registro de la tabla 'parametro de entrada', segÃƒÂºn el parametro ingresado UN_OPCION 
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getParametroNomina
    @METHOD           : GET
  */
(
  UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA DEFAULT GL_COMPANIA,
  UN_OPCION   IN PCK_SUBTIPOS.TI_ENTERO    
)
RETURN VARCHAR2
AS
  RESULTADO     VARCHAR2(50):=' ';
  MI_ROW        PARAMETROS_DE_ENTRADA%ROWTYPE;  
  CURSOR MI_CURSOR IS SELECT *
                        FROM PARAMETROS_DE_ENTRADA
                       WHERE PARAMETROS_DE_ENTRADA.COMPANIA = UN_COMPANIA; 
BEGIN
  BEGIN
  OPEN  MI_CURSOR;
    FETCH MI_CURSOR INTO MI_ROW;
  CLOSE MI_CURSOR;

  CASE UN_OPCION
    WHEN 1 THEN
      RESULTADO:= MI_ROW.COMPANIA;
    WHEN 2 THEN
      RESULTADO:= MI_ROW.DIASLABORADOS;
    WHEN 3 THEN
      RESULTADO:= MI_ROW.INDICADORVACACIONES;
    WHEN 4 THEN
      RESULTADO:= MI_ROW.DIASDISFRUTE;
    WHEN 5 THEN
      RESULTADO:= MI_ROW.DIASPAGARVACACIONES;
    WHEN 6 THEN
      RESULTADO:= MI_ROW.VACACIONESDINERO;
    WHEN 7 THEN
      RESULTADO:= MI_ROW.DIASENCARGO;
    WHEN 8 THEN
      RESULTADO:= MI_ROW.DIASREEMPLAZO;
    WHEN 9 THEN
      RESULTADO:= MI_ROW.SALARIOENCARGO;
    WHEN 10 THEN
      RESULTADO:= MI_ROW.SALARIOREEMPLAZO;
    WHEN 11 THEN
      RESULTADO:= MI_ROW.CUPOSALUD;
    WHEN 12 THEN
      RESULTADO:= MI_ROW.DEDUCIBLE;
    WHEN 13 THEN
      RESULTADO:= MI_ROW.DIASHABILES;
    WHEN 14 THEN
      RESULTADO:= MI_ROW.IBL;
    WHEN 15 THEN
      RESULTADO:= MI_ROW.CESANTIAPARCIAL;
    WHEN 16 THEN
      RESULTADO:= MI_ROW.CESANTIACONSOLIDADA;
    WHEN 17 THEN
      RESULTADO:= MI_ROW.INTERESCESANTIA;
    WHEN 18 THEN
      RESULTADO:= MI_ROW.LABSIGPERIODO;
    WHEN 19 THEN
      RESULTADO:= MI_ROW.PERIODOVACACIONES;
    WHEN 20 THEN
      RESULTADO:= MI_ROW.SALARIOMINIMO;
    WHEN 21 THEN
      RESULTADO:= MI_ROW.PORCRETENCION;
    WHEN 22 THEN
      RESULTADO:= MI_ROW.PORCGASTOS;
    WHEN 30 THEN
      RESULTADO:= MI_ROW.RAZONSOCIAL;
    WHEN 31 THEN
      RESULTADO:= MI_ROW.NIT;
    WHEN 32 THEN
      RESULTADO:= MI_ROW.TIPOAPORTANTE;
    WHEN 33 THEN
      RESULTADO:= MI_ROW.PRESENTACION;
    WHEN 34 THEN
      RESULTADO:= MI_ROW.CODIGO_SUCURSALPAGADORA;
    WHEN 35 THEN
      RESULTADO:= MI_ROW.PORC_PATRON_AFP;
    WHEN 36 THEN
      RESULTADO:= MI_ROW.PORC_EMPLEADO_AFP;
    WHEN 37 THEN
      RESULTADO:= MI_ROW.PORC_FSP_AFP;
    WHEN 38 THEN
      RESULTADO:= MI_ROW.SALARIOSMINIMOS_FSP;
    WHEN 39 THEN
      RESULTADO:= MI_ROW.PORC_TOTAL_AFP;
    WHEN 40 THEN
      RESULTADO:= MI_ROW.PORC_PATRON_EPS;
    WHEN 41 THEN
      RESULTADO:= MI_ROW.PORC_EMPLEADO_EPS;
    WHEN 42 THEN
      RESULTADO:= MI_ROW.PORC_GARANTIAS_EPS;
    WHEN 43 THEN
      RESULTADO:= MI_ROW.PORC_TOTAL_EPS;
    WHEN 44 THEN
      RESULTADO:= MI_ROW.PORC_FONDO_ARP;
    WHEN 45 THEN
      RESULTADO:= MI_ROW.PORC_ICBF;
    WHEN 46 THEN
      RESULTADO:= MI_ROW.PORC_SENA;
    WHEN 47 THEN
      RESULTADO:= MI_ROW.PORC_CAJA;
    WHEN 48 THEN
      RESULTADO:= MI_ROW.PORC_ESAP;
    WHEN 49 THEN
      RESULTADO:= MI_ROW.PORC_INST;
    WHEN 50 THEN
      RESULTADO:= MI_ROW.SALARIOS_MAXIMOS_AFP;
    WHEN 51 THEN
      RESULTADO:= MI_ROW.SALARIOS_MAXIMOS_EPS;
    WHEN 52 THEN
      RESULTADO:= MI_ROW.SALARIOS_MAXIMOS_ARP;
    WHEN 53 THEN
      RESULTADO:= MI_ROW.PORC_SALARIO_INTEGRAL;
    WHEN 54 THEN
      RESULTADO:= MI_ROW.TELEFONO;
    WHEN 55 THEN
      RESULTADO:= MI_ROW.FAX;
    WHEN 56 THEN
      RESULTADO:= MI_ROW.DIRECCION;
    WHEN 57 THEN
      RESULTADO:= MI_ROW.DEPARTAMENTO;
    WHEN 58 THEN
      RESULTADO:= MI_ROW.CIUDAD;
    WHEN 59 THEN
      RESULTADO:= MI_ROW.TIPOVINCULADOS;
    WHEN 60 THEN
      RESULTADO:= MI_ROW.TIPONIT;
    WHEN 64 THEN
      RESULTADO:= MI_ROW.CORREOELECTRONICO;
    WHEN 65 THEN
      RESULTADO:= MI_ROW.NOMBRESUCURSAL;          
    WHEN 70 THEN
      RESULTADO:= MI_ROW.EXONERADO_1607;
    WHEN 71 THEN
      RESULTADO:= MI_ROW.ACOGE_1429;
    ELSE RESULTADO:= 0;
    END CASE;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      RESULTADO:=NULL;
    END;
  RETURN RESULTADO;
END FC_PARAMETRO;

FUNCTION FC_MIRESOLUCION
/*
    NAME              : FC_MIRESOLUCION  --> EN ACCESS MiResolucion  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ�GUEZ
    DATE MIGRADOR     : 16/07/2015
    TIME              : 10:17 aM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : Retorna el valor de un campo de la tabla personal dependiendo el valor del parametro opcion.
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getMiResolucion
    @METHOD           : GET
*/
  (   
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA       DEFAULT GL_COMPANIA, 
    UN_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_OPCION   IN PCK_SUBTIPOS.TI_ENTERO
  )
RETURN VARCHAR2
  AS 
  MI_RESULTADO   VARCHAR(100):=' ';
  MI_NUM_RESOLUCION_ING   PERSONAL.NUM_RESOLUCION_ING%TYPE;
  MI_FECHA_RESOLUCION_ING PERSONAL.FECHA_RESOLUCION_ING%TYPE;
  MI_PROCESORETENCION     PERSONAL.PROCESORETENCION%TYPE;
  CURSOR MI_CURSOR IS
    SELECT  NUM_RESOLUCION_ING
           ,FECHA_RESOLUCION_ING
           ,PROCESORETENCION
    FROM  PERSONAL 
    WHERE PERSONAL.COMPANIA       = UN_COMPANIA
      AND PERSONAL.ID_DE_EMPLEADO = UN_EMPLEADO;
BEGIN   
 BEGIN
  OPEN  MI_CURSOR;
    FETCH MI_CURSOR 
     INTO  MI_NUM_RESOLUCION_ING
          ,MI_FECHA_RESOLUCION_ING
          ,MI_PROCESORETENCION; 
  CLOSE MI_CURSOR;

  IF UN_OPCION = 1 THEN 
    MI_RESULTADO := NVL(MI_NUM_RESOLUCION_ING, '');
  ELSIF UN_OPCION = 2 THEN
    MI_RESULTADO := NVL(TO_CHAR(MI_FECHA_RESOLUCION_ING), '');
  ElSIF UN_OPCION = 3  THEN
    MI_RESULTADO := NVL(MI_NUM_RESOLUCION_ING, '') 
                    || ' de ' || NVL(TO_CHAR(MI_FECHA_RESOLUCION_ING,'DD/MM/YYYY'),'');
  ElSIF UN_OPCION = 99 THEN
    MI_RESULTADO := NVL(TO_CHAR(MI_PROCESORETENCION), 0);
  ELSE  
    MI_RESULTADO := TO_CHAR(NVL(MI_PROCESORETENCION,0));
  END IF;
  EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_RESULTADO := 0; 
  END;
  RETURN MI_RESULTADO;
END FC_MIRESOLUCION;

--10
PROCEDURE FC_COPIAREMPLEADO 
/*
    NAME              : FC_COPIAREMPLEADO
    AUTHORS           : SYSMAN  SAS
    AUTHOR            : SANDRA MILENA DAZA LEGUIZAMON
    DATE              : 14/07/2015
    TIME              : 11:30 AM
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS \ SANDRA MILENA DAZ
    DATE MODIFIED     : \ 07/02/2017 \ 24/01/2023
    TIME              : 
    DESCRIPTION       : CREAR UN NUEVO REGISTRO EN LA TABLA PERSONAL TOMANDO COMO INFORMACIÃƒâ€œN BASE LOS DATOS DEL EMPLEADO INGRESADO POR PARAMETRO
                        SE AJUSTA AL ESTANDAR.
    MODIFICATION      : Se adiciona control la consultar salario (MI_SALARIO_BASE) para insertar al nuevo registro del funcionario y se modifica la inicialización de la 
                        variable MI_VALORESAUX para tomar el orden de los campos de la tabla Personal
    @NAME             : putCopiaPersona
    @METHOD           : PUT       
  */
 (

    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_IDEMPLEADO IN PERSONAL.ID_DE_EMPLEADO%TYPE,
    UN_ANIO       IN PCK_SUBTIPOS.TI_ENTERO

  ) 
  AS 
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_VALORESAUX   PCK_SUBTIPOS.TI_VALORES;
    MI_CSCEMPLEADO  PCK_SUBTIPOS.TI_ID_DE_EMPLEADO;
    MI_CRITERIO     PCK_SUBTIPOS.TI_CONDICION;
    MI_ESCALAFON    PERSONAL.ESCALAFON%TYPE;
    MI_CATEGORIA    PERSONAL.ID_DE_CATEGORIA%TYPE;
    MI_SALARIO_BASE CATEGORIA.SALARIO_BASE%TYPE;
    MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR; 

BEGIN
    --TICKET 7725646
    BEGIN
        BEGIN
            SELECT ESCALAFON, ID_DE_CATEGORIA 
            INTO MI_ESCALAFON, MI_CATEGORIA
            FROM PERSONAL  
            WHERE COMPANIA= UN_COMPANIA 
            AND ID_DE_EMPLEADO= UN_IDEMPLEADO; 
            
        EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_REEMPLAZOS(0).CLAVE := 'VIGENCIA';
                MI_REEMPLAZOS(0).VALOR := UN_ANIO;
                MI_REEMPLAZOS(1).CLAVE := 'CATEGORIA';
                MI_REEMPLAZOS(1).VALOR := MI_CATEGORIA;
                MI_REEMPLAZOS(2).CLAVE := 'ESCALAFON';
                MI_REEMPLAZOS(2).VALOR := MI_ESCALAFON;
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END ;


        BEGIN
            SELECT SALARIO_BASE
            INTO MI_SALARIO_BASE
            FROM CATEGORIA 
            WHERE COMPANIA = UN_COMPANIA
            AND ESCALAFON= MI_ESCALAFON
            AND ID_DE_CATEGORIA= MI_CATEGORIA
            AND ANO = UN_ANIO;
            
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_REEMPLAZOS(0).CLAVE := 'VIGENCIA';
                MI_REEMPLAZOS(0).VALOR := UN_ANIO;
                MI_REEMPLAZOS(1).CLAVE := 'CATEGORIA';
                MI_REEMPLAZOS(1).VALOR := MI_CATEGORIA;
                MI_REEMPLAZOS(2).CLAVE := 'ESCALAFON';
                MI_REEMPLAZOS(2).VALOR := MI_ESCALAFON;
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END ;
        
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD    => SQLCODE
                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_COPIAREMP_SINCATEGORIA
                  ,UN_TABLAERROR => 'PERSONAL'
                  ,UN_REEMPLAZOS => MI_REEMPLAZOS
                ); 
    END;

    MI_CAMPOS      := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(
                                   UN_TABLA     =>'PERSONAL'
                                  ,UN_EXCLUIDOS => 'ID_DE_EMPLEADO, NOMBRECOMPLETO, ANO, SALARIO_BASE_IBC, FECHA_FINAL') || ', ANO, SALARIO_BASE_IBC, ID_DE_EMPLEADO';
    MI_VALORESAUX  := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(
                                   UN_TABLA     =>'PERSONAL'
                                  ,UN_EXCLUIDOS => 'ID_DE_EMPLEADO, NOMBRECOMPLETO, ANO, SALARIO_BASE_IBC, FECHA_FINAL') || ',' || UN_ANIO || ',' || MI_SALARIO_BASE ;  
 
    -- FIN TICKET 7725646
    
    MI_CRITERIO    := ' COMPANIA = ''' || UN_COMPANIA || '''';
    MI_CSCEMPLEADO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                   UN_TABLA    => 'PERSONAL'
                                  ,UN_CRITERIO => MI_CRITERIO
                                  ,UN_CAMPO    => 'ID_DE_EMPLEADO') ;
    MI_VALORES     := ' SELECT ' || MI_VALORESAUX  || ', ' 
                           || MI_CSCEMPLEADO ||
                      ' FROM PERSONAL '            ||
                      ' WHERE '||MI_CRITERIO|| '
                        AND ID_DE_EMPLEADO = '   || UN_IDEMPLEADO;
  
    BEGIN
        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                    UN_TABLA   => 'PERSONAL'
                                   ,UN_ACCION  => 'IS'
                                   ,UN_CAMPOS  => MI_CAMPOS
                                   ,UN_VALORES => MI_VALORES
                                   );  
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                MI_REEMPLAZOS(0).CLAVE := 'IDEMPLEADO';
                MI_REEMPLAZOS(0).VALOR := UN_IDEMPLEADO;
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE
                ,UN_ERROR_COD  => PCK_ERRORES.ERRR_NOMINA_COPIARPERSONAL
                ,UN_TABLAERROR => 'PERSONAL'
                ,UN_REEMPLAZOS => MI_REEMPLAZOS
                );
    END;
END FC_COPIAREMPLEADO;

--11
FUNCTION FC_PREPARARPIVOT_KARDEX 
/*
    NAME              : FC_PREPARARPIVOT_KARDEX
    AUTHORS           : SYSMAN  SAS
    AUTHOR            : SANDRA MILENA DAZA LEGUIZAMON /JOSE PASCUAL GOMEZ BLANCO
    DATE              : 15/07/2015 / 01/08/2015
    TIME              : 5:30 PM    / 10:00AM
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : RETORNA CADENA DE TEXTO CON LOS PERIODOS EN LOS CUALES EL EMPLEADO TIENE CONCEPTOS CON VALORES
                        SE AJUSTA AL ESTANDAR.
                        SE CREA PARAMETRO UN_MAYORIGUAL PARA QUE TOME EL FILTRO DE ANO MAYOR O IGUAL
    @NAME             : getPivotKardexNomina
    @METHOD           : GET

  */
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO         IN PCK_SUBTIPOS.TI_ANIO,
    UN_EMPLEADOINI IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_EMPLEADOFIN IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_NOMBREMES   IN PCK_SUBTIPOS.TI_ENTERO         DEFAULT 0,
    UN_MAYORIGUAL  IN PCK_SUBTIPOS.TI_ENTERO_LARGO   DEFAULT 0,
    UN_ACUMULABLE  IN PCK_SUBTIPOS.TI_LOGICO         DEFAULT 1
  )
RETURN VARCHAR2 
  AS 
  --  MI_ERROR_FUN  NUMBER:= GL_ERROR_NUM + 11; 
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
    MI_PER        PCK_SUBTIPOS.TI_CONDICION;
    MI_MES        PCK_SUBTIPOS.TI_MES;
    MI_ANO        PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODO    PCK_SUBTIPOS.TI_PERIODO_NOMI;
    MI_STRSQL     PCK_SUBTIPOS.TI_CONSULTA;
    MI_ALIAS      PCK_SUBTIPOS.TI_CONDICION;
    RS SYS_REFCURSOR;
BEGIN
  MI_STRSQL:= ' SELECT DISTINCT HISTORICOS.ANO || ''_'' 
                             || HISTORICOS.MES || ''_'' 
                             || HISTORICOS.PERIODO PER, 
                                HISTORICOS.MES, 
                                HISTORICOS.ANO, 
                                HISTORICOS.PERIODO ' ||
              ' FROM HISTORICOS 
                INNER JOIN PERIODOS ON ' || 
                         ' HISTORICOS.COMPANIA      = PERIODOS.COMPANIA '  ||
                    ' AND  HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO ' ||  
                    ' AND  HISTORICOS.ANO           = PERIODOS.ANO ' ||  
                    ' AND  HISTORICOS.MES           = PERIODOS.MES ' ||  
                    ' AND  HISTORICOS.PERIODO       = PERIODOS.PERIODO ' ||  
              ' WHERE  HISTORICOS.COMPANIA          =''' || UN_COMPANIA || '''';

  IF UN_MAYORIGUAL = 0 THEN
    MI_STRSQL:= MI_STRSQL || ' AND HISTORICOS.ANO =' || UN_ANO ;
  ELSE
    MI_STRSQL:= MI_STRSQL || ' AND HISTORICOS.ANO >='|| UN_ANO ;
  END IF;
    MI_STRSQL:= MI_STRSQL || 
                 ' AND     HISTORICOS.ID_DE_EMPLEADO BETWEEN ' || UN_EMPLEADOINI  || ' AND ' || UN_EMPLEADOFIN ||
                 ' AND     HISTORICOS.VALOR <> 0 ';
  IF UN_ACUMULABLE <> 0 THEN
    MI_STRSQL:= MI_STRSQL  || ' AND PERIODOS.ACUMULADO NOT IN(0) ' ;
  END IF;
  MI_STRSQL:= MI_STRSQL  || 
                    ' ORDER BY  HISTORICOS.ANO ASC, HISTORICOS.MES ASC, HISTORICOS.PERIODO ASC ';

  OPEN RS FOR MI_STRSQL;
  <<RECORREHIST>>
    LOOP
      FETCH RS INTO   MI_PER
                    , MI_MES
                    , MI_ANO
                    , MI_PERIODO;
      EXIT WHEN RS%NOTFOUND;
        MI_ALIAS:= '';
        IF UN_NOMBREMES <> 0 THEN
          MI_ALIAS:= MI_ALIAS 
                     || SUBSTR(MI_PER,1, INSTR(MI_PER,'_',1,1))  
                     || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => MI_MES) 
                     || SUBSTR(MI_PER,INSTR(MI_PER,'_',1,2));
        END IF;
          MI_CONDICION:= MI_CONDICION ||'''' ||MI_PER ||''''|| ' AS "' || MI_ALIAS || '" ,' ;
    END LOOP RECORREHIST;
  CLOSE RS;
  MI_CONDICION:= SUBSTR(MI_CONDICION,1, LENGTH(MI_CONDICION)-2);
  RETURN MI_CONDICION;
END FC_PREPARARPIVOT_KARDEX;


--12
FUNCTION FC_PREPARARPIVOT_KARDEX_ACUM
/*
    NAME              : FC_PREPARARPIVOT_KARDEX
    AUTHORS           : SYSMAN  SAS
    AUTHOR            : DIEGO ALFREDO SUESCA RODRÃ�GUEZ / SANDRA MILENA DAZA LEGUIZAMON
    DATE              : 21/07/2015
    TIME              : 10:30 AM
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : Se retorna una cadena concatenada con los meses en donde el empleado tiene registro de acumulados en las clases 1,3,8,4,99,6,7,5
                        SE AJUSTA AL ESTANDAR.
                        dentro de un limite de aÃƒÂ±o+mes+periodo, esto con el fin de armar la cadena de caracteres dentro de la clausula 'in' del pivot
    @NAME             : getKardexAcumNomina
    @METHOD           : GET                    
  */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_LIMITEINF  IN VARCHAR2,
    UN_LIMITESUP  IN VARCHAR2,
    UN_IDEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  )
RETURN VARCHAR2
  AS
  --MI_ERROR_FUN  NUMBER:= GL_ERROR_NUM + 12; 
    MI_RESULTADO  PCK_SUBTIPOS.TI_STRSQL :=  '''';
BEGIN 
 BEGIN
   FOR RS IN (SELECT DISTINCT V_ACUMULADOS.ANO ||'_'
                           || LPAD(V_ACUMULADOS.MES,2,'0') ||'_'
                           || LPAD(V_ACUMULADOS.PERIODO,2,'0') ||'_'
                           || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => V_ACUMULADOS.MES) PIV
              FROM   V_ACUMULADOS
              WHERE  V_ACUMULADOS.COMPANIA       =  UN_COMPANIA 
                AND  V_ACUMULADOS.ID_DE_EMPLEADO =  UN_IDEMPLEADO
                AND  V_ACUMULADOS.CLASE          IN (1,3,8,4,99,6,7,5) 
                AND  V_ACUMULADOS.ANO 
                     || LPAD(V_ACUMULADOS.MES,2,'0') 
                     || LPAD(V_ACUMULADOS.PERIODO,2,'0') 
                     BETWEEN UN_LIMITEINF AND UN_LIMITESUP 
                AND  V_ACUMULADOS.KARDEX NOT IN (0) 
              ORDER BY V_ACUMULADOS.ANO ||'_'
                           || LPAD(V_ACUMULADOS.MES,2,'0') ||'_'
                           || LPAD(V_ACUMULADOS.PERIODO,2,'0') ||'_'
                           || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => V_ACUMULADOS.MES)
              )  
  LOOP
    MI_RESULTADO := MI_RESULTADO || RS.PIV || '''' || ' P' || RS.PIV || ',''' ;
  END LOOP;
  MI_RESULTADO := SUBSTR(MI_RESULTADO,1, LENGTH(MI_RESULTADO)-2);
  EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_RESULTADO:='';
  END;
  RETURN MI_RESULTADO; 
END FC_PREPARARPIVOT_KARDEX_ACUM;

--14
FUNCTION FC_TRAERDATOSENTIDADES
  /*
    NAME              : FC_TRAERDATOSENTIDADES  --> EN ACCESS TRAERDATOSENTIDADES  -
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 23/07/2015
    TIME              : 10:04 AM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : RETORNA DATOS PROPIOS DEL FONDO DADO EL TIPO Y CODIGO DEL MISMO
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getDatosEntidadesFondos
    @METHOD           : GET
  */
  (
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODFONDO  IN VARCHAR2,
    UN_TIPOFONDO IN VARCHAR2,
    UN_PAR       IN PCK_SUBTIPOS.TI_ENTERO
  )
RETURN VARCHAR2
  AS
  RTA              VARCHAR2(300);
BEGIN
  RTA:='';
  <<RECORREFONDOS>>
  FOR MI_RS IN(SELECT  NIT
                      ,NOMBRE_FONDO
                      ,CODA
                 FROM V_FONDOS
                WHERE COMPANIA  = UN_COMPANIA
                  AND COD_FONDO = UN_CODFONDO 
                  AND CLASE     = UN_TIPOFONDO)
  LOOP
    IF UN_PAR = 1 THEN
      RTA:= MI_RS.NIT;
    ELSIF UN_PAR = 2 THEN
      RTA:= MI_RS.NOMBRE_FONDO;
    ELSIF UN_PAR = 3 THEN
      RTA:= NVL(MI_RS.CODA,LPAD(' ',6,' '));
    END IF;
  END LOOP RECORREFONDOS;
 RETURN RTA;
END FC_TRAERDATOSENTIDADES; 

--15
FUNCTION FC_ACUMCONCEPTO
/*
    NAME              : FC_ACUMCONCEPTO  --> EN ACCESS ACUMCONCEPTO  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 22/07/2015
    TIME              : 15:04
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              :
    DESCRIPTION       : Carga el Type CNP con el valor del concepto que se envia de resto lo pone en cero, retornando el nÃºmero de periodos
                        SE AJUSTA AL ESTANDAR.
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CONCEPTO     IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
    UN_ANO1         IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES1         IN PCK_SUBTIPOS.TI_MES,
    UN_PER1         IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ANO2         IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES2         IN PCK_SUBTIPOS.TI_MES,
    UN_PER2         IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_IDDEEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  )
RETURN NUMBER
  AS
   -- MI_ERROR_FUN    NUMBER:= GL_ERROR_NUM + 15;
    MI_ANO2         PCK_SUBTIPOS.TI_ANIO;
    MI_MES2         PCK_SUBTIPOS.TI_MES;
    MI_PER2         PCK_SUBTIPOS.TI_PERIODO_NOMI;
    MI_CONTAR       PCK_SUBTIPOS.TI_ENTERO;
    MI_CNP          PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN
  --IF UN_ANO2 = '' OR UN_ANO2 =' ' OR UN_ANO2 IS NULL THEN
  IF UN_ANO2 IS NULL THEN
    MI_ANO2:= UN_ANO1;
  ELSE
    MI_ANO2:= UN_ANO2;
  END IF;
  IF UN_MES2 IS NULL THEN
    MI_MES2:= UN_MES1;
  ELSE
    MI_MES2:= UN_MES2;
  END IF;
  IF  UN_PER2 IS NULL THEN
    MI_PER2:= UN_PER1;
  ELSE
    MI_PER2:= UN_PER2;
  END IF;
  PCK_NOMINA.CNP.DELETE;

  IF 0 > UN_CONCEPTO
     OR PCK_NOMINA.MAXI < UN_CONCEPTO THEN
    RETURN 0;
  END IF;


    BEGIN
      SELECT  SUM(T.VALOR)
             ,COUNT(T.ANO)
        INTO  MI_CNP
             ,MI_CONTAR
      FROM (SELECT   HISTORICOS.ANO,
                     HISTORICOS.MES,
                    SUM(HISTORICOS.VALOR) VALOR
            FROM HISTORICOS
            INNER JOIN PERIODOS
              ON  HISTORICOS.COMPANIA       = PERIODOS.COMPANIA
              AND HISTORICOS.ID_DE_PROCESO  = PERIODOS.ID_DE_PROCESO
              AND HISTORICOS.ANO            = PERIODOS.ANO
              AND HISTORICOS.MES            = PERIODOS.MES
              AND HISTORICOS.PERIODO        = PERIODOS.PERIODO
            WHERE PERIODOS.COMPANIA       = UN_COMPANIA
              AND PERIODOS.ID BETWEEN LPAD(UN_ANO1,4,'0') || LPAD(UN_MES1,2,'0') || LPAD(UN_PER1,2,'0')
                                  AND LPAD(MI_ANO2,4,'0') || LPAD(MI_MES2,2,'0') || LPAD(MI_PER2,2,'0')
              AND PERIODOS.ACUMULADO        NOT IN (0)
              AND HISTORICOS.ID_DE_EMPLEADO = UN_IDDEEMPLEADO              
              AND HISTORICOS.ID_DE_CONCEPTO = UN_CONCEPTO
              AND HISTORICOS.VALOR          NOT IN (0)
            GROUP BY HISTORICOS.ANO, HISTORICOS.MES
           ) T;
    EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
        MI_CNP := 0;
        MI_CONTAR := 0;
    END;

    IF MI_CNP IS NOT NULL THEN
        PCK_NOMINA.CNP(UN_CONCEPTO):= MI_CNP;
    END IF;

    RETURN MI_CONTAR;    

END FC_ACUMCONCEPTO;
--16 
PROCEDURE PR_ACTUALIZAR_RETEFUENTE
 /*
    NAME              : PR_ACTUALIZAR_RETEFUENTE   -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIEGO ALFREDO SUESCA RODRÃ�GUEZ
    DATE MIGRADOR     : 27/07/2015
    TIME              : 11:00 
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : 
                        SE AJUSTA AL ESTANDAR.
    @NAME             : putActualizarRetefuente
    @METHOD           : PUT
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES      IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO DEFAULT PCK_CONEXION.FC_GETUSER()
  )
  AS  
    MI_TABLA     PCK_SUBTIPOS.TI_TABLA;
    MI_VALORES   PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;
BEGIN
  MI_TABLA := 'RETEFUENTE_CALCULOS';
  MI_VALORES := '(RETEFUENTE_CALCULOS.TIPORET, 
                  RETEFUENTE_CALCULOS.MODIFIED_BY,
                  RETEFUENTE_CALCULOS.DATE_MODIFIED) = (SELECT PERSONAL.TIPORET,
                                                               ''' || UN_USUARIO || ''',SYSDATE 
                                          FROM PERSONAL
                                         WHERE RETEFUENTE_CALCULOS.COMPANIA       = PERSONAL.COMPANIA
                                           AND RETEFUENTE_CALCULOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO )';
  MI_CONDICION := '         RETEFUENTE_CALCULOS.COMPANIA = '''|| UN_COMPANIA||'''
                      AND   RETEFUENTE_CALCULOS.ANO      = '|| UN_ANO||'
                      AND   RETEFUENTE_CALCULOS.MES      = '||UN_MES||'
                      AND   RETEFUENTE_CALCULOS.PERIODO  = '||UN_PERIODO||'';
  BEGIN 
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                        UN_TABLA     => MI_TABLA
                                       ,UN_ACCION    => 'M'
                                       ,UN_CAMPOS   => MI_VALORES
                                       ,UN_CONDICION    => MI_CONDICION
                                       );
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
    RAISE PCK_EXCEPCIONES.EXC_NOMINA; 
  END;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE
         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_NOMINA_ACTUALIZARETEFTE 
         ,UN_TABLAERROR => MI_TABLA
        );

END PR_ACTUALIZAR_RETEFUENTE;  

--17
FUNCTION FC_PRIMERVALOR
  /*
    NAME              : FC_PRIMERVALOR  --> EN ACCESS PRIMERVALOR  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 27/07/2015
    TIME              : 10:04 
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : Retorna el primer registro ordenado por id_de_codigo de la tabla personal
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getPrimerEmpleado
    @METHOD           : GET
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_REGISTRO IN PCK_SUBTIPOS.TI_ENTERO
  )
RETURN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  AS
    MI_ID   PCK_SUBTIPOS.TI_ID_DE_EMPLEADO;
BEGIN
  BEGIN  
    IF UN_REGISTRO = 0 THEN
      SELECT ID_DE_EMPLEADO
        INTO MI_ID
        FROM PERSONAL
       WHERE COMPANIA = UN_COMPANIA
         AND ROWNUM   = 1
       ORDER BY ID_DE_EMPLEADO;
    ELSE
      SELECT ID_DE_EMPLEADO
        INTO MI_ID
        FROM PERSONAL
       WHERE COMPANIA       = UN_COMPANIA
         AND ID_DE_EMPLEADO NOT IN (0)
         AND ROWNUM         = 1
       ORDER BY ID_DE_EMPLEADO;        
      END IF;
  RETURN MI_ID;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    RETURN -1;  
  END; 
END FC_PRIMERVALOR;

--18
FUNCTION FC_NOMBREPERIODO
  /*
    NAME              : FC_NOMBREPERIODO  --> EN ACCESS NOMBREPERIODO  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 27/07/2015
    TIME              : 11:49 
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : Retorna el nombre dado a un periodo
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getNombrePeriodo
    @METHOD           : GET
  */ 
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO  IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES      IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI
  )
RETURN VARCHAR2
AS
    MI_NOMBRE       PCK_SUBTIPOS.TI_VALORES;
BEGIN
  BEGIN  
    SELECT NOM_PERIODO
      INTO MI_NOMBRE
      FROM PERIODOS
     WHERE COMPANIA      = UN_COMPANIA
       AND ID_DE_PROCESO = UN_PROCESO
       AND ANO           = UN_ANO
       AND MES           = UN_MES 
       AND PERIODO       = UN_PERIODO;
  EXCEPTION WHEN NO_DATA_FOUND THEN
      RETURN -1;  
  END; 
  RETURN MI_NOMBRE;

END FC_NOMBREPERIODO;

--19
FUNCTION FC_ACUMCONCEPTO_VALOR

/*
    NAME              : FC_ACUMCONCEPTO_VALOR  
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 28/07/2015
    TIME              : 09:04 
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : Necesaria para traer el valor del concepto acumulado debido a que la original carga el cnp, 
                        SE AJUSTA AL ESTANDAR.
                        pero del lado de java no se puede utilizar
    @NAME             : getAcumConceptoValor
    @METHOD           : GET
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CONCEPTO     IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
    UN_ANO1         IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES1         IN PCK_SUBTIPOS.TI_MES,
    UN_PER1         IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ANO2         IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES2         IN PCK_SUBTIPOS.TI_MES,
    UN_PER2         IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_IDDEEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  )
RETURN PCK_SUBTIPOS.TI_DOBLE
  AS
    MI_ANO2         PCK_SUBTIPOS.TI_ANIO;
    MI_MES2         PCK_SUBTIPOS.TI_MES;
    MI_PER2         PCK_SUBTIPOS.TI_PERIODO_NOMI;
    MI_CNP          PCK_SUBTIPOS.TI_DOBLE :=0;
BEGIN 
  IF UN_ANO2 = '' THEN 
    MI_ANO2:= UN_ANO1;
  ELSE
    MI_ANO2:= UN_ANO2;
  END IF;
  IF UN_MES2 = '' THEN 
    MI_MES2:= UN_MES1;
  ELSE
    MI_MES2:= UN_MES2;
  END IF;
  IF UN_PER2 = '' THEN 
    MI_PER2:= UN_PER1;
  ELSE
    MI_PER2:= UN_PER2;
  END IF;

  IF 0 > UN_CONCEPTO 
     OR  PCK_NOMINA.MAXI < UN_CONCEPTO THEN
    RETURN 0;
  END IF;
  BEGIN
    SELECT SUM(HISTORICOS.VALOR) VALOR
      INTO MI_CNP
      FROM HISTORICOS 
        INNER JOIN PERIODOS 
          ON  HISTORICOS.COMPANIA      = PERIODOS.COMPANIA
          AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
          AND HISTORICOS.ANO           = PERIODOS.ANO
          AND HISTORICOS.MES           = PERIODOS.MES
          AND HISTORICOS.PERIODO       = PERIODOS.PERIODO
     WHERE PERIODOS.COMPANIA       = UN_COMPANIA
       AND PERIODOS.ID BETWEEN LPAD(UN_ANO1,4,'0') || LPAD(UN_MES1,2,'0') || LPAD(UN_PER1,2,'0')
                           AND LPAD(MI_ANO2,4,'0') || LPAD(MI_MES2,2,'0') || LPAD(MI_PER2,2,'0')
       AND PERIODOS.ACUMULADO        NOT IN (0)
       AND HISTORICOS.ID_DE_EMPLEADO = UN_IDDEEMPLEADO       
      AND HISTORICOS.ID_DE_CONCEPTO = UN_CONCEPTO      
      AND HISTORICOS.VALOR          NOT IN (0); 
  EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_CNP:=0;
  END;
  IF MI_CNP IS NULL THEN
    MI_CNP:=0;
  END IF;
RETURN MI_CNP;

END FC_ACUMCONCEPTO_VALOR;

--20
FUNCTION FC_ACUMNOVEDADCONCEPTO

/*
    NAME              : FC_ACUMNOVEDADCONCEPTO  --> EN ACCESS AcumNovedadconcepto  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 28/07/2015
    TIME              : 11:15 
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB PARA MIGRAR FINAL  4TO ENVIO.ACCDB
    MODIFIER          : \ MIGUEL ANGEL VENEGAS RODRIGUEZ
    DATE MODIFIED     : \ 04/01/2018
    TIME              : 
    DESCRIPTION       : CARGA EL TYPE CNA CON EL VALOR DE LA NOVEDAD DEL CONCEPTO CONCEPTO QUE SE ENVIA DE RESTO LO PONE EN CERO, RETORNANDO EL NÃƒÂºMERO DE PERIODOS
                        SE AJUSTA AL ESTANDAR.
    --@NAME             : GETACUMNOVEDADCONCEPTO
    @METHOD           : GET
  */
(
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO1         IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES1         IN PCK_SUBTIPOS.TI_MES,
  UN_PER1         IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
  UN_ANO2         IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES2         IN PCK_SUBTIPOS.TI_MES,
  UN_PER2         IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
  UN_IDDEEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
)
RETURN PCK_SUBTIPOS.TI_DOBLE
  AS
    MI_ANO2         PCK_SUBTIPOS.TI_ANIO;
    MI_MES2         PCK_SUBTIPOS.TI_MES;
    MI_PER2         PCK_SUBTIPOS.TI_PERIODO_NOMI;
    MI_CONTAR       PCK_SUBTIPOS.TI_ENTERO:= 0;
    MI_CNP          PCK_SUBTIPOS.TI_DOBLE:= NULL;
    MI_PROCESO      PCK_SUBTIPOS.TI_ID_DE_PROCESO  DEFAULT 0;
    MI_ACUMULADO    NUMBER := 0;         
BEGIN 
    IF UN_ANO2 = '' THEN 
        MI_ANO2 :=UN_ANO1;
    ELSE
        MI_ANO2 :=UN_ANO2;
    END IF;
    IF UN_MES2 = '' THEN 
        MI_MES2 :=UN_MES1;
    ELSE
        MI_MES2 :=UN_MES2;
    END IF;
    IF UN_PER2 = '' THEN 
        MI_PER2 :=UN_PER1;
    ELSE
        MI_PER2 :=UN_PER2;
    END IF;
    PCK_NOMINA.CNA.DELETE;

    --(APINEDA:02/10/2019)-Se agregan condiciones a WHERE con el fin de tomar novedades configuradas en Proceso retroactivo.
    IF PCK_NOMINA.GL_NOMINARETROACTIVO THEN
        MI_PROCESO := 10;
        MI_ACUMULADO := -1;
    ELSE
        MI_PROCESO := 1;
    END IF;  
    BEGIN
    <<SUMATORIA>> 
    FOR MI_RS IN (
                SELECT  SUM(NOVEDADES.VALOR) SUMACONCEPTO
                       ,NOVEDADES.ID_DE_CONCEPTO
                FROM NOVEDADES 
                     INNER JOIN PERIODOS 
                     ON  NOVEDADES.COMPANIA      = PERIODOS.COMPANIA
                     AND NOVEDADES.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                     AND NOVEDADES.ANO           = PERIODOS.ANO
                     AND NOVEDADES.MES           = PERIODOS.MES
                     AND NOVEDADES.PERIODO       = PERIODOS.PERIODO
                WHERE  PERIODOS.COMPANIA       = UN_COMPANIA
                  AND  PERIODOS.ID BETWEEN LPAD(UN_ANO1,4,'0') || LPAD(UN_MES1,2,'0') || LPAD(UN_PER1,2,'0')
                                          AND LPAD(MI_ANO2,4,'0') || LPAD(MI_MES2,2,'0') || LPAD(MI_PER2,2,'0')
                  AND  PERIODOS.ACUMULADO        NOT IN (MI_ACUMULADO)
                  AND  NOVEDADES.ID_DE_EMPLEADO = UN_IDDEEMPLEADO        
                  AND  NOVEDADES.ID_DE_PROCESO   = MI_PROCESO
                GROUP BY NOVEDADES.ID_DE_CONCEPTO
    ) LOOP
        IF MI_RS.ID_DE_CONCEPTO > 0 AND MI_RS.ID_DE_CONCEPTO <= PCK_NOMINA.MAXI THEN
            PCK_NOMINA.CNA(MI_RS.ID_DE_CONCEPTO) := MI_RS.SUMACONCEPTO;
        END IF;
        MI_CONTAR := MI_CONTAR+1;
    END LOOP SUMATORIA;

  EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_CONTAR:= 0;
  END; 


  RETURN MI_CONTAR;
END FC_ACUMNOVEDADCONCEPTO;

--21
FUNCTION FC_ACUMNOVEDADCONCEPTO_VALOR  
/*
    NAME              : ACUMNOVEDADCONCEPTO  --> EN ACCESS ACUMNOVEDADCONCEPTO  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 28/07/2015
    TIME              : 11:34 
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : Necesaria para cargar cna con los valores desde las novedas
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getAcumNovedadConcValor
    @METHOD           : GET
  */
(
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CONCEPTO     IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
  UN_ANO1         IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES1         IN PCK_SUBTIPOS.TI_MES,
  UN_PER1         IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
  UN_ANO2         IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES2         IN PCK_SUBTIPOS.TI_MES,
  UN_PER2         IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
  UN_IDDEEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
)
RETURN PCK_SUBTIPOS.TI_DOBLE
  AS
    MI_ANO2         PCK_SUBTIPOS.TI_ANIO;
    MI_MES2         PCK_SUBTIPOS.TI_MES;
    MI_PER2         PCK_SUBTIPOS.TI_PERIODO_NOMI;
    MI_CNP          PCK_SUBTIPOS.TI_DOBLE:=0;
    MI_PROCESO     PCK_SUBTIPOS.TI_ID_DE_PROCESO  DEFAULT 0;
    MI_ACUMULADO   NUMBER := 0;       
BEGIN 
  IF UN_ANO2 = '' THEN 
    MI_ANO2:= UN_ANO1;
  ELSE
    MI_ANO2:= UN_ANO2;
  END IF;
  IF UN_MES2 = '' THEN 
    MI_MES2:= UN_MES1;
  ELSE
    MI_MES2:= UN_MES2;
  END IF;
  IF UN_PER2 = '' THEN 
    MI_PER2:= UN_PER1;
  ELSE
    MI_PER2:= UN_PER2;
  END IF;
  IF 0 > UN_CONCEPTO 
     OR  PCK_NOMINA.MAXI < UN_CONCEPTO THEN
    RETURN 0;
  END IF;
  --(APINEDA:02/10/2019)-Se agregan condiciones a WHERE con el fin de tomar novedades configuradas en Proceso retroactivo.
  IF PCK_NOMINA.GL_NOMINARETROACTIVO THEN
    MI_PROCESO := 10;
    MI_ACUMULADO := -1;
  ELSE
    MI_PROCESO := 1;   
  END IF;    
  BEGIN
    SELECT SUM(NOVEDADES.VALOR) VALOR
      INTO MI_CNP
      FROM NOVEDADES 
        INNER JOIN PERIODOS 
          ON  NOVEDADES.COMPANIA      = PERIODOS.COMPANIA 
          AND NOVEDADES.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO 
          AND NOVEDADES.ANO           = PERIODOS.ANO 
          AND NOVEDADES.MES           = PERIODOS.MES 
          AND NOVEDADES.PERIODO       = PERIODOS.PERIODO 
     WHERE PERIODOS.COMPANIA       = UN_COMPANIA
       AND PERIODOS.ACUMULADO       NOT IN (MI_ACUMULADO)
       AND PERIODOS.ID BETWEEN LPAD(UN_ANO1,4,'0') || LPAD(UN_MES1,2,'0') || LPAD(UN_PER1,2,'0')
                           AND LPAD(MI_ANO2,4,'0') || LPAD(MI_MES2,2,'0') || LPAD(MI_PER2,2,'0')
      AND NOVEDADES.ID_DE_EMPLEADO = UN_IDDEEMPLEADO
      AND NOVEDADES.ID_DE_CONCEPTO = UN_CONCEPTO
      AND NOVEDADES.ID_DE_PROCESO  = MI_PROCESO      
      AND NOVEDADES.VALOR          NOT IN (0); 
    EXCEPTION WHEN NO_DATA_FOUND THEN
     RETURN 0;  
  END;
  IF MI_CNP IS NULL THEN
    MI_CNP:=0;
  END IF;
  RETURN MI_CNP;

END FC_ACUMNOVEDADCONCEPTO_VALOR;

--22
FUNCTION FC_SALARIO_BASE_CATEG
/*
    NAME              : ACUMNOVEDADCONCEPTO  --> No existe perp para evitar hace left join a categorias para algunos encabezados de informes  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 30/07/2015
    TIME              : 11:34 
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : TRae el valor del salario desde categorias de un empleado
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getSalarioBaseCategoria
    @METHOD           : GET
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_IDDEEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  )
RETURN PCK_SUBTIPOS.TI_DOBLE
  AS
    MI_VALOR        PCK_SUBTIPOS.TI_DOBLE:= 0;
BEGIN 
  BEGIN
    SELECT CATEGORIA.SALARIO_BASE
      INTO MI_VALOR
      FROM PERSONAL 
        INNER JOIN CATEGORIA
          ON  PERSONAL.COMPANIA        = CATEGORIA.COMPANIA
          AND PERSONAL.ESCALAFON       = CATEGORIA.ESCALAFON
          AND PERSONAL.ID_DE_CATEGORIA = CATEGORIA.ID_DE_CATEGORIA
          AND PERSONAL.ANO             = CATEGORIA.ANO
     WHERE PERSONAL.COMPANIA       = UN_COMPANIA
       AND PERSONAL.ID_DE_EMPLEADO = UN_IDDEEMPLEADO;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    RETURN 0;  
  END;
  IF MI_VALOR IS NULL THEN
    MI_VALOR:=0;
  END IF;
  RETURN MI_VALOR;

END FC_SALARIO_BASE_CATEG;

--23
FUNCTION FC_DEDUCIBLE

/*
    NAME              : FC_DEDUCIBLE  --> En Acces DEDUCIBLE
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 30/07/2015
    TIME              : 11:34 
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : Trae desde la novedades el valor del concepto 300 para siempre a todos los empleados
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getDeducible
    @METHOD           : GET
  */
(
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_IDDEEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
)
RETURN PCK_SUBTIPOS.TI_DOBLE
  AS
    MI_VALOR        PCK_SUBTIPOS.TI_DOBLE:=0;
BEGIN 
  BEGIN
    SELECT SUM(VALOR)
      INTO MI_VALOR
      FROM NOVEDADES 
     WHERE NOVEDADES.COMPANIA        = UN_COMPANIA
       AND NOVEDADES.ID_DE_PROCESO   = 0
       AND NOVEDADES.ANO             = 0
       AND NOVEDADES.MES             = 0
       AND NOVEDADES.PERIODO         = 0
       AND NOVEDADES.ID_DE_CONCEPTO  IN (300,301)
       AND NOVEDADES.ID_DE_EMPLEADO  = UN_IDDEEMPLEADO;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    RETURN 0;  
  END;
  IF MI_VALOR IS NULL THEN
    MI_VALOR:=0;
  END IF;
  RETURN MI_VALOR;

END FC_DEDUCIBLE;


--25
FUNCTION FC_PREPARARPIVOT_DANE_EMPLEADO
/*
    NAME              : FC_PREPARARPIVOT_DANE_EMPLEADO
    AUTHORS           : SYSMAN  SAS
    AUTHOR            : DIEGO ALFREDO SUESCA RODRÃ�GUEZ
    DATE              : 03/08/2015
    TIME              : 11:30 AM
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : Retorna una cadena de texto con los valores de la culumna CONCEPTOS.COLUMNA_SIDEF11 concatenados para usa dentro de un pivot
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getPrepararPivotDaneEmpleado
    @METHOD           : GET                    
  */
  (
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_LIMITEINF IN VARCHAR2,
    UN_LIMITESUP IN VARCHAR2
  )
RETURN VARCHAR2
  AS
    MI_RESULTADO    PCK_SUBTIPOS.TI_STRSQL:=  '';
BEGIN 
  BEGIN
  <<RECORRESIDEF11>>
    FOR RS IN (
      SELECT DISTINCT NVL(CONCEPTOS.COLUMNA_SIDEF11,'<>') COLUMNA_SIDEF11
        FROM HISTORICOS 
          INNER JOIN CONCEPTOS 
            ON  HISTORICOS.COMPANIA       = CONCEPTOS.COMPANIA
            AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO 
          INNER JOIN  PERSONAL 
            ON  HISTORICOS.COMPANIA       = PERSONAL.COMPANIA
            AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
          INNER JOIN PERIODOS
              ON  HISTORICOS.COMPANIA      = PERIODOS.COMPANIA
              AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
              AND HISTORICOS.ANO           = PERIODOS.ANO
              AND HISTORICOS.MES           = PERIODOS.MES
              AND HISTORICOS.PERIODO       = PERIODOS.PERIODO
       WHERE PERIODOS.COMPANIA = UN_COMPANIA
         AND PERIODOS.ID BETWEEN UN_LIMITEINF AND UN_LIMITESUP
         AND (CONCEPTOS.CLASE = 3 
          OR (CONCEPTOS.CLASE = 5 AND HISTORICOS.ID_DE_CONCEPTO IN (130,131,132,120)) 
          OR (CONCEPTOS.CLASE = 8 AND HISTORICOS.ID_DE_CONCEPTO BETWEEN 101 AND 105))
       ORDER BY   COLUMNA_SIDEF11) 
    LOOP
      MI_RESULTADO:= MI_RESULTADO ||''''|| RS.COLUMNA_SIDEF11 || '''' || ' "' || RS.COLUMNA_SIDEF11 || '",' ;
    END LOOP RECORRESIDEF11;
  MI_RESULTADO:= SUBSTR(MI_RESULTADO,1, LENGTH(MI_RESULTADO)-1);
  EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_RESULTADO:='';
  END;
 RETURN MI_RESULTADO;   
END FC_PREPARARPIVOT_DANE_EMPLEADO;

--26 
FUNCTION FC_PERIODOACTIVADOFECHA
/*
    NAME              : PERIODOACTIVADOFECHA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GÃ“MEZ BLANCO
    DATE MIGRADOR     : 10/08/2015
    TIME              : 08:47 AM
    SOURCE MODULE     : NominaP2015.04.01DUI.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : VERIFICA SI UN PERIODO YA SE ENCUENTRA CONFIGURADO, RETORNANDO VERDADERO ENTREGANDOLE LA FECHA Y NO EL 
                        SE AJUSTA AL ESTANDAR.
                        ANO Y MES.
    @NAME             : getPeriodoActivadoFecha
    @METHOD           : GET
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO  IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_FECHA    IN DATE,
    UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI
  )
RETURN PCK_SUBTIPOS.TI_LOGICO
  AS
    MI_EXISTE      PCK_SUBTIPOS.TI_LOGICO;

BEGIN
  BEGIN 
    SELECT ESTADO
      INTO MI_EXISTE
      FROM PERIODOS
     WHERE COMPANIA             = UN_COMPANIA
       AND ID_DE_PROCESO        = UN_PROCESO
       AND TRUNC(FECHAFINAL)    = UN_FECHA
       AND PERIODO              = UN_PERIODO;
  EXCEPTION WHEN NO_DATA_FOUND THEN 
    RETURN 0;
  END;  
  IF MI_EXISTE = 0 THEN
     RETURN 0;
  ELSE
     RETURN -1;
  END IF;  
END FC_PERIODOACTIVADOFECHA ;

--27
FUNCTION FC_ULTIMAFECHAVAC
/*
    NAME              : ULTIMAFECHAVAC
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GÃ“MEZ BLANCO
    DATE MIGRADOR     : 10/08/2015
    TIME              : 09:48 AM
    SOURCE MODULE     : NominaP2015.04.01DUI.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : RETORNA LA ULTIMA FECHA DE VACACIONES TOMADAS DE ACUERDO AL REGISTRO DE PERSONAL.     
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getUltimaFechaVacaciones
    @METHOD           : GET
  */
  (
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_EMPLEADO  IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_PARAMETRO IN PCK_SUBTIPOS.TI_LOGICO
  )
RETURN DATE
AS
  MI_RETORNO     DATE;  
BEGIN
  IF UN_EMPLEADO = 0 THEN
    RETURN NULL;
  END IF;
  FOR RS IN (SELECT  FINAL_DISFRUTE
                    ,FECHA_FINAL
                    ,FECHA_DE_INGRESO
                    ,FECHA_INICIO
               FROM PERSONAL 
              WHERE COMPANIA       = UN_COMPANIA 
                AND ID_DE_EMPLEADO = UN_EMPLEADO
             )
  LOOP           
    IF UN_PARAMETRO = 1 OR UN_PARAMETRO <> 0 THEN
      IF RS.FECHA_FINAL IS NOT NULL THEN 
       MI_RETORNO := CASE WHEN RS.FECHA_FINAL IS NULL THEN 
         RS.FECHA_DE_INGRESO
      ELSE
        RS.FECHA_FINAL - PCK_SYSMAN_UTL.FC_IIF(
                                       UN_CONDICION => RS.FECHA_DE_INGRESO = RS.FECHA_FINAL
                                      ,UN_SI        => 1
                                      ,UN_NO        => 0)
      END;
       MI_RETORNO := CASE WHEN (RS.FECHA_FINAL + 1) IS NULL THEN 
         RS.FECHA_DE_INGRESO
      ELSE
        RS.FECHA_FINAL + PCK_SYSMAN_UTL.FC_IIF(
                                       UN_CONDICION => RS.FECHA_DE_INGRESO = RS.FECHA_FINAL
                                      ,UN_SI        => 0
                                      ,UN_NO        => 1)
      END;
      /*
        MI_RETORNO:= PCK_SYSMAN_UTL.FC_IIF(
                                  UN_CONDICION => TO_DATE(RS.FECHA_FINAL + 1,'DD/MM/YYYY') IS NULL
                                 ,UN_SI        => TO_DATE(RS.FECHA_DE_INGRESO,'DD/MM/YYYY')
                                 ,UN_NO        => TO_DATE( RS.FECHA_FINAL,'DD/MM/YYYY')+ PCK_SYSMAN_UTL.FC_IIF(
                                                                                           UN_CONDICION => RS.FECHA_DE_INGRESO = RS.FECHA_FINAL
                                                                                          ,UN_SI        => 0
                                                                                         ,UN_NO        => 1));
    */
      ELSE
        MI_RETORNO:= TO_DATE(TO_CHAR(RS.FECHA_DE_INGRESO,'DD/MM/YYYY'));
      END IF;
    ELSE

      MI_RETORNO := CASE WHEN RS.FECHA_FINAL IS NULL THEN 
         RS.FECHA_DE_INGRESO
      ELSE
        RS.FECHA_FINAL - PCK_SYSMAN_UTL.FC_IIF(
                                       UN_CONDICION => RS.FECHA_DE_INGRESO = RS.FECHA_FINAL
                                      ,UN_SI        => 1
                                      ,UN_NO        => 0)
      END;

      IF TO_NUMBER(TO_CHAR(MI_RETORNO,'DD'))-1 = 0 THEN
          MI_RETORNO:= MI_RETORNO-1;
      ELSE
        IF TO_CHAR(MI_RETORNO,'MM')='02' 
           AND TO_NUMBER(TO_CHAR(MI_RETORNO,'DD'))=29  THEN
          MI_RETORNO:= TO_DATE((TO_NUMBER(TO_CHAR(MI_RETORNO,'DD'))-1) || '/' || TO_CHAR(MI_RETORNO,'MM') || '/' || (TO_NUMBER(TO_CHAR(MI_RETORNO,'YYYY'))+1), 'DD/MM/YYYY');
        ELSE
          MI_RETORNO:= TO_DATE(TO_CHAR(MI_RETORNO,'DD') || '/' || TO_CHAR(MI_RETORNO,'MM') || '/' || (TO_NUMBER(TO_CHAR(MI_RETORNO,'YYYY'))+1), 'DD/MM/YYYY');
        END IF;        
      END IF;
    END IF;
  END LOOP;  
 RETURN MI_RETORNO;
END FC_ULTIMAFECHAVAC ;

--28
FUNCTION FC_VALIDARFECHASVACACIONES
/*
    NAME              : VALIDARFECHASVACACIONES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GÃ“MEZ BLANCO
    DATE MIGRADOR     : 10/08/2015
    TIME              : 05:13 PM
    SOURCE MODULE     : NominaP2015.04.01DUI.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : RETORNA UN MENSAJE CUANDO LA FECHA INICIAL DE LAS VACACIONES SE CRUCE CON REGISTROS DE OTRAS VACACIONES
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getValidarFechasVacaciones
    @METHOD           : GET
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHA    IN DATE,
    UN_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  )
RETURN VARCHAR2
  AS
    MI_RETORNO    PCK_SUBTIPOS.TI_STRSQL:=NULL;
BEGIN
  FOR RS IN (
    SELECT  FECHA_FINAL
           ,FECHA_INICIO
      FROM VACACIONES 
     WHERE COMPANIA       = UN_COMPANIA 
       AND ID_DE_EMPLEADO = UN_EMPLEADO
       AND UN_FECHA       BETWEEN FECHA_INICIO AND FECHA_FINAL
  ) 
  LOOP       
    MI_RETORNO:= PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD  => PCK_ERRORES.MENS_VACACIONESENTREPERIODO);    
  EXIT;
  END LOOP;  
  RETURN MI_RETORNO;
END FC_VALIDARFECHASVACACIONES ;

--29
FUNCTION FC_DIAPENDIENTEVAC
/*
    NAME              : DIAPENDIENTEVAC
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GÃ“MEZ BLANCO
    DATE MIGRADOR     : 11/08/2015
    TIME              : 08:13 AM
    SOURCE MODULE     : NominaP2015.04.01DUI.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : RETORNA LOS DIAS PENDIENTES DE VACACIONES DE ACUERDO A LOS HISTORICOS
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getDiaPendienteVacacionesHistorico
    @METHOD           : GET
  */
(
  UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROCESO  IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
)
RETURN PCK_SUBTIPOS.TI_DOBLE
  AS  
  MI_FECHA      DATE:= NULL;
  MI_FECHASTR   VARCHAR2(100 CHAR); 
  MI_CONCEPTOD  NUMBER; 
  MI_VALOR      PCK_SUBTIPOS.TI_DOBLE;
  MI_VALOR2     PCK_SUBTIPOS.TI_DOBLE; 
BEGIN
  BEGIN 
    SELECT MAX(VACACIONES.FECHA_FINAL) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) ULTPERIODO
      INTO MI_FECHA
      FROM VACACIONES
     WHERE COMPANIA       = UN_COMPANIA
       AND ID_DE_PROCESO  = UN_PROCESO
       AND ID_DE_EMPLEADO = UN_EMPLEADO;
  EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_FECHA:=NULL;
  END;
  IF MI_FECHA IS NULL THEN
    SELECT FECHA_DE_INGRESO
      INTO MI_FECHA
    FROM PERSONAL
    WHERE COMPANIA        = UN_COMPANIA
      AND ID_DE_EMPLEADO  = UN_EMPLEADO;
  END IF;
  MI_FECHASTR := TO_CHAR(MI_FECHA,'YYYYMM');
  IF TO_NUMBER(TO_CHAR(MI_FECHA,'DD'))>15 THEN
    MI_FECHASTR := MI_FECHASTR || '02';
  ELSE
    MI_FECHASTR := MI_FECHASTR || '01';
  END IF;
  IF PCK_NOMINA_COM1.FC_PARAMETRO( UN_COMPANIA => UN_COMPANIA
                                  ,UN_OPCION   => 1) = '892300548-8' THEN
    MI_CONCEPTOD :=-1;
  ELSE
    MI_CONCEPTOD :=91;
  END IF;
  BEGIN 
    SELECT SUM(HISTORICOS.VALOR) DIAS
      INTO MI_VALOR
      FROM HISTORICOS INNER JOIN PERIODOS
              ON  HISTORICOS.COMPANIA      = PERIODOS.COMPANIA
              AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
              AND HISTORICOS.ANO           = PERIODOS.ANO
              AND HISTORICOS.MES           = PERIODOS.MES
              AND HISTORICOS.PERIODO       = PERIODOS.PERIODO 
     WHERE PERIODOS.COMPANIA               =  UN_COMPANIA
       AND PERIODOS.ID_DE_PROCESO          =  UN_PROCESO
       AND PERIODOS.ID >= MI_FECHASTR
       AND HISTORICOS.ID_DE_EMPLEADO         =  UN_EMPLEADO
       AND HISTORICOS.ID_DE_CONCEPTO         =  MI_CONCEPTOD;   
  EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_VALOR := NULL;
  END;
  IF MI_VALOR IS NULL THEN
    MI_VALOR := 0;
  END IF;
  BEGIN
    SELECT SUM(HISTORICOS.VALOR) DIAS
      INTO MI_VALOR2
    FROM   HISTORICOS  INNER JOIN PERIODOS
              ON  HISTORICOS.COMPANIA      = PERIODOS.COMPANIA
              AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
              AND HISTORICOS.ANO           = PERIODOS.ANO
              AND HISTORICOS.MES           = PERIODOS.MES
              AND HISTORICOS.PERIODO       = PERIODOS.PERIODO 
     WHERE PERIODOS.COMPANIA               =  UN_COMPANIA
       AND PERIODOS.ID_DE_PROCESO          =  UN_PROCESO
       AND PERIODOS.ID >= MI_FECHASTR
       AND HISTORICOS.ID_DE_EMPLEADO         =  UN_EMPLEADO
       AND HISTORICOS.ID_DE_CONCEPTO         =  99;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_VALOR2 := NULL;
  END;   
  IF MI_VALOR2 IS NULL THEN
    MI_VALOR2 := 0;
  END IF;

  MI_VALOR:= MI_VALOR - MI_VALOR2;
  RETURN MI_VALOR;

END FC_DIAPENDIENTEVAC;

--30
FUNCTION FC_AVISO_VACACIONES
/*
    NAME              : AVISO_VACACIONES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GÃ“MEZ BLANCO
    DATE MIGRADOR     : 15/08/2015
    TIME              : 08:13 AM
    SOURCE MODULE     : NominaP2015.04.01DUI.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : RETORNA AVISO DE LICENCIAS EXISTENTES EN DADA UNA FECHA INICIAL
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getAvisoVacaciones
    @METHOD           : GET
  */
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_EMPLEADO    IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_FECHAINICIO IN DATE
  )
RETURN CLOB
  AS
    MI_RETORNO     CLOB; 
    MI_ENCABEZADO  CLOB; 
    MI_REEMP_RESUMEN  PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_REEMP_MENSAJE  PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
  <<RECORREVACACIONES>>
  FOR RS IN (SELECT  COMPANIA
                    ,ID_DE_PROCESO
                    ,FECHA_INICIO
                    ,FECHA_FINAL
                    ,FECHAPAGO
                    ,ANO
                    ,MES
                    ,PERIODO
                    ,DIAS
                    ,DIASPENDIENTESS
               FROM VACACIONES
              WHERE COMPANIA        = UN_COMPANIA
                AND ID_DE_EMPLEADO  = UN_EMPLEADO
                AND FECHA_FINAL    >= UN_FECHAINICIO 
             ORDER BY  ANO DESC 
                      ,FECHA_INICIO DESC
             )
  LOOP
    MI_REEMP_RESUMEN(1).CLAVE := 'FECHAINICIO';
    MI_REEMP_RESUMEN(1).VALOR := RPAD(TO_CHAR(RS.FECHA_INICIO,'DD/MM/YYYY'), 10);
    MI_REEMP_RESUMEN(2).CLAVE := 'FECHAFIN';
    MI_REEMP_RESUMEN(2).VALOR := RPAD(TO_CHAR(RS.FECHA_FINAL,'DD/MM/YYYY'), 10);
    MI_REEMP_RESUMEN(3).CLAVE := 'PERIODO';
    MI_REEMP_RESUMEN(3).VALOR := RPAD(RS.PERIODO, 5);
    MI_REEMP_RESUMEN(4).CLAVE := 'MES';
    MI_REEMP_RESUMEN(4).VALOR := RPAD(RS.MES, 5);
    MI_REEMP_RESUMEN(5).CLAVE := 'FECHAPAGO';
    MI_REEMP_RESUMEN(5).VALOR := RPAD(TO_CHAR(RS.FECHAPAGO,'DD/MM/YYYY'), 10);
    MI_REEMP_RESUMEN(6).CLAVE := 'DIAS';
    MI_REEMP_RESUMEN(6).VALOR := NVL(RS.DIAS, 0);
    MI_REEMP_RESUMEN(7).CLAVE := 'PENDIENTES';
    MI_REEMP_RESUMEN(7).VALOR := RPAD(NVL(RS.DIASPENDIENTESS,0),3);
    MI_REEMP_RESUMEN(8).CLAVE := 'SEPARADOR';
    MI_REEMP_RESUMEN(8).VALOR := '................................................................................' 
                                || CHR(10);
    MI_RETORNO:= MI_RETORNO ||
                 PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD  => PCK_ERRORES.MENS_AVISOVACACIONESRESUMEN,
                                        UN_REEMPLAZOS   => MI_REEMP_RESUMEN);    
  END LOOP RECORREVACACIONES;  
  IF MI_RETORNO IS NOT NULL THEN
    MI_ENCABEZADO:=  PCK_SYSMAN_UTL.FC_PADC(
                                      UN_CADENA   => 'VACACIONES'
                                     ,UN_LONGITUD => 80
                                     ,UN_CARACTER => '*') || CHR(10);
    MI_ENCABEZADO:= MI_ENCABEZADO 
                    || ' ................................................................................' ;

    MI_REEMP_MENSAJE(1).CLAVE := 'EMPLEADO';
    MI_REEMP_MENSAJE(1).VALOR := UN_EMPLEADO;
    MI_REEMP_MENSAJE(2).CLAVE := 'SEPARADOR';
    MI_REEMP_MENSAJE(2).VALOR := MI_ENCABEZADO;
    MI_REEMP_MENSAJE(3).CLAVE := 'VACACIONES';
    MI_REEMP_MENSAJE(3).VALOR := MI_RETORNO;    
    MI_RETORNO:= PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD  => PCK_ERRORES.MENS_AVISOVACACIONES,
                                        UN_REEMPLAZOS   => MI_REEMP_MENSAJE);    
  END IF; 

  RETURN MI_RETORNO;

END FC_AVISO_VACACIONES;

--31
FUNCTION FC_AVISO_LICENCIAS
/*
    NAME              : AVISO_LICENCIAS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GÃ“MEZ BLANCO
    DATE MIGRADOR     : 15/08/2015
    TIME              : 08:13 AM
    SOURCE MODULE     : NominaP2015.04.01DUI.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : RETORNA AVISO DE LICENCIAS EXISTENTES EN DADA UNA FECHA INCIAL
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getAvisoLicencias
    @METHOD           : GET
  */
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_EMPLEADO    IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_FECHAINICIO IN DATE,
    UN_FECHAFINAL  IN DATE
  )
RETURN CLOB
  AS
    MI_RETORNO     CLOB; 
    MI_ENCABEZADO  CLOB; 
    MI_DIAS        PCK_SUBTIPOS.TI_ENTERO:= 0;
    MI_REEMP_RESUMEN  PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_REEMP_MENSAJE  PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
  <<RECORRELICENCIAS>>
  FOR RS IN (
    SELECT  TIPOS_LICENCIA.DESCRIPCION
           ,LICENCIAS.FECHA_INICIO
           ,LICENCIAS.FECHA_FINAL
           ,LICENCIAS.DIAS
      FROM LICENCIAS 
        INNER JOIN TIPOS_LICENCIA 
          ON  LICENCIAS.COMPANIA = TIPOS_LICENCIA.COMPANIA 
          AND LICENCIAS.LICENCIA = TIPOS_LICENCIA.LICENCIA 
     WHERE LICENCIAS.COMPANIA       = UN_COMPANIA
       AND LICENCIAS.ID_DE_EMPLEADO = UN_EMPLEADO
       AND LICENCIAS.FECHA_INICIO   BETWEEN UN_FECHAINICIO AND UN_FECHAFINAL
     ORDER BY  LICENCIAS.ANO DESC 
              ,LICENCIAS.FECHA_INICIO DESC)
  LOOP
    MI_REEMP_RESUMEN(1).CLAVE := 'FECHAINICIO';
    MI_REEMP_RESUMEN(1).VALOR := RPAD(TO_CHAR(RS.FECHA_INICIO,'DD/MM/YYYY'), 10);
    MI_REEMP_RESUMEN(2).CLAVE := 'FECHAFIN';
    MI_REEMP_RESUMEN(2).VALOR := RPAD(TO_CHAR(RS.FECHA_FINAL,'DD/MM/YYYY'), 10);
    MI_REEMP_RESUMEN(3).CLAVE := 'TIPOLICENCIA';
    MI_REEMP_RESUMEN(3).VALOR := PCK_SYSMAN_UTL.FC_PADC(RS.DESCRIPCION,30);
    MI_REEMP_RESUMEN(4).CLAVE := 'DIAS';
    MI_REEMP_RESUMEN(4).VALOR := RPAD(NVL(RS.DIAS,0), 5);
    MI_REEMP_RESUMEN(5).CLAVE := 'SEPARADOR';
    MI_REEMP_RESUMEN(5).VALOR := '................................................................................' 
                                || CHR(10);
    MI_RETORNO:= MI_RETORNO ||
                 PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD  => PCK_ERRORES.MENS_AVISOLICENCIASRESUMEN,
                                        UN_REEMPLAZOS   => MI_REEMP_RESUMEN);   

    MI_DIAS:=    MI_DIAS + RS.DIAS;
  END LOOP RECORRELICENCIAS;
  IF  MI_RETORNO IS NOT NULL THEN
    MI_REEMP_MENSAJE(1).CLAVE := 'EMPLEADO';
    MI_REEMP_MENSAJE(1).VALOR := UN_EMPLEADO;
    MI_REEMP_MENSAJE(2).CLAVE := 'LICENCIAS';
    MI_REEMP_MENSAJE(2).VALOR := MI_RETORNO;
    MI_REEMP_MENSAJE(3).CLAVE := 'DIAS';
    MI_REEMP_MENSAJE(3).VALOR := MI_DIAS;    
    MI_RETORNO:= PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD  => PCK_ERRORES.MENS_AVISOLICENCIAS,
                                        UN_REEMPLAZOS   => MI_REEMP_MENSAJE);            
  END IF; 
 RETURN MI_RETORNO;
END FC_AVISO_LICENCIAS;

--32
FUNCTION FC_DIASLIC_CORRERVAC
/*
    NAME              : DIASLIC_CORRERVAC Variable global tomada para retornar los dÃ­as de licencias
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GÃ“MEZ BLANCO
    DATE MIGRADOR     : 15/08/2015
    TIME              : 08:13 AM
    SOURCE MODULE     : NominaP2015.04.01DUI.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              :
    DESCRIPTION       : RETORNA LOS DIAS DE LICNECIAS PARA UN EMPLEADO ENTR FECHAS
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getDiasLicCorrerVacaciones
    @METHOD           : GET
  */
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_EMPLEADO    IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_FECHAINICIO IN DATE,
    UN_FECHAFINAL  IN DATE
  )
RETURN PCK_SUBTIPOS.TI_ENTERO
  AS
    MI_DIAS        PCK_SUBTIPOS.TI_ENTERO :=0;
BEGIN
    --(MZANGUNA:27/11/2018)-Se excluyen los conceptos, Wue segun Nubia Jimenez no aplican
    BEGIN
        SELECT SUM(LICENCIAS.DIAS) DIAS
        INTO MI_DIAS
        FROM LICENCIAS
        INNER JOIN TIPOS_LICENCIA
          ON LICENCIAS.COMPANIA = TIPOS_LICENCIA.COMPANIA
          AND LICENCIAS.LICENCIA = TIPOS_LICENCIA.LICENCIA
        WHERE LICENCIAS.COMPANIA       = UN_COMPANIA
         AND LICENCIAS.ID_DE_EMPLEADO = UN_EMPLEADO
         AND LICENCIAS.FECHA_INICIO   BETWEEN UN_FECHAINICIO AND UN_FECHAFINAL
         AND TIPOS_LICENCIA.ID_DE_CONCEPTO NOT IN(365, 364, 363, 390 );
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_DIAS := 0;
    END;

    IF MI_DIAS IS NULL THEN
        MI_DIAS:=0;
    END IF;
    RETURN MI_DIAS;
END FC_DIASLIC_CORRERVAC;

--33
FUNCTION FC_DIASHABILES_VACACIONES
/*
    NAME              : Evento Inicio_disfrute_AfterUpdate del formulario vacaciones
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GÃ“MEZ BLANCO
    DATE MIGRADOR     : 15/08/2015
    TIME              : 02:20 PM
    SOURCE MODULE     : NominaP2015.04.01DUI.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : RETORNA LOS DIAS HABILES DE ACUERDO A LOS DATOS DE LAS VACACIONES
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getDiasHabilesVacaciones
    @METHOD           : GET
  */
  (
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO       IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES       IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO   IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_EMPLEADO  IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_NUMEROPER IN PCK_SUBTIPOS.TI_ENTERO,
    UN_DIASPENDI IN PCK_SUBTIPOS.TI_ENTERO
  )
RETURN PCK_SUBTIPOS.TI_ENTERO
  AS
    MI_INGRESO     DATE;
    MI_VPER        PCK_SUBTIPOS.TI_PERIODO_NOMI;
    MI_ANTIG       PCK_SUBTIPOS.TI_ENTERO;
BEGIN
  IF UN_EMPLEADO = 0 THEN
    RETURN 0;
  END IF;
  IF PCK_SYSMAN_UTL.FC_PAR(
                    UN_COMPANIA  => UN_COMPANIA
                   ,UN_NOMBRE    => 'TENER EN CUENTA ANTIGUEDAD PARA DIAS VACACIONES'
                   ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                   ,UN_FECHA_PAR => SYSDATE) = 'SI' THEN
    BEGIN 
      SELECT INGRESO_DISTRITO
        INTO MI_INGRESO
        FROM PERSONAL
       WHERE COMPANIA       = UN_COMPANIA
         AND ID_DE_EMPLEADO = UN_EMPLEADO;  
    EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_INGRESO := NULL;
    END;
    IF MI_INGRESO IS NOT NULL 
       AND MI_INGRESO<= TO_DATE('04/08/1994','DD/MM/YYYY') THEN
      MI_VPER:= PCK_SYSMAN_UTL.FC_IIF(
                               UN_CONDICION => UN_PERIODO=1
                              ,UN_SI        => 15
                              ,UN_NO        => PCK_SYSMAN_UTL.FC_IIF(
                                                              UN_CONDICION => UN_MES = 2
                                                             ,UN_SI        => 28
                                                             ,UN_NO        =>30));
      MI_ANTIG:=PCK_SYSMAN_UTL.FC_EDAD( 
                               UN_FECHAINI => MI_INGRESO
                              ,UN_FECHAFIN => TO_DATE( UN_ANO || '/' || UN_MES || '/' || MI_VPER,'DD/MM/YYYY') 
                              ,UN_FORMATO  => 3);
      MI_ANTIG:= TRUNC(MI_ANTIG/360);
      IF MI_ANTIG >= 15 THEN
        RETURN 20 * UN_NUMEROPER;
      ELSIF MI_ANTIG >= 10 THEN
        RETURN 19 * UN_NUMEROPER;
      ELSIF MI_ANTIG >= 6  THEN
        RETURN 18 * UN_NUMEROPER;
      ELSIF MI_ANTIG >= 3  THEN
        RETURN 17 * UN_NUMEROPER;
      ELSIF MI_ANTIG >=1 THEN
        RETURN 15 * UN_NUMEROPER;
      END IF;
    ELSE
      RETURN 15 * UN_NUMEROPER;
    END IF;
  ELSE
    IF UN_DIASPENDI > 0 THEN
      RETURN UN_DIASPENDI;
    ELSE
      RETURN 15 * UN_NUMEROPER;
    END IF;
  END IF;
END FC_DIASHABILES_VACACIONES;

--34
FUNCTION FC_ULTIMAINTERRUPCION
/*
    NAME              : ULTIMAINTERRUPCION
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 18/08/2015
    TIME              : 10:27 AM
    SOURCE MODULE     : NominaP2015.04.01DUI.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     :\ 07/02/2017
    TIME              :
    DESCRIPTION       : RETORNA LOS DATOS DE LA ÃƒÅ¡LTIMA INTERRUPCIÃ“N DE VACACIONES
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getUltimoInterrupcion
    @METHOD           : GET
  */
  (
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO   IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANO       IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES       IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO   IN PCK_SUBTIPOS.TI_PERIODO_NOMI,    
    UN_EMPLEADO  IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO, 
    UN_PARAMETRO IN PCK_SUBTIPOS.TI_ENTERO
  ) 
RETURN DATE
  AS 
    MI_VLRETORNO  DATE;
    MI_FECHA      DATE;
BEGIN
  MI_FECHA:= PCK_NOMINA.FC_FECHAPERIODO( UN_COMPANIA  => UN_COMPANIA
                                        ,UN_PROCESO   => UN_PROCESO
                                        ,UN_ANO       => UN_ANO
                                        ,UN_MES       => UN_MES
                                        ,UN_PERIODO   => UN_PERIODO);
  <<RECORREINTERRUPCIONES>>
  FOR RS IN ( 
    SELECT  V.INICIOV
           ,V.FECHA_INICIO
           ,V.FECHA_FINAL
      FROM (SELECT  ROW_NUMBER() OVER (ORDER BY FECHAINTERRUPCION DESC) REG
                   ,FECHAINTERRUPCION INICIOV
                   ,FECHA_INICIO
                   ,FECHA_FINAL
              FROM INTERRUPCION_VACACIONES
             WHERE COMPANIA                 = UN_COMPANIA
               AND ID_DE_EMPLEADO           = UN_EMPLEADO
               AND FECHAINTERRUPCION       <= MI_FECHA
            ) V
               WHERE V.REG = 1
           ) 
  LOOP
    IF    UN_PARAMETRO = 1 THEN
      MI_VLRETORNO := TO_DATE(TO_CHAR(RS.FECHA_INICIO,'DD/MM/YYYY'),'DD/MM/YYYY');
    ELSIF UN_PARAMETRO = 2 THEN
      MI_VLRETORNO := TO_DATE(TO_CHAR(RS.FECHA_FINAL,'DD/MM/YYYY'),'DD/MM/YYYY');
    ELSIF UN_PARAMETRO = 3 THEN
      MI_VLRETORNO := TO_DATE(TO_CHAR(RS.INICIOV,'DD/MM/YYYY'),'DD/MM/YYYY');
    END IF;
  END LOOP RECORREINTERRUPCIONES;
 RETURN MI_VLRETORNO;
END FC_ULTIMAINTERRUPCION;

--35
FUNCTION FC_FECHAFINAL_VAC
/*
    NAME              : FC_FECHAFINAL_VAC
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 18/08/2015
    TIME              : 10:27 AM
    SOURCE MODULE     : NominaP2015.04.01DUI.accdb
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     :\ 07/02/2017
    TIME              :
    DESCRIPTION       : RETORNA LA FECHA FINAL ED LAS VACACIONES BASADOS EN LA FECHA DE INICIO Y LOS DIAS HABILES 
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getFechaFinalVacaciones
    @METHOD           : GET
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_INICIODISFRUTE IN DATE,
    UN_DIASHABILES    IN PCK_SUBTIPOS.TI_ENTERO,
    UN_SABADOHABIL    IN VARCHAR2,
    UN_MODULO         IN PCK_SUBTIPOS.TI_MODULO
  ) 
RETURN DATE
  AS
 --   MI_ERROR_FUN      NUMBER:= GL_ERROR_NUM + 35; 
    MI_FECHAFIN       DATE;
    MI_DIASVIERNES    PCK_SUBTIPOS.TI_CAMPOS;  
BEGIN
  IF UN_SABADOHABIL ='SI' THEN
    MI_FECHAFIN:=  PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(
                                  UN_COMPANIA => UN_COMPANIA 
                                 ,UN_FECHA    => UN_INICIODISFRUTE
                                 ,UN_DIAS     => UN_DIASHABILES
                                 ,UN_SABADO   => 1);
  ELSE
    MI_FECHAFIN:=  PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(
                                  UN_COMPANIA => UN_COMPANIA 
                                 ,UN_FECHA    => UN_INICIODISFRUTE
                                 ,UN_DIAS     => UN_DIASHABILES);

  END IF;
  MI_DIASVIERNES:= PCK_SYSMAN_UTL.FC_PAR(
                                  UN_COMPANIA  => UN_COMPANIA
                                 ,UN_NOMBRE    => 'CONTAR DIAS VACACIONES HASTA EL VIERNES'
                                 ,UN_MODULO    => UN_MODULO 
                                 ,UN_FECHA_PAR => SYSDATE);

  IF MI_DIASVIERNES = 'NO' THEN
    IF UN_SABADOHABIL = 'SI' THEN
      WHILE (PCK_SYSMAN_UTL.FC_WEEKDAY(UN_FECHA => MI_FECHAFIN+1) IN(1) --MOD JM CC 1741 04/06/2025
             OR PCK_SYSMAN_UTL.FC_ESFESTIVO( UN_COMPANIA => UN_COMPANIA
                                            ,UN_FECHA    => MI_FECHAFIN+1) NOT IN(0))
      LOOP
        MI_FECHAFIN:= MI_FECHAFIN + 1;
      END LOOP;
      ELSE
        WHILE 
          (PCK_SYSMAN_UTL.FC_WEEKDAY(UN_FECHA =>  MI_FECHAFIN+1) IN(7,1) --MOD JM CC 1741 04/06/2025
          OR PCK_SYSMAN_UTL.FC_ESFESTIVO( UN_COMPANIA => UN_COMPANIA
                                         ,UN_FECHA    => MI_FECHAFIN+1) NOT IN(0))
        LOOP
          MI_FECHAFIN:= MI_FECHAFIN + 1;
        END LOOP;
    END IF;
  END IF;
  RETURN MI_FECHAFIN;
END FC_FECHAFINAL_VAC; 

--36
FUNCTION FC_PREPARARPIVOT_DEVENGOSANO
/*
    NAME              : FC_PREPARARPIVOT_KARDEX
    AUTHORS           : SYSMAN  SAS
    AUTHOR            : SANDRA MILENA DAZA LEGUIZAMON
    DATE              : 17/09/2015
    TIME              : 9:30 AM
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 07/02/2017
    TIME              : 
    DESCRIPTION       : Retorna la cadena del PIVOT para consultar los valores devengados por los empleado para la vigencia seleccionada
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getPrepararPivotDevengosAnio
    @METHOD           : GET                    
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO
  )
RETURN VARCHAR2
  AS
    MI_RESULTADO  PCK_SUBTIPOS.TI_STRSQL;
BEGIN 

 BEGIN
   <<RECORREACUMULADOS>>
   FOR RS IN (
     SELECT DISTINCT  V_ACUMULADOS.CLASE
                     ,V_ACUMULADOS.ID_DE_CONCEPTO
                     ,V_ACUMULADOS.CLASE ||'_'|| V_ACUMULADOS.ID_DE_CONCEPTO PIV
                     ,REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(V_ACUMULADOS.NOMBRE_CONCEPTO, ' ', '_'),'(',''),')',''),'<',''),'>','') NOMBRECONCEPTO
                FROM V_ACUMULADOS
               WHERE V_ACUMULADOS.COMPANIA =  UN_COMPANIA 
                 AND V_ACUMULADOS.ANO      =  UN_ANO
                 AND V_ACUMULADOS.CLASE    IN (3,5,8)                
               ORDER BY  V_ACUMULADOS.CLASE 
                        ,V_ACUMULADOS.ID_DE_CONCEPTO
             )  
  LOOP
    MI_RESULTADO:= MI_RESULTADO 
                   ||  RS.PIV 
                   || SUBSTR('''P' || RS.PIV || '_' || RS.NOMBRECONCEPTO, 1, 30) 
                   ||',''' ;
  END LOOP RECORREACUMULADOS;
  MI_RESULTADO := SUBSTR(MI_RESULTADO,1, LENGTH(MI_RESULTADO)-2);
  MI_RESULTADO := REPLACE(MI_RESULTADO, '/', '');
  MI_RESULTADO := REPLACE(MI_RESULTADO, '%', '');
  MI_RESULTADO := REPLACE(MI_RESULTADO, '-', '');
  MI_RESULTADO := REPLACE(MI_RESULTADO, '.', '');
  EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_RESULTADO:='';
  END;
  RETURN MI_RESULTADO;  
END FC_PREPARARPIVOT_DEVENGOSANO;

FUNCTION FC_MIPERSONAL(UN_COMPANIA IN VARCHAR2, UN_EMPLEADO IN VARCHAR2, UN_CAMPO IN VARCHAR2)
 RETURN VARCHAR2
 AS

 MI_VALOR_CAMPO NUMBER :=0;
 MI_SALIDA NUMBER :=0;
BEGIN
  BEGIN
         SELECT 'PERSONAL.'||UN_CAMPO 
         INTO MI_VALOR_CAMPO 
         FROM PERSONAL 
         WHERE PERSONAL.COMPANIA= UN_COMPANIA 
          AND PERSONAL.ID_DE_EMPLEADO= UN_EMPLEADO;
        EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_VALOR_CAMPO :=0;
    END;
         IF MI_VALOR_CAMPO <> 0 THEN

             MI_SALIDA := MI_VALOR_CAMPO;
         ELSE
             MI_SALIDA := 0;
         END IF;

RETURN MI_SALIDA;
END FC_MIPERSONAL;

PROCEDURE PR_ACTUALIZAR_IBC_NOVEDADES
(
/*
   NAME              : PR_ACTUALIZAR_IBC_NOVEDADES
   AUTHORS           : SYSMAN SAS
   AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÃ‘A HURTADO
   DATE MIGRADOR     : 10/09/2018
   TIME              : 9:11 AM
   SOURCE MODULE     : NominaP2018.09.01 Unificadas. En access ACTUALIZAR_IBC_NOVEDADES
   MODIFIER          :
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       :
   MODIFICATIONS     :
 */
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_EMPLEADO       IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_FINI           IN DATE,
    UN_FFIN           IN DATE,
    UN_VALOR          IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0,
    UN_PAR            IN VARCHAR2,
    UN_USUARIO        IN COMPROBANTE_CNT.CREATED_BY%TYPE
)

AS
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONACME    PCK_SUBTIPOS.TI_CONDICION;
    MI_FILAS            PCK_SUBTIPOS.TI_ENTERO;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RS               SYS_REFCURSOR;
BEGIN
    BEGIN
        IF UN_PAR = 'INC' THEN
            MI_TABLA     := ' INCAPACIDADES ';
            MI_CAMPOS    := ' BASEIBC           = '|| UN_VALOR ||'
                             ,MODIFIED_BY       = '''|| UN_USUARIO ||'''
                             ,DATE_MODIFIED     = SYSDATE ';

            MI_CONDICIONACME := 'COMPANIA   = '''|| UN_COMPANIA || '''
                             AND ID_DE_EMPLEADO = '|| UN_EMPLEADO ||'
                             AND (   TO_DATE(TO_CHAR(FECHA_INICIO, ''DD/MM/YYYY''),''DD/MM/YYYY'') BETWEEN TO_DATE(''' || TO_CHAR(UN_FINI, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')  AND TO_DATE(''' || TO_CHAR(UN_FFIN, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                  OR TO_DATE(TO_CHAR(FECHA_FIN, ''DD/MM/YYYY''),''DD/MM/YYYY'')    BETWEEN TO_DATE(''' || TO_CHAR(UN_FINI, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')  AND TO_DATE(''' || TO_CHAR(UN_FFIN, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                  OR ( TO_DATE(TO_CHAR(FECHA_INICIO, ''DD/MM/YYYY''),''DD/MM/YYYY'') < TO_DATE(''' || TO_CHAR(UN_FINI, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'') AND TO_DATE(TO_CHAR(FECHA_FIN, ''DD/MM/YYYY''),''DD/MM/YYYY'') > TO_DATE(''' || TO_CHAR(UN_FFIN, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')  )
                                 )  ';

            MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => MI_CAMPOS,
                                           UN_CONDICION => MI_CONDICIONACME);

        ELSIF UN_PAR = 'LIC' THEN
            MI_TABLA     := ' LICENCIAS ';
            MI_CAMPOS    := ' BASEIBC           = '|| UN_VALOR ||'
                             ,MODIFIED_BY       = '''|| UN_USUARIO ||'''
                             ,DATE_MODIFIED     = SYSDATE ';

            MI_CONDICIONACME := 'COMPANIA   = '''|| UN_COMPANIA || '''
                             AND ID_DE_EMPLEADO = '|| UN_EMPLEADO ||'
                             AND (    ( TO_DATE(TO_CHAR(FECHA_INICIO, ''DD/MM/YYYY''),''DD/MM/YYYY'') <= TO_DATE(''' || TO_CHAR(UN_FFIN, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'') AND TO_DATE(TO_CHAR(FECHA_FINAL, ''DD/MM/YYYY''),''DD/MM/YYYY'') >= TO_DATE(''' || TO_CHAR(UN_FINI, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')  )
                                   OR TO_DATE(TO_CHAR(FECHA_FINAL, ''DD/MM/YYYY''),''DD/MM/YYYY'') >= TO_DATE(''' || TO_CHAR(UN_FINI, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                 )  ';

            MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                           UN_ACCION    => 'M',
                                           UN_CAMPOS    => MI_CAMPOS,
                                           UN_CONDICION => MI_CONDICIONACME);
        ELSIF UN_PAR = 'VAC' THEN
            MI_TABLA     := ' VACACIONES ';
            --IF PCK_NOMINA.CPARENTRADA(1).NIT = '900.334.265-3' THEN
                FOR MI_RS IN
                (
                    SELECT HISTORICOS.COMPANIA, HISTORICOS.ID_DE_PROCESO, HISTORICOS.ANO, HISTORICOS.MES, HISTORICOS.PERIODO, HISTORICOS.ID_DE_EMPLEADO, HISTORICOS.VALOR
                    FROM VACACIONES INNER JOIN HISTORICOS
                      ON  VACACIONES.COMPANIA = HISTORICOS.COMPANIA
                      AND VACACIONES.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
                      AND VACACIONES.ANO = HISTORICOS.ANO
                      AND VACACIONES.MES = HISTORICOS.MES
                      AND VACACIONES.PERIODO = HISTORICOS.PERIODO
                      AND VACACIONES.ID_DE_EMPLEADO = HISTORICOS.ID_DE_EMPLEADO
                    WHERE VACACIONES.COMPANIA = UN_COMPANIA
                      AND VACACIONES.ID_DE_PROCESO = PCK_NOMINA.GL_SPRC
                      AND VACACIONES.ID_DE_EMPLEADO = UN_EMPLEADO
                      AND VACACIONES.BASEIBC = 0
                      AND VACACIONES.ANO >= 2018
                      AND HISTORICOS.ID_DE_CONCEPTO = 960
                )
                LOOP
                    MI_CAMPOS := ' BASEIBC           = '|| MI_RS.VALOR ||'
                                  ,MODIFIED_BY       = '''|| UN_USUARIO ||'''
                                  ,DATE_MODIFIED     = SYSDATE ';

                    MI_CONDICIONACME := 'COMPANIA   = '''|| UN_COMPANIA || '''
                                      AND ID_DE_PROCESO = '|| MI_RS.ID_DE_PROCESO ||'
                                      AND ANO = '|| MI_RS.ANO ||'
                                      AND MES = '|| MI_RS.MES ||'
                                      AND PERIODO = '|| MI_RS.PERIODO ||'
                                      AND ID_DE_EMPLEADO = '|| UN_EMPLEADO ||'
                                      AND BASEIBC = 0
                                      ';

                    MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICIONACME);
                END LOOP;

            /*ELSE
                MI_CAMPOS    := ' BASEIBC           = '|| UN_VALOR ||'
                                 ,MODIFIED_BY       = '''|| UN_USUARIO ||'''
                                 ,DATE_MODIFIED     = SYSDATE ';

                MI_CONDICIONACME := 'COMPANIA   = '''|| UN_COMPANIA || '''
                                 AND ID_DE_EMPLEADO = '|| UN_EMPLEADO ||'
                                 AND BASEIBC = 0
                                 AND DIAS <> 0
                                 AND QUITARPILA = 0
                                 AND (   TO_DATE(TO_CHAR(FINAL_DISFRUTE, ''DD/MM/YYYY''),''DD/MM/YYYY'') BETWEEN TO_DATE(''' || TO_CHAR(UN_FINI, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')  AND TO_DATE(''' || TO_CHAR(UN_FFIN, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                                     OR (TO_DATE(TO_CHAR(INICIO_DISFRUTE, ''DD/MM/YYYY''),''DD/MM/YYYY'') BETWEEN TO_DATE(''' || TO_CHAR(UN_FINI, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')  AND TO_DATE(''' || TO_CHAR(UN_FFIN, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'') AND TO_NUMBER(TO_CHAR(INICIO_DISFRUTE, ''DD'')) <> 31  )
                                     ) ';

                MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                               UN_ACCION    => 'M',
                                               UN_CAMPOS    => MI_CAMPOS,
                                               UN_CONDICION => MI_CONDICIONACME);
            END IF;*/

        END IF;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;

EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
    MI_MSGERROR(1).CLAVE := 'EMPLEADO';
    MI_MSGERROR(1).VALOR := UN_EMPLEADO;

    PCK_ERR_MSG.RAISE_WITH_MSG
        (UN_EXC_COD   => SQLCODE
        ,UN_ERROR_COD => PCK_ERRORES.ERR_ACTIBCNOVEDAD
        ,UN_REEMPLAZOS => MI_MSGERROR );

END PR_ACTUALIZAR_IBC_NOVEDADES;

FUNCTION FC_CALCULARCN112(
/*
   NAME              : PR_ACTUALIZAR_IBC_NOVEDADES
   AUTHORS           : SYSMAN SAS
   AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÃ‘A HURTADO
   DATE MIGRADOR     : 10/09/2018
   TIME              : 2:39 PM
   SOURCE MODULE     : NominaP2018.09.01 Unificadas. En access CALCULAR_CN112_NUEVO
   MODIFIER          :
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       :
   MODIFICATIONS     :
 */
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA
)
RETURN PCK_SUBTIPOS.TI_DOBLE

AS

    MI_RS                   SYS_REFCURSOR;
    MI_RSINTER              SYS_REFCURSOR;
    MI_TDIAS                PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DD                   PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_TOTBASENOV           PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_SUMADIASMES          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_IBCMESANTERIOR       PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_CUENTA               PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASRECONOCERINCP    PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    --PCK_NOMINA.GL_CN112NUEVO           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_BASEPROPORCIONAL     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_SUELDOANNO           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;


    MI_FINICIO              DATE;
    MI_FFINAL               DATE;
    MI_F_INICIO             DATE;
    MI_F_FINAL              DATE;
    MI_TIPOINCAP            INCAPACIDADES.INCAPACIDAD%TYPE;
    MI_LIQUIDAR12DIASCONSBM PCK_SUBTIPOS.TI_PARAMETRO;
    MI_SALARIO_BASE  NUMBER DEFAULT 0;
    MI_CN1           NUMBER DEFAULT 0;

BEGIN
    MI_SALARIO_BASE := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SALARIO_BASE_IBC;    
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' THEN
        MI_SALARIO_BASE := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_SALARIO_BASE * PCK_NOMINA.CPARENTRADA(1).PORC_SALARIO_INTEGRAL / 100,
                                                   UN_PRECISION => 0);    
    END IF;
    MI_CN1 := PCK_NOMINA.FC_CN(1);    
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' THEN
        MI_CN1 := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_CN1 * PCK_NOMINA.CPARENTRADA(1).PORC_SALARIO_INTEGRAL / 100,
                                          UN_PRECISION => 0);          
    END IF;


    PCK_NOMINA_CALCULO.PR_CALCULAR_ARP_MES;
    IF PCK_NOMINA.GL_CN112NUEVO <> 0 THEN
        PCK_NOMINA.CN(112) := PCK_NOMINA.GL_CN112NUEVO;
        RETURN PCK_NOMINA.GL_CN112NUEVO;
    END IF;

    MI_F_INICIO := TO_DATE('01/' || PCK_NOMINA.GL_SMES || '/' || PCK_NOMINA.GL_SANO);
    MI_F_FINAL := LAST_DAY(TO_DATE('01/' || PCK_NOMINA.GL_SMES || '/' || PCK_NOMINA.GL_SANO));

    MI_LIQUIDAR12DIASCONSBM := PCK_PARST.FC_PAR('LIQUIDAR 1-2 DIAS CON S.B.M.', ' ');

    MI_TOTBASENOV := 0;
    MI_IBCMESANTERIOR := 0;
    MI_IBCMESANTERIOR := PCK_NOMINA.FC_CN(348);
    MI_TOTBASENOV := 0;
    MI_SUMADIASMES := 0;
    MI_SUELDOANNO := 0;
    MI_SUELDOANNO := PCK_NOMINA.CCATEGORIA(1).SALARIO_BASE;

    PCK_NOMINA_COM1.PR_ACTUALIZAR_IBC_NOVEDADES(UN_COMPANIA => UN_COMPANIA, 
                                                UN_EMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 
                                                UN_FINI     => MI_F_INICIO, 
                                                UN_FFIN     => MI_F_FINAL, 
                                                UN_VALOR    => (CASE WHEN PCK_NOMINA.CPARENTRADA(1).NIT = '900.334.265-3' 
                                                                      AND PCK_NOMINA.FC_CN(174) <> 0 
                                                                    THEN PCK_NOMINA.FC_CN(960) 
                                                                    ELSE MI_SALARIO_BASE END), 
                                                UN_PAR      => 'VAC', 
                                                UN_USUARIO  => PCK_CONEXION.FC_GETUSER());
    PCK_NOMINA_COM1.PR_ACTUALIZAR_IBC_NOVEDADES(UN_COMPANIA => UN_COMPANIA, 
                                                UN_EMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 
                                                UN_FINI     => MI_F_INICIO, 
                                                UN_FFIN     => MI_F_FINAL, 
                                                UN_VALOR    => PCK_NOMINA.FC_CN(348), 
                                                UN_PAR      => 'INC', 
                                                UN_USUARIO  => PCK_CONEXION.FC_GETUSER());
    PCK_NOMINA_COM1.PR_ACTUALIZAR_IBC_NOVEDADES(UN_COMPANIA => UN_COMPANIA, 
                                                UN_EMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 
                                                UN_FINI     => MI_F_INICIO, 
                                                UN_FFIN     => MI_F_FINAL, 
                                                UN_VALOR    => MI_CN1, 
                                                UN_PAR      => 'LIC', 
                                                UN_USUARIO  => PCK_CONEXION.FC_GETUSER());

    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM12(UN_COMPANIA   => UN_COMPANIA, 
                                             UN_ANO1       => PCK_NOMINA.GL_SANO, 
                                             UN_MES1       => PCK_NOMINA.GL_SMES, 
                                             UN_PERIODO1   => 1, 
                                             UN_ANO2       => PCK_NOMINA.GL_SANO, 
                                             UN_MES2       => PCK_NOMINA.GL_SMES, 
                                             UN_PERIODO2   => 99, 
                                             UN_IDEMPLEADO =>PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    <<CN112>>
    FOR MI_RS IN
    (
        SELECT BASEIBC AS BASEIBC, COMPANIA, ID_DE_EMPLEADO, FECHA_INICIO AS F1, FECHA_FIN AS F2, DIAS AS DIAS, (CASE WHEN INCAPACIDAD = '04' THEN 'NLMA' ELSE (CASE WHEN INCAPACIDAD='05' OR INCAPACIDAD='09' THEN 'NIRP' ELSE 'NIGE' END) END) NOVEDAD
              ,FECHA_INICIO,FECHA_FIN AS FECHA_FINAL
        FROM  INCAPACIDADES
        WHERE COMPANIA = UN_COMPANIA
          AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
          AND (  TO_DATE(TO_CHAR(FECHA_INICIO, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIO AND MI_F_FINAL
              OR TO_DATE(TO_CHAR(FECHA_FIN, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIO AND MI_F_FINAL
              OR (TO_DATE(TO_CHAR(FECHA_INICIO, 'DD/MM/YYYY'),'DD/MM/YYYY') < MI_F_INICIO AND TO_DATE(TO_CHAR(FECHA_FIN, 'DD/MM/YYYY'),'DD/MM/YYYY') > MI_F_FINAL )
              )
        UNION
        SELECT BASEIBC  AS BASEIBC, COMPANIA, ID_DE_EMPLEADO, FECHA_INICIO AS F1, FECHA_FINAL AS F2, DIAS AS DIAS, (CASE WHEN LICENCIA='01' OR LICENCIA='02' OR LICENCIA = '03' THEN 'NSLN' ELSE (CASE WHEN LICENCIA='04' OR LICENCIA='05' OR LICENCIA='10' OR LICENCIA='12' THEN 'LREM' ELSE 'COMIS' END) END) AS NOVEDAD
                ,FECHA_INICIO, FECHA_FINAL
        FROM LICENCIAS
        WHERE COMPANIA = UN_COMPANIA
          AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
          AND ( (TO_DATE(TO_CHAR(FECHA_INICIO, 'DD/MM/YYYY'),'DD/MM/YYYY') <= MI_F_FINAL AND TO_DATE(TO_CHAR(FECHA_FINAL, 'DD/MM/YYYY'),'DD/MM/YYYY') >= MI_F_INICIO )
              OR TO_DATE(TO_CHAR(FECHA_FINAL, 'DD/MM/YYYY'),'DD/MM/YYYY') >= MI_F_INICIO
              )
        UNION
        SELECT BASEIBC AS BASEIBC, COMPANIA, ID_DE_EMPLEADO, INICIO_DISFRUTE AS F1, FINAL_DISFRUTE AS F2, DIAS, 'NVAC' AS NOVEDAD, FECHA_INICIO, FECHA_FINAL
        FROM VACACIONES
        WHERE COMPANIA = UN_COMPANIA
          AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
          AND ( TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIO AND MI_F_FINAL
             OR (TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIO AND MI_F_FINAL AND TO_NUMBER(TO_CHAR(INICIO_DISFRUTE ,'DD')) <> 31  )
          )
          AND QUITARPILA = 0
          AND DIAS <> 0
    )
    LOOP
        MI_CUENTA := MI_CUENTA + 1;

        BEGIN
            --MI_TIPOINCAP := TIPO_INCAPACIDAD_FECHAS(UNCOMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, MI_RS.F1, MI_RS.F2, 1);
            SELECT INCAPACIDAD
            INTO MI_TIPOINCAP
            FROM INCAPACIDADES
            WHERE COMPANIA = UN_COMPANIA
              AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
              AND FECHA_INICIO = MI_RS.F1
              AND FECHA_FIN = MI_RS.F2
              AND ROWNUM = 1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_TIPOINCAP := '';
        END;

        IF MI_RS.F1 >= MI_F_INICIO AND MI_RS.F1 <= MI_F_FINAL THEN
            MI_FINICIO := MI_RS.F1;
        ELSIF MI_RS.F1 <= MI_F_INICIO THEN
            MI_FINICIO := MI_F_INICIO;
        ELSE
            MI_FINICIO := MI_RS.F1;
        END IF;

        IF MI_RS.F2 >= MI_F_INICIO AND MI_RS.F2 <= MI_F_FINAL THEN
            MI_FFINAL := MI_RS.F2;
        ELSIF MI_RS.F2 > MI_F_FINAL THEN
            MI_FFINAL := MI_F_FINAL;
        ELSE
            MI_FFINAL := MI_RS.F2;
        END IF;

        IF MI_TIPOINCAP = '01' THEN
            MI_IBCMESANTERIOR := (CASE WHEN MI_RS.BASEIBC <> 0 AND PCK_NOMINA.FC_CN(174) <> 0 THEN MI_RS.BASEIBC ELSE (CASE WHEN MI_RS.BASEIBC > 0 THEN MI_RS.BASEIBC ELSE MI_SALARIO_BASE END) END);
            MI_DIASRECONOCERINCP := TO_NUMBER(PCK_PARST.FC_PAR('DIAS A RECONOCER EN INCAPACIDAD INICIAL' , '2'));
            MI_DD := (CASE WHEN PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FINICIO, MI_FFINAL) >= MI_DIASRECONOCERINCP THEN MI_DIASRECONOCERINCP ELSE 1 END);
            MI_TDIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FINICIO, MI_FFINAL) - MI_DD ;
            IF MI_RS.F1 <= MI_F_INICIO - 2 THEN
                MI_DD := 0 ;
                MI_TDIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FINICIO, MI_FFINAL) + MI_DD ;
            END IF;

            IF PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) / 3 * 2 + 0.05, 0), PCK_NOMINA.GL_RBASE1990) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_DD, 0, FALSE, FALSE), PCK_NOMINA.GL_RBASE1990) < PCK_SYSMAN_UTL.FC_ROUND(PCK_PARENTR.PARAMETRO20 / 30 * (MI_TDIAS + MI_DD), 0) AND PCK_PARST.FC_PAR('PAGAR INCAPACIDADES AL CIEN POR CIENTO', ' ') = 'NO' THEN
                MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(PCK_PARENTR.PARAMETRO20, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) + 0.05, 0), PCK_NOMINA.GL_RBASE1990) + PCK_SYSMAN_UTL.FC_ROUND(MI_SALARIO_BASE / 30 * MI_DD, PCK_NOMINA.GL_RBASE1990) ;
            ELSE
                IF PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_DD, 0, FALSE, FALSE), PCK_NOMINA.GL_RBASE1990) - (PCK_NOMINA.FC_CNP(378) + PCK_NOMINA.FC_CN(378)) < 50000 AND (PCK_NOMINA.FC_CNP(358) + PCK_NOMINA.FC_CN(358)) = MI_DD THEN
                    IF (PCK_NOMINA.FC_CNP(379) - PCK_NOMINA.FC_CNP(378) + PCK_NOMINA.FC_CN(379) - PCK_NOMINA.FC_CN(378)) - PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) / 3 * 2 + 0.05, 0), PCK_NOMINA.GL_RBASE1990) < 5000 THEN
                        MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(379) - PCK_NOMINA.FC_CNP(378) + PCK_NOMINA.FC_CN(379) - PCK_NOMINA.FC_CN(378)), PCK_NOMINA.GL_RBASE1990) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNP(378) + PCK_NOMINA.FC_CN(378), PCK_NOMINA.GL_RBASE1990) ;
                    ELSE
                        IF PCK_PARST.FC_PAR('PAGAR INCAPACIDADES AL CIEN POR CIENTO', ' ') = 'SI' OR (PCK_NOMINA.CPARENTRADA(1).NIT = '800.091.594-4' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_SINDICATO2 = '001') THEN
                            MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) + 0.05, 0), PCK_NOMINA.GL_RBASE1990) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNP(378) + PCK_NOMINA.FC_CN(378), PCK_NOMINA.GL_RBASE1990) ;
                        ELSE
                            IF PCK_NOMINA.CPARENTRADA(1).NIT = '890.680.053-6' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                                MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) + 0.05, 0), PCK_NOMINA.GL_RBASE1990) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNP(378) + PCK_NOMINA.FC_CN(378), PCK_NOMINA.GL_RBASE1990) ;
                            ELSE
                                MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) / 3 * 2 + 0.05, 0), PCK_NOMINA.GL_RBASE1990) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNP(378) + PCK_NOMINA.FC_CN(378), PCK_NOMINA.GL_RBASE1990) ;
                            END IF;
                        END IF;

                    END IF;
                ELSIF (PCK_NOMINA.FC_CNP(370) + PCK_NOMINA.FC_CN(370)) < PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_DD, 0, FALSE, FALSE), PCK_NOMINA.GL_RBASE1990) AND (PCK_NOMINA.FC_CNP(358) + PCK_NOMINA.FC_CN(358)) = 0 AND (PCK_NOMINA.FC_CNP(378) + PCK_NOMINA.FC_CNP(378)) = 0 THEN
                    IF PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(370) + PCK_NOMINA.FC_CN(370)), 2) = PCK_SYSMAN_UTL.FC_ROUND(PCK_PARENTR.PARAMETRO20 / 30 * (MI_TDIAS + MI_DD), 0) THEN
                        MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_PARENTR.PARAMETRO20 / 30 * (MI_TDIAS + MI_DD) + 0.5, 0), 0) ;
                    ELSE
                        MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) / 3 * 2 + 0.05, 0), PCK_NOMINA.GL_RBASE1990)
                            + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE((CASE WHEN MI_LIQUIDAR12DIASCONSBM = 'SI' THEN (CASE WHEN PCK_NOMINA.FC_CN(10) = 0 THEN MI_SUELDOANNO ELSE PCK_NOMINA.FC_CN(10)END) ELSE MI_IBCMESANTERIOR END), MI_DD, 0, FALSE, FALSE), PCK_NOMINA.GL_RBASE1990) ;
                    END IF;
                ELSE

                    IF PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) / 3 * 2 + 0.05, 0), PCK_NOMINA.GL_RBASE1990) < PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(PCK_PARENTR.PARAMETRO20, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) + 0.05, 0), PCK_NOMINA.GL_RBASE1990) AND PCK_PARST.FC_PAR('PAGAR INCAPACIDADES AL CIEN POR CIENTO', ' ') = 'NO' THEN
                        MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(PCK_PARENTR.PARAMETRO20, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) + 0.05, 0), PCK_NOMINA.GL_RBASE1990)
                            + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE((CASE WHEN MI_LIQUIDAR12DIASCONSBM = 'SI' THEN (CASE WHEN PCK_NOMINA.FC_CN(10) = 0 THEN MI_SUELDOANNO ELSE PCK_NOMINA.FC_CN(10) END) ELSE MI_IBCMESANTERIOR END), MI_DD, 0, FALSE, FALSE), PCK_NOMINA.GL_RBASE1990) ;
                    ELSE
                        IF PCK_PARST.FC_PAR('PAGAR INCAPACIDADES AL CIEN POR CIENTO', ' ') = 'SI' THEN
                            MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) + 0.05, 0), PCK_NOMINA.GL_RBASE1990)
                                + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE((CASE WHEN MI_LIQUIDAR12DIASCONSBM = 'SI' THEN (CASE WHEN PCK_NOMINA.FC_CN(10) = 0 THEN MI_SUELDOANNO ELSE PCK_NOMINA.FC_CN(10) END) ELSE MI_IBCMESANTERIOR END), MI_DD, 0, FALSE, FALSE), PCK_NOMINA.GL_RBASE1990) ;
                        ELSE
                            IF PCK_NOMINA.CPARENTRADA(1).NIT = '890.680.053-6' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                                MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) + 0.05, 0), PCK_NOMINA.GL_RBASE1990) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_DD, 0, FALSE, FALSE), PCK_NOMINA.GL_RBASE1990) ;
                            ELSE
                                MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) / 3 * 2 + 0.05, 0), PCK_NOMINA.GL_RBASE1990)
                                    + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE((CASE WHEN MI_LIQUIDAR12DIASCONSBM = 'SI' THEN (CASE WHEN PCK_NOMINA.FC_CN(10) = 0 THEN MI_SUELDOANNO ELSE PCK_NOMINA.FC_CN(10) END) ELSE MI_IBCMESANTERIOR END), MI_DD, 0, FALSE, FALSE), PCK_NOMINA.GL_RBASE1990) ;
                            END IF;
                        END IF;
                    END IF;
                END IF;
            END IF;
            MI_TDIAS := MI_TDIAS + MI_DD;
            MI_SUMADIASMES := MI_SUMADIASMES + MI_TDIAS;
        ELSIF MI_TIPOINCAP = '02' OR MI_TIPOINCAP = '03' THEN
            MI_DD := 0;
            MI_TDIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FINICIO, MI_FFINAL);
            IF PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) / 3 * 2 + 0.05, 0), PCK_NOMINA.GL_RBASE1990) < PCK_SYSMAN_UTL.FC_ROUND(PCK_PARENTR.PARAMETRO20 / 30 * (MI_TDIAS + MI_DD), 0) THEN
                MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(PCK_PARENTR.PARAMETRO20, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) + 0.05, 0), PCK_NOMINA.GL_RBASE1990) ;
            ELSE
                IF PCK_NOMINA.CPARENTRADA(1).NIT = '890.680.053-6' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                    MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE), 0) + 0.05, PCK_NOMINA.GL_RBASE1990) ;
                ELSE
                    MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) / 3 * 2, 0) + 0.05, PCK_NOMINA.GL_RBASE1990) ;
                END IF;
            END IF;
            MI_TDIAS := MI_TDIAS + MI_DD;
            MI_SUMADIASMES := MI_SUMADIASMES + MI_TDIAS;

        ELSIF MI_TIPOINCAP = '05' THEN
            MI_TDIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FINICIO, MI_FFINAL) - 1;
            MI_DD := (CASE WHEN PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FINICIO, MI_FFINAL) >= 1 THEN 1 ELSE 0 END);
            IF PCK_PARST.FC_PAR('PAGAR INCAPACIDADES AL CIEN POR CIENTO', ' ') = 'SI' THEN
                MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE), 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_DD, 0, FALSE, FALSE), 0), PCK_NOMINA.GL_RBASE1990);
                MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_DD, 0, FALSE, FALSE), 0), PCK_NOMINA.GL_RBASE1990);
            ELSE
                MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE), 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_DD, 0, FALSE, FALSE), 0), PCK_NOMINA.GL_RBASE1990);
                MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_DD, 0, FALSE, FALSE), 0), PCK_NOMINA.GL_RBASE1990);
            END IF;
            MI_TDIAS := MI_TDIAS + MI_DD;
        ELSIF MI_TIPOINCAP = '10' THEN
            MI_DD := 0;
            MI_TDIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FINICIO, MI_FFINAL);
            IF PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) / 2 + 0.05, 0), PCK_NOMINA.GL_RBASE1990) < PCK_SYSMAN_UTL.FC_ROUND(PCK_PARENTR.PARAMETRO20 / 30 * (MI_TDIAS + MI_DD), 0) THEN
                MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(PCK_PARENTR.PARAMETRO20, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) + 0.05, 0), PCK_NOMINA.GL_RBASE1990)  ;
            ELSE
                IF PCK_NOMINA.CPARENTRADA(1).NIT = '890.680.053-6' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                    MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE), 0) + 0.05, PCK_NOMINA.GL_RBASE1990) ;
                ELSE
                    MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) / 2, 0) + 0.05, PCK_NOMINA.GL_RBASE1990) ;
                END IF;
            END IF;
            MI_TDIAS := MI_TDIAS + MI_DD;
            MI_SUMADIASMES := MI_SUMADIASMES + MI_TDIAS;

        ELSIF MI_TIPOINCAP = '12' THEN
            MI_DD := 0 ;
            MI_TDIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FINICIO, MI_FFINAL);
            MI_IBCMESANTERIOR := PCK_NOMINA.FC_CNP(112);
            MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE), 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_DD, 0, FALSE, FALSE), 0), PCK_NOMINA.GL_RBASE1990);
            MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_DD, 0, FALSE, FALSE), 0), PCK_NOMINA.GL_RBASE1990);
            MI_TDIAS := MI_TDIAS + MI_DD;
        ELSE
            MI_DIASRECONOCERINCP := PCK_PARST.FC_PAR('NUMERO DIAS PAGO PATRONO AMBULATORIO', 2);
            MI_TDIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FINICIO, MI_FFINAL);

            IF MI_RS.NOVEDAD = 'NVAC' AND MI_TIPOINCAP = '' AND PCK_NOMINA.FC_CNP(79) + PCK_NOMINA.FC_CN(79) <> 0 AND PCK_NOMINA.FC_CNP(155) + PCK_NOMINA.FC_CN(155) <> 0 THEN
                MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(112) - PCK_NOMINA.FC_CNP(212) + PCK_NOMINA.FC_CN(112) - PCK_NOMINA.FC_CN(212)), PCK_NOMINA.GL_RBASE1990);
                MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(112) - PCK_NOMINA.FC_CNP(212) + PCK_NOMINA.FC_CN(112) - PCK_NOMINA.FC_CN(212)), PCK_NOMINA.GL_RBASE1990);
            ELSIF MI_RS.NOVEDAD = 'NVAC' AND MI_TIPOINCAP = '' AND (PCK_NOMINA.FC_CNP(348) + PCK_NOMINA.FC_CN(348)) > PCK_SYSMAN_UTL.FC_ROUND(MI_SALARIO_BASE, 0) AND (PCK_NOMINA.FC_CNP(379) + PCK_NOMINA.FC_CN(379)) = 0 THEN
                IF PCK_SYSMAN_UTL.FC_ROUND((PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(112) - PCK_NOMINA.FC_CNP(212)), PCK_NOMINA.GL_RBASE1990) / MI_TDIAS * 30) - 1, 0) < 0 THEN
                    IF PCK_NOMINA.FC_CNP(348) > MI_SALARIO_BASE THEN
                        MI_IBCMESANTERIOR := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(348), 0);
                        MI_IBCMESANTERIOR := PCK_SYSMAN_UTL.FC_ROUND(MI_SALARIO_BASE, 0);
                    END IF;
                ELSE
                    MI_IBCMESANTERIOR := PCK_SYSMAN_UTL.FC_ROUND((PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(112) - PCK_NOMINA.FC_CNP(212)), PCK_NOMINA.GL_RBASE1990) / MI_TDIAS * 30) - 1, 0);
                END IF;

                IF MI_SALARIO_BASE = PCK_PARENTR.PARAMETRO20 AND PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(112) - PCK_NOMINA.FC_CNP(212)) + 0.51, 0) = PCK_SYSMAN_UTL.FC_ROUND(MI_SALARIO_BASE / 30 * MI_TDIAS + 0.51, 0) THEN
                    MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(112) - PCK_NOMINA.FC_CNP(212)) + 0.51, PCK_NOMINA.GL_RBASE1990);
                    MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(112) - PCK_NOMINA.FC_CNP(212)) + 0.51, PCK_NOMINA.GL_RBASE1990);
                ELSE
                    IF PCK_NOMINA.FC_CNP(348) > MI_SALARIO_BASE THEN
                        MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE), 0);
                        MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE), 0);
                    ELSE
                        IF MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(112) - PCK_NOMINA.FC_CNP(212)), PCK_NOMINA.GL_RBASE1990) < 0 THEN
                            MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(212) - PCK_NOMINA.FC_CNP(112)), PCK_NOMINA.GL_RBASE1990);
                            MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(212) - PCK_NOMINA.FC_CNP(112)), PCK_NOMINA.GL_RBASE1990);
                        ELSE
                            MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(112) - PCK_NOMINA.FC_CNP(212)), PCK_NOMINA.GL_RBASE1990);
                            MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(112) - PCK_NOMINA.FC_CNP(212)), PCK_NOMINA.GL_RBASE1990);
                        END IF;
                    END IF;
                END IF;
                --RESTARBASE := 1;
                --RESPETARIBCMESANTERIOR = -1;
            ELSIF MI_RS.NOVEDAD = 'NLMA' AND MI_TIPOINCAP = '04' AND PCK_NOMINA.FC_CNP(348) > PCK_SYSMAN_UTL.FC_ROUND(MI_SALARIO_BASE, 0) AND (PCK_NOMINA.FC_CNP(379) - PCK_NOMINA.FC_CNP(373)) = 0 THEN
                MI_IBCMESANTERIOR := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(112) - PCK_NOMINA.FC_CNP(212)), PCK_NOMINA.GL_RBASE1990) / PCK_NOMINA.FC_CNP(353) * 30, 0);
                MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(112) - PCK_NOMINA.FC_CNP(212)), PCK_NOMINA.GL_RBASE1990);
                MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(112) - PCK_NOMINA.FC_CNP(212)), PCK_NOMINA.GL_RBASE1990);
                --RESTARBASE := 0: RESPETARIBCMESANTERIOR = -1;
            ELSE
                IF MI_RS.NOVEDAD = 'LREM' AND PCK_NOMINA.FC_CNP(390) + PCK_NOMINA.FC_CN(390) = MI_TDIAS THEN
                    MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNP(385) + PCK_NOMINA.FC_CN(385), PCK_NOMINA.GL_RBASE1990);
                    MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNP(385) + PCK_NOMINA.FC_CN(385), PCK_NOMINA.GL_RBASE1990), PCK_NOMINA.GL_RBASE1990);

                    MI_IBCMESANTERIOR := PCK_SYSMAN_UTL.FC_ROUND(MI_SALARIO_BASE, 0);

                ELSIF MI_RS.NOVEDAD = 'LREM' AND PCK_NOMINA.FC_CNP(363) + PCK_NOMINA.FC_CN(363) = MI_TDIAS THEN
                    MI_IBCMESANTERIOR := PCK_SYSMAN_UTL.FC_ROUND(MI_CN1, 0) ;
                    MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNP(382) + PCK_NOMINA.FC_CN(382), PCK_NOMINA.GL_RBASE1990) ;
                ELSIF MI_RS.NOVEDAD = 'COMIS' AND PCK_NOMINA.FC_CNP(339) + PCK_NOMINA.FC_CN(339) = MI_TDIAS THEN
                    MI_IBCMESANTERIOR := PCK_SYSMAN_UTL.FC_ROUND(MI_CN1, 0) ;
                    MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE), PCK_NOMINA.GL_RBASE1990)  ;
                ELSIF (MI_RS.NOVEDAD = 'LREM' AND PCK_NOMINA.FC_CNP(365) + PCK_NOMINA.FC_CN(365) = MI_TDIAS) OR (MI_RS.NOVEDAD = 'LREM' AND PCK_NOMINA.FC_CNP(364) + PCK_NOMINA.FC_CN(364) = MI_TDIAS) THEN
                    IF MI_IBCMESANTERIOR > MI_SALARIO_BASE AND PCK_NOMINA.FC_CN(10) > 0 THEN
                        MI_IBCMESANTERIOR := PCK_NOMINA.FC_CN(10);
                    ELSE
                        MI_IBCMESANTERIOR := PCK_SYSMAN_UTL.FC_ROUND(MI_SALARIO_BASE, 0);
                    END IF;
                    MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNP(384) + PCK_NOMINA.FC_CN(384) + PCK_NOMINA.FC_CNP(383) + PCK_NOMINA.FC_CN(383), PCK_NOMINA.GL_RBASE1990);
                    MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNP(384) + PCK_NOMINA.FC_CN(384) + PCK_NOMINA.FC_CNP(383) + PCK_NOMINA.FC_CN(383), PCK_NOMINA.GL_RBASE1990), PCK_NOMINA.GL_RBASE1990);
                ELSE

                    IF PCK_PARST.FC_PAR('SUMAR VALOR TOTAL DE VACACIONES PAGADAS A SS', ' ') = 'SI' THEN
                        MI_IBCMESANTERIOR := (CASE WHEN MI_RS.BASEIBC <> 0 AND PCK_NOMINA.FC_CN(174) <> 0 THEN MI_RS.BASEIBC ELSE (CASE WHEN MI_RS.BASEIBC > 0 THEN MI_RS.BASEIBC ELSE MI_SALARIO_BASE END )END);
                        IF PCK_PARST.FC_PAR('PAGAR EL DIA 31 EN VACACIONES', ' ') = 'SI' AND TO_CHAR(MI_F_FINAL, 'DD') = '31' AND MI_RS.F2 >= PCK_NOMINA.GL_FECHAFIN THEN
                            MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE), PCK_NOMINA.GL_RBASE1990)   ;
                            MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS + 1, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE), PCK_NOMINA.GL_RBASE1990)      ;
                        ELSE
                            MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE), PCK_NOMINA.GL_RBASE1990) ;
                            MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE), PCK_NOMINA.GL_RBASE1990)        ;
                        END IF;
                        MI_SUMADIASMES := MI_SUMADIASMES + MI_TDIAS;
                        PCK_NOMINA_COM1.PR_ACTUALIZAR_IBC_NOVEDADES(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, MI_FINICIO, MI_FFINAL, MI_IBCMESANTERIOR, 'VAC', PCK_CONEXION.FC_GETUSER());
                    ELSE
                        MI_IBCMESANTERIOR := (CASE WHEN MI_RS.BASEIBC <> 0 AND PCK_NOMINA.FC_CN(174) <> 0 THEN MI_RS.BASEIBC ELSE (CASE WHEN MI_RS.BASEIBC > 0 THEN MI_RS.BASEIBC ELSE MI_SALARIO_BASE END) END)         ;
                        MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE), PCK_NOMINA.GL_RBASE1990) ;
                        MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE(MI_IBCMESANTERIOR, MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE), PCK_NOMINA.GL_RBASE1990) ;
                    END IF;
                END IF;
            END IF;
        END IF;

        IF MI_RS.NOVEDAD = 'NVAC' THEN
            FOR MI_RSINTER IN
            (
                SELECT FECHAINTERRUPCION
                FROM  INTERRUPCION_VACACIONES
                WHERE COMPANIA =  UN_COMPANIA
                  AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                  AND ENDINERO = 0
                  AND QUITARPILA = 0
                  AND (( TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIO AND MI_F_FINAL -- MOD JM 26/05/2025 se agregan parentesis para que tome la informacion correcta
                     OR TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIO AND MI_F_FINAL
                      )
                  AND ( TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_RS.FECHA_INICIO AND MI_RS.FECHA_FINAL
                     OR TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_RS.FECHA_INICIO AND MI_RS.FECHA_FINAL
                      )
                  OR ((EXTRACT(MONTH FROM MI_F_FINAL) BETWEEN EXTRACT(MONTH FROM FECHAINTERRUPCION) AND EXTRACT(MONTH FROM FINAL_DISFRUTE))  --JM 12/05/2025 CC 1513 si es mas de un periodo no va a encontrar el mes intermedio
                       AND (EXTRACT(YEAR FROM MI_F_FINAL) BETWEEN EXTRACT(YEAR FROM FECHAINTERRUPCION) AND EXTRACT(YEAR FROM FINAL_DISFRUTE)) ))  --JM 12/05/2025 CC 1513 si es mas de un periodo no va a encontrar el mes intermedio
            )
            LOOP
                IF MI_RSINTER.FECHAINTERRUPCION >= MI_F_INICIO AND MI_RSINTER.FECHAINTERRUPCION <= MI_F_FINAL THEN
                    MI_FFINAL := MI_RSINTER.FECHAINTERRUPCION;
                ELSIF MI_RSINTER.FECHAINTERRUPCION > MI_F_FINAL THEN
                    MI_FFINAL := MI_F_FINAL;
                ELSIF MI_RSINTER.FECHAINTERRUPCION < MI_F_INICIO THEN
                    MI_FFINAL := MI_F_INICIO;
                ELSE
                    MI_FFINAL := MI_RSINTER.FECHAINTERRUPCION;
                END IF;

                MI_IBCMESANTERIOR := PCK_NOMINA.FC_CNP(348) + PCK_NOMINA.FC_CN(348);
                MI_TOTBASENOV := MI_TOTBASENOV - MI_TOTBASENOV;
                MI_TDIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FINICIO, MI_FFINAL) - 1;
                MI_TOTBASENOV := MI_TOTBASENOV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_SEGSOCI.FC_BASE((PCK_NOMINA.FC_CNP(348) + PCK_NOMINA.FC_CN(348)), MI_TDIAS, PCK_NOMINA.GL_RBASE1990, FALSE, FALSE), PCK_NOMINA.GL_RBASE1990);
            END LOOP;
        END IF;
    END LOOP CN112;

    IF MI_CUENTA > 0 THEN
        IF MI_SUMADIASMES >= 30 THEN
            PCK_NOMINA.GL_CN112NUEVO := MI_TOTBASENOV + PCK_NOMINA.FC_CN(212) ;
        ELSIF MI_SUMADIASMES < 30 AND PCK_NOMINA.FC_CN(9) <> 0 AND PCK_NOMINA.FC_CN(347) = 0 THEN
            PCK_NOMINA.GL_CN112NUEVO := MI_TOTBASENOV + PCK_NOMINA.FC_CN(212) ;
        ELSE
            PCK_NOMINA.GL_CN112NUEVO := MI_TOTBASENOV + PCK_NOMINA.FC_CN(212) ;
        END IF;
        PCK_NOMINA.GL_CN112NUEVO := (CASE WHEN PCK_NOMINA.GL_CN112NUEVO = (PCK_PARENTR.PARAMETRO20 + 1) THEN PCK_NOMINA.GL_CN112NUEVO - 1 ELSE PCK_NOMINA.GL_CN112NUEVO END) ;
    ELSE
        IF MI_SUMADIASMES >= 30 THEN
            PCK_NOMINA.GL_CN112NUEVO := MI_TOTBASENOV + PCK_NOMINA.FC_CN(212);
        ELSIF MI_SUMADIASMES < 30 AND PCK_NOMINA.FC_CN(9) <> 0 AND PCK_NOMINA.FC_CN(347) = 0 THEN
            PCK_NOMINA.GL_CN112NUEVO := MI_TOTBASENOV + PCK_NOMINA.FC_CN(212);
        ELSIF MI_SUMADIASMES = 0 AND PCK_NOMINA.FC_CN(9) <> 0 AND (PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNP(356) + PCK_NOMINA.FC_CNP(357) + PCK_NOMINA.FC_CNP(359)) <> 0 THEN
            PCK_NOMINA.GL_CN112NUEVO := MI_TOTBASENOV + PCK_NOMINA.FC_CN(212);
        ELSIF MI_SUMADIASMES = 0 AND PCK_NOMINA.FC_CN(9) <> 0 AND (PCK_NOMINA.FC_CN(363) + PCK_NOMINA.FC_CNP(363)) <> 0 THEN
            PCK_NOMINA.GL_CN112NUEVO := MI_TOTBASENOV + PCK_NOMINA.FC_CN(212);
        --(APINEDA:18/09/2018)- Se adiciona debido a que faltaba calculo de CN112 cuando el empleado tiene encargo 
        ELSIF PCK_NOMINA.FC_CN(11) = PCK_NOMINA.FC_CN(4) AND MI_SUMADIASMES = 0 THEN
            PCK_NOMINA.GL_CN112NUEVO := MI_TOTBASENOV + PCK_NOMINA.FC_CN(212);
        END IF;

        IF PCK_NOMINA.GL_CN112NUEVO > PCK_PARENTR.PARAMETRO50 * (CASE WHEN PCK_NOMINA.FC_CN(201) = 0 THEN PCK_PARENTR.PARAMETRO20 ELSE PCK_NOMINA.FC_CN(201) END) AND PCK_PARENTR.PARAMETRO50 > 0 THEN
            PCK_NOMINA.GL_CN112NUEVO := PCK_SYSMAN_UTL.FC_ROUND(PCK_PARENTR.PARAMETRO50 * (CASE WHEN PCK_NOMINA.FC_CN(201) = 0 THEN PCK_PARENTR.PARAMETRO20 ELSE PCK_NOMINA.FC_CN(201) END), 0) ;
            IF PCK_NOMINA.GL_CN112NUEVO > PCK_PARENTR.PARAMETRO50 * (CASE WHEN PCK_NOMINA.FC_CN(201) = 0 THEN PCK_PARENTR.PARAMETRO20 ELSE PCK_NOMINA.FC_CN(201) END) THEN
                PCK_NOMINA.GL_CN112NUEVO := PCK_SYSMAN_UTL.FC_ROUND(PCK_PARENTR.PARAMETRO50 * (CASE WHEN PCK_NOMINA.FC_CN(201) = 0 THEN PCK_PARENTR.PARAMETRO20 ELSE PCK_NOMINA.FC_CN(201) END), 0);
            END IF;
        END IF;

    END IF;
    IF PCK_NOMINA.FC_CN(112) = PCK_NOMINA.GL_CN112NUEVO THEN
        PCK_NOMINA.CN(112) := PCK_NOMINA.GL_CN112NUEVO;
    ELSE
        PCK_NOMINA.CN(112) := PCK_NOMINA.GL_CN112NUEVO;
    END IF;

    RETURN PCK_NOMINA.FC_CN(112);

END FC_CALCULARCN112;

FUNCTION FC_CALCULARBASENOVEDADES_SEG

/*
NAME              : PR_ACTUALIZAR_IBC_NOVEDADES
AUTHORS           : SYSMAN SAS
AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÃ‘A HURTADO
DATE MIGRADOR     : 17/09/2018
TIME              : 9:11 AM
SOURCE MODULE     : Proceso para guardar las bases por novedad en nueva tabla
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       :
MODIFICATIONS     :
*/
(
UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
UN_EMPLEADO          IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
UN_ANO             IN PCK_SUBTIPOS.TI_ANIO,
UN_MES             IN PCK_SUBTIPOS.TI_MES,
UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_DOBLE

AS
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICIONACME    PCK_SUBTIPOS.TI_CONDICION;
    MI_FILAS            PCK_SUBTIPOS.TI_ENTERO;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_BASE             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_BASEPROPIIF      PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIAS             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DD               PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_BASEPROPORCIONAL PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_F_INICIOPERACT   DATE;   --Guarda inicio y fin de perido act
    MI_F_FINALPERACT    DATE;

    MI_FINICIO          DATE;  --Fechas ajustadas si la novedad se pasa del mes
    MI_FFINAL           DATE;

    MI_LLAVE            VARCHAR2(100 CHAR);
    MI_CUENTA           PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_RSINTER          SYS_REFCURSOR;
    MI_RS               SYS_REFCURSOR;
    MI_CONSECUTIVO      PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_RTA              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_TIPONOVEDAD      PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASARECONINCAPAINI  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_VALOR            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_CUENTALOOP       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIASNOTRABAJADOS PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_TI_BASES         TI_BASES;
    MI_CUENTAINI        PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_CNTI             PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PORCFSP          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_BASESSUMA112     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PORCFSPADIC      PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PORCFSPSUBSIS    PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_INCAPACIDAD      VARCHAR2(100 CHAR);
    MI_LICENCIA         VARCHAR2(100 CHAR);
    MI_APORTESOLOPATRON BOOLEAN DEFAULT FALSE;
    MI_PERIODOBASENOV   PCK_SUBTIPOS.TI_PERIODO_NOMI;
    MI_NOCALCULANOVLIQUIFIN BOOLEAN DEFAULT FALSE;
    MI_CN212            NUMBER DEFAULT 0;
    MI_SALARIO_BASE     NUMBER DEFAULT 0;
    --(APINEDA:22/11/2019)-TAR 1000095489 Se crean variables para tomar valor de porcentaje aporte patronal pensiÃ³n cuando los empleados son de tipo 95
    MI_PORC_PATRON_AFP  PARAMETROS_DE_ENTRADA.PORC_PATRON_AFP%TYPE;
    MI_PORC_TOTAL_AFP   PARAMETROS_DE_ENTRADA.PORC_TOTAL_AFP%TYPE;
    --(APINEDA:26/12/2019)-TAR 1000096419
    MI_VALOR112OBASEPROPORCIONAL PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALAPORTEPENSION   NUMBER DEFAULT 0;
    --(APINEDA:15/05/2020)-TAR 1000098562 Funcionarios con retiro e ingreso en el mismo mes.
    MI_LICENCIA_RETIRADO            VARCHAR2(100 CHAR);
    MI_PORC_FSP_RETIRADO            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PORCFSPSUBSIS_RETIRADO       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_BASEPROPORCIONAL_RETIRADO    PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;  
    MI_FSP_RETIRADO                 PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;  
    MI_FSPSUBSISTENCIA_RETIRADO     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;  
    MI_CONDICION                    PCK_SUBTIPOS.TI_CONDICION;
    MI_VALPRUE                      NUMBER := 0;
    MI_PERIODOERETRO                PCK_SUBTIPOS.TI_PERIODO_NOMI;
    MI_IBCAUX                       NUMBER := 0;    
    MI_PARAMETRO1990APLICA          PARAMETRO.VALOR%TYPE;
    MI_VALAUX                       NUMBER;
    MI_BASE_INCAP                   NUMBER;
    MI_CN35                         NUMBER; --(CC:819_CFBARRERA)
    MI_DIAS_ENCAR_LICENCIA          NUMBER; --(CC:1031_NCARDENAS)
    MI_CONCEPTO_EVALUAR             NUMBER;
    MI_BASEPROPORCIONAL_ENCAR       NUMBER;
    MI_BASE_AUSENTISMO NUMBER DEFAULT 0; --(CC:2913_CFBARRERA Variable que asigna el valor de MI_BASE a la base de novedades cuando existe una novedad por ausentismo)
    MI_FEB_SITRABAJO                BOOLEAN DEFAULT FALSE; -- jm cc 3612
    MI_DIASNOTRABAJADOSFEB          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0; --JM CC 3787
BEGIN
    --MI_CNTI := MI_TI_BASES.COUNT;
    MI_PARAMETRO1990APLICA := UPPER(PCK_PARST.FC_PAR('APLICAR NUEVO REDONDEO DECRETO 1990 FC_ROUND_100', 'NO')) ;
    MI_TI_BASES.DELETE;
    MI_SALARIO_BASE := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SALARIO_BASE_IBC;    
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' THEN
        MI_SALARIO_BASE := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_SALARIO_BASE * PCK_NOMINA.CPARENTRADA(1).PORC_SALARIO_INTEGRAL / 100,
                                                   UN_PRECISION => 0);                
    END IF;
    
    -- TICKET 7736427 EFCM: SE ADICIONA PARAMETRO PARA DETERMINAR SI SE USA LA ASIGNACION BASICA O EL IBC MES ANTERIOR (348) COMO BASE
    --                      PARA CALCULO DE 1 A 2 DIAS INCAPACIDAD AMBULATORIA
    IF (PCK_PARST.FC_PAR('BASE CALCULO 1 A 2 DIAS INCAP. AMBULATORIA IBC ACTUAL', 'NO') = 'SI') THEN
        -- SUMAR GRP + PT + PA
        MI_IBCAUX := CASE WHEN PCK_NOMINA.FC_CN(10) = 0 
                                THEN PCK_NOMINA.FC_CN(1)
                                ELSE PCK_NOMINA.FC_CN(10)
                     END;
    ELSE
        MI_IBCAUX := 0;
    END IF;  
    -- TICKET 7736427 FIN --
        
    --(APINEDA:03/05/2019)-Se agrega validaciÃ³n para no calcular BASES NOVEDADES en periodo 4 para primas semestral y de navidad.
    --(APINEDA:02/01/2019)-Se agrega validaciÃ³n para no calcular BASES NOVEDADES en periodo 8 de cesantias.
    IF NOT (PCK_NOMINA.GL_ESBONIFICACION) AND PCK_NOMINA.GL_SPER <> 4 AND PCK_NOMINA.GL_SPER <> 8 THEN
        MI_PERIODOBASENOV := CASE WHEN UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'NO')) = 'SI' THEN 3 ELSE 2 END;   --(MZANGUNA:27/03/2019)-Con la unificaciÃ³n de la tabla el concepto 112 se debe restar para los demas periodos, para que no quede acumulado.
        --IF PCK_NOMINA.GL_SPER <> 7 AND PCK_NOMINA.FC_CN(404) = 0 THEN --(MZANGUNA:27/03/2019) Se quita dado que se debe calcular para el periodo 7.
        PCK_NOMINA_CALCULO.PR_CALCULAR_ARP_MES;

        IF PCK_NOMINA.GL_CN112NUEVO <> 0 AND PCK_NOMINA.GL_SPER <> 7 THEN   --(MZANGUNA:29/04/2019)-En periodo 7 debe entrar
            PCK_NOMINA.CN(112) := PCK_NOMINA.GL_CN112NUEVO;
            RETURN PCK_NOMINA.GL_CN112NUEVO;
        END IF;
        MI_VALOR := PCK_NOMINA.FC_CN(150);
        MI_F_INICIOPERACT := TO_DATE('01/' || PCK_NOMINA.GL_SMES || '/' || PCK_NOMINA.GL_SANO);
        MI_F_FINALPERACT := LAST_DAY(TO_DATE('01/' || PCK_NOMINA.GL_SMES || '/' || PCK_NOMINA.GL_SANO));

        MI_DIASARECONINCAPAINI :=  TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                    ,UN_NOMBRE    => 'DIAS A RECONOCER EN INCAPACIDAD INICIAL'
                                    ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                    ,UN_FECHA_PAR => SYSDATE),'2'));

        --Guardo el valor de las novedades manuales.
        BEGIN
            SELECT *
            BULK COLLECT INTO MI_TI_BASES
            FROM BASESNOVEDADES
            WHERE COMPANIA = UN_COMPANIA
              AND ID_DE_EMPLEADO = UN_EMPLEADO
              AND ANO = UN_ANO
              AND MES = UN_MES
              AND PERIODO = PCK_NOMINA.GL_SPER
              AND MANUAL <> 0;
        END;
        MI_CUENTAINI := MI_TI_BASES.COUNT + 1;
        MI_CNTI := MI_CUENTAINI;

        --Elimino las novedades que no son manuales.
        BEGIN
          IF NOT (UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'SI')) = 'NO' AND PCK_NOMINA_PROC01.FC_AUSENTISMO2DAQUINCENA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) AND PCK_NOMINA.GL_SPER = 2) THEN  -- JM CC 2203
            MI_TABLA := 'BASESNOVEDADES';
            MI_CONDICIONACME := 'COMPANIA = '''|| UN_COMPANIA ||'''
                            AND ID_DE_EMPLEADO = '|| UN_EMPLEADO ||'
                            AND ANO = '|| UN_ANO ||'
                            AND MES = '|| UN_MES ||'
                            AND PERIODO = '|| PCK_NOMINA.GL_SPER ||'
                            AND MANUAL = 0 ';
            BEGIN
                MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                UN_ACCION    => 'E',
                UN_CONDICION => MI_CONDICIONACME);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;
          END IF; -- JM CC 2203 no me borres nada del 2 porfa porque ya lo calcule en el uno pero el periodo siempre es 2 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_MSGERROR(1).CLAVE := 'EMPLEADO';
            MI_MSGERROR(1).VALOR := UN_EMPLEADO;

            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_ELIMINARBASENOVEDAD  ,
                UN_REEMPLAZOS => MI_MSGERROR );
        END;

        <<NOVEDADES>>
        FOR MI_RS IN
        (
            SELECT NOVEDAD AS NOVEDAD, MAX(LLAVE) AS LLAVE, MIN(F1) AS F1, MAX(F2) AS F2, MIN(INCAPACIDAD) AS INCAPACIDAD
            FROM (
            SELECT 'NVAC' NOVEDAD, 
                INICIO_DISFRUTE F1, FINAL_DISFRUTE F2,
                ' ' INCAPACIDAD,
                 COMPANIA || '--' || ID_DE_PROCESO|| '--' || ANO || '--' || MES || '--' || PERIODO || '--' || ID_DE_EMPLEADO || '--' || ACTO || '--' || TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY')  LLAVE 
             FROM VACACIONES
            WHERE COMPANIA = UN_COMPANIA
              AND ID_DE_EMPLEADO = UN_EMPLEADO
              AND PERIODO = PCK_NOMINA.GL_SPER --CC4238 MPEREZ
              AND ( TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT
                    OR (TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT AND TO_NUMBER(TO_CHAR(INICIO_DISFRUTE ,'DD')) <> 31  )
                    OR (TO_DATE(TO_CHAR(FECHAPAGO, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT AND TO_NUMBER(TO_CHAR(INICIO_DISFRUTE ,'DD')) <> 31  )
          --(APINEDA:02/04/2019)-Se modifica clausula where para incluir vacaciones cuando se pagan dos periodos seguidos.
          OR (TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') <= MI_F_INICIOPERACT AND TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') >= MI_F_FINALPERACT AND TO_NUMBER(TO_CHAR(INICIO_DISFRUTE ,'DD')) <> 31  )
                  )
              --(JORDUZ:08/10/2021)-Se annade validacion de las fechas ya que esta calculando dias en periodos a los que no corresponde TICKET 7701145
               AND TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') <= MI_F_FINALPERACT
              AND QUITARPILA = 0
              AND DIAS <> 0
              UNION 
              SELECT 'NVAC' NOVEDAD, 
                INICIO_DISFRUTE F1, FINAL_DISFRUTE F2,
                ' ' INCAPACIDAD,
                ' '  LLAVE 
              FROM VACACIONES
            WHERE COMPANIA = UN_COMPANIA
              AND ID_DE_EMPLEADO = UN_EMPLEADO
              AND PERIODO = PCK_NOMINA.GL_SPER --CC4238 MPEREZ
              AND ( TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT
                    OR (TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT AND TO_NUMBER(TO_CHAR(INICIO_DISFRUTE ,'DD')) <> 31  )
                    OR (TO_DATE(TO_CHAR(FECHAPAGO, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT AND TO_NUMBER(TO_CHAR(INICIO_DISFRUTE ,'DD')) <> 31  )
          --(APINEDA:02/04/2019)-Se modifica clausula where para incluir vacaciones cuando se pagan dos periodos seguidos.
          OR (TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') <= MI_F_INICIOPERACT AND TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') >= MI_F_FINALPERACT AND TO_NUMBER(TO_CHAR(INICIO_DISFRUTE ,'DD')) <> 31  )
                  )
              --(JORDUZ:08/10/2021)-Se annade validacion de las fechas ya que esta calculando dias en periodos a los que no corresponde TICKET 7701145
               AND TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') <= MI_F_FINALPERACT
              AND QUITARPILA = 0
              AND ENDINERO = -1
               ) GROUP BY NOVEDAD

            UNION

            SELECT 'NINCAP' NOVEDAD,
                COMPANIA || '--' || ID_DE_EMPLEADO || '--' || ID_DE_PROCESO || '--' || INCAPACIDAD || '--' || ANO || '--' || MES || '--' || PERIODO || '--' || AUTO LLAVE,
                FECHA_INICIO  F1, FECHA_FIN     F2,INCAPACIDAD
            FROM INCAPACIDADES
            WHERE COMPANIA       = UN_COMPANIA
              AND ID_DE_EMPLEADO = UN_EMPLEADO
              AND PERIODO = PCK_NOMINA.GL_SPER --CC4238 MPEREZ
              AND FECHA_INICIO  <= MI_F_FINALPERACT
              AND FECHA_FIN     >= MI_F_INICIOPERACT

            UNION

            SELECT  'NLIC' NOVEDAD,
                COMPANIA || '--' || ID_DE_PROCESO || '--' || ID_DE_EMPLEADO || '--' || NUMERO_ACTO || '--' || LICENCIA LLAVE,
                FECHA_INICIO F1, FECHA_FINAL F2, ' ' INCAPACIDAD
            FROM LICENCIAS
            WHERE COMPANIA       = UN_COMPANIA
              AND ID_DE_EMPLEADO = UN_EMPLEADO
              AND PERIODO = PCK_NOMINA.GL_SPER --CC4238 MPEREZ
            --AND LICENCIA NOT IN('01','02','03')
              AND FECHA_INICIO  <= MI_F_FINALPERACT
              AND FECHA_FINAL   >= MI_F_INICIOPERACT
        )
        LOOP
            MI_CUENTALOOP := MI_CUENTALOOP + 1;
            --(APINEDA:12/08/2019)-Se agrega condiciÃ³n para personal con salario integral.
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' THEN
                MI_BASE := MI_SALARIO_BASE;
            ELSE
                MI_BASE := PCK_NOMINA.FC_CN(348);
                IF MI_BASE = 0 THEN
                    MI_BASE := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_SALARIO_BASE ,UN_PRECISION => 0);
                END IF;            
            END IF;


            --LICENCIAS O COMISIONES
            IF (PCK_NOMINA.FC_CN(355) + PCK_NOMINA.FC_CN(339)) >= 30 THEN
                MI_BASE := PCK_NOMINA.FC_CN(112);
            END IF;

            --(MZANGUNA:11/12/2018)-Licencias remuneradas
            IF MI_RS.NOVEDAD = 'NLIC' THEN
                MI_LICENCIA := SUBSTR(MI_RS.LLAVE, INSTR(MI_RS.LLAVE,'--',1,4) + 2, LENGTH(MI_RS.LLAVE));
                MI_LICENCIA := CASE WHEN MI_LICENCIA IN('01','02','03')
                                  THEN 'NSLN'
                                  ELSE CASE WHEN MI_LICENCIA IN('04','05','07','10','12')--(CC:3148_CFBARRERA_Se agrega la licencia por luto)
                                       THEN 'LREM'
                                       ELSE 'COMIS'
                                       END
                                  END;
                IF MI_LICENCIA = 'LREM' THEN
                    --(MZANGUNA:18/03/2019)-Tome como base el valor de sueldo y encargo de presentarse.
                    MI_BASE := PCK_SYSMAN_UTL.FC_ROUND(MI_SALARIO_BASE + (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) -PCK_NOMINA.FC_CN(1) ELSE 0 END), 0);
                    --MI_BASE := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_SALARIO_BASE,UN_PRECISION => 0);
                END IF;
            END IF;

            MI_FINICIO := MI_F_INICIOPERACT;
            MI_FFINAL := MI_F_FINALPERACT;

            IF MI_RS.F1 >= MI_F_INICIOPERACT AND MI_RS.F1 <= MI_F_FINALPERACT THEN
                MI_FINICIO := MI_RS.F1;
            ELSIF MI_RS.F1 <= MI_FINICIO THEN
                MI_FINICIO := MI_FINICIO;
            ELSE
                MI_FINICIO := MI_RS.F1;
            END IF;

            IF MI_RS.F2 >= MI_F_INICIOPERACT AND MI_RS.F2 <= MI_F_FINALPERACT THEN
                MI_FFINAL := MI_RS.F2;
            ELSIF MI_RS.F2 > MI_F_FINALPERACT THEN
                MI_FFINAL := MI_F_FINALPERACT;
            ELSE
                MI_FFINAL := MI_RS.F2;
            END IF;
            
            -- TICKET 7715571 ECABRERA: SE CALCULA LOS DIAS DE LA NOVEDAD (INCLUYENDO EN EL MES DE FEBRERO), CON LA FUNCION CREADA PARA TAL FIN
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN   => MI_FINICIO ,UN_FECHAFIN  => MI_FFINAL);
            -- FIN 7715571
            IF MI_FINICIO <> MI_F_INICIOPERACT AND PCK_NOMINA.GL_SMES = 2  AND MI_FFINAL = MI_F_FINALPERACT AND MI_DIAS = 28 THEN 
                MI_FEB_SITRABAJO := TRUE;
            END IF;

            IF (MI_RS.NOVEDAD = 'NVAC' OR MI_LICENCIA = 'LREM') AND (NVL(PCK_SYSMAN_UTL.FC_PAR(PCK_NOMINA.GL_COMPANIA, 'LIQUIDAR VACACIONES Y LR EN BASE A DIAS HABILES',PCK_DATOS.FC_MODULONOMINA,SYSDATE),'NO') = 'SI' ) THEN --JM MOD CC 3444
                MI_DIAS := PCK_SYSMAN_UTL.FC_DIASHABIL(UN_COMPANIA => PCK_NOMINA.GL_COMPANIA,UN_FECHAINI   => MI_FINICIO ,UN_FECHAFIN  => MI_FFINAL);
            END IF;
           
            
            MI_TIPONOVEDAD := CASE MI_RS.NOVEDAD WHEN 'NVAC' THEN 1 WHEN 'NINCAP' THEN 2 ELSE 3 END;
            --Licencias e incapacidades
            IF MI_RS.INCAPACIDAD = '01' THEN
                MI_DD := CASE WHEN PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN   => MI_FINICIO ,UN_FECHAFIN  => MI_FFINAL) >= MI_DIASARECONINCAPAINI
                         THEN MI_DIASARECONINCAPAINI ELSE 1 END;
                MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN   => MI_FINICIO ,UN_FECHAFIN  => MI_FFINAL) - MI_DD;

                IF MI_RS.F1 <= MI_FINICIO - 2 THEN
                    MI_DD := 0 ;
                    MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN   => MI_FINICIO ,UN_FECHAFIN  => MI_FFINAL) + MI_DD;
                END IF;
                IF NOT(PCK_PARENTR.PARAMETRO31 = '800.231.969-4') THEN
                    MI_BASEPROPIIF := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                        (UN_IBC       => MI_BASE
                        ,UN_DIAS      => MI_DIAS
                        ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                        ,UN_ROUND     => 0
                        ,UN_FACTOR    => 'PARINC');
                    MI_BASEPROPIIF := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASEPROPIIF
                        ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
                        
                    -- TICKET 7736427 EFCM: SE UTILIZA PARA CALCULO DE 1 A 2 DIAS INICIALES INCAPACIDAD AMBULATORIA
                    --                      EL IBS DEL MES ACTUAL SI EL PARAMETRO ESTA ACTIVO
                    MI_IBCAUX := CASE WHEN MI_IBCAUX != 0 THEN MI_IBCAUX ELSE MI_BASE END;
                    -- TICKET 7736427 FIN --     

                    MI_BASEPROPIIF := MI_BASEPROPIIF
                        + PCK_NOMINA_COM5.FC_BASEPROPORCIONAL(
                            UN_IBC        => MI_IBCAUX --MI_BASE
                            ,UN_DIAS      => MI_DD
                            ,UN_DECIMALES => 0
                            ,UN_ROUND     => PCK_NOMINA.GL_RBASE1990);

                    IF MI_BASEPROPIIF < PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => PCK_PARENTR.PARAMETRO20 / 30 * (MI_DIAS + MI_DD) ,UN_PRECISION => 0) THEN
                        MI_BASE := PCK_PARENTR.PARAMETRO20;
                    ELSE
                        MI_BASEPROPIIF := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                            (UN_IBC       => MI_BASE
                            ,UN_DIAS      => MI_DD
                            ,UN_DECIMALES => 0
                            ,UN_ROUND     => PCK_NOMINA.GL_RBASE1990);

                        IF PCK_NOMINA.FC_CN(370) < PCK_NOMINA_COM5.FC_BASEPROPORCIONAL(UN_IBC => MI_BASE, UN_DIAS => MI_DD, UN_DECIMALES => PCK_NOMINA.GL_RBASE1990, UN_ROUND => 0, UN_FACTOR => 'PARINC') AND PCK_NOMINA.FC_CN(358) = 0 AND PCK_NOMINA.FC_CN(378) = 0 THEN
                            IF PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => PCK_NOMINA.FC_CN(370) ,UN_PRECISION => 2) = PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => PCK_PARENTR.PARAMETRO20 / 30*(MI_DIAS - MI_DD) ,UN_PRECISION => 0) THEN
                                MI_BASE := PCK_PARENTR.PARAMETRO20;
                            END IF;
                        END IF;
                    END IF;
                END IF;
                MI_DIAS := MI_DIAS + MI_DD;
            END IF;

            IF MI_RS.NOVEDAD = 'NVAC' AND PCK_NOMINA.FC_CN(404) = 0 THEN    --
                IF PCK_PARST.FC_PAR('SUMAR VALOR TOTAL DE VACACIONES PAGADAS A SS', ' ') = 'SI' THEN

                    IF PCK_NOMINA.FC_CN(174) <> 0 THEN  --Cuando se estan pagando las vacaciones y se disfrutan en el sig periodo
                        --(APINEDA:12/08/2019)-Se agrega condiciÃ³n para personal con salario integral.
                        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' THEN
                            MI_BASE := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(174) * PCK_NOMINA.CPARENTRADA(1).PORC_SALARIO_INTEGRAL / 100) / PCK_NOMINA.FC_CN(94) * 30 , 0);                                 
                        ELSE                    
                            MI_BASE := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(174) / PCK_NOMINA.FC_CN(94) * 30 , 0);
                        END IF;
                    ELSE    --Cuando se pagan en el periodo anterior pero se disfrutan en este periodo
                        --Busca la base calculada anteriormente.
                        BEGIN
                            --<TAR:7712603 FECHA:20/03/2022 AUTOR:CP> --Se agrega validación para que   cuando el tipo novedades sea NVAC (vacaciones) en el periodo de retroactivo este tome el cálculo del mes anterior si el parámetro SUMAR VALOR TOTAL DE VACACIONES PAGADAS A SS está activo
                            IF PCK_NOMINA.GL_NOMINARETROACTIVO THEN 
                               MI_PERIODOERETRO :=  PCK_NOMINA.GL_PERIODORETROACTIVO;
                            ELSE
                               MI_PERIODOERETRO :=  PCK_NOMINA.GL_SPER;
                            END IF ;
                            SELECT NVL(SUM(BASE),0)
                            INTO   MI_BASE
                            FROM   BASESNOVEDADES
                            WHERE  COMPANIA = UN_COMPANIA
                              AND  LLAVENOVEDAD = MI_RS.LLAVE
                              AND  TIPONOVEDAD = 1
                              AND  ID_DE_EMPLEADO = UN_EMPLEADO
                              AND  ANO = CASE WHEN UN_MES = 1 THEN UN_ANO -1 ELSE UN_ANO END
                              AND  MES = CASE WHEN UN_MES = 1 THEN 12 ELSE UN_MES - 1 END
                              AND  PERIODO = MI_PERIODOERETRO;    
                        EXCEPTION WHEN NO_DATA_FOUND THEN
                            MI_BASE := 0;
                        END;
                    END IF;
                ELSE
                    MI_BASE := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE MI_SALARIO_BASE END) + MI_VALOR;
                    --Entidades normales
                    
                    -- TICKET 7708886 ECABRERA : LA BASE INCLUYE EL CONCEPTO 170 PARA IDIPRON EN PERIODO DE VACACIONES Y CON ENCARGO
                    IF PCK_PARST.FC_PAR('LIQUIDACION PARA IDIPRON', 'NO') = 'SI' AND PCK_NOMINA.FC_CN(35) > 0 AND PCK_NOMINA.FC_CN(10) > 0 THEN 
                        MI_BASE := PCK_NOMINA.FC_CN(10) + PCK_NOMINA.FC_CN(872); 
                    END IF;
                    -- TICKET 7708886 FIN
                END IF;

                --Interrupcion de vacaciones
                FOR MI_RSINTER IN
                (
                    SELECT FECHAINTERRUPCION
                    FROM  INTERRUPCION_VACACIONES
                    WHERE COMPANIA =  UN_COMPANIA
                      AND ID_DE_EMPLEADO = UN_EMPLEADO
                      AND ENDINERO = 0
                      AND QUITARPILA = 0
                      AND (( TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT -- MOD JM 26/05/2025 se agregan parentesis para que tome la informacion correcta
                          OR TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT
                          )
                      AND ( TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_RS.F1 AND MI_RS.F2
                         OR TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_RS.F1 AND MI_RS.F2
                          )
                      OR ((EXTRACT(MONTH FROM MI_F_INICIOPERACT) BETWEEN EXTRACT(MONTH FROM FECHAINTERRUPCION) AND EXTRACT(MONTH FROM FINAL_DISFRUTE))  --JM 12/05/2025 CC 1513 si es mas de un periodo no va a encontrar el mes intermedio
                       AND (EXTRACT(YEAR FROM MI_F_INICIOPERACT) BETWEEN EXTRACT(YEAR FROM FECHAINTERRUPCION) AND EXTRACT(YEAR FROM FINAL_DISFRUTE)) ))  --JM 12/05/2025 CC 1513 si es mas de un periodo no va a encontrar el mes intermedio
                
                )
                LOOP
                    --(APINEDA:14/11/2018)-Se resta un dÃ­a a la fecha de inicio de interrupciÃ³n de vacaciones TAR1000088147 ANE
                    IF MI_RSINTER.FECHAINTERRUPCION >= MI_F_INICIOPERACT AND MI_RSINTER.FECHAINTERRUPCION <= MI_F_FINALPERACT THEN
                        MI_FFINAL := MI_RSINTER.FECHAINTERRUPCION - 1;
                    ELSIF MI_RSINTER.FECHAINTERRUPCION > MI_F_FINALPERACT THEN
                        MI_FFINAL := MI_F_FINALPERACT;
                    ELSIF MI_RSINTER.FECHAINTERRUPCION < MI_F_INICIOPERACT THEN
                        MI_FFINAL := MI_F_INICIOPERACT;
                    ELSE
                        MI_FFINAL := MI_RSINTER.FECHAINTERRUPCION - 1;
                    END IF;
                END LOOP;

                IF (PCK_NOMINA.FC_CN(35) = 0 AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'NOMINA MENSUAL',6,SYSDATE),'NO') = 'SI' ) OR ((PCK_NOMINA.FC_CN(35) + PCK_NOMINA.FC_CNP(35)) = 0 AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'NOMINA MENSUAL',6,SYSDATE),'SI') = 'NO') THEN --Si no existen vacaciones para disfrutar en el mes. --JM CC 3085
                    MI_DIAS := 0;
                    MI_FINICIO := NULL;
                    MI_FFINAL := NULL;
                ELSE
                    --(MZANGUNA:01/03/2019)-Se ajusta para Febrero dado que genera errores de fechas con la funciÃ³n FC_DIASMESCOMERCIAL
                    IF TO_CHAR(MI_FINICIO, 'MM') = '02' AND TO_CHAR(MI_FFINAL, 'MM') = '02' AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'NOMINA MENSUAL',6,SYSDATE),'NO') = 'SI' THEN -- MP CC3843
                    BEGIN --CFBARRERA(CC:819_verifica si la novedad continua en el mes siguiente para completar los 30 dias)
                        SELECT NVL(VALOR,0) INTO MI_CN35  FROM NOVEDADES WHERE ID_DE_CONCEPTO = 35
                        AND ID_DE_EMPLEADO  = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                        AND MES = (PCK_NOMINA.GL_SMES + 1)
                        AND ANO = PCK_NOMINA.GL_SANO
                        AND PERIODO = PCK_NOMINA.GL_SPER;
                        
                        EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_CN35 := 0;
                    END;

                        MI_DIAS := TO_NUMBER(MI_FFINAL - MI_FINICIO) + CASE WHEN PCK_SYSMAN_UTL.FC_DIA(MI_FFINAL) = 28 AND MI_CN35 > 0 THEN 2 ELSE 1 END ; --JM 23/07/2024 para completar los 30 dias,se modifico,CFBARRERA(CC:819_FIN) 
                        
                    ELSE
                        -- JM 12/05/2025 CC 1513 se agrega condicion (MI_FINICIO >= MI_FFINAL)   MI_DIAS := 0;
                        -- ya que FC_DIASMESCOMERCIAL trae la diferencia entre dias 
                        -- y en caso de interrupcion los dias dan negativos (0) y de igual manera los estaba sumando 
                        -- MOD JM 15/07/2025 CC2048, la verdad no se que dice el genio de arriba, pero no es >= es solo > 
                        IF MI_FINICIO > MI_FFINAL THEN  
                            MI_DIAS := 0;
                        ELSE 
                            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN   => MI_FINICIO
                            ,UN_FECHAFIN  => MI_FFINAL);

                            IF (NVL(PCK_SYSMAN_UTL.FC_PAR(PCK_NOMINA.GL_COMPANIA, 'LIQUIDAR VACACIONES Y LR EN BASE A DIAS HABILES',PCK_DATOS.FC_MODULONOMINA,SYSDATE),'NO') = 'SI' ) THEN --JM MOD CC 3444
                                MI_DIAS := PCK_SYSMAN_UTL.FC_DIASHABIL(UN_COMPANIA => PCK_NOMINA.GL_COMPANIA,UN_FECHAINI   => MI_FINICIO ,UN_FECHAFIN  => MI_FFINAL);
                            END IF;

                        END IF;
                        IF PCK_NOMINA.FC_CN(96) > 0 THEN
                            MI_DIAS := MI_DIAS - PCK_NOMINA.FC_CN(96);
                        END IF; --JM 7746481 07/10/2024
                        IF (((PCK_NOMINA.FC_CNAN(35) + PCK_NOMINA.FC_CN(35)) > 0) AND (PCK_NOMINA.FC_CNAN(98) + PCK_NOMINA.FC_CN(98)) > 0) AND (PCK_NOMINA.FC_CNAN(35)+ PCK_NOMINA.FC_CN(35) = PCK_NOMINA.FC_CNAN(98)+ PCK_NOMINA.FC_CN(98)) THEN
                            MI_DIAS :=0;
                        END IF; --LVEGA con apoyo de JM 22/12/2025 CC 3044
                    END IF;
                END IF;

                --MI_DIAS := PCK_NOMINA.FC_CN(35);    --DIF DE DIAS, TOMAR COMO BASE FC JOSE
                --No se deja concepto 35 dado que si termina vacaciones en un mes y comienza otro queda errado.
            END IF;

            BEGIN
                SELECT COUNT(0)
                INTO   MI_CUENTA
                FROM   BASESNOVEDADES
                WHERE  COMPANIA = UN_COMPANIA
                  AND  LLAVENOVEDAD = MI_RS.LLAVE
                  AND  TIPONOVEDAD = MI_TIPONOVEDAD
                  AND  ID_DE_EMPLEADO = UN_EMPLEADO
                  AND  ANO = UN_ANO
                  AND  MES = UN_MES
                  AND  PERIODO = PCK_NOMINA.GL_SPER
                  AND  MANUAL <> 0;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_CUENTA := 0;
            END;

            IF MI_CUENTA = 0 THEN --Si no existe manual
                MI_TABLA := 'BASESNOVEDADES';
                IF MI_CNTI = 0 OR MI_CNTI = MI_CUENTAINI THEN
                    MI_CONSECUTIVO :=  PCK_SYSMAN_UTL.FC_GENCONSECUTIVO
                        (UN_TABLA    => MI_TABLA,
                         UN_CRITERIO => 'COMPANIA = '''|| UN_COMPANIA ||'''
                                    AND  ID_DE_EMPLEADO = '|| UN_EMPLEADO ||'
                                    AND  ANO = '|| UN_ANO ||'
                                    AND  MES = '|| UN_MES ||'
                                    AND  PERIODO = '|| PCK_NOMINA.GL_SPER  ||' ',
                         UN_CAMPO    => 'CONSECUTIVO');
                ELSE
                    MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
                END IF;

                --(MZANGUNA:26/04/2019)-Inicio: Se deja validaciÃ³n para periodo 7 liquidaciones finales, dado que no debe calcular las novedades que ya fueron cÃ¡lculadas en periodo mensual o quincenal.
                MI_NOCALCULANOVLIQUIFIN := CASE WHEN PCK_NOMINA.GL_SPER = 7 AND PCK_NOMINA.FC_CN(404) <> 0 THEN TRUE ELSE FALSE END;
                IF MI_NOCALCULANOVLIQUIFIN THEN
                    BEGIN
                        SELECT BASE, DIAS, BASEPROPORCIONAL, FECHAINICIAL, FECHAFINAL
                        INTO MI_TI_BASES(MI_CNTI).BASE
                            ,MI_TI_BASES(MI_CNTI).DIAS
                            ,MI_TI_BASES(MI_CNTI).BASEPROPORCIONAL
                            ,MI_TI_BASES(MI_CNTI).FECHAINICIAL
                            ,MI_TI_BASES(MI_CNTI).FECHAFINAL
                        FROM BASESNOVEDADES
                        WHERE COMPANIA = UN_COMPANIA
                          AND LLAVENOVEDAD = MI_RS.LLAVE
                          AND TIPONOVEDAD = MI_TIPONOVEDAD
                          AND ID_DE_EMPLEADO = UN_EMPLEADO
                          AND ANO = UN_ANO
                          AND MES = UN_MES
                          AND PERIODO = MI_PERIODOBASENOV;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_TI_BASES(MI_CNTI).BASE := MI_BASE;
                        MI_TI_BASES(MI_CNTI).DIAS := MI_DIAS;
                        MI_TI_BASES(MI_CNTI).BASEPROPORCIONAL := 0;
                        MI_TI_BASES(MI_CNTI).FECHAINICIAL := MI_FINICIO;
                        MI_TI_BASES(MI_CNTI).FECHAFINAL := MI_FFINAL;
                    END;
                    MI_BASE := MI_TI_BASES(MI_CNTI).BASE;
                    MI_DIAS := MI_TI_BASES(MI_CNTI).DIAS;
                    MI_FINICIO := MI_TI_BASES(MI_CNTI).FECHAINICIAL;
                    MI_FFINAL := MI_TI_BASES(MI_CNTI).FECHAFINAL;
                ELSE --(MZANGUNA:26/04/2019)-Fin: Se deja validaciÃ³n para periodo 7 liquidaciones finales, dado que no debe calcular las novedades que ya fueron cÃ¡lculadas en periodo mensual o quincenal.
                    MI_TI_BASES(MI_CNTI).BASE := MI_BASE;
                    MI_TI_BASES(MI_CNTI).DIAS := MI_DIAS;
                    MI_TI_BASES(MI_CNTI).BASEPROPORCIONAL := 0;
                    MI_TI_BASES(MI_CNTI).FECHAINICIAL := MI_FINICIO;
                    MI_TI_BASES(MI_CNTI).FECHAFINAL := MI_FFINAL;
                END IF;

                MI_TI_BASES(MI_CNTI).COMPANIA :=  UN_COMPANIA ;
                MI_TI_BASES(MI_CNTI).LLAVENOVEDAD := MI_RS.LLAVE;
                MI_TI_BASES(MI_CNTI).TIPONOVEDAD := MI_TIPONOVEDAD;
                MI_TI_BASES(MI_CNTI).CONSECUTIVO := MI_CONSECUTIVO;
                MI_TI_BASES(MI_CNTI).ID_DE_EMPLEADO := UN_EMPLEADO;
                MI_TI_BASES(MI_CNTI).ANO := UN_ANO;
                MI_TI_BASES(MI_CNTI).MES := UN_MES;
                MI_TI_BASES(MI_CNTI).PERIODO := PCK_NOMINA.GL_SPER;
                MI_TI_BASES(MI_CNTI).CREATED_BY := UN_USUARIO;
                MI_TI_BASES(MI_CNTI).DATE_CREATED := SYSDATE;
                MI_TI_BASES(MI_CNTI).MANUAL := 0;
                --(APINEDA:18/10/2019)-Se agrega campo para almacenar el nÃºmero de dÃ­as reconocidos al 100% en incapacidades.
                MI_TI_BASES(MI_CNTI).DIASCOMPLETOSRECONOCIDOS := MI_DD;            

                MI_CNTI := MI_CNTI + 1;
            END IF;
            MI_DIASNOTRABAJADOS := MI_DIASNOTRABAJADOS + MI_DIAS;
        END LOOP NOVEDADES;

        BEGIN
            SELECT DIAS
            INTO   MI_DIAS
            FROM   BASESNOVEDADES
            WHERE  COMPANIA = UN_COMPANIA
              AND  ID_DE_EMPLEADO = UN_EMPLEADO
              AND  ANO = UN_ANO
              AND  MES = UN_MES
              AND  PERIODO = PCK_NOMINA.GL_SPER
              AND  TIPONOVEDAD = 4
              AND  MANUAL <> 0;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_DIAS := 0;
        END;

        IF MI_DIAS = 0 THEN   --Ingresa el sueldo
            MI_TIPONOVEDAD := 4;
            --(APINEDA:01/10/2018)-No se esta teniendo en cuenta el valor de la prima tÃ©cnica se suma SALARIO_BASE_IBC
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' THEN
                MI_BASE := MI_SALARIO_BASE + (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) -PCK_NOMINA.FC_CN(1) ELSE 0 END);--MI_SALARIO_BASE;
            ELSE
                MI_BASE := PCK_NOMINA.FC_CN(348);
                IF MI_BASE = 0 THEN
                    MI_BASE := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_SALARIO_BASE ,UN_PRECISION => 0);
                END IF;            
            END IF;
            
            -- ECABRERA T:7708888 LIQUIDACION BASES IDIPRON
            --MI_BASE := MI_SALARIO_BASE + (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) -PCK_NOMINA.FC_CN(1) ELSE 0 END);
            IF PCK_PARST.FC_PAR('LIQUIDACION PARA IDIPRON', 'NO') = 'SI' AND PCK_NOMINA.FC_CN(35) > 0 THEN
              -- TICKET 7708882: ECABRERA LIQUIDACION BASES IDIPRON EN PERIODO DISFRUTE VACACIONE
               -- IF ( PCK_NOMINA.FC_CN(35) > 0 ) THEN
                    -- TICKET 7708882: ECABRERA LIQUIDACION BASES IDIPRON EN PERIODO DISFRUTE VACACIONE
                    IF ( PCK_NOMINA.FC_CN(10) > 0 ) THEN
                        MI_BASE := PCK_NOMINA.FC_CN(10) + PCK_NOMINA.GL_VPT_C + PCK_NOMINA.FC_CN(872);
                    ELSE
                        MI_BASE := PCK_NOMINA.GL_IBC_IDIPRON;
                    END IF;
              /*  ELSE
                    IF ( PCK_NOMINA.FC_CN(10) <> 0 ) THEN
                        MI_BASE := MI_SALARIO_BASE + (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) - PCK_NOMINA.FC_CN(1) ELSE 0 END); 
                    ELSE
                        MI_BASE := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPT_C + PCK_NOMINA.FC_CN(872);
                    END IF;
                END IF;*/ -- comentado por JMillan el 20/11/2023
                -- TICKET 7708882 FIN
                 ELSE 
              --jm 7737788 caso idprom 
                IF PCK_PARST.FC_PAR('LIQUIDACION PARA IDIPRON', 'NO') = 'SI' THEN 
                
                    IF ( PCK_NOMINA.FC_CN(10) > 0 ) THEN 
                    --jm 7737788 mes completo de encanrgo 
                        IF PCK_NOMINA.CN(11) >= 30  THEN
                            MI_BASE := PCK_NOMINA.FC_CN(10)  +PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA, 170) <> 0, PCK_NOMINA.FC_CN(170), 0);     

                        ELSE
                        --jm 7737788 parte encargo// parte sueldo  
                            MI_BASE := ((MI_SALARIO_BASE /30)*PCK_NOMINA.CN(9))+((PCK_NOMINA.FC_CN(10)/30)*PCK_NOMINA.CN(11))+PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA, 170) <> 0, PCK_NOMINA.FC_CN(170), 0);                       
                            MI_BASE := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASE, UN_PRECISION => 2);
                        END IF;
                    ELSE 
                    --jm 7737788 salario mas prima  (sin ecargo)
                    IF PCK_NOMINA.FC_CN(9) > 0 and  PCK_NOMINA.FC_CN(9) <= 30 and PCK_NOMINA.FC_CN(35) = 0 and PCK_PARST.FC_PAR('SUMA CONCEPTOS AUTOLIQUIDACION POR PARAMETRO','') = 'SI'  THEN 
                        
                         MI_BASE := 0; 
                         <<CALCULARBASE>>
                         FOR MI_RSINTER IN (SELECT ID_DE_CONCEPTO, NOMBRE_CONCEPTO, REPORTEEPS
                                     FROM   CONCEPTOS
                                     WHERE  COMPANIA    = PCK_NOMINA.GL_COMPANIA 
                                       AND  REPORTEEPS  <> 0
                                    ORDER BY ID_DE_CONCEPTO
                                    )
                          LOOP
                            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' AND (MI_RSINTER.ID_DE_CONCEPTO = 2 OR MI_RSINTER.ID_DE_CONCEPTO = 7)  THEN
                              MI_BASE := MI_BASE + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(MI_RSINTER.ID_DE_CONCEPTO), 0) * PCK_NOMINA.CPARENTRADA(1).PORC_SALARIO_INTEGRAL / 100, 0);
                            ELSE
                              MI_BASE := MI_BASE + PCK_NOMINA.FC_CN(MI_RSINTER.ID_DE_CONCEPTO);
                            END IF;    
                          END LOOP CALCULARBASE; 
                        
                    ELSE 
                        MI_BASE := MI_SALARIO_BASE + PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA, 170) <> 0, PCK_NOMINA.FC_CN(872), 0)+PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA, 178) <> 0, PCK_NOMINA.FC_CN(178), 0);
                    END IF;
                    
                    
                    MI_BASE := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASE, UN_PRECISION => 2);
                    
               
                    END IF;
                
                   
                ELSE 
                    --MI_BASE := MI_SALARIO_BASE + (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) - PCK_NOMINA.FC_CN(1) ELSE 0 END);
                    
                    --JM 7722164 INI
                IF PCK_NOMINA.FC_CN(11) > 0 AND  PCK_NOMINA.FC_CN(11) < 30 AND PCK_NOMINA.CN(9) > 0 AND PCK_NOMINA.CN(9) < 30 AND  PCK_NOMINA.GL_SMES <> 2 THEN
                    MI_BASE := ((PCK_NOMINA.CN(1) /30)*PCK_NOMINA.CN(9))+((PCK_NOMINA.FC_CN(10)/30)*PCK_NOMINA.CN(11)); --MOD JM CC 3033
                    
                    -- TICKET 7740193: LICENCIAS REMUNERADAS CON ENCARGO SE RESPETA EL CONCEPTO 2 COMO BASE
                    IF ( PCK_NOMINA.FC_CN(10) > 10 AND PCK_NOMINA.FC_CN(365) > 0 ) THEN
                        MI_BASE := PCK_NOMINA.CN(2);
                    END IF;
                    -- TICKET 7740193 FIN --                    
                    MI_BASE := MI_BASE + PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA, 170) <> 0, PCK_NOMINA.FC_CN(170), 0) +PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA, 186) <> 0, PCK_NOMINA.FC_CN(186), 0); --JM CC 3033   
                    MI_BASE := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR => MI_BASE,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
                ELSE
                    MI_BASE := MI_SALARIO_BASE + (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) - PCK_NOMINA.FC_CN(1) ELSE 0 END)+PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA, 150) <> 0, PCK_NOMINA.FC_CN(150), 0); --JM 7722162_NOMINA 13/02/2024
                    --TICKET 7737218 EFCM: SE ADICIONA PRIMA DE ANTIGUEDAD A LA BASE
                    MI_BASE := MI_BASE + PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA, 170) <> 0, PCK_NOMINA.FC_CN(170), 0);
                    --TICKET 7737218 EFCM FIN -- 
                    --JM CC1086 27/03/2025 nos copiamos la validacion de arriba para asegurar que todos los conceptos que son factor se sumen a la base 
                    --MOD JM CC1704 04/06/2025
                    --MOD JM CC2311 20/08/2025 se aañade 339 ya que no esta incluido en el 347 
                    IF (PCK_NOMINA.FC_CN(9) > 0 and  PCK_NOMINA.FC_CN(9) <= 30 and PCK_NOMINA.FC_CN(35) = 0 and PCK_PARST.FC_PAR('SUMA CONCEPTOS AUTOLIQUIDACION POR PARAMETRO','') = 'SI' AND  (PCK_NOMINA.FC_CN(347) + PCK_NOMINA.FC_CN(339)) = 0)  OR (PCK_NOMINA.GL_SPER = 7 AND PCK_NOMINA.FC_CN(404) <> 0 ) OR --MOD JM CC 2089 
                      (NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PAGAR MESES DE 31 EN NOMINA QUINCENAL',6,SYSDATE),'NO') = 'NO' AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'NOMINA MENSUAL',6,SYSDATE),'SI') = 'NO' AND PCK_PARST.FC_PAR('SUMA CONCEPTOS AUTOLIQUIDACION POR PARAMETRO','') = 'SI' AND PCK_NOMINA.GL_SPER = 1 AND PCK_PARST.FC_PAR('MOSTRAR SEGURIDAD SOCIAL EN PRIMERA QUINCENA','SI') = 'SI' ) THEN -- MPEREZ 3843  
                        MI_BASE := 0;
                         <<CALCULARBASE>>
                         FOR MI_RSINTER IN (SELECT ID_DE_CONCEPTO, NOMBRE_CONCEPTO, REPORTEEPS
                                     FROM   CONCEPTOS
                                     WHERE  COMPANIA    = PCK_NOMINA.GL_COMPANIA
                                       AND  REPORTEEPS  <> 0
                                    ORDER BY ID_DE_CONCEPTO
                                    )
                          LOOP
                            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' AND (MI_RSINTER.ID_DE_CONCEPTO = 2 OR MI_RSINTER.ID_DE_CONCEPTO = 7)  THEN
                              MI_BASE := MI_BASE + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(MI_RSINTER.ID_DE_CONCEPTO), 0) * PCK_NOMINA.CPARENTRADA(1).PORC_SALARIO_INTEGRAL / 100, 0);
                            ELSE
                              MI_BASE := MI_BASE + PCK_NOMINA.FC_CN(MI_RSINTER.ID_DE_CONCEPTO) + CASE WHEN (PCK_NOMINA.GL_SPER = 7 AND PCK_NOMINA.FC_CN(404) <> 0) THEN PCK_NOMINA.FC_CNP(MI_RSINTER.ID_DE_CONCEPTO) ELSE 0 END; --MOD JM CC 1314
                            END IF;
                          END LOOP CALCULARBASE;
                      END IF;    
                      --JM CC1086 FIN 27/03/2025
                END IF;

                END IF;
                  --JM 7722164 FIN
                    
            END IF;
            -- 7708888

            --JM INI CC1404 PARA NOMINAS QUINCENALES COMO BUCARAMANGA OBREROS 
            IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PAGAR MESES DE 31 EN NOMINA QUINCENAL',6,SYSDATE),'NO') = 'SI' AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'NOMINA MENSUAL',6,SYSDATE),'SI') = 'NO' THEN 
                MI_BASE := 0; 
                         <<CALCULARBASE>>
                         FOR MI_RSINTER IN (SELECT ID_DE_CONCEPTO, NOMBRE_CONCEPTO, REPORTEEPS
                                     FROM   CONCEPTOS
                                     WHERE  COMPANIA    = PCK_NOMINA.GL_COMPANIA 
                                       AND  REPORTEEPS  <> 0
                                    ORDER BY ID_DE_CONCEPTO
                                    )
                          LOOP
                            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' AND (MI_RSINTER.ID_DE_CONCEPTO = 2 OR MI_RSINTER.ID_DE_CONCEPTO = 7)  THEN
                              MI_BASE := MI_BASE + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(MI_RSINTER.ID_DE_CONCEPTO)+PCK_NOMINA.FC_CNP(MI_RSINTER.ID_DE_CONCEPTO), 0) * PCK_NOMINA.CPARENTRADA(1).PORC_SALARIO_INTEGRAL / 100, 0);
                            ELSE
                              MI_BASE := MI_BASE + PCK_NOMINA.FC_CN(MI_RSINTER.ID_DE_CONCEPTO)+PCK_NOMINA.FC_CNP(MI_RSINTER.ID_DE_CONCEPTO);
                            END IF;    
                          END LOOP CALCULARBASE;
                          
                 MI_BASE := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASE, UN_PRECISION => 2);

                  --JM CC 3085
                 IF (PCK_NOMINA.FC_CNP(35)+ PCK_NOMINA.FC_CN(35)) > 0 THEN 
                    MI_BASE:= MI_BASE + ((CASE WHEN  PCK_NOMINA.FC_CN(10) > 0 THEN  PCK_NOMINA.FC_CN(10) ELSE  PCK_NOMINA.FC_CN(1) END)/ 30 * (PCK_NOMINA.FC_CNP(35)+ PCK_NOMINA.FC_CN(35)));
                 END IF;
                 
                IF (PCK_NOMINA.FC_CNP(50)+PCK_NOMINA.FC_CN(50)+PCK_NOMINA.FC_CNP(53)+PCK_NOMINA.FC_CN(53)+ PCK_NOMINA.FC_CNP(56)+PCK_NOMINA.FC_CN(56)+ PCK_NOMINA.FC_CNP(51)+PCK_NOMINA.FC_CN(51)+ PCK_NOMINA.FC_CNP(59)+PCK_NOMINA.FC_CN(59)+ PCK_NOMINA.FC_CNP(60)+PCK_NOMINA.FC_CN(60)+ PCK_NOMINA.FC_CNP(49)+PCK_NOMINA.FC_CN(49)+ PCK_NOMINA.FC_CNP(48)+PCK_NOMINA.FC_CN(48)+ PCK_NOMINA.FC_CNP(52)+PCK_NOMINA.FC_CN(52)+ PCK_NOMINA.FC_CNP(518)+PCK_NOMINA.FC_CN(518)+ PCK_NOMINA.FC_CNP(522)+PCK_NOMINA.FC_CN(522)+ PCK_NOMINA.FC_CNP(54)+PCK_NOMINA.FC_CN(54)+ PCK_NOMINA.FC_CNP(57)+PCK_NOMINA.FC_CN(57)) > 0 AND (PCK_NOMINA.FC_CN(9)+PCK_NOMINA.FC_CNP(9) < 30 ) AND PCK_NOMINA.GL_SPER = 2 THEN
                    MI_BASE := MI_BASE - (PCK_NOMINA.FC_CNP(50)+PCK_NOMINA.FC_CN(50)+PCK_NOMINA.FC_CNP(53)+PCK_NOMINA.FC_CN(53)+ PCK_NOMINA.FC_CNP(56)+PCK_NOMINA.FC_CN(56)+ PCK_NOMINA.FC_CNP(51)+PCK_NOMINA.FC_CN(51)+ PCK_NOMINA.FC_CNP(59)+PCK_NOMINA.FC_CN(59)+ PCK_NOMINA.FC_CNP(60)+PCK_NOMINA.FC_CN(60)+ PCK_NOMINA.FC_CNP(49)+PCK_NOMINA.FC_CN(49)+ PCK_NOMINA.FC_CNP(48)+PCK_NOMINA.FC_CN(48)+ PCK_NOMINA.FC_CNP(52)+PCK_NOMINA.FC_CN(52)+ PCK_NOMINA.FC_CNP(518)+PCK_NOMINA.FC_CN(518)+ PCK_NOMINA.FC_CNP(522)+PCK_NOMINA.FC_CN(522)+ PCK_NOMINA.FC_CNP(54)+PCK_NOMINA.FC_CN(54)+ PCK_NOMINA.FC_CNP(57)+PCK_NOMINA.FC_CN(57));
                END IF;

            END IF;
             --JM FIN CC1404
            --JM INI 7748140  no esta tomando los conceptos FACTIR AUTOLIQUIDACION para sinchi 2da quincena (se recalcula la base)
            -- MOD JM CC 1918 01/07/2025
            --(CC:3708_CFBARRERA: Eliminacion de nit:860061110)
            IF  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PAGAR MESES DE 31 EN NOMINA QUINCENAL',6,SYSDATE),'NO') = 'NO'
               AND ((UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'NO')) = 'SI' AND PCK_NOMINA.GL_SPER = 7 ) OR (UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'SI')) = 'NO' AND PCK_NOMINA.GL_SPER = 2 ))  THEN             
                IF (PCK_NOMINA.FC_CN(9)+PCK_NOMINA.FC_CNP(9) > 0 and  PCK_NOMINA.FC_CN(9)+PCK_NOMINA.FC_CNP(9) <= 30 and PCK_NOMINA.FC_CN(35) = 0 and PCK_PARST.FC_PAR('SUMA CONCEPTOS AUTOLIQUIDACION POR PARAMETRO','NO') = 'SI') OR PCK_NOMINA.GL_SPER = 7 THEN
                        MI_BASE := 0;
                         <<CALCULARBASE>>
                         FOR MI_RSINTER IN (SELECT ID_DE_CONCEPTO, NOMBRE_CONCEPTO, REPORTEEPS
                                     FROM   CONCEPTOS
                                     WHERE  COMPANIA    = PCK_NOMINA.GL_COMPANIA
                                       AND  REPORTEEPS  <> 0
                                    ORDER BY ID_DE_CONCEPTO
                                    )
                          LOOP
                            IF PCK_NOMINA.GL_PROCESOREAL = 10 THEN  --(CC:3708_CFBRRERA_INI:Ajuste para tomar los conceptos acumulados del retroActivo)
                                MI_VALOR := PCK_NOMINA_COM1.FC_ACUMCONCEPTO_RETRO(PCK_NOMINA.GL_COMPANIA, MI_RSINTER.ID_DE_CONCEPTO,PCK_NOMINA.GL_SANO, 6, 1,PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                                    
                            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' AND (MI_RSINTER.ID_DE_CONCEPTO = 2 OR MI_RSINTER.ID_DE_CONCEPTO = 7) THEN
                                MI_BASE := MI_BASE + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(MI_RSINTER.ID_DE_CONCEPTO)+PCK_NOMINA_CALCULO.FC_CNP_RETRO(MI_RSINTER.ID_DE_CONCEPTO), 0) * PCK_NOMINA.CPARENTRADA(1).PORC_SALARIO_INTEGRAL / 100, 0);
                            ELSE
                                    MI_BASE := MI_BASE + PCK_NOMINA.FC_CN(MI_RSINTER.ID_DE_CONCEPTO) + PCK_NOMINA_CALCULO.FC_CNP_RETRO(MI_RSINTER.ID_DE_CONCEPTO);
                             END IF;
                             ELSE
                             IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '02' AND (MI_RSINTER.ID_DE_CONCEPTO = 2 OR MI_RSINTER.ID_DE_CONCEPTO = 7) THEN
                                MI_BASE := MI_BASE + PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(MI_RSINTER.ID_DE_CONCEPTO)+PCK_NOMINA.FC_CNP(MI_RSINTER.ID_DE_CONCEPTO), 0) * PCK_NOMINA.CPARENTRADA(1).PORC_SALARIO_INTEGRAL / 100, 0);
                            ELSE
                                    MI_BASE := MI_BASE + PCK_NOMINA.FC_CN(MI_RSINTER.ID_DE_CONCEPTO) + PCK_NOMINA.FC_CNP(MI_RSINTER.ID_DE_CONCEPTO);
                             END IF;
                             END IF;--(CC:3708_CFBRRERA_FIN)
                        END LOOP CALCULARBASE;

                END IF;
                IF ((PCK_NOMINA.FC_CN(350) + PCK_NOMINA.FC_CNP(350)+PCK_NOMINA.FC_CN(351) + PCK_NOMINA.FC_CNP(351)+PCK_NOMINA.FC_CN(352) + PCK_NOMINA.FC_CNP(352)+
                PCK_NOMINA.FC_CN(353) + PCK_NOMINA.FC_CNP(353)+PCK_NOMINA.FC_CN(354) + PCK_NOMINA.FC_CNP(354)+PCK_NOMINA.FC_CN(355) + PCK_NOMINA.FC_CNP(355)+
                PCK_NOMINA.FC_CN(367) + PCK_NOMINA.FC_CNP(367)+PCK_NOMINA.FC_CN(368) + PCK_NOMINA.FC_CNP(368)+PCK_NOMINA.FC_CN(336) + PCK_NOMINA.FC_CNP(336)) > 0) THEN
                    MI_BASE := MI_BASE - (PCK_NOMINA.FC_CN(366) + PCK_NOMINA.FC_CNP(366)+PCK_NOMINA.FC_CN(370) + PCK_NOMINA.FC_CNP(370)+PCK_NOMINA.FC_CN(371) + PCK_NOMINA.FC_CNP(371)+
                    PCK_NOMINA.FC_CN(372) + PCK_NOMINA.FC_CNP(372)+PCK_NOMINA.FC_CN(373) + PCK_NOMINA.FC_CNP(373)+PCK_NOMINA.FC_CN(374) + PCK_NOMINA.FC_CNP(374)+
                    PCK_NOMINA.FC_CN(375) + PCK_NOMINA.FC_CNP(375)+ PCK_NOMINA.FC_CN(376) + PCK_NOMINA.FC_CNP(376)+ PCK_NOMINA.FC_CN(377) + PCK_NOMINA.FC_CNP(377)+
                    PCK_NOMINA.FC_CN(378) + PCK_NOMINA.FC_CNP(378));
                END IF; --CC4238 MPEREZ - Ajuste para no tener las incapacidades en cuenta, ya que en codigo anterior ya las estan guardando de forma indiviual
                 MI_BASE := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASE, UN_PRECISION => 2);
            END IF;
            --JM FIN 7748140
            --JM CC 2203 para que tome la base completa en la primera 
            IF UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'SI')) = 'NO' AND PCK_NOMINA_PROC01.FC_AUSENTISMO2DAQUINCENA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) AND PCK_PARST.FC_PAR('MOSTRAR SEGURIDAD SOCIAL EN PRIMERA QUINCENA','SI') = 'NO'THEN -- JM 2203
                MI_BASE := (MI_SALARIO_BASE + (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) - PCK_NOMINA.FC_CN(1) ELSE 0 END))/2;
                MI_BASE_AUSENTISMO := MI_SALARIO_BASE;--(CC:2913_CFBARRERA)
            END IF;
            --JM FIN CC 2203 
            --(MZANGUNA:14/11/2018)-Cambio dias de sueldo con encargo.
            --(APINEDA:01/08/2019)-Se valida parametro PAGAR DIAS TRABAJADOS EN LIQUIDACION DEFINITIVA debido a que en FederaciÃ³n Nacional de Departamentos no estaba tomando los dÃ­as trabajados.
            IF PCK_NOMINA.GL_SPER = 7 AND PCK_NOMINA.FC_CN(404) <> 0 AND PCK_PARST.FC_PAR('PAGAR DIAS TRABAJADOS EN LIQUIDACION DEFINITIVA', 'NO') = 'NO' THEN --(MZANGUNA:29/04/2019)-Tome el concepto 9 acumulado cÃ¡lculado en la funciÃ³n FC_CALCULAR_ARP_MES
                MI_DIAS := PCK_NOMINA.GL_CN9;
            ELSE
            	-- TICKET 7740193 EFCM: EN LRE CON ENCARGO SE RESPETA EL VALOR DE CN 9  --MOD JM 7800139 07/10/2024
              IF ( PCK_NOMINA.FC_CN(11) > 0 AND PCK_NOMINA.FC_CN(365) > 0 AND PCK_NOMINA.FC_CNAN(11) >= 30) THEN
                    MI_DIAS := CASE WHEN PCK_NOMINA.FC_CN(9) = 0 THEN (PCK_NOMINA.FC_CN(209)+PCK_NOMINA_PROC01.FC_DIASLRE_ENCARGO) ELSE PCK_NOMINA.FC_CN(9) END; --JM 07/10/2024
                ELSE
                    MI_DIAS := CASE WHEN PCK_NOMINA.FC_CN(9) = 0 THEN PCK_NOMINA.FC_CN(209) ELSE PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CN(11) END;
                    IF MI_DIAS = 0 AND PCK_NOMINA.FC_CNAN(11) <> 0 THEN --MOD JM CC2845
                        MI_DIAS := (PCK_NOMINA.FC_CNAN(11)-PCK_NOMINA_PROC01.FC_DIASLRE_ENCARGO) - (PCK_NOMINA.FC_CN(35) - PCK_NOMINA.FC_CN(98))- (PCK_NOMINA.FC_CN(350) + PCK_NOMINA.FC_CN(351) + PCK_NOMINA.FC_CN(352) + PCK_NOMINA.FC_CN(353) + PCK_NOMINA.FC_CN(354) + PCK_NOMINA.FC_CN(355) + PCK_NOMINA.FC_CN(336) + PCK_NOMINA.FC_CN(358) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(367) + PCK_NOMINA.FC_CN(368) + PCK_NOMINA.FC_CN(360) + PCK_NOMINA.FC_CN(363) + PCK_NOMINA.FC_CN(364)  + PCK_NOMINA.FC_CN(390) + PCK_NOMINA.FC_CN(339));
                    END IF;
                END IF;                
            END IF;

            --JM CC 3085
            IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PAGAR MESES DE 31 EN NOMINA QUINCENAL',6,SYSDATE),'NO') = 'SI' AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'NOMINA MENSUAL',6,SYSDATE),'SI') = 'NO' AND (PCK_NOMINA.FC_CNP(35)+ PCK_NOMINA.FC_CN(35)) > 0 THEN 
                MI_DIAS :=  PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CNP(9); 
            END IF;

            IF PCK_NOMINA.GL_SPER = 2 THEN 
                                          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM1(UN_COMPANIA   => PCK_NOMINA.GL_COMPANIA,
                                                              UN_ANO1       => PCK_NOMINA.GL_SANO, 
                                                              UN_MES1       => PCK_NOMINA.GL_SMES, 
                                                              UN_PERIODO1   => 1, 
                                                              UN_IDEMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            END IF;

            --(APINEDA:31/07/2019)-Se agrega validaciÃ³n para que al calcular retroactivo se tenga en cuenta la fecha en la que el empleado se retirÃ³ dentro del periodo, debido a que en febrero se estaban calculando mÃ¡s dÃ­as de los realmente trabajados.
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT THEN
                MI_FFINAL := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO;
            ELSE
                IF ((MI_DIAS + MI_DIASNOTRABAJADOS) < 30 OR MI_DIASNOTRABAJADOS >= 28) AND PCK_NOMINA.GL_SMES = 2 AND UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'NO')) = 'SI' AND MI_DIASNOTRABAJADOS > 0 THEN --(MZANGUNA:02/05/2019)-Aproxime los dÃƒÂ­as trabajados en Febrero. --(GPORTILLA:03/03/2023) - Se agrega validacion sobre los dias no trabajados, donde deben ser mayor a cero para que ingrese a recalcular los dias a liquidar  --mod JM CC 3515
                    --(jmillan: 16/01/2023 ticket#7716620) se ajusta el valor de los dias en caso de que sea el febrero tomar 30 para el calculo de cocepto 112
                    MI_DIASNOTRABAJADOSFEB := MI_DIASNOTRABAJADOS; -- JM CC 3787
                    IF MI_DIASNOTRABAJADOS >= 28 AND NOT(MI_FEB_SITRABAJO) THEN
                      MI_DIASNOTRABAJADOS := 30;
                      MI_DIAS := 0;
                    ELSE  
                      MI_DIAS :=  MI_DIAS + (30 - (MI_DIAS + MI_DIASNOTRABAJADOS));
                    END IF;
                    IF (PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CNP(9) > 0) AND MI_DIASNOTRABAJADOS = 30  THEN  -- JM CC 3787 caso especial que hay un dia trabajado y los demas ausentismo 
                       MI_DIAS := 30 - MI_DIASNOTRABAJADOSFEB;
                       MI_DIASNOTRABAJADOS := MI_DIASNOTRABAJADOS - MI_DIAS;
                    END IF;
                END IF;
            END IF;




            MI_FINICIO := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO END;            
            --(APINEDA:31/07/2019)-Se sube lÃ­nea que asigna valor a la fecha final (MI_FFINAL) con la fecha de terminaciÃ³n de contrato, para tener en cuenta en el calculo de retroactivo cuando hay retiro en febrero.


            MI_LLAVE := UN_COMPANIA || '--' || UN_EMPLEADO || '--' || UN_ANO || '--' || UN_MES;

            IF MI_CNTI = 0 OR MI_CNTI = MI_CUENTAINI THEN
                MI_CONSECUTIVO :=  PCK_SYSMAN_UTL.FC_GENCONSECUTIVO
                                    (UN_TABLA    => MI_TABLA,
                                    UN_CRITERIO => 'COMPANIA = '''|| UN_COMPANIA ||'''
                                               AND  ID_DE_EMPLEADO = '|| UN_EMPLEADO ||'
                                               AND  ANO = '|| UN_ANO ||'
                                               AND  MES = '|| UN_MES ||'
                                               AND  PERIODO = '|| PCK_NOMINA.GL_SPER  ||' ',
                                    UN_CAMPO    => 'CONSECUTIVO');
            ELSE
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
            END IF;

            MI_TI_BASES(MI_CNTI).COMPANIA := UN_COMPANIA;
            MI_TI_BASES(MI_CNTI).LLAVENOVEDAD := MI_LLAVE;
            MI_TI_BASES(MI_CNTI).TIPONOVEDAD := MI_TIPONOVEDAD;
            MI_TI_BASES(MI_CNTI).CONSECUTIVO := MI_CONSECUTIVO;
            MI_TI_BASES(MI_CNTI).ID_DE_EMPLEADO := UN_EMPLEADO;
            MI_TI_BASES(MI_CNTI).ANO := UN_ANO;
            MI_TI_BASES(MI_CNTI).MES := UN_MES;
            MI_TI_BASES(MI_CNTI).PERIODO := PCK_NOMINA.GL_SPER;
            MI_TI_BASES(MI_CNTI).FECHAINICIAL := MI_FINICIO;
            MI_TI_BASES(MI_CNTI).FECHAFINAL := MI_FFINAL;
            MI_TI_BASES(MI_CNTI).BASE := MI_BASE;
            MI_TI_BASES(MI_CNTI).DIAS := MI_DIAS;
            MI_TI_BASES(MI_CNTI).BASEPROPORCIONAL := 0;
            MI_TI_BASES(MI_CNTI).CREATED_BY := UN_USUARIO;
            MI_TI_BASES(MI_CNTI).DATE_CREATED := SYSDATE;
            MI_TI_BASES(MI_CNTI).MANUAL := 0;

            MI_CNTI := MI_CNTI + 1;

        END IF;
        MI_CUENTA := MI_DIASNOTRABAJADOS + MI_DIAS;

        IF (MI_CUENTA) > 30 THEN
            --Esta reportando --DIAS-- dÃ­as, debe reportar mÃ¡ximo 30, Empleado: --EMPLEADO--.

            MI_MSGERROR(1).CLAVE := 'DIAS';
            MI_MSGERROR(1).VALOR := MI_CUENTA;
            MI_MSGERROR(2).CLAVE := 'EMPLEADO';
            MI_MSGERROR(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRECOMPLETO;

            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA    => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD => PCK_ERRORES.ERR_NOPUEDESUPERAR30DIAS
                ,UN_REEMPLAZOS  => MI_MSGERROR
                ,UN_PROCESO     => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO         => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES         => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO     => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER        => PCK_CONEXION.FC_GETUSER );

        END IF;


        --(MZANGUNA:07/11/2018)- CÃ¡lculo de bases proporcionales en base a las bases previamentes calculadas.
        --Elimino todas las novedades
        --CC 2203 para que no me borre las novedades del la 2da ya que cuando calcula la primera y hay asuentismo, todo va a la primera (pero con el periodo2)
        IF NOT (UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'SI')) = 'NO' AND PCK_NOMINA_PROC01.FC_AUSENTISMO2DAQUINCENA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) AND PCK_NOMINA.GL_SPER = 2) THEN --JM CC 2203
        BEGIN
            MI_TABLA := 'BASESNOVEDADES';
            MI_CONDICIONACME := 'COMPANIA = '''|| UN_COMPANIA ||'''
                            AND ID_DE_EMPLEADO = '|| UN_EMPLEADO ||'
                            AND ANO = '|| UN_ANO ||'
                            AND MES = '|| UN_MES ||'
                            AND PERIODO = '|| PCK_NOMINA.GL_SPER ||' ';
            BEGIN
                MI_FILAS  := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                UN_ACCION    => 'E',
                UN_CONDICION => MI_CONDICIONACME);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_MSGERROR(1).CLAVE := 'EMPLEADO';
            MI_MSGERROR(1).VALOR := UN_EMPLEADO;

            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_ELIMINARBASENOVEDAD  ,
                UN_REEMPLAZOS => MI_MSGERROR );
        END;
        END IF; --JM CC 2203
        <<BASESPROPORCIONALES>>
        FOR i IN MI_TI_BASES.FIRST .. MI_TI_BASES.LAST
        LOOP
            --Calculo todas las bases incluyendo las manuales.
            MI_BASEPROPORCIONAL := PCK_NOMINA_SEGSOCI.FC_BASE(UN_IBC       => MI_BASE--MI_TI_BASES(i).BASE
                                                             ,UN_DIAS      => MI_TI_BASES(i).DIAS
                                                             ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                             ,UN_FRACCION  => FALSE
                                                             ,UN_PLANO_SOI => TRUE);

            IF PCK_NOMINA.GL_SPER = 7 AND PCK_NOMINA.FC_CN(404) <> 0 THEN  -- JM CC 1704 04/06/2025
                MI_BASEPROPORCIONAL := MI_BASE;
            END IF; 

            IF MI_TI_BASES(i).TIPONOVEDAD = 2 THEN
                MI_INCAPACIDAD := SUBSTR(MI_TI_BASES(i).LLAVENOVEDAD, INSTR(MI_TI_BASES(i).LLAVENOVEDAD,'--',1,3) + 2, INSTR(MI_TI_BASES(i).LLAVENOVEDAD,'--',1,4) - 2 - INSTR(MI_TI_BASES(i).LLAVENOVEDAD,'--',1,3) );
            ELSE
                MI_INCAPACIDAD := ' ';
            END IF;
            
                 MI_BASE_INCAP := 0;
                     IF PCK_NOMINA.FC_CN(348) <> 0 THEN
                         MI_BASE_INCAP := PCK_NOMINA.FC_CN(348);
                    END IF;
                    
                    
        IF MI_TI_BASES(i).TIPONOVEDAD = 2 THEN    
            IF  MI_INCAPACIDAD = '01' THEN
                --(APINEDA:18/10/2019)-Se asigna valor de campo DIASCOMPLETOSRECONOCIDOS a la variable MI_DD para tener en cuenta el nÃƒÂºmero de dÃƒÂ­as pagados al 100%.
                MI_DD := MI_TI_BASES(i).DIASCOMPLETOSRECONOCIDOS;
                MI_DIAS :=  MI_TI_BASES(i).DIAS - MI_DD;
            
                --aqui incapacidad de tipo 1 pero sin 30 dias y 150 
                    MI_BASEPROPIIF := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                        (UN_IBC       => CASE WHEN PCK_NOMINA.FC_CN(348) <> 0 THEN MI_BASE_INCAP ELSE MI_TI_BASES(i).BASE END
                        ,UN_DIAS      => MI_DIAS
                        ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                        ,UN_ROUND     => 0
                        ,UN_FACTOR    => 'PARINC');
                        
                    MI_BASEPROPIIF := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASEPROPIIF
                        ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
               
                        MI_BASEPROPIIF := MI_BASEPROPIIF+PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                            (UN_IBC       => CASE WHEN PCK_NOMINA.FC_CN(348) <> 0 THEN
                                                CASE WHEN PCK_PARST.FC_PAR('LIQUIDAR 1-2 DIAS CON S.B.M.', '') = 'SI' THEN
                                                  MI_SALARIO_BASE
                                                ELSE--CC3132 -FECHA:10/12/2025 - NCARDENAS
                                                    CASE WHEN PCK_PARST.FC_PAR('BASE CALCULO 1 A 2 DIAS INCAP. AMBULATORIA IBC ACTUAL', 'NO') = 'SI' THEN
                                                        CASE WHEN PCK_NOMINA.FC_CN(10) = 0
                                                            THEN PCK_NOMINA.FC_CN(1)
                                                            ELSE PCK_NOMINA.FC_CN(10)
                                                        END
                                                    ELSE   
                                                        CASE WHEN PCK_PARST.FC_PAR('APLICA SENTENCIA 543 DE 2007', '') = 'SI'  THEN --(CC:2870_CFBARRERA)
                                                            GREATEST (MI_BASE_INCAP, CASE WHEN PCK_NOMINA.FC_CN(150) <> 0 AND PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA, 150) <> 0 THEN (MI_BASE-PCK_NOMINA.FC_CN(150)) ELSE MI_BASE END) --MOD JM CC 3274
                                                        ELSE
                                                         MI_BASE_INCAP
                                                        END
                                                    END
                                                END
                                              ELSE MI_BASE END --JM 7752781 18/09/2024 --JM 7741502  18/07/2024 --MI_TI_BASES(i).BASE
                            ,UN_DIAS      => MI_DD
                            ,UN_DECIMALES => 0
                            ,UN_ROUND     => PCK_NOMINA.GL_RBASE1990);
                            
                    MI_BASEPROPIIF := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASEPROPIIF
                        ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);

                         MI_BASEPROPORCIONAL := MI_BASEPROPIIF;
               
               IF (MI_TI_BASES(i).DIAS >= PCK_NOMINA.FC_CN(4)) AND PCK_NOMINA.FC_CN(150) <> 0 THEN --JM 13/05/2025 CC1534
                  MI_BASEPROPORCIONAL:= MI_BASEPROPORCIONAL + PCK_NOMINA.FC_CN(150);
                END IF;
          
            ELSIF MI_INCAPACIDAD = '02' THEN
                --(APINEDA:28/11/2019)-Se agrega llamado a funciÃ³n nueva para validar si aplica calculo de base proporcional al 100% de lo contrario se aplica a las 2/3 partes. TAR1000095945
               -- IF FC_APLICA100SENTENCIA543(MI_INCAPACIDAD, MI_TI_BASES(i).BASE, MI_TI_BASES(i).DIAS) = FALSE THEN
                    MI_BASEPROPORCIONAL := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                        (UN_IBC       => CASE WHEN PCK_NOMINA.FC_CN(348) <> 0 THEN MI_BASE_INCAP ELSE  MI_TI_BASES(i).BASE END 
                        ,UN_DIAS      => MI_TI_BASES(i).DIAS
                        ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                        ,UN_ROUND     => 0
                        ,UN_FACTOR    => 'PARINC');

                    IF (MI_TI_BASES(i).DIAS >= PCK_NOMINA.FC_CN(4)) AND PCK_NOMINA.FC_CN(150) <> 0 THEN --JM 13/05/2025 CC1534
                        MI_BASEPROPORCIONAL:= MI_BASEPROPORCIONAL + PCK_NOMINA.FC_CN(150);
                    END IF;
                        
                    MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASEPROPORCIONAL
                        ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
               -- END IF;
            -- TICKET 7737212 EFCM: NOVEDAD INCAPACIDAD TIPO 3 SE LIQUIDA AL PORCENTAJE PARAMETRIZADO
            ELSIF MI_INCAPACIDAD = '03' THEN
                
                    MI_BASEPROPORCIONAL := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                        (UN_IBC       => CASE WHEN PCK_NOMINA.FC_CN(348) <> 0 THEN MI_BASE_INCAP ELSE  MI_TI_BASES(i).BASE END 
                        ,UN_DIAS      => MI_TI_BASES(i).DIAS
                        ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                        ,UN_ROUND     => 0
                        ,UN_FACTOR    => 'PARINC');

                    IF (MI_TI_BASES(i).DIAS >= PCK_NOMINA.FC_CN(4)) AND PCK_NOMINA.FC_CN(150) <> 0 THEN --JM 13/05/2025 CC1534
                        MI_BASEPROPORCIONAL:= MI_BASEPROPORCIONAL + PCK_NOMINA.FC_CN(150);
                    END IF;
                        
                    MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => MI_BASEPROPORCIONAL
                        ,UN_PRECISION => PCK_NOMINA.GL_RBASE1990);
              
      
          ELSIF MI_INCAPACIDAD in ('04','09','08','05') THEN --MOD por JM el 17/10/2024
          
                     IF PCK_NOMINA.FC_CN(343) <> 0 THEN --MOD por JM el 17/10/2024
                         MI_BASE_INCAP := PCK_NOMINA.FC_CN(343);
                    END IF;

                MI_BASEPROPORCIONAL := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                    (UN_IBC       => CASE WHEN PCK_NOMINA.FC_CN(343) <> 0 THEN MI_BASE_INCAP ELSE  MI_BASE END 
                    ,UN_DIAS      => MI_TI_BASES(i).DIAS
                    ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                    ,UN_ROUND     => 0);
              
              IF (MI_TI_BASES(i).DIAS >= PCK_NOMINA.FC_CN(4)) AND PCK_NOMINA.FC_CN(150) <> 0 THEN --JM 13/05/2025 CC1534
                  MI_BASEPROPORCIONAL:= MI_BASEPROPORCIONAL + PCK_NOMINA.FC_CN(150);
              END IF;

           /* ELSIF MI_INCAPACIDAD = '05' THEN
                --AQUI SUMABA CON DD PERO A ESTA ALTURA EL MI_DD ES 0
                MI_BASEPROPORCIONAL := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                    (UN_IBC       => CASE WHEN PCK_NOMINA.FC_CN(348) <> 0 THEN MI_BASE_INCAP ELSE  MI_BASE END  --JM 7752781 18/09/2024 -- JM 7741502 18/07/2024--MI_TI_BASES(i).BASE
                    ,UN_DIAS      => MI_TI_BASES(i).DIAS
                    ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                    ,UN_ROUND     => 0); */ --comentado por JM el 17/10/2024
            ELSIF MI_INCAPACIDAD IN('11','12','10') THEN
				--CC_894(07/03/2025 JCROJAS): Cuando ya venga dividido el SBM no lo vuelva a hacer
                IF PCK_NOMINA.FC_CN(348) = PCK_NOMINA.FC_CN(1)/2 AND PCK_NOMINA.FC_CN(360) >= 30 THEN 
                    MI_BASEPROPORCIONAL := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                        (UN_IBC       => CASE WHEN PCK_NOMINA.FC_CN(348) <> 0 THEN MI_BASE_INCAP ELSE  MI_BASE END  
                        ,UN_DIAS      => MI_TI_BASES(i).DIAS
                        ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                        ,UN_ROUND     => 0
                        ,UN_FACTOR    => 'VAL0.05');
                ELSE 		 
					--TICKET 7743926(30/04/2024 JCROJAS)
					MI_BASEPROPORCIONAL := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
						(UN_IBC       => CASE WHEN PCK_NOMINA.FC_CN(348) <> 0 THEN MI_BASE_INCAP ELSE  MI_BASE END  --JM 7752781 18/09/2024 -- JM 7741502 18/07/2024 --MI_TI_BASES(i).BASE
						,UN_DIAS      => MI_TI_BASES(i).DIAS
						,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
						,UN_ROUND     => 0
						,UN_FACTOR    => 'VAL*0.5');

            IF (PCK_NOMINA.FC_CN(348) <> 0 AND MI_BASE_INCAP <= PCK_NOMINA.FC_CN(201)) OR (MI_BASE <= PCK_NOMINA.FC_CN(201)) THEN  -- JM CC 3771
                        
                            MI_BASEPROPORCIONAL := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                                    (UN_IBC       => CASE WHEN PCK_NOMINA.FC_CN(348) <> 0 THEN MI_BASE_INCAP ELSE  MI_BASE END  
                                    ,UN_DIAS      => MI_TI_BASES(i).DIAS
                                    ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                    ,UN_ROUND     => 0
                                    ,UN_FACTOR    => 'VAL0.05');
                        
              END IF;
			  --CC_4386(03/07/2026 JCROJAS): Cuando MI_BASEPROPORCIONAL sea menor al salario minimo y la suma de los CN 355, 360 y 336 sea >= 30 se le asigne el CN 201 a MI_BASEPROPORCIONAL.
              IF (PCK_NOMINA.FC_CN(355)+PCK_NOMINA.FC_CN(360)+PCK_NOMINA.FC_CN(336)) >= 30 AND MI_BASEPROPORCIONAL < PCK_NOMINA.FC_CN(201) THEN
                    MI_BASEPROPORCIONAL := PCK_NOMINA.FC_CN(201);
              END IF;	 
				END IF;

            IF (MI_TI_BASES(i).DIAS >= PCK_NOMINA.FC_CN(4)) AND PCK_NOMINA.FC_CN(150) <> 0 THEN --JM 13/05/2025 CC1534
              MI_BASEPROPORCIONAL:= MI_BASEPROPORCIONAL + PCK_NOMINA.FC_CN(150);
            END IF;

            ELSE
             -- JM 7722164 INI  en caso de ser encargo fraccionado tomar los dias de la licencia en base a salario o encargo
             IF (PCK_NOMINA.FC_CN(11) > 0  AND PCK_NOMINA.FC_CN(9) > 0 AND  PCK_NOMINA.FC_CN(359) > 0 ) THEN
                            MI_VALOR:=0;
                            BEGIN
                                SELECT SUELDOMENSUAL INTO MI_VALOR FROM ENCARGOS
                                JOIN LICENCIAS
                                ON   encargos.compania =  licencias.compania
                                AND  encargos.id_de_empleado = licencias.id_de_empleado
                                AND  (licencias.FECHA_INICIO between encargos.FECHAINICIO and encargos.FECHAFINAL or licencias.FECHA_FINAL between encargos.FECHAINICIO and encargos.FECHAFINAL)
                                WHERE encargos.COMPANIA = UN_COMPANIA
                                AND encargos.ID_DE_EMPLEADO = UN_EMPLEADO
                                AND encargos.ANO = PCK_NOMINA.GL_SANO
                                AND encargos.MES = PCK_NOMINA.GL_SMES
                                AND encargos.PERIODO = PCK_NOMINA.GL_SPER;
                                EXCEPTION  WHEN NO_DATA_FOUND THEN
                                    MI_VALOR := MI_SALARIO_BASE;
                            END;
                             MI_BASEPROPORCIONAL := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                            (UN_IBC       => MI_VALOR--MI_TI_BASES(i).BASE
                            ,UN_DIAS      => MI_TI_BASES(i).DIAS
                            ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                            ,UN_ROUND     => 0);
                --TICKET 7740193 EFCM: SE EVALUA ENCARGOS Y LICENCIAS PARA CALCULAR LOS DIAS DE LICENCIA CON SU CORRESPONDIENTE ASIGNACION
                ELSIF (PCK_NOMINA.FC_CN(11) > 0  AND PCK_NOMINA.FC_CN(9) > 0 AND  PCK_NOMINA.FC_CN(365) > 0) THEN
                    BEGIN
                        SELECT  DISTINCT TRUNC(TO_DATE(TO_CHAR(LEAST(MI_TI_BASES(i).FECHAFINAL, ENCARGOS.FECHAFINAL),'DD/MM/YYYY'),'DD/MM/YYYY') - TO_DATE(TO_CHAR(GREATEST(MI_TI_BASES(i).FECHAINICIAL, ENCARGOS.FECHAINICIO ),'DD/MM/YYYY'),'DD/MM/YYYY')) + 1
                        INTO    MI_VALAUX
                        FROM    ENCARGOS
                        WHERE   COMPANIA = UN_COMPANIA
                                AND ID_DE_EMPLEADO = UN_EMPLEADO
                                AND (MI_TI_BASES(i).FECHAINICIAL BETWEEN ENCARGOS.FECHAINICIO AND ENCARGOS.FECHAFINAL
                                OR
                                ENCARGOS.FECHAINICIO BETWEEN MI_TI_BASES(i).FECHAINICIAL AND MI_TI_BASES(i).FECHAFINAL)
                                ;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_VALAUX := 0;
                    END;
                    IF ( MI_VALAUX <= MI_TI_BASES(i).DIAS) THEN
                        MI_VALAUX := (PCK_NOMINA.FC_CN(10) / 30 * MI_VALAUX) + (PCK_NOMINA.FC_CN(1) / 30 * (MI_TI_BASES(i).DIAS-MI_VALAUX));
                        MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(MI_VALAUX, 0) ;
                    ELSE
                        MI_BASEPROPORCIONAL := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL  (
                                                                                UN_IBC       => MI_BASE--MI_TI_BASES(i).BASE
                                                                                ,UN_DIAS      => MI_TI_BASES(i).DIAS
                                                                                ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                                                ,UN_ROUND     => 0);
                    END IF;
                --TICKET 7740193 FIN--
                ELSE
                    MI_BASEPROPORCIONAL := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                    (UN_IBC       =>  CASE WHEN PCK_NOMINA.FC_CN(150) <> 0 AND MI_TI_BASES(i).DIAS <  PCK_NOMINA.FC_CN(4) AND  PCK_NOMINA.GL_SPER = 3 THEN (MI_BASE-PCK_NOMINA.FC_CN(150)) ELSE CASE WHEN (PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNP(359)+PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CNP(356)+PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CNP(357)) <> 0 AND MI_TI_BASES(i).DIAS <=  (PCK_NOMINA.FC_CN(4) + PCK_NOMINA.FC_CNP(4)) AND  PCK_NOMINA.GL_SPER = 2 THEN MI_SALARIO_BASE ELSE MI_BASE END END  --JM 7752781 18/09/2024 -- JM 7741502 18/07/2024 --MI_TI_BASES(i).BASE
                    ,UN_DIAS      => MI_TI_BASES(i).DIAS
                    ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                    ,UN_ROUND     => 0);
                END IF; -- JM 7722164 FIN
                /* MI_BASEPROPORCIONAL := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                    (UN_IBC       => MI_BASE--MI_TI_BASES(i).BASE
                    ,UN_DIAS      => MI_TI_BASES(i).DIAS
                    ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                    ,UN_ROUND     => 0); */ --comentado por jm el 14/12/2023
             
                END IF;
             
            END IF;  --Fin incapacidades y licencias;
            
            IF MI_TI_BASES(i).TIPONOVEDAD = 1 THEN
              -- TICKET 7708882 ECABRERA: SE CAMBIA BASE PARA IDIPRON PARA CALCULO DE BASEPROPORCIONAL
              IF ( PCK_PARST.FC_PAR('LIQUIDACION PARA IDIPRON', 'NO') = 'NO' ) THEN
                 IF MI_TI_BASES(i).DIAS <> PCK_NOMINA.FC_CN(4) AND PCK_NOMINA.FC_CN(150) <> 0 THEN --JM 13/08/2024
                    MI_VALOR := CASE WHEN MI_TI_BASES(i).DIAS = 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(  (MI_TI_BASES(i).BASE - PCK_NOMINA.FC_CN(150))  / 30 * MI_TI_BASES(i).DIAS , 0) END; --JM 7752781 18/09/2024
                  ELSE
                    MI_VALOR := CASE WHEN MI_TI_BASES(i).DIAS = 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(  MI_TI_BASES(i).BASE  / 30 * MI_TI_BASES(i).DIAS , 0) END; --JM 7752781 18/09/2024
                  END IF;
                ELSE
                    IF MI_TI_BASES(i).DIAS <> PCK_NOMINA.FC_CN(4) AND PCK_NOMINA.FC_CN(150) <> 0 THEN --JM 13/08/2024
                        MI_VALOR := CASE WHEN MI_TI_BASES(i).DIAS = 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND( (MI_BASE-PCK_NOMINA.FC_CN(150))  / 30 * MI_TI_BASES(i).DIAS , 0) END; --JM 7752781 18/09/2024
                    ELSE
                        MI_VALOR := CASE WHEN MI_TI_BASES(i).DIAS = 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(  MI_BASE  / 30 * MI_TI_BASES(i).DIAS , 0) END; --JM 7752781 18/09/2024
                    END IF;
                END IF;
                -- TICKET 7708882 FIN
                MI_BASEPROPORCIONAL := CASE WHEN ABS(MI_BASEPROPORCIONAL - MI_VALOR) BETWEEN -100 AND 100 THEN MI_BASEPROPORCIONAL ELSE MI_VALOR END; --(MZANGUNA:22/04/2019) Ajuste redondeo
            ELSIF MI_TI_BASES(i).TIPONOVEDAD = 4 THEN
                IF MI_TI_BASES(i).MANUAL <> 0 THEN
                    MI_VALOR := PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASE / 30 * MI_TI_BASES(i).DIAS);
                    MI_BASEPROPORCIONAL := CASE WHEN ABS(MI_BASEPROPORCIONAL - MI_VALOR) BETWEEN -100 AND 100 THEN MI_BASEPROPORCIONAL ELSE MI_VALOR END; --(MZANGUNA:22/04/2019) Ajuste redondeo
                ELSE
                      -- jmillan 7737788  caso idiprom encargo fraccionado
                      -- (CC:3708_CFBARRERA: Se realiza ajuste para que ingrese cuando es nomina retroactivo)
                     IF   ((PCK_PARST.FC_PAR('LIQUIDACION PARA IDIPRON', 'NO') = 'SI' AND ((PCK_NOMINA.FC_CN(11) > 0  AND PCK_NOMINA.FC_CN(9) > 0) OR  PCK_NOMINA.FC_CN(62) > 0 )) OR ((PCK_NOMINA.FC_CN(11) > 0  AND PCK_NOMINA.FC_CN(9) > 0 AND  PCK_NOMINA.FC_CN(359) > 0 ))) OR UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'NO')) = 'NO' THEN
                        MI_CN212 := MI_BASE;
                     ELSE
                        IF ( PCK_NOMINA.FC_CN(11) > 0 AND PCK_NOMINA.FC_CN(365) > 0 ) AND PCK_PARST.FC_PAR('LIQUIDACION PARA IDIPRON', 'NO') = 'NO' THEN  --MOD JM 07/10/2024 7800139 
                         IF PCK_NOMINA.FC_CNAN(11) >= 30 THEN 
                             MI_CN212 := MI_BASEPROPORCIONAL;
                            ELSE 
                             MI_CN212 := MI_BASE;
                             MI_BASEPROPORCIONAL := MI_CN212; --JM 16/10/2024
                         END IF;
                        ELSE
                            --JM CC 1086 27/03/2025 algunas veces no esta tomando todos los factores de SS pero la base proporcional SI 
                            IF PCK_PARST.FC_PAR('SUMA CONCEPTOS AUTOLIQUIDACION POR PARAMETRO','') = 'SI' AND MI_BASEPROPORCIONAL > PCK_NOMINA.FC_CN(212) THEN 
                              MI_CN212 := MI_BASEPROPORCIONAL;
                            ELSE 
                              MI_CN212 := PCK_NOMINA.FC_CN(212);
                            END IF;
                            --JM CC 1086 27/03/2025 fin
                        END IF;
                     END IF;   -- jmillan 7737788  fin
                    /*COMENTADO POR JM CC 1918 01/07/2025
                    IF PCK_NOMINA.GL_SPER = 7 AND PCK_NOMINA.FC_CN(404) <> 0 THEN --(MZANGUNA:29/04/2019)-NJIMENEZ,Si es periodo final el concepto 212 para la tabla debe tomar el valor acumulado y el valor del periodo
                        PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                        MI_CN212 := MI_CN212 + PCK_NOMINA.FC_CNA(212);
                    END IF;*/
                     --jm 28/11 7738490
                    IF PCK_PARST.FC_PAR('LIQUIDACION PARA IDIPRON', 'NO') = 'SI' AND ((PCK_NOMINA.FC_CN(354) > 0  AND PCK_NOMINA.FC_CN(368) > 0)) THEN
                        MI_BASEPROPORCIONAL :=  PCK_SYSMAN_UTL.FC_ROUND(MI_BASE / 30 * MI_TI_BASES(i).DIAS);
                    ELSE
                       IF PCK_NOMINA.GL_SPER = 2 AND PCK_NOMINA.FC_CN(35) = 0 THEN --7800353_mrosero las vacaciones no tome la base completa 
                          /*
                           //Se comenta este if, debido que no esta evaluando si tiene incapaciades al realizar   MI_BASEPROPORCIONAL
                           IF (PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNP(359)+PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CNP(356)+PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CNP(357)) <> 0 THEN 
                           MI_BASEPROPORCIONAL := MI_BASEPROPORCIONAL;
                            ELSE                
                            MI_BASEPROPORCIONAL := MI_BASE;
                           END IF;
                           */
                              MI_BASEPROPORCIONAL := CASE WHEN ABS(MI_BASEPROPORCIONAL - MI_CN212) BETWEEN -100 AND 100 THEN MI_BASEPROPORCIONAL ELSE MI_CN212 END;
                              MI_TI_BASES(i).DIAS := CASE WHEN PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CNP(9) = 0 THEN PCK_NOMINA.FC_CN(209) + PCK_NOMINA.FC_CNP(209) ELSE PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CN(11) +PCK_NOMINA.FC_CNP(9) + PCK_NOMINA.FC_CNP(11) END;
                              --JM CC 1404 10/06/2025
                              IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PAGAR MESES DE 31 EN NOMINA QUINCENAL',6,SYSDATE),'NO') = 'SI' AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'NOMINA MENSUAL',6,SYSDATE),'SI') = 'NO' THEN 
                                 MI_TI_BASES(i).DIAS := CASE WHEN PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CNP(9) = 0 THEN PCK_NOMINA.FC_CN(209) + PCK_NOMINA.FC_CNP(209) ELSE PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CN(11) +PCK_NOMINA.FC_CNP(9) + PCK_NOMINA.FC_CNP(11) END;
                                 MI_BASEPROPORCIONAL := PCK_NOMINA_SEGSOCI.FC_BASE(UN_IBC       => MI_BASE
                                                                         ,UN_DIAS      => MI_TI_BASES(i).DIAS
                                                                         ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                                                         ,UN_FRACCION  => FALSE
                                                                         ,UN_PLANO_SOI => TRUE);
                              END IF;

                           ELSE
                            --JM INI CC 661 23/01/2025  no debe sacar la proporcionalidad del 150 ya que este se paga siempre completo 
                            IF PCK_NOMINA.FC_CN(150) = 0 THEN 
                              MI_BASEPROPORCIONAL := CASE WHEN ABS(MI_BASEPROPORCIONAL - MI_CN212) BETWEEN -100 AND 100 THEN MI_BASEPROPORCIONAL ELSE MI_CN212 END; --(MZANGUNA:22/04/2019) Ajuste redondeo
                            ELSE 
                              MI_BASEPROPORCIONAL := MI_CN212;
                            END IF;
                              --JM FIN CC 661 23/01/2025 
                            IF (PCK_NOMINA.GL_SPER = 7 AND PCK_NOMINA.FC_CN(404) <> 0) AND (PCK_NOMINA.FC_CN(347) + PCK_NOMINA.FC_CN(339) + (PCK_NOMINA.FC_CNP(347) + PCK_NOMINA.FC_CNP(339)) > 0) THEN --JM CC 2821
                                MI_BASEPROPORCIONAL := (PCK_NOMINA.FC_CN(2)+PCK_NOMINA.FC_CNP(2))+PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA_COM5.FC_ESFACTOR_SS(PCK_NOMINA.GL_COMPANIA, 150) <> 0, PCK_NOMINA.FC_CN(150), 0);
                            END IF;
                       END IF;
                    END IF;
                END IF;

                 IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PAGAR MESES DE 31 EN NOMINA QUINCENAL',6,SYSDATE),'NO') = 'SI' AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'NOMINA MENSUAL',6,SYSDATE),'SI') = 'NO' AND (PCK_NOMINA.FC_CNP(50)+PCK_NOMINA.FC_CN(50)+PCK_NOMINA.FC_CNP(53)+PCK_NOMINA.FC_CN(53)+ PCK_NOMINA.FC_CNP(56)+PCK_NOMINA.FC_CN(56)+ PCK_NOMINA.FC_CNP(51)+PCK_NOMINA.FC_CN(51)+ PCK_NOMINA.FC_CNP(59)+PCK_NOMINA.FC_CN(59)+ PCK_NOMINA.FC_CNP(60)+PCK_NOMINA.FC_CN(60)+ PCK_NOMINA.FC_CNP(49)+PCK_NOMINA.FC_CN(49)+ PCK_NOMINA.FC_CNP(48)+PCK_NOMINA.FC_CN(48)+ PCK_NOMINA.FC_CNP(52)+PCK_NOMINA.FC_CN(52)+ PCK_NOMINA.FC_CNP(518)+PCK_NOMINA.FC_CN(518)+ PCK_NOMINA.FC_CNP(522)+PCK_NOMINA.FC_CN(522)+ PCK_NOMINA.FC_CNP(54)+PCK_NOMINA.FC_CN(54)+ PCK_NOMINA.FC_CNP(57)+PCK_NOMINA.FC_CN(57)) > 0 AND (PCK_NOMINA.FC_CN(9)+PCK_NOMINA.FC_CNP(9) < 30 ) AND PCK_NOMINA.GL_SPER = 2 THEN --JM CC 3085
                    MI_VALOR := MI_BASEPROPORCIONAL;
                    MI_BASEPROPORCIONAL := MI_BASEPROPORCIONAL + (PCK_NOMINA.FC_CNP(50)+PCK_NOMINA.FC_CN(50)+PCK_NOMINA.FC_CNP(53)+PCK_NOMINA.FC_CN(53)+ PCK_NOMINA.FC_CNP(56)+PCK_NOMINA.FC_CN(56)+ PCK_NOMINA.FC_CNP(51)+PCK_NOMINA.FC_CN(51)+ PCK_NOMINA.FC_CNP(59)+PCK_NOMINA.FC_CN(59)+ PCK_NOMINA.FC_CNP(60)+PCK_NOMINA.FC_CN(60)+ PCK_NOMINA.FC_CNP(49)+PCK_NOMINA.FC_CN(49)+ PCK_NOMINA.FC_CNP(48)+PCK_NOMINA.FC_CN(48)+ PCK_NOMINA.FC_CNP(52)+PCK_NOMINA.FC_CN(52)+ PCK_NOMINA.FC_CNP(518)+PCK_NOMINA.FC_CN(518)+ PCK_NOMINA.FC_CNP(522)+PCK_NOMINA.FC_CN(522)+ PCK_NOMINA.FC_CNP(54)+PCK_NOMINA.FC_CN(54)+ PCK_NOMINA.FC_CNP(57)+PCK_NOMINA.FC_CN(57));
                     MI_VALOR := MI_BASEPROPORCIONAL;
                END IF;

            END IF;
            IF MI_NOCALCULANOVLIQUIFIN AND MI_TI_BASES(i).TIPONOVEDAD <> 4 THEN --(MZANGUNA:26/04/2019) No recalcule la novedad cuando sea periodo liquidaciÃ³n final y novedades diferentes al sueldo.
                MI_BASEPROPORCIONAL := MI_TI_BASES(i).BASEPROPORCIONAL;
            ELSE
              --(jmillan: 16/01/2023 ticket#7716620) se ajusta el valor de los dias en caso de que sea el febrero tomar 30 para el calculo de cocepto 112
                IF  MI_TI_BASES(i).TIPONOVEDAD = 3 AND UN_MES = 2 and MI_TI_BASES(i).DIAS >= 28 THEN
                MI_TI_BASES(i).DIAS := CASE WHEN MI_FEB_SITRABAJO THEN  MI_TI_BASES(i).DIAS ELSE 30 END; --mod JM CC 3612
                MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(MI_BASE / 30 * MI_TI_BASES(i).DIAS , 0); --mod JM CC 3515
                MI_TI_BASES(i).BASEPROPORCIONAL := MI_BASEPROPORCIONAL;
                ELSE
                  --JM CC 661 INI  23/01/2025  default para las novedades de tipo 3 
                  IF  MI_TI_BASES(i).TIPONOVEDAD = 3 THEN  
                      MI_BASEPROPORCIONAL := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                                  (UN_IBC       => (MI_BASE - CASE WHEN PCK_NOMINA.FC_CN(150) > 0 AND PCK_NOMINA.FC_CN(9) <> MI_TI_BASES(i).DIAS THEN PCK_NOMINA.FC_CN(150)  ELSE  0 END) -- CC 2310
                                  ,UN_DIAS      => MI_TI_BASES(i).DIAS
                                  ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                  ,UN_ROUND     => 0);
                   END IF;
                   --JM CC 661 FIN 23/01/2025 
                  -- CC:1031 - NCARDENAS  - FECHA:25/03/2025 - se agrega las licencia de luto,calamidad  y compensacion por horas extras
                  IF MI_TI_BASES(i).TIPONOVEDAD = 3 AND (PCK_NOMINA.FC_CN(11) > 0  AND PCK_NOMINA.FC_CN(9) > 0 AND  (PCK_NOMINA.FC_CN(359)+PCK_NOMINA.FC_CN(365)+PCK_NOMINA.FC_CN(363)+PCK_NOMINA.FC_CN(364)+PCK_NOMINA.FC_CN(390)) > 0 ) THEN --JM 07/10/2024 7800139
                    MI_VALOR:=0;
                                BEGIN
                                    SELECT SUELDOMENSUAL INTO MI_VALOR FROM ENCARGOS
                                    JOIN LICENCIAS
                                    ON   encargos.compania =  licencias.compania
                                    AND  encargos.id_de_empleado = licencias.id_de_empleado
                                    AND  (licencias.FECHA_INICIO between encargos.FECHAINICIO and encargos.FECHAFINAL or licencias.FECHA_FINAL between encargos.FECHAINICIO and encargos.FECHAFINAL)
                                    WHERE encargos.COMPANIA = UN_COMPANIA
                                    AND encargos.ID_DE_EMPLEADO = UN_EMPLEADO
                                    AND licencias.FECHA_INICIO = MI_TI_BASES(i).FECHAINICIAL --JM 16/10/2024
                                    AND licencias.FECHA_FINAL= MI_TI_BASES(i).FECHAFINAL --JM 16/10/2024
                                    --AND encargos.ANO = PCK_NOMINA.GL_SANO --JM 16/10/2024
                                    --AND encargos.MES = PCK_NOMINA.GL_SMES --JM 16/10/2024
                                    AND encargos.PERIODO = PCK_NOMINA.GL_SPER;
                                    EXCEPTION  WHEN NO_DATA_FOUND THEN
                                        MI_VALOR := MI_SALARIO_BASE;
                                END;
                    
                    IF PCK_NOMINA.FC_CN(359) <> 0 THEN              
                        MI_BASEPROPORCIONAL := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                                (UN_IBC       => MI_VALOR--MI_TI_BASES(i).BASE
                                ,UN_DIAS      => MI_TI_BASES(i).DIAS
                                ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                ,UN_ROUND     => 0);
                    ELSE
                        MI_CONCEPTO_EVALUAR :=
                            CASE 
                                WHEN PCK_NOMINA.FC_CN(363) <> 0 THEN 363
                                WHEN PCK_NOMINA.FC_CN(364) <> 0 THEN 364
                                WHEN PCK_NOMINA.FC_CN(365) <> 0 THEN 365
                                WHEN PCK_NOMINA.FC_CN(390) <> 0 THEN 390
                                ELSE 0
                            END;        
                            MI_DIAS_ENCAR_LICENCIA := PCK_NOMINA_PROC01.FC_DIASGEN_ENCARGO(MI_CONCEPTO_EVALUAR);
                            MI_BASEPROPORCIONAL_ENCAR := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                                (UN_IBC       => MI_VALOR--MI_TI_BASES(i).BASE
                                ,UN_DIAS      => MI_DIAS_ENCAR_LICENCIA
                                ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                ,UN_ROUND     => 0);
                            MI_BASEPROPORCIONAL := PCK_NOMINA_COM5.FC_BASEPROPORCIONAL
                                (UN_IBC       => MI_SALARIO_BASE--MI_TI_BASES(i).BASE
                                ,UN_DIAS      => MI_TI_BASES(i).DIAS - MI_DIAS_ENCAR_LICENCIA
                                ,UN_DECIMALES => PCK_NOMINA.GL_RBASE1990
                                ,UN_ROUND     => 0);
                          MI_BASEPROPORCIONAL := PCK_SYSMAN_UTL.FC_ROUND(MI_BASEPROPORCIONAL_ENCAR +  MI_BASEPROPORCIONAL, 0); 
                      
                    END IF;            
                  END IF;

                MI_TI_BASES(i).BASEPROPORCIONAL := MI_BASEPROPORCIONAL;
                END IF;
            END IF;
			--CC_894(07/03/2025 JCROJAS): Validacion para que no vuelva a sumar el concepto 150
            IF PCK_NOMINA.FC_CN(360) >= 30 AND PCK_NOMINA.FC_CN(150) > 0 AND PCK_NOMINA.FC_CN(150) = MI_BASEPROPORCIONAL THEN 
                MI_BASESSUMA112 := MI_BASESSUMA112;
            ELSE
                MI_BASESSUMA112 := MI_BASESSUMA112 + MI_BASEPROPORCIONAL;
            END IF;
            --(APINEDA:26/12/2019)-TAR 1000096419 Valido que si tiene novedad de licencia no remunerada y el tipo de novedad es sueldo, entonces calcule sobre la base proporcional y no sobre el total del concepto 112
            IF NVL(MI_LICENCIA,'') = 'NSLN'  AND MI_TI_BASES(i).TIPONOVEDAD = 4  THEN
                MI_VALOR112OBASEPROPORCIONAL := MI_BASEPROPORCIONAL;
            END IF;
        END LOOP BASESPROPORCIONALES;
        --Hasta aquÃ­ 

        --(APINEDA:26/12/2019)-TAR 1000096419 Si no tenÃ­a novedad de licencia no remunerada entonces debe calcular sobre el concepto 112 
         IF MI_VALOR112OBASEPROPORCIONAL = 0  THEN 
            MI_VALOR112OBASEPROPORCIONAL := MI_BASESSUMA112;
         END IF;

    --(APINEDA:15/05/2020)-TAR 1000098562 Funcionarios con retiro e ingreso en el mismo mes.
    --Se crea el proceso para que cuando un funcionario se retira de la entidad e ingresa nuevamente este acumule los IBCS  para las cotizaciones  del fondo de solidaridad.     
    FOR MI_RSEMPRETIRADOS IN
    ( 
      SELECT ID_DE_EMPLEADO,
            TO_CHAR(FECHA_DE_RETIRO,'MM') MES,
            TO_CHAR(FECHA_DE_RETIRO,'YYYY') ANOR
      FROM PERSONAL
      WHERE NUMERO_DCTO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO
      AND ID_DE_EMPLEADO <> PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
      AND ESTADO_ACTUAL = 3
      AND COMPANIA = UN_COMPANIA -- mod JM CC 3688 se añade compañia porque sino trae de todas las demas y da una suma toda loca xd
    ) LOOP
            SELECT  SUM(BASEPROPORCIONAL)
            INTO MI_BASEPROPORCIONAL_RETIRADO
            FROM BASESNOVEDADES 
            WHERE COMPANIA = UN_COMPANIA 
            AND ANO = UN_ANO 
            AND MES = UN_MES 
            AND PERIODO = PCK_NOMINA.GL_SPER 
            AND ID_DE_EMPLEADO IN(MI_RSEMPRETIRADOS.ID_DE_EMPLEADO);
            --(APINEDA:27/05/2020)-Se valida que la base proporcioanl del retirado no sea nula.
            --(JORDUZ:27/10/2021)- Se valida que efectivamente sea el retiro e ingreso el mismo mes y evitar valores incorrectos para funcioanrios que no cumplen con esta condicion Ticket 7701229
          IF MI_RSEMPRETIRADOS.MES = TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, 'MM') AND MI_RSEMPRETIRADOS.ANOR = TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, 'YYYY') THEN
              MI_BASEPROPORCIONAL_RETIRADO := CASE WHEN MI_BASEPROPORCIONAL_RETIRADO IS NULL THEN 0 ELSE MI_BASEPROPORCIONAL_RETIRADO END;
          ELSE 
              MI_BASEPROPORCIONAL_RETIRADO := 0;
          END IF;
    END LOOP;
    MI_VALOR112OBASEPROPORCIONAL := MI_VALOR112OBASEPROPORCIONAL + MI_BASEPROPORCIONAL_RETIRADO;     

            --(EAMAYA:21/05/2019, Se saca este bloque del ciclo 
            --(MZANGUNA:02/05/2019)-Inicio, Se agrega dentro del ciclo, para que evaluÃ© lÃ­nea por lÃ­nea y tome la base proporcional.
            --(APINEDA:26/12/2019)-TAR 1000096419 Se cambia variable MI_BASESSUMA112 por MI_VALOR112OBASEPROPORCIONAL
            /*
            MI_PORCFSP := CASE WHEN MI_VALOR112OBASEPROPORCIONAL > 4 * PCK_NOMINA.FC_CN(201) THEN 0.5 ELSE 0 END;
            MI_PORCFSPADIC := CASE WHEN MI_VALOR112OBASEPROPORCIONAL > 4 * PCK_NOMINA.FC_CN(201) THEN  0.5 ELSE 0 END;
            MI_PORCFSPSUBSIS := CASE WHEN MI_VALOR112OBASEPROPORCIONAL > 4 * PCK_NOMINA.FC_CN(201) THEN  0.5 ELSE 0 END;
            */
            
            -- TICKET 7726613 ECABRERA: SE ADICIONA EL = A LA CONDICION DE 4 SALARIOS MINIMOS PARA EL FSP Y SE CAMBIO EL 4
            --          POR EL PARAMETRO CORRESPONDIENTE
            MI_PORCFSP := CASE WHEN MI_VALOR112OBASEPROPORCIONAL >= PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) THEN 0.5 ELSE 0 END;
            MI_PORCFSPADIC := CASE WHEN MI_VALOR112OBASEPROPORCIONAL >= PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) THEN  0.5 ELSE 0 END;
            MI_PORCFSPSUBSIS := CASE WHEN MI_VALOR112OBASEPROPORCIONAL >=  PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) THEN  0.5 ELSE 0 END;
            -- TICKET 7726613 ECABRERA FIN --

            IF MI_VALOR112OBASEPROPORCIONAL > (16 * PCK_NOMINA.FC_CN(201)) THEN
                IF MI_VALOR112OBASEPROPORCIONAL > (16 * PCK_NOMINA.FC_CN(201)) AND MI_VALOR112OBASEPROPORCIONAL <= (17 * PCK_NOMINA.FC_CN(201)) THEN
                    MI_PORCFSPADIC := MI_PORCFSPADIC + 0.2;
                ELSIF MI_VALOR112OBASEPROPORCIONAL > (17 * PCK_NOMINA.FC_CN(201)) AND MI_VALOR112OBASEPROPORCIONAL <= (18 * PCK_NOMINA.FC_CN(201)) THEN
                    MI_PORCFSPADIC := MI_PORCFSPADIC + 0.4;
                ELSIF MI_VALOR112OBASEPROPORCIONAL > (18 * PCK_NOMINA.FC_CN(201)) AND MI_VALOR112OBASEPROPORCIONAL <= (19 * PCK_NOMINA.FC_CN(201)) THEN
                    MI_PORCFSPADIC := MI_PORCFSPADIC + 0.6;
                ELSIF MI_VALOR112OBASEPROPORCIONAL > (19 * PCK_NOMINA.FC_CN(201)) AND MI_VALOR112OBASEPROPORCIONAL <= (20 * PCK_NOMINA.FC_CN(201)) THEN
                    MI_PORCFSPADIC := MI_PORCFSPADIC + 0.8;
                ELSIF MI_VALOR112OBASEPROPORCIONAL > (20 * PCK_NOMINA.FC_CN(201)) THEN
                    MI_PORCFSPADIC := MI_PORCFSPADIC + 1;
                END IF;
            END IF;
            --(MZANGUNA:02/05/2019)-Fin, Se agrega dentro del ciclo, para que evaluÃ© lÃ­nea por lÃ­nea y tome la base proporcional.    
            
            IF (PCK_NOMINA.GL_SANO || PCK_NOMINA.GL_SMES >= '202507' AND MI_VALOR112OBASEPROPORCIONAL >= (4 * PCK_NOMINA.FC_CN(201)) AND PCK_PARST.FC_PAR('APLICAR REFORMA PENSIONAL ADICIONAL FSPYFS','NO') = 'SI' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN_TRANSICION = 0) THEN
              MI_PORCFSPADIC := MI_PORCFSPADIC + PCK_NOMINA_SEGSOCI.FC_FSP_ADI_REFORMA(UN_COMPANIA, PCK_NOMINA.GL_SANO, 'A', MI_VALOR112OBASEPROPORCIONAL );
             END IF;

    --(APINEDA:15/05/2020)-TAR 1000098562 Funcionarios con retiro e ingreso en el mismo mes.
    --Se crea el proceso para que cuando un funcionario se retira de la entidad e ingresa nuevamente este acumule los IBCS  para las cotizaciones  del fondo de solidaridad.
    IF MI_BASEPROPORCIONAL_RETIRADO > 0 THEN         
      FOR RSRETIRADOS IN
      ( 
                SELECT B.COMPANIA, B.LLAVENOVEDAD, B.TIPONOVEDAD, B.CONSECUTIVO, B.ID_DE_EMPLEADO, B.ANO, B.MES, B.PERIODO, B.BASEPROPORCIONAL
                FROM PERSONAL P INNER JOIN BASESNOVEDADES B
                ON (P.COMPANIA = B.COMPANIA)
                AND (P.ID_DE_EMPLEADO = B.ID_DE_EMPLEADO)
                WHERE P.COMPANIA = UN_COMPANIA 
                AND B.ANO = UN_ANO 
                AND B.MES = UN_MES 
                AND B.PERIODO = PCK_NOMINA.GL_SPER 
                AND P.NUMERO_DCTO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO
                AND P.ID_DE_EMPLEADO <> PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                AND P.ESTADO_ACTUAL = 3                
      ) LOOP
        IF RSRETIRADOS.TIPONOVEDAD = 3 THEN 
          MI_LICENCIA_RETIRADO := SUBSTR(RSRETIRADOS.LLAVENOVEDAD, INSTR(RSRETIRADOS.LLAVENOVEDAD,'--',1,4) + 2, LENGTH(RSRETIRADOS.LLAVENOVEDAD));
          MI_LICENCIA_RETIRADO := CASE WHEN MI_LICENCIA_RETIRADO IN('01','02','03')
                      THEN 'NSLN' 
                      ELSE CASE WHEN MI_LICENCIA_RETIRADO IN('04','05','10','12')
                        THEN 'LREM' 
                        ELSE 'COMIS' 
                        END 
                      END;
        ELSE 
          MI_LICENCIA_RETIRADO := ' ';
        END IF;
        MI_PORC_FSP_RETIRADO := CASE RSRETIRADOS.TIPONOVEDAD
                    WHEN 3 THEN (CASE WHEN MI_LICENCIA_RETIRADO = 'LREM' THEN MI_PORCFSP ELSE 0 END)
                    WHEN 5 THEN 0 
                    ELSE MI_PORCFSP 
                    END;
        MI_FSP_RETIRADO := CASE WHEN RSRETIRADOS.TIPONOVEDAD = 5 THEN 
                  PCK_SYSMAN_UTL.FC_ROUND_100(RSRETIRADOS.BASEPROPORCIONAL  * MI_PORC_FSP_RETIRADO / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990) 
                   ELSE 
                  PCK_SYSMAN_UTL.FC_ROUND_100(RSRETIRADOS.BASEPROPORCIONAL  * MI_PORC_FSP_RETIRADO / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                   END;
        MI_PORCFSPSUBSIS_RETIRADO := CASE RSRETIRADOS.TIPONOVEDAD
                        WHEN 3 THEN (CASE WHEN MI_LICENCIA_RETIRADO = 'LREM' THEN MI_PORCFSPSUBSIS ELSE 0 END)
                        WHEN 5 THEN 0
                        ELSE MI_PORCFSPSUBSIS 
                       END;     
        MI_FSPSUBSISTENCIA_RETIRADO :=  CASE WHEN RSRETIRADOS.TIPONOVEDAD = 5 THEN
                          PCK_SYSMAN_UTL.FC_ROUND_100(RSRETIRADOS.BASEPROPORCIONAL  * MI_PORCFSPSUBSIS_RETIRADO / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                        ELSE  
                          PCK_SYSMAN_UTL.FC_ROUND_100(RSRETIRADOS.BASEPROPORCIONAL  * MI_PORCFSPSUBSIS_RETIRADO / 100  , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                        END;
        --JM 7737828  se agrega a la validacion los SUBTIPOCOTIZANTES 3 y 5--MOD JM CC 2838 tambien el 6
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SUBTIPOCOTIZANTE = 6 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SUBTIPOCOTIZANTE = 4 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SUBTIPOCOTIZANTE = 3 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SUBTIPOCOTIZANTE = 5 OR  (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).TIPOVINCULACION IN (12,19)) THEN
          MI_FSP_RETIRADO := 0;
          MI_FSPSUBSISTENCIA_RETIRADO := 0;
        END IF;         

        BEGIN
          MI_CAMPOS := ' FSP = '|| MI_FSP_RETIRADO ||'
                  ,PORC_FSP = '|| MI_PORC_FSP_RETIRADO ||'
                  ,FSPSUBSISTENCIA = '|| MI_FSPSUBSISTENCIA_RETIRADO ||'';
          MI_CONDICION := ' COMPANIA       = '''|| RSRETIRADOS.COMPANIA ||'''
                  AND LLAVENOVEDAD   = '''|| RSRETIRADOS.LLAVENOVEDAD ||'''
                  AND TIPONOVEDAD    = '|| RSRETIRADOS.TIPONOVEDAD ||'
                  AND CONSECUTIVO    = '|| RSRETIRADOS.CONSECUTIVO ||'
                  AND ID_DE_EMPLEADO = '|| RSRETIRADOS.ID_DE_EMPLEADO ||'
                  AND ANO            = '|| RSRETIRADOS.ANO ||'
                  AND MES            = '|| RSRETIRADOS.MES ||'
                                  AND PERIODO        = '|| RSRETIRADOS.PERIODO ||'';                

          IF NOT (PCK_NOMINA.GL_ESBONIFICACION) THEN
            BEGIN
              MI_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'BASESNOVEDADES'
                           ,UN_ACCION    => 'M'
                           ,UN_CAMPOS    => MI_CAMPOS
                           ,UN_CONDICION => MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;
          END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
          MI_MSGERROR (1).CLAVE := 'EMPLEADO';
          MI_MSGERROR (1).VALOR :=  RSRETIRADOS.ID_DE_EMPLEADO ;
          PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_BASESNOVEDADESEMPRETIRADO,
              UN_REEMPLAZOS => MI_MSGERROR);
        END;                                
      END LOOP;
    END IF;


        <<INSERTBASES>>
        FOR i IN MI_TI_BASES.FIRST .. MI_TI_BASES.LAST
        LOOP
            MI_CONSECUTIVO :=  MI_TI_BASES(i).CONSECUTIVO;

            --(MZANGUNA:24/11/2018)-Licencias, remuneradas y no remuneradas
            IF MI_TI_BASES(i).TIPONOVEDAD = 3 THEN   --Licencias
                --(MZANGUNA:28/11/2018)-Ajuste tipos de licencia.
                MI_LICENCIA := SUBSTR(MI_TI_BASES(i).LLAVENOVEDAD, INSTR(MI_TI_BASES(i).LLAVENOVEDAD,'--',1,4) + 2, LENGTH(MI_TI_BASES(i).LLAVENOVEDAD));
                MI_LICENCIA := CASE WHEN MI_LICENCIA IN('01','02','03')
                                  THEN 'NSLN'
                                  ELSE CASE WHEN MI_LICENCIA IN('04','05','07','10','12')--(CC:3148_CFBARRERA_Se agrega la licencia por luto)
                                       THEN 'LREM'
                                       ELSE 'COMIS'
                                       END
                                  END;
            ELSE
                MI_LICENCIA := ' ';
            END IF;
			
			--INI TICKET 7743926(30/04/2024 JCROJAS)
            IF MI_TI_BASES(i).TIPONOVEDAD = 2 THEN
                MI_INCAPACIDAD := SUBSTR(MI_TI_BASES(i).LLAVENOVEDAD, INSTR(MI_TI_BASES(i).LLAVENOVEDAD,'--',1,3) + 2, INSTR(MI_TI_BASES(i).LLAVENOVEDAD,'--',1,4) - 2 - INSTR(MI_TI_BASES(i).LLAVENOVEDAD,'--',1,3) );
            ELSE
                MI_INCAPACIDAD := ' ';
            END IF;
            --FIN TICKET 7743926(30/04/2024 JCROJAS)

            --(APINEDA:22/11/2019)- TAR 1000095489 si el empleado es de tipo 95 (bomberos) y tiene Colpensiones
            MI_PORC_PATRON_AFP := PCK_PARENTR.PARAMETRO35;
            MI_PORC_TOTAL_AFP  := PCK_PARENTR.PARAMETRO39;
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '95' AND PCK_NOMINA_COM9.FC_OBTENERFONDOPORCODAIFP(PCK_NOMINA.GL_COMPANIA, '25-14') = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DEL_FONDO THEN
                 MI_PORC_TOTAL_AFP := TO_NUMBER(PCK_PARST.FC_PAR('PORCENTAJE APORTES PENSION BOMBEROS','0'));
                 MI_PORC_PATRON_AFP := MI_PORC_TOTAL_AFP - PCK_PARENTR.PARAMETRO36;
            END IF;
            --(APINEDA:22/11/2019)- TAR 1000095489 reemplazan parametros por variables MI_PORC_PATRON_AFP y MI_PORC_TOTAL_AFP para el porcentaje de pension total
            MI_TI_BASES(i).PORC_PENSIONTOTAL := CASE MI_TI_BASES(i).TIPONOVEDAD
                WHEN 3 THEN (CASE WHEN PCK_PARST.FC_PAR('REALIZAR APORTES PENSION EN LNR', ' ') = 'SI' THEN (CASE WHEN PCK_PARST.FC_PAR('REALIZAR APORTES PENSION EN LNR SOLO PATRONAL', 'NO') = 'SI' THEN MI_PORC_PATRON_AFP ELSE MI_PORC_TOTAL_AFP END) ELSE 0 END)
                WHEN 5 THEN 0
                ELSE MI_PORC_TOTAL_AFP END;

            MI_TI_BASES(i).PORC_SALUDTOTAL := CASE MI_TI_BASES(i).TIPONOVEDAD
                WHEN 3 THEN (CASE 
                    WHEN MI_APORTESOLOPATRON THEN 
                        PCK_PARENTR.PARAMETRO40 
                    ELSE 
                        CASE --(CC:2913_CFBARRERA)
                            WHEN PCK_PARST.FC_PAR('NOMINA MENSUAL', 'SI') = 'NO'
                                AND PCK_PARST.FC_PAR('APORTAR PENSION EN LNR', 'SI') = 'SI'
                                AND PCK_PARST.FC_PAR('MOSTRAR SEGURIDAD SOCIAL EN PRIMERA QUINCENA', 'SI') = 'NO'
                                AND PCK_PARST.FC_PAR('REALIZAR APORTES PENSION EN LNR SOLO PATRONAL', 'SI') = 'NO' 
                            THEN 
                                PCK_PARENTR.PARAMETRO40 
                            ELSE 
                                PCK_PARENTR.PARAMETRO43  
                        END
                END)
                WHEN 5 THEN 0
                ELSE PCK_NOMINA.GL_PORCSALUDCONDECRETO1122 END;
            --(APINEDA:21/03/2019)-Se agrega validaciÃ³n para no poner valor de MI_PORCFSP en 0 cuando la novedad corresponde a una licencia remunerada.
            MI_TI_BASES(i).PORC_FSP := CASE MI_TI_BASES(i).TIPONOVEDAD
                WHEN 3 THEN (CASE WHEN MI_LICENCIA = 'LREM' THEN MI_PORCFSP ELSE 0 END)
                WHEN 5 THEN 0
                ELSE MI_PORCFSP END;
            
			--CC_4385(17/06/2026 JCROJAS): Se agrega validacion para que, cuando se cumplan estas condiciones, no se comparen los salarios minimos con el concepto 2, sino con la base correspondiente al mes completo.
            IF PCK_NOMINA.FC_CNAN(11) >= 30 AND PCK_NOMINA.FC_CNAN(10) > PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) AND PCK_NOMINA.FC_CNAN(339) > 0 THEN
                MI_TI_BASES(i).PORC_FSP := CASE WHEN MI_TI_BASES(i).TIPONOVEDAD NOT IN (3,5) AND  (PCK_NOMINA.FC_CN(359)+PCK_NOMINA.FC_CN(339)) > 0 AND ((MI_BASE/(PCK_NOMINA.FC_CN(4)+PCK_NOMINA.FC_CNP(4)))*(PCK_NOMINA.FC_CN(9)+PCK_NOMINA.FC_CNP(9)+PCK_NOMINA.FC_CN(11)+PCK_NOMINA.FC_CNP(11)+PCK_NOMINA.FC_CN(339)+PCK_NOMINA.FC_CNP(339))) < PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) THEN 0 ELSE  MI_TI_BASES(i).PORC_FSP END;
            ELSE	
				--JM CC 3066 validar si efectivamente aplica la proporcion cuando hay comision 
				MI_TI_BASES(i).PORC_FSP := CASE WHEN MI_TI_BASES(i).TIPONOVEDAD NOT IN (3,5) AND  (PCK_NOMINA.FC_CN(359)+PCK_NOMINA.FC_CN(339)) > 0 AND PCK_NOMINA.FC_CN(2) < PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) THEN 0 ELSE  MI_TI_BASES(i).PORC_FSP END;
			END IF;	   

            --(APINEDA:21/03/2019)-Se agrega validaciÃ³n para poner valor de MI_PORCFSPSUBSIS en 0 cuando la novedad corresponde a una licencia por comision.
            --(APINEDA:26/12/2019)-Se cambia asignaciÃ³n a variable MI_PORCFSPSUBSIS por campo PORC_FSP_SUBSISTENCIA TAR 1000096419
            MI_TI_BASES(i).PORC_FSP_SUBSISTENCIA := CASE MI_TI_BASES(i).TIPONOVEDAD
                WHEN 3 THEN (CASE WHEN MI_LICENCIA = 'LREM' THEN MI_PORCFSPSUBSIS ELSE 0 END)
                WHEN 5 THEN 0
                ELSE MI_PORCFSPSUBSIS END;

			--CC_4385(17/06/2026 JCROJAS): Se agrega validacion para que, cuando se cumplan estas condiciones, no se comparen los salarios minimos con el concepto 2, sino con la base correspondiente al mes completo.
            IF PCK_NOMINA.FC_CNAN(11) >= 30 AND PCK_NOMINA.FC_CNAN(10) > PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) AND PCK_NOMINA.FC_CNAN(339) > 0 THEN
                MI_TI_BASES(i).PORC_FSP_SUBSISTENCIA := CASE WHEN MI_TI_BASES(i).TIPONOVEDAD NOT IN (3,5) AND  (PCK_NOMINA.FC_CN(359)+PCK_NOMINA.FC_CN(339)) > 0 AND ((MI_BASE/(PCK_NOMINA.FC_CN(4)+PCK_NOMINA.FC_CNP(4)))*(PCK_NOMINA.FC_CN(9)+PCK_NOMINA.FC_CNP(9)+PCK_NOMINA.FC_CN(11)+PCK_NOMINA.FC_CNP(11)+PCK_NOMINA.FC_CN(339)+PCK_NOMINA.FC_CNP(339))) < PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) THEN 0 ELSE  MI_TI_BASES(i).PORC_FSP_SUBSISTENCIA END;
            ELSE	
				--JM CC 3066 validar si efectivamente aplica la proporcion cuando hay comision 
				MI_TI_BASES(i).PORC_FSP_SUBSISTENCIA := CASE WHEN MI_TI_BASES(i).TIPONOVEDAD NOT IN (3,5) AND  (PCK_NOMINA.FC_CN(359)+PCK_NOMINA.FC_CN(339)) > 0 AND PCK_NOMINA.FC_CN(2) < PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) THEN 0 ELSE  MI_TI_BASES(i).PORC_FSP_SUBSISTENCIA END;
			END IF;	   
           
            --(APINEDA:21/03/2019)-Se agrega validaciÃ³n para no poner valor de MI_PORCFSPADIC en 0 cuando la novedad corresponde a una licencia remunerada.
            MI_TI_BASES(i).PORC_FSP_ADICIONAL := CASE MI_TI_BASES(i).TIPONOVEDAD
                WHEN 3 THEN (CASE WHEN MI_LICENCIA = 'LREM' THEN MI_PORCFSPADIC ELSE 0 END)
                WHEN 5 THEN 0
                ELSE MI_PORCFSPADIC  END;

			--CC_4385(17/06/2026 JCROJAS): Se agrega validacion para que, cuando se cumplan estas condiciones, no se comparen los salarios minimos con el concepto 2, sino con la base correspondiente al mes completo.
            IF PCK_NOMINA.FC_CNAN(11) >= 30 AND PCK_NOMINA.FC_CNAN(10) > PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) AND PCK_NOMINA.FC_CNAN(339) > 0 THEN
                MI_TI_BASES(i).PORC_FSP_ADICIONAL := CASE WHEN MI_TI_BASES(i).TIPONOVEDAD NOT IN (3,5) AND  (PCK_NOMINA.FC_CN(359)+PCK_NOMINA.FC_CN(339)) > 0 AND ((MI_BASE/(PCK_NOMINA.FC_CN(4)+PCK_NOMINA.FC_CNP(4)))*(PCK_NOMINA.FC_CN(9)+PCK_NOMINA.FC_CNP(9)+PCK_NOMINA.FC_CN(11)+PCK_NOMINA.FC_CNP(11)+PCK_NOMINA.FC_CN(339)+PCK_NOMINA.FC_CNP(339))) < PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) THEN 0 ELSE  MI_TI_BASES(i).PORC_FSP_ADICIONAL END;
            ELSE	
				--JM CC 3066 validar si efectivamente aplica la proporcion cuando hay comision 
				MI_TI_BASES(i).PORC_FSP_ADICIONAL := CASE WHEN MI_TI_BASES(i).TIPONOVEDAD NOT IN (3,5) AND  (PCK_NOMINA.FC_CN(359)+PCK_NOMINA.FC_CN(339)) > 0 AND PCK_NOMINA.FC_CN(2) < PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) THEN 0 ELSE  MI_TI_BASES(i).PORC_FSP_ADICIONAL END;
			END IF;	   
           
            --(MZANGUNA:24/11/2018)-Licencias, remuneradas y no remuneradas
            MI_TI_BASES(i).PORC_PENSIONE := CASE MI_TI_BASES(i).TIPONOVEDAD
                WHEN 3 THEN CASE WHEN MI_LICENCIA = 'LREM' THEN  PCK_PARENTR.PARAMETRO36 ELSE  0 END
                WHEN 5 THEN 0
				--TICKET 7743926(30/04/2024 JCROJAS)
                WHEN 2 THEN CASE WHEN MI_INCAPACIDAD = '11' THEN 0 ELSE PCK_PARENTR.PARAMETRO36 END
                ELSE PCK_PARENTR.PARAMETRO36 END;

            --(APINEDA:21/03/2019)-Se agrega validaciÃ³n para poner valor de PORC_PENSIONP en 0 cuando la novedad corresponde a una licencia por comision.
            --(APINEDA:22/11/2019)- TAR 1000095489 reemplazan parametros por variables MI_PORC_PATRON_AFP y MI_PORC_TOTAL_AFP para el porcentaje de aporte patronal pension
            MI_TI_BASES(i).PORC_PENSIONP := CASE MI_TI_BASES(i).TIPONOVEDAD
                WHEN 3 THEN CASE WHEN MI_LICENCIA = 'COMIS' THEN 0 ELSE
                    (CASE WHEN PCK_PARST.FC_PAR('REALIZAR APORTES PENSION EN LNR', ' ') = 'SI' THEN
                        (CASE WHEN PCK_PARST.FC_PAR('REALIZAR APORTES PENSION EN LNR SOLO PATRONAL', 'NO') = 'SI' THEN MI_PORC_PATRON_AFP ELSE MI_PORC_TOTAL_AFP END)
                    ELSE 0 END) END
                WHEN 5 THEN 0
				--CC_894(07/03/2025 JCROJAS): Cuando el concepto 360 >= 30 tome la variable MI_PORC_TOTAL_AFP
                WHEN 2 THEN CASE WHEN MI_INCAPACIDAD = '11' AND PCK_NOMINA.FC_CN(360) >= 30 THEN MI_PORC_TOTAL_AFP ELSE MI_PORC_PATRON_AFP END																							 
                ELSE MI_PORC_PATRON_AFP END;

            --(MZANGUNA:24/11/2018)-Licencias, remuneradas y no remuneradas
            MI_TI_BASES(i).PORC_SALUDE := CASE MI_TI_BASES(i).TIPONOVEDAD
                WHEN 3 THEN CASE WHEN MI_LICENCIA = 'LREM' THEN  PCK_PARENTR.PARAMETRO41 ELSE  0 END
                WHEN 5 THEN 0
				--TICKET 7743926(30/04/2024 JCROJAS)
                WHEN 2 THEN CASE WHEN MI_INCAPACIDAD = '11'  AND PCK_PARENTR.PARAMETRO70 <> 'S' THEN 0 ELSE PCK_PARENTR.PARAMETRO41 END --JM CC 3771
                ELSE PCK_PARENTR.PARAMETRO41 END;

            --(APINEDA:21/03/2019)-Se agrega validaciÃ³n para poner valor de PORC_SALUDP en 0 cuando la novedad corresponde a una licencia por comision.
            MI_TI_BASES(i).PORC_SALUDP := CASE MI_TI_BASES(i).TIPONOVEDAD
                WHEN 3 THEN CASE WHEN MI_LICENCIA = 'COMIS' THEN 0 ELSE PCK_PARENTR.PARAMETRO40 END
                WHEN 5 THEN 0
				--CC_894(07/03/2025 JCROJAS): Cuando el concepto 360 >= 30 tome el parametro PCK_PARENTR.PARAMETRO43
                WHEN 2 THEN CASE WHEN MI_INCAPACIDAD = '11' AND PCK_NOMINA.FC_CN(360) >= 30 THEN PCK_PARENTR.PARAMETRO43 ELSE PCK_PARENTR.PARAMETRO40 END																									
                ELSE PCK_PARENTR.PARAMETRO40 END;

            --MZANGUNA
            MI_TI_BASES(i).PENSION_TOTAL := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_TI_BASES(i).TIPONOVEDAD = 5
                THEN PCK_SYSMAN_UTL.FC_ROUND_100(0 * MI_TI_BASES(i).PORC_PENSIONTOTAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                ELSE PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_PENSIONTOTAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                END, PCK_NOMINA.GL_RAPORTES1990);

            --(EAMAYA:16/05/2019)-Se adiciona validacion para cuando el SUBTIPOCOTIZANTE sea 4
            --(APINEDA:30/05/2019)-Se traslada validaciÃ³n de subtipocotizante 4 a un solo lugar
            --IF ( PCK_PARST.FC_PAR('REDONDEAR SEGURIDAD SOCIAL', 'NO') = 'SI') THEN -- TICKET 7749611 EFCM: PARA CORPOBOYACA NO SE REALIZAN REDONDEOS EN APORTES 
            IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'REDONDEAR SEGURIDAD SOCIAL',6,SYSDATE),'SI') = 'SI' THEN --JM CC 2821
              MI_TI_BASES(i).APORTEEMPLEADOPENSION := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_TI_BASES(i).TIPONOVEDAD = 5
                	THEN PCK_SYSMAN_UTL.FC_ROUND_100(0 * MI_TI_BASES(i).PORC_PENSIONE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                	ELSE PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_PENSIONE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                	END, PCK_NOMINA.GL_RAPORTES1990);
              ELSE 
                MI_TI_BASES(i).APORTEEMPLEADOPENSION := CASE WHEN MI_TI_BASES(i).TIPONOVEDAD = 5
                  THEN PCK_SYSMAN_UTL.FC_ROUND(0 * MI_TI_BASES(i).PORC_PENSIONE / 100,0)
                  ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_PENSIONE / 100,0)
                  END;
             END IF;--LVEGA 23/12/2025 CC 3044

            IF (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESCALAFON = '10' AND PCK_NOMINA.CPARENTRADA(1).NIT =  '860061110')  AND PCK_NOMINA.GL_SPER NOT IN (1,2) THEN  --JM CC 3224
                 MI_TI_BASES(i).APORTEEMPLEADOPENSION := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_TI_BASES(i).TIPONOVEDAD = 5
                  THEN PCK_SYSMAN_UTL.FC_ROUND_100(0 * MI_TI_BASES(i).PORC_PENSIONE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                  ELSE PCK_SYSMAN_UTL.FC_ROUND_100(MI_BASE * MI_TI_BASES(i).PORC_PENSIONE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                  END, PCK_NOMINA.GL_RAPORTES1990);
            END IF;
           /* ELSE
            	MI_TI_BASES(i).APORTEEMPLEADOPENSION := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_TI_BASES(i).TIPONOVEDAD = 5
                	THEN 0 * MI_TI_BASES(i).PORC_PENSIONE / 100
                	ELSE MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_PENSIONE / 100
                	END, -1);
            END IF; */ -- COMENTADO POR JM 7752194  18/09/2024 
            

            --(MZANGUNA:15/03/2019)-Ajuste de pensiÃ³n en licencias no remuneradas
            MI_APORTESOLOPATRON := (CASE WHEN PCK_PARST.FC_PAR('REALIZAR APORTES PENSION EN LNR', ' ') = 'SI' THEN (CASE WHEN PCK_PARST.FC_PAR('REALIZAR APORTES PENSION EN LNR SOLO PATRONAL', 'NO') = 'SI' THEN TRUE ELSE FALSE END) ELSE FALSE END);

            --(EAMAYA:16/05/2019)-Se adiciona validacion para cuando el SUBTIPOCOTIZANTE sea 4
            --(EAMAYA:17/05/2019)-Se elimina el valor PCK_NOMINA.GL_SUMARSS1990 de los redondeos 
            --(APINEDA:30/05/2019)-Se traslada validaciÃ³n de subtipocotizante 4 a un solo lugar
            --(APINEDA:19/12/2019)-Se agrega SUMARSS1990 a sentencia del tipo de novedad 3 (licencia no remunerada) TAR 1000095980
            --(MZANGUNA:30/12/2019)-Se condiciona el aporte de pension patronal de licencias remuneradas
            IF (MI_TI_BASES(i).TIPONOVEDAD = 3 AND MI_LICENCIA = 'LREM') THEN
                MI_VALAPORTEPENSION := PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * (MI_PORC_TOTAL_AFP) / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                                - (PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_PENSIONE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990));
                MI_TI_BASES(i).APORTEPATRONALPENSION := MI_VALAPORTEPENSION;
            ELSE
                MI_TI_BASES(i).APORTEPATRONALPENSION := PCK_SYSMAN_UTL.FC_ROUND(CASE MI_TI_BASES(i).TIPONOVEDAD
                    WHEN 5 THEN PCK_SYSMAN_UTL.FC_ROUND_100(0 * MI_TI_BASES(i).PORC_PENSIONTOTAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990) - PCK_SYSMAN_UTL.FC_ROUND_100(0 * MI_TI_BASES(i).PORC_PENSIONE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                    WHEN 3 THEN PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * (CASE WHEN MI_APORTESOLOPATRON THEN MI_TI_BASES(i).PORC_PENSIONP ELSE MI_TI_BASES(i).PORC_PENSIONTOTAL END) / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                                - (CASE WHEN MI_APORTESOLOPATRON THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_PENSIONE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990) END)
                    --TICKET 7743926(30/04/2024 JCROJAS)
                    WHEN 2 THEN CASE WHEN MI_INCAPACIDAD = '11' 
									--CC_894(07/03/2025 JCROJAS): Cuando el concepto 360 >= 30 multiplique por MI_TI_BASES(i).PORC_PENSIONTOTAL						   
                                    THEN PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASEPROPORCIONAL * (CASE WHEN PCK_NOMINA.FC_CN(360) >= 30 THEN MI_TI_BASES(i).PORC_PENSIONTOTAL ELSE CASE WHEN MI_APORTESOLOPATRON THEN MI_TI_BASES(i).PORC_PENSIONP ELSE MI_TI_BASES(i).PORC_PENSIONTOTAL END END) / 100 + PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990)
                                         - (CASE WHEN MI_APORTESOLOPATRON THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_PENSIONE / 100 + PCK_NOMINA.GL_SUMARSS1990 , PCK_NOMINA.GL_RAPORTES1990) END)
                                    ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_PENSIONTOTAL / 100 + PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990) - PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_PENSIONE / 100 + PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990)     
                                END    
					ELSE 
                       CASE WHEN  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'REDONDEAR SEGURIDAD SOCIAL',6,SYSDATE),'SI') = 'SI' THEN 
                                PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_PENSIONTOTAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)  - PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_PENSIONE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                            ELSE
                                PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_PENSIONTOTAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)  - PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_PENSIONE / 100 ,0)
                          END--LVEGA 23/12/2025 CC 3044
                    END, PCK_NOMINA.GL_RAPORTES1990);
            END IF;


          IF (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESCALAFON = '10' AND PCK_NOMINA.CPARENTRADA(1).NIT =  '860061110')  AND PCK_NOMINA.GL_SPER NOT IN (1,2)  THEN -- JM CC 3224
                
                    IF (MI_TI_BASES(i).TIPONOVEDAD = 3 AND MI_LICENCIA = 'LREM') THEN
                        MI_VALAPORTEPENSION := PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * (MI_PORC_TOTAL_AFP) / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                                        - (PCK_SYSMAN_UTL.FC_ROUND_100(MI_BASE * MI_TI_BASES(i).PORC_PENSIONE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990));
                        MI_TI_BASES(i).APORTEPATRONALPENSION := MI_VALAPORTEPENSION;
                    ELSE
                        MI_TI_BASES(i).APORTEPATRONALPENSION := PCK_SYSMAN_UTL.FC_ROUND(CASE MI_TI_BASES(i).TIPONOVEDAD
                            WHEN 5 THEN PCK_SYSMAN_UTL.FC_ROUND_100(0 * MI_TI_BASES(i).PORC_PENSIONTOTAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990) - PCK_SYSMAN_UTL.FC_ROUND_100(0 * MI_TI_BASES(i).PORC_PENSIONE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                            WHEN 3 THEN PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * (CASE WHEN MI_APORTESOLOPATRON THEN MI_TI_BASES(i).PORC_PENSIONP ELSE MI_TI_BASES(i).PORC_PENSIONTOTAL END) / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                                        - (CASE WHEN MI_APORTESOLOPATRON THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND_100(MI_BASE * MI_TI_BASES(i).PORC_PENSIONE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990) END)
                            --TICKET 7743926(30/04/2024 JCROJAS)
                            WHEN 2 THEN CASE WHEN MI_INCAPACIDAD = '11'
                          --CC_894(07/03/2025 JCROJAS): Cuando el concepto 360 >= 30 multiplique por MI_TI_BASES(i).PORC_PENSIONTOTAL
                                            THEN PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASEPROPORCIONAL * (CASE WHEN PCK_NOMINA.FC_CN(360) >= 30 THEN MI_TI_BASES(i).PORC_PENSIONTOTAL ELSE CASE WHEN MI_APORTESOLOPATRON THEN MI_TI_BASES(i).PORC_PENSIONP ELSE MI_TI_BASES(i).PORC_PENSIONTOTAL END END) / 100 + PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990)
                                                 - (CASE WHEN MI_APORTESOLOPATRON THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_BASE * MI_TI_BASES(i).PORC_PENSIONE / 100 + PCK_NOMINA.GL_SUMARSS1990 , PCK_NOMINA.GL_RAPORTES1990) END)
                                            ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_PENSIONTOTAL / 100 + PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990) - PCK_SYSMAN_UTL.FC_ROUND(MI_BASE * MI_TI_BASES(i).PORC_PENSIONE / 100 + PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990)
                                        END
                    ELSE PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_PENSIONTOTAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)  - PCK_SYSMAN_UTL.FC_ROUND_100(MI_BASE * MI_TI_BASES(i).PORC_PENSIONE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                            END, PCK_NOMINA.GL_RAPORTES1990);
                    END IF;
            
            END IF;
            
            --(EAMAYA:17/05/2019)-Se elimina el valor PCK_NOMINA.GL_SUMARSS1990 de los redondeos 

            MI_TI_BASES(i).SALUD_TOTAL := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_TI_BASES(i).TIPONOVEDAD = 5
                THEN PCK_SYSMAN_UTL.FC_ROUND_100(0 * MI_TI_BASES(i).PORC_SALUDTOTAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990) 
                ELSE PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDTOTAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990) 
                END, PCK_NOMINA.GL_RAPORTES1990);
			
			--IF ( PCK_PARST.FC_PAR('REDONDEAR SEGURIDAD SOCIAL', 'NO') = 'SI') THEN  -- TICKET 7749611 EFCM: PARA CORPOBOYACA NO SE REALIZAN REDONDEOS EN APORTES
            IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'REDONDEAR SEGURIDAD SOCIAL',6,SYSDATE),'SI') = 'SI' THEN --JM CC 2821	
              MI_TI_BASES(i).APORTEEMPLEADOSALUD := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_TI_BASES(i).TIPONOVEDAD = 5
                	THEN PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                	ELSE  PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                	END, PCK_NOMINA.GL_RAPORTES1990);
              ELSE
                MI_TI_BASES(i).APORTEEMPLEADOSALUD := (CASE WHEN MI_TI_BASES(i).TIPONOVEDAD = 5
                  THEN PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDE / 100 ,0)
                  ELSE  PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDE / 100 ,0)
                  END);--LVEGA 23/12/2025 CC 3044
             END IF;

             IF (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESCALAFON = '10' AND PCK_NOMINA.CPARENTRADA(1).NIT =  '860061110')  AND PCK_NOMINA.GL_SPER NOT IN (1,2) THEN 
                MI_TI_BASES(i).APORTEEMPLEADOSALUD := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_TI_BASES(i).TIPONOVEDAD = 5
                  THEN PCK_SYSMAN_UTL.FC_ROUND_100(MI_BASE* MI_TI_BASES(i).PORC_SALUDE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                  ELSE  PCK_SYSMAN_UTL.FC_ROUND_100(MI_BASE * MI_TI_BASES(i).PORC_SALUDE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                  END, PCK_NOMINA.GL_RAPORTES1990);
            END IF;
            /* ELSE
            	MI_TI_BASES(i).APORTEEMPLEADOSALUD := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_TI_BASES(i).TIPONOVEDAD = 5
                	THEN MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDE / 100
                	ELSE MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDE / 100
                	END, -1);
            END IF; */ ---- COMENTADO POR JM 7752194  18/09/2024 
            --JM CC 763 INI 
            IF PCK_PARST.FC_PAR('APLICA EXONERACION EN BASE TOTAL INGRESOS 097', '0') = 'SI'  AND PCK_PARENTR.PARAMETRO70 = 'S' AND PCK_NOMINA.FC_CN(360) >= 30 THEN
                
                MI_TI_BASES(i).SALUD_TOTAL := PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * PCK_PARENTR.PARAMETRO41 / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990);
                MI_TI_BASES(i).APORTEEMPLEADOSALUD :=  PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * PCK_PARENTR.PARAMETRO41 / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990);
                MI_TI_BASES(i).PORC_SALUDE := PCK_PARENTR.PARAMETRO41;
            
            END IF;
            --JM CC 763 FIN

            --(MZANGUNA:15/03/2019)-Ajuste de salud en licencias no remuneradas
            MI_TI_BASES(i).APORTEPATRONALSALUD := PCK_SYSMAN_UTL.FC_ROUND(CASE MI_TI_BASES(i).TIPONOVEDAD
                WHEN 5 THEN PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDTOTAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990) - PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                WHEN 3 THEN PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * (CASE WHEN MI_APORTESOLOPATRON THEN MI_TI_BASES(i).PORC_SALUDP ELSE MI_TI_BASES(i).PORC_SALUDTOTAL END) / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                            - (CASE WHEN MI_APORTESOLOPATRON THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990) END)
                --TICKET 7743926(30/04/2024 JCROJAS)
                WHEN 2 THEN CASE WHEN MI_INCAPACIDAD = '11' 
								--CC_894(07/03/2025 JCROJAS): Cuando el concepto 360 >= 30 multiplique por MI_TI_BASES(i).PORC_SALUDTOTAL									
                                THEN PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASEPROPORCIONAL * (CASE WHEN PCK_NOMINA.FC_CN(360) >= 30 THEN MI_TI_BASES(i).PORC_SALUDTOTAL ELSE CASE WHEN MI_APORTESOLOPATRON THEN MI_TI_BASES(i).PORC_SALUDP ELSE MI_TI_BASES(i).PORC_SALUDTOTAL END END) / 100 + PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990)
                                     - (CASE WHEN MI_APORTESOLOPATRON THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDE / 100 + PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990) END)
                                ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDTOTAL / 100 + PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990) - PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDE / 100 + PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990)
                            END
				ELSE  PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDTOTAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990) - PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                END, PCK_NOMINA.GL_RAPORTES1990);

            IF (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESCALAFON = '10' AND PCK_NOMINA.CPARENTRADA(1).NIT =  '860061110')  AND PCK_NOMINA.GL_SPER NOT IN (1,2)  THEN -- JM CC 3224
            
                MI_TI_BASES(i).APORTEPATRONALSALUD := PCK_SYSMAN_UTL.FC_ROUND(CASE MI_TI_BASES(i).TIPONOVEDAD
                    WHEN 5 THEN PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDTOTAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990) - PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                    WHEN 3 THEN PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * (CASE WHEN MI_APORTESOLOPATRON THEN MI_TI_BASES(i).PORC_SALUDP ELSE MI_TI_BASES(i).PORC_SALUDTOTAL END) / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                                - (CASE WHEN MI_APORTESOLOPATRON THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND_100(MI_BASE * MI_TI_BASES(i).PORC_SALUDE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990) END)
                    --TICKET 7743926(30/04/2024 JCROJAS)
                    WHEN 2 THEN CASE WHEN MI_INCAPACIDAD = '11'
                    --CC_894(07/03/2025 JCROJAS): Cuando el concepto 360 >= 30 multiplique por MI_TI_BASES(i).PORC_SALUDTOTAL
                                    THEN PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASEPROPORCIONAL * (CASE WHEN PCK_NOMINA.FC_CN(360) >= 30 THEN MI_TI_BASES(i).PORC_SALUDTOTAL ELSE CASE WHEN MI_APORTESOLOPATRON THEN MI_TI_BASES(i).PORC_SALUDP ELSE MI_TI_BASES(i).PORC_SALUDTOTAL END END) / 100 + PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990)
                                         - (CASE WHEN MI_APORTESOLOPATRON THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_BASE * MI_TI_BASES(i).PORC_SALUDE / 100 + PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990) END)
                                    ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDTOTAL / 100 + PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990) - PCK_SYSMAN_UTL.FC_ROUND(MI_BASE * MI_TI_BASES(i).PORC_SALUDE / 100 + PCK_NOMINA.GL_SUMARSS1990, PCK_NOMINA.GL_RAPORTES1990)
                                END
                    ELSE  PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_SALUDTOTAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990) - PCK_SYSMAN_UTL.FC_ROUND_100(MI_BASE * MI_TI_BASES(i).PORC_SALUDE / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
                    END, PCK_NOMINA.GL_RAPORTES1990);

            END IF;



            MI_TI_BASES(i).FSP := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_TI_BASES(i).TIPONOVEDAD = 5
                THEN PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_FSP / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
				--CC_4385(17/06/2026 JCROJAS): Se agrega validacion para que, cuando se cumplan estas condiciones, no se tome MI_TI_BASES(i).BASEPROPORCIONAL, sino la base correspondiente al mes completo.																																																																																				 
                ELSE CASE WHEN PCK_NOMINA.FC_CNAN(11) >= 30 AND PCK_NOMINA.FC_CNAN(10) > PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) AND PCK_NOMINA.FC_CNAN(339) > 0 
                        THEN PCK_SYSMAN_UTL.FC_ROUND_100(((MI_BASE/(PCK_NOMINA.FC_CN(4)+PCK_NOMINA.FC_CNP(4)))*(PCK_NOMINA.FC_CN(9)+PCK_NOMINA.FC_CNP(9)+PCK_NOMINA.FC_CN(11)+PCK_NOMINA.FC_CNP(11)+PCK_NOMINA.FC_CN(339)+PCK_NOMINA.FC_CNP(339))) * MI_TI_BASES(i).PORC_FSP / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
						ELSE PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_FSP / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
					 END
                END, PCK_NOMINA.GL_RAPORTES1990);

            --(MZANGUNA:28/02/2019)-Se separa el cÃ¡lculo de subsistencia y adicional.
            --(APINEDA:26/12/2019)-Se cambia variable de porcentaje fsp subsistencia por campo PORC_FSP_SUBSISTENCIA debido a que se esta perdiendo el valor TAR 1000096419.
            MI_TI_BASES(i).FSPSUBSISTENCIA := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_TI_BASES(i).TIPONOVEDAD = 5
                THEN  PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_FSP_SUBSISTENCIA / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
				--CC_4385(17/06/2026 JCROJAS): Se agrega validacion para que, cuando se cumplan estas condiciones, no se tome MI_TI_BASES(i).BASEPROPORCIONAL, sino la base correspondiente al mes completo.																																																																																																																																					  
                ELSE CASE WHEN PCK_NOMINA.FC_CNAN(11) >= 30 AND PCK_NOMINA.FC_CNAN(10) > PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) AND PCK_NOMINA.FC_CNAN(339) > 0 
                        THEN PCK_SYSMAN_UTL.FC_ROUND_100(((MI_BASE/(PCK_NOMINA.FC_CN(4)+PCK_NOMINA.FC_CNP(4)))*(PCK_NOMINA.FC_CN(9)+PCK_NOMINA.FC_CNP(9)+PCK_NOMINA.FC_CN(11)+PCK_NOMINA.FC_CNP(11)+PCK_NOMINA.FC_CN(339)+PCK_NOMINA.FC_CNP(339))) * MI_TI_BASES(i).PORC_FSP_SUBSISTENCIA / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990) 
						ELSE PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_FSP_SUBSISTENCIA / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
					 END																																																																																				  
                END, PCK_NOMINA.GL_RAPORTES1990);

            IF PCK_NOMINA.FC_CN(339) > 0 THEN --(CC:2891 CFBARRERA Cuando hay dias de comision y el ingreso es menor a 4 salarios minimos, no calcular fondo ni fondo de subsistencia)
                --CC_4385(17/06/2026 JCROJAS): Se agrega validacion para que, cuando se cumplan estas condiciones, sume el CN y CNP de los conceptos 9,11 y 339.																																																		
                IF CASE WHEN PCK_NOMINA.FC_CNAN(11) >= 30 AND PCK_NOMINA.FC_CNAN(10) > PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) AND PCK_NOMINA.FC_CNAN(339) > 0 
                      THEN (MI_BASE/(PCK_NOMINA.FC_CN(4)+PCK_NOMINA.FC_CNP(4)))*(PCK_NOMINA.FC_CN(9)+PCK_NOMINA.FC_CNP(9)+PCK_NOMINA.FC_CN(11)+PCK_NOMINA.FC_CNP(11)+PCK_NOMINA.FC_CN(339)+PCK_NOMINA.FC_CNP(339))
					  ELSE (MI_BASE/ (PCK_NOMINA.FC_CN(4)+PCK_NOMINA.FC_CNP(4)))*(PCK_NOMINA.FC_CN(9)+PCK_NOMINA.FC_CN(9)) END <  4 * (PCK_NOMINA.FC_CN(201)) THEN 
                 MI_TI_BASES(i).FSP := 0;
                 MI_TI_BASES(i).FSPSUBSISTENCIA := 0;
                END IF;
            END IF;

            MI_TI_BASES(i).FSPADICIONAL := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN MI_TI_BASES(i).TIPONOVEDAD = 5
                    THEN  PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_FSP_ADICIONAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
					--CC_4385(17/06/2026 JCROJAS): Se agrega validacion para que, cuando se cumplan estas condiciones, no se tome MI_TI_BASES(i).BASEPROPORCIONAL, sino la base correspondiente al mes completo.																																																																																						   
                    ELSE CASE WHEN PCK_NOMINA.FC_CNAN(11) >= 30 AND PCK_NOMINA.FC_CNAN(10) > PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) AND PCK_NOMINA.FC_CNAN(339) > 0 
                            THEN PCK_SYSMAN_UTL.FC_ROUND_100(((MI_BASE/(PCK_NOMINA.FC_CN(4)+PCK_NOMINA.FC_CNP(4)))*(PCK_NOMINA.FC_CN(9)+PCK_NOMINA.FC_CNP(9)+PCK_NOMINA.FC_CN(11)+PCK_NOMINA.FC_CNP(11)+PCK_NOMINA.FC_CN(339)+PCK_NOMINA.FC_CNP(339))) * MI_TI_BASES(i).PORC_FSP_ADICIONAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
							ELSE PCK_SYSMAN_UTL.FC_ROUND_100(MI_TI_BASES(i).BASEPROPORCIONAL * MI_TI_BASES(i).PORC_FSP_ADICIONAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990)
						 END
                    END, PCK_NOMINA.GL_RAPORTES1990);

            IF UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'SI')) = 'NO' AND PCK_NOMINA_PROC01.FC_AUSENTISMO2DAQUINCENA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) AND MI_TI_BASES(i).TIPONOVEDAD = 4 THEN -- JM CC 2203
                MI_VALAUX := (MI_SALARIO_BASE + (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) - PCK_NOMINA.FC_CN(1) ELSE 0 END));
                MI_PORCFSP := CASE WHEN MI_VALAUX >= PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) THEN 0.5 ELSE 0 END;
                MI_PORCFSPADIC := CASE WHEN MI_VALAUX >= PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) THEN  0.5 ELSE 0 END;
                MI_PORCFSPSUBSIS := CASE WHEN MI_VALAUX >=  PCK_PARENTR.PARAMETRO38 * PCK_NOMINA.FC_CN(201) THEN  0.5 ELSE 0 END;
                MI_TI_BASES(i).PORC_FSP := MI_PORCFSP;
                MI_TI_BASES(i).PORC_FSP_SUBSISTENCIA := MI_PORCFSPSUBSIS;
                MI_TI_BASES(i).PORC_FSP_ADICIONAL :=  MI_PORCFSPADIC;
                MI_TI_BASES(i).FSP := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND_100(MI_VALAUX * MI_TI_BASES(i).PORC_FSP / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990), PCK_NOMINA.GL_RAPORTES1990);
                MI_TI_BASES(i).FSPSUBSISTENCIA := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND_100(MI_VALAUX * MI_TI_BASES(i).PORC_FSP_SUBSISTENCIA / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990), PCK_NOMINA.GL_RAPORTES1990);
                MI_TI_BASES(i).FSPADICIONAL := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND_100(MI_VALAUX * MI_TI_BASES(i).PORC_FSP_ADICIONAL / 100 , MI_PARAMETRO1990APLICA,PCK_NOMINA.GL_SUMARSS1990,PCK_NOMINA.GL_RAPORTES1990), PCK_NOMINA.GL_RAPORTES1990);
            END IF;

            --(GPORTILLA:31/01/2022)-7721354_NOMINA                    
            IF (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).TIPOVINCULACION = '23') THEN 
                
                MI_TI_BASES(i).PENSION_TOTAL := 0;
                MI_TI_BASES(i).APORTEEMPLEADOPENSION := 0;
                MI_TI_BASES(i).APORTEPATRONALPENSION := 0;
                MI_TI_BASES(i).SALUD_TOTAL := 0;
                MI_TI_BASES(i).APORTEEMPLEADOSALUD := 0;
                MI_TI_BASES(i).APORTEPATRONALSALUD := 0;
            END IF;
            --FIN(GPORTILLA:31/01/2022)-7721354_NOMINA
            --(APINEDA:29/05/2019)-TAR 1000092482 Se agrega validaciÃ³n para el subtipo 4(cotizante con requisitos cumplidos para pensiÃ³n)
            --(APINEDA:10/08/2019)-Se agrega validaciÃ³n para subtipos cotizante 12 y 19 (Funcionarios SENA)
            --JM 7737828  se agrega a la validacion los SUBTIPOCOTIZANTES 3 y 5 --MOD JM CC 2838 tambien el 6
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SUBTIPOCOTIZANTE = 6 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SUBTIPOCOTIZANTE = 4 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SUBTIPOCOTIZANTE = 3 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SUBTIPOCOTIZANTE = 5 OR  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SUBTIPOCOTIZANTE = 1 OR (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).TIPOVINCULACION IN (12,19)) THEN--(CC:765_CFBARRERA_Ajuste cuando es SubTipo Cotizante  5,4,3 1, no genere aportes a pension)
                MI_TI_BASES(i).FSP := 0;
                MI_TI_BASES(i).FSPSUBSISTENCIA := 0;
                MI_TI_BASES(i).FSPADICIONAL := 0;
                MI_TI_BASES(i).APORTEEMPLEADOPENSION := 0;
                MI_TI_BASES(i).APORTEPATRONALPENSION := 0;
            --(JORDUZ:03/02/2020)-TAR 1000096668 Se agregan a la validaciÃ³n los aportes de salud tanto del patron como del empleado      
                --JM 7737828
				--CC_3464(12/12/2026 JCROJAS): Se adiciona la condicion AND PCK_NOMINA.CPARENTRADA(1).EXONERADO_1607 = 'S' en la validacion, ya que el ajuste unicamente debe aplicarse a las entidades exoneradas.
                IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SUBTIPOCOTIZANTE NOT IN (5,4) AND PCK_NOMINA.CPARENTRADA(1).EXONERADO_1607 = 'S' THEN --MOD JM CC 2938
                    MI_TI_BASES(i).APORTEEMPLEADOSALUD := 0;
                    MI_TI_BASES(i).APORTEPATRONALSALUD := MI_TI_BASES(i).SALUD_TOTAL;
                END IF;
                --BCARDENAS CC_1952 08/07/2025
                --MOD JM CC 2938 (si dejamos por parametro pero no definimos para quien, lo va a hacer para todos)
                IF PCK_PARST.FC_PAR('REFORMA LABORAL LEY 2466 DE 2025 - APRENDICES SENA', 'NO') = 'SI' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).TIPOVINCULACION IN (12,19) THEN 
                    MI_TI_BASES(i).APORTEEMPLEADOSALUD := 0;
                    MI_TI_BASES(i).APORTEPATRONALSALUD := MI_TI_BASES(i).SALUD_TOTAL;
                    /*IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).TIPOVINCULACION IN (19) THEN
                    MI_TI_BASES(i).APORTEEMPLEADOPENSION := 0;
                    MI_TI_BASES(i).APORTEPATRONALPENSION := MI_TI_BASES(i).PENSION_TOTAL;
                    END IF;*/
                END IF;
                --BCARDENAS CC_1952 08/07/2025
            END IF;
            -- CC 1953 BCARDENAS 24/07/2025
            IF PCK_NOMINA.CPARENTRADA(1).EXONERADO_1607 = 'S'  AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '05' And PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).TIPOVINCULACION = '1' THEN -- 24072025 cambios tipo 1 mpv
             MI_TI_BASES(i).APORTEPATRONALSALUD  := 0; -- 24072025 cambios tipo 1 mpv
            END IF;
            MI_VALPRUE :=PCK_NOMINA.GL_IBLREXONERACION; 
             ---<TAR:7702452 FECHA:24/01/2022 AUTOR:CP>
              IF PCK_PARST.FC_PAR('APLICA EXONERACION EN BASE TOTAL INGRESOS 097', '0') = 'SI' THEN --18122018
                   IF  ((PCK_NOMINA.FC_CN(151) = PCK_NOMINA.FC_CNP(151)) Or (PCK_NOMINA.FC_CN(155) = PCK_NOMINA.FC_CNP(155))) Or ((PCK_NOMINA.FC_CN(174) + PCK_NOMINA.FC_CN(175)) = (PCK_NOMINA.FC_CNP(174) + PCK_NOMINA.FC_CNP(175))) THEN --30092021 TAR 110506 ESPCAJICA
                        PCK_NOMINA.GL_IBLREXONERACION := PCK_NOMINA.FC_CN(97) + (PCK_NOMINA.FC_CNP(97) - PCK_NOMINA.FC_CNP(151) - PCK_NOMINA.FC_CNP(155) - PCK_NOMINA.FC_CNP(174) - PCK_NOMINA.FC_CNP(175));
                    ELSE
                        PCK_NOMINA.GL_IBLREXONERACION := (PCK_NOMINA.FC_CN(97) + PCK_NOMINA.FC_CNP(97));
                    END IF;

                    --JM 03/03/2025 CC992 piede darse el caso que en vacaciones el 97 no es suficiente 
                    IF (PCK_NOMINA.FC_CN(35) > 0) AND ( MI_BASE > 10 * PCK_NOMINA.FC_CN(201)) AND PCK_PARENTR.PARAMETRO70 = 'S' THEN 
                        PCK_NOMINA.GL_IBLREXONERACION := MI_BASE;
                    END IF;
                    --JM 03/03/2025 CC992 FIN
              ELSE
                  -- TICKET 7736122 EFCM: COMO BASE DE EXONERACION SE TOMA LOS CONCEPTOS CLASE REMUNERACION CON INDICADOR REPORTEEPS SI EL IBLR ES CERO
                  PCK_NOMINA.GL_IBLREXONERACION := CASE WHEN PCK_NOMINA.GL_IBLR != 0 THEN PCK_NOMINA.GL_IBLR ELSE PCK_NOMINA_SEGSOCI.FC_SUMAREMUNSS(PCK_NOMINA.GL_COMPANIA) END;
                  -- TICKET 7736122 FIN --
              END IF;
            
            MI_VALPRUE :=PCK_NOMINA.GL_IBLREXONERACION; 
            MI_VALPRUE := PCK_PARST.FC_PAR('LIMITE EXONERAR PARAFISCALES',0) * PCK_NOMINA.FC_CN(201);
            IF (PCK_PARST.FC_PAR('APLICAR EXONERACION APORTES SALUD PATRONO 8.5%','NO') = 'SI' AND PCK_NOMINA.CPARENTRADA(1).EXONERADO_1607 = 'S' AND PCK_NOMINA.GL_IBLREXONERACION < PCK_PARST.FC_PAR('LIMITE EXONERAR PARAFISCALES',0) * PCK_NOMINA.FC_CN(201)  AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '04' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '05'))THEN
               MI_TI_BASES(i).APORTEPATRONALSALUD  :=  0;
            END IF ;
            --</TAR>
            --(APINEDA:13/05/2020)-TAR 1000099189 Se crea el parÃ¡metro  para calcular o no  el fondo pensional Decreto_686
            IF PCK_PARST.FC_PAR('CALCULAR FONDO SOLIDARIDAD PENSIONAL', 'NO') = 'NO' THEN 
                MI_TI_BASES(i).FSP := 0;
                MI_TI_BASES(i).FSPSUBSISTENCIA := 0;
                MI_TI_BASES(i).FSPADICIONAL := 0;
            END IF;           

            MI_TI_BASES(i).FSPADICIONAL := MI_TI_BASES(i).FSPADICIONAL - MI_TI_BASES(i).FSPSUBSISTENCIA;
            --(MZANGUNA:28/02/2019)-Se adiciona columna FSPSUBSISTENCIA
            --(APINEDA:18/10/2019)-Se adiciona columna DIASCOMPLETOSRECONOCIDOS para almacenar el nÃºmero de dÃ­as reconocidos al 100% en incapacidades.
            --(APINEDA:26/12/2019)-Se agrega campo PORC_FSP_SUBSISTENCIA
             --(JORDUZ:29/12/2020)-Se aÃ±ade condicion para la base en UPC ya que debe tomar el valor del concepto 1 segun TAR 1000101616
            IF PCK_NOMINA.CPARENTRADA(1).NIT = '892.300.285-6' THEN MI_BASE := PCK_NOMINA.FC_CN(1); END IF;
            MI_CAMPOS  := ' COMPANIA
                           ,LLAVENOVEDAD
                           ,TIPONOVEDAD
                           ,CONSECUTIVO
                           ,ID_DE_EMPLEADO
                           ,ANO
                           ,MES
                           ,PERIODO
                           ,FECHAINICIAL
                           ,FECHAFINAL
                           ,BASE
                           ,DIAS
                           ,BASEPROPORCIONAL
                           ,PORC_PENSIONTOTAL
                           ,PORC_SALUDTOTAL
                           ,PORC_FSP
                           ,PORC_FSP_ADICIONAL
                           ,PORC_PENSIONE
                           ,PORC_PENSIONP
                           ,PORC_SALUDE
                           ,PORC_SALUDP
                           ,PENSION_TOTAL
                           ,APORTEEMPLEADOPENSION
                           ,APORTEPATRONALPENSION
                           ,SALUD_TOTAL
                           ,APORTEEMPLEADOSALUD
                           ,APORTEPATRONALSALUD
                           ,FSP
                           ,FSPSUBSISTENCIA
                           ,FSPADICIONAL
                           ,MANUAL
                           ,CREATED_BY
                           ,DATE_CREATED
                           ,PORC_FSP_SUBSISTENCIA
                           ,DIASCOMPLETOSRECONOCIDOS';

            MI_VALORES := '''' || MI_TI_BASES(i).COMPANIA || '''
                        , ''' || MI_TI_BASES(i).LLAVENOVEDAD || '''
                        , '|| MI_TI_BASES(i).TIPONOVEDAD ||'
                        , '|| MI_TI_BASES(i).CONSECUTIVO ||'
                        ,'|| MI_TI_BASES(i).ID_DE_EMPLEADO ||'
                        ,'|| MI_TI_BASES(i).ANO ||'
                        ,'|| MI_TI_BASES(i).MES ||'
                        ,'|| MI_TI_BASES(i).PERIODO ||'
                        , TO_DATE(''' || TO_CHAR(MI_TI_BASES(i).FECHAINICIAL, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                        , TO_DATE(''' || TO_CHAR(MI_TI_BASES(i).FECHAFINAL, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                        ,'|| CASE WHEN MI_BASE_AUSENTISMO > 0 THEN  MI_BASE_AUSENTISMO ELSE MI_BASE END  ||'
                        ,'|| MI_TI_BASES(i).DIAS ||'
                        ,'|| MI_TI_BASES(i).BASEPROPORCIONAL ||'
                        ,'|| MI_TI_BASES(i).PORC_PENSIONTOTAL ||'
                        ,'|| MI_TI_BASES(i).PORC_SALUDTOTAL ||'
                        ,'|| MI_TI_BASES(i).PORC_FSP ||'
                        ,'|| MI_TI_BASES(i).PORC_FSP_ADICIONAL ||'
                        ,'|| MI_TI_BASES(i).PORC_PENSIONE ||'
                        ,'|| MI_TI_BASES(i).PORC_PENSIONP ||'
                        ,'|| MI_TI_BASES(i).PORC_SALUDE ||'
                        ,'|| MI_TI_BASES(i).PORC_SALUDP ||'
                        ,'|| MI_TI_BASES(i).PENSION_TOTAL ||'
                        ,'|| MI_TI_BASES(i).APORTEEMPLEADOPENSION ||'
                        ,'|| MI_TI_BASES(i).APORTEPATRONALPENSION ||'
                        ,'|| MI_TI_BASES(i).SALUD_TOTAL ||'
                        ,'|| MI_TI_BASES(i).APORTEEMPLEADOSALUD ||'
                        ,'|| MI_TI_BASES(i).APORTEPATRONALSALUD ||'
                        ,'|| MI_TI_BASES(i).FSP ||'
                        ,'|| MI_TI_BASES(i).FSPSUBSISTENCIA ||'
                        ,'|| MI_TI_BASES(i).FSPADICIONAL ||'
                        ,'|| MI_TI_BASES(i).MANUAL ||'
                        ,'''|| MI_TI_BASES(i).CREATED_BY ||'''
                        , ''' || MI_TI_BASES(i).DATE_CREATED || '''
                        ,'|| MI_TI_BASES(i).PORC_FSP_SUBSISTENCIA ||'
                        ,'|| CASE WHEN MI_TI_BASES(i).TIPONOVEDAD = 4 THEN 0 ELSE MI_TI_BASES(i).DIASCOMPLETOSRECONOCIDOS END ||'';
            BEGIN
                BEGIN
                --  (26/03/2020--Jorduz--TAR 1000097800)Se realiza la validacion para que cuando se inserte una novedad que tenga dias en 0 
                --  estas no se creenen la la tabala BASESNOVEDADES y cree registos con valores en 0    
                    IF  MI_TI_BASES(i).DIAS > 0 THEN -- MP CC3843
                      IF NOT (UPPER(PCK_PARST.FC_PAR('NOMINA MENSUAL', 'SI')) = 'NO' AND PCK_NOMINA_PROC01.FC_AUSENTISMO2DAQUINCENA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) AND PCK_NOMINA.GL_SPER = 2) OR PCK_PARST.FC_PAR('MOSTRAR SEGURIDAD SOCIAL EN PRIMERA QUINCENA','SI') = 'SI' THEN -- JM CC 2203 MP CC3843
                        MI_FILAS := PCK_DATOS.FC_ACME
                                ( UN_TABLA => MI_TABLA,
                                  UN_ACCION => 'I',
                                  UN_CAMPOS => MI_CAMPOS,
                                  UN_VALORES => MI_VALORES);
                      END IF;-- JM CC 2203 para que no de error en la segunda ya que calcuulo todo en la primera 
                     END IF;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
                MI_MSGERROR(1).CLAVE := 'EMPLEADO';
                MI_MSGERROR(1).VALOR := UN_EMPLEADO;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    =>SQLCODE,
                UN_ERROR_COD  =>PCK_ERRORES.ERR_INGRESARBASESNOVEDAD  ,
                UN_REEMPLAZOS => MI_MSGERROR );
            END;

        END LOOP INSERTBASES;


        IF (MI_CUENTA) < PCK_NOMINA.FC_CN(4) AND (PCK_NOMINA.GL_SPER <> 7 AND PCK_NOMINA.GL_SPER <> 1)  THEN --(MZANGUNA:27/03/2019)-Cuando se retira esta alerta no debe ir. --JM 04/10/2024
            MI_MSGERROR(1).CLAVE := 'EMPLEADO';
            MI_MSGERROR(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRECOMPLETO; --(MZANGUNA:21/03/2019)-Se ajusta el reeemplazo del mensaje.
            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ERR_DIASMENORA30
                ,UN_REEMPLAZOS   => MI_MSGERROR
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
        END IF;

        BEGIN
            SELECT SUM(BASEPROPORCIONAL)
            INTO   MI_RTA
            FROM   BASESNOVEDADES
            WHERE  COMPANIA = UN_COMPANIA
              AND  ANO = UN_ANO
              AND  MES = UN_MES
              AND  PERIODO = PCK_NOMINA.GL_SPER
              AND  ID_DE_EMPLEADO = UN_EMPLEADO;

        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_RTA := 0;
        END;

        IF PCK_NOMINA.GL_PROCESOREAL <> 10 --(CC:3708_CFBARRERA_INI)
                AND (MI_PERIODOBASENOV <> PCK_NOMINA.GL_SPER 
                OR (NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PAGAR MESES DE 31 EN NOMINA QUINCENAL',6,SYSDATE),'NO') = 'SI' 
                AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'NOMINA MENSUAL',6,SYSDATE),'SI') = 'NO')) THEN --(CC:3708_CFBARRERA_FIN)
            PCK_NOMINA.GL_AC :=  PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_RTA := CASE WHEN PCK_NOMINA.FC_CNA(112) > MI_RTA THEN (PCK_NOMINA.FC_CNA(112) - MI_RTA) ELSE (MI_RTA - PCK_NOMINA.FC_CNA(112)) END ; --(MZANGUNA:29/04/2019)-Se condiciona operaciÃ³n.
        END IF;

        PCK_NOMINA.GL_CN112NUEVO := MI_RTA;
        --END IF;
    END IF;
    RETURN MI_RTA;

END FC_CALCULARBASENOVEDADES_SEG;

FUNCTION FC_CIERRE_NOMINAPRELIMINAR(
/*
   NAME              : PR_CIERRE_NOMINAPRELIMINAR
   AUTHORS           : SYSMAN SAS
   AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÃ‘A HURTADO
   DATE MIGRADOR     : 19/09/2018
   TIME              : 11:31 AM
   SOURCE MODULE     :
   MODIFIER          :
   DATE MODIFIED     :
   TIME              :
   DESCRIPTION       : Actualiza el periodo a un periodo homologo sumÃ¡ndole 80, Se actualizan
                       las tablas principales y se actualizan indicadores del periodo actual y del homologo
   PARAMETERS        : UN_COMPANIA  => CompaÃ±ia en la que se esta trabajando
                       UN_PROCESO   => Proceso actual de nÃ³mina
                       UN_ANO       => AÃ±o del periodo de nÃ³mina
                       UN_MES       => Mes del periodo de nÃ³mina
                       UN_PERIODO   => Periodo del periodo de nÃ³mina el cual se guarda la informaciÃ³n en un nuevo periodo + 80
                                       y con indicador de nÃ³mina Preliminar
                       UN_USUARIO   => Usuario el cual ejecuta el proceso.
   MODIFICATIONS     :
   @NAME:    cierreNominaPreliminar
   @METHOD:  POST
*/
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO     IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANO       IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MES       IN  PCK_SUBTIPOS.TI_MES,
    UN_PERIODO     IN  PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_USUARIO     IN  PCK_SUBTIPOS.TI_USUARIO
) RETURN VARCHAR2

AS
    MI_PERIODOACERRAR       PCK_SUBTIPOS.TI_PERIODO_NOMI;    --Periodo donde se saca la copia.
    MI_PERIODOAGUARDAR      PCK_SUBTIPOS.TI_PERIODO_NOMI;    --Periodo donde se guarda
    MI_CUENTA               PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_FILAS                PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_EXISTEPRELIMINAR     PCK_SUBTIPOS.TI_LOGICO DEFAULT 0;
    MI_EXCLUIDOS            PCK_SUBTIPOS.TI_EXCLUIDOS;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_RTA                  VARCHAR2(500 CHAR);
BEGIN
    MI_PERIODOACERRAR := UN_PERIODO;

    IF (MI_PERIODOACERRAR + 80) > 100 THEN
        --No se permite cerrar el periodo --PERIODO--.
        MI_MSGERROR(1).CLAVE := 'PERIODO';
        MI_MSGERROR(1).VALOR := MI_PERIODOACERRAR;
        PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    =>-20000,
            UN_REEMPLAZOS => MI_MSGERROR,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PERIODOMAYOR100 );
    END IF;

    MI_PERIODOAGUARDAR := MI_PERIODOACERRAR + 80;

    BEGIN
        SELECT COUNT(0), PRELIMINAR
        INTO  MI_CUENTA, MI_EXISTEPRELIMINAR
        FROM  PERIODOS
        WHERE COMPANIA = UN_COMPANIA
          AND ID_DE_PROCESO = UN_PROCESO
          AND ANO = UN_ANO
          AND MES = UN_MES
          AND PERIODO = MI_PERIODOAGUARDAR
        GROUP BY PRELIMINAR ;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CUENTA := 0;
        MI_EXISTEPRELIMINAR := 0;
    END;

    IF MI_CUENTA = 0 OR MI_EXISTEPRELIMINAR <> 0 THEN
        --ERR_EXISTEDATOSNOMPRELI: El periodo --PERIODO-- del aÃ±o --ANIO-- mes --MES--, ya tiene registros de nÃ³mina preliminar.
        --ERR_NOEXISTEPERIODO: El periodo --ANIO--, mes --MES--, periodo --PERIODO--; no se encuentra configurado. Por favor verifique
        /*MI_MSGERROR(1).CLAVE := 'ANIO';
        MI_MSGERROR(1).VALOR := UN_ANO ;
        MI_MSGERROR(2).CLAVE := 'MES';
        MI_MSGERROR(2).VALOR := UN_MES ;
        MI_MSGERROR(3).CLAVE := 'PERIODO';
        MI_MSGERROR(3).VALOR := MI_PERIODOAGUARDAR ;
        PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    =>-20000,
            UN_REEMPLAZOS => MI_MSGERROR,
            UN_ERROR_COD  => CASE WHEN MI_EXISTEPRELIMINAR <> 0 THEN PCK_ERRORES.ERR_EXISTEDATOSNOMPRELI ELSE PCK_ERRORES.ERR_NOEXISTEPERIODO END);*/

        MI_RTA := CASE WHEN MI_EXISTEPRELIMINAR <> 0 THEN 'Este proceso ya fue realizado no se puede volver a Realizar para este periodo'
                  ELSE 'El periodo '|| UN_ANO ||', mes '|| UN_MES ||', periodo '|| MI_PERIODOAGUARDAR ||'; no se encuentra configurado. Por favor verifique'
                  END;
        RETURN MI_RTA;

    END IF;

    --Actualizar Historicos, PERSONAL_HISTORICO, BASESNOVEDADES

    BEGIN
        MI_TABLA     := 'HISTORICOS';
        BEGIN
            MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                          AND ID_DE_PROCESO =  '|| UN_PROCESO ||'
                          AND ANO = '|| UN_ANO ||'
                          AND MES = '|| UN_MES ||'
                          AND PERIODO = '|| MI_PERIODOAGUARDAR ||'   ';

            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                          UN_ACCION  => 'E',
                                          UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;

        MI_EXCLUIDOS := 'PERIODO';
        MI_CAMPOS    := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(UN_TABLA     => MI_TABLA,
                                                      UN_EXCLUIDOS  => MI_EXCLUIDOS,
                                                      UN_VIRTUAL    => 2);

        MI_VALORES   := 'SELECT '|| MI_PERIODOAGUARDAR ||',
                            '|| MI_CAMPOS ||',
                            SYSDATE,'''|| UN_USUARIO ||'''
                        FROM  HISTORICOS
                        WHERE COMPANIA = '''|| UN_COMPANIA ||'''
                          AND ID_DE_PROCESO =  '|| UN_PROCESO ||'
                          AND ANO = '|| UN_ANO ||'
                          AND MES = '|| UN_MES ||'
                          AND PERIODO = '|| MI_PERIODOACERRAR ||'  ';

        MI_CAMPOS := MI_CAMPOS||', DATE_CREATED, CREATED_BY';
        MI_CAMPOS := MI_EXCLUIDOS|| ',' ||MI_CAMPOS ;

        BEGIN
            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                            UN_ACCION  => 'IS',
                                            UN_CAMPOS  => MI_CAMPOS,
                                            UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;

        MI_TABLA     := 'PERSONAL_HISTORICO';
        BEGIN
            MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                          AND ID_DE_PROCESO =  '|| UN_PROCESO ||'
                          AND ANO = '|| UN_ANO ||'
                          AND MES = '|| UN_MES ||'
                          AND PERIODO = '|| MI_PERIODOAGUARDAR ||'    ';

            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                          UN_ACCION  => 'E',
                                          UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;

        MI_EXCLUIDOS := 'PERIODO';
        MI_CAMPOS    := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(UN_TABLA      => MI_TABLA,
                                                       UN_EXCLUIDOS  => MI_EXCLUIDOS,
                                                       UN_VIRTUAL    => 2);

        MI_VALORES   := 'SELECT '|| MI_PERIODOAGUARDAR ||',
                            '|| MI_CAMPOS ||',
                            SYSDATE,'''|| UN_USUARIO ||'''
                        FROM  PERSONAL_HISTORICO
                        WHERE COMPANIA = '''|| UN_COMPANIA ||'''
                          AND ID_DE_PROCESO =  '|| UN_PROCESO ||'
                          AND ANO = '|| UN_ANO ||'
                          AND MES = '|| UN_MES ||'
                          AND PERIODO = '|| MI_PERIODOACERRAR ||'  ';

        MI_CAMPOS   :=MI_CAMPOS||', DATE_CREATED, CREATED_BY';
        MI_CAMPOS   := MI_EXCLUIDOS|| ',' ||MI_CAMPOS ;

        BEGIN
            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                            UN_ACCION  => 'IS',
                                            UN_CAMPOS  => MI_CAMPOS,
                                            UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;

        MI_TABLA     := 'BASESNOVEDADES';
        BEGIN
            MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                          AND ANO = '|| UN_ANO ||'
                          AND MES = '|| UN_MES ||'
                          AND PERIODO = '|| MI_PERIODOAGUARDAR ||'   ';

            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                          UN_ACCION  => 'E',
                                          UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;

        MI_EXCLUIDOS := 'PERIODO';
        MI_CAMPOS    := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(UN_TABLA     => MI_TABLA,
                                                      UN_EXCLUIDOS  => MI_EXCLUIDOS,
                                                      UN_VIRTUAL    => 2);

        MI_VALORES   := 'SELECT '|| MI_PERIODOAGUARDAR ||',
                            '|| MI_CAMPOS ||',
                            SYSDATE,'''|| UN_USUARIO ||'''
                        FROM  BASESNOVEDADES
                        WHERE COMPANIA = '''|| UN_COMPANIA ||'''
                          AND ANO = '|| UN_ANO ||'
                          AND MES = '|| UN_MES ||'
                          AND PERIODO = '|| MI_PERIODOACERRAR ||'  ';

        MI_CAMPOS   :=MI_CAMPOS||', DATE_CREATED, CREATED_BY';
        MI_CAMPOS   := MI_EXCLUIDOS|| ',' ||MI_CAMPOS ;

        BEGIN
            MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                            UN_ACCION  => 'IS',
                                            UN_CAMPOS  => MI_CAMPOS,
                                            UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;

        --Actualizo indicador de PRELIMINAR para que no deje modificar los datos guardados en el calculo, dado que este periodo queda informativo
        --periodo 83 sin indicadores activo, diferido ni acumulado
        MI_TABLA := 'PERIODOS';
        MI_CAMPOS := 'PRELIMINAR = -1
                     ,ACUMULADO = 0
                     ,DIFERIDOS = 0
                     ,ESTADO = 0
                     ,MODIFIED_BY = '''|| UN_USUARIO ||'''
                     ,DATE_MODIFIED = SYSDATE';

        MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                      AND ID_DE_PROCESO = '|| UN_PROCESO ||'
                      AND ANO = '|| UN_ANO ||'
                      AND MES = '|| UN_MES ||'
                      AND PERIODO = '|| MI_PERIODOAGUARDAR ||'    ';
        BEGIN
            MI_FILAS :=PCK_DATOS.FC_ACME
                (UN_TABLA     => MI_TABLA
                ,UN_ACCION    => 'M'
                ,UN_CAMPOS    => MI_CAMPOS
                ,UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;

        --Indicador del periodo para calcular diferencias.
        MI_TABLA := 'PERIODOS';
        MI_CAMPOS := 'AJUSTES = -1';
        MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA ||'''
                      AND ID_DE_PROCESO = '|| UN_PROCESO ||'
                      AND ANO = '|| UN_ANO ||'
                      AND MES = '|| UN_MES ||'
                      AND PERIODO = '|| MI_PERIODOACERRAR ||'    ';
        BEGIN
            MI_FILAS :=PCK_DATOS.FC_ACME
                (UN_TABLA     => MI_TABLA
                ,UN_ACCION    => 'M'
                ,UN_CAMPOS    => MI_CAMPOS
                ,UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
        --Se presentÃ³ error al actualizar el periodo --PERIODOGUARDAR--  en --TABLA--, en el cierre de nÃ³mina.
        MI_MSGERROR(1).CLAVE := 'PERIODOGUARDAR';
        MI_MSGERROR(1).VALOR := MI_PERIODOAGUARDAR;
        MI_MSGERROR(2).CLAVE := 'TABLA';
        MI_MSGERROR(2).VALOR := MI_TABLA;

        PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_ACTPERIODOCIERRENOM,
            UN_REEMPLAZOS => MI_MSGERROR);
    END;
    MI_RTA := 'Proceso ejecutado exitosamente.';
    RETURN MI_RTA;

END FC_CIERRE_NOMINAPRELIMINAR;

PROCEDURE PR_CALCULARQUINQUENIOUPC(
/*
    NAME              : PR_CALCULARQUINQUENIOUPC -> EN ACCESS calcularquinquenioUPC
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÃ‘A HURTADO
    DATE MIGRADOR     : 17/10/2018
    TIME              : 8:04 AM
    SOURCE MODULE     : SERVIDORNOMINA2010
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :

  */
  UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_FQ           DATE;
    MI_DQP          NUMBER DEFAULT 0;
    MI_MSG          PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALORCN140   NUMBER DEFAULT 0;
BEGIN

    --(MZANGUNA:17/10/2018)-Se documenta dado que en nomina mensual no debe quitar el valor.
    /*FOR i IN 1..599
    LOOP
        PCK_NOMINA.CN(i) := CASE WHEN i <> 180 THEN 0 ELSE PCK_NOMINA.FC_CN(i) END;
    END LOOP;*/

    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESCALAFON <> '10' THEN
        --RECARGOSUELDO := 0;
        /*PRV := 0;
        PRJ := 0;
        PRN := 0;*/

        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359);
        MI_FQ := (CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 THEN
                    (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN SYSDATE ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO - 1 END)
                 ELSE (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAANTIGQUINQUENIO IS NULL THEN NULL ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAANTIGQUINQUENIO END)
                 END);
        MI_FQ := CASE WHEN MI_FQ = NULL THEN PCK_NOMINA.GL_FECHAFIN ELSE MI_FQ END;

        PCK_NOMINA.GL_ANTIG := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO, PCK_NOMINA.GL_FECHAFIN) - PCK_NOMINA.GL_DNT;

        --FIPROM := FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN);
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359)
                                 + (CASE WHEN MI_FQ >= PCK_NOMINA.GL_FECHAINI1 THEN PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) ELSE 0 END);
        --FACTORQUIN := PCK_NOMINA.FC_CN(1);
        PCK_NOMINA.GL_SBM := PCK_SYSMAN_UTL.FC_ROUND( (CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE
                                        (CASE WHEN PCK_NOMINA.FC_CN(210) = 0 THEN (CASE WHEN PCK_NOMINA.FC_CN(1) = 0 THEN PCK_NOMINA.CCATEGORIA(1).SALARIO_BASE ELSE PCK_NOMINA.FC_CN(1) END)
                                        ELSE PCK_NOMINA.FC_CN(210) * PCK_NOMINA.FC_CN(211)
                                        END)
                                    END ), 0);
        PCK_NOMINA.CN(1) := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE (CASE WHEN PCK_NOMINA.FC_CN(210) = 0 THEN
                            (CASE WHEN PCK_NOMINA.FC_CN(1) = 0 THEN PCK_NOMINA.CCATEGORIA(1).SALARIO_BASE ELSE PCK_NOMINA.FC_CN(1) END) ELSE PCK_NOMINA.FC_CN(210) * PCK_NOMINA.FC_CN(211) END) END), 0);
        MI_DQP := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL((CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 THEN (CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAQUINQUENIO IS NULL THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO ELSE
                    PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHAQUINQUENIO END) ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO END)
                    , MI_FQ) - PCK_NOMINA.GL_LICENCIAS - PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
        MI_DQP := (CASE WHEN MI_DQP >= 1800 OR PCK_NOMINA.FC_CN(410) = 1 THEN 1800 ELSE MI_DQP END);

        PCK_NOMINA.GL_ANTIG := PCK_NOMINA.GL_ANTIG / 360;
        IF MOD(TRUNC(PCK_NOMINA.GL_ANTIG), 5) = 0 THEN
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO <> 0 THEN
                PCK_NOMINA.CN(180) := CASE WHEN PCK_NOMINA.FC_CN(180) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_SBM, 0) / 30
                                        * (CASE WHEN TRUNC(PCK_NOMINA.GL_ANTIG) = 5 THEN 30 ELSE CASE WHEN TRUNC(PCK_NOMINA.GL_ANTIG) = 10 THEN 40 ELSE (CASE WHEN TRUNC(PCK_NOMINA.GL_ANTIG) >= 15 THEN 45 ELSE 0 END) END END)
                                        ELSE PCK_NOMINA.FC_CN(180) END;
                PCK_NOMINA.CN(924) := PCK_NOMINA.FC_CN(288);
                --PCK_NOMINA.CN(125) := RETEF(PCK_NOMINA.FC_CN(185) * 0.75, PCK_NOMINA.GL_SANO, 'V');
                IF PCK_PARST.FC_PAR('LIQUIDAN DESCUENTO DE ESTAMPILLAS', ' ') = 'SI' THEN
                    /*FOR i IN 1..CONTAD
                    LOOP
                        IF PCK_NOMINA.FC_CND(i).CONCEPTO = '610' THEN
                            PCK_NOMINA.CN(610) := CASE WHEN PCK_PARST.FC_PAR('DESCONTAR ESTAMPILLA PROCIUDADELA', ' ') = 'SI' THEN PCK_NOMINA.FC_CND(i).VALOR ELSE 0 END;
                        END IF;
                    END LOOP;*/
                    PCK_NOMINA.CN(610) := CASE WHEN PCK_PARST.FC_PAR('DESCONTAR ESTAMPILLA PROCIUDADELA', ' ') = 'SI' THEN PCK_NOMINA.FC_CN(610) ELSE 0 END;

                    IF PCK_NOMINA.FC_CN(610) = 0 AND PCK_NOMINA.GL_SPER <> '04' AND PCK_PARST.FC_PAR('DESCONTAR ESTAMPILLA PROCIUDADELA', ' ') = 'SI' THEN
                        PCK_NOMINA.CN(610) := CASE WHEN PCK_NOMINA.FC_CN(610) = 0 THEN (PCK_NOMINA.FC_CN(2)) * TO_NUMBER(PCK_PARST.FC_PAR('ESTAMPILLA', '0')) ELSE PCK_NOMINA.FC_CN(610) END;
                        PCK_NOMINA.CN(610) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(610), 0);
                    END IF;
                END IF;
            ELSE
                PCK_NOMINA.CN(180) := CASE WHEN PCK_NOMINA.FC_CN(180) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_SBM, 0) ELSE PCK_NOMINA.FC_CN(180) END;
                PCK_NOMINA.CN(924) := PCK_NOMINA.FC_CN(288);
                --PCK_NOMINA.CN(125) := RETEF(PCK_NOMINA.FC_CN(185) * 0.75, PCK_NOMINA.GL_SANO, 'V');

                /*FOR i IN 1..CONTAD
                LOOP
                    IF PCK_NOMINA.FC_CND(i).CONCEPTO = '610' THEN
                        PCK_NOMINA.CN(610) := CASE WHEN PCK_PARST.FC_PAR('DESCONTAR ESTAMPILLA PROCIUDADELA', ' ') = 'SI' THEN PCK_NOMINA.FC_CND(i).VALOR ELSE 0 END;
                    END IF;
                END LOOP;*/
                PCK_NOMINA.CN(610) := CASE WHEN PCK_PARST.FC_PAR('DESCONTAR ESTAMPILLA PROCIUDADELA', ' ') = 'SI' THEN PCK_NOMINA.FC_CN(610) ELSE 0 END;

                IF PCK_NOMINA.FC_CN(610) = 0 AND PCK_NOMINA.GL_SPER <> '04' AND PCK_PARST.FC_PAR('DESCONTAR ESTAMPILLA PROCIUDADELA', ' ') = 'SI' THEN
                    PCK_NOMINA.CN(610) := CASE WHEN PCK_NOMINA.FC_CN(610) = 0 THEN (PCK_NOMINA.FC_CN(2)) * TO_NUMBER(PCK_PARST.FC_PAR('ESTAMPILLA', '0')) ELSE PCK_NOMINA.FC_CN(610) END;
                    PCK_NOMINA.CN(610) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(610), 0);
                END IF;
            END IF;
        END IF;


        PCK_NOMINA.CN(915) := PCK_NOMINA.GL_SBM;
        PCK_NOMINA.CN(923) := 0;
        PCK_NOMINA.CN(924) := PCK_NOMINA.FC_CN(924);
        PCK_NOMINA.CN(925) := 0;
        PCK_NOMINA.CN(926) := PCK_NOMINA.GL_LICENCIAS;
        PCK_NOMINA.CN(927) := PCK_NOMINA.GL_SBM;
        PCK_NOMINA.CN(928) := 0;
        PCK_NOMINA.CN(97) := PCK_NOMINA.FC_CN(180) + PCK_NOMINA.FC_CN(181) + PCK_NOMINA.FC_CN(182) + PCK_NOMINA.FC_CN(183) + PCK_NOMINA.FC_CN(184) + PCK_NOMINA.FC_CN(185);

        /*PCK_NOMINA.CN(699) := SUMACOND(600, 698) + PCK_NOMINA.FC_SUMACON(600, 698);
        PCK_NOMINA.CN(799) := SUMACONE(700, 749) + PCK_NOMINA.FC_SUMACON(700, 749);
        PCK_NOMINA.CN(140) := SUMACON5();*/
        --(APINEDA:24/10/2019)-Se agregan nuevos conceptos de descuento del 1600 al 1698 para libranzas, se agrega llamado a funciÃ³n nueva FC_SUMACONRANGOS. 
        PCK_NOMINA.CN(699) := PCK_NOMINA_COM4.FC_SUMACONRANGOS(600, 698);
        PCK_NOMINA.CN(799) := PCK_NOMINA.FC_SUMACON(700, 749);
        FOR i IN PCK_NOMINA.CCONCEPTOS.FIRST .. PCK_NOMINA.CCONCEPTOS.LAST LOOP
            IF PCK_NOMINA.CCONCEPTOS(i).CLASE = 5 THEN
                MI_VALORCN140 := MI_VALORCN140 + PCK_NOMINA.FC_CN(i);
            END IF;
        END LOOP;
        PCK_NOMINA.CN(140) := MI_VALORCN140;

        PCK_NOMINA.CN(144) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(97) - PCK_NOMINA.FC_CN(140), 0);

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

    END IF;

    /*IF PCK_NOMINA.GL_SPRC = '99' AND PCK_NOMINA.FC_SUMACON(180, 185) = 0 THEN
        PCK_NOMINA.FC_CN(915) := 0;
        PCK_NOMINA.FC_CN(916) := 0;
        PCK_NOMINA.FC_CN(917) := 0;
        PCK_NOMINA.FC_CN(918) := 0;
        PCK_NOMINA.FC_CN(919) := 0;
        PCK_NOMINA.FC_CN(920) := 0;
        PCK_NOMINA.FC_CN(921) := 0;
        PCK_NOMINA.FC_CN(922) := 0;
        PCK_NOMINA.FC_CN(923) := 0;
        PCK_NOMINA.FC_CN(924) := 0;
        PCK_NOMINA.FC_CN(925) := 0;
        PCK_NOMINA.FC_CN(926) := 0;
        PCK_NOMINA.FC_CN(927) := 0;
        PCK_NOMINA.FC_CN(928) := 0;
    END IF;*/

END PR_CALCULARQUINQUENIOUPC;


FUNCTION FC_APLICA100SENTENCIA543
/*
    NAME              : APLICA100SENTENCIA543
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA PINEDA
    DATE MIGRADOR     : 28/11/2019
    TIME              : 03:38 PM
    SOURCE MODULE     : Nueva
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : TAR1000095945 REALIZA VALIDACIONES DE LA SENTENCIA 543 PARA DETERMINAR SI SE DEJA CALCULO DE BASE PROPORCIONAL AL 100%, EN BASESNOVEDADES NO SE VA A USAR EL PROCEDIMIENTO PR_SENTENCIA_543 PORQUE ALLI LOS CONCEPTOS SE ENCUENTRAN QUEMADOS
  NO USAR EN JAVA PROPIA DEL CALCULO
  */
  (
    UN_TIPOINCAPACIDAD IN VARCHAR2,
    UN_IBL             IN PCK_SUBTIPOS.TI_DOBLE,
    UN_DIAS            IN PCK_SUBTIPOS.TI_ENTERO
  ) RETURN BOOLEAN
  AS 
  MI_APLICA100 BOOLEAN DEFAULT FALSE;
BEGIN
  IF PCK_PARST.FC_PAR('APLICA SENTENCIA 543 DE 2007', '') = 'SI' THEN
    IF (UN_TIPOINCAPACIDAD = '01' OR UN_TIPOINCAPACIDAD = '02') AND ((UN_IBL / 30) * UN_DIAS) * (2 / 3) < ((PCK_PARENTR.PARAMETRO20 / 30) * UN_DIAS) THEN   
        MI_APLICA100 := TRUE;
    END IF;
    IF UN_TIPOINCAPACIDAD = '03' AND ((UN_IBL / 30) * UN_DIAS) < ((PCK_PARENTR.PARAMETRO20 / 30) * UN_DIAS) THEN
        MI_APLICA100 := TRUE;
    END IF;
    IF UN_TIPOINCAPACIDAD = '10' AND ((UN_IBL / 30) * UN_DIAS / 2) < ((PCK_PARENTR.PARAMETRO20 / 30) * UN_DIAS) THEN
        MI_APLICA100 := TRUE;
    END IF; 
  END IF;
  RETURN MI_APLICA100;
END FC_APLICA100SENTENCIA543;

PROCEDURE PR_GUARDAHISTORICOCUNE(
    /*
    NAME              : PR_GUARDAHISTORICOCUNE
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 24/11/2021
    TIME              :
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Guarda los historicos de nómina electrónica
    @NAME:  guardahistoricoCune
    */
     UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_ANIO       IN PCK_SUBTIPOS.TI_ANIO
    ,UN_MES        IN PCK_SUBTIPOS.TI_MES
    ,UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO DEFAULT PCK_CONEXION.FC_GETUSER()
)AS
    MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES             PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
    MI_FILASAFEC           PCK_SUBTIPOS.TI_CAMPOS;

BEGIN   
  
    MI_CONDICION := 'COMPANIA= '''|| UN_COMPANIA ||'''
                 AND ANO = '|| UN_ANIO ||'
                 AND MES = '|| UN_MES ||' ';
    BEGIN
        MI_FILASAFEC := PCK_DATOS.FC_ACME(
           UN_TABLA     => 'HISTORICOS_CUNE'
          ,UN_ACCION    => 'E'
          ,UN_CONDICION => MI_CONDICION);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;

    MI_CAMPOS := 'COMPANIA, ANO, MES, PERIODO, ID_DE_EMPLEADO, CONCEPTO_CUNE, ID_CONCEPTO_CUNE, VALOR, MEDIODEPAGO_NIE065, CREATED_BY, DATE_CREATED';
    MI_VALORES := ' SELECT H.COMPANIA, H.ANO, H.MES, H.PERIODO, H.ID_DE_EMPLEADO, C.CONCEPTO_CUNE, C.ID_DE_CONCEPTO ID_CONCEPTO_CUNE,SUM(VALOR) VALOR, P.MEDIODEPAGO_NIE065, '''|| UN_USUARIO ||''' CREATED_BY, SYSDATE DATE_CREATED
                    FROM HISTORICOS H
                    INNER JOIN CONCEPTOS C
                      ON H.COMPANIA = C.COMPANIA 
                      AND H.ID_DE_CONCEPTO = C.ID_DE_CONCEPTO
                    INNER JOIN PERSONAL P
                      ON H.COMPANIA = P.COMPANIA
                      AND H.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
                    INNER JOIN PERIODOS PE 
                      ON H.COMPANIA = PE.COMPANIA
                      AND H.ID_DE_PROCESO = PE.ID_DE_PROCESO
                      AND H.ANO = PE.ANO
                      AND H.MES = PE.MES 
                      AND H.PERIODO = PE.PERIODO
                    WHERE H.COMPANIA = '''|| UN_COMPANIA ||'''
                      AND H.ANO = '|| UN_ANIO ||'
                      AND H.MES = '|| UN_MES ||'
                      AND C.CLASE IN (3,4,5,6,7,8)
                      AND C.CONCEPTO_CUNE IS NOT NULL
                      AND PE.ACUMULADO NOT IN (0)
                      AND P.GENERACUNE NOT IN (0)
                    GROUP BY H.COMPANIA, H.ANO, H.MES, H.PERIODO, H.ID_DE_EMPLEADO, C.CONCEPTO_CUNE, C.ID_DE_CONCEPTO, P.MEDIODEPAGO_NIE065  
                  ';
    BEGIN
        MI_FILASAFEC := PCK_DATOS.FC_ACME(UN_TABLA   => 'HISTORICOS_CUNE',
                                      UN_ACCION  => 'IS',
                                      UN_CAMPOS  => MI_CAMPOS,
                                      UN_VALORES => MI_VALORES);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;


END PR_GUARDAHISTORICOCUNE;

PROCEDURE PR_ACTNOMINACUNE
    /*
    NAME              : PR_ACTNOMINACUNE
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 21/10/2021
    TIME              : 12:00 PM
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    PARAMETERS        :

    MODIFICATIONS     :

    @NAME:actNominaCune
    @METHOD:  POST
    */
(
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES      IN PCK_SUBTIPOS.TI_MES,
    UN_TIPONOM  IN VARCHAR2,
    UN_CONSEC   IN PCK_SUBTIPOS.TI_ENTERO,
    UN_FECHARPT IN DATE,
    UN_TRM      IN PCK_SUBTIPOS.TI_DOBLE,
    UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO,
    UN_EMPLEADO IN VARCHAR2
)
AS
    MI_RS                 SYS_REFCURSOR;
    MI_CONSECGLOBAL       PCK_SUBTIPOS.TI_ENTERO DEFAULT 1;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_RTASQL             VARCHAR2(100);
    MI_MSG                PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_I                  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_DIASFECHA          PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_FECHAINICIOMES     DATE;
    MI_FECHAFINALMES      DATE;
    MI_RSFECHAINI         DATE;
    MI_RSFECHAFIN         DATE;
    MI_FECHA003           DATE;
    MI_FECHA004           DATE;
    MI_FECHA005           DATE;
    MI_FECHA_DE_RETIRO    PERSONAL.FECHATERCONTRATO%TYPE;
    MI_FECHA_DE_INGRESO   PERSONAL.FECHA_DE_INGRESO%TYPE;    
    MI_ID_DE_EMPLEADO     PERSONAL.ID_DE_EMPLEADO%TYPE;
    MI_CONSECUTIVO        NUMBER(15, 0) DEFAULT 0;
    MI_NIE126             NUMBER(2, 0);
    MI_PORCENTAJE         NOMINA_CUNE.NIE169%TYPE;
    MI_PARENTRADA20       PARAMETROS_DE_ENTRADA.SALARIOMINIMO%TYPE;


    MI_NOMBRE                   PERSONAL.NOMBRECOMPLETO%TYPE;
    MI_APELLIDO1                PERSONAL.APELLIDO1%TYPE;
    MI_APELLIDO2                PERSONAL.APELLIDO2%TYPE;
    MI_NOMBRES                  PERSONAL.NOMBRES%TYPE;
    MI_CODIGOPOSTAL             PERSONAL.CODIGOPOSTAL%TYPE;
    MI_TIPO_VINCULACION_NIE041  PERSONAL.TIPO_VINCULACION_NIE041%TYPE;
    MI_ALTORIESGOPENSION        PERSONAL.ALTORIESGOPENSION%TYPE;
    MI_TIPOCONTRATO_NIE061      PERSONAL.TIPOCONTRATO_NIE061%TYPE;
    MI_MEDIODEPAGO_NIE065       PERSONAL.MEDIODEPAGO_NIE065%TYPE;
    MI_SUBTIPO_NIE042           PERSONAL.SUBTIPO_NIE042%TYPE;
    MI_DIRECCION                PERSONAL.DIRECCION%TYPE;
    MI_TELEFONOS                PERSONAL.TELEFONOS%TYPE;
    MI_ID_DE_TIPO               PERSONAL.ID_DE_TIPO%TYPE;
    MI_CIUDAD_LABORA            PERSONAL.CIUDAD_LABORA%TYPE;
    MI_ESTADO_ACTUAL            PERSONAL.ESTADO_ACTUAL%TYPE;
    MI_MESADA_PENSIONAL         PERSONAL.MESADA_PENSIONAL%TYPE;
    MI_SALARIO_BASE_IBC         PERSONAL.SALARIO_BASE_IBC%TYPE;
    MI_BANCO                    PERSONAL.BANCO%TYPE;
    MI_TIPOCUENTA               PERSONAL.TIPOCUENTA%TYPE;
    MI_CUENTA                   PERSONAL.CUENTA%TYPE;
    MI_RIESGO_PENSION           PERSONAL.RIESGO_PENSION%TYPE;
    MI_DEPARAMENTO_LABORA       PERSONAL.DEPARTAMENTO_LABORA%TYPE;
    MI_TIPONIE044               TIPOS_DOCUMENTOS.TIPONIE044%TYPE; 
    MI_DCTO_IDENTIDAD           TIPOS_DOCUMENTOS.DCTO_IDENTIDAD%TYPE; 
    MI_NOMBREBANCO              BANCOS_NOMINA.NOMBRE%TYPE; 
    MI_SALARIO_BASE             CATEGORIA.SALARIO_BASE%TYPE; 
    MI_DEPARTAMENTO_CUNE        COMPANIA.DEPARTAMENTO%TYPE; 
    MI_CIUDAD_CUNE              COMPANIA.CIUDAD%TYPE;                      
    MI_PORC_SINDICATO           PERSONAL.PORC_SINDICATO%TYPE;
    MI_PORC_SINDICATO2          PERSONAL.PORC_SINDICATO2%TYPE;
    MI_PORC_EPS_EMP             PARAMETROS_DE_ENTRADA.PORC_EMPLEADO_EPS%TYPE;
    MI_PORC_AFP_EMP             PARAMETROS_DE_ENTRADA.PORC_EMPLEADO_AFP%TYPE;
    MI_TIPOVINCULACION          PERSONAL.TIPOVINCULACION%TYPE;
    MI_NIEA192                  NOMINA_CUNE.NIEA192%TYPE;
    MI_STR_CONSULTA             PCK_SUBTIPOS.TI_STRSQL;
    MI_PREFIJODOCNIE010         PARAMETRO.VALOR%TYPE;    
    
    -- SE CREAN VARIABLES PARA AJUSTAR EL FUNCIONAMIENTO
    MI_RS_ANO                    PCK_SUBTIPOS.TI_ANIO; 
    MI_RS_MES                    PCK_SUBTIPOS.TI_MES;
    MI_RS_COMPANIA               PCK_SUBTIPOS.TI_COMPANIA; 
    MI_RS_NUMERO_DCTO            VARCHAR2(100);
    MI_RS_TOTALDIAS              NUMBER;
    MI_RS_NIE009  NUMBER;
    MI_RS_NIE062  NUMBER;
    MI_RS_NIE069  NUMBER;
    MI_RS_NIE070  NUMBER;
    MI_RS_NIE071  NUMBER;
    MI_RS_NIE072  NUMBER;
    MI_RS_NIE073  NUMBER;
    MI_RS_NIE076  NUMBER;
    MI_RS_NIE078  NUMBER;
    MI_RS_NIE081  NUMBER;
    MI_RS_NIE083  NUMBER;
    MI_RS_NIE086  NUMBER;
    MI_RS_NIE088  NUMBER;
    MI_RS_NIE091  NUMBER;
    MI_RS_NIE093  NUMBER;
    MI_RS_NIE096  NUMBER;
    MI_RS_NIE098  NUMBER;
    MI_RS_NIE101  NUMBER;
    MI_RS_NIE103  NUMBER;
    MI_RS_NIE106  NUMBER;
    MI_RS_NIE108  NUMBER;
    MI_RS_NIE111  NUMBER;
    MI_RS_NIE112  NUMBER;
    MI_RS_NIE115  NUMBER;
    MI_RS_NIE116  NUMBER;
    MI_RS_NIE117  NUMBER;
    MI_RS_NIE118  NUMBER;
    MI_RS_NIE119  NUMBER;
    MI_RS_NIE120  NUMBER;
    MI_RS_NIE122  NUMBER;
    MI_RS_NIE125  NUMBER;
    MI_RS_NIE127  NUMBER;
    MI_RS_NIE130  NUMBER;
    MI_RS_NIE131  NUMBER;
    MI_RS_NIE134  NUMBER;
    MI_RS_NIE135  NUMBER;
    MI_RS_NIE138  NUMBER;
    MI_RS_NIE139  NUMBER;
    MI_RS_NIE140  NUMBER;
    MI_RS_NIE141  NUMBER;
    MI_RS_NIE142  NUMBER;
    MI_RS_NIE145  NUMBER;
    MI_RS_NIE147  NUMBER;
    MI_RS_NIE148  NUMBER;
    MI_RS_NIE149  NUMBER;
    MI_RS_NIE150  NUMBER;
    MI_RS_NIE151  NUMBER;
    MI_RS_NIE152  NUMBER;
    MI_RS_NIE153  NUMBER;
    MI_RS_NIE154  NUMBER;
    MI_RS_NIE155  NUMBER;
    MI_RS_NIE156  NUMBER;
    MI_RS_NIE157  NUMBER;
    MI_RS_NIE158  NUMBER;
    MI_RS_NIE159  NUMBER;
    MI_RS_NIE160  NUMBER;
    MI_RS_NIE163  NUMBER;
    MI_RS_NIE166  NUMBER;
    MI_RS_NIE168  NUMBER;
    MI_RS_NIE170  NUMBER;
    MI_RS_NIE172  NUMBER;
    MI_RS_NIE174  NUMBER;
    MI_RS_NIE176  NUMBER;
    MI_RS_NIE177  NUMBER;
    MI_RS_NIE179  NUMBER;
    MI_RS_NIE180  NUMBER;
    MI_RS_NIE181  NUMBER;
    MI_RS_NIE182  NUMBER;
    MI_RS_NIE183  NUMBER;
    MI_RS_NIE184  NUMBER;
    MI_RS_NIE185  NUMBER;
    MI_RS_NIE186  NUMBER;
    MI_RS_NIE187  NUMBER;
    MI_RS_NIE188  NUMBER;
    MI_RS_NIE189  NUMBER;
    MI_RS_NIE193  NUMBER;
    MI_RS_NIE194  NUMBER;
    MI_RS_NIE195  NUMBER;
    MI_RS_NIE196  NUMBER;
    MI_RS_NIE197  NUMBER;
    MI_RS_NIE198  NUMBER;
    MI_RS_NIE190  NUMBER;
    MI_RS_NIE201  NUMBER;
    MI_RS_NIE173  NUMBER;
    MI_RS_CN112   NUMBER; 
    MI_PAGO_RET   NUMBER DEFAULT 0;
BEGIN
    BEGIN
        SELECT  SALARIOMINIMO,
            PORC_EMPLEADO_EPS,
                PORC_EMPLEADO_AFP 
        INTO  MI_PARENTRADA20,
            MI_PORC_EPS_EMP,
                MI_PORC_AFP_EMP
        FROM PARAMETROS_DE_ENTRADA
        WHERE COMPANIA = UN_COMPANIA
          AND ROWNUM = 1;
    EXCEPTION WHEN NO_DATA_FOUND THEN  
        MI_PARENTRADA20 := 1;
    END;

    MI_FECHAINICIOMES := TO_DATE('01/'||UN_MES||'/'||UN_ANO, 'DD/MM/YYYY');
    MI_FECHAFINALMES := TO_DATE(TO_CHAR(LAST_DAY(MI_FECHAINICIOMES), 'DD/MM/YYYY'), 'DD/MM/YYYY');

    MI_CONSECUTIVO := TO_NUMBER(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA ,UN_NOMBRE    => 'CONSECUTIVO DOCUMENTO NIE011'
                                               ,UN_MODULO     => PCK_DATOS.FC_MODULONOMINA() ,UN_FECHA_PAR => SYSDATE));

    MI_PREFIJODOCNIE010 := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA ,UN_NOMBRE    => 'PREFIJO DOCUMENTO NIE010'
                                                ,UN_MODULO     => PCK_DATOS.FC_MODULONOMINA() ,UN_FECHA_PAR => SYSDATE);
   
    --12/09/2024 LJDIAZ
    IF(UN_EMPLEADO IS NULL)THEN
        MI_STR_CONSULTA := 'SELECT  H.ANO, H.MES, H.COMPANIA, P.NUMERO_DCTO, 
            SUM( (CASE WHEN CN.CONCEPTO_CUNE = ''NIE111'' THEN H.VALOR  ELSE 0 END)
                +(CASE WHEN CN.CONCEPTO_CUNE = ''NIE069'' THEN H.VALOR  ELSE 0 END)
                +(CASE WHEN CN.CONCEPTO_CUNE = ''NIE130'' THEN H.VALOR  ELSE 0 END)
                +(CASE WHEN CN.CONCEPTO_CUNE = ''NIE134'' THEN H.VALOR  ELSE 0 END)
                +(CASE WHEN CN.CONCEPTO_CUNE = ''NIE138'' THEN H.VALOR  ELSE 0 END)
                )  TOTALDIAS,
            MAX(P.ID_DE_EMPLEADO) NIE009,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE062'' THEN H.VALOR ELSE 0 END) NIE062,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE069'' THEN H.VALOR ELSE 0 END) NIE069,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE070'' THEN H.VALOR ELSE 0 END) NIE070,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE071'' THEN H.VALOR ELSE 0 END) NIE071,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE072'' THEN H.VALOR ELSE 0 END) NIE072,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE073'' THEN H.VALOR ELSE 0 END) NIE073,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE076'' THEN H.VALOR ELSE 0 END) NIE076,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE078'' THEN H.VALOR ELSE 0 END) NIE078,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE081'' THEN H.VALOR ELSE 0 END) NIE081,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE083'' THEN H.VALOR ELSE 0 END) NIE083,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE086'' THEN H.VALOR ELSE 0 END) NIE086,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE088'' THEN H.VALOR ELSE 0 END) NIE088,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE091'' THEN H.VALOR ELSE 0 END) NIE091,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE093'' THEN H.VALOR ELSE 0 END) NIE093,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE096'' THEN H.VALOR ELSE 0 END) NIE096,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE098'' THEN H.VALOR ELSE 0 END) NIE098,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE101'' THEN H.VALOR ELSE 0 END) NIE101,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE103'' THEN H.VALOR ELSE 0 END) NIE103,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE106'' THEN H.VALOR ELSE 0 END) NIE106,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE108'' THEN H.VALOR ELSE 0 END) NIE108,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE111'' THEN H.VALOR ELSE 0 END) NIE111,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE112'' THEN H.VALOR ELSE 0 END) NIE112,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE115'' THEN H.VALOR ELSE 0 END) NIE115,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE116'' THEN H.VALOR ELSE 0 END) NIE116,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE117'' THEN H.VALOR ELSE 0 END) NIE117,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE118'' THEN H.VALOR ELSE 0 END) NIE118,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE119'' THEN H.VALOR ELSE 0 END) NIE119,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE120'' THEN H.VALOR ELSE 0 END) NIE120,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE122'' THEN H.VALOR ELSE 0 END) NIE122,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE125'' THEN H.VALOR ELSE 0 END) NIE125,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE127'' THEN H.VALOR ELSE 0 END) NIE127,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE130'' THEN H.VALOR ELSE 0 END) NIE130,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE131'' THEN H.VALOR ELSE 0 END) NIE131,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE134'' THEN H.VALOR ELSE 0 END) NIE134,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE135'' THEN H.VALOR ELSE 0 END) NIE135,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE138'' THEN H.VALOR ELSE 0 END) NIE138,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE139'' THEN H.VALOR ELSE 0 END) NIE139,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE140'' THEN H.VALOR ELSE 0 END) NIE140,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE141'' THEN H.VALOR ELSE 0 END) NIE141,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE142''THEN H.VALOR ELSE 0 END) NIE142,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE145'' THEN H.VALOR ELSE 0 END) NIE145,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE147'' THEN H.VALOR ELSE 0 END) NIE147,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE148'' THEN H.VALOR ELSE 0 END) NIE148,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE149'' THEN H.VALOR ELSE 0 END) NIE149,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE150'' THEN H.VALOR ELSE 0 END) NIE150,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE151'' THEN H.VALOR ELSE 0 END) NIE151,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE152'' THEN H.VALOR ELSE 0 END) NIE152,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE153'' THEN H.VALOR ELSE 0 END) NIE153,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE154'' THEN H.VALOR ELSE 0 END) NIE154,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE155'' THEN H.VALOR ELSE 0 END) NIE155,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE156'' THEN H.VALOR ELSE 0 END) NIE156,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE157'' THEN H.VALOR ELSE 0 END) NIE157,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE158'' THEN H.VALOR ELSE 0 END) NIE158,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE159'' THEN H.VALOR ELSE 0 END) NIE159,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE160'' THEN H.VALOR ELSE 0 END) NIE160,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE163'' THEN H.VALOR ELSE 0 END) NIE163,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE166'' THEN H.VALOR ELSE 0 END) NIE166,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE168'' THEN H.VALOR ELSE 0 END) NIE168,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE170'' THEN H.VALOR ELSE 0 END) NIE170,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE172'' THEN H.VALOR ELSE 0 END) NIE172,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE174'' THEN H.VALOR ELSE 0 END) NIE174,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE176'' THEN H.VALOR ELSE 0 END) NIE176,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE177'' THEN H.VALOR ELSE 0 END) NIE177,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE179'' THEN H.VALOR ELSE 0 END) NIE179,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE180'' THEN H.VALOR ELSE 0 END) NIE180,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE181'' THEN H.VALOR ELSE 0 END) NIE181,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE182'' THEN H.VALOR ELSE 0 END) NIE182,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE183'' THEN H.VALOR ELSE 0 END) NIE183,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE184'' THEN H.VALOR ELSE 0 END) NIE184,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE185'' THEN H.VALOR ELSE 0 END) NIE185,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE186'' THEN H.VALOR ELSE 0 END) NIE186,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE187'' THEN H.VALOR ELSE 0 END) NIE187,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE188'' THEN H.VALOR ELSE 0 END) NIE188,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE189'' THEN H.VALOR ELSE 0 END) NIE189,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE193'' THEN H.VALOR ELSE 0 END) NIE193,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE194'' THEN H.VALOR ELSE 0 END) NIE194,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE195'' THEN H.VALOR ELSE 0 END) NIE195,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE196'' THEN H.VALOR ELSE 0 END) NIE196,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE197'' THEN H.VALOR ELSE 0 END) NIE197,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE198'' THEN H.VALOR ELSE 0 END) NIE198,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE190'' THEN H.VALOR ELSE 0 END) NIE190,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE201'' THEN H.VALOR ELSE 0 END) NIE201,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE173'' THEN H.VALOR ELSE 0 END) NIE173,
            SUM(CASE WHEN H.ID_DE_CONCEPTO = 112 THEN H.VALOR ELSE 0 END) CN112   
        FROM HISTORICOS H 
            INNER JOIN PERSONAL P   ON H.COMPANIA = P.COMPANIA AND H.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO 
            INNER JOIN CONCEPTOS CN ON H.COMPANIA = CN.COMPANIA AND H.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO 
            INNER JOIN PERIODOS PER ON H.COMPANIA = PER.COMPANIA AND H.ID_DE_PROCESO = PER.ID_DE_PROCESO AND H.ANO = PER.ANO AND H.MES = PER.MES AND H.PERIODO = PER.PERIODO 
            INNER JOIN COMPANIA CP    ON H.COMPANIA = CP.CODIGO  
        WHERE H.COMPANIA = '''||UN_COMPANIA||'''
          AND H.ANO = '||UN_ANO||'
          AND H.MES = '||UN_MES||'
          AND CN.CONCEPTO_CUNE IS NOT NULL 
          AND PER.ACUMULADO NOT IN (0)
          AND H.ID_DE_PROCESO NOT IN (''99'')
          AND P.GENERACUNE NOT IN (0)
          AND CP.GENERA_CUNE NOT IN (0)
             AND ( '''||UN_TIPONOM||''' != ''T_NE_BASE'' OR NOT EXISTS (
                                                        SELECT  NULL
                                                        FROM    NOMINA_CUNE
                                                        WHERE   COMPANIA = '''||UN_COMPANIA||'''
                                                                AND ANO = '||UN_ANO||'
                                                                AND MES = '||UN_MES||'
                                                                AND NUMERO_DCTO =  P.NUMERO_DCTO
                                                                AND CONS_TIPONOMINA = 1
                                                                AND ENVIADO != 0
                                                        ) 
              )
        GROUP BY  H.ANO, H.MES, H.COMPANIA, P.NUMERO_DCTO 
        ORDER BY P.NUMERO_DCTO'; 
    ELSE
        MI_STR_CONSULTA := 'SELECT  H.ANO, H.MES, H.COMPANIA, P.NUMERO_DCTO, 
            SUM( (CASE WHEN CN.CONCEPTO_CUNE = ''NIE111'' THEN H.VALOR  ELSE 0 END)
                +(CASE WHEN CN.CONCEPTO_CUNE = ''NIE069'' THEN H.VALOR  ELSE 0 END)
                +(CASE WHEN CN.CONCEPTO_CUNE = ''NIE130'' THEN H.VALOR  ELSE 0 END)
                +(CASE WHEN CN.CONCEPTO_CUNE = ''NIE134'' THEN H.VALOR  ELSE 0 END)
                +(CASE WHEN CN.CONCEPTO_CUNE = ''NIE138'' THEN H.VALOR  ELSE 0 END)
                )  TOTALDIAS,
            MAX(P.ID_DE_EMPLEADO) NIE009,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE062'' THEN H.VALOR ELSE 0 END) NIE062,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE069'' THEN H.VALOR ELSE 0 END) NIE069,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE070'' THEN H.VALOR ELSE 0 END) NIE070,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE071'' THEN H.VALOR ELSE 0 END) NIE071,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE072'' THEN H.VALOR ELSE 0 END) NIE072,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE073'' THEN H.VALOR ELSE 0 END) NIE073,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE076'' THEN H.VALOR ELSE 0 END) NIE076,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE078'' THEN H.VALOR ELSE 0 END) NIE078,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE081'' THEN H.VALOR ELSE 0 END) NIE081,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE083'' THEN H.VALOR ELSE 0 END) NIE083,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE086'' THEN H.VALOR ELSE 0 END) NIE086,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE088'' THEN H.VALOR ELSE 0 END) NIE088,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE091'' THEN H.VALOR ELSE 0 END) NIE091,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE093'' THEN H.VALOR ELSE 0 END) NIE093,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE096'' THEN H.VALOR ELSE 0 END) NIE096,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE098'' THEN H.VALOR ELSE 0 END) NIE098,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE101'' THEN H.VALOR ELSE 0 END) NIE101,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE103'' THEN H.VALOR ELSE 0 END) NIE103,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE106'' THEN H.VALOR ELSE 0 END) NIE106,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE108'' THEN H.VALOR ELSE 0 END) NIE108,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE111'' THEN H.VALOR ELSE 0 END) NIE111,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE112'' THEN H.VALOR ELSE 0 END) NIE112,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE115'' THEN H.VALOR ELSE 0 END) NIE115,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE116'' THEN H.VALOR ELSE 0 END) NIE116,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE117'' THEN H.VALOR ELSE 0 END) NIE117,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE118'' THEN H.VALOR ELSE 0 END) NIE118,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE119'' THEN H.VALOR ELSE 0 END) NIE119,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE120'' THEN H.VALOR ELSE 0 END) NIE120,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE122'' THEN H.VALOR ELSE 0 END) NIE122,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE125'' THEN H.VALOR ELSE 0 END) NIE125,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE127'' THEN H.VALOR ELSE 0 END) NIE127,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE130'' THEN H.VALOR ELSE 0 END) NIE130,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE131'' THEN H.VALOR ELSE 0 END) NIE131,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE134'' THEN H.VALOR ELSE 0 END) NIE134,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE135'' THEN H.VALOR ELSE 0 END) NIE135,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE138'' THEN H.VALOR ELSE 0 END) NIE138,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE139'' THEN H.VALOR ELSE 0 END) NIE139,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE140'' THEN H.VALOR ELSE 0 END) NIE140,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE141'' THEN H.VALOR ELSE 0 END) NIE141,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE142''THEN H.VALOR ELSE 0 END) NIE142,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE145'' THEN H.VALOR ELSE 0 END) NIE145,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE147'' THEN H.VALOR ELSE 0 END) NIE147,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE148'' THEN H.VALOR ELSE 0 END) NIE148,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE149'' THEN H.VALOR ELSE 0 END) NIE149,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE150'' THEN H.VALOR ELSE 0 END) NIE150,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE151'' THEN H.VALOR ELSE 0 END) NIE151,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE152'' THEN H.VALOR ELSE 0 END) NIE152,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE153'' THEN H.VALOR ELSE 0 END) NIE153,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE154'' THEN H.VALOR ELSE 0 END) NIE154,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE155'' THEN H.VALOR ELSE 0 END) NIE155,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE156'' THEN H.VALOR ELSE 0 END) NIE156,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE157'' THEN H.VALOR ELSE 0 END) NIE157,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE158'' THEN H.VALOR ELSE 0 END) NIE158,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE159'' THEN H.VALOR ELSE 0 END) NIE159,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE160'' THEN H.VALOR ELSE 0 END) NIE160,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE163'' THEN H.VALOR ELSE 0 END) NIE163,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE166'' THEN H.VALOR ELSE 0 END) NIE166,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE168'' THEN H.VALOR ELSE 0 END) NIE168,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE170'' THEN H.VALOR ELSE 0 END) NIE170,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE172'' THEN H.VALOR ELSE 0 END) NIE172,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE174'' THEN H.VALOR ELSE 0 END) NIE174,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE176'' THEN H.VALOR ELSE 0 END) NIE176,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE177'' THEN H.VALOR ELSE 0 END) NIE177,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE179'' THEN H.VALOR ELSE 0 END) NIE179,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE180'' THEN H.VALOR ELSE 0 END) NIE180,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE181'' THEN H.VALOR ELSE 0 END) NIE181,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE182'' THEN H.VALOR ELSE 0 END) NIE182,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE183'' THEN H.VALOR ELSE 0 END) NIE183,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE184'' THEN H.VALOR ELSE 0 END) NIE184,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE185'' THEN H.VALOR ELSE 0 END) NIE185,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE186'' THEN H.VALOR ELSE 0 END) NIE186,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE187'' THEN H.VALOR ELSE 0 END) NIE187,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE188'' THEN H.VALOR ELSE 0 END) NIE188,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE189'' THEN H.VALOR ELSE 0 END) NIE189,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE193'' THEN H.VALOR ELSE 0 END) NIE193,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE194'' THEN H.VALOR ELSE 0 END) NIE194,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE195'' THEN H.VALOR ELSE 0 END) NIE195,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE196'' THEN H.VALOR ELSE 0 END) NIE196,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE197'' THEN H.VALOR ELSE 0 END) NIE197,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE198'' THEN H.VALOR ELSE 0 END) NIE198,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE190'' THEN H.VALOR ELSE 0 END) NIE190,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE201'' THEN H.VALOR ELSE 0 END) NIE201,
            SUM( CASE WHEN CN.CONCEPTO_CUNE = ''NIE173'' THEN H.VALOR ELSE 0 END) NIE173,
            SUM(CASE WHEN H.ID_DE_CONCEPTO = 112 THEN H.VALOR ELSE 0 END) CN112   
        FROM HISTORICOS H 
            INNER JOIN PERSONAL P   ON H.COMPANIA = P.COMPANIA AND H.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO 
            INNER JOIN CONCEPTOS CN ON H.COMPANIA = CN.COMPANIA AND H.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO 
            INNER JOIN PERIODOS PER ON H.COMPANIA = PER.COMPANIA AND H.ID_DE_PROCESO = PER.ID_DE_PROCESO AND H.ANO = PER.ANO AND H.MES = PER.MES AND H.PERIODO = PER.PERIODO 
            INNER JOIN COMPANIA CP    ON H.COMPANIA = CP.CODIGO  
        WHERE H.COMPANIA = '''||UN_COMPANIA||'''
          AND H.ANO = '||UN_ANO||'
          AND H.MES = '||UN_MES||'
          AND P.NUMERO_DCTO = '''||UN_EMPLEADO||'''
          AND CN.CONCEPTO_CUNE IS NOT NULL 
          AND PER.ACUMULADO NOT IN (0)
          AND H.ID_DE_PROCESO NOT IN (''99'')
          AND P.GENERACUNE NOT IN (0)
          AND CP.GENERA_CUNE NOT IN (0)
             AND ( '''||UN_TIPONOM||''' != ''T_NE_BASE'' OR NOT EXISTS (
                                                        SELECT  NULL
                                                        FROM    NOMINA_CUNE
                                                        WHERE   COMPANIA = '''||UN_COMPANIA||'''
                                                                AND ANO = '||UN_ANO||'
                                                                AND MES = '||UN_MES||'
                                                                AND NUMERO_DCTO =  P.NUMERO_DCTO
                                                                AND CONS_TIPONOMINA = 1
                                                                AND ENVIADO != 0
                                                        ) 
              )
        GROUP BY  H.ANO, H.MES, H.COMPANIA, P.NUMERO_DCTO 
        ORDER BY P.NUMERO_DCTO';
    END IF;
   
   -- OJO AQUI ESTA PROBANDO ---
    <<HISTCUNE>>
    OPEN MI_RS FOR 
        MI_STR_CONSULTA;
    LOOP
        FETCH MI_RS INTO MI_RS_ANO,
                            MI_RS_MES,
                            MI_RS_COMPANIA, 
                            MI_RS_NUMERO_DCTO,
                            MI_RS_TOTALDIAS,
                            MI_RS_NIE009,
                            MI_RS_NIE062,
                            MI_RS_NIE069,
                            MI_RS_NIE070,
                            MI_RS_NIE071,
                            MI_RS_NIE072,
                            MI_RS_NIE073,
                            MI_RS_NIE076,
                            MI_RS_NIE078,
                            MI_RS_NIE081,
                            MI_RS_NIE083,
                            MI_RS_NIE086,
                            MI_RS_NIE088,
                            MI_RS_NIE091,
                            MI_RS_NIE093,
                            MI_RS_NIE096,
                            MI_RS_NIE098,
                            MI_RS_NIE101,
                            MI_RS_NIE103,
                            MI_RS_NIE106,
                            MI_RS_NIE108,
                            MI_RS_NIE111,
                            MI_RS_NIE112,
                            MI_RS_NIE115,
                            MI_RS_NIE116,
                            MI_RS_NIE117,
                            MI_RS_NIE118,
                            MI_RS_NIE119,
                            MI_RS_NIE120,
                            MI_RS_NIE122,
                            MI_RS_NIE125,
                            MI_RS_NIE127,
                            MI_RS_NIE130,
                            MI_RS_NIE131,
                            MI_RS_NIE134,
                            MI_RS_NIE135,
                            MI_RS_NIE138,
                            MI_RS_NIE139,
                            MI_RS_NIE140,
                            MI_RS_NIE141,
                            MI_RS_NIE142,
                            MI_RS_NIE145,
                            MI_RS_NIE147,
                            MI_RS_NIE148,
                            MI_RS_NIE149,
                            MI_RS_NIE150,
                            MI_RS_NIE151,
                            MI_RS_NIE152,
                            MI_RS_NIE153,
                            MI_RS_NIE154,
                            MI_RS_NIE155,
                            MI_RS_NIE156,
                            MI_RS_NIE157,
                            MI_RS_NIE158,
                            MI_RS_NIE159,
                            MI_RS_NIE160,
                            MI_RS_NIE163,
                            MI_RS_NIE166,
                            MI_RS_NIE168,
                            MI_RS_NIE170,
                            MI_RS_NIE172,
                            MI_RS_NIE174,
                            MI_RS_NIE176,
                            MI_RS_NIE177,
                            MI_RS_NIE179,
                            MI_RS_NIE180,
                            MI_RS_NIE181,
                            MI_RS_NIE182,
                            MI_RS_NIE183,
                            MI_RS_NIE184,
                            MI_RS_NIE185,
                            MI_RS_NIE186,
                            MI_RS_NIE187,
                            MI_RS_NIE188,
                            MI_RS_NIE189,
                            MI_RS_NIE193,
                            MI_RS_NIE194,
                            MI_RS_NIE195,
                            MI_RS_NIE196,
                            MI_RS_NIE197,
                            MI_RS_NIE198,
                            MI_RS_NIE190,
                            MI_RS_NIE201,
                            MI_RS_NIE173,
                            MI_RS_CN112 ; 
                  EXIT WHEN MI_RS%NOTFOUND;
        MI_I := MI_I + 1;
         IF UN_TIPONOM <> 'T_NE_BASE' THEN
            MI_CONSECGLOBAL := UN_CONSEC;
        END IF;
        IF MI_I = 1 THEN
            --ELIMINA LOS DATOS DEL PERIODO EN LA TABLA NOMINACUNE
            BEGIN
                BEGIN
                MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || '''
                              AND ANO  = '|| UN_ANO ||'
                              AND MES  = '|| UN_MES ||'
                              AND CONS_TIPONOMINA = '|| MI_CONSECGLOBAL ||'
                              AND ENVIADO = 0 '
                              ;

                MI_RTASQL := PCK_DATOS.FC_ACME (UN_TABLA    => 'NOMINA_CUNE',
                                               UN_ACCION    => 'E',
                                               UN_CONDICION => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERRR_DEL_NOMINACUNE,
                                           UN_REEMPLAZOS => MI_MSG);
            END;
        END IF;

        -- TICKET 7742331 EFCM: EN NOMINAS DE AJUSTE EL NIEA192 CORRESPONDE A LA FECH DE LA ULTIMA NOMINA BASE ENVIADA
        IF UN_TIPONOM = 'T_NE_BASE' THEN
            MI_NIEA192 := SYSDATE;
        ELSE
            SELECT  NVL(MAX(NIE008),SYSDATE)
            INTO    MI_NIEA192
            FROM    NOMINA_CUNE
            WHERE   COMPANIA = UN_COMPANIA
                    AND ANO = UN_ANO
                    AND MES = UN_MES
                    AND NUMERO_DCTO =  MI_RS_NUMERO_DCTO
                    AND CONS_TIPONOMINA = 1;
        END IF;
        -- TICKET 7742331 FIN --

        MI_CAMPOS  := 'COMPANIA, ANO,MES, NUMERO_DCTO, 
                           NIE199,PERIODO,CONS_TIPONOMINA, CREATED_BY, DATE_CREATED,
                           NIE009, NIE062 ,NIE069 ,NIE070 ,NIE071 ,NIE072 ,NIE073 ,
                           NIE076 ,NIE078 ,NIE081 ,NIE083 ,NIE086 ,NIE088 ,NIE091, 
                           NIE093 ,NIE096 ,NIE098 ,NIE101 ,NIE103 ,NIE106 ,NIE108 ,NIE111 ,NIE112,
                           NIE115 ,NIE116 ,NIE117 ,NIE118 ,NIE119 ,NIE120 ,NIE122 ,NIE125 ,
                           NIE127 ,NIE130 ,NIE131 ,NIE134 ,NIE135 ,NIE138 ,NIE139 ,NIE140 ,
                           NIE141 ,NIE142 ,NIE145 ,NIE147 ,NIE148 ,NIE149 ,NIE150 ,NIE151 ,
                           NIE152 ,NIE153 ,NIE154 ,NIE155 ,NIE156 ,NIE157 ,NIE158 ,NIE159 ,
                           NIE160 ,NIE163 ,NIE166 ,NIE168 ,NIE170 ,NIE172 ,NIE174 ,NIE176 ,
                           NIE177 ,NIE179 ,NIE180 ,NIE181 ,NIE182 ,NIE183 ,NIE184 ,NIE185 ,
                           NIE186 ,NIE187 ,NIE188 ,NIE189 ,NIE193 ,NIE194 ,NIE195 ,NIE196 ,
                           NIE197 ,NIE198 ,NIE201 ,NIE173  ';
        MI_VALORES := ' '''     || UN_COMPANIA || ''', '|| UN_ANO || ','  || UN_MES ||', '''|| MI_RS_NUMERO_DCTO ||''',
                        ' || CASE WHEN UN_TIPONOM <> 'T_NE_BASE' THEN -1 ELSE 0 END ||' ,''03'' , '|| MI_CONSECGLOBAL ||', '''|| UN_USUARIO || ''', SYSDATE
                        , '|| MI_RS_NIE009 ||', '|| MI_RS_NIE062 ||' ,'|| MI_RS_NIE069||' ,'|| MI_RS_NIE070||' ,'|| MI_RS_NIE071||' ,'|| MI_RS_NIE072||' ,'|| MI_RS_NIE073||' 
                        ,'|| MI_RS_NIE076||' ,'|| MI_RS_NIE078||' ,'|| MI_RS_NIE081||' ,'|| MI_RS_NIE083||' ,'|| MI_RS_NIE086||' ,'|| MI_RS_NIE088||' ,'|| MI_RS_NIE091||' 
                        ,'|| MI_RS_NIE093||' ,'|| MI_RS_NIE096||' ,'|| MI_RS_NIE098||' ,'|| MI_RS_NIE101||' ,'|| MI_RS_NIE103||' ,'|| MI_RS_NIE106||' ,'|| MI_RS_NIE108||' ,'|| MI_RS_NIE111||' ,'|| MI_RS_NIE112||'
                        ,'|| MI_RS_NIE115  ||','|| MI_RS_NIE116  ||' ,'|| MI_RS_NIE117  ||','|| MI_RS_NIE118  ||' ,'|| MI_RS_NIE119  ||','|| MI_RS_NIE120  ||' ,'|| MI_RS_NIE122  ||','|| MI_RS_NIE125  ||' 
                        ,'|| MI_RS_NIE127  ||','|| MI_RS_NIE130  ||' ,'|| MI_RS_NIE131  ||','|| MI_RS_NIE134  ||' ,'|| MI_RS_NIE135  ||','|| MI_RS_NIE138  ||' ,'|| MI_RS_NIE139  ||','|| MI_RS_NIE140  ||' 
                        ,'|| MI_RS_NIE141  ||','|| MI_RS_NIE142  ||' ,'|| MI_RS_NIE145  ||','|| MI_RS_NIE147  ||' ,'|| MI_RS_NIE148  ||','|| MI_RS_NIE149  ||' ,'|| MI_RS_NIE150  ||'     ,'|| MI_RS_NIE151  ||'
                        ,'|| MI_RS_NIE152  ||' ,'|| MI_RS_NIE153  ||','|| MI_RS_NIE154  ||' ,'|| MI_RS_NIE155  ||','|| MI_RS_NIE156  ||' ,'|| MI_RS_NIE157  ||','|| MI_RS_NIE158  ||' ,'|| MI_RS_NIE159  ||'
                        ,'|| MI_RS_NIE160  ||' ,'|| MI_RS_NIE163  ||','|| MI_RS_NIE166  ||' ,'|| MI_RS_NIE168  ||','|| MI_RS_NIE170  ||' ,'|| MI_RS_NIE172  ||','|| MI_RS_NIE174  ||' ,'|| MI_RS_NIE176  ||'
                        ,'|| MI_RS_NIE177  ||' ,'|| MI_RS_NIE179  ||','|| MI_RS_NIE180  ||' ,'|| MI_RS_NIE181  ||','|| MI_RS_NIE182  ||' ,'|| MI_RS_NIE183  ||' ,'|| MI_RS_NIE184  ||','|| MI_RS_NIE185  ||' 
                        ,'|| MI_RS_NIE186  ||','|| MI_RS_NIE187  ||' ,'|| MI_RS_NIE188  ||','|| MI_RS_NIE189  ||' ,'|| MI_RS_NIE193  ||','|| MI_RS_NIE194  ||' ,'|| MI_RS_NIE195  ||','|| MI_RS_NIE196  ||' 
                        ,'|| MI_RS_NIE197  ||','|| MI_RS_NIE198  ||' ,'|| MI_RS_NIE201  ||'    ,'|| MI_RS_NIE173  ||'  ';

        --VACACIONES
        BEGIN
            SELECT V.INICIO_DISFRUTE, V.FINAL_DISFRUTE
            INTO MI_RSFECHAINI, MI_RSFECHAFIN
            FROM VACACIONES V INNER JOIN PERSONAL P ON V.COMPANIA = P.COMPANIA AND V.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
            WHERE (  TO_DATE(TO_CHAR(V.INICIO_DISFRUTE, 'DD/MM/YYYY'), 'DD/MM/YYYY') BETWEEN  MI_FECHAINICIOMES AND MI_FECHAFINALMES
                  OR TO_DATE(TO_CHAR(V.FINAL_DISFRUTE, 'DD/MM/YYYY'), 'DD/MM/YYYY') BETWEEN  MI_FECHAINICIOMES AND MI_FECHAFINALMES)
              AND P.NUMERO_DCTO = MI_RS_NUMERO_DCTO
              AND ROWNUM = 1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_RSFECHAINI := NULL;
            MI_RSFECHAFIN := NULL;
        END;
        --Revisar si tiene vacaciones
        IF MI_RSFECHAINI IS NOT NULL AND MI_RSFECHAFIN IS NOT NULL THEN
            MI_CAMPOS := MI_CAMPOS || ' ,NIE109, NIE110';
            MI_VALORES := MI_VALORES || ',
                            ''' || CASE WHEN MI_RSFECHAINI >= MI_FECHAINICIOMES AND MI_RSFECHAINI <= MI_FECHAFINALMES THEN MI_RSFECHAINI ELSE NULL END ||''',
                            ''' || CASE WHEN MI_RSFECHAFIN >= MI_FECHAINICIOMES AND MI_RSFECHAFIN <= MI_FECHAFINALMES THEN MI_RSFECHAFIN ELSE NULL END ||'''  ';
        END IF;

        --LICENCIAS
        BEGIN
            SELECT L.FECHA_INICIO , L.FECHA_FINAL
            INTO MI_RSFECHAINI, MI_RSFECHAFIN
            FROM PERSONAL P INNER JOIN LICENCIAS L ON P.COMPANIA = L.COMPANIA AND P.ID_DE_EMPLEADO = L.ID_DE_EMPLEADO
            WHERE (TO_DATE(TO_CHAR(L.FECHA_INICIO, 'DD/MM/YYYY'), 'DD/MM/YYYY') BETWEEN MI_FECHAINICIOMES AND MI_FECHAFINALMES
              OR   TO_DATE(TO_CHAR(L.FECHA_FINAL, 'DD/MM/YYYY'), 'DD/MM/YYYY') BETWEEN MI_FECHAINICIOMES AND MI_FECHAFINALMES) 
              AND P.NUMERO_DCTO = MI_RS_NUMERO_DCTO
              AND ROWNUM = 1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_RSFECHAINI := NULL;
            MI_RSFECHAFIN := NULL;
        END;


        IF MI_RSFECHAINI IS NOT NULL AND MI_RSFECHAFIN IS NOT NULL THEN
            MI_CAMPOS := MI_CAMPOS || ' ,NIE136, NIE137';
            MI_VALORES := MI_VALORES || ',
                        '''|| CASE WHEN MI_RSFECHAINI >= MI_FECHAINICIOMES AND MI_RSFECHAINI <= MI_FECHAFINALMES THEN MI_RSFECHAINI ELSE NULL END ||''',
                        '''|| CASE WHEN MI_RSFECHAFIN >= MI_FECHAINICIOMES AND MI_RSFECHAFIN <= MI_FECHAFINALMES THEN MI_RSFECHAFIN ELSE NULL END ||'''   ';
        END IF;

        --Realiza el insert de NominaCune
        BEGIN
            BEGIN
                MI_RTASQL:= PCK_DATOS.FC_ACME(UN_TABLA    => 'NOMINA_CUNE'
                                    ,UN_ACCION    => 'I'
                                    ,UN_CAMPOS    => MI_CAMPOS
                                    ,UN_VALORES   => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_MSG (1).CLAVE := 'IDENTIFICACION';
            MI_MSG (1).VALOR := MI_RS_NUMERO_DCTO;

            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERRR_INS_NOMINACUNE,
                                       UN_REEMPLAZOS => MI_MSG);
        END;

        BEGIN  --JM CC 4392 CONDICION PARA RETIRADOS QUE LE PAGAN RETRO, SI ALGO SE DAÑA ES CULPA DE LUISA ERAZO!!!  YO QUERIA RECHAZAR ESTE CC 
            SELECT COUNT(*) 
                INTO MI_PAGO_RET 
            FROM HISTORICOS  H
            JOIN PERSONAL P
                ON H.COMPANIA = P.COMPANIA 
                AND H.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO 
            WHERE P.NUMERO_DCTO = MI_RS_NUMERO_DCTO
                AND P.COMPANIA = UN_COMPANIA
                AND H.MES = TO_CHAR(MI_FECHAFINALMES,'MM')
                AND H.ANO = TO_CHAR(MI_FECHAFINALMES,'YYYY')
                AND P.GENERACUNE NOT IN (0)
                AND P.FECHATERCONTRATO IS NOT NULL
                AND P.ESTADO_ACTUAL IN (1,2,3);
        END;    

        BEGIN
            SELECT  MAX(P.FECHATERCONTRATO) FECHA_DE_RETIRO, MIN(P.FECHA_DE_INGRESO) FECHA_DE_INGRESO, MAX(ID_DE_EMPLEADO) ID_DE_EMPLEADO
            INTO MI_FECHA_DE_RETIRO, MI_FECHA_DE_INGRESO, MI_ID_DE_EMPLEADO
            FROM PERSONAL P INNER JOIN COMPANIA C ON P.COMPANIA = C.CODIGO
            WHERE P.COMPANIA = UN_COMPANIA 
              AND P.ID_DE_EMPLEADO <> 0
              AND P.NUMERO_DCTO = MI_RS_NUMERO_DCTO
              AND C.GENERA_CUNE NOT IN (0)
              AND P.GENERACUNE NOT IN (0)
            GROUP BY P.ESTADO_ACTUAL
            HAVING (    (    MAX(P.FECHATERCONTRATO) IS NULL 
                        AND MIN(TO_DATE(TO_CHAR(P.FECHA_DE_INGRESO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) <= MI_FECHAFINALMES
                        AND P.ESTADO_ACTUAL NOT IN (3) ) 
                OR  ( (    MAX(TO_DATE(TO_CHAR(P.FECHATERCONTRATO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) >= MI_FECHAINICIOMES
                        AND MIN(TO_DATE(TO_CHAR(P.FECHA_DE_INGRESO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) <= MI_FECHAFINALMES
                        AND P.ESTADO_ACTUAL IN (1,2,3) )  OR MI_PAGO_RET <> 0 )
                );
        EXCEPTION
          WHEN TOO_MANY_ROWS THEN
                SELECT  MAX(FECHAR) FECHA_DE_RETIRO, MIN(FECHAI) FECHA_DE_INGRESO, MAX(IDE) ID_DE_EMPLEADO
                INTO MI_FECHA_DE_RETIRO, MI_FECHA_DE_INGRESO, MI_ID_DE_EMPLEADO
                FROM (
                        SELECT  MAX(P.FECHATERCONTRATO) FECHAR, MIN(P.FECHA_DE_INGRESO) FECHAI, MAX(ID_DE_EMPLEADO) IDE
                        FROM PERSONAL P INNER JOIN COMPANIA C ON P.COMPANIA = C.CODIGO
                        WHERE P.COMPANIA = UN_COMPANIA 
                          AND P.ID_DE_EMPLEADO <> 0
                          AND P.NUMERO_DCTO = MI_RS_NUMERO_DCTO
                          AND C.GENERA_CUNE NOT IN (0)
                          AND P.GENERACUNE NOT IN (0)
                        GROUP BY P.ESTADO_ACTUAL
                        HAVING (    (    MAX(P.FECHATERCONTRATO) IS NULL 
                                    AND MIN(TO_DATE(TO_CHAR(P.FECHA_DE_INGRESO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) <= MI_FECHAFINALMES
                                    AND P.ESTADO_ACTUAL NOT IN (3) ) 
                            OR ( (    MAX(TO_DATE(TO_CHAR(P.FECHATERCONTRATO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) >= MI_FECHAINICIOMES
                                    AND MIN(TO_DATE(TO_CHAR(P.FECHA_DE_INGRESO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) <= MI_FECHAFINALMES
                                    AND P.ESTADO_ACTUAL IN (1,2,3) ) OR MI_PAGO_RET <> 0)
                            )
                     ); 
          WHEN NO_DATA_FOUND THEN
              MI_ID_DE_EMPLEADO := NULL;
        END;

        IF MI_ID_DE_EMPLEADO IS NOT NULL THEN
            BEGIN
                SELECT P.NOMBRECOMPLETO , P.APELLIDO1, P.APELLIDO2, P.NOMBRES , P.CODIGOPOSTAL , P.TIPO_VINCULACION_NIE041 , P.ALTORIESGOPENSION, 
                       P.TIPOCONTRATO_NIE061 , P.MEDIODEPAGO_NIE065 , P.SUBTIPO_NIE042 , P.DIRECCION , P.TELEFONOS , P.ID_DE_TIPO , 
                       P.CIUDAD_LABORA , P.ESTADO_ACTUAL , P.MESADA_PENSIONAL , P.SALARIO_BASE_IBC ,P.BANCO , P.TIPOCUENTA , 
                       P.CUENTA ,  P.RIESGO_PENSION ,P.DEPARTAMENTO_LABORA , D.TIPONIE044 , D.DCTO_IDENTIDAD , B.NOMBRE , 
                       CAT.SALARIO_BASE , C.DEPARTAMENTO , C.CIUDAD, PORC_SINDICATO, PORC_SINDICATO2
                       ,TIPOVINCULACION
                INTO  MI_NOMBRE,MI_APELLIDO1,MI_APELLIDO2,MI_NOMBRES,MI_CODIGOPOSTAL,MI_TIPO_VINCULACION_NIE041,MI_ALTORIESGOPENSION
                     ,MI_TIPOCONTRATO_NIE061,MI_MEDIODEPAGO_NIE065,MI_SUBTIPO_NIE042,MI_DIRECCION,MI_TELEFONOS,MI_ID_DE_TIPO
                     ,MI_CIUDAD_LABORA,MI_ESTADO_ACTUAL,MI_MESADA_PENSIONAL,MI_SALARIO_BASE_IBC,MI_BANCO,MI_TIPOCUENTA
                     ,MI_CUENTA,MI_RIESGO_PENSION,MI_DEPARAMENTO_LABORA,MI_TIPONIE044,MI_DCTO_IDENTIDAD,MI_NOMBREBANCO
                     ,MI_SALARIO_BASE,MI_DEPARTAMENTO_CUNE,MI_CIUDAD_CUNE, MI_PORC_SINDICATO, MI_PORC_SINDICATO2
                     ,MI_TIPOVINCULACION
                FROM PERSONAL P
                    INNER JOIN COMPANIA C ON P.COMPANIA = C.CODIGO
                    INNER JOIN TIPOS_DOCUMENTOS D ON P.COMPANIA = D.COMPANIA AND P.DCTO_IDENTIDAD = D.DCTO_IDENTIDAD
                    INNER JOIN CATEGORIA CAT ON P.COMPANIA = CAT.COMPANIA AND P.ESCALAFON = CAT.ESCALAFON AND P.ID_DE_CATEGORIA = CAT.ID_DE_CATEGORIA AND P.ANO = CAT.ANO
                    INNER JOIN BANCOS_NOMINA B ON P.COMPANIA = B.COMPANIA AND P.BANCO = B.BANCO
                WHERE P.COMPANIA = UN_COMPANIA 
                  AND P.ID_DE_EMPLEADO = MI_ID_DE_EMPLEADO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_MSG (1).CLAVE := 'CODIGO';
                MI_MSG (1).VALOR := MI_ID_DE_EMPLEADO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERRR_CONS_NOMINACUNE,
                                       UN_REEMPLAZOS => MI_MSG);
            END;

            MI_DIASFECHA := PCK_SYSMAN_UTL.FC_ROUND(TO_NUMBER((CASE WHEN TO_DATE(TO_CHAR(MI_FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') >= TO_DATE(TO_CHAR(MI_FECHAINICIOMES, 'DD/MM/YYYY'), 'DD/MM/YYYY') AND TO_DATE(TO_CHAR(MI_FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')
                            <= TO_DATE(TO_CHAR(MI_FECHAFINALMES, 'DD/MM/YYYY'), 'DD/MM/YYYY') THEN TO_DATE(TO_CHAR(MI_FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') ELSE TO_DATE(TO_CHAR(MI_FECHAFINALMES, 'DD/MM/YYYY'), 'DD/MM/YYYY') END) - TO_DATE(TO_CHAR(MI_FECHA_DE_INGRESO, 'DD/MM/YYYY'), 'DD/MM/YYYY')),0) + 1 ;
            MI_FECHA005 := CASE WHEN TO_DATE(TO_CHAR(MI_FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') >= TO_DATE(TO_CHAR(MI_FECHAINICIOMES, 'DD/MM/YYYY'), 'DD/MM/YYYY') AND TO_DATE(TO_CHAR(MI_FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') <= TO_DATE(TO_CHAR(MI_FECHAFINALMES, 'DD/MM/YYYY'), 'DD/MM/YYYY') THEN TO_DATE(TO_CHAR(MI_FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') ELSE TO_DATE(TO_CHAR(MI_FECHAFINALMES, 'DD/MM/YYYY'), 'DD/MM/YYYY') END;                
            MI_FECHA004 := CASE WHEN MI_FECHA_DE_INGRESO >= MI_FECHAINICIOMES AND MI_FECHA_DE_INGRESO <= MI_FECHAFINALMES THEN TO_DATE(TO_CHAR(MI_FECHA_DE_INGRESO, 'DD/MM/YYYY'), 'DD/MM/YYYY') ELSE TO_DATE(TO_CHAR(MI_FECHAINICIOMES, 'DD/MM/YYYY'), 'DD/MM/YYYY') END;
            MI_FECHA003 := (CASE WHEN MI_FECHA_DE_RETIRO >= MI_FECHAINICIOMES AND MI_FECHA_DE_RETIRO <= MI_FECHAFINALMES THEN TO_DATE(TO_CHAR(MI_FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') ELSE NULL END);

            MI_VALORES := 'NIE002 = TO_DATE(''' || TO_CHAR(MI_FECHA_DE_INGRESO, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                        ,NIE003 = TO_DATE(''' || TO_CHAR(MI_FECHA003, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'') 
                        ,NIE004 = TO_DATE(''' || TO_CHAR(MI_FECHA004, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'') 
                        ,NIE005 = TO_DATE(''' || TO_CHAR(MI_FECHA005, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'') 
                        ,NIE006 = '|| MI_DIASFECHA ||'
                        ,NIE008 = SYSDATE ' || '
                        ,NIEA192 = TO_DATE(''' || TO_CHAR(MI_NIEA192,'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                        ,NIE014 = '''|| MI_DEPARTAMENTO_CUNE ||'''
                        ,NIE015 = '''|| MI_CIUDAD_CUNE ||'''
                        ,NIE041 = '''|| LPAD(MI_TIPO_VINCULACION_NIE041, 2, '0') ||'''
                        ,NIE042 = '''|| MI_SUBTIPO_NIE042 ||'''
                        ,NIE043 = '|| MI_ALTORIESGOPENSION ||'
                        ,NIE044 = '''|| MI_TIPONIE044 ||'''   
                        ';

            -- TICKET 7735126 ECABRERA: SE REPORTA EN LOS CAMPOS NIE161 Y NIE164 LOS PORCENTAJES DESCONTADOS POR SALUD Y PENSION RESPECTIVAMENTE
            MI_VALORES := MI_VALORES || '
                        ,NIE045 = '|| TO_NUMBER(REPLACE(REPLACE(REPLACE(MI_RS_NUMERO_DCTO, '.',''), ',', ''), '-', '')) ||'
                        ,NIE046 = '''|| MI_APELLIDO1 ||'''
                        ,NIE047 = '''|| MI_APELLIDO2 ||'''
                        ,NIE048 = '''|| CASE WHEN INSTR(MI_NOMBRES, ' ') = 0 THEN MI_NOMBRES ELSE SUBSTR(MI_NOMBRES ,1,INSTR(MI_NOMBRES,' ') - 1 ) END ||'''
                        ,NIE049 = '''|| CASE WHEN INSTR(MI_NOMBRES, ' ') = 0 THEN ' ' ELSE SUBSTR(MI_NOMBRES, INSTR(MI_NOMBRES ,' ') + 1 ,LENGTH(MI_NOMBRES)) END ||'''
                        ,NIE051 = '''|| CASE WHEN MI_DEPARAMENTO_LABORA IS NOT NULL THEN MI_DEPARAMENTO_LABORA ELSE MI_DEPARTAMENTO_CUNE END ||'''
                        ,NIE052 = '''|| CASE WHEN MI_CIUDAD_LABORA IS NOT NULL THEN MI_CIUDAD_LABORA ELSE MI_CIUDAD_CUNE END ||'''
                        ,NIE053 = '''|| MI_DIRECCION ||'''
                        ,NIE056 = '|| CASE WHEN MI_ID_DE_TIPO = '02' THEN -1 ELSE 0 END ||'
                        ,NIE061 = '''|| NVL(MI_TIPOCONTRATO_NIE061, '1') ||'''
                        ,NIE062 = '|| CASE WHEN MI_ESTADO_ACTUAL = 2 AND MI_MESADA_PENSIONAL <> 0 THEN MI_MESADA_PENSIONAL ELSE MI_SALARIO_BASE END ||'
                        ,NIE063 = '''|| MI_ID_DE_EMPLEADO   ||'''
                        ,NIE064 = 1
                        ,NIE065 = '''|| MI_MEDIODEPAGO_NIE065 ||'''
                        ,NIE203 = TO_DATE(''' || TO_CHAR(UN_FECHARPT, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')  
                        ,NIE161 = '''|| CASE WHEN MI_ESTADO_ACTUAL = 2 AND MI_RIESGO_PENSION <> 0 THEN MI_RIESGO_PENSION ELSE CASE WHEN MI_TIPOVINCULACION != 23 THEN MI_PORC_EPS_EMP ELSE 0 END END  ||'''
                        ,NIE164 = '''|| CASE WHEN MI_ESTADO_ACTUAL = 2 THEN 0 ELSE CASE WHEN MI_TIPOVINCULACION != 23 THEN MI_PORC_AFP_EMP ELSE 0 END END  ||'''
                        ,NIE171 = CASE WHEN NIE172 <> 0 THEN '|| CASE WHEN MI_PORC_SINDICATO > 0 THEN MI_PORC_SINDICATO ELSE CASE WHEN MI_PORC_SINDICATO2 > 0 THEN MI_PORC_SINDICATO2 ELSE 0 END END ||' ELSE NIE171 END
                         ';
            -- TICKET 7735126 FIN --
            IF MI_MEDIODEPAGO_NIE065 <> '43' AND MI_MEDIODEPAGO_NIE065 <>'92'  THEN --Transferencia CrÃ©dito Bancario
                MI_VALORES := MI_VALORES || ' ,NIE066 = '''|| MI_NOMBREBANCO ||''' 
                       ,NIE067 = '''|| MI_TIPOCUENTA ||'''         
                       ,NIE068 = '''|| MI_CUENTA ||''' ';
            END IF;
        ELSE
            MI_VALORES := 'NIE004 = '''|| MI_FECHA_DE_INGRESO ||'''
                        ,NIE005 = '''|| MI_FECHAFINALMES ||'''
                        ,NIE008 = SYSDATE
                                                  ';
        END IF;
        MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
        MI_VALORES := MI_VALORES || '
                        ,NIE010 =  '''|| MI_PREFIJODOCNIE010 ||'''
                        ,NIE011 = '|| TO_NUMBER(TO_CHAR(UN_ANO || LPAD(UN_MES, 2, '0') || MI_CONSECUTIVO)) ||'
                        ,NIE012 =  '''|| MI_PREFIJODOCNIE010 || TO_CHAR(UN_ANO || LPAD(UN_MES, 2, '0') || MI_CONSECUTIVO) || '''
                        ,NIE013 = ''CO''
                        ,NIE016 = ''es''
                        ,NIE029 = '|| TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA ,UN_NOMBRE    => 'PERIODOS DE NOMINA ELECTRONICA'
                                                              ,UN_MODULO     => PCK_DATOS.FC_MODULONOMINA() ,UN_FECHA_PAR => SYSDATE), '5')) ||'
                        ,NIE030 = ''COP''
                        ,NIE200 = '|| UN_TRM ||'
                        ,NIE050 = ''CO''
                        ,NIE167 = CASE WHEN NIE168 <> 0 THEN 1 ELSE 0 END 
                     ';

        --NIE126
        BEGIN
            SELECT TIPO_INCAPACIDAD.NIE126
            INTO MI_NIE126
            FROM HISTORICOS 
                INNER JOIN PERSONAL 
                    ON HISTORICOS.COMPANIA = PERSONAL.COMPANIA
                    AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                INNER JOIN TIPO_INCAPACIDAD 
                    ON HISTORICOS.COMPANIA = TIPO_INCAPACIDAD.COMPANIA
                    AND HISTORICOS.ID_DE_CONCEPTO = TIPO_INCAPACIDAD.ID_DE_CONCEPTO
                INNER JOIN NOMINA_CUNE 
                    ON PERSONAL.NUMERO_DCTO = NOMINA_CUNE.NUMERO_DCTO
                    AND HISTORICOS.ANO = NOMINA_CUNE.ANO
                    AND HISTORICOS.MES = NOMINA_CUNE.MES
            WHERE PERSONAL.COMPANIA = UN_COMPANIA
              AND PERSONAL.NUMERO_DCTO = MI_RS_NUMERO_DCTO
              AND ROWNUM = 1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_NIE126 := NULL;            
        END;
        IF MI_NIE126 IS NOT NULL THEN
            MI_VALORES := MI_VALORES || ' , NIE126 = '|| MI_NIE126 ||' ';
        ELSE
            MI_VALORES := MI_VALORES || ', NIE126 = CASE WHEN NIE127 <> 0 THEN 1 ELSE NIE126 END';
        END IF;

        --NIE169
        MI_PORCENTAJE := MI_RS_CN112 / MI_PARENTRADA20;
        IF MI_PORCENTAJE > 16 AND MI_PORCENTAJE <= 17 THEN
            MI_PORCENTAJE := 0.2;
        ELSIF MI_PORCENTAJE > 17 AND MI_PORCENTAJE <= 18 THEN
            MI_PORCENTAJE := 0.4;
        ELSIF MI_PORCENTAJE > 18 AND MI_PORCENTAJE <= 19 THEN
            MI_PORCENTAJE := 0.6;
        ELSIF MI_PORCENTAJE > 19 AND MI_PORCENTAJE <= 20 THEN
            MI_PORCENTAJE := 0.8;
        ELSIF MI_PORCENTAJE > 20 THEN
            MI_PORCENTAJE := 1;
        END IF;

        MI_VALORES := MI_VALORES || ',NIE169 = '|| MI_PORCENTAJE;

        BEGIN
            MI_CONDICION := 'COMPANIA        = '''|| UN_COMPANIA||'''
                        AND ANO             = '|| UN_ANO||'
                        AND MES             = '||UN_MES||'
                        AND NUMERO_DCTO     = '''|| MI_RS_NUMERO_DCTO||'''
                        AND CONS_TIPONOMINA = '|| MI_CONSECGLOBAL;   
            BEGIN 
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                                    UN_TABLA     => 'NOMINA_CUNE'
                                                ,UN_ACCION    => 'M'
                                                ,UN_CAMPOS    => MI_VALORES
                                                ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_NOMINA; 
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
            MI_MSG (1).CLAVE := 'IDENTIFICACION';
            MI_MSG (1).VALOR := MI_RS_NUMERO_DCTO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_ACT_NOMINACUNE,
                                    UN_REEMPLAZOS => MI_MSG);
        END;


    END LOOP HISTCUNE;

    --ACTUALIZA EL PARÁMETRO DEL CONSECUTIVO
    BEGIN
        MI_VALORES := 'VALOR = '''|| MI_CONSECUTIVO ||''' ';
        IF (PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA , UN_NOMBRE    => 'UNICO CONSECUTIVO EN NOMINA ELECTRONICA'
                                  ,UN_MODULO     => PCK_DATOS.FC_MODULONOMINA(), UN_FECHA_PAR => SYSDATE) = 'SI') THEN
          MI_CONDICION := 'NOMBRE = ''CONSECUTIVO DOCUMENTO NIE011'' 
                           AND COMPANIA IN (SELECT COMPANIA.CODIGO FROM PARAMETRO
                                        INNER JOIN COMPANIA 
                                                ON PARAMETRO.COMPANIA = COMPANIA.CODIGO
                                             WHERE COMPANIA.GENERA_CUNE <> 0
                                               AND PARAMETRO.NOMBRE = ''PREFIJO DOCUMENTO NIE010''
                                               AND PARAMETRO.VALOR = '''|| MI_PREFIJODOCNIE010 ||''')';  
        ELSE
          MI_CONDICION := 'COMPANIA = '''|| UN_COMPANIA ||''' AND NOMBRE = ''CONSECUTIVO DOCUMENTO NIE011'' ';
        END IF;
        BEGIN 
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                             UN_TABLA     => 'PARAMETRO'
                                            ,UN_ACCION    => 'M'
                                            ,UN_CAMPOS    => MI_VALORES
                                            ,UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_NOMINA; 
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERRR_ACT_PARCONSEC);
    END;

END PR_ACTNOMINACUNE;

FUNCTION FC_ACTESTNOMINACUNE
    /*
    NAME              : FC_ACTESTNOMINACUNE
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDWIN FERNANDO CABRERA MARTINEZ
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : FUNCION PARA LA CONSULTA Y ACTUALIZACION DE ENVIO DE LA NOMINA CUNA
    PARAMETERS        :

    MODIFICATIONS     :
    TICKET            : 7742331
    */
(
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES      IN PCK_SUBTIPOS.TI_MES,
    UN_TIPONOM  IN VARCHAR2,
    UN_CONSEC   IN PCK_SUBTIPOS.TI_ENTERO,
    UN_NRODOC   IN NOMINA_CUNE.NUMERO_DCTO%TYPE,
    UN_OPERACION IN VARCHAR2
) RETURN NUMBER AS
    MI_CONT NUMBER(1);
BEGIN
    -- "COMPANIA", "ANO", "MES", "NUMERO_DCTO", "CONS_TIPONOMINA"
    IF ( UN_OPERACION = 'A' ) THEN
        UPDATE  NOMINA_CUNE
        SET     ENVIADO = -1
        WHERE   COMPANIA = UN_COMPANIA
                AND ANO = UN_ANO
                AND MES = UN_MES
                AND NUMERO_DCTO = UN_NRODOC 
                AND CONS_TIPONOMINA = UN_CONSEC;
                
        RETURN -1;
    ELSE
        BEGIN
            SELECT  DISTINCT (1)
            INTO    MI_CONT
            FROM    NOMINA_CUNE
            WHERE   COMPANIA = UN_COMPANIA
                    AND ANO = UN_ANO
                    AND MES = UN_MES
                    AND NUMERO_DCTO = UN_NRODOC 
                    AND CONS_TIPONOMINA = UN_CONSEC
                    AND ENVIADO != 0
                    ;
                
            RETURN -1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            RETURN 0;
        END;
    END IF;
END FC_ACTESTNOMINACUNE;

FUNCTION FC_REVISIONDATOSCUNE

    /*
    NAME              : FC_REVISIONDATOSCUNE
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 25/10/2019
    TIME              : 03 :13 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Genera plano para revisar datos cune
    @Name: revisionDatosCune
    */

( 
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO              IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES              IN PCK_SUBTIPOS.TI_MES
) RETURN CLOB

AS
    MI_RETORNO            CLOB:= '';
    MI_RAZONSOCIAL        PARAMETROS_DE_ENTRADA.RAZONSOCIAL%TYPE;
    MI_MSG                PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_ETAPA              PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_I                  PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_RS                 SYS_REFCURSOR;
    MI_DATONULO           BOOLEAN;
BEGIN
    BEGIN
        BEGIN
            SELECT RAZONSOCIAL
            INTO MI_RAZONSOCIAL
            FROM PARAMETROS_DE_ENTRADA
            WHERE COMPANIA = UN_COMPANIA
              AND ROWNUM = 1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_ETAPA := 1;
            RAISE PCK_EXCEPCIONES.EXC_NOMINA; 
        END;

        MI_RETORNO := TO_CLOB(PCK_SYSMAN_UTL.FC_PADC(UN_CADENA => MI_RAZONSOCIAL,UN_LONGITUD => 100,UN_CARACTER => ' ')) 
                    || CHR(10) 
                    || CHR(10) 
                    || TO_CLOB(PCK_SYSMAN_UTL.FC_PADC(UN_CADENA => 'Informe de Datos Nulos para la generación de Nómina Electrónica, los cuales debe revisar',UN_LONGITUD => 100,UN_CARACTER => ' '))
                    || CHR(10) 
                    ; 
        
        MI_ETAPA := 2;
        <<TIPOSDOC>>
        FOR MI_RS IN (
            SELECT PERSONAL.NUMERO_DCTO, TIPOS_DOCUMENTOS.TIPONIE044, TIPOS_DOCUMENTOS.DESCRIPCION  
            FROM NOMINA_CUNE 
                INNER JOIN PERSONAL 
                    ON NOMINA_CUNE.COMPANIA = PERSONAL.COMPANIA
                    AND NOMINA_CUNE.NUMERO_DCTO = PERSONAL.NUMERO_DCTO
                INNER JOIN TIPOS_DOCUMENTOS 
                    ON PERSONAL.COMPANIA = TIPOS_DOCUMENTOS.COMPANIA
                    AND PERSONAL.DCTO_IDENTIDAD = TIPOS_DOCUMENTOS.DCTO_IDENTIDAD
            WHERE NOMINA_CUNE.ANO = UN_ANO 
            AND NOMINA_CUNE.MES = UN_MES 
            AND TIPOS_DOCUMENTOS.TIPONIE044 IS NULL
        )
        LOOP
            MI_I := MI_I + 1;
            IF MI_I = 1 THEN 
                MI_RETORNO := MI_RETORNO || PCK_SYSMAN_UTL.FC_PADC(UN_CADENA => '-',UN_LONGITUD => 100,UN_CARACTER => '-')
                                         || CHR(10)
                                         || TO_CLOB(PCK_SYSMAN_UTL.FC_PADC(UN_CADENA => 'Revisión Configuración de Tipos de Documentos',UN_LONGITUD => 100,UN_CARACTER => ' ')) || CHR(10) ;
            END IF;
            MI_RETORNO := MI_RETORNO || TO_CLOB('Falta Configurar en tipo de Documento  '|| MI_RS.NUMERO_DCTO ||' = '|| MI_RS.DESCRIPCION ||'. Reviselos y ajustelos, para continuar con el proceso.') || CHR(10) ;
        END LOOP TIPOSDOC;

        MI_ETAPA := 3;
        MI_RETORNO := MI_RETORNO || CHR(10) || CHR(10);
        MI_I := 0;

        <<DECIMALES>>
        FOR MI_RS IN(
            SELECT H.PERIODO, H.ID_DE_EMPLEADO, H.ID_DE_CONCEPTO, SUM(H.VALOR) AS VALOR, SUM(H.VALOR -FLOOR(H.VALOR -0)) AS VALORDECIMAL, 
                CN.CLASE, CN.CONCEPTO_CUNE, P.NOMBRECOMPLETO
            FROM HISTORICOS  H
                INNER JOIN CONCEPTOS CN
                    ON H.COMPANIA = CN.COMPANIA 
                    AND H.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
                INNER JOIN PERSONAL P
                    ON H.COMPANIA = P.COMPANIA 
                    AND H.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
            WHERE H.COMPANIA = UN_COMPANIA
            AND H.ANO = UN_ANO 
            AND H.MES = UN_MES  
            AND CN.CLASE IN (3,4,5,6,7) 
            AND CN.CONCEPTO_CUNE IS NOT NULL
            GROUP BY H.PERIODO, H.ID_DE_EMPLEADO, H.ID_DE_CONCEPTO, CN.CLASE, CN.CONCEPTO_CUNE, P.NOMBRECOMPLETO
            HAVING SUM(H.VALOR - FLOOR(H.VALOR-0)) <>0
        )
        LOOP
            MI_I := MI_I + 1;
            IF MI_I = 1 THEN 
                MI_RETORNO := MI_RETORNO || PCK_SYSMAN_UTL.FC_PADC(UN_CADENA => '-',UN_LONGITUD => 100,UN_CARACTER => '-')
                                         || CHR(10)
                                         || TO_CLOB(PCK_SYSMAN_UTL.FC_PADC(UN_CADENA => 'Revisión Conceptos con decimales en Históricos, los cuales se deben corregir',UN_LONGITUD => 100,UN_CARACTER => ' ')) || CHR(10) ;
            END IF;
            MI_RETORNO := MI_RETORNO || TO_CLOB('El Empleado '|| MI_RS.NOMBRECOMPLETO ||', con código interno = '|| MI_RS.ID_DE_EMPLEADO ||', Tiene valores con decimales en el concepto: '|| MI_RS.ID_DE_CONCEPTO ||', por valor de : '|| MI_RS.VALORDECIMAL ||' ') || CHR(10) ;
        END LOOP DECIMALES;
        MI_ETAPA := 4;
        
        MI_I := 0;
        <<NULOS>>
        FOR MI_RS IN(
            SELECT P.NOMBRECOMPLETO , P.ID_DE_EMPLEADO, P.ESTADO_ACTUAL, P.CODIGOPOSTAL, P.TIPO_VINCULACION_NIE041, P.SUBTIPO_NIE042, P.ALTORIESGOPENSION, P.TIPOCONTRATO_NIE061, P.MEDIODEPAGO_NIE065 
            FROM HISTORICOS H
                INNER JOIN PERSONAL P 
                    ON H.COMPANIA = P.COMPANIA 
                    AND H.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
                INNER JOIN COMPANIA CP 
                    ON H.COMPANIA = CP.CODIGO 
            WHERE H.COMPANIA = UN_COMPANIA
            AND H.ANO = UN_ANO 
            AND H.MES = UN_MES 
            AND H.ID_DE_CONCEPTO = 97 
            AND CP.GENERA_CUNE NOT IN (0) 
            AND P.GENERACUNE NOT IN (0)
            GROUP BY P.NOMBRECOMPLETO, P.ID_DE_EMPLEADO, P.ESTADO_ACTUAL, P.CODIGOPOSTAL, P.TIPO_VINCULACION_NIE041, P.SUBTIPO_NIE042, P.ALTORIESGOPENSION, P.TIPOCONTRATO_NIE061, P.MEDIODEPAGO_NIE065 
            HAVING SUM(H.VALOR) <> 0
        )
        LOOP
            MI_DATONULO := FALSE;
            MI_I := MI_I + 1;
            IF MI_I = 1 THEN 
                MI_RETORNO := MI_RETORNO || PCK_SYSMAN_UTL.FC_PADC(UN_CADENA => '-',UN_LONGITUD => 100,UN_CARACTER => '-')
                                         || CHR(10)
                                         || TO_CLOB(PCK_SYSMAN_UTL.FC_PADC(UN_CADENA => 'Revisión Datos de Personal falta configurar datos o hay datos nulos',UN_LONGITUD => 100,UN_CARACTER => ' ')) || CHR(10);
            END IF;
            IF MI_RS.TIPO_VINCULACION_NIE041 IS NULL THEN
                MI_RETORNO := MI_RETORNO || TO_CLOB('Empleado '|| MI_RS.NOMBRECOMPLETO ||' = Tipo Vinculación: Campo NIE041')
                            || CHR(10) ;
            END IF;
            IF MI_RS.SUBTIPO_NIE042 IS NULL OR ( MI_RS.SUBTIPO_NIE042 <> '00' AND MI_RS.SUBTIPO_NIE042 <> '01') Then
                MI_RETORNO := MI_RETORNO || TO_CLOB('Empleado '|| MI_RS.NOMBRECOMPLETO ||' = '|| 'SubTipo Vinculación:' ||'Campo NIE042')
                            || CHR(10) ;
                MI_DATONULO := TRUE;
            END IF;
        
            IF MI_RS.TIPOCONTRATO_NIE061 IS NULL THEN
                MI_RETORNO := MI_RETORNO || TO_CLOB('Empleado '|| MI_RS.NOMBRECOMPLETO ||' = TIPOCONTRATO_NIE061: Campo NIE061')
                            || CHR(10) ;
                MI_DATONULO := TRUE;
            END IF;

            IF MI_RS.MEDIODEPAGO_NIE065 IS NULL THEN
                MI_RETORNO := MI_RETORNO || TO_CLOB('Empleado '|| MI_RS.NOMBRECOMPLETO ||' = "MEDIODEPAGO_NIE065: Campo NIE065')
                            || CHR(10) ;
                MI_DATONULO := TRUE;
            END IF;
            IF MI_DATONULO THEN
                MI_RETORNO := MI_RETORNO || CHR(10) ;
            END IF;            
        END LOOP NULOS;

        MI_ETAPA := 5;
        MI_RETORNO := MI_RETORNO || CHR(10) || CHR(10);
        MI_I := 0;

        <<DEVENGOS>>
        FOR MI_RS IN(
            SELECT H.ANO, H.MES, P.NUMERO_DCTO, 
                SUM(CASE WHEN CN.CLASE = 3 THEN H.VALOR ELSE 0 END)  DEVENGOS, 
                SUM(CASE WHEN CN.CLASE=5 THEN H.VALOR ELSE 0 END)  DESCUENTOS, 
                SUM(CASE WHEN CN.CLASE = 3 THEN H.VALOR ELSE 0 END)  - SUM(CASE WHEN CN.CLASE=5 THEN H.VALOR ELSE 0 END)  NETO, 
                SUM(CASE WHEN CN.CLASE=7 THEN H.VALOR ELSE 0 END)  NETOHIS, 
                ROUND(( SUM(CASE WHEN CN.CLASE = 3 THEN H.VALOR ELSE 0 END)- SUM(CASE WHEN CN.CLASE=5 THEN H.VALOR ELSE 0 END) ),0)- ROUND(SUM(CASE WHEN CN.CLASE = 7 THEN H.VALOR ELSE 0 END),0)  DIFER,
                P.NOMBRECOMPLETO
            FROM HISTORICOS H
                INNER JOIN CONCEPTOS CN
                    ON H.COMPANIA = CN.COMPANIA
                    AND H.ID_DE_CONCEPTO = CN.ID_DE_CONCEPTO
                INNER JOIN PERSONAL P 
                    ON H.COMPANIA = P.COMPANIA
                    AND H.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
            WHERE H.COMPANIA = UN_COMPANIA
            AND CN.CLASE IN(3,5,7)
            AND H.ANO = UN_ANO 
            AND H.MES = UN_MES 
            GROUP BY H.ANO, H.MES, P.NUMERO_DCTO, P.NOMBRECOMPLETO
            HAVING ROUND(SUM(CASE WHEN CN.CLASE=3 THEN H.VALOR ELSE 0 END) - SUM(CASE WHEN CN.CLASE =5 THEN H.VALOR ELSE 0 END),0)- ROUND(SUM(CASE WHEN CN.CLASE =7 THEN H.VALOR ELSE 0 END),0) <>0
        )
        LOOP
            MI_I := MI_I + 1;
            IF MI_I = 1 THEN 
                MI_RETORNO := MI_RETORNO || PCK_SYSMAN_UTL.FC_PADC(UN_CADENA => '-',UN_LONGITUD => 100,UN_CARACTER => '-')
                                         || CHR(10)
                                         || TO_CLOB(PCK_SYSMAN_UTL.FC_PADC(UN_CADENA => 'Revisión Diferencias en los TOTALES: DEVENGOS - DESCUENTOS = NETO, Favor revisar antes de generar.',UN_LONGITUD => 100,UN_CARACTER => ' ')) || CHR(10) ;
            END IF;
            MI_RETORNO := MI_RETORNO || TO_CLOB('El Empleado '|| MI_RS.NOMBRECOMPLETO ||', con cédula interno = '|| MI_RS.NUMERO_DCTO ||', Tiene valor Devengos menos descunetos diferente al neto a pagar: '|| MI_RS.DEVENGOS ||' menos '|| MI_RS.DESCUENTOS ||' igual neto: '|| MI_RS.NETO ||', y el neto histórico es:'|| MI_RS.NETOHIS ||', existe diferencia de '|| MI_RS.DIFER ||'') || CHR(10) ;
        END LOOP DEVENGOS;

        RETURN MI_RETORNO;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
                MI_MSG (1).CLAVE := 'ETAPA';
                MI_MSG (1).VALOR := MI_ETAPA;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERRR_ERROR_PLANOCUNE,
                                           UN_REEMPLAZOS => MI_MSG);
    END;
END FC_REVISIONDATOSCUNE;
  
FUNCTION FC_PREPARAPIVOT_CONSFACT
/*
    NAME              : FC_PREPARAPIVOT_CONSFACT
    AUTHORS           : SYSMAN  SAS
    AUTHOR            : JEIMMY CAROLINA ROJAS GUERRERO
    DATE              : 09/02/2023
    DESCRIPTION       : Retorna la cadena del PIVOT que consulta los factores a tener en cuenta para 
                        la liquidacion de las cesantias FNA.
    @NAME             : getPreparaPivotConsFact
    @METHOD           : GET                    
  */
(
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO              IN PCK_SUBTIPOS.TI_ANIO, 
    UN_MES              IN PCK_SUBTIPOS.TI_MES
)
RETURN VARCHAR2
AS
    MI_RS               SYS_REFCURSOR;
    MI_RESULTADO        PCK_SUBTIPOS.TI_STRSQL;  
BEGIN
    BEGIN
        <<RECORREFACTOR>>
        FOR MI_RS IN (
            SELECT DISTINCT CONCEPTOS.ID_DE_CONCEPTO
            FROM HISTORICOS 
            INNER JOIN CONCEPTOS
            ON HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA 
            AND HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO
            INNER JOIN PERIODOS 
            ON HISTORICOS.COMPANIA = PERIODOS.COMPANIA
            AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
            AND HISTORICOS.ANO = PERIODOS.ANO
            AND HISTORICOS.MES = PERIODOS.MES
            AND HISTORICOS.PERIODO = PERIODOS.PERIODO
            WHERE HISTORICOS.COMPANIA = UN_COMPANIA
                AND HISTORICOS.ANO = UN_ANO
                AND HISTORICOS.MES = UN_MES
                AND PERIODOS.ACUMULADO NOT IN(0)
                AND CONCEPTOS.FACTOR_CESANTIASCONSOLIDADAS NOT IN(0)
            ORDER BY CONCEPTOS.ID_DE_CONCEPTO
        )
        LOOP
            MI_RESULTADO:= MI_RESULTADO 
                           || MI_RS.ID_DE_CONCEPTO
                           || ' AS "CN_' || MI_RS.ID_DE_CONCEPTO || '",'  ;   
        END LOOP RECORREFACTOR;
        MI_RESULTADO := SUBSTR(MI_RESULTADO,1,LENGTH(MI_RESULTADO)-1);
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_RESULTADO:='';
    END;
    RETURN MI_RESULTADO;
END FC_PREPARAPIVOT_CONSFACT; 

PROCEDURE PR_PORCENTAJES_FSP_ADI 
/*
  NAME              : PR_PORCENTAJES_FSP_ADI
  AUTHORS           : SYSMAN S.A.S
  AUTHOR MIGRACION  : SEBASTIAN CARDENAS
  DATE MIGRADOR     : 12/05/2025
  TIME              : 
  SOURCE MODULE     : 
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       : 
  --NAME: porcentajesFsp 
  --METHOD:  POST
*/
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
  UN_SALARIOMINIMO  IN NUMBER,
  UN_USUARIO        IN VARCHAR2
)
AS
  MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES       PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION     PCK_SUBTIPOS.TI_CONDICION;
  MI_ERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_ANIO          PCK_SUBTIPOS.TI_ANIO;
  MI_VALOR_MINIMO  NUMBER(20,2);
  MI_VALOR_MAXIMO  NUMBER(20,2);
  
BEGIN
    
    MI_ANIO := UN_ANIO - 1;
    MI_CAMPOS:= 'COMPANIA, ANO, ESTADO, RANGO_INFERIOR, LIMITE_INFERIOR, RANGO_SUPERIOR ,LIMITE_SUPERIOR, PORCENTAJE_FSP, PORCENTAJE_ADICIONALFSP, CREATED_BY, DATE_CREATED';
    MI_VALORES:= 'SELECT COMPANIA,
                         '''||UN_ANIO||''',
                          ESTADO,
                          RANGO_INFERIOR,
                          LIMITE_INFERIOR, 
                          RANGO_SUPERIOR,
                          LIMITE_SUPERIOR, 
                          PORCENTAJE_FSP, 
                          PORCENTAJE_ADICIONALFSP,
                          '''||UN_USUARIO||''',
                          SYSDATE
                  FROM PORCENTAJES_FSP_Y_ADICIONAL 
                   WHERE COMPANIA || ESTADO || LIMITE_INFERIOR || LIMITE_SUPERIOR  NOT IN (SELECT COMPANIA || ESTADO || LIMITE_INFERIOR || LIMITE_SUPERIOR AS DATO FROM PORCENTAJES_FSP_Y_ADICIONAL
                        WHERE COMPANIA = '||UN_COMPANIA||'
                        AND ANO = '||UN_ANIO||')
                        AND ANO = '|| MI_ANIO ||'
                  ORDER BY LIMITE_INFERIOR, LIMITE_SUPERIOR';
    BEGIN
        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'PORCENTAJES_FSP_Y_ADICIONAL',
                                                   UN_ACCION   => 'IS',
                                                   UN_CAMPOS   =>  MI_CAMPOS,
                                                   UN_VALORES  =>  MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_ERROR (1).CLAVE := 'ANIO1'; MI_ERROR (1).VALOR := MI_ANIO;
            MI_ERROR (2).CLAVE := 'ANIO2'; MI_ERROR (2).VALOR := UN_ANIO;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                   UN_REEMPLAZOS => MI_ERROR,
                                   UN_ERROR_COD => PCK_ERRORES.ERR_INS_PORCENTAJES_FSP
                                  );
    END;

    FOR RS IN (SELECT COMPANIA, ANO, ESTADO, LIMITE_INFERIOR, LIMITE_SUPERIOR, PORCENTAJE_FSP, PORCENTAJE_ADICIONALFSP, VALOR_MINIMO, VALOR_MAXIMO FROM PORCENTAJES_FSP_Y_ADICIONAL WHERE COMPANIA = UN_COMPANIA AND ANO = UN_ANIO ORDER BY LIMITE_INFERIOR, LIMITE_SUPERIOR)
    LOOP
      MI_VALOR_MINIMO := 0;
      MI_VALOR_MAXIMO := 0;
      
      IF RS.ESTADO = 'A' THEN
       IF RS.LIMITE_SUPERIOR = 4 THEN
           MI_VALOR_MINIMO := 0;
           MI_VALOR_MAXIMO := ROUND(RS.LIMITE_SUPERIOR * UN_SALARIOMINIMO, 0) - 1;
        ELSE
           MI_VALOR_MINIMO := ROUND(RS.LIMITE_INFERIOR * UN_SALARIOMINIMO, 0);
           IF RS.LIMITE_SUPERIOR >= 25 THEN
                MI_VALOR_MAXIMO := ROUND(RS.LIMITE_SUPERIOR * UN_SALARIOMINIMO, 0);
           ELSE
                MI_VALOR_MAXIMO := ROUND(RS.LIMITE_SUPERIOR * UN_SALARIOMINIMO, 0) - 1;
           END IF;
        END IF;
      
       ELSIF RS.ESTADO = 'P' THEN 
            IF RS.LIMITE_SUPERIOR = 20 THEN
                MI_VALOR_MINIMO := ROUND(RS.LIMITE_INFERIOR * UN_SALARIOMINIMO, 0);
                MI_VALOR_MAXIMO := ROUND(RS.LIMITE_SUPERIOR * UN_SALARIOMINIMO, 0);
            ELSE
                MI_VALOR_MINIMO := ROUND(RS.LIMITE_INFERIOR * UN_SALARIOMINIMO, 0) + 1;
                MI_VALOR_MAXIMO := ROUND(RS.LIMITE_SUPERIOR * UN_SALARIOMINIMO, 0);
            END IF;
       END IF;
      
      BEGIN
        BEGIN
            MI_CAMPOS   := 'VALOR_MINIMO = '''|| MI_VALOR_MINIMO ||''', 
                            VALOR_MAXIMO = '''|| MI_VALOR_MAXIMO ||''',
                            MODIFIED_BY = '''|| UN_USUARIO ||''',
                            DATE_MODIFIED = SYSDATE ';
            MI_CONDICION := 'COMPANIA = '''|| UN_COMPANIA ||'''
                             AND ANO = '''|| UN_ANIO ||'''
                             AND ESTADO = '''|| RS.ESTADO ||'''
                             AND LIMITE_INFERIOR = '''|| RS.LIMITE_INFERIOR ||'''
                             AND LIMITE_SUPERIOR = '''|| RS.LIMITE_SUPERIOR ||''' ';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'PORCENTAJES_FSP_Y_ADICIONAL',
                                                  UN_ACCION     => 'M',
                                                  UN_CAMPOS     => MI_CAMPOS,
                                                  UN_CONDICION  => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
        MI_ERROR (1).CLAVE := 'ANIO'; MI_ERROR (1).VALOR := UN_ANIO;
        MI_ERROR (2).CLAVE := 'ESTADO'; MI_ERROR (2).VALOR := RS.ESTADO;
        MI_ERROR (3).CLAVE := 'LIMITE_INFERIOR'; MI_ERROR (3).VALOR := RS.LIMITE_INFERIOR;
        MI_ERROR (4).CLAVE := 'LIMITE_SUPERIOR'; MI_ERROR (4).VALOR := RS.LIMITE_SUPERIOR;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                                   UN_REEMPLAZOS => MI_ERROR,
                                   UN_ERROR_COD => PCK_ERRORES.ERR_ACT_PORCENTAJES_FSP
                                   );
    END;             
    
    END LOOP;
END PR_PORCENTAJES_FSP_ADI;

PROCEDURE PR_CALCPRIMSEMES_CARDER(
/*
    NAME              : PR_CALCPRIMSEMES_CARDER
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO
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
                -- 'REVISAR PCK_NOMINA.GL_DOCEAVAS CAUSADAS, YA QUE NO CUMPLIO MÁS DE 6 MESES RADICADO 201520160102642 01/06/15  A: ' and PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 and ' ' and PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 and ' ' and PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES
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
END PR_CALCPRIMSEMES_CARDER;

PROCEDURE PR_CALPRIMNAV_CARDER(
/*
    NAME              : PR_CALPRIMNAV_CARDER
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO
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
END PR_CALPRIMNAV_CARDER;

PROCEDURE PR_CALCPRIMVAC_CARDER
/*
	NAME              : PR_CALCPRIMVAC_CARDER
	AUTHORS           : SYSMAN  SAS
	AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO
*/
AS
    MI_BONPAGADA     PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_FECHA         DATE;
    MI_VALOR         NUMBER DEFAULT 0;
BEGIN
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11' THEN
        PCK_NOMINA.CN(174) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CN(160) / 12), 0) / 2;
    ELSE
        --DC := 0;
        PCK_NOMINA.GL_DIASVAC := 0;
        PCK_NOMINA.GL_DIASPENDIENTES := 0;
        PCK_NOMINA.GL_PENDIENTES := 0;
        PCK_NOMINA.GL_LICENCIAS := 0;
        PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
        PCK_NOMINA.GL_DINEROPROPORCIONAL := 0;
        PCK_NOMINA.GL_FECHAUV := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL END;
        IF PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.GL_SPER = 8 THEN
            PCK_NOMINA.CN(984) := 0;
            IF PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) <> 0 THEN
                PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(160)) / 12, 0);
                IF (PCK_NOMINA.GL_SMES = 7 OR PCK_NOMINA.GL_SMES = 6) AND (PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) <> 0 AND PCK_NOMINA.FC_CN(160) <> 0) AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 160, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    IF PCK_NOMINA.FC_CN(160) = 0 AND PCK_NOMINA.FC_CN(160) > 0 THEN
                        PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160)) / 12, 0);
                    ELSIF PCK_NOMINA.FC_CNP(160) > 0 AND PCK_NOMINA.FC_CN(160) > 0 THEN
                        PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(160) + PCK_NOMINA.FC_CN(160)) / 12, 0);
                    END IF;
                    IF PCK_NOMINA.FC_CN(981) = 0 THEN
                        PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(160)) / 12, 0);
                    END IF;
                END IF;
            ELSE
                PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160)) / 12, 0);
            END IF;
            MI_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0);
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, (PCK_NOMINA.GL_SANO), PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            --(APINEDA:31/07/2019)-Se agrega NVL a LICENCIAS para que no se presenten inconsistencias con empleados nuevos
            PCK_NOMINA.GL_LICENCIAS := NVL(((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.FC_CNA(339)) +
            (
              CASE
              WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                PCK_NOMINA.FC_CESANTIA(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 'L')
              ELSE
                0
              END) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION),0);
            --(APINEDA:31/07/2019)- TAR 1000092350 Se restan días a disfrutar de vacaciones pendientes CNA(99)
            PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91) - PCK_NOMINA.FC_CNA(99);
            PCK_NOMINA.GL_DTV            := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
            (
              (
                CASE
                WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                  PCK_NOMINA.GL_FECHAI
                ELSE
                  --(APINEDA:05/03/2019)-Se ajusta condición para beneficios respecto a la fecha de inicio de vacaciones TAR 1000090701
                  (CASE WHEN (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1) BETWEEN PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.GL_FECHAFIN1 THEN PCK_NOMINA.GL_FECHAINI ELSE (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1) END)
                END), PCK_NOMINA.GL_FECHAFIN1)                    - PCK_NOMINA.GL_LICENCIAS;
            --(APINEDA:31/07/2019)-Se estaba restando dos veces la licencia
            PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.GL_DTV;
            PCK_NOMINA.GL_PERIODOS := TRUNC(PCK_NOMINA.GL_DTV / 360);
            IF (PCK_NOMINA.GL_DTV - (360 * PCK_NOMINA.GL_PERIODOS)) >= 315 THEN
                PCK_NOMINA.GL_PERIODOS := PCK_NOMINA.GL_PERIODOS + 1;
            END IF;
            PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2);
            PCK_NOMINA.CN(93) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) * CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END, 2);
            IF PCK_NOMINA.GL_DIASVAC = 0 THEN
                PCK_NOMINA.GL_PERIODOS := CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END;
                PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END / 360 * PCK_NOMINA.GL_DTV, 0);
            END IF;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_ANOA), PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.GL_DTV = 0 THEN
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
            ELSE
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
            END IF;
            PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA) * 15 / 30 / 360 * PCK_NOMINA.GL_DTV, 0) ELSE PCK_NOMINA.FC_CN(155) END;
            ELSE
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
                PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0) / 30 * 15 / 360 * PCK_NOMINA.GL_DTV, 0) ELSE PCK_NOMINA.FC_CN(155) END;
            END IF;
            IF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '01' THEN
                --(APINEDA:31/07/2019)-TAR1000087154 Se eliminan líneas que estan asignando valor inconsistente a los días trabajados
                IF PCK_NOMINA.GL_SPRC = 99 THEN
                    PCK_NOMINA.GL_RTA := 7;
                END IF;
                /*IF PCK_NOMINA.GL_RTA = 0 OR PCK_NOMINA.GL_RTA IS NULL THEN
                PCK_NOMINA.GL_RTA := MSGBOX('SE ESTÿ CALCULANDO VACACIONES A UN RETIRADO, DESEA CONTARLE LOS SÿBADOS COMO DÿA HÿBIL PARA VACACIONES AL EMPLEADO ' CPERSONAL(PCK_NOMINA.P).APELLIDO1 and ' ' CPERSONAL(PCK_NOMINA.P).APELLIDO2 and ' ' CPERSONAL(PCK_NOMINA.P).NOMBRES, VBYESNO, 'SYSMAN SOFTWARE');
                END IF;*/
                PCK_NOMINA.GL_FECHAFF := '';
                PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') - 1, NVL(PCK_NOMINA.GL_DIASVAC, 1));
                IF PCK_NOMINA.GL_SPER = 8 AND PCK_NOMINA.GL_SMES = 12 THEN
                    PCK_NOMINA.GL_PERIODOS := 1;
                    PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
                    PCK_NOMINA.CN(93) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) * PCK_NOMINA.GL_PERIODOS, 2);
                    PCK_NOMINA.GL_DIASVAC := CASE WHEN PCK_NOMINA.GL_DIASVAC = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2) ELSE PCK_NOMINA.GL_DIASVAC END;
                    PCK_NOMINA.GL_DIASVAC := TRUNC(PCK_NOMINA.GL_DIASVAC / 360 * PCK_NOMINA.GL_DTV);
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_FECHAFIN + 1, PCK_NOMINA.GL_DIASVAC);
                END IF;
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    IF PCK_NOMINA.GL_RTA = 6 THEN
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := TO_NUMBER(TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') - PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC, 0)) + 1;
                        PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                    ELSE
                        PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END, PCK_NOMINA.GL_FECHAFIN1) > 315 THEN
                            MI_FECHA := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO;
                            --MI_FECHA := PCK_NOMINA.GL_FECHAFF1;
                            PCK_NOMINA.CN(96) := TO_NUMBER(PCK_NOMINA.GL_FECHAFF1 - MI_FECHA) + 1;
                            PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                        END IF;
                        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
                            PCK_NOMINA.CN(96) := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
                            PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                        END IF;
                    END IF;
                END IF;
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                    PCK_NOMINA.CN(96) := TO_NUMBER(PCK_NOMINA.GL_FECHAFF - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
                    PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                END IF;
                PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2);
                PCK_NOMINA.CN(164) := PCK_NOMINA.GL_PERIODOS;
                PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0);
                IF PCK_NOMINA.GL_SPRC = '99' THEN
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175)    ;
                ELSE
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.GL_PENDIENTES;
                END IF;
                IF PCK_NOMINA.GL_SPER = 8 AND PCK_NOMINA.GL_SMES = 12 THEN
                    PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12, 0);
                    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                        PCK_NOMINA.CN(155) := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538)) / 12) * PCK_NOMINA.FC_CN(93) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
                    ELSE
                        PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(543)) / 12, 0);
                        PCK_NOMINA.CN(155) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(543)) / 12, 0) * PCK_NOMINA.FC_CN(93) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
                    END IF;
                    PCK_NOMINA.CN(964) := PCK_NOMINA.GL_DIASPENDIENTES;
                    PCK_NOMINA.GL_DIASPROPORCIONAL := PCK_NOMINA.GL_DTV;
                END IF;
                PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END;
            ELSIF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.GL_DTV := PCK_NOMINA.GL_DTV / 30 * 15 / 360;
                --PCK_NOMINA.GL_RTA := MSGBOX('SE ESTÿ CALCULANDO VACACIONES A UN RETIRADO, DESEA CONTARLE LOS SÿBADOS COMO DÿA HÿBIL PARA VACACIONES AL EMPLEADO ' CPERSONAL(PCK_NOMINA.P).APELLIDO1 and ' ' CPERSONAL(PCK_NOMINA.P).APELLIDO2 and ' ' CPERSONAL(PCK_NOMINA.P).NOMBRES, VBYESNO, 'SYSMAN SOFTWARE');
                PCK_NOMINA.GL_FECHAFF := '';
                PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
                PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_DTV / 360, 0);
                PCK_NOMINA.CN(164) := CASE WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
                PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    IF PCK_NOMINA.GL_RTA = 6 THEN
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                    ELSE
                        PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                    END IF;
                END IF;
                IF PCK_NOMINA.FC_CN(96) = 0 THEN
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                    PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                END IF;
                PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0);
                IF PCK_NOMINA.GL_SPRC = '99' THEN
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175)    ;
                ELSE
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.GL_PENDIENTES;
                END IF;
                PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END;
            END IF;
            IF (PCK_NOMINA.GL_SPER = 5 OR PCK_NOMINA.GL_SPER = 7) OR PCK_NOMINA.FC_CN(404) <> 0 THEN
                PCK_NOMINA.CN(68) := 0;
                PCK_NOMINA.CN(93) := CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END;
                IF PCK_NOMINA.FC_CN(155) > 1 OR PCK_NOMINA.FC_CN(155) = 0 THEN
                    IF PCK_NOMINA.GL_DIASPROP > 315 THEN
                        PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.GL_DIASPROP - 360;
                        PCK_NOMINA.GL_DIASPROP := CASE WHEN PCK_NOMINA.GL_DIASPROP < 0 THEN 0 ELSE PCK_NOMINA.GL_DIASPROP END;
                    END IF;
                    IF PCK_NOMINA.GL_DIASPROP >= 0 AND ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06') AND PCK_NOMINA.FC_CN(155) = 0) THEN
                        PCK_NOMINA.CN(68) := 15;
                        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                            PCK_NOMINA.CN(155) := PCK_NOMINA.FC_CN(155) + PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_GRPNGV + MI_BONPAGADA + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160)) / 12) * PCK_NOMINA.FC_CN(68) / 30 / 360 * PCK_NOMINA.GL_DIASPROP, 0);
                        END IF;
                    END IF;
                END IF;
                IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'PV' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'LN' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'SN' THEN
                    --(APINEDA:31/07/2019)-Se eliminan asignaciones de valor repetidas a la variable GL_DTV
                    PCK_NOMINA.GL_DIASVAC := TRUNC(15 * PCK_NOMINA.GL_DTV / 360) + PCK_NOMINA.GL_DIASPENDIENTES;
                    PCK_NOMINA.CN(164) := CASE WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
                    PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, CASE WHEN PCK_NOMINA.GL_DIASVAC <= 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END);
                    PCK_NOMINA.CN(96) := 0;
                    IF PCK_NOMINA.FC_CN(96) = 0 THEN
                        IF PCK_NOMINA.GL_RTA = 6 THEN
                            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                            PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                        ELSE
                            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
                                PCK_NOMINA.CN(96) := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
                            END IF;
                            PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                            PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL AND PCK_NOMINA.FC_CN(96) = 0 THEN
                                PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_FECHAFIN + 1, PCK_NOMINA.GL_DIASVAC);
                                PCK_NOMINA.CN(96) := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
                                PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                            END IF;
                        END IF;
                    END IF;
                    PCK_NOMINA.CN(96) := CASE WHEN PCK_NOMINA.FC_CN(96) = 1 THEN 0 ELSE PCK_NOMINA.FC_CN(96) END;
                    PCK_NOMINA.GL_DIASVAC := CASE WHEN PCK_NOMINA.GL_DIASVAC = 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
                    IF PCK_NOMINA.FC_CN(96) < 0 THEN
                        PCK_NOMINA.CN(96) := 0;
                    END IF;
                    IF PCK_NOMINA.FC_CN(96) = 0 THEN
                        PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
                        PCK_NOMINA.CN(96) := TO_NUMBER(PCK_NOMINA.GL_FECHAFF - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
                        PCK_NOMINA.CN(96) := CASE WHEN PCK_NOMINA.FC_CN(96) = 1 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO <> PCK_NOMINA.GL_FECHAFF AND PCK_NOMINA.GL_DIASVAC < 1 THEN 0 ELSE PCK_NOMINA.FC_CN(96) END;
                        IF PCK_NOMINA.FC_CN(96) < 0 THEN
                            PCK_NOMINA.CN(96) := 0;
                        END IF;
                    END IF;
                    IF PCK_NOMINA.GL_DTV < 24 AND PCK_NOMINA.FC_CN(96) <= 1 THEN
                        PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)), 3), 0);
                        PCK_NOMINA.CN(96) := PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)), 3);
                    ELSE
                        --(APINEDA:31/07/2019)-Se modifica sección para el cálculo de vacaciones proporcionales en retiro.
                        --(MZANGUNA:19/12/2019)
                        PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND((15 * PCK_NOMINA.GL_DTV / 360) + PCK_NOMINA.GL_DIASPENDIENTES,2);
                        --PCK_NOMINA.CN(96)  := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABILDIAS30(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, TRUNC(PCK_NOMINA.GL_DIASVAC)));
                        PCK_NOMINA.CN(96)  := PCK_SYSMAN_UTL.FC_DIASFEBREROYMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, TRUNC(PCK_NOMINA.GL_DIASVAC)));
                        MI_VALOR :=  PCK_NOMINA.FC_CN(96);
                        MI_VALOR :=  PCK_SYSMAN_UTL.FC_ROUND((15 * PCK_NOMINA.GL_DTV / 360) - TRUNC(PCK_NOMINA.GL_DIASVAC), 2);
                        MI_VALOR :=  PCK_NOMINA.GL_DTV;
                        MI_VALOR := PCK_NOMINA.GL_DIASVAC;
                        PCK_NOMINA.CN(96)  := PCK_NOMINA.FC_CN(96) + PCK_SYSMAN_UTL.FC_ROUND((15 * PCK_NOMINA.GL_DTV / 360) - TRUNC(PCK_NOMINA.GL_DIASVAC), 2);
                        PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)) - PCK_NOMINA.GL_DIASVAC, 2), 0);
                    END IF;
                    IF PCK_NOMINA.GL_SPRC = '99' AND (PCK_NOMINA.FC_CN(175) = 0 OR PCK_NOMINA.FC_CN(175) IS NULL) AND PCK_NOMINA.FC_CN(155) > 0 THEN
                        PCK_NOMINA.CN(96) := CASE WHEN PCK_NOMINA.FC_CN(96) IS NULL THEN 21 ELSE PCK_NOMINA.FC_CN(96) END;
                        PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
                    END IF;
                END IF;
            END IF;
            PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END;
            PCK_NOMINA.CN(982) := MI_BONPAGADA;
            PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
        ELSE
            PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
            PCK_NOMINA.CN(984) := 0;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91);
            /*IF PCK_NOMINA.GL_DIASPENDIENTES > 0 THEN
                ALERTA 'EL EMPLEADO ' CPERSONAL(PCK_NOMINA.P).APELLIDO1 and ' ' CPERSONAL(PCK_NOMINA.P).NOMBRES and ', TIENE ' GL_DIASPENDIENTES and ' DIAS PCK_NOMINA.GL_PENDIENTES DE VACACIONES.' and ', CÉDULA NO.' CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO and ',TIPO: ' CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO
            END IF;*/
            PCK_NOMINA.CN(93) := PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164) ;
            MI_BONPAGADA := 0;
            IF PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 12 THEN
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF PCK_NOMINA.FC_CNA(150) > 0 THEN
                    MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                ELSE
                    MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                END IF;
            ELSE
                MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            END IF;
            IF MI_BONPAGADA = 0 AND PCK_NOMINA.FC_CN(150) > 0 AND PCK_NOMINA.GL_SPER <= 3 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INICIO_DISFRUTE > PCK_NOMINA.GL_FECHAFIN THEN
                MI_BONPAGADA := PCK_NOMINA.FC_CN(150);
            END IF;
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + (MI_BONPAGADA / 12), 0), 0);
            ELSE
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA / 12, 0), 0)   ;
                PCK_NOMINA.GL_FACTORESPV1 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + (MI_BONPAGADA) / 12, 0), 0);
            END IF;
            PCK_NOMINA.CN(982) := PCK_SYSMAN_UTL.FC_ROUND((MI_BONPAGADA) / 12, 0);
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' THEN
                IF PCK_NOMINA.FC_CN(403) <> 0 THEN
                    IF PCK_NOMINA.CPARENTRADA(1).NIT = '891855138-1' THEN -- 06/04/2021: JORDUZ Se agrega validacion donde se lleve las primas calculadas y sus retroctivos segun TAR  1000104799
                    PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + ((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514))/12) + ((PCK_NOMINA.FC_CNA(160)+PCK_NOMINA.FC_CNA(503))/12))  / 30 * PCK_NOMINA.FC_CN(94), 0) ELSE PCK_NOMINA.FC_CN(174) END;
                    ELSE
                    PCK_NOMINA.CN(174) :=CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0) ELSE PCK_NOMINA.FC_CN(174) END;
                    END IF;
                END IF;
                IF PCK_NOMINA.FC_CN(419) <> 0 THEN
                    PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0) ELSE PCK_NOMINA.FC_CN(175) END;
                END IF;
                IF PCK_NOMINA.CPARENTRADA(1).NIT = '891855138-1' THEN -- 06/04/2021: JORDUZ Se agrega validacion donde se lleve las primas calculadas y sus retroctivos segun TAR  1000104799
                PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + ((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514))/12) + ((PCK_NOMINA.FC_CNA(160)+PCK_NOMINA.FC_CNA(503))/12))  / 30 * PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164), 0) ELSE PCK_NOMINA.FC_CN(155) END;
                ELSE PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164), 0) ELSE PCK_NOMINA.FC_CN(155) END;
                END IF;
            ELSE
                IF PCK_NOMINA.FC_CN(403) <> 0 THEN
                    PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0) ELSE PCK_NOMINA.FC_CN(174) END;
                END IF;
                IF PCK_NOMINA.FC_CN(419) <> 0 THEN
                    PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0) ELSE PCK_NOMINA.FC_CN(175) END;
                END IF;
            END IF;
        END IF;
    END IF;
    PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
    PCK_NOMINA.CN(960) := PCK_NOMINA.GL_FACTORESPV ;
    IF PCK_NOMINA.GL_SPRC = '99' THEN
        PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(93)     ;
        PCK_NOMINA.CN(962) := PCK_NOMINA.GL_PERIODOS    ;
    ELSE
        PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(94)     ;
        PCK_NOMINA.CN(962) := PCK_NOMINA.FC_CN(164)    ;
    END IF;
    PCK_NOMINA.CN(963) := PCK_NOMINA.GL_LICENCIAS  ;
    PCK_NOMINA.CN(964) := PCK_NOMINA.GL_DIASPENDIENTES;
    PCK_NOMINA.CN(965) := PCK_NOMINA.GL_PRIMAPROPORCIONAL;
    PCK_NOMINA.CN(966) := PCK_NOMINA.GL_DINEROPROPORCIONAL;
    PCK_NOMINA.CN(967) := PCK_NOMINA.GL_PENDIENTES;
    PCK_NOMINA.CN(968) := PCK_NOMINA.GL_DIASPROPORCIONAL;
    PCK_NOMINA.CN(975) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    PCK_NOMINA.CN(976) := 0;
    PCK_NOMINA.CN(977) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(978) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(979) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(980) := PCK_NOMINA.GL_VPT;
    PCK_NOMINA.CN(982) := PCK_SYSMAN_UTL.FC_ROUND((MI_BONPAGADA) / 12, 0);
    PCK_NOMINA.CN(983) := 0;
    PCK_NOMINA.CN(984) := PCK_NOMINA.FC_VALORULTIMOQUINQUENIO(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
    IF PCK_NOMINA.GL_SMES = '12' AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) AND PCK_NOMINA.FC_CN(155) > 0 AND UPPER(PCK_PARST.FC_PAR('RELIQUIDAR PRIMA DE NAVIDAD EN DICIEMBRE CON VACACIONES', 'NO')) = 'SI' THEN
        PCK_NOMINA.CN(402) := 1;
    END IF;
    PCK_NOMINA.GL_PV_BASE := PCK_NOMINA.GL_FACTORESPV;
    PCK_NOMINA.GL_PV_DIAS := PCK_NOMINA.GL_DIASPROP;
    PCK_NOMINA.GL_VAC_DIAS := PCK_NOMINA.GL_DTV;
END PR_CALCPRIMVAC_CARDER;

FUNCTION FC_ACUMCONCEPTO_RETRO--(CC:3708_CFBARRERA_INI)
/*
    NAME              : FC_ACUMCONCEPTO_RETRO  
    AUTHORS           : CFBARRERAO
    DATE MIGRADOR     : 24/03/2026
    DESCRIPTION       : Carga el Type CNP_RETRO con el valor del concepto que se envia de resto lo pone en cero, retornando el número de periodos
                        SE AJUSTA AL ESTANDAR.
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CONCEPTO     IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
    UN_ANO1         IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES1         IN PCK_SUBTIPOS.TI_MES,
    UN_PER1         IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ANO2         IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES2         IN PCK_SUBTIPOS.TI_MES,
    UN_PER2         IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_IDDEEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  )
RETURN NUMBER
  AS
   -- MI_ERROR_FUN    NUMBER:= GL_ERROR_NUM + 15;
    MI_ANO2         PCK_SUBTIPOS.TI_ANIO;
    MI_MES_INI      PCK_SUBTIPOS.TI_MES;   
    MI_MES2         PCK_SUBTIPOS.TI_MES;
    MI_PER2         PCK_SUBTIPOS.TI_PERIODO_NOMI;
    MI_CONTAR       PCK_SUBTIPOS.TI_ENTERO;
    MI_CNP          PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN
   MI_MES_INI := 1;          
  IF UN_ANO2 IS NULL THEN
    MI_ANO2:= UN_ANO1;
  ELSE
    MI_ANO2:= UN_ANO2;
  END IF;
  IF UN_MES2 IS NULL THEN
    MI_MES2:= UN_MES1;
  ELSE
    MI_MES2:= UN_MES2;
  END IF;
  IF  UN_PER2 IS NULL THEN
    MI_PER2:= UN_PER1;
  ELSE
    MI_PER2:= UN_PER2;
  END IF;
  -- ¿ NO borramos PCK_NOMINA.CNP, solo usamos CNP_RETRO
  -- PCK_NOMINA.CNP.DELETE;
  
  IF 0 > UN_CONCEPTO
     OR PCK_NOMINA.MAXI < UN_CONCEPTO THEN
    RETURN 0;
  END IF;
    BEGIN
      SELECT  SUM(T.VALOR)
             ,COUNT(T.ANO)
        INTO  MI_CNP
             ,MI_CONTAR
      FROM (SELECT   HISTORICOS.ANO,
                     HISTORICOS.MES,
                    SUM(HISTORICOS.VALOR) VALOR
            FROM HISTORICOS
            INNER JOIN PERIODOS
              ON  HISTORICOS.COMPANIA       = PERIODOS.COMPANIA
              AND HISTORICOS.ID_DE_PROCESO  = PERIODOS.ID_DE_PROCESO
              AND HISTORICOS.ANO            = PERIODOS.ANO
              AND HISTORICOS.MES            = PERIODOS.MES
              AND HISTORICOS.PERIODO        = PERIODOS.PERIODO
            WHERE PERIODOS.COMPANIA       = UN_COMPANIA
              AND PERIODOS.ID BETWEEN LPAD(UN_ANO1,4,'0') || LPAD(MI_MES_INI,2,'0') || LPAD(UN_PER1,2,'0')
                                  AND LPAD(MI_ANO2,4,'0') || LPAD(UN_MES2,2,'0') || LPAD(MI_PER2,2,'0') 
              AND HISTORICOS.ID_DE_EMPLEADO = UN_IDDEEMPLEADO              
              AND HISTORICOS.ID_DE_CONCEPTO = UN_CONCEPTO
              AND HISTORICOS.VALOR          NOT IN (0)
              AND HISTORICOS.ID_DE_PROCESO = 10
            GROUP BY HISTORICOS.ANO, HISTORICOS.MES
           ) T;
    EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
        MI_CNP := 0;
        MI_CONTAR := 0;
    END;
    IF MI_CNP IS NOT NULL THEN
        -- ¿ GUARDA EN CNP_RETRO en lugar de PCK_NOMINA.CNP
        PCK_NOMINA_CALCULO.CNP_RETRO(UN_CONCEPTO):= MI_CNP;
    END IF;
    RETURN MI_CONTAR;    
END FC_ACUMCONCEPTO_RETRO;

END PCK_NOMINA_COM1;
