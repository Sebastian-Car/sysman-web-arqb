create or replace PACKAGE BODY "PCK_NOMINA_COM2" AS

--1
PROCEDURE PR_ACTUALIZARSUELDOS 
/*
  NAME              : putActualizarSueldos - PR_ACTUALIZARSUELDOS  --> EN ACCESS ACTUALIZARSUELDOS  -
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : JHONATAN ACELAS AREVALO
  DATE MIGRATION    : 09/07/2015
  TIME              : 03:25 PM
  SOURCE MODULE     :
  MODIFIER          : JOSE PASCUAL GÓMEZ BLANCO \ JAVIER ANDRES RODRIGUEZ RIOS / YESSICA SANA,
                      (30/01/2018) PABLO ANDRES ESPITIA CUCA
  DATE MODIFIED     : 12/01/2016 \ 08/02/2017 / 03/11/2017
  DESCRIPTION       : INSERTA SALARIOS CON PORCENTAJE DE INCREMENTO SALARIAL, PENSIONAL Y INTEGRAL
                      SE AJUSTA AL ESTANDAR/ SE AGREGAN CAMPOS DE AUDITORIA Y SE REALIZAN AJUSTES 
                      DE ACUERDO A INDICACIONES
                      (30/01/2018) Manejo de excepciones.
                                   Indentación de PLSQL.
  @NAME             : putActualizarSueldos
  @METHOD           : PUT
*/
(
  UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO_BASE               IN PCK_SUBTIPOS.TI_ANIO,
  UN_ANO_ACTUALIZAR         IN PCK_SUBTIPOS.TI_ANIO,
  UN_PORCENTAJE_INCREMENTO  IN PCK_SUBTIPOS.TI_PORCENTAJE,
  UN_PORCENTAJE_MESADA      IN PCK_SUBTIPOS.TI_PORCENTAJE,
  UN_USUARIO                IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_TABLA_C              PCK_SUBTIPOS.TI_TABLA DEFAULT 'CATEGORIA';
  MI_TABLA_CC             PCK_SUBTIPOS.TI_TABLA DEFAULT 'CENTRO_COSTO';
  MI_TABLA_P              PCK_SUBTIPOS.TI_TABLA DEFAULT 'PERSONAL';
  MI_REEMPLAZOS           PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_RTA                  PCK_SUBTIPOS.TI_RTA_ACME;
  MI_LISTCAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
  MI_USING                PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE          PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE          PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_MERGENOEXIS          PCK_SUBTIPOS.TI_MERGENOEXISTE;
  MI_CUENTA               NUMBER DEFAULT 0; --JM CC 3383
BEGIN

IF UN_PORCENTAJE_MESADA <> 0 THEN  --JM INI CC 3383 
    BEGIN
    
        SELECT COUNT(*) 
        INTO MI_CUENTA
        FROM SALUD_PENSIONADOS
        WHERE COMPANIA = UN_COMPANIA
          AND ANO = UN_ANO_ACTUALIZAR;

        IF MI_CUENTA = 0 THEN 
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END IF;

    EXCEPTION 

        WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            MI_REEMPLAZOS(1).CLAVE := 'ANIO';
            MI_REEMPLAZOS(1).VALOR := UN_ANO_ACTUALIZAR;
            
            PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_N_SALUD_PENSIONADOS,
                UN_REEMPLAZOS => MI_REEMPLAZOS,
                UN_TABLAERROR => MI_TABLA_P
            );
    END;
END IF; --JM FIN CC 3383

  MI_LISTCAMPOS := PCK_SYSMAN_UTL.FC_LISTA_CAMPOMERGE(UN_TABLA  => MI_TABLA_C
                                                     ,UN_EXCLUIDOS  => 'ANO,SALARIO_RETROACTIVO,VLR_INCREMENTO' /*@pespitia: No separar con enter el valor del campo UN_EXCLUIDOS*/
                                                     ,UN_TIPO       => 'IC');

  MI_USING := 'SELECT '||MI_LISTCAMPOS       || '
                     ,ROUND((SALARIO_BASE * (' || UN_PORCENTAJE_INCREMENTO || '/100)) + SALARIO_BASE,2) SALARIONUEVO
                     ,' || UN_ANO_ACTUALIZAR || ' ANO
                     ,0                           VLR_INCREMENTO
                     ,0                           SALARIO_RETROACTIVO 
               FROM CATEGORIA 
               WHERE COMPANIA = '''|| UN_COMPANIA ||'''
                 AND ANO      =   '|| UN_ANO_BASE;

  MI_MERGEENLACE := 'TABLA.COMPANIA        = VISTA.COMPANIA
                 AND TABLA.ESCALAFON       = VISTA.ESCALAFON                       
                 AND TABLA.ANO             = VISTA.ANO                       
                 AND TABLA.ID_DE_CATEGORIA = VISTA.ID_DE_CATEGORIA';      

  MI_LISTCAMPOS := PCK_SYSMAN_UTL.FC_LISTA_CAMPOMERGE(UN_TABLA     => MI_TABLA_C
                                                     ,UN_EXCLUIDOS => ' COMPANIA     ,ESCALAFON      ,ANO                ,ID_DE_CATEGORIA'||
                                                                      ',SALARIO_BASE ,SALARIOANTERIOR,SALARIO_RETROACTIVO,VLR_INCREMENTO '||
                                                                      ',DATE_MODIFIED,MODIFIED_BY'
                                                     ,UN_TIPO      => 'U'
                                                     ,UN_ALIASINI  => 'TABLA'
                                                     ,UN_ALIASFIN  => 'VISTA');

  MI_MERGEEXISTE := ' UPDATE SET '||MI_LISTCAMPOS||' 
                                ,TABLA.SALARIO_BASE        = VISTA.SALARIONUEVO 
                                ,TABLA.SALARIOANTERIOR     = '||CASE WHEN UN_PORCENTAJE_INCREMENTO = 0 
                                                                     THEN 'VISTA.SALARIOANTERIOR' 
                                                                     ELSE 'VISTA.SALARIO_BASE' 
                                                                END                              || ' 
                                ,TABLA.SALARIO_RETROACTIVO = 0
                                ,TABLA.VLR_INCREMENTO      = 0
                                ,TABLA.DATE_MODIFIED       = SYSDATE
                                ,TABLA.MODIFIED_BY         = ''' || UN_USUARIO || ''' 
                             WHERE ANO = ' || UN_ANO_ACTUALIZAR;

  MI_CAMPOS := PCK_SYSMAN_UTL.FC_LISTA_CAMPOMERGE(UN_TABLA     => MI_TABLA_C
                                                 ,UN_EXCLUIDOS => ' ANO            ,SALARIO_RETROACTIVO,VLR_INCREMENTO,SALARIO_BASE'||
                                                                  ',SALARIOANTERIOR,DATE_CREATED       ,CREATED_BY'
                                                 ,UN_TIPO      => 'IC') ||'
              ,ANO 
              ,SALARIO_RETROACTIVO 
              ,VLR_INCREMENTO
              ,SALARIO_BASE 
              ,SALARIOANTERIOR
              ,DATE_CREATED
              ,CREATED_BY';

  MI_VALORES := PCK_SYSMAN_UTL.FC_LISTA_CAMPOMERGE(UN_TABLA     => MI_TABLA_C
                                                  ,UN_EXCLUIDOS => ' ANO            ,SALARIO_RETROACTIVO,VLR_INCREMENTO,SALARIO_BASE'||
                                                                   ',SALARIOANTERIOR,DATE_CREATED       ,CREATED_BY'
                                                  ,UN_TIPO      => 'IV'
                                                  ,UN_ALIASFIN  => 'VISTA') ||'
               ,VISTA.ANO 
               ,VISTA.SALARIO_RETROACTIVO 
               ,VISTA.VLR_INCREMENTO 
               ,VISTA.SALARIONUEVO 
               ,VISTA.SALARIO_BASE
               ,SYSDATE
               , ''' || UN_USUARIO || '''';

  MI_MERGENOEXIS := ' INSERT( '|| MI_CAMPOS ||') VALUES(' || MI_VALORES || ')';

  BEGIN
    BEGIN 
      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA_C
                                  ,UN_ACCION      => 'IM'
                                  ,UN_MERGEUSING  => MI_USING
                                  ,UN_MERGEENLACE => MI_MERGEENLACE
                                  ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                  ,UN_MERGENOEXIS => MI_MERGENOEXIS);    

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
      RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
    MI_REEMPLAZOS(1).CLAVE := 'ANIO';
    MI_REEMPLAZOS(1).VALOR := UN_ANO_ACTUALIZAR;

    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                              ,UN_ERROR_COD  => PCK_ERRORES.ERR_N_MERGE_PRACTSUE_CATEGORIA
                              ,UN_TABLAERROR => MI_TABLA_C
                              ,UN_REEMPLAZOS => MI_REEMPLAZOS); 
  END;

  MI_CAMPOS:= PCK_SYSMAN_UTL.FC_LISTA_CAMPOMERGE(UN_TABLA     => MI_TABLA_CC
                                                ,UN_EXCLUIDOS => 'ANO'
                                                ,UN_TIPO      => 'IC');

  MI_USING := 'SELECT ' || MI_CAMPOS         || '
                     ,' || UN_ANO_ACTUALIZAR || ' ANO 
               FROM CENTRO_COSTO 
               WHERE COMPANIA = '''|| UN_COMPANIA ||'''
                 AND ANO      = '  || UN_ANO_BASE; 

  MI_MERGEENLACE := 'TABLA.COMPANIA = VISTA.COMPANIA  
                 AND TABLA.ANO      = VISTA.ANO
                 AND TABLA.CODIGO   = VISTA.CODIGO';  

  MI_LISTCAMPOS := PCK_SYSMAN_UTL.FC_LISTA_CAMPOMERGE(UN_TABLA     => MI_TABLA_CC
                                                     ,UN_EXCLUIDOS => 'COMPANIA,ANO,CODIGO,DATE_MODIFIED,MODIFIED_BY'
                                                     ,UN_TIPO      => 'U'
                                                     ,UN_ALIASINI  => 'TABLA'
                                                     ,UN_ALIASFIN  => 'VISTA');

  MI_MERGEEXISTE := ' UPDATE SET '||MI_LISTCAMPOS||'
                                 ,TABLA.DATE_MODIFIED = SYSDATE
                                 ,TABLA.MODIFIED_BY   = ''' || UN_USUARIO || ''' 
                      WHERE ANO = ' || UN_ANO_ACTUALIZAR;    

  MI_CAMPOS := PCK_SYSMAN_UTL.FC_LISTA_CAMPOMERGE(UN_TABLA     => MI_TABLA_CC
                                                 ,UN_EXCLUIDOS => 'ANO,DATE_CREATED,CREATED_BY'
                                                 ,UN_TIPO      => 'IC') || '
              ,ANO
              ,DATE_CREATED
              ,CREATED_BY';

  MI_VALORES := PCK_SYSMAN_UTL.FC_LISTA_CAMPOMERGE(UN_TABLA     => MI_TABLA_CC
                                                  ,UN_EXCLUIDOS => 'ANO,DATE_CREATED,CREATED_BY'
                                                  ,UN_TIPO      => 'IV'
                                                  ,UN_ALIASFIN  => 'VISTA') || '
               ,VISTA.ANO
               ,SYSDATE
               ,''' || UN_USUARIO || '''';

  MI_MERGENOEXIS := ' INSERT( '|| MI_CAMPOS ||') VALUES(' || MI_VALORES || ')';

  --Asegurar que los centros de costos existan
  BEGIN    
    BEGIN
      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA_CC
                                  ,UN_ACCION      => 'IM'
                                  ,UN_MERGEUSING  => MI_USING
                                  ,UN_MERGEENLACE => MI_MERGEENLACE
                                  ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                  ,UN_MERGENOEXIS => MI_MERGENOEXIS);    

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
      RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;  

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
    MI_REEMPLAZOS(1).CLAVE := 'ANIO';
    MI_REEMPLAZOS(1).VALOR := UN_ANO_ACTUALIZAR;

    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                              ,UN_ERROR_COD  => PCK_ERRORES.ERR_N_MERGE_PRACTSUE_CENTRCOST
                              ,UN_REEMPLAZOS => MI_REEMPLAZOS
                              ,UN_TABLAERROR => MI_TABLA_CC); 
  END;

  --@pespitia (31/01/2018): Se asigna el valor de REFERENCIA debido a que no permite la actualizacion por un error en los indices.
  MI_CAMPOS := 'ANO           = '||UN_ANO_ACTUALIZAR||' 
               ,REFERENCIA    = REFERENCIA
               ,DATE_MODIFIED = SYSDATE
               ,MODIFIED_BY   = '''||UN_USUARIO     ||'''';

  --(APINEDA:22/01/2019)-Se cambia condición porque se estaba excluyendo a los pensionados (estado actual 2) para la actualización del año en personal.
  MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
               AND ANO      =   '||UN_ANO_BASE||'                           
               AND ID_DE_EMPLEADO NOT IN (0)
               AND ESTADO_ACTUAL  NOT IN (3)'
               ;               

  BEGIN
    BEGIN 
      PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_P
                                          ,UN_ACCION    => 'M'
                                          ,UN_CAMPOS    => MI_CAMPOS
                                          ,UN_CONDICION => MI_CONDICION);

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
      RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;  

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
    MI_REEMPLAZOS(1).CLAVE := 'ANIOBASE';
    MI_REEMPLAZOS(1).VALOR := UN_ANO_BASE;
    MI_REEMPLAZOS(2).CLAVE := 'ANIONUEVO';
    MI_REEMPLAZOS(2).VALOR := UN_ANO_ACTUALIZAR;    

    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                              ,UN_ERROR_COD  => PCK_ERRORES.ERR_N_M_PRACTSUE_ANOPERSONAL
                              ,UN_REEMPLAZOS => MI_REEMPLAZOS
                              ,UN_TABLAERROR => MI_TABLA_P); 
  END;

  IF UN_PORCENTAJE_MESADA NOT IN(0) THEN
    -- TICKET 7740133 EFCM: SE ADICIONA CAMPOS PARA REPORTE AL ACTUALIZAR MESADA PENSIONAL POR ACTUALIZAR SUELDOS,
    --                      SE INCREMENTA VR_MESPENSION2421 Y SE ENCUENTRA RIESGO_PENSION EN LA TABLA SALUD_PENSIONADOS
    MI_CAMPOS := 'MESADA_PENSIONAL_ANT = MESADA_PENSIONAL
                 ,RIESGO_PENSION_ANT = RIESGO_PENSION
                 ,ANO_MESADA_ANT =  ' || UN_ANO_BASE || '
                 ,VR_MESPENSION2421_ANT = VR_MESPENSION2421
                 ,PORC_INC_MESADA = ' || UN_PORCENTAJE_MESADA || '
                 ,MESADA_PENSIONAL = MESADA_PENSIONAL + ROUND(MESADA_PENSIONAL * (' || UN_PORCENTAJE_MESADA || ')/100, 0)
                 ,SALARIO_BASE_IBC = MESADA_PENSIONAL + ROUND(MESADA_PENSIONAL * (' || UN_PORCENTAJE_MESADA || ')/100, 0)
                 ,VR_MESPENSION2421 = VR_MESPENSION2421 + ROUND(VR_MESPENSION2421 * (' || UN_PORCENTAJE_MESADA || ')/100, 0)
                 ,ANO              =   '||UN_ANO_ACTUALIZAR||'
                 ,RIESGO_PENSION = ( SELECT MAX(PORC_MAX) FROM (SELECT SALUD_PENSIONADOS.PORCENTAJE_MAX PORC_MAX FROM SALUD_PENSIONADOS WHERE SALUD_PENSIONADOS.COMPANIA = PERSONAL.COMPANIA AND SALUD_PENSIONADOS.ANO = ' || UN_ANO_ACTUALIZAR || '   AND ( NVL(VR_MESPENSION2421,MESADA_PENSIONAL) + ROUND( NVL(VR_MESPENSION2421,MESADA_PENSIONAL) * (' || UN_PORCENTAJE_MESADA || ')/100, 0)) BETWEEN LIMITE_INFERIOR AND LIMITE_SUPERIOR UNION ALL SELECT 0 FROM DUAL ))
                 ,DATE_MODIFIED    = SYSDATE
                 ,MODIFIED_BY      = '''||UN_USUARIO       ||'''';
    -- TICKET 7740133 FIN --

    MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                 AND ANO      =   '||UN_ANO_ACTUALIZAR||'   
                 AND ID_DE_EMPLEADO NOT IN (0)
                 AND ESTADO_ACTUAL  = 2
                 AND FECHA_DE_RETIRO IS NULL';

    --Por revision en la ultima version de access no se encuentra la validacion de  AND PERSONAL.TIPO_PENSIONADO <> ''I'' por indicaciones se quita.
    BEGIN
      BEGIN  
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_P
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
      END;    

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
      MI_REEMPLAZOS(1).CLAVE := 'ANIO';
      MI_REEMPLAZOS(1).VALOR := UN_ANO_ACTUALIZAR; 

      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_N_M_PRACTSUE_MESADAPENSION
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS
                                ,UN_TABLAERROR => MI_TABLA_P); 
    END;
  END IF;
END PR_ACTUALIZARSUELDOS;

FUNCTION FC_VALIDAANIOMESADA
/*
    NAME              : FC_VALIDAANIOMESADA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDWIN FERNANDO CABRERA MARTINEZ
    DATE MIGRADOR     : 31/01/2024
    TIME              : 5:17 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : RETORNA BOOLEANA SI EXISTEN FUNCIONARIOS EN PERSONAL CON EL ESTADO DEL PARAMETRO (2 PARA PENSIONADOS Y 1 ACTIVOS)
                        EN EL AÑO DEL PARAMETRO
    @NAME             : 
    @METHOD           : 
    TICKET            : 7741636 EFCM
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_ESTADO   IN PERSONAL.ESTADO_ACTUAL%TYPE
  ) 
RETURN PCK_SUBTIPOS.TI_LOGICO
AS 
  MI_CANTIDAD   PCK_SUBTIPOS.TI_ENTERO_LARGO:=0;
BEGIN
    SELECT DISTINCT 1
      INTO MI_CANTIDAD
      FROM PERSONAL
     WHERE COMPANIA = UN_COMPANIA
           AND ANO = UN_ANO
           AND ID_DE_EMPLEADO NOT IN (0)
           AND ((UN_ESTADO = 2 AND ESTADO_ACTUAL = 2 AND FECHA_DE_RETIRO IS NULL) 
                OR (UN_ESTADO != 2 AND ESTADO_ACTUAL != 3 ))
            ;
    RETURN 1;
EXCEPTION WHEN NO_DATA_FOUND THEN 
    RETURN 0;
END FC_VALIDAANIOMESADA;

FUNCTION FC_REVMESADA
/*
    NAME              : FC_REVMESADA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDWIN FERNANDO CABRERA MARTINEZ
    DATE MIGRADOR     : 31/01/2024
    TIME              : 5:17 PM
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : REALIZA EL PROCESO DE REVERSAR ULTIMO INCREMENTO MESADA PENSIONAL RETORNA 1 SI FUE EXITOSO, 0 SI NO LO FUE
    @NAME             : 
    @METHOD           : 
    TICKET            : 7741636 EFCM
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO
  ) 
RETURN PCK_SUBTIPOS.TI_LOGICO
AS 
  MI_CANTIDAD   PCK_SUBTIPOS.TI_ENTERO_LARGO:=0;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA                  PCK_SUBTIPOS.TI_RTA_ACME;
BEGIN
    SELECT DISTINCT 1
      INTO MI_CANTIDAD
      FROM PERSONAL
     WHERE COMPANIA = UN_COMPANIA
           AND ID_DE_EMPLEADO NOT IN (0)
           AND ANO_MESADA_ANT IS NOT NULL
           AND ESTADO_ACTUAL = 2 
           AND FECHA_DE_RETIRO IS NULL;
                
    -- PROCESO REVERSION INCREMENTO MESADA
    MI_CAMPOS := 'MESADA_PENSIONAL = MESADA_PENSIONAL_ANT
    			,SALARIO_BASE_IBC = MESADA_PENSIONAL_ANT
                ,RIESGO_PENSION = RIESGO_PENSION_ANT
                ,ANO = ANO_MESADA_ANT
                ,VR_MESPENSION2421 = VR_MESPENSION2421_ANT
                ,PORC_INC_MESADA = NULL
                ,MESADA_PENSIONAL_ANT = NULL
                ,RIESGO_PENSION_ANT = NULL
                ,ANO_MESADA_ANT = NULL
                ,VR_MESPENSION2421_ANT = NULL
                ,DATE_MODIFIED    = SYSDATE
                ,MODIFIED_BY      = '''||UN_USUARIO       ||'''';
    
    MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                AND ID_DE_EMPLEADO NOT IN (0)
                AND ANO_MESADA_ANT IS NOT NULL
                AND ESTADO_ACTUAL = 2 
                AND FECHA_DE_RETIRO IS NULL';

    BEGIN
      BEGIN  
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'PERSONAL'
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS    => MI_CAMPOS
                                   ,UN_CONDICION => MI_CONDICION);

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
      END;    

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_REV_MESADA
                                    ,UN_TABLAERROR => 'PERSONAL');
    END;
    RETURN 1;
EXCEPTION WHEN NO_DATA_FOUND THEN 
    RETURN 0;
END FC_REVMESADA;

--2
FUNCTION FC_EXISTE_CATEGORIA_ANO
/*
    NAME              : FC_EXISTE_CATEGORIA_ANO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JHONATAN ACELAS AREVALO
    DATE MIGRADOR     : 9/07/2015
    TIME              : 5:17 PM
    SOURCE MODULE     : 
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 09/02/2017
    TIME              : 
    DESCRIPTION       : RETORNA NUMERO DE REGISTROS DE LA TABLA CATEGORIA FILTRANDO POR COMPANIA Y ANO
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getCategoriaAnio
    @METHOD           : get 
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO
  ) 
RETURN PCK_SUBTIPOS.TI_ENTERO_LARGO
  AS 
    MI_REGISTROS  PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN
  --  ' RETORNA NUMERO DE REGISTROS DONDE CATEGORIA Y ANO
  SELECT COUNT(COMPANIA) 
    INTO MI_REGISTROS  
    FROM CATEGORIA
   WHERE COMPANIA = UN_COMPANIA
     AND ANO      = UN_ANO;

 RETURN MI_REGISTROS;  
END FC_EXISTE_CATEGORIA_ANO;

--3
PROCEDURE PR_ELIMINARDECATEGORIA
/*
    NAME              : PR_ELIMINARDECATEGORIA  --> EN ACCESS ACTUALIZARSUELDOS  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JHONATAN ACELAS AREVALO
    DATE MIGRADOR     : 10/07/2015
    TIME              : 09:30 PM
    SOURCE MODULE     :
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 09/02/2017
    TIME              : 
    DESCRIPTION       : ELIMINA REGISTROS DE LA TABLA CATEGORIA DONE ANO = PARAMETRO Y COMPANIA =  PARAMETRO
                        SE AJUSTA AL ESTANDAR
    @NAME             : eliminarCategoria
    @METHOD           : delete
  */
  (
    UN_ANO_BASE IN PCK_SUBTIPOS.TI_ANIO,
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA
  )
  AS
    MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
BEGIN
  MI_CONDICION    := 'COMPANIA = ''' || UN_COMPANIA 
                     || ''' AND ANO = ' || UN_ANO_BASE ;

  BEGIN 
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                  UN_TABLA     => 'CATEGORIA'
                                 ,UN_ACCION    => 'E'
                                 ,UN_CONDICION => MI_CONDICION );
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
    RAISE PCK_EXCEPCIONES.EXC_NOMINA;
  END;
 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
   PCK_ERR_MSG.RAISE_WITH_MSG(
       UN_EXC_COD    => SQLCODE
      ,UN_ERROR_COD  => PCK_ERRORES.ERRR_NOMINA_ELIMINARDECATEG
      ,UN_TABLAERROR => 'CATEGORIA'
    ); 
END PR_ELIMINARDECATEGORIA;

--3
FUNCTION FC_INCLUIR_NOVEDAD_ENCARGOS
/*
    NAME              : PR_INCLUIR_NOVEDAD_ENCARGOS  --> EN ACCESS INCLUIR_NOVEDAD_ENCARGOS  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JHONATAN ACELAS AREVALO
    DATE MIGRADOR     : 13/07/2015
    TIME              : 03:06 PM
    SOURCE MODULE     :
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS 
    DATE MODIFIED     : \ 09/02/2017
    TIME              : 
    DESCRIPTION       : FUNCION QUE INSERTA LAS NOVEDADES EN LA TABLA, CON BASE A LOS ENCARGOS DE ANO MES Y PERIODO QUE IGRESAN POR PARAMETRO
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getIncluirNovedadEncargos
    @METHOD           : get
  */
  (
    UN_MES         IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO     IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ANO         IN PCK_SUBTIPOS.TI_ANIO,
    UN_PROCESO     IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIO IN DATE,
    UN_FECHAFIN    IN DATE,
    UN_USER        IN PCK_SUBTIPOS.TI_USUARIO,  
    UN_ID_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO DEFAULT 0
  ) 
    RETURN PCK_SUBTIPOS.TI_ENTERO_LARGO
  AS
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_FECHAINICIO    DATE;
    MI_FECHAFINAL     DATE;
    MI_FECHAFIN       DATE;
    MI_FECHAINI       DATE;
    MI_DIAS           PCK_SUBTIPOS.TI_ENTERO       :=0;
    MI_SALARIO        PCK_SUBTIPOS.TI_ENTERO_LARGO :=0;
    MI_GR             PCK_SUBTIPOS.TI_ENTERO_LARGO :=0;
    MI_INDSUMA        PCK_SUBTIPOS.TI_LOGICO       :=0;
    MI_CANTEC         PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CANTT          PCK_SUBTIPOS.TI_ENTERO_LARGO :=0;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_SUBCONDICION   PCK_SUBTIPOS.TI_CONDICION;
    MI_SUBCONDICION_1 PCK_SUBTIPOS.TI_CONDICION;
    MI_SUBCONDICION_2 PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS1        PCK_SUBTIPOS.TI_CAMPOS;
    MI_CN11           NUMBER; 
    MI_CN10           NUMBER;  
    MI_VALAUX         NUMBER;
    MI_ACCION         VARCHAR(20);
BEGIN

    IF UN_ID_EMPLEADO <> 0 THEN 
        MI_SUBCONDICION   := 'AND ID_DE_EMPLEADO = '||UN_ID_EMPLEADO;
        MI_SUBCONDICION_1 := UN_ID_EMPLEADO;
        MI_SUBCONDICION_2 := UN_ID_EMPLEADO;
        MI_FECHAFIN := LAST_DAY(UN_FECHAFIN);
        MI_FECHAINI := TO_DATE('01/' || TO_CHAR(UN_FECHAFIN,'MM/YYYY'),'DD/MM/YYYY');
    ELSE 
        MI_SUBCONDICION   := '';
        MI_SUBCONDICION_1 := '0';
        MI_SUBCONDICION_2 := '99999';
        MI_FECHAFIN := UN_FECHAFIN;
        MI_FECHAINI := UN_FECHAINICIO;
    END IF;

     MI_CONDICION := 'COMPANIA      = '''|| UN_COMPANIA ||'''
                 AND ID_DE_PROCESO = '  || UN_PROCESO ||'
                 AND ANO           = '  || UN_ANO ||'
                 AND MES           = '  || UN_MES ||'
                 AND PERIODO       = '  || UN_PERIODO ||'
                                     '  ||MI_SUBCONDICION||'
                 AND ID_DE_CONCEPTO IN (11,10,15)';
    BEGIN
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'NOVEDADES',
                                   UN_ACCION    => 'E',
                                   UN_CONDICION => MI_CONDICION);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
        RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END; 
    
  <<RECORREENCARGOS>>
  FOR RS IN (SELECT   ENCARGOS.COMPANIA
                    , ENCARGOS.ID_DE_EMPLEADO
                    , ENCARGOS.ESCALAFON
                    , ENCARGOS.ID_DE_CARGO
                    , ENCARGOS.ID_DE_CATEGORIA
                    , ENCARGOS.FECHAINICIO
                    , ENCARGOS.FECHAFINAL
                    , ENCARGOS.SUELDOMENSUAL
                    , ENCARGOS.ACTO
                    , ENCARGOS.FECHAACTO
                    , ENCARGOS.FECHAPAGO
                    , CATEGORIA.ANO
                    , CATEGORIA.SALARIO_BASE
                    , CATEGORIA.GASTOSREPRESENTACION
              FROM ENCARGOS 
                LEFT JOIN PERSONAL 
                  ON  ENCARGOS.COMPANIA       = PERSONAL.COMPANIA
                  AND ENCARGOS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                LEFT JOIN CATEGORIA 
                  ON  ENCARGOS.COMPANIA        = CATEGORIA.COMPANIA
                  AND ENCARGOS.ESCALAFON       = CATEGORIA.ESCALAFON
                  AND ENCARGOS.ID_DE_CATEGORIA = CATEGORIA.ID_DE_CATEGORIA
             WHERE ENCARGOS.COMPANIA        = UN_COMPANIA
               AND ENCARGOS.FECHAINICIO    <= MI_FECHAFIN
               AND ENCARGOS.FECHAFINAL     >= MI_FECHAINI
               AND CATEGORIA.ANO            =UN_ANO
               AND ENCARGOS.ID_DE_EMPLEADO BETWEEN MI_SUBCONDICION_1 AND MI_SUBCONDICION_2
            ORDER BY  ENCARGOS.ID_DE_EMPLEADO
                     ,ENCARGOS.FECHAINICIO
            )
  LOOP
    SELECT COUNT(*) 
      INTO MI_CANTEC
     FROM ENCARGOS 
       LEFT JOIN PERSONAL 
         ON  ENCARGOS.COMPANIA        = PERSONAL.COMPANIA  
         AND ENCARGOS.ID_DE_EMPLEADO  = PERSONAL.ID_DE_EMPLEADO 
       LEFT JOIN CATEGORIA 
         ON  ENCARGOS.COMPANIA        = CATEGORIA.COMPANIA 
         AND ENCARGOS.ESCALAFON       = CATEGORIA.ESCALAFON 
         AND ENCARGOS.ID_DE_CATEGORIA = CATEGORIA.ID_DE_CATEGORIA 
    WHERE ENCARGOS.COMPANIA         = UN_COMPANIA
      AND ENCARGOS.ID_DE_EMPLEADO   = RS.ID_DE_EMPLEADO
      AND ENCARGOS.FECHAINICIO     <= MI_FECHAFIN
      AND ENCARGOS.FECHAFINAL      >= MI_FECHAINI
      AND CATEGORIA.ANO             = UN_ANO
    ORDER BY ENCARGOS.ID_DE_EMPLEADO
            ,ENCARGOS.FECHAINICIO;
    <<RECORREENCARGOSCUENTA>>
    FOR RS2 IN (SELECT  ENCARGOS.COMPANIA
                       ,ENCARGOS.ID_DE_EMPLEADO
                       ,ENCARGOS.ESCALAFON
                       ,ENCARGOS.ID_DE_CARGO
                       ,ENCARGOS.ID_DE_CATEGORIA
                       ,ENCARGOS.FECHAINICIO
                       ,ENCARGOS.FECHAFINAL
                       ,ENCARGOS.SUELDOMENSUAL
                       ,ENCARGOS.ACTO
                       ,ENCARGOS.FECHAACTO
                       ,ENCARGOS.FECHAPAGO
                       ,CATEGORIA.ANO
                       ,CATEGORIA.SALARIO_BASE
                       ,CATEGORIA.GASTOSREPRESENTACION
                FROM ENCARGOS 
                  LEFT JOIN PERSONAL 
                    ON  ENCARGOS.COMPANIA        = PERSONAL.COMPANIA
                    AND ENCARGOS.ID_DE_EMPLEADO  = PERSONAL.ID_DE_EMPLEADO 
                  LEFT JOIN CATEGORIA 
                    ON  ENCARGOS.COMPANIA        = CATEGORIA.COMPANIA
                    AND ENCARGOS.ESCALAFON       = CATEGORIA.ESCALAFON
                    AND ENCARGOS.ID_DE_CATEGORIA = CATEGORIA.ID_DE_CATEGORIA 
                WHERE  ENCARGOS.COMPANIA        = UN_COMPANIA
                  AND  ENCARGOS.ID_DE_EMPLEADO  = RS.ID_DE_EMPLEADO
                  AND  ENCARGOS.FECHAINICIO    <= MI_FECHAFIN
                  AND  ENCARGOS.FECHAFINAL     >= MI_FECHAINI
                  AND  CATEGORIA.ANO            = UN_ANO
                  OR   ENCARGOS.COMPANIA        = UN_COMPANIA
                  AND  ENCARGOS.ID_DE_EMPLEADO  = RS.ID_DE_EMPLEADO
                  AND  ENCARGOS.FECHAINICIO    <= MI_FECHAFIN
                  AND  ENCARGOS.FECHAFINAL     >= MI_FECHAINI
                  AND  CATEGORIA.ANO            = UN_ANO
                ORDER BY  ENCARGOS.ID_DE_EMPLEADO
                         ,ENCARGOS.FECHAINICIO
                )
    LOOP
      IF RS2.FECHAINICIO >= MI_FECHAINI
         AND RS2.FECHAINICIO <= MI_FECHAFIN THEN
        MI_FECHAINICIO := RS2.FECHAINICIO;
      ELSIF (RS2.FECHAINICIO<= MI_FECHAINI) THEN
        MI_FECHAINICIO:= MI_FECHAINI;
      ELSE 
        MI_FECHAINICIO:=RS2.FECHAINICIO;
      END IF;

      IF RS2.FECHAFINAL >= MI_FECHAINI 
         AND RS2.FECHAFINAL <= MI_FECHAFIN THEN
        MI_FECHAFINAL:= RS2.FECHAFINAL;
      ELSIF RS2.FECHAFINAL > MI_FECHAFIN THEN
        MI_FECHAFINAL := MI_FECHAFIN;
      ELSE
        MI_FECHAFINAL:= RS2.FECHAFINAL;
      END IF;
      IF TO_CHAR(MI_FECHAFINAL,'DD')=31 THEN
        MI_FECHAFINAL := MI_FECHAFINAL -1;
      END IF;

      MI_DIAS:=0;
      MI_SALARIO:=0;
      MI_GR:=0;
      MI_DIAS:= (MI_FECHAFINAL- MI_FECHAINICIO)+1;

      /* IF( TO_CHAR(MI_FECHAFINAL,'DD')=28 )THEN
        MI_DIAS:= MI_DIAS+2;
      ELSIF ( TO_CHAR(MI_FECHAFINAL,'DD')=28 )THEN
        MI_DIAS := MI_DIAS + 1 ; 
      ELSIF ( MI_DIAS < 0  ) THEN
        MI_DIAS := 0 ;
      END IF; */


      IF (PCK_SYSMAN_UTL.FC_MES(MI_FECHAFINAL) = 2 AND (PCK_SYSMAN_UTL.FC_DIA(MI_FECHAFINAL) = PCK_SYSMAN_UTL.FC_DIA(LAST_day(MI_FECHAFINAL))) AND PCK_SYSMAN_UTL.FC_DIA(LAST_day(MI_FECHAFINAL)) = 28 ) THEN
        MI_DIAS:= MI_DIAS+2;
      ELSIF (PCK_SYSMAN_UTL.FC_MES(MI_FECHAFINAL) = 2 AND (PCK_SYSMAN_UTL.FC_DIA(MI_FECHAFINAL) = PCK_SYSMAN_UTL.FC_DIA(LAST_day(MI_FECHAFINAL))) AND PCK_SYSMAN_UTL.FC_DIA(LAST_day(MI_FECHAFINAL)) = 29 ) THEN
        MI_DIAS := MI_DIAS + 1 ; 
      ELSIF ( MI_DIAS < 0  ) THEN
        MI_DIAS := 0 ;
      END IF;

      MI_SALARIO := NVL(RS2.SALARIO_BASE,0);
      MI_GR := NVL(RS2.GASTOSREPRESENTACION,0);
      IF (MI_DIAS > 0) THEN
        IF MI_INDSUMA NOT IN (0)  
           AND MI_CANTEC  > 1 THEN
           
           -- TICKET 7731971 EFCM: PRORATEAR CONCEPTOS DE ENCARGOS CUANDO EXISTEN MAS DE UNO EN EL MISMO MES
           MI_VALAUX := MI_SALARIO;
           MI_ACCION := '1';
           --IF ( PCK_PARST.FC_PAR('PRORRATEAR SI EXISTE MAS DE UN ENCARGO EN EL PERIODO', 'NO')  = 'NO' ) THEN
               SELECT  SUM(DECODE(ID_DE_CONCEPTO,11,VALOR,0)), SUM(DECODE(ID_DE_CONCEPTO,10,VALOR,0))
               INTO    MI_CN11, MI_CN10
               FROM    NOVEDADES
               WHERE   COMPANIA = UN_COMPANIA
                       AND ID_DE_PROCESO = UN_PROCESO
                       AND ANO = UN_ANO
                       AND MES = UN_MES
                       AND PERIODO = UN_PERIODO
                       AND ID_DE_EMPLEADO = RS2.ID_DE_EMPLEADO
                       AND ID_DE_CONCEPTO IN (11, 10);
                
                IF ( NVL( MI_CN10,0) = 0 ) THEN  
                    MI_VALAUX := MI_SALARIO;
                    MI_ACCION := '1';
                ELSE
                    MI_VALAUX := CEIL(((MI_CN10*MI_CN11)+(MI_SALARIO*MI_DIAS))/(MI_CN11+MI_DIAS));
                    MI_ACCION := NULL;
                END IF;
            --END IF;
            -- TICKET 7731971 FIN --
            
           
          PCK_NOMINA.PR_INCLUIRNOVEDAD(
                     UN_COMPANIA   => UN_COMPANIA
                    ,UN_PROCESO    => UN_PROCESO
                    ,UN_ANIO       => UN_ANO
                    ,UN_MES        => UN_MES
                    ,UN_PERIODO    => UN_PERIODO
                    ,UN_IDEMPLEADO => RS2.ID_DE_EMPLEADO
                    ,UN_IDCONCEPTO => 11
                    ,UN_VALOR      => MI_DIAS
                    ,UN_ACCION     => '1');
          PCK_NOMINA.PR_INCLUIRNOVEDAD(
                     UN_COMPANIA   => UN_COMPANIA
                    ,UN_PROCESO    => UN_PROCESO
                    ,UN_ANIO       => UN_ANO
                    ,UN_MES        => UN_MES
                    ,UN_PERIODO    => UN_PERIODO
                    ,UN_IDEMPLEADO => RS2.ID_DE_EMPLEADO
                    ,UN_IDCONCEPTO => 10
                    ,UN_VALOR      => MI_VALAUX --MI_SALARIO
                    ,UN_ACCION     => MI_ACCION -- '1'
                    );
          PCK_NOMINA.PR_INCLUIRNOVEDAD(
                     UN_COMPANIA   => UN_COMPANIA
                    ,UN_PROCESO    => UN_PROCESO
                    ,UN_ANIO       => UN_ANO
                    ,UN_MES        => UN_MES
                    ,UN_PERIODO    => UN_PERIODO
                    ,UN_IDEMPLEADO => RS2.ID_DE_EMPLEADO
                    ,UN_IDCONCEPTO => 15
                    ,UN_VALOR      => MI_GR
                    ,UN_ACCION     => '1');
        ELSE 
          PCK_NOMINA.PR_INCLUIRNOVEDAD(
                     UN_COMPANIA   => UN_COMPANIA
                    ,UN_PROCESO    => UN_PROCESO
                    ,UN_ANIO       => UN_ANO
                    ,UN_MES        => UN_MES
                    ,UN_PERIODO    => UN_PERIODO
                    ,UN_IDEMPLEADO => RS2.ID_DE_EMPLEADO
                    ,UN_IDCONCEPTO => 11
                    ,UN_VALOR      => MI_DIAS);
          PCK_NOMINA.PR_INCLUIRNOVEDAD(
                     UN_COMPANIA   => UN_COMPANIA
                    ,UN_PROCESO    => UN_PROCESO
                    ,UN_ANIO       => UN_ANO
                    ,UN_MES        => UN_MES
                    ,UN_PERIODO    => UN_PERIODO
                    ,UN_IDEMPLEADO => RS2.ID_DE_EMPLEADO
                    ,UN_IDCONCEPTO => 10
                    ,UN_VALOR      => MI_SALARIO);
          PCK_NOMINA.PR_INCLUIRNOVEDAD(
                     UN_COMPANIA   => UN_COMPANIA
                    ,UN_PROCESO    => UN_PROCESO
                    ,UN_ANIO       => UN_ANO
                    ,UN_MES        => UN_MES
                    ,UN_PERIODO    => UN_PERIODO
                    ,UN_IDEMPLEADO => RS2.ID_DE_EMPLEADO
                    ,UN_IDCONCEPTO => 15
                    ,UN_VALOR      => MI_GR);
          IF MI_CANTEC > 1 THEN 
            MI_INDSUMA := -1;
          ELSE 
            MI_INDSUMA := 0;
          END IF;
        END IF;
        MI_CANTT := MI_CANTT +1;
      END IF;
    END LOOP RECORREENCARGOSCUENTA;
    MI_INDSUMA :=0;
  END LOOP RECORREENCARGOS;
  
  --VALIDA QUE PERSONAL NO TIENE ENCARGO EN EL MES PARA LIMPIAR EL CAMPO CARGO_ENCARGO DE LA TABLA PERSONAL  - TICKET 7721464  
  
 FOR RS3 IN (SELECT PERSONAL.ID_DE_EMPLEADO, ENCARGOMES.ID_DE_CARGO AS ID_ENCARGO
                     FROM PERSONAL 
                     LEFT JOIN (SELECT * FROM                   
                     ENCARGOS 
                     WHERE ENCARGOS.COMPANIA       = UN_COMPANIA
                     AND ENCARGOS.FECHAINICIO      <= MI_FECHAFIN
                     AND ENCARGOS.FECHAFINAL       >= MI_FECHAINI) ENCARGOMES
                     ON  ENCARGOMES.COMPANIA       = PERSONAL.COMPANIA
                     AND ENCARGOMES.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                     WHERE PERSONAL.COMPANIA = UN_COMPANIA
                     AND PERSONAL.ID_DE_EMPLEADO BETWEEN MI_SUBCONDICION_1 AND MI_SUBCONDICION_2
                     AND (PERSONAL.ESTADO_ACTUAL = 1  
                     OR (PERSONAL.ESTADO_ACTUAL = 3 AND PERSONAL.FECHA_DE_RETIRO BETWEEN MI_FECHAINI AND MI_FECHAFIN)))
    LOOP

    MI_CAMPOS1    :='CARGO_ENCARGO = ''' || RS3.ID_ENCARGO || '''';
    MI_CONDICION :='COMPANIA       =''' || UN_COMPANIA || ''' AND
                 ID_DE_EMPLEADO  ='   || RS3.ID_DE_EMPLEADO;
                 
    
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (  
                                  UN_TABLA     => 'PERSONAL'
                                 ,UN_ACCION    => 'M'
                                 ,UN_CAMPOS    => MI_CAMPOS1
                                 ,UN_CONDICION => MI_CONDICION );
    
   END LOOP ACTUALIZA;
   
    IF UN_ID_EMPLEADO <> 0 THEN
        MI_CANTT := -1;
        PCK_NOMINA_COM10.PR_NOVEDADES_SIN_ENCARGOS(UN_COMPANIA,UN_ANO,UN_MES,UN_PERIODO,UN_ID_EMPLEADO,UN_ID_EMPLEADO);
    END IF;
     /*MROSERO CC475*/
    PCK_NOMINA_COM2.PR_INCLUIR_LICENCIAS(
                   UN_MES   => UN_MES
                  ,UN_PERIODO    => UN_PERIODO
                  ,UN_ANO       => UN_ANO
                  ,UN_PROCESO        => UN_PROCESO
                  ,UN_COMPANIA    => UN_COMPANIA
                  ,UN_FECHAINICIO => UN_FECHAINICIO
                  ,UN_FECHAFIN => UN_FECHAFIN
                  ,UN_USER      => UN_USER
                  ,UN_ID_EMPLEADO  =>    UN_ID_EMPLEADO);
 RETURN MI_CANTT;
END FC_INCLUIR_NOVEDAD_ENCARGOS;

--4
FUNCTION FC_INCLUIR_NOVEDAD_LICENCIAS
/*
NAME              : INCLUIR_NOVEDAD_LICENCIAS  --> EN ACCESS INCLUIR_NOVEDAD_LICENCIAS  -
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : JHONATAN ACELAS AREVALO
DATE MIGRADOR     : 14/07/2015
TIME              : 02:39 PM
SOURCE MODULE     :
MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
DATE MODIFIED     : \ 09/02/2017
TIME              :
DESCRIPTION       : FUNCION ENCARGADA DE INCLUIR LAS NOVEDADES DE LAS LICENCIAS EN LA TABLA.
SE AJUSTA AL ESTANDAR.
@NAME             : getIncluirNovedadLicencias
@METHOD           : GET
*/
(
UN_MES         IN PCK_SUBTIPOS.TI_MES,
UN_PERIODO     IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
UN_ANO         IN PCK_SUBTIPOS.TI_ANIO,
UN_PROCESO     IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
UN_FECHAINICIO IN DATE,
UN_FECHAFIN    IN DATE,
UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_ENTERO_LARGO
AS
    --MI_ERROR_FUN     NUMBER:=GL_ERROR_NUM + 5;
    MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES       PCK_SUBTIPOS.TI_VALORES;
    MI_FECHAINICIO   DATE;
    MI_FECHAFINAL    DATE;
    MI_DIAS          PCK_SUBTIPOS.TI_ENTERO       :=0;
    MI_SALARIO       PCK_SUBTIPOS.TI_ENTERO_LARGO :=0;
    MI_GR            PCK_SUBTIPOS.TI_ENTERO_LARGO :=0;
    MI_INDSUMA       PCK_SUBTIPOS.TI_LOGICO       :=0;
    MI_CANTEC        PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CANTT         PCK_SUBTIPOS.TI_ENTERO_LARGO :=0;
    MI_CONCEPTO      PCK_SUBTIPOS.TI_ID_DE_CONCEPTO;
    MI_ACCION        VARCHAR2(20);
    MI_CONDICION     VARCHAR2(500);
    MI_HABILES       BOOLEAN DEFAULT FALSE; --JM 05/03/2026 CC 3444

BEGIN
    MI_HABILES := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(
                    UN_COMPANIA  => UN_COMPANIA
                   ,UN_NOMBRE    => 'LIQUIDAR VACACIONES Y LR EN BASE A DIAS HABILES'
                   ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                   ,UN_FECHA_PAR => SYSDATE),'NO') = 'SI' THEN TRUE ELSE FALSE END; -- JM CC 3444
    MI_DIAS:=0;
    MI_SALARIO:=0;
    MI_GR:=0;
    MI_INDSUMA := 0;
    <<RECORRELICENCIAS>>
    FOR RS IN (
        SELECT  LICENCIAS.COMPANIA
               ,LICENCIAS.ID_DE_PROCESO
               ,LICENCIAS.ID_DE_EMPLEADO
               ,LICENCIAS.FECHA_INICIO
               ,LICENCIAS.FECHA_FINAL
               ,LICENCIAS.NUMERO_ACTO
               ,LICENCIAS.FECHA_ACTO
               ,LICENCIAS.LICENCIA
               ,TIPOS_LICENCIA.ID_DE_CONCEPTO
        FROM LICENCIAS
             LEFT JOIN PERSONAL
             ON  LICENCIAS.COMPANIA       = PERSONAL.COMPANIA
             AND LICENCIAS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
             LEFT JOIN TIPOS_LICENCIA
             ON LICENCIAS.COMPANIA = TIPOS_LICENCIA.COMPANIA
             AND LICENCIAS.LICENCIA = TIPOS_LICENCIA.LICENCIA
        WHERE (LICENCIAS.COMPANIA       = UN_COMPANIA
          AND LICENCIAS.FECHA_INICIO  <= UN_FECHAFIN
          AND LICENCIAS.FECHA_FINAL   >= UN_FECHAINICIO)
          OR  (LICENCIAS.COMPANIA       = UN_COMPANIA
          AND LICENCIAS.FECHA_FINAL   >= UN_FECHAINICIO)
          ORDER BY LICENCIAS.ID_DE_EMPLEADO
    )
    LOOP
        BEGIN
            SELECT COUNT(*)
            INTO MI_CANTEC
            FROM LICENCIAS
                 LEFT JOIN PERSONAL
                 ON  LICENCIAS.COMPANIA       = PERSONAL.COMPANIA
                 AND LICENCIAS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
            WHERE LICENCIAS.COMPANIA       = UN_COMPANIA
              AND LICENCIAS.ID_DE_EMPLEADO = RS.ID_DE_EMPLEADO
              AND LICENCIAS.FECHA_INICIO  <= UN_FECHAFIN
              AND LICENCIAS.FECHA_FINAL   >= UN_FECHAINICIO
              OR  LICENCIAS.COMPANIA       = UN_COMPANIA
              AND LICENCIAS.ID_DE_EMPLEADO = RS.ID_DE_EMPLEADO
              AND LICENCIAS.FECHA_FINAL   >= UN_FECHAINICIO;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CANTEC := 0;
        END;
        <<RECORRELICENCIAS2>>
        FOR RS2 IN (
            SELECT  LICENCIAS.COMPANIA
            ,LICENCIAS.ID_DE_PROCESO
            ,LICENCIAS.ID_DE_EMPLEADO
            ,LICENCIAS.FECHA_INICIO
            ,LICENCIAS.FECHA_FINAL
            ,LICENCIAS.NUMERO_ACTO
            ,LICENCIAS.FECHA_ACTO
            ,LICENCIAS.LICENCIA
            FROM LICENCIAS
            LEFT JOIN PERSONAL
            ON  LICENCIAS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
            AND LICENCIAS.COMPANIA       = PERSONAL.COMPANIA
            WHERE LICENCIAS.COMPANIA        = UN_COMPANIA
            AND LICENCIAS.ID_DE_EMPLEADO  = RS.ID_DE_EMPLEADO
            AND LICENCIAS.FECHA_INICIO   <= UN_FECHAFIN
            AND LICENCIAS.FECHA_FINAL    >= UN_FECHAINICIO
            AND LICENCIAS.LICENCIA = RS.LICENCIA
            OR  LICENCIAS.COMPANIA        = UN_COMPANIA
            AND LICENCIAS.ID_DE_EMPLEADO  = RS.ID_DE_EMPLEADO
            AND LICENCIAS.FECHA_FINAL    >= UN_FECHAINICIO
            AND LICENCIAS.LICENCIA = RS.LICENCIA
        )
        LOOP
            IF (RS2.LICENCIA <> '01' AND RS2.LICENCIA <> '02' AND RS2.LICENCIA <> '11' AND RS2.LICENCIA <> '10') = FALSE THEN
                IF RS2.FECHA_INICIO >=UN_FECHAINICIO AND RS2.FECHA_INICIO <= UN_FECHAFIN THEN
                    MI_FECHAINICIO := RS2.FECHA_INICIO;
                ELSIF  RS2.FECHA_INICIO <= UN_FECHAINICIO THEN
                    MI_FECHAINICIO := UN_FECHAINICIO;
                ELSE
                    MI_FECHAINICIO := RS2.FECHA_INICIO;
                END IF;

                IF RS2.FECHA_FINAL >= UN_FECHAINICIO AND RS2.FECHA_FINAL <= UN_FECHAFIN THEN
                    MI_FECHAFINAL := RS2.FECHA_FINAL;
                ELSIF RS2.FECHA_FINAL > UN_FECHAFIN THEN
                    MI_FECHAFINAL := UN_FECHAFIN;
                ELSE
                    MI_FECHAFINAL := RS2.FECHA_FINAL;
                END IF;

                IF TO_CHAR(MI_FECHAFINAL,'DD')= 31 THEN
                    MI_FECHAFINAL := MI_FECHAFINAL -1;
                END IF;

                MI_DIAS:=0;
                MI_SALARIO:=0;
                MI_GR:=0;

                MI_DIAS := MI_FECHAFINAL - MI_FECHAINICIO + 1;

                IF TO_CHAR(MI_FECHAFINAL,'DD') = 28 AND TO_CHAR(MI_FECHAFINAL,'MM') = 2 AND MOD(UN_ANO,4) <> 0 THEN--(CC:3148_CFBARRERA Se ajusta el proceso para el mes de febrero) --MOD JM CC 3515
                    MI_DIAS := MI_DIAS + 2;
                    -- REVISAR
                ELSIF TO_CHAR(MI_FECHAFINAL,'DD') = 29 AND TO_CHAR(MI_FECHAFINAL,'MM') = 2 AND MOD(UN_ANO,4) =  0 THEN--(CC:3148_CFBARRERA Se ajusta el proceso para el mes de febrero) --MOD JM CC 3515
                    MI_DIAS := MI_DIAS + 1;
                ELSIF MI_DIAS < 0 THEN
                    MI_DIAS := 0;
                END IF;

                IF MI_DIAS > 0 THEN
                    IF  MI_INDSUMA NOT IN (0) AND MI_CANTEC  > 1 THEN
                        MI_ACCION := '1';
                    ELSE
                        MI_ACCION := NULL;
                        IF MI_CANTEC > 1 THEN
                            MI_INDSUMA := -1;
                        ELSE
                            MI_INDSUMA := 0;
                        END IF;
                    END IF;
                    MI_CANTT := MI_CANTT + 1 ;

                    --(MZANGUNA:26/11/2018)-Se ajusta para que el concepto lo tome desde la tabla TIPOS_LICENCIA
                    MI_CONCEPTO := RS.ID_DE_CONCEPTO;
                    /*IF RS2.LICENCIA = '01' THEN
                        MI_CONCEPTO:= 359;
                    ELSIF RS2.LICENCIA = '02' THEN
                        MI_CONCEPTO:= 356;
                    ELSIF RS2.LICENCIA = '03' THEN
                        MI_CONCEPTO:= 357;
                    ELSIF RS2.LICENCIA = '11' THEN
                        MI_CONCEPTO:= 336;
                    END IF;*/
                    IF MI_CONCEPTO IN (363,364,365,390) AND MI_HABILES THEN --JM CC 3444
                        MI_DIAS := PCK_SYSMAN_UTL.FC_DIASHABIL(UN_COMPANIA => UN_COMPANIA,UN_FECHAINI   => MI_FECHAINICIO ,UN_FECHAFIN  => MI_FECHAFINAL);
                    END IF;

                    PCK_NOMINA.PR_INCLUIRNOVEDAD(
                         UN_COMPANIA   => UN_COMPANIA
                        ,UN_PROCESO    => UN_PROCESO
                        ,UN_ANIO       => UN_ANO
                        ,UN_MES        => UN_MES
                        ,UN_PERIODO    => UN_PERIODO
                        ,UN_IDEMPLEADO => RS2.ID_DE_EMPLEADO
                        ,UN_IDCONCEPTO => MI_CONCEPTO
                        ,UN_VALOR      => MI_DIAS
                        ,UN_ACCION     => MI_ACCION
                        ,UN_USER       => UN_USUARIO);
                END IF;
            END IF;
        END LOOP RECORRELICENCIAS2;
        MI_INDSUMA :=0;
    END LOOP RECORRELICENCIAS;
    
    --Ticket#7735511: se crea procedimiento para actualizar el estado en la tabla personal de acuerdo a la licencia de comisión
    <<ACTUALIZAESTADOLICENCIAS>>
        FOR RS3 IN (SELECT  LICENCIAS.COMPANIA
                    ,LICENCIAS.ID_DE_PROCESO
                    ,LICENCIAS.ID_DE_EMPLEADO
                    ,LICENCIAS.FECHA_INICIO
                    ,LICENCIAS.FECHA_FINAL
                    ,LICENCIAS.DIAS
                    ,LICENCIAS.NUMERO_ACTO
                    ,LICENCIAS.FECHA_ACTO
                    ,LICENCIAS.LICENCIA
                    ,TIPOS_LICENCIA.ID_DE_CONCEPTO
                  FROM LICENCIAS
                    LEFT JOIN PERSONAL
                    ON  LICENCIAS.COMPANIA       = PERSONAL.COMPANIA
                    AND LICENCIAS.ID_DE_EMPLEADO = PERSONAL.ID_DE_EMPLEADO
                    LEFT JOIN TIPOS_LICENCIA
                    ON LICENCIAS.COMPANIA = TIPOS_LICENCIA.COMPANIA
                    AND LICENCIAS.LICENCIA = TIPOS_LICENCIA.LICENCIA
                  WHERE LICENCIAS.COMPANIA       = UN_COMPANIA
                    AND LICENCIAS.FECHA_FINAL   >= UN_FECHAINICIO
                    AND LICENCIAS.LICENCIA      = 11
                    ORDER BY LICENCIAS.ID_DE_EMPLEADO
        )
        LOOP
        
        IF RS3.FECHA_FINAL < UN_FECHAFIN THEN
        
            MI_CAMPOS := 'TOTAL_DIAS_COMISION         = NULL
                          , FECHA_INICIO_COMISION     = NULL
                          , FECHA_FINAL_COMISION      = NULL
                          , ESTADO_ACTUAL             = 1
                          ';
        
            MI_CONDICION := 'COMPANIA     = ''' || RS3.COMPANIA      || '''
                             AND ID_DE_EMPLEADO      =   ' || RS3.ID_DE_EMPLEADO|| '
                             ';
              
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PERSONAL'
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION);
        
        ELSE
        
            MI_CAMPOS := 'TOTAL_DIAS_COMISION           = ''' || RS3.DIAS      || '''
                          , FECHA_INICIO_COMISION     = ''' || RS3.FECHA_INICIO|| '''
                          , FECHA_FINAL_COMISION      = ''' || RS3.FECHA_FINAL|| '''
                          , ESTADO_ACTUAL             = 6
                          ';
        
            MI_CONDICION := 'COMPANIA     = ''' || RS3.COMPANIA      || '''
                             AND ID_DE_EMPLEADO      =   ' || RS3.ID_DE_EMPLEADO|| '
                             ';
              
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PERSONAL'
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION);
        
        END IF;
        
        END LOOP ACTUALIZAESTADOLICENCIAS;
        
    
    RETURN MI_CANTT;
END FC_INCLUIR_NOVEDAD_LICENCIAS;

--5
FUNCTION FC_CONTAR_PERIODOS
/*
    NAME              : FC_CONTAR_PERIODOS == > ACCES REGISTRO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JHONATAN ACELAS AREVALO
    DATE MIGRADOR     : 9/07/2015
    TIME              : 5:17 PM
    SOURCE MODULE     : 
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 09/02/2017
    TIME              : 
    DESCRIPTION       : RETORNA NUMERO DE REGISTROS DE LA TABLA CATEGORIA FILTRANDO POR COMPANIA Y ANO
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getExistePeriodo
    @METHOD           : GET                    
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_MES      IN PCK_SUBTIPOS.TI_MES,
    UN_PROCESO  IN PCK_SUBTIPOS.TI_ID_DE_PROCESO
  ) 
RETURN PCK_SUBTIPOS.TI_LOGICO
AS 
  MI_REGISTROS  PCK_SUBTIPOS.TI_ENTERO;
  MI_CANTIDAD   PCK_SUBTIPOS.TI_ENTERO_LARGO:=0;
BEGIN
  --  ' RETORNA NUMERO DE REGISTROS DE PERIODOS DONDE  ANO Y PROCESO Y MES.
  BEGIN 
    SELECT COUNT(*) 
      INTO MI_CANTIDAD
    FROM PERIODOS
    WHERE ID_DE_PROCESO = UN_PROCESO 
      AND ANO           = UN_ANO
      AND MES           = UN_MES
      AND PERIODO       = UN_PERIODO;
  EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_CANTIDAD := 0;
  END;

  IF MI_CANTIDAD <= 0 THEN 
     MI_REGISTROS:= 0;
  ELSE 
     MI_REGISTROS:= -1;
  END IF;
  RETURN MI_REGISTROS;
END FC_CONTAR_PERIODOS;

--6
FUNCTION FC_VALOR_CONCEPTO_NOVEDAD
/*
    NAME              : FC_VALOR_CONCEPTO_NOVEDAD == > ACCES VALORCONCEPTONOVEDAD306
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JHONATAN ACELAS AREVALO
    DATE MIGRADOR     : 15/07/2015
    TIME              : 2:10 PM
    SOURCE MODULE     : 
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 09/02/2017
    TIME              : 
    DESCRIPTION       : RETORNA NUMERO DEL VALOR DE UN CONCEPTO 
                        SE AJUSTA AL ESTANDAR.
    @NAME             : getValorConceptoNovedad
    @METHOD           : GET                    
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_MES      IN PCK_SUBTIPOS.TI_MES,
    UN_CONCEPTO IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO
  ) 
RETURN PCK_SUBTIPOS.TI_DOBLE
  AS 
    MI_REGISTROS  PCK_SUBTIPOS.TI_ENTERO_LARGO:=0;
BEGIN
  --  ' RETORNA NUMERO DE REGISTROS DE PERIODOS DONDE  ANO Y PROCESO Y MES.
  BEGIN 
    SELECT SUM(VALOR) 
      INTO MI_REGISTROS  
    FROM  NOVEDADES
    WHERE COMPANIA       = UN_COMPANIA
      AND ANO            = UN_ANO
      AND MES            = UN_MES
      AND PERIODO        = UN_PERIODO
      AND ID_DE_CONCEPTO = UN_CONCEPTO;
  EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_REGISTROS := 0;
  END;
  IF MI_REGISTROS IS NULL THEN 
    MI_REGISTROS:=0;
  END IF;

  RETURN MI_REGISTROS;
END FC_VALOR_CONCEPTO_NOVEDAD;

--7
PROCEDURE PR_PREPARARFINANCIABLES
/*
    NAME              : PR_PREPARARFINANCIABLES  >> ACCES PrepararFinanciables
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JHONATAN ACELAS AREVALO
    DATE MIGRADOR     : 15/07/2015
    TIME              : 4:36 PM
    SOURCE MODULE     :
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 09/02/2017
    TIME              :
    DESCRIPTION       : \ SE AJUSTA AL ESTANDAR.
    @NAME             : prepararFinanciables
    @METHOD           : POST
  */
  (
    UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO1              IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES1              IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO1          IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ANO2              IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES2              IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO2          IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO,
    UN_TIPO_EMPLEADO     IN PCK_SUBTIPOS.TI_ENTERO        DEFAULT 0,
    UN_IGUAL_O_DIFERENTE IN VARCHAR2                      DEFAULT '='
  )
AS
  --MI_ERROR_FUN        NUMBER:=GL_ERROR_NUM + 8;
  MI_EXISTE           VARCHAR2(1) := 'E';
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
  MI_CADENA_QUERY     PCK_SUBTIPOS.TI_STRSQL;
  MI_VMONTO           PCK_SUBTIPOS.TI_DOBLE;
  MI_NCUOTAS          PCK_SUBTIPOS.TI_ENTERO;
  MI_VCUOTA           PCK_SUBTIPOS.TI_DOBLE;
  MI_VSALDO           PCK_SUBTIPOS.TI_DOBLE;
  MI_ACUMULADOR       PCK_SUBTIPOS.TI_ENTERO:=1;
  MI_FECHAFIN_NULL    DATE;
  MI_FECHALIB_NULL    DATE;
  MI_NLIBRANZA_NULL   VARCHAR2(30);
  MI_CUENTA           PCK_SUBTIPOS.TI_ENTERO;

  TYPE TEMP_DIF_REGISTRO_FIN_TYPE IS RECORD (
     COMPANIA         FINANCIABLES_DE_NOMINA.COMPANIA%TYPE
    ,ID_DE_PROCESO    FINANCIABLES_DE_NOMINA.ID_DE_PROCESO%TYPE
    ,ANO              FINANCIABLES_DE_NOMINA.ANO%TYPE
    ,MES              FINANCIABLES_DE_NOMINA.MES%TYPE
    ,PERIODO          FINANCIABLES_DE_NOMINA.PERIODO%TYPE
    ,ID_DE_EMPLEADO   FINANCIABLES_DE_NOMINA.ID_DE_EMPLEADO%TYPE
    ,ID_DE_CONCEPTO   FINANCIABLES_DE_NOMINA.ID_DE_CONCEPTO%TYPE
    ,MONTO_INICIAL    FINANCIABLES_DE_NOMINA.MONTO_INICIAL%TYPE
    ,NUMERO_DE_CUOTAS FINANCIABLES_DE_NOMINA.NUMERO_DE_CUOTAS%TYPE
    ,SALDO            FINANCIABLES_DE_NOMINA.SALDO%TYPE
    ,VALOR_CUOTA      FINANCIABLES_DE_NOMINA.VALOR_CUOTA%TYPE
    ,CUOTAFIJA        FINANCIABLES_DE_NOMINA.CUOTAFIJA%TYPE
    ,FECHA            FINANCIABLES_DE_NOMINA.FECHA%TYPE
    ,INTERES          FINANCIABLES_DE_NOMINA.INTERES%TYPE
    ,VALDESCUENTO     FINANCIABLES_DE_NOMINA.VALDESCUENTO%TYPE
    ,INDPREPARADO     FINANCIABLES_DE_NOMINA.INDPREPARADO%TYPE
    ,PERIODOCOBRO     FINANCIABLES_DE_NOMINA.PERIODOCOBRO%TYPE
    ,NLIBRANZA        FINANCIABLES_DE_NOMINA.NLIBRANZA%TYPE
    ,FECHA_LIBRANZA   FINANCIABLES_DE_NOMINA.FECHA_LIBRANZA%TYPE
    ,FECHAFIN         FINANCIABLES_DE_NOMINA.FECHAFIN%TYPE
    ,OBSERVACIONES    FINANCIABLES_DE_NOMINA.OBSERVACIONES%TYPE--JORDUZ  Se creo para el campo de observaciones TAR 1000095054
    );
  TYPE FINANCIABLE_TABLA_TYPE IS TABLE OF TEMP_DIF_REGISTRO_FIN_TYPE INDEX BY BINARY_INTEGER;

  TEMDIF_TABLA FINANCIABLE_TABLA_TYPE;

  RS SYS_REFCURSOR;
  I SYS_REFCURSOR;

  RSCOMPANIA            PERSONAL.COMPANIA%TYPE;
  RSTIPO                PERSONAL.ID_DE_TIPO%TYPE;
  RSESTADO_ACTUAL       PERSONAL.ESTADO_ACTUAL%TYPE;
  RSEMPLEADO            FINANCIABLES_DE_NOMINA.ID_DE_EMPLEADO%TYPE;
  RSINICIO_DISFRUTE     PERSONAL.INICIO_DISFRUTE%TYPE;
  RSFINAL_DISFRUTE      PERSONAL.FINAL_DISFRUTE%TYPE;
  RSNUMERO_DE_CUOTAS    FINANCIABLES_DE_NOMINA.NUMERO_DE_CUOTAS%TYPE;
  RSSALDO               FINANCIABLES_DE_NOMINA.SALDO%TYPE;
  RSCUOTAFIJA           FINANCIABLES_DE_NOMINA.CUOTAFIJA%TYPE;
  RSMONTO_INICIAL       FINANCIABLES_DE_NOMINA.MONTO_INICIAL%TYPE;
  RSVALOR_CUOTA         FINANCIABLES_DE_NOMINA.VALOR_CUOTA%TYPE;
  RSVALDESCUENTO        FINANCIABLES_DE_NOMINA.VALDESCUENTO%TYPE;
  RSPERIODOCOBRO        FINANCIABLES_DE_NOMINA.PERIODOCOBRO%TYPE;
  RSINTERES             FINANCIABLES_DE_NOMINA.INTERES%TYPE;
  RSID_DE_PROCESO       FINANCIABLES_DE_NOMINA.ID_DE_PROCESO%TYPE;
  RSID_DE_CONCEPTO      FINANCIABLES_DE_NOMINA.ID_DE_CONCEPTO%TYPE;
  RSFECHA               FINANCIABLES_DE_NOMINA.FECHA%TYPE;
  RSNLIBRANZA           FINANCIABLES_DE_NOMINA.NLIBRANZA%TYPE;
  RSFECHA_LIBRANZA      FINANCIABLES_DE_NOMINA.FECHA_LIBRANZA%TYPE;
  RSFECHAFIN            FINANCIABLES_DE_NOMINA.FECHAFIN%TYPE;
  RSOBSERVACIONES       FINANCIABLES_DE_NOMINA.OBSERVACIONES%TYPE;--JORDUZ  Se creo para el campo de observaciones TAR 1000095054
BEGIN
  --' SUBIR LOS FINANCIABLES_DE_NOMINA AL ARCHIVO DE NOVEDADES-
    -- SE AGREGO EL CAMPO DE OBSERVACIONES --JORDUZ
  MI_CADENA_QUERY := 'SELECT  DISTINCT PERSONAL.COMPANIA
                             ,PERSONAL.ID_DE_TIPO AS TIPO
                             ,PERSONAL.ESTADO_ACTUAL
                             ,FINANCIABLES_DE_NOMINA.ID_DE_EMPLEADO AS EMPLEADO
                             ,PERSONAL.INICIO_DISFRUTE
                             ,PERSONAL.FINAL_DISFRUTE
                             ,FINANCIABLES_DE_NOMINA.NUMERO_DE_CUOTAS
                             ,FINANCIABLES_DE_NOMINA.SALDO
                             ,FINANCIABLES_DE_NOMINA.CUOTAFIJA
                             ,FINANCIABLES_DE_NOMINA.MONTO_INICIAL
                             ,FINANCIABLES_DE_NOMINA.VALOR_CUOTA
                             ,FINANCIABLES_DE_NOMINA.VALDESCUENTO
                             ,FINANCIABLES_DE_NOMINA.PERIODOCOBRO
                             ,FINANCIABLES_DE_NOMINA.INTERES
                             ,FINANCIABLES_DE_NOMINA.ID_DE_PROCESO
                             ,FINANCIABLES_DE_NOMINA.ID_DE_CONCEPTO
                             ,FINANCIABLES_DE_NOMINA.OBSERVACIONES
                             ,FINANCIABLES_DE_NOMINA.FECHA
                             ,FINANCIABLES_DE_NOMINA.NLIBRANZA
                             ,FINANCIABLES_DE_NOMINA.FECHA_LIBRANZA
                             ,FINANCIABLES_DE_NOMINA.FECHAFIN
                      FROM PERSONAL
                        INNER JOIN FINANCIABLES_DE_NOMINA
                          ON  PERSONAL.COMPANIA       = FINANCIABLES_DE_NOMINA.COMPANIA
                          AND PERSONAL.ID_DE_EMPLEADO = FINANCIABLES_DE_NOMINA.ID_DE_EMPLEADO
                      WHERE PERSONAL.COMPANIA              ='''||UN_COMPANIA||'''
                        AND FINANCIABLES_DE_NOMINA.ANO     ='  ||UN_ANO1||'
                        AND FINANCIABLES_DE_NOMINA.MES     ='  ||UN_MES1||'
                        AND FINANCIABLES_DE_NOMINA.PERIODO ='  ||UN_PERIODO1;
  IF UN_TIPO_EMPLEADO IS NOT NULL THEN
    IF UN_IGUAL_O_DIFERENTE = '=' THEN
       MI_CADENA_QUERY:= MI_CADENA_QUERY
                         || ' AND PERSONAL.ID_DE_TIPO =  ''' ||UN_TIPO_EMPLEADO||'''';
    ELSE
       MI_CADENA_QUERY:= MI_CADENA_QUERY
                         || ' AND PERSONAL.ID_DE_TIPO <> '''||UN_TIPO_EMPLEADO||'''';
    END IF;
  END IF;
  MI_CADENA_QUERY:= MI_CADENA_QUERY || ' ORDER BY FINANCIABLES_DE_NOMINA.ANO';

  OPEN RS FOR MI_CADENA_QUERY;
    LOOP
      FETCH RS INTO RSCOMPANIA
                    ,RSTIPO
                    ,RSESTADO_ACTUAL
                    ,RSEMPLEADO
                    ,RSINICIO_DISFRUTE
                    ,RSFINAL_DISFRUTE
                    ,RSNUMERO_DE_CUOTAS
                    ,RSSALDO
                    ,RSCUOTAFIJA
                    ,RSMONTO_INICIAL
                    ,RSVALOR_CUOTA
                    ,RSVALDESCUENTO
                    ,RSPERIODOCOBRO
                    ,RSINTERES
                    ,RSID_DE_PROCESO
                    ,RSID_DE_CONCEPTO
                    ,RSOBSERVACIONES
                    ,RSFECHA
                    ,RSNLIBRANZA
                    ,RSFECHA_LIBRANZA
                    ,RSFECHAFIN;
      EXIT WHEN RS%NOTFOUND;
        IF    RSNUMERO_DE_CUOTAS >= 1
           OR RSSALDO            >  0
           OR RSCUOTAFIJA        <> 0 THEN
          MI_VMONTO := PCK_SYSMAN_UTL.FC_IIF(
                                      UN_CONDICION => RSMONTO_INICIAL <> 0
                                     ,UN_SI        => RSMONTO_INICIAL
                                     ,UN_NO        => 0);
          IF RSVALOR_CUOTA = RSVALDESCUENTO THEN
            MI_NCUOTAS:=RSNUMERO_DE_CUOTAS
                        - PCK_SYSMAN_UTL.FC_IIF(
                                         UN_CONDICION => PCK_SYSMAN_UTL.FC_PAR(
                                                                        UN_COMPANIA  => UN_COMPANIA
                                                                       ,UN_NOMBRE    => 'DUPLICAR CUOTAS EN NOMINA MENSUAL'
                                                                       ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                                                       ,UN_FECHA_PAR => SYSDATE)='SI'
                                                         AND RSPERIODOCOBRO = 0
                                                         AND UN_PERIODO2=2
                                        ,UN_SI        => 2
                                        ,UN_NO        => 1);
          ELSE
           MI_NCUOTAS:=RSNUMERO_DE_CUOTAS - TRUNC(RSVALDESCUENTO/RSVALOR_CUOTA);
          END IF;

          MI_NCUOTAS:= PCK_SYSMAN_UTL.FC_IIF(
                                      UN_CONDICION => MI_NCUOTAS<0
                                     ,UN_SI        => 0
                                     ,UN_NO        => MI_NCUOTAS);
          IF RSVALDESCUENTO = 0 THEN
            MI_NCUOTAS:= RSNUMERO_DE_CUOTAS;
          END IF;
            -- SI ES CUOTA FJA SE DESCUENTA EEN CADA PERIODO
          IF RSCUOTAFIJA <> 0 THEN
            IF RSINTERES > 0 THEN
              MI_VCUOTA := RSVALOR_CUOTA * RSINTERES / 100;
              MI_VSALDO := PCK_SYSMAN_UTL.FC_IIF(
                                          UN_CONDICION => RSSALDO <> 0
                                         ,UN_SI        => RSSALDO
                                         ,UN_NO        => 0) - MI_VCUOTA;
            ELSE
              MI_VCUOTA := RSVALOR_CUOTA;
              IF RSVALDESCUENTO =0 THEN
                MI_VSALDO := RSSALDO;
              ELSIF RSVALOR_CUOTA = RSVALDESCUENTO THEN
                MI_VSALDO := PCK_SYSMAN_UTL.FC_IIF(
                                            UN_CONDICION => RSSALDO <> 0
                                           ,UN_SI        => RSSALDO
                                           ,UN_NO        => 0)
                             - PCK_SYSMAN_UTL.FC_IIF(
                                              UN_CONDICION => PCK_SYSMAN_UTL.FC_PAR(
                                                                             UN_COMPANIA  => UN_COMPANIA
                                                                            ,UN_NOMBRE    => 'DUPLICAR CUOTAS EN NOMINA MENSUAL'
                                                                            ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                                                                            ,UN_FECHA_PAR => SYSDATE) = 'SI'
                                                              AND RSPERIODOCOBRO = 0
                                                              AND UN_PERIODO2 = 3
                                             ,UN_SI        => MI_VCUOTA*2
                                             ,UN_NO        => MI_VCUOTA);
              ELSE
                MI_VSALDO := PCK_SYSMAN_UTL.FC_IIF(
                                            UN_CONDICION => RSSALDO <> 0
                                           ,UN_SI        => RSSALDO
                                           ,UN_NO        => 0 ) - RSVALDESCUENTO;
              END IF;
              IF MI_VSALDO < 0 THEN
                MI_VSALDO :=0;
                MI_VCUOTA :=0;
              END IF;
            END IF;
          ELSE
            -- SI NO SON DE CUOTA FIJA
            IF RSINTERES <> 0 THEN
            -- CUOTA DE INTERES SOBRE SALDO
              MI_VCUOTA := RSSALDO / MI_NCUOTAS + RSSALDO * RSINTERES / 100;
              MI_VSALDO := RSSALDO - RSSALDO/ MI_NCUOTAS;
            ELSE
              MI_VCUOTA := PCK_SYSMAN_UTL.FC_IIF(
                                          UN_CONDICION => RSSALDO < RSVALOR_CUOTA
                                         ,UN_SI        => RSSALDO
                                         ,UN_NO        => RSVALOR_CUOTA);
              MI_VSALDO := RSSALDO - RSVALDESCUENTO;
              IF MI_VSALDO < 0 THEN
                MI_VSALDO :=0;
                MI_VCUOTA :=0;
              END IF;
            END IF;
          END IF;
        END IF;

        IF MI_VSALDO > 0 THEN
          TEMDIF_TABLA(MI_ACUMULADOR).COMPANIA := UN_COMPANIA;
          TEMDIF_TABLA(MI_ACUMULADOR).ID_DE_PROCESO := RSID_DE_PROCESO;
          TEMDIF_TABLA(MI_ACUMULADOR).ANO := PCK_SYSMAN_UTL.FC_IIF(
                                                            UN_CONDICION => RSPERIODOCOBRO =1
                                                                            AND UN_MES1+1 =13
                                                           ,UN_SI        => UN_ANO2+1
                                                           ,UN_NO        => UN_ANO2);
          IF RSPERIODOCOBRO = 1 THEN
            IF UN_PERIODO1 = 1
               OR UN_PERIODO1 = 3 THEN
              TEMDIF_TABLA(MI_ACUMULADOR).MES := PCK_SYSMAN_UTL.FC_IIF(
                                                                UN_CONDICION => UN_MES1+1 = 13
                                                               ,UN_SI        => 1
                                                               ,UN_NO        => 2);
            ELSE
             TEMDIF_TABLA(MI_ACUMULADOR).MES:= UN_MES2;
            END IF;
          ELSE
            TEMDIF_TABLA(MI_ACUMULADOR).MES:= UN_MES2;
          END IF;
          IF RSPERIODOCOBRO = 1 AND UN_PERIODO1 = 1 THEN
        --  FC_SIGUIENTEPERIODO( UN_COMPANIA,MI_ANIO,MI_PERIODO, MI_MES,UN_PROCESO);
            TEMDIF_TABLA(MI_ACUMULADOR).PERIODO :=SUBSTR(PCK_NOMINA_COM2.FC_SIGUIENTEPERIODO(
                                                                         UN_COMPANIA => UN_COMPANIA
                                                                        ,UN_ANO      => UN_ANO2
                                                                        ,UN_PERIODO  => UN_PERIODO2
                                                                        ,UN_MES      => UN_MES2
                                                                        ,UN_PROCESO  => RSID_DE_PROCESO
                                                                        ,UN_OPCION   => 'F'),7,2);
          ELSE
            TEMDIF_TABLA(MI_ACUMULADOR).PERIODO := PCK_SYSMAN_UTL.FC_IIF(
                                                                  UN_CONDICION => RSPERIODOCOBRO = 0
                                                                 ,UN_SI => UN_PERIODO2
                                                                 ,UN_NO => PCK_SYSMAN_UTL.FC_IIF(
                                                                                          UN_CONDICION => UN_PERIODO2 = 3
                                                                                         ,UN_SI => 3
                                                                                         ,UN_NO => RSPERIODOCOBRO));
          END IF;
          TEMDIF_TABLA(MI_ACUMULADOR).ID_DE_EMPLEADO := RSEMPLEADO;
          TEMDIF_TABLA(MI_ACUMULADOR).ID_DE_CONCEPTO:= RSID_DE_CONCEPTO;
          TEMDIF_TABLA(MI_ACUMULADOR).MONTO_INICIAL := MI_VMONTO;
          TEMDIF_TABLA(MI_ACUMULADOR).VALOR_CUOTA:= MI_VCUOTA;
          TEMDIF_TABLA(MI_ACUMULADOR).VALDESCUENTO:=PCK_SYSMAN_UTL.FC_IIF(
                                                                   UN_CONDICION => RSSALDO
                                                                                   < PCK_SYSMAN_UTL.FC_IIF(
                                                                                                    UN_CONDICION => PCK_SYSMAN_UTL.FC_PAR(
                                                                                                                                   UN_COMPANIA => UN_COMPANIA
                                                                                                                                  ,UN_NOMBRE => 'DUPLICAR CUOTAS EN NOMINA MENSUAL'
                                                                                                                                  ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA
                                                                                                                                  ,UN_FECHA_PAR => SYSDATE) = 'SI'
                                                                                                                    AND RSPERIODOCOBRO = 0
                                                                                                                    AND UN_PERIODO2 = 3
                                                                                                   ,UN_SI => MI_VCUOTA*2
                                                                                                   ,UN_NO => MI_VCUOTA )
                                                                  ,UN_SI => MI_VSALDO
                                                                  ,UN_NO => PCK_SYSMAN_UTL.FC_IIF(
                                                                                           UN_CONDICION => PCK_SYSMAN_UTL.FC_PAR(
                                                                                                                          UN_COMPANIA => UN_COMPANIA
                                                                                                                         ,UN_NOMBRE   => 'DUPLICAR CUOTAS EN NOMINA MENSUAL'
                                                                                                                         ,UN_MODULO   => PCK_DATOS.FC_MODULONOMINA
                                                                                                                         ,UN_FECHA_PAR => SYSDATE) = 'SI'
                                                                                                           AND RSPERIODOCOBRO = 0
                                                                                                           AND UN_PERIODO2 = 3
                                                                                          ,UN_SI        => MI_VCUOTA*2
                                                                                          ,UN_NO        => MI_VCUOTA) );
          TEMDIF_TABLA(MI_ACUMULADOR).NUMERO_DE_CUOTAS:= PCK_SYSMAN_UTL.FC_IIF(
                                                                        UN_CONDICION => MI_NCUOTAS <=0
                                                                       ,UN_SI        => 0
                                                                       ,UN_NO        => MI_NCUOTAS);
          TEMDIF_TABLA(MI_ACUMULADOR).SALDO := PCK_SYSMAN_UTL.FC_IIF(
                                                              UN_CONDICION => MI_VSALDO <=0
                                                             ,UN_SI        => 0
                                                             ,UN_NO        => MI_VSALDO);
          TEMDIF_TABLA(MI_ACUMULADOR).CUOTAFIJA := RSCUOTAFIJA;
          TEMDIF_TABLA(MI_ACUMULADOR).INTERES := PCK_SYSMAN_UTL.FC_IIF(
                                                                UN_CONDICION => RSINTERES <> 0
                                                               ,UN_SI        => RSINTERES
                                                               ,UN_NO        => 0 );
          TEMDIF_TABLA(MI_ACUMULADOR).FECHA:= TO_DATE(TO_CHAR(CASE WHEN
                                                             RSFECHA IS NULL
                                                             THEN PCK_NOMINA.FC_FECHAPERIODO(
                                                                                 UN_COMPANIA => UN_COMPANIA
                                                                                ,UN_PROCESO  => RSID_DE_PROCESO
                                                                                ,UN_ANO      => UN_ANO2
                                                                                ,UN_MES      => UN_MES2
                                                                                ,UN_PERIODO  => UN_PERIODO2)
                                                            ELSE TO_DATE(TO_CHAR(RSFECHA,'DD/MM/YYYY'),'DD/MM/YYYY') END,'DD/MM/YYYY'),'DD/MM/YYYY');
          TEMDIF_TABLA(MI_ACUMULADOR).INDPREPARADO := 1;
          TEMDIF_TABLA(MI_ACUMULADOR).PERIODOCOBRO := RSPERIODOCOBRO;
          TEMDIF_TABLA(MI_ACUMULADOR).NLIBRANZA := RSNLIBRANZA;
          TEMDIF_TABLA(MI_ACUMULADOR).FECHA_LIBRANZA := RSFECHA_LIBRANZA;
          TEMDIF_TABLA(MI_ACUMULADOR).FECHAFIN := RSFECHAFIN;
          TEMDIF_TABLA(MI_ACUMULADOR).OBSERVACIONES := RSOBSERVACIONES;
          MI_ACUMULADOR := MI_ACUMULADOR+1;
        END IF;
    END LOOP;
  CLOSE RS;

  MI_CAMPOS := 'COMPANIA,ID_DE_PROCESO,ANO,MES,PERIODO,ID_DE_EMPLEADO,ID_DE_CONCEPTO,MONTO_INICIAL'
               ||',NUMERO_DE_CUOTAS,SALDO ,VALOR_CUOTA,CUOTAFIJA,FECHA ,INTERES ,VALDESCUENTO ,'
               ||'INDPREPARADO ,PERIODOCOBRO ,OBSERVACIONES ,NLIBRANZA ,FECHA_LIBRANZA ,FECHAFIN,
                 DATE_CREATED, CREATED_BY';

  IF TEMDIF_TABLA.LAST IS NOT NULL THEN
    FOR I IN TEMDIF_TABLA.FIRST..TEMDIF_TABLA.LAST
    LOOP

      MI_CUENTA :=0;
      BEGIN
        SELECT COUNT(0)
        INTO   MI_CUENTA
        FROM   FINANCIABLES_DE_NOMINA
        WHERE  COMPANIA = TEMDIF_TABLA(I).COMPANIA
          AND  ID_DE_PROCESO = TEMDIF_TABLA(I).ID_DE_PROCESO
          AND  ANO = TEMDIF_TABLA(I).ANO
          AND  MES = TEMDIF_TABLA(I).MES
          AND  PERIODO = TEMDIF_TABLA(I).PERIODO
          AND  ID_DE_EMPLEADO = TEMDIF_TABLA(I).ID_DE_EMPLEADO
          AND  ID_DE_CONCEPTO = TEMDIF_TABLA(I).ID_DE_CONCEPTO;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CUENTA :=0;
      END;

      IF MI_CUENTA = 0 THEN
          IF (TEMDIF_TABLA(I).NLIBRANZA IS NULL) THEN
            MI_NLIBRANZA_NULL :='NULL';
          ELSE
            MI_NLIBRANZA_NULL := ''''||TEMDIF_TABLA(I).NLIBRANZA||'''';
          END IF;

          MI_VALORES:=''''||TEMDIF_TABLA(I).COMPANIA||''','
                          ||TEMDIF_TABLA(I).ID_DE_PROCESO||','
                          ||TEMDIF_TABLA(I).ANO||','
                          ||TEMDIF_TABLA(I).MES||','
                          ||TEMDIF_TABLA(I).PERIODO||','
                          ||TEMDIF_TABLA(I).ID_DE_EMPLEADO||','
                          ||TEMDIF_TABLA(I).ID_DE_CONCEPTO||','
                          ||TEMDIF_TABLA(I).MONTO_INICIAL||','
                          ||TEMDIF_TABLA(I).NUMERO_DE_CUOTAS||','
                          ||TEMDIF_TABLA(I).SALDO||','
                          ||TEMDIF_TABLA(I).VALOR_CUOTA||','
                          ||TEMDIF_TABLA(I).CUOTAFIJA||','''
                          || TEMDIF_TABLA(I).FECHA ||''','
                          ||TEMDIF_TABLA(I).INTERES||','
                          ||TEMDIF_TABLA(I).VALDESCUENTO||','
                          ||TEMDIF_TABLA(I).INDPREPARADO||','
                          ||TEMDIF_TABLA(I).PERIODOCOBRO||',''' --JORDUZ TAR 1000095054
                          ||TEMDIF_TABLA(I).OBSERVACIONES||''','--JORDUZ TAR 1000095054
                          ||MI_NLIBRANZA_NULL||',TO_DATE('''
                          ||TO_CHAR(TEMDIF_TABLA(I).FECHA_LIBRANZA,'DD/MM/YYYY')||''', ''DD/MM/YYYY''),TO_DATE(''' --JORDUZ TAR 1000095054
                          ||TO_CHAR(TEMDIF_TABLA(I).FECHAFIN,'DD/MM/YYYY')||''', ''DD/MM/YYYY''),SYSDATE,''' || UN_USUARIO ||'''' ; --JORDUZ TAR 1000095054
          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                            UN_TABLA   => 'FINANCIABLES_DE_NOMINA'
                                           ,UN_ACCION  => 'I'
                                           ,UN_CAMPOS  => MI_CAMPOS
                                           ,UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                         UN_EXC_COD    => SQLCODE
                        ,UN_ERROR_COD  => PCK_ERRORES.ERRR_NOMINA_ACTUALIZASUELDOS
                        ,UN_TABLAERROR => 'FINANCIABLES_DE_NOMINA'
                      );
          END;
      END IF;
    END LOOP;
  END IF;

END PR_PREPARARFINANCIABLES;

--8
FUNCTION FC_SIGUIENTEPERIODO
/*
    NAME              : FC_SIGUIENTEPERIODO == > ACCES SiguientePeriodo
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JHONATAN ACELAS AREVALO
    DATE MIGRADOR     : 21/07/2015
    TIME              : 10:16 AM
    SOURCE MODULE     : 
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 09/02/2017
    TIME              : 
    DESCRIPTION       : RETORNA EL SIGUIENTE PERIODO DE NOMINA DE FORMA 19970101 
                        SE AJUSTA AL ESTANDAR.
    @NAME             : siguientePeriodo
    @METHOD           : GET                    
    */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_PERIODO  IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_MES      IN PCK_SUBTIPOS.TI_MES,
    UN_PROCESO  IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_OPCION   IN VARCHAR2 DEFAULT NULL
  ) 
RETURN VARCHAR2
  AS 
    MI_REGISTROS  NUMBER:=0;
BEGIN
  --  ' RETORNA NUMERO DE REGISTROS DE PERIODOS DONDE  ANO Y PROCESO Y MES.
  BEGIN 
    SELECT COUNT(1) 
    INTO MI_REGISTROS
    FROM(SELECT  ROWNUM IND
                ,ANO
                ,MES
                ,PERIODO
                ,FECHAFINAL
         FROM PERIODOS
         WHERE COMPANIA      = UN_COMPANIA
           AND ACUMULADO     <> 0 
           AND DIFERIDOS     <> 0
           AND ANO 
               || LPAD(MES,2,'0') 
               || LPAD(PERIODO,2,'0') > UN_ANO
                                        || LPAD(UN_MES,2,'0')
                                        || LPAD(UN_PERIODO,2,'0')
           AND ID_DE_PROCESO = UN_PROCESO
         ORDER BY  ID_DE_PROCESO 
                  ,ANO 
                  ,MES 
                  ,PERIODO
         )
    WHERE IND=1;
  EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_REGISTROS := NULL;
  END;
  IF MI_REGISTROS IS NULL THEN 
     MI_REGISTROS:=0;
  END IF;

  IF MI_REGISTROS <= 0 THEN
    RETURN ' ';
  END IF;

  FOR I IN (SELECT  ANO
                   ,MES
                   ,PERIODO
                   ,FECHAFINAL
            FROM PERIODOS
            WHERE COMPANIA      = UN_COMPANIA
              AND ACUMULADO     <> 0 
              AND DIFERIDOS     <> 0
              AND ID_DE_PROCESO = UN_PROCESO
              AND ANO 
                  || LPAD(MES,2,'0') 
                  || LPAD(PERIODO,2,'0') > UN_ANO
                                           || LPAD(UN_MES,2,'0')
                                           || LPAD(UN_PERIODO,2,'0')
            ORDER BY  ID_DE_PROCESO 
                     ,ANO 
                     ,MES 
                     ,PERIODO) 
  LOOP
    IF  UN_OPCION IS NOT NULL THEN 
      RETURN  I.FECHAFINAL;
    ELSE
      RETURN  I.ANO||LPAD(I.MES,2,0)||LPAD(I.PERIODO,2,0);
    END IF;
  END LOOP;
  RETURN ' ';
END FC_SIGUIENTEPERIODO;

--9 
PROCEDURE PR_PREPARAREMBARGOS
/*
    NAME              : PR_PREPARAREMBARGOS  >> ACCES PrepararEmbargos
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JHONATAN ACELAS AREVALO
    DATE MIGRADOR     : 21/07/2015
    TIME              : 12:08 PM
    SOURCE MODULE     :
    MODIFIER          : (09/02/2017) JAVIER ANDRES RODRIGUEZ RIOS
                        (07/09/2017) PABLO ANDRES ESPITIA CUCA
    MODIFICATIONS     : (07/09/2017) Adicion de campos de auditoria.
    DESCRIPTION       : \ SE AJUSTA AL ESTANDAR.
    PARAMETERS        : UN_USUARIO => Codigo del usuario que desencadena el proceso.
    @NAME             : prepararEmbargos
    @METHOD           : POST
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO1     IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES1     IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO1 IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ANO2     IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES2     IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO2 IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO
  )
AS
  MI_EXISTE          VARCHAR2(1) := 'E';
  MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES         PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
  MI_CADENA_QUERY    PCK_SUBTIPOS.TI_STRSQL;
  MI_VMONTO          PCK_SUBTIPOS.TI_DOBLE;
  MI_NCUOTAS         PCK_SUBTIPOS.TI_ENTERO;
  MI_VCUOTA          PCK_SUBTIPOS.TI_DOBLE;
  MI_VSALDO          PCK_SUBTIPOS.TI_DOBLE;
  MI_ACUMULADOR      PCK_SUBTIPOS.TI_ENTERO:=1;
  MI_FECHAFIN_NULL   VARCHAR2(30);
  MI_FECHALIB_NULL   VARCHAR2(30);
  MI_NLIBRANZA_NULL  VARCHAR2(30);
  MI_RANGO_PERIODOS  PCK_SUBTIPOS.TI_STRSQL;
  MI_QUERY_PERIODOS  PCK_SUBTIPOS.TI_STRSQL;
  MI_AUX_PERIODOS    PCK_SUBTIPOS.TI_ENTERO;
  MI_MSG             PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_SIGUIENTE       BOOLEAN DEFAULT FALSE;
  MI_EXISTEEMBARG    PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  TYPE TEMP_DIF_REGISTRO_ENB_TYPE IS RECORD (
     COMPANIA              EMBARGOS.COMPANIA%TYPE
    ,ID_DE_PROCESO         EMBARGOS.ID_DE_PROCESO%TYPE
    ,ANO                   EMBARGOS.ANO%TYPE
    ,MES                   EMBARGOS.MES%TYPE
    ,PERIODO               EMBARGOS.PERIODO%TYPE
    ,ID_DE_EMPLEADO        EMBARGOS.ID_DE_EMPLEADO%TYPE
    ,ID_EMBARGO            EMBARGOS.ID_EMBARGO%TYPE
    ,ID_JUZGADO            EMBARGOS.ID_JUZGADO%TYPE
    ,FECHA_EMBARGO         EMBARGOS.FECHA_EMBARGO%TYPE
    ,OFICIO                EMBARGOS.OFICIO%TYPE
    ,MONTO_EMBARGO         EMBARGOS.MONTO_EMBARGO%TYPE
    ,PORCENTAJE            EMBARGOS.PORCENTAJE%TYPE
    ,VALOR                 EMBARGOS.VALOR%TYPE
    ,SALDO                 EMBARGOS.SALDO%TYPE
    ,VALORH                HISTORICOS.VALOR%TYPE
    ,CUOTAS                EMBARGOS.CUOTAS%TYPE
    ,VR_CUOTA              EMBARGOS.VR_CUOTA%TYPE
    ,DEMANDANTE            EMBARGOS.DEMANDANTE%TYPE
    ,TIPO_DEMANDANTE       EMBARGOS.TIPO_DEMANDANTE%TYPE
    ,NUM_RADICACION        EMBARGOS.NUM_RADICACION%TYPE
    ,FECHA_RADICACION      EMBARGOS.FECHA_RADICACION%TYPE
    ,OBSERVACIONES         EMBARGOS.OBSERVACIONES%TYPE
    ,FORMA_PAGO            EMBARGOS.FORMA_PAGO%TYPE
    ,FACTOR1               EMBARGOS.FACTOR1%TYPE
    ,FACTOR2               EMBARGOS.FACTOR2%TYPE
    ,FACTOR3               EMBARGOS.FACTOR3%TYPE
    ,FACTOR4               EMBARGOS.FACTOR4%TYPE
    ,FACTOR5               EMBARGOS.FACTOR5%TYPE
    ,ESTADO                EMBARGOS.ESTADO%TYPE
    ,OFICINA_DESTINO       EMBARGOS.OFICINA_DESTINO%TYPE
    ,ID_DE_CONCEPTO        EMBARGOS.ID_DE_CONCEPTO%TYPE
    ,COMISION              EMBARGOS.COMISION%TYPE
    ,CONCEPTO_DEPOSITO     EMBARGOS.CONCEPTO_DEPOSITO%TYPE
    ,CUENTA                EMBARGOS.CUENTA%TYPE
    ,NUMERO_PROCESO        EMBARGOS.NUMERO_PROCESO%TYPE
    ,DCTO_IDENTIDAD        EMBARGOS.DCTO_IDENTIDAD%TYPE
    ,CEDULA                EMBARGOS.CEDULA%TYPE
    ,BANCO                 EMBARGOS.BANCO%TYPE
    ,DEMANDANTE_NOMBRES    EMBARGOS.DEMANDANTE_NOMBRES%TYPE
    ,PORCENTAJE_CESANTIAS  EMBARGOS.PORCENTAJE_CESANTIAS%TYPE
    ,PORCENTAJE_PRIMAS     EMBARGOS.PORCENTAJE_PRIMAS%TYPE) ;

  TYPE EMBARGOS_TABLA_TYPE IS TABLE OF TEMP_DIF_REGISTRO_ENB_TYPE INDEX BY BINARY_INTEGER;
  TEMDIF_TABLA EMBARGOS_TABLA_TYPE;
  RS SYS_REFCURSOR;
  I SYS_REFCURSOR;
  RSCOMPANIA              EMBARGOS.COMPANIA%TYPE;
  RSID_DE_PROCESO         EMBARGOS.ID_DE_PROCESO%TYPE;
  RSANO                   EMBARGOS.ANO%TYPE;
  RSMES                   EMBARGOS.MES%TYPE;
  RSPERIODO               EMBARGOS.PERIODO%TYPE;
  RSID_DE_EMPLEADO        EMBARGOS.ID_DE_EMPLEADO%TYPE;
  RSID_EMBARGO            EMBARGOS.ID_EMBARGO%TYPE;
  RSID_JUZGADO            EMBARGOS.ID_JUZGADO%TYPE;
  RSFECHA_EMBARGO         EMBARGOS.FECHA_EMBARGO%TYPE;
  RSOFICIO                EMBARGOS.OFICIO%TYPE;
  RSMONTO_EMBARGO         EMBARGOS.MONTO_EMBARGO%TYPE;
  RSPORCENTAJE            EMBARGOS.PORCENTAJE%TYPE;
  RSVALOR                 EMBARGOS.VALOR%TYPE;
  RSVLRCUOTA              EMBARGOS.VR_CUOTA%TYPE;
  RSSALDO                 EMBARGOS.SALDO%TYPE;
  RSVALORH                HISTORICOS.VALOR%TYPE;
  RSCUOTAS                EMBARGOS.CUOTAS%TYPE;
  RSDEMANDANTE            EMBARGOS.DEMANDANTE%TYPE;
  RSTIPO_DEMANDANTE       EMBARGOS.TIPO_DEMANDANTE%TYPE;
  RSNUM_RADICACION        EMBARGOS.NUM_RADICACION%TYPE;
  RSFECHA_RADICACION      EMBARGOS.FECHA_RADICACION%TYPE;
  RSOBSERVACIONES         EMBARGOS.OBSERVACIONES%TYPE;
  RSFORMA_PAGO            EMBARGOS.FORMA_PAGO%TYPE;
  RSFACTOR1               EMBARGOS.FACTOR1%TYPE;
  RSFACTOR2               EMBARGOS.FACTOR2%TYPE;
  RSFACTOR3               EMBARGOS.FACTOR3%TYPE;
  RSFACTOR4               EMBARGOS.FACTOR4%TYPE;
  RSFACTOR5               EMBARGOS.FACTOR5%TYPE;
  RSESTADO                EMBARGOS.ESTADO%TYPE;
  RSOFICINA_DESTINO       EMBARGOS.OFICINA_DESTINO%TYPE;
  RSID_DE_CONCEPTO        EMBARGOS.ID_DE_CONCEPTO%TYPE;
  RSCOMISION              EMBARGOS.COMISION%TYPE;
  RSCONCEPTO_DEPOSITO     EMBARGOS.CONCEPTO_DEPOSITO%TYPE;
  RSCUENTA                EMBARGOS.CUENTA%TYPE;
  RSNUMERO_PROCESO        EMBARGOS.NUMERO_PROCESO%TYPE;
  RSDCTO_IDENTIDAD        EMBARGOS.DCTO_IDENTIDAD%TYPE;
  RSCEDULA                EMBARGOS.CEDULA%TYPE;
  RSBANCO                 EMBARGOS.BANCO%TYPE;
  RSDEMANDANTE_NOMBRES    EMBARGOS.DEMANDANTE_NOMBRES%TYPE;
  RSPORCENTAJE_CESANTIAS  EMBARGOS.PORCENTAJE_CESANTIAS%TYPE;
  RSPORCENTAJE_PRIMAS     EMBARGOS.PORCENTAJE_PRIMAS%TYPE;

BEGIN
  --' SUBIR LOS FINANCIABLES_DE_NOMINA AL ARCHIVO DE NOVEDADES
  MI_CADENA_QUERY := 'SELECT  EMBARGOS.COMPANIA
                             ,EMBARGOS.ID_DE_PROCESO
                             ,EMBARGOS.ANO
                             ,EMBARGOS.MES
                             ,EMBARGOS.PERIODO
                             ,EMBARGOS.ID_DE_EMPLEADO
                             ,EMBARGOS.ID_EMBARGO
                             ,EMBARGOS.ID_JUZGADO
                             ,EMBARGOS.FECHA_EMBARGO
                             ,EMBARGOS.OFICIO
                             ,EMBARGOS.MONTO_EMBARGO
                             ,EMBARGOS.PORCENTAJE
                             ,EMBARGOS.VALOR
                             ,CASE WHEN EMBARGOS.ID_EMBARGO=''02'' OR EMBARGOS.ID_EMBARGO=''03'' OR EMBARGOS.ID_EMBARGO>''04''
                                    THEN EMBARGOS.VR_CUOTA
                                    ELSE 0
                                    END  VLRCUOTA
                             ,EMBARGOS.SALDO
                             ,HISTORICOS.VALOR AS VALORH
                             ,EMBARGOS.CUOTAS
                             ,EMBARGOS.DEMANDANTE
                             ,EMBARGOS.TIPO_DEMANDANTE
                             ,EMBARGOS.NUM_RADICACION
                             ,EMBARGOS.FECHA_RADICACION
                             ,EMBARGOS.OBSERVACIONES
                             ,EMBARGOS.FORMA_PAGO
                             ,EMBARGOS.FACTOR1
                             ,EMBARGOS.FACTOR2
                             ,EMBARGOS.FACTOR3
                             ,EMBARGOS.FACTOR4
                             ,EMBARGOS.FACTOR5
                             ,EMBARGOS.ESTADO
                             ,EMBARGOS.OFICINA_DESTINO
                             ,EMBARGOS.ID_DE_CONCEPTO
                             ,EMBARGOS.COMISION
                             ,EMBARGOS.CONCEPTO_DEPOSITO
                             ,EMBARGOS.CUENTA
                             ,EMBARGOS.NUMERO_PROCESO
                             ,EMBARGOS.DCTO_IDENTIDAD
                             ,EMBARGOS.CEDULA
                             ,EMBARGOS.BANCO
                             ,EMBARGOS.DEMANDANTE_NOMBRES
                             ,EMBARGOS.PORCENTAJE_CESANTIAS
                             ,EMBARGOS.PORCENTAJE_PRIMAS
                      FROM EMBARGOS
                        LEFT JOIN HISTORICOS
                          ON  EMBARGOS.COMPANIA       = HISTORICOS.COMPANIA
                          AND EMBARGOS.ID_DE_PROCESO  = HISTORICOS.ID_DE_PROCESO
                          AND EMBARGOS.ANO            = HISTORICOS.ANO
                          AND EMBARGOS.MES            = HISTORICOS.MES
                          AND EMBARGOS.PERIODO        = HISTORICOS.PERIODO
                          AND EMBARGOS.ID_DE_EMPLEADO = HISTORICOS.ID_DE_EMPLEADO
                          AND EMBARGOS.ID_DE_CONCEPTO = HISTORICOS.ID_DE_CONCEPTO
                      WHERE EMBARGOS.COMPANIA       ='''||UN_COMPANIA||'''
                        AND EMBARGOS.ANO            ='||UN_ANO1||'
                        AND EMBARGOS.MES            ='||UN_MES1||'
                        AND EMBARGOS.PERIODO        ='||UN_PERIODO1||'
                        AND EMBARGOS.ESTADO         <>''C'' ';

  SELECT DISTINCT LISTAGG(PERIODO,',')
                  WITHIN GROUP (ORDER BY PERIODO)
    INTO MI_RANGO_PERIODOS
    FROM PERIODOS
   WHERE COMPANIA  = UN_COMPANIA
     AND ANO = UN_ANO2
     AND MES = UN_MES2;
  OPEN RS FOR MI_CADENA_QUERY;
    <<EMBARGO>>
    LOOP
      FETCH RS INTO  RSCOMPANIA
                    ,RSID_DE_PROCESO
                    ,RSANO,RSMES
                    ,RSPERIODO
                    ,RSID_DE_EMPLEADO
                    ,RSID_EMBARGO
                    ,RSID_JUZGADO
                    ,RSFECHA_EMBARGO
                    ,RSOFICIO
                    ,RSMONTO_EMBARGO
                    ,RSPORCENTAJE
                    ,RSVALOR
                    ,RSVLRCUOTA
                    ,RSSALDO
                    ,RSVALORH
                    ,RSCUOTAS
                    ,RSDEMANDANTE
                    ,RSTIPO_DEMANDANTE
                    ,RSNUM_RADICACION
                    ,RSFECHA_RADICACION
                    ,RSOBSERVACIONES
                    ,RSFORMA_PAGO
                    ,RSFACTOR1
                    ,RSFACTOR2
                    ,RSFACTOR3
                    ,RSFACTOR4
                    ,RSFACTOR5
                    ,RSESTADO
                    ,RSOFICINA_DESTINO
                    ,RSID_DE_CONCEPTO
                    ,RSCOMISION
                    ,RSCONCEPTO_DEPOSITO
                    ,RSCUENTA
                    ,RSNUMERO_PROCESO
                    ,RSDCTO_IDENTIDAD
                    ,RSCEDULA
                    ,RSBANCO
                    ,RSDEMANDANTE_NOMBRES
                    ,RSPORCENTAJE_CESANTIAS
                    ,RSPORCENTAJE_PRIMAS;
      EXIT WHEN RS%NOTFOUND;
        IF RSSALDO - NVL(RSVALORH,0) > 0 THEN
          TEMDIF_TABLA(MI_ACUMULADOR).COMPANIA := UN_COMPANIA;
          TEMDIF_TABLA(MI_ACUMULADOR).ID_DE_PROCESO := RSID_DE_PROCESO;
          TEMDIF_TABLA(MI_ACUMULADOR).ANO := PCK_SYSMAN_UTL.FC_IIF(
                                                            UN_CONDICION => RSFORMA_PAGO='01'
                                                                            AND UN_MES1+1 =13
                                                           ,UN_SI        => UN_ANO2+1
                                                           ,UN_NO        => UN_ANO2);
          IF RSFORMA_PAGO = '01' THEN
            IF UN_PERIODO1 = 1 THEN
               TEMDIF_TABLA(MI_ACUMULADOR).MES := PCK_SYSMAN_UTL.FC_IIF(
                                                                 UN_CONDICION => UN_MES1+1=13
                                                                ,UN_SI        => 1
                                                                ,UN_NO        => 2);
            ELSE
               TEMDIF_TABLA(MI_ACUMULADOR).MES := UN_MES2;
            END IF;
          ELSE
            TEMDIF_TABLA(MI_ACUMULADOR).MES := UN_MES2;
          END IF;

          DECLARE
            MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
          BEGIN
            MI_SIGUIENTE := FALSE;
            IF RSFORMA_PAGO='00' THEN
               MI_QUERY_PERIODOS:='SELECT COUNT(*)
                                     FROM DUAL
                                    WHERE '||UN_PERIODO2||' IN ('||MI_RANGO_PERIODOS||')';
               EXECUTE IMMEDIATE MI_QUERY_PERIODOS INTO MI_AUX_PERIODOS;
                 IF MI_AUX_PERIODOS<>0 THEN
                  TEMDIF_TABLA(MI_ACUMULADOR).PERIODO :=UN_PERIODO2;
                 ELSE
                    --Para el embargo el periodo de cobro : --PERIODO-- esta errado, Para el empleado --NOMEMPLEADO--
                    MI_MSG(1).CLAVE := 'PERIODO';
                    MI_MSG(1).VALOR := UN_PERIODO2;
                    MI_MSG(2).CLAVE := 'NOMEMPLEADO';
                    MI_MSG(2).VALOR := RSID_DE_EMPLEADO;

                    PCK_NOMINA_COM7.PR_ALERTA
                        (UN_COMPANIA     => UN_COMPANIA
                        ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_EMBARGOPERNOEXISTE
                        ,UN_REEMPLAZOS   => MI_MSG
                        ,UN_PROCESO      => RSID_DE_PROCESO
                       ,UN_ANO          => RSANO
                       ,UN_MES          => RSMES
                       ,UN_PERIODO      => RSPERIODO
                       ,UN_USER         => UN_USUARIO
                        );
                    MI_SIGUIENTE := TRUE;
                 END IF;
            ELSE
               MI_QUERY_PERIODOS:='SELECT COUNT(*)
                                     FROM DUAL
                                    WHERE '||RSFORMA_PAGO||' IN ('||MI_RANGO_PERIODOS||')';
               EXECUTE IMMEDIATE MI_QUERY_PERIODOS INTO MI_AUX_PERIODOS;
               IF MI_AUX_PERIODOS<>0 THEN
                TEMDIF_TABLA(MI_ACUMULADOR).PERIODO := RSFORMA_PAGO;
               ELSE
                   --Para el embargo el periodo de cobro : --PERIODO-- esta errado, Para el empleado --NOMEMPLEADO--
                   MI_MSG(1).CLAVE := 'PERIODO';
                   MI_MSG(1).VALOR := RSFORMA_PAGO;
                   MI_MSG(2).CLAVE := 'NOMEMPLEADO';
                   MI_MSG(2).VALOR := RSID_DE_EMPLEADO;

                   PCK_NOMINA_COM7.PR_ALERTA
                       (UN_COMPANIA     => UN_COMPANIA
                       ,UN_MENSAJE_COD  => PCK_ERRORES.ERRRR_EMBARGOPERNOEXISTE
                       ,UN_REEMPLAZOS   => MI_MSG
                       ,UN_PROCESO      => RSID_DE_PROCESO
                       ,UN_ANO          => RSANO
                       ,UN_MES          => RSMES
                       ,UN_PERIODO      => RSPERIODO
                       ,UN_USER         => UN_USUARIO
                       );
                   MI_SIGUIENTE := TRUE;
               END IF;
            END IF;
          /*EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_NOMINA_PERIODONOEXISTE
                       ,UN_TABLAERROR => 'EMBARGOS'
                     );*/
          END;

          IF MI_SIGUIENTE = FALSE THEN
              TEMDIF_TABLA(MI_ACUMULADOR).ID_DE_EMPLEADO := RSID_DE_EMPLEADO;
              TEMDIF_TABLA(MI_ACUMULADOR).ID_EMBARGO := RSID_EMBARGO;
              TEMDIF_TABLA(MI_ACUMULADOR).ID_JUZGADO := RSID_JUZGADO;
              TEMDIF_TABLA(MI_ACUMULADOR).FECHA_EMBARGO := RSFECHA_EMBARGO;
              TEMDIF_TABLA(MI_ACUMULADOR).OFICIO := RSOFICIO;
              TEMDIF_TABLA(MI_ACUMULADOR).MONTO_EMBARGO := RSMONTO_EMBARGO;
              TEMDIF_TABLA(MI_ACUMULADOR).PORCENTAJE := RSPORCENTAJE;
              TEMDIF_TABLA(MI_ACUMULADOR).VALOR := RSVALOR;
              TEMDIF_TABLA(MI_ACUMULADOR).SALDO := RSSALDO - PCK_SYSMAN_UTL.FC_IIF(
                                                                            UN_CONDICION => RSVALORH >0
                                                                           ,UN_SI => RSVALORH
                                                                           ,UN_NO => 0);
              TEMDIF_TABLA(MI_ACUMULADOR).CUOTAS := RSCUOTAS-1;
              IF TEMDIF_TABLA(MI_ACUMULADOR).CUOTAS < 0 THEN 
                TEMDIF_TABLA(MI_ACUMULADOR).CUOTAS := 0;
              END IF;
              TEMDIF_TABLA(MI_ACUMULADOR).VR_CUOTA := RSVLRCUOTA;
              TEMDIF_TABLA(MI_ACUMULADOR).DEMANDANTE := RSDEMANDANTE;
              TEMDIF_TABLA(MI_ACUMULADOR).TIPO_DEMANDANTE := RSTIPO_DEMANDANTE;
              TEMDIF_TABLA(MI_ACUMULADOR).NUM_RADICACION := RSNUM_RADICACION;
              TEMDIF_TABLA(MI_ACUMULADOR).FECHA_RADICACION := RSFECHA_RADICACION;
              TEMDIF_TABLA(MI_ACUMULADOR).OBSERVACIONES := RSOBSERVACIONES;
              TEMDIF_TABLA(MI_ACUMULADOR).FORMA_PAGO := RSFORMA_PAGO;
              TEMDIF_TABLA(MI_ACUMULADOR).FACTOR1 := RSFACTOR1;
              TEMDIF_TABLA(MI_ACUMULADOR).FACTOR2 := RSFACTOR2;
              TEMDIF_TABLA(MI_ACUMULADOR).FACTOR3 := RSFACTOR3;
              TEMDIF_TABLA(MI_ACUMULADOR).FACTOR4 := RSFACTOR4;
              TEMDIF_TABLA(MI_ACUMULADOR).FACTOR5 := RSFACTOR5 ;
              TEMDIF_TABLA(MI_ACUMULADOR).ESTADO := RSESTADO;
              TEMDIF_TABLA(MI_ACUMULADOR).CONCEPTO_DEPOSITO := RSCONCEPTO_DEPOSITO;
              TEMDIF_TABLA(MI_ACUMULADOR).CUENTA := RSCUENTA;
              TEMDIF_TABLA(MI_ACUMULADOR).DCTO_IDENTIDAD := RSDCTO_IDENTIDAD;
              TEMDIF_TABLA(MI_ACUMULADOR).CEDULA := RSCEDULA;
              TEMDIF_TABLA(MI_ACUMULADOR).BANCO := RSBANCO;
              TEMDIF_TABLA(MI_ACUMULADOR).DEMANDANTE_NOMBRES := RSDEMANDANTE_NOMBRES;
              TEMDIF_TABLA(MI_ACUMULADOR).PORCENTAJE_CESANTIAS := RSPORCENTAJE_CESANTIAS;
              TEMDIF_TABLA(MI_ACUMULADOR).PORCENTAJE_PRIMAS := RSPORCENTAJE_PRIMAS;
              TEMDIF_TABLA(MI_ACUMULADOR).OFICINA_DESTINO := RSOFICINA_DESTINO;
              TEMDIF_TABLA(MI_ACUMULADOR).ID_DE_CONCEPTO := RSID_DE_CONCEPTO;
              TEMDIF_TABLA(MI_ACUMULADOR).COMISION := NVL(RSCOMISION,0);
              TEMDIF_TABLA(MI_ACUMULADOR).NUMERO_PROCESO:=NVL(RSNUMERO_PROCESO,'');

              MI_ACUMULADOR := MI_ACUMULADOR+1;
          ELSE 
              TEMDIF_TABLA.DELETE(MI_ACUMULADOR);
          END IF;
        END IF;

    END LOOP EMBARGO;
  CLOSE RS;

  MI_CAMPOS:='COMPANIA           ,ID_DE_PROCESO
             ,ANO                ,MES
             ,PERIODO            ,ID_DE_EMPLEADO
             ,ID_EMBARGO         ,ID_JUZGADO
             ,FECHA_EMBARGO      ,OFICIO
             ,MONTO_EMBARGO      ,PORCENTAJE
             ,VALOR              ,SALDO
             ,CUOTAS             ,DEMANDANTE
             ,TIPO_DEMANDANTE    ,NUM_RADICACION
             ,FECHA_RADICACION   ,OBSERVACIONES
             ,FORMA_PAGO         ,FACTOR1
             ,FACTOR2            ,FACTOR3
             ,FACTOR4            ,FACTOR5
             ,ESTADO             ,OFICINA_DESTINO
             ,ID_DE_CONCEPTO     ,COMISION
             ,CONCEPTO_DEPOSITO  ,CUENTA
             ,NUMERO_PROCESO     ,DCTO_IDENTIDAD
             ,CEDULA             ,BANCO
             ,DEMANDANTE_NOMBRES ,PORCENTAJE_CESANTIAS
             ,PORCENTAJE_PRIMAS  ,DATE_CREATED
             ,CREATED_BY         ,VR_CUOTA';

  IF MI_ACUMULADOR > 1 THEN
    FOR I IN TEMDIF_TABLA.FIRST..TEMDIF_TABLA.LAST
    LOOP
      MI_VALORES := ''''||TEMDIF_TABLA(I).COMPANIA                                    ||'''
                    ,  '||TEMDIF_TABLA(I).ID_DE_PROCESO                               ||'
                    ,  '||TEMDIF_TABLA(I).ANO                                         ||'
                    ,  '||TEMDIF_TABLA(I).MES                                         ||'
                    ,  '||TEMDIF_TABLA(I).PERIODO                                     ||'
                    ,  '||TEMDIF_TABLA(I).ID_DE_EMPLEADO                              ||'
                    ,'''||TEMDIF_TABLA(I).ID_EMBARGO                                  ||'''
                    ,'''||TEMDIF_TABLA(I).ID_JUZGADO                                  ||'''
                    ,  '|| CASE WHEN TEMDIF_TABLA(I).FECHA_EMBARGO IS NULL
                          THEN 'NULL'
                          ELSE  'TO_DATE(''' || TO_CHAR(TEMDIF_TABLA(I).FECHA_EMBARGO, 'DD/MM/YYYY') ||''',''DD/MM/YYYY'')' END ||'
                    ,  '''||TEMDIF_TABLA(I).OFICIO                                      ||'''
                    ,  '||TEMDIF_TABLA(I).MONTO_EMBARGO                               ||'
                    ,  '||TEMDIF_TABLA(I).PORCENTAJE                                  ||'
                    ,  '||TEMDIF_TABLA(I).VALOR                                       ||'
                    ,  '||TEMDIF_TABLA(I).SALDO                                       ||'
                    ,  '||NVL(''''||TEMDIF_TABLA(I).CUOTAS           ||'''','NULL')   ||'
                    ,  '||NVL(''''||TEMDIF_TABLA(I).DEMANDANTE       ||'''','NULL')   ||'
                    ,  '||NVL(''''||TEMDIF_TABLA(I).TIPO_DEMANDANTE  ||'''','NULL')   ||'
                    ,  '||NVL(''''||TEMDIF_TABLA(I).NUM_RADICACION   ||'''','NULL')   ||'
                    ,  '|| CASE WHEN TEMDIF_TABLA(I).FECHA_RADICACION IS NULL
                        THEN 'NULL'
                        ELSE  'TO_DATE(''' || TO_CHAR(TEMDIF_TABLA(I).FECHA_RADICACION, 'DD/MM/YYYY') ||''',''DD/MM/YYYY'')' END ||'
                    ,  '||NVL(''''||TEMDIF_TABLA(I).OBSERVACIONES    ||'''','NULL')   ||'
                    ,  '||NVL(''''||TEMDIF_TABLA(I).FORMA_PAGO       ||'''','NULL')   ||'
                    ,  '|| CASE WHEN TEMDIF_TABLA(I).FACTOR1 IS NULL THEN 'NULL' ELSE ''||TEMDIF_TABLA(I).FACTOR1 END ||'
                    ,  '|| CASE WHEN TEMDIF_TABLA(I).FACTOR2 IS NULL THEN 'NULL' ELSE ''||TEMDIF_TABLA(I).FACTOR2 END ||'
                    ,  '|| CASE WHEN TEMDIF_TABLA(I).FACTOR3 IS NULL THEN 'NULL' ELSE ''||TEMDIF_TABLA(I).FACTOR3 END ||'
                    ,  '|| CASE WHEN TEMDIF_TABLA(I).FACTOR4 IS NULL THEN 'NULL' ELSE ''||TEMDIF_TABLA(I).FACTOR4 END ||'
                    ,  '|| CASE WHEN TEMDIF_TABLA(I).FACTOR5 IS NULL THEN 'NULL' ELSE ''||TEMDIF_TABLA(I).FACTOR5 END ||'
                    ,  '||NVL(''''||TEMDIF_TABLA(I).ESTADO           ||'''','NULL')   ||'
                    ,  '||NVL(''''||TEMDIF_TABLA(I).OFICINA_DESTINO  ||'''','NULL')   ||'
                    ,  '|| CASE WHEN TEMDIF_TABLA(I).ID_DE_CONCEPTO IS NULL THEN 'NULL' ELSE ''||TEMDIF_TABLA(I).ID_DE_CONCEPTO END ||'
                    ,  '||TEMDIF_TABLA(I).COMISION                                    ||'
                    ,  '||NVL(''''||TEMDIF_TABLA(I).CONCEPTO_DEPOSITO ||'''','NULL')  ||'
                    ,  '||NVL(''''||TEMDIF_TABLA(I).CUENTA            ||'''','NULL')  ||'
                    ,  '||NVL(''''||TEMDIF_TABLA(I).NUMERO_PROCESO    ||'''','NULL')  ||'
                    ,  '||NVL(''''||TEMDIF_TABLA(I).DCTO_IDENTIDAD    ||'''','NULL')  ||'
                    ,  '||NVL(''''||TEMDIF_TABLA(I).CEDULA            ||'''','NULL')  ||'
                    ,  '||NVL(''''||TEMDIF_TABLA(I).BANCO             ||'''','NULL')  ||'
                    ,  '||NVL(''''||TEMDIF_TABLA(I).DEMANDANTE_NOMBRES||'''','NULL')  ||'
                    ,  '||TEMDIF_TABLA(I).PORCENTAJE_CESANTIAS                        ||'
                    ,  '||TEMDIF_TABLA(I).PORCENTAJE_PRIMAS                           ||'
                    ,   SYSDATE
                    ,'''||UN_USUARIO                                                  ||'''
                    ,  '||TEMDIF_TABLA(I).VR_CUOTA                                    ||'';

                  IF TEMDIF_TABLA(I).MONTO_EMBARGO > TEMDIF_TABLA(I).SALDO  THEN --(Inicio,HU:7802826,CFBRRERA, Cuando se prepara un embargo al mes siguiente cambie el estado Actual)
                        IF TEMDIF_TABLA(I).SALDO = 0 THEN
                          MI_VALORES := REPLACE(MI_VALORES, '''' || TEMDIF_TABLA(I).ESTADO || '''', '''C''');
                       ELSE
                          MI_VALORES := REPLACE(MI_VALORES, '''' || TEMDIF_TABLA(I).ESTADO || '''', '''T''');
                       END IF;
                  END IF;--(Fin,HU:7802826,CFBRRERA, Cuando se prepara un embargo al mes siguiente cambie el estado Actual)

      BEGIN
          MI_EXISTEEMBARG := 0;
          SELECT COUNT(0) AS CUENTA
          INTO   MI_EXISTEEMBARG
          FROM   EMBARGOS
          WHERE  COMPANIA  = TEMDIF_TABLA(I).COMPANIA
            AND  ID_DE_PROCESO = TEMDIF_TABLA(I).ID_DE_PROCESO
            AND  ANO = TEMDIF_TABLA(I).ANO
            AND  MES = TEMDIF_TABLA(I).MES
            AND  PERIODO = TEMDIF_TABLA(I).PERIODO
            AND  ID_DE_EMPLEADO = TEMDIF_TABLA(I).ID_DE_EMPLEADO
            AND  ID_EMBARGO = TEMDIF_TABLA(I).ID_EMBARGO
            AND  ID_JUZGADO = TEMDIF_TABLA(I).ID_JUZGADO
            AND  FECHA_EMBARGO = TEMDIF_TABLA(I).FECHA_EMBARGO;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_EXISTEEMBARG := 0;
      END;

      IF MI_EXISTEEMBARG = 0 THEN
          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                            UN_TABLA   => 'EMBARGOS'
                                           ,UN_ACCION  => 'I'
                                           ,UN_CAMPOS  =>  MI_CAMPOS
                                           ,UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_NOMINA;
            END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_NOMINA_PREPARAREMBARGOS
                       ,UN_TABLAERROR => 'EMBARGOS');
          END;
      END IF;
    END LOOP;
  END IF;

END PR_PREPARAREMBARGOS;

--12
PROCEDURE PR_RETEFUENTE
/*
    NAME              : PR_RETEFUENTE  --> EN ACCESS Retefuente_Click  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JHONATAN ACELAS AREVALO
    DATE MIGRADOR     : 24/07/2015
    TIME              : 12:15 PM
    SOURCE MODULE     :
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 09/02/2017
    TIME              : 
    DESCRIPTION       : \ SE AJUSTA AL ESTANDAR.
    @NAME             : retefuente
    @METHOD           : POST
  */
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO_NUEVO   IN PCK_SUBTIPOS.TI_ANIO,
    UN_ANO_SESSION IN PCK_SUBTIPOS.TI_ANIO,    
    UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
    MI_FECH1        DATE;
    MI_FECH2        DATE;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_EXISTE       PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_MERGEENLACE  PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE  PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGENOEXIS  PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_USING        PCK_SUBTIPOS.TI_MERGEUSING;
    MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
BEGIN
  SELECT COUNT(*) 
    INTO MI_EXISTE 
    FROM ANO 
   WHERE NUMERO = UN_ANO_NUEVO;

  IF MI_EXISTE = 0 THEN 
    MI_CAMPOS:= 'COMPANIA,NUMERO,ESTADO';
    MI_VALORES:=''''||UN_COMPANIA||''','||UN_ANO_NUEVO||',''A'''; 
    MI_TABLA := 'ANO';
    BEGIN 
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                    UN_TABLA   => MI_TABLA
                                   ,UN_ACCION  => 'I'
                                   ,UN_CAMPOS  => MI_CAMPOS
                                   ,UN_VALORES => MI_VALORES);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
      RAISE PCK_EXCEPCIONES.EXC_NOMINA;
    END;

  --  PCK_NOMINA_COM2.PR_CREARCENTROSDECOSTO(UN_ANO_NUEVO,UN_ANO_SESSION,UN_COMPANIA);
  END IF;

  FOR RS IN (SELECT  PERIODOS.COMPANIA
                    ,PERIODOS.ID_DE_PROCESO
                    ,PERIODOS.ANO
                    ,PERIODOS.MES
                    ,PERIODOS.PERIODO
                    ,PERIODOS.NOM_PERIODO
                    ,PERIODOS.ESTADO
                    ,PERIODOS.ACUMULADO
                    ,PERIODOS.FECHAINICIO
                    ,PERIODOS.FECHAFINAL
                    ,PERIODOS.DIAS
                    ,PERIODOS.DIFERIDOS 
             FROM PERIODOS
             WHERE COMPANIA         = UN_COMPANIA
               AND PERIODOS.ANO     = UN_ANO_SESSION
               AND (PERIODOS.PERIODO BETWEEN 1 AND 4  
                   OR PERIODOS.PERIODO = 8))
  LOOP
    MI_FECH1 := TO_CHAR(RS.FECHAINICIO,'DD')||'/'||TO_CHAR(RS.FECHAINICIO,'MM')||'/'||UN_ANO_NUEVO;
    IF TO_CHAR(RS.FECHAFINAL,'MM') = 2  AND TO_CHAR(RS.FECHAFINAL,'DD') >= 29 THEN 
       MI_FECH2 := '28'||'/'|| TO_CHAR(RS.FECHAFINAL,'MM')||'/'|| UN_ANO_NUEVO;
    ELSE 
       MI_FECH2:= TO_CHAR(RS.FECHAFINAL,'DD')||'/'||TO_CHAR(RS.FECHAFINAL,'MM')||'/'||UN_ANO_NUEVO;
    END IF;
    ------------------------------------------------------------------------------------------------    
    ------------------------------------------------------------------------------------------------    
    -- PENDIENTE OPTIMIZAR CURSOR A SOLO UN LLAMADO DEL MERGE 
    ------------------------------------------------------------------------------------------------    
    ------------------------------------------------------------------------------------------------    

    MI_CAMPOS:='COMPANIA,'||
               'ID_DE_PROCESO,'||
               'ANO,'||
               'MES,'||
               'PERIODO,'||
               'NOM_PERIODO,'||
               'ESTADO,'||
               'ACUMULADO,'||
               'FECHAINICIO,'||
               'FECHAFINAL,'||
               'DIAS,'||
               'DIFERIDOS,'||
               'DATE_CREATED,'||
               'CREATED_BY'; 
          --  MI_VALORES:=''''||RS.COMPANIA||''','||RS.ID_DE_PROCESO||','||UN_ANO_NUEVO||','||RS.MES||','||RS.PERIODO||','''||RS.NOM_PERIODO||''',-1,-1, TO_DATE('''|| TO_CHAR(MI_FECH1,'DD/MM/YYYY')||''', ''DD/MM/YYYY''), TO_DATE('''|| TO_CHAR(MI_FECH2,'DD/MM/YYYY')||''', ''DD/MM/YYYY''),'||RS.DIAS ||' ,'||PCK_SYSMAN_UTL.FC_IIF(RS.PERIODO IN (1,2,3),-1,0) ;
    MI_VALORES:=''''||RS.COMPANIA||''','
                    ||RS.ID_DE_PROCESO||','
                    ||UN_ANO_NUEVO||','
                    ||RS.MES||','
                    ||RS.PERIODO||','''
                    ||RS.NOM_PERIODO||''',-1,-1, TO_DATE('''
                      || TO_CHAR(MI_FECH1,'DD/MM/YYYY')||''', ''DD/MM/YYYY''), TO_DATE('''
                      || TO_CHAR(MI_FECH2,'DD/MM/YYYY')||''', ''DD/MM/YYYY''),'
                    ||RS.DIAS ||' ,'
                    ||PCK_SYSMAN_UTL.FC_IIF(RS.PERIODO IN (1,2,3),-1,0)||','||
                    'SYSDATE,'||
                    ''''||UN_USUARIO||'''';

    MI_USING:=' SELECT '''||RS.COMPANIA||''' COMPANIA,'||
                RS.ID_DE_PROCESO||' ID_DE_PROCESO,'||
                UN_ANO_NUEVO||' ANO,'||
                RS.MES||' MES,'||
                RS.PERIODO||' PERIODO FROM DUAL';
  -- ,'''||          RS.NOM_PERIODO||''',-1,-1, TO_DATE('''|| TO_CHAR(MI_FECH1,'DD/MM/YYYY')||''', ''DD/MM/YYYY''), TO_DATE('''|| TO_CHAR(MI_FECH2,'DD/MM/YYYY')||''', ''DD/MM/YYYY''),'||RS.DIAS ||' ,'||PCK_SYSMAN_UTL.FC_IIF(RS.PERIODO IN (1,2,3),-1,0) || ' FROM DUAL '  ;
  --  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ('PERIODOS', 'I', MI_CAMPOS, MI_VALORES, NULL, NULL );
  -- CAMBIO MERGE 

  MI_MERGEENLACE := '  TABLA.COMPANIA       = VISTA.COMPANIA
                   AND TABLA.ID_DE_PROCESO  = VISTA.ID_DE_PROCESO
                   AND TABLA.ANO            = VISTA.ANO
                   AND TABLA.MES            = VISTA.MES
                   AND TABLA.PERIODO        = VISTA.PERIODO';

  MI_MERGEEXISTE := ' UPDATE SET TABLA.OBSERVACION =  TABLA.OBSERVACION  ' ;
  MI_MERGENOEXIS := ' INSERT ('||MI_CAMPOS|| ') VALUES ('|| MI_VALORES||')';
  MI_TABLA := 'PERIODOS';
  BEGIN
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                  UN_TABLA       => MI_TABLA 
                                 ,UN_ACCION      => 'IM'
                                 ,UN_MERGEUSING  => MI_USING
                                 ,UN_MERGEENLACE => MI_MERGEENLACE
                                 ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                 ,UN_MERGENOEXIS => MI_MERGENOEXIS);
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
      RAISE PCK_EXCEPCIONES.EXC_NOMINA;
  END;
  -- CAMBIO MERGE 

  END LOOP;

  MI_VALORES := ' DIFERIDOS     =  -1,'||
                ' DATE_MODIFIED =SYSDATE,'||
                ' MODIFIED_BY   ='''||UN_USUARIO||'''';
  MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' AND PERIODO < 4' ;
  MI_TABLA :=  'PERIODOS';
  BEGIN
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                  UN_TABLA     => MI_TABLA 
                                 ,UN_ACCION    => 'M'
                                 ,UN_CAMPOS   => MI_VALORES
                                 ,UN_CONDICION => MI_CONDICION );
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
      RAISE PCK_EXCEPCIONES.EXC_NOMINA;
  END;

  MI_VALORES   := ' DIFERIDOS =  0,'||
                  ' DATE_MODIFIED =SYSDATE,'||
                  ' MODIFIED_BY   ='''||UN_USUARIO||'''';
  MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' AND PERIODO >= 4' ;
  MI_TABLA     := 'PERIODOS';
  BEGIN 
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                  UN_TABLA     => MI_TABLA
                                 ,UN_ACCION    => 'M'
                                 ,UN_CAMPOS   => MI_VALORES
                                 ,UN_CONDICION => MI_CONDICION );
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
      RAISE PCK_EXCEPCIONES.EXC_NOMINA;
  END;

  IF PCK_SYSMAN_UTL.FC_PAR(
                    UN_COMPANIA  => UN_COMPANIA
                   ,UN_NOMBRE    => 'NOMINA MENSUAL'
                   ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                   ,UN_FECHA_PAR => SYSDATE)= 'SI' THEN 
     MI_VALORES   := ' DIFERIDOS =  0,'||
                     ' DATE_MODIFIED =SYSDATE,'||
                     ' MODIFIED_BY   ='''||UN_USUARIO||'''';
     MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' AND PERIODO <> 3' ;
     MI_TABLA     := 'PERIODOS';

     BEGIN 
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                    UN_TABLA     => MI_TABLA
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS   => MI_VALORES
                                   ,UN_CONDICION => MI_CONDICION );
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
       RAISE PCK_EXCEPCIONES.EXC_NOMINA;
     END;
  ELSE 
     MI_VALORES := ' DIFERIDOS =  0,'||
                   ' DATE_MODIFIED =SYSDATE,'||
                   ' MODIFIED_BY   ='''||UN_USUARIO||'''';
     MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || ''' AND PERIODO >=3' ;
     MI_TABLA     := 'PERIODOS';
     BEGIN 
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                    UN_TABLA     => MI_TABLA
                                   ,UN_ACCION    => 'M'
                                   ,UN_CAMPOS   => MI_VALORES
                                   ,UN_CONDICION => MI_CONDICION );
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
       RAISE PCK_EXCEPCIONES.EXC_NOMINA;
     END;
  END IF;
 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_ERROR_COD  => PCK_ERRORES.ERRR_NOMINA_RETEFUENTE
                 ,UN_TABLAERROR => MI_TABLA); 

END PR_RETEFUENTE;

--13
FUNCTION FC_AUSENTISMO
/*
    NAME              : FC_AUSENTISMO == > ACCES AUSENTISMO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JHONATAN ACELAS AREVALO
    DATE MIGRADOR     : 4/08/2015
    TIME              : 17:50 
    SOURCE MODULE     : 
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 09/02/2017
    TIME              : 
    DESCRIPTION       : RETORNA EL VALOR TOTAL DE EL USENTISMO DE UN EMPLEADO 
                        \ SE AJUSTA AL ESTANDAR. 
    @NAME             : getAusentismoEmpleadoTotal
    @METHOD           : GET                    
    */
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ID_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO 
   ) 
RETURN PCK_SUBTIPOS.TI_DOBLE
  AS 
    MI_REGISTROS  NUMBER:=0;
    MI_VALOR      PCK_SUBTIPOS.TI_DOBLE;
BEGIN
  BEGIN 
    SELECT SUM(HISTORICOS.VALOR) 
      INTO MI_VALOR
      FROM HISTORICOS
     WHERE HISTORICOS.ID_DE_EMPLEADO = UN_ID_EMPLEADO 
       AND HISTORICOS.ID_DE_CONCEPTO IN ('356','357','359')  
       AND HISTORICOS.COMPANIA       = UN_COMPANIA;
  EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_VALOR := 0;
  END;
  IF MI_VALOR IS NULL THEN 
     MI_VALOR:=0;
  END IF;

  RETURN MI_VALOR;
END FC_AUSENTISMO;

--14
FUNCTION FC_AUTORIZARENVIOCORREO
/*
    NAME              : FC_AUTORIZARENVIOCORREO == > NUEVO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JHONATAN ACELAS AREVALO
    DATE MIGRADOR     : 17/09/2015
    TIME              : 12:13 
    SOURCE MODULE     : 
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 09/02/2017
    TIME              : 
    DESCRIPTION       : RETORNA FALSO O VERDADERO EVALUANDO PARAMETRO 31  
                        \ SE AJUSTA AL ESTANDAR.
    @NAME             : getAutorizarEnvioCorreo
    @METHOD           : GET
    */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_MODULO   IN PCK_SUBTIPOS.TI_MODULO
   ) 
RETURN NUMBER
  AS  
    MI_PERMISO  PCK_SUBTIPOS.TI_LOGICO:=0;
BEGIN
  IF  PCK_NOMINA_COM1.FC_PARAMETRO(
                      UN_COMPANIA => UN_COMPANIA
                     ,UN_OPCION   => 31 ) = '899.999.428-8'  THEN  
     MI_PERMISO:= -1;
  END IF;

  RETURN MI_PERMISO;
END FC_AUTORIZARENVIOCORREO;

--15
FUNCTION FC_DIFERIR
/*
    NAME              : DIFERIR
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GÓMEZ BLANCO
    DATE MIGRADOR     : 14/10/2015
    TIME              : 02:43 
    SOURCE MODULE     : 
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 09/02/2017
    TIME              : 
    DESCRIPTION       : DIFIERE LOS VALORES DE UNA NOVEDAD ENTRE LOS PERIODOS ACUMULABLES Y DIFERIDOS
                        \ SE AJUSTA AL ESTANDAR.
    @NAME             : getDiferir
    @METHOD           : GET                    
    */
(
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROCESO     IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_ANIO        IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES         IN PCK_SUBTIPOS.TI_MES,
  UN_PERIODO     IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
  UN_ID_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
  UN_CONCEPTO    IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
  UN_DIFERIDO    IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
  UN_OPCION      IN VARCHAR2 DEFAULT NULL,
  UN_CONCEPTO1   IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO DEFAULT NULL,
  UN_VALOR1      IN PCK_SUBTIPOS.TI_DOBLE DEFAULT NULL,
  UN_CONCEPTO2   IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO DEFAULT NULL,
  UN_VALOR2      IN PCK_SUBTIPOS.TI_DOBLE DEFAULT NULL,
  UN_FECHAINICIO IN DATE,
  UN_INCAPACIDAD IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 0,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO,
  UN_FECHAFIN IN DATE DEFAULT NULL
)
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_IDIASDISFRUTE    PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  MI_OPCION           VARCHAR(100);
  MI_ANIO             PCK_SUBTIPOS.TI_ANIO;
  MI_MES              PCK_SUBTIPOS.TI_MES;
  MI_PERIODO          PCK_SUBTIPOS.TI_PERIODO_NOMI;
  MI_DIAFINAL         PCK_SUBTIPOS.TI_ENTERO;
  MI_DIAINICIO        PCK_SUBTIPOS.TI_ENTERO;
  MI_ANIOINICIO       PCK_SUBTIPOS.TI_ANIO;
  MI_MESINICIO        PCK_SUBTIPOS.TI_MES;
  MI_NIT              PCK_SUBTIPOS.TI_TERCERO;
  MI_DIFERIDO         PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_SIGUIENTE        PCK_SUBTIPOS.TI_TERCERO;
  MI_DIASPER          PCK_SUBTIPOS.TI_ENTERO;
  MI_VALOR            PCK_SUBTIPOS.TI_DOBLE;
  MI_ACCION           PCK_SUBTIPOS.TI_LOGICO;
  MI_AC               PCK_SUBTIPOS.TI_DOBLE;
  MI_DIAS31           PCK_SUBTIPOS.TI_ENTERO;
  MI_LASTDAY          DATE;                     -- JM CC 4481
  MI_31SABDOM         PCK_SUBTIPOS.TI_ENTERO;   -- JM CC 4481
  MI_31FESTIVO        PCK_SUBTIPOS.TI_ENTERO;   -- JM CC 4481
  MI_PARAMETRO        PARAMETRO.VALOR%TYPE;     -- JM CC 4481
BEGIN
  MI_DIFERIDO :=UN_DIFERIDO;
  MI_OPCION   :=UPPER(UN_OPCION);
  IF  UN_OPCION IS NULL THEN
    MI_OPCION:='ADICIONAR';
  END IF;

  FOR RS IN (
    SELECT   ANO
           , MES
           , PERIODO
           , FECHAFINAL
           , FECHAINICIO
    FROM PERIODOS
    WHERE COMPANIA        = UN_COMPANIA
      AND ID_DE_PROCESO   = UN_PROCESO
      AND ACUMULADO       NOT IN (0)
      AND DIFERIDOS       NOT IN (0)
      AND UN_FECHAINICIO  BETWEEN FECHAINICIO AND FECHAFINAL
    )
  LOOP
    MI_ANIO     := RS.ANO;
    MI_MES      := RS.MES;
    MI_PERIODO  := RS.PERIODO;    
    MI_DIAFINAL := TO_NUMBER(TO_CHAR(RS.FECHAFINAL ,'DD'));
    MI_DIAINICIO:= TO_NUMBER(TO_CHAR(UN_FECHAINICIO,'DD'));
    MI_DIAS31 := 0;
    IF MI_DIAFINAL>16 AND MI_DIAFINAL<>31 THEN
      MI_IDIASDISFRUTE:=30-MI_DIAINICIO+1;
    ELSIF MI_DIAFINAL=31 THEN

       -- JM CC 4481 me traigo lo mismo del utl 
        BEGIN
            SELECT LAST_DAY(TO_DATE(TO_CHAR(RS.FECHAINICIO,'DD/MM/YYYY'),'DD/MM/YYYY')) INTO MI_LASTDAY FROM DUAL;
            SELECT TO_NUMBER(TO_CHAR(MI_LASTDAY, 'D', 'NLS_DATE_LANGUAGE=ENGLISH')) INTO MI_31SABDOM FROM DUAL;
            SELECT  COUNT(0)  INTO MI_31FESTIVO FROM  FESTIVOS  WHERE   COMPANIA = UN_COMPANIA AND   TO_CHAR(ID_DE_FESTIVO,'YYYYMMDD') = TO_CHAR(MI_LASTDAY, 'YYYYMMDD');
        END;
        
         MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'CONTAR SABADOS COMO DIA HABIL',6,SYSDATE);
         IF (UPPER(MI_PARAMETRO) = 'SI') AND MI_31SABDOM = 7  THEN
            MI_31SABDOM := 0;
         END IF;
        -- JM FIN CC 4481

      --MI_IDIASDISFRUTE:=31-MI_DIAINICIO+1;
      --MZANGUNA:02/08/2018, Se revisa contra access y no se resta
      MI_IDIASDISFRUTE:=31-MI_DIAINICIO;
      --(APINEDA:29/03/2019)-Se agrega validación porque para las entidades que no pagan el día 31 en vacaciones, se esta restando el 31 dos veces. TAR1000091158 Bucaramanga
      IF NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PAGAR EL DIA 31 EN VACACIONES',6,SYSDATE), 'NO') = 'SI'  AND UN_CONCEPTO = 35 THEN --MOD JM CC 1473 PT2 05-05-2025, no lo estaba tomando en cuenta
         IF (MI_31SABDOM NOT IN (1,7) OR MI_31FESTIVO = 1) THEN --JM MOD CC 4481 (lo tome en cuenta pero igual que en el utl)
            MI_DIAS31 := 1;
         END IF;
      END IF;
      MI_NIT :=PCK_NOMINA_COM1.FC_PARAMETRO(
                               UN_COMPANIA => UN_COMPANIA
                              ,UN_OPCION   => 31);
      IF UN_INCAPACIDAD <>0 THEN
        IF MI_NIT IN('830.065.741-1','890704536-7','899999433-5') THEN
          MI_IDIASDISFRUTE:=MI_IDIASDISFRUTE-1;
        END IF;
      END IF;
    ELSE
      --(APINEDA:27/08/2019)-Se modifica sumando 1 de acuerdo a como se realiza proceso en access.  
      MI_IDIASDISFRUTE:= TRUNC(RS.FECHAFINAL)-TRUNC(UN_FECHAINICIO) + 1;
    END IF;
    IF MI_IDIASDISFRUTE>= MI_DIFERIDO THEN
      MI_IDIASDISFRUTE := MI_DIFERIDO;
    END IF;
        --Se agrega esta funcion segun lo establecido en acces, MIGUEL VENEGAS
   MI_AC := PCK_NOMINA_COM1.FC_ACUMNOVEDADCONCEPTO(UN_COMPANIA,MI_ANIO,MI_MES,MI_PERIODO,MI_ANIO,MI_MES,MI_PERIODO,UN_ID_EMPLEADO);
    IF MI_OPCION='ADICIONAR' THEN
        PCK_NOMINA.PR_INCLUIRNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO => UN_CONCEPTO
                  ,UN_VALOR      => MI_IDIASDISFRUTE
                  --,UN_ACCION     => CASE WHEN UN_CONCEPTO = '35' AND PCK_NOMINA.FC_CNA(35) > 0 THEN 1 ELSE NULL END); --(ORIGINAL MIGUEL VENEGAS)
                  --(APINEDA:11/10/2018)-Se agrega validación para cuando existe más de una incapacidad, se deben actualizar los días en la novedad.
                  ,UN_ACCION     => CASE WHEN  (UN_INCAPACIDAD <> 0) OR (UN_CONCEPTO = '35' AND PCK_NOMINA.FC_CNA(35) > 0) THEN 1 ELSE NULL END); 

      IF UN_CONCEPTO1 IS NOT NULL AND UN_VALOR1 IS NOT NULL THEN
        PCK_NOMINA.PR_INCLUIRNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO   => UN_CONCEPTO1
                  ,UN_VALOR      => UN_VALOR1);
      END IF;
      IF UN_CONCEPTO2 IS NOT NULL AND UN_VALOR2 IS NOT NULL THEN
        PCK_NOMINA.PR_INCLUIRNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO   => UN_CONCEPTO2
                  ,UN_VALOR      => UN_VALOR2);
      END IF;
    ELSE
      PCK_NOMINA.PR_BORRARNOVEDADX(
                 UN_COMPANIA   => UN_COMPANIA
                ,UN_PROCESO    => UN_PROCESO
                ,UN_ANIO       => MI_ANIO
                ,UN_MES        => MI_MES
                ,UN_PERIODO    => MI_PERIODO
                ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                ,UN_IDCONCEPTO => UN_CONCEPTO
                ,UN_CANTIDAD   => MI_IDIASDISFRUTE
                ,UN_USUARIO    => UN_USUARIO );      
      --(APINEDA:10/10/2018)-Se elimina el registro de concepto 94 solo si el registro eliminado corresponde a PERIODOS DE VACACIONES. Se estaba presentando un problema y estaba eliminando el concepto 94 al eliminar incapacidades, encargos o licencias.
      IF (UN_CONCEPTO = 164) THEN          
          PCK_NOMINA.PR_BORRARNOVEDAD(
                     UN_COMPANIA   => UN_COMPANIA
                    ,UN_PROCESO    => UN_PROCESO
                    ,UN_ANIO       => MI_ANIO
                    ,UN_MES        => MI_MES
                    ,UN_PERIODO    => MI_PERIODO
                    ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                    ,UN_IDCONCEPTO => 94);
      END IF;
      IF UN_CONCEPTO1 IS NOT NULL THEN
        PCK_NOMINA.PR_BORRARNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO => UN_CONCEPTO1);
      END IF;
      IF UN_CONCEPTO2 IS NOT NULL THEN
        PCK_NOMINA.PR_BORRARNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO => UN_CONCEPTO2);
      END IF;
    END IF;

    MI_DIFERIDO :=MI_DIFERIDO - MI_IDIASDISFRUTE - MI_DIAS31;
    MI_DIAS31 :=0;
    --(APINEDA:28/11/2018)-TAR1000087627 ANE Se agrega validación de acuerdo a versión NOMINAP2018.09.01 de access para no dejar variable diferido con valor -1
    IF MI_DIFERIDO = -1 THEN 
      MI_DIFERIDO := 0;
    END IF;     
    WHILE MI_DIFERIDO<>0 
    LOOP
      MI_SIGUIENTE :=PCK_NOMINA_COM2.FC_SIGUIENTEPERIODO( 
                                     UN_COMPANIA => UN_COMPANIA
                                    ,UN_ANO      => MI_ANIO
                                    ,UN_PERIODO => MI_PERIODO
                                    ,UN_MES => MI_MES
                                    ,UN_PROCESO => UN_PROCESO);
      IF MI_SIGUIENTE= ' ' 
         OR MI_SIGUIENTE IS NULL THEN
        RETURN 0;
      END IF;
      MI_ANIO     := TO_NUMBER(SUBSTR(MI_SIGUIENTE,1,4));
      MI_MES      := TO_NUMBER(SUBSTR(MI_SIGUIENTE,5,2));
      MI_PERIODO  := TO_NUMBER(SUBSTR(MI_SIGUIENTE,7,2));
      MI_DIASPER  := PCK_NOMINA.FC_DIASPERIODO(
                                UN_COMPANIA => UN_COMPANIA
                               ,UN_PROCESO  => UN_PROCESO
                               ,UN_ANIO     => MI_ANIO
                               ,UN_MES      => MI_MES
                               ,UN_PERIODO  => MI_PERIODO);
      IF UN_FECHAINICIO IS NOT NULL THEN
        MI_ANIOINICIO := TO_NUMBER(TO_CHAR(UN_FECHAINICIO,'YYYY'));
        MI_MESINICIO  := TO_NUMBER(TO_CHAR(UN_FECHAINICIO,'MM'));
        IF MI_ANIOINICIO=MI_ANIO AND MI_MESINICIO=MI_MES THEN
          IF MI_DIAINICIO<=15 AND MI_PERIODO=1 THEN
            MI_DIASPER:= 15 - MI_DIAINICIO + 1;
          ELSIF MI_DIAINICIO>15 AND MI_PERIODO>1 THEN
            MI_DIASPER:= 30 - MI_DIAINICIO + 1;
          END IF;
          IF MI_DIASPER<0 THEN
            MI_DIASPER:=0;
          END IF;
        END IF;
      END IF;
      IF MI_DIFERIDO>=MI_DIASPER THEN
        MI_VALOR    := MI_DIASPER;
        MI_DIFERIDO := MI_DIFERIDO- MI_DIASPER;
      ELSE
        MI_VALOR    := MI_DIFERIDO;
        MI_DIFERIDO :=0;
      END IF;      
      IF MI_OPCION='ADICIONAR' THEN
        PCK_NOMINA.PR_INCLUIRNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO => UN_CONCEPTO
                  ,UN_VALOR      =>  MI_VALOR);
        IF UN_CONCEPTO1 IS NOT NULL AND UN_VALOR1 IS NOT NULL THEN
          PCK_NOMINA.PR_INCLUIRNOVEDAD(
                     UN_COMPANIA   => UN_COMPANIA
                    ,UN_PROCESO    => UN_PROCESO
                    ,UN_ANIO       => MI_ANIO
                    ,UN_MES        => MI_MES
                    ,UN_PERIODO    => MI_PERIODO
                    ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                    ,UN_IDCONCEPTO => UN_CONCEPTO1
                    ,UN_VALOR      => UN_VALOR1);
        END IF;
        IF UN_CONCEPTO2 IS NOT NULL AND UN_VALOR2 IS NOT NULL THEN
          PCK_NOMINA.PR_INCLUIRNOVEDAD(
                     UN_COMPANIA   => UN_COMPANIA
                    ,UN_PROCESO    => UN_PROCESO
                    ,UN_ANIO       => MI_ANIO
                    ,UN_MES        => MI_MES
                    ,UN_PERIODO    => MI_PERIODO
                    ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                    ,UN_IDCONCEPTO => UN_CONCEPTO2
                    ,UN_VALOR      => UN_VALOR2);
        END IF;
      ELSE
        PCK_NOMINA.PR_BORRARNOVEDADX(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO => UN_CONCEPTO
                  ,UN_CANTIDAD   => MI_VALOR
                  ,UN_USUARIO    => UN_USUARIO);
        IF UN_CONCEPTO1 IS NOT NULL THEN
          PCK_NOMINA.PR_BORRARNOVEDAD(
                     UN_COMPANIA   => UN_COMPANIA
                    ,UN_PROCESO    => UN_PROCESO
                    ,UN_ANIO       => MI_ANIO
                    ,UN_MES        => MI_MES
                    ,UN_PERIODO    => MI_PERIODO
                    ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                    ,UN_IDCONCEPTO => UN_CONCEPTO1);
        END IF;
        IF UN_CONCEPTO2 IS NOT NULL THEN
          PCK_NOMINA.PR_BORRARNOVEDAD(
                     UN_COMPANIA    => UN_COMPANIA
                     ,UN_PROCESO    => UN_PROCESO
                     ,UN_ANIO       => MI_ANIO
                     ,UN_MES        => MI_MES
                     ,UN_PERIODO    => MI_PERIODO
                     ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                     ,UN_IDCONCEPTO => UN_CONCEPTO2);
        END IF;
      END IF;
    END LOOP;

    IF (MI_OPCION ='BORRAR' AND UN_CONCEPTO = 359) THEN

    	PCK_NOMINA_COM2.PR_BORARNOVEDAD359(
    					UN_COMPANIA   => UN_COMPANIA
		                  ,UN_PROCESO    => UN_PROCESO
		                  ,UN_ANIO       => RS.ANO
		                  ,UN_MES        => RS.MES
		                  ,UN_PERIODO    => MI_PERIODO
		                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
		                  ,UN_IDCONCEPTO => UN_CONCEPTO
		                  ,UN_FECHAFIN   => UN_FECHAFIN
		                  ,UN_USUARIO    => UN_USUARIO);

    END IF;

  END LOOP;  
  RETURN -1;
END FC_DIFERIR;

FUNCTION FC_DIFERIRENC
/*
    NAME              : FC_DIFERIRENC
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDWIN F. CABRERA M.
    DATE MIGRADOR     : 29/11/2023
    TIME              : 02:43
    SOURCE MODULE     :
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :
    DESCRIPTION       : DIFIERE LOS VALORES DE UNA NOVEDAD DE ENCARGOS ENTRE LOS PERIODOS ACUMULABLES Y DIFERIDOS
                        PRORRATEANDO LOS VALORES SI EXISTE MAS DE UN ENCARGO EN EL MES
    TICKET			  : 7731971
    */
(
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROCESO     IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_ANIO        IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES         IN PCK_SUBTIPOS.TI_MES,
  UN_PERIODO     IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
  UN_ID_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
  UN_CONCEPTO    IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
  UN_DIFERIDO    IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
  UN_OPCION      IN VARCHAR2 DEFAULT NULL,
  UN_CONCEPTO1   IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO DEFAULT NULL,
  UN_VALOR1      IN PCK_SUBTIPOS.TI_DOBLE DEFAULT NULL,
  UN_CONCEPTO2   IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO DEFAULT NULL,
  UN_VALOR2      IN PCK_SUBTIPOS.TI_DOBLE DEFAULT NULL,
  UN_FECHAINICIO IN DATE,
  UN_INCAPACIDAD IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 0,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO,
  UN_FECHAFIN IN DATE DEFAULT NULL
)
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_IDIASDISFRUTE    PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  MI_OPCION           VARCHAR(100);
  MI_ANIO             PCK_SUBTIPOS.TI_ANIO;
  MI_MES              PCK_SUBTIPOS.TI_MES;
  MI_PERIODO          PCK_SUBTIPOS.TI_PERIODO_NOMI;
  MI_DIAFINAL         PCK_SUBTIPOS.TI_ENTERO;
  MI_DIAINICIO        PCK_SUBTIPOS.TI_ENTERO;
  MI_ANIOINICIO       PCK_SUBTIPOS.TI_ANIO;
  MI_MESINICIO        PCK_SUBTIPOS.TI_MES;
  MI_NIT              PCK_SUBTIPOS.TI_TERCERO;
  MI_DIFERIDO         PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_SIGUIENTE        PCK_SUBTIPOS.TI_TERCERO;
  MI_DIASPER          PCK_SUBTIPOS.TI_ENTERO;
  MI_VALOR            PCK_SUBTIPOS.TI_DOBLE;
  MI_ACCION           PCK_SUBTIPOS.TI_LOGICO;
  MI_AC               PCK_SUBTIPOS.TI_DOBLE;
  MI_DIAS31           PCK_SUBTIPOS.TI_ENTERO;
  MI_VALAUX           NUMBER;
  MI_CN11             NUMBER;
  MI_CN10             NUMBER;
BEGIN
  MI_DIFERIDO :=UN_DIFERIDO;
  MI_OPCION   :=UPPER(UN_OPCION);
  IF  UN_OPCION IS NULL THEN
    MI_OPCION:='ADICIONAR';
  END IF;

  FOR RS IN (
    SELECT   ANO
           , MES
           , PERIODO
           , FECHAFINAL
           , FECHAINICIO
    FROM PERIODOS
    WHERE COMPANIA        = UN_COMPANIA
      AND ID_DE_PROCESO   = UN_PROCESO
      AND ACUMULADO       NOT IN (0)
      AND DIFERIDOS       NOT IN (0)
      AND UN_FECHAINICIO  BETWEEN FECHAINICIO AND FECHAFINAL
    )
  LOOP
    MI_ANIO     := RS.ANO;
    MI_MES      := RS.MES;
    MI_PERIODO  := RS.PERIODO;
    MI_DIAFINAL := TO_NUMBER(TO_CHAR(RS.FECHAFINAL ,'DD'));
    MI_DIAINICIO:= TO_NUMBER(TO_CHAR(UN_FECHAINICIO,'DD'));
    MI_DIAS31 := 0;
    IF MI_DIAFINAL>16 AND MI_DIAFINAL<>31 THEN
      MI_IDIASDISFRUTE:=30-MI_DIAINICIO+1;
    ELSIF MI_DIAFINAL=31 THEN
      --MI_IDIASDISFRUTE:=31-MI_DIAINICIO+1;
      --MZANGUNA:02/08/2018, Se revisa contra access y no se resta
      MI_IDIASDISFRUTE:=31-MI_DIAINICIO;
      --(APINEDA:29/03/2019)-Se agrega validaci¿¿n porque para las entidades que no pagan el d¿¿a 31 en vacaciones, se esta restando el 31 dos veces. TAR1000091158 Bucaramanga
      IF PCK_PARST.FC_PAR('PAGAR EL DIA 31 EN VACACIONES', ' ') = 'SI' THEN
        MI_DIAS31 := 1;
      END IF;
      MI_NIT :=PCK_NOMINA_COM1.FC_PARAMETRO(
                               UN_COMPANIA => UN_COMPANIA
                              ,UN_OPCION   => 31);
      IF UN_INCAPACIDAD <>0 THEN
        IF MI_NIT IN('830.065.741-1','890704536-7','899999433-5') THEN
          MI_IDIASDISFRUTE:=MI_IDIASDISFRUTE-1;
        END IF;
      END IF;
    ELSE
      --(APINEDA:27/08/2019)-Se modifica sumando 1 de acuerdo a como se realiza proceso en access.
      MI_IDIASDISFRUTE:= TRUNC(RS.FECHAFINAL)-TRUNC(UN_FECHAINICIO) + 1;
    END IF;
    IF MI_IDIASDISFRUTE>= MI_DIFERIDO THEN
      MI_IDIASDISFRUTE := MI_DIFERIDO;
    END IF;
        --Se agrega esta funcion segun lo establecido en acces, MIGUEL VENEGAS
   MI_AC := PCK_NOMINA_COM1.FC_ACUMNOVEDADCONCEPTO(UN_COMPANIA,UN_ANIO,UN_MES,UN_PERIODO,UN_ANIO,UN_MES,UN_PERIODO,UN_ID_EMPLEADO);
    IF MI_OPCION='ADICIONAR' THEN
        -- BUSCAR REGISTRO DE ENCARGO YA INCLUIDO EN EL PERIODO
        SELECT  SUM(DECODE(ID_DE_CONCEPTO,UN_CONCEPTO,VALOR,0)), SUM(DECODE(ID_DE_CONCEPTO,UN_CONCEPTO1,VALOR,0))
        INTO    MI_CN11, MI_CN10
        FROM    NOVEDADES
        WHERE   COMPANIA = UN_COMPANIA
                AND ID_DE_PROCESO = UN_PROCESO
                AND ANO = MI_ANIO
                AND MES = MI_MES
                AND PERIODO = MI_PERIODO
                AND ID_DE_EMPLEADO = UN_ID_EMPLEADO
                AND ID_DE_CONCEPTO IN (UN_CONCEPTO, UN_CONCEPTO1 );
        
        IF ( NVL( MI_CN10,0) = 0 ) THEN  
            MI_VALAUX := UN_VALOR1;
        ELSE
            MI_VALAUX := CEIL(((MI_CN10*MI_CN11)+(UN_VALOR1*MI_IDIASDISFRUTE))/(MI_CN11+MI_IDIASDISFRUTE));
        END IF;
      
        PCK_NOMINA.PR_INCLUIRNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO => UN_CONCEPTO
                  ,UN_VALOR      => MI_IDIASDISFRUTE
                  --,UN_ACCION     => CASE WHEN UN_CONCEPTO = '35' AND PCK_NOMINA.FC_CNA(35) > 0 THEN 1 ELSE NULL END); --(ORIGINAL MIGUEL VENEGAS)
                  --(APINEDA:11/10/2018)-Se agrega validaci¿¿n para cuando existe m¿¿s de una incapacidad, se deben actualizar los d¿¿as en la novedad.
                  ,UN_ACCION     => CASE WHEN  (UN_INCAPACIDAD <> 0) OR (UN_CONCEPTO = '35' AND PCK_NOMINA.FC_CNA(35) > 0) THEN 1 ELSE NULL END);

      IF UN_CONCEPTO1 IS NOT NULL AND UN_VALOR1 IS NOT NULL THEN
        PCK_NOMINA.PR_INCLUIRNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO   => UN_CONCEPTO1
                  ,UN_VALOR      => MI_VALAUX --UN_VALOR1
                  );
      END IF;
      IF UN_CONCEPTO2 IS NOT NULL AND UN_VALOR2 IS NOT NULL THEN
        PCK_NOMINA.PR_INCLUIRNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO   => UN_CONCEPTO2
                  ,UN_VALOR      => UN_VALOR2);
      END IF;
    ELSE
        SELECT  SUM(DECODE(ID_DE_CONCEPTO,UN_CONCEPTO,VALOR,0)), SUM(DECODE(ID_DE_CONCEPTO,UN_CONCEPTO1,VALOR,0))
        INTO    MI_CN11, MI_CN10
        FROM    NOVEDADES
        WHERE   COMPANIA = UN_COMPANIA
                AND ID_DE_PROCESO = UN_PROCESO
                AND ANO = MI_ANIO
                AND MES = MI_MES
                AND PERIODO = MI_PERIODO
                AND ID_DE_EMPLEADO = UN_ID_EMPLEADO
                AND ID_DE_CONCEPTO IN (UN_CONCEPTO, UN_CONCEPTO1 );
        
        IF ( MI_CN11 = MI_IDIASDISFRUTE AND ABS(MI_CN10 -UN_VALOR1) < 10 ) THEN
             
              PCK_NOMINA.PR_BORRARNOVEDADX(
                         UN_COMPANIA   => UN_COMPANIA
                        ,UN_PROCESO    => UN_PROCESO
                        ,UN_ANIO       => MI_ANIO
                        ,UN_MES        => MI_MES
                        ,UN_PERIODO    => MI_PERIODO
                        ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                        ,UN_IDCONCEPTO => UN_CONCEPTO
                        ,UN_CANTIDAD   => MI_IDIASDISFRUTE
                        ,UN_USUARIO    => UN_USUARIO );
              --(APINEDA:10/10/2018)-Se elimina el registro de concepto 94 solo si el registro eliminado corresponde a PERIODOS DE VACACIONES. Se estaba presentando un problema y estaba eliminando el concepto 94 al eliminar incapacidades, encargos o licencias.
              IF (UN_CONCEPTO = 164) THEN
                  PCK_NOMINA.PR_BORRARNOVEDAD(
                             UN_COMPANIA   => UN_COMPANIA
                            ,UN_PROCESO    => UN_PROCESO
                            ,UN_ANIO       => MI_ANIO
                            ,UN_MES        => MI_MES
                            ,UN_PERIODO    => MI_PERIODO
                            ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                            ,UN_IDCONCEPTO => 94);
              END IF;
              IF UN_CONCEPTO1 IS NOT NULL THEN
                PCK_NOMINA.PR_BORRARNOVEDAD(
                           UN_COMPANIA   => UN_COMPANIA
                          ,UN_PROCESO    => UN_PROCESO
                          ,UN_ANIO       => MI_ANIO
                          ,UN_MES        => MI_MES
                          ,UN_PERIODO    => MI_PERIODO
                          ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                          ,UN_IDCONCEPTO => UN_CONCEPTO1);
              END IF;
              IF UN_CONCEPTO2 IS NOT NULL THEN
                PCK_NOMINA.PR_BORRARNOVEDAD(
                           UN_COMPANIA   => UN_COMPANIA
                          ,UN_PROCESO    => UN_PROCESO
                          ,UN_ANIO       => MI_ANIO
                          ,UN_MES        => MI_MES
                          ,UN_PERIODO    => MI_PERIODO
                          ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                          ,UN_IDCONCEPTO => UN_CONCEPTO2);
              END IF;
        ELSE -- ACTUALIZAR
            MI_VALAUX := ROUND(((MI_CN10*MI_CN11) - (UN_VALOR1*MI_IDIASDISFRUTE)) / (MI_CN11-MI_IDIASDISFRUTE)); 
            PCK_NOMINA.PR_INCLUIRNOVEDAD(
                                       UN_COMPANIA   => UN_COMPANIA
                                      ,UN_PROCESO    => UN_PROCESO
                                      ,UN_ANIO       => MI_ANIO
                                      ,UN_MES        => MI_MES
                                      ,UN_PERIODO    => MI_PERIODO
                                      ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                                      ,UN_IDCONCEPTO => UN_CONCEPTO
                                      ,UN_VALOR      => MI_IDIASDISFRUTE * -1
                                      --,UN_ACCION     => CASE WHEN UN_CONCEPTO = '35' AND PCK_NOMINA.FC_CNA(35) > 0 THEN 1 ELSE NULL END); --(ORIGINAL MIGUEL VENEGAS)
                                      --(APINEDA:11/10/2018)-Se agrega validaci¿¿n para cuando existe m¿¿s de una incapacidad, se deben actualizar los d¿¿as en la novedad.
                                      ,UN_ACCION     => CASE WHEN  (UN_INCAPACIDAD <> 0) OR (UN_CONCEPTO = '35' AND PCK_NOMINA.FC_CNA(35) > 0) THEN 1 ELSE NULL END);

            IF UN_CONCEPTO1 IS NOT NULL AND UN_VALOR1 IS NOT NULL THEN
                PCK_NOMINA.PR_INCLUIRNOVEDAD(
                           UN_COMPANIA   => UN_COMPANIA
                          ,UN_PROCESO    => UN_PROCESO
                          ,UN_ANIO       => MI_ANIO
                          ,UN_MES        => MI_MES
                          ,UN_PERIODO    => MI_PERIODO
                          ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                          ,UN_IDCONCEPTO   => UN_CONCEPTO1
                          ,UN_VALOR      => MI_VALAUX --UN_VALOR1
                          );
            END IF;
            
            IF UN_CONCEPTO2 IS NOT NULL AND UN_VALOR2 IS NOT NULL THEN
                PCK_NOMINA.PR_INCLUIRNOVEDAD(
                           UN_COMPANIA   => UN_COMPANIA
                          ,UN_PROCESO    => UN_PROCESO
                          ,UN_ANIO       => MI_ANIO
                          ,UN_MES        => MI_MES
                          ,UN_PERIODO    => MI_PERIODO
                          ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                          ,UN_IDCONCEPTO   => UN_CONCEPTO2
                          ,UN_VALOR      => UN_VALOR2);
            END IF;    
        END IF;
    END IF;

    MI_DIFERIDO :=MI_DIFERIDO - MI_IDIASDISFRUTE - MI_DIAS31;
    MI_DIAS31 :=0;
    --(APINEDA:28/11/2018)-TAR1000087627 ANE Se agrega validaci¿¿n de acuerdo a versi¿¿n NOMINAP2018.09.01 de access para no dejar variable diferido con valor -1
    IF MI_DIFERIDO = -1 THEN
      MI_DIFERIDO := 0;
    END IF;
    WHILE MI_DIFERIDO<>0
    LOOP
      MI_SIGUIENTE :=PCK_NOMINA_COM2.FC_SIGUIENTEPERIODO(
                                     UN_COMPANIA => UN_COMPANIA
                                    ,UN_ANO      => MI_ANIO
                                    ,UN_PERIODO => MI_PERIODO
                                    ,UN_MES => MI_MES
                                    ,UN_PROCESO => UN_PROCESO);
      IF MI_SIGUIENTE= ' '
         OR MI_SIGUIENTE IS NULL THEN
        RETURN 0;
      END IF;
      MI_ANIO     := TO_NUMBER(SUBSTR(MI_SIGUIENTE,1,4));
      MI_MES      := TO_NUMBER(SUBSTR(MI_SIGUIENTE,5,2));
      MI_PERIODO  := TO_NUMBER(SUBSTR(MI_SIGUIENTE,7,2));
      MI_DIASPER  := PCK_NOMINA.FC_DIASPERIODO(
                                UN_COMPANIA => UN_COMPANIA
                               ,UN_PROCESO  => UN_PROCESO
                               ,UN_ANIO     => MI_ANIO
                               ,UN_MES      => MI_MES
                               ,UN_PERIODO  => MI_PERIODO);
      IF UN_FECHAINICIO IS NOT NULL THEN
        MI_ANIOINICIO := TO_NUMBER(TO_CHAR(UN_FECHAINICIO,'YYYY'));
        MI_MESINICIO  := TO_NUMBER(TO_CHAR(UN_FECHAINICIO,'MM'));
        IF MI_ANIOINICIO=MI_ANIO AND MI_MESINICIO=MI_MES THEN
          IF MI_DIAINICIO<=15 AND MI_PERIODO=1 THEN
            MI_DIASPER:= 15 - MI_DIAINICIO + 1;
          ELSIF MI_DIAINICIO>15 AND MI_PERIODO>1 THEN
            MI_DIASPER:= 30 - MI_DIAINICIO + 1;
          END IF;
          IF MI_DIASPER<0 THEN
            MI_DIASPER:=0;
          END IF;
        END IF;
      END IF;
      IF MI_DIFERIDO>=MI_DIASPER THEN
        MI_VALOR    := MI_DIASPER;
        MI_DIFERIDO := MI_DIFERIDO- MI_DIASPER;
      ELSE
        MI_VALOR    := MI_DIFERIDO;
        MI_DIFERIDO :=0;
      END IF;
      IF MI_OPCION='ADICIONAR' THEN
        -- BUSCAR REGISTRO DE ENCARGO YA INCLUIDO EN EL PERIODO
        SELECT  SUM(DECODE(ID_DE_CONCEPTO,UN_CONCEPTO,VALOR,0)), SUM(DECODE(ID_DE_CONCEPTO,UN_CONCEPTO1,VALOR,0))
        INTO    MI_CN11, MI_CN10
        FROM    NOVEDADES
        WHERE   COMPANIA = UN_COMPANIA
                AND ID_DE_PROCESO = UN_PROCESO
                AND ANO = MI_ANIO
                AND MES = MI_MES
                AND PERIODO = MI_PERIODO
                AND ID_DE_EMPLEADO = UN_ID_EMPLEADO
                AND ID_DE_CONCEPTO IN (UN_CONCEPTO, UN_CONCEPTO1 );
        
        IF ( NVL( MI_CN10,0) = 0 ) THEN  
            MI_VALAUX := UN_VALOR1;
        ELSE
            MI_VALAUX := CEIL(((MI_CN10*MI_CN11)+(UN_VALOR1*MI_VALOR))/(MI_CN11+MI_VALOR));
        END IF;
        
        PCK_NOMINA.PR_INCLUIRNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO => UN_CONCEPTO
                  ,UN_VALOR      =>  MI_VALOR);
                  
        IF UN_CONCEPTO1 IS NOT NULL AND UN_VALOR1 IS NOT NULL THEN
          PCK_NOMINA.PR_INCLUIRNOVEDAD(
                     UN_COMPANIA   => UN_COMPANIA
                    ,UN_PROCESO    => UN_PROCESO
                    ,UN_ANIO       => MI_ANIO
                    ,UN_MES        => MI_MES
                    ,UN_PERIODO    => MI_PERIODO
                    ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                    ,UN_IDCONCEPTO => UN_CONCEPTO1
                    ,UN_VALOR      => MI_VALAUX --UN_VALOR1
                    );
        END IF;
        
        IF UN_CONCEPTO2 IS NOT NULL AND UN_VALOR2 IS NOT NULL THEN
          PCK_NOMINA.PR_INCLUIRNOVEDAD(
                     UN_COMPANIA   => UN_COMPANIA
                    ,UN_PROCESO    => UN_PROCESO
                    ,UN_ANIO       => MI_ANIO
                    ,UN_MES        => MI_MES
                    ,UN_PERIODO    => MI_PERIODO
                    ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                    ,UN_IDCONCEPTO => UN_CONCEPTO2
                    ,UN_VALOR      => UN_VALOR2);
        END IF;
      ELSE
        SELECT  SUM(DECODE(ID_DE_CONCEPTO,UN_CONCEPTO,VALOR,0)), SUM(DECODE(ID_DE_CONCEPTO,UN_CONCEPTO1,VALOR,0))
        INTO    MI_CN11, MI_CN10
        FROM    NOVEDADES
        WHERE   COMPANIA = UN_COMPANIA
                AND ID_DE_PROCESO = UN_PROCESO
                AND ANO = MI_ANIO
                AND MES = MI_MES
                AND PERIODO = MI_PERIODO
                AND ID_DE_EMPLEADO = UN_ID_EMPLEADO
                AND ID_DE_CONCEPTO IN (UN_CONCEPTO, UN_CONCEPTO1 );
        
        IF ( MI_CN11 = MI_VALOR AND ABS(MI_CN10 -UN_VALOR1) < 10 ) THEN
            PCK_NOMINA.PR_BORRARNOVEDADX(
                       UN_COMPANIA   => UN_COMPANIA
                      ,UN_PROCESO    => UN_PROCESO
                      ,UN_ANIO       => MI_ANIO
                      ,UN_MES        => MI_MES
                      ,UN_PERIODO    => MI_PERIODO
                      ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                      ,UN_IDCONCEPTO => UN_CONCEPTO
                      ,UN_CANTIDAD   => MI_VALOR
                      ,UN_USUARIO    => UN_USUARIO);
            IF UN_CONCEPTO1 IS NOT NULL THEN
              PCK_NOMINA.PR_BORRARNOVEDAD(
                         UN_COMPANIA   => UN_COMPANIA
                        ,UN_PROCESO    => UN_PROCESO
                        ,UN_ANIO       => MI_ANIO
                        ,UN_MES        => MI_MES
                        ,UN_PERIODO    => MI_PERIODO
                        ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                        ,UN_IDCONCEPTO => UN_CONCEPTO1);
            END IF;
            IF UN_CONCEPTO2 IS NOT NULL THEN
              PCK_NOMINA.PR_BORRARNOVEDAD(
                         UN_COMPANIA    => UN_COMPANIA
                         ,UN_PROCESO    => UN_PROCESO
                         ,UN_ANIO       => MI_ANIO
                         ,UN_MES        => MI_MES
                         ,UN_PERIODO    => MI_PERIODO
                         ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                         ,UN_IDCONCEPTO => UN_CONCEPTO2);
            END IF;
        ELSE
            MI_VALAUX := ROUND(((MI_CN10*MI_CN11) - (UN_VALOR1*MI_VALOR)) / (MI_CN11-MI_VALOR)); 
            PCK_NOMINA.PR_INCLUIRNOVEDAD(
                                       UN_COMPANIA   => UN_COMPANIA
                                      ,UN_PROCESO    => UN_PROCESO
                                      ,UN_ANIO       => MI_ANIO
                                      ,UN_MES        => MI_MES
                                      ,UN_PERIODO    => MI_PERIODO
                                      ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                                      ,UN_IDCONCEPTO => UN_CONCEPTO
                                      ,UN_VALOR      => MI_VALOR * -1
                                      --,UN_ACCION     => CASE WHEN UN_CONCEPTO = '35' AND PCK_NOMINA.FC_CNA(35) > 0 THEN 1 ELSE NULL END); --(ORIGINAL MIGUEL VENEGAS)
                                      --(APINEDA:11/10/2018)-Se agrega validaci¿¿n para cuando existe m¿¿s de una incapacidad, se deben actualizar los d¿¿as en la novedad.
                                      ,UN_ACCION     => '1'
                                      );

            IF UN_CONCEPTO1 IS NOT NULL AND UN_VALOR1 IS NOT NULL THEN
                PCK_NOMINA.PR_INCLUIRNOVEDAD(
                           UN_COMPANIA   => UN_COMPANIA
                          ,UN_PROCESO    => UN_PROCESO
                          ,UN_ANIO       => MI_ANIO
                          ,UN_MES        => MI_MES
                          ,UN_PERIODO    => MI_PERIODO
                          ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                          ,UN_IDCONCEPTO   => UN_CONCEPTO1
                          ,UN_VALOR      => MI_VALAUX --UN_VALOR1
                          );
            END IF;
            
            IF UN_CONCEPTO2 IS NOT NULL AND UN_VALOR2 IS NOT NULL THEN
                PCK_NOMINA.PR_INCLUIRNOVEDAD(
                           UN_COMPANIA   => UN_COMPANIA
                          ,UN_PROCESO    => UN_PROCESO
                          ,UN_ANIO       => MI_ANIO
                          ,UN_MES        => MI_MES
                          ,UN_PERIODO    => MI_PERIODO
                          ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                          ,UN_IDCONCEPTO   => UN_CONCEPTO2
                          ,UN_VALOR      => UN_VALOR2);
            END IF; 
        END IF;
      END IF;
    END LOOP;
  END LOOP;
  RETURN -1;
END FC_DIFERIRENC;

--16
FUNCTION FC_DIFERIRVAC 
/*
    NAME              : DIFERIR
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GÓMEZ BLANCO
    DATE MIGRADOR     : 14/10/2015
    TIME              : 08:43 AM
    SOURCE MODULE     : 
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 09/02/2017
    TIME              : 
    DESCRIPTION       : DIFIERE LOS VALORES DE UNA NOVEDAD DE VACACIONES ENTRE LOS PERIODOS ACUMULABLES Y DIFERIDOS
                        \ SE AJUSTA AL ESTANDAR.
    @NAME             : getDiferirVac
    @METHOD           : GET
    */
(
UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
UN_PROCESO        IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
UN_ID_EMPLEADO    IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
UN_CONCEPTO       IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
UN_DIAHABIL       IN PCK_SUBTIPOS.TI_ENTERO,
UN_DIADINERO      IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 0,
UN_DIAPENDIENTE   IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 0,
UN_DIAS           IN PCK_SUBTIPOS.TI_ENTERO,
UN_INICIODISFRUTE IN DATE,
UN_FECHAPAGO      IN DATE  ,
UN_NUMPERIODO     IN PCK_SUBTIPOS.TI_ENTERO DEFAULT NULL,
UN_INDBONIFICACION IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0,
UN_OPCION         IN VARCHAR2 DEFAULT NULL,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO  
) RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_DIASDISFRUTE     PCK_SUBTIPOS.TI_ENTERO;
  MI_DIASPAGARVAC     PCK_SUBTIPOS.TI_ENTERO;
  MI_VACACIONESDINERO PCK_SUBTIPOS.TI_DOBLE;
  MI_DIASHABILES      PCK_SUBTIPOS.TI_ENTERO;
  MI_NUMPERIODOS      PCK_SUBTIPOS.TI_ENTERO;
  MI_PARPERIODOS      PCK_SUBTIPOS.TI_ENTERO;
  MI_IDIFERIRMAS      PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  MI_RTA              PCK_SUBTIPOS.TI_ENTERO;
  MI_PARDIFCN94       PARAMETRO.VALOR%TYPE;
  MI_PARDIFCN99       PARAMETRO.VALOR%TYPE;	
  
  MI_DIASBONVAC       PARAMETRO.VALOR%TYPE;
  MI_CONDIASBONVAC    PARAMETRO.VALOR%TYPE;										
  --MI_ERROR_FUN  NUMBER:=GL_ERROR_NUM + 17; 
BEGIN
  MI_DIASDISFRUTE     :=  TO_NUMBER(PCK_NOMINA_COM1.FC_PARAMETRO(
                                                    UN_COMPANIA => UN_COMPANIA
                                                   ,UN_OPCION   => 4));
  MI_DIASPAGARVAC     :=  TO_NUMBER(PCK_NOMINA_COM1.FC_PARAMETRO(
                                                    UN_COMPANIA => UN_COMPANIA
                                                   ,UN_OPCION   => 5));
  MI_VACACIONESDINERO :=  TO_NUMBER(PCK_NOMINA_COM1.FC_PARAMETRO(
                                                    UN_COMPANIA => UN_COMPANIA
                                                   ,UN_OPCION   => 6));
  MI_DIASHABILES      :=  TO_NUMBER(PCK_NOMINA_COM1.FC_PARAMETRO(
                                                    UN_COMPANIA => UN_COMPANIA
                                                   ,UN_OPCION   => 13));
  MI_PARPERIODOS      :=  TO_NUMBER(PCK_NOMINA_COM1.FC_PARAMETRO(
                                                    UN_COMPANIA => UN_COMPANIA
                                                   ,UN_OPCION   => 19));
  MI_PARDIFCN94 := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA
                                ,UN_NOMBRE => 'DIFERIR DIAS DE VACACIONES PAGADOS CN94'
                                ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA
                                ,UN_FECHA_PAR => SYSDATE), 'NO');
  MI_PARDIFCN99 := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA
                                ,UN_NOMBRE => 'DIFERIR DIAS PENDIENTES DE VACACIONES CN99'
                                ,UN_MODULO => PCK_DATOS.FC_MODULONOMINA
                                ,UN_FECHA_PAR => SYSDATE), 'NO'); 
                                
    -- TICKET 7743077 EFCM: BONIFICACION A VACACIONES
    IF ( UN_INDBONIFICACION != 0 ) THEN
        MI_DIASBONVAC       := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'DIAS BONIFICACION VACACIONES',PCK_DATOS.FC_MODULONOMINA,SYSDATE),'0');
        MI_CONDIASBONVAC    := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'CODIGO CONCEPTO DIAS BONIFICACION VACACIONES',PCK_DATOS.FC_MODULONOMINA,SYSDATE),'0');
    ELSE
        MI_DIASBONVAC       := 0;
        MI_CONDIASBONVAC    := 'NO';
    END IF;
    -- TICKET 7743077 FIN --                               																						 

  MI_NUMPERIODOS      :=  UN_NUMPERIODO;
  IF UN_NUMPERIODO IS NULL  THEN
    MI_NUMPERIODOS:=1;
  END IF;  
  FOR RS IN (
    SELECT  ANO
           ,MES
           ,PERIODO
    FROM PERIODOS
    WHERE COMPANIA        = UN_COMPANIA
      AND ID_DE_PROCESO   = UN_PROCESO
      AND ACUMULADO       NOT IN (0)
      AND DIFERIDOS       NOT IN (0)
      AND UN_FECHAPAGO    BETWEEN FECHAINICIO AND FECHAFINAL
    )
  LOOP
    IF UN_DIADINERO>0 THEN
      PCK_NOMINA.PR_INCLUIRNOVEDAD(
                 UN_COMPANIA   => UN_COMPANIA
                ,UN_PROCESO    => UN_PROCESO
                ,UN_ANIO       => RS.ANO
                ,UN_MES        => RS.MES
                ,UN_PERIODO    => RS.PERIODO
                ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                ,UN_IDCONCEPTO => MI_VACACIONESDINERO
                ,UN_VALOR      => PCK_SYSMAN_UTL.FC_IIF(
                                                 UN_CONDICION => UN_OPCION IS NULL
                                                ,UN_SI        => 1
                                                ,UN_NO        => 0));
    END IF;
    IF UN_DIAPENDIENTE>0 THEN
	  IF MI_PARDIFCN99 = 'SI' THEN 
            MI_RTA:=PCK_NOMINA_COM2.FC_DIFERIR(
                              UN_COMPANIA    => UN_COMPANIA
                             ,UN_PROCESO     => UN_PROCESO
                             ,UN_ANIO        => RS.ANO
                             ,UN_MES         => RS.MES
                             ,UN_PERIODO     => RS.PERIODO
                             ,UN_ID_EMPLEADO => UN_ID_EMPLEADO
                             ,UN_CONCEPTO    => 99
                             ,UN_DIFERIDO    => UN_DIAPENDIENTE
                             ,UN_OPCION      => UN_OPCION
                             ,UN_CONCEPTO1   => NULL
                             ,UN_VALOR1      => NULL
                             ,UN_CONCEPTO2   => NULL
                             ,UN_VALOR2      => NULL
                             ,UN_FECHAINICIO => UN_INICIODISFRUTE
                             ,UN_INCAPACIDAD => 0
                             ,UN_USUARIO     => UN_USUARIO);
      ELSE  
		    PCK_NOMINA.PR_INCLUIRNOVEDAD(
							 UN_COMPANIA   => UN_COMPANIA
							,UN_PROCESO    => UN_PROCESO
							,UN_ANIO       => RS.ANO
							,UN_MES        => RS.MES
							,UN_PERIODO    => RS.PERIODO
							,UN_IDEMPLEADO => UN_ID_EMPLEADO
							,UN_IDCONCEPTO => 99
							,UN_VALOR      => UN_DIAPENDIENTE);
	  END IF;
    ELSE
      PCK_NOMINA.PR_INCLUIRNOVEDAD(
                 UN_COMPANIA   => UN_COMPANIA
                ,UN_PROCESO    => UN_PROCESO
                ,UN_ANIO       => RS.ANO
                ,UN_MES        => RS.MES
                ,UN_PERIODO    => RS.PERIODO
                ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                ,UN_IDCONCEPTO => UN_CONCEPTO
                ,UN_VALOR      => PCK_SYSMAN_UTL.FC_IIF(
                                                 UN_CONDICION => UN_OPCION IS NULL OR UN_DIAHABIL>0
                                                ,UN_SI        => 1
                                                ,UN_NO        => 0));
    END IF;

    IF UN_DIAPENDIENTE=0 THEN
      PCK_NOMINA.PR_INCLUIRNOVEDAD(
                 UN_COMPANIA   => UN_COMPANIA
                ,UN_PROCESO    => UN_PROCESO
                ,UN_ANIO       => RS.ANO
                ,UN_MES        => RS.MES
                ,UN_PERIODO    => RS.PERIODO
                ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                ,UN_IDCONCEPTO => MI_DIASPAGARVAC
                ,UN_VALOR      => PCK_SYSMAN_UTL.FC_IIF(
                                                 UN_CONDICION => UN_OPCION IS NULL AND MI_DIASHABILES>0
                                                ,UN_SI        => UN_DIAS
                                                ,UN_NO        => 0));
      PCK_NOMINA.PR_INCLUIRNOVEDAD(
                 UN_COMPANIA   => UN_COMPANIA
                ,UN_PROCESO    => UN_PROCESO
                ,UN_ANIO       => RS.ANO
                ,UN_MES        => RS.MES
                ,UN_PERIODO    => RS.PERIODO
                ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                ,UN_IDCONCEPTO => MI_PARPERIODOS
                ,UN_VALOR      => PCK_SYSMAN_UTL.FC_IIF(
                                                 UN_CONDICION => UN_OPCION IS NULL
                                                ,UN_SI        => MI_NUMPERIODOS
                                                ,UN_NO        => 0));

      PCK_NOMINA.PR_INCLUIRNOVEDAD(
                 UN_COMPANIA   => UN_COMPANIA
                ,UN_PROCESO    => UN_PROCESO
                ,UN_ANIO       => RS.ANO
                ,UN_MES        => RS.MES
                ,UN_PERIODO    => RS.PERIODO
                ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                ,UN_IDCONCEPTO => MI_DIASHABILES
                ,UN_VALOR      => PCK_SYSMAN_UTL.FC_IIF(
                                                 UN_CONDICION =>UN_OPCION IS NULL
                                                ,UN_SI        => UN_DIAHABIL
                                                ,UN_NO        => 0));
                                                
        -- TICKET 7743077 EFCM: BONIFICACION A VACACIONES
	    IF ( TO_NUMBER(MI_DIASBONVAC) > 0 AND MI_CONDIASBONVAC != 'NO' ) THEN
	        PCK_NOMINA.PR_INCLUIRNOVEDAD(
	                                    UN_COMPANIA   => UN_COMPANIA
	                                    ,UN_PROCESO    => UN_PROCESO
	                                    ,UN_ANIO       => RS.ANO
	                                    ,UN_MES        => RS.MES
	                                    ,UN_PERIODO    => RS.PERIODO
	                                    ,UN_IDEMPLEADO => UN_ID_EMPLEADO
	                                    ,UN_IDCONCEPTO => TO_NUMBER(MI_CONDIASBONVAC)
	                                    ,UN_VALOR      => PCK_SYSMAN_UTL.FC_IIF(
	                                                                            UN_CONDICION =>UN_OPCION IS NULL
	                                                                            ,UN_SI        => TO_NUMBER(MI_DIASBONVAC)
	                                                                            ,UN_NO        => 0
	                                                                            )
	                                    );    
	    END IF;
	    -- TICKET 7743077 FIN --
	    
      IF UN_DIADINERO>0 THEN
        PCK_NOMINA.PR_INCLUIRNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => RS.ANO
                  ,UN_MES        => RS.MES
                  ,UN_PERIODO    => RS.PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO => 96
                  ,UN_VALOR      => PCK_SYSMAN_UTL.FC_IIF(
                                                   UN_CONDICION => UN_OPCION IS NULL
                                                  ,UN_SI        => UN_DIADINERO
                                                  ,UN_NO        => 0));
      END IF;
    END IF;

	IF UN_DIAHABIL>0 THEN
        MI_IDIFERIRMAS:=UN_DIAS;
    END IF;
	  
    IF  UN_OPCION IS NOT NULL THEN
      PCK_NOMINA.PR_BORRARNOVEDAD(
                  UN_COMPANIA   => UN_COMPANIA
                , UN_PROCESO    => UN_PROCESO
                , UN_ANIO       => RS.ANO
                , UN_MES        => RS.MES
                , UN_PERIODO    => RS.PERIODO
                , UN_IDEMPLEADO => UN_ID_EMPLEADO
                , UN_IDCONCEPTO => UN_CONCEPTO);
      PCK_NOMINA.PR_BORRARNOVEDAD(UN_COMPANIA
                , UN_PROCESO    => UN_PROCESO
                , UN_ANIO       => RS.ANO
                , UN_MES        => RS.MES
                , UN_PERIODO    => RS.PERIODO
                , UN_IDEMPLEADO => UN_ID_EMPLEADO
                , UN_IDCONCEPTO => MI_VACACIONESDINERO);
      PCK_NOMINA.PR_BORRARNOVEDAD(
                  UN_COMPANIA   => UN_COMPANIA
                , UN_PROCESO    => UN_PROCESO
                , UN_ANIO       => RS.ANO
                , UN_MES        => RS.MES
                , UN_PERIODO    => RS.PERIODO
                , UN_IDEMPLEADO => UN_ID_EMPLEADO
                , UN_IDCONCEPTO => MI_PARPERIODOS);
      PCK_NOMINA.PR_BORRARNOVEDAD(
                  UN_COMPANIA   => UN_COMPANIA
                , UN_PROCESO    => UN_PROCESO
                , UN_ANIO       => RS.ANO
                , UN_MES        => RS.MES
                , UN_PERIODO    => RS.PERIODO
                , UN_IDEMPLEADO => UN_ID_EMPLEADO
                , UN_IDCONCEPTO => MI_DIASPAGARVAC);
      PCK_NOMINA.PR_BORRARNOVEDAD(
                  UN_COMPANIA   => UN_COMPANIA
                , UN_PROCESO    => UN_PROCESO
                , UN_ANIO       => RS.ANO
                , UN_MES        => RS.MES
                , UN_PERIODO    => RS.PERIODO
                , UN_IDEMPLEADO => UN_ID_EMPLEADO
                , UN_IDCONCEPTO => MI_DIASHABILES);
      PCK_NOMINA.PR_BORRARNOVEDAD(
                  UN_COMPANIA   => UN_COMPANIA
                , UN_PROCESO    => UN_PROCESO
                , UN_ANIO       => RS.ANO
                , UN_MES        => RS.MES
                , UN_PERIODO    => RS.PERIODO
                , UN_IDEMPLEADO => UN_ID_EMPLEADO
                , UN_IDCONCEPTO => 96);
                
        -- TICKET 7743077 EFCM: BONIFICACION A VACACIONES
        IF ( TO_NUMBER(MI_DIASBONVAC) > 0 AND MI_CONDIASBONVAC != 'NO' ) THEN
            PCK_NOMINA.PR_BORRARNOVEDAD(
                                          UN_COMPANIA   => UN_COMPANIA
                                        , UN_PROCESO    => UN_PROCESO
                                        , UN_ANIO       => RS.ANO
                                        , UN_MES        => RS.MES
                                        , UN_PERIODO    => RS.PERIODO
                                        , UN_IDEMPLEADO => UN_ID_EMPLEADO
                                        , UN_IDCONCEPTO => TO_NUMBER(MI_CONDIASBONVAC)
                                        );    
        END IF;
        -- TICKET 7743077 FIN --

	  IF UN_DIAPENDIENTE>0 AND MI_PARDIFCN99 = 'NO' THEN                																		
		  PCK_NOMINA.PR_BORRARNOVEDAD(
					  UN_COMPANIA   => UN_COMPANIA
					, UN_PROCESO    => UN_PROCESO
					, UN_ANIO       => RS.ANO
					, UN_MES        => RS.MES
					, UN_PERIODO    => RS.PERIODO
					, UN_IDEMPLEADO => UN_ID_EMPLEADO
					, UN_IDCONCEPTO => 99);
	  END IF;		 
    END IF;

    IF MI_IDIFERIRMAS>0 THEN
      MI_RTA:=PCK_NOMINA_COM2.FC_DIFERIR(
                              UN_COMPANIA    => UN_COMPANIA
                             ,UN_PROCESO     => UN_PROCESO
                             ,UN_ANIO        => RS.ANO
                             ,UN_MES         => RS.MES
                             ,UN_PERIODO     => RS.PERIODO
                             ,UN_ID_EMPLEADO => UN_ID_EMPLEADO
                             ,UN_CONCEPTO    => MI_DIASDISFRUTE
                             ,UN_DIFERIDO    => MI_IDIFERIRMAS
                             ,UN_OPCION      => UN_OPCION
                             ,UN_CONCEPTO1   => NULL
                             ,UN_VALOR1      => NULL
                             ,UN_CONCEPTO2   => NULL
                             ,UN_VALOR2      => NULL
                             ,UN_FECHAINICIO => UN_INICIODISFRUTE
                             ,UN_INCAPACIDAD => 0
                             ,UN_USUARIO     => UN_USUARIO);
	  IF UN_DIAPENDIENTE=0 THEN													 
		IF MI_PARDIFCN94 = 'SI' THEN                     
            MI_RTA:=PCK_NOMINA_COM2.FC_DIFERIR(
                                  UN_COMPANIA    => UN_COMPANIA
                                 ,UN_PROCESO     => UN_PROCESO
                                 ,UN_ANIO        => RS.ANO
                                 ,UN_MES         => RS.MES
                                 ,UN_PERIODO     => RS.PERIODO
                                 ,UN_ID_EMPLEADO => UN_ID_EMPLEADO
                                 ,UN_CONCEPTO    => MI_DIASPAGARVAC
                                 ,UN_DIFERIDO    => MI_IDIFERIRMAS
                                 ,UN_OPCION      => UN_OPCION
                                 ,UN_CONCEPTO1   => NULL
                                 ,UN_VALOR1      => NULL
                                 ,UN_CONCEPTO2   => NULL
                                 ,UN_VALOR2      => NULL
                                 ,UN_FECHAINICIO => UN_INICIODISFRUTE
                                 ,UN_INCAPACIDAD => 0
                                 ,UN_USUARIO     => UN_USUARIO);
            PCK_NOMINA.PR_BORRARNOVEDAD(
								  UN_COMPANIA   => UN_COMPANIA
								, UN_PROCESO    => UN_PROCESO
								, UN_ANIO       => RS.ANO
								, UN_MES        => RS.MES
								, UN_PERIODO    => RS.PERIODO
								, UN_IDEMPLEADO => UN_ID_EMPLEADO
								, UN_IDCONCEPTO => MI_DIASPAGARVAC);
        END IF;
	  END IF;				
      RETURN MI_RTA;
    END IF;
    RETURN -1;
    EXIT;
  END LOOP; 

    RETURN 0;--(CC:3057_En caso de no ingresar en loop, retorne 0)
END FC_DIFERIRVAC;

--18
FUNCTION FC_ACTUALIZAR_VACA_PERIODO
  /*
    NAME              : FC_AUSENTISMO == > ACCES AUSENTISMO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 21/10/2015
    TIME              : 16:48
    SOURCE MODULE     :
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS, JM CC 3115
    DATE MODIFIED     : \ 09/02/2017, 12/01/2025
    TIME              :
    DESCRIPTION       : ACTUALIZA LAS FECHAS DE LOS PERIODOS DE VACACIONES DE ACUERDO A LA FECHA DE PAGO
                        \ SE AJUSTA AL ESTANDAR.
    @NAME             : getActualizarVacaPeriodo
    @METHOD           : GET
    */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ID_DE_PROCESO  IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES            IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO        IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ID_DE_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
  )
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
  MI_CAMPOS1    PCK_SUBTIPOS.TI_CAMPOS;
  MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
  MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
  MI_CONDICION1 PCK_SUBTIPOS.TI_CONDICION;
  MI_CONDICION2 PCK_SUBTIPOS.TI_CONDICION;
BEGIN
  MI_TABLA    :='VACACIONES';
  MI_CAMPOS   :=' MES='      || UN_MES     || ',' ||
                ' PERIODO='  || UN_PERIODO;
                
    IF UN_ID_DE_EMPLEADO <> 0 THEN --JM CC 3115
        MI_CONDICION2 := ' AND ID_DE_EMPLEADO = '|| UN_ID_DE_EMPLEADO;
    ELSE 
        MI_CONDICION2 := ' AND ID_DE_EMPLEADO <> 0';
    END IF;
    --ID_DE_EMPLEADO ='   || UN_ID_DE_EMPLEADO || ' AND
  MI_CONDICION:='COMPANIA       =''' || UN_COMPANIA || ''' AND
                 ID_DE_PROCESO  ='   || UN_ID_DE_PROCESO  || ' AND
                 TO_CHAR(FECHAPAGO, ''DD/MM/YYY'')       =  TO_CHAR('   || PCK_SYSMAN_UTL.FC_SDATE(
                                                       UN_FECHA => PCK_NOMINA.FC_FECHAINIFINPERIODO(
                                                                              UN_COMPANIA  => UN_COMPANIA
                                                                             ,UN_PROCESO   => UN_ID_DE_PROCESO
                                                                             ,UN_ANIO      => UN_ANIO
                                                                             ,UN_MES       => UN_MES
                                                                             ,UN_PERIODO   => UN_PERIODO
                                                                             ,UN_FECHAINICIO => 2)) || ', ''DD/MM/YYY'')' || MI_CONDICION2; --JM CC 3115
  MI_CAMPOS1   :='VACACIONES.ANO = TO_NUMBER(TO_CHAR(VACACIONES.FECHAPAGO,''YYYY''))';
  
  MI_CONDICION1:=' COMPANIA       =''' || UN_COMPANIA || ''' AND 
  ID_DE_PROCESO  ='   || UN_ID_DE_PROCESO  || ' AND
  ( VACACIONES.ANO - TO_NUMBER(TO_CHAR(VACACIONES.FECHAPAGO,''YYYY''))) <>0 AND
   TO_CHAR(FECHAPAGO, ''DD/MM/YYY'')       =  TO_CHAR('   || PCK_SYSMAN_UTL.FC_SDATE(
                                                       UN_FECHA => PCK_NOMINA.FC_FECHAINIFINPERIODO(
                                                                              UN_COMPANIA  => UN_COMPANIA
                                                                             ,UN_PROCESO   => UN_ID_DE_PROCESO
                                                                             ,UN_ANIO      => UN_ANIO
                                                                             ,UN_MES       => UN_MES
                                                                             ,UN_PERIODO   => UN_PERIODO
                                                                             ,UN_FECHAINICIO => 2)) || ', ''DD/MM/YYY'')' || MI_CONDICION2; --JM CC 3115
  BEGIN
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                  UN_TABLA     => MI_TABLA
                                 ,UN_ACCION    => 'M'
                                 ,UN_CAMPOS    => MI_CAMPOS
                                 ,UN_CONDICION => MI_CONDICION );
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                  UN_TABLA     => MI_TABLA
                                 ,UN_ACCION    => 'M'
                                 ,UN_CAMPOS    => MI_CAMPOS1
                                 ,UN_CONDICION => MI_CONDICION1 );
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
    RAISE PCK_EXCEPCIONES.EXC_NOMINA;
  END;
  RETURN -1;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
    PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD    => SQLCODE
                ,UN_ERROR_COD  => PCK_ERRORES.ERRR_NOMINA_ACT_VACA_PERIODO
                ,UN_TABLAERROR => MI_TABLA
              );
END FC_ACTUALIZAR_VACA_PERIODO;

--19
FUNCTION FC_DIFERIRMAS
/*
    NAME              : FC_DIFERIRMAS 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 
    TIME              : 
    SOURCE MODULE     : 
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 09/02/2017
    TIME              : 
    DESCRIPTION       : \ SE AJUSTA AL ESTANDAR.
    @NAME             : getDiferirMas
    @METHOD           : GET                    
    */
(
UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
UN_PROCESO     IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
UN_ANIO        IN PCK_SUBTIPOS.TI_ANIO,
UN_MES         IN PCK_SUBTIPOS.TI_MES,
UN_PERIODO     IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
UN_ID_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
UN_CONCEPTO    IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
UN_DIFERIDO    IN PCK_SUBTIPOS.TI_ENTERO,
UN_OPCION      IN VARCHAR2                       DEFAULT NULL,
UN_CONCEPTO1   IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO DEFAULT NULL,
UN_VALOR1      IN PCK_SUBTIPOS.TI_DOBLE          DEFAULT NULL,
UN_CONCEPTO2   IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO DEFAULT NULL,
UN_VALOR2      IN PCK_SUBTIPOS.TI_DOBLE          DEFAULT NULL,
UN_FECHAINICIO IN DATE,
UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO,
UN_FECHAFIN IN DATE DEFAULT NULL
)
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  --MI_ERROR_FUN  NUMBER:=GL_ERROR_NUM + 20; 
  MI_RTA       PCK_SUBTIPOS.TI_ENTERO;
BEGIN
  IF UN_DIFERIDO>0 THEN
      MI_RTA:=PCK_NOMINA_COM2.FC_DIFERIR(
                              UN_COMPANIA    => UN_COMPANIA
                             ,UN_PROCESO     => UN_PROCESO
                             ,UN_ANIO        => UN_ANIO
                             ,UN_MES         => UN_MES
                             ,UN_PERIODO     => UN_PERIODO
                             ,UN_ID_EMPLEADO => UN_ID_EMPLEADO
                             ,UN_CONCEPTO    => UN_CONCEPTO
                             ,UN_DIFERIDO    => UN_DIFERIDO
                             ,UN_OPCION      => UN_OPCION
                             ,UN_CONCEPTO1   => UN_CONCEPTO1
                             ,UN_VALOR1      => UN_VALOR1
                             ,UN_CONCEPTO2   => UN_CONCEPTO2
                             ,UN_VALOR2      => UN_VALOR2
                             ,UN_FECHAINICIO => UN_FECHAINICIO
                             ,UN_INCAPACIDAD => 1
                             ,UN_USUARIO     => UN_USUARIO
                             ,UN_FECHAFIN     => UN_FECHAFIN);
      RETURN MI_RTA;
    END IF;
  RETURN -1;
END FC_DIFERIRMAS;

--20
PROCEDURE PR_CREARCENTROSDECOSTO
/*
    NAME              : 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JHONATAN ACELAS AREVALO
    DATE MIGRADOR     : 24/07/2015
    TIME              : Inicio 9:38AM Fin 10:30 AM
    SOURCE MODULE     : 
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     :  \ 09/02/2017
    TIME              : 
    DESCRIPTION       : \ SE AJUSTA AL ESTANDAR.
    @NAME             : crearCentrosDeCosto
    @METHOD           : POST 
  */
  (
    UN_ANO_CREAR   IN PCK_SUBTIPOS.TI_ANIO,
    UN_ANO_SESSION IN PCK_SUBTIPOS.TI_ANIO,
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO 
  )
  AS
    --MI_ERROR_FUN     NUMBER:=GL_ERROR_NUM + 13;
    MI_CONDICION     PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES       PCK_SUBTIPOS.TI_VALORES;
    MI_TABLA         PCK_SUBTIPOS.TI_TABLA := 'CENTRO_COSTO';

BEGIN

  MI_CAMPOS:=PCK_SYSMAN_UTL.FC_LISTA_CAMPOMERGE(
                            UN_TABLA     => MI_TABLA
                           ,UN_EXCLUIDOS => 'ANO,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,CREATED_BY'
                           ,UN_TIPO      => 'IC'
                           ,UN_ALIASINI  => ''
                           ,UN_ALIASFIN  => '') 
                           || ', ANO,DATE_CREATED,CREATED_BY' ;
  MI_VALORES := ' SELECT ' || PCK_SYSMAN_UTL.FC_LISTA_CAMPOMERGE(
                                             UN_TABLA     => MI_TABLA
                                            ,UN_EXCLUIDOS => 'ANO,DATE_CREATED,DATE_MODIFIED,MODIFIED_BY,CREATED_BY'
                                            ,UN_TIPO      => 'IC'
                                            ,UN_ALIASINI  => ''
                                            ,UN_ALIASFIN  => '') 
                            || ','|| UN_ANO_CREAR 
                            || ',SYSDATE,'''|| UN_USUARIO || ''
                 ||' FROM ' || MI_TABLA  
                 ||' WHERE COMPANIA  = ''' || UN_COMPANIA 
                 || ''' AND ANO           = '   ||UN_ANO_SESSION  ;
  BEGIN
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                  UN_TABLA   => 'CENTRO_COSTO'
                                 ,UN_ACCION  => 'IS'
                                 ,UN_CAMPOS  => MI_CAMPOS
                                 ,UN_VALORES => MI_VALORES
                                 ); 
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
    RAISE PCK_EXCEPCIONES.EXC_NOMINA;
  END;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE
               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_NOMINA_CREARCTOSDECOSTO
               ,UN_TABLAERROR => 'CENTRO_COSTO'
              );
END PR_CREARCENTROSDECOSTO;

--21
FUNCTION FC_DIFERIR_QUIN
  /*
    NAME              : FC_DIFERIR_QUIN == > ACCES FORMULARIO DE QUINQUENIO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 09/11/2015
    TIME              : 16:48 
    SOURCE MODULE     : 
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 09/02/2017
    TIME              : 
    DESCRIPTION       : INGRESA A NOVEDADES LA NOVEDAD DE QUINQUENIO.\ SE AJUSTA AL ESTANDAR.
    @NAME             : getDiferirQuin
    @METHOD           : GET                    
    */
(
UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
UN_PROCESO       IN  PCK_SUBTIPOS.TI_ID_DE_PROCESO,
UN_ID_EMPLEADO   IN  PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
UN_OPCION        IN  VARCHAR2 DEFAULT NULL,
UN_FECHAPAGO     IN  DATE
)
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
MI_RTA           VARCHAR2(100);
BEGIN

  FOR RS IN (
    SELECT   ANO
           , MES
           , PERIODO
    FROM PERIODOS
   WHERE COMPANIA      = UN_COMPANIA
     AND ID_DE_PROCESO = UN_PROCESO
     AND UN_FECHAPAGO BETWEEN FECHAINICIO AND FECHAFINAL
     AND ACUMULADO  NOT IN(0)
     AND DIFERIDOS  NOT IN(0)
    )
  LOOP
    IF UN_OPCION IS NOT NULL THEN
      PCK_NOMINA.PR_BORRARNOVEDAD(UN_COMPANIA
                , UN_PROCESO    => UN_PROCESO
                , UN_ANIO       => RS.ANO
                , UN_MES        => RS.MES
                , UN_PERIODO    => RS.PERIODO
                , UN_IDEMPLEADO => UN_ID_EMPLEADO
                , UN_IDCONCEPTO => 410);
    ELSE
      PCK_NOMINA.PR_INCLUIRNOVEDAD(
                 UN_COMPANIA   => UN_COMPANIA
                ,UN_PROCESO    => UN_PROCESO
                ,UN_ANIO       => RS.ANO
                ,UN_MES        => RS.MES
                ,UN_PERIODO    => RS.PERIODO
                ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                ,UN_IDCONCEPTO => 410
                ,UN_VALOR      =>  PCK_SYSMAN_UTL.FC_IIF(
                                                  UN_CONDICION => UN_OPCION IS NULL
                                                 ,UN_SI        => 1
                                                 ,UN_NO        => 0));
    END IF;

    RETURN -1;
    EXIT;
  END LOOP; 

END FC_DIFERIR_QUIN;

--22
FUNCTION FC_DIFERIR_ENC
  /*
    NAME              : FC_DIFERIR_ENC == > ACCES FORMULARIO DE ENCARGOS
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 09/11/2015
    TIME              : 16:48 
    SOURCE MODULE     : 
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 09/02/2017
    TIME              : 
    DESCRIPTION       : INGRESA A NOVEDADES LA NOVEDAD DE ENCARGOS
    @NAME             : getDiferirEnc
    @METHOD           : GET

    */
(
  UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROCESO      IN  PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_ID_EMPLEADO  IN  PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
  UN_DIFERIDO     IN  PCK_SUBTIPOS.TI_ENTERO,
  UN_OPCION       IN  VARCHAR2                       DEFAULT NULL,
  UN_FECHAINICIO  IN  DATE,
  UN_PORGASTO     IN  PCK_SUBTIPOS.TI_PORCENTAJE     DEFAULT 0,
  UN_SALARIO      IN  PCK_SUBTIPOS.TI_DOBLE          DEFAULT 0,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO  
)
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
MI_RTA             VARCHAR2(100);
MI_CNDIASENCARGO   PCK_SUBTIPOS.TI_ENTERO; 
MI_CNSALARIOENC    PCK_SUBTIPOS.TI_DOBLE;
MI_CNGASTOSREPENC  PCK_SUBTIPOS.TI_DOBLE;

BEGIN
  <<RECORREPERIODOS>> 
  FOR RS IN (
    SELECT   ANO
           , MES
           , PERIODO
    FROM PERIODOS
    WHERE COMPANIA      = UN_COMPANIA
      AND ID_DE_PROCESO = UN_PROCESO
      AND UN_FECHAINICIO BETWEEN FECHAINICIO AND FECHAFINAL
      AND ACUMULADO NOT IN(0)
      AND DIFERIDOS NOT IN(0)
    )
  LOOP
    MI_CNDIASENCARGO    :=  TO_NUMBER(PCK_NOMINA_COM1.FC_PARAMETRO(
                                                      UN_COMPANIA => UN_COMPANIA
                                                     ,UN_OPCION   => 7));
    MI_CNSALARIOENC     :=  TO_NUMBER(PCK_NOMINA_COM1.FC_PARAMETRO(
                                                      UN_COMPANIA => UN_COMPANIA
                                                     ,UN_OPCION   => 9));
    MI_CNGASTOSREPENC   :=  TO_NUMBER(PCK_NOMINA_COM1.FC_PARAMETRO(
                                                      UN_COMPANIA => UN_COMPANIA
                                                     ,UN_OPCION   => 22));
    IF UN_DIFERIDO>0 THEN
    	IF ( PCK_PARST.FC_PAR('PRORRATEAR SI EXISTE MAS DE UN ENCARGO EN EL PERIODO', 'NO')  = 'NO' ) THEN	
		      MI_RTA:=PCK_NOMINA_COM2.FC_DIFERIR(
		                              UN_COMPANIA    => UN_COMPANIA
		                             ,UN_PROCESO     => UN_PROCESO
		                             ,UN_ANIO        => RS.ANO
		                             ,UN_MES         => RS.MES
		                             ,UN_PERIODO     => RS.PERIODO
		                             ,UN_ID_EMPLEADO => UN_ID_EMPLEADO
		                             ,UN_CONCEPTO    => MI_CNDIASENCARGO
		                             ,UN_DIFERIDO    => UN_DIFERIDO
		                             ,UN_OPCION      => UN_OPCION
		                             ,UN_CONCEPTO1   => MI_CNSALARIOENC
		                             ,UN_VALOR1      => UN_SALARIO
		                             ,UN_CONCEPTO2   => MI_CNGASTOSREPENC
		                             ,UN_VALOR2      => UN_PORGASTO
		                             ,UN_FECHAINICIO => UN_FECHAINICIO
		                             ,UN_INCAPACIDAD => 1
		                             ,UN_USUARIO     => UN_USUARIO);
		ELSE
            MI_RTA:=PCK_NOMINA_COM2.FC_DIFERIRENC(
                              UN_COMPANIA    => UN_COMPANIA
                             ,UN_PROCESO     => UN_PROCESO
                             ,UN_ANIO        => RS.ANO
                             ,UN_MES         => RS.MES
                             ,UN_PERIODO     => RS.PERIODO
                             ,UN_ID_EMPLEADO => UN_ID_EMPLEADO
                             ,UN_CONCEPTO    => MI_CNDIASENCARGO
                             ,UN_DIFERIDO    => UN_DIFERIDO
                             ,UN_OPCION      => UN_OPCION
                             ,UN_CONCEPTO1   => MI_CNSALARIOENC
                             ,UN_VALOR1      => UN_SALARIO
                             ,UN_CONCEPTO2   => MI_CNGASTOSREPENC
                             ,UN_VALOR2      => UN_PORGASTO
                             ,UN_FECHAINICIO => UN_FECHAINICIO
                             ,UN_INCAPACIDAD => 1
                             ,UN_USUARIO     => UN_USUARIO);
        END IF;
    END IF;
    RETURN -1;
    EXIT;
  END LOOP RECORREPERIODOS; 
END FC_DIFERIR_ENC;

--23
FUNCTION FC_DIFERIR_INTVAC
  /*
    NAME              : FC_DIFERIR_INTVAC == > ACCES FORMULARIO DE INTERRUPCION DE VACACIONES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 24/11/2015
    TIME              : 08:48 
    SOURCE MODULE     : \ JAVIER ANDRES RODRIGUEZ RIOS
    MODIFIER          : 
    DATE MODIFIED     : \ 09/02/2017
    TIME              : 
    DESCRIPTION       : INGRESA A NOVEDADES LA NOVEDAD DE INTERRRUPCION DE VACACIONES
                        \ SE AJUSTA AL ESTANDAR.
    @NAME             : getDiferirIntVac
    @METHOD           : GET

    */
(
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROCESO            IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_ID_EMPLEADO        IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
  UN_OPCION             IN VARCHAR2              DEFAULT NULL,
  UN_FECHAPAGO          IN DATE,
  UN_ENDINERO           IN PCK_SUBTIPOS.TI_LOGICO,
  UN_FECHAINTERRUPCION  IN DATE,
  UN_FECHAFINALDISFRUTE IN DATE,
  UN_DIASINTERRUPCION   IN PCK_SUBTIPOS.TI_ENTERO,
  UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO 
)
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
--MI_ERROR_FUN        NUMBER:=GL_ERROR_NUM + 24;
  MI_RTA              VARCHAR2(100);
  MI_VACACIONESDINERO PCK_SUBTIPOS.TI_DOBLE;
  MI_DIASDIFERIR      PCK_SUBTIPOS.TI_ENTERO;
  MI_FINICIO        DATE;  --JM 12/05/2025 CC 1513
  MI_FFINAL         DATE;  --JM 12/05/2025 CC 1513
  MI_HABILES        BOOLEAN DEFAULT FALSE; --JM 05/03/2026 CC 3444
BEGIN
  MI_VACACIONESDINERO :=  TO_NUMBER(PCK_NOMINA_COM1.FC_PARAMETRO(
                                                    UN_COMPANIA => UN_COMPANIA
                                                   ,UN_OPCION   => 6)); --419
  MI_DIASDIFERIR:=TRUNC(UN_FECHAINTERRUPCION)-TRUNC(UN_FECHAFINALDISFRUTE)+1;

  MI_HABILES := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(
                    UN_COMPANIA  => UN_COMPANIA
                   ,UN_NOMBRE    => 'LIQUIDAR VACACIONES Y LR EN BASE A DIAS HABILES'
                   ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                   ,UN_FECHA_PAR => SYSDATE),'NO') = 'SI' THEN TRUE ELSE FALSE END; -- JM CC 3444
                   
  <<RECORREPERIODOSFEC>>
  FOR RS IN (--MOD JM 12/05/2025 CC 1513 
    SELECT  ANO
          , MES
          , PERIODO
          ,FECHAINICIO
          ,FECHAFINAL
          ,ROW_NUMBER() OVER (ORDER BY ANO, MES) AS ROW_NUM
    FROM PERIODOS
    WHERE COMPANIA      = UN_COMPANIA
      AND ID_DE_PROCESO = UN_PROCESO
      AND (UN_FECHAINTERRUPCION BETWEEN FECHAINICIO AND FECHAFINAL
      OR UN_FECHAFINALDISFRUTE BETWEEN FECHAINICIO AND FECHAFINAL
      OR (MES BETWEEN EXTRACT(MONTH FROM UN_FECHAINTERRUPCION) AND EXTRACT(MONTH FROM UN_FECHAFINALDISFRUTE) 
      AND  ANO BETWEEN EXTRACT(YEAR FROM UN_FECHAINTERRUPCION) AND EXTRACT(YEAR FROM UN_FECHAFINALDISFRUTE)))
      AND ACUMULADO NOT IN(0)
      AND DIFERIDOS NOT IN(0)
      ORDER BY ANO,MES
    )
  LOOP

  --20180606_1100:@eamaya  Ajuste del parametro UN_VALOR para los conceptos 96 y 98
    IF UN_ENDINERO <> 0 THEN
      PCK_NOMINA.PR_INCLUIRNOVEDAD(
                 UN_COMPANIA   => UN_COMPANIA
                ,UN_PROCESO    => UN_PROCESO
                ,UN_ANIO       => RS.ANO
                ,UN_MES        => RS.MES
                ,UN_PERIODO    => RS.PERIODO
                ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                ,UN_IDCONCEPTO => MI_VACACIONESDINERO
                ,UN_VALOR      =>  PCK_SYSMAN_UTL.FC_IIF(
                                                  UN_CONDICION => UN_OPCION IS NULL
                                                 ,UN_SI        => 1
                                                 ,UN_NO        => 0));
      PCK_NOMINA.PR_INCLUIRNOVEDAD(
                 UN_COMPANIA   => UN_COMPANIA
                ,UN_PROCESO    => UN_PROCESO
                ,UN_ANIO       => RS.ANO
                ,UN_MES        => RS.MES
                ,UN_PERIODO    => RS.PERIODO
                ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                ,UN_IDCONCEPTO => 96
                ,UN_VALOR      => CASE WHEN MI_HABILES THEN UN_DIASINTERRUPCION ELSE UN_FECHAFINALDISFRUTE - UN_FECHAINTERRUPCION END ); --MOD JM CC 3444
    ELSE
      IF(RS.ROW_NUM = 1) THEN
        PCK_NOMINA.PR_INCLUIRNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => RS.ANO
                  ,UN_MES        => RS.MES
                  ,UN_PERIODO    => RS.PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO => 91
                  ,UN_VALOR      => UN_DIASINTERRUPCION);
      END IF;
       IF (UN_FECHAINTERRUPCION >= RS.FECHAINICIO) THEN --Dias proporcionales al periodo  JM 12/05/2025 CC 1513 
         MI_FINICIO := UN_FECHAINTERRUPCION;
        ELSE 
         MI_FINICIO := RS.FECHAINICIO;
       END IF;
       
       IF (UN_FECHAFINALDISFRUTE > RS.FECHAFINAL) THEN --Dias proporcionales al periodo  JM 12/05/2025 CC 1513 
         MI_FFINAL := RS.FECHAFINAL;
        ELSE 
         MI_FFINAL := UN_FECHAFINALDISFRUTE;
       END IF;

      PCK_NOMINA.PR_INCLUIRNOVEDAD(
                 UN_COMPANIA   => UN_COMPANIA
                ,UN_PROCESO    => UN_PROCESO
                ,UN_ANIO       => RS.ANO
                ,UN_MES        => RS.MES
                ,UN_PERIODO    => RS.PERIODO
                ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                ,UN_IDCONCEPTO => 98           
                --(APINEDA:14/11/2018)-Se cambia resta entre fechas por llamado a la función PCK_SYSMAN_UTL.FC_EDAD para averiguar número de días calendario entre dos fechas
                ,UN_VALOR      => CASE WHEN MI_HABILES THEN PCK_SYSMAN_UTL.FC_DIASHABIL(UN_COMPANIA => UN_COMPANIA,UN_FECHAINI   => MI_FINICIO ,UN_FECHAFIN  => MI_FFINAL)  ELSE PCK_SYSMAN_UTL.FC_EDAD(MI_FINICIO, MI_FFINAL) END ); --MOD JM 12/05/2025 CC 1513 MOD JM CC 3444
    END IF;

  --20180606_1100:@eamaya FIN

    IF UN_OPCION IS NOT NULL THEN
      PCK_NOMINA.PR_BORRARNOVEDAD(
                  UN_COMPANIA   => UN_COMPANIA
                , UN_PROCESO    => UN_PROCESO
                , UN_ANIO       => RS.ANO
                , UN_MES        => RS.MES
                , UN_PERIODO    => RS.PERIODO
                , UN_IDEMPLEADO => UN_ID_EMPLEADO
                , UN_IDCONCEPTO => MI_VACACIONESDINERO);
      PCK_NOMINA.PR_BORRARNOVEDAD(
                  UN_COMPANIA   => UN_COMPANIA
                , UN_PROCESO    => UN_PROCESO
                , UN_ANIO       => RS.ANO
                , UN_MES        => RS.MES
                , UN_PERIODO    => RS.PERIODO
                , UN_IDEMPLEADO => UN_ID_EMPLEADO
                , UN_IDCONCEPTO => 96);
      PCK_NOMINA.PR_BORRARNOVEDAD(
                  UN_COMPANIA   => UN_COMPANIA
                , UN_PROCESO    => UN_PROCESO
                , UN_ANIO       => RS.ANO
                , UN_MES        => RS.MES
                , UN_PERIODO    => RS.PERIODO
                , UN_IDEMPLEADO => UN_ID_EMPLEADO
                , UN_IDCONCEPTO => 91);
      PCK_NOMINA.PR_BORRARNOVEDAD(
                  UN_COMPANIA   => UN_COMPANIA
                , UN_PROCESO    => UN_PROCESO
                , UN_ANIO       => RS.ANO
                , UN_MES        => RS.MES
                , UN_PERIODO    => RS.PERIODO
                , UN_IDEMPLEADO => UN_ID_EMPLEADO
                , UN_IDCONCEPTO => 98);
    END IF;

    IF UN_ENDINERO<>0 THEN
      MI_RTA:=PCK_NOMINA_COM2.FC_DIFERIR(
                               UN_COMPANIA    => UN_COMPANIA
                             , UN_PROCESO     => UN_PROCESO
                             , UN_ANIO        => RS.ANO
                             , UN_MES         => RS.MES
                             , UN_PERIODO     => RS.PERIODO
                             , UN_ID_EMPLEADO => UN_ID_EMPLEADO
                             , UN_CONCEPTO    => 98
                             , UN_DIFERIDO    => MI_VACACIONESDINERO
                             , UN_OPCION      => UN_OPCION
                             , UN_CONCEPTO1   => NULL
                             , UN_VALOR1      => NULL
                             , UN_CONCEPTO2   => NULL
                             , UN_VALOR2      => NULL
                             , UN_FECHAINICIO => TRUNC(UN_FECHAINTERRUPCION)
                             , UN_INCAPACIDAD => 0
                             ,UN_USUARIO     => UN_USUARIO);
      RETURN MI_RTA;    
    END IF;

    --RETURN -1; --Comentado por JM 12/05/2025 CC 1513 
    --EXIT;   --Comentado por JM 12/05/2025 CC 1513 
  END LOOP RECORREPERIODOSFEC; 
  RETURN -1; --MOD JM 12/05/2025 CC 1513  se comentan las de arriba y se agrega el return aqui porque sino nunca hace el ciclo
END FC_DIFERIR_INTVAC;

PROCEDURE PR_CREARDATOS
/*
    NAME              : PR_CREARDATOS  --> EN ACCESS CrearDatos  -
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JHONATAN ACELAS AREVALO
    DATE MIGRADOR     : 14/12/2015
    TIME              : 17:25 PM
    SOURCE MODULE     :
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 09/02/2017 
    TIME              :    
    DESCRIPTION       : CREA DATOS CONTABLES DE UNA COMPAÃ‘IA EN BASE A OTRA COMPANIA 
                        SE AJUSTA EL ESTANDAR

    @NAME:  crearDatos
  */
  (
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA ,
    UN_COMPANIA_BASE   IN PCK_SUBTIPOS.TI_COMPANIA ,
    UN_NOMBRE_COMPANIA IN VARCHAR2,
    UN_SIGLA_COMPANIA  IN VARCHAR2,
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO 
  )
  AS
    MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
    MI_EXISTE_COMPANIA      PCK_SUBTIPOS.TI_ENTERO:=0;
    MI_RTA_ACME             PCK_SUBTIPOS.TI_RTA_ACME;
    MI_TABLA_D              PCK_SUBTIPOS.TI_TABLA DEFAULT 'DEPENDENCIA';

    MI_REEMPLAZOS           PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

    SELECT COUNT(1) 
    INTO MI_EXISTE_COMPANIA
    FROM COMPANIA 
    WHERE CODIGO = UN_COMPANIA;

    IF MI_EXISTE_COMPANIA = 0 THEN 

        MI_CAMPOS := 'CODIGO, NOMBRE, SIGLACOMPANIA, RETENEDOR_FTE, RETENEDOR_IVA,'
                     ||' RETENEDOR_ICA, RETENEDOR_TBRE, DIRECCION, TELEFONO, CLAVE,'
                     ||' LICENCIA, PAIS, DEPARTAMENTO, CIUDAD, NITCOMPANIA, FAX, CODIGODANE, DATE_CREATED, CREATED_BY ';

        MI_VALORES := 'SELECT '''|| UN_COMPANIA ||''' , ''' 
                                 || UN_NOMBRE_COMPANIA || ''',  ''' 
                                 || UN_SIGLA_COMPANIA|| '''
                                 , RETENEDOR_FTE
                                 , RETENEDOR_IVA
                                 , RETENEDOR_ICA
                                 , RETENEDOR_TBRE
                                 , DIRECCION
                                 , TELEFONO
                                 , CLAVE
                                 , LICENCIA
                                 , PAIS
                                 , DEPARTAMENTO
                                 , CIUDAD
                                 , NITCOMPANIA
                                 , FAX
                                 , CODIGODANE 
                                 , SYSDATE
                                 , '''|| UN_USUARIO||'''
                        FROM COMPANIA 
                       WHERE CODIGO = ' || UN_COMPANIA_BASE ; 
      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                        UN_TABLA     => 'COMPANIA'
                                       ,UN_ACCION    => 'IS'
                                       ,UN_CAMPOS    => MI_CAMPOS
                                       ,UN_VALORES   => MI_VALORES
                                       );    
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_NOMINA; 
        END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_NOMINA_INSERTCOMPANIA
                   ,UN_TABLAERROR => 'COMPANIA'
                  );
      END;
    END IF;

/*
    MI_CAMPOS := ' INDICADORVACACIONES,DIASLABORADOS,DIASDISFRUTE,DIASPAGARVACACIONES,VACACIONESDINERO,DIASENCARGO,DIASREEMPLAZO,SALARIOENCARGO,SALARIOREEMPLAZO,CUPOSALUD,DEDUCIBLE,DIASHABILES,IBL,CESANTIAPARCIAL,CESANTIACONSOLIDADA,INTERESCESANTIA,LABSIGPERIODO,PERIODOVACACIONES,SALARIOMINIMO,PORCRETENCION,CIUDAD,DEPARTAMENTO,PORCGASTOS,TIPOAPORTANTE,PRESENTACION,CODIGO_SUCURSALPAGADORA,PORC_PATRON_AFP,PORC_EMPLEADO_AFP,PORC_FSP_AFP,SALARIOSMINIMOS_FSP,PORC_TOTAL_AFP,PORC_PATRON_EPS,PORC_EMPLEADO_EPS,PORC_GARANTIAS_EPS,PORC_TOTAL_EPS,PORC_FONDO_ARP,PORC_ICBF,PORC_SENA,PORC_CAJA,PORC_ESAP,PORC_INST,SALARIOS_MAXIMOS_AFP,SALARIOS_MAXIMOS_EPS,SALARIOS_MAXIMOS_ARP,PORC_SALARIO_INTEGRAL ';

    MI_VALORES := ' SELECT  INDICADORVACACIONES,DIASLABORADOS,DIASDISFRUTE,DIASPAGARVACACIONES,VACACIONESDINERO,DIASENCARGO,DIASREEMPLAZO,SALARIOENCARGO,SALARIOREEMPLAZO,CUPOSALUD,DEDUCIBLE,DIASHABILES,IBL,CESANTIAPARCIAL,CESANTIACONSOLIDADA,INTERESCESANTIA,LABSIGPERIODO,PERIODOVACACIONES,SALARIOMINIMO,PORCRETENCION,CIUDAD,DEPARTAMENTO,PORCGASTOS,TIPOAPORTANTE,PRESENTACION,CODIGO_SUCURSALPAGADORA,PORC_PATRON_AFP,PORC_EMPLEADO_AFP,PORC_FSP_AFP,SALARIOSMINIMOS_FSP,PORC_TOTAL_AFP,PORC_PATRON_EPS,PORC_EMPLEADO_EPS,PORC_GARANTIAS_EPS,PORC_TOTAL_EPS,PORC_FONDO_ARP,PORC_ICBF,PORC_SENA,PORC_CAJA,PORC_ESAP,PORC_INST,SALARIOS_MAXIMOS_AFP,SALARIOS_MAXIMOS_EPS,SALARIOS_MAXIMOS_ARP,PORC_SALARIO_INTEGRAL  FROM PARAMETRO_DE_ENTRADA  WHERE COMPANIA =  '|| UN_COMPANIA_BASE  ; 

    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ('PARAMETRO_DE_ENTRADA', 'M', MI_VALORES, NULL, NULL, ' COMPANIA = '|| UN_COMPANIA );
*/
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'CONCEPTOS'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'PARAMETROS_DE_ENTRADA'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'PATRONALES'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'BANCO'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;

    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'ANO'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;  

    -- 20181003_1243:@pespitia:  Replicar DEPENDENCIA, excluir valor CODAMISIONAL
    PCK_SYSMAN_UTL.PR_REPLICAR_DATOS_TABLA(UN_TABLA     => 'DEPENDENCIA'
                                          ,UN_CLAVE     => 'COMPANIA'
                                          ,UN_NEW_VALOR => UN_COMPANIA
                                          ,UN_OLD_VALOR => UN_COMPANIA_BASE
                                          ,UN_EXCLUIDOS => 'CODAMISIONAL'
                                          ,UN_USUARIO   => UN_USUARIO);

    -- 20181003_1243:@pespitia:  Adicionar DESTINORECURSOS
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'DESTINORECURSOS'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;      

    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'ESCALAFON'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;

    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'TIPO_EMBARGO'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;

    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'CLASE_DEMANDANTE'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;

    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'TIPOS_DOCUMENTOS'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;

    -- 20181003_1243:@pespitia:  Adicionar UNIDAD_AV                  
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(UN_TABLA         => 'UNIDAD_AV'
                                       ,UN_EXCLUIDOS     => 'COMPANIA'
                                       ,UN_VALOR_NUEVO   => UN_COMPANIA
                                       ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;

    -- 20181003_1243:@pespitia:  Adicionar GRADOS_AV                  
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(UN_TABLA         => 'GRADOS_AV'
                                       ,UN_EXCLUIDOS     => 'COMPANIA'
                                       ,UN_VALOR_NUEVO   => UN_COMPANIA
                                       ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;

    -- 20181003_1243:@pespitia:  Adicionar ZONA 
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(UN_TABLA         => 'ZONA'
                                       ,UN_EXCLUIDOS     => 'COMPANIA'
                                       ,UN_VALOR_NUEVO   => UN_COMPANIA
                                       ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;

    -- 20181003_1243:@pespitia:  Adicionar TERCERO
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'TERCERO'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE
                  ,UN_CONDICION     => ' BASE.NIT      NOT IN('||CHR(39)||PCK_DATOS.FC_CONS_TERCERO ||CHR(39)||') 
                                     AND BASE.SUCURSAL NOT IN('||CHR(39)||PCK_DATOS.FC_CONS_SUCURSAL||CHR(39)||')') ;      

    -- 20181003_1243:@pespitia:  Replicar FONDO, excluir valor RUBRO_SIIF_EMPLEADO y RUBRO_SIIF_PATRONO
    PCK_SYSMAN_UTL.PR_REPLICAR_DATOS_TABLA(UN_TABLA     => 'FONDO'
                                          ,UN_CLAVE     => 'COMPANIA'
                                          ,UN_NEW_VALOR => UN_COMPANIA
                                          ,UN_OLD_VALOR => UN_COMPANIA_BASE
                                          ,UN_EXCLUIDOS => 'RUBRO_SIIF_EMPLEADO,RUBRO_SIIF_PATRONO'
                                          ,UN_USUARIO   => UN_USUARIO);

    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(UN_TABLA         => 'CLASEFONDO'
                                       ,UN_EXCLUIDOS     => 'COMPANIA'
                                       ,UN_VALOR_NUEVO   => UN_COMPANIA
                                       ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;

    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'ESTABLECIMIENTOS_DOCENTES'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;    
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'ESTADO_CIVIL'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'FESTIVOS'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;

    -- 20181003_1243:@pespitia: @pespitia:  Adicionar REFERENCIA
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'REFERENCIA'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;     

    -- 20181003_1243:@pespitia: Replicar AUXILIAR, excluir valor CODIGO_FUT_F3 y CODIGOBP
    PCK_SYSMAN_UTL.PR_REPLICAR_DATOS_TABLA(UN_TABLA     => 'AUXILIAR'
                                          ,UN_CLAVE     => 'COMPANIA'
                                          ,UN_NEW_VALOR => UN_COMPANIA
                                          ,UN_OLD_VALOR => UN_COMPANIA_BASE
                                          ,UN_EXCLUIDOS => 'CODIGO_FUT_F3,CODIGOBP'
                                          ,UN_USUARIO   => UN_USUARIO);    

    -- 20181003_1243:@pespitia: Adicionar FUENTE_RECURSOS              
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'FUENTE_RECURSOS'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ; 

    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'MES'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'MODALIDAD_PRESTAMO'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'PARAMETRO'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'PARENTESCO'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'PROCESOS_DE_NOMINA'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'RETEFUENTEUVT'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'SEDES'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'TIPO_INCAPACIDAD'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'TIPO_EMBARGO'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'TIPOS_LICENCIA'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'TIPOS_DE_EMPLEADO'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'PERIODOS'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'CATEGORIA'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;

    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'CENTRO_COSTO'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;

    -- 20181003_1243:@pespitia: Replicar PLAN_CNT_EQUIVALENTE
    PCK_SYSMAN_UTL.PR_REPLICAR_DATOS_TABLA(UN_TABLA     => 'PLAN_CNT_EQUIVALENTE'
                                          ,UN_CLAVE     => 'COMPANIA'
                                          ,UN_NEW_VALOR => UN_COMPANIA
                                          ,UN_OLD_VALOR => UN_COMPANIA_BASE
                                          ,UN_USUARIO   => UN_USUARIO);                     

    -- 20181003_1243:@pespitia: Replicar PLAN_CONTABLE
    <<RS_ANO_PLAN_CONTABLE>>
    FOR MI_RS IN(SELECT DISTINCT ANO
                FROM PLAN_CONTABLE
                WHERE COMPANIA = UN_COMPANIA_BASE)
    LOOP
      PCK_PREPARAR_ANO.PR_COPIAR_PLAN_CONTABLE(UN_COMPANIA         => UN_COMPANIA_BASE
                                              ,UN_ANO_DESTINO      => MI_RS.ANO
                                              ,UN_ANO_ORIGEN       => MI_RS.ANO
                                              ,UN_COMPANIA_DESTINO => UN_COMPANIA);
    END LOOP RS_ANO_PLAN_CONTABLE;

    -- 20181003_1243:@pespitia: Replicar CARGOS, excluir valor FACTOR_RIESGO
    PCK_SYSMAN_UTL.PR_REPLICAR_DATOS_TABLA(UN_TABLA     => 'CARGOS'
                                          ,UN_CLAVE     => 'COMPANIA'
                                          ,UN_NEW_VALOR => UN_COMPANIA
                                          ,UN_OLD_VALOR => UN_COMPANIA_BASE
                                          ,UN_EXCLUIDOS => 'FACTOR_RIESGO'
                                          ,UN_USUARIO   => UN_USUARIO);    

    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'FORMANOMBRAMIENTO'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;

-- AGREGADA 

    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'TIPO_RETENCIONES'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE) ;
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'PERSONAL'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE
                  ,UN_CONDICION     => ' BASE.ID_DE_EMPLEADO = 0 ') ; 
    PCK_SYSMAN_UTL.PR_INSERTA_FALTANTES(
                   UN_TABLA         => 'NOVEDADES'
                  ,UN_EXCLUIDOS     =>'COMPANIA'
                  ,UN_VALOR_NUEVO   => UN_COMPANIA
                  ,UN_BASE_EXCLUIDO => UN_COMPANIA_BASE
                  ,UN_CONDICION     => '     BASE.ID_DE_EMPLEADO = 0 
                                         AND BASE.PERIODO        = 0  
                                         AND BASE.MES            = 0 
                                         AND BASE.ANO            = 0  ') ;
END PR_CREARDATOS;

--26
FUNCTION FC_NOMBRECONCEPTO
/*
    NAME              : FC_NOMBRECONCEPTO 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JHONATAN ACELAS AREVALO
    DATE MIGRADOR     : 22/12/2015
    TIME              : 16:09 PM
    SOURCE MODULE     :
    MODIFIER          : \ JAVIER ANDRES RODRIGUEZ RIOS
    DATE MODIFIED     : \ 09/02/2017
    TIME              :    
    DESCRIPTION       : RETIORNA EL NOMBRE DE UN CONCEPTO \ SE AJUSTA AL ESTANDAR.
    @NAME             : getNombreConcepto
    @METHOD           : GET
  */
(
  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ID_DE_CONCEPTO IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO
)
RETURN VARCHAR2
AS
  --MI_ERROR_FUN        NUMBER:=GL_ERROR_NUM + 25;
  MI_NOMBRE            VARCHAR2(500 CHAR);
  MI_REEMPLAZOS        PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
  BEGIN 
    BEGIN
    SELECT NOMBRE_CONCEPTO 
      INTO MI_NOMBRE 
      FROM CONCEPTOS 
     WHERE ID_DE_CONCEPTO = UN_ID_DE_CONCEPTO
       AND COMPANIA       = UN_COMPANIA;    
  EXCEPTION WHEN NO_DATA_FOUND THEN 
  /**  MI_REEMPLAZOS(0).CLAVE := 'UN_ID_DE_CONCEPTO'; 
    MI_REEMPLAZOS(0).VALOR := UN_ID_DE_CONCEPTO;
    RAISE PCK_EXCEPCIONES.EXC_NOMINA; **/

    RETURN NVL(MI_NOMBRE,''); 
  END;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(
		      UN_EXC_COD    => SQLCODE
		     ,UN_ERROR_COD  => PCK_ERRORES.ERRR_NOMINA_NOMBRECONCEPTO
		     ,UN_TABLAERROR => 'CONCEPTOS'
         ,UN_REEMPLAZOS => MI_REEMPLAZOS 
		    );
 END;
RETURN MI_NOMBRE; 
END FC_NOMBRECONCEPTO;


FUNCTION FC_VALORACUMULADOCONCEPTOTOTAL
  /*
    NAME              : FC_VALORACUMULADOCONCEPTOTOTAL
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 
    TIME              : 
    SOURCE MODULE     : \ JAVIER ANDRES RODRIGUEZ RIOS
    MODIFIER          : 
    DATE MODIFIED     : \ 09/02/2017
    TIME              : 
    DESCRIPTION       : \ SE AJUSTA AL ESTANDAR.
    @NAME             : getValorAcumuladoConceptoTotal
    @METHOD           : GET

    */
(
  UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROCESO    IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_ANO        IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES        IN PCK_SUBTIPOS.TI_MES,
  UN_PERIODO    IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
  UN_IDCONCEPTO IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO
)
RETURN NUMBER
AS
 --- MI_ERROR_FUN        NUMBER:=GL_ERROR_NUM + 25;
  MI_VALOR            PCK_SUBTIPOS.TI_DOBLE;
BEGIN

    SELECT NVL(SUM(VALOR), 0)
      INTO MI_VALOR 
      FROM HISTORICOS
     WHERE COMPANIA       = UN_COMPANIA
       AND ID_DE_PROCESO  = UN_PROCESO
       AND ANO            = UN_ANO
       AND MES            = UN_MES
       AND PERIODO        = UN_PERIODO
       AND ID_DE_CONCEPTO = UN_IDCONCEPTO;    

RETURN MI_VALOR;

END FC_VALORACUMULADOCONCEPTOTOTAL;

FUNCTION FC_PERMITEDUPLICAR 
(
 /*
    NAME              : FC_PERMITEDUPLICAR
    AUTHORS           : SYSMAN  SAS
    AUTHOR            : EDWIN FERNANDO CABRERA
    DATE              : 16/02/2023
    TIME              :
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :
    DESCRIPTION       : Funcion que retorna TRUE si un concepto permite cobro anticipado por vacaciones
                        TICKET: 7727323
    */
UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
UN_CONCEPTO IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO
) RETURN BOOLEAN IS
    MI_CONT NUMBER(1);
BEGIN
    SELECT  DISTINCT 1
    INTO    MI_CONT
    FROM    CONCEPTOS
    WHERE   CONCEPTOS.COMPANIA = UN_COMPANIA
            AND CONCEPTOS.ID_DE_CONCEPTO = UN_CONCEPTO
            AND CONCEPTOS.CLASE = 5
            AND CONCEPTOS.PERMITE_DUPLICAR != 0;
    
    RETURN (TRUE);
EXCEPTION WHEN NO_DATA_FOUND THEN
    RETURN (FALSE);
END FC_PERMITEDUPLICAR;

PROCEDURE PR_SINDICATO_TODOS
    /*
    NAME              : PR_SINDICATO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
    DATE MIGRADOR     : 18/10/2018
    TIME              :
    SOURCE MODULE     : MEJORA PARA INDEPENDIZAR PROCESO CALCULO SINDICATO
    MODIFIER          : JOSE PASCUAL GOMEZ
    DATE MODIFIED     : 23/04/2019
    TIME              :
    DESCRIPTION       : Funcion para calcular aportes a sindicato por entidad.
                        Se ajusta para incorporar el nuevo modelo de calculo de sindicatos
    */
AS
    MI_MSG                  PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_PORSINDICATO2 		PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_PORSINDICATO3 		PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_CAMBIA129            BOOLEAN DEFAULT FALSE;    
BEGIN
    IF PCK_PARST.FC_PAR('CALCULAR SINDICATO CON MULTIPLE APORTE', 'NO') = 'SI' THEN
        PCK_NOMINA_COM9.PR_CALCULO_SINDICATOS(UN_COMPANIA       => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).COMPANIA,
                                              UN_ID_DE_EMPLEADO => PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                                            );
    ELSE
        --UPC    
        IF PCK_PARENTR.PARAMETRO31 = '892.300.285-6' THEN
            PCK_NOMINA.CN(129) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.FC_CN(196) + PCK_NOMINA.FC_CN(529)) * PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO / 100), 0);   
        ELSE
        --OTRAS ENTIDADES
            IF (PCK_NOMINA.FC_CN(30) = 2 OR PCK_NOMINA.FC_CN(30) = 3 OR ((PCK_PARST.FC_PAR('DESCUENTO SINDICATO QUINCENAL', 'NO') = 'SI' OR PCK_PARST.FC_PAR('DESCUENTO SINDICATO QUINCENAL', 'NO') = '') AND PCK_NOMINA.GL_SPER <> 4)) AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> 2 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO <> 0  AND PCK_NOMINA.FC_CN(129) = 0 THEN
                IF PCK_NOMINA.FC_CN(306) <> 0 AND PCK_NOMINA.FC_CN(129) < 5000 OR PCK_NOMINA.CPARENTRADA(1).NIT = '891.800.260-4' THEN
                    PCK_NOMINA.CN(129) := 0;
                END IF;

                MI_CAMBIA129 := FALSE;
                IF PCK_NOMINA.FC_CN(203) = 0 AND PCK_NOMINA.FC_CN(129) = 0 THEN
                    IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO <> 0  AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO > 0 THEN
                        IF PCK_PARST.FC_PAR('DESCUENTO SINDICATO QUINCENAL', 'NO') = 'SI' THEN
                            IF PCK_NOMINA.FC_CN(129) = 0 THEN
                                PCK_NOMINA.CN(129) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.FC_CN(196)) * PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO / 100) / 2, 0);
                                PCK_NOMINA.CN(129) := PCK_NOMINA.FC_CN(129) + PCK_NOMINA.FC_CN(529);
                                MI_CAMBIA129 := TRUE;
                            END IF;
                        ELSE
                            IF PCK_NOMINA.FC_CN(129) = 0 THEN
                                PCK_NOMINA.CN(129) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.FC_CN(196)) * PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO / 100), 0);
                                PCK_NOMINA.CN(129) := PCK_NOMINA.FC_CN(129) + PCK_NOMINA.FC_CN(529);
                                
                                -- TICKET 7723819 ECABRERA: DESCUENTO ANTICIPADO DE CUOTA DE SINDICATO, EN PERIODO DE DISFRUTE
                                --                          DE VACACIONES NO SE DESCUENTA
                                IF ( PCK_PARST.FC_PAR('DESCUENTO ANTICIPADO AUTOMATICO POR VACACIONES','NO') = 'SI' AND FC_PERMITEDUPLICAR(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).COMPANIA,129) ) THEN
                                    IF ( PCK_NOMINA.FC_CN(403) <> 0 ) THEN
                                        PCK_NOMINA.CN(129) := PCK_NOMINA.FC_CN(129) * 2;
                                    ELSIF ( PCK_NOMINA.FC_CN(35) <> 0  ) THEN
                                        PCK_NOMINA.CN(129) := 0;
                                    END IF;
                                END IF;
                                -- TICKET 7723819 FIN --
                                
                                MI_CAMBIA129 := TRUE;
                            END IF;
                        END IF;

                    ELSE
                        IF PCK_PARST.FC_PAR('DESCUENTO SINDICATO QUINCENAL', 'NO') = 'SI' THEN
                            IF PCK_NOMINA.FC_CN(129) = 0 THEN
                                PCK_NOMINA.CN(129) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.FC_CN(196)) * PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO / 100) / 2, 0);
                                PCK_NOMINA.CN(129) := PCK_NOMINA.FC_CN(129) + PCK_NOMINA.FC_CN(529);
                                MI_CAMBIA129 := TRUE;
                            END IF;
                        ELSE
                            IF PCK_NOMINA.FC_CN(129) = 0 THEN
                                PCK_NOMINA.CN(129) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.FC_CN(196)) * PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO / 100), 0);
                                PCK_NOMINA.CN(129) := PCK_NOMINA.FC_CN(129) + PCK_NOMINA.FC_CN(529);
                                MI_CAMBIA129 := TRUE;
                            END IF;
                        END IF;
                    END IF;
                    IF PCK_NOMINA.FC_CN(129) >= 0 AND (PCK_NOMINA.FC_CN(35) + PCK_NOMINA.FC_CNP(35)) >= (PCK_NOMINA.FC_CN(4) + PCK_NOMINA.FC_CNP(4)) THEN
                        PCK_NOMINA.CN(129) := 0;
                    END IF;

                    IF PCK_PARST.FC_PAR('CALCULAR SINDICATO PROPORCIONAL A DIAS LABORADOS', 'NO') = 'SI' AND PCK_NOMINA.FC_CN(129) > 0  AND MI_CAMBIA129 THEN
                        PCK_NOMINA.CN(129) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(129) / 30) * PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CN(11) );
                    END IF;
                END IF;
            END IF;

            IF PCK_NOMINA.CPARENTRADA(1).NIT = '891855138-1' THEN
                IF (PCK_NOMINA.FC_CN(30) = 2 OR PCK_NOMINA.FC_CN(30) = 3) AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO <> 0  THEN
                    IF PCK_NOMINA.FC_CN(203) = 0 THEN
                        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO <> 0  AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO > 0 THEN
                            PCK_NOMINA.CN(129) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(129) = 0, PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.FC_CN(196)) * PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO / 100) * (1 + PCK_NOMINA.GL_PERVACS), 0), PCK_NOMINA.FC_CN(129));
                        ELSE
                            PCK_NOMINA.CN(129) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(129) = 0, PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(1) + PCK_NOMINA.FC_CN(196)) * 1 / 100, 0), PCK_NOMINA.FC_CN(129));
                        END IF;
                        IF PCK_PARST.FC_PAR('CALCULAR SINDICATO PROPORCIONAL A DIAS LABORADOS', 'NO') = 'SI' AND PCK_NOMINA.FC_CN(129) > 0 THEN
                            PCK_NOMINA.CN(129) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(129) / 30) * PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CN(11) );
                        END IF;
                    END IF;
                END IF;
                IF PCK_NOMINA.FC_CN(451) <> 0 THEN
                    PCK_NOMINA.CN(TO_NUMBER(SUBSTR(PCK_NOMINA.FC_CN(451), 1, 3))) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(TO_NUMBER(SUBSTR(PCK_NOMINA.FC_CN(451), 1, 3))) = 0, PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) * SUBSTR(PCK_NOMINA.FC_CN(451), 4, 6) / 100, 0), PCK_NOMINA.FC_CN(TO_NUMBER(SUBSTR(PCK_NOMINA.FC_CN(451), 1, 3))));
                END IF;
            END IF;
        END IF;    
        IF (PCK_NOMINA.FC_CN(30) = 2 OR PCK_NOMINA.FC_CN(30) = 3 AND PCK_NOMINA.GL_SPER <> 4) AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO2 = -1 AND PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0'))) = 0 THEN
            IF TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0')) <> 0 THEN

                --MI_PORSINDICATO2 = MISINDICATO2(GETCOMPANY(), PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 3)
                 MI_PORSINDICATO2 := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO2;
            END IF;
            IF PCK_NOMINA.FC_CN(203) = 0 AND PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0'))) = 0 THEN
                IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO2 = -1 AND MI_PORSINDICATO2 > 0 THEN
                    IF PCK_PARST.FC_PAR('DESCUENTO SINDICATO QUINCENAL', 'NO') = 'SI' THEN
                        PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0'))) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0'))) = 0, PCK_SYSMAN_UTL.FC_ROUND(((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.FC_CN(196)) * MI_PORSINDICATO2 / 100) / 2, 0), PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0'))));
                    ELSE
                        PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0'))) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0'))) = 0, PCK_SYSMAN_UTL.FC_ROUND(((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.FC_CN(196)) * MI_PORSINDICATO2 / 100), 0), PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0'))));
                    END IF;
                ELSE
                    IF PCK_PARST.FC_PAR('DESCUENTO SINDICATO QUINCENAL', 'NO') = 'SI' THEN
                        PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0'))) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0'))) = 0, PCK_SYSMAN_UTL.FC_ROUND(((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.FC_CN(196)) * MI_PORSINDICATO2 / 100) / 2, 0), PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0'))));
                    ELSE
                        PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0'))) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0'))) = 0, PCK_SYSMAN_UTL.FC_ROUND(((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.FC_CN(196)) * MI_PORSINDICATO2 / 100), 0), PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0'))));
                    END IF;
                END IF;
                IF PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0'))) >= 0 AND (PCK_NOMINA.FC_CN(35) + PCK_NOMINA.FC_CNP(35)) >= (PCK_NOMINA.FC_CN(4) + PCK_NOMINA.FC_CNP(4)) THEN
                    PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 2', '0'))) := 0;
                END IF;
            END IF;
        END IF;	   
        --(APINEDA:19/10/2018)-Se agrega para el cálculo de sindicato 3 UPC de acuerdo a especificaciones punto 1 TAR 1000085846
        IF (PCK_NOMINA.FC_CN(30) = 2 OR PCK_NOMINA.FC_CN(30) = 3 AND PCK_NOMINA.GL_SPER <> 4) AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '02' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO3 = -1 AND PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0'))) = 0 THEN
            IF TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0')) <> 0 THEN			
                 MI_PORSINDICATO3 := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO3;
            END IF;
            IF PCK_NOMINA.FC_CN(203) = 0 AND PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0'))) = 0 THEN
                IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO3 = -1 AND MI_PORSINDICATO3 > 0 THEN
                    IF PCK_PARST.FC_PAR('DESCUENTO SINDICATO QUINCENAL', 'NO') = 'SI' THEN
                        PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0'))) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0'))) = 0, PCK_SYSMAN_UTL.FC_ROUND(((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.FC_CN(196)) * MI_PORSINDICATO3 / 100) / 2, 0), PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0'))));
                    ELSE
                        PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0'))) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0'))) = 0, PCK_SYSMAN_UTL.FC_ROUND(((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.FC_CN(196)) * MI_PORSINDICATO3 / 100), 0), PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0'))));
                    END IF;
                ELSE
                    IF PCK_PARST.FC_PAR('DESCUENTO SINDICATO QUINCENAL', 'NO') = 'SI' THEN
                        PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0'))) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0'))) = 0, PCK_SYSMAN_UTL.FC_ROUND(((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.FC_CN(196)) * MI_PORSINDICATO3 / 100) / 2, 0), PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0'))));
                    ELSE
                        PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0'))) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0'))) = 0, PCK_SYSMAN_UTL.FC_ROUND(((PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(10) <> 0, PCK_NOMINA.FC_CN(10), PCK_NOMINA.FC_CN(1)) + PCK_NOMINA.FC_CN(196)) * MI_PORSINDICATO3 / 100), 0), PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0'))));
                    END IF;
                END IF;
                IF PCK_NOMINA.FC_CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0'))) >= 0 AND (PCK_NOMINA.FC_CN(35) + PCK_NOMINA.FC_CNP(35)) >= (PCK_NOMINA.FC_CN(4) + PCK_NOMINA.FC_CNP(4)) THEN
                    PCK_NOMINA.CN(TO_NUMBER(PCK_PARST.FC_PAR('CODIGO CONCEPTO DESCUENTO SINDICATO 3', '0'))) := 0;
                END IF;
            END IF;
        END IF;	    
    END IF;
	--TICKET 7739594 JCROJAS (04/01/2024): Se agrega validación para que se calcule el concepto 129 en periodo 4 y para los empleados con sindicato configurado
    IF PCK_PARST.FC_PAR('CALCULAR CN129 EN PERIODO 4', 'NO') = 'SI' THEN
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).SINDICATO <> 0 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO > 0 AND PCK_NOMINA.GL_SPER = 4 THEN 
            PCK_NOMINA.CN(129) := PCK_SYSMAN_UTL.FC_ROUND(((PCK_NOMINA.FC_CN(1)) * PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).PORC_SINDICATO / 100), 0);
        END IF;
    END IF;	
END PR_SINDICATO_TODOS;

PROCEDURE PR_ESTAMPILLASTODOS

/*
NAME              : PR_ESTAMPILLASTODOS
AUTHORS           : SYSMAN  SAS
AUTHOR MIGRACION  : ANDREA PINEDA OVALLE
DATE MIGRADOR     : 18/10/2018
TIME              :
SOURCE MODULE     : MEJORA PARA INDEPENDIZAR PROCESO CALCULO ESTAMPILLAS
MODIFIER          : 
DATE MODIFIED     : 
TIME              :
DESCRIPTION       : Funcion para calcular descuento de estampillas.
*/
AS
    MI_MSG                  PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_PORSINDICATO2 		PCK_SUBTIPOS.TI_ENTERO :=0;
    MI_CAMBIA129            BOOLEAN DEFAULT FALSE;    
    MI_VALOR NUMBER;
    MI_NIT VARCHAR2(40);
BEGIN
    --OTRAS ENTIDADES
    IF PCK_PARST.FC_PAR('LIQUIDAN DESCUENTO DE ESTAMPILLAS', 'NO') = 'SI' AND PCK_NOMINA.CPARENTRADA(1).NIT <> '891800462' THEN
        PCK_NOMINA.GL_STRETAPAGLOBAL := '77 - CONCEPTO 610';
        IF PCK_NOMINA.FC_CN(610) = 0 THEN
            PCK_NOMINA.CN(610) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CN(97) - PCK_NOMINA.FC_CN(527)) * 2 / 1000, 0)  ;
            IF PCK_PARST.FC_PAR('CALCULAR ESTAMPILLAS PROPORCIONAL A DIAS LABORADOS', 'NO') = 'SI' AND PCK_NOMINA.FC_CN(610) > 0 THEN
            MI_VALOR := PCK_NOMINA.FC_CN(610);
                PCK_NOMINA.CN(610) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(610)/30 * PCK_NOMINA.FC_CN(9) + PCK_NOMINA.FC_CN(11));
                MI_VALOR := PCK_NOMINA.FC_CN(610);
            END IF;
        END IF;
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = 5 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> 4 OR PCK_NOMINA.GL_SPER = 4 THEN
            PCK_NOMINA.CN(610) := 0;
        END IF;
    END IF;
    --UPC    
    IF PCK_PARENTR.PARAMETRO31 = '892.300.285-6'  THEN
        IF PCK_PARST.FC_PAR('CALCULAR ESTAMPILLAS PROPORCIONAL A DIAS LABORADOS', 'NO') = 'SI' AND PCK_NOMINA.FC_CN(610) > 0 THEN
            PCK_NOMINA.CN(610) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(2) *  0.002);
        END IF;        
    END IF;  
    IF PCK_PARENTR.PARAMETRO31 = '820.004.492' THEN 
      PCK_NOMINA.CN(610) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(2) * TO_NUMBER(PCK_PARST.FC_PAR('ESTAMPILLA', '0')));
      PCK_NOMINA.CN(527) := 0;
    END IF;
END PR_ESTAMPILLASTODOS;

FUNCTION FC_DIFERIR_ENC_MOD
  /*
    NAME              : FC_DIFERIR_ENC_MOD 
    AUTHORS           : SYSMAN  SAS
    AUTHOR            : JUAN DANILO ORDUZ R
    DATE              : 21/08/2020
    TIME              : 16:48 
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : INGRESA A NOVEDADES LA NOVEDAD DE ENCARGOS CUANDO SE MODIFICA LA FECHA FINAL DE UN ENCARGO BUCARAMANGA
    @NAME             : difereirEncMod
    @METHOD           : POST

    */
(
  UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROCESO      IN  PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_ID_EMPLEADO  IN  PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
  UN_OPCION       IN  VARCHAR2                       DEFAULT NULL,
  UN_FECHAINICIO  IN  DATE,
  UN_FECHAFINAL   IN  DATE,
  UN_PORGASTO     IN  PCK_SUBTIPOS.TI_PORCENTAJE     DEFAULT 0,
  UN_SALARIO      IN  PCK_SUBTIPOS.TI_DOBLE          DEFAULT 0,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO,
  UN_PERIODO      IN PCK_SUBTIPOS.TI_PERIODO_NOMI
  )
  RETURN NUMBER AS 

MI_MES     NUMBER(2,0);
MI_ANO     NUMBER(4,0);
MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
MI_DIFERIDO PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_RTA     PCK_SUBTIPOS.TI_LONG;

BEGIN
MI_MES := TO_NUMBER(TO_CHAR(UN_FECHAINICIO, 'MM'));
MI_ANO := TO_NUMBER(TO_CHAR(UN_FECHAINICIO, 'YYYY'));
MI_DIFERIDO:= PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(UN_FECHAIN  => UN_FECHAINICIO,
                                                UN_FECHAFIN => UN_FECHAFINAL
                                                );

MI_CONDICION :='  NOVEDADES.COMPANIA = '||UN_COMPANIA||'
		  AND NOVEDADES.ID_DE_EMPLEADO = '||UN_ID_EMPLEADO||'
		  AND NOVEDADES.PERIODO = '||UN_PERIODO||'
		  AND NOVEDADES.ID_DE_PROCESO = '||UN_PROCESO||'
		  AND NOVEDADES.ID_DE_CONCEPTO IN (10,11,15)
		  AND NOVEDADES.MES >= '||MI_MES||'
		  AND NOVEDADES.ANO >= '||MI_ANO;


    BEGIN
        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'NOVEDADES'
                                            ,UN_ACCION  => 'E'
                                            ,UN_CONDICION => MI_CONDICION);                                                           

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                 

    END;      
    MI_RTA:=PCK_NOMINA_COM2.FC_DIFERIR_ENC(
                                     UN_COMPANIA     => UN_COMPANIA,
                                     UN_PROCESO      => UN_PROCESO,
                                     UN_ID_EMPLEADO  => UN_ID_EMPLEADO,
                                     UN_DIFERIDO     => MI_DIFERIDO,
                                     UN_OPCION       => UN_OPCION,
                                     UN_FECHAINICIO  => UN_FECHAINICIO,
                                     UN_PORGASTO     => UN_PORGASTO,
                                     UN_SALARIO      => UN_SALARIO,
                                     UN_USUARIO      => UN_USUARIO
                                     );


RETURN MI_RTA; 

END FC_DIFERIR_ENC_MOD;

PROCEDURE PR_BORARNOVEDAD359
/*
    NAME              : PR_BORARNOVEDAD359 
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : GUSTAVO PORTILLA SARRIA
    DATE 		      : 31/05/2023
    TIME              : 09:08 AM
    SOURCE MODULE     : NominaP2015.04.01DUI.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : ELIMINA LOS REGISTROS DE NOVEDADES CORRESPONDIENTES AL CONCEPTO 359, QUE NO SEAN ELIMINADAS POR LA FUNCION FC_DIFERIR
    @Name:                     
  */
  (
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_PROCESO		IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIO       IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES	      IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO		IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_IDEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_IDCONCEPTO IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
    UN_FECHAFIN   IN DATE,
    UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS

  MI_FECHAFIN DATE;

  MI_PERIODOINI NUMBER;
  MI_PERIODOFIN NUMBER;
	
	CURSOR lcu_novedades(
		MI_PERINI NUMBER,
		MI_PERFIN NUMBER
	) IS
	SELECT *
	FROM NOVEDADES
	WHERE COMPANIA      = UN_COMPANIA
       AND ID_DE_PROCESO =UN_PROCESO 
       AND TO_NUMBER(CONCAT(ANO, LPAD(MES, 2, '0'))) >= MI_PERINI
       AND TO_NUMBER(CONCAT(ANO, LPAD(MES, 2, '0'))) <= MI_PERFIN
       AND PERIODO       = UN_PERIODO
       AND ID_DE_EMPLEADO = UN_IDEMPLEADO
       AND ID_DE_CONCEPTO = UN_IDCONCEPTO ;            
BEGIN

	IF UN_FECHAFIN IS NOT NULL THEN

		MI_PERIODOINI := TO_NUMBER(CONCAT(UN_ANIO, LPAD(UN_MES, 2, '0')));

		MI_FECHAFIN := ADD_MONTHS(UN_FECHAFIN, 1);	
		MI_PERIODOFIN := TO_NUMBER(TO_CHAR(MI_FECHAFIN, 'YYYYMM'));			

		FOR indice IN lcu_novedades(MI_PERIODOINI, MI_PERIODOFIN) LOOP

			PCK_NOMINA.PR_BORRARNOVEDADX(
	                   UN_COMPANIA   => indice.COMPANIA
	                  ,UN_PROCESO    => indice.ID_DE_PROCESO
	                  ,UN_ANIO       => indice.ANO
	                  ,UN_MES        => indice.MES
	                  ,UN_PERIODO    => indice.PERIODO
	                  ,UN_IDEMPLEADO => indice.ID_DE_EMPLEADO
	                  ,UN_IDCONCEPTO => indice.ID_DE_CONCEPTO
	                  ,UN_CANTIDAD   => indice.VALOR
	                  ,UN_USUARIO    => UN_USUARIO);

		END LOOP;
	END IF;		

END PR_BORARNOVEDAD359;

PROCEDURE PR_DUPLICARHE
/*
    NAME              : PR_DUPLICAHE
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : EDWIN FERNANDO CABRERA MARTINEZ
    DATE MIGRADOR     : 4/02/2024
    TIME              : 12:15 PM
    SOURCE MODULE     :
    MODIFIER          : \ 
    DATE MODIFIED     : \ 
    TIME              : 
    DESCRIPTION       : PROCEDIMIENTO QUE DUPLICA CONFIGURACION HORAS EXTRAS DE UN AÑO A OTRO
    @NAME             : PR_DUPLICAHE
    @METHOD           : POST
    TICKET            : 7741215
  */
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO_NUEVO   IN PCK_SUBTIPOS.TI_ANIO,
    UN_ANO         IN PCK_SUBTIPOS.TI_ANIO,    
    UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_TABLA        PCK_SUBTIPOS.TI_TABLA := 'HORAS_EXTRAS';
    MI_CONT         PCK_SUBTIPOS.TI_ENTERO := 0;
BEGIN
  SELECT DISTINCT 1
    INTO MI_CONT
    FROM ANO 
   WHERE NUMERO = UN_ANO_NUEVO;
     
     DELETE FROM HORAS_EXTRAS WHERE ANO = UN_ANO_NUEVO;
     
     INSERT INTO HORAS_EXTRAS   (
                                CODIGO, 
                                ANO, 
                                CONCEPTO, 
                                PORCENTAJE, 
                                CONCEPTO_RELACIONADO, 
                                FORMULA, 
                                CREATED_BY, 
                                DATE_CREATED
                                ) 
                                (
                                SELECT  CODIGO,
                                        UN_ANO_NUEVO ANO,
                                        CONCEPTO,
                                        PORCENTAJE,
                                        CONCEPTO_RELACIONADO,
                                        FORMULA,
                                        UN_USUARIO CREATED_BY,
                                        SYSDATE DATE_CREATED
                                FROM    HORAS_EXTRAS
                                WHERE   ANO = UN_ANO
                                );
     
EXCEPTION 
    WHEN NO_DATA_FOUND THEN
        PCK_ERR_MSG.RAISE_WITH_MSG (
                                 UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_ANO_NO_CONFIGURADO
                                ,UN_TABLAERROR => MI_TABLA
                                ); 
    WHEN OTHERS THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE
                     ,UN_ERROR_COD  => PCK_ERRORES.ERR_ACT_HORASEXTRAS
                     ,UN_TABLAERROR => MI_TABLA); 
END PR_DUPLICARHE;

--CC_474 MROSERO
PROCEDURE PR_INCLUIR_LICENCIAS
/*
    NAME              : PR_INCLUIR_LICENCIAS
    AUTHORS           : MROSERO
    AUTHOR MIGRACION  :
    DATE MIGRADOR     : 20/01/2025
    TIME              : 03:06 PM
    SOURCE MODULE     :
    MODIFIER          : JM 08/08/2025 PERIODOS QUINCENALES Y DIFERIDOS CC 2225, CC 2203 y CC 3048
    DATE MODIFIED     : \
    TIME              :
    DESCRIPTION       : FUNCION QUE INSERTA LAS NOVEDADES EN LA TABLA, CON BASE A LAS LICENCIAS DE ANO MES Y PERIODO QUE IGRESAN POR PARAMETRO
                        SE AJUSTA AL ESTANDAR.
    --NAME             : getIncluirLicencias
    --METHOD           : get
  */
  (
    UN_MES         IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO     IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_ANO         IN PCK_SUBTIPOS.TI_ANIO,
    UN_PROCESO     IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIO IN DATE,
    UN_FECHAFIN    IN DATE,
    UN_USER        IN PCK_SUBTIPOS.TI_USUARIO,
    UN_ID_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO DEFAULT 0
  )
  AS
      MI_DIASLICENCIA    PCK_SUBTIPOS.TI_ENTERO       :=0;
      MI_CONCEPTO        PCK_SUBTIPOS.TI_ID_DE_CONCEPTO;
      MI_SUBCONDICION    PCK_SUBTIPOS.TI_CONDICION;
      MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
      MI_FECHAFIN        DATE;
      MI_FECHAINI        DATE;
      MI_INCAPACIDAD     PCK_SUBTIPOS.TI_ENTERO       :=1;
      MI_DIFERIDO       NUMBER;
      MI_INICIOP         DATE;
      MI_FINALP          DATE;
      MI_HABILES        BOOLEAN DEFAULT FALSE; --JM 05/03/2026 CC 3444
  BEGIN
  
  MI_HABILES := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(
                    UN_COMPANIA  => UN_COMPANIA
                   ,UN_NOMBRE    => 'LIQUIDAR VACACIONES Y LR EN BASE A DIAS HABILES'
                   ,UN_MODULO    => PCK_DATOS.FC_MODULONOMINA
                   ,UN_FECHA_PAR => SYSDATE),'NO') = 'SI' THEN TRUE ELSE FALSE END; -- JM CC 3444 

    BEGIN 
        SELECT FECHAFINAL,FECHAINICIO,DIFERIDOS INTO MI_FINALP,MI_INICIOP,MI_DIFERIDO
        FROM PERIODOS
        WHERE COMPANIA = UN_COMPANIA
        AND ID_DE_PROCESO = UN_PROCESO
        AND ANO = UN_ANO
        AND MES = UN_MES
        AND PERIODO = UN_PERIODO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_DIFERIDO :=  -1;
        MI_FINALP := LAST_DAY(UN_FECHAFIN);
        MI_INICIOP := TO_DATE('01/' || TO_CHAR(UN_FECHAFIN,'MM/YYYY'),'DD/MM/YYYY');
    END; 
    
      IF UN_ID_EMPLEADO <> 0 THEN
        MI_SUBCONDICION   := 'AND ID_DE_EMPLEADO = '||UN_ID_EMPLEADO;
        MI_FECHAFIN := MI_FINALP;
        MI_FECHAINI := MI_INICIOP;
       ELSE
            MI_SUBCONDICION   := '';
            MI_FECHAFIN := MI_FINALP;
            MI_FECHAINI := MI_INICIOP;
        END IF;
     MI_CONDICION := 'COMPANIA       = '''|| UN_COMPANIA ||'''
                 AND ID_DE_PROCESO   = '  || UN_PROCESO ||'
                 AND ANO             = '  || UN_ANO ||'
                 AND MES             = '  || UN_MES ||'
                 AND PERIODO         = '  || UN_PERIODO ||'
                                       '  ||MI_SUBCONDICION||'
                 AND ID_DE_CONCEPTO IN (SELECT ID_DE_CONCEPTO FROM TIPOS_LICENCIA WHERE COMPANIA =  '''|| UN_COMPANIA ||''') ';
        BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'NOVEDADES',
                                       UN_ACCION    => 'E',
                                       UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;
    BEGIN
    --INTO MI_DIASLICENCIA, MI_CONCEPTO
    FOR RS IN( --MOD JM CC 1675 23-05-2025 estaba difiriendo mal cuando habian 2 ó mas licencias en el mismo mes (del mismo tipo) --mod jm cc 3787 feberero sume 1 o 2 
          SELECT SUM(
        CASE 
            WHEN TO_CHAR(FF, 'DDMM') = '2802' THEN PCK_SYSMAN_UTL.FC_EDAD(FI, FF) + 2
            WHEN TO_CHAR(FF, 'DDMM') = '2902' THEN PCK_SYSMAN_UTL.FC_EDAD(FI, FF) + 1
            ELSE PCK_SYSMAN_UTL.FC_EDAD(FI, FF)
        END
    ) AS DIASLICENCIA,  SUM(PCK_SYSMAN_UTL.FC_DIASHABIL(UN_COMPANIA,FI,FF)) AS DIASLICENCIAHABIL, CONCEPTO, ID_DE_EMPLEADO FROM (SELECT GREATEST(LICENCIAS.FECHA_INICIO, MI_FECHAINI) AS  FI,
                   LEAST(LICENCIAS.FECHA_FINAL, MI_FECHAFIN) AS FF,
                   TIPOS_LICENCIA.ID_DE_CONCEPTO AS CONCEPTO,
                   ID_DE_EMPLEADO
           FROM LICENCIAS
            INNER JOIN TIPOS_LICENCIA ON
            LICENCIAS.COMPANIA = TIPOS_LICENCIA.COMPANIA
            AND LICENCIAS.LICENCIA = TIPOS_LICENCIA.LICENCIA
            WHERE LICENCIAS.COMPANIA       = UN_COMPANIA
              --AND ID_DE_EMPLEADO = UN_ID_EMPLEADO --MOD JM CC 3048
              AND (
                    (UN_ID_EMPLEADO <> 0 AND LICENCIAS.ID_DE_EMPLEADO = UN_ID_EMPLEADO) OR  (UN_ID_EMPLEADO = 0 AND LICENCIAS.ID_DE_EMPLEADO <> 0)
              )
             AND ( TO_DATE(TO_CHAR(FECHA_FINAL, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_FECHAINI AND MI_FECHAFIN
             OR (TO_DATE(TO_CHAR(FECHA_INICIO, 'DD/MM/YYYY'),'DD/MM/YYYY') BETWEEN MI_FECHAINI AND MI_FECHAFIN
             AND TO_NUMBER(TO_CHAR(FECHA_INICIO ,'DD')) <> 31)
           OR (TO_DATE(TO_CHAR(FECHA_INICIO, 'DD/MM/YYYY'),'DD/MM/YYYY') <= MI_FECHAINI
             AND TO_DATE(TO_CHAR(FECHA_FINAL, 'DD/MM/YYYY'),'DD/MM/YYYY') >= MI_FECHAFIN
             AND TO_NUMBER(TO_CHAR(FECHA_INICIO ,'DD')) <> 31  ) )
           AND TO_DATE(TO_CHAR(FECHA_INICIO, 'DD/MM/YYYY'),'DD/MM/YYYY') <= MI_FECHAFIN
             GROUP BY FECHA_INICIO, FECHA_FINAL, TIPOS_LICENCIA.ID_DE_CONCEPTO,ID_DE_EMPLEADO) group by CONCEPTO,ID_DE_EMPLEADO
             --ID_DE_CONCEPTO  IN (339,356,357,359,363,364,365,390);
             )
  LOOP
   IF (RS.DIASLICENCIA >0 OR (MI_HABILES AND RS.DIASLICENCIAHABIL > 0))AND RS.CONCEPTO <> 0 AND MI_DIFERIDO <> 0 THEN --MOD JM CC 3444
            PCK_NOMINA.PR_INCLUIRNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => UN_ANO
                  ,UN_MES        => UN_MES
                  ,UN_PERIODO    => UN_PERIODO
                  ,UN_IDEMPLEADO => RS.ID_DE_EMPLEADO --MOD JM CC 3048
                  ,UN_IDCONCEPTO => RS.CONCEPTO
                  ,UN_VALOR      => CASE WHEN MI_HABILES AND RS.CONCEPTO IN (363,364,365,390) THEN  RS.DIASLICENCIAHABIL ELSE RS.DIASLICENCIA END --MOD JM CC 3444
                  ,UN_ACCION     => NULL);
     END IF;
  END LOOP;
 END;
END PR_INCLUIR_LICENCIAS;

PROCEDURE PR_LIQPRICESSENA
/*
  NAME              : PR_LIQPRIVAC
  AUTHORS           : SYSMAN  SAS
  AUTHOR            : 
  DATE              : 
  TIME              : 
  SOURCE MODULE     : -
  MODIFIER          : -
  DATE MODIFIED     : -
  TIME              : -
  DESCRIPTION       :PROCEDIMIENTO LIQUIDACION DE CESANTIAS EMPLEADOS TIPO SENA
                        TICKET: 7720876
  --NAME:  PR_LIQPRICES
*/
AS
    MI_ALIMRET              PCK_SUBTIPOS.TI_ENTERO :=0;
    MI_RECARGOSUELDO        PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_MASDIASOTRAENTIDAD   PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_FIPROMC              DATE;
    MI_DTP                  PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_DIAS                 PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_ANTICIPOS            PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_ULTQUINQUENIO        PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_DP                   PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_DIASINT              PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_PRV                  PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_DPPJ                 PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_DPPN                 PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_PRJ                  PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_PRN                  PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_ULTQ                 PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_OTT                  PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_PROMFAC      PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_CESANTIA1      PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_SBM          PCK_SUBTIPOS.TI_DOBLE :=0;
BEGIN

    --CC_3314(08/05/2026 JCROJAS): Se agrega validacion para que calcule cesantias al tipo 05 en el mes de diciembre, periodo 8
    IF ( PCK_NOMINA.FC_CN(404) != 0 OR (PCK_NOMINA.GL_SMES = '12' AND PCK_NOMINA.GL_SPER = 8)) THEN
        PCK_NOMINA.GL_BASCES := 0;
        MI_RECARGOSUELDO := 0;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,1991, 1, 1, 1991, 1, 1, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        MI_MASDIASOTRAENTIDAD := PCK_NOMINA.FC_CNA(299);
        --LVEGA (12/08/2025)
        IF PCK_NOMINA.GL_FECHAIR < TO_DATE('25/06/2025', 'DD/MM/YYYY') THEN
           PCK_NOMINA.GL_FECHAIR := TO_DATE('25/06/2025', 'DD/MM/YYYY');
        END IF;
        IF PCK_NOMINA.GL_SPRC = 99 THEN
            MI_FIPROMC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAIR < PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN1), PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN1), PCK_NOMINA.GL_FECHAIR) ;
        ELSE
            MI_FIPROMC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAIR < PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN1), PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN1), PCK_NOMINA.GL_FECHAIR) + 1;
        END IF;
        IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHACS, PCK_NOMINA.GL_FECHAFIN1) > 90 THEN
            MI_SBM := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(900) = 0, PCK_NOMINA.FC_CN(1), PCK_NOMINA.FC_CN(900));
        ELSE
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
            MI_DTP := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAI < MI_FIPROMC, 360, PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAI, PCK_NOMINA.GL_FECHAFIN1));
            MI_SBM := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(900) = 0, (PCK_NOMINA.FC_CNA(2) + PCK_NOMINA.FC_CNA(16) + PCK_NOMINA.FC_CNA(28) + PCK_NOMINA.FC_CNA(29) + PCK_NOMINA.FC_CNA(174) + PCK_NOMINA.FC_SUMACONA(370, 378) + PCK_NOMINA.FC_CNA(507) + PCK_NOMINA.FC_CNA(508) + PCK_NOMINA.FC_CNA(511) + PCK_NOMINA.FC_CN(2) + PCK_NOMINA.FC_CN(174) + PCK_NOMINA.FC_SUMACON(370, 378) + PCK_NOMINA.FC_CN(507) + PCK_NOMINA.FC_CN(508) + PCK_NOMINA.FC_CN(511)) / MI_DTP * 30, PCK_NOMINA.FC_CN(900))      ;
            MI_SBM := PCK_SYSMAN_UTL.FC_ROUND(MI_SBM, 0);
        END IF;
        IF PCK_NOMINA.GL_FECHAINI < TO_DATE('2000/06/01','YYYY/MM/DD') THEN
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, 1999, 5, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO)   ;
            MI_RECARGOSUELDO := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(56)) / 135 * 100, 0);
        END IF;
        MI_ULTQUINQUENIO := PCK_NOMINA.FC_VALORULTIMOQUINQUENIO(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF PCK_NOMINA.GL_FECHAI < TO_DATE('1996/11/21','YYYY/MM/DD') AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).REGIMEN = 1 THEN
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,(PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR)), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR), PCK_SYSMAN_UTL.FC_IIF(PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIR) < 16, 1, 2), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
            PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CESANTIA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 'L');
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
            MI_DIAS := MI_DIAS + MI_MASDIASOTRAENTIDAD;
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1);
            MI_DP := 360;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
        ELSE
            PCK_NOMINA.GL_FECHAIC := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY'), PCK_NOMINA.GL_FECHAIR, TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY'));
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC), PCK_SYSMAN_UTL.FC_IIF(PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIC) < 16, 1, 2), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
            PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359);
            MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS ;
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1);
            MI_DP := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FIPROMC, PCK_NOMINA.GL_FECHAFIN1);
        END IF;
        MI_DIASINT := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.GL_FECHAIR > TO_DATE(PCK_NOMINA.GL_SANO || '/01/01','YYYY/MM/DD'), PCK_NOMINA.GL_FECHAIR, TO_DATE(PCK_NOMINA.GL_SANO || '/01/01','YYYY/MM/DD')), PCK_NOMINA.GL_FECHAFIN1);
        MI_PRV := PCK_SYSMAN_UTL.FC_IIF(PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) >= 360, PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(1) / 30 * PCK_NOMINA.FC_CN(68), 0), 0);
        IF PCK_NOMINA.GL_FECHAFIN <= TO_DATE('31/05/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
            MI_DPPJ := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FIPROMC, TO_DATE('31/05/' || (PCK_NOMINA.GL_SANO - 1),'DD/MM/YYYY'));
        ELSE
            MI_DPPJ := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FIPROMC, TO_DATE('31/05/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY'));
        END IF;
        IF PCK_NOMINA.GL_FECHAFIN <= TO_DATE('30/11/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
            MI_DPPN := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FIPROMC, TO_DATE('31/12/' || (PCK_NOMINA.GL_SANO - 1),'DD/MM/YYYY'));
        ELSE
            MI_DPPN := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FIPROMC, TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY'));
        END IF;
        IF PCK_NOMINA.GL_SPRC = 99 THEN
            MI_PRJ := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(404) <> 0, PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160)), 0), PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160)), 0));
            MI_PRN := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(404) <> 0, PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(158), 0), PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(158), 0));
        ELSE
            MI_PRJ := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(404) <> 0, PCK_NOMINA.FC_CN(160) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160)) / 360 * MI_DPPJ, 0), PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(160)) / 360 * MI_DPPJ, 0));
            MI_PRN := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(404) <> 0, PCK_NOMINA.FC_CN(158) + PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(158) / 360 * MI_DPPN, 0), PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CNA(158) / 360 * MI_DPPN, 0));
        END IF;
        IF PCK_NOMINA.GL_SPRC = 99 THEN
            MI_ULTQ := PCK_NOMINA.FC_SUMACONA(180, 185);
        ELSE
            MI_ULTQ := PCK_NOMINA.FC_SUMACON(180, 185) ;
        END IF;
        MI_OTT := PCK_NOMINA.FC_CNA(95);
        IF PCK_NOMINA.FC_CN(904) = 0 THEN
            MI_ALIMRET := 0;
        ELSE
            MI_ALIMRET := 1;
        END IF;
        PCK_NOMINA.CN(901) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(901) = 0, (PCK_NOMINA.FC_CNA(56)) - MI_RECARGOSUELDO + PCK_NOMINA.FC_CN(56), PCK_NOMINA.FC_CN(901));
        PCK_NOMINA.CN(902) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(902) = 0, (PCK_NOMINA.FC_SUMACONA(47, 60)) - (PCK_NOMINA.FC_CNA(56)) + PCK_NOMINA.FC_SUMACON(47, 60) - PCK_NOMINA.FC_CN(56) - PCK_NOMINA.FC_CNA(126) - PCK_NOMINA.FC_CN(126), PCK_NOMINA.FC_CN(902));
        PCK_NOMINA.CN(903) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(903) = 0, PCK_NOMINA_PROC01.FC_ACPROPEXACTA(MI_FIPROMC, PCK_NOMINA.GL_FECHAFIN1, '080', '036', 'C', PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, PCK_NOMINA.GL_FECHAINI1, PCK_NOMINA.GL_FECHAFIN1, 'Q'), PCK_NOMINA.FC_CN(903));
        PCK_NOMINA.CN(903) := PCK_NOMINA.FC_CN(903) + MI_OTT;
        PCK_NOMINA.CN(904) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(904) = 0, PCK_NOMINA_PROC01.FC_ACPROPEXACTA(MI_FIPROMC, PCK_NOMINA.GL_FECHAFIN1, '079', '041', 'H', PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO, PCK_NOMINA.GL_FECHAINI1, PCK_NOMINA.GL_FECHAFIN1, 'Q'), PCK_NOMINA.FC_CN(904));
        IF PCK_NOMINA.GL_FECHAIC > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC), PCK_SYSMAN_UTL.FC_IIF(PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIC) < 16, 1, 2), PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
        ELSE
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_ANOA, PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) ;
        END IF;
        IF PCK_NOMINA.GL_SPRC = 99 THEN
            PCK_NOMINA.CN(904) := PCK_SYSMAN_UTL.FC_IIF(MI_ALIMRET = 1, PCK_NOMINA.FC_CN(904), PCK_NOMINA.FC_CNA(79));
            MI_ALIMRET := PCK_SYSMAN_UTL.FC_IIF(MI_ALIMRET = 1, 0, MI_ALIMRET);
        END IF;
        PCK_NOMINA.CN(904) := 0; --PCK_NOMINA.FC_CN(904) + MI_ALIMRET;
        PCK_NOMINA.CN(905) := 0; --PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(905) = 0, MI_PRN, PCK_NOMINA.FC_CN(905));
        PCK_NOMINA.CN(906) := 0; --PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(906) = 0, MI_PRJ, PCK_NOMINA.FC_CN(906));
        PCK_NOMINA.CN(907) := 0; --PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(907) = 0, MI_PRV, PCK_NOMINA.FC_CN(907));
        MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.FC_CNA(153) + PCK_NOMINA.FC_CNA(500) + PCK_NOMINA.FC_CNA(175) + PCK_NOMINA.FC_CN(153) + PCK_NOMINA.FC_CN(500) + PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(404) <> 0, 0, PCK_NOMINA.FC_CN(175)) + (MI_ULTQ / 5) + PCK_NOMINA.FC_CN(900) + PCK_NOMINA.FC_CN(901) + PCK_NOMINA.FC_CN(902) + PCK_NOMINA.FC_CN(903) + PCK_NOMINA.FC_CN(904) + PCK_NOMINA.FC_CN(905) + PCK_NOMINA.FC_CN(906) + PCK_NOMINA.FC_CN(907)), 0);
        IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHACS, PCK_NOMINA.GL_FECHAFIN1) <= 90 AND PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) < 360 THEN
            --MI_CESANTIA1 := (MI_SBM + PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC) * 30 / MI_DIAS, 0)) - MI_ANTICIPOS;
            MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC) * 30 / MI_DIAS, 0);
        ELSE
            IF PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIR, PCK_NOMINA.GL_FECHAFIN1) < 360 THEN
                --MI_CESANTIA1 := (MI_SBM + PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC) * 30 / MI_DIAS, 0)) - MI_ANTICIPOS;
                MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND((MI_PROMFAC) * 30 / MI_DIAS, 0);
            ELSE
                --MI_CESANTIA1 := PCK_SYSMAN_UTL.FC_ROUND((MI_SBM + MI_PROMFAC / 12) * MI_DIAS / 360, 0) - MI_ANTICIPOS;
                MI_PROMFAC := PCK_SYSMAN_UTL.FC_ROUND(MI_PROMFAC / 12, 0);
            END IF;
        END IF;
        MI_CESANTIA1    := PCK_SYSMAN_UTL.FC_ROUND(
                                                  PCK_SYSMAN_UTL.FC_ROUND( CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_AUXT , 0) / 360 * MI_DIAS
                                                  , 0
                                                  );
		--CC_3314(08/05/2026 JCROJAS): Se agrega validacion ya que las cesantias para diciembre, periodo 8 se deben guardar en los conceptos 269 y 277                                         
        IF PCK_NOMINA.GL_SMES = '12' AND PCK_NOMINA.GL_SPER = 8 THEN  
            PCK_NOMINA.CN(269) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(169) = 0, PCK_SYSMAN_UTL.FC_IIF(MI_CESANTIA1 < 0, 0, PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0)), PCK_NOMINA.FC_CN(169));
            PCK_NOMINA.CN(277) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(177) = 0, MI_CESANTIA1, PCK_NOMINA.FC_CN(177));
        ELSE	
			PCK_NOMINA.CN(169) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(169) = 0, PCK_SYSMAN_UTL.FC_IIF(MI_CESANTIA1 < 0, 0, PCK_SYSMAN_UTL.FC_ROUND(MI_CESANTIA1 * 12 / 100 * MI_DIASINT / 360, 0)), PCK_NOMINA.FC_CN(169));
			PCK_NOMINA.CN(177) := PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(177) = 0, MI_CESANTIA1, PCK_NOMINA.FC_CN(177));
		END IF;	   
		IF PCK_NOMINA.FC_CN(425) <> 0 AND PCK_NOMINA.GL_SMES = 12 THEN
			PCK_NOMINA.PR_INCLUIRNOVEDAD(PCK_NOMINA.GL_COMPANIA, 1, PCK_NOMINA.GL_SANO + 1, 1,CASE WHEN PCK_PARST.FC_PAR('NOMINA MENSUAL','SI') = 'SI' THEN 3 ELSE 2 END, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, 169, PCK_NOMINA.FC_CN(269),NULL,PCK_CONEXION.FC_GETUSER);
		END IF;
        PCK_NOMINA.CN(900) := MI_SBM                                                     ;
        PCK_NOMINA.CN(908) := PCK_NOMINA.FC_CNA(153) + PCK_NOMINA.FC_CN(153) + PCK_NOMINA.FC_CNA(500) + PCK_NOMINA.FC_CN(500)                                    ;
        PCK_NOMINA.CN(909) := PCK_SYSMAN_UTL.FC_ROUND(MI_ULTQ / 5, 0)                                               ;
        PCK_NOMINA.CN(910) := MI_DIAS                                                    ;
        PCK_NOMINA.CN(911) := MI_ANTICIPOS                                               ;
        PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS + PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DIASINTERRUPCION                                               ;
        PCK_NOMINA.CN(913) := MI_SBM + MI_PROMFAC                                           ;
        PCK_NOMINA.CN(914) := PCK_NOMINA.FC_CNA(175) + PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA.FC_CN(404) <> 0, 0, PCK_NOMINA.FC_CN(175));
    END IF;
END PR_LIQPRICESSENA;

PROCEDURE PR_LIQPRIVACSENA
/*
  NAME              : PR_LIQPRIVAC
  AUTHORS           : SYSMAN  SAS
  AUTHOR            : 
  DATE              : 
  TIME              : 
  SOURCE MODULE     : -
  MODIFIER          : -
  DATE MODIFIED     : -
  TIME              : -
  DESCRIPTION       :PROCEDIMIENTO LIQUIDACION DE VACACIONES EMPLEADOS TIPO SENA
                     TICKET: 7720876
  --NAME:  PR_LIQPRIVAC
*/
AS
    MI_BONPAGADA     PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_FECHA         DATE;
    MI_VALOR         NUMBER DEFAULT 0;
    MI_VALOR_F       VARCHAR2(200 CHAR);
BEGIN

        --DC := 0;
        PCK_NOMINA.GL_DIASVAC := 0;
        PCK_NOMINA.GL_DIASPENDIENTES := 0;
        PCK_NOMINA.GL_PENDIENTES := 0;
        PCK_NOMINA.GL_LICENCIAS := 0;
        PCK_NOMINA.GL_PRIMAPROPORCIONAL := 0;
        PCK_NOMINA.GL_DINEROPROPORCIONAL := 0;
        
         IF PCK_NOMINA.GL_FECHAINI < TO_DATE('25/06/2025', 'DD/MM/YYYY') THEN
           PCK_NOMINA.GL_FECHAINI := TO_DATE('25/06/2025', 'DD/MM/YYYY');
        END IF;
        --LVEGA
        PCK_NOMINA.GL_FECHAUV := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL IS NULL THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_FINAL END;
        IF PCK_NOMINA.GL_FECHAUV < TO_DATE('25/06/2025', 'DD/MM/YYYY') THEN
           PCK_NOMINA.GL_FECHAUV := TO_DATE('25/06/2025', 'DD/MM/YYYY');
        END IF;
        IF PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.GL_SPER = 8 THEN
            PCK_NOMINA.CN(984) := 0;
            PCK_NOMINA.CN(981) := 0;
            
           
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
            --PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91) - PCK_NOMINA.FC_CNA(99);
            PCK_NOMINA.GL_FECHAFIN1 := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 THEN  PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO  ELSE PCK_NOMINA.GL_FECHAFIN1 END;
            PCK_NOMINA.GL_DTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAUV, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
            --(APINEDA:31/07/2019)-Se estaba restando dos veces la licencia
            PCK_NOMINA.GL_DIASPROP := PCK_NOMINA.GL_DTV;
            PCK_NOMINA.GL_PERIODOS := 1;
            PCK_NOMINA.GL_DIASVAC := PCK_SYSMAN_UTL.FC_ROUND(((15 * PCK_NOMINA.GL_DTV) / 360) + 0.0049, 2);
            PCK_NOMINA.CN(93) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(68) * CASE WHEN PCK_NOMINA.GL_PERIODOS = 0 THEN 1 ELSE PCK_NOMINA.GL_PERIODOS END, 2);
        
            PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END, 0);

           IF PCK_NOMINA.FC_CN(175) = 0 /*AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO = '06'*/ THEN
                PCK_NOMINA.CN(96) := PCK_NOMINA.GL_DIASVAC;
                PCK_NOMINA.CN(175) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA.GL_FACTORESPV *  PCK_NOMINA.FC_CN(96)) / 30, 0);
                MI_VALOR := PCK_NOMINA.CN(175);               
                PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) < 0 THEN 0 ELSE PCK_NOMINA.FC_CN(175) END;
           END IF;
           
           
        
        ELSE
            PCK_NOMINA.CN(981) := 0;
            PCK_NOMINA.CN(984) := 0;
            PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAUV), PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAUV), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            PCK_NOMINA.GL_DIASPENDIENTES := PCK_NOMINA.FC_CNA(91);
            PCK_NOMINA.CN(93) := PCK_NOMINA.FC_CN(68) * PCK_NOMINA.FC_CN(164) ;
            MI_BONPAGADA := 0;
   
            PCK_NOMINA.GL_FACTORESPV := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END, 0);
            
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
            ELSE
                IF PCK_NOMINA.FC_CN(403) <> 0 THEN
                    PCK_NOMINA.CN(174) := CASE WHEN PCK_NOMINA.FC_CN(174) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(94), 0) ELSE PCK_NOMINA.FC_CN(174) END;
                END IF;
                IF PCK_NOMINA.FC_CN(419) <> 0 THEN
                    PCK_NOMINA.CN(175) := CASE WHEN PCK_NOMINA.FC_CN(175) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORESPV / 30 * PCK_NOMINA.FC_CN(96), 0) ELSE PCK_NOMINA.FC_CN(175) END;
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
END PR_LIQPRIVACSENA;

FUNCTION FC_INCAPULTIMOANO(
/*
    NAME              : FC_INCAPULTIMOANO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO
    DATE MIGRADOR     : 22/10/2025
*/
	UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_IDEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_FECHA1     IN DATE
)
RETURN PCK_SUBTIPOS.TI_DOBLE
AS
    MI_VALOR	  PCK_SUBTIPOS.TI_DOBLE := 0;
    MI_IDEMPLEADO PCK_SUBTIPOS.TI_ID_DE_EMPLEADO;
BEGIN
    BEGIN
        SELECT ID_DE_EMPLEADO,SUM(DIAS) DIAS
        INTO MI_IDEMPLEADO,MI_VALOR
        FROM INCAPACIDADES
        WHERE COMPANIA = UN_COMPANIA
            AND INCAPACIDAD <> '04'
            AND TO_DATE(TO_CHAR(FECHA_FIN,'DD/MM/YYYY'),'DD/MM/YYYY') >= TO_DATE(TO_CHAR(UN_FECHA1,'DD/MM/YYYY'),'DD/MM/YYYY')
            AND ID_DE_EMPLEADO = UN_IDEMPLEADO
        GROUP BY ID_DE_EMPLEADO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_IDEMPLEADO := 0;
        MI_VALOR := 0;
    END;
    RETURN MI_VALOR;
END FC_INCAPULTIMOANO;

PROCEDURE PR_CALCPRIMASEMESTRALUES(
/*
    NAME              : PR_CALCPRIMASEMESTRALUES
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO
    DATE MIGRADOR     : 21/10/2025
*/
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA
)
AS
    MI_DOCEAVASMINIMASPS   PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
    MI_PRIMAJUNIO          PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIASPACTADOS        PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_INDRETIR            PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_DIASINCAPANOATRAS   PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
BEGIN
    MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS','1'));
    PCK_NOMINA.GL_FACTORPS := 0;
    PCK_NOMINA.GL_FACTORPS1 := 0;
    PCK_NOMINA.GL_DNT := 0;
    PCK_NOMINA.GL_DNT1 := 0;
    
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('16/07/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO-1),'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO-1),'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
    END IF;
    IF PCK_NOMINA.GL_SMES > 7 THEN
        PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPS1 := TO_DATE('01/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('16/07/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO),'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO),'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
    END IF;
    IF PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 4 THEN 
        PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN CASE WHEN PCK_NOMINA.GL_FECHAFIN1 > TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') ELSE PCK_NOMINA.GL_FECHAFIN1 END ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') END;
    ELSE
        PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') END;
        IF PCK_NOMINA.GL_SMES = 12 THEN
            PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('31/12/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') END;
        END IF;
    END IF;
    IF PCK_NOMINA.GL_SMES = 7 AND (UN_COMPANIA = '003' OR UN_COMPANIA = '001') AND PCK_NOMINA.FC_CN(306) <> 0 THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAFIN1 < TO_DATE('16/07/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO-1),'DD/MM/YYYY') ELSE TO_DATE('01/07/' || (PCK_NOMINA.GL_SANO-1),'DD/MM/YYYY') END;
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
        PCK_NOMINA.GL_FECHAFPS := CASE WHEN PCK_NOMINA.FC_CN(404) <> 0 OR PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO IS NOT NULL THEN PCK_NOMINA.GL_FECHAFIN1 ELSE TO_DATE('30/06/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') END;
    END IF;
    PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIPS),PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIPS),CASE WHEN PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAIPS) < 16 THEN 1 ELSE 2 END,PCK_NOMINA.GL_SANO,6,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
    PCK_NOMINA.GL_DNT := PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CN(356)+PCK_NOMINA.FC_CN(357)+PCK_NOMINA.FC_CN(359)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339);
    
    IF PCK_PARST.FC_PAR('SUMAR BASP A PRIMA SERVICIOS DECRETO 2351','NO') = 'SI' THEN
        IF (PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.GL_SPER = 4) AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO >= PCK_NOMINA.GL_FECHAINI AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_CUMPLIMIENTO_BONIFICACIO <= PCK_NOMINA.GL_FECHAFIN) THEN
            PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO))/12,0);
        ELSE
            PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO))/12,0);
        END IF;
        IF PCK_NOMINA.FC_CN(946) = 0 AND PCK_NOMINA.FC_CN(514) <> 0 THEN
            PCK_NOMINA.CN(946) := PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(514)/12,0);
        END IF;
    END IF;
    
    MI_DIASINCAPANOATRAS := PCK_NOMINA_COM2.FC_INCAPULTIMOANO(UN_COMPANIA,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO,PCK_NOMINA.FC_FECHAANOATRAS(PCK_NOMINA.GL_FECHAFIN));
    IF MI_DIASINCAPANOATRAS >= 180 THEN
		PCK_NOMINA.GL_AUXT := 0;
		PCK_NOMINA.GL_AUXA := 0;
    END IF;

    PCK_NOMINA.CN(945) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    PCK_NOMINA.GL_FACTORPS := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END +PCK_NOMINA.GL_AUXA+PCK_NOMINA.GL_AUXT+PCK_NOMINA.GL_VPT+PCK_NOMINA.GL_VPA+PCK_NOMINA.GL_GRPNGV+PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(946),0);
    PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS)-PCK_NOMINA.GL_DNT;
    IF PCK_PARST.FC_PAR('ELIMINAR TIEMPO MINIMO EN PRIMA DE SERVICIOS','NO') = 'SI' THEN
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS)-PCK_NOMINA.FC_CN(953);
        MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS','1'));
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS-PCK_NOMINA.FC_CN(953));
    ELSE
        IF PCK_NOMINA.GL_DCC = 179 OR PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA,TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY'),1) = PCK_NOMINA.GL_FECHAIPS THEN
            PCK_NOMINA.GL_DOCEAVAS := 6;
            PCK_NOMINA.GL_DCC := 180;
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS)-PCK_NOMINA.GL_DNT;
        ELSE
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS)/30;
        END IF;
    END IF;
    IF PCK_NOMINA.GL_SMES <= 7 THEN
        FOR i IN 7..12 LOOP
            PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,(PCK_NOMINA.GL_SANO-1),i,1,(PCK_NOMINA.GL_SANO-1),i,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF (PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339)) > 0 THEN
                PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS-1;
                PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953)+(PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339));
            END IF;
        END LOOP;
    END IF;
    FOR i IN 1..(CASE WHEN PCK_NOMINA.GL_SMES = 7 THEN PCK_NOMINA.GL_SMES-1 ELSE PCK_NOMINA.GL_SMES END) LOOP
        PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,(PCK_NOMINA.GL_SANO),i,1,(PCK_NOMINA.GL_SANO),i,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        IF (PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339)) > 0 THEN
            PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS-1;
            PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953)+(PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339));
        END IF;
    END LOOP;
    IF PCK_PARST.FC_PAR('ELIMINAR TIEMPO MINIMO EN PRIMA DE SERVICIOS','NO') = 'SI' THEN
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS)-PCK_NOMINA.FC_CN(953);
        MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS','1'));
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS-PCK_NOMINA.FC_CN(953));
    ELSE
        IF PCK_NOMINA.FC_CN(952) > 0 THEN
            PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
        END IF;
        IF PCK_NOMINA.GL_DOCEAVAS < 3 THEN 
            PCK_NOMINA.GL_DOCEAVAS := 0;
        END IF;
        IF PCK_NOMINA.GL_DCC < 1 THEN 
            PCK_NOMINA.GL_DCC := 0;
        END IF;
        IF PCK_NOMINA.FC_CN(952) > 0 THEN
            PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
            PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DCC/30;
        END IF;
    END IF;
    IF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('16/06/' || (PCK_NOMINA.GL_SANO-1),'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAIPS <= TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 THEN
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS',' ') = 'SI' THEN   
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS)-PCK_NOMINA.FC_CN(953);
            IF PCK_NOMINA.GL_SMES = 12 THEN
                IF PCK_NOMINA.GL_DCC < 180 THEN 
                    PCK_NOMINA.CN(159) := CASE WHEN PCK_NOMINA.FC_CN(159) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/360*PCK_NOMINA.GL_DCC/30*PCK_NOMINA.FC_CN(67)+0.049,0) ELSE PCK_NOMINA.FC_CN(159) END -PCK_NOMINA.FC_CNA(159);
                ELSE
                    PCK_NOMINA.CN(159) := CASE WHEN PCK_NOMINA.FC_CN(159) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/180*PCK_NOMINA.GL_DCC/30*PCK_NOMINA.FC_CN(67)+0.049,0) ELSE PCK_NOMINA.FC_CN(159) END -PCK_NOMINA.FC_CNA(159);    
                END IF;
            ELSE
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/360*PCK_NOMINA.GL_DCC/30*PCK_NOMINA.FC_CN(67),0) ELSE PCK_NOMINA.FC_CN(160) END -PCK_NOMINA.FC_CNA(160);
            END IF;
        ELSE
            IF PCK_NOMINA.GL_SMES = 12 THEN
                PCK_NOMINA.CN(159) :=  CASE WHEN PCK_NOMINA.FC_CN(159) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/6*PCK_NOMINA.GL_DOCEAVAS/30*PCK_NOMINA.FC_CN(67)+0.049,0) ELSE PCK_NOMINA.FC_CN(159) END -PCK_NOMINA.FC_CNA(159);
            ELSE
                PCK_NOMINA.CN(160) :=  CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/12*PCK_NOMINA.GL_DOCEAVAS/30*PCK_NOMINA.FC_CN(67),0) ELSE PCK_NOMINA.FC_CN(160) END -PCK_NOMINA.FC_CNA(160);    
            END IF;
        END IF;
    ELSIF PCK_NOMINA.GL_FECHAIPS >= TO_DATE('01/01/' || (PCK_NOMINA.GL_SANO),'DD/MM/YYYY') AND PCK_NOMINA.FC_CN(404) = 0 THEN
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS',' ') = 'SI' THEN  
            IF PCK_NOMINA.GL_SMES > 7 AND PCK_NOMINA.GL_FECHAI > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN
                PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
                PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
            END IF;
			IF PCK_NOMINA.GL_SMES = 12 THEN 
                PCK_NOMINA.GL_FECHAIPS := TO_DATE('01/07/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
                PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
            END IF;
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS)-PCK_NOMINA.FC_CN(953);
            IF PCK_NOMINA.GL_SMES = 12 THEN
				--CC_2392(15/12/2025 JCROJAS): Se ajusta calculo del concepto 159
                IF PCK_NOMINA.GL_DCC < 360 AND PCK_NOMINA.GL_DCC <= 180 AND PCK_NOMINA.GL_DCC > 0 THEN
                    PCK_NOMINA.CN(159) :=  CASE WHEN PCK_NOMINA.FC_CN(159) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/360*PCK_NOMINA.GL_DCC+0.049,0) ELSE PCK_NOMINA.FC_CN(159) END -PCK_NOMINA.FC_CNA(159);  
                ELSIF PCK_NOMINA.GL_DCC > 0 THEN
                    PCK_NOMINA.CN(159) :=  CASE WHEN PCK_NOMINA.FC_CN(159) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/180*PCK_NOMINA.GL_DCC+0.049,0) ELSE PCK_NOMINA.FC_CN(159) END -PCK_NOMINA.FC_CNA(159);  
                END IF;
            ELSE    
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/360*PCK_NOMINA.GL_DCC/30*PCK_NOMINA.FC_CN(67),0) ELSE PCK_NOMINA.FC_CN(160) END -PCK_NOMINA.FC_CNA(160);        
            END IF;
            IF PCK_NOMINA.GL_FECHAIPS > PCK_NOMINA.GL_FECHAFPS AND PCK_NOMINA.GL_SMES = 7 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO >= TO_DATE('01/07/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN 
               PCK_NOMINA.CN(160) := 0;
            END IF;
        ELSE
            IF PCK_NOMINA.GL_SMES = 12 THEN
                PCK_NOMINA.CN(159) :=  CASE WHEN PCK_NOMINA.FC_CN(159) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/6*PCK_NOMINA.GL_DOCEAVAS/30*PCK_NOMINA.FC_CN(67)+0.049,0) ELSE PCK_NOMINA.FC_CN(159) END -PCK_NOMINA.FC_CNA(159);
            ELSE
                PCK_NOMINA.CN(160) :=  CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/12*PCK_NOMINA.GL_DOCEAVAS/30*PCK_NOMINA.FC_CN(67),0) ELSE PCK_NOMINA.FC_CN(160) END -PCK_NOMINA.FC_CNA(160);    
            END IF;
        END IF;
    ELSIF PCK_NOMINA.FC_CN(404) = 1 THEN
        PCK_NOMINA.GL_FECHAIPS := CASE WHEN PCK_NOMINA.GL_FECHAI > PCK_NOMINA.GL_FECHAIPS THEN PCK_NOMINA.GL_FECHAI ELSE PCK_NOMINA.GL_FECHAIPS END;
        PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAR)/30;
        PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS)-PCK_NOMINA.GL_DNT;
        FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
            PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,i,1,PCK_NOMINA.GL_SANO,i,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF (PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339)) > 0 THEN
                PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS-1;
                PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953)+(PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339));
            END IF;
        END LOOP;
        FOR i IN 6..12 LOOP
            PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,(PCK_NOMINA.GL_SANO-1),i,1,(PCK_NOMINA.GL_SANO-1),i,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
            IF (PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339)) > 0 THEN
                PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DOCEAVAS-1;
                PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953)+(PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339));
            END IF;
        END LOOP;
        IF PCK_PARST.FC_PAR('ELIMINAR TIEMPO MINIMO EN PRIMA DE SERVICIOS','NO') = 'SI' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS)-PCK_NOMINA.FC_CN(953);
            MI_DOCEAVASMINIMASPS := TO_NUMBER(PCK_PARST.FC_PAR('DOCEAVAS MINIMAS PRIMA SERVICIOS','1'));
            PCK_NOMINA.GL_DOCEAVAS := PCK_SYSMAN_UTL.FC_MESESCOMPLETOS(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS-PCK_NOMINA.FC_CN(953));
        ELSE 
            IF PCK_NOMINA.GL_DOCEAVAS = 0 OR PCK_NOMINA.GL_DOCEAVAS > 12 THEN
                PCK_NOMINA.GL_DCC := 0;
                PCK_NOMINA.GL_DOCEAVAS := 0;
            END IF;
            IF PCK_NOMINA.FC_CN(952) > 0 THEN
                PCK_NOMINA.GL_DCC := PCK_NOMINA.FC_CN(952);
                PCK_NOMINA.GL_DOCEAVAS := PCK_NOMINA.GL_DCC/30;
            END IF;
        END IF;
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS',' ') = 'SI' THEN   
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS)-PCK_NOMINA.FC_CN(953);
            IF PCK_NOMINA.GL_SMES = 12 THEN
                IF PCK_NOMINA.GL_DCC < 180 THEN 
                    PCK_NOMINA.CN(159) := CASE WHEN PCK_NOMINA.FC_CN(159) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/360*PCK_NOMINA.GL_DCC/30*PCK_NOMINA.FC_CN(67)+0.049,0) ELSE PCK_NOMINA.FC_CN(159) END -PCK_NOMINA.FC_CNA(159);
                ELSE
                    PCK_NOMINA.CN(159) := CASE WHEN PCK_NOMINA.FC_CN(159) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/180*PCK_NOMINA.GL_DCC/30*PCK_NOMINA.FC_CN(67)+0.049,0) ELSE PCK_NOMINA.FC_CN(159) END -PCK_NOMINA.FC_CNA(159);    
                END IF;
            ELSE
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/360*PCK_NOMINA.GL_DCC/30*PCK_NOMINA.FC_CN(67),0) ELSE PCK_NOMINA.FC_CN(160) END -PCK_NOMINA.FC_CNA(160);
            END IF;
        ELSE
            IF PCK_NOMINA.GL_SMES = 12  AND PCK_NOMINA.FC_CN(404) = 0 THEN
                PCK_NOMINA.CN(159) :=  CASE WHEN PCK_NOMINA.FC_CN(159) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/6*PCK_NOMINA.GL_DOCEAVAS/30*PCK_NOMINA.FC_CN(67)+0.049,0) ELSE PCK_NOMINA.FC_CN(159) END -PCK_NOMINA.FC_CNA(159);
            ELSE
                PCK_NOMINA.CN(160) :=  CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/12*PCK_NOMINA.GL_DOCEAVAS/30*PCK_NOMINA.FC_CN(67),0) ELSE PCK_NOMINA.FC_CN(160) END -PCK_NOMINA.FC_CNA(160);    
            END IF;
        END IF;
        IF UPPER(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).DE_CARRERA) = 'TD' THEN
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS)-PCK_NOMINA.GL_DNT;
            FOR i IN 1..PCK_NOMINA.GL_SMES LOOP
                PCK_NOMINA.GL_ACS := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_NOMINA.GL_SANO,i,1,PCK_NOMINA.GL_SANO,i,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
                IF (PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339)) > 0 THEN
                    PCK_NOMINA.GL_DCC := PCK_NOMINA.GL_DCC-PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339);
                    PCK_NOMINA.CN(953) := PCK_NOMINA.FC_CN(953)+(PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(339));
                END IF;
            END LOOP;
            IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS',' ') = 'SI' THEN   
                PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS)-PCK_NOMINA.FC_CN(953);
                PCK_NOMINA.CN(160) :=  CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/360*PCK_NOMINA.GL_DCC/30*PCK_NOMINA.FC_CN(67),0) ELSE PCK_NOMINA.FC_CN(160) END -PCK_NOMINA.FC_CNA(160);    
            ELSE
                PCK_NOMINA.CN(160) :=  CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/12*PCK_NOMINA.GL_DOCEAVAS/30*PCK_NOMINA.FC_CN(67),0) ELSE PCK_NOMINA.FC_CN(160) END -PCK_NOMINA.FC_CNA(160);    
           END IF;
        END IF;
    ELSE
        IF PCK_PARST.FC_PAR('CALCULAR PRIMA DE SERVICIOS PROPORCIONAL POR DIAS',' ') = 'SI' THEN   
            PCK_NOMINA.GL_DCC := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIPS,PCK_NOMINA.GL_FECHAFPS)-PCK_NOMINA.FC_CN(953);
            IF PCK_NOMINA.GL_SMES = 12 THEN
                IF PCK_NOMINA.GL_DCC < 180 THEN 
                    PCK_NOMINA.CN(159) := CASE WHEN PCK_NOMINA.FC_CN(159) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/360*PCK_NOMINA.GL_DCC/30*PCK_NOMINA.FC_CN(67)+0.049,0) ELSE PCK_NOMINA.FC_CN(159) END -PCK_NOMINA.FC_CNA(159);
                ELSE
                    PCK_NOMINA.CN(159) := CASE WHEN PCK_NOMINA.FC_CN(159) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/180*PCK_NOMINA.GL_DCC/30*PCK_NOMINA.FC_CN(67)+0.049,0) ELSE PCK_NOMINA.FC_CN(159) END -PCK_NOMINA.FC_CNA(159);    
                END IF;
            ELSE
                PCK_NOMINA.CN(160) :=  CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/360*PCK_NOMINA.GL_DCC/30*PCK_NOMINA.FC_CN(67),0) ELSE PCK_NOMINA.FC_CN(160) END -PCK_NOMINA.FC_CNA(160);    
            END IF;
        ELSE
            IF PCK_NOMINA.GL_SMES = 12 THEN
                PCK_NOMINA.CN(159) := CASE WHEN PCK_NOMINA.FC_CN(159) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/6*PCK_NOMINA.GL_DOCEAVAS/30*PCK_NOMINA.FC_CN(67)+0.049,0) ELSE PCK_NOMINA.FC_CN(159) END -PCK_NOMINA.FC_CNA(159);
            ELSE
                PCK_NOMINA.CN(160) := CASE WHEN PCK_NOMINA.FC_CN(160) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.GL_FACTORPS/12*PCK_NOMINA.GL_DOCEAVAS/30*PCK_NOMINA.FC_CN(67),0) ELSE PCK_NOMINA.FC_CN(160) END -PCK_NOMINA.FC_CNA(160);    
            END IF;
        END IF;
    END IF;
    IF PCK_NOMINA.GL_SMES = 12  AND PCK_NOMINA.FC_CN(404) = 0 THEN
        MI_PRIMAJUNIO := PCK_NOMINA.FC_CN(159);
    ELSE
        MI_PRIMAJUNIO := PCK_NOMINA.FC_CN(160);
    END IF;
    MI_DIASPACTADOS := PCK_NOMINA.FC_CN(67);
	IF MI_PRIMAJUNIO > 0 THEN						  
		PCK_NOMINA.GL_FACTORPS1 := MI_PRIMAJUNIO - CASE WHEN PCK_NOMINA.GL_AUXT = 0 THEN 0 ELSE PCK_NOMINA.GL_AUXT/PCK_NOMINA.GL_DOCEAVAS END;
	END IF;
    MI_INDRETIR := PCK_NOMINA.FC_CN(404);
    IF PCK_NOMINA.GL_SPER = 4 AND (PCK_NOMINA.GL_SMES = 6 OR PCK_NOMINA.GL_SMES = 7) THEN 
        FOR i IN 2..699 LOOP
            IF (i <> 125) AND (i <> 451) AND (i <> 401) AND (i <> 10) AND (i <> 402) AND (i <> 303) AND (i <> 301) AND (i <> 300) AND (i < 599) OR (i >= 600 AND i <= 698) AND (PCK_NOMINA.FC_CN(i) > 0 AND PCK_NOMINA.FC_CN(i) < 1) THEN
                PCK_NOMINA.CN(i) := 0;
            END IF;
        END LOOP;
    END IF;
    IF PCK_NOMINA.FC_CN(451) <> 0 AND PCK_PARENTR.PARAMETRO31 = '801018833-8' THEN
        PCK_NOMINA.CN(611) := CASE WHEN PCK_NOMINA.FC_CN(611) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(160)*PCK_NOMINA.FC_CN(451),0) ELSE PCK_NOMINA.FC_CN(611) END;
    END IF;
    PCK_NOMINA.CN(404) := MI_INDRETIR;
    PCK_NOMINA.CN(67) := PCK_NOMINA.GL_DOCEAVAS;
    IF PCK_NOMINA.GL_SMES = 12  AND PCK_NOMINA.FC_CN(404) = 0 THEN
        PCK_NOMINA.CN(159) := MI_PRIMAJUNIO;
    ELSE 
       PCK_NOMINA.CN(160) := MI_PRIMAJUNIO; 
    END IF;
    PCK_NOMINA.CN(945) := CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
    PCK_NOMINA.CN(947) := 0;
    PCK_NOMINA.CN(948) := PCK_NOMINA.GL_AUXT;
    PCK_NOMINA.CN(949) := PCK_NOMINA.GL_AUXA;
    PCK_NOMINA.CN(950) := PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.GL_VPT;
    PCK_NOMINA.CN(951) := CASE WHEN PCK_NOMINA.GL_DCC < 0 THEN 0 ELSE PCK_NOMINA.GL_DCC END;
    PCK_NOMINA.CN(952) := CASE WHEN PCK_NOMINA.GL_DCC < 0 THEN 0 ELSE PCK_NOMINA.GL_DCC END;
    PCK_NOMINA.CN(954) := PCK_NOMINA.GL_VPA;   
    PCK_NOMINA.GL_PS_BASE := PCK_NOMINA.GL_FACTORPS;
    PCK_NOMINA.GL_PS_DIAS := CASE WHEN PCK_NOMINA.GL_DCC < 0 THEN 0 ELSE PCK_NOMINA.GL_DCC END;
END PR_CALCPRIMASEMESTRALUES;

PROCEDURE PR_CESFNACARDER
/*
    NAME              : PR_CESFNACARDER
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO
    DATE MIGRADOR     : 16/04/2026
*/
AS
    MI_CN990              PCK_SUBTIPOS.TI_DOBLE     DEFAULT 0;
    MI_CN988              PCK_SUBTIPOS.TI_DOBLE     DEFAULT 0;
    MI_CN987              PCK_SUBTIPOS.TI_DOBLE     DEFAULT 0;
    MI_CN989              PCK_SUBTIPOS.TI_DOBLE     DEFAULT 0;
    MI_CN991              PCK_SUBTIPOS.TI_DOBLE     DEFAULT 0;
    MI_DIAS               PCK_SUBTIPOS.TI_ENTERO    DEFAULT 0;
    MI_DIASENCA           PCK_SUBTIPOS.TI_ENTERO    DEFAULT 0;
    MI_ASB                PCK_SUBTIPOS.TI_DOBLE     DEFAULT 0;
    MI_AUX                PCK_SUBTIPOS.TI_DOBLE     DEFAULT 0;
    MI_PASB               PCK_SUBTIPOS.TI_DOBLE     DEFAULT 0;
    MI_FACTORES           NUMBER;
    MI_ASBAUX             NUMBER;
    MI_PARNUM             NUMBER;
    MI_MSG                PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CESCAL             NUMBER;
BEGIN
    -- TICKET 7737584 EFCM: SE CAMBIA PROCESO DE VARIACION DE SALARIO EN LOS ULTIMOS 3 MESES AL PERIODO 3
    --                      EL PROMEDIO SE CALCULA TENIENDO EN CUENTA ENCARCARGOS Y DIAS TRABAJADOS EN LOS ENCARGOS
    -- TICKET 7730381 EFCM: SE VALIDA PARA EL PERIODO 8 LA VARIACION DE SALARIO EN LOS ULTIMOS 3 MESES
    -- TICKET 7741381 EFCM: EL PROMEDIO SE UTILIZA SI HAY VARIACION DE SALARIOS DESDE OCTUBRE A DICIEMBRE
    MI_AUX := 0;
    IF (PCK_NOMINA.GL_SPER = 3 AND PCK_NOMINA.GL_SMES IN (10,11,12) ) THEN
        WITH    ABMS AS
                (
                SELECT  HISTORICOS.MES MES, SUM(HISTORICOS.VALOR) ABM
                FROM    HISTORICOS
                WHERE   HISTORICOS.COMPANIA = PCK_NOMINA.GL_COMPANIA
                        AND HISTORICOS.ID_DE_PROCESO = PCK_NOMINA.GL_SPRC
                        AND HISTORICOS.ANO = PCK_NOMINA.GL_SANO
                        AND HISTORICOS.MES BETWEEN 1 AND PCK_NOMINA.GL_SMES - 1
                        AND HISTORICOS.PERIODO IN (3,33)
                        AND HISTORICOS.ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
                        AND HISTORICOS.ID_DE_CONCEPTO = 900
                GROUP BY HISTORICOS.MES
                UNION ALL
                SELECT  PCK_NOMINA.GL_SMES MES,
                        CASE WHEN PCK_NOMINA.FC_CN(10) > 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END ABM
                FROM    DUAL
                )
        SELECT  CEIL(AVG(ABM)),
        		STDDEV(
                        CASE WHEN PCK_NOMINA.GL_SMES = 10
                            THEN CASE WHEN MES BETWEEN 9 AND 10 THEN ABM ELSE NULL END
                            ELSE CASE WHEN MES BETWEEN 10 AND 12 THEN ABM ELSE NULL END
                        END
                    )
        INTO    MI_PASB, MI_AUX
        FROM    ABMS
        ;
    END IF;
    -- TICKET 7730381 FIN --
    -- TICKET 7737584 FIN --
--24/02/2011 JP EN EL PERIODO MENSUAL ES LA UNICA FORMA DE SABER LOS FACTORES REALES PARA EL CALCULO
PCK_NOMINA.CN(995) := 0;
PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') END;
IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '04' AND (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> PCK_NOMINA.GL_SENAETAPALECTIVA ) THEN --  MOD JM CC 3225 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> '05' AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_TIPO <> PCK_NOMINA.GL_SENAETAPAPRODUCTIVA
    IF (PCK_NOMINA.FC_PERIODODIFERIDOACUMULADO(PCK_NOMINA.GL_COMPANIA, PCK_NOMINA.GL_SPRC, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, PCK_NOMINA.GL_SPER) AND PCK_NOMINA.FC_CN(436) = 0) OR PCK_NOMINA.GL_SPER = 7 OR ( PCK_NOMINA.GL_SPER = 3 AND NVL(MI_AUX,0) != 0) /* TICKET 7730381 EFCM: SE ADICIONA CONDICION PARA QUE EJECUTE CUANDO HAY VARIACION DE SALARIO */ THEN -- IND DE LIQUIDAR PERï¿½?ODO ADICIONAL DE VACACIONES.
        PCK_NOMINA.GL_FECHAIR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).INGRESO_DISTRITO;
        --24/02/2011 JP SE ACUMULA PARA EL MES
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        --PCK_NOMINA.CN(987) := ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160)) / 12, 0); --DOCPRIMASERVICIO
        PCK_NOMINA.CN(988) := ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155)) / 12, 0); --DOCPRIMAVACACIONES
        PCK_NOMINA.CN(989) := ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158)) / 12, 0); --DOCPRIMANAVIDAD
        PCK_NOMINA.CN(990) := ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(538)) / 12, 0); --BONIFICACION
        PCK_NOMINA.CN(991) := PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CN(538) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CN(541) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CN(543) + PCK_NOMINA.FC_CN(504) + PCK_NOMINA.FC_CN(544);
        --SE CALCULA LO DEL MES
        PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339);
        PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/' || PCK_NOMINA.GL_SMES || '/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
        MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
        IF MI_DIAS = 0 THEN --22012020
           IF PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFIN) = 30 THEN --------22012020
              MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1 + 1) - PCK_NOMINA.GL_LICENCIAS;
              IF (PCK_NOMINA.GL_FECHAI >= PCK_NOMINA.GL_FECHAINI1 AND PCK_NOMINA.GL_FECHAI <= PCK_NOMINA.GL_FECHAFIN1 OR (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO >= PCK_NOMINA.GL_FECHAINI1 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO <= PCK_NOMINA.GL_FECHAFIN1)) AND MI_DIAS < 30 THEN
                  MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1 + 1) - PCK_NOMINA.GL_LICENCIAS;
              END IF;
              IF MI_DIAS = 1 THEN
                 MI_DIAS := 0;
              END IF;
           END IF;
        END IF;
        --25/02/2011 JP SE CALCULAN LOS FACTORES DEL MES
        --                 14062017 SE CAMBIO CUANDO TIENE ENCARGO
        IF (CASE WHEN PCK_NOMINA.FC_CN(10) > 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.FC_CN(186)) >= PCK_NOMINA.FC_CN(89) THEN --SI GANAN MAS DE 2 SMLV NO TIENEN DERECHO
           PCK_NOMINA.GL_AUXA := 0;
        END IF;
          IF (CASE WHEN PCK_NOMINA.FC_CN(10) > 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END  >= PCK_NOMINA.FC_CN(201)* 2 ) THEN --SI GANAN MAS DE 2 SMLV NO TIENEN DERECHO
           PCK_NOMINA.GL_AUXT := 0;
        END IF;
        -- TICKET 7730381 EFCM: PERIODO 8 CON VARACION DE SALARIO SE USA COMO ASB EL PROMEDIO DE ASB DE LOS 12 MESES
        -- TICKET 7737584 EFCM: ASIGNACION BASICA EN CASO DE ENCARGO SE CALCULA CON BASE A LOS DIAS DEL ENCARGO + DIAS SALARIO BASICO
        /*
        MI_ASBAUX := CASE  WHEN PCK_NOMINA.FC_CN(10) > 0
                        THEN CASE WHEN PCK_NOMINA.FC_CN(11) >= 30
                                THEN PCK_NOMINA.FC_CN(10)
                                ELSE CEIL(PCK_NOMINA.FC_CN(10) / 30 * PCK_NOMINA.FC_CN(11)) + (PCK_NOMINA.FC_CN(1) / 30 * PCK_NOMINA.FC_CN(9))
                             END
                        ELSE  PCK_NOMINA.FC_CN(1)
                        END;
        */
        -- TICKET 7737584 FIN --
        -- TICKET 7741381 EFCM: SE DETERMINA QUE EL ASB ES EL ANCARGO SI EXISTIERA, DE LO CONTRARIO EL SALARIO BASICO
        MI_ASBAUX := CASE  WHEN PCK_NOMINA.FC_CN(10) > 0
                        THEN  PCK_NOMINA.FC_CN(10)
                        ELSE  PCK_NOMINA.FC_CN(1)
                        END;
        -- TICKET 7741381 FIN --
        MI_FACTORES := PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + PCK_NOMINA.FC_CN(990) + PCK_NOMINA.FC_CN(988) + PCK_NOMINA.FC_CN(987) + PCK_NOMINA.FC_CN(989) + (PCK_NOMINA.FC_CN(991) / 12);
        IF ( NVL(MI_AUX,0) != 0 AND MI_PASB != 0 ) THEN
            PCK_NOMINA.CN(992) := ROUND(MI_PASB + MI_FACTORES, 0);
        ELSE
            --PCK_NOMINA.CN(992) := ROUND((CASE WHEN PCK_NOMINA.FC_CN(10) > 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END) + MI_FACTORES, 0);
            PCK_NOMINA.CN(992) := ROUND(MI_ASBAUX + MI_FACTORES, 0);
        END IF;
        -- TICKET 7730381 FIN --
        PCK_NOMINA.CN(986) := PCK_NOMINA.FC_CN(992);
        PCK_NOMINA.CN(995) := CASE WHEN PCK_NOMINA.FC_CN(995) = 0 THEN ROUND(PCK_NOMINA.FC_CN(992) * MI_DIAS / 360, 0) ELSE PCK_NOMINA.FC_CN(995) END; --DEL MES
        PCK_NOMINA_PROC01.PR_ACTUALIZARNOVEDADES_CN (900);
        --24/02/2011 JP SE GENERAN LAS CESANTIAS CONSOLIDADAS Y APLICADAS POR EL AÃ‘O O PROPORCIONAL A LO TRABAJADO
        PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.GL_FECHAIR > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.GL_FECHAIR ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO,'DD/MM/YYYY') END;
        --07112018 SE CAMBIO PORQUE INGRESA NUEVAMENTE, PERO NO DEBE TOMAR DIAS DE AÃ‘OS ANTERIORES YA QUE ES ANUALIZADAS
        IF PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > TO_DATE('01/01/' || (PCK_NOMINA.GL_SANO), 'DD/MM/YYYY') AND PCK_NOMINA.GL_FECHAIR < TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN
           PCK_NOMINA.GL_FECHAIC := CASE WHEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO > TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') THEN PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO ELSE TO_DATE('01/01/' || PCK_NOMINA.GL_SANO, 'DD/MM/YYYY') END;
        END IF;
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);  -- ACUMULADO DEL AÃ‘O ACTUAL
        PCK_NOMINA.GL_LICENCIAS := PCK_NOMINA.FC_CNA(356) + PCK_NOMINA.FC_CNA(357) + PCK_NOMINA.FC_CNA(359) + PCK_NOMINA.FC_CNA(339) + PCK_NOMINA.FC_CN(356) + PCK_NOMINA.FC_CN(357) + PCK_NOMINA.FC_CN(359) + PCK_NOMINA.FC_CN(339);
        MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1) - PCK_NOMINA.GL_LICENCIAS;
        IF MI_DIAS = 0 THEN --22012020
           IF PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.GL_FECHAFIN) = 30 THEN --------22012020
              MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1 + 1) - PCK_NOMINA.GL_LICENCIAS;
              IF (PCK_NOMINA.GL_FECHAI >= PCK_NOMINA.GL_FECHAINI1 AND PCK_NOMINA.GL_FECHAI <= PCK_NOMINA.GL_FECHAFIN1 OR (PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO >= PCK_NOMINA.GL_FECHAINI1 AND PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHATERCONTRATO <= PCK_NOMINA.GL_FECHAFIN1)) AND MI_DIAS < 30 THEN
                  MI_DIAS := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(PCK_NOMINA.GL_FECHAIC, PCK_NOMINA.GL_FECHAFIN1 + 1) - PCK_NOMINA.GL_LICENCIAS;
              END IF;
              IF MI_DIAS = 1 THEN
                 MI_DIAS := 0;
              END IF;
           END IF;
        END IF;
        --25/02/2011 JP SE MONTA ASI PUES AL PLANO SE MANDA TANTO LAS CESANTIAS COMO EL VALOR AJUSTADO
        MI_CN990 := ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150)) / 12, 0);
        MI_CN988 := ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155)) / 12, 0);
        MI_CN987 := ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160)) / 12, 0);
        MI_CN989 := ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158)) / 12, 0);
        MI_CN991 := PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(538) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(501) + PCK_NOMINA.FC_CNA(501) + PCK_NOMINA.FC_CN(541) + PCK_NOMINA.FC_CNA(541) + PCK_NOMINA.FC_CN(503) + PCK_NOMINA.FC_CNA(503) + PCK_NOMINA.FC_CN(543) + PCK_NOMINA.FC_CNA(543) + PCK_NOMINA.FC_CN(504) + PCK_NOMINA.FC_CNA(504) + PCK_NOMINA.FC_CN(544) + PCK_NOMINA.FC_CNA(544) + PCK_NOMINA.FC_CN(512) + PCK_NOMINA.FC_CN(515) + PCK_NOMINA.FC_CNA(512) + PCK_NOMINA.FC_CNA(515) + PCK_NOMINA.FC_CNA(621) + PCK_NOMINA.FC_CN(621);
        --PCK_NOMINA.CN(987) := ROUND((PCK_NOMINA.FC_CNA(160) + PCK_NOMINA.FC_CN(160)) / 12, 0); --DOCPRIMASERVICIO
        --INICIO 7708549  FAGUERO
        PCK_NOMINA.CN(988) := ROUND((PCK_NOMINA.FC_CNA(155) + PCK_NOMINA.FC_CN(155)) / 12, 0); --DOCPRIMAVACACIONES
        PCK_NOMINA.CN(989) := ROUND((PCK_NOMINA.FC_CNA(158) + PCK_NOMINA.FC_CN(158)) / 12, 0); --DOCPRIMANAVIDAD
        PCK_NOMINA.CN(990) := ROUND((PCK_NOMINA.FC_CNA(150) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CNA(514) + PCK_NOMINA.FC_CN(514) + PCK_NOMINA.FC_CNA(538) + PCK_NOMINA.FC_CN(538)) / 12, 0); --BONIFICACION
        --FIN 7708549 FAGUERO
        --15/02/2013 JP DESPUES DE UN ANALISIS MACRABRO EN EXCEL CON NUBIA PATRICIA Y CAROLINA DESPELUCANDOME SE DESIDIO TOMAR EL ENCARGO SOLAMENTE EN DICIEMBRE Y RETIRO PARA EVITAR AJUSTES NEGATIVOS
        IF PCK_NOMINA.FC_CNA(11) + PCK_NOMINA.FC_CN(11) > 0 AND (PCK_NOMINA.FC_CN(404) <> 0 OR (PCK_NOMINA.FC_CN(412) <> 0 AND PCK_NOMINA.GL_SPER = 8)) THEN
            MI_ASB := PCK_NOMINA_COM10.FC_VALENCARGO(PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC), '01', PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO, MI_DIASENCA);
            IF PCK_NOMINA.FC_CN(10) <> 0 THEN
                MI_ASB := MI_ASB + (PCK_NOMINA.FC_CN(10) / 30 * PCK_NOMINA.FC_CN(11));
                MI_DIASENCA := MI_DIASENCA + PCK_NOMINA.FC_CN(11);
            ELSE
                MI_ASB := MI_ASB + (PCK_NOMINA.FC_CN(1) / 30 * (MI_DIAS - MI_DIASENCA));
                MI_ASB := ROUND(MI_ASB / MI_DIAS * 30, 0);
            END IF;
        -- TICKET 7730381 EFCM: PERIODO 8 CON VARACION DE SALARIO SE USA COMO ASB EL PROMEDIO DE ASB DE LOS 12 MESES
        ELSIF ( NVL(MI_AUX,0) != 0 AND NVL(MI_PASB,0) != 0 ) THEN
            MI_ASB := MI_PASB;
        -- TICKET 7730381 FIN --
        ELSE
            --MI_ASB := CASE WHEN PCK_NOMINA.FC_CN(10) > 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
            MI_ASB := MI_ASBAUX;
        END IF;
        IF PCK_NOMINA.FC_CN(900) <> 0 THEN -- PARA AJUSTES COMO NOVEDAD 69267 13062017
           MI_ASB := PCK_NOMINA.FC_CN(900);
        END IF;
        --JM se identifica que el 987 se utiliza para guardar la base de vacaciones y no la doceava del 160 (el cual utiliza el 906)
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA, (PCK_NOMINA.GL_ANOA), PCK_NOMINA.GL_MESA, PCK_NOMINA.GL_PERA, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        PCK_NOMINA.CN(987) := PCK_SYSMAN_UTL.FC_ROUND(CASE WHEN PCK_NOMINA.FC_CN(10) <> 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END + PCK_NOMINA.GL_GRPNGV + PCK_NOMINA.GL_AUXT + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_VPT + PCK_NOMINA.FC_CN(981) + PCK_SYSMAN_UTL.FC_ROUND((PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO) + PCK_NOMINA.FC_CN(150) + PCK_NOMINA.FC_CN(514)) / 12, 0), 0);
        --GUARDANDO FACTORES
        PCK_NOMINA.CN(900) := MI_ASB;
        PCK_NOMINA.CN(901) := (MI_CN991 / 12);
        PCK_NOMINA.CN(903) := PCK_NOMINA.GL_AUXT;
        PCK_NOMINA.CN(904) := PCK_NOMINA.GL_AUXA;
        PCK_NOMINA.CN(905) := MI_CN989;
        PCK_NOMINA.CN(906) := MI_CN987; --ROUND((PCK_NOMINA.FC_CNA(160) + CN(160)) / 12 + PCK_NOMINA.FC_CNA(503) / 12 + PCK_NOMINA.FC_CNA(543) / 12, 0)
        PCK_NOMINA.CN(907) := MI_CN988; --ROUND(PVAC / 12, 0)
        PCK_NOMINA.CN(990) := MI_CN990; --ROUND(PCK_NOMINA.FC_CNA(150) / 12 + PCK_NOMINA.FC_CNA(514) / 12 + PCK_NOMINA.FC_CNA(538) / 12, 0)      --PCK_NOMINA.FC_CNA(500) + CN(500)                                   -- PRIMA DE LOCALIZACION PCK_NOMINA.FC_CNA(150) / 12 + PCK_NOMINA.FC_CNA(514) / 12 + PCK_NOMINA.FC_CNA(538) / 12
        PCK_NOMINA.CN(909) := 0;        -- ULTIMO QUINQUENIO
        PCK_NOMINA.CN(910) := MI_DIAS;  -- DIAS
        --PCK_NOMINA.CN(911) := ANTICIPOS;-- ANTICIPOS
        PCK_NOMINA.CN(912) := PCK_NOMINA.GL_LICENCIAS; -- DIAS NO TRABAJADOS POR PCK_NOMINA.GL_LICENCIAS
        PCK_NOMINA.CN(992) :=  PCK_SYSMAN_UTL.FC_ROUND(MI_ASB + PCK_NOMINA.GL_AUXA + PCK_NOMINA.GL_AUXT + MI_CN990 + MI_CN988 + MI_CN987 + MI_CN989 + (MI_CN991 / 12), 0);
        PCK_NOMINA.CN(986) := PCK_NOMINA.FC_CN(992);
        --JP SE MONTA CON ROUNDARRIBA PARA QUE LOS AJUSTEN NO DE UN PESO NEGATIVO EN ALGUNOS CASOS
        PCK_NOMINA.CN(996) := CASE WHEN PCK_NOMINA.FC_CN(996) = 0 THEN PCK_SYSMAN_UTL.FC_ROUND(PCK_NOMINA.FC_CN(992) * MI_DIAS / 360, 0) ELSE PCK_NOMINA.FC_CN(996) END;  --CONSOLIDADAS5101874
        --25/02/2011 JP SE AJUSTA PARA QUE RECALCULE TODO EL AÃ‘O Y RESTE LAS APLICADAS CON EL FIN DE EVITAR AJUSTA A FIN DE AÃ‘O
        --JM 12/03/2025 CC 953 SE ASEGURA QUE EL ACUMULADO DEL 483 SOLO SEA DEL AÑO ACTUAL Y DE ESTA MANERA NO CALCULE NEGATIVO
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(PCK_NOMINA.GL_COMPANIA,PCK_NOMINA.GL_SANO, PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIC), 1, PCK_NOMINA.GL_SANO, PCK_NOMINA.GL_SMES, 99, PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);  -- ACUMULADO DEL AÃ‘O ACTUAL
        PCK_NOMINA.CN(483) := CASE WHEN PCK_NOMINA.FC_CN(483) = 0 THEN PCK_NOMINA.FC_CN(996) - PCK_NOMINA.FC_CNA(483) ELSE PCK_NOMINA.FC_CN(483) END; --CESANTIAS DEL MES CON AJUSTE INCLUIDO
        -- TICKET 7737584 EFCM: PARAMETRO PARA CONTROLAR NEGATIVOS EN MES 12
        IF (PCK_NOMINA.GL_SPER = 3 AND PCK_NOMINA.GL_SMES IN (10,11,12) AND PCK_NOMINA.CN(483) < 0 ) THEN
            IF ( PCK_PARST.FC_PAR('CESANTIAS MENSUALIZADAS MES 12 CAMBIAR NEGATIVOS A CERO', 'NO') = 'SI' ) THEN
                    PCK_NOMINA.CN(483) := 0;
            END IF;
            -- 7741381 EFCM: SE ADICIONA ALERTA DE SALDOS NEGATIVOS EN MES 12 PARA CESANTIAS MENSUALIZADAS
            --El empleado --EMPLEADO--, Nro. De identificación --CEDULA--, Tiene saldo Negativo en el mes --MES--, año --ANO-- del periodo --PERIODO-- de nomina, concepto CESANTIAS MENSUALIZADAS.
            MI_MSG(1).CLAVE := 'EMPLEADO';
            MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES;
            MI_MSG(2).CLAVE := 'CEDULA';
            MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
            MI_MSG(3).CLAVE := 'MES';
            MI_MSG(3).VALOR := PCK_NOMINA.GL_SMES;
            MI_MSG(4).CLAVE := 'ANO';
            MI_MSG(4).VALOR := PCK_NOMINA.GL_SANO;
            MI_MSG(5).CLAVE := 'PERIODO';
            MI_MSG(5).VALOR := PCK_NOMINA.GL_SPER;
            PCK_NOMINA_COM7.PR_ALERTA
            (
            UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
            ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_CESMENS_SALDONEG
            ,UN_REEMPLAZOS   => MI_MSG
            ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
            ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
            ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
            ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
            ,UN_USER         => PCK_CONEXION.FC_GETUSER
            );
            -- 7741381 FIN--
        END IF;
        -- TICKET 7737584 FIN --
        PCK_NOMINA.CN(993) := CASE WHEN PCK_NOMINA.FC_CN(993) = 0 THEN PCK_NOMINA.FC_CNA(483) + PCK_NOMINA.FC_CN(483) ELSE PCK_NOMINA.FC_CN(993) END; --APLICADAS
        PCK_NOMINA.CN(997) := CASE WHEN PCK_NOMINA.FC_CN(997) = 0 THEN PCK_NOMINA.FC_CN(996) - PCK_NOMINA.FC_CN(993) ELSE PCK_NOMINA.FC_CN(997) END;
        PCK_NOMINA.CN(995) := PCK_NOMINA.FC_CN(483) - CASE WHEN PCK_NOMINA.FC_CN(995) = -1 THEN 0 ELSE PCK_NOMINA.FC_CN(995) END; --AJUSTE MENSUAL
        IF PCK_NOMINA.FC_CN(995) <> 0 THEN
           PCK_NOMINA.CN(483) := PCK_NOMINA.FC_CN(993) - PCK_NOMINA.FC_CNA(483);
           PCK_NOMINA.CN(995) := 0;
        END IF;
        --2402/2011 JP GUARDA LOS ACUMULADOS
        PCK_NOMINA.CN(994) := PCK_NOMINA.FC_CNA(992) + CASE WHEN PCK_NOMINA.FC_CN(483) <> 0 THEN PCK_NOMINA.FC_CN(995) ELSE 0 END; --ACUMULADO DE FACTORES PARA FNA -- 13032019
        PCK_NOMINA.CN(910) := MI_DIAS;
         -- TICKET 7741381 EFCM : SE CREA ALERTA SI EXISTEN DIFERENCIAS CONSIDERABLES ENTRE LO CONSIGNADO EN EL AÑO POR CESANTIAS
        --                                    Y LO CALCULADO POR CESANTIAS CON EL SALARIO NOMINAL
        IF (PCK_NOMINA.GL_SPER = 3 AND PCK_NOMINA.GL_SMES = 12 ) THEN
            MI_CESCAL := PCK_NOMINA.CN(1)  -- ABM
                                + PCK_NOMINA.CN(903)  -- AT
                                + PCK_NOMINA.CN(904)  -- SA
                                + PCK_NOMINA.CN(990) -- BASP 1/12
                                + PCK_NOMINA.CN(987) -- PS 1/12
                                + PCK_NOMINA.CN(989) -- PN 1/12
                                + PCK_NOMINA.CN(988) -- PV 1/12
                                + PCK_NOMINA.CN(901) -- AJUSTES BASP 1/12
                                + PCK_NOMINA.CN(991) -- RETROACTIVOS 1/12
                                ;
            IF ( ABS( PCK_NOMINA.FC_CN(993) - ROUND(MI_CESCAL /360 * PCK_NOMINA.FC_CN(910))  ) > 1000 ) THEN
                ----ALERTA 'El empleado --EMPLEADO--, Nro. De identificación --CEDULA--, presenta diferencias entre el valor total de CESANTIAS a diciembre y el calculo de CESANTIAS con el SALARIO NOMINAL. Revise encargos y variaciones de salario.'
                MI_MSG(1).CLAVE := 'EMPLEADO';
                MI_MSG(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO1 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).APELLIDO2 || ' ' || PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NOMBRES ;
                MI_MSG(2).CLAVE := 'CEDULA';
                MI_MSG(2).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).NUMERO_DCTO;
                PCK_NOMINA_COM7.PR_ALERTA
                (
                UN_COMPANIA     => PCK_NOMINA.GL_COMPANIA
                ,UN_MENSAJE_COD  => PCK_ERRORES.ALER_CESMENS_DIFERENCIAS
                ,UN_REEMPLAZOS   => MI_MSG
                ,UN_PROCESO      => PCK_NOMINA.GL_PROCESOACTUAL
                ,UN_ANO          => PCK_NOMINA.GL_ANOACTUAL
                ,UN_MES          => PCK_NOMINA.GL_MESACTUAL
                ,UN_PERIODO      => PCK_NOMINA.GL_PERIODOACTUAL
                ,UN_USER         => PCK_CONEXION.FC_GETUSER
                );
            END IF;
        END IF;
        -- TICKET 7741381 FIN --
    END IF;
END IF;
END PR_CESFNACARDER;

FUNCTION FC_DIFERIR_INC
/*
    NAME              : FC_DIFERIR_INC
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : GERMAN DAVID ROJAS
    DATE MIGRADOR     : 13/04/2026
    TIME              : 02:43
    SOURCE MODULE     :
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :
    DESCRIPTION       : DIFIERE LOS VALORES DE UNA NOVEDAD ENTRE LOS PERIODOS ACUMULABLES Y DIFERIDOS.
    --NAME             : getDiferir
    --METHOD           : GET
    */
(
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_PROCESO     IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
  UN_ANIO        IN PCK_SUBTIPOS.TI_ANIO,
  UN_MES         IN PCK_SUBTIPOS.TI_MES,
  UN_PERIODO     IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
  UN_ID_EMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
  UN_CONCEPTO    IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
  UN_DIFERIDO    IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO,
  UN_OPCION      IN VARCHAR2 DEFAULT NULL,
  UN_CONCEPTO1   IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO DEFAULT NULL,
  UN_VALOR1      IN PCK_SUBTIPOS.TI_DOBLE DEFAULT NULL,
  UN_CONCEPTO2   IN PCK_SUBTIPOS.TI_ID_DE_CONCEPTO DEFAULT NULL,
  UN_VALOR2      IN PCK_SUBTIPOS.TI_DOBLE DEFAULT NULL,
  UN_FECHAINICIO IN DATE,
  UN_INCAPACIDAD IN PCK_SUBTIPOS.TI_ENTERO DEFAULT 0,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO,
  UN_FECHAFIN IN DATE DEFAULT NULL
)
RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_IDIASDISFRUTE    PCK_SUBTIPOS.TI_ENTERO DEFAULT 0;
  MI_OPCION           VARCHAR(100);
  MI_ANIO             PCK_SUBTIPOS.TI_ANIO;
  MI_MES              PCK_SUBTIPOS.TI_MES;
  MI_PERIODO          PCK_SUBTIPOS.TI_PERIODO_NOMI;
  MI_DIAFINAL         PCK_SUBTIPOS.TI_ENTERO;
  MI_DIAINICIO        PCK_SUBTIPOS.TI_ENTERO;
  MI_ANIOINICIO       PCK_SUBTIPOS.TI_ANIO;
  MI_MESINICIO        PCK_SUBTIPOS.TI_MES;
  MI_NIT              PCK_SUBTIPOS.TI_TERCERO;
  MI_DIFERIDO         PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_SIGUIENTE        PCK_SUBTIPOS.TI_TERCERO;
  MI_DIASPER          PCK_SUBTIPOS.TI_ENTERO;
  MI_VALOR            PCK_SUBTIPOS.TI_DOBLE;
  MI_ACCION           PCK_SUBTIPOS.TI_LOGICO;
  MI_AC               PCK_SUBTIPOS.TI_DOBLE;
  MI_DIAS31           PCK_SUBTIPOS.TI_ENTERO;
BEGIN
  MI_DIFERIDO :=UN_DIFERIDO;
  MI_OPCION   :=UPPER(UN_OPCION);
  IF  UN_OPCION IS NULL THEN
    MI_OPCION:='ADICIONAR';
  END IF;
  FOR RS IN (
    SELECT   ANO
           , MES
           , PERIODO
           , FECHAFINAL
           , FECHAINICIO
    FROM PERIODOS
    WHERE COMPANIA        = UN_COMPANIA
      AND ID_DE_PROCESO   = UN_PROCESO
      AND ACUMULADO       NOT IN (0)
      AND DIFERIDOS       NOT IN (0)
      AND UN_FECHAINICIO  BETWEEN FECHAINICIO AND FECHAFINAL
    )
  LOOP
    MI_ANIO     := RS.ANO;
    MI_MES      := RS.MES;
    MI_PERIODO  := RS.PERIODO;
    MI_DIAFINAL := TO_NUMBER(TO_CHAR(RS.FECHAFINAL ,'DD'));
    MI_DIAINICIO:= TO_NUMBER(TO_CHAR(UN_FECHAINICIO,'DD'));
    MI_DIAS31 := 0;
    IF MI_DIAFINAL>16 AND MI_DIAFINAL<>31 THEN
      MI_IDIASDISFRUTE:=30-MI_DIAINICIO+1;
    ELSIF MI_DIAFINAL=31 THEN
      --MI_IDIASDISFRUTE:=31-MI_DIAINICIO+1;
      --MZANGUNA:02/08/2018, Se revisa contra access y no se resta
      MI_IDIASDISFRUTE:=31-MI_DIAINICIO;
      
      MI_NIT :=PCK_NOMINA_COM1.FC_PARAMETRO(
                               UN_COMPANIA => UN_COMPANIA
                              ,UN_OPCION   => 31);
      IF UN_INCAPACIDAD <>0 THEN
        IF MI_NIT IN('830.065.741-1','890704536-7','899999433-5') THEN
          MI_IDIASDISFRUTE:=MI_IDIASDISFRUTE-1;
        END IF;
      END IF;
    ELSE
      --(APINEDA:27/08/2019)-Se modifica sumando 1 de acuerdo a como se realiza proceso en access.
      MI_IDIASDISFRUTE:= TRUNC(RS.FECHAFINAL)-TRUNC(UN_FECHAINICIO) + 1;
    END IF;
    IF MI_IDIASDISFRUTE>= MI_DIFERIDO THEN
      MI_IDIASDISFRUTE := MI_DIFERIDO;
    END IF;
        --Se agrega esta funcion segun lo establecido en acces, MIGUEL VENEGAS
   MI_AC := PCK_NOMINA_COM1.FC_ACUMNOVEDADCONCEPTO(UN_COMPANIA,MI_ANIO,MI_MES,MI_PERIODO,MI_ANIO,MI_MES,MI_PERIODO,UN_ID_EMPLEADO);
    IF MI_OPCION='ADICIONAR' THEN
        PCK_NOMINA.PR_INCLUIRNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO => UN_CONCEPTO
                  ,UN_VALOR      => MI_IDIASDISFRUTE
                  --,UN_ACCION     => CASE WHEN UN_CONCEPTO = '35' AND PCK_NOMINA.FC_CNA(35) > 0 THEN 1 ELSE NULL END); --(ORIGINAL MIGUEL VENEGAS)
                  --(APINEDA:11/10/2018)-Se agrega validación para cuando existe más de una incapacidad, se deben actualizar los días en la novedad.
                  ,UN_ACCION     => CASE WHEN  (UN_INCAPACIDAD <> 0) OR (UN_CONCEPTO = '35' AND PCK_NOMINA.FC_CNA(35) > 0) THEN 1 ELSE NULL END);
      IF UN_CONCEPTO1 IS NOT NULL AND UN_VALOR1 IS NOT NULL THEN
        PCK_NOMINA.PR_INCLUIRNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO   => UN_CONCEPTO1
                  ,UN_VALOR      => UN_VALOR1);
      END IF;
      IF UN_CONCEPTO2 IS NOT NULL AND UN_VALOR2 IS NOT NULL THEN
        PCK_NOMINA.PR_INCLUIRNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO   => UN_CONCEPTO2
                  ,UN_VALOR      => UN_VALOR2);
      END IF;
    ELSE
      PCK_NOMINA.PR_BORRARNOVEDADX(
                 UN_COMPANIA   => UN_COMPANIA
                ,UN_PROCESO    => UN_PROCESO
                ,UN_ANIO       => MI_ANIO
                ,UN_MES        => MI_MES
                ,UN_PERIODO    => MI_PERIODO
                ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                ,UN_IDCONCEPTO => UN_CONCEPTO
                ,UN_CANTIDAD   => MI_IDIASDISFRUTE
                ,UN_USUARIO    => UN_USUARIO );
      --(APINEDA:10/10/2018)-Se elimina el registro de concepto 94 solo si el registro eliminado corresponde a PERIODOS DE VACACIONES. Se estaba presentando un problema y estaba eliminando el concepto 94 al eliminar incapacidades, encargos o licencias.
      IF (UN_CONCEPTO = 164) THEN
          PCK_NOMINA.PR_BORRARNOVEDAD(
                     UN_COMPANIA   => UN_COMPANIA
                    ,UN_PROCESO    => UN_PROCESO
                    ,UN_ANIO       => MI_ANIO
                    ,UN_MES        => MI_MES
                    ,UN_PERIODO    => MI_PERIODO
                    ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                    ,UN_IDCONCEPTO => 94);
      END IF;
      IF UN_CONCEPTO1 IS NOT NULL THEN
        PCK_NOMINA.PR_BORRARNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO => UN_CONCEPTO1);
      END IF;
      IF UN_CONCEPTO2 IS NOT NULL THEN
        PCK_NOMINA.PR_BORRARNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO => UN_CONCEPTO2);
      END IF;
    END IF;
    MI_DIFERIDO :=MI_DIFERIDO - MI_IDIASDISFRUTE - MI_DIAS31;
    MI_DIAS31 :=0;
    --(APINEDA:28/11/2018)-TAR1000087627 ANE Se agrega validación de acuerdo a versión NOMINAP2018.09.01 de access para no dejar variable diferido con valor -1
    IF MI_DIFERIDO = -1 THEN
      MI_DIFERIDO := 0;
    END IF;
    WHILE MI_DIFERIDO<>0
    LOOP
      MI_SIGUIENTE :=PCK_NOMINA_COM2.FC_SIGUIENTEPERIODO(
                                     UN_COMPANIA => UN_COMPANIA
                                    ,UN_ANO      => MI_ANIO
                                    ,UN_PERIODO => MI_PERIODO
                                    ,UN_MES => MI_MES
                                    ,UN_PROCESO => UN_PROCESO);
      IF MI_SIGUIENTE= ' '
         OR MI_SIGUIENTE IS NULL THEN
        RETURN 0;
      END IF;
      MI_ANIO     := TO_NUMBER(SUBSTR(MI_SIGUIENTE,1,4));
      MI_MES      := TO_NUMBER(SUBSTR(MI_SIGUIENTE,5,2));
      MI_PERIODO  := TO_NUMBER(SUBSTR(MI_SIGUIENTE,7,2));
      MI_DIASPER  := PCK_NOMINA.FC_DIASPERIODO(
                                UN_COMPANIA => UN_COMPANIA
                               ,UN_PROCESO  => UN_PROCESO
                               ,UN_ANIO     => MI_ANIO
                               ,UN_MES      => MI_MES
                               ,UN_PERIODO  => MI_PERIODO);
      IF UN_FECHAINICIO IS NOT NULL THEN
        MI_ANIOINICIO := TO_NUMBER(TO_CHAR(UN_FECHAINICIO,'YYYY'));
        MI_MESINICIO  := TO_NUMBER(TO_CHAR(UN_FECHAINICIO,'MM'));
        IF MI_ANIOINICIO=MI_ANIO AND MI_MESINICIO=MI_MES THEN
          IF MI_DIAINICIO<=15 AND MI_PERIODO=1 THEN
            MI_DIASPER:= 15 - MI_DIAINICIO + 1;
          ELSIF MI_DIAINICIO>15 AND MI_PERIODO>1 THEN
            MI_DIASPER:= 30 - MI_DIAINICIO + 1;
          END IF;
          IF MI_DIASPER<0 THEN
            MI_DIASPER:=0;
          END IF;
        END IF;
      END IF;
      IF MI_DIFERIDO>=MI_DIASPER THEN
        MI_VALOR    := MI_DIASPER;
        MI_DIFERIDO := MI_DIFERIDO- MI_DIASPER;
      ELSE
        MI_VALOR    := MI_DIFERIDO;
        MI_DIFERIDO :=0;
      END IF;
      IF MI_OPCION='ADICIONAR' THEN
        PCK_NOMINA.PR_INCLUIRNOVEDAD(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO => UN_CONCEPTO
                  ,UN_VALOR      =>  MI_VALOR);
        IF UN_CONCEPTO1 IS NOT NULL AND UN_VALOR1 IS NOT NULL THEN
          PCK_NOMINA.PR_INCLUIRNOVEDAD(
                     UN_COMPANIA   => UN_COMPANIA
                    ,UN_PROCESO    => UN_PROCESO
                    ,UN_ANIO       => MI_ANIO
                    ,UN_MES        => MI_MES
                    ,UN_PERIODO    => MI_PERIODO
                    ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                    ,UN_IDCONCEPTO => UN_CONCEPTO1
                    ,UN_VALOR      => UN_VALOR1);
        END IF;
        IF UN_CONCEPTO2 IS NOT NULL AND UN_VALOR2 IS NOT NULL THEN
          PCK_NOMINA.PR_INCLUIRNOVEDAD(
                     UN_COMPANIA   => UN_COMPANIA
                    ,UN_PROCESO    => UN_PROCESO
                    ,UN_ANIO       => MI_ANIO
                    ,UN_MES        => MI_MES
                    ,UN_PERIODO    => MI_PERIODO
                    ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                    ,UN_IDCONCEPTO => UN_CONCEPTO2
                    ,UN_VALOR      => UN_VALOR2);
        END IF;
      ELSE
        PCK_NOMINA.PR_BORRARNOVEDADX(
                   UN_COMPANIA   => UN_COMPANIA
                  ,UN_PROCESO    => UN_PROCESO
                  ,UN_ANIO       => MI_ANIO
                  ,UN_MES        => MI_MES
                  ,UN_PERIODO    => MI_PERIODO
                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                  ,UN_IDCONCEPTO => UN_CONCEPTO
                  ,UN_CANTIDAD   => MI_VALOR
                  ,UN_USUARIO    => UN_USUARIO);
        IF UN_CONCEPTO1 IS NOT NULL THEN
          PCK_NOMINA.PR_BORRARNOVEDAD(
                     UN_COMPANIA   => UN_COMPANIA
                    ,UN_PROCESO    => UN_PROCESO
                    ,UN_ANIO       => MI_ANIO
                    ,UN_MES        => MI_MES
                    ,UN_PERIODO    => MI_PERIODO
                    ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                    ,UN_IDCONCEPTO => UN_CONCEPTO1);
        END IF;
        IF UN_CONCEPTO2 IS NOT NULL THEN
          PCK_NOMINA.PR_BORRARNOVEDAD(
                     UN_COMPANIA    => UN_COMPANIA
                     ,UN_PROCESO    => UN_PROCESO
                     ,UN_ANIO       => MI_ANIO
                     ,UN_MES        => MI_MES
                     ,UN_PERIODO    => MI_PERIODO
                     ,UN_IDEMPLEADO => UN_ID_EMPLEADO
                     ,UN_IDCONCEPTO => UN_CONCEPTO2);
        END IF;
      END IF;
    END LOOP;
    IF (MI_OPCION ='BORRAR' AND UN_CONCEPTO = 359) THEN
    	PCK_NOMINA_COM2.PR_BORARNOVEDAD359(
    					UN_COMPANIA   => UN_COMPANIA
		                  ,UN_PROCESO    => UN_PROCESO
		                  ,UN_ANIO       => RS.ANO
		                  ,UN_MES        => RS.MES
		                  ,UN_PERIODO    => MI_PERIODO
		                  ,UN_IDEMPLEADO => UN_ID_EMPLEADO
		                  ,UN_IDCONCEPTO => UN_CONCEPTO
		                  ,UN_FECHAFIN   => UN_FECHAFIN
		                  ,UN_USUARIO    => UN_USUARIO);
    END IF;
  END LOOP;
  RETURN -1;
END FC_DIFERIR_INC;

PROCEDURE PR_INSERTINF_BECP
/*
    NAME              : PR_INSERTINF_BECP
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO    
    DESCRIPTION       : Inserta informacion en la tabla TMP_INF_BECPNOM
*/
(
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_IDEMPLEADO IN PCK_SUBTIPOS.TI_ID_DE_EMPLEADO,
    UN_PROCESO    IN PCK_SUBTIPOS.TI_ID_DE_PROCESO,
    UN_ANIO       IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES        IN PCK_SUBTIPOS.TI_MES,
    UN_PERIODO    IN PCK_SUBTIPOS.TI_PERIODO_NOMI,
    UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
)
AS
    MI_CONDI      VARCHAR2(1000);
    MI_RSCONSULTA PCK_SUBTIPOS.TI_STRSQL;
    MI_RS         SYS_REFCURSOR;
    MI_CAMPOS     VARCHAR2(32000);
    MI_VALORES    VARCHAR2(32000);
    MI_RSIDDEMPLEADO  PCK_SUBTIPOS.TI_ID_DE_EMPLEADO;    
    MI_RSSALARIOB PERSONAL.SALARIO_BASE_IBC%TYPE;
    MI_RSSINDICATO PERSONAL.SINDICATO%TYPE;
    MI_RSFCUMPLBONIF PERSONAL.FECHA_CUMPLIMIENTO_BONIFICACIO%TYPE;
    MI_RSFINGRESO PERSONAL.FECHA_DE_INGRESO%TYPE;
    MI_RSNOMCOMPLETO PERSONAL.NOMBRECOMPLETO%TYPE;
    MI_RSNUMDCTO  PERSONAL.NUMERO_DCTO%TYPE;
    MI_RSID_TIPO  PERSONAL.ID_DE_TIPO%TYPE;
    MI_SBM        PCK_SUBTIPOS.TI_DOBLE;   
    MI_AC         NUMBER := 0;
    MI_GR         PCK_SUBTIPOS.TI_DOBLE;
    MI_PT         PCK_SUBTIPOS.TI_DOBLE;
    MI_FINIPSBASP DATE;
    MI_FECHAFIN   DATE;
    MI_MSG        PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_DIASLNRBASP PCK_SUBTIPOS.TI_DOBLE;
    MI_SUELDOBON  PCK_SUBTIPOS.TI_DOBLE;
    MI_PORCBASP   PCK_SUBTIPOS.TI_DOBLE;
    MI_SOBRESUELDO PCK_SUBTIPOS.TI_DOBLE;
    MI_AUXT       PCK_SUBTIPOS.TI_DOBLE;
    MI_AUXA       PCK_SUBTIPOS.TI_DOBLE;
    MI_PA         PCK_SUBTIPOS.TI_DOBLE;
    MI_BASP       PCK_SUBTIPOS.TI_DOBLE;
    MI_EXTRAS     PCK_SUBTIPOS.TI_DOBLE;
    MI_RSREGIMEN  PERSONAL.REGIMEN%TYPE;
    MI_FECHAINIPS DATE;
    MI_DIASLNRPS  PCK_SUBTIPOS.TI_DOBLE;
    MI_PS         PCK_SUBTIPOS.TI_DOBLE;
    MI_FECHAINIPSV DATE;
    MI_RSFFINAL   PERSONAL.FECHA_FINAL%TYPE;
    MI_DIASLNRPV  PCK_SUBTIPOS.TI_DOBLE;
    MI_FECHAFINAJUS DATE;
    MI_DTV        PCK_SUBTIPOS.TI_DOBLE;
    MI_DIASTOTV   PCK_SUBTIPOS.TI_DOBLE;
    MI_PV         PCK_SUBTIPOS.TI_DOBLE;
    MI_FECHAINIPSN DATE;
    MI_DIASLNRPN  PCK_SUBTIPOS.TI_DOBLE;
    MI_PN         PCK_SUBTIPOS.TI_DOBLE;
    MI_FECHAINIPSCES DATE;
    MI_DIASLNRCES PCK_SUBTIPOS.TI_DOBLE;
    MI_ANTICIPOS  PCK_SUBTIPOS.TI_DOBLE;
BEGIN
    MI_SBM := 0; MI_GR := 0; MI_PT := 0;
    MI_FECHAFIN := TO_CHAR(LAST_DAY('01/'|| UN_MES || '/' || UN_ANIO),'DD/MM/YYYY');
    MI_DIASLNRBASP := 0;
    MI_SUELDOBON := PCK_NOMINA.FC_CN(1);
    MI_PORCBASP := 0;
    MI_SOBRESUELDO := 0;
    MI_AUXT := 0; MI_AUXA := 0; MI_PA := 0; MI_BASP := 0;
    PCK_NOMINA.GL_COMPANIA := UN_COMPANIA;
    PCK_NOMINA.GL_SPRC := UN_PROCESO;
    PCK_NOMINA.GL_SANO := UN_ANIO;
    PCK_NOMINA.GL_SMES := UN_MES;
    PCK_NOMINA.GL_SPER := UN_PERIODO;
    MI_EXTRAS := 0; MI_DIASLNRPS := 0; MI_DIASLNRPV := 0;
    MI_PS := 0; MI_DTV := 0; MI_DIASTOTV := 0; MI_PV := 0; MI_DIASLNRPN := 0;
    MI_PN := 0; MI_DIASLNRCES := 0; MI_ANTICIPOS := 0;
    IF UN_IDEMPLEADO <> 0 THEN
        PCK_NOMINA.PR_ACTUALIZARVACACIONES(UN_COMPANIA=>UN_COMPANIA,UN_EMPLEADO=>UN_IDEMPLEADO,UN_PERIODO=>UN_PERIODO,UN_FECHAFINPER=>PCK_NOMINA.GL_FECHAFIN,UN_USUARIO=>UN_USUARIO);
        MI_CONDI := ' AND PERSONAL.ID_DE_EMPLEADO = '||UN_IDEMPLEADO||'';
    ELSE
        MI_CONDI := ' AND PERSONAL.ID_DE_EMPLEADO <> 0';
    END IF;    
    <<INSERTAR_BECP>>
    MI_RSCONSULTA := 'SELECT PERSONAL.ID_DE_EMPLEADO,
                            PERSONAL.SALARIO_BASE_IBC,
                            PERSONAL.SINDICATO,
                            PERSONAL.FECHA_CUMPLIMIENTO_BONIFICACIO,
                            PERSONAL.FECHA_DE_INGRESO,
                            PERSONAL.NOMBRECOMPLETO,
                            PERSONAL.NUMERO_DCTO,
                            PERSONAL.ID_DE_TIPO,
                            PERSONAL.REGIMEN,
                            PERSONAL.FECHA_FINAL
                      FROM PERSONAL
                      LEFT JOIN CATEGORIA 
                      ON PERSONAL.COMPANIA = CATEGORIA.COMPANIA 
                      AND PERSONAL.ESCALAFON = CATEGORIA.ESCALAFON  
                      AND PERSONAL.ID_DE_CATEGORIA = CATEGORIA.ID_DE_CATEGORIA 
                      AND PERSONAL.ANO = CATEGORIA.ANO 
                      WHERE PERSONAL.COMPANIA = '''||UN_COMPANIA||''' 
                        AND PERSONAL.ESTADO_ACTUAL <> 3 
                        AND CATEGORIA.ANO = PERSONAL.ANO 
                        AND PERSONAL.TIPOVINCULACION <> ''23''
                        '||MI_CONDI||'
                    ORDER BY PERSONAL.NOMBRECOMPLETO';
    OPEN MI_RS FOR MI_RSCONSULTA;
    LOOP
        FETCH MI_RS INTO MI_RSIDDEMPLEADO,MI_RSSALARIOB,MI_RSSINDICATO,MI_RSFCUMPLBONIF,MI_RSFINGRESO,MI_RSNOMCOMPLETO,MI_RSNUMDCTO,MI_RSID_TIPO,MI_RSREGIMEN,MI_RSFFINAL;
        EXIT WHEN MI_RS%NOTFOUND; 
        MI_AC := PCK_NOMINA_COM1.FC_ACUMNOVEDADCONCEPTO(UN_COMPANIA,UN_ANIO,UN_MES,UN_PERIODO,UN_ANIO,UN_MES,UN_PERIODO,MI_RSIDDEMPLEADO);
        IF PCK_NOMINA.FC_CNA(10) > 0 THEN
            MI_SBM := PCK_NOMINA.FC_CNA(10);
            PCK_NOMINA.CN(1) := PCK_NOMINA.FC_CNA(10);
        ELSE
            MI_SBM := MI_RSSALARIOB;
            PCK_NOMINA.CN(1) := MI_RSSALARIOB;
        END IF;
        
        IF PCK_PARENTR.PARAMETRO31 = '844.000.755-4' AND MI_RSSINDICATO NOT IN(0) THEN
            MI_SBM := MI_SBM+MI_RSSALARIOB+PCK_SYSMAN_UTL.FC_IIF(MI_RSSALARIOB<=PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,88),PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,90),0);
        END IF;
        IF PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,13) <> 0 OR PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,62) <> 0 THEN
            MI_GR := ROUND(PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,62)=0 AND PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,13)>0 AND PCK_NOMINA_COM5.FC_ESFACTOR_SS(UN_COMPANIA,62)<>0,MI_RSSALARIOB*PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,13)/100,0),0);
        END IF;
        MI_GR := PCK_SYSMAN_UTL.FC_IIF(MI_GR=0,PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,62),MI_GR);
        MI_PT := PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,186)+ROUND(PCK_SYSMAN_UTL.FC_IIF(PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,186)=0 AND PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,85)>0 AND PCK_NOMINA_COM5.FC_ESFACTOR_SS(UN_COMPANIA,186)<>0,MI_RSSALARIOB*PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,85)/100,0),0);
        IF MI_RSFCUMPLBONIF IS NULL THEN
            MI_FINIPSBASP := MI_RSFINGRESO;
            IF MI_FECHAFIN-MI_FINIPSBASP > 360 THEN
                BEGIN
                    MI_MSG(1).CLAVE := 'FECHA';
                    MI_MSG(1).VALOR := MI_FECHAFIN-MI_FINIPSBASP;
                    MI_MSG(2).CLAVE := 'NOMBRECOMPLETO';
                    MI_MSG(2).VALOR := MI_RSNOMCOMPLETO;
                    MI_MSG(3).CLAVE := 'ID_EMPLEADO';
                    MI_MSG(3).VALOR := MI_RSIDDEMPLEADO;
                    MI_MSG(4).CLAVE := 'NUMERO_DCTO';
                    MI_MSG(4).VALOR := MI_RSNUMDCTO;
                    MI_MSG(5).CLAVE := 'ID_TIPO';
                    MI_MSG(5).VALOR := MI_RSID_TIPO;
                    PCK_NOMINA_COM7.PR_ALERTA(UN_COMPANIA => UN_COMPANIA,
                                              UN_MENSAJE_COD => PCK_ERRORES.ALERT_DIASFINICIOPS,
                                              UN_REEMPLAZOS => MI_MSG);
                END;
            END IF;
        ELSE
            MI_FINIPSBASP := MI_RSFCUMPLBONIF;
            IF MI_FECHAFIN-MI_FINIPSBASP > 380 THEN
                BEGIN
                    MI_MSG(1).CLAVE := 'FECHA';
                    MI_MSG(1).VALOR := MI_FECHAFIN-MI_FINIPSBASP;
                    MI_MSG(2).CLAVE := 'NOMBRECOMPLETO';
                    MI_MSG(2).VALOR := MI_RSNOMCOMPLETO;
                    MI_MSG(3).CLAVE := 'ID_EMPLEADO';
                    MI_MSG(3).VALOR := MI_RSIDDEMPLEADO;
                    MI_MSG(4).CLAVE := 'NUMERO_DCTO';
                    MI_MSG(4).VALOR := MI_RSNUMDCTO;
                    MI_MSG(5).CLAVE := 'ID_TIPO';
                    MI_MSG(5).VALOR := MI_RSID_TIPO;
                    PCK_NOMINA_COM7.PR_ALERTA (UN_COMPANIA => UN_COMPANIA,
                                               UN_MENSAJE_COD => PCK_ERRORES.ALERT_DIASFINICIOPS,
                                               UN_REEMPLAZOS => MI_MSG);
                END;
            END IF;
        END IF;
        MI_DIASLNRBASP := PCK_NOMINA_PROC01.FC_ACUMCONCEPTOLICENCIAS('359',PCK_SYSMAN_UTL.FC_ANIO(MI_FINIPSBASP),PCK_SYSMAN_UTL.FC_MES(MI_FINIPSBASP),1,UN_ANIO,UN_MES,UN_PERIODO,MI_RSIDDEMPLEADO,UN_PROCESO);
        MI_DIASLNRBASP := PCK_NOMINA.FC_CNA(359);
        MI_SUELDOBON := MI_SUELDOBON+PCK_NOMINA.GL_GRPNGV+PCK_NOMINA.GL_GRPNV+PCK_NOMINA.GL_GRPGV+PCK_SYSMAN_UTL.FC_IIF(PCK_PARENTR.PARAMETRO31 = '890704536-7',PCK_NOMINA.GL_VPA,0);
        PCK_NOMINA.GL_FECHAFIN1 := MI_FECHAFIN;
        PCK_NOMINA_COM3.PR_BASPFECHAEMPLEADO(UN_COMPANIA=>UN_COMPANIA,UN_ANO=>UN_ANIO,UN_SALARIO=>MI_SUELDOBON,UN_FECHA=>MI_FECHAFIN);
        MI_PORCBASP := PCK_NOMINA.GL_PORC_BASP;
        
        IF PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,410) > 0 THEN
            MI_SOBRESUELDO := ROUND(PCK_NOMINA.FC_CN(1)*20/100,0);
        ELSE
            MI_SOBRESUELDO := 0;
        END IF;
        IF PCK_PARENTR.PARAMETRO31 = '899999117-2' THEN
            IF PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,410) <> 0 THEN
                MI_SOBRESUELDO := ROUND(MI_SBM*20/100,0);     
            END IF;
        END IF;
        IF PCK_NOMINA.FC_CN(1) < 2 * PCK_NOMINA.FC_CN(201) THEN
            MI_AUXT := PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,81);
        END IF;
        IF (PCK_NOMINA.FC_CN(1)+PCK_NOMINA.FC_CN(186))<=PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,89) THEN
            MI_AUXA := PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,82);
        END IF;
        MI_PA := PCK_NOMINA_CALCULO.FC_VLRCONCEPTONOVEDADEMPLEADO(MI_RSIDDEMPLEADO,170);
        MI_BASP := ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMABONIFICACION(MI_RSIDDEMPLEADO)/12,0);
        IF MI_RSREGIMEN = 1 AND PCK_NOMINA.FC_CN(404) = 1 THEN
            MI_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_SYSMAN_UTL.FC_IIF(UN_MES=12,UN_ANIO,UN_ANIO-1),PCK_SYSMAN_UTL.FC_IIF(UN_MES=12,1,UN_MES+1),1,UN_ANIO,UN_MES,99,MI_RSIDDEMPLEADO);
            MI_EXTRAS := 0;
            PCK_NOMINA.CN(902) := ROUND((PCK_NOMINA.FC_CNA(70)+PCK_NOMINA.FC_CN(70))/12,0);
        ELSE
            MI_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,UN_ANIO,1,1,UN_ANIO,UN_MES,99,MI_RSIDDEMPLEADO);
            MI_EXTRAS := 0;
            MI_EXTRAS := ROUND((PCK_NOMINA.FC_CNA(70)+PCK_NOMINA.FC_CN(70))/12,0);
        END IF;
        IF UN_MES <= 6 THEN
            MI_FECHAINIPS := CASE WHEN MI_RSFINGRESO>TO_DATE('01/07/'||(UN_ANIO-1),'DD/MM/YYYY') THEN MI_RSFINGRESO ELSE TO_DATE('01/07/'||(UN_ANIO-1),'DD/MM/YYYY') END;
        ELSE 
            MI_FECHAINIPS := CASE WHEN MI_RSFINGRESO>TO_DATE('01/07/'||(UN_ANIO),'DD/MM/YYYY') THEN MI_RSFINGRESO ELSE TO_DATE('01/07/'||(UN_ANIO),'DD/MM/YYYY') END;
        END IF;
        MI_DIASLNRPS := PCK_NOMINA_PROC01.FC_ACUMCONCEPTOLICENCIAS('359',PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAINIPS),PCK_SYSMAN_UTL.FC_MES(MI_FECHAINIPS),1,UN_ANIO,UN_MES,UN_PERIODO,MI_RSIDDEMPLEADO,UN_PROCESO);
        MI_DIASLNRPS := PCK_NOMINA.FC_CNA(359);
        MI_PS := ROUND(PCK_NOMINA_PROC01.FC_VALORULTIMAPRIMASEMESTRAL(MI_RSIDDEMPLEADO)/12,0);
        IF MI_RSFFINAL IS NULL THEN
            MI_FECHAINIPSV := MI_RSFINGRESO;
        ELSE
            MI_FECHAINIPSV := MI_RSFFINAL+1;
        END IF;
        MI_DIASLNRPV := PCK_NOMINA_PROC01.FC_ACUMCONCEPTOLICENCIAS('359',PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAINIPSV),PCK_SYSMAN_UTL.FC_MES(MI_FECHAINIPSV),1,PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAINIPSV),PCK_SYSMAN_UTL.FC_MES(MI_FECHAINIPSV),3,MI_RSIDDEMPLEADO,UN_PROCESO);
        MI_DIASLNRPV := PCK_NOMINA.FC_CNA(359);
        IF PCK_NOMINA.FC_CNA(359) > PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAINIPSV+1,LAST_DAY(MI_FECHAINIPSV+1)) THEN
            MI_FECHAINIPSV := LAST_DAY(MI_FECHAINIPSV+1);
            MI_DIASLNRPV := PCK_NOMINA_PROC01.FC_ACUMCONCEPTOLICENCIAS('359',PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAINIPSV),PCK_SYSMAN_UTL.FC_MES(MI_FECHAINIPSV),1,UN_ANIO,UN_MES,UN_PERIODO,MI_RSIDDEMPLEADO,UN_PROCESO);
            MI_DIASLNRPV := PCK_NOMINA.FC_CNA(359)+(30-PCK_SYSMAN_UTL.FC_DIA(MI_FECHAINIPSV));
        ELSE
            MI_DIASLNRPV := PCK_NOMINA_PROC01.FC_ACUMCONCEPTOLICENCIAS('359',PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAINIPSV),PCK_SYSMAN_UTL.FC_MES(MI_FECHAINIPSV),1,UN_ANIO,UN_MES,UN_PERIODO,MI_RSIDDEMPLEADO,UN_PROCESO);
            MI_DIASLNRPV := PCK_NOMINA.FC_CNA(359);
        END IF;
        MI_FECHAFINAJUS := CASE WHEN TO_CHAR(MI_FECHAFIN,'DD')=31 THEN MI_FECHAFIN-1 ELSE MI_FECHAFIN END;
        MI_DTV := NVL(((TO_NUMBER(TO_CHAR(MI_FECHAFINAJUS,'YYYY'))-TO_NUMBER(TO_CHAR(MI_FECHAINIPSV,'YYYY')))*360+
                  (TO_NUMBER(TO_CHAR(MI_FECHAFINAJUS,'MM'))-TO_NUMBER(TO_CHAR(MI_FECHAINIPSV,'MM')))*30+
                  (TO_NUMBER(TO_CHAR(MI_FECHAFINAJUS,'DD'))-TO_NUMBER(TO_CHAR(MI_FECHAINIPSV,'DD'))))+1,0);
        MI_DIASTOTV := ROUND((15*MI_DTV/360),2);          
        MI_DIASTOTV := MI_DIASTOTV+ROUND((15*MI_DTV/360)-MI_DIASTOTV,2);
        MI_DIASTOTV := PCK_SYSMAN_UTL.FC_DIASMESCOMERCIAL(MI_FECHAFIN+1,PCK_SYSMAN_UTL.FC_FECHAFINALHABIL(UN_COMPANIA,MI_FECHAFIN,MI_DIASTOTV));
        MI_PV := ROUND(PCK_NOMINA_COM4.FC_VLRULTIMAPRIMADEVACACIONES(MI_RSIDDEMPLEADO)/12,0);
        MI_FECHAINIPSN := TO_DATE('01/01/' || UN_ANIO,'DD/MM/YYYY');
        IF MI_RSFINGRESO > MI_FECHAINIPSN THEN
            MI_FECHAINIPSN := MI_RSFINGRESO;
        ELSE
            MI_FECHAINIPSN := MI_FECHAINIPSN;
        END IF;
        MI_DIASLNRPN := PCK_NOMINA_PROC01.FC_ACUMCONCEPTOLICENCIAS('359',PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAINIPSN),PCK_SYSMAN_UTL.FC_MES(MI_FECHAINIPSN),1,UN_ANIO,UN_MES,UN_PERIODO,MI_RSIDDEMPLEADO,UN_PROCESO);
        MI_DIASLNRPN := PCK_NOMINA.FC_CNA(359);
        MI_PN := ROUND(PCK_NOMINA_CALCULO.FC_VALORULTIMAPRIMANAVIDAD(MI_RSIDDEMPLEADO)/12,0);
        IF MI_RSREGIMEN = 1 THEN
            MI_FECHAINIPSCES := MI_RSFINGRESO;
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA,MI_RSIDDEMPLEADO,MI_RSFINGRESO,MI_FECHAFIN);
        ELSE
            MI_FECHAINIPSCES := TO_DATE('01/01/' || UN_ANIO,'DD/MM/YYYY');
            MI_ANTICIPOS := PCK_NOMINA.FC_ACUMCESANTIA(UN_COMPANIA,MI_RSIDDEMPLEADO,MI_FECHAINIPSCES,MI_FECHAFIN);
        END IF;
        MI_DIASLNRCES := PCK_NOMINA_PROC01.FC_ACUMCONCEPTOLICENCIAS('359',PCK_SYSMAN_UTL.FC_ANIO(MI_FECHAINIPSCES),PCK_SYSMAN_UTL.FC_MES(MI_FECHAINIPSCES),1,UN_ANIO,UN_MES,UN_PERIODO,MI_RSIDDEMPLEADO,UN_PROCESO);
        MI_DIASLNRCES := PCK_NOMINA.FC_CNA(359);
        BEGIN
            MI_CAMPOS := 'COMPANIA,ID_DE_EMPLEADO,PROCESO,ANO,MES,PERIODO,SBM,GR,PT,DIASLNRBASP,PORCBASP,SOBRESUELDO,AUXT,AUXA,PA,BASP,
                          EXTRAS,FECHAINIPS,DIASLNRPS,PS,FECHAINIPSV,DIASLNRPV,DIASTOTV,PV,FECHAINIPSN,DIASLNRPN,PN,FECHAINIPSCES,
                          DIASLNRCES,ANTICIPOS,CREATED_BY,DATE_CREATED';
            MI_VALORES := '''' || UN_COMPANIA || ''', 
                          ' || MI_RSIDDEMPLEADO || ',
                          ' || UN_PROCESO || ',' || UN_ANIO || ',   
                          ' || UN_MES || ',' || UN_PERIODO || ',
                          ' || MI_SBM || ',' || MI_GR || ',' || MI_PT || ',
                          ' || MI_DIASLNRBASP || ',' || MI_PORCBASP || ',
                          ' || MI_SOBRESUELDO || ',' || MI_AUXT || ',
                          ' || MI_AUXA || ',' || MI_PA || ',
                          ' || MI_BASP || ',' || MI_EXTRAS || ',
                          ''' || MI_FECHAINIPS || ''',' || MI_DIASLNRPS || ',
                          ' || MI_PS || ',''' || MI_FECHAINIPSV || ''',
                          ' || MI_DIASLNRPV || ',' || NVL(MI_DIASTOTV,0) || ',
                          ' || MI_PV || ',''' || MI_FECHAINIPSN || ''',
                          ' || MI_DIASLNRPN || ',' || MI_PN || ',
                          ''' || MI_FECHAINIPSCES || ''',' || MI_DIASLNRCES || ',
                          ' || MI_ANTICIPOS || ',''' || UN_USUARIO || ''',  
                          SYSDATE ';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA => 'TMP_INF_BECPNOM',
                                                  UN_ACCION => 'I',
                                                  UN_CAMPOS => MI_CAMPOS,
                                                  UN_VALORES => MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_NOMINA;
        END;
    END LOOP INSERTAR_BECP;
END PR_INSERTINF_BECP;

PROCEDURE PR_CALCQUINQUENIOCARDER(
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
)
/*
    NAME : PR_CALCQUINQUENIOCARDER
    AUTHORS : JEIMMY CAROLINA ROJAS GUERRERO
    DATE : 28/05/2026
    DESCRIPTION : Procedimiento que calcula el quinquenio a partir de la tabla RANGOS_QUINQUENIO(CC 3953)
*/
AS
    MI_FECHAINICIO DATE;
    MI_FECHAFINAL  DATE;
    MI_ANTIGUEDAD  NUMBER := 0;
    MI_QUINQUENIO  NUMBER := 0;    
    MI_BASE        NUMBER := 0;
    MI_ENC         NUMBER := 0;
    MI_PORCENTAJE  NUMBER := 0;
    MI_VALOR       NUMBER := 0;    
    MI_CONCEPTO    NUMBER := 0;
    MI_FECHAQUINQ  DATE;
    MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR    PCK_SUBTIPOS.TI_CLAVEVALOR;
    CURSOR MI_VIG IS
        SELECT FECHA
        FROM RANGOS_QUINQUENIO
        WHERE COMPANIA = UN_COMPANIA
        GROUP BY FECHA ORDER BY FECHA DESC;
    CURSOR MI_RANGO (VFECHA DATE) IS
        SELECT RANGO_INI,RANGO_FIN,PORCENTAJE
        FROM RANGOS_QUINQUENIO
        WHERE COMPANIA = UN_COMPANIA
            AND FECHA = VFECHA
        ORDER BY RANGO_INI ASC;
BEGIN
    IF PCK_PARST.FC_PAR('CALCULAR QUINQUENIO','NO') = 'SI' THEN
        MI_FECHAINICIO := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO;
        MI_FECHAQUINQ := TO_DATE(PCK_SYSMAN_UTL.FC_DIA(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) || '/' || 
                                 PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).FECHA_DE_INGRESO) || '/' || 
                                 PCK_NOMINA.GL_SANO,'DD/MM/YYYY');
        MI_FECHAFINAL := PCK_NOMINA.GL_FECHAFIN1;       
        MI_ANTIGUEDAD := (EXTRACT(YEAR FROM MI_FECHAFINAL)-EXTRACT(YEAR FROM MI_FECHAINICIO))*360
                         +(EXTRACT(MONTH FROM MI_FECHAFINAL)-EXTRACT(MONTH FROM MI_FECHAINICIO))*30
                         +(EXTRACT(DAY FROM MI_FECHAFINAL)-EXTRACT(DAY FROM MI_FECHAINICIO));                         
        PCK_NOMINA.GL_AC := PCK_NOMINA.FC_ACUM(UN_COMPANIA,PCK_SYSMAN_UTL.FC_ANIO(PCK_NOMINA.GL_FECHAIR),PCK_SYSMAN_UTL.FC_MES(PCK_NOMINA.GL_FECHAIR),1,PCK_NOMINA.GL_SANO,PCK_NOMINA.GL_SMES,99,PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO);
        MI_ANTIGUEDAD := MI_ANTIGUEDAD-(PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(356)+PCK_NOMINA.FC_CN(357)+PCK_NOMINA.FC_CN(359)+PCK_NOMINA.FC_CN(339));        
        IF MI_ANTIGUEDAD BETWEEN 1800 AND 1830 THEN
            MI_QUINQUENIO := 5;
            MI_CONCEPTO := 180;
        ELSIF MI_ANTIGUEDAD BETWEEN 3600 AND 3650 THEN
            MI_QUINQUENIO := 10;
            MI_CONCEPTO := 181;
        ELSIF MI_ANTIGUEDAD BETWEEN 5400 AND 5475 THEN
            MI_QUINQUENIO := 15;
            MI_CONCEPTO := 182;
        ELSIF MI_ANTIGUEDAD BETWEEN 7200 AND 7300 THEN
            MI_QUINQUENIO := 20;
            MI_CONCEPTO := 183;
        ELSIF MI_ANTIGUEDAD BETWEEN 9000 AND 9125 THEN
            MI_QUINQUENIO := 25;
            MI_CONCEPTO := 184;
        ELSIF MI_ANTIGUEDAD BETWEEN 10800 AND 10950 THEN
            MI_QUINQUENIO := 30;
            MI_CONCEPTO := 185;
        END IF;        
        IF NVL(MI_QUINQUENIO,0) = 0 THEN
            RETURN;
        END IF;  
        SELECT COUNT(*) INTO MI_ENC
        FROM ENCARGOS
        WHERE COMPANIA = UN_COMPANIA
            AND ID_DE_EMPLEADO = PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO
            AND MI_FECHAQUINQ BETWEEN FECHAINICIO AND FECHAFINAL;
        MI_BASE := CASE WHEN MI_ENC > 0 THEN PCK_NOMINA.FC_CN(10) ELSE PCK_NOMINA.FC_CN(1) END;
        MI_PORCENTAJE := 0;
        MI_VALOR := 0;        
        FOR V IN MI_VIG LOOP
            FOR R IN MI_RANGO(V.FECHA) LOOP
                IF MI_QUINQUENIO BETWEEN R.RANGO_INI AND R.RANGO_FIN THEN
                    MI_PORCENTAJE := R.PORCENTAJE;
                    MI_VALOR := CASE WHEN PCK_NOMINA.FC_CN(MI_CONCEPTO) <> 0 THEN PCK_NOMINA.FC_CN(MI_CONCEPTO) ELSE MI_BASE*MI_PORCENTAJE/100 END;
                    EXIT;
                END IF;
            END LOOP;
            EXIT WHEN MI_PORCENTAJE > 0;
        END LOOP;        
        IF NVL(MI_VALOR,0) > 0 THEN
            PCK_NOMINA.CN(MI_CONCEPTO) := PCK_SYSMAN_UTL.FC_ROUND(MI_VALOR,0);
            PCK_NOMINA.CN(915) := MI_BASE;
            PCK_NOMINA.CN(925) := MI_ANTIGUEDAD;
            PCK_NOMINA.CN(926) := (PCK_NOMINA.FC_CNA(356)+PCK_NOMINA.FC_CNA(357)+PCK_NOMINA.FC_CNA(359)+PCK_NOMINA.FC_CNA(339)+PCK_NOMINA.FC_CN(356)+PCK_NOMINA.FC_CN(357)+PCK_NOMINA.FC_CN(359)+PCK_NOMINA.FC_CN(339));
            BEGIN
                BEGIN
                    MI_CAMPOS := 'FECHAANTIGQUINQUENIO = TO_DATE(''' || TO_CHAR(MI_FECHAQUINQ,'DD/MM/YYYY') || ''',''DD/MM/YYYY''),
                                  DATE_MODIFIED = SYSDATE,
                                  MODIFIED_BY = '''|| UN_USUARIO ||''' ';
                    MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''
                                     AND ID_DE_EMPLEADO = '||PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO||'';
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA => 'PERSONAL',
                                                          UN_ACCION => 'M',
                                                          UN_CAMPOS => MI_CAMPOS,
                                                          UN_CONDICION => MI_CONDICION);                 
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_NOMINA;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_NOMINA THEN
                MI_MSGERROR(1).CLAVE := 'EMPLEADO';
                MI_MSGERROR(1).VALOR := PCK_NOMINA.CPERSONAL(PCK_NOMINA.P).ID_DE_EMPLEADO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE,
                                           UN_ERROR_COD => PCK_ERRORES.ER_NOMINA_UPDATE_PERSONAL,
                                           UN_REEMPLAZOS => MI_MSGERROR);
            END;
        END IF;      
    END IF;
END PR_CALCQUINQUENIOCARDER;
END PCK_NOMINA_COM2;
