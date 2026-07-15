create or replace PACKAGE BODY "PCK_NOMINA_COM6" as

--1
FUNCTION FC_CUALSALARIO
 /*
    NAME              : FC_CUALSALARIO --> Nombre Access cualsalario
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 21/07/2015
    TIME              : 11:16 AM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : EDGAR LEONARDO SARMIENTO - Agregar el manejo de Excepciones.
    DATE MODIFIED     : 08/09/2015
    MODIFIER          : JOSÉ PASCUAL GÓMEZ
    DATE MODIFIED     : 05/09/2017
    TIME              : 
    DESCRIPTION       : FUNCION PROVENIENTE DE ACCESS QUE RETORNA EL SALARIO BÃ¿SICO DE UN EMPLEADO DE ACUERDO A SU CATEGORÃ¿A
                        Se incluye estandarización de código y manejo de errores
    @Name: getSalarioBase
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ID_DE_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  )
RETURN PCK_SUBTIPOS.TI_DOBLE
  AS
    MI_SALARIO_CALCULADO  PCK_SUBTIPOS.TI_DOBLE;
BEGIN
  BEGIN
      SELECT CATEGORIA.SALARIO_BASE
      INTO MI_SALARIO_CALCULADO
      FROM PERSONAL INNER JOIN CATEGORIA ON 
            (PERSONAL.ID_DE_CATEGORIA = CATEGORIA.ID_DE_CATEGORIA) 
        AND (CATEGORIA.ESCALAFON      = PERSONAL.ESCALAFON) 
        AND (CATEGORIA.COMPANIA       = PERSONAL.COMPANIA) 
      WHERE CATEGORIA.ANO             = PERSONAL.ANO 
        AND PERSONAL.ID_DE_EMPLEADO   = UN_ID_DE_EMPLEADO 
        AND PERSONAL.COMPANIA         = UN_COMPANIA;

      EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_SALARIO_CALCULADO:=0;
  END;      
  RETURN MI_SALARIO_CALCULADO;
END FC_CUALSALARIO;

--2
FUNCTION FC_CUPODEUDA_MESANTERIOR
  /*
    NAME              : FC_CUPODEUDA_MESANTERIOR --> Nombre Access CUPODEUDA_MESANTERIOR
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 22/07/2015
    TIME              : 11:16 AM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DATE MODIFIED     : 08/09/2015
    MODIFIER          : JOSÉ PASCUAL GÓMEZ
    DATE MODIFIED     : 05/09/2017
    DESCRIPTION       : FUNCIÃ“N PROVENIENTE DE ACCESS QUE VERIFICA LA CANTIDAD QUE TIENE POR DESCUENTOS EN NOVEDADES Y LE SUMA LOS PRESTAMOS DIFERIDOS, VALIDANDO NO SOBREPASAR EL CUPO ASIGANDO CUANDO SE INGRESA LA NOVEDAD
                        Se incluye estandarización de código y manejo de errores
    @Name: getCupoDeuda
  */
  (
    UN_COMPANIA    IN   PCK_SUBTIPOS.TI_COMPANIA,
    UN_EMPLEADO    IN   PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_FECHA       IN DATE
  )
RETURN PCK_SUBTIPOS.TI_DOBLE
  AS
    MI_TOTAL       NUMBER;
    MI_MANO        VARCHAR2(32000);
    MI_MES         VARCHAR2(32000);

BEGIN
  IF(PCK_SYSMAN_UTL.FC_MES(UN_FECHA) = 1)THEN
    MI_MANO:=PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   =>PCK_SYSMAN_UTL.FC_VIGENCIA(UN_FECHA=> UN_FECHA)-1,
                                       UN_LONGITUD => 4);
    MI_MES:=PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => 12,
	                                    UN_LONGITUD => 2);
  ELSE
    MI_MANO:=PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   =>PCK_SYSMAN_UTL.FC_VIGENCIA(UN_FECHA=> UN_FECHA),
	                                     UN_LONGITUD => 4);
    MI_MES:=PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => PCK_SYSMAN_UTL.FC_MES(UN_FECHA =>UN_FECHA)-1,
	                                    UN_LONGITUD => 2);
  END IF;
  
  -- TICKET 7719342 ECABRERA: SE CAMBIO CONSULTA OPTIMIZANDOLA SEGUN RECOMENDACION DE CESAR CEBALLOS
  BEGIN
	  SELECT 	/*+ USE_MERGE(CONCEPTOS,HISTORICOS) ORDERED */ 
	  			NVL(SUM(HISTORICOS.VALOR), 0) 
	    INTO 	MI_TOTAL
	    FROM 	HISTORICOS INNER JOIN CONCEPTOS 
	          	ON HISTORICOS.ID_DE_CONCEPTO = CONCEPTOS.ID_DE_CONCEPTO 
	             	AND HISTORICOS.COMPANIA = CONCEPTOS.COMPANIA 
	    WHERE 	HISTORICOS.COMPANIA = UN_COMPANIA 
	   			AND HISTORICOS.ANO = MI_MANO 
	   			AND HISTORICOS.MES = MI_MES 
	   			AND HISTORICOS.ID_DE_EMPLEADO = UN_EMPLEADO 
	   			AND CONCEPTOS.CLASE = 5
          AND CONCEPTOS.ID_DE_CONCEPTO NOT IN (130, 131, 132)--(HU:7802826,CFBRRERA, Se excluyen estos conceptos para evitar duplicidad en el cálculo de deudas del mes, ya que estos conceptos ya se consideran en la resta del cupo asignado.) 
	 	GROUP BY HISTORICOS.COMPANIA,HISTORICOS.ANO,HISTORICOS.MES,HISTORICOS.ID_DE_EMPLEADO;
  EXCEPTION WHEN NO_DATA_FOUND THEN
  	 MI_TOTAL:=0;
  END;
  -- FIN 7719342
  RETURN MI_TOTAL;

END FC_CUPODEUDA_MESANTERIOR;

--3
FUNCTION FC_CUPO_ASIGNADO
  /*
    NAME              : FC_CUPO_ASIGNADO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 22/07/2015
    TIME              : 11:16 AM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DATE MODIFIED     : 08/09/2015
    MODIFIER          : JOSÉ PASCUAL GÓMEZ
    DATE MODIFIED     : 05/09/2017
    DESCRIPTION       : FUNCIÃ“N DESARROLLADA PARA CALCULAR EL CUPO ASIGNADO A UN EMPLEADO
                        Se incluye estandarización de código y manejo de errores
   @Name: getCupoAsigando 
  */
  (
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_CUALSALARIO  IN  PCK_SUBTIPOS.TI_DOBLE 
  )
RETURN PCK_SUBTIPOS.TI_DOBLE
  AS
    MI_CUPO_ASIGNADO  PCK_SUBTIPOS.TI_DOBLE;
    MI_CUALSALARIO    PCK_SUBTIPOS.TI_DOBLE;

BEGIN
  BEGIN
    PCK_NOMINA_COM7.PR_CARGAR_PARENTRADA (UN_COMPANIA);
    IF(UN_CUALSALARIO > (4*PCK_NOMINA.CPARENTRADA(1).SALARIOMINIMO)) THEN
     MI_CUPO_ASIGNADO:=(UN_CUALSALARIO - (UN_CUALSALARIO*9/100))/2;
    ELSE
      MI_CUPO_ASIGNADO:=(UN_CUALSALARIO - (UN_CUALSALARIO*8/100))/2;
    END IF;
    EXCEPTION WHEN OTHERS THEN
      MI_CUPO_ASIGNADO:=0;
  END;
  RETURN MI_CUPO_ASIGNADO;

END FC_CUPO_ASIGNADO;

PROCEDURE PR_PREPARAR_PERIODO 

  /*
    NAME              : PR_PREPARAR_PERIODO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 30/07/2015
    TIME              : 11:16 AM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : JOSÉ PASCUAL GÓMEZ
    DATE MODIFIED     : 05/09/2017
    DESCRIPTION       : FUNCION DESARROLLADA PARA PREPARAR LOS DIFERIDOS DE NOMINA DENTRO DE PRESTAMOS
                        Se incluye estandarización de código y manejo de errores
    @Name: prepararPeriodoFinan
  */
  (
    UN_COMPANIA   IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO       IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MES        IN  PCK_SUBTIPOS.TI_MES,
    UN_PERIODO    IN  PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ANIO2      IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MES2       IN  PCK_SUBTIPOS.TI_MES,
    UN_PERIODO2   IN  PCK_SUBTIPOS.TI_PERIODO_NOMI,
	  UN_USUARIO    IN  PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
    MI_ANIO         PCK_SUBTIPOS.TI_ANIO;
    MI_PERIODO      PCK_SUBTIPOS.TI_PERIODO_NOMI;
    MI_MES          PCK_SUBTIPOS.TI_MES;
    MI_RTA          NUMBER := 0;

BEGIN
  MI_CONDICION:= 'COMPANIA =''' || UN_COMPANIA || 
          ''' AND ANO      ='   || UN_ANIO     ||
		        ' AND MES      ='   || UN_MES      ||
            ' AND PERIODO  ='   || UN_PERIODO;
  BEGIN  
    BEGIN
      PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'FINANCIABLES_DE_NOMINA', 
                        UN_ACCION    => 'M', 
                        UN_CAMPOS    => 'NUMERO_DE_CUOTAS=1,  
                                        DATE_MODIFIED = SYSDATE, 
                                        MODIFIED_BY=''' || UN_USUARIO || '''',
                        UN_CONDICION => MI_CONDICION || ' AND NUMERO_DE_CUOTAS = 0'
                        );

      PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'FINANCIABLES_DE_NOMINA', 
                        UN_ACCION    => 'M', 
                        UN_CAMPOS    => 'CUOTAFIJA= -1,  
                                        DATE_MODIFIED = SYSDATE, 
                                        MODIFIED_BY=''' || UN_USUARIO || '''',
                        UN_CONDICION => MI_CONDICION || ' AND CUOTAFIJA = 0');
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
      RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD =>SQLCODE,
          UN_ERROR_COD=>PCK_ERRORES.ER_NOMINA_ACTUALIZAFINAN
        );
  END;
  IF(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                           UN_NOMBRE    => 'FINANCIABLES DE NOMINA EN UN PERIODO QUINCENAL', 
						               UN_MODULO    => PCK_DATOS.FC_MODULONOMINA, 
						               UN_FECHA_PAR => SYSDATE) = 'SI' ) THEN
    PR_PREPARARFINANCIABLES_NUEVO(UN_COMPANIA => UN_COMPANIA, 
                                  UN_ANIO1    => UN_ANIO, 
                                  UN_MES1     => UN_MES, 
                                  UN_PERIODO1 => UN_PERIODO, 
                                  UN_ANIO2    => UN_ANIO2, 
                                  UN_MES2     => UN_MES2, 
                                  UN_PERIODO2 => UN_PERIODO2, 
                                  UN_TIPOEMPLEADO      => '1',
                                  UN_IGUAL_O_DIFERENTE => NULL,
                                  UN_USUARIO           => UN_USUARIO);
  ELSIF(UN_PERIODO2 <> 2) THEN
    PCK_NOMINA_COM2.PR_PREPARARFINANCIABLES(UN_COMPANIA => UN_COMPANIA, 
                                            UN_ANO1     => UN_ANIO, 
                                            UN_MES1     => UN_MES, 
                                            UN_PERIODO1 => UN_PERIODO, 
                                            UN_ANO2     => UN_ANIO2, 
                                            UN_MES2     => UN_MES2, 
                                            UN_PERIODO2 => UN_PERIODO2, 
                                            UN_TIPO_EMPLEADO     => '02', 
                                            UN_IGUAL_O_DIFERENTE => '<>',
                                            UN_USUARIO           => UN_USUARIO);
    PCK_NOMINA_COM2.PR_PREPARARFINANCIABLES(UN_COMPANIA => UN_COMPANIA, 
                                            UN_ANO1     => UN_ANIO, 
                                            UN_MES1     => UN_MES, 
                                            UN_PERIODO1 => UN_PERIODO, 
                                            UN_ANO2     => UN_ANIO2, 
                                            UN_MES2     => UN_MES2, 
                                            UN_PERIODO2 => UN_PERIODO2, 
                                            UN_TIPO_EMPLEADO     => '02', 
                                            UN_IGUAL_O_DIFERENTE => '=',
										                      	UN_USUARIO           => UN_USUARIO);
  ELSE
    PCK_NOMINA_COM2.PR_PREPARARFINANCIABLES(UN_COMPANIA => UN_COMPANIA, 
                                            UN_ANO1     => UN_ANIO, 
                                            UN_MES1     => UN_MES, 
                                            UN_PERIODO1 => UN_PERIODO, 
                                            UN_ANO2     => UN_ANIO2, 
                                            UN_MES2     => UN_MES2, 
                                            UN_PERIODO2 => UN_PERIODO2, 
                                            UN_TIPO_EMPLEADO     => '02', 
                                            UN_IGUAL_O_DIFERENTE => '<>',
											    							    UN_USUARIO           => UN_USUARIO);											
    MI_MES:=UN_MES2-1;
    IF(MI_MES <= 0) THEN
      MI_MES:= 12;
      MI_ANIO:= UN_ANIO2-1;
    END IF;
    MI_PERIODO:= UN_PERIODO2;
	  PCK_NOMINA_COM2.PR_PREPARARFINANCIABLES(UN_COMPANIA => UN_COMPANIA, 
                                            UN_ANO1     => UN_ANIO, 
                                            UN_MES1     => UN_MES, 
                                            UN_PERIODO1 => UN_PERIODO, 
                                            UN_ANO2     => UN_ANIO2, 
                                            UN_MES2     => UN_MES2, 
                                            UN_PERIODO2 => UN_PERIODO2, 
                                            UN_TIPO_EMPLEADO     => '02', 
                                            UN_IGUAL_O_DIFERENTE => '=',
		                      									UN_USUARIO           => UN_USUARIO);
  END IF;

END PR_PREPARAR_PERIODO;

PROCEDURE PR_PREPARARFINANCIABLES_NUEVO
/*
    NAME              : PR_PREPARARFINANCIABLES_NUEVO ---> NOMBRE EN ACCESS: PREPARARFINANCIABLESNUEVO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 28/07/2015
    TIME              : 03:08 PM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : JOSÉ PASCUAL GÓMEZ
    DATE MODIFIED     : 05/09/2017
    DESCRIPTION       : PREPARA LOS FINANCIABLES DE NOMINA PARA EL SIGUIENTE PERIODO
                        Se incluye estandarización de código y manejo de errores    
    @Name: prepararFinanciableNuevo
  */
  (
	  UN_COMPANIA   IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO1      IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MES1       IN  PCK_SUBTIPOS.TI_MES,
    UN_PERIODO1   IN  PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ANIO2      IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MES2       IN  PCK_SUBTIPOS.TI_MES,
    UN_PERIODO2   IN  PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_USUARIO    IN  PCK_SUBTIPOS.TI_USUARIO,
	  UN_TIPOEMPLEADO      IN VARCHAR2 DEFAULT NULL,
    UN_IGUAL_O_DIFERENTE IN VARCHAR2 DEFAULT NULL	  
  )
  IS
    MI_CADENA           PCK_SUBTIPOS.TI_STRSQL;
    MI_CONTADOR         PCK_SUBTIPOS.TI_ENTERO:=1;
    MI_VCUOTA           PCK_SUBTIPOS.TI_DOBLE; 
    MI_VSALDO           PCK_SUBTIPOS.TI_DOBLE; 
    MI_VMONTO           PCK_SUBTIPOS.TI_DOBLE; 
    MI_NCUOTAS          PCK_SUBTIPOS.TI_ENTERO;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_FECHA            DATE;
    MI_FECHA_LIBRANZA   DATE;
    MI_FECHAFIN         DATE;--JORDUZ
    MI_REGISTROS        PCK_SUBTIPOS.TI_ENTERO;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_UPDATE           PCK_SUBTIPOS.TI_CAMPOS;

  TYPE TEM_DIFERIDOS IS TABLE OF FINANCIABLES_DE_NOMINA%ROWTYPE INDEX BY BINARY_INTEGER;
  RS TEM_DIFERIDOS;
  TEMDIFERIDO_TABLA TEM_DIFERIDOS;
  DUPLICA PCK_SUBTIPOS.TI_PARAMETRO;
BEGIN
  DUPLICA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                   UN_NOMBRE    => 'DUPLICAR CUOTAS EN NOMINA MENSUAL',
                                   UN_MODULO    => PCK_DATOS.FC_MODULONOMINA, 
                                   UN_FECHA_PAR => SYSDATE);
  SELECT FINANCIABLES_DE_NOMINA.*
  BULK COLLECT INTO RS
  FROM FINANCIABLES_DE_NOMINA INNER JOIN PERSONAL ON 
       FINANCIABLES_DE_NOMINA.COMPANIA       = PERSONAL.COMPANIA 
   AND FINANCIABLES_DE_NOMINA.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
  WHERE FINANCIABLES_DE_NOMINA.COMPANIA = UN_COMPANIA 
   AND FINANCIABLES_DE_NOMINA.ANO       = UN_ANIO1  
   AND FINANCIABLES_DE_NOMINA.MES       = UN_MES1  
   AND FINANCIABLES_DE_NOMINA.PERIODO   = UN_PERIODO1;

   IF (RS.COUNT > 0) THEN
       <<TotalFinaciables>>
       FOR I IN RS.FIRST .. RS.LAST    
       LOOP
        IF(RS(i).NUMERO_DE_CUOTAS >= 0 OR RS(i).SALDO > 0 OR RS(i).CUOTAFIJA <> 0) THEN
          MI_VMONTO:= PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => RS(i).MONTO_INICIAL <> 0,
                                            UN_SI        => RS(i).MONTO_INICIAL, 
                                            UN_NO        =>0);
                                            
          IF RS(i).VALOR_CUOTA = RS(I).VALDESCUENTO THEN
          
            MI_NCUOTAS:= RS(i).NUMERO_DE_CUOTAS - PCK_SYSMAN_UTL.FC_IIF(
                                                   UN_CONDICION => (DUPLICA= 'SI' 
                                                                AND RS(i).PERIODOCOBRO = 0 
                                                                AND UN_PERIODO2 = 3), 
                                                    UN_SI        =>2, 
                                                    UN_NO        =>1);
          ELSE
           MI_NCUOTAS:= RS(i).NUMERO_DE_CUOTAS - TRUNC(RS(I).VALDESCUENTO/RS(i).VALOR_CUOTA);
          END IF;

          IF(RS(i).CUOTAFIJA <> 0)THEN ---Si es cuota fija
            IF(RS(i).INTERES > 0) THEN
              MI_VCUOTA:= RS(i).VALOR_CUOTA * RS(i).INTERES/100;
              MI_VSALDO:= RS(i).SALDO - MI_VCUOTA;
            ELSE
              MI_VCUOTA:= RS(i).VALOR_CUOTA;
              MI_VSALDO:= RS(I).SALDO - 
                          PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => DUPLICA= 'SI' 
                                                            AND RS(I).PERIODOCOBRO =0 
                                                            AND UN_PERIODO2 = 3, 
                                                UN_SI        => MI_VCUOTA*2, 
                                                UN_NO        => RS(I).VALDESCUENTO);
              IF(MI_VSALDO < 0) THEN
                MI_VSALDO:=0;
              END IF;
            END IF;
          ELSE -- Si no es cuota fija
            IF(RS(I).INTERES <> 0) THEN ---Cuota con interes sobre saldo
              MI_VCUOTA:= (RS(I).SALDO / MI_NCUOTAS) + (RS(I).SALDO * RS(I).INTERES / 100);
              MI_VSALDO:= RS(I).SALDO - RS(I).SALDO/MI_NCUOTAS;
            ELSE
              MI_VCUOTA:= PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => RS(I).SALDO < RS(I).VALOR_CUOTA, 
                                                UN_SI        => RS(I).SALDO, 
                                                UN_NO        => RS(I).VALOR_CUOTA);
              MI_VSALDO:= RS(I).SALDO - RS(I).VALDESCUENTO;                    
              IF(MI_VSALDO < 0) THEN
                MI_VSALDO:= 0;
              END IF;
            END IF;
          END IF;
        ELSE
          MI_VCUOTA:=0;
          MI_VSALDO:=0;
        END IF;
        IF(MI_VSALDO > 0 OR RS(I).SALDO <> 0 AND MI_VCUOTA >= 0 AND MI_NCUOTAS > 0) THEN
          TEMDIFERIDO_TABLA(MI_CONTADOR).COMPANIA      := UN_COMPANIA;
          TEMDIFERIDO_TABLA(MI_CONTADOR).ID_DE_PROCESO := RS(I).ID_DE_PROCESO;
          TEMDIFERIDO_TABLA(MI_CONTADOR).ANO  := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => RS(I).PERIODOCOBRO = 1 
                                                                                   AND UN_MES1+1 = 13, 
                                                                       UN_SI        => UN_ANIO1+1, 
                                                                       UN_NO        => UN_ANIO2);
          IF(RS(I).PERIODOCOBRO = 2 OR RS(I).PERIODOCOBRO = 3) THEN
            IF (UN_PERIODO1 = 2 OR UN_PERIODO1 = 3) THEN
              TEMDIFERIDO_TABLA(MI_CONTADOR).MES := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => UN_MES1+1 = 13, 
                                                                          UN_SI        => 1, 
                                                                          UN_NO        => UN_MES1+1);
            ELSE
              TEMDIFERIDO_TABLA(MI_CONTADOR).MES := UN_MES2;      
            END IF;
          ELSE
            TEMDIFERIDO_TABLA(MI_CONTADOR).MES:= UN_MES2;
          END IF;            
          TEMDIFERIDO_TABLA(MI_CONTADOR).PERIODO        := UN_PERIODO2;
          TEMDIFERIDO_TABLA(MI_CONTADOR).ID_DE_EMPLEADO := RS(I).ID_DE_EMPLEADO;
          TEMDIFERIDO_TABLA(MI_CONTADOR).ID_DE_CONCEPTO := RS(I).ID_DE_CONCEPTO;
          TEMDIFERIDO_TABLA(MI_CONTADOR).MONTO_INICIAL  := MI_VMONTO;
          TEMDIFERIDO_TABLA(MI_CONTADOR).VALOR_CUOTA    := MI_VCUOTA;          
          IF(UN_PERIODO2 = 2 AND RS(I).PERIODOCOBRO = 1) OR 
            (UN_PERIODO2 = 1 AND RS(I).PERIODOCOBRO = 2)     THEN
            TEMDIFERIDO_TABLA(MI_CONTADOR).VALDESCUENTO:= 0;
          ELSE
            TEMDIFERIDO_TABLA(MI_CONTADOR).VALDESCUENTO:= PCK_SYSMAN_UTL.FC_IIF(
                                                              UN_CONDICION => RS(I).SALDO < 
                                                                          PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => DUPLICA = 'SI' 
                                                                                                            AND RS(I).PERIODOCOBRO = 0 
                                                                                                            AND UN_PERIODO2 = 3, 
                                                                                                UN_SI        => MI_VCUOTA*2, 
                                                                                                UN_NO        => MI_VCUOTA),
                                                              UN_SI        => MI_VSALDO, 
                                                              UN_NO        => PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => DUPLICA = 'SI' 
                                                                                                                AND RS(I).PERIODOCOBRO = 0 
                                                                                                                AND UN_PERIODO2=3, 
                                                                                                    UN_SI        => MI_VCUOTA*2, 
                                                                                                    UN_NO        => MI_VCUOTA)
                                                              );
          END IF;
          TEMDIFERIDO_TABLA(MI_CONTADOR).NUMERO_DE_CUOTAS:= PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_NCUOTAS <= 0, 
                                                                                  UN_SI        => 0, 
                                                                                  UN_NO        => MI_NCUOTAS);
          TEMDIFERIDO_TABLA(MI_CONTADOR).SALDO           := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_VSALDO <= 0, 
                                                                                  UN_SI        => 0, 
                                                                                  UN_NO        => MI_VSALDO);
          TEMDIFERIDO_TABLA(MI_CONTADOR).VALDESCUENTO    := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => MI_VSALDO = 0 AND TEMDIFERIDO_TABLA(MI_CONTADOR).VALDESCUENTO > 0, 
                                                                                  UN_SI        => 0, 
                                                                                  UN_NO        => TEMDIFERIDO_TABLA(MI_CONTADOR).VALDESCUENTO);
          TEMDIFERIDO_TABLA(MI_CONTADOR).CUOTAFIJA       := RS(I).CUOTAFIJA;
          TEMDIFERIDO_TABLA(MI_CONTADOR).INTERES         := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION => RS(I).INTERES <> 0, 
                                                                                  UN_SI        => RS(I).INTERES, 
                                                                                  UN_NO        => 0);
          IF RS(I).FECHA IS NULL THEN
            TEMDIFERIDO_TABLA(MI_CONTADOR).FECHA:= PCK_NOMINA.FC_FECHAPERIODO(UN_COMPANIA  => UN_COMPANIA, 
                                                                              UN_PROCESO   => RS(I).ID_DE_PROCESO, 
                                                                              UN_ANO       => UN_ANIO2, 
                                                                              UN_MES       => UN_MES2, 
                                                                              UN_PERIODO   => UN_PERIODO2);
          ELSE
            TEMDIFERIDO_TABLA(MI_CONTADOR).FECHA:= RS(I).FECHA;
          END IF;
          TEMDIFERIDO_TABLA(MI_CONTADOR).INDPREPARADO   := -1;
          TEMDIFERIDO_TABLA(MI_CONTADOR).PERIODOCOBRO   := RS(I).PERIODOCOBRO;
          TEMDIFERIDO_TABLA(MI_CONTADOR).NLIBRANZA      := RS(I).NLIBRANZA;
          TEMDIFERIDO_TABLA(MI_CONTADOR).FECHA_LIBRANZA := RS(I).FECHA_LIBRANZA;
          TEMDIFERIDO_TABLA(MI_CONTADOR).FECHAFIN       := RS(I).FECHAFIN;
          MI_CONTADOR:= MI_CONTADOR+1;
        END IF;
      END LOOP TotalFinaciables;
  END IF;
  IF(TEMDIFERIDO_TABLA.COUNT > 0) THEN
    <<Insertado>>
    FOR MI_TEMDIF IN TEMDIFERIDO_TABLA.FIRST..TEMDIFERIDO_TABLA.LAST
    LOOP
      MI_FECHA:= TEMDIFERIDO_TABLA(MI_TEMDIF).FECHA;
      MI_FECHA_LIBRANZA:= TEMDIFERIDO_TABLA(MI_TEMDIF).FECHA_LIBRANZA;
      MI_FECHAFIN := TEMDIFERIDO_TABLA(MI_TEMDIF).FECHAFIN;
      SELECT COUNT(COMPANIA) VAL
      INTO MI_REGISTROS
      FROM FINANCIABLES_DE_NOMINA
      WHERE ID_DE_EMPLEADO = TEMDIFERIDO_TABLA(MI_TEMDIF).ID_DE_EMPLEADO
        AND ID_DE_CONCEPTO = TEMDIFERIDO_TABLA(MI_TEMDIF).ID_DE_CONCEPTO
        AND COMPANIA       = UN_COMPANIA
        AND ID_DE_PROCESO  = TEMDIFERIDO_TABLA(MI_TEMDIF).ID_DE_PROCESO
        AND ANO            = TEMDIFERIDO_TABLA(MI_TEMDIF).ANO
        AND MES            = TEMDIFERIDO_TABLA(MI_TEMDIF).MES
        AND PERIODO        = TEMDIFERIDO_TABLA(MI_TEMDIF).PERIODO;

      IF(MI_REGISTROS > 0) THEN
        MI_UPDATE:='MONTO_INICIAL    = '||TEMDIFERIDO_TABLA(MI_TEMDIF).MONTO_INICIAL||', '||
                   'VALOR_CUOTA      = '||TEMDIFERIDO_TABLA(MI_TEMDIF).VALOR_CUOTA||', '||
                   'NUMERO_DE_CUOTAS = '||TEMDIFERIDO_TABLA(MI_TEMDIF).NUMERO_DE_CUOTAS||', '||
                   'SALDO            = '||TEMDIFERIDO_TABLA(MI_TEMDIF).SALDO||', '||
                   'VALDESCUENTO     = '||TEMDIFERIDO_TABLA(MI_TEMDIF).VALDESCUENTO||', '||
                   'CUOTAFIJA        = '||TEMDIFERIDO_TABLA(MI_TEMDIF).CUOTAFIJA||', '||
                   'INTERES          = '||TEMDIFERIDO_TABLA(MI_TEMDIF).INTERES||', '||
                   'FECHA            = TO_DATE( '''|| TO_CHAR(MI_FECHA,'DD/MM/YYYY')||''', ''DD/MM/YYYY''),
                   INDPREPARADO     = '||TEMDIFERIDO_TABLA(MI_TEMDIF).INDPREPARADO||', '||
                   'PERIODOCOBRO     = '||TEMDIFERIDO_TABLA(MI_TEMDIF).PERIODOCOBRO||', '||
                   'NLIBRANZA        = '''||TEMDIFERIDO_TABLA(MI_TEMDIF).NLIBRANZA||''', '||
                   'FECHA_LIBRANZA   = TO_DATE('''||TO_CHAR(MI_FECHA_LIBRANZA,'DD/MM/YYYY')||''', ''DD/MM/YYYY''),
                   FECHAFIN   = TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||''', ''DD/MM/YYYY''),
                   DATE_MODIFIED    = SYSDATE,
                    MODIFIED_BY      = ''' || UN_USUARIO || '''';

        MI_CONDICION:='ID_DE_EMPLEADO = '||TEMDIFERIDO_TABLA(MI_TEMDIF).ID_DE_EMPLEADO||
                 ' AND ID_DE_CONCEPTO = '||TEMDIFERIDO_TABLA(MI_TEMDIF).ID_DE_CONCEPTO||
                 ' AND COMPANIA       = '||UN_COMPANIA||
                 ' AND ID_DE_PROCESO  = '||TEMDIFERIDO_TABLA(MI_TEMDIF).ID_DE_PROCESO||
                 ' AND ANO            = '||TEMDIFERIDO_TABLA(MI_TEMDIF).ANO||
                 ' AND MES            = '||TEMDIFERIDO_TABLA(MI_TEMDIF).MES||
                 ' AND PERIODO        = '||TEMDIFERIDO_TABLA(MI_TEMDIF).PERIODO;
        BEGIN
          BEGIN
            PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'FINANCIABLES_DE_NOMINA', 
                                                 UN_ACCION    => 'M', 
                                                 UN_CAMPOS    => MI_UPDATE, 
                                                 UN_CONDICION => MI_CONDICION); 
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               RAISE PCK_EXCEPCIONES.EXC_NOMINA;
           END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN    
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ER_NOMINA_ACTUALIZAFINAN);     
        END;
      ELSE       
        MI_CAMPOS:='COMPANIA, ID_DE_PROCESO, ANO, MES, PERIODO, ID_DE_EMPLEADO, ID_DE_CONCEPTO, 
                    MONTO_INICIAL, VALOR_CUOTA, NUMERO_DE_CUOTAS, SALDO, VALDESCUENTO, CUOTAFIJA, 
                    INTERES, FECHA, INDPREPARADO, PERIODOCOBRO, FECHA_LIBRANZA, FECHAFIN, DATE_CREATED,CREATED_BY';                  
        MI_VALORES := ''''||UN_COMPANIA
                   ||''','||TEMDIFERIDO_TABLA(MI_TEMDIF).ID_DE_PROCESO
                    ||', '||TEMDIFERIDO_TABLA(MI_TEMDIF).ANO
                    ||', '||TEMDIFERIDO_TABLA(MI_TEMDIF).MES
                    ||', '||TEMDIFERIDO_TABLA(MI_TEMDIF).PERIODO
                    ||', '||TEMDIFERIDO_TABLA(MI_TEMDIF).ID_DE_EMPLEADO
                    ||', '||TEMDIFERIDO_TABLA(MI_TEMDIF).ID_DE_CONCEPTO
                    ||', '||TEMDIFERIDO_TABLA(MI_TEMDIF).MONTO_INICIAL
                    ||', '||TEMDIFERIDO_TABLA(MI_TEMDIF).VALOR_CUOTA
                    ||', '||TEMDIFERIDO_TABLA(MI_TEMDIF).NUMERO_DE_CUOTAS
                    ||', '||TEMDIFERIDO_TABLA(MI_TEMDIF).SALDO
                    ||', '||TEMDIFERIDO_TABLA(MI_TEMDIF).VALDESCUENTO
                    ||', '||TEMDIFERIDO_TABLA(MI_TEMDIF).CUOTAFIJA
                    ||', '||TEMDIFERIDO_TABLA(MI_TEMDIF).INTERES
                    ||', TO_DATE( '''|| TO_CHAR(MI_FECHA,'DD/MM/YYYY')||''', ''DD/MM/YYYY''),
                    '|| TEMDIFERIDO_TABLA(MI_TEMDIF).INDPREPARADO
                    ||', '||TEMDIFERIDO_TABLA(MI_TEMDIF).PERIODOCOBRO
                    ||', TO_DATE('''||TO_CHAR(MI_FECHA_LIBRANZA,'DD/MM/YYYY')||''', ''DD/MM/YYYY'')'
                    ||', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||''', ''DD/MM/YYYY''), SYSDATE 
                    , ''' || UN_USUARIO || '''';

        IF(TEMDIFERIDO_TABLA(MI_TEMDIF).NLIBRANZA <> NULL) THEN
          MI_CAMPOS:= MI_CAMPOS||', NLIBRANZA';
          MI_VALORES := MI_VALORES||', '''||TEMDIFERIDO_TABLA(MI_TEMDIF).NLIBRANZA || '''';
        END IF;
        BEGIN
          BEGIN
            PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'FINANCIABLES_DE_NOMINA', 
                                                 UN_ACCION    => 'I', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_VALORES   => MI_VALORES); 
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_NOMINA;
           END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN    
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ER_NOMINA_ACTUALIZAFINAN);     
        END;
      END IF;    
    END LOOP Insertado;    
  END IF;
END PR_PREPARARFINANCIABLES_NUEVO; 

--7
FUNCTION FC_BORRARFINANCIABLES
    /*
    NAME              : FC_BORRARFINANCIABLES ----> NOMBRE EN ACCESS BORRARFINANCIABLES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 01/08/2015
    TIME              : 08:16 AM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : JOSÉ PASCUAL GÓMEZ
    DATE MODIFIED     : 05/09/2017
    DESCRIPTION       : FUNCION MIGRADA QUE PERMITE BORRAR LOS FINANCIABLES DE NOMINA DE UN PERIODO ESTABLECIDO
                        Se incluye estandarización de código y manejo de errores  
    @Name: borrarFinaciables
  */
  (
    UN_COMPANIA   IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO    IN  PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIO       IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MES        IN  PCK_SUBTIPOS.TI_MES,
    UN_PERIODO    IN  PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_USUARIO    IN  PCK_SUBTIPOS.TI_USUARIO
  )
RETURN PCK_SUBTIPOS.TI_ENTERO
  AS
    MI_ESTADO     PCK_SUBTIPOS.TI_ENTERO;
    MI_CONDICION  VARCHAR2(1000);

BEGIN
  MI_CONDICION:= 'FINANCIABLES_DE_NOMINA.COMPANIA      = '||UN_COMPANIA||
            ' AND FINANCIABLES_DE_NOMINA.ID_DE_PROCESO = '||UN_PROCESO||
            ' AND FINANCIABLES_DE_NOMINA.ANO           = '||UN_ANIO||
            ' AND FINANCIABLES_DE_NOMINA.MES           = '||UN_MES||
            ' AND FINANCIABLES_DE_NOMINA.PERIODO       = '||UN_PERIODO||
            ' AND FINANCIABLES_DE_NOMINA.INDPREPARADO  <> 0';
  BEGIN
    MI_ESTADO:= PCK_DATOS.FC_ACME(UN_TABLA     => 'FINANCIABLES_DE_NOMINA', 
                                  UN_ACCION    => 'E',
                                  UN_CONDICION => MI_CONDICION);  
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
    RAISE PCK_EXCEPCIONES.EXC_NOMINA;
  END;      
  RETURN MI_ESTADO;    
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ER_NOMINA_ELIMINAFINAN
      );
END FC_BORRARFINANCIABLES;

--8
FUNCTION FC_BORRARHISTORICOS
  /*
    NAME              : FC_BORRARHISTORICOS ----> NOMBRE EN ACCESS BORRARHISTORICOS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 10/08/2015
    TIME              : 04:37 PM
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    MODIFIER          : JOSÉ PASCUAL GÓMEZ
                        MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MODIFIED     : 05/09/2017
                        03/01/2019
    DESCRIPTION       : FUNCION MIGRADA QUE PERMITE ELIMINAR LOS HISTORICOS DE NÃ“MINA EN UN PERIODO ESTABLECEDISO
                        Se incluye estandarización de código y manejo de errores
                        Se incluye el parámetro UN_EMPLEADO, Si viene con valor 00 elimina todos los empleados.
    @Name: borrarHistoricos
  */
  (
    UN_COMPANIA   IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO    IN  PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIO       IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MES        IN  PCK_SUBTIPOS.TI_MES,
    UN_PERIODO    IN  PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_EMPLEADO   IN  VARCHAR2 DEFAULT '00',
    UN_USUARIO    IN  PCK_SUBTIPOS.TI_USUARIO
  )
RETURN PCK_SUBTIPOS.TI_ENTERO
  AS
    MI_CONDICION   VARCHAR(3000);
    MI_RTA         NUMBER := 0;
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

    --(MZANGUNA:03/01/2019)-Se agregan las tablas RETEFUENTE_CALCULOS, PROVISIONES_MENSUALES_NIIF, BASESNOVEDADES, PERSONAL_HISTORICO
    --RETEFUENTE_CALCULOS
    MI_CONDICION := 'COMPANIA      = '''|| UN_COMPANIA ||'''
                 AND ID_DE_PROCESO = '  || UN_PROCESO ||'
                 AND ANO           = '  || UN_ANIO ||'
                 AND MES           = '  || UN_MES ||'
                 AND PERIODO       = '  || UN_PERIODO;
    IF UN_EMPLEADO <> '00' THEN
        MI_CONDICION := MI_CONDICION || ' AND ID_DE_EMPLEADO = '|| UN_EMPLEADO || ' ';
    END IF;

    BEGIN
        MI_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'RETEFUENTE_CALCULOS',
                                   UN_ACCION    => 'E',
                                   UN_CONDICION => MI_CONDICION);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;

    --PROVISIONES_MENSUALES_NIIF
    MI_CONDICION := 'COMPANIA      = '''|| UN_COMPANIA ||'''
                 AND ID_DE_PROCESO = '  || UN_PROCESO ||'
                 AND ANO           = '  || UN_ANIO ||'
                 AND MES           = '  || UN_MES ||'
                 AND PERIODO       = '  || UN_PERIODO;
    IF UN_EMPLEADO <> '00' THEN
        MI_CONDICION := MI_CONDICION || ' AND ID_DE_EMPLEADO = '|| UN_EMPLEADO || ' ';
    END IF;

    BEGIN
        MI_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'PROVISIONES_MENSUALES_NIIF',
                                   UN_ACCION    => 'E',
                                   UN_CONDICION => MI_CONDICION);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;

    --BASESNOVEDADES
    MI_CONDICION := 'COMPANIA      = '''|| UN_COMPANIA ||'''
                 AND ANO           = '  || UN_ANIO ||'
                 AND MES           = '  || UN_MES ||'
                 AND PERIODO       = '  || UN_PERIODO;
    IF UN_EMPLEADO <> '00' THEN
        MI_CONDICION := MI_CONDICION || ' AND ID_DE_EMPLEADO = '|| UN_EMPLEADO || ' ';
    END IF;

    BEGIN
        MI_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'BASESNOVEDADES',
                                   UN_ACCION    => 'E',
                                   UN_CONDICION => MI_CONDICION);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;

    --PERSONAL_HISTORICO
    MI_CONDICION := 'COMPANIA      = '''|| UN_COMPANIA ||'''
                 AND ID_DE_PROCESO = '  || UN_PROCESO ||'
                 AND ANO           = '  || UN_ANIO ||'
                 AND MES           = '  || UN_MES ||'
                 AND PERIODO       = '  || UN_PERIODO;
    IF UN_EMPLEADO <> '00' THEN
        MI_CONDICION := MI_CONDICION || ' AND ID_DE_EMPLEADO = '|| UN_EMPLEADO || ' ';
    END IF;

    BEGIN
        MI_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'PERSONAL_HISTORICO',
                                   UN_ACCION    => 'E',
                                   UN_CONDICION => MI_CONDICION);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;

    --HISTORICOS
    MI_CONDICION := 'HISTORICOS.COMPANIA      = '''|| UN_COMPANIA ||''''
          ||' AND HISTORICOS.ID_DE_PROCESO = '  || UN_PROCESO
          ||' AND HISTORICOS.ANO           = '  || UN_ANIO
          ||' AND HISTORICOS.MES           = '  || UN_MES
          ||' AND HISTORICOS.PERIODO       = '  || UN_PERIODO;

    IF UN_EMPLEADO <> '00' THEN --(MZANGUNA:03/01/2019)-Se incluye el parámetro UN_EMPLEADO, Si viene con valor 00 elimina todos los empleados.
        MI_CONDICION := MI_CONDICION || ' AND HISTORICOS.ID_DE_EMPLEADO = '|| UN_EMPLEADO || ' ';
    END IF;

    BEGIN
        MI_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'HISTORICOS',
                                   UN_ACCION    => 'E',
                                   UN_CONDICION => MI_CONDICION);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;

    RETURN MI_RTA;
EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
    MI_MSGERROR(1).CLAVE := 'PERIODO';
    MI_MSGERROR(1).VALOR := UN_ANIO || '-' || UN_MES ;
    PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD =>SQLCODE,
        UN_ERROR_COD=>PCK_ERRORES.ER_NOMINA_DELETE_PHISTORICOS);

END FC_BORRARHISTORICOS;

FUNCTION FC_REVISIONINCONSISTENCIAS
 /*
    NAME              : FC_REVISIONINCONSISTENCIAS, NOMBRE EN ACCESS REVISIONINCONSISTENCIAS 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO PACANCHIQUE
    DATE MIGRADOR     : 11/12/2015
    TIME              : 12:45 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : REVISA LOS ERRORES ANTES DE GENERAR LAS AUTOLIQUIDACIONES DE NÃ“MINA
    @Name: inconsistenciaSOI
    */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA    ,
    UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO        ,
    UN_MES          IN PCK_SUBTIPOS.TI_MES         ,
    UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_PROCESO      IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
  )
RETURN NUMBER
AS
  MI_DIF              NUMBER;
  MI_PORCENTAJERIESGO NUMBER;
  MI_RETORNO          NUMBER:=0;
BEGIN
  DELETE FROM ERRORES WHERE COMPANIA=UN_COMPANIA;
  FOR RS IN (SELECT 
                NOVEDADES_AUTOLIQUIDACION.COMPANIA, 
                NOVEDADES_AUTOLIQUIDACION.ANO, 
                NOVEDADES_AUTOLIQUIDACION.MES, 
                NOVEDADES_AUTOLIQUIDACION.NUM_AUTO_INCAP, 
                NOVEDADES_AUTOLIQUIDACION.VR_INCAP, 
                NOVEDADES_AUTOLIQUIDACION.NUM_AUTO_MAT, 
                NOVEDADES_AUTOLIQUIDACION.VR_MAT,  
                NOVEDADES_AUTOLIQUIDACION.NOMBRE_FONDO, 
                NOVEDADES_AUTOLIQUIDACION.NUMERO_AUT_PAGO_INCAPARP, 
                NOVEDADES_AUTOLIQUIDACION.VALOR_TOTAL_INCAP_PAGADASARP  
              FROM NOVEDADES_AUTOLIQUIDACION 
              WHERE NOVEDADES_AUTOLIQUIDACION.COMPANIA	=UN_COMPANIA 
                AND NOVEDADES_AUTOLIQUIDACION.ANO		=UN_ANIO
                AND NOVEDADES_AUTOLIQUIDACION.MES		=UN_MES) LOOP

    IF((RS.NUM_AUTO_INCAP IS NOT NULL OR RS.NUM_AUTO_INCAP <> 0) AND RS.VR_INCAP=0) THEN
      PCK_NOMINA_COM6.PR_AGREGARERROR_LIQUIDACION(UN_COMPANIA  => UN_COMPANIA
                                                 ,UN_ANIO      => UN_ANIO 
                                                 ,UN_MES       => UN_MES
                                                 ,UN_PERIODO   => UN_PERIODO
                                                 ,UN_PROCESO   => UN_PROCESO 
                                                 ,UN_ERROR     => 'Tiene número de incapacidad a reportar, pero no tiene valor reportado. Favor revisar antes de generar archivo plano, el cual presentara inconsistencias en la entidad.'
                                                 ,UN_USUARIO   => UN_USUARIO );

    END IF;
    IF(RS.NUM_AUTO_INCAP IS NULL AND RS.VR_INCAP <> 0) THEN
      PCK_NOMINA_COM6.PR_AGREGARERROR_LIQUIDACION(UN_COMPANIA  => UN_COMPANIA
                                                 ,UN_ANIO      => UN_ANIO 
                                                 ,UN_MES       => UN_MES
                                                 ,UN_PERIODO   => UN_PERIODO
                                                 ,UN_PROCESO   => UN_PROCESO 
                                                 ,UN_ERROR     => 'Tiene valor de incapacidad a reportar, pero no tiene número asignado reportado. Favor revisar antes de generar archivo plano, el cual presentara inconsistencias en la entidad.'
                                                 ,UN_USUARIO   => UN_USUARIO );
    END IF;
    IF((RS.NUM_AUTO_MAT IS NOT NULL OR RS.NUM_AUTO_MAT <> 0 ) AND RS.VR_MAT = 0) THEN
      PCK_NOMINA_COM6.PR_AGREGARERROR_LIQUIDACION(UN_COMPANIA  => UN_COMPANIA
                                                 ,UN_ANIO      => UN_ANIO 
                                                 ,UN_MES       => UN_MES
                                                 ,UN_PERIODO   => UN_PERIODO
                                                 ,UN_PROCESO   => UN_PROCESO 
                                                 ,UN_ERROR     => 'Tiene número de licencia de maternidad a reportar, pero no tiene valor reportado. Favor revisar antes de generar archivo plano, el cual presentara inconsistencias en la entidad.'
                                                 ,UN_USUARIO   => UN_USUARIO );

    END IF;
    IF(RS.NUM_AUTO_MAT IS NULL AND RS.VR_MAT <> 0) THEN
	  PCK_NOMINA_COM6.PR_AGREGARERROR_LIQUIDACION(UN_COMPANIA  => UN_COMPANIA
                                                 ,UN_ANIO      => UN_ANIO 
                                                 ,UN_MES       => UN_MES
                                                 ,UN_PERIODO   => UN_PERIODO
                                                 ,UN_PROCESO   => UN_PROCESO 
                                                 ,UN_ERROR     => 'Tiene valor de licencia de maternidad a reportar, Pero no tiene número asignado reportado. Favor revisar antes de generar archivo plano, el cual presentara inconsistencias en la entidad.'
                                                 ,UN_USUARIO   => UN_USUARIO );      
    END IF;
    IF((RS.NUMERO_AUT_PAGO_INCAPARP IS NOT NULL OR RS.NUMERO_AUT_PAGO_INCAPARP <> 0) AND RS.VALOR_TOTAL_INCAP_PAGADASARP = 0) THEN
	  PCK_NOMINA_COM6.PR_AGREGARERROR_LIQUIDACION(UN_COMPANIA  => UN_COMPANIA
                                                 ,UN_ANIO      => UN_ANIO 
                                                 ,UN_MES       => UN_MES
                                                 ,UN_PERIODO   => UN_PERIODO
                                                 ,UN_PROCESO   => UN_PROCESO 
                                                 ,UN_ERROR     => 'Tiene número de riesgos a reportar, pero no tiene valor reportado. Favor revisar antes de generar archivo plano, el cual presentara inconsistencias en la entidad.'
                                                 ,UN_USUARIO   => UN_USUARIO );
    END IF;
    IF(RS.NUMERO_AUT_PAGO_INCAPARP IS NULL AND RS.VALOR_TOTAL_INCAP_PAGADASARP <> 0) THEN
	  PCK_NOMINA_COM6.PR_AGREGARERROR_LIQUIDACION(UN_COMPANIA  => UN_COMPANIA
                                                 ,UN_ANIO      => UN_ANIO 
                                                 ,UN_MES       => UN_MES
                                                 ,UN_PERIODO   => UN_PERIODO
                                                 ,UN_PROCESO   => UN_PROCESO 
                                                 ,UN_ERROR     => 'Tiene valor de riesgos a reportar, pero no tiene número asignado reportado. Favor revisar antes de generar archivo plano, el cual presentara inconsistencias en la entidad.'
                                                 ,UN_USUARIO   => UN_USUARIO ); 
    END IF;
  END LOOP;

  FOR RS IN (SELECT
                    HISTORICOS.ANO, 
                    HISTORICOS.MES, 
                    PERSONAL_HISTORICO.NUMERO_DCTO NUMERO_DCTO, 
                    MAX(PERSONAL.NOMBRECOMPLETO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) NOMBRE,
                    SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO='212' THEN HISTORICOS.VALOR ELSE 0 END * PATRONALES.FACTOR / 100) VALORRIESGOS, 
                    SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO='112' THEN HISTORICOS.VALOR ELSE 0 END) BASE, 
                    SUM((CASE WHEN HISTORICOS.ID_DE_CONCEPTO='113' THEN HISTORICOS.VALOR ELSE 0 END)+
                        (CASE WHEN HISTORICOS.ID_DE_CONCEPTO='114' THEN HISTORICOS.VALOR ELSE 0 END)+
                          (CASE WHEN HISTORICOS.ID_DE_CONCEPTO='116' THEN HISTORICOS.VALOR ELSE 0 END)+
                            (CASE WHEN HISTORICOS.ID_DE_CONCEPTO='119' THEN HISTORICOS.VALOR ELSE 0 END)) SALUD,  
                    SUM((CASE WHEN HISTORICOS.ID_DE_CONCEPTO='117' THEN HISTORICOS.VALOR ELSE 0 END)+
                        (CASE WHEN HISTORICOS.ID_DE_CONCEPTO='118' THEN HISTORICOS.VALOR ELSE 0 END)) PENSION, 
                    SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO='111' THEN HISTORICOS.VALOR ELSE 0 END) RIESGOS
                FROM HISTORICOS 
                     INNER JOIN PERSONAL ON HISTORICOS.COMPANIA       = PERSONAL.COMPANIA
                                        AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO  
                     INNER JOIN PERIODOS ON HISTORICOS.COMPANIA       = PERIODOS.COMPANIA
                                        AND HISTORICOS.ID_DE_PROCESO  = PERIODOS.ID_DE_PROCESO 
                                        AND HISTORICOS.ANO            = PERIODOS.ANO
                                        AND HISTORICOS.MES            = PERIODOS.MES
                                        AND HISTORICOS.PERIODO = PERIODOS.PERIODO 
                     INNER JOIN PERSONAL_HISTORICO ON HISTORICOS.COMPANIA       = PERSONAL_HISTORICO.COMPANIA
                                                  AND HISTORICOS.ID_DE_PROCESO  = PERSONAL_HISTORICO.ID_DE_PROCESO 
                                                  AND HISTORICOS.ANO            = PERSONAL_HISTORICO.ANO
                                                  AND HISTORICOS.MES            = PERSONAL_HISTORICO.MES	
                                                  AND HISTORICOS.PERIODO        = PERSONAL_HISTORICO.PERIODO			
                                                  AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL_HISTORICO.ID_DE_EMPLEADO 
                     INNER JOIN PATRONALES ON PERSONAL.COMPANIA       = PATRONALES.COMPANIA 
                                          AND PERSONAL.NUMEROPATRONAL = PATRONALES.SUCURSAL

                WHERE PERSONAL.COMPANIA			    = UN_COMPANIA
                  AND HISTORICOS.ANO				= UN_ANIO
                  AND HISTORICOS.MES				= UN_MES
                  AND HISTORICOS.ID_DE_PROCESO<>'98'
                  AND PERIODOS.ACUMULADO NOT IN (0)	
                GROUP BY HISTORICOS.ANO, 
                                    HISTORICOS.MES, 
                                    PERSONAL_HISTORICO.NUMERO_DCTO
                HAVING COUNT(DISTINCT PERSONAL_HISTORICO.ID_DE_EMPLEADO)>1                    
                ORDER BY MAX(PERSONAL.NOMBRECOMPLETO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM)
                )
      LOOP
          MI_DIF:=0;
          IF(PCK_SYSMAN_UTL.FC_ROUND(RS.BASE*PCK_NOMINA_COM1.FC_PARAMETRO(UN_COMPANIA,39)/100,-2) <> RS.PENSION) THEN
              MI_DIF:=PCK_SYSMAN_UTL.FC_ROUND(RS.BASE*PCK_NOMINA_COM1.FC_PARAMETRO(UN_COMPANIA,39)/100,-2)-RS.PENSION;
			  PCK_NOMINA_COM6.PR_AGREGARERROR_LIQUIDACION(UN_COMPANIA  => UN_COMPANIA
                                                 ,UN_ANIO      => UN_ANIO 
                                                 ,UN_MES       => UN_MES
                                                 ,UN_PERIODO   => UN_PERIODO
                                                 ,UN_PROCESO   => UN_PROCESO 
                                                 ,UN_ERROR     => 'El empleado '||RS.NOMBRE||' tiene dos códigos internos y la diferencia en pensión puede presentar error en la validación del archivo integrado; la diferencia puede ser $'||MI_DIF
                                                 ,UN_USUARIO   => UN_USUARIO );
          END IF;
          IF(PCK_SYSMAN_UTL.FC_ROUND(RS.BASE*PCK_NOMINA_COM1.FC_PARAMETRO(UN_COMPANIA, 43)/100,-2) <> RS.SALUD) THEN
              MI_DIF:=PCK_SYSMAN_UTL.FC_ROUND(RS.BASE*PCK_NOMINA_COM1.FC_PARAMETRO(UN_COMPANIA, 43)/100,-2)-RS.SALUD;
			  PCK_NOMINA_COM6.PR_AGREGARERROR_LIQUIDACION(UN_COMPANIA  => UN_COMPANIA
                                                 ,UN_ANIO      => UN_ANIO 
                                                 ,UN_MES       => UN_MES
                                                 ,UN_PERIODO   => UN_PERIODO
                                                 ,UN_PROCESO   => UN_PROCESO 
                                                 ,UN_ERROR     => 'El empleado '||RS.NOMBRE||' tiene dos códigos internos y la diferencia en salud puede presentar error en la validación del archivo integrado; la diferencia puede ser $'||MI_DIF
                                                 ,UN_USUARIO   => UN_USUARIO );

          END IF;
          IF(PCK_SYSMAN_UTL.FC_ROUND(RS.VALORRIESGOS, -2)<> RS.RIESGOS) THEN
             MI_DIF:=PCK_SYSMAN_UTL.FC_ROUND(RS.VALORRIESGOS, -2) - RS.RIESGOS;
			 PCK_NOMINA_COM6.PR_AGREGARERROR_LIQUIDACION(UN_COMPANIA  => UN_COMPANIA
                                                 ,UN_ANIO      => UN_ANIO 
                                                 ,UN_MES       => UN_MES
                                                 ,UN_PERIODO   => UN_PERIODO
                                                 ,UN_PROCESO   => UN_PROCESO 
                                                 ,UN_ERROR     => 'El empleado '||RS.NOMBRE||' tiene dos códigos internos y la diferencia en riesgos puede presentar error en la validación del archivo integrado; la diferencia puede ser $'||MI_DIF
                                                 ,UN_USUARIO   => UN_USUARIO );            
          END IF;

  END LOOP;
  
   -- TICKET 7722577 ECABRERA: SE VALIDA QUE EL EMPLEADO TENGA CONFIGURADO LA ACTIVIDAD ECONOMICA CIIU
   FOR RS IN (
                SELECT  PERSONAL.NOMBRECOMPLETO NOMBRE, PERSONAL.ID_DE_EMPLEADO ID_DE_EMPLEADO
                FROM    PERSONAL_HISTORICO
                INNER JOIN PERSONAL ON PERSONAL_HISTORICO.COMPANIA = PERSONAL.COMPANIA AND PERSONAL_HISTORICO.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                WHERE   PERSONAL_HISTORICO.COMPANIA = UN_COMPANIA
                        AND PERSONAL_HISTORICO.ID_DE_PROCESO = UN_PROCESO
                        AND PERSONAL_HISTORICO.ANO = UN_ANIO
                        AND PERSONAL_HISTORICO.MES = UN_MES	
                        AND PERSONAL_HISTORICO.PERIODO = UN_PERIODO			
                        AND PERSONAL_HISTORICO.ID_CIIU IS NULL AND PERSONAL.ID_CIIU IS NULL
            ) 
   LOOP
      PCK_NOMINA_COM6.PR_AGREGARERROR_LIQUIDACION(UN_COMPANIA  => UN_COMPANIA
                                             ,UN_ANIO      => UN_ANIO 
                                             ,UN_MES       => UN_MES
                                             ,UN_PERIODO   => UN_PERIODO
                                             ,UN_PROCESO   => UN_PROCESO 
                                             ,UN_ERROR     => 'El empleado (' || RS.ID_DE_EMPLEADO || ') '||RS.NOMBRE||' no tiene configurado codigo de actividad economica CIIU'
                                             ,UN_USUARIO   => UN_USUARIO );
   END LOOP;
   -- TICKET 7722577 ECABRERA FIN
   
  
  SELECT COUNT(ERROR)
  INTO MI_RETORNO
  FROM ERRORES
  WHERE COMPANIA=UN_COMPANIA;

  RETURN MI_RETORNO;
END FC_REVISIONINCONSISTENCIAS;

--14
PROCEDURE PR_AGREGARERROR_LIQUIDACION
 /*
    NAME              : PR_AGREGARERROR_LIQUIDACION, NOMBRE EN ACCESS ALERTA 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO PACANCHIQUE
    DATE MIGRADOR     : 11/12/2015
    TIME              : 02:37 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : INSERTA EN LA TABLA ERRORES UN NUEVO ERROR DE LIQUIDACIÃ“N
    Name: NOSE MIGRA PROPIA DE PROCESO
  */
(
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA    ,
    UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO        ,
    UN_MES          IN PCK_SUBTIPOS.TI_MES         ,
    UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_PROCESO      IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ERROR        IN VARCHAR2,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO

)
AS
  MI_CONSECUTIVO      VARCHAR2(3200);
  MI_ERROR            VARCHAR2(3200);

BEGIN
  BEGIN
    SELECT ERROR 
    INTO MI_ERROR
    FROM (SELECT ERROR
          FROM ERRORES
          WHERE COMPANIA=UN_COMPANIA
          ORDER BY ERROR DESC)
    WHERE ROWNUM=1;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_ERROR:='';
  END;

  IF(MI_ERROR IS NULL) THEN
    MI_CONSECUTIVO:='000001';
  ELSE
    MI_CONSECUTIVO:=LPAD((TO_NUMBER(MI_ERROR)+1),6,'0');
  END IF;

  MI_ERROR:= PCK_DATOS.FC_ACME('ERRORES', 'I', 'COMPANIA, ID_DE_PROCESO, ERROR, DESCRIPCION, ANOE, MESE, PERIODOE', ''''||UN_COMPANIA||''','||UN_PROCESO||', '''||MI_CONSECUTIVO||''', '''||UN_ERROR||''', '||UN_ANIO||', '||UN_MES||', '||UN_PERIODO||'' );
END PR_AGREGARERROR_LIQUIDACION;

---16
FUNCTION FC_FACTOR_PARAFISCAL
 /*
    NAME              : FC_FACTOR_PARAFISCAL, NOMBRE EN ACCESS ESFACTOR_PARAFISCAL 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO PACANCHIQUE
    DATE MIGRADOR     : 17/12/2015
    TIME              : 10:23 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : EVALÃšA SI EL CONCEPTO SUMINISTRADO ES FACTOR PARAFISCAL
    @Name: factorParafiscal
  */
(
  UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CONCEPTO   IN PCK_SUBTIPOS.TI_ENTERO
)RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_RETORNO          NUMBER;
BEGIN
  SELECT CASE WHEN COUNT(*)>0 THEN -1 ELSE 0 END
  INTO MI_RETORNO
  FROM CONCEPTOS 
  WHERE CONCEPTOS.COMPANIA          =UN_COMPANIA 
    AND CONCEPTOS.ID_DE_CONCEPTO    =UN_CONCEPTO
    AND CONCEPTOS.FACTOR_PARAFISCAL <>0;    
  RETURN MI_RETORNO;
END FC_FACTOR_PARAFISCAL;

--17
FUNCTION FC_FECHACAMBIORIESGO
 /*
    NAME              : FC_FECHACAMBIORIESGO, NOMBRE EN ACCESS MIFECHACAMBIORIESGO 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO PACANCHIQUE
    DATE MIGRADOR     : 17/12/2015
    TIME              : 10:23 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : RETORNA LA FECHA EN LA QUE EL EMPLEADO CAMBIO DE FONDO DE RIESGOS.
                        SE ELIMINIÃ“ EL PARAMETRO PAR DE ESTA FUNCIÃ“N DEBIDO A QUE SI ESTE NO ERA 1 RETORNABA NULL
   @Name: fechaCambioFondoRiesgo  
  */
(
  UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_EMPLEADO   IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
)RETURN DATE
AS
  MI_RETORNO          DATE;
BEGIN
  BEGIN
    SELECT FECHAFONDORIESGOS
    INTO MI_RETORNO
    FROM PERSONAL 
    WHERE PERSONAL.COMPANIA         = UN_COMPANIA
      AND PERSONAL.ID_DE_EMPLEADO   = UN_EMPLEADO;    
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_RETORNO:=NULL;
  END;  
  RETURN MI_RETORNO;
END FC_FECHACAMBIORIESGO;

--18
FUNCTION FC_PERSONALCAMPO
 /*
    NAME              : FC_PERSONALCAMPO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO PACANCHIQUE
    DATE MIGRADOR     : 17/12/2015
    TIME              : 10:23 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : RETORNA EL VALOR DEL CAMPO DE LA TABLA PERSONAL
    @Name: getCampoPersonal
  */
(
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_IDEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_CAMPO      IN VARCHAR2
) RETURN VARCHAR2
AS
    MI_CAMPO          VARCHAR2(50) := '';
    MI_STRSQL         VARCHAR2(1000);
BEGIN

    IF(INSTR(PCK_SYSMAN_UTL.FC_LISTA_CAMPOS('PERSONAL',0,0), UN_CAMPO) <> 0) THEN
        MI_STRSQL:='SELECT '||UN_CAMPO||' FROM PERSONAL WHERE COMPANIA = '''||UN_COMPANIA||''' AND ID_DE_EMPLEADO = '||UN_IDEMPLEADO||'';
        EXECUTE IMMEDIATE MI_STRSQL INTO MI_CAMPO;
    END IF;
    RETURN MI_CAMPO;

END FC_PERSONALCAMPO;

---19
FUNCTION FC_SUCURSALRIESGO
 /*
    NAME              : FC_SUCURSALRIESGO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO PACANCHIQUE
    DATE MIGRADOR     : 17/12/2015
    TIME              : 10:23 AM
    SOURCE MODULE     : 
    MODIFIER          : MIGUEL ANGEL VENEGAS RODRIGUEZ
    DATE MODIFIED     : 12/01/2018 
    TIME              : 03:19 PM
    DESCRIPTION       : RETORNA LA SUCURSAL DE RIESGO DE LA TABLA PATRONAL
    @Name: sucursalRiesgo
  */
(
	UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
	UN_PAR			IN PCK_SUBTIPOS.TI_LOGICO
)RETURN VARCHAR2

AS
	MI_RETORNO			VARCHAR2(50):='0';
BEGIN
  BEGIN
				SELECT PATRONALES.SUCURSAL 
						INTO MI_RETORNO 
				FROM PERSONAL 
					INNER JOIN PATRONALES ON 
							PERSONAL.COMPANIA = PATRONALES.COMPANIA 
						AND PERSONAL.NUMEROPATRONAL = PATRONALES.SUCURSAL 
				WHERE PERSONAL.COMPANIA=UN_COMPANIA
					AND PERSONAL.ID_DE_EMPLEADO=UN_EMPLEADO; --) LOOP
        EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN 
        MI_RETORNO :=0;
  END;

	RETURN MI_RETORNO;   
END FC_SUCURSALRIESGO;

---20
FUNCTION FC_TRAERDATOSCAJACOMPENSACION
 /*
    NAME              : FC_TRAERDATOSCAJACOMPENSACION
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO PACANCHIQUE
    DATE MIGRADOR     : 17/12/2015
    TIME              : 10:23 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : RETORNA EL CAMPO DE LA VISTA V_CAJA_COMPENSACION DE ACUERDO AL PARÃ¿METRO DE ENTRADA
    @Name: getDatoCajaCompensacion
  */
(
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_EMPLEADO     IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_PAR          IN PCK_SUBTIPOS.TI_ENTERO
) RETURN VARCHAR2
AS
  MI_RETORNO        VARCHAR2(10);
BEGIN
  BEGIN
    SELECT DECODE(UN_PAR, 1, PAIS, 2, DEPARTAMENTOC, 3, CIUDADC, '')
    INTO MI_RETORNO
    FROM V_CAJA_COMPENSACION CC
    INNER JOIN PERSONAL ON
        CC.COMPANIA = PERSONAL.COMPANIA
      AND CC.CAJA_COMPENSACION = PERSONAL.CAJA_COMPENSACION
    WHERE CC.COMPANIA = UN_COMPANIA
      AND ID_DE_EMPLEADO = UN_EMPLEADO;

  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_RETORNO:='';
  END;
  RETURN MI_RETORNO;
END FC_TRAERDATOSCAJACOMPENSACION;

--21
FUNCTION FC_TRAERDATOSENTIDADESEMPLEADO
 /*
    NAME              : FC_TRAERDATOSENTIDADESEMPLEADO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO PACANCHIQUE
    DATE MIGRADOR     : 17/12/2015
    TIME              : 10:23 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : RETORNA INFORMACIÃ“N DEL CAMBIO DE FONDO DE UN EMPLEADO EN ESPECIFICO
    @Name: traerDatosFondoEmpleado
  */
(
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_EMPLEADO     IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_FONDOACTUAL  IN CAMBIOSDEFONDO.FONDOACTUAL%TYPE,
    UN_TIPOFONDO    IN CAMBIOSDEFONDO.TIPOFONDO%TYPE,
    UN_PAR          IN PCK_SUBTIPOS.TI_ENTERO
) RETURN VARCHAR2
AS
    MI_RETORNO        VARCHAR2(1000);
BEGIN
    BEGIN
      SELECT DECODE(UN_PAR, 1, V_FONDOS.NIT, 2, V_FONDOS.NOMBRE_FONDO, 3, V_FONDOS.CODA)
      INTO MI_RETORNO
      FROM CAMBIOSDEFONDO INNER JOIN  V_FONDOS 
          ON CAMBIOSDEFONDO.COMPANIA = V_FONDOS.COMPANIA
        AND CAMBIOSDEFONDO.FONDONUEVO = V_FONDOS.COD_FONDO
      WHERE CAMBIOSDEFONDO.COMPANIA       =UN_COMPANIA
        AND CAMBIOSDEFONDO.ID_DE_EMPLEADO =UN_EMPLEADO
        AND CAMBIOSDEFONDO.TIPOFONDO      =UN_TIPOFONDO
        AND CAMBIOSDEFONDO.FONDOACTUAL     =UN_FONDOACTUAL
      ORDER BY CAMBIOSDEFONDO.FECHA_TRASLADO DESC;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_RETORNO:='';
    END;

    RETURN MI_RETORNO;
END FC_TRAERDATOSENTIDADESEMPLEADO;

--22
FUNCTION FC_CERRARPERIODO
 /*
    NAME              : FC_CERRARPERIODO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO PACANCHIQUE
    DATE MIGRADOR     : 12/01/2015
    TIME              : 10:23 AM
    SOURCE MODULE     : 
    MODIFIER          : YESIKA PAOLA BECERRA CASTRO
    DATE MODIFIED     : 02/10/2017
    TIME              : 05:29 PM
    DESCRIPTION       : RETORNA 0 SI NO SE REALIZÃ“ EL CIERRE DE NÃ“MIMA/ Se agregaron campos de auditoría, se ajustaron excepciones
    @NAME : cerrarPeriodo 
    @METHOD:  GET
  */
(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO        IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES            IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO        IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)RETURN PCK_SUBTIPOS.TI_LOGICO
AS
    MI_VAR            NUMBER:=0;
    MI_CONDICION      VARCHAR2(32000);
BEGIN
  IF(UN_COMPANIA IS NOT NULL AND UN_PROCESO IS NOT NULL AND UN_ANIO IS NOT NULL AND UN_MES IS NOT NULL AND UN_PERIODO IS NOT NULL) THEN    
    --(APINEDA:17/10/2018)-Se realiza nuevo proceso para actualizar puntos docentes en el cierre del periodo si el empleado cumple un año de servicio el siguiente mes, TAR 1000085846 punto 3 UPC.
    IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'MANEJA CALCULO POR PUNTOS DOCENTES', UN_MODULO => PCK_DATOS.MODULONOMINA, UN_FECHA_PAR => SYSDATE) = 'SI' THEN
      BEGIN 
        BEGIN 
        MI_VAR  :=  PCK_DATOS.FC_ACME(UN_TABLA      => '(SELECT NOVEDADES.VALOR, PERSONAL.ID_DE_CARGO
                                                         FROM PERSONAL 
                                                         INNER JOIN NOVEDADES ON (PERSONAL.COMPANIA = NOVEDADES.COMPANIA)
                                                         AND (PERSONAL.ID_DE_EMPLEADO = NOVEDADES.ID_DE_EMPLEADO)
                                                         WHERE PERSONAL.COMPANIA       =  '''||UN_COMPANIA||'''                                                                                 
                                                         AND ((CASE WHEN PCK_SYSMAN_UTL.FC_MES(PERSONAL.FECHA_DE_INGRESO) = (CASE WHEN ' || UN_MES || ' + 1 > 12 THEN 1 ELSE ' || UN_MES || ' + 1 END) THEN ''' || '=' || ''' ELSE ''' || '<>' || ''' END ) = ''' || '=' || ''') 
                                                         AND PERSONAL.ESTADO_ACTUAL    = 1                          
                                                         AND PERSONAL.ESCALAFON        = 10                         
                                                         AND NOVEDADES.ID_DE_CONCEPTO  = 210                       
                                                         AND NOVEDADES.VALOR           > 0                         
                                                         AND NOVEDADES.ID_DE_PROCESO   = 0                         
                                                         AND NOVEDADES.ANO             = 0                         
                                                         AND NOVEDADES.MES             = 0                          
                                                         AND NOVEDADES.PERIODO         = 0
                                                       ) T',
                                      UN_ACCION     => 'M',                                       
                                      UN_CAMPOS     => 'T.VALOR = T.VALOR + (CASE T.ID_DE_CARGO WHEN ''06101'' THEN 2 WHEN ''06102'' THEN 3 WHEN ''06103'' THEN 4 WHEN ''06104'' THEN 5 ELSE 0 END)'
                                     );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;  
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_ACTUALIZARPUNTOSDOCENTES
            );
      END;  
    END IF;

      BEGIN 
        BEGIN 
          MI_CONDICION:=' COMPANIA      = '''||UN_COMPANIA||''' 
                      AND ID_DE_PROCESO =   '||UN_PROCESO||' 
                      AND ANO           =   '||UN_ANIO||' 
                      AND  MES          =   '||UN_MES||' 
                      AND PERIODO       =   '||UN_PERIODO;
        MI_VAR  :=  PCK_DATOS.FC_ACME(UN_TABLA      => 'PERIODOS',
                                      UN_ACCION     => 'M', 
                                      UN_CAMPOS     => 'ESTADO = 0, MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE ',
                                      UN_CONDICION  =>  MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;  
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_BANCO_PROYECTOS THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_CERRARPERIODO
            );
      END;
  END IF;
  RETURN CASE WHEN MI_VAR<>0 THEN -1 ELSE MI_VAR END;
END FC_CERRARPERIODO;

--23
FUNCTION FC_CERRARNOMINA
 /*
    NAME              : FC_CERRARNOMINA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO PACANCHIQUE
    DATE MIGRADOR     : 13/01/2016
    TIME              : 3:45 AM
    SOURCE MODULE     : 
    MODIFIER          : YESIKA PAOLA BECERRA CASTRO
    DATE MODIFIED     : 03/10/2017
    TIME              : 12:56 PM
    DESCRIPTION       : ACTUALIZA LOS FONDOS HISTÃ“RICOS DE LOS EMPLEADOS/CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN Y OPTIMIZACIÓN DEL MANEJO DE ERRORES.
    @NAME: cerrarNomina
    @METHOD:  GET

  */

(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO        IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES            IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO        IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_PAR            IN PCK_SUBTIPOS.TI_ENTERO,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)RETURN PCK_SUBTIPOS.TI_LOGICO
AS
    MI_VAR            PCK_SUBTIPOS.TI_RTA_ACME;
    MI_FECHAINI       DATE;
    MI_FECHAFIN       DATE;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXIST     PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGENOEXIST   PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_RETROACTIVO    VARCHAR2(100 CHAR):='';
    MI_SQL            PCK_SUBTIPOS.TI_STRSQL;
    MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR;


BEGIN
    MI_FECHAINI:=TRUNC(PCK_NOMINA.FC_FECHAINIFINPERIODO(UN_COMPANIA   => UN_COMPANIA, 
                                                          UN_PROCESO    => UN_PROCESO, 
                                                          UN_ANIO       => UN_ANIO, 
                                                          UN_MES        => UN_MES,
                                                          UN_PERIODO    => UN_PERIODO,
                                                          UN_FECHAINICIO  => 1));
    MI_FECHAFIN:=TRUNC(PCK_NOMINA.FC_FECHAINIFINPERIODO(UN_COMPANIA   => UN_COMPANIA,
                                                          UN_PROCESO    => UN_PROCESO,
                                                          UN_ANIO       => UN_ANIO, 
                                                          UN_MES        => UN_MES, 
                                                          UN_PERIODO    => UN_PERIODO,
                                                          UN_FECHAINICIO  => 2));
    --OpciÃ³n para actualizar Fondos

    IF UN_PAR = 1 THEN

      MI_CONDICION:='   COMPANIA  ='''||UN_COMPANIA|| ''' 
                    AND ID_DE_EMPLEADO NOT IN(0)
                    AND ((FECHA_DE_INGRESO  <=  TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'') AND ESTADO_ACTUAL <>  3) 
                    OR  (FECHA_DE_RETIRO    >=  TO_DATE(''' ||TO_CHAR(MI_FECHAINI,'DD/MM/YYYY')||''',''DD/MM/YYYY'')
                    AND FECHA_DE_INGRESO    <=  TO_DATE(''' ||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')|| ''',''DD/MM/YYYY'') 
                    AND ESTADO_ACTUAL       =   3))';
      /*JP 21/02/2018 SE DEBE GUARDAR LOS DATOS POR PERIODO AL FINALIZAR LA LIQUIDACION PARÁ EVITAR QUE ENTRE LA LIQUIDACION Y EL CIERRE NO CAMBIE NADA              
      PCK_NOMINA.PR_INCLUIRNOVEDADFONDOS( UN_COMPANIA   => UN_COMPANIA, 
                                          UN_PROCESO    => UN_PROCESO,
                                          UN_ANIO       => UN_ANIO, 
                                          UN_MES        => UN_MES, 
                                          UN_PERIODO    => UN_PERIODO, 
                                          UN_CONDICION  => MI_CONDICION,
                                          UN_USUARIO    => UN_USUARIO);
        */                                  
      BEGIN 
        BEGIN 
          MI_MERGEUSING:='SELECT  TIPORET, 
                                  COMPANIA, 
                                  ID_DE_EMPLEADO, 
                                  ID_DE_PROCESO, 
                                  ANO, 
                                  MES, 
                                  PERIODO
                          FROM PERSONAL_HISTORICO
                          WHERE COMPANIA      = '||UN_COMPANIA||'
                            AND ID_DE_PROCESO = '||UN_PROCESO||'
                            AND ANO           = '||UN_ANIO||'
                            AND MES           = '||UN_MES||'';
          MI_MERGEENLACE:=' TABLA.COMPANIA          = VISTA.COMPANIA
                        AND TABLA.ID_DE_PROCESO     = VISTA.ID_DE_PROCESO
                        AND TABLA.ANO               = VISTA.ANO
                        AND TABLA.MES               = VISTA.MES
                        AND TABLA.PERIODO           = VISTA.PERIODO
                        AND TABLA.ID_DE_EMPLEADO    = VISTA.ID_DE_EMPLEADO';
          MI_MERGEEXIST:='UPDATE SET 
                            TABLA.TIPORET           = VISTA.TIPORET ,
                            TABLA.MODIFIED_BY       = '''||UN_USUARIO||''',
                            TABLA.DATE_MODIFIED     = SYSDATE
                          WHERE TABLA.COMPANIA      = '||UN_COMPANIA||' 
                            AND TABLA.ID_DE_PROCESO = '||UN_PROCESO||' 
                            AND TABLA.ANO           = '||UN_ANIO||' 
                            AND TABLA.MES           = '||UN_MES||'';


          MI_VAR  :=  PCK_DATOS.FC_ACME(  UN_TABLA        => 'RETEFUENTE_CALCULOS', 
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
                          UN_ERROR_COD  => PCK_ERRORES.ERR_ACTRETEFUENTE
                      );
      END;   
    ---Sube los totales por fondo en el periodo establecido
    ELSIF UN_PAR = 2 THEN
      --Elimina todas las novedades para el periodo establecido
      MI_STRSQL:='  COMPANIA      = '''||UN_COMPANIA||''' 
                AND ID_DE_PROCESO = '||UN_PROCESO||' 
                AND ANO           = '||UN_ANIO||' 
                AND MES           = '||UN_MES;

      --Para periodo retroactivo
      IF UN_PERIODO = 5 THEN
        MI_RETROACTIVO  :=  ' AND PERIODO = 5';
      END IF;
      BEGIN 
        BEGIN 
          MI_VAR:=  PCK_DATOS.FC_ACME(UN_TABLA      => 'NOVEDADES_AUTOLIQUIDACION', 
                                      UN_ACCION     => 'E',
                                      UN_CONDICION  => MI_STRSQL||MI_RETROACTIVO);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE,
                          UN_ERROR_COD  => PCK_ERRORES.ERR_ELINOVAUTO
                      );
      END;                                   
      --Ingresa Fondo PensiÃ³n
      MI_STRSQL:='SELECT V_ACUMULADOS.COMPANIA, 
                        V_ACUMULADOS.ID_DE_PROCESO,
                        V_ACUMULADOS.ANO, 
                        V_ACUMULADOS.MES, 
                        V_ACUMULADOS.PERIODO PERIODO,
                        ''01'' TIPO_ADMINISTRADORA,
                        V_FONDO_DE_PENSIONES.ID_DEL_FONDO CODIGO_FONDO,
                        V_ACUMULADOS.NOMBRE_FONDOPENSION,
                        V_FONDO_DE_PENSIONES.NIT,
                        V_FONDO_DE_PENSIONES.SUCURSAL,
                        SUM(CASE WHEN ID_DE_CONCEPTO  = 120 
                                THEN VALOR 
                                ELSE 0 
                            END +
                            CASE WHEN ID_DE_CONCEPTO  = 117 
                              THEN VALOR 
                              ELSE 0 
                            END +
                            CASE WHEN ID_DE_CONCEPTO  = 115 
                              THEN VALOR 
                              ELSE 0 
                            END + 
                            CASE WHEN ID_DE_CONCEPTO  = 118 
                              THEN VALOR 
                              ELSE 0 
                            END) TOTAL_PAGO_PENSION,
                        PCK_NOMINA_COM6.FC_TOTAL_AFILIADOS('''||UN_COMPANIA||''',V_FONDO_DE_PENSIONES.ID_DEL_FONDO, ''AFP'', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')' || ', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')) TOTAL_AFILIADOS,
                        SUM(CASE WHEN ID_DE_CONCEPTO  =117 
                              THEN VALOR 
                              ELSE 0 
                            END + 
                            CASE WHEN ID_DE_CONCEPTO  = 118 
                              THEN VALOR 
                              ELSE 0 
                            END) PENSION_OBLIGATORIA,
                        SUM(CASE WHEN ID_DE_CONCEPTO  = 115 
                              THEN VALOR 
                              ELSE 0 
                            END) SOLIDARIDAD, 
                        SUM(CASE WHEN ID_DE_CONCEPTO  = 120 
                              THEN VALOR 
                              ELSE 0 
                            END) ADICIONALSOLIDARIDAD,
                        '''||UN_USUARIO||''' CREATED_BY,
                        SYSDATE DATE_CREATED
                      FROM V_ACUMULADOS 
                        INNER JOIN V_FONDO_DE_PENSIONES
                            ON V_ACUMULADOS.COMPANIA      = V_FONDO_DE_PENSIONES.COMPANIA
                          AND V_ACUMULADOS.FONDO_PENSION  = V_FONDO_DE_PENSIONES.ID_DEL_FONDO
                      WHERE V_ACUMULADOS.COMPANIA         = '''||UN_COMPANIA||'''
                          AND V_ACUMULADOS.ID_DE_PROCESO  = '||UN_PROCESO||'
                          AND V_ACUMULADOS.ANO            = '||UN_ANIO||'
                          AND V_ACUMULADOS.MES            = '||UN_MES||MI_RETROACTIVO||' 
                          AND V_ACUMULADOS.ID_DE_CONCEPTO IN (112, 115, 117, 118, 131, 132, 134, 120)
                      GROUP BY  V_ACUMULADOS.COMPANIA, 
                                V_ACUMULADOS.ID_DE_PROCESO, 
                                V_ACUMULADOS.ANO, 
                                V_ACUMULADOS.MES, 
                                V_FONDO_DE_PENSIONES.NIT, 
                                V_FONDO_DE_PENSIONES.ID_DEL_FONDO, 
                                V_ACUMULADOS.NOMBRE_FONDOPENSION, 
                                V_ACUMULADOS.PERIODO, 
                                V_FONDO_DE_PENSIONES.SUCURSAL
                      HAVING SUM(CASE WHEN ID_DE_CONCEPTO IN (131,118,132,115,120,117) THEN VALOR ELSE 0 END) <> 0';


      MI_CAMPOS:='  COMPANIA, 
                    ID_DE_PROCESO, 
                    ANO, 
                    MES, 
                    PERIODO, 
                    TIPO_ADMINISTRADORA, 
                    CODIGO_FONDO, 
                    NOMBRE_FONDO,
                    NIT, 
                    SUCURSAL, 
                    TOTAL_PAGO_PENSION,
                    TOTAL_AFILIADOS_PENSION, 
                    VALOR_PENSION_OBLIGATORIA, 
                    VALOR_TOTAL_FSP, 
                    VALOR_TOTAL_FSP_ADIC,
                    CREATED_BY,
                    DATE_CREATED';

      BEGIN 
        BEGIN 
          MI_VAR  :=  PCK_DATOS.FC_ACME(UN_TABLA    => 'NOVEDADES_AUTOLIQUIDACION', 
                                        UN_ACCION   => 'IS', 
                                        UN_CAMPOS   => MI_CAMPOS, 
                                        UN_VALORES  => MI_STRSQL);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;                                
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
          MI_REEMPLAZOS(1).CLAVE := 'APORTES';
          MI_REEMPLAZOS(1).VALOR := 'Fondo de Pensión';
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_INSERSELEC,
              UN_REEMPLAZOS =>  MI_REEMPLAZOS
            );
        END;
      --------------------------------------------------------------
      IF PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA   => UN_COMPANIA, 
                                UN_NOMBRE     => 'PAGAR APORTES VOLUNTARIOS EN PLANILLA PILA',
                                UN_MODULO     => PCK_DATOS.FC_MODULONOMINA, 
                                UN_FECHA_PAR  => SYSDATE) = 'SI' THEN
        --Aportes voluntarios en pensiÃ³n por NIT
        MI_STRSQL:='SELECT V_ACUMULADOS.COMPANIA, 
                            V_ACUMULADOS.ID_DE_PROCESO,
                            V_ACUMULADOS.ANO, 
                            V_ACUMULADOS.MES,
                            V_ACUMULADOS.PERIODO PERIODO,
                            ''01'' TIPO_ADMINISTRADORA,
                            V_ACUMULADOS.FONDO_PENSION CODIGO_FONDO,
                            V_FONDO_DE_PENSION_VOL.NOMBRE_DEL_FONDO,
                            V_FONDO_DE_PENSION_VOL.NIT NIT, 
                            V_FONDO_DE_PENSION_VOL.SUCURSAL,
                            0 VALOR_PENSION_OBLIGATORIA,
                            SUM(CASE WHEN CODAIFP = CODAIPV
                                  THEN 
                                    CASE WHEN ID_DE_CONCEPTO  = 124 
                                      THEN VALOR 
                                      ELSE 0 
                                    END 
                                  ELSE 0 
                                END) TOL,
                            SUM(CASE WHEN ID_DE_CONCEPTO  = 124 
                                  THEN VALOR 
                                  ELSE 0 
                                END) APVOL, 
                            0 VALOR_AP_VOL_APORTANTE,
                            0 VALOR_TOTAL_FSP,
                            PCK_NOMINA_COM6.FC_TOTAL_AFILIADOS('''||UN_COMPANIA||''',V_FONDO_DE_PENSIONES.ID_DEL_FONDO, ''AFP'', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')' || ', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')) TOTAL_AFILIADOS,
                            '''||UN_USUARIO||''' CREATED_BY,
                            SYSDATE DATE_CREATED
                      FROM V_ACUMULADOS 
                        INNER JOIN V_FONDO_DE_PENSION_VOL
                            ON V_ACUMULADOS.COMPANIA          = V_FONDO_DE_PENSION_VOL.COMPANIA
                          AND V_ACUMULADOS.FONDO_PENSION_VOL  = V_FONDO_DE_PENSION_VOL.ID_DEL_FONDO
                        INNER JOIN V_FONDO_DE_PENSIONES
                            ON V_ACUMULADOS.COMPANIA          = V_FONDO_DE_PENSIONES.COMPANIA
                          AND V_ACUMULADOS.FONDO_PENSION      = V_FONDO_DE_PENSIONES.ID_DEL_FONDO
                      WHERE V_ACUMULADOS.COMPANIA             = '''||UN_COMPANIA||'''
                        AND V_ACUMULADOS.ID_DE_PROCESO        = '||UN_PROCESO||'
                        AND V_ACUMULADOS.ANO                  = '||UN_ANIO||'
                        AND V_ACUMULADOS.MES                  = '||UN_MES||MI_RETROACTIVO||'
                        AND V_ACUMULADOS.ID_DE_CONCEPTO IN (124,127)
                      GROUP BY  V_ACUMULADOS.COMPANIA, 
                                V_ACUMULADOS.ID_DE_PROCESO,
                                V_ACUMULADOS.ANO,
                                V_ACUMULADOS.MES, 
                                V_ACUMULADOS.PERIODO, 
                                V_ACUMULADOS.FONDO_PENSION, 
                                V_FONDO_DE_PENSION_VOL.NOMBRE_DEL_FONDO, 
                                V_FONDO_DE_PENSION_VOL.NIT, 
                                V_FONDO_DE_PENSION_VOL.SUCURSAL,
                                V_FONDO_DE_PENSIONES.ID_DEL_FONDO';

          MI_CAMPOS:='  COMPANIA,  
                        ID_DE_PROCESO, 
                        ANO, 
                        MES, 
                        PERIODO, 
                        TIPO_ADMINISTRADORA, 
                        CODIGO_FONDO, 
                        NOMBRE_FONDO, 
                        NIT, 
                        SUCURSAL, 
                        VALOR_PENSION_OBLIGATORIA, 
                        VALOR_AP_VOL_AFILIADO, 
                        TOTAL_PAGO_PENSION, 
                        VALOR_AP_VOL_APORTANTE,
                        VALOR_TOTAL_FSP,
                        TOTAL_AFILIADOS_PENSION,
                        CREATED_BY,
                        DATE_CREATED';
          BEGIN 
            BEGIN 
              MI_VAR  :=  PCK_DATOS.FC_ACME(UN_TABLA    => 'NOVEDADES_AUTOLIQUIDACION', 
                                            UN_ACCION   => 'IS', 
                                            UN_CAMPOS   => MI_CAMPOS, 
                                            UN_VALORES  => MI_STRSQL);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;                                
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_REEMPLAZOS(1).CLAVE := 'APORTES';
            MI_REEMPLAZOS(1).VALOR := 'voluntarios en pensión por NIT';
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_INSERSELEC,
              UN_REEMPLAZOS =>  MI_REEMPLAZOS
            );
        END;
        END IF;
        --------------------------------------------------------------------------------------------------------------------------------------------------
        ---APORTES A SALUD
          MI_STRSQL:='SELECT SUM( CASE WHEN ID_DE_CONCEPTO=  113 
                                    THEN VALOR 
                                    ELSE 0 
                                  END + 
                                  CASE WHEN ID_DE_CONCEPTO  = 114 
                                    THEN VALOR 
                                    ELSE 0 
                                  END + 
                                  CASE WHEN ID_DE_CONCEPTO  = 116 
                                    THEN VALOR 
                                    ELSE 0 
                                  END + 
                                  CASE WHEN ID_DE_CONCEPTO  = 119 
                                    THEN VALOR 
                                    ELSE 0 
                                  END),
                        V_ACUMULADOS.COMPANIA, 
                        V_ACUMULADOS.ID_DE_PROCESO, 
                        V_ACUMULADOS.ANO, 
                        V_ACUMULADOS.MES, 
                        V_ACUMULADOS.PERIODO PERIODO, 
                        ''02'' TIPO_ADMINISTRADORA,
                        V_FONDO_DE_SALUD.FONDO_SALUD CODIGO_FONDO,
                        V_FONDO_DE_SALUD.NOMBRE_FONDO_SALUD, 
                        V_FONDO_DE_SALUD.NIT, 
                        V_FONDO_DE_SALUD.SUCURSAL,
                        SUM(CASE WHEN ID_DE_CONCEPTO  = 123 
                              THEN VALOR 
                              ELSE 0 
                            END ) UPC,
                        SUM(CASE WHEN ID_DE_CONCEPTO  = 398 
                              THEN ABS(VALOR) 
                              ELSE 0 
                            END ) DTOINCAP,
                        SUM(CASE WHEN ID_DE_CONCEPTO  = 399 
                              THEN ABS(VALOR) 
                              ELSE 0 
                            END ) DTOMATERNIDAD, 
                        SUM(CASE WHEN ID_DE_CONCEPTO  = 113 
                              THEN VALOR 
                              ELSE 0 
                            END + 
                            CASE WHEN ID_DE_CONCEPTO  = 114 
                              THEN VALOR 
                              ELSE 0 
                            END + 
                            CASE WHEN ID_DE_CONCEPTO  = 116 
                              THEN VALOR 
                              ELSE 0 
                            END + 
                            CASE WHEN ID_DE_CONCEPTO  = 119 
                              THEN VALOR 
                              ELSE 0 
                            END) VALOR_NETO,
                            0 DIAS_MORA_SALUD,
                            0 INTERES_MORA,
                            0 INTERES_MORA_UPC,
                            SUM(CASE WHEN ID_DE_CONCEPTO  = 113 
                                  THEN VALOR 
                                  ELSE 0 
                                END + 
                                CASE WHEN ID_DE_CONCEPTO  = 114 
                                  THEN VALOR 
                                ELSE 0 
                                END + 
                                CASE WHEN ID_DE_CONCEPTO  = 116 
                                  THEN VALOR 
                                  ELSE 0 
                                END + 
                                CASE WHEN ID_DE_CONCEPTO  = 119 
                                  THEN VALOR 
                                  ELSE 0 
                                END) SUBTOTAL_APORTES,
                            SUM(CASE WHEN ID_DE_CONCEPTO  = 123 
                                  THEN VALOR 
                                  ELSE 0 
                                END ) SUBTOTAL_APORTES_UPC,
                              0 SALDO_FAVOR,
                              0 SALDO_FAVORUPTC,
                              SUM(CASE WHEN ID_DE_CONCEPTO  = 113 
                                    THEN VALOR 
                                    ELSE 0 
                                  END + 
                                  CASE WHEN ID_DE_CONCEPTO  = 114 
                                    THEN VALOR 
                                    ELSE 0 
                                  END + 
                                  CASE WHEN ID_DE_CONCEPTO = 116 
                                    THEN VALOR 
                                    ELSE 0 
                                  END + 
                                  CASE WHEN ID_DE_CONCEPTO  = 119 
                                    THEN VALOR 
                                    ELSE 0 
                                  END)  TOTAL_COTIZACION_OBLIGAT_SALUD,
                              SUM(CASE WHEN ID_DE_CONCEPTO  = 123 
                                    THEN VALOR 
                                    ELSE 0 
                                  END) UPC_ADICIONALES,
                              SUM(CASE WHEN ID_DE_CONCEPTO  = 113 
                                    THEN VALOR 
                                    ELSE 0 
                                  END + 
                                  CASE WHEN ID_DE_CONCEPTO  = 114 
                                    THEN VALOR 
                                    ELSE 0 
                                  END + 
                                  CASE WHEN ID_DE_CONCEPTO  = 116 
                                    THEN VALOR 
                                    ELSE 0 
                                  END + 
                                  CASE WHEN ID_DE_CONCEPTO  = 119 
                                    THEN VALOR 
                                    ELSE 0 
                                  END + 
                                  CASE WHEN ID_DE_CONCEPTO  = 123 
                                    THEN VALOR 
                                    ELSE 0 
                                  END) TOTAL_PAGO_SALUD,
                              PCK_NOMINA_COM6.FC_TOTAL_AFILIADOS('''||UN_COMPANIA||''',V_FONDO_DE_SALUD.FONDO_SALUD, ''EPS'', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')' || ', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')) TOTAL_AFILIADOS,
                              PCK_SYSMAN_UTL.FC_ROUND(SUM(CASE WHEN ID_DE_CONCEPTO  = 112 
                                                            THEN VALOR 
                                                            ELSE 0 
                                                          END )/100,0) IBC,
                              '''||UN_USUARIO||''' CREATED_BY,
                              SYSDATE DATE_CREATED                            
                        FROM V_ACUMULADOS
                          INNER JOIN V_FONDO_DE_SALUD ON
                              V_ACUMULADOS.COMPANIA       = V_FONDO_DE_SALUD.COMPANIA
                            AND V_ACUMULADOS.FONDO_SALUD  = V_FONDO_DE_SALUD.FONDO_SALUD
                        WHERE V_ACUMULADOS.COMPANIA       = '''||UN_COMPANIA||'''
                          AND V_ACUMULADOS.ID_DE_PROCESO  = '||UN_PROCESO||'
                          AND V_ACUMULADOS.ANO            = '||UN_ANIO||'
                          AND V_ACUMULADOS.MES            = '||UN_MES||MI_RETROACTIVO||' 
                          AND V_ACUMULADOS.ID_DE_CONCEPTO IN (112,113,114,116,119,130,131,132,134,123,369,399,398)  
                        GROUP BY  V_ACUMULADOS.COMPANIA, 
                                  V_ACUMULADOS.ID_DE_PROCESO, 
                                  V_ACUMULADOS.ANO, 
                                  V_ACUMULADOS.MES, 
                                  V_ACUMULADOS.PERIODO, 
                                  V_FONDO_DE_SALUD.FONDO_SALUD, 
                                  V_FONDO_DE_SALUD.NOMBRE_FONDO_SALUD, 
                                  V_FONDO_DE_SALUD.NIT, 
                                  V_FONDO_DE_SALUD.SUCURSAL, 
                                  V_FONDO_DE_SALUD.FONDO_SALUD 
                        HAVING  SUM(CASE WHEN ID_DE_CONCEPTO=123 
                                      THEN VALOR 
                                      ELSE 0 
                                    END +
                                    CASE WHEN ID_DE_CONCEPTO  = 113 
                                      THEN VALOR 
                                      ELSE 0 
                                    END +
                                    CASE WHEN ID_DE_CONCEPTO  = 114 
                                      THEN VALOR 
                                      ELSE 0 
                                    END +
                                    CASE WHEN ID_DE_CONCEPTO  = 116 
                                      THEN VALOR 
                                      ELSE 0 
                                    END +
                                    CASE WHEN ID_DE_CONCEPTO  = 119 
                                      THEN VALOR 
                                      ELSE 0 
                                    END +
                                    CASE WHEN ID_DE_CONCEPTO  = 130 
                                      THEN VALOR 
                                      ELSE 0 
                                    END ) <> 0';

        MI_CAMPOS:='  VALOR_SALUD_OBLIGATORIA, 
                      COMPANIA, 
                      ID_DE_PROCESO, 
                      ANO, 
                      MES, 
                      PERIODO, 
                      TIPO_ADMINISTRADORA, 
                      CODIGO_FONDO, 
                      NOMBRE_FONDO,
                      NIT,
                      SUCURSAL, 
                      VALOR_TOTAL_UPC_ADICIONALES, 
                      VR_INCAP, 
                      VR_MAT, 
                      VALOR_NETO_APORTES_SALUD, 
                      DIAS_MORA_SALUD, 
                      VALOR_INTERESES_MORA_SALUD, 
                      VALOR_INTERESES_MORA_UPC, 
                      SUBTOTAL_APORTES_COTIZACION, 
                      SUBTOTAL_APORTES_UPC_ADICIONAL,  
                      VALOR_SALDO_FAVOR, 
                      VALOR_SALDO_FAVORUPC, 
                      TOTAL_COTIZACION_OBLIGAT_SALUD, 
                      TOTAL_UPC_ADICIONALES, 
                      TOTAL_PAGO_SALUD, 
                      TOTAL_AFILIADOS_SALUD, 
                      VALOR_FSGS,
                      CREATED_BY,
                      DATE_CREATED';

        BEGIN 
            BEGIN 
              MI_VAR  :=  PCK_DATOS.FC_ACME(UN_TABLA    => 'NOVEDADES_AUTOLIQUIDACION', 
                                            UN_ACCION   => 'IS', 
                                            UN_CAMPOS   => MI_CAMPOS, 
                                            UN_VALORES  => MI_STRSQL);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;                                
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_REEMPLAZOS(1).CLAVE := 'APORTES';
            MI_REEMPLAZOS(1).VALOR := 'a Salud';
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_INSERSELEC,
              UN_REEMPLAZOS =>  MI_REEMPLAZOS
            );
        END;
        --------------------------------------------------------------------------------------
        --Aporte fondo de riesgos
        MI_STRSQL:='SELECT 
                      V_ACUMULADOS.COMPANIA,
                      V_ACUMULADOS.ID_DE_PROCESO,
                      V_ACUMULADOS.ANO, 
                      V_ACUMULADOS.MES, 
                      V_ACUMULADOS.PERIODO,
                      ''03'' TIPO_ADMINISTRADORA,
                      V_FONDO_DE_RIESGOS.FONDO_RIESGOS CODIGO_FONDO,
                      V_FONDO_DE_RIESGOS.NOMBRE_FONDO_RIESGOS,
                      V_FONDO_DE_RIESGOS.NIT, 
                      V_FONDO_DE_RIESGOS.SUCURSAL,
                      SUM(CASE WHEN ID_DE_CONCEPTO  = 111 
                            THEN VALOR 
                            ELSE 0 
                          END) RIESGOPATRONO,
                      SUM(CASE WHEN ID_DE_CONCEPTO  = 397 
                            THEN VALOR 
                            ELSE 0 
                          END) DTORIESGOS,
                      0 VALOR_AP_PAGADOS_OTROS_RIESGOS,
                      SUM(CASE WHEN ID_DE_CONCEPTO  = 111 
                            THEN VALOR 
                            ELSE 0 
                          END) TOTAL_COTIZACION,
                      0 DIAS_MORA_ARP,
                      0 VALOR_INTERESES_MORA_ARP,
                      SUM(CASE WHEN ID_DE_CONCEPTO  = 111 
                            THEN VALOR 
                            ELSE 0 
                          END) SUBTOTAL_APORTES_ARP,
                      0 VALOR_SALDOPERIODOANTERIORARP,
                      SUM(CASE WHEN ID_DE_CONCEPTO  = 111 
                            THEN VALOR 
                            ELSE 0 
                          END - 
                          CASE WHEN ID_DE_CONCEPTO  = 397 
                            THEN VALOR 
                            ELSE 0 
                          END) VALOR_TOTAL_NETO_ARP,
                      PCK_SYSMAN_UTL.FC_ROUND(SUM(CASE WHEN ID_DE_CONCEPTO  = 111 
                                                    THEN VALOR 
                                                    ELSE 0 
                                                  END)/100,0) RIESGOPATRONO1,
                      PCK_NOMINA_COM6.FC_TOTAL_AFILIADOS('''||UN_COMPANIA||''',V_FONDO_DE_RIESGOS.FONDO_RIESGOS, ''ARL'', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')' || ', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')) TOTAL_AFILIADOS,
                      '''||UN_USUARIO||''' CREATED_BY,
                      SYSDATE DATE_CREATED        
                    FROM V_ACUMULADOS
                      INNER JOIN V_FONDO_DE_RIESGOS 
                        ON  V_ACUMULADOS.COMPANIA       = V_FONDO_DE_RIESGOS.COMPANIA
                        AND V_ACUMULADOS.FONDO_RIESGOS  = V_FONDO_DE_RIESGOS.FONDO_RIESGOS
                    WHERE V_ACUMULADOS.COMPANIA         = '''||UN_COMPANIA||'''
                      AND V_ACUMULADOS.ID_DE_PROCESO    = '||UN_PROCESO||'
                      AND V_ACUMULADOS.ANO              = '||UN_ANIO||'
                      AND V_ACUMULADOS.MES              = '||UN_MES||MI_RETROACTIVO||' 
                      AND ID_DE_CONCEPTO IN (112, 111, 397)
                    GROUP BY  V_ACUMULADOS.COMPANIA, 
                              V_ACUMULADOS.ID_DE_PROCESO, 
                              V_ACUMULADOS.ANO, 
                              V_ACUMULADOS.MES, 
                              V_ACUMULADOS.PERIODO, 
                              V_FONDO_DE_RIESGOS.FONDO_RIESGOS, 
                              V_FONDO_DE_RIESGOS.NOMBRE_FONDO_RIESGOS, 
                              V_FONDO_DE_RIESGOS.NIT, 
                              V_FONDO_DE_RIESGOS.SUCURSAL, 
                              V_FONDO_DE_RIESGOS.FONDO_RIESGOS 
                    HAVING SUM(CASE WHEN ID_DE_CONCEPTO = 111 
                                  THEN VALOR 
                                  ELSE 0 
                               END)<>0';

        MI_CAMPOS:='  COMPANIA, 
                      ID_DE_PROCESO, 
                      ANO, 
                      MES, 
                      PERIODO, 
                      TIPO_ADMINISTRADORA, 
                      CODIGO_FONDO, 
                      NOMBRE_FONDO,
                      NIT,SUCURSAL, 
                      TOTAL_APORTES_ARP, 
                      VALOR_TOTAL_INCAP_PAGADASARP,
                      VALOR_AP_PAGADOS_OTROS_RIESGOS,
                      VALOR_TOTAL_COTIZACION, 
                      DIAS_MORA_ARP, 
                      VALOR_INTERESES_MORA_ARP, 
                      SUBTOTAL_APORTES_ARP,
                      VALOR_SALDOPERIODOANTERIORARP,
                      VALOR_TOTAL_NETO_ARP,
                      VALOR_FONDO_SOLIDARIDAD_ARP, 
                      TOTAL_AFILIADOS_RIESGOS,
                      CREATED_BY,
                      DATE_CREATED';

        BEGIN 
          BEGIN 
            MI_VAR  :=  PCK_DATOS.FC_ACME(UN_TABLA    => 'NOVEDADES_AUTOLIQUIDACION', 
                                          UN_ACCION   => 'IS', 
                                          UN_CAMPOS   => MI_CAMPOS, 
                                          UN_VALORES  => MI_STRSQL);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;                                
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_REEMPLAZOS(1).CLAVE := 'APORTES';
            MI_REEMPLAZOS(1).VALOR := 'Fondo de riesgo';
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_INSERSELEC,
              UN_REEMPLAZOS =>  MI_REEMPLAZOS
            );
        END;
      ----------------------------------------------------------------------------------------------------------------------------------
        --Cajas de compensanciÃ³n
        MI_STRSQL:='SELECT  V_ACUMULADOS.COMPANIA, 
                            V_ACUMULADOS.ID_DE_PROCESO,
                            V_ACUMULADOS.ANO, 
                            V_ACUMULADOS.MES,
                            V_ACUMULADOS.PERIODO,
                            ''04'' TIPO_ADMINISTRADORA,
                            V_CAJA_COMPENSACION.CAJA_COMPENSACION CODIGO_FONDO, 
                            V_CAJA_COMPENSACION.NOMBRE_CAJA,
                            V_CAJA_COMPENSACION.NIT,
                            V_CAJA_COMPENSACION.SUCURSAL,
                            SUM(CASE WHEN ID_DE_CONCEPTO  = 101 
                                  THEN VALOR 
                                  ELSE 0 
                                END) CAJAS,
                            0 DIAS_MORA_CAJAS,
                            0 VALOR_INTERESES_MORA_CAJAS,
                            SUM(CASE WHEN ID_DE_CONCEPTO  = 101 
                                  THEN VALOR 
                                  ELSE 0 
                                END) TOTAL_NETO_CAJAS,
                            PCK_NOMINA_COM6.FC_TOTAL_AFILIADOS('''||UN_COMPANIA||''',V_CAJA_COMPENSACION.CAJA_COMPENSACION, ''CCF'', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')' || ', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')) TOTAL_AFILIADOS,
                              '''||UN_USUARIO||''' CREATED_BY,
                              SYSDATE DATE_CREATED        
                   FROM V_ACUMULADOS
                      INNER JOIN V_CAJA_COMPENSACION
                          ON V_ACUMULADOS.COMPANIA          = V_CAJA_COMPENSACION.COMPANIA
                        AND V_ACUMULADOS.CAJA_COMPENSACION  = V_CAJA_COMPENSACION.CAJA_COMPENSACION
                   WHERE V_ACUMULADOS.COMPANIA              = '''||UN_COMPANIA||'''
                     AND V_ACUMULADOS.ID_DE_PROCESO         = '||UN_PROCESO||'
                     AND V_ACUMULADOS.ANO                   = '||UN_ANIO||'
                     AND V_ACUMULADOS.MES                   = '||UN_MES||MI_RETROACTIVO||'
                     AND V_ACUMULADOS.ID_DE_CONCEPTO = 101 
                    GROUP BY  V_ACUMULADOS.COMPANIA, 
                              V_ACUMULADOS.ID_DE_PROCESO, 
                              V_ACUMULADOS.ANO,
                              V_ACUMULADOS.MES, 
                              V_ACUMULADOS.PERIODO,
                              V_CAJA_COMPENSACION.CAJA_COMPENSACION, 
                              V_CAJA_COMPENSACION.NOMBRE_CAJA, 
                              V_CAJA_COMPENSACION.NIT, 
                              V_CAJA_COMPENSACION.SUCURSAL, 
                              V_CAJA_COMPENSACION.CAJA_COMPENSACION';

        MI_CAMPOS:='COMPANIA, 
                    ID_DE_PROCESO, 
                    ANO, 
                    MES, 
                    PERIODO, 
                    TIPO_ADMINISTRADORA, 
                    CODIGO_FONDO, 
                    NOMBRE_FONDO,
                    NIT, 
                    SUCURSAL, 
                    VALOR_APORTES_CAJAS, 
                    DIAS_MORA_CAJAS, 
                    VALOR_INTERESES_MORA_CAJAS, 
                    TOTAL_NETO_CAJAS, 
                    TOTAL_AFILIADOS_CAJAS,
                    CREATED_BY,
                    DATE_CREATED';

        BEGIN 
            BEGIN 
              MI_VAR  :=  PCK_DATOS.FC_ACME(UN_TABLA    => 'NOVEDADES_AUTOLIQUIDACION', 
                                            UN_ACCION   => 'IS', 
                                            UN_CAMPOS   => MI_CAMPOS, 
                                            UN_VALORES  => MI_STRSQL);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;                                
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_REEMPLAZOS(1).CLAVE := 'APORTES';
            MI_REEMPLAZOS(1).VALOR := 'Cajas de Compensación';
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_INSERSELEC,
              UN_REEMPLAZOS =>  MI_REEMPLAZOS
            );
        END;

      -----------------------------------------------------------------------------------------------------------------
      --Aportes Sena
        MI_STRSQL:='SELECT  V_ACUMULADOS.COMPANIA,
                            V_ACUMULADOS.ID_DE_PROCESO,
                            V_ACUMULADOS.ANO, 
                            V_ACUMULADOS.MES,
                            V_ACUMULADOS.PERIODO,
                            ''05'' TIPO_ADMINISTRADORA,
                            V_CAJA_COMPENSACION.CAJA_COMPENSACION CODIGO_FONDO, 
                            V_CAJA_COMPENSACION.NOMBRE_CAJA, 
                            V_CAJA_COMPENSACION.NIT, 
                            V_CAJA_COMPENSACION.SUCURSAL,
                            ''01'' TIPO_CONCEPTO_SENA,
                            SUM(CASE WHEN ID_DE_CONCEPTO  = 103 
                                  THEN VALOR 
                                  ELSE 0 
                                END) SENA, 
                            0 DIAS_MORA,
                            0 INTERES_MORA,
                            SUM(CASE WHEN ID_DE_CONCEPTO  = 103 
                                  THEN VALOR 
                                  ELSE 0 
                                END) VALOR_TOTAL, 
                             PCK_NOMINA_COM6.FC_TOTAL_AFILIADOS('''||UN_COMPANIA||''',V_CAJA_COMPENSACION.CAJA_COMPENSACION, ''CCF'', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')' || ', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')) TOTAL_AFILIADOS,
                              '''||UN_USUARIO||''' CREATED_BY,
                              SYSDATE DATE_CREATED        
                    FROM V_ACUMULADOS 
                      INNER JOIN V_CAJA_COMPENSACION
                        ON V_ACUMULADOS.COMPANIA            = V_CAJA_COMPENSACION.COMPANIA
                        AND V_ACUMULADOS.CAJA_COMPENSACION  = V_CAJA_COMPENSACION.CAJA_COMPENSACION
                    WHERE V_ACUMULADOS.COMPANIA             = '''||UN_COMPANIA||'''
                      AND V_ACUMULADOS.ID_DE_PROCESO        = '||UN_PROCESO||'
                      AND V_ACUMULADOS.ANO                  = '||UN_ANIO||'
                      AND V_ACUMULADOS.MES                  = '||UN_MES||MI_RETROACTIVO||'
                      AND ID_DE_CONCEPTO                    = 103 
                    GROUP BY  V_ACUMULADOS.COMPANIA, 
                              V_ACUMULADOS.ID_DE_PROCESO, 
                              V_ACUMULADOS.ANO, 
                              V_ACUMULADOS.MES, 
                              V_ACUMULADOS.PERIODO, 
                              V_CAJA_COMPENSACION.CAJA_COMPENSACION, 
                              V_CAJA_COMPENSACION.NOMBRE_CAJA, 
                              V_CAJA_COMPENSACION.NIT, 
                              V_CAJA_COMPENSACION.SUCURSAL,
                              V_CAJA_COMPENSACION.CAJA_COMPENSACION';

        MI_CAMPOS:='COMPANIA, 
                    ID_DE_PROCESO, 
                    ANO, 
                    MES, 
                    PERIODO, 
                    TIPO_ADMINISTRADORA, 
                    CODIGO_FONDO, 
                    NOMBRE_FONDO, 
                    NIT, SUCURSAL, 
                    TIPO_CONCEPTO_SENA, 
                    VALOR_APORTES_SENA, 
                    DIAS_MORA_SENA, 
                    VALOR_INTERESES_MORA_SENA, 
                    VALOR_NETO_SENA, 
                    TOTAL_AFILIADOS_SENA,
                    CREATED_BY,
                    DATE_CREATED';

       BEGIN 
            BEGIN 
              MI_VAR  :=  PCK_DATOS.FC_ACME(UN_TABLA    => 'NOVEDADES_AUTOLIQUIDACION', 
                                            UN_ACCION   => 'IS', 
                                            UN_CAMPOS   => MI_CAMPOS, 
                                            UN_VALORES  => MI_STRSQL);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;                                
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_REEMPLAZOS(1).CLAVE := 'APORTES';
            MI_REEMPLAZOS(1).VALOR := 'Sena';
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_INSERSELEC,
              UN_REEMPLAZOS =>  MI_REEMPLAZOS
            );
        END;

        MI_SQL:='SELECT COUNT(1)
                 FROM ( SELECT DISTINCT CODIGO_FONDO
                        FROM NOVEDADES_AUTOLIQUIDACION
                        WHERE COMPANIA            = '''||UN_COMPANIA||'''
                          AND ID_DE_PROCESO       = '||UN_PROCESO||'
                          AND ANO                 ='||UN_ANIO||'
                          AND MES                 ='||UN_MES||'
                          AND TIPO_ADMINISTRADORA =';

        EXECUTE IMMEDIATE MI_SQL||'''05'')' INTO MI_VAR;

        MI_CAMPOS:= NULL;

        IF PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA   => UN_COMPANIA, 
                                  UN_NOMBRE     => 'NIT APORTES PARAFISCALES POR SEPARADO', 
                                  UN_MODULO     => PCK_DATOS.FC_MODULONOMINA, 
                                  UN_FECHA_PAR  => SYSDATE) = 'SI' AND MI_VAR = 1 THEN
          --Se reutiliza la variable MI_CAMPOS para almacenar lo que retorna el parÃ¡metro
          MI_CAMPOS := PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA   => UN_COMPANIA, 
                                              UN_NOMBRE     => 'NIT APORTES SENA', 
                                              UN_MODULO     => PCK_DATOS.FC_MODULONOMINA, 
                                              UN_FECHA_PAR  => SYSDATE);
          IF MI_CAMPOS IS NOT NULL THEN
            MI_CONDICION  :=  ' COMPANIA            ='''||UN_COMPANIA||''' 
                            AND ID_DE_PROCESO       = '||UN_PROCESO||' 
                            AND ANO                 = '||UN_ANIO||' 
                            AND MES                 = '||UN_MES||' 
                            AND TIPO_ADMINISTRADORA = ''05''';
            BEGIN 
              BEGIN 
                MI_VAR:=PCK_DATOS.FC_ACME(UN_TABLA      => 'NOVEDADES_AUTOLIQUIDACION', 
                                          UN_ACCION     => 'M', 
                                          UN_CAMPOS     => '  NIT = '''||MI_CAMPOS||''',
                                                              MODIFIED_BY = '''||UN_USUARIO||''', 
                                                              DATE_MODIFIED = SYSDATE',
                                          UN_CONDICION  =>  MI_CONDICION);
              EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_NOMINA;
              END;
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(1).CLAVE := 'TIPO';
                MI_REEMPLAZOS(1).VALOR := '05';
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                  UN_EXC_COD   => SQLCODE,
                                  UN_ERROR_COD => PCK_ERRORES.ERR_ACTNOVEDA,
                                  UN_REEMPLAZOS =>  MI_REEMPLAZOS
                       );
            END;                  
          END IF;
        END IF;


        -----------------------------------------------------------------------------------------------------------------------------------------
        ------Aportes ICBF
        MI_STRSQL:='SELECT  V_ACUMULADOS.COMPANIA,
                            V_ACUMULADOS.ID_DE_PROCESO,
                            V_ACUMULADOS.ANO, 
                            V_ACUMULADOS.MES, 
                            V_ACUMULADOS.PERIODO,
                            ''06'' TIPO_ADMINISTRADORA,
                            V_CAJA_COMPENSACION.CAJA_COMPENSACION CODIGO_FONDO, 
                            V_CAJA_COMPENSACION.NOMBRE_CAJA, 
                            V_CAJA_COMPENSACION.NIT, 
                            V_CAJA_COMPENSACION.SUCURSAL,
                            ''01'' TIPO_CONCEPTO,
                            SUM(CASE WHEN ID_DE_CONCEPTO  = 102 
                                  THEN VALOR 
                                  ELSE 0 
                                END) ICBF, 
                            0 DIAS_MORA,
                            0 INTERESES,
                            SUM(CASE WHEN ID_DE_CONCEPTO  = 102 
                                  THEN VALOR 
                                  ELSE 0 
                                END) VALOR_NETO,
                             PCK_NOMINA_COM6.FC_TOTAL_AFILIADOS('''||UN_COMPANIA||''',V_CAJA_COMPENSACION.CAJA_COMPENSACION, ''CCF'', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')' || ', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')) TOTAL_AFILIADOS,
                              '''||UN_USUARIO||''' CREATED_BY,
                              SYSDATE DATE_CREATED   
                    FROM V_ACUMULADOS 
                      INNER JOIN V_CAJA_COMPENSACION
                        ON V_ACUMULADOS.COMPANIA            = V_CAJA_COMPENSACION.COMPANIA
                        AND V_ACUMULADOS.CAJA_COMPENSACION  = V_CAJA_COMPENSACION.CAJA_COMPENSACION
                    WHERE V_ACUMULADOS.COMPANIA       ='''||UN_COMPANIA||'''
                      AND V_ACUMULADOS.ID_DE_PROCESO  = '||UN_PROCESO||'
                      AND V_ACUMULADOS.ANO            = '||UN_ANIO||'
                      AND V_ACUMULADOS.MES            = '||UN_MES||MI_RETROACTIVO||'
                      AND ID_DE_CONCEPTO              = 102 
                    GROUP BY  V_ACUMULADOS.COMPANIA, 
                              V_ACUMULADOS.ID_DE_PROCESO, 
                              V_ACUMULADOS.ANO, 
                              V_ACUMULADOS.MES, 
                              V_ACUMULADOS.PERIODO, 
                              V_CAJA_COMPENSACION.CAJA_COMPENSACION, 
                              V_CAJA_COMPENSACION.NOMBRE_CAJA, 
                              V_CAJA_COMPENSACION.NIT, 
                              V_CAJA_COMPENSACION.SUCURSAL';

        MI_CAMPOS:='COMPANIA, 
                    ID_DE_PROCESO, 
                    ANO, 
                    MES, 
                    PERIODO, 
                    TIPO_ADMINISTRADORA, 
                    CODIGO_FONDO, 
                    NOMBRE_FONDO, 
                    NIT, 
                    SUCURSAL,
                    TIPO_CONCEPTO_ICBF, 
                    VALOR_APORTES_ICBF, 
                    DIAS_MORA_ICBF, 
                    VALOR_INTERESES_MORA_ICBF, 
                    VALOR_NETO_ICBF, 
                    TOTAL_AFILIADOS_ICBF,
                    CREATED_BY,
                    DATE_CREATED   ';

        BEGIN 
            BEGIN 
              MI_VAR  :=  PCK_DATOS.FC_ACME(UN_TABLA    => 'NOVEDADES_AUTOLIQUIDACION', 
                                            UN_ACCION   => 'IS', 
                                            UN_CAMPOS   => MI_CAMPOS, 
                                            UN_VALORES  => MI_STRSQL);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;                                
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_REEMPLAZOS(1).CLAVE := 'APORTES';
            MI_REEMPLAZOS(1).VALOR := 'ICBF';
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_INSERSELEC,
              UN_REEMPLAZOS =>  MI_REEMPLAZOS
            );
        END;

        EXECUTE IMMEDIATE MI_SQL||'''06'')' INTO MI_VAR;

        MI_CAMPOS := NULL;

        IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                UN_NOMBRE     => 'NIT APORTES PARAFISCALES POR SEPARADO', 
                                UN_MODULO     => PCK_DATOS.FC_MODULONOMINA, 
                                UN_FECHA_PAR  => SYSDATE) = 'SI' AND MI_VAR=1  THEN
          --Se reutiliza la variable MI_CAMPOS para almacenar lo que retorna el parÃ¡metro
          MI_CAMPOS:=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA    => UN_COMPANIA, 
                                            UN_NOMBRE     => 'NIT APORTES ICBF', 
                                            UN_MODULO     => PCK_DATOS.FC_MODULONOMINA,
                                            UN_FECHA_PAR  => SYSDATE);
          IF  MI_CAMPOS IS NOT NULL THEN
            MI_CONDICION  :=' COMPANIA            ='''||UN_COMPANIA||''' 
                          AND ID_DE_PROCESO       = '||UN_PROCESO||' 
                          AND ANO                 ='||UN_ANIO||' 
                          AND MES                 ='||UN_MES||' 
                          AND TIPO_ADMINISTRADORA = ''06''';
            BEGIN 
              BEGIN 
                MI_VAR:=PCK_DATOS.FC_ACME(UN_TABLA      => 'NOVEDADES_AUTOLIQUIDACION', 
                                          UN_ACCION     => 'M', 
                                          UN_CAMPOS     => '  NIT = '''||MI_CAMPOS||''', 
                                                              MODIFIED_BY = '''||UN_USUARIO||''', 
                                                              DATE_MODIFIED = SYSDATE',
                                          UN_CONDICION  =>  MI_CONDICION);
              EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_NOMINA;
              END;
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(1).CLAVE := 'TIPO';
                MI_REEMPLAZOS(1).VALOR := '06';
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                  UN_EXC_COD   => SQLCODE,
                                  UN_ERROR_COD => PCK_ERRORES.ERR_ACTNOVEDA,
                                  UN_REEMPLAZOS =>  MI_REEMPLAZOS
                       );
            END;   
          END IF;
        END IF;

        ------------------------------------------------------------------------------------------------------------------------------------------
        ---Aportes ESAP
        MI_STRSQL:='SELECT  V_ACUMULADOS.COMPANIA,
                            V_ACUMULADOS.ID_DE_PROCESO,
                            V_ACUMULADOS.ANO, 
                            V_ACUMULADOS.MES,
                            V_ACUMULADOS.PERIODO,
                            ''07'' TIPO_ADMINISTRADORA,
                            V_CAJA_COMPENSACION.CAJA_COMPENSACION CODIGO_FONDO, 
                            V_CAJA_COMPENSACION.NOMBRE_CAJA, 
                            V_CAJA_COMPENSACION.NIT,
                            V_CAJA_COMPENSACION.SUCURSAL,
                            SUM(CASE WHEN ID_DE_CONCEPTO=104 THEN VALOR ELSE 0 END) ESAP, 
                            0 DIAS_MORA,
                            0 INTERES,
                            SUM(CASE WHEN ID_DE_CONCEPTO  = 104 
                                  THEN VALOR 
                                  ELSE 0 
                                END) VALOR_NETO_ESAP,
                             PCK_NOMINA_COM6.FC_TOTAL_AFILIADOS('''||UN_COMPANIA||''',V_CAJA_COMPENSACION.CAJA_COMPENSACION, ''CCF'', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')' || ', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')) TOTAL_AFILIADOS,
                            '''||UN_USUARIO||''' CREATED_BY,
                            SYSDATE DATE_CREATED   
                    FROM V_ACUMULADOS 
                      INNER JOIN V_CAJA_COMPENSACION
                          ON V_ACUMULADOS.COMPANIA            = V_CAJA_COMPENSACION.COMPANIA
                          AND V_ACUMULADOS.CAJA_COMPENSACION  = V_CAJA_COMPENSACION.CAJA_COMPENSACION
                    WHERE V_ACUMULADOS.COMPANIA               ='''||UN_COMPANIA||'''
                        AND V_ACUMULADOS.ID_DE_PROCESO        = '||UN_PROCESO||'
                        AND V_ACUMULADOS.ANO                  = '||UN_ANIO||'
                        AND V_ACUMULADOS.MES                  = '||UN_MES||MI_RETROACTIVO||'
                        AND ID_DE_CONCEPTO                    = 104 
                    GROUP BY  V_ACUMULADOS.COMPANIA, 
                              V_ACUMULADOS.ID_DE_PROCESO, 
                              V_ACUMULADOS.ANO, 
                              V_ACUMULADOS.MES, 
                              V_ACUMULADOS.PERIODO, 
                              V_CAJA_COMPENSACION.CAJA_COMPENSACION, 
                              V_CAJA_COMPENSACION.NOMBRE_CAJA, 
                              V_CAJA_COMPENSACION.NIT, 
                              V_CAJA_COMPENSACION.SUCURSAL';

        MI_CAMPOS:='COMPANIA, 
                    ID_DE_PROCESO, 
                    ANO, 
                    MES, 
                    PERIODO, 
                    TIPO_ADMINISTRADORA, 
                    CODIGO_FONDO, 
                    NOMBRE_FONDO, 
                    NIT, 
                    SUCURSAL, 
                    VALOR_APORTES_ESAP, 
                    DIAS_MORA_ESAP, 
                    VALOR_INTERESES_MORA_ESAP, 
                    VALOR_NETO_ESAP, 
                    TOTAL_AFILIADOS_ESAP,
                    CREATED_BY,
                    DATE_CREATED';

        BEGIN 
            BEGIN 
              MI_VAR  :=  PCK_DATOS.FC_ACME(UN_TABLA    => 'NOVEDADES_AUTOLIQUIDACION', 
                                            UN_ACCION   => 'IS', 
                                            UN_CAMPOS   => MI_CAMPOS, 
                                            UN_VALORES  => MI_STRSQL);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;                                
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_REEMPLAZOS(1).CLAVE := 'APORTES';
            MI_REEMPLAZOS(1).VALOR := 'ESAP';
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_INSERSELEC,
              UN_REEMPLAZOS =>  MI_REEMPLAZOS
            );
        END;

        EXECUTE IMMEDIATE MI_SQL||'''07'')' INTO MI_VAR;

        MI_CAMPOS:=NULL;

        IF PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA   => UN_COMPANIA, 
                                  UN_NOMBRE     => 'NIT APORTES PARAFISCALES POR SEPARADO', 
                                  UN_MODULO     => PCK_DATOS.FC_MODULONOMINA, 
                                  UN_FECHA_PAR  => SYSDATE) = 'SI' AND MI_VAR=1  THEN
          --Se reutiliza la variable MI_CAMPOS para almacenar lo que retorna el parÃ¡metro
          MI_CAMPOS:=PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA   => UN_COMPANIA, 
                                            UN_NOMBRE     => 'NIT APORTES ESAP', 
                                            UN_MODULO     => PCK_DATOS.FC_MODULONOMINA, 
                                            UN_FECHA_PAR  => SYSDATE);
          IF MI_CAMPOS IS NOT NULL THEN
            MI_CONDICION:=' COMPANIA            ='''||UN_COMPANIA||''' 
                        AND ID_DE_PROCESO       = '||UN_PROCESO||' 
                        AND ANO                 = '||UN_ANIO||' 
                        AND MES                 = '||UN_MES||' 
                        AND TIPO_ADMINISTRADORA = ''07''';
            BEGIN 
              BEGIN 
                MI_VAR:=PCK_DATOS.FC_ACME(UN_TABLA      => 'NOVEDADES_AUTOLIQUIDACION', 
                                          UN_ACCION     => 'M', 
                                          UN_CAMPOS     => '  NIT = '''||MI_CAMPOS||''',
                                                              MODIFIED_BY = '''||UN_USUARIO||''',
                                                              DATE_MODIFIED = SYSDATE',
                                          UN_CONDICION  =>  MI_CONDICION);
              EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_NOMINA;
              END;
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(1).CLAVE := 'TIPO';
                MI_REEMPLAZOS(1).VALOR := '07';
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                  UN_EXC_COD   => SQLCODE,
                                  UN_ERROR_COD => PCK_ERRORES.ERR_ACTNOVEDA,
                                  UN_REEMPLAZOS =>  MI_REEMPLAZOS
                       );
            END; 
          END IF;
        END IF;

        -----------------------------------------------------------------------------------------------------------
        ----Aportes Institutos
        MI_STRSQL:='SELECT  V_ACUMULADOS.COMPANIA,
                            V_ACUMULADOS.ID_DE_PROCESO,
                            V_ACUMULADOS.ANO, 
                            V_ACUMULADOS.MES,
                            V_ACUMULADOS.PERIODO,
                            ''08'' TIPO_ADMINISTRADORA,
                            V_CAJA_COMPENSACION.CAJA_COMPENSACION AS CODIGO_FONDO,
                            V_CAJA_COMPENSACION.NOMBRE_CAJA, 
                            V_CAJA_COMPENSACION.NIT, 
                            V_CAJA_COMPENSACION.SUCURSAL,
                            SUM(CASE WHEN ID_DE_CONCEPTO  = 105 
                                  THEN VALOR 
                                  ELSE 0 
                                END) INSTITUTOS, 
                            0 DIAS,
                            0 INTERESES,
                            SUM(CASE WHEN ID_DE_CONCEPTO  = 105 
                                  THEN VALOR 
                                  ELSE 0 
                                END) VALOR_NETO,
                             PCK_NOMINA_COM6.FC_TOTAL_AFILIADOS('''||UN_COMPANIA||''',V_CAJA_COMPENSACION.CAJA_COMPENSACION, ''CCF'', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')' || ', TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY')||  ''',''DD/MM/YYYY'')) TOTAL_AFILIADOS,
                              '''||UN_USUARIO||''' CREATED_BY,
                              SYSDATE DATE_CREATED   
                    FROM V_ACUMULADOS 
                      INNER JOIN V_CAJA_COMPENSACION
                        ON V_ACUMULADOS.COMPANIA            = V_CAJA_COMPENSACION.COMPANIA
                        AND V_ACUMULADOS.CAJA_COMPENSACION  = V_CAJA_COMPENSACION.CAJA_COMPENSACION
                    WHERE V_ACUMULADOS.COMPANIA             ='''||UN_COMPANIA||'''
                      AND V_ACUMULADOS.ID_DE_PROCESO        = '||UN_PROCESO||'
                      AND V_ACUMULADOS.ANO                  = '||UN_ANIO||'
                      AND V_ACUMULADOS.MES                  = '||UN_MES||MI_RETROACTIVO||'
                      AND ID_DE_CONCEPTO                    = 105 
                    GROUP BY  V_ACUMULADOS.COMPANIA, 
                              V_ACUMULADOS.ID_DE_PROCESO, 
                              V_ACUMULADOS.ANO, 
                              V_ACUMULADOS.MES, 
                              V_ACUMULADOS.PERIODO, 
                              V_CAJA_COMPENSACION.CAJA_COMPENSACION, 
                              V_CAJA_COMPENSACION.NOMBRE_CAJA, 
                              V_CAJA_COMPENSACION.NIT, 
                              V_CAJA_COMPENSACION.SUCURSAL';

        MI_CAMPOS:='COMPANIA, 
                    ID_DE_PROCESO, 
                    ANO, 
                    MES, 
                    PERIODO, 
                    TIPO_ADMINISTRADORA, 
                    CODIGO_FONDO, 
                    NOMBRE_FONDO, 
                    NIT, 
                    SUCURSAL, 
                    VALOR_APORTES_INSTITUTOS, 
                    DIAS_MORA_INSTITUTOS, 
                    VALOR_INTERES_MORA_INSTITUTOS, 
                    VALOR_NETO_INSTITUTOS, 
                    TOTAL_AFILIADOS_INSTITUTOS,
                    CREATED_BY,
                    DATE_CREATED  ';
        BEGIN 
            BEGIN 
              MI_VAR  :=  PCK_DATOS.FC_ACME(UN_TABLA    => 'NOVEDADES_AUTOLIQUIDACION', 
                                            UN_ACCION   => 'IS', 
                                            UN_CAMPOS   => MI_CAMPOS, 
                                            UN_VALORES  => MI_STRSQL);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;                                
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_REEMPLAZOS(1).CLAVE := 'APORTES';
            MI_REEMPLAZOS(1).VALOR := 'Institutos';
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_INSERSELEC,
              UN_REEMPLAZOS =>  MI_REEMPLAZOS
            );
        END;
        EXECUTE IMMEDIATE MI_SQL||'''08'')' INTO MI_VAR;

        MI_CAMPOS:=NULL;

        IF PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA   => UN_COMPANIA,
                                  UN_NOMBRE     => 'NIT APORTES PARAFISCALES POR SEPARADO', 
                                  UN_MODULO     => PCK_DATOS.FC_MODULONOMINA, 
                                  UN_FECHA_PAR  => SYSDATE) = 'SI' AND MI_VAR = 1 THEN
          --Se reutiliza la variable MI_CAMPOS para almacenar lo que retorna el parÃ¡metro
          MI_CAMPOS:=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA    => UN_COMPANIA, 
                                            UN_NOMBRE     => 'NIT APORTES INSTITUTOS TECNICOS', 
                                            UN_MODULO     => PCK_DATOS.FC_MODULONOMINA,
                                            UN_FECHA_PAR  => SYSDATE);
          IF MI_CAMPOS IS NOT NULL THEN
            MI_CONDICION:=' COMPANIA            ='''||UN_COMPANIA||''' 
                        AND ID_DE_PROCESO       = '||UN_PROCESO||' 
                        AND ANO                 = '||UN_ANIO||' 
                        AND MES                 = '||UN_MES||' 
                        AND TIPO_ADMINISTRADORA = ''08''';
            BEGIN 
              BEGIN 
                MI_VAR:=PCK_DATOS.FC_ACME(UN_TABLA      => 'NOVEDADES_AUTOLIQUIDACION', 
                                          UN_ACCION     => 'M', 
                                          UN_CAMPOS     => '  NIT           = '''||MI_CAMPOS||''',
                                                              MODIFIED_BY   = '''||UN_USUARIO||''', 
                                                              DATE_MODIFIED = SYSDATE',
                                          UN_CONDICION  =>  MI_CONDICION);
              EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_NOMINA;
              END;
            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(1).CLAVE := 'TIPO';
                MI_REEMPLAZOS(1).VALOR := '08';
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                  UN_EXC_COD    => SQLCODE,
                                  UN_ERROR_COD  => PCK_ERRORES.ERR_ACTNOVEDA,
                                  UN_REEMPLAZOS =>  MI_REEMPLAZOS
                       );
            END; 
          END IF;
        END IF;
      END IF;

    RETURN CASE WHEN MI_VAR<>0 THEN -1 ELSE MI_VAR END;
END FC_CERRARNOMINA;

--24
FUNCTION FC_TOTAL_AFILIADOS
 /*
    NAME              : FC_TOTAL_AFILIADOS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO PACANCHIQUE
    DATE MIGRADOR     : 13/01/2015
    TIME              : 5.31 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : RETORNA EL TOTAL DE AFILIADOS DE UN FONDO DETERMINADO
  */

(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CODFONDO       IN VARCHAR2,
  UN_TIPOFONDO      IN CAMBIOSDEFONDO.TIPOFONDO%TYPE,
  UN_FECHAINI       IN DATE,
  UN_FECHAFIN       IN DATE
)RETURN NUMBER
AS
  MI_RETORNO        NUMBER:=0;
  MI_STRSQL         VARCHAR2(32000);
  MI_SSQL           VARCHAR2(32000);
BEGIN
  MI_STRSQL:='SELECT COUNT(1)
              FROM 
              (SELECT DISTINCT NUMERO_DCTO
               FROM PERSONAL
               WHERE COMPANIA = '''||UN_COMPANIA||'''   
                  AND ID_DE_EMPLEADO NOT IN(0)';
  MI_SSQL:=' AND ((FECHA_DE_INGRESO  <=TO_DATE('''||TO_CHAR(UN_FECHAFIN,'DD/MM/YYYY')||''',''DD/MM/YYYY'') AND ESTADO_ACTUAL     <> 3)
                    OR (FECHA_DE_RETIRO   >=TO_DATE('''||TO_CHAR(UN_FECHAINI,'DD/MM/YYYY')||''',''DD/MM/YYYY'') 
                  AND FECHA_DE_INGRESO  <=TO_DATE('''||TO_CHAR(UN_FECHAFIN,'DD/MM/YYYY')||''',''DD/MM/YYYY'') 
                  AND ESTADO_ACTUAL      = 3)))';

  IF(UN_TIPOFONDO = 'AFP') THEN
    MI_STRSQL:=MI_STRSQL||' AND ID_DEL_FONDO='''||UN_CODFONDO||''''||MI_SSQL;
  ELSIF (UN_TIPOFONDO = 'EPS') THEN
    MI_STRSQL:=MI_STRSQL||' AND FONDO_SALUD='''||UN_CODFONDO||''''||MI_SSQL;
  ELSIF (UN_TIPOFONDO = 'ARL') THEN
    MI_STRSQL:=MI_STRSQL||' AND FONDO_RIESGOS='''||UN_CODFONDO||''''||MI_SSQL;
  ELSIF (UN_TIPOFONDO = 'CCF') THEN
    MI_STRSQL:=MI_STRSQL||' AND CAJA_COMPENSACION='''||UN_CODFONDO||''''||MI_SSQL;
  ELSE
    MI_STRSQL:=NULL;
  END IF;

  IF(MI_STRSQL IS NOT NULL) THEN
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_RETORNO;
  END IF;

  RETURN MI_RETORNO;
END FC_TOTAL_AFILIADOS;

--27
PROCEDURE PR_PERIODOACUMULABLE
 /*
    NAME              : FC_TRAERDATOSENTIDADESEMPLEADO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO PACANCHIQUE
    DATE MIGRADOR     : 06/04/2016
    TIME              : 8.32 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : ESTA FUNCIÃ“N ACTIVA O DESACTIVA EL INDICADOR ACUMULADO DE UN PERIODO a PARTIR DE UN PARÃ¿METRO
  */

(
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO    IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIO       IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES        IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO    IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_PARAMETRO  IN PCK_SUBTIPOS.TI_LOGICO
)
AS
  MI_CONDICION          VARCHAR2(32000);
  MI_AUX                NUMBER;
BEGIN
  MI_CONDICION:='COMPANIA      = '''||UN_COMPANIA||''' 
             AND ID_DE_PROCESO = '||UN_PROCESO||' 
             AND ANO           = '||UN_ANIO||' 
             AND MES           = '||UN_MES||' 
             AND PERIODO       = '||UN_PERIODO;
  MI_AUX:=PCK_DATOS.FC_ACME(UN_TABLA     => 'PERIODOS', 
                            UN_ACCION    => 'M', 
                            UN_CAMPOS    => 'ACUMULADO= '||CASE WHEN UN_PARAMETRO=0 THEN 0 ELSE -1 END, 
                            UN_CONDICION => MI_CONDICION);  
END PR_PERIODOACUMULABLE;

-- 30
FUNCTION FC_CALCULARPRENOMINA 
/*
    NAME              : CALCULARPRENOMINA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
    DATE MIGRADOR     : 21/03/2017
    TIME              : 3:40 PM
    SOURCE MODULE     : 
    MODIFIER          : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED     : 10/10/2017
    TIME              : 15:10
    DESCRIPTION       : UNIFICA EL PROCEDIMIENTO PR_ACTUALIZARTRASLADOS 
                        Y  LA FUNCIÓN FC_VALOR_CONCEPTO_NOVEDAD PARA REALIZAR
                        EL CÁLCULO DE PRENOMINA
                        //ADICION DE LOGICA DEL CONTROLADOR CalculoPrenominaControlador
    @Name: calcularPrenomina
  */
(
    UN_COMPANIA      IN   PCK_SUBTIPOS.TI_COMPANIA,
    UN_PERIODO       IN   PCK_SUBTIPOS.TI_ENTERO,
    UN_PROCESO       IN   PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIO          IN   PCK_SUBTIPOS.TI_ANIO,
    UN_MES           IN   PCK_SUBTIPOS.TI_MES,
    UN_OPCION        IN   VARCHAR2,
    UN_INICIAL       IN   VARCHAR2,
    UN_FINAL         IN   VARCHAR2,
    UN_RUTINA        IN   VARCHAR2,
    UN_USUARIO       IN   PCK_SUBTIPOS.TI_USUARIO
) RETURN VARCHAR2
AS 
    MI_VALORCONCEPTO  PCK_SUBTIPOS.TI_ENTERO;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_PARAMETRO      VARCHAR2(30 CHAR);
    MI_ANIOPREPARAR   VARCHAR2(30 CHAR);
    MI_RTA            VARCHAR2(32000 CHAR) DEFAULT ' ';
BEGIN
    BEGIN
        IF UN_PERIODO <= 3 THEN
            PCK_NOMINA.PR_ACTUALIZARTRASLADOS(UN_COMPANIA, 
                                              UN_PROCESO, 
                                              UN_ANIO, 
                                              UN_MES, 
                                              UN_PERIODO);
        END IF;
        MI_VALORCONCEPTO := PCK_NOMINA_COM2.FC_VALOR_CONCEPTO_NOVEDAD(UN_COMPANIA,
                                                  UN_ANIO,
                                                  UN_PERIODO,
                                                  UN_MES,
                                                  306);


        IF UN_MES = '1' AND MI_VALORCONCEPTO = 0 THEN
            MI_CAMPOS    := 'VALOR          = 13
                             ,DATE_MODIFIED = SYSDATE
                             ,MODIFIED_BY   = '''||UN_USUARIO||''' ';

            MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                             AND NOMBRE = '''||'HASTA QUE MES REALIZAR RETROACTIVO'||''' 
                             ';
            BEGIN
                BEGIN
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PARAMETRO',
                                                           UN_ACCION    =>  'M',
                                                           UN_CAMPOS    =>  MI_CAMPOS,
                                                           UN_CONDICION =>  MI_CONDICION
                                                           ); 
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_NOMINA;  
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
                    MI_MSGERROR (1).CLAVE := 'PERIODOSESION';
                    MI_MSGERROR (1).VALOR := UN_PERIODO;
                    MI_MSGERROR (2).CLAVE := 'PROCESOSESION';
                    MI_MSGERROR (2).VALOR := UN_PROCESO;
                    MI_MSGERROR (2).CLAVE := 'ANIOSESION';
                    MI_MSGERROR (2).VALOR := UN_ANIO;
                    MI_MSGERROR (2).CLAVE := 'MESSESION';
                    MI_MSGERROR (2).VALOR := UN_MES;
                    PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                                UN_ERROR_COD  => PCK_ERRORES.ERRR_NOMINA_ACTUALIZAPARAMETRO,
                                                UN_TABLAERROR => 'PARAMETRO',
                                                UN_REEMPLAZOS => MI_MSGERROR);  
            END;
        END IF;

        MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                              ,UN_NOMBRE    => 'PREPARACION DE  DIFERIDOS Y EMBARGOS AUTOMATICO'
                                              ,UN_MODULO    => 6
                                              ,UN_FECHA_PAR => SYSDATE);

        IF MI_PARAMETRO = 'SI' THEN

              IF UN_OPCION = '1' AND UN_PERIODO <> '04' AND UN_PERIODO < '08'     THEN

                  IF UN_MES = '01' THEN 

                        MI_CAMPOS    := 'VALOR = 13   
                                        ,DATE_MODIFIED = SYSDATE
                                        ,MODIFIED_BY   = '''||UN_USUARIO||''' ';
                        MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                                         AND NOMBRE = '''||'HASTA QUE MES REALIZAR RETROACTIVO'||''' 
                                         ';
                        BEGIN
                            BEGIN
                                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PARAMETRO',
                                                                       UN_ACCION    =>  'M',
                                                                       UN_CAMPOS    =>  MI_CAMPOS,
                                                                       UN_CONDICION =>  MI_CONDICION
                                                                       ); 
                                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_NOMINA;  
                            END;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
                                MI_MSGERROR (1).CLAVE := 'PERIODOSESION';
                                MI_MSGERROR (1).VALOR := UN_PERIODO;
                                MI_MSGERROR (2).CLAVE := 'PROCESOSESION';
                                MI_MSGERROR (2).VALOR := UN_PROCESO;
                                MI_MSGERROR (2).CLAVE := 'ANIOSESION';
                                MI_MSGERROR (2).VALOR := UN_ANIO;
                                MI_MSGERROR (2).CLAVE := 'MESSESION';
                                MI_MSGERROR (2).VALOR := UN_MES;
                                PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_NOMINA_ACTUALIZAPARAMETRO,
                                                            UN_TABLAERROR => 'PARAMETRO',
                                                            UN_REEMPLAZOS => MI_MSGERROR);  
                        END;

                  END IF;


                  IF UN_MES = '01' THEN
                    MI_ANIOPREPARAR := UN_ANIO - 1;
                  ELSE
                    MI_ANIOPREPARAR := UN_ANIO;
                  END IF;

                PCK_NOMINA_COM2.PR_PREPARARFINANCIABLES(UN_COMPANIA       => UN_COMPANIA
                                                        ,UN_ANO1          =>MI_ANIOPREPARAR
                                                        ,UN_MES1          => UN_MES
                                                        ,UN_PERIODO1      => UN_PERIODO
                                                        ,UN_ANO2          => UN_ANIO
                                                        ,UN_MES2          => UN_MES
                                                        ,UN_PERIODO2      =>UN_PERIODO
                                                        ,UN_USUARIO       =>UN_USUARIO
                                                        ,UN_TIPO_EMPLEADO => '02'
                                                        ,UN_IGUAL_O_DIFERENTE => '<>');


                  IF UN_PERIODO = '03' OR  UN_PERIODO = '044' THEN

                      PCK_NOMINA_COM2.PR_PREPARAREMBARGOS(UN_COMPANIA  => UN_COMPANIA
                                                          ,UN_ANO1     =>MI_ANIOPREPARAR
                                                          ,UN_MES1     =>UN_MES
                                                          ,UN_PERIODO1 =>UN_PERIODO
                                                          ,UN_ANO2     =>UN_ANIO
                                                          ,UN_MES2     =>UN_MES
                                                          ,UN_PERIODO2 =>UN_PERIODO
                                                          ,UN_USUARIO  =>UN_USUARIO);
                  END IF;

              END IF;

        END IF;

        MI_RTA := PCK_NOMINA_COM7.FC_LIQUIDAR(UN_COMPANIA => UN_COMPANIA
                                                       ,UN_PROCESO => UN_PROCESO
                                                       ,UN_INICIAL => UN_INICIAL
                                                       ,UN_FINAL   => UN_FINAL
                                                       ,UN_RUTINA  => UN_RUTINA
                                                       ,UN_ANO     =>UN_ANIO
                                                       ,UN_MES     =>UN_MES
                                                       ,UN_PERIODO => UN_PERIODO
                                                       ,UN_USUARIO =>UN_USUARIO);


    END;
    RETURN MI_RTA;
END FC_CALCULARPRENOMINA;

PROCEDURE PR_PASARCONFDIAN(
/*
NAME              : PR_PASARCONFDIAN
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
DATE MIGRADOR     : 15/02/2019
TIME              : 11:57 PM
SOURCE MODULE     :
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       : Procedimiento para pasar la configuración de un año a otro
@Name: pasarConfiguracionDian
*/
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANOACTUAL  IN PCK_SUBTIPOS.TI_ANIO,
    UN_ANOCONF    IN PCK_SUBTIPOS.TI_ANIO,
    UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
)

AS
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;

BEGIN
    MI_CONDICION := ' COMPANIA = '''|| UN_COMPANIA || '''
                 AND  ANO =  '|| UN_ANOACTUAL ||' ';
    BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME
            (UN_TABLA => 'CONCEPTOS_ANO',
             UN_ACCION => 'E',
             UN_CONDICION => MI_CONDICION );
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;


    MI_CAMPOS := 'COMPANIA, ID_DE_CONCEPTO, ANO, CODDIAN, CREATED_BY, DATE_CREATED';
    MI_VALORES := ' SELECT COMPANIA, ID_DE_CONCEPTO, '|| UN_ANOACTUAL || ', CODDIAN, '''|| UN_USUARIO || ''', SYSDATE
                    FROM   CONCEPTOS_ANO
                    WHERE  COMPANIA = '''|| UN_COMPANIA || '''
                      AND  ANO =  '|| UN_ANOCONF ||' ';

    BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME
            (UN_TABLA       =>'CONCEPTOS_ANO',
             UN_ACCION      =>'IS',
             UN_CAMPOS  =>MI_CAMPOS,
             UN_VALORES =>MI_VALORES);

    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;
EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
    PCK_ERR_MSG.RAISE_WITH_MSG
        (UN_EXC_COD     => SQLCODE
        ,UN_ERROR_COD   => PCK_ERRORES.ERR_PASACONCEPTOSDIAN);
END PR_PASARCONFDIAN;

PROCEDURE PR_DIFERENCIASPLANOCALCULO(
/*
NAME              : PR_DIFERENCIASPLANOCALCULO
AUTHORS           : SYSMAN SAS
AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
DATE MIGRADOR     : 06/03/2019
TIME              : 14:44 PM
    SOURCE MODULE     : Procedimiento para generar como alertas las diferencias entre la tabla BASESNOVEDADES y el cálculo de conceptos.
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       :
MODIFICATIONS     :
*/
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_SALUDP           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_SALUDE           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PENSIONP         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PENSIONE         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_FSP              PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_FSPSUBSISTENCIA  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_FSPADICIONAL     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_MSG              PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CONT             NUMBER;
BEGIN
    IF PCK_PARST.FC_PAR('CALCULA 112 POR NOVEDADES', ' ') = 'SI' THEN
        --(MZANGUNA:29/01/2019)-Si existen diferencias tome el valor cálculado de la tabla BASESNOVEDADES
        BEGIN
            SELECT NVL(SUM(APORTEPATRONALPENSION),0), NVL(SUM(APORTEEMPLEADOPENSION),0)
                  ,NVL(SUM(FSP) ,0), NVL(SUM(FSPSUBSISTENCIA) ,0), NVL(SUM(FSPADICIONAL) ,0)
                  ,NVL(SUM(APORTEPATRONALSALUD),0) , NVL(SUM(APORTEEMPLEADOSALUD),0)
                  ,COUNT(1)
            INTO  MI_PENSIONP, MI_PENSIONE,
                  MI_FSP, MI_FSPSUBSISTENCIA, MI_FSPADICIONAL,
                  MI_SALUDP, MI_SALUDE,
                  MI_CONT
            FROM BASESNOVEDADES
            WHERE COMPANIA = UN_COMPANIA
              AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
              AND ANO = PCK_NOMINA.GL_SANO
              AND MES = PCK_NOMINA.GL_SMES
              AND PERIODO = PCK_NOMINA.GL_SPER;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_PENSIONP := 0;
            MI_PENSIONE := 0;
            MI_FSP := 0;
            MI_FSPSUBSISTENCIA := 0;
            MI_FSPADICIONAL := 0;
            MI_SALUDP := 0;
            MI_SALUDE := 0;
        END;

        MI_MSG(1).CLAVE := 'NOMBRES';
        MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRECOMPLETO;
		--MOD JM CC 2994 y 3550 (REDONDEOS) 
		IF (MI_PENSIONP + CASE WHEN  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'REDONDEAR SEGURIDAD SOCIAL',6,SYSDATE),'SI') = 'SI' THEN (MI_PENSIONE) ELSE ROUND(MI_PENSIONE,-2) END) <> (PCK_NOMINA.FC_CN(117) + PCK_NOMINA.FC_CN(118) + PCK_NOMINA.FC_CNA(117) + PCK_NOMINA.FC_CNA(118)) AND PCK_NOMINA.GL_SPER NOT IN (1,6,5,8,9,4)   THEN --24/10/2024 7800941 LVEGA -- jm 7721450 15/12 -- TICKET 7742876 EFCM: SE ELIMINANA ALERTAS EN RETROACTIVO
          IF(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'REDONDEAR SEGURIDAD SOCIAL',6,SYSDATE),'SI') = 'SI' AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'NOMINA MENSUAL',6,SYSDATE),'SI') = 'SI' AND ((MI_PENSIONP + CASE WHEN  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'REDONDEAR SEGURIDAD SOCIAL',6,SYSDATE),'SI') = 'SI' THEN (MI_PENSIONE) ELSE ROUND(MI_PENSIONE,-2) END) - (PCK_NOMINA.FC_CN(117) + PCK_NOMINA.FC_CN(118) + PCK_NOMINA.FC_CNA(117) + PCK_NOMINA.FC_CNA(118))) > 100) THEN -- MPEREZ CC4140
            --El empleado --NOMBRES-- Presenta diferencias en pensión.
            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ERR_PLANONOMPENSION
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                ,UN_ESERROR      => -1);
          END IF;  -- MPEREZ CC4140
        -- TICKET 7740193 EFCM: CUADRE DE PENSION PARA PERIODOS CON MAS DE UNA NOVEDAD
        /*ELSIF (MI_CONT > 1) THEN
            PCK_NOMINA.CN(117) := MI_PENSIONP;
            PCK_NOMINA.CN(131) := MI_PENSIONE;*/
        END IF;
        -- TICKET 7740193 FIN --
        
        -- TICKET 7745393 EFCM: SE EXLUYE VALIDACION PARA PRIMERA QUINCENA
        IF (MI_FSP + MI_FSPSUBSISTENCIA) <> (PCK_NOMINA.FC_CN(115) + PCK_NOMINA.FC_CNA(115)) AND PCK_NOMINA.GL_SPER NOT IN (1,6,5,8,9,4) THEN --24/10/2024 7800941 LVEGA--jm 7721450 13/02 -- TICKET 7742876 EFCM: SE ELIMINANA ALERTAS EN RETROACTIVO
            --El empleado --NOMBRES-- Presenta diferencias en Fondo solidario
            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ERR_PLANONOMFSP
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                ,UN_ESERROR      => -1);
        END IF;

        IF MI_FSPADICIONAL <> (PCK_NOMINA.FC_CN(120) + PCK_NOMINA.FC_CNA(120)) AND PCK_NOMINA.GL_SPER <> 6 THEN  --jm 7721450 13/02
            --El empleado --NOMBRES-- Presenta diferencias en Fondo solidario adicional
            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ERR_PLANONOMFSPADC
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                ,UN_ESERROR      => -1);
        END IF;
        --MOD JM CC 2994 (REDONDEOS)
		    IF (MI_SALUDP + CASE WHEN  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'REDONDEAR SEGURIDAD SOCIAL',6,SYSDATE),'SI') = 'SI' THEN (MI_SALUDE) ELSE ROUND(MI_SALUDE + 49,-2) END) <> (PCK_NOMINA.FC_CN(113) + PCK_NOMINA.FC_CN(116) + PCK_NOMINA.FC_CNA(113) + PCK_NOMINA.FC_CNA(116)) AND PCK_NOMINA.GL_SPER NOT IN (1,6,5,8,9,4)  THEN --24/10/2024 7800941 LVEGA--jm 7721450 15/12 -- TICKET 7742876 EFCM: SE ELIMINANA ALERTAS EN RETROACTIVO
          IF(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'REDONDEAR SEGURIDAD SOCIAL',6,SYSDATE),'SI') = 'SI' AND NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'NOMINA MENSUAL',6,SYSDATE),'SI') = 'SI' AND (MI_SALUDP + CASE WHEN  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'REDONDEAR SEGURIDAD SOCIAL',6,SYSDATE),'SI') = 'SI' THEN (MI_SALUDE) ELSE ROUND(MI_SALUDE + 49,-2) END) - (PCK_NOMINA.FC_CN(113) + PCK_NOMINA.FC_CN(116) + PCK_NOMINA.FC_CNA(113) + PCK_NOMINA.FC_CNA(116)) > 100) THEN -- MPEREZ CC4140
            --El empleado --NOMBRES-- Presenta diferencias en salud
            PCK_NOMINA_COM7.PR_ALERTA
                (UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ERR_PLANONOMSALUD
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                ,UN_ESERROR      => -1);
          END IF; -- MPEREZ CC4140
        -- TICKET 7740193 EFCM: CUADRE DE SALUD PARA PERIODOS CON MAS DE UNA NOVEDAD
        /*  ELSIF (MI_CONT > 1) THEN
            PCK_NOMINA.CN(116) := MI_SALUDP;
            PCK_NOMINA.CN(130) := MI_SALUDE;*/
        END IF;
        -- TICKET 7740193 FIN --
    END IF;
END PR_DIFERENCIASPLANOCALCULO;

FUNCTION FC_CANTIDADEMPLEADOSMES (
/*
    NAME              : FC_CANTIDADEMPLEADOSMES  --> EN ACCESS ACUMCONCEPTO  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DIANA MARCELA CASTIBLANCO SANCHEZ
    DATE MIGRADOR     : 24/03/2021
    TIME              : 10:36 AM
    SOURCE MODULE     : NOMINAP2020.12.01_GNAR 2388 22122020 FINAL 88 - FINAL PMAG_PGOB_ACT.accdb
    MODIFIER          : 
    DATE MODIFIED     : 04/10/2021
    TIME              :
    DESCRIPTION       : Retorna la cantidad de empleados en el mes,periodo y año.
                        SE AJUSTA AL ESTANDAR.
  */

    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO            IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES             IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO         IN PCK_SUBTIPOS.TI_ENTERO
  )
RETURN NUMBER
  AS
    MI_CONTAR       PCK_SUBTIPOS.TI_ENTERO;
BEGIN

     BEGIN
      SELECT  SUM(T.TOTAL)
        INTO  MI_CONTAR
      FROM (SELECT COUNT(HISTORICOS.ID_DE_CONCEPTO) AS TOTAL, 
            HISTORICOS.ID_DE_CONCEPTO 
            FROM HISTORICOS 
            WHERE HISTORICOS.COMPANIA       = UN_COMPANIA
              AND HISTORICOS.MES = UN_MES
              AND HISTORICOS.ANO = UN_ANIO
              AND HISTORICOS.Periodo = UN_PERIODO
              AND Historicos.ID_DE_CONCEPTO  = '144'   
            GROUP BY HISTORICOS.ID_DE_CONCEPTO
           ) T;
    EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
        MI_CONTAR := 0;
    END;

    RETURN MI_CONTAR;    

END FC_CANTIDADEMPLEADOSMES;

PROCEDURE PR_CAL_PRIMAVAC_SYSMAN(
      /*
      NAME              : PR_CAL_PRIMAVAC_SYSMAN
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÑAS
      DATE MIGRADOR     : 19/01/2022
      TIME              :
      SOURCE MODULE     : NOMINAP2022.01.01 UNIFICADAS MPV 05012021 - 654_ESPBOY_SNMARTIN En access calcularprimadevacacionesSYSMAN
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       :
      @NAME:  calcularprimadevacacionesSYSMAN
      */
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA )
  AS
    MI_BONPAGADA          NUMBER DEFAULT 0;
    MI_MSG                PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_DINEROPROPORCIONAL NUMBER;
  BEGIN
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11' THEN
      PCK_NOMINA.CN(174)                            := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CN(160) / 12), 0) / 2;
    ELSE
      --adicionarle la condicion de no dejar liquidar más de un periodo de vacaciones a la vez
      --DC := 0;
      PCK_NOMINA.GL_DIASVAC           := 0;
      PCK_NOMINA.GL_DIASPENDIENTES    := 0;
      PCK_NOMINA.GL_PENDIENTES        := 0;
      PCK_NOMINA.GL_LICENCIAS         := 0;
      PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
      MI_DINEROPROPORCIONAL           := 0;
      PCK_NOMINA.GL_FECHAUV           :=
      (
        CASE
        WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
          PCK_NOMINA.GL_FECHAI
        ELSE
          PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL
        END);
       -- Para personal que se retira
      IF PCK_NOMINA.FC_CN(404)  <> 0 THEN
        PCK_NOMINA.GL_AC        := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        --REVISAR SI DIASINTERRUPCON YA SE DESCONTO DENTRO DEL ULTIMO PERIODO DE LAS VACACIONES, O SE TIENE QUE DESCONTAR EN FECHAI ANTES DE CALCULAR FECHAUV PARA NIEVOS
        PCK_NOMINA.GL_LICENCIAS := (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.FC_CNA(339)) 
                                   + (
                                      CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                                           PCK_NOMINA.FC_CESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 'L')
                                      ELSE
                                           0
                                      END
                                     ) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
        PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91);                            
        --dtv = diasmescomercial(IIf(IsNull(personal!Fecha_Final), CVDate(FechaI), personal!Fecha_Final), CVDate(FECHAFIN1))
        PCK_NOMINA.GL_DTV            := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
            (
              (
                CASE
                WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                  PCK_NOMINA.GL_FECHAI
                ELSE
                  (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)
                END), PCK_NOMINA.GL_FECHAR );
        PCK_NOMINA.GL_PERIODOS                                                              := TRUNC(PCK_NOMINA.GL_DTV/ 360);
        -- diasvac = Round(15 * periodos, 2)
        PCK_NOMINA.CN(93)          := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) * PCK_NOMINA.GL_PERIODOS, 2);
        --adicionarle la condicion de no dejar liquidar más de un periodo de vacaicones a la vez 
        PCK_NOMINA.GL_AC           := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); -- Acumulado del ultimo año
        IF PCK_NOMINA.GL_DTV        = 0 THEN
          PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1), 0); -- factores prima de vacaciones
        ELSE
          PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) , 0); -- factores prima de vacaciones
        END IF;
        IF PCK_NOMINA.GL_DTV = 0 THEN 
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN --EMPLEADOS OFICIALES
                PCK_NOMINA.CN(155)                            :=
                    (
                      CASE
                      WHEN PCK_NOMINA.FC_CN(155) = 0 THEN
                        PCK_SYSMAN_UTL.FC_ROUND(
                                                  PCK_SYSMAN_UTL.FC_ROUND( 
                                                                            PCK_NOMINA.FC_CN(1) 
                                                                          + PCK_NOMINA.GL_AUXT 
                                                                          + PCK_NOMINA.GL_AUXA  
                                                                          + ( PCK_NOMINA.FC_CNA(160) / 12 ) 
                                                                          + PCK_NOMINA.FC_CN(170)  
                                                                          ,0 ) 
                                                  *   PCK_NOMINA.FC_CN(93) / 30, 0)
                      ELSE
                        PCK_NOMINA.FC_CN(155)
                      END);  -- Prima de vacaciones
              ELSE
                PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA  + ( PCK_NOMINA.FC_CNA(160) / 12 )  + PCK_NOMINA.FC_CN(170)   , 0);  -- factores prima de vacaciones
                -- cn(155) = IIf(cn(155) = 0, Round(Round(cn(1) + AUXT + AUXA + (Cna(160) / 12) + (cn(170)), 0) * cn(93) / 30, 0), cn(155))  ' Prima de vacaciones
            END IF;
        END IF;
        --cn(155) = IIf(cn(155) = 0, Round(Round(cn(1) + AUXT + AUXA + Cna(170) / 12 + Cna(160) / 12, 0) * cn(93) / 30, 0), cn(155))   ' Prima de vacaciones
        IF PCK_NOMINA.FC_CN(175) = 0  THEN
          PCK_NOMINA.CN(175)  := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.GL_DTV / 720, 2);
          -- DiasProporcional = DTV - (periodos * 360)
          -- DiasProporcional = IIf(DiasProporcional < 0, 0, DiasProporcional)
          -- If DiasProporcional > 180 Then
          --    DineroProporcional = Round(FactoresPV * 15 / 30 * DiasProporcional / 360, 0)
          --    PrimaProporcional = Round(FactoresPV * cn(68) / 30 * DiasProporcional / 360, 0)
          -- End If
          -- If DiasPendientes > 0 Then
          --    PENDIENTES = Round(FactoresPV * DiasPendientes / 30, 0)
          -- End If
          IF PCK_NOMINA.GL_SPRC  = 99 THEN -- en proyecciones van dineroproporcional y pendientes por aparte
            PCK_NOMINA.CN(175)  := PCK_NOMINA.FC_CN(175);  -- + DineroProporcional + PENDIENTES
          ELSE
            PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + MI_DINEROPROPORCIONAL + PCK_NOMINA.GL_PENDIENTES;
          END IF;
        END IF;
      ELSE
        --Vacaciones Normales
        --acumulado para dias pensientes de vacaicones
        PCK_NOMINA.GL_AC                := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.GL_DIASPENDIENTES   := PCK_NOMINA.FC_CNA(91);
        IF PCK_NOMINA.GL_DIASPENDIENTES > 0 THEN
          --El empleado --EMPLEADO--, tiene --DIAS-- días pendientes de vacaciones
          MI_MSG(1).CLAVE := 'EMPLEADO';
          MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
          MI_MSG(2).CLAVE := 'DIAS';
          MI_MSG(2).VALOR := PCK_NOMINA.GL_DIASPENDIENTES;
          PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA => PCK_NOMINA.GL_COMPANIA ,UN_MENSAJE_COD => PCK_ERRORES.ALER_DIASPENDIENTESVACACIONES ,UN_REEMPLAZOS => MI_MSG ,UN_PROCESO => PCK_NOMINA.GL_PROCESOACTUAL ,UN_ANO => PCK_NOMINA.GL_ANOACTUAL ,UN_MES => PCK_NOMINA.GL_MESACTUAL ,UN_PERIODO => PCK_NOMINA.GL_PERIODOACTUAL ,UN_USER => PCK_CONEXION.FC_GETUSER );
        END IF;
        PCK_NOMINA.CN(93)          := PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164); -- dias de prima pactados para Prima de Vaca
        PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA,(PCK_NOMINA.GL_SANO - 1), (PCK_NOMINA.GL_SMES + 1 ), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); -- Acumulado del ultimo año
        --anterior ac = AcumCC(CStr(Anoa), strzero(mesa, 2), strzero(pera, 2), CStr(s_Ano), strzero(s_mes, 2), "99", personal!NUMERO_DCTO)
        --FactoresPV = Round(IIf(cn(11) <> 0, cn(10), cn(1)) + cn(61) + PROMEDIOEXTRAS + AUXT + AUXA + cn(197) + cn(198) + cn(199) + cn(527) + (Cna(160) / 12) + (Cna(150) / 12), 0) ' factores prima de vacaciones         'FactoresPV = Round(IIf(cn(11) <> 0, cn(10), cn(1)) + ((Cna(56)) / 12), 0)      ' factores prima de vacaciones
        --FactoresPV = Round(IIf(cn(11) <> 0, cn(10), cn(1)) + ((Cna(56)) / 12), 0)      ' factores prima de vacaciones
        --If personal!ID_de_Tipo = "06" Then ''EMPLEADOS OFICIALES
        --Feb 21/06
        PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1)); -- factores prima de vacaciones
        --Else
        --   FactoresPV = Round(IIf(cn(11) <> 0, cn(10), cn(1)) + Round(AUXT + AUXA + (Cna(160) / 12) + cn(186), 0), 0) ' factores prima de vacaciones
        --   FactoresPV1 = Round(IIf(cn(11) <> 0, cn(10), cn(1)) + Round(AUXT + AUXA, 0), 0)  ' factores prima de vacaciones
        --End If
        IF PCK_NOMINA.GL_SPER       = 2 THEN --diferente de salario integral
          IF PCK_NOMINA.FC_CN(403) <> 0 THEN -- Salario de Vacaciones y prima de vaciones
              PCK_NOMINA.CN(174)   :=
                      (
                        CASE
                        WHEN PCK_NOMINA.FC_CN(174) = 0 THEN
                          PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0)
                        ELSE
                          PCK_NOMINA.FC_CN(174)
                        END); -- Vacaciones en Tiempo
          END IF;
          IF PCK_NOMINA.FC_CN(419) <> 0 THEN -- Vacaciones en dinero
                      PCK_NOMINA.CN(175)     :=
                      (
                        CASE
                        WHEN PCK_NOMINA.FC_CN(175) = 0 THEN
                          PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0)
                        ELSE
                          PCK_NOMINA.FC_CN(175)
                        END); -- Vacaciones en Tiempo
          END IF;
          --    If personal!Sindicato = True Then 'para los sindicalizados
          --   cn(155) = IIf(cn(155) = 0, Round(IIf(cn(11) <> 0, cn(10), (cn(1) / 3 * 2) + PROMEDIOEXTRAS + AUXA + AUXT + cn(197) + cn(198) + cn(199) + cn(527) + (Cna(160) / 12) + (Cna(150) / 12) + (cn(173) / 12)), 0), cn(155))       ' Prima de vacaciones
          -- Else --para los no sindicalizados
          PCK_NOMINA.CN(155) :=
                    (
                      CASE
                      WHEN PCK_NOMINA.FC_CN(155) = 0 THEN
                        PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164), 0)
                      ELSE
                        PCK_NOMINA.FC_CN(155)
                      END); -- Vacaciones en Tiempo
        ELSE -- salario Integral no tiene prima de vacaciones
          IF PCK_NOMINA.FC_CN(403) <> 0 THEN 
              PCK_NOMINA.CN(174)   :=
                      (
                        CASE
                        WHEN PCK_NOMINA.FC_CN(174) = 0 THEN
                          PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0)
                        ELSE
                          PCK_NOMINA.FC_CN(174)
                        END); -- Vacaciones en Tiempo
          END IF;
          IF PCK_NOMINA.FC_CN(419) <> 0 THEN -- Vacaciones en dinero
                      PCK_NOMINA.CN(175)     :=
                      (
                        CASE
                        WHEN PCK_NOMINA.FC_CN(175) = 0 THEN
                          PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0)
                        ELSE
                          PCK_NOMINA.FC_CN(175)
                        END); -- Vacaciones en Tiempo
          END IF;
        END IF;
      END IF;
    END IF;
    --bonificacion especial de recreación
    --If cn(156) = 0 And cn(174) > 0 Then
    -- cn(156) = Round((cn(1) / 30 * 2), 0)
    --End If
    --Guardando Factores
    PCK_NOMINA.CN(960)   := PCK_NOMINA.GL_FACTORESPV; -- Sueldo
    IF PCK_NOMINA.GL_SPRC = 99 THEN
      PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(93); -- Dias de Vacaciones
      PCK_NOMINA.CN(962) := PCK_NOMINA.GL_PERIODOS; -- Periodos de Vacaciones
    ELSE
      PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(94);   -- Dias de Vacaciones
      PCK_NOMINA.CN(962) := PCK_NOMINA.FC_CN(164);  -- Periodos de Vacaciones
    END IF;
    PCK_NOMINA.CN(963) := PCK_NOMINA.GL_LICENCIAS;         -- Licencias
    PCK_NOMINA.CN(964) := PCK_NOMINA.GL_DIASPENDIENTES;    -- Dias Pendientes de Vacaciones
    PCK_NOMINA.CN(965) := PCK_NOMINA.GL_PRIMAPROPORCIONAL; -- Prima Proporcional
    PCK_NOMINA.CN(966) := MI_DINEROPROPORCIONAL;           -- Valor a pagar por vacacion proporcional
    PCK_NOMINA.CN(967) := PCK_NOMINA.GL_PENDIENTES;        -- Valor a pagar por dias pendientes
    PCK_NOMINA.CN(968) := PCK_NOMINA.GL_DIASPROPORCIONAL;  -- Dias proporcionales vacaciones
    --01022018
     PCK_NOMINA.GL_PV_BASE  := PCK_NOMINA.GL_FACTORESPV;
    PCK_NOMINA.GL_PV_DIAS  := PCK_NOMINA.GL_DIASPROP;
    PCK_NOMINA.GL_VAC_DIAS := PCK_NOMINA.GL_DTV;
END PR_CAL_PRIMAVAC_SYSMAN;

FUNCTION FC_DISCOFNA
/*
  NAME              : FC_DISCOFNA
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : JEIMMY CAROLINA ROJAS GUERRERO
  DATE MIGRATION    : 21/04/2023
  TIME              : 
  SOURCE MODULE     : NOMINA(6)
  MODIFIED BY       : 
  MODIFICATIONS     : 
  DESCRIPTION       : Funcion que permite generar archivo plano para el Fondo Nacional Del Ahorro
                      de acuerdo al parametro DISCO CESANTIAS FNA
  PARAMETERS        : 
  @Name             :discoFna
  @Method           :GET
*/
(
    UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO          IN  PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_PERIODO          IN  PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_MES              IN  PCK_SUBTIPOS.TI_MES,
    UN_ANIO             IN  PCK_SUBTIPOS.TI_ANIO,								 
    UN_BANCO            IN  BANCO.BANCO%TYPE,
	UN_MES13            IN  PCK_SUBTIPOS.TI_LOGICO
)
RETURN CLOB AS 
    MI_DISCO            CLOB;
    MI_PARCESANTIASFNA  PARAMETRO.VALOR%TYPE;
BEGIN
    MI_PARCESANTIASFNA := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                   ,UN_NOMBRE    => 'DISCO CESANTIAS FNA'
                                                   ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA()
                                                   ,UN_FECHA_PAR => SYSDATE), 'DISCOFNACBY'); 
    MI_DISCO := '';
                                                  
    IF MI_PARCESANTIASFNA = 'DISCOFNACBY' THEN
        MI_DISCO := PCK_NOMINA_COM4.FC_DISCOFNACBY(UN_COMPANIA => UN_COMPANIA,
                                                   UN_PROCESO  => UN_PROCESO,
                                                   UN_PERIODO  => UN_PERIODO,
                                                   UN_MES      => UN_MES,
                                                   UN_ANIO     => UN_ANIO,								 
                                                   UN_BANCO    => UN_BANCO,
                                                   UN_MES13    => UN_MES13);
    ELSIF MI_PARCESANTIASFNA = 'DISCOFNACRTLMA' THEN
        MI_DISCO := PCK_NOMINA_COM10.FC_DISCOFNACRTLMA(UN_COMPANIA => UN_COMPANIA,
                                                       UN_PROCESO  => UN_PROCESO,
                                                       UN_PERIODO  => UN_PERIODO,
                                                       UN_MES      => UN_MES,
                                                       UN_ANIO     => UN_ANIO,								 
                                                       UN_BANCO    => UN_BANCO,
                                                       UN_MES13    => UN_MES13);
    END IF;
    RETURN MI_DISCO;
END FC_DISCOFNA;

PROCEDURE PR_REVISAR_LEY2277_2022_2
  /*
    NAME 			 : PR_REVISAR_LEY2277_2022_2
    AUTHORS 		 : JEIMMY CAROLINA ROJAS GUERRERO
    DATE             : 01/11/2023
    DESCRIPTION		 : Proceso que permite ajustar los valores de los campos DED_4_25_EXENTOACUMULADO y LIMITE_RENTAEXENTA_1340ANUAL
                       en la tabla RETEFUENTE_CALCULOS 
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO  IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES      IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_TODOS    IN PCK_SUBTIPOS.TI_LOGICO,
    UN_USUARIO  IN  PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_UVTACTUAL            NUMBER;
    MI_CONDI                VARCHAR2(200);
    MI_RSCONSULTA           PCK_SUBTIPOS.TI_STRSQL;
	MI_RSRN                 NUMBER;							   
    MI_RSCOMPANIA           RETEFUENTE_CALCULOS.COMPANIA%TYPE;
    MI_RSPROCESO            RETEFUENTE_CALCULOS.ID_DE_PROCESO%TYPE;
    MI_RSANO                RETEFUENTE_CALCULOS.ANO%TYPE;
    MI_RSMES                RETEFUENTE_CALCULOS.MES%TYPE;
    MI_RSPERIODO            RETEFUENTE_CALCULOS.PERIODO%TYPE;
    MI_RSEMPLEADO           RETEFUENTE_CALCULOS.ID_DE_EMPLEADO%TYPE;
    MI_RSID                 RETEFUENTE_CALCULOS.ID%TYPE;
    MI_RSDED_4_25           RETEFUENTE_CALCULOS.DED_4_25_EXENTO%TYPE;
    MI_RSDEDYRENTAS         RETEFUENTE_CALCULOS.BASE4_SUMA_DEDYRENTAS%TYPE;
    MI_RSDED_4_25_ACUM      RETEFUENTE_CALCULOS.DED_4_25_EXENTOACUMULADO%TYPE;
    MI_RSRENTAEX_1340ANUAL  RETEFUENTE_CALCULOS.LIMITE_RENTAEXENTA_1340ANUAL%TYPE;
    MI_ACUM25               PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_ACUM40               PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_RS                   SYS_REFCURSOR;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_ACUMCN481            PCK_SUBTIPOS.TI_DOBLE := 0; --JM CC 3051
    MI_ACUMCN482            PCK_SUBTIPOS.TI_DOBLE := 0; --JM CC 3051
BEGIN
    MI_UVTACTUAL := TO_NUMBER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                        UN_NOMBRE    => 'VALOR UVT ACTUAL',
                                                        UN_MODULO    => PCK_DATOS.MODULONOMINA, 
                                                        UN_FECHA_PAR => SYSDATE), '0'));
                                                        
    IF UN_TODOS NOT IN(0) THEN
        MI_CONDI := 'AND ID_DE_EMPLEADO = '||UN_EMPLEADO||'';
    ELSE  
        MI_CONDI := '';
    END IF;
    
    <<ACT_CAMPOS_RETEFUENTE_CALC>>
    MI_RSCONSULTA := 'SELECT ROW_NUMBER() OVER (PARTITION BY ID_DE_EMPLEADO ORDER BY ID_DE_EMPLEADO,MES) RN,
              COMPANIA,
                          ID_DE_PROCESO, 
                          ANO, 
                          MES, 
                          PERIODO, 
                          ID_DE_EMPLEADO,
                          ID, 
                          DED_4_25_EXENTO, 
                          BASE4_SUMA_DEDYRENTAS, 
                          DED_4_25_EXENTOACUMULADO, 
                          LIMITE_RENTAEXENTA_1340ANUAL 
                      FROM RETEFUENTE_CALCULOS   
                      WHERE COMPANIA = '''||UN_COMPANIA||'''
                          AND ID_DE_PROCESO = '||UN_PROCESO||'
                          AND ANO = '||UN_ANO||'
                          AND MES < '||UN_MES||'
                           '||MI_CONDI||' 
                          AND ID = 1 
                      ORDER BY ID_DE_EMPLEADO,MES';
                      --AND PERIODO = '||UN_PERIODO||' --MOD JM CC 3051 (debe tomar el periodo 4 tambien)
    OPEN MI_RS FOR MI_RSCONSULTA;
    LOOP
    FETCH MI_RS INTO MI_RSRN,MI_RSCOMPANIA,MI_RSPROCESO,MI_RSANO,MI_RSMES,MI_RSPERIODO,MI_RSEMPLEADO,
                     MI_RSID,MI_RSDED_4_25,MI_RSDEDYRENTAS,MI_RSDED_4_25_ACUM,MI_RSRENTAEX_1340ANUAL;	  
    EXIT WHEN MI_RS%NOTFOUND;	
       
        IF MI_RSRN = 1 THEN 
            MI_ACUM25 := MI_RSDED_4_25;
            MI_ACUM40 := MI_RSDEDYRENTAS;
        ELSE
            MI_ACUM25 := MI_ACUM25 + MI_RSDED_4_25;
            MI_ACUM40 := MI_ACUM40 + MI_RSDEDYRENTAS;
        END IF;

         IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA, UN_NOMBRE => 'SUMAR RETROACTIVO EN ACUMULADOS DE 25% y 40% EXENTOS', UN_MODULO => PCK_DATOS.FC_MODULONOMINA, UN_FECHA_PAR => SYSDATE),'NO') = 'SI' THEN --JM CC 3051
                BEGIN 
                    SELECT SUM(CASE WHEN ID_DE_CONCEPTO = 481 THEN  SUM(NVL(h.VALOR,0)) ELSE 0 END),
                     SUM(CASE WHEN ID_DE_CONCEPTO = 482 THEN  SUM(NVL(h.VALOR,0)) ELSE 0 END)
                    INTO MI_ACUMCN481,MI_ACUMCN482
                    FROM HISTORICOS h
                    WHERE h.COMPANIA = UN_COMPANIA
                    AND h.ID_DE_EMPLEADO = MI_RSEMPLEADO
                    AND h.ANO = UN_ANO
                    AND h.ID_DE_CONCEPTO IN (481,482)
                    AND h.MES = MI_RSMES
                    AND h.periodo in (33,34,37,31)
                    --AND h.id_de_proceso = UN_PROCESO
                    GROUP BY ID_DE_CONCEPTO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_ACUMCN481 := 0;
                    MI_ACUMCN482 := 0;
                END; 
                MI_ACUM25 := MI_ACUM25 + NVL(MI_ACUMCN481,0);
                MI_ACUM40 := MI_ACUM40 + NVL(MI_ACUMCN482,0);
            END IF;--JM FIN CC 3051 
        
        IF (MI_ACUM25 > (PCK_NOMINA_COM9.FC_PARLIMRET(MI_RSCOMPANIA,MI_RSANO,25) * MI_UVTACTUAL)) THEN
            MI_ACUM25 := (PCK_NOMINA_COM9.FC_PARLIMRET(MI_RSCOMPANIA,MI_RSANO,25) * MI_UVTACTUAL);
        END IF;
        
        IF (MI_ACUM40 > (PCK_NOMINA_COM9.FC_PARLIMRET(MI_RSCOMPANIA,MI_RSANO,40) * MI_UVTACTUAL)) THEN
            MI_ACUM40 := (PCK_NOMINA_COM9.FC_PARLIMRET(MI_RSCOMPANIA,MI_RSANO,40) * MI_UVTACTUAL);
        END IF;
        
        BEGIN
            MI_CAMPOS := 'DED_4_25_EXENTOACUMULADO     = '||MI_ACUM25||' ,
                          LIMITE_RENTAEXENTA_1340ANUAL = '||MI_ACUM40||' ,
                          DATE_MODIFIED                = SYSDATE,
                          MODIFIED_BY                  = '''||UN_USUARIO||'''';
    
            MI_CONDICION := 'COMPANIA = '''||MI_RSCOMPANIA||'''
                            AND ID_DE_PROCESO = '||MI_RSPROCESO||'
                            AND ANO = '||MI_RSANO||'
                            AND MES = '||MI_RSMES||'
                            AND PERIODO = '||MI_RSPERIODO||'
                            AND ID_DE_EMPLEADO = '||MI_RSEMPLEADO||'
                            AND ID = 1';
                        
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'RETEFUENTE_CALCULOS'
                                                 ,UN_ACCION    => 'M'
                                                 ,UN_CAMPOS    => MI_CAMPOS
                                                 ,UN_CONDICION => MI_CONDICION);
                                                 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;                                   
    END LOOP ACT_CAMPOS_RETEFUENTE_CALC;
END PR_REVISAR_LEY2277_2022_2;				

--(lvguaba 09/11/2023 7734461) Se crea función que permite subir conceptos a históricos por medio de excel 

FUNCTION FC_SUBIR_CN_HIST 
/*
    NAME              : FC_SUBIR_CN_HIST
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 
    TIME              :
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : 
    
    @NAME: subirCnHistoricos
  */
  (
    UN_COMPANIA   VARCHAR2,
    UN_PROCESO    NUMBER,
    UN_ANIO       NUMBER,
    UN_MES        NUMBER,
    UN_PERIODO    NUMBER,
    UN_IDEMPLEADO NUMBER,
    UN_IDCONCEPTO NUMBER,
    UN_VALOR      IN PCK_SUBTIPOS.TI_DOBLE,
    UN_FECHAC     DATE,
    UN_OBS        VARCHAR2 DEFAULT NULL,
    UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
  )
RETURN VARCHAR2  

  
  AS
    MI_VLR          NUMBER:=0;
    MI_FECHA        DATE;
    MI_CAMPOS       VARCHAR2(32000);
    MI_VALORES      VARCHAR2(32000);
    MI_CONDICION    VARCHAR2(32000);
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_PRUEBA       NUMBER :=0;
    MI_CONDI        VARCHAR2(32000);
    MI_FECHAINI_PER DATE;
    MI_FECHAFIN_PER DATE;
    MI_EMPFINAL PCK_SUBTIPOS.TI_ID_DE_EMPLEADO;
    MI_ERROR        VARCHAR2(32000) := 'OK';
    MI_CONCEPTO    NUMBER := 0;
    MI_EMPLEADO    NUMBER := 0;
    MI_HISTORICO   NUMBER := 0; 
    MI_DESCRIPCION        VARCHAR2(32000) DEFAULT NULL;
    MI_ESTADO VARCHAR2(32000);
    MI_OBSERVACION VARCHAR2(32000);
    MI_NOM_ESTADO VARCHAR2(32000);
BEGIN
           BEGIN
            PCK_CONEXION.PR_SETUSER(UN_USUARIO);
            IF UN_PROCESO IS NULL                          THEN RETURN 'Debe seleccionar un proceso'; END IF;
            IF UN_ANIO IS NULL OR UN_ANIO             = '' THEN RETURN 'Debe seleccionar un año'; END IF;
            IF UN_MES IS NULL OR UN_MES               = '' THEN RETURN 'Debe seleccionar un mes'; END IF;
            IF UN_PERIODO IS NULL OR UN_PERIODO       = '' THEN RETURN 'Debe seleccionar un periodo'; END IF;
            IF UN_IDEMPLEADO IS NULL OR UN_IDEMPLEADO = '' THEN RETURN 'El codigo del empleado no puede estar vacio'; END IF;
            IF UN_IDCONCEPTO IS NULL OR UN_IDCONCEPTO = '' THEN RETURN 'El codigo del concepto no puede estar vacio'; END IF;
            IF UN_VALOR IS NULL OR UN_VALOR = '' THEN RETURN 'Debe ingresar un valor'; END IF;
            END;
            
            BEGIN
                SELECT 
                    COUNT('x')HISTORICO
                    INTO MI_HISTORICO
                FROM HISTORICOS
                WHERE COMPANIA = UN_COMPANIA
                AND ID_DE_PROCESO = UN_PROCESO
                AND ANO = UN_ANIO
                AND MES = UN_MES
                AND PERIODO = UN_PERIODO
                AND ID_DE_EMPLEADO = UN_IDEMPLEADO
                AND ID_DE_CONCEPTO = UN_IDCONCEPTO;                       
            END;
            
            BEGIN
                SELECT
                    COUNT('x') CONCEPTO
                    INTO MI_CONCEPTO
                FROM CONCEPTOS
                WHERE COMPANIA = UN_COMPANIA
                AND ID_DE_CONCEPTO = UN_IDCONCEPTO;
           END;
           
           BEGIN
                 SELECT COUNT('x') EMPLEADO
                    INTO MI_EMPLEADO
                FROM PERSONAL
                WHERE COMPANIA = UN_COMPANIA
                AND ID_DE_EMPLEADO = UN_IDEMPLEADO;
                
           END; 
    
    IF MI_CONCEPTO NOT IN (0) AND MI_EMPLEADO NOT IN (0) THEN
        
            BEGIN
        
                PCK_NOMINA.PR_INCLUIRHISTORICOF(UN_COMPANIA   => UN_COMPANIA
                                      ,UN_PROCESO    => UN_PROCESO
                                      ,UN_ANIO       => UN_ANIO
                                      ,UN_MES        => UN_MES
                                      ,UN_PERIODO    => UN_PERIODO
                                      ,UN_IDEMPLEADO => UN_IDEMPLEADO
                                      ,UN_IDCONCEPTO => UN_IDCONCEPTO
                                      ,UN_VALOR      => UN_VALOR
                                      ,UN_FECHAC     => UN_FECHAC
                                      ,UN_OBS        => UN_OBS
                                      ,UN_USUARIO    => UN_USUARIO);

--(lvguaba 30/11/2023 7738017) Se implementa consulta para insertar la observacion en excel del estado del empleado y de la modificacion cuando se cambia el valor 
            BEGIN 
                            
              SELECT 
                    P.ESTADO_ACTUAL,
                    NVL(H.OBSERVACIONES,'') OBSERVACIONES,
                    E.NOMBRE
                    INTO MI_ESTADO,
                    MI_OBSERVACION,
                    MI_NOM_ESTADO
                FROM HISTORICOS H
                INNER JOIN PERSONAL P
                 ON H.COMPANIA = P.COMPANIA
                AND H.ID_DE_EMPLEADO = P.ID_DE_EMPLEADO
                INNER JOIN ESTADO_ACTUAL E
                 ON E.CODIGO = P.ESTADO_ACTUAL
               WHERE H.COMPANIA = UN_COMPANIA
                AND H.ID_DE_PROCESO = UN_PROCESO
                AND H.ANO = UN_ANIO
                AND H.MES = UN_MES
                AND H.PERIODO = UN_PERIODO
                AND H.ID_DE_EMPLEADO = UN_IDEMPLEADO
                AND H.ID_DE_CONCEPTO = UN_IDCONCEPTO; 
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_ESTADO := '';
                    MI_OBSERVACION :='';
                    MI_NOM_ESTADO :=''; 
            END;
                
                MI_DESCRIPCION := 'OK' ||' ' || CASE WHEN MI_OBSERVACION <> 'null' THEN MI_OBSERVACION || ', 'ELSE '' END  || 'Estado actual empleado = ' || MI_ESTADO;
                
                MI_ERROR :=  MI_DESCRIPCION;
                                      
                        BEGIN                    
                             MI_CAMPOS := 'MANUAL = -1  ,
                             FECHA = TO_DATE(''' || TO_CHAR(UN_FECHAC,'DD/MM/YYYY') || ''', ''DD/MM/YYYY''),
                             DATE_MODIFIED     = SYSDATE
                                       ,MODIFIED_BY       = ''' || UN_USUARIO || '''';
                             MI_CONDICION := 'COMPANIA       = ''' || UN_COMPANIA ||
                                ''' AND  ID_DE_PROCESO   = '   || UN_PROCESO ||
                                ' AND  ANO             = '   || UN_ANIO||
                                ' AND  MES             = '   || UN_MES||
                                ' AND  PERIODO         = '   || UN_PERIODO||
                                ' AND  ID_DE_EMPLEADO  = '   || UN_IDEMPLEADO||
                                ' AND  ID_DE_CONCEPTO  = '   || UN_IDCONCEPTO || '';
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ('HISTORICOS', 'M', MI_CAMPOS, NULL, NULL, MI_CONDICION );
                    
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                        END;
                                              
            EXCEPTION
                WHEN OTHERS THEN
                MI_ERROR := SQLERRM;              
            END;    
            
            --CREAR EL DATO EN PERSONAL_HISTORICO        
            BEGIN
  
                MI_FECHAINI_PER := PCK_NOMINA.FC_FECHAINIFINPERIODO( UN_COMPANIA => UN_COMPANIA, UN_PROCESO => UN_PROCESO, UN_ANIO => UN_ANIO, UN_MES => UN_MES, UN_PERIODO => UN_PERIODO, UN_FECHAINICIO => 1);
                MI_FECHAFIN_PER := PCK_NOMINA.FC_FECHAINIFINPERIODO( UN_COMPANIA => UN_COMPANIA, UN_PROCESO => UN_PROCESO, UN_ANIO => UN_ANIO, UN_MES => UN_MES, UN_PERIODO => UN_PERIODO, UN_FECHAINICIO => 2); 
                
                MI_CONDI := '   COMPANIA             =''' || UN_COMPANIA || '''' || CHR(10) || CHR(13) || '   AND ID_DE_EMPLEADO NOT IN(0) ' || CHR(10) || CHR(13) || '
                       AND ((FECHA_DE_INGRESO  <=TO_DATE(''' ||MI_FECHAINI_PER|| ''',''DD/MM/YYYY HH24:MI:SS'')' || CHR(10) || CHR(13) || '     AND ESTADO_ACTUAL     <> 3) ' || CHR(10) || CHR(13) || '

                       OR (FECHA_DE_INGRESO  <=TO_DATE(''' || MI_FECHAINI_PER|| ''',''DD/MM/YYYY HH24:MI:SS'') ' || CHR(10) || CHR(13) || '     AND ESTADO_ACTUAL      = 3)) ' || CHR(10) || CHR(13);
                MI_CONDI := MI_CONDI || ' AND ID_DE_EMPLEADO       BETWEEN ' || UN_IDEMPLEADO || ' AND ' || UN_IDEMPLEADO ||' ';
             
             PCK_NOMINA.PR_INCLUIRNOVEDADFONDOS (UN_COMPANIA, UN_PROCESO, UN_ANIO, UN_MES , UN_PERIODO, MI_CONDI, UN_USUARIO);
             
             EXCEPTION
                WHEN OTHERS THEN
                MI_ERROR := SQLERRM;              
             END;
           
         
    ELSE     
    MI_ERROR := CASE  WHEN MI_CONCEPTO = 0 AND MI_EMPLEADO = 0 THEN 
                     '**Carga Masiva. Error.** El concepto: ' || UN_IDCONCEPTO || ' No existe, por favor verificar si el código digitado es correcto, si se requiere crear un nuevo concepto por favor hacerlo en la opción correspondiente del modulo. ' || 'El empleado: ' || UN_IDEMPLEADO || ' No existe, por favor verificar si el código digitado es correcto, si se requiere crear un nuevo empleado por favor hacerlo en la opción correspondiente del modulo.'
                     WHEN MI_CONCEPTO IN (0) THEN '**Carga Masiva. Error.** El concepto: ' || UN_IDCONCEPTO || ' no existe, por favor verificar si el código digitado es correcto, si se requiere crear un nuevo concepto por favor hacerlo en la opción correspondiente del modulo.'
                     WHEN MI_EMPLEADO IN (0) THEN '**Carga Masiva. Error.** El empleado: ' || UN_IDEMPLEADO || ' no existe, por favor verificar si el código digitado es correcto, si se requiere crear un nuevo empleado por favor hacerlo en la opción correspondiente del modulo.' END;     
    END IF;


RETURN MI_ERROR;
END FC_SUBIR_CN_HIST;		  

--INI_7719434(15/12/2022 CARENAS)

FUNCTION FC_CREARNOVEDADESPENSIONADOS
/*
    NAME              : FC_CREARNOVEDADESPENSIONADOS  
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : CRISTIAN FELIPE ARENAS GALVIS
    DATE MIGRADOR     : 05/12/2022
    TIME              : 10:36 AM
    SOURCE MODULE     : NOMINAP2020.12.01_GNAR 2388 22122020 FINAL 88 - FINAL PMAG_PGOB_ACT.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :
    DESCRIPTION       : genera la creacion de una nueva novedad para la opcion crear novedades pen
                        SE AJUSTA AL ESTANDAR.
  */

  (
     UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,  
     UN_FECHAINICIAL           IN DATE,
     UN_FECHAFINAL             IN DATE,
     UN_NUMERODOC         IN VARCHAR2,
     UN_ID_DE_EMPLEADO    IN VARCHAR2,
     UN_COD_NOVEDAD       IN VARCHAR2
  )

RETURN CLOB
AS
MI_CADENA         CLOB;
MI_RS             PCK_SUBTIPOS.TI_STRSQL;
MI_NIT            VARCHAR(60);
MI_RAZONSOCIAL    VARCHAR(150);
TV                VARCHAR(60);
C1                VARCHAR(60);
C2                VARCHAR(60);
C3                VARCHAR(60);
C4                VARCHAR(60);
C5                VARCHAR(60);
C6                VARCHAR(60);
C7                VARCHAR(60);
C8                VARCHAR(60);
C9                VARCHAR(60);
C10               DATE;
C11                VARCHAR(60);
C12                VARCHAR(60);
C13                VARCHAR(60);
C14                VARCHAR(60);
C15                VARCHAR(60);
C16                VARCHAR(60);
C17                VARCHAR(60);
C18                VARCHAR(60);
C19                VARCHAR(60);
C20                VARCHAR(60);
C21                VARCHAR(60);
C22                VARCHAR(60);
C23                VARCHAR(60);
C24                VARCHAR(160);
C25                VARCHAR(60);
C26                VARCHAR(60);
C27                VARCHAR(60);
C28                VARCHAR(60);
C29                VARCHAR(60);
C30                DATE;
C31                DATE;
C32                DATE;
C33                VARCHAR(60);
C34                NUMBER(30);
C35                NUMBER(10,4); 
C36                NUMBER(10,4); 
C37                NUMBER(10,4); 
C38                VARCHAR(60);
C39                VARCHAR(60);
C40                VARCHAR(160);
C41                VARCHAR(60);
C42                VARCHAR(60);
C43                VARCHAR(160);
C44                VARCHAR(60);
C45                VARCHAR(60);
C46                DATE;
C47                VARCHAR(60);
C48                DATE;
C56                DATE;
ID                 NUMBER(15);
MI_PORC            number(10,4);

BEGIN
     FOR RS IN
    (
    SELECT PARAMETROS_DE_ENTRADA.COMPANIA,
    PARAMETROS_DE_ENTRADA.NIT,
    PARAMETROS_DE_ENTRADA.RAZONSOCIAL
    FROM PARAMETROS_DE_ENTRADA 
    WHERE PARAMETROS_DE_ENTRADA.COMPANIA = UN_COMPANIA
    )
    LOOP
    MI_NIT := RS.NIT;
    MI_RAZONSOCIAL := RS.RAZONSOCIAL;
    END LOOP;

     FOR RS IN
    ( SELECT 
                    PERSONAL.COMPANIA,
                    PERSONAL.ID_DE_EMPLEADO,
                    PERSONAL.DCTO_IDENTIDADCAUSANTE, 
                    PERSONAL.NUMERO_DCTOCAUSANTE,
                    PERSONAL.APELLIDO1CAUSANTE,
                    PERSONAL.APELLIDO2CAUSANTE,
                    PERSONAL.NOMBRE1CAUSANTE,
                    PERSONAL.NOMBRE2CAUSANTE,
                    PERSONAL.DCTO_IDENTIDAD,
                    PERSONAL.NUMERO_DCTO,
                    PERSONAL.SEXO,
                    PERSONAL.FECHANCTO,
                    PERSONAL.PAIS_NAC,
                    PERSONAL.DEPARTAMENTO_NAC,
                    PERSONAL.CIUDAD_NAC,
                    PERSONAL.APELLIDO1,
                    PERSONAL.APELLIDO2,
                    PERSONAL.NOMBRES, 
                    PERSONAL.PAIS_HAB,
                    PERSONAL.DEPARTAMENTO_HAB,
                    PERSONAL.CIUDAD_HAB,
                    PERSONAL.COLOMBIANO_RESIDENTE,
                    PERSONAL.TIPOVINCULACION,
                    PERSONAL.TIPOPENSIONADO,
                    PERSONAL.MODALIDAD_PENSION,
                    PERSONAL.PENSIONCOMPARTIDA,
                    PERSONAL.F_INI_PENSION_AFP,
                    PERSONAL.NUM_RESOLUCION_ING,
                    PERSONAL.FECHA_RESOLUCION_ING,
                    PERSONAL.FECHA_DE_INGRESO,
                    PERSONAL.FECHA_ACTIVACION_MESADA,
                    PERSONAL.CAUSA_ACTIVACION_MESADA, 
                    PERSONAL.PORCENTAJE_IBC, 
                    PERSONAL.PORC_PERDIDA_CAPACIDAD_LABORAL,
                    PERSONAL.PORC_PERDIDA_CAPAC_PERMANENTE ,
                    PERSONAL.PORC_PERDIDA_CAPAC_INVALIDEZ,
                    PERSONAL.FECHA_RETIRO_PENSION ,
                    PERSONAL.CAUSA_RETIRO_PENSION,
                    PERSONAL.ESTADO_SUPERVIVENCIA_TITULAR,
                    PERSONAL.FECHA_FALLECIDO_TITULAR,
                    PERSONAL.ESTADO_SUPERVIVENCIA_CAUSANTE,
                    PERSONAL.FECHA_FALLECIDO_CAUSANTE,
                    PERSONAL.Mesada_Pensional
            FROM PERSONAL 
            WHERE PERSONAL.COMPANIA = UN_COMPANIA 
            AND PERSONAL.ID_DE_EMPLEADO = UN_ID_DE_EMPLEADO
            ORDER BY PERSONAL.APELLIDO1, 
            PERSONAL.APELLIDO2,
            PERSONAL.NOMBRES
            )
            LOOP

            TV := RS.TIPOVINCULACION;
                 IF TV = 1 OR TV = 8 OR TV = 9 OR TV = 10 OR TV = 11 OR TV = 12 OR TV = 13 OR TV = 14 OR TV = 15 THEN
                    IF RS.DCTO_IDENTIDAD = 'C' THEN
                    C1 :='CC';
                    END IF;
                    IF RS.DCTO_IDENTIDAD = 'E' THEN
                    C1 :='CE';
                    END IF;
                    IF RS.DCTO_IDENTIDAD = 'P' THEN
                    C1 :='PA';
                    END IF;
                    IF RS.DCTO_IDENTIDAD = 'T' THEN
                    C1 :='TI';
                    END IF;
                    IF RS.DCTO_IDENTIDAD = '' THEN
                    C1 :='R';
                    END IF;
                    IF RS.DCTO_IDENTIDAD = 'R' THEN
                    C1 :='RC';
                    END IF;
                    C2 := lpad(RS.NUMERO_DCTO,17,'0');
                    C3 := RS.APELLIDO1;
                    C4 := RS.APELLIDO2;
                    C5 := SUBSTR(RS.NOMBRES,0,insTr(RS.NOMBRES,' ' ));
                    C6 := SUBSTR(RS.NOMBRES,insTr(RS.NOMBRES,' ' ));
                   -- C6 := SUBSTR(C6,0,insTr(C6,' ' )); 
                    IF RS.DCTO_IDENTIDAD = 'C' THEN
                    C7 :='CC';
                    END IF;
                    IF RS.DCTO_IDENTIDAD = 'E' THEN
                    C7 :='CE';
                    END IF;
                    C8 := lpad(RS.NUMERO_DCTO,17,'0');
                    C9 := RS.SEXO;
                    C10 := RS.FECHANCTO;
                    C11 := RS.DEPARTAMENTO_NAC;
                    C12 := RS.CIUDAD_NAC;
                    C13 := C3;
                    C14 := C4;
                    C15 := C5;
                    C16 := C6;
                 END IF; 

                 IF TV = 2 OR TV = 3 OR TV = 4 OR TV = 5 OR TV = 6 OR TV = 7 THEN
                     IF RS.DCTO_IDENTIDADCAUSANTE = 01 THEN
                     C1 := 'CC';
                     END IF;
                     IF RS.DCTO_IDENTIDADCAUSANTE = 02 THEN
                     C1 := 'CE';
                     END IF;
                     IF RS.DCTO_IDENTIDADCAUSANTE = 03 THEN
                     C1 := 'PA';
                     END IF;
                     IF RS.DCTO_IDENTIDADCAUSANTE = 04 THEN
                     C1 := 'TI';
                     END IF;
                     IF RS.DCTO_IDENTIDADCAUSANTE = 09 THEN
                     C1 := 'RC';
                     END IF;
                     IF RS.DCTO_IDENTIDADCAUSANTE = 10 THEN
                     C1 := 'CD';
                     END IF;
                     C2 := lpad(RS.NUMERO_DCTOCAUSANTE,17,'0');
                     C3 := RS.APELLIDO1CAUSANTE;
                     C4 := RS.APELLIDO2CAUSANTE;
                     C5 := RS.NOMBRE1CAUSANTE;
                     C6 := RS.NOMBRE2CAUSANTE;
                     IF RS.DCTO_IDENTIDAD = 'C' THEN
                     C7 :='CC';
                     END IF;
                     IF RS.DCTO_IDENTIDAD = 'E' THEN
                     C7 :='CE';
                     END IF;
                     C8 := lpad(RS.NUMERO_DCTO,17,'0');
                     C9 := RS.SEXO;
                     C10 := RS.FECHANCTO;
                     C11 := RS.DEPARTAMENTO_NAC;
                     C12 := RS.CIUDAD_NAC;
                     C13 := RS.APELLIDO1;
                     C14 := CaSe whEn RS.APELLIDO2 = ' ' tHen NuLl eLSE RS.APELLIDO2 enD;
                     C15 := SUBSTR(RS.NOMBRES,0,insTr(RS.NOMBRES,' ' ));
                     C15 := rEplacE(SUBSTR(C15,0), ' ','');
                     C16 := SUBSTR(RS.NOMBRES,insTr(RS.NOMBRES,' ' )); 
                     C16 := rEplacE(SUBSTR(C16,0), ' ','');
                 END IF;
                 C17 := RS.DEPARTAMENTO_HAB; 
                 C18 := RS.CIUDAD_HAB;
                 C19 := 'N';
                 IF RS.COLOMBIANO_RESIDENTE = -1 THEN
                     C17 := 'EX';
                     C18 := 'EXT';
                     C19 := 'S';
                 END IF;
                 C20 := 'N';
                 C21 := 'NI';
                 C22 := lpad(rEplacE(SUBSTR(MI_NIT,0,insTr(MI_NIT,'-')-1), '.',''),17,'0'); 
                 C23 := SUBSTR(MI_NIT,insTr(MI_NIT,'-')+1);
                 C24 := SUBSTR(MI_RAZONSOCIAL,0,insTr(MI_RAZONSOCIAL,'-')-1) ;
                 C25 := RS.TIPOVINCULACION;
                 C26 := RS.TIPOPENSIONADO;
                 C27 := RS.MODALIDAD_PENSION;
                 C28 := case  RS.PENSIONCOMPARTIDA when '0' then 'N' else 'S' end;
                 C29 := RS.NUM_RESOLUCION_ING;
                 C30 := RS.FECHA_RESOLUCION_ING;
                 C31 := to_date(to_char(RS.FECHA_DE_INGRESO,'DD/MM/YYYY'),'DD/MM/YYYY');
                 C32 := RS.FECHA_ACTIVACION_MESADA;
                 C33 := RS.CAUSA_ACTIVACION_MESADA;
                 IF RS.Mesada_Pensional IS NULL OR RS.Mesada_Pensional = 0 THEN          
                             FOR RS IN
                            (
                         SELECT Categoria.Salario_Base
                         FROM Personal 
                         LEFT JOIN Categoria 
                         ON Personal.ID_de_Categoria = Categoria.ID_de_Categoria
                         AND Personal.Escalafon = Categoria.Escalafon 
                         AND Personal.Compania = Categoria.Compania
                         AND PERSONAL.ANO = Categoria.Ano
                         WHERE Personal.Compania = UN_COMPANIA
                         AND Personal.ID_de_Empleado =  UN_ID_DE_EMPLEADO
                            )
                        LOOP
                       C34 := RS.Salario_Base;
                        END LOOP;
                ELSE
                    C34 := RS.Mesada_Pensional;
                 END IF;
                 MI_PORC := NVL(RS.PORCENTAJE_IBC,0);
                 C35 := case  MI_PORC when 0 then 100 else MI_PORC end /100;
                 MI_PORC := NVL(RS.PORC_PERDIDA_CAPACIDAD_LABORAL,0);
                 C36 := case  MI_PORC when 0 then 100 else MI_PORC end /100;
                 MI_PORC := NVL(RS.PORC_PERDIDA_CAPAC_INVALIDEZ,0);
                 C37 := case  MI_PORC when 0 then 100 else MI_PORC end /100;
                 C38 := 'NI';
                 C39 := C22;
                 C40 := C24;
                 C41 := 'NI';
                 C42 := lpad(rEplacE(SUBSTR(MI_NIT,0,insTr(MI_NIT,'-')-1), '.',''),17,'0');
                 C43 := SUBSTR(MI_RAZONSOCIAL,0,insTr(MI_RAZONSOCIAL,'-')-1) ;
                 C44 := RS.NUMERO_DCTO;
                 C45 := 0;
                 IF TV = 16 OR TV = 17 OR TV = 18 THEN
                 C45 := RS.ESTADO_SUPERVIVENCIA_TITULAR;
                 END IF;
                 IF TV = 16 OR TV = 17 OR TV = 18 AND RS.ESTADO_SUPERVIVENCIA_TITULAR <> 0 THEN
                 C46 := RS.FECHA_FALLECIDO_TITULAR;
                 END IF;
                 C47 := '0';
                 IF TV = 16 OR TV = 17 OR TV = 18 THEN
                 C47 := RS.ESTADO_SUPERVIVENCIA_TITULAR;
                 END IF;
                 IF TV = 16 OR TV = 17 OR TV = 18 AND RS.ESTADO_SUPERVIVENCIA_TITULAR <> 0 THEN
                 C48 := RS.FECHA_FALLECIDO_TITULAR;
                 END IF;
                 C56 := RS.FECHA_RETIRO_PENSION;

                 ID := UN_ID_DE_EMPLEADO;

            MI_CADENA:= RS.COMPANIA; 
                  END LOOP;

                FOR RS IN(
                    SELECT PLANOS_NOVEDADES_PENSIONADOS.*
                    FROM PLANOS_NOVEDADES_PENSIONADOS
                    WHERE PLANOS_NOVEDADES_PENSIONADOS.ID_DE_EMPLEADO = UN_ID_DE_EMPLEADO  
                    AND PLANOS_NOVEDADES_PENSIONADOS.FECHA_INICIAL Between UN_FECHAINICIAL And UN_FECHAFINAL
                    AND PLANOS_NOVEDADES_PENSIONADOS.FECHA_FINAL Between UN_FECHAINICIAL And UN_FECHAFINAL
                    AND PLANOS_NOVEDADES_PENSIONADOS.NOVEDAD = UN_COD_NOVEDAD
                    )
                LOOP

            MERGE INTO PLANOS_NOVEDADES_PENSIONADOS PNP USING(
                                                            SELECT 
                                                                PNP.ID_DE_EMPLEADO,
                                                                PNP.FECHA_INICIAL,
                                                                PNP.FECHA_FINAL
                                                            FROM PLANOS_NOVEDADES_PENSIONADOS PNP
                                                                             WHERE PNP.ID_DE_EMPLEADO = UN_ID_DE_EMPLEADO  
                                                                            AND PNP.FECHA_INICIAL Between UN_FECHAINICIAL And UN_FECHAFINAL
                                                                            AND PNP.FECHA_FINAL Between UN_FECHAINICIAL And UN_FECHAFINAL
                                                        ) PNP2 ON (PNP.ID_DE_EMPLEADO = PNP2.ID_DE_EMPLEADO
                                                                 AND PNP.FECHA_INICIAL = PNP2.FECHA_INICIAL
                                                                 AND PNP.FECHA_FINAL = PNP2.FECHA_FINAL
                                                        )
                                                        WHEN MATCHED THEN UPDATE SET C1	=	                C1,
                                                                                    C2	=	                C2, 
                                                                                    C3	=	                C3,
                                                                                    C4	=	                C4,
                                                                                    C5	=	                C5,
                                                                                    C6	=	                C6,
                                                                                    C7	=	                C7,
                                                                                    C8	=	                C8,
                                                                                    C9	=	                C9,
                                                                                    C10	=	                C10,
                                                                                    C11	=	                C11,
                                                                                    C12	=	                C12,
                                                                                    C13	=	                C13, 
                                                                                    C14	=	                C14,
                                                                                    C15	=	                C15,
                                                                                    C16	=	                C16,
                                                                                    C17	=	                C17,
                                                                                    C18	=	                C18,
                                                                                    C19	=	                C19,
                                                                                    C20	=	                C20,
                                                                                    C21	=	                C21,
                                                                                    C22	=	                C22,
                                                                                    C23	=	                C23,
                                                                                    C24	=	                C24,
                                                                                    C25	=	                C25,
                                                                                    C26	=	                C26,
                                                                                    C27	=	                C27,
                                                                                    C28	=	                C28,
                                                                                    C29	=	                C29,
                                                                                    C30	=	                C30,
                                                                                    C31	=	                C31,
                                                                                    C32	=	                C32,
                                                                                    C33	=	                C33,
                                                                                    C34	=	                C34,
                                                                                    C35	=	                C35,
                                                                                    C36	=	                C36,
                                                                                    C37	=	                C37,
                                                                                    C38	=	                C38,
                                                                                    C39	=	                C39,
                                                                                    C40	=	                C40,
                                                                                    C41	=	                C41,
                                                                                    C42	=	                C22,
                                                                                    C43	=	                C24,
                                                                                    C44	=	                C44, 
                                                                                    C45	=	                C45,
                                                                                    C46	=	                C46,
                                                                                    C47	=	                C47,
                                                                                    C48	=	                C48,
                                                                                    C56	=	                C56;

                MI_CADENA := 'UPDATE';
                END LOOP;
            IF MI_CADENA = '005' THEN
            INSERT INTO PLANOS_NOVEDADES_PENSIONADOS
            (
                COMPANIA, 
                ID_DE_EMPLEADO,
                IDENTIFICACION,
                FECHA_INICIAL,
                FECHA_FINAL, 
                NOVEDAD,
                C1,
                C2,
                C3,
                C4,
                C5,
                C6,
                C7,
                C8,
                C9,
                C10,
                C11,
                C12,
                C13,
                C14,
                C15,
                C16,
                C17,
                C18,
                C19,
                C20,
                C21,
                C22,
                C23,
                C24,
                C25,
                C26,
                C27,
                C28,
                C29,
                C30,
                C31,
                C32,
                C33,
                C34,
                C35,
                C36,
                C37,
                C38,
                C39,
                C40,
                C41,
                C42,
                C43,
                C44,
                C45,
                C46,
                C47,
                C48,
                C56
            )
            VALUES
            (
             UN_COMPANIA, 
             UN_ID_DE_EMPLEADO,
             UN_NUMERODOC,
             UN_FECHAINICIAL,
             UN_FECHAFINAL,
             UN_COD_NOVEDAD ,
             C1,
                C2,
                C3,
                C4,
                C5,
                C6,
                C7,
                C8,
                C9,
                C10,
                C11,
                C12,
                C13,
                C14,
                C15,
                C16,
                C17,
                C18,
                C19,
                C20,
                C21,
                C22,
                C23,
                C24,
                C25,
                C26,
                C27,
                C28,
                C29,
                C30,
                C31,
                C32,
                C33,
                C34,
                C35,
                C36,
                C37,
                C38,
                C39,
                C40,
                C41,
                C42,
                C43,
                C44,
                C45,
                C46,
                C47,
                C48,
                C56
     );
            END IF;



    RETURN MI_CADENA;    

END FC_CREARNOVEDADESPENSIONADOS;

FUNCTION FC_GENERARPLANOPERIODICO ( 
/*
    NAME              : FC_CREARNOVEDADESPENSIONADOS  
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : CRISTIAN FELIPE ARENAS GALVIS
    DATE MIGRADOR     : 05/12/2022
    TIME              : 10:36 AM
    SOURCE MODULE     : NOMINAP2020.12.01_GNAR 2388 22122020 FINAL 88 - FINAL PMAG_PGOB_ACT.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :
    DESCRIPTION       : genera la creacion de una nueva novedad para la opcion crear novedades pen
                        SE AJUSTA AL ESTANDAR.
  */

   UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,    
   UN_FECHAINICIAL           IN DATE,
   UN_FECHAFINAL             IN DATE
  )
RETURN CLOB
  AS
MI_CADENA         CLOB;
MI_RS             PCK_SUBTIPOS.TI_STRSQL;
MI_NIT            VARCHAR(60);
MI_RAZONSOCIAL    VARCHAR(150);
TV                VARCHAR(60);
C1                VARCHAR(60);
C2                VARCHAR(60);
C3                VARCHAR(60);
C4                VARCHAR(60);
C5                VARCHAR(60);
C6                VARCHAR(60);
C7                VARCHAR(60);
C8                VARCHAR(60);
C9                VARCHAR(60);
C10               VARCHAR(60);
C11                VARCHAR(60);
C12                VARCHAR(60);
C13                VARCHAR(60);
C14                VARCHAR(60);
C15                VARCHAR(60);
C16                VARCHAR(60);
C17                VARCHAR(60);
C18                VARCHAR(60);
C19                VARCHAR(60);
C20                VARCHAR(60);
C21                VARCHAR(60);
C22                VARCHAR(60);
C23                VARCHAR(60);
C24                VARCHAR(160);
C25                VARCHAR(60);
C26                VARCHAR(60);
C27                VARCHAR(60);
C28                VARCHAR(60);
C29                VARCHAR(60);
C30                VARCHAR(60);
C31                VARCHAR(60);
C32                VARCHAR(60);
C33                VARCHAR(60);
C34                NUMBER(30);
C35                VARCHAR(60);
C36                VARCHAR(60); 
C37                VARCHAR(60);
C38                VARCHAR(60);
C39                VARCHAR(60);
C40                VARCHAR(160);
C41                VARCHAR(60);
C42                VARCHAR(60);
C43                VARCHAR(160);
C44                VARCHAR(60);
C45                VARCHAR(60);
C46                VARCHAR(60);
C47                VARCHAR(60);
C48                VARCHAR(60);
C56                VARCHAR(60);
ID                 NUMBER(15);
MI_PORC            number(10,4);
FEI             VARCHAR(60);
TOTAL_EMPLEADOS     VARCHAR(20);
FEF             VARCHAR(20);
FEFI                VARCHAR(20);
CONT                NUMBER(10);
cadena2             varchar(60);
TOTAL_REGISTROS     NUMBER(10);
BEGIN
     FOR RS IN
    (
    SELECT PARAMETROS_DE_ENTRADA.COMPANIA,
    PARAMETROS_DE_ENTRADA.NIT,
    PARAMETROS_DE_ENTRADA.RAZONSOCIAL
    FROM PARAMETROS_DE_ENTRADA 
    WHERE PARAMETROS_DE_ENTRADA.COMPANIA = UN_COMPANIA
    )
    LOOP
    --FEI := to_date(to_char(UN_FECHAINICIAL,'DD/MM/YYYY'),'DD-MM-YYYY');
    MI_NIT := RS.NIT;
    MI_NIT := lpad(rEplacE(SUBSTR(MI_NIT,0,insTr(MI_NIT,'-')-1), '.',''),17,'0'); 
    MI_RAZONSOCIAL := RS.RAZONSOCIAL;

    END LOOP;

    FOR RS IN(
        SELECT COUNT(ID_DE_EMPLEADO) TOTAL_EMPLEADOS
        FROM PERSONAL
        WHERE PERSONAL.COMPANIA = UN_COMPANIA 
        AND PERSONAL.ID_DE_EMPLEADO<>'0'
        AND PERSONAL.FECHA_DE_INGRESO Between UN_FECHAINICIAL And UN_FECHAFINAL
    )
    LOOP
    TOTAL_EMPLEADOS := RS.TOTAL_EMPLEADOS;
    TOTAL_REGISTROS := RS.TOTAL_EMPLEADOS;
    TOTAL_EMPLEADOS := lpad(TOTAL_EMPLEADOS,8,'0');
    FEI := to_char(to_date(UN_FECHAINICIAL, 'DD/MM/YYYY'), 'YYYY-MM-DD');
    FEF := to_char(to_date(UN_FECHAFINAL, 'DD/MM/YYYY'), 'YYYY-MM-DD');
    FEFI :=  rEplacE(SUBSTR(FEF,0), '-','');  


    MI_CADENA := '1' || ',' || MI_NIT  || ',' || FEI ||  ',' ||  FEF || ',' || TOTAL_EMPLEADOS || ',' || 'RUA' || '250' || 'NMPP' ||  FEFI ||  'NI' || MI_NIT || '.TXT' || CHR(10);

    END LOOP;

     FOR RS IN
    ( SELECT 
                    PERSONAL.COMPANIA,
                    PERSONAL.ID_DE_EMPLEADO,
                    PERSONAL.DCTO_IDENTIDADCAUSANTE, 
                    PERSONAL.NUMERO_DCTOCAUSANTE,
                    PERSONAL.APELLIDO1CAUSANTE,
                    PERSONAL.APELLIDO2CAUSANTE,
                    PERSONAL.NOMBRE1CAUSANTE,
                    PERSONAL.NOMBRE2CAUSANTE,
                    PERSONAL.DCTO_IDENTIDAD,
                    PERSONAL.NUMERO_DCTO,
                    PERSONAL.SEXO,
                    TO_CHAR(PERSONAL.FECHANCTO, 'YYYY-MM-DD') FECHANCTO,
                    PERSONAL.PAIS_NAC,
                    PERSONAL.DEPARTAMENTO_NAC,
                    PERSONAL.CIUDAD_NAC,
                    PERSONAL.APELLIDO1,
                    PERSONAL.APELLIDO2,
                    PERSONAL.NOMBRES, 
                    PERSONAL.PAIS_HAB,
                    PERSONAL.DEPARTAMENTO_HAB,
                    PERSONAL.CIUDAD_HAB,
                    PERSONAL.COLOMBIANO_RESIDENTE,
                    PERSONAL.TIPOVINCULACION,
                    PERSONAL.TIPOPENSIONADO,
                    PERSONAL.MODALIDAD_PENSION,
                    PERSONAL.PENSIONCOMPARTIDA,
                    PERSONAL.F_INI_PENSION_AFP,
                    PERSONAL.NUM_RESOLUCION_ING,
                    TO_CHAR(PERSONAL.FECHA_RESOLUCION_ING, 'YYYY-MM-DD') FECHA_RESOLUCION_ING,
                    TO_CHAR(PERSONAL.FECHA_DE_INGRESO, 'YYYY-MM-DD') FECHA_DE_INGRESO,
                    TO_CHAR(PERSONAL.FECHA_ACTIVACION_MESADA, 'YYYY-MM-DD') FECHA_ACTIVACION_MESADA,
                    PERSONAL.CAUSA_ACTIVACION_MESADA, 
                    PERSONAL.PORCENTAJE_IBC, 
                    PERSONAL.PORC_PERDIDA_CAPACIDAD_LABORAL,
                    /*" & _ "*/ PERSONAL.PORC_PERDIDA_CAPAC_PERMANENTE ,
                    PERSONAL.PORC_PERDIDA_CAPAC_INVALIDEZ,
              --      FONDO_DE_PENSIONES.Nit, 
              --      FONDO_DE_PENSIONES.Nombre_del_Fondo,
                    PERSONAL.FECHA_RETIRO_PENSION ,
                    PERSONAL.CAUSA_RETIRO_PENSION,
                    PERSONAL.ESTADO_SUPERVIVENCIA_TITULAR,
                    TO_CHAR(PERSONAL.FECHA_FALLECIDO_TITULAR, 'YYYY-MM-DD') FECHA_FALLECIDO_TITULAR,
                    PERSONAL.ESTADO_SUPERVIVENCIA_CAUSANTE,
                    PERSONAL.FECHA_FALLECIDO_CAUSANTE,
                    PERSONAL.Mesada_Pensional
            FROM PERSONAL 
            WHERE PERSONAL.COMPANIA = UN_COMPANIA 
             --AND PERSONAL.ID_DE_EMPLEADO)<>'0'
            AND PERSONAL.FECHA_DE_INGRESO Between UN_FECHAINICIAL And UN_FECHAFINAL
            ORDER BY PERSONAL.APELLIDO1, 
            PERSONAL.APELLIDO2,
            PERSONAL.NOMBRES
            )
            LOOP
            ID := RS.ID_DE_EMPLEADO;
            TV := RS.TIPOVINCULACION;
                 IF TV = 1 OR TV = 8 OR TV = 9 OR TV = 10 OR TV = 11 OR TV = 12 OR TV = 13 OR TV = 14 OR TV = 15 THEN
                    IF RS.DCTO_IDENTIDAD = 'C' THEN
                    C1 :='CC';
                    END IF;
                    IF RS.DCTO_IDENTIDAD = 'E' THEN
                    C1 :='CE';
                    END IF;
                    IF RS.DCTO_IDENTIDAD = 'P' THEN
                    C1 :='PA';
                    END IF;
                    IF RS.DCTO_IDENTIDAD = 'T' THEN
                    C1 :='TI';
                    END IF;
                    IF RS.DCTO_IDENTIDAD = '' THEN
                    C1 :='R';
                    END IF;
                    IF RS.DCTO_IDENTIDAD = 'R' THEN
                    C1 :='RC';
                    END IF;
                    C2 := RS.NUMERO_DCTO;
                    C3 := RS.APELLIDO1;
                    C4 := RS.APELLIDO2;
                    C5 := SUBSTR(RS.NOMBRES,0,insTr(RS.NOMBRES,' ' ));
                    C6 := SUBSTR(RS.NOMBRES,insTr(RS.NOMBRES,' ' ));
                   -- C6 := SUBSTR(C6,0,insTr(C6,' ' )); 
                    IF RS.DCTO_IDENTIDAD = 'C' THEN
                    C7 :='CC';
                    END IF;
                    IF RS.DCTO_IDENTIDAD = 'E' THEN
                    C7 :='CE';
                    END IF;
                    C8 := lpad(RS.NUMERO_DCTO,17,'0');
                    C9 := RS.SEXO;
                    C10 := RS.FECHANCTO;
                    C11 := RS.DEPARTAMENTO_NAC;
                    C12 := RS.CIUDAD_NAC;
                    C13 := C3;
                    C14 := C4;
                    C15 := C5; 
                    C16 := C6; 
                 END IF; 

                 IF TV = 2 OR TV = 3 OR TV = 4 OR TV = 5 OR TV = 6 OR TV = 7 THEN
                     IF RS.DCTO_IDENTIDADCAUSANTE = 01 THEN
                     C1 := 'CC';
                     END IF;
                     IF RS.DCTO_IDENTIDADCAUSANTE = 02 THEN
                     C1 := 'CE';
                     END IF;
                     IF RS.DCTO_IDENTIDADCAUSANTE = 03 THEN
                     C1 := 'PA';
                     END IF;
                     IF RS.DCTO_IDENTIDADCAUSANTE = 04 THEN
                     C1 := 'TI';
                     END IF;
                     IF RS.DCTO_IDENTIDADCAUSANTE = 09 THEN
                     C1 := 'RC';
                     END IF;
                     IF RS.DCTO_IDENTIDADCAUSANTE = 10 THEN
                     C1 := 'CD';
                     END IF;
                     C2 := RS.NUMERO_DCTOCAUSANTE;
                     C3 := RS.APELLIDO1CAUSANTE;
                     C4 := RS.APELLIDO2CAUSANTE;
                     C5 := RS.NOMBRE1CAUSANTE;
                     C6 := RS.NOMBRE2CAUSANTE;
                     IF RS.DCTO_IDENTIDAD = 'C' THEN
                     C7 :='CC';
                     END IF;
                     IF RS.DCTO_IDENTIDAD = 'E' THEN
                     C7 :='CE';
                     END IF;
                     C8 := lpad(RS.NUMERO_DCTO,17,'0');
                     C9 := RS.SEXO;
                     C10 := RS.FECHANCTO;
                     C11 := RS.DEPARTAMENTO_NAC;
                     C12 := RS.CIUDAD_NAC;
                     C13 := RS.APELLIDO1;
                     C14 := RS.APELLIDO2;
                     C15 := SUBSTR(RS.NOMBRES,0,insTr(RS.NOMBRES,' ' ));
                     C15 := rEplacE(SUBSTR(C15,0), ' ',''); 
                     C16 := SUBSTR(RS.NOMBRES,insTr(RS.NOMBRES,' ' ));      
                     C16 := rEplacE(SUBSTR(C16,0), ' ','');
                 END IF;
                 C17 := RS.DEPARTAMENTO_HAB; 
                 C18 := RS.CIUDAD_HAB;
                 C19 := 'N';
                 IF RS.COLOMBIANO_RESIDENTE = -1 THEN
                     C17 := 'EX';
                     C18 := 'EXT';
                     C19 := 'S';
                 END IF;
                 C20 := 'N';
                 C21 := 'NI';
                 C22 := lpad(rEplacE(SUBSTR(MI_NIT,0,insTr(MI_NIT,'-')-1), '.',''),17,'0'); 
                 C23 := SUBSTR(MI_NIT,insTr(MI_NIT,'-')+1);
                 C24 := SUBSTR(MI_RAZONSOCIAL,0,insTr(MI_RAZONSOCIAL,'-')-1) ;
                 C25 := RS.TIPOVINCULACION;
                 C26 := RS.TIPOPENSIONADO;
                 C27 := RS.MODALIDAD_PENSION;
                 C28 := case  RS.PENSIONCOMPARTIDA when '0' then 'N' else 'S' end;
                 C29 := RS.NUM_RESOLUCION_ING;
                 C30 := RS.FECHA_RESOLUCION_ING;
                 C31 := RS.FECHA_DE_INGRESO;--to_date(to_char(RS.FECHA_DE_INGRESO,'DD/MM/YYYY'),'DD-MM-YYYY');
                 C32 := RS.FECHA_ACTIVACION_MESADA;
                 C33 := RS.CAUSA_ACTIVACION_MESADA;
                 IF RS.Mesada_Pensional IS NULL OR RS.Mesada_Pensional = 0 THEN          
                             FOR RS IN
                            (
                         SELECT Categoria.Salario_Base
                         FROM Personal 
                         LEFT JOIN Categoria 
                         ON Personal.ID_de_Categoria = Categoria.ID_de_Categoria
                         AND Personal.Escalafon = Categoria.Escalafon 
                         AND Personal.Compania = Categoria.Compania
                         AND PERSONAL.ANO = Categoria.Ano
                         WHERE Personal.Compania = UN_COMPANIA
                         AND Personal.ID_de_Empleado =  ID
                            )
                        LOOP
                       C34 := RS.Salario_Base;
                        END LOOP;
                ELSE
                    C34 := RS.Mesada_Pensional;
                 END IF;
                 MI_PORC := NVL(RS.PORCENTAJE_IBC,0);
                 C35 := to_char(case  MI_PORC when 0 then 100 else MI_PORC end /100,'0.0000');
                 MI_PORC := NVL(RS.PORC_PERDIDA_CAPACIDAD_LABORAL,0);
                 C36 := to_char(case  MI_PORC when 0 then 100 else MI_PORC end /100,'0.0000');
                 MI_PORC := NVL(RS.PORC_PERDIDA_CAPAC_INVALIDEZ,0);
                 C37 := to_char(case  MI_PORC when 0 then 100 else MI_PORC end /100,'0.0000');
                 C38 := 'NI';
                 C39 := C22;
                 C40 := C24;
                 C41 := 'NI';
                 C42 := lpad(rEplacE(SUBSTR(MI_NIT,0,insTr(MI_NIT,'-')-1), '.',''),17,'0');
                 C43 := SUBSTR(MI_RAZONSOCIAL,0,insTr(MI_RAZONSOCIAL,'-')-1) ;
                 C44 := RS.NUMERO_DCTO;
                 C45 := 0;
                 IF TV = 16 OR TV = 17 OR TV = 18 THEN
                 C45 := RS.ESTADO_SUPERVIVENCIA_TITULAR;
                 END IF;
                 IF TV = 16 OR TV = 17 OR TV = 18 AND RS.ESTADO_SUPERVIVENCIA_TITULAR <> 0 THEN
                 C46 := RS.FECHA_FALLECIDO_TITULAR;
                 END IF;
                 C47:= '0';
                 IF TV = 16 OR TV = 17 OR TV = 18 THEN
                 C47 := RS.ESTADO_SUPERVIVENCIA_TITULAR;
                 END IF;
                 IF TV = 16 OR TV = 17 OR TV = 18 AND RS.ESTADO_SUPERVIVENCIA_TITULAR <> 0 THEN
                 C48 := RS.FECHA_FALLECIDO_TITULAR;
                 END IF;
                 C56 := RS.FECHA_RETIRO_PENSION;
                
                
              cadena2 := '';
              
              IF CONT != TOTAL_EMPLEADOS THEN
              cadena2 := chr(10);

              END IF;


            MI_CADENA:= MI_CADENA || '2' || ',' ||  C1 || ',' || C2 || ',' || C3 || ',' || C4  || ',' || C5|| ',' || C6 || ',' || C7 || ',' || C8 || ',' || C9 || ',' || C10 || ',' || C11 || ',' || C12 || ',' || C13 || ',' || C14 || ',' || C15 || ',' || C16 || ',' || C17 || ',' || C18 || ',' || C19 || ',' || C20 || ',' || C21 || ',' || C22 || ',' || C23 || ',' || C24 || ',' || C25 || ',' || C26 || ',' || C27 || ',' || C28 || ',' || C29 || ',' || C30 || ',' || C31 || ',' || C32 || ',' || C33 || ',' || C34 || ',' || C35 || ',' || C36 || ',' || C37 || ',' || C38 || ',' || C39 || ',' || C40 || ',' || C41 || ',' || C42 || ',' || C43 || ',' || C44 || ',' || C45 || ',' || C46 || ',' || C47 || ',' || C48 || CHR(10);

        --    Print #2, "2," & C1 & "," & C2 & "," & C3 & "," & C4 & "," & C5 & "," & C6 & "," & C7 & "," & C8 & "," & C9 & "," & C10 & "," & C11 & "," & C12 & "," & C13 & "," & C14 & "," & C15 & "," & C16 & "," & C17 & "," & C18 & "," & C19 & "," & C20 & "," & C21 & "," & C22 & "," & C23 & "," & C24 & "," & C25 & "," & C26 & "," & C27 & "," & C28 & "," & C29 & "," & C30 & "," & C31 & "," & C32 & "," & C33 & "," & C34 & "," & C35 & "," & C36 & "," & C37 & "," & C38 & "," & C39 & "," & C40 & "," & C41 & "," & C42 & "," & C43 & "," & C44 & "," & C45 & "," & C46 & "," & C47 & "," & C48 & IIf(lcount = contador1, Chr(4), "")
            
            CONT := CONT + 1;
                  END LOOP;

    RETURN MI_CADENA;    

END FC_GENERARPLANOPERIODICO;


FUNCTION FC_GENERARPLANOSPENSIONADOS (
/*
    NAME              : FC_CREARNOVEDADESPENSIONADOS  
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : CRISTIAN FELIPE ARENAS GALVIS
    DATE MIGRADOR     : 05/12/2022
    TIME              : 10:36 AM
    SOURCE MODULE     : NOMINAP2020.12.01_GNAR 2388 22122020 FINAL 88 - FINAL PMAG_PGOB_ACT.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :
    DESCRIPTION       : genera la creacion de una nueva novedad para la opcion crear novedades pen
                        SE AJUSTA AL ESTANDAR.
  */

   UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,    
   UN_FECHAINICIAL           IN DATE,
   UN_FECHAFINAL             IN DATE
  )
RETURN CLOB
  AS
MI_CADENA         CLOB;
MI_RS             PCK_SUBTIPOS.TI_STRSQL;
TV                VARCHAR(60);
C1                VARCHAR(60);
C2                VARCHAR(60);
C3                VARCHAR(60);
C4                VARCHAR(60);
C5                VARCHAR(60);
C6                VARCHAR(60);
C7                VARCHAR(60);
C8                VARCHAR(60);
C9                VARCHAR(60);
C10               VARCHAR(60);
C11                VARCHAR(60);
C12                VARCHAR(60);
C13                VARCHAR(60);
C14                VARCHAR(60);
C15                VARCHAR(60);
C16                VARCHAR(60);
C17                VARCHAR(60);
C18                VARCHAR(60);
C19                VARCHAR(60);
C20                VARCHAR(60);
C21                VARCHAR(60);
C22                VARCHAR(60);
C23                VARCHAR(60);
C24                VARCHAR(160);
C25                VARCHAR(60);
C26                VARCHAR(60);
C27                VARCHAR(60);
C28                VARCHAR(60);
C29                VARCHAR(60);
C30                VARCHAR(60);
C31                VARCHAR(60);
C32                VARCHAR(60);
C33                VARCHAR(60);
C34                VARCHAR(60);
C35                VARCHAR(60);
C36                VARCHAR(60); 
C37                VARCHAR(60);
C38                VARCHAR(60);
C39                VARCHAR(60);
C40                VARCHAR(160);
C41                VARCHAR(60);
C42                VARCHAR(60);
C43                VARCHAR(160);
C44                VARCHAR(60);
C45                VARCHAR(60);
C46                VARCHAR(60);
C47                VARCHAR(60);
C48                VARCHAR(60);
C49                VARCHAR(60);
C51                VARCHAR(60);
C55                VARCHAR(60);
C56                VARCHAR(60);
ID                 NUMBER(15);
MI_PORC            number(10,4);
FEI             VARCHAR(60);
FEF             VARCHAR(60);
FEFI             VARCHAR(60);
TOTAL_EMPLEADOS     VARCHAR(20);
MI_NIT               VARCHAR(60);
CONT                NUMBER(10);
cadena2             varchar(60);
TOTAL_REGISTROS     NUMBER(10);
BEGIN

    CONT := 1;
     FOR RS IN
    (
    SELECT PARAMETROS_DE_ENTRADA.COMPANIA,
    PARAMETROS_DE_ENTRADA.NIT,
    PARAMETROS_DE_ENTRADA.RAZONSOCIAL
    FROM PARAMETROS_DE_ENTRADA 
    WHERE PARAMETROS_DE_ENTRADA.COMPANIA = UN_COMPANIA
    )
    LOOP
    FEI := to_char(to_date(UN_FECHAINICIAL, 'DD/MM/YYYY'), 'YYYY-MM-DD');
    FEF := to_char(to_date(UN_FECHAFINAL, 'DD/MM/YYYY'), 'YYYY-MM-DD');
    MI_NIT := RS.NIT;
    FEFI :=  rEplacE(SUBSTR(FEF,0), '-',''); 
    MI_NIT := lpad(rEplacE(SUBSTR(MI_NIT,0,insTr(MI_NIT,'-')-1), '.',''),17,'0'); 

    END LOOP;

    FOR RS IN(
        SELECT COUNT(ID_DE_EMPLEADO) TOTAL_EMPLEADOS
        FROM PLANOS_NOVEDADES_PENSIONADOS
        WHERE PLANOS_NOVEDADES_PENSIONADOS.COMPANIA = UN_COMPANIA 
        AND PLANOS_NOVEDADES_PENSIONADOS.ID_DE_EMPLEADO<>'0'
        AND PLANOS_NOVEDADES_PENSIONADOS.FECHA_INICIAL Between UN_FECHAINICIAL And UN_FECHAFINAL
        AND PLANOS_NOVEDADES_PENSIONADOS.FECHA_FINAL Between UN_FECHAINICIAL And UN_FECHAFINAL
    )
    LOOP
    TOTAL_EMPLEADOS := lpad(RS.TOTAL_EMPLEADOS,8,'0');
    TOTAL_REGISTROS := RS.TOTAL_EMPLEADOS;
    MI_CADENA := '1' || ',' || MI_NIT  || ',' || FEI ||  ',' ||  FEF || ',' || TOTAL_EMPLEADOS || ',' || 'RUA' || '250' || 'NNPE' ||  FEFI ||  'NI' || MI_NIT || '.TXT' || CHR(10);

    END LOOP;


        FOR RS IN(
        SELECT   COMPANIA, ID_DE_EMPLEADO, IDENTIFICACION,FECHA_INICIAL, FECHA_FINAL, NOVEDAD,C1, C2,C3,C4,C5,C6,C7,C8,C9,TO_CHAR(C10, 'YYYY-MM-DD') C10,C11,C12,C13,C14,C15,
        C16, C17, C18, C19,C20,C21,C22,C23,C24,C25,C26,C27,C28,C29,TO_CHAR(C30, 'YYYY-MM-DD') C30,TO_CHAR(C31, 'YYYY-MM-DD') C31,TO_CHAR(C32, 'YYYY-MM-DD') C32,C33,C34,C35,C36,C37,C38,C39,C40,C41,C42,C43,C44,C45,TO_CHAR(C46, 'YYYY-MM-DD') C46,C47,TO_CHAR(C48, 'YYYY-MM-DD') C48,TO_CHAR(C56, 'YYYY-MM-DD') C56
        FROM PLANOS_NOVEDADES_PENSIONADOS
        WHERE PLANOS_NOVEDADES_PENSIONADOS.FECHA_INICIAL BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
        AND PLANOS_NOVEDADES_PENSIONADOS.FECHA_FINAL BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
        And COMPANIA = UN_COMPANIA
        )
        LOOP
        ID := RS.ID_DE_EMPLEADO;

        C25 := RS.C25;
        C21 := RS.C21;
        C22 := RS.C22;
        C23 := RS.C23;
        C7 := RS.C7;
        C8 := RS.C8;
        C13 := RS.C13;
        C14 := RS.C14;
        C15 := RS.C15;
        C16 := RS.C16;

            IF RS.Novedad = 'N01' THEN

               C9 := RS.C9;
               C10 := RS.C10;
               C11 := RS.C11;
               C12 := RS.C12; 
               C13 := RS.C13;
               C14 := RS.C14;
               C15 := RS.C15;
               C16 := RS.C16;
               C17 := RS.C17;
               C18 := RS.C18;

            END IF;

            IF RS.NOVEDAD = 'N02' THEN

               C7 := RS.C7;
               C8 := RS.C8;
            END IF;
            IF RS.NOVEDAD = 'N03' THEN

               C19 := NVL(RS.C19,'N');
               C20 := NVL(RS.C20,'N');
               C25 := RS.C25;
               C26 := RS.C26;
               C27 := lpad( RS.C29,10,'0');
               C28 := RS.C30;
               C43 := RS.C27;
               C44 := RS.C28;
               C51 := lpad(RS.C44,10,'0');
            END IF;

            IF RS.NOVEDAD = 'N04' THEN

               C29 := RS.C31;
               C32 := lpad(RS.C34,10,'0');
               C33 := to_char(RS.C35,'0.0000');
               C34 := to_char(RS.C36,'0.0000'); 
               C35 := to_char(RS.C37,'0.0000');
               C27 := lpad(RS.C29,10,'0');
               C28 := RS.C30;
               C45 := RS.C38;
               C46 := RS.C39;
               C48 := RS.C41;
               C49 := RS.C42;
               C51 := RS.C44;

            END IF;

            IF RS.NOVEDAD = 'N05' THEN

               C1 := RS.C1;
               C2 := RS.C2;
               C3 := RS.C3;
               C4 := RS.C4;
               C5 := RS.C5;
               C6 := RS.C6;
            END IF;

            IF RS.NOVEDAD = 'N06' THEN

               C30 := RS.C32;
               C31 := RS.C33;
               C27 := lpad(RS.C29,10,'0');
               C28 := RS.C30;
               C51 := lpad(RS.C44,10,'0');
            END IF;

            IF RS.NOVEDAD = 'N07' THEN
                FOR RS IN(
                SELECT TO_CHAR(FECHA_RETIRO_PENSION, 'YYYY-MM-DD') FECHA_RETIRO_PENSION,
                        CAUSA_RETIRO_PENSION,
                        TO_CHAR(Fecha_de_Retiro, 'YYYY-MM-DD') Fecha_de_Retiro
                FROM PERSONAL
                WHERE ID_DE_EMPLEADO = ID
                AND COMPANIA = UN_COMPANIA
                )
                LOOP
                C36 := RS.FECHA_RETIRO_PENSION;
                C37 := RS.CAUSA_RETIRO_PENSION;
                C38 := RS.Fecha_de_Retiro;
                END LOOP;


               C27 := NVL(RS.C29,'0');
               C28 := RS.C30;
               C51 := RS.C44;
             END IF;

             IF RS.NOVEDAD = 'N08' THEN

               C30 := RS.C32;
               C31 := RS.C33;
               C27 := lpad(RS.C29,10,'0');    
               C28 := RS.C30;
               C51 := lpad(RS.C44,10,'0');
             END IF;

             IF RS.NOVEDAD = 'N09' THEN
               C1 := RS.C1;
               C2 := RS.C2;
               C3 := RS.C3;
               C4 := RS.C4;
               C5 := RS.C5;
               C6 := RS.C6;
               C51 := lpad(RS.C44,10,'0');
               C55 := lpad(RS.C48,10,'0');
               
              END IF;
              
              cadena2 := '';
              
              IF CONT != TOTAL_EMPLEADOS THEN
              cadena2 := chr(10);

              END IF;
              
        IF RS.NOVEDAD = 'N01' THEN
        MI_CADENA :=  MI_CADENA || '2' || ',' || C21 || ',' || C22 || ',' || '' || ',' || C7 || ',' || C8 || ',' || C13 || ',' || C14 || ',' || C15 || ',' || C16 || RS.Novedad || ',' || ',' || C9 || ',' || C10 || ',' || C11 || ',' || C12 || ',' || C13 || ',' || C14 || ',' || C15 || ',' || C16 || ',' || C17 || ',' || C18 || ',,,,,,' || cadena2;
        END IF;
        IF RS.NOVEDAD = 'N02' THEN
        MI_CADENA :=  MI_CADENA || '2' || ','  || C21 || ',' || C22 || ',' || '' || ',' || C7 || ',' || C8 || ',' || C13 || ',' || C14 || ',' || C15 || ',' || C16 || ',' || RS.Novedad || ',' || ',' || C7 || ',' || C8 ||  ',,,,,,,,,,,,,,' || cadena2;
        END IF;
        IF RS.NOVEDAD = 'N03' THEN
        MI_CADENA :=  MI_CADENA || '2' || ','  || C21 || ',' || C22 || ',' || '' || ',' || C7 || ',' || C8 || ',' || C13 || ',' || C14 || ',' || C15 || ',' || C16 || ',' || RS.Novedad || ',' || ',' || C19 || ',' || C20 || ',' || C25 || ',' || C26 || ',' || C27 || ',' || C28 || ',' || C43 || ',' || C44 || ',' || C51 || ',,,,,,,' || cadena2;
        END IF;
        IF RS.NOVEDAD = 'N04' THEN
        MI_CADENA :=  MI_CADENA || '2' || ','  || C21 || ',' || C22 || ',' || '' || ',' || C7 || ',' || C8 || ',' || C13 || ',' || C14 || ',' || C15 || ',' || C16 || ',' || RS.Novedad || ',' || ',' || C29 || ',' || C32 || ',' || C33 || ',' || C34 || ',' || C35 || ',' || C27 || ',' || C28 || ',' || C45 || ',' || C46 || ',' || C48 || ',' || C49 || ',' || C51 || ',,,,' || cadena2;
        END IF;
        IF RS.NOVEDAD = 'N05' THEN
          MI_CADENA :=  MI_CADENA || '2' || ','  || C21 || ',' || C22 || ',' || '' || ',' || C7 || ',' || C8 || ',' || C13 || ',' || C14 || ',' || C15 || ',' || C16 || ',' || RS.Novedad || ',' || ',' || C1 || ',' || C2 || ',' || C3 || ',' || C4 || ',' || C5 || ',' || C6 || ',,,,,,,,,,' || cadena2;
        END IF;
        IF RS.NOVEDAD = 'N06' THEN
         MI_CADENA :=  MI_CADENA || '2' || ','  || C21 || ',' || C22 || ',' || '' || ',' || C7 || ',' || C8 || ',' || C13 || ',' || C14 || ',' || C15 || ',' || C16 || ',' || RS.Novedad || ',' || ',' || C30 || ',' || C31 || ',' || C27 || ',' || C28 || ',' || C51 ||  ',,,,,,,,,,,' || cadena2;
        END IF;
        IF RS.NOVEDAD = 'N07' THEN
         MI_CADENA :=  MI_CADENA || '2' || ','  || C21 || ',' || C22 || ',' || '' || ',' || C7 || ',' || C8 || ',' || C13 || ',' || C14 || ',' || C15 || ',' || C16 || ',' || RS.Novedad || ',' || ',' || C36 || ',' || C37 || ',' || C38 || ',' || C27 || ',' || C28 || ',' || C51 || ',,,,,,,,,' || cadena2;
        END IF;
        IF RS.NOVEDAD = 'N08' THEN
        MI_CADENA :=  MI_CADENA || '2' || ','  || C21 || ',' || C22 || ',' || '' || ',' || C7 || ',' || C8 || ',' || C13 || ',' || C14 || ',' || C15 || ',' || C16 || ',' || RS.Novedad || ',' || ',' || C30 || ',' || C31 || ',' || C27 || ',' || C28 || ',' || C51 ||  ',,,,,,,,,,,' || cadena2;
        END IF;
        IF RS.NOVEDAD = 'N09' THEN 
        MI_CADENA :=  MI_CADENA || '2' || ','  || C21 || ',' || C22 || ',' || '' || ',' || C7 || ',' || C8 || ',' || C13 || ',' || C14 || ',' || C15 || ',' || C16 || ',' || RS.Novedad || ',' || ',' || C1 || ',' || C2 || ',' || C3 || ',' || C4 || ',' || C5 || ',' || C6 || ',' || C51 || ',' || C55 ||  ',,,,,,,,' || cadena2;
        END IF;

        CONT := CONT + 1;

        END LOOP;
    RETURN MI_CADENA;    

END FC_GENERARPLANOSPENSIONADOS;
--FIN_7719434(07/12/2022 CARENAS)

PROCEDURE PR_CALCULARPRIMADENAVIDADSTR(
/*
    NAME              : PR_CALCULARPRIMADENAVIDADSTR
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO
    DATE MIGRADOR     : 08/05/2025
    DESCRIPTION       : PROCEDIMIENTO PARA CALCULAR PRIMA DE NAVIDAD ESTANDAR
*/
UN_COMPANIA    IN  PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_FECHAFPN         DATE;
    MI_TRANSPORTELEGAL  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_RETEFUENTE       PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
	MI_VALMAYORPV       PCK_SUBTIPOS.TI_PARAMETRO;
BEGIN
	MI_VALMAYORPV := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA,
                                               UN_NOMBRE => 'TOMA MAYOR PV EN PN EN RETIRO',
                                               UN_MODULO => PCK_DATOS.FC_MODULONOMINA,
                                               UN_FECHA_PAR => SYSDATE),'NO');
											   
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
			--CC_3672(13/03/2026 JCROJAS): Validacion para que sume la prima de antiguedad solo cuando la entidad si la maneje																												  
            PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.FC_CN(942) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + CASE WHEN PCK_PARST.FC_PAR('CALCULAR PRIMA DE ANTIGUEDAD','NO') = 'SI' THEN PCK_NOMINA.GL_VPA ELSE 0 END + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
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
            --CC_1588(04/06/2025 JCROJAS)
            IF MI_VALMAYORPV = 'SI' THEN
                IF (PCK_NOMINA.FC_CN(155)) > (PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) THEN
                    PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(155))/12,0);
                ELSE
                    PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)/12), 0);
                END IF;
            ELSE
                PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12), 0);
            END IF;
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
			--CC_3672(13/03/2026 JCROJAS): Validacion para que sume la prima de antiguedad solo cuando la entidad si la maneje																												  
            PCK_NOMINA.GL_FACTORPN := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.FC_CN(942) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + CASE WHEN PCK_PARST.FC_PAR('CALCULAR PRIMA DE ANTIGUEDAD','NO') = 'SI' THEN PCK_NOMINA.GL_VPA ELSE 0 END + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(931), 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(932), 0) + PCK_NOMINA.FC_CN(939), 0);
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
	--CC_3672(13/03/2026 JCROJAS): Validacion para que tome la prima de antiguedad solo cuando la maneje																									
    PCK_NOMINA.CN(943) := CASE WHEN PCK_PARST.FC_PAR('CALCULAR PRIMA DE ANTIGUEDAD','NO') = 'SI' THEN PCK_NOMINA.GL_VPA ELSE 0 END;
    PCK_NOMINA.CN(988) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPN), 0)                            ;
    PCK_NOMINA.GL_PN_DIAS := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.GL_PN_BASE := PCK_NOMINA.GL_FACTORPN;
END PR_CALCULARPRIMADENAVIDADSTR;

PROCEDURE PR_CALCULARPRIMASEMESTRALSTR(
/*
NAME              : PR_CALCULARPRIMASEMESTRALSTR
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO
DATE MIGRADOR     : 09/05/2025
DESCRIPTION       : PROCEDIMIENTO PARA CALCULAR PRIMA SEMESTRAL ESTANDAR EN BASE A PR_CALCPRIMASEMESTRAL_CORTO
*/
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_DOCEAVASMINIMASPS         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PSPAGADA                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PRIMAJUNIO                PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_INDRETIR                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_MSG                       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALOR                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
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
		IF (PCK_NOMINA.FC_CN(150)+PCK_NOMINA.FC_CN(514)) > (PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) THEN
            PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150)+PCK_NOMINA.FC_CN(514))/12, 0);
        ELSE
            PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO))/12, 0);
        END IF;
    END IF;
    IF PCK_NOMINA.FC_CN(946) = 0 AND PCK_NOMINA.FC_CN(514) <> 0 THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(514) / 12, 0);
    END IF;
	--CC_3672(13/03/2026 JCROJAS): Validacion para que sume la prima de antiguedad solo cuando la entidad si la maneje																												  
    PCK_NOMINA.GL_FACTORPS := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT_C + CASE WHEN PCK_PARST.FC_PAR('CALCULAR PRIMA DE ANTIGUEDAD','NO') = 'SI' THEN PCK_NOMINA.GL_VPA ELSE 0 END + PCK_NOMINA.GL_GRPNGV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(946), 0);
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
                -- 'REVISAR PCK_NOMINA.GL_DOCEAVAS CAUSADAS, YA QUE NO CUMPLIO MÃ¿S DE 6 MESES RADICADO 201520160102642 01/06/15  A: ' and PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 and ' ' and PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 and ' ' and PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES
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
END PR_CALCULARPRIMASEMESTRALSTR;

PROCEDURE PR_CALCULARPRIMANAVIDCANAL13

/*
  NAME              : PR_CALCULARPRIMANAVIDCANAL13
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
  DATE MIGRADOR     : 20/05/2025
  TIME              :
  SOURCE MODULE     : NOMINA
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       : Migración procedimiento PR_CALCULARPRIMANAVIDCANAL13 CS
  @NAME:  PR_CALCULARPRIMANAVIDCANAL13
*/
AS

  MI_N2         PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_N1         PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_FECHAFPN       DATE;
  MI_ANOS       PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_MESCOM       PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_FECHAIPN        DATE;
  MI_FECHAIPN1       DATE;
  MI_FACTORPN        NUMBER:=0;
  MI_DNT            NUMBER:= 0;
  MI_DNT1            NUMBER:= 0;
  MI_DCC        NUMBER:=0;
  MI_PRIMADIC       NUMBER:=0;
  MI_DIASPRIMADIC       NUMBER:=0;
  MI_TRANSPORTELEGAL        NUMBER:=0;
  MI_RETEFUENTE       NUMBER:=0;

BEGIN

  PCK_NOMINA.GL_PVAC := 0;
  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '11' THEN
    MI_FACTORPN := 0 ;
    MI_DNT := 0 ;
    MI_DNT1 := 0 ;
    MI_FECHAIPN := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY') ;
    MI_FECHAIPN := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAI > MI_FECHAIPN, PCK_NOMINA.GL_FECHAI, MI_FECHAIPN) ;
    MI_FECHAIPN1 := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY');
    MI_FECHAIPN1 := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAI > MI_FECHAIPN1, PCK_NOMINA.GL_FECHAI, MI_FECHAIPN1);
    IF PCK_NOMINA.GL_SMES = 12 AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) THEN
      MI_FECHAIPN := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
      MI_FECHAIPN1 := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    END IF;
    IF PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_FECHAI <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,  'DD/MM/YYYY') THEN
      MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');

      PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(MI_FECHAIPN), 1, PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

      PCK_NOMINA.GL_PVAC := 0;
      IF PCK_NOMINA.FC_CNA(155) = 0 THEN
        PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0);
      ELSE
        PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(541);
        PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0);
      END IF;
      PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
      PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
      PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
      MI_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
      MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, MI_FECHAFPN) ;
      PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(MI_FECHAIPN, MI_FECHAFPN);
      FOR I IN 1..PCK_NOMINA.GL_SMES LOOP
      PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, I, 1, PCK_NOMINA.GL_SANO, I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
      IF PCK_NOMINA.FC_CNA(359) > 0 THEN
        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
      END IF;
    END LOOP;
    IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN
      IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) THEN
        MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, PCK_NOMINA.GL_FECHAR) - MI_DNT;
      END IF;
    END IF;
    MI_DNT := PCK_NOMINA.FC_CN(938);
    IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
      MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, MI_FECHAFPN) - MI_DNT;
      IF PCK_NOMINA.FC_CN(158) = 0 THEN
        PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORPN / 360 * MI_DCC, 0);
      END IF;
      IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
      END IF;
    ELSE
      IF PCK_NOMINA.FC_CN(158) = 0 THEN
        PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
      END IF;
    END IF;
  ELSIF PCK_NOMINA.FC_CN(404) <> 0 THEN

    MI_FECHAFPN := PCK_NOMINA.GL_FECHAR;

    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(MI_FECHAIPN), PCK_SYSMAN_UTL.FC_IIF(PCK_SYSMAN_UTL.FC_DIA(MI_FECHAIPN) < 16, 1, 2), PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

    IF PCK_NOMINA.FC_CNA(155) = 0 THEN
      PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0);
    ELSE      
        PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155);
      END IF;
    END IF;
     --CC_4008(11/05/2026 JCROJAS): Se agrega validacion para que cuando no hayan valores en diciembre tome el acumulado del anio
	IF (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) > 0 THEN															   
		PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
	END IF;	
    PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
	--CC_4008(11/05/2026 JCROJAS): Se agrega validacion para que cuando no hayan valores en diciembre tome el acumulado del anio
    IF (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514)) > 0 THEN																						
		IF PCK_NOMINA.FC_CN(150) > 0 THEN
		  PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150)) / 12, 0);
		ELSE
		  PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0);
		END IF;
	END IF;	
    MI_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
    MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, MI_FECHAFPN) - MI_DNT;
    IF PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAFPN) < PCK_NOMINA.GL_SANO AND PCK_NOMINA.GL_SMES <= 1 THEN
      PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(MI_FECHAIPN, MI_FECHAFPN);
    FOR I IN 1..PCK_NOMINA.GL_SMES LOOP
    PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, I, 1, PCK_NOMINA.GL_SANO, I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.FC_CNA(359) > 0 THEN
      PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
    END IF;
  END LOOP;
  PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_DOCEAVAS < 0, 0, PCK_NOMINA.GL_DOCEAVAS) ;
  MI_DNT := PCK_NOMINA.FC_CN(938);
  IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
    MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, MI_FECHAFPN) - MI_DNT;
    IF PCK_NOMINA.FC_CN(158) = 0 THEN
      PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORPN / 360 * MI_DCC, 0);
    END IF;
    IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
      PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
      PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
    END IF;
  ELSE
    IF PCK_NOMINA.FC_CN(158) = 0 THEN
      PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
    END IF;
  END IF;
ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) AND PCK_NOMINA.GL_FECHAI <= TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) THEN

  MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' );

  IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN
    IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) THEN
      MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, PCK_NOMINA.GL_FECHAR) - MI_DNT;
    END IF;
  END IF;
  PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(MI_FECHAIPN), PCK_SYSMAN_UTL.FC_IIF(PCK_SYSMAN_UTL.FC_DIA(MI_FECHAIPN) < 16, 1, 2), PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

  PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0);  
  PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
  PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
  IF PCK_NOMINA.FC_CN(150) > 0 THEN
    PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150)) / 12, 0);
  ELSE
    PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0);
  END IF;
  MI_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_BONESPMADOC + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
  MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, MI_FECHAFPN) - MI_DNT;
  PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(MI_FECHAIPN, MI_FECHAFPN);
  FOR I IN 1..PCK_NOMINA.GL_SMES LOOP
  PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, I, 1, PCK_NOMINA.GL_SANO, I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
  IF PCK_NOMINA.FC_CNA(359) > 0 THEN
    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
  END IF;
END LOOP;
MI_DNT := PCK_NOMINA.FC_CN(938);
IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
  MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, MI_FECHAFPN) - MI_DNT;
  IF PCK_NOMINA.FC_CN(158) = 0 THEN
    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORPN / 360 * MI_DCC, 0);
  END IF;
  IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
  END IF;
ELSE
  IF PCK_NOMINA.FC_CN(158) = 0 THEN
    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
  END IF;
END IF;
ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) THEN
  MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' );
  MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, MI_FECHAFPN) - MI_DNT;
  MI_N1 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, TO_DATE('30/11/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' )) - MI_DNT1;
  IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN
    IF PCK_NOMINA.GL_FECHAR < TO_DATE('30/11/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) THEN
      MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, PCK_NOMINA.GL_FECHAR) - MI_DNT;
      MI_N1 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, PCK_NOMINA.GL_FECHAR) - MI_DNT1;
    END IF;
    IF PCK_NOMINA.GL_FECHAR > TO_DATE('30/11/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) AND PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) THEN
      MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, PCK_NOMINA.GL_FECHAR) - MI_DNT;
    END IF;
  END IF;
  PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(MI_FECHAIPN), PCK_SYSMAN_UTL.FC_IIF(PCK_SYSMAN_UTL.FC_DIA(MI_FECHAIPN) < 16, 1, 2), PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

  PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0);  
  PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
  PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
  IF PCK_NOMINA.FC_CN(150) > 0 THEN
    PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150)) / 12, 0);
  ELSE
    PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0);
  END IF;
  MI_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_BONESPMADOC + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
  PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(MI_FECHAIPN, MI_FECHAFPN);
  FOR I IN 1..PCK_NOMINA.GL_SMES LOOP
  PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, I, 1, PCK_NOMINA.GL_SANO, I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
  IF PCK_NOMINA.FC_CNA(359) > 0 THEN
    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
  END IF;
END LOOP;
MI_DNT := PCK_NOMINA.FC_CN(938);
IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
  MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, MI_FECHAFPN) - MI_DNT;
  IF PCK_NOMINA.FC_CN(158) = 0 THEN
    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORPN / 360 * MI_DCC, 0);
  END IF;
  IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
  END IF;
ELSE
  IF PCK_NOMINA.FC_CN(158) = 0 THEN
    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
  END IF;
END IF;
END IF;
MI_PRIMADIC := PCK_NOMINA.FC_CN(158);
MI_DIASPRIMADIC := PCK_NOMINA.GL_DOCEAVAS ;
MI_TRANSPORTELEGAL := PCK_NOMINA.FC_CN(254);
MI_RETEFUENTE := PCK_NOMINA.FC_CN(125);
IF PCK_NOMINA.GL_SPER = 4 THEN
  FOR I IN 2..599 LOOP
  IF (I <> 125) AND (I < 599) OR (I >= 600 AND I <= 698) AND (PCK_NOMINA.FC_CN(I) > 0 AND PCK_NOMINA.FC_CN(I) < 1) THEN
    PCK_NOMINA.CN(I) := 0;
  END IF;
END LOOP;
END IF;
PCK_NOMINA.CN(125) := MI_RETEFUENTE;
PCK_NOMINA.CN(254) := MI_TRANSPORTELEGAL;
PCK_NOMINA.CN(158) := MI_PRIMADIC;
PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS ;

PCK_NOMINA.CN(930) := PCK_NOMINA.FC_CN(1);
PCK_NOMINA.CN(933) := PCK_NOMINA.GL_AUXT ;
PCK_NOMINA.CN(934) := PCK_NOMINA.GL_AUXA;
PCK_NOMINA.CN(936) := PCK_NOMINA.FC_CN(67) ;
PCK_NOMINA.CN(937) := MI_DCC ;
PCK_NOMINA.CN(938) := MI_DNT ;
PCK_NOMINA.CN(935) := PCK_NOMINA.GL_GRPNGV;

END PR_CALCULARPRIMANAVIDCANAL13;

PROCEDURE PR_CALCULARPRIMAVACACANAL13(
/*
NAME              : PR_CALCULARPRIMAVACACANAL13
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
DATE MIGRADOR     : 20/05/2025
TIME              :
SOURCE MODULE     : NOMINA
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       : Migracion del proceso de calcularprimavacacionesCANAL13 CS
@NAME:  CALCULARPRIMAVACACANAL13
*/
UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA )
  AS
    MI_BONPAGADA NUMBER DEFAULT 0;
    MI_MSG PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11' THEN
      PCK_NOMINA.CN(174)                            := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CN(160) / 12), 0) / 2;
    ELSE
      --DC := 0;
      PCK_NOMINA.GL_DIASVAC           := 0;
      PCK_NOMINA.GL_DIASPENDIENTES    := 0;
      PCK_NOMINA.GL_PENDIENTES        := 0;
      PCK_NOMINA.GL_LICENCIAS         := 0;
      PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
      PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
      PCK_NOMINA.GL_FECHAUV           :=
      (
        CASE
        WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
          PCK_NOMINA.GL_FECHAI
        ELSE
          PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL
        END);
      IF PCK_NOMINA.FC_CN(404)  <> 0 OR PCK_NOMINA.GL_SPER = 8 THEN
        PCK_NOMINA.CN(984)      := 0;
        IF PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) <> 0 THEN    
            PCK_NOMINA.CN(981)      := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.CN(160)) / 12, 0);
            IF PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) <> 0 AND PCK_NOMINA.CN(160) <> 0 AND PCK_NOMINA.CN(404) <> 0THEN
                IF PCK_NOMINA.CNP(160) = 0 AND PCK_NOMINA.CN(160) > 0 THEN
                    PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.CNP(160))/12, 0);
                ELSE 
                    IF PCK_NOMINA.CNP(160) > 0 AND PCK_NOMINA.CN(160) > 0 THEN
                        PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.CNP(160) + PCK_NOMINA.CN(160))/12, 0);
                    END IF;
                END IF;
            END IF;
            IF PCK_NOMINA.CN(981) = 0 THEN
                PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.CN(160)) / 12, 0);
            END IF;
        ELSE
            PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160)) / 12, 0);
        END IF;
        IF PCK_NOMINA.FC_CN(150) > 0 THEN
          --(APINEDA:15/11/2018)-Se suma CN514 a la bonificacion por servicios prestados de acuerdo a revisiÃ³n de rmedina en ISDN.
         -- MI_BONPAGADA          := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0); RM 21/11/2019 POR SOLICITUD DEL IDSN LORENA GUEVARA DEBE TOMAR LA ULTIMA PAGADA
           MI_BONPAGADA          := ROUND((PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(514)) / 12), 0);
        ELSE
          MI_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
        END IF;
        PCK_NOMINA.GL_AC        := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.GL_LICENCIAS := (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.FC_CNA(339)) +
        (
          CASE
          WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
            PCK_NOMINA.FC_CESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 'L')
          ELSE
            0
          END) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
        PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91);
        PCK_NOMINA.GL_DTV            := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
        (
          (
            CASE
            WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
              PCK_NOMINA.GL_FECHAI
            ELSE
              (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)
            END), PCK_NOMINA.GL_FECHAFIN1);
        PCK_NOMINA.GL_DIASPROP                                                              := PCK_NOMINA.GL_DTV;
        PCK_NOMINA.GL_PERIODOS                                                              := TRUNC(PCK_NOMINA.GL_DTV/ 360);
        IF (PCK_NOMINA.GL_DTV                                                                                         - (360 * PCK_NOMINA.GL_PERIODOS)) >= 315 THEN
          PCK_NOMINA.GL_PERIODOS                                                            := PCK_NOMINA.GL_PERIODOS + 1;
        END IF;
        PCK_NOMINA.GL_PERIODOS :=
        (
          CASE
          WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN
            1
          ELSE
            PCK_NOMINA.GL_PERIODOS
          END);
        PCK_NOMINA.GL_DIASVAC      := PCK_SYSMAN_UTL.FC_ROUND(15                   * PCK_NOMINA.GL_PERIODOS, 2);
        PCK_NOMINA.CN(93)          := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) * PCK_NOMINA.GL_PERIODOS, 2);
        PCK_NOMINA.GL_AC           := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.GL_DTV        = 0 THEN
          PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
        ELSE
          PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
        END IF;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
          PCK_NOMINA.CN(155)                            :=
          (
            CASE
            WHEN PCK_NOMINA.FC_CN(155) = 0 THEN
              PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA) / 30 * ROUND((PCK_NOMINA.GL_DTV*15)/360,3), 0) --CC_3908(23/04/2026 JCROJAS): Se ajusta calculo de los dias
            ELSE
              PCK_NOMINA.FC_CN(155)
            END);
        ELSE
          PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
          PCK_NOMINA.CN(155)       :=
          (
            CASE
            WHEN PCK_NOMINA.FC_CN(155) = 0 THEN
              PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0) / 30 * ROUND((PCK_NOMINA.GL_DTV*15)/360,3), 0) --CC_3908(23/04/2026 JCROJAS): Se ajusta calculo de los dias
            ELSE
              PCK_NOMINA.FC_CN(155)
            END);
        END IF;
        IF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '01' THEN
          PCK_NOMINA.GL_DTV     := PCK_NOMINA.GL_DTV / 30 * 15 / 360;
          /*IF PCK_NOMINA.GL_SPRC = 99 THEN
          RTA := 7;
          END IF;
          IF RTA = 0 OR ISNULL(RTA) THEN
          IF PAR('CONTAR SABADOS COMO DIA HABIL') = 'NO' THEN
          RTA := 7;
          ELSE
          RTA := MSGBOX('SE ESTÃ� CALCULANDO VACACIONES A UN RETIRADO, DESEA CONTARLE LOS SÃ�BADOS COMO DÃ�A HÃ�BIL PARA VACACIONES AL EMPLEADO ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES, VBYESNO, 'SYSMAN SOFTWARE');
          END IF;
          END IF;*/
          PCK_NOMINA.GL_FECHAFF := NULL;
          PCK_NOMINA.GL_DTV     := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
          (
            (
              CASE
              WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                PCK_NOMINA.GL_FECHAI
              ELSE
                (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)
              END), PCK_NOMINA.GL_FECHAFIN1);
          PCK_NOMINA.GL_DIASVAC    := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_DTV * 15 / 360, 0);
          PCK_NOMINA.GL_FECHAFF    := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
          IF PCK_NOMINA.GL_SPER     = 8 AND PCK_NOMINA.GL_SMES = 12 THEN
            PCK_NOMINA.GL_PERIODOS := 1;
            PCK_NOMINA.GL_DTV      := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
            (
              (
                CASE
                WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                  PCK_NOMINA.GL_FECHAI
                ELSE
                  (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)
                END), PCK_NOMINA.GL_FECHAFIN1);
            PCK_NOMINA.CN(93)     := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) * PCK_NOMINA.GL_PERIODOS, 2);
            PCK_NOMINA.GL_DIASVAC :=
            (
              CASE
              WHEN PCK_NOMINA.GL_DIASVAC = 0 THEN
                PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2)
              ELSE
                PCK_NOMINA.GL_DIASVAC
              END);
            PCK_NOMINA.GL_DIASVAC := TRUNC(PCK_NOMINA.GL_DIASVAC                                           / 360 * PCK_NOMINA.GL_DTV);
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.GL_FECHAFIN + 1, PCK_NOMINA.GL_DIASVAC);
          END IF;
          IF PCK_NOMINA.FC_CN(96) = 0 THEN
            /*IF RTA = 6 THEN
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.CN(96) := (TO_DATE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY') - PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC)) + 1;
            ELSE*/
            PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.GL_FECHAFF  := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
              (
                (
                  CASE
                  WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                    PCK_NOMINA.GL_FECHAI
                  ELSE
                    (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)
                  END), PCK_NOMINA.GL_FECHAFIN1) > 1 THEN
              PCK_NOMINA.CN(96)                 := (PCK_NOMINA.GL_FECHAFF1 - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
            END IF;
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
              PCK_NOMINA.CN(96)                                   := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
            END IF;
            --END IF;
          END IF;
          IF PCK_NOMINA.FC_CN(96)  = 0 THEN
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.CN(96)     := (PCK_NOMINA.GL_FECHAFF - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY'));
          END IF;
          PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2);
          PCK_NOMINA.CN(164)    := PCK_NOMINA.GL_PERIODOS;
          PCK_NOMINA.CN(175)    := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0);
          IF PCK_NOMINA.GL_SPRC  = 99 THEN
            PCK_NOMINA.CN(175)  := PCK_NOMINA.FC_CN(175);
          ELSE
            PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.GL_PENDIENTES;
          END IF;
          IF PCK_NOMINA.GL_SPER                              = 8 AND PCK_NOMINA.GL_SMES = 12 THEN
            PCK_NOMINA.GL_FACTORESPV                        := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12, 0);
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
              PCK_NOMINA.CN(155)                            := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538)) / 12) * PCK_NOMINA.FC_CN(93) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
            ELSE
              PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1)                         + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(543)) / 12, 0);
              PCK_NOMINA.CN(155)       := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(543)) / 12, 0) * PCK_NOMINA.FC_CN(93) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
            END IF;
            PCK_NOMINA.CN(964)             := PCK_NOMINA.GL_DIASPENDIENTES;
            PCK_NOMINA.GL_DIASPROPORCIONAL := PCK_NOMINA.GL_DTV;
          END IF;
        ELSIF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
          PCK_NOMINA.GL_DTV        := PCK_NOMINA.GL_DTV / 30 * 15 / 360;
          --RTA := MSGBOX('SE ESTÃ� CALCULANDO VACACIONES A UN RETIRADO, DESEA CONTARLE LOS SÃ�BADOS COMO DÃ�A HÃ�BIL PARA VACACIONES AL EMPLEADO ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES, VBYESNO, 'SYSMAN SOFTWARE');
          PCK_NOMINA.GL_FECHAFF := NULL;
          PCK_NOMINA.GL_DTV     := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
          (
            (
              CASE
              WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                PCK_NOMINA.GL_FECHAI
              ELSE
                (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)
              END), PCK_NOMINA.GL_FECHAFIN1);
          PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_DTV / 360, 0);
          PCK_NOMINA.CN(164)    :=
          (
            CASE
            WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN
              1
            ELSE
              PCK_NOMINA.GL_DIASVAC
            END);
          PCK_NOMINA.GL_FECHAFF  := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
          IF PCK_NOMINA.FC_CN(96) = 0 THEN
            /*IF RTA = 6 THEN
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.CN(96) := (TO_DATE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY') - PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC)) + 1;
            ELSE*/
            PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.GL_FECHAFF  := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.CN(96)      := (PCK_NOMINA.GL_FECHAFF1 - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
			--CC_3908(23/04/2026 JCROJAS): Se agrega suma de los decimales
            PCK_NOMINA.CN(96) := PCK_NOMINA.FC_CN(96) + PCK_SYSMAN_UTL.FC_ROUND((15*PCK_NOMINA.GL_DTV/360)-TRUNC(PCK_NOMINA.GL_DIASVAC),2);																														   
            --END IF;
          END IF;
          IF PCK_NOMINA.FC_CN(96)  = 0 THEN
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.CN(96)     := (PCK_NOMINA.GL_FECHAFF - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
          END IF;
          PCK_NOMINA.CN(175)   := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0);
          IF PCK_NOMINA.GL_SPRC = 99 THEN
            PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175);
          ELSE
            PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.GL_PENDIENTES;
          END IF;
        END IF;
        IF (PCK_NOMINA.GL_SPER = 5 OR PCK_NOMINA.GL_SPER = 7) OR PCK_NOMINA.FC_CN(404) <> 0 THEN
          PCK_NOMINA.CN(68)   := 0;
          PCK_NOMINA.CN(93)   :=
          (
            CASE
            WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN
              1
            ELSE
              PCK_NOMINA.GL_PERIODOS
            END);
          IF PCK_NOMINA.FC_CN(155)    > 1 OR PCK_NOMINA.FC_CN(155) = 0 THEN
            IF PCK_NOMINA.GL_DIASPROP > 315 THEN
              PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.GL_DIASPROP - 360;
              PCK_NOMINA.GL_DIASPROP :=
              (
                CASE
                WHEN PCK_NOMINA.GL_DIASPROP < 0 THEN
                  0
                ELSE
                  PCK_NOMINA.GL_DIASPROP
                END);
            END IF;
            IF PCK_NOMINA.GL_DIASPROP                         >= 0 AND ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06') AND PCK_NOMINA.FC_CN(155) = 0) THEN
              PCK_NOMINA.CN(68)                               := 15;
              IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.CN(155)                            := PCK_NOMINA.FC_CN(155) + PCK_SYSMAN_UTL.FC_ROUND
                (
                  (
                    (
                      CASE
                      WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
                        PCK_NOMINA.FC_CN(10)
                      ELSE
                        PCK_NOMINA.FC_CN(1)
                      END) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_GRPNGV + MI_BONPAGADA + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160)) / 12) * PCK_NOMINA.FC_CN(68) / 30 / 360 * PCK_NOMINA.GL_DIASPROP, 0);
              END IF;
            END IF;
          END IF;
          IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'PV' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'LN' THEN
            PCK_NOMINA.GL_DTV                                    := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
            (
              (
                CASE
                WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                  PCK_NOMINA.GL_FECHAI
                ELSE
                  (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)
                END), PCK_NOMINA.GL_FECHAFIN1);
            PCK_NOMINA.GL_DTV :=
            (
              CASE
              WHEN PCK_NOMINA.GL_DTV > 330 THEN
                PCK_NOMINA.GL_DTV
              ELSE
                PCK_NOMINA.GL_DTV
              END);
            PCK_NOMINA.GL_DTV :=
            (
              CASE
              WHEN PCK_NOMINA.GL_DTV > 315 AND PCK_NOMINA.GL_DTV < 360 THEN
                360
              ELSE
                PCK_NOMINA.GL_DTV
              END);
            PCK_NOMINA.GL_DIASVAC := TRUNC(15 * PCK_NOMINA.GL_DTV / 360) + PCK_NOMINA.GL_DIASPENDIENTES;
            PCK_NOMINA.CN(164)    :=
            (
              CASE
              WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN
                1
              ELSE
                PCK_NOMINA.GL_DIASVAC
              END);
            PCK_NOMINA.GL_FECHAFF  := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.CN(96)      := 0;
            IF PCK_NOMINA.FC_CN(96) = 0 THEN
              /*IF RTA = 6 THEN
              PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
              PCK_NOMINA.CN(96) := (TO_DATE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY') - PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC)) + 1;
              ELSE*/
              PCK_NOMINA.GL_FECHAFF1                                                      := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
              PCK_NOMINA.GL_FECHAFF                                                       := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
              PCK_NOMINA.CN(96)                                                           := (PCK_NOMINA.GL_FECHAFF1 - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
              IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO) < 2 AND PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFF1) > 2 THEN
                IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO                      < TO_DATE(LAST_DAY(TO_DATE('01/02/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')) || '/02/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                  PCK_NOMINA.CN(96)                                                       := PCK_NOMINA.FC_CN(96) + (30 - TO_NUMBER(LAST_DAY(TO_DATE('01/02/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'))));
                END IF;
              END IF;
              --END IF;
            END IF;
            PCK_NOMINA.CN(96) :=
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(96) = 1 THEN
                0
              ELSE
                PCK_NOMINA.FC_CN(96)
              END);
            IF PCK_NOMINA.FC_CN(96)  = 0 THEN
              PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
              PCK_NOMINA.CN(96)     := (PCK_NOMINA.GL_FECHAFF - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
              PCK_NOMINA.CN(96)     :=
              (
                CASE
                WHEN PCK_NOMINA.FC_CN(96) = 1 THEN
                  0
                ELSE
                  PCK_NOMINA.FC_CN(96)
                END);
            END IF;
            PCK_NOMINA.CN(175)   := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)) - PCK_NOMINA.GL_DIASVAC, 2), 0);
            PCK_NOMINA.CN(96)    := PCK_NOMINA.FC_CN(96)                             + PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)) - PCK_NOMINA.GL_DIASVAC, 2);
            IF PCK_NOMINA.GL_SPRC = 99 AND (PCK_NOMINA.FC_CN(175) = 0 OR PCK_NOMINA.FC_CN(175)= 0) AND PCK_NOMINA.FC_CN(155) > 0 THEN
              PCK_NOMINA.CN(96)  :=
              (
                CASE
                WHEN PCK_NOMINA.FC_CN(96) = 0 THEN
                  21
                ELSE
                  PCK_NOMINA.FC_CN(96)
                END);
              PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
            END IF;
          END IF;
        END IF;
        PCK_NOMINA.CN(982)                               := MI_BONPAGADA;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CARGO = '00007' THEN
          PCK_NOMINA.CN(155)                             := 0;
        END IF;
      ELSE
        PCK_NOMINA.CN(981)             := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
        PCK_NOMINA.CN(984)             := 0;
        PCK_NOMINA.GL_AC               := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.GL_DIASPENDIENTES   := PCK_NOMINA.FC_CNA(91);
        IF PCK_NOMINA.GL_DIASPENDIENTES > 0 THEN
          --El empleado --EMPLEADO--, tiene --DIAS-- dÃ­as pendientes de vacaciones
          MI_MSG(1).CLAVE := 'EMPLEADO';
          MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
          MI_MSG(2).CLAVE := 'DIAS';
          MI_MSG(2).VALOR := PCK_NOMINA.GL_DIASPENDIENTES;
          PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA => PCK_NOMINA.GL_COMPANIA ,UN_MENSAJE_COD => PCK_ERRORES.ALER_DIASPENDIENTESVACACIONES ,UN_REEMPLAZOS => MI_MSG ,UN_PROCESO => PCK_NOMINA.GL_PROCESOACTUAL ,UN_ANO => PCK_NOMINA.GL_ANOACTUAL ,UN_MES => PCK_NOMINA.GL_MESACTUAL ,UN_PERIODO => PCK_NOMINA.GL_PERIODOACTUAL ,UN_USER => PCK_CONEXION.FC_GETUSER );
        END IF;
        PCK_NOMINA.CN(93)          := PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164);
        MI_BONPAGADA               := 0;
        IF PCK_NOMINA.GL_SPER       = 2 OR PCK_NOMINA.GL_SPER = 12 THEN
          PCK_NOMINA.GL_AC         := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
          IF PCK_NOMINA.FC_CNA(150) > 0 THEN
            MI_BONPAGADA           := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA,(PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
          ELSE
            MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
          END IF;
        ELSE
          MI_BONPAGADA :=
          (
            CASE
            WHEN PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) = 0 THEN
              PCK_NOMINA.FC_CN(150)
            ELSE
              PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)
            END);
          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        END IF;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
          PCK_NOMINA.GL_FACTORESPV                      := PCK_SYSMAN_UTL.FC_ROUND
          (
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
                PCK_NOMINA.FC_CN(10)
              ELSE
                PCK_NOMINA.FC_CN(1)
              END) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CNA(160) / 12 + ((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12), 0), 0);
        ELSE
          PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND
          (
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
                PCK_NOMINA.FC_CN(10)
              ELSE
                PCK_NOMINA.FC_CN(1)
              END) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA / 12, 0), 0);
          PCK_NOMINA.GL_FACTORESPV1 := PCK_SYSMAN_UTL.FC_ROUND
          (
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
                PCK_NOMINA.FC_CN(10)
              ELSE
                PCK_NOMINA.FC_CN(1)
              END) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + (MI_BONPAGADA) / 12, 0), 0);
        END IF;
        PCK_NOMINA.CN(982)                               := PCK_SYSMAN_UTL.FC_ROUND((MI_BONPAGADA) / 12, 0);
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' THEN
          IF PCK_NOMINA.FC_CN(403)                       <> 0 THEN
            PCK_NOMINA.CN(174)                           :=
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(174) = 0 THEN
                PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0)
              ELSE
                PCK_NOMINA.FC_CN(174)
              END);
          END IF;
          IF PCK_NOMINA.FC_CN(419) <> 0 THEN
            PCK_NOMINA.CN(175)     :=
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(175) = 0 THEN
                PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0)
              ELSE
                PCK_NOMINA.FC_CN(175)
              END);
          END IF;
          PCK_NOMINA.CN(155) :=
          (
            CASE
            WHEN PCK_NOMINA.FC_CN(155) = 0 THEN
              PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164), 0)
            ELSE
              PCK_NOMINA.FC_CN(155)
            END);
        ELSE
          IF PCK_NOMINA.FC_CN(403) <> 0 THEN
            PCK_NOMINA.CN(174)     :=
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(174) = 0 THEN
                PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0)
              ELSE
                PCK_NOMINA.FC_CN(174)
              END);
          END IF;
          IF PCK_NOMINA.FC_CN(419) <> 0 THEN
            PCK_NOMINA.CN(175)     :=
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(175) = 0 THEN
                PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0)
              ELSE
                PCK_NOMINA.FC_CN(175)
              END);
          END IF;
        END IF;
      END IF;
    END IF;
    PCK_NOMINA.CN(960)   := PCK_NOMINA.GL_FACTORESPV;
    IF PCK_NOMINA.GL_SPRC = 99 THEN
      PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(93);
      PCK_NOMINA.CN(962) := PCK_NOMINA.GL_PERIODOS;
    ELSE
      PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(94);
      PCK_NOMINA.CN(962) := PCK_NOMINA.FC_CN(164);
    END IF;
    PCK_NOMINA.CN(963) := PCK_NOMINA.GL_LICENCIAS;
    PCK_NOMINA.CN(964) := PCK_NOMINA.GL_DIASPENDIENTES;
    PCK_NOMINA.CN(965) := PCK_NOMINA.GL_PRIMAPROPORCIONAL;
    PCK_NOMINA.CN(966) := PCK_NOMINA.GL_PRIMAPROPORCIONAL;
    PCK_NOMINA.CN(967) := PCK_NOMINA.GL_PENDIENTES;
    PCK_NOMINA.CN(968) := PCK_NOMINA.GL_DIASPROPORCIONAL;
    PCK_NOMINA.CN(975) :=
    (
      CASE
      WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
        PCK_NOMINA.FC_CN(10)
      ELSE
        PCK_NOMINA.FC_CN(1)
      END);
    PCK_NOMINA.CN(976)     := 0;
    PCK_NOMINA.CN(977)     := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(978)     := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(979)     := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(980)     := PCK_NOMINA.GL_VPT;
    PCK_NOMINA.CN(982)     := PCK_SYSMAN_UTL.FC_ROUND((MI_BONPAGADA) / 12, 0);
    PCK_NOMINA.CN(983)     := 0;
    PCK_NOMINA.CN(984)     := PCK_NOMINA.FC_VALORULTIMOQUINQUENIO(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.CN(987)     := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV, 0);
    PCK_NOMINA.GL_PV_BASE  := PCK_NOMINA.GL_FACTORESPV;
    PCK_NOMINA.GL_PV_DIAS  := PCK_NOMINA.GL_DIASPROP;
    PCK_NOMINA.GL_VAC_DIAS := PCK_NOMINA.GL_DTV;
  
END PR_CALCULARPRIMAVACACANAL13;

PROCEDURE PR_CALCULARPRIMASEMESCANAL13(
/*
NAME              : PR_CALCULARPRIMASEMESCANAL13
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
DATE MIGRADOR     : 20/05/2025
DESCRIPTION       : Migracion del procedimiento calcularprimasemestralCANAL13 CS
*/
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_DOCEAVASMINIMASPS         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PSPAGADA                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PRIMAJUNIO                PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_INDRETIR                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_MSG                       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALOR                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN
    MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1'));
    PCK_NOMINA.GL_FACTORPS := 0;
    PCK_NOMINA.GL_FACTORPS1 := 0;
    PCK_NOMINA.GL_DNT := 0;
  IF PCK_NOMINA.GL_SMES <= 7 THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
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
                -- 'REVISAR PCK_NOMINA.GL_DOCEAVAS CAUSADAS, YA QUE NO CUMPLIO MÃ¿S DE 6 MESES RADICADO 201520160102642 01/06/15  A: ' and PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 and ' ' and PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 and ' ' and PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES
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
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
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
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
            MI_VALOR := PCK_NOMINA.FC_CN(160);
            PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.GL_SMES IN (7) OR PCK_NOMINA.GL_SMES  IN (6) AND PCK_NOMINA.FC_CNA(160) > 0 THEN CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END ELSE   CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160) END;
            MI_VALOR := PCK_NOMINA.FC_CN(160);
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
    PCK_NOMINA.CN(67)  := PCK_NOMINA.GL_DOCEAVAS;
    PCK_NOMINA.CN(160) := MI_PRIMAJUNIO;
    PCK_NOMINA.CN(945) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1));
    PCK_NOMINA.CN(947) := PCK_NOMINA.GL_VPT;
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
END PR_CALCULARPRIMASEMESCANAL13;

PROCEDURE PR_CALCULARPRIMANAVIDADASO

/*
  NAME              : PR_CALCULARPRIMANAVIDADASO
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
  DATE MIGRADOR     : 27/06/2025
  TIME              :
  SOURCE MODULE     : NOMINA
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       : Migración procedimiento calcularprimadenavidadASO CS
  @NAME:  PR_CALCULARPRIMANAVIDADASO
*/
AS

  MI_N2         PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_N1         PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_FECHAFPN       DATE;
  MI_ANOS       PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_MESCOM       PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_FECHAIPN        DATE;
  MI_FECHAIPN1       DATE;
  MI_FACTORPN        NUMBER:=0;
  MI_DNT            NUMBER:= 0;
  MI_DNT1            NUMBER:= 0;
  MI_DCC        NUMBER:=0;
  MI_PRIMADIC       NUMBER:=0;
  MI_DIASPRIMADIC       NUMBER:=0;
  MI_TRANSPORTELEGAL        NUMBER:=0;
  MI_RETEFUENTE       NUMBER:=0;

BEGIN

  PCK_NOMINA.GL_PVAC := 0;
  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '11' THEN
    MI_FACTORPN := 0 ;
    MI_DNT := 0 ;
    MI_DNT1 := 0 ;
    MI_FECHAIPN := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY') ;
    MI_FECHAIPN := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAI > MI_FECHAIPN, PCK_NOMINA.GL_FECHAI, MI_FECHAIPN) ;
    MI_FECHAIPN1 := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO , 'DD/MM/YYYY');
    MI_FECHAIPN1 := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAI > MI_FECHAIPN1, PCK_NOMINA.GL_FECHAI, MI_FECHAIPN1);
    IF PCK_NOMINA.GL_SMES = 12 AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) THEN
      MI_FECHAIPN := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
      MI_FECHAIPN1 := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    END IF;
    IF PCK_NOMINA.FC_CN(404) = 0 AND PCK_NOMINA.GL_FECHAI <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,  'DD/MM/YYYY') THEN
      MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');

      PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(MI_FECHAIPN), 1, PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

      PCK_NOMINA.GL_PVAC := 0;
      IF PCK_NOMINA.FC_CNA(155) = 0 THEN

        PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12) / 2, 0);
      ELSE
        IF PCK_NOMINA.FC_CNA(164) > 1 THEN
          PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501)) / PCK_NOMINA.FC_CNA(164), 0);
        ELSE
          PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501);
        END IF;
      END IF;      
      PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
      PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
      PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
      MI_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.CN(939);
      MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, MI_FECHAFPN) ;
      PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(MI_FECHAIPN, MI_FECHAFPN);
      FOR I IN 1..PCK_NOMINA.GL_SMES LOOP
      PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, I, 1, PCK_NOMINA.GL_SANO, I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
      IF PCK_NOMINA.FC_CNA(359) > 0 THEN
        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
      END IF;
    END LOOP;
    IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN
      IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) THEN
        MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, PCK_NOMINA.GL_FECHAR) - MI_DNT;
      END IF;
    END IF;
    MI_DNT := PCK_NOMINA.FC_CN(938);
    IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
      MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, MI_FECHAFPN) - MI_DNT;
      IF PCK_NOMINA.FC_CN(158) = 0 THEN
        PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORPN / 360 * MI_DCC, 0);
      END IF;
      IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
      END IF;
    ELSE
      IF PCK_NOMINA.FC_CN(158) = 0 THEN
        PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
      END IF;
    END IF;
  ELSIF PCK_NOMINA.FC_CN(404) <> 0 THEN

    MI_FECHAFPN := PCK_NOMINA.GL_FECHAR;

    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(MI_FECHAIPN), PCK_SYSMAN_UTL.FC_IIF(PCK_SYSMAN_UTL.FC_DIA(MI_FECHAIPN) < 16, 1, 2), PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

      IF PCK_NOMINA.FC_CNA(164) > 1 THEN--CFBARRERA(CC:2092_INI)
        PCK_NOMINA.GL_PVAC := (PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501)) / PCK_NOMINA.FC_CNA(164);
      ELSE
        PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501);
      END IF;


    PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((GREATEST(NVL(PCK_NOMINA.FC_CN(155), 0), NVL(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0)) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)), 0);
    PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(GREATEST(NVL(PCK_NOMINA.FC_CN(160), 0), NVL(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0)) / 12, 0);
    PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
    PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND(GREATEST(NVL(PCK_NOMINA.FC_CN(150), 0), NVL(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0)) / 12, 0); 
    
    MI_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + ((PCK_NOMINA.CN(931) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503))) + (PCK_NOMINA.GL_PVAC / 12) + ((PCK_NOMINA.CN(939)  + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)));--CFBARRERA(CC:2092)
    MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, MI_FECHAFPN) - MI_DNT;
    IF PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAFPN) < PCK_NOMINA.GL_SANO AND PCK_NOMINA.GL_SMES <= 1 THEN
      PCK_NOMINA.GL_DOCEAVAS := 0;
    END IF;
    PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(MI_FECHAIPN, MI_FECHAFPN);
    FOR I IN 1..PCK_NOMINA.GL_SMES LOOP
    PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, I, 1, PCK_NOMINA.GL_SANO, I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    IF PCK_NOMINA.FC_CNA(359) > 0 THEN
      PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
    END IF;
  END LOOP;
  PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_DOCEAVAS < 0, 0, PCK_NOMINA.GL_DOCEAVAS) ;
  MI_DNT := PCK_NOMINA.FC_CN(938);
  IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
    MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, MI_FECHAFPN) - MI_DNT;
    IF PCK_NOMINA.FC_CN(158) = 0 THEN
      PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORPN / 360 * MI_DCC, 0);
    END IF;
    IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
      PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
      PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
    END IF;
  ELSE
    IF PCK_NOMINA.FC_CN(158) = 0 THEN
      PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
    END IF;
  END IF;
ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) AND PCK_NOMINA.GL_FECHAI <= TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) THEN

  MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' );

  IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN
    IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) THEN
      MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, PCK_NOMINA.GL_FECHAR) - MI_DNT;
    END IF;
  END IF;
  PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(MI_FECHAIPN), PCK_SYSMAN_UTL.FC_IIF(PCK_SYSMAN_UTL.FC_DIA(MI_FECHAIPN) < 16, 1, 2), PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

  IF PCK_NOMINA.FC_CNA(155) = 0 THEN
    PCK_NOMINA.GL_PVAC := 0;
    PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12) / 2, 0);

  ELSE
    IF PCK_NOMINA.FC_CNA(164) > 1 THEN
      PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501)) / PCK_NOMINA.FC_CNA(164), 0);
    ELSE
      PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501);
    END IF;
  END IF;
  MI_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_BONESPMADOC + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12) + (PCK_NOMINA.GL_PVAC / 12) + ((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12);
  PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
  PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
  PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0);
  MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, MI_FECHAFPN) - MI_DNT;
  PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(MI_FECHAIPN, MI_FECHAFPN);
  FOR I IN 1..PCK_NOMINA.GL_SMES LOOP
  PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, I, 1, PCK_NOMINA.GL_SANO, I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
  IF PCK_NOMINA.FC_CNA(359) > 0 THEN
    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
  END IF;
END LOOP;
MI_DNT := PCK_NOMINA.FC_CN(938);
IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
  MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, MI_FECHAFPN) - MI_DNT;
  IF PCK_NOMINA.FC_CN(158) = 0 THEN
    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORPN / 360 * MI_DCC, 0);
  END IF;
  IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
  END IF;
ELSE
  IF PCK_NOMINA.FC_CN(158) = 0 THEN
    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
  END IF;
END IF;
ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) THEN
  MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' );
  MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, MI_FECHAFPN) - MI_DNT;
  MI_N1 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, TO_DATE('30/11/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' )) - MI_DNT1;
  IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN
    IF PCK_NOMINA.GL_FECHAR < TO_DATE('30/11/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) THEN
      MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, PCK_NOMINA.GL_FECHAR) - MI_DNT;
      MI_N1 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, PCK_NOMINA.GL_FECHAR) - MI_DNT1;
    END IF;
    IF PCK_NOMINA.GL_FECHAR > TO_DATE('30/11/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) AND PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY' ) THEN
      MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, PCK_NOMINA.GL_FECHAR) - MI_DNT;
    END IF;
  END IF;
  PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(MI_FECHAIPN), PCK_SYSMAN_UTL.FC_IIF(PCK_SYSMAN_UTL.FC_DIA(MI_FECHAIPN) < 16, 1, 2), PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

  IF PCK_NOMINA.FC_CNA(155) = 0 THEN
    PCK_NOMINA.GL_PVAC := 0;
    PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12) / 2, 0);

  ELSE
    PCK_NOMINA.GL_PVAC := 0;
  END IF;
  MI_FACTORPN := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_BONESPMADOC + ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12) + (PCK_NOMINA.GL_PVAC / 12) + ((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12);
  PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
  PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
  PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514)) / 12, 0);
  PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(MI_FECHAIPN, MI_FECHAFPN);
  FOR I IN 1..PCK_NOMINA.GL_SMES LOOP
  PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, I, 1, PCK_NOMINA.GL_SANO, I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
  IF PCK_NOMINA.FC_CNA(359) > 0 THEN
    PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
  END IF;
END LOOP;
MI_DNT := PCK_NOMINA.FC_CN(938);
IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
  MI_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIPN, MI_FECHAFPN) - MI_DNT;
  IF PCK_NOMINA.FC_CN(158) = 0 THEN
    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORPN / 360 * MI_DCC, 0);
  END IF;
  IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.FC_CN(404) <> 0 THEN
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.CN(158) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CNA(158);
  END IF;
ELSE
  IF PCK_NOMINA.FC_CN(158) = 0 THEN
    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
  END IF;
END IF;
END IF;
END IF;
MI_PRIMADIC := PCK_NOMINA.FC_CN(158);
MI_DIASPRIMADIC := PCK_NOMINA.GL_DOCEAVAS ;
MI_TRANSPORTELEGAL := PCK_NOMINA.FC_CN(254);
MI_RETEFUENTE := PCK_NOMINA.FC_CN(125);
IF PCK_NOMINA.GL_SPER = 4 THEN
  FOR I IN 2..599 LOOP
  IF (I <> 125) AND (I < 599) OR (I >= 600 AND I <= 698) AND (PCK_NOMINA.FC_CN(I) > 0 AND PCK_NOMINA.FC_CN(I) < 1) THEN
    PCK_NOMINA.CN(I) := 0;
  END IF;
END LOOP;
END IF;
PCK_NOMINA.CN(125) := MI_RETEFUENTE;
PCK_NOMINA.CN(254) := MI_TRANSPORTELEGAL;
PCK_NOMINA.CN(158) := MI_PRIMADIC;
PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS ;

PCK_NOMINA.CN(930) := PCK_NOMINA.FC_CN(1);
PCK_NOMINA.CN(933) := PCK_NOMINA.GL_AUXT ;
PCK_NOMINA.CN(934) := PCK_NOMINA.GL_AUXA;
PCK_NOMINA.CN(936) := PCK_NOMINA.FC_CN(67) ;
PCK_NOMINA.CN(937) := MI_DCC ;
PCK_NOMINA.CN(938) := MI_DNT ;
PCK_NOMINA.CN(935) := PCK_NOMINA.GL_GRPNGV;

END PR_CALCULARPRIMANAVIDADASO;

PROCEDURE PR_CALCULARPRIMAVACACIONASO(
/*
NAME              : PR_CALCULARPRIMAVACACIONASO
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
DATE MIGRADOR     : 27/05/2025
TIME              :
SOURCE MODULE     : NOMINA
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       : Migracion del proceso de calcularprimadevacacionesASO CS
@NAME:  CALCULARPRIMAVACACIONASO
*/
UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA )
  AS
    MI_BONPAGADA NUMBER DEFAULT 0;
    MI_MSG PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11' THEN
      PCK_NOMINA.CN(174)                            := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CN(160) / 12), 0) / 2;
    ELSE
      --DC := 0;
      PCK_NOMINA.GL_DIASVAC           := 0;
      PCK_NOMINA.GL_DIASPENDIENTES    := 0;
      PCK_NOMINA.GL_PENDIENTES        := 0;
      PCK_NOMINA.GL_LICENCIAS         := 0;
      PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
      PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
      PCK_NOMINA.GL_FECHAUV           :=
      (
        CASE
        WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
          PCK_NOMINA.GL_FECHAI
        ELSE
          PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL
        END);
      IF PCK_NOMINA.FC_CN(404)  <> 0 OR PCK_NOMINA.GL_SPER = 8 THEN
        PCK_NOMINA.CN(984)      := 0;
        IF PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) <> 0 THEN    
            PCK_NOMINA.CN(981)      := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.CN(160)) / 12, 0);
            IF   PCK_NOMINA.CN(404) <> 0 THEN--CFBARRERA(CC:2092)
                IF PCK_NOMINA.FC_CNP(160) = 0 AND PCK_NOMINA.FC_CN(160) > 0 THEN
                    PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND(GREATEST(NVL(PCK_NOMINA.FC_CN(160), 0), NVL(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0)) / 12, 0);--INI_CFBARRERA(CC:2092)
                ELSE 
                    IF PCK_NOMINA.FC_CNP(160) > 0 AND PCK_NOMINA.FC_CN(160) > 0 THEN
                        PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.CNP(160) + PCK_NOMINA.CN(160))/12, 0);
                    END IF;
                END IF;
            END IF;
            IF PCK_NOMINA.CN(981) = 0 THEN
                PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.CN(160)) / 12, 0);
            END IF;
        ELSE
            PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.CN(160)) / 12, 0);
        END IF;
        IF PCK_NOMINA.FC_CN(150) > 0 THEN
          --(APINEDA:15/11/2018)-Se suma CN514 a la bonificacion por servicios prestados de acuerdo a revisiÃ³n de rmedina en ISDN.
         -- MI_BONPAGADA          := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0); RM 21/11/2019 POR SOLICITUD DEL IDSN LORENA GUEVARA DEBE TOMAR LA ULTIMA PAGADA
            MI_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((GREATEST(NVL(PCK_NOMINA.FC_CN(150), 0), NVL(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0)) + PCK_NOMINA.FC_CN(514)) / 12, 0);--INI_CFBARRERA(CC:2092)
        ELSE
          MI_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
        END IF;
        PCK_NOMINA.GL_AC        := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.GL_LICENCIAS := (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.FC_CNA(339)) +
        (
          CASE
          WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
            PCK_NOMINA.FC_CESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 'L')
          ELSE
            0
          END) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
        PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91);
        PCK_NOMINA.GL_DTV            := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
        (
          (
            CASE
            WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
              PCK_NOMINA.GL_FECHAI
            ELSE
              (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)
            END), PCK_NOMINA.GL_FECHAFIN1);
        PCK_NOMINA.GL_DIASPROP                                                              := PCK_NOMINA.GL_DTV;
        PCK_NOMINA.GL_PERIODOS                                                              := TRUNC(PCK_NOMINA.GL_DTV/ 360);
        IF (PCK_NOMINA.GL_DTV                                                                                         - (360 * PCK_NOMINA.GL_PERIODOS)) >= 315 THEN
          PCK_NOMINA.GL_PERIODOS                                                            := PCK_NOMINA.GL_PERIODOS + 1;
        END IF;
        PCK_NOMINA.GL_PERIODOS :=
        (
          CASE
          WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN
            1
          ELSE
            PCK_NOMINA.GL_PERIODOS
          END);
        PCK_NOMINA.GL_DIASVAC      := PCK_SYSMAN_UTL.FC_ROUND(15                   * PCK_NOMINA.GL_PERIODOS, 2);
        PCK_NOMINA.CN(93)          := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) * PCK_NOMINA.GL_PERIODOS, 2);
        PCK_NOMINA.GL_AC           := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.GL_DTV        = 0 THEN
          PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
        ELSE
          PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
        END IF;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
          PCK_NOMINA.CN(155)                            :=
          (
            CASE
            WHEN PCK_NOMINA.FC_CN(155) = 0 THEN
              PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA) * 15 / 30 / 360 * PCK_NOMINA.GL_DTV, 0)
            ELSE
              PCK_NOMINA.FC_CN(155)
            END);
        ELSE
          PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
          PCK_NOMINA.CN(155)       :=
          (
            CASE
            WHEN PCK_NOMINA.FC_CN(155) = 0 THEN
              PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0) * 15 / 30 / 360 * PCK_NOMINA.GL_DTV, 0)
            ELSE
              PCK_NOMINA.FC_CN(155)
            END);
        END IF;
        IF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '01' THEN
          PCK_NOMINA.GL_DTV     := PCK_NOMINA.GL_DTV / 30 * 15 / 360;          
          PCK_NOMINA.GL_FECHAFF := NULL;
          PCK_NOMINA.GL_DTV     := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
          (
            (
              CASE
              WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                PCK_NOMINA.GL_FECHAI
              ELSE
                (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)
              END), PCK_NOMINA.GL_FECHAFIN1);
          PCK_NOMINA.GL_DIASVAC    := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_DTV * 15 / 360, 0);
          PCK_NOMINA.GL_FECHAFF    := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
          IF PCK_NOMINA.GL_SPER     = 8 AND PCK_NOMINA.GL_SMES = 12 THEN
            PCK_NOMINA.GL_PERIODOS := 1;
            PCK_NOMINA.GL_DTV      := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
            (
              (
                CASE
                WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                  PCK_NOMINA.GL_FECHAI
                ELSE
                  (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)
                END), PCK_NOMINA.GL_FECHAFIN1);
            PCK_NOMINA.CN(93)     := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) * PCK_NOMINA.GL_PERIODOS, 2);
            PCK_NOMINA.GL_DIASVAC :=
            (
              CASE
              WHEN PCK_NOMINA.GL_DIASVAC = 0 THEN
                PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2)
              ELSE
                PCK_NOMINA.GL_DIASVAC
              END);
            PCK_NOMINA.GL_DIASVAC := TRUNC(PCK_NOMINA.GL_DIASVAC                                           / 360 * PCK_NOMINA.GL_DTV);
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.GL_FECHAFIN + 1, PCK_NOMINA.GL_DIASVAC);
          END IF;
          IF PCK_NOMINA.FC_CN(96) = 0 THEN
            /*IF RTA = 6 THEN
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.CN(96) := (TO_DATE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY') - PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC)) + 1;
            ELSE*/
            PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.GL_FECHAFF  := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
              (
                (
                  CASE
                  WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                    PCK_NOMINA.GL_FECHAI
                  ELSE
                    (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)
                  END), PCK_NOMINA.GL_FECHAFIN1) > 1 THEN
              PCK_NOMINA.CN(96)                 := (PCK_NOMINA.GL_FECHAFF1 - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
            END IF;
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
              PCK_NOMINA.CN(96)                                   := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
            END IF;
            --END IF;
          END IF;
          IF PCK_NOMINA.FC_CN(96)  = 0 THEN
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.CN(96)     := (PCK_NOMINA.GL_FECHAFF - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY'));
          END IF;
          PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2);
          PCK_NOMINA.CN(164)    := PCK_NOMINA.GL_PERIODOS;
          PCK_NOMINA.CN(175)    := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0);
          IF PCK_NOMINA.GL_SPRC  = 99 THEN
            PCK_NOMINA.CN(175)  := PCK_NOMINA.FC_CN(175);
          ELSE
            PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.GL_PENDIENTES;
          END IF;
          IF PCK_NOMINA.GL_SPER                              = 8 AND PCK_NOMINA.GL_SMES = 12 THEN
            PCK_NOMINA.GL_FACTORESPV                        := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12, 0);
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
              PCK_NOMINA.CN(155)                            := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538)) / 12) * PCK_NOMINA.FC_CN(93) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
            ELSE
              PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1)                         + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(543)) / 12, 0);
              PCK_NOMINA.CN(155)       := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(543)) / 12, 0) * PCK_NOMINA.FC_CN(93) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
            END IF;
            PCK_NOMINA.CN(964)             := PCK_NOMINA.GL_DIASPENDIENTES;
            PCK_NOMINA.GL_DIASPROPORCIONAL := PCK_NOMINA.GL_DTV;
          END IF;
        ELSIF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
          PCK_NOMINA.GL_DTV        := PCK_NOMINA.GL_DTV / 30 * 15 / 360;
          --RTA := MSGBOX('SE ESTÃ� CALCULANDO VACACIONES A UN RETIRADO, DESEA CONTARLE LOS SÃ�BADOS COMO DÃ�A HÃ�BIL PARA VACACIONES AL EMPLEADO ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES, VBYESNO, 'SYSMAN SOFTWARE');
          PCK_NOMINA.GL_FECHAFF := NULL;
          PCK_NOMINA.GL_DTV     := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
          (
            (
              CASE
              WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                PCK_NOMINA.GL_FECHAI
              ELSE
                (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)
              END), PCK_NOMINA.GL_FECHAFIN1);
          PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_DTV / 360, 0);
          PCK_NOMINA.CN(164)    :=
          (
            CASE
            WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN
              1
            ELSE
              PCK_NOMINA.GL_DIASVAC
            END);
          PCK_NOMINA.GL_FECHAFF  := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
          IF PCK_NOMINA.FC_CN(96) = 0 THEN
            /*IF RTA = 6 THEN
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.CN(96) := (TO_DATE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY') - PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC)) + 1;
            ELSE*/
            PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.GL_FECHAFF  := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.CN(96)      := (PCK_NOMINA.GL_FECHAFF1 - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
            --END IF;
          END IF;
          IF PCK_NOMINA.FC_CN(96)  = 0 THEN
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.CN(96)     := (PCK_NOMINA.GL_FECHAFF - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
          END IF;
          PCK_NOMINA.CN(175)   := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0);
          IF PCK_NOMINA.GL_SPRC = 99 THEN
            PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175);
          ELSE
            PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.GL_PENDIENTES;
          END IF;
        END IF;
        IF (PCK_NOMINA.GL_SPER = 5 OR PCK_NOMINA.GL_SPER = 7) OR PCK_NOMINA.FC_CN(404) <> 0 THEN
          PCK_NOMINA.CN(68)   := 0;
          PCK_NOMINA.CN(93)   :=
          (
            CASE
            WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN
              1
            ELSE
              PCK_NOMINA.GL_PERIODOS
            END);
          IF PCK_NOMINA.FC_CN(155)    > 1 OR PCK_NOMINA.FC_CN(155) = 0 THEN
            IF PCK_NOMINA.GL_DIASPROP > 315 THEN
              PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.GL_DIASPROP - 360;
              PCK_NOMINA.GL_DIASPROP :=
              (
                CASE
                WHEN PCK_NOMINA.GL_DIASPROP < 0 THEN
                  0
                ELSE
                  PCK_NOMINA.GL_DIASPROP
                END);
            END IF;
            IF PCK_NOMINA.GL_DIASPROP                         >= 0 AND ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06') AND PCK_NOMINA.FC_CN(155) = 0) THEN
              PCK_NOMINA.CN(68)                               := 15;
              IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.CN(155)                            := PCK_NOMINA.FC_CN(155) + PCK_SYSMAN_UTL.FC_ROUND
                (
                  (
                    (
                      CASE
                      WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
                        PCK_NOMINA.FC_CN(10)
                      ELSE
                        PCK_NOMINA.FC_CN(1)
                      END) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_GRPNGV + MI_BONPAGADA + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160)) / 12) * PCK_NOMINA.FC_CN(68) / 30 / 360 * PCK_NOMINA.GL_DIASPROP, 0);
              END IF;
            END IF;
          END IF;
          IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'PV' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'LN' THEN
            PCK_NOMINA.GL_DTV                                    := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
            (
              (
                CASE
                WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                  PCK_NOMINA.GL_FECHAI
                ELSE
                  (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)
                END), PCK_NOMINA.GL_FECHAFIN1);
            PCK_NOMINA.GL_DTV :=
            (
              CASE
              WHEN PCK_NOMINA.GL_DTV > 330 THEN
                PCK_NOMINA.GL_DTV
              ELSE
                PCK_NOMINA.GL_DTV
              END);
            PCK_NOMINA.GL_DTV :=
            (
              CASE
              WHEN PCK_NOMINA.GL_DTV > 315 AND PCK_NOMINA.GL_DTV < 360 THEN
                360
              ELSE
                PCK_NOMINA.GL_DTV
              END);
            PCK_NOMINA.GL_DIASVAC := TRUNC(15 * PCK_NOMINA.GL_DTV / 360) + PCK_NOMINA.GL_DIASPENDIENTES;
            PCK_NOMINA.CN(164)    :=
            (
              CASE
              WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN
                1
              ELSE
                PCK_NOMINA.GL_DIASVAC
              END);
            PCK_NOMINA.GL_FECHAFF  := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.CN(96)      := 0;
            IF PCK_NOMINA.FC_CN(96) = 0 THEN
              /*IF RTA = 6 THEN
              PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
              PCK_NOMINA.CN(96) := (TO_DATE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY') - PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC)) + 1;
              ELSE*/
              PCK_NOMINA.GL_FECHAFF1                                                      := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
              PCK_NOMINA.GL_FECHAFF                                                       := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
              PCK_NOMINA.CN(96)                                                           := (PCK_NOMINA.GL_FECHAFF1 - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
              IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO) < 2 AND PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFF1) > 2 THEN
                IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO                      < TO_DATE(LAST_DAY(TO_DATE('01/02/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')) || '/02/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                  PCK_NOMINA.CN(96)                                                       := PCK_NOMINA.FC_CN(96) + (30 - TO_NUMBER(LAST_DAY(TO_DATE('01/02/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'))));
                END IF;
              END IF;
              --END IF;
            END IF;
            PCK_NOMINA.CN(96) :=
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(96) = 1 THEN
                0
              ELSE
                PCK_NOMINA.FC_CN(96)
              END);
            IF PCK_NOMINA.FC_CN(96)  = 0 THEN
              PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
              PCK_NOMINA.CN(96)     := (PCK_NOMINA.GL_FECHAFF - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
              PCK_NOMINA.CN(96)     :=
              (
                CASE
                WHEN PCK_NOMINA.FC_CN(96) = 1 THEN
                  0
                ELSE
                  PCK_NOMINA.FC_CN(96)
                END);
            END IF;
            PCK_NOMINA.CN(175)   := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)) - PCK_NOMINA.GL_DIASVAC, 2), 0);
            PCK_NOMINA.CN(96)    := PCK_NOMINA.FC_CN(96)                             + PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)) - PCK_NOMINA.GL_DIASVAC, 2);
            IF PCK_NOMINA.GL_SPRC = 99 AND (PCK_NOMINA.FC_CN(175) = 0 OR PCK_NOMINA.FC_CN(175)= 0) AND PCK_NOMINA.FC_CN(155) > 0 THEN
              PCK_NOMINA.CN(96)  :=
              (
                CASE
                WHEN PCK_NOMINA.FC_CN(96) = 0 THEN
                  21
                ELSE
                  PCK_NOMINA.FC_CN(96)
                END);
              PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
            END IF;
          END IF;
        END IF;
        PCK_NOMINA.CN(982)                               := MI_BONPAGADA;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CARGO = '00007' THEN
          PCK_NOMINA.CN(155)                             := 0;
        END IF;
      ELSE
        PCK_NOMINA.CN(981)             := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
        PCK_NOMINA.CN(984)             := 0;
        PCK_NOMINA.GL_AC               := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.GL_DIASPENDIENTES   := PCK_NOMINA.FC_CNA(91);
        IF PCK_NOMINA.GL_DIASPENDIENTES > 0 THEN
          --El empleado --EMPLEADO--, tiene --DIAS-- dÃ­as pendientes de vacaciones
          MI_MSG(1).CLAVE := 'EMPLEADO';
          MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
          MI_MSG(2).CLAVE := 'DIAS';
          MI_MSG(2).VALOR := PCK_NOMINA.GL_DIASPENDIENTES;
          PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA => PCK_NOMINA.GL_COMPANIA ,UN_MENSAJE_COD => PCK_ERRORES.ALER_DIASPENDIENTESVACACIONES ,UN_REEMPLAZOS => MI_MSG ,UN_PROCESO => PCK_NOMINA.GL_PROCESOACTUAL ,UN_ANO => PCK_NOMINA.GL_ANOACTUAL ,UN_MES => PCK_NOMINA.GL_MESACTUAL ,UN_PERIODO => PCK_NOMINA.GL_PERIODOACTUAL ,UN_USER => PCK_CONEXION.FC_GETUSER );
        END IF;
        PCK_NOMINA.CN(93)          := PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164);
        MI_BONPAGADA               := 0;
        IF PCK_NOMINA.GL_SPER       = 2 OR PCK_NOMINA.GL_SPER = 12 THEN
          PCK_NOMINA.GL_AC         := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
          IF PCK_NOMINA.FC_CNA(150) > 0 THEN
            MI_BONPAGADA           := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA,(PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
          ELSE
            MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
          END IF;
        ELSE
          MI_BONPAGADA :=
          (
            CASE
            WHEN PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) = 0 THEN
              PCK_NOMINA.FC_CN(150)
            ELSE
              PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)
            END);
          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        END IF;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
          PCK_NOMINA.GL_FACTORESPV                      := PCK_SYSMAN_UTL.FC_ROUND
          (
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
                PCK_NOMINA.FC_CN(10)
              ELSE
                PCK_NOMINA.FC_CN(1)
              END) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CNA(160) / 12 + ((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12), 0), 0);
        ELSE
          PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND
          (
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
                PCK_NOMINA.FC_CN(10)
              ELSE
                PCK_NOMINA.FC_CN(1)
              END) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA / 12, 0), 0);
          PCK_NOMINA.GL_FACTORESPV1 := PCK_SYSMAN_UTL.FC_ROUND
          (
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
                PCK_NOMINA.FC_CN(10)
              ELSE
                PCK_NOMINA.FC_CN(1)
              END) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + (MI_BONPAGADA) / 12, 0), 0);
        END IF;
        PCK_NOMINA.CN(982)                               := PCK_SYSMAN_UTL.FC_ROUND((MI_BONPAGADA) / 12, 0);
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' THEN
          IF PCK_NOMINA.FC_CN(403)                       <> 0 THEN
            PCK_NOMINA.CN(174)                           :=
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(174) = 0 THEN
                PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0)
              ELSE
                PCK_NOMINA.FC_CN(174)
              END);
          END IF;
          IF PCK_NOMINA.FC_CN(419) <> 0 THEN
            PCK_NOMINA.CN(175)     :=
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(175) = 0 THEN
                PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0)
              ELSE
                PCK_NOMINA.FC_CN(175)
              END);
          END IF;
          PCK_NOMINA.CN(155) :=
          (
            CASE
            WHEN PCK_NOMINA.FC_CN(155) = 0 THEN
              PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164), 0)
            ELSE
              PCK_NOMINA.FC_CN(155)
            END);
        ELSE
          IF PCK_NOMINA.FC_CN(403) <> 0 THEN
            PCK_NOMINA.CN(174)     :=
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(174) = 0 THEN
                PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0)
              ELSE
                PCK_NOMINA.FC_CN(174)
              END);
          END IF;
          IF PCK_NOMINA.FC_CN(419) <> 0 THEN
            PCK_NOMINA.CN(175)     :=
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(175) = 0 THEN
                PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0)
              ELSE
                PCK_NOMINA.FC_CN(175)
              END);
          END IF;
        END IF;
      END IF;
    END IF;
    PCK_NOMINA.CN(960)   := PCK_NOMINA.GL_FACTORESPV;
    IF PCK_NOMINA.GL_SPRC = 99 THEN
      PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(93);
      PCK_NOMINA.CN(962) := PCK_NOMINA.GL_PERIODOS;
    ELSE
      PCK_NOMINA.CN(961) := PCK_NOMINA.FC_CN(94);
      PCK_NOMINA.CN(962) := PCK_NOMINA.FC_CN(164);
    END IF;
    PCK_NOMINA.CN(963) := PCK_NOMINA.GL_LICENCIAS;
    PCK_NOMINA.CN(964) := PCK_NOMINA.GL_DIASPENDIENTES;
    PCK_NOMINA.CN(965) := PCK_NOMINA.GL_PRIMAPROPORCIONAL;
    PCK_NOMINA.CN(966) := PCK_NOMINA.GL_PRIMAPROPORCIONAL;
    PCK_NOMINA.CN(967) := PCK_NOMINA.GL_PENDIENTES;
    PCK_NOMINA.CN(968) := PCK_NOMINA.GL_DIASPROPORCIONAL;
    PCK_NOMINA.CN(975) :=
    (
      CASE
      WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
        PCK_NOMINA.FC_CN(10)
      ELSE
        PCK_NOMINA.FC_CN(1)
      END);
    PCK_NOMINA.CN(976)     := 0;
    PCK_NOMINA.CN(977)     := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(978)     := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(979)     := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(980)     := PCK_NOMINA.GL_VPT;
    PCK_NOMINA.CN(982)     := PCK_SYSMAN_UTL.FC_ROUND(MI_BONPAGADA,0);--FIN_CFBARRERA(CC:2092)
    PCK_NOMINA.CN(983)     := 0;
    PCK_NOMINA.CN(984)     := PCK_NOMINA.FC_VALORULTIMOQUINQUENIO(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.CN(987)     := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV, 0);
    PCK_NOMINA.GL_PV_BASE  := PCK_NOMINA.GL_FACTORESPV;
    PCK_NOMINA.GL_PV_DIAS  := PCK_NOMINA.GL_DIASPROP;
    PCK_NOMINA.GL_VAC_DIAS := PCK_NOMINA.GL_DTV;
  
END PR_CALCULARPRIMAVACACIONASO;

PROCEDURE PR_CALCULARCESANTIASASO

/*
  NAME              : PR_CALCULARCESANTIASASO
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
  DATE MIGRADOR     : 26/06/2025
  TIME              : 10:17 AM
  SOURCE MODULE     : NOMINA
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       : Migración procedimiento calcularcesantiasASO CS
  @NAME:  CALCULARCESANTIASASO
*/
AS
  MI_PROMFAC            NUMBER:=0;
  MI_SBM                NUMBER:=0;
  MI_DIASPROMEDIO       NUMBER:=0;
  MI_COMISIONES         NUMBER:=0;
  MI_DIAS         NUMBER:=0;
  MI_MASDIASOTRAENTIDAD NUMBER:=0;
  MI_ANTICIPOS          NUMBER:=0;
  MI_FECHAIC        DATE;
  MI_DIASINT            NUMBER:=0;
  MI_CESANTIA1          NUMBER:=0;
BEGIN
    MI_PROMFAC := 0;
  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO < PCK_NOMINA.GL_FECHAINI THEN
    RETURN;
  END IF;
  IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHACS, PCK_NOMINA.GL_FECHAFIN1) > 90 THEN
    MI_SBM := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(900) = 0, PCK_NOMINA.FC_CN(1), PCK_NOMINA.FC_CN(900));
  ELSE
    MI_SBM := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(900) = 0, PCK_NOMINA.FC_CN(1), PCK_NOMINA.FC_CN(900));
  END IF;
  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN1) THEN
      PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
      PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO);
      PCK_NOMINA.GL_PERA := PCK_SYSMAN_UTL.FC_IIF(PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) < 16, 1, 2);
      MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, PCK_NOMINA.GL_FECHAFIN1, 3,0);
    ELSE
      PCK_NOMINA.GL_ANOA := PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
      PCK_NOMINA.GL_MESA := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
      PCK_NOMINA.GL_PERA := PCK_SYSMAN_UTL.FC_IIF(PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN)) < 16, 1, 2);
      MI_DIASPROMEDIO := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN), PCK_NOMINA.GL_FECHAFIN1, 3,0);
    END IF;

    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES - 1, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    MI_COMISIONES := (PCK_NOMINA.FC_CNA(188) / MI_DIASPROMEDIO) * 30;
    PCK_NOMINA.CN(971) := MI_COMISIONES;
  END IF;
  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '04' THEN
    MI_SBM := MI_SBM + MI_COMISIONES;
  ELSE
    MI_SBM := MI_SBM;
  END IF;
  PCK_NOMINA.GL_BONPAGADA := 0;

  IF PCK_NOMINA.GL_FECHAI < TO_DATE('1996/11/21','YYYY/MM/DD') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR), PCK_SYSMAN_UTL.FC_IIF(PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16, 1, 2), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
    PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
    MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;

    MI_DIAS := MI_DIAS + MI_MASDIASOTRAENTIDAD;

    MI_ANTICIPOS :=   PCK_NOMINA.FC_ACUMCESANTIA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1);
    
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, '12', 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
  ELSE
    MI_FECHAIC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), PCK_NOMINA.GL_FECHAIR, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'));
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(MI_FECHAIC), PCK_SYSMAN_UTL.FC_IIF(PCK_SYSMAN_UTL.FC_DIA(MI_FECHAIC) < 16, 1, 2), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
    PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
    MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
    MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, MI_FECHAIC, PCK_NOMINA.GL_FECHAFIN1 - 10);

  END IF;
  MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAIR > TO_DATE(PCK_NOMINA.GL_SANO || '/01/01', 'YYYY/MM/DD'), PCK_NOMINA.GL_FECHAIR, TO_DATE(PCK_NOMINA.GL_SANO || '/01/01', 'YYYY/MM/DD')), PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
  IF PCK_NOMINA.FC_CNA(155) = 0 THEN

    PCK_NOMINA.GL_PVAC := 0 + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501) ;
  ELSE
    IF PCK_NOMINA.FC_CNA(164) > 1 THEN
      PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)) / PCK_NOMINA.FC_CNA(164), 0) + PCK_NOMINA.FC_CN(155);

    ELSE
      PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501);

    END IF;
  END IF;

  PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0)  ;
  PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
  IF PCK_NOMINA.FC_CNA(150) > 0 THEN
    PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538)) / 12, 0);
  ELSIF PCK_NOMINA.FC_CN(150) > 0 THEN
    PCK_NOMINA.GL_BONPAGADA := PCK_NOMINA.FC_CN(150) ;
  END IF;
  PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
  PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
  PCK_NOMINA.CN(902) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(902) = 0, PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) + PCK_NOMINA.FC_CN(70)) / 12, 0), PCK_NOMINA.FC_CN(902)) ;
  PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) ;
  
   IF PCK_NOMINA.FC_CN(404) <> 0 THEN--INI_CFBARRERA(CC:2092)
    PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((GREATEST(NVL(PCK_NOMINA.FC_CN(160), 0), NVL(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0)) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
    PCK_NOMINA.GL_BONPAGADA :=  PCK_SYSMAN_UTL.FC_ROUND(GREATEST(NVL(PCK_NOMINA.FC_CN(150), 0), NVL(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0)) / 12, 0);
    PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((GREATEST(NVL(PCK_NOMINA.FC_CN(155), 0), NVL(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0))), 0);
    PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
    PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((GREATEST(NVL(PCK_NOMINA.FC_CN(158),0), NVL(PCK_NOMINA_CALCULO.FC_VALORULTIMAPRIMANAVIDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0))) / 12, 0);
  END IF;--FIN_CFBARRERA(CC:2092)
  
  MI_PROMFAC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(969) > 0, PCK_NOMINA.FC_CN(969), PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_AUXT + PCK_NOMINA.FC_CN(906) + PCK_NOMINA.FC_CN(905) + PCK_NOMINA.FC_CN(907) + PCK_NOMINA.FC_CN(902) + PCK_NOMINA.GL_BONPAGADA);
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
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 2 THEN
      PCK_NOMINA.CN(169) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(169) = 0, PCK_SYSMAN_UTL.FC_IIF(MI_CESANTIA1 < 0, 0, PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0)), PCK_NOMINA.FC_CN(169));
    END IF;
    PCK_NOMINA.CN(177) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(177) = 0, MI_CESANTIA1, PCK_NOMINA.FC_CN(177));
  ELSIF PCK_NOMINA.FC_CN(412) <> 0 THEN
    IF PCK_NOMINA.GL_FECHAI > TO_DATE('21/11/1996', 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN <> 1 THEN
      PCK_NOMINA.CN(269) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(269) = 0, PCK_SYSMAN_UTL.FC_IIF(MI_CESANTIA1 < 0, 0, PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0)), PCK_NOMINA.FC_CN(269));
    END IF;
    PCK_NOMINA.CN(277) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(277) = 0, MI_CESANTIA1, PCK_NOMINA.FC_CN(277));
    IF PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = 12 THEN
      PCK_NOMINA.PR_INCLUIRNOVEDAD(PCK_NOMINA.GL_COMPANIA, 1, PCK_NOMINA.GL_SANO + 1, 1,3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 169, PCK_NOMINA.FC_CN(269),NULL,PCK_CONEXION.FC_GETUSER);
    END IF;
  END IF;

  PCK_NOMINA.CN(900) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1));
  PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
  PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;

  PCK_NOMINA.CN(901) := PCK_NOMINA.GL_GRPNGV;

  PCK_NOMINA.CN(908) := PCK_NOMINA.GL_BONPAGADA;
  PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA;
  PCK_NOMINA.CN(910) := MI_DIAS;
  PCK_NOMINA.CN(911) := MI_ANTICIPOS;
  PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
  PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0);
  PCK_NOMINA.CN(914) := PCK_NOMINA.GL_VPT;
  PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1);

  --20062018 CESANTIAS
  PCK_NOMINA.GL_CES_FECHAINI := PCK_NOMINA.GL_FECHAIC;
  PCK_NOMINA.GL_CES_FECHAFIN := PCK_NOMINA.GL_FECHAFIN1;
  PCK_NOMINA.GL_CES_LNR := PCK_NOMINA.GL_LICENCIAS;
  PCK_NOMINA.GL_CES_DIASSINLNR := PCK_NOMINA.GL_DIAS + PCK_NOMINA.GL_LICENCIAS;
  PCK_NOMINA.GL_CES_GR := PCK_NOMINA.GL_GRPNGV;
  PCK_NOMINA.GL_CES_PT := PCK_NOMINA.GL_VPT;
  PCK_NOMINA.GL_CES_PA := PCK_NOMINA.GL_VPA;
  PCK_NOMINA.GL_CES_EXTRAS := PCK_NOMINA.CN(902);
  PCK_NOMINA.GL_BASP_CES := PCK_NOMINA.GL_BONPAGADA;
  PCK_NOMINA.GL_PS_CES := PCK_NOMINA.CN(906);
  PCK_NOMINA.GL_PN_CES := PCK_NOMINA.CN(905);
  PCK_NOMINA.GL_PV_CES := PCK_NOMINA.CN(907);
END PR_CALCULARCESANTIASASO;

PROCEDURE PR_CALCCESANTIASFNACONCTOCAN
/*
    NAME              : PR_CALCCESANTIASFNACONCTOCAN
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 09/03/2026
    TIME              : 02:37 PM
    SOURCE MODULE     : NOMINA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Se personaliza procedimiento de calculo de cesantias FNA en base al proceso de alcaldia de tocancipa
    @NAME             : CALCULARCESANTIASFNACONCTOCANCIPA
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

END PR_CALCCESANTIASFNACONCTOCAN;

PROCEDURE PR_CALCULACESANTIASCONCTOCAN(
    /*
    NAME              : PR_CALCULACESANTIASCONCTOCAN
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 09/03/2026
    TIME              :
    SOURCE MODULE     : NOMINA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Se personaliza procedimiento de calculo de cesantias en base al proceso de alcaldia de tocancipa
    @NAME             : CALCULARCESANTIASCONCTOCANCIPA
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
            PCK_NOMINA.GL_PVAC := 0 + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        ELSE
            IF PCK_NOMINA.FC_CNA(164) > 1 THEN            
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)), 0) + PCK_NOMINA.FC_CN(155);
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501);
            END IF;
        END IF;
        PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;
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

END PR_CALCULACESANTIASCONCTOCAN;

PROCEDURE PR_CALCPRIMASEMESTRALCONCTOC(
/*
NAME              : PR_CALCPRIMASEMESTRALCONCTOC
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
DATE MIGRADOR     : 09/03/2026
TIME              : 02:19 PM
SOURCE MODULE     : NOMINA
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       : Se personaliza procedimiento de calculo de prima semestral en base al proceso de alcaldia de tocancipa
@NAME             : CALCULARPRIMASEMESTRALCONCTOCANCIPA
*/
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)

AS
    MI_DOCEAVASMINIMASPS         PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PSPAGADA                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_PRIMAJUNIO                PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_INDRETIR                  PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_MSG                       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALOR                     PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN
    MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS', '1'));
    PCK_NOMINA.GL_FACTORPS := 0;
    PCK_NOMINA.GL_FACTORPS1 := 0;
    PCK_NOMINA.GL_DNT := 0;

    IF PCK_NOMINA.GL_SMES <= 7 THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
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

        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS', 'NO') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                MI_VALOR := PCK_NOMINA.FC_CN(160);
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS / 360 * PCK_NOMINA.GL_DCC / 30 * PCK_NOMINA.FC_CN(67) + 0.49, 0) ELSE PCK_NOMINA.FC_CN(160) END - PCK_NOMINA.FC_CNA(160);
                MI_VALOR := PCK_NOMINA.FC_CN(160);
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
        IF MI_PSPAGADA > 0 AND PCK_NOMINA.FC_CN(160) > 0 AND PCK_NOMINA.GL_PROCESOACTUAL <> 99 AND PCK_NOMINA.GL_PERIODOACTUAL <> 7 THEN
            PCK_NOMINA.CN(160) := 0;            
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

END PR_CALCPRIMASEMESTRALCONCTOC;

PROCEDURE PR_CALCPRIMANAVIDADCONCTOCAN(
    /*
    NAME              : PR_CALCPRIMANAVIDADCONCTOCAN
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 09/03/2026
    TIME              :
    SOURCE MODULE     : NOMINA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Se personaliza procedimiento de calculo de prima de navidad en base al proceso de alcaldia de tocancipa
    @NAME             : CALCULARPRIMADENAVIDADCONCTOCANCIPA
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
                PCK_NOMINA.GL_PVAC := 0 + PCK_NOMINA.FC_CNA(501);
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) ;
            END IF;
            IF PCK_NOMINA.GL_SMES = 12 AND PCK_NOMINA.GL_SPER = 3 AND PCK_NOMINA.FC_CN(155) > 0 THEN
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501);
            END IF;

            PCK_NOMINA.CN(942) := 0;

            PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
            
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
      
      PCK_NOMINA.GL_FACTORPN := MI_FACTORSUELDO + PCK_NOMINA.FC_CN(942) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + (PCK_NOMINA.GL_PVAC / 12) + PCK_NOMINA.FC_CN(939);
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
            IF PCK_NOMINA.FC_CN(150) > 0 THEN
                PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514)) / 12, 0);
            ELSIF PCK_NOMINA.FC_CNA(150) > 0 THEN
                PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CNA(514)) / 12, 0) ;
            END IF;
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
    MI_TRANSPORTELEGAL := PCK_NOMINA.FC_CN(254);
    MI_RETEFUENTE := PCK_NOMINA.FC_CN(125);

    IF ( NVL(MI_FACTORSUELDO,0) != 0 ) THEN
        PCK_NOMINA.CN(930) := MI_FACTORSUELDO;
    ELSE
        PCK_NOMINA.CN(930) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    END IF;
     
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

END PR_CALCPRIMANAVIDADCONCTOCAN;

PROCEDURE PR_CALPRIMAVACACIONESCONCTOC
/*
NAME              : PR_CALPRIMAVACACIONESCONCTOC
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
DATE MIGRADOR     : 09/03/2026
TIME              : 02:45 PM
SOURCE MODULE     : NOMINA
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       : Se personaliza procedimiento de prima de vacaciones en base al proceso de alcaldia de tocancipa
@NAME             : CALCULARPRIMADEVACACIONESCONCTOCANCIPA
*/
AS
    MI_BONPAGADA     PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_FECHA         DATE;
    MI_VALOR         NUMBER DEFAULT 0;
BEGIN
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11' THEN
        PCK_NOMINA.CN(174) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (PCK_NOMINA.FC_CN(160) / 12), 0) / 2;
    ELSE
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

            PCK_NOMINA.GL_LICENCIAS := NVL(((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.FC_CNA(339)) +
            (
              CASE
              WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                PCK_NOMINA.FC_CESANTIA(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 'L')
              ELSE
                0
              END) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION),0);
            PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91) - PCK_NOMINA.FC_CNA(99);
            PCK_NOMINA.GL_DTV            := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
            (
              (
                CASE
                WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                  PCK_NOMINA.GL_FECHAI
                ELSE
                  (CASE WHEN (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1) BETWEEN PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.GL_FECHAFIN1 THEN PCK_NOMINA.GL_FECHAINI ELSE (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1) END)
                END), PCK_NOMINA.GL_FECHAFIN1)                    - PCK_NOMINA.GL_LICENCIAS;
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
                IF PCK_NOMINA.GL_SPRC = 99 THEN
                    PCK_NOMINA.GL_RTA := 7;
                END IF;

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
                        PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND((15 * PCK_NOMINA.GL_DTV / 360) + PCK_NOMINA.GL_DIASPENDIENTES,2);
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

END PR_CALPRIMAVACACIONESCONCTOC;

PROCEDURE PR_CALPRIMAVACACINTERCONCTOC(
      /*
      NAME              : PR_CALPRIMAVACACINTERCONCTOC
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
      DATE MIGRADOR     : 09/03/2026
      TIME              :
      SOURCE MODULE     : NOMINA
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       :
      @NAME             : CALPRIMAVACINTERCONCTOCANCIPA
      */
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA )
AS 
BEGIN
   IF NOT (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11') THEN
      PCK_NOMINA.GL_DIASVAC := 0;
      PCK_NOMINA.GL_DIASPENDIENTES := 0;
      PCK_NOMINA.GL_PENDIENTES := 0;
      PCK_NOMINA.GL_LICENCIAS := 0;
      PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
      PCK_NOMINA.GL_DINEROPROPORCIONAL := 0;
      PCK_NOMINA.GL_FECHAUV := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL END;
      IF NOT PCK_NOMINA.FC_CN(404) <> 0 THEN
         PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
         PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.CNA(91);
         PCK_NOMINA.CN(93) := PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164);    --' dias de prima pactados para Prima de Vacaciones
         PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,(PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);-- ' Acumulado del ultimo aÃ±o
         IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN --EMPLEADOS OFICIALES
            PCK_NOMINA.GL_FACTORESPV := PCK_NOMINA.FC_CN(1); --' factORes prima de vacaciones
         ELSE
            PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END  + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CN(160) / 12) + (PCK_NOMINA.CNA(160) / 12) + CASE WHEN PCK_NOMINA.GL_SMES <> 9 THEN  (PCK_NOMINA.CNA(150) / 12) + (PCK_NOMINA.FC_CN(150) / 12) ELSE  CASE WHEN PCK_NOMINA.CNA(150) = 0 THEN  PCK_NOMINA.FC_CN(150) ELSE PCK_NOMINA.CNA(150) END / 12 END , 0); -- ' factORes prima de vacaciones
            PCK_NOMINA.GL_FACTORESPV1 := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE  PCK_NOMINA.FC_CN(1) END  + PCK_NOMINA.GL_GASTOSREP + PCK_NOMINA.GL_VPT + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA, 0), 0); --' factORes prima de vacaciones
         END IF;
         IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' THEN 
            IF PCK_NOMINA.FC_CN(99) <> 0 THEN     
               PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(99), 0) ELSE  PCK_NOMINA.FC_CN(174) END;                         --' Vacaciones en Tiempo
            END IF;
            IF PCK_NOMINA.FC_CN(419) <> 0 THEN 
               PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0) ELSE  PCK_NOMINA.FC_CN(175) END;                         --' Vacaciones en dinero
            END IF;
         ELSE 
            IF PCK_NOMINA.FC_CN(403) <> 0 THEN
               PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(99), 0) ELSE  PCK_NOMINA.FC_CN(174) END;                        --' Vacaciones en tiempo
            END IF;
            IF PCK_NOMINA.FC_CN(419) <> 0 THEN
               PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(99), 0) ELSE  PCK_NOMINA.FC_CN(175) END;                         --' Vacaciones en dinero
            END IF;
         END IF;
      END IF;
   END IF;
END PR_CALPRIMAVACACINTERCONCTOC;

END PCK_NOMINA_COM6;