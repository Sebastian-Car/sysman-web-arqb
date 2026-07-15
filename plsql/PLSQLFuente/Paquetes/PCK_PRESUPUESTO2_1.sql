create or replace PACKAGE BODY               "PCK_PRESUPUESTO2" 
AS
  
--2
FUNCTION FC_AUCOMPROBANTE_DETALLEPPTAL
  /*
  NAME              : FC_AU_COMPROBANTE_DETALLEPPTAL 
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : Diego Maldonado
  DATE MIGRADOR     : 25/07/2016
  TIME              : 10:00 AM
  SOURCE MODULE     : SysmanPR2016.03.02.accdb
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Función para actualizar los rubros de la tabla OrdenDeCompraPpto 
                      con los datos de la tabla DetalleComprobantePptal al actualizar 
                      el comprobante presupuestal.
  @NAME:  actualizarRubrosAsociadosEnContratacion
  @METHOD:  GET
  */
  (
  -- Parametro que recibe el numero de compania 
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el año 
  UN_ANO                      IN PCK_SUBTIPOS.TI_ANIO,
  -- Parametro que recibe el tipo de comprobante presupuestal 
  UN_TIPOCOMPROBANTE          IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL,
  -- Parametro que recibe el numero de comprobante a actualizar
  UN_NUMEROCOMPROBANTE        IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
  -- Parametro que recibe el tipo de contrato a actualizar
  UN_TIPOCONTRATO             IN ORDENDECOMPRAPPTO.CLASEORDEN%TYPE,
  -- Parametro que el numero de contrato a actualizar
  UN_NUMEROCONTRATO           IN ORDENDECOMPRAPPTO.NUMERO%TYPE
  ) RETURN NUMBER
  AS
    MI_ERROR_FUN              PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 1;
    -- Variable que almacenara el nombre de la tabla a modificar
    MI_TABLA                  PCK_SUBTIPOS.TI_TABLA;
    -- Variable que almacenara la cadena de campos a actualizar
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacenara la cadena de nuevos valores a actualizar
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    -- Variable que almacenara la condicion al momento de hacer la actuzaliacion
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
    BEGIN
        MI_TABLA := 'ORDENDECOMPRAPPTO';
        MI_CONDICION := 'COMPANIA       = ''' || UN_COMPANIA          ||'''
                         AND ANOPPTO    = '   || UN_ANO               ||'
                         AND TIPOPPTO   = ''' || UN_TIPOCOMPROBANTE   ||'''
                         AND NUMEROPPTO = '   || UN_NUMEROCOMPROBANTE ||'
                         AND CLASEORDEN = ''' || UN_TIPOCONTRATO      ||'''
                         AND NUMERO     = '   || UN_NUMEROCONTRATO    ||'';
        BEGIN -- BEGIN usado en el control de errores                
        GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     =>MI_TABLA ,
                                    UN_ACCION    =>'E',
                                    UN_CONDICION =>MI_CONDICION
                                    );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END ;

        MI_CAMPOS := '  COMPANIA
                      , CLASEORDEN
                      , NUMERO
                      , ANOPPTO
                      , MES 
                      , TIPOPPTO 
                      , NUMEROPPTO 
                      , FECHA 
                      , RUBRO 
                      , CENTRO_COSTO
                      , TERCERO
                      , SUCURSAL
                      , AUXILIAR
                      , REFERENCIA
                      , FUENTE_RECURSO';

        MI_VALORES := 'SELECT DETALLE_COMPROBANTE_PPTAL.COMPANIA
                            , DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE CLASEORDEN
                            , DETALLE_COMPROBANTE_PPTAL.COMPROBANTE NUMERO
                            , DETALLE_COMPROBANTE_PPTAL.ANO ANOPPTO
                            , DETALLE_COMPROBANTE_PPTAL.MES
                            , DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE TIPOPPTO
                            , DETALLE_COMPROBANTE_PPTAL.COMPROBANTE NUMEROPPTO
                            , DETALLE_COMPROBANTE_PPTAL.FECHA
                            , DETALLE_COMPROBANTE_PPTAL.CUENTA RUBRO
                            , DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO
                            , DETALLE_COMPROBANTE_PPTAL.TERCERO
                            , DETALLE_COMPROBANTE_PPTAL.SUCURSAL
                            , DETALLE_COMPROBANTE_PPTAL.AUXILIAR
                            , DETALLE_COMPROBANTE_PPTAL.REFERENCIA
                            , DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO
                         FROM DETALLE_COMPROBANTE_PPTAL 
                        WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA     = ''' || UN_COMPANIA          || '''
                          AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE    = ''' || UN_TIPOCOMPROBANTE   || '''
                          AND DETALLE_COMPROBANTE_PPTAL.COMPROBANTE  = ''' || UN_NUMEROCOMPROBANTE || '''
                          AND DETALLE_COMPROBANTE_PPTAL.TIPOCONTRATO IS NOT NULL 
                          AND DETALLE_COMPROBANTE_PPTAL.NUMEROCONTRATO IS NOT NULL';

        BEGIN                 
        GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   =>MI_TABLA, 
                                    UN_ACCION  =>'IS', 
                                    UN_CAMPOS  =>MI_CAMPOS,
                                    UN_VALORES =>MI_VALORES
                                  );   
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END ;                  
       RETURN -1;   

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERR_PPTO_AUCOMPDETPPTAL
  ); 
END FC_AUCOMPROBANTE_DETALLEPPTAL;

PROCEDURE PR_AFECTAROTROCOMPROBANTE
  /*
  NAME                 : PR_AFECTAROTROCOMPROBANTE
  AUTHORS              : SYSMAN SAS
  AUTHOR MIGRACION     : Diego Maldonado
  DATE MIGRADOR        : 10/08/2016
  TIME                 : 5:10 PM
  SOURCE MODULE        : SysmanPR2016.03.02.accdb
  MODIFIER             : Juan Carlos Rodríguez Amézquita
  DATE MODIFIED        : 14/07/2017
  TIME                 : 09:37 AM
  DESCRIPTION          : Afecta un comprobante Pptal del cual depende otro comprobante Pptal.
  MODIFICATIONS        : Registro de la afectación en la tabla COMPROBANTE_PPTALAFECTADOS
                         tal como se hace en PCK_PRESUPUESTO1.FC_DOCUMENTOAFECTAR_PPTAL
  PARAMETROS DE ENTRADA: 
    UN_COMPANIA:        Código de la compañía
    UN_MODULO:          Código del módulo.
    UN_ANO:             Año del comprobante que se va a afectar
    UN_ANO0:            Año del comprobante que afecta
    UN_TIPO0:           Tipo del comprobante que afecta
    UN_TIPO:            Tipo del comprobante que se va a afectar
    UN_NUMERO:          Número del comprobante que se va a afectar
    UN_CUENTA:          Código del rubro que va afectar
    UN_CREDITOA:        Valor del crédito anterior
    UN_CONTRACREDITOA:  Valor del contracredito anterior
    UN_CREDITO:         Valor del nuevo credito
    UN_CONTRACREDITO:   Valor del nuevo contracredito
    UN_CONSECUTIVO:     Número consecutivo dado al detalle del comprobante presupuestal
    UN_CONSECUTIVOPPTO: Número que identifica el consecutivo de presupuesto
    UN_CON:             Indica si viene de contabilidad
    UN_NUMERO0:         Numero inicial del comprobante que afecta
    UN_USUARIO:         Usuario que afecta el comprobante al registrar el detalle presupuestal

  @NAME: afectarOtroComprobantePresupuestal
  @METHOD: POST        
  */
  (
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_MODULO                   IN PCK_SUBTIPOS.TI_MODULO,
  UN_ANO                      IN PCK_SUBTIPOS.TI_ANIO,
  UN_ANO0                     IN PCK_SUBTIPOS.TI_ANIO,
  UN_TIPO0                    IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL,
  UN_TIPO                     IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL,
  UN_NUMERO                   IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
  UN_CUENTA                   IN PCK_SUBTIPOS.TI_CODIGOPPTAL,
  UN_CREDITOA                 IN PCK_SUBTIPOS.TI_DOBLE,
  UN_CONTRACREDITOA           IN PCK_SUBTIPOS.TI_DOBLE,
  UN_CREDITO                  IN PCK_SUBTIPOS.TI_DOBLE,
  UN_CONTRACREDITO            IN PCK_SUBTIPOS.TI_DOBLE,
  UN_CONSECUTIVO              IN PCK_SUBTIPOS.TI_ENTERO,
  UN_CONSECUTIVOPPTO          IN PCK_SUBTIPOS.TI_ENTERO,
  UN_CON                      IN VARCHAR2 DEFAULT NULL,
  UN_NUMERO0                  IN BP_D_NOVEDADPROYECTO.NOVEDAD%TYPE DEFAULT NULL,
  UN_USUARIO                  IN PCK_SUBTIPOS.TI_USUARIO DEFAULT NULL
  )
  AS
    MI_ERROR_FUN              PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 1;
    MI_AFECTAROTROCOMPROBANTE PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
    -- Variable que almacenara el valor del año del comprobante a afectar
    MI_ANO                    PCK_SUBTIPOS.TI_ANIO;
    -- Variable que almacenara la cadena de campos a actualizar 
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacenara la cadena de valores usados al actualizar
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    -- Variable que almacenara la condicion usada al momento de hacer la actualizacion
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
    -- Variable que almacenara el tipo de comprobante a afectar
    MI_TIPOAFECT              PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL;
    -- Variable que almacenara el numero de comprobante a afectar
    MI_NUMEROAFECT            PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
    -- Variable que almacenara el valor a retornar en la funcion 
    MI_RTA                    PCK_SUBTIPOS.TI_ENTERO_LARGO;
    -- Variable que almacenara las consultas usadas en la funcion 
    MI_STRSQL                 PCK_SUBTIPOS.TI_STRSQL;
    -- Cursor que almaenara el resultado de una consulta
    MI_RSDETALLE              SYS_REFCURSOR;
    -- Varibale que almacenara el ano que se trae en el cursor 
    MI_RSAANO                 PCK_SUBTIPOS.TI_ANIO;
    -- Variable que almacenara el tipo de comprobante afectado que trae el cursor
    MI_RSATIPO_CPTE_AFECT     PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL;
    -- Variable que almacenara el numero de comprobante que se trae en el cursor
    MI_RSACMPTE_AFECTADO      PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
    -- Variable que almacenara el consecutivoppto que se trae del cursor
    MI_RSACONSECUTIVOPPTO     DETALLE_COMPROBANTE_PPTAL.CONSECUTIVOPPTO%TYPE;
    -- Variable que almacenara el codigo de la cuenta que se trae desde el cursor
    MI_RSACUENTA              PCK_SUBTIPOS.TI_CODIGOPPTAL;
    -- Variable que almacenara el codigo que se trae desde el cursor
    MI_RSCCODIGO              PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL;
    -- Variable que almacenara la clase de afectacion que se trae desde el cursor
    MI_RSCAFECTACION          CLASECNTPRES.AFECTACION%TYPE;
    -- Variable que almacenara el valor que retorna un parametro
    MI_PARAMETRO              VARCHAR2(4000 CHAR);
    -- Variable que almacenara el consecutivo del detalle
    MI_DETCONSECUTIVO         PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
    -- Variable que almacenara el valor del detalle 
    MI_DETVALOR               PCK_SUBTIPOS.TI_DOBLE;
    MI_RSBCMPTE               PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
    MI_RSBTIPO                PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL;
    MI_RSBITEM                BP_D_NOVEDADPROYECTO.ITEM_AFECT%TYPE;
    MI_RSAFOUND               PCK_SUBTIPOS.TI_LOGICO := -1;
    MI_CLASEAFECTAR           CLASECNTPRES.CLASEAFECTAR%TYPE;
    MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN
        BEGIN
          GL_MSGLOG:='';
          IF UN_TIPO IS NULL OR UN_NUMERO IS NULL THEN
             RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END IF; 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_VALORESNULOS
                                               );     
        END;
        BEGIN
            --AQUÍ AVERIGUA EL AÑO DEL COMPROBANTE PRESUPUESTAL;
            IF UN_TIPO = 'SCD' THEN
              BEGIN
                SELECT  ANO_AFECT
                     ,  TIPO_CPTE_AFECT
                     ,  CMPTE_AFECTADO
                     ,  CONSECUTIVOPPTO
                     ,  CUENTA 
                  INTO  MI_RSAANO
                     ,  MI_RSATIPO_CPTE_AFECT
                     ,  MI_RSACMPTE_AFECTADO
                     ,  MI_RSACONSECUTIVOPPTO
                     ,  MI_RSACUENTA 
                  FROM  DETALLE_COMPROBANTE_PPTAL 
                 WHERE  COMPANIA          =  UN_COMPANIA
                   AND  ANO               =  UN_ANO 
                   AND  TIPO_CPTE_AFECT   =  UN_TIPO 
                   AND  CMPTE_AFECTADO    =  UN_NUMERO 
                   AND  CUENTA            =  UN_CUENTA 
                   AND  CONSECUTIVO       =  UN_CONSECUTIVOPPTO;

              EXCEPTION WHEN NO_DATA_FOUND THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END ; 

            ELSE
              BEGIN
                MI_ANO := UN_ANO;
                SELECT  ANO_AFECT
                     ,  TIPO_CPTE_AFECT
                     ,  CMPTE_AFECTADO
                     ,  CONSECUTIVOPPTO
                     ,  CUENTA 
                  INTO  MI_RSAANO
                     ,  MI_RSATIPO_CPTE_AFECT
                     ,  MI_RSACMPTE_AFECTADO
                     ,  MI_RSACONSECUTIVOPPTO
                     ,  MI_RSACUENTA 
                  FROM  DETALLE_COMPROBANTE_PPTAL 
                 WHERE  COMPANIA          =  UN_COMPANIA
                   AND  ANO               =  UN_ANO 
                   AND  TIPO_CPTE         =  UN_TIPO 
                   AND  COMPROBANTE       =  UN_NUMERO 
                   AND  CUENTA            =  UN_CUENTA 
                   AND  CONSECUTIVO       =  UN_CONSECUTIVOPPTO;

              EXCEPTION WHEN NO_DATA_FOUND THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END ;

            END IF;
            --SE REALIZA ESTE CASE PORQUE LA VARIABLE MI_ANO ESTABA QUEDANDO NULA CUANDO EL COMPROBANTE A AFECTAR 
            --NO TIENE COMPROBANTE AFECTADO EN CONSECUENCIA LAS VARIABLES MI_RSAANO,  MI_RSATIPO_CPTE_AFECT,  MI_RSACMPTE_AFECTADO SON NULAS
            --Y DAÑAN EL PROCESO DE AFECTACIÓN REALIZADO DESDE EL MÓDULO DE PRESUPUESTO.
            MI_ANO := CASE WHEN MI_RSAANO IS NULL 
                           THEN UN_ANO 
                           ELSE MI_RSAANO 
                           END;                       

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    MI_MSGERROR(1).CLAVE := 'TIPOCPTE';
                    MI_MSGERROR(1).VALOR := UN_TIPO;
                    MI_MSGERROR(2).CLAVE := 'NROCPTE';
                    MI_MSGERROR(2).VALOR := UN_NUMERO;
                    MI_MSGERROR(3).CLAVE := 'CUENTA';
                    MI_MSGERROR(3).VALOR := UN_CUENTA;
                    MI_MSGERROR(4).CLAVE := 'CONSECUTIVO';
                    MI_MSGERROR(4).VALOR := UN_CONSECUTIVOPPTO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_CPTEAFECTAR_NE,
                                               UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL',
                                               UN_REEMPLAZOS => MI_MSGERROR
                                               );        
        END;
        BEGIN
          BEGIN

            SELECT  CLASECNTPRES.CODIGO
                 ,  CLASECNTPRES.AFECTACION 
              INTO  MI_RSCCODIGO
                 ,  MI_RSCAFECTACION
              FROM  TIPO_COMPROBPP 
             INNER JOIN CLASECNTPRES 
                     ON TIPO_COMPROBPP.CLASE = CLASECNTPRES.CODIGO
             WHERE  TIPO_COMPROBPP.COMPANIA  = UN_COMPANIA
               AND  TIPO_COMPROBPP.CODIGO    = UN_TIPO0 ;

            EXCEPTION WHEN NO_DATA_FOUND THEN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    MI_MSGERROR(1).CLAVE := 'TIPOCPTE';
                    MI_MSGERROR(1).VALOR :=  UN_TIPO0;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_CONFCLASECNTPRES,
                                               UN_TABLAERROR => 'CLASECNTPRES',
                                               UN_REEMPLAZOS => MI_MSGERROR
                                               );        
          END;
        IF UN_CON = 'E' THEN
            MI_CAMPOS := 'DEBITO_AFECTADOCNT  = DEBITO_AFECTADOCNT  - ' || UN_CREDITOA       || ' + ' || UN_CREDITO || ' ,
                          CREDITO_AFECTADOCNT = CREDITO_AFECTADOCNT - ' || UN_CONTRACREDITOA || ' + ' || UN_CONTRACREDITO;    

            MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA ||''' 
                             AND ANO     = '   || MI_ANO      || ' 
                             AND TIPO    = ''' || UN_TIPO     || ''' 
                             AND NUMERO  = '   || UN_NUMERO   ||'';
            BEGIN
              BEGIN
                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'COMPROBANTE_PPTAL',
                                            UN_ACCION     => 'M',
                                            UN_CAMPOS     => MI_CAMPOS,
                                            UN_CONDICION  => MI_CONDICION
                                            );

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                END;

              IF MI_RTA <= 0 THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END IF;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                        MI_MSGERROR(1).CLAVE := 'TIPOCPTE';
                        MI_MSGERROR(1).VALOR :=  UN_TIPO;
                        MI_MSGERROR(2).CLAVE := 'NROCPTE';
                        MI_MSGERROR(2).VALOR :=  UN_NUMERO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                   UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECTADO,
                                                   UN_TABLAERROR => 'COMPROBANTE_PPTAL',
                                                   UN_REEMPLAZOS => MI_MSGERROR
                                                   );        
            END;

            --LA ASIGNACION ANTERIOR DE MI_CAMPOS APLICA PARA ESTA ACTUALIZACIÓN DEL DETALLE
            MI_CONDICION := 'COMPANIA         = ''' || UN_COMPANIA        || ''' 
                              AND ANO         = '   || MI_ANO             || ' 
                              AND TIPO_CPTE   = ''' || UN_TIPO            || ''' 
                              AND COMPROBANTE = '   || UN_NUMERO          || ' 
                              AND CUENTA      = ''' || UN_CUENTA          || ''' 
                              AND CONSECUTIVO = '   || UN_CONSECUTIVOPPTO || '';
            BEGIN
              BEGIN
                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'DETALLE_COMPROBANTE_PPTAL',
                                            UN_ACCION     => 'M',
                                            UN_CAMPOS     => MI_CAMPOS,
                                            UN_CONDICION  => MI_CONDICION
                                            );

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                END;

              IF MI_RTA <= 0 THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END IF;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                        MI_MSGERROR(1).CLAVE := 'TIPOCPTE';
                        MI_MSGERROR(1).VALOR :=  UN_TIPO;
                        MI_MSGERROR(2).CLAVE := 'NROCPTE';
                        MI_MSGERROR(2).VALOR :=  UN_NUMERO;
                        MI_MSGERROR(3).CLAVE := 'CUENTA';
                        MI_MSGERROR(3).VALOR :=  UN_CUENTA;
                        MI_MSGERROR(4).CLAVE := 'CONSECUTIVO';
                        MI_MSGERROR(4).VALOR :=  UN_CONSECUTIVO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                   UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECTADO_D,
                                                   UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL',
                                                   UN_REEMPLAZOS => MI_MSGERROR
                                                   );        
            END;
        ELSE
            --********************************************************************************************;
            MI_PARAMETRO :=  NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA,
                                                       UN_NOMBRE   =>'MANEJA CONTROL DE SOLICITUD DE DISPONIBILIDAD', 
                                                       UN_MODULO   =>UN_MODULO, 
                                                       UN_FECHA_PAR=>SYSDATE
                                                       ), 'NO');

            IF MI_RSCCODIGO = 'DIS' AND MI_PARAMETRO = 'SI' THEN

                MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>UN_COMPANIA,
                                                          UN_NOMBRE    =>'AGRUPAR SOLICITUD AFECTADA DE BANCO DE PROYECTOS', 
                                                          UN_MODULO    =>UN_MODULO,
                                                          UN_FECHA_PAR =>SYSDATE
                                                          ), 'NO');

                IF MI_PARAMETRO = 'SI' THEN 
                    MI_STRSQL := 'SELECT  BP_D_NOVEDADPROYECTO.CODIGO  CONSECUTIVO
                                       ,  BP_D_NOVEDADPROYECTO.VALORAPROBADO
                                    FROM  BP_D_NOVEDADPROYECTO 
                                   INNER JOIN V_PLAN_PRESUPUESTAL 
                                           ON BP_D_NOVEDADPROYECTO.RUBROPRESUPUESTAL = V_PLAN_PRESUPUESTAL.ID 
                                          AND BP_D_NOVEDADPROYECTO.ANORUBRO          = V_PLAN_PRESUPUESTAL.ANO 
                                          AND BP_D_NOVEDADPROYECTO.COMPANIA          = V_PLAN_PRESUPUESTAL.COMPANIA 
                                   WHERE  BP_D_NOVEDADPROYECTO.COMPANIA = '''||UN_COMPANIA||'''
                                     AND  TIPOT                         = '''||UN_TIPO    ||''' 
                                     AND  NOVEDAD                       = '  ||UN_NUMERO  ||'';

                    <<ACTUALIZARBP_D_NOVEDADPROYECTO>>
                    OPEN MI_RSDETALLE FOR MI_STRSQL;
                    LOOP
                        EXIT WHEN MI_RSDETALLE%NOTFOUND;
                        FETCH MI_RSDETALLE INTO MI_DETCONSECUTIVO
                                              , MI_DETVALOR; 
                        MI_CAMPOS := 'VALORAFECTADO = VALORAFECTADO - ' || MI_DETVALOR|| ' + ' || UN_CREDITO;
                        MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA|| '''  
                                         AND TIPOT   = ''' || UN_TIPO    || ''' 
                                         AND NOVEDAD = '   || UN_NUMERO  || '  
                                         AND CODIGO  = '   || MI_DETCONSECUTIVO;
                        BEGIN
                          BEGIN
                            MI_RTA    := PCK_DATOS.FC_ACME(UN_TABLA      => 'BP_D_NOVEDADPROYECTO',
                                                           UN_ACCION     => 'M',
                                                           UN_CAMPOS     => MI_CAMPOS,
                                                           UN_CONDICION  => MI_CONDICION
                                                           );

                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                            END;
                          --SE COMENTA PORQUE AUNQUE EL PROCESO DE ACTUALIZAR SE EJECUTO CORRECTAMENTE 
                          -- PERO NO EXISTEN DATOS QUE COINCIDAN. - SE DEJA SI EN FUTURAS VERIFICACIONES 
                          -- SE CONSIDERA QUE SE DEBE CONTROLAR
                          --IF MI_RTA <= 0 THEN
                          --  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                          --END IF;
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                                    MI_MSGERROR(1).CLAVE := 'TIPO';
                                    MI_MSGERROR(1).VALOR :=  UN_TIPO;
                                    MI_MSGERROR(2).CLAVE := 'NOVEDAD';
                                    MI_MSGERROR(2).VALOR :=  UN_NUMERO;
                                    MI_MSGERROR(3).CLAVE := 'CODIGO';
                                    MI_MSGERROR(3).VALOR :=  MI_DETCONSECUTIVO;
                                    MI_MSGERROR(4).CLAVE := 'COMPLEMENTO';
                                    MI_MSGERROR(4).VALOR :=  '-1-';
                                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                               UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECT_NOVPROY,
                                                               UN_TABLAERROR => 'BP_D_NOVEDADPROYECTO',
                                                               UN_REEMPLAZOS => MI_MSGERROR
                                                               );        
                        END;
                    END LOOP ACTUALIZARBP_D_NOVEDADPROYECTO;
                    CLOSE MI_RSDETALLE;
                ELSE
                  MI_CAMPOS := 'VALORAFECTADO = VALORAFECTADO - ' || UN_CREDITOA || ' + ' || UN_CREDITO;
                  MI_CONDICION := 'COMPANIA   = ''' || UN_COMPANIA    || ''' 
                                   AND TIPOT  = ''' || UN_TIPO        || ''' 
                                   AND NOVEDAD= '   || UN_NUMERO      || '  
                                   AND CODIGO = '   || UN_CONSECUTIVO || '';
                  BEGIN
                    BEGIN
                      MI_RTA    := PCK_DATOS.FC_ACME(UN_TABLA      => 'BP_D_NOVEDADPROYECTO',
                                                     UN_ACCION     => 'M',
                                                     UN_CAMPOS     => MI_CAMPOS,
                                                     UN_CONDICION  => MI_CONDICION
                                                     );

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                      END;

                    --SE COMENTA PORQUE AUNQUE EL PROCESO DE ACTUALIZAR SE EJECUTO CORRECTAMENTE 
                          -- PERO NO EXISTEN DATOS QUE COINCIDAN. - SE DEJA SI EN FUTURAS VERIFICACIONES 
                          -- SE CONSIDERA QUE SE DEBE CONTROLAR
                          --IF MI_RTA <= 0 THEN
                          --  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                          --END IF;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                              MI_MSGERROR(1).CLAVE := 'TIPO';
                              MI_MSGERROR(1).VALOR :=  UN_TIPO;
                              MI_MSGERROR(2).CLAVE := 'NOVEDAD';
                              MI_MSGERROR(2).VALOR :=  UN_NUMERO;
                              MI_MSGERROR(3).CLAVE := 'CODIGO';
                              MI_MSGERROR(3).VALOR :=  MI_DETCONSECUTIVO;
                              MI_MSGERROR(4).CLAVE := 'COMPLEMENTO';
                              MI_MSGERROR(4).VALOR :=  '-2-';
                              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                         UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECT_NOVPROY,
                                                         UN_TABLAERROR => 'BP_D_NOVEDADPROYECTO',
                                                         UN_REEMPLAZOS => MI_MSGERROR
                                                         );        
                  END;
                END IF;
            ELSIF MI_RSCAFECTACION IN('N','A','R') THEN
              --AFECTA LOS COMPROBANTES DE BANCO DE PROYECTOS;
              MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>UN_COMPANIA,
                                                        UN_NOMBRE    =>'MANEJA CONTROL DE SOLICITUD DE DISPONIBILIDAD', 
                                                        UN_MODULO    =>UN_MODULO, 
                                                        UN_FECHA_PAR =>SYSDATE
                                                        ), 'NO');

              IF MI_RSCCODIGO IN('RES', 'ADR','DMR') AND  MI_PARAMETRO = 'SI' THEN
                IF MI_RSCAFECTACION IN('N', 'A') THEN
                  --PRIMERO AFECTAMOS EL COMPROBANTE;
                  MI_CAMPOS := 'VALORAPROBADO=' || UN_CREDITO;
                  MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA  || ''' 
                                   AND TIPOT   = ''' || UN_TIPO0     || ''' 
                                   AND NOVEDAD = '   || UN_NUMERO0   || '  
                                   AND CODIGO  = '   || UN_CONSECUTIVOPPTO ;
                  BEGIN
                    BEGIN
                      MI_RTA    := PCK_DATOS.FC_ACME(UN_TABLA      => 'BP_D_NOVEDADPROYECTO',
                                                     UN_ACCION     => 'M',
                                                     UN_CAMPOS     =>  MI_CAMPOS,
                                                     UN_CONDICION  =>  MI_CONDICION
                                                     );

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                      END;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                              MI_MSGERROR(1).CLAVE := 'TIPO';
                              MI_MSGERROR(1).VALOR :=  UN_TIPO0;
                              MI_MSGERROR(2).CLAVE := 'NOVEDAD';
                              MI_MSGERROR(2).VALOR :=  UN_NUMERO0;
                              MI_MSGERROR(3).CLAVE := 'CODIGO';
                              MI_MSGERROR(3).VALOR :=  UN_CONSECUTIVO;
                              MI_MSGERROR(4).CLAVE := 'COMPLEMENTO';
                              MI_MSGERROR(4).VALOR :=  '-3-';
                              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                         UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECT_NOVPROY,
                                                         UN_TABLAERROR => 'BP_D_NOVEDADPROYECTO',
                                                         UN_REEMPLAZOS => MI_MSGERROR
                                                         );        
                  END;
                  BEGIN
                    --AHORA EL AFECTADO;
                    --AMONROY(15/01/2019) Se adiciona el join con la tabla BPNOVEDADPROYECTO, para traer numero y tipo documento afectar
                    SELECT BPNOVEDADPROYECTO.DOCUMENTO_AFECTAR 
                         ,  BPNOVEDADPROYECTO.TIPOT_AFECTAR 
                         ,  BP_D_NOVEDADPROYECTO.ITEM_AFECT  
                      INTO  MI_RSBCMPTE
                         ,  MI_RSBTIPO
                         ,  MI_RSBITEM
                      FROM  BPNOVEDADPROYECTO
                      INNER JOIN BP_D_NOVEDADPROYECTO
                         ON BPNOVEDADPROYECTO.COMPANIA     = BP_D_NOVEDADPROYECTO.COMPANIA
                        AND BPNOVEDADPROYECTO.TIPOT        = BP_D_NOVEDADPROYECTO.TIPOT
                        AND BPNOVEDADPROYECTO.CLASET       = BP_D_NOVEDADPROYECTO.CLASET
                        AND BPNOVEDADPROYECTO.CODIGO       = BP_D_NOVEDADPROYECTO.NOVEDAD
                        AND BPNOVEDADPROYECTO.DEPENDENCIA  = BP_D_NOVEDADPROYECTO.DEPENDENCIA    
                      WHERE BP_D_NOVEDADPROYECTO.COMPANIA  = UN_COMPANIA
                        AND BP_D_NOVEDADPROYECTO.TIPOT     = UN_TIPO
                        AND BP_D_NOVEDADPROYECTO.CLASET    = 'P'
                        AND BP_D_NOVEDADPROYECTO.NOVEDAD   = UN_NUMERO
                        AND BP_D_NOVEDADPROYECTO.CODIGO    = UN_CONSECUTIVO; 

                    IF MI_RSCCODIGO = 'RES' THEN --EL AFECTADO ES UNA SOLICITUD DE DIS Y SE AFECTAR EL VALOR APROBADO Y NO LA MODIFICACION;
                      MI_CAMPOS := 'VALORAPROBADO= ' || UN_CREDITO;
                      MI_CONDICION := 'COMPANIA     =  '''|| UN_COMPANIA|| '''
                                       AND TIPOT    =  '''|| MI_RSBTIPO || '''
                                       AND NOVEDAD  =  '  || MI_RSBCMPTE|| '
                                       AND CODIGO   =  '  || MI_RSBITEM ;
                      BEGIN
                        BEGIN
                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      =>  'BP_D_NOVEDADPROYECTO',
                                                      UN_ACCION     =>  'M', 
                                                      UN_CAMPOS     =>  MI_CAMPOS,
                                                      UN_CONDICION  => MI_CONDICION
                                                      );

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO ;
                         END;

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                                  MI_MSGERROR(1).CLAVE := 'TIPO';
                                  MI_MSGERROR(1).VALOR :=  MI_RSBTIPO;
                                  MI_MSGERROR(2).CLAVE := 'NOVEDAD';
                                  MI_MSGERROR(2).VALOR :=  MI_RSBCMPTE;
                                  MI_MSGERROR(3).CLAVE := 'CODIGO';
                                  MI_MSGERROR(3).VALOR :=  MI_RSBITEM;
                                  MI_MSGERROR(4).CLAVE := 'COMPLEMENTO';
                                  MI_MSGERROR(4).VALOR :=  '-4-';
                                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                             UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECT_NOVPROY,
                                                             UN_TABLAERROR => 'BP_D_NOVEDADPROYECTO',
                                                             UN_REEMPLAZOS => MI_MSGERROR
                                                             );        
                        END;
                    ELSE -- SI ERA ADR O DMR HAY QUE AFECTAR LA SOLICITUD DE DIS;
                      MI_CAMPOS := 'MODIFICACIONVALORAPROBADO = MODIFICACIONVALORAPROBADO - ' || UN_CREDITOA || ' + ' || UN_CREDITO; 
                      MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA || '''
                                       AND TIPOT   = ''' || MI_RSBTIPO  || '''
                                       AND NOVEDAD = '   || MI_RSBCMPTE || '
                                       AND CODIGO  = '   || MI_RSBITEM  ;
                      BEGIN
                        BEGIN
                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      =>  'BP_D_NOVEDADPROYECTO',
                                                      UN_ACCION     =>  'M',
                                                      UN_CAMPOS     =>  MI_CAMPOS,
                                                      UN_CONDICION  => MI_CONDICION
                                                      );

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO ;
                          END;

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                                  MI_MSGERROR(1).CLAVE := 'TIPO';
                                  MI_MSGERROR(1).VALOR :=  MI_RSBTIPO;
                                  MI_MSGERROR(2).CLAVE := 'NOVEDAD';
                                  MI_MSGERROR(2).VALOR :=  MI_RSBCMPTE;
                                  MI_MSGERROR(3).CLAVE := 'CODIGO';
                                  MI_MSGERROR(3).VALOR :=  MI_RSBITEM;
                                  MI_MSGERROR(4).CLAVE := 'COMPLEMENTO';
                                  MI_MSGERROR(4).VALOR :=  '-5-';
                                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                             UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECT_NOVPROY,
                                                             UN_TABLAERROR => 'BP_D_NOVEDADPROYECTO',
                                                             UN_REEMPLAZOS => MI_MSGERROR
                                                             );        
                        END;
                      BEGIN
                        SELECT  TIPO_CPTE_AFECT
                             ,  CMPTE_AFECTADO
                             ,  ITEM_AFECT
                          INTO  MI_RSBTIPO
                             ,  MI_RSBCMPTE
                             ,  MI_RSBITEM
                          FROM  BP_D_NOVEDADPROYECTO
                         WHERE  COMPANIA  = UN_COMPANIA 
                           AND  TIPOT     = MI_RSBTIPO
                           AND  CLASET    = 'P'
                           AND  NOVEDAD   = MI_RSBCMPTE 
                           AND  CODIGO    = MI_RSBITEM;

                        MI_CAMPOS := 'VALORAPROBADO=' || UN_CREDITO ;
                        MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA|| '''
                                         AND TIPOT   = ''' || MI_RSBTIPO || '''
                                         AND NOVEDAD =   ' || MI_RSBCMPTE|| '
                                         AND CODIGO  =   ' || MI_RSBITEM ;                                                                                           
                        BEGIN
                          BEGIN
                            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'BP_D_NOVEDADPROYECTO',
                                                        UN_ACCION     => 'M',
                                                        UN_CAMPOS     => MI_CAMPOS,
                                                        UN_CONDICION  => MI_CONDICION
                                                        );

                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO ;
                            END;
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                                    MI_MSGERROR(1).CLAVE := 'TIPO';
                                    MI_MSGERROR(1).VALOR :=  MI_RSBTIPO;
                                    MI_MSGERROR(2).CLAVE := 'NOVEDAD';
                                    MI_MSGERROR(2).VALOR :=  MI_RSBCMPTE;
                                    MI_MSGERROR(3).CLAVE := 'CODIGO';
                                    MI_MSGERROR(3).VALOR :=  MI_RSBITEM;
                                    MI_MSGERROR(4).CLAVE := 'COMPLEMENTO';
                                    MI_MSGERROR(4).VALOR :=  '-6-';
                                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                               UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECT_NOVPROY,
                                                               UN_TABLAERROR => 'BP_D_NOVEDADPROYECTO',
                                                               UN_REEMPLAZOS => MI_MSGERROR
                                                               );        
                        END;
                        EXCEPTION WHEN NO_DATA_FOUND THEN
                            MI_RTA := 0;
                        END;
                    END IF;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                      MI_RTA := 0;
                    END; 
                ELSIF MI_RSCAFECTACION = 'R' THEN 
                  --PRIMERO AFECTAMOS EL COMPROBANTE;
                  MI_CAMPOS := 'VALORAPROBADO =' || UN_CONTRACREDITO;
                  MI_CONDICION := 'COMPANIA    =''' || UN_COMPANIA || ''' 
                                   AND TIPOT   =''' || UN_TIPO0    || ''' 
                                   AND NOVEDAD =  ' || UN_NUMERO0  || '  
                                   AND CODIGO  =  ' || UN_CONSECUTIVO ;
                  BEGIN
                    BEGIN
                      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'BP_D_NOVEDADPROYECTO',
                                                  UN_ACCION     => 'M',
                                                  UN_CAMPOS     => MI_CAMPOS,
                                                  UN_CONDICION  => MI_CONDICION
                                                  );

                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO ;
                      END;

                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                              MI_MSGERROR(1).CLAVE := 'TIPO';
                              MI_MSGERROR(1).VALOR :=  UN_TIPO0;
                              MI_MSGERROR(2).CLAVE := 'NOVEDAD';
                              MI_MSGERROR(2).VALOR :=  UN_NUMERO0;
                              MI_MSGERROR(3).CLAVE := 'CODIGO';
                              MI_MSGERROR(3).VALOR :=  UN_CONSECUTIVO;
                              MI_MSGERROR(4).CLAVE := 'COMPLEMENTO';
                              MI_MSGERROR(4).VALOR :=  '-7-';
                              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                         UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECT_NOVPROY,
                                                         UN_TABLAERROR => 'BP_D_NOVEDADPROYECTO',
                                                         UN_REEMPLAZOS => MI_MSGERROR
                                                         );        
                  END;      
                  BEGIN
                    --AHORA EL AFECTADO;
                    --AMONROY(15/01/2019) Se adiciona el join con la tabla BPNOVEDADPROYECTO, para traer numero y tipo documento afectar
                    SELECT BPNOVEDADPROYECTO.DOCUMENTO_AFECTAR 
                         ,  BPNOVEDADPROYECTO.TIPOT_AFECTAR 
                         ,  BP_D_NOVEDADPROYECTO.ITEM_AFECT  
                      INTO  MI_RSBCMPTE
                         ,  MI_RSBTIPO
                         ,  MI_RSBITEM
                      FROM  BPNOVEDADPROYECTO
                      INNER JOIN BP_D_NOVEDADPROYECTO
                         ON BPNOVEDADPROYECTO.COMPANIA     = BP_D_NOVEDADPROYECTO.COMPANIA
                        AND BPNOVEDADPROYECTO.TIPOT        = BP_D_NOVEDADPROYECTO.TIPOT
                        AND BPNOVEDADPROYECTO.CLASET       = BP_D_NOVEDADPROYECTO.CLASET
                        AND BPNOVEDADPROYECTO.CODIGO       = BP_D_NOVEDADPROYECTO.NOVEDAD
                        AND BPNOVEDADPROYECTO.DEPENDENCIA  = BP_D_NOVEDADPROYECTO.DEPENDENCIA    
                      WHERE BP_D_NOVEDADPROYECTO.COMPANIA  = UN_COMPANIA
                        AND BP_D_NOVEDADPROYECTO.TIPOT     = UN_TIPO
                        AND BP_D_NOVEDADPROYECTO.CLASET    = 'P'
                        AND BP_D_NOVEDADPROYECTO.NOVEDAD   = UN_NUMERO
                        AND BP_D_NOVEDADPROYECTO.CODIGO    = UN_CONSECUTIVO; 

                    IF MI_RSCCODIGO = 'RES' THEN --EL AFECTADO ES UNA SOLICITUD DE DIS Y SE AFECTAR EL VALOR APROBADO Y NO LA MODIFICACION;
                       MI_CAMPOS := 'VALORAPROBADO  = '   || UN_CONTRACREDITO;
                       MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA || '''
                                        AND TIPOT   = ''' || MI_RSBTIPO  || '''
                                        AND NOVEDAD =   ' || MI_RSBCMPTE || '
                                        AND CODIGO  =   ' || MI_RSBITEM  ;
                      BEGIN
                        BEGIN
                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'BP_D_NOVEDADPROYECTO',
                                                      UN_ACCION     => 'M', 
                                                      UN_CAMPOS     => MI_CAMPOS,
                                                      UN_CONDICION  => MI_CONDICION
                                                      );

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO ;
                          END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                                  MI_MSGERROR(1).CLAVE := 'TIPO';
                                  MI_MSGERROR(1).VALOR :=  MI_RSBTIPO;
                                  MI_MSGERROR(2).CLAVE := 'NOVEDAD';
                                  MI_MSGERROR(2).VALOR :=  MI_RSBCMPTE;
                                  MI_MSGERROR(3).CLAVE := 'CODIGO';
                                  MI_MSGERROR(3).VALOR :=  MI_RSBITEM;
                                  MI_MSGERROR(4).CLAVE := 'COMPLEMENTO';
                                  MI_MSGERROR(4).VALOR :=  '-8-';
                                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                             UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECT_NOVPROY,
                                                             UN_TABLAERROR => 'BP_D_NOVEDADPROYECTO',
                                                             UN_REEMPLAZOS => MI_MSGERROR
                                                             );        
                      END;      
                    ELSE -- SI ERA ADR O DMR HAY QUE AFECTAR LA SOLICITUD DE DIS;
                      MI_CAMPOS := 'MODIFICACIONVALORAPROBADO = MODIFICACIONVALORAPROBADO - ' || UN_CONTRACREDITOA || ' + ' || UN_CONTRACREDITO; 
                      MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA || '''
                                       AND TIPOT   = ''' || MI_RSBTIPO  || '''
                                       AND NOVEDAD =   ' || MI_RSBCMPTE || '
                                       AND CODIGO  =   ' || MI_RSBITEM  ;
                      BEGIN
                        BEGIN
                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'BP_D_NOVEDADPROYECTO',
                                                      UN_ACCION     => 'M', 
                                                      UN_CAMPOS     => MI_CAMPOS,
                                                      UN_CONDICION  => MI_CONDICION
                                                      );

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO ;
                          END;

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                                  MI_MSGERROR(1).CLAVE := 'TIPO';
                                  MI_MSGERROR(1).VALOR :=  MI_RSBTIPO;
                                  MI_MSGERROR(2).CLAVE := 'NOVEDAD';
                                  MI_MSGERROR(2).VALOR :=  MI_RSBCMPTE;
                                  MI_MSGERROR(3).CLAVE := 'CODIGO';
                                  MI_MSGERROR(3).VALOR :=  MI_RSBITEM;
                                  MI_MSGERROR(4).CLAVE := 'COMPLEMENTO';
                                  MI_MSGERROR(4).VALOR :=  '-9-';
                                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                             UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECT_NOVPROY,
                                                             UN_TABLAERROR => 'BP_D_NOVEDADPROYECTO',
                                                             UN_REEMPLAZOS => MI_MSGERROR
                                                             );        
                      END;           
                      BEGIN
                        SELECT  TIPO_CPTE_AFECT
                             ,  CMPTE_AFECTADO
                             ,  ITEM_AFECT
                          INTO  MI_RSBTIPO
                             ,  MI_RSBCMPTE
                             ,  MI_RSBITEM
                          FROM  BP_D_NOVEDADPROYECTO
                         WHERE  COMPANIA = UN_COMPANIA
                           AND  TIPOT    = MI_RSBTIPO
                           AND  CLASET   = 'P'
                           AND  NOVEDAD  = MI_RSBCMPTE
                           AND  CODIGO   = MI_RSBITEM; 

                        MI_CAMPOS := 'VALORAPROBADO  = '   || UN_CONTRACREDITO ;
                        MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA|| '''
                                         AND TIPOT   = ''' || MI_RSBTIPO || '''
                                         AND NOVEDAD =   ' || MI_RSBCMPTE|| '
                                         AND CODIGO  =   ' || MI_RSBITEM ;
                        BEGIN
                          BEGIN
                            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      =>  'BP_D_NOVEDADPROYECTO',
                                                        UN_ACCION     =>  'M',
                                                        UN_CAMPOS     =>  MI_CAMPOS,
                                                        UN_CONDICION  =>  MI_CONDICION
                                                        );

                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO ;
                            END;

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                                    MI_MSGERROR(1).CLAVE := 'TIPO';
                                    MI_MSGERROR(1).VALOR :=  MI_RSBTIPO;
                                    MI_MSGERROR(2).CLAVE := 'NOVEDAD';
                                    MI_MSGERROR(2).VALOR :=  MI_RSBCMPTE;
                                    MI_MSGERROR(3).CLAVE := 'CODIGO';
                                    MI_MSGERROR(3).VALOR :=  MI_RSBITEM;
                                    MI_MSGERROR(4).CLAVE := 'COMPLEMENTO';
                                    MI_MSGERROR(4).VALOR :=  '-10-';
                                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                               UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECT_NOVPROY,
                                                               UN_TABLAERROR => 'BP_D_NOVEDADPROYECTO',
                                                               UN_REEMPLAZOS => MI_MSGERROR
                                                               );        
                        END;           
                        EXCEPTION WHEN NO_DATA_FOUND THEN
                          MI_RTA := 0;
                        END;
                    END IF;
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                      MI_RTA := 0;
                    END; 
              END IF;
            END IF;
          END IF;
            --*********************************************************;
          IF(UN_CREDITOA<>UN_CREDITO OR UN_CONTRACREDITOA<>UN_CONTRACREDITO) THEN
            IF MI_RSCAFECTACION = 'N' THEN

              MI_CAMPOS := 'DEBITO_AFECTADO = DEBITO_AFECTADO - ' || UN_CREDITOA || ' + ' || UN_CREDITO || '
                          , CREDITO_AFECTADO = CREDITO_AFECTADO - ' || UN_CONTRACREDITOA || ' + ' || UN_CONTRACREDITO;
              MI_CONDICION := ' COMPANIA   =''' || UN_COMPANIA || ''' 
                                AND ANO    =  ' || MI_ANO      || ' 
                                AND TIPO   =''' || UN_TIPO     || ''' 
                                AND NUMERO =  ' || UN_NUMERO   || '';                
              BEGIN
                BEGIN
                  MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      =>  'COMPROBANTE_PPTAL', 
                                              UN_ACCION     =>  'M', 
                                              UN_CAMPOS     =>  MI_CAMPOS, 
                                              UN_CONDICION  =>  MI_CONDICION
                                              );

                  IF MI_RTA <= 0 AND MI_RSCCODIGO <> 'DIS' THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                  END IF;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO ;

                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                          MI_MSGERROR(1).CLAVE := 'TIPOCPTE';
                          MI_MSGERROR(1).VALOR :=  UN_TIPO;
                          MI_MSGERROR(2).CLAVE := 'NROCPTE';
                          MI_MSGERROR(2).VALOR :=  UN_NUMERO;
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                     UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECTADO,
                                                     UN_TABLAERROR => 'COMPROBANTE_PPTAL',
                                                     UN_REEMPLAZOS => MI_MSGERROR
                                                     );        
              END; 

              MI_CAMPOS := 'DEBITO_AFECTADO = DEBITO_AFECTADO - ' || UN_CREDITOA || ' + ' || UN_CREDITO || '
                          , CREDITO_AFECTADO = CREDITO_AFECTADO - ' || UN_CONTRACREDITOA || ' + ' || UN_CONTRACREDITO;
              MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA || ''' 
                               AND ANO         = '   || MI_ANO      || ' 
                               AND TIPO_CPTE   = ''' || UN_TIPO     || ''' 
                               AND COMPROBANTE = '   || UN_NUMERO   || ' 
                               AND CUENTA      = ''' || UN_CUENTA   || ''' 
                               AND CONSECUTIVO = '   || UN_CONSECUTIVOPPTO;
              BEGIN
                BEGIN
                  MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      =>  'DETALLE_COMPROBANTE_PPTAL', 
                                              UN_ACCION     =>  'M', 
                                              UN_CAMPOS     =>  MI_CAMPOS,
                                              UN_CONDICION  =>  MI_CONDICION
                                              );

                  IF MI_RTA <= 0 AND MI_RSCCODIGO <> 'DIS' THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                  END IF;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO ;
                  END;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                          MI_MSGERROR(1).CLAVE := 'TIPOCPTE';
                          MI_MSGERROR(1).VALOR :=  UN_TIPO;
                          MI_MSGERROR(2).CLAVE := 'NROCPTE';
                          MI_MSGERROR(2).VALOR :=  UN_NUMERO;
                          MI_MSGERROR(3).CLAVE := 'CUENTA';
                          MI_MSGERROR(3).VALOR :=  UN_CUENTA;
                          MI_MSGERROR(4).CLAVE := 'CONSECUTIVO';
                          MI_MSGERROR(4).VALOR :=  UN_CONSECUTIVO;
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                     UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECTADO_D,
                                                     UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL',
                                                     UN_REEMPLAZOS => MI_MSGERROR
                                                     );        
                END; 

                  --SI AFECTACION EL = R;
            ELSE
              MI_CAMPOS := 'MODIFICACION_DEBITO = MODIFICACION_DEBITO - ' || UN_CREDITOA || ' + ' || UN_CREDITO || '
                          , MODIFICACION_CREDITO = MODIFICACION_CREDITO - ' || UN_CONTRACREDITOA || ' + ' || UN_CONTRACREDITO;
              MI_CONDICION := ' COMPANIA   = ''' || UN_COMPANIA || ''' 
                                AND ANO    = '   || MI_ANO      || ' 
                                AND TIPO   = ''' || UN_TIPO     || ''' 
                                AND NUMERO = '   || UN_NUMERO   || '';
              BEGIN
                BEGIN
                  MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      =>  'COMPROBANTE_PPTAL',
                                              UN_ACCION     =>  'M', 
                                              UN_CAMPOS     =>  MI_CAMPOS, 
                                              UN_CONDICION  =>  MI_CONDICION
                                              );
                  IF MI_RTA <= 0 THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                  END IF;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO ;
                  END;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                          MI_MSGERROR(1).CLAVE := 'TIPOCPTE';
                          MI_MSGERROR(1).VALOR :=  UN_TIPO;
                          MI_MSGERROR(2).CLAVE := 'NROCPTE';
                          MI_MSGERROR(2).VALOR :=  UN_NUMERO;
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                     UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECTADO,
                                                     UN_TABLAERROR => 'COMPROBANTE_PPTAL',
                                                     UN_REEMPLAZOS => MI_MSGERROR
                                                     );        
                END;

              MI_CAMPOS := 'MODIFICACION_DEBITO = MODIFICACION_DEBITO - ' || UN_CREDITOA || ' + ' || UN_CREDITO || '
                          , MODIFICACION_CREDITO = MODIFICACION_CREDITO - ' || UN_CONTRACREDITOA || ' + ' || UN_CONTRACREDITO;
              MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA || ''' 
                               AND ANO         = '   || MI_ANO      || ' 
                               AND TIPO_CPTE   = ''' || UN_TIPO     || ''' 
                               AND COMPROBANTE = '   || UN_NUMERO   || ' 
                               AND CUENTA      = ''' || UN_CUENTA   || ''' 
                               AND CONSECUTIVO = '   || UN_CONSECUTIVOPPTO;
              BEGIN
                BEGIN
                  MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      =>  'DETALLE_COMPROBANTE_PPTAL',
                                              UN_ACCION     =>  'M', 
                                              UN_CAMPOS     =>  MI_CAMPOS, 
                                              UN_CONDICION  =>  MI_CONDICION
                                              );

                  IF MI_RTA <= 0  THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                  END IF;

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO ;
                  END;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                          MI_MSGERROR(1).CLAVE := 'TIPOCPTE';
                          MI_MSGERROR(1).VALOR :=  UN_TIPO;
                          MI_MSGERROR(2).CLAVE := 'NROCPTE';
                          MI_MSGERROR(2).VALOR :=  UN_NUMERO;
                          MI_MSGERROR(3).CLAVE := 'CUENTA';
                          MI_MSGERROR(3).VALOR :=  UN_CUENTA;
                          MI_MSGERROR(4).CLAVE := 'CONSECUTIVO';
                          MI_MSGERROR(4).VALOR :=  UN_CONSECUTIVO;
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                     UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECTADO_D,
                                                     UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL',
                                                     UN_REEMPLAZOS => MI_MSGERROR
                                                     );        
                END; 

              IF MI_RSCAFECTACION IN('R', 'A') THEN
                BEGIN
                  SELECT  CLASECNTPRES.CLASEAFECTAR 
                    INTO  MI_CLASEAFECTAR
                    FROM  TIPO_COMPROBPP 
                    LEFT JOIN CLASECNTPRES 
                           ON TIPO_COMPROBPP.CLASE = CLASECNTPRES.CODIGO 
                   WHERE  TIPO_COMPROBPP.COMPANIA  = UN_COMPANIA
                     AND  TIPO_COMPROBPP.CODIGO    = UN_TIPO 
                   ORDER BY TIPO_COMPROBPP.CODIGO;

                  IF LENGTH(MI_CLASEAFECTAR) > 0  THEN
                    IF MI_RSAFOUND NOT IN (0) AND MI_RSATIPO_CPTE_AFECT IS NOT NULL AND MI_RSACMPTE_AFECTADO IS NOT NULL THEN
                      BEGIN                                                  
                        MI_CAMPOS := 'DEBITO_AFECTADO= DEBITO_AFECTADO - ' || UN_CREDITOA || ' + ' || UN_CREDITO || '
                                    , CREDITO_AFECTADO = CREDITO_AFECTADO - ' || UN_CONTRACREDITOA || ' + ' || UN_CONTRACREDITO;
                        MI_CONDICION := 'COMPANIA   = ''' || UN_COMPANIA           || ''' 
                                         AND ANO    = '   || MI_ANO                || ' 
                                         AND TIPO   = ''' || MI_RSATIPO_CPTE_AFECT || ''' 
                                         AND NUMERO = '   || MI_RSACMPTE_AFECTADO  || '';
                        BEGIN
                          BEGIN
                            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      =>  'COMPROBANTE_PPTAL',
                                                        UN_ACCION     =>  'M',
                                                        UN_CAMPOS     =>  MI_CAMPOS,
                                                        UN_CONDICION  =>  MI_CONDICION
                                                        );

                            IF MI_RTA <= 0 THEN
                              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                            END IF;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO ;
                            END;

                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                                    MI_MSGERROR(1).CLAVE := 'TIPOCPTE';
                                    MI_MSGERROR(1).VALOR :=  MI_RSATIPO_CPTE_AFECT;
                                    MI_MSGERROR(2).CLAVE := 'NROCPTE';
                                    MI_MSGERROR(2).VALOR :=  MI_RSACMPTE_AFECTADO;
                                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                               UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECTADO,
                                                               UN_TABLAERROR => 'COMPROBANTE_PPTAL',
                                                               UN_REEMPLAZOS => MI_MSGERROR
                                                               );        
                          END;              

                        MI_CAMPOS := 'DEBITO_AFECTADO = DEBITO_AFECTADO - ' || UN_CREDITOA || ' + ' || UN_CREDITO || '
                                    , CREDITO_AFECTADO = CREDITO_AFECTADO- ' || UN_CONTRACREDITOA || ' + ' || UN_CONTRACREDITO;
                        MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA           || ''' 
                                         AND ANO         = '   || MI_ANO                || ' 
                                         AND TIPO_CPTE   = ''' || MI_RSATIPO_CPTE_AFECT || ''' 
                                         AND COMPROBANTE = '   || MI_RSACMPTE_AFECTADO  || ' 
                                         AND CUENTA      = ''' || MI_RSACUENTA          || ''' 
                                         AND CONSECUTIVO = '   || MI_RSACONSECUTIVOPPTO || '';
                        BEGIN
                          BEGIN
                            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      =>  'DETALLE_COMPROBANTE_PPTAL',
                                                        UN_ACCION     =>  'M',
                                                        UN_CAMPOS     =>  MI_CAMPOS,
                                                        UN_CONDICION  =>  MI_CONDICION
                                                        );

                            IF MI_RTA <= 0  THEN
                              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                            END IF;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO ;
                            END;
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                                    MI_MSGERROR(1).CLAVE := 'TIPOCPTE';
                                    MI_MSGERROR(1).VALOR :=  MI_RSATIPO_CPTE_AFECT;
                                    MI_MSGERROR(2).CLAVE := 'NROCPTE';
                                    MI_MSGERROR(2).VALOR :=  MI_RSACMPTE_AFECTADO;
                                    MI_MSGERROR(3).CLAVE := 'CUENTA';
                                    MI_MSGERROR(3).VALOR :=  MI_RSACUENTA;
                                    MI_MSGERROR(4).CLAVE := 'CONSECUTIVO';
                                    MI_MSGERROR(4).VALOR :=  MI_RSACONSECUTIVOPPTO;
                                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                               UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTCPTEAFECTADO_D,
                                                               UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL',
                                                               UN_REEMPLAZOS => MI_MSGERROR
                                                               );        
                        END;               
                        EXCEPTION WHEN NO_DATA_FOUND THEN
                          MI_RTA := 0;
                        END;
                    END IF;
                  END IF;
                  EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_RTA := 0;
                  END;
              END IF;
            END IF;
          END IF;
        END IF;
    -- Inserción de la afectación, verificando si ya existe el dato
    DECLARE 
      MI_EXISTE                        VARCHAR2(3 CHAR);
    BEGIN
      SELECT  DISTINCT 'X'
      INTO    MI_EXISTE
      FROM    COMPROBANTE_PPTALAFECTADOS
      WHERE   COMPANIA          = UN_COMPANIA
        AND   ANO               = UN_ANO0
        AND   TIPO_CPTE         = UN_TIPO0
        AND   COMPROBANTE       = UN_NUMERO0
        AND   ANO_AFECT         = UN_ANO
        AND   TIPO_CPTE_AFECT   = UN_TIPO
        AND   COMPROBANTE_AFECT = UN_NUMERO; 
      EXCEPTION WHEN NO_DATA_FOUND THEN
        BEGIN
          BEGIN
            MI_CAMPOS := 'COMPANIA, ANO, TIPO_CPTE, COMPROBANTE,ANO_AFECT,TIPO_CPTE_AFECT,COMPROBANTE_AFECT,CREATED_BY,DATE_CREATED';
            MI_VALORES := '''' || UN_COMPANIA || ''',' || UN_ANO0 || ' , 
                          '''  || UN_TIPO0    || ''',' || UN_NUMERO0 || ',' || UN_ANO || ',
                          '''  || UN_TIPO    || ''',' || UN_NUMERO || ',
                          '''  || UN_USUARIO  || ''', SYSDATE';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'COMPROBANTE_PPTALAFECTADOS', 
                                                  UN_ACCION  => 'I', 
                                                  UN_CAMPOS  => MI_CAMPOS, 
                                                  UN_VALORES => MI_VALORES);                 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_INSER_CPTAFECTADOS,
              UN_TABLAERROR => 'COMPROBANTE_PPTALAFECTADOS'
            );
        END;  
    END;
   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD =>SQLCODE,
                  UN_ERROR_COD=>PCK_ERRORES.ERR_PPTO_AFECTOTROCOMPROBANTE
  );     

END PR_AFECTAROTROCOMPROBANTE;

FUNCTION FC_LIBERARCOMPROBANTE
  /*
  NAME              : FC_LIBERARCOMPROBANTE
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : Diego Maldonado
  DATE MIGRADOR     : 10/08/2016
  TIME              : 3:30 PM
  SOURCE MODULE     : SysmanPR2016.03.02.accdb
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Permite ejecutar la liberación del comprobante presupuestal.
  @NAME:  liberarComprobantePresupuestal
  @METHOD:  GET        
  */
  (

  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,             -- Parametro que recibe el numero de compania
  UN_MODULO                   IN PCK_SUBTIPOS.TI_MODULO,               -- Parametro que recibe el numero del modulo
  UN_ANO                      IN PCK_SUBTIPOS.TI_ANIO,                 -- Parametro que recibe el ano del comprobante a liberar
  UN_TIPO                     IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL, -- Parametro que recibe el tipo de comprobante a liberar
  UN_NUMERO                   IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT, -- Parametro que recibe el numero de comprobante a liberar
  UN_CONSECUTIVO              IN PCK_SUBTIPOS.TI_CONSECUTIVOPPTAL,     -- Parametro que recibe el consecutivo del comprobante a liberar
  UN_VALORNETO                IN PCK_SUBTIPOS.TI_DOBLE,                -- Parametro que recibe el valor neto del comprobante
  UN_FECHA                    IN DATE,                                 -- Parametro que recibe la fecha del comprobante a liberar
  UN_CUENTA                   IN PCK_SUBTIPOS.TI_CODIGOPPTAL,          -- Parametro que recibe el codigo de la cuenta a liberar
  UN_TERCERO                  IN PCK_SUBTIPOS.TI_TERCERO,              -- Parametro que recibe el codigo del tercero 
  UN_SUCURSAL                 IN PCK_SUBTIPOS.TI_SUCURSAL,             -- Parametro que recibe el codigo de la sucursal
  UN_CENTRO_COSTO             IN PCK_SUBTIPOS.TI_CENTRO_COSTO,         -- Parametro que recibe el codigo del centro de costo 
  UN_AUXILIAR                 IN PCK_SUBTIPOS.TI_AUXILIAR,             -- Parametro que recibe el codigo del auxiliar
  UN_FUENTE_RECURSO           IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS,      -- Parametro que recibe el codigo de la Fuente de Recurso
  UN_REFERENCIA               IN PCK_SUBTIPOS.TI_REFERENCIA,           -- Parametro que recibe el codigo de la Referencia
  UN_NATURALEZA               IN PCK_SUBTIPOS.TI_NATURALEZACONTA,      -- Parametro que recibe el valor de la naturaleza
  UN_DESCRIPCION_USUARIO      IN PCK_SUBTIPOS.TI_DESCRIPCION,          -- Parámetro que recibe la descripción escrita por usuario
  UN_USUARIO                  IN COMPROBANTE_PPTAL.CREATED_BY%TYPE     -- Parametro que recibe el usuario 
  )RETURN VARCHAR2
  AS 
    MI_ERROR_FUN              PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 1;
    -- Variable que almacenara el codigo de la clase a liberar
    MI_CLASELIBERAR           VARCHAR(5 CHAR) := 'DIS';
    -- variable que almacenara el comprobante a liberar
    MI_LIBERARCOMPROBANTE     VARCHAR2(140 CHAR);
    -- Variable que almacenara las consultas a ejecutar
    MI_STRSQL                 PCK_SUBTIPOS.TI_STRSQL; 
    -- Variable que almacenara el tipo de comprobante a crear
    MI_TIPOACREAR             PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL;
    -- Variable que almacenara el numero de comprobante a crear
    MI_NUMEROACREAR           PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
    -- Variable que almacenara la descripcion del comprobante
    MI_DESCRIPCION            COMPROBANTE_PPTAL.DESCRIPCION%TYPE; 
    -- Variable que almacenara la fecha de vencimiento del comprobante a liberar
    MI_FECHAVENCIMIENTO       DATE;
    -- Variable que almacenara la clase interes consulta retornada en la consulta
    MI_COLUMNA                VARCHAR2(4000 CHAR); 
    -- Variable que almacenara la clase de comprobante a liberar
    MI_CLASE                  PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL; 
    -- Variable que almacenara el valor de la diferencia
    MI_DIFERENCIA             PCK_SUBTIPOS.TI_DOBLE;
    -- Variable que almacenara la respuesta a retornar
    MI_RTA                    PCK_SUBTIPOS.TI_ENTERO_LARGO;
    -- Variable que almacenara el valor maximo 
    MI_MAXIMO                 PCK_SUBTIPOS.TI_DOBLE;
    -- Variable que almacenara el ano 
    MI_ANO                    PCK_SUBTIPOS.TI_ANIO;
    -- Variable que almacenara el consecutivo
    MI_CONSECUTIVO            PCK_SUBTIPOS.TI_CONSECUTIVOCNT;
    -- Variable que almacenara el mensaje a afectar
    MI_MENSAJESAFECTAR        VARCHAR2(10 CHAR);
    -- Variable que almacenara el valor de los campos a actualizar
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacenara los valores a usar en la actualizacion
    MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
    MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN
      --EVALUAR CLASE DEL TIPO --SOLO EJECUTA SI LA CLASE ES DIS
      BEGIN
        BEGIN
          BEGIN
            SELECT  CLASE 
              INTO  MI_CLASE
              FROM  TIPO_COMPROBPP 
             WHERE  COMPANIA = UN_COMPANIA
               AND  CODIGO   = UN_TIPO;

            EXCEPTION WHEN NO_DATA_FOUND THEN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END ;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_LIBCPTE_TIPO
                                       );         
          END ;
        IF MI_CLASE <> MI_CLASELIBERAR  THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END IF; 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_LIBCPTE_CLASEINC
                                     );
        END ;

      --SE BUSCA TIPO DE COMPROBANTE QUE SE VA A CREAR PARA REDUCIR EL VALOR;
      BEGIN  
        BEGIN
            SELECT  TIPO_COMPROBPP_1.CODIGO
                 ,  CLASECNTPRES.COLUMNA
                 ,  TIPO_COMPROBPP_1.CLASE
              INTO  MI_TIPOACREAR
                 ,  MI_COLUMNA
                 ,  MI_CLASE
              FROM  TIPO_COMPROBPP 
              LEFT JOIN CLASECNTPRES 
                     ON TIPO_COMPROBPP.CLASE = CLASECNTPRES.CLASEAFECTAR
              LEFT JOIN TIPO_COMPROBPP TIPO_COMPROBPP_1 
                     ON CLASECNTPRES.CODIGO  = TIPO_COMPROBPP_1.CODIGO 
             WHERE  TIPO_COMPROBPP.COMPANIA   = UN_COMPANIA
               AND  TIPO_COMPROBPP.CODIGO     = UN_TIPO
               AND  TIPO_COMPROBPP_1.COMPANIA = UN_COMPANIA
               AND  CLASECNTPRES.AFECTACION   =  'R';
            -- SI DA MAS DE UN REGISTRO REVISAR CONFIGURACION 

            EXCEPTION WHEN TOO_MANY_ROWS THEN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END ;

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                               UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_LIBCPTE_MANYROWS
                                               );         
          END;

      --SE BUSCA EL NÚMERO CONSECUTIVO DEL COMPROBANTE;
      SELECT  MAX(NUMERO) MAXIMO
        INTO  MI_MAXIMO
        FROM  COMPROBANTE_PPTAL 
       WHERE  COMPANIA = UN_COMPANIA 
         AND  TIPO     = MI_TIPOACREAR
         AND  ANO      = UN_ANO  
       ORDER BY COMPANIA
           , ANO
           , TIPO
           , NUMERO; 

      IF MI_MAXIMO NOT IN (0) THEN
            MI_NUMEROACREAR := MI_MAXIMO + 1;
      ELSE
        BEGIN
          SELECT  CONSECUTIVO 
            INTO  MI_CONSECUTIVO
            FROM  CONSECUTIVOTCP 
           WHERE  COMPANIA        =   UN_COMPANIA
             AND  TIPOCOMPROBANTE =   MI_TIPOACREAR
             AND  ANO             =   UN_ANO 
           ORDER BY COMPANIA
               ,  TIPOCOMPROBANTE
               ,  ANO;

          MI_NUMEROACREAR := UN_ANO || PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   =>MI_CONSECUTIVO,
                                                                 UN_LONGITUD =>6);
          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_NUMEROACREAR := PCK_SYSMAN_UTL.FC_STRZERO(UN_NUMERO   =>UN_ANO,
                                                         UN_LONGITUD =>4) ||'000001';
            MI_CAMPOS := '  COMPANIA
                          , ANO
                          , TIPOCOMPROBANTE
                          , CONSECUTIVO';
            MI_VALORES := ' ''' || UN_COMPANIA   || '''
                           ,'   || UN_ANO        || '
                           ,''' || MI_TIPOACREAR || '''                         
                           ,1';
            BEGIN
              BEGIN
                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA =>'CONSECUTIVOTCP',
                                            UN_ACCION => 'I',
                                            UN_CAMPOS => MI_CAMPOS,
                                            UN_VALORES => MI_VALORES
                                            );

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                END ;

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                        MI_MSGERROR(1).CLAVE := 'TIPOCPTE';
                        MI_MSGERROR(1).VALOR := MI_TIPOACREAR;
                        MI_MSGERROR(2).CLAVE := 'ANO';
                        MI_MSGERROR(2).CLAVE := UN_ANO;
                          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                     UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_CREARCONSECUTIVO,
                                                     UN_REEMPLAZOS => MI_MSGERROR
                                                     );
              END;
        END;
      END IF;
      --ARMO LA DESCRIPCIÓN
      MI_DESCRIPCION := 'Reducción automática del comprobante' || UN_TIPO || ' número ' || UN_NUMERO || ' ' || UN_DESCRIPCION_USUARIO;
      --CALCULA LA FECHA DE VENCIMIENTO
      MI_FECHAVENCIMIENTO := ADD_MONTHS(UN_FECHA,1);
      --INSERTA EL ENCABEZADO DEL COMPROBANTE A CREAR
      MI_CAMPOS := '  COMPANIA
                    , ANO
                    , TIPO
                    , NUMERO
                    , FECHA
                    , TERCERO
                    , SUCURSAL
                    , DESCRIPCION
                    , VLR_DOCUMENTO
                    , FECHA_VCN_DOC
                    , CREATED_BY
                    , DATE_CREATED';
      MI_VALORES := ' ''' || UN_COMPANIA    || '''
                     ,'   || UN_ANO         || '
                     ,''' || MI_TIPOACREAR  || '''
                     ,'   || MI_NUMEROACREAR|| '
                     ,       TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY HH24:MI:SS') || ''', ''DD/MM/YYYY HH24:MI:SS'')
                     ,''' || UN_TERCERO     || '''
                     ,''' || UN_SUCURSAL    || '''
                     ,''' || MI_DESCRIPCION || '''
                     ,'   || UN_VALORNETO   || '
                     ,       TO_DATE(''' || TO_CHAR(MI_FECHAVENCIMIENTO, 'DD/MM/YYYY HH24:MI:SS') || ''', ''DD/MM/YYYY HH24:MI:SS'')
                     ,''' || UN_USUARIO     || '''
                     ,       TO_DATE(''' || TO_CHAR(SYSDATE, 'DD/MM/YYYY HH24:MI:SS')  || ''', ''DD/MM/YYYY HH24:MI:SS'')';
      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'COMPROBANTE_PPTAL',
                                      UN_ACCION  => 'I',
                                      UN_CAMPOS  => MI_CAMPOS, 
                                      UN_VALORES => MI_VALORES
                                      );

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END;

          IF MI_RTA <= 0 THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END IF; 

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                         MI_MSGERROR(1).CLAVE := 'TIPOCPTE';
                         MI_MSGERROR(1).VALOR := MI_TIPOACREAR;
                         MI_MSGERROR(2).CLAVE := 'NROCPTE';
                         MI_MSGERROR(2).VALOR := MI_NUMEROACREAR;
                         MI_MSGERROR(3).CLAVE := 'DESCRIPCION';
                         MI_MSGERROR(3).VALOR := MI_DESCRIPCION;
                         PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                    UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_CREARCPTERED,
                                                    UN_TABLAERROR => 'COMPROBANTE_PPTAL',
                                                    UN_REEMPLAZOS => MI_MSGERROR
                                                    );
      END ;

      MI_CAMPOS := '  COMPANIA
                    , ANO
                    , TIPO_CPTE
                    , COMPROBANTE
                    , ANO_AFECT
                    , TIPO_CPTE_AFECT
                    , COMPROBANTE_AFECT
                    , CREATED_BY
                    , DATE_CREATED';
      MI_VALORES := ' ''' || UN_COMPANIA    || '''
                     ,'   || UN_ANO         || '
                     ,''' || MI_TIPOACREAR  || '''
                     ,'   || MI_NUMEROACREAR|| '
                     ,'   || UN_ANO         || '
                     ,''' || UN_TIPO        || '''
                     ,'   || UN_NUMERO      || '
                     ,''' || UN_USUARIO     || '''
                     , TO_DATE('''||TO_CHAR(SYSDATE, 'DD/MM/YYYY HH24:MI:SS')||''', ''DD/MM/YYYY HH24:MI:SS'')';
      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'COMPROBANTE_PPTALAFECTADOS',
                                      UN_ACCION   => 'I', 
                                      UN_CAMPOS   => MI_CAMPOS, 
                                      UN_VALORES  => MI_VALORES
                                      );

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END;

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                       MI_MSGERROR(1).CLAVE := 'TIPOCPTENUEVO';
                       MI_MSGERROR(1).VALOR := MI_TIPOACREAR;
                       MI_MSGERROR(2).CLAVE := 'NROCPTENUEVO';
                       MI_MSGERROR(2).VALOR := MI_NUMEROACREAR;
                       MI_MSGERROR(3).CLAVE := 'TIPOCPTE';
                       MI_MSGERROR(3).VALOR := UN_TIPO;
                       MI_MSGERROR(4).CLAVE := 'NROCPTE';
                       MI_MSGERROR(4).VALOR := UN_NUMERO;
                       PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                  UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_RELCPTEAFECTADOS,
                                                  UN_TABLAERROR => 'SALDO_AUX_PPTAL',
                                                  UN_REEMPLAZOS => MI_MSGERROR
                                                  );
        END ;

      --INSERTA EL DETALLE DE LA REDUCCIÓN DEL COMPROBANTE;
      MI_CAMPOS := '  COMPANIA 
                    , ANO
                    , TIPO_CPTE
                    , COMPROBANTE
                    , CONSECUTIVO
                    , CUENTA
                    , FECHA
                    , NATURALEZA
                    , DESCRIPCION
                    , VALOR_DEBITO
                    , VALOR_CREDITO
                    , ANO_AFECT
                    , TIPO_CPTE_AFECT
                    , CMPTE_AFECTADO
                    , CONSECUTIVOPPTO
                    , CREATED_BY
                    , DATE_CREATED
                    , TERCERO
                    , SUCURSAL
                    , CENTRO_COSTO
                    , AUXILIAR
                    ,FUENTE_RECURSO
                    ,REFERENCIA';
      MI_VALORES := ' ''' || UN_COMPANIA     || '''
                     ,'   || UN_ANO          || '
                     ,''' || MI_TIPOACREAR   || '''
                     ,'   || MI_NUMEROACREAR || '
                     ,    1
                     ,''' || UN_CUENTA       || '''
                     ,    TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY HH24:MI:SS') || ''',''DD/MM/YYYY HH24:MI:SS'') 
                     ,''' || UN_NATURALEZA   ||'''
                     ,''' || MI_DESCRIPCION  || '''
                     ,    0
                     ,'   || UN_VALORNETO    || '
                     ,'   || UN_ANO          || '
                     ,''' || UN_TIPO         || '''
                     ,'   || UN_NUMERO       || '
                     ,'   || UN_CONSECUTIVO  || '
                     ,''' || UN_USUARIO      || '''
                     ,    TO_DATE(''' || TO_CHAR(SYSDATE, 'DD/MM/YYYY HH24:MI:SS') || ''',''DD/MM/YYYY HH24:MI:SS'')
                     ,''' || UN_TERCERO      || '''
                     ,''' || UN_SUCURSAL     || '''
                     ,''' || UN_CENTRO_COSTO || '''
                     ,''' || UN_AUXILIAR     || '''
                     ,''' || UN_FUENTE_RECURSO    || '''
                     ,''' || UN_REFERENCIA    || '''';
      BEGIN
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'DETALLE_COMPROBANTE_PPTAL',
                                      UN_ACCION  => 'I',
                                      UN_CAMPOS  =>  MI_CAMPOS,
                                      UN_VALORES => MI_VALORES
                                      );

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                  MI_MSGERROR(1).CLAVE := 'TIPOCPTE';
                  MI_MSGERROR(1).VALOR := MI_TIPOACREAR;
                  MI_MSGERROR(2).CLAVE := 'NROCPTE';
                  MI_MSGERROR(2).VALOR := MI_NUMEROACREAR;
                  MI_MSGERROR(3).CLAVE := 'CONSECUTIVO';
                  MI_MSGERROR(3).VALOR := UN_CONSECUTIVO;
                  MI_MSGERROR(4).CLAVE := 'CUENTA';
                  MI_MSGERROR(4).VALOR := UN_CUENTA;
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_CREARDETREDUCCION,
                                             UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL',
                                             UN_REEMPLAZOS => MI_MSGERROR
                                             );
        END;
      IF (MI_COLUMNA <>'D') THEN
       IF MI_CLASE = 'DMR' THEN
          MI_CAMPOS := '  COMPANIA
                        , ANO
                        , TIPO_CPTE
                        , COMPROBANTE
                        , CONSECUTIVO
                        , CUENTA
                        , FECHA
                        , MOV_DEBITO
                        , MOV_CREDITO';
          MI_VALORES := ' ''' || UN_COMPANIA    || '''
                         ,'   || UN_ANO         || '
                         ,''' || MI_TIPOACREAR  || '''
                         ,'   || MI_NUMEROACREAR|| '
                         ,    1
                         ,''' || UN_CUENTA      || '''
                         ,    TO_DATE(''' || TO_CHAR(UN_FECHA, 'DD/MM/YYYY HH24:MI:SS') || ''',''DD/MM/YYYY HH24:MI:SS'')
                         ,    0
                         ,'   || UN_VALORNETO ;
          BEGIN
            BEGIN
              MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'PACPROGRAMADO',
                                          UN_ACCION   => 'I',
                                          UN_CAMPOS   => MI_CAMPOS,
                                          UN_VALORES  =>MI_VALORES
                                          );

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                      MI_MSGERROR(1).CLAVE := 'TIPOCPTE';
                      MI_MSGERROR(1).VALOR := MI_TIPOACREAR;
                      MI_MSGERROR(2).CLAVE := 'NROCPTE';
                      MI_MSGERROR(2).VALOR := MI_NUMEROACREAR;
                      MI_MSGERROR(3).CLAVE := 'CONSECUTIVO';
                      MI_MSGERROR(3).VALOR := UN_CONSECUTIVO;
                      MI_MSGERROR(4).CLAVE := 'CUENTA';
                      MI_MSGERROR(4).VALOR := UN_CUENTA;
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                 UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_CREARDETREDUCCION,
                                                 UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL',
                                                 UN_REEMPLAZOS => MI_MSGERROR
                                                 );
            END;
        END IF;
      END IF;
      IF MI_MENSAJESAFECTAR IS NOT NULL THEN
        MI_LIBERARCOMPROBANTE:= MI_MENSAJESAFECTAR;
      ELSE
        MI_LIBERARCOMPROBANTE := '-1';
      END IF;        
      RETURN MI_LIBERARCOMPROBANTE || '#'||MI_TIPOACREAR||'#'||MI_NUMEROACREAR;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERR_PPTO_LIBERARCOMPROBANTE
  ); 
END FC_LIBERARCOMPROBANTE;

FUNCTION FC_ACTUALIZARDETALLEPPTAL 
  /*
  NAME              : FC_ACTUALIZARDETALLEPPTAL (En Access, metodos cambiar en el módulo del formulario Comprobante_Pptal.
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : Diego Maldonado
  DATE MIGRADOR     : 17/08/2016
  TIME              : 08:40 AM
  SOURCE MODULE     : SysmanPR2016.03.02.accdb
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Permite realizar las actualizaciones pertinentes al detalle del Comprobante Presupuestal.
  @NAME:  modificarAuxiliaresEnDetallesPresupuestales
  @METHOD:  GET        
  */
  (

  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA, -- Parametro que recibe el numero de la compania
  UN_ANO                      IN PCK_SUBTIPOS.TI_ANIO,     -- Parametro que recibe el ano  del comprobante
  UN_TIPO                     IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL, -- Parametro que recibe el tipo de comprobante
  UN_COMPROBANTE              IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,-- Parametro que recibe el numero de comprobante
  UN_TERCEROA                 IN PCK_SUBTIPOS.TI_TERCERO,-- Parametro que recibe el tercero anterior
  UN_TERCERON                 IN PCK_SUBTIPOS.TI_TERCERO,-- Parametro que recibe el tercero nuevo
  UN_SUCURSALA                IN PCK_SUBTIPOS.TI_SUCURSAL,-- Parametro que recibe el numero de sucursal anterior
  UN_SUCURSALN                IN PCK_SUBTIPOS.TI_SUCURSAL,-- Parametro que recibe el numero de la sucursal nueva
  UN_DESCRIPCIONA             IN DETALLE_COMPROBANTE_PPTAL.DESCRIPCION%TYPE,-- Parametro que recibe la descripcion anterior
  UN_DESCRIPCIONN             IN DETALLE_COMPROBANTE_PPTAL.DESCRIPCION%TYPE,-- Parametro que recibe la descripcion nueva
  UN_NUMERODOCA               IN DETALLE_COMPROBANTE_PPTAL.NRO_DOCUMENTO%TYPE,-- Parametro que recibe el numero de comprobante anterior
  UN_NUMERODOCN               IN DETALLE_COMPROBANTE_PPTAL.NRO_DOCUMENTO%TYPE,-- Parametro que recibe el numero de comprobante nuevo
  UN_REFERENCIAA              IN PCK_SUBTIPOS.TI_REFERENCIA,-- Parametro que recibe el numero de referencia anterior
  UN_REFERENCIAN              IN PCK_SUBTIPOS.TI_REFERENCIA,-- Parametro que recibe el numero de referencia nuevo
  UN_AUXILIARA                IN PCK_SUBTIPOS.TI_AUXILIAR,-- Parametro que recibe el auxiliar anterior 
  UN_AUXILIARN                IN PCK_SUBTIPOS.TI_AUXILIAR-- Parametro que recibe el auxiliar nuevo

  )RETURN NUMBER
  AS
    -- Variable que almacenara el indicador de actualizar detalle
    MI_ACTUALIZARDETALLEPPTAL PCK_SUBTIPOS.TI_LOGICO := 0;
    MI_ERROR_FUN              PCK_SUBTIPOS.TI_ERROR_FUN:= GL_ERROR_NUM + 1;
    MI_STRSQL                 PCK_SUBTIPOS.TI_STRSQL;      -- Variable que almacenara las consultas 
    MI_CONTEOTER              PCK_SUBTIPOS.TI_ENTERO_LARGO;-- Variable que almacenara el numero de terceros retornado al ejecutar la consulta
    MI_CONTEOTERDIF           PCK_SUBTIPOS.TI_ENTERO_LARGO;-- Variable que almacenara el numero de terceros 
    MI_PARAMETRO              PARAMETRO.VALOR%TYPE;        -- Variable que almacenara el valor de un parametro
    MI_TERCERODISPONI         PARAMETRO.VALOR%TYPE;
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;      -- Variable que almacenara la cadena de campos que se van a actualizar
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;   -- Variable que almacenara la condicion a usar en la actualizacion
    MI_MODULO PCK_SUBTIPOS.TI_MODULO;
    MI_RTA                    VARCHAR2(200 CHAR);
    BEGIN
    MI_MODULO := PCK_DATOS.MODULOCONTABILIDAD;
    /*13/04/2018 JP Se comenta pues se llega a la conclusión que el tercero del header no debe interferir en los terceros de los detalles
     caso de los comprobantes COM o NOM de nomina donde quedan a nombre de la entidad(por ejemplo) pero cada detalle
    lleva el tercero al cual se le va a girar el dinero
        IF (UN_TERCERON <> UN_TERCEROA) OR (UN_SUCURSALN <> UN_SUCURSALA) THEN
            SELECT COUNT(COMPANIA) CONTEO
            INTO MI_CONTEOTER
            FROM DETALLE_COMPROBANTE_PPTAL
            WHERE COMPANIA    = UN_COMPANIA  
              AND ANO         = UN_ANO   
              AND TIPO_CPTE   = UN_TIPO   
              AND COMPROBANTE = UN_COMPROBANTE 
              AND TERCERO     = UN_TERCEROA 
              AND SUCURSAL    = UN_SUCURSALA 
           ORDER BY COMPANIA, 
                    ANO,        
                    TIPO_CPTE,
                    COMPROBANTE,
                    CONSECUTIVO;

            SELECT COUNT(COMPANIA) CONTEO
            INTO MI_CONTEOTERDIF
            FROM DETALLE_COMPROBANTE_PPTAL
            WHERE COMPANIA    = UN_COMPANIA
              AND ANO         = UN_ANO     
              AND TIPO_CPTE   = UN_TIPO  
              AND COMPROBANTE = UN_COMPROBANTE
              AND TERCERO     NOT IN (UN_TERCEROA)
              AND SUCURSAL    NOT IN (UN_SUCURSALA)
             ORDER BY COMPANIA,
                   ANO,
                   TIPO_CPTE,
                   COMPROBANTE,
                   CONSECUTIVO;

            IF MI_CONTEOTER > 0 THEN
              MI_CAMPOS:='TERCERO  =  '''||UN_TERCERON ||''', SUCURSAL =  '''||UN_SUCURSALN||'''';
              MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA    || '''  
                               AND ANO         = '   || UN_ANO         || '
                               AND TIPO_CPTE   = ''' || UN_TIPO        || '''  
                               AND COMPROBANTE = '   || UN_COMPROBANTE || '
                               AND TERCERO     = ''' || UN_TERCEROA    || '''  
                               AND SUCURSAL    = ''' || UN_SUCURSALA   || '''';
              BEGIN
                GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'DETALLE_COMPROBANTE_PPTAL', 
                                            UN_ACCION   => 'M',
                                            UN_CAMPOS   => MI_CAMPOS, 
                                            UN_CONDICION=> MI_CONDICION
                                            );
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
              END;
            END IF;

            IF MI_CONTEOTERDIF <> 0 THEN
                --Existen detalles con terceros diferentes al del comprobante. Debe modificarlos manualmente.
                MI_ACTUALIZARDETALLEPPTAL := '1';
            END IF;
        END IF;
       */
		MI_TERCERODISPONI := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA,
											  UN_NOMBRE   =>'MANEJA TERCERO EN SOLICITUD DE DISPONIBILIDAD FUNCIONAMIENTO',
											  UN_MODULO   =>MI_MODULO,
											  UN_FECHA_PAR=>SYSDATE );  
			IF MI_TERCERODISPONI = 'SI' THEN
				  MI_CAMPOS:='TERCERO  =  '''||UN_TERCERON ||''', SUCURSAL =  '''||UN_SUCURSALN||'''';
				  MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA    || '''
								   AND ANO         = '   || UN_ANO         || '
								   AND TIPO_CPTE   = ''' || UN_TIPO        || '''
								   AND COMPROBANTE = '   || UN_COMPROBANTE || '
								   AND TERCERO = ''' || '999999999999999999' || '''
								   AND SUCURSAL    = ''' || UN_SUCURSALA   || '''';
				  BEGIN
					GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'DETALLE_COMPROBANTE_PPTAL',
												UN_ACCION   => 'M',
												UN_CAMPOS   => MI_CAMPOS,
												UN_CONDICION=> MI_CONDICION
												);
				  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
					RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
				  END;
		END IF;
			
        IF UN_NUMERODOCN <> NVL(UN_NUMERODOCA,'1') THEN
            MI_CAMPOS:='NRO_DOCUMENTO = '''||UN_NUMERODOCN||'''';
            MI_CONDICION:='COMPANIA          = ''' || UN_COMPANIA    || ''' 
                           AND ANO           = '   || UN_ANO         || ' 
                           AND TIPO_CPTE     = ''' || UN_TIPO        || '''  
                           AND COMPROBANTE   = '   || UN_COMPROBANTE || ''; 
            BEGIN                          
            GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    =>'DETALLE_COMPROBANTE_PPTAL', 
                                        UN_ACCION   =>'M', 
                                        UN_CAMPOS   =>MI_CAMPOS,
                                        UN_CONDICION=>MI_CONDICION
                                        );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                             END ; 
        END IF;

        IF UN_DESCRIPCIONN <> NVL(UN_DESCRIPCIONA,'1') THEN
            MI_PARAMETRO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA, 
                                                  UN_NOMBRE   =>'PERMITE ACTUALIZAR DESCRIPCIÓN DE DETALLE',
                                                  UN_MODULO   =>MI_MODULO,
                                                  UN_FECHA_PAR=>SYSDATE
                                                  );
            MI_PARAMETRO := NVL(MI_PARAMETRO, 'NO');        
            IF MI_PARAMETRO = 'SI' THEN
               MI_CAMPOS:='DESCRIPCION = '''||UN_DESCRIPCIONN||'''';
               MI_CONDICION:='COMPANIA        = ''' || UN_COMPANIA || ''' 
                              AND ANO         = '   || UN_ANO      || ' 
                              AND TIPO_CPTE   = ''' || UN_TIPO     || '''
                              AND COMPROBANTE = '   || UN_COMPROBANTE ; 

                BEGIN                            
                GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     =>'DETALLE_COMPROBANTE_PPTAL',
                                            UN_ACCION    =>'M', 
                                            UN_CAMPOS    =>MI_CAMPOS,
                                            UN_CONDICION =>MI_CONDICION
                                            );

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                             END ;                              
            ELSE   
              MI_CAMPOS:='DESCRIPCION = '''||UN_DESCRIPCIONN||''''; 
              MI_CONDICION:='COMPANIA        = ''' || UN_COMPANIA     || '''
                             AND ANO         = '   || UN_ANO          || ' 
                             AND TIPO_CPTE   = ''' || UN_TIPO         || '''  
                             AND COMPROBANTE = '   || UN_COMPROBANTE  || ' 
                             AND DESCRIPCION = ''' || UN_DESCRIPCIONA || ''''; 

                BEGIN             
                GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     =>'DETALLE_COMPROBANTE_PPTAL', 
                                            UN_ACCION    =>'M', 
                                            UN_CAMPOS    =>MI_CAMPOS, 
                                            UN_CONDICION =>MI_CONDICION
                                            );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                             END ;                             
            END IF;
        END IF;

        IF UN_REFERENCIAN <> UN_REFERENCIAA THEN
           MI_CAMPOS := ' REFERENCIA = '''||UN_REFERENCIAN||''''; 
           MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA    || ''' 
                            AND ANO         = '   || UN_ANO         || ' 
                            AND TIPO_CPTE   = ''' || UN_TIPO        || ''' 
                            AND COMPROBANTE = '   || UN_COMPROBANTE || '
                            AND REFERENCIA  = ''' || UN_REFERENCIAA || ''''; 
            BEGIN                             
            GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     =>'DETALLE_COMPROBANTE_PPTAL', 
                                        UN_ACCION    =>'M',
                                        UN_CAMPOS    =>MI_CAMPOS, 
                                        UN_CONDICION =>MI_CONDICION
                                        );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                             END ;                             
        END IF;

        IF UN_AUXILIARN <> UN_AUXILIARA THEN
           MI_CAMPOS := ' AUXILIAR = '''||UN_AUXILIARN||''''; 
           MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA || ''' 
                            AND ANO         = '   || UN_ANO      || ' 
                            AND TIPO_CPTE   = ''' || UN_TIPO     || '''  
                            AND COMPROBANTE = '   || UN_COMPROBANTE ; 

            BEGIN                
            GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     =>'DETALLE_COMPROBANTE_PPTAL', 
                                        UN_ACCION    =>'M',
                                        UN_CAMPOS    =>MI_CAMPOS, 
                                        UN_CONDICION =>MI_CONDICION
                                        );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                             END ;                             
        END IF; 

        IF MI_ACTUALIZARDETALLEPPTAL <> 1 THEN
            MI_ACTUALIZARDETALLEPPTAL := -1;
        END IF;

        RETURN MI_ACTUALIZARDETALLEPPTAL;

     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERR_PPTO_ACTUALIZARDETEPPTAL
     );    

END FC_ACTUALIZARDETALLEPPTAL;   


PROCEDURE PR_CUADRESALDOSPTO
  /*
  NAME              : 
  AUTHORS           : 
  AUTHOR MIGRACION  : 
  DATE MIGRADOR     : 
  TIME              : 
  SOURCE MODULE     : 
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : 
  @NAME:  cuadrarSaldosPpto
  @METHOD:  POST      
  */
  (
  -- Parametro que recibe el numero de la compania
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA, 
  -- Parametor que recibe el numero de mes inicial
  UN_MESINICIAL               IN PCK_SUBTIPOS.TI_MES, 
  -- Parametro que recibe el numero de mes final 
  UN_MESFINAL                 IN PCK_SUBTIPOS.TI_MES,
  -- Parametro que recibe el numero de ano 
  UN_ANIO                     IN PCK_SUBTIPOS.TI_ANIO
  )
  AS
    -- Variable que almacenara la cadena de campos a actualizar 
    MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacenara los valores usados al hacer la actualizacion
    MI_VALORES                PCK_SUBTIPOS.TI_CAMPOS;
    -- Variable que almacenara la condicion a usar en la actualizacion
    MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION;
    -- Variable que almacenara la clase de movimiento
    MI_STRCLASEMOV            VARCHAR(3,CHAR);
    -- Variable que almacenara el valor de la diferencia
    MI_DIFERENCIA             PCK_SUBTIPOS.TI_DOBLE;
    -- Variable que almacenara el valor a retornar en la funcion 
    MI_RTA                    PCK_SUBTIPOS.TI_RTA_ACME;
    -- Variable que almacenara el nombre de la tabla 
    MI_TABLA                  PCK_SUBTIPOS.TI_TABLA;
    -- Variable que almacenara los campos usados en la insercion del merge
    MI_MERGEUSING             PCK_SUBTIPOS.TI_MERGEUSING; 
    -- Variable que almacenara los campos de enlace en el merge
    MI_MERGEENLACE            PCK_SUBTIPOS.TI_MERGEENLACE; 
    -- Variable que almacenara campos usados en el merge 
    MI_MERGEEXISTE            PCK_SUBTIPOS.TI_MERGEEXISTE;  
    --'Función    : Reversa todos los movimientos pptales hechos y los vuelve a afectar. Revisa saldos de cuentas pptales.
    --'Creador    : Pilar Alexandra Moreno
    --'Modificador: Pilar Alexandra Moreno
    --'Parámetros : Mes Incial para el proceso, Mes Final para el proceso, Año para el proceso
    MI_FR varchar2(22) := '99999999999999999999';
  BEGIN 
  /*SE DEBE REVISAR AL FINAL LAS CUENTAS QUE QUEDN CON VALOR EN CERO PARA ELIMINARLAS --> SUMATORIA DE TODAS LAS COLUMNAS IGUAL A CERO*/

    MI_CAMPOS := ' ADICION                  = 0
                 , REDUCCION                = 0
                 , PAC_PROGRAMADO           = 0
                 , DISPONIBILIDAD           = 0
                 , DISPONIBILIDADADD        = 0
                 , DISPONIBILIDADDMD        = 0 
                 , REG_NO_CONTRACT          = 0
                 , REG_CONTRACT             = 0 
                 , REG_REVERSION            = 0 
                 , MODIF_PAC_DEBITO         = 0 
                 , MODIF_PAC_CREDITO        = 0 
                 , MODIF_REG_CONT           = 0
                 , MODIF_REG_NOCONT         = 0 
                 , MODIF_REG_CONTADR        = 0
                 , MODIF_REG_NOCONTADR      = 0
                 , MODIF_REG_CONTDMR        = 0
                 , MODIF_REG_NOCONTDMR      = 0
                 , REINTEGRO                = 0
                 , VIGENCIAANTERIOR         = 0
                 , VIGENCIAFUTURA           = 0
                 , TRASLADO_DEBITO          = 0
                 , TRASLADO_CREDITO         = 0
                 , APLAZAM_DEBITO           = 0
                 , APLAZAM_CREDITO          = 0 
                 , EJE_CNT_DEBITO           = 0
                 , EJE_CNT_CREDITO          = 0
                 , EJE_PPT_CREDITO          = 0
                 , EJE_PPT_DEBITO           = 0
                 , EJE_PPT_DEBITOAEG        = 0
                 , EJE_PPT_DEBITODEG        = 0
                 , REGISTRO_OBLIGACION      = 0
                 , MODIF_REGISTRO_OBLIGACION= 0
                 , MODIF_REGISTRO_OBLIGACIONARO = 0
                 , MODIF_REGISTRO_OBLIGACIONDRO = 0
                 , INGRESOS_PAPELES         = 0
                 , INGRESOS_EFECTIVO        = 0
                 , MODIF_INGRESOS           = 0
                 , INGRESOS_CAUSADOS        = 0
                 , MODIF_INGRESOS_CAUSADOS  = 0
                 , MODIF_INGRESOS_PAPELES   = 0
                 , MODIF_INGRESOS_EFECTIVO  = 0 
                 , NETOEGRESO               = 0
                 , PACTESORERIA             = 0
                 , PAC_EJECUTADO            = 0
                 , PAC_COMPROMETIDO         = 0
                 , RECONOCIMIENTOS          = 0 '; 
    MI_CONDICION := 'COMPANIA =  ''' || UN_COMPANIA || '''
                     AND ANO  =  '   || UN_ANIO     || '
                     AND MES  BETWEEN ' || CASE WHEN UN_MESINICIAL = 1 THEN 0  ELSE  UN_MESINICIAL  END || ' AND ' || UN_MESFINAL  ;
  BEGIN
    BEGIN
      --<TICKET: 7723626 AUTOR:CP FECHA:22/12/2022  Se agrega creación de saldos pptal al proceso ya que no está creando el registro de la cuenta cuando esta ya existe >
      MI_RTA := PCK_PRESUPUESTO.FC_CREARSALDOSPPTALES(
                               UN_COMPANIA      => UN_COMPANIA,
                               UN_ANIO          => UN_ANIO
                              );
      --<TICKET/>
      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA => 'SALDO_PLAN_PPTAL',
                                  UN_ACCION => 'M',
                                  UN_CAMPOS => MI_CAMPOS,
                                  UN_CONDICION => MI_CONDICION
                                  );

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
           RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_LIMPIARSALDOSPPTAL
                                         );
    END;
  BEGIN
    BEGIN
      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_AUX_PPTAL',
                                  UN_ACCION    => 'M',
                                  UN_CAMPOS    => MI_CAMPOS,
                                  UN_CONDICION => MI_CONDICION
                                  );

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_LIMPIARSALDOSAUX
                                         );
    END; 

  <<ACTUALIZADIFERENCIA>>
  FOR RSDETALLE IN (   
            SELECT  DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE
                 ,  DETALLE_COMPROBANTE_PPTAL.CUENTA
                 ,  SUM(DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO) SUMADEVALOR_DEBITO
                 ,  SUM(DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO) SUMADEVALOR_CREDITO
                 ,  DETALLE_COMPROBANTE_PPTAL.MES
                 ,  DETALLE_COMPROBANTE_PPTAL.CONTRACTUAL
                 ,  DETALLE_COMPROBANTE_PPTAL.PAPELES
                 ,  PLAN_PRESUPUESTAL.NATURALEZA
                 ,  TIPO_COMPROBPP.CLASE 
                 ,  DETALLE_COMPROBANTE_PPTAL.TERCERO
                 ,  DETALLE_COMPROBANTE_PPTAL.SUCURSAL
                 ,  DETALLE_COMPROBANTE_PPTAL.AUXILIAR
                 ,  DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO
                 ,  DETALLE_COMPROBANTE_PPTAL.REFERENCIA
                 ,  DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO
              FROM  DETALLE_COMPROBANTE_PPTAL  
             INNER JOIN PLAN_PRESUPUESTAL 
                     ON DETALLE_COMPROBANTE_PPTAL.COMPANIA  = PLAN_PRESUPUESTAL.COMPANIA 
                    AND DETALLE_COMPROBANTE_PPTAL.ANO       = PLAN_PRESUPUESTAL.ANO
                    AND DETALLE_COMPROBANTE_PPTAL.CUENTA    = PLAN_PRESUPUESTAL.CODIGO
             INNER JOIN TIPO_COMPROBPP  
                     ON  DETALLE_COMPROBANTE_PPTAL.COMPANIA = TIPO_COMPROBPP.COMPANIA 
                    AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE = TIPO_COMPROBPP.CODIGO
             WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA = UN_COMPANIA 
               AND DETALLE_COMPROBANTE_PPTAL.ANO      = UN_ANIO 
               AND DETALLE_COMPROBANTE_PPTAL.FECHA BETWEEN TO_DATE('01/'||CASE WHEN UN_MESINICIAL = 0
                                                                               THEN '01'
                                                                               ELSE LPAD(UN_MESINICIAL,2)
                                                                               END||'/'||UN_ANIO, 'DD/MM/YYYY') 
               AND LAST_DAY(TO_DATE('01/'||LPAD(UN_MESFINAL,2)||'/'||UN_ANIO, 'DD/MM/YYYY')) 
             GROUP BY  DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE
                    ,  TIPO_COMPROBPP.CLASE
                    ,  DETALLE_COMPROBANTE_PPTAL.CUENTA
                    ,  DETALLE_COMPROBANTE_PPTAL.MES
                    ,  DETALLE_COMPROBANTE_PPTAL.CONTRACTUAL
                    ,  DETALLE_COMPROBANTE_PPTAL.PAPELES
                    ,  PLAN_PRESUPUESTAL.NATURALEZA
                    ,  TIPO_COMPROBPP.CLASE 
                    ,  DETALLE_COMPROBANTE_PPTAL.TERCERO
                    ,  DETALLE_COMPROBANTE_PPTAL.SUCURSAL
                    ,  DETALLE_COMPROBANTE_PPTAL.AUXILIAR
                    ,  DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO
                    ,  DETALLE_COMPROBANTE_PPTAL.REFERENCIA
                    ,  DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO
               ) LOOP 
    MI_DIFERENCIA:= CASE WHEN RSDETALLE.NATURALEZA = 'D'
                         THEN NVL(RSDETALLE.SUMADEVALOR_DEBITO, 0) - NVL(RSDETALLE.SUMADEVALOR_CREDITO, 0)
                         ELSE NVL(RSDETALLE.SUMADEVALOR_CREDITO, 0) - NVL(RSDETALLE.SUMADEVALOR_DEBITO, 0)
                         END;
   --STRCLASE = RSDETALLE!CLASE
   --XX = ACTPTO0(DBCUADRE, STRCLASE, STRCOMPANIA, ANO, RSDETALLE.FIELDS(5), RSDETALLE!CUENTA, 0, 0, 0, RSDETALLE.FIELDS(3), RSDETALLE.FIELDS(4), DIFERENCIA, IIF(NZ(RSDETALLE!CONTRACTUAL, 0), "C", "N"))
   PCK_PRESUPUESTO1.PR_ACTPPTO0AUX (UN_CLASE         => RSDETALLE.CLASE,
                                    UN_COMPANIA      => UN_COMPANIA,
                                    UN_ANIO          => UN_ANIO,
                                    UN_CODIGO        => RSDETALLE.CUENTA,
                                    UN_TERCERO       => RSDETALLE.TERCERO,
                                    UN_SUCURSAL      =>  RSDETALLE.SUCURSAL,
                                    UN_AUXILIAR      => RSDETALLE.AUXILIAR,
                                    UN_CENTRO        => RSDETALLE.CENTRO_COSTO,
                                    UN_REFERENCIA    => RSDETALLE.REFERENCIA,
                                    UN_FUENTERECURSO => RSDETALLE.FUENTE_RECURSO,
                                    UN_MES           => RSDETALLE.MES,
                                    UN_DEBITO        => RSDETALLE.SUMADEVALOR_DEBITO, 
                                    UN_CREDITO       => RSDETALLE.SUMADEVALOR_CREDITO,
                                    UN_DEBITO_ANT    => 0, 
                                    UN_CREDITO_ANT   => 0, 
                                    UN_NATURALEZA    => RSDETALLE.NATURALEZA, 
                                    UN_DIFERENCIA    => MI_DIFERENCIA, 
                                    UN_DIFERENCIAANT => 0, 
                                    UN_TIPO          => CASE WHEN RSDETALLE.CONTRACTUAL <> 0 THEN 'C' ELSE 'N' END,
                                    UN_TIPOINGRESO   => NULL
                                    );
   IF RSDETALLE.CLASE = 'ING' THEN
     --XX = ActPto0(dbCuadre, "OIN", strcompania, Ano, rsDetalle.Fields(5), rsDetalle!Cuenta, 0, 0, 0, rsDetalle.Fields(3), rsDetalle.Fields(4), Diferencia, IIf(Nz(rsDetalle!Contractual, 0), "C", "N"), IIf(Nz(rsDetalle!Papeles, 0), "P", "E"))
     PCK_PRESUPUESTO1.PR_ACTPPTO0AUX (UN_CLASE         => 'OIN', 
                                      UN_COMPANIA      => UN_COMPANIA,
                                      UN_ANIO          => UN_ANIO, 
                                      UN_CODIGO        => RSDETALLE.CUENTA, 
                                      UN_TERCERO       => RSDETALLE.TERCERO, 
                                      UN_SUCURSAL      =>  RSDETALLE.SUCURSAL, 
                                      UN_AUXILIAR      => RSDETALLE.AUXILIAR, 
                                      UN_CENTRO        => RSDETALLE.CENTRO_COSTO, 
                                      UN_REFERENCIA    => RSDETALLE.REFERENCIA,
                                      UN_FUENTERECURSO => RSDETALLE.FUENTE_RECURSO,
                                      UN_MES           => RSDETALLE.MES, 
                                      UN_DEBITO        => RSDETALLE.SUMADEVALOR_DEBITO,
                                      UN_CREDITO       => RSDETALLE.SUMADEVALOR_CREDITO, 
                                      UN_DEBITO_ANT    => 0,
                                      UN_CREDITO_ANT   => 0, 
                                      UN_NATURALEZA    => RSDETALLE.NATURALEZA, 
                                      UN_DIFERENCIA    => MI_DIFERENCIA,
                                      UN_DIFERENCIAANT => 0, 
                                      UN_TIPO          => CASE WHEN RSDETALLE.CONTRACTUAL <> 0 THEN 'C' ELSE 'N' END,
                                      UN_TIPOINGRESO   => CASE WHEN RSDETALLE.PAPELES <> 0 THEN 'P' ELSE 'E' END
                                      );
   ELSIF RSDETALLE.CLASE = 'AIN' OR RSDETALLE.CLASE = 'DIN' THEN
        --     XX = ACTPTO0(DBCUADRE, "MOI", STRCOMPANIA, ANO, RSDETALLE.FIELDS(5), RSDETALLE!CUENTA, 0, 0, 0, RSDETALLE.FIELDS(3), RSDETALLE.FIELDS(4), DIFERENCIA, IIF(NZ(RSDETALLE!CONTRACTUAL, 0), "C", "N"), IIF(NZ(RSDETALLE!PAPELES, 0), "P", "E"))
        PCK_PRESUPUESTO1.PR_ACTPPTO0AUX (UN_CLASE         => 'MOI', 
                                         UN_COMPANIA      => UN_COMPANIA, 
                                         UN_ANIO          => UN_ANIO, 
                                         UN_CODIGO        => RSDETALLE.CUENTA, 
                                         UN_TERCERO       => RSDETALLE.TERCERO, 
                                         UN_SUCURSAL      => RSDETALLE.SUCURSAL, 
                                         UN_AUXILIAR      => RSDETALLE.AUXILIAR,
                                         UN_CENTRO        => RSDETALLE.CENTRO_COSTO, 
                                         UN_REFERENCIA    => RSDETALLE.REFERENCIA, 
                                         UN_FUENTERECURSO => RSDETALLE.FUENTE_RECURSO, 
                                         UN_MES           => RSDETALLE.MES, 
                                         UN_DEBITO        => RSDETALLE.SUMADEVALOR_DEBITO,
                                         UN_CREDITO       => RSDETALLE.SUMADEVALOR_CREDITO,
                                         UN_DEBITO_ANT    => 0, 
                                         UN_CREDITO_ANT   => 0, 
                                         UN_NATURALEZA    => RSDETALLE.NATURALEZA, 
                                         UN_DIFERENCIA    => MI_DIFERENCIA,
                                         UN_DIFERENCIAANT => 0, 
                                         UN_TIPO          => CASE WHEN RSDETALLE.CONTRACTUAL <> 0 THEN 'C' ELSE 'N' END,
                                         UN_TIPOINGRESO   => CASE WHEN RSDETALLE.PAPELES <> 0 THEN 'P' ELSE 'E' END
                                         );
    END IF;
  END LOOP ACTUALIZADIFERENCIA;

  <<ACTUALIZAVIGENCIAFUTURA>>
   FOR RSDETALLE IN (
             SELECT  PACPROGRAMADO.COMPANIA 
                  ,  PACPROGRAMADO.ANO 
                  ,  PACPROGRAMADO.TIPO_CPTE
                  ,  PACPROGRAMADO.COMPROBANTE
                  ,  PACPROGRAMADO.CUENTA
                  ,  EXTRACT(MONTH FROM PACPROGRAMADO.FECHA) MES
                  ,  SUM(PACPROGRAMADO.MOV_DEBITO) SUMADEMOV_DEBITO
                  ,  SUM(PACPROGRAMADO.MOV_CREDITO) SUMADEMOV_CREDITO
                  ,  MIN(DETALLE_COMPROBANTE_PPTAL.CONTRACTUAL) CONTRACTUAL
                  ,  EXTRACT(YEAR FROM PACPROGRAMADO.FECHA) ANOPAC 
                  ,  DETALLE_COMPROBANTE_PPTAL.TERCERO
                  ,  DETALLE_COMPROBANTE_PPTAL.SUCURSAL
                  ,  DETALLE_COMPROBANTE_PPTAL.AUXILIAR
                  ,  DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO
                  ,  DETALLE_COMPROBANTE_PPTAL.REFERENCIA
                  ,  DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO
                  ,  PLAN_PRESUPUESTAL.NATURALEZA
               FROM  PACPROGRAMADO  
              INNER JOIN DETALLE_COMPROBANTE_PPTAL 
                      ON PACPROGRAMADO.COMPANIA    = DETALLE_COMPROBANTE_PPTAL.COMPANIA 
                     AND PACPROGRAMADO.ANO         = DETALLE_COMPROBANTE_PPTAL.ANO 
                     AND PACPROGRAMADO.TIPO_CPTE   = DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE  
                     AND PACPROGRAMADO.COMPROBANTE = DETALLE_COMPROBANTE_PPTAL.COMPROBANTE  
                     AND PACPROGRAMADO.CONSECUTIVO = DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO 
              INNER JOIN PLAN_PRESUPUESTAL
                      ON PACPROGRAMADO.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                     AND PACPROGRAMADO.ANO      = PLAN_PRESUPUESTAL.ANO
                     AND PACPROGRAMADO.CUENTA   = PLAN_PRESUPUESTAL.CODIGO
              WHERE  PACPROGRAMADO.COMPANIA      = UN_COMPANIA 
                AND  PACPROGRAMADO.ANO         = UN_ANIO 
                AND  EXTRACT (MONTH FROM PACPROGRAMADO.FECHA) BETWEEN 1 AND 12 
              GROUP BY  PACPROGRAMADO.COMPANIA 
                     ,  PACPROGRAMADO.ANO 
                     ,  PACPROGRAMADO.TIPO_CPTE 
                     ,  PACPROGRAMADO.COMPROBANTE
                     ,  PACPROGRAMADO.CUENTA
                     ,  EXTRACT(MONTH FROM PACPROGRAMADO.FECHA) 
                     ,  EXTRACT(YEAR FROM PACPROGRAMADO.FECHA)
                     ,  DETALLE_COMPROBANTE_PPTAL.TERCERO
                     ,  DETALLE_COMPROBANTE_PPTAL.SUCURSAL
                     ,  DETALLE_COMPROBANTE_PPTAL.AUXILIAR
                     ,  DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO
                     ,  DETALLE_COMPROBANTE_PPTAL.REFERENCIA
                     ,  DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO
                     ,  PLAN_PRESUPUESTAL.NATURALEZA
          ) LOOP
   IF RSDETALLE.ANOPAC > RSDETALLE.ANO THEN
    MI_STRCLASEMOV:= 'VIF';
   ELSE 
    MI_STRCLASEMOV:= 'PAP';
   END IF;
   MI_DIFERENCIA := CASE WHEN RSDETALLE.NATURALEZA = 'D'
                         THEN RSDETALLE.SUMADEMOV_DEBITO - RSDETALLE.SUMADEMOV_CREDITO
                         ELSE RSDETALLE.SUMADEMOV_CREDITO - RSDETALLE.SUMADEMOV_DEBITO
                         END;
  PCK_PRESUPUESTO1.PR_ACTPPTO0AUX (UN_CLASE         => MI_STRCLASEMOV, 
                                   UN_COMPANIA      => UN_COMPANIA,
                                   UN_ANIO          => UN_ANIO, 
                                   UN_CODIGO        => RSDETALLE.CUENTA, 
                                   UN_TERCERO       => RSDETALLE.TERCERO, 
                                   UN_SUCURSAL      =>  RSDETALLE.SUCURSAL,
                                   UN_AUXILIAR      => RSDETALLE.AUXILIAR,
                                   UN_CENTRO        => RSDETALLE.CENTRO_COSTO,
                                   UN_REFERENCIA    => RSDETALLE.REFERENCIA,
                                   UN_FUENTERECURSO => RSDETALLE.FUENTE_RECURSO,
                                   UN_MES           => RSDETALLE.MES,
                                   UN_DEBITO        => RSDETALLE.SUMADEMOV_DEBITO,
                                   UN_CREDITO       => RSDETALLE.SUMADEMOV_CREDITO,
                                   UN_DEBITO_ANT    => 0, 
                                   UN_CREDITO_ANT   => 0,
                                   UN_NATURALEZA    => RSDETALLE.NATURALEZA, 
                                   UN_DIFERENCIA    => MI_DIFERENCIA, 
                                   UN_DIFERENCIAANT => 0, 
                                   UN_TIPO          => CASE WHEN RSDETALLE.CONTRACTUAL <> 0 THEN 'C' ELSE 'N' END, 
                                   UN_TIPOINGRESO   => NULL
                                   );
         --XX = ActPto0(dbCuadre, strClaseMov, strcompania, Ano, rsDetalle!Mes, rsDetalle!Cuenta, 0, 0, 0, rsDetalle!SumaDeMov_Debito, rsDetalle!SumaDeMov_Credito, Diferencia, IIf(Nz(rsDetalle!Contractual, 0), "C", "N"))

  END LOOP ACTUALIZAVIGENCIAFUTURA;
  -- '* 12/08/2005 Mayoriza pac ejecutado del mes 0
  -- ' De aquí para adelante empieza mayorizacion de PAC ejecutado del mes 0
  MI_TABLA := 'SALDO_PLAN_PPTAL';
  MI_MERGEUSING := 'SELECT  SALDO_PLAN_PPTAL.COMPANIA,
                            SALDO_PLAN_PPTAL.ANO,
                            SALDO_PLAN_PPTAL.CODIGO 
                      FROM  PLAN_PRESUPUESTAL      
                     INNER JOIN SALDO_PLAN_PPTAL 
                             ON     PLAN_PRESUPUESTAL.COMPANIA = SALDO_PLAN_PPTAL.COMPANIA 
                            AND    PLAN_PRESUPUESTAL.ANO       = SALDO_PLAN_PPTAL.ANO 
                            AND    PLAN_PRESUPUESTAL.CODIGO    = SALDO_PLAN_PPTAL.CODIGO 
                     WHERE  PLAN_PRESUPUESTAL.COMPANIA = ''' || UN_COMPANIA || '''
                       AND  PLAN_PRESUPUESTAL.ANO      = '   || UN_ANIO     || '
                       AND  MES                        = 0  
                       AND  PAC_EJECUTADO              NOT IN (0) 
                       AND  PLAN_PRESUPUESTAL.MAN_PAC  IN (0)';
  MI_MERGEENLACE := 'TABLA.COMPANIA   = VISTA.COMPANIA 
                     AND TABLA.ANO    = VISTA.ANO
                     AND TABLA.CODIGO = VISTA.CODIGO ';
  MI_MERGEEXISTE := 'UPDATE SET TABLA.PAC_EJECUTADO = 0 ';
  BEGIN
    BEGIN
      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA        => MI_TABLA,
                                  UN_ACCION       => 'MM',
                                  UN_MERGEUSING   => MI_MERGEUSING,
                                  UN_MERGEENLACE  => MI_MERGEENLACE,
                                  UN_MERGEEXISTE  => MI_MERGEEXISTE
                                  );
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                   PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                              UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTSALDOPLANPPTAL
                                              );
    END;
     /*         ELIMINAR DEPUES DE REVISAR PRUEBA     
  MERGE INTO SALDO_PLAN_PPTAL TABLA
  USING (
         SELECT SALDO_PLAN_PPTAL.COMPANIA,
                SALDO_PLAN_PPTAL.ANO,
                SALDO_PLAN_PPTAL.CODIGO 
         FROM PLAN_PRESUPUESTAL      
         INNER JOIN SALDO_PLAN_PPTAL 
          ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_PLAN_PPTAL.COMPANIA 
          AND PLAN_PRESUPUESTAL.ANO      = SALDO_PLAN_PPTAL.ANO 
          AND PLAN_PRESUPUESTAL.CODIGO   = SALDO_PLAN_PPTAL.CODIGO 
        WHERE PLAN_PRESUPUESTAL.COMPANIA = UN_COMPANIA
          AND PLAN_PRESUPUESTAL.ANO      = UN_ANIO 
          AND MES                        = 0  
          AND PAC_EJECUTADO              NOT IN (0) 
          AND PLAN_PRESUPUESTAL.MAN_PAC  IN (0)

         ) VISTA
  ON (     TABLA.COMPANIA = VISTA.COMPANIA 
       AND TABLA.ANO      = VISTA.ANO
       AND TABLA.CODIGO   = VISTA.CODIGO 
      )
  WHEN MATCHED THEN 
   UPDATE SET TABLA.PAC_EJECUTADO = 0 
  ;*/

  <<ACTUALIZAR_PACEJECUTADO>>  
  FOR RS IN (
            SELECT  SALDO_PLAN_PPTAL.PAC_EJECUTADO
                 ,  SALDO_PLAN_PPTAL.CODIGO
              FROM  PLAN_PRESUPUESTAL 
             INNER JOIN SALDO_PLAN_PPTAL 
                     ON PLAN_PRESUPUESTAL.COMPANIA = SALDO_PLAN_PPTAL.COMPANIA  
                    AND PLAN_PRESUPUESTAL.ANO      = SALDO_PLAN_PPTAL.ANO 
                    AND PLAN_PRESUPUESTAL.CODIGO   = SALDO_PLAN_PPTAL.CODIGO 
             WHERE SALDO_PLAN_PPTAL.COMPANIA = UN_COMPANIA
               AND SALDO_PLAN_PPTAL.ANO      = UN_ANIO
               AND SALDO_PLAN_PPTAL.MES      IN (0) 
               AND PLAN_PRESUPUESTAL.MAN_PAC NOT IN (0)
             )LOOP
    -- ' Va acumulando para arriba los nuevos valores de PAC ejecutado en el mes 0
   /*REVISAR EL SIGUIENTE UPDATE. --> UPDATE SALDO_PLAN_PPTAL 
     SET SALDO_PLAN_PPTAL.PAC_EJECUTADO=NVL(SALDO_PLAN_PPTAL.PAC_EJECUTADO,0)+ NVL(RSDETALLE.PAC_EJECUTADO, 0)  
     WHERE SALDO_PLAN_PPTAL.COMPANIA = '001'  
       AND SALDO_PLAN_PPTAL.ANO      = 2011 
       AND SALDO_PLAN_PPTAL.MES      IN (0) 
       AND SALDO_PLAN_PPTAL.CODIGO  BETWEEN SUBSTR(RS.CODIGO, 0, 1) 
                                        AND SUBSTR(RS.CODIGO, 1, LENGTH(RS.CODIGO))   
     ;*/
     MI_CAMPOS := 'PAC_EJECUTADO = PAC_EJECUTADO + ' || RS.PAC_EJECUTADO ;
     MI_CONDICION := 'COMPANIA   = ''' || UN_COMPANIA || '''
                      AND ANO    = '   || UN_ANIO     || '
                      AND MES    IN (0) 
                      AND CODIGO BETWEEN ''' || SUBSTR(RS.CODIGO, 0, 1) || ''' AND ''' || SUBSTR(RS.CODIGO, 1, LENGTH(RS.CODIGO)) || '''';
      BEGIN
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'SALDO_PLAN_PPTAL',
                                    UN_ACCION    => 'M',
                                    UN_CAMPOS    => MI_CAMPOS,
                                    UN_CONDICION => MI_CONDICION
                                    );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                             UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_LIMPIARSALDOSAUX
                                             );
        END;
  END LOOP;

  --'22/11/2004
  --'Aquí recorro PacEjecutado para contabilizar los pac ejecutados. Pilar Moreno
  FOR RSDETALLE IN (
            SELECT  PACEJECUTADO.COMPANIA
                 ,  PACEJECUTADO.ANO 
                 ,  PACEJECUTADO.TIPO_CPTE
                 ,  PACEJECUTADO.COMPROBANTE
                 ,  PACEJECUTADO.CUENTA 
                 ,  EXTRACT (MONTH FROM PACEJECUTADO.FECHA) MES 
                 ,  SUM(PACEJECUTADO.MOV_DEBITO) SUMADEMOV_DEBITO 
                 ,  SUM(PACEJECUTADO.MOV_CREDITO) SUMADEMOV_CREDITO 
                 ,  MIN(DETALLE_COMPROBANTE_PPTAL.CONTRACTUAL) CONTRACTUAL 
                 ,  EXTRACT(YEAR FROM PACEJECUTADO.FECHA) ANOPAC 
                 ,  DETALLE_COMPROBANTE_PPTAL.TERCERO
                 ,  DETALLE_COMPROBANTE_PPTAL.SUCURSAL
                 ,  DETALLE_COMPROBANTE_PPTAL.AUXILIAR
                 ,  DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO
                 ,  DETALLE_COMPROBANTE_PPTAL.REFERENCIA
                 ,  DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO
                 ,  PLAN_PRESUPUESTAL.NATURALEZA
              FROM  PACEJECUTADO 
             INNER JOIN DETALLE_COMPROBANTE_PPTAL 
                     ON PACEJECUTADO.COMPANIA    = DETALLE_COMPROBANTE_PPTAL.COMPANIA 
                    AND PACEJECUTADO.ANO         = DETALLE_COMPROBANTE_PPTAL.ANO 
                    AND PACEJECUTADO.TIPO_CPTE   = DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE  
                    AND PACEJECUTADO.COMPROBANTE = DETALLE_COMPROBANTE_PPTAL.COMPROBANTE  
                    AND PACEJECUTADO.CONSECUTIVO = DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO 
             INNER JOIN PLAN_PRESUPUESTAL
                     ON PACEJECUTADO.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                    AND PACEJECUTADO.ANO      = PLAN_PRESUPUESTAL.ANO
                    AND PACEJECUTADO.CUENTA   = PLAN_PRESUPUESTAL.CODIGO
             WHERE  PACEJECUTADO.COMPANIA = UN_COMPANIA 
               AND  PACEJECUTADO.ANO      = UN_ANIO 
               AND  EXTRACT(MONTH FROM PACEJECUTADO.FECHA) BETWEEN UN_MESINICIAL AND UN_MESFINAL
             GROUP BY PACEJECUTADO.COMPANIA  
                 ,  PACEJECUTADO.ANO
                 ,  PACEJECUTADO.TIPO_CPTE
                 ,  PACEJECUTADO.COMPROBANTE
                 ,  PACEJECUTADO.CUENTA
                 ,  EXTRACT(MONTH FROM PACEJECUTADO.FECHA)
                 ,  EXTRACT(YEAR  FROM PACEJECUTADO.FECHA)
                 ,  DETALLE_COMPROBANTE_PPTAL.TERCERO
                 ,  DETALLE_COMPROBANTE_PPTAL.SUCURSAL
                 ,  DETALLE_COMPROBANTE_PPTAL.AUXILIAR
                 ,  DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO
                 ,  DETALLE_COMPROBANTE_PPTAL.REFERENCIA
                 ,  DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO
                 ,  PLAN_PRESUPUESTAL.NATURALEZA
                 ) LOOP 
    MI_STRCLASEMOV:= 'PAE';
    MI_DIFERENCIA := CASE WHEN RSDETALLE.NATURALEZA = 'D'
                          THEN RSDETALLE.SUMADEMOV_DEBITO - RSDETALLE.SUMADEMOV_CREDITO
                          ELSE RSDETALLE.SUMADEMOV_CREDITO - RSDETALLE.SUMADEMOV_DEBITO
                          END; 
  --  XX = ActPto0(dbCuadre, strClaseMov, strcompania, Ano, rsDetalle!Mes, rsDetalle!Cuenta, 0, 0, 0, rsDetalle!SumaDeMov_Debito, rsDetalle!SumaDeMov_Credito, Diferencia, IIf(Nz(rsDetalle!Contractual, 0), "C", "N"))
    PCK_PRESUPUESTO1.PR_ACTPPTO0AUX (UN_CLASE => MI_STRCLASEMOV,
                                     UN_COMPANIA => UN_COMPANIA,
                                     UN_ANIO => UN_ANIO, 
                                     UN_CODIGO => RSDETALLE.CUENTA, 
                                     UN_TERCERO => RSDETALLE.TERCERO, 
                                     UN_SUCURSAL =>  RSDETALLE.SUCURSAL,
                                     UN_AUXILIAR => RSDETALLE.AUXILIAR, 
                                     UN_CENTRO  => RSDETALLE.CENTRO_COSTO, 
                                     UN_REFERENCIA => RSDETALLE.REFERENCIA,
                                     UN_FUENTERECURSO => RSDETALLE.FUENTE_RECURSO,
                                     UN_MES => RSDETALLE.MES, 
                                     UN_DEBITO  => RSDETALLE.SUMADEMOV_DEBITO,
                                     UN_CREDITO => RSDETALLE.SUMADEMOV_CREDITO, 
                                     UN_DEBITO_ANT => 0, 
                                     UN_CREDITO_ANT => 0,
                                     UN_NATURALEZA => RSDETALLE.NATURALEZA,
                                     UN_DIFERENCIA => MI_DIFERENCIA, 
                                     UN_DIFERENCIAANT  => 0,
                                     UN_TIPO => CASE WHEN RSDETALLE.CONTRACTUAL <> 0 THEN 'C' ELSE 'N' END,
                                     UN_TIPOINGRESO => NULL
                                      );       
  END LOOP; 
   --'Aquí recorro la tabla PacTesoreria para contabilizar los pagos
   --'realizados desde tesorería (Clase="EJE").
   --'Columnas eje_cnt_debito y Eje_cnt_credito
   --'Primero comprueba que la tabla exista

  FOR RSDETALLE IN ( 
        SELECT  EXTRACT(MONTH FROM PACTESORERIA.FECHA) MES
             ,  PACTESORERIA.CUENTA
             ,  SUM(PACTESORERIA.VALOR_DEBITO) DEBITO
             ,  SUM(PACTESORERIA.VALOR_CREDITO) CREDITO 
             ,  DETALLE_COMPROBANTE_PPTAL.TERCERO
             ,  DETALLE_COMPROBANTE_PPTAL.SUCURSAL
             ,  DETALLE_COMPROBANTE_PPTAL.AUXILIAR
             ,  DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO
             ,  DETALLE_COMPROBANTE_PPTAL.REFERENCIA
             ,  DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO
             ,  PLAN_PRESUPUESTAL.NATURALEZA
          FROM  PACTESORERIA
         INNER JOIN DETALLE_COMPROBANTE_PPTAL 
                 ON PACTESORERIA.COMPANIA    = DETALLE_COMPROBANTE_PPTAL.COMPANIA 
                AND PACTESORERIA.ANO         = DETALLE_COMPROBANTE_PPTAL.ANO 
                AND PACTESORERIA.TIPO        = DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE  
                AND PACTESORERIA.NUMERO      = DETALLE_COMPROBANTE_PPTAL.COMPROBANTE  
                AND PACTESORERIA.CONSECUTIVO = DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO 
         INNER JOIN PLAN_PRESUPUESTAL
                 ON PACTESORERIA.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                AND PACTESORERIA.ANO      = PLAN_PRESUPUESTAL.ANO
                AND PACTESORERIA.CUENTA   = PLAN_PRESUPUESTAL.CODIGO
         WHERE  PACTESORERIA.COMPANIA = UN_COMPANIA
           AND  PACTESORERIA.ANO      = UN_ANIO 
           AND  EXTRACT(MONTH FROM PACTESORERIA.FECHA) BETWEEN 1 AND 1
         GROUP BY  EXTRACT(MONTH FROM PACTESORERIA.FECHA)
                ,  PACTESORERIA.CUENTA
                ,  DETALLE_COMPROBANTE_PPTAL.TERCERO
                ,  DETALLE_COMPROBANTE_PPTAL.SUCURSAL
                ,  DETALLE_COMPROBANTE_PPTAL.AUXILIAR
                ,  DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO
                ,  DETALLE_COMPROBANTE_PPTAL.REFERENCIA
                ,  DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO
                ,  PLAN_PRESUPUESTAL.NATURALEZA
                ) LOOP

   /* REVISAR ESTA PARTE -- MI_DIFERENCIA := CASE WHEN NATURALEZACUENTAP(STRCOMPANIA, ANO, RSDETALLE.CUENTA) = 'D'
                      THEN RSDETALLE.DEBITO - RSDETALLE.CREDITO
                      ELSE RSDETALLE.CREDITO - RSDETALLE.DEBITO
                 END;*/
  --   XX = ACTPTO0(DBCUADRE, "EJE", STRCOMPANIA, ANO, RSDETALLE!MES, RSDETALLE!CUENTA, 0, 0, 0, RSDETALLE!DEBITO, RSDETALLE!CREDITO, DIFERENCIA)
    PCK_PRESUPUESTO1.PR_ACTPPTO0AUX (UN_CLASE         => MI_STRCLASEMOV, 
                                     UN_COMPANIA      => UN_COMPANIA,
                                     UN_ANIO          => UN_ANIO,
                                     UN_CODIGO        => RSDETALLE.CUENTA,
                                     UN_TERCERO       => RSDETALLE.TERCERO,
                                     UN_SUCURSAL      =>  RSDETALLE.SUCURSAL,
                                     UN_AUXILIAR      => RSDETALLE.AUXILIAR,
                                     UN_CENTRO        => RSDETALLE.CENTRO_COSTO,
                                     UN_REFERENCIA    => RSDETALLE.REFERENCIA,
                                     UN_FUENTERECURSO => RSDETALLE.FUENTE_RECURSO,
                                     UN_MES           => RSDETALLE.MES,
                                     UN_DEBITO        => RSDETALLE.DEBITO,
                                     UN_CREDITO       => RSDETALLE.CREDITO,
                                     UN_DEBITO_ANT    => 0,
                                     UN_CREDITO_ANT   => 0,
                                     UN_NATURALEZA    => RSDETALLE.NATURALEZA,
                                     UN_DIFERENCIA    => MI_DIFERENCIA,
                                     UN_DIFERENCIAANT => 0,
                                     UN_TIPO          => NULL,
                                     UN_TIPOINGRESO   => NULL
                                     );       
  END LOOP;

  PCK_PRESUPUESTO.PR_LIMPIA_SALDO_AUX_PPTAL(UN_COMPANIA      => UN_COMPANIA, 
                                            UN_ANIO          => UN_ANIO,
                                            UN_CODIGOINICIAL => '0', 
                                            UN_CODIGOFINAL   => PCK_DATOS.FC_CONS_MAX_ID
                                            );


 /*eliminar los codigos que reflejan fuente 9999999 de los rubros de ingresos  TAR-7727856*/
 
    <<ELIMINAR_PLAN_PPTAL_CONFIG>>
    FOR MI_RS IN (SELECT  V_PLAN_PRESUPUESTAL.ID CUENTA,
        V_PLAN_PRESUPUESTAL.CODIGO, 
        REPLACE(V_PLAN_PRESUPUESTAL.NOMBRE,CHR(32)||CHR(32),'')NOMBRE,
        V_PLAN_PRESUPUESTAL.CENTRO_COSTO,
        V_PLAN_PRESUPUESTAL.REFERENCIA,
        V_PLAN_PRESUPUESTAL.AUXILIAR,
        V_PLAN_PRESUPUESTAL.FUENTE_RECURSO,
        SUM(APROPIACIONANTERIOR) APROPIADO,
        SUM(ADICION)ADICION,
        SUM(REDUCCION)REDUCCION,
        SUM(CREDITO)CREDITO,
        SUM(CONTRACREDITOS)CONTRACREDITOS,
        SUM(APLAZAMIENTO)APLAZAMIENTO,
        SUM(DESPLAZAMIENTO)DESPLAZAMIENTO,
        SUM(TOTALAPROPIADO)"TOTAL APROPIADO",     
        SUM(RECAUDOSMES) "RECAUDOS MES",
        SUM(RECAUDOS) "RECAUDOS ACUMULADOS"
FROM V_PLAN_PRESUPUESTAL INNER JOIN (
SELECT  COMPANIA ,
        ANO,
        ID,
        SUM(  CASE WHEN MES < 1  THEN APROPIACIONVIGENTE ELSE 0 END) AS APROPIACIONANTERIOR,
        SUM(ADICION)ADICION,
        SUM(REDUCCION)REDUCCION,
        SUM(TRASLADO_DEBITO)CREDITO,
        SUM(TRASLADO_CREDITO)CONTRACREDITOS,
        SUM(APLAZAM_CREDITO )APLAZAMIENTO,
        SUM(APLAZAM_DEBITO)DESPLAZAMIENTO,
        SUM(APROPIACIONVIGENTE)TOTALAPROPIADO,
        SUM(CASE WHEN MES = UN_MESFINAL  THEN DISPONIBILIDAD ELSE 0 END)DISPONIBILIDADMES,
        SUM(DISPONIBILIDAD)DISPONIBILIDAD,
        SUM(APROPIACIONVIGENTE  - DISPONIBILIDAD )SALDODISPONIBLE,
        SUM(  CASE WHEN MES = UN_MESFINAL THEN COMPROMISOSACUM ELSE 0 END)COMPROMISOSMES,
        SUM(COMPROMISOSACUM)COMPROMISOS,
        SUM(DISPONIBILIDAD  -  COMPROMISOSACUM )DISPONIBILIDADABIERTA,
        SUM(APROPIACIONVIGENTE -  COMPROMISOSACUM)SALDOAPROPIADO,
        SUM(  CASE WHEN MES = UN_MESFINAL THEN OBLIGACIONESACUM ELSE 0 END)OBLIGACIONESMES, 
        SUM(OBLIGACIONESACUM)OBLIGACIONES,
        SUM(COMPROMISOSACUM - OBLIGACIONESACUM)DISPONIBLEOBLIGACIONES,
        SUM(  CASE WHEN MES = UN_MESFINAL THEN EJECUCIONPPT + MODIF_INGRESOS_EFECTIVO ELSE 0 END) RECAUDOSMES,
        SUM(EJECUCIONPPT+ MODIF_INGRESOS_EFECTIVO) RECAUDOS,
        SUM(OBLIGACIONESACUM  - EJECUCIONPPT )SALDOPORPAGAR
FROM V_RESUMENPPTO_BASE
WHERE COMPANIA = UN_COMPANIA
  AND ANO = UN_ANIO
  AND MES <= UN_MESFINAL
  AND NATURALEZA = 'C'
  AND MOVIMIENTO NOT IN(0)
  AND FUENTE_RECURSO = MI_FR
  AND REFERENCIA = MI_FR
  AND CENTRO_COSTO = MI_FR
  AND AUXILIAR = MI_FR
GROUP BY    COMPANIA ,
            ANO,
            ID
ORDER BY ID)SALDOS
  ON V_PLAN_PRESUPUESTAL.COMPANIA = SALDOS.COMPANIA
  AND V_PLAN_PRESUPUESTAL.ANO      = SALDOS.ANO
  AND V_PLAN_PRESUPUESTAL.ID       = SUBSTR(SALDOS.ID, 1, LENGTH(V_PLAN_PRESUPUESTAL.ID))
GROUP BY  V_PLAN_PRESUPUESTAL.ID ,
          V_PLAN_PRESUPUESTAL.CODIGO, 
          REPLACE(V_PLAN_PRESUPUESTAL.NOMBRE,CHR(32)||CHR(32),''),
          V_PLAN_PRESUPUESTAL.CENTRO_COSTO,
          V_PLAN_PRESUPUESTAL.AUXILIAR,
          V_PLAN_PRESUPUESTAL.REFERENCIA,
          V_PLAN_PRESUPUESTAL.FUENTE_RECURSO
          having FUENTE_RECURSO IS NOT NULL 
          AND SUM(APROPIACIONANTERIOR) = 0
AND SUM(ADICION) = 0
AND SUM(REDUCCION) = 0
AND SUM(CREDITO) = 0
AND SUM(CONTRACREDITOS) = 0
AND SUM(APLAZAMIENTO) = 0
AND SUM(DESPLAZAMIENTO) = 0
AND SUM(TOTALAPROPIADO) = 0
AND SUM(RECAUDOSMES) = 0
AND SUM(RECAUDOS) = 0
ORDER BY V_PLAN_PRESUPUESTAL.ID )
LOOP
       
    

   BEGIN
   MI_CONDICION := 'COMPANIA   = '''|| UN_COMPANIA || ''' AND CODIGO = ''' || MI_RS.CODIGO || ''' AND ANO = ' || UN_ANIO || ' AND FUENTE_RECURSO = '''   || MI_FR || ''' AND REFERENCIA = '''   || MI_FR || ''' AND CENTRO_COSTO =   '''  || MI_FR || ''' AND AUXILIAR =   '''  || MI_FR ||'''';
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'PLAN_PPTAL_CONFIG', UN_ACCION => 'E', UN_CONDICION => MI_CONDICION);
     EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
       RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
     END;


END LOOP ELIMINAR_PLAN_PPTAL_CONFIG;  

   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD =>SQLCODE,
                  UN_ERROR_COD=>PCK_ERRORES.ERR_PPTO_CUADRESALDOSPTO
  );                                        
END PR_CUADRESALDOSPTO;   

PROCEDURE PR_MAYORIZAPACAPROPIADO 
  /*
  NAME              :
  AUTHORS           : 
  AUTHOR MIGRACION  : 
  DATE MIGRADOR     :
  TIME              : 
  SOURCE MODULE     : 
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       :
  @NAME:  mayorizarPacApropiado
  @METHOD:POST        
  */
  (
  -- Paramtro que recibe el numero de la compania
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA,
  -- Parametro que recibe el numero del ano
  UN_ANIO                     IN PCK_SUBTIPOS.TI_ANIO,
  -- Parametro que recibe el codigo del comprobante presupuestal
  UN_CODIGO                   IN PCK_SUBTIPOS.TI_CODIGOPPTAL 
  )
  AS 
    -- Variable que almacenara en valor de los campos a reemplazar en el control de errores
    MI_REEMPLAZOS             PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    BEGIN  
      PCK_DATOS.GL_RTA :=  PCK_DATOS.FC_ACME( UN_TABLA       => 'SALDO_PLAN_PPTAL'
                                             ,UN_ACCION      => 'MM'
                                             ,UN_MERGEUSING  => 'SELECT  SALDO_AUX_PPTAL.COMPANIA
                                                                      ,  SALDO_AUX_PPTAL.CODIGO
                                                                      ,  SALDO_AUX_PPTAL.ANO
                                                                      ,  SALDO_AUX_PPTAL.MES
                                                                      ,  SUM(SALDO_AUX_PPTAL.PAC_APROPIADO) PAC_APROPIADO
                                                                      ,  SUM(SALDO_AUX_PPTAL.PAC_PROGRAMADO) PAC_PROGRAMADO
                                                                      ,  SUM(SALDO_AUX_PPTAL.MODIF_PAC_DEBITO) MODIF_PAC_DEBITO
                                                                      ,  SUM(SALDO_AUX_PPTAL.MODIF_PAC_CREDITO) MODIF_PAC_CREDITO
                                                                      ,  SUM(SALDO_AUX_PPTAL.PACTESORERIA) PACTESORERIA
                                                                      ,  SUM(SALDO_AUX_PPTAL.PAC_EJECUTADO) PAC_EJECUTADO
                                                                      ,  SUM(SALDO_AUX_PPTAL.PAC_COMPROMETIDO) PAC_COMPROMETIDO 
                                                                   FROM  SALDO_AUX_PPTAL 
                                                                  INNER JOIN PLAN_PRESUPUESTAL 
                                                                          ON SALDO_AUX_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                                                                         AND SALDO_AUX_PPTAL.ANO      = PLAN_PRESUPUESTAL.ANO
                                                                         AND SALDO_AUX_PPTAL.CODIGO   = PLAN_PRESUPUESTAL.CODIGO
                                                                  WHERE  SALDO_AUX_PPTAL.COMPANIA = '''||UN_COMPANIA||'''
                                                                    AND  SALDO_AUX_PPTAL.ANO      = '  ||UN_ANIO    ||'
                                                                    AND  SALDO_AUX_PPTAL.CODIGO   = '''||UN_CODIGO  ||'''
                                                                    AND  PLAN_PRESUPUESTAL.MAN_PAC NOT IN (0) 
                                                                  GROUP  BY SALDO_AUX_PPTAL.CODIGO
                                                                      ,  SALDO_AUX_PPTAL.COMPANIA
                                                                      ,  SALDO_AUX_PPTAL.ANO
                                                                      ,  SALDO_AUX_PPTAL.MES'
                                            ,UN_MERGEENLACE => 'TABLA.COMPANIA   = VISTA.COMPANIA    
                                                                AND TABLA.ANO    = VISTA.ANO
                                                                AND TABLA.MES    = VISTA.MES
                                                                AND TABLA.CODIGO = VISTA.CODIGO '
                                            ,UN_MERGEEXISTE => 'UPDATE SET  TABLA.PAC_APROPIADO     = VISTA.PAC_APROPIADO,
                                                                            TABLA.PAC_PROGRAMADO    = VISTA.PAC_PROGRAMADO,
                                                                            TABLA.MODIF_PAC_DEBITO  = VISTA.MODIF_PAC_DEBITO,
                                                                            TABLA.MODIF_PAC_CREDITO = VISTA.MODIF_PAC_CREDITO,
                                                                            TABLA.PACTESORERIA      = VISTA.PACTESORERIA,
                                                                            TABLA.PAC_EJECUTADO     = VISTA.PAC_EJECUTADO,
                                                                            TABLA.PAC_COMPROMETIDO  = VISTA.PAC_COMPROMETIDO '); 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
              MI_REEMPLAZOS(0).CLAVE:='CODIGO';
              MI_REEMPLAZOS(0).VALOR:=UN_CODIGO;
              MI_REEMPLAZOS(1).CLAVE:='ANIO';
              MI_REEMPLAZOS(1).VALOR:=UN_ANIO;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                                         UN_ERROR_COD   => PCK_ERRORES.ERR_PPTO_MAYORIZA_PAC1,
                                         UN_TABLAERROR  => 'SALDO_PLAN_PPTAL',
                                         UN_REEMPLAZOS  => MI_REEMPLAZOS  
                                         );
    END;  
   BEGIN 
    PCK_DATOS.GL_RTA :=  PCK_DATOS.FC_ACME(UN_TABLA       => 'SALDO_PLAN_PPTAL',
                                           UN_ACCION      => 'MM',
                                           UN_MERGEUSING  => ' SELECT  SALDO_PLAN_PPTAL.COMPANIA
                                                                    ,  SALDO_PLAN_PPTAL.ANO
                                                                    ,  SALDO_PLAN_PPTAL.MES
                                                                    ,  SALDO_PLAN_PPTAL.CODIGO
                                                                    ,  SUM(PACAPROPIADO.PAC_APROPIADO) PAC_APROPIADO
                                                                 FROM  SALDO_PLAN_PPTAL
                                                                INNER JOIN PLAN_PRESUPUESTAL 
                                                                        ON SALDO_PLAN_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                                                                       AND SALDO_PLAN_PPTAL.ANO      = PLAN_PRESUPUESTAL.ANO
                                                                       AND SALDO_PLAN_PPTAL.CODIGO   = PLAN_PRESUPUESTAL.CODIGO
                                                                INNER JOIN
                                                                      (SELECT  SALDO_PLAN_PPTAL.COMPANIA
                                                                            ,  SALDO_PLAN_PPTAL.ANO
                                                                            ,  SALDO_PLAN_PPTAL.CODIGO
                                                                            ,  SALDO_PLAN_PPTAL.MES
                                                                            ,  PLAN_PRESUPUESTAL.MAN_PAC
                                                                            ,  SALDO_PLAN_PPTAL.PAC_APROPIADO
                                                                         FROM  SALDO_PLAN_PPTAL
                                                                        INNER JOIN PLAN_PRESUPUESTAL
                                                                                ON SALDO_PLAN_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                                                                               AND SALDO_PLAN_PPTAL.ANO      = PLAN_PRESUPUESTAL.ANO
                                                                               AND SALDO_PLAN_PPTAL.CODIGO   = PLAN_PRESUPUESTAL.CODIGO
                                                                        WHERE  SALDO_PLAN_PPTAL.COMPANIA = '''||UN_COMPANIA||'''
                                                                          AND  SALDO_PLAN_PPTAL.ANO      = '  ||UN_ANIO    ||'
                                                                          AND  PLAN_PRESUPUESTAL.MAN_PAC NOT IN (0)
                                                                       ) PACAPROPIADO 
                                                                        ON SALDO_PLAN_PPTAL.COMPANIA                                    = PACAPROPIADO.COMPANIA
                                                                       AND SALDO_PLAN_PPTAL.ANO                                         = PACAPROPIADO.ANO
                                                                       AND SALDO_PLAN_PPTAL.MES                                         = PACAPROPIADO.MES
                                                                       AND SUBSTR(PACAPROPIADO.CODIGO,0,LENGTH(SALDO_PLAN_PPTAL.CODIGO))= SALDO_PLAN_PPTAL.CODIGO
                                                                WHERE  SALDO_PLAN_PPTAL.COMPANIA           = '''||UN_COMPANIA||'''
                                                                  AND  SALDO_PLAN_PPTAL.ANO                = '  ||UN_ANIO    ||'
                                                                  AND  SUBSTR(SALDO_PLAN_PPTAL.CODIGO,0,1) = SUBSTR('''||UN_CODIGO||''',0,1)
                                                                  AND  PLAN_PRESUPUESTAL.MAN_PAC           IN (0)   
                                                                GROUP BY SALDO_PLAN_PPTAL.COMPANIA
                                                                    ,  SALDO_PLAN_PPTAL.ANO
                                                                    ,  SALDO_PLAN_PPTAL.MES
                                                                    ,  SALDO_PLAN_PPTAL.CODIGO'
                                          ,UN_MERGEENLACE => 'TABLA.COMPANIA   = VISTA.COMPANIA 
                                                              AND TABLA.ANO    = VISTA.ANO
                                                              AND TABLA.MES    = VISTA.MES
                                                              AND TABLA.CODIGO = VISTA.CODIGO'
                                          ,UN_MERGEEXISTE => ' UPDATE SET TABLA.PAC_APROPIADO = VISTA.PAC_APROPIADO '); 
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
    MI_REEMPLAZOS(0).CLAVE:='ANIO';
    MI_REEMPLAZOS(0).VALOR:=UN_ANIO;
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE,
                               UN_ERROR_COD   => PCK_ERRORES.ERR_PPTO_MAYORIZA_PAC2,
                               UN_TABLAERROR  => 'SALDO_PLAN_PPTAL',
                               UN_REEMPLAZOS => MI_REEMPLAZOS  
                               );
  END; 

  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERR_PPTO_MAYORIZAPACAPROP
  );

END PR_MAYORIZAPACAPROPIADO;

 FUNCTION FC_SALDODISPONIBLE
  /*
  NAME              : FC_SALDODISPONIBLE
  AUTHORS           : SYSMAN LTDA
  AUTHOR MIGRACION  : JAVIER VILLATE
  DATE MIGRADOR     : 05/01/2017
  TIME              : 2:00 PM 
  MODIFIER          : 
  DATE MODIFIED     : 
  DESCRIPTION       : VERIFICA SI EXISTE SALDO DISPONIBLE PARA REALIZAR UNA DISPONIBILIDAD
  MODIFICATIONS     : 
  @NAME:  verificarSaldoDisponible
  @METHOD:  POST
  */
  ( 
  -- Parametro que recibe la clase de comprobante
  UN_CLASE                    IN PCK_SUBTIPOS.TI_CLASECOMPROPPTO,  
  -- Parametro que recibe el numero de la compania
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA, 
  -- Parametro que recibe el numero del ano 
  UN_ANIO                     IN PCK_SUBTIPOS.TI_ANIO, 
  -- Parametro que recibe el numero del comprobante presupuestal
  UN_CODIGO                   IN PCK_SUBTIPOS.TI_CODIGOPPTAL,
  -- Parametro que recibe el codigo del tercero
  UN_TERCERO                  IN PCK_SUBTIPOS.TI_TERCERO, 
  -- Parametro que recibe el numero de la sucursal 
  UN_SUCURSAL                 IN PCK_SUBTIPOS.TI_SUCURSAL,
  -- Parametro que recibe el codigo del auxiliar
  UN_AUXILIAR                 IN PCK_SUBTIPOS.TI_AUXILIAR,
  -- Parametro que recibe el numero de centro de costo
  UN_CENTRO                   IN PCK_SUBTIPOS.TI_CENTRO_COSTO, 
  -- Parametro que recibe el numero de referencia
  UN_REFERENCIA               IN PCK_SUBTIPOS.TI_REFERENCIA, 
  -- Parametro que recibe la fuente de recursos
  UN_FUENTERECURSO            IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS, 
  -- Parametro que recibe el valor del debito anterior
  UN_DEBITOANT                IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0,
  -- Parametro que recibe el valor del credito anterior
  UN_CREDITOANT               IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0,
  -- Parametro que recibe el valor del debito nuevo
  UN_DEBITO                   IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0,
  -- Parametro que recibe el valor del credito nuevo
  UN_CREDITO                  IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0
  ) RETURN PCK_SUBTIPOS.TI_DOBLE
  AS
    -- Variable que almacenara el valor del saldo disponible 
    MI_SALDODISPONIBLE        PCK_SUBTIPOS.TI_DOBLE;
    -- Variable que almacenara el valor disponible
	  MI_DISPONIBLE             PCK_SUBTIPOS.TI_DOBLE;
    -- Variable que almacenara el valor rg
	  MI_VALORG                 PCK_SUBTIPOS.TI_DOBLE; 
	  MI_VALORGS                PCK_SUBTIPOS.TI_DOBLE; -- VALOR DEL SALDOS DISPONIBLE

    MI_RETORNO                PCK_SUBTIPOS.TI_DOBLE; 
    MI_NATURALEZA             PCK_SUBTIPOS.TI_NATURALEZA; 
    MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
  MI_VALORG:=0;

  IF UN_CODIGO='311010202007' THEN
        MI_VALORG:=0;
  END IF;

  IF UN_CLASE IN('DIS', 'ADD', 'TRA', 'CDP', 'RED', 'ADC', 'APL','DMD') THEN--(CFBRRERA:CC_2435)
    BEGIN
      BEGIN
        SELECT  SUM(SALDO_AUX_PPTAL.DISPONIBILIDAD) DISPONIBLE
             ,  SUM(CASE WHEN SALDO_AUX_PPTAL.NATURALEZA = 'D' 
                    THEN (APROPIACION_DEBITO - APROPIACION_CREDITO) 
                    ELSE (APROPIACION_CREDITO - APROPIACION_DEBITO)  
                    END)
                    + SUM(SALDO_AUX_PPTAL.ADICION) 
                    + SUM(SALDO_AUX_PPTAL.REDUCCION)
                    + SUM(CASE WHEN SALDO_AUX_PPTAL.NATURALEZA = 'D'
                           THEN (TRASLADO_DEBITO - TRASLADO_CREDITO)    
                           ELSE (TRASLADO_CREDITO    - TRASLADO_DEBITO)     
                           END)
                    + SUM(SALDO_AUX_PPTAL.REINTEGRO) 
                    + SUM(CASE WHEN SALDO_AUX_PPTAL.NATURALEZA = 'D' 
                          THEN (APLAZAM_DEBITO     - APLAZAM_CREDITO)  
                          ELSE (APLAZAM_CREDITO     - APLAZAM_DEBITO)     
                          END) SALDODISPONIBLE,
                PLAN_PRESUPUESTAL.NATURALEZA 
          INTO  MI_DISPONIBLE
             ,  MI_SALDODISPONIBLE 
             ,  MI_NATURALEZA
          FROM  PLAN_PRESUPUESTAL
          INNER JOIN SALDO_AUX_PPTAL
          ON PLAN_PRESUPUESTAL.COMPANIA = SALDO_AUX_PPTAL.COMPANIA
          AND PLAN_PRESUPUESTAL.ANO     = SALDO_AUX_PPTAL.ANO
          AND PLAN_PRESUPUESTAL.CODIGO  = SALDO_AUX_PPTAL.CODIGO
         WHERE  SALDO_AUX_PPTAL.COMPANIA       =  UN_COMPANIA 
           AND  SALDO_AUX_PPTAL.ANO            =  UN_ANIO 
           AND  SALDO_AUX_PPTAL.CODIGO         =  UN_CODIGO
           AND  SALDO_AUX_PPTAL.CENTRO_COSTO   =  CASE WHEN MAN_CEN_CTO NOT IN(0) THEN UN_CENTRO        ELSE PCK_DATOS.FC_CONS_CENTRO     END
           AND  SALDO_AUX_PPTAL.TERCERO        =  CASE WHEN MAN_AUX_TER NOT IN(0) THEN UN_TERCERO       ELSE PCK_DATOS.FC_CONS_TERCERO    END 
           AND  SALDO_AUX_PPTAL.SUCURSAL       =  CASE WHEN MAN_AUX_TER NOT IN(0) THEN UN_SUCURSAL      ELSE PCK_DATOS.FC_CONS_SUCURSAL   END 
           AND  SALDO_AUX_PPTAL.AUXILIAR       =  CASE WHEN MAN_AUX_GEN NOT IN(0) THEN UN_AUXILIAR      ELSE PCK_DATOS.FC_CONS_AUXILIAR   END
           AND  SALDO_AUX_PPTAL.REFERENCIA     =  CASE WHEN MAN_AUX_REF NOT IN(0) THEN UN_REFERENCIA    ELSE PCK_DATOS.FC_CONS_REFERENCIA END
           AND  SALDO_AUX_PPTAL.FUENTE_RECURSO =  CASE WHEN MAN_AUX_FUE NOT IN(0) THEN UN_FUENTERECURSO ELSE PCK_DATOS.FC_CONS_FUENTE     END
         GROUP BY SALDO_AUX_PPTAL.COMPANIA,
                SALDO_AUX_PPTAL.ANO,
                SALDO_AUX_PPTAL.CODIGO,
                PLAN_PRESUPUESTAL.NATURALEZA;    

		  EXCEPTION WHEN NO_DATA_FOUND THEN
        BEGIN
          --JP Con el fin de poder realizar adiciones y traslados a rubros existentes en plan presupuestal, 
          --   pero no en saldo_aux_pptal para los auxiliares seleccionados.
          SELECT NATURALEZA
          INTO MI_NATURALEZA
          FROM PLAN_PRESUPUESTAL
          WHERE  COMPANIA       =  UN_COMPANIA 
            AND  ANO            =  UN_ANIO 
            AND  CODIGO         =  UN_CODIGO;
          MI_SALDODISPONIBLE :=0;  
          MI_DISPONIBLE :=0;  
        EXCEPTION WHEN NO_DATA_FOUND THEN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
      END;

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
      MI_MSGERROR(1).CLAVE := 'RUBRO';
      MI_MSGERROR(1).VALOR := UN_CODIGO;
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                 UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_RUBROSIN_SALDO,
                                 UN_TABLAERROR => 'SALDO_AUX_PPTAL',
                                 UN_REEMPLAZOS => MI_MSGERROR
                                 );   
    END;   
--    MI_VALORG :=  PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     =>(NVL(MI_SALDODISPONIBLE, 0) - ((NVL(MI_DISPONIBLE, 0)- UN_VALOR) - (UN_DEBITOANT - UN_CREDITOANT)))
--                                         ,UN_PRECISION =>  2);

    MI_VALORGS :=  PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     =>NVL(MI_SALDODISPONIBLE, 0) - NVL(MI_DISPONIBLE, 0)+ UN_DEBITOANT - UN_CREDITOANT ,UN_PRECISION =>  2);



   IF UN_CLASE IN('DIS','ADD', 'CDP','DMD') THEN--(CFBRRERA:CC_2435)
       MI_VALORG :=  PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     =>NVL(MI_SALDODISPONIBLE, 0) - NVL(MI_DISPONIBLE, 0) + 
                                                            CASE WHEN MI_NATURALEZA='D' 
                                                                 THEN (UN_CREDITO - UN_CREDITOANT) - (UN_DEBITO   - UN_DEBITOANT) 
                                                                 ELSE (UN_DEBITO  - UN_DEBITOANT ) - (UN_CREDITO  - UN_CREDITOANT) END                                                                    
                                             ,UN_PRECISION =>  2);
   ELSE
        MI_VALORG :=  PCK_SYSMAN_UTL.FC_ROUND(UN_VALOR     =>NVL(MI_SALDODISPONIBLE, 0) - NVL(MI_DISPONIBLE, 0) + 
                                                        CASE WHEN MI_NATURALEZA='C' 
                                                             THEN (UN_CREDITO - UN_CREDITOANT) - (UN_DEBITO   - UN_DEBITOANT) 
                                                             ELSE (UN_DEBITO  - UN_DEBITOANT ) - (UN_CREDITO  - UN_CREDITOANT) END                                                                    
                                         ,UN_PRECISION =>  2);
   END IF;
   --CONDICION PARA VALIDAR CUANDO LOS RUBROS ESTAN EN CEROS Y NO SE PERMITAN REALIZAR DISPONIBILIDADES 
   IF UN_CLASE='DIS' OR UN_CLASE = 'CDP' OR UN_CLASE = 'DMD'  THEN--(CFBRRERA:CC_2435)
--      IF NVL(UN_DEBITO,0)-NVL(UN_CREDITO,0) > NVL(MI_VALORG,0) THEN	
      IF MI_VALORG<0 THEN

        DECLARE
          MI_MSGERROR PCK_SUBTIPOS.TI_CLAVEVALOR; 
        BEGIN
             RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
          MI_MSGERROR(1).CLAVE := 'APROPIACIONINICIAL';
          MI_MSGERROR(1).VALOR := 'Saldo Disponible';
          MI_MSGERROR(2).CLAVE := 'VALORG'; 
          MI_MSGERROR(2).VALOR := TRIM(TO_CHAR(MI_VALORGS, '999,999,999.99')); 
          MI_MSGERROR(3).CLAVE := 'VALOR';
          MI_MSGERROR(3).VALOR := TRIM(TO_CHAR(ABS(MI_VALORG),'999,999,999.99')); 
          MI_MSGERROR(4).CLAVE := 'RUBRO';
          MI_MSGERROR(4).VALOR := UN_CODIGO; 

          PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_APROPIACION_INICIAL,
                UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                UN_REEMPLAZOS => MI_MSGERROR
          );
        END;
      END IF;
    END IF;                                         


    MI_RETORNO := MI_VALORG;
    IF UN_CLASE='ADD' THEN                                         
      IF MI_VALORG<0 THEN
        DECLARE
          MI_MSGERROR PCK_SUBTIPOS.TI_CLAVEVALOR; 
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
          MI_MSGERROR(1).CLAVE := 'SALDOAPROPIACIONINICIAL';
          MI_MSGERROR(1).VALOR := 'Saldo de la Apropiación Inicial';
          MI_MSGERROR(2).CLAVE := 'VALORG'; 
          MI_MSGERROR(2).VALOR := TRIM(TO_CHAR(MI_VALORGS, '999,999,999.99')); 
          MI_MSGERROR(3).CLAVE := 'VALOR';
          MI_MSGERROR(3).VALOR := TRIM(TO_CHAR(ABS(MI_VALORG),'999,999,999.99')); 
          MI_MSGERROR(4).CLAVE := 'RUBRO';
          MI_MSGERROR(4).VALOR := UN_CODIGO; 

          PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_SALDOAPROPIA_INICIAL,
                UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                UN_REEMPLAZOS => MI_MSGERROR
          );
        END;
      END IF;
    END IF;   

    IF UN_CLASE='DIS'  OR UN_CLASE = 'CDP' THEN
      IF MI_VALORG<0 THEN	

        DECLARE
          MI_MSGERROR PCK_SUBTIPOS.TI_CLAVEVALOR; 
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
          MI_MSGERROR(1).CLAVE := 'APROPIACIONINICIAL';
          MI_MSGERROR(1).VALOR := 'Saldo Disponible';
          MI_MSGERROR(2).CLAVE := 'VALORG'; 
          MI_MSGERROR(2).VALOR :=TRIM(TO_CHAR(MI_VALORGS, '999,999,999.99')); 
          MI_MSGERROR(3).CLAVE := 'VALOR';
          MI_MSGERROR(3).VALOR := TRIM(TO_CHAR(ABS(MI_VALORG),'999,999,999.99')); 
          MI_MSGERROR(4).CLAVE := 'RUBRO';
          MI_MSGERROR(4).VALOR := UN_CODIGO; 

          PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_APROPIACION_INICIAL,
                UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                UN_REEMPLAZOS => MI_MSGERROR
          );
        END;
      END IF;
    END IF;
  END IF; 
  --VALIDA SI ES UN TRASLADO
    IF UN_CLASE IN ('TRA','RED','ADC','APL') THEN
       IF MI_VALORG<0 THEN
         DECLARE
          MI_MSGERROR PCK_SUBTIPOS.TI_CLAVEVALOR; 
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
          MI_MSGERROR(1).CLAVE := 'APROPIADO';
          MI_MSGERROR(1).VALOR := 'Valor de Traslado Mayor Que Saldo Disponible';
          MI_MSGERROR(2).CLAVE := 'VALORG'; 
          --MI_MSGERROR(2).VALOR :=TRIM(TO_CHAR(MI_SALDODISPONIBLE-MI_DISPONIBLE, '999,999,999.99')); 
          MI_MSGERROR(2).VALOR :=TRIM(TO_CHAR(MI_VALORGS, '999,999,999.99')); 
          MI_MSGERROR(3).CLAVE := 'VALOR';
          MI_MSGERROR(3).VALOR := TRIM(TO_CHAR(ABS(MI_VALORG), '999,999,999.99')); 
          MI_MSGERROR(4).CLAVE := 'RUBRO';
          MI_MSGERROR(4).VALOR := UN_CODIGO;  
          PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_APROPIACION_TRASLADO,
                UN_TABLAERROR => 'SALDO_PLAN_PPTAL',
                UN_REEMPLAZOS => MI_MSGERROR
          );
        END;
      END IF;
    END IF;

  RETURN MI_VALORG;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                 PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD =>SQLCODE,
                 UN_ERROR_COD=>PCK_ERRORES.ERR_PPTO_SALDODISPONIBLE
  );
END FC_SALDODISPONIBLE;

  PROCEDURE PR_AGREGAR_RUBROS_INF
  (
    /*
      NAME              : PR_AGREGAR_RUBROS_INF
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MIGRADOR     : 19/04/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PROCEDIMIENTO QUE ELIMINA EL REGISTRO DE UN RUBRO Y LUEGO HACE LA 
                          INSERCION DE UN NUEVO REGISTRO.
      PARAMETERS        : UN_COMPANIA     => COMPANIA EN LA QUE SE ESTA TRABAJANDO.
                          UN_ANIO         => ANIO DEL QUE SE MANEJA EL PLAN PRESUPUESTAL.
                          UN_CODIGO       => CODIGO DEL RUBRO.
                          UN_CUENTA_FINAL => CODIGO DEL RUBRO FINAL.
      MODIFICATIONS     : 

      @NAME:    agregarRubrosInferiores
      @METHOD:  PUT
    */
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_CODIGO         IN PLAN_PRESUPUESTAL.CODIGO%TYPE,
    UN_CUENTA_FINAL   IN PLAN_PRESUPUESTAL.CODIGO%TYPE
  )
  AS
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN

    FOR MI_RS IN
    (
      SELECT  DISTINCT CODIGO
      FROM    V_PLAN_PRESUPUESTAL
      WHERE   COMPANIA                           = UN_COMPANIA
        AND   ANO                                = UN_ANIO
        AND   SUBSTR(CODIGO,0,LENGTH(UN_CODIGO)) = UN_CODIGO
        AND   LENGTH(CODIGO)                     > LENGTH(UN_CODIGO)
        AND   MOVIMIENTO                         NOT IN (0)
        AND   CODIGO                             BETWEEN UN_CODIGO AND UN_CUENTA_FINAL
    )
    LOOP
      MI_CONDICION := '     COMPANIA = '''|| UN_COMPANIA ||'''
                       AND  ANO      = '  || UN_ANIO ||' 
                       AND  RUBRO    = '''|| MI_RS.CODIGO ||'''';

      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'PLAN_PPTAL_CUENTACNT',
                                                UN_ACCION    => 'E',
                                                UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
          MI_MSGERROR(1).CLAVE := 'RUBRO';
          MI_MSGERROR(1).VALOR := MI_RS.CODIGO;
          MI_MSGERROR(2).CLAVE := 'ACCION';
          MI_MSGERROR(2).VALOR := 'eliminar';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_AGR_RUBRO_INF,
            UN_REEMPLAZOS => MI_MSGERROR);
      END;

      MI_CAMPOS  := 'COMPANIA, ANO, RUBRO, CUENTA_CONTABLE';
      MI_VALORES := 'SELECT COMPANIA,
                            ANO,
                            '''|| MI_RS.CODIGO ||''' RUBRO,
                            CUENTA_CONTABLE
                     FROM   PLAN_PPTAL_CUENTACNT
                     WHERE  COMPANIA = '''|| UN_COMPANIA ||'''
                       AND  ANO      = '  || UN_ANIO ||'
                       AND  RUBRO    = '''|| UN_CODIGO ||'''';

      BEGIN
        BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => 'PLAN_PPTAL_CUENTACNT',
                                                UN_ACCION  => 'IS',
                                                UN_CAMPOS  => MI_CAMPOS,
                                                UN_VALORES => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
          MI_MSGERROR(1).CLAVE := 'RUBRO';
          MI_MSGERROR(1).VALOR := MI_RS.CODIGO;
          MI_MSGERROR(2).CLAVE := 'ACCION';
          MI_MSGERROR(2).VALOR := 'insertar';
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_AGR_RUBRO_INF,
            UN_REEMPLAZOS => MI_MSGERROR);
      END;

    END LOOP;
  END PR_AGREGAR_RUBROS_INF;

FUNCTION FC_GENERAR_REGISTRO_CON_TIPO
  /*           
  @NAME:  generarRegistroConTipo
  @METHOD:  PUT
  */
(
    UN_COMPANIA                     PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO                          PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO                         VARCHAR2,
    UN_FECHA_COMPROBANTE            DATE,
    UN_DESCRIPCION_COMPROBANTE      VARCHAR2,
    UN_OBJETO                       VARCHAR2,
    UN_DOCUMENTO_COMPROBANTE        VARCHAR2,
    UN_VLR_DOCUMENTO_COMPROBANTE    VARCHAR2,
    UN_TERCERO                      PCK_SUBTIPOS.TI_TERCERO,
    UN_SUCURSAL                     PCK_SUBTIPOS.TI_SUCURSAL,
    UN_DEBITO_COMPROBANTE           PCK_SUBTIPOS.TI_DOBLE,
    UN_CREDITO_COMPROBANTE          PCK_SUBTIPOS.TI_DOBLE,
    UN_ABONADO_COMPROBANTE          PCK_SUBTIPOS.TI_DOBLE,
    UN_CREADOR_COMPROBANTE          VARCHAR2,
    UN_DESTINO_COMPROBANTE          VARCHAR2,
    UN_TIPO_COMPROBANTE             VARCHAR2,
    UN_NUMERO_COMPROBANTE           PCK_SUBTIPOS.TI_LONG


)RETURN NUMBER AS 
    MI_CONSECUTIVO      PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_AUXCONSECUTIVO   PCK_SUBTIPOS.TI_ENTERO;
    MI_STRCONSECUTIVO   VARCHAR2(10CHAR);
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_PCKDATOS         PCK_SUBTIPOS.TI_ENTERO;
    MI_EXISTE 		      PCK_SUBTIPOS.TI_TEXTO1 DEFAULT 'N';


    CURSOR MI_RS_DETALLE IS SELECT CONSECUTIVO,
                                    CUENTA, 
                                    FECHA,
                                    NATURALEZA, 
                                    DESCRIPCION,
                                    VALOR_DEBITO,
                                    VALOR_CREDITO, 
                                    TIPO_DOCUMENTO, 
                                    NRO_DOCUMENTO, 
                                    CENTRO_COSTO,
                                    AUXILIAR
                             FROM DETALLE_COMPROBANTE_PPTAL  
                             WHERE COMPANIA  = UN_COMPANIA
                             AND ANO         = UN_ANO
                             AND TIPO_CPTE   = UN_TIPO_COMPROBANTE
                             AND COMPROBANTE = UN_NUMERO_COMPROBANTE
                             ORDER BY COMPANIA,ANO, TIPO_CPTE,COMPROBANTE;

  BEGIN 
      SELECT MAX(NUMERO) KEEP (DENSE_RANK LAST ORDER BY ROWNUM) ULTIMO INTO MI_CONSECUTIVO
      FROM COMPROBANTE_PPTAL 
      WHERE COMPANIA = UN_COMPANIA
      AND ANO        = UN_ANO
      AND TIPO       = UN_TIPO
      ORDER BY NUMERO;

      BEGIN
        IF MI_CONSECUTIVO IS NULL THEN 
          SELECT CONSECUTIVO INTO MI_AUXCONSECUTIVO
          FROM CONSECUTIVOTCP
          WHERE COMPANIA = UN_COMPANIA
          AND ANO        = UN_ANO
          AND TIPOCOMPROBANTE = UN_TIPO;
            IF MI_AUXCONSECUTIVO IS NOT NULL THEN 
                MI_STRCONSECUTIVO :=  UN_ANO||PCK_SYSMAN_UTL.FC_STRZERO(MI_AUXCONSECUTIVO,6);
            END IF;
        END IF;
        EXCEPTION WHEN NO_DATA_FOUND THEN
           MI_STRCONSECUTIVO :=  UN_ANO||PCK_SYSMAN_UTL.FC_STRZERO(1,6);
           MI_CONSECUTIVO := TO_NUMBER(MI_STRCONSECUTIVO);
      END;


      MI_CAMPOS := 'COMPANIA,
                    ANO,  
                    TIPO,  
                    NUMERO,  
                    FECHA,  
                    FECHA_VENCIMIENTO,  
                    DESCRIPCION,  
                    TEXTO,  
                    NRO_DOCUMENTO,  
                    FECHA_VCN_DOC, 
                    VLR_DOCUMENTO, 
                    TERCERO, 
                    SUCURSAL,  
                    DEBITO, 
                    CREDITO,
                    ABONADO, 
                    CREATED_BY, 
                    MODIFIED_BY,
                    CONTRACTUAL,
                    DESTINO';

          MI_VALORES :=''''||UN_COMPANIA||''',
                    '||UN_ANO||',
                    '''||UN_TIPO||''',
                    '||MI_CONSECUTIVO||',
                    TO_DATE('''||UN_FECHA_COMPROBANTE||''', ''DD/MM/YYYY HH24:MI:SS''),
                    TO_DATE('''||ADD_MONTHS(UN_FECHA_COMPROBANTE,1)||''', ''DD/MM/YYYY HH24:MI:SS''),
                    '''||UN_DESCRIPCION_COMPROBANTE||''',
                    '''||UN_OBJETO||''',
                    '''||UN_DOCUMENTO_COMPROBANTE||''',
                    TO_DATE('''||ADD_MONTHS(UN_FECHA_COMPROBANTE,1)||''', ''DD/MM/YYYY HH24:MI:SS''),
                    '||UN_VLR_DOCUMENTO_COMPROBANTE||',
                    '''||UN_TERCERO||''',
                    '''||UN_SUCURSAL||''',
                    '||UN_DEBITO_COMPROBANTE||',
                    '||UN_CREDITO_COMPROBANTE||',
                    '||UN_ABONADO_COMPROBANTE||',
                    '''||UN_CREADOR_COMPROBANTE||''',
                    '''||UN_CREADOR_COMPROBANTE||''',
                    0,
                    '''||UN_DESTINO_COMPROBANTE||'''
                    ';
      BEGIN       
          MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA   => 'COMPROBANTE_PPTAL',
                                           UN_ACCION  => 'I',
                                           UN_CAMPOS  => MI_CAMPOS,
                                           UN_VALORES => MI_VALORES);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;

      END;
      FOR RS_DETALLE IN MI_RS_DETALLE LOOP
          MI_CAMPOS:='COMPANIA, 
                      ANO, 
                      TIPO_CPTE, 
                      COMPROBANTE, 
                      CONSECUTIVO, 
                      CUENTA,  
                      FECHA, 
                      NATURALEZA, 
                      DESCRIPCION, 
                      VALOR_DEBITO, 
                      VALOR_CREDITO, 
                      TIPO_DOCUMENTO, 
                      NRO_DOCUMENTO, 
                      CENTRO_COSTO, 
                      TERCERO, 
                      SUCURSAL, 		
                      AUXILIAR, 
                      TIPO_CPTE_AFECT, 
                      CMPTE_AFECTADO, 
                      CONSECUTIVOPPTO';

          MI_VALORES :=''''||UN_COMPANIA||''',
                        '||UN_ANO||',
                        '''||UN_TIPO||''',
                        '||MI_CONSECUTIVO||',
                        '||RS_DETALLE.CONSECUTIVO||',
                        '''||RS_DETALLE.CUENTA||''',
                        TO_DATE('''||UN_FECHA_COMPROBANTE||''', ''DD/MM/YYYY HH24:MI:SS''),
                        '''||RS_DETALLE.NATURALEZA||''',
                        '''||RS_DETALLE.DESCRIPCION||''',
                        '||RS_DETALLE.VALOR_DEBITO||',
                        '||RS_DETALLE.VALOR_CREDITO||',
                        '''||RS_DETALLE.TIPO_DOCUMENTO||''',
                        '''||RS_DETALLE.NRO_DOCUMENTO||''',
                        '''||RS_DETALLE.CENTRO_COSTO||''',
                        '''||UN_TERCERO||''',
                        '''||UN_SUCURSAL||''',                        
                        '''||RS_DETALLE.AUXILIAR||''',
                        '''||UN_TIPO_COMPROBANTE||''',
                        '||UN_NUMERO_COMPROBANTE||',
                        '||RS_DETALLE.CONSECUTIVO||'                        
                        ';            
        BEGIN
            MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA   => 'DETALLE_COMPROBANTE_PPTAL',
                                             UN_ACCION  => 'I',
                                             UN_CAMPOS  => MI_CAMPOS,
                                             UN_VALORES => MI_VALORES);                       
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;                        
        END;                                             

            MI_CAMPOS :='COMPANIA,
                         ANO,
                         TIPO_CPTE,
                         COMPROBANTE,
                         CONSECUTIVO,
                         CUENTA,FECHA,
                         MOV_DEBITO,
                         MOV_CREDITO';                

            MI_VALORES :=''''||UN_COMPANIA||''',
                        '||UN_ANO||',
                        '''||UN_TIPO||''',
                        '||MI_CONSECUTIVO||',
                        '||RS_DETALLE.CONSECUTIVO||',
                        '''||RS_DETALLE.CUENTA||''',
                        TO_DATE('''||UN_FECHA_COMPROBANTE||''', ''DD/MM/YYYY HH24:MI:SS''),
                        '||RS_DETALLE.VALOR_DEBITO||',
                        '||RS_DETALLE.VALOR_CREDITO||',                        
                        ';          
          BEGIN
            MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA   => 'PACPROGRAMADO',
                                             UN_ACCION  => 'I',
                                             UN_CAMPOS  => MI_CAMPOS,
                                             UN_VALORES => MI_VALORES);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;                         
          END;                                             

            MI_CAMPOS :='DEBITO_AFECTADO='||RS_DETALLE.VALOR_DEBITO||'
                         ,CREDITO_AFECTADO ='||RS_DETALLE.VALOR_CREDITO||'';          

            MI_CONDICION:='COMPANIA='''||UN_COMPANIA||'''
                          AND ANO = '||UN_ANO||'
                          AND TIPO_CPTE = '''||UN_TIPO_COMPROBANTE||'''
                          AND COMPROBANTE = '||UN_NUMERO_COMPROBANTE||'
                          AND CONSECUTIVO='||RS_DETALLE.CONSECUTIVO||'';
        BEGIN         
            MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA => 'DETALLE_COMPROBANTE_PPTAL', 
                                             UN_ACCION => 'M', 
                                             UN_CAMPOS => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION); 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;                                  
        END;

            MI_CAMPOS :='DEBITO_AFECTADO=DEBITO_AFECTADO +'||RS_DETALLE.VALOR_DEBITO||'
                         ,CREDITO_AFECTADO =CREDITO_AFECTADO + '||RS_DETALLE.VALOR_CREDITO||'';          

            MI_CONDICION:='COMPANIA='''||UN_COMPANIA||'''
                          AND ANO = '||UN_ANO||'
                          AND TIPO = '''||UN_TIPO_COMPROBANTE||'''
                          AND NUMERO = '||UN_NUMERO_COMPROBANTE||'';

        BEGIN                  
            MI_PCKDATOS := PCK_DATOS.FC_ACME(UN_TABLA => 'COMPROBANTE_PPTAL', 
                                             UN_ACCION => 'M', 
                                             UN_CAMPOS => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION);  
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;                                  
        END;

      END LOOP;
            RETURN MI_CONSECUTIVO;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD =>SQLCODE,
                    UN_ERROR_COD=>PCK_ERRORES.ERR_PPTO_CUADRESALDOSPTO
                    );

END FC_GENERAR_REGISTRO_CON_TIPO;

  --10. diferenciaValidaVA
  FUNCTION FC_DIFERENCIA_VALIDA_VA
  /*
    NAME              : FC_DIFERENCIA_VALIDA_VA
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
    DATE MIGRADOR     : 25/04/2017
    TIME              : 09:48 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Valida que el total de créditos sea igual al de contracréditos 
                        para las cuentas que tengan configurado el tipo de vigencia: 
                        "Vigencia Actual". Para clases de tipo Reducción, Adición o Traslado.
    RETURN            : Verdadero si la diferencia entre creditos y ;
                        contracreditos es cero o no aplica la validación.
    MODIFICATIONS     : 
    PARAMETROS DE ENTRADA: 
      UN_COMPANIA:          Código de la compañía
      UN_ANIO:              Año en el cual se realiza el comprobante
      UN_TIPO_CPTE:         Tipo de comprobante presupuestal 
      UN_COMPROBANTE:       Número del comprobante presupuestal
      UN_CLASE_COMPROBANTE: Clase del tipo del comprobanet presupuestal.

    @NAME: diferenciaValidaVA
    @METHOD: get
  */
  (
    UN_COMPANIA                     IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_ANIO                         IN PCK_SUBTIPOS.TI_ANIO
  , UN_TIPO_CPTE                    IN DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE%TYPE
  , UN_COMPROBANTE                  IN DETALLE_COMPROBANTE_PPTAL.COMPROBANTE%TYPE
  , UN_CLASE_COMPROBANTE            IN TIPO_COMPROBPP.CLASE%TYPE
  )RETURN NUMBER AS
    MI_TOTAL_DEBITO                  PCK_SUBTIPOS.TI_DOBLE;
    MI_TOTAL_CREDITO                 PCK_SUBTIPOS.TI_DOBLE;
  BEGIN
    SELECT NVL(SUM(VALOR_DEBITO), 0) SUMA_DEBITO,
           NVL(SUM(VALOR_CREDITO), 0) SUMA_CREDITO
      INTO MI_TOTAL_DEBITO, MI_TOTAL_CREDITO
      FROM DETALLE_COMPROBANTE_PPTAL
     INNER JOIN PLAN_PRESUPUESTAL
        ON DETALLE_COMPROBANTE_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
       AND DETALLE_COMPROBANTE_PPTAL.ANO = PLAN_PRESUPUESTAL.ANO
       AND DETALLE_COMPROBANTE_PPTAL.CUENTA = PLAN_PRESUPUESTAL.CODIGO
     WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA = UN_COMPANIA
       AND DETALLE_COMPROBANTE_PPTAL.ANO = UN_ANIO
       AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE = UN_TIPO_CPTE
       AND DETALLE_COMPROBANTE_PPTAL.COMPROBANTE = UN_COMPROBANTE
       AND PLAN_PRESUPUESTAL.TIPOVIGENCIA = 'VA';
    -- Validación de clase
    IF UN_CLASE_COMPROBANTE IN('RED', 'ADC', 'TRA') THEN
      RETURN CASE WHEN MI_TOTAL_DEBITO = MI_TOTAL_CREDITO THEN -1 ELSE 0 END;
    ELSE
      RETURN -1;
    END IF;
  END FC_DIFERENCIA_VALIDA_VA;

   FUNCTION FC_AFECTARCPTES
  /*
    NAME              : FC_AFECTARCPTES
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 25/04/2017
    TIME              : 05:15 PM
    DESCRIPTION       : Se tra el procedimiento de java a PLSQL. del formulario PedirSolicitudAfectarControlador
    RETURN            : Las inconsistencias generadas en el proceso para generar un plano.
    MODIFIER          : GUSTAVO ANDRES FIGUEREDO AVILA
    DATE MODIFIED     : 23/04/2021
    TIME              : 03:05 PM
    MODIFICATIONS     : Se modifica la consulta asignada cuando la variable MI_AGRUPARSOLICITUD toma el valor de SI
    					para que el group by se realice por los valores que se traen en el select.
    PARAMETROS DE ENTRADA: 
      UN_COMPANIA:          CÃ³digo de la compaÃ±Ã­a
      UN_ANO:               AÃ±o en el cual se realiza el comprobante
      UN_FECHA              Fecha en el cual se realiza el comprobante
      UN_TIPOT              Tipo de solicitud que afecta el comprobante
      UN_USUARIO            Usuario que ingreso a la aplicacion
      UN_TIPO_CPTE_AFECT    Tipo de comprobante presupuestal afectado
      UN_CMPTE_AFECTADO     NÃºmero del comprobante presupuestal afectado
      UN_DEPENDENCIA        Dependencia en la cual se realiza el comprobante 
      UN_NOVEDAD            Novedad de la solicitud que afecta el comprobante 
      UN_CLASET             Clase de la solicitud que afecta el comprobante 
      UN_VLR_DOCUMENTO      Valor con el cual se realiza el comprobante
      UN_DEBITO             Valor con el cual se realiza el comprobante
      UN_CARGO              Cargo con el cual se realiza el comprobante 
      UN_COD_PROYECTO       Codigo del proyecto al cual pertenece la solicitud que afecta el comprobante 
      UN_DESCRIPCION        Descripcion en el cual se realiza el comprobante
      UN_TIPO_CPTE:         Tipo de comprobante presupuestal 
      UN_NUMERO_CPTE:       NÃºmero del comprobante presupuestal

    @NAME: afectarCptesDesdeSolicitudes
    @METHOD: get
  */
(
     UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_ANO             IN PCK_SUBTIPOS.TI_ANIO
    ,UN_FECHA           IN DATE
    ,UN_TIPOT           IN BP_D_NOVEDADPROYECTO.TIPOT%TYPE
    ,UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
    ,UN_TIPO_CPTE_AFECT IN DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE_AFECT%TYPE
    ,UN_CMPTE_AFECTADO  IN DETALLE_COMPROBANTE_PPTAL.CMPTE_AFECTADO%TYPE
    ,UN_DEPENDENCIA     IN BP_D_NOVEDADPROYECTO.DEPENDENCIA%TYPE
    ,UN_NOVEDAD         IN BP_D_NOVEDADPROYECTO.NOVEDAD%TYPE 
    ,UN_CLASET          IN BP_D_NOVEDADPROYECTO.CLASET%TYPE   
    ,UN_VLR_DOCUMENTO   IN COMPROBANTE_PPTAL.VLR_DOCUMENTO%TYPE
    ,UN_DEBITO          IN COMPROBANTE_PPTAL.DEBITO%TYPE
    ,UN_CARGO           IN COMPROBANTE_PPTAL.CARGO%TYPE
    ,UN_COD_PROYECTO    IN COMPROBANTE_PPTAL.COD_PROYECTO_PPTAL%TYPE
    ,UN_DESCRIPCION     IN DETALLE_COMPROBANTE_PPTAL.DESCRIPCION%TYPE
    ,UN_TIPO_CPTE       IN DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE%TYPE
    ,UN_NUMERO_CPTE     IN DETALLE_COMPROBANTE_PPTAL.COMPROBANTE%TYPE
    ,UN_DESTINO         IN VARCHAR2
)RETURN VARCHAR2 
AS

    MI_APROPIADO         PCK_SUBTIPOS.TI_DOBLE;
    MI_ADICION           PCK_SUBTIPOS.TI_DOBLE;
    MI_REDUCCION         PCK_SUBTIPOS.TI_DOBLE;
    MI_TRASLADO          PCK_SUBTIPOS.TI_DOBLE;
    MI_DEBITOC           PCK_SUBTIPOS.TI_DOBLE;
    MI_DISPONIBILIDAD    PCK_SUBTIPOS.TI_DOBLE;
    MI_STRSQL            PCK_SUBTIPOS.TI_STRSQL;
    MI_TABLA             PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES           PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;
    MI_REEMPLAZOS        PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RUBROPRESUPUESTAL BP_D_NOVEDADPROYECTO.RUBROPRESUPUESTAL%TYPE;
    MI_FUENTERECURSOS    BP_D_NOVEDADPROYECTO.FUENTERECURSOS%TYPE;
    MI_NATURALEZA        PCK_SUBTIPOS.TI_NATURALEZA;
    MI_RTA               PCK_SUBTIPOS.TI_ENTERO;
    MI_SALDO             PCK_SUBTIPOS.TI_DOBLE;
    MI_AGRUPARSOLICITUD  PARAMETRO.VALOR%TYPE;
    MI_STRDESCRIPCION    DETALLE_COMPROBANTE_PPTAL.DESCRIPCION%TYPE;
    MI_EXISTE            BP_D_NOVEDADPROYECTO.RUBROPRESUPUESTAL%TYPE;
    MI_CONSECUTIVODET    PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_RSDETALLE         SYS_REFCURSOR;
    MI_VALORSOLICITADO   BP_D_NOVEDADPROYECTO.VALORSOLICITADO%TYPE;
    MI_CONSECUTIVO       BP_D_NOVEDADPROYECTO.CODIGO%TYPE;
    MI_MSGERROR          PCK_SUBTIPOS.TI_STRSQL;
    MI_SALDOPARADISPONIBILIDADES PCK_SUBTIPOS.TI_DOBLE;
    MI_CENTROCOSTO       DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO%TYPE;
    MI_CENTRO_COSTO      DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO%TYPE;
    MI_REFERENCIA        DETALLE_COMPROBANTE_PPTAL.REFERENCIA%TYPE;
    MI_TERCERO           DETALLE_COMPROBANTE_PPTAL.TERCERO%TYPE;
    MI_SUCURSAL          DETALLE_COMPROBANTE_PPTAL.SUCURSAL%TYPE;
    MI_AUXILIAR          DETALLE_COMPROBANTE_PPTAL.AUXILIAR%TYPE;
    MI_CREDITOC          PCK_SUBTIPOS.TI_DOBLE; 
    MI_SUMADEBITO        DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO%TYPE;
    MI_SUMACREDITO       DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO%TYPE;
    MI_OBJETO_CDP        COMPROBANTE_PPTAL.DESCRIPCION%TYPE;
    MI_SECTORRUBRO       BP_D_NOVEDADPROYECTO.SECTORRUBRO%TYPE;
    MI_PROGRAMARUBRO     BP_D_NOVEDADPROYECTO.PROGRAMARUBRO%TYPE;
    MI_SUBPROGRAMARUBRO  BP_D_NOVEDADPROYECTO.SUBPROGRAMARUBRO%TYPE;
    MI_CODIGOPRODUCTO    BP_D_NOVEDADPROYECTO.CODIGOPRODUCTO%TYPE;
    MI_CODIGOBPIN        BP_D_NOVEDADPROYECTO.CODIGOBPIN%TYPE;
    MI_CODIGOCCPET       BP_D_NOVEDADPROYECTO.CODIGOCCPET%TYPE;
    MI_CODIGOCPCDANE     BP_D_NOVEDADPROYECTO.CODIGOCPCDANE%TYPE;
    MI_CODIGOUNIDADEJECUTORA   BP_D_NOVEDADPROYECTO.CODIGOUNIDADEJECUTORA%TYPE;
    MI_CODIGOFUENTE      BP_D_NOVEDADPROYECTO.CODIGOFUENTE%TYPE;
    MI_CODIGOCCPETREGALIAS     BP_D_NOVEDADPROYECTO.CODIGOCCPETREGALIAS%TYPE;
    MI_CODIGODETALLESECTORIAL   BP_D_NOVEDADPROYECTO.CODIGODETALLESECTORIAL%TYPE;
  
BEGIN 

  -- 07/09/2018 @jreina Se agrega if con llmado a funcion.
  IF UN_DESTINO = 'F' THEN
      PCK_PRESUPUESTO2.PR_AFECTARCPTESCAQ( UN_COMPANIA    => UN_COMPANIA,
                                           UN_ANO         => UN_ANO, 
                                           UN_COMPR_AFECT => UN_CMPTE_AFECTADO,
                                           UN_NUMERO      => UN_NUMERO_CPTE,
                                           UN_FECHA       => UN_FECHA,
                                           UN_TIPO        => UN_TIPO_CPTE,
                                           UN_TIPO_AFECT  => UN_TIPO_CPTE_AFECT,
                                           UN_USUARIO     => UN_USUARIO);
  ELSE
    IF UN_TIPO_CPTE_AFECT IS NOT NULL 
       AND NVL(UN_CMPTE_AFECTADO, 0) NOT IN (0) THEN
        --'Busco los detalles a copiar
        --'SE VERIFICA QUE LOS RUBROS ASOCIADOS AL COMPROBANTE TENGAN SALDO DISPONIBLE
        <<VERIFICARUBROS>>
        FOR MI_RSDETALLE IN ( 
             SELECT  BP_D_NOVEDADPROYECTO.RUBROPRESUPUESTAL 
                    ,PLAN_PRESUPUESTAL.NATURALEZA
                    ,BP_D_NOVEDADPROYECTO.CODIGO CONSECUTIVO
               FROM BP_D_NOVEDADPROYECTO 
                   INNER JOIN PLAN_PRESUPUESTAL 
                       ON  BP_D_NOVEDADPROYECTO.COMPANIA          = PLAN_PRESUPUESTAL.COMPANIA 
                       AND BP_D_NOVEDADPROYECTO.ANORUBRO          = PLAN_PRESUPUESTAL.ANO 
                       AND BP_D_NOVEDADPROYECTO.RUBROPRESUPUESTAL = PLAN_PRESUPUESTAL.CODIGO 
              WHERE BP_D_NOVEDADPROYECTO.COMPANIA    = UN_COMPANIA 
                AND BP_D_NOVEDADPROYECTO.TIPOT       = UN_TIPOT 
                AND BP_D_NOVEDADPROYECTO.CLASET      = UN_CLASET 
                AND BP_D_NOVEDADPROYECTO.NOVEDAD     = UN_NOVEDAD 
                AND BP_D_NOVEDADPROYECTO.DEPENDENCIA = UN_DEPENDENCIA)
        LOOP
            BEGIN 
                SELECT   SUM(SALDO_PLAN_PPTAL.DISPONIBILIDAD) DISPONIBILIDAD
                       , SUM(SALDO_PLAN_PPTAL.ADICION) ADICION
                       , SUM(SALDO_PLAN_PPTAL.REDUCCION) REDUCCION
                       , SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA = 'D'
                                  THEN APROPIACION_DEBITO-APROPIACION_CREDITO
                                  ELSE APROPIACION_CREDITO-APROPIACION_DEBITO
                             END) APROPIADO
                       , SUM(CASE WHEN PLAN_PRESUPUESTAL.NATURALEZA = 'D'
                                  THEN TRASLADO_DEBITO-TRASLADO_CREDITO
                                  ELSE TRASLADO_CREDITO-TRASLADO_DEBITO
                             END) TRASLADO
                INTO  MI_DISPONIBILIDAD
                     ,MI_ADICION
                     ,MI_REDUCCION
                     ,MI_APROPIADO
                     ,MI_TRASLADO
                FROM PLAN_PRESUPUESTAL 
                    INNER JOIN SALDO_PLAN_PPTAL 
                        ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_PLAN_PPTAL.COMPANIA 
                        AND PLAN_PRESUPUESTAL.ANO      = SALDO_PLAN_PPTAL.ANO 
                        AND PLAN_PRESUPUESTAL.CODIGO   = SALDO_PLAN_PPTAL.CODIGO
                WHERE SALDO_PLAN_PPTAL.COMPANIA = UN_COMPANIA 
                  AND SALDO_PLAN_PPTAL.ANO      = UN_ANO 
                  AND SALDO_PLAN_PPTAL.MES      < 13
                  AND SALDO_PLAN_PPTAL.CODIGO   = MI_RSDETALLE.RUBROPRESUPUESTAL
                GROUP BY   SALDO_PLAN_PPTAL.COMPANIA
                         , SALDO_PLAN_PPTAL.ANO
                         , SALDO_PLAN_PPTAL.CODIGO; 
            EXCEPTION WHEN NO_DATA_FOUND THEN 
                MI_APROPIADO :=0;
                MI_ADICION   :=0;
                MI_REDUCCION :=0;
                MI_TRASLADO  :=0;
                MI_DISPONIBILIDAD:=0;
            END;
            MI_SALDO:= (NVL(MI_APROPIADO, 0) 
                        + NVL(MI_ADICION, 0) 
                        + NVL(MI_REDUCCION, 0) 
                        + NVL(MI_TRASLADO, 0)
                       )- NVL(MI_DISPONIBILIDAD, 0);
            IF MI_SALDO < 0 THEN
                BEGIN 
                    MI_REEMPLAZOS(0).CLAVE:='RUBROPRESUPUESTAL';
                    MI_REEMPLAZOS(0).VALOR:=MI_RSDETALLE.RUBROPRESUPUESTAL;
                    MI_REEMPLAZOS(1).CLAVE:='UN_CMPTE_AFECTADO';
                    MI_REEMPLAZOS(1).VALOR:=UN_CMPTE_AFECTADO;
                    MI_REEMPLAZOS(2).CLAVE:='SALDO';
                    MI_REEMPLAZOS(2).VALOR:=TO_CHAR(MI_SALDO,'999,999,999,999.00');
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD   =>SQLCODE
                               ,UN_ERROR_COD =>PCK_ERRORES.ERRR_PPTO_PEDIR_AFECTA_CPTES_1
                               ,UN_TABLAERROR => 'PLAN_PRESUPUESTAL'
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                              );
                END;      
            END IF;
        END LOOP VERIFICARUBROS;
        --Selecionando detalles"
        MI_AGRUPARSOLICITUD:=PCK_SYSMAN_UTL.FC_PAR(
                                            UN_COMPANIA  => UN_COMPANIA
                                           ,UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO 
                                           ,UN_NOMBRE    => 'AGRUPAR SOLICITUD AFECTADA DE BANCO DE PROYECTOS'
                                           ,UN_FECHA_PAR => SYSDATE);
        IF MI_AGRUPARSOLICITUD = 'SI' THEN
            MI_STRSQL:='SELECT
								SUM(BP_D_NOVEDADPROYECTO.VALORSOLICITADO) VALORSOLICITADO,
								BP_D_NOVEDADPROYECTO.RUBROPRESUPUESTAL,
								CASE
									WHEN PLAN_PRESUPUESTAL.MAN_AUX_FUE NOT IN (0) THEN BP_D_NOVEDADPROYECTO.FUENTERECURSOS
									ELSE PCK_DATOS.FC_CONS_FUENTE
								END FUENTERECURSOS,
								CASE
									WHEN PLAN_PRESUPUESTAL.MAN_AUX_REF NOT IN (0) THEN BP_D_NOVEDADPROYECTO.REFERENCIA
									ELSE PCK_DATOS.FC_CONS_REFERENCIA
								END REFERENCIA,
								CASE
									WHEN PLAN_PRESUPUESTAL.MAN_CEN_CTO NOT IN (0) THEN BP_D_NOVEDADPROYECTO.CENTRO_COSTO
									ELSE PCK_DATOS.FC_CONS_CENTRO
								END CENTROCOSTO,
								CASE
									WHEN PLAN_PRESUPUESTAL.MAN_AUX_GEN NOT IN (0) THEN BP_D_NOVEDADPROYECTO.AUXILIAR
									ELSE PCK_DATOS.FC_CONS_AUXILIAR
								END AUXILIAR,
								PLAN_PRESUPUESTAL.NATURALEZA,
								MAX(BP_D_NOVEDADPROYECTO.CODIGO) CONSECUTIVO,
                                BP_D_NOVEDADPROYECTO.SECTORRUBRO,
                                BP_D_NOVEDADPROYECTO.PROGRAMARUBRO,
                                BP_D_NOVEDADPROYECTO.SUBPROGRAMARUBRO,
                                BP_D_NOVEDADPROYECTO.CODIGOPRODUCTO,
                                BP_D_NOVEDADPROYECTO.CODIGOBPIN,
                                BP_D_NOVEDADPROYECTO.CODIGOCCPET,
                                BP_D_NOVEDADPROYECTO.CODIGOCPCDANE,
                                BP_D_NOVEDADPROYECTO.CODIGOUNIDADEJECUTORA,
                                BP_D_NOVEDADPROYECTO.CODIGOFUENTE,
                                BP_D_NOVEDADPROYECTO.CODIGOCCPETREGALIAS,
                                BP_D_NOVEDADPROYECTO.CODIGODETALLESECTORIAL
							FROM
								BP_D_NOVEDADPROYECTO
							INNER JOIN PLAN_PRESUPUESTAL ON
								BP_D_NOVEDADPROYECTO.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
								AND BP_D_NOVEDADPROYECTO.ANORUBRO = PLAN_PRESUPUESTAL.ANO
								AND BP_D_NOVEDADPROYECTO.RUBROPRESUPUESTAL = PLAN_PRESUPUESTAL.CODIGO
							WHERE
								BP_D_NOVEDADPROYECTO.COMPANIA = '''||UN_COMPANIA||'''
								AND BP_D_NOVEDADPROYECTO.TIPOT = '''||UN_TIPOT||'''
								AND BP_D_NOVEDADPROYECTO.CLASET = '''||UN_CLASET||'''
								AND BP_D_NOVEDADPROYECTO.NOVEDAD = '''||UN_NOVEDAD||'''
								AND BP_D_NOVEDADPROYECTO.DEPENDENCIA = '''||UN_DEPENDENCIA||'''
							GROUP BY
								BP_D_NOVEDADPROYECTO.RUBROPRESUPUESTAL,
								CASE
									WHEN PLAN_PRESUPUESTAL.MAN_AUX_FUE NOT IN (0) THEN BP_D_NOVEDADPROYECTO.FUENTERECURSOS
									ELSE PCK_DATOS.FC_CONS_FUENTE
								END,
								CASE
									WHEN PLAN_PRESUPUESTAL.MAN_AUX_REF NOT IN (0) THEN BP_D_NOVEDADPROYECTO.REFERENCIA
									ELSE PCK_DATOS.FC_CONS_REFERENCIA
								END,
								CASE
									WHEN PLAN_PRESUPUESTAL.MAN_CEN_CTO NOT IN (0) THEN BP_D_NOVEDADPROYECTO.CENTRO_COSTO
									ELSE PCK_DATOS.FC_CONS_CENTRO
								END,
								CASE
									WHEN PLAN_PRESUPUESTAL.MAN_AUX_GEN NOT IN (0) THEN BP_D_NOVEDADPROYECTO.AUXILIAR
									ELSE PCK_DATOS.FC_CONS_AUXILIAR
								END,
								PLAN_PRESUPUESTAL.NATURALEZA,
                                BP_D_NOVEDADPROYECTO.SECTORRUBRO,
                                BP_D_NOVEDADPROYECTO.PROGRAMARUBRO,
                                BP_D_NOVEDADPROYECTO.SUBPROGRAMARUBRO,
                                BP_D_NOVEDADPROYECTO.CODIGOPRODUCTO,
                                BP_D_NOVEDADPROYECTO.CODIGOBPIN,
                                BP_D_NOVEDADPROYECTO.CODIGOCCPET,
                                BP_D_NOVEDADPROYECTO.CODIGOCPCDANE,
                                BP_D_NOVEDADPROYECTO.CODIGOUNIDADEJECUTORA,
                                BP_D_NOVEDADPROYECTO.CODIGOFUENTE,
                                BP_D_NOVEDADPROYECTO.CODIGOCCPETREGALIAS,
                                BP_D_NOVEDADPROYECTO.CODIGODETALLESECTORIAL
							ORDER BY
								4';
        ELSE
            MI_STRSQL:=' SELECT   BP_D_NOVEDADPROYECTO.VALORSOLICITADO
                                , BP_D_NOVEDADPROYECTO.RUBROPRESUPUESTAL
                                , CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_FUE NOT IN(0)
                                    THEN BP_D_NOVEDADPROYECTO.FUENTERECURSOS
                                    ELSE PCK_DATOS.FC_CONS_FUENTE END FUENTERECURSOS 
                                , CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_REF NOT IN(0)
                                    THEN BP_D_NOVEDADPROYECTO.REFERENCIA
                                    ELSE PCK_DATOS.FC_CONS_REFERENCIA END REFERENCIA
                                , CASE WHEN PLAN_PRESUPUESTAL.MAN_CEN_CTO NOT IN(0)   
                                    THEN BP_D_NOVEDADPROYECTO.CENTRO_COSTO               
                                    ELSE PCK_DATOS.FC_CONS_CENTRO END CENTROCOSTO
                                , CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_GEN NOT IN(0)   
                                    THEN BP_D_NOVEDADPROYECTO.AUXILIAR               
                                    ELSE PCK_DATOS.FC_CONS_AUXILIAR END AUXILIAR
                                , PLAN_PRESUPUESTAL.NATURALEZA
                                , BP_D_NOVEDADPROYECTO.CODIGO CONSECUTIVO
                                , BP_D_NOVEDADPROYECTO.SECTORRUBRO
                                , BP_D_NOVEDADPROYECTO.PROGRAMARUBRO
                                , BP_D_NOVEDADPROYECTO.SUBPROGRAMARUBRO
                                , BP_D_NOVEDADPROYECTO.CODIGOPRODUCTO
                                , BP_D_NOVEDADPROYECTO.CODIGOBPIN
                                , BP_D_NOVEDADPROYECTO.CODIGOCCPET
                                , BP_D_NOVEDADPROYECTO.CODIGOCPCDANE
                                , BP_D_NOVEDADPROYECTO.CODIGOUNIDADEJECUTORA
                                , BP_D_NOVEDADPROYECTO.CODIGOFUENTE
                                , BP_D_NOVEDADPROYECTO.CODIGOCCPETREGALIAS
                                , BP_D_NOVEDADPROYECTO.CODIGODETALLESECTORIAL
                           FROM BP_D_NOVEDADPROYECTO 
                               INNER JOIN PLAN_PRESUPUESTAL 
                                   ON  BP_D_NOVEDADPROYECTO.COMPANIA          = PLAN_PRESUPUESTAL.COMPANIA
                                   AND BP_D_NOVEDADPROYECTO.ANORUBRO          = PLAN_PRESUPUESTAL.ANO 
                                   AND BP_D_NOVEDADPROYECTO.RUBROPRESUPUESTAL = PLAN_PRESUPUESTAL.CODIGO 
                          WHERE BP_D_NOVEDADPROYECTO.COMPANIA    = '''||UN_COMPANIA||''' 
                            AND BP_D_NOVEDADPROYECTO.TIPOT       = '''||UN_TIPOT||''' 
                            AND BP_D_NOVEDADPROYECTO.CLASET      = '''||UN_CLASET||''' 
                            AND BP_D_NOVEDADPROYECTO.NOVEDAD     = '''||UN_NOVEDAD||''' 
                            AND BP_D_NOVEDADPROYECTO.DEPENDENCIA = '''||UN_DEPENDENCIA||''' ';
        END IF;
        -- 07/09/2018 @jreina Se elimino excepcion de NO_DATA_FOUND
        EXECUTE IMMEDIATE ' SELECT COUNT(1) EXISTE FROM ('||MI_STRSQL||')' INTO MI_EXISTE; 
        IF MI_EXISTE = 0 THEN
            BEGIN      
                BEGIN         
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                    --La solicitud de disponibilidad no tiene detalles
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD   =>SQLCODE
                               ,UN_ERROR_COD =>PCK_ERRORES.ERRR_PPTO_PEDIR_AFECTA_CPTES_2
                               ,UN_TABLAERROR => 'BP_D_NOVEDADPROYECTO'
                              );
            END;
        END IF;
        --Verificar si el proyecto existe en presupuesto
        IF UN_COD_PROYECTO IS NOT NULL THEN
          PCK_PRESUPUESTO2.PR_CREAR_PROYECTO_PPTAL_BP (
            UN_COMPANIA => UN_COMPANIA
          , UN_ANO      => UN_ANO
          , UN_CODIGO   => UN_COD_PROYECTO
          , UN_USUARIO  => UN_USUARIO
          );
        END IF;

        SELECT NVL(OBJETO,' ') || NVL(OBSERVACIONES,' ')
        INTO MI_OBJETO_CDP
        FROM BPNOVEDADPROYECTO 
        WHERE COMPANIA        = UN_COMPANIA
              AND TIPOT       = UN_TIPOT
              AND CLASET      = UN_CLASET
              AND CODIGO      = UN_CMPTE_AFECTADO
              AND DEPENDENCIA = UN_DEPENDENCIA;

        --'crear datos de header
        MI_TABLA:='COMPROBANTE_PPTAL';
        MI_CAMPOS:=  ' VLR_DOCUMENTO      = '||NVL(UN_VLR_DOCUMENTO, 0)||'
                      ,NRO_DOCUMENTO      = ''' || UN_CMPTE_AFECTADO || '''
                      ,DESTINO            = ''I''
                      ,DESCRIPCION        = ''' || MI_OBJETO_CDP || '''
                      ,DEBITO             = '||NVL(UN_DEBITO, 0)||'
                      ,CREDITO            = 0
                      ,CARGO              = '''||UN_CARGO||'''
                      ,DEPENDENCIA        = '''||UN_DEPENDENCIA||''' 
                      ,CONTRACTUAL        = 0
                      ,PAPELES            = 0
                      ,SITUACIONFONDOS    = 0
                      ,REFERENCIA         = ''' || PCK_DATOS.FC_CONS_REFERENCIA || '''
                      ,COD_PROYECTO_PPTAL = '''||NVL(UN_COD_PROYECTO, '')||''' 
                      ,DATE_MODIFIED      = SYSDATE 
                      ,MODIFIED_BY        = '''||UN_USUARIO||''' ';
        MI_CONDICION:='    COMPANIA = '''||UN_COMPANIA||'''
                       AND ANO      = '||UN_ANO|| '
                       AND TIPO     = '''||UN_TIPO_CPTE||'''
                       AND NUMERO   = '||UN_NUMERO_CPTE ;           
        BEGIN 
            BEGIN
                MI_RTA:=PCK_DATOS.FC_ACME(
                                  UN_TABLA     => MI_TABLA
                                 ,UN_ACCION    => 'M'
                                 ,UN_CAMPOS    => MI_CAMPOS
                                 ,UN_CONDICION => MI_CONDICION 
                                 ) ;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE:='UN_NUMERO_CPTE';
                MI_REEMPLAZOS(0).VALOR:=UN_NUMERO_CPTE;
                MI_REEMPLAZOS(1).CLAVE:='UN_TIPO_CPTE';
                MI_REEMPLAZOS(1).VALOR:=UN_TIPO_CPTE;
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PPTO_PEDIR_AFECTA_CPTES_3
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                       );
        END;

        IF NVL(PCK_SYSMAN_UTL.FC_PAR(
                              UN_COMPANIA  => UN_COMPANIA
                             ,UN_NOMBRE    => 'TRAER DETALLE Y TEXTO DE DOCUMENTO AFECTADO'
                             ,UN_MODULO    => PCK_DATOS.FC_MODULOPRESUPUESTO
                             ,UN_FECHA_PAR => SYSDATE), 'SI') = 'SI' THEN
            MI_STRDESCRIPCION:= SUBSTR(UN_DESCRIPCION, 1, 255);
        END IF;
        MI_CONSECUTIVODET:= 1;
        <<RECORREDETALLE>>
        OPEN MI_RSDETALLE FOR MI_STRSQL;
        LOOP
            FETCH MI_RSDETALLE INTO  MI_VALORSOLICITADO
                                    ,MI_RUBROPRESUPUESTAL
                                    ,MI_FUENTERECURSOS
                                    ,MI_REFERENCIA
                                    ,MI_CENTROCOSTO
                                    ,MI_AUXILIAR
                                    ,MI_NATURALEZA
                                    ,MI_CONSECUTIVO
                                    ,MI_SECTORRUBRO
                                    ,MI_PROGRAMARUBRO
                                    ,MI_SUBPROGRAMARUBRO
                                    ,MI_CODIGOPRODUCTO
                                    ,MI_CODIGOBPIN
                                    ,MI_CODIGOCCPET
                                    ,MI_CODIGOCPCDANE
                                    ,MI_CODIGOUNIDADEJECUTORA
                                    ,MI_CODIGOFUENTE
                                    ,MI_CODIGOCCPETREGALIAS
                                    ,MI_CODIGODETALLESECTORIAL;
            EXIT WHEN MI_RSDETALLE%NOTFOUND;
            --CONTROLAR SALDO DE RUBRO ANTES DE INSERTAR
            -- Validando Saldo de rubro presupuestal "
            BEGIN 
                SELECT   
                         PLAN_PRESUPUESTAL.AUXILIAR
                       , PLAN_PRESUPUESTAL.CENTRO_COSTO
                       , PLAN_PRESUPUESTAL.REFERENCIA
                       , PLAN_PRESUPUESTAL.TERCERO
                       , PLAN_PRESUPUESTAL.SUCURSAL
                       , SUM(APROPIACION_DEBITO-APROPIACION_CREDITO)
                         +SUM(SALDO_PLAN_PPTAL.ADICION)
                         +SUM(SALDO_PLAN_PPTAL.REDUCCION)
                         +SUM(TRASLADO_DEBITO-TRASLADO_CREDITO)
                         +SUM(APLAZAM_DEBITO-APLAZAM_CREDITO)
                         -SUM(SALDO_PLAN_PPTAL.DISPONIBILIDAD)  SALDOPARADISPONIBILIDADES
                   INTO  MI_AUXILIAR
                        ,MI_CENTRO_COSTO
                        ,MI_REFERENCIA
                        ,MI_TERCERO
                        ,MI_SUCURSAL
                        ,MI_SALDOPARADISPONIBILIDADES
                   FROM V_PLAN_PRESUPUESTAL PLAN_PRESUPUESTAL 
                       INNER JOIN V_SALDO_PLAN_PPTAL SALDO_PLAN_PPTAL 
                           ON  PLAN_PRESUPUESTAL.COMPANIA = SALDO_PLAN_PPTAL.COMPANIA 
                           AND PLAN_PRESUPUESTAL.ANO      = SALDO_PLAN_PPTAL.ANO 
                           AND PLAN_PRESUPUESTAL.ID       = SALDO_PLAN_PPTAL.ID
                       INNER JOIN PLAN_PRESUPUESTAL PLAN
                           ON  PLAN_PRESUPUESTAL.COMPANIA = PLAN.COMPANIA 
                           AND PLAN_PRESUPUESTAL.ANO      = PLAN.ANO 
                           AND PLAN_PRESUPUESTAL.CODIGO   = PLAN.CODIGO    

                  WHERE PLAN_PRESUPUESTAL.COMPANIA   =  UN_COMPANIA
                    AND PLAN_PRESUPUESTAL.NATURALEZA =  'D'
                    AND SALDO_PLAN_PPTAL.MES         <= EXTRACT (MONTH FROM UN_FECHA) 
                    AND PLAN_PRESUPUESTAL.ANO        =  UN_ANO
                    AND PLAN_PRESUPUESTAL.CODIGO          IN (MI_RUBROPRESUPUESTAL)
                    AND PLAN_PRESUPUESTAL.FUENTE_RECURSO  IN (CASE WHEN PLAN.MAN_AUX_FUE NOT IN (0) THEN  MI_FUENTERECURSOS ELSE PCK_DATOS.FC_CONS_FUENTE END)
                    AND PLAN_PRESUPUESTAL.CENTRO_COSTO  IN (CASE WHEN PLAN.MAN_CEN_CTO NOT IN (0) THEN  MI_CENTROCOSTO ELSE PCK_DATOS.FC_CONS_CENTRO END)
                    AND PLAN_PRESUPUESTAL.REFERENCIA  IN (CASE WHEN PLAN.MAN_AUX_REF NOT IN (0) THEN  MI_REFERENCIA ELSE PCK_DATOS.FC_CONS_REFERENCIA END)
                    AND PLAN_PRESUPUESTAL.AUXILIAR  IN (CASE WHEN PLAN.MAN_AUX_GEN NOT IN (0) THEN  MI_AUXILIAR ELSE PCK_DATOS.FC_CONS_AUXILIAR END)
                   HAVING SUM(APROPIACION_DEBITO-APROPIACION_CREDITO)
                         +SUM(SALDO_PLAN_PPTAL.ADICION)
                         +SUM(SALDO_PLAN_PPTAL.REDUCCION)
                         +SUM(TRASLADO_DEBITO-TRASLADO_CREDITO)
                         +SUM(APLAZAM_DEBITO-APLAZAM_CREDITO)
                         -SUM(SALDO_PLAN_PPTAL.DISPONIBILIDAD) > 0  
                  GROUP BY   PLAN_PRESUPUESTAL.COMPANIA
                           , PLAN_PRESUPUESTAL.ANO
                           , PLAN_PRESUPUESTAL.ID
                           , PLAN_PRESUPUESTAL.NOMBRE
                           , PLAN_PRESUPUESTAL.AUXILIAR
                           , PLAN_PRESUPUESTAL.CENTRO_COSTO
                           , PLAN_PRESUPUESTAL.REFERENCIA
                           , PLAN_PRESUPUESTAL.TERCERO
                           , PLAN_PRESUPUESTAL.SUCURSAL
                           , PLAN_PRESUPUESTAL.CODIGO;
            EXCEPTION WHEN NO_DATA_FOUND THEN 
                MI_MSGERROR:=MI_MSGERROR
                             ||' No se encontro Rubro, no se inserto IMPUTACION de '
                             ||MI_RUBROPRESUPUESTAL
                             ||' Por favor revise. '; 
            END;
            IF NVL(MI_SALDOPARADISPONIBILIDADES, 0) < NVL(MI_VALORSOLICITADO, 0) THEN
                MI_MSGERROR:=MI_MSGERROR
                             ||'El saldo del Rubro: ('
                             ||MI_RUBROPRESUPUESTAL
                             ||') Valor: '
                             ||TO_CHAR(NVL(MI_SALDOPARADISPONIBILIDADES, 0), '999,999,999,999,999.00') 
                             ||'Es inferior al Valor solicitado: '
                             ||TO_CHAR(NVL(MI_VALORSOLICITADO, 0), '999,999,999,999,999,999.00') ||CHR(10)||CHR(13)
                             ||', No se creara IMPUTACION presupuestal por favor revise';
                CONTINUE;
            END IF;
            --Preparando, Insertando Variables"
            MI_CENTROCOSTO:= NVL(MI_CENTRO_COSTO, PCK_DATOS.FC_CONS_CENTRO);
            MI_AUXILIAR:= NVL(MI_AUXILIAR, PCK_DATOS.FC_CONS_AUXILIAR);
            MI_DEBITOC:= NVL(MI_VALORSOLICITADO, 0);
            MI_CREDITOC:= 0;
            --20180920_3607@jreina Se agrega la fuente de recurso, el tipo, la clase y la dependencia para realizar la insercion
            MI_TABLA:='DETALLE_COMPROBANTE_PPTAL';
            MI_CAMPOS:= ' COMPANIA
                         ,ANO
                         ,TIPO_CPTE
                         ,COMPROBANTE
                         ,CONSECUTIVO
                         ,CUENTA
                         ,FECHA
                         ,NATURALEZA
                         ,DESCRIPCION
                         ,VALOR_DEBITO
                         ,VALOR_CREDITO
                         ,TIPO_DOCUMENTO
                         ,NRO_DOCUMENTO
                         ,CENTRO_COSTO
                         ,TERCERO
                         ,SUCURSAL
                         ,AUXILIAR
                         ,FUENTE_RECURSO
                         ,TIPOT
                         ,CLASET
                         ,CMPTE_SOLICI_AFECTADO
                         ,DEPENDENCIA
                         ,CONSECUTIVOPPTO
                         ,CONTRACTUAL
                         ,PAPELES
                         ,PAC_DISPONIBLE
                         ,REFERENCIA
                         ,CONSITUACIONFONDOS
                         ,DATE_CREATED
                         ,CREATED_BY
                         ,FUENTE_CUIPO
                         ,COD_PROD_CUIPO
                         ,CODIGO_CPC
                         ,CODIGO_BPIN
                         ,CODIGO_CCPET
                         ,SECTOR
                         ,PROGRAMA
                         ,SUBPROGRAMA
                         ,CODIGOUNIDADEJE
                         ,CODIGOCCPETREGA
                         ,DETALLE_SECTORIAL';         
            MI_VALORES:=' '''||UN_COMPANIA||'''
                         ,'||UN_ANO||'
                         ,'''||UN_TIPO_CPTE||'''
                         ,'''||UN_NUMERO_CPTE||'''
                         ,'||MI_CONSECUTIVO||'
                         ,'''||MI_RUBROPRESUPUESTAL||'''
                         ,TO_DATE('''||UN_FECHA||''',''DD/MM/YYYY'')
                         ,'''||NVL(MI_NATURALEZA, 'D')||'''
                         ,'''||MI_OBJETO_CDP||'''
                         ,'||MI_DEBITOC||'
                         ,'||MI_CREDITOC||' 
                         , NULL
                         , 0 
                         ,'''||MI_CENTRO_COSTO||'''
                         ,'''||MI_TERCERO||'''
                         ,'''||MI_SUCURSAL||'''
                         ,'''||MI_AUXILIAR||'''
                         ,'''||MI_FUENTERECURSOS||'''
                         ,'''||UN_TIPOT||'''
                         ,'''||UN_CLASET||'''
                         ,'  ||NVL(UN_CMPTE_AFECTADO, 0)||'
                         ,'''||UN_DEPENDENCIA||'''
                         ,'||NVL(MI_CONSECUTIVO, 0)||'
                         ,0
                         ,0
                         ,0
                         ,'''||MI_REFERENCIA||'''
                         ,0
                         ,SYSDATE
                         ,'''||UN_USUARIO||'''
                         ,'''||MI_CODIGOFUENTE||'''
                         ,'''||MI_CODIGOPRODUCTO||'''
                         ,'''||MI_CODIGOCPCDANE||'''
                         ,'''||MI_CODIGOBPIN||'''
                         ,'''||MI_CODIGOCCPET||'''   
                         ,'''||MI_SECTORRUBRO||'''
                         ,'''||MI_PROGRAMARUBRO||'''
                         ,'''||MI_SUBPROGRAMARUBRO||'''
                         ,'''||MI_CODIGOUNIDADEJECUTORA||''' 
                         ,'''||MI_CODIGOCCPETREGALIAS||'''
                         ,'''||MI_CODIGODETALLESECTORIAL||'''';
            --"Insertando Variables"
            BEGIN
                BEGIN 
                    MI_RTA:=PCK_DATOS.FC_ACME(
                                      UN_TABLA   => MI_TABLA
                                     ,UN_ACCION  => 'I'
                                     ,UN_CAMPOS  => MI_CAMPOS
                                     ,UN_VALORES => MI_VALORES); 
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                    MI_REEMPLAZOS(0).CLAVE:='MI_CONSECUTIVO';
                    MI_REEMPLAZOS(0).VALOR:=MI_CONSECUTIVO;
                    MI_REEMPLAZOS(1).CLAVE:='UN_NUMERO_CPTE';
                    MI_REEMPLAZOS(1).VALOR:=UN_NUMERO_CPTE;
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO; 
                END; 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PPTO_PEDIR_AFECTA_CPTES_4
                           ,UN_TABLAERROR => MI_TABLA
                           ,UN_REEMPLAZOS => MI_REEMPLAZOS
                          );
            END;
            IF MI_RTA <= 0 THEN
                BEGIN 
                    MI_REEMPLAZOS(0).CLAVE:='MI_CONSECUTIVO';
                    MI_REEMPLAZOS(0).VALOR:=MI_CONSECUTIVO;
                    MI_REEMPLAZOS(1).CLAVE:='UN_NUMERO_CPTE';
                    MI_REEMPLAZOS(1).VALOR:=UN_NUMERO_CPTE;
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO; 
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PPTO_PEDIR_AFECTA_CPTES_4
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                              );
                END;
            END IF;
            --"Actualizando saldos"
            --"Afectando detalles de banco"
            IF MI_AGRUPARSOLICITUD = 'SI' THEN
                FOR MI_RS IN ( 
                    SELECT   VALORSOLICITADO
                           , NATURALEZA
                           , BP_D_NOVEDADPROYECTO.CODIGO CONSECUTIVO
                      FROM BP_D_NOVEDADPROYECTO 
                          INNER JOIN PLAN_PRESUPUESTAL 
                              ON  BP_D_NOVEDADPROYECTO.COMPANIA          = PLAN_PRESUPUESTAL.COMPANIA
                              AND BP_D_NOVEDADPROYECTO.ANORUBRO          = PLAN_PRESUPUESTAL.ANO 
                              AND BP_D_NOVEDADPROYECTO.RUBROPRESUPUESTAL = PLAN_PRESUPUESTAL.CODIGO  
                     WHERE BP_D_NOVEDADPROYECTO.COMPANIA    = UN_COMPANIA 
                       AND BP_D_NOVEDADPROYECTO.TIPOT       = UN_TIPOT 
                       AND BP_D_NOVEDADPROYECTO.CLASET      = UN_CLASET 
                       AND BP_D_NOVEDADPROYECTO.NOVEDAD     = UN_NOVEDAD 
                       AND BP_D_NOVEDADPROYECTO.DEPENDENCIA = UN_DEPENDENCIA)
                LOOP
                    MI_TABLA:='BP_D_NOVEDADPROYECTO';
                    MI_CAMPOS:= ' VALORAFECTADO = '||NVL(MI_RS.VALORSOLICITADO, 0)||'
                                 ,DATE_MODIFIED = SYSDATE
                                 ,MODIFIED_BY   = '''||UN_USUARIO||''' ';
                    MI_CONDICION:='    COMPANIA    = '''||UN_COMPANIA||''' 
                                   AND TIPOT       = '''||UN_TIPOT||''' 
                                   AND CLASET      = '''||UN_CLASET||''' 
                                   AND NOVEDAD     = '''||UN_NOVEDAD||''' 
                                   AND DEPENDENCIA ='''||UN_DEPENDENCIA||'''
                                   AND CODIGO      ='||MI_RS.CONSECUTIVO;
                    BEGIN 
                        BEGIN
                            MI_RTA:=PCK_DATOS.FC_ACME(
                                              UN_TABLA     => MI_TABLA
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION 
                                             ) ;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                            MI_REEMPLAZOS(0).CLAVE:='UN_NOVEDAD';
                            MI_REEMPLAZOS(0).CLAVE:=UN_NOVEDAD;
                            MI_REEMPLAZOS(1).CLAVE:='UN_TIPOT';
                            MI_REEMPLAZOS(1).CLAVE:=UN_TIPOT;
                            MI_REEMPLAZOS(2).CLAVE:='UN_DEPENDENCIA';
                            MI_REEMPLAZOS(2).CLAVE:=UN_DEPENDENCIA;
                            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                         PCK_ERR_MSG.RAISE_WITH_MSG(
                                     UN_EXC_COD    => SQLCODE
                                    ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PPTO_PEDIR_AFECTA_CPTES_5
                                    ,UN_TABLAERROR => MI_TABLA
                                    ,UN_REEMPLAZOS => MI_REEMPLAZOS
                                    );
                    END;
                END LOOP;
                MI_TABLA:= 'BPNOVEDADPROYECTO';
                 --,CONCOMPROBANTEPPTAL = -1
                MI_CAMPOS:= ' AFECTADO            = -1
                             ,DATE_MODIFIED       = SYSDATE
                             ,MODIFIED_BY         = '''||UN_USUARIO||''' ';
                MI_CONDICION:='    COMPANIA    = '''|| UN_COMPANIA ||'''
                               AND TIPOT       = '''|| UN_TIPOT||''' 
                               AND CLASET      = '''||UN_CLASET||''' 
                               AND CODIGO      = '''||UN_NOVEDAD||''' 
                               AND DEPENDENCIA = '''||UN_DEPENDENCIA||''' ';
                BEGIN 
                    BEGIN
                        MI_RTA:=PCK_DATOS.FC_ACME(
                                          UN_TABLA     => MI_TABLA
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS
                                         ,UN_CONDICION => MI_CONDICION 
                                         ) ;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                        MI_REEMPLAZOS(0).CLAVE:='UN_NOVEDAD';
                        MI_REEMPLAZOS(0).CLAVE:=UN_NOVEDAD;
                        MI_REEMPLAZOS(1).CLAVE:='UN_TIPOT';
                        MI_REEMPLAZOS(1).CLAVE:=UN_TIPOT;
                        MI_REEMPLAZOS(2).CLAVE:='UN_DEPENDENCIA';
                        MI_REEMPLAZOS(2).CLAVE:=UN_DEPENDENCIA;
                        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PPTO_PEDIR_AFECTA_CPTES_6
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                               );
                END;
            ELSE
                MI_TABLA:='BP_D_NOVEDADPROYECTO';
                MI_CAMPOS:=' VALORAFECTADO = '||MI_DEBITOC||'
                            ,DATE_MODIFIED = SYSDATE
                            ,MODIFIED_BY   = '''||UN_USUARIO||''' '; 
                MI_CONDICION:='    COMPANIA    = '''||UN_COMPANIA||''' 
                               AND TIPOT       = '''||UN_TIPOT||''' 
                               AND CLASET      = '''||UN_CLASET||''' 
                               AND NOVEDAD     = '''||UN_NOVEDAD||''' 
                               AND DEPENDENCIA = '''||UN_DEPENDENCIA||''' 
                               AND CODIGO      = '||MI_CONSECUTIVO;
                BEGIN 
                    BEGIN
                        MI_RTA:=PCK_DATOS.FC_ACME(
                                          UN_TABLA     => MI_TABLA
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS
                                         ,UN_CONDICION => MI_CONDICION 
                                         );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                        MI_REEMPLAZOS(0).CLAVE:='UN_NOVEDAD';
                        MI_REEMPLAZOS(0).CLAVE:=UN_NOVEDAD;
                        MI_REEMPLAZOS(1).CLAVE:='UN_TIPOT';
                        MI_REEMPLAZOS(1).CLAVE:=UN_TIPOT;
                        MI_REEMPLAZOS(2).CLAVE:='UN_DEPENDENCIA';
                        MI_REEMPLAZOS(2).CLAVE:=UN_DEPENDENCIA;
                        MI_REEMPLAZOS(3).CLAVE:='MI_CONSECUTIVO';
                        MI_REEMPLAZOS(3).CLAVE:=MI_CONSECUTIVO;
                        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                                 UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PPTO_PEDIR_AFECTA_CPTES_7
                                ,UN_TABLAERROR => MI_TABLA
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS
                                );
                END;
                MI_TABLA:='BPNOVEDADPROYECTO'; 
                -- ,CONCOMPROBANTEPPTAL = -1
                MI_CAMPOS:=' AFECTADO            = -1
                            ,DATE_MODIFIED       = SYSDATE
                            ,MODIFIED_BY         = '''||UN_USUARIO||''' ';
                MI_CONDICION:= '    COMPANIA    = '''||UN_COMPANIA||''' 
                                AND TIPOT       = '''||UN_TIPOT||''' 
                                AND CLASET      = '''||UN_CLASET||''' 
                                AND CODIGO      = '''||UN_NOVEDAD||''' 
                                AND DEPENDENCIA = '''||UN_DEPENDENCIA||''' ';
                BEGIN 
                    BEGIN
                        MI_RTA:=PCK_DATOS.FC_ACME(
                                          UN_TABLA     => MI_TABLA
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS
                                         ,UN_CONDICION => MI_CONDICION 
                                         ) ;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                        MI_REEMPLAZOS(0).CLAVE:='UN_NOVEDAD';
                        MI_REEMPLAZOS(0).CLAVE:=UN_NOVEDAD;
                        MI_REEMPLAZOS(1).CLAVE:='UN_TIPOT';
                        MI_REEMPLAZOS(1).CLAVE:=UN_TIPOT;
                        MI_REEMPLAZOS(2).CLAVE:='UN_DEPENDENCIA';
                        MI_REEMPLAZOS(2).CLAVE:=UN_DEPENDENCIA;
                        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                     PCK_ERR_MSG.RAISE_WITH_MSG(
                                 UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTABILIDAD_COPIANIIF
                                ,UN_TABLAERROR => MI_TABLA
                                ,UN_REEMPLAZOS => MI_REEMPLAZOS
                                );
                END;
                MI_CONSECUTIVODET:= MI_CONSECUTIVODET + 1;
            END IF;
        END LOOP RECORREDETALLE;

        SELECT NVL(SUM(VALOR_DEBITO),0) SUMA_DEBITO,
                   NVL(SUM(VALOR_CREDITO),0) SUMA_CREDITO
            INTO   MI_SUMADEBITO,
                   MI_SUMACREDITO
                FROM DETALLE_COMPROBANTE_PPTAL
                WHERE COMPANIA  = UN_COMPANIA
                AND ANO         = UN_ANO
                AND TIPO_CPTE   = UN_TIPO_CPTE
                AND COMPROBANTE = UN_NUMERO_CPTE;


            MI_CAMPOS :=  'VLR_DOCUMENTO =   '||MI_SUMADEBITO||', 
                           DESCRIPCION   = '''||MI_OBJETO_CDP||''',
                           DATE_MODIFIED = SYSDATE, 
                           MODIFIED_BY   = ''' || UN_USUARIO  ||'''';                          
            MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA ||''' 
                             AND ANO     = '   || UN_ANO      || ' 
                             AND TIPO    = ''' || UN_TIPO_CPTE     ||'''
                             AND NUMERO  = '   || UN_NUMERO_CPTE;
          BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'COMPROBANTE_PPTAL',
                                                      UN_ACCION     => 'M',
                                                      UN_CAMPOS     => MI_CAMPOS,
                                                      UN_CONDICION  => MI_CONDICION
                                                      );

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                END;      
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                   UN_ERROR_COD  => PCK_ERRORES.ERR_ACTVALORDOCUMENTO,
                                                   UN_TABLAERROR => 'COMPROBANTE_PPTAL'
                                                   );        
            END;
        --Afectando Header de banco

        /*MI_TABLA:='COMPROBANTE_PPTALAFECTADOS';
        MI_CAMPOS:= 'COMPANIA,
                     ANO,
                     TIPO_CPTE,
                     COMPROBANTE,
                     ANO_AFECT,
                     TIPO_CPTE_AFECT,
                     COMPROBANTE_AFECT,
                     CREATED_BY,
                     DATE_CREATED'; 

        MI_VALORES:=' ''' ||  UN_COMPANIA         ||''',
                      '   ||  UN_ANO              || ',
                      ''' ||  UN_TIPO_CPTE        ||''',
                      '   ||  UN_NUMERO_CPTE      ||',
                      '   ||  UN_ANO              || ',
                      ''' ||  UN_TIPO_CPTE_AFECT  ||''',
                      ''' ||  UN_CMPTE_AFECTADO   ||''',
                      ''' ||  UN_USUARIO          ||''',
                      SYSDATE
                      ';         
        BEGIN 
            BEGIN
                MI_RTA:=PCK_DATOS.FC_ACME(
                                  UN_TABLA     => MI_TABLA
                                 ,UN_ACCION    => 'I'
                                 ,UN_CAMPOS    => MI_CAMPOS
                                 ,UN_CONDICION => MI_CONDICION 
                                 ) ;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                MI_REEMPLAZOS(0).CLAVE:='UN_NUMERO_CPTE';
                MI_REEMPLAZOS(0).VALOR:=UN_NUMERO_CPTE;
                MI_REEMPLAZOS(1).CLAVE:='UN_TIPO_CPTE';
                MI_REEMPLAZOS(1).VALOR:=UN_TIPO_CPTE;
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PPTO_PEDIR_AFECTA_CPTES_3
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                       );
        END;*/
    ELSE
        BEGIN 
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   =>SQLCODE
                       ,UN_ERROR_COD =>PCK_ERRORES.ERRR_PPTO_PEDIR_AFECTA_CPTES_8
                       );
        END;
    END IF;
  END IF;
    RETURN MI_MSGERROR;
    -- DoCmd.Close acForm, "PedirSolicitudAAfectar"
END FC_AFECTARCPTES;

--12 crearProyectoPptalBP
  PROCEDURE PR_CREAR_PROYECTO_PPTAL_BP
  /*
    NAME              : PR_CREAR_PROYECTO_PPTAL_BP
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JUAN CARLOS RODRÍGUEZ AMÉZQUITA
    DATE MIGRADOR     : 24/11/2017
    TIME              : 04:29 PM
    DESCRIPTION       : Permite crear un proyecto presupuestal a partir de un 
                        proyecto existente en Banco de Proyectos.

    PARAMETROS DE ENTRADA: 
      UN_COMPANIA:          Código de la compañía
      UN_ANO:               Año del comprobante presupuestal
      UN_CODIGO:            Código del proyecto presupuestal a asociar con el comprobante presupuestal
      UN_USUARIO:           Usuario que ingreso a la aplicacion

    @NAME: crearProyectoPresupuestalDesdeBancoProyectos
    @METHOD: put
  */
  (
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA
  , UN_ANO                IN PCK_SUBTIPOS.TI_ANIO
  , UN_CODIGO             IN PROYECTOS_PPTAL.CODIGO%TYPE
  , UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO
  ) AS
    MI_EXISTE                PCK_SUBTIPOS.TI_LOGICO;
  BEGIN
    BEGIN
      -- Verifica si está creado el proyecto presupuestal
      SELECT 1
        INTO MI_EXISTE
        FROM PROYECTOS_PPTAL
       WHERE COMPANIA = UN_COMPANIA
         AND ANO = UN_ANO
         AND CODIGO = UN_CODIGO;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        BEGIN
          DECLARE
            MI_CODIGOBPIM                             PROYECTOS.CODIGOBPIM%TYPE;
            MI_NOMBREPROYECTO                         PROYECTOS.NOMBREPROYECTO%TYPE;
            MI_VALORTOTAL                             PROYECTOS.VALORTOTAL%TYPE;
            MI_VIGENCIAINICIO                         PROYECTOS.VIGENCIAINICIO%TYPE;
            MI_CAMPOS                                 PCK_SUBTIPOS.TI_CAMPOS;
            MI_VALORES                                PCK_SUBTIPOS.TI_VALORES;
            MI_PCKDATOS                               PCK_SUBTIPOS.TI_RTA_ACME;
          BEGIN
            -- Trae los datos del proyecto en Banco de Proyectos
            SELECT CODIGOBPIM, NOMBREPROYECTO, VALORTOTAL, VIGENCIAINICIO
              INTO MI_CODIGOBPIM, MI_NOMBREPROYECTO, MI_VALORTOTAL, MI_VIGENCIAINICIO
              FROM PROYECTOS
             WHERE COMPANIA = UN_COMPANIA
               AND CODIGO = UN_CODIGO;
            -- Creación del proyecto presupuestal
            MI_CAMPOS := ' COMPANIA
                        , CODIGO
                        , ANO
                        , NOMBRE
                        , ACTIVO
                        , VLR_PROYECTO
                        , CODIGOBPIM
                        , CREATED_BY
                        , DATE_CREATED';
            MI_VALORES := '''' || UN_COMPANIA || '''
                        , ''' || UN_CODIGO || '''
                        , ' || UN_ANO || '
                        , ''' || MI_NOMBREPROYECTO || '''
                        , -1
                        , ' || MI_VALORTOTAL || '
                        , ''' || MI_CODIGOBPIM || '''
                        , ''' || UN_USUARIO || '''
                        , SYSDATE';
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                UN_TABLA   => 'PROYECTOS_PPTAL'
              , UN_ACCION  => 'I'
              , UN_CAMPOS  => MI_CAMPOS
              , UN_VALORES => MI_VALORES);
          EXCEPTION 
            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END;
        EXCEPTION
          WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE
            , UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_CREAR_PROY_PPTAL_BP
            , UN_TABLAERROR => 'PROYECTOS_PPTAL'
            );
        END;
    END;
    --IF MI_EXISTE = 0 THEN
      --null;
    --END IF;
  END PR_CREAR_PROYECTO_PPTAL_BP;

FUNCTION FC_CLASEPPTAL(
     UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_TIPO_CPTE       IN DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE%TYPE
)RETURN VARCHAR2
AS
  MI_CLASE TIPO_COMPROBPP.CLASE%TYPE;
BEGIN
  SELECT CLASE
  INTO MI_CLASE
  FROM TIPO_COMPROBPP
  WHERE COMPANIA = UN_COMPANIA
    AND CODIGO   = UN_TIPO_CPTE;
  RETURN MI_CLASE;
  EXCEPTION WHEN NO_DATA_FOUND THEN
      RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;

END FC_CLASEPPTAL ;

FUNCTION FC_CLASECNTPRES(
     UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_TIPO_CPTE       IN DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE%TYPE
)RETURN VARCHAR2
AS
  MI_CLASE TIPO_COMPROBPP.CLASE%TYPE;
  MI_CLASEAFECTAR CLASECNTPRES.CLASEAFECTAR%TYPE;
BEGIN
  MI_CLASE := PCK_PRESUPUESTO2.FC_CLASEPPTAL(UN_COMPANIA  => UN_COMPANIA
                                            ,UN_TIPO_CPTE => UN_TIPO_CPTE); 
  BEGIN
    SELECT CLASEAFECTAR
    INTO MI_CLASEAFECTAR
    FROM CLASECNTPRES
    WHERE CODIGO   = MI_CLASE;
    RETURN MI_CLASEAFECTAR;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    RETURN '';
  END;
END FC_CLASECNTPRES;

PROCEDURE PR_SALDODISPONIBLE
  (
  UN_TIPO_CPTE                IN DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE%TYPE,  
  UN_COMPANIA                 IN PCK_SUBTIPOS.TI_COMPANIA, 
  UN_ANIO                     IN PCK_SUBTIPOS.TI_ANIO,   
  UN_CODIGO                   IN PCK_SUBTIPOS.TI_CODIGOPPTAL,
  UN_TERCERO                  IN PCK_SUBTIPOS.TI_TERCERO, 
  UN_SUCURSAL                 IN PCK_SUBTIPOS.TI_SUCURSAL,
  UN_AUXILIAR                 IN PCK_SUBTIPOS.TI_AUXILIAR,
  UN_CENTRO                   IN PCK_SUBTIPOS.TI_CENTRO_COSTO, 
  UN_REFERENCIA               IN PCK_SUBTIPOS.TI_REFERENCIA, 
  UN_FUENTERECURSO            IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS, 
  UN_DEBITO                   IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0,
  UN_CREDITO                  IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0,
  UN_TIPO_CPTE_ANT            IN DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE%TYPE,
  UN_COMPANIA_ANT             IN PCK_SUBTIPOS.TI_COMPANIA, 
  UN_ANIO_ANT                 IN PCK_SUBTIPOS.TI_ANIO,   
  UN_CODIGO_ANT               IN PCK_SUBTIPOS.TI_CODIGOPPTAL,
  UN_TERCERO_ANT              IN PCK_SUBTIPOS.TI_TERCERO, 
  UN_SUCURSAL_ANT             IN PCK_SUBTIPOS.TI_SUCURSAL,
  UN_AUXILIAR_ANT             IN PCK_SUBTIPOS.TI_AUXILIAR,
  UN_CENTRO_ANT               IN PCK_SUBTIPOS.TI_CENTRO_COSTO, 
  UN_REFERENCIA_ANT           IN PCK_SUBTIPOS.TI_REFERENCIA, 
  UN_FUENTERECURSO_ANT        IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS,
  UN_DEBITO_ANT                IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0,
  UN_CREDITO_ANT               IN PCK_SUBTIPOS.TI_DOBLE DEFAULT 0
  )
  AS
    MI_CLASE VARCHAR2(10);
    MI_CLASE_ANT VARCHAR2(10);
    MI_SALDO NUMBER;
    MI_RUBRO_ANT        PCK_PRESUPUESTO1.TYP_RUBRO_AUX;
    MI_RUBRO_NUE        PCK_PRESUPUESTO1.TYP_RUBRO_AUX;
  -- ACTUALIZADO  POR HPV EN OCT 18 DE 2018
  -- CUANDO LA CUENTA NO REQUIERE AUXILIARES NO DEBE EVALUAR SALDO CUANDO NO HAY CAMBIO DE TERCEROS EN LA ACTUALIZACION 

  BEGIN

      MI_RUBRO_NUE := PCK_PRESUPUESTO1.FC_VERIFICAR_INDICADORES_PPTO(UN_COMPANIA      => UN_COMPANIA,
                                                      UN_ANIO          => UN_ANIO,
                                                      UN_MES           => 1,
                                                      UN_CODIGO        => UN_CODIGO,
                                                      UN_CENTRO        => UN_CENTRO,
                                                      UN_TERCERO       => UN_TERCERO,
                                                      UN_SUCURSAL      => UN_SUCURSAL,
                                                      UN_AUXILIAR      => UN_AUXILIAR,
                                                      UN_REFERENCIA    => UN_REFERENCIA,
                                                      UN_FUENTERECURSO => UN_FUENTERECURSO); 
    MI_RUBRO_ANT := PCK_PRESUPUESTO1.FC_VERIFICAR_INDICADORES_PPTO(UN_COMPANIA      => UN_COMPANIA_ANT,
                                                      UN_ANIO          => UN_ANIO_ANT,
                                                      UN_MES           => 1,
                                                      UN_CODIGO        => UN_CODIGO_ANT,
                                                      UN_CENTRO        => UN_CENTRO_ANT,
                                                      UN_TERCERO       => UN_TERCERO_ANT,
                                                      UN_SUCURSAL      => UN_SUCURSAL_ANT,
                                                      UN_AUXILIAR      => UN_AUXILIAR_ANT,
                                                      UN_REFERENCIA    => UN_REFERENCIA_ANT,
                                                      UN_FUENTERECURSO => UN_FUENTERECURSO_ANT); 

    MI_SALDO:=0;
    MI_CLASE     := PCK_PRESUPUESTO2.FC_CLASEPPTAL(UN_COMPANIA  => UN_COMPANIA
                                                  ,UN_TIPO_CPTE => UN_TIPO_CPTE); 
    MI_CLASE_ANT := PCK_PRESUPUESTO2.FC_CLASEPPTAL(UN_COMPANIA  => UN_COMPANIA_ANT
                                                  ,UN_TIPO_CPTE => UN_TIPO_CPTE_ANT); 

    IF  UN_TIPO_CPTE     = UN_TIPO_CPTE_ANT
	  AND MI_CLASE         = MI_CLASE_ANT
    AND UN_COMPANIA      = UN_COMPANIA_ANT
    AND UN_ANIO          = UN_ANIO_ANT
    AND UN_CODIGO        = UN_CODIGO_ANT
    AND MI_RUBRO_NUE.MI_TERCERO       = MI_RUBRO_ANT.MI_TERCERO
    AND MI_RUBRO_NUE.MI_SUCURSAL      = MI_RUBRO_ANT.MI_SUCURSAL
    AND MI_RUBRO_NUE.MI_AUXILIAR      = MI_RUBRO_ANT.MI_AUXILIAR
    AND MI_RUBRO_NUE.MI_CENTROCOSTO   = MI_RUBRO_ANT.MI_CENTROCOSTO
    AND MI_RUBRO_NUE.MI_REFERENCIA    = MI_RUBRO_ANT.MI_REFERENCIA
    AND MI_RUBRO_NUE.MI_FUENTERECURSO = MI_RUBRO_ANT.MI_FUENTERECURSO
    AND (UN_DEBITO        <> UN_DEBITO_ANT
      OR UN_CREDITO       <> UN_CREDITO_ANT) THEN
      MI_SALDO:=PCK_PRESUPUESTO2.FC_SALDODISPONIBLE(UN_CLASE         => MI_CLASE,
                                                    UN_COMPANIA      => UN_COMPANIA,
                                                    UN_ANIO          => UN_ANIO,
                                                    UN_CODIGO        => UN_CODIGO,
                                                    UN_TERCERO       => MI_RUBRO_NUE.MI_TERCERO,
                                                    UN_SUCURSAL      => MI_RUBRO_NUE.MI_SUCURSAL,
                                                    UN_AUXILIAR      => MI_RUBRO_NUE.MI_AUXILIAR ,
                                                    UN_CENTRO        => MI_RUBRO_NUE.MI_CENTROCOSTO ,
                                                    UN_REFERENCIA    => MI_RUBRO_NUE.MI_REFERENCIA,
                                                    UN_FUENTERECURSO => MI_RUBRO_NUE.MI_FUENTERECURSO,
                                                    UN_DEBITOANT     => UN_DEBITO_ANT,
                                                    UN_CREDITOANT    => UN_CREDITO_ANT,
                                                    UN_DEBITO        => UN_DEBITO,
                                                    UN_CREDITO       => UN_CREDITO);         
    ELSIF  UN_TIPO_CPTE     <> UN_TIPO_CPTE_ANT
        OR UN_COMPANIA      <> UN_COMPANIA_ANT
        OR UN_ANIO          <> UN_ANIO_ANT
        OR UN_CODIGO        <> UN_CODIGO_ANT
	    OR MI_RUBRO_NUE.MI_TERCERO       <> MI_RUBRO_ANT.MI_TERCERO
        OR MI_RUBRO_NUE.MI_SUCURSAL      <> MI_RUBRO_ANT.MI_SUCURSAL
        OR MI_RUBRO_NUE.MI_AUXILIAR      <> MI_RUBRO_ANT.MI_AUXILIAR
        OR MI_RUBRO_NUE.MI_CENTROCOSTO   <> MI_RUBRO_ANT.MI_CENTROCOSTO
        OR MI_RUBRO_NUE.MI_REFERENCIA    <> MI_RUBRO_ANT.MI_REFERENCIA
        OR MI_RUBRO_NUE.MI_FUENTERECURSO <> MI_RUBRO_ANT.MI_FUENTERECURSO
        OR UN_DEBITO        <> UN_DEBITO_ANT
        OR UN_CREDITO       <> UN_CREDITO_ANT
        OR MI_CLASE         <> MI_CLASE_ANT
     THEN

      MI_SALDO:=PCK_PRESUPUESTO2.FC_SALDODISPONIBLE(UN_CLASE         => MI_CLASE,
                                                    UN_COMPANIA      => UN_COMPANIA_ANT,
                                                    UN_ANIO          => UN_ANIO_ANT,
                                                    UN_CODIGO        => UN_CODIGO_ANT,
                                                    UN_TERCERO       => MI_RUBRO_ANT.MI_TERCERO,
                                                    UN_SUCURSAL      => MI_RUBRO_ANT.MI_SUCURSAL,
                                                    UN_AUXILIAR      => MI_RUBRO_ANT.MI_AUXILIAR ,
                                                    UN_CENTRO        => MI_RUBRO_ANT.MI_CENTROCOSTO ,
                                                    UN_REFERENCIA    => MI_RUBRO_ANT.MI_REFERENCIA,
                                                    UN_FUENTERECURSO => MI_RUBRO_ANT.MI_FUENTERECURSO,
                                                    UN_DEBITOANT     => UN_DEBITO_ANT,
                                                    UN_CREDITOANT    => UN_CREDITO_ANT,
                                                    UN_DEBITO        => 0,
                                                    UN_CREDITO       => 0);   

	  MI_SALDO:=PCK_PRESUPUESTO2.FC_SALDODISPONIBLE(UN_CLASE         => MI_CLASE_ANT,
                                                    UN_COMPANIA      => UN_COMPANIA,
                                                    UN_ANIO          => UN_ANIO,
                                                    UN_CODIGO        => UN_CODIGO,
                                                    UN_TERCERO       => MI_RUBRO_NUE.MI_TERCERO,
                                                    UN_SUCURSAL      => MI_RUBRO_NUE.MI_SUCURSAL,
                                                    UN_AUXILIAR      => MI_RUBRO_NUE.MI_AUXILIAR ,
                                                    UN_CENTRO        => MI_RUBRO_NUE.MI_CENTROCOSTO ,
                                                    UN_REFERENCIA    => MI_RUBRO_NUE.MI_REFERENCIA,
                                                    UN_FUENTERECURSO => MI_RUBRO_NUE.MI_FUENTERECURSO,
                                                    UN_DEBITOANT     => 0,
                                                    UN_CREDITOANT    => 0,
                                                    UN_DEBITO        => UN_DEBITO,
                                                    UN_CREDITO       => UN_CREDITO); 	

    END IF;
  END PR_SALDODISPONIBLE;

PROCEDURE PR_AFECTARCPTESCAQ
 /*
    NAME              : PR_AFECTARCPTESCAQ  --> bancoproyectoscaqueta
    AUTHORS           : SYSMAN SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 04/09/2017
    TIME              : 04:00 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    MODIFICATIONS     : 

    @NAME: afectarCptesCaqueta
    @METHOD: POST
  */
(
 UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
 UN_ANO             IN PCK_SUBTIPOS.TI_ANIO, 
 UN_COMPR_AFECT     IN DETALLE_COMPROBANTE_PPTAL.COMPROBANTE%TYPE,
 UN_NUMERO          IN DETALLE_COMPROBANTE_PPTAL.COMPROBANTE%TYPE,
 UN_FECHA           IN DATE,
 UN_TIPO            IN DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE%TYPE,
 UN_TIPO_AFECT      IN DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE%TYPE,
 UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_CODIGO             PLAN_PRESUPUESTAL.CODIGO%TYPE;
  MI_CENTRO             PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MI_REFERENCIA         PCK_SUBTIPOS.TI_REFERENCIA;
  MI_NATURALEZA         PLAN_PRESUPUESTAL.NATURALEZA%TYPE;
  MI_CONSECUTIVO        DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO%TYPE;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_SUMADEBITO         DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO%TYPE;
  MI_SUMACREDITO        DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO%TYPE;
BEGIN

    FOR RS IN (SELECT * 
                FROM SOLICITUDDISPONIBILIDAD 
                WHERE COMPANIA = UN_COMPANIA
                  AND ANO      = UN_ANO 
                  AND NUMERO   = UN_COMPR_AFECT
                  AND TIPO_SOLICITUD = CASE UN_TIPO_AFECT  WHEN 'SCD' THEN 1 
                                                           WHEN 'ADR' THEN 2
                                                           WHEN 'DMT' THEN 3 
                                       END)
    LOOP
      MI_CONSECUTIVO := 1;
      FOR RS_DET IN (SELECT FUENTE, 
                      RUBRO, 
                      CENTRO_COSTO,
                      REFERENCIA,
                      AUXILIAR,
                      SUM(D_SOLICITUDDISPONIBILIDAD.VALOR)  VALOR, 
                      SUM(D_SOLICITUDDISPONIBILIDAD.IMPUESTO) IMPUESTO,
                      SUM(D_SOLICITUDDISPONIBILIDAD.VALOR_BASE) VALOR_BASE,
                      SECTOR, PROGRAMA, SUBPROGRAMA, CODIGOBPIN, CODIGOPRODUCTO, 
                      CODIGOCCPET, CODIGOCPCDANE, CODIGOUNIDADEJE, CODIGOFUENTE, 
                      CODIGOCCPETREGA, POLITICA_PUBLICA, DETALLE_SECTORIAL
                      FROM D_SOLICITUDDISPONIBILIDAD
                      WHERE COMPANIA  = UN_COMPANIA
                        AND ANO       =  UN_ANO
                        AND NUMERO    =  UN_COMPR_AFECT
                      GROUP BY FUENTE, RUBRO,CENTRO_COSTO,
                      REFERENCIA,AUXILIAR,SECTOR, PROGRAMA, SUBPROGRAMA, CODIGOBPIN, CODIGOPRODUCTO, 
                      CODIGOCCPET, CODIGOCPCDANE, CODIGOUNIDADEJE, CODIGOFUENTE, 
                      CODIGOCCPETREGA, POLITICA_PUBLICA, DETALLE_SECTORIAL)
      LOOP
        SELECT CODIGO,
          CENTRO_COSTO,
          REFERENCIA,
          NATURALEZA
        INTO MI_CODIGO,
          MI_CENTRO,
          MI_REFERENCIA,
          MI_NATURALEZA
        FROM V_PLAN_PRESUPUESTAL
        WHERE COMPANIA      = UN_COMPANIA
        AND ANO             = UN_ANO
        AND CODIGO          = RS_DET.RUBRO
        AND FUENTE_RECURSO  = RS_DET.FUENTE
        AND CENTRO_COSTO    = RS_DET.CENTRO_COSTO
        AND REFERENCIA      = RS_DET.REFERENCIA
        AND AUXILIAR        = RS_DET.AUXILIAR;

         MI_CAMPOS:= 'COMPANIA
                     ,ANO
                     ,TIPO_CPTE
                     ,COMPROBANTE
                     ,CONSECUTIVO
                     ,CUENTA
                     ,ID
                     ,FECHA
                     ,NATURALEZA
                     ,DESCRIPCION
                     ,VALOR_DEBITO
                     ,VALOR_CREDITO
                     ,TIPO_DOCUMENTO
                     ,NRO_DOCUMENTO
                     ,FUENTE_RECURSO
                     ,CENTRO_COSTO
                     ,TERCERO
                     ,SUCURSAL
                     ,AUXILIAR
                     ,REFERENCIA
                     ,TIPO_SOLICITUD
                     ,NUMERO_SOLICITUD
                     ,CONSECUTIVOPPTO
                     ,CONTRACTUAL
                     ,PAPELES
                     ,PAC_DISPONIBLE 
                     ,CONSITUACIONFONDOS
                     ,VALOR_BASE
                     ,IMPUESTO
                     ,DATE_CREATED
                     ,CREATED_BY
                     ,SECTOR
                     ,PROGRAMA
                     ,SUBPROGRAMA
                     ,CODIGO_BPIN
                     ,COD_PROD_CUIPO
                     ,CODIGO_CCPET
                     ,CODIGO_CPC
                     ,CODIGOUNIDADEJE
                     ,FUENTE_CUIPO
                     ,CODIGOCCPETREGA
                     ,POLITICA_PUBLICA
                     ,DETALLE_SECTORIAL'; 

          MI_VALORES:=' '''  ||UN_COMPANIA||'''
                         ,'  ||UN_ANO||'
                         ,'''||UN_TIPO||'''
                         ,'''||UN_NUMERO||'''
                         ,'  ||MI_CONSECUTIVO||'
                         ,'''||RS_DET.RUBRO||'''
                         ,'''||MI_CODIGO||'''
                         ,TO_DATE('''||UN_FECHA||''',''DD/MM/YYYY HH24:MI:SS'')
                         ,'''||NVL(MI_NATURALEZA, 'D')||'''
                         ,'''||RS.OBJETO||'''
                         ,'  ||RS_DET.VALOR||'
                         ,0 
                         , NULL
                         , 0 
                         ,'''||RS_DET.FUENTE||'''
                         ,'''||MI_CENTRO||'''
                         ,'''||PCK_DATOS.CONS_TERCERO||'''
                         ,'''||PCK_DATOS.CONS_SUCURSAL||'''
                         ,'''||RS_DET.AUXILIAR||'''
                         ,'''||MI_REFERENCIA||'''
                         ,'  || CASE UN_TIPO_AFECT WHEN 'SCD' THEN 1 WHEN 'SAD' THEN 2 WHEN 'SRE' THEN 3 END ||'
                         ,'  ||UN_COMPR_AFECT||'
                         ,'  ||MI_CONSECUTIVO||'
                         ,0
                         ,0
                         ,0                     
                         ,0
                         ,'''||RS_DET.VALOR_BASE||'''
                         ,'''||RS_DET.IMPUESTO||'''                         
                         ,SYSDATE                         
                         ,'''||UN_USUARIO||'''
                         ,'''||RS_DET.SECTOR||'''
                         ,'''||RS_DET.PROGRAMA||'''
                         ,'''||RS_DET.SUBPROGRAMA||'''
                         ,'''||RS_DET.CODIGOBPIN||'''
                         ,'''||RS_DET.CODIGOPRODUCTO||'''
                         ,'''||RS_DET.CODIGOCCPET||'''
                         ,'''||RS_DET.CODIGOCPCDANE||'''
                         ,'''||RS_DET.CODIGOUNIDADEJE||'''
                         ,'''||RS_DET.CODIGOFUENTE||'''
                         ,'''||RS_DET.CODIGOCCPETREGA||'''
                         ,'''||RS_DET.POLITICA_PUBLICA||'''
                         ,'''||RS_DET.DETALLE_SECTORIAL||'''';
         BEGIN
                BEGIN 
                    PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(
                                      UN_TABLA   => 'DETALLE_COMPROBANTE_PPTAL'
                                     ,UN_ACCION  => 'I'
                                     ,UN_CAMPOS  => MI_CAMPOS
                                     ,UN_VALORES => MI_VALORES); 
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 

                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO; 
                END; 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD    => SQLCODE
                           ,UN_ERROR_COD  => PCK_ERRORES.ERR_INSDETPPTALCAQ
                          );
            END;
        MI_CONSECUTIVO := MI_CONSECUTIVO+1;
      END LOOP;
       MI_CAMPOS := 'AFECTADO      =  -1,  
                     DATE_MODIFIED = SYSDATE, 
                     MODIFIED_BY   = '''||UN_USUARIO||'''';                          
            MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA ||''' 
                             AND ANO     = '   || UN_ANO      || ' 
                             AND NUMERO  = '   || UN_COMPR_AFECT;
         BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'SOLICITUDDISPONIBILIDAD',
                                                      UN_ACCION     => 'M',
                                                      UN_CAMPOS     => MI_CAMPOS,
                                                      UN_CONDICION  => MI_CONDICION
                                                      );

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                END;      
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                   UN_ERROR_COD  => PCK_ERRORES.ERR_ACTSOLICITUDDIS,
                                                   UN_TABLAERROR => 'SOLICITUDDISPONIBILIDAD'
                                                   );        
            END;

            SELECT NVL(SUM(VALOR_DEBITO),0) SUMA_DEBITO,
                   NVL(SUM(VALOR_CREDITO),0) SUMA_CREDITO
            INTO   MI_SUMADEBITO,
                   MI_SUMACREDITO
                FROM DETALLE_COMPROBANTE_PPTAL
                WHERE COMPANIA  = UN_COMPANIA
                AND ANO         = UN_ANO
                AND TIPO_CPTE   = UN_TIPO
                AND COMPROBANTE = UN_NUMERO;

			/*7708148 - Mperez - Se adiciona el nro_documento al comprobante para guardar el comprobante a afectar*/
            MI_CAMPOS :=  'VLR_DOCUMENTO =   '|| MI_SUMADEBITO  ||', 
						   NRO_DOCUMENTO = ''' || UN_COMPR_AFECT || ''', 
                           TIPO_DOCUMENTO= ''' || UN_TIPO_AFECT || ''',
                           DESCRIPCION   = '''|| RS.OBJETO      ||''',
                           DEPENDENCIA   = '''|| RS.DEPENDENCIA ||''',
                           TIPO_COMPROMISO  = '''|| RS.TIPO_COMPROMISO ||''',
                           DATE_MODIFIED = SYSDATE, 
                           MODIFIED_BY   = ''' || UN_USUARIO  ||'''';                          
            MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA ||''' 
                             AND ANO     = '   || UN_ANO      || ' 
                             AND TIPO    = ''' || UN_TIPO     ||'''
                             AND NUMERO  = '   || UN_NUMERO;
          BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'COMPROBANTE_PPTAL',
                                                      UN_ACCION     => 'M',
                                                      UN_CAMPOS     => MI_CAMPOS,
                                                      UN_CONDICION  => MI_CONDICION
                                                      );

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                         RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                END;      
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                   UN_ERROR_COD  => PCK_ERRORES.ERR_ACTVALORDOCUMENTO,
                                                   UN_TABLAERROR => 'COMPROBANTE_PPTAL'
                                                   );        
            END;
    END LOOP;
END PR_AFECTARCPTESCAQ;





END PCK_PRESUPUESTO2;