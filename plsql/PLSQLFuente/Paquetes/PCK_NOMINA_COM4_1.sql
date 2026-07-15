create or replace PACKAGE BODY "PCK_NOMINA_COM4" AS

  --02
  FUNCTION FC_BORRAREMBARGOS  
  /*
    NAME              : FC_BORRAREMBARGOS  --> EN ACCESS BorrarEmbargos
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRATION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRADOR     : 27/07/2015
    TIME              : 09:00 AM
    MODIFIED BY       : JULIO CESAR REINA PANCHE /
                        (05/09/2017) PABLO ANDRES ESPITIA CUCA /

    DATE MODIFIED     : 22/03/2017
    TIME              : 12:00 PM
    MODIFICATIONS     : CORRECCIÃ“N SEGÃšN ESTÃ�NDAR DE PROGRAMACIÃ“N /
                        (05/09/2017) Aplicar estandar de programacion PL. 
                                     Manejo de excepciones.  /                                  
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : Eliminar las Novedades de Embargos de todo un periodo
    PARAMETERS        : UN_PROCESO  => ID DE PROCESO POR EL QUE SE FILTRAN LOS PERIODOS
                        UN_MES      => MES POR EL QUE SE FILTRA LOS PERIODOS
                        UN_ANIO     => ANIO POR EL QUE SE FILTRA LOS PERIODOS
                        UN_PERIODO  => PERIODO POR EL QUE SE FILTRA LOS PERIODOS
                        UN_COMPANIA  => COMPANIA EN LA QUE SE ESTÃ� TRABAJANDO.

    @NAME:    borrarEmbargos 
    @METHOD:  GET
  */
  (
    UN_PROCESO  IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_MES      IN PCK_SUBTIPOS.TI_MES,
    UN_ANIO     IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
  )
  RETURN PCK_SUBTIPOS.TI_LOGICO
  AS
    MI_RESULTADO PCK_SUBTIPOS.TI_LOGICO; 
    MI_CONDICION PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR  PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA_P   PCK_SUBTIPOS.TI_TABLA DEFAULT 'PERIODOS'; /*PERIODOS*/
    MI_TABLA_E   PCK_SUBTIPOS.TI_TABLA DEFAULT 'EMBARGOS'; /*EMBARGOS*/
  BEGIN
    BEGIN
      BEGIN
        SELECT ESTADO 
        INTO MI_RESULTADO
        FROM PERIODOS
        WHERE COMPANIA      = UN_COMPANIA
          AND ID_DE_PROCESO = UN_PROCESO
          AND ANO           = UN_ANIO
          AND MES           = UN_MES
          AND PERIODO       = UN_PERIODO;    

      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
      MI_MSGERROR(1).CLAVE := 'PROCESO';
      MI_MSGERROR(1).VALOR := UN_PROCESO;
      MI_MSGERROR(2).CLAVE := 'ANIO';
      MI_MSGERROR(2).VALOR := UN_ANIO;
      MI_MSGERROR(3).CLAVE := 'MES';
      MI_MSGERROR(3).VALOR := UN_MES;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_N_NDF_FBE_ESTADOPERIODO
                                ,UN_TABLAERROR => MI_TABLA_P
                                ,UN_REEMPLAZOS => MI_MSGERROR);
    END;

    MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA||'''
                 AND ID_DE_PROCESO =   '||UN_PROCESO ||'
                 AND ANO           =   '||UN_ANIO    ||' 
                 AND MES           =   '||UN_MES     ||'
                 AND PERIODO       =   '||UN_PERIODO;

    IF MI_RESULTADO NOT IN(0) THEN
      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_E
                                               ,UN_ACCION    => 'E'
                                               ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
          RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN    
        MI_MSGERROR(1).CLAVE := 'PERIODO';
        MI_MSGERROR(1).VALOR := UN_PERIODO;
        MI_MSGERROR(2).CLAVE := 'ANIO';
        MI_MSGERROR(2).VALOR := UN_ANIO;
        MI_MSGERROR(3).CLAVE := 'MES';
        MI_MSGERROR(3).VALOR := UN_MES;      

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_N_FBE_E_EMBARGOPORPERIODO
                                  ,UN_TABLAERROR => MI_TABLA_E
                                  ,UN_REEMPLAZOS => MI_MSGERROR);
      END;    
    END IF;

    RETURN MI_RESULTADO;
  END FC_BORRAREMBARGOS;

  --03
  FUNCTION FC_NOMJUZGADO
  /*
    NAME              : FC_NOMJUZGADO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRATION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRATION    : 29/07/2015
    TIME              : 09:00 AM
    MODIFIER          : JULIO CESAR REINA PANCHE /
                        (06/09/2017) PABLO ANDRES ESPITIA CUCA

    DATE MODIFIED     : 22/03/2017
    TIME              : 02:00 PM
    MODIFICATIONS     : CORRECCIÃ“N SEGÃšN ESTÃ�NDAR DE PROGRAMACIÃ“N Y OPTIMIZACIÃ“N.
                        (06/09/2017) Aplicar estandar de programacion PL.
                                     Manejo de excepciones. /

    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : RETORNA El nombre de un juzgado mediante su id.
    PARAMETERS        : UN_COMPANIA  => COMPANIA EN LA QUE SE ESTÃ� TRABAJANDO.
                        UN_IDJUZGADO => ID POR EL QUE SE FILTRAN LOS JUZGADOS

    @NAME:    nombreJuzgado 
    @METHOD:  GET
  */
  (
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_IDJUZGADO IN JUZGADOS.ID_JUZGADO%TYPE
  )
  RETURN VARCHAR2
  AS
    MI_VLRETORNO JUZGADOS.NOMBRE%TYPE := '';
    MI_MSGERROR  PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    BEGIN
      BEGIN 
        SELECT NOMBRE 
        INTO MI_VLRETORNO 
        FROM JUZGADOS
        WHERE COMPANIA   = UN_COMPANIA
          AND ID_JUZGADO = UN_IDJUZGADO;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;     
      END; 

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
      MI_MSGERROR(1).CLAVE := 'CODIGO';
      MI_MSGERROR(1).VALOR := UN_IDJUZGADO;

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_N_FNJ_NDF_VERIFICAJUZGADO
                                ,UN_TABLAERROR => 'JUZGADOS'
                                ,UN_REEMPLAZOS => MI_MSGERROR);
    END;

    RETURN MI_VLRETORNO;
  END FC_NOMJUZGADO;

  --5
  FUNCTION FC_CONCEPTOEQUIVALENTE
  /*
    NAME              : FC_CONCEPTOEQUIVALENTE --> EN ACCESS conceptoEquivalente
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRATION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRATION    : 03/09/2015
    TIME              : 10:00 AM
    MODIFIER          : JULIO CESAR REINA PANCHE /
                        (06/09/2017) PABLO ANDRES ESPITIA CUCA /
    DATE MODIFIED     : 22/03/2017
    TIME              : 05:00 PM
    MODIFICATIONS     : CORRECCIÃ“N SEGÃšN ESTÃ�NDAR DE PROGRAMACIÃ“N Y OPTIMIZACIÃ“N DEL MANEJO DE ERRORES. /
                        (06/09/2017) Aplicar estandar de programacion PLSQL.

    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : Retorna el concepto equivalente
    PARAMETERS        : 
                        UN_COMPANIA       => COMPANIA EN LA QUE SE ESTÃ� TRABAJANDO.
                        UN_ID_DE_CONCEPTO => ID DE CONCEPTO POR EL CUAL SE FILTRAN LOS CONCEPTOS

    @NAME:    conceptoEquivalente 
    @METHOD:  GET
  */
  (
    UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ID_DE_CONCEPTO IN  CONCEPTOS.CODSISE%TYPE
  )
  RETURN PCK_SUBTIPOS.TI_ENTERO
  AS
    MI_RTA PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  BEGIN
    <<IDCONCEPTOS>>
    FOR MI_ID IN (SELECT ID_DE_CONCEPTO 
                  FROM CONCEPTOS 
                  WHERE COMPANIA = UN_COMPANIA
                    AND CODSISE  = UN_ID_DE_CONCEPTO) 
    LOOP 
      IF MI_ID.ID_DE_CONCEPTO IS NOT NULL THEN
          MI_RTA:= MI_ID.ID_DE_CONCEPTO;
       ELSE
          MI_RTA:= 0;
       END IF;
    END LOOP IDCONCEPTOS;

    RETURN MI_RTA;
  END FC_CONCEPTOEQUIVALENTE;

  --6
  FUNCTION FC_SUBIRNOVEDADEXCEL
  /*
    NAME              : FC_SUBIRNOVEDADEXCEL --> EN ACCESS SubirNovedadExcel
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRATION  : NICOLAS GOMEZ BARBOSA
    DATE MIGRATION    : 08/09/2015
    TIME              : 10:00 AM
    MODIFIER          : JULIO CESAR REINA PANCHE /
                        (06/09/2017) PABLO ANDRES ESPITIA CUCA
    DATE MODIFIED     : 22/03/2017
    TIME              : 05:30 PM
    MODIFICATIONS     : CORRECCIÃ“N SEGÃšN ESTÃ�NDAR DE PROGRAMACIÃ“N Y OPTIMIZACIÃ“N DEL MANEJO DE ERRORES. /
                        (06/09/2017) Aplicar estandar de programacion PLSQL.
                                     Adicion de campos de auditoria.
    SOURCE MODULE     : NOMINAP2015.04.02 UNIFICADAS MPV 11052015_MPV - 185  - WEB para migrar final  4TO envio.accdb
    DESCRIPTION       : Se usa para actualizar la tabla Novedades dependiendo del archivo Excel seleccionado en el Bean
    PARAMETERS        : UN_COMPANIA   => COMPANIA EN LA QUE SE ESTÃ� TRABAJANDO.
                        UN_PROCESO    => ID DE CONCEPTO POR EL CUAL SE FILTRAN LOS CONCEPTOS
                        UN_ANO        => ANIO EN EL QUE SE VA A INCLUIR LA NOVEDAD
                        UN_MES        => MES EN EL QUE SE VA A INCLUIR LA NOVEDAD
                        UN_CONCEPTO   => CONCEPTO EN EL QUE SE VA A INCLUIR LA NOVEDAD
                        UN_CODIGO     => ID EMPLEADO EN EL QUE SE VA A INCLUIR LA NOVEDAD
                        UN_VALOR      => VALOR POR EL CUAL SE VA A INCLUIR LA NOVEDAD
                        UN_PERIODO    => PERIODO EN EL QUE SE VA A INCLUIR LA NOVEDAD
                        UN_TIPO       => PARAMETRO POR EL CUAL SE DEFINE COMO INCLUIR LA NOVEDAD
                        UN_USUARIO    => Codigo del usuario que desencadena el proceso.

    @NAME:    subirNovedadExcel 
    @METHOD:  GET
  */
  (
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO      IN  PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANO          IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MES          IN  PCK_SUBTIPOS.TI_MES,
    UN_CONCEPTO     IN  PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
    UN_CODIGO       IN  PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_VALOR        IN  NOVEDADES.VALOR%TYPE,
    UN_PERIODO      IN  PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_TIPO         IN  VARCHAR2,
    UN_USUARIO      IN  PCK_SUBTIPOS.TI_USUARIO,
    UN_NSIAUE       IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 0
  )
  RETURN PCK_SUBTIPOS.TI_LOGICO
  AS
    MI_AUX2       PCK_SUBTIPOS.TI_ENTERO;
    MI_VALOR      PCK_SUBTIPOS.TI_DOBLE;
    MI_DECIMALES  PCK_SUBTIPOS.TI_DOBLE;
    MI_RTA        PCK_SUBTIPOS.TI_LOGICO := -1; 
    MI_PARTIPO    PCK_SUBTIPOS.TI_PARAMETRO;   /*Contiene el valor del parametro: TIPO DE NOMINA ACTIVOS O PENSIONADOS*/
    MI_PARGRABAR  PCK_SUBTIPOS.TI_PARAMETRO;   /*Contiene el valor del parametro: GRABAR NOVEDADES CON CODIGO DE EQUIVALENCIA*/
    MI_IDCONCEPTO CONCEPTOS.ID_DE_CONCEPTO%TYPE;
    MI_VALOR_AUX  PCK_SUBTIPOS.TI_DOBLE;
    MI_CONT       PCK_SUBTIPOS.TI_DOBLE;
  BEGIN

-- AFCP 7746969 SUBIR NOVEDADES AL RETROACTIVO
    IF UN_PROCESO = 10 THEN
        IF UN_CODIGO <> 0 THEN
            SELECT COUNT(*) 
            INTO MI_AUX2
            FROM PERSONAL 
            WHERE COMPANIA = UN_COMPANIA 
              AND ID_DE_EMPLEADO = UN_CODIGO;
        END IF; 
    ELSE
        IF UN_CODIGO <> 0 THEN -- jm 7737101 
            SELECT COUNT(*) 
            INTO MI_AUX2
            FROM PERSONAL 
            WHERE COMPANIA = UN_COMPANIA 
              AND ID_DE_EMPLEADO = UN_CODIGO;
              /*AND ESTADO_ACTUAL <> 3*/
        END IF; -- jm 7737101 
    END IF;
-- FIN AFCP 7746969

    IF MI_AUX2 <> 0 THEN  
      MI_PARTIPO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                         ,UN_NOMBRE     => 'TIPO DE NOMINA ACTIVOS O PENSIONADOS'
                                         ,UN_MODULO     => PCK_DATOS.FC_MODULONOMINA
                                         ,UN_FECHA_PAR  => SYSDATE);

      MI_PARGRABAR := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                           ,UN_NOMBRE     => 'GRABAR NOVEDADES CON CODIGO DE EQUIVALENCIA'
                                           ,UN_MODULO     => PCK_DATOS.FC_MODULONOMINA
                                           ,UN_FECHA_PAR  => SYSDATE)
                         ,'NO');                                   

      MI_VALOR := TRUNC(UN_VALOR);

      MI_DECIMALES := PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     => (UN_VALOR - MI_VALOR)
                                             ,UN_PRECISION => 2);

      IF MI_PARTIPO IN('PENSIONADOS') THEN
        IF MI_PARGRABAR IN('SI') THEN
          MI_IDCONCEPTO := PCK_NOMINA_COM4.FC_CONCEPTOEQUIVALENTE(UN_COMPANIA       => UN_COMPANIA
                                                                 ,UN_ID_DE_CONCEPTO => UN_CONCEPTO);
        ELSE
          MI_IDCONCEPTO := UN_CONCEPTO;
        END IF;

        MI_VALOR_AUX := (MI_VALOR + (MI_DECIMALES*0.01));

      ELSE
        IF MI_PARGRABAR IN('SI') THEN
          IF UN_TIPO = '12' THEN  
            MI_IDCONCEPTO := UN_CONCEPTO;
          ELSIF UN_TIPO = '01' THEN  
            MI_IDCONCEPTO := PCK_NOMINA_COM1.FC_RELACIONADO(UN_COMPANIA => UN_COMPANIA
                                                           ,UN_CONCEPTO => UN_CONCEPTO);
          ELSE
            MI_IDCONCEPTO := PCK_NOMINA_COM4.FC_CONCEPTOEQUIVALENTE(UN_COMPANIA        => UN_COMPANIA
                                                                   ,UN_ID_DE_CONCEPTO  => UN_CONCEPTO);
          END IF;

          MI_VALOR_AUX := (MI_VALOR + (MI_DECIMALES*0.01));

        ELSE
          MI_IDCONCEPTO := UN_CONCEPTO;
          MI_VALOR_AUX := (MI_VALOR + MI_DECIMALES);
        END IF;                       
      END IF;
      
       -- TICKET 7739968 EFCM: VALIDACION DE LA BANDERA DE CONCEPTOS QUE PERMITEN SUBIR NOVEDADES
      BEGIN
            SELECT    DISTINCT 1
            INTO      MI_CONT     
            FROM      CONCEPTOS
            WHERE     COMPANIA = UN_COMPANIA
                      AND ID_DE_CONCEPTO = MI_IDCONCEPTO
                      AND PERMITE_SUBIR_NOVEDAD != 0;

            IF UN_NSIAUE = 0 THEN  
              PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA    => UN_COMPANIA
                                      ,UN_PROCESO     => UN_PROCESO
                                      ,UN_ANIO        => UN_ANO
                                      ,UN_MES         => UN_MES
                                      ,UN_PERIODO     => UN_PERIODO
                                      ,UN_IDEMPLEADO  => UN_CODIGO
                                      ,UN_IDCONCEPTO  => MI_IDCONCEPTO
                                      ,UN_VALOR       => MI_VALOR_AUX
                                      ,UN_USER        => UN_USUARIO);
          ELSE 
        
              PCK_NOMINA_COM3.PR_INCLUIRNOVEDADSAUE(UN_COMPANIA    => UN_COMPANIA
                                      ,UN_IDEMPLEADO  => UN_CODIGO
                                      ,UN_IDCONCEPTO  => MI_IDCONCEPTO
                                      ,UN_VALOR       => MI_VALOR_AUX
                                      ,UN_OBSERV      => 'Novedad subida desde plantilla por: ' ||UN_USUARIO
                                      ,UN_USER        => UN_USUARIO);
        
        END IF;

    EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_RTA := 0;
          RAISE PCK_EXCEPCIONES.EXC_NOMINA;
      END;
      -- TICKET 7739968 FIN                                  
    ELSE
      MI_RTA := 0;
    END IF;        

    RETURN MI_RTA;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
    RAISE_APPLICATION_ERROR(-20099,'EL CONCEPTO NO ESTA CONFIGURADO PARA PERMITIR SUBIR NOVEDADES',FALSE);  
  END FC_SUBIRNOVEDADEXCEL;

  --7
  PROCEDURE PR_CREAR_FONDOS_ACTUALES2
  /*
    NAME              : FC_PERIODO
    AUTHORS           : SYSMAN  SAS / NicolÃƒÂ¡s GÃƒÂ³mez Barbosa
    DATE              : 29/12/2015
    TIME              : 3:53 PM
    MODIFIER          : JULIO CESAR REINA PANCHE
                        (06/09/2017) PABLO ANDRES ESPITIA CUCA /
    DATE MODIFIED     : 22/03/2017
    TIME              : 05:30 PM
    MODIFICATIONS     : CORRECCIÃ“N SEGÃšN ESTÃ�NDAR DE PROGRAMACIÃ“N Y OPTIMIZACIÃ“N DEL MANEJO DE ERRORES. /
                        (06/09/2017) Aplicar estandar de programacion PLSQL.
                                     Adicion de campos de auditoria.
    DESCRIPTION       : Actualiza los fondos actuales para cada empleado tomados de personal en la tabla cambiosdefondo, en caso de que ya existan no los inserta                         
    PARAMETERS        : UN_COMPANIA   => COMPANIA EN LA QUE SE ESTÃ� TRABAJANDO.

    @NAME:    crearNovedadesActuales 
    @METHOD:  GET
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO 
  )
  AS
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_USING        PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE  PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGENOEXIS  PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_TABLA_C      PCK_SUBTIPOS.TI_TABLA DEFAULT 'CAMBIOSDEFONDO'; /*Tabla: CAMBIOSDEFONDO*/
  BEGIN
    --AFP
    MI_USING := 'SELECT 
                    COMPANIA
                   ,ID_DE_EMPLEADO
                   ,''AFP'' TIPOFONDO
                   ,ID_DEL_FONDO
                   ,NVL(FECHAFONDOPENSION,FECHA_DE_INGRESO) FECHA_FONDO_PENSION
                   ,FECHATERCONTRATO
                 FROM PERSONAL 
                 WHERE COMPANIA = '''||UN_COMPANIA||'''
                   AND ID_DE_EMPLEADO NOT IN(0)';

    MI_MERGEENLACE := 'TABLA.COMPANIA       = VISTA.COMPANIA
                   AND TABLA.ID_DE_EMPLEADO = VISTA.ID_DE_EMPLEADO
                   AND TABLA.TIPOFONDO      = VISTA.TIPOFONDO
                   AND TABLA.FECHAINICIAL   = VISTA.FECHA_FONDO_PENSION';

    MI_CAMPOS := 'COMPANIA
                 ,ID_DE_EMPLEADO
                 ,TIPOFONDO
                 ,FONDOACTUAL
                 ,FECHAINICIAL
                 ,FECHAFINAL
                 ,DATE_CREATED
                 ,CREATED_BY';

    MI_VALORES := ''''||UN_COMPANIA||'''
                  ,VISTA.ID_DE_EMPLEADO
                  ,VISTA.TIPOFONDO
                  ,VISTA.ID_DEL_FONDO
                  ,VISTA.FECHA_FONDO_PENSION  
                  ,VISTA.FECHATERCONTRATO 
                  ,SYSDATE
                  ,'''||UN_USUARIO||'''';

    MI_MERGENOEXIS := ' INSERT( '|| MI_CAMPOS ||') VALUES(' || MI_VALORES || ')';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA        => MI_TABLA_C
                                              ,UN_ACCION       => 'IN'
                                              ,UN_MERGEUSING   => MI_USING
                                              ,UN_MERGEENLACE  => MI_MERGEENLACE
                                              ,UN_MERGENOEXIS  => MI_MERGENOEXIS);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ER_NOMINA_UPDATE_FONDOAFP
                                ,UN_TABLAERROR => MI_TABLA_C);
    END;   

    --EPS
    MI_USING := 'SELECT 
                   COMPANIA
                   ,ID_DE_EMPLEADO
                   ,''EPS'' TIPOFONDO
                   ,FONDO_SALUD
                   ,NVL(FECHAFONDOSALUD,FECHA_DE_INGRESO) FECHA_FONDO_SALUD
                   ,FECHATERCONTRATO
                 FROM PERSONAL 
                 WHERE COMPANIA = '''||UN_COMPANIA||'''
                   AND ID_DE_EMPLEADO NOT IN(0)';

    MI_MERGEENLACE := 'TABLA.COMPANIA         = VISTA.COMPANIA
                   AND TABLA.ID_DE_EMPLEADO   = VISTA.ID_DE_EMPLEADO
                   AND TABLA.TIPOFONDO        = VISTA.TIPOFONDO
                   AND TABLA.FECHAINICIAL     = VISTA.FECHA_FONDO_SALUD';

    MI_CAMPOS := 'COMPANIA
                 ,ID_DE_EMPLEADO
                 ,TIPOFONDO
                 ,FONDOACTUAL
                 ,FECHAINICIAL
                 ,FECHAFINAL
                 ,CREATED_BY
                 ,DATE_CREATED';

    MI_VALORES := ''''||UN_COMPANIA||'''
                  ,VISTA.ID_DE_EMPLEADO 
                  ,VISTA.TIPOFONDO
                  ,VISTA.FONDO_SALUD
                  ,VISTA.FECHA_FONDO_SALUD
                  ,VISTA.FECHATERCONTRATO
                  ,'''||UN_USUARIO ||'''
                  ,SYSDATE';

    MI_MERGENOEXIS := ' INSERT( '|| MI_CAMPOS ||') VALUES(' || MI_VALORES || ')';

    BEGIN
      BEGIN 
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA_C
                                              ,UN_ACCION      => 'IN'
                                              ,UN_MERGEUSING  => MI_USING
                                              ,UN_MERGEENLACE => MI_MERGEENLACE
                                              ,UN_MERGENOEXIS => MI_MERGENOEXIS);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ER_NOMINA_UPDATE_FONDOEPS
                                ,UN_TABLAERROR => MI_TABLA_C);
    END;   

    --ARL
    MI_USING := 'SELECT 
                    COMPANIA
                   ,ID_DE_EMPLEADO
                   ,''ARL'' TIPOFONDO
                   ,FONDO_RIESGOS
                   ,NVL(FECHAFONDORIESGOS,FECHA_DE_INGRESO) FECHA_FONDO_RIESGOS
                   ,FECHATERCONTRATO
                 FROM PERSONAL 
                 WHERE COMPANIA = '''||UN_COMPANIA||'''
                   AND ID_DE_EMPLEADO NOT IN(0)';

    MI_MERGEENLACE := 'TABLA.COMPANIA         = VISTA.COMPANIA
                   AND TABLA.ID_DE_EMPLEADO   = VISTA.ID_DE_EMPLEADO
                   AND TABLA.TIPOFONDO        = VISTA.TIPOFONDO
                   AND TABLA.FECHAINICIAL     = VISTA.FECHA_FONDO_RIESGOS';

    MI_CAMPOS := 'COMPANIA
                 ,ID_DE_EMPLEADO
                 ,TIPOFONDO
                 ,FONDOACTUAL
                 ,FECHAINICIAL
                 ,FECHAFINAL
                 ,CREATED_BY
                 ,DATE_CREATED';

    MI_VALORES := ''''||UN_COMPANIA||'''
                  ,VISTA.ID_DE_EMPLEADO 
                  ,VISTA.TIPOFONDO
                  ,VISTA.FONDO_RIESGOS
                  ,VISTA.FECHA_FONDO_RIESGOS  
                  ,VISTA.FECHATERCONTRATO 
                  ,'''||UN_USUARIO ||'''
                  ,SYSDATE';

    MI_MERGENOEXIS := ' INSERT( '|| MI_CAMPOS ||') VALUES(' || MI_VALORES || ')';

    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA_C
                                              ,UN_ACCION      => 'IN'
                                              ,UN_MERGEUSING  => MI_USING
                                              ,UN_MERGEENLACE => MI_MERGEENLACE
                                              ,UN_MERGENOEXIS => MI_MERGENOEXIS);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN    
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ER_NOMINA_UPDATE_FONDOARL
                                  ,UN_TABLAERROR => MI_TABLA_C);
    END;  
  END PR_CREAR_FONDOS_ACTUALES2;

FUNCTION FC_DISCOBANCOAGRARIOGOBNARINO
  /*
      NAME              : FC_DISCOBANCOAGRARIO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
      DATE MIGRADOR     : 23/08/2018
      TIME              : 8:50 AM
      SOURCE MODULE     : NOMINAP2018.07.04_UNIFICADAS MPV 13072018_MPV
      MODIFIER          : ERIKA SANCHEZ SEPULVEDA
      DATE MODIFIED     : 07/07/2021
      TIME              : 
      DESCRIPTION       : PERMITE GENERAR ARCHIVOS PLANOS PARA LA EMISIÃ“N MASIVA DE LOS DEPOSITOS JUDICIALES BANCO AGRARIO
      PARAMETERS        : 
      MODIFICATIONS     : SE CREA UNA NUEVA VERSIÃ“N DE ESTA FUNCION PARA GOB DE NARINO TENIENDO EN CUENTA LA DIFERENCIA EN LA CONSULTA
     @NAME: discoBancoAgrarioGobNarino
    */
(
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO          IN HISTORICOS.ID_DE_PROCESO%TYPE,
    UN_ANIO             IN HISTORICOS.ANO%TYPE,
    UN_MES              IN HISTORICOS.MES%TYPE,
    UN_PERIODO          IN HISTORICOS.PERIODO%TYPE,
    UN_BANCO            IN BANCO.BANCO%TYPE,    
    UN_FECHAREPORTE     IN DATE,            
    UN_OFICINAORIGEN    IN PCK_SUBTIPOS.TI_ENTERO

)
RETURN CLOB 
AS 
MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_CADENA         CLOB;
MI_VALORTOTAL     HISTORICOS.VALOR%TYPE;
MI_CANTIDAD       PCK_SUBTIPOS.TI_ENTERO;
MI_NITCOMPANIA    PARAMETROS_DE_ENTRADA.NIT%TYPE;
MI_RAZONSOCIAL    PARAMETROS_DE_ENTRADA.RAZONSOCIAL%TYPE; 
MI_NUM            PCK_SUBTIPOS.TI_ENTERO;

BEGIN
      BEGIN  

        SELECT SUM (EMBARGOS.VALOR),
               COUNT (1) NUMERO,
               COMPANIA.NITCOMPANIA,
               COMPANIA.NOMBRE
        INTO MI_VALORTOTAL, 
             MI_CANTIDAD,
             MI_NITCOMPANIA,
             MI_RAZONSOCIAL
        FROM EMBARGOS 
        INNER JOIN PERSONAL ON (EMBARGOS.COMPANIA = PERSONAL.COMPANIA)
        AND (EMBARGOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO)
        INNER JOIN COMPANIA 
        ON (COMPANIA.CODIGO = EMBARGOS.COMPANIA)      
        WHERE EMBARGOS.COMPANIA = UN_COMPANIA
        AND EMBARGOS.ID_DE_PROCESO = UN_PROCESO       
        AND EMBARGOS.ANO = UN_ANIO 
        AND EMBARGOS.MES= UN_MES
        AND EMBARGOS.PERIODO = UN_PERIODO 
        AND EMBARGOS.BANCO = UN_BANCO
        GROUP BY COMPANIA.NITCOMPANIA, COMPANIA.NOMBRE; 

       EXCEPTION WHEN NO_DATA_FOUND THEN
         RAISE PCK_EXCEPCIONES.EXC_NOMINA;                     
      END;    

      MI_NITCOMPANIA :=REPLACE(MI_NITCOMPANIA, '.', '');
      MI_NITCOMPANIA :=REPLACE(MI_NITCOMPANIA, '-', '');       
      MI_NITCOMPANIA := SUBSTR(MI_NITCOMPANIA,0,9);

      MI_NITCOMPANIA := MI_NITCOMPANIA ||  PCK_SYSMAN_UTL.FC_DCH(MI_NITCOMPANIA);

      MI_CADENA :=  LPAD(' ',23,' ')                                      || 
                    PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CANTIDAD, 
                                              UN_LONGITUD => 10)          ||  
                    LPAD(' ',40,' ')                                      || 
                    '3'                                                   ||                                                 
                    ' '                                                   || 
                    MI_NITCOMPANIA                                        || 
                    LPAD(' ',12,' ')                                      || 
                    RPAD(MI_RAZONSOCIAL,40,' ')                           ||
                    LPAD(' ',63,' ') ||  CHR(13) || CHR(10);
        MI_NUM :=1;    

        FOR RS IN (SELECT DISTINCT EMBARGOS.CONCEPTO_DEPOSITO,
                          EMBARGOS.OFICIO,
                          EMBARGOS.ID_JUZGADO,
                          EMBARGOS.CUENTA,
                          HISTORICOS.VALOR  VR_CUOTA,
                          EMBARGOS.DCTO_IDENTIDAD AS TIPODOCDEMANDANTE,
                          EMBARGOS.CEDULA AS NITDEMANDANTE,
                          PERSONAL.DCTO_IDENTIDAD AS TIPODOCDEMANDADO,
                          PERSONAL.NUMERO_DCTO AS NITDEMANDADO,
                          EMBARGOS.DEMANDANTE,
                          EMBARGOS.DEMANDANTE_NOMBRES,
                          PERSONAL.APELLIDO1,
                          PERSONAL.APELLIDO2,
                          PERSONAL.NOMBRES,
                          EMBARGOS.NUMERO_PROCESO,
                          EMBARGOS.OFICINA_DESTINO
                      FROM EMBARGOS 
                        INNER JOIN PERSONAL ON (EMBARGOS.COMPANIA = PERSONAL.COMPANIA)
                        AND (EMBARGOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO)
                        INNER JOIN COMPANIA 
                        ON (COMPANIA.CODIGO = EMBARGOS.COMPANIA) 
                        LEFT JOIN HISTORICOS
                          ON  EMBARGOS.COMPANIA           =HISTORICOS.COMPANIA
                          AND EMBARGOS.ID_DE_PROCESO      =HISTORICOS.ID_DE_PROCESO
                          AND EMBARGOS.ANO                =HISTORICOS.ANO
                          AND EMBARGOS.MES                =HISTORICOS.MES
                          AND EMBARGOS.PERIODO            =HISTORICOS.PERIODO
                          AND EMBARGOS.ID_DE_EMPLEADO     =HISTORICOS.ID_DE_EMPLEADO
                       INNER JOIN TIPO_EMBARGO
                          ON  TIPO_EMBARGO.COMPANIA           =HISTORICOS.COMPANIA
                          AND TIPO_EMBARGO.CONCEPTO_DESCUENTO =  HISTORICOS.ID_DE_CONCEPTO
                    WHERE EMBARGOS.COMPANIA = UN_COMPANIA
                    AND EMBARGOS.ID_DE_PROCESO = UN_PROCESO       
                    AND EMBARGOS.ANO = UN_ANIO 
                    AND EMBARGOS.MES= UN_MES
                    AND EMBARGOS.PERIODO = UN_PERIODO 
                    AND EMBARGOS.BANCO = UN_BANCO)
        LOOP        

             MI_CADENA := MI_CADENA ||  
                          /*CAMPO1*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_NUM, 
                                                    UN_LONGITUD => 6);                        
             MI_CADENA := MI_CADENA ||                                                      
                          /*CAMPO2*/TO_CHAR(UN_FECHAREPORTE, 'YYYYMMDD');                        
             MI_CADENA := MI_CADENA ||
                          /*CAMPO3*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => UN_OFICINAORIGEN, 
                                                    UN_LONGITUD => 4);                          
             MI_CADENA := MI_CADENA ||                                                    
                          /*CAMPO4*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.OFICINA_DESTINO, 
                                                    UN_LONGITUD => 4);                          
             MI_CADENA := MI_CADENA ||                                                    
                          /*CAMPO5*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.CONCEPTO_DEPOSITO, 
                                                    UN_LONGITUD => 1);                            
            MI_CADENA := MI_CADENA ||                                                       

                          /*CAMPO6*/ PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO  => REPLACE(REPLACE(RS.OFICIO, ' ', ''), '-', ''), 
                                                    UN_LONGITUD => 10);
            MI_CADENA := MI_CADENA ||                                                     
                          /*CAMPO7*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.ID_JUZGADO, 
                                                    UN_LONGITUD => 12) ;                       
            MI_CADENA := MI_CADENA ||                                                      
                          /*CAMPO8*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.CUENTA, 
                                                    UN_LONGITUD => 12);                        
            MI_CADENA := MI_CADENA ||                                                      
                          /*CAMPO9*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.VR_CUOTA, 
                                                    UN_LONGITUD => 13) ;     
            MI_CADENA := MI_CADENA ||                                       
                                    '.00';
            MI_CADENA := MI_CADENA ||                                         
                          /*CAMPO10*/PCK_NOMINA_COM4.FC_TIPODOCBANAGRARIO(RS.TIPODOCDEMANDANTE)               ||                                                   
                          /*CAMPO11*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO  => REPLACE(REPLACE(RS.NITDEMANDANTE, '.', ''), '-', ''), 
                                                    UN_LONGITUD => 11)                        ||      
                          /*CAMPO12*/PCK_NOMINA_COM4.FC_TIPODOCBANAGRARIO(RS.TIPODOCDEMANDADO)                ||                                                                                                       
                          /*CAMPO13*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO  => REPLACE(REPLACE(RS.NITDEMANDADO, '.', ''), '-', ''), 
                                                    UN_LONGITUD => 11)                        ||                                                      
                          /*CAMPO14*/RPAD(NVL(TRIM(RS.DEMANDANTE),' '),20,' ');                               
            MI_CADENA := MI_CADENA ||                                                        
                          /*CAMPO15*/RPAD(NVL(TRIM(RS.DEMANDANTE_NOMBRES),' '),20,' ')                       ||
                          /*CAMPO16*/RPAD(TRIM(PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(RS.APELLIDO1,' ') || ' ' || NVL(RS.APELLIDO2,' '))), 20, ' ')       ||
                          /*CAMPO17*/RPAD(NVL(TRIM(PCK_NOMINA_COM5.FC_QUITAESPECIALES(RS.NOMBRES)),' '), 20, ' ')                                ||
                          /*CAMPO18*/LPAD(NVL(RS.NUMERO_PROCESO,' '), 23, ' ') || CHR(10); 
            MI_NUM := MI_NUM +1;
        END LOOP;            

  RETURN MI_CADENA;
END FC_DISCOBANCOAGRARIOGOBNARINO;  

FUNCTION FC_DISCOBANCOAGRARIO 
  /*
      NAME              : FC_DISCOBANCOAGRARIO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
      DATE MIGRADOR     : 23/08/2018
      TIME              : 8:50 AM
      SOURCE MODULE     : NOMINAP2018.07.04_UNIFICADAS MPV 13072018_MPV
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PERMITE GENERAR ARCHIVOS PLANOS PARA LA EMISIÃ“N MASIVA DE LOS DEPOSITOS JUDICIALES BANCO AGRARIO
      PARAMETERS        : 
      MODIFICATIONS     : 
     @NAME: discoBancoAgrario
    */
(
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO          IN HISTORICOS.ID_DE_PROCESO%TYPE,
    UN_ANIO             IN HISTORICOS.ANO%TYPE,
    UN_MES              IN HISTORICOS.MES%TYPE,
    UN_PERIODO          IN HISTORICOS.PERIODO%TYPE,
    UN_BANCO            IN BANCO.BANCO%TYPE,    
    UN_FECHAREPORTE     IN DATE,            
    UN_OFICINAORIGEN    IN PCK_SUBTIPOS.TI_ENTERO

)
RETURN CLOB 
AS 
MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_CADENA         CLOB;
MI_VALORTOTAL     HISTORICOS.VALOR%TYPE;
MI_CANTIDAD       PCK_SUBTIPOS.TI_ENTERO;
MI_NITCOMPANIA    PARAMETROS_DE_ENTRADA.NIT%TYPE;
MI_RAZONSOCIAL    PARAMETROS_DE_ENTRADA.RAZONSOCIAL%TYPE; 
MI_NUM            PCK_SUBTIPOS.TI_ENTERO;

BEGIN
      BEGIN  

        SELECT SUM (EMBARGOS.VALOR),
               COUNT (1) NUMERO,
               COMPANIA.NITCOMPANIA,
               COMPANIA.NOMBRE
        INTO MI_VALORTOTAL, 
             MI_CANTIDAD,
             MI_NITCOMPANIA,
             MI_RAZONSOCIAL
        FROM EMBARGOS 
        INNER JOIN PERSONAL ON (EMBARGOS.COMPANIA = PERSONAL.COMPANIA)
        AND (EMBARGOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO)
        INNER JOIN COMPANIA 
        ON (COMPANIA.CODIGO = EMBARGOS.COMPANIA)      
        WHERE EMBARGOS.COMPANIA = UN_COMPANIA
        AND EMBARGOS.ID_DE_PROCESO = UN_PROCESO       
        AND EMBARGOS.ANO = UN_ANIO 
        AND EMBARGOS.MES= UN_MES
        AND EMBARGOS.PERIODO = UN_PERIODO 
        AND EMBARGOS.BANCO = UN_BANCO
        GROUP BY COMPANIA.NITCOMPANIA, COMPANIA.NOMBRE; 

       EXCEPTION WHEN NO_DATA_FOUND THEN
         RAISE PCK_EXCEPCIONES.EXC_NOMINA;                     
      END;    

      MI_NITCOMPANIA :=REPLACE(MI_NITCOMPANIA, '.', '');
      MI_NITCOMPANIA :=REPLACE(MI_NITCOMPANIA, '-', '');       
      MI_NITCOMPANIA := SUBSTR(MI_NITCOMPANIA,0,9);

      MI_NITCOMPANIA := MI_NITCOMPANIA ||  PCK_SYSMAN_UTL.FC_DCH(MI_NITCOMPANIA);

      MI_CADENA :=  LPAD(' ',23,' ')                                      || 
                    PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CANTIDAD, 
                                              UN_LONGITUD => 10)          ||  
                    LPAD(' ',40,' ')                                      || 
                    '3'                                                   ||                                                 
                    ' '                                                   || 
                    MI_NITCOMPANIA                                        || 
                    LPAD(' ',12,' ')                                      || 
                    RPAD(MI_RAZONSOCIAL,40,' ')                           ||
                    LPAD(' ',63,' ') ||  CHR(13) || CHR(10);
        MI_NUM :=1;    

        FOR RS IN (SELECT EMBARGOS.CONCEPTO_DEPOSITO,
                          EMBARGOS.OFICIO,
                          EMBARGOS.ID_JUZGADO,
                          EMBARGOS.CUENTA,
                          HISTORICOS.VALOR  VR_CUOTA,
                          EMBARGOS.DCTO_IDENTIDAD AS TIPODOCDEMANDANTE,
                          EMBARGOS.CEDULA AS NITDEMANDANTE,
                          PERSONAL.DCTO_IDENTIDAD AS TIPODOCDEMANDADO,
                          PERSONAL.NUMERO_DCTO AS NITDEMANDADO,
                          EMBARGOS.DEMANDANTE,
                          EMBARGOS.DEMANDANTE_NOMBRES,
                          PERSONAL.APELLIDO1,
                          PERSONAL.APELLIDO2,
                          PERSONAL.NOMBRES,
                          EMBARGOS.NUMERO_PROCESO,
                          EMBARGOS.OFICINA_DESTINO
                    FROM EMBARGOS 
                    INNER JOIN PERSONAL ON (EMBARGOS.COMPANIA = PERSONAL.COMPANIA)
                    AND (EMBARGOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO)
                    INNER JOIN COMPANIA 
                    ON (COMPANIA.CODIGO = EMBARGOS.COMPANIA) 
                    INNER JOIN HISTORICOS
                      ON  EMBARGOS.COMPANIA           =HISTORICOS.COMPANIA
                      AND EMBARGOS.ID_DE_PROCESO      =HISTORICOS.ID_DE_PROCESO
                      AND EMBARGOS.ANO                =HISTORICOS.ANO
                      AND EMBARGOS.MES                =HISTORICOS.MES
                      AND EMBARGOS.PERIODO            =HISTORICOS.PERIODO
                      AND EMBARGOS.ID_DE_EMPLEADO     =HISTORICOS.ID_DE_EMPLEADO
                      AND EMBARGOS.ID_DE_CONCEPTO      =HISTORICOS.ID_DE_CONCEPTO 
                    WHERE EMBARGOS.COMPANIA = UN_COMPANIA
                    AND EMBARGOS.ID_DE_PROCESO = UN_PROCESO       
                    AND EMBARGOS.ANO = UN_ANIO 
                    AND EMBARGOS.MES= UN_MES
                    AND EMBARGOS.PERIODO = UN_PERIODO 
                    AND EMBARGOS.BANCO = UN_BANCO)
        LOOP        

             MI_CADENA := MI_CADENA ||  
                          /*CAMPO1*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_NUM, 
                                                    UN_LONGITUD => 6)                         ||
                          /*CAMPO2*/TO_CHAR(UN_FECHAREPORTE, 'YYYYMMDD')                      ||                                                    
                          /*CAMPO3*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => UN_OFICINAORIGEN, 
                                                    UN_LONGITUD => 4)                         ||                          
                          /*CAMPO4*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.OFICINA_DESTINO, 
                                                    UN_LONGITUD => 4)                         ||                         
                          /*CAMPO5*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.CONCEPTO_DEPOSITO, 
                                                    UN_LONGITUD => 1);                            
            MI_CADENA := MI_CADENA ||                                                        

                          /*CAMPO6*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.OFICIO, 
                                                    UN_LONGITUD => 10)                        ||    
                          /*CAMPO7*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.ID_JUZGADO, 
                                                    UN_LONGITUD => 12)                        ||    
                          /*CAMPO8*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.CUENTA, 
                                                    UN_LONGITUD => 12)                        ||              
                          /*CAMPO9*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.VR_CUOTA, 
                                                    UN_LONGITUD => 13)                        ||                                                                                                                   
                                    '.00';
            MI_CADENA := MI_CADENA ||                                         
                          /*CAMPO10*/PCK_NOMINA_COM4.FC_TIPODOCBANAGRARIO(RS.TIPODOCDEMANDANTE)               ||                                                   
                          /*CAMPO11*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO  => REPLACE(REPLACE(RS.NITDEMANDANTE, '.', ''), '-', ''), 
                                                    UN_LONGITUD => 11)                        ||      
                          /*CAMPO12*/PCK_NOMINA_COM4.FC_TIPODOCBANAGRARIO(RS.TIPODOCDEMANDADO)                ||                                                                                                       
                          /*CAMPO13*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO  => REPLACE(REPLACE(RS.NITDEMANDADO, '.', ''), '-', ''), 
                                                    UN_LONGITUD => 11)                        ||                                                      
                          /*CAMPO14*/RPAD(NVL(TRIM(RS.DEMANDANTE),' '),20,' ');                               
            MI_CADENA := MI_CADENA ||                                                        
                          /*CAMPO15*/RPAD(NVL(TRIM(RS.DEMANDANTE_NOMBRES),' '),20,' ')                       ||
                          /*CAMPO16*/RPAD(TRIM(PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(RS.APELLIDO1,' ') || ' ' || NVL(RS.APELLIDO2,' '))), 20, ' ')       ||
                          /*CAMPO17*/RPAD(NVL(TRIM(PCK_NOMINA_COM5.FC_QUITAESPECIALES(RS.NOMBRES)),' '), 20, ' ')                                ||
                          /*CAMPO18*/LPAD(NVL(RS.NUMERO_PROCESO,' '), 23, ' ') || CHR(10); 
            MI_NUM := MI_NUM +1;
        END LOOP;            

  RETURN MI_CADENA;
END FC_DISCOBANCOAGRARIO;  

FUNCTION FC_TIPODOCBANAGRARIO 
  /*
      NAME              : FC_TIPODOCBANAGRARIO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
      DATE MIGRADOR     : 24/08/2018
      TIME              : 10:02 AM
      SOURCE MODULE     : NOMINAP2018.07.04_UNIFICADAS MPV 13072018_MPV
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PERMITE OBTENER EL CODIGO DEL TIPO DE DOCUMENTO DE ACUERDO A ESTRUCTURA DE ARCHIVO PLANO PARA LA EMISION DE DEPOSITOS JUDICIALES DEL BANCO AGRARIO
      PARAMETERS        : 
      MODIFICATIONS     : 
    */
(
  UN_TIPODOCUMENTO   IN EMBARGOS.DCTO_IDENTIDAD%TYPE
)
RETURN VARCHAR2 AS 
  MI_TIPO            VARCHAR(2);
BEGIN

MI_TIPO := CASE UN_TIPODOCUMENTO 
            WHEN 'C' THEN '1'
            WHEN 'N' THEN '3'
            WHEN 'U' THEN '5'
            WHEN 'E' THEN '2'
            WHEN 'P' THEN '4'
            WHEN 'T' THEN '5'
            WHEN 'R' THEN '9'
            ELSE ' '  END;      
  RETURN MI_TIPO;
END FC_TIPODOCBANAGRARIO;

FUNCTION FC_DISCOBANCOLOMBIA 
 /*
      NAME              : FC_DISCOBANCOLOMBIA
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
      DATE MIGRADOR     : 28/08/2018
      TIME              : 10:29 AM
      SOURCE MODULE     : NOMINAP2018.07.04_UNIFICADAS MPV 13072018_MPV
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PERMITE GENERAR ARCHIVOS PLANOS PARA EL BANCO DE COLOMBIA
      PARAMETERS        : 
      MODIFICATIONS     : 
     @NAME: generarDiscoBancoColombia
    */
(
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO          IN HISTORICOS.ID_DE_PROCESO%TYPE,
    UN_ANIO             IN HISTORICOS.ANO%TYPE,
    UN_MES              IN HISTORICOS.MES%TYPE,
    UN_PERIODO          IN HISTORICOS.PERIODO%TYPE,
    UN_BANCO            IN BANCO.BANCO%TYPE,    
    UN_FECHAREPORTE     IN DATE,            
    UN_TODOSLOSBANCOS   IN PCK_SUBTIPOS.TI_LOGICO,
    UN_OBSERVACION      IN VARCHAR2,
    UN_LOTE             IN VARCHAR2,
    UN_INFORME          IN PCK_SUBTIPOS.TI_ENTERO,
    UN_TCUENTABANORIGEN IN VARCHAR2,   
    UN_CUENTABANORIGEN  IN VARCHAR2 
)
RETURN CLOB 
AS 
MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_CADENA           CLOB;
MI_NITCOMPANIA      COMPANIA.NITCOMPANIA%TYPE;
MI_RAZONSOCIAL      COMPANIA.NOMBRE%TYPE; 
MI_CUENTABANCO      BANCOS_NOMINA.CUENTA%TYPE;
MI_VALORTOTAL       HISTORICOS.VALOR%TYPE;
MI_CANTIDAD         PCK_SUBTIPOS.TI_ENTERO;
MI_APELLIDO1        VARCHAR2(30);
MI_APELLIDO2        VARCHAR2(30);
MI_NOMBRES          VARCHAR2(255); 
MI_PAR_BANCOS_FND   PCK_SUBTIPOS.TI_PARAMETRO;
BEGIN

  BEGIN
    SELECT SUM (HISTORICOS.VALOR),
            COUNT (1) NUMERO,
            COMPANIA.NITCOMPANIA,
            COMPANIA.NOMBRE  
       INTO MI_VALORTOTAL, 
            MI_CANTIDAD,
            MI_NITCOMPANIA, 
            MI_RAZONSOCIAL
    FROM HISTORICOS INNER JOIN PERSONAL ON (HISTORICOS.COMPANIA = PERSONAL.COMPANIA) 
    AND (HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO)
    INNER JOIN BANCOS_NOMINA ON (PERSONAL.COMPANIA = BANCOS_NOMINA.COMPANIA) 
    AND (PERSONAL.BANCO = BANCOS_NOMINA.BANCO)
    INNER JOIN COMPANIA ON PERSONAL.COMPANIA = COMPANIA.CODIGO
    WHERE HISTORICOS.COMPANIA     = UN_COMPANIA
      AND HISTORICOS.ID_DE_PROCESO  = UN_PROCESO
      AND HISTORICOS.ANO            = UN_ANIO
      AND HISTORICOS.MES            = UN_MES
      AND HISTORICOS.PERIODO        = UN_PERIODO
      AND HISTORICOS.ID_DE_CONCEPTO = '144'  
      AND PERSONAL.BANCO NOT IN ('00','98','99')
      AND PERSONAL.BANCO            = CASE WHEN UN_TODOSLOSBANCOS <> 0 THEN PERSONAL.BANCO ELSE UN_BANCO END        
      GROUP BY COMPANIA.NITCOMPANIA,
      COMPANIA.NOMBRE;

    EXCEPTION WHEN NO_DATA_FOUND THEN
     RAISE PCK_EXCEPCIONES.EXC_NOMINA;       
  END;

  MI_NITCOMPANIA :=REPLACE(MI_NITCOMPANIA, '.', '');
  MI_NITCOMPANIA :=REPLACE(MI_NITCOMPANIA, '-', '');              
  MI_CUENTABANCO :=REPLACE(UN_CUENTABANORIGEN, '.', '');
  MI_CUENTABANCO :=REPLACE(MI_CUENTABANCO, '-', '');     

  MI_PAR_BANCOS_FND  := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                         ,UN_NOMBRE     => 'PLANO PAGO BANCOS FND'
                                         ,UN_MODULO     => PCK_DATOS.FC_MODULONOMINA
                                         ,UN_FECHA_PAR  => SYSDATE);

  IF UN_INFORME = 0 THEN   --PLANO BANCOLOMBIA
  MI_CADENA :=  '1' || 
                CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN 
                LPAD(PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_NITCOMPANIA, 
                               UN_LONGITUD => 9),15,'0') ELSE
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_NITCOMPANIA, 
                                          UN_LONGITUD => 10)  END     ||
                CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN 'I' 
                ELSE '' END                                           ||
                CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN 
                RPAD(MI_RAZONSOCIAL,15,' ')
                ELSE
                RPAD(MI_RAZONSOCIAL,16,' ') END                       ||                                          
                '225'                                                 ||  
                RPAD(NVL(UN_OBSERVACION,'PAGONOMINA'),10,' ')         ||                                                 
                CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN 
                TO_CHAR(SYSDATE,'YYYYMMDD') ELSE  
                TO_CHAR(SYSDATE,'YYMMDD') END                         || 
                NVL(UN_LOTE, 'A')                                     ||
                CASE WHEN MI_PAR_BANCOS_FND IN('SI') THEN
                TO_CHAR(UN_FECHAREPORTE, 'YYYYMMDD')ELSE 
                TO_CHAR(UN_FECHAREPORTE, 'YYMMDD') END                || 
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CANTIDAD, 
                                          UN_LONGITUD => 6)           ||                                                                       
                CASE WHEN MI_PAR_BANCOS_FND IN('SI') THEN
                LPAD('0',17,'0') ELSE
                LPAD('0',12,'0') END                                  || 
                CASE WHEN MI_PAR_BANCOS_FND IN('SI') THEN
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_VALORTOTAL, 
                                          UN_LONGITUD => 15) || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   =>ROUND(MI_VALORTOTAL - TRUNC(MI_VALORTOTAL),2)*100,
                                                UN_LONGITUD => 2) ELSE 
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_VALORTOTAL, 
                                          UN_LONGITUD => 12) END      ||                      
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CUENTABANCO, 
                                          UN_LONGITUD => 11)          ||

                CASE WHEN MI_PAR_BANCOS_FND IN('SI') THEN
                'S' ELSE
                CASE WHEN UPPER(UN_TCUENTABANORIGEN) = 'C' THEN 'D' ELSE 'S' END      
                END 
                ||  CHR(13) || CHR(10);

  ELSIF UN_INFORME = 1 THEN  --ESTRUCTURA PAB 2015
  MI_CADENA :=  '1' || 
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => CASE WHEN LENGTH(MI_NITCOMPANIA) = 10 THEN SUBSTR(MI_NITCOMPANIA,1,9) ELSE MI_NITCOMPANIA END, 
                                          UN_LONGITUD => 15)          ||  
                'I'                                                   ||                                                                                          
                RPAD(' ',15,' ')                                      ||                                          
                '225'                                                 ||  
                CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN  RPAD(NVL(UN_OBSERVACION, 'NOM'||TO_CHAR(TO_DATE('01-'||UN_MES||'-'||UN_ANIO), 'MON', 'NLS_DATE_LANGUAGE = Spanish')||UN_ANIO),10,' ') ELSE RPAD('NOMINA',10,' ') END                                 || --MOD JM CC2935                                                                           
                TO_CHAR(SYSDATE,'YYYYMMDD')                           || 
                RPAD(NVL(UN_LOTE, 'AA'),2)                            ||
                TO_CHAR(UN_FECHAREPORTE, 'YYYYMMDD')                  || 
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CANTIDAD, 
                                          UN_LONGITUD => 6)           ||
                LPAD('0',17,'0')                                      ||                                                                 
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_VALORTOTAL, 
                                          UN_LONGITUD => 15)          ||                      
                '00'                                                  ||                                                                               
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CUENTABANCO, 
                                          UN_LONGITUD => 11)          ||          
                CASE WHEN UPPER(UN_TCUENTABANORIGEN) = 'C' THEN 'D' ELSE 'S' END  ||
                RPAD(' ',149,' ')                                                                                              
                ||  CHR(13) || CHR(10);  
  ELSIF UN_INFORME = 2 THEN --GENERAR EXCEL                  
  MI_CADENA :=  MI_NITCOMPANIA                                                 || PCK_DATOS.GL_SEPARADOR_COL ||
                MI_RAZONSOCIAL                                                 || PCK_DATOS.GL_SEPARADOR_COL ||
                '225'                                                          || PCK_DATOS.GL_SEPARADOR_COL ||
                UN_LOTE                                                        || PCK_DATOS.GL_SEPARADOR_COL ||
                UN_CUENTABANORIGEN                                             || PCK_DATOS.GL_SEPARADOR_COL ||
                UN_TCUENTABANORIGEN                                            || PCK_DATOS.GL_SEPARADOR_COL ||
                PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MES)                           || PCK_DATOS.GL_SEPARADOR_REG;                
  END IF;
    FOR RS IN (
    SELECT 
           PERSONAL.NUMERO_DCTO,
           PERSONAL.APELLIDO1, 
           PERSONAL.APELLIDO2,
           PERSONAL.NOMBRES,
           PERSONAL.APELLIDOS_TUTOR,
           PERSONAL.NOMBRES_TUTOR,
           PERSONAL.CEDULA_TUTOR,
           BANCOS_NOMINA.CODIGO_ENTIDAD,
           BANCOS_NOMINA.CODIGO_NACHAM,
           PERSONAL.CUENTA,
           PERSONAL.REFERENCIA,
           PERSONAL.TIPOCUENTA,
           HISTORICOS.VALOR,
           PERSONAL.ID_DE_EMPLEADO,
           PERSONAL.DCTO_IDENTIDAD
    FROM HISTORICOS INNER JOIN PERSONAL ON (HISTORICOS.COMPANIA = PERSONAL.COMPANIA) 
    AND (HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO)
    INNER JOIN BANCOS_NOMINA ON (PERSONAL.COMPANIA = BANCOS_NOMINA.COMPANIA) 
    AND (PERSONAL.BANCO = BANCOS_NOMINA.BANCO)
    INNER JOIN COMPANIA ON PERSONAL.COMPANIA = COMPANIA.CODIGO
    WHERE HISTORICOS.COMPANIA     = UN_COMPANIA
      AND HISTORICOS.ID_DE_PROCESO  = UN_PROCESO
      AND HISTORICOS.ANO            = UN_ANIO
      AND HISTORICOS.MES            = UN_MES
      AND HISTORICOS.PERIODO        = UN_PERIODO
      AND HISTORICOS.ID_DE_CONCEPTO = '144'  
      AND PERSONAL.BANCO NOT IN ('00','98','99')
      AND PERSONAL.BANCO            = CASE WHEN UN_TODOSLOSBANCOS <> 0 THEN PERSONAL.BANCO ELSE UN_BANCO END               
      ORDER BY PERSONAL.APELLIDO1
            || ' '
            || PERSONAL.APELLIDO2
            || ' '
            || PERSONAL.NOMBRES)

        LOOP  

        IF RS.CODIGO_ENTIDAD IS NULL THEN
          MI_MSGERROR(1).CLAVE := 'EMPLEADO';
          MI_MSGERROR(1).VALOR := RS.ID_DE_EMPLEADO || ' ' || RS.APELLIDO1 || ' ' || RS.APELLIDO2 || ' ' || RS.NOMBRES; 
          PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => UN_COMPANIA,
                                     UN_MENSAJE_COD => PCK_ERRORES.ALER_CODENTIDADFINANCIERA,
                                     UN_REEMPLAZOS  => MI_MSGERROR);           
        END IF;

        MI_APELLIDO1 := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(TRIM(RS.APELLIDO1), ''));
        MI_APELLIDO2 := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(TRIM(RS.APELLIDO2), ''));
        MI_NOMBRES := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(TRIM(RS.NOMBRES), ''));      
      --(APINEDA:17/10/2018)-Se agrega replace al nÃƒÂºmero de cuenta debido a que en la UPC contiene espacios intermedios.
      IF UN_INFORME = 0 THEN   --PLANO BANCOLOMBIA
         MI_CADENA := MI_CADENA                                                               ||  
             /*CAMPO1*/'6'                                                                    ||
            /*CAMPO2*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.NUMERO_DCTO, 
                                                UN_LONGITUD => 15)                            ||                                                
            CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN
            /*CAMPO3*/RPAD(MI_APELLIDO1 || ' ' || MI_APELLIDO2 || ' ' || MI_NOMBRES, 30,' ') ELSE                                               
            /*CAMPO3*/RPAD(MI_APELLIDO1 || ' ' || MI_APELLIDO2 || ' ' || MI_NOMBRES, 18,' ')        
            END                                                                               ||
            /*CAMPO4*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.CODIGO_ENTIDAD, 
                                                UN_LONGITUD => 9)                             ||    
         CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN
                      RPAD(RS.CUENTA,17,' ')                                              
                         ELSE
            /*CAMPO5*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => REPLACE(RS.CUENTA,' ',''),
                                                UN_LONGITUD => 17)    END                     ||                  
            /*CAMPO6*/'S'                                                                     ||                                                
            /*CAMPO7*/CASE WHEN RS.TIPOCUENTA = 'A' THEN '37' ELSE '27' END                   ||
            CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN
            PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.VALOR, 
                                                UN_LONGITUD => 15) || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   =>ROUND(RS.VALOR - TRUNC(RS.VALOR),2)*100,
                                                UN_LONGITUD => 2) ELSE
            /*CAMPO8*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.VALOR, 
                                                UN_LONGITUD => 10) END                           ||  
            CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN
             TO_CHAR(UN_FECHAREPORTE, 'YYYYMMDD') ||  RPAD('NOMINA ' || MI_RAZONSOCIAL ,21,' ') || '000000' || RPAD(' ',15,' ') || RPAD(' ',80,' ') || LPAD(' ',16,'0') || RPAD(' ',27,' ') ELSE                    
            /*CAMPO9*/RPAD('NOMINA',9,' ')  ||
            /*CAMPO10*/RPAD(' ',12,' ')      ||
            /*CAMPO11*/RPAD(' ',1,' ')  
            END || CHR(10);
      ELSIF UN_INFORME = 1 THEN  --ESTRUCTURA PAB 2015  
         MI_CADENA := MI_CADENA                                                               ||  
            /*CAMPO1*/'6'                                                                     ||
            /*CAMPO2*/ CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA ,
                        UN_NOMBRE =>'FORMATO PLANO PAGOS BANCO TIPO PAB 2023' , 
                        UN_MODULO =>PCK_DATOS.FC_MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), 'NO') = 'SI' THEN  
                                RPAD(RS.NUMERO_DCTO,15,' ') ELSE 
                                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.NUMERO_DCTO, 
                                                UN_LONGITUD => 15) END                        ||                                                
            /*CAMPO3*/RPAD(MI_APELLIDO1 || ' ' || MI_APELLIDO2 || ' ' || MI_NOMBRES, 30,' ')  ||      
            /*CAMPO4*/CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA ,
                        UN_NOMBRE =>'TOMAR CODIGO BANCO PARA PLANO DE CODIGO NACHAM' , 
                        UN_MODULO =>PCK_DATOS.FC_MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), 'NO') = 'SI' THEN  
                            PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.CODIGO_NACHAM, 
                                                UN_LONGITUD => 9)   
                      ELSE 
                         PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.CODIGO_ENTIDAD, 
                                                UN_LONGITUD => 9) END                         ||                                                
            /*CAMPO5*/RPAD(CASE WHEN LENGTH(NVL(RS.CUENTA, ' ')) < 15 THEN 
                              NVL(RS.CUENTA, ' ')
                           ELSE 
                              SUBSTR(NVL(RS.CUENTA, ' '), 1, 4) || SUBSTR(NVL(RS.CUENTA, ' '),  LENGTH(RS.CUENTA) - 6)
                           END, 17, ' ')                                                      ||                  
            /*CAMPO6*/' '                                                                     ||                                                
            /*CAMPO7*/CASE WHEN RS.TIPOCUENTA = 'A' THEN '37' ELSE '27' END                   ||
            /*CAMPO8*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.VALOR, 
                                                UN_LONGITUD => 15)                            ||    
            /*CAMPO9*/'00'                                                                    ||                                                                                                
            /*CAMPO10*//*CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA ,
                        UN_NOMBRE =>'FORMATO PLANO PAGOS BANCO TIPO PAB 2023' ,
                        UN_MODULO =>PCK_DATOS.FC_MODULONOMINA, UN_FECHA_PAR=>SYSDATE ), 'NO') = 'SI' THEN
                                TO_CHAR(UN_FECHAREPORTE, 'YYYYMMDD')  ELSE  '00000000'  END*/  
                                TO_CHAR(UN_FECHAREPORTE, 'YYYYMMDD')                          ||--JM MOD CC 2820                                                             
            /*CAMPO11*/
            CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN
            RPAD(' ', 21,' ')
            ELSE
            RPAD('NOMINA ' || SUBSTR(NVL(UN_OBSERVACION, UPPER(SUBSTR(PCK_SYSMAN_UTL.FC_NOMBRE_MES(TO_NUMBER(TO_CHAR(SYSDATE, 'MM'))),1,3))), 1, 10), 21,' ') END || --JM MOD CC 2935
            /*CAMPO12*/CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN '1' ELSE ' ' END             || --JM MOD CC 2935
            /*CAMPO13*/CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN '00000' ELSE RPAD(' ',5,' ') END                                                       ||--JM MOD CC 2935
            /*CAMPO14*/RPAD(' ',15,' ')                                                       ||
            /*CAMPO15*/RPAD(' ',80,' ')                                                       ||
            /*CAMPO16*/RPAD(' ',15,' ')                                                       ||
            /*CAMPO17*/RPAD(' ',27,' ') || CHR(10);   
      ELSIF UN_INFORME = 2 THEN --GENERAR EXCEL              
          MI_CADENA := MI_CADENA || 
                        CASE RS.DCTO_IDENTIDAD
                        WHEN 'C' THEN '1'
                        WHEN 'E' THEN '2' 
                        WHEN 'N' THEN '3' 
                        WHEN 'T' THEN '4'
                        ELSE '1' END                                                            || PCK_DATOS.GL_SEPARADOR_COL ||                        
                        CASE WHEN RS.APELLIDOS_TUTOR IS NOT NULL THEN SUBSTR(RS.CEDULA_TUTOR,0,15)    ELSE SUBSTR(RS.NUMERO_DCTO,0,15)    END                                         || PCK_DATOS.GL_SEPARADOR_COL || --Se estable valor maximo de caracteres a 15 TAR 1000097787 JORDUZ (27/02/03)
                        CASE WHEN RS.APELLIDOS_TUTOR IS NOT NULL THEN REPLACE(substr(RS.APELLIDOS_TUTOR || ' '|| RS.NOMBRES_TUTOR,0,30),'Ñ','N') ELSE REPLACE(substr(MI_APELLIDO1 || ' ' || MI_APELLIDO2 || ' ' || MI_NOMBRES,0,30),'Ñ','N') end   || PCK_DATOS.GL_SEPARADOR_COL ||  --Se estable valor maximo de caracteres a 18 TAR 1000097787 JORDUZ (27/02/03)
                        CASE WHEN RS.TIPOCUENTA = 'A' THEN '37' ELSE '27' END                   || PCK_DATOS.GL_SEPARADOR_COL ||
                        SUBSTR(RS.CODIGO_ENTIDAD,0,4)                                                      || PCK_DATOS.GL_SEPARADOR_COL ||
                        SUBSTR(REPLACE(REPLACE(RS.CUENTA, '.', ''), '-', ''),0,17)              || PCK_DATOS.GL_SEPARADOR_COL || --Se estable valor maximo de caracteres a 17 TAR 1000097787 JORDUZ (27/02/03)
                        SUBSTR(RS.VALOR,0,15)                                             || PCK_DATOS.GL_SEPARADOR_REG;   --Se estable valor maximo de caracteres a 10 TAR 1000097787 JORDUZ (27/02/03)
      END IF;                
    END LOOP; 

  RETURN MI_CADENA;
END FC_DISCOBANCOLOMBIA;

FUNCTION FC_DISCOBANCOLOMBIAIDI
/*
  NAME              : FC_DISCOBANCOLOMBIAIDI
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : LUIS MAURICIO MOSQUERA C
  DATE MIGRATION    : 14/09/2020
  TIME              : 10:00 AM
  SOURCE MODULE     : NOMINA (6)
  MODIFIED BY       : 
  MODIFICATIONS     : 
  DESCRIPTION       : FunciÃ³n que permite generar archivo plano para Bancolombia para IDIPRON
  PARAMETERS        : 
  @Name             :generarDiscoBancolombiaIdipron
  @Method           :GET
*/
(
    UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO          IN  PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_PERIODO          IN  PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_MES              IN  PCK_SUBTIPOS.TI_MES,
    UN_ANO              IN  PCK_SUBTIPOS.TI_ANIO,
    UN_FECHAEMISION     IN  DATE,
    UN_FECHATRANSACCION IN  DATE,
    UN_OBSERVACION      IN  VARCHAR2,
    UN_SECUENCIALOTE    IN  VARCHAR2,
    UN_BANCO            IN  BANCO.BANCO%TYPE,
    UN_TODOSLOSBANCOS   IN PCK_SUBTIPOS.TI_LOGICO
)
RETURN CLOB AS 


MI_ENCABEZADO           VARCHAR2(250);
MI_DATOSDISCO           CLOB;
MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;

MI_CONTADORDATOS        NUMBER := 0;
MI_SUMCREDITO           NUMBER := 0;
MI_CUENTABANCO          BANCOS_NOMINA.CUENTA%TYPE;
MI_TIPOCUENTABANCO      BANCOS_NOMINA.TIPO_CUENTA%TYPE;
MI_NITCOMPANIA          COMPANIA.NITCOMPANIA%TYPE;
MI_RAZONSOCIAL          COMPANIA.NOMBRE%TYPE;
MI_TIPOPAGO             NUMBER := 0;
MI_TIPOTRANSACCION      NUMBER := 0;
MI_PERSONAL             VARCHAR(250);
MI_INDICADORPAGO        VARCHAR(1);

BEGIN
    --Consulta para datos de encabezado
    BEGIN
        SELECT
            COMPANIA.NOMBRE,
            REPLACE(COMPANIA.NITCOMPANIA, '-', '') NITCOMPANIA,
            NVL(BANCOS_NOMINA.CUENTA,0) AS CUENTA,
            CASE BANCOS_NOMINA.TIPO_CUENTA
                WHEN 'A' THEN 'S'
                WHEN 'C' THEN 'D' 
                ELSE ''
            END AS TIPO_CUENTA
        INTO    MI_RAZONSOCIAL,
                MI_NITCOMPANIA,
                MI_CUENTABANCO,
                MI_TIPOCUENTABANCO
        FROM
            COMPANIA
            INNER JOIN BANCOS_NOMINA
                ON COMPANIA.CODIGO = BANCOS_NOMINA.COMPANIA
        WHERE
            COMPANIA.CODIGO = UN_COMPANIA
            AND BANCOS_NOMINA.BANCO = '005600078'
        GROUP BY
            COMPANIA.NOMBRE,
            COMPANIA.NITCOMPANIA,
            BANCOS_NOMINA.CUENTA,
            BANCOS_NOMINA.TIPO_CUENTA;
        END;

    MI_NITCOMPANIA :=REPLACE(MI_NITCOMPANIA, '.', '');
    MI_NITCOMPANIA :=REPLACE(MI_NITCOMPANIA, '-', '');              
    MI_CUENTABANCO :=REPLACE(MI_CUENTABANCO, '.', '');
    MI_CUENTABANCO :=REPLACE(MI_CUENTABANCO, '-', '');  

    -- Consulta para obtener los datos de los empleados
    FOR MI_RS IN (
        SELECT
            PERSONAL.NOMBRECOMPLETO AS TERCERO,
            PERSONAL.NUMERO_DCTO AS DOCUMENTO,
            PERSONAL.BANCO AS BANCO,
            BANCOS_NOMINA.CUENTA,
            PERSONAL.CUENTA AS CUENTAP,
            SUM(HISTORICOS.VALOR) AS VALOR,
            CASE BANCOS_NOMINA.TIPO_CUENTA
                WHEN 'A' THEN 37
                WHEN 'C' THEN 27
                ELSE 0
            END AS TIPO_TRANSACCION
        FROM
            HISTORICOS
            INNER JOIN PERSONAL
                ON HISTORICOS.COMPANIA = PERSONAL.COMPANIA
                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
            INNER JOIN BANCOS_NOMINA
                ON PERSONAL.BANCO = BANCOS_NOMINA.BANCO
                AND PERSONAL.COMPANIA = BANCOS_NOMINA.COMPANIA
        WHERE
            HISTORICOS.ANO = UN_ANO
            AND HISTORICOS.MES = UN_MES
            AND HISTORICOS.ID_DE_PROCESO = UN_PROCESO
            AND HISTORICOS.PERIODO = UN_PERIODO
            AND HISTORICOS.COMPANIA = UN_COMPANIA
            AND HISTORICOS.ID_DE_CONCEPTO = '144'
            AND PERSONAL.BANCO NOT IN ('00','98','99')
            AND BANCOS_NOMINA.CUENTA IS NOT NULL
            AND BANCOS_NOMINA.CUENTA NOT IN ('NA')
            AND PERSONAL.BANCO = CASE WHEN UN_TODOSLOSBANCOS <> 0 THEN PERSONAL.BANCO ELSE UN_BANCO END  
            AND BANCOS_NOMINA.PAGOBANCOLOMBIA <> CASE WHEN UN_TODOSLOSBANCOS <> 0 THEN 0 ELSE -1 END
          GROUP BY
            PERSONAL.NOMBRECOMPLETO,
            PERSONAL.NUMERO_DCTO,
            BANCOS_NOMINA.CODIGO_ENTIDAD,
            BANCOS_NOMINA.CUENTA,
            BANCOS_NOMINA.TIPO_CUENTA,
            PERSONAL.BANCO,
            PERSONAL.CUENTA
          ORDER BY PERSONAL.NOMBRECOMPLETO DESC
    ) LOOP
        BEGIN
            IF MI_RS.BANCO IS NULL THEN
              MI_MSGERROR(1).CLAVE := 'EMPLEADO';
              MI_MSGERROR(1).VALOR := MI_RS.TERCERO; 
              PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => UN_COMPANIA,
                                         UN_MENSAJE_COD => PCK_ERRORES.ALER_CODENTIDADFINANCIERA,
                                         UN_REEMPLAZOS  => MI_MSGERROR);           
            END IF;        

            --MI_PERSONAL := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(TRIM(MI_RS.TERCERO), ''));
            MI_PERSONAL := NVL(TRIM(MI_RS.TERCERO), '');
            IF MI_RS.TIPO_TRANSACCION > 0 THEN
                MI_INDICADORPAGO:= 'S';
            ELSE
                MI_INDICADORPAGO := '1';
            END IF;

            MI_DATOSDISCO :=    '6' || LPAD(MI_RS.DOCUMENTO,15,'0') || LPAD(MI_PERSONAL,18,' ') || LPAD(MI_RS.BANCO,9,'0') ||
                                LPAD(MI_RS.CUENTAP,17,'0') || MI_INDICADORPAGO || MI_RS.TIPO_TRANSACCION ||
                                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RS.VALOR, 
                                                UN_LONGITUD => 10)  || 'PG NOMINA' || LPAD((UN_MES||UN_ANO),12,'0') || 
                                ' ' || CHR(13) || CHR(10) || MI_DATOSDISCO;
            MI_SUMCREDITO := MI_SUMCREDITO + MI_RS.VALOR;
            MI_CONTADORDATOS := MI_CONTADORDATOS + 1;

        END;
    END LOOP;

    MI_ENCABEZADO :=    '1' || LPAD(MI_NITCOMPANIA,10,'0') || LPAD(MI_RAZONSOCIAL,16,' ') || '225' || LPAD(UN_OBSERVACION,10,' ') ||
                        TO_CHAR(UN_FECHAEMISION,'YYMMDD') || UN_SECUENCIALOTE || TO_CHAR(UN_FECHATRANSACCION,'YYMMDD') || 
                        LPAD(MI_CONTADORDATOS,6,'0') || LPAD('000',12,'0') || LPAD(MI_SUMCREDITO,12,'0') || 
                        LPAD(MI_CUENTABANCO,11,'0') || MI_TIPOCUENTABANCO || CHR(13) || CHR(10);

    MI_DATOSDISCO := MI_ENCABEZADO || MI_DATOSDISCO;

  RETURN MI_DATOSDISCO;
END FC_DISCOBANCOLOMBIAIDI;

FUNCTION FC_DISCODAVIVIENDAIDI 
/*
  NAME              : FC_DISCODAVIVIENDAIDI
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : LUIS MAURICIO MOSQUERA C
  DATE MIGRATION    : 11/09/2020
  TIME              : 10:15 AM
  SOURCE MODULE     : NOMINA (6)
  MODIFIED BY       : 
  MODIFICATIONS     : 
  DESCRIPTION       : FunciÃ³n que permite generar archivo plano para Bancolombia para IDIPRON
  PARAMETERS        : 
  @Name             :generarDiscoDaviviendaIdi
  @Method           :GET
*/
(
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO      IN  PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_PERIODO      IN  PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_MES          IN  PCK_SUBTIPOS.TI_MES,
    UN_ANO          IN  PCK_SUBTIPOS.TI_ANIO,
    UN_FECHAPROCESO IN  DATE,
    UN_FECHASISTEMA IN  DATE,
    UN_BANCO        IN  BANCOS_NOMINA.BANCO%TYPE DEFAULT NULL,
    UN_TODOSLOSBANCOS   IN PCK_SUBTIPOS.TI_LOGICO DEFAULT -1
)
RETURN CLOB AS 

MI_ENCABEZADO           VARCHAR2(250);
MI_DATOSDISCO           CLOB;

MI_CUENTABANCO          BANCOS_NOMINA.CUENTA%TYPE;
MI_TIPOCUENTABANCO      VARCHAR(2);
MI_NITCOMPANIA          COMPANIA.NITCOMPANIA%TYPE;
MI_CODIGOBANCO          NUMBER := 0;
MI_VALTOTALTRASLADOS    NUMBER := 0;
MI_TOTALTRASLADOS       NUMBER := 0;
MI_RAZONSOCIAL          COMPANIA.NOMBRE%TYPE;
MI_PARBANCO             PCK_SUBTIPOS.TI_PARAMETRO;   /*Contiene el valor del parametro: CODIGO BANCO DAVIVIENDA PARA ARCHIVO PLANO*/
MI_MANEJADIGITO         PCK_SUBTIPOS.TI_PARAMETRO;   /*Contiene el valor del parametro: MANEJA NIT CON DIGITO DE VERIFICACION PARA ARCHIVO PLANO*/

BEGIN
    BEGIN

    MI_PARBANCO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                         ,UN_NOMBRE     => 'CODIGO BANCO DAVIVIENDA PARA ARCHIVO PLANO'
                                         ,UN_MODULO     => PCK_DATOS.FC_MODULONOMINA
                                         ,UN_FECHA_PAR  => SYSDATE);

    MI_MANEJADIGITO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                         ,UN_NOMBRE     => 'MANEJA NIT CON DIGITO DE VERIFICACION PARA ARCHIVO PLANO'
                                         ,UN_MODULO     => PCK_DATOS.FC_MODULONOMINA
                                         ,UN_FECHA_PAR  => SYSDATE);
        SELECT
            COMPANIA.NOMBRE,
            CASE WHEN MI_MANEJADIGITO = 'SI' THEN PARAMETROS_DE_ENTRADA.NIT ELSE COMPANIA.NITCOMPANIA END,
            NVL(BANCOS_NOMINA.CUENTA,0) AS CUENTA,
            CASE BANCOS_NOMINA.TIPO_CUENTA
                WHEN 'A' THEN 'CA'
                WHEN 'C' THEN 'CC' 
                ELSE ''
            END AS TIPO_CUENTA
        INTO    MI_RAZONSOCIAL,
            MI_NITCOMPANIA,
            MI_CUENTABANCO,
            MI_TIPOCUENTABANCO
        FROM
            COMPANIA
            INNER JOIN BANCOS_NOMINA
                ON COMPANIA.CODIGO = BANCOS_NOMINA.COMPANIA
            LEFT JOIN PARAMETROS_DE_ENTRADA 
                ON COMPANIA.CODIGO = PARAMETROS_DE_ENTRADA.COMPANIA
        WHERE
            COMPANIA.CODIGO = UN_COMPANIA
            AND BANCOS_NOMINA.BANCO = MI_PARBANCO
        GROUP BY
            COMPANIA.NOMBRE,
            CASE WHEN MI_MANEJADIGITO = 'SI' THEN PARAMETROS_DE_ENTRADA.NIT ELSE COMPANIA.NITCOMPANIA END,
            BANCOS_NOMINA.CUENTA,
            BANCOS_NOMINA.TIPO_CUENTA;
    EXCEPTION WHEN NO_DATA_FOUND THEN
     RAISE PCK_EXCEPCIONES.EXC_NOMINA;       
    END;
    MI_NITCOMPANIA :=REPLACE(MI_NITCOMPANIA, '.', '');
    MI_NITCOMPANIA :=REPLACE(MI_NITCOMPANIA, '-', '');              
    MI_CUENTABANCO :=REPLACE(MI_CUENTABANCO, '.', '');
    MI_CUENTABANCO :=REPLACE(MI_CUENTABANCO, '-', ''); 

    FOR MI_RS IN (
        SELECT
            PERSONAL.NUMERO_DCTO AS DOCUMENTO,
            BANCOS_NOMINA.CODIGO_ENTIDAD AS BANCO,
            PERSONAL.CUENTA,
            (SUM(HISTORICOS.VALOR)*100) AS VALOR,
            CASE PERSONAL.TIPOCUENTA
                WHEN 'A' THEN 'CA'
                WHEN 'C' THEN 'CC'
                ELSE 'OP'
            END AS TIPO_TRANSACCION,
            CASE PERSONAL.DCTO_IDENTIDAD
                WHEN 'N' THEN '01'
                WHEN 'C' THEN '02'
                WHEN 'T' THEN '03'
                WHEN 'E' THEN '04'
                WHEN 'P' THEN '05'
                ELSE '00'
            END AS TIPO_DCTO
        FROM
            HISTORICOS
            INNER JOIN PERSONAL
                ON HISTORICOS.COMPANIA = PERSONAL.COMPANIA
                AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
            INNER JOIN BANCOS_NOMINA
                ON PERSONAL.BANCO = BANCOS_NOMINA.BANCO
                AND PERSONAL.COMPANIA = BANCOS_NOMINA.COMPANIA
        WHERE
            HISTORICOS.ANO = UN_ANO
            AND HISTORICOS.MES = UN_MES
            AND HISTORICOS.ID_DE_PROCESO = UN_PROCESO
            AND HISTORICOS.PERIODO = UN_PERIODO
            AND HISTORICOS.COMPANIA = UN_COMPANIA
            AND HISTORICOS.ID_DE_CONCEPTO = '144'
            AND PERSONAL.BANCO NOT IN ('00','98','99')
            AND BANCOS_NOMINA.CUENTA IS NOT NULL
            AND BANCOS_NOMINA.CUENTA NOT IN ('NA')
            AND BANCOS_NOMINA.BANCO = CASE WHEN UN_TODOSLOSBANCOS <> 0 THEN BANCOS_NOMINA.BANCO ELSE UN_BANCO END
          GROUP BY
            PERSONAL.NUMERO_DCTO,
            BANCOS_NOMINA.CODIGO_ENTIDAD,
            PERSONAL.CUENTA,
            PERSONAL.TIPOCUENTA,
            PERSONAL.DCTO_IDENTIDAD
    ) LOOP
        BEGIN
            MI_DATOSDISCO := MI_DATOSDISCO || 'TR' || LPAD(MI_RS.DOCUMENTO,16,'0') || LPAD('0',16,'0') || LPAD(MI_RS.CUENTA,16,'0') || MI_RS.TIPO_TRANSACCION ||
                             LPAD(MI_RS.BANCO,6,'0') || LPAD(MI_RS.VALOR,18,'0') || '000000' || MI_RS.TIPO_DCTO || '1' || '9999' ||
                             LPAD('00',40,'0') || LPAD('00',18,'0') || LPAD('00',8,'0') || LPAD('00',4,'0') || LPAD('00',4,'0') ||
                             LPAD('00',7,'0') || CHR(13) || CHR(10);
            MI_TOTALTRASLADOS := MI_TOTALTRASLADOS + 1;
            MI_VALTOTALTRASLADOS := MI_VALTOTALTRASLADOS + MI_RS.VALOR;
        END;
    END LOOP;

    MI_ENCABEZADO := 'RC' || LPAD(MI_NITCOMPANIA,16,'0') || 'NOMI' || '0000' || LPAD(MI_CUENTABANCO,16,'0') || 
                            MI_TIPOCUENTABANCO || '000051' || LPAD(MI_VALTOTALTRASLADOS,18,'0') || 
                            LPAD(MI_TOTALTRASLADOS,6,'0') || TO_CHAR(UN_FECHAPROCESO,'YYYYMMDD') || TO_CHAR(SYSDATE,'HH24MISS')||
                            '0000' || '9999' || '00000000' || '000000' || '00' || '01' || '000000000000' ||
                            '0000' ||LPAD('0',40,'0') || CHR(13) || CHR(10) ;

    MI_DATOSDISCO := MI_ENCABEZADO || MI_DATOSDISCO;    

  RETURN MI_DATOSDISCO;
END FC_DISCODAVIVIENDAIDI;

FUNCTION FC_DISCOBANCOPOPULAR 
  /*
      NAME              : FC_DISCOBANCOPOPULAR
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
      DATE MIGRADOR     : 13/09/2018
      TIME              : 10:10 AM
      SOURCE MODULE     : NOMINAP2018.09.01_UNIFICADAS MPV 04092018_MPV - 463 NIIF ANE
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PERMITE GENERAR ARCHIVOS PLANO DE ACUERDO A ESTRUCTURA DEL BANCO POPULAR
      PARAMETERS        : 
      MODIFICATIONS     : 
     @NAME: discoBancoPopular
    */
(
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO          IN HISTORICOS.ID_DE_PROCESO%TYPE,
    UN_ANIO             IN HISTORICOS.ANO%TYPE,
    UN_MES              IN HISTORICOS.MES%TYPE,
    UN_PERIODO          IN HISTORICOS.PERIODO%TYPE,
    UN_BANCO            IN BANCO.BANCO%TYPE,    
    UN_FECHAREPORTE     IN DATE,            
    UN_OBSERVACION      IN VARCHAR2
)
RETURN CLOB 
AS 
MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_CADENA         CLOB;
MI_VALORTOTAL     HISTORICOS.VALOR%TYPE;
MI_CANTIDAD       PCK_SUBTIPOS.TI_ENTERO;
MI_NITCOMPANIA    PARAMETROS_DE_ENTRADA.NIT%TYPE;
MI_RAZONSOCIAL    PARAMETROS_DE_ENTRADA.RAZONSOCIAL%TYPE; 
MI_CUENTABANCO    VARCHAR2(20);
MI_TIPOCUENTA     VARCHAR2(1);
MI_APELLIDO1      VARCHAR2(30);
MI_APELLIDO2      VARCHAR2(30);
MI_NOMBRES        VARCHAR2(255); 
BEGIN

  BEGIN
    SELECT SUM (HISTORICOS.VALOR),
            COUNT (1) NUMERO,
            COMPANIA.NITCOMPANIA,
            COMPANIA.NOMBRE,
            BANCOS_NOMINA.CUENTA,
            BANCOS_NOMINA.TIPO_CUENTA
       INTO MI_VALORTOTAL, 
            MI_CANTIDAD,
            MI_NITCOMPANIA, 
            MI_RAZONSOCIAL,
            MI_CUENTABANCO,
            MI_TIPOCUENTA
    FROM HISTORICOS INNER JOIN PERSONAL ON (HISTORICOS.COMPANIA = PERSONAL.COMPANIA) 
    AND (HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO)
    INNER JOIN BANCOS_NOMINA ON (PERSONAL.COMPANIA = BANCOS_NOMINA.COMPANIA) 
    AND (PERSONAL.BANCO = BANCOS_NOMINA.BANCO)
    INNER JOIN COMPANIA ON (PERSONAL.COMPANIA = COMPANIA.CODIGO)
    WHERE HISTORICOS.COMPANIA       = UN_COMPANIA
      AND HISTORICOS.ID_DE_PROCESO  = UN_PROCESO
      AND HISTORICOS.ANO            = UN_ANIO
      AND HISTORICOS.MES            = UN_MES
      AND HISTORICOS.PERIODO        = UN_PERIODO
      AND HISTORICOS.ID_DE_CONCEPTO = '144'  
      AND PERSONAL.BANCO NOT IN ('00','99')
      AND PERSONAL.BANCO            = UN_BANCO        
      GROUP BY COMPANIA.NITCOMPANIA,
      COMPANIA.NOMBRE,
      BANCOS_NOMINA.CUENTA,
      BANCOS_NOMINA.TIPO_CUENTA;
    EXCEPTION WHEN NO_DATA_FOUND THEN
     RAISE PCK_EXCEPCIONES.EXC_NOMINA;       
  END;      

  MI_CUENTABANCO :=REPLACE(MI_CUENTABANCO, '.', '');
  MI_CUENTABANCO :=REPLACE(MI_CUENTABANCO, '-', '');           
  MI_NITCOMPANIA :=REPLACE(MI_NITCOMPANIA, '.', '');
  MI_NITCOMPANIA :=REPLACE(MI_NITCOMPANIA, '-', '');      

  MI_CADENA :=  '01' || 
                TO_CHAR(UN_FECHAREPORTE, 'YYYYMMDD')                             || 
                RPAD(MI_RAZONSOCIAL,16,' ')                                      ||   
                CASE WHEN NVL(MI_TIPOCUENTA,'A') = 'C' THEN '110' ELSE '000' END ||                         
                RPAD(MI_CUENTABANCO,9)                                           ||                      
                RPAD(NVL(SUBSTR(MI_NITCOMPANIA,1,INSTR(MI_NITCOMPANIA,'-') - 1),MI_NITCOMPANIA),10) ||  
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CANTIDAD, 
                                          UN_LONGITUD => 6)                      ||  
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => (MI_VALORTOTAL * 100), 
                                          UN_LONGITUD => 18)                     ||   
                LPAD(' ',12,' ')                                                 ||  
                RPAD(UN_OBSERVACION,10,' ')                                      ||                   
                LPAD(' ',26,' ')                                                 ||  
                LPAD(' ',1,' ')                                                  ||  
                LPAD(' ',44,' ')                                                 ||  
                RPAD('V',2,' ')                                                  ||                   
                LPAD(' ',40,' ')                                                                                                                               
                ||  CHR(13) || CHR(10);     

    FOR RS IN (
    SELECT 
           PERSONAL.NUMERO_DCTO,
           PERSONAL.APELLIDO1, 
           PERSONAL.APELLIDO2,
           PERSONAL.NOMBRES,
           BANCOS_NOMINA.CODIGO_ENTIDAD,
           PERSONAL.CUENTA,
           PERSONAL.TIPOCUENTA,
           HISTORICOS.VALOR,
           PERSONAL.ID_DE_EMPLEADO,
           PERSONAL.DCTO_IDENTIDAD
    FROM HISTORICOS INNER JOIN PERSONAL ON (HISTORICOS.COMPANIA = PERSONAL.COMPANIA) 
    AND (HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO)
    INNER JOIN BANCOS_NOMINA ON (PERSONAL.COMPANIA = BANCOS_NOMINA.COMPANIA) 
    AND (PERSONAL.BANCO = BANCOS_NOMINA.BANCO)
    INNER JOIN COMPANIA ON PERSONAL.COMPANIA = COMPANIA.CODIGO
    WHERE HISTORICOS.COMPANIA       = UN_COMPANIA
      AND HISTORICOS.ID_DE_PROCESO  = UN_PROCESO
      AND HISTORICOS.ANO            = UN_ANIO
      AND HISTORICOS.MES            = UN_MES
      AND HISTORICOS.PERIODO        = UN_PERIODO
      AND HISTORICOS.ID_DE_CONCEPTO = '144'  
      AND PERSONAL.BANCO NOT IN ('00','99')
      AND PERSONAL.BANCO            = UN_BANCO        
      ORDER BY PERSONAL.APELLIDO1
            || ' '
            || PERSONAL.APELLIDO2
            || ' '
            || PERSONAL.NOMBRES)
    LOOP          
        IF RS.CODIGO_ENTIDAD IS NULL THEN
          MI_MSGERROR(1).CLAVE := 'EMPLEADO';
          MI_MSGERROR(1).VALOR := RS.ID_DE_EMPLEADO || ' ' || RS.APELLIDO1 || ' ' || RS.APELLIDO2 || ' ' || RS.NOMBRES; 
          PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => UN_COMPANIA,
                                     UN_MENSAJE_COD => PCK_ERRORES.ALER_CODENTIDADFINANCIERA,
                                     UN_REEMPLAZOS  => MI_MSGERROR);           
        END IF;    

        MI_APELLIDO1 := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(TRIM(RS.APELLIDO1), ''));
        MI_APELLIDO2 := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(TRIM(RS.APELLIDO2), ''));
        MI_NOMBRES := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(TRIM(RS.NOMBRES), ''));      

         MI_CADENA := MI_CADENA                                                               ||  
            /*CAMPO1*/'02'                                                                    ||
            /*CAMPO2*/RPAD(RS.NUMERO_DCTO, 15)                                                ||      
            /*CAMPO3*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.VALOR, 
                                                UN_LONGITUD => 16)                            ||     
                      '00'                                                                    ||                                                                                            
            /*CAMPO4*/RPAD(MI_APELLIDO1 || ' ' || MI_APELLIDO2 || ' ' || MI_NOMBRES, 22,' ')  ||                          
            /*CAMPO5*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.CODIGO_ENTIDAD, 
                                                UN_LONGITUD => 9)                             ||                 
            /*CAMPO6*/CASE WHEN RS.TIPOCUENTA = 'A' THEN '32' ELSE '22' END                   ||
                CASE WHEN MI_NITCOMPANIA = '891855138' THEN '32' ELSE '' END                  ||
                CASE WHEN MI_NITCOMPANIA = '891855138' THEN RPAD(REPLACE(REPLACE(RS.CUENTA, '.', ''), '-', ''), 15) ELSE
            /*CAMPO7*/RPAD(REPLACE(REPLACE(RS.CUENTA, '.', ''), '-', ''), 17) END             ||  
            /*CAMPO8*/RPAD(NVL(SUBSTR(MI_NITCOMPANIA,1,INSTR(MI_NITCOMPANIA,'-') - 1),MI_NITCOMPANIA),15) ||  
            /*CAMPO9*/RPAD(' ',2,' ')                                                         ||
           /*CAMPO10*/RPAD(UN_OBSERVACION,10,' ')                                             ||                   
           /*CAMPO11*/RPAD(('NOMINA ' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MES) || '/' || UN_ANIO),53) ||
           /*CAMPO12*/RPAD('V',2,' ')                                                         ||                                                           
           /*CAMPO13*/RPAD(' ',40,' ')  || CHR(10);        
    END LOOP; 

  RETURN MI_CADENA;
END FC_DISCOBANCOPOPULAR;  

FUNCTION FC_DISCOBANCOPOPULARGEN 
  /*
      NAME              : FC_DISCOBANCOPOPULARGEN
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LINA PAOLA VEGA
      DATE MIGRADOR     : 13/09/2018
      TIME              : 10:10 AM
      SOURCE MODULE     : NOMINAP2018.09.01_UNIFICADAS MPV 04092018_MPV - 463 NIIF ANE
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PERMITE GENERAR ARCHIVOS PLANO DE ACUERDO A ESTRUCTURA DEL BANCO POPULAR PARA TODOS LOS BANCOS
      PARAMETERS        : 
      MODIFICATIONS     : 
     @NAME: discoBancoPopularGen
    */
    (
        UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_PROCESO          IN HISTORICOS.ID_DE_PROCESO%TYPE,
        UN_ANIO             IN HISTORICOS.ANO%TYPE,
        UN_MES              IN HISTORICOS.MES%TYPE,
        UN_PERIODO          IN HISTORICOS.PERIODO%TYPE,
        UN_BANCO            IN BANCO.BANCO%TYPE,
        UN_TODOSLOSBANCOS   IN PCK_SUBTIPOS.TI_LOGICO,
        UN_FECHAREPORTE     IN DATE,            
        UN_OBSERVACION      IN VARCHAR2,
        UN_TCUENTABANORIGEN IN VARCHAR2,   
        UN_CUENTABANORIGEN  IN VARCHAR2 
        
    )
    RETURN CLOB 
    AS 
        MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
        MI_CADENA         CLOB;
        MI_VALORTOTAL     HISTORICOS.VALOR%TYPE;
        MI_CANTIDAD       PCK_SUBTIPOS.TI_ENTERO;
        MI_NITCOMPANIA    COMPANIA.NITCOMPANIA%TYPE;
        MI_RAZONSOCIAL    COMPANIA.NOMBRE%TYPE;
        MI_CUENTABANCO    BANCOS_NOMINA.CUENTA%TYPE;
        MI_TIPOCUENTA     VARCHAR2(1);
        MI_APELLIDO1      VARCHAR2(30);
        MI_APELLIDO2      VARCHAR2(30);
        MI_NOMBRES        VARCHAR2(255);
       
    BEGIN
    
      BEGIN
        SELECT SUM (HISTORICOS.VALOR),
                COUNT (PERSONAL.BANCO) NUMERO,
                COMPANIA.NITCOMPANIA,
                COMPANIA.NOMBRE,            
                BANCOS_NOMINA.CUENTA,
                BANCOS_NOMINA.TIPO_CUENTA
           INTO MI_VALORTOTAL, 
                MI_CANTIDAD,
                MI_NITCOMPANIA, 
                MI_RAZONSOCIAL,
                MI_CUENTABANCO,
                MI_TIPOCUENTA
        
        FROM HISTORICOS INNER JOIN PERSONAL ON (HISTORICOS.COMPANIA = PERSONAL.COMPANIA) 
        AND (HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO)
        INNER JOIN BANCOS_NOMINA ON (PERSONAL.COMPANIA = BANCOS_NOMINA.COMPANIA) 
        AND (PERSONAL.BANCO = BANCOS_NOMINA.BANCO)
        INNER JOIN COMPANIA ON (PERSONAL.COMPANIA = COMPANIA.CODIGO)
        WHERE HISTORICOS.COMPANIA       = UN_COMPANIA
          AND HISTORICOS.ID_DE_PROCESO  = UN_PROCESO
          AND HISTORICOS.ANO            = UN_ANIO
          AND HISTORICOS.MES            = UN_MES
          AND HISTORICOS.PERIODO        = UN_PERIODO
          AND HISTORICOS.ID_DE_CONCEPTO = '144'
          AND BANCOS_NOMINA.CUENTA = UN_CUENTABANORIGEN
          AND BANCOS_NOMINA.TIPO_CUENTA = UN_TCUENTABANORIGEN  
          AND PERSONAL.BANCO NOT IN ('00','99')
          AND PERSONAL.BANCO            = CASE WHEN UN_TODOSLOSBANCOS <> 0 THEN PERSONAL.BANCO ELSE UN_BANCO END        
         
          GROUP BY COMPANIA.NITCOMPANIA,
          COMPANIA.NOMBRE,
          BANCOS_NOMINA.CUENTA,
          BANCOS_NOMINA.TIPO_CUENTA;
          
        EXCEPTION WHEN NO_DATA_FOUND THEN
         RAISE PCK_EXCEPCIONES.EXC_NOMINA; 
        
          END;      

           
  MI_CUENTABANCO :=REPLACE(UN_CUENTABANORIGEN, '.', '');
  MI_CUENTABANCO :=REPLACE(MI_CUENTABANCO, '-', '');           
  MI_NITCOMPANIA :=REPLACE(MI_NITCOMPANIA, '.', '');
  MI_NITCOMPANIA :=REPLACE(MI_NITCOMPANIA, '-', '');      

MI_CADENA :=  '01' || 
                TO_CHAR(UN_FECHAREPORTE, 'YYYYMMDD')                             || 
                RPAD(MI_RAZONSOCIAL,16,' ')                                      ||   
                CASE WHEN NVL(MI_TIPOCUENTA,'A') = 'C' THEN '110' ELSE '000' END ||                         
                RPAD(MI_CUENTABANCO,9)                                           ||                      
                RPAD(NVL(SUBSTR(MI_NITCOMPANIA,1,INSTR(MI_NITCOMPANIA,'-') - 1),MI_NITCOMPANIA),10) ||  
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CANTIDAD, 
                                          UN_LONGITUD => 6)                      ||  
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => (MI_VALORTOTAL * 100), 
                                          UN_LONGITUD => 18)                     ||   
                LPAD(' ',12,' ')                                                 ||  
                RPAD(UN_OBSERVACION,10,' ')                                      ||                   
                LPAD(' ',26,' ')                                                 ||  
                LPAD(' ',1,' ')                                                  ||  
                LPAD(' ',44,' ')                                                 ||  
                RPAD('V',2,' ')                                                  ||                   
                LPAD(' ',40,' ')                                                                                                                               
                ||  CHR(13) || CHR(10);      

    FOR RS IN (
    SELECT 
           PERSONAL.NUMERO_DCTO,
           PERSONAL.APELLIDO1, 
           PERSONAL.APELLIDO2,
           PERSONAL.NOMBRES,
           BANCOS_NOMINA.CODIGO_ENTIDAD,
           PERSONAL.CUENTA,
           PERSONAL.TIPOCUENTA,
           HISTORICOS.VALOR,
           PERSONAL.ID_DE_EMPLEADO,
           PERSONAL.DCTO_IDENTIDAD
    FROM HISTORICOS INNER JOIN PERSONAL ON (HISTORICOS.COMPANIA = PERSONAL.COMPANIA) 
    AND (HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO)
    INNER JOIN BANCOS_NOMINA ON (PERSONAL.COMPANIA = BANCOS_NOMINA.COMPANIA) 
    AND (PERSONAL.BANCO = BANCOS_NOMINA.BANCO)
    INNER JOIN COMPANIA ON PERSONAL.COMPANIA = COMPANIA.CODIGO
    WHERE HISTORICOS.COMPANIA       = UN_COMPANIA
      AND HISTORICOS.ID_DE_PROCESO  = UN_PROCESO
      AND HISTORICOS.ANO            = UN_ANIO
      AND HISTORICOS.MES            = UN_MES
      AND HISTORICOS.PERIODO        = UN_PERIODO
      AND HISTORICOS.ID_DE_CONCEPTO = '144'  
      AND PERSONAL.BANCO NOT IN ('00','99')
      AND PERSONAL.BANCO            = CASE WHEN UN_TODOSLOSBANCOS <> 0 THEN PERSONAL.BANCO ELSE UN_BANCO END        
      ORDER BY PERSONAL.APELLIDO1
            || ' '
            || PERSONAL.APELLIDO2
            || ' '
            || PERSONAL.NOMBRES)
    LOOP          
        IF RS.CODIGO_ENTIDAD IS NULL THEN
          MI_MSGERROR(1).CLAVE := 'EMPLEADO';
          MI_MSGERROR(1).VALOR := RS.ID_DE_EMPLEADO || ' ' || RS.APELLIDO1 || ' ' || RS.APELLIDO2 || ' ' || RS.NOMBRES; 
          PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => UN_COMPANIA,
                                     UN_MENSAJE_COD => PCK_ERRORES.ALER_CODENTIDADFINANCIERA,
                                     UN_REEMPLAZOS  => MI_MSGERROR);           
        END IF;    

        MI_APELLIDO1 := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(TRIM(RS.APELLIDO1), ''));
        MI_APELLIDO2 := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(TRIM(RS.APELLIDO2), ''));
        MI_NOMBRES := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(TRIM(RS.NOMBRES), ''));      

         MI_CADENA := MI_CADENA                                                               ||  
            /*CAMPO1*/'02'                                                                    ||
            /*CAMPO2*/RPAD(RS.NUMERO_DCTO, 15)                                                ||      
            /*CAMPO3*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.VALOR, 
                                                UN_LONGITUD => 16)                            ||     
                      '00'                                                                    ||                                                                                            
            /*CAMPO4*/RPAD(MI_APELLIDO1 || ' ' || MI_APELLIDO2 || ' ' || MI_NOMBRES, 22,' ')  ||                          
            /*CAMPO5*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.CODIGO_ENTIDAD, 
                                                UN_LONGITUD => 9)                             ||                 
            /*CAMPO6*/CASE WHEN RS.TIPOCUENTA = 'A' THEN '32' ELSE '22' END                   ||
                CASE WHEN MI_NITCOMPANIA = '891855138' THEN '32' ELSE '' END                  ||
                CASE WHEN MI_NITCOMPANIA = '891855138' THEN RPAD(REPLACE(REPLACE(RS.CUENTA, '.', ''), '-', ''), 15) ELSE
            /*CAMPO7*/RPAD(REPLACE(REPLACE(RS.CUENTA, '.', ''), '-', ''), 17) END             ||  
            /*CAMPO8*/RPAD(NVL(SUBSTR(MI_NITCOMPANIA,1,INSTR(MI_NITCOMPANIA,'-') - 1),MI_NITCOMPANIA),15) ||  
            /*CAMPO9*/RPAD(' ',2,' ')                                                         ||
           /*CAMPO10*/RPAD(UN_OBSERVACION,10,' ')                                             ||                   
           /*CAMPO11*/RPAD(('NOMINA ' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MES) || '/' || UN_ANIO),53) ||
           /*CAMPO12*/RPAD('V',2,' ')                                                         ||                                                           
           /*CAMPO13*/RPAD(' ',40,' ')  || CHR(10);         
    END LOOP; 

  RETURN MI_CADENA;
  
END FC_DISCOBANCOPOPULARGEN; 

FUNCTION FC_DISCOBBVA_CASH_DUITAMA
/*
  NAME              : FC_DISCOBBVA_CASH_DUITAMA
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : JUAN DANILO ORDUZ RIVERO
  DATE MIGRATION    : 06/02/2021
  TIME              : 10:00 AM
  SOURCE MODULE     : NOMINA (6)
  MODIFIED BY       : 
  MODIFICATIONS     : 
  DESCRIPTION       : FunciÃ³n que permite generar archivo plano para Banco BBVA CASH
  PARAMETERS        : 
  @Name             :generarDiscobbva_Cash
  @Method           :GET
*/
(
    UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO          IN  PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_PERIODO          IN  PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_MES              IN  PCK_SUBTIPOS.TI_MES,
    UN_ANO              IN  PCK_SUBTIPOS.TI_ANIO,
    UN_FECHAEMISION     IN  DATE,
    UN_BANCO            IN  BANCO.BANCO%TYPE

)
RETURN CLOB AS 

    MI_DATOSDISCO1          CLOB;
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CTA                  VARCHAR2(30);
    MI_DCTO                 VARCHAR2(30);
    MI_TIPOCTA              VARCHAR2(30);
    MI_TIPOCUENTABBVA       VARCHAR2(30);
    MI_OFICINABBVA          VARCHAR2(30);
    MI_CUENTABBVA           VARCHAR2(30);
    MI_CUENTANACHA          VARCHAR2(30);
    MI_CUENTAS              VARCHAR2(30);
    MI_ANO                  VARCHAR2(30);
    MI_MES                  VARCHAR2(30);
    MI_DIA                  VARCHAR2(30);
    MI_MESLET               VARCHAR2(30);
    MI_CADENANOMINA         VARCHAR2(280);
BEGIN

    -- Consulta para obtener los datos de los empleados
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
            HISTORICOS.VALOR AS VALOR, 
            PERSONAL.BANCO, 
            PERSONAL.TIPOCUENTA,
            (CASE WHEN LENGTH(PERSONAL.CUENTA) = 20 AND LPAD(BANCOS_NOMINA.CODIGO_ENTIDAD,4,'0') = '0013' THEN 
              SUBSTR(PERSONAL.CUENTA,6,LENGTH(PERSONAL.CUENTA))
            ELSE 
                CASE WHEN (LENGTH(PERSONAL.CUENTA) = 10 OR LENGTH(PERSONAL.CUENTA) = 16) AND LPAD(BANCOS_NOMINA.CODIGO_ENTIDAD,4,'0') = '0013' THEN 
                 SUBSTR( PERSONAL.CUENTA,2,LENGTH(PERSONAL.CUENTA))
                ELSE 
                 PERSONAL.CUENTA
                END
            END)
             AS CUENTAEMPLEADO,
            BANCOS_NOMINA.CUENTA AS CUENTAORIGEN, 
            LPAD(BANCOS_NOMINA.CODIGO_ENTIDAD,4,'0') AS CODIGO_ENTIDAD, 
            PERSONAL.NUMERO_DCTO, 
            BANCOS_NOMINA.PAGOBANCOLOMBIA,
            PERSONAL.DCTO_IDENTIDAD,
            PERSONAL.EMAIL_PERSONAL,
            PERSONAL.DIRECCION
        FROM HISTORICOS 
        LEFT JOIN PERSONAL 
            ON HISTORICOS.COMPANIA = PERSONAL.COMPANIA
            AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
        LEFT JOIN BANCOS_NOMINA 
            ON PERSONAL.BANCO = BANCOS_NOMINA.BANCO
            AND PERSONAL.COMPANIA = BANCOS_NOMINA.COMPANIA
        WHERE 
            HISTORICOS.COMPANIA = UN_COMPANIA 
            AND HISTORICOS.ID_DE_PROCESO = UN_PROCESO
            AND HISTORICOS.ANO = UN_ANO
            AND HISTORICOS.MES = UN_MES
            AND HISTORICOS.PERIODO = UN_PERIODO
            AND HISTORICOS.ID_DE_CONCEPTO = '144'
            AND LPAD(PERSONAL.BANCO,4,'0') IN('0013')
            --AND PERSONAL.BANCO <> '00' 
            --AND PERSONAL.BANCO <> '99' 
            AND BANCOS_NOMINA.PAGOBANCOLOMBIA <> 0           
        ORDER BY
        PERSONAL.NOMBRECOMPLETO

    ) LOOP
        BEGIN
            IF MI_RS.BANCO IS NULL THEN
              MI_MSGERROR(1).CLAVE := 'EMPLEADO';
              MI_MSGERROR(1).VALOR := MI_RS.NUMERO_DCTO; 
              PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => UN_COMPANIA,
                                         UN_MENSAJE_COD => PCK_ERRORES.ALER_CODENTIDADFINANCIERA,
                                         UN_REEMPLAZOS  => MI_MSGERROR);           
            END IF;        

        IF MI_RS.DCTO_IDENTIDAD = 'C' THEN MI_DCTO :='01'; END IF;

        IF MI_RS.TIPOCUENTA = 'A' THEN MI_TIPOCTA := '02'; ELSE MI_TIPOCTA := '01'; END IF;

        IF MI_RS.CODIGO_ENTIDAD = '0013'THEN
            MI_TIPOCUENTABBVA :=MI_RS.CODIGO_ENTIDAD || '00';
            MI_OFICINABBVA := '0' || SUBSTR(MI_RS.CUENTAEMPLEADO,1,3);
            MI_CUENTABBVA := MI_OFICINABBVA || '00' || SUBSTR(MI_RS.CUENTAEMPLEADO,(LENGTH (MI_RS.CUENTAEMPLEADO) -5),6);
            MI_CUENTANACHA := '0000000000000000000';
            MI_CUENTAS := SUBSTR(MI_RS.CUENTAEMPLEADO,(LENGTH (MI_RS.CUENTAEMPLEADO) -5),6);
        ELSE
            MI_TIPOCUENTABBVA := '000000';
            MI_CUENTABBVA := '                ';
            MI_CUENTAS := LPAD(MI_RS.CUENTAEMPLEADO, 17);
        END IF;

        MI_CTA := REPLACE (MI_RS.CUENTAEMPLEADO,'-','');
        MI_CTA := REPLACE (MI_CTA,'.','');
        MI_CTA := REPLACE (MI_CTA,'-','');

        MI_ANO :=  PCK_SYSMAN_UTL.FC_ANIO(UN_FECHAEMISION);
        MI_MES :=  PCK_SYSMAN_UTL.FC_MES(UN_FECHAEMISION);
        MI_DIA :=  PCK_SYSMAN_UTL.FC_DIA(UN_FECHAEMISION);

        MI_MESLET := LOWER(PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MES));
        MI_CADENANOMINA := 'nomina mes de '|| MI_MESLET || ' ' || UN_ANO; 

        IF MI_RS.CODIGO_ENTIDAD = '0013'THEN
            MI_DATOSDISCO1 :=   MI_DATOSDISCO1 || MI_DCTO || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RS.NUMERO_DCTO, UN_LONGITUD => 15) ||
                                '01' || LPAD(MI_RS.CODIGO_ENTIDAD,4,'0') || MI_OFICINABBVA || '00' || MI_TIPOCTA || '00' || MI_CUENTAS ||
                                MI_CUENTANACHA || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RS.VALOR, UN_LONGITUD => 13) || '00' || 
                                MI_ANO || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_MES, UN_LONGITUD => 2) || 
                                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_DIA, UN_LONGITUD => 2) || '0000' || RPAD(MI_RS.NOMBRECOMPLETO,36,' ') ||
                                RPAD(MI_RS.DIRECCION,36,' ') || RPAD(MI_RS.DIRECCION,36,' ') || RPAD(NVL(MI_RS.EMAIL_PERSONAL,'.'),48,' ') ||
                                RPAD(MI_CADENANOMINA,280,' ') || CHR(13) || CHR(10);
        ELSE 
             MI_DATOSDISCO1 :=   MI_DATOSDISCO1 || MI_DCTO || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RS.NUMERO_DCTO, UN_LONGITUD => 15) ||
                                '01' || LPAD(MI_RS.CODIGO_ENTIDAD,4,'0') || '0000000000000000' || MI_CUENTAS ||
                                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RS.VALOR, UN_LONGITUD => 13) || '00' || 
                                MI_ANO || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_MES, UN_LONGITUD => 2) || 
                                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_DIA, UN_LONGITUD => 2) || '0000' || RPAD(MI_RS.NOMBRECOMPLETO,36,' ') ||
                                RPAD(MI_RS.DIRECCION,36,' ') || RPAD(MI_RS.DIRECCION,36,' ') || RPAD(NVL(MI_RS.EMAIL_PERSONAL,'.'),48,' ') ||
                                RPAD(MI_CADENANOMINA,280,' ') || CHR(13) || CHR(10);
        END IF;

        END;
    END LOOP;

  RETURN MI_DATOSDISCO1;
END FC_DISCOBBVA_CASH_DUITAMA;

FUNCTION FC_DISCOCAJASOCIAL_DUITAMA
/*
  NAME              : FC_DISCOCAJASOCIAL_DUITAMA
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : JUAN DANILO ORDUZ RIVERO
  DATE MIGRATION    : 06/02/2021
  TIME              : 10:00 AM
  SOURCE MODULE     : NOMINA (6)
  MODIFIED BY       : 
  MODIFICATIONS     : 
  DESCRIPTION       : FunciÃ³n que permite generar archivo plano para Banco Caja Social en Duitama
  PARAMETERS        : 
  @Name             :generarDiscoCajaSocialDuitama
  @Method           :GET
*/
(
    UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO          IN  PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_PERIODO          IN  PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_MES              IN  PCK_SUBTIPOS.TI_MES,
    UN_ANO              IN  PCK_SUBTIPOS.TI_ANIO,
    UN_FECHAEMISION     IN  DATE,
    UN_CONCPAGO         IN  VARCHAR2,
    UN_BANCO            IN  BANCO.BANCO%TYPE

)
RETURN CLOB AS 


MI_CONCPAGO             VARCHAR2(250);
MI_DATOSDISCO           CLOB;
MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;


BEGIN

    -- Consulta para obtener los datos de los empleados
    FOR MI_RS IN (
       SELECT
            HISTORICOS.ID_DE_PROCESO,
            PERSONAL.NOMBRECOMPLETO,
            PERSONAL.NUMERO_DCTO,
            HISTORICOS.ID_DE_CONCEPTO,
            HISTORICOS.VALOR,
            PERSONAL.ID_DE_EMPLEADO,
            PERSONAL.BANCO,
            BANCOS_NOMINA.NOMBRE,
            PERSONAL.CUENTA,
            PERSONAL.TIPOCUENTA,
            TO_CHAR(PERIODOS.FECHAFINAL, 'DD/MM/YYYY') FECHAFINAL
        FROM PERSONAL 
            LEFT JOIN BANCOS_NOMINA 
            ON PERSONAL.BANCO = BANCOS_NOMINA.BANCO 
            AND PERSONAL.COMPANIA = BANCOS_NOMINA.COMPANIA
            LEFT JOIN HISTORICOS 
            ON PERSONAL.ID_DE_EMPLEADO = HISTORICOS.ID_DE_EMPLEADO
            AND PERSONAL.COMPANIA = HISTORICOS.COMPANIA
            LEFT JOIN PERIODOS
            ON PERIODOS.COMPANIA = HISTORICOS.COMPANIA
            AND PERIODOS.ID_DE_PROCESO = HISTORICOS.ID_DE_PROCESO
            AND PERIODOS.MES = HISTORICOS.MES
            AND PERIODOS.ANO = HISTORICOS.ANO
            AND PERIODOS.PERIODO = HISTORICOS.PERIODO
        WHERE 
            PERSONAL.COMPANIA = UN_COMPANIA
            AND HISTORICOS.ID_DE_CONCEPTO = '144' 
            AND PERSONAL.BANCO = UN_BANCO
            AND HISTORICOS.ID_DE_PROCESO = (CASE WHEN UN_PROCESO IN (0) THEN HISTORICOS.ID_DE_PROCESO ELSE TO_NUMBER(UN_PROCESO) END)
            AND HISTORICOS.ANO = UN_ANO
            AND HISTORICOS.MES = UN_MES
            AND HISTORICOS.PERIODO = UN_PERIODO

    ) LOOP
        BEGIN
            IF MI_RS.BANCO IS NULL THEN
              MI_MSGERROR(1).CLAVE := 'EMPLEADO';
              MI_MSGERROR(1).VALOR := MI_RS.NUMERO_DCTO; 
              PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => UN_COMPANIA,
                                         UN_MENSAJE_COD => PCK_ERRORES.ALER_CODENTIDADFINANCIERA,
                                         UN_REEMPLAZOS  => MI_MSGERROR);           
            END IF;

            MI_CONCPAGO := UN_CONCPAGO || '     ' || PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MES) || UN_ANO;

            IF LENGTH(MI_CONCPAGO) <=30 THEN 
                MI_CONCPAGO := RPAD(MI_CONCPAGO,30,' ');
                ELSE 
                MI_CONCPAGO := SUBSTR(MI_CONCPAGO,0,29);
            END IF;
            MI_DATOSDISCO :=    MI_DATOSDISCO || '632' || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_RS.VALOR, UN_LONGITUD => 10) ||
                                '00' || RPAD(MI_RS.CUENTA,17,' ') || '000010320' || RPAD(MI_RS.NUMERO_DCTO,15,' ') ||
                                RPAD(MI_RS.NOMBRECOMPLETO, 22,' ') || 'V' || LPAD(' ',14,' ')  || MI_CONCPAGO ||LPAD('d',37,' ') ||

                                CHR(13) || CHR(10);


        END;
    END LOOP;

  RETURN MI_DATOSDISCO;
END FC_DISCOCAJASOCIAL_DUITAMA;

FUNCTION FC_DISCOFNACBY
/*
  NAME              : FC_DISCOFNACBY
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : JUAN DANILO ORDUZ RIVERO
  DATE MIGRATION    : 26/02/2021
  TIME              : 10:00 AM
  SOURCE MODULE     : NOMINA (6)
  MODIFIED BY       : JEIMMY CAROLINA ROJAS GUERRERO
  MODIFICATIONS     : 
  DESCRIPTION       : Funcion que permite generar archivo plano para el Fondo Nacional Del Ahorro
  PARAMETERS        : 
  @Name             :generarDiscofnacby
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
RETURN CLOB 
AS 
    MI_DATOSDISCO1          CLOB;
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_FECMES               VARCHAR2(280);
    MI_FIFNA                VARCHAR2(280);
    MI_FRFNA                VARCHAR2(280);
    MI_MES                  VARCHAR2(2);
    MI_ANO                  VARCHAR2(4);
    MI_PARPER4              VARCHAR2(3); --JM CC 2237
BEGIN
    --JM CC 2237 Excluirr periodo 4 de la consulta
    MI_PARPER4 := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'MOSTRAR PERIODO 4 EN PLANO CESANTIAS FNA', PCK_DATOS.FC_MODULONOMINA , SYSDATE),'SI');
   -- Consulta para obtener los datos de los empleados
    FOR MI_RS IN (
         SELECT 
            HISTORICOS.COMPANIA,
            HISTORICOS.ANO, 
            HISTORICOS.MES, 
            V_FONDO_DE_CESANTIAS.FONDO_CESANTIAS AS FONDO_CESANTIAS,
            HISTORICOS.ID_DE_EMPLEADO,
            TD.SIGLA2 AS CC,
            V_FONDO_DE_CESANTIAS.NIT AS NIT,
            V_FONDO_DE_CESANTIAS.NOMBRE_FONDO_CESANTIAS AS NOMBRE_FONDO_CESANTIAS,
            PERSONAL.NUMERO_DCTO AS NUMERO_DCTO1,
            HISTORICOS.ANO AS NUMERO_ANO,
            PERSONAL.APELLIDO1,
            PERSONAL.APELLIDO2,
            PERSONAL.NOMBRES,
            PARAMETROS_DE_ENTRADA.NIT AS NITCIA,
            PARAMETROS_DE_ENTRADA.CIUDAD,
            PARAMETROS_DE_ENTRADA.DEPARTAMENTO,
            CATEGORIA.SALARIO_BASE AS SALARIO_BASE,
            PERSONAL.FECHAFONDOCESANTIA,
            PERSONAL.FECHA_DE_INGRESO,
            PERSONAL.ESTADO_ACTUAL,
            SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 992 THEN HISTORICOS.VALOR ELSE 0 END) AS C992,
            SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 277 THEN HISTORICOS.VALOR ELSE 0 END) AS C277,
            SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 483 THEN HISTORICOS.VALOR ELSE 0 END) AS C483,
            SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 996 THEN HISTORICOS.VALOR ELSE 0 END) AS C996CONSOLIDADAS,
            SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 993 THEN HISTORICOS.VALOR ELSE 0 END) AS C993APLICADAS,
            SUM(CASE WHEN HISTORICOS.ID_DE_CONCEPTO = 997 THEN HISTORICOS.VALOR ELSE 0 END) AS C997DIFERENCIA,
            PERSONAL.FECHATERCONTRATO AS FR,
            PERIODOS.FECHAINICIO,
            PERIODOS.FECHAFINAL
        FROM (HISTORICOS
                LEFT JOIN (PERSONAL 
                    LEFT JOIN V_FONDO_DE_CESANTIAS
                        ON (PERSONAL.FONDO_CESANTIAS = V_FONDO_DE_CESANTIAS.FONDO_CESANTIAS)
                        AND (PERSONAL.COMPANIA = V_FONDO_DE_CESANTIAS.COMPANIA))
                    ON (HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO)
                    AND (HISTORICOS.COMPANIA = PERSONAL.COMPANIA))
                    LEFT JOIN PERIODOS
                        ON HISTORICOS.COMPANIA = PERIODOS.COMPANIA
                        AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                        AND HISTORICOS.ANO = PERIODOS.ANO
                        AND HISTORICOS.MES = PERIODOS.MES
                        AND HISTORICOS.PERIODO = PERIODOS.PERIODO
                    LEFT JOIN PARAMETROS_DE_ENTRADA 
                        ON HISTORICOS.COMPANIA = PARAMETROS_DE_ENTRADA.COMPANIA
                    LEFT JOIN CATEGORIA
                        ON PERSONAL.ANO = CATEGORIA.ANO 
                        AND PERSONAL.ID_DE_CATEGORIA = CATEGORIA.ID_DE_CATEGORIA
                        AND PERSONAL.ESCALAFON = CATEGORIA.ESCALAFON 
                        AND PERSONAL.COMPANIA = CATEGORIA.COMPANIA
                    LEFT JOIN TIPOS_DOCUMENTOS TD
                        ON TD.COMPANIA  =  PERSONAL.COMPANIA          
                        AND TD.DCTO_IDENTIDAD = PERSONAL.DCTO_IDENTIDAD
        WHERE
         HISTORICOS.ANO = UN_ANIO
         AND HISTORICOS.MES = UN_MES
         AND HISTORICOS.COMPANIA = CASE WHEN PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'FILTRADO POR COMPANIA PLANO FNA',6,SYSDATE) = 'SI' THEN UN_COMPANIA ELSE HISTORICOS.COMPANIA END
         AND PERIODOS.ACUMULADO =-1
         AND PERIODOS.PERIODO <> CASE WHEN MI_PARPER4 = 'NO' THEN 4 ELSE -1 END --JM CC 2237
         AND PERSONAL.FONDO_CESANTIAS = UN_BANCO

        GROUP BY
        HISTORICOS.COMPANIA,
        HISTORICOS.ANO,
        HISTORICOS.MES, 
        V_FONDO_DE_CESANTIAS.FONDO_CESANTIAS,
        HISTORICOS.ID_DE_EMPLEADO,
        TD.SIGLA2,
        V_FONDO_DE_CESANTIAS.NIT, 
        V_FONDO_DE_CESANTIAS.NOMBRE_FONDO_CESANTIAS,
        PERSONAL.NUMERO_DCTO,
        HISTORICOS.ANO,
        PERSONAL.APELLIDO1,
        PERSONAL.APELLIDO2, 
        PERSONAL.NOMBRES,
        PARAMETROS_DE_ENTRADA.NIT, 
        PARAMETROS_DE_ENTRADA.CIUDAD, 
        PARAMETROS_DE_ENTRADA.DEPARTAMENTO, 
        CATEGORIA.SALARIO_BASE, 
        PERSONAL.FECHAFONDOCESANTIA,
        PERSONAL.FECHA_DE_INGRESO,
        PERSONAL.ESTADO_ACTUAL,
        PERSONAL.FECHATERCONTRATO,
        PERIODOS.FECHAINICIO,
        PERIODOS.FECHAFINAL
        ORDER BY
        PERSONAL.APELLIDO1 ASC,
        PERSONAL.APELLIDO2 ASC,
        PERSONAL.NOMBRES ASC

    ) LOOP
        BEGIN

            MI_FIFNA := LPAD(' ',10,' ');
            IF MI_RS.FECHA_DE_INGRESO >= MI_RS.FECHAINICIO AND MI_RS.FECHA_DE_INGRESO <=  MI_RS.FECHAFINAL THEN 
                MI_FIFNA := LPAD(TO_CHAR(MI_RS.FECHA_DE_INGRESO,'DD/MM/YYYY'),10,' ');
            END IF;
            MI_FRFNA := LPAD(' ',10,' ');
            IF MI_RS.FR IS NOT NULL AND MI_RS.FR >= MI_RS.FECHAINICIO AND MI_RS.FR <=  MI_RS.FECHAFINAL THEN
                MI_FRFNA := LPAD(TO_CHAR(MI_RS.FR,'DD/MM/YYYY'),10,' ');
            END IF;
            MI_MES := CASE WHEN UN_MES13 NOT IN(0) THEN TO_CHAR(LPAD(13,2,'0')) ELSE TO_CHAR(LPAD(UN_MES,2,'0')) END; 
            MI_ANO := TO_CHAR(LPAD(UN_ANIO,4,'0'));


            MI_DATOSDISCO1 :=   MI_DATOSDISCO1 || LPAD(REPLACE(REPLACE((MI_RS.NITCIA),'.',''),'-',''),14,'0') || ',' || LPAD((MI_RS.NUMERO_DCTO1),11,'0') || ',' ||
                                NVL(MI_RS.CC,'CC') || ',' || RPAD(NVL(MI_RS.APELLIDO1,' '),25,' ') || ',' || RPAD(NVL(MI_RS.APELLIDO2,' '),25,' ') || ',' ||
                                RPAD(NVL(MI_RS.NOMBRES,' '),64,' ') || ',' || RPAD(MI_RS.DEPARTAMENTO,2,' ') || ',' || RPAD(MI_RS.CIUDAD,3,' ') || ',' ||
                                LPAD(TRUNC(MI_RS.SALARIO_BASE),12,'0') || ',' || LPAD(NVL(TRUNC(MI_RS.C992),'0'),12,'0') || ',' ||
                                LPAD(CASE WHEN MI_RS.C483 <> 0 THEN  MI_RS.C483 ELSE MI_RS.C277 END,12,'0') || ',' ||
                                MI_ANO || ',' || MI_MES || ',' ||
                                MI_FIFNA || ',' || MI_FRFNA  || ',' || (CASE WHEN MI_RS.ESTADO_ACTUAL = 1 THEN '1' ELSE (CASE WHEN MI_FRFNA <> '          ' THEN '2' ELSE '1' END) END) 
                                || ',' || CASE WHEN MI_RS.ESTADO_ACTUAL = 2 OR UN_MES = 12 THEN LPAD(MI_RS.C996CONSOLIDADAS,12,'0') ELSE LPAD('0',12,'0') END || ',' ||
                                CASE WHEN MI_RS.ESTADO_ACTUAL = 2 OR UN_MES = 12 THEN LPAD(MI_RS.C993APLICADAS,12,'0') ELSE LPAD('0',12,'0') END || ',' ||
                                CASE WHEN MI_RS.ESTADO_ACTUAL = 2 OR UN_MES = 12 THEN CASE WHEN MI_RS.C997DIFERENCIA < 0 THEN LPAD(-(MI_RS.C997DIFERENCIA),12,'0') ELSE LPAD((MI_RS.C997DIFERENCIA),12,'0') END ELSE LPAD('0',12,'0') END || ',' || CHR(13) || CHR(10);

        END;
    END LOOP;
    RETURN MI_DATOSDISCO1;

END FC_DISCOFNACBY;

PROCEDURE PR_ACTUALIZARNOVEDADESTODASN AS
/*
    NAME              : PR_ACTUALIZARNOVEDADESTODASN  -> EN ACCESS actualizarnovedadesTODASn
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA PINEDA
    DATE MIGRADOR     : 2018/09/15
    TIME              : 11:58 AM
    SOURCE MODULE     : NOMINAP2018.09.01_UNIFICADAS MPV 04092018_MPV - 463 NIIF ANE
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Se migra por diferencia valor encargo 20180915 ANE TAR 1000086753
  */
BEGIN      
    FOR I IN 1.. 999 LOOP
        IF I < 350 OR I > 360 THEN
            IF PCK_NOMINA.FC_CNAN(I) <> 0 THEN
                PCK_NOMINA.CN(I) := PCK_NOMINA.FC_CNAN(I);
            END IF;
        END IF;        
    END LOOP;
END PR_ACTUALIZARNOVEDADESTODASN;

PROCEDURE PR_BONDIRECCIONENTIDADNACIONAL 
/*
    NAME              : PR_BONDIRECCIONENTIDADNACIONAL
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA PINEDA
    DATE MIGRADOR     : 2018/09/17
    TIME              : 3:40 PM
    SOURCE MODULE     : NOMINAP2018.09.01_UNIFICADAS MPV 04092018_MPV - 463 NIIF ANE
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Se crea para realizar el cÃ¡lculo de bonificaciÃ³n por DirecciÃ³n ANE TAR1000086811
  */
AS 
    MI_PRIMATECNICA         NUMBER:=0;
    MI_VALORBONIFICACION    NUMBER:=0;
    MI_DOCEAVABONIFICACION  NUMBER:=0;    
    MI_DIASNOTRABAJADOSMES  NUMBER:=0;
    MI_DIASTRABAJADOS       NUMBER:=0;
    MI_VALORMES             NUMBER:=0;
    MI_VALORACUMULADO       NUMBER:=0;
    MI_INICIO               NUMBER:=1;
    MI_FECHAINICIO          DATE;
    MI_FECHAINGRESO         DATE;
    MI_NUMERODEDOCEAVAS     NUMBER := 0;    
    MI_MESFINAL             NUMBER;      
  --(APINEDA:20/12/2018)-Se crean variables para el beneficio bonificaciÃ³n por direcciÃ³n
    MI_PRIMATECNICAFS       NUMBER:=0;
    MI_PRIMATECNICANFS      NUMBER:=0;
    MI_ACUMDIASNOTRABAJADOS NUMBER:=0;
    MI_UPDATE               PCK_SUBTIPOS.TI_ENTERO;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
  MI_MSG                  PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CARGO = PCK_PARST.FC_PAR('CODIGO CARGO DIRECTOR(A) ENTIDAD DEL ORDEN NACIONAL','NO') THEN
        IF PCK_PARST.FC_PAR('LIQUIDAN BONIFICACION DE DIRECCION ENTIDADES DEL ORDEN NACIONAL','NO') = 'SI' THEN           
            --VÃ¡lido prima TÃ©cnica ya sea Factor Salarial (CN 186) o  la Prima TÃ©cnica No Factor Salarial (CN 188)
      --(APINEDA:20/12/2018)-Se pone valor de prima tecnica factor y no factor en variables diferentes para almacenar beneficios a empleado.
            IF PCK_NOMINA.FC_CN(87) > 0 THEN 
                MI_PRIMATECNICANFS := CASE WHEN PCK_NOMINA.FC_CN(188) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(1) * PCK_NOMINA.FC_CN(87)) / 100)+0.05 ,0) ELSE PCK_NOMINA.FC_CN(188) END;                
            ELSE
                MI_PRIMATECNICAFS := CASE WHEN PCK_NOMINA.FC_CN(186) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(1) * PCK_NOMINA.FC_CN(85)) / 100)+0.05 ,0) ELSE PCK_NOMINA.FC_CN(186) END;
            END IF;       
            MI_PRIMATECNICA := CASE WHEN MI_PRIMATECNICANFS <> 0 THEN MI_PRIMATECNICANFS ELSE MI_PRIMATECNICAFS END;
            MI_VALORBONIFICACION := ((CASE WHEN PCK_NOMINA.FC_CN(10) > 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) + PCK_NOMINA.FC_CN(61) + MI_PRIMATECNICA) * TO_NUMBER(PCK_PARST.FC_PAR('NUMERO DE SALARIOS A RECONOCER BONIFICACION DE DIRECCION ENTIDADES DEL ORDEN NACIONAL', '0'));             
            IF PCK_NOMINA.GL_SMES > 6 THEN                    
                MI_FECHAINICIO := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');                     
            ELSE
                MI_FECHAINICIO := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');                 
            END IF;     
            MI_FECHAINGRESO := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO;                
            MI_FECHAINICIO := PCK_SYSMAN_UTL.FC_IIF(MI_FECHAINGRESO > MI_FECHAINICIO, MI_FECHAINGRESO, MI_FECHAINICIO);              
            MI_MESFINAL := PCK_NOMINA.GL_SMES; 
            IF PCK_NOMINA.GL_SPER = 7 AND PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN                   
                MI_MESFINAL := PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO);                          
                MI_DIASTRABAJADOS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(TO_DATE('01/'|| MI_MESFINAL || '/' ||PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO), 'DD/MM/YYYY'), PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO);
                IF MI_DIASTRABAJADOS < 30 THEN
                    MI_MESFINAL := MI_MESFINAL - 1;
                END IF;
            END IF;            

            MI_NUMERODEDOCEAVAS := 0;    
            MI_DOCEAVABONIFICACION := PCK_SYSMAN_UTL.FC_ROUND(MI_VALORBONIFICACION / 12, 0);
            MI_INICIO := PCK_SYSMAN_UTL.FC_MES(MI_FECHAINICIO);    
      --(APINEDA:20/12/2018)-Se acumulan dÃ­as no trabajados
            MI_ACUMDIASNOTRABAJADOS := 0;
            FOR i IN MI_INICIO..MI_MESFINAL LOOP
                MI_NUMERODEDOCEAVAS := MI_NUMERODEDOCEAVAS + 1;
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO), i, 1, (PCK_NOMINA.GL_SANO), i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                MI_DIASNOTRABAJADOSMES := (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339));                
                IF (MI_DIASNOTRABAJADOSMES) > 0 THEN                       
                    MI_NUMERODEDOCEAVAS := MI_NUMERODEDOCEAVAS - 1;
                END IF;
                MI_ACUMDIASNOTRABAJADOS := MI_ACUMDIASNOTRABAJADOS + MI_DIASNOTRABAJADOSMES;
            END LOOP;                     
            MI_VALORACUMULADO := MI_DOCEAVABONIFICACION * MI_NUMERODEDOCEAVAS;     
            IF PCK_NOMINA.GL_SPER = 3 AND (PCK_NOMINA.GL_SMES = 6 OR PCK_NOMINA.GL_SMES = 12) THEN    
                  PCK_NOMINA.CN(172) := MI_VALORACUMULADO;  
            END IF;     
            IF PCK_NOMINA.GL_SPER = 7 AND PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN                   
               PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO PARA PAGO BONIFICACION DE DIRECCION ENTIDADES DEL ORDEN NACIONAL','0'))) := MI_VALORACUMULADO;                
         --(APINEDA:20/12/2018)-Se guardan factores de bonificaciÃ³n por direcciÃ³n y se actualizan en PROVISIONES_MENSUALES_NIIF
               PCK_NOMINA.CN(957) := MI_NUMERODEDOCEAVAS;
               PCK_NOMINA.CN(944) := MI_PRIMATECNICAFS;
               PCK_NOMINA.CN(970) := MI_PRIMATECNICANFS;
               PCK_NOMINA.CN(996) := (PCK_NOMINA.GL_SMES * 30);
               PCK_NOMINA.CN(997) := MI_ACUMDIASNOTRABAJADOS;
               --PCK_NOMINA.CN(Pendiente definir) := (PCK_NOMINA.GL_SMES * 30) - MI_ACUMDIASNOTRABAJADOS;
               PCK_NOMINA.CN(999) := MI_VALORBONIFICACION;

                BEGIN            
                    MI_CAMPOS := 'FECHAINGRESO = TO_DATE(''' || TO_CHAR(MI_FECHAINGRESO, 'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                                  BONDIR_FECHAINI = TO_DATE(''' || TO_CHAR(MI_FECHAINICIO, 'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                                  BONDIR_FECHAFIN = TO_DATE(''' || TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO, 'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                                  BONDIR_VPT_FS = '   || NVL(MI_PRIMATECNICAFS,0) ||', 
                                  BONDIR_VPT_NFS = '  || NVL(MI_PRIMATECNICANFS,0) ||', 
                                  BONDIR_DIAS = '   || NVL((PCK_NOMINA.GL_SMES * 30),0) ||',
                                  BONDIR_LNR = '    || NVL(MI_ACUMDIASNOTRABAJADOS,0) ||', 
                                  BONDIR_DIASSINLNR = ' || NVL(((PCK_NOMINA.GL_SMES * 30) - MI_ACUMDIASNOTRABAJADOS),0) ||',
                                  BONDIR_DOCEAVAS = ' || NVL(MI_NUMERODEDOCEAVAS,0) ||', 
                                  BONDIR_BASE = '   || NVL(MI_VALORBONIFICACION,0) ||',
                                  BONDIR_TOTAL = '    || NVL(MI_VALORACUMULADO,0) ||' ';                        

                    MI_CONDICION := 'COMPANIA= '''||PCK_NOMINA.GL_COMPANIA||'''
                                 AND ID_DE_PROCESO='|| 1 ||'
                                 AND ANO = '|| PCK_NOMINA.GL_SANO ||'
                                 AND MES = '|| PCK_NOMINA.GL_SMES ||'
                                 AND PERIODO = '|| 3||'
                                 AND ID_DE_EMPLEADO = '|| PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO ||'
                                 AND DCTO_IDENTIDAD =  '|| PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;

                    IF (PCK_NOMINA.GL_ESBONIFICACION) THEN
                        BEGIN
                            MI_UPDATE:=PCK_DATOS.FC_ACME(UN_TABLA     => 'PROVISIONES_MENSUALES_NIIF'
                            ,UN_ACCION    => 'M'
                            ,UN_CAMPOS    => MI_CAMPOS
                            ,UN_CONDICION => MI_CONDICION);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                        END;
                    END IF;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
                    MI_MSG (1).CLAVE := 'EMPLEADO';
                    MI_MSG (1).VALOR := ''||PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
                    MI_MSG (2).CLAVE := 'ANO';
                    MI_MSG (2).VALOR := PCK_NOMINA.GL_SANO;

                    PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD =>SQLCODE,
                    UN_ERROR_COD=>PCK_ERRORES.ERR_REGISTRANDOBENEFICIOBONDIR,
                    UN_REEMPLAZOS => MI_MSG);
                END;                              
            END IF;                       
        END IF;
    END IF;

END PR_BONDIRECCIONENTIDADNACIONAL;

FUNCTION FC_VALORACUMCONCEPTOYAJUSTE
/*
    NAME              : FC_VALORACUMCONCEPTOYAJUSTE
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
    DATE MIGRADOR     : 24/11/2018
    TIME              : 12:49
    SOURCE MODULE     : NUEVO - TAR 1000088289 ANE
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :
    DESCRIPTION       : Carga el Type CNP con el valor del concepto de prima de vacaciones mÃ¡s los ajustes a prima de vacaciones de acuerdo al periodo indicado por parametro.
  */
  ( 
    UN_ANO1         IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES1         IN PCK_SUBTIPOS.TI_MES,
    UN_PER1         IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ANO2         IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES2         IN PCK_SUBTIPOS.TI_MES,
    UN_PER2         IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_IDDEEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_CONCEPTOS    IN PCK_SUBTIPOS.TI_CLAVEVALOR
  )
RETURN NUMBER AS 
    MI_VALOR          PCK_SUBTIPOS.TI_DOBLE;
    MI_CONCEPTO       NUMBER;    
BEGIN                                                 
  MI_VALOR := 0;
  IF UN_CONCEPTOS.COUNT>0 THEN
    FOR i IN UN_CONCEPTOS.FIRST..UN_CONCEPTOS.LAST
    LOOP
      MI_CONCEPTO := UN_CONCEPTOS(i).VALOR;
      PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA   => PCK_NOMINA.GL_COMPANIA,
                                                          UN_CONCEPTO   => MI_CONCEPTO,
                                                          UN_ANO1       => UN_ANO1,
                                                          UN_MES1       => UN_MES1,
                                                          UN_PER1       => UN_PER1,
                                                          UN_ANO2       => UN_ANO2,
                                                          UN_MES2       => UN_MES2,
                                                          UN_PER2       => UN_PER2,
                                                          UN_IDDEEMPLEADO => UN_IDDEEMPLEADO);                                                                                                                
      MI_VALOR := MI_VALOR + PCK_NOMINA.FC_CNP(MI_CONCEPTO);        
    END LOOP;
  END IF;

  RETURN MI_VALOR;

END FC_VALORACUMCONCEPTOYAJUSTE;

FUNCTION FC_ACUMULARVALORCONCEPTO
/*
    NAME              : FC_ACUMULARVALORCONCEPTO
    AUTHORS           : CBARRERA
    DATE CREATE       : 31/12/2024
    TIME              : 12:49
    SOURCE MODULE     : NOMINA-BUCARAMANGA
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :
    DESCRIPTION       : Esta función carga el tipo CNA con el valor del concepto de prima de vacaciones, 
                        incluyendo los ajustes correspondientes al período indicado como parámetro. 
                        Posteriormente, almacena el valor de los conceptos evaluados y los acumula en MI_VALOR. 
*/
  ( 
    UN_ANO1         IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES1         IN PCK_SUBTIPOS.TI_MES,
    UN_PER1         IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ANO2         IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES2         IN PCK_SUBTIPOS.TI_MES,
    UN_PER2         IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_IDDEEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_CONCEPTO     IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
    UN_CONCEPTO2    IN PCK_SUBTIPOS.TI_CLAVEVALOR
  )
RETURN NUMBER AS 
    MI_VALOR          PCK_SUBTIPOS.TI_DOBLE;
    MI_CONCEPTO       NUMBER;    
BEGIN                                                 
  MI_VALOR := 0;
  
    FOR RS IN (SELECT ANO, MES, PERIODO, VALOR
FROM (
    SELECT HISTORICOS.ANO,
           HISTORICOS.MES,
           HISTORICOS.PERIODO,
           SUM(HISTORICOS.VALOR) AS VALOR,
           ROW_NUMBER() OVER (ORDER BY HISTORICOS.ANO DESC, HISTORICOS.MES DESC) AS RN
    FROM HISTORICOS
    INNER JOIN PERIODOS
        ON HISTORICOS.COMPANIA = PERIODOS.COMPANIA
        AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
        AND HISTORICOS.ANO = PERIODOS.ANO
        AND HISTORICOS.MES = PERIODOS.MES
        AND HISTORICOS.PERIODO = PERIODOS.PERIODO
    WHERE PERIODOS.COMPANIA = PCK_NOMINA.GL_COMPANIA
      AND PERIODOS.ID <= LPAD(UN_ANO1,4,'0') || LPAD(UN_MES1,2,'0') || LPAD(UN_PER1,2,'0')
      AND PERIODOS.ACUMULADO NOT IN (0)
      AND HISTORICOS.ID_DE_EMPLEADO = UN_IDDEEMPLEADO
      AND HISTORICOS.ID_DE_CONCEPTO = UN_CONCEPTO
      AND HISTORICOS.VALOR NOT IN (0)
    GROUP BY HISTORICOS.ANO, HISTORICOS.MES, HISTORICOS.PERIODO
) T
WHERE RN = 1)
    LOOP
    MI_VALOR := RS.VALOR;
    IF UN_CONCEPTO2.COUNT>0 THEN
    FOR i IN UN_CONCEPTO2.FIRST..UN_CONCEPTO2.LAST
    LOOP
      MI_CONCEPTO := UN_CONCEPTO2(i).VALOR;
      PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA   => PCK_NOMINA.GL_COMPANIA,
                                                          UN_CONCEPTO   => MI_CONCEPTO,
                                                          UN_ANO1       => RS.ANO,
                                                          UN_MES1       => RS.MES,
                                                          UN_PER1       => RS.PERIODO,
                                                          UN_ANO2       => UN_ANO2,
                                                          UN_MES2       => UN_MES2,
                                                          UN_PER2       => UN_PER2,
                                                          UN_IDDEEMPLEADO => UN_IDDEEMPLEADO);  
                                                                                                                
      MI_VALOR := MI_VALOR + PCK_NOMINA.FC_CNP(MI_CONCEPTO);
     END LOOP;
      END IF;

    END LOOP;

  RETURN MI_VALOR;

END FC_ACUMULARVALORCONCEPTO;

PROCEDURE PR_PRIMAVACACIONESBUCARAMANGA(
      /*
      NAME              : PR_PRIMAVACACIONESBUCARAMANGA
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
      DATE MIGRADOR     : 26/03/2019
      TIME              :
      SOURCE MODULE     : NOMINAP2018.05.05_UNIFICADAS En access calcularprimadevacacionesANE - NOMINAP2019.01.01 Proc01 Contraloria General de Cundinamarca
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       :
      @NAME:  CALCPRIMAVACACIONESBUCARAMANGA
      */
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA )
  AS
    MI_BONPAGADA            NUMBER DEFAULT 0;
    MI_MSG                  PCK_SUBTIPOS.TI_CLAVEVALOR;        
    MI_FACTORESVACACIONES   NUMBER DEFAULT 0; 
    MI_UPDATE               PCK_SUBTIPOS.TI_ENTERO;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;    
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;    
    MI_BASP                 NUMBER :=0;
    MI_PRIMASERVICIOS       NUMBER :=0;
    GL_DIASPRIMAVAC_SINRED NUMBER;--CFBARRERA-14-02-2025--CC:551
    GL_DIASPRIMAVAC_PDEC NUMBER;--CFBRAERA-14-02-2025--CC:551
  BEGIN

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

    --CC_4440(18/06/2026 JCROJAS): Se agrega acumulado y validacion para que, cuando se este liquidando el concepto 160, no tome el acumulado desde el mismo mes del anio anterior, sino desde el mes siguiente.
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, CASE WHEN PCK_NOMINA.FC_CNA(160) > 0 THEN PCK_NOMINA.GL_MESA+1 ELSE PCK_NOMINA.GL_MESA END, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

      MI_BASP := CASE WHEN PCK_NOMINA.FC_CN(150) <> 0 THEN PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) ELSE (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514)) END;
      
    IF PCK_NOMINA.FC_CN(160) <> 0 THEN --(CFBARRERA:CC_5511_29-04-2025_Ajuste para el caculo de manera semetral, tomar el valor de la ultima pagada)
         IF PCK_NOMINA.FC_CN(404) <> 0 THEN
                MI_PRIMASERVICIOS := (PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO));
            ELSE
                MI_PRIMASERVICIOS := (PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503));
            END IF; 
        ELSE
            --MI_PRIMASERVICIOS := PCK_NOMINA_COM4.FC_VALORACUMCONCEPTOYAJUSTEMAX(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 1, 160, 503); --MOD JM CC 3075
			MI_PRIMASERVICIOS := (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)); --CC_4024(23/04/2026 JCROJAS); Se le asigna el valor del acumulado																																		  
    END IF;--(CFBARRERA:CC_5511_29-04-2025_FIN)

    PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(
    (
    CASE
    WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
      PCK_NOMINA.FC_CN(10)
    ELSE
      PCK_NOMINA.FC_CN(1)
    END) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + (MI_BASP / 12) + (MI_PRIMASERVICIOS / 12), 0);

        MI_CAMPOS    := 'BASEIBC = ' || NVL(PCK_NOMINA.GL_FACTORESPV,0);

        MI_CONDICION := ' COMPANIA       = '''||UN_COMPANIA||'''
                      AND ID_DE_PROCESO  = 1
                      AND ANO            = '|| PCK_NOMINA.GL_SANO ||'
                      AND MES            = '|| PCK_NOMINA.GL_SMES ||'
                      AND PERIODO        = '|| PCK_NOMINA.GL_SPER ||'
                      AND ID_DE_EMPLEADO = '|| PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;                                        
        BEGIN
            BEGIN
                MI_UPDATE:=PCK_DATOS.FC_ACME(UN_TABLA => 'VACACIONES' ,UN_ACCION => 'M' ,UN_CAMPOS => MI_CAMPOS ,UN_CONDICION => MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            --OcurriÃ³ un problema al intentar almacenar la base para el cÃ¡lculo de Vacaciones, para el empleado --EMPLEADO--
            MI_MSGERROR (1).CLAVE := 'EMPLEADO';
            MI_MSGERROR (1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD =>SQLCODE, UN_ERROR_COD=>PCK_ERRORES.ERR_ACTUALIZARBASEVACACIONES, UN_REEMPLAZOS => MI_MSGERROR );
        END;      

      IF PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.GL_SPER = 8 THEN
        PCK_NOMINA.CN(984) := 0;

          PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ) / 12, 0);  --PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(160)) / 12, 0);
          IF (PCK_NOMINA.GL_SMES = 7 OR PCK_NOMINA.GL_SMES = 6) AND (PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) <> 0 AND PCK_NOMINA.FC_CN(160) <> 0) AND PCK_NOMINA.FC_CN(404) <> 0 THEN
            PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 160, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.FC_CN(160)= 0 AND PCK_NOMINA.FC_CN(160) > 0 THEN
              PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160)) / 12, 0);
            ELSIF PCK_NOMINA.FC_CNP(160) > 0 AND PCK_NOMINA.FC_CN(160) > 0 THEN
              PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(160) ) / 12, 0); --PCK_NOMINA.CN(981)                                                                             := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(160) + PCK_NOMINA.FC_CN(160)) / 12, 0);
            END IF;
            IF PCK_NOMINA.FC_CN(981) = 0 THEN
              PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(160)) / 12, 0);
            END IF;
          END IF;
        MI_BONPAGADA            := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(514)) / 12, 0); --MI_BONPAGADA            := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0);
        PCK_NOMINA.GL_AC        := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

        PCK_NOMINA.GL_LICENCIAS := NVL(((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.FC_CNA(339)) +
        (
          CASE
          WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
            PCK_NOMINA.FC_CESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 'L')
          ELSE
            0
          END) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION),0);
        PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91);
        PCK_NOMINA.GL_DTV            := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
        (
          (
            CASE
            WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
              PCK_NOMINA.GL_FECHAI
            ELSE
              (CASE WHEN (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1) BETWEEN PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.GL_FECHAFIN1 THEN PCK_NOMINA.GL_FECHAINI ELSE (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1) END)
            END), PCK_NOMINA.GL_FECHAFIN1)                    - PCK_NOMINA.GL_LICENCIAS;
        PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.GL_DTV           - PCK_NOMINA.GL_LICENCIAS;
        PCK_NOMINA.GL_PERIODOS                                                                  := TRUNC(PCK_NOMINA.GL_DTV/ 360);
        IF (PCK_NOMINA.GL_DTV                                                                                             - (360 * PCK_NOMINA.GL_PERIODOS)) >= 315 THEN
          PCK_NOMINA.GL_PERIODOS                                                                := PCK_NOMINA.GL_PERIODOS + 1;
        END IF;
        PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15                   * PCK_NOMINA.GL_PERIODOS, 2);
        PCK_NOMINA.CN(93)     := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) *
        (
          CASE
          WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN
            1
          ELSE
            PCK_NOMINA.GL_PERIODOS
          END), 2);
        IF PCK_NOMINA.GL_DIASVAC  = 0 THEN
          PCK_NOMINA.GL_PERIODOS :=
          (
            CASE
            WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN
              1
            ELSE
              PCK_NOMINA.GL_PERIODOS
            END);            
          PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 *
          (
            CASE
            WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN
              1
            ELSE
              PCK_NOMINA.GL_PERIODOS
            END) / 360 * PCK_NOMINA.GL_DTV, 0);
        END IF;
        PCK_NOMINA.GL_AC           := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

        PCK_NOMINA.GL_PROPDIASPRIMAVAC := ROUND(((15 / 360) * PCK_NOMINA.GL_DTV), 2);
        GL_DIASPRIMAVAC_SINRED:= TRUNC(((15 / 360) * PCK_NOMINA.GL_DTV));--CFBARRERA-14-02-2025--CC:551
        GL_DIASPRIMAVAC_PDEC := PCK_NOMINA.GL_PROPDIASPRIMAVAC - GL_DIASPRIMAVAC_SINRED;--CFBARRERA-14-02-2025--CC:551          

        IF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '01' THEN
          PCK_NOMINA.GL_FECHAFF    := NULL;
          PCK_NOMINA.GL_FECHAFF    := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') - 1, NVL(PCK_NOMINA.GL_DIASVAC, 1));
          IF PCK_NOMINA.GL_SPER     = 8 AND PCK_NOMINA.GL_SMES = 12 THEN
            PCK_NOMINA.GL_PERIODOS := 1;
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
            PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.GL_FECHAFF  := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);

            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
              PCK_NOMINA.CN(96)                                   := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
              PCK_NOMINA.CN(96)                                   := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
            END IF;
          END IF;
          IF PCK_NOMINA.FC_CN(96)  = 0 THEN
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.CN(96)     := (TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') - PCK_NOMINA.GL_FECHAFF) + 1;
            PCK_NOMINA.CN(96)     := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
            PCK_NOMINA.CN(96)     := PCK_NOMINA.CN(96) + GL_DIASPRIMAVAC_PDEC;--CFBARRERA-14-02-2025--CC:551 
          END IF;
          PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2);
          PCK_NOMINA.CN(164)    := PCK_NOMINA.GL_PERIODOS;
          PCK_NOMINA.CN(175)    := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0);
          IF PCK_NOMINA.GL_SPRC  = 99 THEN
            PCK_NOMINA.CN(175)  := PCK_NOMINA.FC_CN(175);
          ELSE
            PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.GL_PENDIENTES;
          END IF;

          PCK_NOMINA.CN(175) :=
          (
            CASE
            WHEN PCK_NOMINA.FC_CN(175) < 0 THEN
              0
            ELSE
              PCK_NOMINA.FC_CN(175)
            END);           
        END IF;
        IF (PCK_NOMINA.GL_SPER = 5 OR PCK_NOMINA.GL_SPER = 7) OR PCK_NOMINA.FC_CN(404) <> 0 THEN
          PCK_NOMINA.CN(93)   :=
          (
            CASE
            WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN
              1
            ELSE
              PCK_NOMINA.GL_PERIODOS
            END);      
          IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'PV' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'LN' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'SN' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'PP' THEN
            PCK_NOMINA.GL_DIASVAC := TRUNC(15 * PCK_NOMINA.GL_DTV / 360) + PCK_NOMINA.GL_DIASPENDIENTES;
            PCK_NOMINA.CN(164)    :=
            (
              CASE
              WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN
                1
              ELSE
                PCK_NOMINA.GL_DIASVAC
              END);
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,
            (
              CASE
              WHEN PCK_NOMINA.GL_DIASVAC <= 0 THEN
                1
              ELSE
                PCK_NOMINA.GL_DIASVAC
              END));
            PCK_NOMINA.CN(96)      := 0;
            IF PCK_NOMINA.FC_CN(96) = 0 THEN
              IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
                PCK_NOMINA.CN(96)                                   := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
              END IF;
              PCK_NOMINA.GL_FECHAFF1                                := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
              PCK_NOMINA.GL_FECHAFF                                 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
              PCK_NOMINA.CN(96)                                     := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
              IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL AND PCK_NOMINA.FC_CN(96) = 0 THEN
                PCK_NOMINA.GL_FECHAFF                               := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, (PCK_NOMINA.GL_FECHAFIN + 1), PCK_NOMINA.GL_DIASVAC);
                PCK_NOMINA.CN(96)                                   := (PCK_NOMINA.GL_FECHAFF                                                 - (PCK_NOMINA.GL_FECHAFIN + 1));
                PCK_NOMINA.CN(96)                                   := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
              END IF;
            END IF;

          IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 THEN
           PCK_NOMINA.CN(96) := (PCK_NOMINA.GL_DTV * 15) / 360;  
          END IF;

            PCK_NOMINA.CN(96) :=
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(96) = 1 THEN
                0
              ELSE
                PCK_NOMINA.FC_CN(96)
              END);
            PCK_NOMINA.GL_DIASVAC :=
            (
              CASE
              WHEN PCK_NOMINA.GL_DIASVAC = 0 THEN
                1
              ELSE
                PCK_NOMINA.GL_DIASVAC
              END);
            IF PCK_NOMINA.FC_CN(96) < 0 THEN
              PCK_NOMINA.CN(96)    := 0;
            END IF;
            IF PCK_NOMINA.FC_CN(96)  = 0 THEN
              PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
              PCK_NOMINA.CN(96)     := (PCK_NOMINA.GL_FECHAFF - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') ) + 1;
              PCK_NOMINA.CN(96)     :=
              (
                CASE
                WHEN PCK_NOMINA.FC_CN(96) = 1 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO <> PCK_NOMINA.GL_FECHAFF AND PCK_NOMINA.GL_DIASVAC < 1 THEN
                  0
                ELSE
                  PCK_NOMINA.FC_CN(96)
                END);
              IF PCK_NOMINA.FC_CN(96) < 0 THEN
                PCK_NOMINA.CN(96)    := 0;
              END IF;
            END IF;           
            IF PCK_NOMINA.GL_DTV  < 24 AND PCK_NOMINA.FC_CN(96) <= 1 THEN
              PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)), 3), 0);
              PCK_NOMINA.CN(96)  := PCK_SYSMAN_UTL.FC_ROUND(((15                     * PCK_NOMINA.GL_DTV / 360)), 3);
            ELSE
              PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND((15 * PCK_NOMINA.GL_DTV / 360) + PCK_NOMINA.GL_DIASPENDIENTES,2);
              PCK_NOMINA.CN(96)  := PCK_SYSMAN_UTL.FC_DIASFEBREROYMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABILDIAS30(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, TRUNC(PCK_NOMINA.GL_DIASVAC)));              
              PCK_NOMINA.CN(96)  := PCK_NOMINA.FC_CN(96) + PCK_SYSMAN_UTL.FC_ROUND((15 * PCK_NOMINA.GL_DTV / 360) - TRUNC(PCK_NOMINA.GL_DIASVAC), 2);  
              IF PCK_NOMINA.GL_ESBONIFICACION THEN                 
                  MI_FACTORESVACACIONES := PCK_SYSMAN_UTL.FC_ROUND(
                    (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
                        PCK_NOMINA.FC_CN(10)
                      ELSE
                        PCK_NOMINA.FC_CN(1)
                      END) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);                                     
              ELSE 
                MI_FACTORESVACACIONES := PCK_NOMINA.GL_FACTORESPV;
              END IF;
              PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORESVACACIONES * PCK_NOMINA.FC_CN(96) / 30, 0) + PCK_SYSMAN_UTL.FC_ROUND(MI_FACTORESVACACIONES / 30 * PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)) - PCK_NOMINA.GL_DIASVAC, 2), 0);                                    
            END IF;
            IF PCK_NOMINA.GL_SPRC = 99 AND (PCK_NOMINA.FC_CN(175) = 0 OR PCK_NOMINA.FC_CN(175) = 0) AND PCK_NOMINA.FC_CN(155) > 0 THEN
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

        PCK_NOMINA.CN(175) :=
        (
          CASE
          WHEN PCK_NOMINA.FC_CN(175) < 0 THEN
            0
          ELSE
            PCK_NOMINA.FC_CN(175)
          END);

    PCK_NOMINA.CN(155) := (CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN
     PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV / 30) * PCK_NOMINA.GL_PROPDIASPRIMAVAC, 0)
    ELSE
      PCK_NOMINA.FC_CN(155)
    END);     
        PCK_NOMINA.CN(982) := MI_BONPAGADA/12; --MOD JM CC 3075
        PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
      ELSE
    --En periodo mensual
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
          PCK_NOMINA.GL_AC         := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 01, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
          IF PCK_NOMINA.FC_CNA(150) > 0 THEN
            MI_BONPAGADA           := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_AC       := PCK_NOMINA.FC_ACUM(UN_COMPANIA,(PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
          ELSE
            MI_BONPAGADA := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
          END IF;
        ELSE
            IF MI_BONPAGADA = 0 AND PCK_NOMINA.FC_CN(150) > 0 AND PCK_NOMINA.GL_SPER <= 3 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INICIO_DISFRUTE > PCK_NOMINA.GL_FECHAFIN THEN
              MI_BONPAGADA := PCK_NOMINA.FC_CN(150);
            ELSE 
              MI_BONPAGADA     := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);            
            END IF;     
          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,(PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        END IF;

        PCK_NOMINA.CN(982)                               := PCK_SYSMAN_UTL.FC_ROUND((MI_BONPAGADA) / 12, 0);        

    PCK_NOMINA.CN(155) := (CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN
     PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * (PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164)), 0)
    ELSE
      PCK_NOMINA.FC_CN(155)
    END);

        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' THEN
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
        ELSE     
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
    PCK_NOMINA.CN(987)   := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
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
    PCK_NOMINA.CN(976)   := 0;
    PCK_NOMINA.CN(977)   := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(978)   := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(979)   := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(980)   := PCK_NOMINA.GL_VPT;    
	--CC_4024(23/04/2026 JCROJAS): Se modifican los valores de los conceptos 981 y 982
    PCK_NOMINA.CN(981)   := MI_PRIMASERVICIOS/12;
    PCK_NOMINA.CN(982)   := MI_BASP/12;
    PCK_NOMINA.CN(983)   := 0;
    PCK_NOMINA.CN(984)   := PCK_NOMINA.FC_VALORULTIMOQUINQUENIO(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.CN(987)   := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);    
    PCK_NOMINA.GL_PV_BASE  := PCK_NOMINA.GL_FACTORESPV;
    PCK_NOMINA.GL_PV_DIAS  := PCK_NOMINA.GL_DIASPROP;
    PCK_NOMINA.GL_VAC_DIAS := PCK_NOMINA.GL_DTV;        
  END PR_PRIMAVACACIONESBUCARAMANGA;

FUNCTION FC_VALORACUMCONCEPTOYAJUSTEMAX
(
  UN_ID_EMPLEADO     IN PCK_SUBTIPOS.TI_ENTERO,
  UN_MAXIMOREGISTROS   IN PCK_SUBTIPOS.TI_ENTERO,
  UN_CONCEPTO      IN PCK_SUBTIPOS.TI_ENTERO,
  UN_CONCEPTOADICIONAL IN PCK_SUBTIPOS.TI_ENTERO DEFAULT NULL
)
RETURN PCK_SUBTIPOS.TI_DOBLE
/*
  NAME              : FC_VALORACUMCONCEPTOYAJUSTEMAX
  AUTHORS           : SYSMAN  SAS
  AUTHOR            : ANDREA CAROLINA PINEDA OVALLE
  DATE              : 26/03/2019
  TIME              :
  SOURCE MODULE     :
  MODIFIER          :
  DATE MODIFIED     : 09/06/2025 JM para que tome correctamente el acumulado del 503 en caso de tener 
  TIME              :
  DESCRIPTION       : Obtiene la suma de los Ãºltimos valores pagados del concepto recibido por parametro y el concepto de ajuste teniendo en cuenta un mÃ¡ximo de registros
  --NAME:  FC_VALORACUMCONCEPTOYAJUSTEMAX
*/
AS
  MI_MES        PCK_SUBTIPOS.TI_ENTERO :=0;
  MI_ANO        PCK_SUBTIPOS.TI_ENTERO :=0;
  MI_TOTAL      PCK_SUBTIPOS.TI_DOBLE  :=0;
  MI_VALORAJUSTE  PCK_SUBTIPOS.TI_DOBLE  :=0;
  MI_REG          PCK_SUBTIPOS.TI_DOBLE  :=0; --JM CC 1706
BEGIN
  FOR RS IN (
    --(APINEDA:28/08/2019)-Se modifica orden de la consulta TAR 1000093667
    SELECT VALOR,ANO,MES FROM (
      SELECT H.VALOR,H.ANO,H.MES
      FROM HISTORICOS H
      INNER JOIN PERIODOS P ON (H.COMPANIA = P.COMPANIA)
      AND (H.ID_DE_PROCESO = P.ID_DE_PROCESO)
      AND (H.ANO = P.ANO)
      AND (H.MES = P.MES)
      AND (H.PERIODO = P.PERIODO)
      WHERE H.ID_DE_CONCEPTO = UN_CONCEPTO
      AND (H.ANO || LPAD(H.MES,2,'0')) <= PCK_NOMINA.GL_SANO  ||  LPAD(PCK_NOMINA.GL_SMES,2,'0')
      AND H.COMPANIA  =  PCK_NOMINA.GL_COMPANIA
      AND H.ID_DE_EMPLEADO  = UN_ID_EMPLEADO
      AND H.ID_DE_PROCESO  = 1
      AND H.VALOR   <>  0
      AND P.ACUMULADO   =   -1
      ORDER BY H.ANO DESC, H.MES ASC)
    WHERE ROWNUM <= UN_MAXIMOREGISTROS
  )
  LOOP
   --MOD JM CC 1706
    IF MI_REG = 0 THEN 
    MI_ANO := RS.ANO;
    MI_MES := RS.MES;
    END IF;
    MI_TOTAL := MI_TOTAL + RS.VALOR;
    MI_REG := MI_REG +1;
  END LOOP;
     --INI JM CC 1706
    IF UN_CONCEPTOADICIONAL IS NOT NULL AND MI_ANO <> 0 AND MI_MES <> 0 THEN
      FOR RS IN (
        --(APINEDA:28/08/2019)-Se modifica orden de la consulta TAR 1000093667
        SELECT VALOR,ANO,MES FROM (
          SELECT H.VALOR,H.ANO,H.MES
          FROM HISTORICOS H
          INNER JOIN PERIODOS P ON (H.COMPANIA = P.COMPANIA)
          AND (H.ID_DE_PROCESO = P.ID_DE_PROCESO)
          AND (H.ANO = P.ANO)
          AND (H.MES = P.MES)
          AND (H.PERIODO = P.PERIODO)
          WHERE H.ID_DE_CONCEPTO = UN_CONCEPTOADICIONAL
          AND ((H.ANO || LPAD(H.MES,2,'0')) <= PCK_NOMINA.GL_SANO  ||  LPAD(PCK_NOMINA.GL_SMES,2,'0') AND (H.ANO || LPAD(H.MES,2,'0')) >= MI_ANO  ||  LPAD(MI_MES,2,'0'))
          AND H.COMPANIA  =  PCK_NOMINA.GL_COMPANIA
          AND H.ID_DE_EMPLEADO  = UN_ID_EMPLEADO
          AND H.ID_DE_PROCESO  = 1
          AND H.VALOR   <>  0
          AND P.ACUMULADO   =   -1
          ORDER BY H.ANO DESC, H.MES DESC)
        WHERE ROWNUM <= UN_MAXIMOREGISTROS
      )
      LOOP
        MI_VALORAJUSTE := MI_VALORAJUSTE + RS.VALOR;
      END LOOP;
    END IF;
    --FIN JM CC 1706
  RETURN MI_TOTAL + MI_VALORAJUSTE;
END FC_VALORACUMCONCEPTOYAJUSTEMAX;

PROCEDURE PR_VACACIONESBUCARAMANGA(
      /*
      NAME              : PR_VACACIONESBUCARAMANGA
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
      DATE MIGRADOR     : 27/03/2019
      TIME              :
      SOURCE MODULE     : NOMINAP2019.01.01 Proc01 Contraloria General de Cundinamarca
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       :
      @NAME:  PR_VACACIONESBUCARAMANGA
      */
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA )
  AS
    MI_F_INICIOPERACT   DATE;   --Guarda inicio y fin de perido act
    MI_F_FINALPERACT    DATE;
    MI_FINICIO          DATE;  --Fechas ajustadas si la novedad se pasa del mes
    MI_FFINAL           DATE;
  MI_CONCEPTOVACACIONES PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  MI_TOTALVACACIONES PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIAS             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  BEGIN

  MI_F_INICIOPERACT := TO_DATE('01/' || PCK_NOMINA.GL_SMES || '/' || PCK_NOMINA.GL_SANO);
  MI_F_FINALPERACT := LAST_DAY(TO_DATE('01/' || PCK_NOMINA.GL_SMES || '/' || PCK_NOMINA.GL_SANO));

  FOR MI_RS IN
  (
  SELECT INICIO_DISFRUTE F1, FINAL_DISFRUTE F2, BASEIBC
  FROM VACACIONES
  WHERE COMPANIA = UN_COMPANIA
    AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
    AND ID_DE_PROCESO  = 1
    AND DIAS <> 0
    AND ( TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT
      OR (TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT AND TO_NUMBER(TO_CHAR(INICIO_DISFRUTE ,'DD')) <> 31  )
      OR (TO_DATE(TO_CHAR(FECHAPAGO, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT AND TO_NUMBER(TO_CHAR(INICIO_DISFRUTE ,'DD')) <> 31  )
            OR (TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') <= MI_F_INICIOPERACT AND TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') >= MI_F_FINALPERACT AND TO_NUMBER(TO_CHAR(INICIO_DISFRUTE ,'DD')) <> 31  )
      )
  )
  LOOP
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

    MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN   => MI_FINICIO ,UN_FECHAFIN  => MI_FFINAL);

    --Interrupcion de vacaciones
    FOR MI_RSINTER IN
    (
      SELECT FECHAINTERRUPCION
      FROM  INTERRUPCION_VACACIONES
      WHERE COMPANIA =  UN_COMPANIA
        AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
        AND ENDINERO = 0
        AND QUITARPILA = 0
        AND ( TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT
          OR TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT
          )
        AND ( TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_RS.F1 AND MI_RS.F2
         OR TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_RS.F1 AND MI_RS.F2
          )
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

    IF PCK_NOMINA.FC_CN(35) = 0 THEN --Si no existen vacaciones para disfrutar en el mes.
      MI_DIAS := 0;
      MI_FINICIO := NULL;
      MI_FFINAL := NULL;
    ELSE
      --(MZANGUNA:01/03/2019)-Se ajusta para Febrero dado que genera errores de fechas con la funciÃ³n FC_DIASMESCOMERCIAL
      IF MI_FFINAL < MI_FINICIO THEN  --(MZANGUNA:13/12/2019)-Se agrega condiciÃ³n para casos con vacaciones con interrupciÃ³n total de vacaciones
        MI_DIAS := 0;
      ELSE
        IF TO_CHAR(MI_FINICIO, 'MM') = '02' AND TO_CHAR(MI_FFINAL, 'MM') = '02' THEN
          MI_DIAS := TO_NUMBER(MI_FFINAL - MI_FINICIO) + 1;
		--CC_4024(23/04/2026 JCROJAS): Se suma 1 para que tenga en cuenta el dia 31
        ELSIF PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(MI_RS.F1)) = 31 AND LAST_DAY(MI_RS.F1) BETWEEN MI_RS.F1 AND MI_RS.F2 THEN
          MI_DIAS := TO_NUMBER(MI_FFINAL - MI_FINICIO) + 1;												 
        ELSE
          MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN   => MI_FINICIO
          ,UN_FECHAFIN  => MI_FFINAL);
        END IF;
      END IF;
    END IF;

    MI_CONCEPTOVACACIONES := (MI_RS.BASEIBC / 30) * MI_DIAS;
    MI_TOTALVACACIONES := MI_TOTALVACACIONES + MI_CONCEPTOVACACIONES;
    END LOOP;
    --(APINEDA:27/06/2019)-Se agrega redondeo a total de vacaciones.
  PCK_NOMINA.CN(174) := PCK_SYSMAN_UTL.FC_ROUND(MI_TOTALVACACIONES, 0);
  --CC_4024(23/04/2026 JCROJAS): Se le asigna MI_DIAS al concepto 35 para que tenga en cuenta el dia 31
  PCK_NOMINA.CN(35) := MI_DIAS;						   
  END PR_VACACIONESBUCARAMANGA;


PROCEDURE PR_PRIMASEMESTRALBUCARAMANGA 
    /*
    NAME              : PR_PRIMASEMESTRALBUCARAMANGA
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
    DATE MIGRADOR     : 03/04/2019
    TIME              : 09:12 AM
    SOURCE MODULE     : NOMINAP2019.01.01_UNIFICADAS MPV 02012019_MPV - 490 NCASE WHEN  eaam MPV - En access calcularprimasemestralDUITAMA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  PRIMASEMESTRALBUCARAMANGA
    */
    (    
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
    )
AS 
    MI_FECHA_ASIGBASICAPS   DATE;
    MI_ENCARGO_FECINICIO    DATE;
    MI_ENCARGO_FECFIN       DATE;
    MI_BASE                 PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_VALORPRIMA           PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN    

    PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/01/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END;    

    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END;
    ELSE
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('30/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
    END IF;    

    PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
    PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 THEN PCK_NOMINA.GL_FECHAFIN1 ELSE (CASE WHEN PCK_NOMINA.GL_SMES = 6 THEN TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') ELSE TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') END) END;    

    IF PCK_NOMINA.GL_SMES = 6 AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3 OR PCK_NOMINA.GL_SPER = 4) THEN
      PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), 1, PCK_NOMINA.GL_SANO, 6, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);    
      PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
      --Se tendrÃ¡ en cuenta la asignaciÃ³n bÃ¡sica que tenga el funcionario a 31 de mayo, ya sea la del cargo titular o la del encargo
      PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 5, 1, PCK_NOMINA.GL_SANO, 5, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);    
      MI_FECHA_ASIGBASICAPS := TO_DATE('31/05/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    ELSIF PCK_NOMINA.GL_SMES = 12 THEN -- Solo acumula desde julio a diciembre
      PCK_NOMINA.GL_AC := PCK_NOMINA_PROC01.FC_ACUMCONCEPTOLICENCIAS('359', PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS), 1, PCK_NOMINA.GL_SANO, 12, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,0);            
      PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
      --Se tendrÃ¡ en cuenta la asignaciÃ³n bÃ¡sica que tenga el funcionario a 30 de noviembre, ya sea la del cargo titular o la del encargo
      PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 11, 1, PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
      MI_FECHA_ASIGBASICAPS := TO_DATE('30/11/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    END IF;
    --Base prima semestral        
    IF PCK_NOMINA.FC_CN(10) <> 0 THEN
        BEGIN
            SELECT FECHAINICIO, FECHAFINAL
            INTO MI_ENCARGO_FECINICIO, MI_ENCARGO_FECFIN                  
            FROM ENCARGOS 
            WHERE COMPANIA = UN_COMPANIA
            AND ANO = PCK_NOMINA.GL_SANO
            AND ID_DE_PROCESO = 1
            AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
            AND TO_DATE(TO_CHAR(FECHAINICIO, 'DD/MM/YYYY'),'DD/MM/YYYY') <= MI_FECHA_ASIGBASICAPS AND TO_DATE(TO_CHAR(FECHAFINAL, 'DD/MM/YYYY'),'DD/MM/YYYY') >= MI_FECHA_ASIGBASICAPS;

           MI_BASE := PCK_NOMINA.FC_CN(10);
        EXCEPTION WHEN NO_DATA_FOUND THEN
           MI_BASE := PCK_NOMINA.FC_CN(1);                
        END;
    ELSE
        MI_BASE := PCK_NOMINA.FC_CN(1);
    END IF;    
    PCK_NOMINA.GL_FACTORPS := MI_BASE;

    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
    PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
    FOR i IN (CASE WHEN PCK_NOMINA.GL_SMES = 12 THEN PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS) ELSE 1 END).. PCK_NOMINA.GL_SMES LOOP      
      PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);            
      IF PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339) > 0 OR PCK_NOMINA.FC_CNA(356) > 0 OR PCK_NOMINA.FC_CNA(357) > 0 THEN
         PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
      END IF;
    END LOOP;

    MI_VALORPRIMA := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORPS / 360) * PCK_NOMINA.GL_DCC, 0) ELSE PCK_NOMINA.FC_CN(160) END;

    --Para que el funcionario tenga derecho al pago de la prima de servicios en cada semestre tiene que haber laborado como mÃ­nimo 30 dÃ­as  en cada semestre.
    IF PCK_NOMINA.GL_DCC < 30 THEN
         MI_VALORPRIMA := 0;
    END IF;
    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..699 LOOP
            --(APINEDA:02/12/2019)-TAR 1000095516 Se agrega condiciÃ³n para que no sea puesto en 0 el indicador 402 para liquidar prima de navidad.
            --CC4399 MPEREZ - Se agrega condicion para no poner en 0 el concepto 160 para que se puedan tener en cuenta los datos que se configurar por novedad.
            IF (i <> 125) AND (i <> 160) AND (i <> 303) AND (i <> 301) AND (i <> 300) AND (i < 599 AND i <> 402) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;    
    /*INI_CC4399_MPEREZ - Se ajusta para que valide si el 160 tiene datos no lo vuelva a calcular*/
    IF PCK_NOMINA.FC_CN(160) = 0 THEN
        PCK_NOMINA.CN(160) := MI_VALORPRIMA;
    END IF;
    /*FIN_CC4399_MPEREZ*/
    --Guardando Factores
    PCK_NOMINA.CN(951) := PCK_NOMINA.FC_CN(67);        -- Dias pactados prima
    PCK_NOMINA.CN(952) := PCK_NOMINA.GL_DCC;           -- Dias calendario Comercial a 30 de Junio
    PCK_NOMINA.CN(953) := PCK_NOMINA.GL_DNT;      
    PCK_NOMINA.CN(945) := PCK_NOMINA.GL_FACTORPS;
    PCK_NOMINA.GL_PS_BASE := PCK_NOMINA.GL_FACTORPS;
    PCK_NOMINA.GL_PS_DIAS := PCK_NOMINA.GL_DCC;
END PR_PRIMASEMESTRALBUCARAMANGA;

PROCEDURE PR_PRIMAVACBUCARAMANGAOBREROS(
      /*
      NAME              : PR_PRIMAVACBUCARAMANGAOBREROS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
      DATE MIGRADOR     : 23/08/2019
      TIME              :
      SOURCE MODULE     : Nueva
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Procedimiento especÃ­fico para el calculo de la Prima de vacaciones de la compaÃ±Ã­a de obreros en la AlcaldÃ­a de Bucaramanga
      @NAME:  PRIMAVACBUCARAMANGAOBREROS
      */
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA )
  AS
    MI_BONPAGADA            NUMBER DEFAULT 0;
    MI_MSG                  PCK_SUBTIPOS.TI_CLAVEVALOR;         
    MI_UPDATE               PCK_SUBTIPOS.TI_ENTERO;
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;    
    MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;    
    MI_JORNAL               NUMBER :=0; 
  MI_EXTRAS               NUMBER :=0; 
    MI_PRIMASERVICIOS       NUMBER :=0;
  BEGIN

    PCK_NOMINA.GL_DIASVAC           := 0;
      PCK_NOMINA.GL_DIASPENDIENTES    := 0;
      PCK_NOMINA.GL_PENDIENTES        := 0;
      PCK_NOMINA.GL_LICENCIAS         := 0;
      PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
      PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
      PCK_NOMINA.GL_FECHAUV           :=
      (CASE
        WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
          PCK_NOMINA.GL_FECHAI
        ELSE
          PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL
        END);

    MI_JORNAL := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

    MI_EXTRAS := PCK_NOMINA.FC_CNA(70);
      IF PCK_NOMINA.FC_CN(160) <> 0 THEN
          MI_PRIMASERVICIOS := (PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503)) + PCK_NOMINA_COM4.FC_VALORACUMCONCEPTOYAJUSTEMAX(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 1, 160, 503);
      ELSE
          MI_PRIMASERVICIOS := PCK_NOMINA_COM4.FC_VALORACUMCONCEPTOYAJUSTEMAX(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 2, 160, 503);
      END IF;
      -- TICKET 7736013 , se solicita solo tomar el jornal mas la doceava de extras mas doceavas de prima como base de la prima de vacaciones. 
    PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(MI_JORNAL       --VALOR DEL JORNAL * 30 
                          --  + PCK_NOMINA.FC_CN(80)  --AUXILIO DE TRANSPORTE
                          --  + PCK_NOMINA.FC_CN(79)  --SUBSIDIO DE ALIMENTACIÃ“N
                          --  + PCK_NOMINA.FC_CN(195) --SOBRESUELDO
                          --  + PCK_NOMINA.FC_CN(190) --PRIMA CLIMATICA
                          --  + PCK_NOMINA.FC_CN(194) --AUXILIO DE MOVILIZACION
                          --  + PCK_NOMINA.FC_CN(192) --PRIMA DE ALIMENTACION                 
                            + (MI_EXTRAS / 12)    --1/12 HORAS EXTRAS Y/O TRABAJO SUPLEMENTARIO     
                            + (MI_PRIMASERVICIOS / 12), 0); -- 1/12 ULTIMAS DOS PRIMAS DE SERVICIOS PAGADAS

        MI_CAMPOS    := 'BASEIBC = ' || NVL(PCK_NOMINA.GL_FACTORESPV,0);

        MI_CONDICION := ' COMPANIA       = '''||UN_COMPANIA||'''
                      AND ID_DE_PROCESO  = '|| PCK_NOMINA.GL_SPRC ||' 
                      AND ANO            = '|| PCK_NOMINA.GL_SANO ||'
                      AND MES            = '|| PCK_NOMINA.GL_SMES ||'
                      AND PERIODO        = '|| PCK_NOMINA.GL_SPER ||'
                      AND ID_DE_EMPLEADO = '|| PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;                                        
        BEGIN
            BEGIN
                MI_UPDATE:=PCK_DATOS.FC_ACME(UN_TABLA => 'VACACIONES' ,UN_ACCION => 'M' ,UN_CAMPOS => MI_CAMPOS ,UN_CONDICION => MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            --OcurriÃ³ un problema al intentar almacenar la base para el cÃ¡lculo de Vacaciones, para el empleado --EMPLEADO--
            MI_MSGERROR (1).CLAVE := 'EMPLEADO';
            MI_MSGERROR (1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD =>SQLCODE, UN_ERROR_COD=>PCK_ERRORES.ERR_ACTUALIZARBASEVACACIONES, UN_REEMPLAZOS => MI_MSGERROR );
        END;      

      IF PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.GL_SPER = 8 THEN
        PCK_NOMINA.CN(984) := 0;

        PCK_NOMINA.GL_AC        := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.GL_LICENCIAS := NVL(((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.FC_CNA(339)) +
        (
          CASE
          WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
            PCK_NOMINA.FC_CESANTIA(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 'L')
          ELSE
            0
          END) + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION),0);
        PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91);
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
        PCK_NOMINA.GL_PERIODOS := TRUNC(PCK_NOMINA.GL_DTV/ 360);
        PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2);
        PCK_NOMINA.CN(93)     := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) *
        (
          CASE
          WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN
            1
          ELSE
            PCK_NOMINA.GL_PERIODOS
          END), 2);
        IF PCK_NOMINA.GL_DIASVAC  = 0 THEN
          PCK_NOMINA.GL_PERIODOS := CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END;            
          PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 *
          (
            CASE
            WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN
              1
            ELSE
              PCK_NOMINA.GL_PERIODOS
            END) / 360 * PCK_NOMINA.GL_DTV, 0);
        END IF;
        PCK_NOMINA.GL_AC           := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.GL_PROPDIASPRIMAVAC := ROUND(((15 / 360) * PCK_NOMINA.GL_DTV), 2);       

        IF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '01' THEN
          PCK_NOMINA.GL_FECHAFF    := NULL;
          PCK_NOMINA.GL_FECHAFF    := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') - 1, NVL(PCK_NOMINA.GL_DIASVAC, 1));
          IF PCK_NOMINA.GL_SPER     = 8 AND PCK_NOMINA.GL_SMES = 12 THEN
            PCK_NOMINA.GL_PERIODOS := 1;
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
            PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.GL_FECHAFF  := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);

            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
              PCK_NOMINA.CN(96)                                   := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
              PCK_NOMINA.CN(96)                                   := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
            END IF;
          END IF;
          IF PCK_NOMINA.FC_CN(96)  = 0 THEN
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.CN(96)     := (TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') - PCK_NOMINA.GL_FECHAFF) + 1;
            PCK_NOMINA.CN(96)     := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
          END IF;
          PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * PCK_NOMINA.GL_PERIODOS, 2);
          PCK_NOMINA.CN(164)    := PCK_NOMINA.GL_PERIODOS;
          PCK_NOMINA.CN(175)    := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0);
          IF PCK_NOMINA.GL_SPRC  = 99 THEN
            PCK_NOMINA.CN(175)  := PCK_NOMINA.FC_CN(175);
          ELSE
            PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(175) + PCK_NOMINA.GL_PENDIENTES;
          END IF;

          PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END;            
        END IF;
        IF (PCK_NOMINA.GL_SPER = 5 OR PCK_NOMINA.GL_SPER = 7) OR PCK_NOMINA.FC_CN(404) <> 0 THEN
          PCK_NOMINA.CN(93)   :=
          (
            CASE
            WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN
              1
            ELSE
              PCK_NOMINA.GL_PERIODOS
            END);      
          IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'PV' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'LN' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'SN' OR UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'PP' THEN
            PCK_NOMINA.GL_DIASVAC := TRUNC(15 * PCK_NOMINA.GL_DTV / 360) + PCK_NOMINA.GL_DIASPENDIENTES;
            PCK_NOMINA.CN(164)    := CASE WHEN PCK_NOMINA.GL_DIASVAC > 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO,
            (
              CASE
              WHEN PCK_NOMINA.GL_DIASVAC <= 0 THEN
                1
              ELSE
                PCK_NOMINA.GL_DIASVAC
              END));
            PCK_NOMINA.CN(96)      := 0;
            IF PCK_NOMINA.FC_CN(96) = 0 THEN
              IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
                PCK_NOMINA.CN(96)                                   := (PCK_NOMINA.GL_FECHAFF - (PCK_NOMINA.GL_FECHAFIN + 1));
              END IF;
              PCK_NOMINA.GL_FECHAFF1                                := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
              PCK_NOMINA.GL_FECHAFF                                 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
              PCK_NOMINA.CN(96)                                     := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
              IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL AND PCK_NOMINA.FC_CN(96) = 0 THEN
                PCK_NOMINA.GL_FECHAFF                               := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, (PCK_NOMINA.GL_FECHAFIN + 1), PCK_NOMINA.GL_DIASVAC);
                PCK_NOMINA.CN(96)                                   := (PCK_NOMINA.GL_FECHAFF                                                 - (PCK_NOMINA.GL_FECHAFIN + 1));
                PCK_NOMINA.CN(96)                                   := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
              END IF;
            END IF;

          IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 THEN
           PCK_NOMINA.CN(96) := (PCK_NOMINA.GL_DTV * 15) / 360;  
          END IF;

            PCK_NOMINA.CN(96) := CASE WHEN PCK_NOMINA.FC_CN(96) = 1 THEN 0 ELSE PCK_NOMINA.FC_CN(96) END;
            PCK_NOMINA.GL_DIASVAC := CASE WHEN PCK_NOMINA.GL_DIASVAC = 0 THEN 1 ELSE PCK_NOMINA.GL_DIASVAC END;
            IF PCK_NOMINA.FC_CN(96) < 0 THEN
              PCK_NOMINA.CN(96)    := 0;
            END IF;
            IF PCK_NOMINA.FC_CN(96)  = 0 THEN
              PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
              PCK_NOMINA.CN(96)     := (PCK_NOMINA.GL_FECHAFF - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') ) + 1;
              PCK_NOMINA.CN(96)     :=
              (
                CASE
                WHEN PCK_NOMINA.FC_CN(96) = 1 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO <> PCK_NOMINA.GL_FECHAFF AND PCK_NOMINA.GL_DIASVAC < 1 THEN
                  0
                ELSE
                  PCK_NOMINA.FC_CN(96)
                END);
              IF PCK_NOMINA.FC_CN(96) < 0 THEN
                PCK_NOMINA.CN(96)    := 0;
              END IF;
            END IF;           
            IF PCK_NOMINA.GL_DTV  < 24 AND PCK_NOMINA.FC_CN(96) <= 1 THEN
              PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)), 3), 0);
              PCK_NOMINA.CN(96)  := PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)), 3);
            ELSE
              PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND((15 * PCK_NOMINA.GL_DTV / 360) + PCK_NOMINA.GL_DIASPENDIENTES,2);
              PCK_NOMINA.CN(96)  := PCK_SYSMAN_UTL.FC_DIASFEBREROYMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABILDIAS30(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, TRUNC(PCK_NOMINA.GL_DIASVAC)));              
              PCK_NOMINA.CN(96)  := PCK_NOMINA.FC_CN(96) + PCK_SYSMAN_UTL.FC_ROUND((15 * PCK_NOMINA.GL_DTV / 360) - TRUNC(PCK_NOMINA.GL_DIASVAC), 2);  
              PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)) - PCK_NOMINA.GL_DIASVAC, 2), 0);                                    
            END IF;
            IF PCK_NOMINA.GL_SPRC = 99 AND (PCK_NOMINA.FC_CN(175) = 0 OR PCK_NOMINA.FC_CNA(175) = 0) AND PCK_NOMINA.FC_CN(155) > 0 THEN
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

        PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END;

    PCK_NOMINA.CN(155) := (CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN
     PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV / 30) * PCK_NOMINA.GL_PROPDIASPRIMAVAC, 0)
    ELSE
      PCK_NOMINA.FC_CN(155)
    END);     
        PCK_NOMINA.CN(982) := MI_BONPAGADA;
        PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
      ELSE
    --En periodo mensual
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

    PCK_NOMINA.CN(155) := (CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN
     PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * (PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164)), 0)
    ELSE
      PCK_NOMINA.FC_CN(155)
    END);

        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' THEN
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
        ELSE     
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
    PCK_NOMINA.CN(987)   := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
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
    PCK_NOMINA.CN(975) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    PCK_NOMINA.CN(976) := 0;
    PCK_NOMINA.CN(977) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(978) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(979) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(983) := 0;
    PCK_NOMINA.CN(984) := PCK_NOMINA.FC_VALORULTIMOQUINQUENIO(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);    
    PCK_NOMINA.GL_PV_BASE  := PCK_NOMINA.GL_FACTORESPV;
    PCK_NOMINA.GL_PV_DIAS  := PCK_NOMINA.GL_DIASPROP;
    PCK_NOMINA.GL_VAC_DIAS := PCK_NOMINA.GL_DTV;        
  END PR_PRIMAVACBUCARAMANGAOBREROS;

PROCEDURE PR_VACBUCARAMANGAOBREROS(
      /*
      NAME              : PR_VACBUCARAMANGAOBREROS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
      DATE MIGRADOR     : 27/08/2019
      TIME              :
      SOURCE MODULE     : 
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Procedimiento especÃ­fico para el calculo de vacaciones de la compaÃ±Ã­a de obreros en la AlcaldÃ­a de Bucaramanga
      @NAME:  VACBUCARAMANGAOBREROS
      */
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
      )
  AS
    MI_F_INICIOPERACT   DATE;  --Guarda inicio y fin de perido act
    MI_F_FINALPERACT    DATE;
    MI_FINICIO          DATE;  --Fechas ajustadas si la novedad se pasa del mes
    MI_FFINAL           DATE;     
  MI_CONCEPTOVACACIONES PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  MI_TOTALVACACIONES PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIAS             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  BEGIN  

  MI_F_INICIOPERACT := PCK_NOMINA.GL_FECHAINI;
    IF PCK_NOMINA.GL_SPER = 1 THEN    
        MI_F_FINALPERACT := PCK_NOMINA.GL_FECHAFIN;
    ELSIF PCK_NOMINA.GL_SPER = 2 THEN
        --MI_F_FINALPERACT := TO_DATE('30/' || PCK_NOMINA.GL_SMES || '/' || PCK_NOMINA.GL_SANO); comentado por JM 13/02/2024
        MI_F_FINALPERACT := TO_DATE(LAST_DAY('01/' || PCK_NOMINA.GL_SMES || '/' || PCK_NOMINA.GL_SANO)); --JM 7742002 13/02/2024   
    END IF;

  FOR MI_RS IN
  ( 
  SELECT INICIO_DISFRUTE F1, FINAL_DISFRUTE F2, BASEIBC
  FROM VACACIONES
  WHERE COMPANIA = UN_COMPANIA
    AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
    AND ID_DE_PROCESO  = 1
    AND DIAS <> 0   
    AND ( TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT
      OR (TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT AND TO_NUMBER(TO_CHAR(INICIO_DISFRUTE ,'DD')) <> 31  )
      OR (TO_DATE(TO_CHAR(FECHAPAGO, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT AND TO_NUMBER(TO_CHAR(INICIO_DISFRUTE ,'DD')) <> 31  )
            OR (TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') <= MI_F_INICIOPERACT AND TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') >= MI_F_FINALPERACT AND TO_NUMBER(TO_CHAR(INICIO_DISFRUTE ,'DD')) <> 31  )
      )
  )
  LOOP        
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

    MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN   => MI_FINICIO ,UN_FECHAFIN  => MI_FFINAL);

    --Interrupcion de vacaciones
    FOR MI_RSINTER IN
    (
      SELECT FECHAINTERRUPCION
      FROM  INTERRUPCION_VACACIONES
      WHERE COMPANIA =  UN_COMPANIA
        AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
        AND ENDINERO = 0
        AND QUITARPILA = 0
        AND ( TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT
          OR TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_F_INICIOPERACT AND MI_F_FINALPERACT
          )
        AND ( TO_DATE(TO_CHAR(FINAL_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_RS.F1 AND MI_RS.F2
         OR TO_DATE(TO_CHAR(INICIO_DISFRUTE, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_RS.F1 AND MI_RS.F2
          )
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

    IF PCK_NOMINA.FC_CN(35) = 0 THEN --Si no existen vacaciones para disfrutar en el mes.
      MI_DIAS := 0;
      MI_FINICIO := NULL;
      MI_FFINAL := NULL;
    ELSE
      --(MZANGUNA:01/03/2019)-Se ajusta para Febrero dado que genera errores de fechas con la funciÃ³n FC_DIASMESCOMERCIAL
      IF TO_CHAR(MI_FINICIO, 'MM') = '02' AND TO_CHAR(MI_FFINAL, 'MM') = '02' THEN
        MI_DIAS := TO_NUMBER(MI_FFINAL - MI_FINICIO) + 1;
      ELSE
        MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN   => MI_FINICIO
        ,UN_FECHAFIN  => MI_FFINAL);
      END IF;
    END IF;   

    MI_CONCEPTOVACACIONES := (MI_RS.BASEIBC / 30) * MI_DIAS;  
    MI_TOTALVACACIONES := MI_TOTALVACACIONES + MI_CONCEPTOVACACIONES;
    END LOOP;
    --(APINEDA:27/06/2019)-Se agrega redondeo a total de vacaciones.
  PCK_NOMINA.CN(174) := PCK_SYSMAN_UTL.FC_ROUND(MI_TOTALVACACIONES, 0);    
  END PR_VACBUCARAMANGAOBREROS;

  FUNCTION FC_SUMACONRANGOS
  /*
    NAME              : SUMACONRANGOS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA PINEDA
    DATE MIGRADOR     : 24/10/2019
    TIME              : 3:30 PM
    SOURCE MODULE     : Nueva
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 10:00 AM
    DESCRIPTION       : RETORNA LA SUMATORIA DE LOS CONCEPTOS EXISTENTES ENTRE EL RANGO ENVIADO COMO PARAMETRO Y SUMA MIL AL RANGO PROPORCIONADO HASTA COMPLETAR EL MAXIMO DE CONCEPTOS PERMITIDOS.
    @Name: NO USAR EN JAVA PROPIO DEL CALCULO                              
  */
  (
    UN_CONCEPTOINI IN PCK_SUBTIPOS.TI_ENTERO,
    UN_CONCEPTOFIN IN PCK_SUBTIPOS.TI_ENTERO
  )
  RETURN NUMBER
  AS
    MI_RANGO   NUMBER := 0;
    MI_SUMA    NUMBER := 0;
    MI_VALORCN NUMBER := 0;    
  BEGIN
    WHILE MI_RANGO < PCK_NOMINA.MAXI LOOP
       FOR MI_CONCEPTO IN UN_CONCEPTOINI .. UN_CONCEPTOFIN LOOP  
        MI_VALORCN := PCK_NOMINA.FC_CN(MI_RANGO + MI_CONCEPTO);
        MI_SUMA := MI_SUMA + MI_VALORCN;
       END LOOP;
       MI_RANGO := MI_RANGO + 1000; 
    END LOOP;
    RETURN MI_SUMA ;
  END FC_SUMACONRANGOS;  

PROCEDURE PR_VALORBENEFICIARIOSUPC
/*
    NAME              : PR_VALORBENEFICIARIOSUPC
    AUTHORS           : ANDREA PINEDA
    DATE MIGRADOR     : 29/11/2019
    TIME              : 12:50 
    SOURCE MODULE     : Nueva TAR 1000094879
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Permite obtener el valor total correspondiente a UPC adicional de la tabla BENEFICIARIOS_UPC y validar si el aporte lo realiza el patrono o el empleado.
    @Name: getDeducibleSalud
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO
  )
AS
  MI_VALORUPC     NUMBER:=0;
BEGIN 
IF PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3 THEN
  BEGIN
  SELECT SUM(VALORUPC)
    INTO MI_VALORUPC
  FROM BENEFICIARIOS_UPC
  WHERE COMPANIA = UN_COMPANIA
    AND ID_DE_EMPLEADO = UN_EMPLEADO
    AND ESTADO_ACTUALUPC <> 3;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_VALORUPC := 0;           
  END;
  IF MI_VALORUPC IS NULL THEN
    MI_VALORUPC:=0;
  END IF;
  IF UPPER(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'APORTE UPC PATRONO',PCK_DATOS.FC_MODULONOMINA,SYSDATE),'NO')) = 'SI' THEN
  PCK_NOMINA.CN(486) := MI_VALORUPC;  
  ELSE
  PCK_NOMINA.CN(123) := MI_VALORUPC;
  END IF;
END IF;  
END PR_VALORBENEFICIARIOSUPC;  

PROCEDURE PR_PRIMASEMESTRALOBREROS(
    /*
    NAME              : PR_PRIMASEMESTRALOBREROS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
    DATE MIGRADOR     : 06/12/2019
    TIME              : 11:58 AM
    SOURCE MODULE     : Nueva TAR1000095516 
    MODIFIER          : Se tienen en cuenta factores de enero a junio TAR 1000099825
    DATE MODIFIED     : 16/06/2020
    TIME              : 04:00 pm
    DESCRIPTION       : Procedimiento cÃ¡lculo prima de servicios de la AlcaldÃ­a de Bucaramanga, compaÃ±Ã­a de obreros.
    */
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_VALORPRIMA         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIASPACTADOSPRIMA  NUMBER DEFAULT 0;
BEGIN
    PCK_NOMINA.GL_FACTORPS := 0;
  PCK_NOMINA.GL_DCC := 0;
    PCK_NOMINA.GL_DNT := 0; 
    IF PCK_NOMINA.GL_SMES >= 7 THEN
        MI_DIASPACTADOSPRIMA := PCK_NOMINA.FC_CN(67);
        PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    PCK_NOMINA.GL_FECHAFPS := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');    
        --Para el pago de la prima de servicios en diciembre se deben tener en cuenta los factores del 01 de julio al 31 de diciembre del mismo aÃ±o.
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 7, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);        
    ELSE
        MI_DIASPACTADOSPRIMA := PCK_NOMINA.FC_CN(90);
        PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAFPS := TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        --Para el pago de la prima de servicios en junio se deben tener en cuenta los factores del 01 de enero al 30 de junio.
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);        
    END IF; 
    PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
    PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO >= PCK_NOMINA.GL_FECHAIPS AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO <= PCK_NOMINA.GL_FECHAFPS) THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO ELSE PCK_NOMINA.GL_FECHAFPS END;    

    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339); 
    --(APINEDA:08/12/2019)-Se cargan CN con valores correspondientes a los pagos especiales del empleado para el calculo de la Prima de servicios.
    PCK_NOMINA_COM4.PR_PAGOESPECIALEMPLEADO(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES);
    PCK_NOMINA.GL_FACTORPS := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END --Jornal
              + PCK_NOMINA.FC_CN(80)  --Auxilio de transporte
              + ((PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CNA(70)) / 12) --Horas extra 
              + PCK_NOMINA.FC_CN(190) --Prima climatica
              + PCK_NOMINA.FC_CN(192) --Prima de alimentaciÃ³n
              + PCK_NOMINA.FC_CN(194) --Auxilio de movilizaciÃ³n
              + PCK_NOMINA.FC_CN(195); --Sobresueldo

    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;

  MI_VALORPRIMA := PCK_SYSMAN_UTL.FC_ROUND((((PCK_NOMINA.GL_FACTORPS / 180) * PCK_NOMINA.GL_DCC) / 30) * MI_DIASPACTADOSPRIMA, 0);

    IF PCK_NOMINA.GL_SPER = 4 THEN
        FOR i IN 2..699 LOOP
            --(APINEDA:02/12/2019)-TAR 1000095516 Se agrega condiciÃ³n para que no sea puesto en 0 el indicador 402 para liquidar prima de navidad.
            --CC4399 MPEREZ - Se agrega condicion para no poner en 0 los conceptos 160 y 193 para que se puedan tener en cuenta los datos que se configurar por novedad.
            IF (i <> 125) AND (i <> 303) AND (i <> 160) AND (i <> 193) AND (i <> 301) AND (i <> 300) AND (i < 599 AND i <> 402) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;    

    /*INI_CC4399_MPEREZ - Se ajusta para que valide si el 160 tiene datos no lo vuelva a calcular*/
    IF PCK_NOMINA.FC_CN(160) = 0 THEN
        PCK_NOMINA.CN(160) := MI_VALORPRIMA;
    END IF;
    /*FIN_CC4399_MPEREZ*/

    PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(951) := MI_DIASPACTADOSPRIMA;
    PCK_NOMINA.CN(952) := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.CN(953) := PCK_NOMINA.GL_DNT;
    PCK_NOMINA.CN(945) := PCK_NOMINA.GL_FACTORPS;    

END PR_PRIMASEMESTRALOBREROS;

PROCEDURE PR_PRIMANAVIDADOBREROS(
    /*
    NAME              : PR_PRIMANAVIDADOBREROS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
    DATE MIGRADOR     : 05/12/2019
    TIME              : 05:48 PM
    SOURCE MODULE     : Nueva TAR1000095516 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Procedimiento cÃ¡lculo prima de navidad de la AlcaldÃ­a de Bucaramanga, compaÃ±Ã­a de obreros.
    */
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_VALORPRIMA        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIASNOTRABAJADOS  NUMBER DEFAULT 0;
    MI_FECHAFPN          DATE;
BEGIN
    PCK_NOMINA.GL_FACTORPN := 0;
  PCK_NOMINA.GL_DCC := 0;
    PCK_NOMINA.GL_DNT := 0; 
  PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
  MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');   
  PCK_NOMINA.GL_FECHAIPN := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPN END;
  MI_FECHAFPN := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO >= PCK_NOMINA.GL_FECHAIPN AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO <= MI_FECHAFPN) THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO ELSE MI_FECHAFPN END;   

  PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    --(APINEDA:08/12/2019)-Se cargan CN con valores correspondientes a los pagos especiales del empleado 
    PCK_NOMINA_COM4.PR_PAGOESPECIALEMPLEADO(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES);
    PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END --Jornal
              + PCK_NOMINA.FC_CN(80)  --Auxilio de transporte
              + ((PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CNA(70)) / 12) --1/12 Horas extra 
              + ((PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(155)) / 12) --1/12 Prima vacaciones
              + ((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(160)) / 12) --1/12 Prima de servicios Junio y diciembre del mismo aÃ±o
              + PCK_NOMINA.FC_CN(190) --Prima climatica
              + PCK_NOMINA.FC_CN(192) --Prima de alimentaciÃ³n
              + PCK_NOMINA.FC_CN(194) --Auxilio de movilizaciÃ³n
              + PCK_NOMINA.FC_CN(195); --Sobresueldo

    FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    MI_DIASNOTRABAJADOS := (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
        IF (MI_DIASNOTRABAJADOS) > 0 THEN
            PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + MI_DIASNOTRABAJADOS;
        END IF;
    END LOOP;   
  PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938); 
  PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
    PCK_NOMINA.CN(936) := 23; 

  MI_VALORPRIMA := PCK_SYSMAN_UTL.FC_ROUND((((PCK_NOMINA.GL_FACTORPN / 360) * PCK_NOMINA.GL_DCC) / 30) *  PCK_NOMINA.FC_CN(936), 0);

    IF PCK_NOMINA.GL_SPER = 4 THEN
        --CC4399 MPEREZ - Se agrega condicion para no poner en 0 los conceptos 160 y 193 para que se puedan tener en cuenta los datos que se configurar por novedad.
        FOR I IN 2 .. 599 LOOP
            IF ((I <> 125) AND (I <> 160) AND (I <> 193) AND (I <> 67) AND (I <> 404)) AND (I < 599) OR (I >= 600 AND I <= 798) AND (PCK_NOMINA.FC_CN(I) > 0 AND PCK_NOMINA.FC_CN(I) < 1) THEN
                PCK_NOMINA.CN(I) := 0;
            END IF;
        END LOOP;
    END IF;  

    PCK_NOMINA.CN(158) := MI_VALORPRIMA;
    --Guardando factores Prima de Navidad
    PCK_NOMINA.CN(930) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    PCK_NOMINA.CN(933) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(934) := PCK_NOMINA.FC_CN(192);    
    PCK_NOMINA.CN(937) := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.CN(938) := PCK_NOMINA.GL_DNT; 
    --(APINEDA:09/12/2019)-Se agrega llamado a procedimiento para calcular prima de costo de vida obreros.
    PR_PRIMACOSTOVIDAOBREROS(UN_COMPANIA);
END PR_PRIMANAVIDADOBREROS;

PROCEDURE PR_PAGOESPECIALEMPLEADO
/*
NAME              : PR_PAGOESPECIALEMPLEADO
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
DATE MIGRADOR     : 06/12/2019
TIME              : 03:00 PM
SOURCE MODULE     :
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       : Carga el type PCK_NOMINA.CN con el valor de los pagos especiales configurados por empleado.
PARAMETERS        :
MODIFICATIONS     :

*/
(
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES      IN PCK_SUBTIPOS.TI_MES
)
AS
    MI_RS   SYS_REFCURSOR;
BEGIN
    <<PAGOSESPECIALESEMPLEADO>>
    FOR MI_RS IN (
        SELECT PE.ID_DE_CONCEPTO,
            DP.DIAS,
            DP.VALOR
        FROM PAGOESPECIAL PE
        INNER JOIN DETALLEPAGOESPECIAL DP
             ON PE.COMPANIA = DP.COMPANIA
            AND PE.CODIGO = DP.CODIGOPAGOESP
        INNER JOIN PAGOESPECIAL_PERSONAL PEP
             ON DP.COMPANIA = PEP.COMPANIA
            AND DP.CODIGOPAGOESP = PEP.CODIGOPAGOESP
            AND DP.CONSECUTIVO = PEP.CODIGODETALLEPAGO
        WHERE PEP.COMPANIA = UN_COMPANIA
        AND PEP.ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
        AND DP.ANO = UN_ANO
        AND (PE.MES = 0 OR PE.MES = UN_MES)       
    )
    LOOP
        IF MI_RS.ID_DE_CONCEPTO > 0 AND MI_RS.ID_DE_CONCEPTO <= PCK_NOMINA.MAXI THEN
            PCK_NOMINA.CN(MI_RS.ID_DE_CONCEPTO) := MI_RS.VALOR;            
        END IF;
    END LOOP PAGOSESPECIALESEMPLEADO;

END PR_PAGOESPECIALEMPLEADO;

 PROCEDURE PR_LIQUIDARPRIMANTIGIDI
  /*
    NAME              : PR_LIQUIDARPRIMANTIGIDI
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : JUAN DANILO ORDUZ RIVERO
    DATE MIGRATION    : 28/07/2020
    TIME              : 09:11 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIMEUN_           : 
    DESCRIPTION       :

  */

AS 
 MI_FECVAC        DATE;
 MI_FECHAFIN      DATE;
 MI_FECHAINI      DATE;
 MI_VPA         NUMBER;
 MI_PRIMANTIG     NUMBER;
 MI_ANTIGD        NUMBER;
 MI_ANTIG       NUMBER;
 MI_DISTRDD       NUMBER;
 MI_DISTR       NUMBER;
 MI_DIASINCAPATEP1    NUMBER := 0;
 MI_DIASINCAPATEP2    NUMBER := 0;
 MI_DIASINCAPHOSP1    NUMBER := 0;
 MI_DIASINCAPHOSP2    NUMBER := 0;
 MI_PPA         NUMBER;
 I_ANTIG                NUMBER;

BEGIN

    SELECT
        PERIODOS.FECHAFINAL
        INTO MI_FECHAFIN
    FROM PERIODOS
    WHERE PERIODOS.COMPANIA = PCK_NOMINA.GL_COMPANIA
    AND PERIODOS.MES = PCK_NOMINA.GL_SMES
    AND PERIODOS.ANO = PCK_NOMINA.GL_SANO
    AND PERIODOS.PERIODO = PCK_NOMINA.GL_SPER
    AND PERIODOS.ID_DE_PROCESO = 1;

-- MI_FECHAFIN := TO_DATE('30/'||PCK_NOMINA.GL_SMES || '/' ||PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
 
 --IF (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESTADO_ACTUAL <> 3 OR (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO >= MI_FECHAFIN + 1)) AND PCK_NOMINA.FC_CN(170) = 0 THEN
 -- EFCABRERA TICKET: 7713183 Se cambia el IF para que tambien liquide prima de antiguedad a empleados en retiro
 IF ( PCK_NOMINA.FC_CN(170) = 0 ) THEN
 -- T: 7713183
  MI_ANTIGD := ( PCK_SYSMAN_UTL.FC_EDAD (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO, TO_DATE((CASE WHEN PCK_NOMINA.GL_SPER = 1 THEN 15 ELSE (CASE WHEN PCK_NOMINA.GL_SMES = 2 THEN 28 ELSE 30 END)-1 END) || '/' || PCK_NOMINA.GL_SMES || '/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 3));
  MI_ANTIG := MI_ANTIGD / 360;
-- Se valida en que quincena cumple los 4 o 9 o 14 aÃ±os  
  IF MI_ANTIG = 4 OR MI_ANTIG = 9 OR MI_ANTIG = 14 THEN 
    IF PCK_NOMINA.GL_SMES = TO_NUMBER(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO, 'MM')) THEN 
      IF (TO_NUMBER(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO, 'DD')) >= TO_NUMBER(TO_CHAR(MI_FECHAINI, 'DD'))) AND (TO_NUMBER(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO, 'DD')) <= TO_NUMBER(TO_CHAR(MI_FECHAFIN, 'DD'))) THEN
        --MPV
        IF TO_NUMBER(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO, 'DD')) = TO_NUMBER(TO_CHAR(MI_FECHAFIN, 'DD')) THEN
          MI_ANTIG := MI_ANTIG - 1;
        END IF;
      ELSIF (TO_NUMBER(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO, 'DD')) > TO_NUMBER(TO_CHAR(MI_FECHAINI, 'DD'))) AND ((MI_ANTIGD / 365.25) - (ROUND(MI_ANTIGD / 365.25)) < 0.3) THEN
        MI_ANTIG := MI_ANTIG - 1;
      END IF;
    ELSIF TO_NUMBER(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO, 'MM')) > PCK_NOMINA.GL_SMES THEN
      MI_ANTIG := MI_ANTIG; -- '-1 '  Pendiente julio maria del carmen
    ELSIF TO_NUMBER(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO, 'MM')) < PCK_NOMINA.GL_SMES THEN
      MI_ANTIG := MI_ANTIG; -- '-1 '  Pendiente julio maria del carmen
    ELSE
      I_ANTIG := MI_ANTIG; -- '-1 '  Pendiente julio maria del carmen
    END IF;
  END IF;
  MI_VPA      := 0;
  IF MI_ANTIG >= 4 AND MI_ANTIG < 9 THEN
    MI_PRIMANTIG := (PCK_NOMINA.FC_CN(1) * 7 / 100 * (PCK_NOMINA.FC_CN(9) + MI_DIASINCAPHOSP1 + MI_DIASINCAPATEP1) / 30) + (PCK_NOMINA.FC_CN(10) * 7 / 100 * (PCK_NOMINA.FC_CN(11) + MI_DIASINCAPHOSP2 + MI_DIASINCAPATEP2) / 30);
    MI_VPA := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) * 7 / 100, 0); 
    --MI_VPA := PCK_NOMINA.FC_CN(2) * 7 / 100;--(APINEDA:08/11/2021)- TICKET 7702214 Se comenta línea debido a que el valor de la prima de antiguedad para el calculo de BASP no debe ser proporcional.
    MI_PPA := 7;
    PCK_NOMINA.CN(448) := MI_PPA;
  ELSIF MI_ANTIG >= 9 AND MI_ANTIG < 14 THEN
    MI_PRIMANTIG := (PCK_NOMINA.FC_CN(1) * 9 / 100 * (PCK_NOMINA.FC_CN(9) + MI_DIASINCAPHOSP1 + MI_DIASINCAPATEP1) / 30) + (PCK_NOMINA.FC_CN(10) * 9 / 100 * (PCK_NOMINA.FC_CN(11) + MI_DIASINCAPHOSP2 + MI_DIASINCAPATEP2) / 30);
    MI_VPA := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) * 9 / 100, 0); 
    MI_PPA := 9;
    PCK_NOMINA.CN(448) := MI_PPA;
  ELSIF MI_ANTIG >= 14 THEN
    MI_PRIMANTIG := (PCK_NOMINA.FC_CN(1) * 11 / 100 * (PCK_NOMINA.FC_CN(9) + MI_DIASINCAPHOSP1 + MI_DIASINCAPATEP1) / 30) + (PCK_NOMINA.FC_CN(10) * 11 / 100 * (PCK_NOMINA.FC_CN(11) + MI_DIASINCAPHOSP2 + MI_DIASINCAPATEP2) / 30);
    MI_VPA := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) * 11 / 100, 0); 
    MI_PPA := 11;
    PCK_NOMINA.CN(448) := MI_PPA;
  END IF;
  MI_PRIMANTIG := PCK_SYSMAN_UTL.FC_ROUND(MI_PRIMANTIG, 0);
  PCK_NOMINA.CN(170) := PCK_SYSMAN_UTL.FC_ROUND(MI_PRIMANTIG, 0);

  IF MOD(PCK_NOMINA.FC_CN(170) *2,2) <> 0 AND PCK_NOMINA.CN(30) = 1 THEN
    PCK_NOMINA.CN(170) := PCK_NOMINA.FC_CN(170) + 1;
  END IF;
  MI_VPA := PCK_SYSMAN_UTL.FC_ROUND(MI_VPA, 0);
  PCK_NOMINA.CN(872) := MI_VPA;
  PCK_NOMINA.CN(827) := MI_VPA;

  IF PCK_NOMINA.FC_CN(404) = 1 THEN
    PCK_NOMINA.CN(170) := 0 ;
  END IF;
  --(APINEDA:18/06/2021) TAR 1000107290 Se asigna valor de concepto a variable global de Prima de Antiguedad
    PCK_NOMINA.GL_VPA := PCK_NOMINA.FC_CN(170); 
END IF;

END PR_LIQUIDARPRIMANTIGIDI;

PROCEDURE PR_LIQUIDARPRIMASECRETARIALIDI 
  /*
    NAME              : PR_LIQUIDARPRIMASECRETARIALIDI  --> EN ACCESS liquidarprimasecretarial  -
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : JUAN DANILO ORDUZ RIVERO
    DATE MIGRATION    : 28/07/2020
    TIME              : 09:11 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIMEUN_           : 
    DESCRIPTION       :

  */

AS 
MI_VPS        NUMBER:= 0;
MI_ALERTA           VARCHAR2(32767);
BEGIN
PCK_NOMINA.CN(178) :=0;
-- comentado por JM 24/11/2023 IF SUBSTR ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CARGO),1,3) = '440' THEN 
--JM 7737785  CALCULO DE PRIMAS CON ENCARGOS 
--IF (SUBSTR ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CARGO),1,3) = '440' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).CARGO_ENCARGO IS NULL) OR (SUBSTR ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CARGO),1,3) = '440' AND PCK_NOMINA.FC_CN(11) < 30 ) OR (SUBSTR ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).CARGO_ENCARGO),1,3) = '440') THEN
IF (SUBSTR ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CARGO),1,3) = '440' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).CARGO_ENCARGO IS NULL)  OR (SUBSTR ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).CARGO_ENCARGO),1,3) = '440') THEN
  PCK_NOMINA.CN(178) := CASE WHEN PCK_NOMINA.FC_CN(178) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(2) * 2 / 100, 0) ELSE PCK_NOMINA.FC_CN(178) END;
  MI_VPS := PCK_NOMINA.CN(178);
  -- comentado por JM el 24/11/2023 MI_VPS := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) >= 30 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) * 2 /100 END, 0);
  -- comentado por JM el 24/11/2023 PCK_NOMINA.GL_VPSEC := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) >= 30 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) * 2 /100 END, 0);
  PCK_NOMINA.GL_VPSEC := PCK_NOMINA.CN(178);
  -- PARA CARGOS QUE SON SECRETARIO PERO ESTAN EN ENCARGOS

  --- se deja este fragmento para hacer validaciones a futuro de estos casos JM 24/11/2023 cya que el  PCK_NOMINA.FC_CN(11) llega vacio aca 
      -- jm tiene el cargo o el encargo full  --
    /*IF (SUBSTR ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CARGO),1,3) = '440' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).CARGO_ENCARGO IS NULL)  OR  (SUBSTR ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CARGO),1,3) = '440' AND PCK_NOMINA.FC_CN(11) >= 30 ) THEN 
        PCK_NOMINA.CN(178) := CASE WHEN PCK_NOMINA.FC_CN(178) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(2) * 2 / 100, 0) ELSE PCK_NOMINA.FC_CN(178) END;
    END IF;
    -- jm encargo fraccionado cargo secretario / encargo diferente   
    IF (SUBSTR ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CARGO),1,3) = '440' AND PCK_NOMINA.FC_CN(11) < 30)  THEN     
        PCK_NOMINA.CN(178) := CASE WHEN PCK_NOMINA.FC_CN(178) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND( ((PCK_NOMINA_COM1.FC_SALARIOBASE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / PCK_NOMINA.FC_CN(9) ) * (2 / 100), 0) ELSE PCK_NOMINA.FC_CN(178) END;   
    END IF;
      -- jm encargo fraccionado encargo de secretario pero menor a 30 dias 
    IF (SUBSTR ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).CARGO_ENCARGO),1,3) = '440' AND PCK_NOMINA.FC_CN(11) < 30)  THEN     
        PCK_NOMINA.CN(178) := CASE WHEN PCK_NOMINA.FC_CN(178) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND( ( PCK_NOMINA.FC_CN(10)/ PCK_NOMINA.FC_CN(11) )* (2 / 100), 0) ELSE PCK_NOMINA.FC_CN(178) END;
    END IF; */
  ------- 

  /* IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO = '92197' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO = '90107' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO = '90400' THEN
    PCK_NOMINA.CN(178) := 0;
    MI_VPS := 0;
    PCK_NOMINA.GL_VPSEC := 0;
        --MI_ALERTA := 'Revisar cargo si sigue siendo Secretario para efectos de la Prima Secretarial, con CÃ©dula No.' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DCTO_IDENTIDAD || ' y ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRECOMPLETO || ' Codigo: ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;
       -- PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => PCK_NOMINA.GL_COMPANIA,
                                 --  UN_MENSAJE_COD => MI_ALERTA);
    END IF;   */  -- comentado por JM el 28/11/2023  7738421 
  ELSE
      PCK_NOMINA.CN(178) := 0;
      MI_VPS := PCK_NOMINA.FC_CN(178);
      PCK_NOMINA.GL_VPSEC := 0;
  END IF;
--PARACARGOS QUE NO SON SECRETARIOS PERO QUE ESTAN EN ENCARGO DE SECRETARIO
/* IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO = '00127' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO = '00027' THEN
  PCK_NOMINA.CN(178) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(2) * 2 / 100, 0);
  MI_VPS := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(2) >= 30 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) * 2 /100 END), 0);
  PCK_NOMINA.GL_VPSEC := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) >= 30 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) * 2 /100 END, 0);
    IF PCK_NOMINA.GL_PROCESOACTUAL <> '99' THEN
            MI_ALERTA := 'Revisar cargo si sigue siendo Secretario para efectos de la Prima Secretarial, con CÃ©dula No.' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DCTO_IDENTIDAD || ' y ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRECOMPLETO || ' Codigo: ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;
            PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => PCK_NOMINA.GL_COMPANIA,
                                       UN_MENSAJE_COD => MI_ALERTA);
        END IF;
END IF;  */  -- comentado por JM el 28/11/2023  7738421 

IF PCK_NOMINA.FC_CN(360) <> 0 THEN
    IF SUBSTR ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CARGO),1,3) = '440' THEN
        PCK_NOMINA.CN(178) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(2) * 2 / 100, 0);
        MI_VPS := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(11) >= 30 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) * 2 /100 END, 0);
        PCK_NOMINA.GL_VPSEC := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(2) >= 30 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) * 2 /100 END, 0);
    END IF;
END IF;

IF SUBSTR ((PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_CARGO),1,3) = '440' AND PCK_NOMINA.FC_CN(178) <> 0 THEN
    /* MI_ALERTA := 'Tiene pago de Prima secretaria y NO tiene cargo de Secretario. Revisar cargo si sigue siendo Secretario para efectos de la Prima Secretarial, con CÃ©dula No.' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DCTO_IDENTIDAD || ' y ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRECOMPLETO || ' Codigo: ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;
        PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => PCK_NOMINA.GL_COMPANIA,
                                   UN_MENSAJE_COD => MI_ALERTA);*/
         MI_VPS := PCK_NOMINA.CN(178);
END IF;
 MI_VPS := PCK_NOMINA.CN(178);

END PR_LIQUIDARPRIMASECRETARIALIDI;

PROCEDURE PR_PRIMA_DE_NAVIDAD_IDIPRON
  /*
    NAME              : PR_PRIMA_DE_NAVIDAD_IDIPRON
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : JUAN DANILO ORDUZ RIVERO
    DATE MIGRATION    : 28/07/2020
    TIME              : 09:11 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIMEUN_           : 
    DESCRIPTION       :
  
  */
  

AS 
    SALARIO                     NUMBER;
    MI_FECHAPAGO                DATE;    
    MI_FF                   DATE;
    MI_FECHAINICIO          DATE;
    PRIMAS2                 NUMBER;
    DOCEAVAS                NUMBER;
    EXTRAS                  NUMBER;
    MI_DIAS                     NUMBER;
    MI_AC                       NUMBER;
    MI_DOCEAVAS                 NUMBER;
    MI_ACUMVAC                  NUMBER;
    VPR                         NUMBER;
    VPS                         NUMBER; 
    MI_PRIMAS1                  NUMBER;
    APCESANT                    NUMBER;
    APMANEJO                    NUMBER;
    MI_VALOR                    NUMBER;
    
BEGIN
-- SALARIO := CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
--comentado por jm 05/12/2023 el de debe validar en base a el 30 de noviembre 
--JM 7738630 Calcular salario 30 nov si tiene encargo -- sino sueldo CN(1)
    BEGIN 

        SELECT SUELDOMENSUAL 
        INTO SALARIO
        FROM ENCARGOS 
        WHERE COMPANIA = PCK_NOMINA.GL_COMPANIA 
        AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
        AND TO_DATE('30/11/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')
        BETWEEN FECHAINICIO AND FECHAFINAL;

        MI_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 10, PCK_NOMINA.GL_SANO, 11, 3,null,null,null, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        SALARIO := PCK_NOMINA.CNP(10); 

        EXCEPTION  WHEN NO_DATA_FOUND THEN
            SALARIO := PCK_NOMINA.FC_CN(1);
    END;
    
    
    
     
    /* PCK_NOMINA.GL_AUXT := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 80, PCK_NOMINA.GL_SANO, 11, 3,null,null,null, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.GL_AUXT := PCK_NOMINA.CNP(80); 
    PCK_NOMINA.GL_AUXA := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 79, PCK_NOMINA.GL_SANO, 11, 3,null,null,null, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.GL_AUXA := PCK_NOMINA.CNP(79); */ -- se comenta porque esta generando error si el funcionario no cuenta con el subsidio/auxili jm 7738630  05/12/2023 

    --AND PCK_NOMINA.GL_SMES > 6
IF (PCK_NOMINA.FC_CN(402) <> 0 OR PCK_NOMINA.FC_CN(404) <> 0)  THEN     --'INDICADOR DE LIQUIDAR PRIMA DE NAVIDAD
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
        MI_FECHAINICIO := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO;
    ELSE
        MI_FECHAINICIO := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    END IF;
     
  MI_FF := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    IF PCK_NOMINA.FC_CN(404) <> 0 THEN 
    MI_FF := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO; 
    END IF; --JM 07/03/2024
  MI_DIAS := PCK_SYSMAN_UTL.FC_EDAD(MI_FECHAINICIO, MI_FF,3);
  PCK_NOMINA.CN(39) := MI_DIAS;
    MI_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 359, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
    --'DIAS = DIASNOREMUNERADOS_("359", CSTR(S_ANO), PERSONAL![ID_DE_EMPLEADO], DIAS)
    MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES-1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); --ACUMULADO DESDE LA FECHA_DE_INGRESO
    MI_AC := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359);
    PCK_NOMINA.CN(355) := MI_DIAS - PCK_NOMINA.FC_CNA(359);
     DOCEAVAS :=PCK_NOMINA.FC_CN(355);
    MI_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(MI_FECHAINICIO, (CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO ELSE MI_FECHAPAGO END) - MI_AC);
    PCK_NOMINA.CN(855) := MI_DOCEAVAS;
    PRIMAS2 := 0;
     
                      
    MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO - 1), 12, 1, PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    MI_ACUMVAC := PCK_NOMINA.FC_CNA(155);
    MI_ACUMVAC := PCK_NOMINA.FC_CNA(525);
    MI_ACUMVAC := PCK_NOMINA.FC_CNA(501);
    MI_ACUMVAC := PCK_NOMINA.FC_CNA(544);
    MI_ACUMVAC := PCK_NOMINA.FC_CN(155)+PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(525) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CNA(544);
    IF PCK_NOMINA.FC_CN(94) = 1 THEN --' ([PERSONAL]![INICIO_DISFRUTE] >= CVDATE("01/01/" and CSTR(S_ANO))) THEN
    MI_ACUMVAC := 0;
    END IF;
  
    MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, 1, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO -1), 12, 1, PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
  
  DOCEAVAS :=PCK_NOMINA.FC_CN(62);
 
     IF PCK_NOMINA.FC_CN(404) <> 0 THEN
                 
     MI_PRIMAS1 := PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_GRPNV + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_VPA  + (CASE WHEN PCK_NOMINA.FC_CN(85) <> 0 THEN ((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) * PCK_NOMINA.FC_CN(85)/100) ELSE 0 END);
     ELSE 
     
     MI_PRIMAS1 := PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_GRPNV + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_VPA +/* VPR + VPS + */PCK_NOMINA.GL_VPT;
     
     END IF;--JM 14/03/2024
  
    
    MI_VALOR := 0;
    
    BEGIN 
        SELECT SUM(1) 
        INTO MI_VALOR
        FROM HISTORICOS 
        WHERE COMPANIA = PCK_NOMINA.GL_COMPANIA 
        AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
        AND ID_DE_PROCESO =  PCK_NOMINA.GL_PROCESOACTUAL
        AND MES = 12 
        AND PERIODO = 4
        AND ANO = PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO)
        AND ID_DE_CONCEPTO = 158;
        EXCEPTION  WHEN NO_DATA_FOUND THEN
        MI_VALOR := -1;
    END;
    
    
    PCK_NOMINA.CN(150) := CASE WHEN PCK_NOMINA.FC_CN(150) <> 0 THEN ((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(506))) ELSE PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(506))/360*PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO IS NULL THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO END,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO)), 0) END;
    
    IF MI_VALOR > 0 THEN 
        MI_VALOR := PCK_NOMINA.CN(150);
    ELSE 
        MI_VALOR := PCK_NOMINA.CN(150) + PCK_NOMINA.CNA(150);
    END IF;
         
    PRIMAS2 := PRIMAS2 + ((PCK_NOMINA.FC_CNA(506) - PCK_NOMINA.FC_CNA(695) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(503) + MI_ACUMVAC) - PCK_NOMINA.FC_CNA(637)) + MI_VALOR + PCK_NOMINA.FC_CNA(160)+PCK_NOMINA.FC_CN(160);
    EXTRAS := ((PCK_NOMINA.FC_SUMACONA(48, 58) + PCK_NOMINA.FC_CNA(63)) + PCK_NOMINA.FC_SUMACONA(48, 58) + PCK_NOMINA.FC_CN(63)) / 6;

    IF (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INICIO_DISFRUTE >= TO_DATE('01/01/' || (PCK_NOMINA.GL_SANO + 1), 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INICIO_DISFRUTE <= TO_DATE('02/01/' || (PCK_NOMINA.GL_SANO + 1), 'DD/MM/YYYY')) OR 
       (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INICIO_DISFRUTE >= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INICIO_DISFRUTE <= TO_DATE('02/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')) THEN
        PRIMAS2 := PRIMAS2 - PCK_NOMINA.FC_CN(155);
    END IF;
  
  MI_DOCEAVAS := CASE WHEN MI_DOCEAVAS > 12 THEN 12 ELSE MI_DOCEAVAS END;
    --'SALARIO = IIF(CN(11) <> 0, CN(10), CN(1))
    

  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO > TO_DATE('01/06/1994', 'DD/MM/YYYY') THEN
        EXTRAS := 0;
    END IF;
    IF  PCK_NOMINA.FC_CN(428) <> 0 THEN  --'PARA DISMINUIR LAS DOCEAVAS CUANDO NO SON MESES COMPLETOS
    MI_DOCEAVAS := MI_DOCEAVAS - PCK_NOMINA.FC_CN(428);
    END IF;
  --'ACUMULADO PRIMA DE SERVICIOS DEL ANO ACTUAL
  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO < TO_DATE('01/06/1994', 'DD/MM/YYYY') THEN
    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND((((SALARIO + MI_PRIMAS1 + (PRIMAS2 / 12) + (EXTRAS / 12)) / 30) * 30) / 360 * PCK_NOMINA.FC_CN(355), 0);
        PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(504), 0);
  ELSE
    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(((SALARIO + MI_PRIMAS1 + PRIMAS2 / 12 + EXTRAS / 12) / 360) * PCK_NOMINA.FC_CN(355), 0);   
        DOCEAVAS :=PCK_NOMINA.FC_CN(158);
    PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(504), 0);
        DOCEAVAS :=PCK_NOMINA.FC_CN(158);
        DOCEAVAS :=PCK_NOMINA.FC_CN(504);
  END IF;
                 
  IF PCK_NOMINA.FC_CN(590) <> 0 THEN
    PCK_NOMINA.CN(709) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CN(201)) / 5), 0);
  END IF;
END IF;
    
--  'GUARDANDO FACTORES PARA PRIMA DE NAVIDAD
--JM INI 24/04/2024
         PCK_NOMINA.CN(841) := PCK_NOMINA.GL_AUXT;
         PCK_NOMINA.CN(842) := PCK_NOMINA.GL_AUXA ;
         PCK_NOMINA.CN(843) := PCK_NOMINA.GL_GRPNV+PCK_NOMINA.GL_GRPGV;
         PCK_NOMINA.CN(844) := PCK_NOMINA.FC_CN(62);
         PCK_NOMINA.CN(845) := PCK_NOMINA.GL_VPA; 
         PCK_NOMINA.CN(846) := PCK_NOMINA.FC_CN(355);
         PCK_NOMINA.CN(347) := PCK_NOMINA.FC_CNA(359);
         PCK_NOMINA.CN(849) := PCK_NOMINA.FC_CN(155)+PCK_NOMINA.FC_CNA(155)+PCK_NOMINA.FC_CNA(525); --PCK_SYSMAN_UTL.FC_ROUND((( PCK_NOMINA.FC_CN(155) + MI_ACUMVAC) -  PCK_NOMINA.FC_CNA(637)) / 12, 0);      --   ' prima de vacaciones
         PCK_NOMINA.CN(848) := PCK_NOMINA.GL_VPT;
         PCK_NOMINA.CN(850) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160)), 0);  --'FACTOR PRIMA SEMESTRAL
         PCK_NOMINA.CN(851) := MI_VALOR; -- PCK_NOMINA.FC_CN(150);
         PCK_NOMINA.CN(852) := 0; --'Round(Cna(180) / 12, 0) 'quinquenio
         PCK_NOMINA.CN(853) := SALARIO;-- ' IIf(cn(11) <> 0, cn(10), cn(1))      --   'sueldo
         PCK_NOMINA.CN(855) := DOCEAVAS;    --  'doceavas
         PCK_NOMINA.CN(856) := PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNV + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_VPA +/* VPR +*/ PCK_NOMINA.GL_VPS + PCK_NOMINA.GL_VPT + 
                               PCK_NOMINA.FC_CN(849) + PCK_NOMINA.FC_CN(850) + PCK_NOMINA.FC_CN(851) + PCK_NOMINA.FC_CN(852) + PCK_NOMINA.FC_CN(853) + PCK_NOMINA.FC_CN(854) + PCK_NOMINA.FC_CN(844);
         PCK_NOMINA.CN(856) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(856), 0);
         PCK_NOMINA.CN(857) := PCK_NOMINA.FC_CN(158) - PCK_NOMINA.FC_CN(125) - PCK_NOMINA.FC_CN(127) - PCK_NOMINA.FC_CN(250) - PCK_NOMINA.FC_CN(124) - PCK_NOMINA.FC_CN(745) - 
                           PCK_NOMINA.FC_CN(858) - PCK_NOMINA.FC_CN(733) - PCK_NOMINA.FC_CN(713) - PCK_NOMINA.FC_CN(709) - PCK_NOMINA.FC_CN(654); --'total prima navidad
         
         PCK_NOMINA.CN(124) := PCK_NOMINA.FC_CN(124);
         PCK_NOMINA.CN(594) := PCK_NOMINA.FC_CN(125) + PCK_NOMINA.FC_CN(250) + PCK_NOMINA.FC_CN(124) + PCK_NOMINA.FC_CN(600) + PCK_NOMINA.FC_CN(709) + PCK_NOMINA.FC_CN(711) + 
                              PCK_NOMINA.FC_CN(745) + PCK_NOMINA.FC_CN(733) + PCK_NOMINA.FC_CN(713) + PCK_NOMINA.FC_CN(753) + PCK_NOMINA.FC_CN(654) + PCK_NOMINA.FC_CNA(659) + 
                              PCK_NOMINA.FC_CN(127); --'TOTAL DESCUENTOS EN PRIMA DE NAVIDAD
--END IF; --JM FIN 24/04/2024
IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS = 'CES286' AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO < TO_DATE('01/06/1994','DD/MM/YYYY') THEN
           APCESANT := 0;
           APMANEJO := 0;
           IF PCK_NOMINA.FC_CN(158) <> 0 THEN
           PCK_NOMINA.CN(260) := PCK_NOMINA.FC_CN(158);
           END IF;
           PCK_NOMINA.CN(261) := CASE WHEN PCK_NOMINA.FC_CN(261) = 0 THEN 12 ELSE PCK_NOMINA.FC_CN(261) END;    --' Porcentaje de Aporte Cesantias
           PCK_NOMINA.CN(265) := CASE WHEN PCK_NOMINA.FC_CN(265) = 0 THEN 12 ELSE PCK_NOMINA.FC_CN(265) END;  -- ' Porcentaje Base para Cuota de Manejo
           PCK_NOMINA.CN(262) := CASE WHEN PCK_NOMINA.FC_CN(262) = 0 THEN 2 ELSE PCK_NOMINA.FC_CN(262)END;  --' Porcentaje de Aporte Sobre Base Couta de Manejo
           APCESANT := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(260) * PCK_NOMINA.FC_CN(261) / 100, 0);
           APMANEJO := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(260) * PCK_NOMINA.FC_CN(265) / 100 * PCK_NOMINA.FC_CN(262) / 100, 0);
           PCK_NOMINA.CN(263) := CASE WHEN PCK_NOMINA.FC_CN(263) = 0 THEN APCESANT + APMANEJO ELSE PCK_NOMINA.FC_CN(263)END;
           
           -- TICKET 7737716 EFCM: SE CAMBIAN CONCEPTOS PARA FONCEP EN IDIPRON
           PCK_NOMINA.CN(440) := APCESANT;
           PCK_NOMINA.CN(441) := APMANEJO;
           -- TICKET 7737716 FIN --
END IF;
      --  ' **   FIN FAVIDI MENSUAL
        PCK_NOMINA.CN(927) := PCK_NOMINA.FC_CNA(927);
        PCK_NOMINA.CN(928) := PCK_NOMINA.FC_CNA(928);
        PCK_NOMINA.CN(87) := PCK_NOMINA.FC_CN(598);
        
/*      --  '                    D E S C U E N T O S
        PCK_NOMINA.CN(635) := 0;
        PCK_NOMINA.CN(636) := 0;
        PCK_NOMINA.CN(699) := PCK_NOMINA.FC_SUMACON(600, 698);--  ' Total descuentos Cooperativas
        PCK_NOMINA.CN(799) := PCK_NOMINA.FC_SUMACON(700, 798);-- ' Total descuentos Embargos
        PCK_NOMINA.CN(140) := PCK_NOMINA.FC_CN(125) + PCK_NOMINA.FC_CN(250) + PCK_NOMINA.FC_CN(699) + PCK_NOMINA.FC_CN(799) + PCK_NOMINA.FC_CN(124) + PCK_NOMINA.FC_CN(858) + PCK_NOMINA.FC_CN(127);
        PCK_NOMINA.CN(594) := PCK_NOMINA.FC_CN(125) + PCK_NOMINA.FC_CN(250) + PCK_NOMINA.FC_CN(124) + PCK_NOMINA.FC_CN(755) + PCK_NOMINA.FC_CNA(659) + PCK_NOMINA.FC_CN(709) + PCK_NOMINA.FC_CN(710) + PCK_NOMINA.FC_CN(711) + PCK_NOMINA.FC_CN(712) + PCK_NOMINA.FC_CN(600) + PCK_NOMINA.FC_CN(753) + PCK_NOMINA.FC_CN(754);
       PCK_NOMINA. CN(836) := PCK_NOMINA.FC_CN(160) - PCK_NOMINA.FC_CN(250) - PCK_NOMINA.FC_CN(125) - PCK_NOMINA.FC_CN(124) - PCK_NOMINA.FC_CN(709) - PCK_NOMINA.FC_CN(712) - PCK_NOMINA.FC_CN(710) - PCK_NOMINA.FC_CN(745) - PCK_NOMINA.FC_CNA(659) - PCK_NOMINA.FC_CN(753) - PCK_NOMINA.FC_CN(600);--  'TOTAL PRIMA SEMESTRAL
*/
PCK_NOMINA.CN(143):= 0;
    IF PCK_PARST.FC_PAR('LIQUIDACION PARA IDIPRON', 'NO') = 'SI' AND PCK_NOMINA.GL_SPER = 4 THEN
        PCK_NOMINA.CN(97) := PCK_NOMINA.FC_CN(97) - PCK_NOMINA.FC_CN(2) - PCK_NOMINA.FC_CN(62) - PCK_NOMINA.FC_CN(186) - PCK_NOMINA.FC_CN(79)- PCK_NOMINA.FC_CN(80) - PCK_NOMINA.FC_CN(170) -
                            PCK_NOMINA.FC_CN(150) - PCK_NOMINA.FC_CN(178);
        PCK_NOMINA.CN(2) := 0;
        PCK_NOMINA.CN(62) := 0;
        PCK_NOMINA.CN(186) := 0;
        PCK_NOMINA.CN(79) := 0;
        PCK_NOMINA.CN(80) := 0;
        PCK_NOMINA.CN(170) := 0;
        PCK_NOMINA.CN(150) := 0;
        PCK_NOMINA.CN(178) := 0;
    END IF;
    
END PR_PRIMA_DE_NAVIDAD_IDIPRON;

PROCEDURE PR_LIQUIDARFAVIDI 
  /*
    NAME              : PR_LIQUIDARFAVIDI  --> EN ACCESS LiquidarFavidi  -
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : JUAN DANILO ORDUZ RIVERO
    DATE MIGRATION    : 28/07/2020
    TIME              : 09:11 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIMEUN_           : 
    DESCRIPTION       :

  */
AS 
MI_VALORULTIMABONIFICACIONN   NUMBER;
MI_APCESANT           NUMBER;
MI_APMANEJO           NUMBER;
MI_FACTOR           NUMBER;
VPA                           NUMBER;
GRPGV                         NUMBER;    
VPT                           NUMBER;
AUXT                          NUMBER;
AUXA                          NUMBER;
PV                            NUMBER;
LICCESAN                      NUMBER;
MI_AC                         NUMBER;
BEGIN

--APORTE MENSUAL A FAVIDI CESANTIAS

IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS = 'CES286' AND (PCK_NOMINA.GL_PERIODOACTUAL = 2 OR PCK_NOMINA.GL_PERIODOACTUAL = 3) AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO < TO_DATE('01/06/1994', 'DD/MM/YYYY') THEN 
  MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 4, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 4, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
  MI_VALORULTIMABONIFICACIONN := 0;

  PCK_NOMINA.CNA(150) := MI_VALORULTIMABONIFICACIONN;

  MI_APCESANT := 0;
  MI_APMANEJO := 0;

  IF  PCK_NOMINA.FC_CN(972) = 0 THEN

  PCK_NOMINA.CN(26) := 0;
  MI_FACTOR := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + 
                 PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CN(504) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(158) + 
                 PCK_NOMINA.FC_CN(105) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CN(506) + PCK_NOMINA.FC_CN(508) + PCK_NOMINA.FC_CN(502) + PCK_NOMINA.FC_CN(500) + PCK_NOMINA.FC_CN(509) + 
                 PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(520) + PCK_NOMINA.FC_CN(521) + PCK_NOMINA.FC_CN(548) + PCK_NOMINA.FC_CN(525) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CN(523);

  PCK_NOMINA.CN(972) := MI_FACTOR;
  PCK_NOMINA.CN(264) := 30;
  END IF;
  
  -- TICKET 7737716 EFCM: SE CAMBIAN CONCEPTOS PARA FONCEP EN IDIPRON
  PCK_NOMINA.CN(261) := CASE WHEN PCK_NOMINA.FC_CN(261) = 0 THEN 12 ELSE PCK_NOMINA.CN(261) END;
  PCK_NOMINA.CN(265) := CASE WHEN PCK_NOMINA.FC_CN(265) = 0 THEN 12 ELSE PCK_NOMINA.CN(265) END;
  PCK_NOMINA.CN(262) := CASE WHEN PCK_NOMINA.FC_CN(262) = 0 THEN 2 ELSE PCK_NOMINA.CN(262) END;
  MI_APCESANT := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(972) * PCK_NOMINA.FC_CN(261) / 100, 0);
  MI_APMANEJO := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(972) * PCK_NOMINA.FC_CN(265) / 100 * PCK_NOMINA.FC_CN(262) /100, 0); 
  PCK_NOMINA.CN(263) := CASE WHEN  PCK_NOMINA.FC_CN(263) = 0 THEN MI_APCESANT + MI_APMANEJO ELSE PCK_NOMINA.FC_CN(263) END;
  PCK_NOMINA.CN(440) := MI_APCESANT;
  PCK_NOMINA.CN(441) := MI_APMANEJO;
  -- TICKET 7737716 FIN --

--CONCEPTOS PARA APORTES FAVIDI

  PCK_NOMINA.CN(900) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160), 0);
  PCK_NOMINA.CN(901) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE (PCK_NOMINA.FC_CN(1) + PCK_NOMINA.FC_CN(504)) END;
  PCK_NOMINA.CN(902) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) - (PCK_NOMINA.FC_CN(616) * 2) + PCK_NOMINA.FC_CNA(504), 0); 
  PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
  PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
  PCK_NOMINA.CN(906) := PCK_NOMINA.GL_VPA + PCK_NOMINA.FC_CN(502);
  PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(525), 0); 
  PCK_NOMINA.CN(908) := PCK_NOMINA.GL_GRPGV + PCK_NOMINA.FC_CN(509);
  PCK_NOMINA.CN(910) := PCK_NOMINA.FC_CN(264);
  PCK_NOMINA.CN(912) := LICCESAN;
  PCK_NOMINA.CN(913) := PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(500);
  PCK_NOMINA.CN(914) := CASE WHEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(506) + PCK_NOMINA.FC_CN(506), 0) = 0  THEN
                  (MI_VALORULTIMABONIFICACIONN + PCK_NOMINA.FC_CNA(528)) ELSE 
                  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(506) + PCK_NOMINA.FC_CN(506), 0) END;
END IF;
-- FIN FAVIDI MENSUAL
IF PCK_NOMINA.FC_CN(168) <> 0 THEN 
    PCK_NOMINA.CN(972) := 0; 
END IF;

IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS = 'CES286' AND PCK_NOMINA.FC_CN(349) <> 0 OR PCK_NOMINA.FC_CN(433) <> 0 THEN
  MI_FACTOR := PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(170) + PCK_NOMINA.FC_CN(61) + PCK_NOMINA.FC_CN(186) + PCK_NOMINA.FC_CN(80) + 
         PCK_NOMINA.FC_CN(79) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(158) + 
         PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CN(504);

  PCK_NOMINA.CN(972):= MI_FACTOR;
END IF;

IF PCK_NOMINA.FC_CN(400) <> 0 OR PCK_NOMINA.FC_CN(349) <> 0 THEN 
  IF  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS = 'CES289' THEN  
    PCK_NOMINA.CN(972) := 0;
    PCK_NOMINA.CN(913) := 0;
    PCK_NOMINA.CN(910) := 0;
  END IF;
END IF; 


END PR_LIQUIDARFAVIDI;

PROCEDURE PR_PRIMA_DE_SERVICIOS_IDIPRON 
  /*
    NAME              : PR_PRIMA_DE_SERVICIOS_IDIPRON  --> EN ACCESS PRIMA_DE_SERVICIOS  -
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : JUAN DANILO ORDUZ RIVERO
    DATE MIGRATION    : 28/07/2020
    TIME              : 09:11 PM
    SOURCE MODULE     : 
    MODIFIER          : ANDREA CAROLINA PINEDA OVALLE
    DATE MODIFIED     : 05/11/2021
    TIMEUN_           : 
    DESCRIPTION       :
  
  */
AS 
MI_DIAS          NUMBER;
MI_MES         NUMBER;
MI_SALARIO       NUMBER;
MI_EXTRAS      NUMBER;
MI_AUXILIOS      NUMBER;
BAN1                 NUMBER;
BAN                  NUMBER;
MI_PRIMAS            NUMBER;
MI_PSS               NUMBER;
APCESANT       NUMBER;
APMANEJO       NUMBER;
MI_VALOR             NUMBER;
VPC                  NUMBER;
VPR                  NUMBER;
MI_501               NUMBER;
MI_525               NUMBER;
MI_528               NUMBER;
MI_506               NUMBER;
MI_DIASPRIMA         NUMBER;
BEGIN
BAN1:= 0;
BAN := 0;
VPR := 0;
VPC := 0;
PCK_NOMINA.GL_GRPGV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) * PCK_NOMINA.FC_CN(13)/ 100,0);

PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO), 1, 3, PCK_NOMINA.GL_SANO, 5, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
 MI_EXTRAS := ((PCK_NOMINA.FC_SUMACONA(48, 58) + PCK_NOMINA.FC_CNA(63)) + PCK_NOMINA.FC_SUMACON(48, 58) + PCK_NOMINA.FC_CN(63));
 PCK_NOMINA.CN(834) := MI_EXTRAS;
 
           
 IF (PCK_NOMINA.FC_CN(401) <> 0 OR PCK_NOMINA.FC_CN(404) <> 0) AND PCK_NOMINA.GL_SMES <= 6 THEN --' INDICADOR DE LIQUIDAR PRIMA SEMESTRAL
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO -1), 6, 3, PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

        
        IF TO_CHAR(PCK_SYSMAN_UTL.FC_MES('01/01/'||(PCK_NOMINA.GL_SANO))||PCK_SYSMAN_UTL.FC_ANIO('01/01/'||(PCK_NOMINA.GL_SANO))) = TO_CHAR(PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO)||PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO)) THEN 
        MI_DIAS := 0;
        ELSE 
        MI_DIAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS('01/01/'||(PCK_NOMINA.GL_SANO), PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO );
        END IF; --JM 02/05/2024
        MI_DIAS := (MI_DIAS*30)+PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO);
        
        MI_DIAS := MI_DIAS - (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359));
        
    --    IF MI_DIAS >= 90 THEN
            MI_SALARIO := CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
            MI_EXTRAS := ((PCK_NOMINA.FC_SUMACONA(48, 58) + PCK_NOMINA.FC_CNA(63)) + PCK_NOMINA.FC_SUMACON(48, 58) + PCK_NOMINA.FC_CN(63)) / 5;
            MI_AUXILIOS := (PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNV + PCK_NOMINA.GL_GRPGV);
            
            --FACTOR PRIMA DE VACACIONES
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO -1), 12, 3, PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            MI_501 := PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501);
            MI_525 := PCK_NOMINA.FC_CNA(525) + PCK_NOMINA.FC_CN(525);
            
            
      PCK_NOMINA.CN(832) := PCK_SYSMAN_UTL.FC_ROUND((MI_PSS) / 12, 2);
      IF PCK_NOMINA.FC_CN(832) < 0 THEN --'08052020MPV
        PCK_NOMINA.CN(832) := PCK_SYSMAN_UTL.FC_ROUND((MI_525 + PCK_NOMINA.FC_CNA(544) + MI_501) / 12, 0);
      END IF;     
            PCK_NOMINA.CN(928) := (MI_501 + MI_525) / 12;                                                
      
      --FACTOR BASP
      PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO -1), 6, 3, PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
      BAN := PCK_NOMINA.FC_CN(150);
      BAN1 := PCK_NOMINA.FC_CNA(150);
      MI_528 := PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CN(528);
      MI_506 := PCK_NOMINA.FC_CNA(506) + PCK_NOMINA.FC_CN(506);
      PCK_NOMINA.CN(831) := PCK_SYSMAN_UTL.FC_ROUND((BAN1 + BAN - PCK_NOMINA.FC_CNA(695)) / 12, 2);
      PCK_NOMINA.CN(927) := (MI_528 + MI_506) / 12;
      
      
         IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO > TO_DATE('03/08/2022', 'DD/MM/YYYY') THEN 
             PCK_NOMINA.GL_VPA := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) *  PCK_NOMINA.FC_CN(448) /100 , 0);
           ELSE 
               PCK_NOMINA_PROC01.PR_CALCULAR_PRIANTIGUE;
           END IF;  -- 05/2024 TOMAR EN CUENTA A LA HORA DE PASAR EL TICKET 
           
           
            IF PCK_NOMINA.FC_CN(404) <> 0 THEN
           
            BAN1 := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 AND  TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO,'Month') =  TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO,'Month') THEN 0 ELSE PCK_NOMINA.FC_CNA(528) END;
           
            PCK_NOMINA.CN(150) := CASE WHEN PCK_NOMINA.FC_CN(150) <> 0 THEN ((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(506))) ELSE PCK_SYSMAN_UTL.FC_ROUND((BAN1/360*PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO IS NULL THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO END,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO)), 0) END;
           
            BAN := PCK_NOMINA.CN(150);
           
            MI_PSS := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) + 
                                PCK_NOMINA.GL_GRPNV + PCK_NOMINA.GL_GRPGV + (CASE WHEN PCK_NOMINA.FC_CN(85) <> 0 THEN ((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) * PCK_NOMINA.FC_CN(85)/100) ELSE 0 END) + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + 
                                PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN ((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(72) + PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12)
                                ELSE (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(503)) / 12  END , 0) + 
                                PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150) + BAN1 ) /12,0);
           
          
           -- 05/2024 TOMAR EN CUENTA A LA HORA DE PASAR EL TICKET 
            
            ELSE
           
           
            
            MI_PSS := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) + 
                                PCK_NOMINA.GL_GRPNV + PCK_NOMINA.GL_GRPGV + (CASE WHEN PCK_NOMINA.FC_CN(85) <> 0 THEN ((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) * PCK_NOMINA.FC_CN(85)/100) ELSE 0 END) + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + 
                                PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN ((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(72) + PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12)
                                ELSE (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(503)) / 12  END , 0) + 
                                PCK_SYSMAN_UTL.FC_ROUND((BAN + BAN1 ) /12,0);
                                
            END IF;
        
            MI_VALOR := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)END,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO);
            MI_VALOR := (MI_VALOR - PCK_NOMINA.FC_CNA(359));
      MI_PSS := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(MI_PSS, 0) / 30 * 15 / 360 * MI_VALOR, 0);
            MI_VALOR := 0;
            MI_VALOR := PCK_NOMINA.FC_CN(831);  
      

      --CALCULO DE LA PRIMA SEMESTRAL
            MI_PRIMAS := PCK_NOMINA.GL_VPA + VPR + VPC + (CASE WHEN PCK_NOMINA.FC_CN(85) <> 0 THEN ((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) * PCK_NOMINA.FC_CN(85)/100) ELSE 0 END) + (BAN1 + BAN + MI_528 + MI_506 + MI_PSS + MI_501 + MI_525 - PCK_NOMINA.FC_CNA(929)) / 12;
                IF PCK_NOMINA.FC_CN(404) <> 0 THEN 
                    PCK_NOMINA.CN(170) := 0;
                 END IF;
           PCK_NOMINA.CN(160) := PCK_SYSMAN_UTL.FC_ROUND(((MI_SALARIO + MI_AUXILIOS + MI_PRIMAS + MI_EXTRAS  + PCK_NOMINA.GL_VPSEC)/ PCK_NOMINA.FC_CN(67) * 37) / 180 * MI_DIAS, 0) + ROUND(PCK_NOMINA.FC_CN(503), 0);
           
           
            --aqui prima semestral JM 02/05/2024
            IF PCK_NOMINA.FC_CN(401) <> 0 THEN
                  PCK_NOMINA.CN(125) := PCK_NOMINA.FC_CN(125);
            END IF;
      --  END IF;
 END IF;
  PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO -1), 7, 2, PCK_NOMINA.GL_SANO, 6, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

            
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO > TO_DATE('01/04/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
               PCK_NOMINA.CN(160) := 0;
            ELSE
               --PCK_NOMINA.CN(160) := PCK_SYSMAN_UTL.FC_ROUND((MI_SALARIO + MI_AUXILIOS + MI_PRIMAS + MI_EXTRAS + PCK_NOMINA.GL_VPSEC) * PCK_NOMINA.FC_CN(67) / 30 * MI_DIAS / 180, 0) + ROUND(PCK_NOMINA.FC_CN(503), 0);
               PCK_NOMINA.CN(160) := PCK_SYSMAN_UTL.FC_ROUND(((MI_SALARIO + MI_AUXILIOS + MI_PRIMAS + MI_EXTRAS  + PCK_NOMINA.GL_VPSEC)/ PCK_NOMINA.FC_CN(67) * 37) / 180 * MI_DIAS, 0) + ROUND(PCK_NOMINA.FC_CN(503), 0);
           
            END IF;
                  
            IF PCK_NOMINA.FC_CNA(150) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO = 90372 THEN --'CASTRO JOSE VICENTE
                -- AC = ACUM(CSTR(S_ANO - 1), "10", "02", CSTR(S_ANO), "12", "99", PERSONAL![ID_DE_EMPLEADO])
                 PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO -1), 10, 2, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                 IF BAN <> 0 THEN
                     PCK_NOMINA.CNA(150) := 0;
                ELSE
                     BAN1 := PCK_NOMINA.CNA(150);-- 'BXSP ES LA BONIFICACION DIFERENTE A JUNIO DEL AÃ‘O
                     PCK_NOMINA.CNA(150) := 0;
                     MI_PRIMAS := PCK_NOMINA.GL_VPA + VPR + PCK_NOMINA.GL_VPT + VPC + (BAN1 + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.CNA(528) - PCK_NOMINA.CNA(929) + PCK_NOMINA.FC_CN(525) + PCK_NOMINA.CNA(155) + PCK_NOMINA.CNA(525) + PCK_NOMINA.CNA(501)) / 12;
                     PCK_NOMINA.CN(160) := PCK_SYSMAN_UTL.FC_ROUND((MI_SALARIO + MI_AUXILIOS + MI_PRIMAS + MI_EXTRAS + PCK_NOMINA.FC_CN(170) + PCK_NOMINA.GL_VPSEC) * PCK_NOMINA.FC_CN(67) / 30 * MI_DIAS / 180, 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(503), 0);
                END IF;
           END IF;
  
--' **   APORTE MENSUAL A FAVIDI CESANTIAS PRIMA DE JUNIO
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS = 'CES286' AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) THEN --AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO < TO_DATE('01/06/1994', 'DD/MM/YYYY') THEN
           APCESANT := 0;
       APMANEJO := 0;
           IF PCK_NOMINA.FC_CN(160) <> 0 THEN
              PCK_NOMINA.CN(972) := PCK_NOMINA.FC_CN(160);
              PCK_NOMINA.CN(900) := PCK_NOMINA.FC_CN(160);
            END IF;
           IF PCK_NOMINA.FC_CN(158) <> 0 THEN
           PCK_NOMINA.CN(972) := PCK_NOMINA.FC_CN(160);
           END IF;
           PCK_NOMINA.CN(261) := CASE WHEN PCK_NOMINA.FC_CN(261) = 0 THEN 12 ELSE PCK_NOMINA.FC_CN(261)END;    --' PORCENTAJE DE APORTE CESANTIAS
           PCK_NOMINA.CN(265) := CASE WHEN PCK_NOMINA.FC_CN(265) = 0 THEN 12 ELSE PCK_NOMINA.FC_CN(265)END;    --' PORCENTAJE BASE PARA CUOTA DE MANEJO
           PCK_NOMINA.CN(262) := CASE WHEN PCK_NOMINA.FC_CN(262) = 0 THEN 2  ELSE PCK_NOMINA.FC_CN(262)END;     --' PORCENTAJE DE APORTE SOBRE BASE COUTA DE MANEJO
           APCESANT := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(972) * PCK_NOMINA.FC_CN(261) / 100, 0);
           APMANEJO := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(972) * PCK_NOMINA.FC_CN(265) / 100 * PCK_NOMINA.FC_CN(262) / 100, 0);
           PCK_NOMINA.CN(263) := CASE WHEN PCK_NOMINA.FC_CN(263) = 0 THEN APCESANT + APMANEJO ELSE PCK_NOMINA.FC_CN(263)END;
           PCK_NOMINA.CN(440) := APCESANT;
           PCK_NOMINA.CN(441) := APMANEJO;
    END IF;
    
--' **   APORTE MENSUAL A FAVIDI CESANTIAS PRIMA DE NAVIDAD
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS = 'CES286' AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) THEN --AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO < TO_DATE('01/06/1994', 'DD/MM/YYYY') THEN
           APCESANT := 0;
       APMANEJO := 0;
           IF PCK_NOMINA.FC_CN(158) <> 0 THEN
           PCK_NOMINA.CN(972) := PCK_NOMINA.FC_CN(158);
           END IF;
           PCK_NOMINA.CN(261) := CASE WHEN PCK_NOMINA.FC_CN(261) = 0 THEN 12 ELSE PCK_NOMINA.FC_CN(261) END;   --' PORCENTAJE DE APORTE CESANTIAS
           PCK_NOMINA.CN(265) := CASE WHEN PCK_NOMINA.FC_CN(265) = 0 THEN 9  ELSE PCK_NOMINA.FC_CN(265) END;   --' PORCENTAJE BASE PARA CUOTA DE MANEJO
           PCK_NOMINA.CN(262) := CASE WHEN PCK_NOMINA.FC_CN(262) = 0 THEN 2  ELSE PCK_NOMINA.FC_CN(262) END;   --' PORCENTAJE DE APORTE SOBRE BASE COUTA DE MANEJO
           APCESANT := ROUND(PCK_NOMINA.FC_CN(972) * (PCK_NOMINA.FC_CN(261) / 100), 0);                         
           APMANEJO := ROUND(PCK_NOMINA.FC_CN(972) * (PCK_NOMINA.FC_CN(265) / 100) * (PCK_NOMINA.FC_CN(262) / 100), 0);
           PCK_NOMINA.CN(263) := CASE WHEN PCK_NOMINA.FC_CN(263) = 0 THEN APCESANT + APMANEJO ELSE PCK_NOMINA.FC_CN(263)END;
           PCK_NOMINA.CN(440) := APCESANT;
           PCK_NOMINA.CN(441) := APMANEJO;
    END IF;
    MI_VALOR := PCK_NOMINA.FC_CN(160);
    PCK_NOMINA.CN(821) := MI_SALARIO;
    PCK_NOMINA.CN(823) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(824) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(825) := PCK_NOMINA.GL_GRPGV;
    PCK_NOMINA.CN(827) := PCK_NOMINA.GL_VPA;
    PCK_NOMINA.CN(828) := VPC;
    PCK_NOMINA.CN(830) := PCK_NOMINA.GL_VPT;
    PCK_NOMINA.CN(833) := MI_DIAS;--MI_DIASPRIMA; Se cambia de variable ya que se estan presdentado valores incorrectos para este concepto T:7706368
    PCK_NOMINA.CN(87) := PCK_NOMINA.FC_CN(598);
    PCK_NOMINA.CN(813) := PCK_NOMINA.GL_VPSEC;
    
    IF PCK_PARST.FC_PAR('LIQUIDACION PARA IDIPRON', 'NO') = 'SI' AND PCK_NOMINA.GL_SPER = 4 THEN
        PCK_NOMINA.CN(97) := PCK_NOMINA.FC_CN(97) - PCK_NOMINA.FC_CN(2) - PCK_NOMINA.FC_CN(62) - PCK_NOMINA.FC_CN(186) - PCK_NOMINA.FC_CN(79)- PCK_NOMINA.FC_CN(80) - PCK_NOMINA.FC_CN(170) -
                            PCK_NOMINA.FC_CN(150) - PCK_NOMINA.FC_CN(178);
        PCK_NOMINA.CN(2) := 0;
        PCK_NOMINA.CN(62) := 0;
        PCK_NOMINA.CN(186) := 0;
        PCK_NOMINA.CN(79) := 0;
        PCK_NOMINA.CN(80) := 0;
        PCK_NOMINA.CN(170) := 0;
        PCK_NOMINA.CN(150) := 0;
        PCK_NOMINA.CN(178) := 0;
    END IF;
END PR_PRIMA_DE_SERVICIOS_IDIPRON;

PROCEDURE PR_PRIMA_DE_VACACIONES_IDIPRON
  /*
    NAME              : PR_PRIMA_DE_VACACIONES_IDIPRON
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : JUAN DANILO ORDUZ RIVERO
    DATE MIGRATION    : 28/07/2020
    TIME              : 09:11 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIMEUN_           : 
    DESCRIPTION       :
  
  */

AS 
    MI_VBASPPAGADA              NUMBER;
    MI_FECVAC                   DATE;
    MI_FINVAC                   DATE;
    MI_FECHAINI                 DATE;
    MI_FECHAFIN                 DATE;
    MI_FFH                      DATE;
    MI_FECHAFF                  DATE;
    MI_DIAEND                   NUMBER;
    MI_AC                   NUMBER;
    MI_ANTIGD               NUMBER;
    MI_ANTIG                    NUMBER;
    MI_DIASVAC                  NUMBER;
    MI_MESCOM                   NUMBER;
    MI_PERIODOS                 NUMBER;
    MI_DOCEAVAS                 NUMBER;
    MI_DIASPRIMA                NUMBER;
    MI_DC                       NUMBER;
    FACTORESPV                  NUMBER;
    MI_RTA                      NUMBER;
    MI_DTV                      NUMBER;
    FECHAFF1                    DATE;
    FACTORESSV                  NUMBER;
    MI_SVF                      NUMBER;
    VPR                         NUMBER;
    MI_FECHAEND                 VARCHAR2(32) ;
    VBASPPAGADA                 NUMBER;
    PORCENTAJEINCREMENTO        NUMBER;
    PORCENTAJE_GR               NUMBER; 
    PORCENTAJE_PT               NUMBER; 
    PORCENTAJE_PA               NUMBER;
    FECHAC                      DATE;
    MI_FERC           DATE;
    MI_VALOR                    NUMBER;
    MI_DIASBER                  NUMBER; -- JM CC 3961
    
BEGIN
    
    --PCK_NOMINA.CN(92) := 0;
    
    PCK_NOMINA.GL_GRPGV := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) * PCK_NOMINA.FC_CN(13)/ 100,0);
    PCK_NOMINA.GL_VPA   := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) * PCK_NOMINA.FC_CN(448)/ 100,0);
    VPR := 0;
    MI_FECHAEND := CASE WHEN PCK_NOMINA.GL_SPER = 1 THEN '15' ELSE  CASE WHEN PCK_NOMINA.GL_SMES = 2 THEN '28' ELSE '30' END END;
    PCK_NOMINA.CNA(160) :=0;
    IF PCK_NOMINA.GL_SMES >= 6 THEN
        MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, 1, 3, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); 
    ELSE
        MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO-1), 6, 3, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); 
    END IF;
    PCK_NOMINA.CNA(150) := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 AND  TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO,'Month') =  TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO,'Month') THEN 0 ELSE PCK_NOMINA.FC_CNA(528) END;
    /*IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > TO_DATE('01/01/2017', 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO < TO_DATE('31/12/2017', 'DD/MM/YYYY') THEN
        PCK_NOMINA.CNA(150) := 0;
    END IF;*/
    MI_VBASPPAGADA := 0;
    MI_VBASPPAGADA := PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(506);
    PCK_NOMINA.CNA(528) := 0;

    IF PCK_NOMINA.GL_SMES >= 6 THEN
        MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, 1, 3, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); 
    ELSE
        MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO-1), 6, 3, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); 
    END IF;
    
    PCK_NOMINA.CNA(160) := PCK_NOMINA.FC_CNA(160) - PCK_NOMINA.FC_CN(604);
    
    --Para personal que se retira
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ESTADO_ACTUAL <> 3 OR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO >= MI_FECHAINI AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO >= MI_FECHAFIN) THEN 
        MI_ANTIGD := PCK_SYSMAN_UTL.FC_EDAD(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO,'DD/MM/YYYY'),TO_DATE(MI_FECHAEND || '/' || PCK_NOMINA.GL_SMES || '/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'),3); 
        MI_ANTIG := PCK_SYSMAN_UTL.FC_ROUND(MI_ANTIGD/360,2);
    END IF;
    MI_DIASVAC := 0;
    
    --IF PCK_NOMINA.FC_CN(404) <> 0 THEN
            FECHAC := (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL);
            FECHAC := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO;
            MI_VALOR := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CN(357) / 30) + 0.9 , 0);
        IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO) = PCK_NOMINA.GL_SMES AND PCK_NOMINA.FC_CN(404) <> 0 THEN
            MI_MESCOM := 11;
            MI_MESCOM := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(CASE WHEN (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL -2)IS NULL THEN 
                                                                            (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO + 1) ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL END,
                                                                                (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO) - 
                         PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CN(357) / 30) + 0.9 , 0));
        ELSE
            MI_MESCOM := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(CASE WHEN (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL -2)IS NULL THEN 
                                                                            (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO + 1) ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL END,
                                                                               (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO) - 
                         PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CN(357) / 30) + 0.9 , 0));
        END IF;
        MI_PERIODOS := PCK_SYSMAN_UTL.FC_ROUND(MI_MESCOM/11, 0);
        IF MI_MESCOM <> 0 THEN 
            MI_PERIODOS := 1; 
        END IF; 
        MI_DOCEAVAS := MI_MESCOM * MI_PERIODOS;
        IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO) = PCK_NOMINA.GL_SMES AND PCK_NOMINA.FC_CN(404) <> 0 THEN
            MI_DOCEAVAS := 11;
            MI_PERIODOS := 1;
        END IF;
        IF MI_DOCEAVAS > 10 AND MI_DOCEAVAS < 24 THEN 
            MI_PERIODOS := MI_PERIODOS;
    END IF;
            IF MI_PERIODOS > 2 THEN 
                MI_PERIODOS := MI_PERIODOS;
      
                SELECT
                    MAX(VAC.FECHA_FINAL)
                INTO MI_FINVAC
                FROM VACACIONES VAC
                WHERE VAC.COMPANIA = PCK_NOMINA.GL_COMPANIA
                AND VAC.ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;
    
                IF MI_FINVAC > TO_DATE('01/08/2002', 'DD/MM/YYYY') THEN
                    MI_DIASVAC := 15 * MI_PERIODOS;
                    ELSIF MI_ANTIG >= 1 THEN MI_DIASVAC := 15; 
                    ELSIF MI_ANTIG >= 3 THEN MI_DIASVAC := 17;
                    ELSIF MI_ANTIG >= 6 THEN MI_DIASVAC := 18;
                    ELSIF MI_ANTIG >= 10 THEN MI_DIASVAC := 19;
                    ELSIF  MI_ANTIG >= 15 THEN MI_DIASVAC := 20;
                END IF;
            
                MI_DIASPRIMA := PCK_NOMINA.FC_CN(40) * MI_PERIODOS;
                MI_DIASVAC := MI_DIASVAC * MI_PERIODOS;
            ELSE
                MI_DIASVAC := 15 * MI_PERIODOS;
                MI_DIASPRIMA := 15 * MI_PERIODOS;
            
            IF PCK_NOMINA.FC_CN(404) <> 0 THEN
            
               MI_FFH := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, MI_DIASVAC);
            ELSE
                
               MI_FFH := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO + 1), MI_DIASVAC);
            END IF;
            IF PCK_NOMINA.FC_CN(94) = 0 AND MI_DOCEAVAS >= 11 THEN
                PCK_NOMINA.CN(94) := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, MI_FFH, 3);
            END IF;
         END IF;
         MI_DC := PCK_NOMINA.FC_CN(94);
         IF PCK_NOMINA.GL_SPER = 3 THEN
            IF PCK_NOMINA.FC_CN(164) = 0 AND MI_PERIODOS > 0 THEN
                PCK_NOMINA.CN(164) := MI_PERIODOS;
            END IF;
            IF PCK_NOMINA.FC_CN(403) = 0 AND MI_PERIODOS > 0 THEN
                PCK_NOMINA.CN(403) := 1;
            END IF;
            IF NVL(MI_DIASPRIMA,0) = 0  THEN 
                IF PCK_NOMINA.FC_CN(149) = 1 THEN 
                    MI_DIASPRIMA := 15 * PCK_NOMINA.FC_CN(164);
                ELSE
                    MI_DIASPRIMA := 15 * PCK_NOMINA.FC_CN(164);
                END IF;
            END IF;
            IF PCK_NOMINA.FC_CN(149) <> 0 THEN
                MI_DIASPRIMA := PCK_SYSMAN_UTL.FC_ROUND(37 / PCK_NOMINA.FC_CN(47) * PCK_NOMINA.FC_CN(31), 2);
            ELSE
                 PCK_NOMINA.CN(68) := MI_DIASPRIMA;
            END IF;
            IF PCK_NOMINA.FC_CN(404) <> 0 THEN
                 FACTORESPV := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) + 
                                PCK_NOMINA.GL_GRPNV + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + 
                                PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN ((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(72) + PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12)
                                ELSE (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(503)) / 12  END , 0) + 
                                CASE WHEN PCK_NOMINA.FC_CN(150) <> 0 THEN (PCK_NOMINA.FC_CN(150)/ 12) ELSE (MI_VBASPPAGADA / 12) END; -- factores prima de vacaciones
            ELSE
                 FACTORESPV := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) + 
                                PCK_NOMINA.GL_GRPNV + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_VPT + VPR + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + 
                                PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(160) <> 0 THEN PCK_NOMINA.FC_CN(160) / 12 ELSE (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12 END, 0) + 
                                CASE WHEN PCK_NOMINA.FC_CN(150) <> 0 THEN ((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(506)) / 12) ELSE (MI_VBASPPAGADA + PCK_NOMINA.FC_CNA(506)) / 12 END;-- ' factores prima de vacaciones'
            END IF;
            FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(FACTORESPV + 0.049, 0);
            PCK_NOMINA.CN(68) := MI_DIASPRIMA;
        ELSE
            -- Factores para P.V.  REGIMEN NUEVO
            MI_DIASVAC := MI_DC;
            IF MI_DIASPRIMA = 0 THEN
                MI_DIASPRIMA := 15 * PCK_NOMINA.FC_CN(164);
            END IF;
            /*FACTORESPV := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) +
                           PCK_NOMINA.GL_GRPNV + (PCK_NOMINA.GL_GRPGV) + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT +
                           CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(72)) / 12) ELSE (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12 END +
                           CASE WHEN PCK_NOMINA.FC_CN(150) <> 0 THEN ((PCK_NOMINA.FC_CN(150)) / 12) ELSE (MI_VBASPPAGADA + PCK_NOMINA.FC_CNA(506)) / 12 END;-- ' factores prima de vacaciones
           */ --comentado por jm el 05/12/2023 esta duplicando el 160 y el 150 se debe calcular proporcional en caso de ser liquidacion final 
            -- jm 05/12 7738630 INI 
            IF PCK_NOMINA.FC_CN(404) <> 0 THEN
            
            PCK_NOMINA.CN(150) := CASE WHEN PCK_NOMINA.FC_CN(150) <> 0 THEN ((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(506))) ELSE PCK_SYSMAN_UTL.FC_ROUND((MI_VBASPPAGADA/360*PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO IS NULL THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO END,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO)), 0) END;
           
            FACTORESPV := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) + 
                                PCK_NOMINA.GL_GRPNV + PCK_NOMINA.GL_GRPGV + (CASE WHEN PCK_NOMINA.FC_CN(85) <> 0 THEN ((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) * PCK_NOMINA.FC_CN(85)/100) ELSE 0 END) + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + 
                                PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN ((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(72) + PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12)
                                ELSE (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(503)) / 12  END , 0) + 
                                PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150) + MI_VBASPPAGADA ) /12,0);
           
           
           --JM 07/03/2024
            
            ELSE
                 FACTORESPV := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) + 
                                PCK_NOMINA.GL_GRPNV + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_VPT + VPR + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + 
                                PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(160) <> 0 THEN PCK_NOMINA.FC_CN(160) / 12 ELSE (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12 END, 0) + 
                                CASE WHEN PCK_NOMINA.FC_CN(150) <> 0 THEN ((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(506)) / 12) ELSE (MI_VBASPPAGADA + PCK_NOMINA.FC_CNA(506)) / 12 END;-- ' factores prima de vacaciones'
            END IF;

            
            -- jm 05/12 7738630 FIN 
            FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(FACTORESPV, 0);
            IF PCK_NOMINA.FC_CN(31) <> 0 THEN
               MI_DIASPRIMA := PCK_SYSMAN_UTL.FC_ROUND(37 / PCK_NOMINA.FC_CN(47) * PCK_NOMINA.FC_CN(31), 2);
            ELSE
               PCK_NOMINA.CN(68) := MI_DIASPRIMA;
            END IF;
        END IF;
        IF MI_DC = 0 THEN
            MI_RTA := 7;
            MI_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)END,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO);
            MI_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * (CASE WHEN MI_PERIODOS = 0 THEN 1 ELSE MI_PERIODOS END) / 360 * MI_DTV, 0);
            IF MI_RTA = 6 THEN
                MI_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, MI_DIASVAC);      
                PCK_NOMINA.CN(96) := (TO_DATE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY') - PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, MI_DIASVAC)); --'+ 1
            ELSE
                FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, MI_DIASVAC);
                MI_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, MI_DIASVAC);
                IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO <> NULL THEN
                PCK_NOMINA.CN(96) := ((TO_DATE(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY') - 1) - FECHAFF1); -- '16102013
                END IF;
                IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO = FECHAFF1 THEN
                 MI_FERC :=PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO;
                    PCK_NOMINA.CN(96) := MI_FERC - FECHAFF1;
                END IF;
                IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO = NULL THEN-- '31012005
                    PCK_NOMINA.CN(96) := (MI_FECHAFIN - MI_FECHAFF);----REVISAR ESTO FECHAFIN
                END IF;
            END IF;
            IF PCK_SYSMAN_UTL.FC_DIA(MI_FECHAFIN) = 31 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO <= TO_DATE('1994/06/01','DD/MM/YYYY') THEN
                PCK_NOMINA.CN(96) := (TO_DATE(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY') - MI_FECHAFF) + 1;
            END IF;
            MI_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)END,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO);
            MI_DTV := (MI_DTV - PCK_NOMINA.FC_CNA(359));
            MI_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * (CASE WHEN MI_PERIODOS = 0 THEN 1 ELSE MI_PERIODOS END) / 360 * MI_DTV + 0.0049, 2);
            MI_FFH := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, MI_DIASVAC);
            IF PCK_NOMINA.FC_CN(94) = 0 THEN
                PCK_NOMINA.CN(94) := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO + 1, MI_FFH, 3);
                MI_DC := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO, MI_FFH, 3); -- '16082013
            END IF;
        END IF;
        IF PCK_NOMINA.FC_CN(403) <> 0 THEN --'Salario de Vacaciones y prima de vaciones
            FACTORESSV := FACTORESPV;  --' - auxt - auxa  ' factores salario vacaciones
            IF PCK_NOMINA.FC_CN(404) <> 0 THEN            
                MI_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)END,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO);
                MI_DTV := (MI_DTV - PCK_NOMINA.FC_CNA(359));
                PCK_NOMINA.CN(155) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(FACTORESPV, 0) / 30 * 15 / 360 * MI_DTV, 0); --' Prima de vacaciones
                IF PCK_NOMINA.FC_CN(96) = 1 OR MI_DIASVAC >= 1 THEN --jm 05/12 7738630   
                PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(FACTORESSV * ROUND((MI_DTV * MI_DC / 360), 2) / 30, 0);
                ELSE
                    PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(FACTORESSV * PCK_NOMINA.FC_CN(96) / 30, 0);
                END IF;
            ELSE
                PCK_NOMINA.CN(155) := PCK_SYSMAN_UTL.FC_ROUND(FACTORESPV * MI_DIASPRIMA / 30, 0); --' Prima de vacaciones
                PCK_NOMINA.CN(174) := PCK_SYSMAN_UTL.FC_ROUND(FACTORESPV * MI_DC / 30, 0);  --' Salario de vacaciones
            END IF;
        END IF;
        IF PCK_NOMINA.FC_CN(419) <> 0 THEN --'And doceavas >= 11 Then 'Vacaciones en dinero
            FACTORESSV := FACTORESPV;  --' factores salario vacaciones
            
            
            
            IF PCK_NOMINA.FC_CN(404) <> 0 THEN
                MI_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)END,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO);
                MI_DTV := (MI_DTV - PCK_NOMINA.FC_CNA(359));
                IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO = '92285' THEN
                    MI_DTV := 360;
                END IF;
                
                MI_DIASVAC := 15 * (CASE WHEN MI_PERIODOS = 0 THEN 1 ELSE MI_PERIODOS END) / 360 * MI_DTV;
        
                PCK_NOMINA.CN(155) := PCK_SYSMAN_UTL.FC_ROUND(FACTORESSV * PCK_SYSMAN_UTL.FC_ROUND((MI_DTV * 15 / 360), 2) / 30, 0); -- JM 07/03/2024
                IF PCK_NOMINA.FC_CN(96) = 1 OR MI_DIASVAC >= 1 THEN
                
                IF  mod(MI_DIASVAC, 1)  > 0 THEN 
                 MI_DC := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO+1,(PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO+1),MI_DIASVAC)-1),3); -- JM 07/03/2024
                 MI_DC := MI_DC + mod(MI_DIASVAC, 1);
                ELSE 
                  MI_DC := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO+1,PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO+1),MI_DIASVAC),3); -- JM 07/03/2024
                END IF; --JM 24/04/2024
                  
                  
                  PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(FACTORESSV *  MI_DC / 30, 0);
                  
                ELSE
                    PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(FACTORESSV * PCK_NOMINA.FC_CN(96) / 30, 0);  --' Salario de vacaciones
                END IF;
            ELSE
                PCK_NOMINA.CN(155) := PCK_SYSMAN_UTL.FC_ROUND(FACTORESPV * (CASE WHEN MI_DIASPRIMA <> 0 THEN MI_DIASPRIMA ELSE 15 END) / 30, 0); --' Prima de vacaciones
                IF PCK_NOMINA.FC_CN(175) = 0 THEN
                    PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0);  --' vacaciones se pagan en dinero
                END IF;
            END IF;
               PCK_NOMINA.CN(174) := 0;
        END IF;
        --'Para retroactivos de vacaciones y salario de vacaciones que se pagaron con sueldo anterior
        IF PCK_NOMINA.FC_CN(47) <> 0 THEN --'Dias R.T de salario de vaciones
            MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO-1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); --   'Acumulado del Ãºltimo ano
            MI_SVF := PCK_SYSMAN_UTL.FC_ROUND(FACTORESSV * PCK_NOMINA.FC_CN(47) / 30, 0); --' Salario de vacaciones Ficticio
            IF PCK_NOMINA.FC_CNA(94) <> 0 THEN
                PCK_NOMINA.CN(176) := PCK_SYSMAN_UTL.FC_ROUND(MI_SVF - PCK_NOMINA.FC_CNA(174) * PCK_NOMINA.FC_CN(47) / PCK_NOMINA.FC_CNA(94), 0); -- 'R.T.Salario de vacaciones
            END IF;
        END IF;
        IF PCK_NOMINA.FC_CNA(163) <> 0 AND PCK_NOMINA.FC_CN(163) <> 0 THEN
            PCK_NOMINA.CN(174) := PCK_NOMINA.FC_CN(174) - PCK_NOMINA.FC_CNA(174); --' descuenta las vacaciones pagadas inicialmente
            PCK_NOMINA.CN(155) := PCK_NOMINA.FC_CN(155) - PCK_NOMINA.FC_CNA(155); --' descuenta la prima de vacaciones pagada inicialmente
            PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(174) END;
            PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(155) END;
        END IF;
        IF PCK_NOMINA.FC_CN(413) <> 0 THEN
            IF PCK_NOMINA.FC_CN(175) <> 0 THEN
                IF PCK_NOMINA.FC_CN(175) < PCK_NOMINA.FC_CN(620) THEN 
                    PCK_NOMINA.CN(175) := PCK_NOMINA.FC_CN(620); 
                END IF;
            ELSIF PCK_NOMINA.FC_CN(174) < PCK_NOMINA.FC_CN(620) THEN 
                PCK_NOMINA.CN(174) := PCK_NOMINA.FC_CN(620);
            END IF;
            IF PCK_NOMINA.FC_CN(155) < PCK_NOMINA.FC_CN(637) THEN 
                PCK_NOMINA.CN(155) := PCK_NOMINA.FC_CN(637);
      END IF; 
         END IF;
        -- ' Aqui se Reliquidan y se pagan dias trabajados en vacaciones que no se pagaron en el momento
         IF PCK_NOMINA.FC_CN(103) = 0 AND PCK_NOMINA.FC_CN(423) <> 0 THEN
            PCK_NOMINA.CN(103) := PCK_SYSMAN_UTL.FC_ROUND(((CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) +
                                    PCK_NOMINA.GL_GRPNV + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_VPT + VPR + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_VPS + 
                                    PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT) / 30 * MI_DC, 0); -- ' Factores para reliquidacion de sueldo vacaciones trabajadas
         END IF;
        -- ' Aca se liquida en dinero un retroactivo de vacaciones trabajadas
         IF PCK_NOMINA.FC_CN(173) = 0 AND PCK_NOMINA.FC_CN(426) <> 0 THEN
            PCK_NOMINA.CN(173) := 0; --'Round(FactoresSV * dc / 30, 0)
         END IF;
        IF PCK_NOMINA.FC_CN(164) <> 0 THEN
          MI_DIASBER := NVL(PCK_SYSMAN_UTL.FC_PAR(PCK_NOMINA.GL_COMPANIA,'NUMERO DE DIAS PARA BONIFICACION DE RECREACION',6,SYSDATE),2);  -- JM CC 3961
            IF PCK_NOMINA.FC_CN(404) <> 0 THEN
            -- (28/02/2022:jorduz) se  realiza validaci¿n al calculo para incluir tambi¿n a los funcionarios que se encuentran en encargo Ticket 7705681 
                PCK_NOMINA.CN(151) := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END / 30 * MI_DIASBER / 360 * MI_DTV), 0) * NVL(CASE WHEN PCK_NOMINA.FC_CN(164) = 0 THEN  1 ELSE PCK_NOMINA.FC_CN(164) END, 1);
            ELSE
                PCK_NOMINA.CN(151) := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END / 30 * MI_DIASBER), 0) * PCK_NOMINA.FC_CN(164);
            END IF;
        END IF;
       
        IF PCK_NOMINA.FC_CN(407) <> 0 THEN
            PCK_NOMINA.CN(174) := 0;
        END IF;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO = MI_FECHAFIN THEN
            PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(FACTORESSV * PCK_NOMINA.FC_CN(94), 0);
            PCK_NOMINA.CN(174) := 0;
        END IF;
        IF PCK_NOMINA.FC_CN(406) <> 0 THEN --'PARA CUANDO NO HAY PRESUPUESTO PARA CANCELAR LAS VACACIONES EN DINERO
            PCK_NOMINA.CN(175) := 0;
        END IF;
        --'  GUARDANDO LOS FACTORES DE VACACIONES
        --JM 24/04/2024
        PCK_NOMINA.CN(801) := PCK_NOMINA.GL_GRPNV;
        PCK_NOMINA.CN(802) := PCK_NOMINA.GL_GRPGV; 
        PCK_NOMINA.CN(803) := (CASE WHEN PCK_NOMINA.FC_CN(85) <> 0 THEN ((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) * PCK_NOMINA.FC_CN(85)/100) ELSE 0 END);--PCK_NOMINA.GL_VPT;
        PCK_NOMINA.CN(804) := VPR;
        PCK_NOMINA.CN(805) := PCK_NOMINA.GL_VPA;
        PCK_NOMINA.CN(807) := PCK_NOMINA.GL_AUXT;
        PCK_NOMINA.CN(808) := PCK_NOMINA.GL_AUXA;
        PCK_NOMINA.CN(809) := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN ((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(72) + PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12)
                                ELSE (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(503)) / 12  END , 0);--PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(160) <> 0 THEN PCK_NOMINA.FC_CN(160) ELSE PCK_NOMINA.FC_CNA(160) + /*PCK_NOMINA.FC_CNA(72) +*/ PCK_NOMINA.FC_CNA(503) END) / 12, 0);-- '(cn(160) + Cna(160)) / 12
        PCK_NOMINA.CN(810) := CASE WHEN PCK_NOMINA.FC_CN(11) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1)END;
        --JM INI  24/04/2024
        PCK_NOMINA.CN(811) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150) + MI_VBASPPAGADA ) /12,0); --PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(150) <> 0 THEN ((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(506))) ELSE PCK_SYSMAN_UTL.FC_ROUND((MI_VBASPPAGADA/360*PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO IS NULL THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO END,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO)), 0) END) / 12, 0);-- '(cn(150) + Cna(150)) / 12
        --JM FIN  24/04/2024
        PCK_NOMINA.CN(812) := 0; --' IIf((personal!Id_de_Empleado = "90099" Or "00110" Or "90342"), Round((Cna(180) + Cna(60)) / 12, 0), 0)
        PCK_NOMINA.CN(813) := 0; --' IIf((personal!Id_de_Empleado = "90376"), 0, VPC)
        --JM INI  24/04/2024
        IF PCK_NOMINA.FC_CN(404) <> 0 THEN
        MI_ANTIGD := PCK_SYSMAN_UTL.FC_EDAD(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO,'DD/MM/YYYY'),TO_DATE(MI_FECHAEND || '/' || PCK_NOMINA.GL_SMES || '/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'),3); 
        MI_ANTIG := PCK_SYSMAN_UTL.FC_ROUND(MI_ANTIGD/360,2);
        MI_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 * (CASE WHEN MI_PERIODOS = 0 THEN 1 ELSE MI_PERIODOS END) / 360 * MI_DTV + 0.0049, 2);
        --MI_FFH := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO+1, MI_DIASVAC);
                IF  mod(MI_DIASVAC, 1)  > 0 THEN 
                 MI_DC := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO+1,(PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO+1),MI_DIASVAC)-1),3); -- JM 07/03/2024
                 MI_DC := MI_DC + mod(MI_DIASVAC, 1);
                ELSE 
                  MI_DC := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO+1,PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA,(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO+1),MI_DIASVAC),3); -- JM 07/03/2024
                END IF;
                
        
        PCK_NOMINA.CN(162) := PCK_SYSMAN_UTL.FC_ROUND(MI_DTV * 15 / 360,2);
        PCK_NOMINA.CN(166) := MI_DTV;
        PCK_NOMINA.CN(94) := MI_DC; --PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO + 1, MI_FFH, 3);
        PCK_NOMINA.CN(92) := PCK_SYSMAN_UTL.FC_ROUND(TO_NUMBER(PCK_PARST.FC_PAR('NUMERO DE DIAS PARA BONIFICACION DE RECREACION', '0')) / 360 * (MI_DTV - PCK_NOMINA.FC_CN(963)),2); --PCK_SYSMAN_UTL.FC_ROUND('1.555',2); --'0.555'; --
        IF PCK_NOMINA.FC_CN(92) <> 0 AND PCK_NOMINA.FC_CN(92) < 1 THEN 
            PCK_NOMINA.PR_INCLUIRHISTORICOF (PCK_NOMINA.GL_COMPANIA,1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 7, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 92, PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(92), 2),NULL,'',NULL,PCK_CONEXION.FC_GETUSER);
        END IF;
        PCK_NOMINA.CN(873) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(810)/30*PCK_NOMINA.FC_CN(92)),2);
        END IF;
        --JM FIN  24/04/2024
        PCK_NOMINA.CN(814) := CASE WHEN MI_ANTIG = 0 THEN 1 ELSE MI_ANTIG END;
        --PCK_NOMINA.CN(873) := PCK_NOMINA.FC_CN(151);
        PCK_NOMINA.CN(985) := PCK_SYSMAN_UTL.FC_ROUND(FACTORESPV, 0);
        --'  FIN DE FACTORES DE VACACIONES
        --'11042019
        --'FACTORESPV = IIf(cn(11) <> 0, cn(10), cn(1)) + grpnv + (grpgv) + vpt + VPA + AUXT + AUXA + IIf(cn(160) = 0, ((Cna(160) + cn(160) + Cna(72)) / 12), (Cna(160) + Cna(72)) / 12) + IIf(cn(150) <> 0, ((cn(150)) / 12), (VBASPPAGADA) / 12) ' factores prima de vacaciones
        PCK_NOMINA.GL_FV_SBM := CASE WHEN  PCK_NOMINA.FC_CN(11) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE  PCK_NOMINA.FC_CN(1)END;
        PCK_NOMINA.GL_FV_ISPA := 0;
        PCK_NOMINA.GL_FV_GR := PCK_NOMINA.GL_GRPNV + (PCK_NOMINA.GL_GRPGV);
        PCK_NOMINA.GL_FV_PT := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_VPT, 0);
        PCK_NOMINA.GL_FV_PA := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_VPA, 0);
        PCK_NOMINA.GL_FV_AT := PCK_NOMINA.GL_AUXT;
        PCK_NOMINA.GL_FV_SA := PCK_NOMINA.GL_AUXA;
        PCK_NOMINA.GL_FV_BASP := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(150) <> 0 THEN  ((PCK_NOMINA.FC_CN(150)) / 12) ELSE (MI_VBASPPAGADA) / 12 END, 0);
        PCK_NOMINA.GL_FV_PS := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN  ((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503)) / 12) ELSE (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12 END, 0);
        PCK_NOMINA.GL_FV_PV := 0; 
        PCK_NOMINA.GL_FV_PN := 0; 
        PCK_NOMINA.GL_FV_HE := 0; 
        PCK_NOMINA.GL_FV_QUI := 0;
        PCK_NOMINA.GL_FV_BASE := FACTORESPV; 
        PCK_NOMINA.GL_FV_DIAS := PCK_NOMINA.FC_CN(94);
        PCK_NOMINA.GL_FV_DIASD := PCK_NOMINA.FC_CN(96); 
        PCK_NOMINA.GL_FV_VVAC := PCK_NOMINA.FC_CN(174); 
        PCK_NOMINA.GL_FV_VVACD := PCK_NOMINA.FC_CN(175);
        PCK_NOMINA.GL_FV_VPV := PCK_NOMINA.FC_CN(155);
        PCK_NOMINA.GL_FV_VBER := PCK_NOMINA.FC_CN(151); 
        PCK_NOMINA.GL_FV_VPA := PCK_NOMINA.GL_VPA;
        PORCENTAJEINCREMENTO := PCK_NOMINA.CCATEGORIA(1).VLR_INCREMENTO;
        PORCENTAJE_GR := 0; 
        PORCENTAJE_PT := 0; 
        PORCENTAJE_PA := 0;
       
END PR_PRIMA_DE_VACACIONES_IDIPRON;

PROCEDURE PR_CALCULAR_CESANTIAS_IDIPRON 
  /*
    NAME              : PR_CALCULAR_CESANTIAS_IDIPRON  
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : JUAN DANILO ORDUZ RIVERO
    DATE MIGRATION    : 28/07/2020
    TIME              : 09:11 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIMEUN_           : 
    DESCRIPTION       :
  
  */
AS

    VALORULTIMABONIFICACIONN   NUMBER;
    ANTICIPOSCES               NUMBER;
    BASCES                     NUMBER;
    T_HE                       NUMBER;
    DIAS                       NUMBER;
    PRIMAS                     NUMBER;
    FINICIAL                   DATE;
    FECHAFIN                   DATE;
    FECHAPAGO                  DATE;
    FFINAL                     DATE;
    MI_AC                      NUMBER;
    S_ANO1                     NUMBER;
    S_MES1                     NUMBER;
    SANO1                      NUMBER;
    SMES1                      NUMBER;
    VPR                        NUMBER;
    VPC                        NUMBER;
    LICCESAN                   NUMBER;
    DIASCES                    NUMBER;
    DIASTOTAL                  NUMBER;
    CESANTIAS1                 NUMBER;
    BASE                       NUMBER;
    ENTREGADAS                 NUMBER;
    CONSOLID                   NUMBER;
    PSS                        NUMBER;
    VBASPPAGADA                NUMBER;
    MI_VALOR                NUMBER;
    
    
BEGIN
    PCK_NOMINA.GL_GRPGV := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) * PCK_NOMINA.FC_CN(13)/ 100,0);
    VALORULTIMABONIFICACIONN := 0;
    PCK_NOMINA.CNA(150) := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

    VALORULTIMABONIFICACIONN := PCK_NOMINA.CNA(150);
    BASCES  := 0;
    T_HE  := 0;
    DIAS  := 0;
    PRIMAS  := 0;
    VPR   := 0;
    VPC   := 0;
    FECHAPAGO := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
    VBASPPAGADA := PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(506);
    --'14022019 SE CAMNIO ULTIMO AÃ‘O
    IF PCK_NOMINA.GL_SMES = 12 THEN --'14022020
        MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); -- ACUMULADO ULTIMO AÃ‘O
    ELSE
        MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO -1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); --ACUMULADO ULTIMO AÃ‘O
    END IF;
    IF PCK_SYSMAN_UTL.FC_MES(FECHAFIN) = 1 THEN --PILAS REVISAR DE DONDE VIENE ESE FECHAFIN
       S_ANO1 := PCK_NOMINA.GL_SANO - 1;
    ELSE
       S_ANO1 := PCK_NOMINA.GL_SANO;
    END IF;
    IF PCK_NOMINA.FC_CN(404) = 0 THEN
       S_MES1 := 12;
    ELSE
       S_MES1 := PCK_NOMINA.GL_SMES;
       IF PCK_NOMINA.FC_CN(9) = 0 THEN
          BASCES := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_GRPNV + VPR + VPC + PCK_NOMINA.FC_CN(170) + PCK_NOMINA.GL_VPS + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT;
       ELSE
          IF PCK_NOMINA.FC_CN(404) <> 0 THEN
            BASCES := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_GRPNV + (PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(183) / PCK_NOMINA.FC_CN(9) * 30), 0) + PCK_NOMINA.FC_CN(500)) + VPR + VPC + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT;
          ELSE
            BASCES := PCK_NOMINA.FC_CN(1) + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_GRPNV + (PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(183) / PCK_NOMINA.FC_CN(9) * 30), 0) + PCK_NOMINA.FC_CN(500)) + VPR + VPC + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT;
          END IF;
       END IF;
       
       BASCES := PCK_SYSMAN_UTL.FC_ROUND(BASCES, 0);
       DIAS := PCK_NOMINA.FC_CN(9); --'- 1
       PRIMAS := PCK_NOMINA.FC_SUMACON(48, 59) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(525) + PCK_NOMINA.FC_CN(176) + (CASE WHEN VALORULTIMABONIFICACIONN <> 0 AND (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(506)) = 0 THEN  VALORULTIMABONIFICACIONN ELSE (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(506))END) + PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(501);
    END IF;

             IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO > TO_DATE('01/06/1994', 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS = 'CES286' THEN  --'MODIFICADO ENERO 13/2000 ESPERANZA
                  SANO1 := TO_NUMBER(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO, 'YYYY'));
                  SMES1 := TO_NUMBER(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO, 'MM')); --'"01"
                  MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, SANO1, SMES1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); --ACUMULADO DESDE LA FECHA_DE_INGRESO
                  --'DD = DIASPER - CN(427)
                  IF PCK_NOMINA.FC_CN(404) = 0 THEN
                     DIAS := DIAS + PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO, FECHAPAGO, 3); ---REVISAR ESE FECHAPAGO
                  ELSE
                     DIAS := PCK_SYSMAN_UTL.FC_EDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO, 3); --'FECHA_DE_RETIRO 201212
                  END IF;
                   IF (PCK_NOMINA.GL_SMES = 1 OR PCK_NOMINA.GL_SMES = 3 OR PCK_NOMINA.GL_SMES = 5 OR PCK_NOMINA.GL_SMES = 7 OR PCK_NOMINA.GL_SMES = 8 OR PCK_NOMINA.GL_SMES = 10 OR PCK_NOMINA.GL_SMES = 12) AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3 OR PCK_NOMINA.GL_SPER = 9) THEN
                   DIAS := DIAS - 1; --' REVIZAR EN MESES DE 31 DIAS
                   END IF;
                  LICCESAN := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359);
                  DIASCES := DIAS;
                  DIASTOTAL := DIAS - PCK_NOMINA.FC_CNA(356) - PCK_NOMINA.FC_CNA(357) - PCK_NOMINA.FC_CNA(359); --' DIAS LABORADOS DESDE EL INICIO DEL SISTEMA
                    IF PCK_NOMINA.FC_CN(404) = 0 THEN
                     MI_AC:= PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, S_ANO1, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); --ACUMULADO DEL ANO ACTUAL
                  ELSE
                     MI_AC:= PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (S_ANO1 -1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); --ACUMULADO DEL ANO ACTUAL

                  END IF;
                  CESANTIAS1 := 0;
                   IF PCK_NOMINA.FC_CN(9) = 0 THEN
                       BASE := (((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1)END) + (PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_GRPNV + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_VPT) + 
                               (PCK_NOMINA.FC_CNA(158) - PCK_NOMINA.FC_CN(616) + PCK_NOMINA.FC_CN(158)) + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503)) + 
                                (PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) - PCK_NOMINA.FC_CNA(695) + PCK_NOMINA.FC_CNA(77) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501)) + 
                                (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CN(506) + PCK_NOMINA.FC_CNA(506))) / 12);
                     --'CESANTIAS1 = (BASCES + IIF(CN(10) <> 0, CN(10), CN(1)) + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + CN(61) + CN(62) + CN(170) + VPC + CN(183) + ((CNA(158) + CN(158) + CNA(160) + CN(160) + CNA(176) + CN(176) + CNA(180) + CN(180) + (CNA(155) - CNA(637)) + (CN(155) - CN(637)) + CNA(150) + CN(150) + PRIMAS) / 12)) * DIAS / 360 'FEBRERO 14
                     CESANTIAS1 := ((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1)END) + (PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_GRPNV + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_VPT) + 
                                   (ROUND(((PCK_NOMINA.FC_CNA(158) - PCK_NOMINA.FC_CN(616) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504)) + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(72)) + 
                                   (PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) - PCK_NOMINA.FC_CNA(695) + PCK_NOMINA.FC_CNA(77) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501)) + 
                                   (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12), 0))) * DIAS / 360;
                     CESANTIAS1 := PCK_SYSMAN_UTL.FC_ROUND((CESANTIAS1), 0);
                     END IF;
                  IF PCK_NOMINA.FC_CN(413) <> 0 THEN
                     --AC = ACUM(S_ANO - 1, "12", "03", S_ANO, "12", "03", PERSONAL![ID_DE_EMPLEADO], "99") --' ACUMULADO DEL ANO ACTUAL
                     MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO-1), 12, 3, PCK_NOMINA.GL_SANO, 12, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    -- 'AC = ACUM(S_ANO - 1, "11", "03", S_ANO, "12", "03", PERSONAL![ID_DE_EMPLEADO], "99") ' ACUMULADO DEL ANO ACTUAL
                     BASE := PCK_SYSMAN_UTL.FC_ROUND(((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1)END) + ((PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_GRPNV + PCK_NOMINA.GL_VPA) + PCK_NOMINA.GL_VPT) + 
                              PCK_NOMINA.CNA(158) - PCK_NOMINA.FC_CN(616) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.CNA(519) + PCK_NOMINA.CNA(504) + PCK_NOMINA.CNA(160) + 
                              PCK_NOMINA.CNA(503) + PCK_NOMINA.CN(160) + PCK_NOMINA.CNA(72) + PCK_NOMINA.CNA(155) - PCK_NOMINA.CNA(695) + PCK_NOMINA.CNA(525) + 
                              PCK_NOMINA.CN(155) + PCK_NOMINA.CNA(501) + PCK_NOMINA.FC_CN(501) +( 
                              CASE WHEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(150) + PCK_NOMINA.CNA(506) + PCK_NOMINA.FC_CN(506), 0) = 0 
                              THEN (VALORULTIMABONIFICACIONN + PCK_NOMINA.CNA(528)) ELSE  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.CNA(506) + PCK_NOMINA.FC_CN(506), 0)END)) / 12, 0);
                     CESANTIAS1 := PCK_SYSMAN_UTL.FC_ROUND((BASE / 360 * DIAS), 0);
                  END IF;
                  
                  IF PCK_NOMINA.FC_CN(404) <> 0 THEN
                     --AC = ACUM(S_ANO - 1, S_MES, "01", S_ANO, S_MES, "03", PERSONAL![ID_DE_EMPLEADO], "99") --' ACUMULADO DEL ANO ACTUAL
                     BASE := PCK_SYSMAN_UTL.FC_ROUND(((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) + ((PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_GRPNV + PCK_NOMINA.GL_VPA) + PCK_NOMINA.GL_VPT) + 
                        ((PCK_NOMINA.FC_CNA(158) - PCK_NOMINA.FC_CN(616) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(519) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CNA(160) + 
                          PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(72) + PCK_NOMINA.FC_CNA(155) - PCK_NOMINA.FC_CNA(695) + PCK_NOMINA.FC_CNA(525) + PCK_NOMINA.FC_CN(155) + 
                          PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(506) + PCK_NOMINA.FC_CN(506)) / 12)), 0);
                     CESANTIAS1 := PCK_SYSMAN_UTL.FC_ROUND((BASE / 360 * DIAS), 0);
                  END IF;
                 
                  CESANTIAS1 := PCK_SYSMAN_UTL.FC_ROUND(CESANTIAS1, 0);
                  ENTREGADAS := PCK_NOMINA.FC_ACUMCESANTIA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO, FECHAPAGO); --REVISAR ESA FECHAPAGO
                  CONSOLID := CESANTIAS1;
                  PCK_NOMINA.CN(177) := CESANTIAS1;
                  PCK_NOMINA.CN(817) := PCK_NOMINA.FC_CN(177); --' SALDO CESANTIAS
                  PCK_NOMINA.CN(816) := PCK_NOMINA.FC_CN(177); --'PROVISION CESANTIAS
                  CESANTIAS1 := CESANTIAS1 - ENTREGADAS;
                  PCK_NOMINA.CN(817) := CESANTIAS1; --'SALDO CESANTIAS
                  PCK_NOMINA.CN(485) := ENTREGADAS; --'CESANTIAS PARCIALES CON RETROACTIVIDAD CANCELADAS
             ELSE
                  PCK_NOMINA.CNA(150) := VALORULTIMABONIFICACIONN; --'14022020 SE QUITO PORQUE DAÃ‘A ELÃ‘ ACUMULADO DE LOS PAGOS  VALORULTIMABONIFICACION(PERSONAL!ID_DE_EMPLEADO)
                  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO >= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                     FINICIAL := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO;
                  ELSE
                     FINICIAL := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
                  END IF;
                  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO <= FECHAPAGO THEN -- REVISAR FECHAPAGO
                     FFINAL := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO;
                  ELSE
                     FFINAL := FECHAPAGO;
                  END IF;
                  
                IF(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO) IS NOT NULL THEN --'AND PERSONAL!ID_DE_EMPLEADO <> "90346" THEN
                  DIAS := PCK_SYSMAN_UTL.FC_EDAD(FINICIAL, FFINAL, 3); --'- 1
                  --'DIAS := DIAS + CNA(9) - CNA(356) - CNA(357) - CNA(359)
                  IF PCK_NOMINA.CN(201) = 0 THEN
                     PCK_NOMINA.CN(201) := 0;--PARAMETRO(20);---REVISAR ESE PARAMETRO
                  END IF;
                  IF PCK_NOMINA.FC_CN(1) < 2 * PCK_NOMINA.FC_CN(201) THEN
                     PCK_NOMINA.GL_AUXT := PCK_NOMINA.FC_CN(81);
                  END IF;
                  IF PCK_NOMINA.FC_CN(1) < PCK_NOMINA.FC_CN(69) THEN
                     PCK_NOMINA.GL_AUXA := PCK_NOMINA.FC_CN(82);
                  END IF;
                   IF (PCK_NOMINA.GL_SMES = 1 OR PCK_NOMINA.GL_SMES = 3 OR PCK_NOMINA.GL_SMES = 5 OR PCK_NOMINA.GL_SMES = 7 OR PCK_NOMINA.GL_SMES = 8 OR PCK_NOMINA.GL_SMES = 10 OR PCK_NOMINA.GL_SMES = 12) AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3 OR PCK_NOMINA.GL_SPER = 9) THEN
                        DIAS := DIAS - 1; --' REVIZAR EN MESES DE 31 DIAS
                   END IF;
                  IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INICIO_DISFRUTE > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                        --AC = ACUM(CSTR(S_ANO - 1), "12", "03", CSTR(S_ANO), "06", "99", PERSONAL![ID_DE_EMPLEADO])
                        PSS := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CNA(525);
                  END IF;
                  DIAS := DIAS;
                  DIASCES := DIAS - PCK_NOMINA.FC_CNA(356) - PCK_NOMINA.FC_CNA(357) - PCK_NOMINA.FC_CNA(359) - (PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357));-- '23052019
                  LICCESAN := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359);
                  BASE := ((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1)END) + (PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_VPT) + 
                            (ROUND(((((PCK_NOMINA.FC_CN(616) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) + 
                            (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160) /*+ PCK_NOMINA.FC_CNA(72)*/) + (PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(525) + 
                             PCK_NOMINA.FC_CNA(155) + PSS + PCK_NOMINA.FC_CN(501)) + (CASE WHEN(PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) - PCK_NOMINA.FC_CNA(695) + PCK_NOMINA.FC_CNA(528)) = 0 
                             THEN VALORULTIMABONIFICACIONN + PCK_NOMINA.FC_CNA(528) ELSE (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CN(150) - PCK_NOMINA.FC_CNA(695) + PCK_NOMINA.FC_CNA(506))END)) / 12)), 0)));
                  BASE := PCK_SYSMAN_UTL.FC_ROUND(BASE, 0);
                  CESANTIAS1 := PCK_SYSMAN_UTL.FC_ROUND((BASE / 360 * DIASCES), 0); --'(IIF(CN(10) <> 0, CN(10), CN(1))) + (PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_VPT),0)
                  IF PCK_NOMINA.FC_CN(182) = 0 THEN
                        PCK_NOMINA.CN(486) := CESANTIAS1;  --'ROUND(CESANTIAS1 * DIASCES / 360, 0) ' CESANTIAS PROPORCIONALES
                        PCK_NOMINA.CN(177) := CESANTIAS1; --'ROUND(CESANTIAS1 * DIAS / 360, 0) - ANTICIPOSCES
                        PCK_NOMINA.CN(817) := PCK_NOMINA.FC_CN(177); -- 'SALDO CESANTIAS
                        PCK_NOMINA.CN(182) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(177) * 12 / 100) * DIASCES) / 360, 0);
                        PCK_NOMINA.CN(816) := PCK_NOMINA.FC_CN(177);-- 'PROVISION CESANTIAS
                        PCK_NOMINA.CN(815) := PCK_NOMINA.FC_CN(169); -- 'PROVISION INTERESES CESANTIAS
                        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS = 'CES289' AND PCK_NOMINA.FC_CN(404) <> 0 THEN --'LIQUIDA DEFINITIVAS F.N.A
                          PCK_NOMINA.CN(191) := PCK_NOMINA.FC_CN(177);
                          PCK_NOMINA.CN(169) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(191) * 12 / 100) * DIASCES) / 360, 0);
                          PCK_NOMINA.CN(177) := 0;
                          PCK_NOMINA.CN(182) := 0;
                          END IF;
                    END IF;
                 ELSE     
                  --AC = ACUM(S_ANO, "01", "03", S_ANO, "12", "03", PERSONAL![ID_DE_EMPLEADO], "99")  '
                  PCK_NOMINA.CNA(150) := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                  IF PCK_NOMINA.FC_CN(150) <> 0 THEN
                     PCK_NOMINA.CNA(150) := 0;
                  END IF;
       
                  DIAS := PCK_SYSMAN_UTL.FC_EDAD(FINICIAL, FFINAL, 3);
                  --'DIAS = DIAS + CNA(9) - CNA(356) - CNA(357) - CNA(359)
                  DIAS := DIAS;
                  DIASCES := DIAS - PCK_NOMINA.FC_CNA(356) - PCK_NOMINA.FC_CNA(357) - PCK_NOMINA.FC_CNA(359) - (PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357)) - PCK_NOMINA.FC_CNA(200); --'23052019
                  LICCESAN := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359);
                  MI_VALOR := PCK_NOMINA.FC_CN(528);
                  MI_VALOR := PCK_NOMINA.FC_CNA(528);
                  BASE := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1)END + ((PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_VPT)) + (PCK_SYSMAN_UTL.FC_ROUND((((PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(158) - PCK_NOMINA.FC_CN(616) - (PCK_NOMINA.FC_CNA(616) * 2) + PCK_NOMINA.FC_CN(504)) + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(72) + PCK_NOMINA.FC_CNA(503)) + (PCK_NOMINA.FC_CNA(155) + (PCK_NOMINA.FC_CNA(525)) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(544) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501)) + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(506) + PCK_NOMINA.FC_CN(506) - PCK_NOMINA.FC_CNA(695) - PCK_NOMINA.FC_CNA(607))) / 12), 0)));
                  CESANTIAS1 := BASE - PCK_NOMINA.FC_CNA(191) - PCK_NOMINA.FC_CNA(178);
             
                  IF PCK_NOMINA.FC_CN(155) <> 0 OR PCK_NOMINA.FC_CNA(528) <> 0 OR PCK_NOMINA.FC_CNA(525) <> 0 OR PCK_NOMINA.FC_CNA(506) <> 0 THEN
                  BASE := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1)END + (PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_VPT) + (PCK_SYSMAN_UTL.FC_ROUND((((PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(158) - PCK_NOMINA.FC_CN(616) - (PCK_NOMINA.FC_CNA(616) * 2) + PCK_NOMINA.FC_CN(504)) + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(72) + PCK_NOMINA.FC_CNA(503)) + ((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(544) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(525)) + (PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501)) + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(506) + PCK_NOMINA.FC_CN(506) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CNA(528)) - PCK_NOMINA.FC_CNA(695))) / 12), 0)));
                  CESANTIAS1 := BASE - PCK_NOMINA.FC_CNA(191) - PCK_NOMINA.FC_CNA(178);
                  END IF;
                 IF PCK_NOMINA.FC_CN(155) = 0 AND PCK_NOMINA.FC_CN(150) <> 0 THEN
                  BASE := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1)END + (PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_VPT) + (ROUND(((((PCK_NOMINA.FC_CN(616) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(72)) + (PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(525) + PSS + PCK_NOMINA.FC_CN(501)) + (CASE WHEN(PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) - PCK_NOMINA.FC_CNA(695) + PCK_NOMINA.FC_CNA(528)) = 0 THEN VALORULTIMABONIFICACIONN + PCK_NOMINA.FC_CNA(528) ELSE (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CN(150) - PCK_NOMINA.FC_CNA(695) + PCK_NOMINA.FC_CNA(506))END)) / 12)), 0)));
                  CESANTIAS1 := BASE - PCK_NOMINA.CNA(191) - PCK_NOMINA.CNA(178);
                 END IF;
         
                   IF PCK_NOMINA.FC_CN(413) = 1 THEN
                       IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO >= TO_DATE('01/06/1994', 'DD/MM/YYYY') OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS <> 'CES286' THEN -- 'MODIFICADO ENERO 13/2000 ESPERANZA
                            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS <> 'CES289' THEN
                            PCK_NOMINA.CN(178) := PCK_SYSMAN_UTL.FC_ROUND(CESANTIAS1 * DIASCES / 360, 0);
                            PCK_NOMINA.CN(817) := PCK_NOMINA.FC_CN(177); -- 'SALDO CESANTIAS
                            PCK_NOMINA.CN(169) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(178) * 12 / 100) * DIASCES) / 360, 0);
                            PCK_NOMINA.CN(182) := PCK_NOMINA.FC_CN(169);
                            PCK_NOMINA.CN(815) := PCK_NOMINA.FC_CN(169); --'PROVISION INTERESES CESANTIAS
                         
                      
                            END IF;
                        END IF;
                    END IF;
             END IF;
       
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS <> 'CES289' AND PCK_NOMINA.FC_CN(404) = 1 THEN --'LIQUIDA DEFINITIVAS FONDO PRIVADO
                    PCK_NOMINA.CN(178) := PCK_NOMINA.FC_CN(177);
                    PCK_NOMINA.CN(177) := 0;
                    PCK_NOMINA.CN(182) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(178) * 12 / 100) * DIASCES) / 360, 0);
                    PCK_NOMINA.CN(177) := 0;
                    PCK_NOMINA.CN(169) := 0;
                    END IF;
             
            END IF;
             
                IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NOT NULL AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO <> 92239 THEN
                IF PCK_NOMINA.FC_CN(413) = 1 THEN   --'liquidar provision cesantias
                 PCK_NOMINA.CN(816) := PCK_NOMINA.FC_CN(177); --'PROVISION CESANTIAS
                 PCK_NOMINA.CN(815) := PCK_NOMINA.FC_CN(169); --'PROVISION INTERESES CESANTIAS
                 PCK_NOMINA.CN(177) := 0;
                 PCK_NOMINA.CN(169) := 0;
                END IF;
           END IF;
                 IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS <> 'CES286' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS <> 'CES289' AND PCK_NOMINA.FC_CN(182) = 0 THEN
                    DIASCES := DIAS - PCK_NOMINA.FC_CNA(356) - PCK_NOMINA.FC_CNA(357) - PCK_NOMINA.FC_CNA(359) - PCK_NOMINA.FC_CN(359) - PCK_NOMINA.FC_CN(356) - PCK_NOMINA.FC_CN(357) - PCK_NOMINA.FC_CNA(200); -- '23052019
                    PCK_NOMINA.CN(169) := PCK_NOMINA.FC_CN(169); --'Round(((cn(817) * 12 / 100) * Dias) / 360, 0)
                    MI_VALOR := PCK_NOMINA.FC_CN(169);
                 END IF;
                 IF PCK_NOMINA.FC_CN(434) = 1 AND PCK_NOMINA.FC_CN(413) = 1 THEN --'Liquidar pago cesantias a fondos
                 IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS <> 'CES286' THEN --'And personal![Fondo_Cesantias] <> 2 And personal!Id_de_Empleado <> "90346" Then
                 --'If personal![Ingreso_Distrito] >= CVDate("1994/06/01") Then     'modificado enero 13/2000 esperanza
                        PCK_NOMINA.CN(177) := PCK_SYSMAN_UTL.FC_ROUND(CESANTIAS1 * DIASCES / 360, 0);
                        PCK_NOMINA.CN(817) := PCK_NOMINA.FC_CN(177); --'SALDO CESANTIAS
                        PCK_NOMINA.CN(169) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(817) * 12 / 100) * DIASCES) / 360, 0);
                   IF (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO) IS NOT NULL  THEN
                 PCK_NOMINA.CN(178) := PCK_NOMINA.FC_CN(177); --'CESANTIAS DEFINITIVAS
                 PCK_NOMINA.CN(177) := 0;
                 PCK_NOMINA.CN(182) := PCK_NOMINA.FC_CN(169); --'INTERESES DE CESANTIAS DEFINITIVAS
                 PCK_NOMINA.CN(169) := 0;
                 PCK_NOMINA.CN(169) := 0;
                END IF;
                 PCK_NOMINA.CN(277) := PCK_NOMINA.FC_CN(177) - PCK_NOMINA.FC_CN(622);
                 PCK_NOMINA.CN(169) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(817) * 12 / 100) * DIASCES) / 360, 0);
                 PCK_NOMINA.CN(177) := 0;
                 PCK_NOMINA.CN(169) := 0;
               END IF;
               END IF;
              IF PCK_NOMINA.FC_CN(434) <> 0 THEN
              PCK_NOMINA.CN(177) := 0;
              PCK_NOMINA.CN(816) := 0; --'PROVISION CESANTIAS
              END IF;
              
              
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS <> 'CES286' AND PCK_NOMINA.FC_CN(404) = 1  AND PCK_NOMINA.FC_CN(403) = 1 AND PCK_NOMINA.FC_CN(413) = 1 AND PCK_NOMINA.FC_CN(419) = 1 THEN 
                  
                  BASCES :=  (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1)END + (PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_VPT) + (PCK_SYSMAN_UTL.FC_ROUND((((PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(158) - PCK_NOMINA.FC_CN(616) - (PCK_NOMINA.FC_CNA(616) * 2) + PCK_NOMINA.FC_CN(504)) + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(72) + PCK_NOMINA.FC_CNA(503)) + ((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(544) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(525)) + (PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501)) + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(506) + PCK_NOMINA.FC_CN(506) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CNA(528)) - PCK_NOMINA.FC_CNA(695))) / 12), 0)));
                  PCK_NOMINA.CN(177) := PCK_SYSMAN_UTL.FC_ROUND(BASCES * DIASCES / 360, 0);
                  PCK_NOMINA.CN(817) := PCK_NOMINA.FC_CN(177); --'SALDO CESANTIAS
                  PCK_NOMINA.CN(169) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(817) * 12 / 100) * DIASCES) / 360, 0);
            END IF;
                 
 -- 'CONSIGNACION CESANTIAS FONDOS Y F.N.A
  IF PCK_NOMINA.FC_CN(434) = 1 THEN
   IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS = 'CES289' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS <> 'CES286' THEN
                    DIASCES := DIAS - PCK_NOMINA.FC_CNA(356) - PCK_NOMINA.FC_CNA(357) - PCK_NOMINA.FC_CNA(359) - PCK_NOMINA.FC_CN(359) - PCK_NOMINA.FC_CN(356) - PCK_NOMINA.FC_CN(357) - PCK_NOMINA.FC_CNA(200); --'23052019
                    MI_VALOR := PCK_NOMINA.FC_CNA(528);
                    MI_VALOR := PCK_NOMINA.FC_CN(528);
                    BASE := ((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1)END) + (PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPGV + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_VPT) + 
                            (PCK_SYSMAN_UTL.FC_ROUND((((PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(158) - PCK_NOMINA.FC_CN(616) - (PCK_NOMINA.FC_CNA(616) * 2) + PCK_NOMINA.FC_CN(504)) + 
                            (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160)) + ((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) + 
                                PCK_NOMINA.FC_CNA(544) + PCK_NOMINA.FC_CNA(525)) + (PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501)) + (CASE WHEN PCK_NOMINA.FC_CN(150) <> 0 THEN PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CN(528) ELSE VBASPPAGADA + PCK_NOMINA.FC_CNA(528)END) + 
                                PCK_NOMINA.FC_CNA(506) + PCK_NOMINA.FC_CN(506)) - PCK_NOMINA.FC_CNA(695) - PCK_NOMINA.FC_CNA(607))) / 12, 0)));
                    PCK_NOMINA.CN(483) := PCK_SYSMAN_UTL.FC_ROUND((BASE / 360 * DIASCES), 0); --'CONSIGNACION CESANTIAS PUBLICO FNA
                    PCK_NOMINA.CN(169) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(483) * 12 / 100) * DIASCES) / 360, 0); --'INTERESES CESANTIAS PUBLICO FNA DICIEMBRE
 END IF;
        --'CONSIGNACION CESANTIAS PRIVADOS
   IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS <> 'CES289' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS <> 'CES286' AND PCK_NOMINA.FC_CN(182) = 0 THEN
                    PCK_NOMINA.CN(277) := PCK_SYSMAN_UTL.FC_ROUND((BASE / 360 * DIASCES), 0);  --'CONSIGNACION CESANTIAS FONDO PRIVADO
                    MI_VALOR := PCK_NOMINA.FC_CN(277);
                    PCK_NOMINA.CN(169) := PCK_SYSMAN_UTL.FC_ROUND((((PCK_NOMINA.FC_CN(277)) * 12 / 100) * (DIASCES)) / 360, 0); --'INTERESES CESANTIAS FONDO PRIVADO DICIEMBRE
                    MI_VALOR := PCK_NOMINA.FC_CN(169);
                    PCK_NOMINA.CN(182) := 0;
                    --PCK_NOMINA.CN(169) := 0;
                    PCK_NOMINA.CN(178) := 0;
  END IF;
 END IF;
 
 --JM 24/04/2024
 
 IF PCK_NOMINA.FC_CN(404) <> 0 THEN 
        --PCK_NOMINA.CN(177) := PCK_SYSMAN_UTL.FC_ROUND(CESANTIAS1, 0);
        PCK_NOMINA.CN(990) := DIASCES;
        PCK_NOMINA.CN(957) := LICCESAN;
        PCK_NOMINA.CN(988) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160)), 0);  --'FACTOR PRIMA SEMESTRAL
        PCK_NOMINA.CN(901) := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1)END); --'FACTOR SUELDO
        PCK_NOMINA.CN(902) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(158) - PCK_NOMINA.FC_CN(616) - (PCK_NOMINA.FC_CNA(616) * 2) + PCK_NOMINA.FC_CN(504) + PCK_NOMINA.FC_CNA(504), 0); --'FACTOR PRIMA DE NAVIDAD
        PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;                           --'FACTOR AUXILIO DE TRANSPORTE
        PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;                           --'FACTOR AUXILIO DE ALIMENTACION
        PCK_NOMINA.CN(905) := 0; --'cn(813)                     'FACTOR PRIMA DE CLIMA
        PCK_NOMINA.CN(906) := PCK_NOMINA.GL_VPA;                        --'FACTOR PRIMA DE ANTIGUEDAD
        PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(525) + PCK_NOMINA.FC_CNA(544) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CN(155) - PCK_NOMINA.FC_CNA(607)), 0); --'FACTOR PRIMA DE VACACIONES
        PCK_NOMINA.CN(908) := PCK_NOMINA.GL_GRPGV;                          --'FACTOR GASTOS DE REPRESENTACION
        PCK_NOMINA.CN(909) := 0; --' Round( Cna(505)+CN (180) , 0)   'FACTOR QUINQUENIO
        PCK_NOMINA.CN(910) := PCK_NOMINA.FC_CN(264);                        --'FACTOR DIAS PARA CALCULO DE CESANTIAS FONCEP
        PCK_NOMINA.CN(911) := ENTREGADAS;                     --'FACTOR ANTICIPOS CESANTIAS
        PCK_NOMINA.CN(912) := LICCESAN;                       --'FACTOR DIAS LICENCIAS
        PCK_NOMINA.CN(913) := (CASE WHEN PCK_NOMINA.FC_CN(85) <> 0 THEN ((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) * PCK_NOMINA.FC_CN(85)/100) ELSE 0 END);
        PCK_NOMINA.CN(914) := PCK_NOMINA.FC_CN(150)+ VBASPPAGADA+PCK_NOMINA.FC_CNA(528);
        PCK_NOMINA.CN(899) := PCK_SYSMAN_UTL.FC_ROUND(BASE, 0);           --'FACTOR BASE DE LIQUIDACION
        PCK_NOMINA.CN(992) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(158) - PCK_NOMINA.FC_CN(616) - (PCK_NOMINA.FC_CNA(616) * 2) + PCK_NOMINA.FC_CN(504)), 0);
        PCK_NOMINA.CN(989) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(899) / 360 * DIASCES), 0);
 END IF;
 --JM FIN 24/04/2024
  
    IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.FC_CN(413) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO <= TO_DATE('01/06/1994', 'DD/MM/YYYY') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS = 'CES286' THEN 
        PCK_NOMINA.CN(177) := PCK_SYSMAN_UTL.FC_ROUND(CESANTIAS1, 0);
        PCK_NOMINA.CN(988) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160)), 0);  --'FACTOR PRIMA SEMESTRAL
        PCK_NOMINA.CN(901) := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1)END); --'FACTOR SUELDO
        PCK_NOMINA.CN(902) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(158) - PCK_NOMINA.FC_CN(616) - (PCK_NOMINA.FC_CNA(616) * 2) + PCK_NOMINA.FC_CN(504) + PCK_NOMINA.FC_CNA(504), 0); --'FACTOR PRIMA DE NAVIDAD
        PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;                           --'FACTOR AUXILIO DE TRANSPORTE
        PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;                           --'FACTOR AUXILIO DE ALIMENTACION
        PCK_NOMINA.CN(905) := 0; --'cn(813)                     'FACTOR PRIMA DE CLIMA
        PCK_NOMINA.CN(906) := PCK_NOMINA.GL_VPA;                        --'FACTOR PRIMA DE ANTIGUEDAD
        PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(525) + PCK_NOMINA.FC_CNA(544) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CN(155) - PCK_NOMINA.FC_CNA(607)), 0); --'FACTOR PRIMA DE VACACIONES
        PCK_NOMINA.CN(908) := PCK_NOMINA.GL_GRPGV;                          --'FACTOR GASTOS DE REPRESENTACION
        PCK_NOMINA.CN(909) := 0; --' Round( Cna(505)+CN (180) , 0)   'FACTOR QUINQUENIO
        PCK_NOMINA.CN(910) := PCK_NOMINA.FC_CN(264);                        --'FACTOR DIAS PARA CALCULO DE CESANTIAS FONCEP
        PCK_NOMINA.CN(911) := ENTREGADAS;                     --'FACTOR ANTICIPOS CESANTIAS
        PCK_NOMINA.CN(912) := LICCESAN;                       --'FACTOR DIAS LICENCIAS
        PCK_NOMINA.CN(913) := (CASE WHEN PCK_NOMINA.FC_CN(85) <> 0 THEN ((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) * PCK_NOMINA.FC_CN(85)/100) ELSE 0 END);
        PCK_NOMINA.CN(914) :=   PCK_NOMINA.FC_CN(150)+ VBASPPAGADA+PCK_NOMINA.FC_CNA(528);
        PCK_NOMINA.CN(899) := PCK_SYSMAN_UTL.FC_ROUND(BASE, 0);           --'FACTOR BASE DE LIQUIDACION
        PCK_NOMINA.CN(992) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(158) - PCK_NOMINA.FC_CN(616) - (PCK_NOMINA.FC_CNA(616) * 2) + PCK_NOMINA.FC_CN(504)), 0);
        PCK_NOMINA.CN(989) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(899) / 360 * DIASCES), 0);
      IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO <> NULL OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS = 'CES286' THEN
              PCK_NOMINA.CN(447) := PCK_NOMINA.FC_CN(177); --'saldo cesnatrias con retroactivodad '201212
              PCK_NOMINA.CN(177) := 0;  --'definir en que concepto guardar las cesantias a consignar
      END IF;
END IF;   
--'GUARDA CONCEPTOS PLANILLA PARA PAGO CESANTIAS FONDOS FNA

IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FONDO_CESANTIAS <> 'CES286' THEN
     PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503)), 0);    --'FACTOR PRIMA SEMESTRAL
     PCK_NOMINA.CN(940) := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1)END); --'FACTOR SUELDO
     IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO = '92248' THEN
     PCK_NOMINA.CN(940) := 2141456;
     END IF;
     PCK_NOMINA.CN(901) := (CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1)END); --'FACTOR SUELDO
     PCK_NOMINA.CN(902) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(158) - PCK_NOMINA.FC_CN(616) - (PCK_NOMINA.FC_CNA(616) * 2) + PCK_NOMINA.FC_CN(504) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504), 0); --'FACTOR PRIMA DE NAVIDAD
     PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;                           --'FACTOR AUXILIO DE TRANSPORTE
     PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;                           --'FACTOR AUXILIO DE ALIMENTACION--(CFBARRERA:CC_1297)
     PCK_NOMINA.CN(908) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) * PCK_NOMINA.FC_CN(13)/ 100,0);
     PCK_NOMINA.CN(906) := PCK_NOMINA.GL_VPA;
     PCK_NOMINA.CN(942) := PCK_NOMINA.GL_AUXT;                           --'FACTOR AUXILIO DE TRANSPORTE
     PCK_NOMINA.CN(943) := PCK_NOMINA.GL_AUXA;                          --'FACTOR AUXILIO DE ALIMENTACION
     PCK_NOMINA.CN(944) := PCK_NOMINA.GL_VPA;                       --'FACTOR PRIMA DE ANTIGUEDAD
     PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(544) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CNA(525) + PCK_NOMINA.FC_CN(155) - PCK_NOMINA.FC_CNA(607)), 0); --'FACTOR PRIMA DE VACACIONES
     PCK_NOMINA.CN(955) := PCK_NOMINA.GL_GRPGV;                          --'FACTOR GASTOS DE REPRESENTACION
     PCK_NOMINA.CN(956) := DIASCES;                       --'FACTOR TOTAL DIAS PARA CALCULO DE CESANTIAS FONDOS Y FNA
     PCK_NOMINA.CN(912) := LICCESAN;
     PCK_NOMINA.CN(913) := (CASE WHEN PCK_NOMINA.FC_CN(85) <> 0 THEN ((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN  PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) * PCK_NOMINA.FC_CN(85)/100) ELSE 0 END);
     PCK_NOMINA.CN(958) := PCK_NOMINA.GL_VPT;                            --'Factor prima tecnica
     PCK_NOMINA.CN(914) := (CASE WHEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(506) + PCK_NOMINA.FC_CN(506), 0) = 0 THEN (VALORULTIMABONIFICACIONN + PCK_NOMINA.FC_CNA(528)) ELSE PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(528) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(506) + PCK_NOMINA.FC_CN(506), 0)END);  --'factor bonificacion anual por ser.v p.
     PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND(BASE, 0);           --'FACTOR BASE DE LIQUIDACION
     PCK_NOMINA.CN(988) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160)), 0);
     PCK_NOMINA.CN(914) := PCK_NOMINA.FC_CN(150)+ VBASPPAGADA+PCK_NOMINA.FC_CNA(528);
     PCK_NOMINA.CN(899) := PCK_SYSMAN_UTL.FC_ROUND(BASE, 0);           --'FACTOR BASE DE LIQUIDACION
  
 
     END IF;

   
  IF PCK_NOMINA.FC_CN(1) <= PCK_NOMINA.FC_CN(67) THEN --' este dato se debe cambiar cada ano
                PCK_NOMINA.CN(823) := PCK_NOMINA.FC_CN(81);
                PCK_NOMINA.CN(824) := PCK_NOMINA.FC_CN(82);
                
    END IF;
  PCK_NOMINA.CN(928) := PCK_NOMINA.FC_CNA(525);
  
END PR_CALCULAR_CESANTIAS_IDIPRON;

PROCEDURE PR_LIQUIDARBONPERMANENCIA
  
  /*
    NAME              : FC_LIQUIDARBONPERMANENCIA  
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRATION  : JUAN DANILO ORDUZ RIVERO
    DATE MIGRATION    : 28/07/2020
    TIME              : 09:11 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIMEUN_           : 
    DESCRIPTION       :
  
  */ 
  AS

    MI_FECVAC              DATE;
    MI_VRDIFERENCIA        NUMBER;
    MI_MM                  NUMBER;
    VRDIFERENCIA           NUMBER;
    FECPERM                DATE;
    ANTIG                  NUMBER;
    CONCEPTO001ACUMULADO   NUMBER;
    TOTAL                  NUMBER;
    MI_AC NUMBER;
    FECHPER DATE;
    TERMINARR BOOLEAN;
    CATEGSALAANT NUMBER;
    MI_VALOR               NUMBER;
    MI_SUELDOENCARGO       ENCARGOS.SUELDOMENSUAL%TYPE;   

BEGIN
    MI_MM := PCK_PARST.FC_PAR('NUMERO MESES SIN INCREMENTO BONIF.PERMANENCIA',2);
 --HASTA QUE MES TENER EN CUENTA LAS VACACIONES CN(35)
     MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO - 1), 1, 3, (PCK_NOMINA.GL_SANO - 1), MI_MM, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

    VRDIFERENCIA := 0;
    IF PCK_NOMINA.FC_CNA(35) <> 0 THEN
        VRDIFERENCIA := 0;
    END IF;
    MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, PCK_NOMINA.GL_SPER, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, PCK_NOMINA.GL_SPER, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

  --ANTIG := PCK_SYSMAN_UTL.FC_ROUND(( PCK_SYSMAN_UTL.FC_EDAD (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).Ingreso_Distrito, TO_DATE((CASE WHEN PCK_NOMINA.GL_SPER = 1 THEN 15 ELSE (CASE WHEN PCK_NOMINA.GL_SMES = 2 THEN 28 ELSE 30 END)END) || '/' || PCK_NOMINA.GL_SMES || '/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 3) / 360), 0);
  
  --TICKET 7717345 ECABRERA: Se cambia el calculo de la antigudad sin redondeo para mas precision
  ANTIG := PCK_SYSMAN_UTL.FC_ROUND(( PCK_SYSMAN_UTL.FC_EDAD (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).Ingreso_Distrito, TO_DATE((CASE WHEN PCK_NOMINA.GL_SPER = 1 THEN 15 ELSE (CASE WHEN PCK_NOMINA.GL_SMES = 2 THEN 28 ELSE 30 END)END) || '/' || PCK_NOMINA.GL_SMES || '/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'))/360), 3);
  -- FIN 7717345
  
  /*IF PCK_SYSMAN_UTL.FC_EDAD (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).Ingreso_Distrito, TO_DATE((CASE WHEN PCK_NOMINA.GL_SPER = 1 THEN 15 ELSE (CASE WHEN PCK_NOMINA.GL_SMES = 2 THEN 28 ELSE 30 END)END) || '/' || PCK_NOMINA.GL_SMES || '/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 3) < 1800 THEN
   
  END IF;*/
 
 FECHPER := TO_DATE('01/01/' || (PCK_NOMINA.GL_SANO - TRUNC(ANTIG)), 'DD/MM/YYYY');

  MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(FECHPER), 1, 3, PCK_SYSMAN_UTL.FC_ANIO(FECHPER), 12, 3, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

--ACUMULAR EL CONCEPTOR 001 SIN TENER EN CUENTA NOMAISN ADICIONALES.
 MI_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 1, (PCK_NOMINA.GL_SANO - 1), 1, PCK_NOMINA.GL_SPER, PCK_NOMINA.GL_SANO, 12, PCK_NOMINA.GL_SPER, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ; 
CONCEPTO001ACUMULADO := 0;
CONCEPTO001ACUMULADO := PCK_NOMINA.FC_CNA(1);

--AC = Acum((PCK_NOMINA.GL_SANO - 1), "01", s_per, (PCK_NOMINA.GL_SANO), "12", s_per, personal![Id_de_Empleado], "01")  --'s_prc
 MI_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_SANO - 1), 1, PCK_NOMINA.GL_SPER, PCK_NOMINA.GL_SANO, 12, PCK_NOMINA.GL_SPER, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
PCK_NOMINA.CNA(202) := PCK_NOMINA.FC_CNA(202);

IF PCK_NOMINA.FC_CN(202) = 0 AND ANTIG >= 5 THEN
  IF PCK_NOMINA.FC_CN(404) <> 0 THEN
     TOTAL := PCK_SYSMAN_UTL.FC_ROUND(((CONCEPTO001ACUMULADO + PCK_NOMINA.FC_CNA(487) + PCK_NOMINA.FC_CNA(508)) * 0.18) - PCK_NOMINA.FC_CNA(202) - PCK_NOMINA.FC_CNA(589) - PCK_NOMINA.FC_CN(589), 0);
     PCK_NOMINA.CN(202) := TOTAL;
  ELSE
      
      TOTAL := PCK_SYSMAN_UTL.FC_ROUND(((CONCEPTO001ACUMULADO + PCK_NOMINA.FC_CNA(487) + PCK_NOMINA.FC_CNA(508) + VRDIFERENCIA) * 0.18) - PCK_NOMINA.FC_CNA(589) - PCK_NOMINA.FC_CN(589), 0);
      
      --TICKET 7717345 ECABRERA: Si a corte del 31 de dic del año pasada existe un encargo, se toma para la liquidacion
      -- de la bonificacion por permanencia
      IF ( PCK_PARST.FC_PAR('LIQUIDACION PARA IDIPRON', 'NO') = 'SI' ) THEN    
          BEGIN
              SELECT  SUELDOMENSUAL
                INTO  MI_SUELDOENCARGO
                FROM  ENCARGOS
               WHERE  COMPANIA = PCK_NOMINA.GL_COMPANIA
                      AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                      AND TO_DATE('31/12/' || (PCK_NOMINA.GL_SANO - 1),'DD/MM/YYYY') BETWEEN FECHAINICIO AND FECHAFINAL
              ;
              PCK_NOMINA.CN(202) := PCK_SYSMAN_UTL.FC_ROUND(((MI_SUELDOENCARGO * 12)*0.18)/5 ,0);
          EXCEPTION WHEN NO_DATA_FOUND THEN
              PCK_NOMINA.CN(202) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(1)*12)*0.18)/5 ,0);
          END;
      ELSE
          PCK_NOMINA.CN(202) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(1)*12)*0.18)/5 ,0);
      END IF;
      -- FIN 7717345
      MI_VALOR :=  PCK_NOMINA.FC_CN(202);
  END IF;
END IF;

PCK_NOMINA.CN(814) := PCK_SYSMAN_UTL.FC_ROUND((ANTIG), 2);

If PCK_NOMINA.FC_CN(1) < CATEGSALAANT THEN  --Categoria!SalarioAnterior Then  ----PILAS
   PCK_NOMINA.cn(471) := PCK_NOMINA.FC_CN(1) * 12;
ELSE
    IF PCK_NOMINA.FC_CN(404) = 0 THEN --'ACTIVOS 19012017
        TOTAL := PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CNA(1) + PCK_NOMINA.FC_CNA(487) + PCK_NOMINA.FC_CNA(508) + VRDIFERENCIA) * 0.18) - PCK_NOMINA.FC_CNA(589) - PCK_NOMINA.FC_CN(589), 0);
        PCK_NOMINA.CN(471) := PCK_NOMINA.FC_CN(1) * 12;
    ELSE --' RETIRADOS
        TOTAL := PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CNA(1) + PCK_NOMINA.FC_CNA(487) + PCK_NOMINA.FC_CNA(508) + VRDIFERENCIA) * 0.18) - PCK_NOMINA.FC_CNA(202) - PCK_NOMINA.FC_CNA(589) - PCK_NOMINA.FC_CN(589), 0);
        PCK_NOMINA.CN(471) := PCK_NOMINA.FC_CN(1) * 12; --'(CATEGORIA!SALARIOANTERIOR * 12)
    END IF;
END IF;

PCK_NOMINA.CN(454) := 0;

IF PCK_NOMINA.FC_CN(814) >= 5 THEN
    --'FOR I = CN(814) TO 6
    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO < TO_DATE('01/01/2002','DD/MM/YYYY') THEN
      PCK_NOMINA.CN(454) := PCK_NOMINA.GL_SANO - 2006;
    ELSE
      PCK_NOMINA.CN(454) := PCK_NOMINA.GL_SANO - TO_NUMBER(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO, 'YYYY')) - 5;
    END IF;
    --'NEXT I
END IF;

    IF PCK_NOMINA.FC_CNA(202) = 0 AND PCK_NOMINA.FC_CN(202) <> 0 THEN
        PCK_NOMINA.CN(454) := 1;

    END IF;
END PR_LIQUIDARBONPERMANENCIA;

PROCEDURE PR_PRIMACOSTOVIDAOBREROS(
    /*
    NAME              : PR_PRIMACOSTOVIDAOBREROS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
    DATE MIGRADOR     : 09/12/2019
    TIME              : 10:11 AM
    SOURCE MODULE     : Nueva TAR1000095516 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Procedimiento cÃ¡lculo prima de costo de vida AlcaldÃ­a de Bucaramanga, compaÃ±Ã­a de obreros.
    */
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_VALORPRIMA        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIASNOTRABAJADOS  NUMBER DEFAULT 0;
    MI_FECHAINICIO       DATE;
    MI_FECHAFIN          DATE;
    MI_FACTORES          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_JORNAL            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIASPRIMA         NUMBER DEFAULT 0;
BEGIN

    BEGIN
        SELECT SALARIO_BASE / 30
        INTO   MI_JORNAL
        FROM   CATEGORIA
        WHERE  COMPANIA        = UN_COMPANIA
          AND  ESCALAFON       = '06'
          AND  ID_DE_CATEGORIA = '001' --TAR1000095516 Se debe tomar el salario base de la categoria 1
          AND  ANO             = PCK_NOMINA.GL_SANO;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
        MI_JORNAL := 0;
    END ;      

  PCK_NOMINA.GL_DCC := 0;
    PCK_NOMINA.GL_DNT := 0; 
  MI_FECHAINICIO := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
  MI_FECHAFIN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');   
  MI_FECHAINICIO := CASE WHEN PCK_NOMINA.GL_FECHAI > MI_FECHAINICIO THEN PCK_NOMINA.GL_FECHAI ELSE MI_FECHAINICIO END;
  MI_FECHAFIN := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO >= MI_FECHAINICIO AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO <= MI_FECHAFIN) THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO ELSE MI_FECHAFIN END;       
    MI_DIASPRIMA := 8;

    FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, i, 1, PCK_NOMINA.GL_SANO, i, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    MI_DIASNOTRABAJADOS := (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
        IF (MI_DIASNOTRABAJADOS) > 0 THEN
            PCK_NOMINA.GL_DNT := PCK_NOMINA.GL_DNT + MI_DIASNOTRABAJADOS;
        END IF;
    END LOOP;   

  PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAINICIO, MI_FECHAFIN) - PCK_NOMINA.GL_DNT; 
  MI_FACTORES := MI_JORNAL * MI_DIASPRIMA;
    MI_VALORPRIMA := PCK_SYSMAN_UTL.FC_ROUND((MI_FACTORES / 180) * PCK_NOMINA.GL_DCC, 0);
    /*INI_CC4399_MPEREZ - Se ajusta para que valide si el 193 tiene datos no lo vuelva a calcular*/
    IF PCK_NOMINA.FC_CN(193) = 0 THEN
      PCK_NOMINA.CN(193) := MI_VALORPRIMA; 
    END IF;
    /*FIN_CC4399_MPEREZ*/

END PR_PRIMACOSTOVIDAOBREROS;

PROCEDURE PR_CALCULARCESANTIASBUCOBREROS(
    /*
    NAME              : PR_CALCULARCESANTIASBUCOBREROS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
    DATE MIGRADOR     : 19/12/2019
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
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


        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA   => UN_COMPANIA, 
                                                   UN_ANO1       => PCK_NOMINA.GL_SANO, 
                                                   UN_MES1       => 1, 
                                                   UN_PERIODO1   => 1, 
                                                   UN_ANO2       => PCK_NOMINA.GL_SANO,
                                                   UN_MES2       => PCK_NOMINA.GL_SMES, 
                                                   UN_PERIODO2   => 99, 
                                                   UN_IDEMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

        PCK_NOMINA.CN(906) := ((PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(160)) / 12);
        PCK_NOMINA.CN(907) := ((PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(155)) / 12);               
        PCK_NOMINA.CN(902) := ((PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CNA(70)) / 12);
        PCK_NOMINA.CN(905) := ((PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(158)) / 12);    

    PCK_NOMINA_COM4.PR_PAGOESPECIALEMPLEADO(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES);
        PCK_NOMINA.CN(903) := PCK_NOMINA.FC_CN(80);
        PCK_NOMINA.CN(904) := PCK_NOMINA.FC_CN(192);
        PCK_NOMINA.CN(941) := PCK_NOMINA.FC_CN(190);
        PCK_NOMINA.CN(943) := PCK_NOMINA.FC_CN(194);
        PCK_NOMINA.CN(956) := PCK_NOMINA.FC_CN(195);

        MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END --Jornal 
            + PCK_NOMINA.FC_CN(903) --Auxilio de transporte
            + PCK_NOMINA.FC_CN(902) --Horas extra 
            + PCK_NOMINA.FC_CN(907) --1/12 Prima vacaciones
            + PCK_NOMINA.FC_CN(906) --1/12 Prima de servicios Junio y diciembre del mismo aÃ±o
            + PCK_NOMINA.FC_CN(905) --1/12 Prima de navidad
            + PCK_NOMINA.FC_CN(941) --Prima climatica
            + PCK_NOMINA.FC_CN(904) --Prima de alimentaciÃ³n
            + PCK_NOMINA.FC_CN(943) --Auxilio de movilizaciÃ³n
            + PCK_NOMINA.FC_CN(956); --Sobresueldo                     

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
            --(APINEDA:22/01/2020)-Se elimina condiciÃ³n con el fin de calcular el concepto 269 tambien para empleados con regimen 2. TAR 1000096943
            PCK_NOMINA.CN(269) := CASE WHEN PCK_NOMINA.FC_CN(269) = 0 
                                       THEN CASE WHEN MI_CESANTIA1 < 0 
                                                 THEN 0 
                                                 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) 
                                            END 
                                       ELSE PCK_NOMINA.FC_CN(269) 
                                  END;
            PCK_NOMINA.CN(277) := CASE WHEN PCK_NOMINA.FC_CN(277) = 0 
                                       THEN MI_CESANTIA1 
                                       ELSE PCK_NOMINA.FC_CN(277) 
                                  END;
            IF PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = '12' THEN
                PCK_NOMINA.PR_INCLUIRNOVEDAD(UN_COMPANIA => UN_COMPANIA, 
                                             UN_PROCESO  => 1, 
                                             UN_ANIO     => (PCK_NOMINA.GL_SANO + 1), 
                                             UN_MES      => 1, 
                                             UN_PERIODO  => 2, 
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


        PCK_NOMINA.CN(910) := MI_DIAS                                                    ;
        PCK_NOMINA.CN(911) := MI_ANTICIPOS                                               ;
        PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS;
        PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0);
        PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN  => PCK_NOMINA.GL_FECHAI, 
                                                                 UN_FECHAFIN => PCK_NOMINA.GL_FECHAFIN1) + 1 ;
    END IF;
END PR_CALCULARCESANTIASBUCOBREROS;

PROCEDURE PR_LIMPIARNOVEDADHISTORICO(
    /*
    NAME              : PR_LIMPIARNOVEDADHISTORICO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
    DATE MIGRADOR     : 16/01/2020
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Elimina registros de la tabla NOVEDADES_HISTORICO al comenzar el proceso de liquidaciÃ³n de nÃ³mina para un rango de empleados.
    @NAME:  PR_LIMPIARNOVEDADHISTORICO
    */
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO  IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_INICIAL  IN VARCHAR2,
    UN_FINAL    IN VARCHAR2,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES      IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI        
)
AS
    MI_CONDICION    VARCHAR2(500);
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
--(APINEDA:19/03/2020)-Se agrega validaciÃ³n para no eliminar historico de novedades cuando se calcule el retroactivo
IF NOT PCK_NOMINA.GL_NOMINARETROACTIVO THEN
    MI_CONDICION := 'COMPANIA       = '''|| UN_COMPANIA ||'''
                AND ID_DE_PROCESO   =   '|| UN_PROCESO ||'
                AND ANOAPLICADO     =   '|| UN_ANO      ||'
                AND MESAPLICADO     =   '|| UN_MES      ||'
                AND PERIODOAPLICADO =   '|| UN_PERIODO  ||'
                AND ID_DE_EMPLEADO BETWEEN '|| TO_NUMBER(UN_INICIAL) || ' AND ' || TO_NUMBER(UN_FINAL);     
    BEGIN
        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'NOVEDADES_HISTORICO', 
                                                   UN_ACCION => 'E', 
                                                   UN_CONDICION => MI_CONDICION);               
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
        MI_MSGERROR(1).CLAVE := 'PERIODO';
        MI_MSGERROR(1).VALOR := UN_ANO || '-' || UN_MES ;
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD =>SQLCODE, UN_ERROR_COD=>PCK_ERRORES.ERR_NOMINA_DELETE_NOVEDADESH, UN_REEMPLAZOS => MI_MSGERROR);
    END;            
END IF;
END PR_LIMPIARNOVEDADHISTORICO;

PROCEDURE PR_REGISTRARNOVEDADHISTORICO(
    /*
    NAME              : PR_REGISTRARNOVEDADHISTORICO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
    DATE MIGRADOR     : 17/01/2020
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Realiza registro de la novedad en la tabla NOVEDADES_HISTORICO por empleado al liquidar la nÃ³mina.
    */
    UN_CONCEPTO         IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
    UN_EMPLEADO         IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_ANOORIGINAL      IN PCK_SUBTIPOS.TI_ANIO,
    UN_MESORIGINAL      IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODOORIGINAL  IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_VALOR            IN PCK_SUBTIPOS.TI_DOBLE,
    UN_OBSERVACIONES    IN VARCHAR2,
    UN_CREATED_BY       IN VARCHAR2,
    UN_MODIFIED_BY      IN VARCHAR2,
    UN_FECHA            IN DATE,
    UN_DATE_CREATED     IN DATE,
    UN_DATE_MODIFIED    IN DATE    
)
AS
   MI_CAMPOS       VARCHAR2(4000);
   MI_VALORES      VARCHAR2(4000);
   MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
--(APINEDA:19/03/2020)-Se agrega validaciÃ³n para no eliminar historico de novedades cuando se calcule el retoactivo
IF NOT PCK_NOMINA.GL_NOMINARETROACTIVO THEN
  BEGIN        
    BEGIN
      MI_CAMPOS := 'COMPANIA, ID_DE_PROCESO, ANOAPLICADO, MESAPLICADO, PERIODOAPLICADO, ID_DE_EMPLEADO, ID_DE_CONCEPTO, EMPLEADO_ORIGINAL, ANO_ORIGINAL, MES_ORIGINAL, PERIODO_ORIGINAL, VALOR, OBSERVACIONES, CREATED_BY, MODIFIED_BY, FECHA, DATE_CREATED, DATE_MODIFIED';
      MI_VALORES := '''' || PCK_NOMINA.GL_COMPANIA || ''', ' || PCK_NOMINA.GL_PROCESOACTUAL || ','
                 || PCK_NOMINA.GL_SANO || ',' || PCK_NOMINA.GL_SMES || ', '
                               || PCK_NOMINA.GL_SPER || ','|| PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO || ', '
                 || UN_CONCEPTO || ','|| UN_EMPLEADO || ', '
                 || UN_ANOORIGINAL || ','|| UN_MESORIGINAL || ', '
                 || UN_PERIODOORIGINAL || ','|| UN_VALOR || ', '''
                 || UN_OBSERVACIONES || ''','''|| UN_CREATED_BY || ''', '''
                 || UN_MODIFIED_BY || ''','''|| UN_FECHA || ''', '''
                 || UN_DATE_CREATED || ''','''|| UN_DATE_MODIFIED || '''';

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'NOVEDADES_HISTORICO',
                       UN_ACCION  => 'I',
                       UN_CAMPOS  => MI_CAMPOS,
                       UN_VALORES => MI_VALORES);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
      RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
        MI_MSGERROR(1).CLAVE := 'EMPLEADO';
        MI_MSGERROR(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO || '- Concepto: ' || UN_CONCEPTO ;    
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                               UN_ERROR_COD   => PCK_ERRORES.ERR_INSERTARNOVEDADHISTORICO,
                               UN_REEMPLAZOS  => MI_MSGERROR);    
    END;  
END IF;    
END PR_REGISTRARNOVEDADHISTORICO;



PROCEDURE PR_CALPRIMANAVIDADALCHONDA (
     /*
    NAME              : PR_CALPRIMANAVIDADALCHONDA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÃ‘AS
    DATE MIGRADOR     : 16/03/2020
    TIME              : 08:00 AM
    SOURCE MODULE     : NOMINAP2020.12.05_UNIFICADAS En access calcularprimadenavidadALCHONDA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  CALCULARPRIMANAVIDADALCHONDA
    */ 

        UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
    ) 
    AS
        MI_FECHAFPN          DATE;
        MI_TRANSPORTELEGAL   PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
        MI_RETEFUENTE        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
        MI_ANOS              PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
        MI_MESCOM            PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
        MI_N1                PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
        MI_N2                PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
        MI_DIASPRIMADIC      NUMBER:= 0;
        MI_DCC1              NUMBER:= 0;
        MI_GL_DNT1           NUMBER:= 0;  
        MI_ULTIMABONPAGADA   NUMBER:= 0; 
        MI_VALORP            NUMBER := 0;

    BEGIN
          PCK_NOMINA.GL_PVAC := 0;        
--        IF NOT ( PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '10' OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '11' ) THEN -- no manejan docentes
            PCK_NOMINA.GL_FACTORPN := 0;
            PCK_NOMINA.GL_DNT:= 0;
            MI_GL_DNT1 := 0;
            PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_FECHAIPN := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN THEN    PCK_NOMINA.GL_FECHAI   ELSE PCK_NOMINA.GL_FECHAIPN END;
            PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            PCK_NOMINA.GL_FECHAIPN1 :=  CASE  WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPN1 THEN    PCK_NOMINA.GL_FECHAI  ELSE PCK_NOMINA.GL_FECHAIPN1 END;

            IF PCK_NOMINA.GL_SMES = 12 AND ( PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3 ) AND PCK_NOMINA.FC_CN(155) = 0 THEN
                PCK_NOMINA.GL_FECHAIPN := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
                PCK_NOMINA.GL_FECHAIPN1 := TO_DATE('01/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
            END IF;
      IF  PCK_NOMINA.GL_SPER = 4  THEN --020117
         PCK_NOMINA.CN(150) := 0;
      END IF ;
------ CASO 1: NORMAL
            IF PCK_NOMINA.FC_CN(404) = 0 THEN --MOD JM CC 3472 se ajusta para que tome tambien a los que son de ingreso reciente 
          --CALCULAR PRIMA EXTRALEGAL CON PRIMA DE NAVIDAD CONCEPTO 159

        IF PCK_NOMINA.GL_SMES  >= 7 THEN  --19112020 ADICION PRIMA DE NAVIDAD
            PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');  

          PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;

                    PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR   PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('31/12' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') END;
          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS),2), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_DNT :=  PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) +  PCK_NOMINA.FC_CN(356) +  PCK_NOMINA.FC_CN(357) +  PCK_NOMINA.FC_CN(359) +   PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
          PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,  PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
          PCK_NOMINA.CN(159) := CASE WHEN PCK_NOMINA.FC_CN(159) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND( CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10)  ELSE PCK_NOMINA.FC_CN(1) END / 180  * PCK_NOMINA.GL_DCC / 30 * 15 ,0) ELSE PCK_NOMINA.FC_CN(159) END  ;
        END IF ;
        MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
                -- Trae acumulados

                --02122019 cn(931) = Round(VALORULTIMAPRIMASEMESTRAL(personal!Id_de_Empleado) / 12 + Round(cn(159) / 12, 0), 0) '27112019
        PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0) / 12; --27112019
        PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) +  PCK_NOMINA.FC_CN(514)) / 12, 0);
        PCK_NOMINA.GL_AC   := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN),2), 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);

        --Factores para el calculo de la prima de navidad
        PCK_NOMINA.GL_PVAC := 0;
        IF PCK_NOMINA.FC_CNA(155) = 0 THEN 
                    PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(FC_VLRULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0);
                ELSE
                    PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND(FC_VLRULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0);
                END IF;

        IF PCK_NOMINA.GL_SMES =  12  AND  PCK_NOMINA.GL_SMES = 3 AND PCK_NOMINA.FC_CN(155) > 0  AND  PCK_NOMINA.GL_PVAC = 0 THEN 
            PCK_NOMINA.GL_PVAC :=  PCK_NOMINA.GL_PVAC + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501);  
        END IF ;

        PCK_NOMINA.CN(942) :=  0; --Round(Cna(70) / 12, 0)

        PCK_NOMINA.CN(932) :=  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12 ,0);
                MI_VALORP :=  PCK_NOMINA.FC_CN(10);
                MI_VALORP :=  PCK_NOMINA.FC_CN(1);
                MI_VALORP :=  PCK_NOMINA.FC_CN(942);
                MI_VALORP :=  PCK_NOMINA.GL_GRPNGV ;
                MI_VALORP :=  PCK_NOMINA.GL_AUXT;
                MI_VALORP :=  PCK_NOMINA.GL_AUXA;
                MI_VALORP :=  PCK_NOMINA.GL_VPT;
                MI_VALORP :=  PCK_NOMINA.FC_CN(931);
                MI_VALORP :=  PCK_NOMINA.GL_PVAC;
                MI_VALORP :=  PCK_NOMINA.FC_CN(939);
        PCK_NOMINA.GL_FACTORPN := CASE WHEN  PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END  + PCK_NOMINA.FC_CN(942)  + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + ( PCK_NOMINA.GL_PVAC / 12 ) + PCK_NOMINA.FC_CN(939);
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);  



                FOR I IN 1..PCK_NOMINA.GL_SMES LOOP
                    PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_STRZERO(I,2), 1, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_STRZERO(I,2), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357)+  PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(359)+ PCK_NOMINA.FC_CN(339) ) > 0 THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                        PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359)  +  PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357)  +  PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)   );
                    END IF;
                END LOOP;

                IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN
                    IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                    END IF;
                END IF;
                MI_DCC1 := 0;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);

                IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
          --If cn(937) > 0 Then DCC = cn(937): DOCEAVAS = Int(cn(937) / 30) 'JUNIO 30/2004
                    IF PCK_NOMINA.FC_CN(158) = 0 THEN
                        MI_VALORP :=  PCK_NOMINA.GL_FACTORPN;
                        MI_VALORP :=  PCK_NOMINA.GL_DCC;
                        PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                    END IF;                 

                ELSE
            IF PCK_NOMINA.FC_CN(937) > 0 THEN
                        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                        PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                    END IF;  --JUNIO 30/2004
                    IF PCK_NOMINA.FC_CN(158) = 0 THEN
                        PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                    END IF;
                END IF;
            ELSIF PCK_NOMINA.FC_CN(404) <> 0 THEN
          --CALCULAR PRIMA EXTRALEGAL CON PRIMA DE NAVIDAD CONCEPTO 159
        IF PCK_NOMINA.GL_SMES  >= 7 THEN  --19112020 ADICION PRIMA DE NAVIDAD
            PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');  
          PCK_NOMINA.GL_FECHAIPS := TO_DATE( CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END , 'DD/MM/YYYY');
                    PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR   PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('31/12' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY' ) END;
          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS),2), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_DNT :=  PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) +  PCK_NOMINA.FC_CN(356) +  PCK_NOMINA.FC_CN(357) +  PCK_NOMINA.FC_CN(359) +   PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
          PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,  PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
          PCK_NOMINA.CN(159) := CASE WHEN PCK_NOMINA.FC_CN(159) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND( CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10)  ELSE PCK_NOMINA.FC_CN(1) END / 180  * PCK_NOMINA.GL_DCC / 30 * 15 ,0) ELSE PCK_NOMINA.FC_CN(159) END  ;
        END IF ;
        --CASO 2: CUANDO SE ORDENAR LIQUIDACION
               --<TAR:10000106504 FECHA24/03/2021 AUTOR:CP>--Se quita formateo de la fecha para cuando esta viene nula 
                --MI_FECHAFPN := TO_DATE(PCK_NOMINA.GL_FECHAR, 'DD/MM/YYYY');
                MI_FECHAFPN := PCK_NOMINA.GL_FECHAR;
                   -- Trae acumulados
                PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO,1,CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN)<16 THEN 1 ELSE 2 END , PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.CN(942) := 0;

        IF PCK_NOMINA.FC_CNA(155) = 0 AND PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) = 0 THEN  -- PARA LOS QUE NO TIENEN PRIMA DE VACACIONES PAGADAS EN EL AÃ‘O
            PCK_NOMINA.GL_PVAC := CASE WHEN  PCK_NOMINA.FC_CN(404) <> 0 THEN  PCK_NOMINA.FC_CN(155) ELSE 0 END ;
        ELSE
            PCK_NOMINA.GL_PVAC :=  PCK_NOMINA.FC_CNA(155)  +  PCK_NOMINA.FC_CNA(501)  + PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + CASE WHEN  PCK_PARST.FC_PAR('USAR ULTIMA PRIMA DE VACACIONES PAGADA EN LIQUIDACION FINAL', ' ') =  'SI' THEN  PCK_NOMINA.FC_CN(155)  ELSE 0 END;  -- 20112019
        END IF ;
        MI_ULTIMABONPAGADA :=  PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_SYSMAN_UTL.FC_ROUND( CASE WHEN  PCK_PARST.FC_PAR('USAR ULTIMA PRIMA DE VACACIONES PAGADA EN LIQUIDACION FINAL', ' ') =  'SI' THEN  PCK_NOMINA.FC_CN(155)  ELSE 0 END ,0) ;  -- 03042019 sanluisgaceno
        --FACTORES PAGADOS EN EL AÃ‘O
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO,1,CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN)<16 THEN 1 ELSE 2 END , PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); --20112019 sanluisgacena
        IF PCK_NOMINA.FC_CNA(160) > 0 THEN 
            PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160)  +  PCK_NOMINA.FC_CNA(503)) / 12,0) + PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN  PCK_PARST.FC_PAR('USAR ULTIMA PRIMA DE VACACIONES PAGADA EN LIQUIDACION FINAL', ' ') =  'SI' THEN  PCK_NOMINA.FC_CN(160) / 12  ELSE 0 END,0); --209112019
        ELSIF PCK_NOMINA.FC_CN(160) > 0 THEN  
           PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160)) / 12,0); --13062018
           --NO SE PUEDE TOMAR LA DEL AÃ‘O ANTERIOR , PORQUE YA FUE FACTOR DE DICIEMBRE, SOLO SE TOMA LA QUE SE VA A PAGAR. cn(931) = Round((VALORULTIMAPRIMASEMESTRAL(personal!Id_de_Empleado) + cn(160)) / 12, 0) ' 13062018 ALCT TOCANCIPA EN VISITA.
        END IF ;
        PCK_NOMINA.CN(932) :=  PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12,0);
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO,1,CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN)<16 THEN 1 ELSE 2 END , PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); --20112019 sanluisgacena
                IF PCK_NOMINA.FC_CN(150) > 0 AND  MI_ULTIMABONPAGADA = 0 THEN 
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) ) / 12,0); 
                ELSIF PCK_NOMINA.FC_CN(150) > 0 THEN 
            PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_PARST.FC_PAR('USAR ULTIMA PRIMA DE VACACIONES PAGADA EN LIQUIDACION FINAL', ' ') =  'SI' THEN PCK_NOMINA.FC_CN(150) ELSE 0 END  ,0) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CNA(514)) /12,0);  -- 05052016
        ELSIF PCK_NOMINA.FC_CNA(150) = 0 AND PCK_NOMINA.FC_CN(150)  <> 0  THEN 
            PCK_NOMINA.CN(939) :=  PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150) +PCK_NOMINA.FC_CN(514)) / 12 ,0); --13062018 ALCTOCANCIPA EN VISITA
        END IF ;

        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
        MI_DCC1:=0;
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);  
                FOR I IN 1..(PCK_NOMINA.GL_SMES - CASE WHEN PCK_NOMINA.GL_SPER = 7 THEN 0 ELSE 1 END)  LOOP --06052015 cas
                    PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_STRZERO(I,2), 1, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_STRZERO(I,2), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    IF ( PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) +PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339) ) > 0 THEN
              IF PCK_SYSMAN_UTL.FC_MES(MI_FECHAFPN) <> I  AND PCK_SYSMAN_UTL.FC_DIA(MI_FECHAFPN) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(MI_FECHAFPN))  THEN 
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
              END IF;
            --DOCEAVAS = DOCEAVAS - 1
                        PCK_NOMINA.CN(938) := PCK_NOMINA.FC_CN(938) + ( PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339) );
                    END IF;
                END LOOP;
        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - (PCK_NOMINA.FC_CN(359) +  PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) );
        PCK_NOMINA.GL_DOCEAVAS := CASE WHEN PCK_NOMINA.GL_DOCEAVAS < 0  THEN 0 ELSE PCK_NOMINA.GL_DOCEAVAS END ;
                MI_VALORP :=  PCK_NOMINA.FC_CN(10);
                MI_VALORP :=  PCK_NOMINA.FC_CN(1);
                MI_VALORP :=  PCK_NOMINA.FC_CN(942);
                MI_VALORP :=  PCK_NOMINA.GL_GRPNGV ;
                MI_VALORP :=  PCK_NOMINA.GL_AUXT;
                MI_VALORP :=  PCK_NOMINA.GL_AUXA;
                MI_VALORP :=  PCK_NOMINA.GL_VPT;
                MI_VALORP :=  PCK_NOMINA.FC_CN(931);
                MI_VALORP :=  PCK_NOMINA.GL_PVAC;
                MI_VALORP :=  PCK_NOMINA.FC_CN(939);
        PCK_NOMINA.GL_FACTORPN := CASE WHEN  PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END  + PCK_NOMINA.FC_CN(942)  + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + ( PCK_NOMINA.GL_PVAC / 12 ) + PCK_NOMINA.FC_CN(939);
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);

        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
          --If cn(937) > 0 Then DCC = cn(937): DOCEAVAS = Int(cn(937) / 30) 'JUNIO 30/2004
                    IF PCK_NOMINA.FC_CN(158) = 0 THEN
                        MI_VALORP :=  PCK_NOMINA.GL_FACTORPN;
                        MI_VALORP :=  PCK_NOMINA.GL_DCC;
                        PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC, 0);
                    END IF;                 

                ELSE
            IF PCK_NOMINA.FC_CN(937) > 0 THEN
                        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                        PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);
                    END IF;  --JUNIO 30/2004
                    IF PCK_NOMINA.FC_CN(158) = 0 THEN
                        PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                    END IF;
                END IF;
      ELSIF PCK_NOMINA.GL_FECHAI > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')  AND PCK_NOMINA.GL_FECHAI <= TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')  THEN 
          --Caso 3: Cuando Ingresa entre el 01/01 y el 30/06 y no se retira
        --CALCULAR PRIMA EXTRALEGAL CON PRIMA DE NAVIDAD CONCEPTO
        IF PCK_NOMINA.GL_SMES  >= 7 THEN  --19112020 ADICION PRIMA DE NAVIDAD
            PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');  
          PCK_NOMINA.GL_FECHAIPS := TO_DATE( CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END , 'DD/MM/YYYY');

                    PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR   PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('31/12' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') END;
          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS),2), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_DNT :=  PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) +  PCK_NOMINA.FC_CN(356) +  PCK_NOMINA.FC_CN(357) +  PCK_NOMINA.FC_CN(359) +   PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
          PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,  PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
          PCK_NOMINA.CN(159) := CASE WHEN PCK_NOMINA.FC_CN(159) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND( CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10)  ELSE PCK_NOMINA.FC_CN(1) END / 180  * PCK_NOMINA.GL_DCC / 30 * 15 ,0) ELSE PCK_NOMINA.FC_CN(159) END  ;
        END IF ;
        MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        -- Trae acumulados
        IF PCK_NOMINA.GL_FECHAR IS NOT NULL THEN 
            IF PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN 
              PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
          END IF;
        END IF ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN),2), CASE WHEN  PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1 ELSE 2 END , PCK_NOMINA.GL_SANO, 11, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CNA(155) = 0 THEN --PARA LOS QUE NO TIENEN PRIMA DE VACACIONES PAGADAS EN EL AÃ‘O
            PCK_NOMINA.GL_PVAC := 0;
        ELSE
            PCK_NOMINA.GL_PVAC :=  PCK_NOMINA.FC_CNA(155)  + PCK_NOMINA.FC_CNA(501);
        END IF;
        PCK_NOMINA.CN(942) := 0; --Round(Cna(70) / 12, 0)
        PCK_NOMINA.CN(939) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) )/12,0)  ;

        PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)/ 12, 0);
        PCK_NOMINA.CN(932) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);
        --05052016 cn(939) = 0 --Round(valorultimABONIFICACION(personal!Id_de_Empleado) / 12, 0)
        PCK_NOMINA.GL_FACTORPN := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0  THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END +  PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + ( PCK_NOMINA.GL_PVAC / 12 ) + PCK_NOMINA.FC_CN(939);
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
        MI_DCC1:=0;
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN); 
        FOR I IN 1..PCK_NOMINA.GL_SMES LOOP
                    PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_STRZERO(I,2), 1, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_STRZERO(I,2), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339)  + PCK_NOMINA.FC_CN(339)) > 0 THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
            PCK_NOMINA.CN(938) :=  PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339)  + PCK_NOMINA.FC_CN(339) );
                    END IF;
                END LOOP;
          IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
            IF PCK_NOMINA.FC_CN(108) = 0 THEN
                  PCK_NOMINA.CN(108) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC,0) ;   
            END IF;
          ELSE
                   IF PCK_NOMINA.FC_CN(937) > 0 THEN
                        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                        PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);--JUNIO 30/2004
                    END IF; 
                    IF PCK_NOMINA.FC_CN(158) = 0 THEN
                        PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                    END IF;  
            END IF;
        ELSIF  PCK_NOMINA.GL_FECHAI > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')   THEN 
          -- caso 4: Cuando ingresa despues del 30 de Junio
                -- CALCULAR PRIMA EXTRALEGAL CON PRIMA DE NAVIDAD CONCEPTO 159
                IF PCK_NOMINA.GL_SMES  >= 7 THEN --19112020 ADICION PRIMA DE NAVIDAD
            PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');  
          PCK_NOMINA.GL_FECHAIPS := TO_DATE( CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END , 'DD/MM/YYYY');

                    PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR   PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('31/12' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') END;
          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS), PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS),2), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_DNT :=  PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) +  PCK_NOMINA.FC_CN(356) +  PCK_NOMINA.FC_CN(357) +  PCK_NOMINA.FC_CN(359) +   PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339);
          PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,  PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
          PCK_NOMINA.CN(159) := CASE WHEN PCK_NOMINA.FC_CN(159) = 0 THEN  PCK_SYSMAN_UTL.FC_ROUND( CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10)  ELSE PCK_NOMINA.FC_CN(1) END / 180  * PCK_NOMINA.GL_DCC / 30 * 15 ,0) ELSE PCK_NOMINA.FC_CN(159) END  ;
        END IF;
        MI_FECHAFPN := TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY');
        -- Trae acumulados
        MI_N2 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
                MI_N1 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, TO_DATE('30/11/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY')) - MI_GL_DNT1;
                IF NVL(PCK_NOMINA.GL_FECHAR, '') <> '' THEN
                    IF PCK_NOMINA.GL_FECHAR < TO_DATE('30/11/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                        MI_N2:=PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                        MI_N1:=PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - MI_GL_DNT1;
                    END IF;
                    IF  PCK_NOMINA.GL_FECHAR > TO_DATE('30/11/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAR < TO_DATE('31/12/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
                        MI_N2:=PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, PCK_NOMINA.GL_FECHAR) - PCK_NOMINA.GL_DNT;
                    END IF;
                END IF;       
          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_STRZERO(PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPN),2), CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPN) < 16 THEN 1  ELSE 2  END, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);    
        --Factores para el calculo de la prima de navidad
        IF PCK_NOMINA.FC_CNA(155) = 0 THEN --' PARA LOS QUE NO TIENEN PRIMA DE VACACIONES PAGADAS EN EL AÃ‘O
           PCK_NOMINA.GL_PVAC := 0;
          ELSE
           PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155);
                END IF; 
        PCK_NOMINA.CN(942) := 0; --Round(Cna(70) / 12, 0)
        PCK_NOMINA.CN(949) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150))/ 12 ,0); --05052016
        PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO),0) / 12; --01122011
        PCK_NOMINA.CN(931) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12,0);
        --05052016 cn(939) = 0 --Round(valorultimABONIFICACION(personal!Id_de_Empleado) / 12, 0)
        PCK_NOMINA.GL_FACTORPN := CASE WHEN  PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END  + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(931) + ( PCK_NOMINA.GL_PVAC / 12 ) + PCK_NOMINA.FC_CN(939);
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN);
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN, MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
        FOR I IN 1..PCK_NOMINA.GL_SMES LOOP
                    PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_STRZERO(I,2), 1, PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_STRZERO(I,2), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                    IF (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339)  + PCK_NOMINA.FC_CN(339)) > 0 THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
            PCK_NOMINA.CN(938) :=  PCK_NOMINA.FC_CN(938) + (PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(339)  + PCK_NOMINA.FC_CN(339) );
                    END IF;
                END LOOP;
        PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CN(938);
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE NAVIDAD PROPORCIONAL POR DIAS', ' ') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPN,MI_FECHAFPN) - PCK_NOMINA.GL_DNT;
          IF PCK_NOMINA.FC_CN(158) = 0 THEN
                        PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 360 * PCK_NOMINA.GL_DCC,0);      
          END IF;
          ELSE
          IF PCK_NOMINA.FC_CN(937) > 0 THEN
                        PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(937);
                        PCK_NOMINA.GL_DOCEAVAS := TRUNC(PCK_NOMINA.FC_CN(937) / 30);--JUNIO 30/2004
                    END IF; 
                    IF PCK_NOMINA.FC_CN(158) = 0 THEN
                        PCK_NOMINA.CN(158) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN / 12 * PCK_NOMINA.GL_DOCEAVAS, 0);
                    END IF;  
        END IF;
      END IF; 
    --END IF;   
    --27042015 AJUSTES NOMINA DICIEMBRE MENSUAL PAGO PRIMA DE VACACIONES NO TENIDA EN CUENTA EN PRIMA PERIODO 04 DEL MISMO MES.
    IF PCK_NOMINA.GL_SMES =  12 AND PCK_NOMINA.FC_CN(155) > 0 THEN 
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 12, 1, PCK_NOMINA.GL_SANO, 12, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);    
        IF PCK_NOMINA.FC_CN(158) > 0 AND PCK_NOMINA.FC_CN(504) = 0 AND PCK_NOMINA.FC_CNA(158) <> 0 THEN 
          PCK_NOMINA.CN(504) :=  PCK_NOMINA.FC_CN(158)- PCK_NOMINA.CNA(158);
        PCK_NOMINA.CN(158) := 0 ;
      END IF;
      IF  PCK_NOMINA.FC_CN(158) < 5000 THEN 
           PCK_NOMINA.CN(158) := 0;
      END IF; 
      IF  PCK_NOMINA.FC_CN(504) < 5000 THEN 
           PCK_NOMINA.CN(504) := 0;
      END IF;
    END IF;   
    PCK_NOMINA.GL_PRIMADIC := PCK_NOMINA.FC_CN(158);
        MI_DIASPRIMADIC := PCK_NOMINA.GL_DOCEAVAS ;-- 'cn(67)
        MI_TRANSPORTELEGAL := PCK_NOMINA.FC_CN(254);
        MI_RETEFUENTE := PCK_NOMINA.FC_CN(125);
        PCK_NOMINA.CN(930) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END ;
        IF PCK_NOMINA.GL_SPER = 4 THEN
            FOR I IN 2..599 LOOP
                IF ( I <> 125 ) AND ( I < 599 ) AND ( I <> 303 ) AND ( I <> 401 ) AND ( I <> 159 ) AND ( I <> 402 ) AND ( I <> 301 )  AND ( I <> 10 ) AND ( I <> 300 ) OR ( I >= 600 AND I <= 698 ) AND ( PCK_NOMINA.FC_CN(I) > 0 AND PCK_NOMINA.FC_CN(I) < 1 ) THEN
                    PCK_NOMINA.CN(I) := 0;
                END IF;
            END LOOP;
        END IF;

        PCK_NOMINA.CN(125) := MI_RETEFUENTE;
        PCK_NOMINA.CN(254) := MI_TRANSPORTELEGAL;
        PCK_NOMINA.CN(158) := PCK_NOMINA.GL_PRIMADIC;
        PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS; --'DIASPRIMADIC
        --'Guardando Factores
        PCK_NOMINA.CN(933) := PCK_NOMINA.GL_AUXT;
        PCK_NOMINA.CN(934) := PCK_NOMINA.GL_AUXA;
        PCK_NOMINA.CN(936) := PCK_NOMINA.FC_CN(67); -- Dias pactados prima
        PCK_NOMINA.CN(937) := PCK_NOMINA.GL_DCC; -- Dias calendario Comercial a 31 de Diciembre
    --cn(938) = DNT ' Licencias
    PCK_NOMINA.CN(935) := PCK_NOMINA.GL_GRPNGV;
        PCK_NOMINA.CN(940) := PCK_NOMINA.GL_VPT; 
    PCK_NOMINA.CN(988) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPN,0); --Promedio

        --01022018 NIIF
        PCK_NOMINA.GL_PN_DIAS := PCK_NOMINA.GL_DCC;
        PCK_NOMINA.GL_PN_BASE := PCK_NOMINA.GL_FACTORPN;

END PR_CALPRIMANAVIDADALCHONDA;

PROCEDURE PR_CALCPRIMASEMESTRALALCHONDA(
/*
NAME              : PR_CALCPRIMASEMESTRALALCHONDA
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÃ‘AS
DATE MIGRADOR     : 16/03/2020
TIME              : 08:00 AM
SOURCE MODULE     : NOMINAP2020.12.05_UNIFICADAS En access calcularprimasemestralALCHONDA
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       :
@NAME:  CALCULARPRIMASEMESTRALALCHONDA
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
    MI_FECHAAUX             	DATE;
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
    
    MI_VALOR :=   PCK_NOMINA.FC_CN(150);
    MI_VALOR :=  PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    MI_VALOR := PCK_NOMINA.FC_CN(514);
    IF (PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 4) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO >= PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO <= PCK_NOMINA.GL_FECHAFIN) THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);
    ELSE
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12 + CASE WHEN  PCK_PARST.FC_PAR('USAR ULTIMA BASP PAGADA EN LIQUIDACION FINAL', 'NO') =  'SI' THEN  PCK_NOMINA.FC_CN(150) / 12 ELSE 0 END ), 0);
    END IF;
    IF PCK_NOMINA.FC_CN(946) = 0 AND PCK_NOMINA.FC_CN(514) <> 0 THEN
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(514) / 12, 0);
    END IF;

MI_VALOR :=  PCK_NOMINA.FC_CN(10);
MI_VALOR :=  PCK_NOMINA.FC_CN(1);
MI_VALOR :=  PCK_NOMINA.GL_AUXA ;
MI_VALOR :=  PCK_NOMINA.GL_AUXT ;
MI_VALOR :=  PCK_NOMINA.GL_VPT;
MI_VALOR :=  PCK_NOMINA.GL_GRPNGV ;
MI_VALOR := PCK_NOMINA.FC_CN(946);

    PCK_NOMINA.GL_FACTORPS := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(946), 0);
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT ;
    IF (PCK_NOMINA.GL_DCC = 179 AND PCK_NOMINA.GL_SMES <= 7) OR PCK_NOMINA.GL_FECHAIPS = PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY'), 1) THEN
        PCK_NOMINA.GL_DOCEAVAS := 6;

        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - PCK_NOMINA.GL_DNT;
    ELSE
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS);
    END IF;


    FOR I IN 7..12 LOOP
        PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), I, 1, (PCK_NOMINA.GL_SANO - 1), I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
            IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  
                PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
            END IF;
            PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
        END IF;
    END LOOP;
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        FOR I IN 1..PCK_NOMINA.GL_SMES LOOP
            PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, I, 1, PCK_NOMINA.GL_SANO, I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  
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
            FOR I IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, I, 1, PCK_NOMINA.GL_SANO, I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF ((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0) THEN
                    IF PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAFPS) <> I AND PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFPS) < PCK_SYSMAN_UTL.FC_DIA(LAST_DAY(PCK_NOMINA.GL_FECHAFPS)) THEN
                        PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS - 1;
                    END IF;
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953) + (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            FOR I IN 6..12 LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - 1), I, 1, (PCK_NOMINA.GL_SANO - 1), I, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) > 0 THEN
                    IF NOT (PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.FC_CN(404) = 1) THEN  
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
            
            -- TICKET 7748553 EFCM: PERIODO 7 LIQUIDACIONES, SE BUSCA LA ULTIMA PRIMA DE SERVICIOS PAGADA PARA CALCULAR DESDE AHI LOS DIAS A PAGAR
            IF (PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 ) THEN
                SELECT  FECHAINICIO
                INTO    MI_FECHAAUX
                FROM    (
                        SELECT  HISTORICOS.VALOR, HISTORICOS.ANO, HISTORICOS.MES, PERIODOS.FECHAINICIO
                        FROM    HISTORICOS INNER JOIN PERIODOS
                                ON HISTORICOS.COMPANIA      = PERIODOS.COMPANIA
                                AND HISTORICOS.ID_DE_PROCESO = PERIODOS.ID_DE_PROCESO
                                AND HISTORICOS.ANO           = PERIODOS.ANO
                                AND HISTORICOS.MES           = PERIODOS.MES
                                AND HISTORICOS.PERIODO       = PERIODOS.PERIODO
                        WHERE   HISTORICOS.ID_DE_CONCEPTO IN(160)
                                AND HISTORICOS.ANO || PCK_SYSMAN_UTL.FC_STRZERO(HISTORICOS.MES, 2) <= PCK_NOMINA.GL_SANO || PCK_SYSMAN_UTL.FC_STRZERO(PCK_NOMINA.GL_SMES, 2)
                                AND HISTORICOS.COMPANIA       = PCK_NOMINA.GL_COMPANIA
                                AND HISTORICOS.ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                AND HISTORICOS.ID_DE_PROCESO  ='01'
                                AND HISTORICOS.VALOR           >0
                                AND PERIODOS.ACUMULADO         NOT IN(0)
                                AND PERIODOS.PERIODO != 7
                        ORDER BY HISTORICOS.ANO || PCK_SYSMAN_UTL.FC_STRZERO(HISTORICOS.MES, 2) DESC
                        ) TABLA
                WHERE ROWNUM =1;
                
                IF ( MI_FECHAAUX > PCK_NOMINA.GL_FECHAIPS ) THEN
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAAUX, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                ELSE
                    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS, PCK_NOMINA.GL_FECHAFPS) - (PCK_NOMINA.FC_CN(953));
                END IF;
            END IF;
            -- TICKET 7748553 FIN --


                MI_VALOR := PCK_NOMINA.FC_CN(160);
                MI_VALOR := PCK_NOMINA.GL_FACTORPS;
                MI_VALOR := PCK_NOMINA.GL_DCC;
                MI_VALOR := PCK_NOMINA.FC_CN(67);
                MI_VALOR := PCK_NOMINA.FC_CNA(160);
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
            FOR I IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, I, '01', PCK_NOMINA.GL_SANO, I, '99', PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
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
        FOR I IN 2..699 LOOP
            IF (I <> 125) AND (I <> 303) AND (I <> 301) AND (I <> 300) AND (I < 599) OR (I >= 600 AND I <= 698) AND (PCK_NOMINA.FC_CN(I) > 0 AND PCK_NOMINA.FC_CN(I) < 1) THEN
                PCK_NOMINA.CN(I) := 0;
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

END PR_CALCPRIMASEMESTRALALCHONDA;

PROCEDURE PR_PRIMAVACACIONESALCHONDA(
      /*
      NAME              : PR_PRIMAVACACIONESALCHONDA
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÃ‘AS
      DATE MIGRADOR     : 16/03/2021
      TIME              :
      SOURCE MODULE     : NOMINAP2020.12.05_UNIFICADAS en acces calcularprimadevacacionesALCHONDA
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       :
      @NAME:  CALCPRIMAVACACIONESALCHONDA
      */
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA )
AS
    MI_BONPAGADA NUMBER DEFAULT 0;
    MI_VALORP NUMBER := 0;
    MI_VALORF DATE;
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
      IF PCK_NOMINA.FC_CN(404) <> 0 THEN
        
        PCK_NOMINA.CN(984)                                                                                   := 0;
        IF PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) <> 0 THEN
          PCK_NOMINA.CN(981)  := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) 
             +  CASE WHEN PCK_PARST.FC_PAR(UN_PARAMETRO => 'USAR ULTIMA PRIMA SEMESTRAL PAGADA EN LIQUIDACION FINAL', UN_VLOMISION =>'')    = 'SI' THEN 
                 PCK_NOMINA.FC_CN(160)
                 ELSE 0 END )/ 12, 0);
          IF (PCK_NOMINA.GL_SMES = 7 OR PCK_NOMINA.GL_SMES = 6) AND (PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) <> 0 AND PCK_NOMINA.FC_CN(160) <> 0) AND PCK_NOMINA.FC_CN(404) <> 0 THEN
            PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 160, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.FC_CN(160)= 0 AND PCK_NOMINA.FC_CN(160) > 0 THEN
              PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160)) / 12, 0);
            ELSIF PCK_NOMINA.FC_CNP(160) > 0 AND PCK_NOMINA.FC_CN(160) > 0 THEN
              PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(160) + PCK_NOMINA.FC_CN(160)  ) / 12, 0); --PCK_NOMINA.CN(981)                                                                             := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNP(160) + PCK_NOMINA.FC_CN(160)) / 12, 0);
            END IF;
            IF PCK_NOMINA.FC_CN(981) = 0 THEN
              PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) 
             +  CASE WHEN PCK_PARST.FC_PAR(UN_PARAMETRO => 'USAR ULTIMA PRIMA SEMESTRAL PAGADA EN LIQUIDACION FINAL', UN_VLOMISION =>'')    = 'SI' THEN 
                 PCK_NOMINA.FC_CN(160)
                 ELSE 0 END) / 12, 0);
            END IF;
          END IF;
        ELSE
          PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160)) / 12, 0);
        END IF;
        PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;
        MI_BONPAGADA := PCK_NOMINA.GL_BONPAGADA; 
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CNA(150) > 0 THEN
            MI_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) ) / 12, 0);
        ELSIF PCK_NOMINA.FC_CN(150) > 0 THEN
            MI_BONPAGADA := (PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12;
        END IF;
        PCK_NOMINA.GL_AC        := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.GL_LICENCIAS := (PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(339)) +
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
            END), PCK_NOMINA.GL_FECHAFIN1)                    - PCK_NOMINA.GL_LICENCIAS;
        PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.GL_DTV;
        PCK_NOMINA.GL_DTV      := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
        (
          (
            CASE
            WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
              PCK_NOMINA.GL_FECHAI
            ELSE
              (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL                                                             + 1)
            END), PCK_NOMINA.GL_FECHAFIN1)                                                                                - PCK_NOMINA.GL_LICENCIAS;
        PCK_NOMINA.GL_PERIODOS                                                                  := TRUNC(PCK_NOMINA.GL_DTV/ 360);
        IF (PCK_NOMINA.GL_DTV                                                                                             - (360 * PCK_NOMINA.GL_PERIODOS)) >= 315 THEN
          PCK_NOMINA.GL_PERIODOS                                                                := PCK_NOMINA.GL_PERIODOS + 1;
        END IF;
        PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15                   * PCK_NOMINA.GL_PERIODOS, 2);
        PCK_NOMINA.CN(93)     := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) *
        (
          CASE
          WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN
            1
          ELSE
            PCK_NOMINA.GL_PERIODOS
          END), 2);
        IF PCK_NOMINA.GL_DIASVAC  = 0 THEN
          PCK_NOMINA.GL_PERIODOS :=
          (
            CASE
            WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN
              1
            ELSE
              PCK_NOMINA.GL_PERIODOS
            END);
          PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(15 *
          (
            CASE
            WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN
              1
            ELSE
              PCK_NOMINA.GL_PERIODOS
            END) / 360 * PCK_NOMINA.GL_DTV, 0);
        END IF;
        PCK_NOMINA.GL_AC           := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.GL_DTV        = 0 THEN
          PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND
          (
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
                PCK_NOMINA.FC_CN(10)
              ELSE
                PCK_NOMINA.FC_CN(1)
              END) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
        ELSE
          PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND
          (
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
                PCK_NOMINA.FC_CN(10)
              ELSE
                PCK_NOMINA.FC_CN(1)
              END) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
        END IF;
        PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
        (
          (
            CASE
            WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
              PCK_NOMINA.GL_FECHAI
            ELSE
              (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1)
            END), PCK_NOMINA.GL_FECHAFIN1)                    - PCK_NOMINA.GL_LICENCIAS;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
          PCK_NOMINA.CN(155)                            :=
          (
            CASE
            WHEN PCK_NOMINA.FC_CN(155) = 0 THEN
              PCK_SYSMAN_UTL.FC_ROUND
              (
                (
                  (
                    CASE
                    WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
                      PCK_NOMINA.FC_CN(10)
                    ELSE
                      PCK_NOMINA.FC_CN(1)
                    END) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA) * 15 / 30, 0)
            ELSE
              PCK_NOMINA.FC_CN(155)
            END);
        ELSE
          PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND
          (
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
                PCK_NOMINA.FC_CN(10)
              ELSE
                PCK_NOMINA.FC_CN(1)
              END) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0);
        MI_VALORP := PCK_NOMINA.FC_CN(155) ;
        MI_VALORP := PCK_NOMINA.FC_CN(10) ;
        MI_VALORP := PCK_NOMINA.FC_CN(1) ;
        MI_VALORP := PCK_NOMINA.GL_GRPNGV  ;
        MI_VALORP := PCK_NOMINA.GL_AUXT ;
        MI_VALORP := PCK_NOMINA.GL_AUXA  ;
        MI_VALORP := PCK_NOMINA.GL_VPT  ;
        MI_VALORP := PCK_NOMINA.FC_CN(981)  ;
        MI_VALORP := MI_BONPAGADA ;
        MI_VALORP := PCK_NOMINA.GL_DTV  ;
        
          PCK_NOMINA.CN(155) :=
          (
            CASE
            WHEN PCK_NOMINA.FC_CN(155) = 0 THEN
              PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND
              (
                (
                  CASE
                  WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
                    PCK_NOMINA.FC_CN(10)
                  ELSE
                    PCK_NOMINA.FC_CN(1)
                  END) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA, 0) * 15 / 30 / 360 * PCK_NOMINA.GL_DTV, 0)
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
          RTA := MSGBOX('SE ESTÃ¿ CALCULANDO VACACIONES A UN RETIRADO, DESEA CONTARLE LOS SÃ¿BADOS COMO DÃ¿A HÃ¿BIL PARA VACACIONES AL EMPLEADO ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES, VBYESNO, 'SYSMAN SOFTWARE');
          END IF;*/
          PCK_NOMINA.GL_FECHAFF    := NULL;
          PCK_NOMINA.GL_FECHAFF    := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY') - 1, NVL(PCK_NOMINA.GL_DIASVAC, 1));
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
              PCK_NOMINA.GL_FECHAFF1 := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA
                                                                          ,CASE WHEN PCK_NOMINA.GL_SPRC =  '99' 
                                                                             THEN  PCK_NOMINA.GL_FECHAFIN1
                                                                             ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO
                                                                          END 
                                                                          , PCK_NOMINA.GL_DIASVAC);
              PCK_NOMINA.GL_FECHAFF  := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA
                                                                          ,CASE WHEN PCK_NOMINA.GL_SPRC =  '99' 
                                                                             THEN  PCK_NOMINA.GL_FECHAFIN1
                                                                             ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO
                                                                          END
                                                                          , PCK_NOMINA.GL_DIASVAC);
            IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL
              (
                (
                  CASE
                  WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN
                    PCK_NOMINA.GL_FECHAI
                  ELSE
                    (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL = PCK_NOMINA.GL_FECHAI THEN 0 ELSE  1 END )
                  END), PCK_NOMINA.GL_FECHAFIN1) > 315 THEN
                  
                    MI_VALORF :=  PCK_NOMINA.GL_FECHAFIN1;
                    MI_VALORF :=  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO;
                    MI_VALORP :=  PCK_NOMINA.GL_DIASVAC;
                    
                    PCK_NOMINA.CN(96)                 := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.GL_SPRC =  '99' 
                                                                             THEN  PCK_NOMINA.GL_FECHAFIN1
                                                                             ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO
                                                                          END , PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC =  '99' 
                                                                             THEN  PCK_NOMINA.GL_FECHAFIN1
                                                                             ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO
                                                                          END , PCK_NOMINA.GL_DIASVAC));
            END IF;
             MI_VALORP := PCK_NOMINA.FC_CN(96);
            IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL THEN
              
              
                 PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC =  '99' 
                                                                             THEN  PCK_NOMINA.GL_FECHAFIN1 + 1 
                                                                             ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO
                                                                          END, PCK_NOMINA.GL_DIASVAC);
                PCK_NOMINA.CN(96)  := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
                
                IF PCK_NOMINA.FC_CN(96) = 0 AND PCK_NOMINA.GL_DIASVAC  =  1 THEN
                   PCK_NOMINA.CN(96) := 1;
                END IF;
                IF PCK_NOMINA.FC_CN(96) < 0 AND PCK_NOMINA.GL_DIASVAC  <>  0 THEN
                  PCK_NOMINA.CN(96) := 0;
                END IF; 
            END IF;
          END IF;
          IF PCK_NOMINA.FC_CN(96) = 0 THEN 
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC =  '99' 
                                                                             THEN  PCK_NOMINA.GL_FECHAFIN1 
                                                                             ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO
                                                                          END, PCK_NOMINA.GL_DIASVAC);
            PCK_NOMINA.CN(96)  := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.GL_SPRC =  '99' 
                                                                             THEN  PCK_NOMINA.GL_FECHAFIN1 
                                                                             ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO
                                                                          END, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, CASE WHEN PCK_NOMINA.GL_SPRC =  '99' 
                                                                             THEN  PCK_NOMINA.GL_FECHAFIN1 
                                                                             ELSE  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO
                                                                          END, PCK_NOMINA.GL_DIASVAC));
            --PCK_NOMINA.CN(96)     := PCK_NOMINA.FC_CN(96) + PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA.GL_DIASVAC - PCK_NOMINA.GL_DIASVAC  ,2);
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
              PCK_NOMINA.CN(155)                            := PCK_SYSMAN_UTL.FC_ROUND
              (
                (
                  (
                    CASE
                    WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN
                      PCK_NOMINA.FC_CNA(10)
                    ELSE
                      PCK_NOMINA.FC_CN(1)
                    END) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538)) / 12) * PCK_NOMINA.FC_CN(93) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
            ELSE
              PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND
              (
                (
                  CASE
                  WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN
                    PCK_NOMINA.FC_CNA(10)
                  ELSE
                    PCK_NOMINA.FC_CN(1)
                  END) + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(543)) / 12, 0);
              PCK_NOMINA.CN(155) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND
              (
                (
                  CASE
                  WHEN PCK_NOMINA.FC_CNA(10) <> 0 THEN
                    PCK_NOMINA.FC_CNA(10)
                  ELSE
                    PCK_NOMINA.FC_CN(1)
                  END) + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(543)) / 12, 0) * PCK_NOMINA.FC_CN(93) / 30 / 360 * PCK_NOMINA.GL_DTV, 0);
            END IF;
            PCK_NOMINA.CN(964)             := PCK_NOMINA.GL_DIASPENDIENTES;
            PCK_NOMINA.GL_DIASPROPORCIONAL := PCK_NOMINA.GL_DTV;
          END IF;
        ELSIF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
          PCK_NOMINA.GL_DTV        := PCK_NOMINA.GL_DTV / 30 * 15 / 360;
          --RTA := MSGBOX('SE ESTÃ¿ CALCULANDO VACACIONES A UN RETIRADO, DESEA CONTARLE LOS SÃ¿BADOS COMO DÃ¿A HÃ¿BIL PARA VACACIONES AL EMPLEADO ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 & ' ' & PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES, VBYESNO, 'SYSMAN SOFTWARE');
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
            --PCK_NOMINA.CN(96)      := (PCK_NOMINA.GL_FECHAFF1 - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
            PCK_NOMINA.CN(96)  := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
            --END IF;
          END IF;
          IF PCK_NOMINA.FC_CN(96)  = 0 THEN
            PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC);
           -- PCK_NOMINA.CN(96)     := (PCK_NOMINA.GL_FECHAFF - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
           PCK_NOMINA.CN(96)  := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
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
                END), PCK_NOMINA.GL_FECHAFIN1)                    - PCK_NOMINA.GL_LICENCIAS;
            PCK_NOMINA.GL_DTV :=
            (
              CASE
              WHEN PCK_NOMINA.GL_DTV > 330 THEN
                PCK_NOMINA.GL_DTV
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
              --PCK_NOMINA.CN(96)                                                           := (PCK_NOMINA.GL_FECHAFF1 - TO_DATE(TO_CHAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, 'DD/MM/YYYY'), 'DD/MM/YYYY')) + 1;
              PCK_NOMINA.CN(96)  := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
              IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO IS NULL AND PCK_NOMINA.FC_CN(96)  = 0 THEN
                 PCK_NOMINA.GL_FECHAFF := PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.GL_FECHAFIN + 1, PCK_NOMINA.GL_DIASVAC);
                 PCK_NOMINA.CN(96)  := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_NOMINA.GL_DIASVAC));
               END IF;
              --END IF;
            END IF;
            PCK_NOMINA.CN(96) :=
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(96) = 1  OR PCK_NOMINA.FC_CN(96) < 1 THEN
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
                WHEN PCK_NOMINA.FC_CN(96) = 1 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO <> PCK_NOMINA.GL_FECHAFF AND PCK_NOMINA.GL_DIASVAC < 1   THEN
                  0
                ELSE
                  PCK_NOMINA.FC_CN(96)
                END);
                IF PCK_NOMINA.FC_CN(96) < 0  THEN 
                   PCK_NOMINA.CN(96)     := 0;
                END IF;
            END IF;
            PCK_NOMINA.CN(175)   := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV * PCK_NOMINA.FC_CN(96) / 30, 0) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV / 360)) - PCK_NOMINA.GL_DIASVAC, 3), 0);
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
        PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
      ELSE
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
          MI_BONPAGADA     := PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
          PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,(PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        END IF;
        IF MI_BONPAGADA = 0 AND PCK_NOMINA.FC_CN(150) > 0 AND PCK_NOMINA.GL_SPER <= 3 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INICIO_DISFRUTE > PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO THEN
          MI_BONPAGADA := PCK_NOMINA.FC_CN(150);
        END IF;
        PCK_NOMINA.CN(982)             := PCK_SYSMAN_UTL.FC_ROUND((MI_BONPAGADA) / 12, 0);
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
        PCK_NOMINA.CN(93)                               := PCK_NOMINA.FC_CN(68)                               * PCK_NOMINA.FC_CN(164);
        PCK_NOMINA.GL_AC                                := PCK_NOMINA.FC_ACUM(UN_COMPANIA,(PCK_NOMINA.GL_SANO - 1), PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, (PCK_NOMINA.GL_SMES - 1), 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
          PCK_NOMINA.GL_FACTORESPV                      := PCK_SYSMAN_UTL.FC_ROUND
          (
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
                PCK_NOMINA.FC_CN(10)
              ELSE
                PCK_NOMINA.FC_CN(1)
              END) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(919) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12, 0), 0), 0);
        ELSE
          PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND
          (
            (
              CASE
              WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN
                PCK_NOMINA.FC_CN(10)
              ELSE
                PCK_NOMINA.FC_CN(1)
              END) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.FC_CN(919) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12, 0) + PCK_NOMINA.FC_CN(982), 0), 0);
        END IF;
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
          PCK_NOMINA.GL_TOTVACACIONESCAJICA := PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(174) + PCK_NOMINA.FC_CN(175) + PCK_NOMINA.FC_CN(151);
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
    PCK_NOMINA.CN(981)     := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503)) / 12, 0);
    PCK_NOMINA.CN(983)     := 0;
    PCK_NOMINA.CN(984)     := 0;
    PCK_NOMINA.CN(985)     := 0;
    PCK_NOMINA.CN(986)     := 0;
    PCK_NOMINA.CN(987)     := 0;
    PCK_NOMINA.GL_PV_BASE  := PCK_NOMINA.GL_FACTORESPV;
    PCK_NOMINA.GL_PV_DIAS  := PCK_NOMINA.GL_DIASPROP;
    PCK_NOMINA.GL_VAC_DIAS := PCK_NOMINA.GL_DTV;

  END PR_PRIMAVACACIONESALCHONDA;


 PROCEDURE PR_CALCULARCESANTIASALCHONDA(
    /*
    NAME              : PR_CALCULARCESANTIASALCHONDA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÃ‘AS
    DATE MIGRADOR     : 16/03/2021
    TIME              :
    SOURCE MODULE     : NOMINAP2020.12.05_UNIFICADAS, En access = calcularcesantiasALCHONDA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  CESANTIASALCHONDA
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
    MI_VALORP               NUMBER := 0;
    MI_PARANTICIPOCEN       PCK_SUBTIPOS.TI_PARAMETRO;
    MI_ANTANO               NUMBER;
    
BEGIN

    PCK_NOMINA.GL_BASCES := 0;
    MI_FECHA := PCK_NOMINA.GL_FECHAINI;
    --(MZANGUNA:25/10/2018)-Se cambia GL_FECHAFIN a GL_FECHAINI
    
   --7752537_MROSERO - Se crea Parametro para actualizar la fecha de corte de los anticipos de cesantias retroactivas automaticamente 
   -- al a�o anterior del sistema en el que realizan la liquidacion asignando el valor a PCK_NOMINA.GL_FECHACORTE, si el parametro 
   -- esta en NO toma por defecto el valor del capo de Personal Fecha Cesantia Anticipo.
   
          MI_PARANTICIPOCEN := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                           ,UN_NOMBRE     => 'ACTUALIZAR FECHA CORTE ANTICIPO CESANTIAS RETROACTIVAS AUTOMATICAMENTE'
                                           ,UN_MODULO     => PCK_DATOS.FC_MODULONOMINA
                                           ,UN_FECHA_PAR  => SYSDATE),'NO'); 
        IF MI_PARANTICIPOCEN ='SI' THEN
            MI_ANTANO := EXTRACT(YEAR FROM SYSDATE) - 1;
            PCK_NOMINA.GL_FECHACORTE:= TO_DATE('30/12/' || MI_ANTANO, 'DD/MM/YYYY');
            ELSE 
            PCK_NOMINA.GL_FECHACORTE:= NVL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_ANTICIPO_CESANTIAS,
                                            TO_DATE('30/12/' || EXTRACT(YEAR FROM SYSDATE), 'DD/MM/YYYY'));
        END IF;
    --7752537_MROSERO
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
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHACORTE) - PCK_NOMINA.GL_LICENCIAS ;
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
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(501);
            END IF;
        END IF;

        PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.FC_CNA(150) > 0 THEN
            PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) ) / 12, 0);
        ELSIF PCK_NOMINA.FC_CN(150) > 0 THEN
            PCK_NOMINA.GL_BONPAGADA := ((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12)     ;
            PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538)) / 12, 0);
            PCK_NOMINA.GL_BONPAGADA := (PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12;
        END IF;
       -- PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)/ 12, 0);
        PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)/ 12, 0), 0);       
        --(EAMAYA:09/09/2019)-Se cambia suma de concepto 70 por la sumatoria del rango de conceptos entre 49 y 60
        PCK_NOMINA.CN(902) := CASE WHEN PCK_NOMINA.FC_CN(902) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(70) +  PCK_NOMINA.FC_CN(70) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(519)) / 12, 0) ELSE PCK_NOMINA.FC_CN(902) END ;
        --PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(504)) / 12, 0) ;
        PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND( PCK_NOMINA_CALCULO.FC_VALORULTIMAPRIMANAVIDAD(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
    MI_VALORP :=  PCK_NOMINA.FC_CN(969);
    MI_VALORP :=  PCK_NOMINA.FC_CN(10);
    MI_VALORP :=  PCK_NOMINA.FC_CN(1);
    MI_VALORP :=  PCK_NOMINA.GL_GRPNGV;
    MI_VALORP :=  PCK_NOMINA.GL_AUXA;
    MI_VALORP :=  PCK_NOMINA.GL_VPT;
    MI_VALORP :=  PCK_NOMINA.GL_AUXT;
    MI_VALORP :=  PCK_NOMINA.FC_CN(906);
    MI_VALORP :=  PCK_NOMINA.FC_CN(905);
    MI_VALORP :=  PCK_NOMINA.FC_CN(907);
    MI_VALORP :=  PCK_NOMINA.FC_CN(902);
    MI_VALORP :=  PCK_NOMINA.GL_BONPAGADA;

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
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.CN(910) := MI_DIAS                                                    ;
        PCK_NOMINA.CN(911) := MI_ANTICIPOS                                               ;
        PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION                   ;
        PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0)                            ;
        PCK_NOMINA.CN(914) := PCK_NOMINA.GL_VPT ;
        PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1) + 1 ;
    END IF;
END PR_CALCULARCESANTIASALCHONDA;

FUNCTION FC_VLRULTIMAPRIMADEVACACIONES
(
     /*
    NAME              : FC_VLRULTIMAPRIMADEVACACIONES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÃ‘AS
    DATE MIGRADOR     : 16/03/2020
    TIME              : 08:00 AM
    SOURCE MODULE     : NOMINAP2020.12.05_UNIFICADAS En access valorultimAPRIMADEVACACIONES
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  valorultimAPRIMADEVACACIONES
    */ 
  UN_ID_EMPLEADO IN PCK_SUBTIPOS.TI_ENTERO
) 
RETURN PCK_SUBTIPOS.TI_DOBLE

IS 
  MI_VALOR    PCK_SUBTIPOS.TI_DOBLE :=0;
  MI_MES    PCK_SUBTIPOS.TI_ENTERO :=0;
  MI_ANO    PCK_SUBTIPOS.TI_ENTERO :=0;
BEGIN


  BEGIN
    SELECT VALOR,ANO,MES INTO MI_VALOR,MI_ANO,MI_MES FROM (
      SELECT H.VALOR,H.ANO,H.MES
      FROM HISTORICOS H 
      LEFT JOIN PERIODOS P ON (H.COMPANIA = P.COMPANIA) 
      AND (H.ID_DE_PROCESO = P.ID_DE_PROCESO) 
      AND (H.ANO = P.ANO) 
      AND (H.MES = P.MES) 
      AND (H.PERIODO = P.PERIODO)
      WHERE H.ID_DE_CONCEPTO = 155
      AND TO_NUMBER(H.ANO || H.MES) <= TO_NUMBER(PCK_NOMINA.GL_SANO  ||  PCK_NOMINA.GL_SMES)
      AND H.COMPANIA  =  PCK_NOMINA.GL_COMPANIA
      AND H.ID_DE_EMPLEADO  = UN_ID_EMPLEADO
      AND H.ID_DE_PROCESO  = 1
      AND H.VALOR   <>  0 
      AND P.ACUMULADO   =   -1
      ORDER BY  H.ANO DESC, H.MES DESC) 
    WHERE ROWNUM <= 1;

    IF MI_VALOR > 0 THEN
      PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA,TO_NUMBER(NVL(PCK_PARST.FC_PAR('CODIGO CONCEPTO AJUSTES PRIMA DE VACACIONES', '0'), '501')), MI_ANO, MI_MES, 1, PCK_NOMINA.GL_SANO , PCK_NOMINA.GL_SMES ,PCK_NOMINA.GL_SPER, UN_ID_EMPLEADO);
      MI_VALOR := MI_VALOR + PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.CPARENTRADA(1).NIT = '832000283-6', 0, PCK_NOMINA.FC_CNP(TO_NUMBER(NVL(PCK_PARST.FC_PAR('CODIGO CONCEPTO AJUSTES PRIMA DE VACACIONES', '0'), '501'))));
      PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA,541, MI_ANO, MI_MES, 1, PCK_NOMINA.GL_SANO , PCK_NOMINA.GL_SMES ,PCK_NOMINA.GL_SPER, UN_ID_EMPLEADO);
      MI_VALOR := MI_VALOR + PCK_NOMINA.FC_CNP(541);
    END IF;

    RETURN MI_VALOR;

  EXCEPTION
    WHEN OTHERS THEN
    RETURN 0;
  END;

END FC_VLRULTIMAPRIMADEVACACIONES;

FUNCTION FC_DISCOBANCOLOMBIAGN 
 /*
      NAME              : FC_DISCOBANCOLOMBIA
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JOSE CAMILO HENAO BARRERA
      DATE MIGRADOR     : 28/08/2018
      TIME              : 10:29 AM
      SOURCE MODULE     : NOMINAP2018.07.04_UNIFICADAS MPV 13072018_MPV
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PERMITE GENERAR ARCHIVOS PLANOS PARA EL BANCO DE COLOMBIA GOB NARINO
      PARAMETERS        : 
      MODIFICATIONS     : 
     @NAME: generarDiscoBancoColombia
    */
(
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO          IN HISTORICOS.ID_DE_PROCESO%TYPE,
    UN_ANIO             IN HISTORICOS.ANO%TYPE,
    UN_MES              IN HISTORICOS.MES%TYPE,
    UN_PERIODO          IN HISTORICOS.PERIODO%TYPE,
    UN_BANCO            IN BANCO.BANCO%TYPE,    
    UN_FECHAREPORTE     IN DATE,            
    UN_TODOSLOSBANCOS   IN PCK_SUBTIPOS.TI_LOGICO,
    UN_OBSERVACION      IN VARCHAR2,
    UN_LOTE             IN VARCHAR2,
    UN_DESPAGO          IN VARCHAR2,
    UN_REFERENCIA       IN VARCHAR2,
    UN_INFORME          IN PCK_SUBTIPOS.TI_ENTERO,
    UN_TCUENTABANORIGEN IN VARCHAR2,   
    UN_CUENTABANORIGEN  IN VARCHAR2 
)
RETURN CLOB 
AS 
MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_CADENA           CLOB;
MI_NITCOMPANIA      COMPANIA.NITCOMPANIA%TYPE;
MI_RAZONSOCIAL      COMPANIA.NOMBRE%TYPE; 
MI_CUENTABANCO      BANCOS_NOMINA.CUENTA%TYPE;
MI_VALORTOTAL       HISTORICOS.VALOR%TYPE;
MI_CANTIDAD         PCK_SUBTIPOS.TI_ENTERO;
MI_APELLIDO1        VARCHAR2(30);
MI_APELLIDO2        VARCHAR2(30);
MI_NOMBRES          VARCHAR2(255); 
MI_PAR_BANCOS_FND   PCK_SUBTIPOS.TI_PARAMETRO;
BEGIN

  BEGIN
    SELECT SUM (HISTORICOS.VALOR),
            COUNT (1) NUMERO,
            COMPANIA.NITCOMPANIA,
            COMPANIA.NOMBRE  
       INTO MI_VALORTOTAL, 
            MI_CANTIDAD,
            MI_NITCOMPANIA, 
            MI_RAZONSOCIAL
    FROM HISTORICOS INNER JOIN PERSONAL ON (HISTORICOS.COMPANIA = PERSONAL.COMPANIA) 
    AND (HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO)
    INNER JOIN BANCOS_NOMINA ON (PERSONAL.COMPANIA = BANCOS_NOMINA.COMPANIA) 
    AND (PERSONAL.BANCO = BANCOS_NOMINA.BANCO)
    INNER JOIN COMPANIA ON PERSONAL.COMPANIA = COMPANIA.CODIGO
    WHERE HISTORICOS.COMPANIA     = UN_COMPANIA
      AND HISTORICOS.ID_DE_PROCESO  = UN_PROCESO
      AND HISTORICOS.ANO            = UN_ANIO
      AND HISTORICOS.MES            = UN_MES
      AND HISTORICOS.PERIODO        = UN_PERIODO
      AND HISTORICOS.ID_DE_CONCEPTO = '144'  
      AND PERSONAL.BANCO NOT IN ('00','98','99')
      AND PERSONAL.BANCO            = CASE WHEN UN_TODOSLOSBANCOS <> 0 THEN PERSONAL.BANCO ELSE UN_BANCO END        
      GROUP BY COMPANIA.NITCOMPANIA,
      COMPANIA.NOMBRE;

    EXCEPTION WHEN NO_DATA_FOUND THEN
     RAISE PCK_EXCEPCIONES.EXC_NOMINA;       
  END;

  MI_NITCOMPANIA :=REPLACE(MI_NITCOMPANIA, '.', '');
  MI_NITCOMPANIA :=REPLACE(MI_NITCOMPANIA, '-', '');              
  MI_CUENTABANCO :=REPLACE(UN_CUENTABANORIGEN, '.', '');
  MI_CUENTABANCO :=REPLACE(MI_CUENTABANCO, '-', '');     

  MI_PAR_BANCOS_FND  := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                         ,UN_NOMBRE     => 'PLANO PAGO BANCOS FND'
                                         ,UN_MODULO     => PCK_DATOS.FC_MODULONOMINA
                                         ,UN_FECHA_PAR  => SYSDATE);

  IF UN_INFORME = 0 THEN   --PLANO BANCOLOMBIA
  MI_CADENA :=  '1' || 
                CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN 
                LPAD(PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_NITCOMPANIA, 
                               UN_LONGITUD => 9),15,'0') ELSE
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_NITCOMPANIA, 
                                          UN_LONGITUD => 10)  END     ||
                CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN 'I' 
                ELSE '' END                                           ||
                CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN 
                RPAD(MI_RAZONSOCIAL,15,' ')
                ELSE
                RPAD(MI_RAZONSOCIAL,16,' ') END                       ||                                          
                '225'                                                 ||  
                RPAD(NVL(UN_OBSERVACION,'PAGONOMINA'),10,' ')         ||                                                 
                CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN 
                TO_CHAR(SYSDATE,'YYYYMMDD') ELSE  
                TO_CHAR(SYSDATE,'YYMMDD') END                         || 
                NVL(UN_LOTE, 'A')                                     ||
                CASE WHEN MI_PAR_BANCOS_FND IN('SI') THEN
                TO_CHAR(UN_FECHAREPORTE, 'YYYYMMDD')ELSE 
                TO_CHAR(UN_FECHAREPORTE, 'YYMMDD') END                || 
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CANTIDAD, 
                                          UN_LONGITUD => 6)           ||                                                                       
                CASE WHEN MI_PAR_BANCOS_FND IN('SI') THEN
                LPAD('0',17,'0') ELSE
                LPAD('0',12,'0') END                                  || 
                CASE WHEN MI_PAR_BANCOS_FND IN('SI') THEN
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_VALORTOTAL, 
                                          UN_LONGITUD => 15) || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   =>ROUND(MI_VALORTOTAL - TRUNC(MI_VALORTOTAL),2)*100,
                                                UN_LONGITUD => 2) ELSE 
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_VALORTOTAL, 
                                          UN_LONGITUD => 12) END      ||                      
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CUENTABANCO, 
                                          UN_LONGITUD => 11)          ||

                CASE WHEN MI_PAR_BANCOS_FND IN('SI') THEN
                'S' ELSE
                CASE WHEN UPPER(UN_TCUENTABANORIGEN) = 'C' THEN 'D' ELSE 'S' END      
                END 
                ||  CHR(13) || CHR(10);

  ELSIF UN_INFORME = 1 THEN  --ESTRUCTURA PAB 2015
  MI_CADENA :=  '1' || 
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => CASE WHEN LENGTH(MI_NITCOMPANIA) = 10 THEN SUBSTR(MI_NITCOMPANIA,1,9) ELSE MI_NITCOMPANIA END, 
                                          UN_LONGITUD => 15)          ||  
                'I'                                                   ||                                                                                          
                RPAD(' ',15,' ')                                      ||                                          
                '225'                                                 ||  
                RPAD('NOMINA',10,' ')                                 ||                                                                            
                TO_CHAR(SYSDATE,'YYYYMMDD')                           || 
                RPAD(NVL(UN_LOTE, 'AA'),2)                            ||
                TO_CHAR(UN_FECHAREPORTE, 'YYYYMMDD')                  || 
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CANTIDAD, 
                                          UN_LONGITUD => 6)           ||
                LPAD('0',17,'0')                                      ||                                                                 
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_VALORTOTAL, 
                                          UN_LONGITUD => 15)          ||                      
                '00'                                                  ||                                                                               
                PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => MI_CUENTABANCO, 
                                          UN_LONGITUD => 11)          ||          
                CASE WHEN UPPER(UN_TCUENTABANORIGEN) = 'C' THEN 'D' ELSE 'S' END  ||
                RPAD(' ',149,' ')                                                                                              
                ||  CHR(13) || CHR(10);  
  ELSIF UN_INFORME = 2 THEN --GENERAR EXCEL                  
  MI_CADENA :=  MI_NITCOMPANIA                                                 || PCK_DATOS.GL_SEPARADOR_COL ||
                '225'                                                          || PCK_DATOS.GL_SEPARADOR_COL ||
                MI_RAZONSOCIAL                                                 || PCK_DATOS.GL_SEPARADOR_COL ||
                UN_LOTE                                                        || PCK_DATOS.GL_SEPARADOR_COL ||
                UN_CUENTABANORIGEN                                             || PCK_DATOS.GL_SEPARADOR_COL ||
                UN_TCUENTABANORIGEN                                            || PCK_DATOS.GL_SEPARADOR_COL ||
                UN_DESPAGO                                                     || PCK_DATOS.GL_SEPARADOR_REG;                
  END IF;
    FOR RS IN (
    SELECT 
           PERSONAL.NUMERO_DCTO,
           PERSONAL.APELLIDO1, 
           PERSONAL.APELLIDO2,
           PERSONAL.NOMBRES,
           BANCOS_NOMINA.CODIGO_ENTIDAD,
           PERSONAL.CUENTA,
           PERSONAL.TIPOCUENTA,
           HISTORICOS.VALOR,
           PERSONAL.ID_DE_EMPLEADO,
           PERSONAL.DCTO_IDENTIDAD
    FROM HISTORICOS INNER JOIN PERSONAL ON (HISTORICOS.COMPANIA = PERSONAL.COMPANIA) 
    AND (HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO)
    INNER JOIN BANCOS_NOMINA ON (PERSONAL.COMPANIA = BANCOS_NOMINA.COMPANIA) 
    AND (PERSONAL.BANCO = BANCOS_NOMINA.BANCO)
    INNER JOIN COMPANIA ON PERSONAL.COMPANIA = COMPANIA.CODIGO
    WHERE HISTORICOS.COMPANIA     = UN_COMPANIA
      AND HISTORICOS.ID_DE_PROCESO  = UN_PROCESO
      AND HISTORICOS.ANO            = UN_ANIO
      AND HISTORICOS.MES            = UN_MES
      AND HISTORICOS.PERIODO        = UN_PERIODO
      AND HISTORICOS.ID_DE_CONCEPTO = '144'  
      AND PERSONAL.BANCO NOT IN ('00','98','99')
      AND PERSONAL.BANCO            = CASE WHEN UN_TODOSLOSBANCOS <> 0 THEN PERSONAL.BANCO ELSE UN_BANCO END               
      ORDER BY PERSONAL.APELLIDO1
            || ' '
            || PERSONAL.APELLIDO2
            || ' '
            || PERSONAL.NOMBRES)

        LOOP  

        IF RS.CODIGO_ENTIDAD IS NULL THEN
          MI_MSGERROR(1).CLAVE := 'EMPLEADO';
          MI_MSGERROR(1).VALOR := RS.ID_DE_EMPLEADO || ' ' || RS.APELLIDO1 || ' ' || RS.APELLIDO2 || ' ' || RS.NOMBRES; 
          PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA    => UN_COMPANIA,
                                     UN_MENSAJE_COD => PCK_ERRORES.ALER_CODENTIDADFINANCIERA,
                                     UN_REEMPLAZOS  => MI_MSGERROR);           
        END IF;

        MI_APELLIDO1 := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(TRIM(RS.APELLIDO1), ''));
        MI_APELLIDO2 := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(TRIM(RS.APELLIDO2), ''));
        MI_NOMBRES := PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(TRIM(RS.NOMBRES), ''));      
      --(APINEDA:17/10/2018)-Se agrega replace al nÃºmero de cuenta debido a que en la UPC contiene espacios intermedios.
      IF UN_INFORME = 0 THEN   --PLANO BANCOLOMBIA
         MI_CADENA := MI_CADENA                                                               ||  
             /*CAMPO1*/'6'                                                                    ||
            /*CAMPO2*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.NUMERO_DCTO, 
                                                UN_LONGITUD => 15)                            ||                                                
            CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN
            /*CAMPO3*/RPAD(MI_APELLIDO1 || ' ' || MI_APELLIDO2 || ' ' || MI_NOMBRES, 30,' ') ELSE                                               
            /*CAMPO3*/RPAD(MI_APELLIDO1 || ' ' || MI_APELLIDO2 || ' ' || MI_NOMBRES, 18,' ')        
            END                                                                               ||
            /*CAMPO4*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.CODIGO_ENTIDAD, 
                                                UN_LONGITUD => 9)                             ||    
         CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN
                      RPAD(RS.CUENTA,17,' ')                                              
                         ELSE
            /*CAMPO5*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => REPLACE(RS.CUENTA,' ',''),
                                                UN_LONGITUD => 17)    END                     ||                  
            /*CAMPO6*/'S'                                                                     ||                                                
            /*CAMPO7*/CASE WHEN RS.TIPOCUENTA = 'A' THEN '37' ELSE '27' END                   ||
            CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN
            PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.VALOR, 
                                                UN_LONGITUD => 15) || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   =>ROUND(RS.VALOR - TRUNC(RS.VALOR),2)*100,
                                                UN_LONGITUD => 2) ELSE
            /*CAMPO8*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.VALOR, 
                                                UN_LONGITUD => 10) END                           ||  
            CASE WHEN MI_PAR_BANCOS_FND IN ('SI') THEN
             TO_CHAR(UN_FECHAREPORTE, 'YYYYMMDD') ||  RPAD('NOMINA ' || MI_RAZONSOCIAL ,21,' ') || '000000' || RPAD(' ',15,' ') || RPAD(' ',80,' ') || LPAD(' ',16,'0') || RPAD(' ',27,' ') ELSE                    
            /*CAMPO9*/RPAD('NOMINA',9,' ')  ||
            /*CAMPO10*/RPAD(' ',12,' ')      ||
            /*CAMPO11*/RPAD(' ',1,' ')  
            END || CHR(10);
      ELSIF UN_INFORME = 1 THEN  --ESTRUCTURA PAB 2015  
         MI_CADENA := MI_CADENA                                                               ||  
            /*CAMPO1*/'6'                                                                     ||
            /*CAMPO2*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.NUMERO_DCTO, 
                                                UN_LONGITUD => 15)                            ||                                                
            /*CAMPO3*/RPAD(MI_APELLIDO1 || ' ' || MI_APELLIDO2 || ' ' || MI_NOMBRES, 30,' ')  ||      
            /*CAMPO4*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.CODIGO_ENTIDAD, 
                                                UN_LONGITUD => 9)                             ||                                                
            /*CAMPO5*/RPAD(CASE WHEN LENGTH(NVL(RS.CUENTA, ' ')) < 15 THEN 
                              NVL(RS.CUENTA, ' ')
                           ELSE 
                              SUBSTR(NVL(RS.CUENTA, ' '), 1, 4) || SUBSTR(NVL(RS.CUENTA, ' '),  LENGTH(RS.CUENTA) - 6)
                           END, 17, ' ')                                                      ||                  
            /*CAMPO6*/' '                                                                     ||                                                
            /*CAMPO7*/CASE WHEN RS.TIPOCUENTA = 'A' THEN '37' ELSE '27' END                   ||
            /*CAMPO8*/PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   => RS.VALOR, 
                                                UN_LONGITUD => 15)                            ||    
            /*CAMPO9*/'00'                                                                    ||                                                                                                
            /*CAMPO10*/'00000000'                                                             ||    
            /*CAMPO11*/RPAD('NOMINA ' || SUBSTR(NVL(UN_OBSERVACION, UPPER(SUBSTR(PCK_SYSMAN_UTL.FC_NOMBRE_MES(TO_NUMBER(TO_CHAR(SYSDATE, 'MM'))),1,3))), 1, 10), 21,' ') ||
            /*CAMPO12*/' '                                                                    ||
            /*CAMPO13*/RPAD(' ',5,' ')                                                        ||
            /*CAMPO14*/RPAD(' ',15,' ')                                                       ||
            /*CAMPO15*/RPAD(' ',80,' ')                                                       ||
            /*CAMPO16*/RPAD(' ',15,' ')                                                       ||
            /*CAMPO17*/RPAD(' ',27,' ') || CHR(10);  
      ELSIF UN_INFORME = 2 THEN --GENERAR EXCEL              
          MI_CADENA := MI_CADENA || 
                        CASE RS.DCTO_IDENTIDAD 
                        WHEN 'E' THEN '2' 
                        WHEN 'P' THEN '3' 
                        WHEN 'T' THEN '4' 
                        WHEN 'N' THEN '3' 
                        ELSE '1' END                                                            || PCK_DATOS.GL_SEPARADOR_COL ||                        
                        SUBSTR(RS.NUMERO_DCTO,0,15)                                             || PCK_DATOS.GL_SEPARADOR_COL || --Se estable valor maximo de caracteres a 15 TAR 1000097787 JORDUZ (27/02/03)
                        SUBSTR(MI_APELLIDO1 || ' ' || MI_APELLIDO2 || ' ' || MI_NOMBRES,0,18)   || PCK_DATOS.GL_SEPARADOR_COL || --Se estable valor maximo de caracteres a 18 TAR 1000097787 JORDUZ (27/02/03)
                        CASE WHEN RS.TIPOCUENTA = 'A' THEN '37' ELSE '27' END                   || PCK_DATOS.GL_SEPARADOR_COL ||
                        RS.CODIGO_ENTIDAD                                                       || PCK_DATOS.GL_SEPARADOR_COL ||
                        ''                                                          || PCK_DATOS.GL_SEPARADOR_COL ||
                        SUBSTR(REPLACE(REPLACE(RS.CUENTA, '.', ''), '-', ''),0,17)  || PCK_DATOS.GL_SEPARADOR_COL ||--Se estable valor maximo de caracteres a 17 TAR 1000097787 JORDUZ (27/02/03)
                        ''                                                          || PCK_DATOS.GL_SEPARADOR_COL ||
                        UN_REFERENCIA                                               || PCK_DATOS.GL_SEPARADOR_COL ||
                        ''                                                          || PCK_DATOS.GL_SEPARADOR_COL ||
                        SUBSTR(RS.VALOR,0,10)                                       || PCK_DATOS.GL_SEPARADOR_COL ||
                        TO_CHAR(UN_FECHAREPORTE, 'YYYYMMDD')                        || PCK_DATOS.GL_SEPARADOR_REG;   --Se estable valor maximo de caracteres a 10 TAR 1000097787 JORDUZ (27/02/03)
      END IF;                
    END LOOP; 

  RETURN MI_CADENA;
END FC_DISCOBANCOLOMBIAGN;

FUNCTION FC_GENERARDAVIVIENDAEXCEL
/*
  NAME              : FC_GENERAEXCELDAVIVIENDA
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : GUSTAVO PORTILLA SARRIA
  DATE MIGRATION    : 09/06/2023
  TIME              : 03:50 PM
  SOURCE MODULE     : NOMINA (6)
  MODIFIED BY       : 
  MODIFICATIONS     : 
  DESCRIPTION       : Funcion que permite generar ael archivo Excel del banco Davivienda
  PARAMETERS        : 
  @Name             :FC_GENERAEXCELDAVIVIENDA
  @Method           :GET
*/
(
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO      IN  PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_PERIODO      IN  PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_MES          IN  PCK_SUBTIPOS.TI_MES,
    UN_ANO          IN  PCK_SUBTIPOS.TI_ANIO
)
RETURN CLOB AS 

MI_ENCABEZADO           VARCHAR2(250);
MI_DATOSDISCO           CLOB;
MI_TOTALTRASLADOS       NUMBER;

BEGIN
  
  MI_TOTALTRASLADOS := 1;
    
  FOR MI_RS IN (
      SELECT
          PERSONAL.NUMERO_DCTO AS DOCUMENTO,
          CASE PERSONAL.DCTO_IDENTIDAD
              WHEN 'N' THEN '01'
              WHEN 'C' THEN '02'
              WHEN 'T' THEN '03'
              WHEN 'E' THEN '04'
              WHEN 'P' THEN '05'
              ELSE '00'
          END AS TIPO_DCTO,
          PERSONAL.CUENTA,
          CASE PERSONAL.TIPOCUENTA
              WHEN 'A' THEN 'CA'
              WHEN 'C' THEN 'CC'
              ELSE 'OP'
          END AS TIPO_TRANSACCION,
          BANCOS_NOMINA.CODIGO_ENTIDAD AS BANCO,          
          (SUM(HISTORICOS.VALOR)*100) AS VALOR                   
      FROM
          HISTORICOS
          INNER JOIN PERSONAL
              ON HISTORICOS.COMPANIA = PERSONAL.COMPANIA
              AND HISTORICOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
          INNER JOIN BANCOS_NOMINA
              ON PERSONAL.BANCO = BANCOS_NOMINA.BANCO
              AND PERSONAL.COMPANIA = BANCOS_NOMINA.COMPANIA
      WHERE
          HISTORICOS.ANO = UN_ANO
          AND HISTORICOS.MES = UN_MES
          AND HISTORICOS.ID_DE_PROCESO = UN_PROCESO
          AND HISTORICOS.PERIODO = UN_PERIODO
          AND HISTORICOS.COMPANIA = UN_COMPANIA
          AND HISTORICOS.ID_DE_CONCEPTO = '144'
          AND PERSONAL.BANCO NOT IN ('00','98','99')
          AND BANCOS_NOMINA.CUENTA IS NOT NULL
          AND BANCOS_NOMINA.CUENTA NOT IN ('NA')
        GROUP BY
          PERSONAL.NUMERO_DCTO,
          PERSONAL.DCTO_IDENTIDAD,
          PERSONAL.CUENTA,
          PERSONAL.TIPOCUENTA,
          BANCOS_NOMINA.CODIGO_ENTIDAD
  ) LOOP
      BEGIN
        MI_DATOSDISCO := MI_DATOSDISCO || MI_TOTALTRASLADOS       || PCK_DATOS.GL_SEPARADOR_COL ||
                                          MI_RS.DOCUMENTO         || PCK_DATOS.GL_SEPARADOR_COL ||
                                          MI_RS.TIPO_DCTO         || PCK_DATOS.GL_SEPARADOR_COL ||
                                          MI_RS.CUENTA            || PCK_DATOS.GL_SEPARADOR_COL ||
                                          MI_RS.TIPO_TRANSACCION  || PCK_DATOS.GL_SEPARADOR_COL ||
                                          MI_RS.BANCO             || PCK_DATOS.GL_SEPARADOR_COL ||
                                          MI_RS.VALOR             || PCK_DATOS.GL_SEPARADOR_REG;
                           
        MI_TOTALTRASLADOS := MI_TOTALTRASLADOS + 1;
      END;
  END LOOP;


  MI_ENCABEZADO := 'No. de Registro'         || PCK_DATOS.GL_SEPARADOR_COL ||
                   'Identificacion'         || PCK_DATOS.GL_SEPARADOR_COL ||
                   'Tipo de Identificacion' || PCK_DATOS.GL_SEPARADOR_COL ||
                   'Producto de Destino'    || PCK_DATOS.GL_SEPARADOR_COL ||
                   'Tipo de Producto'       || PCK_DATOS.GL_SEPARADOR_COL ||
                   'Codigo del banco'       || PCK_DATOS.GL_SEPARADOR_COL ||
                   'Valor del Traslado'     || PCK_DATOS.GL_SEPARADOR_REG;  

  MI_DATOSDISCO := MI_ENCABEZADO || MI_DATOSDISCO;    

  RETURN MI_DATOSDISCO;
END FC_GENERARDAVIVIENDAEXCEL;

FUNCTION FC_DATOS_REFORMA_PENS 
/*
    NAME              : FC_DATOS_REFORMA_P
    AUTHOR MIGRACION  : SEBASTIAN CARDENAS
    DATE MIGRADOR     : 07/05/025
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : 
    --NAME:    cargarDatosReforma
    --METHOD:  PUT
    */
(
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENA      IN CLOB,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)
 RETURN CLOB
AS
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_ANIO               PCK_SUBTIPOS.TI_ANIO;
MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
CONTADOR              NUMBER:=0;
MI_EXISTE             NUMBER := 0;
MI_BANDERA            BOOLEAN :=  FALSE;
MI_ID_DEL_FONDO       VARCHAR2(200 CHAR);
MI_FECHA_FONDO        VARCHAR2(200 CHAR);
MI_REGIMEN            NUMBER;
MI_MENSAJE            CLOB := '';
MI_NOMBRE             VARCHAR2(2000 CHAR);
MI_CAMPOS_ACT         VARCHAR2(2000 CHAR);
BEGIN


  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENA,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
  <<ACT_REFORMA_PENS>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST
  LOOP
  
   MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA =>  MI_DATOS_FILA(RS),
            UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
            
  MI_FECHA_FONDO := CASE  WHEN MI_DATOS_COLUMNAS(8) = 'NoData' THEN 'FECHAFONDOACCAI = null,' ELSE 'FECHAFONDOACCAI = TO_DATE(''' || MI_DATOS_COLUMNAS(8) || ''',''DD/MM/YYYY''),' END;
  MI_REGIMEN := CASE  WHEN MI_DATOS_COLUMNAS(9) = 'NoData' THEN '' ELSE CASE WHEN UPPER(MI_DATOS_COLUMNAS(9)) = 'NO' THEN 0 ELSE -1 END END;
  MI_ID_DEL_FONDO := CASE  WHEN MI_DATOS_COLUMNAS(7) = 'NoData' THEN '' ELSE REGEXP_SUBSTR(MI_DATOS_COLUMNAS(7), '^AFP[0-9]+') END;
  MI_NOMBRE := TRIM(MI_DATOS_COLUMNAS(3));
  
      SELECT COUNT(*)
    INTO MI_EXISTE
    FROM PERSONAL
    WHERE COMPANIA = UN_COMPANIA
      AND ID_DE_EMPLEADO = MI_DATOS_COLUMNAS(1)
      AND ID_DE_EMPLEADO NOT IN (0)  
      AND ESTADO_ACTUAL IN (1,3) 
      AND (FECHA_DE_RETIRO IS NULL OR FECHA_DE_RETIRO >= TO_DATE('01/01/2025', 'DD/MM/YYYY'));

    IF MI_EXISTE = 0 THEN
        DBMS_LOB.APPEND(MI_MENSAJE, 'Error: El empleado con ID ' || MI_DATOS_COLUMNAS(1) || ' Nombre: '  || MI_NOMBRE || ', no está registrado.' || CHR(13) || CHR(10));
      CONTINUE;
    END IF;

  IF MI_FECHA_FONDO IS NOT NULL OR MI_REGIMEN IS NOT NULL OR MI_ID_DEL_FONDO IS NOT NULL OR MI_EXISTE NOT IN (0) THEN
   MI_CAMPOS := 'FONDO_ACCAI  = '''|| MI_ID_DEL_FONDO ||''',
                    '|| MI_FECHA_FONDO ||'
                    REGIMEN_TRANSICION = '|| NVL(MI_REGIMEN,0) ||',
                    DATE_MODIFIED = SYSDATE,
                    MODIFIED_BY = '''|| UN_USUARIO ||''' ';
                    
   MI_CONDICION := 'COMPANIA = '''|| UN_COMPANIA ||'''
                    AND  ID_DE_EMPLEADO =  '''|| MI_DATOS_COLUMNAS(1) ||''' ';

   BEGIN
         PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => 'PERSONAL',
                            UN_ACCION    => 'M',
                            UN_CAMPOS    => MI_CAMPOS,
                            UN_CONDICION => MI_CONDICION);
                            
    MI_CAMPOS_ACT := CASE WHEN MI_ID_DEL_FONDO IS NULL THEN '' ELSE 'Fondo ACCAI,' END
                                                      || CASE WHEN MI_DATOS_COLUMNAS(8) = 'NoData' THEN '' ELSE ' Fecha Fondo ACCAI, ' END 
                                                      || 'Régimen Transición';
                            
     MI_MENSAJE := MI_MENSAJE || 'Actualización exitosa para el empleado ID: ' || MI_DATOS_COLUMNAS(1) || ' Nombre: '  || MI_NOMBRE ||  
                            '. Campos actualizados: ' || MI_CAMPOS_ACT || CHR(13) || CHR(10);
    
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
    RAISE PCK_EXCEPCIONES.EXC_NOMINA;
   END;
    
  END IF;
 END LOOP ACT_REFORMA_PENS;
 RETURN MI_MENSAJE;
END FC_DATOS_REFORMA_PENS;

PROCEDURE PR_CALCULARCESANTIASUES(
    /*
    NAME              : PR_CALCULARCESANTIASUES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 24/09/2025
    TIME              :
    SOURCE MODULE     : NOMINAP2018.05.01_UNIFICADAS, En access = calcularcesantiasALCTOCANCIPA
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       :
    @NAME:  CALCULARCESANTIASUES
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
        
            PCK_NOMINA.GL_PVAC := 0 + PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501); -- + PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
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
        
        PCK_NOMINA.CN(902) := CASE WHEN PCK_NOMINA.FC_CN(902) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_SUMACONA(49,60) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(519)) / 12, 0) ELSE PCK_NOMINA.FC_CN(902) END ;
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
        PCK_NOMINA.CN(908) := PCK_NOMINA.GL_BONPAGADA ;
        PCK_NOMINA.CN(910) := MI_DIAS                                                    ;
        PCK_NOMINA.CN(911) := MI_ANTICIPOS                                               ;
        PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION                   ;
        PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0)                            ;
        PCK_NOMINA.CN(914) := PCK_NOMINA.GL_VPT ;
        PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1) + 1 ;
    END IF;

    --20062018 CESANTIAS
    PCK_NOMINA.GL_CES_FECHAINI := PCK_NOMINA.GL_FECHAIC;
    PCK_NOMINA.GL_CES_FECHAFIN := PCK_NOMINA.GL_FECHAFIN1;
    PCK_NOMINA.GL_FECHAFINC    := PCK_NOMINA.GL_FECHAFIN1;
    PCK_NOMINA.GL_CES_LNR      := PCK_NOMINA.GL_LICENCIAS;
    PCK_NOMINA.GL_CES_DIASSINLNR := PCK_NOMINA.GL_DIAS + PCK_NOMINA.GL_LICENCIAS;
    PCK_NOMINA.GL_CES_GR := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.GL_CES_PT := PCK_NOMINA.GL_VPT;
    PCK_NOMINA.GL_CES_PA := PCK_NOMINA.GL_VPA;
    PCK_NOMINA.GL_CES_EXTRAS := PCK_NOMINA.CN(902);
    PCK_NOMINA.GL_BASP_CES := PCK_NOMINA.GL_BONPAGADA;
    PCK_NOMINA.GL_PS_CES := PCK_NOMINA.CN(906);
    PCK_NOMINA.GL_PN_CES := PCK_NOMINA.CN(905);
    PCK_NOMINA.GL_PV_CES := PCK_NOMINA.CN(907);

END PR_CALCULARCESANTIASUES;

PROCEDURE PR_CALCPRIMASEMESTRAL_TELEP(
/*
NAME              : PR_CALCPRIMASEMESTRAL_TELEP
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
DATE MIGRADOR     : 09/10/2025
TIME              : 02:19 PM
SOURCE MODULE     : 
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       : PROCEDIMIENTO PARA CALCULO PRIMA DE SERVICIO PARA TELEPACIFICO
                    CC2363
@NAME:  CALCULARPRIMASEMESTRALTELEPACIFICO
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

    IF PCK_NOMINA.GL_SMES <= 7 THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('01/07/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO - 1), 'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
        IF PCK_NOMINA.FC_CN(404) <> 0 AND PCK_NOMINA.GL_PERIODOACTUAL = 7 THEN
            IF (PCK_NOMINA.GL_FECHAIPS > PCK_NOMINA.GL_FECHAI) AND (PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAIPS) < 180) AND  PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) = 0 THEN --JM 27/03/2025 CC 1274
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

    IF PCK_NOMINA.FC_CN(404) <> 0 THEN 
        PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12 + (PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514))/12, 0);
    ELSE
      PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) / 12, 0);
    END IF; 

    PCK_NOMINA.GL_FACTORPS := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_VPT_C + PCK_NOMINA.GL_VPA + PCK_NOMINA.GL_GRPNGV + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(946), 0);
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

    PCK_NOMINA.CN(947) := PCK_NOMINA.GL_VPT_C; 
    PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXT ;
    PCK_NOMINA.CN(949) := PCK_NOMINA.GL_AUXA ;
    PCK_NOMINA.CN(950) := PCK_NOMINA.GL_GRPNGV;
    PCK_NOMINA.CN(951) := PCK_NOMINA.FC_CN(67);
    PCK_NOMINA.CN(952) := PCK_NOMINA.GL_DCC;
    PCK_NOMINA.CN(954) := PCK_NOMINA.GL_VPA;

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

END PR_CALCPRIMASEMESTRAL_TELEP;

PROCEDURE PR_CALCPRIMAVACACIONESTELEP
/*
NAME              : PR_CALCPRIMAVACACIONESTELEP
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
DATE MIGRADOR     : 09/10/2025
TIME              : 03:45 PM
SOURCE MODULE     : 
MODIFIER          :
DATE MODIFIED     :
TIME              :
DESCRIPTION       : PROCEDIMIENTO PARA CALCULO PRIMA DE VACACIONES PARA TELEPACIFICO
                    CC2363
@NAME:  CALCULARPRIMADEVACACIONESTELEPACIFICO
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
            PCK_NOMINA.CN(981) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)/12 + (PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503)) / 12, 0);
            IF (PCK_NOMINA.GL_SMES = 7 OR PCK_NOMINA.GL_SMES = 6) AND (PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) <> 0 AND PCK_NOMINA.FC_CN(160) <> 0) AND PCK_NOMINA.FC_CN(404) <> 0 THEN
                    PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(PCK_NOMINA.GL_COMPANIA, 160, PCK_NOMINA.GL_SANO, 6, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            END IF;

            MI_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0);

            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, (PCK_NOMINA.GL_SANO), PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            
            PCK_NOMINA.GL_LICENCIAS := NVL(((PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339) + PCK_NOMINA.FC_CNA(339)) +
            (
              CASE
              WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL OR PCK_NOMINA.GL_FECHAI =  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL THEN --mod JM CC 3352
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
            -- MOD JM CC 3352 
            PCK_NOMINA.CN(155) := CASE WHEN PCK_NOMINA.FC_CN(155) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.FC_CN(981) + MI_BONPAGADA)/ 30 * PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_DTV* (CASE WHEN PCK_NOMINA.FC_CN(68)>0 THEN PCK_NOMINA.FC_CN(68) ELSE 15 END) /360),0), 0) ELSE PCK_NOMINA.FC_CN(155) END;
  
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
                        -- MOD JM CC 3352
                        PCK_NOMINA.CN(155) := PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_GRPNGV + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CNA(543)) / 12 + (PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538)) / 12) * PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(68) > 0 THEN PCK_NOMINA.FC_CN(68) ELSE 15 END) / 30 / 360 * PCK_NOMINA.GL_DTV,0), 0);
               
                    PCK_NOMINA.CN(964) := PCK_NOMINA.GL_DIASPENDIENTES;
                    PCK_NOMINA.GL_DIASPROPORCIONAL := PCK_NOMINA.GL_DTV;
                END IF;
                PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END;
            ELSIF PCK_NOMINA.FC_CN(175) = 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06' THEN
                PCK_NOMINA.GL_DTV := PCK_NOMINA.GL_DTV / 30 * 22 / 360;
                
                PCK_NOMINA.GL_FECHAFF := '';
                PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL + 1 END, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
                PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(22 * PCK_NOMINA.GL_DTV / 360, 0);
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
                PCK_NOMINA.CN(96) := PCK_NOMINA.GL_DIASVAC;
                PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.GL_DIASVAC , 0);
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
                    IF PCK_NOMINA.GL_DIASPROP >= 0 AND PCK_NOMINA.FC_CN(155) = 0 THEN --MOD JM CC 3352

                        PCK_NOMINA.CN(68) := 15;
                       
                            PCK_NOMINA.CN(155) := PCK_NOMINA.FC_CN(155) + PCK_SYSMAN_UTL.FC_ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_GRPNGV + MI_BONPAGADA + (PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(160)) / 12) * PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) / 30 / 360 * PCK_NOMINA.GL_DIASPROP,0), 0);
                        
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
                        PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND((22 * PCK_NOMINA.GL_DTV / 360) + PCK_NOMINA.GL_DIASPENDIENTES,0);
                        PCK_NOMINA.CN(96)  := PCK_SYSMAN_UTL.FC_DIASFEBREROYMESCOMERCIAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_RETIRO, TRUNC(PCK_NOMINA.GL_DIASVAC)));
                        MI_VALOR :=  PCK_NOMINA.FC_CN(96);
                        MI_VALOR :=  PCK_SYSMAN_UTL.FC_ROUND((15 * PCK_NOMINA.GL_DTV / 360) - TRUNC(PCK_NOMINA.GL_DIASVAC), 2);
                        MI_VALOR :=  PCK_NOMINA.GL_DTV;
                        MI_VALOR := PCK_NOMINA.GL_DIASVAC;
                        PCK_NOMINA.CN(96) := PCK_NOMINA.GL_DIASVAC;
                        PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.GL_DIASVAC , 0);
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
            IF PCK_NOMINA.FC_CN(150) > 0 AND PCK_NOMINA.GL_SPRC <> 99  AND PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) = 0 THEN 
                MI_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND(GREATEST(NVL(PCK_NOMINA.FC_CN(150), 0), NVL(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0)) / 12, 0); 
            ELSE
                MI_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND(GREATEST(NVL(PCK_NOMINA.FC_CN(150), 0), NVL(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO), 0)) / 12, 0); 
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
                    IF PCK_NOMINA.CPARENTRADA(1).NIT = '891855138-1' THEN 
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
    PCK_NOMINA.CN(982) := PCK_SYSMAN_UTL.FC_ROUND(MI_BONPAGADA, 0);
    PCK_NOMINA.CN(983) := 0;
    PCK_NOMINA.CN(984) := PCK_NOMINA.FC_VALORULTIMOQUINQUENIO(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV), 0);
    
    IF PCK_NOMINA.GL_SMES = '12' AND (PCK_NOMINA.GL_SPER = 2 OR PCK_NOMINA.GL_SPER = 3) AND PCK_NOMINA.FC_CN(155) > 0 AND UPPER(PCK_PARST.FC_PAR('RELIQUIDAR PRIMA DE NAVIDAD EN DICIEMBRE CON VACACIONES', 'NO')) = 'SI' THEN
        PCK_NOMINA.CN(402) := 1;
    END IF;

    PCK_NOMINA.GL_PV_BASE := PCK_NOMINA.GL_FACTORESPV;
    PCK_NOMINA.GL_PV_DIAS := PCK_NOMINA.GL_DIASPROP;
    PCK_NOMINA.GL_VAC_DIAS := PCK_NOMINA.GL_DTV;

END PR_CALCPRIMAVACACIONESTELEP;

PROCEDURE PR_CALCULARCESANTIASTELEPAC
(
    /*
    NAME              : PR_CALCULARCESANTIASTELEPAC
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 09/10/2025
    TIME              :
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : PROCEDIMIENTO PARA CALCULO DE CESANTIAS PARA TELEPACIFICO
                        CC2363
    @NAME:  CALCULARCESANTIASTELEPACIFICO
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
                PCK_NOMINA.GL_PVAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CNA(501)), 0) + PCK_NOMINA.FC_CN(155) + PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            ELSE
                PCK_NOMINA.GL_PVAC := PCK_NOMINA.FC_CN(155) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA_PROC01.FC_VALULTIMAPRIMADEVACACIONES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            END IF;
        END IF;
        PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0) ;
        IF PCK_NOMINA.GL_SPER <> 7 THEN 
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, PCK_NOMINA.GL_SANO, 1, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        ELSE 
        PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA, 158, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); --MOD JM CC 3302
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA, (PCK_NOMINA.GL_SANO - CASE WHEN PCK_NOMINA.FC_CNP(158) > 0 AND PCK_NOMINA.GL_SMES = 12 THEN  0 ELSE  1 END) , (CASE WHEN PCK_NOMINA.FC_CNP(158) > 0 AND PCK_NOMINA.GL_SMES = 12 THEN  1 ELSE  PCK_NOMINA.GL_SMES END), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO); --MOD JM CC 3302
        END IF;
        IF PCK_NOMINA.FC_CNA(150) > 0 THEN
            IF PCK_NOMINA.FC_CN(404) <> 0 THEN 
               PCK_NOMINA.GL_BONPAGADA :=PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)) / 12, 0);  
            ELSE
                PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514)) / 12, 0);
            END IF;
        ELSIF PCK_NOMINA.FC_CN(150) > 0 AND PCK_NOMINA.GL_SPER <> 7  THEN
            PCK_NOMINA.GL_BONPAGADA := ((PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12)     ;
            PCK_NOMINA.GL_BONPAGADA := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538)) / 12, 0);
        END IF;
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA ;
        IF PCK_NOMINA.FC_CN(404) <> 0  THEN
            PCK_NOMINA.GL_AC := PCK_NOMINA_COM1.FC_ACUMCONCEPTO(UN_COMPANIA   => PCK_NOMINA.GL_COMPANIA,
                                                          UN_CONCEPTO   => '401',
                                                          UN_ANO1       => PCK_NOMINA.GL_SANO,
                                                          UN_MES1       => PCK_NOMINA.GL_MESACTUAL,
                                                          UN_PER1       => 1,
                                                          UN_ANO2       => PCK_NOMINA.GL_SANO,
                                                          UN_MES2       => PCK_NOMINA.GL_MESACTUAL,
                                                          UN_PER2       => 99,
                                                          UN_IDDEEMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF PCK_NOMINA.FC_CNP(401) <> 0  THEN 
                PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(160) + PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)+ PCK_NOMINA.FC_CN(503)+ PCK_NOMINA.FC_CNA(503)) / 12, 0);
            ELSE
                PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CNA(503)) / 12, 0);
            END IF;  
        ELSE
            PCK_NOMINA.CN(906) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(503)) / 12, 0);
        END IF;
        PCK_NOMINA.CN(907) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_PVAC / 12, 0);       
        PCK_NOMINA.CN(902) := CASE WHEN PCK_NOMINA.FC_CN(902) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_SUMACONA(47,60) + PCK_NOMINA.FC_CNA(528) + PCK_NOMINA.FC_CNA(519)) / 12, 0) ELSE PCK_NOMINA.FC_CN(902) END ; -- mod JM CC 3194 se agregan 47 y 48 
        PCK_NOMINA.CN(905) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158) + PCK_NOMINA.FC_CN(504)) / 12, 0) ;

        MI_PROMFAC := CASE WHEN PCK_NOMINA.FC_CN(969) > 0 THEN PCK_NOMINA.FC_CN(969) ELSE CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXA
                    + PCK_NOMINA.GL_VPT + PCK_NOMINA.GL_AUXT + PCK_NOMINA.FC_CN(906) + PCK_NOMINA.FC_CN(905) + PCK_NOMINA.FC_CN(907) + PCK_NOMINA.GL_BONPAGADA + PCK_NOMINA.FC_CN(902) END;  -- mod JM CC 3194 se agregan HX en la base 
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

        PCK_NOMINA.CN(269) := CASE WHEN PCK_NOMINA.FC_CN(269) = 0 THEN CASE WHEN MI_CESANTIA1 < 0 THEN 0 ELSE PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0) END ELSE PCK_NOMINA.FC_CN(269) END;
        PCK_NOMINA.CN(277) := CASE WHEN PCK_NOMINA.FC_CN(277) = 0 THEN MI_CESANTIA1 ELSE PCK_NOMINA.FC_CN(277) END;

        PCK_NOMINA.CN(900) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
        PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
        PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
        PCK_NOMINA.CN(901) := PCK_NOMINA.GL_GRPNGV;
        PCK_NOMINA.CN(909) := PCK_NOMINA.GL_BONPAGADA;
        PCK_NOMINA.CN(908) := PCK_NOMINA.GL_BONPAGADA;
        PCK_NOMINA.CN(910) := MI_DIAS;
        PCK_NOMINA.CN(911) := MI_ANTICIPOS;
        PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION;
        PCK_NOMINA.CN(913) := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC), 0);
        PCK_NOMINA.CN(914) := PCK_NOMINA.GL_VPT;
        PCK_NOMINA.CN(973) := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1) + 1;
    END IF;

END PR_CALCULARCESANTIASTELEPAC;
END PCK_NOMINA_COM4;
