create or replace PACKAGE BODY PCK_PRECONTRACTUAL AS

--1
PROCEDURE PR_VARIABLESPROPONENTES
/*
    NAME              : PR_VARIABLESPROPONENTES ----> EN ACCESS: PrepararVariablesProponentes  
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : LIZETH CASTRO ORDONEZ
    DATE MIGRADOR     : 04/12/2015
    TIME              : 03:20 PM
    SOURCE MODULE     : SysmanTTP2015.11.01 --- MODULO PRECONTRACTUAL
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : ACTUALIZA EL CAMPO VRPREDETERMINADO EN LA TABLA VARIABLE_PROPONENTE
    MODIFICATIONS     : 
    PARAMETERS        : UN_COMPANIA      => Compañia de ingreso a la aplicación
                        UN_USUARIO       => Usuario que accede al sistema 

    @NAME  :  actualizarVariablesPrponentes
    @METHOD:  PUT   
  */
(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_STRCADENA          VARIABLE_PROPONENTE.VRPREDETERMINADO%TYPE;
  MI_STRCADENA2         VARIABLE_PROPONENTE.VRPREDETERMINADO%TYPE;
  MI_POS1               PCK_SUBTIPOS.TI_ENTERO;
  MI_POS2               PCK_SUBTIPOS.TI_ENTERO;
  MI_STRVAR             VARIABLE_PROPONENTE.VRPREDETERMINADO%TYPE;
  MI_SQL                PCK_SUBTIPOS.TI_STRSQL;
  MI_VALPREDETERMINADO  VARIABLE_PROPONENTE.VRPREDETERMINADO%TYPE;
  MI_TABLA              PCK_SUBTIPOS.TI_TABLA; 
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS; 
  MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;
  MI_MERGEUSING         PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE        PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE        PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGENOEXIS        PCK_SUBTIPOS.TI_MERGENOEXISTE;
BEGIN
  <<ACTUALIZA_VARIABLE_PROPONENTE>>  
  FOR RS IN (
    SELECT COMPANIA,
           CODIGO,
           DESCRIPCION,
           TIPO,
           NVL(VRPREDETERMINADO,'') VRPREDETERMINADO,
           ESCONSTANTE,
           ORDEN
      FROM VARIABLE_PROPONENTE
     WHERE COMPANIA   = UN_COMPANIA
       AND TIPO       = '5')

  LOOP
    MI_STRCADENA := RS.VRPREDETERMINADO;
    MI_STRCADENA := REPLACE(MI_STRCADENA, '=Formula', '');
    MI_STRCADENA := REPLACE(MI_STRCADENA, '''', '');
    MI_POS1      := INSTR(MI_STRCADENA, '<<', 1, 1);

    IF MI_POS1 > 0 THEN
      MI_POS2 := INSTR(MI_STRCADENA, '>>', 1, 1);
      IF MI_POS2 > 0 THEN
        MI_STRVAR     := SUBSTR(MI_STRCADENA, MI_POS1 + 2, MI_POS2 - MI_POS1 - 2);
        MI_STRCADENA2 := SUBSTR(MI_STRCADENA, MI_POS2 + 2, 255);
        MI_STRCADENA  := SUBSTR(MI_STRCADENA, 1, MI_POS1 - 1);

        MI_SQL := 'SELECT VRPREDETERMINADO 
                     FROM VARIABLE_PROPONENTE
                    WHERE COMPANIA = ''' || UN_COMPANIA || '''
                      AND CODIGO   = ''' || MI_STRVAR ||'''';

        EXECUTE IMMEDIATE MI_SQL INTO MI_VALPREDETERMINADO;

        IF MI_VALPREDETERMINADO IS NOT NULL THEN
          MI_STRCADENA := MI_STRCADENA || MI_VALPREDETERMINADO;
        END IF;

        MI_STRCADENA:= MI_STRCADENA || MI_STRCADENA2;
      END IF;
    END IF;

    MI_TABLA      := 'VARIABLE_PROPONENTE';
    MI_CAMPOS     := 'VRPREDETERMINADO  = ''' || MI_STRCADENA || ''', '||
                     'DATE_MODIFIED     = SYSDATE, '||
                     'MODIFIED_BY       = ''' || UN_USUARIO || ''' ';    
    MI_CONDICION  := '     COMPANIA     = ''' ||UN_COMPANIA || ''' '||
                     ' AND CODIGO       = ''' ||RS.CODIGO || '''';

    BEGIN
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                     UN_ACCION    =>  'M', 
                                     UN_CAMPOS    =>  MI_CAMPOS,
                                     UN_CONDICION =>  MI_CONDICION);                 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;
      END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN    
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACTUAL_ACTVARPRO,
            UN_TABLAERROR => MI_TABLA
          );
    END;

  END LOOP ACTUALIZA_VARIABLE_PROPONENTE;

  MI_MERGEUSING := 'SELECT TRANSACCION.COMPANIA,
                    TRANSACCION.TIPOCONTRATO,
                    TRANSACCION.CONSECUTIVO ,
                    PROPONENTE.CONSECUTIVODETALLE,
                    PROPONENTE.PROPONENTE,
                    VARIABLE_PROPONENTE.CODIGO,
                    VARIABLE_PROPONENTE.VRPREDETERMINADO,
                    VARIABLE_PROPONENTE.TIPO,
                    PROPONENTE.SUCURSAL,
                    CASE WHEN   VARIABLE_PROPONENTE.TIPO = 1
                     THEN NVL(VARIABLE_PROPONENTE.VRPREDETERMINADO , '' '')
                        WHEN VARIABLE_PROPONENTE.TIPO  IN (3,4,6)
                         THEN  '' ''
                        WHEN VARIABLE_PROPONENTE.TIPO = 5
                            THEN NVL(PCK_SYSMAN_UTL.FC_EVAL(VARIABLE_PROPONENTE.VRPREDETERMINADO),'' '')
                    END VLR_TEXTO ,
                    CASE WHEN VARIABLE_PROPONENTE.TIPO =  3
                            THEN TO_CHAR(NVL(PCK_SYSMAN_UTL.FC_EVAL(VARIABLE_PROPONENTE.VRPREDETERMINADO) , ''0''))
                        WHEN VARIABLE_PROPONENTE.TIPO  IN (1,4,5,6)
                            THEN ''0''
                        ELSE
                            NULL
                    END VLR_NUMERO,
                       CASE WHEN VARIABLE_PROPONENTE.TIPO =  4
                            THEN  TO_DATE(TO_CHAR(PCK_SYSMAN_UTL.FC_EVAL(NVL(VARIABLE_PROPONENTE.VRPREDETERMINADO,SYSDATE))))
                        WHEN VARIABLE_PROPONENTE.TIPO  IN (1,3,5)
                            THEN TO_DATE(TO_CHAR(SYSDATE))
                        WHEN VARIABLE_PROPONENTE.TIPO = 6    
                        THEN  TO_DATE(TO_CHAR(SYSDATE,''HH24:MI:ss''),''HH24:MI:ss'')
                        ELSE    NULL
                   END VLR_FECHA
              FROM TRANSACCION 
              INNER JOIN D_TRANSACCION 
                ON D_TRANSACCION.COMPANIA = TRANSACCION.COMPANIA
                AND D_TRANSACCION.TIPOCONTRATO = TRANSACCION.TIPOCONTRATO
                AND D_TRANSACCION.TRANSACCION = TRANSACCION.CONSECUTIVO
              INNER JOIN PROPONENTE 
                ON D_TRANSACCION.COMPANIA = PROPONENTE.COMPANIA
                AND D_TRANSACCION.TIPOCONTRATO = PROPONENTE.TIPOCONTRATO
                AND D_TRANSACCION.TRANSACCION = PROPONENTE.TRANSACCION
                AND D_TRANSACCION.CONSECUTIVODETALLE = PROPONENTE.CONSECUTIVODETALLE
              INNER JOIN VARIABLE_PROPONENTE
                ON TRANSACCION.COMPANIA = VARIABLE_PROPONENTE.COMPANIA
              WHERE TRANSACCION.COMPANIA = '''||UN_COMPANIA||'''
              AND TRANSACCION.ESTADO   = ''AC''
              AND D_TRANSACCION.ESTADO = ''A''
              AND PROPONENTE.ESTADO IN( ''AC'', ''EV'')';

  MI_MERGEENLACE :=  'TABLA.COMPANIA = VISTA.COMPANIA 
                      AND TABLA.TIPOCONTRATO = VISTA.TIPOCONTRATO
                      AND TABLA.TRANSACCION = VISTA.CONSECUTIVO
                      AND TABLA.CONSECUTIVODETALLE = VISTA.CONSECUTIVODETALLE
                      AND TABLA.PROPONENTE = VISTA.PROPONENTE
                      AND TABLA.SUCURSAL = VISTA.SUCURSAL
                      AND TABLA.VARIABLE=VISTA.CODIGO';

  MI_MERGEEXISTE := 'UPDATE SET 
                    TABLA.VALOR = VISTA.VRPREDETERMINADO, 
                    TABLA.VLR_NUMERO = VISTA.VLR_NUMERO, 
                    TABLA.VLR_TEXTO = VISTA.VLR_TEXTO, 
                    TABLA.VLR_FECHA = VISTA.VLR_FECHA';

  MI_MERGENOEXIS:= 'INSERT (TABLA.COMPANIA ,TABLA.SUCURSAL, 
                            TABLA.TIPOCONTRATO, TABLA.CONSECUTIVODETALLE , 
                            TABLA.TRANSACCION , TABLA.PROPONENTE , 
                            TABLA.VARIABLE , TABLA.VALOR , 
                            TABLA.VLR_NUMERO , TABLA.VLR_TEXTO , 
                            TABLA.VLR_FECHA)
                            VALUES (VISTA.COMPANIA ,VISTA.SUCURSAL, 
                            VISTA.TIPOCONTRATO , VISTA.CONSECUTIVODETALLE , 
                            VISTA.CONSECUTIVO , VISTA.PROPONENTE, 
                            VISTA.CODIGO , VISTA.VRPREDETERMINADO , 
                            VISTA.VLR_NUMERO , VISTA.VLR_TEXTO ,
                            VISTA.VLR_FECHA)';

   BEGIN
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       =>  'VLR_VARIABLE_PROPONENTE', 
                                     UN_ACCION      =>  'IM', 
                                     UN_MERGEUSING  =>  MI_MERGEUSING,
                                     UN_MERGEENLACE =>  MI_MERGEENLACE,
                                     UN_MERGEEXISTE =>  MI_MERGEEXISTE,
                                     UN_MERGENOEXIS =>  MI_MERGENOEXIS);                 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACT_MERGEPROPON,
                                    UN_TABLAERROR => MI_TABLA);  
    END;

END PR_VARIABLESPROPONENTES;

--2
PROCEDURE PR_ACTUALIZARVARIABLES
/*
    NAME              : PR_ACTUALIZARVARIABLES ----> EN ACCESS------ Actualizar_Variables_Click  
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : LIZETH CASTRO ORDONEZ
    DATE MIGRADOR     : 04/12/2015
    TIME              : 05:50 PM
    SOURCE MODULE     : SysmanTTP2015.11.01 --- MODULO PRECONTRACTUAL
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : ACTUALIZA LOS CAMPOS DE UNA TABLA
    MODIFICATIONS     : 
    PARAMETERS        : UN_COMPANIA      => Compañia de ingreso a la aplicación
                        UN_USUARIO       => Usuario que accede al sistema 

    @NAME  :  actualizarVariables
    @METHOD:  PUT   
  */
(
  UN_COMPANIA  	IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_USUARIO 	  IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_COUNT     PCK_SUBTIPOS.TI_ENTERO;
BEGIN
  PCK_PRECONTRACTUAL.PR_VARIABLESPROPONENTES(UN_COMPANIA => UN_COMPANIA,
                                             UN_USUARIO  => UN_USUARIO);

  BEGIN
    SELECT COUNT(*) 
      INTO MI_COUNT
      FROM VARIABLE_PROPONENTE;
  END;

  EXCEPTION WHEN NO_DATA_FOUND THEN
  RETURN;

   --POR TERMINAR 
END PR_ACTUALIZARVARIABLES;

--3
FUNCTION FC_ACTUALIZARTRANSACCION
/*
    NAME              : FC_ACTUALIZARTRANSACCION
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : LEYDI MILENA CORTES FORERO
    DATE MIGRADOR     : 09/12/2015
    TIME              : 12:15 PM
    SOURCE MODULE     : SysmanTTP2015.11.01 --> Precontractual
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : PERMITE ACTUALIZAR EL ESTADO DE UNA TRANSACCION AL CERRARSE O CANCELARSE EL PROCESO.
    MODIFICATIONS     : 
    PARAMETERS        : UN_COMPANIA           => Compañia de ingreso a la aplicación
                        UN_TIPOCONTRATO       => Tipo de contrato de la transacción en la cual se desea cambiar el estado
                        UN_CONSECUTIVO        => Identificador de la transacción que se desea modificar
                        UN_ESTADO             => Estado actual de la transacción con la que se desea trabajar
                        UN_OBSERVACION        => Observación a registrar en la transacción que se esrá modificando
                        UN_USUARIO            => Usuario que accede al sistema 

    @NAME  :  actualizarTransaccion
    @METHOD:  GET 

  */ 
(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCONTRATO       IN TRANSACCION.TIPOCONTRATO%TYPE,
  UN_CONSECUTIVO        IN TRANSACCION.CONSECUTIVO%TYPE,
  UN_ESTADO             IN TRANSACCION.ESTADO%TYPE,
  UN_OBSERVACION        IN TRANSACCION.OBSERVACION%TYPE,
  UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_RTA_ACME
AS
  MI_TABLA              PCK_SUBTIPOS.TI_TABLA; 
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS; 
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;
  MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
  IF UN_ESTADO = 'CE' THEN -- estado cerrado -> cierro transaccion y etapas
    MI_TABLA      := 'D_TRANSACCION';
    MI_CAMPOS     := 'D_TRANSACCION.ESTADO = ''C'', '||
                     'DATE_MODIFIED     = SYSDATE, '||
                     'MODIFIED_BY       = ''' || UN_USUARIO || ''' ';       
    MI_CONDICION  := '     COMPANIA     = ''' || UN_COMPANIA || ''' ' ||
                     ' AND TIPOCONTRATO = ''' || UN_TIPOCONTRATO || ''' ' ||
                     ' AND TRANSACCION  = ''' || UN_CONSECUTIVO || '''';

    BEGIN
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                     UN_ACCION    =>  'M', 
                                     UN_CAMPOS    =>  MI_CAMPOS,
                                     UN_CONDICION =>  MI_CONDICION);                 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN
        MI_MSGERROR (1).CLAVE := 'CONSECUTIVO';
        MI_MSGERROR (1).VALOR := UN_CONSECUTIVO;
        PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACTUAL_ACTESTDTRA,
                                    UN_TABLAERROR => MI_TABLA,
                                    UN_REEMPLAZOS => MI_MSGERROR);  
    END;
  END IF;

  MI_TABLA      := 'TRANSACCION';
  MI_CAMPOS     := ' TRANSACCION.ESTADO = ''' || UN_ESTADO || ''', ' ||
                   ' OBSERVACION        = ''' || UN_OBSERVACION || ''', '||
                   ' DATE_MODIFIED      = SYSDATE, '||
                   ' MODIFIED_BY        = ''' || UN_USUARIO || ''' ';       
  MI_CONDICION  := '     COMPANIA     = ''' || UN_COMPANIA || ''' ' ||
                   ' AND TIPOCONTRATO = ''' || UN_TIPOCONTRATO || ''' ' ||
                   ' AND CONSECUTIVO  = ''' || UN_CONSECUTIVO || ''' ';

  BEGIN
    BEGIN
      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                   UN_ACCION    =>  'M', 
                                   UN_CAMPOS    =>  MI_CAMPOS,
                                   UN_CONDICION =>  MI_CONDICION);                 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN    
      MI_MSGERROR (1).CLAVE := 'CONSECUTIVO';
      MI_MSGERROR (1).VALOR := UN_CONSECUTIVO;
      PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                  UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACTUAL_ACTESTTRA,
                                  UN_TABLAERROR => MI_TABLA,
                                  UN_REEMPLAZOS => MI_MSGERROR);  
  END;

  RETURN MI_RTA;
END FC_ACTUALIZARTRANSACCION;

--4
PROCEDURE PR_ACTUALIZARETAPAS

/*
    NAME              : PR_ACTUALIZARETAPAS -> ACCESS ActEtp
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 15/12/2015
    TIME              : 11:30 AM
    SOURCE MODULE     : SysmanTTP2015.11.01 --> Precontractual
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Actualiza las etapas pertenecientes a un tipo de contrato
    MODIFICATIONS     : 
    PARAMETERS        : UN_COMPANIA           => Compañia de ingreso a la aplicación
                        UN_TIPOCONTRATO       => Tipo de contrato de la transacción realizar la actualización de etapas
                        UN_CONSECUTIVO        => Identificador de la transacción que se desea modificar
                        UN_USUARIO            => Usuario que accede al sistema 
    @NAME  :  actualizarEtapas
    @METHOD:  POST                          
*/ 

(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCONTRATO       IN TRANSACCION.TIPOCONTRATO%TYPE,
  UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_TRANSACCION        PCK_SUBTIPOS.TI_LOGICO;
  MI_ORDEN              ETAPA_PR.ORDEN%TYPE;
  MI_VCONSECUTIVO       D_TRANSACCION.CONSECUTIVODETALLE%TYPE := NULL;
  MI_VORDEN             D_TRANSACCION.ORDEN%TYPE := NULL;
  MI_VAR                PCK_SUBTIPOS.TI_RTA_ACME;
  MI_TABLA              PCK_SUBTIPOS.TI_TABLA; 
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS; 
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_BANDERA            PCK_SUBTIPOS.TI_LOGICO := 0;
  MI_TEMPORAL           DATE; 
  MI_FECHAINI           DATE; 
  MI_FECHAFIN           DATE; 
  MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
  ---Realiza la insercion de las nuevas etapas
  <<INSERTA_DETALLES>>
  FOR RS IN (
    SELECT TRANSACCION.ANO,
           TRANSACCION.CONSECUTIVO,
           ETAPA_PR.IDETAPA,
           ETAPA_PR.ORDEN
      FROM TRANSACCION
        INNER JOIN ETAPA_PR 
           ON TRANSACCION.COMPANIA      = ETAPA_PR.COMPANIA
          AND TRANSACCION.TIPOCONTRATO  = ETAPA_PR.TIPOCONTRATO
     WHERE TRANSACCION.COMPANIA     = UN_COMPANIA
       AND TRANSACCION.TIPOCONTRATO = UN_TIPOCONTRATO
       AND TRANSACCION.ESTADO       IN ('AC')
       AND ETAPA_PR.HEREDABLE       NOT IN (0))

  LOOP
    MI_ORDEN := RS.ORDEN;
    BEGIN
    --Se quita el filtro por estapa debido a que no permite evaluar la integridad de la tabla
     SELECT 1 
       INTO MI_TRANSACCION
       FROM D_TRANSACCION
      WHERE COMPANIA           = UN_COMPANIA
        AND TIPOCONTRATO       = UN_TIPOCONTRATO
        AND TRANSACCION        = RS.CONSECUTIVO
        AND CONSECUTIVODETALLE = RS.ORDEN;
        --AND IDETAPA = RS.IDETAPA;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_TRANSACCION := 0;
    END;

    IF(MI_TRANSACCION IN (0)) THEN
      WITH D_TRAN AS (
        SELECT COMPANIA,
               TRANSACCION,
               TIPOCONTRATO,
               CONSECUTIVODETALLE,
               ORDEN
         FROM D_TRANSACCION
        WHERE COMPANIA      = UN_COMPANIA
          AND TIPOCONTRATO  = UN_TIPOCONTRATO
          AND TRANSACCION   = RS.CONSECUTIVO
          AND ORDEN         > MI_ORDEN
          AND ESTADO        IN ('A', 'P')
          )
      SELECT MAX(D_TRAN.CONSECUTIVODETALLE), 
             MAX(D_TRAN.ORDEN) 
        INTO MI_VCONSECUTIVO,
             MI_VORDEN
        FROM D_TRAN 
          INNER JOIN D_TRANSACCION
             ON D_TRAN.COMPANIA     = D_TRANSACCION.COMPANIA
            AND D_TRAN.TIPOCONTRATO = D_TRANSACCION.TIPOCONTRATO
            AND D_TRAN.TRANSACCION  = D_TRANSACCION.TRANSACCION;

      IF(MI_VCONSECUTIVO IS NOT NULL AND MI_VORDEN IS NOT NULL) THEN
        MI_CAMPOS := 'COMPANIA,
                      TIPOCONTRATO,
                      TRANSACCION,
                      CONSECUTIVODETALLE,
                      IDETAPA,
                      ESTADO,
                      ORDEN,
                      FECHAINICIAL,
                      FECHAFINAL,
                      CREATED_BY,
                      DATE_CREATED';

        MI_VALORES := '''' || UN_COMPANIA || ''',
                       ''' || UN_TIPOCONTRATO || ''',
                         ' || RS.CONSECUTIVO ||',
                         ' || RS.ORDEN || ',
                         ' || RS.IDETAPA || ',
                         ''A'',
                         ' || RS.ORDEN || ',
                         SYSDATE,
                         SYSDATE,
                       ''' || UN_USUARIO || ''',
                         SYSDATE';                      

      ELSE
        MI_CAMPOS := 'COMPANIA,
                      TIPOCONTRATO,
                      TRANSACCION,
                      CONSECUTIVODETALLE,
                      IDETAPA,
                      ESTADO,
                      ORDEN,
                      CREATED_BY,
                      DATE_CREATED';

        MI_VALORES := '''' || UN_COMPANIA || ''',
                       ''' || UN_TIPOCONTRATO || ''',
                         ' || RS.CONSECUTIVO || ',
                         ' || RS.ORDEN || ',
                         ' || RS.IDETAPA || ',
                         ''P'',
                         ' || RS.ORDEN || ',
                       ''' || UN_USUARIO || ''',
                         SYSDATE';

      END IF;

      BEGIN
        BEGIN
          MI_TABLA := 'D_TRANSACCION';
          MI_VAR   :=  PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA,
                                          UN_ACCION     => 'I', 
                                          UN_CAMPOS     => MI_CAMPOS,
                                          UN_VALORES    => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                         

          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN
            MI_MSGERROR(1).CLAVE := 'CONSECUTIVO';
            MI_MSGERROR(1).VALOR := RS.CONSECUTIVO ;
            MI_MSGERROR(2).CLAVE := 'ANO';
            MI_MSGERROR(2).VALOR := RS.ANO;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_PRECONTRACTUAL_INSETADTRA,
              UN_TABLAERROR =>  MI_TABLA,
              UN_REEMPLAZOS =>  MI_MSGERROR
          );
        END ;        

    END IF;
  END LOOP INSERTA_DETALLES;

  MI_BANDERA := 0;
  --Actualiza las fechas
  <<ACTUALIZA_FECHAS>>
  FOR RS IN (
    SELECT D_TRANSACCION.CONSECUTIVODETALLE,
            D_TRANSACCION.ESTADO,
            D_TRANSACCION.FECHAINICIAL, 
            D_TRANSACCION.FECHAFINAL,
            D_TRANSACCION.TRANSACCION,
            ETAPA_PR.TIPODIAS,
            D_TRANSACCION.IDETAPA,
            ETAPA_PR.DIAS
      FROM TRANSACCION
        INNER JOIN D_TRANSACCION
          ON TRANSACCION.COMPANIA       = D_TRANSACCION.COMPANIA
          AND TRANSACCION.TIPOCONTRATO  = D_TRANSACCION.TIPOCONTRATO
          AND TRANSACCION.CONSECUTIVO   = D_TRANSACCION.TRANSACCION
        INNER JOIN ETAPA_PR 
          ON D_TRANSACCION.COMPANIA       = ETAPA_PR.COMPANIA
          AND D_TRANSACCION.TIPOCONTRATO  = ETAPA_PR.TIPOCONTRATO
          AND D_TRANSACCION.IDETAPA       = ETAPA_PR.IDETAPA
     WHERE TRANSACCION.COMPANIA     = UN_COMPANIA
       AND TRANSACCION.TIPOCONTRATO = UN_TIPOCONTRATO
       AND TRANSACCION.ESTADO       = 'AC'
       AND D_TRANSACCION.ESTADO     IN ('C','S','A','P')) 
  LOOP

    IF(RS.ESTADO = 'C' OR RS.ESTADO='S') THEN
      MI_BANDERA  := 1;
      MI_TEMPORAL := NVL(RS.FECHAFINAL, SYSDATE); 
    ELSIF (RS.ESTADO = 'A' OR RS.ESTADO = 'P') THEN
      IF(MI_BANDERA IN(0)) THEN
        MI_FECHAINI := NVL(RS.FECHAINICIAL,SYSDATE);
        MI_BANDERA  :=1;
      ELSE
        MI_FECHAINI := MI_TEMPORAL;     
      END IF;
      MI_FECHAFIN := TO_CHAR(PCK_PRECONTRACTUAL.FC_FECHACIERRE(UN_COMPANIA      => UN_COMPANIA,
                                                               UN_FECHAINICIAL  => MI_FECHAINI,
                                                               UN_NUMDIAS       => RS.DIAS,
                                                               UN_TIPO          => RS.TIPODIAS),
                             'DD/MM/YYYY');
      MI_TEMPORAL := MI_FECHAFIN;
      MI_TABLA    := 'D_TRANSACCION';
      MI_CAMPOS   := ' FECHAINICIAL  = TO_DATE('''||TO_CHAR(MI_FECHAINI,'DD/MM/YYYY' )||''', ''DD/MM/YYYY''),
                       FECHAFINAL    = TO_DATE('''||TO_CHAR(MI_FECHAFIN,'DD/MM/YYYY' )||''', ''DD/MM/YYYY''),
                       MODIFIED_BY   = ''' || UN_USUARIO || ''',  
                       DATE_MODIFIED = SYSDATE';

      --Toma el papel de condicion para no tener que crear otra funcion
      MI_VALORES := '     COMPANIA            = '''||UN_COMPANIA||''' 
                      AND TIPOCONTRATO        = '''||UN_TIPOCONTRATO||''' 
                      AND TRANSACCION         =   '||RS.TRANSACCION||' 
                      AND CONSECUTIVODETALLE  =   '||RS.CONSECUTIVODETALLE||' 
                      AND IDETAPA             =   '||RS.IDETAPA;

      BEGIN
        BEGIN
          MI_VAR := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                       UN_ACCION    =>  'M', 
                                       UN_CAMPOS    =>  MI_CAMPOS,
                                       UN_CONDICION =>  MI_VALORES);                 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN    
          MI_MSGERROR (1).CLAVE := 'CONSECUTIVO';
          MI_MSGERROR (1).VALOR := RS.TRANSACCION;
          PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACTUAL_INSFECDTRA,
                                      UN_TABLAERROR => MI_TABLA,
                                      UN_REEMPLAZOS => MI_MSGERROR);  
      END;
    END IF;
  END LOOP ACTUALIZA_FECHAS;
END PR_ACTUALIZARETAPAS;

--5
  FUNCTION FC_FECHACIERRE
  /*
      NAME              : FC_FECHACIERRE Nombre en Access --> FECHACIERRE
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
      DATE MIGRADOR     : 15/02/2016
      TIME              : 02:00 PM
      SOURCE MODULE     : SysmanTTP2015.11.01 --> Precontractual
      MODIFIER          : AURA LILIANA MONROY GARCIA
      DATE MODIFIED     : 27/10/2017 
      TIME              : 
      DESCRIPTION       : Adiciona los dias ingresados por parametro a una fecha inicial, teniendo en cuenta si son calendario o no
      MODIFICATIONS     : Se cambia la forma de comparar los fines de semana, pasando del numero de dias a los nombres de los dias
      PARAMETERS        : UN_COMPANIA      => Compañia de ingreso a la aplicación
                          UN_FECHAINICIAL  => Fecha base para adicionar los días
                          UN_NUMDIAS  	   => Número de días que se desean adicionar
                          UN_TIPO  		     => Tipo de día (Hábiles[1] - Calendario)

    @NAME  :  adicionarDias
    @METHOD:  GET  

  */ 
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIAL   IN DATE,
    UN_NUMDIAS        IN PCK_SUBTIPOS.TI_ENTERO,
    UN_TIPO           IN PCK_SUBTIPOS.TI_LOGICO
  )
  RETURN DATE
  AS
    MI_FECHARETURN    DATE;
    MI_FESTIVO        PCK_SUBTIPOS.TI_LOGICO;
  BEGIN
    MI_FECHARETURN := UN_FECHAINICIAL;

    -- El parametro con valor 1 corresponde a dias habiles
    <<AUMENTA_DIAS>>
    FOR I IN 1..UN_NUMDIAS 
    LOOP
      IF(UN_TIPO = 1) THEN
        MI_FECHARETURN := MI_FECHARETURN + 1;
        MI_FESTIVO 	   := 0;

        <<EVALUA_FESTIVO>>
        WHILE MI_FESTIVO = 0 
        LOOP
          -- Dia de la semana      
          IF(     TRIM(TO_CHAR(MI_FECHARETURN, 'DAY', 'NLS_DATE_LANGUAGE = ENGLISH')) NOT LIKE 'SATURDAY'
              AND TRIM(TO_CHAR(MI_FECHARETURN, 'DAY', 'NLS_DATE_LANGUAGE = ENGLISH')) NOT LIKE 'SUNDAY' 
              AND PCK_PRECONTRACTUAL.FC_ESFESTIVO(UN_COMPANIA => UN_COMPANIA,
                                                  UN_FECHA    => MI_FECHARETURN) = 0) THEN
            MI_FESTIVO 	 := 1;
          ELSE
            MI_FECHARETURN := MI_FECHARETURN + 1;
          END IF;
        END LOOP EVALUA_FESTIVO;

      ELSE
        MI_FECHARETURN := UN_FECHAINICIAL + UN_NUMDIAS;
      END IF;
    END LOOP AUMENTA_DIAS;    

    RETURN MI_FECHARETURN;
  END FC_FECHACIERRE;

--6
FUNCTION FC_ESFESTIVO
/*
    NAME              : FC_ESFESTIVO Nombre en Access --> ESFESTIVO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 15/02/2016
    TIME              : 05:30 PM
    SOURCE MODULE     : SysmanTTP2015.11.01 --> Precontractual
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Verifica si la fecha ingresada por parametro es festivo
    MODIFICATIONS     : 
    PARAMETERS        : UN_COMPANIA      => Compañia de ingreso a la aplicación
                        UN_FECHA         => Fecha a evaluar si es festivo

    @NAME  :  evaluarFestivo
    @METHOD:  GET   

*/ 
(
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_FECHA        IN DATE
)
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_RETURN       PCK_SUBTIPOS.TI_LOGICO;
BEGIN
  BEGIN
   SELECT 1 
     INTO MI_RETURN
     FROM FESTIVOS
    WHERE COMPANIA 							   = UN_COMPANIA
      AND TO_CHAR(ID_DE_FESTIVO, 'DD/MM/YYYY') = TO_CHAR(UN_FECHA, 'DD/MM/YYYY');

  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_RETURN := 0;
  END;

  RETURN MI_RETURN;

END FC_ESFESTIVO;

--7
PROCEDURE PR_ACTUALIZARVARIABLESPREC
/*
    NAME              : PR_ACTUALIZARVARIABLES Nombre en Access --> ActVar
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 15/02/2016
    TIME              : 05:30 PM
    SOURCE MODULE     : SysmanTTP2015.11.01 --> Precontractual
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Actualiza las variables a la licitacion activa
    MODIFICATIONS     : 
    PARAMETERS        : UN_COMPANIA           => Compañia de ingreso a la aplicación
                        UN_TIPOCONTRATO       => Tipo de contrato de la transacción en la que se va a realizar 
                                                 la actualización de las variables de precontrato
                        UN_USUARIO            => Usuario que accede al sistema 

    @NAME  :  actualizarVariables
    @METHOD:  PUT                          

*/ 
(
  UN_COMPANIA       		    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOCONTRATO   		    IN TRANSACCION.TIPOCONTRATO%TYPE,
  UN_USUARIO             	  IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_VAR                    PCK_SUBTIPOS.TI_RTA_ACME;
  MI_VARIABLE               PCK_SUBTIPOS.TI_LOGICO;
  MI_VALORPREDETERMINADO    VARIABLE.VRPREDETERMINADO%TYPE;
  MI_VLR_TEXTO              VARIABLETRANSACCION.VLR_TEXTO%TYPE;
  MI_VLR_NUMERO             VARIABLETRANSACCION.VLR_NUMERO%TYPE;
  MI_VLR_FECHA              VARIABLETRANSACCION.VLR_FECHA%TYPE;
  MI_EVALUAR                VARIABLE.VRPREDETERMINADO%TYPE;
  MI_TABLA                  PCK_SUBTIPOS.TI_TABLA; 
  MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS; 
  MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
BEGIN
  <<INSERTA_VARIABLE>>
	FOR RS IN (
	  	SELECT TRANSACCION.CONSECUTIVO,
	            VARIABLE.CODIGO,
	            VARIABLE.VRPREDETERMINADO,
	            VARIABLE.TIPO
	      FROM TRANSACCION
	      	INNER JOIN VARIABLE
		    	 ON TRANSACCION.COMPANIA      = VARIABLE.COMPANIA
		    	AND TRANSACCION.TIPOCONTRATO  = VARIABLE.TIPOCONTRATO
	     WHERE TRANSACCION.COMPANIA 	  = UN_COMPANIA
	       AND TRANSACCION.TIPOCONTRATO = UN_TIPOCONTRATO
	       AND TRANSACCION.ESTADO 		  = 'AC')
    LOOP  
	    BEGIN
        SELECT 1 
          INTO MI_VARIABLE
          FROM VARIABLETRANSACCION
         WHERE COMPANIA 		= UN_COMPANIA
           AND TIPOCONTRATO = UN_TIPOCONTRATO
           AND TRANSACCION 	= RS.CONSECUTIVO
           AND VARIABLE 		= RS.CODIGO;

        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_VARIABLE := 0;
	    END;

	    IF(MI_VARIABLE = 0) THEN
	    	MI_VALORPREDETERMINADO := '';
	      	IF(RS.TIPO = 1) THEN ---TEXTO
		        MI_VLR_TEXTO	:= REPLACE(NVL(RS.VRPREDETERMINADO, ''), '''', '');
		        MI_VLR_NUMERO	:= 0;
		        MI_VLR_FECHA	:= SYSDATE;
	      	ELSIF (RS.TIPO = 3) THEN ---NUMERO
		        --En el version de access si el VRPREDETERMINADO la funcion VAL devuelve 0
		        BEGIN
		          MI_VLR_NUMERO := NVL(RS.VRPREDETERMINADO, 0);
		          EXCEPTION WHEN OTHERS THEN
		            MI_VLR_NUMERO := 0;
		        END;
		        MI_VLR_FECHA := SYSDATE;
		        MI_VLR_TEXTO := '';
	      	ELSIF(RS.TIPO = 4) THEN ---FECHA
		        BEGIN
		          MI_EVALUAR := NVL(RS.VRPREDETERMINADO, SYSDATE);
		          IF(UPPER(MI_EVALUAR) = 'DATE()') THEN
		            MI_VLR_FECHA := SYSDATE;
		          ELSE
		            MI_VLR_FECHA := TO_DATE(NVL(RS.VRPREDETERMINADO, SYSDATE), 'DD/MM/YYYY');
		          END IF;
		        EXCEPTION WHEN OTHERS THEN
		          MI_VLR_FECHA := NULL;
		        END;
		        MI_VLR_TEXTO  := '';
		        MI_VLR_NUMERO := 0;
	      	ELSIF(RS.TIPO = 6) THEN ---HORA
		        BEGIN
		        	MI_VLR_FECHA := TO_DATE(TO_CHAR(NVL(RS.VRPREDETERMINADO, SYSDATE), 'HH24:MI:SS'), 'HH24:MI:SS');
			        EXCEPTION WHEN OTHERS THEN
			          MI_VLR_FECHA := NULL;
		        END;
		        MI_VLR_TEXTO  := '';
		        MI_VLR_NUMERO := 0;
	      	ELSIF(RS.TIPO = 5) THEN
		        BEGIN
			        MI_VLR_TEXTO := PCK_SYSMAN_UTL.EVAL(RS.VRPREDETERMINADO);
			        EXCEPTION WHEN OTHERS THEN
			          MI_VLR_TEXTO := NULL;
		        END;
		        MI_VLR_NUMERO := 0;
		        MI_VLR_FECHA  := SYSDATE;
	      	END IF;

		    --Se agrego la funcion converirVariable que permite hacer la conversion del valor predeterminado a un valor aceptado por la sintaxis de Oracle
		    MI_VALORPREDETERMINADO  := REPLACE(NVL(RS.VRPREDETERMINADO,'0'), '''', '');
		    MI_EVALUAR 				      := PCK_SYSMAN_UTL.FC_EVAL(UN_CADENA => NVL(PCK_PRECONTRACTUAL.FC_CONVERTIR_VARIABLE(UN_VARIABLE => MI_VALORPREDETERMINADO), '0'));


		    MI_TABLA  := 'VARIABLETRANSACCION';

		    MI_CAMPOS := 'COMPANIA,
                      TIPOCONTRATO,
                      TRANSACCION,
                      VARIABLE,
                      VALOR,
                      VLR_TEXTO,
                      VLR_NUMERO,
                      VLR_FECHA,
                      CREATED_BY,
                      DATE_CREATED';

        MI_VALORES := '''' || UN_COMPANIA || ''',
                       ''' || UN_TIPOCONTRATO || ''',
                         ' || RS.CONSECUTIVO || ',
                       ''' || RS.CODIGO || ''',
                       ''' || MI_EVALUAR || ''',
                       ''' || MI_VLR_TEXTO || ''',
                         ' || MI_VLR_NUMERO || ',
                       TO_DATE('''|| TO_CHAR(MI_VLR_FECHA,'DD/MM/YYYY HH24:MI:SS') ||''', ''DD/MM/YYYY HH24:MI:SS''),
                       ''' || UN_USUARIO || ''',
	                       SYSDATE';

	  		BEGIN
		        BEGIN
              MI_VAR   :=  PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA,
                                             UN_ACCION     => 'I', 
                                             UN_CAMPOS     => MI_CAMPOS,
                                             UN_VALORES    => MI_VALORES);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                         

		         END;
		          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
		            PCK_ERR_MSG.RAISE_WITH_MSG(
		              UN_EXC_COD    =>  SQLCODE,
		              UN_ERROR_COD  =>  PCK_ERRORES.ERR_PRECONTRACTUAL_INSVARTRA,
		              UN_TABLAERROR =>  MI_TABLA
		          );
	        END ;  

	    END IF;
  	END LOOP INSERTA_VARIABLE;

END PR_ACTUALIZARVARIABLESPREC;

--8
FUNCTION FC_CONVERTIR_VARIABLE
/*
    NAME              : FC_PREPARARVARIABLESPRO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDGAR LEONARDO SARMIENTO
    DATE MIGRADOR     : 16/02/2016
    TIME              : 11:20 AM
    SOURCE MODULE     : SysmanTTP2015.11.01 --> Precontractual
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : FUNCION DESARROLLA QUE CALCULA LA EQUIVALENCIA DEL VALOR PREDETERMINADO DE LAS VARIABLES A ORACLE
    MODIFICATIONS     : 
    PARAMETERS        : UN_VARIABLE 	=>  Variable a convertir

    @NAME  :  convertirVariables
    @METHOD:  GET                        

*/ 
(
  UN_VARIABLE     IN PCK_SUBTIPOS.TI_VALORES
)
RETURN PCK_SUBTIPOS.TI_VALORES
AS
	MI_VARIABLE     PCK_SUBTIPOS.TI_VALORES := '';
BEGIN
	IF(UN_VARIABLE IS NOT NULL) THEN
		IF(UPPER(UN_VARIABLE)    = 'DATE()') THEN
		  MI_VARIABLE := 'SYSDATE';
		ELSIF(UPPER(UN_VARIABLE) = 'TIME()') THEN
		  MI_VARIABLE := 'TO_CHAR(SYSDATE, ''HH24:MI:SS'')';
		ELSIF(UPPER(UN_VARIABLE) = 'YEAR(DATE())') THEN
		  MI_VARIABLE := 'EXTRACT (YEAR FROM SYSDATE)';
		ELSIF(UPPER(UN_VARIABLE) = 'MONTH(DATE())') THEN
		  MI_VARIABLE := 'EXTRACT (MONTH FROM SYSDATE)';
		ELSE
		  MI_VARIABLE := UN_VARIABLE;
		END IF;
	END IF;

  RETURN MI_VARIABLE;

END FC_CONVERTIR_VARIABLE;


--9
PROCEDURE PR_CAMBIAR_NUM_CERT_INEXIST
/*
  NAME              : PR_CAMBIAR_NUM_CERT_INEXIST
  AUTHORS           : SYSMAN
  AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO
  DATE MIGRADOR     : 06/07/2022
  TIME              : 
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       : Permite cambiar el numero del certificado inexistencia
  MODIFICATIONS     :
  PARAMETROS DE ENTRADA:
    UN_COMPANIA:           Codigo de la compania
    UN_USUARIO:            Nombre de usuario que se identifica en la aplicacion
    UN_NUMERO_CERTIFICADO: Numero de certificado anterior.
    UN_NUMERO_NUEVO:       Numero nuevo que se le va a asignar al certificado de inexistencia.

*/
(
  UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
, UN_USUARIO                      IN PCK_SUBTIPOS.TI_USUARIO
, UN_NUMERO_CERTIFICADO           IN CERTIFICADO_INEXISTENCIA.NUMERO%TYPE
, UN_NUMERO_NUEVO                 IN CERTIFICADO_INEXISTENCIA.NUMERO%TYPE
) AS
  MI_CANTIDAD                     PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS                       PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION                    PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA_ACME                     PCK_SUBTIPOS.TI_RTA_ACME;
  MI_REEMPLAZOS                   PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

  SELECT COUNT(0) CANT
    INTO MI_CANTIDAD
  FROM CERTIFICADO_INEXISTENCIA
  WHERE COMPANIA = UN_COMPANIA
    AND NUMERO = UN_NUMERO_NUEVO;
    
  IF MI_CANTIDAD = 0 THEN
  
    MI_CAMPOS := 'NUMERO = ' || UN_NUMERO_NUEVO ||
        ', MODIFIED_BY = ''' || UN_USUARIO ||
        ''', DATE_MODIFIED = SYSDATE';
        
    MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA ||
        ''' AND NUMERO = ' || UN_NUMERO_CERTIFICADO;

    BEGIN
        BEGIN   
            MI_RTA_ACME := PCK_DATOS.FC_ACME(
                UN_TABLA => 'CERTIFICADO_INEXISTENCIA',
                UN_ACCION => 'M',
                UN_CAMPOS => MI_CAMPOS,
                UN_CONDICION => MI_CONDICION

        );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_ACTUALIZAR ;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        MI_REEMPLAZOS(1).CLAVE := 'NUMERO';
        MI_REEMPLAZOS(1).VALOR := UN_NUMERO_CERTIFICADO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                    , UN_TABLAERROR => 'CERTIFICADO_INEXISTENCIA'
          , UN_ERROR_COD => PCK_ERRORES.ERR_CAMBIAR_NUMERO_CERTIFICADO
          , UN_REEMPLAZOS => MI_REEMPLAZOS );
        END;
  ELSE
  BEGIN
    RAISE PCK_EXCEPCIONES.EXC_ACTUALIZAR;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        MI_REEMPLAZOS(1).CLAVE := 'NUMERO';
        MI_REEMPLAZOS(1).VALOR := UN_NUMERO_CERTIFICADO;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
            , UN_TABLAERROR => 'CERTIFICADO_INEXISTENCIA'
      , UN_ERROR_COD => PCK_ERRORES.ERR_CAMBIAR_NUMERO_CERTIFICADO
      , UN_REEMPLAZOS => MI_REEMPLAZOS );
    END;
  END IF;
END PR_CAMBIAR_NUM_CERT_INEXIST;

END PCK_PRECONTRACTUAL;