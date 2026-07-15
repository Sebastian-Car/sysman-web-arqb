create or replace PACKAGE BODY "PCK_PRESTAMOS" AS

PROCEDURE PR_GENERAREXCLUSION
  /*
    NAME              : GENERAREXCLUSION  --> EN ACCESS Relacionado  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 08/02/2016
    TIME              : 16:00 PM 
    SOURCE MODULE     : SYSMANPD2016.02.01
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED	    : 27/01/2017
    TIME				      : 2:24 PM
    MODIFICATIONS	    : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
    DESCRIPTION       : CARGA LA TABLA DE HORARIO_EXCLUIDO A PARTIR DE UN ELEMENTO A PRESTAR
    @NAME:    generarExclusion
    @METHOD:  POST    
  */
( 
 UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
 UN_FECHAINI      IN DATE,
 UN_FECHAFIN      IN DATE,
 UN_ELEMENTO      IN VARCHAR2,
 UN_SERIE         IN PCK_SUBTIPOS.TI_ENTERO,
 UN_LIMPIA        IN PCK_SUBTIPOS.TI_ENTERO
 ) AS

  MI_TABLA               PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
  MI_STRSQL              PCK_SUBTIPOS.TI_STRSQL;
  MI_FECHAINI            DATE;
  MI_FECHAFIN            DATE; 
  RTA                    PCK_SUBTIPOS.TI_ENTERO;   
  MI_ELEMENTO_PAD        VARCHAR2(32000 CHAR);
  MI_SERIE_PAD           PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    MI_ELEMENTO_PAD:= '';
    IF UN_LIMPIA NOT IN(0) THEN
     BEGIN 
      BEGIN
      MI_TABLA   :='HORARIO_EXCLUIDO';    
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA
                                             ,UN_ACCION    => 'E'
                                             ,UN_CONDICION => ' COMPANIA=''' || UN_COMPANIA || ''''); 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 

      END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
                       MI_MSGERROR(1).CLAVE := 'TABLA';
                       MI_MSGERROR(1).VALOR := MI_TABLA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD =>PCK_ERRORES.ERRR_ALMACEN_DELETE_TABLA
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        );   

     END; 
    END IF;
    MI_FECHAINI := UN_FECHAINI;
    MI_FECHAFIN := UN_FECHAFIN;
    /*
    MI_FECHAINI := TO_DATE('01/' || UN_MES || '/' || UN_ANIO, 'DD/MM/YYYY');
    MI_FECHAINI := MI_FECHAINI - PCK_SYSMAN_UTL.FC_WEEKDAY(MI_FECHAINI,2)+1;
    --Se ubican curenta y dos dias por efectos de que el componente fecha mantiene los 42 espacios
    MI_FECHAFIN := MI_FECHAINI + 41;
    */
    --Días unicos propios de la parte
    PR_GENERAUNICOS    (UN_COMPANIA => UN_COMPANIA
                        ,UN_FECHAINI => MI_FECHAINI
                        ,UN_FECHAFIN => MI_FECHAFIN
                        ,UN_ELEMENTO => UN_ELEMENTO
                        ,UN_SERIE => UN_SERIE
                        ,UN_ELEMENTOPAD => UN_ELEMENTO
                        ,UN_SERIEPAD => UN_SERIE
                        ,UN_ESTODOS=> 0);

    --Días festivos propios de la parte
    PR_GENERAFESTIVOS  (UN_COMPANIA     => UN_COMPANIA
                        ,UN_FECHAINI    => MI_FECHAINI
                        ,UN_FECHAFIN    => MI_FECHAFIN
                        ,UN_ELEMENTO    => UN_ELEMENTO
                        ,UN_SERIE       => UN_SERIE
                        ,UN_ELEMENTOPAD => UN_ELEMENTO
                        ,UN_SERIEPAD    => UN_SERIE
                        ,UN_ESTODOS     => 0);
    --Un Día propios de la parte
    PR_GENERAUNDIA     (UN_COMPANIA     => UN_COMPANIA
                        ,UN_FECHAINI    => MI_FECHAINI
                        ,UN_FECHAFIN    => MI_FECHAFIN
                        ,UN_ELEMENTO    => UN_ELEMENTO
                        ,UN_SERIE       => UN_SERIE
                        ,UN_ELEMENTOPAD => UN_ELEMENTO
                        ,UN_SERIEPAD    => UN_SERIE
                        ,UN_ESTODOS     => 0);
    --Excluye todos los días propios de la parte
    PR_GENERATODOSDIAS (UN_COMPANIA     => UN_COMPANIA
                        ,UN_FECHAINI    => MI_FECHAINI
                        ,UN_FECHAFIN    => MI_FECHAFIN
                        ,UN_ELEMENTO    => UN_ELEMENTO
                        ,UN_SERIE       => UN_SERIE
                        ,UN_ELEMENTOPAD => UN_ELEMENTO
                        ,UN_SERIEPAD    => UN_SERIE
                        ,UN_ESTODOS     => 0);
    --Genera Condiciones del Padre
    <<CONDICIONES_PADRE>>
    FOR RS IN (
              SELECT ELEMENTO_PADRE, SERIE_PADRE 
              FROM DEVOLUTIVO 
              WHERE COMPANIA           = UN_COMPANIA
                AND ELEMENTO           = UN_ELEMENTO
                AND SERIE              = UN_SERIE
                AND SE_PRESTA          NOT IN(0) 
                AND NOT ELEMENTO_PADRE IS NULL 
                AND NOT SERIE_PADRE    IS NULL 
    )
    LOOP
      PR_GENERAUNICOS    (UN_COMPANIA     => UN_COMPANIA
                          ,UN_FECHAINI    => MI_FECHAINI
                          ,UN_FECHAFIN    => MI_FECHAFIN
                          ,UN_ELEMENTO    => UN_ELEMENTO
                          ,UN_SERIE       => UN_SERIE
                          ,UN_ELEMENTOPAD => RS.ELEMENTO_PADRE
                          ,UN_SERIEPAD    => RS.SERIE_PADRE
                          ,UN_ESTODOS     => 0);

      PR_GENERAFESTIVOS  (UN_COMPANIA     => UN_COMPANIA
                          ,UN_FECHAINI    => MI_FECHAINI
                          ,UN_FECHAFIN    => MI_FECHAFIN
                          ,UN_ELEMENTO    => UN_ELEMENTO
                          ,UN_SERIE       => UN_SERIE
                          ,UN_ELEMENTOPAD => RS.ELEMENTO_PADRE
                          ,UN_SERIEPAD    => RS.SERIE_PADRE
                          ,UN_ESTODOS     => 0);

      PR_GENERAUNDIA     (UN_COMPANIA     => UN_COMPANIA
                          ,UN_FECHAINI    => MI_FECHAINI
                          ,UN_FECHAFIN    => MI_FECHAFIN
                          ,UN_ELEMENTO    => UN_ELEMENTO
                          ,UN_SERIE       => UN_SERIE
                          ,UN_ELEMENTOPAD => RS.ELEMENTO_PADRE
                          ,UN_SERIEPAD    => RS.SERIE_PADRE
                          ,UN_ESTODOS     => 0);

      PR_GENERATODOSDIAS (UN_COMPANIA     => UN_COMPANIA
                          ,UN_FECHAINI    => MI_FECHAINI
                          ,UN_FECHAFIN    => MI_FECHAFIN
                          ,UN_ELEMENTO    => UN_ELEMENTO
                          ,UN_SERIE       => UN_SERIE
                          ,UN_ELEMENTOPAD => RS.ELEMENTO_PADRE
                          ,UN_SERIEPAD    => RS.SERIE_PADRE
                          ,UN_ESTODOS     => 0); 

      MI_ELEMENTO_PAD := RS.ELEMENTO_PADRE;
      MI_SERIE_PAD    := RS.SERIE_PADRE;
    END LOOP CONDICIONES_PADRE;
    --Días unicos propios de la parte
    PR_GENERAUNICOS    (UN_COMPANIA     => UN_COMPANIA
                        ,UN_FECHAINI    => MI_FECHAINI
                        ,UN_FECHAFIN    => MI_FECHAFIN
                        ,UN_ELEMENTO    => UN_ELEMENTO
                        ,UN_SERIE       => UN_SERIE
                        ,UN_ELEMENTOPAD => UN_ELEMENTO
                        ,UN_SERIEPAD    => UN_SERIE
                        ,UN_ESTODOS     => -1);
    --Días festivos propios de la parte
    PR_GENERAFESTIVOS  (UN_COMPANIA     => UN_COMPANIA
                        ,UN_FECHAINI    => MI_FECHAINI
                        ,UN_FECHAFIN    => MI_FECHAFIN
                        ,UN_ELEMENTO    => UN_ELEMENTO
                        ,UN_SERIE       => UN_SERIE
                        ,UN_ELEMENTOPAD => UN_ELEMENTO
                        ,UN_SERIEPAD    => UN_SERIE
                        ,UN_ESTODOS     => -1);
    --Un Día propios de la parte
    PR_GENERAUNDIA     (UN_COMPANIA     => UN_COMPANIA
                        ,UN_FECHAINI    => MI_FECHAINI
                        ,UN_FECHAFIN    => MI_FECHAFIN
                        ,UN_ELEMENTO    => UN_ELEMENTO
                        ,UN_SERIE       => UN_SERIE
                        ,UN_ELEMENTOPAD => UN_ELEMENTO
                        ,UN_SERIEPAD    => UN_SERIE
                        ,UN_ESTODOS     => -1);
    --Excluye todos los días propios de la parte
    PR_GENERATODOSDIAS (UN_COMPANIA    => UN_COMPANIA
                       ,UN_FECHAINI    => MI_FECHAINI
                       ,UN_FECHAFIN    => MI_FECHAFIN
                       ,UN_ELEMENTO    => UN_ELEMENTO
                       ,UN_SERIE       => UN_SERIE
                       ,UN_ELEMENTOPAD => UN_ELEMENTO
                       ,UN_SERIEPAD    => UN_SERIE
                       ,UN_ESTODOS     => -1);


    --18/04/2016 Se agrega para restar los horarios ya prestados por el elemento

    PR_GENERAPRESTADOS (UN_COMPANIA     => UN_COMPANIA
                        ,UN_FECHAINI    => MI_FECHAINI
                        ,UN_FECHAFIN    => MI_FECHAFIN
                        ,UN_ELEMENTO    => UN_ELEMENTO
                        ,UN_SERIE       => UN_SERIE
                        ,UN_ELEMENTOPAD => UN_ELEMENTO
                        ,UN_SERIEPAD    => UN_SERIE);

    IF MI_ELEMENTO_PAD IS NOT NULL THEN
      PR_GENERAPRESTADOS (UN_COMPANIA     => UN_COMPANIA
                          ,UN_FECHAINI    =>  MI_FECHAINI
                          ,UN_FECHAFIN    => MI_FECHAFIN
                          ,UN_ELEMENTO    => UN_ELEMENTO
                          ,UN_SERIE       => UN_SERIE
                          ,UN_ELEMENTOPAD => MI_ELEMENTO_PAD
                          ,UN_SERIEPAD    => MI_SERIE_PAD);
    END IF;

END PR_GENERAREXCLUSION;

PROCEDURE PR_GENERAUNICOS
    /*
    NAME              : GENERAUNICOS  --> EN ACCESS Relacionado  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 08/02/2016
    TIME              : 16:00 PM 
    SOURCE MODULE     : SYSMANPD2016.02.01
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED	    : 27/01/2017
    TIME				      : 3:35 PM
    MODIFICATIONS	    : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
    DESCRIPTION       : CARGA A LA TABLA DE HORARIO_EXCLUIDO A PARTIR DE UN ELEMENTO A PRESTAR LOS DIAS PROPIOS DE UNA PARTE
    @NAME:    generarUnicos
    @METHOD:  POST    
  */
( 
 UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
 UN_FECHAINI      IN DATE,
 UN_FECHAFIN      IN DATE,
 UN_ELEMENTO      IN VARCHAR2,
 UN_SERIE         IN PCK_SUBTIPOS.TI_ENTERO,
 UN_ELEMENTOPAD   IN VARCHAR2,
 UN_SERIEPAD      IN PCK_SUBTIPOS.TI_ENTERO,
 UN_ESTODOS       IN PCK_SUBTIPOS.TI_ENTERO
 ) AS

  MI_TABLA       PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES     PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
  MI_STRSQL      PCK_SUBTIPOS.TI_STRSQL;
  RTA            PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR    PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN

   BEGIN
    BEGIN

    MI_TABLA   :='HORARIO_EXCLUIDO'; 

    MI_CAMPOS  :=' COMPANIA
                  , ELEMENTO_DEVOLUTIVO
                  , SERIE_DEVOLUTIVO
                  , FECHA_HORA_INICIO
                  , FECHA_HORA_FIN';

    MI_VALORES :=' SELECT REGLAHORARIO.COMPANIA,''' || UN_ELEMENTO || ''',' || UN_SERIE || ', ' || CHR(13) || CHR(10) ||
                        ' TO_DATE( TO_CHAR(REGLAHORARIO.FECHA_INICIO +    ' || CHR(13) || CHR(10) ||
                                ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_INICIO,''hh24''),''HOUR''  ) + ' || CHR(13) || CHR(10) ||
                                ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_INICIO,''mi''  ),''MINUTE'') + ' || CHR(13) || CHR(10) ||
                                ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_INICIO,''ss''  ),''SECOND'')   ' || CHR(13) || CHR(10) ||
                                ',''DD/MM/YYYY hh24:mi:ss'') ' ||
                         ' ,''DD/MM/YYYY hh24:mi:ss''), ' || CHR(13) || CHR(10) ||
                        ' TO_DATE( TO_CHAR(REGLAHORARIO.FECHA_INICIO +    ' || CHR(13) || CHR(10) || 
                                ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_FINAL,''hh24''),''HOUR''  ) + ' || CHR(13) || CHR(10) ||
                                ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_FINAL,''mi''  ),''MINUTE'') + ' || CHR(13) || CHR(10) ||
                                ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_FINAL,''ss''  ),''SECOND'')   ' || CHR(13) || CHR(10) ||
                                ',''DD/MM/YYYY hh24:mi:ss'') ' ||
                        ',''DD/MM/YYYY hh24:mi:ss'') '            || CHR(13) || CHR(10) ||
                 ' FROM REGLAHORARIO '  || CHR(13) || CHR(10) ||
                 ' WHERE REGLAHORARIO.COMPANIA            =''' || UN_COMPANIA || '''' || CHR(13) || CHR(10) ||
                   ' AND REGLAHORARIO.ELEMENTO_DEVOLUTIVO =''' || CASE WHEN UN_ESTODOS <>0 THEN  '-1' ELSE UN_ELEMENTOPAD END || '''' || CHR(13) || CHR(10) ||
                   ' AND REGLAHORARIO.SERIE_DEVOLUTIVO    ='   || CASE WHEN UN_ESTODOS <>0 THEN   -1  ELSE UN_SERIEPAD    END         || CHR(13) || CHR(10) ||
                   ' AND REGLAHORARIO.DIA                 =9 ' || CHR(13) || CHR(10) ||
                   ' AND REGLAHORARIO.FECHA_INICIO BETWEEN TO_DATE(''' || TO_CHAR(UN_FECHAINI,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ' ||
                                                     ' AND TO_DATE(''' || TO_CHAR(UN_FECHAFIN,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ' || CHR(13) || CHR(10) ||
                   ' AND NOT EXISTS (SELECT FECHA_HORA_INICIO ' || CHR(13) || CHR(10) ||
                                   ' FROM HORARIO_EXCLUIDO    ' || CHR(13) || CHR(10) ||
                                   ' WHERE HORARIO_EXCLUIDO.COMPANIA            =''' || UN_COMPANIA || '''' || CHR(13) || CHR(10) ||
                                     ' AND HORARIO_EXCLUIDO.ELEMENTO_DEVOLUTIVO =''' || UN_ELEMENTO || '''' || CHR(13) || CHR(10) ||
                                     ' AND HORARIO_EXCLUIDO.SERIE_DEVOLUTIVO    ='   || UN_SERIE || CHR(13) || CHR(10) ||
                                     ' AND TO_DATE(TO_CHAR(HORARIO_EXCLUIDO.FECHA_HORA_INICIO,''DD/MM/YYYY''),''DD/MM/YYYY'') BETWEEN TO_DATE(''' || TO_CHAR(UN_FECHAINI,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ' ||
                                                     ' AND TO_DATE(''' || TO_CHAR(UN_FECHAFIN,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ' || CHR(13) || CHR(10) ||
                                    ')';

    RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA
                            ,UN_ACCION  => 'IS'
                            ,UN_CAMPOS  => MI_CAMPOS
                            ,UN_VALORES => MI_VALORES); 



        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 

    END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
                       MI_MSGERROR(1).CLAVE := 'TABLA';
                       MI_MSGERROR(1).VALOR := MI_TABLA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD =>PCK_ERRORES.ERRR_ALMACEN_INSERT_TABLA
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        );  

   END; 
END PR_GENERAUNICOS;

PROCEDURE PR_GENERAFESTIVOS
/*
    NAME              : GENERAUNICOS  --> EN ACCESS Relacionado  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 08/02/2016
    TIME              : 16:31 PM 
    SOURCE MODULE     : SYSMANPD2016.02.01
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED	    : 27/01/2017
    TIME				      : 3:46 PM
    MODIFICATIONS	    : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
    DESCRIPTION       : CARGA A LA TABLA DE HORARIO_EXCLUIDO A PARTIR DE UN ELEMENTO A PRESTAR LOS DIAS FESTIVOS
    @NAME:    generarFestivos
    @METHOD:  POST    
  */
( 
 UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
 UN_FECHAINI      IN DATE,
 UN_FECHAFIN      IN DATE,
 UN_ELEMENTO      IN VARCHAR2,
 UN_SERIE         IN PCK_SUBTIPOS.TI_ENTERO,
 UN_ELEMENTOPAD   IN VARCHAR2,
 UN_SERIEPAD      IN PCK_SUBTIPOS.TI_ENTERO,
 UN_ESTODOS       IN PCK_SUBTIPOS.TI_ENTERO
 ) AS

  MI_TABLA       PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES     PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
  MI_STRSQL      PCK_SUBTIPOS.TI_STRSQL;
  RTA            PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR    PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN

   BEGIN
    BEGIN
    MI_TABLA   :='HORARIO_EXCLUIDO';    
    MI_CAMPOS  :=' COMPANIA
                  , ELEMENTO_DEVOLUTIVO
                  , SERIE_DEVOLUTIVO
                  , FECHA_HORA_INICIO
                  , FECHA_HORA_FIN';
    MI_VALORES :=' SELECT REGLAHORARIO.COMPANIA,''' || UN_ELEMENTO || ''',' || UN_SERIE || ', ' || CHR(13) || CHR(10) ||
                        ' TO_DATE( TO_CHAR(FESTIVOS.ID_DE_FESTIVO +    ' || CHR(13) || CHR(10) ||
                                ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_INICIO,''hh24''),''HOUR''  ) + ' || CHR(13) || CHR(10) ||
                                ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_INICIO,''mi''  ),''MINUTE'') + ' || CHR(13) || CHR(10) ||
                                ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_INICIO,''ss''  ),''SECOND'')   ' || CHR(13) || CHR(10) ||
                                ',''DD/MM/YYYY hh24:mi:ss'') ' ||
                         ',''DD/MM/YYYY hh24:mi:ss''), ' || CHR(13) || CHR(10) ||
                        ' TO_DATE( TO_CHAR(FESTIVOS.ID_DE_FESTIVO +    ' || CHR(13) || CHR(10) || 
                                ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_FINAL,''hh24''),''HOUR''  ) + ' || CHR(13) || CHR(10) ||
                                ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_FINAL,''mi''  ),''MINUTE'') + ' || CHR(13) || CHR(10) ||
                                ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_FINAL,''ss''  ),''SECOND'')   ' || CHR(13) || CHR(10) ||
                                ',''DD/MM/YYYY hh24:mi:ss'') ' ||
                        ',''DD/MM/YYYY hh24:mi:ss'') '            || CHR(13) || CHR(10) ||
                 ' FROM REGLAHORARIO INNER JOIN FESTIVOS ON REGLAHORARIO.COMPANIA = FESTIVOS.COMPANIA '  || CHR(13) || CHR(10) ||
                 ' WHERE REGLAHORARIO.COMPANIA            =''' || UN_COMPANIA || '''' || CHR(13) || CHR(10) ||
                   ' AND REGLAHORARIO.ELEMENTO_DEVOLUTIVO =''' || CASE WHEN UN_ESTODOS <>0 THEN  '-1' ELSE UN_ELEMENTOPAD END || '''' || CHR(13) || CHR(10) ||
                   ' AND REGLAHORARIO.SERIE_DEVOLUTIVO    ='   || CASE WHEN UN_ESTODOS <>0 THEN   -1  ELSE UN_SERIEPAD    END         || CHR(13) || CHR(10) ||
                   ' AND REGLAHORARIO.DIA                 =8 ' || CHR(13) || CHR(10) ||
                   ' AND FESTIVOS.ID_DE_FESTIVO BETWEEN TO_DATE(''' || TO_CHAR(UN_FECHAINI,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ' ||
                                                  ' AND TO_DATE(''' || TO_CHAR(UN_FECHAFIN,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ' || CHR(13) || CHR(10) ||
                   ' AND FESTIVOS.ID_DE_FESTIVO BETWEEN REGLAHORARIO.FECHA_INICIO AND REGLAHORARIO.FECHA_FINAL ' || CHR(13) || CHR(10) ||
                   ' AND NOT EXISTS (SELECT FECHA_HORA_INICIO ' || CHR(13) || CHR(10) ||
                                   ' FROM HORARIO_EXCLUIDO    ' || CHR(13) || CHR(10) ||
                                   ' WHERE HORARIO_EXCLUIDO.COMPANIA            =''' || UN_COMPANIA || '''' || CHR(13) || CHR(10) ||
                                     ' AND HORARIO_EXCLUIDO.ELEMENTO_DEVOLUTIVO =''' || UN_ELEMENTO || '''' || CHR(13) || CHR(10) ||
                                     ' AND HORARIO_EXCLUIDO.SERIE_DEVOLUTIVO    ='   || UN_SERIE || CHR(13) || CHR(10) ||
                                     ' AND TO_DATE(TO_CHAR(HORARIO_EXCLUIDO.FECHA_HORA_INICIO,''DD/MM/YYYY''),''DD/MM/YYYY'') = FESTIVOS.ID_DE_FESTIVO ' || CHR(13) || CHR(10) ||
                                    ')';

    RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA
                            ,UN_ACCION  => 'IS'
                            ,UN_CAMPOS  => MI_CAMPOS
                            ,UN_VALORES => MI_VALORES); 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 

    END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
                       MI_MSGERROR(1).CLAVE := 'TABLA';
                       MI_MSGERROR(1).VALOR := MI_TABLA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD =>PCK_ERRORES.ERRR_ALMACEN_INSERT_TABLA
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        );  

   END; 

END PR_GENERAFESTIVOS;

PROCEDURE PR_GENERAUNDIA
/*
    NAME              : GENERAUNDIA  --> EN ACCESS Relacionado  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 08/02/2016
    TIME              : 16:31 PM 
    SOURCE MODULE     : SYSMANPD2016.02.01
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED	    : 27/01/2017
    TIME				      : 4:24 PM
    MODIFICATIONS	    : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
    DESCRIPTION       : CARGA A LA TABLA DE HORARIO_EXCLUIDO A PARTIR DE UN ELEMENTO A PRESTAR DE LOS DIAS ENTRE LUNES Y DOMINGO
    @NAME:    generarUnDia
    @METHOD:  POST    
  */
( 
 UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
 UN_FECHAINI      IN DATE,
 UN_FECHAFIN      IN DATE,
 UN_ELEMENTO      IN VARCHAR2,
 UN_SERIE         IN PCK_SUBTIPOS.TI_ENTERO,
 UN_ELEMENTOPAD   IN VARCHAR2,
 UN_SERIEPAD      IN PCK_SUBTIPOS.TI_ENTERO,
 UN_ESTODOS       IN PCK_SUBTIPOS.TI_ENTERO
 ) AS

  MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
  MI_FECHACONTROL DATE;
  MI_DIACONTROL   PCK_SUBTIPOS.TI_ENTERO;
  RTA             PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    MI_TABLA   :='HORARIO_EXCLUIDO'; 
    <<EXTRAER_REGLAHORARIO>>
    FOR RS IN(
              SELECT DISTINCT REGLAHORARIO.DIA 
              FROM  REGLAHORARIO 
              WHERE REGLAHORARIO.COMPANIA            = UN_COMPANIA
                AND REGLAHORARIO.ELEMENTO_DEVOLUTIVO = CASE WHEN UN_ESTODOS <> 0 THEN '-1' ELSE UN_ELEMENTOPAD END
                AND REGLAHORARIO.SERIE_DEVOLUTIVO    = CASE WHEN UN_ESTODOS <> 0 THEN  -1  ELSE UN_SERIEPAD    END
                AND REGLAHORARIO.DIA                 BETWEEN 1 AND 7 
                AND REGLAHORARIO.FECHA_INICIO        <= UN_FECHAFIN
                AND REGLAHORARIO.FECHA_FINAL         >= UN_FECHAINI
             )
  LOOP
    MI_FECHACONTROL := UN_FECHAINI;

    MI_DIACONTROL   := PCK_SYSMAN_UTL.FC_WEEKDAY(UN_FECHA    => MI_FECHACONTROL
                                                 ,UN_ARRANCA => 2);

    IF MI_DIACONTROL<RS.DIA THEN
      MI_FECHACONTROL :=  MI_FECHACONTROL + ( RS.DIA-MI_DIACONTROL);
    ELSIF  MI_DIACONTROL>RS.DIA THEN
      MI_FECHACONTROL :=  MI_FECHACONTROL + (8 - MI_DIACONTROL);
    END IF;

    WHILE MI_FECHACONTROL<= UN_FECHAFIN
    LOOP

     BEGIN
      BEGIN
      MI_CAMPOS  :=' COMPANIA
                    , ELEMENTO_DEVOLUTIVO
                    , SERIE_DEVOLUTIVO
                    , FECHA_HORA_INICIO
                    , FECHA_HORA_FIN';

      MI_VALORES :=' SELECT REGLAHORARIO.COMPANIA,''' || UN_ELEMENTO || ''',' || UN_SERIE || ', ' || CHR(13) || CHR(10) ||
                          ' TO_DATE(TO_CHAR(TO_DATE(''' || TO_CHAR(MI_FECHACONTROL,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') +    '        || CHR(13) || CHR(10) ||
                                  ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_INICIO,''hh24''),''HOUR''  ) + ' || CHR(13) || CHR(10) ||
                                  ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_INICIO,''mi''  ),''MINUTE'') + ' || CHR(13) || CHR(10) ||
                                  ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_INICIO,''ss''  ),''SECOND'')   ' || CHR(13) || CHR(10) ||
                                  ',''DD/MM/YYYY hh24:mi:ss'') ' ||
                           ' ,''DD/MM/YYYY hh24:mi:ss''), ' || CHR(13) || CHR(10) ||
                          ' TO_DATE( TO_CHAR(TO_DATE(''' || TO_CHAR(MI_FECHACONTROL,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') +    '        || CHR(13) || CHR(10) ||
                                  ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_FINAL,''hh24''),''HOUR''  ) + ' || CHR(13) || CHR(10) ||
                                  ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_FINAL,''mi''  ),''MINUTE'') + ' || CHR(13) || CHR(10) ||
                                  ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_FINAL,''ss''  ),''SECOND'')   ' || CHR(13) || CHR(10) ||
                                  ',''DD/MM/YYYY hh24:mi:ss'') ' ||
                          ' ,''DD/MM/YYYY hh24:mi:ss'') '            || CHR(13) || CHR(10) ||
                   ' FROM REGLAHORARIO '  || CHR(13) || CHR(10) ||
                   ' WHERE REGLAHORARIO.COMPANIA            =''' || UN_COMPANIA || '''' || CHR(13) || CHR(10) ||
                     ' AND REGLAHORARIO.ELEMENTO_DEVOLUTIVO =''' || CASE WHEN UN_ESTODOS <>0 THEN  '-1' ELSE UN_ELEMENTOPAD END || '''' || CHR(13) || CHR(10) ||
                     ' AND REGLAHORARIO.SERIE_DEVOLUTIVO    ='   || CASE WHEN UN_ESTODOS <>0 THEN   -1  ELSE UN_SERIEPAD    END         || CHR(13) || CHR(10) ||
                     ' AND REGLAHORARIO.DIA = ' || RS.DIA || CHR(13) || CHR(10) || --' AND REGLAHORARIO.DIA          BETWEEN 1 AND 7 ' || CHR(13) || CHR(10) ||
                     ' AND TO_DATE(''' || TO_CHAR(MI_FECHACONTROL,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') BETWEEN REGLAHORARIO.FECHA_INICIO AND REGLAHORARIO.FECHA_FINAL ' ||                                                       
                     ' AND NOT EXISTS (SELECT FECHA_HORA_INICIO ' || CHR(13) || CHR(10) ||
                                     ' FROM HORARIO_EXCLUIDO    ' || CHR(13) || CHR(10) ||
                                     ' WHERE HORARIO_EXCLUIDO.COMPANIA            =''' || UN_COMPANIA || '''' || CHR(13) || CHR(10) ||
                                       ' AND HORARIO_EXCLUIDO.ELEMENTO_DEVOLUTIVO =''' || UN_ELEMENTO || '''' || CHR(13) || CHR(10) ||
                                       ' AND HORARIO_EXCLUIDO.SERIE_DEVOLUTIVO    ='   || UN_SERIE || CHR(13) || CHR(10) ||
                                       ' AND TO_DATE(TO_CHAR(HORARIO_EXCLUIDO.FECHA_HORA_INICIO,''DD/MM/YYYY''),''DD/MM/YYYY'')=TO_DATE(''' || TO_CHAR(MI_FECHACONTROL,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ' || CHR(13) || CHR(10) ||
                                      ')';

           RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA
                                   ,UN_ACCION  => 'IS'
                                   ,UN_CAMPOS  => MI_CAMPOS
                                   ,UN_VALORES => MI_VALORES);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 

      END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
                       MI_MSGERROR(1).CLAVE := 'TABLA';
                       MI_MSGERROR(1).VALOR := MI_TABLA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD =>PCK_ERRORES.ERRR_ALMACEN_INSERT_TABLA
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        );  

     END; 

      MI_FECHACONTROL :=  MI_FECHACONTROL + 7;
    END LOOP;    
  END LOOP EXTRAER_REGLAHORARIO;
  END PR_GENERAUNDIA;

PROCEDURE PR_GENERATODOSDIAS 
/*
    NAME              : GENERATODOSDIAS   --> EN ACCESS Relacionado  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 08/02/2016
    TIME              : 15:10 PM 
    SOURCE MODULE     : SYSMANPD2016.02.01
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED	    : 27/01/2017
    TIME				      : 4:58 PM
    MODIFICATIONS	    : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
    DESCRIPTION       : CARGA A LA TABLA DE HORARIO_EXCLUIDO A PARTIR DE UN ELEMENTO A PRESTAR DE TODOS LOS DIAS
    @NAME:    generarTodosDias
    @METHOD:  POST    
  */
( 
 UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
 UN_FECHAINI      IN DATE,
 UN_FECHAFIN      IN DATE,
 UN_ELEMENTO      IN VARCHAR2,
 UN_SERIE         IN PCK_SUBTIPOS.TI_ENTERO,
 UN_ELEMENTOPAD   IN VARCHAR2,
 UN_SERIEPAD      IN PCK_SUBTIPOS.TI_ENTERO,
 UN_ESTODOS       IN PCK_SUBTIPOS.TI_ENTERO
 ) AS
  MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
  MI_FECHACONTROL DATE;
  MI_DIACONTROL   PCK_SUBTIPOS.TI_ENTERO;
  RTA             PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
    MI_TABLA   :='HORARIO_EXCLUIDO';

    MI_FECHACONTROL := UN_FECHAINI;    

    WHILE MI_FECHACONTROL<= UN_FECHAFIN
    LOOP
     BEGIN
      BEGIN
      MI_CAMPOS  :=' COMPANIA
                    , ELEMENTO_DEVOLUTIVO
                    , SERIE_DEVOLUTIVO
                    , FECHA_HORA_INICIO
                    , FECHA_HORA_FIN';

      MI_VALORES :=' SELECT REGLAHORARIO.COMPANIA,''' || UN_ELEMENTO || ''',' || UN_SERIE || ', ' || CHR(13) || CHR(10) ||
                          ' TO_DATE(TO_CHAR(TO_DATE(''' || TO_CHAR(MI_FECHACONTROL,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') +    '        || CHR(13) || CHR(10) ||
                                  ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_INICIO,''hh24''),''HOUR''  ) + ' || CHR(13) || CHR(10) ||
                                  ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_INICIO,''mi''  ),''MINUTE'') + ' || CHR(13) || CHR(10) ||
                                  ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_INICIO,''ss''  ),''SECOND'')   ' || CHR(13) || CHR(10) ||
                                  ',''DD/MM/YYYY hh24:mi:ss'') ' ||
                           ',''DD/MM/YYYY hh24:mi:ss''), ' || CHR(13) || CHR(10) ||
                          ' TO_DATE(TO_CHAR(TO_DATE(''' || TO_CHAR(MI_FECHACONTROL,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') +    '        || CHR(13) || CHR(10) ||
                                  ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_FINAL,''hh24''),''HOUR''  ) + ' || CHR(13) || CHR(10) ||
                                  ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_FINAL,''mi''  ),''MINUTE'') + ' || CHR(13) || CHR(10) ||
                                  ' NUMTODSINTERVAL(TO_CHAR(REGLAHORARIO.HORA_FINAL,''ss''  ),''SECOND'')   ' || CHR(13) || CHR(10) ||
                                  ',''DD/MM/YYYY hh24:mi:ss'') ' ||
                          ',''DD/MM/YYYY hh24:mi:ss'') '            || CHR(13) || CHR(10) ||
                   ' FROM REGLAHORARIO '  || CHR(13) || CHR(10) ||
                   ' WHERE REGLAHORARIO.COMPANIA            =''' || UN_COMPANIA || '''' || CHR(13) || CHR(10) ||
                     ' AND REGLAHORARIO.ELEMENTO_DEVOLUTIVO =''' || CASE WHEN UN_ESTODOS <>0 THEN  '-1' ELSE UN_ELEMENTOPAD END || '''' || CHR(13) || CHR(10) ||
                     ' AND REGLAHORARIO.SERIE_DEVOLUTIVO    ='   || CASE WHEN UN_ESTODOS <>0 THEN   -1  ELSE UN_SERIEPAD    END         || CHR(13) || CHR(10) ||
                     ' AND REGLAHORARIO.DIA                 =0 ' || CHR(13) || CHR(10) ||
                     ' AND TO_DATE(''' || TO_CHAR(MI_FECHACONTROL,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') BETWEEN REGLAHORARIO.FECHA_INICIO AND REGLAHORARIO.FECHA_FINAL ' ||                                                       
                     ' AND NOT EXISTS (SELECT FECHA_HORA_INICIO ' || CHR(13) || CHR(10) ||
                                     ' FROM HORARIO_EXCLUIDO    ' || CHR(13) || CHR(10) ||
                                     ' WHERE HORARIO_EXCLUIDO.COMPANIA            =''' || UN_COMPANIA || '''' || CHR(13) || CHR(10) ||
                                       ' AND HORARIO_EXCLUIDO.ELEMENTO_DEVOLUTIVO =''' || UN_ELEMENTO || '''' || CHR(13) || CHR(10) ||
                                       ' AND HORARIO_EXCLUIDO.SERIE_DEVOLUTIVO    ='   || UN_SERIE || CHR(13) || CHR(10) ||
                                       ' AND TO_DATE(TO_CHAR(HORARIO_EXCLUIDO.FECHA_HORA_INICIO,''DD/MM/YYYY''),''DD/MM/YYYY'')=TO_DATE(''' || TO_CHAR(MI_FECHACONTROL,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ' || CHR(13) || CHR(10) ||
                                      ')';

      RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA
                              ,UN_ACCION  => 'IS'
                              ,UN_CAMPOS  => MI_CAMPOS
                              ,UN_VALORES => MI_VALORES); 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 

      END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
                       MI_MSGERROR(1).CLAVE := 'TABLA';
                       MI_MSGERROR(1).VALOR := MI_TABLA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD =>PCK_ERRORES.ERRR_ALMACEN_INSERT_TABLA
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        ); 

     END; 

      MI_FECHACONTROL :=  MI_FECHACONTROL + 1;
    END LOOP;

 END PR_GENERATODOSDIAS;


FUNCTION FC_HACERPASE
    /* 
   NAME 			    : HACERPASE -> 
   AUTHORS 			  : VÍCTOR JULIO MOLANO BOLÍVAR 
   DATE MIGRADOR	: 10/02/2016
   TIME				    : 09:10 AM
   MODULO ORIGEN	: ALMACÉN (PRESTAMO DE INMUEBLES)
   DESCRIPTION		: PERMITE REALIZAR EL PASE DE UNA TRANSACCION A OTRA HEREDANDO TODOS LOS DATOS DE LA MISMA 
                    (DETALLE, HORARIOS, BIENES, REQUISITOS) Y CERRANDO LA TRANSACCION ACTUAL.
   MODIFIER			  : ELKIN GEOVANNY AMAYA SILVA
   DATE MODIFIED	: 27/01/2017
   TIME				    : 5:24 PM
   MODIFICATIONS	: Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
    @NAME:    hacerPase
    @METHOD:  GET    
  */
  (
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPOORIGEN         IN VARCHAR2,
  UN_CODIGOORIGEN       IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TIPODESTINO        IN VARCHAR2,
  UN_FECHAPASE          IN DATE DEFAULT SYSDATE
  )
  RETURN NUMBER 
  AS  

  MI_RETORNO      		   PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_RTA                 VARCHAR(100 CHAR);
  MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES             PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION           PCK_SUBTIPOS.TI_CONDICION;
  MI_CONSECUTIVO         PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN 
      MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'TRANSACCION_DEVOLUTIVO'
                                                        ,UN_CRITERIO => NULL
                                                        ,UN_CAMPO    =>'CODIGO');

   BEGIN
    BEGIN
      --Nueva Transacción
      MI_CAMPOS := 'COMPANIA
                    , TIPO
                    , CODIGO
                    , FECHA
                    , EXPEDIENTE
                    , TIPO_SOLICITANTE
                    , DOCUMENTO_SOLICITANTE
                    , SUCURSAL_SOLICITANTE
                    , DEPENDENCIA_SOLICITANTE
                    , ESTADO
                    , DESCRIPCION
                    , NUMDOCUMENTO_REPRESENTANTE
                    , SUCURSAL_REPRESENTANTE
                    , CEDULA_RESPONSABLE
                    , SUCURSAL_RESPONSABLE
                    , RESPUESTA
                    , TIPO_PADRE
                    , CODIGO_PADRE
                    , CREATED_BY
                    , DATE_CREATED
                    , MODIFIED_BY
                    , DATE_MODIFIED
                    , TIPO_EVENTO
                    , OBSERVACIONES
                    ,ID_BACKOFFICE';

      MI_VALORES := 'SELECT  
                          COMPANIA, 
                          ''' || UN_TIPODESTINO || ''' TIPO, 
                          ' || MI_CONSECUTIVO || ' CODIGO, 
                          TO_DATE(''' || UN_FECHAPASE || ''',''DD/MM/YYYY HH24:MI:SS'') FECHA, 
                          EXPEDIENTE,  
                          TIPO_SOLICITANTE,  
                          DOCUMENTO_SOLICITANTE, 
                          SUCURSAL_SOLICITANTE, 
                          DEPENDENCIA_SOLICITANTE, 
                          ''A'' AS ESTADO, 
                          DESCRIPCION, 
                          NUMDOCUMENTO_REPRESENTANTE, 
                          SUCURSAL_REPRESENTANTE, 
                          CEDULA_RESPONSABLE, 
                          SUCURSAL_RESPONSABLE, 
                          RESPUESTA,  
                          ''' || UN_TIPOORIGEN || ''' TIPO_PADRE, 
                          ' || UN_CODIGOORIGEN || ' CODIGO_PADRE, 
                          CREATED_BY,
                          DATE_CREATED, 
                          MODIFIED_BY,  
                          DATE_MODIFIED,
                          TIPO_EVENTO,
                          OBSERVACIONES,
                          ID_BACKOFFICE
                    FROM  TRANSACCION_DEVOLUTIVO  
                    WHERE COMPANIA = ''' || UN_COMPANIA || ''' 
                      AND TIPO     = ''' || UN_TIPOORIGEN || ''' 
                      AND CODIGO   = ' || UN_CODIGOORIGEN || ''; 

      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'TRANSACCION_DEVOLUTIVO'
                                  ,UN_ACCION  => 'IS'
                                  ,UN_CAMPOS  => MI_CAMPOS
                                  ,UN_VALORES => MI_VALORES);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 

      END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD =>PCK_ERRORES.ER_ALMACEN_INSERT_TRANSDEV); 
   END; 


      IF MI_RTA <> '0' THEN

        BEGIN
         BEGIN
          --Detalles Nueva Transacción
          MI_CAMPOS := 'COMPANIA
                      , CODIGO_TRANSACCION
                      , TIPO_TRANSACCION
                      , ELEMENTO_DEVOLUTIVO
                      , SERIE_DEVOLUTIVO
                      , CREATED_BY
                      , DATE_CREATED
                      , MODIFIED_BY
                      , DATE_MODIFIED';

          MI_VALORES := 'SELECT COMPANIA, 
                               ' || MI_CONSECUTIVO || ' CODIGO_TRANSACCION, 
                               ''' || UN_TIPODESTINO || ''' TIPO_TRANSACCION, 
                               ELEMENTO_DEVOLUTIVO, 
                               SERIE_DEVOLUTIVO, 
                               CREATED_BY, 
                               DATE_CREATED,  
                               MODIFIED_BY, 
                               DATE_MODIFIED 
                         FROM  D_TRANSACCION_DEV
                        WHERE  COMPANIA           = ''' || UN_COMPANIA || ''' 
                          AND  TIPO_TRANSACCION   = ''' || UN_TIPOORIGEN || ''' 
                          AND  CODIGO_TRANSACCION = ' || UN_CODIGOORIGEN || ''; 

          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'D_TRANSACCION_DEV'
                                       ,UN_ACCION  => 'IS'
                                       ,UN_CAMPOS  => MI_CAMPOS
                                       ,UN_VALORES => MI_VALORES);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 

         END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
                             PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD =>PCK_ERRORES.ER_ALMACEN_INSERT_DTRANSDEV);                                       
        END;

        BEGIN --Horarios Solicitud
         BEGIN

          MI_CAMPOS := 'COMPANIA
                      , CODIGO_TRANSACCION
                      , TIPO_TRANSACCION
                      , FECHA_HORA_INICIO
                      , FECHA_HORA_FIN
                      , CREATED_BY
                      , DATE_CREATED
                      , MODIFIED_BY
                      , DATE_MODIFIED';

          MI_VALORES := 'SELECT COMPANIA, 
                               ' || MI_CONSECUTIVO || ' CODIGO_TRANSACCION, 
                               ''' || UN_TIPODESTINO || ''' TIPO_TRANSACCION, 
                               FECHA_HORA_INICIO, 
                               FECHA_HORA_FIN, 
                               CREATED_BY, 
                               DATE_CREATED, 
                               MODIFIED_BY, 
                               DATE_MODIFIED 
                         FROM  HORARIO_SOLICITUD 
                        WHERE  COMPANIA           = ''' || UN_COMPANIA || ''' 
                          AND  TIPO_TRANSACCION   = ''' || UN_TIPOORIGEN || ''' 
                          AND  CODIGO_TRANSACCION = ' || UN_CODIGOORIGEN || ''; 

          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'HORARIO_SOLICITUD'
                                       ,UN_ACCION  => 'IS'
                                       ,UN_CAMPOS  => MI_CAMPOS
                                       ,UN_VALORES => MI_VALORES);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 

         END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
                             PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD =>PCK_ERRORES.ER_ALMACEN_INSERT_HSOLICITUD);           
        END;

        BEGIN
         BEGIN
          --Requisitos Solicitud
          MI_CAMPOS := 'COMPANIA
                       , TIPO_TRANSACCION
                       , CODIGO_TRANSACCION
                       , REQUISITO
                       , CUMPLE
                       , RUTA
                       , CREATED_BY
                       , DATE_CREATED
                       , MODIFIED_BY
                       , DATE_MODIFIED';

          MI_VALORES := 'SELECT COMPANIA, 
                               ''' || UN_TIPODESTINO || ''' TIPO_TRANSACCION, 
                               ' || MI_CONSECUTIVO || ' CODIGO_TRANSACCION, 
                               REQUISITO, 
                               CUMPLE, 
                               RUTA, 
                               CREATED_BY, 
                               DATE_CREATED, 
                               MODIFIED_BY, 
                               DATE_MODIFIED 
                         FROM  TRANSACCION_REQUISITOS 
                        WHERE  COMPANIA           = ''' || UN_COMPANIA || ''' 
                          AND  TIPO_TRANSACCION   = ''' || UN_TIPOORIGEN || ''' 
                          AND  CODIGO_TRANSACCION = ' || UN_CODIGOORIGEN || ''; 

          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TRANSACCION_REQUISITOS'
                                       ,UN_ACCION  =>  'IS'
                                       ,UN_CAMPOS  => MI_CAMPOS
                                       ,UN_VALORES => MI_VALORES);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 

         END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
                             PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD    => SQLCODE
                                       ,UN_ERROR_COD =>PCK_ERRORES.ER_ALMACEN_INSERT_TRANSREQ);      
        END;

        BEGIN 
         BEGIN
          --Cierre de transacción
          MI_CAMPOS := 'ESTADO = ''C''
                      , FECHA_PASE=TO_DATE(''' || UN_FECHAPASE || ''',''DD/MM/YYYY HH24:MI:SS'')';

          MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''   AND 
                           TIPO     = ''' || UN_TIPOORIGEN || ''' AND 
                           CODIGO   = ' || UN_CODIGOORIGEN || ''; 

          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'TRANSACCION_DEVOLUTIVO'
                                      ,UN_ACCION    => 'M'
                                      ,UN_CAMPOS    =>  MI_CAMPOS
                                      ,UN_CONDICION => MI_CONDICION);
          MI_RETORNO := 1;


            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 

         END;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
                             PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD    => SQLCODE
                                       ,UN_ERROR_COD =>PCK_ERRORES.ER_ALMACEN_UPDATE_TRANSDEV); 

        END; 
      END IF; 

  RETURN MI_RETORNO;

  /*EXCEPTION WHEN OTHERS THEN

      PCK_DATOS.GL_ERROR_MSG:= 'ERROR REALIZAR EL PASE DE LA TRANSACCIÓN';
      PCK_DATOS.GL_ERROR_MSG:= PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'FC_HACERPASE','',SQLERRM );
      RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG ); */  
END FC_HACERPASE;

PROCEDURE PR_GENERAPRESTADOS
    /*
    NAME              : 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 18/04/2016
    TIME              : 10:00 AM 
    SOURCE MODULE     : SYSMANPD2016.02.01
    MODIFIER			    : ELKIN GEOVANNY AMAYA SILVA
    DATE MODIFIED	    : 27/01/2017
    TIME				      : 5:48 PM
    MODIFICATIONS	    : Se cambió el estándar de codificación y llamados por referencia de las funciones.Se agregó manejo de excepciones
    DESCRIPTION       : CARGA A LA TABLA DE HORARIO_EXCLUIDO A PARTIR DE UN ELEMENTO A PRESTAR LOS DIAS PROPIOS EN QUE SE PRESTARON LOS PADRES Y ELEMENTOS
    @NAME:    generarPrestamos
    @METHOD:  POST    
  */
( 
 UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
 UN_FECHAINI      IN DATE,
 UN_FECHAFIN      IN DATE,
 UN_ELEMENTO      IN VARCHAR2,
 UN_SERIE         IN PCK_SUBTIPOS.TI_ENTERO,
 UN_ELEMENTOPAD   IN VARCHAR2,
 UN_SERIEPAD      IN PCK_SUBTIPOS.TI_ENTERO
 ) AS
  MI_TABLA       PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES     PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
  MI_STRSQL      PCK_SUBTIPOS.TI_STRSQL;
  RTA            PCK_SUBTIPOS.TI_ENTERO;
  MI_MSGERROR    PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN

   BEGIN 
    BEGIN
    MI_TABLA   :='HORARIO_EXCLUIDO'; 

    MI_CAMPOS  :=' COMPANIA
                 , ELEMENTO_DEVOLUTIVO
                 , SERIE_DEVOLUTIVO
                 , FECHA_HORA_INICIO
                 , FECHA_HORA_FIN
                 , SOLICITADO';

    MI_VALORES :=' SELECT HORARIO_SOLICITUD.COMPANIA, ''' || UN_ELEMENTO || ''',' || UN_SERIE || ', ' || CHR(13) || CHR(10) ||
                 '        HORARIO_SOLICITUD.FECHA_HORA_INICIO, ' || CHR(13) || CHR(10) ||
                 '        HORARIO_SOLICITUD.FECHA_HORA_FIN, ' || CHR(13) || CHR(10) ||
                 '        -1 ' || CHR(13) || CHR(10) ||
                 ' FROM  TIPO_TRANSACCION_DEV INNER JOIN HORARIO_SOLICITUD ' || CHR(13) || CHR(10) ||
                 '   ON  TIPO_TRANSACCION_DEV.COMPANIA = HORARIO_SOLICITUD.COMPANIA ' || CHR(13) || CHR(10) ||
                 '   AND TIPO_TRANSACCION_DEV.CODIGO   = HORARIO_SOLICITUD.TIPO_TRANSACCION ' || CHR(13) || CHR(10) ||
                 ' INNER JOIN D_TRANSACCION_DEV ' || CHR(13) || CHR(10) ||
                 '    ON HORARIO_SOLICITUD.COMPANIA           = D_TRANSACCION_DEV.COMPANIA' || CHR(13) || CHR(10) ||
                 '   AND HORARIO_SOLICITUD.CODIGO_TRANSACCION = D_TRANSACCION_DEV.CODIGO_TRANSACCION' || CHR(13) || CHR(10) ||
                 '   AND HORARIO_SOLICITUD.TIPO_TRANSACCION   = D_TRANSACCION_DEV.TIPO_TRANSACCION ' || CHR(13) || CHR(10) ||
                 ' WHERE TIPO_TRANSACCION_DEV.COMPANIA=''' || UN_COMPANIA || '''' || CHR(13) || CHR(10) ||
                 '   AND TIPO_TRANSACCION_DEV.CONFIRMA_PRESTAMO NOT IN(0) ' || CHR(13) || CHR(10) ||
                 '   AND D_TRANSACCION_DEV.ELEMENTO_DEVOLUTIVO =''' ||  UN_ELEMENTOPAD  || '''' || CHR(13) || CHR(10) ||
                 '   AND D_TRANSACCION_DEV.SERIE_DEVOLUTIVO    ='   ||  UN_SERIEPAD     || CHR(13) || CHR(10) ||
                 '   AND TO_DATE(TO_CHAR(HORARIO_SOLICITUD.FECHA_HORA_INICIO,''DD/MM/YYYY''),''DD/MM/YYYY'')<=TO_DATE(''' || TO_CHAR(UN_FECHAFIN,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ' || CHR(13) || CHR(10) ||
                 '   AND TO_DATE(TO_CHAR(HORARIO_SOLICITUD.FECHA_HORA_FIN   ,''DD/MM/YYYY''),''DD/MM/YYYY'')<=TO_DATE(''' || TO_CHAR(UN_FECHAINI,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ';

    RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA
                            ,UN_ACCION  => 'IS'
                            ,UN_CAMPOS  => MI_CAMPOS
                            ,UN_VALORES => MI_VALORES);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                 RAISE PCK_EXCEPCIONES.EXC_ALMACEN; 

    END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ALMACEN THEN 
                       MI_MSGERROR(1).CLAVE := 'TABLA';
                       MI_MSGERROR(1).VALOR := MI_TABLA;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD =>PCK_ERRORES.ERRR_ALMACEN_INSERT_TABLA
                                        ,UN_REEMPLAZOS => MI_MSGERROR
                                        ); 
  END;  

END PR_GENERAPRESTADOS;


END PCK_PRESTAMOS;