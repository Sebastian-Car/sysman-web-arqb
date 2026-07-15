create or replace PACKAGE BODY PCK_PRESUPUESTO_CIE AS

FUNCTION FC_CIERREPRESUPUESTOCB
   /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 01/12/2018
        TIME              : 06:45 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite realizar el cierre presupuetal anual en base a los movimientos abiertos de presupuesto
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:cierrePresupuestalCb
        @METHOD:Post
*/   
    (
     UN_COMPANIA             IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE           IN  PCK_SUBTIPOS.TI_ANIO,
     UN_USUARIO              IN  PCK_SUBTIPOS.TI_USUARIO,
     UN_CIERRENORMAL         IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREPASIVO         IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREVIGFUTURAS     IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREVIGFUTUAPASIVO IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREREGALIAS       IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_FECHACIERRE          IN  DATE,
     UN_CIERRECOFINANCIADOS  IN  PCK_SUBTIPOS.TI_LOGICO
    )
RETURN CLOB AS
    MI_ANOACTUAL          NUMBER(4,0);
    MI_RTA   CLOB;
BEGIN
    MI_ANOACTUAL     := UN_ANOACIERRE + 1;    
    /**
    *SE PREPARAN LAS AUXILIARES PUES EN OCASIONES NO LAS HAN PREPARADO
    **/
    PCK_PREPARAR_ANO.PR_PREPARA_ANO_SIGUIENTE (UN_COMPANIA	       => UN_COMPANIA,
                                               UN_ANO_DESTINO 	   => MI_ANOACTUAL,
                                               UN_ANO_ORIGEN       => UN_ANOACIERRE,
                                               UN_COMPANIA_DESTINO => UN_COMPANIA,
                                               UN_SOLOAUXILIAR     => -1);


    IF UN_CIERREREGALIAS <> 0 THEN--(CC:3349_INI_CFBARRERA)
            MI_RTA := FC_CIERREPRESUPUESTOREGA(
                        UN_COMPANIA    => UN_COMPANIA,  
                        UN_ANOACIERRE  => UN_ANOACIERRE,
                        UN_FECHACIERRE => UN_FECHACIERRE,
                        UN_USUARIO     => UN_USUARIO
                      );          
            IF MI_RTA IS NOT NULL THEN
                RETURN MI_RTA;
            END IF;
              
    END IF;--(CC:3349_FIN_CFBARRERA)

    /*
    * PREPARA INFORMACIÓN DE CIERRE EN LA TEMPORAL
    */
    MI_RTA := FC_PREPARATODO(UN_COMPANIA             => UN_COMPANIA,  
                             UN_ANOACIERRE           => UN_ANOACIERRE,
                             UN_CIERRENORMAL         => UN_CIERRENORMAL,
                             UN_CIERREPASIVO         => UN_CIERREPASIVO,
                             UN_CIERREVIGFUTURAS     => UN_CIERREVIGFUTURAS,
                             UN_CIERREVIGFUTUAPASIVO => UN_CIERREVIGFUTUAPASIVO,
                             UN_CIERREREGALIAS       => UN_CIERREREGALIAS,
                             UN_CIERRECOFINANCIADOS  => UN_CIERRECOFINANCIADOS);
    /*
    * CREA PLAN PRESUPUESTAL Y SALDOS INICIALES
    */
    PR_INSERTACIERREPLAN(UN_COMPANIA   => UN_COMPANIA,
                         UN_ANOACIERRE => UN_ANOACIERRE,
                         UN_USUARIO    => UN_USUARIO);

    PR_INSERTARAPROPIACIONCIERRE(UN_COMPANIA   => UN_COMPANIA,
                                 UN_ANOACIERRE => UN_ANOACIERRE,
                                 UN_USUARIO    => UN_USUARIO);

    MI_RTA :=PCK_PRESUPUESTO.FC_CONTABILIZARAPROPINICIAL(UN_COMPANIA => UN_COMPANIA,
                                                         UN_ANIO     => UN_ANOACIERRE + 1);

    PR_INSERTARADICIONCIERRE(UN_COMPANIA    => UN_COMPANIA,
                             UN_ANOACIERRE  => UN_ANOACIERRE,
                             UN_FECHACIERRE => UN_FECHACIERRE,
                             UN_USUARIO     => UN_USUARIO);
    /*
    * CREA COMPROBANTES PRESUPUESTALES
    */
    PR_INSERTARCOMPROBANTECIERRE(UN_COMPANIA   => UN_COMPANIA,
                                UN_ANOACIERRE  => UN_ANOACIERRE,
                                UN_FECHACIERRE => UN_FECHACIERRE,
                                UN_PREFIJO     => 'DIS',
                                UN_USUARIO     => UN_USUARIO);
    PR_INSERTARCOMPROBANTECIERRE(UN_COMPANIA   => UN_COMPANIA,
                                UN_ANOACIERRE  => UN_ANOACIERRE,
                                UN_FECHACIERRE => UN_FECHACIERRE,
                                UN_PREFIJO     => 'RES',
                                UN_USUARIO     => UN_USUARIO);

    PR_INSERTARCOMPROBANTECIERRE(UN_COMPANIA    => UN_COMPANIA,
                                 UN_ANOACIERRE  => UN_ANOACIERRE,
                                 UN_FECHACIERRE => UN_FECHACIERRE,
                                 UN_PREFIJO     => 'REO',
                                 UN_USUARIO     => UN_USUARIO);
    IF UN_CIERRECOFINANCIADOS <> 0 THEN
    PR_INSERT_COFINANCIADO(UN_COMPANIA            => UN_COMPANIA,
                           UN_ANOACIERRE          => UN_ANOACIERRE,
                           UN_CIERRECOFINANCIADOS => UN_CIERRECOFINANCIADOS,
                           UN_FECHACIERRE         => UN_FECHACIERRE,
                           UN_USUARIO             => UN_USUARIO);
    END IF;

    RETURN MI_RTA;
END FC_CIERREPRESUPUESTOCB;

PROCEDURE PR_CONTROLCUENTAS
       /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 01/12/2018
        TIME              : 06:45 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Para realizar un unico llamado y controlar si al momento de realizar el cierre
                            en el plan ya existen
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:cierrePresupuestalCb
        @METHOD:Post
*/  
    (
     UN_COMPANIA      IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANO           IN  PCK_SUBTIPOS.TI_ANIO,
     UN_CODIGO        IN  PCK_SUBTIPOS.TI_CODIGOPPTAL,
     UN_MENSAJE       IN  VARCHAR2
    )
    AS
        MI_REEMPLAZOS         PCK_SUBTIPOS.TI_CLAVEVALOR; 
        MI_CONTROL            NUMBER(5,0) :=0; 
BEGIN
    SELECT COUNT(COMPANIA) 
      INTO MI_CONTROL  
      FROM PLAN_PRESUPUESTAL
    WHERE COMPANIA = UN_COMPANIA
      AND ANO      = UN_ANO
      AND CODIGO   = UN_CODIGO;
    IF MI_CONTROL <> 0 THEN
        MI_REEMPLAZOS(1).CLAVE := 'TIPO';
        MI_REEMPLAZOS(1).VALOR := UN_MENSAJE;
        MI_REEMPLAZOS(2).CLAVE := 'RUBRO';
        MI_REEMPLAZOS(2).VALOR := UN_CODIGO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD =>-20000,
                UN_ERROR_COD=>PCK_ERRORES.ERR_PPTOCIERRECUENTAS,
                UN_TABLAERROR =>'PLAN_PRESUPUESTAL',
                UN_REEMPLAZOS => MI_REEMPLAZOS
              ); 
    END IF;
END PR_CONTROLCUENTAS;


PROCEDURE PR_INSERTARTIPOCOMPPTAL
     /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 01/12/2018
        TIME              : 06:45 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Inserta o actualiza los tipos de comprobantes cuando no existen los crea en base a 
                            a uno existente de la misma clase
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:cierrePresupuestalCb
        @METHOD:Post
    */  
    (
     UN_COMPANIA      IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_TIPO          IN  PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL,
     UN_CLASE         IN  PCK_SUBTIPOS.TI_CLASECOMPROPPTO,
     UN_NOMBRE        IN  VARCHAR2,
     UN_USUARIO       IN  PCK_SUBTIPOS.TI_USUARIO 
    )
AS
    MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_RTA            PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGENOEXISTE  PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
BEGIN 
    BEGIN
        IF NVL(UN_TIPO,'')<>' ' THEN
            <<TIPOS>>
            FOR RS IN ( SELECT *
                        FROM (
                            SELECT TABLA.*
                            FROM (
                                SELECT CASE WHEN CODIGO = UN_TIPO THEN 0 ELSE 1 END ORD,
                                      COMPANIA, CODIGO, NOMBRE, CLASE, 
                                      TIPOAFECTA, OBLIGAAFECTACION, 
                                      TECEROIGUAL, TIPO_DOCUMENTO, FORMATO, 
                                      AUTOMATICO, TIPOVIGENCIAFUTURA
                                FROM TIPO_COMPROBPP
                                WHERE COMPANIA = UN_COMPANIA
                                  AND CLASE    = UN_CLASE
                            ) TABLA
                            ORDER BY TABLA.ORD
                        )
                        WHERE ROWNUM=1
            ) 
            LOOP
                IF RS.CODIGO <> UN_TIPO THEN
                    BEGIN
                        BEGIN
                            MI_CAMPOS := ' COMPANIA, 
                                           CODIGO, 
                                           NOMBRE, 
                                           CLASE,
                                           TIPOAFECTA, 
                                           OBLIGAAFECTACION, 
                                           TECEROIGUAL,
                                           TIPO_DOCUMENTO,
                                           FORMATO, 
                                           AUTOMATICO, 
                                           TIPOVIGENCIAFUTURA,
                                           DATE_MODIFIED,
                                           CREATED_BY';
                            MI_VALORES :=' SELECT COMPANIA, 
                                                  ''' || UN_TIPO   || ''' CODIGO, 
                                                  ''' || UN_NOMBRE || ''' NOMBRE, 
                                                  CLASE, 
                                                  TIPOAFECTA, 
                                                  OBLIGAAFECTACION, 
                                                  TECEROIGUAL, 
                                                  TIPO_DOCUMENTO, 
                                                  FORMATO, 
                                                  AUTOMATICO, 
                                                  TIPOVIGENCIAFUTURA,
                                                  SYSDATE,
                                                  ''' || UN_USUARIO || '''
                                            FROM TIPO_COMPROBPP
                                            WHERE COMPANIA = ''' || UN_COMPANIA || '''
                                              AND CODIGO   = ''' || RS.CODIGO   || '''';

                            MI_RTA  := PCK_DATOS.FC_ACME(UN_TABLA   => 'TIPO_COMPROBPP',
                                                         UN_ACCION  => 'IS',
                                                         UN_CAMPOS  => MI_CAMPOS,
                                                         UN_VALORES => MI_VALORES);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                        MI_REEMPLAZOS(1).CLAVE := 'TIPO';
                        MI_REEMPLAZOS(1).VALOR := UN_TIPO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOERRORINSERTTIPOCOM
                                                  ,UN_TABLAERROR => 'TIPO_COMPROBPP'
                                                  ,UN_REEMPLAZOS => MI_REEMPLAZOS);
                    END;                        
                END IF;
            END LOOP TIPOS;
        END IF;
    END;    
END PR_INSERTARTIPOCOMPPTAL;

FUNCTION FC_PREPARA_TIPOCIERRE
     /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 18/01/2019
        TIME              : 09:57 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite preparar los datos para el cierre por tipo de cierre
                            (NOR = Normal, 
                             PAE = Pasivos Exigibles
                             VF  = Vigencias Futuras
                             VFE = De Vigencias Futuras a PAsivos Exigibles
                             REG = Regalias)
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:cierrePresupuestalCb
        @METHOD:Post
    */  
   (
     UN_COMPANIA        IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE      IN  PCK_SUBTIPOS.TI_ANIO,
     UN_TIPOCIERRE      IN  VARCHAR2
   )
RETURN VARCHAR2
AS 
    MI_CLASERESERVA  VARCHAR2(5);             
    MI_CLASECAJA     VARCHAR2(5);
    MI_RTA           VARCHAR2(1500 CHAR) DEFAULT ' ';
BEGIN
    FOR RS IN (SELECT CLASERESERVA,  TIPO_DIS, 
                      TIPO_RES,      TIPO_REO,
                      DIGITOSCAMBIO, TIPOVIGENCIAFINAL, 
                      GENERAR,       CREAJERARQUIA, 
                      TIPOVIGENCIAINICI, TIPO_ADI
               FROM  CONFIG_CIERRE_PPTAL
               WHERE COMPANIA   = UN_COMPANIA
                 AND TIPOCIERRE = UN_TIPOCIERRE 
                 AND GENERAR    NOT IN(0)
             )
    LOOP    
        /*
        * VALIDA QUE LA CONFIGURACIÓN SEA CORRECTA
        */
        PR_VALIDARCONFIG(UN_COMPANIA          => UN_COMPANIA,
                         UN_TIPOCIERRE        => UN_TIPOCIERRE,
                         UN_CLASERESERVA      => RS.CLASERESERVA,
                         UN_TIPO_DIS          => RS.TIPO_DIS,
                         UN_TIPO_RES          => RS.TIPO_RES,
                         UN_TIPO_REO          => RS.TIPO_REO,
                         UN_TIPO_ADI          => RS.TIPO_ADI,
                         UN_DIGITOSCAMBIO     => RS.DIGITOSCAMBIO,
                         UN_TIPOVIGENCIAINICI => RS.TIPOVIGENCIAINICI,
                         UN_TIPOVIGENCIAFINAL => RS.TIPOVIGENCIAFINAL,
                         UN_GENERAR           => RS.GENERAR);  
        /*
        * CONTROLA QUE LA CUENTA NO EXISTA
        */
        PR_CONTROLCUENTAS(UN_COMPANIA => UN_COMPANIA,
                          UN_ANO      => UN_ANOACIERRE + 1,
                          UN_CODIGO   => RS.DIGITOSCAMBIO,
                          UN_MENSAJE  => 'de ' || FC_NOMBRETIPOCIERRE(UN_TIPOCIERRE => UN_TIPOCIERRE));
        IF RS.CLASERESERVA IN('APROPIACION', 'PASIVO', 'PASAIGUAL') THEN
            /*
            * PREPARA LOS DATOS DE RESERVAS DE APROPIACION
            */
            MI_CLASERESERVA := FC_PREPARA_APROP_RESERVA(
                                     UN_COMPANIA       => UN_COMPANIA,  
                                     UN_ANOACIERRE     => UN_ANOACIERRE,
                                     UN_TIPOCIERRE     => UN_TIPOCIERRE,
                                     UN_CLASERESERVA   => RS.CLASERESERVA
                                     );
        ELSIF RS.CLASERESERVA IN('CAJA') THEN   
            /**
            * PREPARA LOS DATOS DE RESERVAS DE CAJA
            **/ 
            MI_CLASECAJA := FC_PREPARA_APROP_CAJA(
                                  UN_COMPANIA       => UN_COMPANIA,  
                                  UN_ANOACIERRE     => UN_ANOACIERRE,
                                  UN_TIPOCIERRE     => UN_TIPOCIERRE,
                                  UN_CLASERESERVA   => RS.CLASERESERVA);
        END IF;
        IF RS.CLASERESERVA IN( 'PASIVO', 'PASAIGUAL') THEN
            /**
            * PREPARA LOS DATOS DE RESERVAS DE CAJA
            **/ 
            MI_CLASECAJA := FC_PREPARA_APROP_CAJA(
                                  UN_COMPANIA       => UN_COMPANIA,  
                                  UN_ANOACIERRE     => UN_ANOACIERRE,
                                  UN_TIPOCIERRE     => UN_TIPOCIERRE,
                                  UN_CLASERESERVA   => RS.CLASERESERVA);
        END IF;
        IF RS.CLASERESERVA IN( 'SALDOS') AND UN_TIPOCIERRE = 'REG' THEN
            /**
            * PREPARA LOS DATOS DE LOS SALDOS DE REGALIAS DE APROPIACIONES INICIALES
            **/ 
            PR_INSERTARAPROPIACIERREREGALI(
                                  UN_COMPANIA       => UN_COMPANIA,  
                                  UN_ANOACIERRE     => UN_ANOACIERRE);
        END IF;
        IF RS.CLASERESERVA IN('SALDOS') AND UN_TIPOCIERRE = 'COF' THEN
            /**
            * PREPARA LOS DATOS DE LOS SALDOS DE COFINANCIADOS DE APROPIACIONES INICIALES
            **/
            PR_INSERT_COFINANCIADO(
                                  UN_COMPANIA       => UN_COMPANIA,
                                  UN_ANOACIERRE     => UN_ANOACIERRE,
                                  UN_CIERRECOFINANCIADOS => 0);
        END IF;

    END LOOP;
    /*
    * SI ES VIGENCIA FUTURA Y ESTA CONFIGURADA COMO TIPO VIGENCIA VT
    * Y EL AÑO CONFIGURADO PARA VIGENCIA FUTURA ES IGUAL AL AÑO SIGUIENTE AL CIERRE
    * SE DEJA LA VIGENCIA VF (VIGENCIA FUTURA ACTUAL)
    */
    BEGIN
        BEGIN
            MI_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     =>  'TEMP_CIERRE_PPTAL',
                                      UN_ACCION    =>  'M',
                                      UN_CAMPOS    =>  'TIPOVIGENCIA = ''VF''',
                                      UN_CONDICION =>  'VIGENCIA     = ' || TO_CHAR(UN_ANOACIERRE + 1) || '
                                                   AND TIPOCIERRE   = ''PASAIGUAL''
                                                   AND CLASERESERVA = ''VF''
                                                   AND TIPOVIGENCIA = ''VT''');
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTDEBITOCREDNEGATIVO,
                                    UN_TABLAERROR => 'TEMP_CIERRE_PPTAL');    
    END;
    RETURN ' ';
END FC_PREPARA_TIPOCIERRE;


FUNCTION FC_PREPARATODO
        /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 22/01/2019
        TIME              : 05:50 PM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite preparar los datos para el cierre para todos los tipos dependiendo de los check seleccionados
                            (NOR = Normal, 
                             PAE = Pasivos Exigibles
                             VF  = Vigencias Futuras
                             VFE = De Vigencias Futuras a PAsivos Exigibles
                             REG = Regalias)
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:cierrePresupuestalCb
        @METHOD:Post
    */  
   (
     UN_COMPANIA             IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE           IN  PCK_SUBTIPOS.TI_ANIO,
     UN_CIERRENORMAL         IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREPASIVO         IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREVIGFUTURAS     IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREVIGFUTUAPASIVO IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREREGALIAS       IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERRECOFINANCIADOS  IN  PCK_SUBTIPOS.TI_LOGICO
   )RETURN VARCHAR2
AS 
   MI_RTA           VARCHAR2(1500 CHAR) DEFAULT ' ';
BEGIN

    /*
    * CPREPARA INFORMACIÓN DE CIERRE EN LA TEMPORAL
    */
    IF UN_CIERRENORMAL <> 0 THEN
        MI_RTA:=PCK_PRESUPUESTO_CIE.FC_PREPARA_TIPOCIERRE(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'NOR');
    END IF;
    IF UN_CIERREPASIVO <>0 THEN
        MI_RTA:=PCK_PRESUPUESTO_CIE.FC_PREPARA_TIPOCIERRE(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'PAE');
    END IF;
    IF UN_CIERREVIGFUTURAS<>0 THEN
        MI_RTA:=PCK_PRESUPUESTO_CIE.FC_PREPARA_TIPOCIERRE(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'VF');
    END IF;
    IF UN_CIERREVIGFUTUAPASIVO<>0 THEN
        MI_RTA:=PCK_PRESUPUESTO_CIE.FC_PREPARA_TIPOCIERRE(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'VFE');
    END IF;   
    IF UN_CIERREREGALIAS<>0 THEN
        MI_RTA:=PCK_PRESUPUESTO_CIE.FC_PREPARA_TIPOCIERRE(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'REG');
    END IF;
    IF UN_CIERRECOFINANCIADOS<>0 THEN
        MI_RTA:=PCK_PRESUPUESTO_CIE.FC_PREPARA_TIPOCIERRE(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'COF');
    END IF;  
   RETURN MI_RTA;
END FC_PREPARATODO;

FUNCTION FC_PREPARA_APROP_RESERVA
      /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 01/12/2018
        TIME              : 06:45 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite preparar los datos para las reservas apropiaciones
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:prepararApropiacionReserva
        @METHOD:Post
*/   
   (
     UN_COMPANIA        IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE      IN  PCK_SUBTIPOS.TI_ANIO,
     UN_TIPOCIERRE      IN  VARCHAR2,
     UN_CLASERESERVA    IN  CONFIG_CIERRE_PPTAL.CLASERESERVA%TYPE
   )
RETURN VARCHAR2
AS
    MI_CONSULTA   VARCHAR2(32000);
    MI_CAMPOS     VARCHAR2(32000);
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CLASE      PCK_SUBTIPOS.TI_CLASECOMPROPPTO;
    MI_CONSECUTIVO NUMBER(10,0);
    MI_VIG_FUTURA  PCK_SUBTIPOS.TI_LOGICO;
    MI_CONDIC_VIG_FUT VARCHAR2(200);

BEGIN
    MI_CLASE:='RES';
    <<RECORRETIPOCIERRE>>
    FOR RS IN (SELECT CLASERESERVA,  TIPO_DIS, 
                      TIPO_RES,      TIPO_REO,
                      DIGITOSCAMBIO, TIPOVIGENCIAFINAL, 
                      GENERAR,       CREAJERARQUIA, 
                      TIPOVIGENCIAINICI, TIPO_ADI
                      ,RECLASIFICAR
               FROM  CONFIG_CIERRE_PPTAL
               WHERE COMPANIA     = UN_COMPANIA
                 AND TIPOCIERRE   = UN_TIPOCIERRE 
                 AND CLASERESERVA = UN_CLASERESERVA
                 AND GENERAR    NOT IN(0)
             )
    LOOP
        SELECT COUNT(REGISTRO)
          INTO MI_CONSECUTIVO
        FROM TEMP_CIERRE_PPTAL;
        /*
        * PARA EL CIERRE DE VIGENCIAS FUTURAS SE TIENE EN CUENTA
        * Si el año de la vigencia es igual al año de cierre +1 creo apropiacion inicial y movimientos, cambio digito
        * Si el año de la vigencia es menor al año de cierre +1 copio con arbol y no cambio el digito        
        */
        IF UN_TIPOCIERRE ='VF' AND RS.CLASERESERVA='PASAIGUAL' AND RS.TIPOVIGENCIAINICI ='VT' THEN
            MI_VIG_FUTURA:= -1;
        ELSE 
            MI_VIG_FUTURA:= 0;
        END IF;
        MI_CONDIC_VIG_FUT:=' (0<>' || MI_VIG_FUTURA || ' AND PP.VIGENCIA = PP.ANO + 1) OR 0=' || MI_VIG_FUTURA;
        MI_CONSULTA:= 'SELECT ''' || MI_CLASE || ''' CLASECIERRE
                              ,ROWNUM + ' || MI_CONSECUTIVO || ' REGISTRO  
                              ,DP.COMPANIA
                              ,''' || UN_TIPOCIERRE  || ''' TIPOCIERRE
                              ,''' || RS.CLASERESERVA || ''' CLASERESERVA
                              ,'   || RS.CREAJERARQUIA || ' CREAJERARQUIA
                              ,DP.ANO             ANO
                              ,DP.TIPO_CPTE_AFECT          DIS_TIPO_INICIAL 
                              ,''' || RS.TIPO_DIS || '''   DIS_TIPO_FINAL
                              ,DP.CMPTE_AFECTADO           DIS_NUMERO 
                              ,DP.CONSECUTIVOPPTO          DIS_CONSECUTIVO
                              ,DP.TIPO_CPTE                RES_TIPO_INICIAL
                              ,''' || RS.TIPO_RES || '''   RES_TIPO_FINAL
                              ,DP.COMPROBANTE              RES_NUMERO 
                              ,DP.CONSECUTIVO              RES_CONSECUTIVO 
                              ,CASE WHEN ' || MI_CONDIC_VIG_FUT || '
                                    THEN ' || CASE WHEN RS.DIGITOSCAMBIO IS NULL THEN ' DP.CUENTA ' ELSE CHR(39) || RS.DIGITOSCAMBIO || CHR(39) || ' || SUBSTR(DP.CUENTA,2 , LENGTH(DP.CUENTA)) ' END || 
                                  ' ELSE DP.CUENTA 
                                    END CUENTA
                              ,DP.CUENTA CUENTAANT
                              ,CASE WHEN ' || MI_CONDIC_VIG_FUT || '
                                    THEN ' || CASE WHEN RS.DIGITOSCAMBIO     IS NULL THEN ' NULL ' ELSE CHR(39) || RS.DIGITOSCAMBIO     || CHR(39) END || 
                                  ' ELSE SUBSTR(DP.CUENTA,1,1) 
                                    END DIGITOS
                              ,CASE WHEN ' || MI_CONDIC_VIG_FUT || '
                                    THEN ' || CASE WHEN RS.TIPOVIGENCIAFINAL IS NULL AND UN_TIPOCIERRE = 'REG' THEN ' PP.TIPOVIGENCIA ' ELSE CHR(39) || RS.TIPOVIGENCIAFINAL || CHR(39) END || 
                                  ' ELSE ' || CASE WHEN RS.TIPOVIGENCIAINICI IS NULL AND UN_TIPOCIERRE = 'REG' THEN ' PP.TIPOVIGENCIA ' ELSE CHR(39) || RS.TIPOVIGENCIAINICI || CHR(39) END || ' END TIPOVIGENCIA                                  
                              ,PP.VIGENCIA
                              ,DP.NATURALEZA
                              ,DIS.CENTRO_COSTO        DIS_CENTRO_COSTO
                              ,DIS.AUXILIAR            DIS_AUXILIAR
                              ,DIS.FUENTE_RECURSO      DIS_FUENTE_RECURSO
                              ,DIS.REFERENCIA          DIS_REFERENCIA     
                              ,DIS.TERCERO             DIS_TERCERO
                              ,DIS.SUCURSAL            DIS_SUCURSAL
                              ,DIS.DESCRIPCION         DIS_DESCRIPCION
                              ,DIS.DESTINO             DIS_DESTINO
                              ,NVL(DIS.TEXTO,''.'')    DIS_TEXTO 
                              ,DIS.NRO_DOCUMENTO       DIS_NRO_DOCUMENTO
                              ,DIS.TIPOCONTRATO        DIS_TIPOCONTRATO
                              ,DIS.NUMEROCONTRATO      DIS_NUMEROCONTRATO                           
                              ,DIS.CODSOLICITANTE      DIS_CODSOLICITANTE
                              ,DIS.SUCSOLICITANTE      DIS_SUCSOLICITANTE
                              ,DIS.COD_PROYECTO_PPTAL  DIS_COD_PROYECTO_PPTAL
                              ,DIS.ANOPROYECTO         DIS_ANOPROYECTO
                              ,DIS.LUGAR               DIS_LUGAR
                              ,DIS.FUENTE_FINANCIACION DIS_FUENTE_FINANCIACION
                              ,DIS.ASIGNACION          DIS_ASIGNACION              
                              ,DIS.DEPENDENCIA         DIS_DEPENDENCIA                          
                              ,DIS_DET.CENTRO_COSTO    DIS_DET_CENTRO_COSTO
                              ,DIS_DET.AUXILIAR        DIS_DET_AUXILIAR
                              ,DIS_DET.FUENTE_RECURSO  DIS_DET_FUENTE_RECURSO
                              ,DIS_DET.REFERENCIA      DIS_DET_REFERENCIA
                              ,DIS_DET.TERCERO         DIS_DET_TERCERO
                              ,DIS_DET.SUCURSAL        DIS_DET_SUCURSAL
                              ,DIS_DET.DESCRIPCION     DIS_DET_DESCRIPCION
                              ,DIS_DET.NRO_DOCUMENTO   DIS_DET_NRO_DOCUMENTO                          
                              ,DIS_DET.TIPOT           DIS_DET_TIPOT        
                              ,DIS_DET.CLASET          DIS_DET_CLASET   
                              ,DIS_DET.CMPTE_SOLICI_AFECTADO DIS_DET_CMPTE_SOLICI_AFECTADO
                              ,DIS_DET.DEPENDENCIA     DIS_DET_DEPENDENCIA 
                              ,DIS_DET.NUMERO_SOLICITUD DIS_DET_NUMERO_SOLICITUD
                              ,DIS_DET.TIPO_SOLICITUD  DIS_DET_TIPO_SOLICITUD                          
                              ,DIS_DET.TIPOCONTRATO    DIS_DET_TIPOCONTRATO
                              ,DIS_DET.NUMEROCONTRATO  DIS_DET_NUMEROCONTRATO
                              ,RES.CENTRO_COSTO        RES_CENTRO_COSTO
                              ,RES.AUXILIAR            RES_AUXILIAR
                              ,RES.FUENTE_RECURSO      RES_FUENTE_RECURSO
                              ,RES.REFERENCIA          RES_REFERENCIA     
                              ,RES.TERCERO             RES_TERCERO
                              ,RES.SUCURSAL            RES_SUCURSAL
                              ,RES.DESCRIPCION         RES_DESCRIPCION
                              ,RES.DESTINO             RES_DESTINO
                              ,NVL(RES.TEXTO,''.'')    RES_TEXTO 
                              ,RES.NRO_DOCUMENTO       RES_NRO_DOCUMENTO
                              ,RES.TIPOCONTRATO        RES_TIPOCONTRATO
                              ,RES.NUMEROCONTRATO      RES_NUMEROCONTRATO                           
                              ,RES.CODSOLICITANTE      RES_CODSOLICITANTE
                              ,RES.SUCSOLICITANTE      RES_SUCSOLICITANTE
                              ,RES.COD_PROYECTO_PPTAL  RES_COD_PROYECTO_PPTAL
                              ,RES.ANOPROYECTO         RES_ANOPROYECTO
                              ,RES.LUGAR               RES_LUGAR
                              ,RES.FUENTE_FINANCIACION RES_FUENTE_FINANCIACION
                              ,RES.ASIGNACION          RES_ASIGNACION              
                              ,RES.DEPENDENCIA         RES_DEPENDENCIA                          
                              ,DP.CENTRO_COSTO         RES_DET_CENTRO_COSTO
                              ,DP.AUXILIAR             RES_DET_AUXILIAR
                              ,DP.FUENTE_RECURSO       RES_DET_FUENTE_RECURSO
                              ,DP.REFERENCIA           RES_DET_REFERENCIA
                              ,DP.TERCERO              RES_DET_TERCERO
                              ,DP.SUCURSAL             RES_DET_SUCURSAL
                              ,DP.DESCRIPCION          RES_DET_DESCRIPCION
                              ,DP.NRO_DOCUMENTO        RES_DET_NRO_DOCUMENTO                          
                              ,DP.TIPOT                RES_DET_TIPOT        
                              ,DP.CLASET               RES_DET_CLASET   
                              ,DP.CMPTE_SOLICI_AFECTADO RES_DET_CMPTE_SOLICI_AFECTADO
                              ,DP.DEPENDENCIA          RES_DET_DEPENDENCIA 
                              ,DP.NUMERO_SOLICITUD     RES_DET_NUMERO_SOLICITUD
                              ,DP.TIPO_SOLICITUD       RES_DET_TIPO_SOLICITUD                          
                              ,DP.TIPOCONTRATO         RES_DET_TIPOCONTRATO
                              ,DP.NUMEROCONTRATO       RES_DET_NUMEROCONTRATO
                              ,((DP.VALOR_DEBITO        - DP.VALOR_CREDITO       )
                                  -(DP.DEBITO_AFECTADO     - DP.CREDITO_AFECTADO    )
                                  +(DP.MODIFICACION_DEBITO - DP.MODIFICACION_CREDITO)) AS VALOR_DEBITO
                              , ' || CASE WHEN RS.TIPO_ADI IS NULL THEN ' NULL ' ELSE '''' || RS.TIPO_ADI || '''' END || '
                              ,DP.SECTOR
                             ,DP.PROGRAMA
                             ,DP.SUBPROGRAMA
                             ,DP.COD_PROD_CUIPO
                             ,DP.CODIGO_BPIN
                             ,DP.CODIGO_CCPET
                             ,DP.CODIGO_CPC
                             ,DP.CODIGOUNIDADEJE
                             ,DP.FUENTE_CUIPO
                             ,DP.CODIGOCCPETREGA
                             ,DP.POLITICA_PUBLICA
                             ,DP.DETALLE_SECTORIAL
                         FROM DETALLE_COMPROBANTE_PPTAL DP INNER JOIN PLAN_PRESUPUESTAL PP 
                           ON DP.COMPANIA = PP.COMPANIA 
                          AND DP.ANO      = PP.ANO 
                          AND DP.CUENTA   = PP.CODIGO
                         INNER JOIN TIPO_COMPROBPP TC 
                           ON DP.COMPANIA  = TC.COMPANIA 
                          AND DP.TIPO_CPTE = TC.CODIGO 
                         INNER JOIN COMPROBANTE_PPTAL RES
                           ON DP.COMPANIA        = RES.COMPANIA 
                          AND DP.ANO             = RES.ANO  
                          AND DP.TIPO_CPTE       = RES.TIPO 
                          AND DP.COMPROBANTE     = RES.NUMERO  
                         INNER JOIN DETALLE_COMPROBANTE_PPTAL DIS_DET
                           ON DP.COMPANIA        = DIS_DET.COMPANIA 
                          AND DP.ANO_AFECT       = DIS_DET.ANO  
                          AND DP.TIPO_CPTE_AFECT = DIS_DET.TIPO_CPTE 
                          AND DP.CMPTE_AFECTADO  = DIS_DET.COMPROBANTE
                          AND DP.CONSECUTIVOPPTO = DIS_DET.CONSECUTIVO
                         INNER JOIN COMPROBANTE_PPTAL DIS
                           ON DIS_DET.COMPANIA    = DIS.COMPANIA 
                          AND DIS_DET.ANO         = DIS.ANO  
                          AND DIS_DET.TIPO_CPTE   = DIS.TIPO 
                          AND DIS_DET.COMPROBANTE = DIS.NUMERO
                        WHERE DP.COMPANIA   = ''' || UN_COMPANIA   || '''
                          AND DP.ANO        = '   || UN_ANOACIERRE || '
                          AND PP.NATURALEZA = ''D''' ||  
                          CASE WHEN UN_TIPOCIERRE = 'REG' AND RS.TIPOVIGENCIAINICI IS NULL THEN ' ' ELSE ' AND PP.TIPOVIGENCIA IN (''' || RS.TIPOVIGENCIAINICI || ''') ' END ||
                          CASE WHEN UN_TIPOCIERRE = 'REG' THEN ' AND PP.REGALIAS NOT IN(0) ' ELSE ' AND PP.REGALIAS IN(0) ' END || 
                          CASE WHEN UN_TIPOCIERRE = 'COF' THEN ' AND PP.COFINANCIADO NOT IN(0) ' ELSE ' AND PP.COFINANCIADO IN(0) ' END || '                           
                          AND TC.CLASE IN (''' || MI_CLASE || ''') 
                          AND ((DP.VALOR_DEBITO        - DP.VALOR_CREDITO       )
                             - (DP.DEBITO_AFECTADO     - DP.CREDITO_AFECTADO    )
                             + (DP.MODIFICACION_DEBITO - DP.MODIFICACION_CREDITO))>0';

        MI_CAMPOS := '    CLASECIERRE
                        , REGISTRO
                        , COMPANIA
                        , TIPOCIERRE
                        , CLASERESERVA
                        , CREAJERARQUIA
                        , ANO
                        , DIS_TIPO_INICIAL
                        , DIS_TIPO_FINAL
                        , DIS_NUMERO
                        , DIS_CONSECUTIVO
                        , RES_TIPO_INICIAL
                        , RES_TIPO_FINAL
                        , RES_NUMERO
                        , RES_CONSECUTIVO                    
                        , CUENTA
                        , CUENTAANT
                        , DIGITOCAMBIO
                        , TIPOVIGENCIA
                        , VIGENCIA
                        , NATURALEZA                    
                        , DIS_CENTRO_COSTO
                        , DIS_AUXILIAR
                        , DIS_FUENTE_RECURSO
                        , DIS_REFERENCIA
                        , DIS_TERCERO
                        , DIS_SUCURSAL
                        , DIS_DESCRIPCION
                        , DIS_DESTINO
                        , DIS_TEXTO
                        , DIS_NRO_DOCUMENTO
                        , DIS_TIPOCONTRATO
                        , DIS_NUMEROCONTRATO
                        , DIS_CODSOLICITANTE
                        , DIS_SUCSOLICITANTE
                        , DIS_COD_PROYECTO_PPTAL
                        , DIS_ANOPROYECTO
                        , DIS_LUGAR
                        , DIS_FUENTE_FINANCIACION
                        , DIS_ASIGNACION
                        , DIS_DEPENDENCIA                    
                        , DIS_DET_CENTRO_COSTO
                        , DIS_DET_AUXILIAR
                        , DIS_DET_FUENTE_RECURSO
                        , DIS_DET_REFERENCIA
                        , DIS_DET_TERCERO
                        , DIS_DET_SUCURSAL
                        , DIS_DET_DESCRIPCION
                        , DIS_DET_NRO_DOCUMENTO                    
                        , DIS_DET_TIPOT
                        , DIS_DET_CLASET
                        , DIS_DET_CMPTE_SOLICI_AFECTADO
                        , DIS_DET_DEPENDENCIA     
                        , DIS_DET_NUMERO_SOLICITUD 
                        , DIS_DET_TIPO_SOLICITUD 
                        , DIS_DET_TIPOCONTRATO   
                        , DIS_DET_NUMEROCONTRATO 
                        , RES_CENTRO_COSTO
                        , RES_AUXILIAR
                        , RES_FUENTE_RECURSO
                        , RES_REFERENCIA
                        , RES_TERCERO
                        , RES_SUCURSAL
                        , RES_DESCRIPCION
                        , RES_DESTINO
                        , RES_TEXTO
                        , RES_NRO_DOCUMENTO
                        , RES_TIPOCONTRATO
                        , RES_NUMEROCONTRATO
                        , RES_CODSOLICITANTE
                        , RES_SUCSOLICITANTE
                        , RES_COD_PROYECTO_PPTAL
                        , RES_ANOPROYECTO
                        , RES_LUGAR
                        , RES_FUENTE_FINANCIACION
                        , RES_ASIGNACION
                        , RES_DEPENDENCIA                    
                        , RES_DET_CENTRO_COSTO
                        , RES_DET_AUXILIAR
                        , RES_DET_FUENTE_RECURSO
                        , RES_DET_REFERENCIA
                        , RES_DET_TERCERO
                        , RES_DET_SUCURSAL
                        , RES_DET_DESCRIPCION
                        , RES_DET_NRO_DOCUMENTO                    
                        , RES_DET_TIPOT
                        , RES_DET_CLASET  
                        , RES_DET_CMPTE_SOLICI_AFECTADO
                        , RES_DET_DEPENDENCIA     
                        , RES_DET_NUMERO_SOLICITUD 
                        , RES_DET_TIPO_SOLICITUD 
                        , RES_DET_TIPOCONTRATO   
                        , RES_DET_NUMEROCONTRATO
                        , VALOR_DEBITO
                        , TIPO_ADI
                        ,SECTOR
                        ,PROGRAMA
                        ,SUBPROGRAMA
                        ,COD_PROD_CUIPO
                        ,CODIGO_BPIN
                        ,CODIGO_CCPET
                        ,CODIGO_CPC
                        ,CODIGOUNIDADEJE
                        ,FUENTE_CUIPO
                        ,CODIGOCCPETREGA
                        ,POLITICA_PUBLICA
                        ,DETALLE_SECTORIAL
                        ';   
        BEGIN
            BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'TEMP_CIERRE_PPTAL', 
                                                       UN_ACCION  => 'IS', 
                                                       UN_CAMPOS  => MI_CAMPOS, 
                                                       UN_VALORES => MI_CONSULTA);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
            MI_REEMPLAZOS(0).CLAVE := 'COMPANIA';
            MI_REEMPLAZOS(0).VALOR := UN_COMPANIA;
            MI_REEMPLAZOS(1).CLAVE := 'ANIO';
            MI_REEMPLAZOS(1).VALOR := UN_ANOACIERRE;        
            MI_REEMPLAZOS(2).CLAVE := 'CLASE';
            MI_REEMPLAZOS(2).VALOR := MI_CLASE;  
            MI_REEMPLAZOS(3).CLAVE := 'DIGITO';
            MI_REEMPLAZOS(3).VALOR := RS.DIGITOSCAMBIO;
            MI_REEMPLAZOS(4).CLAVE := 'TIPOCIERRE';
            MI_REEMPLAZOS(4).VALOR := FC_NOMBRETIPOCIERRE(UN_TIPOCIERRE);
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOERRORINSERTTEMPORAL,
              UN_TABLAERROR => 'TEMP_CIERRE_PPTAL',
              UN_REEMPLAZOS => MI_REEMPLAZOS  
            );  
        END;              
        IF RS.RECLASIFICAR NOT IN(0) THEN
            PR_RECLASIFICACION(UN_COMPANIA     => UN_COMPANIA,
                               UN_CLASERESERVA => RS.CLASERESERVA,
                               UN_TIPOCIERRE   => UN_TIPOCIERRE,
                               UN_CLASECIERRE  => MI_CLASE,
                               UN_PREFIJO      =>'DIS');
            PR_RECLASIFICACION(UN_COMPANIA     => UN_COMPANIA,
                               UN_CLASERESERVA => RS.CLASERESERVA,
                               UN_TIPOCIERRE   => UN_TIPOCIERRE,
                               UN_CLASECIERRE  => MI_CLASE,
                               UN_PREFIJO      =>'RES');
        END IF;
    END LOOP RECORRETIPOCIERRE;

    --SE VALIDA SI EXISTEN COMPROBANTES DUPLICADOS PARA DEJAR EL MISMO TIPO DE COMPROBANTE DEL AÑO PASADO
    PR_ACTUALIZARTIPOFINAL(UN_COMPANIA    => UN_COMPANIA,  
                           UN_PREFIJO     => 'DIS',
                           UN_CLASECIERRE => MI_CLASE);
    PR_ACTUALIZARTIPOFINAL(UN_COMPANIA    => UN_COMPANIA,  
                           UN_PREFIJO     => 'RES',
                           UN_CLASECIERRE => MI_CLASE);  
    RETURN MI_CLASE;  
END FC_PREPARA_APROP_RESERVA;

FUNCTION FC_PREPARA_APROP_CAJA
         /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 01/12/2018
        TIME              : 06:45 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite preparar los datos para las reservas caja
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:prepararApropiacionCaja
        @METHOD:Post
*/   
   (
     UN_COMPANIA        IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE      IN  PCK_SUBTIPOS.TI_ANIO,
     UN_TIPOCIERRE      IN  VARCHAR2,
     UN_CLASERESERVA    IN  CONFIG_CIERRE_PPTAL.CLASERESERVA%TYPE     
   ) RETURN VARCHAR2
AS
    MI_CONSULTA   VARCHAR2(32000);
    MI_CAMPOS     VARCHAR2(32000);
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CLASE      PCK_SUBTIPOS.TI_CLASECOMPROPPTO;
    MI_CONSECUTIVO NUMBER(10,0);
    MI_VIG_FUTURA  PCK_SUBTIPOS.TI_LOGICO;
    MI_CONDIC_VIG_FUT VARCHAR2(200);
BEGIN
    MI_CLASE:='REO';
    <<RECORRETIPOCIERRE>>
    FOR RS IN (SELECT CLASERESERVA,  TIPO_DIS, 
                      TIPO_RES,      TIPO_REO,
                      DIGITOSCAMBIO, TIPOVIGENCIAFINAL, 
                      GENERAR,       CREAJERARQUIA, 
                      TIPOVIGENCIAINICI, TIPO_ADI
                      , RECLASIFICAR
               FROM  CONFIG_CIERRE_PPTAL
               WHERE COMPANIA     = UN_COMPANIA
                 AND TIPOCIERRE   = UN_TIPOCIERRE 
                 AND CLASERESERVA = UN_CLASERESERVA
                 AND GENERAR    NOT IN(0)
             )
    LOOP
        SELECT COUNT(REGISTRO)
        INTO MI_CONSECUTIVO
        FROM TEMP_CIERRE_PPTAL;
        /*
        * PARA EL CIERRE DE VIGENCIAS FUTURAS SE TIENE EN CUENTA
        * Si el año de la vigencia es igual al año de cierre +1 creo apropiacion inicial y movimientos, cambio digito
        * Si el año de la vigencia es menor al año de cierre +1 copio con arbol y no cambio el digito        
        */
        IF UN_TIPOCIERRE ='VF' AND RS.CLASERESERVA='PASAIGUAL' AND RS.TIPOVIGENCIAINICI ='VT' THEN
            MI_VIG_FUTURA:= -1;
        ELSE 
            MI_VIG_FUTURA:= 0;
        END IF;
        MI_CONDIC_VIG_FUT:=' (0<>' || MI_VIG_FUTURA || ' AND PP.VIGENCIA = PP.ANO + 1) OR 0=' || MI_VIG_FUTURA;
        MI_CONSULTA:= 'SELECT ''' || MI_CLASE || ''' CLASECIERRE
                              ,ROWNUM + ' || MI_CONSECUTIVO || ' REGISTRO  
                              ,DP.COMPANIA
                              ,''' || UN_TIPOCIERRE    || ''' TIPOCIERRE
                              ,''' || RS.CLASERESERVA  || ''' CLASERESERVA
                              ,'   || RS.CREAJERARQUIA || '   CREAJERARQUIA
                              ,DP.ANO             ANO
                              ,RES_DET.TIPO_CPTE_AFECT     DIS_TIPO_INICIAL 
                              ,''' || RS.TIPO_DIS || '''   DIS_TIPO_FINAL
                              ,RES_DET.CMPTE_AFECTADO      DIS_NUMERO 
                              ,RES_DET.CONSECUTIVOPPTO     DIS_CONSECUTIVO                          
                              ,DP.TIPO_CPTE_AFECT          RES_TIPO_INICIAL 
                              ,''' || RS.TIPO_RES || '''   RES_TIPO_FINAL
                              ,DP.CMPTE_AFECTADO           RES_NUMERO 
                              ,DP.CONSECUTIVOPPTO          RES_CONSECUTIVO                          
                              ,DP.TIPO_CPTE                REO_TIPO_INICIAL
                              ,''' || RS.TIPO_REO || '''   REO_TIPO_FINAL
                              ,DP.COMPROBANTE              REO_NUMERO 
                              ,DP.CONSECUTIVO              REO_CONSECUTIVO
                              ,CASE WHEN ' || MI_CONDIC_VIG_FUT || '
                                    THEN ' || CASE WHEN RS.DIGITOSCAMBIO IS NULL THEN ' DP.CUENTA '  ELSE CHR(39) || RS.DIGITOSCAMBIO || CHR(39) || ' || SUBSTR(DP.CUENTA,2 , LENGTH(DP.CUENTA)) ' END || 
                                  ' ELSE DP.CUENTA 
                                    END CUENTA
                              ,DP.CUENTA CUENTAANT
                              ,CASE WHEN ' || MI_CONDIC_VIG_FUT || '
                                    THEN ' || CASE WHEN RS.DIGITOSCAMBIO IS NULL THEN ' NULL '  ELSE CHR(39) || RS.DIGITOSCAMBIO     || CHR(39) END || 
                                  ' ELSE SUBSTR(DP.CUENTA,1,1) 
                                    END DIGITOS
                              ,CASE WHEN ' || MI_CONDIC_VIG_FUT || '
                                    THEN ' || CASE WHEN RS.TIPOVIGENCIAFINAL IS NULL AND UN_TIPOCIERRE = 'REG' THEN ' PP.TIPOVIGENCIA ' ELSE CHR(39) || RS.TIPOVIGENCIAFINAL || CHR(39) END || 
                                  ' ELSE ' || CASE WHEN RS.TIPOVIGENCIAINICI IS NULL AND UN_TIPOCIERRE = 'REG' THEN ' PP.TIPOVIGENCIA ' ELSE CHR(39) || RS.TIPOVIGENCIAINICI || CHR(39) END || ' END TIPOVIGENCIA
                              ,PP.VIGENCIA
                              ,DP.NATURALEZA
                              ,DIS.CENTRO_COSTO        DIS_CENTRO_COSTO
                              ,DIS.AUXILIAR            DIS_AUXILIAR
                              ,DIS.FUENTE_RECURSO      DIS_FUENTE_RECURSO
                              ,DIS.REFERENCIA          DIS_REFERENCIA     
                              ,DIS.TERCERO             DIS_TERCERO
                              ,DIS.SUCURSAL            DIS_SUCURSAL
                              ,DIS.DESCRIPCION         DIS_DESCRIPCION
                              ,DIS.DESTINO             DIS_DESTINO
                              ,NVL(DIS.TEXTO,''.'')    DIS_TEXTO 
                              ,DIS.NRO_DOCUMENTO       DIS_NRO_DOCUMENTO
                              ,DIS.TIPOCONTRATO        DIS_TIPOCONTRATO
                              ,DIS.NUMEROCONTRATO      DIS_NUMEROCONTRATO                           
                              ,DIS.CODSOLICITANTE      DIS_CODSOLICITANTE
                              ,DIS.SUCSOLICITANTE      DIS_SUCSOLICITANTE
                              ,DIS.COD_PROYECTO_PPTAL  DIS_COD_PROYECTO_PPTAL
                              ,DIS.ANOPROYECTO         DIS_ANOPROYECTO
                              ,DIS.LUGAR               DIS_LUGAR
                              ,DIS.FUENTE_FINANCIACION DIS_FUENTE_FINANCIACION
                              ,DIS.ASIGNACION          DIS_ASIGNACION              
                              ,DIS.DEPENDENCIA         DIS_DEPENDENCIA                          
                              ,DIS_DET.CENTRO_COSTO    DIS_DET_CENTRO_COSTO
                              ,DIS_DET.AUXILIAR        DIS_DET_AUXILIAR
                              ,DIS_DET.FUENTE_RECURSO  DIS_DET_FUENTE_RECURSO
                              ,DIS_DET.REFERENCIA      DIS_DET_REFERENCIA
                              ,DIS_DET.TERCERO         DIS_DET_TERCERO
                              ,DIS_DET.SUCURSAL        DIS_DET_SUCURSAL
                              ,DIS_DET.DESCRIPCION     DIS_DET_DESCRIPCION
                              ,DIS_DET.NRO_DOCUMENTO   DIS_DET_NRO_DOCUMENTO                          
                              ,DIS_DET.TIPOT           DIS_DET_TIPOT        
                              ,DIS_DET.CLASET          DIS_DET_CLASET                             
                              ,DIS_DET.CMPTE_SOLICI_AFECTADO DIS_DET_CMPTE_SOLICI_AFECTADO
                              ,DIS_DET.DEPENDENCIA     DIS_DET_DEPENDENCIA 
                              ,DIS_DET.NUMERO_SOLICITUD DIS_DET_NUMERO_SOLICITUD
                              ,DIS_DET.TIPO_SOLICITUD  DIS_DET_TIPO_SOLICITUD                          
                              ,DIS_DET.TIPOCONTRATO    DIS_DET_TIPOCONTRATO
                              ,DIS_DET.NUMEROCONTRATO  DIS_DET_NUMEROCONTRATO
                              ,RES.CENTRO_COSTO        RES_CENTRO_COSTO
                              ,RES.AUXILIAR            RES_AUXILIAR
                              ,RES.FUENTE_RECURSO      RES_FUENTE_RECURSO
                              ,RES.REFERENCIA          RES_REFERENCIA     
                              ,RES.TERCERO             RES_TERCERO
                              ,RES.SUCURSAL            RES_SUCURSAL
                              ,RES.DESCRIPCION         RES_DESCRIPCION
                              ,RES.DESTINO             RES_DESTINO
                              ,NVL(RES.TEXTO,''.'')    RES_TEXTO 
                              ,RES.NRO_DOCUMENTO       RES_NRO_DOCUMENTO
                              ,RES.TIPOCONTRATO        RES_TIPOCONTRATO
                              ,RES.NUMEROCONTRATO      RES_NUMEROCONTRATO                           
                              ,RES.CODSOLICITANTE      RES_CODSOLICITANTE
                              ,RES.SUCSOLICITANTE      RES_SUCSOLICITANTE
                              ,RES.COD_PROYECTO_PPTAL  RES_COD_PROYECTO_PPTAL
                              ,RES.ANOPROYECTO         RES_ANOPROYECTO
                              ,RES.LUGAR               RES_LUGAR
                              ,RES.FUENTE_FINANCIACION RES_FUENTE_FINANCIACION
                              ,RES.ASIGNACION          RES_ASIGNACION              
                              ,RES.DEPENDENCIA         RES_DEPENDENCIA                          
                              ,RES_DET.CENTRO_COSTO    RES_DET_CENTRO_COSTO
                              ,RES_DET.AUXILIAR        RES_DET_AUXILIAR
                              ,RES_DET.FUENTE_RECURSO  RES_DET_FUENTE_RECURSO
                              ,RES_DET.REFERENCIA      RES_DET_REFERENCIA
                              ,RES_DET.TERCERO         RES_DET_TERCERO
                              ,RES_DET.SUCURSAL        RES_DET_SUCURSAL
                              ,RES_DET.DESCRIPCION     RES_DET_DESCRIPCION
                              ,RES_DET.NRO_DOCUMENTO   RES_DET_NRO_DOCUMENTO                          
                              ,RES_DET.TIPOT           RES_DET_TIPOT        
                              ,RES_DET.CLASET          RES_DET_CLASET                             
                              ,RES_DET.CMPTE_SOLICI_AFECTADO RES_DET_CMPTE_SOLICI_AFECTADO
                              ,RES_DET.DEPENDENCIA     RES_DET_DEPENDENCIA 
                              ,RES_DET.NUMERO_SOLICITUD RES_DET_NUMERO_SOLICITUD
                              ,RES_DET.TIPO_SOLICITUD  RES_DET_TIPO_SOLICITUD                          
                              ,RES_DET.TIPOCONTRATO    RES_DET_TIPOCONTRATO
                              ,RES_DET.NUMEROCONTRATO  RES_DET_NUMEROCONTRATO                          
                              ,REO.CENTRO_COSTO        REO_CENTRO_COSTO
                              ,REO.AUXILIAR            REO_AUXILIAR
                              ,REO.FUENTE_RECURSO      REO_FUENTE_RECURSO
                              ,REO.REFERENCIA          REO_REFERENCIA     
                              ,REO.TERCERO             REO_TERCERO
                              ,REO.SUCURSAL            REO_SUCURSAL
                              ,REO.DESCRIPCION         REO_DESCRIPCION
                              ,REO.DESTINO             REO_DESTINO
                              ,NVL(REO.TEXTO,''.'')    REO_TEXTO 
                              ,REO.NRO_DOCUMENTO       REO_NRO_DOCUMENTO
                              ,REO.TIPOCONTRATO        REO_TIPOCONTRATO
                              ,REO.NUMEROCONTRATO      REO_NUMEROCONTRATO                           
                              ,REO.CODSOLICITANTE      REO_CODSOLICITANTE
                              ,REO.SUCSOLICITANTE      REO_SUCSOLICITANTE
                              ,REO.COD_PROYECTO_PPTAL  REO_COD_PROYECTO_PPTAL
                              ,REO.ANOPROYECTO         REO_ANOPROYECTO
                              ,REO.LUGAR               REO_LUGAR
                              ,REO.FUENTE_FINANCIACION REO_FUENTE_FINANCIACION
                              ,REO.ASIGNACION          REO_ASIGNACION              
                              ,REO.DEPENDENCIA         REO_DEPENDENCIA                          
                              ,DP.CENTRO_COSTO         REO_DET_CENTRO_COSTO
                              ,DP.AUXILIAR             REO_DET_AUXILIAR
                              ,DP.FUENTE_RECURSO       REO_DET_FUENTE_RECURSO
                              ,DP.REFERENCIA           REO_DET_REFERENCIA
                              ,DP.TERCERO              REO_DET_TERCERO
                              ,DP.SUCURSAL             REO_DET_SUCURSAL
                              ,DP.DESCRIPCION          REO_DET_DESCRIPCION
                              ,DP.NRO_DOCUMENTO        REO_DET_NRO_DOCUMENTO                          
                              ,DP.TIPOT                REO_DET_TIPOT        
                              ,DP.CLASET               REO_DET_CLASET 
                              ,DP.CMPTE_SOLICI_AFECTADO REO_DET_CMPTE_SOLICI_AFECTADO
                              ,DP.DEPENDENCIA          REO_DET_DEPENDENCIA 
                              ,DP.NUMERO_SOLICITUD     REO_DET_NUMERO_SOLICITUD
                              ,DP.TIPO_SOLICITUD       REO_DET_TIPO_SOLICITUD                          
                              ,DP.TIPOCONTRATO         REO_DET_TIPOCONTRATO
                              ,DP.NUMEROCONTRATO       REO_DET_NUMEROCONTRATO
                              ,((DP.VALOR_DEBITO        - DP.VALOR_CREDITO       )
                                  -(DP.DEBITO_AFECTADO     - DP.CREDITO_AFECTADO    )
                                  +(DP.MODIFICACION_DEBITO - DP.MODIFICACION_CREDITO)) AS VALOR_DEBITO
                              , ' || CASE WHEN RS.TIPO_ADI IS NULL THEN ' NULL ' ELSE '''' || RS.TIPO_ADI || '''' END || '    
                              ,DP.SECTOR
                              ,DP.PROGRAMA
                              ,DP.SUBPROGRAMA
                              ,DP.COD_PROD_CUIPO
                              ,DP.CODIGO_BPIN
                              ,DP.CODIGO_CCPET
                              ,DP.CODIGO_CPC
                              ,DP.CODIGOUNIDADEJE
                              ,DP.FUENTE_CUIPO
                              ,DP.CODIGOCCPETREGA
                              ,DP.POLITICA_PUBLICA
                              ,DP.DETALLE_SECTORIAL
                         FROM DETALLE_COMPROBANTE_PPTAL DP INNER JOIN PLAN_PRESUPUESTAL PP 
                           ON DP.COMPANIA = PP.COMPANIA 
                          AND DP.ANO      = PP.ANO 
                          AND DP.CUENTA   = PP.CODIGO
                         INNER JOIN TIPO_COMPROBPP TC 
                           ON DP.COMPANIA  = TC.COMPANIA 
                          AND DP.TIPO_CPTE = TC.CODIGO 
                         INNER JOIN COMPROBANTE_PPTAL REO
                           ON DP.COMPANIA        = REO.COMPANIA 
                          AND DP.ANO             = REO.ANO  
                          AND DP.TIPO_CPTE       = REO.TIPO 
                          AND DP.COMPROBANTE     = REO.NUMERO  
                         INNER JOIN DETALLE_COMPROBANTE_PPTAL RES_DET
                           ON DP.COMPANIA        = RES_DET.COMPANIA 
                          AND DP.ANO_AFECT       = RES_DET.ANO  
                          AND DP.TIPO_CPTE_AFECT = RES_DET.TIPO_CPTE 
                          AND DP.CMPTE_AFECTADO  = RES_DET.COMPROBANTE
                          AND DP.CONSECUTIVOPPTO = RES_DET.CONSECUTIVO
                         INNER JOIN COMPROBANTE_PPTAL RES
                           ON RES_DET.COMPANIA     = RES.COMPANIA 
                          AND RES_DET.ANO          = RES.ANO  
                          AND RES_DET.TIPO_CPTE    = RES.TIPO
                          AND RES_DET.COMPROBANTE  = RES.NUMERO
                         INNER JOIN DETALLE_COMPROBANTE_PPTAL DIS_DET
                           ON RES_DET.COMPANIA        = DIS_DET.COMPANIA 
                          AND RES_DET.ANO_AFECT       = DIS_DET.ANO  
                          AND RES_DET.TIPO_CPTE_AFECT = DIS_DET.TIPO_CPTE 
                          AND RES_DET.CMPTE_AFECTADO  = DIS_DET.COMPROBANTE
                          AND RES_DET.CONSECUTIVOPPTO = DIS_DET.CONSECUTIVO
                         INNER JOIN COMPROBANTE_PPTAL DIS
                           ON DIS_DET.COMPANIA    = DIS.COMPANIA 
                          AND DIS_DET.ANO         = DIS.ANO  
                          AND DIS_DET.TIPO_CPTE   = DIS.TIPO 
                          AND DIS_DET.COMPROBANTE = DIS.NUMERO
                        WHERE DP.COMPANIA   = ''' || UN_COMPANIA   || '''
                          AND DP.ANO        = '   || UN_ANOACIERRE || '
                          AND PP.NATURALEZA = ''D''' ||  
                          CASE WHEN UN_TIPOCIERRE = 'REG' AND RS.TIPOVIGENCIAINICI IS NULL THEN ' ' ELSE ' AND PP.TIPOVIGENCIA IN (''' || RS.TIPOVIGENCIAINICI || ''') ' END ||
                          CASE WHEN UN_TIPOCIERRE = 'REG' THEN ' AND PP.REGALIAS NOT IN(0) ' ELSE ' AND PP.REGALIAS IN(0) ' END || 
                          CASE WHEN UN_TIPOCIERRE = 'COF' THEN ' AND PP.COFINANCIADO NOT IN(0) ' ELSE ' AND PP.COFINANCIADO IN(0) ' END || '                          
                           AND TC.CLASE IN (''' || MI_CLASE || ''') 
                           AND ((DP.VALOR_DEBITO        - DP.VALOR_CREDITO       )
                              - (DP.DEBITO_AFECTADO     - DP.CREDITO_AFECTADO    )
                              + (DP.MODIFICACION_DEBITO - DP.MODIFICACION_CREDITO))>0';

        MI_CAMPOS := '    CLASECIERRE
                        , REGISTRO
                        , COMPANIA
                        , TIPOCIERRE
                        , CLASERESERVA
                        , CREAJERARQUIA
                        , ANO
                        , DIS_TIPO_INICIAL
                        , DIS_TIPO_FINAL
                        , DIS_NUMERO
                        , DIS_CONSECUTIVO
                        , RES_TIPO_INICIAL
                        , RES_TIPO_FINAL
                        , RES_NUMERO
                        , RES_CONSECUTIVO
                        , REO_TIPO_INICIAL
                        , REO_TIPO_FINAL
                        , REO_NUMERO 
                        , REO_CONSECUTIVO                          
                        , CUENTA
                        , CUENTAANT
                        , DIGITOCAMBIO
                        , TIPOVIGENCIA  
                        , VIGENCIA
                        , NATURALEZA
                        , DIS_CENTRO_COSTO
                        , DIS_AUXILIAR
                        , DIS_FUENTE_RECURSO
                        , DIS_REFERENCIA
                        , DIS_TERCERO
                        , DIS_SUCURSAL
                        , DIS_DESCRIPCION
                        , DIS_DESTINO
                        , DIS_TEXTO
                        , DIS_NRO_DOCUMENTO
                        , DIS_TIPOCONTRATO
                        , DIS_NUMEROCONTRATO
                        , DIS_CODSOLICITANTE
                        , DIS_SUCSOLICITANTE
                        , DIS_COD_PROYECTO_PPTAL
                        , DIS_ANOPROYECTO
                        , DIS_LUGAR
                        , DIS_FUENTE_FINANCIACION
                        , DIS_ASIGNACION
                        , DIS_DEPENDENCIA                    
                        , DIS_DET_CENTRO_COSTO
                        , DIS_DET_AUXILIAR
                        , DIS_DET_FUENTE_RECURSO
                        , DIS_DET_REFERENCIA
                        , DIS_DET_TERCERO
                        , DIS_DET_SUCURSAL
                        , DIS_DET_DESCRIPCION
                        , DIS_DET_NRO_DOCUMENTO                    
                        , DIS_DET_TIPOT
                        , DIS_DET_CLASET
                        , DIS_DET_CMPTE_SOLICI_AFECTADO
                        , DIS_DET_DEPENDENCIA     
                        , DIS_DET_NUMERO_SOLICITUD 
                        , DIS_DET_TIPO_SOLICITUD 
                        , DIS_DET_TIPOCONTRATO   
                        , DIS_DET_NUMEROCONTRATO 
                        , RES_CENTRO_COSTO
                        , RES_AUXILIAR
                        , RES_FUENTE_RECURSO
                        , RES_REFERENCIA
                        , RES_TERCERO
                        , RES_SUCURSAL
                        , RES_DESCRIPCION
                        , RES_DESTINO
                        , RES_TEXTO
                        , RES_NRO_DOCUMENTO
                        , RES_TIPOCONTRATO
                        , RES_NUMEROCONTRATO
                        , RES_CODSOLICITANTE
                        , RES_SUCSOLICITANTE
                        , RES_COD_PROYECTO_PPTAL
                        , RES_ANOPROYECTO
                        , RES_LUGAR
                        , RES_FUENTE_FINANCIACION
                        , RES_ASIGNACION
                        , RES_DEPENDENCIA                    
                        , RES_DET_CENTRO_COSTO
                        , RES_DET_AUXILIAR
                        , RES_DET_FUENTE_RECURSO
                        , RES_DET_REFERENCIA
                        , RES_DET_TERCERO
                        , RES_DET_SUCURSAL
                        , RES_DET_DESCRIPCION
                        , RES_DET_NRO_DOCUMENTO                    
                        , RES_DET_TIPOT
                        , RES_DET_CLASET
                        , RES_DET_CMPTE_SOLICI_AFECTADO
                        , RES_DET_DEPENDENCIA     
                        , RES_DET_NUMERO_SOLICITUD 
                        , RES_DET_TIPO_SOLICITUD 
                        , RES_DET_TIPOCONTRATO   
                        , RES_DET_NUMEROCONTRATO
                        , REO_CENTRO_COSTO
                        , REO_AUXILIAR
                        , REO_FUENTE_RECURSO
                        , REO_REFERENCIA
                        , REO_TERCERO
                        , REO_SUCURSAL
                        , REO_DESCRIPCION
                        , REO_DESTINO
                        , REO_TEXTO
                        , REO_NRO_DOCUMENTO
                        , REO_TIPOCONTRATO
                        , REO_NUMEROCONTRATO
                        , REO_CODSOLICITANTE
                        , REO_SUCSOLICITANTE
                        , REO_COD_PROYECTO_PPTAL
                        , REO_ANOPROYECTO
                        , REO_LUGAR
                        , REO_FUENTE_FINANCIACION
                        , REO_ASIGNACION
                        , REO_DEPENDENCIA                    
                        , REO_DET_CENTRO_COSTO
                        , REO_DET_AUXILIAR
                        , REO_DET_FUENTE_RECURSO
                        , REO_DET_REFERENCIA
                        , REO_DET_TERCERO
                        , REO_DET_SUCURSAL
                        , REO_DET_DESCRIPCION
                        , REO_DET_NRO_DOCUMENTO                    
                        , REO_DET_TIPOT
                        , REO_DET_CLASET
                        , REO_DET_CMPTE_SOLICI_AFECTADO
                        , REO_DET_DEPENDENCIA     
                        , REO_DET_NUMERO_SOLICITUD 
                        , REO_DET_TIPO_SOLICITUD 
                        , REO_DET_TIPOCONTRATO   
                        , REO_DET_NUMEROCONTRATO
                        , VALOR_DEBITO
                        , TIPO_ADI
                        ,SECTOR
                        ,PROGRAMA
                        ,SUBPROGRAMA
                        ,COD_PROD_CUIPO
                        ,CODIGO_BPIN
                        ,CODIGO_CCPET
                        ,CODIGO_CPC
                        ,CODIGOUNIDADEJE
                        ,FUENTE_CUIPO
                        ,CODIGOCCPETREGA
                        ,POLITICA_PUBLICA
                        ,DETALLE_SECTORIAL
                        ';   
        BEGIN
            BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'TEMP_CIERRE_PPTAL', 
                                                       UN_ACCION  => 'IS', 
                                                       UN_CAMPOS  => MI_CAMPOS, 
                                                       UN_VALORES => MI_CONSULTA);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
            MI_REEMPLAZOS(0).CLAVE := 'COMPANIA';
            MI_REEMPLAZOS(0).VALOR := UN_COMPANIA;
            MI_REEMPLAZOS(1).CLAVE := 'ANIO';
            MI_REEMPLAZOS(1).VALOR := UN_ANOACIERRE;        
            MI_REEMPLAZOS(2).CLAVE := 'CLASE';
            MI_REEMPLAZOS(2).VALOR := MI_CLASE;  
            MI_REEMPLAZOS(3).CLAVE := 'DIGITO';
            MI_REEMPLAZOS(3).VALOR := RS.DIGITOSCAMBIO;
            MI_REEMPLAZOS(4).CLAVE := 'TIPOCIERRE';
            MI_REEMPLAZOS(4).VALOR := FC_NOMBRETIPOCIERRE(UN_TIPOCIERRE);       
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOERRORINSERTTEMPORAL,
              UN_TABLAERROR => 'TEMP_CIERRE_PPTAL',
              UN_REEMPLAZOS => MI_REEMPLAZOS  
            );  
        END;
        IF RS.RECLASIFICAR NOT IN(0) THEN
            PR_RECLASIFICACION(UN_COMPANIA     => UN_COMPANIA,
                               UN_CLASERESERVA => RS.CLASERESERVA,
                               UN_TIPOCIERRE   => UN_TIPOCIERRE,
                               UN_CLASECIERRE  => MI_CLASE,
                               UN_PREFIJO      =>'DIS');
            PR_RECLASIFICACION(UN_COMPANIA     => UN_COMPANIA,
                               UN_CLASERESERVA => RS.CLASERESERVA,
                               UN_TIPOCIERRE   => UN_TIPOCIERRE,
                               UN_CLASECIERRE  => MI_CLASE,
                               UN_PREFIJO      =>'RES');
            PR_RECLASIFICACION(UN_COMPANIA     => UN_COMPANIA,
                               UN_CLASERESERVA => RS.CLASERESERVA,
                               UN_TIPOCIERRE   => UN_TIPOCIERRE,
                               UN_CLASECIERRE  => MI_CLASE,
                               UN_PREFIJO      =>'REO');
        END IF;
    END LOOP RECORRETIPOCIERRE;

    --SE VALIDA SI EXISTEN COMPROBANTES DUPLICADOS PARA DEJAR EL MISMO TIPO DE COMPROBANTE DEL AÑO PASADO
    PR_ACTUALIZARTIPOFINAL(UN_COMPANIA    => UN_COMPANIA,  
                           UN_PREFIJO     => 'DIS',
                           UN_CLASECIERRE => MI_CLASE);
    PR_ACTUALIZARTIPOFINAL(UN_COMPANIA    => UN_COMPANIA,  
                           UN_PREFIJO     => 'RES',
                           UN_CLASECIERRE => MI_CLASE);   
    PR_ACTUALIZARTIPOFINAL(UN_COMPANIA    => UN_COMPANIA,  
                           UN_PREFIJO     => 'REO',
                           UN_CLASECIERRE => MI_CLASE); 
    RETURN MI_CLASE;                        
END FC_PREPARA_APROP_CAJA;

PROCEDURE PR_INSERTACIERREPLAN
    /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 03/12/2018
        TIME              : 08:04 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Esta consulta se realiza para que crear los registros de plan presupuestal 
                            y ademas para generar la consulta de los datos a generar
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:insertaPlanCierre
        @METHOD:Post
    */ 
    (
     UN_COMPANIA        IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE      IN  PCK_SUBTIPOS.TI_ANIO,
     UN_USUARIO         IN  PCK_SUBTIPOS.TI_USUARIO 
    ) 
    AS
    MI_CONSULTA      VARCHAR2(32000); 
    MI_CONSULTAAGR   VARCHAR2(32000); 
    MI_ANOFINAL      PCK_SUBTIPOS.TI_ANIO;
    MI_REEMPLAZOS    PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_RS            SYS_REFCURSOR;
    MI_RUBRO         VARCHAR2(32);
    MI_RUBRO_PADRE   VARCHAR2(32);
    MI_RTA CLOB;
    MI_TABLA        PCK_SUBTIPOS.TI_TABLA;

BEGIN
    MI_TABLA :='PLAN_PRESUPUESTAL';
    MI_RTA:=' ';
    MI_ANOFINAL   := UN_ANOACIERRE + 1;
    MI_CONSULTA := FC_CONSULTAPLANCIERRE(UN_COMPANIA    => UN_COMPANIA,  
                                         UN_ANOACIERRE  => UN_ANOACIERRE
                                        );
    MI_CONSULTA :=  MI_CONSULTA || ' AND CONF.RECLASIFICAR IN(0) ';
    /*
    * VALIDAR QUE POR CUESTIONES DEL ARBOL A GENERAR NO SE REPITAN LOS RUBROS
    */
    MI_CONSULTAAGR := ' SELECT CODIGO FROM (' || MI_CONSULTA ||' )  
                        GROUP BY COMPANIA, ANO, CODIGO
                        HAVING COUNT(CODIGO)>1';    
    OPEN MI_RS FOR MI_CONSULTAAGR;
    LOOP
        FETCH MI_RS
        INTO MI_RUBRO;
        EXIT WHEN MI_RS%NOTFOUND;
            MI_RTA := MI_RUBRO || '; ' ;
    END LOOP; 
    CLOSE MI_RS;
    IF MI_RTA<>' ' THEN
        BEGIN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
            MI_REEMPLAZOS(1).CLAVE := 'RUBRO';
            MI_REEMPLAZOS(1).VALOR := MI_RTA;         
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOVALIDARRUBROREPE,
              UN_TABLAERROR => MI_TABLA,
              UN_REEMPLAZOS => MI_REEMPLAZOS  
            );  
        END;
    END IF;

    /*
    * VALIDAR QUE LOS INDICADORES SEAN VALIDOS
    */
    MI_CONSULTAAGR := ' SELECT PADRE.CODIGO PADRES, HIJO.CODIGO HIJOS
                        FROM (' || MI_CONSULTA || ') PADRE INNER JOIN (' || MI_CONSULTA || ') HIJO
                          ON PADRE.COMPANIA     = HIJO.COMPANIA
                         AND PADRE.ANO          = HIJO.ANO 
                         AND PADRE.CODIGO       = SUBSTR(HIJO.CODIGO, 1, LENGTH(PADRE.CODIGO))
                        WHERE PADRE.COMPANIA = ''' || UN_COMPANIA || '''
                          AND PADRE.ANO      = '   || UN_ANOACIERRE || '
                          AND PADRE.CODIGO      <> HIJO.CODIGO
                          AND (HIJO.MOVIMIENTO  <> 0 
                            OR HIJO.MAN_CEN_CTO <> 0 
                            OR HIJO.MAN_AUX_TER <> 0 
                            OR HIJO.MAN_AUX_GEN <> 0 
                            OR HIJO.MAN_AUX_REF <> 0
                            OR HIJO.MAN_AUX_FUE <> 0
                            )
                          AND (PADRE.MOVIMIENTO  <> 0 
                            OR PADRE.MAN_CEN_CTO <> 0 
                            OR PADRE.MAN_AUX_TER <> 0 
                            OR PADRE.MAN_AUX_GEN <> 0 
                            OR PADRE.MAN_AUX_REF <> 0
                            OR PADRE.MAN_AUX_FUE <> 0
                            )
                        ORDER BY PADRE.CODIGO, HIJO.CODIGO ';

    OPEN MI_RS FOR MI_CONSULTAAGR;
    LOOP
        FETCH MI_RS
        INTO MI_RUBRO_PADRE, MI_RUBRO;
        EXIT WHEN MI_RS%NOTFOUND;
            MI_RTA := MI_RTA || 'Padre ' || MI_RUBRO || ', Hijo' || CHR(10) || CHR(13);
    END LOOP; 
    CLOSE MI_RS;

    IF MI_RTA<>' ' THEN
        BEGIN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
            MI_REEMPLAZOS(1).CLAVE := 'RUBRO';
            MI_REEMPLAZOS(1).VALOR := MI_RTA;         
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOVALIDARRUBROINDICA,
              UN_TABLAERROR => MI_TABLA,
              UN_REEMPLAZOS => MI_REEMPLAZOS  
            );  
        END;
    END IF;

    BEGIN
        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA, 
                                                   UN_ACCION  => 'IS', 
                                                   UN_CAMPOS  => MI_CAMPOSPLANFIN, 
                                                   UN_VALORES => 'SELECT ' || MI_CAMPOSPLANFIN || ' FROM (' || MI_CONSULTA || ') WHERE MOVIMIENTO  = 0 
                                                                                                            AND MAN_CEN_CTO = 0 
                                                                                                            AND MAN_AUX_TER = 0 
                                                                                                            AND MAN_AUX_GEN = 0 
                                                                                                            AND MAN_AUX_REF = 0
                                                                                                            AND MAN_AUX_FUE = 0
                                                                                                             ');
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => MI_TABLA, 
                                                   UN_ACCION  => 'IS', 
                                                   UN_CAMPOS  => MI_CAMPOSPLANFIN, 
                                                   UN_VALORES => 'SELECT ' || MI_CAMPOSPLANFIN || ' FROM (' || MI_CONSULTA || ') WHERE MOVIMIENTO  <> 0 
                                                                                                            OR MAN_CEN_CTO <> 0 
                                                                                                            OR MAN_AUX_TER <> 0 
                                                                                                            OR MAN_AUX_GEN <> 0 
                                                                                                            OR MAN_AUX_REF <> 0
                                                                                                            OR MAN_AUX_FUE <> 0
                                                                                                             ');
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        MI_REEMPLAZOS(1).CLAVE := 'COMPANIA';
        MI_REEMPLAZOS(1).VALOR := UN_COMPANIA;
        MI_REEMPLAZOS(2).CLAVE := 'ANIO';
        MI_REEMPLAZOS(2).VALOR := MI_ANOFINAL;         
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOERRORINSERTPLAN,
          UN_TABLAERROR => MI_TABLA,
          UN_REEMPLAZOS => MI_REEMPLAZOS  
        );  
    END;

END PR_INSERTACIERREPLAN; 

PROCEDURE PR_INSERTARAPROPIACIONCIERRE
    /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 03/12/2018
        TIME              : 08:04 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Inserta las apropiaciones iniciales de acuerdo a los registros que se cierran
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME: insertarApropiacionCierre
        @METHOD:Post
    */ 
    (
     UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE     IN  PCK_SUBTIPOS.TI_ANIO,
     UN_USUARIO        IN  PCK_SUBTIPOS.TI_USUARIO 
    ) AS 
    MI_CONSULTA CLOB; 
    MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
    MI_ANOACTUAL    PCK_SUBTIPOS.TI_ANIO;    
    MI_MERGEUSING   PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE  PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE  PCK_SUBTIPOS.TI_MERGEEXISTE;
BEGIN    
    --Se eliminan las apropiaciones iniciales de acuerdo al digito nuevo de presupuesto
    MI_ANOACTUAL := UN_ANOACIERRE +1;
    BEGIN
        BEGIN
            MI_TABLA     := 'APROPIACIONESINICIALES';
            /*
            MI_CONDICION := ' COMPANIA    = ''' || UN_COMPANIA       || '''
                          AND ANO         = '   || MI_ANOACTUAL      || '
                          AND SUBSTR(CODIGO,1,' || LENGTH(UN_DIGITO) || ') IN('''|| UN_DIGITO ||''')';
            PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME( UN_TABLA => MI_TABLA,
                                                  UN_ACCION     => 'E',
                                                  UN_CONDICION => MI_CONDICION);
        */
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
        PCK_ERR_MSG.RAISE_WITH_MSG
            ( UN_EXC_COD    => SQLCODE
            , UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOERRORDELETEAPROPIA
            , UN_TABLAERROR => MI_TABLA);
    END;
    --SE DEJAN POR DEFECTO EL TERCERO VARIOS, DEBIDO A 
    --QUE EL SALDO DE PRESUPUESTO NO SE MANEJA POR TERCERO
    MI_CONSULTA := 'SELECT COMPANIA, 
                           ' || MI_ANOACTUAL || ' ANO, 
                           CUENTA CODIGO, 
                           DIS_DET_TERCERO         TERCERO, 
                           DIS_DET_SUCURSAL        SUCURSAL, 
                           DIS_DET_AUXILIAR        AUXILIAR, 
                           DIS_DET_CENTRO_COSTO    CENTRO_COSTO, 
                           DIS_DET_REFERENCIA      REFERENCIA, 
                           DIS_DET_FUENTE_RECURSO  FUENTE_RECURSO, 
                           SUM(VALOR_DEBITO) APROPIACIONINICIAL
                    FROM TEMP_CIERRE_PPTAL
                    WHERE COMPANIA   = '''|| UN_COMPANIA   ||'''
                      AND ANO        = '  || UN_ANOACIERRE ||' 
                      AND CLASECIERRE IN(''RES'',''REO'',''SAL'')
                      AND TIPO_ADI IS NULL
                      AND TIPOCIERRE NOT IN (''DCO'')
                    GROUP BY COMPANIA, 
                             ANO, 
                             CUENTA, 
                             DIS_DET_TERCERO, 
                             DIS_DET_SUCURSAL, 
                             DIS_DET_AUXILIAR, 
                             DIS_DET_CENTRO_COSTO, 
                             DIS_DET_REFERENCIA, 
                             DIS_DET_FUENTE_RECURSO';
    MI_TABLA     := 'APROPIACIONESINICIALES';
    BEGIN
        BEGIN
            PCK_DATOS.GL_RTA :=  PCK_DATOS.FC_ACME(
                             UN_TABLA       => MI_TABLA,
                             UN_ACCION      => 'IM',
                             UN_MERGEUSING  => MI_CONSULTA,
                             UN_MERGEENLACE => '    TABLA.COMPANIA       = VISTA.COMPANIA
                                                AND TABLA.ANO            = VISTA.ANO 
                                                AND TABLA.CODIGO         = VISTA.CODIGO
                                                AND TABLA.TERCERO        = VISTA.TERCERO
                                                AND TABLA.SUCURSAL       = VISTA.SUCURSAL
                                                AND TABLA.AUXILIAR       = VISTA.AUXILIAR
                                                AND TABLA.CENTRO_COSTO   = VISTA.CENTRO_COSTO
                                                AND TABLA.REFERENCIA     = VISTA.REFERENCIA
                                                AND TABLA.FUENTE_RECURSO = VISTA.FUENTE_RECURSO',
                             UN_MERGEEXISTE => 'UPDATE 
                                                  SET TABLA.APROPIACIONINICIAL = VISTA.APROPIACIONINICIAL,
                                                      TABLA.DATE_MODIFIED      = SYSDATE, 
                                                      TABLA.MODIFIED_BY        = ''' || UN_USUARIO || '''', 
                             UN_MERGENOEXIS => 'INSERT (COMPANIA, 
                                                        ANO, 
                                                        CODIGO, 
                                                        TERCERO, 
                                                        SUCURSAL, 
                                                        AUXILIAR, 
                                                        CENTRO_COSTO, 
                                                        REFERENCIA, 
                                                        FUENTE_RECURSO,
                                                        APROPIACIONINICIAL,
                                                        DATE_CREATED,
                                                        CREATED_BY)
                                                        VALUES(VISTA.COMPANIA, 
                                                               VISTA.ANO, 
                                                               VISTA.CODIGO, 
                                                               VISTA.TERCERO, 
                                                               VISTA.SUCURSAL, 
                                                               VISTA.AUXILIAR, 
                                                               VISTA.CENTRO_COSTO, 
                                                               VISTA.REFERENCIA, 
                                                               VISTA.FUENTE_RECURSO,
                                                               VISTA.APROPIACIONINICIAL,
                                                               SYSDATE,
                                                               ''' || UN_USUARIO || ''')');                      
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        MI_REEMPLAZOS(1).CLAVE := 'COMPANIA';
        MI_REEMPLAZOS(1).VALOR := UN_COMPANIA;
        MI_REEMPLAZOS(2).CLAVE := 'ANIO';
        MI_REEMPLAZOS(2).VALOR := UN_ANOACIERRE;        
        PCK_ERR_MSG.RAISE_WITH_MSG(
                                  UN_EXC_COD    => SQLCODE,
                                  UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOERRORINSERAPROPIA,
                                  UN_TABLAERROR => MI_TABLA,
                                  UN_REEMPLAZOS => MI_REEMPLAZOS  
        );
    END;
    
    --CC_2934 GROJAS: ACTUALIZA LOS CLASIFICADORES DE CUIPO DE ACUERDO A LAS APROPIACIONES
    
    BEGIN
    MI_TABLA := 'APROPIACIONESINICIALES';

    MI_MERGEUSING := 'SELECT APROPIACIONESINICIALES.COMPANIA, TEMP.ANO, APROPIACIONESINICIALES.CODIGO, TEMP.CUENTA, APROPIACIONESINICIALES.AUXILIAR, 
                            APROPIACIONESINICIALES.CENTRO_COSTO, APROPIACIONESINICIALES.REFERENCIA, APROPIACIONESINICIALES.FUENTE_RECURSO, 
                            APROPIACIONESINICIALES.CODIGO_CCPET, APROPIACIONESINICIALES.PROGRAMA, APROPIACIONESINICIALES.CODIGOUNIDADEJE, APROPIACIONESINICIALES.CODIGOBPIN, 
                            APROPIACIONESINICIALES.RECURSO_SGR, APROPIACIONESINICIALES.SECTOR, APROPIACIONESINICIALES.DETALLESECTORIAL 
                            FROM APROPIACIONESINICIALES
                            INNER JOIN (
                            SELECT A.COMPANIA, A.ANO, T.VIGENCIA, T.CUENTA, T.CUENTAANT, A.AUXILIAR, A.CENTRO_COSTO,A.REFERENCIA, A.FUENTE_RECURSO
                            FROM APROPIACIONESINICIALES A
                            INNER JOIN TEMP_CIERRE_PPTAL T
                            ON A.COMPANIA = T.COMPANIA
                            AND ' || UN_ANOACIERRE ||' = T.ANO
                            AND A.CODIGO = T.CUENTA
                            AND A.AUXILIAR = T.DIS_DET_AUXILIAR
                            AND A.CENTRO_COSTO = T.DIS_DET_CENTRO_COSTO
                            AND A.REFERENCIA = T.DIS_DET_REFERENCIA
                            AND A.FUENTE_RECURSO = T.DIS_DET_FUENTE_RECURSO
                            WHERE A.COMPANIA = '''|| UN_COMPANIA   ||'''
                            AND A.ANO = ' || MI_ANOACTUAL || '
                            GROUP BY A.COMPANIA, A.ANO,T.VIGENCIA, T.CUENTA, T.CUENTAANT, A.AUXILIAR, A.CENTRO_COSTO,A.REFERENCIA, A.FUENTE_RECURSO) TEMP
                            ON APROPIACIONESINICIALES.COMPANIA = TEMP.COMPANIA
                            AND APROPIACIONESINICIALES.ANO = TEMP.VIGENCIA
                            AND APROPIACIONESINICIALES.CODIGO = TEMP.CUENTAANT
                            AND APROPIACIONESINICIALES.AUXILIAR = TEMP.AUXILIAR
                            AND APROPIACIONESINICIALES.CENTRO_COSTO = TEMP.CENTRO_COSTO
                            AND APROPIACIONESINICIALES.REFERENCIA = TEMP.REFERENCIA
                            AND APROPIACIONESINICIALES.FUENTE_RECURSO = TEMP.FUENTE_RECURSO';
    
    MI_MERGEENLACE:='TABLA.COMPANIA = VISTA.COMPANIA
                    AND TABLA.ANO = VISTA.ANO
                    AND TABLA.CODIGO = VISTA.CUENTA
                    AND TABLA.AUXILIAR = VISTA.AUXILIAR
                    AND TABLA.CENTRO_COSTO = VISTA.CENTRO_COSTO
                    AND TABLA.REFERENCIA = VISTA.REFERENCIA
                    AND TABLA.FUENTE_RECURSO = VISTA.FUENTE_RECURSO';   
    
    MI_MERGEEXISTE := 'UPDATE SET TABLA.CODIGO_CCPET = VISTA.CODIGO_CCPET, 
                                TABLA.PROGRAMA = VISTA.PROGRAMA, 
                                TABLA.CODIGOUNIDADEJE = VISTA.CODIGOUNIDADEJE, 
                                TABLA.CODIGOBPIN = VISTA.CODIGOBPIN, 
                                TABLA.RECURSO_SGR = VISTA.RECURSO_SGR, 
                                TABLA.SECTOR = VISTA.SECTOR, 
                                TABLA.DETALLESECTORIAL = VISTA.DETALLESECTORIAL ';  

    BEGIN
    
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA
                                                     ,UN_ACCION => 'MM'
                                                     ,UN_MERGEUSING => MI_MERGEUSING
                                                     ,UN_MERGEENLACE => MI_MERGEENLACE
                                                     ,UN_MERGEEXISTE => MI_MERGEEXISTE);

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
            MI_REEMPLAZOS(1).CLAVE := 'COMPANIA';
            MI_REEMPLAZOS(1).VALOR := UN_COMPANIA;
            MI_REEMPLAZOS(2).CLAVE := 'ANIO';
            MI_REEMPLAZOS(2).VALOR := UN_ANOACIERRE;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                                      UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOERRORINSERAPROPIA,
                                      UN_TABLAERROR => MI_TABLA,
                                      UN_REEMPLAZOS => MI_REEMPLAZOS
            );
    END;
        
END PR_INSERTARAPROPIACIONCIERRE;  

PROCEDURE PR_INSERTARADICIONCIERRE
    /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 28/01/2019
        TIME              : 08:04 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Inserta comprobante de adiciones de los tipos configurados para las adiciones y nopara crear apropiacion
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME: insertarComprobanteAdicionCierre
        @METHOD:Post
    */ 
    (
     UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE     IN  PCK_SUBTIPOS.TI_ANIO,
     UN_FECHACIERRE    IN  DATE,
     UN_USUARIO        IN  PCK_SUBTIPOS.TI_USUARIO 
    ) AS 
    MI_CONSULTA CLOB; 
    MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
    MI_ANOACTUAL    PCK_SUBTIPOS.TI_ANIO;    
    MI_NUMERO       NUMBER(20,2);    

    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_DESCRIPCION        VARCHAR2(1000);
    MI_FECHACOMP          VARCHAR2(50);
    MI_FECHAVCNCOMP       VARCHAR2(50);
BEGIN    
    --Se eliminan las apropiaciones iniciales de acuerdo al digito nuevo de presupuesto
    MI_ANOACTUAL := UN_ANOACIERRE +1;
    MI_FECHACOMP    := 'TO_DATE(''' || UN_FECHACIERRE || ''',''DD/MM/YYYY'')';
    MI_FECHAVCNCOMP := 'TO_DATE(''' || ADD_MONTHS(UN_FECHACIERRE,1) || ''',''DD/MM/YYYY'')';
    <<RECORRERTIPOS>>
    FOR RS IN( SELECT COMPANIA, TIPOCIERRE, CLASERESERVA, TIPO_ADI
                FROM CONFIG_CIERRE_PPTAL
                WHERE COMPANIA = UN_COMPANIA
                 AND GENERAR     NOT IN(0)
                 AND TIPO_ADI IS NOT NULL)
    LOOP 
        MI_NUMERO  := PCK_PRESUPUESTO3.FC_ENUMERAR(UN_COMPANIA   => UN_COMPANIA,
                                                   UN_ANO        => MI_ANOACTUAL,
                                                   UN_TIPO       => RS.TIPO_ADI);
        MI_CAMPOS := 'COMPANIA,     ANO, 
                      TIPO,         NUMERO, 
                      DESCRIPCION,  TEXTO,
                      FECHA,        FECHA_VCN_DOC,                      
                      DATE_CREATED,   CREATED_BY';
        MI_DESCRIPCION:= 'Adición Reservas de ' || INITCAP(RS.CLASERESERVA) || ' Presupuestales - Comprobante Generado para el cierre presupuestal vigencia ' 
                         || UN_ANOACIERRE || '. de ' || FC_NOMBRETIPOCIERRE(UN_TIPOCIERRE => RS.TIPOCIERRE);
        MI_VALORES :=   '''' || UN_COMPANIA    || ''','   || MI_ANOACTUAL    || '
                        ,''' || RS.TIPO_ADI    || ''','   || MI_NUMERO       || '         
                        ,''' || MI_DESCRIPCION || ''',''' || MI_DESCRIPCION  || '''
                        ,'   || MI_FECHACOMP   ||   ','   || MI_FECHAVCNCOMP || ',
                        SYSDATE,''' || UN_USUARIO || '''';
        BEGIN
            BEGIN
              PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA   => 'COMPROBANTE_PPTAL', 
                                                     UN_ACCION  => 'I', 
                                                     UN_CAMPOS  => MI_CAMPOS, 
                                                     UN_VALORES => MI_VALORES);
            EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
            MI_REEMPLAZOS(1).CLAVE := 'COMPANIA';
            MI_REEMPLAZOS(1).VALOR := UN_COMPANIA;
            MI_REEMPLAZOS(2).CLAVE := 'ANIO';
            MI_REEMPLAZOS(2).VALOR := UN_ANOACIERRE;        
            PCK_ERR_MSG.RAISE_WITH_MSG(
                                      UN_EXC_COD    => SQLCODE,
                                      UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOERRORINSERAPROPIA,
                                      UN_TABLAERROR => MI_TABLA,
                                      UN_REEMPLAZOS => MI_REEMPLAZOS  
            );
        END;

        --SE DEJAN POR DEFECTO EL TERCERO VARIOS, DEBIDO A 
        --QUE EL SALDO DE PRESUPUESTO NO SE MANEJA POR TERCERO
        MI_CONSULTA := 'SELECT COMPANIA, 
                               ' || MI_ANOACTUAL || ' ANO, 
                               TIPO_ADI             TIPO_CPTE, '
                               || MI_NUMERO || '    COMPROBANTE,
                               '   || MI_FECHACOMP   ||   ',
                               ROWNUM               CONSECUTIVO,  
                               CUENTA               CUENTA,  
                               ''' || MI_DESCRIPCION || ''' DESCRIPCION,
                               DIS_DET_TERCERO         TERCERO, 
                               DIS_DET_SUCURSAL        SUCURSAL, 
                               DIS_DET_AUXILIAR        AUXILIAR, 
                               DIS_DET_CENTRO_COSTO    CENTRO_COSTO, 
                               DIS_DET_REFERENCIA      REFERENCIA, 
                               DIS_DET_FUENTE_RECURSO  FUENTE_RECURSO, 
                               SUM(VALOR_DEBITO)       APROPIACIONINICIAL,
                               ''' || UN_USUARIO || '''  CREATED_BY,
                               SYSDATE                   DATE_CREATED     
                              ,SECTOR
                              ,PROGRAMA
                              ,SUBPROGRAMA
                              ,COD_PROD_CUIPO
                              ,CODIGO_BPIN
                              ,CODIGO_CCPET
                              ,CODIGO_CPC
                              ,CODIGOUNIDADEJE
                              ,FUENTE_CUIPO
                              ,CODIGOCCPETREGA
                              ,POLITICA_PUBLICA
                              ,DETALLE_SECTORIAL  
                        FROM TEMP_CIERRE_PPTAL
                        WHERE COMPANIA    = '''|| UN_COMPANIA   ||'''
                          AND ANO         = '  || UN_ANOACIERRE ||' 
                          AND TIPOCIERRE  = '''|| RS.TIPOCIERRE ||'''
                          AND CLASERESERVA= '''|| RS.CLASERESERVA || '''
                          AND TIPO_ADI    = '''|| RS.TIPO_ADI     || '''
                          AND CLASECIERRE IN(''RES'',''REO'',''SAL'')
                          AND TIPO_ADI IS NOT NULL
                        GROUP BY COMPANIA, 
                                 ' || MI_ANOACTUAL || ', 
                                 TIPO_ADI             , '
                                 || MI_NUMERO || '    ,
                                 ROWNUM               ,  
                                 CUENTA               ,  
                                 ''' || MI_DESCRIPCION || ''', 
                                 DIS_DET_TERCERO, 
                                 DIS_DET_SUCURSAL, 
                                 DIS_DET_AUXILIAR, 
                                 DIS_DET_CENTRO_COSTO, 
                                 DIS_DET_REFERENCIA, 
                                 DIS_DET_FUENTE_RECURSO,

                                 ''' || UN_USUARIO || '''  ,
                                 SYSDATE                    
                                ,SECTOR
                                ,PROGRAMA
                                ,SUBPROGRAMA
                                ,COD_PROD_CUIPO
                                ,CODIGO_BPIN
                                ,CODIGO_CCPET
                                ,CODIGO_CPC
                                ,CODIGOUNIDADEJE
                                ,FUENTE_CUIPO
                                ,CODIGOCCPETREGA
                                ,POLITICA_PUBLICA
                                ,DETALLE_SECTORIAL ';
        MI_TABLA     := 'DETALLE_COMPROBANTE_PPTAL';
        MI_CAMPOS := 'COMPANIA,  ANO, TIPO_CPTE, COMPROBANTE, FECHA,
                      CONSECUTIVO, CUENTA, DESCRIPCION, 
                      TERCERO, SUCURSAL, AUXILIAR, CENTRO_COSTO, 
                      REFERENCIA, FUENTE_RECURSO, VALOR_DEBITO,
                      CREATED_BY, DATE_CREATED
                      ,SECTOR
                      ,PROGRAMA
                      ,SUBPROGRAMA
                      ,COD_PROD_CUIPO
                      ,CODIGO_BPIN
                      ,CODIGO_CCPET
                      ,CODIGO_CPC
                      ,CODIGOUNIDADEJE
                      ,FUENTE_CUIPO
                      ,CODIGOCCPETREGA
                      ,POLITICA_PUBLICA
                      ,DETALLE_SECTORIAL ';
    BEGIN
        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                                  UN_ACCION  => 'IS', 
                                                  UN_CAMPOS  => MI_CAMPOS, 
                                                  UN_VALORES => MI_CONSULTA);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        MI_REEMPLAZOS(1).CLAVE := 'COMPANIA';
        MI_REEMPLAZOS(1).VALOR := UN_COMPANIA;
        MI_REEMPLAZOS(2).CLAVE := 'ANIO';
        MI_REEMPLAZOS(2).VALOR := UN_ANOACIERRE;        
        PCK_ERR_MSG.RAISE_WITH_MSG(
                                  UN_EXC_COD    => SQLCODE,
                                  UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOERRORINSERAPROPIA,
                                  UN_TABLAERROR => MI_TABLA,
                                  UN_REEMPLAZOS => MI_REEMPLAZOS  
        );
    END;

    END LOOP RECORRERTIPOS;


END PR_INSERTARADICIONCIERRE;  


PROCEDURE PR_INSERTARCOMPROBANTECIERRE
    /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 03/01/2019
        TIME              : 03:011 PM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Inserta los comprobantes presupuestales 
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME: insertarComprobanteCierre
        @METHOD:Post
    */ 
    (
     UN_COMPANIA        IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE      IN  PCK_SUBTIPOS.TI_ANIO,
     UN_FECHACIERRE     IN  DATE,
     UN_PREFIJO         IN  PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL,
     UN_USUARIO         IN  PCK_SUBTIPOS.TI_USUARIO 
    ) AS 
    MI_CONSULTA           VARCHAR2(32000); 
    MI_SELECT             VARCHAR2(32000); 
    MI_REEMPLAZOS         PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_FECHACOMP          VARCHAR2(50);
    MI_FECHAVCNCOMP       VARCHAR2(50);
    MI_ANOACTUAL          NUMBER(4,0);
    MI_PREFIJO_PADRE      PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL;
    MI_WHEREREO           VARCHAR2(200);
    MI_MERGEUSING       PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE      PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE      PCK_SUBTIPOS.TI_MERGEEXISTE;
BEGIN
    MI_ANOACTUAL    := UN_ANOACIERRE + 1;   
    MI_FECHACOMP    := 'TO_DATE(''' || UN_FECHACIERRE || ''',''DD/MM/YYYY'')';
    MI_FECHAVCNCOMP := 'TO_DATE(''' || ADD_MONTHS(UN_FECHACIERRE,1) || ''',''DD/MM/YYYY'')';
    MI_WHEREREO := ' ';
    IF UN_PREFIJO = 'RES' THEN
        MI_PREFIJO_PADRE := 'DIS';
    ELSIF UN_PREFIJO = 'REO' THEN
        MI_PREFIJO_PADRE := 'RES';
        MI_WHEREREO := ' AND ' || UN_PREFIJO || '_TIPO_FINAL IS NOT NULL';
    ELSE
        MI_PREFIJO_PADRE := ' ';
    END IF;

    MI_CAMPOS := 'COMPANIA,     ANO, 
                  TIPO,         NUMERO, 
                  TERCERO,      SUCURSAL,
                  CENTRO_COSTO, AUXILIAR,
                  REFERENCIA,   FUENTE_RECURSO,
                  DESCRIPCION,  TEXTO,
                  FECHA,        FECHA_VCN_DOC,
                  NRO_DOCUMENTO,DESTINO,
                  TIPOCONTRATO, NUMEROCONTRATO,
                  DEPENDENCIA,  CODSOLICITANTE,
                  SUCSOLICITANTE,COD_PROYECTO_PPTAL,
                  ANOPROYECTO,
                  LUGAR,         FUENTE_FINANCIACION,
                  ASIGNACION,
                  DATE_CREATED,   CREATED_BY';
    MI_SELECT := 'SELECT DISTINCT COMPANIA, 
                                   ' || MI_ANOACTUAL || '   ANO, 
                                   ' || UN_PREFIJO   || '_TIPO_FINAL, 
                                   ' || UN_PREFIJO   || '_NUMERO, 
                                   ' || UN_PREFIJO   || '_TERCERO, 
                                   ' || UN_PREFIJO   || '_SUCURSAL, 
                                   ' || UN_PREFIJO   || '_CENTRO_COSTO, 
                                   ' || UN_PREFIJO   || '_AUXILIAR, 
                                   ' || UN_PREFIJO   || '_REFERENCIA, 
                                   ' || UN_PREFIJO   || '_FUENTE_RECURSO,                                    
                                   ' || UN_PREFIJO   || '_DESCRIPCION, 
                                   ' || UN_PREFIJO   || '_TEXTO,
                                   ' || MI_FECHACOMP    || ' FECHA,
                                   ' || MI_FECHAVCNCOMP || ' FECHA_VCN_DOC, 
                                   ' || UN_PREFIJO   || '_NRO_DOCUMENTO,
                                   ' || UN_PREFIJO   || '_DESTINO,
                                   ' || UN_PREFIJO   || '_TIPOCONTRATO,
                                   ' || UN_PREFIJO   || '_NUMEROCONTRATO,
                                   ' || UN_PREFIJO   || '_DEPENDENCIA,
                                   ' || UN_PREFIJO   || '_CODSOLICITANTE,
                                   ' || UN_PREFIJO   || '_SUCSOLICITANTE,
                                   ' || UN_PREFIJO   || '_COD_PROYECTO_PPTAL,
                                   ' || UN_PREFIJO   || '_ANOPROYECTO,
                                   ' || UN_PREFIJO   || '_LUGAR,
                                   ' || UN_PREFIJO   || '_FUENTE_FINANCIACION,
                                   ' || UN_PREFIJO   || '_ASIGNACION,                                   
                                   SYSDATE,
                                   ''' || UN_USUARIO || '''
                    FROM TEMP_CIERRE_PPTAL
                    WHERE COMPANIA    = ''' || UN_COMPANIA    ||'''
                      AND ANO         = '   || UN_ANOACIERRE || '
                      AND CLASECIERRE IN(''RES'',''REO'') ' ||
                      MI_WHEREREO;    
    BEGIN
        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'COMPROBANTE_PPTAL', 
                                                   UN_ACCION  => 'IS', 
                                                   UN_CAMPOS  => MI_CAMPOS, 
                                                   UN_VALORES => MI_SELECT);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        MI_REEMPLAZOS(1).CLAVE := 'ANIO';
        MI_REEMPLAZOS(1).VALOR := MI_ANOACTUAL;  
        MI_REEMPLAZOS(2).CLAVE := 'TIPO';
        MI_REEMPLAZOS(2).VALOR := UN_PREFIJO;
        MI_REEMPLAZOS(3).CLAVE := 'PARTE';
        MI_REEMPLAZOS(3).VALOR := 'Comprobante';
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOERRORINSERTCOMPROB,
                                   UN_TABLAERROR => MI_TABLA,
                                   UN_REEMPLAZOS => MI_REEMPLAZOS  
                                    );  
    END;

    --INSERTA DETALLE
    MI_CAMPOS := 'COMPANIA,     ANO, 
                  TIPO_CPTE,    COMPROBANTE,
                  CONSECUTIVO,  NATURALEZA,
                  CUENTA,
                  TERCERO,      SUCURSAL,
                  CENTRO_COSTO, AUXILIAR,
                  REFERENCIA,   FUENTE_RECURSO,
                  DESCRIPCION,  
                  FECHA,        
                  TIPOCONTRATO, 
                  NUMEROCONTRATO,
                  DEPENDENCIA, 
                  NRO_DOCUMENTO,
                  DATE_CREATED,   
                  CREATED_BY,
                  TIPOT, 
                  CLASET,
                  CMPTE_SOLICI_AFECTADO,
                  NUMERO_SOLICITUD,
                  TIPO_SOLICITUD,
                  VALOR_DEBITO';
    IF MI_PREFIJO_PADRE <>' ' THEN
        MI_CAMPOS := MI_CAMPOS ||
                     ',ANO_AFECT
                      ,TIPO_CPTE_AFECT
                      ,CMPTE_AFECTADO
                      ,CONSECUTIVOPPTO';                      
    END IF;
    MI_SELECT := 'SELECT COMPANIA, 
                         ' || MI_ANOACTUAL || ' ANO, 
                         ' || UN_PREFIJO   || '_TIPO_FINAL, 
                         ' || UN_PREFIJO   || '_NUMERO, 
                         ' || UN_PREFIJO   || '_CONSECUTIVO,
                         NATURALEZA,
                         CUENTA,
                         ' || UN_PREFIJO   || '_DET_TERCERO , 
                         ' || UN_PREFIJO   || '_DET_SUCURSAL,
                         ' || UN_PREFIJO   || '_DET_CENTRO_COSTO, 
                         ' || UN_PREFIJO   || '_DET_AUXILIAR, 
                         ' || UN_PREFIJO   || '_DET_REFERENCIA, 
                         ' || UN_PREFIJO   || '_DET_FUENTE_RECURSO,
                         ' || UN_PREFIJO   || '_DET_DESCRIPCION,
                         ' || MI_FECHACOMP    || ' FECHA,
                         ' || UN_PREFIJO   || '_DET_TIPOCONTRATO,
                         ' || UN_PREFIJO   || '_DET_NUMEROCONTRATO , 
                         ' || UN_PREFIJO   || '_DET_DEPENDENCIA,
                         ' || UN_PREFIJO   || '_DET_NRO_DOCUMENTO,
                         SYSDATE,
                         ''' || UN_USUARIO || ''',
                         ' || UN_PREFIJO   || '_DET_TIPOT,
                         ' || UN_PREFIJO   || '_DET_CLASET,
                         ' || UN_PREFIJO   || '_DET_CMPTE_SOLICI_AFECTADO,
                         ' || UN_PREFIJO   || '_DET_NUMERO_SOLICITUD,
                         ' || UN_PREFIJO   || '_DET_TIPO_SOLICITUD,
                         SUM(VALOR_DEBITO)';
    IF MI_PREFIJO_PADRE <>' ' THEN
        MI_SELECT := MI_SELECT || 
                     ', ANO + 1  ANO_AFECT    
                     , ' || MI_PREFIJO_PADRE || '_TIPO_FINAL 
                     , ' || MI_PREFIJO_PADRE || '_NUMERO 
                     , ' || MI_PREFIJO_PADRE || '_CONSECUTIVO';
    END IF;
    MI_SELECT := MI_SELECT || '
                    FROM TEMP_CIERRE_PPTAL
                    WHERE COMPANIA   = '''|| UN_COMPANIA   ||'''
                      AND ANO        = '  || UN_ANOACIERRE || '
                      AND CLASECIERRE IN(''RES'',''REO'') ' ||
                      MI_WHEREREO || '    
                    GROUP BY  COMPANIA, 
                         ' || MI_ANOACTUAL || ', 
                         ' || UN_PREFIJO   || '_TIPO_FINAL, 
                         ' || UN_PREFIJO   || '_NUMERO, 
                         ' || UN_PREFIJO   || '_CONSECUTIVO,
                         NATURALEZA,
                         CUENTA,
                         ' || UN_PREFIJO   || '_DET_TERCERO , 
                         ' || UN_PREFIJO   || '_DET_SUCURSAL,
                         ' || UN_PREFIJO   || '_DET_CENTRO_COSTO, 
                         ' || UN_PREFIJO   || '_DET_AUXILIAR, 
                         ' || UN_PREFIJO   || '_DET_REFERENCIA, 
                         ' || UN_PREFIJO   || '_DET_FUENTE_RECURSO,
                         ' || UN_PREFIJO   || '_DET_DESCRIPCION,
                         ' || MI_FECHACOMP    || ',
                         ' || UN_PREFIJO   || '_DET_TIPOCONTRATO,
                         ' || UN_PREFIJO   || '_DET_NUMEROCONTRATO , 
                         ' || UN_PREFIJO   || '_DET_DEPENDENCIA,
                         ' || UN_PREFIJO   || '_DET_NRO_DOCUMENTO,
                         SYSDATE,
                         ''' || UN_USUARIO || ''',
                         ' || UN_PREFIJO   || '_DET_TIPOT,
                         ' || UN_PREFIJO   || '_DET_CLASET,
                         ' || UN_PREFIJO   || '_DET_CMPTE_SOLICI_AFECTADO,
                         ' || UN_PREFIJO   || '_DET_NUMERO_SOLICITUD,
                         ' || UN_PREFIJO   || '_DET_TIPO_SOLICITUD';
        IF MI_PREFIJO_PADRE <>' ' THEN
            MI_SELECT := MI_SELECT || 
                         ', ANO 
                          , ' || MI_PREFIJO_PADRE || '_TIPO_FINAL 
                          , ' || MI_PREFIJO_PADRE || '_NUMERO 
                          , ' || MI_PREFIJO_PADRE || '_CONSECUTIVO';
        END IF;
    BEGIN
        BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'DETALLE_COMPROBANTE_PPTAL', 
                                                   UN_ACCION  => 'IS', 
                                                   UN_CAMPOS  => MI_CAMPOS, 
                                                   UN_VALORES => MI_SELECT);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        MI_REEMPLAZOS(1).CLAVE := 'ANIO';
        MI_REEMPLAZOS(1).VALOR := MI_ANOACTUAL;  
        MI_REEMPLAZOS(2).CLAVE := 'TIPO';
        MI_REEMPLAZOS(2).VALOR := UN_PREFIJO;
        MI_REEMPLAZOS(3).CLAVE := 'PARTE';
        MI_REEMPLAZOS(3).VALOR := 'Detalle del Comprobante';
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                   UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOERRORINSERTCOMPROB,
                                   UN_TABLAERROR => MI_TABLA,
                                   UN_REEMPLAZOS => MI_REEMPLAZOS  
                                    );   
    END;
    
    --CC_2934 GROJAS: ACTUALIZA IDCONTRATOSECOP Y IDCONTRATOENTIDAD DE ACUERDO CON EL HEADER
    BEGIN
    
    MI_TABLA := 'COMPROBANTE_PPTAL';

    MI_MERGEUSING := 'SELECT T.COMPANIA, (T.ANO +1) ANO, T.' || UN_PREFIJO || '_TIPO_INICIAL, T.' || UN_PREFIJO || '_TIPO_FINAL, T.' || UN_PREFIJO || '_NUMERO,
                        C.IDCONTRATOSECOP, C.IDCONTRATOENTIDAD
                        FROM TEMP_CIERRE_PPTAL T
                        INNER JOIN COMPROBANTE_PPTAL C
                        ON T.COMPANIA = C.COMPANIA
                        AND T.ANO = C.ANO
                        AND T.' || UN_PREFIJO || '_TIPO_INICIAL = C.TIPO
                        AND T.' || UN_PREFIJO || '_NUMERO = C.NUMERO
                        GROUP BY T.COMPANIA, (T.ANO +1 ), T.' || UN_PREFIJO || '_TIPO_INICIAL, T.' || UN_PREFIJO || '_TIPO_FINAL, T.' || UN_PREFIJO || '_NUMERO,
                        C.IDCONTRATOSECOP, C.IDCONTRATOENTIDAD';
    
    MI_MERGEENLACE:='TABLA.COMPANIA         = VISTA.COMPANIA 
                        AND TABLA.ANO       = VISTA.ANO 
                        AND TABLA.TIPO      = VISTA.' || UN_PREFIJO || '_TIPO_FINAL 
                        AND TABLA.NUMERO    = VISTA.' || UN_PREFIJO || '_NUMERO';   
    
    MI_MERGEEXISTE := 'UPDATE SET TABLA.IDCONTRATOSECOP   = VISTA.IDCONTRATOSECOP
                        ,TABLA.IDCONTRATOENTIDAD          = VISTA.IDCONTRATOENTIDAD';  
    
    BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA
                                                     ,UN_ACCION => 'MM'
                                                     ,UN_MERGEUSING => MI_MERGEUSING
                                                     ,UN_MERGEENLACE => MI_MERGEENLACE
                                                     ,UN_MERGEEXISTE => MI_MERGEEXISTE);
                
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                      ,UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTUALIZARDETEPPTAL);
    END;
    
    --CC_2934 GROJAS: ACTUALIZA LOS CLASIFICADORES DE CUIPO DE ACUERDO CON CADA DETALLE
    BEGIN
    MI_TABLA := 'DETALLE_COMPROBANTE_PPTAL';

    MI_MERGEUSING := 'SELECT T.COMPANIA, (T.ANO +1) ANO, T.' || UN_PREFIJO || '_TIPO_INICIAL, T.' || UN_PREFIJO || '_TIPO_FINAL, T.' || UN_PREFIJO || '_NUMERO, T.' || UN_PREFIJO || '_CONSECUTIVO
                        ,D.SECTOR ,D.PROGRAMA ,D.SUBPROGRAMA ,D.COD_PROD_CUIPO ,D.CODIGO_BPIN ,D.CODIGO_CCPET ,D.CODIGO_CPC 
                        ,D.CODIGOUNIDADEJE ,D.FUENTE_CUIPO ,D.CODIGOCCPETREGA ,D.POLITICA_PUBLICA ,D.DETALLE_SECTORIAL, D.RECURSO_SGR
                    FROM TEMP_CIERRE_PPTAL T
                    INNER JOIN DETALLE_COMPROBANTE_PPTAL D
                    ON T.COMPANIA = D.COMPANIA
                    AND T.ANO = D.ANO
                    AND T.' || UN_PREFIJO || '_TIPO_INICIAL = D.TIPO_CPTE
                    AND T.' || UN_PREFIJO || '_NUMERO = D.COMPROBANTE
                    AND T.' || UN_PREFIJO || '_CONSECUTIVO = D.CONSECUTIVO
                    GROUP BY T.COMPANIA, (T.ANO +1 ), T.' || UN_PREFIJO || '_TIPO_INICIAL, T.' || UN_PREFIJO || '_TIPO_FINAL, T.' || UN_PREFIJO || '_NUMERO, T.' || UN_PREFIJO || '_CONSECUTIVO
                        ,D.SECTOR ,D.PROGRAMA ,D.SUBPROGRAMA ,D.COD_PROD_CUIPO ,D.CODIGO_BPIN ,D.CODIGO_CCPET ,D.CODIGO_CPC
                        ,D.CODIGOUNIDADEJE ,D.FUENTE_CUIPO ,D.CODIGOCCPETREGA ,D.POLITICA_PUBLICA ,D.DETALLE_SECTORIAL, D.RECURSO_SGR';
    
    MI_MERGEENLACE:='TABLA.COMPANIA       = VISTA.COMPANIA 
                 AND TABLA.ANO            = VISTA.ANO 
                 AND TABLA.TIPO_CPTE      = VISTA.' || UN_PREFIJO || '_TIPO_FINAL 
                 AND TABLA.COMPROBANTE    = VISTA.' || UN_PREFIJO || '_NUMERO
                 AND TABLA.CONSECUTIVO    = VISTA.' || UN_PREFIJO || '_CONSECUTIVO';   
    
    MI_MERGEEXISTE := 'UPDATE SET  TABLA.SECTOR   = VISTA.SECTOR
                       ,TABLA.PROGRAMA            = VISTA.PROGRAMA
                       ,TABLA.SUBPROGRAMA         = VISTA.SUBPROGRAMA
                       ,TABLA.COD_PROD_CUIPO      = VISTA.COD_PROD_CUIPO
                       ,TABLA.CODIGO_BPIN         = VISTA.CODIGO_BPIN
                       ,TABLA.CODIGO_CCPET        = VISTA.CODIGO_CCPET
                       ,TABLA.CODIGO_CPC          = VISTA.CODIGO_CPC
                       ,TABLA.CODIGOUNIDADEJE     = VISTA.CODIGOUNIDADEJE
                       ,TABLA.FUENTE_CUIPO        = VISTA.FUENTE_CUIPO
                       ,TABLA.CODIGOCCPETREGA     = VISTA.CODIGOCCPETREGA
                       ,TABLA.POLITICA_PUBLICA    = VISTA.POLITICA_PUBLICA
                       ,TABLA.DETALLE_SECTORIAL   = VISTA.DETALLE_SECTORIAL
                       ,TABLA.RECURSO_SGR         = VISTA.RECURSO_SGR';  

    BEGIN
			PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA
                                                 ,UN_ACCION => 'MM'
                                                 ,UN_MERGEUSING => MI_MERGEUSING
                                                 ,UN_MERGEENLACE => MI_MERGEENLACE
                                                 ,UN_MERGEEXISTE => MI_MERGEEXISTE);
            
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
			RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_ACTUALIZARDETEPPTAL);
    END;
    
END PR_INSERTARCOMPROBANTECIERRE;  

PROCEDURE PR_ACTUALIZARTIPOFINAL
    /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 09/01/2019
        TIME              : 02:05 PM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Actualiza los comprobantes que se repiten en el año
                            anterior que pasaran como DIX o REX, etc.
                            Con estos los comprobantes que se repiten se les deja 
                            el mismo tipo que tenia el año anterior
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME: actualizartipofinal
        @METHOD:Post
    */ 
    (
     UN_COMPANIA      IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_PREFIJO       IN  PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL,
     UN_CLASECIERRE   IN  PCK_SUBTIPOS.TI_CLASECOMPROPPTO
    )
AS
    MI_TABLA                       PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                      PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                     PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION                   PCK_SUBTIPOS.TI_CONDICION;    
BEGIN
    BEGIN
        MI_TABLA := 'TEMP_CIERRE_PPTAL';
        MI_CAMPOS := UN_PREFIJO || '_TIPO_FINAL = ' || UN_PREFIJO || '_TIPO_INICIAL';
        MI_CONDICION := ' COMPANIA    = ''' || UN_COMPANIA    || '''
                     AND  CLASECIERRE = ''' || UN_CLASECIERRE || '''
                     AND (' || UN_PREFIJO || '_NUMERO)  IN(
                            SELECT ' || UN_PREFIJO || '_NUMERO
                            FROM  (SELECT DISTINCT ' || UN_PREFIJO || '_TIPO_INICIAL, ' || UN_PREFIJO || '_NUMERO 
                                            FROM TEMP_CIERRE_PPTAL
                                            WHERE  CLASECIERRE = ''' || UN_CLASECIERRE || ''')
                            GROUP BY ' || UN_PREFIJO || '_NUMERO
                            HAVING COUNT(' || UN_PREFIJO || '_NUMERO) >1)';  

        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'M',      
                                              UN_CAMPOS    => MI_CAMPOS,      
                                              UN_CONDICION => MI_CONDICION);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
    END;    
EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE,
                               UN_ERROR_COD => PCK_ERRORES.ERR_PPTOERRORUPDATETEMPORAL
                              );  
END PR_ACTUALIZARTIPOFINAL;

FUNCTION FC_NOMBRETIPOCIERRE
   (
     UN_TIPOCIERRE      IN  VARCHAR2
   )  
    /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 18/01/2019
        TIME              : 08:05 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Genera el nombre del tipo de Cierre de la configuración del mismo
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME: nombreTipoCierre
        @METHOD:Get
    */ 
RETURN VARCHAR2
AS
BEGIN
    IF UN_TIPOCIERRE ='NOR' THEN
        RETURN 'Cierre Vigencia';
    ELSIF UN_TIPOCIERRE ='PAE' THEN
        RETURN 'Cierre Pasivos Exigibles';
    ELSIF UN_TIPOCIERRE ='REG' THEN
        RETURN 'Cierre Regalias';
    ELSIF UN_TIPOCIERRE ='VF' THEN
        RETURN 'Cierre Vigencias Futuras';
    ELSIF UN_TIPOCIERRE ='VFE' THEN
        RETURN 'Cierre Vigencias Futuras a Pasivos Exigibles';
    ELSIF UN_TIPOCIERRE ='COF' THEN
        RETURN 'Cierre Cofinanciados';
    END IF;    
    RETURN ' ';
END FC_NOMBRETIPOCIERRE;

PROCEDURE PR_VALIDARCONFIG
       /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 18/01/2019
        TIME              : 08:05 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Valida que la configuración del cierre sea correcto de acuerdo a la normatividad
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME: validarConfiguracionCierre
        @METHOD:Get
    */ 
    (
     UN_COMPANIA          IN  PCK_SUBTIPOS.TI_COMPANIA,
     UN_TIPOCIERRE        IN  CONFIG_CIERRE_PPTAL.TIPOCIERRE%TYPE,
     UN_CLASERESERVA      IN  CONFIG_CIERRE_PPTAL.CLASERESERVA%TYPE,
     UN_TIPO_DIS          IN  CONFIG_CIERRE_PPTAL.TIPO_DIS%TYPE,
     UN_TIPO_RES          IN  CONFIG_CIERRE_PPTAL.TIPO_RES%TYPE,
     UN_TIPO_REO          IN  CONFIG_CIERRE_PPTAL.TIPO_REO%TYPE,
     UN_TIPO_ADI          IN  CONFIG_CIERRE_PPTAL.TIPO_ADI%TYPE,
     UN_DIGITOSCAMBIO     IN  CONFIG_CIERRE_PPTAL.DIGITOSCAMBIO%TYPE,
     UN_TIPOVIGENCIAINICI IN  CONFIG_CIERRE_PPTAL.TIPOVIGENCIAINICI%TYPE,
     UN_TIPOVIGENCIAFINAL IN  CONFIG_CIERRE_PPTAL.TIPOVIGENCIAFINAL%TYPE,
     UN_GENERAR           IN  CONFIG_CIERRE_PPTAL.GENERAR%TYPE
    )
    AS 
        MI_TIPOCIERRENOM     VARCHAR2(100);
        MI_REEMPLAZOS        PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN        
        MI_TIPOCIERRENOM := FC_NOMBRETIPOCIERRE(UN_TIPOCIERRE);
        IF UN_GENERAR NOT IN(0) THEN
            IF UN_CLASERESERVA IN ('APROPIACION', 'PASAIGUAL')  THEN
                IF  UN_TIPO_REO IS NOT NULL THEN
                    /*
                    * SE VALIDA QUE NO SE CONFIGUREN LOS REO PUES NO DEBEN IR POR NINGUN MOTIVO
                    */
                    MI_REEMPLAZOS(1).CLAVE := 'CLASERESERVA';
                    MI_REEMPLAZOS(1).VALOR := CASE WHEN UN_CLASERESERVA IN('APROPIACION') THEN 'Reservas de Apropiación' ELSE 'Vigencias Futuras' END; 
                    MI_REEMPLAZOS(2).CLAVE := 'TIPO';
                    MI_REEMPLAZOS(2).VALOR := MI_TIPOCIERRENOM;                
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD =>-20000,
                            UN_ERROR_COD=>PCK_ERRORES.ERR_PPTOCONFCIERRREREONULO,
                            UN_TABLAERROR =>'CONFIG_CIERRE_PPTAL',
                            UN_REEMPLAZOS => MI_REEMPLAZOS
                          ); 
                --ELSIF UN_TIPO_DIS IS NULL OR UN_TIPO_RES IS NULL OR UN_DIGITOSCAMBIO IS NULL  THEN
                ELSIF UN_TIPO_DIS IS NULL OR UN_TIPO_RES IS NULL THEN
                    /*
                    * SE VALIDA QUE SE CONFIGUREN LOS TIPOS NECESARIOS Y EL DIGITO
                    */
                    MI_REEMPLAZOS(1).CLAVE := 'CLASES';
                    MI_REEMPLAZOS(1).VALOR := 'DIS, RES';
                    MI_REEMPLAZOS(2).CLAVE := 'TIPO';
                    MI_REEMPLAZOS(2).VALOR := MI_TIPOCIERRENOM;
                    MI_REEMPLAZOS(3).CLAVE := 'CLASERESERVA';
                    MI_REEMPLAZOS(3).VALOR := CASE WHEN UN_CLASERESERVA IN('APROPIACION') THEN 'Apropiación' ELSE 'Vigencia Futura' END; 
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD =>-20000,
                            UN_ERROR_COD=>PCK_ERRORES.ERR_PPTOCONFCIERRREOBLIGA,
                            UN_TABLAERROR =>'CONFIG_CIERRE_PPTAL',
                            UN_REEMPLAZOS => MI_REEMPLAZOS
                          ); 
                END IF;  
            ELSIF UN_CLASERESERVA IN('CAJA', 'PASIVO') THEN
                --IF UN_TIPO_DIS IS NULL OR UN_TIPO_RES IS NULL OR UN_TIPO_REO IS NULL OR UN_DIGITOSCAMBIO IS NULL  THEN
                IF UN_TIPO_DIS IS NULL OR UN_TIPO_RES IS NULL OR UN_TIPO_REO IS NULL  THEN
                    /*
                    * SE VALIDA QUE SE CONFIGUREN LOS TIPOS NECESARIOS Y EL DIGITO
                    */
                    MI_REEMPLAZOS(1).CLAVE := 'CLASES';
                    MI_REEMPLAZOS(1).VALOR := 'DIS, RES, REO';
                    MI_REEMPLAZOS(2).CLAVE := 'TIPO';
                    MI_REEMPLAZOS(2).VALOR := MI_TIPOCIERRENOM;
                    MI_REEMPLAZOS(3).CLAVE := 'CLASERESERVA';
                    MI_REEMPLAZOS(3).VALOR := CASE WHEN UN_CLASERESERVA IN('CAJA') THEN 'Caja' ELSE 'Pasivo Exigible' END;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD =>-20000,
                            UN_ERROR_COD=>PCK_ERRORES.ERR_PPTOCONFCIERRREOBLIGA,
                            UN_TABLAERROR =>'CONFIG_CIERRE_PPTAL',
                            UN_REEMPLAZOS => MI_REEMPLAZOS
                          ); 
                END IF;  
            END IF;
            IF (UN_TIPOVIGENCIAFINAL IS NULL OR UN_TIPOVIGENCIAINICI IS NULL) AND UN_TIPOCIERRE <>'REG' THEN
                /*
                * SE VALIDA QUE SE CONFIGURE EL TIPO DE VIGENCIA FINAL O EL QUE QUEDARA EN LA CONFIGURACIÓN
                */
                MI_REEMPLAZOS(1).CLAVE := 'TIPO';
                MI_REEMPLAZOS(1).VALOR := MI_TIPOCIERRENOM;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD =>-20000,
                        UN_ERROR_COD=>PCK_ERRORES.ERR_PPTOCONFCIERRRETIPOVIG,
                        UN_TABLAERROR =>'CONFIG_CIERRE_PPTAL',
                        UN_REEMPLAZOS => MI_REEMPLAZOS
                      ); 
            END IF;   
            /*
            * VALIDA QUE LOS TIPOS DE COMPROBANTES CONFIGURADOS SEAN CORRECTOS DE ACUERDO A LA CLASE DE LOS MISMOS
            */
            PR_VALIDARCLASETIPOCOM(UN_COMPANIA => UN_COMPANIA,
                                   UN_TIPO     => UN_TIPO_DIS,
                                   UN_CLASE    => 'DIS');
            PR_VALIDARCLASETIPOCOM(UN_COMPANIA => UN_COMPANIA,
                                   UN_TIPO     => UN_TIPO_RES,
                                   UN_CLASE    => 'RES');
            IF UN_TIPO_REO IS NOT NULL THEN
                PR_VALIDARCLASETIPOCOM(UN_COMPANIA => UN_COMPANIA,
                                       UN_TIPO     => UN_TIPO_REO,
                                       UN_CLASE    => 'REO');
            END IF;
            IF UN_TIPO_ADI IS NOT NULL THEN
                PR_VALIDARCLASETIPOCOM(UN_COMPANIA => UN_COMPANIA,
                                       UN_TIPO     => UN_TIPO_ADI,
                                       UN_CLASE    => 'ADC');
            END IF;
        END IF;
END PR_VALIDARCONFIG;

PROCEDURE PR_VALIDARCLASETIPOCOM
    /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 18/01/2019
        TIME              : 10:16 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Valida que el tipo de comprobante tenga la clase que se envia 
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME: validarClaseTipoComprobantePptal
        @METHOD:Get
    */ 
   (
     UN_COMPANIA          IN  PCK_SUBTIPOS.TI_COMPANIA,
     UN_TIPO              IN  PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL,
     UN_CLASE             IN  TIPO_COMPROBPP.CLASE%TYPE    
   )AS
        MI_CONTEO      NUMBER(1);
        MI_REEMPLAZOS  PCK_SUBTIPOS.TI_CLAVEVALOR;
   BEGIN
        SELECT COUNT(CODIGO)
        INTO MI_CONTEO
        FROM TIPO_COMPROBPP
        WHERE COMPANIA = UN_COMPANIA
          AND CODIGO   = UN_TIPO
          AND CLASE    = UN_CLASE;
        IF MI_CONTEO =0 THEN
            MI_REEMPLAZOS(1).CLAVE := 'CLASE';
            MI_REEMPLAZOS(1).VALOR := UN_CLASE;
            MI_REEMPLAZOS(2).CLAVE := 'TIPO';
            MI_REEMPLAZOS(2).VALOR := UN_TIPO;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD =>-20000,
                    UN_ERROR_COD=>PCK_ERRORES.ERR_PPTOVALIDARCLASETIPO,
                    UN_TABLAERROR =>'CONFIG_CIERRE_PPTAL',
                    UN_REEMPLAZOS => MI_REEMPLAZOS
                  ); 
        END IF;
END PR_VALIDARCLASETIPOCOM;

PROCEDURE PR_CREAR_TIPOSDEFECTO
      /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 22/01/2019
        TIME              : 06:45 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite crear los comprobantes por defecto para la configuración
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:crearTipoDefectoCierre
        @METHOD:Post
*/   
   (
     UN_COMPANIA          IN  PCK_SUBTIPOS.TI_COMPANIA,
     UN_USUARIO           IN  PCK_SUBTIPOS.TI_USUARIO 
   )AS
BEGIN
    /*
    * COMPROBANTES DE TIPO CIERRE NORMAL(NOR) 
    */
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'DIX',
                            UN_CLASE    => 'DIS',
                            UN_NOMBRE   => 'Disponibilidad para reserva apropiación',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'REX',
                            UN_CLASE    => 'RES',
                            UN_NOMBRE   => 'Registro para reserva apropiación',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'DIZ',
                            UN_CLASE    => 'DIS',
                            UN_NOMBRE   => 'Disponibilidad para reserva de caja',
                            UN_USUARIO  => UN_USUARIO);                      
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'REZ',
                            UN_CLASE    => 'RES',
                            UN_NOMBRE   => 'Registro para reserva de caja',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'ROZ',
                            UN_CLASE    => 'REO',
                            UN_NOMBRE   => 'Obligación para reserva de caja',
                            UN_USUARIO  => UN_USUARIO);
    /*
    * COMPROBANTES DE TIPO CIERRE PASIVOS EXIGIBLES (PAE)
    */    
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'DIV',
                            UN_CLASE    => 'DIS',
                            UN_NOMBRE   => 'Disponibilidad Reserva de Apropiación Pasivo Exigible',
                            UN_USUARIO  => UN_USUARIO); 
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'REV',
                            UN_CLASE    => 'RES',
                            UN_NOMBRE   => 'Registro Reserva de Apropiación Pasivo Exigible',
                            UN_USUARIO  => UN_USUARIO);

    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'DIU',
                            UN_CLASE    => 'DIS',
                            UN_NOMBRE   => 'Disponibilidad Reserva de Caja Pasivo Exigible',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'REU',
                            UN_CLASE    => 'RES',
                            UN_NOMBRE   => 'Registro Reserva de Caja Pasivo Exigible',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'ROU',
                            UN_CLASE    => 'REO',
                            UN_NOMBRE   => 'Obligación Reserva de Caja Pasivo Exigible',
                            UN_USUARIO  => UN_USUARIO);

    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'DPE',
                            UN_CLASE    => 'DIS',
                            UN_NOMBRE   => 'Disponibilidad Pasivo Exigible',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'RPE',
                            UN_CLASE    => 'RES',
                            UN_NOMBRE   => 'Registro Pasivo Exigible',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'ROP',
                            UN_CLASE    => 'REO',
                            UN_NOMBRE   => 'Obligación Pasivo Exigible',
                            UN_USUARIO  => UN_USUARIO);

    /*
    * COMPROBANTES DE TIPO CIERRE REGALIAS (REG)
    */        
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'DRX',
                            UN_CLASE    => 'DIS',
                            UN_NOMBRE   => 'Disponi para reserva de Regalías',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'RRX',
                            UN_CLASE    => 'RES',
                            UN_NOMBRE   => 'Registro para reserva regalías',
                            UN_USUARIO  => UN_USUARIO);

    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'DRZ',
                            UN_CLASE    => 'DIS',
                            UN_NOMBRE   => 'Dis cuentas por pagar regalías',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'RRZ',
                            UN_CLASE    => 'RES',
                            UN_NOMBRE   => 'Res cuentas por pagar regalías',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'RBZ',
                            UN_CLASE    => 'REO',
                            UN_NOMBRE   => 'Reo cuentas por pagar regalías',
                            UN_USUARIO  => UN_USUARIO);

   /*
    * COMPROBANTES DE TIPO CIERRE VIGENCIAS FUTURAS
    */        
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'DIY',
                            UN_CLASE    => 'DIS',
                            UN_NOMBRE   => 'Disponi para reserva de Vigencias Futuras',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'REY',
                            UN_CLASE    => 'RES',
                            UN_NOMBRE   => 'Registro para reserva de Vigencias Futuras',
                            UN_USUARIO  => UN_USUARIO);

    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'DIW',
                            UN_CLASE    => 'DIS',
                            UN_NOMBRE   => 'Dis cuentas por pagar Vigencias Futuras',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'REW',
                            UN_CLASE    => 'RES',
                            UN_NOMBRE   => 'Res cuentas por pagar Vigencias Futuras',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'ROW',
                            UN_CLASE    => 'REO',
                            UN_NOMBRE   => 'Reo cuentas por pagar Vigencias Futuras',
                            UN_USUARIO  => UN_USUARIO);

    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'AVF',
                            UN_CLASE    => 'DIS',
                            UN_NOMBRE   => 'Disponi para Vigencias Futuras Actual',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'RVF',
                            UN_CLASE    => 'RES',
                            UN_NOMBRE   => 'Registro para Vigencias Futuras Actual',
                            UN_USUARIO  => UN_USUARIO);

    /*
    * COMPROBANTES DE TIPO CIERRE VIGENCIAS FUTURAS A PASIVOS EXIGIBLES
    */        
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'DIK',
                            UN_CLASE    => 'DIS',
                            UN_NOMBRE   => 'Disponi Reserva Pasivo Exigible de Vigencias Futuras',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'REK',
                            UN_CLASE    => 'RES',
                            UN_NOMBRE   => 'Registro Reserva Pasivo Exigible de Vigencias Futuras',
                            UN_USUARIO  => UN_USUARIO);

    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'DIQ',
                            UN_CLASE    => 'DIS',
                            UN_NOMBRE   => 'Dispo Cuenta por Pagar Pasivo Exigible de Vigencias Futuras',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'REQ',
                            UN_CLASE    => 'RES',
                            UN_NOMBRE   => 'Res Cuenta por Pagar Pasivo Exigible de Vigencias Futuras',
                            UN_USUARIO  => UN_USUARIO);
    PR_INSERTARTIPOCOMPPTAL(UN_COMPANIA => UN_COMPANIA,  
                            UN_TIPO     => 'ROQ',
                            UN_CLASE    => 'REO',
                            UN_NOMBRE   => 'Reo Cuenta por Pagar Pasivo Exigible de Vigencias Futuras',
                            UN_USUARIO  => UN_USUARIO);
END PR_CREAR_TIPOSDEFECTO;

FUNCTION FC_CONSULTAPLANCIERRE
       /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 23/01/2019
        TIME              : 08:06 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Genera la consulta se crea función aparte pues se utiliza 
                            para el proceso de cierre como para la validación o revisión
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME: validarClaseTipoComprobantePptal
        @METHOD:Get
    */ 
   (
     UN_COMPANIA        IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE      IN  PCK_SUBTIPOS.TI_ANIO
   )
RETURN VARCHAR2
AS
    MI_CONSULTA      VARCHAR2(32000); 
    MI_TABLA         VARCHAR2(200);
    MI_CAMPOSPLAN    VARCHAR2(32000);     
    MI_EXCLUIDOS     VARCHAR2(200);
    MI_ANOFINAL      PCK_SUBTIPOS.TI_ANIO;

BEGIN
    MI_ANOFINAL   := UN_ANOACIERRE + 1;
    MI_TABLA      := 'PLAN_PRESUPUESTAL';
    MI_EXCLUIDOS  := 'ANO,CODIGO,TIPOVIGENCIA,APROPIACIONINICIAL,RESERVADEAPROPIACION,RESERVADECAJA,CREATED_BY,MODIFIED_BY,DATE_MODIFIED,DATE_CREATED,COD_SIGUIENTE_VIGENCIA,COD_ANTERIOR_VIGENCIA';

    MI_CAMPOSPLAN := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS(UN_TABLA      => MI_TABLA,
                                                    UN_EXCLUIDOS  => MI_EXCLUIDOS,
                                                    UN_CAMPOSCLOB => 0,
                                                    UN_CONDOMINIO => -1);

    MI_CAMPOSPLANFIN := REPLACE(MI_CAMPOSPLAN, MI_TABLA || '.','') || ', ANO, CODIGO, TIPOVIGENCIA, RESERVADEAPROPIACION, RESERVADECAJA, COD_ANTERIOR_VIGENCIA ';
    MI_CONSULTA := ' SELECT DISTINCT ' || MI_CAMPOSPLAN || 
                                 ',   ' || MI_ANOFINAL   || ' ANO
                                  ,
                                  CASE WHEN CONF.RECLASIFICAR IN(0)
                                    THEN 
                                         CASE WHEN MOV.DIGITOCAMBIO IS NULL 
                                         THEN PLAN_PRESUPUESTAL.CODIGO 
                                         ELSE MOV.DIGITOCAMBIO || SUBSTR(PLAN_PRESUPUESTAL.CODIGO, 2 , LENGTH(PLAN_PRESUPUESTAL.CODIGO)) END
                                    ELSE
                                       MOV.CUENTA
                                    END 

                                      CODIGO 
                                  , MOV.TIPOVIGENCIA
                                  , CASE WHEN MOV.CLASECIERRE =''RES'' AND MOV.TIPOVIGENCIA = ''RA'' THEN -1 ELSE 0 END RESERVADEAPROPIACION
                                  , CASE WHEN MOV.CLASECIERRE =''REO'' AND MOV.TIPOVIGENCIA = ''RC'' THEN -1 ELSE 0 END RESERVADECAJA
                                  , PLAN_PRESUPUESTAL.CODIGO COD_ANTERIOR_VIGENCIA
                    FROM TEMP_CIERRE_PPTAL MOV INNER JOIN PLAN_PRESUPUESTAL
                      ON PLAN_PRESUPUESTAL.COMPANIA = MOV.COMPANIA
                     AND PLAN_PRESUPUESTAL.ANO      = MOV.ANO
                     AND PLAN_PRESUPUESTAL.CODIGO   = CASE WHEN MOV.CREAJERARQUIA IN(0)
                                                           THEN MOV.CUENTAANT 
                                                           ELSE SUBSTR(MOV.CUENTAANT, 1, LENGTH(PLAN_PRESUPUESTAL.CODIGO))
                                                           END 
                    INNER JOIN CONFIG_CIERRE_PPTAL CONF
                         ON  MOV.COMPANIA     = CONF.COMPANIA
                        AND  MOV.TIPOCIERRE   = CONF.TIPOCIERRE
                        AND  MOV.CLASERESERVA = CONF.CLASERESERVA
                    WHERE MOV.COMPANIA =''' || UN_COMPANIA || '''
                      AND MOV.ANO      ='   ||  UN_ANOACIERRE || '
                      AND MOV.CLASECIERRE IN(''RES'', ''REO'',''SAL'')
                     
                     ';    
                   

    RETURN MI_CONSULTA;
END FC_CONSULTAPLANCIERRE;

FUNCTION FC_VALIDARCIERREPLAN
   /*  
        NAME              : En Access CierrePresupuestoCB
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 01/12/2018
        TIME              : 06:45 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite generar archivo de excel con validación de los datos que se pasaran
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:validarCierrePlan
        @METHOD:Post
*/   
    (
     UN_COMPANIA             IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE           IN  PCK_SUBTIPOS.TI_ANIO,
     UN_CIERRENORMAL         IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREPASIVO         IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREVIGFUTURAS     IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREVIGFUTUAPASIVO IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREREGALIAS       IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERRECOFINANCIADOS  IN  PCK_SUBTIPOS.TI_LOGICO
    )
RETURN CLOB AS
    MI_ANOACTUAL    NUMBER(4,0);
    MI_RTA          CLOB;
    MI_RTA_DETALLE  CLOB;
    MI_CONSULTA     VARCHAR2(32000);
    MI_REGISTRO     VARCHAR2(32000);
    MI_RS           SYS_REFCURSOR;
    MI_RUBRONUEVO            DETALLE_COMPROBANTE_PPTAL.CUENTA%TYPE;
    MI_RUBROANTERIOR         DETALLE_COMPROBANTE_PPTAL.CUENTA%TYPE;
    MI_NOMBRE                VARCHAR2(600);
    MI_TIPOVIGENCIA          VARCHAR2(200);
    MI_RESERVADEAPROPIACION  VARCHAR2(20);
    MI_RESERVADECAJA         VARCHAR2(20);
    MI_RUBRODUPLICADO        DETALLE_COMPROBANTE_PPTAL.CUENTA%TYPE;
    MI_ANOVIGENCIA           NUMBER(5,0);
    MI_TIPOFINAL             VARCHAR2(32);
    MI_NUMERO                NUMBER(20,2);
    MI_CONSECUTIVO           NUMBER(20,2);
BEGIN
    MI_ANOACTUAL     := UN_ANOACIERRE + 1;    


    IF UN_CIERREREGALIAS <> 0 THEN--(CC:3349_INI_CFBARRERA)
        MI_RTA := FC_GENERAREXCELVIGENCIA(
                    UN_COMPANIA   => UN_COMPANIA,  
                    UN_ANOACIERRE => UN_ANOACIERRE
                  );
        
        
        IF MI_RTA IS NOT NULL THEN
            RETURN MI_RTA;
        END IF;
    
    END IF;--(CC:3349_FIN_CFBARRERA)

    /*
    * PREPARA INFORMACIÓN DE CIERRE EN LA TEMPORAL
    */
    MI_RTA := FC_PREPARATODO(UN_COMPANIA             => UN_COMPANIA,  
                             UN_ANOACIERRE           => UN_ANOACIERRE,
                             UN_CIERRENORMAL         => UN_CIERRENORMAL,
                             UN_CIERREPASIVO         => UN_CIERREPASIVO,
                             UN_CIERREVIGFUTURAS     => UN_CIERREVIGFUTURAS,
                             UN_CIERREVIGFUTUAPASIVO => UN_CIERREVIGFUTUAPASIVO,
                             UN_CIERREREGALIAS       => UN_CIERREREGALIAS,
                             UN_CIERRECOFINANCIADOS  => UN_CIERRECOFINANCIADOS);

    /*
    * CREA PLAN PRESUPUESTAL Y SALDOS INICIALES
    */
    MI_CONSULTA := FC_CONSULTAPLANCIERRE(UN_COMPANIA    => UN_COMPANIA,  
                                         UN_ANOACIERRE  => UN_ANOACIERRE
                                        );
    /*
    * CONSULTA FINAL PARA GENERAR EL EXCEL NECESARIO
    */

    MI_CONSULTA := ' SELECT TEM.CODIGO                  RUBRONUEVO
                          , TEM.COD_ANTERIOR_VIGENCIA   RUBROANTERIOR
                          , TEM.NOMBRE
                          , TIPOV.NOMBRE
                          , TEM.VIGENCIA
                          , TEM.RESERVADEAPROPIACION
                          , TEM.RESERVADECAJA
                     FROM (' || MI_CONSULTA ||' ) TEM INNER JOIN TIPOVIGENCIA TIPOV
                       ON TEM.TIPOVIGENCIA = TIPOV.CODIGO 
                     ORDER BY TEM.CODIGO';    

    MI_RTA := 'Plan Presupuestal' ||  PCK_DATOS.GL_SEPARADOR_REG;
    MI_RTA := MI_RTA || TO_CLOB( 
                      'DUPLICADO'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      'RUBRO NUEVO'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      'RUBRO ANTERIOR'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      'NOMBRE'                 ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      'TIPO VIGENCIA'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      'VIGENCIA EJECUCION'     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      'RESERVA APROPIACIÓN'    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      'RESERVA DE CAJA'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      PCK_DATOS.GL_SEPARADOR_REG);
    MI_RUBRODUPLICADO:= ' ';                  
    OPEN MI_RS FOR MI_CONSULTA;
    LOOP
        FETCH MI_RS
        INTO MI_RUBRONUEVO, MI_RUBROANTERIOR, MI_NOMBRE, MI_TIPOVIGENCIA, MI_ANOVIGENCIA, MI_RESERVADEAPROPIACION, MI_RESERVADECAJA;
        EXIT WHEN MI_RS%NOTFOUND;
            MI_REGISTRO:= REPLACE(REPLACE(TO_CLOB( 
                          CASE WHEN MI_RUBRODUPLICADO = MI_RUBRONUEVO 
                               THEN 'SI' 
                               ELSE 'NO' END       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_RUBRONUEVO            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_RUBROANTERIOR         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          TRIM(MI_NOMBRE)          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_TIPOVIGENCIA          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_ANOVIGENCIA           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_RESERVADEAPROPIACION  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_RESERVADECAJA         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          PCK_DATOS.GL_SEPARADOR_REG),
                            CHR(10),''),CHR(13),'')  ;
            MI_RTA := MI_RTA || MI_REGISTRO;            
            IF MI_RUBRODUPLICADO = MI_RUBRONUEVO THEN
                MI_RTA := REPLACE(MI_RTA, 
                                 'NO' || PCK_DATOS.GL_SEPARADOR_COL  || MI_RUBRONUEVO || PCK_DATOS.GL_SEPARADOR_COL,
                                 'SI' || PCK_DATOS.GL_SEPARADOR_COL  || MI_RUBRONUEVO || PCK_DATOS.GL_SEPARADOR_COL);
            END IF;
            MI_RUBRODUPLICADO:= MI_RUBRONUEVO;
    END LOOP; 
    CLOSE MI_RS;

     /*
    * DATOS DEL DETALLE DE COMPROBANTES QUE PASAN ABIERTOS EN EL CIERRE
    */
    MI_RTA_DETALLE := MI_RTA_DETALLE || PCK_DATOS.GL_SEPARADOR_HOJ || 'Detalle Comprobante Abiertos';
    MI_RTA_DETALLE := MI_RTA_DETALLE || PCK_DATOS.GL_SEPARADOR_REG ||
                                    'REVISAR'               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CLASE COMPROBANTE'     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'TIPO VIGENCIA'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'VIGENCIA EJECUCION'    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'NATURALEZA'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||                                    
                                    'TIPO INICIAL'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'DUPLICADO'             ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'TIPO FINAL'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'NUMERO'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CONSECUTIVO'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||                                    
                                    'CUENTA'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CUENTA ANTERIOR'       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'VALOR DEBITO'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'TERCERO'               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'TERCERO NOMBRE'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CENTRO COSTO'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'AUXILIAR'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'FUENTE RECURSO'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REFERENCIA'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||                                    
                                    'NRO DOCUMENTO'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||     
                                    'SECTOR'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'PROGRAMA'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'SUBPROGRAMA'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'COD_PROD_CUIPO'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGO_BPIN'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGO_CCPET'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGO_CPC'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGOUNIDADEJE'       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'FUENTE_CUIPO'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGOCCPETREGA'       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'POLITICA_PUBLICA'      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'DETALLE_SECTORIAL'     ||  PCK_DATOS.GL_SEPARADOR_COL  ||                                
                                    PCK_DATOS.GL_SEPARADOR_REG ;    
    MI_TIPOFINAL    := ' ';
    MI_NUMERO       := -1;
    MI_CONSECUTIVO  := -1;
    <<DETALLE>>
    FOR RS IN(
        SELECT  CASE WHEN TEM.TIPOCIERRE ='REG' AND TEM.TIPOVIGENCIA IN('VT','VF','FR','FC','PE') 
                THEN 'Regalias Como Vigencias futuras o pasivo Exigible'  
                ELSE CASE WHEN TEM.TIPOVIGENCIA ='VT' AND TEM.VIGENCIA <=UN_ANOACIERRE 
                    THEN 'Vigencia Futura sin Vigencia'  
                    ELSE CASE WHEN TEM.TIPOVIGENCIA ='VF' AND TEM.VIGENCIA <> UN_ANOACIERRE 
                         THEN 'Vigencia Futura Actual sin Vigencia'  
                         ELSE CASE WHEN TEM.TIPOVIGENCIA ='PE' AND TEM.VIGENCIA >= UN_ANOACIERRE 
                              THEN 'Pasivo Exigible con Vigencia Incosistente'  
                              ELSE ''
                              END 
                         END
                    END
                END REVISAR                
            , TEM.CLASECIERRE
            , TIPOV.NOMBRE TIPOVIGENCIA
            , TEM.VIGENCIA
            , TEM.NATURALEZA
            , CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_TIPO_INICIAL       ELSE TEM.REO_TIPO_INICIAL       END TIPO_INICIAL      
            , CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_TIPO_FINAL         ELSE TEM.REO_TIPO_FINAL         END TIPO_FINAL     
            , CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_NUMERO             ELSE TEM.REO_NUMERO             END NUMERO 
            , CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_CONSECUTIVO        ELSE TEM.REO_CONSECUTIVO        END CONSECUTIVO
            , TEM.CUENTA
            , TEM.CUENTAANT
            , TEM.VALOR_DEBITO
            , CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_DET_TERCERO        ELSE TEM.REO_DET_TERCERO        END TERCERO
            , CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_DET_SUCURSAL       ELSE TEM.REO_DET_SUCURSAL       END SUCURSAL
            , TERCERO.NOMBRE TERCERONOM
            , CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_DET_CENTRO_COSTO   ELSE TEM.REO_DET_CENTRO_COSTO   END CENTRO_COSTO
            , CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_DET_AUXILIAR       ELSE TEM.REO_DET_AUXILIAR       END AUXILIAR
            , CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_DET_FUENTE_RECURSO ELSE TEM.REO_DET_FUENTE_RECURSO END FUENTE_RECURSO
            , CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_DET_REFERENCIA     ELSE TEM.REO_DET_REFERENCIA     END REFERENCIA            
            , CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_DET_CENTRO_COSTO   ELSE TEM.REO_DET_NRO_DOCUMENTO  END NRO_DOCUMENTO
            , CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_DET_CENTRO_COSTO   ELSE TEM.REO_DET_DEPENDENCIA    END DEPENDENCIA
            ,TEM.SECTOR
            ,TEM.PROGRAMA
            ,TEM.SUBPROGRAMA
            ,TEM.COD_PROD_CUIPO
            ,TEM.CODIGO_BPIN
            ,TEM.CODIGO_CCPET
            ,TEM.CODIGO_CPC
            ,TEM.CODIGOUNIDADEJE
            ,TEM.FUENTE_CUIPO
            ,TEM.CODIGOCCPETREGA
            ,TEM.POLITICA_PUBLICA
            ,TEM.DETALLE_SECTORIAL
        FROM TEMP_CIERRE_PPTAL TEM INNER JOIN TIPOVIGENCIA TIPOV
         ON TEM.TIPOVIGENCIA = TIPOV.CODIGO 
        INNER JOIN TERCERO 
         ON TERCERO.COMPANIA = TEM.COMPANIA
        AND TERCERO.SUCURSAL = CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_DET_SUCURSAL       ELSE TEM.REO_DET_SUCURSAL       END
        AND TERCERO.NIT      = CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_DET_TERCERO        ELSE TEM.REO_DET_TERCERO        END
        WHERE TEM.COMPANIA  = UN_COMPANIA
          AND TEM.ANO       = UN_ANOACIERRE
          AND TEM.CLASECIERRE IN('RES','REO') 
        ORDER BY  TEM.CLASECIERRE, 
                 CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_TIPO_FINAL         ELSE TEM.REO_TIPO_FINAL         END 
               , CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_NUMERO             ELSE TEM.REO_NUMERO             END 
               , CASE WHEN TEM.CLASECIERRE ='RES' THEN TEM.RES_CONSECUTIVO        ELSE TEM.REO_CONSECUTIVO        END
           )
    LOOP
        MI_REGISTRO:= REPLACE(REPLACE(TO_CLOB( 
                        RS.REVISAR               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CLASECIERRE           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.TIPOVIGENCIA          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.VIGENCIA              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.NATURALEZA            ||  PCK_DATOS.GL_SEPARADOR_COL  ||                                                
                        RS.TIPO_INICIAL          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        CASE WHEN MI_TIPOFINAL = RS.TIPO_FINAL AND MI_NUMERO = RS.NUMERO AND MI_CONSECUTIVO = RS.CONSECUTIVO 
                             THEN 'SI' 
                             ELSE 'NO' END       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.TIPO_FINAL            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.NUMERO                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CONSECUTIVO           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CUENTA                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CUENTAANT             ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.VALOR_DEBITO          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.TERCERO               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.TERCERONOM            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CENTRO_COSTO          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.AUXILIAR              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.FUENTE_RECURSO        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.REFERENCIA            ||  PCK_DATOS.GL_SEPARADOR_COL  ||                        
                        RS.NRO_DOCUMENTO         ||  PCK_DATOS.GL_SEPARADOR_COL  ||                    
                        RS.SECTOR                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.PROGRAMA              ||  PCK_DATOS.GL_SEPARADOR_COL ||
                        RS.SUBPROGRAMA           ||  PCK_DATOS.GL_SEPARADOR_COL ||
                        RS.COD_PROD_CUIPO        ||  PCK_DATOS.GL_SEPARADOR_COL ||
                        RS.CODIGO_BPIN           ||  PCK_DATOS.GL_SEPARADOR_COL ||
                        RS.CODIGO_CCPET          ||  PCK_DATOS.GL_SEPARADOR_COL ||
                        RS.CODIGO_CPC            ||  PCK_DATOS.GL_SEPARADOR_COL ||
                        RS.CODIGOUNIDADEJE       ||  PCK_DATOS.GL_SEPARADOR_COL ||
                        RS.FUENTE_CUIPO          ||  PCK_DATOS.GL_SEPARADOR_COL ||
                        RS.CODIGOCCPETREGA       ||  PCK_DATOS.GL_SEPARADOR_COL ||
                        RS.POLITICA_PUBLICA      ||  PCK_DATOS.GL_SEPARADOR_COL ||
                        RS.DETALLE_SECTORIAL     ||  PCK_DATOS.GL_SEPARADOR_COL ||
                      PCK_DATOS.GL_SEPARADOR_REG),
                        CHR(10),''),CHR(13),'')  ;

        MI_RTA_DETALLE := MI_RTA_DETALLE || MI_REGISTRO; 
        IF MI_TIPOFINAL = RS.TIPO_FINAL AND MI_NUMERO = RS.NUMERO AND MI_CONSECUTIVO = RS.CONSECUTIVO  THEN
            MI_RTA_DETALLE := REPLACE(MI_RTA_DETALLE, 
                             'NO' || PCK_DATOS.GL_SEPARADOR_COL || RS.TIPO_FINAL   
                                  || PCK_DATOS.GL_SEPARADOR_COL || RS.NUMERO       
                                  || PCK_DATOS.GL_SEPARADOR_COL || RS.CONSECUTIVO  || PCK_DATOS.GL_SEPARADOR_COL,
                             'SI' || PCK_DATOS.GL_SEPARADOR_COL || RS.TIPO_FINAL   
                                  || PCK_DATOS.GL_SEPARADOR_COL || RS.NUMERO       
                                  || PCK_DATOS.GL_SEPARADOR_COL || RS.CONSECUTIVO  || PCK_DATOS.GL_SEPARADOR_COL
                                  );
        END IF;
        MI_TIPOFINAL    := RS.TIPO_FINAL;
        MI_NUMERO       := RS.NUMERO;
        MI_CONSECUTIVO  := RS.CONSECUTIVO;
    END LOOP DETALLE;    

    MI_RTA := MI_RTA || MI_RTA_DETALLE;


    /*
    * DATOS DE LAS DIS ABIERTAS
    */
    MI_RTA := MI_RTA || PCK_DATOS.GL_SEPARADOR_HOJ || 'Disponibilidades Abiertas';
    MI_RTA := MI_RTA || PCK_DATOS.GL_SEPARADOR_REG ||
                                    'TIPO COMPROBANTE'      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'COMPROBANTE'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CONSECUTIVO'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||                                    
                                    'CUENTA'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'TIPO VIGENCIA'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'VIGENCIA EJECUCION'    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'NATURALEZA'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'NRO DOCUMENTO'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CENTRO COSTO'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'AUXILIAR'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'FUENTE RECURSO'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REFERENCIA'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'SECTOR'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'PROGRAMA'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'SUBPROGRAMA'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'COD_PROD_CUIPO'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGO_BPIN'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGO_CCPET'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGO_CPC'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGOUNIDADEJE'       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'FUENTE_CUIPO'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGOCCPETREGA'       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'POLITICA_PUBLICA'      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'DETALLE_SECTORIAL'     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'SALDO ABIERTO'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||

                                    PCK_DATOS.GL_SEPARADOR_REG ;    
    <<DISABIERTAS>>
    FOR RS IN(
        SELECT DP.COMPANIA
              ,DP.ANO          
              ,DP.TIPO_CPTE    
              ,DP.COMPROBANTE               
              ,DP.CONSECUTIVO               
              ,DP.CUENTA
              ,TVIG.NOMBRE TIPOVIGENCIA
              ,PP.VIGENCIA
              ,DP.NATURALEZA
              ,DIS.NRO_DOCUMENTO       NRO_DOCUMENTO                              
              ,DP.CENTRO_COSTO         CENTRO_COSTO
              ,DP.AUXILIAR             AUXILIAR
              ,DP.FUENTE_RECURSO       FUENTE_RECURSO
              ,DP.REFERENCIA           REFERENCIA 
              ,((DP.VALOR_DEBITO        - DP.VALOR_CREDITO       )
                  -(DP.DEBITO_AFECTADO     - DP.CREDITO_AFECTADO    )
                  +(DP.MODIFICACION_DEBITO - DP.MODIFICACION_CREDITO)) AS SALDO
              ,DP.SECTOR
              ,DP.PROGRAMA
              ,DP.SUBPROGRAMA
              ,DP.COD_PROD_CUIPO
              ,DP.CODIGO_BPIN
              ,DP.CODIGO_CCPET
              ,DP.CODIGO_CPC
              ,DP.CODIGOUNIDADEJE
              ,DP.FUENTE_CUIPO
              ,DP.CODIGOCCPETREGA
              ,DP.POLITICA_PUBLICA
              ,DP.DETALLE_SECTORIAL

         FROM DETALLE_COMPROBANTE_PPTAL DP INNER JOIN PLAN_PRESUPUESTAL PP 
           ON DP.COMPANIA = PP.COMPANIA 
          AND DP.ANO      = PP.ANO 
          AND DP.CUENTA   = PP.CODIGO
         INNER JOIN TIPO_COMPROBPP TC 
           ON DP.COMPANIA  = TC.COMPANIA 
          AND DP.TIPO_CPTE = TC.CODIGO 
         INNER JOIN COMPROBANTE_PPTAL DIS
           ON DP.COMPANIA        = DIS.COMPANIA 
          AND DP.ANO             = DIS.ANO  
          AND DP.TIPO_CPTE       = DIS.TIPO 
          AND DP.COMPROBANTE     = DIS.NUMERO  
         LEFT JOIN TIPOVIGENCIA TVIG
            ON PP.TIPOVIGENCIA = TVIG.CODIGO 
        WHERE DP.COMPANIA   = UN_COMPANIA
          AND DP.ANO        = UN_ANOACIERRE
          AND TC.CLASE IN ('DIS') 
          AND ((DP.VALOR_DEBITO        - DP.VALOR_CREDITO       )
             - (DP.DEBITO_AFECTADO     - DP.CREDITO_AFECTADO    )
             + (DP.MODIFICACION_DEBITO - DP.MODIFICACION_CREDITO))>0
           )
    LOOP
        MI_REGISTRO:= REPLACE(REPLACE(TO_CLOB( 
                        RS.TIPO_CPTE             ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.COMPROBANTE           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CONSECUTIVO           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CUENTA                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.TIPOVIGENCIA          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.VIGENCIA              ||  PCK_DATOS.GL_SEPARADOR_COL  ||                        
                        RS.NATURALEZA            ||  PCK_DATOS.GL_SEPARADOR_COL  ||                        
                        RS.NRO_DOCUMENTO         ||  PCK_DATOS.GL_SEPARADOR_COL  ||                    
                        RS.CENTRO_COSTO          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.AUXILIAR              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.FUENTE_RECURSO        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.REFERENCIA            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.SECTOR                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.PROGRAMA              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.SUBPROGRAMA           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.COD_PROD_CUIPO        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CODIGO_BPIN           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CODIGO_CCPET          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CODIGO_CPC            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CODIGOUNIDADEJE       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.FUENTE_CUIPO          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CODIGOCCPETREGA       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.POLITICA_PUBLICA      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.DETALLE_SECTORIAL     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.SALDO                 ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      PCK_DATOS.GL_SEPARADOR_REG),
                        CHR(10),''),CHR(13),'')  ;
        MI_RTA := MI_RTA || MI_REGISTRO;    
    END LOOP DISABIERTAS;  


    /*
    * DATOS DEL DETALLE A GENERAR
    */
    MI_RTA := MI_RTA || PCK_DATOS.GL_SEPARADOR_HOJ || 'Detalle Comprobante';
    MI_RTA := MI_RTA || PCK_DATOS.GL_SEPARADOR_REG ||
                                    'CLASE COMPROBANTE'             ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CLASERESERVA'                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'TIPO VIGENCIA'                 ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'VIGENCIA EJECUCION'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'NATURALEZA'                    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CUENTA'                        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CUENTA ANTEIOR'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'VALOR DEBITO'                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'DIS TIPO INICIAL'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'DIS TIPO FINAL'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'DIS NUMERO'                    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'DIS CONSECUTIVO'               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'DIS DET CENTRO COSTO'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'DIS DET AUXILIAR'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'DIS DET FUENTE RECURSO'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'DIS DET REFERENCIA'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'DIS DET NRO DOCUMENTO'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'DIS DET DEPENDENCIA'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'SECTOR'                        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'PROGRAMA'                      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'SUBPROGRAMA'                   ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'COD_PROD_CUIPO'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGO_BPIN'                   ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGO_CCPET'                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGO_CPC'                    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGOUNIDADEJE'               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'FUENTE_CUIPO'                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGOCCPETREGA'               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'POLITICA_PUBLICA'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'DETALLE_SECTORIAL'             ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'RES TIPO INICIAL'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'RES TIPO FINAL'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'RES NUMERO'                    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'RES CONSECUTIVO'               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'RES DET CENTRO_COSTO'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'RES DET AUXILIAR'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'RES DET FUENTE RECURSO'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'RES DET REFERENCIA'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'RES DET TERCERO'               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'RES DET SUCURSAL'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'RES DET NRO_DOCUMENTO'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'RES DET DEPENDENCIA'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REO TIPO INICIAL'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REO TIPO FINAL'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REO NUMERO'                    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REO CONSECUTIVO'               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REO DET CENTRO COSTO'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REO DET AUXILIAR'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REO DET FUENTE RECURSO'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REO DET REFERENCIA'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REO DET TERCERO'               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REO DET SUCURSAL'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REO DET NRO DOCUMENTO'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REO DET DEPENDENCIA'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    PCK_DATOS.GL_SEPARADOR_REG ;    
    <<DETALLETOTAL>>
    FOR RS IN(
        SELECT TEM.CLASECIERRE
            , TEM.CLASERESERVA
            , TEM.CUENTA
            , TEM.CUENTAANT
            , TIPOV.NOMBRE TIPOVIGENCIA
            , TEM.VIGENCIA
            , TEM.NATURALEZA
            , TEM.VALOR_DEBITO
            , TEM.DIS_TIPO_INICIAL
            , TEM.DIS_TIPO_FINAL
            , TEM.DIS_NUMERO
            , TEM.DIS_CONSECUTIVO
            , TEM.DIS_DET_CENTRO_COSTO
            , TEM.DIS_DET_AUXILIAR
            , TEM.DIS_DET_FUENTE_RECURSO
            , TEM.DIS_DET_REFERENCIA
            , TEM.DIS_DET_NRO_DOCUMENTO
            , TEM.DIS_DET_DEPENDENCIA
            , TEM.SECTOR
            , TEM.PROGRAMA
            , TEM.SUBPROGRAMA
            , TEM.COD_PROD_CUIPO
            , TEM.CODIGO_BPIN
            , TEM.CODIGO_CCPET
            , TEM.CODIGO_CPC
            , TEM.CODIGOUNIDADEJE
            , TEM.FUENTE_CUIPO
            , TEM.CODIGOCCPETREGA
            , TEM.POLITICA_PUBLICA
            , TEM.DETALLE_SECTORIAL
            , TEM.RES_TIPO_INICIAL
            , TEM.RES_TIPO_FINAL
            , TEM.RES_NUMERO
            , TEM.RES_CONSECUTIVO
            , TEM.RES_DET_CENTRO_COSTO
            , TEM.RES_DET_AUXILIAR
            , TEM.RES_DET_FUENTE_RECURSO
            , TEM.RES_DET_REFERENCIA
            , TEM.RES_DET_TERCERO
            , TEM.RES_DET_SUCURSAL
            , TEM.RES_DET_NRO_DOCUMENTO
            , TEM.RES_DET_DEPENDENCIA
            , TEM.REO_TIPO_INICIAL
            , TEM.REO_TIPO_FINAL
            , TEM.REO_NUMERO
            , TEM.REO_CONSECUTIVO
            , CASE WHEN TEM.CLASECIERRE <>'RES' THEN TEM.REO_DET_CENTRO_COSTO   ELSE NULL END REO_DET_CENTRO_COSTO
            , CASE WHEN TEM.CLASECIERRE <>'RES' THEN TEM.REO_DET_AUXILIAR       ELSE NULL END REO_DET_AUXILIAR
            , CASE WHEN TEM.CLASECIERRE <>'RES' THEN TEM.REO_DET_FUENTE_RECURSO ELSE NULL END REO_DET_FUENTE_RECURSO
            , CASE WHEN TEM.CLASECIERRE <>'RES' THEN TEM.REO_DET_REFERENCIA     ELSE NULL END REO_DET_REFERENCIA
            , CASE WHEN TEM.CLASECIERRE <>'RES' THEN TEM.REO_DET_TERCERO        ELSE NULL END REO_DET_TERCERO
            , CASE WHEN TEM.CLASECIERRE <>'RES' THEN TEM.REO_DET_SUCURSAL       ELSE NULL END REO_DET_SUCURSAL
            , TEM.REO_DET_NRO_DOCUMENTO
            , TEM.REO_DET_DEPENDENCIA
        FROM TEMP_CIERRE_PPTAL TEM INNER JOIN TIPOVIGENCIA TIPOV
         ON TEM.TIPOVIGENCIA = TIPOV.CODIGO 
        WHERE TEM.COMPANIA  = UN_COMPANIA
          AND TEM.ANO       = UN_ANOACIERRE
          AND TEM.CLASECIERRE IN('RES','REO') 
           )
    LOOP
        MI_REGISTRO:= REPLACE(REPLACE(TO_CLOB( 
                        RS.CLASECIERRE                   ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CLASERESERVA                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.TIPOVIGENCIA                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.VIGENCIA                      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.NATURALEZA                    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CUENTA                        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CUENTAANT                     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.VALOR_DEBITO                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.DIS_TIPO_INICIAL              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.DIS_TIPO_FINAL                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.DIS_NUMERO                    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.DIS_CONSECUTIVO               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.DIS_DET_CENTRO_COSTO          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.DIS_DET_AUXILIAR              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.DIS_DET_FUENTE_RECURSO        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.DIS_DET_REFERENCIA            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.DIS_DET_NRO_DOCUMENTO         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.DIS_DET_DEPENDENCIA           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.SECTOR                        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.PROGRAMA                      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.SUBPROGRAMA                   ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.COD_PROD_CUIPO                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CODIGO_BPIN                   ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CODIGO_CCPET                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CODIGO_CPC                    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CODIGOUNIDADEJE               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.FUENTE_CUIPO                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CODIGOCCPETREGA               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.POLITICA_PUBLICA              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.DETALLE_SECTORIAL             ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.RES_TIPO_INICIAL              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.RES_TIPO_FINAL                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.RES_NUMERO                    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.RES_CONSECUTIVO               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.RES_DET_CENTRO_COSTO          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.RES_DET_AUXILIAR              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.RES_DET_FUENTE_RECURSO        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.RES_DET_REFERENCIA            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.RES_DET_TERCERO               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.RES_DET_SUCURSAL              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.RES_DET_NRO_DOCUMENTO         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.RES_DET_DEPENDENCIA           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.REO_TIPO_INICIAL              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.REO_TIPO_FINAL                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.REO_NUMERO                    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.REO_CONSECUTIVO               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.REO_DET_CENTRO_COSTO          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.REO_DET_AUXILIAR              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.REO_DET_FUENTE_RECURSO        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.REO_DET_REFERENCIA            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.REO_DET_TERCERO               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.REO_DET_SUCURSAL              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.REO_DET_NRO_DOCUMENTO         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.REO_DET_DEPENDENCIA           ||  PCK_DATOS.GL_SEPARADOR_COL  ||                    
                      PCK_DATOS.GL_SEPARADOR_REG),
                        CHR(10),''),CHR(13),'')  ;
        MI_RTA := MI_RTA || MI_REGISTRO;    
    END LOOP DETALLETOTAL;    

    /*
    * DATOS DE REGALIAS PARA CONSTITUIR EL INGRESO
    */
    MI_RTA := MI_RTA || PCK_DATOS.GL_SEPARADOR_HOJ || 'Saldos de Regalias';
    MI_RTA := MI_RTA || PCK_DATOS.GL_SEPARADOR_REG ||
                                    'CLASE CIERRE'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CUENTA'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CUENTA ANTERIROR'      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'TIPO VIGENCIA'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'VIGENCIA EJECUCION'    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'NATURALEZA'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'TERCERO'               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'SUCURSAL'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CENTRO COSTO'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'AUXILIAR'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'FUENTE RECURSO'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REFERENCIA'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'SALDO ABIERTO'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    PCK_DATOS.GL_SEPARADOR_REG ;    
    <<SALDOSREGALIAS>>
    FOR RS IN(
            SELECT CASE WHEN TEM.CLASECIERRE = 'RES' 
                   THEN 'Compromisos por Pagar' 
                   ELSE CASE WHEN TEM.CLASECIERRE = 'REO' 
                        THEN 'Cunetas por Pagar' 
                        ELSE 'Saldos de Apropiación' 
                        END 
                    END CLASECIERRE
                 , TEM.CUENTA
                 , TEM.CUENTAANT
                 , TIPOV.NOMBRE TIPOVIGENCIA
                 , TEM.VIGENCIA
                 , TEM.NATURALEZA  
                 , CASE WHEN TEM.CLASECIERRE ='SAL' THEN TEM.DIS_DET_TERCERO        ELSE CASE WHEN TEM.CLASECIERRE ='REO' THEN TEM.REO_DET_TERCERO        ELSE TEM.RES_DET_TERCERO         END END TERCERO
                 , CASE WHEN TEM.CLASECIERRE ='SAL' THEN TEM.DIS_DET_SUCURSAL       ELSE CASE WHEN TEM.CLASECIERRE ='REO' THEN TEM.REO_DET_SUCURSAL       ELSE TEM.RES_DET_SUCURSAL        END END SUCURSAL
                 , CASE WHEN TEM.CLASECIERRE ='SAL' THEN TEM.DIS_DET_CENTRO_COSTO   ELSE CASE WHEN TEM.CLASECIERRE ='REO' THEN TEM.REO_DET_CENTRO_COSTO   ELSE TEM.RES_DET_CENTRO_COSTO    END END CENTRO_COSTO
                 , CASE WHEN TEM.CLASECIERRE ='SAL' THEN TEM.DIS_DET_AUXILIAR       ELSE CASE WHEN TEM.CLASECIERRE ='REO' THEN TEM.REO_DET_AUXILIAR       ELSE TEM.RES_DET_AUXILIAR        END END AUXILIAR
                 , CASE WHEN TEM.CLASECIERRE ='SAL' THEN TEM.DIS_DET_FUENTE_RECURSO ELSE CASE WHEN TEM.CLASECIERRE ='REO' THEN TEM.REO_DET_FUENTE_RECURSO ELSE TEM.RES_DET_FUENTE_RECURSO  END END FUENTE_RECURSO
                 , CASE WHEN TEM.CLASECIERRE ='SAL' THEN TEM.DIS_DET_REFERENCIA     ELSE CASE WHEN TEM.CLASECIERRE ='REO' THEN TEM.REO_DET_REFERENCIA     ELSE TEM.RES_DET_REFERENCIA      END END REFERENCIA
                 , TEM.VALOR_DEBITO 
        FROM TEMP_CIERRE_PPTAL TEM INNER JOIN TIPOVIGENCIA TIPOV
         ON TEM.TIPOVIGENCIA = TIPOV.CODIGO 
        WHERE TEM.COMPANIA  = UN_COMPANIA
          AND TEM.ANO       = UN_ANOACIERRE
          AND TEM.TIPOCIERRE IN('REG') 

           )
    LOOP
        MI_REGISTRO:= REPLACE(REPLACE(TO_CLOB( 
                        RS.CLASECIERRE   ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CUENTA        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CUENTAANT     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.TIPOVIGENCIA  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.VIGENCIA      ||  PCK_DATOS.GL_SEPARADOR_COL  ||                        
                        RS.NATURALEZA    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.TERCERO       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.SUCURSAL      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CENTRO_COSTO  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.AUXILIAR      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.FUENTE_RECURSO||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.REFERENCIA    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.VALOR_DEBITO  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      PCK_DATOS.GL_SEPARADOR_REG),
                        CHR(10),''),CHR(13),'')  ;
        MI_RTA := MI_RTA || MI_REGISTRO;    
    END LOOP SALDOSREGALIAS;  

    /*
    * DATOS DE COFINANCIADOS PARA CONSTITUIR EL INGRESO
    */
    MI_RTA := MI_RTA || PCK_DATOS.GL_SEPARADOR_HOJ || 'Saldos Cofinanciados';
    MI_RTA := MI_RTA || PCK_DATOS.GL_SEPARADOR_REG ||
                                    'CLASE CIERRE'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CUENTA'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CUENTA ANTERIROR'      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'TIPO VIGENCIA'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'VIGENCIA EJECUCION'    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'NATURALEZA'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'TERCERO'               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'SUCURSAL'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CENTRO COSTO'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'AUXILIAR'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'FUENTE RECURSO'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REFERENCIA'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'SALDO ABIERTO'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    PCK_DATOS.GL_SEPARADOR_REG ;
    <<SALDOSCOFINANCIADOS>>
    FOR RS IN(
            SELECT CASE WHEN TEM.CLASECIERRE = 'RES'
                   THEN 'Compromisos por Pagar'
                   ELSE CASE WHEN TEM.CLASECIERRE = 'REO'
                        THEN 'Cuentas por Pagar'
                        ELSE 'Saldos de Apropiación'
                        END
                    END CLASECIERRE
                 , TEM.CUENTA
                 , TEM.CUENTAANT
                 , TIPOV.NOMBRE TIPOVIGENCIA
                 , TEM.VIGENCIA
                 , TEM.NATURALEZA
                 , CASE WHEN TEM.CLASECIERRE ='SAL' THEN TEM.DIS_DET_TERCERO        ELSE CASE WHEN TEM.CLASECIERRE ='REO' THEN TEM.REO_DET_TERCERO        ELSE TEM.RES_DET_TERCERO         END END TERCERO
                 , CASE WHEN TEM.CLASECIERRE ='SAL' THEN TEM.DIS_DET_SUCURSAL       ELSE CASE WHEN TEM.CLASECIERRE ='REO' THEN TEM.REO_DET_SUCURSAL       ELSE TEM.RES_DET_SUCURSAL        END END SUCURSAL
                 , CASE WHEN TEM.CLASECIERRE ='SAL' THEN TEM.DIS_DET_CENTRO_COSTO   ELSE CASE WHEN TEM.CLASECIERRE ='REO' THEN TEM.REO_DET_CENTRO_COSTO   ELSE TEM.RES_DET_CENTRO_COSTO    END END CENTRO_COSTO
                 , CASE WHEN TEM.CLASECIERRE ='SAL' THEN TEM.DIS_DET_AUXILIAR       ELSE CASE WHEN TEM.CLASECIERRE ='REO' THEN TEM.REO_DET_AUXILIAR       ELSE TEM.RES_DET_AUXILIAR        END END AUXILIAR
                 , CASE WHEN TEM.CLASECIERRE ='SAL' THEN TEM.DIS_DET_FUENTE_RECURSO ELSE CASE WHEN TEM.CLASECIERRE ='REO' THEN TEM.REO_DET_FUENTE_RECURSO ELSE TEM.RES_DET_FUENTE_RECURSO  END END FUENTE_RECURSO
                 , CASE WHEN TEM.CLASECIERRE ='SAL' THEN TEM.DIS_DET_REFERENCIA     ELSE CASE WHEN TEM.CLASECIERRE ='REO' THEN TEM.REO_DET_REFERENCIA     ELSE TEM.RES_DET_REFERENCIA      END END REFERENCIA
                 , TEM.VALOR_DEBITO
        FROM TEMP_CIERRE_PPTAL TEM INNER JOIN TIPOVIGENCIA TIPOV
         ON TEM.TIPOVIGENCIA = TIPOV.CODIGO
        WHERE TEM.COMPANIA  = UN_COMPANIA
          AND TEM.ANO       = UN_ANOACIERRE
          AND TEM.TIPOCIERRE IN('COF')
           )
    LOOP
        MI_REGISTRO:= REPLACE(REPLACE(TO_CLOB(
                        RS.CLASECIERRE   ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CUENTA        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CUENTAANT     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.TIPOVIGENCIA  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.VIGENCIA      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.NATURALEZA    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.TERCERO       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.SUCURSAL      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.CENTRO_COSTO  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.AUXILIAR      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.FUENTE_RECURSO||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.REFERENCIA    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS.VALOR_DEBITO  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        PCK_DATOS.GL_SEPARADOR_REG),
                        CHR(10),''),CHR(13),'')  ;
        MI_RTA := MI_RTA || MI_REGISTRO;
    END LOOP SALDOSCOFINANCIADOS;
    RETURN MI_RTA;
END FC_VALIDARCIERREPLAN;

FUNCTION FC_ELIMINARCIERREPRESUPUESTO
    /*  
        NAME              : En Access 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 23/01/2019
        TIME              : 08:39 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
        DATE MODIFIED     : 18/10/2024
        TIME              :
        DESCRIPTION       : Permite reversar el cierre de acuerdo a los indicadores mostrados
        PARAMETERS        :
        MODIFICATIONS     : Se modifica para que se realice el proceso de reversar cierre pptal

        @NAME:eliminarCierrePlan
        @METHOD:Post
    */  
   (
     UN_COMPANIA             IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE           IN  PCK_SUBTIPOS.TI_ANIO,
     UN_USUARIO              IN  PCK_SUBTIPOS.TI_USUARIO,
     UN_CIERRENORMAL         IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREPASIVO         IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREVIGFUTURAS     IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREVIGFUTUAPASIVO IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREREGALIAS       IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERRECOFINANCIADOS  IN  PCK_SUBTIPOS.TI_LOGICO
   )RETURN CLOB
   AS
    MI_RTA  CLOB; 
BEGIN
    MI_RTA := '0';
    IF UN_CIERRENORMAL <> 0 THEN
        PCK_PRESUPUESTO_CIE.PR_ELIMINARAFECTACIONESPPTAL(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'NOR');
        PCK_PRESUPUESTO_CIE.PR_ELIMINARCIERRETIPO(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'NOR');
    END IF;
    IF UN_CIERREPASIVO <>0 THEN
        PCK_PRESUPUESTO_CIE.PR_ELIMINARAFECTACIONESPPTAL(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'PAE');                         
        PCK_PRESUPUESTO_CIE.PR_ELIMINARCIERRETIPO(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'PAE');
    END IF;
    IF UN_CIERREVIGFUTURAS<>0 THEN
        PCK_PRESUPUESTO_CIE.PR_ELIMINARAFECTACIONESPPTAL(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'VF');
        PCK_PRESUPUESTO_CIE.PR_ELIMINARCIERRETIPO(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'VF');
    END IF;
    IF UN_CIERREVIGFUTUAPASIVO<>0 THEN
        PCK_PRESUPUESTO_CIE.PR_ELIMINARAFECTACIONESPPTAL(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'VFE');
        PCK_PRESUPUESTO_CIE.PR_ELIMINARCIERRETIPO(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'VFE');
    END IF;   
    IF UN_CIERREREGALIAS<>0 THEN
        PCK_PRESUPUESTO_CIE.PR_ELIMINARAFECTACIONESPPTAL(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'REG');
        PCK_PRESUPUESTO_CIE.PR_ELIMINARCIERRETIPO(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'REG');
    END IF;
    IF UN_CIERRECOFINANCIADOS<>0 THEN
        PCK_PRESUPUESTO_CIE.PR_ELIMINARAFECTACIONESPPTAL(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'COF');
        PCK_PRESUPUESTO_CIE.PR_ELIMINARCIERRETIPO(
                         UN_COMPANIA          => UN_COMPANIA,
                         UN_ANOACIERRE        => UN_ANOACIERRE,
                         UN_TIPOCIERRE        => 'COF');
    END IF;
    MI_RTA := '1';
   RETURN MI_RTA;
END FC_ELIMINARCIERREPRESUPUESTO;

PROCEDURE PR_ELIMINARCIERRETIPO
   /*  
        NAME              : En Access 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 23/01/2019
        TIME              : 09:34 AM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite reversar un tipo de cierre
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:cierrePresupuestalCb
        @METHOD:Post
    */
   (
     UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE   IN  PCK_SUBTIPOS.TI_ANIO,
     UN_TIPOCIERRE   IN  CONFIG_CIERRE_PPTAL.TIPOCIERRE%TYPE
   )AS 
   MI_TABLA                       PCK_SUBTIPOS.TI_TABLA;
   MI_CAMPOS                      PCK_SUBTIPOS.TI_CAMPOS;
   MI_VALORES                     PCK_SUBTIPOS.TI_VALORES;
   MI_CONDICION                   PCK_SUBTIPOS.TI_CONDICION;
   MI_CONDICIONDETALLE            PCK_SUBTIPOS.TI_CONDICION;   
   MI_CONDICIONHEADER             PCK_SUBTIPOS.TI_CONDICION;   
   MI_CONDICIONPLAN               PCK_SUBTIPOS.TI_CONDICION;   
   MI_ANOACTUAL                   NUMBER(4,0);
   MI_DIGITOCAMBIO                CONFIG_CIERRE_PPTAL.DIGITOSCAMBIO%TYPE;
BEGIN
    MI_ANOACTUAL     := UN_ANOACIERRE + 1;  
    FOR RS IN (SELECT CLASERESERVA,  TIPO_DIS, 
                      TIPO_RES,      TIPO_REO,
                      DIGITOSCAMBIO, TIPOVIGENCIAFINAL, 
                      GENERAR,       CREAJERARQUIA, 
                      TIPOVIGENCIAINICI
               FROM  CONFIG_CIERRE_PPTAL
               WHERE COMPANIA   = UN_COMPANIA
                 AND TIPOCIERRE = UN_TIPOCIERRE
                 AND GENERAR    NOT IN(0)
              )
    LOOP    
        
        IF UN_TIPOCIERRE = 'COF' THEN 
        
            BEGIN
                SELECT SUBSTR(CODIGO,1,1)
                    INTO MI_DIGITOCAMBIO
                FROM PLAN_PRESUPUESTAL
                WHERE COMPANIA = UN_COMPANIA
                AND ANO = MI_ANOACTUAL
                AND COFINANCIADO <> 0
                GROUP BY SUBSTR(CODIGO,1,1);
            EXCEPTION WHEN NO_DATA_FOUND THEN 
                MI_DIGITOCAMBIO := RS.DIGITOSCAMBIO;
            END;
                
            MI_CONDICIONDETALLE := ' COMPANIA    = ''' || UN_COMPANIA       || '''
                              AND ANO         = '   || MI_ANOACTUAL      || '
                              AND SUBSTR(CUENTA,1,' || LENGTH(MI_DIGITOCAMBIO) || ')=''' || MI_DIGITOCAMBIO || '''';
                
        ELSE
            MI_DIGITOCAMBIO := RS.DIGITOSCAMBIO;
            MI_CONDICIONDETALLE := ' COMPANIA    = ''' || UN_COMPANIA       || '''
                                  AND ANO         = '   || MI_ANOACTUAL      || '
                                  AND SUBSTR(CUENTA,1,' || LENGTH(MI_DIGITOCAMBIO) || ')=''' || MI_DIGITOCAMBIO || '''
                                  AND TIPO_CPTE  IN(''' || RS.TIPO_DIS || ''',''' || RS.TIPO_RES || '''' 
                                                        || CASE WHEN RS.CLASERESERVA NOT IN('APROPIACION','PASAIGUAL') 
                                                                THEN ',''' || RS.TIPO_REO || '''' END || ')';                                             
        END IF;
        
        /*
        * ELIMINA LOS VALORES DE
        */

        BEGIN
            BEGIN
                /*
                * ELIMINACIÓN DE COMPROBANTES
                */
                MI_TABLA     := 'DETALLE_COMPROBANTE_PPTAL';
                
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME( UN_TABLA => MI_TABLA,
                                                      UN_ACCION     => 'E',
                                                      UN_CONDICION => MI_CONDICIONDETALLE);
                                                      
                --DELETE FROM COMPROBANTE_PPTALAFECTADOS WHERE COMPANIA = '001' AND ANO = 2026 AND TIPO_CPTE  IN('DIX','REX','');
                MI_TABLA     := 'COMPROBANTE_PPTALAFECTADOS';
                MI_CONDICIONHEADER :=' COMPANIA = ''' || UN_COMPANIA || '''
                                     AND ANO = ' || MI_ANOACTUAL || '
                                     AND NOT EXISTS (
                                           SELECT 1
                                             FROM DETALLE_COMPROBANTE_PPTAL D
                                            WHERE D.COMPANIA = COMPROBANTE_PPTALAFECTADOS.COMPANIA
                                              AND D.ANO = COMPROBANTE_PPTALAFECTADOS.ANO
                                              AND D.TIPO_CPTE = COMPROBANTE_PPTALAFECTADOS.TIPO_CPTE
                                              AND D.COMPROBANTE = COMPROBANTE_PPTALAFECTADOS.COMPROBANTE
                                        )';
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME( UN_TABLA => MI_TABLA,
                                                      UN_ACCION     => 'E',
                                                      UN_CONDICION => MI_CONDICIONHEADER);                        
                                                      
                MI_TABLA     := 'COMPROBANTE_PPTAL';
                MI_CONDICIONHEADER :=' COMPANIA = ''' || UN_COMPANIA || '''
                                     AND ANO = ' || MI_ANOACTUAL || '
                                     AND NOT EXISTS (
                                           SELECT 1
                                             FROM DETALLE_COMPROBANTE_PPTAL D
                                            WHERE D.COMPANIA = COMPROBANTE_PPTAL.COMPANIA
                                              AND D.ANO = COMPROBANTE_PPTAL.ANO
                                              AND D.TIPO_CPTE = COMPROBANTE_PPTAL.TIPO
                                              AND D.COMPROBANTE = COMPROBANTE_PPTAL.NUMERO
                                        )';
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME( UN_TABLA => MI_TABLA,
                                                      UN_ACCION     => 'E',
                                                      UN_CONDICION => MI_CONDICIONHEADER);
                /*
                * ELIMINACIÓN DE SALDOS
                */
                MI_TABLA     := 'SALDO_PLAN_PPTAL';


                MI_CONDICION := ' COMPANIA    = ''' || UN_COMPANIA       || '''
                              AND ANO         = '   || MI_ANOACTUAL      || '
                              AND SUBSTR(CODIGO,1,' || LENGTH(MI_DIGITOCAMBIO) || ')=''' || MI_DIGITOCAMBIO || '''';

                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME( UN_TABLA => MI_TABLA,
                                                      UN_ACCION     => 'E',
                                                      UN_CONDICION => MI_CONDICION);    
                MI_TABLA     := 'SALDO_AUX_PPTAL';
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME( UN_TABLA => MI_TABLA,
                                                      UN_ACCION     => 'E',
                                                      UN_CONDICION => MI_CONDICION);  
                /*
                * ELIMINACIÓN DE APROPIACIONES INICIALES
                */
                MI_TABLA     := 'APROPIACIONESINICIALES';
                MI_CONDICION := ' COMPANIA    = ''' || UN_COMPANIA       || '''
                              AND ANO         = '   || MI_ANOACTUAL      || '
                              AND SUBSTR(CODIGO,1,' || LENGTH(MI_DIGITOCAMBIO) || ')=''' || MI_DIGITOCAMBIO || '''';
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME( UN_TABLA => MI_TABLA,
                                                      UN_ACCION     => 'E',
                                                      UN_CONDICION => MI_CONDICION);                                        
                /*
                * ELIMINACIÓN DE PLAN_PPTAL_CONFIG
                */
                MI_TABLA     := 'PLAN_PPTAL_CONFIG';
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME( UN_TABLA => MI_TABLA,
                                                      UN_ACCION     => 'E',
                                                      UN_CONDICION => MI_CONDICION);                                       
                /*
                * ELIMINACIÓN DE PLAN_PRESUPUESTAL SE CONTROLA QUE SI LA CONFIGURACIÓN NO CREA JERARQUIA NO LA ELIMINE
                */                
                MI_TABLA     := 'PLAN_PRESUPUESTAL';
                MI_CONDICION := ' COMPANIA    = ''' || UN_COMPANIA       || '''
                              AND ANO         = '   || MI_ANOACTUAL      || '
                              AND ''' || MI_DIGITOCAMBIO || ''' =' || CASE WHEN RS.CREAJERARQUIA IN(0)
                                                                       THEN 'CODIGO' 
                                                                       ELSE 'SUBSTR(CODIGO, 1, LENGTH(''' || MI_DIGITOCAMBIO || ''')) '
                                                                       END;
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME( UN_TABLA => MI_TABLA,
                                                      UN_ACCION     => 'E',
                                                      UN_CONDICION => MI_CONDICION);                                       
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
            PCK_ERR_MSG.RAISE_WITH_MSG
                ( UN_EXC_COD    => SQLCODE
                , UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOERRORDELETEAPROPIA
                , UN_TABLAERROR => MI_TABLA);
        END;        
    END LOOP;
END PR_ELIMINARCIERRETIPO;

PROCEDURE PR_INSERTARAPROPIACIERREREGALI
   /*  
        NAME              : En Access 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 31/01/2019
        TIME              : 04:45 PM
        SOURCE MODULE     : SysmanPH
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite incluir los saldos de regalias en el cierre presupuetal
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:saldoRegalia
        @METHOD:Post
    */
   (
     UN_COMPANIA        IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE      IN  PCK_SUBTIPOS.TI_ANIO
   ) 
AS 
     MI_CLASE       PCK_SUBTIPOS.TI_CLASECOMPROPPTO;
     MI_CONSECUTIVO NUMBER(20,2);
     MI_CONSULTA    VARCHAR2(32000);
     MI_RTA         CLOB;
     MI_TABLA       PCK_SUBTIPOS.TI_TABLA;
     MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
     MI_REEMPLAZOS  PCK_SUBTIPOS.TI_CLAVEVALOR; 
     MI_DIGITO        VARCHAR2(20);
     MI_CREAJERARQUIA NUMBER(1,0);
     MI_TIPOCIERRE       CONFIG_CIERRE_PPTAL.TIPOCIERRE%TYPE;
     MI_CLASERESERVA    CONFIG_CIERRE_PPTAL.CLASERESERVA%TYPE;
BEGIN
    MI_TIPOCIERRE   := 'REG';
    MI_CLASE        := 'SAL';
    MI_CLASERESERVA := 'SALDOS';
    BEGIN 
        SELECT DIGITOSCAMBIO, CREAJERARQUIA 
          INTO MI_DIGITO, MI_CREAJERARQUIA 
        FROM  CONFIG_CIERRE_PPTAL
        WHERE COMPANIA     = UN_COMPANIA
          AND TIPOCIERRE   = MI_TIPOCIERRE
          AND CLASERESERVA = MI_CLASERESERVA
          AND GENERAR    NOT IN(0);
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        RETURN;
    END;     

    SELECT COUNT(REGISTRO)
      INTO MI_CONSECUTIVO
    FROM TEMP_CIERRE_PPTAL;
    MI_CONSULTA := ' SELECT CLASECIERRE
                            , ROWNUM + ' || MI_CONSECUTIVO || ' REGISTRO
                            , COMPANIA
                            , TIPOCIERRE
                            , CLASERESERVA
                            , CREAJERARQUIA
                            , ANO
                            , CUENTA
                            , CUENTAANT
                            , DIGITOS
                            , TIPOVIGENCIA
                            , VIGENCIA
                            , NATURALEZA  
                            , DIS_TIPO_INICIAL 
                            , DIS_TIPO_FINAL
                            , DIS_NUMERO 
                            , DIS_CONSECUTIVO
                            , RES_TIPO_INICIAL
                            , RES_TIPO_FINAL
                            , RES_NUMERO 
                            , RES_CONSECUTIVO 
                            , DIS_CENTRO_COSTO
                            , DIS_AUXILIAR
                            , DIS_FUENTE_RECURSO
                            , DIS_REFERENCIA
                            , DIS_TERCERO
                            , DIS_SUCURSAL
                            , SALDOPORCOMPROMETER
                     FROM (  SELECT ''' || MI_CLASE || ''' CLASECIERRE
                                   , PLAN_PRESUPUESTAL.COMPANIA
                                   , ''' || MI_TIPOCIERRE    || ''' TIPOCIERRE
                                   , ''' || MI_CLASERESERVA  || ''' CLASERESERVA
                                   , '   || MI_CREAJERARQUIA || ' CREAJERARQUIA
                                   , PLAN_PRESUPUESTAL.ANO                           
                                   , ' || CASE WHEN MI_DIGITO IS NULL THEN ' PLAN_PRESUPUESTAL.CODIGO ' ELSE CHR(39) || MI_DIGITO || CHR(39) || ' || SUBSTR(PLAN_PRESUPUESTAL.CODIGO,2 , LENGTH(PLAN_PRESUPUESTAL.CODIGO)) ' END || ' CUENTA
                                   , PLAN_PRESUPUESTAL.CODIGO CUENTAANT
                                   , ' || CASE WHEN MI_DIGITO IS NULL THEN ' NULL ' ELSE CHR(39) || MI_DIGITO || CHR(39) END || ' DIGITOS
                                   , PLAN_PRESUPUESTAL.TIPOVIGENCIA
                                   , PLAN_PRESUPUESTAL.VIGENCIA
                                   , PLAN_PRESUPUESTAL.NATURALEZA
                                   , ''DIS''   DIS_TIPO_INICIAL 
                                   , ''DIS''   DIS_TIPO_FINAL
                                   , -1        DIS_NUMERO 
                                   , -1        DIS_CONSECUTIVO
                                   , ''RES''   RES_TIPO_INICIAL
                                   , ''RES''   RES_TIPO_FINAL
                                   , -1        RES_NUMERO 
                                   , -1        RES_CONSECUTIVO 
                                   , SALDO_AUX_PPTAL.CENTRO_COSTO   DIS_CENTRO_COSTO
                                   , SALDO_AUX_PPTAL.AUXILIAR       DIS_AUXILIAR    
                                   , SALDO_AUX_PPTAL.FUENTE_RECURSO DIS_FUENTE_RECURSO
                                   , SALDO_AUX_PPTAL.REFERENCIA     DIS_REFERENCIA  
                                   , SALDO_AUX_PPTAL.TERCERO        DIS_TERCERO 
                                   , SALDO_AUX_PPTAL.SUCURSAL       DIS_SUCURSAL
                                   , SUM((DECODE(PLAN_PRESUPUESTAL.NATURALEZA,''D'', 
                                                 APROPIACION_DEBITO  - APROPIACION_CREDITO, 
                                                 APROPIACION_CREDITO - APROPIACION_DEBITO )
                                         + ADICION + REDUCCION 
                                         + (DECODE(PLAN_PRESUPUESTAL.NATURALEZA,''D'',
                                                   TRASLADO_DEBITO     - TRASLADO_CREDITO, 
                                                   TRASLADO_CREDITO    - TRASLADO_DEBITO)))
                                         - (REG_NO_CONTRACT + REG_CONTRACT + MODIF_REG_NOCONT +MODIF_REG_CONT)) SALDOPORCOMPROMETER
                                FROM SALDO_AUX_PPTAL INNER JOIN PLAN_PRESUPUESTAL
                                  ON SALDO_AUX_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                                 AND SALDO_AUX_PPTAL.ANO      = PLAN_PRESUPUESTAL.ANO
                                 AND SALDO_AUX_PPTAL.CODIGO   = PLAN_PRESUPUESTAL.CODIGO
                                WHERE PLAN_PRESUPUESTAL.COMPANIA =''' || UN_COMPANIA   || '''
                                  AND PLAN_PRESUPUESTAL.ANO      =  ' || UN_ANOACIERRE || '
                                  AND PLAN_PRESUPUESTAL.REGALIAS NOT IN(0)
                                  AND PLAN_PRESUPUESTAL.NATURALEZA =''D''
                                GROUP BY ''' || MI_CLASE || ''' 
                                        , PLAN_PRESUPUESTAL.COMPANIA
                                        , ''' || MI_TIPOCIERRE    || ''' 
                                        , ''' || MI_CLASERESERVA  || ''' 
                                        , '   || MI_CREAJERARQUIA || ' 
                                        , PLAN_PRESUPUESTAL.ANO                           
                                        , ' || CASE WHEN MI_DIGITO IS NULL THEN ' NULL ' ELSE CHR(39) || MI_DIGITO || CHR(39) || ' || SUBSTR(PLAN_PRESUPUESTAL.CODIGO,2 , LENGTH(PLAN_PRESUPUESTAL.CODIGO)) ' END || ' 
                                        , PLAN_PRESUPUESTAL.CODIGO 
                                        , ' || CASE WHEN MI_DIGITO IS NULL THEN ' NULL ' ELSE CHR(39) || MI_DIGITO || CHR(39) END || ' 
                                        , PLAN_PRESUPUESTAL.TIPOVIGENCIA
                                        , PLAN_PRESUPUESTAL.VIGENCIA
                                        , PLAN_PRESUPUESTAL.NATURALEZA
                                        , ''DIS''    
                                        , ''DIS''   
                                        , -1         
                                        , -1        
                                        , ''RES''   
                                        , ''RES''   
                                        , -1         
                                        , -1         
                                        , SALDO_AUX_PPTAL.CENTRO_COSTO   
                                        , SALDO_AUX_PPTAL.AUXILIAR           
                                        , SALDO_AUX_PPTAL.FUENTE_RECURSO 
                                        , SALDO_AUX_PPTAL.REFERENCIA       
                                        , SALDO_AUX_PPTAL.TERCERO         
                                        , SALDO_AUX_PPTAL.SUCURSAL   
                                 HAVING SUM((DECODE(PLAN_PRESUPUESTAL.NATURALEZA,''D'', 
                                                 APROPIACION_DEBITO  - APROPIACION_CREDITO, 
                                                 APROPIACION_CREDITO - APROPIACION_DEBITO )
                                         + ADICION + REDUCCION 
                                         + (DECODE(PLAN_PRESUPUESTAL.NATURALEZA,''D'',
                                                   TRASLADO_DEBITO     - TRASLADO_CREDITO, 
                                                   TRASLADO_CREDITO    - TRASLADO_DEBITO)))
                                         - (REG_NO_CONTRACT + REG_CONTRACT + MODIF_REG_NOCONT +MODIF_REG_CONT))<>0
                          )';
        MI_CAMPOS := '    CLASECIERRE
                        , REGISTRO
                        , COMPANIA
                        , TIPOCIERRE
                        , CLASERESERVA
                        , CREAJERARQUIA
                        , ANO
                        , CUENTA
                        , CUENTAANT
                        , DIGITOCAMBIO
                        , TIPOVIGENCIA
                        , VIGENCIA
                        , NATURALEZA  
                        , DIS_TIPO_INICIAL 
                        , DIS_TIPO_FINAL
                        , DIS_NUMERO 
                        , DIS_CONSECUTIVO
                        , RES_TIPO_INICIAL
                        , RES_TIPO_FINAL
                        , RES_NUMERO 
                        , RES_CONSECUTIVO 
                        , DIS_DET_CENTRO_COSTO
                        , DIS_DET_AUXILIAR
                        , DIS_DET_FUENTE_RECURSO
                        , DIS_DET_REFERENCIA
                        , DIS_DET_TERCERO
                        , DIS_DET_SUCURSAL
                        , VALOR_DEBITO                        
                        ';       
     BEGIN
            BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'TEMP_CIERRE_PPTAL', 
                                                       UN_ACCION  => 'IS', 
                                                       UN_CAMPOS  => MI_CAMPOS, 
                                                       UN_VALORES => MI_CONSULTA);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
            MI_REEMPLAZOS(0).CLAVE := 'COMPANIA';
            MI_REEMPLAZOS(0).VALOR := UN_COMPANIA;
            MI_REEMPLAZOS(1).CLAVE := 'ANIO';
            MI_REEMPLAZOS(1).VALOR := UN_ANOACIERRE;        
            MI_REEMPLAZOS(2).CLAVE := 'CLASE';
            MI_REEMPLAZOS(2).VALOR := MI_CLASE;  
            MI_REEMPLAZOS(3).CLAVE := 'DIGITO';
            MI_REEMPLAZOS(3).VALOR := MI_DIGITO;
            MI_REEMPLAZOS(4).CLAVE := 'TIPOCIERRE';
            MI_REEMPLAZOS(4).VALOR := FC_NOMBRETIPOCIERRE(MI_TIPOCIERRE);
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOERRORINSERTTEMPORAL,
              UN_TABLAERROR => 'TEMP_CIERRE_PPTAL',
              UN_REEMPLAZOS => MI_REEMPLAZOS  
            );  
        END;   
END PR_INSERTARAPROPIACIERREREGALI;

PROCEDURE PR_RECLASIFICACION
   /*  
        NAME              : En Access 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 20/02/2019
        TIME              : 11:55 AM
        SOURCE MODULE     : 
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite actualizar los rubros y auxiliares en el cierre presupuestal 
                            cuando se tiene configurado el indicador de reclasificar, en la configuración 
                            del cierre presupuestal
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:saldoRegalia
        @METHOD:Post
    */
   (
     UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
     UN_CLASERESERVA IN  CONFIG_CIERRE_PPTAL.CLASERESERVA%TYPE,
     UN_TIPOCIERRE   IN  CONFIG_CIERRE_PPTAL.TIPOCIERRE%TYPE,
     UN_CLASECIERRE  IN  VARCHAR2,
     UN_PREFIJO      IN  PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL
   )
AS
    MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGENOEXISTE  PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR; 
BEGIN

     BEGIN
        BEGIN
            MI_MERGEUSING:= '   SELECT TEMP_CIERRE_PPTAL.COMPANIA, 
                                       TEMP_CIERRE_PPTAL.CLASECIERRE, 
                                       TEMP_CIERRE_PPTAL.REGISTRO,
                                       NVL(PLAN_PPTAL_CONFIG.' || UN_CLASECIERRE || '_CODIGO_CIERRE,         TEMP_CIERRE_PPTAL.CUENTAANT)              CODIGO,
                                       NVL(PLAN_PPTAL_CONFIG.' || UN_CLASECIERRE || '_AUXILIAR_CIERRE,       TEMP_CIERRE_PPTAL.' || UN_PREFIJO || '_DET_AUXILIAR)       ' || UN_PREFIJO || '_DET_AUXILIAR,
                                       NVL(PLAN_PPTAL_CONFIG.' || UN_CLASECIERRE || '_CENTRO_COSTO_CIERRE,   TEMP_CIERRE_PPTAL.' || UN_PREFIJO || '_DET_CENTRO_COSTO)   ' || UN_PREFIJO || '_DET_CENTRO_COSTO,
                                       NVL(PLAN_PPTAL_CONFIG.' || UN_CLASECIERRE || '_FUENTE_RECURSO_CIERRE, TEMP_CIERRE_PPTAL.' || UN_PREFIJO || '_DET_FUENTE_RECURSO) ' || UN_PREFIJO || '_DET_FUENTE_RECURSO,
                                       NVL(PLAN_PPTAL_CONFIG.' || UN_CLASECIERRE || '_REFERENCIA_CIERRE,     TEMP_CIERRE_PPTAL.' || UN_PREFIJO || '_DET_REFERENCIA)     ' || UN_PREFIJO || '_DET_REFERENCIA,
                                       0 CREAJERARQUIA 
                                FROM TEMP_CIERRE_PPTAL INNER JOIN PLAN_PRESUPUESTAL
                                  ON TEMP_CIERRE_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                                 AND TEMP_CIERRE_PPTAL.ANO      = PLAN_PRESUPUESTAL.ANO
                                 AND TEMP_CIERRE_PPTAL.CUENTAANT= PLAN_PRESUPUESTAL.CODIGO
                                INNER JOIN PLAN_PPTAL_CONFIG
                                  ON PLAN_PRESUPUESTAL.COMPANIA = PLAN_PPTAL_CONFIG.COMPANIA
                                 AND PLAN_PRESUPUESTAL.ANO      = PLAN_PPTAL_CONFIG.ANO
                                 AND PLAN_PRESUPUESTAL.CODIGO   = PLAN_PPTAL_CONFIG.CODIGO
                                 AND PLAN_PPTAL_CONFIG.AUXILIAR     = CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_GEN IN (0)
                                                                           THEN ''99999999999999999999''
                                                                           ELSE TEMP_CIERRE_PPTAL.' || UN_PREFIJO || '_DET_AUXILIAR
                                                                           END 
                                 AND PLAN_PPTAL_CONFIG.TERCERO      = CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_TER IN (0)
                                                                           THEN ''999999999999999999''
                                                                           ELSE TEMP_CIERRE_PPTAL.' || UN_PREFIJO || '_DET_TERCERO
                                                                           END 
                                 AND PLAN_PPTAL_CONFIG.SUCURSAL     = CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_TER IN (0)
                                                                           THEN ''999''
                                                                           ELSE TEMP_CIERRE_PPTAL.' || UN_PREFIJO || '_DET_SUCURSAL
                                                                           END                                       
                                 AND PLAN_PPTAL_CONFIG.CENTRO_COSTO = CASE WHEN PLAN_PRESUPUESTAL.MAN_CEN_CTO IN (0)
                                                                           THEN ''99999999999999999999''
                                                                           ELSE TEMP_CIERRE_PPTAL.' || UN_PREFIJO || '_DET_CENTRO_COSTO
                                                                           END 
                                 AND PLAN_PPTAL_CONFIG.REFERENCIA   = CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_REF IN (0)
                                                                           THEN ''99999999999999999999''
                                                                           ELSE TEMP_CIERRE_PPTAL.' || UN_PREFIJO || '_DET_REFERENCIA
                                                                           END 
                                 AND PLAN_PPTAL_CONFIG.FUENTE_RECURSO = CASE WHEN PLAN_PRESUPUESTAL.MAN_AUX_FUE IN (0)
                                                                             THEN ''99999999999999999999''
                                                                             ELSE TEMP_CIERRE_PPTAL.' || UN_PREFIJO || '_DET_FUENTE_RECURSO
                                                                             END
                                WHERE TEMP_CIERRE_PPTAL.COMPANIA    = ''' || UN_COMPANIA     || '''
                                  AND TEMP_CIERRE_PPTAL.CLASECIERRE = ''' || UN_CLASECIERRE  || '''
                                  AND TEMP_CIERRE_PPTAL.TIPOCIERRE  = ''' || UN_TIPOCIERRE   || '''
                                  AND TEMP_CIERRE_PPTAL.CLASERESERVA= ''' || UN_CLASERESERVA || '''';
            PCK_DATOS.GL_RTA :=  PCK_DATOS.FC_ACME(
                             UN_TABLA       => 'TEMP_CIERRE_PPTAL',
                             UN_ACCION      => 'MM',
                             UN_MERGEUSING  => MI_MERGEUSING,
                             UN_MERGEENLACE => '    TABLA.COMPANIA    = VISTA.COMPANIA 
                                                AND TABLA.CLASECIERRE = VISTA.CLASECIERRE 
                                                AND TABLA.REGISTRO    = VISTA.REGISTRO',
                             UN_MERGEEXISTE => 'UPDATE 
                                            SET TABLA.CUENTA                 = VISTA.CODIGO,
                                                TABLA.' || UN_PREFIJO || '_DET_AUXILIAR       = VISTA.' || UN_PREFIJO || '_DET_AUXILIAR, 
                                                TABLA.' || UN_PREFIJO || '_DET_CENTRO_COSTO   = VISTA.' || UN_PREFIJO || '_DET_CENTRO_COSTO,
                                                TABLA.' || UN_PREFIJO || '_DET_FUENTE_RECURSO = VISTA.' || UN_PREFIJO || '_DET_FUENTE_RECURSO,
                                                TABLA.' || UN_PREFIJO || '_DET_REFERENCIA     = VISTA.' || UN_PREFIJO || '_DET_REFERENCIA,
                                                TABLA.CREAJERARQUIA          = VISTA.CREAJERARQUIA'); 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        MI_REEMPLAZOS(1).CLAVE := 'TIPOCIERRE';
        MI_REEMPLAZOS(1).VALOR := FC_NOMBRETIPOCIERRE(UN_TIPOCIERRE => UN_TIPOCIERRE);
        MI_REEMPLAZOS(2).CLAVE := 'CLASERESERVA';
        MI_REEMPLAZOS(2).VALOR := UN_CLASERESERVA;        
        PCK_ERR_MSG.RAISE_WITH_MSG(
                                  UN_EXC_COD    => SQLCODE,
                                  UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOMERGERECLASIFICA,
                                  UN_TABLAERROR => 'TEMP_CIERRE_PPTAL',
                                  UN_REEMPLAZOS => MI_REEMPLAZOS  
        );
    END;
END PR_RECLASIFICACION;

PROCEDURE PR_INSERT_COFINANCIADO
   /*
        NAME              : En Access
        AUTHORS           : SYSMAN  SAS
        AUTHOR            : GERMAN DAVID ROJAS G
        DATE              : 23/10/2024
        TIME              : 04:45 PM
        SOURCE MODULE     : 
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite incluir los saldos de los rubros de cofinanciados en el cierre presupuetal
        PARAMETERS        :
        MODIFICATIONS     :
    */
   (
     UN_COMPANIA             IN  PCK_SUBTIPOS.TI_COMPANIA,
     UN_ANOACIERRE           IN  PCK_SUBTIPOS.TI_ANIO,
     UN_CIERRECOFINANCIADOS  IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_FECHACIERRE          IN  DATE DEFAULT NULL,
     UN_USUARIO              IN  PCK_SUBTIPOS.TI_USUARIO DEFAULT ''
   )
AS
     MI_CLASE           PCK_SUBTIPOS.TI_CLASECOMPROPPTO;
     MI_CONSECUTIVO     NUMBER(20,2);
     MI_CONSULTA        VARCHAR2(32000);
     MI_RTA             CLOB;
     MI_TABLA           PCK_SUBTIPOS.TI_TABLA;
     MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
     MI_REEMPLAZOS      PCK_SUBTIPOS.TI_CLAVEVALOR;
     MI_DIGITO          VARCHAR2(20);
     MI_CREAJERARQUIA   NUMBER(1,0);
     MI_TIPOCIERRE      CONFIG_CIERRE_PPTAL.TIPOCIERRE%TYPE;
     MI_CLASERESERVA    CONFIG_CIERRE_PPTAL.CLASERESERVA%TYPE;
     MI_CLASECAJA       VARCHAR2(5);
     MI_SELECT          VARCHAR2(32000);
     MI_FECHACOMP       VARCHAR2(50);
     MI_ANOACTUAL       NUMBER(4,0);
     MI_EXISTE          NUMBER := 0;
     MI_VALORES         PCK_SUBTIPOS.TI_VALORES;
     MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
     MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    MI_TIPOCIERRE   := 'COF';
    MI_CLASE        := 'SAL';
    MI_CLASERESERVA := 'SALDOS';
    
    IF UN_CIERRECOFINANCIADOS = 0 THEN
    
    BEGIN
        SELECT DIGITOSCAMBIO, CREAJERARQUIA
          INTO MI_DIGITO, MI_CREAJERARQUIA
        FROM  CONFIG_CIERRE_PPTAL
        WHERE COMPANIA     = UN_COMPANIA
          AND TIPOCIERRE   = MI_TIPOCIERRE
          AND CLASERESERVA = MI_CLASERESERVA
          AND GENERAR    NOT IN(0);
    EXCEPTION WHEN NO_DATA_FOUND THEN
        RETURN;
    END;
    SELECT COUNT(REGISTRO)
      INTO MI_CONSECUTIVO
    FROM TEMP_CIERRE_PPTAL;
    MI_CONSULTA := ' SELECT CLASECIERRE
                            , ROWNUM + ' || MI_CONSECUTIVO || ' REGISTRO
                            , COMPANIA
                            , TIPOCIERRE
                            , CLASERESERVA
                            , CREAJERARQUIA
                            , ANO
                            , CUENTA
                            , CUENTAANT
                            , DIGITOS
                            , TIPOVIGENCIA
                            , VIGENCIA
                            , NATURALEZA
                            , DIS_TIPO_INICIAL
                            , DIS_TIPO_FINAL
                            , DIS_NUMERO
                            , DIS_CONSECUTIVO
                            , RES_TIPO_INICIAL
                            , RES_TIPO_FINAL
                            , RES_NUMERO
                            , RES_CONSECUTIVO
                            , DIS_CENTRO_COSTO
                            , DIS_AUXILIAR
                            , DIS_FUENTE_RECURSO
                            , DIS_REFERENCIA
                            , DIS_TERCERO
                            , DIS_SUCURSAL
                            , SALDOPORCOMPROMETER
                     FROM (  SELECT ''' || MI_CLASE || ''' CLASECIERRE
                                   , PLAN_PRESUPUESTAL.COMPANIA
                                   , ''' || MI_TIPOCIERRE    || ''' TIPOCIERRE
                                   , ''' || MI_CLASERESERVA  || ''' CLASERESERVA
                                   , '   || MI_CREAJERARQUIA || ' CREAJERARQUIA
                                   , PLAN_PRESUPUESTAL.ANO
                                   , ' || CASE WHEN MI_DIGITO IS NULL THEN ' PLAN_PRESUPUESTAL.CODIGO ' ELSE CHR(39) || MI_DIGITO || CHR(39) || ' || SUBSTR(PLAN_PRESUPUESTAL.CODIGO,2 , LENGTH(PLAN_PRESUPUESTAL.CODIGO)) ' END || ' CUENTA
                                   , PLAN_PRESUPUESTAL.CODIGO CUENTAANT
                                   , ' || CASE WHEN MI_DIGITO IS NULL THEN ' NULL ' ELSE CHR(39) || MI_DIGITO || CHR(39) END || ' DIGITOS
                                   , PLAN_PRESUPUESTAL.TIPOVIGENCIA
                                   , PLAN_PRESUPUESTAL.VIGENCIA
                                   , PLAN_PRESUPUESTAL.NATURALEZA
                                   , ''DIS''   DIS_TIPO_INICIAL
                                   , ''DIS''   DIS_TIPO_FINAL
                                   , -1        DIS_NUMERO
                                   , -1        DIS_CONSECUTIVO
                                   , ''RES''   RES_TIPO_INICIAL
                                   , ''RES''   RES_TIPO_FINAL
                                   , -1        RES_NUMERO
                                   , -1        RES_CONSECUTIVO
                                   , SALDO_AUX_PPTAL.CENTRO_COSTO   DIS_CENTRO_COSTO
                                   , SALDO_AUX_PPTAL.AUXILIAR       DIS_AUXILIAR
                                   , SALDO_AUX_PPTAL.FUENTE_RECURSO DIS_FUENTE_RECURSO
                                   , SALDO_AUX_PPTAL.REFERENCIA     DIS_REFERENCIA
                                   , SALDO_AUX_PPTAL.TERCERO        DIS_TERCERO
                                   , SALDO_AUX_PPTAL.SUCURSAL       DIS_SUCURSAL
                                   , SUM((DECODE(PLAN_PRESUPUESTAL.NATURALEZA,''D'',
                                                 APROPIACION_DEBITO  - APROPIACION_CREDITO,
                                                 APROPIACION_CREDITO - APROPIACION_DEBITO )
                                         + ADICION + REDUCCION
                                         + (DECODE(PLAN_PRESUPUESTAL.NATURALEZA,''D'',
                                                   TRASLADO_DEBITO     - TRASLADO_CREDITO,
                                                   TRASLADO_CREDITO    - TRASLADO_DEBITO)))
                                         - (EJE_PPT_DEBITO-EJE_PPT_CREDITO)) SALDOPORCOMPROMETER
                                FROM SALDO_AUX_PPTAL INNER JOIN PLAN_PRESUPUESTAL
                                  ON SALDO_AUX_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
                                 AND SALDO_AUX_PPTAL.ANO      = PLAN_PRESUPUESTAL.ANO
                                 AND SALDO_AUX_PPTAL.CODIGO   = PLAN_PRESUPUESTAL.CODIGO
                                WHERE PLAN_PRESUPUESTAL.COMPANIA =''' || UN_COMPANIA   || '''
                                  AND PLAN_PRESUPUESTAL.ANO      =  ' || UN_ANOACIERRE || '
                                  AND PLAN_PRESUPUESTAL.COFINANCIADO NOT IN(0)
                                  AND PLAN_PRESUPUESTAL.NATURALEZA =''D''
                                GROUP BY ''' || MI_CLASE || '''
                                        , PLAN_PRESUPUESTAL.COMPANIA
                                        , ''' || MI_TIPOCIERRE    || '''
                                        , ''' || MI_CLASERESERVA  || '''
                                        , '   || MI_CREAJERARQUIA || '
                                        , PLAN_PRESUPUESTAL.ANO
                                        , ' || CASE WHEN MI_DIGITO IS NULL THEN ' NULL ' ELSE CHR(39) || MI_DIGITO || CHR(39) || ' || SUBSTR(PLAN_PRESUPUESTAL.CODIGO,2 , LENGTH(PLAN_PRESUPUESTAL.CODIGO)) ' END || '
                                        , PLAN_PRESUPUESTAL.CODIGO
                                        , ' || CASE WHEN MI_DIGITO IS NULL THEN ' NULL ' ELSE CHR(39) || MI_DIGITO || CHR(39) END || '
                                        , PLAN_PRESUPUESTAL.TIPOVIGENCIA
                                        , PLAN_PRESUPUESTAL.VIGENCIA
                                        , PLAN_PRESUPUESTAL.NATURALEZA
                                        , ''DIS''
                                        , ''DIS''
                                        , -1
                                        , -1
                                        , ''RES''
                                        , ''RES''
                                        , -1
                                        , -1
                                        , SALDO_AUX_PPTAL.CENTRO_COSTO
                                        , SALDO_AUX_PPTAL.AUXILIAR
                                        , SALDO_AUX_PPTAL.FUENTE_RECURSO
                                        , SALDO_AUX_PPTAL.REFERENCIA
                                        , SALDO_AUX_PPTAL.TERCERO
                                        , SALDO_AUX_PPTAL.SUCURSAL
                                 HAVING SUM((DECODE(PLAN_PRESUPUESTAL.NATURALEZA,''D'',
                                                 APROPIACION_DEBITO  - APROPIACION_CREDITO,
                                                 APROPIACION_CREDITO - APROPIACION_DEBITO )
                                         + ADICION + REDUCCION
                                         + (DECODE(PLAN_PRESUPUESTAL.NATURALEZA,''D'',
                                                   TRASLADO_DEBITO     - TRASLADO_CREDITO,
                                                   TRASLADO_CREDITO    - TRASLADO_DEBITO)))
                                         - (EJE_PPT_DEBITO-EJE_PPT_CREDITO))<>0
                          )';
        MI_CAMPOS := '    CLASECIERRE
                        , REGISTRO
                        , COMPANIA
                        , TIPOCIERRE
                        , CLASERESERVA
                        , CREAJERARQUIA
                        , ANO
                        , CUENTA
                        , CUENTAANT
                        , DIGITOCAMBIO
                        , TIPOVIGENCIA
                        , VIGENCIA
                        , NATURALEZA
                        , DIS_TIPO_INICIAL
                        , DIS_TIPO_FINAL
                        , DIS_NUMERO
                        , DIS_CONSECUTIVO
                        , RES_TIPO_INICIAL
                        , RES_TIPO_FINAL
                        , RES_NUMERO
                        , RES_CONSECUTIVO
                        , DIS_DET_CENTRO_COSTO
                        , DIS_DET_AUXILIAR
                        , DIS_DET_FUENTE_RECURSO
                        , DIS_DET_REFERENCIA
                        , DIS_DET_TERCERO
                        , DIS_DET_SUCURSAL
                        , VALOR_DEBITO
                        ';
     BEGIN
            BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'TEMP_CIERRE_PPTAL',
                                                       UN_ACCION  => 'IS',
                                                       UN_CAMPOS  => MI_CAMPOS,
                                                       UN_VALORES => MI_CONSULTA);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
            MI_REEMPLAZOS(0).CLAVE := 'COMPANIA';
            MI_REEMPLAZOS(0).VALOR := UN_COMPANIA;
            MI_REEMPLAZOS(1).CLAVE := 'ANIO';
            MI_REEMPLAZOS(1).VALOR := UN_ANOACIERRE;
            MI_REEMPLAZOS(2).CLAVE := 'CLASE';
            MI_REEMPLAZOS(2).VALOR := MI_CLASE;
            MI_REEMPLAZOS(3).CLAVE := 'DIGITO';
            MI_REEMPLAZOS(3).VALOR := MI_DIGITO;
            MI_REEMPLAZOS(4).CLAVE := 'TIPOCIERRE';
            MI_REEMPLAZOS(4).VALOR := FC_NOMBRETIPOCIERRE(MI_TIPOCIERRE);
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOERRORINSERTTEMPORAL,
              UN_TABLAERROR => 'TEMP_CIERRE_PPTAL',
              UN_REEMPLAZOS => MI_REEMPLAZOS
            );
        END;
        
        --PASA LOS REGISTROS DE COMPROMISO ABIERTOS DE COFINANCIADOS
        MI_CLASE:='RES';
        MI_TIPOCIERRE := 'DCO';
    
        MI_CONSULTA:= 'SELECT ''' || MI_CLASE || ''' CLASECIERRE
                                  ,ROWNUM + ' || MI_CONSECUTIVO || ' REGISTRO
                                  ,DP.COMPANIA
                                  ,''' || MI_TIPOCIERRE  || ''' TIPOCIERRE
                                  ,''' || MI_TIPOCIERRE || ''' CLASERESERVA
                                  ,-1 CREAJERARQUIA
                                  ,DP.ANO  ANO
                                  ,DP.TIPO_CPTE_AFECT          DIS_TIPO_INICIAL
                                  ,DP.TIPO_CPTE_AFECT          DIS_TIPO_FINAL
                                  ,DP.CMPTE_AFECTADO           DIS_NUMERO
                                  ,DP.CONSECUTIVOPPTO          DIS_CONSECUTIVO
                                  ,DP.TIPO_CPTE                RES_TIPO_INICIAL
                                  ,DP.TIPO_CPTE                RES_TIPO_FINAL
                                  ,DP.COMPROBANTE              RES_NUMERO
                                  ,DP.CONSECUTIVO              RES_CONSECUTIVO
                                  ,DP.CUENTA CUENTA
                                  ,DP.CUENTA CUENTAANT
                                  ,NULL DIGITOS
                                  ,PP.TIPOVIGENCIA
                                  ,PP.VIGENCIA
                                  ,DP.NATURALEZA
                                  ,DIS.CENTRO_COSTO        DIS_CENTRO_COSTO
                                  ,DIS.AUXILIAR            DIS_AUXILIAR
                                  ,DIS.FUENTE_RECURSO      DIS_FUENTE_RECURSO
                                  ,DIS.REFERENCIA          DIS_REFERENCIA
                                  ,DIS.TERCERO             DIS_TERCERO
                                  ,DIS.SUCURSAL            DIS_SUCURSAL
                                  ,DIS.DESCRIPCION         DIS_DESCRIPCION
                                  ,DIS.DESTINO             DIS_DESTINO
                                  ,NVL(DIS.TEXTO,''.'')    DIS_TEXTO
                                  ,DIS.NRO_DOCUMENTO       DIS_NRO_DOCUMENTO
                                  ,DIS.TIPOCONTRATO        DIS_TIPOCONTRATO
                                  ,DIS.NUMEROCONTRATO      DIS_NUMEROCONTRATO
                                  ,DIS.CODSOLICITANTE      DIS_CODSOLICITANTE
                                  ,DIS.SUCSOLICITANTE      DIS_SUCSOLICITANTE
                                  ,DIS.COD_PROYECTO_PPTAL  DIS_COD_PROYECTO_PPTAL
                                  ,DIS.ANOPROYECTO         DIS_ANOPROYECTO
                                  ,DIS.LUGAR               DIS_LUGAR
                                  ,DIS.FUENTE_FINANCIACION DIS_FUENTE_FINANCIACION
                                  ,DIS.ASIGNACION          DIS_ASIGNACION
                                  ,DIS.DEPENDENCIA         DIS_DEPENDENCIA
                                  ,DIS_DET.CENTRO_COSTO    DIS_DET_CENTRO_COSTO
                                  ,DIS_DET.AUXILIAR        DIS_DET_AUXILIAR
                                  ,DIS_DET.FUENTE_RECURSO  DIS_DET_FUENTE_RECURSO
                                  ,DIS_DET.REFERENCIA      DIS_DET_REFERENCIA
                                  ,DIS_DET.TERCERO         DIS_DET_TERCERO
                                  ,DIS_DET.SUCURSAL        DIS_DET_SUCURSAL
                                  ,DIS_DET.DESCRIPCION     DIS_DET_DESCRIPCION
                                  ,DIS_DET.NRO_DOCUMENTO   DIS_DET_NRO_DOCUMENTO
                                  ,DIS_DET.TIPOT           DIS_DET_TIPOT
                                  ,DIS_DET.CLASET          DIS_DET_CLASET
                                  ,DIS_DET.CMPTE_SOLICI_AFECTADO DIS_DET_CMPTE_SOLICI_AFECTADO
                                  ,DIS_DET.DEPENDENCIA     DIS_DET_DEPENDENCIA
                                  ,DIS_DET.NUMERO_SOLICITUD DIS_DET_NUMERO_SOLICITUD
                                  ,DIS_DET.TIPO_SOLICITUD  DIS_DET_TIPO_SOLICITUD
                                  ,DIS_DET.TIPOCONTRATO    DIS_DET_TIPOCONTRATO
                                  ,DIS_DET.NUMEROCONTRATO  DIS_DET_NUMEROCONTRATO
                                  ,RES.CENTRO_COSTO        RES_CENTRO_COSTO
                                  ,RES.AUXILIAR            RES_AUXILIAR
                                  ,RES.FUENTE_RECURSO      RES_FUENTE_RECURSO
                                  ,RES.REFERENCIA          RES_REFERENCIA
                                  ,RES.TERCERO             RES_TERCERO
                                  ,RES.SUCURSAL            RES_SUCURSAL
                                  ,RES.DESCRIPCION         RES_DESCRIPCION
                                  ,RES.DESTINO             RES_DESTINO
                                  ,NVL(RES.TEXTO,''.'')    RES_TEXTO
                                  ,RES.NRO_DOCUMENTO       RES_NRO_DOCUMENTO
                                  ,RES.TIPOCONTRATO        RES_TIPOCONTRATO
                                  ,RES.NUMEROCONTRATO      RES_NUMEROCONTRATO
                                  ,RES.CODSOLICITANTE      RES_CODSOLICITANTE
                                  ,RES.SUCSOLICITANTE      RES_SUCSOLICITANTE
                                  ,RES.COD_PROYECTO_PPTAL  RES_COD_PROYECTO_PPTAL
                                  ,RES.ANOPROYECTO         RES_ANOPROYECTO
                                  ,RES.LUGAR               RES_LUGAR
                                  ,RES.FUENTE_FINANCIACION RES_FUENTE_FINANCIACION
                                  ,RES.ASIGNACION          RES_ASIGNACION
                                  ,RES.DEPENDENCIA         RES_DEPENDENCIA
                                  ,DP.CENTRO_COSTO         RES_DET_CENTRO_COSTO
                                  ,DP.AUXILIAR             RES_DET_AUXILIAR
                                  ,DP.FUENTE_RECURSO       RES_DET_FUENTE_RECURSO
                                  ,DP.REFERENCIA           RES_DET_REFERENCIA
                                  ,DP.TERCERO              RES_DET_TERCERO
                                  ,DP.SUCURSAL             RES_DET_SUCURSAL
                                  ,DP.DESCRIPCION          RES_DET_DESCRIPCION
                                  ,DP.NRO_DOCUMENTO        RES_DET_NRO_DOCUMENTO
                                  ,DP.TIPOT                RES_DET_TIPOT
                                  ,DP.CLASET               RES_DET_CLASET
                                  ,DP.CMPTE_SOLICI_AFECTADO RES_DET_CMPTE_SOLICI_AFECTADO
                                  ,DP.DEPENDENCIA          RES_DET_DEPENDENCIA
                                  ,DP.NUMERO_SOLICITUD     RES_DET_NUMERO_SOLICITUD
                                  ,DP.TIPO_SOLICITUD       RES_DET_TIPO_SOLICITUD
                                  ,DP.TIPOCONTRATO         RES_DET_TIPOCONTRATO
                                  ,DP.NUMEROCONTRATO       RES_DET_NUMEROCONTRATO
                                  ,((DP.VALOR_DEBITO        - DP.VALOR_CREDITO       )
                                      -(DP.DEBITO_AFECTADO     - DP.CREDITO_AFECTADO    )
                                      +(DP.MODIFICACION_DEBITO - DP.MODIFICACION_CREDITO)) AS VALOR_DEBITO
                                  ,NULL 
                                  ,DP.SECTOR
                                 ,DP.PROGRAMA
                                 ,DP.SUBPROGRAMA
                                 ,DP.COD_PROD_CUIPO
                                 ,DP.CODIGO_BPIN
                                 ,DP.CODIGO_CCPET
                                 ,DP.CODIGO_CPC
                                 ,DP.CODIGOUNIDADEJE
                                 ,DP.FUENTE_CUIPO
                                 ,DP.CODIGOCCPETREGA
                                 ,DP.POLITICA_PUBLICA
                                 ,DP.DETALLE_SECTORIAL
                             FROM DETALLE_COMPROBANTE_PPTAL DP INNER JOIN PLAN_PRESUPUESTAL PP
                               ON DP.COMPANIA = PP.COMPANIA
                              AND DP.ANO      = PP.ANO
                              AND DP.CUENTA   = PP.CODIGO
                             INNER JOIN TIPO_COMPROBPP TC
                               ON DP.COMPANIA  = TC.COMPANIA
                              AND DP.TIPO_CPTE = TC.CODIGO
                             INNER JOIN COMPROBANTE_PPTAL RES
                               ON DP.COMPANIA        = RES.COMPANIA
                              AND DP.ANO             = RES.ANO
                              AND DP.TIPO_CPTE       = RES.TIPO
                              AND DP.COMPROBANTE     = RES.NUMERO
                             INNER JOIN DETALLE_COMPROBANTE_PPTAL DIS_DET
                               ON DP.COMPANIA        = DIS_DET.COMPANIA
                              AND DP.ANO_AFECT       = DIS_DET.ANO
                              AND DP.TIPO_CPTE_AFECT = DIS_DET.TIPO_CPTE
                              AND DP.CMPTE_AFECTADO  = DIS_DET.COMPROBANTE
                              AND DP.CONSECUTIVOPPTO = DIS_DET.CONSECUTIVO
                             INNER JOIN COMPROBANTE_PPTAL DIS
                               ON DIS_DET.COMPANIA    = DIS.COMPANIA
                              AND DIS_DET.ANO         = DIS.ANO
                              AND DIS_DET.TIPO_CPTE   = DIS.TIPO
                              AND DIS_DET.COMPROBANTE = DIS.NUMERO
                            WHERE DP.COMPANIA   = ''' || UN_COMPANIA   || '''
                              AND DP.ANO        = '   || UN_ANOACIERRE || '
                              AND PP.NATURALEZA = ''D''
                              AND PP.COFINANCIADO NOT IN(0)
                              AND TC.CLASE IN (''' || MI_CLASE || ''')
                              AND ((DP.VALOR_DEBITO        - DP.VALOR_CREDITO       )
                                 - (DP.DEBITO_AFECTADO     - DP.CREDITO_AFECTADO    )
                                 + (DP.MODIFICACION_DEBITO - DP.MODIFICACION_CREDITO))>0';
                                 
            MI_CAMPOS := '    CLASECIERRE
                            , REGISTRO
                            , COMPANIA
                            , TIPOCIERRE
                            , CLASERESERVA
                            , CREAJERARQUIA
                            , ANO
                            , DIS_TIPO_INICIAL
                            , DIS_TIPO_FINAL
                            , DIS_NUMERO
                            , DIS_CONSECUTIVO
                            , RES_TIPO_INICIAL
                            , RES_TIPO_FINAL
                            , RES_NUMERO
                            , RES_CONSECUTIVO
                            , CUENTA
                            , CUENTAANT
                            , DIGITOCAMBIO
                            , TIPOVIGENCIA
                            , VIGENCIA
                            , NATURALEZA
                            , DIS_CENTRO_COSTO
                            , DIS_AUXILIAR
                            , DIS_FUENTE_RECURSO
                            , DIS_REFERENCIA
                            , DIS_TERCERO
                            , DIS_SUCURSAL
                            , DIS_DESCRIPCION
                            , DIS_DESTINO
                            , DIS_TEXTO
                            , DIS_NRO_DOCUMENTO
                            , DIS_TIPOCONTRATO
                            , DIS_NUMEROCONTRATO
                            , DIS_CODSOLICITANTE
                            , DIS_SUCSOLICITANTE
                            , DIS_COD_PROYECTO_PPTAL
                            , DIS_ANOPROYECTO
                            , DIS_LUGAR
                            , DIS_FUENTE_FINANCIACION
                            , DIS_ASIGNACION
                            , DIS_DEPENDENCIA
                            , DIS_DET_CENTRO_COSTO
                            , DIS_DET_AUXILIAR
                            , DIS_DET_FUENTE_RECURSO
                            , DIS_DET_REFERENCIA
                            , DIS_DET_TERCERO
                            , DIS_DET_SUCURSAL
                            , DIS_DET_DESCRIPCION
                            , DIS_DET_NRO_DOCUMENTO
                            , DIS_DET_TIPOT
                            , DIS_DET_CLASET
                            , DIS_DET_CMPTE_SOLICI_AFECTADO
                            , DIS_DET_DEPENDENCIA
                            , DIS_DET_NUMERO_SOLICITUD
                            , DIS_DET_TIPO_SOLICITUD
                            , DIS_DET_TIPOCONTRATO
                            , DIS_DET_NUMEROCONTRATO
                            , RES_CENTRO_COSTO
                            , RES_AUXILIAR
                            , RES_FUENTE_RECURSO
                            , RES_REFERENCIA
                            , RES_TERCERO
                            , RES_SUCURSAL
                            , RES_DESCRIPCION
                            , RES_DESTINO
                            , RES_TEXTO
                            , RES_NRO_DOCUMENTO
                            , RES_TIPOCONTRATO
                            , RES_NUMEROCONTRATO
                            , RES_CODSOLICITANTE
                            , RES_SUCSOLICITANTE
                            , RES_COD_PROYECTO_PPTAL
                            , RES_ANOPROYECTO
                            , RES_LUGAR
                            , RES_FUENTE_FINANCIACION
                            , RES_ASIGNACION
                            , RES_DEPENDENCIA
                            , RES_DET_CENTRO_COSTO
                            , RES_DET_AUXILIAR
                            , RES_DET_FUENTE_RECURSO
                            , RES_DET_REFERENCIA
                            , RES_DET_TERCERO
                            , RES_DET_SUCURSAL
                            , RES_DET_DESCRIPCION
                            , RES_DET_NRO_DOCUMENTO
                            , RES_DET_TIPOT
                            , RES_DET_CLASET
                            , RES_DET_CMPTE_SOLICI_AFECTADO
                            , RES_DET_DEPENDENCIA
                            , RES_DET_NUMERO_SOLICITUD
                            , RES_DET_TIPO_SOLICITUD
                            , RES_DET_TIPOCONTRATO
                            , RES_DET_NUMEROCONTRATO
                            , VALOR_DEBITO
                            , TIPO_ADI
                            ,SECTOR
                            ,PROGRAMA
                            ,SUBPROGRAMA
                            ,COD_PROD_CUIPO
                            ,CODIGO_BPIN
                            ,CODIGO_CCPET
                            ,CODIGO_CPC
                            ,CODIGOUNIDADEJE
                            ,FUENTE_CUIPO
                            ,CODIGOCCPETREGA
                            ,POLITICA_PUBLICA
                            ,DETALLE_SECTORIAL
                            ';
            BEGIN
                BEGIN
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'TEMP_CIERRE_PPTAL',
                                                           UN_ACCION  => 'IS',
                                                           UN_CAMPOS  => MI_CAMPOS,
                                                           UN_VALORES => MI_CONSULTA);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                MI_REEMPLAZOS(0).CLAVE := 'COMPANIA';
                MI_REEMPLAZOS(0).VALOR := UN_COMPANIA;
                MI_REEMPLAZOS(1).CLAVE := 'ANIO';
                MI_REEMPLAZOS(1).VALOR := UN_ANOACIERRE;
                MI_REEMPLAZOS(2).CLAVE := 'CLASE';
                MI_REEMPLAZOS(2).VALOR := MI_CLASE;
                MI_REEMPLAZOS(3).CLAVE := 'DIGITO';
                MI_REEMPLAZOS(3).VALOR := '';
                MI_REEMPLAZOS(4).CLAVE := 'TIPOCIERRE';
                MI_REEMPLAZOS(4).VALOR := FC_NOMBRETIPOCIERRE(MI_TIPOCIERRE);
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOERRORINSERTTEMPORAL,
                  UN_TABLAERROR => 'TEMP_CIERRE_PPTAL',
                  UN_REEMPLAZOS => MI_REEMPLAZOS
                );
            END;
        
        --SE VALIDA SI EXISTEN COMPROBANTES DUPLICADOS PARA DEJAR EL MISMO TIPO DE COMPROBANTE DEL AÑO PASADO
        PR_ACTUALIZARTIPOFINAL(UN_COMPANIA    => UN_COMPANIA,
                               UN_PREFIJO     => 'DIS',
                               UN_CLASECIERRE => MI_CLASE);
        PR_ACTUALIZARTIPOFINAL(UN_COMPANIA    => UN_COMPANIA,
                               UN_PREFIJO     => 'RES',
                               UN_CLASECIERRE => MI_CLASE);
        --PASA LOS REGISTROS DE COMPROMISO ABIERTOS DE COFINANCIADOS
        
        --PASA LAS OBLIGACIONES ABIERTAS
        MI_CLASE:='REO';
        MI_CONSULTA:= 'SELECT ''' || MI_CLASE || ''' CLASECIERRE
                              ,ROWNUM + ' || MI_CONSECUTIVO || ' REGISTRO
                              ,DP.COMPANIA
                              ,''' || MI_TIPOCIERRE    || ''' TIPOCIERRE
                              ,''' || MI_TIPOCIERRE  || ''' CLASERESERVA
                              ,-1 CREAJERARQUIA
                              ,DP.ANO  ANO
                              ,RES_DET.TIPO_CPTE_AFECT     DIS_TIPO_INICIAL
                              ,RES_DET.TIPO_CPTE_AFECT     DIS_TIPO_FINAL
                              ,RES_DET.CMPTE_AFECTADO      DIS_NUMERO
                              ,RES_DET.CONSECUTIVOPPTO     DIS_CONSECUTIVO
                              ,DP.TIPO_CPTE_AFECT          RES_TIPO_INICIAL
                              ,DP.TIPO_CPTE_AFECT          RES_TIPO_FINAL
                              ,DP.CMPTE_AFECTADO           RES_NUMERO
                              ,DP.CONSECUTIVOPPTO          RES_CONSECUTIVO
                              ,DP.TIPO_CPTE                REO_TIPO_INICIAL
                              ,DP.TIPO_CPTE                REO_TIPO_FINAL
                              ,DP.COMPROBANTE              REO_NUMERO
                              ,DP.CONSECUTIVO              REO_CONSECUTIVO
                              ,DP.CUENTA CUENTA
                              ,DP.CUENTA CUENTAANT
                              ,NULL DIGITOS
                              ,PP.TIPOVIGENCIA
                              ,PP.VIGENCIA
                              ,DP.NATURALEZA
                              ,DIS.CENTRO_COSTO        DIS_CENTRO_COSTO
                              ,DIS.AUXILIAR            DIS_AUXILIAR
                              ,DIS.FUENTE_RECURSO      DIS_FUENTE_RECURSO
                              ,DIS.REFERENCIA          DIS_REFERENCIA
                              ,DIS.TERCERO             DIS_TERCERO
                              ,DIS.SUCURSAL            DIS_SUCURSAL
                              ,DIS.DESCRIPCION         DIS_DESCRIPCION
                              ,DIS.DESTINO             DIS_DESTINO
                              ,NVL(DIS.TEXTO,''.'')    DIS_TEXTO
                              ,DIS.NRO_DOCUMENTO       DIS_NRO_DOCUMENTO
                              ,DIS.TIPOCONTRATO        DIS_TIPOCONTRATO
                              ,DIS.NUMEROCONTRATO      DIS_NUMEROCONTRATO
                              ,DIS.CODSOLICITANTE      DIS_CODSOLICITANTE
                              ,DIS.SUCSOLICITANTE      DIS_SUCSOLICITANTE
                              ,DIS.COD_PROYECTO_PPTAL  DIS_COD_PROYECTO_PPTAL
                              ,DIS.ANOPROYECTO         DIS_ANOPROYECTO
                              ,DIS.LUGAR               DIS_LUGAR
                              ,DIS.FUENTE_FINANCIACION DIS_FUENTE_FINANCIACION
                              ,DIS.ASIGNACION          DIS_ASIGNACION
                              ,DIS.DEPENDENCIA         DIS_DEPENDENCIA
                              ,DIS_DET.CENTRO_COSTO    DIS_DET_CENTRO_COSTO
                              ,DIS_DET.AUXILIAR        DIS_DET_AUXILIAR
                              ,DIS_DET.FUENTE_RECURSO  DIS_DET_FUENTE_RECURSO
                              ,DIS_DET.REFERENCIA      DIS_DET_REFERENCIA
                              ,DIS_DET.TERCERO         DIS_DET_TERCERO
                              ,DIS_DET.SUCURSAL        DIS_DET_SUCURSAL
                              ,DIS_DET.DESCRIPCION     DIS_DET_DESCRIPCION
                              ,DIS_DET.NRO_DOCUMENTO   DIS_DET_NRO_DOCUMENTO
                              ,DIS_DET.TIPOT           DIS_DET_TIPOT
                              ,DIS_DET.CLASET          DIS_DET_CLASET
                              ,DIS_DET.CMPTE_SOLICI_AFECTADO DIS_DET_CMPTE_SOLICI_AFECTADO
                              ,DIS_DET.DEPENDENCIA     DIS_DET_DEPENDENCIA
                              ,DIS_DET.NUMERO_SOLICITUD DIS_DET_NUMERO_SOLICITUD
                              ,DIS_DET.TIPO_SOLICITUD  DIS_DET_TIPO_SOLICITUD
                              ,DIS_DET.TIPOCONTRATO    DIS_DET_TIPOCONTRATO
                              ,DIS_DET.NUMEROCONTRATO  DIS_DET_NUMEROCONTRATO
                              ,RES.CENTRO_COSTO        RES_CENTRO_COSTO
                              ,RES.AUXILIAR            RES_AUXILIAR
                              ,RES.FUENTE_RECURSO      RES_FUENTE_RECURSO
                              ,RES.REFERENCIA          RES_REFERENCIA
                              ,RES.TERCERO             RES_TERCERO
                              ,RES.SUCURSAL            RES_SUCURSAL
                              ,RES.DESCRIPCION         RES_DESCRIPCION
                              ,RES.DESTINO             RES_DESTINO
                              ,NVL(RES.TEXTO,''.'')    RES_TEXTO
                              ,RES.NRO_DOCUMENTO       RES_NRO_DOCUMENTO
                              ,RES.TIPOCONTRATO        RES_TIPOCONTRATO
                              ,RES.NUMEROCONTRATO      RES_NUMEROCONTRATO
                              ,RES.CODSOLICITANTE      RES_CODSOLICITANTE
                              ,RES.SUCSOLICITANTE      RES_SUCSOLICITANTE
                              ,RES.COD_PROYECTO_PPTAL  RES_COD_PROYECTO_PPTAL
                              ,RES.ANOPROYECTO         RES_ANOPROYECTO
                              ,RES.LUGAR               RES_LUGAR
                              ,RES.FUENTE_FINANCIACION RES_FUENTE_FINANCIACION
                              ,RES.ASIGNACION          RES_ASIGNACION
                              ,RES.DEPENDENCIA         RES_DEPENDENCIA
                              ,RES_DET.CENTRO_COSTO    RES_DET_CENTRO_COSTO
                              ,RES_DET.AUXILIAR        RES_DET_AUXILIAR
                              ,RES_DET.FUENTE_RECURSO  RES_DET_FUENTE_RECURSO
                              ,RES_DET.REFERENCIA      RES_DET_REFERENCIA
                              ,RES_DET.TERCERO         RES_DET_TERCERO
                              ,RES_DET.SUCURSAL        RES_DET_SUCURSAL
                              ,RES_DET.DESCRIPCION     RES_DET_DESCRIPCION
                              ,RES_DET.NRO_DOCUMENTO   RES_DET_NRO_DOCUMENTO
                              ,RES_DET.TIPOT           RES_DET_TIPOT
                              ,RES_DET.CLASET          RES_DET_CLASET
                              ,RES_DET.CMPTE_SOLICI_AFECTADO RES_DET_CMPTE_SOLICI_AFECTADO
                              ,RES_DET.DEPENDENCIA     RES_DET_DEPENDENCIA
                              ,RES_DET.NUMERO_SOLICITUD RES_DET_NUMERO_SOLICITUD
                              ,RES_DET.TIPO_SOLICITUD  RES_DET_TIPO_SOLICITUD
                              ,RES_DET.TIPOCONTRATO    RES_DET_TIPOCONTRATO
                              ,RES_DET.NUMEROCONTRATO  RES_DET_NUMEROCONTRATO
                              ,REO.CENTRO_COSTO        REO_CENTRO_COSTO
                              ,REO.AUXILIAR            REO_AUXILIAR
                              ,REO.FUENTE_RECURSO      REO_FUENTE_RECURSO
                              ,REO.REFERENCIA          REO_REFERENCIA
                              ,REO.TERCERO             REO_TERCERO
                              ,REO.SUCURSAL            REO_SUCURSAL
                              ,REO.DESCRIPCION         REO_DESCRIPCION
                              ,REO.DESTINO             REO_DESTINO
                              ,NVL(REO.TEXTO,''.'')    REO_TEXTO
                              ,REO.NRO_DOCUMENTO       REO_NRO_DOCUMENTO
                              ,REO.TIPOCONTRATO        REO_TIPOCONTRATO
                              ,REO.NUMEROCONTRATO      REO_NUMEROCONTRATO
                              ,REO.CODSOLICITANTE      REO_CODSOLICITANTE
                              ,REO.SUCSOLICITANTE      REO_SUCSOLICITANTE
                              ,REO.COD_PROYECTO_PPTAL  REO_COD_PROYECTO_PPTAL
                              ,REO.ANOPROYECTO         REO_ANOPROYECTO
                              ,REO.LUGAR               REO_LUGAR
                              ,REO.FUENTE_FINANCIACION REO_FUENTE_FINANCIACION
                              ,REO.ASIGNACION          REO_ASIGNACION
                              ,REO.DEPENDENCIA         REO_DEPENDENCIA
                              ,DP.CENTRO_COSTO         REO_DET_CENTRO_COSTO
                              ,DP.AUXILIAR             REO_DET_AUXILIAR
                              ,DP.FUENTE_RECURSO       REO_DET_FUENTE_RECURSO
                              ,DP.REFERENCIA           REO_DET_REFERENCIA
                              ,DP.TERCERO              REO_DET_TERCERO
                              ,DP.SUCURSAL             REO_DET_SUCURSAL
                              ,DP.DESCRIPCION          REO_DET_DESCRIPCION
                              ,DP.NRO_DOCUMENTO        REO_DET_NRO_DOCUMENTO
                              ,DP.TIPOT                REO_DET_TIPOT
                              ,DP.CLASET               REO_DET_CLASET
                              ,DP.CMPTE_SOLICI_AFECTADO REO_DET_CMPTE_SOLICI_AFECTADO
                              ,DP.DEPENDENCIA          REO_DET_DEPENDENCIA
                              ,DP.NUMERO_SOLICITUD     REO_DET_NUMERO_SOLICITUD
                              ,DP.TIPO_SOLICITUD       REO_DET_TIPO_SOLICITUD
                              ,DP.TIPOCONTRATO         REO_DET_TIPOCONTRATO
                              ,DP.NUMEROCONTRATO       REO_DET_NUMEROCONTRATO
                              ,((DP.VALOR_DEBITO        - DP.VALOR_CREDITO       )
                                  -(DP.DEBITO_AFECTADO     - DP.CREDITO_AFECTADO    )
                                  +(DP.MODIFICACION_DEBITO - DP.MODIFICACION_CREDITO)) AS VALOR_DEBITO
                              ,NULL 
                              ,DP.SECTOR
                              ,DP.PROGRAMA
                              ,DP.SUBPROGRAMA
                              ,DP.COD_PROD_CUIPO
                              ,DP.CODIGO_BPIN
                              ,DP.CODIGO_CCPET
                              ,DP.CODIGO_CPC
                              ,DP.CODIGOUNIDADEJE
                              ,DP.FUENTE_CUIPO
                              ,DP.CODIGOCCPETREGA
                              ,DP.POLITICA_PUBLICA
                              ,DP.DETALLE_SECTORIAL
                         FROM DETALLE_COMPROBANTE_PPTAL DP INNER JOIN PLAN_PRESUPUESTAL PP
                           ON DP.COMPANIA = PP.COMPANIA
                          AND DP.ANO      = PP.ANO
                          AND DP.CUENTA   = PP.CODIGO
                         INNER JOIN TIPO_COMPROBPP TC
                           ON DP.COMPANIA  = TC.COMPANIA
                          AND DP.TIPO_CPTE = TC.CODIGO
                         INNER JOIN COMPROBANTE_PPTAL REO
                           ON DP.COMPANIA        = REO.COMPANIA
                          AND DP.ANO             = REO.ANO
                          AND DP.TIPO_CPTE       = REO.TIPO
                          AND DP.COMPROBANTE     = REO.NUMERO
                         INNER JOIN DETALLE_COMPROBANTE_PPTAL RES_DET
                           ON DP.COMPANIA        = RES_DET.COMPANIA
                          AND DP.ANO_AFECT       = RES_DET.ANO
                          AND DP.TIPO_CPTE_AFECT = RES_DET.TIPO_CPTE
                          AND DP.CMPTE_AFECTADO  = RES_DET.COMPROBANTE
                          AND DP.CONSECUTIVOPPTO = RES_DET.CONSECUTIVO
                         INNER JOIN COMPROBANTE_PPTAL RES
                           ON RES_DET.COMPANIA     = RES.COMPANIA
                          AND RES_DET.ANO          = RES.ANO
                          AND RES_DET.TIPO_CPTE    = RES.TIPO
                          AND RES_DET.COMPROBANTE  = RES.NUMERO
                         INNER JOIN DETALLE_COMPROBANTE_PPTAL DIS_DET
                           ON RES_DET.COMPANIA        = DIS_DET.COMPANIA
                          AND RES_DET.ANO_AFECT       = DIS_DET.ANO
                          AND RES_DET.TIPO_CPTE_AFECT = DIS_DET.TIPO_CPTE
                          AND RES_DET.CMPTE_AFECTADO  = DIS_DET.COMPROBANTE
                          AND RES_DET.CONSECUTIVOPPTO = DIS_DET.CONSECUTIVO
                         INNER JOIN COMPROBANTE_PPTAL DIS
                           ON DIS_DET.COMPANIA    = DIS.COMPANIA
                          AND DIS_DET.ANO         = DIS.ANO
                          AND DIS_DET.TIPO_CPTE   = DIS.TIPO
                          AND DIS_DET.COMPROBANTE = DIS.NUMERO
                        WHERE DP.COMPANIA   = ''' || UN_COMPANIA   || '''
                          AND DP.ANO        = '   || UN_ANOACIERRE || '
                          AND PP.NATURALEZA = ''D''
                          AND PP.COFINANCIADO NOT IN(0)
                          AND TC.CLASE IN (''' || MI_CLASE || ''')
                          AND ((DP.VALOR_DEBITO        - DP.VALOR_CREDITO       )
                              - (DP.DEBITO_AFECTADO     - DP.CREDITO_AFECTADO    )
                              + (DP.MODIFICACION_DEBITO - DP.MODIFICACION_CREDITO))>0';
                              
        MI_CAMPOS := '    CLASECIERRE
                        , REGISTRO
                        , COMPANIA
                        , TIPOCIERRE
                        , CLASERESERVA
                        , CREAJERARQUIA
                        , ANO
                        , DIS_TIPO_INICIAL
                        , DIS_TIPO_FINAL
                        , DIS_NUMERO
                        , DIS_CONSECUTIVO
                        , RES_TIPO_INICIAL
                        , RES_TIPO_FINAL
                        , RES_NUMERO
                        , RES_CONSECUTIVO
                        , REO_TIPO_INICIAL
                        , REO_TIPO_FINAL
                        , REO_NUMERO
                        , REO_CONSECUTIVO
                        , CUENTA
                        , CUENTAANT
                        , DIGITOCAMBIO
                        , TIPOVIGENCIA
                        , VIGENCIA
                        , NATURALEZA
                        , DIS_CENTRO_COSTO
                        , DIS_AUXILIAR
                        , DIS_FUENTE_RECURSO
                        , DIS_REFERENCIA
                        , DIS_TERCERO
                        , DIS_SUCURSAL
                        , DIS_DESCRIPCION
                        , DIS_DESTINO
                        , DIS_TEXTO
                        , DIS_NRO_DOCUMENTO
                        , DIS_TIPOCONTRATO
                        , DIS_NUMEROCONTRATO
                        , DIS_CODSOLICITANTE
                        , DIS_SUCSOLICITANTE
                        , DIS_COD_PROYECTO_PPTAL
                        , DIS_ANOPROYECTO
                        , DIS_LUGAR
                        , DIS_FUENTE_FINANCIACION
                        , DIS_ASIGNACION
                        , DIS_DEPENDENCIA
                        , DIS_DET_CENTRO_COSTO
                        , DIS_DET_AUXILIAR
                        , DIS_DET_FUENTE_RECURSO
                        , DIS_DET_REFERENCIA
                        , DIS_DET_TERCERO
                        , DIS_DET_SUCURSAL
                        , DIS_DET_DESCRIPCION
                        , DIS_DET_NRO_DOCUMENTO
                        , DIS_DET_TIPOT
                        , DIS_DET_CLASET
                        , DIS_DET_CMPTE_SOLICI_AFECTADO
                        , DIS_DET_DEPENDENCIA
                        , DIS_DET_NUMERO_SOLICITUD
                        , DIS_DET_TIPO_SOLICITUD
                        , DIS_DET_TIPOCONTRATO
                        , DIS_DET_NUMEROCONTRATO
                        , RES_CENTRO_COSTO
                        , RES_AUXILIAR
                        , RES_FUENTE_RECURSO
                        , RES_REFERENCIA
                        , RES_TERCERO
                        , RES_SUCURSAL
                        , RES_DESCRIPCION
                        , RES_DESTINO
                        , RES_TEXTO
                        , RES_NRO_DOCUMENTO
                        , RES_TIPOCONTRATO
                        , RES_NUMEROCONTRATO
                        , RES_CODSOLICITANTE
                        , RES_SUCSOLICITANTE
                        , RES_COD_PROYECTO_PPTAL
                        , RES_ANOPROYECTO
                        , RES_LUGAR
                        , RES_FUENTE_FINANCIACION
                        , RES_ASIGNACION
                        , RES_DEPENDENCIA
                        , RES_DET_CENTRO_COSTO
                        , RES_DET_AUXILIAR
                        , RES_DET_FUENTE_RECURSO
                        , RES_DET_REFERENCIA
                        , RES_DET_TERCERO
                        , RES_DET_SUCURSAL
                        , RES_DET_DESCRIPCION
                        , RES_DET_NRO_DOCUMENTO
                        , RES_DET_TIPOT
                        , RES_DET_CLASET
                        , RES_DET_CMPTE_SOLICI_AFECTADO
                        , RES_DET_DEPENDENCIA
                        , RES_DET_NUMERO_SOLICITUD
                        , RES_DET_TIPO_SOLICITUD
                        , RES_DET_TIPOCONTRATO
                        , RES_DET_NUMEROCONTRATO
                        , REO_CENTRO_COSTO
                        , REO_AUXILIAR
                        , REO_FUENTE_RECURSO
                        , REO_REFERENCIA
                        , REO_TERCERO
                        , REO_SUCURSAL
                        , REO_DESCRIPCION
                        , REO_DESTINO
                        , REO_TEXTO
                        , REO_NRO_DOCUMENTO
                        , REO_TIPOCONTRATO
                        , REO_NUMEROCONTRATO
                        , REO_CODSOLICITANTE
                        , REO_SUCSOLICITANTE
                        , REO_COD_PROYECTO_PPTAL
                        , REO_ANOPROYECTO
                        , REO_LUGAR
                        , REO_FUENTE_FINANCIACION
                        , REO_ASIGNACION
                        , REO_DEPENDENCIA
                        , REO_DET_CENTRO_COSTO
                        , REO_DET_AUXILIAR
                        , REO_DET_FUENTE_RECURSO
                        , REO_DET_REFERENCIA
                        , REO_DET_TERCERO
                        , REO_DET_SUCURSAL
                        , REO_DET_DESCRIPCION
                        , REO_DET_NRO_DOCUMENTO
                        , REO_DET_TIPOT
                        , REO_DET_CLASET
                        , REO_DET_CMPTE_SOLICI_AFECTADO
                        , REO_DET_DEPENDENCIA
                        , REO_DET_NUMERO_SOLICITUD
                        , REO_DET_TIPO_SOLICITUD
                        , REO_DET_TIPOCONTRATO
                        , REO_DET_NUMEROCONTRATO
                        , VALOR_DEBITO
                        , TIPO_ADI
                        ,SECTOR
                        ,PROGRAMA
                        ,SUBPROGRAMA
                        ,COD_PROD_CUIPO
                        ,CODIGO_BPIN
                        ,CODIGO_CCPET
                        ,CODIGO_CPC
                        ,CODIGOUNIDADEJE
                        ,FUENTE_CUIPO
                        ,CODIGOCCPETREGA
                        ,POLITICA_PUBLICA
                        ,DETALLE_SECTORIAL
                        ';
        BEGIN
            BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'TEMP_CIERRE_PPTAL',
                                                       UN_ACCION  => 'IS',
                                                       UN_CAMPOS  => MI_CAMPOS,
                                                       UN_VALORES => MI_CONSULTA);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
            MI_REEMPLAZOS(0).CLAVE := 'COMPANIA';
            MI_REEMPLAZOS(0).VALOR := UN_COMPANIA;
            MI_REEMPLAZOS(1).CLAVE := 'ANIO';
            MI_REEMPLAZOS(1).VALOR := UN_ANOACIERRE;
            MI_REEMPLAZOS(2).CLAVE := 'CLASE';
            MI_REEMPLAZOS(2).VALOR := MI_CLASE;
            MI_REEMPLAZOS(3).CLAVE := 'DIGITO';
            MI_REEMPLAZOS(3).VALOR := '';
            MI_REEMPLAZOS(4).CLAVE := 'TIPOCIERRE';
            MI_REEMPLAZOS(4).VALOR := FC_NOMBRETIPOCIERRE(MI_TIPOCIERRE);
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_PPTOERRORINSERTTEMPORAL,
              UN_TABLAERROR => 'TEMP_CIERRE_PPTAL',
              UN_REEMPLAZOS => MI_REEMPLAZOS
            );
        END;
  
    --SE VALIDA SI EXISTEN COMPROBANTES DUPLICADOS PARA DEJAR EL MISMO TIPO DE COMPROBANTE DEL AÑO PASADO
            PR_ACTUALIZARTIPOFINAL(UN_COMPANIA    => UN_COMPANIA,
                                   UN_PREFIJO     => 'DIS',
                                   UN_CLASECIERRE => MI_CLASE);
            PR_ACTUALIZARTIPOFINAL(UN_COMPANIA    => UN_COMPANIA,
                                   UN_PREFIJO     => 'RES',
                                   UN_CLASECIERRE => MI_CLASE);
            PR_ACTUALIZARTIPOFINAL(UN_COMPANIA    => UN_COMPANIA,
                                   UN_PREFIJO     => 'REO',
                                   UN_CLASECIERRE => MI_CLASE);
        --PASA LAS OBLIGACIONES ABIERTAS
        
        ELSE
            
            MI_FECHACOMP    := 'TO_DATE(''' || UN_FECHACIERRE || ''',''DD/MM/YYYY'')';
            MI_ANOACTUAL    := UN_ANOACIERRE + 1;
            BEGIN 
            
            <<CREAR_HEADER>>
            FOR RS IN (SELECT DP.COMPANIA
                              ,DP.TIPO_CPTE
                              ,DP.COMPROBANTE
                              ,DIS.DEPENDENCIA
                              ,DIS.DESCRIPCION
                              ,DIS.TERCERO
                              ,DIS.SUCURSAL
                              ,DIS.NRO_DOCUMENTO
                              ,DIS.CENTRO_COSTO 
                              ,DIS.AUXILIAR
                              ,DIS.REFERENCIA   
                              ,DIS.FUENTE_RECURSO
                         FROM DETALLE_COMPROBANTE_PPTAL DP INNER JOIN PLAN_PRESUPUESTAL PP
                           ON DP.COMPANIA = PP.COMPANIA
                          AND DP.ANO      = PP.ANO
                          AND DP.CUENTA   = PP.CODIGO
                         INNER JOIN TIPO_COMPROBPP TC
                           ON DP.COMPANIA  = TC.COMPANIA
                          AND DP.TIPO_CPTE = TC.CODIGO
                         INNER JOIN COMPROBANTE_PPTAL DIS
                           ON DP.COMPANIA        = DIS.COMPANIA
                          AND DP.ANO             = DIS.ANO
                          AND DP.TIPO_CPTE       = DIS.TIPO
                          AND DP.COMPROBANTE     = DIS.NUMERO
                         LEFT JOIN TIPOVIGENCIA TVIG
                            ON PP.TIPOVIGENCIA = TVIG.CODIGO
                        WHERE DP.COMPANIA   = UN_COMPANIA
                          AND DP.ANO        = UN_ANOACIERRE
                          AND TC.CLASE IN ('DIS')
                          AND PP.COFINANCIADO IN (-1)
                          AND ((DP.VALOR_DEBITO        - DP.VALOR_CREDITO       )
                             - (DP.DEBITO_AFECTADO     - DP.CREDITO_AFECTADO    )
                             + (DP.MODIFICACION_DEBITO - DP.MODIFICACION_CREDITO))>0
                        GROUP BY DP.COMPANIA
                              ,DP.TIPO_CPTE
                              ,DP.COMPROBANTE
                              ,DIS.DEPENDENCIA
                              ,DIS.DESCRIPCION
                              ,DIS.TERCERO
                              ,DIS.SUCURSAL
                              ,DIS.NRO_DOCUMENTO
                              ,DIS.CENTRO_COSTO 
                              ,DIS.AUXILIAR
                              ,DIS.REFERENCIA   
                              ,DIS.FUENTE_RECURSO
            )
            
            LOOP 
            
                BEGIN
                    SELECT NUMERO
                    INTO MI_EXISTE
                    FROM COMPROBANTE_PPTAL
                    WHERE COMPANIA = UN_COMPANIA
                    AND ANO = MI_ANOACTUAL
                    AND TIPO = RS.TIPO_CPTE
                    AND NUMERO = RS.COMPROBANTE;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_EXISTE := 0;
                END;
            
               IF MI_EXISTE = 0 THEN
                    
                    MI_CAMPOS := 'COMPANIA      ,ANO
                                  ,TIPO         ,NUMERO
                                  ,FECHA        ,DEPENDENCIA
                                  ,DESCRIPCION  ,TERCERO
                                  ,SUCURSAL     ,NRO_DOCUMENTO
                                  ,CENTRO_COSTO ,AUXILIAR
                                  ,REFERENCIA   ,FUENTE_RECURSO
                                  ,CREATED_BY   ,DATE_CREATED';
                    
                    MI_VALORES := ''''|| UN_COMPANIA ||'''
                                  ,'  || MI_ANOACTUAL ||'
                                  ,'''|| RS.TIPO_CPTE ||'''
                                  ,'  || RS.COMPROBANTE ||'
                                  ,'  || MI_FECHACOMP ||'
                                  ,'''|| RS.DEPENDENCIA ||'''
                                  ,'''|| RS.DESCRIPCION ||'''
                                  ,'''|| RS.TERCERO ||'''
                                  ,'''|| RS.SUCURSAL ||'''
                                  ,'''|| RS.NRO_DOCUMENTO ||'''
                                  ,'''|| RS.CENTRO_COSTO ||'''
                                  ,'''|| RS.AUXILIAR ||''' 
                                  ,'''|| RS.REFERENCIA ||'''
                                  ,'''|| RS.FUENTE_RECURSO ||''' 
                                  ,'''|| UN_USUARIO ||''' 
                                  ,SYSDATE';
                    
                    BEGIN
                       BEGIN
        
                          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'COMPROBANTE_PPTAL'
                                                            ,UN_ACCION  => 'I'
                                                            ,UN_CAMPOS  => MI_CAMPOS
                                                            ,UN_VALORES => MI_VALORES);
        
                          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                        END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                      MI_MSGERROR(1).CLAVE := 'TIPCPTE';
                      MI_MSGERROR(1).VALOR := RS.TIPO_CPTE;
                      MI_MSGERROR(2).CLAVE := 'NROCPTE';
                      MI_MSGERROR(2).VALOR := RS.COMPROBANTE;
                      MI_MSGERROR(3).CLAVE := 'DESCRIPCION';
                      MI_MSGERROR(3).VALOR := RS.DESCRIPCION;
                      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_CREARCPTERED, UN_TABLAERROR => 'COMPROBANTE_PPTAL', UN_REEMPLAZOS => MI_MSGERROR );
                  END;
              
              END IF;
          
          END LOOP CREAR_HEADER;
          
          BEGIN
          <<CREAR_DETALLES>>
          FOR RS IN (SELECT DP.COMPANIA          ,DP.TIPO_CPTE         ,DP.COMPROBANTE
                                  ,DP.CONSECUTIVO       ,DP.NATURALEZA   
                                  ,DP.CUENTA
                                  ,DP.TERCERO           ,DP.SUCURSAL
                                  ,DP.CENTRO_COSTO      ,DP.AUXILIAR 
                                  ,DP.FUENTE_RECURSO    ,DP.REFERENCIA
                                  ,DP.DESCRIPCION       
                                  ,DP.DEPENDENCIA       ,DIS.NRO_DOCUMENTO
                                  ,SYSDATE              
                                  ,DP.TIPOT
                                  ,DP.CLASET            ,DP.CMPTE_SOLICI_AFECTADO
                                  ,DP.NUMERO_SOLICITUD  ,DP.TIPO_SOLICITUD
                                  ,((DP.VALOR_DEBITO        - DP.VALOR_CREDITO       )
                                      -(DP.DEBITO_AFECTADO     - DP.CREDITO_AFECTADO    )
                                      +(DP.MODIFICACION_DEBITO - DP.MODIFICACION_CREDITO)) AS SALDO
                                  ,DP.SECTOR
                                  ,DP.PROGRAMA
                                  ,DP.SUBPROGRAMA
                                  ,DP.COD_PROD_CUIPO
                                  ,DP.CODIGO_BPIN
                                  ,DP.CODIGO_CCPET
                                  ,DP.CODIGO_CPC
                                  ,DP.CODIGOUNIDADEJE
                                  ,DP.FUENTE_CUIPO
                                  ,DP.CODIGOCCPETREGA
                                  ,DP.POLITICA_PUBLICA
                                  ,DP.DETALLE_SECTORIAL
                             FROM DETALLE_COMPROBANTE_PPTAL DP INNER JOIN PLAN_PRESUPUESTAL PP
                               ON DP.COMPANIA = PP.COMPANIA
                              AND DP.ANO      = PP.ANO
                              AND DP.CUENTA   = PP.CODIGO
                             INNER JOIN TIPO_COMPROBPP TC
                               ON DP.COMPANIA  = TC.COMPANIA
                              AND DP.TIPO_CPTE = TC.CODIGO
                             INNER JOIN COMPROBANTE_PPTAL DIS
                               ON DP.COMPANIA        = DIS.COMPANIA
                              AND DP.ANO             = DIS.ANO
                              AND DP.TIPO_CPTE       = DIS.TIPO
                              AND DP.COMPROBANTE     = DIS.NUMERO
                             LEFT JOIN TIPOVIGENCIA TVIG
                                ON PP.TIPOVIGENCIA = TVIG.CODIGO
                            WHERE DP.COMPANIA   = UN_COMPANIA
                              AND DP.ANO        = UN_ANOACIERRE
                              AND TC.CLASE IN ('DIS')
                              AND PP.COFINANCIADO IN (-1)
                              AND ((DP.VALOR_DEBITO        - DP.VALOR_CREDITO       )
                                 - (DP.DEBITO_AFECTADO     - DP.CREDITO_AFECTADO    )
                                 + (DP.MODIFICACION_DEBITO - DP.MODIFICACION_CREDITO))>0)
                                 
            LOOP
            
                BEGIN
                    SELECT COUNT(COMPROBANTE)
                    INTO MI_EXISTE
                    FROM DETALLE_COMPROBANTE_PPTAL
                    WHERE COMPANIA = UN_COMPANIA
                    AND ANO = MI_ANOACTUAL
                    AND TIPO_CPTE = RS.TIPO_CPTE
                    AND COMPROBANTE = RS.COMPROBANTE
                    AND CONSECUTIVO = RS.CONSECUTIVO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_EXISTE := 0;
                END;
            
            IF MI_EXISTE = 0 THEN
            
                MI_CAMPOS := 'COMPANIA,         ANO,
                              TIPO_CPTE,        COMPROBANTE,
                              CONSECUTIVO,      NATURALEZA,
                              CUENTA,
                              TERCERO,          SUCURSAL,
                              CENTRO_COSTO,     AUXILIAR,
                              REFERENCIA,       FUENTE_RECURSO,
                              DESCRIPCION,      FECHA,
                              DEPENDENCIA,       NRO_DOCUMENTO,
                              TIPOT,
                              CLASET,       CMPTE_SOLICI_AFECTADO,
                              NUMERO_SOLICITUD,
                              TIPO_SOLICITUD,
                              VALOR_DEBITO
                             ,SECTOR
                             ,PROGRAMA
                             ,SUBPROGRAMA
                             ,COD_PROD_CUIPO
                             ,CODIGO_BPIN
                             ,CODIGO_CCPET
                             ,CODIGO_CPC
                             ,CODIGOUNIDADEJE
                             ,FUENTE_CUIPO
                             ,CODIGOCCPETREGA
                             ,POLITICA_PUBLICA
                             ,DETALLE_SECTORIAL
                             ,CREATED_BY
                             ,DATE_CREATED';
                             
                MI_VALORES := ''''|| UN_COMPANIA ||'''
                                  ,'  || MI_ANOACTUAL ||'
                                  ,'''|| RS.TIPO_CPTE ||'''
                                  ,'  || RS.COMPROBANTE ||'
                                  ,'  || RS.CONSECUTIVO ||'
                                  ,'''|| RS.NATURALEZA ||'''
                                  ,'''|| RS.CUENTA ||'''
                                  ,'''|| RS.TERCERO ||'''
                                  ,'''|| RS.SUCURSAL ||'''
                                  ,'''|| RS.CENTRO_COSTO ||'''
                                  ,'''|| RS.AUXILIAR ||''' 
                                  ,'''|| RS.REFERENCIA ||'''
                                  ,'''|| RS.FUENTE_RECURSO ||'''
                                  ,'''|| RS.DESCRIPCION ||'''
                                  ,'  || MI_FECHACOMP ||'
                                  ,'''|| RS.DEPENDENCIA ||'''
                                  ,'''|| RS.NRO_DOCUMENTO ||'''
                                  ,'''|| RS.TIPOT ||'''
                                  ,'''|| RS.CLASET ||'''
                                  ,'''|| RS.CMPTE_SOLICI_AFECTADO ||'''
                                  ,'''|| RS.NUMERO_SOLICITUD ||'''
                                  ,'''|| RS.TIPO_SOLICITUD ||'''
                                  ,  '|| RS.SALDO ||'
                                  ,'''|| RS.SECTOR ||'''
                                  ,'''|| RS.PROGRAMA ||'''
                                  ,'''|| RS.SUBPROGRAMA ||'''
                                  ,'''|| RS.COD_PROD_CUIPO ||'''
                                  ,'''|| RS.CODIGO_BPIN ||'''
                                  ,'''|| RS.CODIGO_CCPET ||'''
                                  ,'''|| RS.CODIGO_CPC ||'''
                                  ,'''|| RS.CODIGOUNIDADEJE ||'''
                                  ,'''|| RS.FUENTE_CUIPO ||'''
                                  ,'''|| RS.CODIGOCCPETREGA ||'''
                                  ,'''|| RS.POLITICA_PUBLICA ||'''
                                  ,'''|| RS.DETALLE_SECTORIAL ||'''
                                  ,'''|| UN_USUARIO ||''' 
                                  ,SYSDATE';
                                     
                    BEGIN
                        BEGIN
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'DETALLE_COMPROBANTE_PPTAL'
                                                                ,UN_ACCION  => 'I'
                                                                ,UN_CAMPOS  => MI_CAMPOS
                                                                ,UN_VALORES => MI_VALORES);
            
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    MI_MSGERROR(1).CLAVE := 'TIPOCPTE';
                    MI_MSGERROR(1).VALOR := RS.TIPO_CPTE;
                    MI_MSGERROR(2).CLAVE := 'NROCPTE';
                    MI_MSGERROR(2).VALOR := RS.COMPROBANTE;
                    MI_MSGERROR(3).CLAVE := 'CONSECUTIVO';
                    MI_MSGERROR(3).VALOR := RS.CONSECUTIVO;
                    MI_MSGERROR(4).CLAVE := 'CUENTA';
                    MI_MSGERROR(4).VALOR := RS.CUENTA;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_CREARDETREDUCCION, UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL', UN_REEMPLAZOS => MI_MSGERROR );
                    END;
                
             ELSE
             
                BEGIN
                    BEGIN
                    MI_CAMPOS:= 'VALOR_DEBITO = VALOR_DEBITO + ' || RS.SALDO ||'';
                    
                    MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA    || '''
                                 AND ANO         = ''' || MI_ANOACTUAL   || '''
                                 AND TIPO_CPTE   = ''' || RS.TIPO_CPTE   || '''
                                 AND COMPROBANTE =   ' || RS.COMPROBANTE || ' 
                                 AND CONSECUTIVO =   ' || RS.CONSECUTIVO || '';
                                 
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                      ,UN_ACCION    => 'M'
                                                      ,UN_CAMPOS    => MI_CAMPOS
                                                      ,UN_CONDICION => MI_CONDICION); 
                                                      
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                MI_MSGERROR(1).CLAVE := 'COMPROBANTE';
                MI_MSGERROR(1).VALOR := RS.COMPROBANTE;
                MI_MSGERROR(2).CLAVE := 'TIPO';
                MI_MSGERROR(2).VALOR := RS.TIPO_CPTE ;
                MI_MSGERROR(3).CLAVE := 'CONSECUTIVO';
                MI_MSGERROR(3).VALOR := RS.CONSECUTIVO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                        ,UN_ERROR_COD => PCK_ERRORES.ERR_PPTOACTUALIZASALDOS
                                        ,UN_REEMPLAZOS  => MI_MSGERROR  );
                END;
                
            END IF;
            
            END LOOP CREAR_DETALLES;
            
        END;

          END;
                
        END IF;
                
END PR_INSERT_COFINANCIADO;

PROCEDURE PR_ELIMINARAFECTACIONESPPTAL
   /*  
        NAME              : PR_ELIMINARAFECTACIONESPPTAL 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
        DATE MIGRADOR     : 17/10/2024
        TIME              : 09:34 AM
        SOURCE MODULE     : PRESUPUESTO
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite eliminar las afectaciones de los comprobantes pptales
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:PR_ELIMINARAFECTACIONESPPTAL
        @METHOD:Post
    */
   (
     UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE   IN  PCK_SUBTIPOS.TI_ANIO,
     UN_TIPOCIERRE   IN  CONFIG_CIERRE_PPTAL.TIPOCIERRE%TYPE
   )AS 
   MI_TABLA                       PCK_SUBTIPOS.TI_TABLA;
   MI_CAMPOS                      PCK_SUBTIPOS.TI_CAMPOS;
   MI_VALORES                     PCK_SUBTIPOS.TI_VALORES;
   MI_CONDICION                   PCK_SUBTIPOS.TI_CONDICION;
   MI_CONDICIONDETALLE            PCK_SUBTIPOS.TI_CONDICION;
   MI_ANOACTUAL          NUMBER(4,0);
   MI_DIGITOCAMBIO                CONFIG_CIERRE_PPTAL.DIGITOSCAMBIO%TYPE;
BEGIN
    MI_ANOACTUAL     := UN_ANOACIERRE + 1;  
    FOR RS IN (SELECT CLASERESERVA,  TIPO_DIS, 
                      TIPO_RES,      TIPO_REO,
                      DIGITOSCAMBIO, TIPOVIGENCIAFINAL, 
                      GENERAR,       CREAJERARQUIA, 
                      TIPOVIGENCIAINICI
               FROM  CONFIG_CIERRE_PPTAL
               WHERE COMPANIA   = UN_COMPANIA
                 AND TIPOCIERRE = UN_TIPOCIERRE
                 AND GENERAR    NOT IN(0)
              )
    LOOP 
    
        IF UN_TIPOCIERRE = 'COF' THEN 
        
            BEGIN
                SELECT SUBSTR(CODIGO,1,1)
                    INTO MI_DIGITOCAMBIO
                FROM PLAN_PRESUPUESTAL
                WHERE COMPANIA = UN_COMPANIA
                AND ANO = MI_ANOACTUAL
                AND COFINANCIADO <> 0
                GROUP BY SUBSTR(CODIGO,1,1);
            EXCEPTION WHEN NO_DATA_FOUND THEN 
                MI_DIGITOCAMBIO := RS.DIGITOSCAMBIO;
            END;
                
            MI_CONDICIONDETALLE := ' COMPANIA    = ''' || UN_COMPANIA       || '''
                              AND ANO         = '   || MI_ANOACTUAL      || '
                              AND SUBSTR(CUENTA,1,' || LENGTH(MI_DIGITOCAMBIO) || ')=''' || MI_DIGITOCAMBIO || '''';
                
        ELSE
            MI_DIGITOCAMBIO := RS.DIGITOSCAMBIO;
            MI_CONDICIONDETALLE := ' COMPANIA    = ''' || UN_COMPANIA       || '''
                                  AND ANO         = '   || MI_ANOACTUAL      || '
                                  AND SUBSTR(CUENTA,1,' || LENGTH(MI_DIGITOCAMBIO) || ')=''' || MI_DIGITOCAMBIO || '''
                                  AND TIPO_CPTE  IN(''' || RS.TIPO_DIS || ''',''' || RS.TIPO_RES || '''' 
                                                        || CASE WHEN RS.CLASERESERVA NOT IN('APROPIACION','PASAIGUAL') 
                                                                THEN ',''' || RS.TIPO_REO || '''' END || ')';                                             
        END IF;
        
        /*
        * ELIMINA LOS VALORES DE LAS AFECTACIONES
        */
        BEGIN
            BEGIN                
                MI_TABLA     := 'DETALLE_COMPROBANTE_PPTAL';
                MI_CAMPOS    := 'ANO_AFECT = NULL, TIPO_CPTE_AFECT = NULL, CMPTE_AFECTADO = NULL, DEBITO_AFECTADO  = 0, CREDITO_AFECTADO = 0'; 
                
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME( UN_TABLA => MI_TABLA,
                                                      UN_ACCION     => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICIONDETALLE);
                MI_TABLA     := 'COMPROBANTE_PPTAL';
                MI_CAMPOS    := 'TIPO_CPTE_AFECT = NULL, CMPTE_AFECTADO = NULL';
                MI_CONDICION := ' COMPANIA    = ''' || UN_COMPANIA       || '''
                              AND ANO         = '   || MI_ANOACTUAL      || '
                              AND (TIPO, NUMERO) IN( SELECT DISTINCT TIPO_CPTE, COMPROBANTE
                                                     FROM DETALLE_COMPROBANTE_PPTAL
                                                     WHERE COMPANIA  = ''' || UN_COMPANIA       || '''
                                                     AND SUBSTR(CUENTA,1,' || LENGTH(RS.DIGITOSCAMBIO) || ')=''' || RS.DIGITOSCAMBIO || '''
                                                     AND TIPO_CPTE  IN(''' || RS.TIPO_DIS || ''',''' || RS.TIPO_RES || '''' 
                                                        || CASE WHEN RS.CLASERESERVA NOT IN('APROPIACION','PASAIGUAL') 
                                                            THEN ',''' || RS.TIPO_REO || '''' END || '))';
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME( UN_TABLA => MI_TABLA,
                                                      UN_ACCION     => 'M',
                                                      UN_CAMPOS    => MI_CAMPOS, 
                                                      UN_CONDICION => MI_CONDICION);
                                                       
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
            PCK_ERR_MSG.RAISE_WITH_MSG
                ( UN_EXC_COD    => SQLCODE
                , UN_ERROR_COD  => PCK_ERRORES.ERR_AFECTACION_MODIF_CPTE
                , UN_TABLAERROR => MI_TABLA);
        END;        
    END LOOP;
END PR_ELIMINARAFECTACIONESPPTAL;

FUNCTION FC_GENERAREXCELVIGENCIA
   /*  
    NAME              : FC_GENERAREXCELVIGENCIA
    AUTHORS           : CRISTIAN FERNEY SUESCUN BARRERA
    SOURCE MODULE     : SysmanPresupuesto
    DESCRIPTION       : Valida y genera archivo Excel con los registros presupuestales que serán 
                        creados en el cierre de vigencia. Filtra rubros del plan presupuestal que 
                        tengan regalías marcadas y comprobantes presupuestales asociados,
                        preparándolos para el siguiente año fiscal.
    
    PARAMETERS        : UN_COMPANIA, UN_ANOACIERRE, UN_USUARIO
    RETURN            : BLOB (Archivo Excel)
    
    @NAME:validarCierrePlan
    @METHOD:Post
    CC:3349
   */
    (
     UN_COMPANIA             IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE           IN  PCK_SUBTIPOS.TI_ANIO
    )
RETURN CLOB AS
    MI_RTA          CLOB;
    MI_REGISTRO     VARCHAR2(32000);
    V_EXISTE_REGALIAS NUMBER := 0;
    
    -- Variables para el Plan Presupuestal
    MI_RUBRONUEVO            PLAN_PRESUPUESTAL.CODIGO%TYPE;
    MI_RUBROANTERIOR         PLAN_PRESUPUESTAL.COD_ANTERIOR_VIGENCIA%TYPE;
    MI_NOMBRE                PLAN_PRESUPUESTAL.NOMBRE%TYPE;
    MI_TIPOVIGENCIA          VARCHAR2(200);
    MI_VIGENCIA              PLAN_PRESUPUESTAL.VIGENCIA%TYPE;
    MI_RESERVADEAPROPIACION  PLAN_PRESUPUESTAL.RESERVADEAPROPIACION%TYPE;
    MI_RESERVADECAJA         PLAN_PRESUPUESTAL.RESERVADECAJA%TYPE;
    
    -- Variables para Detalle Comprobante
    MI_CLASE_COMPROBANTE     VARCHAR2(50);
    MI_NATURALEZA            DETALLE_COMPROBANTE_PPTAL.NATURALEZA%TYPE;
    MI_CUENTA                DETALLE_COMPROBANTE_PPTAL.CUENTA%TYPE;
    MI_VALOR_DEBITO          DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO%TYPE;
    MI_TIPO                  COMPROBANTE_PPTAL.TIPO%TYPE;
    MI_NUMERO                COMPROBANTE_PPTAL.NUMERO%TYPE;
    MI_CONSECUTIVO           DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO%TYPE;
    MI_CENTRO_COSTO          DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO%TYPE;
    MI_AUXILIAR              DETALLE_COMPROBANTE_PPTAL.AUXILIAR%TYPE;
    MI_FUENTE_RECURSO        DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO%TYPE;
    MI_REFERENCIA            DETALLE_COMPROBANTE_PPTAL.REFERENCIA%TYPE;
    MI_NRO_DOCUMENTO         COMPROBANTE_PPTAL.NRO_DOCUMENTO%TYPE;
    MI_SECTOR                DETALLE_COMPROBANTE_PPTAL.SECTOR%TYPE;
    MI_PROGRAMA              DETALLE_COMPROBANTE_PPTAL.PROGRAMA%TYPE;
    MI_SUBPROGRAMA           DETALLE_COMPROBANTE_PPTAL.SUBPROGRAMA%TYPE;
    MI_COD_PROD_CUIPO        DETALLE_COMPROBANTE_PPTAL.COD_PROD_CUIPO%TYPE;
    MI_CODIGO_BPIN           DETALLE_COMPROBANTE_PPTAL.CODIGO_BPIN%TYPE;
    MI_CODIGO_CCPET          DETALLE_COMPROBANTE_PPTAL.CODIGO_CCPET%TYPE;
    MI_CODIGO_CPC            DETALLE_COMPROBANTE_PPTAL.CODIGO_CPC%TYPE;
    MI_CODIGOUNIDADEJE       DETALLE_COMPROBANTE_PPTAL.CODIGOUNIDADEJE%TYPE;
    MI_FUENTE_CUIPO          DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO%TYPE;
    MI_CODIGOCCPETREGA       DETALLE_COMPROBANTE_PPTAL.CODIGOCCPETREGA%TYPE;
    MI_POLITICA_PUBLICA      DETALLE_COMPROBANTE_PPTAL.POLITICA_PUBLICA%TYPE;
    MI_DETALLE_SECTORIAL     DETALLE_COMPROBANTE_PPTAL.DETALLE_SECTORIAL%TYPE;
    
BEGIN
    /*
    * VALIDAR SI EXISTEN RUBROS CON REGALIAS
    */
    BEGIN
        SELECT COUNT(1)
        INTO V_EXISTE_REGALIAS
        FROM PLAN_PRESUPUESTAL
        WHERE COMPANIA = UN_COMPANIA
          AND ANO = UN_ANOACIERRE
          AND REGALIAS <> 0
          AND ROWNUM = 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            V_EXISTE_REGALIAS := 0;
    END;
    
    
    IF V_EXISTE_REGALIAS = 0 THEN
        RETURN NULL;  
    END IF;
    
    /*
    * HOJA 1: PLAN PRESUPUESTAL CON REGALIAS
    */
    MI_RTA := 'Plan Presupuestal Regalias' || PCK_DATOS.GL_SEPARADOR_REG;
    MI_RTA := MI_RTA || TO_CLOB( 
                      'RUBRO NUEVO'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      'RUBRO ANTERIOR'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      'NOMBRE'                 ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      'TIPO VIGENCIA'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      'VIGENCIA EJECUCION'     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      'RESERVA APROPIACIÓN'    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      'RESERVA DE CAJA'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      PCK_DATOS.GL_SEPARADOR_REG);
    
    -- Loop para Plan Presupuestal con Regalías
    <<PLAN_REGALIAS>>
    FOR RS_PLAN IN(
        SELECT 
            P.CODIGO AS RUBRO_NUEVO,
            P.COD_ANTERIOR_VIGENCIA AS RUBRO_ANTERIOR,
            P.NOMBRE,
            T.NOMBRE AS TIPO_VIGENCIA,
            P.VIGENCIA AS VIGENCIA_EJECUCION,
            P.RESERVADEAPROPIACION,
            P.RESERVADECAJA
        FROM PLAN_PRESUPUESTAL P
        INNER JOIN TIPOVIGENCIA T
            ON T.CODIGO = P.TIPOVIGENCIA
        WHERE P.COMPANIA = UN_COMPANIA
          AND P.ANO = UN_ANOACIERRE
          AND P.REGALIAS <> 0
        ORDER BY P.CODIGO
    )
    LOOP
        MI_REGISTRO := REPLACE(REPLACE(TO_CLOB( 
                          RS_PLAN.RUBRO_NUEVO            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          RS_PLAN.RUBRO_ANTERIOR         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          TRIM(RS_PLAN.NOMBRE)           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          RS_PLAN.TIPO_VIGENCIA          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          RS_PLAN.VIGENCIA_EJECUCION     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          RS_PLAN.RESERVADEAPROPIACION   ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          RS_PLAN.RESERVADECAJA          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          PCK_DATOS.GL_SEPARADOR_REG),
                            CHR(10),''),CHR(13),'');
        MI_RTA := MI_RTA || MI_REGISTRO;
    END LOOP PLAN_REGALIAS;
    
    
    /*
    * HOJA 2: DETALLE COMPROBANTES DE REGALIAS
    */
    MI_RTA := MI_RTA || PCK_DATOS.GL_SEPARADOR_HOJ || 'Detalle Comprobantes Regalias';
    MI_RTA := MI_RTA || PCK_DATOS.GL_SEPARADOR_REG ||
                                    'CLASE COMPROBANTE'     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'TIPO VIGENCIA'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'VIGENCIA EJECUCION'    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'NATURALEZA'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CUENTA'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'VALOR DEBITO'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'TIPO'                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'NUMERO'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CONSECUTIVO'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CENTRO COSTO'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'AUXILIAR'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'FUENTE RECURSO'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REFERENCIA'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'NRO DOCUMENTO'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'SECTOR'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'PROGRAMA'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'SUBPROGRAMA'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'COD_PROD_CUIPO'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGO_BPIN'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGO_CCPET'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGO_CPC'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGOUNIDADEJE'       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'FUENTE_CUIPO'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGOCCPETREGA'       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'POLITICA_PUBLICA'      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'DETALLE_SECTORIAL'     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    PCK_DATOS.GL_SEPARADOR_REG;
    
    -- Loop para Detalle Comprobantes de Regalías
    <<DETALLE_REGALIAS>>
    FOR RS_DET IN(
        SELECT 
            TC.CLASE AS CLASE_COMPROBANTE,
            TV.NOMBRE AS TIPO_VIGENCIA,
            PP.ANO AS VIGENCIA_EJECUCION,
            DP.NATURALEZA,
            DP.CUENTA,
            DP.VALOR_DEBITO,
            CP.TIPO,
            CP.NUMERO,
            DP.CONSECUTIVO,
            DP.CENTRO_COSTO,
            DP.AUXILIAR,
            DP.FUENTE_RECURSO,
            DP.REFERENCIA,
            CP.NRO_DOCUMENTO,
            DP.SECTOR,
            DP.PROGRAMA,
            DP.SUBPROGRAMA,
            DP.COD_PROD_CUIPO,
            DP.CODIGO_BPIN,
            DP.CODIGO_CCPET,
            DP.CODIGO_CPC,
            DP.CODIGOUNIDADEJE,
            DP.FUENTE_CUIPO,
            DP.CODIGOCCPETREGA,
            DP.POLITICA_PUBLICA,
            DP.DETALLE_SECTORIAL
        FROM DETALLE_COMPROBANTE_PPTAL DP
        INNER JOIN PLAN_PRESUPUESTAL PP 
            ON DP.COMPANIA = PP.COMPANIA 
           AND DP.ANO = PP.ANO 
           AND DP.CUENTA = PP.CODIGO
        INNER JOIN TIPO_COMPROBPP TC 
            ON DP.COMPANIA = TC.COMPANIA 
           AND DP.TIPO_CPTE = TC.CODIGO
        INNER JOIN COMPROBANTE_PPTAL CP
            ON DP.COMPANIA = CP.COMPANIA 
           AND DP.ANO = CP.ANO  
           AND DP.TIPO_CPTE = CP.TIPO 
           AND DP.COMPROBANTE = CP.NUMERO
        LEFT JOIN TIPOVIGENCIA TV
            ON PP.TIPOVIGENCIA = TV.CODIGO
        WHERE DP.COMPANIA = UN_COMPANIA
          AND DP.ANO = UN_ANOACIERRE
          AND PP.REGALIAS <> 0
        ORDER BY TC.CLASE, CP.TIPO, CP.NUMERO, DP.CONSECUTIVO
    )
    LOOP
        MI_REGISTRO := REPLACE(REPLACE(TO_CLOB( 
                        RS_DET.CLASE_COMPROBANTE     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.TIPO_VIGENCIA         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.VIGENCIA_EJECUCION    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.NATURALEZA            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.CUENTA                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.VALOR_DEBITO          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.TIPO                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.NUMERO                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.CONSECUTIVO           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.CENTRO_COSTO          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.AUXILIAR              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.FUENTE_RECURSO        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.REFERENCIA            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.NRO_DOCUMENTO         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.SECTOR                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.PROGRAMA              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.SUBPROGRAMA           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.COD_PROD_CUIPO        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.CODIGO_BPIN           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.CODIGO_CCPET          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.CODIGO_CPC            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.CODIGOUNIDADEJE       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.FUENTE_CUIPO          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.CODIGOCCPETREGA       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.POLITICA_PUBLICA      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        RS_DET.DETALLE_SECTORIAL     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                        PCK_DATOS.GL_SEPARADOR_REG),
                        CHR(10),''),CHR(13),'');
        MI_RTA := MI_RTA || MI_REGISTRO;
    END LOOP DETALLE_REGALIAS;

    RETURN MI_RTA;
    
EXCEPTION
    WHEN OTHERS THEN
        RETURN 'ERROR: ' || SQLERRM;
        
END FC_GENERAREXCELVIGENCIA;

FUNCTION FC_CIERREPRESUPUESTOREGA
    /*  
        NAME              : FC_CIERREPRESUPUESTOREGA
        AUTHORS           : CRISTIAN FERNEY SUESCUN BARRERA
        SOURCE MODULE     : SysmanPresupuesto
        MODIFIER          : 
        DATE MODIFIED     : 
        DESCRIPTION       : Permite realizar el cierre presupuestal anual en base a los movimientos abiertos de presupuesto
                           Solo procesa rubros y comprobantes con REGALIAS <> 0

        @NAME:cierrePresupuestalCb
        CC:3349
    */   
    (
        UN_COMPANIA         IN  PCK_SUBTIPOS.TI_COMPANIA,  
        UN_ANOACIERRE       IN  PCK_SUBTIPOS.TI_ANIO,
        UN_FECHACIERRE      IN  DATE,
        UN_USUARIO          IN  PCK_SUBTIPOS.TI_USUARIO
    )
RETURN CLOB AS

    -- Variables de control
    V_EXISTE_REGALIAS       NUMBER := 0;
    V_GENERAR_PASAIGUAL     NUMBER := 0;
    V_EXISTE_ANO_NUEVO      NUMBER := 0;
    
    -- Variables para respuesta
    MI_RTA                  CLOB;
    MI_RTA2                 NUMBER;
    
    UN_FECHA_COMPROBANTE    DATE;
    UN_ANO_CIERRE           PCK_SUBTIPOS.TI_ANIO;
    UN_ANO_NUEVO            PCK_SUBTIPOS.TI_ANIO;

    -- Colección para tipos de comprobantes en orden
    TYPE T_TIPOS_CP IS TABLE OF VARCHAR2(3) INDEX BY PLS_INTEGER;
    V_TIPOS_CP              T_TIPOS_CP;

BEGIN
    EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS = ''.,''';

    UN_ANO_CIERRE := UN_ANOACIERRE;
    UN_ANO_NUEVO := UN_ANO_CIERRE + 1;
    UN_FECHA_COMPROBANTE := UN_FECHACIERRE;
    
    -- Definir orden de tipos de comprobantes
    V_TIPOS_CP(1)  := 'ADC';
    V_TIPOS_CP(2)  := 'TRA';
    V_TIPOS_CP(3)  := 'DIS';
    V_TIPOS_CP(4)  := 'ADD';
    V_TIPOS_CP(5)  := 'RES';
    V_TIPOS_CP(6)  := 'ADR';
    V_TIPOS_CP(7)  := 'REO';
    V_TIPOS_CP(8)  := 'EGR';
    V_TIPOS_CP(9)  := 'DRO';
    V_TIPOS_CP(10) := 'DMR';
    V_TIPOS_CP(11) := 'DMD';
    V_TIPOS_CP(12) := 'ING';
    V_TIPOS_CP(13) := 'RED';
    V_TIPOS_CP(14) := 'APL';
    V_TIPOS_CP(15) := 'AEG';
    V_TIPOS_CP(16) := 'DEG';
    V_TIPOS_CP(17) := 'DIN';

    -- ==================================================================
    -- PASO 1: VALIDAR CONFIGURACIÓN PASAIGUAL
    -- ==================================================================
       BEGIN
        SELECT GENERAR
        INTO V_GENERAR_PASAIGUAL
        FROM CONFIG_CIERRE_PPTAL
        WHERE COMPANIA = UN_COMPANIA
          AND CLASERESERVA = 'PASAIGUAL'
          AND GENERAR <> 0
          AND TIPOCIERRE = 'REG'
          AND ROWNUM = 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            V_GENERAR_PASAIGUAL := 0;
    END;
    
    IF V_GENERAR_PASAIGUAL = 0 THEN
        RETURN NULL;
    END IF;
    -- ==================================================================
    -- PASO 2: PASAR PLAN PRESUPUESTAL CON REGALIAS (MERGE)
    -- ==================================================================
    
    MERGE INTO PLAN_PRESUPUESTAL T
    USING (
        SELECT COMPANIA, 
               UN_ANO_NUEVO AS ANO, 
               CODIGO, CODIGO_EQUIV, CODIGO_EQUIV2, NOMBRE, NATURALEZA, MOVIMIENTO, 
               MAN_CEN_CTO, MAN_AUX_TER, MAN_AUX_GEN, MAN_AUX_REF, MAN_AUX_FUE, MAN_PAC, 
               VIGENCIA, DINAMICA, INDECONOMICO, APROPIACIONINICIAL, RESERVADECAJA, RESERVADEAPROPIACION, 
               NIVEL1, NIVEL2, NIVEL3, NIVEL4, NIVEL5, NIVEL6, NIVEL7, NIVEL8, INDCONTROL, 
               INFORME, DEPENDENCIAASOCIADA, ENARCHIVOPLANO, CODCONTABLE5544, RECURSO5544, 
               SUBRECURSO5544, COD_EQUIV5544, PERMITECONSOLIDAR, COD_SIGUIENTE_VIGENCIA, 
               COD_ANTERIOR_VIGENCIA, RECONOCIMIENTO, INFORMESHACIENDA, CONSITUACIONFONDOS, 
               ORIGENESPECIFICACIONINGRESOS, CUENTAAFORO, CODIGO_EQUIVALENTE2, CENTRO_EQUIV, 
               AUXILIAR_EQUIV, JERARQUIA_GASTO, NOM_CODIGO_EQUIVALENTE, MANEJA_PROYECTOS, REGALIAS, 
               CREATED_BY, MODIFIED_BY, DESTINO_RECURSOS, PASAR_SALDOS_APROP, CODIGO_MEN, MEN, 
               FECHA_ACTIVACION, DATE_MODIFIED, DATE_CREATED, TIPOVIGENCIA, DESTINO, FUENTE_RECURSOS, 
               COVID, PMR1, PMR2, FUENTERECURSOS, MAN_POLITICA_PUBLICA, EQUIV_GASTO, CUENTAGASTOCNT, 
               TIPOCLASIFICADOR, CLASECLASIFICADOR, SECTOR, PROGRAMA, SUBPROGRAMA, CODIGOPRODUCTO, 
               CODIGOBPIN, CODIGOCCPET, CODIGOCPCDANE, CODIGOUNIDADEJE, CODIGOFUENTE, 
               CODIGOCCPETREGA, TIPO_RECURSO, TIPO_NORMA, NUMERO_NORMA, FECHA_NORMA, 
               APLICA_DEST_ESPECIFICA, TRANSFERENCIA, POLITCA_PUBLICA_CUIPO, DETALLE_SECTORIAL, 
               RECAUDO_VA, CODIGO_CCPET, TERCERO_CHIP, OBLIGACCPET, COFINANCIADO
        FROM PLAN_PRESUPUESTAL 
        WHERE COMPANIA = UN_COMPANIA
          AND ANO = UN_ANO_CIERRE
          AND REGALIAS <> 0
    ) S
    ON (T.COMPANIA = S.COMPANIA 
        AND T.ANO = S.ANO 
        AND T.CODIGO = S.CODIGO)
    WHEN NOT MATCHED THEN
        INSERT (
            COMPANIA, ANO, CODIGO, CODIGO_EQUIV, CODIGO_EQUIV2, NOMBRE, NATURALEZA, MOVIMIENTO, 
            MAN_CEN_CTO, MAN_AUX_TER, MAN_AUX_GEN, MAN_AUX_REF, MAN_AUX_FUE, MAN_PAC, 
            VIGENCIA, DINAMICA, INDECONOMICO, APROPIACIONINICIAL, RESERVADECAJA, RESERVADEAPROPIACION, 
            NIVEL1, NIVEL2, NIVEL3, NIVEL4, NIVEL5, NIVEL6, NIVEL7, NIVEL8, INDCONTROL, 
            INFORME, DEPENDENCIAASOCIADA, ENARCHIVOPLANO, CODCONTABLE5544, RECURSO5544, 
            SUBRECURSO5544, COD_EQUIV5544, PERMITECONSOLIDAR, COD_SIGUIENTE_VIGENCIA, 
            COD_ANTERIOR_VIGENCIA, RECONOCIMIENTO, INFORMESHACIENDA, CONSITUACIONFONDOS, 
            ORIGENESPECIFICACIONINGRESOS, CUENTAAFORO, CODIGO_EQUIVALENTE2, CENTRO_EQUIV, 
            AUXILIAR_EQUIV, JERARQUIA_GASTO, NOM_CODIGO_EQUIVALENTE, MANEJA_PROYECTOS, REGALIAS, 
            CREATED_BY, MODIFIED_BY, DESTINO_RECURSOS, PASAR_SALDOS_APROP, CODIGO_MEN, MEN, 
            FECHA_ACTIVACION, DATE_MODIFIED, DATE_CREATED, TIPOVIGENCIA, DESTINO, FUENTE_RECURSOS, 
            COVID, PMR1, PMR2, FUENTERECURSOS, MAN_POLITICA_PUBLICA, EQUIV_GASTO, CUENTAGASTOCNT, 
            TIPOCLASIFICADOR, CLASECLASIFICADOR, SECTOR, PROGRAMA, SUBPROGRAMA, CODIGOPRODUCTO, 
            CODIGOBPIN, CODIGOCCPET, CODIGOCPCDANE, CODIGOUNIDADEJE, CODIGOFUENTE, 
            CODIGOCCPETREGA, TIPO_RECURSO, TIPO_NORMA, NUMERO_NORMA, FECHA_NORMA, 
            APLICA_DEST_ESPECIFICA, TRANSFERENCIA, POLITCA_PUBLICA_CUIPO, DETALLE_SECTORIAL, 
            RECAUDO_VA, CODIGO_CCPET, TERCERO_CHIP, OBLIGACCPET, COFINANCIADO
        )
        VALUES (
            S.COMPANIA, S.ANO, S.CODIGO, S.CODIGO_EQUIV, S.CODIGO_EQUIV2, S.NOMBRE, S.NATURALEZA, S.MOVIMIENTO, 
            S.MAN_CEN_CTO, S.MAN_AUX_TER, S.MAN_AUX_GEN, S.MAN_AUX_REF, S.MAN_AUX_FUE, S.MAN_PAC, 
            S.VIGENCIA, S.DINAMICA, S.INDECONOMICO, S.APROPIACIONINICIAL, S.RESERVADECAJA, S.RESERVADEAPROPIACION, 
            S.NIVEL1, S.NIVEL2, S.NIVEL3, S.NIVEL4, S.NIVEL5, S.NIVEL6, S.NIVEL7, S.NIVEL8, S.INDCONTROL, 
            S.INFORME, S.DEPENDENCIAASOCIADA, S.ENARCHIVOPLANO, S.CODCONTABLE5544, S.RECURSO5544, 
            S.SUBRECURSO5544, S.COD_EQUIV5544, S.PERMITECONSOLIDAR, S.COD_SIGUIENTE_VIGENCIA, 
            S.COD_ANTERIOR_VIGENCIA, S.RECONOCIMIENTO, S.INFORMESHACIENDA, S.CONSITUACIONFONDOS, 
            S.ORIGENESPECIFICACIONINGRESOS, S.CUENTAAFORO, S.CODIGO_EQUIVALENTE2, S.CENTRO_EQUIV, 
            S.AUXILIAR_EQUIV, S.JERARQUIA_GASTO, S.NOM_CODIGO_EQUIVALENTE, S.MANEJA_PROYECTOS, S.REGALIAS, 
            S.CREATED_BY, S.MODIFIED_BY, S.DESTINO_RECURSOS, S.PASAR_SALDOS_APROP, S.CODIGO_MEN, S.MEN, 
            S.FECHA_ACTIVACION, S.DATE_MODIFIED, S.DATE_CREATED, S.TIPOVIGENCIA, S.DESTINO, S.FUENTE_RECURSOS, 
            S.COVID, S.PMR1, S.PMR2, S.FUENTERECURSOS, S.MAN_POLITICA_PUBLICA, S.EQUIV_GASTO, S.CUENTAGASTOCNT, 
            S.TIPOCLASIFICADOR, S.CLASECLASIFICADOR, S.SECTOR, S.PROGRAMA, S.SUBPROGRAMA, S.CODIGOPRODUCTO, 
            S.CODIGOBPIN, S.CODIGOCCPET, S.CODIGOCPCDANE, S.CODIGOUNIDADEJE, S.CODIGOFUENTE, 
            S.CODIGOCCPETREGA, S.TIPO_RECURSO, S.TIPO_NORMA, S.NUMERO_NORMA, S.FECHA_NORMA, 
            S.APLICA_DEST_ESPECIFICA, S.TRANSFERENCIA, S.POLITCA_PUBLICA_CUIPO, S.DETALLE_SECTORIAL, 
            S.RECAUDO_VA, S.CODIGO_CCPET, S.TERCERO_CHIP, S.OBLIGACCPET, S.COFINANCIADO
        );

    -- ==================================================================
    -- PASO 3: PASAR APROPIACIONES INICIALES (MERGE)
    -- ==================================================================
    
    MERGE INTO APROPIACIONESINICIALES T
    USING (
        SELECT AI.COMPANIA, 
               UN_ANO_NUEVO AS ANO, 
               AI.CODIGO, AI.TERCERO, AI.SUCURSAL, AI.AUXILIAR, AI.CENTRO_COSTO, AI.REFERENCIA, 
               AI.FUENTE_RECURSO, AI.APROPIACIONINICIAL, AI.DEBITO, AI.CREDITO, AI.CONTABILIZADO, 
               AI.CREATED_BY, AI.MODIFIED_BY, AI.DATE_CREATED, AI.DATE_MODIFIED, AI.ID, 
               AI.CODIGO_CCPET, AI.PROGRAMA, AI.CODIGOUNIDADEJE, AI.CODIGOBPIN, 
               AI.DETALLESECTORIAL, AI.RECURSO_SGR, AI.SECTOR
        FROM PLAN_PRESUPUESTAL PP 
        INNER JOIN APROPIACIONESINICIALES AI 
          ON PP.COMPANIA = AI.COMPANIA 
          AND PP.ANO = AI.ANO 
          AND PP.CODIGO = AI.CODIGO 
        WHERE PP.COMPANIA = UN_COMPANIA
          AND PP.ANO = UN_ANO_CIERRE
          AND PP.REGALIAS <> 0
    ) S
    ON (T.COMPANIA = S.COMPANIA 
        AND T.ANO = S.ANO 
        AND T.CODIGO = S.CODIGO
        AND T.TERCERO = S.TERCERO
        AND T.SUCURSAL = S.SUCURSAL
        AND T.AUXILIAR = S.AUXILIAR
        AND T.CENTRO_COSTO = S.CENTRO_COSTO
        AND T.REFERENCIA = S.REFERENCIA
        AND T.FUENTE_RECURSO = S.FUENTE_RECURSO)
    WHEN NOT MATCHED THEN
        INSERT (COMPANIA, ANO, CODIGO, TERCERO, SUCURSAL, AUXILIAR, CENTRO_COSTO, REFERENCIA, 
                FUENTE_RECURSO, APROPIACIONINICIAL, DEBITO, CREDITO, CONTABILIZADO, 
                CREATED_BY, MODIFIED_BY, DATE_CREATED, DATE_MODIFIED, ID, CODIGO_CCPET, PROGRAMA, 
                CODIGOUNIDADEJE, CODIGOBPIN, DETALLESECTORIAL, RECURSO_SGR, SECTOR)
        VALUES (S.COMPANIA, S.ANO, S.CODIGO, S.TERCERO, S.SUCURSAL, S.AUXILIAR, S.CENTRO_COSTO, S.REFERENCIA, 
                S.FUENTE_RECURSO, S.APROPIACIONINICIAL, S.DEBITO, S.CREDITO, S.CONTABILIZADO, 
                S.CREATED_BY, S.MODIFIED_BY, S.DATE_CREATED, S.DATE_MODIFIED, S.ID, S.CODIGO_CCPET, S.PROGRAMA, 
                S.CODIGOUNIDADEJE, S.CODIGOBPIN, S.DETALLESECTORIAL, S.RECURSO_SGR, S.SECTOR);

    -- Contabilizar apropiaciones iniciales
    MI_RTA := PCK_PRESUPUESTO.FC_CONTABILIZARAPROPINICIAL(
        UN_COMPANIA => UN_COMPANIA,
        UN_ANIO     => UN_ANO_NUEVO
    );

    -- ==================================================================
    -- PASO 4: PASAR COMPROBANTES PPTAL 
    -- ==================================================================
    
    MERGE INTO COMPROBANTE_PPTAL T
    USING (
        SELECT DISTINCT 
               CP.COMPANIA, 
               UN_ANO_NUEVO AS ANO, 
               CP.TIPO, CP.NUMERO, CP.TERCERO, CP.SUCURSAL, CP.CENTRO_COSTO, CP.AUXILIAR, 
               CP.REFERENCIA, CP.FUENTE_RECURSO, CP.DESCRIPCION, CP.TEXTO, CP.NRO_DOCUMENTO, 
               CP.VLR_DOCUMENTO, CP.DEBITO, CP.CREDITO, CP.DEBITO_AFECTADO, CP.CREDITO_AFECTADO, 
               CP.DEBITO_AFECTADOCNT, CP.CREDITO_AFECTADOCNT, CP.MODIFICACION_DEBITO, 
               CP.MODIFICACION_CREDITO, CP.ABONADO, CP.ENTREGADO, CP.PAGADOBANCO, CP.IMPRESO, 
               CP.ANULADO, CP.CONTRACTUAL, CP.DESTINO, CP.REGISTROAUTOMATICO, CP.CARGO, 
               CP.DEPENDENCIA, CP.CODSOLICITANTE, CP.SUCSOLICITANTE, CP.PAPELES, CP.TIPOCONTRATO, 
               CP.NUMEROCONTRATO, CP.SITUACIONFONDOS, CP.TIPO_DOCUMENTO, CP.ANO_GENERA, 
               CP.TIPO_GENERA, CP.NRO_GENERA, CP.TIPO_VF, CP.TIPO_AUTORIZACION_VF, 
               CP.NRO_AUTORIZACION_VF, CP.COD_PROYECTO_PPTAL, CP.LUGAR, CP.FUENTE_FINANCIACION, 
               CP.ASIGNACION, CP.DANE, CP.CREATED_BY, CP.MODIFIED_BY, CP.MEN_VIATICOS, CP.INF_MEN, 
               UN_FECHA_COMPROBANTE AS FECHA_VCN_DOC,
               CP.FECHA_APROB_VF, CP.FECHA_VENCIMIENTO,
               UN_FECHA_COMPROBANTE AS FECHA,
               CP.DATE_CREATED, CP.DATE_MODIFIED, CP.FECHA_AUTO_VF, CP.MES, CP.DIA, CP.ANOPROYECTO, 
               CP.CMPTE_AFECTADO, CP.TIPO_CPTE_AFECT, CP.NOLIBERAR, CP.FICHA,
               CP.IDCONTRATOSECOP, CP.IDCONTRATOENTIDAD, CP.CERTIFICADO_PAA
        FROM PLAN_PRESUPUESTAL PP 
        INNER JOIN DETALLE_COMPROBANTE_PPTAL DCP 
          ON PP.COMPANIA = DCP.COMPANIA 
          AND PP.ANO = DCP.ANO 
          AND PP.CODIGO = DCP.CUENTA 
        INNER JOIN COMPROBANTE_PPTAL CP 
          ON CP.COMPANIA = DCP.COMPANIA 
          AND CP.ANO = DCP.ANO 
          AND CP.TIPO = DCP.TIPO_CPTE 
          AND CP.NUMERO = DCP.COMPROBANTE 
        WHERE PP.COMPANIA = UN_COMPANIA
          AND PP.ANO = UN_ANO_CIERRE
          AND PP.REGALIAS <> 0
    ) S
    ON (T.COMPANIA = S.COMPANIA 
        AND T.ANO = S.ANO 
        AND T.TIPO = S.TIPO 
        AND T.NUMERO = S.NUMERO)
    WHEN NOT MATCHED THEN
        INSERT (
            COMPANIA, ANO, TIPO, NUMERO, TERCERO, SUCURSAL, CENTRO_COSTO, AUXILIAR, REFERENCIA, 
            FUENTE_RECURSO, DESCRIPCION, TEXTO, NRO_DOCUMENTO, VLR_DOCUMENTO, DEBITO, CREDITO, 
            DEBITO_AFECTADO, CREDITO_AFECTADO, DEBITO_AFECTADOCNT, CREDITO_AFECTADOCNT, 
            MODIFICACION_DEBITO, MODIFICACION_CREDITO, ABONADO, ENTREGADO, PAGADOBANCO, IMPRESO, 
            ANULADO, CONTRACTUAL, DESTINO, REGISTROAUTOMATICO, CARGO, DEPENDENCIA, CODSOLICITANTE, 
            SUCSOLICITANTE, PAPELES, TIPOCONTRATO, NUMEROCONTRATO, SITUACIONFONDOS, TIPO_DOCUMENTO, 
            ANO_GENERA, TIPO_GENERA, NRO_GENERA, TIPO_VF, TIPO_AUTORIZACION_VF, 
            NRO_AUTORIZACION_VF, COD_PROYECTO_PPTAL, LUGAR, FUENTE_FINANCIACION, ASIGNACION, 
            DANE, CREATED_BY, MODIFIED_BY, MEN_VIATICOS, INF_MEN, FECHA_VCN_DOC, FECHA_APROB_VF, 
            FECHA_VENCIMIENTO, FECHA, DATE_CREATED, DATE_MODIFIED, FECHA_AUTO_VF, MES, DIA, 
            ANOPROYECTO, CMPTE_AFECTADO, TIPO_CPTE_AFECT, NOLIBERAR, FICHA, IDCONTRATOSECOP, 
            IDCONTRATOENTIDAD, CERTIFICADO_PAA
        )
        VALUES (
            S.COMPANIA, S.ANO, S.TIPO, S.NUMERO, S.TERCERO, S.SUCURSAL, S.CENTRO_COSTO, S.AUXILIAR, S.REFERENCIA, 
            S.FUENTE_RECURSO, S.DESCRIPCION, S.TEXTO, S.NRO_DOCUMENTO, S.VLR_DOCUMENTO, S.DEBITO, S.CREDITO, 
            S.DEBITO_AFECTADO, S.CREDITO_AFECTADO, S.DEBITO_AFECTADOCNT, S.CREDITO_AFECTADOCNT, 
            S.MODIFICACION_DEBITO, S.MODIFICACION_CREDITO, S.ABONADO, S.ENTREGADO, S.PAGADOBANCO, S.IMPRESO, 
            S.ANULADO, S.CONTRACTUAL, S.DESTINO, S.REGISTROAUTOMATICO, S.CARGO, S.DEPENDENCIA, S.CODSOLICITANTE, 
            S.SUCSOLICITANTE, S.PAPELES, S.TIPOCONTRATO, S.NUMEROCONTRATO, S.SITUACIONFONDOS, S.TIPO_DOCUMENTO, 
            S.ANO_GENERA, S.TIPO_GENERA, S.NRO_GENERA, S.TIPO_VF, S.TIPO_AUTORIZACION_VF, 
            S.NRO_AUTORIZACION_VF, S.COD_PROYECTO_PPTAL, S.LUGAR, S.FUENTE_FINANCIACION, S.ASIGNACION, 
            S.DANE, S.CREATED_BY, S.MODIFIED_BY, S.MEN_VIATICOS, S.INF_MEN, S.FECHA_VCN_DOC, S.FECHA_APROB_VF, 
            S.FECHA_VENCIMIENTO, S.FECHA, S.DATE_CREATED, S.DATE_MODIFIED, S.FECHA_AUTO_VF, S.MES, S.DIA, 
            S.ANOPROYECTO, S.CMPTE_AFECTADO, S.TIPO_CPTE_AFECT, S.NOLIBERAR, S.FICHA, S.IDCONTRATOSECOP, 
            S.IDCONTRATOENTIDAD, S.CERTIFICADO_PAA
        );

    -- ==================================================================
    -- PASO 5: PASAR DETALLES DE COMPROBANTES POR TIPO (EN ORDEN) 
    -- ==================================================================
    
    FOR i IN 1 .. V_TIPOS_CP.COUNT LOOP
        
        MERGE INTO DETALLE_COMPROBANTE_PPTAL T
        USING (
            SELECT DCP.COMPANIA, 
                   UN_ANO_NUEVO AS ANO, 
                   DCP.TIPO_CPTE, DCP.COMPROBANTE, DCP.CONSECUTIVO, DCP.CUENTA, DCP.DESCRIPCION, 
                   DCP.VALOR_DEBITO, DCP.VALOR_CREDITO, 
                   0 AS DEBITO_AFECTADO, 
                   0 AS CREDITO_AFECTADO, 
                   DCP.DEBITO_AFECTADOCNT, DCP.CREDITO_AFECTADOCNT, 
                   0 AS MODIFICACION_DEBITO, 
                   0 AS MODIFICACION_CREDITO, 
                   DCP.TIPO_DOCUMENTO, DCP.NRO_DOCUMENTO, DCP.CENTRO_COSTO, DCP.TERCERO, 
                   DCP.SUCURSAL, DCP.AUXILIAR, DCP.REFERENCIA, DCP.FUENTE_RECURSO, 
                   NULL AS ANO_AFECT, 
                   NULL AS TIPO_CPTE_AFECT, 
                   NULL AS CMPTE_AFECTADO, 
                   NULL AS CONSECUTIVOPPTO,
                   DCP.NATURALEZA, DCP.PROGRAMARPAC, DCP.SALDO, DCP.VALORCOMPROMISO, 
                   DCP.DISPONIBILIDADNETA, DCP.DISACUMULADAS, DCP.RESACUMULADAS, DCP.APRDEFINITIVA, 
                   DCP.TIPOCONTRATO, DCP.NUMEROCONTRATO, DCP.CONTRACTUAL, DCP.PAPELES, 
                   DCP.PAC_DISPONIBLE, DCP.RECONOCIMIENTO, DCP.CONSITUACIONFONDOS, 
                   DCP.ANO_GENERAMOP, DCP.TIPO_GENERAMOP, DCP.NRO_GENERAMOP, 
                   DCP.PORCENTAJEDISTRIBUIDO, DCP.CREATED_BY, DCP.MODIFIED_BY, 
                   UN_FECHA_COMPROBANTE AS FECHA, 
                   DCP.DATE_MODIFIED, DCP.DATE_CREATED, DCP.HORA, DCP.AUXILIARI, DCP.TERCEROI, 
                   DCP.REFERENCIAI, DCP.CENTRO_COSTOI, DCP.DIA, DCP.FUENTE_RECURSOI, DCP.SUCURSALI, 
                   DCP.ID, DCP.MES, DCP.CUENTA_CREDITO, DCP.CUENTA_DEBITO, DCP.CMPTE_SOLICI_AFECTADO, 
                   DCP.DEPENDENCIA, DCP.TIPOT, DCP.CLASET, DCP.NUMERO_SOLICITUD, DCP.TIPO_SOLICITUD, 
                   DCP.SALDO_DIS, DCP.SALDO_RES, DCP.SALDO_REO, DCP.SALDO_EGR, DCP.FUENTE_CUIPO, 
                   DCP.COD_PROD_CUIPO, DCP.CODIGO_CPC, DCP.CODIGO_BPIN, DCP.CODIGO_CCPET, 
                   DCP.SECTOR, DCP.PROGRAMA, DCP.SUBPROGRAMA, DCP.CODIGOUNIDADEJE, 
                   DCP.CODIGOCCPETREGA, DCP.SITUACION_FONDOS, DCP.POLITICA_PUBLICA, 
                   DCP.DETALLE_SECTORIAL, DCP.RECURSO_SGR, DCP.IMPUESTO, DCP.VALOR_BASE, 
                   DCP.LOTE, DCP.SALDO_ACTUAL, DCP.F_HORA_SALDO_ACTUAL
            FROM PLAN_PRESUPUESTAL PP 
            INNER JOIN DETALLE_COMPROBANTE_PPTAL DCP 
              ON PP.COMPANIA = DCP.COMPANIA 
              AND PP.ANO = DCP.ANO 
              AND PP.CODIGO = DCP.CUENTA 
            INNER JOIN COMPROBANTE_PPTAL CP 
              ON CP.COMPANIA = DCP.COMPANIA 
              AND CP.ANO = DCP.ANO 
              AND CP.TIPO = DCP.TIPO_CPTE 
              AND CP.NUMERO = DCP.COMPROBANTE 
            INNER JOIN TIPO_COMPROBPP TC 
              ON CP.COMPANIA = TC.COMPANIA 
              AND CP.TIPO = TC.CODIGO 
            WHERE PP.COMPANIA = UN_COMPANIA
              AND PP.ANO = UN_ANO_CIERRE
              AND PP.REGALIAS <> 0 
              AND TC.CLASE = V_TIPOS_CP(i)
        ) S
        ON (T.COMPANIA = S.COMPANIA 
            AND T.ANO = S.ANO 
            AND T.TIPO_CPTE = S.TIPO_CPTE 
            AND T.COMPROBANTE = S.COMPROBANTE 
            AND T.CONSECUTIVO = S.CONSECUTIVO)
        WHEN NOT MATCHED THEN
            INSERT (
                COMPANIA, ANO, TIPO_CPTE, COMPROBANTE, CONSECUTIVO, CUENTA, DESCRIPCION, 
                VALOR_DEBITO, VALOR_CREDITO, DEBITO_AFECTADO, CREDITO_AFECTADO, 
                DEBITO_AFECTADOCNT, CREDITO_AFECTADOCNT,
                MODIFICACION_DEBITO, MODIFICACION_CREDITO, TIPO_DOCUMENTO, NRO_DOCUMENTO, 
                CENTRO_COSTO, TERCERO, SUCURSAL, AUXILIAR, REFERENCIA, FUENTE_RECURSO, 
                ANO_AFECT, TIPO_CPTE_AFECT, CMPTE_AFECTADO, CONSECUTIVOPPTO, NATURALEZA, 
                PROGRAMARPAC, SALDO, VALORCOMPROMISO, DISPONIBILIDADNETA, DISACUMULADAS, 
                RESACUMULADAS, APRDEFINITIVA, TIPOCONTRATO, NUMEROCONTRATO, CONTRACTUAL, 
                PAPELES, PAC_DISPONIBLE, RECONOCIMIENTO, CONSITUACIONFONDOS,
                ANO_GENERAMOP, TIPO_GENERAMOP, NRO_GENERAMOP, PORCENTAJEDISTRIBUIDO, 
                CREATED_BY, MODIFIED_BY, FECHA, DATE_MODIFIED, DATE_CREATED, HORA, AUXILIARI, 
                TERCEROI, REFERENCIAI, CENTRO_COSTOI, DIA, FUENTE_RECURSOI, SUCURSALI, ID, MES, 
                CUENTA_CREDITO, CUENTA_DEBITO, CMPTE_SOLICI_AFECTADO, DEPENDENCIA, TIPOT, CLASET, 
                NUMERO_SOLICITUD, TIPO_SOLICITUD, SALDO_DIS, SALDO_RES, SALDO_REO, SALDO_EGR, 
                FUENTE_CUIPO, COD_PROD_CUIPO, CODIGO_CPC, CODIGO_BPIN, CODIGO_CCPET, SECTOR, 
                PROGRAMA, SUBPROGRAMA, CODIGOUNIDADEJE, CODIGOCCPETREGA, SITUACION_FONDOS, 
                POLITICA_PUBLICA, DETALLE_SECTORIAL, RECURSO_SGR, IMPUESTO, VALOR_BASE, LOTE, 
                SALDO_ACTUAL, F_HORA_SALDO_ACTUAL
            )
            VALUES (
                S.COMPANIA, S.ANO, S.TIPO_CPTE, S.COMPROBANTE, S.CONSECUTIVO, S.CUENTA, S.DESCRIPCION, 
                S.VALOR_DEBITO, S.VALOR_CREDITO, S.DEBITO_AFECTADO, S.CREDITO_AFECTADO, 
                S.DEBITO_AFECTADOCNT, S.CREDITO_AFECTADOCNT,
                S.MODIFICACION_DEBITO, S.MODIFICACION_CREDITO, S.TIPO_DOCUMENTO, S.NRO_DOCUMENTO, 
                S.CENTRO_COSTO, S.TERCERO, S.SUCURSAL, S.AUXILIAR, S.REFERENCIA, S.FUENTE_RECURSO, 
                S.ANO_AFECT, S.TIPO_CPTE_AFECT, S.CMPTE_AFECTADO, S.CONSECUTIVOPPTO, S.NATURALEZA, 
                S.PROGRAMARPAC, S.SALDO, S.VALORCOMPROMISO, S.DISPONIBILIDADNETA, S.DISACUMULADAS, 
                S.RESACUMULADAS, S.APRDEFINITIVA, S.TIPOCONTRATO, S.NUMEROCONTRATO, S.CONTRACTUAL, 
                S.PAPELES, S.PAC_DISPONIBLE, S.RECONOCIMIENTO, S.CONSITUACIONFONDOS,
                S.ANO_GENERAMOP, S.TIPO_GENERAMOP, S.NRO_GENERAMOP, S.PORCENTAJEDISTRIBUIDO, 
                S.CREATED_BY, S.MODIFIED_BY, S.FECHA, S.DATE_MODIFIED, S.DATE_CREATED, S.HORA, 
                S.AUXILIARI, S.TERCEROI, S.REFERENCIAI, S.CENTRO_COSTOI, S.DIA, S.FUENTE_RECURSOI, 
                S.SUCURSALI, S.ID, S.MES, S.CUENTA_CREDITO, S.CUENTA_DEBITO, S.CMPTE_SOLICI_AFECTADO, 
                S.DEPENDENCIA, S.TIPOT, S.CLASET, S.NUMERO_SOLICITUD, S.TIPO_SOLICITUD, 
                S.SALDO_DIS, S.SALDO_RES, S.SALDO_REO, S.SALDO_EGR, S.FUENTE_CUIPO, 
                S.COD_PROD_CUIPO, S.CODIGO_CPC, S.CODIGO_BPIN, S.CODIGO_CCPET, S.SECTOR, 
                S.PROGRAMA, S.SUBPROGRAMA, S.CODIGOUNIDADEJE, S.CODIGOCCPETREGA, S.SITUACION_FONDOS, 
                S.POLITICA_PUBLICA, S.DETALLE_SECTORIAL, S.RECURSO_SGR, S.IMPUESTO, S.VALOR_BASE, 
                S.LOTE, S.SALDO_ACTUAL, S.F_HORA_SALDO_ACTUAL
            );
        
    END LOOP;

    -- ==================================================================
    -- PASO 6: ACTUALIZAR AFECTACIONES EN DETALLES 
    -- ==================================================================
    MERGE INTO DETALLE_COMPROBANTE_PPTAL T
    USING (
        SELECT DISTINCT
            DCP.COMPANIA,
            UN_ANO_NUEVO AS ANO,
            DCP.TIPO_CPTE,
            DCP.COMPROBANTE,
            DCP.CONSECUTIVO,
            UN_ANO_NUEVO AS ANO_AFECT,
            DCP.TIPO_CPTE_AFECT,
            DCP.CMPTE_AFECTADO,
            DCP.CONSECUTIVOPPTO
        FROM PLAN_PRESUPUESTAL PP
        INNER JOIN DETALLE_COMPROBANTE_PPTAL DCP
            ON PP.COMPANIA = DCP.COMPANIA
           AND PP.ANO = DCP.ANO
           AND PP.CODIGO = DCP.CUENTA
        WHERE PP.COMPANIA = UN_COMPANIA
          AND PP.ANO = UN_ANOACIERRE
          AND PP.REGALIAS <> 0
          AND DCP.ANO_AFECT IS NOT NULL
    ) S
    ON (T.COMPANIA = S.COMPANIA 
        AND T.ANO = S.ANO 
        AND T.TIPO_CPTE = S.TIPO_CPTE 
        AND T.COMPROBANTE = S.COMPROBANTE 
        AND T.CONSECUTIVO = S.CONSECUTIVO)
    WHEN MATCHED THEN
        UPDATE SET
            T.ANO_AFECT = S.ANO_AFECT,
            T.TIPO_CPTE_AFECT = S.TIPO_CPTE_AFECT,
            T.CMPTE_AFECTADO = S.CMPTE_AFECTADO,
            T.CONSECUTIVOPPTO = S.CONSECUTIVOPPTO;

    -- ==================================================================
    -- PASO 7: REVISAR AFECTACIONES
    -- ==================================================================
    MI_RTA2 := PCK_PRESUPUESTO.FC_REVISARAFECTACIONESHR(
        UN_COMPANIA => UN_COMPANIA, 
        UN_ANO      => UN_ANO_NUEVO, 
        UN_USUARIO  => UN_USUARIO
    );

    RETURN 'OK: Cierre presupuestal ejecutado correctamente';

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RETURN 'ERROR: ' || SQLERRM;

END FC_CIERREPRESUPUESTOREGA;

FUNCTION FC_VALIDARREVERSARCIE
   /*  
        AUTHORS           : SYSMAN  SAS
        AUTHOR            : GERMAN DAVID ROJAS
        DATE              : 06/01/2026
        DESCRIPTION       : Permite generar archivo de excel con validación de los datos que se eliminaran al ejecutar el reversar cierre
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:validarReversarCierre
        @METHOD:Post
*/   
    (
     UN_COMPANIA             IN  PCK_SUBTIPOS.TI_COMPANIA,  
     UN_ANOACIERRE           IN  PCK_SUBTIPOS.TI_ANIO,
     UN_CIERRENORMAL         IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREPASIVO         IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREVIGFUTURAS     IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREVIGFUTUAPASIVO IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERREREGALIAS       IN  PCK_SUBTIPOS.TI_LOGICO,
     UN_CIERRECOFINANCIADOS  IN  PCK_SUBTIPOS.TI_LOGICO
    )
RETURN CLOB AS
    MI_ANOACTUAL    NUMBER(4,0);
    MI_RTA          CLOB;
    MI_RTA_DETALLE  CLOB;
    MI_CONSULTA     VARCHAR2(32000);
    MI_REGISTRO     VARCHAR2(32000);
    MI_LIKES        VARCHAR2(32000);
    MI_TIPOCIERRE   CONFIG_CIERRE_PPTAL.TIPOCIERRE%TYPE;
    MI_RS           SYS_REFCURSOR;
    MI_RUBRONUEVO            DETALLE_COMPROBANTE_PPTAL.CUENTA%TYPE;
    MI_RUBROANTERIOR         DETALLE_COMPROBANTE_PPTAL.CUENTA%TYPE;
    MI_NOMBRE                VARCHAR2(600);
    MI_TIPOVIGENCIA          VARCHAR2(200);
    MI_RESERVADEAPROPIACION  VARCHAR2(20);
    MI_RESERVADECAJA         VARCHAR2(20);
    MI_RUBRODUPLICADO        DETALLE_COMPROBANTE_PPTAL.CUENTA%TYPE;
    MI_ANOVIGENCIA           NUMBER(5,0);
    MI_TIPOFINAL             VARCHAR2(32);
    MI_NUMERO                NUMBER(20,2);
    MI_CONSECUTIVO           NUMBER(20,2);
    MI_DIGITOCAMBIO          CONFIG_CIERRE_PPTAL.DIGITOSCAMBIO%TYPE;
    MI_ANO                   DETALLE_COMPROBANTE_PPTAL.ANO%TYPE;
    MI_TIPO_CPTE             DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE%TYPE;
    MI_COMPROBANTE           DETALLE_COMPROBANTE_PPTAL.COMPROBANTE%TYPE;
    MI_CUENTA                DETALLE_COMPROBANTE_PPTAL.CUENTA%TYPE;
    MI_VALOR_DEBITO          DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO%TYPE;
    MI_VALOR_CREDITO         DETALLE_COMPROBANTE_PPTAL.VALOR_CREDITO%TYPE;
    MI_TERCERO               DETALLE_COMPROBANTE_PPTAL.TERCERO%TYPE;
    MI_SUCURSAL              DETALLE_COMPROBANTE_PPTAL.SUCURSAL%TYPE;
    MI_AUXILIAR              DETALLE_COMPROBANTE_PPTAL.AUXILIAR%TYPE;
    MI_REFERENCIA            DETALLE_COMPROBANTE_PPTAL.REFERENCIA%TYPE;
    MI_FUENTE_RECURSO        DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO%TYPE;
    MI_CENTRO_COSTO          DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO%TYPE;
    MI_ANO_AFECT             DETALLE_COMPROBANTE_PPTAL.ANO_AFECT%TYPE;
    MI_TIPO_CPTE_AFECT       DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE_AFECT%TYPE;
    MI_CMPTE_AFECTADO        DETALLE_COMPROBANTE_PPTAL.CMPTE_AFECTADO%TYPE;
    MI_CONSECUTIVOPPTO       DETALLE_COMPROBANTE_PPTAL.CONSECUTIVOPPTO%TYPE;
    MI_FUENTE_CUIPO          DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO%TYPE;
    MI_COD_PROD_CUIPO        DETALLE_COMPROBANTE_PPTAL.COD_PROD_CUIPO%TYPE;
    MI_CODIGO_CPC            DETALLE_COMPROBANTE_PPTAL.CODIGO_CPC%TYPE;
    MI_CODIGO_BPIN           DETALLE_COMPROBANTE_PPTAL.CODIGO_BPIN%TYPE;
    MI_CODIGO_CCPET          DETALLE_COMPROBANTE_PPTAL.CODIGO_CCPET%TYPE;
    MI_SECTOR                DETALLE_COMPROBANTE_PPTAL.SECTOR%TYPE;
    MI_PROGRAMA              DETALLE_COMPROBANTE_PPTAL.PROGRAMA%TYPE;
    MI_SUBPROGRAMA           DETALLE_COMPROBANTE_PPTAL.SUBPROGRAMA%TYPE;
    MI_CODIGOUNIDADEJE       DETALLE_COMPROBANTE_PPTAL.CODIGOUNIDADEJE%TYPE;
    MI_CODIGOCCPETREGA       DETALLE_COMPROBANTE_PPTAL.CODIGOCCPETREGA%TYPE;
BEGIN
    MI_ANOACTUAL     := UN_ANOACIERRE + 1;
    
    MI_TIPOCIERRE := CASE
      WHEN UN_CIERRENORMAL <> 0           THEN 'NOR'
      WHEN UN_CIERREPASIVO <> 0           THEN 'PAE'
      WHEN UN_CIERREVIGFUTURAS <> 0       THEN 'VF'
      WHEN UN_CIERREVIGFUTUAPASIVO <> 0   THEN 'VFE'
      WHEN UN_CIERREREGALIAS <> 0         THEN 'REG'
      WHEN UN_CIERRECOFINANCIADOS <> 0    THEN 'COF'
      ELSE NULL END;
    
    /*
    * DATOS DE COMPROBANTES AFECTADOS
    */
    
    MI_CONSULTA := 'SELECT TIPO_CPTE, COMPROBANTE, CONSECUTIVO, CUENTA, TIPO_CPTE_AFECT, CMPTE_AFECTADO, CONSECUTIVOPPTO, 
                    VALOR_DEBITO, VALOR_CREDITO, CENTRO_COSTO, TERCERO, AUXILIAR, REFERENCIA, FUENTE_RECURSO
                    FROM DETALLE_COMPROBANTE_PPTAL
                    WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA = ''' || UN_COMPANIA || ''' 
                    AND DETALLE_COMPROBANTE_PPTAL.ANO = ' || MI_ANOACTUAL;

    MI_LIKES := '';
    
    BEGIN
        SELECT ' AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE_AFECT IN ('
           || LISTAGG(
                '''' ||
                CASE WHEN CLASERESERVA = 'APROPIACION' THEN TIPO_RES
                     WHEN CLASERESERVA = 'CAJA'        THEN TIPO_REO END || '''',','
              ) WITHIN GROUP (ORDER BY CLASERESERVA) || ')'
        INTO MI_LIKES
    FROM CONFIG_CIERRE_PPTAL
    WHERE COMPANIA   = '001'
      AND TIPOCIERRE = 'NOR'
      AND GENERAR   <> 0
      AND (
            (CLASERESERVA = 'APROPIACION' AND TIPO_RES IS NOT NULL)
         OR (CLASERESERVA = 'CAJA'        AND TIPO_REO IS NOT NULL)
          );
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_LIKES := '';
    END;
    
    IF MI_TIPOCIERRE = 'COF' THEN
        
        MI_CONSULTA := 'SELECT DCP.TIPO_CPTE, DCP.COMPROBANTE, DCP.CONSECUTIVO, DCP.CUENTA, DCP.TIPO_CPTE_AFECT, DCP.CMPTE_AFECTADO,
            DCP.CONSECUTIVOPPTO, DCP.VALOR_DEBITO, DCP.VALOR_CREDITO, DCP.CENTRO_COSTO, DCP.TERCERO, DCP.AUXILIAR, DCP.REFERENCIA, DCP.FUENTE_RECURSO
                         FROM DETALLE_COMPROBANTE_PPTAL DCP
                         INNER JOIN PLAN_PRESUPUESTAL PP
                            ON DCP.COMPANIA = PP.COMPANIA
                           AND DCP.ANO      = PP.ANO
                           AND DCP.CUENTA   = PP.CODIGO
                         INNER JOIN TIPO_COMPROBPP T
                            ON DCP.COMPANIA = T.COMPANIA
                           AND DCP.TIPO_CPTE = T.CODIGO
                         WHERE DCP.COMPANIA = ''' || UN_COMPANIA || '''
                           AND DCP.ANO = ' || MI_ANOACTUAL || '
                           AND PP.COFINANCIADO <> 0
                           AND T.CLASE IN (''RES'',''REO'',''EGR'')
                           AND DCP.COMPROBANTE LIKE ''' || MI_ANOACTUAL || '%''
                           AND DCP.CMPTE_AFECTADO LIKE ''' || UN_ANOACIERRE || '%''';
        MI_LIKES := '';
    END IF;
      
    MI_CONSULTA := MI_CONSULTA || MI_LIKES;

    MI_RTA := 'Comprobantes afectados' ||  PCK_DATOS.GL_SEPARADOR_REG;
    MI_RTA := MI_RTA || TO_CLOB( 
                                'TIPO_CPTE'             ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                'COMPROBANTE'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                'CONSECUTIVO'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                'CUENTA'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                'TIPO_CPTE_AFECT'       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                'CMPTE_AFECTADO'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                'CONSECUTIVOPPTO'       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                'VALOR_DEBITO'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                'VALOR_CREDITO'         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                'CENTRO_COSTO'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                'TERCERO'               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                'AUXILIAR'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                'REFERENCIA'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                'FUENTE_RECURSO'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                      PCK_DATOS.GL_SEPARADOR_REG);
                      
    OPEN MI_RS FOR MI_CONSULTA;
    LOOP
        FETCH MI_RS
        INTO MI_TIPO_CPTE, MI_COMPROBANTE, MI_CONSECUTIVO, MI_CUENTA, MI_TIPO_CPTE_AFECT, MI_CMPTE_AFECTADO, MI_CONSECUTIVOPPTO,
                MI_VALOR_DEBITO, MI_VALOR_CREDITO, MI_CENTRO_COSTO, MI_TERCERO, MI_AUXILIAR, MI_REFERENCIA, MI_FUENTE_RECURSO;
        EXIT WHEN MI_RS%NOTFOUND;
            MI_REGISTRO:= REPLACE(REPLACE(TO_CLOB( 
                          MI_TIPO_CPTE            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_COMPROBANTE          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_CONSECUTIVO          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_CUENTA               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_TIPO_CPTE_AFECT      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_CMPTE_AFECTADO       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_CONSECUTIVOPPTO      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_VALOR_DEBITO         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_VALOR_CREDITO        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_CENTRO_COSTO         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_TERCERO              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_AUXILIAR             ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_REFERENCIA           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_FUENTE_RECURSO       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          PCK_DATOS.GL_SEPARADOR_REG),
                            CHR(10),''),CHR(13),'')  ;
            MI_RTA := MI_RTA || MI_REGISTRO;
    END LOOP; 
    CLOSE MI_RS;
    
    /*
    * DATOS DEL PLAN A REVERSAR
    */
    
    MI_CONSULTA := 'SELECT PP.CODIGO, PP.NOMBRE, PP.TIPOVIGENCIA, PP.VIGENCIA, RESERVADECAJA, RESERVADEAPROPIACION
                FROM PLAN_PRESUPUESTAL PP
                WHERE PP.COMPANIA = ''' || UN_COMPANIA || '''
                AND PP.ANO = ' || MI_ANOACTUAL;

    -- Variable auxiliar
    MI_LIKES := '';
    
    BEGIN
        SELECT ' AND ( ' ||
           LISTAGG('PP.CODIGO LIKE ''' || DIGITOSCAMBIO || '%''', ' OR ')
           WITHIN GROUP (ORDER BY DIGITOSCAMBIO)|| ' )'
                INTO MI_LIKES
        FROM CONFIG_CIERRE_PPTAL
        WHERE COMPANIA   = UN_COMPANIA
          AND TIPOCIERRE = MI_TIPOCIERRE
          AND GENERAR    <> 0;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_LIKES := '';
    END;
    
    IF MI_TIPOCIERRE = 'COF' THEN
        BEGIN
                        SELECT SUBSTR(CODIGO,1,1)
                            INTO MI_DIGITOCAMBIO
                        FROM PLAN_PRESUPUESTAL
                        WHERE COMPANIA = UN_COMPANIA
                        AND ANO = MI_ANOACTUAL
                        AND COFINANCIADO <> 0
                        GROUP BY SUBSTR(CODIGO,1,1);
                    EXCEPTION WHEN NO_DATA_FOUND THEN 
                        MI_DIGITOCAMBIO := '';
                    END;
        MI_LIKES := ' AND ( PP.CODIGO LIKE ''' || MI_DIGITOCAMBIO || '%'' )';
    END IF;
      
    MI_CONSULTA := MI_CONSULTA || MI_LIKES;

    MI_RTA := MI_RTA || PCK_DATOS.GL_SEPARADOR_HOJ || 'Plan Presupuestal';
    MI_RTA := MI_RTA || PCK_DATOS.GL_SEPARADOR_REG ||
                                    'RUBRO'                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'NOMBRE'                 ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'TIPO VIGENCIA'          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'VIGENCIA EJECUCION'     ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'RESERVA APROPIACIÓN'    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'RESERVA DE CAJA'        ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    PCK_DATOS.GL_SEPARADOR_REG ; 
    MI_RUBRODUPLICADO:= ' ';                  
    OPEN MI_RS FOR MI_CONSULTA;
    LOOP
        FETCH MI_RS
        INTO MI_RUBRONUEVO, MI_NOMBRE, MI_TIPOVIGENCIA, MI_ANOVIGENCIA, MI_RESERVADEAPROPIACION, MI_RESERVADECAJA;
        EXIT WHEN MI_RS%NOTFOUND;
            MI_REGISTRO:= REPLACE(REPLACE(TO_CLOB( 
                          MI_RUBRONUEVO         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          TRIM(MI_NOMBRE)          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_TIPOVIGENCIA          ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_ANOVIGENCIA           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_RESERVADEAPROPIACION  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          MI_RESERVADECAJA         ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                          PCK_DATOS.GL_SEPARADOR_REG),
                            CHR(10),''),CHR(13),'')  ;
            MI_RTA := MI_RTA || MI_REGISTRO;            
            IF MI_RUBRODUPLICADO = MI_RUBRONUEVO THEN
                MI_RTA := REPLACE(MI_RTA, 
                                 'NO' || PCK_DATOS.GL_SEPARADOR_COL  || MI_RUBRONUEVO || PCK_DATOS.GL_SEPARADOR_COL,
                                 'SI' || PCK_DATOS.GL_SEPARADOR_COL  || MI_RUBRONUEVO || PCK_DATOS.GL_SEPARADOR_COL);
            END IF;
            MI_RUBRODUPLICADO:= MI_RUBRONUEVO;
    END LOOP; 
    CLOSE MI_RS;

    /*
    * DATOS DEL DETALLE A REVERSAR
    */
    
    MI_CONSULTA := 'SELECT PP.RESERVADECAJA, PP.RESERVADEAPROPIACION,
                           DCP.ANO, DCP.TIPO_CPTE, DCP.COMPROBANTE, DCP.CONSECUTIVO, DCP.CUENTA,
                           DCP.VALOR_DEBITO, DCP.VALOR_CREDITO, DCP.TERCERO, DCP.SUCURSAL,
                           DCP.AUXILIAR, DCP.REFERENCIA, DCP.FUENTE_RECURSO, DCP.ANO_AFECT,
                           DCP.TIPO_CPTE_AFECT, DCP.CMPTE_AFECTADO, DCP.CONSECUTIVOPPTO,
                           DCP.FUENTE_CUIPO, DCP.COD_PROD_CUIPO, DCP.CODIGO_CPC, DCP.CODIGO_BPIN,
                           DCP.CODIGO_CCPET, DCP.SECTOR, DCP.PROGRAMA, DCP.SUBPROGRAMA,
                           DCP.CODIGOUNIDADEJE, DCP.CODIGOCCPETREGA
                    FROM DETALLE_COMPROBANTE_PPTAL DCP
                    INNER JOIN PLAN_PRESUPUESTAL PP
                       ON DCP.COMPANIA = PP.COMPANIA
                      AND DCP.ANO = PP.ANO
                      AND DCP.CUENTA = PP.CODIGO
                    INNER JOIN TIPO_COMPROBPP T
                       ON DCP.COMPANIA = T.COMPANIA
                      AND DCP.TIPO_CPTE = T.CODIGO
                    WHERE DCP.COMPANIA = ''' || UN_COMPANIA || '''
                      AND DCP.ANO = ' || MI_ANOACTUAL || '
                    ' || MI_LIKES || '
                    AND T.CLASE IN (''DIS'',''RES'',''REO'')
                    ORDER BY DCP.CUENTA';
    
    MI_RTA := MI_RTA || PCK_DATOS.GL_SEPARADOR_HOJ || 'Detalle Comprobante';
    MI_RTA := MI_RTA || PCK_DATOS.GL_SEPARADOR_REG ||
                                    'RESERVADECAJA'             ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'RESERVADEAPROPIACION'      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'ANO'                       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'TIPO CPTE'                 ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'COMPROBANTE'               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CONSECUTIVO'               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CUENTA'                    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'VALOR DEBITO'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'VALOR CREDITO'             ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'TERCERO'                   ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'SUCURSAL'                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'AUXILIAR'                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'REFERENCIA'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'FUENTE_RECURSO'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'ANO AFECT'                 ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'TIPO CPTE AFECT'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CMPTE AFECTADO'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CONSECUTIVOPPTO'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'FUENTE CUIPO'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'COD_PROD_CUIPO'            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGO_CPC'                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGO_BPIN'               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGO_CCPET'              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'SECTOR'                    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'PROGRAMA'                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'SUBPROGRAMA'               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGOUNIDADEJE'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    'CODIGOCCPETREGA'           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    PCK_DATOS.GL_SEPARADOR_REG ;    
    <<DETALLETOTAL>>
    OPEN MI_RS FOR MI_CONSULTA;
    LOOP
        FETCH MI_RS
        INTO MI_RESERVADECAJA, MI_RESERVADEAPROPIACION,
                MI_ANO, MI_TIPO_CPTE, MI_COMPROBANTE, MI_CONSECUTIVO, MI_CUENTA, MI_VALOR_DEBITO, MI_VALOR_CREDITO, MI_TERCERO, MI_SUCURSAL, 
                MI_AUXILIAR, MI_REFERENCIA, MI_FUENTE_RECURSO, MI_ANO_AFECT, MI_TIPO_CPTE_AFECT, MI_CMPTE_AFECTADO, MI_CONSECUTIVOPPTO, 
                MI_FUENTE_CUIPO, MI_COD_PROD_CUIPO, MI_CODIGO_CPC, MI_CODIGO_BPIN, MI_CODIGO_CCPET, MI_SECTOR, MI_PROGRAMA, MI_SUBPROGRAMA, 
                MI_CODIGOUNIDADEJE, MI_CODIGOCCPETREGA;
        EXIT WHEN MI_RS%NOTFOUND;
        MI_REGISTRO:= REPLACE(REPLACE(TO_CLOB( 
                                    MI_RESERVADECAJA             ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_RESERVADEAPROPIACION      ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_ANO                       ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_TIPO_CPTE                 ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_COMPROBANTE               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_CONSECUTIVO               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_CUENTA                    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_VALOR_DEBITO              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_VALOR_CREDITO             ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_TERCERO                   ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_SUCURSAL                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_AUXILIAR                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_REFERENCIA                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_FUENTE_RECURSO            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_ANO_AFECT                 ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_TIPO_CPTE_AFECT           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_CMPTE_AFECTADO            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_CONSECUTIVOPPTO           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_FUENTE_CUIPO              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_COD_PROD_CUIPO            ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_CODIGO_CPC                ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_CODIGO_BPIN               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_CODIGO_CCPET              ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_SECTOR                    ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_PROGRAMA                  ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_SUBPROGRAMA               ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_CODIGOUNIDADEJE           ||  PCK_DATOS.GL_SEPARADOR_COL  ||
                                    MI_CODIGOCCPETREGA           ||  PCK_DATOS.GL_SEPARADOR_COL  ||                    
                      PCK_DATOS.GL_SEPARADOR_REG),
                        CHR(10),''),CHR(13),'')  ;
        MI_RTA := MI_RTA || MI_REGISTRO;    
    END LOOP DETALLETOTAL;
    CLOSE MI_RS;    
    
    RETURN MI_RTA;
END FC_VALIDARREVERSARCIE;


END PCK_PRESUPUESTO_CIE;