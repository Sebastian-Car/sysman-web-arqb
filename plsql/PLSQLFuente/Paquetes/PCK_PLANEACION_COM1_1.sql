create or replace PACKAGE BODY PCK_PLANEACION_COM1
AS
  -- 1
  PROCEDURE PR_REG_CERTPLANCOMPRAS
    /*
    NAME              : FC_REG_CERTPLANCOMPRAS
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : SERGIO ESTEBAN PIÑA VARGAS
    DATE MIGRATION    : 11/09/2017
    TIME              : 05:48 PM
    SOURCE MODULE     : SysmanPc2015.09.02.accdb
    PARAMETERS        : UN_COMPANIA => Codigo de la compania desde la que se inicio sesion.
    MODIFIED BY       :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : 
    @NAME: registrarCertPlanCompras
    */
    (
      UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_ANIO         IN PCK_SUBTIPOS.TI_ANIO,
      UN_NUMERO       IN CERTIFICADO_PLAN_COMPRAS.NUMERO%TYPE,
      UN_DEPENDENCIA  IN DEPENDENCIA_RESPONSABLE.DEPENDENCIA%TYPE,
      UN_RESPONSABLE  IN DEPENDENCIA_RESPONSABLE.RESPONSABLE%TYPE,
      UN_MES          IN DETALLE_PLAN_COMPRAS.MES%TYPE)
    
  AS
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_RUBRO            NUMBER;
    MI_MERGEUSING         PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE        PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE        PCK_SUBTIPOS.TI_MERGEEXISTE;
  BEGIN
    MI_CAMPOS     := 'IMPRESO         = -1, 
                      CONSECUTIVO_PC  = ' || UN_NUMERO;
    MI_CONDICION  := 'CERTIFICADO_PLAN_COMPRAS.COMPANIA   = ''' || UN_COMPANIA ||'''
                      AND CERTIFICADO_PLAN_COMPRAS.ANO    = '|| UN_ANIO ||' 
                      AND CERTIFICADO_PLAN_COMPRAS.NUMERO = '|| UN_NUMERO ||'
                     ';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA      => 'CERTIFICADO_PLAN_COMPRAS' ,
                                                UN_ACCION     => 'M' ,
                                                UN_CAMPOS     => MI_CAMPOS,
                                                UN_CONDICION  => MI_CONDICION );
      EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
      END;
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE ,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_PLANEACION_ACTIM_CERT ,
                                    UN_TABLAERROR => 'CERTIFICADO_PLAN_COMPRAS' );
    END;

    BEGIN
        SELECT DISTINCT DPC.RUBRO
        INTO MI_RUBRO
        FROM PLAN_DE_COMPRAS PC
            INNER JOIN DETALLE_PLAN_COMPRAS DPC
                ON PC.COMPANIA = DPC.COMPANIA
                AND PC.CODIGO  = DPC.RUBRO
                AND PC.ANO     = DPC.ANO
            INNER JOIN DEPENDENCIA_RESPONSABLE DR
                ON DPC.COMPANIA     = DR.COMPANIA
                AND DPC.DEPENDENCIA = DR.DEPENDENCIA
        WHERE PC.COMPANIA       = UN_COMPANIA
          AND PC.ANO              = UN_ANIO
          AND DR.DEPENDENCIA      = UN_DEPENDENCIA
          AND DR.RESPONSABLE      = UN_RESPONSABLE
          AND DPC.ENVIAR_CERT     = -1      
          AND DPC.MES             = UN_MES
          AND DPC.CONSECUTIVO_PC  = UN_NUMERO;   

        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_RUBRO := '';
        END;      

    MI_MERGEUSING := 'SELECT DETALLE_PLAN_COMPRAS.COMPANIA,
                    DETALLE_PLAN_COMPRAS.ANO,
                    DETALLE_PLAN_COMPRAS.RUBRO,
                    DETALLE_PLAN_COMPRAS.CODIGO,
                    DETALLE_PLAN_COMPRAS.DEPENDENCIA,
                    DETALLE_PLAN_COMPRAS.MES    
                 FROM PLAN_DE_COMPRAS
                          INNER JOIN DETALLE_PLAN_COMPRAS
                            ON PLAN_DE_COMPRAS.COMPANIA = DETALLE_PLAN_COMPRAS.COMPANIA
                            AND PLAN_DE_COMPRAS.ANO       = DETALLE_PLAN_COMPRAS.ANO
                            AND PLAN_DE_COMPRAS.CODIGO   = DETALLE_PLAN_COMPRAS.RUBRO
                            AND PLAN_DE_COMPRAS.FUENTE_DE_RECURSOS = DETALLE_PLAN_COMPRAS.FUENTE_DE_RECURSOS
                            AND PLAN_DE_COMPRAS.REFERENCIA = DETALLE_PLAN_COMPRAS.REFERENCIA
                            AND PLAN_DE_COMPRAS.CENTRO_COSTO = DETALLE_PLAN_COMPRAS.CENTRO_COSTO
                            AND PLAN_DE_COMPRAS.AUXILIAR = DETALLE_PLAN_COMPRAS.AUXILIAR
                          INNER JOIN DEPENDENCIA_RESPONSABLE
                            ON DETALLE_PLAN_COMPRAS.COMPANIA        = DEPENDENCIA_RESPONSABLE.COMPANIA
                            AND DETALLE_PLAN_COMPRAS.DEPENDENCIA    = DEPENDENCIA_RESPONSABLE.DEPENDENCIA
                  WHERE PLAN_DE_COMPRAS.COMPANIA            = '''|| UN_COMPANIA ||'''
                    AND PLAN_DE_COMPRAS.ANO                 = '|| UN_ANIO ||'
                    AND DETALLE_PLAN_COMPRAS.MES            = '|| UN_MES ||'
                    AND DEPENDENCIA_RESPONSABLE.DEPENDENCIA = '''|| UN_DEPENDENCIA ||'''
                    AND DEPENDENCIA_RESPONSABLE.RESPONSABLE = '''|| UN_RESPONSABLE ||'''                                     
                    AND DETALLE_PLAN_COMPRAS.CONSECUTIVO_PC = 0 ';

    MI_MERGEENLACE := 'TABLA.COMPANIA    = VISTA.COMPANIA
                   AND TABLA.ANO         = VISTA.ANO
                   AND TABLA.RUBRO       = VISTA.RUBRO
                   AND TABLA.CODIGO      = VISTA.CODIGO
                   AND TABLA.DEPENDENCIA = VISTA.DEPENDENCIA
                   AND TABLA.MES         = VISTA.MES';
                   
	/*TICKET 7701221-22/02/2022 (JCROJAS): Se quita actualizaciòn al campo ENVIAR_CERT ya que para todos los detalles estaba 
                                           asignando el valor de -1 y se solicita que se puedan seleccionar los que se deseen enviar*/
    MI_MERGEEXISTE := 'UPDATE SET TABLA.CONSECUTIVO_PC = '|| UN_NUMERO;

    BEGIN
      BEGIN               
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_ACCION   => 'MM',
                                           UN_TABLA       => 'DETALLE_PLAN_COMPRAS',
                                           UN_MERGEUSING  => MI_MERGEUSING,
                                           UN_MERGEENLACE => MI_MERGEENLACE,
                                           UN_MERGEEXISTE => MI_MERGEEXISTE);                                                
      EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
      END;
      EXCEPTION
      WHEN PCK_EXCEPCIONES.EXC_PLANEACION THEN
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE ,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_PLANEACION_ACTIM_DETALLE ,
                                    UN_TABLAERROR => 'DETALLE_PLAN_COMPRAS' );
    END;

  END PR_REG_CERTPLANCOMPRAS;

--2
 FUNCTION FC_RESPONDERPROPUESTA
    /*
    NAME              : FC_RESPONDERPROPUESTA nombre migrado del controlador  PropuestareqsControlador metodo:responder
    AUTHORS           : STEFANINI SYSMAN
    AUTHOR MIGRATION  : JUAN CAMILO RODRIGUEZ DIAZ
    DATE MIGRATION    : 13/09/2017
    TIME              : 05:48 PM
    SOURCE MODULE     : SysmanPc2015.09.02.accdb
    PARAMETERS        :
                        UN_COMPANIA         =>compañia con la que se encuentra en la sesión actual
                        UN_USUARIO          =>usuario de la session actual
                        UN_COD_REQUISICION  =>codigo de requisision
                        UN_COD_PROPUESTA    =>codigo de la propuesta
    MODIFIED BY       :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : 
    @NAME: responderPropuesta
    */
    (
      UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA,
      UN_USUARIO          IN  PCK_SUBTIPOS.TI_USUARIO,
      UN_COD_REQUISICION  IN  PCK_SUBTIPOS.TI_ENTERO_LARGO,
      UN_COD_PROPUESTA    IN  PCK_SUBTIPOS.TI_ENTERO_LARGO,
      UN_RESPONDIO        IN  PCK_SUBTIPOS.TI_LOGICO
      )
    RETURN PCK_SUBTIPOS.TI_ENTERO
  AS
    MI_RPTA       PCK_SUBTIPOS.TI_ENTERO;
    MI_CUENTA     PCK_SUBTIPOS.TI_ENTERO;
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
  BEGIN
      IF UN_RESPONDIO = 1 THEN
              SELECT COUNT(*) CUENTA INTO MI_CUENTA
              FROM PROPUESTA_DETALLE
              WHERE COD_REQUISICION = UN_COD_REQUISICION
              AND COMPANIA          = UN_COMPANIA
              AND COD_PROPUESTA     = UN_COD_PROPUESTA;

            IF MI_CUENTA = 0 THEN
                   BEGIN 
                       MI_CAMPOS:='COD_PROPUESTA,'||
                                  'COD_REQUISICION,'||
                                  'COMPANIA,'||
                                  'COD_DETALLE,'||
                                  'ORDENDESUMINISTRO,'||
                                  'DEPENDENCIA,'||
                                  'ELEMENTO,'||
                                  'ESPECIFICACION,'||
                                  'CANTIDAD,'||
                                  'SALDOCANT,'||
                                  'VALORUNITARIO,'||
                                  'PORCIVA,'||
                                  'PORCDESC,'||
                                  'VLRTOTAL,'||
                                  'MARCA';

                        MI_VALORES:='SELECT UN_COD_PROPUESTA,'||
                                    'DETALLEREQUIS.COD_REQUISICION,'||
                                    'DETALLEREQUIS.COMPANIA,'||
                                    'DETALLEREQUIS.COD_DETALLE,'||
                                    'DETALLEREQUIS.ORDENDESUMINISTRO,'||
                                    'DETALLEREQUIS.DEPENDENCIA,'||
                                    'DETALLEREQUIS.CODIGOELEMENTO,'||
                                    'DETALLEREQUIS.ESPECIFICACION,'||
                                    'DETALLEREQUIS.CANTIDAD,'||
                                    'DETALLEREQUIS.CANTIDAD,'||
                                    '0,'||
                                    '0,'||
                                    '0,'||
                                    '0,'||
                                    ''' '''||
                                  'FROM DETALLEREQUIS'||
                                  'WHERE COMPANIA      = '''||UN_COMPANIA||''''||
                                  'AND COD_REQUISICION = '''||UN_COD_REQUISICION||'''';


                            BEGIN
                                  MI_RPTA:=PCK_DATOS.FC_ACME( UN_TABLA    =>'PROPUESTA_DETALLE',
                                                              UN_ACCION   =>'IS',
                                                              UN_CAMPOS   =>MI_CAMPOS,
                                                              UN_VALORES  =>MI_VALORES);

                                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                                           RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
                            END;

                       EXCEPTION  WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        MI_MSGERROR(1).CLAVE := 'TABLA';
                        MI_MSGERROR(1).VALOR := MI_TABLA;
                        MI_MSGERROR(2).CLAVE := 'CAMPOS';
                        MI_MSGERROR(2).VALOR :=MI_CAMPOS;
                          PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE,
                            UN_ERROR_COD  => PCK_ERRORES.ERR_FC_INS_RESPODETALLEREQUIS,
                            UN_REEMPLAZOS => MI_MSGERROR
                          );
                   END;
            END IF;      
      ELSE 
             BEGIN
                 MI_CONDICION:='COD_REQUISICION ='||
                               ''''||UN_COD_REQUISICION||''''||
                               ' AND COMPANIA ='||
                               ''''||UN_COMPANIA||''''||
                               ' AND COD_PROPUESTA ='||
                               ''''||UN_COD_PROPUESTA||'''';
                  BEGIN

                          MI_RPTA:=PCK_DATOS.FC_ACME( UN_TABLA    =>'PROPUESTA_DETALLE',
                                                      UN_ACCION   =>'E',
                                                      UN_CONDICION=>MI_CONDICION);

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
                                   RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
                  END;


                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                        MI_MSGERROR(1).CLAVE := 'TABLA';
                        MI_MSGERROR(1).VALOR := MI_TABLA;
                        MI_MSGERROR(2).CLAVE := 'CONDICION';
                        MI_MSGERROR(2).VALOR := MI_CONDICION;
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE,
                            UN_ERROR_COD  => PCK_ERRORES.ERR_FC_DEL_RESPODETALLEREQUIS,
                            UN_REEMPLAZOS => MI_MSGERROR);
              END;
      END IF;       

  RETURN MI_RPTA;                             
END FC_RESPONDERPROPUESTA;


FUNCTION FC_RESPONDERCOTIZACION
/*
   NAME 			      : Respondio_AfterUpdate
   AUTHORS 			    : SYSMAN SAS
   AUTHOR MIGRACION	: JUAN CAMILO RODRIGUEZ DIAZ
   DATE MIGRADOR	  : 03/11/2017
   TIME				      : 12:00 PM
   MODULO ORIGEN	  : PLANEACION 
   DESCRIPTION		  : Procedimiento que al cambiar el check de respondio registra o elimina. 
   PARAMETERS       :  UN_COMPANIA        => COMPANIA DE LA SESION ACTUAL
                       UN_COD_PROPUESTA   => CODIGO DE LA PROPUESTA
                       UN_COD_REQUISICION => CODIGO DE REQUISICION
                       UN_RESPONDIO       => BOOLEANON CHECK RESPONDIO
                       UN_USUARIO         => USUARIO
   @NAME:    actualizarEstadoRespondio 
   @METHOD:  GET
  */
(
 UN_COMPANIA        IN  PCK_SUBTIPOS.TI_COMPANIA,
 UN_COD_PROPUESTA   IN  PROPUESTA_DETALLE.COD_PROPUESTA%TYPE,
 UN_COD_REQUISICION IN  PROPUESTA_DETALLE.COD_REQUISICION%TYPE,
 UN_RESPONDIO       IN  PCK_SUBTIPOS.TI_LOGICO,
 UN_USUARIO         IN  PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_LOGICO
AS


MI_CONTAR     PCK_SUBTIPOS.TI_ENTERO;
MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS:=NULL;
MI_VALORES    PCK_SUBTIPOS.TI_CAMPOS; 
MI_CONDICION  PCK_SUBTIPOS.TI_CAMPOS:=NULL; 
MI_RPTA       PCK_SUBTIPOS.TI_ENTERO;
MI_MSGERROR   PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_VALOR      PCK_SUBTIPOS.TI_LOGICO;
BEGIN

    MI_CONTAR:=0;
    MI_VALOR:=0;

    SELECT COUNT(*)AS CONTAR INTO MI_CONTAR
    FROM PROPUESTA_DETALLE
    WHERE COD_REQUISICION = UN_COD_REQUISICION
    AND COMPANIA          = UN_COMPANIA
    AND COD_PROPUESTA     = UN_COD_PROPUESTA;

    MI_TABLA:='PROPUESTA_DETALLE';
    BEGIN

        IF UN_RESPONDIO = 1 OR UN_RESPONDIO = -1 THEN

              IF MI_CONTAR = 0  THEN
                    BEGIN

                    MI_CAMPOS:='COD_PROPUESTA,'||
                               'COD_REQUISICION,'||
                               'COMPANIA,'||
                               'COD_DETALLE,'||
                               'ORDENDESUMINISTRO,'||
                               'DEPENDENCIA,'||
                               'ELEMENTO,'||
                               'ESPECIFICACION,'||
                               'CANTIDAD,'||
                               'SALDOCANT,'||
                               'VALORUNITARIO,'||
                               'PORCIVA,'||
                               'PORCDESC,'||
                               'VLRTOTAL,'||
                               'CREATED_BY,'||
                               'DATE_CREATED,'||
                               'MARCA';

                    MI_VALORES:='SELECT '||UN_COD_PROPUESTA||','||
                                'DETALLEREQUIS.COD_REQUISICION,'||    
                                'DETALLEREQUIS.COMPANIA,'||
                                'DETALLEREQUIS.COD_DETALLE,'||
                                'DETALLEREQUIS.ORDENDESUMINISTRO,'||
                                'DETALLEREQUIS.DEPENDENCIA,'||
                                'DETALLEREQUIS.CODIGOELEMENTO,'||
                                'DETALLEREQUIS.ESPECIFICACION,'||
                                'DETALLEREQUIS.CANTIDAD,'||
                                'DETALLEREQUIS.CANTIDAD,'||
                                '0,0,0,0,'||
                                ''''||UN_USUARIO||''','||
                                'SYSDATE,'||
                                ''' '''||
                                ' FROM DETALLEREQUIS'||
                                ' WHERE DETALLEREQUIS.COD_REQUISICION ='|| UN_COD_REQUISICION||
                                ' AND DETALLEREQUIS.COMPANIA          = '''||UN_COMPANIA||'''';

                     MI_RPTA:=PCK_DATOS.FC_ACME(UN_ACCION  =>'IS',
                                                UN_TABLA   =>MI_TABLA,
                                                UN_CAMPOS  =>MI_CAMPOS,
                                                UN_VALORES =>MI_VALORES);

                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
                     END ;

              END IF;
              MI_VALOR:=1;

        ELSE

                    BEGIN

                    MI_CONDICION:='COD_REQUISICION    ='||UN_COD_REQUISICION||
                                  ' AND COMPANIA      ='''|| UN_COMPANIA||''''||
                                  ' AND COD_PROPUESTA = '||UN_COD_PROPUESTA;

                    MI_RPTA:=PCK_DATOS.FC_ACME(UN_ACCION    =>'E',
                                               UN_TABLA     =>MI_TABLA,
                                               UN_CONDICION =>MI_CONDICION);

                     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                     RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
                     END ;
              MI_VALOR:=0;
        END IF;

     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANEACION THEN
                 MI_MSGERROR(1).CLAVE := 'TABLA';
                 MI_MSGERROR(1).VALOR := MI_TABLA;
                 MI_MSGERROR(2).CLAVE := 'CAMPOS';
                 MI_MSGERROR(2).VALOR := MI_CONDICION;
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERR_RESPONDER_COTIZACION_GEN,
                 UN_REEMPLAZOS => MI_MSGERROR);
    END;
    RETURN MI_VALOR; 
END FC_RESPONDERCOTIZACION;

  --4 evaluarTipoAdjudicacionPredeterminado
  PROCEDURE PR_EVALUAR_TIPO_ADJUD_PREDET
  /*
   NAME 			      : PR_EVALUAR_TIPO_ADJUD_PREDET
   AUTHORS 			    : SYSMAN SAS
   AUTHOR MIGRACION	: Juan Carlos Rodríguez Amézquita
   DATE MIGRADOR	  : 14/11/2017
   TIME				      : 04:03 PM
   MODULO ORIGEN	  : PLANEACION 
   DESCRIPTION		  : Verifica que exista el tipo de adjudicación configurado como predeterminado 
                      en la tabla del detalle del plan de adquisiciones.
   PARAMETERS       : 
    UN_COMPANIA => Código de la compañía
    UN_VALOR_PREDETERMINADO => Valor asigando por defecto para el tipo de adjudicación 
                              en la tabla del detalle de plan de compras.

   @NAME:    evaluarTipoAdjudicacionPredeterminado 
   @METHOD:  POST
  */
  (
    UN_COMPANIA                   IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_VALOR_PREDETERMINADO       IN DETALLE_PLAN_COMPRAS.TIPO_CONTRATACION%TYPE
  ) AS
    MI_EXISTE                                 PCK_SUBTIPOS.TI_LOGICO;
    MI_REEMPLAZOS                             PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    BEGIN
      -- Verifica si existe el tipo de adjudicación predeterminado
      SELECT 1
      INTO MI_EXISTE
      FROM TIPOADJUDICACION
      WHERE COMPANIA = UN_COMPANIA
      AND CODIGO     = UN_VALOR_PREDETERMINADO;
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN
        RAISE PCK_EXCEPCIONES.EXC_PLANEACION;
    END;
  EXCEPTION
    WHEN PCK_EXCEPCIONES.EXC_PLANEACION THEN
      MI_REEMPLAZOS(1).CLAVE := 'VALOR';
      MI_REEMPLAZOS(1).VALOR := UN_VALOR_PREDETERMINADO;
      PCK_ERR_MSG.RAISE_WITH_MSG(
        UN_EXC_COD => SQLCODE
      , UN_TABLAERROR => 'TIPOADJUDICACION'
      , UN_ERROR_COD => PCK_ERRORES.ERR_PLANEACION_CREAR_TIPOADJUD
      , UN_REEMPLAZOS => MI_REEMPLAZOS
      );
  END PR_EVALUAR_TIPO_ADJUD_PREDET;

END PCK_PLANEACION_COM1;