create or replace PACKAGE BODY PCK_PREDIAL_COM4 AS

--1
FUNCTION FC_REGISTROPAGOCUOTAINICIAL
/*
    NAME              : FC_REGISTROPAGOCUOTAINICIAL  --> EN ACCESS registrar_Click
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS 
    DATE MIGRADOR     : 10/02/2017
    TIME              : 02:15 PM 
    SOURCE MODULE     : PredialP2017.01.06VB.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : REALIZA EL PROCESO DE REGISTRO QUE SE EJECUTA EN EL EVENTO
                        DEL BOTON REGISTRAR EN EL FORMULARIO PREDIAL_REGISCUOTAANTER.                        
    @NAME             : getRegistroPagoCuotaInicial
    @METHOD           : GET
*/
(
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUMEROORDEN      IN PCK_SUBTIPOS.TI_NUMORDEN,
    UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO,
    UN_OBSERVACION      IN VARCHAR2 ,
    UN_FECHACORTE       IN DATE,
    UN_CODPREDIO        IN VARCHAR2,
    UN_CODIGOBANCO      IN VARCHAR2,
    UN_NROACUERDO       IN VARCHAR2,
    UN_NRORECIBO        IN PCK_SUBTIPOS.TI_DOCNUM,
    UN_TOTALPAGADO      IN PCK_SUBTIPOS.TI_DOBLE,
    UN_NROCUOTA         IN PCK_SUBTIPOS.TI_ENTERO,
    UN_TRPCOD           IN VARCHAR2
)
  RETURN VARCHAR2
AS
    MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_OBS_PAGOS        PCK_SUBTIPOS.TI_CAMPOS;
    MI_FECHACORTE       DATE;
    MI_PAGO             VARCHAR2(50 CHAR);
    MI_ANULADO          PCK_SUBTIPOS.TI_LOGICO;
    MI_PAG_BANPAG       VARCHAR2(50 CHAR);
    MI_PREFECPAG        DATE;
    MI_PAQUETEPAG       VARCHAR2(50 CHAR);
    MI_MSG              PCK_SUBTIPOS.TI_CAMPOS := ' ';
    MI_CODIGOBANCO      VARCHAR2(50 CHAR);
    MI_CUOTASCANCELADAS PCK_SUBTIPOS.TI_ENTERO;
    MI_PREDIOAGRU       VARCHAR2(50 CHAR);
    MI_CUOTAS           PCK_SUBTIPOS.TI_ENTERO;
    MI_CANTIDADCUOTAS   PCK_SUBTIPOS.TI_ENTERO;
    MI_TOTAL_ACUERDO    PCK_SUBTIPOS.TI_DOBLE;
    MI_PAGO_ACUERDO     PCK_SUBTIPOS.TI_DOBLE;
    MI_FACTURA_ACUERDO  PCK_SUBTIPOS.TI_DOBLE;
    MI_PREFEC           DATE;
    MI_DOCNUM           VARCHAR2(50 CHAR);
    MI_FECHAPAGO        DATE;
    MI_INDPAGO_ACPAG    PCK_SUBTIPOS.TI_LOGICO;
    MI_TOTAL            PCK_SUBTIPOS.TI_DOBLE;
    MI_CAPITAL          PCK_SUBTIPOS.TI_DOBLE;
    MI_INTERESES        PCK_SUBTIPOS.TI_DOBLE;
    MI_INTERES_ACUERDO  PCK_SUBTIPOS.TI_DOBLE;
    MI_INTERES_RECARGO  PCK_SUBTIPOS.TI_DOBLE;
    MI_PREANO           PCK_SUBTIPOS.TI_ANIO;
    MI_PREANOI          PCK_SUBTIPOS.TI_ANIO;
    MI_PAG_BAN          VARCHAR2(50 CHAR);
    MI_RECARGO          PCK_SUBTIPOS.TI_DOBLE;
    MI_BANCO            VARCHAR2(50 CHAR);
    MI_FECPAG           DATE;
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
BEGIN
    MI_OBS_PAGOS:= UN_OBSERVACION ||' Pago de cuotas ya Canceladas';
    MI_CODIGOBANCO:= UN_CODIGOBANCO;

    IF    UN_FECHACORTE  IS NULL 
       OR UN_CODPREDIO   IS NULL  
       OR UN_CODIGOBANCO IS NULL 
       OR UN_NROACUERDO  IS NULL  
       OR UN_NRORECIBO   IS NULL 
       OR UN_TOTALPAGADO IS NULL 
       OR UN_NROCUOTA    IS NULL 
       OR UN_OBSERVACION IS NULL 
       THEN 
         BEGIN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
             PCK_ERR_MSG.RAISE_WITH_MSG(
                         UN_EXC_COD    => SQLCODE
                        ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REGISPAGOFALTA
                       );
         END;
    ELSE
        IF UN_TRPCOD IS NULL THEN
            BEGIN 
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
              PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE
                         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REGPAGOSINTARI
                       );
            END;
        END IF;
    END IF;
    MI_FECHACORTE := UN_FECHACORTE;  

    --' Abril 15/2010 Se debe consultar en el sistema si la factura existe  y en que estado se encuentra, debido a que se estan registrando pagos de facturas del sistema por esta opción y la factura no es actualizada.
    --' Si la factura corresponde a una generada por el sistema esta debe ser recaudada por la opción de menú correspondiente.
    BEGIN 
      SELECT  PAGO
             ,ANULADO
             ,PAG_BANPAG
             ,PREFECPAG
             ,PAQUETEPAG
        INTO  MI_PAGO
             ,MI_ANULADO
             ,MI_PAG_BANPAG
             ,MI_PREFECPAG
             ,MI_PAQUETEPAG  
        FROM IP_RECIBOS_DE_PAGO  
       WHERE COMPANIA     = UN_COMPANIA 
         AND NUMERO_ORDEN = UN_NUMEROORDEN
         AND DOCNUM       = UN_NRORECIBO; 
    EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_PAGO       := NULL;
      MI_ANULADO    := NULL;
      MI_PAG_BANPAG := NULL;
      MI_PREFECPAG  := NULL;
      MI_PAQUETEPAG := NULL;
    END;

    IF MI_PAGO IS NOT NULL THEN
        -- 'Evaluar en el caso de encontrar el recibo ya recaudado
        IF MI_PAGO NOT IN (0) THEN
            IF MI_PREFECPAG <> MI_FECHACORTE THEN
              MI_MSG := 'El recibo que se esta relacionado pertenece al sistema y se encuentra cancelado pero la fecha que esta ingresado difiere de la registrada en el pago de la factura. El sistema tomará la registrada para la factura.';
              MI_FECHACORTE:= MI_PREFECPAG;
            END IF;
            IF MI_PAG_BANPAG <> UN_CODIGOBANCO THEN
              MI_MSG := MI_MSG || 'El recibo que esta relacionado pertenece al sistema y se encuentra cancelado pero el codigo del banco que esta ingresado difiere de la registrada en el pago de la factura. El sistema tomará la registrada para la factura';
              MI_CODIGOBANCO:= MI_PAG_BANPAG;
            END IF;
        END IF;
        --'Evaluar cuando se encuentra el recibo pero esta anulado
        IF MI_ANULADO NOT IN (0) THEN
            BEGIN 
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                PCK_ERR_MSG.RAISE_WITH_MSG(
                           UN_EXC_COD    => SQLCODE
                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBORELANULA
                         );
            END;
        END IF;
        --'Evaluar cuando se encuentra el recibo y esta activo
        IF MI_ANULADO IN (0) 
           AND MI_PAGO IN (0) THEN
           BEGIN 
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
               PCK_ERR_MSG.RAISE_WITH_MSG(
                           UN_EXC_COD    => SQLCODE
                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECRELACIONADO
                          );
           END;
        END IF;
    END IF;

    BEGIN 
         SELECT  COUNT(PAGADO) AS CANT
                ,PREDIO 
           INTO MI_CUOTASCANCELADAS
                ,MI_PREDIOAGRU 
           FROM IP_FACTURADOSACUERDOS 
          WHERE COMPANIA      = UN_COMPANIA
            AND NUMERO_ORDEN  = UN_NUMEROORDEN
            AND PREDIO        = UN_CODPREDIO 
            AND CODIGOACUERDO = UN_NROACUERDO 
            AND PAGADO        NOT IN (0) 
          GROUP BY PREDIO;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_CUOTASCANCELADAS := 0;
        MI_PREDIOAGRU  := 0;
    END;

    BEGIN 
        SELECT COUNT(CUOTA) CUOTAS
               ,PREDIO 
          INTO MI_CUOTAS
               ,MI_PREDIOAGRU      
          FROM IP_FACTURADOSACUERDOS 
         WHERE COMPANIA      = UN_COMPANIA
           AND NUMERO_ORDEN  = UN_NUMEROORDEN 
           AND PREDIO        = UN_CODPREDIO  
           AND CODIGOACUERDO = UN_NROACUERDO 
        GROUP BY PREDIO ;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_CUOTAS     := 0;
        MI_PREDIOAGRU := NULL;
    END;
    MI_CANTIDADCUOTAS := MI_CUOTAS;
    BEGIN 
      SELECT  TOTAL_ACUERDO
             ,PAGO_ACUERDO
             ,FACTURA_ACUERDO
        INTO  MI_TOTAL_ACUERDO
             ,MI_PAGO_ACUERDO
             ,MI_FACTURA_ACUERDO 
        FROM IP_USUARIOS_PREDIAL 
       WHERE COMPANIA     = UN_COMPANIA
         AND NUMERO_ORDEN = UN_NUMEROORDEN 
         AND CODIGO       = UN_CODPREDIO;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
       MI_TOTAL_ACUERDO   := 0;
       MI_PAGO_ACUERDO    := NULL;
       MI_FACTURA_ACUERDO := NULL;
    END;
 /*   BEGIN 
      SELECT   PREFEC
             , DOCNUM
             , FECHAPAGO
             , INDPAGO_ACPAG
        INTO   MI_PREFEC
             , MI_DOCNUM
             , MI_FECHAPAGO
             , MI_INDPAGO_ACPAG     
       FROM IP_FACTURADOS 
       WHERE COMPANIA     = UN_COMPANIA
         AND NUMERO_ORDEN = UN_NUMEROORDEN 
         AND CODIGO       = UN_CODPREDIO  
         AND INDPAGO_ACPAG NOT IN (0);
    EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_PREFEC         := NULL;
      MI_DOCNUM         := NULL;
      MI_FECHAPAGO      := NULL; 
      MI_INDPAGO_ACPAG  := NULL; 
    END;*/
    IF MI_CUOTASCANCELADAS <> MI_CANTIDADCUOTAS THEN
        BEGIN 
            SELECT   TOTAL
                   , CAPITAL
                   , INTERESES
                   , INTERES_ACUERDO
                   , INTERES_RECARGO
                   , PREANO
                   , PREANOI
                   , FECHAPAGO
                   , PAG_BAN 
              INTO   MI_TOTAL
                   , MI_CAPITAL
                   , MI_INTERESES
                   , MI_INTERES_ACUERDO
                   , MI_INTERES_RECARGO
                   , MI_PREANO
                   , MI_PREANOI
                   , MI_FECHAPAGO
                   , MI_PAG_BAN 
              FROM IP_FACTURADOSACUERDOS 
             WHERE COMPANIA      = UN_COMPANIA
               AND CODIGOACUERDO = UN_NROACUERDO  
               AND PREDIO        = UN_CODPREDIO  
               AND CUOTA         = UN_NROCUOTA ;
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_TOTAL           := NULL;
            MI_CAPITAL         := NULL;
            MI_INTERESES       := NULL;
            MI_INTERES_ACUERDO := NULL;
            MI_INTERES_RECARGO := NULL;
            MI_PREANO          := NULL;
            MI_PREANOI         := NULL;
            MI_FECHAPAGO       := NULL;
            MI_PAG_BAN         := NULL;
        END;
        IF MI_INTERES_RECARGO IS NOT NULL THEN
           MI_RECARGO := MI_INTERES_RECARGO;
           MI_BANCO   := NVL(MI_PAG_BAN, UN_CODIGOBANCO);
           MI_FECPAG  := NVL(MI_FECHAPAGO, MI_FECHACORTE);
        END IF;

        MI_TOTAL :=   MI_CAPITAL 
                    + MI_INTERESES 
                    + MI_INTERES_ACUERDO 
                    + NVL(MI_INTERES_RECARGO, 0);

        IF MI_TOTAL <> UN_TOTALPAGADO THEN
          RETURN 'DIALOGO,'||TO_CHAR(MI_FECHACORTE,'DD/MM/YYYY')||','||MI_OBS_PAGOS||','||MI_MSG;
        ELSE 
            MI_TABLA := 'IP_FACTURADOSACUERDOS';
            MI_CAMPOS := '  DOCNUM          = '''||UN_NRORECIBO||'''
                           , PAGADO          = -1
                           , INTERES_RECARGO = 0
                           , PAG_BAN         = '''||UN_CODIGOBANCO||'''
                           , FECHAPAGO       = TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY HH24:mi:ss'')
                           , TOTAL           = '||MI_TOTAL||'
                           , MODIFIED_BY     = '''||UN_USUARIO||'''
                           , DATE_MODIFIED   = SYSDATE';
            MI_CONDICION:= '    COMPANIA      = '''||UN_COMPANIA||'''
                            AND CODIGOACUERDO = '''||UN_NROACUERDO||''' 
                            AND PREDIO        = '''||UN_CODPREDIO||''' 
                            AND CUOTA         = '||UN_NROCUOTA;
            BEGIN 
              BEGIN 
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                             UN_TABLA     => MI_TABLA
                                            ,UN_ACCION    => 'M' 
                                            ,UN_CAMPOS    => MI_CAMPOS
                                            ,UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE := 'UN_NROACUERDO';
                MI_REEMPLAZOS(0).VALOR := UN_NROACUERDO;
                MI_REEMPLAZOS(1).CLAVE := 'UN_PREDIO';
                MI_REEMPLAZOS(1).VALOR := UN_CODPREDIO;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE
                         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTINDPAGAFACT
                         ,UN_TABLAERROR => MI_TABLA
                         ,UN_REEMPLAZOS => MI_REEMPLAZOS 
                        );
            END;
            PCK_PREDIAL.PR_AUDITORIA(
                        UN_COMPANIA    => UN_COMPANIA
                       ,UN_CODMOD      => UN_CODPREDIO
                       ,UN_OPEMOD      => UN_USUARIO
                       ,UN_CCOMOD      => '400'
                       ,UN_VANMOD      => MI_RECARGO
                       ,UN_VNUMOD      => MI_RECARGO
                       ,UN_DESCRIPCION => 'Cambio del interes de recargo de la cuota '
                                         ||UN_NROCUOTA
                                         ||' del acuerdo '
                                         ||UN_NROACUERDO); 

            MI_TABLA   := 'IP_USUARIOS_PREDIAL';
            MI_CAMPOS  := '  PAGO_ANO              = '||MI_PREANO||
                          ', PAG_FEC               = TO_DATE ('''||UN_FECHACORTE||''' , ''DD/MM/YYYY'')
                           , PAG_VAL               = '||UN_TOTALPAGADO||'
                           , PAG_BAN               = '''||UN_CODIGOBANCO||'''
                           , FACTURA_ACUERDO       = '''||UN_NRORECIBO||'''
                           , TOTAL_ACUERDO         = '||UN_TOTALPAGADO ||'
                           , PAGO_ACUERDO          = -1  '||       
                          ', OBSERVACIONES_ACUERDO = '''|| MI_OBS_PAGOS ||'''
                           , MODIFIED_BY           = '''||UN_USUARIO||'''
                           , DATE_MODIFIED         = SYSDATE';
            MI_CONDICION := '      COMPANIA     = '''||UN_COMPANIA||''' '
                           ||' AND CODIGO       = '''||UN_CODPREDIO||''' '
                           ||' AND NUMERO_ORDEN = '''||UN_NUMEROORDEN||''' ';
            BEGIN  
              BEGIN 
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                             UN_TABLA     => MI_TABLA
                                            ,UN_ACCION    => 'M' 
                                            ,UN_CAMPOS    => MI_CAMPOS
                                            ,UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE := 'UN_NROACUERDO';
                MI_REEMPLAZOS(0).VALOR := UN_NROACUERDO;
                MI_REEMPLAZOS(1).CLAVE := 'UN_PREDIO';
                MI_REEMPLAZOS(1).VALOR := UN_CODPREDIO;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE
                         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUPAGADOACUE
                         ,UN_TABLAERROR => MI_TABLA
                         ,UN_REEMPLAZOS => MI_REEMPLAZOS
                        );
            END;

            PCK_PREDIAL.PR_AUDITORIA(
                        UN_COMPANIA    => UN_COMPANIA
                       ,UN_CODMOD      => UN_CODPREDIO
                       ,UN_OPEMOD      => UN_USUARIO
                       ,UN_CCOMOD      => '042'
                       ,UN_VANMOD      => MI_BANCO
                       ,UN_VNUMOD      => UN_CODIGOBANCO
                       ,UN_DESCRIPCION => 'Cambio de banco donde se realizó el pago de la cuota '
                                          ||UN_NROCUOTA
                                          ||' del acuerdo '
                                          ||UN_NROACUERDO); 
			IF PCK_DATOS.GL_RTA NOT IN(0) THEN
				MI_MSG := MI_MSG || ' Proceso Realizado Satisfactoriamente';
			ELSE
				MI_MSG := MI_MSG || ' El proceso No se realizó Correctamente. Por favor Verificar';
			END IF;
        END IF;
    ELSE
        MI_TABLA    := 'IP_FACTURADOS';
        MI_CAMPOS   := ' PREFEC        = TO_DATE('''||UN_FECHACORTE ||''',''DD/MM/YYYY HH24:mi:ss'')
                       , DOCNUM        = '''||UN_NRORECIBO ||'''
                       , PAG_BAN       = '''||UN_CODIGOBANCO ||'''
                       , PAGADO        = -1
                       , FECHAPAGO     = TO_DATE('''||UN_FECHACORTE ||''',''DD/MM/YYYY HH24:mi:ss'')
                       , INDPAGO_ACPAG = 0  
                       , MODIFIED_BY   = '''||UN_USUARIO||'''
                       , DATE_MODIFIED = SYSDATE';
        MI_CONDICION := '       COMPANIA      = '''||UN_COMPANIA||''' '
                        ||' AND NUMERO_ORDEN  = '''||UN_NUMEROORDEN||''' '
                        ||' AND CODIGO        = '''||UN_CODPREDIO||''' '
                        ||' AND PAGADO        IN(0) '
                        ||' AND INDPAGO_ACPAG NOT IN(0) ';

        BEGIN 
            BEGIN 
              PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                           UN_TABLA     => MI_TABLA
                                          ,UN_ACCION    => 'M' 
                                          ,UN_CAMPOS    => MI_CAMPOS
                                          ,UN_CONDICION =>  MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
              MI_REEMPLAZOS(0).CLAVE := 'UN_NROACUERDO';
              MI_REEMPLAZOS(0).VALOR := UN_NROACUERDO;
              MI_REEMPLAZOS(1).CLAVE := 'UN_PREDIO';
              MI_REEMPLAZOS(1).VALOR := UN_CODPREDIO;
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTUINDIPAGADO
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;
        PCK_PREDIAL.PR_AUDITORIA(
                    UN_COMPANIA    => UN_COMPANIA
                   ,UN_CODMOD      => UN_CODPREDIO
                   ,UN_OPEMOD      => UN_USUARIO
                   ,UN_CCOMOD      => '042'
                   ,UN_VANMOD      => MI_FECPAG
                   ,UN_VNUMOD      => MI_FECHACORTE
                   ,UN_DESCRIPCION => 'CAMBIO DE FECHA EN LA QUE SE REALIZO EL PAGO DE LA CUOTA N° '
                                      ||UN_NROCUOTA
                                      ||' DEL ACUERDO '
                                      ||UN_NROACUERDO);    

        MI_TABLA     := 'IP_ACUERDOS';
        MI_CAMPOS    := '  CANCELADO      = -1  
                        , MODIFIED_BY     = '''||UN_USUARIO||'''
                        , DATE_MODIFIED   = SYSDATE';
        MI_CONDICION := '       COMPANIA     = '''||UN_COMPANIA||''' '
                       ||' AND CODIGOACUERDO = '''||UN_NROACUERDO||''' '
                       ||' AND PREDIO        = '''||UN_CODPREDIO||''' ';

        BEGIN
            BEGIN 
              PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                           UN_TABLA     => MI_TABLA
                                          ,UN_ACCION    => 'M' 
                                          ,UN_CAMPOS    => MI_CAMPOS
                                          ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE := 'UN_NROACUERDO';
                MI_REEMPLAZOS(0).VALOR := UN_NROACUERDO;
                MI_REEMPLAZOS(1).CLAVE := 'UN_PREDIO';
                MI_REEMPLAZOS(1).VALOR := UN_CODPREDIO;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTINDCANCELAD
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );  
        END;
        IF PCK_DATOS.GL_RTA NOT IN(0) THEN
            MI_MSG := MI_MSG || ' Proceso Realizado Satisfactoriamente';
        ELSE
            MI_MSG := MI_MSG || ' El proceso No se realizó Correctamente. Por favor Verificar';
        END IF;
    END IF;
    MI_TABLA   := 'IP_BITPAGOSANOSANT';
    MI_CAMPOS  := ' COMPANIA
                    ,NUMERO_ORDEN 
                    ,FECHAACCESO
                    ,USUARIO
                    ,NUMCOM
                    ,PAG_FEC
                    ,PAG_BAN
                    ,CODPRED
                    ,ANOFIN
                    ,PAG_VAL
                    ,CREATED_BY
                    ,DATE_CREATED';
    MI_VALORES :=  ' '''||UN_COMPANIA||'''  
                   , '''||UN_NUMEROORDEN||'''   
                   , SYSDATE
                   , '''||UN_USUARIO||'''
                   , '''||UN_NRORECIBO||'''
                   , TO_DATE('''||UN_FECHACORTE||''', ''DD/MM/YYYY HH24:mi:ss'')
                   , '''||UN_CODIGOBANCO||'''
                   , '''||UN_CODPREDIO||'''
                   , '||MI_PREANOI||'
                   , '||UN_TOTALPAGADO||'
                   , '''||UN_USUARIO||'''
                   , SYSDATE';
    --'Mayo 16/2006 - Se los datos que se afectaran al realizar el pago por esta opción a fin de tener un registro de que usuarios y que datos se modificaron  
    BEGIN
        BEGIN 
          PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                       UN_TABLA   => MI_TABLA
                                      ,UN_ACCION  => 'I' 
                                      ,UN_CAMPOS  => MI_CAMPOS
                                      ,UN_VALORES => MI_VALORES); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_BITPAGOANOSANT
                   ,UN_TABLAERROR => MI_TABLA
                  );  
    END;
    MI_MSG := MI_MSG || CASE WHEN PCK_DATOS.GL_RTA > 0 
                             THEN ' Pago registrado con éxito'
                             ELSE ' '
                        END;
   RETURN MI_MSG;

END FC_REGISTROPAGOCUOTAINICIAL;

--2
FUNCTION FC_REGISTROPAGOCUOTAACEPTAR
/*
    NAME              : FC_REGISTROPAGOCUOTAACEPTAR  --> EN ACCESS registrar_Click
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS 
    DATE MIGRADOR     : 10/02/2017
    TIME              : 02:15 PM 
    SOURCE MODULE     : PredialP2017.01.06VB.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : REALIZA EL PROCESO DE REGISTRO QUE SE EJECUTA EN EL EVENTO
                        DEL BOTON ACEPTAR EN EL DIALOGO DEL FORMULARIO PREDIAL_REGISCUOTAANTER.                        
    @NAME             : getRegistroPagoCuotaInicial
    @METHOD           : GET
*/
(
  UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_NRORECIBO        IN VARCHAR2,
  UN_CODBANCO         IN VARCHAR2,
  UN_FECHACORTE       IN DATE ,
  UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO ,
  UN_NROACUERDO       IN VARCHAR2,
  UN_CODPREDIO        IN PCK_SUBTIPOS.TI_CODPREDIO ,
  UN_NROCUOTA         IN PCK_SUBTIPOS.TI_ENTERO ,
  UN_TOTALPAGADO      IN PCK_SUBTIPOS.TI_DOBLE ,
  UN_OBSPAGOS         IN VARCHAR2
)  
RETURN VARCHAR2 
AS
  MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
  MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_TOTAL            PCK_SUBTIPOS.TI_DOBLE;
  MI_CAPITAL          PCK_SUBTIPOS.TI_DOBLE;
  MI_INTERESES        PCK_SUBTIPOS.TI_DOBLE;
  MI_INTERES_ACUERDO  PCK_SUBTIPOS.TI_DOBLE;
  MI_INTERES_RECARGO  PCK_SUBTIPOS.TI_DOBLE;
  MI_PREANOI          PCK_SUBTIPOS.TI_ANIO;
  MI_FECHAPAGO        DATE;
  MI_PAG_BAN          IP_FACTURADOSACUERDOS.PAG_BAN%TYPE;
  MI_RESULTADO        PCK_SUBTIPOS.TI_CAMPOS;
BEGIN
  BEGIN 
      SELECT   CAPITAL
             , INTERESES
             , INTERES_ACUERDO
             , INTERES_RECARGO
             , PREANOI
             , FECHAPAGO
             , PAG_BAN 
        INTO   MI_CAPITAL
             , MI_INTERESES
             , MI_INTERES_ACUERDO
             , MI_INTERES_RECARGO
             , MI_PREANOI
             , MI_FECHAPAGO
             , MI_PAG_BAN 
        FROM IP_FACTURADOSACUERDOS 
       WHERE COMPANIA      = UN_COMPANIA
         AND CODIGOACUERDO = UN_NROACUERDO  
         AND PREDIO        = UN_CODPREDIO  
         AND CUOTA         = UN_NROCUOTA ;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_TOTAL           := NULL;
      MI_CAPITAL         := NULL;
      MI_INTERESES       := NULL;
      MI_INTERES_ACUERDO := NULL;
      MI_INTERES_RECARGO := NULL;
      MI_PREANOI         := NULL;
      MI_FECHAPAGO       := NULL;
      MI_PAG_BAN         := NULL;
    END;
    IF MI_PAG_BAN IS NULL THEN
       MI_PAG_BAN    := NVL(MI_PAG_BAN, UN_CODBANCO);
       MI_FECHAPAGO  := NVL(MI_FECHAPAGO, UN_FECHACORTE);
    END IF;

    MI_TOTAL :=   MI_CAPITAL 
                + MI_INTERESES 
                + MI_INTERES_ACUERDO 
                + NVL(MI_INTERES_RECARGO, 0);

    MI_TABLA := 'IP_FACTURADOSACUERDOS';
    MI_CAMPOS := '  DOCNUM          = '''||UN_NRORECIBO||'''
                   , PAGADO          = -1
                   , INTERES_RECARGO = 0
                   , PAG_BAN         = '''||UN_CODBANCO||'''
                   , FECHAPAGO       = TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY HH24:mi:ss'')
                   , TOTAL           = '||MI_TOTAL||'
                   , MODIFIED_BY     = '''||UN_USUARIO||'''
                   , DATE_MODIFIED   = SYSDATE';
    MI_CONDICION:= '    COMPANIA      = '''||UN_COMPANIA||'''
                    AND CODIGOACUERDO = '''||UN_NROACUERDO||''' 
                    AND PREDIO        = '''||UN_CODPREDIO||''' 
                    AND CUOTA         = '||UN_NROCUOTA;
    BEGIN 
      PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                   UN_TABLA     => MI_TABLA
                                  ,UN_ACCION    => 'M' 
                                  ,UN_CAMPOS    => MI_CAMPOS
                                  ,UN_CONDICION => MI_CONDICION);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
    END;
    PCK_PREDIAL.PR_AUDITORIA(
                UN_COMPANIA    => UN_COMPANIA
               ,UN_CODMOD      => UN_CODPREDIO
               ,UN_OPEMOD      => UN_USUARIO
               ,UN_CCOMOD      => '400'
               ,UN_VANMOD      => MI_INTERES_RECARGO
               ,UN_VNUMOD      => MI_INTERES_RECARGO
               ,UN_DESCRIPCION => 'Cambio del interes de recargo de la cuota '
                                 ||UN_NROCUOTA
                                 ||' del acuerdo '
                                 ||UN_NROACUERDO);

    MI_TABLA   := 'IP_USUARIOS_PREDIAL';
    MI_CAMPOS  := '  FACTURA_ACUERDO       = '''||UN_NRORECIBO|| ''' '||
                  ', TOTAL_ACUERDO         = '||UN_TOTALPAGADO||
                  ', PAGO_ACUERDO          = -1
                   , OBSERVACIONES_ACUERDO = '''||UN_OBSPAGOS||'''
                   , MODIFIED_BY           = '''||UN_USUARIO|| '''
                   , DATE_MODIFIED         = SYSDATE';
    MI_CONDICION := '       CODIGO      = '''||UN_CODPREDIO||''' '
                   ||' AND COMPANIA     = '''||UN_COMPANIA||''' '
                   ||' AND NUMERO_ORDEN = '''||PCK_DATOS.FC_MODULOPREDIAL||''' ';
    BEGIN               
        BEGIN  
           PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                       UN_TABLA     => MI_TABLA
                                      ,UN_ACCION    => 'M' 
                                      ,UN_CAMPOS    => MI_CAMPOS
                                      ,UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
         PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE
               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REGISPAGOACEPT
               ,UN_TABLAERROR => MI_TABLA
              );          
    END;
    PCK_PREDIAL.PR_AUDITORIA(
                UN_COMPANIA    => UN_COMPANIA
               ,UN_CODMOD      => UN_CODPREDIO
               ,UN_OPEMOD      => UN_USUARIO
               ,UN_CCOMOD      => '042'
               ,UN_VANMOD      => MI_PAG_BAN
               ,UN_VNUMOD      => UN_CODBANCO
               ,UN_DESCRIPCION => 'Cambio de banco donde se realizo el pago de la cuota '
                                  ||UN_NROCUOTA
                                  ||' del acuerdo '
                                  ||UN_NROACUERDO); 

    IF PCK_DATOS.GL_RTA NOT IN (0) THEN
      PCK_PREDIAL.PR_AUDITORIA(
                UN_COMPANIA    => UN_COMPANIA
               ,UN_CODMOD      => UN_CODPREDIO
               ,UN_OPEMOD      => UN_USUARIO
               ,UN_CCOMOD      => '042'
               ,UN_VANMOD      => MI_FECHAPAGO
               ,UN_VNUMOD      => UN_FECHACORTE
               ,UN_DESCRIPCION => 'CAMBIO DE FECHA EN LA QUE SE REALIZO EL PAGO DE LA CUOTA N° '
                                  ||UN_NROCUOTA
                                  ||' DEL ACUERDO '
                                  ||UN_NROACUERDO
               ,UN_FECMOD      => SYSDATE + 1/86400
               ,UN_HORMOD      => SYSDATE + 1/86400);
    END IF;

    MI_TABLA   := 'IP_BITPAGOSANOSANT';
    MI_CAMPOS  := ' COMPANIA
                    ,NUMERO_ORDEN 
                    ,FECHAACCESO
                    ,USUARIO
                    ,NUMCOM
                    ,PAG_FEC
                    ,PAG_BAN
                    ,CODPRED
                    ,ANOFIN
                    ,PAG_VAL
                    ,CREATED_BY
                    ,DATE_CREATED';
    MI_VALORES :=  ' '''||UN_COMPANIA||'''  
                   , '''||PCK_DATOS.CONS_NUMERO_ORDEN_PREDIAL||'''   
                   , SYSDATE
                   , '''||UN_USUARIO||'''
                   , '''||UN_NRORECIBO||'''
                   , TO_DATE('''||UN_FECHACORTE||''', ''DD/MM/YYYY HH24:mi:ss'')
                   , '''||UN_CODBANCO||'''
                   , '''||UN_CODPREDIO||'''
                   , '||MI_PREANOI||'
                   , '||UN_TOTALPAGADO||'
                   , '''||UN_USUARIO||'''
                   , SYSDATE';

    BEGIN
      BEGIN 
        PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                     UN_TABLA   => MI_TABLA
                                    ,UN_ACCION  => 'I' 
                                    ,UN_CAMPOS  => MI_CAMPOS
                                    ,UN_VALORES => MI_VALORES); 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE
                 ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_BITPAGOANOSANT
                 ,UN_TABLAERROR => MI_TABLA
                );  
    END;   

    MI_RESULTADO := '-1';

  RETURN MI_RESULTADO;

END FC_REGISTROPAGOCUOTAACEPTAR;

FUNCTION FC_ANULARREGISTROPAGOPREDIAL
/*
    NAME              : FC_REGISTROPAGOCUOTAACEPTAR  --> EN ACCESS registrar_Click
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS 
    DATE MIGRADOR     : 10/02/2017
    TIME              : 02:15 PM 
    SOURCE MODULE     : PredialP2017.01.06VB.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : REALIZA EL PROCESO DE REGISTRO QUE SE EJECUTA EN EL EVENTO
                        DEL BOTON ACEPTAR EN EL DIALOGO DEL FORMULARIO PREDIAL_REGISCUOTAANTER.                        
    @NAME             : getAnularRegistroPago
    @METHOD           : GET
*/
(
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUMEROORDEN IN PCK_SUBTIPOS.TI_NUMORDEN, 
    UN_NUMFACTURA  IN PCK_SUBTIPOS.TI_DOCNUM, 
    UN_CODPREDIO   IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_PAGBAN      IN VARCHAR2, 
    UN_PAQUETE     IN VARCHAR2, 
    UN_FECHAPAGO   IN DATE , 
    UN_ACUERDO     IN IP_FACTURADOSACUERDOS.CODIGOACUERDO%TYPE, 
    UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO 
)RETURN VARCHAR2
AS 
  MI_TABLA       PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
  MI_MERGEUSING  PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_DOCNUM      VARCHAR2(100 CHAR);
  MI_RTA         VARCHAR2(100 CHAR);

BEGIN
    BEGIN  
      SELECT IP_PAGOSDOBLES.DOCNUM 
        INTO MI_DOCNUM  
        FROM IP_PAGOSDOBLES
       WHERE IP_PAGOSDOBLES.COMPANIA     = UN_COMPANIA
         AND IP_PAGOSDOBLES.NUMERO_ORDEN = UN_NUMEROORDEN
         AND IP_PAGOSDOBLES.DOCNUM       = UN_NUMFACTURA;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_DOCNUM:= NULL;
    END;
    IF MI_DOCNUM IS NOT NULL THEN
        MI_TABLA  := 'IP_PAGOSDOBLES'; 
        MI_CAMPOS := 'IP_PAGOSDOBLES.ANULADO = -1,
                      IP_PAGOSDOBLES.MODIFIED_BY = '''|| UN_USUARIO ||''',
                      IP_PAGOSDOBLES.DATE_MODIFIED = SYSDATE ';
        MI_CONDICION := '       IP_PAGOSDOBLES.COMPANIA   = '''||UN_COMPANIA||
                      ''' AND IP_PAGOSDOBLES.NUMERO_ORDEN = '''||UN_NUMEROORDEN||   
                      ''' AND IP_PAGOSDOBLES.DOCNUM       = '''||MI_DOCNUM||''' ';
        DECLARE
            MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
        BEGIN 
            BEGIN 
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                             UN_TABLA     => MI_TABLA
                                            ,UN_ACCION    => 'M' 
                                            ,UN_CAMPOS    => MI_CAMPOS
                                            ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE := 'UN_NUMFACTURA';
                MI_REEMPLAZOS(0).VALOR := UN_NUMFACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
            MI_TABLA := 'IP_RECIBOS_DE_PAGO';
            -- Anular recibos de pago
            MI_MERGEUSING := 'SELECT  IP_RECIBOS_DE_PAGO.COMPANIA
                                     ,IP_RECIBOS_DE_PAGO.PRECOD
                                     ,IP_RECIBOS_DE_PAGO.DOCNUM 
                                     ,0 PAGO
                                     ,-1 ANULADO
                                     ,'''||UN_USUARIO||''' ANULADO_POR
                                     ,TO_DATE(SYSDATE,''DD/MM/YYYY HH24:mi:ss'') FECHAANULACION
                                     ,'''||UN_USUARIO||''' MODIFIED_BY
                                     ,TO_DATE(SYSDATE,''DD/MM/YYYY HH24:mi:ss'') DATE_MODIFIED  
                              FROM IP_RECIBOS_DE_PAGO 
                                INNER JOIN IP_PAGOSDOBLES  
                                  ON  IP_RECIBOS_DE_PAGO.COMPANIA = IP_PAGOSDOBLES.COMPANIA  
                                  AND IP_RECIBOS_DE_PAGO.PRECOD   = IP_PAGOSDOBLES.PRECOD  
                                  AND IP_RECIBOS_DE_PAGO.DOCNUM   = IP_PAGOSDOBLES.DOCNUM
                             WHERE IP_RECIBOS_DE_PAGO.COMPANIA = '''||UN_COMPANIA||'''
                               AND IP_RECIBOS_DE_PAGO.DOCNUM   = '''||MI_DOCNUM||''' ';

            MI_MERGEENLACE:=   '     VISTA.COMPANIA  = TABLA.COMPANIA  
                               AND VISTA.PRECOD    = TABLA.PRECOD    
                               AND VISTA.DOCNUM    = TABLA.DOCNUM        ';

            MI_MERGEEXISTE:= ' UPDATE SET TABLA.PAGO            = VISTA.PAGO,
                                          TABLA.ANULADO         = VISTA.ANULADO,
                                          TABLA.ANULADO_POR     = VISTA.ANULADO_POR,
                                          TABLA.FECHAANULACION  = VISTA.FECHAANULACION, 
                                          TABLA.MODIFIED_BY     = VISTA.MODIFIED_BY,
                                          TABLA.DATE_MODIFIED   = VISTA.DATE_MODIFIED';

            BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                              UN_TABLA       => MI_TABLA 
                                             ,UN_ACCION      => 'MM'
                                             ,UN_MERGEUSING  => MI_MERGEUSING
                                             ,UN_MERGEENLACE => MI_MERGEENLACE
                                             ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                             );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
                MI_REEMPLAZOS(0).CLAVE := 'UN_NUMFACTURA';
                MI_REEMPLAZOS(0).VALOR := UN_NUMFACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;

            -- Anular pagos dobles
            MI_TABLA := 'IP_PAGOSDOBLES';
            MI_MERGEUSING:= 'SELECT 
                                  IP_PAGOSDOBLES.COMPANIA
                                 ,IP_PAGOSDOBLES.PRECOD
                                 ,IP_PAGOSDOBLES.DOCNUM 
                                 ,-1 ANULADO
                                 ,'''||UN_USUARIO||''' ANULADOPOR
                                 ,TO_DATE(SYSDATE,''DD/MM/YYYY HH24:mi:ss'') FECHAANULADO  
                             FROM IP_RECIBOS_DE_PAGO 
                               INNER JOIN IP_PAGOSDOBLES  
                                 ON  IP_RECIBOS_DE_PAGO.COMPANIA  = IP_PAGOSDOBLES.COMPANIA  
                                 AND IP_RECIBOS_DE_PAGO.PRECOD    = IP_PAGOSDOBLES.PRECOD  
                                 AND IP_RECIBOS_DE_PAGO.DOCNUM    = IP_PAGOSDOBLES.DOCNUM
                            WHERE IP_RECIBOS_DE_PAGO.COMPANIA = '''||UN_COMPANIA||'''
                              AND IP_RECIBOS_DE_PAGO.DOCNUM   = '''||UN_NUMFACTURA||''' ';

            MI_MERGEENLACE:=  '     VISTA.COMPANIA  = TABLA.COMPANIA  
                               AND VISTA.PRECOD    = TABLA.PRECOD    
                               AND VISTA.DOCNUM    = TABLA.DOCNUM';

            MI_MERGEEXISTE:= ' UPDATE SET TABLA.ANULADO     = VISTA.ANULADO,
                                         TABLA.ANULADOPOR   = VISTA.ANULADOPOR,
                                         TABLA.FECHAANULADO = VISTA.FECHAANULADO';
            BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                              UN_TABLA       => MI_TABLA 
                                             ,UN_ACCION      => 'MM'
                                             ,UN_MERGEUSING  => MI_MERGEUSING
                                             ,UN_MERGEENLACE => MI_MERGEENLACE
                                             ,UN_MERGEEXISTE => MI_MERGEEXISTE
                                             );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
                MI_REEMPLAZOS(0).CLAVE := 'UN_NUMFACTURA';
                MI_REEMPLAZOS(0).VALOR := UN_NUMFACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE
                         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ANULAR_REGIS_PAGO
                         ,UN_TABLAERROR => MI_TABLA
                         ,UN_REEMPLAZOS => MI_REEMPLAZOS 
                        );
        END;
        MI_RTA := '1';
    ELSE
      MI_RTA := PCK_PREDIAL.FC_REVERSARPAGO_RECIBO(
                            UN_COMPANIA    => UN_COMPANIA 
                           ,UN_NUM_FACTURA => UN_NUMFACTURA  
                           ,UN_COD_PREDIO  => UN_CODPREDIO  
                           ,UN_PAG_BAN     => UN_PAGBAN  
                           ,UN_PAQUETE     => UN_PAQUETE
                           ,UN_FECHAPAGO   => UN_FECHAPAGO 
                           ,UN_COD_ACUERDO => UN_ACUERDO  
                           ,UN_USUARIO     => UN_USUARIO);
    END IF;
   RETURN MI_RTA;   
END FC_ANULARREGISTROPAGOPREDIAL;

--4
FUNCTION FC_REACTIVARPREDIO
/*
    NAME              : FC_REACTIVARPREDIO  --> EN ACCESS CmdActivar_Click
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS 
    DATE MIGRADOR     : 10/02/2017
    TIME              : 05:15 PM 
    SOURCE MODULE     : PredialP2017.01.06VB.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : REALIZA EL PROCESO DE REGISTRO QUE SE EJECUTA EN EL EVENTO
                        DEL BOTON REACTIVAR DEL FORMULARIO REACTIVARPREDIO.                        
    @NAME             : getReactivarPredio
    @METHOD           : GET
*/
(
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_NIVEL           IN PCK_SUBTIPOS.TI_ENTERO, 
    UN_CODIGO          IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_NORESOLUCION    IN VARCHAR2,
    UN_FECRESOLUCION   IN DATE,
    UN_ELABRESOLUCION  IN VARCHAR2,
    UN_FIRMARESOLUCION IN VARCHAR2,
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO 
)RETURN VARCHAR2
AS 
  MI_TABLA       PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
  MI_OBSERVACION VARCHAR2(500 CHAR);
  MI_RTA         VARCHAR2(100 CHAR);
  MI_MOD         VARCHAR2(100 CHAR);
BEGIN
IF UN_NIVEL > 6 THEN
        IF UN_CODIGO IS NULL
           OR UN_NORESOLUCION IS NULL 
           OR UN_FECRESOLUCION IS NULL
           OR UN_ELABRESOLUCION IS NULL 
           OR UN_FIRMARESOLUCION IS NULL THEN
            BEGIN 
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE
                         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REACPREDIOFALTAN
                        );
            END;
        ELSE 
            MI_OBSERVACION:= 'Activación del predio con Resolución No. '
                              ||UN_NORESOLUCION
                              ||' del '||TO_CHAR(UN_FECRESOLUCION,'DD/MM/YYYY')
                              ||', elaborada por '||UN_ELABRESOLUCION
                              ||' y firmada por '||UN_FIRMARESOLUCION;
            MI_TABLA:= 'IP_USUARIOS_PREDIAL ';
            MI_CAMPOS := '  INDBORRADO       = 0
                          , CODIGO_NO_ACTIVO = 0
                          , FECHABORRADO     = NULL
                          , OBSERVACIO       = '''||MI_OBSERVACION||''' 
                          , MODIFIED_BY = '''||UN_USUARIO||'''
                          , DATE_MODIFIED = SYSDATE
                          ';
            MI_CONDICION := '         COMPANIA     = '''||UN_COMPANIA
                            ||''' AND CODIGO       = '''||UN_CODIGO ||'''  ';
            DECLARE 
                MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
            BEGIN
              BEGIN 
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                             UN_TABLA     => MI_TABLA
                                            ,UN_ACCION    => 'M' 
                                            ,UN_CAMPOS    => MI_CAMPOS
                                            ,UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE := 'CODIGO';
                MI_REEMPLAZOS(0).VALOR := UN_CODIGO;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE
                         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REACTPREDIOUPDAT
                         ,UN_TABLAERROR => MI_TABLA
                         ,UN_REEMPLAZOS => MI_REEMPLAZOS 
                        );
            END;
            IF PCK_DATOS.GL_RTA > 0 THEN
                BEGIN 
                  SELECT RMOCOD  
                    INTO MI_MOD 
                    FROM IP_MODICOD
                   WHERE RMOVAR = 'INGRESACTIVACION';
                EXCEPTION WHEN NO_DATA_FOUND THEN 
                  MI_MOD:=NULL;
                END;
                 IF MI_MOD IS NOT NULL THEN
                      PCK_PREDIAL.PR_AUDITORIA(
                                UN_COMPANIA => UN_COMPANIA
                               ,UN_CODMOD   => UN_CODIGO
                               ,UN_OPEMOD   => UN_USUARIO
                               ,UN_CCOMOD   => MI_MOD
                               ,UN_VANMOD   => '-'
                               ,UN_VNUMOD   => '-'
                               ,UN_DESCRIPCION => MI_OBSERVACION); 
                 END IF;
                 MI_RTA := 'Proceso finalizado satisfactoriamente';
            ELSE
                MI_RTA := 'El proceso no se realizó correctamente';
            END IF;
        END IF;
    ELSE
      BEGIN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE
                     ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REACPREDIOFALTAN
                    );
      END;
    END IF;
    RETURN MI_RTA;
END FC_REACTIVARPREDIO;

--5
FUNCTION FC_ANULARPREDIO
/*
    NAME              : FC_ANULARPREDIO  --> EN ACCESS CmdActivar_Click
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS 
    DATE MIGRADOR     : 13/02/2017
    TIME              : 09:15 AM 
    SOURCE MODULE     : PredialP2017.01.06VB.accdb
    MODIFIER          : YESIKA PAOLA BECERRA CASTRO
    DATE MODIFIED     : 22/06/2017
    TIME              : 
    DESCRIPTION       : REALIZA EL PROCESO DE REGISTRO QUE SE EJECUTA EN EL EVENTO
                        DEL BOTON ANULAR DEL FORMULARIO ANULARPREDIO.  / Se agregaron campos de auditoría al actualizar la tabla IP_USUARIOS_PREDIAL     

    @NAME             : getAnularPredio
    @METHOD           : GET
*/
(
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_NIVEL           IN PCK_SUBTIPOS.TI_ENTERO, 
    UN_CODIGO          IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_NORESOLUCION    IN VARCHAR2,
    UN_FECRESOLUCION   IN DATE,
    UN_ELABRESOLUCION  IN VARCHAR2,
    UN_FIRMARESOLUCION IN VARCHAR2,
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO 
)RETURN VARCHAR2
AS 
  MI_TABLA       PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
  MI_OBSERVACION VARCHAR2(500 CHAR):='NA';
  MI_RTA         VARCHAR2(100 CHAR):='SIN FINALIZAR';
  MI_MOD         VARCHAR2(100 CHAR);
BEGIN
    IF UN_NIVEL > 6 THEN
        IF UN_CODIGO IS NULL
           OR UN_NORESOLUCION IS NULL 
           OR UN_FECRESOLUCION IS NULL
           OR UN_ELABRESOLUCION IS NULL 
           OR UN_FIRMARESOLUCION IS NULL THEN
            BEGIN 
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE
                         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REACPREDIOFALTAN
                        );
            END;
        ELSE 
            MI_OBSERVACION:= 'Activación del predio' || ' '|| UN_CODIGO ||' con Resolución No. '
                              ||UN_NORESOLUCION
                              ||' del '||TO_CHAR(UN_FECRESOLUCION,'DD/MM/YYYY')
                              ||', elaborada por '||UN_ELABRESOLUCION
                              ||' y firmada por '||UN_FIRMARESOLUCION;
            MI_TABLA:= 'IP_USUARIOS_PREDIAL ';
            MI_CAMPOS := '  INDBORRADO       = -1
                          , CODIGO_NO_ACTIVO = -1
                          , FECHABORRADO     = TO_DATE('''||UN_FECRESOLUCION||''',''DD/MM/YYYY HH24:mi:ss'')
                          , OBSERVACIO      = '''||MI_OBSERVACION||''' 
                          , MODIFIED_BY = '''||UN_USUARIO||'''
                          , DATE_MODIFIED = SYSDATE';
            MI_CONDICION := '         COMPANIA     = '''||UN_COMPANIA
                            ||''' AND CODIGO       = '''||UN_CODIGO||''' ';
            DECLARE 
                MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
            BEGIN
              BEGIN 
                PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(
                                             UN_TABLA     => MI_TABLA
                                            ,UN_ACCION    => 'M' 
                                            ,UN_CAMPOS    => MI_CAMPOS
                                            ,UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                MI_REEMPLAZOS(0).CLAVE := 'CODIGO';
                MI_REEMPLAZOS(0).VALOR := UN_CODIGO;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
              END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
              PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE
                         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ANULARPREDIOUPDT
                         ,UN_REEMPLAZOS => MI_REEMPLAZOS 
                        );
            END;
            IF PCK_DATOS.GL_RTA > 0 THEN
                BEGIN 
                  SELECT RMOCOD  
                    INTO MI_MOD 
                    FROM IP_MODICOD
                   WHERE RMOVAR = 'INGRESANULACION'
                     AND ROWNUM = 1;
                EXCEPTION WHEN NO_DATA_FOUND THEN 
                  MI_MOD:=NULL;
                END;
                IF MI_MOD IS NOT NULL THEN
                    PCK_PREDIAL.PR_AUDITORIA(
                              UN_COMPANIA => UN_COMPANIA
                             ,UN_CODMOD   => UN_CODIGO
                             ,UN_OPEMOD   => UN_USUARIO
                             ,UN_CCOMOD   => MI_MOD
                             ,UN_VANMOD   => '-'
                             ,UN_VNUMOD   => '-'
                             ,UN_DESCRIPCION => MI_OBSERVACION); 

                 END IF;
                 MI_RTA := 'Proceso finalizado satisfactoriamente';

            ELSE
                MI_RTA := 'El proceso no se realizó correctamente';
            END IF;
        END IF;
    ELSE
      BEGIN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE
                     ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_REACPREDIOFALTAN
                    );
      END;
    END IF;
    RETURN MI_RTA;
END FC_ANULARPREDIO;

--6
PROCEDURE PR_REGISTRARPRESCRIPCION 
/*
    NAME              : PR_REGISTRARPRESCRIPCION
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS 
    DATE MIGRADOR     : 13/02/2017
    TIME              : 10:18 AM 
    SOURCE MODULE     : Controlador RegistroprescripcionesControlador
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : REALIZA EL PROCESO DE REGISTRO DE PRESCRIPCION.                        
    @NAME             : registrarPreinscripcion
    @METHOD           : POST
*/
(
  UN_ANIOPRESCRIPCION   IN PCK_SUBTIPOS.TI_ANIO,
  UN_CODIGOUSUARIO      IN VARCHAR2,
  UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CODIGOPRED         IN PCK_SUBTIPOS.TI_CODPREDIO,
  UN_FECHAPRESCRIPCION  IN VARCHAR2,
  UN_RESOLUCION         IN VARCHAR2,
  UN_OBSERVACION        IN VARCHAR2
) AS 
  MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
  MI_RMOCOD           VARCHAR2(4);
BEGIN
  BEGIN
      MI_CAMPOS := 'IP_FACTURADOS.PAGADO              = -1 
                  , IP_FACTURADOS.NOCOBRADO           = -1 
                  , IP_FACTURADOS.FECHAPRESCRIPCION   = '''||UN_FECHAPRESCRIPCION||'''
                  , IP_FACTURADOS.OBSERVACIONES       = '||'''RES. DE PRESCRIPCION: '||UN_RESOLUCION||'''
                  , IP_FACTURADOS.MODIFIED_BY         = '''||UN_CODIGOUSUARIO||'''
                  , IP_FACTURADOS.DATE_CREATED        = SYSDATE';

      MI_CONDICION := 'IP_FACTURADOS.COMPANIA         = '''||UN_COMPANIA||''' 
                       AND IP_FACTURADOS.CODIGO       = '''||UN_CODIGOPRED||''' 
                       AND IP_FACTURADOS.PAGADO       = 0 
                       AND IP_FACTURADOS.PREANO      <= '''||UN_ANIOPRESCRIPCION||''' 
                       ';  

      BEGIN                  
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'IP_FACTURADOS'
                                              ,UN_ACCION    => 'M'
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

      END;

      BEGIN
        BEGIN
            IF PCK_DATOS.GL_RTA = 0 THEN
                  RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END IF;
         END;

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
               UN_EXC_COD =>SQLCODE
              ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_NOVIGENCIAS
             );
       END;


      MI_CAMPOS := 'IP_USUARIOS_PREDIAL.PAGO_ANO      = '''||UN_ANIOPRESCRIPCION||''' 
                  , IP_USUARIOS_PREDIAL.OBSERVACIO    = IP_USUARIOS_PREDIAL.OBSERVACIO ||
                  -- las siguiente linea se debe dejar asi ya que se forma una cadena de caracteres y al hacer saltos de linea serian agregados en la bd como tabulaciones
                                                        '''||' RES. DE PRESCRIPCION Nº:'||' '||UN_RESOLUCION||' '||'DEL'||' '||UN_FECHAPRESCRIPCION||'''
                  , IP_USUARIOS_PREDIAL.MODIFIED_BY   = '''||UN_CODIGOUSUARIO||'''
                  , IP_USUARIOS_PREDIAL.DATE_MODIFIED = SYSDATE';

      MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA   = '''||UN_COMPANIA||''' 
                      AND IP_USUARIOS_PREDIAL.CODIGO  = '''||UN_CODIGOPRED||'''';

      BEGIN                  
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'IP_USUARIOS_PREDIAL'
                                              ,UN_ACCION    => 'M'
                                              ,UN_CAMPOS    => MI_CAMPOS
                                              ,UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;   
      END;

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
        PCK_ERR_MSG.RAISE_WITH_MSG(
           UN_EXC_COD =>SQLCODE
          ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_REGPRESCRIPCION
      );
  END;

  BEGIN                  
    BEGIN
      SELECT  IP_MODICOD.RMOCOD INTO MI_RMOCOD
      FROM    IP_MODICOD
      WHERE   IP_MODICOD.RMOVAR = 'INGRESPRESCRIPCION';

      EXCEPTION WHEN NO_DATA_FOUND THEN 
        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;   
    END;

    PCK_PREDIAL.PR_AUDITORIA(
        UN_COMPANIA       => ''||UN_COMPANIA||'',
        UN_CODMOD         => ''||UN_CODIGOPRED||'',
        UN_OPEMOD         => ''||UN_CODIGOUSUARIO||'',
        UN_CCOMOD         => ''||MI_RMOCOD||'',
        UN_VANMOD         => '-',
        UN_VNUMOD         => '-',
        UN_DESCRIPCION    => ''||UN_OBSERVACION||''
    );

    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(
         UN_EXC_COD =>SQLCODE
        ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_INSERTAUDITORIA
    );

  END;

END PR_REGISTRARPRESCRIPCION;

--7
FUNCTION FC_RECIBOSEXCEDENTESUNO
/*
    NAME              : FC_RECIBOSEXCEDENTESUNO  --> EN ACCESS un parte de pago_caja
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS 
    DATE MIGRADOR     : 13/02/2017
    TIME              : 03:45 PM 
    SOURCE MODULE     : PredialP2017.01.06VB.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : REALIZA EL PROCESO DE REGISTRO QUE SE EJECUTA EN EL EVENTO
                        DEL BOTON REGISTRAR EXCEDENTE DEL FORMULARIO RECIBOS DE PAGO EXCEDENTES.                        
    @NAME             : getRecibosExcedentesUno
    @METHOD           : GET
*/
(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUMFACTURAEX   IN PCK_SUBTIPOS.TI_DOCNUM,
    UN_CODIGOPREDIOEX IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_NUMEROORDEN    IN PCK_SUBTIPOS.TI_NUMORDEN,
    UN_BANCO          IN VARCHAR2,
    UN_PAQUETE        IN VARCHAR2,
    UN_FECHA          IN DATE ,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO,
    UN_CODIGOPREDIO   IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_NUMORDENEX     IN PCK_SUBTIPOS.TI_NUMORDEN,
    UN_BARRAS         IN VARCHAR2,
    UN_FACTURAPAGO    IN VARCHAR2,
    UN_EXCEDENTES     IN VARCHAR2,
    UN_DOCNUM         IN IP_RECIBOS_DE_PAGO.DOCNUM%TYPE,
    UN_RPPREDIO       IN IP_RECIBOS_DE_PAGO.PRECOD%TYPE,
    UN_RPANIO         IN IP_RECIBOS_DE_PAGO.PREANO%TYPE,
    UN_RPTOTAL        IN IP_RECIBOS_DE_PAGO.PREVAL%TYPE,
    UN_RPAVALUO       IN IP_RECIBOS_DE_PAGO.AVALUO%TYPE,
    UN_INSRECIBOPAGO  IN PCK_SUBTIPOS.TI_LOGICO,
    UN_ACTUALIZA      IN PCK_SUBTIPOS.TI_LOGICO
)
  RETURN VARCHAR2
AS 
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_PRECOD         PCK_SUBTIPOS.TI_MERGEUSING;
    MI_DOCNUM         VARCHAR2(100 CHAR);
    MI_RTA            VARCHAR2(100 CHAR);
    MI_PREANO         PCK_SUBTIPOS.TI_ANIO;
    MI_PREFEC         DATE;
    MI_PREVAL         PCK_SUBTIPOS.TI_DOBLE;
    MI_AVALUO         PCK_SUBTIPOS.TI_DOBLE;
    MI_PAG_BAN        PCK_SUBTIPOS.TI_ENTERO;
    MI_ANULADO        PCK_SUBTIPOS.TI_LOGICO;
    MI_PAGO           PCK_SUBTIPOS.TI_ENTERO;
    MI_PAG_BANPAG     PCK_SUBTIPOS.TI_ENTERO;
    MI_PREFECPAG      DATE;
    MI_PAQUETEPAG     PCK_SUBTIPOS.TI_ENTERO;
    MI_C1             PCK_SUBTIPOS.TI_ENTERO;
    MI_C2             PCK_SUBTIPOS.TI_ENTERO;
    MI_C3             PCK_SUBTIPOS.TI_ENTERO;
    MI_C4             PCK_SUBTIPOS.TI_ENTERO;
    MI_C5             PCK_SUBTIPOS.TI_ENTERO;
    MI_C6             PCK_SUBTIPOS.TI_ENTERO;
    MI_C7             PCK_SUBTIPOS.TI_ENTERO;
    MI_C8             PCK_SUBTIPOS.TI_ENTERO;
    MI_C9             PCK_SUBTIPOS.TI_ENTERO;
    MI_C10            PCK_SUBTIPOS.TI_ENTERO;
    MI_C11            PCK_SUBTIPOS.TI_ENTERO;
    MI_C12            PCK_SUBTIPOS.TI_ENTERO;
    MI_C13            PCK_SUBTIPOS.TI_ENTERO;
    MI_C14            PCK_SUBTIPOS.TI_ENTERO;
    MI_C15            PCK_SUBTIPOS.TI_ENTERO;
    MI_C16            PCK_SUBTIPOS.TI_ENTERO;
    MI_C17            PCK_SUBTIPOS.TI_ENTERO;
    MI_C18            PCK_SUBTIPOS.TI_ENTERO;
    MI_C19            PCK_SUBTIPOS.TI_ENTERO;
    MI_C20            PCK_SUBTIPOS.TI_ENTERO;
    MI_PAQUETE        PCK_SUBTIPOS.TI_ENTERO;
    MI_NROCUPONES     PCK_SUBTIPOS.TI_DOBLE;
    MI_VLRREPORTADO   PCK_SUBTIPOS.TI_DOBLE;
    MI_NROCUPONESACU  PCK_SUBTIPOS.TI_DOBLE;
    MI_ACUMULADO      PCK_SUBTIPOS.TI_DOBLE; 
    MI_NUMCUPONES     PCK_SUBTIPOS.TI_ENTERO;
    MI_NUMCUPONESACUM PCK_SUBTIPOS.TI_ENTERO;
    MI_EXISTE         PCK_SUBTIPOS.TI_LOGICO;
    MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

/*AGREGADO*/
  IF UN_INSRECIBOPAGO = -1 THEN 
    MI_CAMPOS := 'IP_RECIBOS_DE_PAGO.COMPANIA, 
                  IP_RECIBOS_DE_PAGO.DOCNUM, 
                  IP_RECIBOS_DE_PAGO.PRECOD, 
                  IP_RECIBOS_DE_PAGO.NUMERO_ORDEN, 
                  IP_RECIBOS_DE_PAGO.PREANO, 
                  IP_RECIBOS_DE_PAGO.PAG_BAN, 
                  IP_RECIBOS_DE_PAGO.PAG_BANPAG, 
                  IP_RECIBOS_DE_PAGO.PREANOI, 
                  IP_RECIBOS_DE_PAGO.PREANOF, 
                  IP_RECIBOS_DE_PAGO.PREVAL, 
                  IP_RECIBOS_DE_PAGO.PREFEC, 
                  IP_RECIBOS_DE_PAGO.C'||UN_EXCEDENTES||', 
                  IP_RECIBOS_DE_PAGO.AVALUO, 
                  IP_RECIBOS_DE_PAGO.PAQUETEPAG, 
                  IP_RECIBOS_DE_PAGO.CREATED_BY, 
                  IP_RECIBOS_DE_PAGO.DATE_CREATED ';

                  MI_VALORES := ' '''  || UN_COMPANIA   ||''' , 
                                  '''  || UN_DOCNUM     ||''',
                                  '''  || UN_RPPREDIO   ||''',
                                  '''  || UN_NUMORDENEX ||''',
                                  '    || UN_RPANIO     ||',
                                  '''  || UN_BANCO      ||''', 
                                  '''  || UN_BANCO      ||''', 
                                  0,
                                  0,
                                  '    || UN_RPTOTAL    ||', TO_DATE(
                                  '''  || UN_FECHA      ||''',''DD/MM/YYYY HH24:MI:SS''),
                                  '    || UN_RPTOTAL    ||',
                                  '    || UN_RPAVALUO   ||',
                                  '''  || UN_PAQUETE    ||''', 
                                  '''  || UN_USUARIO    ||''' , 
                                  SYSDATE';

                    MI_TABLA := 'IP_RECIBOS_DE_PAGO';
                    BEGIN 
                        BEGIN 
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                                          UN_TABLA   => MI_TABLA 
                                                         ,UN_ACCION  => 'I'
                                                         ,UN_CAMPOS  => MI_CAMPOS
                                                         ,UN_VALORES => MI_VALORES 
                                                         );
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSRECEXCEDENTE
                                   ,UN_TABLAERROR => MI_TABLA
                                  );
                    END;    
  END IF;

  IF UN_ACTUALIZA = -1 THEN
    MI_TABLA:= 'IP_RECIBO_EXCEDENTES';
    MI_CAMPOS:= ' IP_RECIBO_EXCEDENTES.PAGO = 0,
                  IP_RECIBO_EXCEDENTES.ANULADO = 0,
                  IP_RECIBO_EXCEDENTES.MODIFIED_BY   = '''||UN_USUARIO||''',
                  IP_RECIBO_EXCEDENTES.DATE_MODIFIED = SYSDATE';

    MI_CONDICION:= ' IP_RECIBO_EXCEDENTES.COMPANIA           = '''|| UN_COMPANIA     ||''' 
                     AND IP_RECIBO_EXCEDENTES.NUMERO_ORDEN   = '''|| UN_NUMEROORDEN  ||''' 
                     AND IP_RECIBO_EXCEDENTES.CODIGO_PREDIO  = '''|| UN_CODIGOPREDIO ||''' 
                     AND IP_RECIBO_EXCEDENTES.NUMERO_FACTURA = '''|| UN_FACTURAPAGO ||'''';
    BEGIN
        BEGIN 
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                          UN_TABLA     => MI_TABLA 
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS
                                         ,UN_CONDICION => MI_CONDICION 
                                         );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_UPDRECEXCEDENTE
                   ,UN_TABLAERROR => MI_TABLA
                  );
    END;
  END IF;
/*AGREGADO*/

    BEGIN 
        SELECT  IP_RECIBOS_DE_PAGO.DOCNUM
               ,IP_RECIBOS_DE_PAGO.PRECOD
               ,IP_RECIBOS_DE_PAGO.PREANO
               ,IP_RECIBOS_DE_PAGO.PREFEC
               ,IP_RECIBOS_DE_PAGO.PREVAL
               ,IP_RECIBOS_DE_PAGO.AVALUO
               ,IP_RECIBOS_DE_PAGO.PAG_BAN
               ,IP_RECIBOS_DE_PAGO.ANULADO
               ,IP_RECIBOS_DE_PAGO.PAGO
               ,IP_RECIBOS_DE_PAGO.PAG_BANPAG
               ,IP_RECIBOS_DE_PAGO.PREFECPAG
               ,IP_RECIBOS_DE_PAGO.PAQUETEPAG
               ,IP_RECIBOS_DE_PAGO.C1
               ,IP_RECIBOS_DE_PAGO.C2
               ,IP_RECIBOS_DE_PAGO.C3
               ,IP_RECIBOS_DE_PAGO.C4
               ,IP_RECIBOS_DE_PAGO.C5
               ,IP_RECIBOS_DE_PAGO.C6
               ,IP_RECIBOS_DE_PAGO.C7
               ,IP_RECIBOS_DE_PAGO.C8
               ,IP_RECIBOS_DE_PAGO.C9
               ,IP_RECIBOS_DE_PAGO.C10
               ,IP_RECIBOS_DE_PAGO.C11
               ,IP_RECIBOS_DE_PAGO.C12
               ,IP_RECIBOS_DE_PAGO.C13
               ,IP_RECIBOS_DE_PAGO.C14
               ,IP_RECIBOS_DE_PAGO.C15
               ,IP_RECIBOS_DE_PAGO.C16
               ,IP_RECIBOS_DE_PAGO.C17
               ,IP_RECIBOS_DE_PAGO.C18
               ,IP_RECIBOS_DE_PAGO.C19
               ,IP_RECIBOS_DE_PAGO.C20 
          INTO  
                MI_DOCNUM
               ,MI_PRECOD
               ,MI_PREANO
               ,MI_PREFEC
               ,MI_PREVAL
               ,MI_AVALUO
               ,MI_PAG_BAN
               ,MI_ANULADO
               ,MI_PAGO
               ,MI_PAG_BANPAG
               ,MI_PREFECPAG
               ,MI_PAQUETEPAG
               ,MI_C1
               ,MI_C2
               ,MI_C3
               ,MI_C4
               ,MI_C5
               ,MI_C6
               ,MI_C7
               ,MI_C8
               ,MI_C9
               ,MI_C10
               ,MI_C11
               ,MI_C12
               ,MI_C13
               ,MI_C14
               ,MI_C15
               ,MI_C16
               ,MI_C17
               ,MI_C18
               ,MI_C19
               ,MI_C20
          FROM IP_RECIBOS_DE_PAGO  
         WHERE IP_RECIBOS_DE_PAGO.COMPANIA     = UN_COMPANIA
           AND IP_RECIBOS_DE_PAGO.NUMERO_ORDEN = UN_NUMEROORDEN
           AND IP_RECIBOS_DE_PAGO.PRECOD       = UN_CODIGOPREDIOEX 
           AND IP_RECIBOS_DE_PAGO.DOCNUM       = UN_NUMFACTURAEX ;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_DOCNUM :=NULL;
    END;
    -----------Despuï¿½s de Insertar en la Tabla
    IF MI_DOCNUM IS NOT NULL THEN 
        IF MI_ANULADO IN (0) THEN 
            IF MI_PAGO IN (0) THEN 
                BEGIN 
                   SELECT  IP_PAGO_BANCOSCAB.PREFEC
                          ,IP_PAGO_BANCOSCAB.PAQUETE
                          ,IP_PAGO_BANCOSCAB.PAG_BAN
                          ,IP_PAGO_BANCOSCAB.NROCUPONES
                          ,IP_PAGO_BANCOSCAB.VLRREPORTADO
                          ,IP_PAGO_BANCOSCAB.NROCUPONESACU
                          ,IP_PAGO_BANCOSCAB.ACUMULADO 
                     INTO MI_PREFEC, 
                          MI_PAQUETE, 
                          MI_PAG_BAN, 
                          MI_NROCUPONES, 
                          MI_VLRREPORTADO, 
                          MI_NROCUPONESACU, 
                          MI_ACUMULADO
                     FROM IP_PAGO_BANCOSCAB     
                    WHERE IP_PAGO_BANCOSCAB.COMPANIA = UN_COMPANIA 
                      AND IP_PAGO_BANCOSCAB.PREFEC   = UN_FECHA
                      AND IP_PAGO_BANCOSCAB.PAQUETE  = UN_PAQUETE 
                      AND IP_PAGO_BANCOSCAB.PAG_BAN  = UN_BANCO;
                EXCEPTION WHEN NO_DATA_FOUND THEN 
                    MI_PREFEC := NULL;
                    MI_PAQUETE := NULL;
                    MI_PAG_BAN := NULL;
                    MI_NROCUPONES := NULL;
                    MI_VLRREPORTADO := NULL;
                    MI_NROCUPONESACU := NULL;
                    MI_ACUMULADO  := NULL;              
                END;
                IF MI_PAG_BAN IS NULL THEN 
                    MI_CAMPOS := ' IP_PAGO_BANCOSCAB.COMPANIA 
                                  ,IP_PAGO_BANCOSCAB.PREFEC 
                                  ,IP_PAGO_BANCOSCAB.PAQUETE
                                  ,IP_PAGO_BANCOSCAB.PAG_BAN 
                                  ,IP_PAGO_BANCOSCAB.NROCUPONES 
                                  ,IP_PAGO_BANCOSCAB.VLRREPORTADO 
                                  ,IP_PAGO_BANCOSCAB.NROCUPONESACU 
                                  ,IP_PAGO_BANCOSCAB.ACUMULADO 
                                  ,IP_PAGO_BANCOSCAB.CREATED_BY 
                                  ,IP_PAGO_BANCOSCAB.DATE_CREATED ';
                    MI_VALORES := ' '''||UN_COMPANIA||''' , TO_DATE('''
                                       ||UN_FECHA||''',''DD/MM/YYYY'') , '''
                                       ||UN_PAQUETE||''','''
                                       ||UN_BANCO||''', 1 , '
                                       ||MI_PREVAL||',1 , '
                                       ||MI_PREVAL||', '''
                                       ||UN_USUARIO||''' , SYSDATE';

                    MI_TABLA := 'IP_PAGO_BANCOSCAB';
                    BEGIN 
                        BEGIN 
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                                          UN_TABLA   => MI_TABLA 
                                                         ,UN_ACCION  => 'I'
                                                         ,UN_CAMPOS  => MI_CAMPOS
                                                         ,UN_VALORES => MI_VALORES 
                                                         );
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_EXCPAGOBANCOSCAB
                                   ,UN_TABLAERROR => MI_TABLA
                                  );
                    END;    
                ELSE 
                    MI_NUMCUPONES:= MI_NROCUPONES+ 1;
                    MI_VLRREPORTADO:= MI_VLRREPORTADO + MI_PREVAL;
                    MI_NUMCUPONESACUM:= MI_NROCUPONESACU+ 1;
                    MI_ACUMULADO:= MI_ACUMULADO + MI_PREVAL;
                    MI_TABLA := 'IP_PAGO_BANCOSCAB';
                    MI_CAMPOS:= '      IP_PAGO_BANCOSCAB.NROCUPONES    = '||MI_NUMCUPONES
                                 ||'  ,IP_PAGO_BANCOSCAB.VLRREPORTADO  = '||MI_VLRREPORTADO
                                 ||'  ,IP_PAGO_BANCOSCAB.NROCUPONESACU = '||MI_NUMCUPONESACUM
                                 ||'  ,IP_PAGO_BANCOSCAB.ACUMULADO     = '||MI_ACUMULADO
                                 ||'  ,IP_PAGO_BANCOSCAB.MODIFIED_BY   = '''|| UN_USUARIO
                                 ||''',IP_PAGO_BANCOSCAB.DATE_MODIFIED = SYSDATE';
                    MI_CONDICION:= '    IP_PAGO_BANCOSCAB.COMPANIA = '''|| UN_COMPANIA||''' 
                                    AND IP_PAGO_BANCOSCAB.PREFEC   = TO_DATE('''||UN_FECHA|| ''',''DD/MM/YYYY'') 
                                    AND IP_PAGO_BANCOSCAB.PAQUETE  = '''||UN_PAQUETE||''' 
                                    AND IP_PAGO_BANCOSCAB.PAG_BAN  = '''||UN_BANCO||''' ';
                    BEGIN 
                        BEGIN 
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                                          UN_TABLA     => MI_TABLA 
                                                         ,UN_ACCION    => 'M'
                                                         ,UN_CAMPOS    => MI_CAMPOS
                                                         ,UN_CONDICION => MI_CONDICION 
                                                         );
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTPAGOBANCOSCAB
                                   ,UN_TABLAERROR => MI_TABLA
                                  );
                    END;
                END IF;
                BEGIN 
                  SELECT 'X' 
                    INTO MI_EXISTE
                    FROM IP_PAGO_BANCOSDET  
                   WHERE IP_PAGO_BANCOSDET.COMPANIA     = UN_COMPANIA 
                     AND IP_PAGO_BANCOSDET.NUMERO_ORDEN = UN_NUMEROORDEN 
                     AND IP_PAGO_BANCOSDET.PREFEC       = UN_FECHA 
                     AND IP_PAGO_BANCOSDET.PAQUETE      = UN_PAQUETE   
                     AND IP_PAGO_BANCOSDET.PAG_BAN      = UN_BANCO   
                     AND IP_PAGO_BANCOSDET.PRECOD       = UN_CODIGOPREDIO   
                     AND IP_PAGO_BANCOSDET.ANO          = MI_PREANO;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                  MI_EXISTE:= 0;
                END;
                IF MI_EXISTE = 0 THEN 
                    MI_TABLA := 'IP_PAGO_BANCOSDET';
                    MI_CAMPOS:= 'IP_PAGO_BANCOSDET.COMPANIA 
                                ,IP_PAGO_BANCOSDET.PRECOD 
                                ,IP_PAGO_BANCOSDET.NUMERO_ORDEN
                                ,IP_PAGO_BANCOSDET.PAG_BAN
                                ,IP_PAGO_BANCOSDET.PREFEC 
                                ,IP_PAGO_BANCOSDET.PAQUETE
                                ,IP_PAGO_BANCOSDET.ANO
                                ,IP_PAGO_BANCOSDET.NUMFACTURA
                                ,IP_PAGO_BANCOSDET.PREVAL
                                ,IP_PAGO_BANCOSDET.BARRAS
                                ,IP_PAGO_BANCOSDET.C1
                                ,IP_PAGO_BANCOSDET.C2
                                ,IP_PAGO_BANCOSDET.C3
                                ,IP_PAGO_BANCOSDET.C4
                                ,IP_PAGO_BANCOSDET.C5
                                ,IP_PAGO_BANCOSDET.C6
                                ,IP_PAGO_BANCOSDET.C7
                                ,IP_PAGO_BANCOSDET.C8
                                ,IP_PAGO_BANCOSDET.C9
                                ,IP_PAGO_BANCOSDET.C10
                                ,IP_PAGO_BANCOSDET.C11
                                ,IP_PAGO_BANCOSDET.C12
                                ,IP_PAGO_BANCOSDET.C13
                                ,IP_PAGO_BANCOSDET.C14
                                ,IP_PAGO_BANCOSDET.C15
                                ,IP_PAGO_BANCOSDET.C16
                                ,IP_PAGO_BANCOSDET.C17
                                ,IP_PAGO_BANCOSDET.C18
                                ,IP_PAGO_BANCOSDET.C19
                                ,IP_PAGO_BANCOSDET.C20
                                ,IP_PAGO_BANCOSDET.CREATED_BY 
                                ,IP_PAGO_BANCOSDET.DATE_CREATED ';
                    MI_VALORES:= ' '''||UN_COMPANIA||''' , '''
                                      ||UN_CODIGOPREDIOEX|| ''', '''
                                      ||UN_NUMORDENEX||''', '''
                                      ||UN_BANCO||''',TO_DATE('''
                                      ||UN_FECHA||''',''DD/MM/YYYY''), '''
                                      ||UN_PAQUETE||''', '
                                      ||MI_PREANO||', '''
                                      ||UN_NUMFACTURAEX||''', ' 
                                      ||MI_PREVAL||' , '''
                                      ||UN_BARRAS||''' , ' 
                                      ||MI_C1||' , ' 
                                      ||MI_C2||' ,' 
                                      ||MI_C3||' , ' 
                                      ||MI_C4||', ' 
                                      ||MI_C5||' , ' 
                                      ||MI_C6||' , '
                                      ||MI_C7||' , ' 
                                      ||MI_C8|| ', ' 
                                      ||MI_C9|| ' , ' 
                                      ||MI_C10||' , ' 
                                      ||MI_C11||' , ' 
                                      ||MI_C12||' , ' 
                                      ||MI_C13||' , ' 
                                      ||MI_C14||', ' 
                                      ||MI_C15||',' 
                                      ||MI_C16||' , '
                                      ||MI_C17||',' 
                                      ||MI_C18||' , ' 
                                      ||MI_C19||', ' 
                                      ||MI_C20||' , '''
                                      ||UN_USUARIO||''', SYSDATE';
                    BEGIN 
                        BEGIN 
                            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                                          UN_TABLA   => MI_TABLA 
                                                         ,UN_ACCION  => 'I'
                                                         ,UN_CAMPOS  => MI_CAMPOS
                                                         ,UN_VALORES => MI_VALORES 
                                                         );
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                            MI_REEMPLAZOS(0).CLAVE := 'UN_CODIGOPREDIOEX';
                            MI_REEMPLAZOS(0).VALOR := UN_CODIGOPREDIOEX;
                            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INS_DETALLE_PAGO
                                   ,UN_TABLAERROR => MI_TABLA
                                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                                  );
                    END;
                ELSE  
                  RETURN 'TB_TB1184';
                END IF;
                --Actuliza el Pago en Recibos de Pago
                MI_TABLA := 'IP_RECIBOS_DE_PAGO'; 
                MI_CAMPOS:= '  IP_RECIBOS_DE_PAGO.PAGO          = -1 
                             , IP_RECIBOS_DE_PAGO.PAG_BANPAG    = '''||UN_BANCO||'''
                             , IP_RECIBOS_DE_PAGO.PREFECPAG     = TO_DATE('''||UN_FECHA||''',''DD/MM/YYYY'')
                             , IP_RECIBOS_DE_PAGO.PAQUETEPAG    = '''||UN_PAQUETE||''' 
                             , IP_RECIBOS_DE_PAGO.MODIFIED_BY   = '''|| UN_USUARIO||''' 
                             , IP_RECIBOS_DE_PAGO.DATE_MODIFIED =  SYSDATE';
                MI_CONDICION:= '     IP_RECIBOS_DE_PAGO.COMPANIA     = '''||UN_COMPANIA||''' 
                                 AND IP_RECIBOS_DE_PAGO.NUMERO_ORDEN = '''||UN_NUMEROORDEN||''' 
                                 AND IP_RECIBOS_DE_PAGO.PRECOD       = '''||UN_CODIGOPREDIOEX||''' 
                                 AND IP_RECIBOS_DE_PAGO.DOCNUM       = '''||UN_NUMFACTURAEX||''' ';
                BEGIN 
                    BEGIN 
                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                                      UN_TABLA     => MI_TABLA 
                                                     ,UN_ACCION    => 'M'
                                                     ,UN_CAMPOS    => MI_CAMPOS
                                                     ,UN_CONDICION => MI_CONDICION 
                                                     );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                      MI_REEMPLAZOS(0).CLAVE := 'UN_CODIGOPREDIOEX';
                      MI_REEMPLAZOS(0).VALOR := UN_CODIGOPREDIOEX;
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTU_RECIBO_PAGO
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                              );
                END;
                --// Actualiza el pago en Pagos Dobles
                MI_TABLA:= 'IP_PAGOSDOBLES';
                MI_CAMPOS:= ' IP_PAGOSDOBLES.PAGO          = -1 
                             ,IP_PAGOSDOBLES.MODIFIED_BY   = '''||UN_USUARIO||''' 
                             ,IP_PAGOSDOBLES.DATE_MODIFIED = SYSDATE';
                MI_CONDICION:= '     IP_PAGOSDOBLES.COMPANIA     = '''||UN_COMPANIA||''' 
                                 AND IP_PAGOSDOBLES.NUMERO_ORDEN = '''||UN_NUMEROORDEN||''' 
                                 AND IP_PAGOSDOBLES.PRECOD       = '''||UN_CODIGOPREDIOEX||''' 
                                 AND IP_PAGOSDOBLES.PAGO         IN (0)';
                BEGIN
                    BEGIN 
                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                                      UN_TABLA     => MI_TABLA 
                                                     ,UN_ACCION    => 'M'
                                                     ,UN_CAMPOS    => MI_CAMPOS
                                                     ,UN_CONDICION => MI_CONDICION 
                                                     );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                        MI_REEMPLAZOS(0).CLAVE := 'UN_CODIGOPREDIOEX';
                        MI_REEMPLAZOS(0).VALOR := UN_CODIGOPREDIOEX;
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTU_PAGOS_DOBLES
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                              );
                END;
                -- Actualiza recibos excedentes.
                MI_TABLA := 'IP_RECIBO_EXCEDENTES';
                MI_CAMPOS:= ' IP_RECIBO_EXCEDENTES.FECHA_PAGO    = TO_DATE('''||UN_FECHA||''',''DD/MM/YYYY'')
                             ,IP_RECIBO_EXCEDENTES.PAGO          = -1 
                             ,IP_RECIBO_EXCEDENTES.BANCO         = '''||UN_BANCO||''' 
                             ,IP_RECIBO_EXCEDENTES.MODIFIED_BY   = '''||UN_USUARIO||''' 
                             ,IP_RECIBO_EXCEDENTES.DATE_MODIFIED = SYSDATE ';
                MI_CONDICION:= '     IP_RECIBO_EXCEDENTES.COMPANIA       = '''||UN_COMPANIA||''' 
                                 AND IP_RECIBO_EXCEDENTES.NUMERO_FACTURA = '''|| UN_FACTURAPAGO||'''
                                 AND IP_RECIBO_EXCEDENTES.NUMERO_ORDEN   = '''||UN_NUMEROORDEN||''' ';
                BEGIN 
                    BEGIN  
                        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                                          UN_TABLA     => MI_TABLA 
                                                         ,UN_ACCION    => 'M'
                                                         ,UN_CAMPOS    => MI_CAMPOS
                                                         ,UN_CONDICION => MI_CONDICION 
                                                     );
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                      MI_REEMPLAZOS(0).CLAVE := 'UN_CODIGOPREDIOEX';
                      MI_REEMPLAZOS(0).VALOR := UN_CODIGOPREDIOEX;
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTU_REC_EXCEDENT
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                              );
                END;
            ELSE     
                RETURN 'TB_TB1184';
            END IF; 
        ELSE  
            RETURN 'TB_TB1186';
        END IF;
    END IF;
    RETURN 'TB_TB1187';    

END FC_RECIBOSEXCEDENTESUNO;

  --8
FUNCTION FC_REG_EXONERACION_VIGENCIAS
  ( 
    /*
      NAME              : FC_REG_EXONERACION_VIGENCIAS 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MIGRADOR     : 13/02/2017
      TIME              : 05:00 PM 
      SOURCE MODULE     : PredialP2017.01.06VB.accdb
      MODIFIER          : SERGIO ESTEBAN PIÑA VARGAS
      DATE MODIFIED     : 25/07/2017 - SE AGREGAN LOS CAMPOS DE AUDITORÍA EN LAS ACTUALIZACIONES DE LAS TABLAS
      TIME              : 10:00 AM
      DESCRIPTION       : REALIZA EL PROCESO DE REGISTRO QUE SE EJECUTA EN EL EVENTO DEL BOTON REGISTRAR 
                          DEL FORMULARIO EXONERACION DE VIGENCIAS.
      PARAMETERS        : UN_COMPANIA        => COMPANIA ACTUAL.
                          UN_CODIGO          => CODIGO DEL REGISTRO FACTURADO.
                          UN_FECRESOLUCION   => FECHA EN QUE SE REALIZO RESOLUCION DE EXONERACION.
                          UN_NUMRESOLUCION   => NUMERO DE RESOLUCION DE EXONERACION.
                          UN_OBSERVACION     => OBSERVACION INGRESADA POR EL USUARIO.
                          UN_VIGINICIAL      => ANIO DE LA VIGENCIA INICIAL.
                          UN_VIGFINAL        => ANIO DE LA VIGENCIA FINAL.
                          UN_ELABRESOLUCION  => NOMBRE DE QUIEN ELABORO RESOLUCION.
                          UN_FIRMARESOLUCION => NOMBRE DE QUIEN FIRMO RESOLUCION.
                          UN_USUARIO         => USUARIO ACTUAL.
                          UN_INDAPLICAR      => VARIABLE DE SI SE APLICA O NO.
      @NAME             : registrarExoneracionVigencias
      @METHOD           : GET
    */
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGO           IN IP_FACTURADOS.CODIGO%TYPE,
    UN_FECRESOLUCION    IN DATE,
    UN_NUMRESOLUCION    IN VARCHAR2,
    UN_OBSERVACION      IN VARCHAR2,
    UN_VIGINICIAL       IN PCK_SUBTIPOS.TI_ANIO,
    UN_VIGFINAL         IN PCK_SUBTIPOS.TI_ANIO,
    UN_ELABRESOLUCION   IN VARCHAR2,
    UN_FIRMARESOLUCION  IN VARCHAR2,
    UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO,
    UN_INDAPLICAR       IN PCK_SUBTIPOS.TI_LOGICO
  )
  RETURN PCK_SUBTIPOS.TI_LOGICO
  AS
    MI_OBSERVACIONES    VARCHAR2(1000 CHAR);
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_FILAS            PCK_SUBTIPOS.TI_ENTERO;
    MI_ANIO             PCK_SUBTIPOS.TI_ANIO;
    MI_RMOCOD           IP_MODICOD.RMOCOD%TYPE;
    MI_INDREGISTRO      PCK_SUBTIPOS.TI_LOGICO;

  BEGIN                                                
    MI_FILAS := 0;
    MI_INDREGISTRO := 0;

    IF UN_INDAPLICAR <> 0 THEN -- ' Se ingresa una resolución para la exoneración de vigencias
      IF NVL(UN_OBSERVACION,' ') = ' ' THEN 
        MI_OBSERVACIONES := 'Resolución de Exoneración de vigencias No. ';
      ELSE
        MI_OBSERVACIONES := UN_OBSERVACION|| '. Resolución No. ';
      END IF;

      MI_OBSERVACIONES := MI_OBSERVACIONES
                          || UN_NUMRESOLUCION || ' del ' 
                          || TO_CHAR(UN_FECRESOLUCION,'DD/MM/YYYY') ||
                          ', desde la vigencia ' 
                          || UN_VIGINICIAL ||
                          ' hasta la vigencia ' 
                          || UN_VIGFINAL ||
                          '. Elaborada por ' 
                          ||UN_ELABRESOLUCION ||
                          ' y firmada por ' 
                          ||UN_FIRMARESOLUCION;

      MI_CAMPOS    := 'IP_FACTURADOS.PAGADO           = -1,
                       IP_FACTURADOS.NOCOBRADO        = -1,
                       IP_FACTURADOS.OBSERVACIONES    = '''||MI_OBSERVACIONES||''',
                       IP_FACTURADOS.PREANO_EXONERADO = -1,
                       IP_FACTURADOS.PREFEC           = '''||UN_FECRESOLUCION||''',
                       IP_FACTURADOS.MODIFIED_BY      = '''||UN_USUARIO||''',
                       IP_FACTURADOS.DATE_MODIFIED    = SYSDATE
                       ';

      MI_CONDICION := '    IP_FACTURADOS.COMPANIA = '''|| UN_COMPANIA ||'''
                       AND IP_FACTURADOS.CODIGO   = '''|| UN_CODIGO ||'''
                       AND IP_FACTURADOS.PAGADO   = 0
                       AND IP_FACTURADOS.PREANO   BETWEEN '|| UN_VIGINICIAL ||' AND '|| UN_VIGFINAL ||'';

      BEGIN
        BEGIN
          MI_FILAS     := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_FACTURADOS',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_EXOVIG_AC_OBS_FAC
          );
      END;

      BEGIN
        BEGIN
          IF MI_FILAS = 0 THEN -- ' No se afectaron registros en facturados
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END IF;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN          
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_EXOVIG_CERO_FILAS
          );
      END; -- ' Se afectaron registros en facturados

      BEGIN
        SELECT  MIN(PREANO)
        INTO    MI_ANIO
        FROM    IP_FACTURADOS
        WHERE   COMPANIA = UN_COMPANIA
          AND   CODIGO   = UN_CODIGO
          AND   PAGADO   = 0;

        EXCEPTION WHEN NO_DATA_FOUND THEN 
          MI_ANIO := 0;
      END; 

      MI_CAMPOS        := 'IP_USUARIOS_PREDIAL.PAGO_ANO               = '  || MI_ANIO || ' - 1,
                           IP_USUARIOS_PREDIAL.OBSERVACIONES_DE_PAGOS = '''|| MI_OBSERVACIONES ||'''||'' - ''||IP_USUARIOS_PREDIAL.OBSERVACIONES_DE_PAGOS,
                           IP_USUARIOS_PREDIAL.EXONERADO              = -1,
                           IP_USUARIOS_PREDIAL.EXOINICIAL             = '|| UN_VIGINICIAL ||',
                           IP_USUARIOS_PREDIAL.EXOFINAL               = '|| UN_VIGFINAL ||',
                           IP_USUARIOS_PREDIAL.MODIFIED_BY            = '''|| UN_USUARIO ||''',
                           IP_USUARIOS_PREDIAL.DATE_MODIFIED          = SYSDATE
                           ';

      MI_CONDICION := '    IP_USUARIOS_PREDIAL.COMPANIA = '''|| UN_COMPANIA ||'''
                       AND IP_USUARIOS_PREDIAL.CODIGO   = '''|| UN_CODIGO ||'''';

      IF MI_ANIO IS NOT NULL THEN    -- ' Existen vigencias sin cancelar
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
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD =>SQLCODE,
              UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_EXOVIG_AC_USUP_ME
            );
        END;
      ELSE -- 'No existen vigencias sin cancelar
        BEGIN
          SELECT  MAX(PREANO)
          INTO    MI_ANIO
          FROM    IP_FACTURADOS
          WHERE   COMPANIA =  UN_COMPANIA
            AND   CODIGO   =  UN_CODIGO
            AND   PAGADO   <> 0;

          EXCEPTION WHEN NO_DATA_FOUND THEN -- ' Todos los facturados se encuentran cancelados. Selecciona el mayor de estos
            MI_ANIO := 0;
        END;

        BEGIN    
          BEGIN
            MI_CAMPOS        := 'IP_USUARIOS_PREDIAL.PAGO_ANO               = '  || MI_ANIO ||',
                                 IP_USUARIOS_PREDIAL.OBSERVACIONES_DE_PAGOS = '''|| MI_OBSERVACIONES ||'''||'' - ''||IP_USUARIOS_PREDIAL.OBSERVACIONES_DE_PAGOS,
                                 IP_USUARIOS_PREDIAL.EXONERADO              = -1,
                                 IP_USUARIOS_PREDIAL.EXOINICIAL             = '|| UN_VIGINICIAL ||',
                                 IP_USUARIOS_PREDIAL.EXOFINAL               = '|| UN_VIGFINAL ||',
                                 IP_USUARIOS_PREDIAL.MODIFIED_BY            = '''|| UN_USUARIO ||''',
                                 IP_USUARIOS_PREDIAL.DATE_MODIFIED          = SYSDATE
                                 ';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD =>SQLCODE,
              UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_EXOVIG_AC_USUP_MA
            );
        END;

        -- 'Registra la auditoria del proceso
      END IF;

      MI_INDREGISTRO := -1; 
    ELSE -- ' Se ingresa una resolucion para la desactivación de una vigencia anteriormente exonerada
      IF NVL(UN_OBSERVACION,' ') = ' ' THEN
        MI_OBSERVACIONES := 'Resolución de Desactivación de Exoneración de vigencias No. ';
      ELSE
        MI_OBSERVACIONES := UN_OBSERVACION|| '. Resolución No. ';
      END IF;

      MI_OBSERVACIONES := MI_OBSERVACIONES
                          || UN_NUMRESOLUCION || ' del ' 
                          || TO_CHAR(UN_FECRESOLUCION,'DD/MM/YYYY') ||
                          ', desde la vigencia ' 
                          || UN_VIGINICIAL ||
                          ' hasta la vigencia ' 
                          || UN_VIGFINAL ||
                          '. Elaborada por ' 
                          ||UN_ELABRESOLUCION ||
                          ' y firmada por ' 
                          ||UN_FIRMARESOLUCION;

      MI_CAMPOS    := 'IP_FACTURADOS.PAGADO           = 0,
                       IP_FACTURADOS.NOCOBRADO        = 0,
                       IP_FACTURADOS.OBSERVACIONES    = '''||MI_OBSERVACIONES||''',
                       IP_FACTURADOS.PREANO_EXONERADO = 0,
                       IP_FACTURADOS.PREFEC           = TO_DATE('''||UN_FECRESOLUCION||''',''DD/MM/YYYY''),
                       IP_FACTURADOS.MODIFIED_BY      = '''|| UN_USUARIO  ||''',
                       IP_FACTURADOS.DATE_MODIFIED    = SYSDATE
                       ';

      MI_CONDICION := '    IP_FACTURADOS.COMPANIA         =  '''|| UN_COMPANIA ||'''
                       AND IP_FACTURADOS.CODIGO           =  '''|| UN_CODIGO ||'''
                       AND IP_FACTURADOS.PAGADO           <> 0
                       AND IP_FACTURADOS.NOCOBRADO        <> 0
                       AND IP_FACTURADOS.PREANO_EXONERADO <> 0
                       AND IP_FACTURADOS.PREANO           BETWEEN '|| UN_VIGINICIAL ||' AND '|| UN_VIGFINAL ||'';

       BEGIN
        BEGIN
          MI_FILAS     := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_FACTURADOS',
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;

        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_EXOVIG_AC_OBS_FAC
          );
      END;

      BEGIN
        BEGIN
          IF MI_FILAS = 0 THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END IF;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERRR_PREDIAL_NOAFECFACT
        );
      END;

      IF NVL(UN_OBSERVACION,' ') <> ' ' THEN
        MI_OBSERVACIONES := UN_OBSERVACION;
      END IF;

      BEGIN
        SELECT  MIN(PREANO)
        INTO    MI_ANIO
        FROM    IP_FACTURADOS
        WHERE   COMPANIA = UN_COMPANIA
          AND   CODIGO = UN_CODIGO 
          AND   PAGADO = 0;

        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_ANIO := 0;
      END;

      MI_CAMPOS        := 'IP_USUARIOS_PREDIAL.PAGO_ANO               = '  || MI_ANIO|| ' -1,
                           IP_USUARIOS_PREDIAL.OBSERVACIONES_DE_PAGOS = '''|| MI_OBSERVACIONES ||'''||'' - ''||IP_USUARIOS_PREDIAL.OBSERVACIONES_DE_PAGOS,
                           IP_USUARIOS_PREDIAL.EXONERADO              = 0,
                           IP_USUARIOS_PREDIAL.EXOINICIAL             = 0,
                           IP_USUARIOS_PREDIAL.EXOFINAL               = 0,
                           IP_USUARIOS_PREDIAL.MODIFIED_BY            = '''|| UN_USUARIO ||''',
                           IP_USUARIOS_PREDIAL.DATE_MODIFIED          = SYSDATE
                           ';

      MI_CONDICION := '    IP_USUARIOS_PREDIAL.COMPANIA = '''|| UN_COMPANIA ||'''
                       AND IP_USUARIOS_PREDIAL.CODIGO   = '''|| UN_CODIGO ||'''';

      IF MI_ANIO IS NOT NULL THEN    
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
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD =>SQLCODE,
              UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_EXOVIG_AC_USUP_ME
            );
        END;

      ELSE
        BEGIN        
          SELECT  MAX(PREANO)
          INTO    MI_ANIO
          FROM    IP_FACTURADOS
          WHERE   COMPANIA =  UN_COMPANIA
            AND   CODIGO   =  UN_CODIGO
            AND   PAGADO   <> 0;

          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_ANIO := 0;
        END;

        BEGIN
          BEGIN
            MI_CAMPOS        := 'IP_USUARIOS_PREDIAL.PAGO_ANO               = '  || MI_ANIO ||',
                                 IP_USUARIOS_PREDIAL.OBSERVACIONES_DE_PAGOS = '''|| MI_OBSERVACIONES ||'''||'' - ''||IP_USUARIOS_PREDIAL.OBSERVACIONES_DE_PAGOS,
                                 IP_USUARIOS_PREDIAL.EXONERADO              = -1,
                                 IP_USUARIOS_PREDIAL.EXOINICIAL             = '|| UN_VIGINICIAL ||',
                                 IP_USUARIOS_PREDIAL.EXOFINAL               = '|| UN_VIGFINAL ||',
                                 IP_USUARIOS_PREDIAL.MODIFIED_BY            = '''|| UN_USUARIO ||''',
                                 IP_USUARIOS_PREDIAL.DATE_MODIFIED          = SYSDATE
                                 ';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD =>SQLCODE,
              UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_EXOVIG_AC_USUP_MA
            );
        END;
      END IF;
    END IF;

    MI_OBSERVACIONES := MI_OBSERVACIONES 
                        || '. Durante el proceso se afectaron '|| MI_FILAS || 'registros.';

    BEGIN
      BEGIN
        SELECT  RMOCOD
        INTO    MI_RMOCOD
        FROM    IP_MODICOD 
        WHERE   RMOVAR = 'INGRESEXONERACION'
          AND   ROWNUM = 1;

        EXCEPTION WHEN NO_DATA_FOUND THEN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD =>SQLCODE,
          UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_EXOVIG_RMOCOD
        );
    END;
    -- 'Registra la auditoria del proceso
    IF MI_RMOCOD IS NOT NULL THEN
      PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA,
                               UN_CODMOD      => UN_CODIGO,
                               UN_OPEMOD      => UN_USUARIO,
                               UN_CCOMOD      => MI_RMOCOD,
                               UN_VANMOD      => '-',
                               UN_VNUMOD      => '-',
                               UN_DESCRIPCION => MI_OBSERVACIONES);
    END IF;

    RETURN MI_INDREGISTRO;
  END FC_REG_EXONERACION_VIGENCIAS;

--9
PROCEDURE PR_ACTUALIZA_DGRE
/*
    NAME              : PR_ACTUALIZA_DGRE  --> EN ACCESS una parte de pago_caja
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS 
    DATE MIGRADOR     : 13/02/2017
    TIME              : 05:30 PM 
    SOURCE MODULE     : PredialP2017.01.06VB.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : REALIZA EL PROCESO DE ANULACION DE UN RECIBO DEL BOTON REGISTRAR EXCEDENTE 
                        DEL FORMULARIO RECIBOS DE PAGO EXCEDENTES.                        
    @NAME             : anulaRecibosExcedentes
    @METHOD           : POST
*/
(
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_NUMEROORDEN    IN PCK_SUBTIPOS.TI_NUMORDEN,
    UN_NUMEROFACTURA  IN VARCHAR2,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO  
)
AS 
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN 
    MI_TABLA := 'IP_RECIBOS_DE_PAGO';
    MI_CAMPOS:= ' IP_RECIBOS_DE_PAGO.ANULADO       = -1 
                 ,IP_RECIBOS_DE_PAGO.MODIFIED_BY   = '''||UN_USUARIO||''' 
                 ,IP_RECIBOS_DE_PAGO.DATE_MODIFIED = SYSDATE';
    MI_CONDICION:= '    IP_RECIBOS_DE_PAGO.COMPANIA     = '''||UN_COMPANIA||'''
                    AND IP_RECIBOS_DE_PAGO.NUMERO_ORDEN = '''||UN_NUMEROORDEN||'''
                    AND IP_RECIBOS_DE_PAGO.DOCNUM       = '''||UN_NUMEROFACTURA||''' ';
    BEGIN 
        BEGIN 
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                          UN_TABLA     => MI_TABLA 
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS
                                         ,UN_CONDICION => MI_CONDICION 
                                         );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            MI_REEMPLAZOS(0).CLAVE := 'UN_NUMEROFACTURA';
            MI_REEMPLAZOS(0).VALOR := UN_NUMEROFACTURA;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTU_ANULA_RECPAG
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;
    MI_TABLA:='IP_RECIBO_EXCEDENTES';
    MI_CAMPOS:= ' IP_RECIBO_EXCEDENTES.ANULADO       = -1 
                 ,IP_RECIBO_EXCEDENTES.MODIFIED_BY   = '''||UN_USUARIO||'''
                 ,IP_RECIBO_EXCEDENTES.DATE_MODIFIED = SYSDATE';
    MI_CONDICION:= '     IP_RECIBO_EXCEDENTES.COMPANIA       = '''||UN_COMPANIA||'''
                     AND IP_RECIBO_EXCEDENTES.NUMERO_ORDEN   = '''||UN_NUMEROORDEN||'''
                     AND IP_RECIBO_EXCEDENTES.NUMERO_FACTURA = '''||UN_NUMEROFACTURA||''' ';
    BEGIN 
        BEGIN 
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (
                                          UN_TABLA     => MI_TABLA 
                                         ,UN_ACCION    => 'M'
                                         ,UN_CAMPOS    => MI_CAMPOS
                                         ,UN_CONDICION => MI_CONDICION 
                                         );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
            MI_REEMPLAZOS(0).CLAVE := 'UN_NUMEROFACTURA';
            MI_REEMPLAZOS(0).VALOR := UN_NUMEROFACTURA;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_ACTU_ANULA_RECPAG
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;
END PR_ACTUALIZA_DGRE;

--10
  FUNCTION FC_EJECUTARDEPURACIONCUPONES
    /*
      NAME              : FC_EJECUTARDEPURACIONCUPONES En Access --> Comando13_Click en Form_MANT_DEPURACIONDECUPONES
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADOR     : 13/02/2017
      TIME              : 04:07 PM
      SOURCE MODULE     : PREDIAL - PredialP2017.01.06VB
      DESCRIPTION       : Proceso que permite actualizar el valor y número de cupones que se realiza al ejecuta en el evento click 
                          del botón aceptar del formulario Access FRM_MANT_DEPURACIONDECUPONES.
      PARAMETERS        : UN_COMPANIA       => Compañia de ingreso a la aplicación.  
                          UN_FECHAINICIAL   => Fecha inicial desde la cual se buscarán los pagos.
                          UN_FECHAFINAL     => Fecha final hasta la cual se buscarán los pagos.
                          UN_BANCOINICIAL   => Banco inicial desde el cual se buscarán los pagos.
                          UN_BANCOFINAL     => Banco final desde el cual se buscarán los pagos.
                          UN_PAQUETEINICIAL => Paquete inicial desde el cual se buscarán los pagos.
                          UN_PAQUETEFINAL   => Paquete final desde el cual se buscarán los pagos.
                          UN_NOMBREBANCOINI => Nombre del bnaco inicial seleccionado.
                          UN_USUARIO        => Usuario que realiza el registro..
      @NAME:  arreglarCupones
      @METHOD:  GET
      */
    (
      UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_FECHAINICIAL       IN VARCHAR2,
      UN_FECHAFINAL         IN VARCHAR2,
      UN_BANCOINICIAL       IN IP_PAGO_BANCOSCAB.PAG_BAN%TYPE,
      UN_BANCOFINAL         IN IP_PAGO_BANCOSCAB.PAG_BAN%TYPE,
      UN_PAQUETEINICIAL     IN IP_PAGO_BANCOSCAB.PAQUETE%TYPE,
      UN_PAQUETEFINAL       IN IP_PAGO_BANCOSCAB.PAQUETE%TYPE,
      UN_NOMBREBANCOINI     IN IP_BANCOS.NOMBREBANCO%TYPE,
      UN_USUARIO            IN VARCHAR2
    )
  RETURN VARCHAR2 AS 
    MI_CODAUD      IP_MODICOD.RMOCOD%TYPE;
    MI_CONT        PCK_SUBTIPOS.TI_ENTERO := 0;
    MI_FECHA       VARCHAR(20 CHAR);
    MI_RESPUESTA   VARCHAR2(10 CHAR);
  BEGIN
    MI_CODAUD := FC_CONSULTARCODIGOAUDITORIA(UN_COMPANIA   => UN_COMPANIA,
                                             UN_MOVIMIENTO => 'MANTENIMIENTO');
    <<DATOS_BANCOSCAB>>
    FOR RS_DATOS IN  (SELECT TO_CHAR(PREFEC, 'DD/MM/YYYY') PREFEC, 
                             PAG_BAN, 
                             PAQUETE 
                        FROM IP_PAGO_BANCOSCAB 
                       WHERE COMPANIA = UN_COMPANIA  
                         AND PREFEC  BETWEEN TO_DATE(UN_FECHAINICIAL, 'DD/MM/YYYY')   AND TO_DATE(UN_FECHAFINAL, 'DD/MM/YYYY')  
                         AND PAG_BAN BETWEEN UN_BANCOINICIAL   AND UN_BANCOFINAL
                         AND PAQUETE BETWEEN UN_PAQUETEINICIAL AND UN_PAQUETEFINAL)
    LOOP
      PCK_PREDIAL_COM2.PR_ARREGLARCUPONES(UN_COMPANIA    => UN_COMPANIA,
                                          UN_FECHA       => TO_DATE(RS_DATOS.PREFEC),
                                          UN_BANCO       => RS_DATOS.PAG_BAN,
                                          UN_PAQUETE     => RS_DATOS.PAQUETE,
                                          UN_USUARIO     => UN_USUARIO);
      SELECT TO_CHAR(TRUNC(TO_DATE(UN_FECHAINICIAL, 'DD/MM/YYYY HH24:mi:ss')), 'DD/MM/YYYY')
        INTO MI_FECHA
        FROM DUAL;                  
      PCK_PREDIAL.PR_AUDITORIA(UN_COMPANIA    => UN_COMPANIA,     
                               UN_CODMOD      => 'VARIOS',
                               UN_OPEMOD      => UN_USUARIO,
                               UN_CCOMOD      => MI_CODAUD,
                               UN_VANMOD      => '-',
                               UN_VNUMOD      => '-',
                               UN_DESCRIPCION => 'Mantenimiento de Cupones de la fecha ' || MI_FECHA || 
                                                 ', Banco  ' || UN_NOMBREBANCOINI || 
                                                 ' y paquete ' || UN_PAQUETEINICIAL,
                               UN_FECMOD      => SYSDATE,
                               UN_HORMOD      => SYSDATE);    
      MI_CONT :=+1;
    END LOOP DATOS_BANCOSCAB;

    IF MI_CONT NOT IN (0)
    THEN
      MI_RESPUESTA := 'OK';
    ELSE 
      MI_RESPUESTA := 'NO';
    END IF;
    RETURN MI_RESPUESTA;
  END FC_EJECUTARDEPURACIONCUPONES;

 --11 
  FUNCTION FC_CONSULTARCODIGOAUDITORIA 
    /*
      NAME              : FC_CONSULTARCODIGOAUDITORIA En Access --> CodigoAuditoria en Utilidades
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTÉS FORERO
      DATE MIGRADOR     : 13/02/2017
      TIME              : 03:52 PM
      SOURCE MODULE     : PREDIAL - PredialP2017.01.06VB
      DESCRIPTION       : Proceso que permite consultar el código de auditoría para el movimiento correspondiente.
      PARAMETERS        : UN_COMPANIA   => Compañia de ingreso a la aplicación.
                          UN_MOVIMIENTO => Tipo de movimiento del cual se desea conocer el código de auditoría.

      @NAME:  consultarCodigoAuditoria
      @METHOD:  GET
      */
    (
      UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_MOVIMIENTO     IN VARCHAR2
    )
  RETURN VARCHAR2 
    AS 
      MI_CODAUDITORIA     IP_MODICOD.RMOCOD%TYPE;
  BEGIN
    BEGIN
      SELECT RMOCOD   
        INTO MI_CODAUDITORIA
        FROM IP_MODICOD                                   
       WHERE RMOVAR = UN_MOVIMIENTO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_CODAUDITORIA := 'NE';
    END;
    RETURN MI_CODAUDITORIA;
  END FC_CONSULTARCODIGOAUDITORIA;

--12 
FUNCTION FC_GENERARFACTURAEXCEDENTE
/*
      @NAME:  generarFacturaExcedente
      @METHOD:  GET
*/
(
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGO_PREDIO IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_NUMERO_ORDEN  IN PCK_SUBTIPOS.TI_NUMORDEN,
    UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO,
    UN_NOMBRE        IN IP_USUARIOS_PREDIAL.NOMBRE%TYPE
) RETURN VARCHAR2
AS 
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    MI_FECHALIMITE    PARAMETRO.VALOR%TYPE;
    MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_EXISTE         PCK_SUBTIPOS.TI_ENTERO_LARGO; 
    MI_MENSERROR      PCK_SUBTIPOS.TI_CONDICION;  
    MI_PRECOD         PCK_SUBTIPOS.TI_DOBLE;
    MI_TOTALCONDEC    PCK_SUBTIPOS.TI_DOBLE;
    MI_C1CONDEC       PCK_SUBTIPOS.TI_DOBLE;
    MI_C2CONDEC       PCK_SUBTIPOS.TI_DOBLE;
    MI_C3CONDEC       PCK_SUBTIPOS.TI_DOBLE;
    MI_C4CONDEC       PCK_SUBTIPOS.TI_DOBLE;
    MI_C13CONDEC      PCK_SUBTIPOS.TI_DOBLE;
    MI_C14CONDEC      PCK_SUBTIPOS.TI_DOBLE;
    MI_C15CONDEC      PCK_SUBTIPOS.TI_DOBLE;
    MI_C16CONDEC      PCK_SUBTIPOS.TI_DOBLE;
    MI_C17CONDEC      PCK_SUBTIPOS.TI_DOBLE;
    MI_C18CONDEC      PCK_SUBTIPOS.TI_DOBLE;
    MI_C19CONDEC      PCK_SUBTIPOS.TI_DOBLE;
    MI_C20CONDEC      PCK_SUBTIPOS.TI_DOBLE;
    MI_MPREANO        PCK_SUBTIPOS.TI_DOBLE;
    MI_TARIFA         VARCHAR2(20 CHAR);
    MI_AVALUO_ANO     VARCHAR2(20 CHAR);
    MI_NUEVAFACTURAEX PCK_SUBTIPOS.TI_DOCNUM;
    MI_NUEVAFACTURA   PCK_SUBTIPOS.TI_DOCNUM;
    MI_RTA            PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_NUMANOS        PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_ANOFIN         PCK_SUBTIPOS.TI_ANIO;
    MI_CODIGO_PREDIO  PCK_SUBTIPOS.TI_CODPREDIO;
    MI_SUMADEC1       PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMADEC2       PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMADEC3       PCK_SUBTIPOS.TI_DOBLE;
    MI_SUMADEC4       PCK_SUBTIPOS.TI_DOBLE;
BEGIN
    --'(TAR:1000065468; FECHA:29/08/2016; AUTOR:AA)
    --'Se controla que el parámetro fecha límite de pago esté configurado para insertar el registro en la tabla recibos de pago
    MI_FECHALIMITE:= PCK_SYSMAN_UTL.FC_PAR( 
                                    UN_COMPANIA  => UN_COMPANIA   
                                   ,UN_NOMBRE    => 'FECHA LIMITE DE PAGO'
                                   ,UN_MODULO    => PCK_DATOS.MODULOPREDIAL 
                                   ,UN_FECHA_PAR => SYSDATE);
    IF MI_FECHALIMITE IS NULL 
       OR MI_FECHALIMITE = 'NA' 
       OR MI_FECHALIMITE = ' ' THEN
        BEGIN 
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBOSEXCEDENTES
                       ,UN_TABLAERROR => 'PARAMETRO'
                      );
        END;    
    END IF;

    IF UN_CODIGO_PREDIO IS NULL THEN
       BEGIN  
           RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
           PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE
                     ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBOSEXCEDENTE1
                     ,UN_TABLAERROR => MI_TABLA
                    );
       END;
    END IF;

    --'(TAR:1000065975; FECHA:07/09/2016; AUTOR:AA)
    --'Se incluyen controles para cuando hay más de un excedente para la misma vigencia, 
    --hay valores decimales, y error en el porcentaje de tarifas
    BEGIN 
        SELECT  COUNT(IP_PAGOSDOBLES.PREANO) CUENTADEPREANO       
          INTO MI_EXISTE
          FROM IP_PAGOSDOBLES   
         WHERE COMPANIA            = UN_COMPANIA
           AND PRECOD              = UN_CODIGO_PREDIO 
           AND IP_PAGOSDOBLES.TIPO = 'D' 
           AND IP_PAGOSDOBLES.PAGO IN (0)
         GROUP BY   IP_PAGOSDOBLES.PRECOD
                  , IP_PAGOSDOBLES.PREANO
                  , IP_PAGOSDOBLES.TIPO 
        HAVING COUNT(IP_PAGOSDOBLES.PREANO) > 1;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_EXISTE:=0;
    END;

    IF MI_EXISTE NOT IN (0) THEN
        MI_MENSERROR:= ' El predio tiene más de un excedente para la misma vigencia.'||CHR(10)||CHR(13);
    END IF;
    BEGIN
        SELECT   PRECOD
               , SUM(VALOR)-SUM(TRUNC(VALOR)) TOTALCONDEC
               , SUM(C1)-SUM(TRUNC(C1)) C1CONDEC
               , SUM(C2)-SUM(TRUNC(C2)) C2CONDEC
               , SUM(C3)-SUM(TRUNC(C3)) C3CONDEC
               , SUM(C4)-SUM(TRUNC(C4)) C4CONDEC
               , SUM(C13)-SUM(TRUNC(C13)) C13CONDEC
               , SUM(C14)-SUM(TRUNC(C14)) C14CONDEC
               , SUM(C15)-SUM(TRUNC(C15)) C15CONDEC
               , SUM(C16)-SUM(TRUNC(C16)) C16CONDEC
               , SUM(C17)-SUM(TRUNC(C17)) C17CONDEC
               , SUM(C18)-SUM(TRUNC(C18)) C18CONDEC
               , SUM(C19)-SUM(TRUNC(C19)) C19CONDEC
               , SUM(C20)-SUM(TRUNC(C20)) C20CONDEC
               , MAX(PREANO) MPREANO  
          INTO  MI_PRECOD
               ,MI_TOTALCONDEC
               ,MI_C1CONDEC
               ,MI_C2CONDEC
               ,MI_C3CONDEC
               ,MI_C4CONDEC
               ,MI_C13CONDEC
               ,MI_C14CONDEC
               ,MI_C15CONDEC
               ,MI_C16CONDEC
               ,MI_C17CONDEC
               ,MI_C18CONDEC
               ,MI_C19CONDEC
               ,MI_C20CONDEC
               ,MI_MPREANO
          FROM IP_PAGOSDOBLES 
         WHERE COMPANIA = UN_COMPANIA
           AND PRECOD   = UN_CODIGO_PREDIO 
           AND TIPO     = 'D'  
           AND PAGO     IN (0) 
         GROUP BY  PRECOD
                 , TIPO
                 , PAGO; 
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_PRECOD:=NULL;
        MI_TOTALCONDEC:=NULL;
        MI_C1CONDEC:=NULL;
        MI_C2CONDEC:=NULL;
        MI_C3CONDEC:=NULL;
        MI_C4CONDEC:=NULL;
        MI_C13CONDEC:=NULL;
        MI_C14CONDEC:=NULL;
        MI_C15CONDEC:=NULL;
        MI_C16CONDEC:=NULL;
        MI_C17CONDEC:=NULL;
        MI_C18CONDEC:=NULL;
        MI_C19CONDEC:=NULL;
        MI_C20CONDEC:=NULL;
        MI_MPREANO:=NULL;
    END;
    IF MI_PRECOD IS NOT NULL THEN
        IF MI_TOTALCONDEC NOT IN (0) THEN
            MI_MENSERROR:= MI_MENSERROR
                           ||' Valores decimales en el TOTAL del excedente '
                           ||CHR(10)||CHR(13);
        END IF;
        IF MI_C1CONDEC NOT IN (0) THEN
            MI_MENSERROR:= MI_MENSERROR
                           ||' Valores decimales en '
                           ||PCK_PREDIAL_COM4.FC_NOMBRE_CONCEPTO_PREDIAL(1, MI_MPREANO) 
                           ||' del excedente '||CHR(10)||CHR(13); 
        END IF;
        IF MI_C2CONDEC NOT IN (0) THEN
            MI_MENSERROR:= MI_MENSERROR||' Valores decimales en '
                           ||PCK_PREDIAL_COM4.FC_NOMBRE_CONCEPTO_PREDIAL(1, MI_MPREANO)
                           ||' del excedente '||CHR(10)||CHR(13);
        END IF;
        IF MI_C3CONDEC NOT IN (0) THEN
            MI_MENSERROR:= MI_MENSERROR||' Valores decimales en '
                           ||PCK_PREDIAL_COM4.FC_NOMBRE_CONCEPTO_PREDIAL(1, MI_MPREANO)
                           ||' del excedente '||CHR(10)||CHR(13);
        END IF;
        IF MI_C4CONDEC NOT IN (0) THEN
            MI_MENSERROR:= MI_MENSERROR||' Valores decimales en '
                           ||PCK_PREDIAL_COM4.FC_NOMBRE_CONCEPTO_PREDIAL(1, MI_MPREANO)
                           ||' del excedente '||CHR(10)||CHR(13);
        END IF;
        IF MI_C13CONDEC NOT IN (0) THEN
            MI_MENSERROR:= MI_MENSERROR||' Valores decimales en '
                           ||PCK_PREDIAL_COM4.FC_NOMBRE_CONCEPTO_PREDIAL(1, MI_MPREANO)
                           ||' del excedente '||CHR(10)||CHR(13);
        END IF;
        IF MI_C14CONDEC NOT IN (0) THEN
            MI_MENSERROR:= MI_MENSERROR||' Valores decimales en '
                           ||PCK_PREDIAL_COM4.FC_NOMBRE_CONCEPTO_PREDIAL(1, MI_MPREANO)
                           ||' del excedente '||CHR(10)||CHR(13);
        END IF;
        IF MI_C15CONDEC NOT IN (0) THEN
            MI_MENSERROR:= MI_MENSERROR||' Valores decimales en '
                           ||PCK_PREDIAL_COM4.FC_NOMBRE_CONCEPTO_PREDIAL(1, MI_MPREANO)
                           ||' del excedente '||CHR(10)||CHR(13);
        END IF;
        IF MI_C16CONDEC NOT IN (0) THEN
            MI_MENSERROR:= MI_MENSERROR||' Valores decimales en '
                           ||PCK_PREDIAL_COM4.FC_NOMBRE_CONCEPTO_PREDIAL(1, MI_MPREANO)
                           ||' del excedente '||CHR(10)||CHR(13);
        END IF;
        IF MI_C17CONDEC NOT IN (0) THEN
            MI_MENSERROR:= MI_MENSERROR||' Valores decimales en '
                           ||PCK_PREDIAL_COM4.FC_NOMBRE_CONCEPTO_PREDIAL(1, MI_MPREANO)
                           ||' del excedente '||CHR(10)||CHR(13);
        END IF;
        IF MI_C18CONDEC NOT IN (0) THEN
            MI_MENSERROR:= MI_MENSERROR||' Valores decimales en '
                           ||PCK_PREDIAL_COM4.FC_NOMBRE_CONCEPTO_PREDIAL(1, MI_MPREANO)
                           ||' del excedente '||CHR(10)||CHR(13);
        END IF;
        IF MI_C19CONDEC NOT IN (0) THEN
            MI_MENSERROR:= MI_MENSERROR||' Valores decimales en '
                           ||PCK_PREDIAL_COM4.FC_NOMBRE_CONCEPTO_PREDIAL(1, MI_MPREANO)
                           ||' del excedente '||CHR(10)||CHR(13);
        END IF;
        IF MI_C20CONDEC NOT IN (0) THEN
            MI_MENSERROR:= MI_MENSERROR||' Valores decimales en '
                           ||PCK_PREDIAL_COM4.FC_NOMBRE_CONCEPTO_PREDIAL(1, MI_MPREANO)
                           ||' del excedente '||CHR(10)||CHR(13);
        END IF;

        MI_TARIFA:= FC_TARIFAVIGENCIA(
                       UN_COMPANIA     => UN_COMPANIA
                      ,UN_PREDIO       => MI_PRECOD
                      ,UN_NUMERO_ORDEN => UN_NUMERO_ORDEN
                      ,UN_ANIO         => MI_MPREANO);
        IF MI_TARIFA IS NULL THEN 
            MI_MENSERROR:= MI_MENSERROR  
                           ||' El predio '||MI_PRECOD  
                           ||' no tiene porcentaje de tarifa asignado.'
                           ||' Revise por la ruta ARCHIVOS/ USUARIOS PREDIAL/ AVALUOS ANTERIORES '||CHR(10)||CHR(13);
        END IF;
    END IF;
    IF MI_MENSERROR IS NOT NULL THEN
        BEGIN 
            MI_REEMPLAZOS(0).CLAVE:='MI_MENSERROR0';
            MI_REEMPLAZOS(0).VALOR:=MI_MENSERROR;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBOSEXCEDENTE2
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS

                      );
        END;
    END IF;
    --nuevo
    BEGIN 
        SELECT COUNT(1)
          INTO MI_EXISTE
          FROM IP_RECIBO_EXCEDENTES;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_EXISTE :=0;
    END;
    IF MI_EXISTE NOT IN (0) THEN
        MI_TABLA  := 'IP_RECIBO_EXCEDENTES'; 
        MI_CAMPOS := '  ANULADO       = -1 
                      , DATE_MODIFIED = SYSDATE
                      , MODIFIED_BY   = '''||UN_USUARIO||''' ';
        MI_CONDICION:='     COMPANIA      = '''||UN_COMPANIA||''' 
                        AND CODIGO_PREDIO = '''||UN_CODIGO_PREDIO||''' 
                        AND PAGO          IN (0) 
                        AND ANULADO       IN (0)';
        BEGIN 
            BEGIN 
                MI_RTA:=PCK_DATOS.FC_ACME(
                                  UN_TABLA     => MI_TABLA
                                 ,UN_ACCION    => 'M' 
                                 ,UN_CAMPOS    => MI_CAMPOS
                                 ,UN_CONDICION => MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_CODIGO_PREDIO;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBOSEXCEDENTE3
                       ,UN_TABLAERROR => MI_TABLA
                      );
        END;
        MI_TABLA  := 'IP_RECIBOS_DE_PAGO'; 
        MI_CONDICION:='     COMPANIA  = '''||UN_COMPANIA||''' 
                        AND PRECOD    = '''||UN_CODIGO_PREDIO||''' 
                        AND PAGO      IN (0) 
                        AND ANULADO   IN (0)';
        BEGIN 
            BEGIN 
                MI_RTA:=PCK_DATOS.FC_ACME(
                                  UN_TABLA     => MI_TABLA
                                 ,UN_ACCION    => 'M' 
                                 ,UN_CAMPOS    => MI_CAMPOS
                                 ,UN_CONDICION => MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                MI_REEMPLAZOS(0).CLAVE:='PREDIO';
                MI_REEMPLAZOS(0).VALOR:=UN_CODIGO_PREDIO;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBOSEXCEDENTE3
                       ,UN_TABLAERROR => MI_TABLA
                      );
        END;
        --'(TAR:1000065468; FECHA:30/08/2016; AUTOR:AA)
        --'Se incluye la generación del consecutivo a partir de la función GenConsecutivoEx
        MI_NUEVAFACTURAEX:= 'EX'||PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                                'IP_RECIBO_EXCEDENTES'
                               ,'    COMPANIA = '''||UN_COMPANIA||''' 
                                 AND NUMERO_FACTURA IS NOT NULL'
                               ,'SUBSTR(NUMERO_FACTURA,3,9)');
    ELSE
        --'(TAR:1000065468; FECHA:30/08/2016; AUTOR:AA)
        --'Se modifica para que el consecutivo se genere con el valor por defecto así:
        --'Sigla = "EX", dos últimos digitos del año si es 2016 "16", 00001, así el cosecutivo inicial sería EX1600001
        MI_NUEVAFACTURAEX:= 'EX'||SUBSTR(EXTRACT(YEAR FROM SYSDATE),3,5)||'00001';
        --'(TAR:1000068366; FECHA:22/12/2016; AUTOR:AA)
        /*
        MI_NUEVAFACTURA:= PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(
                             UN_TABLA    => 'IP_RECIBO_EXCEDENTES'
                            ,UN_CRITERIO => '     COMPANIA = '''||UN_COMPANIA||'''
                                              AND NUMERO_FACTURA   IS NOT NULL'
                            ,UN_CAMPO    =>'NUMERO_FACTURA');*/
    END IF;

    BEGIN 
        BEGIN 
            MI_TABLA:='IP_NUMEROSDEFACTURA'; 
            MI_CAMPOS:= '     COMPANIA        = '''||UN_COMPANIA||''' 
                          AND CONSECUTIVOREAL = '''||PCK_SYSMAN_UTL.FC_STRZERO(MI_NUEVAFACTURA, 9)||''' '; --secuencia desc
            MI_CONDICION:= '  TIPO  = ''N'' '; 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            MI_REEMPLAZOS(0).CLAVE:='MI_NUEVAFACTURA';
            MI_REEMPLAZOS(0).VALOR:=PCK_SYSMAN_UTL.FC_STRZERO(MI_NUEVAFACTURA, 9);
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBOSEXCEDENTE4
                   ,UN_TABLAERROR => MI_TABLA
                  );
    END;
    --'(Cambio solicitado por HJPV y EPV para el proceso de abonos y excedentes; FECHA:26/07/2016)
	  --'Se conservó la misma consulta incluyendo los campos de los conceptos del 1-4 y del 13-20
    FOR MI_RS IN(
           SELECT  DOCNUM
                 , PRECOD
                 , PREANO
                 , VALOR
                 , TIPO
                 , OBSERVACIONES
                 , ANO_EXCEDENTE
                 , PAGO
                 , C1
                 , C2
                 , C3
                 , C4
                 , C13
                 , C14
                 , C15
                 , C16
                 , C17
                 , C18
                 , C19
                 , C20 
            FROM IP_PAGOSDOBLES  
           WHERE COMPANIA     = UN_COMPANIA
             AND NUMERO_ORDEN = UN_NUMERO_ORDEN
             AND PRECOD       = UN_CODIGO_PREDIO 
             AND TIPO         = 'D' 
             AND PAGO         IN (0))
    LOOP
        --'(Cambio solicitado por HJPV y EPV para el proceso de abonos y excedentes; FECHA:26/07/2016)
        --'(TAR:1000068366; FECHA:22/12/2016; AUTOR:AA)
        MI_TABLA := 'IP_RECIBO_EXCEDENTES';
        MI_CAMPOS:='  COMPANIA
                     ,NUMERO_ORDEN
                     ,NUMERO_FACTURA
                     ,PAGO
                     ,FECHA_EXPEDICION
                     ,ANULADO
                     ,CODIGO_PREDIO
                     ,NOMBRE
                     ,RESOLUCION
                     ,VALOR
                     ,ANO_CAUSO
                     ,ANO_EXCEDENTE
                     ,C1
                     ,C2
                     ,C3
                     ,C4
                     ,C13
                     ,C14
                     ,C15
                     ,C16
                     ,C17
                     ,C18
                     ,C19
                     ,C20
                     ,DOCNUM
                     ,DATE_CREATED
                     ,CREATED_BY';
        MI_VALORES:= ''''||UN_COMPANIA||'''
                     ,'''||UN_NUMERO_ORDEN||'''
                     ,'''||MI_NUEVAFACTURAEX||'''
                          ,0
                          ,SYSDATE
                          ,0
                     ,'''||UN_CODIGO_PREDIO||'''
                     ,'''||UN_NOMBRE||'''
                     ,'''||NVL(MI_RS.OBSERVACIONES, ' ')||'''
                     ,'''||MI_RS.VALOR||'''
                     ,'''||MI_RS.PREANO||'''
                     ,'''||MI_RS.ANO_EXCEDENTE||'''
                     ,'''||MI_RS.C1||'''
                     ,'''||MI_RS.C2||'''
                     ,'''||MI_RS.C3||'''
                     ,'''||MI_RS.C4||'''
                     ,'''||MI_RS.C13||'''
                     ,'''||MI_RS.C14||'''
                     ,'''||MI_RS.C15||'''
                     ,'''||MI_RS.C16||'''
                     ,'''||MI_RS.C17||'''
                     ,'''||MI_RS.C18||'''
                     ,'''||MI_RS.C19||'''
                     ,'''||MI_RS.C20||'''
                     ,'''||MI_RS.DOCNUM||'''
                     ,SYSDATE
                     ,'''||UN_USUARIO||''' ';
        BEGIN 
            BEGIN
                MI_RTA:= PCK_DATOS.FC_ACME( 
                                 UN_TABLA   => MI_TABLA
                                ,UN_ACCION  => 'I'
                                ,UN_CAMPOS  => MI_CAMPOS
                                ,UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                MI_REEMPLAZOS(0).CLAVE:='MI_NUEVAFACTURA';
                MI_REEMPLAZOS(0).VALOR:=MI_NUEVAFACTURA;
                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBOSEXCEDENTE5
                       ,UN_TABLAERROR => MI_TABLA
                       ,UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END; 
    END LOOP;
    --'(TAR:1000065723; FECHA:25/08/2016; AUTOR:AA)
    --'se cierra el recordset ya que hasta el momento siempre estaba quedanod abierto, además se realiza el insert
    --'en las tablas RECIBOS_DE_PAGO y DETALLE_RECIBOSPAGO
    --Me!TxtFactura = nuevafactura

    BEGIN 
        SELECT  AVALUO_ANO
               ,CODIGO               
          INTO MI_AVALUO_ANO
               ,MI_CODIGO_PREDIO
          FROM IP_USUARIOS_PREDIAL 
         WHERE CODIGO       = UN_CODIGO_PREDIO
           AND NUMERO_ORDEN = UN_NUMERO_ORDEN;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_AVALUO_ANO:=NULL;
        MI_CODIGO_PREDIO:=NULL;
    END;
    IF MI_CODIGO_PREDIO IS NULL THEN
        BEGIN 
            MI_REEMPLAZOS(0).CLAVE:='UN_CODIGO_PREDIO';
            MI_REEMPLAZOS(0).VALOR:=UN_CODIGO_PREDIO;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBOSEXCEDENTE6
                       ,UN_TABLAERROR => MI_TABLA
                      );
        END; 
    END IF;
    MI_TABLA:= 'IP_RECIBOS_DE_PAGO';
    MI_VALORES:='(COMPANIA
                  ,NUMERO_ORDEN
                  ,DOCNUM
                  ,PRECOD
                  ,AVALUO
                  ,PREANO
                  ,PREFECLIM
                  ,PREVAL
                  ,PREFEC
                  ,C1
                  ,C2
                  ,C3
                  ,C4
                  ,C5
                  ,C6
                  ,C7
                  ,C8
                  ,C9
                  ,C10
                  ,C11
                  ,C12
                  ,C13
                  ,C14
                  ,C15
                  ,C16
                  ,C17
                  ,C18
                  ,C19
                  ,C20
                  ,PREANOI
                  ,PREANOF
                  ,ESEXCEDENTE
                  ,CREATED_BY
                  ,DATE_CREATED)
            SELECT  
                     COMPANIA
                   , NUMERO_ORDEN
                   , NUMERO_FACTURA
                   , CODIGO_PREDIO
                   , '||MI_AVALUO_ANO||'
                   , MAX(ANO_CAUSO) PREANOF
                   , SYSDATE
                   , SUM(VALOR) TOTAL
                   , FECHA_EXPEDICION
                   , SUM(CASE WHEN ANO_CAUSO = EXTRACT (YEAR FROM FECHA_EXPEDICION)
                              THEN C1
                              ELSE 0
                         END) TC1
                   , SUM(CASE WHEN ANO_CAUSO = EXTRACT (YEAR FROM FECHA_EXPEDICION)
                              THEN C2
                              ELSE 0
                         END) TC2
                   , SUM(CASE WHEN ANO_CAUSO = EXTRACT (YEAR FROM FECHA_EXPEDICION)
                              THEN C3
                              ELSE 0
                         END) TC3
                   , SUM(CASE WHEN ANO_CAUSO = EXTRACT (YEAR FROM FECHA_EXPEDICION)
                              THEN C4
                              ELSE 0
                         END) TC4
                   , SUM(CASE WHEN ANO_CAUSO = EXTRACT (YEAR FROM FECHA_EXPEDICION)-1
                              THEN C1
                              ELSE 0
                         END) TC5
                   , SUM(CASE WHEN ANO_CAUSO = EXTRACT (YEAR FROM FECHA_EXPEDICION)-1
                              THEN C2
                              ELSE 0
                         END) TC6
                   , SUM(CASE WHEN ANO_CAUSO = EXTRACT (YEAR FROM FECHA_EXPEDICION)-1
                              THEN C3
                              ELSE 0
                         END) TC7
                   , SUM(CASE WHEN ANO_CAUSO = EXTRACT (YEAR FROM FECHA_EXPEDICION)-1
                              THEN C4
                              ELSE 0
                         END) TC8
                   , SUM(CASE WHEN ANO_CAUSO <= EXTRACT (YEAR FROM FECHA_EXPEDICION)-2
                              THEN C1
                              ELSE 0
                         END) TC9
                   , SUM(CASE WHEN ANO_CAUSO <= EXTRACT (YEAR FROM FECHA_EXPEDICION)-2
                              THEN C2
                              ELSE 0
                         END) TC10
                   , SUM(CASE WHEN ANO_CAUSO <= EXTRACT (YEAR FROM FECHA_EXPEDICION)-2
                              THEN C3
                              ELSE 0
                         END) TC11
                   , SUM(CASE WHEN ANO_CAUSO <= EXTRACT (YEAR FROM FECHA_EXPEDICION)-2
                              THEN C4
                              ELSE 0
                         END) TC12
                   , SUM(C13) TC13
                   , SUM(C14) TC14
                   , SUM(C15) TC15
                   , SUM(C16) TC16
                   , SUM(C17) TC17
                   , SUM(C18) TC18
                   , SUM(C19) TC19
                   , SUM(C20) TC20
                   , MIN(ANO_CAUSO) PREANOI
                   , MAX(ANO_CAUSO) FINAL
                   , -1
                   ,'''||UN_USUARIO||'''
                   ,SYSDATE
              FROM IP_RECIBO_EXCEDENTES 
             WHERE COMPANIA       = '''||UN_COMPANIA||'''
               AND NUMERO_FACTURA = '''||MI_NUEVAFACTURAEX||''' 
               AND CODIGO_PREDIO  = '''||UN_CODIGO_PREDIO||'''  
             GROUP BY   COMPANIA
                      , NUMERO_ORDEN
                      , NUMERO_FACTURA
                      , FECHA_EXPEDICION
                      , -1
                      , CODIGO_PREDIO
                      , NOMBRE
                      , DOCNUM';
    BEGIN
        BEGIN
            MI_RTA:=PCK_DATOS.FC_ACME(
                              UN_TABLA   => MI_TABLA
                             ,UN_ACCION  => 'IS'
                             ,UN_VALORES => MI_VALORES);                      
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            MI_REEMPLAZOS(0).CLAVE:='MI_NUEVAFACTURA';
            MI_REEMPLAZOS(0).VALOR:=MI_NUEVAFACTURA;
            MI_REEMPLAZOS(1).CLAVE:='UN_CODIGO_PREDIO';
            MI_REEMPLAZOS(1).VALOR:=UN_CODIGO_PREDIO;
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBOSEXCEDENTE7
                   ,UN_TABLAERROR => MI_TABLA
                   ,UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;

    BEGIN 
        SELECT COUNT(1),
               ANO_CAUSO
          INTO  MI_NUMANOS
               ,MI_ANOFIN  
          FROM IP_RECIBO_EXCEDENTES 
         WHERE COMPANIA       = UN_COMPANIA 
           AND NUMERO_FACTURA = MI_NUEVAFACTURA
         GROUP BY ANO_CAUSO  
         ORDER BY ANO_CAUSO DESC;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_NUMANOS:=NULL;
        MI_ANOFIN:=NULL;
    END;
    IF MI_NUMANOS IS NOT NULL THEN 
        FOR MI_RS IN (
              SELECT   COMPANIA
                     , DOCNUM
                     , NUMERO_FACTURA
                     , CODIGO_PREDIO
                     , ANO_CAUSO
                     , C1
                     , C2
                     , C3
                     , C4
                     , C13
                     , C14
                     , C15
                     , C16
                     , C17
                     , C18
                     , C19
                     , C20 
                FROM IP_RECIBO_EXCEDENTES 
               WHERE COMPANIA       = UN_COMPANIA 
                 AND NUMERO_FACTURA = MI_NUEVAFACTURAEX
               ORDER BY ANO_CAUSO DESC)
        LOOP                       
            BEGIN 
                SELECT 'X' 
                  INTO MI_EXISTE 
                  FROM IP_DETALLE_RECIBOPAGO 
                 WHERE COMPANIA = UN_COMPANIA
                   AND DOCNUM   = MI_NUEVAFACTURA 
                   AND PREANO   = MI_RS.ANO_CAUSO;
            EXCEPTION WHEN NO_DATA_FOUND THEN 
                MI_EXISTE:=NULL;
            END;
            IF MI_EXISTE IS NULL THEN
                MI_TABLA:='IP_DETALLE_RECIBOPAGO'; 
                MI_CAMPOS:='  COMPANIA
                            , DOCNUM
                            , CONSECUTIVO
                            , PREANO
                            , AVALUO
                            , TRPPOR
                            , C1
                            , C2
                            , C3
                            , C4
                            , C5
                            , C6
                            , C7
                            , C8
                            , C9
                            , C10
                            , C11
                            , C12
                            , C13
                            , C14
                            , C15
                            , C16
                            , C17
                            , C18
                            , C19
                            , C20
                            , TOTAL 
                            , DATE_CREATED
                            , CREATED_BY';
                MI_VALORES:=' '''||UN_COMPANIA||'''
                             ,'''||MI_NUEVAFACTURAEX||'''
                             ,  '||MI_NUMANOS||'
                             ,  '||MI_RS.ANO_CAUSO||'  
                              , '||FC_AVALUOVIGENCIA( 
                                      UN_COMPANIA => UN_COMPANIA                   
                                     ,UN_PREDIO   => MI_RS.CODIGO_PREDIO
                                     ,UN_NUMERO_ORDEN => UN_NUMERO_ORDEN
                                     ,UN_ANIO     => MI_RS.ano_causo)||'
                              , '||FC_TARIFAVIGENCIA(
                                      UN_COMPANIA => UN_COMPANIA 
                                     ,UN_PREDIO   => MI_RS.CODIGO_PREDIO
                                     ,UN_NUMERO_ORDEN => UN_NUMERO_ORDEN
                                     ,UN_ANIO         => MI_RS.ANO_CAUSO
                                    )||'
                              , '||MI_RS.C1||' 
                              , '||MI_RS.C2||'
                              , '||MI_RS.C3||'
                              , '||MI_RS.C4||'
                                 ,0
                                 ,0
                                 ,0
                                 ,0
                                 ,0
                                 ,0
                                 ,0
                                 ,0
                              , '||MI_RS.C13||'
                              , '||MI_RS.C14||'
                              , '||MI_RS.C15||'
                              , '||MI_RS.C16||'
                              , '||MI_RS.C17||'
                              , '||MI_RS.C18||'
                              , '||MI_RS.C19||'
                              , '||MI_RS.C20||'
                              , '||(MI_RS.C1 + MI_RS.C2 + MI_RS.C3 
                                   + MI_RS.C4 + MI_RS.C13 + MI_RS.C14 
                                   + MI_RS.C15 + MI_RS.C16 + MI_RS.C17 
                                   + MI_RS.C18 + MI_RS.C19 + MI_RS.C20)||'
                              , SYSDATE
                              , '''||UN_USUARIO||''' ';
                BEGIN
                  	BEGIN
                        MI_RTA:= PCK_DATOS.FC_ACME(
                        	               UN_TABLA   => MI_TABLA
                        	              ,UN_ACCION  => 'I'
                        	              ,UN_CAMPOS  => MI_CAMPOS
                        	              ,UN_VALORES => MI_VALORES);
                	  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                	  	  MI_REEMPLAZOS(0).CLAVE:='MI_NUEVAFACTURA';
                        MI_REEMPLAZOS(0).VALOR:=MI_NUEVAFACTURAEX;
                        MI_REEMPLAZOS(1).CLAVE:='UN_CODIGO_PREDIO';
                        MI_REEMPLAZOS(1).VALOR:=UN_CODIGO_PREDIO;
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                	  END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBOSEXCEDENTE8
                               ,UN_TABLAERROR => MI_TABLA
                              );
                END;

                IF MI_ANOFIN - 1 = MI_RS.ANO_CAUSO THEN
                    MI_TABLA:= 'IP_DETALLE_RECIBOPAGO'; 
                    MI_CAMPOS:= '  C5 = '||MI_RS.C1||
                                ', C6 = '||MI_RS.C2||
                                ', C7 = '||MI_RS.C3||
                                ', C8 = '||MI_RS.C4;
                    MI_CONDICION:='     COMPANIA = '''||UN_COMPANIA||'''
                                    AND DOCNUM   = '''||MI_NUEVAFACTURAEX||''' 
                                    AND PREANO   = '||MI_ANOFIN       
                                    ; 
                    BEGIN 
                        BEGIN 
                            MI_RTA:=PCK_DATOS.FC_ACME(
                                            UN_TABLA     => MI_TABLA
                                           ,UN_ACCION    => 'M' 
                                           ,UN_CAMPOS    => MI_CAMPOS
                                           ,UN_CONDICION => MI_CONDICION );
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            MI_REEMPLAZOS(0).CLAVE:='MI_NUEVAFACTURA';
                            MI_REEMPLAZOS(0).VALOR:=MI_NUEVAFACTURA;
                            MI_REEMPLAZOS(1).CLAVE:='UN_CODIGO_PREDIO';
                            MI_REEMPLAZOS(1).VALOR:=UN_CODIGO_PREDIO;
                            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBOSEXCEDENTE9
                                   ,UN_TABLAERROR => MI_TABLA
                                  );
                    END;
                END IF;

                IF MI_ANOFIN - 2 = MI_RS.ANO_CAUSO THEN
                    MI_TABLA:='IP_DETALLE_RECIBOPAGO';
                    MI_CAMPOS:='  C5 = '||MI_RS.C1||
                               ', C6 = '||MI_RS.C2||
                               ', C7 = '||MI_RS.C3||
                               ', C8 = '||MI_RS.C4;
                    MI_CONDICION:= '    COMPANIA = '''||UN_COMPANIA||'''
                                    AND DOCNUM   = '''||MI_NUEVAFACTURA||''' 
                                    AND PREANO   = '||MI_ANOFIN - 1;
                    BEGIN 
                        BEGIN
                            MI_RTA:=PCK_DATOS.FC_ACME(
                    	    	              UN_TABLA     => MI_TABLA
                    	    	             ,UN_ACCION    => 'M' 
                    	    	             ,UN_CAMPOS    => MI_CAMPOS
                    	    	             ,UN_CONDICION => MI_CONDICION );
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                          	MI_REEMPLAZOS(0).CLAVE:='MI_NUEVAFACTURA';
                            MI_REEMPLAZOS(0).VALOR:=MI_NUEVAFACTURA;
                            MI_REEMPLAZOS(1).CLAVE:='UN_CODIGO_PREDIO';
                            MI_REEMPLAZOS(1).VALOR:=UN_CODIGO_PREDIO;
                            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                        END;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                    	  PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBOSEXCEDENT10
                                   ,UN_TABLAERROR => MI_TABLA
                                  );
                    END;
                    BEGIN 
                        SELECT   SUM(C1) SUMADEC1
                               , SUM(C2) SUMADEC2
                               , SUM(C3) SUMADEC3
                               , SUM(C4) SUMADEC4 
                          INTO   MI_SUMADEC1
                               , MI_SUMADEC2
                               , MI_SUMADEC3
                               , MI_SUMADEC4
                          FROM IP_RECIBO_EXCEDENTES 
                         WHERE COMPANIA       =  UN_COMPANIA
                           AND NUMERO_FACTURA =  MI_NUEVAFACTURA
                           AND ANO_CAUSO      <= (MI_ANOFIN - 2);
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_SUMADEC1:=NULL;
                        MI_SUMADEC2:=NULL;
                        MI_SUMADEC3:=NULL;
                        MI_SUMADEC4:=NULL;
                    END;
                    IF MI_SUMADEC1 IS NOT NULL THEN
                        MI_TABLA:='IP_DETALLE_RECIBOPAGO'; 
                        MI_CAMPOS:='  C9  = '||MI_SUMADEC1||'
                                    , C10 = '||MI_SUMADEC2||' 
                                    , C11 = '||MI_SUMADEC3||' 
                                    , C12 = '||MI_SUMADEC4;
                        MI_CONDICION:='     COMPANIA = '''||UN_COMPANIA||'''
                                        AND DOCNUM   = '''||MI_NUEVAFACTURA||''' 
                                        AND PREANO   = '||MI_ANOFIN;
                        BEGIN 
                            BEGIN
                                MI_RTA:=PCK_DATOS.FC_ACME(
                                              UN_TABLA     => MI_TABLA
                                             ,UN_ACCION    => 'M' 
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION );
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                                MI_REEMPLAZOS(0).CLAVE:='MI_NUEVAFACTURA';
                                MI_REEMPLAZOS(0).VALOR:=MI_NUEVAFACTURA;
                                MI_REEMPLAZOS(1).CLAVE:='UN_CODIGO_PREDIO';
                                MI_REEMPLAZOS(1).VALOR:=UN_CODIGO_PREDIO;
                                RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
                            END;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBOSEXCEDENT11
                                   ,UN_TABLAERROR => MI_TABLA
                                  );
                        END;
                        MI_ANOFIN:= MI_ANOFIN - 1;
                    END IF;
                END IF;
            END IF;
            MI_NUMANOS:= MI_NUMANOS - 1;
        END LOOP;
    END IF;
    --'(TAR:1000065468; FECHA:30/08/2016; AUTOR:AA)
    --'Se actualiza el valor del parámetro CONSECUTIVO EXCEDENTES con el último generado
    MI_TABLA:='PARAMETRO';
    MI_CAMPOS:= ' VALOR = '''||MI_NUEVAFACTURAEX||''' ';
    MI_CONDICION:='     COMPANIA = '''||UN_COMPANIA||'''
                    AND NOMBRE   = ''CONSECUTIVO EXCEDENTES'' ';
    BEGIN 
        BEGIN
            MI_RTA:=PCK_DATOS.FC_ACME(
    	    	              UN_TABLA     => MI_TABLA
    	    	             ,UN_ACCION    => 'M' 
    	    	             ,UN_CAMPOS    => MI_CAMPOS
    	    	             ,UN_CONDICION => MI_CONDICION );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
        	RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN 
    	  PCK_ERR_MSG.RAISE_WITH_MSG(
                                    UN_EXC_COD    => SQLCODE
                                   ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBOSEXCEDENT12
                                   ,UN_TABLAERROR => MI_TABLA
                                  );
    END;
    RETURN MI_NUEVAFACTURAEX;
END FC_GENERARFACTURAEXCEDENTE;

--13
  PROCEDURE PR_IMPRESORAVIGENCIACAJA
  /*
      NAME              : FC_IMPRESORAVIGENCIACAJA 
      AUTHORS           : SYSMAN SAS
      AUTHOR MIGRACION  : AURA LILIANA MONROY GARCÍA
      DATE MIGRADOR     : 09/02/2017
      TIME              : 9:05 AM
      SOURCE MODULE     : PredialP2016.05.06
      DESCRIPTION       : Creación del recibo de caja para un predio
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     :                           
      PARAMETERS        : UN_COMPANIA       => Compañia de ingreso a la aplicación
                          UN_CODIGOPREDIAL  => Código del Predio del que se realizará el registro de pago en caja
                          UN_NUMEROORDEN	  => Número del propietario  
                          UN_AVALUOANO   	  => Año del avalúo
                          UN_FECHACORTE 	  => Fecha de corte 
                          UN_APLICALEY1066  => Indica si el predio aplica ley 1066
                          UN_APLICALEY1175  => Indica si el predio aplica ley 1175
                          UN_ANIOUNICO 		  => Indica si ha sido seleccionada la opción de año único
                          UN_ANIOFINAL 		  => Año a pagar seleccionado en el formulario 
                          UN_PAGOANO		    => Último año cancelado
                          UN_USUARIO    	  => Usuario que realiza la anulación de los recibos
                          UN_NOMPROPIETARIO => Nombre Propietario del Predio
                          UN_NITPROPFACT 	  => NIT del Propietario del Predio
                          UN_BANCO          => Código de la entidad bancaria donde se realizó el pago

    @NAME: imprimirVigenciaCaja  
    @METHOD: GET     
    */
    (
      UN_COMPANIA			    IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_CODIGOPREDIAL   	IN PCK_SUBTIPOS.TI_CODPREDIO, 
      UN_NUMEROORDEN 		  IN PCK_SUBTIPOS.TI_NUMORDEN,
      UN_AVALUOANO 		    IN PCK_SUBTIPOS.TI_DOBLE,
      UN_FECHACORTE 		  IN DATE,
      UN_APLICALEY1066    IN PCK_SUBTIPOS.TI_LOGICO, 
      UN_APLICALEY1175    IN PCK_SUBTIPOS.TI_LOGICO,  
      UN_ANIOUNICO 		    IN PCK_SUBTIPOS.TI_LOGICO,
      UN_ANIOFINAL 		    IN PCK_SUBTIPOS.TI_ANIO,
      UN_PAGOANO 			    IN PCK_SUBTIPOS.TI_ANIO,
      UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO,
      UN_NOMPROPIETARIO   IN IP_USUARIOS_PREDIAL.NOMBRE%TYPE,
      UN_NITPROPFACT      IN IP_USUARIOS_PREDIAL.NIT%TYPE,
      UN_BANCO			      IN IP_BANCOS.CODIGOBANCO%TYPE
    )
    AS
      MI_TRPRAN             IP_USUARIOS_PREDIAL.TRPRAN%TYPE;
      MI_STRSQL             PCK_SUBTIPOS.TI_STRSQL;	
      MI_ANIOCORTE 		      PCK_SUBTIPOS.TI_ANIO;		
      MI_APLICADESCESP      PCK_SUBTIPOS.TI_LOGICO := 0; -- Indica si se aplica descuento especial, en Access existe una función diferente para el tipo de check seleccionado	 
      MI_CALCULOLEY         VARCHAR2(2 CHAR);
      MI_BETWEENINI 		    PCK_SUBTIPOS.TI_ANIO;
      MI_RS 				        SYS_REFCURSOR;
      MI_CPTO               PCK_PREDIAL_COM3.TYPE_VECTORNUM;
      MI_TOTAL              PCK_SUBTIPOS.TI_DOBLE := 0;
      MI_CANT_CPTOS         PCK_SUBTIPOS.TI_DOBLE := 20;
      MI_FACPENDIENTES 	    VARCHAR2(500 CHAR);
      MI_AUXPENDIENTE       VARCHAR2(10 CHAR);
      MI_CONTROLARECS 	    VARCHAR2(2 CHAR);
      MI_NUMRECIBO 		      IP_NUMEROSDEFACTURA.CONSECUTIVOREAL%TYPE;
      MI_PREANO 			      PCK_SUBTIPOS.TI_ANIO;
      MI_DOCNUM			        IP_RECIBOS_DE_PAGO.DOCNUM%TYPE;
      MI_ANIOAPAGAR 		    PCK_SUBTIPOS.TI_ANIO;
      MI_DIGITOS 			      PCK_SUBTIPOS.TI_ENTERO;
      MI_PAGOREALIZADO 	 	  BOOLEAN;
      MI_TABLA              PCK_SUBTIPOS.TI_TABLA;  
      MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
      MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;
      MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;

    BEGIN
      MI_ANIOCORTE := EXTRACT(YEAR FROM UN_FECHACORTE);

      BEGIN 
        BEGIN
          MI_STRSQL := 'SELECT '||
                       '	   TARIFAS.TRPRAN' ||
                       ' FROM '||
                       ' 	   IP_USUARIOS_PREDIAL USUARIOS_PREDIAL '||
                       ' LEFT JOIN IP_TARIFAS TARIFAS' ||
                       '	 ON USUARIOS_PREDIAL.TRPCOD = TARIFAS.TRPCOD' ||
                       ' WHERE '||
                       '	    USUARIOS_PREDIAL.COMPANIA     =  '''||UN_COMPANIA|| ''''||
                       '  AND TARIFAS.TRPRAN1              <= '||UN_AVALUOANO||
                       '  AND TARIFAS.TRPRAN2              >= '||UN_AVALUOANO||
                       '  AND USUARIOS_PREDIAL.CODIGO       = ''' ||UN_CODIGOPREDIAL||''''||
                       '  AND USUARIOS_PREDIAL.NUMERO_ORDEN = '''||UN_NUMEROORDEN||''''||
                       '  AND TARIFAS.TRPANO                =  EXTRACT(YEAR FROM SYSDATE)';
          EXECUTE IMMEDIATE MI_STRSQL INTO MI_TRPRAN;	

          EXCEPTION WHEN NO_DATA_FOUND THEN 
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL; 	
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD 		=>	SQLCODE,
              UN_ERROR_COD	=>	PCK_ERRORES.ERRR_PREDIAL_TARIFA,
              UN_TABLAERROR =>	'IP_TARIFAS'
            );
      END;		 
    /*
      -------------------------------------------------------------------------------------
      Bloque de Código comentado debido a que se encuentra por definir esta funcionalidad 
      -------------------------------------------------------------------------------------

      BEGIN
        BEGIN   
          MI_CALCULOLEY := PCK_SYSMAN_UTL.FC_PAR (UN_COMPANIA  => UN_COMPANIA,
                                                  UN_NOMBRE    => 'CALCULO LEY 10/03/2015', 
                                                  UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL,
                                                  UN_FECHA_PAR => SYSDATE); 

            EXCEPTION WHEN NO_DATA_FOUND THEN 
                     RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;	
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
             UN_EXC_COD 	=>SQLCODE,
             UN_ERROR_COD	=>PCK_ERRORES.ERRR_PREDIAL_PARCALCULO
         ); 
      END; 

      IF UN_APLICALEY1175 AND MI_APLICADESCESP THEN
        ---  LLAMA FUNCION Calpred_Usuario ---
        IF MI_CALCULOLEY = 'SI' AND --LLAMA FUNCION validaPredioProy--
         THEN
          PCK_PREDIAL.FC_CALCULAR_PROYECCIONINICIAL(UN_COMPANIA   => UN_COMPANIA,
                                                    UN_CODPREDIO  => UN_CODIGOPREDIAL, 
                                                    UN_ANOINICIAL => 2014, --Pendiente: Definir el valor que debe ir en este parámetro
                                                    UN_ANOFINAL   => EXTRACT(YEAR FROM SYSDATE));

          PCK_PREDIAL.PR_PRIMERAJUSTE_PROYECCION (UN_COMPANIA            => UN_COMPANIA,   
                                                  UN_CODPREDIO           => UN_CODIGOPREDIAL,
                                                  UN_ANOINICIAL          =>  , --Pendiente: Definir el valor que debe ir en estos parámetros
                                                  UN_INDLOTES            =>  ,
                                                  UN_INDVERIFICAR_AVALUO =>  ,
                                                  UN_PROY_INICIAL        => );
        END IF;
      ELSE
        ---  lLAMA FUNCION Calpred_Usuario  lo diferente es el valor de un parámetro ---
        IF MI_CALCULOLEY = 'SI' AND ---LLAMA FUNCION validaPredioProy--
         THEN
          PCK_PREDIAL.FC_CALCULAR_PROYECCIONINICIAL(UN_COMPANIA   => UN_COMPANIA,
                                                    UN_CODPREDIO  => UN_CODIGOPREDIAL, 
                                                    UN_ANOINICIAL => 2014,
                                                    UN_ANOFINAL   => EXTRACT(YEAR FROM SYSDATE));

          PCK_PREDIAL.PR_PRIMERAJUSTE_PROYECCION (UN_COMPANIA            => UN_COMPANIA,   
                                                  UN_CODPREDIO           => UN_CODIGOPREDIAL,
                                                  UN_ANOINICIAL          =>  ,
                                                  UN_INDLOTES            =>  ,
                                                  UN_INDVERIFICAR_AVALUO =>  ,
                                                  UN_PROY_INICIAL        => );
        END IF;		

      END IF;
    */

      MI_BETWEENINI := CASE WHEN UN_ANIOUNICO = -1 THEN UN_ANIOFINAL ELSE NVL(UN_PAGOANO, 1970) END;
      MI_STRSQL 	  := ' SELECT   '||
                               ' SUM(CASE WHEN PREANO  =  '||MI_ANIOCORTE||'     THEN C1 ELSE 0 END)  CC1, '||
                               ' SUM(CASE WHEN PREANO  =  '||MI_ANIOCORTE||'     THEN C2 ELSE 0 END)  CC2, '||
                               ' SUM(CASE WHEN PREANO  =  '||MI_ANIOCORTE||'     THEN C3 ELSE 0 END)  CC3, '||
                               ' SUM(CASE WHEN PREANO  =  '||MI_ANIOCORTE||'     THEN C4 ELSE 0 END)  CC4, '||
                               ' SUM(CASE WHEN PREANO  =  '||MI_ANIOCORTE||' - 1 THEN C1 ELSE 0 END)  CC5, '||
                               ' SUM(CASE WHEN PREANO  =  '||MI_ANIOCORTE||' - 1 THEN C2 ELSE 0 END)  CC6, '||
                               ' SUM(CASE WHEN PREANO  =  '||MI_ANIOCORTE||' - 1 THEN C3 ELSE 0 END)  CC7, '||
                               ' SUM(CASE WHEN PREANO  =  '||MI_ANIOCORTE||' - 1 THEN C4 ELSE 0 END)  CC8, '||
                               ' SUM(CASE WHEN PREANO <=  '||MI_ANIOCORTE||' - 2 THEN C1 ELSE 0 END)  CC9, '||
                               ' SUM(CASE WHEN PREANO <=  '||MI_ANIOCORTE||' - 2 THEN C2 ELSE 0 END)  CC10, '||
                               ' SUM(CASE WHEN PREANO <=  '||MI_ANIOCORTE||' - 2 THEN C3 ELSE 0 END)  CC11, '||
                               ' SUM(CASE WHEN PREANO <=  '||MI_ANIOCORTE||' - 2 THEN C4 ELSE 0 END)  CC12, '||
                               ' SUM(C13) CC13, '||
                               ' SUM(C14) CC14, '||
                               ' SUM(C15) CC15, '||
                               ' SUM(C16) CC16, '||
                               ' SUM(C17) CC17, '||
                               ' SUM(C18) CC18, '||
                               ' SUM(C19) CC19, '||
                               ' SUM(C20) CC20  '||
                        ' FROM  '||
                        '      IP_FACTURADOS '||
                        ' WHERE   '||
                        '      COMPANIA      = ''' || UN_COMPANIA ||''' '||
                        '  AND CODIGO        = ''' || UN_CODIGOPREDIAL ||''' '||
                        '  AND NUMERO_ORDEN  = ''001'' '||    
                        '  AND PREANO        BETWEEN '||MI_BETWEENINI||' AND '||UN_ANIOFINAL||' '||
                        '  AND PAGADO        IN (0) '||
                        '  AND INDEXE        IN (0) '||
                        '  AND NOCOBRADO     IN (0) '||
                        '  AND INDPAGO_ACPAG IN (0) ';

      OPEN MI_RS FOR MI_STRSQL;
        LOOP
          FETCH MI_RS INTO MI_CPTO(1), MI_CPTO(2), MI_CPTO(3), MI_CPTO(4), MI_CPTO(5), 
                           MI_CPTO(6), MI_CPTO(7), MI_CPTO(8), MI_CPTO(9), MI_CPTO(10), 
                           MI_CPTO(11), MI_CPTO(12), MI_CPTO(13), MI_CPTO(14), MI_CPTO(15),
                           MI_CPTO(16), MI_CPTO(17), MI_CPTO(18), MI_CPTO(19), MI_CPTO(20);
          EXIT WHEN MI_RS%NOTFOUND;
            <<CALCULO_CONCEPTOS>>
            FOR I IN 1 .. MI_CANT_CPTOS LOOP
              IF MI_CPTO(I) IS NOT NULL THEN
                  MI_TOTAL := MI_TOTAL + MI_CPTO(I);
              ELSE
                  MI_CPTO(I) := 0;
                  MI_TOTAL   := MI_TOTAL + 0;
              END IF;
            END LOOP CALCULO_CONCEPTOS;      
        END LOOP;
      CLOSE MI_RS;

     -- Asigna a la variable MI_FACPENDIENTES las facturas pendientes por pagar en el predio
      MI_STRSQL :=  ' SELECT '||
                    '       DOCNUM  '||
                    '  FROM  '||
                    '       IP_RECIBOS_DE_PAGO '||
                    ' WHERE  '||
                    '       PRECOD   =  TRIM('''||UN_CODIGOPREDIAL||''') '||
                    '   AND (CASE WHEN ANULADO IS NULL THEN 0 ELSE ANULADO END) IN(0) '||
                    '   AND (CASE WHEN PAGO IS NULL THEN 0 ELSE PAGO END) IN(0) '; 

      OPEN MI_RS FOR MI_STRSQL;
        LOOP
          FETCH MI_RS INTO MI_AUXPENDIENTE;
          EXIT WHEN MI_RS%NOTFOUND;
            IF MI_FACPENDIENTES IS NULL THEN
              MI_FACPENDIENTES := MI_AUXPENDIENTE;    
            ELSE         
              MI_FACPENDIENTES := MI_AUXPENDIENTE || ', '|| MI_FACPENDIENTES ;    
            END IF;
        END LOOP; 
      CLOSE MI_RS; 	


        IF MI_FACPENDIENTES IS NOT NULL THEN 
          -- Consulta el valor del parámetro "CONTROLAR RECIBOS POR USUARIO"
          BEGIN
            BEGIN   
              MI_CONTROLARECS := PCK_SYSMAN_UTL.FC_PAR (UN_COMPANIA  => UN_COMPANIA,
                                    UN_NOMBRE    => 'CONTROLAR RECIBOS POR USUARIO', 
                                    UN_MODULO    => PCK_DATOS.FC_MODULOPREDIAL,
                                    UN_FECHA_PAR => SYSDATE); 

                EXCEPTION WHEN NO_DATA_FOUND THEN 
                         RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;	
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
                PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_EXC_COD 	=>SQLCODE,
                 UN_ERROR_COD	=>PCK_ERRORES.ERRR_PREDIAL_PARCONTROLA
             ); 
          END; 

          -- Anulación de recibos pendientes
          IF MI_CONTROLARECS = 'SI' THEN
              BEGIN
                  BEGIN
                      MI_TABLA     := 'IP_RECIBOS_DE_PAGO';
                      MI_CAMPOS    := ' ANULADO          = -1, '||
                                      ' FECHAANULACION   = TRUNC(SYSDATE), '||
                                      ' PAGO             = 0, '||
                                      ' ANULADO_POR      = '''||UN_USUARIO||''',  '||
                                      ' MODIFIED_BY      = '''||UN_USUARIO||''', '||
                                      ' DATE_MODIFIED    = SYSDATE ';
                      MI_CONDICION := '     COMPANIA         = ''' || UN_COMPANIA || ''''||
                                      ' AND PRECOD           = TRIM(''' || UN_CODIGOPREDIAL ||''')';
                      MI_RTA       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                                         UN_ACCION    => 'M', 
                                                         UN_CAMPOS    => MI_CAMPOS, 
                                                         UN_CONDICION => MI_CONDICION); 
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                               RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                        
                  END;
                      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                        MI_MSGERROR(1).CLAVE := 'RECIBOS';
                        MI_MSGERROR(1).VALOR :=  MI_FACPENDIENTES;
                        MI_MSGERROR(2).CLAVE := 'CODIGO';
                        MI_MSGERROR(2).VALOR :=  UN_CODIGOPREDIAL;	              
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD      =>  SQLCODE,
                          UN_ERROR_COD    =>  PCK_ERRORES.ERRR_PREDIAL_ACTRECS,
                          UN_TABLAERROR   =>  'IP_RECIBOS_DE_PAGO',
                          UN_REEMPLAZOS   =>  MI_MSGERROR
                        );
              END;			
          END IF;
        END IF;


      -- ETAPA IMPRIMIR
      -- Etapa 1: Consecutivo del recibo
      MI_NUMRECIBO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => 'IP_NUMEROSDEFACTURA',
                                                       UN_CRITERIO => '	  COMPANIA = ''' || UN_COMPANIA || ''' '||
                                                                      ' AND TIPO     IN (''N'') '||
                                                                      ' AND ACTIVO   IN (-1) ',
                                                       UN_CAMPO    => 'CONSECUTIVOREAL',
                                                       UN_INICIAL  => '000000001')+1;

      -- Consulta el número de digitos para el tipo de factura Normal(N)
      BEGIN
        SELECT DIGITOS 
          INTO MI_DIGITOS
          FROM IP_TIPOSNUMERACION 
         WHERE CODIGO IN ('N');

      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_DIGITOS := 9;
      END;

      MI_DOCNUM 	 := LPAD(''||MI_NUMRECIBO||'',MI_DIGITOS,0);      		

      -- Etapa 2: 
      SELECT MIN(PREANO)
        INTO MI_PREANO
        FROM IP_FACTURADOS  
       WHERE CODIGO        = UN_CODIGOPREDIAL
         AND INDPAGO_ACPAG IN(0) 
         AND PAGADO        IN(0);

      -- Adición de nuevo recibo en caja
      BEGIN
          BEGIN
              MI_CAMPOS	:=	' COMPANIA, '|| 
                            ' PRECOD, '|| 
                            ' NUMERO_ORDEN, '|| 
                            ' PREANO, '||
                            ' PREFEC, '||
                            ' DOCNUM, '||
                            ' PREVAL, '||
                            ' PREFECLIM, '||
                            ' C1, C2, C3, C4, C5, '||
                            ' C6, C7, C8, C9, C10, '|| 
                            ' C11, C12, C13, C14, C15, '|| 
                            ' C16, C17, C18, C19, C20, '|| 
                            ' PREANOI, '||
                            ' PREANOF, '||
                            ' UNICO_ANO, '||
                            ' APLICALEY1066, '||
                            ' APLICALEY1175, '||
                            ' APLICADESCESP, '||
                            ' PREUSU, '||
                            ' IND_MULTIFECHAS, '||
                            ' NOMBREPROPFAC, '||
                            ' NITPROPFAC, '||
                            ' ORDENPROPFAC, '||
                            ' CREATED_BY, '||
                            ' DATE_CREATED '; 
            MI_VALORES 	:=  '''' || UN_COMPANIA || ''', '||
                            '''' || UN_CODIGOPREDIAL || ''', '||
                            '''' || UN_NUMEROORDEN || ''', '||
                            '''' || UN_ANIOFINAL || ''', '||
                            'TO_DATE (''' || UN_FECHACORTE || ''',''DD/MM/YYYY HH24:MI:SS''),' ||   
                            '''' || MI_DOCNUM ||''', '||
                            '' || MI_TOTAL ||', '||
                            ' LAST_DAY( TO_DATE ('''|| UN_FECHACORTE ||''',''DD/MM/YYYY HH24:MI:SS'')), '||
                            '' || MI_CPTO(1) || ', ' || MI_CPTO(2) || ', ' || MI_CPTO(3) || ', ' || MI_CPTO(4) || ', ' || MI_CPTO(5) || ' , '||
                            '' || MI_CPTO(6) || ', ' || MI_CPTO(7) || ', ' || MI_CPTO(8) || ', ' || MI_CPTO(9) || ', ' || MI_CPTO(10) || ' , '||
                            '' || MI_CPTO(11) || ', ' || MI_CPTO(12) || ', ' || MI_CPTO(13) || ', ' || MI_CPTO(14) || ', ' || MI_CPTO(15) || ', '||
                            '' || MI_CPTO(16) || ', ' || MI_CPTO(17) || ', ' || MI_CPTO(18) || ', ' || MI_CPTO(19) || ', ' || MI_CPTO(20) || ', '||
                            ' CASE WHEN '|| UN_ANIOUNICO ||' IN (0) THEN '|| MI_PREANO ||' ELSE '||UN_ANIOFINAL ||' END , ' ||
                            ' '|| UN_ANIOFINAL ||', ' ||
                            ' '|| UN_ANIOUNICO ||', '||
                            ' 0, '||
                            ' '|| UN_APLICALEY1175 ||', '||
                            ' 0, '|| -- OJO: Valor APLICADESCESP se está enviando quemado pero es un valor calculado
                            ' '''|| UN_USUARIO ||''', '||
                            ' 0, '||
                            ' '''|| UN_NOMPROPIETARIO ||''', '|| 
                            ' '''|| UN_NITPROPFACT ||''', '||
                            ' '''|| UN_NUMEROORDEN ||''', '||
                            ' '''|| UN_USUARIO ||''', '||
                            ' SYSDATE';
            MI_RTA 		 :=   PCK_DATOS.FC_ACME(UN_TABLA      => 'IP_RECIBOS_DE_PAGO',
                                              UN_ACCION     => 'I', 
                                              UN_CAMPOS     => MI_CAMPOS,
                                              UN_VALORES    => MI_VALORES);

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                        RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                         

          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            MI_MSGERROR(1).CLAVE := 'NUMERORECIBO';
            MI_MSGERROR(1).VALOR :=  MI_DOCNUM;     		
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                         UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSREC,
                                         UN_TABLAERROR => 'IP_RECIBOS_DE_PAGO',
                                         UN_REEMPLAZOS => MI_MSGERROR);
        END ;


        -- Etapa 3: Creación detalles de factura
        PCK_PREDIAL.PR_CREARDETALLE_FACTURA(UN_COMPANIA      => UN_COMPANIA,
                                            UN_CODPREDIO     => UN_CODIGOPREDIAL,
                                            UN_NUMORDEN      => UN_NUMEROORDEN,
                                            UN_DOCNUM        => MI_DOCNUM,
                                            UN_TIPOFRA       => 'N',
                                            UN_MENORVIGENCIA => UN_ANIOFINAL,
                                            UN_MAYORVIGENCIA => MI_ANIOCORTE,
                                            UN_USUARIO  	   => UN_USUARIO);
        -- Etapa 4:
        -- Actualizar información en la tabla Usuario Predial
        BEGIN
          BEGIN
            MI_TABLA     := 'IP_USUARIOS_PREDIAL';
            MI_CAMPOS    := 'NUMERO_FACTURA    = ''' || MI_DOCNUM || ''', '||
                            'RECIBO_ACTUAL     = ''' || MI_DOCNUM || ''', '||
                            'PAGO_ANO2 		     = PAGO_ANO1, '||
                            'PAG_VAL2 		     = PAG_VAL1, '||
                            'PAG_FEC2 		     = PAG_FEC1, '||
                            'NUM_COM2 		     = NUM_COM1,'||
                            'MODIFIED_BY       ='''||UN_USUARIO||''','||
                            'DATE_MODIFIED     =SYSDATE';
            MI_CONDICION := 'COMPANIA          = ''' || UN_COMPANIA || ''' '||
                            ' AND CODIGO       = ''' || UN_CODIGOPREDIAL || ''''||
                            ' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''';
            MI_RTA       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                               UN_ACCION    => 'M', 
                                               UN_CAMPOS    => MI_CAMPOS, 
                                               UN_CONDICION => MI_CONDICION);     
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                                                                                              
          END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            MI_MSGERROR(1).CLAVE := 'CODIGO';
                MI_MSGERROR(1).VALOR := UN_CODIGOPREDIAL;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD      =>  SQLCODE,
                      UN_ERROR_COD    =>  PCK_ERRORES.ERRR_PREDIAL_ACTUSUARIO,
                      UN_TABLAERROR   =>  'IP_USUARIOS_PREDIAL',
                      UN_REEMPLAZOS   =>  MI_MSGERROR
                    );
        END;  

        BEGIN
          BEGIN
            MI_TABLA     := 'IP_USUARIOS_PREDIAL';
            MI_CAMPOS    := 'PAGO_ANO1 		   = PAGO_ANO, '|| 
                            'PAG_VAL1   	   = PAG_VAL, '|| 
                            'PAG_FEC1  		   = PAG_FEC, '|| 
                            'NUM_COM1  		   = NUM_COM, '||
                            'MODIFIED_BY     ='''||UN_USUARIO||''','||
                            'DATE_MODIFIED   =SYSDATE';
            MI_CONDICION := 'COMPANIA          = ''' || UN_COMPANIA || ''' '||
                            ' AND CODIGO       = ''' || UN_CODIGOPREDIAL || ''''||
                            ' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''';
            MI_RTA       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                               UN_ACCION    => 'M', 
                                               UN_CAMPOS    => MI_CAMPOS, 
                                               UN_CONDICION => MI_CONDICION);     
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                                                                                              
          END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              MI_MSGERROR(1).CLAVE := 'CODIGO';
              MI_MSGERROR(1).VALOR := UN_CODIGOPREDIAL;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD      =>  SQLCODE,
                      UN_ERROR_COD    =>  PCK_ERRORES.ERRR_PREDIAL_ACTUSUARIO,
                      UN_TABLAERROR   =>  'IP_USUARIOS_PREDIAL',
                      UN_REEMPLAZOS   =>  MI_MSGERROR
                    );
        END;  		

        -- Guardar en usuario_predial la condición de unico_ano_a_pagar para cuando esta activa la casilla unico_ano
        MI_ANIOAPAGAR := CASE WHEN UN_ANIOUNICO IN(-1) THEN UN_ANIOFINAL ELSE NULL END;
        BEGIN
          BEGIN
            MI_TABLA     := 'IP_USUARIOS_PREDIAL';
            MI_CAMPOS    := 'UNICO_ANO_A_PAGAR    = ''' || MI_ANIOAPAGAR || ''','||
                            'MODIFIED_BY          ='''||UN_USUARIO||''','||
                            'DATE_MODIFIED        =SYSDATE';
            MI_CONDICION := 'COMPANIA          = ''' || UN_COMPANIA || ''' '||
                            ' AND CODIGO       = ''' || UN_CODIGOPREDIAL || ''''||
                            ' AND NUMERO_ORDEN = ''' || UN_NUMEROORDEN || '''';
            MI_RTA       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                             UN_ACCION    => 'M', 
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION);     
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                                                                                              
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_MSGERROR(1).CLAVE := 'CODIGO';
          MI_MSGERROR(1).VALOR := UN_CODIGOPREDIAL;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD      =>  SQLCODE,
                      UN_ERROR_COD    =>  PCK_ERRORES.ERRR_PREDIAL_ACTUSUANIO,
                      UN_TABLAERROR   =>  'IP_USUARIOS_PREDIAL',
                      UN_REEMPLAZOS   =>  MI_MSGERROR
                    );
        END;  	

      -- Etapa 5 : Actualiza el valor del consecutivo en IP_NUMEROSDEFACTURA
      BEGIN
        BEGIN
          MI_TABLA     := 'IP_NUMEROSDEFACTURA';
          MI_CAMPOS    := 'CONSECUTIVOREAL = ''' || MI_DOCNUM || ''','||
                          'MODIFIED_BY     ='''||UN_USUARIO||''','||
                          'DATE_MODIFIED   =SYSDATE';
          MI_CONDICION := '	COMPANIA     = ''' || UN_COMPANIA || ''' '||
                          ' AND TIPO     IN (''N'') '||
                          ' AND ACTIVO   IN (-1) ';
          MI_RTA       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                             UN_ACCION    => 'M', 
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION);     
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                                                                                              
        END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD      =>  SQLCODE,
                    UN_ERROR_COD    =>  PCK_ERRORES.ERRR_PREDIAL_ACTNUMFACT,
                    UN_TABLAERROR   =>  'IP_NUMEROSDEFACTURA'
                  );
      END;  

      BEGIN
        BEGIN
          MI_PAGOREALIZADO := PCK_PREDIAL_COM4.FC_REALIZARPAGOCAJA (UN_COMPANIA           => UN_COMPANIA,
                                                                    UN_CODIGOPREDIAL      => UN_CODIGOPREDIAL,
                                                                    UN_NUMEROORDEN        => UN_NUMEROORDEN,
                                                                    UN_ANIOUNICO          => UN_ANIOUNICO,
                                                                    UN_BANCO              => UN_BANCO,
                                                                    UN_USUARIO            => UN_USUARIO,
                                                                    UN_APLICALEY1066      => UN_APLICALEY1066, 
                                                                    UN_CPTO               => MI_CPTO,
                                                                    UN_ANIOFINAL          => UN_ANIOFINAL,
                                                                    UN_PAGOANO            => UN_PAGOANO,
                                                                    UN_NUMRECIBO          => MI_NUMRECIBO,
                                                                    UN_DOCNUM             => MI_DOCNUM);
          IF NOT MI_PAGOREALIZADO THEN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END IF;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD      =>  SQLCODE,
                                      UN_ERROR_COD    =>  PCK_ERRORES.ERRR_PREDIAL_PAGOREALIZADO);		
      END;      

    END PR_IMPRESORAVIGENCIACAJA;	


-- 14
    PROCEDURE PR_INCAUTARPREDIO
    /*
        NAME              : PR_INCAUTARPREDIO 
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : SERGIO ESTEBAN PIÑA VARGAS
        DATE MIGRADOR     : 14/02/2017
        TIME              : 05:23 AM
        SOURCE MODULE     : PredialP2016.05.06
        DESCRIPTION       : Paso de lógica de negocio a PL
        MODIFIER          : AURA LILIANA MONROY GARCIA
        DATE MODIFIED     : 30/06/2017 
        TIME MODIFIED     : 05:50 PM
        MODIFICATIONS     : Se adicionan los campos de auditoría para realizar la actualización                          
        PARAMETERS        :

      @NAME:   incautarPredio
      @METHOD: POST     
      */
  (    
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGOPRED         IN PCK_SUBTIPOS.TI_CODPREDIO,
    UN_CODIGOUSUARIO      IN VARCHAR2,
    UN_OBSERVACION        IN VARCHAR2,
    UN_OPCION             IN PCK_SUBTIPOS.TI_LOGICO,
    UN_CAMPOS_AUX         IN PCK_SUBTIPOS.TI_CAMPOS,
    UN_NORESOLUCION       IN VARCHAR2
  ) 
  AS 
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_OPCION           NUMBER;
  BEGIN

    BEGIN
      MI_CAMPOS := ' '||UN_CAMPOS_AUX||' 
                 , IP_USUARIOS_PREDIAL.OBSERVACIO             = '''||UN_OBSERVACION||'''
                 , IP_USUARIOS_PREDIAL.RESOLUCION_INCAUTA     = '''||UN_NORESOLUCION||''' 
                 , DATE_MODIFIED                              = SYSDATE
                 , MODIFIED_BY                                = ''' || UN_CODIGOUSUARIO || ''' ';

      MI_CONDICION := 'IP_USUARIOS_PREDIAL.COMPANIA   = '''||UN_COMPANIA||''' 
                       AND IP_USUARIOS_PREDIAL.CODIGO = '''||UN_CODIGOPRED||''' ';
      BEGIN
        PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'IP_USUARIOS_PREDIAL',
                                             UN_ACCION    => 'M',
                                             UN_CAMPOS    => MI_CAMPOS,
                                             UN_CONDICION => MI_CONDICION);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
      END;

      BEGIN
        IF UN_OPCION = 1 THEN 
          MI_OPCION := -1;
        ELSE
          MI_OPCION := 0;
        END IF;

         PCK_PREDIAL.PR_AUDITORIA(
              UN_COMPANIA       => ''||UN_COMPANIA||'',
              UN_CODMOD         => ''||UN_CODIGOPRED||'',
              UN_OPEMOD         => ''||UN_CODIGOUSUARIO||'',
              UN_CCOMOD         => '250',
              UN_VANMOD         => ''||MI_OPCION||'',
              UN_VNUMOD         => '-1',
              UN_DESCRIPCION    => ''||UN_OBSERVACION||''
         );
      END;  

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN    
      PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE, 
                                 UN_ERROR_COD   => PCK_ERRORES.ERRR_PREDIAL_ACTU_INCAUTAR);  
    END;

  END PR_INCAUTARPREDIO;

--15

FUNCTION FC_NOMBRE_CONCEPTO_PREDIAL
/*
    NAME              : FC_NOMBRE_CONCEPTO_PREDIAL  --> EN ACCESS nombre_concepto
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS 
    DATE MIGRADOR     : 15/02/2017
    TIME              : 05:30 PM 
    SOURCE MODULE     : PredialP2017.01.06VB.accdb
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : retorna el nombre del concepto creado para ese año.                       
    @NAME             : getNombreConceptoPredial
    @METHOD           : GET
*/
(
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CODIGO   IN PCK_SUBTIPOS.TI_ENTERO, 
    UN_ANIO     IN PCK_SUBTIPOS.TI_ANIO   DEFAULT NULL
) RETURN VARCHAR2
AS
    MI_ANIO   PCK_SUBTIPOS.TI_ANIO;
    MI_NOMBRE IP_CONCEPTOS.NOMBRE%TYPE;
BEGIN 
   MI_ANIO :=UN_ANIO;
   IF UN_ANIO IS NULL THEN 
      MI_ANIO:= EXTRACT (YEAR FROM SYSDATE);
   END IF;
   BEGIN 
       SELECT NOMBRE 
         INTO MI_NOMBRE 
         FROM IP_CONCEPTOS 
        WHERE COMPANIA = UN_COMPANIA
          AND ANO      = MI_ANIO
          AND CODIGO   = UN_CODIGO;
   EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_NOMBRE := NULL;
   END;
   IF MI_NOMBRE IS NULL THEN
      MI_NOMBRE:= 'Concepto NO creado.';
   ELSE
      MI_NOMBRE:= NVL(MI_NOMBRE, '');
   END IF;
   RETURN MI_NOMBRE;
END FC_NOMBRE_CONCEPTO_PREDIAL;
  --16
  FUNCTION FC_ARMACONSULTA_MOROSOS_PRD
  (
    /*
      NAME              : FC_ARMACONSULTA_MOROSOS_PRD --> EN ACCESS ValidarDatos 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MIGRADOR     : 14/02/2017
      TIME              : 02:00 PM 
      SOURCE MODULE     : PredialP2017.01.06VB.accdb
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : VALIDA LOS CAMPOS DE LAS VIGENCIAS Y ARMA LA CONSULTA PARA EL INFORME DE CARTERA MOROSA
      PARAMETERS        : UN_COMPANIA      => COMPANIA ACTUAL.
                          UN_ANIO_INICIAL  => NUMERO DEL ANIO INICIAL DE LA VIGENCIA.
                          UN_ANIO_FINAL    => NUMERO DEL ANIO FINAL DE LA VIGENCIA.
                          UN_CHECKACUERDOS => VALOR DEL CHECK DEL FORMULARIO.
      @NAME             : armarConsultaMorososPredial
      @METHOD           : GET
    */
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO_INICIAL   IN PCK_SUBTIPOS.TI_ANIO,
    UN_ANIO_FINAL     IN PCK_SUBTIPOS.TI_ANIO,
    UN_CHECKACUERDOS  IN PCK_SUBTIPOS.TI_LOGICO
  ) 
  RETURN VARCHAR2
  AS
    MI_CONCEPTOS      VARCHAR2(1000 CHAR);
    MI_CONCEPTO       VARCHAR2(1000 CHAR);
    MI_POS            PCK_SUBTIPOS.TI_ENTERO;
    MI_STRSQLFROM     PCK_SUBTIPOS.TI_STRSQL;
    MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
    MI_CODRURALES     VARCHAR2(1000 CHAR);

  BEGIN  
    IF NVL(UN_ANIO_INICIAL, 0) = 0 OR NVL(UN_ANIO_FINAL, 0) = 0 THEN
      BEGIN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_CONSMORPRD_CERO
          );
      END;
    ELSE
      IF UN_ANIO_FINAL < UN_ANIO_INICIAL THEN
        BEGIN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD =>SQLCODE,
              UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_CONSMORPRD_MENOR
            );
        END;
      END IF;
    END IF;

    MI_CODRURALES := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                           UN_NOMBRE    => 'CODIGOS PREDIOS RURALES',
                                           UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                           UN_FECHA_PAR => SYSDATE);

    IF SUBSTR(MI_CODRURALES, 1, 1) <> '0' THEN
      BEGIN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_CONSMORPRD_CODRUR
          );
      END;
    ELSE
      MI_CODRURALES := '''' || REPLACE(MI_CODRURALES, ',', ''',''') || '''';
    END IF;

    MI_CONCEPTOS := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                          UN_NOMBRE    => 'CONCEPTOS PARA EXCEDENTES Y SALDO',
                                          UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                          UN_FECHA_PAR => SYSDATE);
    MI_CONCEPTOS := REPLACE(REPLACE(MI_CONCEPTOS, ' ,', ','), ', ', ',');
    MI_CONCEPTO := MI_CONCEPTOS;

    MI_STRSQLFROM := 'FROM     IP_FACTURADOS 
                        INNER JOIN (SELECT IP_USUARIOS_PREDIAL.COMPANIA,
                                           IP_USUARIOS_PREDIAL.CODIGO, 
                                           IP_USUARIOS_PREDIAL.NUMERO_ORDEN, 
                                           IP_USUARIOS_PREDIAL.TIPO_NIT, 
                                           IP_USUARIOS_PREDIAL.NIT, 
                                           IP_USUARIOS_PREDIAL.DIRECCION, 
                                           IP_USUARIOS_PREDIAL.TRPCOD, 
                                           IP_USUARIOS_PREDIAL.INDBORRADO, 
                                           IP_USUARIOS_PREDIAL.CODIGO_NO_ACTIVO
                                    FROM   IP_USUARIOS_PREDIAL 
                                    WHERE  IP_USUARIOS_PREDIAL.INDBORRADO       = 0 
                                      AND  IP_USUARIOS_PREDIAL.CODIGO_NO_ACTIVO = 0) USUARIOSACTIVOS
                          ON   IP_FACTURADOS.COMPANIA     = USUARIOSACTIVOS.COMPANIA
                          AND  IP_FACTURADOS.CODIGO       = USUARIOSACTIVOS.CODIGO
                          AND  IP_FACTURADOS.NUMERO_ORDEN = USUARIOSACTIVOS.NUMERO_ORDEN 
                      WHERE    IP_FACTURADOS.PREANO        BETWEEN '|| UN_ANIO_INICIAL ||' AND '|| UN_ANIO_FINAL ||' 
                        AND    IP_FACTURADOS.PAGADO        = 0
                        AND    IP_FACTURADOS.INDEXE        = 0
                        AND    IP_FACTURADOS.NOCOBRADO     = 0
                        AND    IP_FACTURADOS.INDPAGO_ACPAG = 0 ';

    IF MI_CONCEPTOS = 'NA' THEN
      MI_STRSQL := ' SELECT   TO_CHAR(IP_FACTURADOS.PREANO) PREANO, 
                              SUM(CASE WHEN SUBSTR(IP_FACTURADOS.CODIGO,1,2) NOT IN ('|| MI_CODRURALES ||') 
                                       THEN C1 + C13
                                       ELSE 0 END)          URBANO,
                              SUM(CASE WHEN SUBSTR(IP_FACTURADOS.CODIGO,1,2) IN ('|| MI_CODRURALES ||')
                                       THEN C1 + C13
                                       ELSE 0 END)          RURAL '|| MI_STRSQLFROM;

      IF UN_CHECKACUERDOS <> 0 THEN
        MI_STRSQL := MI_STRSQL ||
                     ' GROUP BY IP_FACTURADOS.PREANO ';
      ELSE
        MI_STRSQL := MI_STRSQL ||
                     '  AND    IP_FACTURADOS.INDPAGO_ACPAG = 0 
                      GROUP BY IP_FACTURADOS.PREANO';
      END IF;
    ELSE
      WHILE LENGTH(MI_CONCEPTO) > 0 LOOP
        IF INSTR(MI_CONCEPTO, ',', 1) = 0 THEN
          MI_POS := INSTR('14,15,16,17,18,19,20', MI_CONCEPTO,1);
        ELSE
          MI_POS := INSTR('14,15,16,17,18,19,20', SUBSTR(MI_CONCEPTO, 1, INSTR(MI_CONCEPTO, ',', 1) - 1), 1);
        END IF;

        IF MI_POS = 0 THEN
          BEGIN
            BEGIN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD =>SQLCODE,
                UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_CONSMORPRD_EXCSAL
              );
          END;
        ELSE
          IF INSTR(MI_CONCEPTO, ',', 1) = 0 THEN
            MI_CONCEPTO := '';
          ELSE
            MI_CONCEPTO := SUBSTR(MI_CONCEPTO, INSTR(MI_CONCEPTO, ',', 1) + 1, LENGTH(MI_CONCEPTO));
          END IF;
        END IF;
      END LOOP;

      MI_CONCEPTOS := REPLACE(MI_CONCEPTOS, ',', '+ C');
      MI_CONCEPTOS := 'C' || MI_CONCEPTOS;

      MI_STRSQL := ' SELECT   TO_CHAR(IP_FACTURADOS.PREANO) PREANO, 
                              SUM(CASE WHEN SUBSTR(IP_FACTURADOS.CODIGO,1,2) NOT IN ('|| MI_CODRURALES ||') 
                                       THEN C1 + C13 + '|| MI_CONCEPTOS ||'
                                       ELSE 0 END)          URBANO,
                              SUM(CASE WHEN SUBSTR(IP_FACTURADOS.CODIGO,1,2) IN ('|| MI_CODRURALES ||')
                                       THEN C1 + C13 + '|| MI_CONCEPTOS ||'
                                       ELSE 0 END)          RURAL '|| MI_STRSQLFROM;  

      IF UN_CHECKACUERDOS <> 0 THEN
        MI_STRSQL := MI_STRSQL ||
                     ' GROUP BY  IP_FACTURADOS.PREANO ';
      ELSE
        MI_STRSQL := MI_STRSQL ||
                     '  AND     IP_FACTURADOS.INDPAGO_ACPAG = 0 
                      GROUP BY  IP_FACTURADOS.PREANO';
      END IF;
    END IF;

    MI_STRSQL := MI_STRSQL || ' ORDER BY IP_FACTURADOS.PREANO';
    RETURN MI_STRSQL;
  END FC_ARMACONSULTA_MOROSOS_PRD;
  --17
  FUNCTION FC_ARMACONSULTA_FACTURADOS_PRD
  (
    /*
      NAME              : FC_ARMACONSULTA_MOROSOS_PRD --> EN ACCESS ValidarDatos 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MIGRADOR     : 16/02/2017
      TIME              : 10:00 AM 
      SOURCE MODULE     : PredialP2017.01.06VB.accdb
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : VALIDA LOS CAMPOS DE LAS VIGENCIAS Y ARMA LA CONSULTA PARA EL INFORME DE CARTERA MOROSA
      PARAMETERS        : UN_COMPANIA      => COMPANIA ACTUAL.
                          UN_ANIO_INICIAL  => NUMERO DEL ANIO INICIAL DE LA VIGENCIA.
                          UN_ANIO_FINAL    => NUMERO DEL ANIO FINAL DE LA VIGENCIA.
      @NAME             : armarConsultaFacturadosPredial
      @METHOD           : GET
    */
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO_INICIAL   IN PCK_SUBTIPOS.TI_ANIO,
    UN_ANIO_FINAL     IN PCK_SUBTIPOS.TI_ANIO
  ) 
  RETURN VARCHAR2
  AS
    MI_CONCEPTOS      VARCHAR2(1000 CHAR);
    MI_CONCEPTO       VARCHAR2(1000 CHAR);
    MI_POS            PCK_SUBTIPOS.TI_ENTERO;
    MI_STRSQLFROM     PCK_SUBTIPOS.TI_STRSQL;
    MI_STRSQL         PCK_SUBTIPOS.TI_STRSQL;
    MI_CODRURALES     VARCHAR2(1000 CHAR);

  BEGIN  
    IF NVL(UN_ANIO_INICIAL, 0) = 0 OR NVL(UN_ANIO_FINAL, 0) = 0 THEN
      BEGIN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_CONSMORPRD_CERO
          );
      END;
    ELSE
      IF UN_ANIO_FINAL < UN_ANIO_INICIAL THEN
        BEGIN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD =>SQLCODE,
              UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_CONSMORPRD_MENOR
            );
        END;
      END IF;
    END IF;

    MI_CODRURALES := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                           UN_NOMBRE    => 'CODIGOS PREDIOS RURALES',
                                           UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                           UN_FECHA_PAR => SYSDATE);

    IF SUBSTR(MI_CODRURALES, 1, 1) <> '0' THEN
      BEGIN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD =>SQLCODE,
            UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_CONSMORPRD_CODRUR
          );
      END;
    ELSE
      MI_CODRURALES := '''' || REPLACE(MI_CODRURALES, ',', ''',''') || '''';
    END IF;

    MI_CONCEPTOS := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                          UN_NOMBRE    => 'CONCEPTOS PARA EXCEDENTES Y SALDO',
                                          UN_MODULO    => PCK_DATOS.MODULOPREDIAL,
                                          UN_FECHA_PAR => SYSDATE);
    MI_CONCEPTOS := REPLACE(REPLACE(MI_CONCEPTOS, ' ,', ','), ', ', ',');
    MI_CONCEPTO := MI_CONCEPTOS;

    MI_STRSQLFROM := 'FROM     IP_FACTURADOS
                      WHERE    IP_FACTURADOS.PREANO        BETWEEN '|| UN_ANIO_INICIAL ||' AND '|| UN_ANIO_FINAL ||'';

    IF MI_CONCEPTOS = 'NA' THEN
      MI_STRSQL := ' SELECT   TO_CHAR(IP_FACTURADOS.PREANO) PREANO, 
                              SUM(CASE WHEN SUBSTR(IP_FACTURADOS.CODIGO,1,2) NOT IN ('|| MI_CODRURALES ||') 
                                       THEN C1 + C13
                                       ELSE 0 END)          URBANO,
                              SUM(CASE WHEN SUBSTR(IP_FACTURADOS.CODIGO,1,2) IN ('|| MI_CODRURALES ||')
                                       THEN C1 + C13
                                       ELSE 0 END)          RURAL '|| MI_STRSQLFROM;

    ELSE
      WHILE LENGTH(MI_CONCEPTO) > 0 LOOP
        IF INSTR(MI_CONCEPTO, ',', 1) = 0 THEN
          MI_POS := INSTR('14,15,16,17,18,19,20', MI_CONCEPTO, 1);
        ELSE
          MI_POS := INSTR('14,15,16,17,18,19,20', SUBSTR(MI_CONCEPTO, 1, INSTR(MI_CONCEPTO, ',', 1) - 1), 1);
        END IF;

        IF MI_POS = 0 THEN
          BEGIN
            BEGIN
              RAISE PCK_EXCEPCIONES.EXC_PREDIAL;
            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD =>SQLCODE,
                UN_ERROR_COD=>PCK_ERRORES.ERR_PREDIAL_CONSMORPRD_EXCSAL
              );
          END;
        ELSE
          IF INSTR(MI_CONCEPTO, ',', 1) = 0 THEN
            MI_CONCEPTO := '';
          ELSE
            MI_CONCEPTO := SUBSTR(MI_CONCEPTO, INSTR(MI_CONCEPTO, ',', 1) + 1, LENGTH(MI_CONCEPTO));
          END IF;
        END IF;
      END LOOP;

      MI_CONCEPTOS := REPLACE(MI_CONCEPTOS, ',', '+ C');
      MI_CONCEPTOS := 'C' || MI_CONCEPTOS;

      MI_STRSQL := ' SELECT   TO_CHAR(IP_FACTURADOS.PREANO) PREANO, 
                              SUM(CASE WHEN SUBSTR(IP_FACTURADOS.CODIGO,1,2) NOT IN ('|| MI_CODRURALES ||') 
                                       THEN C1 + C13 + '|| MI_CONCEPTOS ||'
                                       ELSE 0 END)          URBANO,
                              SUM(CASE WHEN SUBSTR(IP_FACTURADOS.CODIGO,1,2) IN ('|| MI_CODRURALES ||')
                                       THEN C1 + C13 + '|| MI_CONCEPTOS ||'
                                       ELSE 0 END)          RURAL '|| MI_STRSQLFROM;  
    END IF;

    MI_STRSQL := MI_STRSQL || ' 
                 GROUP BY IP_FACTURADOS.PREANO
                 ORDER BY IP_FACTURADOS.PREANO';

    RETURN MI_STRSQL;
  END FC_ARMACONSULTA_FACTURADOS_PRD;

  -- 18
  FUNCTION FC_REALIZARPAGOCAJA
  /*
      NAME              : FC_REALIZARPAGOCAJA
      AUTHORS           : SYSMAN SAS
      AUTHOR MIGRACION  : AURA LILIANA MONROY GARCÍA
      DATE MIGRADOR     : 14/02/2017
      TIME              : 12:50 PM
      SOURCE MODULE     : PredialP2016.05.06
      DESCRIPTION       : Este procedimiento efectúa el registro del pago por caja, procesado con previa acción.
                          Afecta las tablas IP_USUARIOS_PREDIAL, IP_FACTURADOS, IP_RECIBOS_DE_PAGO, IP_PAGO_BACOSCAB, IP_PAGO_BANCOSDET
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     :                           
      PARAMETERS        : UN_COMPANIA       => Compañia de ingreso a la aplicación
      					          UN_CODIGOPREDIAL  => Código del Predio del que se realizará el registro de pago en caja
                          UN_NUMEROORDEN	  => Número del propietario  
                          UN_ANIOUNICO 		  => Indica si ha sido seleccionada la opción de año único
                          UN_BANCO          => Código de la entidad bancaria donde se realizó el pago
                          UN_USUARIO    	  => Usuario que realiza la anulación de los recibos
                          UN_APLICALEY1066  => Indica si el predio aplica ley 1066
                          UN_CPTO           => Valor de los conceptos relacionados al predio del que se genera el recibo
                          UN_ANIOFINAL      => Año a pagar seleccionado en el formulario 
                          UN_PAGOANO        => Último año cancelado
                          UN_NUMRECIBO      => Número que identifica el recibo generado para el predio
                          UN_DOCNUM         => Consecutivo real asociado al número de recibo del parámetro anterior

    @NAME               : Realizar_Pago_de_Caja   
    @METHOD             : GET     
    */
    (
      UN_COMPANIA			      IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_CODIGOPREDIAL   	  IN PCK_SUBTIPOS.TI_CODPREDIO,
      UN_NUMEROORDEN 		    IN PCK_SUBTIPOS.TI_NUMORDEN,
      UN_ANIOUNICO 		      IN PCK_SUBTIPOS.TI_LOGICO,
      UN_BANCO              IN IP_BANCOS.CODIGOBANCO%TYPE,
      UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO,
      UN_APLICALEY1066      IN PCK_SUBTIPOS.TI_LOGICO, 
      UN_CPTO               IN PCK_PREDIAL_COM3.TYPE_VECTORNUM,
      UN_ANIOFINAL          IN PCK_SUBTIPOS.TI_ANIO,
      UN_PAGOANO            IN PCK_SUBTIPOS.TI_ANIO,
      UN_NUMRECIBO          IN IP_NUMEROSDEFACTURA.CONSECUTIVOREAL%TYPE,
      UN_DOCNUM             IN IP_RECIBOS_DE_PAGO.DOCNUM%TYPE
    )
    RETURN BOOLEAN
    AS
      MI_RESULTADO          BOOLEAN := TRUE;
      MI_CONDICIONAUX       VARCHAR2(15 CHAR); 
      MI_TOTAL              PCK_SUBTIPOS.TI_DOBLE; 
      MI_VALORPAGAR         PCK_SUBTIPOS.TI_DOBLE := 0;  
      MI_RECIBOACTUAL       IP_USUARIOS_PREDIAL.RECIBO_ACTUAL%TYPE;
      MI_ANIOPAGO           PCK_SUBTIPOS.TI_ANIO;
      MI_PAGOBANCO          PCK_SUBTIPOS.TI_ENTERO;
      MI_STRSQL             PCK_SUBTIPOS.TI_STRSQL; 
      MI_RS                 SYS_REFCURSOR;
      MI_TABLA              PCK_SUBTIPOS.TI_TABLA;  
      MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
      MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;  
      MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;

    BEGIN
      -- Consulta número de recibo actual
      BEGIN
        BEGIN
          SELECT RECIBO_ACTUAL
            INTO MI_RECIBOACTUAL
            FROM IP_USUARIOS_PREDIAL
           WHERE COMPANIA      = UN_COMPANIA 
             AND CODIGO        = UN_CODIGOPREDIAL
             AND NUMERO_ORDEN  = '001';

          EXCEPTION WHEN NO_DATA_FOUND THEN 
                    MI_RESULTADO   := FALSE;
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                         
        END;  
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN      
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_RECIBOACTUAL,
                                     UN_TABLAERROR => 'IP_USUARIOS_PREDIAL'); 
      END;

      -- Calcula el valor total pagado para registrarlo 
      MI_CONDICIONAUX := CASE WHEN UN_ANIOUNICO IN(0) THEN ' <= ' ELSE ' = ' END;
      MI_STRSQL       :=  'SELECT '||
                          '       TOTAL  '||
                          '  FROM  '||
                          '       IP_FACTURADOS  '||
                          ' WHERE  '||
                          '       COMPANIA      = '''|| UN_COMPANIA||''' '||
                          '   AND PREANO '|| MI_CONDICIONAUX ||' '|| UN_ANIOFINAL ||' '||
                          '   AND CODIGO        =  '''|| UN_CODIGOPREDIAL || ''' '||
                          '   AND PAGADO        IN(0) '||
                          '   AND INDPAGO_ACPAG IN(0) ';
      OPEN MI_RS FOR MI_STRSQL;
        LOOP
          FETCH MI_RS INTO MI_TOTAL;
          EXIT WHEN MI_RS%NOTFOUND;
              MI_VALORPAGAR := MI_VALORPAGAR + MI_TOTAL;
        END LOOP; 
      CLOSE MI_RS; 

      -- Guardar datos en facturados y cambiar a estado de pagado 
      BEGIN
        BEGIN
          MI_TABLA     := 'IP_FACTURADOS';
          MI_CAMPOS    := ' PAGADO         = -1, '||
                          ' DOCNUM         = ''' || MI_RECIBOACTUAL || ''', '||
                          ' PAG_BAN        = ''' || UN_BANCO || ''', '||
                          ' PREVAL         =   ' || MI_VALORPAGAR || ', '||
                          ' FECHAPAGO      = SYSDATE, '||
                          ' MODIFIED_BY    = ''' ||UN_USUARIO||''', '||
                          ' DATE_MODIFIED  = SYSDATE, '||                     
                          ' FECHA_MODIFICACION_REGISTRO  = SYSDATE, '||
                          ' USUARIO_MODIFICO_REGISTRO    = ''' || UN_USUARIO || '''';
          MI_CONDICION := 'COMPANIA            = ''' || UN_COMPANIA || ''' '||
                          ' AND CODIGO         = ''' || UN_CODIGOPREDIAL || ''' '||
                          ' AND NUMERO_ORDEN   = ''' || UN_NUMEROORDEN || ''' '||
                          ' AND PREANO         '|| MI_CONDICIONAUX ||' '|| UN_ANIOFINAL ||' '||
                          ' AND PAGADO         IN(0) '||
                          ' AND INDEXE         IN(0) '||
                          ' AND NOCOBRADO      IN(0) '||
                          ' AND INDPAGO_ACPAG  IN(0) ';
          MI_RTA       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                             UN_ACCION    => 'M', 
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION);     
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    MI_RESULTADO   := FALSE;
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                                                                                              
        END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD      =>  SQLCODE,
                                        UN_ERROR_COD    =>  PCK_ERRORES.ERRR_PREDIAL_ACTFACTURADOS,
                                        UN_TABLAERROR   =>  'IP_FACTURADOS');
      END;  

      -- Guardar datos en recibo_pago para dejar el registro de pago efectuado por el propietario en caja
      BEGIN
        BEGIN
          MI_TABLA     := 'IP_RECIBOS_DE_PAGO';
          MI_CAMPOS    := ' PAGO               = -1, '||
                          ' APLICALEY1066      = ' || UN_APLICALEY1066 || ', '||
                          ' PAG_BANPAG         = ''' || UN_BANCO || ''', '||
                          ' PREFECPAG          = SYSDATE, '||
                          ' PREFEC             = SYSDATE, '||
                          ' PAQUETEPAG         = ''01'', '||
                          ' FECHA_REGRECAUDO   = SYSDATE, '||
                          ' USUARIO_REGRECAUDO = ''' ||UN_USUARIO||''', '||                      
                          ' MODIFIED_BY        = ''' ||UN_USUARIO||''', '||
                          ' DATE_MODIFIED      = SYSDATE ';
          MI_CONDICION := '     COMPANIA       = ''' || UN_COMPANIA || ''' '||
                          ' AND DOCNUM         = ''' || MI_RECIBOACTUAL || ''' '||
                          ' AND PRECOD         = ''' || UN_CODIGOPREDIAL || ''' '||
                          ' AND PAGO           IN(0) '||
                          ' AND ANULADO        IN(0) ';
          MI_RTA       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                             UN_ACCION    => 'M', 
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION);     
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    MI_RESULTADO   := FALSE;
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                                                                                              
        END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD      =>  SQLCODE,
                                        UN_ERROR_COD    =>  PCK_ERRORES.ERRR_PREDIAL_ACTRECIBOS,
                                        UN_TABLAERROR   =>  'IP_RECIBOS_DE_PAGO');
      END; 

      -- Guardar datos deL antepenúltimo pago
      BEGIN
        BEGIN
          MI_TABLA     := 'IP_USUARIOS_PREDIAL';
          MI_CAMPOS    := ' PAGO_ANO2        = PAGO_ANO1, '||
                          ' PAG_VAL2         = PAG_VAL1, '||
                          ' PAG_FEC2         = NVL(PAG_FEC1, TO_DATE(''01/01/1970'', ''DD/MM/YYYY'')) , '||
                          ' PAG_BAN2         = PAG_BAN1, '||
                          ' NUM_COM2         = NUM_COM1, '||
                          ' MODIFIED_BY      = ''' ||UN_USUARIO||''', '||
                          ' DATE_MODIFIED    = SYSDATE ';
          MI_CONDICION := '     COMPANIA     = ''' || UN_COMPANIA || ''' '||                      
                          ' AND CODIGO       = TRIM(''' || UN_CODIGOPREDIAL || ''')';
          MI_RTA       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                             UN_ACCION    => 'M', 
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION);     
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    MI_RESULTADO   := FALSE;
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                                                                                              
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_MSGERROR(1).CLAVE := 'CODIGO';
          MI_MSGERROR(1).VALOR :=  UN_CODIGOPREDIAL;       
          PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD      =>  SQLCODE,
                                      UN_ERROR_COD    =>  PCK_ERRORES.ERRR_PREDIAL_ACTUSUPREANTE,
                                      UN_TABLAERROR   =>  'IP_USUARIOS_PREDIAL',
                                      UN_REEMPLAZOS   =>  MI_MSGERROR);
      END;   

      -- Guardar datos deL penúltimo pago
      BEGIN
        BEGIN
          MI_TABLA     := 'IP_USUARIOS_PREDIAL';
          MI_CAMPOS    := ' PAGO_ANO1        = PAGO_ANO, '||
                          ' PAG_VAL1         = PAG_VAL, '||
                          ' PAG_FEC1         = NVL(PAG_FEC, TO_DATE(''01/01/1970'', ''DD/MM/YYYY'')) , '||
                          ' PAG_BAN1         = PAG_BAN, '||
                          ' NUM_COM1         = NUM_COM, '||
                          ' MODIFIED_BY      = ''' ||UN_USUARIO||''', '||
                          ' DATE_MODIFIED    = SYSDATE ';
          MI_CONDICION := '     COMPANIA     = ''' || UN_COMPANIA || ''' '||                      
                          ' AND CODIGO       = TRIM(''' || UN_CODIGOPREDIAL || ''')';
          MI_RTA       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                             UN_ACCION    => 'M', 
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION);     
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    MI_RESULTADO   := FALSE;      
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                                                                                              
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_MSGERROR(1).CLAVE := 'CODIGO';
          MI_MSGERROR(1).VALOR :=  UN_CODIGOPREDIAL;       
          PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD      =>  SQLCODE,
                                      UN_ERROR_COD    =>  PCK_ERRORES.ERRR_PREDIAL_ACTUSUPREPEN,
                                      UN_TABLAERROR   =>  'IP_USUARIOS_PREDIAL',
                                      UN_REEMPLAZOS   =>  MI_MSGERROR);
      END;  

      -- Guardar datos de último pago
      BEGIN
        BEGIN
          MI_ANIOPAGO := CASE WHEN UN_ANIOUNICO NOT IN(0) THEN UN_PAGOANO ELSE UN_ANIOFINAL END ;

          MI_TABLA     := 'IP_USUARIOS_PREDIAL';
          MI_CAMPOS    := ' PAGO_ANO          = '|| MI_ANIOPAGO ||' , '||
                          ' PAG_VAL           = '|| MI_VALORPAGAR ||', '||
                          ' PAG_FEC           = SYSDATE, '||
                          ' PAG_BAN           = '''|| UN_BANCO || ''', '||
                          ' NUM_COM           = '''|| UN_NUMRECIBO || ''', '|| 
                          ' INDPAGOAUTOMATICO = -1, '||
                          ' C1                = ' || UN_CPTO(1) || ',  C2  = ' || UN_CPTO(2) || ',  C3  = ' || UN_CPTO(3) || ',  C4  = ' || UN_CPTO(4) || ',  C5  = ' || UN_CPTO(5) || ', '||
                          ' C6                = ' || UN_CPTO(6) || ',  C7  = ' || UN_CPTO(7) || ',  C8  = ' || UN_CPTO(8) || ',  C9  = ' || UN_CPTO(9) || ',  C10 = ' || UN_CPTO(10) || ','||
                          ' C11               = ' || UN_CPTO(11) || ', C12 = ' || UN_CPTO(12) || ', C13 = ' || UN_CPTO(13) || ', C14 = ' || UN_CPTO(14) || ', C15 = ' || UN_CPTO(15) || ', '||
                          ' C16               = ' || UN_CPTO(16) || ', C17 = ' || UN_CPTO(17) || ', C18 = ' || UN_CPTO(18) || ', C19 = ' || UN_CPTO(19) || ', C20 = ' || UN_CPTO(20) || ', '||
                          ' MODIFIED_BY       = ''' ||UN_USUARIO||''', '||
                          ' DATE_MODIFIED     = SYSDATE ';
          MI_CONDICION := '     COMPANIA      = ''' || UN_COMPANIA || ''' '||                      
                          ' AND CODIGO        = TRIM(''' || UN_CODIGOPREDIAL || ''')';
          MI_RTA       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                             UN_ACCION    => 'M', 
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_CONDICION => MI_CONDICION);     
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    MI_RESULTADO   := FALSE;      
                    RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                                                                                              
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_MSGERROR(1).CLAVE := 'CODIGO';
          MI_MSGERROR(1).VALOR :=  UN_CODIGOPREDIAL;       
          PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD      =>  SQLCODE,
                                      UN_ERROR_COD    =>  PCK_ERRORES.ERRR_PREDIAL_ACTUSUPREULT,
                                      UN_TABLAERROR   =>  'IP_USUARIOS_PREDIAL',
                                      UN_REEMPLAZOS   =>  MI_MSGERROR);
      END;    

      -- Guardar datos en pago_bancoscab
      -- Verifica si se han registrado pagos en el día, si ya existe información la actualiza, sino realiza un registro nuevo
      SELECT COUNT(*) 
        INTO MI_PAGOBANCO
        FROM IP_PAGO_BANCOSCAB      
       WHERE COMPANIA = UN_COMPANIA
         AND TO_CHAR(PREFEC,'DD/MM/YYYY') = TO_CHAR(SYSDATE,'DD/MM/YYYY')
         AND PAQUETE  = '01' 
         AND PAG_BAN  = '01';

      IF MI_PAGOBANCO NOT IN(0) THEN
        BEGIN
          BEGIN
            MI_TABLA     := 'IP_PAGO_BANCOSCAB';
            MI_CAMPOS    := ' NROCUPONES       = NROCUPONES + 1, '||
                            ' VLRREPORTADO     = VLRREPORTADO + ' || MI_VALORPAGAR ||', '||
                            ' NROCUPONESACU    = NROCUPONESACU + 1, '||
                            ' ACUMULADO        = ACUMULADO + ' || MI_VALORPAGAR ||', '||
                            ' MODIFIED_BY      = ''' ||UN_USUARIO||''', '||
                            ' DATE_MODIFIED    = SYSDATE ';
            MI_CONDICION := '     COMPANIA     = ''' || UN_COMPANIA || ''' '||                      
                            ' AND TO_CHAR(PREFEC,''DD/MM/YYYY'') = TO_CHAR(SYSDATE,''DD/MM/YYYY'') '||
                            ' AND PAQUETE  = ''01'' '|| 
                            ' AND PAG_BAN  = ''' || UN_BANCO || ''' ';
            MI_RTA       := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                               UN_ACCION    => 'M', 
                                               UN_CAMPOS    => MI_CAMPOS, 
                                               UN_CONDICION => MI_CONDICION);     
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      MI_RESULTADO   := FALSE;        
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                                                                                              
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            MI_MSGERROR(1).CLAVE := 'CODIGO';
            MI_MSGERROR(1).VALOR :=  UN_CODIGOPREDIAL;        
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD      =>  SQLCODE,
                                        UN_ERROR_COD    =>  PCK_ERRORES.ERRR_PREDIAL_ACTPAGOBCAB,
                                        UN_TABLAERROR   =>  'IP_PAGO_BANCOSCAB',
                                        UN_REEMPLAZOS   =>  MI_MSGERROR);
        END;      
      ELSE
        BEGIN
            BEGIN
              MI_CAMPOS :=  ' COMPANIA, '|| 
                            ' PREFEC, '|| 
                            ' PAQUETE, '|| 
                            ' PAG_BAN, '||
                            ' VLRREPORTADO, '||
                            ' NROCUPONES, '||
                            ' NROCUPONESACU, '||
                            ' ACUMULADO, '||
                            ' CREATED_BY, '||
                            ' DATE_CREATED '; 
              MI_VALORES  :=  '''' || UN_COMPANIA || ''', '||
                              ' TRUNC(SYSDATE), '||
                              ' ''01'', '||
                              ' ''' || UN_BANCO || ''', '||
                              ' ' || MI_VALORPAGAR ||', '||
                              ' 1, '||
                              ' 1, '||
                              ' '|| MI_VALORPAGAR ||', ' ||
                              ' '''|| UN_USUARIO ||''', '||
                              ' SYSDATE';
              MI_RTA    := PCK_DATOS.FC_ACME(UN_TABLA      => 'IP_PAGO_BANCOSCAB',
                                             UN_ACCION     => 'I', 
                                             UN_CAMPOS     => MI_CAMPOS,
                                             UN_VALORES    => MI_VALORES);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                          MI_RESULTADO   := FALSE;            
                          RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                         

            END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
              MI_MSGERROR(1).CLAVE := 'CODIGO';
              MI_MSGERROR(1).VALOR :=  UN_CODIGOPREDIAL;        
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                           UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSPAGOBCAB,
                                           UN_TABLAERROR => 'IP_PAGO_BANCOSCAB',
                                           UN_REEMPLAZOS => MI_MSGERROR);
          END ;   
      END IF;

      -- Guardar datos en pago_bancosdet 
      BEGIN
        BEGIN
          MI_CAMPOS   :=  ' COMPANIA, '|| 
                          ' PRECOD, '||
                          ' NUMERO_ORDEN, '||
                          ' PAG_BAN, '||
                          ' PREFEC, '||
                          ' PAQUETE, '||
                          ' PREVAL, '||
                          ' ANO, '||
                          ' C1,  C2,  C3,  C4,  C5,  '||
                          ' C6,  C7,  C8,  C9,  C10, '||
                          ' C11, C12, C13, C14, C15, '||
                          ' C16, C17, C18, C19, C20, '|| 
                          ' NUMFACTURA, '||
                          ' BARRAS, '||
                          ' USUARIO, '||
                          ' CREATED_BY, '||
                          ' DATE_CREATED '; 

          MI_VALORES  :=  ' ''' || UN_COMPANIA || ''', '||
                          ' ''' || UN_CODIGOPREDIAL ||''', '||
                          ' ''001'', '||
                          ' ''' || UN_BANCO || ''', '||
                          ' TRUNC(SYSDATE), '||
                          ' ''01'', '||
                          ' ' || MI_VALORPAGAR ||', '||
                          ' ' || UN_ANIOFINAL ||', ' ||
                          ' ' || UN_CPTO(1) || ',  ' || UN_CPTO(2) || ',  ' || UN_CPTO(3) || ',  ' || UN_CPTO(4) || ',  ' || UN_CPTO(5) || ', '||
                          ' ' || UN_CPTO(6) || ',  ' || UN_CPTO(7) || ',  ' || UN_CPTO(8) || ',  ' || UN_CPTO(9) || ',  ' || UN_CPTO(10) || ','||
                          ' ' || UN_CPTO(11) || ', ' || UN_CPTO(12) || ', ' || UN_CPTO(13) || ', ' || UN_CPTO(14) || ', ' || UN_CPTO(15) || ', '||
                          ' ' || UN_CPTO(16) || ', ' || UN_CPTO(17) || ', ' || UN_CPTO(18) || ', ' || UN_CPTO(19) || ', ' || UN_CPTO(20) || ', '||
                          ' ''' || UN_DOCNUM ||''',' || -- numrecibo con lpad
                          ' NULL, '||
                          ' '''|| UN_USUARIO ||''', '||
                          ' '''|| UN_USUARIO ||''', '||
                          ' SYSDATE';


          MI_RTA    := PCK_DATOS.FC_ACME(UN_TABLA      => 'IP_PAGO_BANCOSDET',
                                         UN_ACCION     => 'I', 
                                         UN_CAMPOS     => MI_CAMPOS,
                                         UN_VALORES    => MI_VALORES);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                      MI_RESULTADO   := FALSE;        
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                         

        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
          MI_MSGERROR(1).CLAVE := 'CODIGO';
          MI_MSGERROR(1).VALOR :=  UN_CODIGOPREDIAL;        
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERRR_PREDIAL_INSPAGOBDET,
                                       UN_TABLAERROR => 'IP_PAGO_BANCOSDET',
                                       UN_REEMPLAZOS => MI_MSGERROR);
      END ;   

      --Guarda los conceptos (c1-c20) de forma general
      PCK_PREDIAL_COM4.PR_COPIACONCEPTOS (UN_COMPANIA       => UN_COMPANIA, 
                                          UN_CODIGOPREDIAL  => UN_CODIGOPREDIAL,
                                          UN_NUMEROORDEN    => UN_NUMEROORDEN,
                                          UN_PAGOANO        => UN_PAGOANO,
                                          UN_USUARIO        => UN_USUARIO);

      RETURN MI_RESULTADO;

    END FC_REALIZARPAGOCAJA;

    -- 19
    PROCEDURE PR_COPIACONCEPTOS
      /*
        NAME              : PR_COPIACONCEPTOS
        AUTHORS           : SYSMAN SAS  
        AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
        DATE MIGRADOR     : 16/02/2017
        TIME              : 10:40 AM
        SOURCE MODULE     : PredialP2016.05.06
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              :    
        DESCRIPTION       : Realiza la copia del valor de los conceptos desde IP_FACTURADOS a IP_USUARIOS_PREDIAL
        PARAMETERS        : UN_COMPANIA       => Compania de ingreso a la aplicacion
                            UN_CODIGOPREDIAL  => Código del Predio del que se realizará la copia del valor de los conceptos
                            UN_NUMEROORDEN    => Número que identifica al propietario del predio
                            UN_PAGOANO        => Último año cancelado   
                            UN_USUARIO        => Usuario que realiza la copia de los conceptos
        @NAME             : copiarConceptos
        @METHOD           : POST     
      */ 
    (
      UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA, 
      UN_CODIGOPREDIAL    IN PCK_SUBTIPOS.TI_CODPREDIO,
      UN_NUMEROORDEN      IN PCK_SUBTIPOS.TI_NUMORDEN,
      UN_PAGOANO          IN PCK_SUBTIPOS.TI_ANIO,
      UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
    )
    AS  
      MI_UNICOANIO        PCK_SUBTIPOS.TI_ANIO;
      MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
      MI_MERGEUSING       PCK_SUBTIPOS.TI_MERGEUSING;  
      MI_MERGEENLACE      PCK_SUBTIPOS.TI_MERGEENLACE;
      MI_MERGEEXISTE      PCK_SUBTIPOS.TI_MERGEEXISTE;
      MI_RTA              PCK_SUBTIPOS.TI_RTA_ACME;
      MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;

    BEGIN

      BEGIN
        BEGIN
          SELECT UNICO_ANO_A_PAGAR
            INTO MI_UNICOANIO
            FROM IP_USUARIOS_PREDIAL 
           WHERE COMPANIA     = UN_COMPANIA
             AND CODIGO       = UN_CODIGOPREDIAL
             AND NUMERO_ORDEN = UN_NUMEROORDEN;

          EXCEPTION WHEN NO_DATA_FOUND THEN 
               RAISE PCK_EXCEPCIONES.EXC_PREDIAL;   
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    =>  SQLCODE,
              UN_ERROR_COD  =>  PCK_ERRORES.ERRR_PREDIAL_UNICOANIO,
              UN_TABLAERROR =>  'IP_USUARIOS_PREDIAL'
            );
      END;  

      IF MI_UNICOANIO IS NULL THEN
        BEGIN
          BEGIN
            MI_TABLA        := 'IP_USUARIOS_PREDIAL';

            MI_MERGEUSING   := 'SELECT  IP_USUARIOS_PREDIAL.COMPANIA, '||
                                      ' IP_USUARIOS_PREDIAL.CODIGO, '||
                                      ' IP_USUARIOS_PREDIAL.NUMERO_ORDEN, '||
                                      ' IP_FACTURADOS.C1, '||
                                      ' IP_FACTURADOS.C2, '||
                                      ' IP_FACTURADOS.C3, '||
                                      ' IP_FACTURADOS.C4, '||
                                      ' IP_FACTURADOS.C5, '||
                                      ' IP_FACTURADOS.C6, '||
                                      ' IP_FACTURADOS.C7, '||
                                      ' IP_FACTURADOS.C8, '||
                                      ' IP_FACTURADOS.C9, '||
                                      ' IP_FACTURADOS.C10, '||
                                      ' IP_FACTURADOS.C11, '||
                                      ' IP_FACTURADOS.C12, '||
                                      ' IP_FACTURADOS.C13, '||
                                      ' IP_FACTURADOS.C14, '||
                                      ' IP_FACTURADOS.C15, '||
                                      ' IP_FACTURADOS.C16, '||
                                      ' IP_FACTURADOS.C17, '||
                                      ' IP_FACTURADOS.C18, '||
                                      ' IP_FACTURADOS.C19, '||
                                      ' IP_FACTURADOS.C20, '||
                                      ' IP_FACTURADOS.C1  '||
                                      '+ IP_FACTURADOS.C2  '||
                                      '+ IP_FACTURADOS.C3  '||
                                      '+ IP_FACTURADOS.C4  '||
                                      '+ IP_FACTURADOS.C13  '||
                                      '+ IP_FACTURADOS.C14  '||
                                      '+ IP_FACTURADOS.C15  '||
                                      '+ IP_FACTURADOS.C16  '||
                                      '+ IP_FACTURADOS.C17  '||
                                      '+ IP_FACTURADOS.C18  '||
                                      '+ IP_FACTURADOS.C19  '||
                                      '+ IP_FACTURADOS.C20 TOTAL, '||
                                      ''''||UN_USUARIO||''' MODIFIED_BY, '||
                                      ' SYSDATE  DATE_MODIFIED'||
                              '  FROM IP_USUARIOS_PREDIAL '||
                              '   INNER JOIN IP_FACTURADOS '||
                              '       ON IP_USUARIOS_PREDIAL.COMPANIA     = IP_USUARIOS_PREDIAL.COMPANIA '||
                              '      AND IP_USUARIOS_PREDIAL.CODIGO       = IP_FACTURADOS.CODIGO '||
                              '      AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = IP_FACTURADOS.NUMERO_ORDEN '||        
                              '  WHERE IP_FACTURADOS.COMPANIA     = ''' || UN_COMPANIA ||''' '||
                              '    AND IP_FACTURADOS.PREANO       =    '|| UN_PAGOANO ||'  '||
                              '    AND IP_USUARIOS_PREDIAL.CODIGO = ''' || UN_CODIGOPREDIAL ||'''';

            MI_MERGEENLACE  := '     TABLA.COMPANIA     = VISTA.COMPANIA    '||
                               ' AND TABLA.CODIGO       = VISTA.CODIGO      '||     
                               ' AND TABLA.NUMERO_ORDEN = VISTA.NUMERO_ORDEN'; 

            MI_MERGEEXISTE  := ' UPDATE SET TABLA.C1              = VISTA.C1, '||
                                          ' TABLA.C2              = VISTA.C2, '||
                                          ' TABLA.C3              = VISTA.C3, '||
                                          ' TABLA.C4              = VISTA.C4, '||
                                          ' TABLA.C5              = VISTA.C5, '||
                                          ' TABLA.C6              = VISTA.C6, '||
                                          ' TABLA.C7              = VISTA.C7, '||
                                          ' TABLA.C8              = VISTA.C8, '||
                                          ' TABLA.C9              = VISTA.C9, '||
                                          ' TABLA.C10             = VISTA.C10, '||
                                          ' TABLA.C11             = VISTA.C11, '||
                                          ' TABLA.C12             = VISTA.C12, '||
                                          ' TABLA.C13             = VISTA.C13, '||
                                          ' TABLA.C14             = VISTA.C14, '||
                                          ' TABLA.C15             = VISTA.C15, '||
                                          ' TABLA.C16             = VISTA.C16, '||
                                          ' TABLA.C17             = VISTA.C17, '||
                                          ' TABLA.C18             = VISTA.C18, '||
                                          ' TABLA.C19             = VISTA.C19, '||
                                          ' TABLA.C20             = VISTA.C20, '||
                                          ' TABLA.TOTAL           = VISTA.TOTAL, '||
                                          ' TABLA.MODIFIED_BY     = VISTA.MODIFIED_BY, '||
                                          ' TABLA.DATE_MODIFIED   = VISTA.DATE_MODIFIED';

            MI_RTA          := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA,
                                                  UN_ACCION      => 'MM', 
                                                  UN_MERGEUSING  => MI_MERGEUSING, 
                                                  UN_MERGEENLACE => MI_MERGEENLACE, 
                                                  UN_MERGEEXISTE => MI_MERGEEXISTE); 

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                      RAISE PCK_EXCEPCIONES.EXC_PREDIAL;                                              
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PREDIAL THEN
            MI_MSGERROR(1).CLAVE := 'CODIGO';
            MI_MSGERROR(1).VALOR :=  UN_CODIGOPREDIAL;  
            PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD      =>  SQLCODE,
                                        UN_ERROR_COD    =>  PCK_ERRORES.ERRR_PREDIAL_ACTCPTOSUSUP,
                                        UN_TABLAERROR   =>  'IP_USUARIOS_PREDIAL',
                                        UN_REEMPLAZOS   =>  MI_MSGERROR);        
        END;  
      END IF;    

    END PR_COPIACONCEPTOS;    

FUNCTION FC_AVALUOVIGENCIA
/*   
        @NAME             : obtenerAvaluoVigencia
        @METHOD           : GET     
      */ 
(
   UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA, 
   UN_PREDIO   IN PCK_SUBTIPOS.TI_CODPREDIO, 
   UN_ANIO     IN PCK_SUBTIPOS.TI_ANIO,
   UN_NUMERO_ORDEN IN PCK_SUBTIPOS.TI_NUMORDEN
) RETURN VARCHAR2
AS
    MI_AVALUOVIGENCIA IP_FACTURADOS.AVALUO%TYPE;
BEGIN
    BEGIN 
        SELECT AVALUO 
          INTO MI_AVALUOVIGENCIA
          FROM IP_FACTURADOS  
         WHERE COMPANIA     = UN_COMPANIA
           AND CODIGO       = UN_PREDIO 
           AND NUMERO_ORDEN = UN_NUMERO_ORDEN
           AND PREANO       = UN_ANIO;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_AVALUOVIGENCIA := NULL;   
    END;

   IF MI_AVALUOVIGENCIA IS NULL THEN
      MI_AVALUOVIGENCIA:= 0;
   END IF;
   RETURN MI_AVALUOVIGENCIA;
END FC_AVALUOVIGENCIA;
--21
FUNCTION FC_TARIFAVIGENCIA
/*   
        @NAME             : obtenerTarifaVigencia
        @METHOD           : GET     
      */ 

(
   UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,  
   UN_PREDIO   IN PCK_SUBTIPOS.TI_CODPREDIO, 
   UN_NUMERO_ORDEN IN PCK_SUBTIPOS.TI_NUMORDEN,
   UN_ANIO     IN PCK_SUBTIPOS.TI_ANIO)
RETURN VARCHAR2
AS
    MI_TARIFAVIGENCIA PCK_SUBTIPOS.TI_DOBLE;
BEGIN  
   BEGIN 
       SELECT NVL(PCK_SYSMAN_UTL.FC_ROUND(TRPPOR * 1000,1),0) TRP_PORC
         INTO MI_TARIFAVIGENCIA
         FROM IP_FACTURADOS  
        WHERE CODIGO       = UN_PREDIO 
          AND NUMERO_ORDEN = UN_NUMERO_ORDEN 
          AND PREANO       = UN_ANIO;
   EXCEPTION WHEN NO_DATA_FOUND THEN 
       MI_TARIFAVIGENCIA:= NULL;
   END;

   IF MI_TARIFAVIGENCIA IS NULL THEN
      MI_TARIFAVIGENCIA:= 0;
   END IF;
   RETURN MI_TARIFAVIGENCIA;
END FC_TARIFAVIGENCIA;


END PCK_PREDIAL_COM4;