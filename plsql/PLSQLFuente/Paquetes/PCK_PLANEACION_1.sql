create or replace PACKAGE BODY "PCK_PLANEACION" AS

  --1
  FUNCTION FC_ELEMENTOSINV
  /*
    NAME              : FC_ELEMENTOSINV --> EN ACCESS ElementosInv
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRATION    : 19/01/2016
    TIME              : 5:56 PM
    SOURCE MODULE     : SysmanPc2015.09.02.accdb
    PARAMETERS        : UN_COMPANIA => Codigo de la compania desde la que se inicio sesion.

    MODIFIED BY       : PABLO ANDRES ESPITIA CUCA
    DATE MODIFIED     : 02/08/2017
    TIME              : 02:54 PM
    DESCRIPTION       : Estandar de programacion.
    @NAME: getElementoInventarioOC
  */
  (
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CLASEORDEN   IN D_ORDENDECOMPRA.CLASEORDEN%TYPE,
    UN_NUMEROORDEN  IN D_ORDENDECOMPRA.ORDENDECOMPRA%TYPE,
    UN_DEPENDENCIA  IN D_ORDENDECOMPRA.DEPENDENCIA%TYPE
  )
  RETURN VARCHAR2
  AS
    MI_ESPECIFICACION D_ORDENDECOMPRA.ESPECIFICACION%TYPE;
    MI_SQL            PCK_SUBTIPOS.TI_STRSQL;
    MI_ELEMENTOS      VARCHAR2(500 CHAR) DEFAULT ' ';
  BEGIN           
    <<DORDENESCOMPRA>>
    FOR RS IN (
      SELECT 
        INVENTARIO.NOMBRELARGO
       ,D_ORDENDECOMPRA.ESPECIFICACION
       ,D_ORDENDECOMPRA.ELEMENTO 
      FROM D_ORDENDECOMPRA 
       LEFT JOIN INVENTARIO 
         ON D_ORDENDECOMPRA.COMPANIA = INVENTARIO.COMPANIA 
        AND D_ORDENDECOMPRA.ELEMENTO = INVENTARIO.CODIGOELEMENTO 
      WHERE D_ORDENDECOMPRA.COMPANIA     = UN_COMPANIA
       AND D_ORDENDECOMPRA.CLASEORDEN    = UN_CLASEORDEN
       AND D_ORDENDECOMPRA.ORDENDECOMPRA = UN_NUMEROORDEN
       AND D_ORDENDECOMPRA.DEPENDENCIA   = UN_DEPENDENCIA
       AND ROWNUM                       <= 2
    ) LOOP
      MI_ELEMENTOS := MI_ELEMENTOS || CASE WHEN RS.ELEMENTO IN('9999999999999999')
                                           THEN NVL(RS.ESPECIFICACION,' ') || ' '
                                           ELSE NVL(RS.NOMBRELARGO,' ') || NVL(RS.ESPECIFICACION,' ')
                                      END;
    END LOOP DORDENESCOMPRA;

    RETURN MI_ELEMENTOS;
  END FC_ELEMENTOSINV;

  --2
  FUNCTION FC_ACTUALIZAREJECUCION
  /* 
    NAME 			     : ACTUALIZAREJECUCION -> Contratacion.ActualizarPlanCompras
    AUTHORS 			 : VÍCTOR JULIO MOLANO BOLÍVAR 
    DATE MIGRATION : 22/03/2016
    TIME				   : 11:10 AM
    SOURCE MODULE	 : SysmanPc2015.09.02.accdb
    DESCRIPTION		 : FUNCION QUE ACTUALIZA LA EJECUCIÓN DEL PLAN DE ADQUISICIONES CALCULANDO LOS VALORES COMPRADO Y 
                     VALORTOTALCOMPRADO DE CADA UNO DE LOS DETALLES DEL PLAN, PARA LUEGO ACTUALIZAR EL HEADER CON EL 
                     VLREJECUTADO TOTAL.
    PARAMETERS     : UN_COMPANIA => Codigo de la compania desde la que se inicio sesion.
                     UN_ANO      => Codigo del anio.
                     UN_USUARIO  => Codigo del usuario que desencadena el proceso.

    RETURN         : -1 => Proceso ejecutado satisfactoriamente.
                      0 => Excepcion capturada, enviar 0.

    MODIFIED BY		 : PABLO ANDRES ESPITIA CUCA
    DATE MODIFIED	 : 02/08/2017
    TIME				   : 03:40 PM
    MODIFICATIONS	 : Estandar de programacion y manejo de excepciones.
                     Adicion de campos de auditoria.
    @NAME: actualizarPlanCompras                 
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN PCK_SUBTIPOS.TI_LOGICO 
  AS    
    --Variables comunes
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_STRSQL           PCK_SUBTIPOS.TI_STRSQL;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    PR_CUATROPORMIL     PCK_SUBTIPOS.TI_PARAMETRO;                              --Controla el valor del parametro: MANEJA CUATRO POR MIL
    PR_MANSUBPROYECTOS  PCK_SUBTIPOS.TI_PARAMETRO;                              --Controla el valor del parametro: MANEJA SUBPROYECTOS EN PLAN DE COMPRAS
    MI_ASOCIADOPC       PCK_SUBTIPOS.TI_LOGICO;                                 --Indicador asociado al anio
    MI_CANT             PCK_SUBTIPOS.TI_ENTERO;                                 --Util para almacenar el valor de un COUNT en una consulta
    MI_TABLA_D          PCK_SUBTIPOS.TI_TABLA  DEFAULT 'DETALLE_PLAN_COMPRAS';  --Contiene el nombre de la tabla DETALLE_PLAN_COMPRAS
    MI_TABLA_P          PCK_SUBTIPOS.TI_TABLA  DEFAULT 'PLAN_DE_COMPRAS';       --Contine el nombre de la tabla PLAN_DE_COMPRAS
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;                             --Contiene los parametros a enviar al mensaje de error.
  BEGIN 
    PR_MANSUBPROYECTOS := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                                   ,UN_NOMBRE    => 'MANEJA SUBPROYECTOS EN PLAN DE COMPRAS'
                                                   ,UN_MODULO    => PCK_DATOS.MODULOPLANEACION
                                                   ,UN_FECHA_PAR => SYSDATE)
                             ,'NO');

    PR_CUATROPORMIL    := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => UN_COMPANIA
                                                   ,UN_NOMBRE   => 'MANEJA CUATRO POR MIL'
                                                   ,UN_MODULO    => PCK_DATOS.MODULOPLANEACION
                                                   ,UN_FECHA_PAR => SYSDATE)
                             ,'NO');
    BEGIN 
        BEGIN
            SELECT ASOCIADOPC
              INTO MI_ASOCIADOPC
              FROM ANO
             WHERE COMPANIA = UN_COMPANIA
               AND NUMERO   = UN_ANO;


          IF MI_ASOCIADOPC IN(0) THEN

            RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
          END IF;
       END; 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANEACION THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD  => SQLCODE,
                                  UN_ERROR_COD => PCK_ERRORES.ERR_PLANEACION_ACTUA_EJECU);
    END;


    <<D_ORDENESCOMPRA>>
    FOR RS IN (
      SELECT 
         D_ORDENDECOMPRA.DEPENDENCIA
        ,D_ORDENDECOMPRA.RUBROPPTO
        ,D_ORDENDECOMPRA.ANOPPTO
        ,PLAN_PRESUPUESTAL.NOMBRE
        ,D_ORDENDECOMPRA.ELEMENTO 
        ,EXTRACT(MONTH FROM ORDENDECOMPRA.FECHA) MES
        ,SUM(D_ORDENDECOMPRA.VLRTOTAL  ) TOTALCOMPRADO
        ,SUM(D_ORDENDECOMPRA.CANTIDAD  ) TOTALCANTIDAD
        ,SUM(D_ORDENDECOMPRA.CUATROXMIL) TOTALCUATROPORMIL
        ,SUM(D_ORDENDECOMPRA.VLRTOTAL + 
             D_ORDENDECOMPRA.CUATROXMIL) TCOMPRADOCUATROXMIL 
        ,CASE WHEN PR_MANSUBPROYECTOS IN('SI')
              THEN D_ORDENDECOMPRA.CODIGOSUBPROYECTO
              ELSE 0
         END CODIGOSUBPROYECTO
      FROM D_ORDENDECOMPRA 
        INNER JOIN PLAN_PRESUPUESTAL
           ON D_ORDENDECOMPRA.COMPANIA  = PLAN_PRESUPUESTAL.COMPANIA
          AND D_ORDENDECOMPRA.ANOPPTO   = PLAN_PRESUPUESTAL.ANO
          AND D_ORDENDECOMPRA.RUBROPPTO = PLAN_PRESUPUESTAL.CODIGO
        INNER JOIN ORDENDECOMPRA 
           ON D_ORDENDECOMPRA.COMPANIA      = ORDENDECOMPRA.COMPANIA 
          AND D_ORDENDECOMPRA.CLASEORDEN    = ORDENDECOMPRA.CLASEORDEN 
          AND D_ORDENDECOMPRA.ORDENDECOMPRA = ORDENDECOMPRA.NUMERO
      WHERE D_ORDENDECOMPRA.COMPANIA               = UN_COMPANIA 
        AND EXTRACT(YEAR FROM ORDENDECOMPRA.FECHA) = UN_ANO 
        AND D_ORDENDECOMPRA.RUBROPPTO IS NOT NULL
        AND ORDENDECOMPRA.ACTUALIZAPLANDECOMPRAS NOT IN(0)
      GROUP BY 
         D_ORDENDECOMPRA.DEPENDENCIA
        ,D_ORDENDECOMPRA.RUBROPPTO
        ,D_ORDENDECOMPRA.ANOPPTO
        ,PLAN_PRESUPUESTAL.NOMBRE
        ,D_ORDENDECOMPRA.ELEMENTO
        ,EXTRACT(MONTH FROM ORDENDECOMPRA.FECHA)
        ,CASE WHEN PR_MANSUBPROYECTOS IN('SI')
              THEN D_ORDENDECOMPRA.CODIGOSUBPROYECTO
              ELSE 0
         END      
    )
    LOOP
      SELECT COUNT(1)
      INTO MI_CANT --Asigna la cantidad de detalles del plan de cuentas
      FROM DETALLE_PLAN_COMPRAS
      WHERE COMPANIA    = UN_COMPANIA
        AND ANO         = RS.ANOPPTO
        AND RUBRO       = RS.RUBROPPTO
        AND CODIGO      = RS.ELEMENTO
        AND MES         = RS.MES
        AND DEPENDENCIA = RS.DEPENDENCIA
        AND SUBPROYECTO = CASE WHEN PR_MANSUBPROYECTOS IN('SI')
                               THEN RS.CODIGOSUBPROYECTO
                               ELSE SUBPROYECTO
                          END; 

      --Si los detalles no existen
      IF MI_CANT IN(0) THEN
        --Verifica si existe el plan de cuentas
        SELECT COUNT(1) 
        INTO MI_CANT 
        FROM PLAN_DE_COMPRAS 
        WHERE COMPANIA = UN_COMPANIA 
          AND ANO      = RS.ANOPPTO 
          AND CODIGO   = RS.RUBROPPTO;

        IF MI_CANT IN(0) THEN --Crear plan de cuentas
          MI_CAMPOS := 'COMPANIA
                       ,CODIGO
                       ,ANO
                       ,NOMBRE
                       ,DEPENDENCIA
                       ,SUBPROYECTO
                       ,CREATED_BY
                       ,DATE_CREATED';   

          MI_VALORES := ''''||UN_COMPANIA   ||'''
                        ,'''||RS.RUBROPPTO  ||'''
                        ,  '||RS.ANOPPTO    ||'
                        ,'''||RS.NOMBRE     ||'''
                        ,'''||RS.DEPENDENCIA||'''
                        ,0
                        ,'''||UN_USUARIO    ||'''
                        ,SYSDATE';

          BEGIN
            BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_P
                                                   ,UN_ACCION    => 'I'
                                                   ,UN_CAMPOS    => MI_CAMPOS
                                                   ,UN_VALORES   => MI_VALORES);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
            END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANEACION THEN
            MI_MSGERROR(1).CLAVE := 'CODIGO';
            MI_MSGERROR(1).VALOR := RS.RUBROPPTO;
            MI_MSGERROR(2).CLAVE := 'ANIO';
            MI_MSGERROR(2).VALOR := RS.ANOPPTO;

            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERR_P_FCAE_I_PLANDECOMPRA
                                      ,UN_TABLAERROR => MI_TABLA_P
                                      ,UN_REEMPLAZOS => MI_MSGERROR);
          END;          
        END IF;

        --Insertar detalle al plan de cuentas
        MI_CAMPOS  := 'COMPANIA
                      ,CODIGO
                      ,DEPENDENCIA
                      ,ANO
                      ,MES
                      ,ENTRADAS
                      ,SALIDAS
                      ,VALOR_UNITARIO
                      ,VALORACOMPRAR
                      ,RUBRO
                      ,COMPRADO
                      ,VALORTOTALCOMPRADO'||
                       CASE WHEN PR_MANSUBPROYECTOS IN('SI')
                            THEN ',SUBPROYECTO'
                       END                ||'
                      ,CREATED_BY
                      ,DATE_CREATED';

        MI_VALORES := ''''||UN_COMPANIA     ||'''
                      ,'''||RS.ELEMENTO     ||'''
                      ,'''||RS.DEPENDENCIA  ||'''
                      ,  '||RS.ANOPPTO      ||'
                      ,  '||RS.MES          ||'
                      ,0
                      ,0
                      ,0
                      ,0
                      ,'''||RS.RUBROPPTO    ||'''
                      ,  '||RS.TOTALCANTIDAD||'
                      ,  '||CASE WHEN PR_CUATROPORMIL IN('SI') 
                                 THEN RS.TCOMPRADOCUATROXMIL
                                 ELSE RS.TOTALCOMPRADO
                            END             ||
                            CASE WHEN PR_MANSUBPROYECTOS IN('SI')
                                 THEN ','''||RS.CODIGOSUBPROYECTO||''''
                            END             ||'
                      ,'''||UN_USUARIO      ||'''
                      ,SYSDATE'; 

        BEGIN
          BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_D
                                                 ,UN_ACCION    => 'I'
                                                 ,UN_CAMPOS    => MI_CAMPOS
                                                 ,UN_VALORES   => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANEACION THEN
          MI_MSGERROR(1).CLAVE := 'CODIGO';
          MI_MSGERROR(1).VALOR := RS.ELEMENTO;
          MI_MSGERROR(2).CLAVE := 'RUBRO';
          MI_MSGERROR(2).VALOR := RS.RUBROPPTO;
          MI_MSGERROR(3).CLAVE := 'DEPENDENCIA';
          MI_MSGERROR(3).VALOR := RS.DEPENDENCIA;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_P_FCAE_I_DETALLEPLANCOMPRA
                                    ,UN_TABLAERROR => MI_TABLA_D
                                    ,UN_REEMPLAZOS => MI_MSGERROR);
        END;

      ELSE --Si existe se actualizan los detalles del plan de compras
        MI_CAMPOS := 'MODIFIED_BY        = '''||UN_USUARIO    ||'''
                     ,DATE_MODIFIED      = SYSDATE
                     ,COMPRADO           = '||RS.TOTALCANTIDAD||'
                     ,VALORTOTALCOMPRADO = '||CASE WHEN PR_CUATROPORMIL IN('SI') 
                                                   THEN RS.TCOMPRADOCUATROXMIL
                                                   ELSE RS.TOTALCOMPRADO
                                              END;

        MI_CONDICION := 'COMPANIA    = '''||UN_COMPANIA   ||'''
                     AND ANO         =   '||RS.ANOPPTO    ||'   
                     AND RUBRO       = '''||RS.RUBROPPTO  ||''' 
                     AND CODIGO      = '''||RS.ELEMENTO   ||''' 
                     AND MES         =   '||RS.MES        ||'   
                     AND DEPENDENCIA = '''||RS.DEPENDENCIA||''' ';


        MI_CONDICION := MI_CONDICION || CASE WHEN PR_MANSUBPROYECTOS IN('SI')
                                             THEN 'AND SUBPROYECTO = '''||RS.CODIGOSUBPROYECTO||''''
                                        END;   

        BEGIN
          BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_D
                                                 ,UN_ACCION    => 'M'
                                                 ,UN_CAMPOS    => MI_CAMPOS
                                                 ,UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANEACION THEN
          MI_MSGERROR(1).CLAVE := 'CODIGO';
          MI_MSGERROR(1).VALOR := RS.ELEMENTO;
          MI_MSGERROR(2).CLAVE := 'RUBRO';
          MI_MSGERROR(2).VALOR := RS.RUBROPPTO;
          MI_MSGERROR(3).CLAVE := 'DEPENDENCIA';
          MI_MSGERROR(3).VALOR := RS.DEPENDENCIA;

          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERR_P_FCAE_M_DETALLEPLANCOMPRA
                                    ,UN_TABLAERROR => MI_TABLA_D
                                    ,UN_REEMPLAZOS => MI_MSGERROR);
        END;            
      END IF;


    END LOOP D_ORDENESCOMPRA;

    <<TOTALORDENESCOMPRA>>
    FOR RS IN(
      SELECT DOC.RUBROPPTO,
        SUM(DOC.VLRTOTAL) TOTALCOMPRADOC
      FROM D_ORDENDECOMPRA DOC
        LEFT JOIN ORDENDECOMPRA OC
          ON DOC.COMPANIA      = OC.COMPANIA
         AND DOC.CLASEORDEN    = OC.CLASEORDEN
         AND DOC.ORDENDECOMPRA = OC.NUMERO
      WHERE DOC.COMPANIA                = UN_COMPANIA
        AND EXTRACT(YEAR FROM OC.FECHA) = UN_ANO
        AND DOC.RUBROPPTO IS NOT NULL
        AND OC.ACTUALIZAPLANDECOMPRAS NOT IN(0)
      GROUP BY DOC.RUBROPPTO
    )
    LOOP
      MI_CAMPOS := 'VLREJECUTADO  = '||RS.TOTALCOMPRADOC||'
                   ,MODIFIED_BY   = '''||UN_USUARIO     ||'''
                   ,DATE_MODIFIED = SYSDATE';

      MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA   || '''
                   AND CODIGO   = ''' || RS.RUBROPPTO  || ''' 
                   AND ANO      =   ' || UN_ANO;

      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA_P
                                               ,UN_ACCION    => 'M'
                                               ,UN_CAMPOS    => MI_CAMPOS
                                               ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
        END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANEACION THEN
        MI_MSGERROR(1).CLAVE := 'CODIGO';
        MI_MSGERROR(1).VALOR := RS.RUBROPPTO;
        MI_MSGERROR(2).CLAVE := 'ANIO';
        MI_MSGERROR(2).VALOR := UN_ANO;

        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_P_FCAE_M_PLANDECOMPRA
                                  ,UN_TABLAERROR => MI_TABLA_P
                                  ,UN_REEMPLAZOS => MI_MSGERROR);
      END;  
    END LOOP TOTALORDENESCOMPRA;

    RETURN -1; --Proceso completo ejecutado
  END FC_ACTUALIZAREJECUCION;

  --3
  PROCEDURE PR_CARGAVALORESPDA
  /* 
    NAME 			      : CARGAVALORESPDA -> En Access, se encuentra dentro de la función Codigo_AfterUpdate del formulario Plan_De_ComprasElem
    AUTHORS 			  : DIEGO FERNANDO MALDONADO MORALES
    DATE MIGRATION	: 28/03/2016
    TIME				    : 11:30 AM
    SOURCE MODULE  	: SysmanPc2015.09.02
    DESCRIPTION	  	: Procedimiento que define los parámetros para la consulta de la vista V_RESUMENPPTO_P
    PARAMETERS      :

    MODIFIED BY			: PABLO ANDRES ESPITIA CUCA
    DATE MODIFIED	  : 03/08/2017
    TIME				    : 04:26 PM
    MODIFICATIONS	  : Estandar de programacion.
    @NAME:  No Uitilizar desde JAVA revisar
  */
  (
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_MODULO   IN PCK_SUBTIPOS.TI_MODULO,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_RUBRO    IN NUMBER
  )
  AS
    MI_MES NUMBER(2,0);
  BEGIN
    MI_MES := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA
                                   ,UN_NOMBRE    => 'NUMERO DE MES APROPIACION PLAN DE COMPRAS'
                                   ,UN_MODULO    => UN_MODULO
                                   ,UN_FECHA_PAR => SYSDATE);

    PCK_ENTORNO.PR_SETCOMPANIA  (UN_COMPANIA   => UN_COMPANIA);
    PCK_ENTORNO.PR_SETYEAR      (UN_ANIO       => UN_ANO     );
    PCK_ENTORNO.PR_SETRUBRO     (UN_RUBRO      => UN_RUBRO   );
    PCK_ENTORNO.PR_SETMESINICIAL(UN_MESINICIAL => MI_MES     );
    PCK_ENTORNO.PR_SETMESFINAL  (UN_MESFINAL   => MI_MES     );
  END;

--4  
PROCEDURE PR_REGISTRARACTPLANADQUI
/*
    NAME              : PR_REGISTRARACTPLANADQUI
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 11/19/2017
    TIME              : 08:00 AM  
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     :
    DESCRIPTION       : REGISTRA Y ACTUALIZA EL PLAN DE COMPRAS

    @NAME:    registrarActualizacionPlanAdquisiciones 
    @METHOD:  PUT
  */
(
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_REALIZADA        IN ACTPLAN_DE_COMPRAS.REALIZADA%TYPE,
  UN_ACTUALIZACION    IN ACTPLAN_DE_COMPRAS.ACTUALIZACION%TYPE,
  UN_ANO              IN ACTPLAN_DE_COMPRAS.ANO%TYPE,
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;
  MI_MERGEUSING         PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE        PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE        PCK_SUBTIPOS.TI_MERGEEXISTE;
BEGIN 

  MI_TABLA := 'ACTPLAN_DE_COMPRAS';
  MI_CAMPOS := 'REALIZADA       = '|| UN_REALIZADA ||', 
                DATE_MODIFIED   = SYSDATE, 
                MODIFIED_BY     = '''||UN_USUARIO||'''' ;
  MI_CONDICION :=   'ACTPLAN_DE_COMPRAS.COMPANIA           = '''||  UN_COMPANIA       ||'''
                    AND ACTPLAN_DE_COMPRAS.ACTUALIZACION   = '  ||  UN_ACTUALIZACION  ||'
                    AND ACTPLAN_DE_COMPRAS.ANO             = '  ||  UN_ANO;
        BEGIN
          BEGIN
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                         UN_ACCION    =>  'M', 
                                         UN_CAMPOS    =>  MI_CAMPOS,
                                         UN_CONDICION =>  MI_CONDICION);                 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
          END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANEACION THEN    
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_PLANEACION_ACTPLANCOMPRAS
              );
        END;

     MI_TABLA := 'DETALLE_PLAN_COMPRAS';
     IF  PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA, 
                               UN_NOMBRE      => 'MANEJA SUBPROYECTOS EN PLAN DE COMPRAS', 
                               UN_MODULO      => PCK_DATOS.MODULOPLANEACION,
                               UN_FECHA_PAR   => SYSDATE)= 'NO' THEN

        MI_MERGEUSING := 'SELECT  DETALLE_PLAN_COMPRAS.COMPANIA,
                                  DETALLE_PLAN_COMPRAS.RUBRO,
                                  DETALLE_PLAN_COMPRAS.CODIGO,
                                  DETALLE_PLAN_COMPRAS.ANO,
                                  DETALLE_PLAN_COMPRAS.MES,
                                  DETALLE_PLAN_COMPRAS.ENTRADAS,
                                  DETALLE_PLAN_COMPRAS.SALIDAS,
                                  DETALLE_PLAN_COMPRAS.VALOR_ENTRADAS,
                                  DETALLE_PLAN_COMPRAS.VALOR_UNITARIO,
                                  DETALLE_PLAN_COMPRAS.VALOR_SALIDAS,
                                  ACTDETPLAN_COMPRAS.VALORTOTAL,
                                  ACTDETPLAN_COMPRAS.CANTIDAD,
                                  ACTDETPLAN_COMPRAS.TIPO
                          FROM DETALLE_PLAN_COMPRAS
                          INNER JOIN ACTDETPLAN_COMPRAS
                            ON DETALLE_PLAN_COMPRAS.COMPANIA       = ACTDETPLAN_COMPRAS.COMPANIA
                            AND DETALLE_PLAN_COMPRAS.ANO           = ACTDETPLAN_COMPRAS.ANO
                            AND DETALLE_PLAN_COMPRAS.DEPENDENCIA   = ACTDETPLAN_COMPRAS.DEPENDENCIA
                            AND DETALLE_PLAN_COMPRAS.RUBRO         = ACTDETPLAN_COMPRAS.RUBRO
                            AND DETALLE_PLAN_COMPRAS.CODIGO        = ACTDETPLAN_COMPRAS.CODIGO
                            AND DETALLE_PLAN_COMPRAS.MES           = ACTDETPLAN_COMPRAS.MES
                          WHERE ACTDETPLAN_COMPRAS.COMPANIA       = '''||UN_COMPANIA||'''
                            AND ACTDETPLAN_COMPRAS.ACTUALIZACION  = '||UN_ACTUALIZACION||'
                            AND REALIZADA                         = 0
                            AND DETALLE_PLAN_COMPRAS.ANO          = '||UN_ANO;

        MI_MERGEENLACE:= 'TABLA.COMPANIA    = VISTA.COMPANIA 
                          AND TABLA.ANO     = VISTA.ANO
                          AND TABLA.RUBRO   = VISTA.RUBRO 
                          AND TABLA.CODIGO  = VISTA.CODIGO
                          AND TABLA.MES     = VISTA.MES';

        MI_MERGEEXISTE :='UPDATE  SET
                          TABLA.ENTRADAS        = TABLA.ENTRADAS + (CASE WHEN VISTA.TIPO=''A'' THEN VISTA.CANTIDAD ELSE 0 END),
                          TABLA.SALIDAS         = TABLA.SALIDAS + (CASE WHEN VISTA.TIPO=''D'' THEN VISTA.CANTIDAD ELSE 0 END), 
                          TABLA.VALOR_ENTRADAS  = TABLA.VALOR_ENTRADAS + (CASE WHEN VISTA.TIPO=''A'' THEN VISTA.VALORTOTAL  ELSE 0 END),
                          TABLA.VALOR_UNITARIO  = VISTA.VALOR_UNITARIO,
                          TABLA.VALOR_SALIDAS   = TABLA.VALOR_SALIDAS + (CASE WHEN VISTA.TIPO=''D'' THEN VISTA.VALORTOTAL ELSE 0 END)';

      BEGIN
          BEGIN
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       =>  MI_TABLA, 
                                         UN_ACCION      =>  'MM', 
                                         UN_MERGEUSING  =>  MI_MERGEUSING,
                                         UN_MERGEENLACE =>  MI_MERGEENLACE,
                                         UN_MERGEEXISTE =>  MI_MERGEEXISTE);                 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
          END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANEACION THEN    
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_P_FCAE_ACTDETPLANCOMPRAS
              );
        END;

     MI_CAMPOS  := 'COMPANIA,
                    CODIGO,
                    DEPENDENCIA,
                    RESPONSABLE,
                    SUCURSAL,
                    ANO,
                    MES,
                    CANTIDAD,
                    ENTRADAS,
                    SALIDAS,
                    VALOR_UNITARIO,
                    VALORACOMPRAR,
                    RUBRO,
                    VALOR_ENTRADAS,
                    DATE_CREATED,
                    CREATED_BY';

     MI_VALORES := 'SELECT  ACTDETPLAN_COMPRAS.COMPANIA,
                            ACTDETPLAN_COMPRAS.CODIGO,
                            ACTDETPLAN_COMPRAS.DEPENDENCIA,
                            PLAN_DE_COMPRAS.RESPONSABLE,
                            PLAN_DE_COMPRAS.SUCURSAL,
                            ACTDETPLAN_COMPRAS.ANO,
                            ACTDETPLAN_COMPRAS.MES,
                            ACTDETPLAN_COMPRAS.CANTIDAD,
                            (CASE
                              WHEN ACTDETPLAN_COMPRAS.TIPO=''A''
                              THEN ACTDETPLAN_COMPRAS.CANTIDAD
                              ELSE 0
                            END) ENTRADAS ,
                            (CASE
                              WHEN ACTDETPLAN_COMPRAS.TIPO=''D''
                              THEN ACTDETPLAN_COMPRAS.CANTIDAD
                              ELSE 0
                            END) SALIDAS ,
                            ACTDETPLAN_COMPRAS.VALOR_UNITARIO,
                            ACTDETPLAN_COMPRAS.VALORTOTAL ,
                            ACTDETPLAN_COMPRAS.RUBRO,
                            ACTDETPLAN_COMPRAS.VALORTOTAL,
                            SYSDATE,
                            '''||UN_USUARIO||'''
                  FROM ACTDETPLAN_COMPRAS
                  INNER JOIN PLAN_DE_COMPRAS
                    ON ACTDETPLAN_COMPRAS.COMPANIA  = PLAN_DE_COMPRAS.COMPANIA
                    AND ACTDETPLAN_COMPRAS.RUBRO    = PLAN_DE_COMPRAS.CODIGO
                    AND ACTDETPLAN_COMPRAS.ANO      = PLAN_DE_COMPRAS.ANO
                  WHERE ACTDETPLAN_COMPRAS.COMPANIA      = '''||  UN_COMPANIA      ||'''
                    AND ACTDETPLAN_COMPRAS.ACTUALIZACION = '  ||  UN_ACTUALIZACION ||'
                    AND ACTDETPLAN_COMPRAS.REALIZADA     = 0';
      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                         UN_ACCION    => 'IS',
                                         UN_CAMPOS    => MI_CAMPOS,
                                         UN_VALORES   => MI_VALORES);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
            END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANEACION THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_P_FCAE_INSDETPLANCOMPRAS
            );
        END;
     ELSE
        MI_MERGEUSING := 'SELECT  DETALLE_PLAN_COMPRAS.COMPANIA,
                                  DETALLE_PLAN_COMPRAS.CODIGO,
                                  DETALLE_PLAN_COMPRAS.SUBPROYECTO,
                                  DETALLE_PLAN_COMPRAS.RUBRO,
                                  DETALLE_PLAN_COMPRAS.DEPENDENCIA,
                                  DETALLE_PLAN_COMPRAS.ANO,
                                  DETALLE_PLAN_COMPRAS.MES,
                                  DETALLE_PLAN_COMPRAS.ENTRADAS,
                                  DETALLE_PLAN_COMPRAS.SALIDAS,
                                  DETALLE_PLAN_COMPRAS.VALOR_ENTRADAS,
                                  DETALLE_PLAN_COMPRAS.VALOR_UNITARIO,
                                  DETALLE_PLAN_COMPRAS.VALOR_SALIDAS,
                                  ACTDETPLAN_COMPRAS.VALORTOTAL,
                                  ACTDETPLAN_COMPRAS.CANTIDAD,
                                  ACTDETPLAN_COMPRAS.TIPO
                          FROM DETALLE_PLAN_COMPRAS
                          INNER JOIN ACTDETPLAN_COMPRAS
                            ON DETALLE_PLAN_COMPRAS.COMPANIA       = ACTDETPLAN_COMPRAS.COMPANIA
                            AND DETALLE_PLAN_COMPRAS.CODIGO        = ACTDETPLAN_COMPRAS.CODIGO
                            AND DETALLE_PLAN_COMPRAS.SUBPROYECTO   = ACTDETPLAN_COMPRAS.SUBPROYECTO
                            AND DETALLE_PLAN_COMPRAS.RUBRO         = ACTDETPLAN_COMPRAS.RUBRO
                            AND DETALLE_PLAN_COMPRAS.DEPENDENCIA   = ACTDETPLAN_COMPRAS.DEPENDENCIA
                            AND DETALLE_PLAN_COMPRAS.ANO           = ACTDETPLAN_COMPRAS.ANO
                            AND DETALLE_PLAN_COMPRAS.MES           = ACTDETPLAN_COMPRAS.MES
                          WHERE ACTDETPLAN_COMPRAS.COMPANIA       = '''||  UN_COMPANIA       ||'''
                            AND ACTDETPLAN_COMPRAS.ACTUALIZACION  = '  ||  UN_ACTUALIZACION  ||'        
                            AND REALIZADA                         = 0
                            AND DETALLE_PLAN_COMPRAS.ANO          = '||UN_ANO;

          MI_MERGEENLACE := ' TABLA.COMPANIA        = VISTA.COMPANIA
                              AND TABLA.CODIGO      = VISTA.CODIGO
                              AND TABLA.SUBPROYECTO = VISTA.SUBPROYECTO
                              AND TABLA.RUBRO       = VISTA.RUBRO
                              AND TABLA.DEPENDENCIA = VISTA.DEPENDENCIA
                              AND TABLA.ANO         = VISTA.ANO
                              AND TABLA.MES         = VISTA.MES';

          MI_MERGEEXISTE := 'UPDATE  SET
                            TABLA.ENTRADAS        = TABLA.ENTRADAS + (CASE WHEN VISTA.TIPO=''A'' THEN VISTA.CANTIDAD ELSE 0 END),
                            TABLA.SALIDAS         = TABLA.SALIDAS + (CASE WHEN VISTA.TIPO=''D'' THEN VISTA.CANTIDAD ELSE 0 END), 
                            TABLA.VALOR_ENTRADAS  = TABLA.VALOR_ENTRADAS + (CASE WHEN VISTA.TIPO=''A'' THEN VISTA.VALORTOTAL  ELSE 0 END),
                            TABLA.VALOR_UNITARIO  = VISTA.VALOR_UNITARIO,
                            TABLA.VALOR_SALIDAS   = TABLA.VALOR_SALIDAS + (CASE WHEN VISTA.TIPO=''D'' THEN VISTA.VALORTOTAL ELSE 0 END)';


          BEGIN
          BEGIN
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       =>  MI_TABLA, 
                                         UN_ACCION      =>  'MM', 
                                         UN_MERGEUSING  =>  MI_MERGEUSING,
                                         UN_MERGEENLACE =>  MI_MERGEENLACE,
                                         UN_MERGEEXISTE =>  MI_MERGEEXISTE);                 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
          END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANEACION THEN    
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_P_FCAE_ACTDETPLANCOMPRAS
              );
        END;
        MI_CAMPOS := 'COMPANIA,
                      CODIGO, 
                      DEPENDENCIA, 
                      RESPONSABLE, 
                      SUCURSAL, 
                      ANO, 
                      MES, 
                      CANTIDAD, 
                      ENTRADAS, 
                      SALIDAS, 
                      VALOR_UNITARIO, 
                      VALORACOMPRAR,  
                      RUBRO, 
                      SUBPROYECTO,
                      DATE_CREATED,
                      CREATED_BY';


            MI_VALORES := 'SELECT ACTDETPLAN_COMPRAS.COMPANIA,
                                  ACTDETPLAN_COMPRAS.CODIGO,
                                  ACTDETPLAN_COMPRAS.DEPENDENCIA,
                                  PLAN_DE_COMPRAS.RESPONSABLE,
                                  PLAN_DE_COMPRAS.SUCURSAL,
                                  ACTDETPLAN_COMPRAS.ANO,
                                  ACTDETPLAN_COMPRAS.MES,
                                  ACTDETPLAN_COMPRAS.CANTIDAD,
                                  (CASE
                                  WHEN ACTDETPLAN_COMPRAS.TIPO=''A''
                                  THEN ACTDETPLAN_COMPRAS.CANTIDAD
                                  ELSE 0
                                  END),
                                  (CASE
                                  WHEN ACTDETPLAN_COMPRAS.TIPO=''D''
                                  THEN ACTDETPLAN_COMPRAS.CANTIDAD
                                  ELSE 0
                                  END),
                                  ACTDETPLAN_COMPRAS.VALOR_UNITARIO,
                                  ACTDETPLAN_COMPRAS.VALORTOTAL,
                                  ACTDETPLAN_COMPRAS.RUBRO,
                                  ACTDETPLAN_COMPRAS.SUBPROYECTO,
                                  SYSDATE,
                                  '''||UN_USUARIO||'''
                            FROM ACTDETPLAN_COMPRAS
                            INNER JOIN PLAN_DE_COMPRAS
                              ON ACTDETPLAN_COMPRAS.COMPANIA  = PLAN_DE_COMPRAS.COMPANIA
                              AND ACTDETPLAN_COMPRAS.RUBRO    = PLAN_DE_COMPRAS.CODIGO
                              AND ACTDETPLAN_COMPRAS.ANO      = PLAN_DE_COMPRAS.ANO
                            WHERE ACTDETPLAN_COMPRAS.COMPANIA    = '''||  UN_COMPANIA       ||'''
                              AND ACTDETPLAN_COMPRAS.ACTUALIZACION = '  ||  UN_ACTUALIZACION  ||'
                              AND ACTDETPLAN_COMPRAS.REALIZADA     = 0';

      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                         UN_ACCION    => 'IS',
                                         UN_CAMPOS    => MI_CAMPOS,
                                         UN_VALORES   => MI_VALORES);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
            END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANEACION THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERR_P_FCAE_INSDETPLANCOMPRAS
            );
        END; 
     END IF;

END PR_REGISTRARACTPLANADQUI;

  -- 5 calcularValorEjecutado
  FUNCTION FC_CALC_VLR_EJECUTADO
  /*
    NAME              : FC_CALC_VLR_EJECUTADO
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
    DATE MIGRATION    : 13/09/2017
    TIME              : 08:33 AM
    DESCRIPTION       : Calcula el valor adquirido  o ejecutado para el plan de compras.
    PARAMETROS DE ENTRADA: 
      UN_COMPANIA: Código de la compañía.
      UN_ANIO:      Año del plan de compras.
      UN_CODIGO:   Código del plan de compras.

    @NAME: calcularValorEjecutado
    @METHOD: get
  */
  (
    UN_COMPANIA                  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO                      IN PCK_SUBTIPOS.TI_ANIO,
    UN_CODIGO                    IN PLAN_DE_COMPRAS.CODIGO%TYPE
  )
  RETURN PCK_SUBTIPOS.TI_DOBLE
  AS
    MI_VLR_EJECUTADO             PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
    MI_NOMBRE_PC                 PLAN_DE_COMPRAS.NOMBRE%TYPE;
  BEGIN
    WITH CON_I_PLANDECOMPRASGRUPOCR AS
    (
      SELECT V_IPLANCOMPRASESPECIALCR.COMPANIA,
             V_IPLANCOMPRASESPECIALCR.COD,
             V_IPLANCOMPRASESPECIALCR.ANO,
             V_IPLANCOMPRASESPECIALCR.NOMBRE,
             SUM(V_IPLANCOMPRASESPECIALCR.VLRTOTAL) SUMADEVALORTOTALCOMPRADO
        FROM V_IPLANCOMPRASESPECIALCR
       WHERE V_IPLANCOMPRASESPECIALCR.COMPANIA = UN_COMPANIA
         AND V_IPLANCOMPRASESPECIALCR.COD      = UN_CODIGO
         AND V_IPLANCOMPRASESPECIALCR.ANO      = UN_ANIO
         AND V_IPLANCOMPRASESPECIALCR.ANOORDEN = UN_ANIO
       GROUP BY V_IPLANCOMPRASESPECIALCR.COMPANIA,
                V_IPLANCOMPRASESPECIALCR.COD,
                V_IPLANCOMPRASESPECIALCR.ANO,
                V_IPLANCOMPRASESPECIALCR.NOMBRE
    )
    SELECT CON_I_PLANDECOMPRASGRUPOCR.NOMBRE AS NOMBRE,
           SUM(CON_I_PLANDECOMPRASGRUPOCR.SUMADEVALORTOTALCOMPRADO) VALORTOTALCOMPRADO
      INTO MI_VLR_EJECUTADO, MI_NOMBRE_PC
      FROM CON_I_PLANDECOMPRASGRUPOCR
     WHERE CON_I_PLANDECOMPRASGRUPOCR.COMPANIA = UN_COMPANIA
       AND CON_I_PLANDECOMPRASGRUPOCR.COD      = UN_CODIGO
       AND CON_I_PLANDECOMPRASGRUPOCR.ANO      = UN_ANIO
     GROUP BY CON_I_PLANDECOMPRASGRUPOCR.COD,
              CON_I_PLANDECOMPRASGRUPOCR.COMPANIA,
              CON_I_PLANDECOMPRASGRUPOCR.ANO,
              CON_I_PLANDECOMPRASGRUPOCR.NOMBRE;
  EXCEPTION 
    WHEN NO_DATA_FOUND THEN
      RETURN MI_VLR_EJECUTADO;
  END FC_CALC_VLR_EJECUTADO;

  --6 traerUltimoValorElemento
  FUNCTION FC_GET_ULTIMO_VALOR_ELEMENTO
  /*
    NAME              : FC_GET_ULTIMO_VALOR_ELEMENTO
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
    DATE MIGRATION    : 20/09/2017
    TIME              : 14:05 AM
    DESCRIPTION       : Trae el último valor del elemento en el detalle del Plan de Compras. 
                        Si no se encuentran datos con los parámetros suministrados, retorna cero.
    PARAMETROS DE ENTRADA: 
      UN_COMPANIA: Código de la compañía.
      UN_ANIO:      Año del plan de compras.
      UN_ELEMENTO:   Código del elemento.

    @NAME: traerUltimoValorElemento
    @METHOD: get
  */
  (
    UN_COMPANIA                  IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_ANIO                      IN PCK_SUBTIPOS.TI_ANIO
  , UN_ELEMENTO                  IN DETALLE_PLAN_COMPRAS.CODIGO%TYPE
  )
  RETURN PCK_SUBTIPOS.TI_DOBLE
  AS
    MI_VALOR                        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  BEGIN
    SELECT MAX(VALOR_UNITARIO) 
           KEEP(DENSE_RANK LAST ORDER BY ROWNUM) ULTIMO
      INTO MI_VALOR
      FROM DETALLE_PLAN_COMPRAS
     WHERE COMPANIA = UN_COMPANIA
       AND ANO      = UN_ANIO
       AND CODIGO   = UN_ELEMENTO;
    RETURN MI_VALOR;
  EXCEPTION 
    WHEN NO_DATA_FOUND THEN
      RETURN 0;
  END FC_GET_ULTIMO_VALOR_ELEMENTO;

 -- 7 calcularValorProgramado
  FUNCTION FC_CALCULAR_PROGRAMADO
  /*
    NAME              : FC_CALCULAR_PROGRAMADO
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : JUAN CARLOS RODRÃ¿GUEZ AMÃ‰ZQUITA
    DATE MIGRATION    : 20/09/2017
    TIME              : 15:25 AM
    DESCRIPTION       : Calcula el valor programado en el plan de compras para 
                        el rubro y aÃ±o ingresado por parÃ¡metro. Sumatoria de los 
                        detalles del plan de compras, sin tener en cuenta la 
                        dependencia y el mes.
    PARAMETROS DE ENTRADA: 
      UN_COMPANIA: CÃ³digo de la compaÃ±Ã­a.
      UN_ANIO:     AÃ±o del plan de compras.
      UN_RUBRO:    CÃ³digo del rubro presupuestal.

    @NAME: calcularValorProgramado
    @METHOD: get
  */
  (
    UN_COMPANIA                  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO                      IN PCK_SUBTIPOS.TI_ANIO,
    UN_RUBRO                     IN DETALLE_PLAN_COMPRAS.RUBRO%TYPE,
    UN_FUENTE_RECURSO            IN DETALLE_PLAN_COMPRAS.FUENTE_DE_RECURSOS%TYPE,
    UN_REFERENCIA                IN DETALLE_PLAN_COMPRAS.REFERENCIA%TYPE,
    UN_CENTRO_COSTO              IN DETALLE_PLAN_COMPRAS.CENTRO_COSTO%TYPE,
    UN_AUXILIAR                  IN DETALLE_PLAN_COMPRAS.AUXILIAR%TYPE
  ) 
  RETURN PCK_SUBTIPOS.TI_DOBLE
  AS
    MI_VALOR                        PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
  BEGIN
    SELECT NVL(SUM(VALORACOMPRAR),0) AS SUMADEVALORACOMPRAR
      INTO MI_VALOR
      FROM DETALLE_PLAN_COMPRAS
     WHERE COMPANIA = UN_COMPANIA
       AND ANO      = UN_ANIO
       AND RUBRO    = UN_RUBRO
       AND FUENTE_DE_RECURSOS = UN_FUENTE_RECURSO     
       AND REFERENCIA = UN_REFERENCIA     
       AND CENTRO_COSTO = UN_CENTRO_COSTO     
       AND AUXILIAR = UN_AUXILIAR
     GROUP BY ANO, RUBRO, COMPANIA;
     RETURN MI_VALOR;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      RETURN 0;
  END FC_CALCULAR_PROGRAMADO;

  FUNCTION FC_TIENE_DETALLES_PLAN_COMPRAS
  /*
    NAME              : FC_TIENE_DETALLES_PLAN_COMPRAS
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
    DATE MIGRATION    : 22/09/2017
    TIME              : 09:15 AM
    DESCRIPTION       : Verifica si el plan de adquisiciones tiene detalles.
    PARAMETROS DE ENTRADA: 
      UN_COMPANIA:    Código de la compañía.
      UN_ANIO:        Año del plan de compras.
      UN_RUBRO:       Código del rubro presupuestal.
      UN_DEPENDENCIA: Código de la dependencia.

    @NAME: tieneDetallesPlanCompras
    @METHOD: get
  */
  (
    UN_COMPANIA                  IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_ANIO                      IN PCK_SUBTIPOS.TI_ANIO
  , UN_RUBRO                     IN DETALLE_PLAN_COMPRAS.RUBRO%TYPE
  , UN_DEPENDENCIA               IN DETALLE_PLAN_COMPRAS.DEPENDENCIA%TYPE
  ) 
  RETURN PCK_SUBTIPOS.TI_LOGICO AS
    MI_EXISTE                       PCK_SUBTIPOS.TI_ENTERO;
  BEGIN
    SELECT COUNT(1) NRO_DETALLES
      INTO MI_EXISTE
      FROM DETALLE_PLAN_COMPRAS
     WHERE COMPANIA    = UN_COMPANIA
       AND ANO         = UN_ANIO
       AND RUBRO       = UN_RUBRO
       AND DEPENDENCIA = UN_DEPENDENCIA;
    IF MI_EXISTE > 0 THEN
      RETURN -1;
    ELSE
      RETURN 0;
    END IF;
  EXCEPTION WHEN NO_DATA_FOUND THEN 
    RETURN 0;
  END FC_TIENE_DETALLES_PLAN_COMPRAS;
------------------------------------------
END PCK_PLANEACION;