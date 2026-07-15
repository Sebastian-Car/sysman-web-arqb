create or replace PACKAGE BODY "PCK_MANT" AS

--01

FUNCTION FC_SERIEACCESORIOSVEHICULO

/*
    NAME              : FC_SERIEACCESORIOSVEHICULO  -->
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRADOR     : 24/09/2015
    TIME              : 09:00 AM
    SOURCE MODULE     : MANTENIMIENTO_ACTIVOS
    DESCRIPTION       : Devuelve si hay o no registros con una serie especÃ­fica en la tabla VEHICULOACCESORIOS   
    @NAME: getExistenAccesorios
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PLACA    IN VARCHAR2,
    UN_SERIE    IN PCK_SUBTIPOS.TI_ENTERO_LARGO
  )
   RETURN PCK_SUBTIPOS.TI_LOGICO
  AS
    MI_I            PCK_SUBTIPOS.TI_ENTERO;
    MI_RTA          NUMBER:=0;
    MI_ERROR_FUN    NUMBER:=GL_ERROR_NUM + 1;
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    BEGIN
        SELECT COUNT(*) INTO MI_I
        FROM    VEHICULOACCESORIOS
        WHERE   VEHICULOACCESORIOS.COMPANIA           = UN_COMPANIA
          AND   VEHICULOACCESORIOS.PLACA              = UN_PLACA
          AND   VEHICULOACCESORIOS.SERIE_ELEMENTO     = UN_SERIE;

        IF(MI_I>0) THEN
          MI_RTA  :=-1;
        END IF;
      RETURN MI_RTA;

      EXCEPTION WHEN NO_DATA_FOUND 
        THEN RAISE PCK_EXCEPCIONES.EXC_MANTENIMIENTO;    
    END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MANTENIMIENTO THEN
      MI_MSGERROR (1).CLAVE := 'PLACA';
      MI_MSGERROR (1).VALOR := UN_PLACA;
      MI_MSGERROR (2).CLAVE := 'SERIE';
      MI_MSGERROR (2).VALOR := UN_SERIE;
      PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                  UN_ERROR_COD  => PCK_ERRORES.ERR_MANT_NOEXISTEPLACA,
                                  UN_TABLAERROR => 'VEHICULOACCESORIOS',
                                  UN_REEMPLAZOS => MI_MSGERROR);    

END FC_SERIEACCESORIOSVEHICULO;

--02

PROCEDURE PR_AGREGARVEHICULOPARTES
/*
    NAME              : PC_AGREGARVEHICULOPARTES  -->
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRADOR     : 06/10/2015
    TIME              : 05:00 PM
    SOURCE MODULE     : MANTENIMIENTO_ACTIVOS
    DESCRIPTION       : Agrega las partes funcionales en general a un vehÃ­culo especÃ­fico
    @NAME: insertarParteFuncional
  */
  (
  UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PLACA    IN VARCHAR2,
  UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
    MI_ERROR_FUN  PCK_SUBTIPOS.TI_ERROR_FUN;
    MI_MSGERROR   PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN

      BEGIN
        MI_CAMPOS:=' COMPANIA
                    ,PLACA
                    ,PARTEFUNCIONAL
                    ,ESTADO
                    ,CREATED_BY
                    ,DATE_CREATED';
        MI_VALORES:='(SELECT '''||UN_COMPANIA||'''
                    , '''||UN_PLACA||'''
                    , '||'PARTEFUNCIONAL.CODIGO
                    , ''B'' 
                    ,''' || UN_USUARIO ||''',
                     SYSDATE
                    FROM PARTEFUNCIONAL)';
        PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   =>  'VEHICULO_PARTES',
                                             UN_ACCION  =>  'IS',
                                             UN_CAMPOS  =>  MI_CAMPOS,
                                             UN_VALORES =>  MI_VALORES
                                             );     
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_MANTENIMIENTO;  
      END;      
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MANTENIMIENTO THEN
        MI_MSGERROR (1).CLAVE := 'PLACA';
        MI_MSGERROR (1).VALOR := UN_PLACA;
        PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_MANT_NOINSERTAPLACA,
                                    UN_TABLAERROR => 'VEHICULO_PARTES',
                                    UN_REEMPLAZOS => MI_MSGERROR);  

END PR_AGREGARVEHICULOPARTES;

--03

FUNCTION FC_ACTUALIZARSOLICITUD
/*
    NAME              : FC_ACTUALIZARSOLICITUD  --> 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRADOR     : 07/10/2015
    TIME              : 03:00 PM
    SOURCE MODULE     : MANTENIMIENTO_ACTIVOS
    DESCRIPTION       : Copia elementos de mantenimiento dependiendo del tipo en que este dicho mantenimiento.
    MODIFIER          : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
    DATE              : 30/08/2017
    TIME              : 12:15 PM
    MODIFICATIONS     : Adición de campo KILOMETRAJE al realizar la copia del detalle de movimiento.

    @NAME: actualizarSolicitudMantenimiento
    @METHOD: get
  */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO        IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO       IN VARCHAR2,
    UN_SOLICITUD  IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_NUMERO     IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN PCK_SUBTIPOS.TI_LOGICO
  AS
    MI_RTA        NUMBER(1):=0;
    MI_ERROR_FUN  PCK_SUBTIPOS.TI_ERROR_FUN;
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
    RS SYS_REFCURSOR;
    MI_MSGERROR   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_EXISTE      VARCHAR2(1 CHAR);


  BEGIN
      IF(UN_TIPO  = 'AUT') THEN   
      --@JGOMEZP, 21/08/2018, Se agrega el campo Cantidad.
        FOR RS IN(SELECT COMPANIA
                        , ANO
                        , TIPO_CPTE
                        , COMPROBANTE
                        , CONSECUTIVO
                        , CODIGOELEMENTO
                        , NOMBRELEMENTO
                        , PLACA
                        , DESCRIPCION
                        , TAREAMANTENIMIENTO
                        , TIPO_MANTENIMIENTO 
                        , KILOMETRAJE
                        , CANTIDAD
                     FROM   D_MANTENIMIENTO
                     WHERE  COMPANIA    = UN_COMPANIA 
                      AND   ANO         = UN_ANO
                      AND   TIPO_CPTE   = 'SOL'
                      AND   COMPROBANTE = UN_SOLICITUD) 
        LOOP
            MI_CAMPOS:= 'COMPANIA
                        ,ANO
                        ,TIPO_CPTE
                        ,COMPROBANTE
                        ,CONSECUTIVO
                        ,TIPO_CPTE_AFECT
                        ,COMPROBANTE_AFECT
                        ,CODIGOELEMENTO
                        ,NOMBRELEMENTO
                        ,PLACA
                        ,DESCRIPCION
                        ,TAREAMANTENIMIENTO
                        ,TIPO_MANTENIMIENTO
                        ,KILOMETRAJE
                        ,CANTIDAD
                        ,CREATED_BY 
                        ,DATE_CREATED';
            MI_VALORES:=''''||RS.COMPANIA||''',
                        '||RS.ANO||'
                        ,'''||UN_TIPO||'''
                        ,'||UN_NUMERO||'
                        ,'||RS.CONSECUTIVO||'
                        ,'''||RS.TIPO_CPTE||'''
                        ,'||RS.COMPROBANTE||'
                        ,'||RS.CODIGOELEMENTO||'
                        ,'''||RS.NOMBRELEMENTO||'''
                        ,'''||RS.PLACA||'''
                        ,'''||NVL(RS.DESCRIPCION, '')||''''||'
                        ,'''||NVL(RS.TAREAMANTENIMIENTO, '')||'''
                        ,'''||RS.TIPO_MANTENIMIENTO||'''
                        ,'||RS.KILOMETRAJE||'
                        ,'||RS.CANTIDAD||'
                        ,''' || UN_USUARIO ||'''
                        ,    SYSDATE';
            BEGIN
              BEGIN
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => 'D_MANTENIMIENTO',
                                                     UN_ACCION  => 'I',
                                                     UN_CAMPOS  => MI_CAMPOS,
                                                     UN_VALORES => MI_VALORES);
                MI_RTA:=-1;  
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_MANTENIMIENTO;  
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MANTENIMIENTO THEN
                MI_MSGERROR (1).CLAVE := 'NOMBRE';
                MI_MSGERROR (1).VALOR := RS.NOMBRELEMENTO;
                MI_MSGERROR (2).CLAVE := 'PLACA';
                MI_MSGERROR (2).VALOR := RS.PLACA;
                PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERR_MANT_NOINSERTAEN_D_MANT,
                                            UN_TABLAERROR => 'D_MANTENIMIENTO',
                                            UN_REEMPLAZOS => MI_MSGERROR);  
            END;

        END LOOP;
          MI_CAMPOS:= 'APROBADO      = -1' ||
                    ', MODIFIED_BY   =''' || UN_USUARIO || '''
                    , DATE_MODIFIED =SYSDATE ' ;
          MI_CONDICION:='COMPANIA='''||UN_COMPANIA||''' 
                          AND ANO='||UN_ANO||' 
                          AND TIPO=''SOL'''||' 
                          AND NUMERO='||UN_SOLICITUD;
          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA      =>  'MANTENIMIENTO',
                                                   UN_ACCION     =>  'M',
                                                   UN_CAMPOS     =>  MI_CAMPOS,
                                                   UN_CONDICION  =>  MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_MANTENIMIENTO;  
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MANTENIMIENTO THEN
              MI_MSGERROR (1).CLAVE := 'SOLICITUD';
              MI_MSGERROR (1).VALOR := UN_SOLICITUD;
              MI_MSGERROR (2).CLAVE := 'ANO';
              MI_MSGERROR (2).VALOR := UN_ANO;
              PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERR_MANT_NOACTEN_MANT,
                                          UN_TABLAERROR => 'MANTENIMIENTO',
                                          UN_REEMPLAZOS => MI_MSGERROR);
          END;

      ELSE 
        FOR RS IN(SELECT  COMPANIA
                        , ANO
                        , TIPO_CPTE
                        , COMPROBANTE
                        , CONSECUTIVO
                        , CODIGOELEMENTO
                        , NOMBRELEMENTO
                        , PLACA
                        , DESCRIPCION
                        , TAREAMANTENIMIENTO
                        , FECHAINICIAL
                        , FECHAFINAL
                        , CANTIDAD
                        , VALORUNITARIO
                        , VALORTOTAL
                        , NIT_TALLER
                        , SUCURSAL
                        , TIPO_MANTENIMIENTO
                        , KILOMETRAJE
                    FROM    D_MANTENIMIENTO
                    WHERE   COMPANIA     =   UN_COMPANIA 
                      AND   ANO          =   UN_ANO
                      AND   TIPO_CPTE    =  'AUT'
                      AND   COMPROBANTE  =  UN_SOLICITUD)
        LOOP
            MI_CAMPOS:= 'COMPANIA
                        ,ANO
                        ,TIPO_CPTE
                        ,COMPROBANTE
                        ,CONSECUTIVO
                        ,TIPO_CPTE_AFECT
                        ,COMPROBANTE_AFECT
                        ,CODIGOELEMENTO
                        ,NOMBRELEMENTO
                        ,PLACA
                        ,DESCRIPCION
                        ,TAREAMANTENIMIENTO
                        ,FECHAINICIAL
                        ,FECHAFINAL
                        ,CANTIDAD
                        ,VALORUNITARIO
                        ,VALORTOTAL
                        ,NIT_TALLER
                        ,SUCURSAL
                        ,TIPO_MANTENIMIENTO
                        ,KILOMETRAJE
                        ,CREATED_BY
                        ,DATE_CREATED';
            MI_VALORES:=''''||RS.COMPANIA||''',
                        '||RS.ANO||',
                        '''||UN_TIPO||''',
                        '||UN_NUMERO||',
                        '||RS.CONSECUTIVO||',
                        '''||RS.TIPO_CPTE||''',
                        '||RS.COMPROBANTE||',
                        '||RS.CODIGOELEMENTO||',
                        '''||RS.NOMBRELEMENTO||''',
                        '''||RS.PLACA||''',
                        '''||NVL(RS.DESCRIPCION, '')||''''||',
                        '''||NVL(RS.TAREAMANTENIMIENTO, '')||''',
                        '''||NVL(RS.FECHAINICIAL, '')||''',
                        '''||NVL(RS.FECHAFINAL, '')||''',
                        '|| NVL(RS.CANTIDAD, 0)||',
                        '||NVL(RS.VALORUNITARIO, 0)||',
                        '||NVL(RS.VALORTOTAL, 0)||',
                        '''||NVL(RS.NIT_TALLER,'')||''',
                        '''||RS.SUCURSAL||''',
                        '''||RS.TIPO_MANTENIMIENTO||''',
                        '||RS.KILOMETRAJE||',
                        ''' || UN_USUARIO ||''',
                            SYSDATE';

            BEGIN
              BEGIN
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   =>  'D_MANTENIMIENTO',
                                                     UN_ACCION  =>  'I',
                                                     UN_CAMPOS  =>  MI_CAMPOS,
                                                     UN_VALORES =>  MI_VALORES);
                MI_RTA:=-1;  
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_MANTENIMIENTO; 
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MANTENIMIENTO THEN
              MI_MSGERROR (1).CLAVE := 'NOMBRE';
              MI_MSGERROR (1).VALOR := RS.NOMBRELEMENTO;
              MI_MSGERROR (2).CLAVE := 'PLACA';
              MI_MSGERROR (2).VALOR := RS.PLACA;
              PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERR_MANT_NOINSERTAEN_D_MANT,
                                          UN_TABLAERROR => 'D_MANTENIMIENTO',
                                          UN_REEMPLAZOS => MI_MSGERROR);
            END;


        END LOOP;
          MI_CAMPOS:= 'APROBADO     = -1
                    , EJECUTADO     = -1
                    , MODIFIED_BY   =''' || UN_USUARIO || ''' 
                    , DATE_MODIFIED =SYSDATE ';

          MI_CONDICION:='COMPANIA='''||UN_COMPANIA||''' 
                        AND ANO='||UN_ANO||' 
                        AND TIPO=''AUT'''||' 
                        AND NUMERO='||UN_SOLICITUD;

          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     =>  'MANTENIMIENTO',
                                                   UN_ACCION    =>  'M',
                                                   UN_CAMPOS    =>  MI_CAMPOS,
                                                   UN_CONDICION =>  MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_MANTENIMIENTO;  
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MANTENIMIENTO THEN
              MI_MSGERROR (1).CLAVE := 'SOLICITUD';
              MI_MSGERROR (1).VALOR := UN_SOLICITUD;
              MI_MSGERROR (2).CLAVE := 'ANO';
              MI_MSGERROR (2).VALOR := UN_ANO;
              PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERR_MANT_NOACTEN_MANT,
                                          UN_TABLAERROR => 'MANTENIMIENTO',
                                          UN_REEMPLAZOS => MI_MSGERROR);
          END;

      END IF;  

      RETURN MI_RTA;

END FC_ACTUALIZARSOLICITUD;



 FUNCTION FC_VALIDAR_AFECTADOS 
 /*
    NAME              : FC_VALIDAR_AFECTADOS  --> 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN ENRIQUE GUERRERO TORRES
    DATE MIGRADOR     : 17/10/2017
    TIME              : 10:05 AM
    SOURCE MODULE     : MANTENIMIENTO_ACTIVOS
    DESCRIPTION       : Valida si  una solicitud o una Autorizacion ya fue afectada para poder  elmininarla
    PARAMETERS        : UN_COMPANIA  => COMPANIA INGRESO DEL SISTEMA
                        UN_ANO       => ANO DE LA SOLICITUD
                        UN_SOLICITUD => SOLICITUD QUE FUE SE QUIERE ELIMINAR

  */

(UN_COMPANIA     PCK_SUBTIPOS.TI_COMPANIA,
 UN_ANO          PCK_SUBTIPOS.TI_ANIO,
 UN_SOLICITUD    PCK_SUBTIPOS.TI_ENTERO_LARGO,
 UN_TIPO         VARCHAR2)

RETURN PCK_SUBTIPOS.TI_LOGICO AS 

MI_AFECTADO      PCK_SUBTIPOS.TI_ENTERO_LARGO;

BEGIN
  --VALIDA SI LA SOLICITUD FUE AFECTADA POR UNA AUTORIZACION

  IF UN_TIPO = 'SOL' THEN 
    BEGIN 
      SELECT SOLICITUD
        INTO MI_AFECTADO
        FROM MANTENIMIENTO 
       WHERE COMPANIA  = UN_COMPANIA
         AND ANO       = UN_ANO
         AND SOLICITUD = UN_SOLICITUD
         AND TIPO      = 'AUT';
     EXCEPTION WHEN NO_DATA_FOUND THEN 
      RETURN 0;
     END;      
      IF MI_AFECTADO NOT IN(0) THEN 
          RETURN -1;
      END IF;
  ELSE
  -- VALIDA SI LA AUTORIZACION FUE AFECTADA POR UNA EJECUCIÓN
    BEGIN  
      SELECT SOLICITUD
        INTO MI_AFECTADO
        FROM MANTENIMIENTO 
       WHERE COMPANIA  = UN_COMPANIA
         AND ANO       = UN_ANO
         AND SOLICITUD = UN_SOLICITUD
         AND TIPO      = 'EJE';
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        RETURN 0;
     END;
      IF MI_AFECTADO NOT IN (0) THEN 
          RETURN -1;
      END IF;
END IF;
  RETURN 0;
END FC_VALIDAR_AFECTADOS;

FUNCTION FC_ENUMERARMANTENIMIENTO
  /*
    NAME              : FC_ENUMERARMANTENIMIENTO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 26/10/2017
    TIME              : 03:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : genera el consecutivo del mantenimiento cuando este no fue ingresado como nulo
                        Sera usado unicamente en trigger de la misma tabla

    @NAME:  enumerarMantenimiento
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO         IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_NUMERO       IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 0

  )
  RETURN PCK_SUBTIPOS.TI_LONG
  AS

    MI_NUMERO                 MANTENIMIENTO.NUMERO%TYPE;   
    MI_MSGERRORES             PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
    MI_NUMERO:=0;


      MI_NUMERO:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'MANTENIMIENTO' ,
                                                  UN_CRITERIO =>'COMPANIA     =''' || UN_COMPANIA     ||'''' ||
                                                           ' AND ANO          ='   || UN_ANIO         ||
                                                           ' AND TIPO         =''' || UN_TIPO         ||'''' ,
                                                  UN_CAMPO    =>'NUMERO',
                                                  UN_INICIAL  =>'0');


      IF  MI_NUMERO = 0 THEN  
          MI_NUMERO:=PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'CONSECUTIVOTC' ,
                                                      UN_CRITERIO =>'COMPANIA        =''' || UN_COMPANIA     ||'''' ||
                                                               ' AND ANO             ='   || UN_ANIO         ||
                                                               ' AND TIPOCOMPROBANTE =''' || UN_TIPO         ||'''' ,
                                                      UN_CAMPO    =>'CONSECUTIVO',
                                                      UN_INICIAL  =>'0');

     END IF;  

      IF MI_NUMERO <>0 THEN

        RETURN  MI_NUMERO;

      ELSE
        MI_NUMERO :=UN_ANIO ||  LPAD(1,6,'0');

        DECLARE
          MI_CAMPOS    PCK_SUBTIPOS.TI_CAMPOS;
          MI_VALORES   PCK_SUBTIPOS.TI_VALORES;
          MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;
        BEGIN
          MI_CAMPOS := 'COMPANIA,TIPOCOMPROBANTE,ANO,CONSECUTIVO';
          MI_VALORES := '''' || UN_COMPANIA || '''
                        ,''' || UN_TIPO     || '''
                        ,'   || UN_ANIO     || '
                        ,       1';
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'CONSECUTIVOTC', 
                                                 UN_ACCION  => 'I', 
                                                 UN_CAMPOS  => MI_CAMPOS, 
                                                 UN_VALORES => MI_VALORES);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;

      END IF;

    RETURN MI_NUMERO;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
    MI_MSGERRORES(1).CLAVE := 'TIPO';
    MI_MSGERRORES(1).VALOR := UN_TIPO;
    PCK_ERR_MSG.RAISE_WITH_MSG(
                             UN_EXC_COD    => SQLCODE,
                             UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_INS_CONSECUTIVO,
                             UN_REEMPLAZOS => MI_MSGERRORES);
END FC_ENUMERARMANTENIMIENTO;

PROCEDURE PR_MANT_COMPONENTES 

    /*
    NAME              : PR_MANT_COMPONENTES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
    DATE MIGRADOR     : 16/10/2018
    TIME              : 11:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Se agregan los componentes que contienen una estación para
                        realizar mantenimiento

    @NAME: mantEstacion
    @METHOD: post
  */

( 
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO          IN  D_MANTENIMIENTO.ANO%TYPE,
    UN_TIPOCPTE     IN  D_MANTENIMIENTO.TIPO_CPTE%TYPE,
    UN_COMPROBANTE  IN  D_MANTENIMIENTO.COMPROBANTE%TYPE,
    UN_TIPO         IN  D_MANTENIMIENTO.TIPO_MANTENIMIENTO%TYPE,
    UN_COMPONENTE   IN  ELEMENTOSCOMPONENTES.CONSECUTIVO%TYPE,
    UN_OBSERVACION  IN  D_MANTENIMIENTO.DESCRIPCION%TYPE,
    UN_TAREA        IN  D_MANTENIMIENTO.TAREAMANTENIMIENTO%TYPE,
    UN_USUARIO      IN  PCK_SUBTIPOS.TI_USUARIO    
)
AS 
    MI_RS           SYS_REFCURSOR;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_RTA          NUMBER(2,0);
    MI_CONSECUTIVO  NUMBER(2,0);
BEGIN
    BEGIN
        FOR MI_RS IN (SELECT D.CODIGO_COMPONENTE, D.SERIE_COMPONENTE,
                             I.NOMBRELARGO
                        FROM DETALLE_ELEMENTOSCOMPONENTES D
                       INNER JOIN INVENTARIO I
                          ON D.COMPANIA = I.COMPANIA
                         AND D.CODIGO_COMPONENTE = I.CODIGOELEMENTO
                       WHERE D.COMPANIA = UN_COMPANIA
                         AND D.CODIGO_ELEMENTO = UN_COMPONENTE
                      )
                  LOOP

                      MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'D_MANTENIMIENTO',
                                                                         UN_CRITERIO => ' COMPANIA = ''' || UN_COMPANIA || 
                                                                                         ''' AND ANO = ' || UN_ANO || 
                                                                                         ' AND TIPO_CPTE = ''' || UN_TIPOCPTE || 
                                                                                         ''' AND COMPROBANTE = ' || UN_COMPROBANTE, 
                                                                         UN_CAMPO    => 'CONSECUTIVO' );
                      MI_CAMPOS  := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,
                                     CODIGOELEMENTO,PLACA,CANTIDAD,TIPO_MANTENIMIENTO,
                                     TAREAMANTENIMIENTO,DESCRIPCION,CREATED_BY,DATE_CREATED, NOMBRELEMENTO ';

                      MI_VALORES := '''' || UN_COMPANIA || ''', ' || UN_ANO || ' ,''' ||
                                    UN_TIPOCPTE || ''', ' || UN_COMPROBANTE || ',' ||
                                    MI_CONSECUTIVO || ',''' || MI_RS.CODIGO_COMPONENTE || ''',' ||
                                    MI_RS.SERIE_COMPONENTE || ',1, ''' || UN_TIPO || ''',''' || UN_TAREA || ''',''' ||
                                    UN_OBSERVACION || ''', ''' || UN_USUARIO || ''' , SYSDATE, ''' || MI_RS.NOMBRELARGO || '''';

                      MI_RTA := PCK_DATOS.FC_ACME(  UN_ACCION   => 'I',
                                                    UN_CAMPOS   => MI_CAMPOS,
                                                    UN_VALORES  => MI_VALORES,
                                                    UN_TABLA    => 'D_MANTENIMIENTO'
                                                  );
                  END LOOP;
    END;
END PR_MANT_COMPONENTES;

END PCK_MANT;