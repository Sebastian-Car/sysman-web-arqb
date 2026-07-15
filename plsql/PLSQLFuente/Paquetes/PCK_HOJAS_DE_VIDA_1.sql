create or replace PACKAGE BODY PCK_HOJAS_DE_VIDA AS

--1
PROCEDURE PR_CONSECEVAL
/*
    NAME              : PR_CONSECEVAL -> En Access: ConsecEval
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : JULIO CESAR REINA PANCHE
    DATE MIGRATION    : 19/12/2017
    TIME              : 02:20 PM
    SOURCE MODULE     : HOJAS DE VIDA (21) - SysmanHv2017.11.02 16112017 MPV - 32 CHIA_ALCTOC_CHIA
    DESCRIPTION       : Valida el valor de los los filtros ingresados para generar cada reporte.
    MODIFIED BY       : 

    @NAME  : actualizarConsecutivoEvaluacion
  */
(
  UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_NUMERODCTOINICIAL   IN NAT_DATOS_PERSONALES.NUMERO_DCTO%TYPE,
  UN_NUMERODCTOFINAL     IN NAT_DATOS_PERSONALES.NUMERO_DCTO%TYPE,
  UN_FECHAINICIAL        IN NAT_EVALUACION.EV_FECHDESDEEVAL%TYPE,
  UN_FECHAFINAL          IN NAT_EVALUACION.EV_FECHDESDEEVAL%TYPE,
  UN_USUARIO             IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_CARPETA            NAT_DATOS_PERSONALES.NUMEROCARPETA%TYPE;
  MI_CONT               PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_STRSQL             PCK_SUBTIPOS.TI_STRSQL;
BEGIN
  MI_CONT:=0;
  FOR MI_RS IN (SELECT NAT_DATOS_PERSONALES.NUMEROCARPETA
              FROM NAT_EVALUACION EVALUACION
              LEFT JOIN NAT_DATOS_PERSONALES 
              ON EVALUACION.DP_NUMEDOCU = NAT_DATOS_PERSONALES.NUMERO_DCTO
              WHERE EVALUACION.COMPANIA = UN_COMPANIA
              AND NAT_DATOS_PERSONALES.NUMERO_DCTO BETWEEN UN_NUMERODCTOINICIAL  AND UN_NUMERODCTOFINAL
              AND EVALUACION.EV_FECHDESDEEVAL >= UN_FECHAINICIAL
              AND EVALUACION.EV_FECHHASTAEVAL <= UN_FECHAFINAL
              ORDER BY NAT_DATOS_PERSONALES.NUMEROCARPETA, EVALUACION.EV_FECHDESDEEVAL)
  LOOP
    IF MI_CARPETA <> MI_RS.NUMEROCARPETA THEN
      MI_CONT:=1;
    ELSE
      MI_CONT:=MI_CONT+1;
    END IF;
    MI_CARPETA:=MI_RS.NUMEROCARPETA;

    MI_CAMPOS  := 'CONSECUTIVO       = '|| MI_CONT || ',
                   DATE_MODIFIED     = SYSDATE, 
                   MODIFIED_BY       = '''||UN_USUARIO||'''';
    MI_CONDICION := ' COMPANIA                = '''|| UN_COMPANIA     ||
                    ''' AND EV_FECHDESDEEVAL  = '''|| UN_FECHAINICIAL ||
                    ''' AND EV_FECHHASTAEVAL  = '''|| UN_FECHAFINAL   ||'''';  
    BEGIN 
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'NAT_EVALUACION', 
                                               UN_ACCION    =>  'M', 
                                               UN_CAMPOS    =>  MI_CAMPOS,
                                               UN_CONDICION =>  MI_CONDICION );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN    
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERRR_HOJAS_VIDA_ACT_CONS
                    );
    END;
  END LOOP;
END PR_CONSECEVAL;

  --2
  PROCEDURE PR_VALIDARFILTROSIMPRESIONHV
  /*
    NAME              : PR_VALIDARFILTROSIMPRESIONHV -> En Access: ImprimirHojasDeVida
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : PABLO ANDRES ESPITIA CUCA
    DATE MIGRATION    : 18/12/2017
    TIME              : 09:20 AM
    SOURCE MODULE     : HOJAS DE VIDA (21) - SysmanHv2017.11.02 16112017 MPV - 32 CHIA_ALCTOC_CHIA
    DESCRIPTION       : Valida el valor de los los filtros ingresados para generar cada reporte.
    MODIFIED BY       : 

    @NAME  : validarFiltrosImpresionHV
  */
  (
    UN_IND_LISTADO     IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0,      -- Indicador listado.
    UN_IND_FECHAS      IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0,      -- Indicador entre fechas.
    UN_IND_ESTADO      IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0,      -- Indicador estado actual.
    UN_IND_CONSOLIDADO IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0,      -- Indicador consolidado.
    UN_IND_HISTORIAL   IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0,      -- Indicador historial unico.
    UN_FECHAINICIAL    IN DATE,                                  -- Fecha inicial 
    UN_FECHAFINAL      IN DATE,                                  -- Fecha final.
    UN_EMPLEADOINI_NUM IN NAT_DATOS_PERSONALES.NUMERO_DCTO%TYPE, -- Numero del documento empleado inicial.
    UN_EMPLEADOFIN_NUM IN NAT_DATOS_PERSONALES.NUMERO_DCTO%TYPE, -- Numero del documento empleado final.
    UN_INFORME         IN VARCHAR2,                              -- Tipo de informe consolidado.
    UN_ESTADO          IN VARCHAR2                               -- Estado actual
  )
  AS 
  BEGIN
    --Verifica que se haya seleccionado algun indicador.
    IF (UN_IND_LISTADO + UN_IND_FECHAS + UN_IND_ESTADO + UN_IND_CONSOLIDADO + UN_IND_HISTORIAL) IN(0) THEN 
      BEGIN
       RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                  ,UN_ERROR_COD => PCK_ERRORES.ERR_HV_MSG_PR_VALFIL_VALIDAIND);
      END;
    END IF;

    IF UN_IND_FECHAS IN(0) THEN
      IF (UN_EMPLEADOINI_NUM IS NULL OR UN_EMPLEADOFIN_NUM IS NULL) THEN 
        BEGIN
         RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                    ,UN_ERROR_COD => PCK_ERRORES.ERR_HV_MSG_PR_VALFIL_VALEMPNUL);
        END;        
      END IF;
    ELSE --Cuando el indicador entre fechas esta marcado
      IF (UN_FECHAINICIAL IS NULL OR UN_FECHAFINAL IS NULL) THEN
        BEGIN
         RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                    ,UN_ERROR_COD => PCK_ERRORES.ERR_HV_MSG_PR_VALFIL_FECHASNUL);
        END;         
      END IF;

      RETURN;
    END IF;

    --Cuando el indicador consolidado esta marcado
    IF UN_IND_CONSOLIDADO NOT IN(0) AND UN_INFORME IN(0) THEN
      BEGIN
       RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                  ,UN_ERROR_COD => PCK_ERRORES.ERR_HV_MSG_PR_VALFIL_INFACERDE);
      END;          
    END IF;

    --Cuando el indicador estado actual esta marcado
    IF UN_IND_ESTADO NOT IN(0) AND UN_ESTADO IN(0) THEN
      BEGIN
       RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE
                                  ,UN_ERROR_COD => PCK_ERRORES.ERR_HV_MSG_PR_VALFIL_ESTADONUL);
      END;          
    END IF;
  END PR_VALIDARFILTROSIMPRESIONHV;  
--3
PROCEDURE PR_VALIDARESTUDIOS
(

    /*
    NAME              : PR_VALIDARESTUDIOS 
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRATION    : 18/12/2017
    TIME              : 02:20 PM
    SOURCE MODULE     :
    DESCRIPTION       : Valida si se puede eliminar el registro en la tabla NAT_EDUCACION_BASICAYMEDIA
    MODIFIED BY       : 

    @NAME  : validarEstudiosSuperiores
    @METHOD  : GET
  */
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_NUMERO_DCTO  IN PCK_SUBTIPOS.TI_TERCERO,
  UN_SUCURSAL     IN PCK_SUBTIPOS.TI_SUCURSAL
)
  AS 
    MI_STRSQL     PCK_SUBTIPOS.TI_STRSQL;
    MI_NUMERO     PCK_SUBTIPOS.TI_ENTERO;
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
  BEGIN 
    BEGIN
      SELECT  COUNT(NUMERO_DCTO) NUMERO_DCTO
      INTO    MI_NUMERO
      FROM    NAT_FORMACION_ACADEMICA
      WHERE   COMPANIA    = UN_COMPANIA
        AND   NUMERO_DCTO = UN_NUMERO_DCTO
        AND   SUCURSAL    = UN_SUCURSAL;
    EXCEPTION WHEN NO_DATA_FOUND THEN   
      MI_NUMERO := 0;
    END;


    IF MI_NUMERO > 0 THEN
      BEGIN    
        RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;   
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
          PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE
         ,UN_TABLAERROR => 'NAT_FORMACION_ACADEMICA',
                    UN_ERROR_COD  =>PCK_ERRORES.ERR_FORMACIONACADEMICA);
      END;
    END IF;

    BEGIN
      SELECT  COUNT(NUMERO_DCTO) NUMERO_DCTO
      INTO    MI_NUMERO
      FROM    NAT_OTROS_ESTUDIOS
      WHERE   COMPANIA    = UN_COMPANIA
        AND   NUMERO_DCTO = UN_NUMERO_DCTO
        AND   SUCURSAL    = UN_SUCURSAL;
    EXCEPTION WHEN NO_DATA_FOUND THEN   
      MI_NUMERO := 0;
    END;

    IF MI_NUMERO > 0 
    THEN 
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;   
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
          PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE
                   ,UN_TABLAERROR => 'NAT_OTROS_ESTUDIOS',
                    UN_ERROR_COD  =>PCK_ERRORES.ERR_OTROSESTUDIOS);
      END;
    END IF;   

    BEGIN 
      BEGIN 
        MI_CONDICION := ' COMPANIA     = '''||UN_COMPANIA||'''
                      AND NUMERO_DCTO  = '''||UN_NUMERO_DCTO||'''
                      AND SUCURSAL     ='''||UN_SUCURSAL||'''';


        PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME (  UN_TABLA     => 'NAT_EDUCACION_BASICAYMEDIA',
                                                UN_ACCION    => 'E',
                                                UN_CONDICION => MI_CONDICION); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
              PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                          UN_ERROR_COD  => PCK_ERRORES.ERR_FORMACIONBASICA,
                                          UN_TABLAERROR => 'NAT_EDUCACION_BASICAYMEDIA'); 
      END;
END PR_VALIDARESTUDIOS  ; 

-- 4
FUNCTION FC_CERRARCONVOCATORIA
    /*
    NAME              : PR_CERRARCONVOCATORIA 
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : SERGIO ESTEBAN PIÃ‘A VARGAS
    DATE MIGRATION    : 30/01/2018
    TIME              : 10:30 am
    AUTHOR MODIFIED   : SANDRA MILENA DAZA LEGUIZAMON
    DATE MODIFIED     : 06/02/2018
    SOURCE MODULE     : SysmanHv2018.01.07_HV_SST_Manual_SelPersonal_Bienestar
    DESCRIPTION       : Genera la lista de elegibles a partir de las diferentes pruebas
    MODIFIED BY       : Se agrega al proceso un indicador para cerrar la convocatoria y no poder realizar procesos sobre esta. Se adicionan controles 

    @NAME  : cerrarConvocatoria
    @METHOD  : GET
  */
(
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CONVOCATORIA IN NAT_CALIFICACION_PRUEBAS.NRO_CONVOCATORIA%TYPE,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN VARCHAR2
AS 
  MI_MAXCANT        PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_CANTELEG       PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_AUX            PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
  MI_RTA            PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CANTELEGIBLES  PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_CERRADA        PCK_SUBTIPOS.TI_LOGICO := 0;
  MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
  MI_NOINSCRITOS    VARCHAR2(200 CHAR) := '';

BEGIN
  -- VALIDAR EL ESTADO DE LA CONVOCATORIA 
  SELECT  CERRADA
  INTO    MI_CERRADA
  FROM    NAT_APERTURA
  WHERE   COMPANIA          = UN_COMPANIA  
  AND     NRO_CONVOCATORIA  = UN_CONVOCATORIA;

  BEGIN
    IF MI_CERRADA <> 0 THEN
      RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;   
    END IF;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
      MI_REEMPLAZOS (0).CLAVE := 'NRO_CONVOCATORIA';
      MI_REEMPLAZOS (0).VALOR := UN_CONVOCATORIA;    
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                 UN_TABLAERROR  => 'NAT_APERTURA',
                                 UN_ERROR_COD   => PCK_ERRORES.ERR_HV_CERRARCONV_CERRADA,                                   
                                 UN_REEMPLAZOS  => MI_REEMPLAZOS);

  END;

  -- DEBIDO A QUE NO EXISTIA EL CONTROL DE CERRAR CON UN INDICADOR LA CONVOCATORIA SE DEBE ELIMINAR LOS DATOS DE ELEGIBLES PREVIOS EN CASO DE QUE EXISTAN
      BEGIN
        BEGIN 
          MI_CONDICION := 'COMPANIA         = ''' || UN_COMPANIA || ''' 
                     AND NRO_CONVOCATORIA = ' || UN_CONVOCATORIA;


          PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME (  UN_TABLA     => 'NAT_ELEGIBLES',
                                                  UN_ACCION    => 'E',
                                                  UN_CONDICION => MI_CONDICION); 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
          MI_REEMPLAZOS (0).CLAVE := 'NRO_CONVOCATORIA';
          MI_REEMPLAZOS (0).VALOR := UN_CONVOCATORIA;   
          PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_HV_CERRARCONV_ELIMELEG,
                                      UN_TABLAERROR => 'NAT_ELEGIBLES');  
      END;   
  -- CONSULTA LA CANTIDAD DE PRUEBAS ESTABLECIDAS EN LA CONVOCATORIA
  SELECT  COUNT(0)
  INTO    MI_MAXCANT
  FROM    NAT_RELACION_PRUEBAS
  WHERE   COMPANIA          = UN_COMPANIA  
  AND     NRO_CONVOCATORIA  = UN_CONVOCATORIA;

  BEGIN
    IF MI_MAXCANT = 0 THEN
      RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;   
    END IF;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
      MI_REEMPLAZOS (0).CLAVE := 'NRO_CONVOCATORIA';
      MI_REEMPLAZOS (0).VALOR := UN_CONVOCATORIA;    
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                 UN_TABLAERROR  => 'NAT_APERTURA',
                                 UN_ERROR_COD   => PCK_ERRORES.ERR_HV_CERRARCONV_SINPRUEBAS,                                   
                                 UN_REEMPLAZOS  => MI_REEMPLAZOS);

  END;

  <<INSCRITOSCONPRUEBAS>>
  FOR RS IN (  SELECT COUNT(*) AS CANTIDAD,
                      NAT_CALIFICACION_PRUEBAS.NUMERO_DCTO,
                      NAT_CALIFICACION_PRUEBAS.SUCURSAL
                 FROM NAT_CALIFICACION_PRUEBAS
            LEFT JOIN NAT_APERTURA_INSCRITOS
                   ON NAT_CALIFICACION_PRUEBAS.COMPANIA          = NAT_APERTURA_INSCRITOS.COMPANIA
                  AND NAT_CALIFICACION_PRUEBAS.NUMERO_DCTO       = NAT_APERTURA_INSCRITOS.NUMERO_DCTO
                  AND NAT_CALIFICACION_PRUEBAS.SUCURSAL          = NAT_APERTURA_INSCRITOS.SUCURSAL
                  AND NAT_CALIFICACION_PRUEBAS.NRO_CONVOCATORIA  = NAT_APERTURA_INSCRITOS.NRO_CONVOCATORIA
            INNER JOIN  NAT_RELACION_PRUEBAS
                    ON NAT_RELACION_PRUEBAS.NRO_CONVOCATORIA      = NAT_CALIFICACION_PRUEBAS.NRO_CONVOCATORIA
                    AND NAT_RELACION_PRUEBAS.PRUEBA               = NAT_CALIFICACION_PRUEBAS.PRUEBA
                WHERE NAT_CALIFICACION_PRUEBAS.COMPANIA          = UN_COMPANIA
                  AND NAT_CALIFICACION_PRUEBAS.NRO_CONVOCATORIA  = UN_CONVOCATORIA
                  AND NAT_APERTURA_INSCRITOS.RECHAZOEXPERIENCIA IN (0)
                  AND NAT_APERTURA_INSCRITOS.RECHAZOESTUDIOS    IN (0)
                  AND NAT_APERTURA_INSCRITOS.RECHAZOOTROS       IN (0)
                  AND NAT_CALIFICACION_PRUEBAS.CALIFICACION    >= NAT_RELACION_PRUEBAS.CALIFICACION
                  AND NAT_RELACION_PRUEBAS.PUNMAX <> 0
             GROUP BY NAT_CALIFICACION_PRUEBAS.NUMERO_DCTO,
                NAT_CALIFICACION_PRUEBAS.SUCURSAL
              ORDER BY COUNT(*) DESC)
  LOOP
    -- CAMBIA EL VALOR DE LA VARIABLE MI_AUX A 1 SI EXISTEN INSCRITOS CON CALIFICACIONES
    -- SE VERIFICA SI LOS INSCRITOS PRESENTARON  TODAS LAS PRUEBAS
    IF RS.CANTIDAD = MI_MAXCANT THEN
      -- CAMBIA EL VALOR DE LA VARIABLE MI_AUX A 2 SI EXISTEN INSCRITOS CON CALIFICACIONES COMPLETAS
      -- EL INSCRITO CUMPLIO CON LAS PRUEBAS DE  LA CONVOCATORIA, SE DEBE CALCULAR EL PUNTAJE TOTAL                
      -- INSERTAR EL INSCRITO QUE CUMPLE CON LAS CONDICIONES PARA SER ELEGIBLE   
      MI_CAMPOS  := ' COMPANIA
                    , NRO_CONVOCATORIA
                    , NUMERO_DCTO
                    , SUCURSAL
                    , TOTAL
                    , ACEPTO
                    , CREATED_BY
                    , DATE_CREATED
                    ';

      MI_VALORES := 'SELECT '''|| UN_COMPANIA ||''', 
                      '''|| UN_CONVOCATORIA ||''',
                      '''|| RS.NUMERO_DCTO ||''',
                      '''|| RS.SUCURSAL ||''',
                      SUM(( NVL(NAT_RELACION_PRUEBAS.VALOR_CONCURSO,0) * NVL(NAT_CALIFICACION_PRUEBAS.CALIFICACION,0)) / NVL(NAT_RELACION_PRUEBAS.PUNMAX,1)) MI_TOTAL,
                      -1,
                      '''|| UN_USUARIO ||''',
                      SYSDATE
                    FROM NAT_RELACION_PRUEBAS
                    INNER JOIN NAT_CALIFICACION_PRUEBAS
                    ON NAT_RELACION_PRUEBAS.NRO_CONVOCATORIA      = NAT_CALIFICACION_PRUEBAS.NRO_CONVOCATORIA
                    AND NAT_RELACION_PRUEBAS.PRUEBA               = NAT_CALIFICACION_PRUEBAS.PRUEBA
                    WHERE NAT_CALIFICACION_PRUEBAS.COMPANIA       = '''|| UN_COMPANIA ||'''
                    AND NAT_CALIFICACION_PRUEBAS.NUMERO_DCTO      = '''|| RS.NUMERO_DCTO ||'''
                    AND NAT_CALIFICACION_PRUEBAS.SUCURSAL         = '''|| RS.SUCURSAL ||'''
                    AND NAT_CALIFICACION_PRUEBAS.NRO_CONVOCATORIA = '''|| UN_CONVOCATORIA ||'''
                    AND NAT_CALIFICACION_PRUEBAS.CALIFICACION    >= NAT_RELACION_PRUEBAS.CALIFICACION
                    AND NAT_RELACION_PRUEBAS.PUNMAX <> 0
                    ';


      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME (UN_CAMPOS  => MI_CAMPOS, 
                                       UN_VALORES => MI_VALORES, 
                                       UN_TABLA   => 'NAT_ELEGIBLES', 
                                       UN_ACCION  => 'IS'); 
        EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
        END;
      EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE,  
                                    UN_ERROR_COD => PCK_ERRORES.ERR_HV_CERRARCONV_NOREGELEG );
      END;


    ELSE 
       MI_NOINSCRITOS :=MI_NOINSCRITOS ||','|| RS.NUMERO_DCTO;
    END IF; 
  END LOOP INSCRITOSCONPRUEBAS;

  MI_CAMPOS:= '  CERRADA = -1 ' ||
             ', DATE_MODIFIED = SYSDATE' ||
             ', MODIFIED_BY  = ''' || UN_USUARIO ||'''';
  MI_CONDICION := 'COMPANIA         = ''' || UN_COMPANIA || ''' 
                     AND NRO_CONVOCATORIA = ' || UN_CONVOCATORIA; 
  BEGIN
    BEGIN
        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA        => 'NAT_APERTURA',
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_CAMPOS,
                                        UN_CONDICION    => MI_CONDICION);                                    

    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      RAISE  PCK_EXCEPCIONES.EXC_HOJAS_VIDA; 
    END;
  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN                    
            MI_REEMPLAZOS (0).CLAVE := 'NRO_CONVOCATORIA';
            MI_REEMPLAZOS (0).VALOR := UN_CONVOCATORIA; 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                       UN_TABLAERROR  => 'NAT_APERTURA',
                                       UN_ERROR_COD   => PCK_ERRORES.ERR_HV_CERRARCONV_NOCERRO,                                   
                                       UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;
   RETURN MI_NOINSCRITOS;
END FC_CERRARCONVOCATORIA;

--5
PROCEDURE PR_CALIFICAR_EVALUACION
/*
    NAME              : PR_CALIFICAR_EVALUACION
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 31/01/2018
    TIME              : 02:00 PM  
    SOURCE MODULE     : SysmanHv2018.01.07_HV_SST_Manual_SelPersonal_Bienestar
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : REGISTRA LOS DETALLES DE LA EVALUACION 

      @NAME:    calificarEvaluacion 
      @METHOD:  POST
  */
(
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ID_EMPLEADO        IN PERSONAL.ID_DE_EMPLEADO%TYPE,
    UN_EVALUACION         IN EV_DETALLE_EVALUACION.NUMERO_EVALUACION%TYPE,
    UN_CLASE_EVALUACION   IN EV_CRITERIO_GRUPO.CLASE_EVALUACION%TYPE,
    UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
  MI_EXISTE         PCK_SUBTIPOS.TI_ENTERO;
  MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGENOEXIS    PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGEUSING      PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE     PCK_SUBTIPOS.TI_MERGEENLACE;
BEGIN
  FOR RS IN (SELECT PERSONAL.ID_DE_EMPLEADO,
                    PERSONAL.NUMERO_DCTO,
                    PERSONAL.NOMBRECOMPLETO,
                    PERSONAL.SUCURSAL,
                    PERSONAL.ID_DE_CARGO,
                    PERSONAL.ESCALAFON
              FROM PERSONAL
              WHERE PERSONAL.COMPANIA     = UN_COMPANIA
              AND PERSONAL.ID_DE_EMPLEADO = UN_ID_EMPLEADO
              AND PERSONAL.ESTADO_ACTUAL IN (1)) 
  LOOP

      SELECT COUNT(1)
            INTO MI_EXISTE
      FROM EV_DETALLE_EVALUACION
      WHERE COMPANIA           = UN_COMPANIA
        AND NUMERO_EVALUACION  = UN_EVALUACION
        AND CLASE_EVALUACION   = 2
        AND TIPO_EVALUACION    = 001
        AND CEDULA_EVALUADO    = RS.NUMERO_DCTO
        AND SUCURSAL_EVALUADO  =  RS.SUCURSAL
        AND CEDULA_EVALUADOR   = RS.NUMERO_DCTO
        AND SUCURSAL_EVALUADOR = RS.SUCURSAL;

      IF MI_EXISTE = 0 THEN
       MI_CAMPOS :='COMPANIA,                  
                    NUMERO_EVALUACION,                  
                    CLASE_EVALUACION,                  
                    TIPO_EVALUACION,                  
                    CEDULA_EVALUADO,                  
                    SUCURSAL_EVALUADO,                  
                    CEDULA_EVALUADOR,                  
                    SUCURSAL_EVALUADOR,                  
                    CARGO_EVALUADOR,                  
                    CARGO_EVALUADO,                  
                    FECHA,                  
                    HORA,                  
                    CODIGO_EMPLEADO_EVALUADO,                  
                    CODIGO_EMPLEADO_EVALUADOR,                  
                    ESCALAFON_EVALUADOR,                  
                    ESCALAFON_EVALUADO,                  
                    CREATED_BY,                  
                    DATE_CREATED,                  
                    OBSERVACIONES,                  
                    EVALUADOR_COMISION';

       MI_VALORES:=''''||UN_COMPANIA||''',
                    '|| UN_EVALUACION||',
                    2,
                    ''001'',
                    '''|| RS.NUMERO_DCTO||''',
                    '''|| RS.SUCURSAL||''',
                    '''|| RS.NUMERO_DCTO||''',
                    '''|| RS.SUCURSAL||''',
                    '''|| RS.ID_DE_CARGO || ''',
                    '''|| RS.ID_DE_CARGO || ''',
                    SYSDATE,
                    TO_DATE('''|| '01/01/1970 ' ||TO_CHAR(SYSDATE, 'HH24:MI:SS')||''', ''DD/MM/YYYY HH24:MI:SS''),
                    '''|| RS.ID_DE_EMPLEADO||''',
                    '''|| RS.ID_DE_EMPLEADO||''',
                    '''|| RS.ESCALAFON||''',
                    '''|| RS.ESCALAFON||''',
                    '''|| UN_USUARIO ||''',
                    SYSDATE,
                    ''.'',
                    '''||RS.ID_DE_EMPLEADO||'''
                    ';
          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => 'EV_DETALLE_EVALUACION', 
                                                      UN_ACCION   => 'I', 
                                                      UN_CAMPOS   => MI_CAMPOS, 
                                                      UN_VALORES  => MI_VALORES);
            EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
            END;
          EXCEPTION
          WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE,  
                                        UN_ERROR_COD => PCK_ERRORES.ERR_HV_INSDETALLEEVALUACION );
          END;
      END IF;

      MI_MERGEUSING := 'SELECT DISTINCT EV_CRITERIO_GRUPO.COMPANIA,
                               '''||UN_EVALUACION||''' EVALUACION,
                               '''||RS.ID_DE_CARGO ||''' CARGO_EVALUADOR,
                               '''||RS.ID_DE_CARGO ||''' CARGO_EVALUADO,
                               EV_CRITERIOS_EVALUACION.CODIGO CRITERIO_EVALUADO,
                               EV_CRITERIO_GRUPO.CLASE_EVALUACION  CLASE_EVALUACION,
                               ''001'' TIPO_EVALUACION,
                               0 VALOR_ASIGNADO,
                               '''||RS.NUMERO_DCTO ||''' CEDULA_EVALUADO,
                               '''||RS.SUCURSAL ||''' SUCURSAL_EVALUADO,
                               '''||RS.NUMERO_DCTO ||''' CEDULA_EVALUADOR,
                               '''||RS.SUCURSAL ||''' SUCURSAL_EVALUADOR,
                               EV_CRITERIO_GRUPO.GRUPO,
                               EV_CRITERIOS_EVALUACION.MOVIMIENTO,
                               EV_CRITERIOS_EVALUACION.PUNTAJE,
                               EV_CRITERIOS_EVALUACION.IND_TEXTO,
                               '''' TEXTO,
                               '''||RS.ID_DE_CARGO ||''' ESCALAFON_EVALUADOR,
                               '''||RS.ID_DE_CARGO ||'''  ESCALAFON_EVALUADO
              FROM  EV_CRITERIOS_EVALUACION
                INNER JOIN EV_CRITERIO_GRUPO  
                  ON  EV_CRITERIOS_EVALUACION.COMPANIA          = EV_CRITERIO_GRUPO.COMPANIA 
                  AND  SUBSTR(EV_CRITERIOS_EVALUACION.CODIGO,1,LENGTH(EV_CRITERIO_GRUPO.CODIGO_CRITERIO)) = EV_CRITERIO_GRUPO.CODIGO_CRITERIO
                  AND EV_CRITERIOS_EVALUACION.CLASE_EVALUACION  = EV_CRITERIO_GRUPO.CLASE_EVALUACION         
                INNER JOIN EV_EVALUACIONES 
                  ON  EV_CRITERIO_GRUPO.GRUPO    = EV_EVALUACIONES.GRUPO_APLICAR
                  AND EV_CRITERIO_GRUPO.COMPANIA = EV_EVALUACIONES.COMPANIA
                  AND EV_CRITERIO_GRUPO.CLASE_EVALUACION = EV_EVALUACIONES.CLASE_EVALUACION 
                WHERE  EV_CRITERIO_GRUPO.COMPANIA         =  '''||UN_COMPANIA||'''
                  AND  EV_CRITERIO_GRUPO.CLASE_EVALUACION =  '||UN_CLASE_EVALUACION||'';

            MI_MERGEENLACE:=' TABLA.COMPANIA                =   VISTA.COMPANIA
                              AND TABLA.EVALUACION          =   VISTA.EVALUACION
                              AND TABLA.CEDULA_EVALUADO     =   VISTA.CEDULA_EVALUADO
                              AND TABLA.SUCURSAL_EVALUADO   =   VISTA.SUCURSAL_EVALUADO
                              AND TABLA.CEDULA_EVALUADOR    =   VISTA.CEDULA_EVALUADOR
                              AND TABLA.SUCURSAL_EVALUADOR  =   VISTA.SUCURSAL_EVALUADOR
                              AND TABLA.CRITERIO_EVALUADO   =   VISTA.CRITERIO_EVALUADO
                              AND TABLA.CLASE_EVALUACION    =   VISTA.CLASE_EVALUACION';

            MI_MERGEEXISTE:=' UPDATE SET  TABLA.GRUPO = VISTA.GRUPO,
                                          TABLA.MOVIMIENTO= VISTA.MOVIMIENTO,
                                          TABLA.PUNTAJE = VISTA.PUNTAJE,
                                          TABLA.IND_TEXTO = VISTA.IND_TEXTO
                                          WHERE VISTA.PUNTAJE <> 0';

             MI_MERGENOEXIS :='INSERT (
                        COMPANIA,
                        EVALUACION,
                        CARGO_EVALUADOR,
                        CARGO_EVALUADO,
                        CRITERIO_EVALUADO,
                        CLASE_EVALUACION,
                        TIPO_EVALUACION,
                        VALOR_ASIGNADO,
                        FECHA,
                        HORA,
                        CEDULA_EVALUADO,
                        SUCURSAL_EVALUADO,
                        CEDULA_EVALUADOR,
                        SUCURSAL_EVALUADOR,
                        GRUPO,
                        MOVIMIENTO,
                        PUNTAJE,
                        IND_TEXTO,
                        TEXTO,
                        CREATED_BY,
                        DATE_CREATED,
                        ESCALAFON_EVALUADOR,
                        ESCALAFON_EVALUADO) VALUES 
                        ( VISTA.COMPANIA,
                          VISTA.EVALUACION,
                          VISTA.CARGO_EVALUADOR,
                          VISTA.CARGO_EVALUADOR,
                          VISTA.CRITERIO_EVALUADO,
                          VISTA.CLASE_EVALUACION,
                          VISTA.TIPO_EVALUACION,
                          VISTA.VALOR_ASIGNADO,
                          SYSDATE,
                          TO_DATE(''01/01/1970 '|| TO_CHAR(SYSDATE, 'HH24:MI:SS')||' '', ''DD/MM/YYYY HH24:MI:SS''),
                          VISTA.CEDULA_EVALUADO,
                          VISTA.SUCURSAL_EVALUADO,
                          VISTA.CEDULA_EVALUADOR,
                          VISTA.SUCURSAL_EVALUADOR,
                          VISTA.GRUPO,
                          VISTA.MOVIMIENTO,
                          VISTA.PUNTAJE,
                          VISTA.IND_TEXTO,
                          VISTA.TEXTO,
                          '''||UN_USUARIO||''',
                          SYSDATE,
                          VISTA.ESCALAFON_EVALUADOR,
                          VISTA.ESCALAFON_EVALUADO)';

          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA        => 'EV_SUBDETALLE_EVALUACION', 
                                                      UN_ACCION       => 'IM', 
                                                      UN_MERGEUSING   => MI_MERGEUSING, 
                                                      UN_MERGEENLACE  => MI_MERGEENLACE,
                                                      UN_MERGEEXISTE  => MI_MERGEEXISTE,
                                                      UN_MERGENOEXIS  => MI_MERGENOEXIS);
            EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
            END;
          EXCEPTION
          WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE,  
                                        UN_ERROR_COD => PCK_ERRORES.ERR_HV_INSSUBDETALLEEVALUACION );
          END; 

  END LOOP;
END PR_CALIFICAR_EVALUACION;

--6
PROCEDURE PR_ACT_CAMPOS_NULOS
 /*
      NAME              : PR_ACT_CAMPOS_NULOS --> EN ACCESS ACTUALIZAR_CAMPOS_Click()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 25/04/2018
      TIME              : 14:26 PM
      SOURCE MODULE     : SYSMAN_HV_2018_01_02_SEGURIDAD
      MODIFIER          :
      DATE MODIFIED     : 
      TIME              :
      DESCRIPTION       : PROCEDIMIENTO QUE GENERA ACTUALIZAR CAMPOS NULOS DE PERSONAL POR DATOS PERSONALES
      PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
      PARAMETERS        : UN_NUMERO_DCTO   => NUMERO_DCTO NUMERO DE DOCUMENTO POR EL CUAL SE REALIZA EL PROCEDIMIENTO.                               
      @NAME:  actualizarCamposNulosPersonal
      @METHOD:  GET
    */
(    
  UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_NUMERO_DCTO         IN PERSONAL.NUMERO_DCTO%TYPE 
)

AS 
  MI_RDS                    SYS_REFCURSOR;
  MI_RSCONCEPTOSSINCONF     SYS_REFCURSOR;
  MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
  MI_CONTADOR               NUMBER(10,0);
  MI_TABLA                  PCK_SUBTIPOS.TI_TABLA;
  MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_MERGEUSING             PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE            PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE            PCK_SUBTIPOS.TI_MERGEEXISTE;

BEGIN
  FOR MI_RDS IN (SELECT NAT_DATOS_PERSONALES.NUMERO_DCTO CEDULA,
                    PERSONAL.NUMERO_DCTO,
                    NAT_DATOS_PERSONALES.SUCURSAL,
                    NAT_DATOS_PERSONALES.FECHARETIRO,
                    NAT_DATOS_PERSONALES.APELLIDO1,
                    NAT_DATOS_PERSONALES.APELLIDO2,
                    NAT_DATOS_PERSONALES.NOMBRES,
                    NAT_DATOS_PERSONALES.CREATED_BY
               FROM NAT_DATOS_PERSONALES
                    LEFT JOIN PERSONAL
                           ON NAT_DATOS_PERSONALES.NUMERO_DCTO      = PERSONAL.NUMERO_DCTO
                          AND NAT_DATOS_PERSONALES.SUCURSAL         = PERSONAL.SUCURSAL
              WHERE NAT_DATOS_PERSONALES.NUMERO_DCTO                IS NOT NULL
                          AND PERSONAL.NUMERO_DCTO                  =  UN_NUMERO_DCTO
                          AND PERSONAL.ESTADO_ACTUAL                NOT IN (3)
                          AND NAT_DATOS_PERSONALES.FECHARETIRO IS NULL
              ORDER BY NAT_DATOS_PERSONALES.NUMERO_DCTO)
  LOOP 

    PR_ACT_CAMPOS_NULOS_NU( UN_COMPANIA      => UN_COMPANIA        
                           ,UN_NUMERO_DCTO   => UN_NUMERO_DCTO   
                           ,UN_SUCURSAL      => MI_RDS.SUCURSAL   
                           ,UN_USUARIO       => MI_RDS.CREATED_BY );

BEGIN 
  MI_TABLA := 'NAT_FORMACION_ACADEMICA';
  MI_MERGEUSING := 'SELECT DETALLE_PROFESIONES.COMPANIA,
                          DETALLE_PROFESIONES.NUMERO_DCTO,
                          DETALLE_PROFESIONES.NOMBRE_PROFESION,
                          DETALLE_PROFESIONES.EGRESADO_DE  
                     FROM DETALLE_PROFESIONES
                          LEFT JOIN PERSONAL 
                                 ON DETALLE_PROFESIONES.COMPANIA       = PERSONAL.COMPANIA
                                AND DETALLE_PROFESIONES.NUMERO_DCTO    = PERSONAL.NUMERO_DCTO
                                AND DETALLE_PROFESIONES.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                    WHERE PERSONAL.COMPANIA                            = '''||UN_COMPANIA||'''
                                AND PERSONAL.NUMERO_DCTO               = '''||UN_NUMERO_DCTO||'''
                                AND PERSONAL.ESTADO_ACTUAL             NOT IN (3)
                    ORDER BY DETALLE_PROFESIONES.NUMERO_DCTO';   

  MI_MERGEENLACE := 'TABLA.COMPANIA = VISTA.COMPANIA AND TABLA.NUMERO_DCTO = VISTA.NUMERO_DCTO';

  MI_MERGEEXISTE := 'UPDATE SET TABLA.TITULOOBTENIDO = VISTA.NOMBRE_PROFESION , TABLA.ESTABLECIMIENTO = VISTA.EGRESADO_DE'; 

  BEGIN      
  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA,
                                         UN_ACCION      => 'MM' ,
                                         UN_MERGEUSING  => MI_MERGEUSING , 
                                         UN_MERGEENLACE => MI_MERGEENLACE , 
                                         UN_MERGEEXISTE => MI_MERGEEXISTE);

               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                                                
  END;   
END;

BEGIN
  MI_TABLA := 'NAT_OTROS_ESTUDIOS';
  MI_MERGEUSING := 'SELECT NAT_OTROS_ESTUDIOS.COMPANIA,
                          NAT_OTROS_ESTUDIOS.NUMERO_DCTO,
                          NAT_OTROS_ESTUDIOS.SUCURSAL,
                          NAT_OTROS_ESTUDIOS.NUMERO,
                          NAT_OTROS_ESTUDIOS.NOE_CODIGOPERSONA,
                          PERSONAL.OTROSESTUDIOS
                     FROM NAT_OTROS_ESTUDIOS
                          LEFT JOIN PERSONAL 
                                 ON NAT_OTROS_ESTUDIOS.COMPANIA    = PERSONAL.COMPANIA
                                AND NAT_OTROS_ESTUDIOS.NUMERO_DCTO = PERSONAL.NUMERO_DCTO
                    WHERE NAT_OTROS_ESTUDIOS.COMPANIA              = '''||UN_COMPANIA||'''
                                AND NAT_OTROS_ESTUDIOS.NUMERO_DCTO = '''||UN_NUMERO_DCTO||'''
                                AND PERSONAL.ESTADO_ACTUAL         NOT IN (3)
                    ORDER BY NAT_OTROS_ESTUDIOS.NUMERO_DCTO';

  MI_MERGEENLACE := 'TABLA.COMPANIA = VISTA.COMPANIA AND TABLA.NUMERO_DCTO = VISTA.NUMERO_DCTO';

  MI_MERGEEXISTE := 'UPDATE SET TABLA.TITULOOBTENIDO = VISTA.OTROSESTUDIOS'; 

  BEGIN

  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA,
                                         UN_ACCION      => 'MM',
                                         UN_MERGEUSING  => MI_MERGEUSING, 
                                         UN_MERGEENLACE => MI_MERGEENLACE, 
                                         UN_MERGEEXISTE => MI_MERGEEXISTE);

               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                                                
  END;   
END;               

  END LOOP ACT_CAMPOS_NULOS;

  END PR_ACT_CAMPOS_NULOS;

--7
PROCEDURE PR_ACT_CAMPOS_NULOS_NU
/*
      NAME              : PR_ACT_CAMPOS_NULOS_NU --> EN ACCESS actualiza_campos_nuevos
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 25/04/2018
      TIME              : 08:32 AM
      SOURCE MODULE     : SYSMAN_HV_2018_01_02_SEGURIDAD
      MODIFIER          :
      DATE MODIFIED     : 
      TIME              :
      DESCRIPTION       : PROCEDIMIENTO ACTUALIZAR OTROS CAMPOS NUEVOS
      PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
      PARAMETERS        : UN_NUMERO_DCTO   => NUMERO_DCTO NUMERO DE DOCUMENTO POR EL CUAL SE REALIZA EL PROCEDIMIENTO.
      PARAMETERS        : UN_SUCURSAL      => SUCURSAL POR EL CUAL SE REALIZA EL PROCEDIMIENTO.
      PARAMETERS        : UN_USUARIO       => USUARIO POR EL CUAL SE REALIZA EL PROCEDIMIENTO.

      @NAME:  actualizarCamposNuevos
      @METHOD:  GET
    */
(
  UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_NUMERO_DCTO         IN PERSONAL.NUMERO_DCTO%TYPE,
  UN_SUCURSAL            IN PCK_SUBTIPOS.TI_SUCURSAL,
  UN_USUARIO             IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_RSHV                   SYS_REFCURSOR;
  MI_RSNOM                  SYS_REFCURSOR;
  MI_RSCONCEPTOSSINCONF     SYS_REFCURSOR;
  MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
  MI_CONTADOR               NUMBER(10,0);
  MI_TABLA                  PCK_SUBTIPOS.TI_TABLA;
  MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
  MI_REEMPLAZOS             PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_PCKDATOS               PCK_SUBTIPOS.TI_RTA_ACME;
  MI_ERROR                  PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
  <<AUT>>
        FOR MI_RSHV IN (SELECT NAT_DATOS_PERSONALES.CODIGO AS COD,
                     NAT_DATOS_PERSONALES.NUMERO_DCTO,
                     NAT_DATOS_PERSONALES.SEXO, 
                     NAT_DATOS_PERSONALES.GRUPOSANGUINEO,
                     NAT_DATOS_PERSONALES.RH,
                     NAT_DATOS_PERSONALES.PAISNCTO,
                     NAT_DATOS_PERSONALES.DEPTONCTO,
                     NAT_DATOS_PERSONALES.MUNICIPIONCTO,
                     NAT_DATOS_PERSONALES.DIRECCIONRESIDENCIA,
                     NAT_DATOS_PERSONALES.TELEFONORESIDENCIA,
                     NAT_DATOS_PERSONALES.ALERGIAS,
                     NAT_DATOS_PERSONALES.MEDICOTRATANTE,
                     NAT_DATOS_PERSONALES.NUMLIBMILITAR,
                     NAT_DATOS_PERSONALES.LICENCIACONDUCCION,
                     NAT_DATOS_PERSONALES.CERTIFICADOJUDICIAL,
                     NAT_DATOS_PERSONALES.PERSONAS,
                     NAT_DATOS_PERSONALES.ESTADO_CIVIL,
                     NAT_DATOS_PERSONALES.EXPEDIDA,
                     NAT_DATOS_PERSONALES.FECHANCTO,
                     NAT_DATOS_PERSONALES.FECHATERCONTRATO,
                     NAT_DATOS_PERSONALES.PAISRESIDE,
                     NAT_DATOS_PERSONALES.CIUDAD_CUENTA,
                     NAT_DATOS_PERSONALES.REGIMEN,
                     NAT_DATOS_PERSONALES.DEPENDIENTES384,
                     NAT_DATOS_PERSONALES.FECHA_DEPENDIENTES384,
                     NAT_DATOS_PERSONALES.RETE_MINIMA,
                     NAT_DATOS_PERSONALES.FECHA_RETE_MINIMA,
                     NAT_DATOS_PERSONALES.DECLARANTES384,
                     NAT_DATOS_PERSONALES.FECHA_DECLARANTES384,
                     NAT_DATOS_PERSONALES.ASALARIADO_NOEMPLEADOS,
                     NAT_DATOS_PERSONALES.TIPO_SALARIO,
                     NAT_DATOS_PERSONALES.AREAMISOADM,
                     NAT_DATOS_PERSONALES.TIPOACTIVIDAD,
                     NAT_DATOS_PERSONALES.SEDE,
                     NAT_DATOS_PERSONALES.FECHAINGRESO,
                     NAT_DATOS_PERSONALES.DEPTORESIDE,
                     NAT_DATOS_PERSONALES.MUNICIPIORESIDE,
                     NAT_DATOS_PERSONALES.NUMEROPATRONAL,
                     NAT_DATOS_PERSONALES.EMAIL,
                     NAT_DATOS_PERSONALES.BANCO,
                     NAT_DATOS_PERSONALES.CUENTA,
                     NAT_DATOS_PERSONALES.TIPOCUENTA,
                     NAT_DATOS_PERSONALES.ESTRATO,
                     NAT_DATOS_PERSONALES.PAISEXTRANJERO,
                     NAT_DATOS_PERSONALES.DPTOEXPCEDULA,
                     NAT_DATOS_PERSONALES.NIVELSIIF,
                     NAT_DATOS_PERSONALES.GRUPOCONTABLE,
                     NAT_DATOS_PERSONALES.FONDO_SINDICATO,
                     NAT_DATOS_PERSONALES.PROCESORETENCION,
                     NAT_DATOS_PERSONALES.ID_DE_TIPO,
                     NAT_DATOS_PERSONALES.ID_CENTROS_DE_COSTO,
                     NAT_DATOS_PERSONALES.NIT_ESTABLECIMIENTO_DOCENTES
                FROM NAT_DATOS_PERSONALES
                     LEFT JOIN NAT_NOMBRAMIENTO
                            ON NAT_DATOS_PERSONALES.CODIGO = NAT_NOMBRAMIENTO.NB_CODIGOPERSONA
               WHERE NAT_DATOS_PERSONALES.COMPANIA  = UN_COMPANIA
               AND NAT_DATOS_PERSONALES.NUMERO_DCTO = UN_NUMERO_DCTO
               AND NAT_DATOS_PERSONALES.SUCURSAL    = UN_SUCURSAL
               ORDER BY NAT_DATOS_PERSONALES.NUMERO_DCTO ASC)                  
          LOOP

        FOR MI_RSNOM IN (SELECT PERSONAL.NUMERO_DCTO AS NUMERO,
                           PERSONAL.SEXO,
                           PERSONAL.GRUPOSANGINEO,
                           PERSONAL.RH,
                           PERSONAL.PAIS_NAC,
                           PERSONAL.DEPARTAMENTO_NAC,
                           PERSONAL.CIUDAD_NAC,
                           PERSONAL.DIRECCION,
                           PERSONAL.TELEFONOS,
                           PERSONAL.ALERGIAS,
                           PERSONAL.MEDICOTRATANTE,
                           PERSONAL.LIBRETA_MILITAR,
                           PERSONAL.LICENCIACONDUCCION,
                           PERSONAL.CERTIFICADOJUDICIAL,
                           PERSONAL.PERSONASCARGO,
                           PERSONAL.ESTADO_CIVIL,
                           PERSONAL.EXPEDIDA,
                           PERSONAL.FECHANCTO,
                           PERSONAL.NUM_RESOLUCION_ING,
                           PERSONAL.FECHA_RESOLUCION_ING,
                           PERSONAL.ACTA_POSESION,
                           PERSONAL.FECHA_ACTA_POSESION,
                           PERSONAL.FECHATERCONTRATO,
                           PERSONAL.PAIS_CED,
                           PERSONAL.CIUDAD_CUENTA,
                           PERSONAL.REGIMEN,
                           PERSONAL.DEPENDIENTES384,
                           PERSONAL.FECHA_DEPENDIENTES384,
                           PERSONAL.RETE_MINIMA,
                           PERSONAL.FECHA_RETE_MINIMA,
                           PERSONAL.DECLARANTES384,
                           PERSONAL.FECHA_DECLARANTES384,
                           PERSONAL.ASALARIADO_NOEMPLEADOS,
                           PERSONAL.TIPO_SALARIO,
                           PERSONAL.AREAMISOADM,
                           PERSONAL.TIPOACTIVIDAD,
                           PERSONAL.SEDE,
                           PERSONAL.NIVELSIIF,
                           PERSONAL.INGRESO_DISTRITO,
                           PERSONAL.INGRESODISTRITOREAL,
                           PERSONAL.PAIS_HAB,
                           PERSONAL.DEPARTAMENTO_HAB,
                           PERSONAL.CIUDAD_HAB,
                           PERSONAL.PAIS_LABORA,
                           PERSONAL.DEPARTAMENTO_LABORA,
                           PERSONAL.CIUDAD_LABORA,
                           PERSONAL.NUMEROPATRONAL,
                           PERSONAL.EMAIL_PERSONAL,
                           PERSONAL.BANCO,
                           PERSONAL.CUENTA,
                           PERSONAL.TIPOCUENTA,
                           PERSONAL.ESTRATO,
                           PERSONAL.DEPARTAMENTO_CED,
                           PERSONAL.GRUPOCONTABLE,
                           PERSONAL.FONDO_SINDICATO,
                           PERSONAL.PROCESORETENCION,
                           PERSONAL.ID_DE_TIPO,
                           PERSONAL.ID_CENTRO_DE_COSTO,
                           PERSONAL.NOMBRES
                      FROM PERSONAL
                     WHERE PERSONAL.COMPANIA           = UN_COMPANIA
                           AND PERSONAL.NUMERO_DCTO    = UN_NUMERO_DCTO
                           AND PERSONAL.SUCURSAL       = UN_SUCURSAL
                           AND PERSONAL.ESTADO_ACTUAL  NOT IN (3)
                     ORDER BY PERSONAL.NUMERO_DCTO ASC)
            LOOP

                IF MI_RSNOM.SEXO IS NULL AND  MI_RSHV.SEXO IS NOT NULL THEN
                   MI_CAMPOS:=  ',SEXO =''' || MI_RSHV.SEXO ||''' ';    
               END IF;

               IF MI_RSNOM.GRUPOSANGINEO IS NULL AND  MI_RSHV.GRUPOSANGUINEO IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS || ',GRUPOSANGUINEO ='''|| MI_RSHV.GRUPOSANGUINEO||''' ';
              END IF;

               IF MI_RSNOM.RH IS NULL AND MI_RSHV.RH IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',RH ='''||  MI_RSHV.RH||''' ';
              END IF;

               IF MI_RSNOM.PAIS_NAC IS NULL AND MI_RSHV.PAISNCTO IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',PAISNCTO ='''|| MI_RSHV.PAISNCTO||''' ';   
              END IF;

               IF MI_RSNOM.CIUDAD_NAC IS NULL AND MI_RSHV.MUNICIPIONCTO IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',MUNICIPIONCTO ='''|| MI_RSHV.MUNICIPIONCTO||''' ';  
              END IF;

              IF MI_RSNOM.DEPARTAMENTO_NAC  IS NULL AND MI_RSHV.DEPTONCTO  IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',DEPTONCTO = ''' || MI_RSHV.DEPTONCTO||''' ';
               END IF;

               IF MI_RSNOM.DIRECCION IS NULL AND MI_RSHV.DIRECCIONRESIDENCIA IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',DIRECCION ='''|| MI_RSHV.DIRECCIONRESIDENCIA||''' ';
              END IF;

               IF MI_RSNOM.TELEFONOS IS NULL AND MI_RSHV.TELEFONORESIDENCIA IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',TELEFONOS ='|| MI_RSHV.TELEFONORESIDENCIA||' ';
              END IF;

               IF MI_RSNOM.ALERGIAS IS NULL AND MI_RSHV.ALERGIAS IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',ALERGIAS ='''|| MI_RSHV.ALERGIAS||''' ';
              END IF;

               IF MI_RSNOM.MEDICOTRATANTE IS NULL AND MI_RSHV.MEDICOTRATANTE IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',MEDICOTRATANTE ='''|| MI_RSHV.MEDICOTRATANTE||''' ';
              END IF;

               IF MI_RSNOM.LIBRETA_MILITAR IS NULL AND MI_RSHV.NUMLIBMILITAR IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',LIBRETA_MILITAR ='''|| MI_RSHV.NUMLIBMILITAR||''' ';
              END IF;

               IF MI_RSNOM.LICENCIACONDUCCION IS NULL AND MI_RSHV.LICENCIACONDUCCION IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',LICENCIACONDUCCION ='''|| MI_RSHV.LICENCIACONDUCCION||''' ';
              END IF;

               IF MI_RSNOM.CERTIFICADOJUDICIAL IS NULL AND  MI_RSHV.CERTIFICADOJUDICIAL IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',CERTIFICACOJUDICIAL ='''|| MI_RSHV.CERTIFICADOJUDICIAL||''' ';
              END IF;

               IF MI_RSNOM.PERSONASCARGO IS NULL AND MI_RSHV.PERSONAS IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',PERSONAS ='''|| MI_RSHV.PERSONAS||''' ';
              END IF;

               IF MI_RSNOM.ESTADO_CIVIL IS NULL AND MI_RSHV.ESTADO_CIVIL IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',ESTADO_CIVIL ='''|| MI_RSHV.ESTADO_CIVIL||''' ';
              END IF;

               IF MI_RSNOM.DEPARTAMENTO_CED IS NULL AND MI_RSHV.DPTOEXPCEDULA IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',DPTOEXPCEDULA ='''|| MI_RSHV.DPTOEXPCEDULA||''' ';
              END IF;

               IF MI_RSNOM.FECHANCTO IS NULL AND MI_RSHV.FECHANCTO IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',FECHANCTO = TO_DATE('''||TO_CHAR (MI_RSHV.FECHANCTO,'DD/MM/YYYY HH24:MI:SS')||''', ''DD/MM/YYYY HH24:MI:SS'')'; 
              END IF;

               IF MI_RSNOM.FECHATERCONTRATO IS NULL AND MI_RSHV.FECHATERCONTRATO IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',FECHATERCONTRATO = TO_DATE('''||TO_CHAR (MI_RSHV.FECHATERCONTRATO,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'')';
              END IF;

               IF MI_RSNOM.PAIS_CED IS NULL AND MI_RSHV.PAISRESIDE IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',PAISRESIDE ='''|| MI_RSHV.PAISRESIDE||''' ';
              END IF;

               IF MI_RSNOM.CIUDAD_CUENTA IS NULL AND MI_RSHV.CIUDAD_CUENTA IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',CIUDAD_CUENTA ='''|| MI_RSHV.CIUDAD_CUENTA||''' ';
              END IF;

               IF MI_RSNOM.REGIMEN IS NULL AND MI_RSHV.REGIMEN IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',REGIMEN ='''|| MI_RSHV.REGIMEN||''' ';
              END IF;

               IF MI_RSNOM.DEPENDIENTES384 IS NULL AND MI_RSHV.DEPENDIENTES384 IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',DEPENDIENTES384 ='''|| MI_RSHV.DEPENDIENTES384||''' ' ;
              END IF;

               IF MI_RSNOM.FECHA_DEPENDIENTES384 IS NULL AND MI_RSHV.FECHA_DEPENDIENTES384 IS NOT NULL THEN
                 MI_CAMPOS := MI_CAMPOS ||',FECHA_DEPENDIENTES384 = TO_DATE('''||TO_CHAR (MI_RSHV.FECHA_DEPENDIENTES384,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'')';
              END IF;

               IF MI_RSNOM.RETE_MINIMA IS NULL AND MI_RSHV.RETE_MINIMA IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',RETE_MINIMA =''' || MI_RSHV.RETE_MINIMA||''' ';
              END IF;

               IF MI_RSNOM.FECHA_RETE_MINIMA IS NULL AND MI_RSHV.FECHA_RETE_MINIMA IS NOT NULL THEN
                 MI_CAMPOS := MI_CAMPOS ||',FECHA_RETE_MINIMA = TO_DATE('''||TO_CHAR (MI_RSHV.FECHA_RETE_MINIMA,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'')';
              END IF;

               IF MI_RSNOM.DECLARANTES384 IS NULL AND MI_RSHV.DECLARANTES384 IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',DECLARANTES384 ='''|| MI_RSHV.DECLARANTES384||''' ';
              END IF;

               IF MI_RSNOM.FECHA_DECLARANTES384 IS NULL AND MI_RSHV.FECHA_DECLARANTES384 IS NOT NULL THEN
                 MI_CAMPOS := MI_CAMPOS ||',FECHA_DECLARANTES384 = TO_DATE ('''||TO_CHAR(MI_RSHV.FECHA_DECLARANTES384,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'')';
              END IF;

               IF MI_RSNOM.ASALARIADO_NOEMPLEADOS IS NULL AND MI_RSHV.ASALARIADO_NOEMPLEADOS IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',ASALARIADO_NOEMPLEADOS ='''|| MI_RSHV.ASALARIADO_NOEMPLEADOS||''' ';
              END IF;

               IF MI_RSNOM.TIPO_SALARIO IS NULL AND MI_RSHV.TIPO_SALARIO IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',TIPO_SALARIO ='''|| MI_RSHV.TIPO_SALARIO||''' ';
              END IF;

               IF MI_RSNOM.AREAMISOADM IS NULL AND MI_RSHV.AREAMISOADM IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',AREAMISOADM ='''|| MI_RSHV.AREAMISOADM||''' ';
              END IF;

               IF MI_RSNOM.TIPOACTIVIDAD IS NULL AND MI_RSHV.TIPOACTIVIDAD IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',TIPOACTIVIDAD ='''|| MI_RSHV.TIPOACTIVIDAD||''' ';
              END IF;

               IF MI_RSNOM.SEDE IS NULL AND MI_RSHV.SEDE IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',SEDE ='''|| MI_RSHV.SEDE||''' ';
              END IF;

               IF MI_RSNOM.NIVELSIIF IS NULL AND MI_RSHV.NIVELSIIF IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',NIVELSIIF ='''|| MI_RSHV.NIVELSIIF||''' ';
              END IF;

               IF MI_RSNOM.INGRESO_DISTRITO IS NULL AND MI_RSHV.FECHAINGRESO IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',INGRESO_DISTRITO ='''|| MI_RSHV.FECHAINGRESO||''' ';
              END IF;

               IF MI_RSNOM.INGRESODISTRITOREAL IS NULL AND MI_RSHV.FECHAINGRESO IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',INGRESODISTRITOREAL ='''|| MI_RSHV.FECHAINGRESO||''' ';
              END IF;

               IF MI_RSNOM.PAIS_HAB IS NULL AND MI_RSHV.PAISRESIDE IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',PAIS_HAB ='''||MI_RSHV.PAISRESIDE||''' ';
              END IF;

               IF MI_RSNOM.DEPARTAMENTO_HAB IS NULL AND MI_RSHV.DEPTORESIDE IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',DEPARTAMENTO_HAB ='''|| MI_RSHV.DEPTORESIDE||''' ';
              END IF;

               IF MI_RSNOM.CIUDAD_HAB IS NULL AND MI_RSHV.MUNICIPIORESIDE IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',CIUDAD_HAB ='''|| MI_RSHV.MUNICIPIORESIDE||''' ';
              END IF;

               IF MI_RSNOM.PAIS_LABORA IS NULL AND MI_RSHV.PAISRESIDE IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',PAIS_LABORA ='''|| MI_RSHV.PAISRESIDE||''' ';
              END IF;

               IF MI_RSNOM.DEPARTAMENTO_LABORA IS NULL AND MI_RSHV.DEPTORESIDE IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',DEPARAMENTO_LABORA ='''|| MI_RSHV.DEPTORESIDE||''' ';
              END IF;

               IF MI_RSNOM.CIUDAD_LABORA IS NULL AND MI_RSHV.MUNICIPIORESIDE IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',CIUDAD_LABORA ='''|| MI_RSHV.MUNICIPIORESIDE||''' ';
              END IF;

               IF MI_RSNOM.NUMEROPATRONAL IS NULL AND MI_RSHV.NUMEROPATRONAL IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',NUMEROPATRONAL ='''|| MI_RSHV.NUMEROPATRONAL||''' ';
              END IF;

               IF MI_RSNOM.EMAIL_PERSONAL IS NULL AND MI_RSHV.EMAIL IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',EMAIL_PERSONAL ='''|| MI_RSHV.EMAIL||''' ';
              END IF;

               IF MI_RSNOM.EXPEDIDA IS NULL AND MI_RSHV.EXPEDIDA IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',EXPEDIDA ='''|| MI_RSHV.EXPEDIDA||''' ';
              END IF;

               IF MI_RSNOM.PAIS_NAC IS NULL AND MI_RSHV.PAISEXTRANJERO IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',PAISEXTRANJERO ='''|| MI_RSHV.PAISEXTRANJERO||''' ';
              END IF;

               IF MI_RSNOM.BANCO IS NULL AND MI_RSHV.BANCO IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',BANCO ='''|| MI_RSHV.BANCO||''' ';
              END IF;

               IF MI_RSNOM.CUENTA IS NULL AND MI_RSHV.CUENTA IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',CUENTA ='''|| MI_RSHV.CUENTA||''' ';
              END IF;

               IF MI_RSNOM.TIPOCUENTA IS NULL AND MI_RSHV.TIPOCUENTA IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',TIPOCUENTA ='''|| MI_RSHV.TIPOCUENTA||''' ';
              END IF;

               IF MI_RSNOM.ESTRATO IS NULL AND MI_RSHV.ESTRATO IS NOT NULL THEN
                  MI_CAMPOS := MI_CAMPOS ||',ESTRATO ='''|| MI_RSHV.ESTRATO||''' ';
              END IF;

               IF MI_RSNOM.NIVELSIIF IS NULL  THEN
                  MI_CAMPOS := MI_CAMPOS || ',NIVELSIIF = ''Nivel Administrativo'' '; 
              END IF;


             MI_TABLA:='NAT_DATOS_PERSONALES';
            MI_CAMPOS:= LTRIM(MI_CAMPOS||
                      ',NAT_DATOS_PERSONALES.DATE_MODIFIED = SYSDATE,' ||
                       'NAT_DATOS_PERSONALES.MODIFIED_BY         = ''' || UN_USUARIO ||'''' ,',');

         MI_CONDICION:='NAT_DATOS_PERSONALES.COMPANIA            = ''' || UN_COMPANIA || ''''||
                         'AND NAT_DATOS_PERSONALES.NUMERO_DCTO   = ''' || UN_NUMERO_DCTO ||''''||
                         'AND NAT_DATOS_PERSONALES.SUCURSAL      = ''' || UN_SUCURSAL || '''';


      BEGIN
       BEGIN
        MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                        UN_ACCION       => 'M',
                                        UN_CAMPOS       => MI_CAMPOS,
                                        UN_CONDICION    => MI_CONDICION);                                    

          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE  PCK_EXCEPCIONES.EXC_HOJAS_VIDA; 
      END;
          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN                    
                     MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                     MI_REEMPLAZOS (0).VALOR := 'NAT_DATOS_PERSONALES';    
                     MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                     MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                     PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                UN_TABLAERROR  => 'NAT_DATOS_PERSONALES',
                                                UN_ERROR_COD   => PCK_ERRORES.ERRR_HOJAS_VIDA_ACT_CONS,                                   
                                                UN_REEMPLAZOS  => MI_ERROR);
        END;

      MI_CAMPOS := '';

                IF MI_RSHV.SEXO IS NULL AND MI_RSNOM.SEXO IS NOT NULL THEN  
                   MI_CAMPOS := ',SEXO =''' || MI_RSNOM.SEXO||''' ';
               END IF;

                IF MI_RSHV.GRUPOSANGUINEO IS NULL AND MI_RSNOM.GRUPOSANGINEO IS NOT NULL THEN 
                   MI_CAMPOS := MI_CAMPOS || ',GRUPOSANGINEO = ''' || MI_RSNOM.GRUPOSANGINEO||''' ';
               END IF;

                IF MI_RSHV.RH IS NULL AND MI_RSNOM.RH IS NOT NULL THEN 
                   MI_CAMPOS := MI_CAMPOS || ',RH = ''' || MI_RSNOM.RH ||''' ';
               END IF;

                IF MI_RSHV.PAISNCTO IS NULL AND MI_RSNOM.PAIS_NAC IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',PAISNCTO =''' || MI_RSNOM.PAIS_NAC||''' ';
               END IF;

                IF MI_RSHV.DEPTONCTO IS NULL AND MI_RSNOM.DEPARTAMENTO_NAC IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',DEPTONCTO = ''' || MI_RSNOM.DEPARTAMENTO_NAC||''' ';
               END IF;

                IF MI_RSHV.MUNICIPIONCTO IS NULL AND MI_RSNOM.CIUDAD_NAC IS NOT NULL THEN 
                   MI_CAMPOS := MI_CAMPOS || ',MUNICIPIONCTO =''' || MI_RSNOM.CIUDAD_NAC ||''' ';
               END IF;

                If MI_RSHV.DIRECCIONRESIDENCIA IS NULL AND MI_RSNOM.DIRECCION IS NOT NULL THEN 
                   MI_CAMPOS := MI_CAMPOS || ',DIRECCION =''' || MI_RSNOM.DIRECCION||''' '; 
               END IF;

                If MI_RSHV.TELEFONORESIDENCIA IS NULL AND MI_RSNOM.TELEFONOS IS NOT NULL THEN 
                   MI_CAMPOS := MI_CAMPOS || ',TELEFONOS =''' || MI_RSNOM.TELEFONOS||''' ';
               END IF;

                If MI_RSHV.ALERGIAS IS NULL AND MI_RSNOM.ALERGIAS IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS ||',ALERGIAS = ''' ||MI_RSNOM.ALERGIAS||''' ';
               END IF;

                If MI_RSHV.MEDICOTRATANTE IS NULL AND  MI_RSNOM.MEDICOTRATANTE IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS ||',MEDICOTRATANTE =''' || MI_RSNOM.MEDICOTRATANTE||''' ';
               END IF;

                IF MI_RSHV.PERSONAS IS NULL AND MI_RSNOM.PERSONASCARGO IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',PERSONAS =''' || MI_RSNOM.PERSONASCARGO ||''' ';
               END IF;

                IF MI_RSHV.ESTADO_CIVIL IS NULL AND MI_RSNOM.ESTADO_CIVIL IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',ESTADO_CIVIL =''' || MI_RSNOM.ESTADO_CIVIL ||''' ';
               END IF;

                IF MI_RSHV.DPTOEXPCEDULA IS NULL AND MI_RSNOM.DEPARTAMENTO_NAC IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',DPTOEXPCEDULA =''' || MI_RSNOM.DEPARTAMENTO_NAC ||''' ';
               END IF;

                IF MI_RSHV.EXPEDIDA IS NULL AND MI_RSNOM.EXPEDIDA IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',EXPEDIDA =''' || MI_RSNOM.EXPEDIDA ||''' ';
               END IF;

                IF MI_RSHV.FECHANCTO IS NULL AND MI_RSNOM.FECHANCTO IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',FECHANCTO = TO_DATE (''' ||TO_CHAR (MI_RSNOM.FECHANCTO,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'')';
               END IF;

                IF MI_RSHV.FECHATERCONTRATO IS NULL AND MI_RSNOM.FECHATERCONTRATO IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',FECHATERCONTRATO = TO_DATE (''' ||TO_CHAR (MI_RSNOM.FECHATERCONTRATO,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'')';
               END IF;

                IF MI_RSHV.PAISRESIDE IS NULL AND MI_RSNOM.PAIS_CED IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',PAISRESIDE =''' || MI_RSNOM.PAIS_CED ||''' ';
               END IF;

                IF MI_RSHV.CIUDAD_CUENTA IS NULL AND MI_RSNOM.CIUDAD_CUENTA IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',CIUDAD_CUENTA =''' || MI_RSNOM.CIUDAD_CUENTA ||''' ';
               END IF;

                IF MI_RSHV.REGIMEN IS NULL AND MI_RSNOM.REGIMEN IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',REGIMEN =''' || MI_RSNOM.REGIMEN ||''' ';
               END IF;

                IF MI_RSHV.DEPENDIENTES384 IS NULL AND MI_RSNOM.DEPENDIENTES384 IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',DEPENDIENTES384 =''' || MI_RSNOM.DEPENDIENTES384 ||''' ';
               END IF;

                IF MI_RSHV.FECHA_DEPENDIENTES384 IS NULL AND MI_RSNOM.FECHA_DEPENDIENTES384 IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',FECHA_DEPENDIENTES384 = TO_DATE (''' || TO_CHAR (MI_RSNOM.FECHA_DEPENDIENTES384,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'')';
               END IF;

                IF MI_RSHV.RETE_MINIMA IS NULL AND MI_RSNOM.RETE_MINIMA IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',RETE_MINIMA =''' || MI_RSNOM.RETE_MINIMA ||''' ';
               END IF;

                IF MI_RSHV.FECHA_RETE_MINIMA IS NULL AND MI_RSNOM.FECHA_RETE_MINIMA IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',FECHA_RETE_MINIMA = TO_DATE (''' ||TO_CHAR (MI_RSNOM.FECHA_RETE_MINIMA,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'')';
               END IF;

                IF MI_RSHV.DECLARANTES384 IS NULL AND MI_RSNOM.DECLARANTES384 IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',DECLARANTES384 =''' || MI_RSNOM.DECLARANTES384 ||''' ';
               END IF;

                IF MI_RSHV.FECHA_DECLARANTES384 IS NULL AND MI_RSNOM.FECHA_DECLARANTES384 IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',FECHA_DECLARANTES384 = TO_DATE (''' ||TO_CHAR (MI_RSNOM.FECHA_DECLARANTES384,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'')';
               END IF;

                IF MI_RSHV.ASALARIADO_NOEMPLEADOS IS NULL AND MI_RSNOM.ASALARIADO_NOEMPLEADOS IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',ASALARIADO_NOEMPLEADOS =''' || MI_RSNOM.ASALARIADO_NOEMPLEADOS ||''' ';
               END IF;

                IF MI_RSHV.TIPO_SALARIO IS NULL AND MI_RSNOM.TIPO_SALARIO IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS ||',TIPO_SALARIO =''' || MI_RSNOM.TIPO_SALARIO ||''' ';
               END IF;

                IF MI_RSHV.AREAMISOADM IS NULL AND MI_RSNOM.AREAMISOADM IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',AREAMISOADM =''' || MI_RSNOM.AREAMISOADM ||''' ';
               END IF;

                IF MI_RSHV.TIPOACTIVIDAD IS NULL AND MI_RSNOM.TIPOACTIVIDAD IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',TIPOACTIVIDAD =''' || MI_RSNOM.TIPOACTIVIDAD ||''' ';
               END IF;

                IF MI_RSHV.SEDE IS NULL AND MI_RSNOM.SEDE IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',SEDE =''' || MI_RSNOM.SEDE ||''' ';
               END IF;

                IF MI_RSHV.NIVELSIIF IS NULL AND MI_RSNOM.NIVELSIIF IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',NIVELSIIF =''' || MI_RSNOM.NIVELSIIF ||''' ';
               END IF;

                IF MI_RSHV.FECHAINGRESO IS NULL AND MI_RSNOM.INGRESO_DISTRITO IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',FECHA_DE_INGRESO = TO_DATE (''' ||TO_CHAR (MI_RSNOM.INGRESO_DISTRITO,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'')';

                ELSIF MI_RSHV.FECHAINGRESO IS NULL AND MI_RSNOM.INGRESODISTRITOREAL IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',FECHA_DE_INGRESO = TO_DATE (''' ||TO_CHAR (MI_RSNOM.INGRESODISTRITOREAL,'DD/MM/YYYY HH24:MI:SS')||''',''DD/MM/YYYY HH24:MI:SS'')';
               END IF;

                IF MI_RSHV.NUMEROPATRONAL IS NULL AND MI_RSNOM.NUMEROPATRONAL IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',NUMEROPATRONAL =''' || MI_RSNOM.NUMEROPATRONAL ||''' ';
               END IF;

                IF MI_RSHV.GRUPOCONTABLE IS NULL AND MI_RSNOM.GRUPOCONTABLE IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',GRUPOCONTABLE =''' || MI_RSNOM.GRUPOCONTABLE ||''' ';
               END IF;

                IF MI_RSHV.EMAIL IS NULL AND MI_RSNOM.EMAIL_PERSONAL IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',EMAIL_PERSONAL =''' || MI_RSNOM.EMAIL_PERSONAL ||''' ';
               END IF;

                IF MI_RSHV.DPTOEXPCEDULA IS NULL AND MI_RSNOM.DEPARTAMENTO_NAC IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',DPTOEXPCEDULA =''' || MI_RSNOM.DEPARTAMENTO_NAC ||''' ';
               END IF;

                IF MI_RSHV.EXPEDIDA IS NULL AND MI_RSNOM.EXPEDIDA IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',EXPEDIDA =''' || MI_RSNOM.EXPEDIDA ||''' ';
               END IF;

                IF MI_RSHV.PAISEXTRANJERO IS NULL AND MI_RSNOM.PAIS_NAC IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',PAISEXTRANJERO =''' || MI_RSNOM.PAIS_NAC ||''' ';
               END IF;

                IF MI_RSHV.BANCO IS NULL AND MI_RSNOM.BANCO IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',BANCO =''' || MI_RSNOM.BANCO ||''' ';
               END IF;

                IF MI_RSHV.CUENTA IS NULL AND MI_RSNOM.CUENTA IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',CUENTA =''' || MI_RSNOM.CUENTA ||''' ';
               END IF;

                IF MI_RSHV.TIPOCUENTA IS NULL AND MI_RSNOM.TIPOCUENTA IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',TIPOCUENTA =''' || MI_RSNOM.TIPOCUENTA ||''' ';
               END IF;

                IF MI_RSHV.ESTRATO IS NULL AND MI_RSNOM.ESTRATO IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',ESTRATO =''' || MI_RSNOM.ESTRATO ||''' ';
               END IF;

                IF MI_RSHV.FONDO_SINDICATO IS NULL AND MI_RSNOM.FONDO_SINDICATO IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS ||',FONDO_SINDICATO =''' || MI_RSNOM.FONDO_SINDICATO ||''' ';
               END IF;

                IF MI_RSHV.NUMLIBMILITAR IS NULL AND MI_RSNOM.LIBRETA_MILITAR IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',LIBRETA_MILITAR =''' || MI_RSNOM.LIBRETA_MILITAR ||''' ';
               END IF;

                IF MI_RSHV.CERTIFICADOJUDICIAL IS NULL AND MI_RSNOM.CERTIFICADOJUDICIAL IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',CERTIFICADOJUDICIAL =''' || MI_RSNOM.CERTIFICADOJUDICIAL ||''' ';
               END IF;

                IF MI_RSHV.PAISRESIDE IS NULL AND MI_RSNOM.PAIS_HAB IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS ||',PAISRESIDE =''' || MI_RSNOM.PAIS_HAB ||''' ';
               END IF;

                IF MI_RSHV.DEPTORESIDE IS NULL AND MI_RSNOM.DEPARTAMENTO_HAB IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',DEPTORESIDE =''' || MI_RSNOM.DEPARTAMENTO_HAB ||''' ';
               END IF;

                IF MI_RSHV.MUNICIPIORESIDE IS NULL AND MI_RSNOM.CIUDAD_HAB IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',MUNICIPIORESIDE =''' || MI_RSNOM.CIUDAD_HAB ||''' ';
               END IF;

                IF MI_RSHV.PROCESORETENCION IS NULL AND MI_RSNOM.PROCESORETENCION IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',PROCESORETENCION =''' || MI_RSNOM.PROCESORETENCION ||''' ';
               END IF;

                IF MI_RSHV.ID_DE_TIPO IS NULL AND MI_RSNOM.ID_DE_TIPO IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',ID_DE_TIPO =''' || MI_RSNOM.ID_DE_TIPO ||''' ';
               END IF;

                IF MI_RSHV.ID_CENTROS_DE_COSTO IS NULL AND MI_RSNOM.ID_CENTRO_DE_COSTO IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',ID_CENTROS_DE_COSTO =''' || MI_RSNOM.ID_CENTRO_DE_COSTO ||''' ';
               END IF;

                IF MI_RSHV.NIT_ESTABLECIMIENTO_DOCENTES IS NULL AND MI_RSNOM.NOMBRES IS NOT NULL THEN
                   MI_CAMPOS := MI_CAMPOS || ',NOMBRES =''' || MI_RSNOM.NOMBRES ||''' ';
               END IF;

     BEGIN
            MI_TABLA:= 'PERSONAL';
            MI_CAMPOS:= LTRIM(MI_CAMPOS||
                        ',PERSONAL.DATE_MODIFIED     = SYSDATE,'||
                        'PERSONAL.MODIFIED_BY        = ''' ||UN_USUARIO ||'''',',');

         MI_CONDICION:= 'PERSONAL.COMPANIA           = ''' || UN_COMPANIA || ''''||
                           'AND PERSONAL.NUMERO_DCTO = ''' || UN_NUMERO_DCTO || '''
                           AND PERSONAL.ESTADO_ACTUAL NOT IN (3)';

        BEGIN
              MI_PCKDATOS:=PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA,
                                              UN_ACCION       => 'M',
                                              UN_CAMPOS       => MI_CAMPOS,
                                              UN_CONDICION    => MI_CONDICION);                                    
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE  PCK_EXCEPCIONES.EXC_HOJAS_VIDA; 
            END;
               EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN                    
                           MI_REEMPLAZOS (0).CLAVE := 'TABLA';
                           MI_REEMPLAZOS (0).VALOR := 'PERSONAL';    
                           MI_REEMPLAZOS (1).CLAVE := 'CAMPOS';                    
                           MI_REEMPLAZOS (1).VALOR :=  MI_VALORES;
                           PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                      UN_TABLAERROR  => 'PERSONAL',
                                                      UN_ERROR_COD   => PCK_ERRORES.ERRR_HOJAS_VIDA_ACT_CONS,                                   
                                                      UN_REEMPLAZOS  => MI_ERROR);
        END;


  END LOOP ;
END LOOP AUT;
END PR_ACT_CAMPOS_NULOS_NU; 


--8
PROCEDURE PR_DETALLE_EVALUACION
/*
    NAME              : PR_DETALLE_EVALUACION
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 16/02/2018
    TIME              : 08:00 AM  
    SOURCE MODULE     : SysmanHv2018.01.07_HV_SST_Manual_SelPersonal_Bienestar
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : REGISTRA LOS DETALLES DE LA EVALUACION 

      @NAME:    registrarDetallesEvaluacion 
      @METHOD:  POST
  */
(
    UN_COMPANIA               IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_EVALUACION             IN  EV_DETALLE_EVALUACION.NUMERO_EVALUACION%TYPE,
    UN_CLASE_EVALUACION       IN  EV_CRITERIO_GRUPO.CLASE_EVALUACION%TYPE,
    UN_CEDULA_EVALUADO        IN  EV_SUBDETALLE_EVALUACION.CEDULA_EVALUADO%TYPE,
    UN_CEDULA_EVALUADOR       IN  EV_SUBDETALLE_EVALUACION.CEDULA_EVALUADOR%TYPE,
    UN_SUCURSAL_EVALUADO      IN  EV_SUBDETALLE_EVALUACION.SUCURSAL_EVALUADO%TYPE,
    UN_SUCURSAL_EVALUADOR     IN  EV_SUBDETALLE_EVALUACION.SUCURSAL_EVALUADOR%TYPE,
    UN_ESCALAFON_EVALUADOR    IN  EV_SUBDETALLE_EVALUACION.ESCALAFON_EVALUADOR%TYPE,
    UN_ESCALAFON_EVALUADO     IN  EV_SUBDETALLE_EVALUACION.ESCALAFON_EVALUADO%TYPE,
    UN_TIPO_EVALUACION        IN  EV_SUBDETALLE_EVALUACION.TIPO_EVALUACION%TYPE,
    UN_CARGO_EVALUADOR        IN  EV_SUBDETALLE_EVALUACION.CARGO_EVALUADOR%TYPE,
    UN_CARGO_EVALUADO         IN  EV_SUBDETALLE_EVALUACION.CARGO_EVALUADO%TYPE,
    UN_CODIGO_EVALUADOR       IN  EV_DETALLE_EVALUACION.CODIGO_EMPLEADO_EVALUADOR%TYPE,
    UN_CODIGO_EVALUADO        IN  EV_DETALLE_EVALUACION.CODIGO_EMPLEADO_EVALUADO%TYPE,
    UN_EVALUADOR_COMISION     IN  EV_DETALLE_EVALUACION.EVALUADOR_COMISION%TYPE,
    UN_USUARIO                IN  PCK_SUBTIPOS.TI_USUARIO
)
AS 
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
  MI_EXISTE         PCK_SUBTIPOS.TI_ENTERO;
  MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGENOEXIS    PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGEUSING      PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE     PCK_SUBTIPOS.TI_MERGEENLACE;
BEGIN

   SELECT COUNT(1)
            INTO MI_EXISTE
      FROM EV_DETALLE_EVALUACION
      WHERE COMPANIA           = UN_COMPANIA
        AND NUMERO_EVALUACION  = UN_EVALUACION
        AND CLASE_EVALUACION   = UN_CLASE_EVALUACION
        AND TIPO_EVALUACION    = UN_TIPO_EVALUACION
        AND CEDULA_EVALUADO    = UN_CEDULA_EVALUADO
        AND SUCURSAL_EVALUADO  = UN_SUCURSAL_EVALUADO
        AND CEDULA_EVALUADOR   = UN_CEDULA_EVALUADOR
        AND SUCURSAL_EVALUADOR = UN_SUCURSAL_EVALUADOR;

    IF MI_EXISTE = 0 THEN
       MI_CAMPOS :='COMPANIA,                  
                NUMERO_EVALUACION,                  
                CLASE_EVALUACION,                  
                TIPO_EVALUACION,                  
                CEDULA_EVALUADO,                  
                SUCURSAL_EVALUADO,                  
                CEDULA_EVALUADOR,                  
                SUCURSAL_EVALUADOR,                  
                CARGO_EVALUADOR,                  
                CARGO_EVALUADO,                  
                FECHA,                  
                HORA,                  
                CODIGO_EMPLEADO_EVALUADO,                  
                CODIGO_EMPLEADO_EVALUADOR,                  
                ESCALAFON_EVALUADOR,                  
                ESCALAFON_EVALUADO,                  
                CREATED_BY,                  
                DATE_CREATED,                  
                OBSERVACIONES,                  
                EVALUADOR_COMISION';

   MI_VALORES:=''''||UN_COMPANIA||''',
                '|| UN_EVALUACION||',
                '|| UN_CLASE_EVALUACION ||',
                '''||UN_TIPO_EVALUACION||''',
                '''|| UN_CEDULA_EVALUADO||''',
                '''|| UN_SUCURSAL_EVALUADO||''',
                '''|| UN_CEDULA_EVALUADOR||''',
                '''|| UN_SUCURSAL_EVALUADOR||''',
                '''|| UN_CARGO_EVALUADOR || ''',
                '''|| UN_CARGO_EVALUADO || ''',
                SYSDATE,
                TO_DATE('''|| '01/01/1970 ' ||TO_CHAR(SYSDATE, 'HH24:MI:SS')||''', ''DD/MM/YYYY HH24:MI:SS''),
                '''|| UN_CODIGO_EVALUADO||''',
                '''|| UN_CODIGO_EVALUADOR||''',
                '''|| UN_ESCALAFON_EVALUADOR||''',
                '''|| UN_ESCALAFON_EVALUADO||''',
                '''|| UN_USUARIO ||''',
                SYSDATE,
                ''.'',
                '''||UN_EVALUADOR_COMISION||'''
                ';
      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => 'EV_DETALLE_EVALUACION', 
                                                  UN_ACCION   => 'I', 
                                                  UN_CAMPOS   => MI_CAMPOS, 
                                                  UN_VALORES  => MI_VALORES);
        EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
        END;
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE,  
                                    UN_ERROR_COD => PCK_ERRORES.ERR_HV_INSDETALLEEVALUACION );
      END;      
    END IF;

    FOR RS IN (SELECT DISTINCT EV_CRITERIO_GRUPO.COMPANIA,
                               UN_EVALUACION EVALUACION,
                               UN_CARGO_EVALUADOR CARGO_EVALUADOR,
                               UN_CARGO_EVALUADO CARGO_EVALUADO,
                               EV_CRITERIOS_EVALUACION.CODIGO CRITERIO_EVALUADO,
                               EV_CRITERIO_GRUPO.CLASE_EVALUACION  CLASE_EVALUACION,
                               UN_TIPO_EVALUACION TIPO_EVALUACION,
                               0 VALOR_ASIGNADO,
                               UN_CEDULA_EVALUADO CEDULA_EVALUADO,
                               UN_SUCURSAL_EVALUADO SUCURSAL_EVALUADO,
                               UN_CEDULA_EVALUADOR CEDULA_EVALUADOR,
                               UN_SUCURSAL_EVALUADOR SUCURSAL_EVALUADOR,
                               EV_CRITERIO_GRUPO.GRUPO,
                               EV_CRITERIOS_EVALUACION.MOVIMIENTO,
                               EV_CRITERIOS_EVALUACION.PUNTAJE,
                               EV_CRITERIOS_EVALUACION.IND_TEXTO,
                               '' TEXTO,
                               UN_ESCALAFON_EVALUADOR ESCALAFON_EVALUADOR,
                               UN_ESCALAFON_EVALUADO  ESCALAFON_EVALUADO
              FROM  EV_CRITERIOS_EVALUACION
                INNER JOIN EV_CRITERIO_GRUPO  
                  ON  EV_CRITERIOS_EVALUACION.COMPANIA          = EV_CRITERIO_GRUPO.COMPANIA 
                  AND  SUBSTR(EV_CRITERIOS_EVALUACION.CODIGO,1,LENGTH(EV_CRITERIO_GRUPO.CODIGO_CRITERIO)) = EV_CRITERIO_GRUPO.CODIGO_CRITERIO
                  AND EV_CRITERIOS_EVALUACION.CLASE_EVALUACION  = EV_CRITERIO_GRUPO.CLASE_EVALUACION         
                INNER JOIN EV_EVALUACIONES 
                  ON  EV_CRITERIO_GRUPO.GRUPO    = EV_EVALUACIONES.GRUPO_APLICAR
                  AND EV_CRITERIO_GRUPO.COMPANIA = EV_EVALUACIONES.COMPANIA
                  AND EV_CRITERIO_GRUPO.CLASE_EVALUACION = EV_EVALUACIONES.CLASE_EVALUACION 
                WHERE  EV_CRITERIO_GRUPO.COMPANIA         =  UN_COMPANIA
                  AND  EV_CRITERIO_GRUPO.CLASE_EVALUACION =  UN_CLASE_EVALUACION
                  AND  EV_EVALUACIONES.CONSECUTIVO        =  UN_EVALUACION)
    LOOP
        MI_EXISTE :=0;
        SELECT COUNT(1)
                INTO MI_EXISTE
          FROM EV_SUBDETALLE_EVALUACION
          WHERE COMPANIA           = UN_COMPANIA
            AND EVALUACION         = UN_EVALUACION
            AND CLASE_EVALUACION   = UN_CLASE_EVALUACION
            AND CEDULA_EVALUADO    = UN_CEDULA_EVALUADO
            AND SUCURSAL_EVALUADO  = UN_SUCURSAL_EVALUADO
            AND CEDULA_EVALUADOR   = UN_CEDULA_EVALUADOR
            AND SUCURSAL_EVALUADOR = UN_SUCURSAL_EVALUADOR
            AND CRITERIO_EVALUADO  = RS.CRITERIO_EVALUADO;

        IF MI_EXISTE = 0 THEN
           MI_CAMPOS :=' COMPANIA,
                        EVALUACION,
                        CARGO_EVALUADOR,
                        CARGO_EVALUADO,
                        CRITERIO_EVALUADO,
                        CLASE_EVALUACION,
                        TIPO_EVALUACION,
                        VALOR_ASIGNADO,
                        FECHA,
                        HORA,
                        CEDULA_EVALUADO,
                        SUCURSAL_EVALUADO,
                        CEDULA_EVALUADOR,
                        SUCURSAL_EVALUADOR,
                        GRUPO,
                        MOVIMIENTO,
                        PUNTAJE,
                        IND_TEXTO,
                        TEXTO,
                        CREATED_BY,
                        DATE_CREATED,
                        ESCALAFON_EVALUADOR,
                        ESCALAFON_EVALUADO';

           MI_VALORES:=''''||RS.COMPANIA||''',
                        '  || RS.EVALUACION||',
                        '''||RS.CARGO_EVALUADOR||''',
                        '''||RS.CARGO_EVALUADO||''',
                        '''||RS.CRITERIO_EVALUADO||''',
                        '  ||RS.CLASE_EVALUACION||',
                        '''||RS.TIPO_EVALUACION||''',
                        '  ||RS.VALOR_ASIGNADO||',
                        SYSDATE,
                        TO_DATE('''|| '01/01/1970 ' ||TO_CHAR(SYSDATE, 'HH24:MI:SS')||''', ''DD/MM/YYYY HH24:MI:SS''),
                        '''|| RS.CEDULA_EVALUADO||''',
                        '''|| RS.SUCURSAL_EVALUADO||''',
                        '''|| RS.CEDULA_EVALUADOR||''',
                        '''|| RS.SUCURSAL_EVALUADOR||''',
                        '  || RS.GRUPO ||',
                        '  || RS.MOVIMIENTO ||',
                        '  || RS.PUNTAJE||',
                        '  || RS.IND_TEXTO||',
                        '''|| RS.TEXTO||''',
                        '''|| UN_USUARIO||''',
                        SYSDATE,
                        '''||RS.ESCALAFON_EVALUADOR||''',
                        '''||RS.ESCALAFON_EVALUADO||'''
                        ';
              BEGIN
                BEGIN
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => 'EV_SUBDETALLE_EVALUACION', 
                                                          UN_ACCION   => 'I', 
                                                          UN_CAMPOS   => MI_CAMPOS, 
                                                          UN_VALORES  => MI_VALORES);
                EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
                END;
              EXCEPTION
              WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
                PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE,  
                                            UN_ERROR_COD => PCK_ERRORES.ERR_HV_INSSUBDETALLEEVALUACION );
              END;      
        ELSE 

            MI_CAMPOS:= 'GRUPO          = '||RS.GRUPO||',
                         MOVIMIENTO     = '||RS.MOVIMIENTO||',
                         PUNTAJE        = '||RS.PUNTAJE||',
                         IND_TEXTO      = '||RS.IND_TEXTO||',
                         DATE_MODIFIED  = SYSDATE,
                         MODIFIED_BY    = '''||UN_USUARIO ||'''';

             MI_CONDICION:= ' COMPANIA               = '''||UN_COMPANIA||'''
                              AND EVALUACION         = '  ||UN_EVALUACION||'
                              AND CLASE_EVALUACION   = '  ||UN_CLASE_EVALUACION||'
                              AND CEDULA_EVALUADO    = '''||UN_CEDULA_EVALUADO||'''
                              AND SUCURSAL_EVALUADO  = '''||UN_SUCURSAL_EVALUADO||'''
                              AND CEDULA_EVALUADOR   = '''||UN_CEDULA_EVALUADOR||'''
                              AND SUCURSAL_EVALUADOR = '''||UN_SUCURSAL_EVALUADOR||'''
                              AND CRITERIO_EVALUADO  = '''||RS.CRITERIO_EVALUADO||'''';
             BEGIN
            BEGIN
                  PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME ( UN_TABLA        => 'EV_SUBDETALLE_EVALUACION',
                                                        UN_ACCION       => 'M',
                                                        UN_CAMPOS       => MI_CAMPOS,
                                                        UN_CONDICION    => MI_CONDICION);                                    
                    EXCEPTION
                         WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE  PCK_EXCEPCIONES.EXC_HOJAS_VIDA; 
                END;
                   EXCEPTION
                         WHEN  PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN                    
                               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                          UN_TABLAERROR  => 'EV_SUBDETALLE_EVALUACION',
                                                          UN_ERROR_COD   => PCK_ERRORES.ERRR_HOJAS_VIDA_ACT_CONS);
            END;
        END IF;
    END LOOP;

             MI_CAMPOS:= 'REG_DETALLE    = -1,
                          DATE_MODIFIED  = SYSDATE,
                          MODIFIED_BY    = '''||UN_USUARIO ||'''';

             MI_CONDICION:= ' COMPANIA               = '''||UN_COMPANIA||'''
                              AND NUMERO_EVALUACION  = '  ||UN_EVALUACION||'
                              AND CLASE_EVALUACION   = '  ||UN_CLASE_EVALUACION||'
                              AND TIPO_EVALUACION  = '''||UN_TIPO_EVALUACION||'''
                              AND CEDULA_EVALUADO    = '''||UN_CEDULA_EVALUADO||'''
                              AND SUCURSAL_EVALUADO  = '''||UN_SUCURSAL_EVALUADO||'''
                              AND CEDULA_EVALUADOR   = '''||UN_CEDULA_EVALUADOR||'''
                              AND SUCURSAL_EVALUADOR = '''||UN_SUCURSAL_EVALUADOR||'''';
             BEGIN
            BEGIN
                  PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME ( UN_TABLA        => 'EV_DETALLE_EVALUACION',
                                                        UN_ACCION       => 'M',
                                                        UN_CAMPOS       => MI_CAMPOS,
                                                        UN_CONDICION    => MI_CONDICION);                                    
                    EXCEPTION
                         WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE  PCK_EXCEPCIONES.EXC_HOJAS_VIDA; 
                END;
                   EXCEPTION
                         WHEN  PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN                    
                               PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                          UN_TABLAERROR  => 'EV_DETALLE_EVALUACION',
                                                          UN_ERROR_COD   => PCK_ERRORES.ERRR_HOJAS_VIDA_ACT_CONS);
            END;
END PR_DETALLE_EVALUACION;

PROCEDURE PR_ACTUALIZAR_DETA_PROFESIONES
(
  /*
    NAME              : PR_ACTUALIZAR_DETA_PROFESIONES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL VENEGAS RODRIGUEZ
    DATE MIGRADOR     : 16/02/2018
    TIME              : 
    SOURCE MODULE     : SysmanHv2018.01.04HojasdeVida 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  ActualizarDetallesProfesiones
    @METHOD:  GET
  */
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_NUMERO_DCTO      IN VARCHAR2,
  UN_SUCURSAL         IN VARCHAR2,
  UN_ID_DE_EMPLEADO   IN NUMBER,
  UN_USUARIO          IN VARCHAR2
  ) 
  AS
    MI_TABLA        VARCHAR2( 32000);
    MI_CAMPOS       VARCHAR2(32000);
    MI_VALORES      VARCHAR2(32000);
    MI_CANTIDAD     NUMBER;
    MI_USUARIO   VARCHAR2(255 CHAR);

  BEGIN

    MI_TABLA := 'DETALLE_PROFESIONES';

    FOR MI_RS IN( 
      SELECT  N.COMPANIA,
            N.NUMERO_DCTO,
            N.TITULOOBTENIDO,
            P.TIPO,
            N.ESTABLECIMIENTO,
            P.NOMBRE_PROFESION,
            P.DESCRIPCIONPROF
      FROM NAT_FORMACION_ACADEMICA N  
        INNER JOIN PROFESIONES P 
          ON N.TITULOOBTENIDO = P.CODIGOPROF 
      WHERE N.COMPANIA    = UN_COMPANIA
        AND N.NUMERO_DCTO = UN_NUMERO_DCTO
        AND N.SUCURSAL    = UN_SUCURSAL) 
    LOOP
      MI_CANTIDAD :=0;

       SELECT COUNT(*) INTO MI_CANTIDAD
       FROM DETALLE_PROFESIONES
       WHERE COMPANIA      = MI_RS.COMPANIA
        AND CODIGOPROF     = MI_RS.TITULOOBTENIDO
        AND ID_DE_EMPLEADO = UN_ID_DE_EMPLEADO;

    IF MI_CANTIDAD <= 0 THEN

  MI_CAMPOS := 'COMPANIA, 
                  CODIGOPROF, 
                  ID_DE_EMPLEADO, 
                  NUMERO_DCTO, 
                  SUCURSAL, 
                  TIPO, 
                  ESTABLECIMIENTO, 
                  NOMBRE_PROFESION,
                  DESCRIPCIONPROF, 
                  CREATED_BY, 
                  DATE_CREATED';

    MI_VALORES := '     ''' || MI_RS.COMPANIA       || ''' ,
                        ''' || MI_RS.TITULOOBTENIDO || ''' ,
                          ' || UN_ID_DE_EMPLEADO    || ',
                        ''' || UN_NUMERO_DCTO       || ''',
                        ''' || UN_SUCURSAL          || ''', 
                        ''' || MI_RS.TIPO           || ''', 
                        ''' || MI_RS.ESTABLECIMIENTO    || ''' ,
                        ''' || MI_RS.NOMBRE_PROFESION    || ''' ,
                        ''' || MI_RS.DESCRIPCIONPROF     || ''' ,
                        ''' ||  UN_USUARIO     ||''', 
                        SYSDATE  ';   

    BEGIN
            BEGIN

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA  => MI_TABLA, 
                                                UN_ACCION  => 'I', 
                                                UN_CAMPOS  => MI_CAMPOS, 
                                                UN_VALORES => MI_VALORES); 
           EXCEPTION
                  WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
        END;
   EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
          PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE,  
            UN_ERROR_COD => PCK_ERRORES.ERR_HV_ACTUALIIZADETAPREFE );
    END; 
      END IF;


    END LOOP;


END PR_ACTUALIZAR_DETA_PROFESIONES;



PROCEDURE PR_ACTUALIZAR_PUNTAJE
/*
    NAME              : PR_ACTUALIZAR_PUNTAJE
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 26/02/2018
    TIME              : 08:00 AM  
    SOURCE MODULE     : SysmanHv2018.01.07_HV_SST_Manual_SelPersonal_Bienestar
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : ACTUALIZA LOS PUNTAJES PARA LOS CRITERIOS DE EVALUACION 

      @NAME:    actualizarPuntaje 
      @METHOD:  PUT
  */
(
    UN_COMPANIA               IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_EVALUACION             IN  EV_DETALLE_EVALUACION.NUMERO_EVALUACION%TYPE,
    UN_CLASE_EVALUACION       IN  EV_CRITERIO_GRUPO.CLASE_EVALUACION%TYPE,
    UN_TIPO_EVALUACION        IN  EV_SUBDETALLE_EVALUACION.TIPO_EVALUACION%TYPE,
    UN_CRITERIO_EVALUADO      IN  EV_SUBDETALLE_EVALUACION.CRITERIO_EVALUADO%TYPE,
    UN_CRITERIO_SELECCIONADO  IN  EV_SUBDETALLE_EVALUACION.CRITERIO_EVALUADO%TYPE,
    UN_CEDULA_EVALUADO        IN  EV_SUBDETALLE_EVALUACION.CEDULA_EVALUADO%TYPE,
    UN_CEDULA_EVALUADOR       IN  EV_SUBDETALLE_EVALUACION.CEDULA_EVALUADOR%TYPE,
    UN_SUCURSAL_EVALUADO      IN  EV_SUBDETALLE_EVALUACION.SUCURSAL_EVALUADO%TYPE,
    UN_SUCURSAL_EVALUADOR     IN  EV_SUBDETALLE_EVALUACION.SUCURSAL_EVALUADOR%TYPE,
    UN_ESCOMPROMISO           IN  PCK_SUBTIPOS.TI_LOGICO,  
    UN_USUARIO                IN  PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_CONT               PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
BEGIN

 IF UN_ESCOMPROMISO = 0 THEN


  MI_CAMPOS  := 'VALOR_ASIGNADO  = 0, 
                 DATE_MODIFIED   = SYSDATE,
                 MODIFIED_BY     = '''||UN_USUARIO||'''';
  MI_CONDICION := '     EV_SUBDETALLE_EVALUACION.COMPANIA                                                   = ''' ||  UN_COMPANIA           ||'''
                    AND EV_SUBDETALLE_EVALUACION.EVALUACION                                                 = '   ||  UN_EVALUACION         ||'
                    AND EV_SUBDETALLE_EVALUACION.CEDULA_EVALUADO                                            = ''' ||  UN_CEDULA_EVALUADO    ||'''
                    AND EV_SUBDETALLE_EVALUACION.SUCURSAL_EVALUADO                                          = ''' ||  UN_SUCURSAL_EVALUADO  ||'''
                    AND EV_SUBDETALLE_EVALUACION.CEDULA_EVALUADOR                                           = ''' ||  UN_CEDULA_EVALUADOR   ||'''
                    AND EV_SUBDETALLE_EVALUACION.SUCURSAL_EVALUADOR                                         = ''' ||  UN_SUCURSAL_EVALUADOR ||'''
                    AND EV_SUBDETALLE_EVALUACION.CLASE_EVALUACION                                           = '   ||  UN_CLASE_EVALUACION   ||'
                    AND EV_SUBDETALLE_EVALUACION.TIPO_EVALUACION                                            = ''' ||  UN_TIPO_EVALUACION    ||'''
                    AND SUBSTR(EV_SUBDETALLE_EVALUACION.CRITERIO_EVALUADO,1,LENGTH('''||UN_CRITERIO_EVALUADO||''')) = '''||UN_CRITERIO_EVALUADO||'''
                    AND EV_SUBDETALLE_EVALUACION.CRITERIO_EVALUADO NOT                                     IN ''' ||  UN_CRITERIO_EVALUADO    ||'''
                    AND EV_SUBDETALLE_EVALUACION.CRITERIO_EVALUADO NOT                                     IN ''' ||  UN_CRITERIO_SELECCIONADO||'''
                    AND '''||UN_CRITERIO_EVALUADO||'''   IN
                                                  (SELECT CODIGO
                                                  FROM EV_CRITERIOS_EVALUACION
                                                  WHERE EV_CRITERIOS_EVALUACION.COMPANIA              = ''' ||  UN_COMPANIA         ||'''
                                                  AND EV_CRITERIOS_EVALUACION.CLASE_EVALUACION        = '   ||  UN_CLASE_EVALUACION ||'
                                                  AND EV_CRITERIOS_EVALUACION.CODIGO                  = ''' ||  UN_CRITERIO_EVALUADO||'''
                                                  AND EV_CRITERIOS_EVALUACION.IND_SELECCION_MULTIPLE IN (0)
                                                  )';  
    BEGIN 
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'EV_SUBDETALLE_EVALUACION', 
                                               UN_ACCION    =>  'M', 
                                               UN_CAMPOS    =>  MI_CAMPOS,
                                               UN_CONDICION =>  MI_CONDICION );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN    
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_HV_ACTVALORASIGSUBDETALLE
                    );
    END;
END IF;
  --ACTUALIZACION PUNTAJE

   --27/06/2018  @jreina  -- se cambio en la sentencia SUBSTR(PADRE.CRITERIO_EVALUADO) A SUBSTR(CRITERIO_EVALUADO) VARIABLE MI_CONT
   MI_CONT :='(SELECT SUM(EV_SUBDETALLE_EVALUACION.PUNTAJE)
              FROM EV_SUBDETALLE_EVALUACION
                WHERE EV_SUBDETALLE_EVALUACION.COMPANIA                                 = ''' || UN_COMPANIA           ||'''
                  AND EV_SUBDETALLE_EVALUACION.EVALUACION                               = '   || UN_EVALUACION         ||'
                  AND EV_SUBDETALLE_EVALUACION.CEDULA_EVALUADO                          = ''' || UN_CEDULA_EVALUADO    ||'''
                  AND EV_SUBDETALLE_EVALUACION.SUCURSAL_EVALUADO                        = ''' || UN_SUCURSAL_EVALUADO  ||'''
                  AND EV_SUBDETALLE_EVALUACION.CEDULA_EVALUADOR                         = ''' || UN_CEDULA_EVALUADOR   ||'''
                  AND EV_SUBDETALLE_EVALUACION.SUCURSAL_EVALUADOR                       = ''' || UN_SUCURSAL_EVALUADOR ||'''
                  AND EV_SUBDETALLE_EVALUACION.CLASE_EVALUACION                         = '   || UN_CLASE_EVALUACION   ||'
                  AND EV_SUBDETALLE_EVALUACION.TIPO_EVALUACION                          = ''' || UN_TIPO_EVALUACION    ||'''
                  AND SUBSTR(CRITERIO_EVALUADO,1,LENGTH(PADRE.CRITERIO_EVALUADO)) = PADRE.CRITERIO_EVALUADO
                  AND EV_SUBDETALLE_EVALUACION.CRITERIO_EVALUADO NOT                   IN PADRE.CRITERIO_EVALUADO
                  AND NVL(EV_SUBDETALLE_EVALUACION.VALOR_ASIGNADO,0) NOT               IN (0))';

   MI_CAMPOS  := 'PUNTAJE       = '|| MI_CONT||', 
                  DATE_MODIFIED = SYSDATE,
                  MODIFIED_BY   = '''||UN_USUARIO||'''';
   MI_CONDICION := ' PADRE.COMPANIA               = '''  ||  UN_COMPANIA           ||'''
                    AND PADRE.EVALUACION         = '    ||  UN_EVALUACION         ||'
                    AND PADRE.CEDULA_EVALUADO    = '''  ||  UN_CEDULA_EVALUADO    ||'''
                    AND PADRE.SUCURSAL_EVALUADO  = '''  ||  UN_SUCURSAL_EVALUADO  ||'''
                    AND PADRE.CEDULA_EVALUADOR   = '''  ||  UN_CEDULA_EVALUADOR   ||'''
                    AND PADRE.SUCURSAL_EVALUADOR = '''  ||  UN_SUCURSAL_EVALUADOR ||'''
                    AND PADRE.CLASE_EVALUACION   = '    ||  UN_CLASE_EVALUACION   ||'
                    AND PADRE.TIPO_EVALUACION    = '''  ||  UN_TIPO_EVALUACION    ||'''
                    AND PADRE.CRITERIO_EVALUADO  = SUBSTR('''||UN_CRITERIO_EVALUADO||''',1,LENGTH(PADRE.CRITERIO_EVALUADO))';  
    BEGIN 
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'EV_SUBDETALLE_EVALUACION PADRE', 
                                               UN_ACCION    =>  'M', 
                                               UN_CAMPOS    =>  MI_CAMPOS,
                                               UN_CONDICION =>  MI_CONDICION );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN    
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_HV_ACTPUNTAJESUBDETALLE
                    );
    END;
END PR_ACTUALIZAR_PUNTAJE;

PROCEDURE PR_ACT_INSCR_ACTIV
/*
    NAME              : PR_ACT_INSCR_ACTIV
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : DANIEL FERNANDO NIÃ‘O CARO
    DATE MIGRADOR     : 28/02/2018
    TIME              : 08:00 AM  
    SOURCE MODULE     : Hojas de Vida (21) - SysmanHv2018.01.02_ManualFunciones.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : INSERTA LOS FAMILIARES CON EL INDICADOR ACTIVO DE BENEFICIARIOS A LA TABLA NAT_ACTIVIDADESINSCRITOS.
    PARAMETERS        : UN_COMPANIA      => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
    PARAMETERS        : UN_TIPO_EV       => VARIABLE QUE INDICA EL TIPO DE ACTIVIDAD (SALUD,DEPORTIVAS,CULTURALES,RECREATIVAS).
    PARAMETERS        : UN_EVENTO        => EVENTO RELACIONADO CON EL TIPO DE EVENTO.
    PARAMETERS        : UN_NUMERO_DCTO   => NUMERO_DCTO NUMERO DE DOCUMENTO DEL EMPLEADO AL QUE ESTÃ¿ RELACIONADO EL BENEFICIARIO.                               

      @NAME:    actualizaractividadesinscritos 
      @METHOD:  GET
  */
  (   
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_EVENTO       IN VARCHAR2,
  UN_TIPO_EV      IN VARCHAR2,
  UN_FECHA_EV     IN DATE,
  UN_NUMERO_DCTO  IN PCK_SUBTIPOS.TI_TERCERO,
  UN_SUCURSAL     IN PCK_SUBTIPOS.TI_SUCURSAL,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
)
AS  
  MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR   PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_STRSQL     PCK_SUBTIPOS.TI_STRSQL;
  MI_RTA        PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_CONTEO     NUMBER(20,0);
BEGIN  

/*<<ELIMINAR_NO_INSCRITOS>>
     FOR MI_RS IN (SELECT F.COMPANIA,
        F.DCTO_IDENTIDAD,
        F.IDENTIFICACION,
        F.SUCURSAL,
        F.DCTO_EMPLEADO,
        F.SUCURSAL_EMPLEADO,
        F.NOMBRE,
        F.INSCRIBIR,
        F.PARENTESCO,
        P.DESCRIPCION ,
        TD.DESCRIPCION DOCUMENTO
      FROM FAMILIARES F
      LEFT JOIN TIPOS_DOCUMENTOS TD
      ON F.COMPANIA        = TD.COMPANIA
      AND F.DCTO_IDENTIDAD = TD.DCTO_IDENTIDAD
      INNER JOIN PARENTESCO P
      ON F.COMPANIA           = P.COMPANIA
      AND F.PARENTESCO        = P.PARENTESCO
      WHERE F.COMPANIA        = UN_COMPANIA
      AND F.DCTO_EMPLEADO     = UN_NUMERO_DCTO
      AND F.SUCURSAL_EMPLEADO = UN_SUCURSAL
      AND F.INSCRIBIR  IN (0))LOOP


              MI_CONDICION  := 'COMPANIA      = '''||MI_RS.COMPANIA||'''
                            AND IDEVENTO      = '''||UN_EVENTO||''' 
                            AND TIPOEVENTO    = '''||UN_TIPO_EV||''' 
                            AND NUMERO_DCTO   = '''||MI_RS.IDENTIFICACION||'''
                            AND SUCURSAL      = '''||MI_RS.SUCURSAL||''' ';

              PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME (  UN_TABLA     => 'NAT_ACTIVIDADESINSCRITOS',
                                                     UN_ACCION    => 'E',
                                                   UN_CONDICION => MI_CONDICION);  

      END LOOP ELIMINAR_NO_INSCRITOS;*/


 <<INSERTAR_INSCRITOS>>
     FOR MI_RS IN (SELECT F.COMPANIA,
        F.DCTO_IDENTIDAD,
        F.IDENTIFICACION,
        F.SUCURSAL,
        F.DCTO_EMPLEADO,
        F.SUCURSAL_EMPLEADO,
        F.NOMBRE,
        F.INSCRIBIR,
        F.PARENTESCO,
        P.DESCRIPCION ,
        TD.DESCRIPCION DOCUMENTO
      FROM FAMILIARES F
      LEFT JOIN TIPOS_DOCUMENTOS TD
      ON F.COMPANIA        = TD.COMPANIA
      AND F.DCTO_IDENTIDAD = TD.DCTO_IDENTIDAD
      INNER JOIN PARENTESCO P
      ON F.COMPANIA           = P.COMPANIA
      AND F.PARENTESCO        = P.PARENTESCO
      WHERE F.COMPANIA        = UN_COMPANIA
      AND F.DCTO_EMPLEADO     = UN_NUMERO_DCTO
      AND F.SUCURSAL_EMPLEADO = UN_SUCURSAL
      AND F.INSCRIBIR NOT IN (0))LOOP

 --REVISAR QUE NO EXISTA EL REGISTRO

    SELECT COUNT(*)
     INTO MI_CONTEO
    FROM NAT_ACTIVIDADESINSCRITOS
                  WHERE COMPANIA      = MI_RS.COMPANIA
                    AND IDEVENTO      = UN_EVENTO
                    AND TIPOEVENTO    = UN_TIPO_EV
                    AND NUMERO_DCTO   = MI_RS.IDENTIFICACION
                    AND SUCURSAL      = MI_RS.SUCURSAL;

    IF MI_CONTEO IN (0) THEN                    



  BEGIN
    BEGIN
      MI_CAMPOS := 'COMPANIA,
                    IDEVENTO,
                    TIPOEVENTO,  
                    FECHAINICIAL,
                    NUMERO_DCTO,
                    SUCURSAL,
                    ASISTIO,
                    BENEFICIARIO,
                    CREATED_BY,
                    DATE_CREATED
                    ';
    MI_VALORES := 'SELECT 
                      COMPANIA,
                      '''||UN_EVENTO||'''  IDEVENTO,
                      '''||UN_TIPO_EV||'''  TIPOEVENTO,      
                      '''||UN_FECHA_EV||''' FECHAINICIAL,
                      IDENTIFICACION NUMERO_DCTO,
                      SUCURSAL,
                      0 ASISTIO,
                      -1 BENEFICIARIO,
                      '''||UN_USUARIO||''' CREATED_BY,
                      SYSDATE DATE_CREATED
                  FROM FAMILIARES 
                    WHERE   COMPANIA      = '''||UN_COMPANIA||'''
                    AND IDENTIFICACION     = '''||MI_RS.IDENTIFICACION||'''
                    AND SUCURSAL_EMPLEADO = '''||MI_RS.SUCURSAL||'''
                    AND INSCRIBIR     NOT IN(0)
                          ';

          MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA => 'NAT_ACTIVIDADESINSCRITOS',
                                                  UN_ACCION  => 'IS',
                                                  UN_CAMPOS  => MI_CAMPOS, 
                                                  UN_VALORES => MI_VALORES
                                                ); 


        EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        MI_MSGERROR(0).CLAVE:= 'NUMERO_DCTO';
                        MI_MSGERROR(0).VALOR:= UN_NUMERO_DCTO;
          RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
        END;

      EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE,  
                                        UN_ERROR_COD => PCK_ERRORES.ERR_HV_ACTBENEFICIARIO ,
                                        UN_REEMPLAZOS => MI_MSGERROR );
      END;  
    END IF;  
  END LOOP INSERTAR_INSCRITOS;



  <<ACTUALIZAR_INDICADOR>>

  FOR MI_RS IN (SELECT F.COMPANIA,
        F.DCTO_IDENTIDAD,
        F.IDENTIFICACION,
        F.SUCURSAL,
        F.DCTO_EMPLEADO,
        F.SUCURSAL_EMPLEADO,
        F.NOMBRE,
        F.INSCRIBIR,
        F.PARENTESCO,
        P.DESCRIPCION ,
        TD.DESCRIPCION DOCUMENTO
      FROM FAMILIARES F
      LEFT JOIN TIPOS_DOCUMENTOS TD
      ON F.COMPANIA        = TD.COMPANIA
      AND F.DCTO_IDENTIDAD = TD.DCTO_IDENTIDAD
      INNER JOIN PARENTESCO P
      ON F.COMPANIA           = P.COMPANIA
      AND F.PARENTESCO        = P.PARENTESCO
      WHERE F.COMPANIA        = UN_COMPANIA
      AND F.DCTO_EMPLEADO     = UN_NUMERO_DCTO
      AND F.SUCURSAL_EMPLEADO = UN_SUCURSAL)LOOP

      MI_CAMPOS := 'INSCRIBIR = 0,
                    MODIFIED_BY = '''||UN_USUARIO||''',
                    DATE_MODIFIED = SYSDATE';

      MI_CONDICION := 'COMPANIA             = '''||UN_COMPANIA||'''
                      AND DCTO_EMPLEADO     = '''||UN_NUMERO_DCTO||'''
                      AND SUCURSAL_EMPLEADO = '''||UN_SUCURSAL||'''
                      AND IDENTIFICACION    = '''||MI_RS.IDENTIFICACION||'''
                      AND SUCURSAL          = '''||MI_RS.SUCURSAL||''' ';                    

    BEGIN
     BEGIN 
               MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'FAMILIARES',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS, 
                                            UN_CONDICION => MI_CONDICION
                                                ); 


        EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        MI_MSGERROR(0).CLAVE:= 'IDENTIFICACION';
                        MI_MSGERROR(0).VALOR:= MI_RS.IDENTIFICACION;
          RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
        END;

      EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
            MI_MSGERROR(1).CLAVE := 'CEDULA';
            MI_MSGERROR(1).VALOR := UN_NUMERO_DCTO;
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE,  
                                        UN_ERROR_COD => PCK_ERRORES.ERR_HV_ACTINSCRBENEFICIARIO ,
                                        UN_REEMPLAZOS => MI_MSGERROR );
      END;  

  END LOOP ACTUALIZAR_INDICADOR;

END PR_ACT_INSCR_ACTIV;

--11

PROCEDURE PR_ACTEXPLABORALPERSONALES
/*
    NAME              : PR_ACTEXPLABORALPERSONALES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 28/03/2018
    TIME              : 09:45 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : Procedimiento que actualiza la experiencia laboral de NAT_DATOS_PERSONALES
                        dependiendo si es experiencia Publica,Provada o Independiente

    PARAMETERS        :                         

    @NAME:  actualizarExperienciaLaboral
    @METHOD:  POST                         
  */
  (
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_NUMERO_DCTO     IN NAT_DATOS_PERSONALES.NUMERO_DCTO%TYPE, 
  UN_SUCURSAL        IN NAT_DATOS_PERSONALES.SUCURSAL%TYPE, 
  UN_CLASE           IN NAT_EXPERIENCIA_LABORAL.CLASE%TYPE, 
  UN_ANO             IN NAT_EXPERIENCIA_LABORAL.ANOSERVICIO%TYPE, 
  UN_MES             IN NAT_EXPERIENCIA_LABORAL.MESESERVICIO%TYPE,
  UN_DIA             IN NAT_EXPERIENCIA_LABORAL.DIASERVICIO%TYPE,
  UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
  )
AS
  MI_CANTIDAD             PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_TOTAL                VI_DETALLE_VIATICOS.TOTAL%TYPE;
  MI_RSVIATICO            SYS_REFCURSOR;

BEGIN   

  IF UN_CLASE = '1' THEN

     MI_CAMPOS := 'ANOPB           = ANOPB  + '|| UN_ANO||'
                  ,MESPB           = MESPB  + '|| UN_MES||'
                  ,DIASPB          = DIASPB + '|| UN_DIA||'
                  ,MODIFIED_BY     = '''||UN_USUARIO||'''
                  ,DATE_MODIFIED   = SYSDATE';

     MI_CONDICION := 'COMPANIA      = ''' || UN_COMPANIA  ||'''
                    AND NUMERO_DCTO = ''' ||UN_NUMERO_DCTO||'''
                    AND SUCURSAL    = '''||UN_SUCURSAL||'''
                       ';  

        BEGIN
           BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'NAT_DATOS_PERSONALES', 
                                                       UN_ACCION    =>  'M', 
                                                       UN_CAMPOS    =>  MI_CAMPOS, 
                                                       UN_CONDICION =>  MI_CONDICION );
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
           END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN  
                    MI_MSGERROR(1).CLAVE := 'CEDULA';
                    MI_MSGERROR(1).VALOR := UN_NUMERO_DCTO;
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD   => SQLCODE,
                                              UN_ERROR_COD => PCK_ERRORES.ERR_HVACTEXPPUBLICA,
                                              UN_REEMPLAZOS  => MI_MSGERROR
                                            );
        END;
  END IF;


  IF UN_CLASE = '2' THEN

          MI_CAMPOS := 'ANOPV          = ANOPV  +  '|| UN_ANO||'
                      ,MESPV           = MESPV  +  '|| UN_MES||'
                      ,DIASPV          = DIASPV + '|| UN_DIA||'
                      ,MODIFIED_BY     = '''||UN_USUARIO||'''
                      ,DATE_MODIFIED   = SYSDATE';

     MI_CONDICION := 'COMPANIA      = ''' || UN_COMPANIA  ||'''
                    AND NUMERO_DCTO = ''' ||UN_NUMERO_DCTO||'''
                    AND SUCURSAL    = '''||UN_SUCURSAL||'''   ';  

        BEGIN
           BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'NAT_DATOS_PERSONALES', 
                                                       UN_ACCION    =>  'M', 
                                                       UN_CAMPOS    =>  MI_CAMPOS, 
                                                       UN_CONDICION =>  MI_CONDICION );
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
           END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN  
                    MI_MSGERROR(1).CLAVE := 'CEDULA';
                    MI_MSGERROR(1).VALOR := UN_NUMERO_DCTO;
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD   => SQLCODE,
                                              UN_ERROR_COD => PCK_ERRORES.ERR_HVACTEXPPRIVADA,
                                              UN_REEMPLAZOS  => MI_MSGERROR
                                            );
        END;

  END IF;

  IF UN_CLASE = '3' THEN
        MI_CAMPOS := 'ANOTI         = ANOTI  + '|| UN_ANO||'
                     ,MESTI         = MESTI  + '|| UN_MES||'
                     ,DIASTI        = DIASTI + '|| UN_DIA||'
                     ,MODIFIED_BY   = '''||UN_USUARIO||'''
                     ,DATE_MODIFIED = SYSDATE';

     MI_CONDICION := 'COMPANIA      = ''' || UN_COMPANIA  ||'''
                    AND NUMERO_DCTO = ''' ||UN_NUMERO_DCTO||'''
                    AND SUCURSAL    = '''||UN_SUCURSAL||'''   ';  

        BEGIN
           BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'NAT_DATOS_PERSONALES', 
                                                       UN_ACCION    =>  'M', 
                                                       UN_CAMPOS    =>  MI_CAMPOS, 
                                                       UN_CONDICION =>  MI_CONDICION );
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
           END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN  
                    MI_MSGERROR(1).CLAVE := 'CEDULA';
                    MI_MSGERROR(1).VALOR := UN_NUMERO_DCTO;
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD   => SQLCODE,
                                              UN_ERROR_COD => PCK_ERRORES.ERR_HVACTEXTINDEPEN,
                                              UN_REEMPLAZOS  => MI_MSGERROR
                                            );
        END;
  END IF;

END PR_ACTEXPLABORALPERSONALES;
--12
PROCEDURE PR_INSERTARACTIVIDADES
(
/*
    NAME              : PR_INSERTARACTIVIDADES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 30/04/2018
    TIME              : 03:17 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : Procedimiento que inserta las actividades registrardas en tipo transaccion a las transacciones

    PARAMETERS        :                         

    @NAME:  insertarActividades
    @METHOD:  POST                         
  */
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPO_TRANSACCION IN PCK_SUBTIPOS.TI_ENTERO,
    UN_TRANSACCION      IN PCK_SUBTIPOS.TI_LONG,
    UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)
AS
     MI_STRSQL          PCK_SUBTIPOS.TI_STRSQL;
     MI_TIPO            PCK_SUBTIPOS.TI_ENTERO;
     MI_TABLA           PCK_SUBTIPOS.TI_TABLA;
     MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
     MI_VALORES         PCK_SUBTIPOS.TI_VALORES;
     MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN 
    BEGIN 
        MI_STRSQL := '  SELECT TIPO_TRANSACCION
                        FROM SST_TRANSACCION_ACTIVIDAD
                        WHERE   COMPANIA          = '''||UN_COMPANIA||'''
                            AND TIPO_TRANSACCION  = '||UN_TIPO_TRANSACCION||'
                            AND TRANSACCION       = '||UN_TRANSACCION||'
                            AND ROWNUM = 1';
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_TIPO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_TABLA := 'SST_TRANSACCION_ACTIVIDAD';
      MI_CAMPOS := '  COMPANIA,
                      TIPO_TRANSACCION,
                      TRANSACCION,
                      ACTIVIDAD,
                      CODIGO_PLANTILLA,
                      DATE_CREATED,
                      CREATED_BY';
      MI_VALORES := 'SELECT 
                        COMPANIA,
                        TIPO_TRANSACCION,
                        '||UN_TRANSACCION||' TRANSACCION,
                        CODIGO,
                        CODIGO_PLANTILLA,
                        SYSDATE,
                        '''||UN_USUARIO||'''CREATED_BY
                       FROM  SST_TIPO_ACTIVIDAD
                       WHERE COMPANIA          = '''||UN_COMPANIA||'''
                         AND   TIPO_TRANSACCION  = '||UN_TIPO_TRANSACCION||'';
        BEGIN 
          BEGIN                        
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                                    UN_ACCION  => 'IS', 
                                                    UN_CAMPOS  => MI_CAMPOS, 
                                                    UN_VALORES => MI_VALORES);  
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
          END;     
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN  
            MI_MSGERROR(0).CLAVE := 'TIPOTRANSACCION';
            MI_MSGERROR(0).VALOR := UN_TIPO_TRANSACCION;          
                   PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD   => SQLCODE,
                   UN_ERROR_COD => PCK_ERRORES.ERR_ACTIVIDADES,
                   UN_REEMPLAZOS => MI_MSGERROR
                 );
      END;                                                                                     
    END;                        
END PR_INSERTARACTIVIDADES;


--13
PROCEDURE PR_ACTUALIZAPLANTILLA 
(
/*
    NAME              : PR_ACTUALIZAPLANTILLA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 30/04/2018
    TIME              : 03:17 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : Procedimiento que actualiza el codigo de plantilla en la transaccion

    PARAMETERS        :                         

    @NAME:  actualizarPlantilla
    @METHOD:  PUT                         
  */
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOTRANSACCION  IN  SST_TIPO_ACTIVIDAD.TIPO_TRANSACCION%TYPE,
    UN_ACTIVIDAD        IN SST_TIPO_ACTIVIDAD.CODIGO%TYPE
)
 AS 
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CODIGO_PLANTILLA VARCHAR2(15 CHAR);

BEGIN 
   SELECT CODIGO_PLANTILLA
   INTO MI_CODIGO_PLANTILLA
   FROM SST_TIPO_ACTIVIDAD
   WHERE   COMPANIA             = UN_COMPANIA
     AND CODIGO                 = UN_ACTIVIDAD
     AND TIPO_TRANSACCION       = UN_TIPOTRANSACCION;

    BEGIN 
        BEGIN 
        IF MI_CODIGO_PLANTILLA IS NULL THEN
        MI_CODIGO_PLANTILLA := 'NULL';
        END IF;

          MI_TABLA  := 'SST_TRANSACCION_ACTIVIDAD';
          MI_CAMPOS := '  CODIGO_PLANTILLA  = '||MI_CODIGO_PLANTILLA||',
                          DATE_MODIFIED     = SYSDATE';
          MI_CONDICION := ' COMPANIA          = '''||UN_COMPANIA||'''
                        AND TIPO_TRANSACCION  = '||UN_TIPOTRANSACCION||'
                        AND ACTIVIDAD         = '||UN_ACTIVIDAD||'';   
          PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA       =>MI_TABLA,
                                              UN_ACCION      => 'M',
                                              UN_CAMPOS      => MI_CAMPOS,
                                              UN_CONDICION   => MI_CONDICION
                                              );                                 
      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
      END;
    EXCEPTION  WHEN  PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
        MI_MSGERROR(0).CLAVE := 'TIPOTRANSACCION';
        MI_MSGERROR(0).VALOR := UN_TIPOTRANSACCION;
        MI_MSGERROR(1).CLAVE := 'ACTIVIDAD';
        MI_MSGERROR(1).VALOR := UN_ACTIVIDAD;
        PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD =>SQLCODE,
                                    UN_ERROR_COD=>PCK_ERRORES.ERR_ACTIVIDADESPLANTILLA,
                                    UN_REEMPLAZOS  => MI_MSGERROR                                      
                                        );


    END;    
END  PR_ACTUALIZAPLANTILLA;

--14

PROCEDURE PR_DOCUMENTOSPRESENTADOS (

/*
    NAME              : PR_DOCUMENTOSPRESENTADOS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
    DATE MIGRADOR     : 25/06/2018
    TIME              : 11:40 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : Procedimiento que verifica que los documentos presentados se creen para cada tercero

    PARAMETERS        :                         

    @NAME:  documentosPresentados
    @METHOD:  POST                     
  */
    UN_COMPANIA   IN    PCK_SUBTIPOS.TI_COMPANIA,
    UN_SUCURSAL   IN    DOCUMENTOS_PRESENTADOS.SUCURSAL%TYPE,
    UN_TERCERO    IN    DOCUMENTOS_PRESENTADOS.NUMERO_DCTO%TYPE ,   
    UN_USUARIO    IN    PCK_SUBTIPOS.TI_USUARIO
)  AS 
    MI_VALOR    PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CAMPOS   PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES  PCK_SUBTIPOS.TI_VALORES;
    MI_RTA      PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN

    BEGIN
        FOR MI_RS IN (SELECT CONSECUTIVO, PRESENTAR
                        FROM DOCUMENTOS_REQUERIDOS 
                       WHERE COMPANIA = UN_COMPANIA 
                         AND PRESENTAR NOT IN (0))
        LOOP  
            BEGIN
                SELECT COUNT(1) 
                  INTO MI_VALOR
                  FROM DOCUMENTOS_PRESENTADOS
                 WHERE COMPANIA     = UN_COMPANIA
                   AND NUMERO_DCTO  = UN_TERCERO
                   AND CODIGO       = MI_RS.CONSECUTIVO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_VALOR := 0;
            END;

            IF MI_VALOR = 0 THEN

                BEGIN
                    BEGIN
                        MI_CAMPOS   := 'COMPANIA, SUCURSAL, NUMERO_DCTO, CODIGO, 
                                        PRESENTADO, DATE_CREATED, CREATED_BY';
                        MI_VALORES  := ''''    || UN_COMPANIA || ''' , ''' || UN_SUCURSAL ||
                                       ''',''' || UN_TERCERO  || ''' ,   ' || MI_RS.CONSECUTIVO || 
                                         ','   || MI_RS.PRESENTAR || ', SYSDATE , ''' || UN_USUARIO || '''';
                        MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    =>  'DOCUMENTOS_PRESENTADOS',
                                                                  UN_ACCION   =>  'I', 
                                                                  UN_CAMPOS   =>  MI_CAMPOS, 
                                                                  UN_VALORES  =>  MI_VALORES);
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                          RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN    
                                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD   => SQLCODE,
                                    UN_ERROR_COD => PCK_ERRORES.ERR_HV_INSDOCPRESENTADOS);
                END;

            END IF;
        END LOOP;
    END;
END PR_DOCUMENTOSPRESENTADOS;

-- 15 
PROCEDURE PR_EXPERIENCIALABORAL (

    /*
    NAME              : PR_EXPERIENCIALABORAL
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
    DATE MIGRADOR     : 26/06/2018
    TIME              : 10:00 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : Procedimiento que registra la experiencia laboral desde nomina a Hojas de Vida

    PARAMETERS        :                         

    @NAME:  experienciaLaboral
    @METHOD:  POST                     
  */

UN_COMPANIA    IN    PCK_SUBTIPOS.TI_COMPANIA, 
     UN_TERCERO     IN    PCK_SUBTIPOS.TI_USUARIO,
     UN_SUCURSAL    IN    PCK_SUBTIPOS.TI_SUCURSAL,
     UN_USUARIO     IN    PCK_SUBTIPOS.TI_USUARIO
) AS 
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_RTA          PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_CONSECUTIVO  PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
    MI_DIFERENCIA   VARCHAR2(50 CHAR);
    MI_CANTIDAD     PCK_SUBTIPOS.TI_ENTERO_LARGO;

BEGIN

    MI_TABLA := 'NAT_EXPERIENCIA_LABORAL';

    BEGIN

        FOR MI_RS IN ( SELECT P.ID_DE_EMPLEADO, P.FECHA_DE_INGRESO, 
                             P.FECHA_DE_RETIRO, P.DEPENDENCIA,
                             C.NOMBRE, C.TIPOENTIDAD, C.PAIS, C.DEPARTAMENTO, 
                             C.CIUDAD, C.DIRECCION, C.TELEFONO,
                             CARGOS.NOMBRE_DEL_CARGO
                        FROM PERSONAL P
                       INNER JOIN COMPANIA C
                          ON P.COMPANIA     = C.CODIGO
                       INNER JOIN CARGOS
                          ON P.COMPANIA    = CARGOS.COMPANIA
                         AND P.ID_DE_CARGO = CARGOS.ID_DE_CARGO
                       WHERE P.COMPANIA     = UN_COMPANIA
                         AND P.NUMERO_DCTO  = UN_TERCERO)

        LOOP
            IF MI_RS.FECHA_DE_INGRESO IS NOT NULL AND MI_RS.FECHA_DE_RETIRO IS NOT NULL THEN

                BEGIN
                  SELECT COUNT(1) 
                    INTO MI_CANTIDAD
                    FROM NAT_EXPERIENCIA_LABORAL 
                   WHERE COMPANIA     = UN_COMPANIA 
                     AND NUMERO_DCTO  = UN_TERCERO 
                     AND SUCURSAL     = UN_SUCURSAL 
                     AND NEL_CODIGOPERSONA = MI_RS.ID_DE_EMPLEADO;
                END;

                IF MI_CANTIDAD = 0 THEN

                    MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO (UN_TABLA    => MI_TABLA,
                                                      UN_CRITERIO => '              COMPANIA = ''' || UN_COMPANIA || ''' 
                                                                             AND NUMERO_DCTO = ''' || UN_TERCERO  || ''' 
                                                                             AND SUCURSAL    = ''' || UN_SUCURSAL || '''',
                                                      UN_CAMPO    => 'NUMERO',
                                                      UN_INICIAL  => 1);

                    MI_DIFERENCIA := PCK_SYSMAN_UTL.FC_EDAD(TO_CHAR(MI_RS.FECHA_DE_INGRESO,'DD/MM/YYYY'), 
                                                            TO_CHAR(MI_RS.FECHA_DE_RETIRO ,'DD/MM/YYYY'), 1, 0);  

                        BEGIN
                            BEGIN
                                MI_CAMPOS   := 'COMPANIA, NUMERO_DCTO, SUCURSAL, NUMERO,
                                                NEL_CODIGOPERSONA, NOMBRE, PAIS, DEPTO, 
                                                MUNICIPIO, DIRECCION, TELEFONOS, CARGO,
                                                FECHAINGRESO, FECHARETIRO,
                                                ANOSERVICIO, MESESERVICIO, DIASERVICIO,
                                                DEPENDENCIA, DATE_CREATED, CREATED_BY';

                                MI_VALORES  := '''' || UN_COMPANIA          || ''', 
                                                ''' || UN_TERCERO           || ''', 
                                                ''' || UN_SUCURSAL          || ''',
                                                ''' || MI_CONSECUTIVO       || ''',
                                                ''' || MI_RS.ID_DE_EMPLEADO || ''', 
                                                ''' || MI_RS.NOMBRE         || ''',
                                                ''' || MI_RS.PAIS           || ''',
                                                ''' || MI_RS.DEPARTAMENTO   || ''',
                                                ''' || MI_RS.CIUDAD         || ''',
                                                ''' || MI_RS.DIRECCION      || ''',
                                                ''' || MI_RS.TELEFONO       || ''',
                                                ''' || MI_RS.NOMBRE_DEL_CARGO|| ''',
                                                ''' || MI_RS.FECHA_DE_INGRESO|| ''',
                                                ''' || MI_RS.FECHA_DE_RETIRO || ''',
                                                  ' || SUBSTR(MI_DIFERENCIA, 0, 2)|| ',
                                                  ' || SUBSTR(MI_DIFERENCIA, 4, 2)|| ',
                                                  ' || SUBSTR(MI_DIFERENCIA, 7, 2)|| ',
                                                ''' || MI_RS.DEPENDENCIA    || ''', SYSDATE , ''' || UN_USUARIO || '''';
                                MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    =>  MI_TABLA,
                                                                          UN_ACCION   =>  'I', 
                                                                          UN_CAMPOS   =>  MI_CAMPOS, 
                                                                          UN_VALORES  =>  MI_VALORES);
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                                  RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
                            END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN    
                                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                            UN_EXC_COD   => SQLCODE,
                                            UN_ERROR_COD => PCK_ERRORES.ERR_HV_ERRORINSEXPERIENCIAL);
                        END;

                    END IF; 
            END IF;

        END LOOP;
    END;
END PR_EXPERIENCIALABORAL;

--16

FUNCTION FC_INSERCPERSONAL
    (
/*
    NAME              : FC_INSERCPERSONAL
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANA YESSICA SANA ROJAS
    DATE MIGRADOR     : 29/06/2018
    TIME              : 12:30 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : Funcion que permite crear registro en Personal

    PARAMETERS        :                         

    @NAME:  registrarPersonal
    @METHOD:  POST                     
  */    

    UN_COMPANIA    IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_TERCERO     IN  NAT_DATOS_PERSONALES.NUMERO_DCTO%TYPE,
    UN_SUCURSAL    IN  PCK_SUBTIPOS.TI_SUCURSAL,
    UN_USUARIO     IN  PCK_SUBTIPOS.TI_USUARIO ) RETURN PCK_SUBTIPOS.TI_ENTERO
AS 
    MI_CARGO        NAT_NOMBRAMIENTO.NO_ID_DE_CARGO%TYPE;
    MI_ESCALAFON    NAT_NOMBRAMIENTO.NO_ESCALAFON%TYPE;
    MI_TIPONOMBRAM  NAT_NOMBRAMIENTO.NO_TIPO%TYPE;
    MI_CATEGORIA    NAT_NOMBRAMIENTO.NO_CATEGORIA%TYPE;
    MI_ANO          PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CONSECUTIVO  PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_RTA          PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
BEGIN
    BEGIN
        BEGIN
            SELECT NO_ID_DE_CARGO,
                   NO_ESCALAFON,
                   NO_TIPO,
                   NO_CATEGORIA,
                   ANO
              INTO MI_CARGO,
                   MI_ESCALAFON,
                   MI_TIPONOMBRAM,
                   MI_CATEGORIA,
                   MI_ANO
              FROM (SELECT 
                   NO_ID_DE_CARGO,
                   NO_ESCALAFON,
                   NO_TIPO,
                   NO_CATEGORIA,
                   EXTRACT(YEAR FROM NVL(NO_FECHAEFECTIVIDAD, SYSDATE)) ANO
              FROM NAT_NOMBRAMIENTO
             WHERE COMPANIA    = UN_COMPANIA
               AND DP_NUMEDOCU = UN_TERCERO
               AND SUCURSAL    = UN_SUCURSAL
             ORDER BY NO_FECHRESODECR DESC)
             WHERE ROWNUM = 1;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_HV_ERRORSINNOMBRAMIENTO
                    );
    END;
    --Solamente se crea un registro pero se deja con un for dado que se van a acrualiza varios campos que aun no se tienen definidos
    FOR MI_RS IN (SELECT NUMERO_DCTO
                        ,DCTO_IDENTIDAD
                        ,APELLIDO1
                        ,APELLIDO2
                        ,NOMBRES
                        ,PAISEXTRANJERO
                        ,DPTOEXPCEDULA
                        ,EXPEDIDA
                        ,TO_CHAR(FECHANCTO,'DD/MM/YYYY') FECHANCTO
                        ,PAISNCTO
                        ,DEPTONCTO
                        ,MUNICIPIONCTO
                        ,SEXO
                        ,PAISRESIDE
                        ,DEPTORESIDE
                        ,MUNICIPIORESIDE
                        ,DIRECCION
                        ,TELEFONOS
                        ,EMAIL
                        ,CORREOCORPORATIVO
                        ,ESTADO_CIVIL
                        ,ESTRATO
                        ,GRUPOSANGUINEO
                        ,RH
                        ,ALERGIAS
                        ,MEDICOTRATANTE
                        ,FECHAINGRESO
                        ,FECHARETIRO
                        ,FECHATERCONTRATO
                        ,PAIS_LABORA
                        ,DEPARAMENTO_LABORA
                        ,CIUDAD_LABORA
                        ,ID_CENTROS_DE_COSTO
                        ,NIT_ESTABLECIMIENTO_DOCENTES
                        ,SINDICATO
                        ,FONDO_SINDICATO
                        ,PERSONAS
                        ,BANCO
                        ,CUENTA
                        ,TIPOCUENTA
                        ,CIUDAD_CUENTA
                        ,LICENCIACONDUCCION
                        ,NUMLIBMILITAR
                        ,CERTIFICADOJUDICIAL
                        ,REGIMEN
                        ,NUMEROPATRONAL
                        ,GRUPOCONTABLE
                        ,PROCESORETENCION
                        ,ID_DE_TIPO
                        ,TIPO_SALARIO
                        ,AREAMISOADM
                        ,TIPOACTIVIDAD
                        ,SEDE
                        ,DEPENDIENTES384
                        ,DECLARANTES384
                        ,TO_CHAR(FECHA_RETE_MINIMA,'DD/MM/YYYY') FECHA_RETE_MINIMA
                        ,NIVELSIIF
                    FROM  NAT_DATOS_PERSONALES 
                   WHERE  COMPANIA    = UN_COMPANIA
                     AND  NUMERO_DCTO = UN_TERCERO
                     AND  SUCURSAL    = UN_SUCURSAL )
    LOOP

        BEGIN
            IF MI_RS.CORREOCORPORATIVO IS NULL THEN
                BEGIN
                    BEGIN
                        RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;

                    END;
                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_HV_CORREOCORPORATIVO
                    );
                END;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_HV_CORREOCORPORATIVO
                    );
            END IF;
        END;
        MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA   => 'PERSONAL',
                                         UN_CRITERIO=> ' COMPANIA = ''' || UN_COMPANIA || '''',
                                         UN_CAMPO   => 'ID_DE_EMPLEADO',
                                         UN_INICIAL => 1 );
        BEGIN
            BEGIN
                MI_CAMPOS  := 'COMPANIA  
                              ,ID_DE_EMPLEADO 
                              ,SUCURSAL
                              ,ID_DE_CARGO
                              ,ESCALAFON
                              ,ID_DE_CATEGORIA
                              ,ANO
                              ,DE_CARRERA
                              ,NUMERO_DCTO
                              ,DCTO_IDENTIDAD
                              ,APELLIDO1    
                              ,APELLIDO2
                              ,NOMBRES
                              ,PAIS_CED
                              ,DEPARTAMENTO_CED
                              ,EXPEDIDA
                              ,FECHANCTO
                              ,PAIS_NAC
                              ,DEPARTAMENTO_NAC
                              ,CIUDAD_NAC
                              ,SEXO
                              ,PAIS_HAB
                              ,DEPARTAMENTO_HAB
                              ,CIUDAD_HAB
                              ,DIRECCION
                              ,TELEFONOS
                              ,EMAIL_PERSONAL
                              ,EMAIL_CORPORATIVO
                              ,ESTADO_CIVIL
                              ,ESTRATO
                              ,GRUPOSANGINEO
                              ,RH
                              ,ALERGIAS
                              ,MEDICOTRATANTE
                              ,FECHA_DE_INGRESO
                              ,FECHA_DE_RETIRO
                              ,FECHATERCONTRATO
                              ,PAIS_LABORA
                              ,DEPARTAMENTO_LABORA
                              ,CIUDAD_LABORA
                              ,ID_CENTRO_DE_COSTO
                              ,CODIGO_ESTABLECIMIENTO
                              ,SINDICATO
                              ,FONDO_SINDICATO
                              ,PERSONASCARGO
                              ,BANCO
                              ,CUENTA
                              ,TIPOCUENTA
                              ,CIUDAD_CUENTA
                              ,LICENCIACONDUCCION
                              ,LIBRETA_MILITAR
                              ,CERTIFICADOJUDICIAL
                              ,REGIMEN
                              ,NUMEROPATRONAL
                              ,GRUPOCONTABLE
                              ,PROCESORETENCION
                              ,ID_DE_TIPO
                              ,TIPO_SALARIO
                              ,AREAMISOADM
                              ,TIPOACTIVIDAD
                              ,SEDE
                              ,DEPENDIENTES384
                              ,DECLARANTES384
                              ,FECHA_RETE_MINIMA
                              ,NIVELSIIF
                              ,DATE_CREATED
                              ,CREATED_BY  ';

                MI_VALORES   := '''' || UN_COMPANIA                         || 
                             ''',  ' || MI_CONSECUTIVO                      || 
                               ',''' || UN_SUCURSAL                         ||
                             ''',''' || MI_CARGO                            || 
                             ''',''' || MI_ESCALAFON                        ||
                             ''',''' || MI_CATEGORIA                        || 
                             ''',  ' || MI_ANO                              ||
                               ',''' || MI_TIPONOMBRAM                      ||
                             ''',''' || MI_RS.NUMERO_DCTO                   || 
                             ''',''' || MI_RS.DCTO_IDENTIDAD                || 
                             ''',''' || MI_RS.APELLIDO1                     || 
                             ''',''' || MI_RS.APELLIDO2                     ||
                             ''',''' || MI_RS.NOMBRES                       ||  
                             ''',''' || MI_RS.PAISEXTRANJERO                || 
                             ''',''' || MI_RS.DPTOEXPCEDULA                 ||
                             ''',''' || MI_RS.EXPEDIDA                      || 
                             ''',''' || MI_RS.FECHANCTO                     ||
                             ''',''' || MI_RS.PAISNCTO                      || 
                             ''',''' || MI_RS.DEPTONCTO                     ||
                             ''',''' || MI_RS.MUNICIPIONCTO                 ||
                             ''',''' || MI_RS.SEXO                          ||
                             ''',''' || MI_RS.PAISRESIDE                    ||
                             ''',''' || MI_RS.DEPTORESIDE                   ||                             
                             ''',''' || MI_RS.MUNICIPIORESIDE               ||  
                             ''',''' || MI_RS.DIRECCION                     ||
                             ''',''' || MI_RS.TELEFONOS                     ||
                             ''',''' || MI_RS.EMAIL                         ||
                             ''',''' || MI_RS.CORREOCORPORATIVO             ||
                             ''',''' || MI_RS.ESTADO_CIVIL                  ||
                             ''',''' || MI_RS.ESTRATO                       ||
                             ''',''' || MI_RS.GRUPOSANGUINEO                ||
                             ''',''' || MI_RS.RH                            ||
                             ''',''' || MI_RS.ALERGIAS                      ||
                             ''',''' || MI_RS.MEDICOTRATANTE                ||
                             ''',''' ||  MI_RS.FECHAINGRESO                   ||
                             ''','''  || MI_RS.FECHARETIRO                      ||
                             ''','''  || MI_RS.FECHATERCONTRATO                 || 
                             ''',''' || MI_RS.PAIS_LABORA                   ||
                             ''',''' || MI_RS.DEPARAMENTO_LABORA            ||
                             ''',''' || MI_RS.CIUDAD_LABORA                 ||
                             ''',''' || MI_RS.ID_CENTROS_DE_COSTO           ||
                             ''',''' || MI_RS.NIT_ESTABLECIMIENTO_DOCENTES  ||
                             ''',  ' || MI_RS.SINDICATO                     ||
                               ',''' || CASE WHEN MI_RS.FONDO_SINDICATO IS NULL THEN 'SIN99'  ELSE  MI_RS.FONDO_SINDICATO  END               || 
                             ''',  ' || MI_RS.PERSONAS                      ||
                               ',''' || CASE WHEN MI_RS.BANCO IS NULL THEN '99'  ELSE MI_RS.BANCO END  ||
                             ''',''' || CASE WHEN MI_RS.CUENTA IS NULL THEN '00'  ELSE MI_RS.CUENTA END ||
                             ''',''' || MI_RS.TIPOCUENTA                    ||
                             ''',''' || MI_RS.CIUDAD_CUENTA                 ||
                             ''',''' || MI_RS.LICENCIACONDUCCION            ||
                             ''',''' || MI_RS.NUMLIBMILITAR                 ||
                             ''',''' || MI_RS.CERTIFICADOJUDICIAL           ||
                             ''',''' || MI_RS.REGIMEN                       ||
                             ''',''' || MI_RS.NUMEROPATRONAL                ||
                             ''',''' || MI_RS.GRUPOCONTABLE                 ||
                             ''',  ' || MI_RS.PROCESORETENCION              ||
                               ',''' || MI_RS.ID_DE_TIPO                    ||
                             ''',''' || MI_RS.TIPO_SALARIO                  ||
                             ''',''' || MI_RS.AREAMISOADM                   ||
                             ''',''' || MI_RS.TIPOACTIVIDAD                 ||
                             ''',''' || MI_RS.SEDE                          ||
                             ''',  ' || MI_RS.DEPENDIENTES384               ||
                               ',  ' || MI_RS.DECLARANTES384                ||
                               ',''' || MI_RS.FECHA_RETE_MINIMA               || 
                               ''',' || CASE WHEN MI_RS.NIVELSIIF IS NULL THEN '''NIVEL ADMINISTRATIVO'''  ELSE '''' || MI_RS.NIVELSIIF || '''' END   || 
                               ' ,       SYSDATE                  
                                ,''' || UN_USUARIO                          || '''';

                MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => 'PERSONAL', 
                                              UN_ACCION   => 'I', 
                                              UN_CAMPOS   => MI_CAMPOS, 
                                              UN_VALORES  => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE,  
                                        UN_ERROR_COD => PCK_ERRORES.ERR_HV_INSERTPERSONAL );
        END;
    END LOOP;

    BEGIN
        BEGIN
            MI_CAMPOS   := ' CODIGO = ' || MI_CONSECUTIVO;

            MI_CONDICION:= '     COMPANIA  = ''' || UN_COMPANIA || 
                        ''' AND  NUMERO_DCTO  = ''' || UN_TERCERO  ||
                        ''' AND  SUCURSAL     = ''' || UN_SUCURSAL || '''';

            MI_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => 'NAT_DATOS_PERSONALES', 
                                          UN_ACCION   => 'M', 
                                          UN_CAMPOS   => MI_CAMPOS, 
                                          UN_CONDICION=> MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE,  
                                        UN_ERROR_COD => PCK_ERRORES.ERR_HV_MODIFICAIDEMPLEADO );
    END;

 RETURN MI_CONSECUTIVO;

END FC_INSERCPERSONAL;

PROCEDURE PR_ACTUALIZARCORREOSINSCRITOS
(
    /*
      NAME             : PR_ACTUALIZARCORREOSINSCRITOS
      AUTHORS          : SYSMAN SAS
      AUTHOR MIGRACION : MIGUEL ANGEL VENEGAS RODRGUEZ
      DATE MIGRADOR    : 04/07/2018
      TIME             : 10:40 AM
      DESCRIPTION      : Funcion que actualiza la fecha de envio del correo en la tabla NAT_APERTURA_INSCRITOS
                         0 = INSCRITOS                EN HOJAS DE VIDA/SELECCION DE PERSONAL
                         1 = EVALUACION DE DOCUMENTOS EN HOJAS DE VIDA/SELECCION DE PERSONAL (APROBADOS)
                         2 = CERRAR CONVOCATORIA      EN HOJAS DE VIDA/CERRAR CONVOCATORIA (ARPOBADOS)
                         3 = CERRAR CONVOCATORIA      EN HOJAS DE VIDA/CERRAR CONVOCATORIA (RECHAZADOS)
                         4 = CONVOCATORIA             EN HOJAS DE VIDA/SELECCION DE PERSONAL
                         5 = EVALUACION DE DOCUMENTOS EN HOJAS DE VIDA/SELECCION DE PERSONAL (RECHAZADOS)
                         6 = ELEGIBLES RECHAZADOS  
                         7 = AUTOSERVIC
      MODIFIER         : 
      DATE MODIFIED    : 
      TIME             : 
      MODIFICATIONS    : 

    @NAME  : actualizarEnvioCorreos
    */
  UN_COMPANIA         IN NAT_APERTURA_INSCRITOS.COMPANIA%TYPE,
  UN_NRO_CONVOCATORIA IN NAT_APERTURA_INSCRITOS.NRO_CONVOCATORIA%TYPE,
  UN_OPCION           IN NUMBER
)
AS
 MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
 MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
 MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
BEGIN

    MI_TABLA := 'NAT_APERTURA_INSCRITOS';
    IF UN_OPCION = 0 THEN
    MI_CAMPOS  := 'FECHA_ENVIO_CORREO_INSCRITOS = SYSDATE';
    ELSIF  UN_OPCION = 1 THEN
    MI_CAMPOS  := 'FECHA_ENVIO_CORREO_APROBADOS = SYSDATE'; 
    ELSIF  UN_OPCION = 2 THEN
    MI_CAMPOS  := 'FECHA_ENVIO_ACP = SYSDATE';
    ELSIF UN_OPCION = 3 THEN
    MI_CAMPOS  := 'FECHA_ENVIO_RCZ = SYSDATE';
    ELSIF UN_OPCION = 4 THEN
    MI_CAMPOS  := 'FECHA_ENVIO_CORREO = SYSDATE';
    MI_TABLA := 'NAT_APERTURA';
    ELSIF UN_OPCION = 6 THEN 
    MI_CAMPOS  := 'FECHA_ENVIO_CORREO = SYSDATE';
    MI_TABLA := 'NAT_ELEGIBLES';
    ELSE
    MI_CAMPOS  := 'FECHA_ENVIO_CORREO_RECHAZADOS = SYSDATE';
    END IF;

    MI_CONDICION := ' COMPANIA              = '''|| UN_COMPANIA         ||''' 
                      AND NRO_CONVOCATORIA  = '''|| UN_NRO_CONVOCATORIA ||''' ';  
    BEGIN 
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                               UN_ACCION    =>  'M', 
                                               UN_CAMPOS    =>  MI_CAMPOS,
                                               UN_CONDICION =>  MI_CONDICION );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN    
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERRR_HOJAS_VIDA_ACT_CONS
                    );
    END;
END PR_ACTUALIZARCORREOSINSCRITOS;


PROCEDURE PR_ACTCORREOSAUTOSERVICIO
(
    /*
      NAME             : PR_ACTCORREOSAUTOSERVICIO
      AUTHORS          : SYSMAN SAS
      AUTHOR MIGRACION : MIGUEL ANGEL VENEGAS RODRGUEZ
      DATE MIGRADOR    : 26/10/2018
      TIME             : 01:00 pM
      DESCRIPTION      : Funcion que actualiza la fecha de envio del correo en la tabla AUT_SOLICITUDES
                         0 = TIPO PERMISOS Y VACACIONES AUTOSERVICIO/
      MODIFIER         : 
      DATE MODIFIED    : 
      TIME             : 
      MODIFICATIONS    : 

    @NAME  : actualizarEnvioCorreosAutoservicio
    */
  UN_COMPANIA         IN NAT_APERTURA_INSCRITOS.COMPANIA%TYPE,
  UN_CONSECUTIVO      IN AUT_SOLICITUDES.CONSECUTIVO%TYPE,
  UN_CLASE            IN AUT_SOLICITUDES.CLASE_SOLICITUD%TYPE,
  UN_OPCION           IN NUMBER
)
AS
 MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
 MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
 MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
BEGIN

    MI_TABLA     := 'AUT_SOLICITUDES';
    MI_CAMPOS    := 'FECHA_ENVIO_CORREO = SYSDATE';
    MI_CONDICION := ' COMPANIA            = '''|| UN_COMPANIA    ||''' 
                      AND CONSECUTIVO     =   '|| UN_CONSECUTIVO ||' 
                      AND CLASE_SOLICITUD =   '|| UN_CLASE       ||' ';  
    BEGIN 
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                               UN_ACCION    =>  'M', 
                                               UN_CAMPOS    =>  MI_CAMPOS,
                                               UN_CONDICION =>  MI_CONDICION );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN    
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERRR_HOJAS_VIDA_ACT_CONS
                    );
    END;
END PR_ACTCORREOSAUTOSERVICIO;

--17

PROCEDURE PR_CARGACONVOMANUAL
/*
    NAME              : PR_CARGACONVOMANUAL
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRATION    : 05/07/2018
    TIME              : 08:00 AM
    SOURCE MODULE     : 
    DESCRIPTION       : Carga la informaciÃ³n de educaciÃ³n y experiencia de  
                        las convocatorias a partir del manual seleccionado
    MODIFIED BY       : 

    @NAME  : cargarConvocatoriaManual
  */
(
  UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_NUMEROMANUAL        IN EV_MANUAL.NUMERO_MANUAL%TYPE,
  UN_VERSION             IN EV_MANUAL.VERSION%TYPE,
  UN_NROCONVOCATORIA     IN NAT_APERTURA.NRO_CONVOCATORIA%TYPE, 
  UN_USUARIO             IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_EDUCACION          CLOB;
  MI_EXPERIENCIA        CLOB;
  MI_EQUIEDUCACION      CLOB;
  MI_EQUIEXPERIENCIA    CLOB;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

 IF UN_NUMEROMANUAL IS NULL AND UN_VERSION IS NULL THEN

      MI_CAMPOS := 'REQUISITOEDUCACION = NULL,
                REQUISITOEXPERIENCIA = NULL,
                REQUISITOEQUIVALENCIAS = NULL,
                REQUISITOEQUIEXPERIENCIA = NULL,
                DATE_MODIFIED = SYSDATE,
                MODIFIED_BY = '''||UN_USUARIO||''' ';


  MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' 
                   AND NRO_CONVOCATORIA = '''||UN_NROCONVOCATORIA||''' ';                

  BEGIN 
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'NAT_APERTURA', 
                                               UN_ACCION    =>  'M', 
                                               UN_CAMPOS    =>  MI_CAMPOS,
                                               UN_CONDICION =>  MI_CONDICION );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN    
       MI_MSGERROR(1).CLAVE := 'NUMERO';
       MI_MSGERROR(1).VALOR := UN_NROCONVOCATORIA;
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERRR_HVACTEQUIAPERTURA,
                      UN_REEMPLAZOS  => MI_MSGERROR
                    );
    END;


    RETURN;

 END IF;

  MI_EQUIEDUCACION := '';
  MI_EQUIEXPERIENCIA := '';

--Cargar informaciÃ³n educaciÃ³n alternativa 0

  SELECT DESCRIPCION
    INTO MI_EDUCACION 
    FROM EV_REQUISITOS
   WHERE COMPANIA       = UN_COMPANIA
     AND NUMERO_MANUAL  = UN_NUMEROMANUAL
     AND VERSION        = UN_VERSION
     AND TIPO_REQUISITO IN ('00001')
     AND ALTERNATIVA    IN (0);


--Cargar informaciÃ³n experiencia alternativa 0

    SELECT DESCRIPCION
      INTO MI_EXPERIENCIA
    FROM EV_REQUISITOS
    WHERE COMPANIA       = UN_COMPANIA
      AND NUMERO_MANUAL  = UN_NUMEROMANUAL
      AND VERSION        = UN_VERSION
      AND TIPO_REQUISITO IN ('00002')
      AND ALTERNATIVA    IN (0);  

--Cargar informaciÃ³n educaciÃ³n cualquier equivalencia  

  <<RECORRER_EDUCACION>>
  FOR MI_RS IN (SELECT DESCRIPCION
                FROM EV_REQUISITOS
                WHERE COMPANIA       = UN_COMPANIA
                  AND NUMERO_MANUAL  = UN_NUMEROMANUAL
                  AND VERSION        = UN_VERSION
                  AND TIPO_REQUISITO IN ('00001')
                  AND ALTERNATIVA   NOT IN (0))

    LOOP

    MI_EQUIEDUCACION := MI_EQUIEDUCACION||MI_RS.DESCRIPCION||' '; 


  END LOOP RECORRER_EDUCACION;

--Cargar informaciÃ³n experiencia cualquier equivalencia  

  <<RECORRER_EXPERIENCIA>>
  FOR MI_RS IN (SELECT DESCRIPCION
                FROM EV_REQUISITOS
                WHERE COMPANIA       = UN_COMPANIA
                  AND NUMERO_MANUAL  = UN_NUMEROMANUAL
                  AND VERSION        = UN_VERSION
                  AND TIPO_REQUISITO IN ('00002')
                  AND ALTERNATIVA   NOT IN (0))

    LOOP

    MI_EQUIEXPERIENCIA := MI_EQUIEXPERIENCIA||MI_RS.DESCRIPCION||' '; 


  END LOOP RECORRER_EXPERIENCIA;  


  MI_CAMPOS := 'REQUISITOEDUCACION = '''||MI_EDUCACION||''',
                REQUISITOEXPERIENCIA = '''||MI_EXPERIENCIA||''',
                REQUISITOEQUIVALENCIAS = '''||MI_EQUIEDUCACION||''',
                REQUISITOEQUIEXPERIENCIA = '''||MI_EQUIEXPERIENCIA||''',
                DATE_MODIFIED = SYSDATE,
                MODIFIED_BY = '''||UN_USUARIO||''' ';


  MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' 
                   AND NRO_CONVOCATORIA = '''||UN_NROCONVOCATORIA||''' ';                

  BEGIN 
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'NAT_APERTURA', 
                                               UN_ACCION    =>  'M', 
                                               UN_CAMPOS    =>  MI_CAMPOS,
                                               UN_CONDICION =>  MI_CONDICION );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN    
       MI_MSGERROR(1).CLAVE := 'NUMERO';
       MI_MSGERROR(1).VALOR := UN_NROCONVOCATORIA;
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERRR_HVACTEQUIAPERTURA,
                      UN_REEMPLAZOS  => MI_MSGERROR
                    );
    END;
END PR_CARGACONVOMANUAL;




PROCEDURE PR_GENERARCOMPROMISOS
    /*
    NAME              : PR_GENERARCOMPROMISOS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 11/07/2018
    TIME              : 12:00 M
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : PROCEDIMIENTO QUE GENERA LOS COMPROMISOS ACORDADOS PARA CALIFICAR

    PARAMETERS        :                         

    @NAME:  generarCompromisos
    @METHOD:  POST                     
  */
(
    UN_COMPANIA           IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_EVALUACION         IN  EV_COMPROMISOS_LABORALES.NUMERO_EVALUACION%TYPE,
    UN_CLASE              IN  EV_COMPROMISOS_LABORALES.CLASE_EVALUACION%TYPE,
    UN_ANO                IN  EV_COMPROMISOS_LABORALES.ANO%TYPE,
    UN_EVALUADO           IN  EV_COMPROMISOS_LABORALES.CEDULA_EVALUADO%TYPE,
    UN_SUCURSALEVALUADO   IN  EV_COMPROMISOS_LABORALES.SUCURSAL_EVALUADO%TYPE,
    UN_PERIODO            IN  EV_DETALLE_COMPACORDADOS.PERIODO%TYPE,
    UN_TIPO               IN  EV_COMPROMISOS_LABORALES.TIPO_EVALUACION%TYPE,
    UN_USUARIO            IN  PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
  MI_EXISTE         PCK_SUBTIPOS.TI_ENTERO;
  MI_CONSECUTIVO    PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN
      FOR RS IN (SELECT COMPANIA,
                      NUMERO_EVALUACION,
                      CLASE_EVALUACION,
                      TIPO_EVALUACION,
                      CEDULA_EVALUADO,
                      SUCURSAL_EVALUADO,
                      CEDULA_EVALUADOR,
                      SUCURSAL_EVALUADOR,
                      CONSECUTIVO,
                      PESO_PORCENTUAL
                FROM EV_COMPROMISOS_LABORALES
                WHERE COMPANIA        = UN_COMPANIA
                AND CLASE_EVALUACION  = UN_CLASE
                AND TIPO_EVALUACION   = UN_TIPO
                AND ANO               = UN_ANO
                AND CEDULA_EVALUADO   = UN_EVALUADO
                AND SUCURSAL_EVALUADO = UN_SUCURSALEVALUADO)
    LOOP

    SELECT COUNT(1)
            INTO MI_EXISTE
      FROM EV_DETALLE_COMPACORDADOS
      WHERE COMPANIA           = UN_COMPANIA
        AND CLASE_EVALUACION   = UN_CLASE
        AND TIPO_EVALUACION    = UN_TIPO
        AND ANO                = UN_ANO
        AND PERIODO            = UN_PERIODO
        AND CEDULA_EVALUADO    = RS.CEDULA_EVALUADO
        AND SUCURSAL_EVALUADO  = RS.SUCURSAL_EVALUADO
        AND CONSECUTIVO        = RS.CONSECUTIVO;

      IF MI_EXISTE = 0 THEN
       MI_CAMPOS :='COMPANIA,                  
                    NUMERO_EVALUACION,                  
                    CLASE_EVALUACION,                  
                    TIPO_EVALUACION,
                    ANO,
                    PERIODO,
                    CEDULA_EVALUADO,                  
                    SUCURSAL_EVALUADO,                  
                    CEDULA_EVALUADOR,                  
                    SUCURSAL_EVALUADOR,
                    CONSECUTIVO,
                    PESO_PORCENTUAL,
                    DATE_CREATED,
                    CREATED_BY
                    ';

       MI_VALORES:=''''|| RS.COMPANIA||''',
                    '  || UN_EVALUACION||',
                    '  || RS.CLASE_EVALUACION||',
                    '''|| RS.TIPO_EVALUACION ||''',
                    '  || UN_ANO||',
                    '  || UN_PERIODO||',
                    '''|| RS.CEDULA_EVALUADO||''',
                    '''|| RS.SUCURSAL_EVALUADO||''',
                    '''|| RS.CEDULA_EVALUADOR||''',
                    '''|| RS.SUCURSAL_EVALUADOR||''',
                    '  || RS.CONSECUTIVO || ',
                    '  || RS.PESO_PORCENTUAL || ',
                    SYSDATE,
                    '''|| UN_USUARIO ||'''
                    ';
          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => 'EV_DETALLE_COMPACORDADOS', 
                                                      UN_ACCION   => 'I', 
                                                      UN_CAMPOS   => MI_CAMPOS, 
                                                      UN_VALORES  => MI_VALORES);
            EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
            END;
          EXCEPTION
          WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE,  
                                        UN_ERROR_COD => PCK_ERRORES.ERR_HVINSCOMPROMISOSACORD );
          END;
      END IF;
    END LOOP;  
END PR_GENERARCOMPROMISOS;


PROCEDURE PR_HEREDAREVIDENCIAS
 /*
      NAME              : PR_HEREDAREVIDENCIAS
      AUTHORS           : 
      AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
      DATE MIGRADOR     : 17/08/2018
      TIME              : 05:00 PM
      SOURCE MODULE     : HOJAS DE VIDA
      MODIFIER          :
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : HEREDA LAS COMPETENCIAS Y LOS COMPROMISOS A LA TABLA DE EVIDENCIAS 
      MODIFICATIONS     : 

      @NAME:  heredarEvidencias
      @METHOD:  POST

  */
(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_EVALUACION         IN EV_SUBDETALLE_EVALUACION.EVALUACION%TYPE,
  UN_CEDULA_EVALUADO    IN EV_SUBDETALLE_EVALUACION.CEDULA_EVALUADO%TYPE,
  UN_CEDULA_EVALUADOR   IN EV_SUBDETALLE_EVALUACION.CEDULA_EVALUADOR%TYPE,
  UN_CLASE              IN EV_SUBDETALLE_EVALUACION.CLASE_EVALUACION%TYPE,
  UN_TIPO               IN EV_SUBDETALLE_EVALUACION.TIPO_EVALUACION%TYPE,
  UN_ANO                IN EV_COMPROMISOS_LABORALES.ANO%TYPE,
  UN_OPCION             IN PCK_SUBTIPOS.TI_ENTERO,
  UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
)
AS
MI_CONSECUTIVO        PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;    
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_EXISTE             PCK_SUBTIPOS.TI_ENTERO;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

     MI_TABLA := CASE UN_OPCION WHEN 1 THEN 'EV_EVIDENCIAS' 
                 ELSE 'EV_ACCIONES_MEJORA_COMPORTAMEN' END; 

     MI_CAMPOS := 'COMPANIA,
                    NUMERO_EVALUACION,
                    CLASE_EVALUACION,
                    TIPO_EVALUACION,
                    CONSECUTIVO,'|| CASE UN_OPCION WHEN 1 THEN 
                    'DESCRIPCION,' ELSE  '' END || '
                    TIPO,
                    CEDULA_EVALUADO,
                    SUCURSAL_EVALUADO,
                    CEDULA_EVALUADOR,
                    SUCURSAL_EVALUADOR,
                    COMPONENTE,
                    '|| CASE UN_OPCION WHEN 2 THEN 
                    'MOTIVO,' ELSE  '' END || '
                    DATE_CREATED,
                    CREATED_BY';

    FOR RS IN (SELECT EV_SUBDETALLE_EVALUACION.CRITERIO_EVALUADO,
                      EV_SUBDETALLE_EVALUACION.SUCURSAL_EVALUADO,
                      EV_SUBDETALLE_EVALUACION.SUCURSAL_EVALUADOR,
                      EV_CRITERIOS_EVALUACION.NOMBRE NOMBRECRITERIO,
                      EV_CRITERIOS_EVALUACION.TEXTO
                FROM EV_SUBDETALLE_EVALUACION
                INNER JOIN EV_CRITERIOS_EVALUACION
                ON EV_SUBDETALLE_EVALUACION.CRITERIO_EVALUADO = EV_CRITERIOS_EVALUACION.CODIGO
                AND EV_SUBDETALLE_EVALUACION.CLASE_EVALUACION = EV_CRITERIOS_EVALUACION.CLASE_EVALUACION
                AND EV_SUBDETALLE_EVALUACION.COMPANIA         = EV_CRITERIOS_EVALUACION.COMPANIA
                WHERE EV_SUBDETALLE_EVALUACION.COMPANIA       = UN_COMPANIA
                AND EV_SUBDETALLE_EVALUACION.EVALUACION       = UN_EVALUACION
                AND EV_SUBDETALLE_EVALUACION.CEDULA_EVALUADO  = UN_CEDULA_EVALUADO
                AND EV_SUBDETALLE_EVALUACION.CEDULA_EVALUADOR = UN_CEDULA_EVALUADOR
                AND EV_SUBDETALLE_EVALUACION.CLASE_EVALUACION = UN_CLASE
                AND EV_SUBDETALLE_EVALUACION.TIPO_EVALUACION  = UN_TIPO
                AND EV_SUBDETALLE_EVALUACION.MOVIMIENTO       = 0)
    LOOP
      IF UN_OPCION = 1 THEN 
           SELECT COUNT (1)
           INTO MI_EXISTE
            FROM EV_EVIDENCIAS
            WHERE COMPANIA        = UN_COMPANIA
              AND NUMERO_EVALUACION = UN_EVALUACION
              AND CLASE_EVALUACION  = UN_CLASE
              AND TIPO_EVALUACION   = UN_TIPO
              AND TIPO              = 'CC'
              AND CEDULA_EVALUADO   = UN_CEDULA_EVALUADO
              AND CEDULA_EVALUADOR  = UN_CEDULA_EVALUADOR
              AND CONSECUTIVO       = RS.CRITERIO_EVALUADO;
        ELSE
            SELECT COUNT (1)
            INTO MI_EXISTE
            FROM EV_ACCIONES_MEJORA_COMPORTAMEN
              WHERE COMPANIA        = UN_COMPANIA
              AND NUMERO_EVALUACION = UN_EVALUACION
              AND CLASE_EVALUACION  = UN_CLASE
              AND TIPO_EVALUACION   = UN_TIPO
              AND TIPO              = 'CC'
              AND CEDULA_EVALUADO   = UN_CEDULA_EVALUADO
              AND CEDULA_EVALUADOR  = UN_CEDULA_EVALUADOR
              AND CONSECUTIVO       = RS.CRITERIO_EVALUADO;
        END IF;

      IF MI_EXISTE = 0 THEN     
          MI_VALORES := ''''|| UN_COMPANIA           ||''',
                        '   || UN_EVALUACION         ||',
                        '   || UN_CLASE              ||',
                        ''' || UN_TIPO               ||''',
                        ''' || RS.CRITERIO_EVALUADO  ||''','||
                        CASE UN_OPCION WHEN 1 THEN 
                        '''' || RS.TEXTO              ||''','
                        ELSE '' END ||'
                        ''CC'',
                        ''' || UN_CEDULA_EVALUADO    ||''',
                        ''' || RS.SUCURSAL_EVALUADO  ||''',
                        ''' || UN_CEDULA_EVALUADOR   ||''',
                        ''' || RS.SUCURSAL_EVALUADOR ||''',
                        ''' || RS.NOMBRECRITERIO     ||''','||
                         CASE UN_OPCION WHEN 2 THEN 
                        '''' || UN_TIPO              ||''','
                        ELSE '' END ||'
                        SYSDATE,
                        ''' || UN_USUARIO            ||'''';
          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    =>  MI_TABLA,
                                                      UN_ACCION   =>  'I', 
                                                      UN_CAMPOS   =>  MI_CAMPOS, 
                                                      UN_VALORES  =>  MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            END;
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
                MI_MSGERROR(1).CLAVE := 'ELEMENTO';
                MI_MSGERROR(1).VALOR := 'COMPETENCIAS';
                                PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD   => SQLCODE,
                                UN_ERROR_COD => PCK_ERRORES.ERR_HVINSEVIPLANMEJORA,
                                 UN_REEMPLAZOS  => MI_MSGERROR
                              );
          END; 
      END IF;
    END LOOP;

    FOR RS IN (SELECT  EV_COMPROMISOS_LABORALES.CONSECUTIVO, 
                       TO_CHAR(EV_COMPROMISOS_LABORALES.COMPROMISO) COMPROMISO,
                       EV_COMPROMISOS_LABORALES.SUCURSAL_EVALUADO,
                       EV_COMPROMISOS_LABORALES.SUCURSAL_EVALUADOR
                  FROM EV_COMPROMISOS_LABORALES
                  WHERE EV_COMPROMISOS_LABORALES.COMPANIA        = UN_COMPANIA
                  AND EV_COMPROMISOS_LABORALES.TIPO_EVALUACION   = UN_TIPO
                  AND EV_COMPROMISOS_LABORALES.CLASE_EVALUACION  = UN_CLASE
                  AND EV_COMPROMISOS_LABORALES.CEDULA_EVALUADO   = UN_CEDULA_EVALUADO
                  AND EV_COMPROMISOS_LABORALES.ANO               = UN_ANO) 
    LOOP
      IF UN_OPCION = 1 THEN 
        SELECT COUNT (1)
         INTO MI_EXISTE
          FROM EV_EVIDENCIAS
          WHERE COMPANIA        = UN_COMPANIA
          AND NUMERO_EVALUACION = UN_EVALUACION
          AND CLASE_EVALUACION  = UN_CLASE
          AND TIPO_EVALUACION   = UN_TIPO
          AND TIPO              = 'CL'
          AND CEDULA_EVALUADO   = UN_CEDULA_EVALUADO
          AND CEDULA_EVALUADOR  = UN_CEDULA_EVALUADOR
          AND CONSECUTIVO       = RS.CONSECUTIVO;
      ELSE
        SELECT COUNT (1)
         INTO MI_EXISTE
          FROM EV_ACCIONES_MEJORA_COMPORTAMEN
          WHERE COMPANIA        = UN_COMPANIA
          AND NUMERO_EVALUACION = UN_EVALUACION
          AND CLASE_EVALUACION  = UN_CLASE
          AND TIPO_EVALUACION   = UN_TIPO
          AND TIPO              = 'CL'
          AND CEDULA_EVALUADO   = UN_CEDULA_EVALUADO
          AND CEDULA_EVALUADOR  = UN_CEDULA_EVALUADOR
          AND CONSECUTIVO       = RS.CONSECUTIVO;
      END IF;  
      IF MI_EXISTE = 0 THEN    
          MI_VALORES := ''''|| UN_COMPANIA           ||''',
                        '   || UN_EVALUACION         ||',
                        '   || UN_CLASE              ||',
                        ''' || UN_TIPO               ||''',
                        ''' || RS.CONSECUTIVO        ||''',
                        '||
                        CASE UN_OPCION WHEN 1 THEN 
                         ''''','
                        ELSE '' END 
                        ||'
                        ''CL'',
                        ''' || UN_CEDULA_EVALUADO    ||''',
                        ''' || RS.SUCURSAL_EVALUADO  ||''',
                        ''' || UN_CEDULA_EVALUADOR   ||''',
                        ''' || RS.SUCURSAL_EVALUADOR ||''',
                        ''' || SUBSTR(RS.COMPROMISO, 0, 254) ||''','||
                         CASE UN_OPCION WHEN 2 THEN 
                        '''' || UN_TIPO              ||''','
                        ELSE '' END ||'
                        SYSDATE,
                        ''' || UN_USUARIO            ||'''';
          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    =>  MI_TABLA,
                                                      UN_ACCION   =>  'I', 
                                                      UN_CAMPOS   =>  MI_CAMPOS, 
                                                      UN_VALORES  =>  MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                  RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
            END;
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
                MI_MSGERROR(1).CLAVE := 'ELEMENTO';
                MI_MSGERROR(1).VALOR := 'COMPROMISOS';
                                PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD   => SQLCODE,
                                UN_ERROR_COD => PCK_ERRORES.ERR_HVINSEVIPLANMEJORA,
                                UN_REEMPLAZOS  => MI_MSGERROR
                              );  
          END;
      END IF;
    END LOOP;
END PR_HEREDAREVIDENCIAS;

PROCEDURE PR_HEREDAREVALUACION
/*
    NAME              : PR_HEREDAREVALUACION
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 13/09/2018
    TIME              : 09:00 AM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : PROCEDIMIENTO QUE GENERA LOS DETALLE DE EVALUACION SI YA SE REALIZO EN EL PRIMER SEMESTRE 

    PARAMETERS        :                         

    @NAME:  heredarEvaluacion
    @METHOD:  POST                     
*/
(
    UN_COMPANIA           IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_EVALUACION         IN  EV_COMPROMISOS_LABORALES.NUMERO_EVALUACION%TYPE,
    UN_CLASE              IN  EV_COMPROMISOS_LABORALES.CLASE_EVALUACION%TYPE,
    UN_ANO                IN  EV_COMPROMISOS_LABORALES.ANO%TYPE,
    UN_EVALUADO           IN  EV_COMPROMISOS_LABORALES.CEDULA_EVALUADO%TYPE,
    UN_SUCURSALEVALUADO   IN  EV_COMPROMISOS_LABORALES.SUCURSAL_EVALUADO%TYPE,
    UN_EVALUADOR          IN  EV_COMPROMISOS_LABORALES.CEDULA_EVALUADO%TYPE,
    UN_SUCURSALEVALUADOR  IN  EV_COMPROMISOS_LABORALES.SUCURSAL_EVALUADO%TYPE,
    UN_TIPO               IN  EV_COMPROMISOS_LABORALES.TIPO_EVALUACION%TYPE,
    UN_USUARIO            IN  PCK_SUBTIPOS.TI_USUARIO
)
AS
 MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
 MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
 MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
 MI_EXISTE             PCK_SUBTIPOS.TI_ENTERO;
BEGIN
  FOR RS IN (SELECT EV_DETALLE_EVALUACION.COMPANIA,
                    EV_DETALLE_EVALUACION.NUMERO_EVALUACION,
                    EV_DETALLE_EVALUACION.CLASE_EVALUACION,
                    EV_DETALLE_EVALUACION.TIPO_EVALUACION,
                    EV_DETALLE_EVALUACION.CEDULA_EVALUADO,
                    EV_DETALLE_EVALUACION.SUCURSAL_EVALUADO,
                    EV_DETALLE_EVALUACION.CEDULA_EVALUADOR,
                    EV_DETALLE_EVALUACION.SUCURSAL_EVALUADOR,
                    EV_DETALLE_EVALUACION.CARGO_EVALUADOR,
                    EV_DETALLE_EVALUACION.CARGO_EVALUADO,
                    EV_DETALLE_EVALUACION.FECHA,
                    EV_DETALLE_EVALUACION.HORA,
                    EV_DETALLE_EVALUACION.OBSERVACIONES,
                    EV_DETALLE_EVALUACION.CODIGO_EMPLEADO_EVALUADO,
                    EV_DETALLE_EVALUACION.CODIGO_EMPLEADO_EVALUADOR,
                    EV_DETALLE_EVALUACION.ESCALAFON_EVALUADO,
                    EV_DETALLE_EVALUACION.ESCALAFON_EVALUADOR
              FROM EV_DETALLE_EVALUACION
              INNER JOIN EV_EVALUACIONES
                ON EV_DETALLE_EVALUACION.COMPANIA           = EV_EVALUACIONES.COMPANIA
                AND EV_DETALLE_EVALUACION.NUMERO_EVALUACION = EV_EVALUACIONES.CONSECUTIVO
                AND EV_DETALLE_EVALUACION.CLASE_EVALUACION  = EV_EVALUACIONES.CLASE_EVALUACION
              WHERE EV_DETALLE_EVALUACION.COMPANIA            = UN_COMPANIA
                AND EV_DETALLE_EVALUACION.CLASE_EVALUACION    = UN_CLASE
                AND EV_DETALLE_EVALUACION.TIPO_EVALUACION     = UN_TIPO
                AND EV_DETALLE_EVALUACION.CEDULA_EVALUADOR    = UN_EVALUADOR
                AND EV_DETALLE_EVALUACION.SUCURSAL_EVALUADOR  = UN_SUCURSALEVALUADOR
                AND EV_EVALUACIONES.ANO                       = UN_ANO
                AND EV_EVALUACIONES.ACUMULADA IN (-1)
                AND EV_EVALUACIONES.PERIODO = 1
                AND EV_EVALUACIONES.CEDULA = '999999999999999999')
    LOOP
      SELECT COUNT(1) 
      INTO MI_EXISTE
      FROM EV_DETALLE_EVALUACION
      WHERE EV_DETALLE_EVALUACION.COMPANIA            = RS.COMPANIA
        AND EV_DETALLE_EVALUACION.NUMERO_EVALUACION   = UN_EVALUACION
        AND EV_DETALLE_EVALUACION.CLASE_EVALUACION    = RS.CLASE_EVALUACION
        AND EV_DETALLE_EVALUACION.TIPO_EVALUACION     = RS.TIPO_EVALUACION
        AND EV_DETALLE_EVALUACION.CEDULA_EVALUADOR    = RS.CEDULA_EVALUADOR
        AND EV_DETALLE_EVALUACION.SUCURSAL_EVALUADOR  = RS.SUCURSAL_EVALUADOR
        AND EV_DETALLE_EVALUACION.CEDULA_EVALUADO     = RS.CEDULA_EVALUADO
        AND EV_DETALLE_EVALUACION.SUCURSAL_EVALUADO   = RS.SUCURSAL_EVALUADO;

      IF MI_EXISTE = 0 THEN   
          MI_CAMPOS :=' COMPANIA,
                        NUMERO_EVALUACION,
                        CLASE_EVALUACION,
                        TIPO_EVALUACION,
                        CEDULA_EVALUADO,
                        SUCURSAL_EVALUADO,
                        CEDULA_EVALUADOR,
                        SUCURSAL_EVALUADOR,
                        CARGO_EVALUADOR,
                        CARGO_EVALUADO,
                        FECHA,
                        HORA,
                        OBSERVACIONES,
                        CODIGO_EMPLEADO_EVALUADO,
                        CODIGO_EMPLEADO_EVALUADOR,
                        ESCALAFON_EVALUADO,
                        ESCALAFON_EVALUADOR,
                        DATE_CREATED,
                        CREATED_BY
                        ';

           MI_VALORES:=''''|| RS.COMPANIA           ||''',
                        '  || UN_EVALUACION         ||',
                        '  || RS.CLASE_EVALUACION   ||',
                        '''|| RS.TIPO_EVALUACION    ||''',
                        '''|| RS.CEDULA_EVALUADO    ||''',
                        '''|| RS.SUCURSAL_EVALUADO  ||''',
                        '''|| RS.CEDULA_EVALUADOR   ||''',
                        '''|| RS.SUCURSAL_EVALUADOR ||''',
                        '''|| RS.CARGO_EVALUADOR    ||''',
                        '''|| RS.CARGO_EVALUADO     ||''',
                        SYSDATE,
                        TO_DATE('''|| '01/01/1970 '|| TO_CHAR( SYSDATE,'HH24:MM:SS')||''',''DD/MM/YYYY HH24:MI:SS''),
                        '''|| RS.OBSERVACIONES      ||''',
                        '''|| RS.CODIGO_EMPLEADO_EVALUADO||''',
                        '''|| RS.CODIGO_EMPLEADO_EVALUADOR||''',
                        '''|| RS.ESCALAFON_EVALUADO ||''',
                        '''|| RS.ESCALAFON_EVALUADOR||''',  
                        SYSDATE,
                        '''|| UN_USUARIO ||'''
                        ';
              BEGIN
                BEGIN
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => 'EV_DETALLE_EVALUACION', 
                                                          UN_ACCION   => 'I', 
                                                          UN_CAMPOS   => MI_CAMPOS, 
                                                          UN_VALORES  => MI_VALORES);
                EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
                END;
              EXCEPTION
              WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
                PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE,  
                                            UN_ERROR_COD => PCK_ERRORES.ERR_HVINSCOMPROMISOSACORD );
              END;

              FOR RS_SUB IN (SELECT  CRITERIO_EVALUADO,
                                     FECHA,
                                     HORA,
                                     GRUPO, 
                                     MOVIMIENTO,
                                     PUNTAJE,
                                     IND_TEXTO
                              FROM EV_SUBDETALLE_EVALUACION
                              WHERE COMPANIA            = UN_COMPANIA
                                AND EVALUACION          = RS.NUMERO_EVALUACION
                                AND CEDULA_EVALUADO     = RS.CEDULA_EVALUADO
                                AND SUCURSAL_EVALUADO   = RS.SUCURSAL_EVALUADO
                                AND CEDULA_EVALUADOR    = RS.CEDULA_EVALUADOR
                                AND SUCURSAL_EVALUADOR  = RS.SUCURSAL_EVALUADOR
                                AND CLASE_EVALUACION    = RS.CLASE_EVALUACION)
              LOOP
                     MI_CAMPOS :='COMPANIA,
                                  EVALUACION,
                                  CEDULA_EVALUADO,
                                  SUCURSAL_EVALUADO,
                                  CEDULA_EVALUADOR,
                                  SUCURSAL_EVALUADOR,
                                  CRITERIO_EVALUADO,
                                  CLASE_EVALUACION,
                                  CARGO_EVALUADOR,
                                  CARGO_EVALUADO,
                                  ESCALAFON_EVALUADOR,
                                  ESCALAFON_EVALUADO,
                                  TIPO_EVALUACION,
                                  FECHA,
                                  HORA,
                                  GRUPO,
                                  MOVIMIENTO,
                                  IND_TEXTO,
                                  DATE_CREATED,
                                  CREATED_BY
                                  ';

                   MI_VALORES:=''''|| UN_COMPANIA||''',
                                '  || UN_EVALUACION||',
                                '''|| RS.CEDULA_EVALUADO||''',
                                '''|| RS.SUCURSAL_EVALUADO||''',
                                '''|| RS.CEDULA_EVALUADOR||''',
                                '''|| RS.SUCURSAL_EVALUADOR||''',
                                '''|| RS_SUB.CRITERIO_EVALUADO||''', 
                                '  || RS.CLASE_EVALUACION||',
                                '''|| RS.CARGO_EVALUADOR||''',
                                '''|| RS.CARGO_EVALUADO||''',
                                '''|| RS.ESCALAFON_EVALUADOR||''',
                                '''|| RS.ESCALAFON_EVALUADO||''',
                                '''|| RS.TIPO_EVALUACION ||''',
                                SYSDATE,
                                TO_DATE('''|| '01/01/1970 '|| TO_CHAR( SYSDATE,'HH24:MM:SS')||''',''DD/MM/YYYY HH24:MI:SS''),
                                '  || RS_SUB.GRUPO  ||',
                                '  || RS_SUB.MOVIMIENTO  ||',
                                '  || RS_SUB.IND_TEXTO  ||',
                                SYSDATE,
                                '''|| UN_USUARIO ||'''
                                ';
                      BEGIN
                        BEGIN
                          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => 'EV_SUBDETALLE_EVALUACION', 
                                                                  UN_ACCION   => 'I', 
                                                                  UN_CAMPOS   => MI_CAMPOS, 
                                                                  UN_VALORES  => MI_VALORES);
                        EXCEPTION
                        WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
                        END;
                      EXCEPTION
                      WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD => SQLCODE,  
                                                    UN_ERROR_COD => PCK_ERRORES.ERR_HVINSCOMPROMISOSACORD );
                      END; 
              END LOOP;

                 MI_CAMPOS:= 'REG_DETALLE    = -1,
                              DATE_MODIFIED  = SYSDATE,
                              MODIFIED_BY    = '''||UN_USUARIO ||'''';

                 MI_CONDICION:= ' COMPANIA               = '''||UN_COMPANIA||'''
                                  AND NUMERO_EVALUACION  = '  ||UN_EVALUACION||'
                                  AND CLASE_EVALUACION   = '  ||RS.CLASE_EVALUACION||'
                                  AND TIPO_EVALUACION    = '''||RS.TIPO_EVALUACION||'''
                                  AND CEDULA_EVALUADO    = '''||RS.CEDULA_EVALUADO||'''
                                  AND SUCURSAL_EVALUADO  = '''||RS.SUCURSAL_EVALUADO||'''
                                  AND CEDULA_EVALUADOR   = '''||RS.CEDULA_EVALUADOR||'''
                                  AND SUCURSAL_EVALUADOR = '''||RS.SUCURSAL_EVALUADOR||'''';
                 BEGIN
                BEGIN
                      PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME ( UN_TABLA        => 'EV_DETALLE_EVALUACION',
                                                            UN_ACCION       => 'M',
                                                            UN_CAMPOS       => MI_CAMPOS,
                                                            UN_CONDICION    => MI_CONDICION);                                    
                        EXCEPTION
                             WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE  PCK_EXCEPCIONES.EXC_HOJAS_VIDA; 
                    END;
                       EXCEPTION
                             WHEN  PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN                    
                                   PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                                              UN_TABLAERROR  => 'EV_DETALLE_EVALUACION',
                                                              UN_ERROR_COD   => PCK_ERRORES.ERRR_HOJAS_VIDA_ACT_CONS);
                END;
        END IF;
    END LOOP;
END PR_HEREDAREVALUACION;

FUNCTION FC_CREARUTANEXOS
    /*
    NAME              : FC_CREARUTANEXOS 
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRATION    : 22/08/2019
    TIME              : 
    AUTHOR MODIFIED   : 
    DATE MODIFIED     : 
    SOURCE MODULE     : 
    DESCRIPTION       : Genera la ruta donde se guardara el archivo cargado a la aplicacion
    MODIFIED BY       : 

    @NAME  : crearRutaAnexos
    @METHOD  : GET
  */
(
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_MODULO       IN PCK_SUBTIPOS.TI_MODULO,
    UN_CEDULA       IN PCK_SUBTIPOS.TI_NRODOCUMENTO,
    UN_CODIGORUTA   IN NIVEL_AGRUPAMIENTO.CODIGO%TYPE
 )
RETURN VARCHAR2
AS 
  MI_RUTA           VARCHAR2(200 CHAR);
  MI_CARPETACEDULA  VARCHAR2(30 CHAR);
BEGIN
    <<CREAR_RUTA>>
   FOR MI_RS IN (SELECT CODIGO, 
                        NOMBRE,
                        DOMINIO
                FROM NIVEL_AGRUPAMIENTO
                WHERE COMPANIA = UN_COMPANIA
                  AND MODULO = UN_MODULO
                  AND CODIGO = SUBSTR(UN_CODIGORUTA,1,LENGTH(CODIGO))
                ORDER BY CODIGO) 
   LOOP

        IF MI_RS.NOMBRE = 'CEDULA_APELLIDOS' THEN            
            MI_CARPETACEDULA := UN_CEDULA;
            MI_RUTA := MI_RUTA || '/'|| MI_CARPETACEDULA;
        ELSE

        MI_RUTA := MI_RUTA || '/'|| MI_RS.NOMBRE;

        END IF;        

   END LOOP CREAR_RUTA;      

 RETURN MI_RUTA;
END FC_CREARUTANEXOS;


FUNCTION FC_GLOSADESCRIPCION
/*
    NAME              : FC_GLOSADESCRIPCION 
    AUTHORS           : STEFANINI SYSMAN
    DATE CREATED      : 24/07/2024
    TIME              : 08:40 am
    AUTHOR CREATED    : CRISTIAN Y JESUS 
    DESCRIPTION       : Regresa las glosas con su descripcion 
    MODIFIED BY       : 

    @NAME  : 
    @METHOD  : 
 */ 
(
        UN_COD_GEN_GLOSA   IN VARCHAR2,
        UN_COD_ESP_GLOSA   IN VARCHAR2,
        UN_ID_GLOSA        IN VARCHAR2,
        UN_NRO_FACTURA     IN VARCHAR2
)
RETURN clob
AS 
  MI_RTA        clob;

BEGIN
 
  BEGIN
     SELECT 
        LISTAGG(
            CM_CODIGOS_GLOSAS_FACTURA.ID_GLOSA ||'-'|| CM_CODIFICACION_GLOSAS.NOMBRE || ': ' || CM_CODIGOS_GLOSAS_FACTURA.OBSERVACIONES,
            CHR(10) || '-------------------' || CHR(10)
        ) WITHIN GROUP (ORDER BY CM_CODIFICACION_GLOSAS.NOMBRE)
        INTO  MI_RTA
       FROM  CM_CODIGOS_GLOSAS_FACTURA LEFT JOIN 
        CM_CODIFICACION_GLOSAS
        ON CM_CODIGOS_GLOSAS_FACTURA.COD_GEN_GLOSA = CM_CODIFICACION_GLOSAS.CODIGO_GENERAL
        AND CM_CODIGOS_GLOSAS_FACTURA.COD_ESP_GLOSA = CM_CODIFICACION_GLOSAS.CODIGO_ESPECIAL
        AND CM_CODIGOS_GLOSAS_FACTURA.ID_GLOSA = CM_CODIFICACION_GLOSAS.ID
        WHERE COD_GEN_GLOSA = UN_COD_GEN_GLOSA
        AND COD_ESP_GLOSA = UN_COD_ESP_GLOSA
        AND ID_GLOSA = UN_ID_GLOSA 
        AND CM_CODIGOS_GLOSAS_FACTURA.NUM_FACTURA = UN_NRO_FACTURA
        HAVING COUNT(CM_CODIFICACION_GLOSAS.NOMBRE) > 0;
    EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_RTA := '';
  END;

    MI_RTA :=  REPLACE(MI_RTA, '||', CHR(10) || '-------------------' || CHR(10));
   RETURN NVL(MI_RTA,CHR(10));
END FC_GLOSADESCRIPCION;

PROCEDURE PR_ACTUALIZAEXPLABORALPER
/*
    NAME              : PR_ACTUALIZAEXPLABORALPER
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 09/12/2025
    TIME              : 09:45 AM
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    DESCRIPTION       : Procedimiento que actualiza la experiencia laboral de NAT_DATOS_PERSONALES
                        dependiendo si es experiencia Publica,Privada o Independiente
    PARAMETERS        :
    --NAME:  actualizarExperienciaLaboral
    --METHOD:  POST
  */
  (
  UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_NUMERO_DCTO     IN NAT_DATOS_PERSONALES.NUMERO_DCTO%TYPE,
  UN_CODIGO_PER      IN NAT_DATOS_PERSONALES.CODIGO%TYPE,
  UN_SUCURSAL        IN NAT_DATOS_PERSONALES.SUCURSAL%TYPE,
  UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
  )
AS
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_ANOS_EXP             NAT_EXPERIENCIA_LABORAL.ANOSERVICIO%TYPE;
  MI_MESES_EXP            NAT_EXPERIENCIA_LABORAL.MESESERVICIO%TYPE;
  MI_DIAS_EXP             NAT_EXPERIENCIA_LABORAL.DIASERVICIO%TYPE;
BEGIN
    
    FOR MI_RS IN (SELECT CLASE, SUM(ANOSERVICIO) ANOSEXP, SUM(MESESERVICIO) MESESEXP, SUM(DIASERVICIO)DIASEXP 
                    FROM NAT_EXPERIENCIA_LABORAL
                   WHERE COMPANIA  = UN_COMPANIA
                     AND NUMERO_DCTO = UN_NUMERO_DCTO
                     AND NEL_CODIGOPERSONA = UN_CODIGO_PER
                     AND SUCURSAL = UN_SUCURSAL
                GROUP BY CLASE)
    LOOP
        MI_ANOS_EXP     := 0;
        MI_MESES_EXP    := 0;
        MI_DIAS_EXP     := 0;
        
        IF(MI_RS.DIASEXP >= 30)THEN
            MI_MESES_EXP :=TRUNC(MI_RS.DIASEXP/30);
            MI_DIAS_EXP  := MOD(MI_RS.DIASEXP,30);
        ELSE
            MI_DIAS_EXP := MI_RS.DIASEXP;
        END IF;
        
        MI_MESES_EXP := MI_RS.MESESEXP + MI_MESES_EXP;
        
        IF(MI_MESES_EXP >= 12)THEN
            MI_ANOS_EXP := TRUNC(MI_MESES_EXP/12);
            MI_MESES_EXP  := MOD(MI_MESES_EXP,12);    
        END IF;
        
        MI_ANOS_EXP := MI_RS.ANOSEXP + MI_ANOS_EXP;
        
        IF MI_RS.CLASE = '1' THEN
          MI_CAMPOS := 'ANOPB           = '|| MI_ANOS_EXP||'
                      ,MESPB           = '|| MI_MESES_EXP||'
                      ,DIASPB          = '|| MI_DIAS_EXP||'
                      ,MODIFIED_BY     = '''||UN_USUARIO||'''
                      ,DATE_MODIFIED   = SYSDATE';
          MI_CONDICION := 'COMPANIA      = ''' || UN_COMPANIA  ||'''
                        AND NUMERO_DCTO = ''' ||UN_NUMERO_DCTO||'''
                        AND CODIGO = ''' ||UN_CODIGO_PER||'''
                        AND SUCURSAL    = '''||UN_SUCURSAL||'''
                       ';
          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'NAT_DATOS_PERSONALES',
                                   UN_ACCION    =>  'M',
                                   UN_CAMPOS    =>  MI_CAMPOS,
                                   UN_CONDICION =>  MI_CONDICION );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
            END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
                MI_MSGERROR(1).CLAVE := 'CEDULA';
                MI_MSGERROR(1).VALOR := UN_NUMERO_DCTO;
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                              UN_EXC_COD   => SQLCODE,
                              UN_ERROR_COD => PCK_ERRORES.ERR_HVACTEXPPUBLICA,
                              UN_REEMPLAZOS  => MI_MSGERROR
                            );
          END;
        END IF;
        IF MI_RS.CLASE = '2' THEN
          MI_CAMPOS := 'ANOPV          = '|| MI_ANOS_EXP||'
                          ,MESPV           = '|| MI_MESES_EXP||'
                          ,DIASPV          = '|| MI_DIAS_EXP||'
                          ,MODIFIED_BY     = '''||UN_USUARIO||'''
                          ,DATE_MODIFIED   = SYSDATE';
          MI_CONDICION := 'COMPANIA      = ''' || UN_COMPANIA  ||'''
                        AND NUMERO_DCTO = ''' ||UN_NUMERO_DCTO||'''
                        AND CODIGO = ''' ||UN_CODIGO_PER||'''
                        AND SUCURSAL    = '''||UN_SUCURSAL||'''   ';
          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'NAT_DATOS_PERSONALES',
                                                           UN_ACCION    =>  'M',
                                                           UN_CAMPOS    =>  MI_CAMPOS,
                                                           UN_CONDICION =>  MI_CONDICION );
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
            END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
                    MI_MSGERROR(1).CLAVE := 'CEDULA';
                    MI_MSGERROR(1).VALOR := UN_NUMERO_DCTO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD   => SQLCODE,
                                                UN_ERROR_COD => PCK_ERRORES.ERR_HVACTEXPPRIVADA,
                                                UN_REEMPLAZOS  => MI_MSGERROR
                                                );
          END;
        END IF;
        IF MI_RS.CLASE = '3' THEN
          MI_CAMPOS := 'ANOTI         = '|| MI_ANOS_EXP||'
                         ,MESTI         = '|| MI_MESES_EXP||'
                         ,DIASTI        = '|| MI_DIAS_EXP||'
                         ,MODIFIED_BY   = '''||UN_USUARIO||'''
                         ,DATE_MODIFIED = SYSDATE';
          MI_CONDICION := 'COMPANIA      = ''' || UN_COMPANIA  ||'''
                        AND NUMERO_DCTO = ''' ||UN_NUMERO_DCTO||'''
                        AND CODIGO = ''' ||UN_CODIGO_PER||'''
                        AND SUCURSAL    = '''||UN_SUCURSAL||'''   ';
          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  'NAT_DATOS_PERSONALES',
                                                           UN_ACCION    =>  'M',
                                                           UN_CAMPOS    =>  MI_CAMPOS,
                                                           UN_CONDICION =>  MI_CONDICION );
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
            END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
                    MI_MSGERROR(1).CLAVE := 'CEDULA';
                    MI_MSGERROR(1).VALOR := UN_NUMERO_DCTO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                                UN_EXC_COD   => SQLCODE,
                                                UN_ERROR_COD => PCK_ERRORES.ERR_HVACTEXTINDEPEN,
                                                UN_REEMPLAZOS  => MI_MSGERROR
                                                );
          END;
        END IF;  
    END LOOP;  
  END PR_ACTUALIZAEXPLABORALPER;

PROCEDURE PR_FONDOS_HV_NOM
 /*
    NAME              : PR_FONDOS_HV_NOM
    AUTHORS           : SYSMAN S.A.S
    AUTHOR MIGRATION  : 
    DATE MIGRATION    : 
    TIME              : 
    SOURCE MODULE     : HOJAS DE VIDA
    DESCRIPTION       : Actualiza los fondos configurados desde el modulo hojas de vida al modulo de nomina.
    MODIFIED BY       : 

    @NAME  : actualizarFondosHV
 */
(
  UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ID_DE_EMPLEADO        IN PERSONAL.ID_DE_EMPLEADO%TYPE
)
AS 
  MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
  MI_MERGEUSING         PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE        PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE        PCK_SUBTIPOS.TI_MERGEEXISTE;
BEGIN
  BEGIN
    MI_TABLA := 'PERSONAL';
    MI_MERGEUSING := 'SELECT COMPANIA,
                        ID_DE_EMPLEADO,
                        MAX(CASE WHEN SS_REGIMEN = ''PENSION'' THEN SS_ENTIDAD END) AS FONDO_PENSION,
                        MAX(CASE WHEN SS_REGIMEN = ''PENSION'' THEN SS_FECHVINC END) AS FECHA_PENSION,
                        MAX(CASE WHEN SS_REGIMEN = ''SALUD'' THEN SS_ENTIDAD END) AS FONDO_SALUD,
                        MAX(CASE WHEN SS_REGIMEN = ''SALUD'' THEN SS_FECHVINC END) AS FECHA_SALUD,
                        MAX(CASE WHEN SS_REGIMEN = ''FONDO DE CESANTIAS'' THEN SS_ENTIDAD END) AS FONDO_CESANTIAS,
                        MAX(CASE WHEN SS_REGIMEN = ''FONDO DE CESANTIAS'' THEN SS_FECHVINC END) AS FECHA_CESANTIAS,
                        MAX(CASE WHEN SS_REGIMEN = ''CAJA DE COMPENSACION'' THEN SS_ENTIDAD END) AS CAJA_COMPENSACION,
                        MAX(CASE WHEN SS_REGIMEN = ''CAJA DE COMPENSACION'' THEN SS_FECHVINC END) AS FECHA_CAJA,
                        MAX(CASE WHEN SS_REGIMEN = ''RIESGOS'' THEN SS_ENTIDAD END) AS FONDO_RIESGOS,
                        MAX(CASE WHEN SS_REGIMEN = ''RIESGOS'' THEN SS_FECHVINC END) AS FECHA_RIESGOS
                    FROM NAT_SEGURIDAD_SOCIAL
                    WHERE COMPANIA = '''|| UN_COMPANIA ||'''
                      AND ID_DE_EMPLEADO = '|| UN_ID_DE_EMPLEADO ||'
                    GROUP BY COMPANIA, ID_DE_EMPLEADO';
    MI_MERGEENLACE := ' TABLA.COMPANIA = VISTA.COMPANIA
                    AND TABLA.ID_DE_EMPLEADO = VISTA.ID_DE_EMPLEADO';
    MI_MERGEEXISTE := 'UPDATE SET
                        TABLA.ID_DEL_FONDO          = NVL(VISTA.FONDO_PENSION, TABLA.ID_DEL_FONDO),
                        TABLA.FECHAFONDOPENSION     = NVL(VISTA.FECHA_PENSION, TABLA.FECHAFONDOPENSION),
                        TABLA.FONDO_SALUD           = NVL(VISTA.FONDO_SALUD, TABLA.FONDO_SALUD),
                        TABLA.FECHAFONDOSALUD       = NVL(VISTA.FECHA_SALUD, TABLA.FECHAFONDOSALUD),
                        TABLA.FONDO_CESANTIAS       = NVL(VISTA.FONDO_CESANTIAS, TABLA.FONDO_CESANTIAS),
                        TABLA.FECHAFONDOCESANTIA    = NVL(VISTA.FECHA_CESANTIAS, TABLA.FECHAFONDOCESANTIA),
                        TABLA.CAJA_COMPENSACION     = NVL(VISTA.CAJA_COMPENSACION, TABLA.CAJA_COMPENSACION),
                        TABLA.FECHACAJACOMPENSACION = NVL(VISTA.FECHA_CAJA, TABLA.FECHACAJACOMPENSACION),
                        TABLA.FONDO_RIESGOS         = NVL(VISTA.FONDO_RIESGOS, TABLA.FONDO_RIESGOS),
                        TABLA.FECHAFONDORIESGOS     = NVL(VISTA.FECHA_RIESGOS, TABLA.FECHAFONDORIESGOS)';
    BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA
                                            ,UN_ACCION      => 'MM'
                                            ,UN_MERGEUSING  => MI_MERGEUSING
                                            ,UN_MERGEENLACE => MI_MERGEENLACE
                                            ,UN_MERGEEXISTE => MI_MERGEEXISTE);
      
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
      RAISE PCK_EXCEPCIONES.EXC_HOJAS_VIDA;
    END;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_HOJAS_VIDA THEN
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                              ,UN_ERROR_COD => PCK_ERRORES.ERR_HVMERGEFONDOSNOMINA);
  END;
END PR_FONDOS_HV_NOM;

END PCK_HOJAS_DE_VIDA;