create or replace PACKAGE BODY "PCK_PREDIAL_COM3" 
AS

--1 

  
--2

  
--3

   
--4

FUNCTION FC_REPARTIRDEUDA
  /*
  NAME              : PR_REPARTIRDEUDA  --> EN ACCESS RepartirDeuda
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE MIGRADOR     : 07/09/2016
  TIME              : 10:02 AM
  SOURCE MODULE     : PredialP2016.05.06
  DESCRIPTION       : Procedimiento que toma las vigencias con deuda y realiza una distribución de los conceptos adeduados hasta llevar
                      al tompe del valor ingresado por parámetro que indica el monto a abonar a la deuda. 
                      El parámetro UN_INDACUERDO se usa para determinar si el abono se realiza sobre vigencias que se encuentran en acuerdo de pago
  MODIFIER          : Leydi Milena Cortés Forero
  DATE MODIFIED     : 06/03/2017
  TIME              : 12:25 PM
  MODIFICATIONS     : Se corrige el envío de los parámetros a la función PCK_DATOS.FC_ACME acción 'M' en los cursores RECORRE_DISTCONCEPTOS, 
                      VERIFICAR_CONCEPTOINTERES y VERIFICAR_CONCEPTOCAPITAL, para no asignar UN_VALORES sino UN_CAMPOS que es el 
                      parámetro que utiliza dicha acción en la función.
  @NAME: repartirDeuda
  @METHOD: POST
  */
  (
   -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el codigo del predio al que se le va a repartir la deuda
  UN_CODPREDIO               IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el ano en el que se va a realizar el abono
  UN_ANOABONO                IN PCK_SUBTIPOS.TI_ANIO,
  -- Parametro que recibe el valor del abono que se va a realizar
  UN_ABONO                   IN PCK_SUBTIPOS.TI_DOBLE,
  -- Parametro que contiene el numero de recibo del que se va a realizar el abono
  UN_NUMRECIBO               IN PCK_SUBTIPOS.TI_DOCNUM,
  -- Parametro usado para determinar si el abono se realiza sobre vigencias que se encuentran en acuerdo de pago 
  UN_INDACUERDO              IN PCK_SUBTIPOS.TI_LOGICO,
  -- Parametro que recibe el nombre del usuario que va a realizar en abono
  UN_USUARIO                 IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN PCK_SUBTIPOS.TI_ANIO
  AS
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 4;
    MI_CPTODESC              PCK_SUBTIPOS.TI_PARAMETRO;
    MI_APLICADSCT            PCK_SUBTIPOS.TI_PARAMETRO;
    MI_APLICAABONOVIG        PCK_SUBTIPOS.TI_PARAMETRO;
    MI_MSG                   VARCHAR2(32000 CHAR);
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    MI_RSFACTURADOS          SYS_REFCURSOR;
    MI_SQL                   PCK_SUBTIPOS.TI_STRSQL;
    MI_DEUDAVIGENCIA         PCK_SUBTIPOS.TI_DOBLE;
    MI_DEUDACPTO             PCK_SUBTIPOS.TI_DOBLE;
    MI_PREANO                PCK_SUBTIPOS.TI_ANIO;
    MI_TOTAL                 PCK_SUBTIPOS.TI_DOBLE;
    MI_C1                    PCK_SUBTIPOS.TI_DOBLE;
    MI_C2                    PCK_SUBTIPOS.TI_DOBLE;
    MI_C3                    PCK_SUBTIPOS.TI_DOBLE;
    MI_C4                    PCK_SUBTIPOS.TI_DOBLE;
    MI_C5                    PCK_SUBTIPOS.TI_DOBLE;
    MI_C6                    PCK_SUBTIPOS.TI_DOBLE;
    MI_C7                    PCK_SUBTIPOS.TI_DOBLE;
    MI_C8                    PCK_SUBTIPOS.TI_DOBLE;
    MI_C9                    PCK_SUBTIPOS.TI_DOBLE;
    MI_C10                   PCK_SUBTIPOS.TI_DOBLE;
    MI_C11                   PCK_SUBTIPOS.TI_DOBLE;
    MI_C12                   PCK_SUBTIPOS.TI_DOBLE;
    MI_C13                   PCK_SUBTIPOS.TI_DOBLE;
    MI_C14                   PCK_SUBTIPOS.TI_DOBLE; 
    MI_C15                   PCK_SUBTIPOS.TI_DOBLE;
    MI_C16                   PCK_SUBTIPOS.TI_DOBLE;
    MI_C17                   PCK_SUBTIPOS.TI_DOBLE;
    MI_C18                   PCK_SUBTIPOS.TI_DOBLE;
    MI_C19                   PCK_SUBTIPOS.TI_DOBLE;
    MI_C20                   PCK_SUBTIPOS.TI_DOBLE;
    MI_VLCPTO                PCK_SUBTIPOS.TI_DOBLE;  --ALMACENA EL VALOR DEL CONCEPTO QUE SE ESTA DISTRIBUYENDO
    MI_VLVIGANT              PCK_SUBTIPOS.TI_DOBLE;  --VALOR DEL CONCEPTO QUE CORRESPONDE A LA VIGENCIA ANTERIOR
    MI_VLVIGDFR              PCK_SUBTIPOS.TI_DOBLE;  --VALOR DEL CONCEPTO QUE CORRESPONDE A LAS VIGENCIAS DE DIFICIL RECAUDO
    MI_TIPODISTRIB           PCK_SUBTIPOS.TI_PARAMETRO;
    MI_SALDOABONO            PCK_SUBTIPOS.TI_DOBLE;
    MI_CAPITAL               PCK_SUBTIPOS.TI_DOBLE;
    MI_INTERES               PCK_SUBTIPOS.TI_DOBLE;
    MI_SALDOCAPITAL          PCK_SUBTIPOS.TI_DOBLE;
    MI_SALDOINTERES          PCK_SUBTIPOS.TI_DOBLE;
    MI_AVALUO                PCK_SUBTIPOS.TI_DOBLE;
    MI_TRPPOR                PCK_SUBTIPOS.TI_PORCENTAJE;
    MI_REEMPLAZOS            PCK_SUBTIPOS.TI_CLAVEVALOR; -- Variable usada para los reemplazos en el control de errores
    MI_ANIOABONO             PCK_SUBTIPOS.TI_ANIO; -- VARIABLE PARA RETORNAR LA VIGENCIA HASTA LA CUAL SE ALCANZA A REALIZAR EL ABONO  - INDISPENSABLE PARA PROCESO DE ELIMINAR ACUERDO
  BEGIN
   EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,''';
  MI_SALDOABONO := UN_ABONO;
  -- ESTABLECER LA CONDICION DE LAS CONSULTAS, SE DEBE TENER EN CUENTA SI LAS VIGENCIAS ESTAN EN ACUERDO DE PAGO O NO
  MI_CONDICION := '   COMPANIA         = ''' || UN_COMPANIA  ||
                  ''' AND CODIGO       = ''' || UN_CODPREDIO ||
                  ''' AND NUMERO_ORDEN = ''' || GL_NUMORDEN  || 
                  ''' AND PAGADO       = 0' ;
  -- LIMPIAR LA TABLA DE DISTRIBUCIÓN DE ABONOS
  BEGIN
  PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSABONOS', 
                                          UN_ACCION   =>'E',
                                          UN_VALORES  =>MI_VALORES,
                                          UN_CONDICION=>MI_CONDICION
                                          );  
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END ; 

  MI_APLICAABONOVIG := PCK_PARST.FC_PAR(UN_PARAMETRO=>'APLICA ABONOS POR VIGENCIA', 
                                        UN_VLOMISION=>''
                                        );

  IF MI_APLICAABONOVIG = 'SI' AND UN_INDACUERDO = 0 THEN
     MI_CONDICION      := MI_CONDICION            ||
                          ' AND INDPAGO_ACPAG = ' || UN_INDACUERDO ||
                          ' AND PREANO       >= ' || UN_ANOABONO;
  ELSE
     MI_CONDICION      := MI_CONDICION            ||
                          ' AND INDPAGO_ACPAG = ' || UN_INDACUERDO ;
  END IF;

  --VERIFICAR EL CONCEPTO CONFIGURADO PARA ALMACENAR EL VALOR DE DESCUENTO
  MI_CPTODESC   := PCK_PARST.FC_PAR(UN_PARAMETRO=>'CONCEPTO DE DESCUENTO', 
                                    UN_VLOMISION=>''
                                    );
  -- VERIFICA SI EL SISTEMA ESTA CONFIGURADO PARA TENER EN CUENTA O NO EL VALOR DE ABONOS EN LA DISTRIBUCIÓN 
  MI_APLICADSCT := PCK_PARST.FC_PAR(UN_PARAMETRO=>'APLICAR DESCUENTO PARA ABONOS',
                                    UN_VLOMISION=>''
                                    );
  IF MI_APLICADSCT = 'NO' THEN
    IF MI_CPTODESC = '' THEN
       MI_MSG := 'No se encontró configuración del concepto en el cual se calculan los intereses. Por favor verificar el parámetro CONCEPTO DE DESCUENTO';
    ELSE 
      -- PRIMERO, SE DEBE SUMAR EL VALOR DE INTERESES AL TOTAL DE LAS VIGENCIAS QUE APLIQUEN PARA REALIZAR EL ABONO 
      MI_VALORES := ' TOTAL = TOTAL + ABS(C' || MI_CPTODESC ||
                    '), MODIFIED_BY     = '''||UN_USUARIO   ||'''
                     , DATE_MODIFIED    = SYSDATE '; 
      BEGIN               
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOS', 
                                             UN_ACCION   =>'M',
                                             UN_CAMPOS  =>MI_VALORES, 
                                             UN_CONDICION=>MI_CONDICION 
                                             );  
      -- SEGUNDO, SE DEBE DEJAR EN CERO EL CAMPO DONDE SE ALMACENA EL VALOR DEL  DESCUENTO   
      MI_VALORES := ' C' || MI_CPTODESC ||' = 0
                    , MODIFIED_BY = ''' || UN_USUARIO ||'''
                    , DATE_MODIFIED = SYSDATE' ;

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOS',
                                             UN_ACCION   =>'M',
                                             UN_CAMPOS  =>MI_VALORES, 
                                             UN_CONDICION=>MI_CONDICION 
                                             );  

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                 END ;                 
    END IF;
  END IF;
  -- CARGAR EN UN CURSOR LAS VIGENCIAS QUE SE PUEDE TOMAR PARA SER AFECTADAS POR EL ABONO

  MI_CONDICION := MI_CONDICION ||
                ' ORDER BY PREANO ASC ' ;
  MI_SQL := ' SELECT PREANO  , AVALUO
                   , TRP_POR , TOTAL
                   , C1      , C2
                   , C3      , C4
                   , C5      , C6
                   , C7      , C8
                   , C9      , C10
                   , C11     , C12
                   , C13     , C14
                   , C15     , C16
                   , C17     , C18
                   , C19     , C20  ' ||
            '   FROM IP_FACTURADOS  ' ||
            '  WHERE '|| MI_CONDICION;

  OPEN MI_RSFACTURADOS FOR MI_SQL;
	<<RECORRER_FACTURADOS>>
    LOOP
      FETCH MI_RSFACTURADOS INTO MI_PREANO  , MI_AVALUO
                               , MI_TRPPOR  , MI_TOTAL
                               , MI_C1      , MI_C2
                               , MI_C3      , MI_C4
                               , MI_C5      , MI_C6
                               , MI_C7      , MI_C8
                               , MI_C9      , MI_C10
                               , MI_C11     , MI_C12
                               , MI_C13     , MI_C14
                               , MI_C15     , MI_C16
                               , MI_C17     , MI_C18
                               , MI_C19     , MI_C20;
      EXIT WHEN MI_RSFACTURADOS%NOTFOUND;
        MI_DEUDAVIGENCIA := MI_TOTAL;
        MI_ANIOABONO := MI_PREANO;
        IF MI_SALDOABONO  = 0 THEN
          GOTO SALIR;
        END IF;
        --VERIFICA SI EL ABONO ALCANCA A CUBRIR LA TOTALIDAD DE LA VIGENCIA
        IF MI_SALDOABONO >= MI_DEUDAVIGENCIA THEN
          MI_CAMPOS := 'COMPANIA,         CODIGO, 
                        NUMERO_ORDEN,     PREANO, 
                        DOCNUM,           PAGADO, 
                        C1 ,              C2, 
                        C3,               C4, 
                        C5,               C6, 
                        C7,               C8, 
                        C9,               C10,
                        C11 ,             C12, 
                        C13,              C14, 
                        C15,              C16, 
                        C17,              C18, 
                        C19,              C20,
                        TOTAL,            FECHAFACTURADO, 
                        AVALUO ,          TRPPOR,
                        CREATED_BY,       DATE_CREATED';
          MI_VALORES := ''''    || UN_COMPANIA  || ''', '''|| UN_CODPREDIO ||
                        ''', '''|| GL_NUMORDEN  ||''' , '  || MI_PREANO    ||
                        '  , '''|| UN_NUMRECIBO ||''' ,  0, 
                        '       ||MI_C1         ||'   , '  || MI_C2        ||
                        '  ,'   ||MI_C3         ||'   , '  || MI_C4        ||
                        '  ,'   ||MI_C5         ||'   , '  || MI_C6        ||
                        '  ,'   ||MI_C7         ||'   , '  || MI_C8        ||
                        '  ,'   ||MI_C9         ||'   , '  || MI_C10       ||
                        '  ,'   ||MI_C11        ||'   , '  || MI_C12       ||
                        '  ,'   ||MI_C13        ||'   , '  || MI_C14       ||
                        '  ,'   ||MI_C15        ||'   , '  || MI_C16       ||
                        '  ,'   ||MI_C17        ||'   , '  || MI_C18       ||
                        '  ,'   ||MI_C19        ||'   , '  || MI_C20       ||
                        '  ,'   || MI_TOTAL     ||'   , TRUNC(SYSDATE)
                           ,'   || MI_AVALUO    ||'   , '  || MI_TRPPOR    || 
                        '  ,''' || UN_USUARIO   || ''', SYSDATE ';

          BEGIN              
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_FACTURADOSABONOS',
                                                 UN_ACCION=>'I',
                                                 UN_CAMPOS=>MI_CAMPOS,
                                                 UN_VALORES=>MI_VALORES
                                                 ); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END ;                                       
          MI_SALDOABONO := MI_SALDOABONO - MI_DEUDAVIGENCIA;
          MI_ANIOABONO := MI_PREANO;
        ELSE       
          --CREAR EL REGISTRO PARA LUEGO IRLO MODIFICANDO CON LOS VALORES QUE CORRESPONDA A CADA CONCEPTO
          MI_CAMPOS := '  COMPANIA
                        , CODIGO
                        , NUMERO_ORDEN
                        , PREANO
                        , DOCNUM
                        , PAGADO
                        , FECHAFACTURADO
                        , TOTAL
                        , AVALUO
                        , TRPPOR
                        , CREATED_BY
                        , DATE_CREATED';
          MI_VALORES := ''''    || UN_COMPANIA  || 
                        ''', '''|| UN_CODPREDIO ||
                        ''', '''|| GL_NUMORDEN  ||
                        ''', '  || MI_PREANO    ||
                        '  , '''|| UN_NUMRECIBO ||
                        ''', 0 
                           , TRUNC(SYSDATE)
                           ,0
                           , '  || MI_AVALUO    ||
                        '  , '  || MI_TRPPOR    ||
                        '  , '''|| UN_USUARIO   ||
                        ''',  SYSDATE ';      
          BEGIN              
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA  =>'IP_FACTURADOSABONOS', 
                                                 UN_ACCION =>'I', 
                                                 UN_CAMPOS =>MI_CAMPOS, 
                                                 UN_VALORES=>MI_VALORES
                                                 );  

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END ; 

          --CONDICION PARA REALIZAR LA ACTUALIZACION DE LOS CONCEPTOS QUE VAYAN SIENDO AFECTADOS POR EL ABONO
          MI_CONDICION := '   COMPANIA          = '''|| UN_COMPANIA  ||
                          ''' AND CODIGO        = '''|| UN_CODPREDIO || 
                          ''' AND NUMERO_ORDEN  = '''|| GL_NUMORDEN  ||
                          ''' AND PREANO        = '  || MI_PREANO    ||
                          '   AND DOCNUM        = '''|| UN_NUMRECIBO || ''' ';
          --RECORRE LA CONFIGURACION DE CONCEPTOS PARA INICIALIZAR LOS CONCEPTOS EN CERO DONDE SE REALIZA LA DISTRIBUCIÓN Y 
          --HACER COPIA DE CONCEPTOS DE VIGENCIA ANTERIOR Y DE DIFICIL RECAUDO
		  <<RECORRE_CONFCONCEPTOS>>
          FOR RSCPTO IN(
                          SELECT  CODIGO,
                                  VIGANT, 
                                  VIGDFR 
                            FROM  IP_CONCEPTOS
                           WHERE  COMPANIA  = UN_COMPANIA
                             AND  ANO       = MI_PREANO
                             AND  PRIORIDAD  NOT IN (0)
                           ORDER BY CODIGO
                        )
                        LOOP
            --CONSULTAR EL VALOR DEL CONCEPTO PARA ACTUALIZA EL CAMPO CORRESPONDIENTE EN EL ABONO
          BEGIN
            BEGIN
              MI_SQL := ' SELECT C' || RSCPTO.VIGANT ||', C'||RSCPTO.VIGDFR||
                        '   FROM IP_FACTURADOS 
                           WHERE COMPANIA       = ''' || UN_COMPANIA  || '''
                             AND CODIGO         = ''' || UN_CODPREDIO || '''
                             AND NUMERO_ORDEN   = ''' || GL_NUMORDEN  || '''
                             AND PREANO         = '   || MI_PREANO    || '';

               EXECUTE IMMEDIATE MI_SQL INTO  MI_VLVIGANT,  MI_VLVIGDFR;
               IF RSCPTO.CODIGO <> RSCPTO.VIGANT AND RSCPTO.CODIGO <> RSCPTO.VIGDFR THEN
                  MI_VALORES := '  C' || RSCPTO.CODIGO ||' = 0, 
                                   C' || RSCPTO.VIGANT ||' = ' || MI_VLVIGANT ||
                                ', C' || RSCPTO.VIGDFR ||' = ' || MI_VLVIGDFR ||
                                ', MODIFIED_BY = '''||UN_USUARIO ||'''
                                 , DATE_MODIFIED = SYSDATE';
               ELSE
                  MI_VALORES := '  C' || RSCPTO.CODIGO ||' = 0';
               END IF;

               BEGIN
               PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSABONOS', 
                                                      UN_ACCION   =>'M', 
                                                      UN_CAMPOS   =>MI_VALORES,
                                                      UN_CONDICION=>MI_CONDICION
                                                      ); 

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END ;

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                   MI_REEMPLAZOS(0).CLAVE:='UN_CODPREDIO';
                   MI_REEMPLAZOS(0).VALOR:=UN_CODPREDIO;   
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD     => SQLCODE,
                   UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_PROCABONOPREDIO,
                   UN_TABLAERROR  => 'IP_FACTURADOSABONOS',
                   UN_REEMPLAZOS => MI_REEMPLAZOS  
                  );
         END;    

      END;
          END LOOP RECORRE_CONFCONCEPTOS;
          --VERIFICAR VALOR DE CADA CONCEPTO CON RESPECTO AL SALDO DEL ABONO PARA REALIZAR LA DISTRIBUCIÓN DE LA DEUDA
          -- SE DEBE TENER EN CUENTA LA FORMA DE DISTRIBUICIÓN QUE SE DESEA REALIZAR
          MI_TIPODISTRIB := PCK_PARST.FC_PAR(UN_PARAMETRO=>'DISTRIBUYE ABONO POR PRORATEO (R), PRIORIDAD (P)', 
                                             UN_VLOMISION=>''
                                             );
          IF MI_TIPODISTRIB = 'P' THEN
		    <<RECORRE_DISTCONCEPTOS>>
            FOR RSCPTO IN(
                            SELECT  CODIGO, VIGANT, VIGDFR 
                              FROM  IP_CONCEPTOS
                             WHERE  COMPANIA  = UN_COMPANIA
                               AND  ANO       = MI_PREANO
                               AND  PRIORIDAD NOT IN (0)
                             ORDER BY PRIORIDAD
                          )
                          LOOP
              --CONSULTAR EL VALOR DEL CONCEPTO PARA ACTUALIZA EL CAMPO CORRESPONDIENTE EN EL ABONO
              BEGIN
                MI_SQL := ' SELECT C' || RSCPTO.CODIGO ||
                          '   FROM IP_FACTURADOS 
                             WHERE COMPANIA       = ''' || UN_COMPANIA  || '''
                               AND CODIGO         = ''' || UN_CODPREDIO || '''
                               AND NUMERO_ORDEN   = ''' || GL_NUMORDEN  || '''
                               AND PREANO         = '   || MI_PREANO    || '';

                EXECUTE IMMEDIATE MI_SQL INTO  MI_DEUDACPTO;
                IF MI_SALDOABONO >= ABS(MI_DEUDACPTO) THEN
                  MI_VALORES := '  C' || RSCPTO.CODIGO ||' = '  || MI_DEUDACPTO ||
                                ', TOTAL = TOTAL           + '  || MI_DEUDACPTO ||
                                ', MODIFIED_BY             = '''|| UN_USUARIO   ||'''
                                 , DATE_MODIFIED           = SYSDATE ';

                  BEGIN               
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSABONOS',
                                                         UN_ACCION   =>'M',
                                                         UN_CAMPOS   =>MI_VALORES,
                                                         UN_CONDICION=>MI_CONDICION 
                                                         ); 
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                             END ;

                  MI_SALDOABONO := MI_SALDOABONO - MI_DEUDACPTO;
                ELSE
                  MI_VALORES := '  C' || RSCPTO.CODIGO ||' = '  || MI_SALDOABONO ||
                                ', TOTAL = TOTAL           + '  || MI_SALDOABONO ||
                                ', MODIFIED_BY             = '''||UN_USUARIO     ||'''
                                 , DATE_MODIFIED           = SYSDATE ';
                  BEGIN               
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSABONOS',
                                                         UN_ACCION   =>'M',
                                                         UN_CAMPOS   =>MI_VALORES,
                                                         UN_CONDICION=>MI_CONDICION 
                                                         ); 
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                             END ;

                  MI_SALDOABONO := 0;
                   MI_ANIOABONO := MI_PREANO;
                END IF;                 

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                   MI_REEMPLAZOS(0).CLAVE:='UN_CODPREDIO';
                   MI_REEMPLAZOS(0).VALOR:=UN_CODPREDIO;   
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD     => SQLCODE,
                   UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_PROCABONOPREDIO,
                   UN_TABLAERROR  => 'IP_FACTURADOSABONOS',
                   UN_REEMPLAZOS => MI_REEMPLAZOS  
                  );
           END; 
            END LOOP RECORRE_DISTCONCEPTOS;
          ELSE
            MI_CAPITAL := MI_C1 + MI_C3;
            MI_INTERES := MI_C2 + MI_C4;
            MI_SALDOCAPITAL := ROUND(MI_SALDOABONO * (MI_CAPITAL / (MI_CAPITAL + MI_INTERES)), 0);
            MI_SALDOINTERES := ROUND(MI_SALDOABONO * (MI_INTERES / (MI_CAPITAL + MI_INTERES)), 0);

			<<VERIFICAR_CONCEPTOINTERES>>
            FOR RSCPTO IN(
                            SELECT  CODIGO, VIGANT, VIGDFR 
                              FROM  IP_CONCEPTOS
                             WHERE  COMPANIA  = UN_COMPANIA
                               AND  ANO       = MI_PREANO
                               AND  PRIORIDAD  NOT IN (0)
                               AND  ESINTERESVIGENCIA NOT IN (0) 
                             ORDER BY PRIORIDAD
                          )
                          LOOP
              --CONSULTAR EL VALOR DEL CONCEPTO PARA ACTUALIZA EL CAMPO CORRESPONDIENTE EN EL ABONO
              BEGIN
                MI_SQL := ' SELECT C' || RSCPTO.CODIGO ||
                          ' FROM   IP_FACTURADOS 
                            WHERE  COMPANIA       = ''' || UN_COMPANIA   || '''
                              AND  CODIGO         = ''' || UN_CODPREDIO || '''
                              AND  NUMERO_ORDEN   = ''' || GL_NUMORDEN  || '''
                              AND  PREANO         = '   || MI_PREANO    ||'';

                EXECUTE IMMEDIATE MI_SQL INTO  MI_DEUDACPTO;
                IF MI_SALDOINTERES >= ABS(MI_DEUDACPTO) THEN
                  MI_VALORES := '  C' || RSCPTO.CODIGO ||' = '  || MI_DEUDACPTO ||
                                ', TOTAL = TOTAL           + '  || MI_DEUDACPTO ||
                                ', MODIFIED_BY             = '''|| UN_USUARIO    ||'''
                                 , DATE_MODIFIED           = SYSDATE ';
                  BEGIN               
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSABONOS',
                                                         UN_ACCION   =>'M',
                                                         UN_CAMPOS   =>MI_VALORES,
                                                         UN_CONDICION=>MI_CONDICION
                                                         ); 
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                             END ;

                  MI_SALDOABONO := MI_SALDOABONO - MI_DEUDACPTO;
                  MI_SALDOINTERES := MI_SALDOINTERES - MI_DEUDACPTO;
                ELSIF MI_SALDOINTERES > 0 THEN
                  MI_VALORES := '  C' || RSCPTO.CODIGO ||' = '  || MI_SALDOINTERES ||
                                ', TOTAL = TOTAL           + '  || MI_SALDOINTERES ||
                                ', MODIFIED_BY             = '''|| UN_USUARIO       ||'''
                                 , DATE_MODIFIED           = SYSDATE ';
                  BEGIN               
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSABONOS',
                                                         UN_ACCION   =>'M',
                                                         UN_CAMPOS   =>MI_VALORES, 
                                                         UN_CONDICION=>MI_CONDICION 
                                                         ); 
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                             END ;

                  MI_SALDOABONO := MI_SALDOABONO - MI_SALDOINTERES;
                  MI_SALDOINTERES := 0;
                END IF;             

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                   MI_REEMPLAZOS(0).CLAVE:='UN_CODPREDIO';
                   MI_REEMPLAZOS(0).VALOR:=UN_CODPREDIO;   
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD     => SQLCODE,
                   UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_PROCABONOPREDIO,
                   UN_TABLAERROR  => 'IP_FACTURADOSABONOS',
                   UN_REEMPLAZOS => MI_REEMPLAZOS  
                  );
           END; 
            END LOOP VERIFICAR_CONCEPTOINTERES;

			<<VERIFICAR_CONCEPTOCAPITAL>>
            FOR RSCPTO IN(
                            SELECT  CODIGO, VIGANT, VIGDFR 
                              FROM  IP_CONCEPTOS
                             WHERE  COMPANIA  = UN_COMPANIA
                               AND  ANO       = MI_PREANO
                               AND  PRIORIDAD  NOT IN (0)
                               AND  ESCAPITALVIGENCIA NOT IN (0) 
                             ORDER BY PRIORIDAD
                          )
                          LOOP
              --CONSULTAR EL VALOR DEL CONCEPTO PARA ACTUALIZA EL CAMPO CORRESPONDIENTE EN EL ABONO
              BEGIN
                MI_SQL := ' SELECT C' || RSCPTO.CODIGO ||
                          '   FROM IP_FACTURADOS 
                             WHERE COMPANIA       = ''' || UN_COMPANIA   || '''
                               AND CODIGO         = ''' || UN_CODPREDIO ||'''
                               AND NUMERO_ORDEN   = ''' || GL_NUMORDEN  || '''
                               AND PREANO         = '   || MI_PREANO    ||'';

                EXECUTE IMMEDIATE MI_SQL INTO  MI_DEUDACPTO;
                IF MI_SALDOCAPITAL  >= ABS(MI_DEUDACPTO) THEN
                  MI_VALORES := '  C' || RSCPTO.CODIGO ||' = '  || MI_DEUDACPTO ||
                                ', TOTAL = TOTAL           + '  || MI_DEUDACPTO ||
                                ', MODIFIED_BY             = '''|| UN_USUARIO    ||'''
                                 , DATE_MODIFIED           = SYSDATE ';

                  BEGIN               
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSABONOS',
                                                         UN_ACCION   =>'M', 
                                                         UN_CAMPOS   =>MI_VALORES,
                                                         UN_CONDICION=>MI_CONDICION
                                                         ); 
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                             END ;                                       

                  MI_SALDOABONO := MI_SALDOABONO - MI_DEUDACPTO;
                  MI_SALDOCAPITAL := MI_SALDOCAPITAL - MI_DEUDACPTO;
                ELSIF MI_SALDOCAPITAL > 0 THEN
                  MI_VALORES := '  C' || RSCPTO.CODIGO ||' = '  || MI_SALDOCAPITAL ||
                                ', TOTAL = TOTAL           + '  || MI_SALDOCAPITAL ||
                                ', MODIFIED_BY             = '''|| UN_USUARIO       ||'''
                                 , DATE_MODIFIED           = SYSDATE ';
                  BEGIN              
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSABONOS', 
                                                         UN_ACCION   =>'M', 
                                                         UN_CAMPOS   =>MI_VALORES,
                                                         UN_CONDICION=>MI_CONDICION 
                                                         ); 
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                             END ;

                  MI_SALDOABONO := MI_SALDOABONO - MI_SALDOCAPITAL;
                  MI_SALDOCAPITAL := 0;
                END IF;                 

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                   MI_REEMPLAZOS(0).CLAVE:='UN_CODPREDIO';
                   MI_REEMPLAZOS(0).VALOR:=UN_CODPREDIO;   
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD     => SQLCODE,
                   UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_PROCABONOPREDIO,
                   UN_TABLAERROR  => 'IP_FACTURADOSABONOS',
                   UN_REEMPLAZOS => MI_REEMPLAZOS  
                  );
            END;
            END LOOP VERIFICAR_CONCEPTOCAPITAL;
          END IF; -- FIN TIPO DE DISTRIBUCIÓN 
        END IF;        
    END LOOP RECORRER_FACTURADOS;
  <<SALIR>>   
  CLOSE MI_RSFACTURADOS; 
  RETURN  MI_ANIOABONO ;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_REPARTIR_DEUDA
  );
END FC_REPARTIRDEUDA;

--5
PROCEDURE PR_REPARTIRDEUDAPRORATEADA
  /*
  NAME              : PR_REPARTIRDEUDAPRORATEADA  --> EN ACCESS RepartirDeudaProratiada
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE MIGRADOR     : 09/09/2016
  TIME              : 12:05 PM
  SOURCE MODULE     : PredialP2016.05.06
  DESCRIPTION       : Procedimiento que toma las vigencias con deuda y realiza una distribución de los conceptos adeduados prorrateando
  @NAME: repartirDeudaProratiada
  @METHOD: POST
  */
  (
   -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el codigo del predio a evaluar
  UN_CODPREDIO               IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el ano al que se realiza el abono
  UN_ANOABONO                IN PCK_SUBTIPOS.TI_ANIO,
  -- Parametro que recibe el valor del abono que se va a evaluar
  UN_ABONO                   IN PCK_SUBTIPOS.TI_DOBLE,
  -- Parametro que recibe el numero de recibo del abono que se va a evaluar
  UN_NUMRECIBO               IN PCK_SUBTIPOS.TI_DOCNUM,
  -- Parametro que recibe el nombre del usuario que va a realizar el abono
  UN_USUARIO                 IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 5;
    MI_CPTODESC              PCK_SUBTIPOS.TI_PARAMETRO;
    MI_APLICAABONOVIG        PCK_SUBTIPOS.TI_PARAMETRO;
    MI_APLICADSCT            PCK_SUBTIPOS.TI_PARAMETRO;
    MI_PRORATEA              PCK_SUBTIPOS.TI_PARAMETRO;
    MI_MSG                   VARCHAR2(32000 CHAR);
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    MI_RSFACTURADOS          SYS_REFCURSOR;
    MI_SQL                   PCK_SUBTIPOS.TI_STRSQL;
    MI_DEUDAVIGENCIA         PCK_SUBTIPOS.TI_DOBLE;
    MI_DEUDACPTO             PCK_SUBTIPOS.TI_DOBLE;
    MI_PREANO                PCK_SUBTIPOS.TI_ANIO;
    MI_TOTAL                 PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C1                    PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C2                    PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C3                    PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C4                    PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C5                    PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C6                    PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C7                    PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C8                    PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C9                    PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C10                   PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C11                   PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C12                   PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C13                   PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C14                   PCK_SUBTIPOS.TI_DOBLE := 0; 
    MI_C15                   PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C16                   PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C17                   PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C18                   PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C19                   PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_C20                   PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_VLCPTO                PCK_SUBTIPOS.TI_DOBLE := 0;  --ALMACENA EL VALOR DEL CONCEPTO QUE SE ESTA DISTRIBUYENDO
    MI_VLVIGANT              PCK_SUBTIPOS.TI_DOBLE := 0;  --VALOR DEL CONCEPTO QUE CORRESPONDE A LA VIGENCIA ANTERIOR
    MI_VLVIGDFR              PCK_SUBTIPOS.TI_DOBLE := 0;  --VALOR DEL CONCEPTO QUE CORRESPONDE A LAS VIGENCIAS DE DIFICIL RECAUDO
    MI_SALDOABONO            PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_PORCCPTO              PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_VLABONAR              PCK_SUBTIPOS.TI_DOBLE := 0;  
    MI_TOTALABONADO          PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_VLCPTOABONO           PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_DIFERENCIA            PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_AVALUO                PCK_SUBTIPOS.TI_DOBLE;
    MI_TRPPOR                PCK_SUBTIPOS.TI_PORCENTAJE;
    MI_REEMPLAZOS            PCK_SUBTIPOS.TI_CLAVEVALOR; -- Variable que almacena los reemplazos usados en el control de errores 
  BEGIN
  MI_SALDOABONO := UN_ABONO;
  -- ESTABLECER LA CONDICION DE LAS CONSULTAS
  MI_CONDICION := '           COMPANIA = ''' || UN_COMPANIA  ||
                  '''       AND CODIGO = ''' || UN_CODPREDIO ||
                  ''' AND NUMERO_ORDEN = ''' || GL_NUMORDEN  || 
                  ''' AND PAGADO = 0' ;      
  -- LIMPIAR LA TABLA DE DISTRIBUCIÓN DE ABONOS
  BEGIN
  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSABONOS',
                                         UN_ACCION   =>'E', 
                                         UN_VALORES  =>MI_VALORES, 
                                         UN_CONDICION=>MI_CONDICION 
                                         );

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END ;
  --INICIALIZAR PARAMETROS
  PCK_PARST.PR_INICIALIZAR_PARSISTEMA(UN_COMPANIA=>UN_COMPANIA, 
                                      UN_MODULO  =>GL_MODULO
                                      );
  -- VERIFICA SI EL SISTEMA ESTA CONFIGURADO PARA REALIZAR ABONOS A PARTIR DE UNA VIGENCIA ESPECIFICA
  MI_APLICAABONOVIG := PCK_PARST.FC_PAR(UN_PARAMETRO=>'APLICA ABONOS POR VIGENCIA',
                                        UN_VLOMISION=>''
                                        );
  IF MI_APLICAABONOVIG = 'SI' THEN
    MI_CONDICION := MI_CONDICION ||
                    ' AND INDPAGO_ACPAG = 0
                      AND PREANO >= ' || UN_ANOABONO;
  ELSE 
    MI_CONDICION := MI_CONDICION ||
                    ' AND INDPAGO_ACPAG = 0';
  END IF;
  --VERIFICAR EL CONCEPTO CONFIGURADO PARA ALMACENAR EL VALOR DE DESCUENTO
  MI_CPTODESC := PCK_PARST.FC_PAR(UN_PARAMETRO=>'CONCEPTO DE DESCUENTO',
                                  UN_VLOMISION=>'0'
                                  );

  /*
  -- VERIFICA SI EL SISTEMA ESTA CONFIGURADO PARA TENER EN CUENTA O NO EL VALOR DE DESCUENTOS EN LA DISTRIBUCIÓN 
  MI_APLICADSCT := PCK_PARST.FC_PAR('APLICAR DESCUENTO PARA ABONOS', '');
  IF MI_APLICADSCT = 'NO' THEN
    IF MI_CPTODESC = '0' THEN
      MI_MSG := 'No se encontró configuración del concepto en el cual se calculan los intereses. Por favor verificar el parámetro CONCEPTO DE DESCUENTO';
      PCK_DATOS.GL_ERROR_MSG := MI_MSG ;
      PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONFIGURACION PARÁMETROS','',SQLERRM );
      RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG ); 
    ELSE 
      -- PRIMERO, SE DEBE SUMAR EL VALOR DE INTERESES AL TOTAL DE LAS VIGENCIAS QUE APLIQUEN PARA REALIZAR EL ABONO 
      MI_VALORES := ' TOTAL = TOTAL + ABS(C' || MI_CPTODESC || ')';
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ('IP_FACTURADOS', 'M', MI_VALORES, NULL, NULL, MI_CONDICION );  
      -- SEGUNDO, SE DEBE DEJAR EN CERO EL CAMPO DONDE SE ALMACENA EL VALOR DEL  DESCUENTO   
      MI_VALORES := ' C' || MI_CPTODESC ||' = 0' ;
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ('IP_FACTURADOS', 'M', MI_VALORES, NULL, NULL, MI_CONDICION );                        
    END IF;
  END IF;
  */
  --ADICIONAR ORDENAMIENTO A LA CONSULTA PARA TOMAR DE LA MENOR A LA MAYOR VIGENCIA
  MI_CONDICION := MI_CONDICION ||
             ' ORDER BY PREANO ASC ' ;
  -- CARGAR EN UN CURSOR LAS VIGENCIAS QUE SE PUEDE TOMAR PARA SER AFECTADAS POR EL ABONO
  MI_SQL := ' SELECT PREANO , AVALUO
                   , TRP_POR, TOTAL
                   , C1     , C2
                   , C3     , C4
                   , C5     , C6
                   , C7     , C8
                   , C9     , C10
                   , C11    , C12
                   , C13    , C14
                   , C15    , C16
                   , C17    , C18
                   , C19    , C20    ' ||
            '   FROM   IP_FACTURADOS ' ||
            '  WHERE ' || MI_CONDICION;

  OPEN MI_RSFACTURADOS FOR MI_SQL;
	<<RECORRER_FACTURADOS>>
    LOOP
      FETCH MI_RSFACTURADOS INTO MI_PREANO, MI_AVALUO
                               , MI_TRPPOR, MI_TOTAL
                               , MI_C1    , MI_C2
                               , MI_C3    , MI_C4
                               , MI_C5    , MI_C6
                               , MI_C7    , MI_C8
                               , MI_C9    , MI_C10
                               , MI_C11   , MI_C12
                               , MI_C13   , MI_C14
                               , MI_C15   , MI_C16
                               , MI_C17   , MI_C18
                               , MI_C19   , MI_C20;
      EXIT WHEN MI_RSFACTURADOS%NOTFOUND;
        MI_DEUDAVIGENCIA := MI_TOTAL;
        IF MI_SALDOABONO = 0 THEN
          GOTO SALIR;
        END IF;
        MI_VLABONAR := MI_SALDOABONO;
        --VERIFICA SI EL ABONO ALCANCA A CUBRIR LA TOTALIDAD DE LA VIGENCIA
        IF MI_SALDOABONO >= MI_DEUDAVIGENCIA THEN
          MI_CAMPOS := '  COMPANIA    ,   CODIGO
                        , NUMERO_ORDEN,   PREANO
                        , DOCNUM      ,   PAGADO
                        , C1          ,   C2
                        , C3          ,   C4
                        , C5          ,   C6
                        , C7          ,   C8
                        , C9          ,   C10
                        , C11         ,   C12
                        , C13         ,   C14
                        , C15         ,   C16
                        , C17         ,   C18 
                        , C19         ,   C20
                        , TOTAL       ,   FECHAFACTURADO
                        , AVALUO      ,   TRPPOR
                        , CREATED_BY  ,   DATE_CREATED ';
          MI_VALORES := ''''   || UN_COMPANIA  ||''', '''|| UN_CODPREDIO ||
                        ''','''|| GL_NUMORDEN  ||''', '  || MI_PREANO    ||
                        '  ,'''|| UN_NUMRECIBO ||''',  0, 
                        '      || MI_C1        ||'  , '  || MI_C2        ||
                        '  ,'  || MI_C3        ||'  , '  || MI_C4        ||
                        '  ,'  || MI_C5        ||'  , '  || MI_C6        ||
                        '  ,'  || MI_C7        ||'  , '  || MI_C8        ||
                        '  ,'  || MI_C9        ||'  , '  || MI_C10       ||
                        '  ,'  || MI_C11       ||'  , '  || MI_C12       ||
                        '  ,'  || MI_C13       ||'  , '  || MI_C14       ||
                        '  ,'  || MI_C15       ||'  , '  || MI_C16       ||
                        '  ,'  || MI_C17       ||'  , '  || MI_C18       ||
                        '  ,'  || MI_C19       ||'  , '  || MI_C20       ||
                        '  ,'  || MI_TOTAL     ||'  , TRUNC(SYSDATE)
                           ,'  || MI_AVALUO    ||'  , '  || MI_TRPPOR    ||
                        '  ,'''|| UN_USUARIO   ||''', SYSDATE';

          BEGIN             
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>'IP_FACTURADOSABONOS', 
                                                 UN_ACCION  =>'I',
                                                 UN_CAMPOS  =>MI_CAMPOS,
                                                 UN_VALORES =>MI_VALORES
                                                 ); 

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END ;

          MI_SALDOABONO := MI_SALDOABONO - MI_DEUDAVIGENCIA;
        ELSE       
          --CREAR EL REGISTRO PARA LUEGO IRLO MODIFICANDO CON LOS VALORES QUE CORRESPONDA A CADA CONCEPTO
          MI_CAMPOS := '  COMPANIA
                        , CODIGO
                        , NUMERO_ORDEN
                        , PREANO
                        , DOCNUM
                        , PAGADO
                        , FECHAFACTURADO
                        , TOTAL
                        , AVALUO
                        , TRPPOR
                        , CREATED_BY
                        , DATE_CREATED';
          MI_VALORES := ''''    || UN_COMPANIA  || 
                        ''', '''|| UN_CODPREDIO ||
                        ''', '''|| GL_NUMORDEN  ||
                        ''', '  || MI_PREANO    ||
                        ', '''  || UN_NUMRECIBO ||
                        ''', 0 
                        , TRUNC(SYSDATE)
                        ,0
                        , '     || MI_AVALUO    ||'
                        , '     || MI_TRPPOR    ||
                        ', '''  || UN_USUARIO   ||
                        ''', SYSDATE';             

          BEGIN             
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>'IP_FACTURADOSABONOS',
                                                 UN_ACCION  =>'I', 
                                                 UN_CAMPOS  =>MI_CAMPOS,
                                                 UN_VALORES =>MI_VALORES
                                                 );  

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                     END ;                                      
          --CONDICION PARA REALIZAR LA ACTUALIZACION DE LOS CONCEPTOS QUE VAYAN SIENDO AFECTADOS POR EL ABONO
          MI_CONDICION := '   COMPANIA      = '''|| UN_COMPANIA  ||
                      ''' AND CODIGO        = '''|| UN_CODPREDIO || 
                      ''' AND NUMERO_ORDEN  = '''|| GL_NUMORDEN  ||
                      ''' AND PREANO        = '  || MI_PREANO    ||
                      '   AND DOCNUM        = '''|| UN_NUMRECIBO || ''' ';
          --RECORRE LA CONFIGURACION DE CONCEPTOS PARA INICIALIZAR LOS CONCEPTOS EN CERO DONDE SE REALIZA LA DISTRIBUCIÓN Y 
          --HACER COPIA DE CONCEPTOS DE VIGENCIA ANTERIOR Y DE DIFICIL RECAUDO
		  <<RECORRER_PRIORIDADCPTO>>
          FOR RSCPTO IN(
                          SELECT  CODIGO, VIGANT, VIGDFR 
                            FROM  IP_CONCEPTOS
                           WHERE  COMPANIA  = UN_COMPANIA
                             AND  ANO       = MI_PREANO
                             AND  PRIORIDAD  NOT IN (0)
                           ORDER BY CODIGO
                        )
                        LOOP
            --CONSULTAR EL VALOR DEL CONCEPTO PARA ACTUALIZA EL CAMPO CORRESPONDIENTE EN EL ABONO
            BEGIN
              MI_SQL := ' SELECT C' || RSCPTO.VIGANT ||', C'||RSCPTO.VIGDFR||
                        '   FROM IP_FACTURADOS 
                           WHERE COMPANIA       = ''' ||UN_COMPANIA   || '''
                             AND CODIGO         = ''' || UN_CODPREDIO || '''
                             AND NUMERO_ORDEN   = ''' || GL_NUMORDEN  || '''
                             AND PREANO         = '   || MI_PREANO    || '';
               EXECUTE IMMEDIATE MI_SQL INTO  MI_VLVIGANT,  MI_VLVIGDFR;
               IF RSCPTO.CODIGO <> RSCPTO.VIGANT AND RSCPTO.CODIGO <> RSCPTO.VIGDFR THEN
                  MI_VALORES := '  C' || RSCPTO.CODIGO ||' = 0, 
                                   C' || RSCPTO.VIGANT ||' = ' || MI_VLVIGANT ||
                                ', C' || RSCPTO.VIGDFR ||' = ' || MI_VLVIGDFR ||
                                ', MODIFIED_BY   = '''||UN_USUARIO||'''
                                 , DATE_MODIFIED = SYSDATE ';
               ELSE
                  MI_VALORES := '  C' || RSCPTO.CODIGO ||' = 0
                                 , MODIFIED_BY    = '''||UN_USUARIO||'''
                                 , DATE_MODIFIED  = SYSDATE ';
               END IF;
               BEGIN
               PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSABONOS', 
                                                      UN_ACCION   =>'M', 
                                                      UN_VALORES  =>MI_VALORES,
                                                      UN_CONDICION=>MI_CONDICION 
                                                      ); 

               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                          END ;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                   MI_REEMPLAZOS(0).CLAVE:='UN_CODPREDIO';
                   MI_REEMPLAZOS(0).VALOR:=UN_CODPREDIO;   
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD     => SQLCODE,
                   UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_PROCABONOPREDIO,
                   UN_TABLAERROR  => 'IP_FACTURADOSABONOS',
                   UN_REEMPLAZOS => MI_REEMPLAZOS  
                  );

            END;
          END LOOP RECORRER_PRIORIDADCPTO;
          --VERIFICAR VALOR DE CADA CONCEPTO CON RESPECTO AL SALDO DEL ABONO PARA REALIZAR LA DISTRIBUCIÓN DE LA DEUDA
          -- SE DEBE TENER EN CUENTA LA FORMA DE DISTRIBUICIÓN QUE SE DESEA REALIZAR
          MI_PRORATEA := PCK_PARST.FC_PAR(UN_PARAMETRO=>'PRORATEA CONCEPTOS DE ABONOS', 
                                          UN_VLOMISION=>''
                                          );
        --  IF MI_TIPODISTRIB = 'P' THEN
		  <<RECORRER_PRIORIDADCPTO_1>>
          FOR RSCPTO IN(
                          SELECT  CODIGO, VIGANT, VIGDFR 
                            FROM  IP_CONCEPTOS
                           WHERE  COMPANIA  = UN_COMPANIA
                             AND  ANO       = MI_PREANO
                             AND  PRIORIDAD  NOT IN (0)
                          ORDER BY PRIORIDAD
                        )
                        LOOP
            --CONSULTAR EL VALOR DEL CONCEPTO PARA ACTUALIZA EL CAMPO CORRESPONDIENTE EN EL ABONO
            BEGIN
              MI_SQL := ' SELECT C' || RSCPTO.CODIGO ||
                        '   FROM IP_FACTURADOS 
                           WHERE COMPANIA       = '''|| UN_COMPANIA   || '''
                             AND CODIGO         = '''|| UN_CODPREDIO  || '''
                             AND NUMERO_ORDEN   = '''|| GL_NUMORDEN   || '''
                             AND PREANO         = '  || MI_PREANO     ||'';
              EXECUTE IMMEDIATE MI_SQL INTO  MI_DEUDACPTO;
              IF MI_PRORATEA = 'SI' THEN
                MI_PORCCPTO := MI_DEUDACPTO / MI_DEUDAVIGENCIA; 
                MI_VLCPTO := ROUND(MI_VLABONAR * MI_PORCCPTO,0);
                MI_VALORES := '  C' || RSCPTO.CODIGO ||' = '  || MI_VLCPTO ||
                                ',TOTAL = TOTAL          + '  || MI_VLCPTO ||
                                ', MODIFIED_BY           = '''|| UN_USUARIO||'''
                                 , DATE_MODIFIED         = SYSDATE ';

                BEGIN                 
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSABONOS',
                                                       UN_ACCION   =>'M', 
                                                       UN_VALORES  =>MI_VALORES,
                                                       UN_CONDICION=>MI_CONDICION
                                                       ); 

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                           END ;

                MI_VLCPTOABONO := MI_VLCPTO;
                MI_SQL := ' SELECT TOTAL 
                              FROM IP_FACTURADOSABONOS 
                             WHERE COMPANIA       = ''' || UN_COMPANIA  || '''
                               AND CODIGO         = ''' || UN_CODPREDIO || '''
                               AND NUMERO_ORDEN   = ''' || GL_NUMORDEN  || '''
                               AND PREANO         = '   || MI_PREANO    || '';
                EXECUTE IMMEDIATE MI_SQL INTO  MI_TOTALABONADO;
                IF MI_TOTALABONADO > MI_VLABONAR THEN
                  MI_DIFERENCIA := MI_TOTALABONADO - MI_VLABONAR;
                  MI_VALORES := '  C' || RSCPTO.CODIGO ||' = C' || RSCPTO.CODIGO ||' - '|| MI_DIFERENCIA ||
                                ', TOTAL = TOTAL - ' || MI_DIFERENCIA ||
                                ', MODIFIED_BY  =  '''||UN_USUARIO    ||'''
                                 , DATE_MODIFIED = SYSDATE ';

                  BEGIN               
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSABONOS', 
                                                         UN_ACCION   =>'M', 
                                                         UN_VALORES  =>MI_VALORES, 
                                                         UN_CONDICION=>MI_CONDICION 
                                                         ); 

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                             END ;   

                  MI_VLCPTOABONO := MI_VLCPTO - MI_DIFERENCIA;
                END IF;
                MI_SALDOABONO := MI_SALDOABONO - MI_VLCPTOABONO;
                IF MI_SALDOABONO = 1 AND (MI_VLCPTOABONO + MI_SALDOABONO) <= MI_DEUDACPTO THEN
                  MI_VALORES := '  C' || RSCPTO.CODIGO ||' = C' || RSCPTO.CODIGO ||' + '|| MI_SALDOABONO ||
                                ',TOTAL = TOTAL + '  || MI_SALDOABONO ||
                                ', MODIFIED_BY  = '''|| UN_USUARIO    ||'''
                                 , DATE_MODIFIED = SYSDATE ';

                  BEGIN               
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSABONOS',
                                                         UN_ACCION   =>'M',
                                                         UN_VALORES  =>MI_VALORES, 
                                                         UN_CONDICION=>MI_CONDICION
                                                         ); 
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                             END ; 

                  MI_SALDOABONO := MI_SALDOABONO - MI_SALDOABONO;
                END IF;
              ELSE
                IF MI_SALDOABONO >= ABS(MI_DEUDACPTO) THEN
                  MI_VALORES := '  C' || RSCPTO.CODIGO ||' = '|| MI_DEUDACPTO ||
                                ',TOTAL = TOTAL  + '  || MI_DEUDACPTO ||
                                ', MODIFIED_BY   = '''|| UN_USUARIO   ||'''
                                 , DATE_MODIFIED = SYSDATE ';

                  BEGIN               
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSABONOS',
                                                         UN_ACCION   =>'M', 
                                                         UN_VALORES  =>MI_VALORES, 
                                                         UN_CONDICION=>MI_CONDICION
                                                         ); 
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                             END ; 

                  MI_SALDOABONO := MI_SALDOABONO - MI_DEUDACPTO;
                ELSE
                  MI_VALORES := '  C' || RSCPTO.CODIGO ||' = '|| MI_SALDOABONO ||
                                ', TOTAL = TOTAL + '  || MI_SALDOABONO ||
                                ', MODIFIED_BY   = '''|| UN_USUARIO    ||'''
                                 , DATE_MODIFIED = SYSDATE ';

                  BEGIN               
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSABONOS', 
                                                         UN_ACCION   =>'M', 
                                                         UN_VALORES  =>MI_VALORES, 
                                                         UN_CONDICION=>MI_CONDICION
                                                         ); 

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                             END ;   

                  MI_SALDOABONO := 0;
                END IF;                 
              END IF;                
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                   MI_REEMPLAZOS(0).CLAVE:='UN_CODPREDIO';
                   MI_REEMPLAZOS(0).VALOR:=UN_CODPREDIO;   
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD     => SQLCODE,
                   UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_PROCABONOPREDIO,
                   UN_TABLAERROR  => 'IP_FACTURADOSABONOS',
                   UN_REEMPLAZOS => MI_REEMPLAZOS  
                  );
            END;
          END LOOP RECORRER_PRIORIDADCPTO_1;
        END IF;      
    END LOOP RECORRER_FACTURADOS;
    <<SALIR>>   
  CLOSE MI_RSFACTURADOS;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_REPARTIRDEUDAPROR
  );
END PR_REPARTIRDEUDAPRORATEADA;      
--6 
FUNCTION PR_IMPRESORAABONO
  /*
  NAME              : PR_IMPRESORAABONO  --> EN ACCESS ImpresoraAbono (Formulario RECIBO_PRE_FUSA)
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE MIGRADOR     : 12/09/2016
  TIME              : 08:40 AM
  SOURCE MODULE     : PredialP2016.05.06
  DESCRIPTION       : Procedimiento que genera el registro de la factura de abono
  @NAME: impresoraAbono
  @METHOD: POST
  */
  (
  -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe la fecha de corte del abono
  UN_FECHACORTE              IN DATE,
  -- Parametro que recibe el ano del abono que se genera la factura
  UN_ANOABONO                IN PCK_SUBTIPOS.TI_ANIO,
  -- Parametro que recibe el codigo del predio al cual se le genera la factura
  UN_CODPREDIO               IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el valor del abono que se genera en la factura
  UN_ABONO                   IN PCK_SUBTIPOS.TI_DOBLE,
  -- Parametro que recibe el nombre del usuario al que se le genera la factura
  UN_USUARIO                 IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN VARCHAR2
  AS
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 6;
    MI_ANOCORTE              PCK_SUBTIPOS.TI_ANIO := TO_NUMBER(TO_CHAR(UN_FECHACORTE , 'YYYY'));  
    MI_TIPOFECHAABONOS       PCK_SUBTIPOS.TI_PARAMETRO;
    MI_FECHAVENCIMIENTO      DATE;
    MI_MANEJAFACTUNICA       PCK_SUBTIPOS.TI_PARAMETRO;
    MI_TIPOFACTURA           PCK_SUBTIPOS.TI_PARAMETRO;
    MI_NUMRECIBO             PCK_SUBTIPOS.TI_DOCNUM;
    MI_DIGRECIBO             PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    MI_EXISTE                VARCHAR2(32000 CHAR);
    MI_TIPODISTRIBUCION      PCK_SUBTIPOS.TI_PARAMETRO;
    MI_ABONOPREANOI          PCK_SUBTIPOS.TI_ANIO;
    MI_ABONOPREANOF          PCK_SUBTIPOS.TI_ANIO;
    MI_STR                   PCK_SUBTIPOS.TI_STRSQL;
    MI_RSFACTABONOS          SYS_REFCURSOR;
    MI_TOTAL                 PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_CONTROLRECIBO         PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CSC                   PCK_SUBTIPOS.TI_ENTERO;
    MI_CCONCEPTOS            TYPE_CONCEPTOS;
    MI_REEMPLAZOS            PCK_SUBTIPOS.TI_CLAVEVALOR; -- Variable que almacenara los reemplazos en el control de errores
    MI_ANIOABONO             PCK_SUBTIPOS.TI_ANIO; -- RECIBE EL AÑO HASTA EL CUAL ALCANZO EL ABONO . FUNCION REPARTIRABONO 
  BEGIN
  --INICIALIZAR PARAMETROS
  PCK_PARST.PR_INICIALIZAR_PARSISTEMA(UN_COMPANIA=>UN_COMPANIA, 
                                      UN_MODULO  =>GL_MODULO
                                      );
  -- VERIFICA SI EL SISTEMA ESTA CONFIGURADO PARA REALIZAR ABONOS A PARTIR DE UNA VIGENCIA ESPECIFICA
  MI_TIPOFECHAABONOS := PCK_PARST.FC_PAR(UN_PARAMETRO=>'TIPO FECHA AL DIA PARA ABONOS',
                                         UN_VLOMISION=>''
                                         );
  IF MI_TIPOFECHAABONOS = 'MORAACUMULADAMENSUAL' THEN
    MI_FECHAVENCIMIENTO := LAST_DAY(TRUNC(SYSDATE));
  ELSE
    MI_FECHAVENCIMIENTO := TRUNC(SYSDATE);
  END IF;

  MI_MANEJAFACTUNICA := PCK_PARST.FC_PAR(UN_PARAMETRO=>'MANEJA NUMERACION UNICA',
                                         UN_VLOMISION=>'SI'
                                         );
  IF MI_MANEJAFACTUNICA = 'SI' THEN
    MI_TIPOFACTURA := 'N';
  ELSE
    MI_TIPOFACTURA := 'B';
  END IF;
  <<OTRO_CONSECUTIVO>>  
  --OBTENER NUMERO DE FACTURA
  BEGIN
    SELECT  CONSECUTIVOREAL
      INTO  MI_NUMRECIBO 
      FROM  IP_NUMEROSDEFACTURA
     WHERE  COMPANIA = UN_COMPANIA
       AND  TIPO     = MI_TIPOFACTURA
       AND  ACTIVO   NOT IN (0);

    EXCEPTION WHEN OTHERS THEN
      MI_NUMRECIBO := '0';
  END;
  MI_NUMRECIBO := MI_NUMRECIBO + 1;
  --VERIFICAR LA LONGITUD QUE DEBE TENER EL RECIBO DE PAGO
  BEGIN
    SELECT DIGITOS
      INTO MI_DIGRECIBO
      FROM IP_TIPOSNUMERACION
     WHERE CODIGO = MI_TIPOFACTURA;

    EXCEPTION WHEN OTHERS THEN
      MI_DIGRECIBO := 9;
  END;
  --VALIDAR EL CONSECUTIVO A EVALUAR
  MI_NUMRECIBO := PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO  =>MI_NUMRECIBO, 
                                            UN_LONGITUD=>MI_DIGRECIBO
                                            );
  --ACTUALIZAR EL CONSECUTIVO EN LA TABLA IP_NUMEROSDEFACTURA
  MI_VALORES := ' CONSECUTIVOREAL = ''' || MI_NUMRECIBO || '''
                 , MODIFIED_BY    = ''' || UN_USUARIO   || '''
                 , DATE_MODIFIED  = SYSDATE ';
  MI_CONDICION := '   COMPANIA    = ''' || UN_COMPANIA    ||
                  ''' AND TIPO    = ''' || MI_TIPOFACTURA || 
                  ''' AND ACTIVO   NOT IN  (0)'; 

  BEGIN                
  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA=>'IP_NUMEROSDEFACTURA',
                                         UN_ACCION=>'M',
                                         UN_CAMPOS=>MI_VALORES, 
                                         UN_CONDICION=>MI_CONDICION 
                                         ); 

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
             END ;                                       
  --
  --SE VERIFICA SI EN LA TABLA DE RECIBOS DE PAGO YA EXISTE UNA FACTURA CON ESTE NUMERO
  BEGIN
    SELECT 'X' 
      INTO MI_EXISTE 
      FROM IP_RECIBOS_DE_PAGO
     WHERE COMPANIA = UN_COMPANIA
       AND DOCNUM   = MI_NUMRECIBO;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_EXISTE := 'N';
  END;
  --SI EXISTE EL NUMERO DE FACTURA SE DEBE REGRESAR A OBTENER OTRO CONSECUTIVO
  IF MI_EXISTE = 'X' THEN
    GOTO OTRO_CONSECUTIVO;
  ELSE -- SE CREA EL HEADER DEL RECIBO 
    MI_CAMPOS := '  COMPANIA
                  , DOCNUM
                  , PRECOD
                  , NUMERO_ORDEN
                  , PREANO
                  , PREANOI
                  , PREANOF
                  , CREATED_BY
                  , DATE_CREATED  ';
    MI_VALORES := ''''     || UN_COMPANIA  || 
                  ''', ''' || MI_NUMRECIBO ||
                  ''', ''' || UN_CODPREDIO || 
                  ''', ''' || GL_NUMORDEN  ||
                  ''', '   || UN_ANOABONO  ||
                  '  , '   || UN_ANOABONO  ||
                  '  , '   || UN_ANOABONO  ||
                  '  , ''' || UN_USUARIO   || 
                  ''', SYSDATE';
    BEGIN             
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA  =>'IP_RECIBOS_DE_PAGO',
                                           UN_ACCION =>'I',
                                           UN_CAMPOS =>MI_CAMPOS, 
                                           UN_VALORES=>MI_VALORES
                                           ); 

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
             RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
               END ;                                      
  END IF;
  --GENERAR DISTRIBUICIÓN DEL ABONO
  MI_TIPODISTRIBUCION := PCK_PARST.FC_PAR(UN_PARAMETRO=>'DISTRIBUYE ABONO POR PRORATEO (R), PRIORIDAD (P)',
                                          UN_VLOMISION=>'P'
                                          );
  IF MI_TIPODISTRIBUCION = 'R' THEN
    PR_REPARTIRDEUDAPRORATEADA (UN_COMPANIA  =>UN_COMPANIA,
                                UN_CODPREDIO =>UN_CODPREDIO, 
                                UN_ANOABONO  =>UN_ANOABONO,
                                UN_ABONO     =>UN_ABONO, 
                                UN_NUMRECIBO =>MI_NUMRECIBO,
                                UN_USUARIO   =>UN_USUARIO
                                );
  ELSE 
     MI_ANIOABONO := FC_REPARTIRDEUDA (UN_COMPANIA  =>UN_COMPANIA,
                            UN_CODPREDIO =>UN_CODPREDIO,
                            UN_ANOABONO  =>UN_ANOABONO, 
                            UN_ABONO     =>UN_ABONO,
                            UN_NUMRECIBO =>MI_NUMRECIBO,
                            UN_INDACUERDO=>0,
                            UN_USUARIO   =>UN_USUARIO
                            );
  END IF;
  --ACTUALIZAR EL HEADER DE LA FACTURA
  BEGIN

    SELECT MAX(PREANO) 
      INTO MI_ABONOPREANOF
      FROM IP_FACTURADOSABONOS
     WHERE COMPANIA     = UN_COMPANIA
       AND CODIGO       = UN_CODPREDIO
       AND NUMERO_ORDEN = GL_NUMORDEN
       AND PAGADO       = 0 ;

    EXCEPTION WHEN NO_DATA_FOUND THEN 
                   MI_REEMPLAZOS(0).CLAVE:='UN_CODPREDIO';
                   MI_REEMPLAZOS(0).VALOR:=UN_CODPREDIO;   
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD     => SQLCODE,
                   UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_PROCABONOPREDIO,
                   UN_TABLAERROR  => 'IP_FACTURADOSABONOS',
                   UN_REEMPLAZOS => MI_REEMPLAZOS  
       );
      END;

  --CARGAR CONFIGURACION DE CONCEPTOS
  BEGIN
    MI_CCONCEPTOS := PCK_PREDIAL_COM3.FC_CONFIGURARCONCEPTOS(UN_COMPANIA => UN_COMPANIA,
                                                             UN_ANOCORTE => MI_ANOCORTE
                                                             );
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_CONFCONCEPTOS
            ); 
  END;
  IF MI_EXISTE = 'MUNICIPIO DE SOGAMOSO' THEN
    MI_STR := '  SELECT CASE WHEN PREANO =  '  || MI_ANOCORTE || ' THEN C1 ELSE 0 END CC1 
                      , CASE WHEN PREANO =  '  || MI_ANOCORTE || ' THEN C2 ELSE 0 END CC2 
                      , CASE WHEN PREANO =  '  || MI_ANOCORTE || ' THEN C3 ELSE 0 END CC3 
                      , CASE WHEN PREANO =  '  || MI_ANOCORTE || ' THEN C4 ELSE 0 END CC4 
                      , CASE WHEN PREANO =  '  || MI_ANOCORTE || '- 1  THEN C1 ELSE 0 END CC5
                      , CASE WHEN PREANO =  '  || MI_ANOCORTE || '- 1  THEN C2 ELSE 0 END CC6
                      , CASE WHEN PREANO =  '  || MI_ANOCORTE || '- 1  THEN C3 ELSE 0 END CC7
                      , CASE WHEN PREANO =  '  || MI_ANOCORTE || '- 1  THEN C4 ELSE 0 END CC8 
                      , CASE WHEN PREANO <= '  || MI_ANOCORTE || '- 2  THEN C1 ELSE 0 END CC9
                      , CASE WHEN PREANO <= '  || MI_ANOCORTE || '- 2  THEN C2 ELSE 0 END CC10
                      , CASE WHEN PREANO <= '  || MI_ANOCORTE || '- 2  THEN C3 ELSE 0 END CC11 
                      , CASE WHEN PREANO <= '  || MI_ANOCORTE || '- 2  THEN C4 ELSE 0 END CC12 
                      , SUM(C13) CC13,         SUM(C14) CC14,    SUM(C15) CC15,  SUM(C16) CC16
                      , CASE WHEN PREANO =  '  || MI_ANOCORTE || ' THEN C17 ELSE 0 END CC17 
                      , CASE WHEN PREANO =  '  || MI_ANOCORTE || '- 1 THEN C18 ELSE 0 END CC18 
                      , CASE WHEN PREANO <= '  || MI_ANOCORTE || '- 2 THEN C19 ELSE 0 END CC19
                      , SUM(C20) CC20 
                  FROM IP_FACTURADOSABONOS 
                 WHERE COMPANIA      = ''' || UN_COMPANIA  || 
              '''  AND CODIGO        = ''' || UN_CODPREDIO || 
              '''  AND NUMERO_ORDEN  = ''' || GL_NUMORDEN  || 
              '''  AND PAGADO        = 0  ';    
  ELSE
    MI_STR := ' SELECT SUM(CASE WHEN PREANO =  ' || MI_ANOCORTE || ' THEN C1 ELSE 0 END) CC1
                     , SUM(CASE WHEN PREANO =  ' || MI_ANOCORTE || ' THEN C2 ELSE 0 END) CC2 
                     , SUM(CASE WHEN PREANO =  ' || MI_ANOCORTE || ' THEN C3 ELSE 0 END) CC3 
                     , SUM(CASE WHEN PREANO =  ' || MI_ANOCORTE || ' THEN C4 ELSE 0 END) CC4 
                     , SUM(CASE WHEN PREANO =  ' || MI_ANOCORTE || '- 1 THEN C1 ELSE 0 END) CC5 
                     , SUM(CASE WHEN PREANO =  ' || MI_ANOCORTE || '- 1  THEN C2 ELSE 0 END) CC6 
                     , SUM(CASE WHEN PREANO =  ' || MI_ANOCORTE || '- 1  THEN C3 ELSE 0 END) CC7
                     , SUM(CASE WHEN PREANO =  ' || MI_ANOCORTE || ' - 1  THEN C4 ELSE 0 END) CC8 
                     , SUM(CASE WHEN PREANO <= ' || MI_ANOCORTE || '- 2  THEN C1 ELSE 0 END) CC9 
                     , SUM(CASE WHEN PREANO <= ' || MI_ANOCORTE || '- 2  THEN C2 ELSE 0 END) CC10 
                     , SUM(CASE WHEN PREANO <= ' || MI_ANOCORTE || '- 2  THEN C3 ELSE 0 END) CC11
                     , SUM(CASE WHEN PREANO <= ' || MI_ANOCORTE || '- 2  THEN C4 ELSE 0 END) CC12 
                     , SUM(C13) CC13,        SUM(C14) CC14,      SUM(C15) CC15,      SUM(C16) CC16
                     , SUM(C17) CC17,        SUM(C18) CC18,      SUM(C19) CC19,      SUM(C20) CC20  
                  FROM IP_FACTURADOSABONOS 
                 WHERE COMPANIA      = ''' || UN_COMPANIA  || 
              '''  AND CODIGO        = ''' || UN_CODPREDIO || 
              '''  AND NUMERO_ORDEN  = ''' || GL_NUMORDEN  || 
              '''  AND PAGADO        = 0  ';    
  END IF;
  OPEN MI_RSFACTABONOS FOR MI_STR;
	<<RECORRER_FACTURADOS>>
    LOOP
      FETCH MI_RSFACTABONOS INTO CPTO(1) , CPTO(2) ,
                                 CPTO(3) , CPTO(4) , 
                                 CPTO(5) , CPTO(6) ,
                                 CPTO(7) , CPTO(8) , 
                                 CPTO(9) , CPTO(10), 
                                 CPTO(11), CPTO(12),
                                 CPTO(13), CPTO(14),
                                 CPTO(15), CPTO(16),
                                 CPTO(17), CPTO(18),
                                 CPTO(19), CPTO(20);
      EXIT WHEN MI_RSFACTABONOS%NOTFOUND;
		<<CALCULAR_TOTALDEUDA>>   
    BEGIN
        FOR I IN CCONCEPTOS.FIRST .. CCONCEPTOS.LAST LOOP
          IF CCONCEPTOS(I).CODIGO = I THEN
            IF CCONCEPTOS(I).INFORMATIVO = 0 THEN
              MI_TOTAL := MI_TOTAL + FC_CPTO(I);

            END IF;
          END IF;
        END LOOP CALCULAR_TOTALDEUDA;      

     EXCEPTION WHEN NO_DATA_FOUND THEN 
                   MI_REEMPLAZOS(0).CLAVE:='UN_CODPREDIO';
                   MI_REEMPLAZOS(0).VALOR:=UN_CODPREDIO;   
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD     => SQLCODE,
                   UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_PROCABONOPREDIO,
                   UN_TABLAERROR  => 'IP_FACTURADOSABONOS',
                   UN_REEMPLAZOS => MI_REEMPLAZOS  
       );
      END;
      END LOOP RECORRER_FACTURADOS;
  CLOSE MI_RSFACTABONOS;

  -- VERIFICAR SI EL PREDIO TIENE RECIBOS PENDIENTES
  BEGIN
    SELECT  DISTINCT 'X'
      INTO  MI_EXISTE
      FROM  IP_RECIBOS_DE_PAGO
     WHERE  COMPANIA      = UN_COMPANIA
       AND  DOCNUM        <> MI_NUMRECIBO
       AND  PRECOD        = UN_CODPREDIO 
       AND  NUMERO_ORDEN  = GL_NUMORDEN
       AND  PAGO          = 0 
       AND  ANULADO       = 0;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_EXISTE := 'N';
  END ;
  IF MI_EXISTE = 'X' THEN
    MI_CONTROLRECIBO:= PCK_PARST.FC_PAR(UN_PARAMETRO=>'CONTROLAR RECIBOS POR USUARIO',
                                        UN_VLOMISION=>'NO'
                                        );
    IF MI_CONTROLRECIBO = 'SI' THEN
      MI_VALORES := '  ANULADO        =   -1
                     , FECHAANULACION = TRUNC(SYSDATE)
                     , ANULADO_POR    = '''||UN_USUARIO||'''
                     , MODIFIED_BY    = '''||UN_USUARIO||'''
                     , DATE_MODIFIED = SYSDATE ';
      MI_CONDICION := '   COMPANIA          = ''' || UN_COMPANIA  || 
                      ''' AND DOCNUM       <> ''' || MI_NUMRECIBO ||
                      ''' AND PRECOD        = ''' || UN_CODPREDIO || 
                      ''' AND NUMERO_ORDEN  = ''' || GL_NUMORDEN  || 
                      ''' AND ANULADO       = 0 
                          AND PAGO          = 0 
                          AND ESACUERDO     = 0
                          AND ACUERDO       IS NULL'; 

      BEGIN                    
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_RECIBOS_DE_PAGO', 
                                             UN_ACCION   =>'M', 
                                             UN_VALORES  =>MI_VALORES, 
                                             UN_CONDICION=>MI_CONDICION
                                             );

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                 END ;                                       
    END IF;
  END IF;
  -- SE DEBE ACTUALIZAR EL HEADER DEL RECIBO CON LOS DATOS FINALES LUEGO DE LA DISTRIBUCIÓN DEL ABONO
  MI_VALORES := '    PREANO    = '   || MI_ABONOPREANOF     || 
                '  , PREFEC    = ''' || UN_FECHACORTE       || 
                ''', PREVAL    = '   || MI_TOTAL            || 
                '  , PREFECLIM = ''' || MI_FECHAVENCIMIENTO ||
                ''', C1        = '   || FC_CPTO(1)          ||', C2  = ' ||FC_CPTO(2) ||
                '  , C3        = '   || FC_CPTO(3)          ||', C4  = ' ||FC_CPTO(4) ||
                '  , C5        = '   || FC_CPTO(5)          ||', C6  = ' ||FC_CPTO(6) ||
                '  , C7        = '   || FC_CPTO(7)          ||', C8  = ' ||FC_CPTO(8) ||
                '  , C9        = '   || FC_CPTO(9)          ||', C10 = ' ||FC_CPTO(10)||
                '  , C11       = '   || FC_CPTO(11)         ||', C12 = ' ||FC_CPTO(12)||
                '  , C13       = '   || FC_CPTO(13)         ||', C14 = ' ||FC_CPTO(14)||
                '  , C15       = '   || FC_CPTO(15)         ||', C16 = ' ||FC_CPTO(16)||
                '  , C17       = '   || FC_CPTO(17)         ||', C18 = ' ||FC_CPTO(18)||
                '  , C19       = '   || FC_CPTO(19)         ||', C20 = ' ||FC_CPTO(20)||
                '  , PREANOI   = '   || UN_ANOABONO         ||', PREANOF = '||MI_ABONOPREANOF||
                '  , ESABONO   = -1,  PREUSU = '''||UN_USUARIO||''', IND_MULTIFECHAS = 0 
                   , MODIFIED_BY = '''||UN_USUARIO||''', DATE_MODIFIED = SYSDATE ';

  MI_CONDICION := '    COMPANIA      = ''' || UN_COMPANIA || 
                  ''' AND DOCNUM     = ''' || MI_NUMRECIBO || ''''; 

  BEGIN                
  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_RECIBOS_DE_PAGO',
                                         UN_ACCION   =>'M',
                                         UN_VALORES  =>MI_VALORES, 
                                         UN_CONDICION=>MI_CONDICION 
                                         );

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
             END ;                                      
  --CREAR LOS DETALLES DEL RECIBO DE PAGO
  MI_CSC := 0;
  <<CREAR_DETABONO>>
  FOR RSFACTABONOS IN (
                        SELECT COMPANIA, DOCNUM
                             , PREANO  , AVALUO
                             , TRPPOR  , C1 
                             , C2      , C3
                             , C4      , C5 
                             , C6      , C7 
                             , C8      , C9
                             , C10     , C11
                             , C12     , C13
                             , C14     , C15
                             , C16     , C17
                             , C18     , C19 
                             , C20     , TOTAL
                          FROM IP_FACTURADOSABONOS
                         WHERE COMPANIA       =  UN_COMPANIA
                           AND CODIGO         =  UN_CODPREDIO
                           AND NUMERO_ORDEN   =  GL_NUMORDEN
                           AND DOCNUM         =  MI_NUMRECIBO
                           AND PAGADO         =  0
                      )
                      LOOP
    MI_CSC := MI_CSC + 1;
    MI_CAMPOS := '  COMPANIA   , DOCNUM  , CONSECUTIVO
                  , PREANO     , AVALUO  , TRPPOR
                  , C1         , C2      , C3 
                  , C4         , C5      , C6
                  , C7         , C8      , C9
                  , C10        , C11     , C12
                  , C13        , C14     , C15 
                  , C16        , C17     , C18
                  , C19        , C20     , TOTAL
                  , CREATED_BY , DATE_CREATED';
    MI_VALORES := ''''||RSFACTABONOS.COMPANIA||''', '''||RSFACTABONOS.DOCNUM||''', '||MI_CSC          ||', '||RSFACTABONOS.PREANO||', '||RSFACTABONOS.AVALUO||','||RSFACTABONOS.TRPPOR||
                   ','||RSFACTABONOS.C1      ||'  , '  ||RSFACTABONOS.C2    ||'  , '||RSFACTABONOS.C3 ||', '||RSFACTABONOS.C4    ||', '||RSFACTABONOS.C5    ||
                   ','||RSFACTABONOS.C6      ||'  , '  ||RSFACTABONOS.C7    ||'  , '||RSFACTABONOS.C8 ||', '||RSFACTABONOS.C9    ||', '||RSFACTABONOS.C10   ||
                   ','||RSFACTABONOS.C11     ||'  , '  ||RSFACTABONOS.C12   ||'  , '||RSFACTABONOS.C13||', '||RSFACTABONOS.C14   ||', '||RSFACTABONOS.C15   ||
                   ','||RSFACTABONOS.C16     ||'  , '  ||RSFACTABONOS.C17   ||'  , '||RSFACTABONOS.C18||', '||RSFACTABONOS.C19   ||', '||RSFACTABONOS.C20   ||
                   ','||RSFACTABONOS.TOTAL   ||'  , '''||UN_USUARIO         ||''', SYSDATE   ';

    BEGIN               
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>'IP_DETALLE_RECIBOPAGO',
                                           UN_ACCION  =>'I',
                                           UN_CAMPOS  =>MI_CAMPOS,
                                           UN_VALORES =>MI_VALORES
                                           ); 

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                   END ;  

  END LOOP CREAR_DETABONO;
  --ACTUALIZAR EL NUMERO DE FACTURA DE ABONO EN EL PREDIO 
  MI_VALORES   := 'NUMERO_FACTURA = '''||MI_NUMRECIBO  ||''', RECIBO_ACTUAL  = '''||MI_NUMRECIBO||'''';
  MI_CONDICION := '   COMPANIA    = ''' ||UN_COMPANIA  || 
                  ''' AND CODIGO  = ''' ||UN_CODPREDIO ||'''';

  BEGIN                
  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_USUARIOS_PREDIAL',
                                         UN_ACCION   =>'M',
                                         UN_VALORES  =>MI_VALORES,
                                         UN_CONDICION=>MI_CONDICION 
                                         ); 

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                   END ; 

  RETURN MI_NUMRECIBO;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD =>SQLCODE,
                  UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_GEN_FACTURAABONO
   );
END PR_IMPRESORAABONO; 

FUNCTION FC_CONFIGURARCONCEPTOS
  /*
  NAME              : FC_CONFIGURARCONCEPTOS  
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE MIGRADOR     : 12/09/2016
  TIME              : 02:30 PM
  SOURCE MODULE     : 
  DESCRIPTION       : Procedimiento que genera carga la configuracion de conceptos para una compania y vigencia ingresados par parámetro
  @NAME: configurarConceptos
  @METHOD: GET
  */
  (
  -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el ano en el que se esta configurando el concepto
  UN_ANOCORTE                IN PCK_SUBTIPOS.TI_ANIO
  )
  RETURN TYPE_CONCEPTOS
  AS  
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 7;
    MI_STR                   PCK_SUBTIPOS.TI_STRSQL;
    MI_CCONCEPTOS            TYPE_CONCEPTOS;
  BEGIN
  MI_STR := 'SELECT *
               FROM IP_CONCEPTOS
              WHERE COMPANIA = '''              || UN_COMPANIA ||
                               '''  AND ANO = ' || UN_ANOCORTE ; 
  EXECUTE IMMEDIATE MI_STR BULK  COLLECT INTO MI_CCONCEPTOS;
  IF MI_CCONCEPTOS.COUNT = 0 THEN
    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
  END IF;
  RETURN MI_CCONCEPTOS;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_CONFIG_CONCEPTOS,
                 UN_TABLAERROR =>'IP_CONCEPTOS'
  );  
END FC_CONFIGURARCONCEPTOS;

--8
FUNCTION FC_CPTO
  /*
  NAME              : FC_CPTO
  AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE              : 12/09/2016
  TIME              : 03:05 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       :  Recibe el codigo del concepto a consultar el valor en el vector. Se evalua si la posición existe y retorna el 
                       valor almacenado o cero 
  @NAME: consultarCodigoConcepto
  @METHOD: GET
  */
  (
  -- Parametro que recibe el codigo de concepto a consultar el valor. 
  UN_CONCEPTO                IN PCK_SUBTIPOS.TI_ENTERO
  )
  RETURN NUMBER
  AS
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 8;
    MI_VALOR                 PCK_SUBTIPOS.TI_DOBLE:=0;
  BEGIN
  BEGIN
    IF CPTO.EXISTS(UN_CONCEPTO) THEN  
      MI_VALOR := CPTO(UN_CONCEPTO);
      IF MI_VALOR IS NULL THEN
        MI_VALOR:=0 ;
      END IF;
    END IF;
  EXCEPTION WHEN OTHERS THEN
    MI_VALOR:=0;
  END;
  RETURN MI_VALOR;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_CONSULCOD_CONCEP
  ); 

END FC_CPTO ;

-- 9
PROCEDURE PR_ASIGNAPORC_RESERVA
  /*
  NAME              : PR_ASIGNAPORC_RESERVA
  AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE              : 14/09/2016
  TIME              : 05:00 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       :  Procedimiento que se encarga de asignar el porcentaje de reserva a un predio     
  @NAME: asignarPorcReserva
  @METHOD: POST
  */
  (
  -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe la fecha de corte al asignar el porcentaje de reserva al predio
  UN_FECHACORTE              IN DATE,
  -- Parametro que recibe el nombre del usuario 
  UN_USUARIO                 IN PCK_SUBTIPOS.TI_USUARIO,
  -- Parametro que recibe el codigo del predio al que se le asigna el porcentaje
  UN_CODPREDIO               IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el valor del indicador si aplica descuento o no 
  UN_APLICADESC              IN PCK_SUBTIPOS.TI_LOGICO,
  -- Parametro que recibe el valor del indicador de reserva
  UN_INDRESERVA              IN PCK_SUBTIPOS.TI_LOGICO,
  -- Parametro que recibe el valor del porcentaje de reserva 
  UN_PORCRESERVA             IN PCK_SUBTIPOS.TI_PORCENTAJE, 
  -- Parametro que recibe el ano del que se va a realizar el pago
  UN_PAGOANO                 IN PCK_SUBTIPOS.TI_ANIO,
  -- Parametro que recibe el valor del indicador de calculo serie
  UN_CALCULOSERIE            IN PCK_SUBTIPOS.TI_LOGICO := 0,
  -- Parametro que recibe el valor del indicador de ley1066
  UN_LEY1066                 IN PCK_SUBTIPOS.TI_LOGICO := 0,
  -- Parametro que recibe el valor del indicador de ley1175
  UN_LEY1175                 IN PCK_SUBTIPOS.TI_LOGICO := 0
  )
  AS
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 9;
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
    MI_CICLO                 PCK_SUBTIPOS.TI_ENTERO;
    MI_ANOCORTE              PCK_SUBTIPOS.TI_ANIO;
    MI_FACT_PORCRESERVA      PCK_SUBTIPOS.TI_PORCENTAJE;
    MI_FACT_RESRESERVA       IP_FACTURADOS.RESOLUCION_RESERVA%TYPE;
    MI_CONSECUTIVO           PCK_SUBTIPOS.TI_ENTERO:= 0;
    MI_ANOPROCESO            PCK_SUBTIPOS.TI_ANIO:= 0;
  BEGIN
  MI_ANOCORTE := TO_NUMBER(TO_CHAR(UN_FECHACORTE, 'YYYY'));
  MI_ANOPROCESO := PCK_SYSMAN_UTL.FC_IIF(UN_PAGOANO > GL_ANORESERVA - 1, UN_PAGOANO, GL_ANORESERVA - 1);
  IF UN_PAGOANO <  MI_ANOCORTE THEN
    MI_CONDICION := '     COMPANIA     = '''||UN_COMPANIA ||'''
                      AND CODIGO       = '''||UN_CODPREDIO||'''
                      AND NUMERO_ORDEN = '''||GL_NUMORDEN ||'''
                      AND PREANO       >   '||UN_PAGOANO; 

    BEGIN                  
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSRESERVA',
                                           UN_ACCION   =>'E', 
                                           UN_CONDICION=>MI_CONDICION 
                                           ); 

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                   END ;                   

    MI_CICLO := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION=>UN_PORCRESERVA<1,
                                      UN_SI=>1,
                                      UN_NO=>0
                                      );
	<<CICLOS_RESERVA>>
    FOR I IN 0 ..MI_CICLO LOOP
      -- CALPRED_USUARIO
      IF I = 0 THEN
        MI_VALORES := ' CONSECUTIVO = ' || I || '+ 1
                      , MODIFIED_BY        = '''||UN_USUARIO    ||'''
                      , DATE_MODIFIED      = SYSDATE';
        MI_CONDICION := 'COMPANIA          = '''||UN_COMPANIA   ||'''
                         AND CODIGO        = '''||UN_CODPREDIO  ||'''
                         AND NUMERO_ORDEN  = '''||GL_NUMORDEN   ||'''
                         AND PREANO        >  ' || MI_ANOPROCESO || '
                         AND PORCENTAJE_RESERVA > 0
                         AND RESOLUCION_RESERVA IS NOT NULL'; 

        BEGIN                 
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOS',
                                               UN_ACCION   =>'M',
                                               UN_VALORES  =>MI_VALORES,
                                               UN_CONDICION=>MI_CONDICION 
                                               ); 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                   END ;   

        MI_CONSECUTIVO := I + 1;
      ELSE
        MI_VALORES := ' PORCENTAJE_RESERVA =  1 - PORCENTAJE_RESERVA
                      , CONSECUTIVO        =  ' || I || '+ 1
                      , MODIFIED_BY        = '''||UN_USUARIO||'''
                      , DATE_MODIFIED      = SYSDATE';
        MI_CONDICION := 'COMPANIA               = '''||UN_COMPANIA    ||'''
                         AND CODIGO             = '''||UN_CODPREDIO   ||'''
                         AND NUMERO_ORDEN       = '''||GL_NUMORDEN    ||'''
                         AND PREANO             >  ' || MI_ANOPROCESO || '
                         AND PORCENTAJE_RESERVA > 0
                         AND RESOLUCION_RESERVA IS NOT NULL'; 

        BEGIN                 
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOS', 
                                               UN_ACCION   =>'M', 
                                               UN_VALORES  =>MI_VALORES,
                                               UN_CONDICION=>MI_CONDICION
                                               );    

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                   END ;  

        MI_CONSECUTIVO := I + 1;
      END IF;
      --RECORRE LAS VIGENCIAS A PARTIR DEL AÑO DE RESERVA HASTA LA VIGENCIA DE
      --LA FECHA DE CORTE PARA REALIZAR LA ASIGNACIÓN AL PREDIO CORRESPONDIENTE
	  <<APLICAR_PORCRESERVA_FACTURADOS>>
      FOR J IN GL_ANORESERVA .. MI_ANOCORTE LOOP
        BEGIN
          SELECT  PORCENTAJE_RESERVA, RESOLUCION_RESERVA
            INTO  MI_FACT_PORCRESERVA, MI_FACT_RESRESERVA
            FROM  IP_FACTURADOS
           WHERE  COMPANIA      = UN_COMPANIA
             AND  CODIGO        = UN_CODPREDIO
             AND  NUMERO_ORDEN  = GL_NUMORDEN
             AND  PREANO        = J;

          EXCEPTION WHEN NO_DATA_FOUND THEN
            NULL;            
        END;
        IF MI_FACT_PORCRESERVA > 0 AND TO_NUMBER(MI_FACT_RESRESERVA) > 0 THEN

          MI_CAMPOS := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(UN_TABLA    =>'IP_FACTURADOSRESERVA',
                                                      UN_EXCLUIDOS=>'CONSECUTIVO');
          MI_CAMPOS := MI_CAMPOS || ', CONSECUTIVO';
          MI_VALORES := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(UN_TABLA   =>'IP_FACTURADOS', 
                                                      UN_EXCLUIDOS=>'TEMPORAL, CONSECUTIVO');
          MI_VALORES := MI_VALORES || ', CONSECUTIVO';
          MI_CONDICION := '   COMPANIA         = ''' || UN_COMPANIA  || 
                          ''' AND CODIGO       = ''' || UN_CODPREDIO || 
                          ''' AND NUMERO_ORDEN = ''' || GL_NUMORDEN  ||
                          ''' AND PREANO       = ' || J || ' ';
          MI_VALORES := ' SELECT ' || MI_VALORES  || 
                        ' FROM IP_FACTURADOS ' ||
                        ' WHERE ' ||
                        MI_CONDICION;

          BEGIN              
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA  =>'IP_FACTURADOSRESERVA', 
                                                 UN_ACCION =>'IS', 
                                                 UN_CAMPOS =>MI_CAMPOS,
                                                 UN_VALORES=>MI_VALORES
                                                 ); 

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                 RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                   END ;  

          IF PCK_DATOS.GL_RTA > 0 THEN
            MI_CAMPOS := ' AVALUO = AVALUO * PORCENTAJE_RESERVA 
                            , C1 = C1 * PORCENTAJE_RESERVA 
                            , C2 = C2 * PORCENTAJE_RESERVA 
                            , C3 = C3 * PORCENTAJE_RESERVA 
                            , C4 = C4 * PORCENTAJE_RESERVA 
                            , C5 = C5 * PORCENTAJE_RESERVA 
                            , C6 = C6 * PORCENTAJE_RESERVA 
                            , C7 = C7 * PORCENTAJE_RESERVA 
                            , C8 = C8 * PORCENTAJE_RESERVA 
                            , C9 = C9 * PORCENTAJE_RESERVA 
                            , C10 = C10 * PORCENTAJE_RESERVA 
                            , C11 = C11 * PORCENTAJE_RESERVA 
                            , C12 = C12 * PORCENTAJE_RESERVA 
                            , C13 = C13 * PORCENTAJE_RESERVA 
                            , C14 = C14 * PORCENTAJE_RESERVA 
                            , C15 = C15 * PORCENTAJE_RESERVA 
                            , C16 = C16 * PORCENTAJE_RESERVA 
                            , C17 = C17 * PORCENTAJE_RESERVA 
                            , C18 = C18 * PORCENTAJE_RESERVA 
                            , C19 = C19 * PORCENTAJE_RESERVA 
                            , C20 = C20 * PORCENTAJE_RESERVA 
                            , C1_CPY = C1_CPY * PORCENTAJE_RESERVA
                            , C3_CPY = C3_CPY * PORCENTAJE_RESERVA '  ;
            MI_CONDICION := '   COMPANIA         = ''' || UN_COMPANIA    || 
                            ''' AND CODIGO       = ''' || UN_CODPREDIO   || 
                            ''' AND NUMERO_ORDEN = ''' || GL_NUMORDEN    ||
                            ''' AND PREANO       = '   || J              || 
                            '   AND CONSECUTIVO  = '   || MI_CONSECUTIVO ||' ';

            BEGIN                
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOSRESERVA',
                                                   UN_ACCION   =>'M',
                                                   UN_CAMPOS   =>MI_CAMPOS,
                                                   UN_CONDICION=>MI_CONDICION 
                                                   );  

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                       END ;

          END IF;
        END IF;
        IF FC_AVALUOINICIALRES(J)<> 0 THEN
          MI_VALORES   := 'AVALUO = ' || FC_AVALUOINICIALRES(J);
          MI_CONDICION := '   COMPANIA         = ''' || UN_COMPANIA  || 
                          ''' AND CODIGO       = ''' || UN_CODPREDIO || 
                          ''' AND NUMERO_ORDEN = ''' || GL_NUMORDEN  ||
                          ''' AND PREANO       = '   || J ;
        END IF;
      END LOOP APLICAR_PORCRESERVA_FACTURADOS;
    END LOOP CICLOS_RESERVA;
    MI_CAMPOS := ' INDICADOR_RESERVA = -1';
    IF UN_INDRESERVA < 0 THEN
      MI_CAMPOS := MI_CAMPOS || 
                  ', PORCENTAJE_RESERVA = '|| UN_PORCRESERVA ;
    END IF;
    MI_CONDICION := '   COMPANIA         = ''' || UN_COMPANIA  || 
                    ''' AND CODIGO       = ''' || UN_CODPREDIO || 
                    ''' AND NUMERO_ORDEN = ''' || GL_NUMORDEN  || '''';

    BEGIN                
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_USUARIOS_PREDIAL',
                                           UN_ACCION   =>'M',
                                           UN_CAMPOS   =>MI_CAMPOS,
                                           UN_CONDICION=>MI_CONDICION 
                                           ); 

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
             RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
               END ;   

    <<RECORRER_FACTURADOSRESERVA>>
    FOR RS IN (
                SELECT   CODIGO
                       , NUMERO_ORDEN
                       , PREANO
                       , SUM(AVALUO) AVALUO
                       , SUM(TOTAL) TOTAL
                       , SUM(C1) C1
                       , SUM(C2) C2
                       , SUM(C3) C3
                       , SUM(C4) C4
                       , SUM(C13) C13
                       , SUM(C14) C14
                       , SUM(C15) C15
                       , SUM(C16) C16
                       , SUM(C17) C17
                       , SUM(C18) C18
                       , SUM(C19) C19
                       , SUM(C20) C20
                       , SUM(C1_CPY) C1_CPY
                       , SUM(C3_CPY) C3_CPY 
                  FROM IP_FACTURADOSRESERVA
                 WHERE COMPANIA     = UN_COMPANIA
                   AND CODIGO       = UN_CODPREDIO
                   AND NUMERO_ORDEN = GL_NUMORDEN
                   AND PREANO       > MI_ANOPROCESO 
                 GROUP BY CODIGO, NUMERO_ORDEN, PREANO
               ) LOOP
      IF UN_PORCRESERVA < 1 THEN
        MI_CAMPOS := ' PORCENTAJE_RESERVA = 1 - PORCENTAJE_RESERVA, ';
      ELSE
        MI_CAMPOS := '';
      END IF;
      MI_CAMPOS := MI_CAMPOS || 
                  '  AVALUO  = ' || RS.AVALUO ||
                  ', TOTAL   = ' || RS.TOTAL  ||
                  ', C1      = ' || RS.C1     || 
                  ', C2      = ' || RS.C2     || 
                  ', C3      = ' || RS.C3     || 
                  ', C4      = ' || RS.C4     || 
                  ', C13     = ' || RS.C13    || 
                  ', C14     = ' || RS.C14    || 
                  ', C15     = ' || RS.C15    || 
                  ', C16     = ' || RS.C16    || 
                  ', C17     = ' || RS.C17    || 
                  ', C18     = ' || RS.C18    || 
                  ', C19     = ' || RS.C19    || 
                  ', C20     = ' || RS.C20    || 
                  ', C1_CPY  = ' || RS.C1_CPY || 
                  ', C3_CPY  = ' || RS.C3_CPY || '';
      MI_CONDICION := '   COMPANIA         = ''' || UN_COMPANIA  || 
                      ''' AND CODIGO       = ''' || UN_CODPREDIO || 
                      ''' AND NUMERO_ORDEN = ''' || GL_NUMORDEN  || 
                      ''' AND PREANO       = '   || RS.PREANO    ||'';

      BEGIN                
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'IP_FACTURADOS', 
                                             UN_ACCION   =>'M', 
                                             UN_CAMPOS   =>MI_CAMPOS, 
                                             UN_CONDICION=>MI_CONDICION 
                                             ); 

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                 END ;                                       
    END LOOP RECORRER_FACTURADOSRESERVA;
  END IF;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD =>SQLCODE,
                  UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_ASIGPORCRESERVA
   );

END PR_ASIGNAPORC_RESERVA;

--10
FUNCTION FC_AVALUOINICIALRES
  /*
  NAME              : FC_AVALUOINICIALRES
  AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE              : 15/09/2016
  TIME              : 02:10 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       :  Recibe la vigencia a consultar y retorna el valor del avaluo almacenado en el vector.    
  @NAME: avaluoInicialRes
  @METHOD: GET
  */
  (
  -- Parametro que recibe el ano de vigencia a consultar el valor del avaluo
  UN_VIGENCIA                IN PCK_SUBTIPOS.TI_ANIO
  )
  RETURN NUMBER
  AS
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 10;
    MI_VALOR                 PCK_SUBTIPOS.TI_DOBLE:=0;
  BEGIN
  BEGIN
    IF CPTO.EXISTS(UN_VIGENCIA) THEN  
      MI_VALOR := CPTO(UN_VIGENCIA);
      IF MI_VALOR IS NULL THEN
        MI_VALOR:=0 ;
      END IF;
    END IF;
  EXCEPTION WHEN OTHERS THEN
    MI_VALOR:=0;
  END;
  RETURN MI_VALOR;

   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD =>SQLCODE,
                  UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_AVALUOSINICIALESR
   );

END FC_AVALUOINICIALRES ;

-- 11
FUNCTION FC_DESCUENTOMES
  /*
  NAME              : FC_DESCUENTOMES
  AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE              : 03/10/2016
  TIME              : 04:10 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       :  Función que verifica la configuración de descuentos con el fin de retornar el porcentaje a descontar
                       teniendo en cuenta los parámetros. En la función de access no se manejan parámetros sino variables
                       globales que manejan los datos durante todo el proceso. Se debe tener en cuenta en el momento de integrar 
                       el proceso grande de calculo. 
  @NAME             : retornarPorcetajeDescuento
  @METHOD           : GET
  @PARAMETERS       : UN_COMPANIA, entidad que se calcula
                      UN_ANO, vigencia calculada y en la cual se debe consultar la configuración de descuentos
                      UN_FECHACORTE,  fecha hasta la cual se realizará el calculo 
                      UN_CLASEPREDIO , clase del predio que se esta calculando. Valor de la tabla de IP_USUARIOS_PREDIAL
                      UN_CODPREDIO ,  cédula catrastral calculada. 
  */
  (
  -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe la vigencia calculada y en la cual se debe consultar la configuracion de descuentos
  UN_ANO                     IN PCK_SUBTIPOS.TI_ANIO, 
  -- Parametro que recibe la fecha hasta la cual se realiza el calculo
  UN_FECHACORTE              IN DATE,
  -- Parametro que recibe la clase del predio  que se esta calculando 
  UN_CLASEPREDIO             IN IP_USUARIOS_PREDIAL.CLASE_PREDIO%TYPE,
  -- Parametro que recibe la cedula catastral calculada
  UN_CODPREDIO               IN PCK_SUBTIPOS.TI_CODPREDIO
  )
  RETURN NUMBER
  AS
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 11;
    MI_ANO                   PCK_SUBTIPOS.TI_ANIO:= TO_NUMBER(TO_CHAR(SYSDATE, 'YYYY'));
    MI_MES                   PCK_SUBTIPOS.TI_MES := TO_NUMBER(TO_CHAR(UN_FECHACORTE, 'MM'));
    MI_VLRRETORNO            PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_PORCENTAJE            PCK_SUBTIPOS.TI_PORCENTAJE := 0;
    MI_EXISTE                VARCHAR2(1 CHAR) := 'N';
    MI_INCRAVALUO            PCK_SUBTIPOS.TI_DOBLE := 0;
  BEGIN
  IF UN_ANO = MI_ANO THEN
    --verifica si existe configuración en la tabla de descuentos especiales, de
    --no ser asi verifica la tabla general de configuración 
    BEGIN 
      SELECT  DISTINCT 'S'
        INTO  MI_EXISTE
        FROM  IP_DESCUENTOS_ESPECIALES;

      EXCEPTION WHEN NO_DATA_FOUND THEN 
         MI_EXISTE := 'N';
    END;
    IF MI_EXISTE = 'S' THEN
      MI_INCRAVALUO :=  FC_INCREMENTOAVALUO (UN_COMPANIA, UN_CODPREDIO);
      BEGIN
        SELECT   PORC_DSCTO 
          INTO   MI_PORCENTAJE
          FROM   IP_DESCUENTOS_ESPECIALES
         WHERE   COMPANIA        = UN_COMPANIA
           AND   CODIGO_INICIAL  <= UN_CODPREDIO
           AND   CODIGO_FINAL    >= UN_CODPREDIO
           AND   INCREM_INICIAL  <= MI_INCRAVALUO 
           AND   INCREM_FINAL    >= MI_INCRAVALUO
           AND   CLASE_PREDIO    = UN_CLASEPREDIO
         ORDER BY  MES, IND_PRIORIDAD;

        EXCEPTION WHEN NO_DATA_FOUND THEN
          GOTO DESCUENTO;
      END;
      MI_VLRRETORNO := MI_PORCENTAJE;
    ELSE
      GOTO DESCUENTO;
    END IF;

  <<DESCUENTO>>
    BEGIN
      SELECT PORCENTAJE
        INTO MI_PORCENTAJE
        FROM IP_DESCUENTOS_ANO
       WHERE COMPANIA = UN_COMPANIA
         AND ANO      = UN_ANO
         AND MES      = MI_MES;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_PORCENTAJE := 0;
    END;
    MI_VLRRETORNO := MI_PORCENTAJE;
  END IF;
  RETURN MI_VLRRETORNO;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD =>SQLCODE,
                  UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_AVALUOSINICIALESR
   );

END FC_DESCUENTOMES;

--12
FUNCTION FC_DESCABONOS
  /*
  NAME              : FC_DESCABONOS
  AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE              : 03/10/2016
  TIME              : 05:08 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       :  Funcion que retorna el valor del campo indicado por el parámetro en la tabla de IP_FACTURADOSABONOS
  @NAME             : retornarValorConceptoAbono
  @METHOD           : GET
  @PARAMETERS       : UN_COMPANIA, entidad que se calcula
                      UN_CODPREDIO ,  cédula catrastral calculada. 
                      UN_CONCEPTO, código del concepto del cual se desea retornar el valor
  */
  (
  -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe la cedula catastral calculada
  UN_CODPREDIO               IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el codigo del concepto del cual se desea retornar el valor
  UN_CONCEPTO                IN PCK_SUBTIPOS.TI_ENTERO
  )
  RETURN NUMBER
  AS
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 12;
    MI_VLRRETORNO            PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_STRSQL                PCK_SUBTIPOS.TI_STRSQL;
    MI_ANO                   PCK_SUBTIPOS.TI_ANIO:= TO_NUMBER(TO_CHAR(SYSDATE, 'YYYY'));
  BEGIN
  MI_STRSQL := ' SELECT C' || UN_CONCEPTO ||
               '   FROM IP_FACTURADOSABONOS 
                  WHERE COMPANIA  = ''' || UN_COMPANIA  ||
               '''  AND CODIGO    = ''' || UN_CODPREDIO ||
               '''  AND PREANO    = '   || MI_ANO       || 
               '    AND PAGADO    NOT IN (0)'; 
  BEGIN
    EXECUTE IMMEDIATE MI_STRSQL INTO MI_VLRRETORNO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_VLRRETORNO := 0;
  END;
  RETURN MI_VLRRETORNO;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD =>SQLCODE,
                  UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_DESCABONOS
   );
END FC_DESCABONOS;

--13
FUNCTION FC_MESESMORA
  /*
  NAME              : FC_MESESMORA
  AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE              : 03/10/2016
  TIME              : 05:18 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       :  Funcion que retorna la cantidad de meses en mora de un predio
  @NAME             : retornarMesesEnMora
  @METHOD           : GET
  @PARAMETERS       : UN_COMPANIA, entidad que se calcula
                      UN_ANO, vigencia de la cual se desea obtener la cantidad de meses en mora
                      UN_FECHACORTE,  fecha hasta la cual se realizará el calculo 
                      UN_IND_EXEINT, si es diferente de cero indica que el predio esta exento de intereses
                      UN_FECHAINICIAL_EXEINT , fecha inicial desde la cual el predio esta exento de intereses
                      UN_FECHAFINAL_EXEINT  , fecha final desde la cual el predio esta exento de intereses
  */
  (
  -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA, 
  -- Parametro que recibe la vigencia de la cual se desea obtener la cantidad de meses en mora
  UN_ANO                     IN PCK_SUBTIPOS.TI_ANIO,
  -- Parametro que recibe la fecha hasta la cual se realizara el calculo
  UN_FECHACORTE              IN DATE,
  -- Parametro que recibe el un valor de cero si el predio no esta excento de interes
  UN_IND_EXEINT              IN PCK_SUBTIPOS.TI_LOGICO,
  -- Parametro que recibe la fecha inicial desde la cual el predio esta exento de intereses
  UN_FECHAINICIAL_EXEINT     IN DATE,
  -- Parametro que recibe la fecha final hasta la cual el predio esta exento de intereses
  UN_FECHAFINAL_EXEINT       IN DATE
  )
  RETURN NUMBER
  AS 
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 13;
    MI_VLRRETORNO            PCK_SUBTIPOS.TI_DOBLE;
    MI_MESESAMNISTIA         PCK_SUBTIPOS.TI_ENTERO;
    MI_ANOCORTE              PCK_SUBTIPOS.TI_ANIO :=  TO_NUMBER(TO_CHAR(UN_FECHACORTE, 'YYYY'));
    MI_MESCORTE              PCK_SUBTIPOS.TI_MES := TO_NUMBER(TO_CHAR(UN_FECHACORTE, 'MM'));
    MI_DIFMESES              PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_ANOI_EXEINT           PCK_SUBTIPOS.TI_ANIO :=  TO_NUMBER(TO_CHAR(UN_FECHAINICIAL_EXEINT, 'YYYY'));
    MI_ANOF_EXEINT           PCK_SUBTIPOS.TI_ANIO :=  TO_NUMBER(TO_CHAR(UN_FECHAFINAL_EXEINT, 'YYYY'));
    MI_FECHAINICIAL          DATE;
    MI_FECHAFINAL            DATE;
    MI_AMNISTIA              PCK_SUBTIPOS.TI_ENTERO;
    MI_MESESMORA             PCK_SUBTIPOS.TI_ENTERO;
    MI_FECHATEMP             VARCHAR2(32000 CHAR);
  BEGIN
  BEGIN
    SELECT  MESESAMNISTIA_PREDIAL
      INTO  MI_MESESAMNISTIA
      FROM  ANO 
     WHERE  COMPANIA = UN_COMPANIA
       AND  NUMERO   = UN_ANO;  

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_MESESAMNISTIA := 0;
  END;

  IF MI_ANOCORTE = UN_ANO THEN
    MI_DIFMESES := MI_MESCORTE - MI_MESESAMNISTIA;
  ELSE
    MI_DIFMESES := (MI_ANOCORTE - UN_ANO) * 12 - MI_MESESAMNISTIA + MI_MESCORTE;
  END IF;
  MI_MESESMORA := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION=>MI_DIFMESES < 0, 
                                        UN_SI       =>0,
                                        UN_NO       =>MI_DIFMESES
                                        );
  IF UN_IND_EXEINT <> 0 THEN
    IF MI_ANOI_EXEINT <= UN_ANO THEN
      MI_AMNISTIA := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION=>MI_MESESAMNISTIA = 0, 
                                           UN_SI       =>1, 
                                           UN_NO       =>MI_MESESAMNISTIA
                                           );
      IF MI_AMNISTIA = 1 THEN
        MI_FECHATEMP := '01/12/' || TO_CHAR(UN_ANO -1);
        MI_FECHAINICIAL := TO_DATE(MI_FECHATEMP , 'DD/MM/YYYY');
      ELSE
        MI_FECHATEMP := '01/' || TO_CHAR(MI_AMNISTIA) || '/' || TO_CHAR(UN_ANO);
        MI_FECHAINICIAL := TO_DATE(MI_FECHATEMP , 'DD/MM/YYYY');
      END IF;    
    ELSE  
      MI_FECHAINICIAL := UN_FECHAINICIAL_EXEINT;
    END IF;

    IF MI_ANOF_EXEINT <= UN_ANO THEN
      MI_FECHAFINAL := UN_FECHAFINAL_EXEINT;
      MI_FECHAFINAL := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION=>MI_FECHAFINAL < MI_FECHAINICIAL,
                                             UN_SI       =>MI_FECHAINICIAL, 
                                             UN_NO       =>MI_FECHAFINAL
                                             );
    ELSE
      MI_FECHAFINAL := TRUNC(SYSDATE);
    END IF;
    MI_MESESMORA := MI_MESESMORA - PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(MI_FECHAINICIAL, MI_FECHAFINAL);
  END IF;
  MI_VLRRETORNO := PCK_SYSMAN_UTL.FC_IIF(UN_CONDICION=>MI_MESESMORA < 0,
                                         UN_SI       =>0,
                                         UN_NO       =>MI_MESESMORA
                                         );
  RETURN MI_VLRRETORNO;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_MESESMORA
   );

END FC_MESESMORA;

--14
FUNCTION FC_MENORVIGENCIA_ADEUDADA
  /*
  NAME              : FC_MENORVIGENCIA_ADEUDADA
  AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE              : 04/10/2016
  TIME              : 02:44 PM
  MODIFIER          : 18/08/2017
  DATE MODIFIED     : SANDRA MILENA DAZA LEGUIZAMON
  TIME              : 09:00AM 
  DESCRIPTION       : Funcion que retorna la menor vigencia adeudada del predio
                      En la modificación se valida que no retorne valor NULL
  @name             : retornarMenorVigenciaAdeudada
  @method           : GET
  @parameters       : UN_COMPANIA, entidad que se calcula
                      UN_CODPREDIO, predio del cual se esta consultando la menor vigencia adeudada. 
  */
  (
  -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el codigo del predio, al que se esta consultando la menor vigencia adecuada
  UN_CODPREDIO               IN PCK_SUBTIPOS.TI_CODPREDIO
  )
  RETURN NUMBER
  AS
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 14;
    MI_VLRRETORNO            PCK_SUBTIPOS.TI_ANIO := 0;
  BEGIN
  BEGIN
    SELECT MIN(PREANO) 
      INTO MI_VLRRETORNO 
      FROM IP_FACTURADOS
     WHERE COMPANIA      = UN_COMPANIA
       AND CODIGO        = UN_CODPREDIO
       AND NUMERO_ORDEN  = GL_NUMORDEN 
       AND PAGADO        = 0;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_VLRRETORNO := 0;
  END;
  RETURN NVL(MI_VLRRETORNO, 0);

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_MENORVIG_ADEUDADA
   );
END FC_MENORVIGENCIA_ADEUDADA;

--14
FUNCTION FC_MENORVIGENCIA_SINACUERDO
  /*
  NAME              : FC_MENORVIGENCIA_SINACUERDO
  AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE              : 04/10/2016
  TIME              : 02:54 PM
  MODIFIER          : 18/08/2017
  DATE MODIFIED     : SANDRA MILENA DAZA LEGUIZAMON
  TIME              : 09:00AM 
  DESCRIPTION       : Funcion que retorna la menor vigencia adeudada del predio sin tener en cuenta las vigencias en acuerdo de pago
                      En la modificación se valida que no retorne valor NULL
  @name             : retornarMenorVigenciaAdeudadaSinAcuerdo
  @method           : GET
  @parameters       : UN_COMPANIA, entidad que se calcula
                      UN_CODPREDIO, predio del cual se esta consultando la menor vigencia adeudada. 
  */
  (
  -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el codigo del predio del cual se esta calculando la menor vigencia adecuada
  UN_CODPREDIO               IN PCK_SUBTIPOS.TI_CODPREDIO
  )
  RETURN NUMBER
  AS
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 15;
    MI_VLRRETORNO            PCK_SUBTIPOS.TI_ANIO := 0;
  BEGIN
  BEGIN
    SELECT MIN(PREANO) 
      INTO MI_VLRRETORNO 
      FROM IP_FACTURADOS
     WHERE COMPANIA       = UN_COMPANIA
       AND CODIGO         = UN_CODPREDIO
       AND NUMERO_ORDEN   = GL_NUMORDEN 
       AND PAGADO         = 0
       AND INDPAGO_ACPAG  = 0;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_VLRRETORNO := 0;
  END;
  RETURN NVL(MI_VLRRETORNO, 0) ;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_MENORVIG_SINACUER
   );

END FC_MENORVIGENCIA_SINACUERDO;

--16
FUNCTION FC_INCREMENTOAVALUO
  /*
  NAME              : FC_INCREMENTOAVALUO
  AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE              : 04/10/2016
  TIME              : 05:40 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Funcion que retorna el valor o porcentaje en que incrementa el avaluo de un predio entre la vigencia actual y la anterior
  @name             : calcularIncrementoAvaluo
  @method           : GET
  @parameters       : UN_COMPANIA, entidad que se calcula
                      UN_CODPREDIO, predio del cual se esta consultando la menor vigencia adeudada. 
  */
  (
  -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el codigo del predio del cual se esta consultando la vigencia menor
  UN_CODPREDIO               IN PCK_SUBTIPOS.TI_CODPREDIO
  )
  RETURN NUMBER
  AS
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 16;
    MI_VLRRETORNO            PCK_SUBTIPOS.TI_DOBLE;
    MI_ANO                   PCK_SUBTIPOS.TI_ANIO:= PCK_SYSMAN_UTL.FC_ANIO(SYSDATE);
    MI_AVALUOACTUAL          PCK_SUBTIPOS.TI_DOBLE;
    MI_AVALUOANTERIOR        PCK_SUBTIPOS.TI_DOBLE;
  BEGIN
  --CONSULTAR AVALUO ACTUAL DEL PREDIO
  BEGIN
    SELECT  AVALUO 
      INTO  MI_AVALUOACTUAL
      FROM  IP_FACTURADOS
     WHERE  COMPANIA = UN_COMPANIA
       AND  CODIGO   = UN_CODPREDIO
       AND  PREANO   = MI_ANO;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      RETURN 0;
  END;
  --CONSULTAR AVALUO ANTERIOR DEL PREDIO
  BEGIN
    SELECT  AVALUO 
      INTO  MI_AVALUOANTERIOR
      FROM  IP_FACTURADOS
     WHERE  COMPANIA = UN_COMPANIA
       AND  CODIGO   = UN_CODPREDIO
       AND  PREANO   = MI_ANO - 1;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      RETURN 0;
  END;

  MI_VLRRETORNO := ROUND (((MI_AVALUOACTUAL * 100)/ MI_AVALUOANTERIOR) -100, 1);
  RETURN MI_VLRRETORNO;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_INCREM_AVALUO
   );

END FC_INCREMENTOAVALUO;

--17
FUNCTION FC_PAGADESCESP 
  /*
  NAME              : FC_PAGADESCESP
  AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE              : 05/10/2016
  TIME              : 11:00 AM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Funcion que  verifica la configuracion de vigencias para descuentos especiales con el fin de validar 
                      si la vigencia hasta la cual se desea cancelar esta dentro del rango permitido. 
                      Retorna 0 si se presentan configuraciones incorrectas o si la vigencia a pagar no cumple con  la configuración
                             -1 si la vigencia a cancelar esta dentro de la configuración requerida para aplicar el descuento.
  @name             : validarPagaDescEspecial
  @method           : GET
  @parameters       : UN_COMPANIA, entidad que se calcula
                      UN_CODPREDIO, predio del cual se esta consultando la menor vigencia adeudada. 
                      UN_ANOFIN, vigencia hasta la cual se va a calcular la factura del predio y la que se debe validar para ver si aplica o no descuento especial
  */
  (
  -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA, 
  -- Parametro que recibe el codigo del predio del cual se esta calculando la vigencia menor adeudada
  UN_CODPREDIO               IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe la vigencia hasta la cual se va a calcular la factura del predio 
  UN_ANOFIN                  IN PCK_SUBTIPOS.TI_ANIO
  )
  RETURN NUMBER
  AS 
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 17;
    MI_VLRRETORNO            PCK_SUBTIPOS.TI_LOGICO:= 0;
    MI_PERMITEMODLEY         PCK_SUBTIPOS.TI_PARAMETRO;
    MI_ANOMAXAPLICAR         PCK_SUBTIPOS.TI_PARAMETRO;
    MI_ANOMAXPAGAR           PCK_SUBTIPOS.TI_PARAMETRO;
    MI_STRSQL                PCK_SUBTIPOS.TI_STRSQL;
    MI_ANOMAXIMO             PCK_SUBTIPOS.TI_ANIO;
  BEGIN
  -- SE USA LA FUNCION PARA CARGAR TODOS LOS PARAMETROS, ESTA INVOCACION DEBE SER REUBICADA EN LA FUNCIÓN DONDE SE INVOQUE ESTA
  PCK_PARST.PR_INICIALIZAR_PARSISTEMA(UN_COMPANIA, GL_MODULO);
  MI_PERMITEMODLEY := PCK_PARST.FC_PAR(UN_PARAMETRO=>'PERMITE MODIFICACIONES LEY DESC. ESPECIALES',
                                       UN_VLOMISION=>'0'
                                       );
  MI_ANOMAXAPLICAR := PCK_PARST.FC_PAR(UN_PARAMETRO=>'VIGENCIA MAX PERMITIDA OPCION DESC ESP', 
                                       UN_VLOMISION=>'0'
                                       );
  MI_ANOMAXPAGAR := PCK_PARST.FC_PAR(UN_PARAMETRO=>'VIGENCIA MIN POR PAGAR PARA DESC ESP',
                                     UN_VLOMISION=>'0'
                                     );

  IF MI_ANOMAXPAGAR = '0' THEN
    MI_ANOMAXPAGAR := TO_NUMBER(PCK_SYSMAN_UTL.FC_ANIO(SYSDATE));    
  END IF;
  IF MI_PERMITEMODLEY = '1' THEN
    MI_STRSQL := ' SELECT MAX(PREANO) ANOMAXIMO ' ||
                 '   FROM   IP_FACTURADOS ' ||
                 '  WHERE    COMPANIA      = ''' || UN_COMPANIA  || '''' ||
                 '    AND    CODIGO        = ''' || UN_CODPREDIO || '''' ||
                 '    AND    NUMERO_ORDEN  = ''' || GL_NUMORDEN  || '''' ||
                 '    AND    PAGADO        = 0'  ||
                 '    AND    PREANO        <= '  || MI_ANOMAXAPLICAR     || '';
  ELSE 
    MI_STRSQL := ' SELECT MAX(PREANO) ANOMAXIMO ' ||
                 '   FROM   IP_FACTURADOS ' ||
                 '  WHERE    COMPANIA      = ''' || UN_COMPANIA  || '''' ||
                 '    AND    CODIGO        = ''' || UN_CODPREDIO || '''' ||
                 '    AND    NUMERO_ORDEN  = ''' || GL_NUMORDEN  || '''' ||
                 '    AND    PAGADO        = 0'  ||
                 '    AND    PREANO        <= '  || MI_ANOMAXPAGAR       || '';
  END IF;
  BEGIN
    EXECUTE IMMEDIATE MI_STRSQL INTO MI_ANOMAXIMO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      RETURN 0; 
  END ;
  IF UN_ANOFIN >= NVL(MI_ANOMAXIMO, 0) THEN
    MI_VLRRETORNO := -1;
  END IF;
  RETURN MI_VLRRETORNO;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_VALIDADESCPAGESP
   );

END FC_PAGADESCESP;

--18 
FUNCTION FC_PAGATODO 
  /*
  NAME              : FC_PAGATODO
  AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE              : 05/10/2016
  TIME              : 12:20 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Funcion que  verifica si estan pagas las vigencias que determina la ley de descuentos
                      Retorna 0 si no cumple con las condiciones de pago para aplicar la ley de descuento
                             -1 si cumple con las condiciones de pago para aplicar la ley de descuento
  @name             : validarPagaTodo
  @method           : GET
  @parameters       : UN_COMPANIA, entidad que se calcula
                      UN_CODPREDIO, predio del cual se esta consultando las vigencias pagas. 
                      UN_ANOFIN, vigencia hasta la cual se va a calcular la factura del predio y la que se debe validar para ver si aplica o no descuento especial
  */
  (
  -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el codigo del predio del cual se estan consultando las vigencias pagas
  UN_CODPREDIO               IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe el ano hasta el cual se esta calculando la factura del predio
  UN_ANOFIN                  IN PCK_SUBTIPOS.TI_ANIO
  )
  RETURN NUMBER
  AS 
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 18;
    MI_VLRRETORNO            PCK_SUBTIPOS.TI_LOGICO:= 0 ;
    MI_CANCELARVIGANT_LEY1066  PCK_SUBTIPOS.TI_PARAMETRO; 
    MI_PAGO                  PCK_SUBTIPOS.TI_LOGICO;
    MI_ANO                   PCK_SUBTIPOS.TI_ANIO;
  BEGIN
  -- SE USA LA FUNCION PARA CARGAR TODOS LOS PARAMETROS, ESTA INVOCACION DEBE SER REUBICADA EN LA FUNCIÓN DONDE SE INVOQUE ESTA
  PCK_PARST.PR_INICIALIZAR_PARSISTEMA(UN_COMPANIA=>UN_COMPANIA,
                                      UN_MODULO  =>GL_MODULO
                                      );
  MI_CANCELARVIGANT_LEY1066 := PCK_PARST.FC_PAR(UN_PARAMETRO=>'SE DEBE CANCELAR 2005 Y 2006 PARA 3RA OPCION LEY 1066/2006',
                                                UN_VLOMISION=>'NO'
                                                );

  IF MI_CANCELARVIGANT_LEY1066 = 'SI' THEN  
    BEGIN 
      SELECT  PAGADO
        INTO  MI_PAGO
        FROM  IP_FACTURADOS
       WHERE  COMPANIA      = UN_COMPANIA
         AND  CODIGO        = UN_CODPREDIO
         AND  NUMERO_ORDEN  = GL_NUMORDEN
         AND  PREANO        = 2005;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VLRRETORNO := -1;
    END ;    

    IF MI_VLRRETORNO <> 0 THEN
      BEGIN 
        SELECT  PAGADO
          INTO  MI_PAGO 
          FROM  IP_FACTURADOS
         WHERE  COMPANIA      = UN_COMPANIA
           AND  CODIGO        = UN_CODPREDIO
           AND  NUMERO_ORDEN  = GL_NUMORDEN
           AND  PREANO        = 2006;

        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_PAGO := -1;
      END ;   
    END IF;
    IF MI_PAGO <> 0 THEN
      MI_VLRRETORNO := MI_PAGO;
      RETURN MI_VLRRETORNO;
    END IF;
  ELSE 
    BEGIN 
      SELECT  MAX(PREANO)
        INTO  MI_ANO
        FROM  IP_FACTURADOS
       WHERE  COMPANIA      = UN_COMPANIA
         AND  CODIGO        = UN_CODPREDIO
         AND  NUMERO_ORDEN  = GL_NUMORDEN
         AND  PAGADO        = 0;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VLRRETORNO := 0;
    END ;   
    IF MI_ANO = UN_ANOFIN THEN
      MI_VLRRETORNO := -1;
    END IF;
  END IF;
  RETURN MI_VLRRETORNO;

 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_VALIDAPAGATODO
   ); 

END FC_PAGATODO; 

--19
FUNCTION FC_DESC_REGISTRADOS
  /*
  NAME              : FC_DESC_REGISTRADOS EN ACCESS DESC_FAC
  AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE              : 06/10/2016
  TIME              : 08:10 AM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Funcion que  verifica si el predio entre un rango de fechas tiene registrados 
                      descuentos a favor y retorna el valor de todos los que este activos                       
  @name             : calcularDescuentosRegistrados
  @method           : GET
  @parameters       : UN_COMPANIA, entidad que se calcula
                      UN_CODPREDIO, predio del cual se esta consultando las vigencias pagas. 
                      UN_ANOINICIAL, vigencia inicial del rango de consulta
                      UN_ANOFINAL, vigencia final del rango de consulta                        
  */
  (
  -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el codigo del predio del cual se estan consultando las vigencias
  UN_CODPREDIO               IN PCK_SUBTIPOS.TI_CODPREDIO,
  -- Parametro que recibe la vigencia inicial del rango de consulta
  UN_ANOINICIAL              IN PCK_SUBTIPOS.TI_ANIO,
  -- Parametro que recibe la vigencia final del rango de consulta
  UN_ANOFINAL                IN PCK_SUBTIPOS.TI_ANIO
  )
  RETURN NUMBER
  AS
    MI_VLRRETORNO            PCK_SUBTIPOS.TI_DOBLE:= 0;
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 19;
  BEGIN
  BEGIN
    SELECT SUM(VALOR)
      INTO MI_VLRRETORNO
      FROM IP_PAGOSDOBLES
     WHERE COMPANIA   = UN_COMPANIA
       AND PRECOD     = UN_CODPREDIO
       AND PREANO     BETWEEN UN_ANOINICIAL AND UN_ANOFINAL
       AND TIPO       = 'S'
       AND ANULADO    = 0
       AND DESCUENTO  NOT IN (0);

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_VLRRETORNO := 0;
  END;
  RETURN MI_VLRRETORNO;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_DESCREGISTRADOS
   );

END FC_DESC_REGISTRADOS;

--20

--21
FUNCTION FC_RETORNAR_FORMULACPTO
  /*
  NAME              : FC_RETORNAR_FORMULACPTO EN ACCESS NO EXISTIA
  AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE              : 06/10/2016
  TIME              : 04:20 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Funcion que  retorna la formula de un concepto para la vigencia y compañia enviada por parametro        
  @name             : retornarFormulaConcepto
  @method           : GET
  @parameters       : UN_COMPANIA, entidad que se calcula
                      UN_ANO, año de la configuración del concepto consultado
                      UN_CONCEPTO, codigo del concepto consultado                 
  */
  (
  -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe la vigencia en la cual se esta configurando el concepto consultado
  UN_ANO                     IN PCK_SUBTIPOS.TI_ANIO, 
  -- Parametro que recibe el codigo del conceptop consultado
  UN_CONCEPTO                IN PCK_SUBTIPOS.TI_ENTERO
  ) 
  RETURN VARCHAR2
  AS 
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 21;
    MI_FORMULA               IP_CONCEPTOS.FORMULA%TYPE;
  BEGIN
  BEGIN
    SELECT FORMULA
      INTO MI_FORMULA
      FROM IP_CONCEPTOS 
     WHERE COMPANIA = UN_COMPANIA
       AND ANO      = UN_ANO
       AND CODIGO   = UN_CONCEPTO;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_FORMULA := '0';      
  END ;
  RETURN MI_FORMULA;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_RETORNAFORMCONCEP
   );


END FC_RETORNAR_FORMULACPTO;

--22
FUNCTION FC_AMNISTIA
  /*
  NAME              : FC_AMNISTIA 
  AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE              : 24/10/2016
  TIME              : 12:20 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Funcion que  retorna la cantidad de meses en amnistia para la vigencia indicada por el parámetro
  @name             : retornarMesesAmnistia
  @method           : GET
  @parameters       : UN_COMPANIA, entidad que se calcula
                      UN_ANO, año de la configuración                
  */
  (
  -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el año de configuracion de la administia
  UN_ANO                     IN PCK_SUBTIPOS.TI_ANIO
  )
  RETURN NUMBER
  AS
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 22;
    MI_VLRRETORNO            PCK_SUBTIPOS.TI_ENTERO;
  BEGIN
    BEGIN
      SELECT  MESESAMNISTIA_PREDIAL
        INTO  MI_VLRRETORNO
        FROM  ANO 
       WHERE  COMPANIA = UN_COMPANIA
         AND  NUMERO   = UN_ANO;  

      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VLRRETORNO := 0;
    END;
    RETURN MI_VLRRETORNO;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_CANTMESESAMNIS
   );

END FC_AMNISTIA;

--23
FUNCTION FC_EVALUARPORCDESCUENTO 
  /*
  NAME              : FC_EVALUARPORCDESCUENTO EN ACCESS EVALPORCDESCUENTO
  AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE              : 24/10/2016
  TIME              : 15:20 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Funcion que  retorna un porcentaje de descuento.
                      Si la compania es Pasto retorna el valor configurado en la tarifa correspondiente,
                      al invocar la función
                      de lo contrario evalua el parámetro MANEJA DIF. PORCENTAJES EN LEY DESCUENTOS ESPECIALES
                      para tomar el valor seleccionado en el formulario o retorna el valor ingresado por parámetro
  @name             : evaluarPorcentajeDescuento
  @method           : GET
  @parameters       : UN_NITCOMPANIA, NIT entidad que se calcula
                      UN_ANO, año de la configuración de las tarifas
                      UN_ANOMINDESC ,año minimo configurado para aplicar ley de descuentos
                      UN_PORCENTAJE , valor de porcentaje a aplicar si no cumple las condiciones previas
                      UN_PORCENTAJEGR , valor de porcentaje si se toma la condición del parámetro 
                                        MANEJA DIF. PORCENTAJES EN LEY DESCUENTOS ESPECIALES y este viene del
                                        control grafico del formulario de calculo
                      UN_CODTARIFA  , código de la tarifa en la cual se configuró el descuento especial.
  */
  (
  -- Parametro que recibe el numero de la entidad que se calcula
  UN_COMPANIA                IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el ano de configuracion de las tarifas
  UN_NITCOMPANIA             IN COMPANIA.NITCOMPANIA%TYPE,
  -- Parametro que recibe el ano de configuracion de las tarifas 
  UN_ANO                     IN PCK_SUBTIPOS.TI_ANIO,
  -- Parametro que recibe el ano minimo para aplicar ley de descuentos
  UN_ANOMINDESC              IN PCK_SUBTIPOS.TI_ANIO,
  -- Parametro que recibe el valor de porcentaje a aplicar si no cumple las condiciones previas
  UN_PORCENTAJE              IN PCK_SUBTIPOS.TI_PORCENTAJE,
  -- Parametro que recibe el valor del porcentaje si se toma la condicion del parametro 
  UN_PORCENTAJEGR            IN PCK_SUBTIPOS.TI_PORCENTAJE,
  -- Parametro que recibe el codigo de la tarifa en la cual se configuro el descuento especial
  UN_CODTARIFA               IN IP_FACTURADOS.TRPCOD%TYPE
  )
  RETURN NUMBER
  AS 
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 23;
    MI_VLRRETORNO            PCK_SUBTIPOS.TI_PORCENTAJE := 0;
    MI_TOMAPORC_DESCTARIFAS  PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
  BEGIN

  MI_TOMAPORC_DESCTARIFAS := PCK_PARST.FC_PAR(UN_PARAMETRO=>'TOMA PORCENTAJE DESCUENTO ESPECIAL TARIFAS',
                                              UN_VLOMISION=>'NO'
                                              );
  IF UN_NITCOMPANIA = '8912800003' THEN
    IF MI_TOMAPORC_DESCTARIFAS = 'SI' THEN
      IF UN_ANO >= UN_ANOMINDESC THEN 
        BEGIN
          SELECT DISTINCT PORCDESCUENTOS 
            INTO MI_VLRRETORNO 
            FROM IP_TARIFAS
           WHERE TRPANO = UN_ANO
             AND TRPCOD = UN_CODTARIFA;

          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_VLRRETORNO := UN_PORCENTAJE;
            RETURN MI_VLRRETORNO;
        END ;
        MI_CAMPOS := ' VALOR = ' || MI_VLRRETORNO;
        MI_CONDICION := '   COMPANIA     = ''' || UN_COMPANIA ||
                        ''' AND MODULO   = '   || GL_MODULO   ||
                        '   AND NOMBRE   = ''' || 'PORCENTAJE DESCUENTO ESPECIAL' || ''' '; 

        BEGIN               
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>'PARAMETRO',
                                               UN_ACCION   =>'M',
                                               UN_CAMPOS   =>MI_CAMPOS,
                                               UN_CONDICION=>MI_CONDICION
                                               );               
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                   END ;                                       
      ELSE
        MI_VLRRETORNO := 0;
      END IF;
    ELSE
      MI_VLRRETORNO := UN_PORCENTAJE;
    END IF;
  ELSIF PCK_PARST.FC_PAR(UN_PARAMETRO=>'MANEJA DIF. PORCENTAJES EN LEY DESCUENTOS ESPECIALES',
                         UN_VLOMISION=>'NO'
                         ) = 'SI' THEN
    MI_VLRRETORNO := UN_PORCENTAJEGR / 100;
  ELSE
    MI_VLRRETORNO := UN_PORCENTAJE;
  END IF;
  RETURN MI_VLRRETORNO;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_EVALPORCDESCTO
   );

END FC_EVALUARPORCDESCUENTO;

--24
FUNCTION FC_TASA
  /*
  NAME              : FC_TASA EN ACCESS TASA()
  AUTHORS           : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE              : 25/10/2016
  TIME              : 09:10 AM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Funcion que  retorna el valor configurado en el parámetro de sistema TASA DE INTERES VIGENTE, 
                      validando que no este nula o cero. Si ocurre esto, el retorno de la función será de -1 el 
                      cual indica que la configuración es incorrecta. 
  @name             : retornarTasaInteresParametro
  @method           : GET
  @parameters       : no hay parámetros de entrada
  */
  RETURN NUMBER
  AS
    MI_ERROR_FUN             PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 24;
    MI_VLRPARAMETRO          PCK_SUBTIPOS.TI_PARAMETRO;
    MI_VLRRETORNO            PCK_SUBTIPOS.TI_DOBLE := 0;
  BEGIN

  MI_VLRPARAMETRO := PCK_PARST.FC_PAR(UN_PARAMETRO=>'TASA DE INTERES VIGENTE',
                                      UN_VLOMISION=>'0'
                                      );
  IF MI_VLRPARAMETRO IS NULL OR MI_VLRPARAMETRO = '0' THEN
    MI_VLRRETORNO := -1;
  ELSE
    BEGIN
      --MI_VLRRETORNO := TO_NUMBER(MI_VLRPARAMETRO);
      SELECT TO_NUMBER(MI_VLRPARAMETRO)
        INTO MI_VLRRETORNO 
        FROM DUAL;
      EXCEPTION WHEN INVALID_NUMBER THEN
        MI_VLRRETORNO := -1;
    END ;
  END IF;
  RETURN MI_VLRRETORNO;

   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_TASAINTERES_VIG
   );

END FC_TASA;

  --25
  FUNCTION FC_FACTURAR
  (
    /*
      NAME              : FC_FACTURAR --> EN ACCESS ImprimirVigencia
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MIGRADOR     : 24/02/2017
      TIME              : 02:00 PM
      SOURCE MODULE     : IMPUESTO PREDIAL
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : FUNCION QUE RETORNA LA CANTIDAD DE FILAS AFECTADAS POR UNA ACCIÓN DEPENDIENDO DE EL MOMENTO QUE SE INGRESE POR PARAMETRO.
      PARAMETERS        : UN_COMPANIA                => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_NITCOMPANIA             => CADENA CON EL NIT DE LA COMPANIA.
                          UN_SIGLACOMPANIA           => CADENA CON LA SIGLA DE LA COMPANIA.
                          UN_CODIGOPREDIO            => CODIGO DEL PREDIO EN EL CUAL SE VAN A REALIZAR LAS OPERACIONES.
                          UN_PAGO_ANO                => ANIO EN EL QUE SE REALIZA EL PAGO. VIENE DESDE EL FORMULARIO USUARIOS PREDIAL.
                          UN_UNICO_ANO               => VALOR DEL CHECK "UN SOLO ANIO" DEL FORMULARIO.
                          UN_FECHACORTE              => FECHA DE CORTE DE LOS RECIBOS.
                          UN_FECHALIMITE             => FECHA LIMITE INGRESADA POR EL FORMULARIO.
                          UN_NOMBRE_PROPIETARIO      => CADENA CON EL NOMBRE DEL PROPIETARIO DEL PREDIO.
                          UN_NUMEROORDEN_PROPIETARIO => CADENA CON EL NUMERO DE ORDEN DEL PROPIETARIO DEL PREDIO.
                          UN_NIT_PROPIETARIO         => CADENA CON EL NIT DEL PROPIETARIO DEL PREDIO.
                          UN_FACTURA_CERO            => VARIABLE QUE DETERMINA SI SE IMPRIMEN O NO FACTURAS EN CERO.
                          UN_USUARIO                 => USUARIO ACTUAL DE LA APLICACION.
                          UN_ANIOINICIAL             => ANIO INICIAL DESDE EL QUE SE FACTURA.
                          UN_ANIOFIN                 => ANIO FINAL HASTA EL QUE SE FACTURA.
      MODIFICATIONS     : 

      @NAME:    facturarVigencia
      @METHOD:  GET
    */
    UN_COMPANIA                  IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_NITCOMPANIA               IN  VARCHAR2,
    UN_SIGLACOMPANIA             IN  COMPANIA.SIGLACOMPANIA%TYPE,
    UN_CODIGOPREDIO              IN  PCK_SUBTIPOS.TI_CODPREDIO,
    UN_PAGO_ANO                  IN  PCK_SUBTIPOS.TI_ANIO,
    UN_UNICO_ANO                 IN  PCK_SUBTIPOS.TI_LOGICO,
    UN_FECHACORTE                IN  DATE,
    UN_FECHALIMITE               IN  DATE,
    UN_NOMBRE_PROPIETARIO        IN  VARCHAR2, 
    UN_NUMEROORDEN_PROPIETARIO   IN  IP_FACTURADOS.NUMERO_ORDEN%TYPE,
    UN_NIT_PROPIETARIO           IN  VARCHAR2,
    UN_FACTURA_CERO              IN  PCK_SUBTIPOS.TI_LOGICO,
    UN_USUARIO                   IN  PCK_SUBTIPOS.TI_USUARIO,
    UN_ANIOINICIAL               IN  PCK_SUBTIPOS.TI_ANIO,
    UN_ANIOFIN                   IN  PCK_SUBTIPOS.TI_ANIO,
    UN_AVALUO_ANO                IN  IP_RECIBOS_DE_PAGO.AVALUO%TYPE,
    UN_APLICADESC                IN  PCK_SUBTIPOS.TI_LOGICO
  ) 
RETURN VARCHAR2 
  AS
    MI_RSRECIBOSPAGO            SYS_REFCURSOR;
    MI_RSNUMFACTURA             SYS_REFCURSOR;
    MI_STRNUMRECIBO             VARCHAR2 (1000 CHAR);
    MI_STROBSERVACIONES         PCK_SUBTIPOS.TI_STRSQL;
    MI_STRSQL                   PCK_SUBTIPOS.TI_STRSQL;
    MI_ANOCORTE                 PCK_SUBTIPOS.TI_ANIO;
    MI_I                        PCK_SUBTIPOS.TI_ENTERO;
    MI_DBLTOTAL                 PCK_SUBTIPOS.TI_DOBLE;
    MI_STRCODIGO                VARCHAR2 (1000 CHAR);
    MI_MSGERROR                 PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CAMPOS                   PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION                PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES                  PCK_SUBTIPOS.TI_VALORES;
    MI_CONTEO                   PCK_SUBTIPOS.TI_ENTERO;
    MI_CONSECUTIVO              IP_NUMEROSDEFACTURA.CONSECUTIVO%TYPE;
    MI_CONSECUTIVOREAL          IP_NUMEROSDEFACTURA.CONSECUTIVOREAL%TYPE;
    MI_SECUENCIA                IP_NUMEROSDEFACTURA.SECUENCIA%TYPE;
    MI_SQLRECIBOSPAGO           PCK_SUBTIPOS.TI_STRSQL;
    MI_STRDETALLADO             PCK_SUBTIPOS.TI_STRSQL;
    MI_SQLNUMFACTURA            PCK_SUBTIPOS.TI_STRSQL;
    MI_SQLSELECT                PCK_SUBTIPOS.TI_STRSQL;
    MI_PRECOD                   IP_RECIBOS_DE_PAGO.PRECOD%TYPE;
    MI_DOCNUM                   IP_RECIBOS_DE_PAGO.DOCNUM%TYPE;
    MI_PREFEC                   IP_RECIBOS_DE_PAGO.PREFEC%TYPE;
    MI_PREANO                   IP_RECIBOS_DE_PAGO.PREANO%TYPE;
    MI_PREVAL                   IP_RECIBOS_DE_PAGO.PREVAL%TYPE;
    MI_CCONCEPTOS               TYPE_CONCEPTOS;
    MI_PREDREPORTE              VARCHAR2(100 CHAR);
    MI_PREANOI                  PCK_SUBTIPOS.TI_ANIO;
    MI_PREANOF                  PCK_SUBTIPOS.TI_ANIO;
    MI_ANIOINICIAL              PCK_SUBTIPOS.TI_ANIO;

  BEGIN 
    IF UN_ANIOFIN IS NULL THEN
      BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN          
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD   => SQLCODE,
            UN_ERROR_COD => PCK_ERRORES.ERR_PREDIAL_FACT_ANIO_PAG);
      END;
    END IF;

    MI_ANOCORTE         := EXTRACT(YEAR FROM UN_FECHACORTE);
    MI_STROBSERVACIONES := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                 UN_NOMBRE    => 'OBSERVACIONES RECIBO',
                                                 UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                                 UN_FECHA_PAR => SYSDATE);   
    MI_STROBSERVACIONES := MI_STROBSERVACIONES || MI_STROBSERVACIONES;

    MI_SQLRECIBOSPAGO   := 'SELECT  PRECOD,
                                  DOCNUM,
                                  PREFEC,
                                  PREANO,
                                  PREVAL
                          FROM    IP_RECIBOS_DE_PAGO
                          WHERE   COMPANIA                  = '''|| UN_COMPANIA ||'''
                            AND   PRECOD                    = '''|| UN_CODIGOPREDIO ||'''
                            AND   ANULADO IN (0)
                            AND   PAGO IN (0)
                            AND   NVL(ESACUERDO,0)          IN (0)';

    <<RECIBOSPAGO>>
    OPEN MI_RSRECIBOSPAGO FOR MI_SQLRECIBOSPAGO;
      LOOP
        FETCH MI_RSRECIBOSPAGO
        INTO  MI_PRECOD,
              MI_DOCNUM,
              MI_PREFEC,
              MI_PREANO,
              MI_PREVAL;
        EXIT WHEN MI_RSRECIBOSPAGO%NOTFOUND;

        MI_CAMPOS    := 'ANULADO         = -1 , 
                         FECHAANULACION  = SYSDATE, 
                         ANULADO_POR     = '''|| UN_USUARIO ||''',
                         DATE_MODIFIED   = SYSDATE,
                         MODIFIED_BY     = '''|| UN_USUARIO ||'''
                         ';

        MI_CONDICION := '     COMPANIA          = '''|| UN_COMPANIA ||'''
                         AND  PRECOD            = '''|| UN_CODIGOPREDIO ||'''
                         AND  ANULADO           IN (0)
                         AND  PAGO              IN (0)
                         AND  NVL(ESACUERDO, 0) IN (0)
                         AND  ACUERDO           IS NULL';

        BEGIN
          BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_RECIBOS_DE_PAGO',
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            MI_MSGERROR(1).CLAVE := 'CODIGOPREDIO';
            MI_MSGERROR(1).VALOR := UN_CODIGOPREDIO;
            MI_MSGERROR(2).CLAVE := 'TABLA';
            MI_MSGERROR(2).VALOR := 'IP_RECIBOS_DE_PAGO';
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD =>SQLCODE,
              UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_FACT_ACT_ANULADO,
              UN_REEMPLAZOS  => MI_MSGERROR);
        END;

        IF UN_SIGLACOMPANIA = 'AGUAZUL' THEN
          MI_CAMPOS    := 'ANULADO = -1,
                          DATE_MODIFIED = SYSDATE,
                          MODIFIED_BY = '''||UN_USUARIO||'''
                          ';

          MI_CONDICION := '     COMPANIA         = '''|| UN_COMPANIA ||'''
                           AND  COD_PREDIO       = '''|| UN_CODIGOPREDIO ||'''
                           AND  ANULADO          IN (0)';

          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_RECIBOS_MULTIFECHA',
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);                                   

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              MI_MSGERROR(1).CLAVE := 'CODIGOPREDIO';
              MI_MSGERROR(1).VALOR := UN_CODIGOPREDIO;
              MI_MSGERROR(2).CLAVE := 'TABLA';
              MI_MSGERROR(2).VALOR := 'IP_RECIBOS_MULTIFECHA';
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD =>SQLCODE,
                UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_FACT_ACT_ANULADO,
                UN_REEMPLAZOS  => MI_MSGERROR);
          END;
        END IF;

      END LOOP RECIBOSPAGO;
    CLOSE MI_RSRECIBOSPAGO;

    MI_I := 0;
    MI_SQLNUMFACTURA := ' SELECT  SECUENCIA,
                                  CONSECUTIVO,
                                  CONSECUTIVOREAL
                          FROM    IP_NUMEROSDEFACTURA
                          WHERE   COMPANIA = '''|| UN_COMPANIA ||'''
                            AND   TIPO     = ''N''
                          ORDER BY SECUENCIA DESC';

    OPEN MI_RSNUMFACTURA FOR MI_SQLNUMFACTURA;
      LOOP
        FETCH MI_RSNUMFACTURA
        INTO  MI_SECUENCIA,
              MI_CONSECUTIVO,
              MI_CONSECUTIVOREAL;
        EXIT WHEN MI_I = 10;

        SELECT  COUNT('1') 
        INTO    MI_CONTEO
        FROM    IP_RECIBOS_DE_PAGO 
        WHERE   DOCNUM = MI_CONSECUTIVO;

        IF MI_CONTEO = 0 THEN
          MI_STRNUMRECIBO := TO_NUMBER(MI_CONSECUTIVOREAL) + 1;
          MI_STRNUMRECIBO := PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_STRNUMRECIBO,
                                                       UN_LONGITUD => 9);
          MI_CAMPOS    := 'CONSECUTIVOREAL = '''|| MI_STRNUMRECIBO ||''',
                          DATE_MODIFIED = SYSDATE,
                          MODIFIED_BY = '''||UN_USUARIO||'''
                        ';

          MI_CONDICION := '     COMPANIA  = '''|| UN_COMPANIA ||'''
                           AND  SECUENCIA = '  || MI_SECUENCIA;

          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_NUMEROSDEFACTURA',
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              MI_MSGERROR(1).CLAVE := 'CONSECUTIVOREAL';
              MI_MSGERROR(1).VALOR := MI_STRNUMRECIBO;
              MI_MSGERROR(2).CLAVE := 'SECUENCIA';
              MI_MSGERROR(2).VALOR := MI_SECUENCIA;
              MI_MSGERROR(3).CLAVE := 'TABLA';
              MI_MSGERROR(3).VALOR := 'IP_NUMEROSDEFACTURA';
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD =>SQLCODE,
                UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_FACT_ACT_CONREAL,
                UN_REEMPLAZOS  => MI_MSGERROR);
          END;
          MI_I := 10;
        ELSE
          MI_I := MI_I + 1;
          IF MI_I = 10 THEN
            BEGIN
              BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_FACT_INTENTO_CON,
                  UN_REEMPLAZOS => MI_MSGERROR);
            END;
          END IF;
        END IF;
      END LOOP;
    CLOSE MI_RSNUMFACTURA;

    MI_SQLSELECT := 'SELECT ';

    FOR MI_I IN 1..4 LOOP
      MI_SQLSELECT := MI_SQLSELECT || 'SUM(CASE WHEN PREANO = '|| MI_ANOCORTE ||'
                                                THEN C' || MI_I ||'
                                                ELSE 0
                                           END) CC'|| MI_I ||', ';
    END LOOP;

    FOR MI_I IN 1..4 LOOP
      MI_SQLSELECT := MI_SQLSELECT || 'SUM(CASE WHEN PREANO = '|| (MI_ANOCORTE - 1)||'
                                                THEN C' || MI_I ||'
                                                ELSE 0
                                           END) CC'|| (MI_I + 4)||', ';
    END LOOP;

    FOR MI_I IN 1..4 LOOP
      MI_SQLSELECT := MI_SQLSELECT || 'SUM(CASE WHEN PREANO <= '|| (MI_ANOCORTE - 2) ||' 
                                                THEN C' || MI_I ||'
                                                ELSE 0
                                           END) CC'|| (MI_I + 8) ||', ';
    END LOOP;



    MI_SQLSELECT := MI_SQLSELECT || '
                    SUM(C13) CC13,
                    SUM(C14) CC14,
                    SUM(C15) CC15,
                    SUM(C16) CC16,';

    FOR MI_RSFACTURADOS IN 
    (
      SELECT  * 
      FROM    IP_FACTURADOS
      WHERE   COMPANIA     = UN_COMPANIA
        AND   CODIGO       = UN_CODIGOPREDIO
        AND   NUMERO_ORDEN = '001'
        AND   PREANO       = UN_ANIOFIN
    )
    LOOP
      IF PCK_GENERALES.FC_NOMBRECOMPANIA(UN_COMPANIA => UN_COMPANIA) = 'MUNICIPIO DE SOGAMOSO' THEN
        MI_STRSQL := MI_SQLSELECT || ' 
                            SUM(CASE WHEN PREANO = '|| MI_ANOCORTE ||'
                                     THEN C17
                                     ELSE 0
                                END ) CC17,
                            SUM(CASE WHEN PREANO = '|| (MI_ANOCORTE - 1) ||' 
                                     THEN C17
                                     ELSE 0
                                END) CC18, 
                            SUM(CASE WHEN PREANO <= '|| (MI_ANOCORTE -2) ||'
                                     THEN C17
                                     ELSE 0
                                END) CC19, 
                            SUM(C20) CC20
                     FROM   IP_FACTURADOS ';

      ELSE
        MI_STRSQL := MI_SQLSELECT || ' 
                            SUM(C17) CC17,
                            SUM(C18) CC18, 
                            SUM(C19) CC19, 
                            SUM(C20) CC20
                     FROM   IP_FACTURADOS ';
      END IF;

      MI_STRDETALLADO := 'WHERE   COMPANIA             = '''|| UN_COMPANIA ||'''
                            AND   CODIGO               = '''|| UN_CODIGOPREDIO ||''' 
                            AND   NUMERO_ORDEN         = ''001''
                            AND   PREANO               BETWEEN '|| (CASE WHEN UN_UNICO_ANO = 1 
                                                                         THEN UN_ANIOFIN 
                                                                         ELSE NVL(UN_ANIOINICIAL, 1970)
                                                                    END) ||' AND '|| UN_ANIOFIN ||'
                            AND   PAGADO               = 0
                            AND   NOCOBRADO            = 0
                            AND   NVL(INDPAGO_ACPAG,0) = 0';

      MI_STRSQL   := MI_STRSQL || MI_STRDETALLADO;
      MI_DBLTOTAL := 0;

      EXECUTE IMMEDIATE MI_STRSQL 
        INTO  CPTO(1) , CPTO(2) ,
              CPTO(3) , CPTO(4) , 
              CPTO(5) , CPTO(6) ,
              CPTO(7) , CPTO(8) , 
              CPTO(9) , CPTO(10), 
              CPTO(11), CPTO(12),
              CPTO(13), CPTO(14),
              CPTO(15), CPTO(16),
              CPTO(17), CPTO(18),
              CPTO(19), CPTO(20);

      MI_CCONCEPTOS := PCK_PREDIAL_COM3.FC_CONFIGURARCONCEPTOS(UN_COMPANIA => UN_COMPANIA,
                                                               UN_ANOCORTE => EXTRACT (YEAR FROM SYSDATE));

      FOR I IN MI_CCONCEPTOS.FIRST .. MI_CCONCEPTOS.LAST LOOP
        IF MI_CCONCEPTOS(I).CODIGO = I THEN
          IF MI_CCONCEPTOS(I).INFORMATIVO = 0 THEN
            MI_DBLTOTAL := MI_DBLTOTAL + FC_CPTO(I);
          END IF;
        END IF;
      END LOOP;

      IF MI_DBLTOTAL = 0 THEN
        IF UN_FACTURA_CERO = 0 THEN
          BEGIN
            BEGIN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_FACT_FACTURA_CERO);
          END;
        END IF;
      END IF;

      MI_CAMPOS  := 'COMPANIA, PRECOD, PREANO, NUMERO_ORDEN, PREFEC, DOCNUM, PREVAL, PREFECLIM, C1, C2, C3, C4, C5, C6, C7, 
                     C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, PREANOI, PREANOF, UNICO_ANO, PREUSU, 
                     APLICALEY1175, NOMBREPROPFAC, NITPROPFAC, ORDENPROPFAC,APLICADESCESP, AVALUO, VALOR_APORTE, DATE_CREATED, 
                     CREATED_BY';

      MI_VALORES := ''''|| UN_COMPANIA ||''',
                    ''' || UN_CODIGOPREDIO ||''',
                    '   || UN_ANIOFIN || ',
                    ''' || UN_NUMEROORDEN_PROPIETARIO ||''',
                    TO_DATE('''|| TO_CHAR(UN_FECHACORTE, 'MM/DD/YYYY') ||''',''MM/DD/YYYY''),
                    ''' || MI_STRNUMRECIBO ||''', 
                    '   || MI_DBLTOTAL ||',
                    TO_DATE('''|| TO_CHAR(UN_FECHACORTE, 'MM/DD/YYYY') ||''',''MM/DD/YYYY''),
                    '   || FC_CPTO(1)  || ', ' || FC_CPTO(2)  || ', 
                    '   || FC_CPTO(3)  || ', ' || FC_CPTO(4)  || ', 
                    '   || FC_CPTO(5)  || ', ' || FC_CPTO(6)  || ', 
                    '   || FC_CPTO(7)  || ', ' || FC_CPTO(8)  || ', 
                    '   || FC_CPTO(9)  || ', ' || FC_CPTO(10) || ',
                    '   || FC_CPTO(11) || ', ' || FC_CPTO(12) || ',
                    '   || FC_CPTO(13) || ', ' || FC_CPTO(14) || ',
                    '   || FC_CPTO(15) || ', ' || FC_CPTO(16) || ',
                    '   || FC_CPTO(17) || ', ' || FC_CPTO(18) || ',
                    '   || FC_CPTO(19) || ', ' || FC_CPTO(20) || ',
                    '   || CASE WHEN UN_UNICO_ANO <> 0
                                THEN UN_ANIOFIN
                                ELSE UN_ANIOINICIAL
                           END || ',
                    '   || UN_ANIOFIN || ',
                    '   || CASE WHEN UN_UNICO_ANO <> 0
                                THEN -1
                                ELSE 0
                           END || ',
                    ''' || UN_USUARIO || ''', 0,
                    ''' || UN_NOMBRE_PROPIETARIO ||''',
                    ''' || UN_NIT_PROPIETARIO ||''',
                    ''' || UN_NUMEROORDEN_PROPIETARIO ||''', 0,
                    '   || UN_AVALUO_ANO ||',0, SYSDATE,''' || UN_USUARIO || '''';

      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'IP_RECIBOS_DE_PAGO',
                                                UN_ACCION  => 'I',
                                                UN_CAMPOS  => MI_CAMPOS,
                                                UN_VALORES => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_MSGERROR(1).CLAVE := 'CODIGOPREDIO';
          MI_MSGERROR(1).VALOR := UN_CODIGOPREDIO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_FACT_INS_DETALLE2,
            UN_REEMPLAZOS => MI_MSGERROR);
      END;

      MI_CAMPOS  := 'COMPANIA, DOCNUM, CONSECUTIVO, AVALUO, PREANO, C1, C2, C3, C4, C5, C6, C7, 
                     C8, C9, C10, C11, C12, C13, C14, C15, C16, C17, C18, C19, C20, TOTAL, 
                     DATE_CREATED, CREATED_BY';    

      IF UN_UNICO_ANO = 0 THEN 
        MI_ANIOINICIAL := UN_ANIOINICIAL;
      ELSE
        MI_ANIOINICIAL := UN_ANIOFIN;
      END IF;

      FOR MI_CONT_ANIO IN MI_ANIOINICIAL..UN_ANIOFIN LOOP
        MI_VALORES :=  '(SELECT '''|| UN_COMPANIA ||''', 
                                '''|| MI_STRNUMRECIBO ||''',
                                '  || PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    =>  'IP_DETALLE_RECIBOPAGO', 
                                                                       UN_CRITERIO =>  '    COMPANIA = '''|| UN_COMPANIA ||''' 
                                                                                        AND DOCNUM   = '''|| MI_STRNUMRECIBO ||'''', 
                                                                       UN_CAMPO    =>  'CONSECUTIVO') ||',
                                AVALUO,
                                PREANO,
                                C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, 
                                C12, C13, C14, C15, C16, C17, C18, C19, C20,
                                TOTAL,
                                SYSDATE,
                                '''|| UN_USUARIO ||'''
                        FROM    IP_FACTURADOS 
                        WHERE   COMPANIA             = '''|| UN_COMPANIA ||'''
                          AND   CODIGO               = '''|| UN_CODIGOPREDIO ||''' 
                          AND   NUMERO_ORDEN         = ''001''
                          AND   PREANO               = '  || MI_CONT_ANIO ||'
                          AND   PAGADO               = 0
                          AND   NOCOBRADO            = 0
                          AND   NVL(INDPAGO_ACPAG,0) = 0)';
        BEGIN
          BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'IP_DETALLE_RECIBOPAGO',
                                                  UN_ACCION  => 'IS',
                                                  UN_CAMPOS  => MI_CAMPOS,
                                                  UN_VALORES => MI_VALORES);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            MI_MSGERROR(1).CLAVE := 'CODIGOPREDIO';
            MI_MSGERROR(1).VALOR := UN_CODIGOPREDIO;
            MI_MSGERROR(2).CLAVE := 'TABLA';
            MI_MSGERROR(2).VALOR := 'IP_DETALLE_RECIBOPAGO';
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_FACT_INS_DETALLE,
              UN_REEMPLAZOS => MI_MSGERROR);
        END;
      END LOOP; 

      -- MULTIFECHAS
      DECLARE
        MI_FACTURACION_MULTIFECHA       PCK_SUBTIPOS.TI_PARAMETRO;
      BEGIN
        MI_FACTURACION_MULTIFECHA := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                           UN_NOMBRE    => 'FACTURACION MULTIFECHAS', 
                                                           UN_MODULO    => PCK_DATOS.MODULOPREDIAL, 
                                                           UN_FECHA_PAR => SYSDATE);
        IF MI_FACTURACION_MULTIFECHA = 'SI' THEN
          PCK_PREDIAL_COM3.PR_FACTURAR_MULTIFECHAS(UN_COMPANIA              => UN_COMPANIA, 
                                                   UN_DOCNUM                => MI_STRNUMRECIBO, 
                                                   UN_USUARIO               => UN_USUARIO, 
                                                   UN_UNICO_ANO             => UN_UNICO_ANO, 
                                                   UN_APLICA_DSCTO_ESPECIAL => UN_APLICADESC, 
                                                   UN_ANO_INICIAL           => UN_ANIOINICIAL, 
                                                   UN_ANO_FINAL             => UN_ANIOFIN);
        END IF;
      END;

      MI_CAMPOS    := 'ANOHASTA  = '  || UN_ANIOFIN ||',
                       TOTAL     = '  || MI_DBLTOTAL ||',
                       C1        = '  || FC_CPTO(1)  || ', C2  = ' || FC_CPTO(2)  || ' , 
                       C3        = '  || FC_CPTO(3)  || ', C4  = ' || FC_CPTO(4)  || ' , 
                       C5        = '  || FC_CPTO(5)  || ', C6  = ' || FC_CPTO(6)  || ' , 
                       C7        = '  || FC_CPTO(7)  || ', C8  = ' || FC_CPTO(8)  || ' , 
                       C9        = '  || FC_CPTO(9)  || ', C10 = ' || FC_CPTO(10) || ' ,
                       C11       = '  || FC_CPTO(11) || ', C12 = ' || FC_CPTO(12) || ' ,
                       C13       = '  || FC_CPTO(13) || ', C14 = ' || FC_CPTO(14) || ' ,
                       C15       = '  || FC_CPTO(15) || ', C16 = ' || FC_CPTO(16) || ' ,
                       C17       = '  || FC_CPTO(17) || ', C18 = ' || FC_CPTO(18) || ' ,
                       C19       = '  || FC_CPTO(19) || ', C20 = ' || FC_CPTO(20) || ' ,
                       DATE_MODIFIED = SYSDATE,
                       MODIFIED_BY = '''||UN_USUARIO||'''
                       ';

      MI_CONDICION := '    COMPANIA     = '''|| UN_COMPANIA ||'''
                       AND CODIGO       = '''|| UN_CODIGOPREDIO ||'''
                       AND NUMERO_ORDEN = ''001''';             

      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_MSGERROR(1).CLAVE := 'CODIGOPREDIO';
          MI_MSGERROR(1).VALOR := UN_CODIGOPREDIO;
          MI_MSGERROR(2).CLAVE := 'ANOHASTA';
          MI_MSGERROR(2).VALOR := UN_ANIOFIN;
          MI_MSGERROR(3).CLAVE := 'TABLA';
          MI_MSGERROR(3).VALOR := 'IP_USUARIOS_PREDIAL';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_FACT_ACT_CONANIO,
            UN_REEMPLAZOS => MI_MSGERROR);
      END; 
    END LOOP;

    FOR MI_RSRECIBOS IN
    (
      SELECT  DOCNUM
      FROM    IP_RECIBOS_DE_PAGO 
      WHERE   COMPANIA          = UN_COMPANIA
        AND   PRECOD            = UN_CODIGOPREDIO 
        AND   PREANO            = UN_ANIOFIN
        AND   ANULADO           IN(0) 
        AND   PAGO              IN(0) 
        AND   NVL(ESACUERDO, 0) IN(0) 
        AND   ACUERDO           IS NULL
    )
    LOOP
      MI_CAMPOS    := 'NUMERO_FACTURA = '''|| PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RSRECIBOS.DOCNUM,
                                                                        UN_LONGITUD => 9)||''',
                       RECIBO_ACTUAL  = '''|| PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RSRECIBOS.DOCNUM,
                                                                        UN_LONGITUD => 9)||''',
                       TOTAL          = '  || MI_DBLTOTAL ||',
                       ANOHASTA       = '  || UN_ANIOFIN  ||',
                       DATE_MODIFIED  = SYSDATE,
                       MODIFIED_BY    = '''||UN_USUARIO||'''
                       ';

      MI_CONDICION := '    COMPANIA = '''|| UN_COMPANIA ||'''
                       AND CODIGO   = '''|| UN_CODIGOPREDIO ||'''';

      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_MSGERROR(1).CLAVE := 'NUMEROFACTURA';
          MI_MSGERROR(1).VALOR := MI_RSRECIBOS.DOCNUM;
          MI_MSGERROR(2).CLAVE := 'ANOHASTA';
          MI_MSGERROR(2).VALOR := UN_ANIOFIN;
          MI_MSGERROR(3).CLAVE := 'CODIGOPREDIO';
          MI_MSGERROR(3).VALOR := UN_CODIGOPREDIO;
          MI_MSGERROR(4).CLAVE := 'TABLA';
          MI_MSGERROR(4).VALOR := 'IP_USUARIOS_PREDIAL';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_FACT_ACT_FACPRED,
            UN_REEMPLAZOS  => MI_MSGERROR);
      END;
    END LOOP;

    IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                             UN_NOMBRE    => 'FACTURA EN REPORTE',
                             UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                             UN_FECHA_PAR => SYSDATE) <> 'NO' THEN

      IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                               UN_NOMBRE    => 'FORMATO FACTURA',
                               UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                               UN_FECHA_PAR => SYSDATE) = ' ' THEN

        MI_PREDREPORTE := 'PREDIAL_RECIBOPAGOPREBANC';

      ELSE
        MI_PREDREPORTE := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                UN_NOMBRE    => 'FORMATO FACTURA',
                                                UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                                UN_FECHA_PAR => SYSDATE);
      END IF;

      MI_STRSQL := 'SELECT  PREANOI,
                            PREANOF
                    FROM    IP_RECIBOS_DE_PAGO 
                    WHERE   COMPANIA = '''|| UN_COMPANIA ||'''
                      AND   DOCNUM   = '''|| MI_STRNUMRECIBO||'''';

      IF UN_NITCOMPANIA = '8912800003' THEN
        OPEN MI_RSRECIBOSPAGO FOR MI_STRSQL;
          LOOP
            FETCH MI_RSRECIBOSPAGO
            INTO  MI_PREANOI,
                  MI_PREANOF;
            EXIT WHEN MI_RSRECIBOSPAGO%NOTFOUND;

            FOR MI_RSINTERES IN
            (
              SELECT  CODIGO,
                      SUM(DESC_INTERES) DTO_INTERES
              FROM    IP_FACTURADOS
              WHERE   COMPANIA = UN_COMPANIA
                AND   PREANO   BETWEEN MI_PREANOI AND MI_PREANOF
                AND   CODIGO   = UN_CODIGOPREDIO
              GROUP BY CODIGO
            )
            LOOP
              MI_CAMPOS    := 'DESC_INTERES = '  || MI_RSINTERES.DTO_INTERES  || ', 
                              DATE_MODIFIED = SYSDATE,
                              MODIFIED_BY   = '''||UN_USUARIO||'''
                              ';
              MI_CONDICION := '    COMPANIA = '''|| UN_COMPANIA ||'''
                               AND DOCNUM   = '''|| MI_STRNUMRECIBO ||'''';

              BEGIN
                BEGIN
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_DETALLE_RECIBOPAGO',
                                                        UN_ACCION    => 'M',
                                                        UN_CAMPOS    => MI_CAMPOS,
                                                        UN_CONDICION => MI_CONDICION);

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                  MI_MSGERROR(1).CLAVE := 'DOCNUM';
                  MI_MSGERROR(1).VALOR := MI_STRNUMRECIBO;
                  MI_MSGERROR(4).CLAVE := 'TABLA';
                  MI_MSGERROR(4).VALOR := 'IP_DETALLE_RECIBOPAGO';
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERR_PREDIAL_FACT_ACT_DES_INT,
                    UN_REEMPLAZOS => MI_MSGERROR);
              END;
            END LOOP;
          END LOOP;
        CLOSE MI_RSRECIBOSPAGO;
      END IF;

      IF UN_UNICO_ANO <> 0 THEN
        MI_CAMPOS := 'UNICO_ANO_A_PAGAR = '|| UN_ANIOFIN ;

      ELSE
        MI_CAMPOS := 'UNICO_ANO_A_PAGAR = NULL';

      END IF;

      MI_CAMPOS := MI_CAMPOS || ', DATE_MODIFIED = SYSDATE ,
                                  MODIFIED_BY = '''||UN_USUARIO||''' 
                                  ' ;
      MI_CONDICION := '    COMPANIA = '''|| UN_COMPANIA ||'''
                       AND CODIGO   = '''|| UN_CODIGOPREDIO ||'''';

      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_MSGERROR(1).CLAVE := 'UNICOANIO';
          MI_MSGERROR(1).VALOR := UN_ANIOFIN;
          MI_MSGERROR(2).CLAVE := 'CODIGOPREDIO';
          MI_MSGERROR(2).VALOR := UN_CODIGOPREDIO;
          MI_MSGERROR(3).CLAVE := 'TABLA';
          MI_MSGERROR(3).VALOR := 'IP_USUARIOS_PREDIAL';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_FACT_ACT_ANIO_UN,
            UN_REEMPLAZOS  => MI_MSGERROR);
      END;                                       
    ELSE
      IF UN_UNICO_ANO <> 0 THEN
        MI_CAMPOS := 'UNICO_ANO_A_PAGAR = '|| UN_ANIOFIN ;

      ELSE
        MI_CAMPOS := 'UNICO_ANO_A_PAGAR = NULL';

      END IF;

      MI_CAMPOS := MI_CAMPOS || ', DATE_MODIFIED = SYSDATE ,
                                  MODIFIED_BY = '''||UN_USUARIO||''' 
                                  ';
      MI_CONDICION := '    COMPANIA = '''|| UN_COMPANIA ||'''
                       AND CODIGO   = '''|| UN_CODIGOPREDIO ||'''';

      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                UN_ACCION    => 'M',
                                                UN_CAMPOS    => MI_CAMPOS,
                                                UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_MSGERROR(1).CLAVE := 'UNICOANIO';
          MI_MSGERROR(1).VALOR := UN_ANIOFIN;
          MI_MSGERROR(2).CLAVE := 'CODIGOPREDIO';
          MI_MSGERROR(2).VALOR := UN_CODIGOPREDIO;
          MI_MSGERROR(3).CLAVE := 'TABLA';
          MI_MSGERROR(3).VALOR := 'IP_USUARIOS_PREDIAL';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_FACT_ACT_ANIO_UN,
            UN_REEMPLAZOS  => MI_MSGERROR);
      END;                                       
    END IF;

    RETURN MI_STRNUMRECIBO;

  END FC_FACTURAR;

  --26. facturarMultifechas
  PROCEDURE PR_FACTURAR_MULTIFECHAS 
  /*
  NAME              : PR_FACTURAR_MULTIFECHAS
  AUTHORS           : SYSMAN SAS
  AUTHOR MIGRACION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE MIGRADOR     : 17/03/2017
  TIME              : 05:34 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : A partir del facturado del recibo de pago realiza el cálculo 
                      de descuentos, intereses y totales por cada mes según la 
                      configuración de los conceptos.
  MODIFICATIONS     : 
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.
    UN_DOCNUM: Numero de recibo de pago.
    UN_USUARIO:  Nombre del usuario que se identificó en la aplicación.
    UN_UNICO_ANO: Valor del indicador "Un sólo Año".
    UN_APLICA_DSCTO_ESPECIAL: Valor del indicador "Ley 1607 de 2012".
    UN_ANO_INICIAL: Año desde el que se factura. Menor Vigencia.
    UN_ANO_FINAL: Año hasta el que se factura.

  @NAME: facturarMultifechas
  @METHOD: put
  */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_DOCNUM                       IN IP_RECIBOS_DE_PAGO.DOCNUM%TYPE 
  , UN_USUARIO                      IN PCK_SUBTIPOS.TI_USUARIO
  , UN_UNICO_ANO                    IN PCK_SUBTIPOS.TI_LOGICO
  , UN_APLICA_DSCTO_ESPECIAL        IN PCK_SUBTIPOS.TI_LOGICO
  , UN_ANO_INICIAL                  IN PCK_SUBTIPOS.TI_ANIO
  , UN_ANO_FINAL                    IN PCK_SUBTIPOS.TI_ANIO
  ) AS
    MI_VALOR_PARAMETRO              PCK_SUBTIPOS.TI_PARAMETRO;
    MI_CONCEPTO_DESCUENTO           IP_CONCEPTOS.CODIGO%TYPE;
    MI_CONCEPTO_DESCUENTO_CAR       IP_CONCEPTOS.CODIGO%TYPE;
    MI_FORMULA_DESCUENTOS           IP_CONCEPTOS.FORMULA%TYPE := '0';
    MI_FORMULA_DESCUENTOS_CAR       IP_CONCEPTOS.FORMULA%TYPE := '0';
    MI_VLR_ABONO_CONCEPTO           PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_VLR_ABONO_CONCEPTO_CAR       PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_DESCUENTO_NC                 PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_NUMERO_ORDEN                 IP_RECIBOS_DE_PAGO.NUMERO_ORDEN%TYPE;
    MI_CODIGO_PREDIO                IP_RECIBOS_DE_PAGO.PRECOD%TYPE;
    MI_INCREMENTO_AVALUO            PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_CLASE_PREDIO                 IP_CLASE_PREDIOS.CODIGO%TYPE;
    MI_STRSQL                       PCK_SUBTIPOS.TI_STRSQL;
    MI_IND_DESCUENTO_ESPECIAL       BOOLEAN;
    -- Variables del cursor descuentos
    CURSOR_DESCUENTOS               SYS_REFCURSOR;
    RS_MES                          PCK_SUBTIPOS.TI_MES;
    RS_PORCENTAJE                   PCK_SUBTIPOS.TI_PORCENTAJE;
    RS_DIA_LIMITE                   PCK_SUBTIPOS.TI_DIA;
    -- Arreglo de fechas
    TYPE FECHAS_ARRAY               IS VARRAY(12) OF DATE;
    -- Fechas límites de pago según los descuentos configurados
    MI_FECHAS                       FECHAS_ARRAY := FECHAS_ARRAY();
    -- Arreglo de decimales
    TYPE VALORES_ARRAY              IS VARRAY(12) OF PCK_SUBTIPOS.TI_DOBLE;
    -- Intereses de cada descuento configurado
    MI_INTERESES                    VALORES_ARRAY := VALORES_ARRAY();
    -- Intereses de CAR de cada descuento configurado
    MI_INTERESES_CAR                VALORES_ARRAY := VALORES_ARRAY();
    -- Descuentos
    MI_DESCUENTOS                   VALORES_ARRAY := VALORES_ARRAY();
    -- Descuentos CAR         
    MI_DESCUENTOS_CAR               VALORES_ARRAY := VALORES_ARRAY();
    -- Totales
    MI_TOTALES                      VALORES_ARRAY := VALORES_ARRAY();
    --
    MI_SIGLA_COMPANIA               COMPANIA.SIGLACOMPANIA%TYPE;
  BEGIN
    -- Extracción de datos del predio
    SELECT PRECOD, NUMERO_ORDEN
      INTO MI_CODIGO_PREDIO, MI_NUMERO_ORDEN
      FROM IP_RECIBOS_DE_PAGO
     WHERE COMPANIA = UN_COMPANIA
       AND DOCNUM = UN_DOCNUM;
    --
    SELECT CLASE_PREDIO
      INTO MI_CLASE_PREDIO
      FROM IP_USUARIOS_PREDIAL
     WHERE COMPANIA = UN_COMPANIA 
       AND CODIGO = MI_CODIGO_PREDIO
       AND NUMERO_ORDEN = MI_NUMERO_ORDEN;
    /*Consultar en los parametros los conceptos configurados para recalcular
    los descuentos de la vigencia actual*/
    -- Concepto configurado para el de descuento
    MI_VALOR_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                UN_NOMBRE    => 'CONCEPTO DE DESCUENTO', 
                                                UN_MODULO    => PCK_DATOS.MODULOPREDIAL, 
                                                UN_FECHA_PAR => SYSDATE);
    MI_CONCEPTO_DESCUENTO := NVL(TO_NUMBER(MI_VALOR_PARAMETRO, '99'), 0);
    -- Concepto configurado para el de descuento CAR
    MI_VALOR_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                UN_NOMBRE    => 'CONCEPTO PARA DESCUENTO CAR', 
                                                UN_MODULO    => PCK_DATOS.MODULOPREDIAL, 
                                                UN_FECHA_PAR => SYSDATE);
    MI_CONCEPTO_DESCUENTO_CAR := NVL(TO_NUMBER(MI_VALOR_PARAMETRO, '99'), 0);  
    -- Inicialización de arreglos
    MI_FECHAS.EXTEND(12);
    MI_INTERESES.EXTEND(12);
    MI_INTERESES_CAR.EXTEND(12);
    MI_DESCUENTOS.EXTEND(12);
    MI_DESCUENTOS_CAR.EXTEND(12);
    MI_TOTALES.EXTEND(12);
    FOR i IN 1..12
    LOOP
      MI_FECHAS(i) := TO_DATE('01/01/1900','DD/MM/YYYY');
      MI_INTERESES(i) := 0;
      MI_INTERESES_CAR(i) := 0;
      MI_DESCUENTOS(i) := 0;
      MI_DESCUENTOS_CAR(i) := 0;
      MI_TOTALES(i) := 0;
    END LOOP;
    --
    SELECT SIGLACOMPANIA 
      INTO MI_SIGLA_COMPANIA
      FROM COMPANIA 
     WHERE CODIGO = UN_COMPANIA;
    -- Validación de proceso por compañía
    IF MI_SIGLA_COMPANIA = 'AGUAZUL' THEN
      -- Ejecuta proceso especial para Aguazul
      DECLARE 
        MI_CONDICION                    PCK_SUBTIPOS.TI_CONDICION;                    
      BEGIN
        MI_CONDICION := 'AND PREANO' 
                      || CASE WHEN UN_UNICO_ANO <> 0 THEN ' = ' ELSE ' <= ' END
                      || UN_ANO_FINAL;
        PCK_PREDIAL_COM3.PR_RECIBOS_MULTIFECHA (UN_COMPANIA               => UN_COMPANIA, 
                                                UN_DOCNUM                 => UN_DOCNUM, 
                                                UN_USUARIO                => UN_USUARIO, 
                                                UN_COD_PREDIO             => MI_CODIGO_PREDIO, 
                                                UN_NUMERO_ORDEN           => MI_NUMERO_ORDEN, 
                                                UN_CONDICION              => MI_CONDICION, 
                                                UN_APLICA_DSCTO_ESPECIAL  => UN_APLICA_DSCTO_ESPECIAL, 
                                                UN_UNICO_ANO              => UN_UNICO_ANO, 
                                                UN_ANO_INICIAL            => UN_ANO_INICIAL, 
                                                UN_ANO_FINAL              => UN_ANO_FINAL);
      END;
    ELSE
      -- Los descuentos solo se deben aplicar para la vigencia actual
      IF UN_ANO_INICIAL = EXTRACT(YEAR FROM SYSDATE) THEN
        IF MI_CONCEPTO_DESCUENTO NOT IN(0) THEN
          SELECT FORMULA
            INTO MI_FORMULA_DESCUENTOS
            FROM IP_CONCEPTOS
           WHERE ANO = EXTRACT(YEAR FROM SYSDATE)
             AND CODIGO = MI_CONCEPTO_DESCUENTO;
          --
          MI_VLR_ABONO_CONCEPTO := PCK_PREDIAL_COM3.FC_DESCABONOS(UN_COMPANIA   => UN_COMPANIA, 
                                                                  UN_CODPREDIO  => MI_CODIGO_PREDIO, 
                                                                  UN_CONCEPTO   => MI_CONCEPTO_DESCUENTO);
        END IF;
        IF MI_CONCEPTO_DESCUENTO_CAR NOT IN(0) THEN
          SELECT FORMULA
            INTO MI_FORMULA_DESCUENTOS_CAR
            FROM IP_CONCEPTOS
           WHERE ANO = EXTRACT(YEAR FROM SYSDATE)
             AND CODIGO = MI_CONCEPTO_DESCUENTO_CAR;
          --
          MI_VLR_ABONO_CONCEPTO_CAR := PCK_PREDIAL_COM3.FC_DESCABONOS(UN_COMPANIA  => UN_COMPANIA, 
                                                                      UN_CODPREDIO => MI_CODIGO_PREDIO, 
                                                                      UN_CONCEPTO  => MI_CONCEPTO_DESCUENTO_CAR);
        END IF;
        -- Descuento especial en saldos a favor
        MI_DESCUENTO_NC := PCK_PREDIAL_COM3.FC_DESC_FAC(UN_COMPANIA    => UN_COMPANIA, 
                                                        UN_CODPREDIO   => MI_CODIGO_PREDIO, 
                                                        UN_ANO_INICIAL => UN_ANO_INICIAL, 
                                                        UN_ANO_FINAL   => UN_ANO_FINAL);
        -- Valor de incremento del avalúo
        MI_INCREMENTO_AVALUO := PCK_PREDIAL_COM3.FC_INCREMENTOAVALUO(UN_COMPANIA => UN_COMPANIA, 
                                                                     UN_CODPREDIO => MI_CODIGO_PREDIO);
        -- Verifica si hay descuentos especiales
        DECLARE
          MI_VAR_AUX                     NUMBER(1);
        BEGIN
          SELECT 1
            INTO MI_VAR_AUX
            FROM IP_DESCUENTOS_ESPECIALES
           WHERE COMPANIA = UN_COMPANIA
             AND ANO = EXTRACT(YEAR FROM SYSDATE)
             AND MES >= EXTRACT(MONTH FROM SYSDATE)
             AND MI_CODIGO_PREDIO BETWEEN CODIGO_INICIAL AND CODIGO_FINAL
             AND MI_INCREMENTO_AVALUO BETWEEN INCREM_INICIAL AND INCREM_FINAL
             AND CLASE_PREDIO = MI_CLASE_PREDIO
           ORDER BY MES, IND_PRIORIDAD;
          MI_IND_DESCUENTO_ESPECIAL := TRUE;
          MI_STRSQL := 'SELECT MES,
                   PORC_DSCTO AS PORCENTAJE,
                   EXTRACT(DAY FROM FECHA_LIMITE) AS DIA_LIMITE
              FROM IP_DESCUENTOS_ESPECIALES
             WHERE COMPANIA = :p_compania
               AND ANO = EXTRACT(YEAR FROM SYSDATE)
               AND MES >= EXTRACT(MONTH FROM SYSDATE)
               AND :p_predio BETWEEN CODIGO_INICIAL AND CODIGO_FINAL
               AND :p_incremento_avaluo BETWEEN INCREM_INICIAL AND INCREM_FINAL
               AND CLASE_PREDIO = :p_clase_predio
             ORDER BY MES, IND_PRIORIDAD';
          OPEN CURSOR_DESCUENTOS FOR MI_STRSQL 
          USING UN_COMPANIA, MI_CODIGO_PREDIO, MI_INCREMENTO_AVALUO, MI_CLASE_PREDIO;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          MI_IND_DESCUENTO_ESPECIAL := FALSE;
          MI_STRSQL := q'[SELECT MES,
                 PORCENTAJE,
                 DIA_LIMITE
            FROM IP_DESCUENTOS_ANO
           WHERE COMPANIA = :p_compania
             AND ANO = EXTRACT(YEAR FROM SYSDATE)
             AND TO_DATE(DIA_LIMITE || '/' || MES || '/' || ANO, 'DD/MM/YYYY') >= SYSDATE
           ORDER BY MES, DIA_LIMITE]';
          OPEN CURSOR_DESCUENTOS FOR MI_STRSQL 
          USING UN_COMPANIA;
        END;
        --
        DECLARE
          MI_MES_TRABAJO              PCK_SUBTIPOS.TI_PARAMETRO := 0;
          MI_CONTADOR                 PCK_SUBTIPOS.TI_ENTERO := 1;
          MI_VALOR                    PCK_SUBTIPOS.TI_DOBLE := 0;
          MI_FECHA_CORTE              DATE := SYSDATE;
        BEGIN
          <<calcular_descuentos>>
          LOOP
            FETCH CURSOR_DESCUENTOS INTO RS_MES, RS_PORCENTAJE, RS_DIA_LIMITE;
            EXIT WHEN CURSOR_DESCUENTOS%NOTFOUND;
            IF MI_MES_TRABAJO = 0 THEN
              MI_MES_TRABAJO := RS_MES;
            ELSIF MI_MES_TRABAJO = RS_MES AND MI_IND_DESCUENTO_ESPECIAL THEN
              CONTINUE;
            ELSE
              MI_MES_TRABAJO := RS_MES;
            END IF;
            MI_FECHAS(MI_CONTADOR) := TO_DATE(RS_DIA_LIMITE || '/' || RS_MES  || '/' || EXTRACT(YEAR FROM SYSDATE), 'DD/MM/YYYY');
            -- Resolución de formula configurada para descuentos.
            MI_FORMULA_DESCUENTOS := UPPER(MI_FORMULA_DESCUENTOS);
            MI_FORMULA_DESCUENTOS := REPLACE(MI_FORMULA_DESCUENTOS, 'DESCUENTOSMES()', RS_PORCENTAJE);
            MI_FORMULA_DESCUENTOS := PCK_PREDIAL_COM3.FC_EVALUAR_CONCEPTOS(UN_FORMULA   => MI_FORMULA_DESCUENTOS, 
                                                                           UN_COMPANIA  => UN_COMPANIA, 
                                                                           UN_ANO       => UN_ANO_FINAL, 
                                                                           UN_CODIGO    => MI_CODIGO_PREDIO);
            MI_VALOR := PCK_SYSMAN_UTL.FC_EVAL(UN_CADENA => MI_FORMULA_DESCUENTOS);
            IF MI_VALOR < 0 THEN
              MI_DESCUENTOS(MI_CONTADOR) := - ROUND(ABS(MI_VALOR)) - MI_DESCUENTO_NC - MI_VLR_ABONO_CONCEPTO;
            ELSE 
              MI_DESCUENTOS(MI_CONTADOR) := ROUND(MI_VALOR) - MI_DESCUENTO_NC - MI_VLR_ABONO_CONCEPTO;
            END IF;
            --
            MI_FORMULA_DESCUENTOS_CAR := REPLACE(MI_FORMULA_DESCUENTOS_CAR, 'DESCUENTOSMES()', RS_PORCENTAJE);
            MI_FORMULA_DESCUENTOS_CAR := PCK_PREDIAL_COM3.FC_EVALUAR_CONCEPTOS(UN_FORMULA   => MI_FORMULA_DESCUENTOS_CAR, 
                                                                               UN_COMPANIA  => UN_COMPANIA, 
                                                                               UN_ANO       => UN_ANO_FINAL, 
                                                                               UN_CODIGO    => MI_CODIGO_PREDIO);
            MI_VALOR :=  PCK_SYSMAN_UTL.FC_EVAL(UN_CADENA => MI_FORMULA_DESCUENTOS_CAR);
            IF MI_VALOR < 0 THEN
              MI_DESCUENTOS_CAR(MI_CONTADOR) := - ROUND(ABS(MI_VALOR)) - MI_VLR_ABONO_CONCEPTO_CAR;
            ELSE 
              MI_DESCUENTOS_CAR(MI_CONTADOR) := ROUND(MI_VALOR) - MI_VLR_ABONO_CONCEPTO;
            END IF;
            DECLARE
              /*Conceptos que no se requieren para calcular el total. 
              Ej: Conceptos en los cuales se calculan acumulativos de otros.*/
              MI_CONCEPTOS_EXCLUIDOS          VARCHAR2(55 CHAR);
              -- 
              MI_VALOR_CONCEPTO               PCK_SUBTIPOS.TI_DOBLE := 0;
              MI_STRSQL                       PCK_SUBTIPOS.TI_STRSQL;
            BEGIN
              MI_CONCEPTOS_EXCLUIDOS := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                              UN_NOMBRE    => 'EXCLUIR CONCEPTOS PARA TOTAL MULTIFECHAS', 
                                                              UN_MODULO    => PCK_DATOS.MODULOPREDIAL, 
                                                              UN_FECHA_PAR => SYSDATE);
              <<calcular_totales>>
              FOR i IN 1..20
              LOOP
                IF INSTR(MI_CONCEPTOS_EXCLUIDOS, TO_CHAR(i, '00')) = 0
                THEN
                    IF i = MI_CONCEPTO_DESCUENTO THEN
                      MI_TOTALES(MI_CONTADOR) := MI_TOTALES(MI_CONTADOR) + MI_DESCUENTOS(MI_CONTADOR);
                    ELSIF i = MI_CONCEPTO_DESCUENTO_CAR THEN
                      MI_TOTALES(MI_CONTADOR) := MI_TOTALES(MI_CONTADOR) + MI_DESCUENTOS_CAR(MI_CONTADOR);
                    ELSE
                      MI_STRSQL := 'SELECT C' || i || 
                                  '  FROM IP_RECIBOS_DE_PAGO 
                                   WHERE COMPANIA = :p_compania 
                                     AND DOCNUM = :p_docnum';
                      EXECUTE IMMEDIATE MI_STRSQL INTO MI_VALOR_CONCEPTO
                      USING UN_COMPANIA, UN_DOCNUM;
                      MI_TOTALES(MI_CONTADOR) := MI_TOTALES(MI_CONTADOR) + MI_VALOR_CONCEPTO;
                    END IF;
                END IF;
              END LOOP calcular_totales;
            END;
            --
            MI_CONTADOR := MI_CONTADOR +1;
          END LOOP calcular_descuentos;
          CLOSE CURSOR_DESCUENTOS;
        END;
      END IF;
      -- Actualización del recibo de pago
      DECLARE
        MI_CAMPOS                       PCK_SUBTIPOS.TI_CAMPOS;
        MI_CONDICION                    PCK_SUBTIPOS.TI_CONDICION;
        MI_PCKDATOS                     PCK_SUBTIPOS.TI_RTA_ACME;
        MI_REEMPLAZOS                   PCK_SUBTIPOS.TI_CLAVEVALOR;
      BEGIN
        MI_CAMPOS := 'IND_MULTIFECHAS = -1 ' || 
         ', FECHA1 = TO_DATE(''' || TO_CHAR(MI_FECHAS(1), 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
         ', FECHA2 = TO_DATE(''' || TO_CHAR(MI_FECHAS(2), 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
         ', FECHA3 = TO_DATE(''' || TO_CHAR(MI_FECHAS(3), 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
         ', FECHA4 = TO_DATE(''' || TO_CHAR(MI_FECHAS(4), 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
         ', FECHA5 = TO_DATE(''' || TO_CHAR(MI_FECHAS(5), 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
         ', FECHA6 = TO_DATE(''' || TO_CHAR(MI_FECHAS(6), 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
         ', FECHA7 = TO_DATE(''' || TO_CHAR(MI_FECHAS(7), 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
         ', FECHA8 = TO_DATE(''' || TO_CHAR(MI_FECHAS(8), 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
         ', FECHA9 = TO_DATE(''' || TO_CHAR(MI_FECHAS(9), 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
         ', FECHA10 = TO_DATE(''' || TO_CHAR(MI_FECHAS(10), 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
         ', FECHA11 = TO_DATE(''' || TO_CHAR(MI_FECHAS(11), 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
         ', FECHA12 = TO_DATE(''' || TO_CHAR(MI_FECHAS(12), 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
         ', DESC1 = ' || ROUND(MI_DESCUENTOS(1)) || 
         ', DESC2 = ' || ROUND(MI_DESCUENTOS(2)) || 
         ', DESC3 = ' || ROUND(MI_DESCUENTOS(3)) || 
         ', DESC4 = ' || ROUND(MI_DESCUENTOS(4)) || 
         ', DESC5 = ' || ROUND(MI_DESCUENTOS(5)) || 
         ', DESC6 = ' || ROUND(MI_DESCUENTOS(6)) || 
         ', DESC7 = ' || ROUND(MI_DESCUENTOS(7)) || 
         ', DESC8 = ' || ROUND(MI_DESCUENTOS(8)) || 
         ', DESC9 = ' || ROUND(MI_DESCUENTOS(9)) || 
         ', DESC10 = ' || ROUND(MI_DESCUENTOS(10)) || 
         ', DESC11 = ' || ROUND(MI_DESCUENTOS(11)) || 
         ', DESC12 = ' || ROUND(MI_DESCUENTOS(12)) || 
         ', DESC_CAR1 = ' || ROUND(MI_DESCUENTOS_CAR(1)) || 
         ', DESC_CAR2 = ' || ROUND(MI_DESCUENTOS_CAR(2)) || 
         ', DESC_CAR3 = ' || ROUND(MI_DESCUENTOS_CAR(3)) || 
         ', DESC_CAR4 = ' || ROUND(MI_DESCUENTOS_CAR(4)) || 
         ', DESC_CAR5 = ' || ROUND(MI_DESCUENTOS_CAR(5)) || 
         ', DESC_CAR6 = ' || ROUND(MI_DESCUENTOS_CAR(6)) || 
         ', DESC_CAR7 = ' || ROUND(MI_DESCUENTOS_CAR(7)) || 
         ', DESC_CAR8 = ' || ROUND(MI_DESCUENTOS_CAR(8)) || 
         ', DESC_CAR9 = ' || ROUND(MI_DESCUENTOS_CAR(9)) || 
         ', DESC_CAR10 = ' || ROUND(MI_DESCUENTOS_CAR(10)) || 
         ', DESC_CAR11 = ' || ROUND(MI_DESCUENTOS_CAR(11)) || 
         ', DESC_CAR12 = ' || ROUND(MI_DESCUENTOS_CAR(12)) || 
         ', INT1 = ' || MI_INTERESES(1) ||  
         ', INT2 = ' || MI_INTERESES(2) ||  
         ', INT3 = ' || MI_INTERESES(3) ||  
         ', INT4 = ' || MI_INTERESES(4) ||  
         ', INT5 = ' || MI_INTERESES(5) ||  
         ', INT6 = ' || MI_INTERESES(6) ||  
         ', INT7 = ' || MI_INTERESES(7) ||  
         ', INT8 = ' || MI_INTERESES(8) ||  
         ', INT9 = ' || MI_INTERESES(9) ||  
         ', INT10 = ' || MI_INTERESES(10) ||  
         ', INT11 = ' || MI_INTERESES(11) ||  
         ', INT12 = ' || MI_INTERESES(12) ||  
         ', INTCAR1 = ' || MI_INTERESES_CAR(1) ||  
         ', INTCAR2 = ' || MI_INTERESES_CAR(2) ||  
         ', INTCAR3 = ' || MI_INTERESES_CAR(3) ||  
         ', INTCAR4 = ' || MI_INTERESES_CAR(4) ||  
         ', INTCAR5 = ' || MI_INTERESES_CAR(5) ||  
         ', INTCAR6 = ' || MI_INTERESES_CAR(6) ||  
         ', INTCAR7 = ' || MI_INTERESES_CAR(7) ||  
         ', INTCAR8 = ' || MI_INTERESES_CAR(8) ||  
         ', INTCAR9 = ' || MI_INTERESES_CAR(9) ||  
         ', INTCAR10 = ' || MI_INTERESES_CAR(10) ||  
         ', INTCAR11 = ' || MI_INTERESES_CAR(11) ||  
         ', INTCAR12 = ' || MI_INTERESES_CAR(12) ||  
         ', TOTAL1 = ' || MI_TOTALES(1) ||  
         ', TOTAL2 = ' || MI_TOTALES(2) ||  
         ', TOTAL3 = ' || MI_TOTALES(3) ||  
         ', TOTAL4 = ' || MI_TOTALES(4) ||  
         ', TOTAL5 = ' || MI_TOTALES(5) ||  
         ', TOTAL6 = ' || MI_TOTALES(6) ||  
         ', TOTAL7 = ' || MI_TOTALES(7) ||  
         ', TOTAL8 = ' || MI_TOTALES(8) ||  
         ', TOTAL9 = ' || MI_TOTALES(9) ||  
         ', TOTAL10 = ' || MI_TOTALES(10) ||  
         ', TOTAL11 = ' || MI_TOTALES(11) ||  
         ', TOTAL12 = ' || MI_TOTALES(12) ||  
         ', DATE_MODIFIED = SYSDATE' ||
         ', MODIFIED_BY = ''' || UN_USUARIO || '''';
        MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA 
                        ||''' AND DOCNUM = ''' || UN_DOCNUM || '''';
        MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA => 'IP_RECIBOS_DE_PAGO',
                                         UN_ACCION => 'U', 
                                         UN_CAMPOS => MI_CAMPOS,
                                         UN_CONDICION => MI_CONDICION);
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        MI_REEMPLAZOS(1).CLAVE := 'DOCNUM';
        MI_REEMPLAZOS(1).VALOR := UN_DOCNUM;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD => SQLCODE,
          UN_TABLAERROR => 'IP_RECIBOS_DE_PAGO',
          UN_ERROR_COD => PCK_ERRORES.ERR_PREDIAL_MULTIFECHAS,
          UN_REEMPLAZOS => MI_REEMPLAZOS
        );
      END;
    END IF;
  END PR_FACTURAR_MULTIFECHAS;

  --27. recibosMultifecha
  PROCEDURE PR_RECIBOS_MULTIFECHA 
  /*
  NAME              : PR_RECIBOS_MULTIFECHA
  AUTHORS           : SYSMAN SAS
  AUTHOR MIGRACION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE MIGRADOR     : 17/03/2017
  TIME              : 05:38 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Proceso de facturación de multifechas para Aguazul. 
                      Cálculo de intereses.
  MODIFICATIONS     : 
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.
    UN_DOCNUM: Numero de recibo de pago.
    UN_USUARIO: Nombre del usuario que se identificó en la aplicación.
    UN_COD_PREDIO: Predio del cual se está consultando las vigencias pagas.
    UN_NUMERO_ORDEN: Número de orden del propietario del predio.
    UN_CONDICION: Condición año predial.
    UN_APLICA_DSCTO_ESPECIAL: Valor del indicador "Ley 1607 de 2012".
    UN_UNICO_ANO: Valor del indicador "Un sólo Año".
    UN_ANO_INICIAL: Año desde el que se factura.
    UN_ANO_FINAL: Año hasta el que se factura.

  @NAME: recibosMultifecha
  @METHOD: post
  */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_DOCNUM                       IN IP_RECIBOS_MULTIFECHA.DOCNUM%TYPE
  , UN_USUARIO                      IN PCK_SUBTIPOS.TI_USUARIO
  , UN_COD_PREDIO                   IN IP_RECIBOS_MULTIFECHA.COD_PREDIO%TYPE
  , UN_NUMERO_ORDEN                 IN IP_RECIBOS_MULTIFECHA.NUMERO_ORDEN%TYPE
  , UN_CONDICION                    IN PCK_SUBTIPOS.TI_CONDICION
  , UN_APLICA_DSCTO_ESPECIAL        IN PCK_SUBTIPOS.TI_LOGICO  
  , UN_UNICO_ANO                    IN PCK_SUBTIPOS.TI_LOGICO
  , UN_ANO_INICIAL                  IN PCK_SUBTIPOS.TI_ANIO
  , UN_ANO_FINAL                    IN PCK_SUBTIPOS.TI_ANIO
  ) AS 
    TYPE MULTIFECHA IS RECORD (
      FECHA                         DATE
    , DESCUENTO                     PCK_SUBTIPOS.TI_DOBLE
    , IMP_ACT                       PCK_SUBTIPOS.TI_DOBLE
    , IMP_ANT                       PCK_SUBTIPOS.TI_DOBLE
    , IMP_DR                        PCK_SUBTIPOS.TI_DOBLE
    , INT_ACT                       PCK_SUBTIPOS.TI_DOBLE
    , INT_ANT                       PCK_SUBTIPOS.TI_DOBLE
    , INT_DR                        PCK_SUBTIPOS.TI_DOBLE
    , TOTAL                         PCK_SUBTIPOS.TI_DOBLE
    );  
    TYPE TI_MULTIFECHA IS TABLE OF MULTIFECHA INDEX BY BINARY_INTEGER;
    MI_DATOS_MULTIFECHA             TI_MULTIFECHA;
    MI_MES_ANTERIOR                 PCK_SUBTIPOS.TI_ENTERO := EXTRACT(MONTH FROM SYSDATE);
    MI_ANIO_ACTUAL                  PCK_SUBTIPOS.TI_ENTERO := EXTRACT(YEAR FROM SYSDATE);
    MI_VALOR_PARAMETRO              PCK_SUBTIPOS.TI_PARAMETRO;
    MI_EXCLUYE_VIGENCIAS_ACUERDO    BOOLEAN;
    MI_STRSQL                       PCK_SUBTIPOS.TI_STRSQL;
    MI_CONDICION_ACUERDO_PAGO       PCK_SUBTIPOS.TI_CONDICION := '';
    RS                              SYS_REFCURSOR; 
    RS_TRPCOD                       IP_FACTURADOS.TRPCOD%TYPE;
    RS_AVALUO                       IP_FACTURADOS.AVALUO%TYPE;
    RS_TRPRAN                       IP_FACTURADOS.TRPRAN%TYPE;
    RS_C1                           IP_FACTURADOS.C1%TYPE;
    RS_C2                           IP_FACTURADOS.C2%TYPE;
    RS_PREANO                       IP_FACTURADOS.PREANO%TYPE;
    RS_ALDIAINTERES                 IP_FACTURADOS.ALDIAINTERES%TYPE;
    RS_INDPAGO_ACPAG                IP_FACTURADOS.INDPAGO_ACPAG%TYPE;
    MI_PERIODOS_CALCULO             PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_PERIODO_INICIAL              PCK_SUBTIPOS.TI_ENTERO := EXTRACT(MONTH FROM ADD_MONTHS(SYSDATE, -1));
  BEGIN
    -- INICIALIZACIÓN DE DATOS MULTIFECHA
    FOR i IN 1..12
    LOOP
      MI_DATOS_MULTIFECHA(i).FECHA := TO_DATE('01/01/1900','DD/MM/YYYY');
      MI_DATOS_MULTIFECHA(i).DESCUENTO := 0;
      MI_DATOS_MULTIFECHA(i).IMP_ACT := 0;
      MI_DATOS_MULTIFECHA(i).IMP_ANT := 0;
      MI_DATOS_MULTIFECHA(i).IMP_DR := 0;
      MI_DATOS_MULTIFECHA(i).INT_ACT := 0;
      MI_DATOS_MULTIFECHA(i).INT_ANT := 0;
      MI_DATOS_MULTIFECHA(i).INT_DR := 0;
      MI_DATOS_MULTIFECHA(i).TOTAL := 0;
    END LOOP;
    --
    MI_VALOR_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(
      UN_COMPANIA  => UN_COMPANIA
    , UN_NOMBRE    => 'EXCLUIR VIGENCIAS EN ACUERDO'
    , UN_MODULO    => PCK_DATOS.MODULOPREDIAL
    , UN_FECHA_PAR => SYSDATE
    );
    MI_EXCLUYE_VIGENCIAS_ACUERDO := NVL(MI_VALOR_PARAMETRO, 'NO') = 'SI';
    IF MI_EXCLUYE_VIGENCIAS_ACUERDO THEN
      MI_CONDICION_ACUERDO_PAGO := 'AND INDPAGO_ACPAG = 0' || CHR(10) || CHR(13);
    END IF;
    MI_STRSQL := 'SELECT TRPCOD, AVALUO, TRPRAN, C1, C2, 
           PREANO, ALDIAINTERES, INDPAGO_ACPAG
      FROM IP_FACTURADOS
      WHERE COMPANIA    = :p_compania
      AND CODIGO        = :p_predio
      AND PAGADO        = 0
      AND INDEXE        = 0
      AND NOCOBRADO     = 0' || CHR(10) || CHR(13) ||
      MI_CONDICION_ACUERDO_PAGO || 
      UN_CONDICION || CHR(10) || CHR(13) ||
      'ORDER BY PREANO';
    <<procesar_facturado>>
    OPEN RS FOR MI_STRSQL USING UN_COMPANIA, UN_COD_PREDIO;
    LOOP
      FETCH RS INTO RS_TRPCOD, RS_AVALUO, RS_TRPRAN, RS_C1, RS_C2, RS_PREANO, 
      RS_ALDIAINTERES, RS_INDPAGO_ACPAG;
      EXIT WHEN RS%NOTFOUND;
      DECLARE
        MI_FORMULA_INTERESES            IP_CONCEPTOS.FORMULA%TYPE;
        MI_FORMULA_DESCUENTOS           IP_CONCEPTOS.FORMULA%TYPE;
        MI_DIGITOS_REDONDEO             PCK_SUBTIPOS.TI_ENTERO;
        MI_MES_FECHA_ANTERIOR           PCK_SUBTIPOS.TI_ENTERO;
        MI_FECHA_AUX                    DATE;
        MI_AMNISTIA                     PCK_SUBTIPOS.TI_ENTERO := 0;
        MI_ANO_MAX_DSCTOESP             PCK_SUBTIPOS.TI_ANIO;
        MI_FECHA_LIMITE_DSCTOESP        DATE;
        MI_PORCENTAJE_DSCTOESP          PCK_SUBTIPOS.TI_PORCENTAJE;
        MI_VALOR_PORCENTAJE             PCK_SUBTIPOS.TI_PORCENTAJE;
        MI_NITCOMPANIA                  COMPANIA.NITCOMPANIA%TYPE;
        MI_ANO_MIN_DSCTOESP             PCK_SUBTIPOS.TI_ANIO;
      BEGIN
        -- Extracción de formula para el concepto de intereses
        SELECT FORMULA
          INTO MI_FORMULA_INTERESES
          FROM IP_CONCEPTOS
         WHERE COMPANIA = UN_COMPANIA
           AND ANO = RS_PREANO
           AND CODIGO = 2;
        -- Extracción de formula para el concepto de descuentos
        SELECT FORMULA
          INTO MI_FORMULA_DESCUENTOS
          FROM IP_CONCEPTOS
         WHERE COMPANIA = UN_COMPANIA
           AND ANO = RS_PREANO
           AND CODIGO = 13;
        --
        MI_VALOR_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(
          UN_COMPANIA  => UN_COMPANIA
        , UN_NOMBRE    => 'DIGITOS DE REDONDEO PARA ABONOS'
        , UN_MODULO    => PCK_DATOS.MODULOPREDIAL
        , UN_FECHA_PAR => SYSDATE
        );
        MI_DIGITOS_REDONDEO := NVL(MI_VALOR_PARAMETRO * -1, 0);
        -- Preparación de formula de intereses
        MI_FORMULA_INTERESES := UPPER(MI_FORMULA_INTERESES);
        IF RS_ALDIAINTERES IS NULL THEN
          DECLARE
            MI_IND_EXEINT               IP_USUARIOS_PREDIAL.IND_EXEINT%TYPE;
            MI_FECINICIAL_EXEINT        IP_USUARIOS_PREDIAL.FECINICIAL_EXEINT%TYPE;
            MI_FECFINAL_EXEINT          IP_USUARIOS_PREDIAL.FECFINAL_EXEINT%TYPE;
            MI_MESES_MORA               PCK_SUBTIPOS.TI_ENTERO;
          BEGIN
            SELECT FECINICIAL_EXEINT, FECFINAL_EXEINT, IND_EXEINT
              INTO MI_FECINICIAL_EXEINT, MI_FECFINAL_EXEINT, MI_IND_EXEINT
              FROM IP_USUARIOS_PREDIAL
             WHERE COMPANIA = UN_COMPANIA
               AND CODIGO = UN_COD_PREDIO
               AND NUMERO_ORDEN = GL_NUMORDEN
               AND CODIGO_NO_ACTIVO = 0;
            --
            MI_MESES_MORA := PCK_PREDIAL_COM3.FC_MESESMORA(
              UN_COMPANIA => UN_COMPANIA
            , UN_ANO => EXTRACT(YEAR FROM SYSDATE)
            , UN_FECHACORTE => SYSDATE
            , UN_IND_EXEINT => MI_IND_EXEINT
            , UN_FECHAINICIAL_EXEINT => MI_FECINICIAL_EXEINT
            , UN_FECHAFINAL_EXEINT => MI_FECFINAL_EXEINT
            );
            --
            MI_FORMULA_INTERESES := REPLACE(MI_FORMULA_INTERESES,  'MESESMORA()', MI_MESES_MORA);
          END;
          DECLARE
            MI_VALOR_TASA             PCK_SUBTIPOS.TI_DOBLE;
          BEGIN
            MI_VALOR_TASA := PCK_PREDIAL_COM3.FC_TASAINTERESTARIFA(
              UN_COMPANIA => UN_COMPANIA
            , UN_CODIGOTARIFA => RS_TRPCOD
            , UN_ANOTARIFA => RS_PREANO
            , UN_AVALUOTARIFA => RS_AVALUO
            , UN_RANGOTARIFA => RS_TRPRAN
            );
            MI_FORMULA_INTERESES := REPLACE(MI_FORMULA_INTERESES, 'TASAINTERESTARIFA()', MI_VALOR_TASA);
          END;
        ELSE
          DECLARE
            MI_MESES_MORA             PCK_SUBTIPOS.TI_ENTERO;
          BEGIN
            MI_MESES_MORA := PCK_PREDIAL_COM3.FC_MESESMORA_FECHA(
              UN_COMPANIA => UN_COMPANIA
            , UN_FECHA_AL_DIA => RS_ALDIAINTERES
            , UN_FECHA_CORTE => SYSDATE
            );
            MI_FORMULA_INTERESES := REPLACE(MI_FORMULA_INTERESES, 'MESESMORA()', MI_MESES_MORA);
          END;
          MI_FORMULA_INTERESES := REPLACE(MI_FORMULA_INTERESES,  'TASAINTERESTARIFA()', 'PCK_PREDIAL_COM3.FC_TASA');
          --
          MI_FORMULA_INTERESES := PCK_PREDIAL_COM3.FC_EVALUAR_CONCEPTOS(
            UN_FORMULA => MI_FORMULA_INTERESES
          , UN_COMPANIA => UN_COMPANIA
          , UN_ANO => EXTRACT(YEAR FROM SYSDATE) 
          , UN_CODIGO => UN_COD_PREDIO
          , UN_DIGITOS_REDONDEO => MI_DIGITOS_REDONDEO
          );
        END IF;
        --      
        MI_VALOR_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(
          UN_COMPANIA  => UN_COMPANIA
        , UN_NOMBRE    => 'VIGENCIA MAX PERMITIDA OPCION DESC ESP'
        , UN_MODULO    => PCK_DATOS.MODULOPREDIAL
        , UN_FECHA_PAR => SYSDATE
        );
        MI_ANO_MAX_DSCTOESP := NVL(MI_VALOR_PARAMETRO, 0);
        --
        MI_VALOR_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(
          UN_COMPANIA  => UN_COMPANIA
        , UN_NOMBRE    => 'FECHA LIMITE DE APLICACION DESCUENTO ESPECIAL'
        , UN_MODULO    => PCK_DATOS.MODULOPREDIAL
        , UN_FECHA_PAR => SYSDATE
        );
        MI_FECHA_LIMITE_DSCTOESP := TO_DATE(MI_VALOR_PARAMETRO, 'DD/MM/YYYY');
        --
        MI_VALOR_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(
          UN_COMPANIA  => UN_COMPANIA
        , UN_NOMBRE    => 'PORCENTAJE DESCUENTO ESPECIAL'
        , UN_MODULO    => PCK_DATOS.MODULOPREDIAL
        , UN_FECHA_PAR => SYSDATE
        );
        MI_PORCENTAJE_DSCTOESP := NVL(MI_VALOR_PARAMETRO, '1');
        --
        MI_VALOR_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(
          UN_COMPANIA  => UN_COMPANIA
        , UN_NOMBRE    => 'PERIODOS CALCULO INTERES MF'
        , UN_MODULO    => PCK_DATOS.MODULOPREDIAL
        , UN_FECHA_PAR => SYSDATE
        );
        MI_PERIODOS_CALCULO := TO_NUMBER(MI_VALOR_PARAMETRO);
        -- Preparación de formula de descuentos
        MI_FORMULA_DESCUENTOS := UPPER(MI_FORMULA_DESCUENTOS);
        --
        MI_FORMULA_INTERESES := REPLACE(MI_FORMULA_INTERESES, 'CONCEPTO(1)', RS_C1);
        --
        IF  RS_PREANO = MI_ANIO_ACTUAL THEN
          DECLARE
            MI_DESCUENTO_MENSUAL      PCK_SUBTIPOS.TI_DOBLE;
          BEGIN
            MI_DESCUENTO_MENSUAL := PCK_PREDIAL_COM3.FC_DESCUENTOMES_FACTURA(
              UN_COMPANIA => UN_COMPANIA
            , UN_MES => MI_MES_ANTERIOR
            );
            MI_FORMULA_DESCUENTOS := REPLACE(MI_FORMULA_DESCUENTOS, 'DESCUENTOSMES()', MI_DESCUENTO_MENSUAL);
          END;
          --
          MI_FORMULA_DESCUENTOS := PCK_PREDIAL_COM3.FC_EVALUAR_CONCEPTOS(
            UN_FORMULA => MI_FORMULA_DESCUENTOS
          , UN_COMPANIA => UN_COMPANIA
          , UN_ANO => EXTRACT(YEAR FROM SYSDATE) 
          , UN_CODIGO => UN_COD_PREDIO
          );
          --
          <<ciclo_periodos>>
          FOR i IN 1..MI_PERIODOS_CALCULO
          LOOP
            -- Si la fecha es mayor que el último día del año evalúe el año siguiente
            IF MI_MES_ANTERIOR = 12 AND (MI_MES_ANTERIOR +1) > 12 AND i > 1 THEN
              MI_FECHA_AUX := TO_DATE('01/' || MI_MES_ANTERIOR + i - 12 || '/' 
                || MI_ANIO_ACTUAL + 1, 'DD/MM/YYYY') -1;
              MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).FECHA := MI_FECHA_AUX;
              MI_FORMULA_DESCUENTOS := REPLACE(MI_FORMULA_DESCUENTOS, 'FACTURA(' 
                || MI_MES_ANTERIOR + i -2 || ')', 'FACTURA(' || MI_MES_ANTERIOR + i - 1 || ')');
            ELSE
              MI_FECHA_AUX := TO_DATE('01/' || MI_MES_ANTERIOR + i || '/' 
                || MI_ANIO_ACTUAL, 'DD/MM/YYYY') -1;
              MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).FECHA := MI_FECHA_AUX;
              IF i > 1 THEN
                MI_FORMULA_DESCUENTOS := REPLACE(MI_FORMULA_DESCUENTOS, 'FACTURA(' 
                  || MI_MES_FECHA_ANTERIOR + i -2 || ')', 'FACTURA(' || MI_MES_ANTERIOR + i - 1 || ')');
              END IF;
              MI_MES_FECHA_ANTERIOR := EXTRACT(MONTH FROM MI_FECHA_AUX);
            END IF;
            --
            DECLARE
              MI_VALOR                  PCK_SUBTIPOS.TI_DOBLE;
            BEGIN
              MI_VALOR := PCK_PREDIAL_COM3.FC_MORAACUMULADAMENSUAL(
                UN_COMPANIA => UN_COMPANIA
              , UN_CODIGO => UN_COD_PREDIO
              , UN_ANO => EXTRACT(YEAR FROM SYSDATE)
              , UN_FECHA_CORTE => MI_FECHA_AUX
              );
              MI_FORMULA_INTERESES := REPLACE(MI_FORMULA_INTERESES, 'MORAACUMULADAMENSUAL()', MI_VALOR);
            END;
            --
            MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).IMP_ACT := RS_C1;
            MI_AMNISTIA := PCK_PREDIAL_COM3.FC_AMNISTIA(
              UN_COMPANIA => UN_COMPANIA, UN_ANO => MI_ANIO_ACTUAL);
            IF MI_MES_ANTERIOR > MI_AMNISTIA THEN
              IF NOT MI_EXCLUYE_VIGENCIAS_ACUERDO AND RS_INDPAGO_ACPAG <> 0 THEN
                MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).INT_ACT := RS_C2;
              ELSE
                IF UN_APLICA_DSCTO_ESPECIAL <> 0 AND RS_PREANO <=  MI_ANO_MAX_DSCTOESP AND MI_FECHA_AUX <= MI_FECHA_LIMITE_DSCTOESP THEN
                  SELECT NITCOMPANIA 
                    INTO MI_NITCOMPANIA 
                    FROM COMPANIA 
                   WHERE CODIGO = UN_COMPANIA;
                  MI_VALOR_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(
                    UN_COMPANIA  => UN_COMPANIA
                  , UN_NOMBRE    => 'VIGENCIA MIN POR PAGAR PARA DESC ESP'
                  , UN_MODULO    => PCK_DATOS.MODULOPREDIAL
                  , UN_FECHA_PAR => SYSDATE
                  );
                  MI_ANO_MIN_DSCTOESP := TO_NUMBER(MI_ANO_MIN_DSCTOESP);
                  MI_VALOR_PORCENTAJE := PCK_PREDIAL_COM3.FC_EVALUARPORCDESCUENTO(
                    UN_COMPANIA => UN_COMPANIA
                  , UN_NITCOMPANIA => MI_NITCOMPANIA
                  , UN_ANO => RS_PREANO
                  , UN_ANOMINDESC => MI_ANO_MIN_DSCTOESP
                  , UN_PORCENTAJE => MI_PORCENTAJE_DSCTOESP
                  , UN_PORCENTAJEGR => MI_PORCENTAJE_DSCTOESP
                  , UN_CODTARIFA => RS_TRPCOD
                  );
                  MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).INT_ACT := ROUND(PCK_SYSMAN_UTL.EVAL(MI_FORMULA_INTERESES)) * (1 - MI_VALOR_PORCENTAJE);
                ELSE
                  MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).INT_ACT := ROUND(PCK_SYSMAN_UTL.EVAL(MI_FORMULA_INTERESES));
                END IF;
              END IF;
            END IF;
            IF EXTRACT(MONTH FROM MI_FECHA_AUX) > MI_AMNISTIA AND EXTRACT(MONTH FROM MI_FECHA_AUX) - MI_AMNISTIA > 1 THEN
              MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).INT_ACT := RS_C1 * (PCK_PREDIAL_COM3.FC_TASA() * EXTRACT(MONTH FROM MI_FECHA_AUX) - MI_AMNISTIA - 1);
            END IF;
            MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).DESCUENTO := ROUND(ABS(PCK_SYSMAN_UTL.EVAL(MI_FORMULA_DESCUENTOS))) * -1;
          END LOOP ciclo_periodos;
        ELSIF RS_PREANO = MI_ANIO_ACTUAL - 1 THEN 
          FOR i IN 1..MI_PERIODOS_CALCULO 
          LOOP            --
            DECLARE
              MI_VALOR                  PCK_SUBTIPOS.TI_DOBLE;
            BEGIN
              MI_VALOR := PCK_PREDIAL_COM3.FC_MORAACUMULADAMENSUAL(
                UN_COMPANIA => UN_COMPANIA
              , UN_CODIGO => UN_COD_PREDIO
              , UN_ANO => EXTRACT(YEAR FROM SYSDATE)
              , UN_FECHA_CORTE => MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).FECHA
              );
              MI_FORMULA_INTERESES := REPLACE(MI_FORMULA_INTERESES, 'MORAACUMULADAMENSUAL()', MI_VALOR);
            END;
            MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).IMP_ACT := RS_C1;
            IF NOT MI_EXCLUYE_VIGENCIAS_ACUERDO AND RS_INDPAGO_ACPAG <> 0 THEN
              MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).INT_ANT := RS_C2;
            ELSE
              IF UN_APLICA_DSCTO_ESPECIAL <> 0 AND RS_PREANO <=  MI_ANO_MAX_DSCTOESP AND MI_FECHA_AUX <= MI_FECHA_LIMITE_DSCTOESP THEN
                SELECT NITCOMPANIA 
                  INTO MI_NITCOMPANIA 
                  FROM COMPANIA 
                 WHERE CODIGO = UN_COMPANIA;
                MI_ANO_MIN_DSCTOESP := TO_NUMBER(MI_ANO_MIN_DSCTOESP);
                MI_VALOR_PORCENTAJE := PCK_PREDIAL_COM3.FC_EVALUARPORCDESCUENTO(
                  UN_COMPANIA => UN_COMPANIA
                , UN_NITCOMPANIA => MI_NITCOMPANIA
                , UN_ANO => RS_PREANO
                , UN_ANOMINDESC => MI_ANO_MIN_DSCTOESP
                , UN_PORCENTAJE => MI_PORCENTAJE_DSCTOESP
                , UN_PORCENTAJEGR => MI_PORCENTAJE_DSCTOESP
                , UN_CODTARIFA => RS_TRPCOD
                );
                MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).INT_ANT := ROUND(PCK_SYSMAN_UTL.EVAL(MI_FORMULA_INTERESES)) * (1 - MI_VALOR_PORCENTAJE);
              ELSE
                MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).INT_ANT := ROUND(PCK_SYSMAN_UTL.EVAL(MI_FORMULA_INTERESES));
              END IF;
            END IF;
          END LOOP;
        ELSE
          FOR i IN 1..MI_PERIODOS_CALCULO 
          LOOP            
            DECLARE
              MI_VALOR                  PCK_SUBTIPOS.TI_DOBLE;
            BEGIN
              MI_VALOR := PCK_PREDIAL_COM3.FC_MORAACUMULADAMENSUAL(
                UN_COMPANIA => UN_COMPANIA
              , UN_CODIGO => UN_COD_PREDIO
              , UN_ANO => EXTRACT(YEAR FROM SYSDATE)
              , UN_FECHA_CORTE => MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).FECHA
              );
              MI_FORMULA_INTERESES := REPLACE(MI_FORMULA_INTERESES, 'MORAACUMULADAMENSUAL()', MI_VALOR);
            END;
            MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).IMP_DR := MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).IMP_DR + RS_C1;
            IF NOT MI_EXCLUYE_VIGENCIAS_ACUERDO AND RS_INDPAGO_ACPAG <> 0 THEN
              MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).INT_DR := MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).INT_DR + RS_C2;
            ELSE
              IF UN_APLICA_DSCTO_ESPECIAL <> 0 AND RS_PREANO <=  MI_ANO_MAX_DSCTOESP AND MI_FECHA_AUX <= MI_FECHA_LIMITE_DSCTOESP THEN
                SELECT NITCOMPANIA 
                  INTO MI_NITCOMPANIA 
                  FROM COMPANIA 
                 WHERE CODIGO = UN_COMPANIA;
                MI_ANO_MIN_DSCTOESP := TO_NUMBER(MI_ANO_MIN_DSCTOESP);
                MI_VALOR_PORCENTAJE := PCK_PREDIAL_COM3.FC_EVALUARPORCDESCUENTO(
                  UN_COMPANIA => UN_COMPANIA
                , UN_NITCOMPANIA => MI_NITCOMPANIA
                , UN_ANO => RS_PREANO
                , UN_ANOMINDESC => MI_ANO_MIN_DSCTOESP
                , UN_PORCENTAJE => MI_PORCENTAJE_DSCTOESP
                , UN_PORCENTAJEGR => MI_PORCENTAJE_DSCTOESP
                , UN_CODTARIFA => RS_TRPCOD
                );
                MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).INT_DR := MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).INT_DR 
                  + ROUND(PCK_SYSMAN_UTL.EVAL(MI_FORMULA_INTERESES)) * (1 - MI_VALOR_PORCENTAJE);
              ELSE
                MI_DATOS_MULTIFECHA(i + MI_PERIODO_INICIAL).INT_DR := ROUND(PCK_SYSMAN_UTL.EVAL(MI_FORMULA_INTERESES));
              END IF;
            END IF;
          END LOOP;
        END IF;
      END;
    END LOOP procesar_facturado;
    CLOSE RS;
    -- Inserción del recibo multifecha para Aguazul
    DECLARE
      MI_CAMPOS                       PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES                      PCK_SUBTIPOS.TI_VALORES;
      MI_PCKDATOS                     PCK_SUBTIPOS.TI_RTA_ACME;
      MI_REEMPLAZOS                   PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN
      MI_CAMPOS := 'COMPANIA,NUMERO_ORDEN,DOCNUM,COD_PREDIO,ANULADO,PAGO,FECHA1,'
        || 'DESC1,IMP_ACT1,INT_ACT1,IMP_ANT1,INT_ANT1,IMP_DR1,INT_DR1,TOTAL1,FECHA2,'
        || 'DESC2,IMP_ACT2,INT_ACT2,IMP_ANT2,INT_ANT2,IMP_DR2,INT_DR2,TOTAL2,FECHA3,'
        || 'DESC3,IMP_ACT3,INT_ACT3,IMP_ANT3,INT_ANT3,IMP_DR3,INT_DR3,TOTAL3,FECHA4,'
        || 'DESC4,IMP_ACT4,INT_ACT4,IMP_ANT4,INT_ANT4,IMP_DR4,INT_DR4,TOTAL4,FECHA5,'
        || 'DESC5,IMP_ACT5,INT_ACT5,IMP_ANT5,INT_ANT5,IMP_DR5,INT_DR5,TOTAL5,FECHA6,'
        || 'DESC6,IMP_ACT6,INT_ACT6,IMP_ANT6,INT_ANT6,IMP_DR6,INT_DR6,TOTAL6,FECHA7,'
        || 'DESC7,IMP_ACT7,INT_ACT7,IMP_ANT7,INT_ANT7,IMP_DR7,INT_DR7,TOTAL7,FECHA8,'
        || 'DESC8,IMP_ACT8,INT_ACT8,IMP_ANT8,INT_ANT8,IMP_DR8,INT_DR8,TOTAL8,FECHA9,'
        || 'DESC9,IMP_ACT9,INT_ACT9,IMP_ANT9,INT_ANT9,IMP_DR9,INT_DR9,TOTAL9,FECHA10,'
        || 'DESC10,IMP_ACT10,INT_ACT10,IMP_ANT10,INT_ANT10,IMP_DR10,INT_DR10,TOTAL10,'
        || 'FECHA11,DESC11,IMP_ACT11,INT_ACT11,IMP_ANT11,INT_ANT11,IMP_DR11,INT_DR11,'
        || 'TOTAL11,FECHA12,DESC12,IMP_ACT12,INT_ACT12,IMP_ANT12,INT_ANT12,IMP_DR12,'
        || 'INT_DR12,TOTAL12,DATE_CREATED, CREATED_BY';
       MI_VALORES := '''' || UN_COMPANIA || ''',''' || UN_NUMERO_ORDEN 
        || ''','''    || UN_DOCNUM   || ''',''' || UN_COD_PREDIO   || '0,0,' 
        || 'TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(1).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY''),'
        || MI_DATOS_MULTIFECHA(1).DESCUENTO || ',' || MI_DATOS_MULTIFECHA(1).IMP_ACT || ',' 
        || MI_DATOS_MULTIFECHA(1).INT_ACT   || ',' || MI_DATOS_MULTIFECHA(1).IMP_ANT || ',' 
        || MI_DATOS_MULTIFECHA(1).INT_ANT   || ',' || MI_DATOS_MULTIFECHA(1).IMP_DR  || ',' 
        || MI_DATOS_MULTIFECHA(1).INT_DR    || ',' || MI_DATOS_MULTIFECHA(1).TOTAL 
        || ',TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(2).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY''),'
        || MI_DATOS_MULTIFECHA(2).DESCUENTO || ',' || MI_DATOS_MULTIFECHA(2).IMP_ACT || ',' 
        || MI_DATOS_MULTIFECHA(2).INT_ACT   || ',' || MI_DATOS_MULTIFECHA(2).IMP_ANT || ',' 
        || MI_DATOS_MULTIFECHA(2).INT_ANT   || ',' || MI_DATOS_MULTIFECHA(2).IMP_DR  || ',' 
        || MI_DATOS_MULTIFECHA(2).INT_DR    || ',' || MI_DATOS_MULTIFECHA(2).TOTAL 
        || ',TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(3).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY''),'
        || MI_DATOS_MULTIFECHA(3).DESCUENTO || ',' || MI_DATOS_MULTIFECHA(3).IMP_ACT || ',' 
        || MI_DATOS_MULTIFECHA(3).INT_ACT   || ',' || MI_DATOS_MULTIFECHA(3).IMP_ANT || ',' 
        || MI_DATOS_MULTIFECHA(3).INT_ANT   || ',' || MI_DATOS_MULTIFECHA(3).IMP_DR  || ',' 
        || MI_DATOS_MULTIFECHA(3).INT_DR    || ',' || MI_DATOS_MULTIFECHA(3).TOTAL 
        || ',TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(4).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY''),'
        || MI_DATOS_MULTIFECHA(4).DESCUENTO || ',' || MI_DATOS_MULTIFECHA(4).IMP_ACT || ',' 
        || MI_DATOS_MULTIFECHA(4).INT_ACT   || ',' || MI_DATOS_MULTIFECHA(4).IMP_ANT || ',' 
        || MI_DATOS_MULTIFECHA(4).INT_ANT   || ',' || MI_DATOS_MULTIFECHA(4).IMP_DR  || ',' 
        || MI_DATOS_MULTIFECHA(4).INT_DR    || ',' || MI_DATOS_MULTIFECHA(4).TOTAL 
        || ',TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(5).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY''),'
        || MI_DATOS_MULTIFECHA(5).DESCUENTO || ',' || MI_DATOS_MULTIFECHA(5).IMP_ACT || ',' 
        || MI_DATOS_MULTIFECHA(5).INT_ACT   || ',' || MI_DATOS_MULTIFECHA(5).IMP_ANT || ',' 
        || MI_DATOS_MULTIFECHA(5).INT_ANT   || ',' || MI_DATOS_MULTIFECHA(5).IMP_DR  || ',' 
        || MI_DATOS_MULTIFECHA(5).INT_DR    || ',' || MI_DATOS_MULTIFECHA(5).TOTAL 
        || ',TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(6).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY''),'
        || MI_DATOS_MULTIFECHA(6).DESCUENTO || ',' || MI_DATOS_MULTIFECHA(6).IMP_ACT || ',' 
        || MI_DATOS_MULTIFECHA(6).INT_ACT   || ',' || MI_DATOS_MULTIFECHA(6).IMP_ANT || ',' 
        || MI_DATOS_MULTIFECHA(6).INT_ANT   || ',' || MI_DATOS_MULTIFECHA(6).IMP_DR  || ',' 
        || MI_DATOS_MULTIFECHA(6).INT_DR    || ',' || MI_DATOS_MULTIFECHA(6).TOTAL 
        || ',TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(7).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY''),'
        || MI_DATOS_MULTIFECHA(7).DESCUENTO || ',' || MI_DATOS_MULTIFECHA(7).IMP_ACT || ',' 
        || MI_DATOS_MULTIFECHA(7).INT_ACT   || ',' || MI_DATOS_MULTIFECHA(7).IMP_ANT || ',' 
        || MI_DATOS_MULTIFECHA(7).INT_ANT   || ',' || MI_DATOS_MULTIFECHA(7).IMP_DR  || ',' 
        || MI_DATOS_MULTIFECHA(7).INT_DR    || ',' || MI_DATOS_MULTIFECHA(7).TOTAL 
        || ',TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(8).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY''),'
        || MI_DATOS_MULTIFECHA(8).DESCUENTO || ',' || MI_DATOS_MULTIFECHA(8).IMP_ACT || ',' 
        || MI_DATOS_MULTIFECHA(8).INT_ACT   || ',' || MI_DATOS_MULTIFECHA(8).IMP_ANT || ',' 
        || MI_DATOS_MULTIFECHA(8).INT_ANT   || ',' || MI_DATOS_MULTIFECHA(8).IMP_DR  || ',' 
        || MI_DATOS_MULTIFECHA(8).INT_DR    || ',' || MI_DATOS_MULTIFECHA(8).TOTAL 
        || ',TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(9).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY''),'
        || MI_DATOS_MULTIFECHA(9).DESCUENTO || ',' || MI_DATOS_MULTIFECHA(9).IMP_ACT || ',' 
        || MI_DATOS_MULTIFECHA(9).INT_ACT   || ',' || MI_DATOS_MULTIFECHA(9).IMP_ANT || ',' 
        || MI_DATOS_MULTIFECHA(9).INT_ANT   || ',' || MI_DATOS_MULTIFECHA(9).IMP_DR  || ',' 
        || MI_DATOS_MULTIFECHA(9).INT_DR    || ',' || MI_DATOS_MULTIFECHA(9).TOTAL 
        || ',TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(10).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY''),'
        || MI_DATOS_MULTIFECHA(10).DESCUENTO || ',' || MI_DATOS_MULTIFECHA(10).IMP_ACT || ',' 
        || MI_DATOS_MULTIFECHA(10).INT_ACT   || ',' || MI_DATOS_MULTIFECHA(10).IMP_ANT || ',' 
        || MI_DATOS_MULTIFECHA(10).INT_ANT   || ',' || MI_DATOS_MULTIFECHA(10).IMP_DR  || ',' 
        || MI_DATOS_MULTIFECHA(10).INT_DR    || ',' || MI_DATOS_MULTIFECHA(10).TOTAL 
        || ',TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(11).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY''),'
        || MI_DATOS_MULTIFECHA(11).DESCUENTO || ',' || MI_DATOS_MULTIFECHA(11).IMP_ACT || ',' 
        || MI_DATOS_MULTIFECHA(11).INT_ACT   || ',' || MI_DATOS_MULTIFECHA(11).IMP_ANT || ',' 
        || MI_DATOS_MULTIFECHA(11).INT_ANT   || ',' || MI_DATOS_MULTIFECHA(11).IMP_DR  || ',' 
        || MI_DATOS_MULTIFECHA(11).INT_DR    || ',' || MI_DATOS_MULTIFECHA(11).TOTAL 
        || ',TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(12).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY''),'
        || MI_DATOS_MULTIFECHA(12).DESCUENTO || ',' || MI_DATOS_MULTIFECHA(12).IMP_ACT || ',' 
        || MI_DATOS_MULTIFECHA(12).INT_ACT   || ',' || MI_DATOS_MULTIFECHA(12).IMP_ANT || ',' 
        || MI_DATOS_MULTIFECHA(12).INT_ANT   || ',' || MI_DATOS_MULTIFECHA(12).IMP_DR  || ',' 
        || MI_DATOS_MULTIFECHA(12).INT_DR    || ',' || MI_DATOS_MULTIFECHA(12).TOTAL || ', SYSDATE,' || UN_USUARIO;
      MI_PCKDATOS := PCK_DATOS.FC_ACME(
        UN_TABLA => 'IP_RECIBOS_MULTIFECHA',
        UN_ACCION => 'I', 
        UN_CAMPOS => MI_CAMPOS,
        UN_VALORES => MI_VALORES
      );
    EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        MI_REEMPLAZOS(1).CLAVE := 'DOCNUM';
        MI_REEMPLAZOS(1).VALOR := UN_DOCNUM;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD => SQLCODE,
          UN_TABLAERROR => 'IP_RECIBOS_MULTIFECHA',
          UN_ERROR_COD => PCK_ERRORES.ERR_PREDIAL_RECIBOS_MULTIFECHA,
          UN_REEMPLAZOS => MI_REEMPLAZOS
        );
    END;
    -- Actualización del recibo de pago
    DECLARE
      MI_CAMPOS                       PCK_SUBTIPOS.TI_CAMPOS;
      MI_CONDICION                    PCK_SUBTIPOS.TI_CONDICION;
      MI_PCKDATOS                     PCK_SUBTIPOS.TI_RTA_ACME;
      MI_REEMPLAZOS                   PCK_SUBTIPOS.TI_CLAVEVALOR;
      MI_STRSQL                       PCK_SUBTIPOS.TI_STRSQL;
      MI_NUM                          NUMBER;
      --
      UN_VALOR_CONCEPTO               PCK_SUBTIPOS.TI_DOBLE := 0;
      UN_TOTAL_CONCEPTOS              PCK_SUBTIPOS.TI_DOBLE := 0;
    BEGIN
      -- Cálculo de totales excluyendo algunos conceptos.
      FOR i IN 1..20 LOOP
        IF i NOT IN(2, 6, 10, 13) THEN
          MI_STRSQL := 'SELECT NVL(SUM(C' || i || '),0)
            FROM IP_FACTURADOS
           WHERE COMPANIA = :p_compania
             AND CODIGO = :p_cod_predio
             AND NUMERO_ORDEN = :p_num_orden
             AND PREANO BETWEEN (CASE WHEN :p_unico_ano <> 0 
              THEN :p_ano_inicial ELSE :p_ano_final END) AND :p_ano_final
             AND PAGADO = 0
             AND NOCOBRADO = 0';
          IF MI_EXCLUYE_VIGENCIAS_ACUERDO THEN
            MI_STRSQL := MI_STRSQL || CHR(13) || CHR(10) || 'AND INDPAGO_ACPAG = 0';
          END IF;
          EXECUTE IMMEDIATE MI_STRSQL INTO UN_VALOR_CONCEPTO
          USING UN_COMPANIA, UN_COD_PREDIO, GL_NUMORDEN, UN_UNICO_ANO, UN_ANO_INICIAL, UN_ANO_FINAL;
          UN_TOTAL_CONCEPTOS := UN_TOTAL_CONCEPTOS + UN_VALOR_CONCEPTO;
        END IF;
      END LOOP;
      --
      FOR i IN 1..MI_PERIODOS_CALCULO 
      LOOP
        MI_NUM := i + MI_PERIODO_INICIAL;
        MI_DATOS_MULTIFECHA(MI_NUM).TOTAL := UN_TOTAL_CONCEPTOS 
          + MI_DATOS_MULTIFECHA(MI_NUM).INT_ACT + MI_DATOS_MULTIFECHA(MI_NUM).INT_ANT 
          + MI_DATOS_MULTIFECHA(MI_NUM).INT_DR + MI_DATOS_MULTIFECHA(MI_NUM).DESCUENTO;
      END LOOP;

      MI_CAMPOS := 'IND_MULTIFECHAS = -1 ' || 
       ', FECHA1 = TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(1).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
       ', FECHA2 = TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(2).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
       ', FECHA3 = TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(3).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
       ', FECHA4 = TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(4).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
       ', FECHA5 = TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(5).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
       ', FECHA6 = TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(6).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
       ', FECHA7 = TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(7).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
       ', FECHA8 = TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(8).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
       ', FECHA9 = TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(9).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
       ', FECHA10 = TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(10).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
       ', FECHA11 = TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(11).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
       ', FECHA12 = TO_DATE(''' || TO_CHAR(MI_DATOS_MULTIFECHA(12).FECHA, 'DD/MM/YYYY') || ''', ''DD/MM/YYYY'')' ||
       ', DESC1 = ' || ROUND(MI_DATOS_MULTIFECHA(1).DESCUENTO) || 
       ', DESC2 = ' || ROUND(MI_DATOS_MULTIFECHA(2).DESCUENTO) || 
       ', DESC3 = ' || ROUND(MI_DATOS_MULTIFECHA(3).DESCUENTO) || 
       ', DESC4 = ' || ROUND(MI_DATOS_MULTIFECHA(4).DESCUENTO) || 
       ', DESC5 = ' || ROUND(MI_DATOS_MULTIFECHA(5).DESCUENTO) || 
       ', DESC6 = ' || ROUND(MI_DATOS_MULTIFECHA(6).DESCUENTO) || 
       ', DESC7 = ' || ROUND(MI_DATOS_MULTIFECHA(7).DESCUENTO) || 
       ', DESC8 = ' || ROUND(MI_DATOS_MULTIFECHA(8).DESCUENTO) || 
       ', DESC9 = ' || ROUND(MI_DATOS_MULTIFECHA(9).DESCUENTO) || 
       ', DESC10 = ' || ROUND(MI_DATOS_MULTIFECHA(10).DESCUENTO) || 
       ', DESC11 = ' || ROUND(MI_DATOS_MULTIFECHA(11).DESCUENTO) || 
       ', DESC12 = ' || ROUND(MI_DATOS_MULTIFECHA(12).DESCUENTO) ||  
       ', TOTAL1 = ' || MI_DATOS_MULTIFECHA(1).TOTAL ||  
       ', TOTAL2 = ' || MI_DATOS_MULTIFECHA(2).TOTAL ||  
       ', TOTAL3 = ' || MI_DATOS_MULTIFECHA(3).TOTAL ||  
       ', TOTAL4 = ' || MI_DATOS_MULTIFECHA(4).TOTAL ||  
       ', TOTAL5 = ' || MI_DATOS_MULTIFECHA(5).TOTAL ||  
       ', TOTAL6 = ' || MI_DATOS_MULTIFECHA(6).TOTAL ||  
       ', TOTAL7 = ' || MI_DATOS_MULTIFECHA(7).TOTAL ||  
       ', TOTAL8 = ' || MI_DATOS_MULTIFECHA(8).TOTAL ||  
       ', TOTAL9 = ' || MI_DATOS_MULTIFECHA(9).TOTAL ||  
       ', TOTAL10 = ' || MI_DATOS_MULTIFECHA(10).TOTAL ||  
       ', TOTAL11 = ' || MI_DATOS_MULTIFECHA(11).TOTAL ||  
       ', TOTAL12 = ' || MI_DATOS_MULTIFECHA(12).TOTAL ||  
       ', DATE_MODIFIED = SYSDATE' ||
       ', MODIFIED_BY = ''' || UN_USUARIO || '''';
      MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA 
        ||''' AND DOCNUM = ''' || UN_DOCNUM || '''';
      MI_PCKDATOS := PCK_DATOS.FC_ACME(
        UN_TABLA => 'IP_RECIBOS_DE_PAGO',
        UN_ACCION => 'U', 
        UN_CAMPOS => MI_CAMPOS,
        UN_CONDICION => MI_CONDICION
      );
    EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      MI_REEMPLAZOS(1).CLAVE := 'DOCNUM';
      MI_REEMPLAZOS(1).VALOR := UN_DOCNUM;
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD => SQLCODE,
        UN_TABLAERROR => 'IP_RECIBOS_DE_PAGO',
        UN_ERROR_COD => PCK_ERRORES.ERR_PREDIAL_MULTIFECHAS,
        UN_REEMPLAZOS => MI_REEMPLAZOS
      );
    END;
  END PR_RECIBOS_MULTIFECHA;

  --28. Desc_Fac
  FUNCTION FC_DESC_FAC
  /*
  NAME              : FC_DESC_FAC
  AUTHORS           : SYSMAN SAS
  AUTHOR MIGRACION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE MIGRADOR     : 21/03/2017
  TIME              : 09:10 AM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Evalua si al predio se le asignó un descuento especial en saldos 
                      a favor con el fin de tenerlo en cuenta al totalizar el concepto.
  MODIFICATIONS     : 
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.
    UN_CODPREDIO: Predio del cual se está consultando las vigencias pagas.
    UN_ANO_INICIAL: Año inicial.
    UN_ANO_FINAL: Año final.

  @NAME: getDescuentoFactura
  @METHOD: get
  */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_CODPREDIO                    IN IP_USUARIOS_PREDIAL.CODIGO%TYPE
  , UN_ANO_INICIAL                  IN PCK_SUBTIPOS.TI_ANIO
  , UN_ANO_FINAL                    IN PCK_SUBTIPOS.TI_ANIO
  ) RETURN NUMBER AS 
    MI_VALOR_DESCUENTO              PCK_SUBTIPOS.TI_DOBLE := 0;
  BEGIN
    FOR RS IN (
      SELECT VALOR
        INTO MI_VALOR_DESCUENTO
        FROM IP_PAGOSDOBLES
       WHERE COMPANIA = UN_COMPANIA
         AND PRECOD = UN_CODPREDIO
         AND PREANO BETWEEN UN_ANO_INICIAL AND UN_ANO_FINAL
         AND TIPO = 'S'
         AND ANULADO = 0
         AND DESCUENTO <> 0) 
    LOOP
      MI_VALOR_DESCUENTO := MI_VALOR_DESCUENTO + RS.VALOR;
    END LOOP;
    RETURN MI_VALOR_DESCUENTO;
  END FC_DESC_FAC;

  --29. concepto
  FUNCTION FC_CONCEPTO
  /*
  NAME              : FC_CONCEPTO
  AUTHORS           : SYSMAN SAS / TOL
  AUTHOR MIGRACION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE MIGRADOR     : 22/03/2017
  TIME              : 12:41 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Retorna el valor de un concepto dado de la tabla facturados. 
                      (Para formulación)
  MODIFICATIONS     : 
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.
    UN_CONCEPTO: Codigo del concepto
    UN_ANO: Año para el cual se está haciendo el cálculo.
    UN_CODIGO: Código de predio.

  @NAME: getValorConcepto
  @METHOD: get
  */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_CONCEPTO                     IN IP_CONCEPTOS.CODIGO%TYPE
  , UN_ANO                          IN PCK_SUBTIPOS.TI_ANIO
  , UN_CODIGO                       IN IP_FACTURADOS.CODIGO%TYPE
  )RETURN NUMBER AS
    MI_STRSQL                       PCK_SUBTIPOS.TI_STRSQL;
    MI_VALOR_CONCEPTO               PCK_SUBTIPOS.TI_DOBLE := 0;
  BEGIN
    MI_STRSQL := 'SELECT C' || UN_CONCEPTO || '
      FROM IP_FACTURADOS
     WHERE COMPANIA = :p_compania
       AND CODIGO = :p_docnum
       AND NUMERO_ORDEN = :p_numero_orden
       AND PREANO = :p_ano';
    EXECUTE IMMEDIATE MI_STRSQL INTO MI_VALOR_CONCEPTO 
    USING UN_COMPANIA, UN_CODIGO, GL_NUMORDEN, UN_ANO;
    RETURN MI_VALOR_CONCEPTO;
  END;

  --30. moraAcumuladaMensual
  FUNCTION FC_MORAACUMULADAMENSUAL
  /*
  NAME              : FC_MORAACUMULADAMENSUAL
  AUTHORS           : SYSMAN SAS / TOL
  AUTHOR MIGRACION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE MIGRADOR     : 22/03/2017
  TIME              : 02:11 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Retorna el valor de la deuda acumulada mensualmente.
  MODIFICATIONS     : 
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.
    UN_CODIGO: Codigo del predio.
    UN_ANO: Año para el cual se está haciendo el cálculo.
    UN_FECHA_CORTE: 

  @NAME: getMoraAcumuladaMensual
  @METHOD: get
  */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_CODIGO                       IN IP_FACTURADOS.CODIGO%TYPE
  , UN_ANO                          IN PCK_SUBTIPOS.TI_ANIO
  , UN_FECHA_CORTE                  IN DATE DEFAULT SYSDATE
  )RETURN NUMBER AS
    MI_VALOR_MORA                   PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_FECINICIAL_EXEINT            IP_USUARIOS_PREDIAL.FECINICIAL_EXEINT%TYPE;
    MI_FECFINAL_EXEINT              IP_USUARIOS_PREDIAL.FECFINAL_EXEINT%TYPE;
    MI_IND_EXEINT                   IP_USUARIOS_PREDIAL.IND_EXEINT%TYPE;
    MI_AMNISTIA                     PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_FECINICIAL                   DATE;
    MI_FECFINAL                     DATE;
    MI_ANO                          PCK_SUBTIPOS.TI_ANIO;
    MI_PERINICIAL                   NUMBER;
    MI_PERFINAL                     NUMBER;
    MI_MESINI                       PCK_SUBTIPOS.TI_MES;
    MI_MESFIN                       PCK_SUBTIPOS.TI_MES;
    MI_TASA                         PCK_SUBTIPOS.TI_DOBLE := 0;
  BEGIN
    DECLARE
      MI_REEMPLAZOS                   PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN
      SELECT FECINICIAL_EXEINT, FECFINAL_EXEINT, IND_EXEINT
        INTO MI_FECINICIAL_EXEINT, MI_FECFINAL_EXEINT, MI_IND_EXEINT
        FROM IP_USUARIOS_PREDIAL
       WHERE COMPANIA = UN_COMPANIA
         AND CODIGO = UN_CODIGO
         AND NUMERO_ORDEN = GL_NUMORDEN
         AND CODIGO_NO_ACTIVO = 0;
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        MI_REEMPLAZOS(1).CLAVE := 'PREDIO';
        MI_REEMPLAZOS(1).VALOR := UN_CODIGO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD => SQLCODE
        , UN_ERROR_COD => PCK_ERRORES.ER_PREDIAL_NOPREDIO
        , UN_REEMPLAZOS => MI_REEMPLAZOS
        ); 
    END;
    MI_AMNISTIA := PCK_PREDIAL_COM3.FC_AMNISTIA(
      UN_COMPANIA => UN_COMPANIA, UN_ANO => UN_ANO);
    --
    MI_PERINICIAL := UN_ANO || LPAD(MI_AMNISTIA+1, 2, 0);
    IF MI_IND_EXEINT <> 0 THEN
      MI_FECINICIAL := MI_FECINICIAL_EXEINT;
      MI_FECFINAL := MI_FECFINAL_EXEINT;
    ELSE
      IF MI_PERINICIAL <= 200607 THEN
        MI_PERINICIAL := 200608;
      END IF;
      MI_PERFINAL := TO_CHAR(UN_FECHA_CORTE,'YYYY') || TO_CHAR(UN_FECHA_CORTE,'MM');
    END IF;
    --
    FOR MI_ANO IN UN_ANO..EXTRACT(YEAR FROM UN_FECHA_CORTE) LOOP
      MI_MESINI := 1;
      MI_MESFIN := 12;
      IF MI_ANO = UN_ANO THEN
        MI_MESINI := MI_AMNISTIA + 1;
      END IF;
      IF MI_ANO = TO_CHAR(UN_FECHA_CORTE,'YYYY') THEN
        MI_MESFIN := TO_CHAR(UN_FECHA_CORTE,'MM');
      END IF;
      FOR MI_MES IN MI_MESINI..MI_MESFIN LOOP
        DECLARE
          MI_PERIODO_ACTUAL               NUMBER;
          MI_PERIODO_INICIAL              NUMBER;
          MI_PERIODO_FINAL                NUMBER;
        BEGIN
          MI_PERIODO_ACTUAL := (LPAD(MI_ANO, 4 ,0) || LPAD(MI_MES, 2, 0));
          MI_PERIODO_INICIAL := TO_CHAR(MI_FECINICIAL,'YYYY') || LPAD(TO_CHAR(MI_FECINICIAL,'MM'), 2, 0);
          MI_PERIODO_FINAL := TO_CHAR(MI_FECFINAL,'YYYY') || LPAD(TO_CHAR(MI_FECFINAL,'MM'), 2, 0);
          IF MI_IND_EXEINT <> 0 AND (MI_PERIODO_ACTUAL BETWEEN MI_PERIODO_INICIAL AND MI_PERIODO_FINAL) THEN
            CONTINUE;
          END IF;
          IF MI_PERIODO_ACTUAL <= '200607' THEN
            SELECT TASAMENSUAL
              INTO MI_TASA
              FROM IP_TASASINTERES
             WHERE ANO = 2006
               AND MES = 7;
            --
            MI_PERINICIAL := MI_ANO || LPAD(MI_MES, 2, 0);
            MI_VALOR_MORA := MI_VALOR_MORA + MI_TASA;
          ELSE
            IF NVL(MI_IND_EXEINT, 0) = 0 THEN
              GOTO GRINTERES;
            END IF;
            --
            SELECT TASAMENSUAL
               INTO MI_TASA
              FROM IP_TASASINTERES
             WHERE ANO = MI_ANO
               AND MES = MI_MES;
            --
             MI_VALOR_MORA := MI_VALOR_MORA + MI_TASA;
          END IF;
        END;
      END LOOP;
    END LOOP;
    <<GRINTERES>>
    --
    IF MI_PERINICIAL <= MI_PERFINAL THEN
      SELECT SUM(TASAMENSUAL)
        INTO MI_TASA
        FROM IP_TASASINTERES
       WHERE ANO || LPAD(MES, 2, 0) BETWEEN MI_PERINICIAL AND MI_PERFINAL;
      --
      MI_VALOR_MORA := MI_VALOR_MORA + MI_TASA;
    END IF;
    --
    RETURN MI_VALOR_MORA;
  END;

  --31. tasaInteresTarifa
  FUNCTION FC_TASAINTERESTARIFA
  /*
  NAME              : FC_MORAACUMULADAMENSUAL
  AUTHORS           : SYSMAN SAS / TOL
  AUTHOR MIGRACION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE MIGRADOR     : 22/03/2017
  TIME              : 05:40 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Retorna la tasa de interés que acompaña cada tarifa en el 
                      año de cálculo
  MODIFICATIONS     : 
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.
    UN_CODIGOTARIFA: Código de la tarifa.
    UN_ANOTARIFA: Año de la tarifa.
    UN_AVALUOTARIFA: Valor del avaluo de la tarifa.
    UN_RANGOTARIFA: Rango de la tarifa.

  @NAME: getTasaInteresTarifa
  @METHOD: get
  */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_CODIGOTARIFA                 IN IP_TARIFAS.TRPCOD%TYPE
  , UN_ANOTARIFA                    IN PCK_SUBTIPOS.TI_ANIO
  , UN_AVALUOTARIFA                 IN PCK_SUBTIPOS.TI_DOBLE
  , UN_RANGOTARIFA                  IN IP_TARIFAS.TRPRAN%TYPE
  )RETURN NUMBER AS
    MI_TASAINTERES                  PCK_SUBTIPOS.TI_DOBLE := 0;
  BEGIN 
    SELECT TRPINT / 100
      INTO MI_TASAINTERES
      FROM IP_TARIFAS
     WHERE COMPANIA = UN_COMPANIA
       AND TRPCOD = UN_CODIGOTARIFA
       AND TRPANO = UN_ANOTARIFA
       AND TRPRAN = UN_RANGOTARIFA
       AND (TRPRAN1 <= UN_AVALUOTARIFA AND TRPRAN2 > = UN_AVALUOTARIFA);
    RETURN MI_TASAINTERES;
  END;

  --32. mesesMoraFecha
  FUNCTION FC_MESESMORA_FECHA
  /*
  NAME              : FC_MESESMORA_FECHA
  AUTHORS           : SYSMAN SAS
  AUTHOR MIGRACION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE MIGRADOR     : 23/03/2017
  TIME              : 09:56 AM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Retorna los meses de mora del año de cálculo.
  MODIFICATIONS     : 
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.
    FECHA_AL_DIA: 
    UN_FECHA_CORTE: 

  @NAME: getMesesMoraFecha
  @METHOD: get
  */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_FECHA_AL_DIA                 IN DATE
  , UN_FECHA_CORTE                  IN DATE
  )RETURN NUMBER AS
    MI_MESES_AMNISTIA               PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_MESES_CORTE                  PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_MESES_AL_DIA                 PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_DIFERENCIA_MESES             PCK_SUBTIPOS.TI_ENTERO := 0;
  BEGIN
    MI_MESES_AMNISTIA := PCK_PREDIAL_COM3.FC_AMNISTIA(
      UN_COMPANIA => UN_COMPANIA, UN_ANO => EXTRACT(YEAR FROM UN_FECHA_AL_DIA));
    IF EXTRACT(YEAR FROM UN_FECHA_CORTE) = EXTRACT(YEAR FROM UN_FECHA_AL_DIA) THEN
      MI_MESES_CORTE := EXTRACT(MONTH FROM UN_FECHA_CORTE) - MI_MESES_AMNISTIA;
      MI_MESES_AL_DIA := EXTRACT(MONTH FROM UN_FECHA_AL_DIA) - MI_MESES_AMNISTIA;
      MI_DIFERENCIA_MESES := MI_MESES_CORTE - MI_MESES_AL_DIA;
    ELSE
      MI_MESES_AL_DIA := ROUND(MONTHS_BETWEEN(UN_FECHA_CORTE, UN_FECHA_AL_DIA));
      MI_DIFERENCIA_MESES := ABS(MI_MESES_AL_DIA);
    END IF;
    RETURN CASE WHEN MI_DIFERENCIA_MESES < 0 THEN 0 ELSE MI_DIFERENCIA_MESES END;
  END FC_MESESMORA_FECHA;

  --33. descuentoMesFactura
  FUNCTION FC_DESCUENTOMES_FACTURA
  /*
  NAME              : FC_DESCUENTOMES_FACTURA
  AUTHORS           : SYSMAN SAS
  AUTHOR MIGRACION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE              : 23/03/2017
  TIME              : 10:33 AM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME MODIFIED     : 
  DESCRIPTION       : Busca por cada año las fechas límite para pago y su 
                      correspondiente descuento.
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.
    UN_MES: Mes al que se le busca el descuento.

  @NAME: getDescuentoMesFactura
  @METHOD: get
  */
  (
    UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_MES                      IN PCK_SUBTIPOS.TI_MES
  )RETURN NUMBER AS
    MI_VALOR_DESCUENTOS         PCK_SUBTIPOS.TI_DOBLE := 0;
  BEGIN 
    SELECT PORCENTAJE
      INTO MI_VALOR_DESCUENTOS
      FROM IP_DESCUENTOS_ANO
     WHERE COMPANIA = UN_COMPANIA
       AND ANO      = EXTRACT(YEAR FROM SYSDATE)
       AND MES      = UN_MES;
    RETURN MI_VALOR_DESCUENTOS;
  END FC_DESCUENTOMES_FACTURA;

  --34. avaluo
  FUNCTION FC_AVALUO
  /*
  NAME              : FC_AVALUO
  AUTHORS           : SYSMAN SAS / TOL
  AUTHOR MIGRACION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE              : 23/03/2017
  TIME              : 10:46 AM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME MODIFIED     : 
  DESCRIPTION       : Retorna el avalúo de un predio para una año determinado.
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.
    UN_ANO: Año para extraer el avalúo.
    UN_CODIGO: Código del predio.

  @NAME: getValorAvaluo
  @METHOD: get
  */
  (
    UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_ANO                      IN PCK_SUBTIPOS.TI_ANIO
  , UN_CODIGO                   IN IP_FACTURADOS.CODIGO%TYPE
  ) RETURN NUMBER AS
    MI_VALOR_AVALUO             PCK_SUBTIPOS.TI_DOBLE := 0;
  BEGIN 
    SELECT AVALUO 
      INTO MI_VALOR_AVALUO
      FROM IP_FACTURADOS
     WHERE CODIGO = UN_CODIGO
       AND PREANO = UN_ANO;
    RETURN MI_VALOR_AVALUO;
  END FC_AVALUO;

  --35. tarifa
  FUNCTION FC_TARIFA 
  /*
  NAME              : FC_TARIFA
  AUTHORS           : SYSMAN SAS / TOL
  AUTHOR MIGRACION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE              : 23/03/2017
  TIME              : 11:25 AM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME MODIFIED     : 
  DESCRIPTION       : Retorna la tarifa para un facturado.
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.
    UN_ANO: Año para extraer el tarifa.
    UN_CODIGO: Código del predio.

  @NAME: getTarifa
  @METHOD: get
  */
  (
    UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_ANO                      IN PCK_SUBTIPOS.TI_ANIO
  , UN_CODIGO                   IN IP_FACTURADOS.CODIGO%TYPE
  )RETURN NUMBER AS
    MI_TARIFA                   IP_TARIFAS.TRPPOR%TYPE := 0;
    MI_VALOR                    IP_TARIFAS.TRPVAL%TYPE;
  BEGIN
    SELECT IP_TARIFAS.TRPPOR, IP_TARIFAS.TRPVAL
      INTO MI_TARIFA, MI_VALOR
      FROM IP_FACTURADOS 
     INNER JOIN IP_TARIFAS 
        ON IP_TARIFAS.TRPCOD = IP_FACTURADOS.TRPCOD 
       AND IP_TARIFAS.TRPANO = IP_FACTURADOS.PREANO
       AND IP_TARIFAS.TRPRAN = IP_FACTURADOS.TRPRAN
     WHERE IP_FACTURADOS.COMPANIA = UN_COMPANIA
       AND IP_FACTURADOS.CODIGO = UN_CODIGO
       AND IP_FACTURADOS.PREANO = UN_ANO;
    RETURN MI_TARIFA / MI_VALOR; 
  END FC_TARIFA;

  --36. conceptoPeriodoAnterior
  FUNCTION FC_CONCEPTOPERIODOANTERIOR
  /*
  NAME              : FC_CONCEPTOPERIODOANTERIOR
  AUTHORS           : SYSMAN SAS / TOL
  AUTHOR MIGRACION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE MIGRADOR     : 23/03/2017
  TIME              : 12:10 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Retorna el valor de un concepto de la vigencia anterior a
                      la recibida como parámetro.
  MODIFICATIONS     : 
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.
    UN_CONCEPTO: Codigo del concepto
    UN_ANO: Año para el cual se está haciendo el cálculo.
    UN_CODIGO: Código de predio.

  @NAME: getValorConceptoPeriodoAnterior
  @METHOD: get
  */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_CONCEPTO                     IN IP_CONCEPTOS.CODIGO%TYPE
  , UN_ANO                          IN PCK_SUBTIPOS.TI_ANIO
  , UN_CODIGO                       IN IP_FACTURADOS.CODIGO%TYPE
  )RETURN NUMBER AS
    MI_STRSQL                       PCK_SUBTIPOS.TI_STRSQL;
    MI_VALOR_CONCEPTO               PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_ANO_EVALUAR                  PCK_SUBTIPOS.TI_ANIO;
  BEGIN
    BEGIN
      SELECT PREANO
        INTO MI_ANO_EVALUAR
        FROM IP_FACTURADOS
       WHERE COMPANIA = UN_COMPANIA
         AND PAGADO = 0
         AND PREANO > UN_ANO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_ANO_EVALUAR := UN_ANO - 1;
    END;
    MI_STRSQL := 'SELECT C' || UN_CONCEPTO || '
      FROM IP_FACTURADOS
     WHERE COMPANIA = :p_compania
       AND CODIGO = :p_codigo
       AND NUMERO_ORDEN = :p_numero_orden
       AND PREANO = :p_ano
       AND PAGADO = 0
       AND NOCOBRADO = 0
       AND INDEXE = 0';
    EXECUTE IMMEDIATE MI_STRSQL INTO MI_VALOR_CONCEPTO 
    USING UN_COMPANIA, UN_CODIGO, GL_NUMORDEN, MI_ANO_EVALUAR;
    RETURN MI_VALOR_CONCEPTO;
  END FC_CONCEPTOPERIODOANTERIOR;

  --37. suma
  FUNCTION FC_SUMA
  /*
  NAME              : FC_SUMA
  AUTHORS           : SYSMAN SAS / TOL
  AUTHOR MIGRACION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE MIGRADOR     : 23/03/2017
  TIME              : 12:25 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Retorna el valor de la suma de un concepto dado desde 
                      facturados.
  MODIFICATIONS     : 
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.
    UN_CONCEPTO: Codigo del concepto
    UN_ANO: Año para el cual se está haciendo el cálculo.
    UN_CODIGO: Código de predio.

  @NAME: getSumaConcepto
  @METHOD: get
  */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_CONCEPTO                     IN IP_CONCEPTOS.CODIGO%TYPE
  , UN_ANO                          IN PCK_SUBTIPOS.TI_ANIO
  , UN_CODIGO                       IN IP_FACTURADOS.CODIGO%TYPE
  )RETURN NUMBER AS 
    MI_STRSQL                       PCK_SUBTIPOS.TI_STRSQL;
    MI_VALOR_CONCEPTO               PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_ANO_EVALUAR                  PCK_SUBTIPOS.TI_ANIO;
  BEGIN 
    MI_ANO_EVALUAR := UN_ANO - 1;
    MI_STRSQL := 'SELECT SUM(C' || UN_CONCEPTO || ') 
      FROM IP_FACTURADOS
     WHERE COMPANIA = :p_compania
       AND CODIGO = :p_codigo
       AND NUMERO_ORDEN = :p_numero_orden
       AND PREANO < :p_ano
       AND PAGADO = 0
       AND NOCOBRADO = 0
       AND INDEXE = 0
     GROUP BY CODIGO, NUMERO_ORDEN';
    EXECUTE IMMEDIATE MI_STRSQL INTO MI_VALOR_CONCEPTO 
    USING UN_COMPANIA, UN_CODIGO, GL_NUMORDEN, MI_ANO_EVALUAR;
    RETURN MI_VALOR_CONCEPTO;
  END FC_SUMA;

  --38. excedentes
  FUNCTION FC_EXCEDENTES 
  /*
  NAME              : FC_EXCEDENTES
  AUTHORS           : SYSMAN SAS
  AUTHOR MIGRACION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE MIGRADOR     : 23/03/2017
  TIME              : 12:49 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Retorna la suma de los valores excedentes de un año y 
                      predio dado.
  MODIFICATIONS     : 
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.
    UN_CONCEPTO: Codigo del concepto
    UN_ANO: Año para el cual se está haciendo el cálculo.
    UN_CODIGO: Código de predio.

  @NAME: getValorExcedentes
  @METHOD: get
  */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_ANO                          IN PCK_SUBTIPOS.TI_ANIO
  , UN_CODIGO                       IN IP_FACTURADOS.CODIGO%TYPE
  )RETURN NUMBER AS
    MI_VALOR_EXCEDENTES             PCK_SUBTIPOS.TI_DOBLE := 0;
  BEGIN 
    SELECT SUM(VALOR)
      INTO MI_VALOR_EXCEDENTES
      FROM IP_PAGOSDOBLES
     WHERE COMPANIA = UN_COMPANIA
       AND PRECOD = UN_CODIGO
       AND ANO_EXCEDENTE = UN_ANO
       AND TIPO = 'D'
       AND PAGO = 0
       AND ANULADO = 0
       AND AFECTAFACT <> 0;
    RETURN MI_VALOR_EXCEDENTES;
  END FC_EXCEDENTES;

  --39. saldos
  FUNCTION FC_SALDOS
  /*
  NAME              : FC_SALDOS
  AUTHORS           : SYSMAN SAS / TOL
  AUTHOR MIGRACION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE MIGRADOR     : 23/03/2017
  TIME              : 02:20 PM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Retorna el total de los saldos a cargo desde la tabla PagosDobles.
  MODIFICATIONS     : 
  PARAMETERS        : 
    UN_COMPANIA: Código de la compañía.
    UN_ANO: Año para el cual se está haciendo el cálculo.
    UN_CODIGO: Código de predio.

  @NAME: getValorSaldos
  @METHOD: get
  */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_ANO                          IN PCK_SUBTIPOS.TI_ANIO
  , UN_CODIGO                       IN IP_FACTURADOS.CODIGO%TYPE
  )
  RETURN NUMBER AS
    MI_VALOR_SALDOS                 PCK_SUBTIPOS.TI_DOBLE := 0;
  BEGIN 
    SELECT SUM(VALOR)
      INTO MI_VALOR_SALDOS
      FROM IP_PAGOSDOBLES
     WHERE COMPANIA = UN_COMPANIA
       AND PRECOD = UN_CODIGO
       AND PREANO = UN_ANO
       AND TIPO = 'S'
       AND ANULADO = 0;
    RETURN MI_VALOR_SALDOS;
  END FC_SALDOS;

  --40. evaluarConceptos
  FUNCTION FC_EVALUAR_CONCEPTOS
  /*
  NAME              : FC_EVALUAR_CONCEPTOS
  AUTHORS           : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
  DATE              : 27/03/2017
  TIME              : 10:38 AM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Evalua y calcula el valor de los conceptos existetnes en 
                      la formula. Retorna la formula con los valores de los 
                      conceptos calculados.
  MODIFICATIONS     : 
  PARAMETERS        : 
    UN_FORMULA: Formula de cálculo para evaluar.
    UN_COMPANIA: Código de la compañía.
    UN_ANO: Año para el cual se está haciendo el cálculo.
    UN_CODIGO: Código de predio.
    UN_DIGITOS_REDONDEO: Opcional. Número de decimales a redondear.

  @NAME: evaluarConceptos
  @METHOD: get
  */
  (
    UN_FORMULA                      IN IP_CONCEPTOS.FORMULA%TYPE
  , UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_ANO                          IN PCK_SUBTIPOS.TI_ANIO
  , UN_CODIGO                       IN IP_FACTURADOS.CODIGO%TYPE
  , UN_DIGITOS_REDONDEO             IN NUMBER DEFAULT NULL
  )RETURN VARCHAR2 AS 
    /* Busca la expresión concepto(numeroConcepto) donde el parametro 
    numeroConcepto debe ser un número */
    MI_REGEXP                       CONSTANT VARCHAR2(20) := 'CONCEPTO\(([0-9]+)\)';
    MI_NUMERO_COINCIDENCIAS         NUMBER;
    MI_FORMULA_PROCESABLE           IP_CONCEPTOS.FORMULA%TYPE;
    MI_SUBSTR_FORMULA               IP_CONCEPTOS.FORMULA%TYPE;
    MI_NUMERO_CONCEPTO              NUMBER;
    MI_VALOR_CONCEPTO               PCK_SUBTIPOS.TI_DOBLE;
  BEGIN
    -- Número de conceptos encontrados en la formula
    MI_NUMERO_COINCIDENCIAS := REGEXP_COUNT(UN_FORMULA, MI_REGEXP, 1, 'i');
    MI_FORMULA_PROCESABLE := UN_FORMULA;
    <<procesar_formula>>
    FOR i IN 1..MI_NUMERO_COINCIDENCIAS
    LOOP
      -- Extracción de la cadena coincidente
      MI_SUBSTR_FORMULA := REGEXP_SUBSTR(UN_FORMULA, MI_REGEXP, 1, i, 'i');
      -- Extrae el número de concepto
      MI_NUMERO_CONCEPTO := TO_NUMBER(
        REGEXP_SUBSTR(MI_SUBSTR_FORMULA, '([0-9]+)', 1, 1, 'i'));
      -- Llamado a función equivalente para resolver la expresión
      MI_VALOR_CONCEPTO := PCK_PREDIAL_COM3.FC_CONCEPTO(UN_COMPANIA => UN_COMPANIA
      , UN_CONCEPTO => MI_NUMERO_CONCEPTO
      , UN_ANO => UN_ANO
      , UN_CODIGO => UN_CODIGO);
      --
      IF UN_DIGITOS_REDONDEO IS NOT NULL THEN
        MI_VALOR_CONCEPTO := ROUND(MI_VALOR_CONCEPTO, UN_DIGITOS_REDONDEO);
      END IF;
      -- Reemplazo de la expresión por su respectivo valor.
      MI_FORMULA_PROCESABLE := REPLACE(MI_FORMULA_PROCESABLE, MI_SUBSTR_FORMULA, MI_VALOR_CONCEPTO);
    END LOOP procesar_formula;
    RETURN MI_FORMULA_PROCESABLE;
  END FC_EVALUAR_CONCEPTOS;

END PCK_PREDIAL_COM3;