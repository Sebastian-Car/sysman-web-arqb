create or replace PACKAGE BODY PCK_CONTABILIZAR_JSON AS

FUNCTION FC_CONTABILIZARJSON(
   /*
        NAME              : En Access InterfazContableH
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 06/03/2019
        TIME              : 09:27 AM
        SOURCE MODULE     : INTERFACES InterfacesPb2017.12.02
        MODIFIER          :
        DATE MODIFIED     : 23/11/2022 - CORRECION DE DESCRIPCION DE LOS DETALLES TICKET 7706695
        TIME              :
        DESCRIPTION       : Interface a traves de un json que es recibido con estructura de header y detalle
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME: contabilizaJson
        @METHOD:  POST
    */   
   UN_JSON          IN CLOB     
)RETURN NUMBER 
AS
    MI_RTA VARCHAR2(200);

    MI_TERCEROANTERIOR       PCK_SUBTIPOS.TI_TERCERO := '99999999999';
    MI_TERCERO               PCK_SUBTIPOS.TI_TERCERO;
    MI_SUCURSAL              PCK_SUBTIPOS.TI_SUCURSAL;
    MI_CENTRO                PCK_SUBTIPOS.TI_CENTRO_COSTO;
    MI_AUXILIAR              PCK_SUBTIPOS.TI_AUXILIAR;
    MI_REFERENCIA            PCK_SUBTIPOS.TI_REFERENCIA;
    MI_FUENTE                PCK_SUBTIPOS.TI_FUENTE_RECURSOS;
    MI_FECHA                 DATE;  
    MI_CONTADOR              PCK_SUBTIPOS.TI_ENTERO;  
    MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_NUMERO                PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
    MI_CONSECUTIVODET        PCK_SUBTIPOS.TI_CONSECUTIVOCNT;
    MI_TIPONULO              VARCHAR2(100);    
    MI_CAMPONUMERICO         VARCHAR2(100);    
    MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
    MI_NATURALEZA            PCK_SUBTIPOS.TI_NATURALEZACONTA;
    MI_FILAS                 PCK_SUBTIPOS.TI_ENTERO;
    MI_PINCONSISTENCIAS      CLOB;
    MI_DESCRIPCION           VARCHAR2(4000);
    MI_NITCOMPANIA           COMPANIA.NITCOMPANIA%TYPE;
    MI_SIMPLIFICA            BOOLEAN; --JM CC 1429  
    MI_TIPO                  VARCHAR2(200); --JM CC 1846
    MI_PARINDYCOM            VARCHAR2(200); --JM CC 1846
    MI_PARRETEICA            VARCHAR2(200); --JM CC 1846
    MI_PARPREDIAL            VARCHAR2(200); --JM CC 2285
    MI_PARSERPUBLICOR        VARCHAR2(200); --JM CC 2477 RECAUDO
    MI_PARSERPUBLICOC        VARCHAR2(200); --JM CC 2477 CAUSADO

BEGIN
  PCK_PRESUPUESTO.GL_DES_KEY :=0;
    MI_NUMERO :=-1;
    MI_FILAS  :=0;
    <<COMPROBANTE>>

    FOR RS IN(
             WITH JSON AS 
                  (SELECT UN_JSON DOC
                   FROM DUAL
                  )  
             SELECT COMPANIA
                  , TIPO
                  , NUMERO
                  , ANO
                  , FECHA
                  , TERCERO
                  , SUCURSAL
                  , CENTROCOSTO
                  , AUXILIAR
                  , REFERENCIA
                  , FUENTE
                  , DESCRIPCION
                  , TEXTO
                  , SIMPLIFICA
                  , OMITIRPPTAL
                  , CONCILIAR
                  , NONETEA
                  , CONTRATISTA
                  , TIPOCONTRATO
                  , CONTRATO
                  , NRODOCUMENTO
                  , ALMDEP
                  , RESPETATERCERO
                  , RESPETAAUXILIAR
                  , REEMPLAZA
                  , USUARIO
            FROM  JSON_TABLE ((SELECT DOC FROM JSON) , '$' 
                            COLUMNS ( COMPANIA        PATH '$.compania'
                                    , TIPO            PATH '$.tipo'
                                    , NUMERO          PATH '$.numero'
                                    , ANO             PATH '$.ano'
                                    , FECHA           PATH '$.fecha'
                                    , TERCERO         PATH '$.tercero'
                                    , SUCURSAL        PATH '$.sucursal'
                                    , CENTROCOSTO     PATH '$.centroCosto'
                                    , AUXILIAR        PATH '$.auxiliar'
                                    , REFERENCIA      PATH '$.referencia'
                                    , FUENTE          PATH '$.fuenteRecurso'
                                    , DESCRIPCION     PATH '$.descripcion'
                                    , TEXTO           PATH '$.texto'
                                    , SIMPLIFICA      PATH '$.simplifica'
                                    , OMITIRPPTAL     PATH '$.omitirPptal'
                                    , CONCILIAR       PATH '$.concilia'
                                    , NONETEA         PATH '$.noNetea'
                                    , CONTRATISTA     PATH '$.contratista'
                                    , TIPOCONTRATO    PATH '$.tipoContrato'
                                    , CONTRATO        PATH '$.contrato'
                                    , NRODOCUMENTO    PATH '$.nroDocumento'
                                    , ALMDEP          PATH '$.almDep'
                                    , RESPETATERCERO  PATH '$.respetaTercero'
                                    , RESPETAAUXILIAR PATH '$.respetaAuxiliar'
                                    , REEMPLAZA       PATH '$.reemplaza'
                                    , USUARIO         PATH '$.usuario'
                                    ) 
                           )
            )
    LOOP
        -- TOMA EL VALOR DE LA DESCRICION GENERAL PARA ASIGNARLA A LOS DETALLES, PUES DE LA ARQ TOMA DOS PERO SON LA MISMA.
        MI_DESCRIPCION := RS.DESCRIPCION;
        --VALIDA LOS NULOS DEL HEADER
        MI_TIPONULO:= ' ';
        IF RS.COMPANIA IS NULL THEN
            MI_TIPONULO:= MI_TIPONULO ||'COMPANIA,';            
        END IF;
        IF RS.TIPO IS NULL THEN
            MI_TIPONULO:= MI_TIPONULO || 'TIPO,';
        END IF;
        IF RS.FECHA IS NULL THEN
            MI_TIPONULO:= MI_TIPONULO || 'FECHA,';
        END IF;
        IF RS.ANO IS NULL THEN
            MI_TIPONULO:= MI_TIPONULO || 'ANO,';
        END IF;
        IF MI_TIPONULO <> ' ' THEN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_MSGERROR(1).CLAVE := 'CAMPO';
                MI_MSGERROR(1).VALOR := MI_TIPONULO;
                MI_MSGERROR(2).CLAVE := 'PARTE';
                MI_MSGERROR(2).VALOR := 'Encabezado del Comprobante';
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE
                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_VALIDARNULOJSON
                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;
        END IF;
        /**
        * VALIDAR CAMPOS NUMERICOS
        */
        MI_CAMPONUMERICO:=' ';
        IF UPPER(NVL(TRIM(RS.SIMPLIFICA),'')) NOT IN('FALSE','TRUE') THEN
            MI_CAMPONUMERICO := MI_CAMPONUMERICO || 'SIMPLIFICA,';    
        END IF;
        IF UPPER(NVL(TRIM(RS.OMITIRPPTAL),'')) NOT IN('FALSE','TRUE') THEN
            MI_CAMPONUMERICO := MI_CAMPONUMERICO || 'OMITIRPPTAL,';    
        END IF;
        IF UPPER(NVL(TRIM(RS.CONCILIAR),'')) NOT IN('FALSE','TRUE') THEN
            MI_CAMPONUMERICO := MI_CAMPONUMERICO || 'CONCILIAR,';    
        END IF;
        IF UPPER(NVL(TRIM(RS.NONETEA),'')) NOT IN('FALSE','TRUE') THEN
            MI_CAMPONUMERICO := MI_CAMPONUMERICO || 'NONETEA,';    
        END IF;
        IF UPPER(NVL(TRIM(RS.CONTRATISTA),'')) NOT IN('FALSE','TRUE') THEN
            MI_CAMPONUMERICO := MI_CAMPONUMERICO || 'CONTRATISTA,';    
        END IF;
        IF UPPER(NVL(TRIM(RS.RESPETATERCERO),'')) NOT IN('FALSE','TRUE') THEN
            MI_CAMPONUMERICO := MI_CAMPONUMERICO || 'RESPETATERCERO,';    
        END IF;
        IF UPPER(NVL(TRIM(RS.RESPETAAUXILIAR),'')) NOT IN('FALSE','TRUE') THEN
            MI_CAMPONUMERICO := MI_CAMPONUMERICO || 'RESPETAAUXILIAR,';    
        END IF;
        IF UPPER(NVL(TRIM(RS.ALMDEP),'')) NOT IN('FALSE','TRUE') THEN
            MI_CAMPONUMERICO := MI_CAMPONUMERICO || 'ALMDEP,';    
        END IF;
        IF UPPER(NVL(TRIM(RS.REEMPLAZA),'')) NOT IN('FALSE','TRUE') THEN
            MI_CAMPONUMERICO := MI_CAMPONUMERICO || 'REEMPLAZA,';    
        END IF;
        IF PCK_SYSMAN_UTL.FC_ES_NUMERO(NVL(TRIM(RS.CONTRATO),'')) =0 THEN
            MI_CAMPONUMERICO := MI_CAMPONUMERICO || 'CONTRATO,';    
        END IF;
        IF PCK_SYSMAN_UTL.FC_ES_NUMERO(NVL(TRIM(RS.ANO),'')) =0 THEN
            MI_CAMPONUMERICO := MI_CAMPONUMERICO || 'ANO,';    
        END IF;

        IF MI_CAMPONUMERICO <> ' ' THEN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_MSGERROR(1).CLAVE := 'CAMPO';
                MI_MSGERROR(1).VALOR := MI_CAMPONUMERICO;
                MI_MSGERROR(2).CLAVE := 'PARTE';
                MI_MSGERROR(2).VALOR := 'Encabezado del Comprobante';
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE
                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_VALIDARNUMERICOSJSON
                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;
        END IF;

        --ASIGNAR VALORES POR DEFECTO
        MI_NUMERO        := NVL(RS.NUMERO,0);
        MI_TERCERO       := NVL(RS.TERCERO , PCK_DATOS.CONS_TERCERO);
        MI_SUCURSAL      := NVL(RS.SUCURSAL, PCK_DATOS.CONS_SUCURSAL);        
        MI_CENTRO        := NVL(RS.CENTROCOSTO ,PCK_DATOS.CONS_CENTRO);
        MI_AUXILIAR      := NVL(RS.AUXILIAR    ,PCK_DATOS.CONS_AUXILIAR);
        MI_REFERENCIA    := NVL(RS.REFERENCIA  ,PCK_DATOS.CONS_REFERENCIA);
        MI_FUENTE        := NVL(RS.FUENTE      ,PCK_DATOS.CONS_FUENTE);
        BEGIN
            MI_FECHA         := TO_DATE(RS.FECHA,'DD/MM/YYYY HH24:MI:SS');
        EXCEPTION WHEN OTHERS THEN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_MSGERROR(1).CLAVE := 'FECHA';
                MI_MSGERROR(1).VALOR := RS.FECHA;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE
                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_VALIDARFECHAJSON
                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;
        END;
        --VALIDAR LOS TERCERO VARIOS DEL MODELO ACCESS
        IF MI_TERCERO = MI_TERCEROANTERIOR OR (MI_TERCERO = PCK_DATOS.CONS_TERCERO AND MI_SUCURSAL <>PCK_DATOS.CONS_SUCURSAL) THEN
            MI_TERCERO := PCK_DATOS.CONS_TERCERO;
            MI_SUCURSAL:= PCK_DATOS.CONS_SUCURSAL;
        END IF;
        --VALIDAR QUE LA SUCURSAL SEA 001 CUANDO EL TERCERO ES DIFERENTE DE VARIOS
        IF MI_TERCERO <> PCK_DATOS.CONS_TERCERO AND MI_SUCURSAL =PCK_DATOS.CONS_SUCURSAL THEN
            MI_SUCURSAL :='001';
        END IF;    
        --VALIDA SI EXISTE LA COMPANIA
        SELECT COUNT(CODIGO) 
        INTO MI_CONTADOR
        FROM COMPANIA 
        WHERE CODIGO = RS.COMPANIA;
        IF MI_CONTADOR=0 THEN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_MSGERROR(1).CLAVE := 'COMPANIA';
                MI_MSGERROR(1).VALOR := RS.COMPANIA;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE
                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_VALIDARCOMPANIA
                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;
        END IF;

        --SE VALIDAN LAS AUXILIARES
        MI_PINCONSISTENCIAS := ' ';
        <<DATOSINCONSISTENTES>>
        FOR MI_RS IN
            (SELECT TP.COMPANIA,  TP.ANO,  TP.TIPO_CPTE,  TP.TERCERO, TP.SUCURSAL, 
                TP.CENTRO_COSTO, TP.AUXILIAR, TP.FUENTE_RECURSOS, TP.REFERENCIA,
                TC.CODIGO TIPOCOMPROBANTE              
              , T.NIT     NIT
              , CCO.CODIGO  CENTROCODIGO
              , AX.CODIGO   AUXILIARCODIGO
              , REFE.CODIGO REFERENCIACODIGO
              , FUENTE.CODIGO FUENTECODIGO
            FROM (SELECT RS.COMPANIA      COMPANIA,   
                         RS.ANO           ANO,  
                         RS.TIPO          TIPO_CPTE,    
                         MI_TERCERO       TERCERO,
                         MI_SUCURSAL      SUCURSAL,
                         MI_CENTRO        CENTRO_COSTO,
                         MI_AUXILIAR      AUXILIAR,
                         MI_REFERENCIA    REFERENCIA,
                         MI_FUENTE        FUENTE_RECURSOS
                FROM DUAL) TP
            LEFT JOIN TIPO_COMPROBANTE TC
                      ON TP.COMPANIA   = TC.COMPANIA
                      AND TP.TIPO_CPTE = TC.CODIGO
            LEFT JOIN TERCERO T
                      ON  TP.COMPANIA = T.COMPANIA
                      AND TP.TERCERO  = T.NIT
                      AND TP.SUCURSAL = T.SUCURSAL
            LEFT JOIN CENTRO_COSTO CCO
                      ON  TP.COMPANIA     = CCO.COMPANIA
                      AND TP.ANO          = CCO.ANO
                      AND TP.CENTRO_COSTO = CCO.CODIGO
            LEFT JOIN AUXILIAR AX
                      ON  TP.COMPANIA = AX.COMPANIA
                      AND TP.ANO      = AX.ANO
                      AND TP.AUXILIAR = AX.CODIGO
            LEFT JOIN REFERENCIA REFE
                      ON  TP.COMPANIA   = REFE.COMPANIA
                      AND TP.ANO        = REFE.ANO
                      AND TP.REFERENCIA = REFE.CODIGO
            LEFT JOIN FUENTE_RECURSOS FUENTE
                      ON  TP.COMPANIA        = FUENTE.COMPANIA
                      AND TP.ANO             = FUENTE.ANO
                      AND TP.FUENTE_RECURSOS = FUENTE.CODIGO
            WHERE TC.CODIGO IS NULL
               OR T.NIT IS NULL
               OR CCO.CODIGO IS NULL
               OR AX.CODIGO IS NULL)
        LOOP
            IF MI_RS.TIPOCOMPROBANTE IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El tipo de comprobante '|| MI_RS.TIPO_CPTE || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
            END IF;
            IF MI_RS.NIT IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El tercero '|| MI_RS.TERCERO || ' sucursal '|| MI_RS.SUCURSAL ||' NO está creado en el sistema.' || CHR(13) || CHR(10));
            END IF;

            IF MI_RS.CENTROCODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El centro de costo '|| MI_RS.CENTRO_COSTO || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
            END IF;
            IF MI_RS.AUXILIARCODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El auxiliar '|| MI_RS.AUXILIAR || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
            END IF;
            IF MI_RS.REFERENCIACODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('La referencia '|| MI_RS.REFERENCIA || ' NO está creada en el sistema.' || CHR(13) || CHR(10));
            END IF;
            IF MI_RS.FUENTECODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('La fuente de Recursos '|| MI_RS.FUENTE_RECURSOS || ' NO está creada en el sistema.' || CHR(13) || CHR(10));
            END IF;
        END LOOP DATOSINCONSISTENTES;

         -- TICKET 7729150 SE CONSULTA EL NIT DE FLORENCIA CON EL FIN DE ESTABLECER QUE DESCRIPCION VALIDAR
        -- ESTO DEBE SER ELIMINADO CUANDO SE REALICE EL DESARROLLO DEL TICKET 7730475
        
        BEGIN 
            SELECT NITCOMPANIA
            INTO MI_NITCOMPANIA
            FROM COMPANIA
            WHERE CODIGO = RS.COMPANIA;
        EXCEPTION WHEN OTHERS THEN
            MI_NITCOMPANIA := '9';
        END ;

        --VALIDA EL NUMERO DE COMPROBANTE        
        IF MI_NUMERO = 0 THEN
            MI_NUMERO := PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT(
                                                  UN_COMPANIA     => RS.COMPANIA,
                                                  UN_ANIO         => RS.ANO,
                                                  UN_TIPO         => RS.TIPO,
                                                  UN_NUMERO       => MI_NUMERO,
                                                  UN_CENTRO_COSTO => MI_CENTRO);
        END IF;
        --SI EL PARAMETRO DE REEMPLAZAR EL COMPROBANTE ESTA INACTIVO ENVIA MENSAJE 
        IF  UPPER(NVL(TRIM(RS.REEMPLAZA),'FALSE')) = 'FALSE' THEN
            SELECT COUNT(COMPANIA)
            INTO MI_CONTADOR
            FROM COMPROBANTE_CNT 
            WHERE COMPANIA = RS.COMPANIA 
              AND NUMERO   = MI_NUMERO 
              AND TIPO     = RS.TIPO;
            IF MI_CONTADOR <> 0 THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                    MI_MSGERROR(1).CLAVE := 'COMPANIA';
                    MI_MSGERROR(1).VALOR := RS.COMPANIA;
                    MI_MSGERROR(2).CLAVE := 'TIPO';
                    MI_MSGERROR(2).VALOR := RS.TIPO;
                    MI_MSGERROR(3).CLAVE := 'NUMERO';
                    MI_MSGERROR(3).VALOR := MI_NUMERO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                                       UN_EXC_COD =>SQLCODE
                                      ,UN_ERROR_COD=>PCK_ERRORES.ERR_EXISTECOMPROBANTE
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
                END;
            END IF;
        END IF;

        MI_CONSECUTIVODET:=0;
        /**
        * RECORRER DETALLES QUE VIENEN EN EL JSON
        */
        <<DETALLE>>
        FOR DET IN(
             WITH JSON AS 
                  (SELECT UN_JSON DOC
                   FROM DUAL
                  )  
             SELECT CUENTA
                  , TERCERO
                  , SUCURSAL
                  , CENTROCOSTO
                  , FUENTERECURSO
                  , AUXILIAR
                  , REFERENCIA
                  , NRODOCUMENTO
                  , DESCRIPCION
                  , DEBITO
                  , CREDITO
                  , RUBRO
            FROM  JSON_TABLE ((SELECT DOC FROM JSON) , '$.detalle[*]' 
                            COLUMNS ( CUENTA          PATH '$.cuenta'
                                    , TERCERO         PATH '$.tercero'
                                    , SUCURSAL        PATH '$.sucursal'
                                    , CENTROCOSTO     PATH '$.centroCosto'
                                    , FUENTERECURSO   PATH '$.fuenteRecurso'
                                    , AUXILIAR        PATH '$.auxiliar'
                                    , REFERENCIA      PATH '$.referencia'
                                    , NRODOCUMENTO    PATH '$.nroDocumento'
                                    , DESCRIPCION     PATH '$.descripcion'
                                    , DEBITO          PATH '$.valorDebito'
                                    , CREDITO         PATH '$.valorCredito'
                                    , RUBRO           PATH '$.rubro'
                                    ) 
                            )
            )
        LOOP
            BEGIN 
                MI_CONSECUTIVODET:= MI_CONSECUTIVODET + 1;
                
                 -- TICKET 7729150 SE VALIDA EL NIT DE LA ENTIDAD, SI ES DIFERENTE A FLORENCIA Y LA DESCRIPCION DEL REGISTRO NO ES NULA SE TOMA LA QUE VIENE EN EL REGISTRO
                 -- DE LO CONTRARIO SE TOMA LA GENERAL ASIGNADA ANTES EN LA VARIABLE MI_DESCRIPCION, ESTO DEBE SER ELIMINADO CUANDO SE REALICE EL DESARROLLO DEL TICKET 7730475
        
                IF MI_NITCOMPANIA <> '800095728-2' AND NOT DET.DESCRIPCION IS NULL THEN
                    MI_DESCRIPCION := DET.DESCRIPCION;
                END IF;

                --VALIDA LOS NULOS DEL HEADER
                MI_TIPONULO:= ' ';
                IF NVL(DET.CUENTA,'') = '' THEN
                    MI_TIPONULO:= MI_TIPONULO ||'CUENTA,';            
                END IF;
                IF MI_TIPONULO <> ' ' THEN
                    BEGIN
                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                        MI_MSGERROR(1).CLAVE := 'CAMPO';
                        MI_MSGERROR(1).VALOR := MI_TIPONULO;
                        MI_MSGERROR(2).CLAVE := 'PARTE';
                        MI_MSGERROR(2).VALOR := 'Detalle del Comprobante. Consecutivo ' || TO_CHAR(MI_CONSECUTIVODET);
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                           UN_EXC_COD =>SQLCODE
                                          ,UN_ERROR_COD=>PCK_ERRORES.ERR_VALIDARNULOJSON
                                          ,UN_REEMPLAZOS  => MI_MSGERROR);
                    END;
                END IF;
                /**
                * VALIDAR CAMPOS NUMERICOS
                */
                MI_CAMPONUMERICO:=' ';
                IF PCK_SYSMAN_UTL.FC_ES_NUMERO(DET.DEBITO) = 0 THEN
                    MI_CAMPONUMERICO := MI_CAMPONUMERICO || 'DEBITO,';    
                END IF;
                IF PCK_SYSMAN_UTL.FC_ES_NUMERO(DET.CREDITO) = 0 THEN
                    MI_CAMPONUMERICO := MI_CAMPONUMERICO || 'CREDITO,';    
                END IF;
                IF MI_CAMPONUMERICO <> ' ' THEN
                    BEGIN
                        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                        MI_MSGERROR(1).CLAVE := 'CAMPO';
                        MI_MSGERROR(1).VALOR := MI_CAMPONUMERICO;
                        MI_MSGERROR(2).CLAVE := 'PARTE';
                        MI_MSGERROR(2).VALOR := 'Encabezado del Comprobante';
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                                           UN_EXC_COD =>SQLCODE
                                          ,UN_ERROR_COD=>PCK_ERRORES.ERR_VALIDARNUMERICOSJSON
                                          ,UN_REEMPLAZOS  => MI_MSGERROR);
                    END;
                END IF;
                /*
                * SE INSERTAN LOS DETALLES A LA TEMPORAL
                */
                MI_NATURALEZA := PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => DET.CUENTA);
                MI_TABLA := 'TEMP_PLANA_AJUSTES';
                MI_CAMPOS := 'COMPANIA
                             ,ANO
                             ,TIPO_CPTE
                             ,COMPROBANTE
                             ,CONSECUTIVO
                             ,CUENTA
                             ,NATURALEZA
                             ,FECHA
                             ,DESCRIPCION
                             ,VALOR_DEBITO
                             ,VALOR_CREDITO
                             ,NRO_DOCUMENTO
                             ,TERCERO
                             ,SUCURSAL
                             ,CENTRO_COSTO
                             ,AUXILIAR
                             ,REFERENCIA
                             ,FUENTE_RECURSOS';
                MI_VALORES := ' '''|| RS.COMPANIA       ||'''';
                MI_VALORES := MI_VALORES || ' , '  || RS.ANO;
                MI_VALORES := MI_VALORES || ' , '''|| RS.TIPO           ||'''';
                MI_VALORES := MI_VALORES || ' , '  || MI_NUMERO;
                MI_VALORES := MI_VALORES || ' , '  || TO_CHAR(MI_CONSECUTIVODET);
                MI_VALORES := MI_VALORES || ' , '''|| DET.CUENTA        ||'''';
                MI_VALORES := MI_VALORES || ' , '''|| MI_NATURALEZA     ||'''';
                MI_VALORES := MI_VALORES || ' , TO_DATE(''' || RS.FECHA || ''',''DD/MM/YYYY HH24:MI:SS'')';
                MI_VALORES := MI_VALORES || ' , '''|| MI_DESCRIPCION    ||'''';
                MI_VALORES := MI_VALORES || ' , '  || NVL(DET.DEBITO ,'0');
                MI_VALORES := MI_VALORES || ' , '  || NVL(DET.CREDITO,'0');
                MI_VALORES := MI_VALORES || ' , '  || CASE WHEN DET.NRODOCUMENTO IS NULL THEN 'NULL' ELSE '''' || REPLACE(DET.NRODOCUMENTO,CHR(39),'') || '''' END ;
                MI_VALORES := MI_VALORES || ' , '''|| NVL(DET.TERCERO      , PCK_DATOS.CONS_TERCERO)    ||'''';
                MI_VALORES := MI_VALORES || ' , '''|| NVL(DET.SUCURSAL     , PCK_DATOS.CONS_SUCURSAL)   ||'''';
                MI_VALORES := MI_VALORES || ' , '''|| NVL(DET.CENTROCOSTO  , PCK_DATOS.CONS_CENTRO)     ||'''';
                MI_VALORES := MI_VALORES || ' , '''|| NVL(DET.AUXILIAR     , PCK_DATOS.CONS_AUXILIAR)   ||'''';
                MI_VALORES := MI_VALORES || ' , '''|| NVL(DET.REFERENCIA   , PCK_DATOS.CONS_REFERENCIA) ||'''';
                MI_VALORES := MI_VALORES || ' , '''|| NVL(DET.FUENTERECURSO, PCK_DATOS.CONS_FUENTE)     ||'''';
                IF NVL(DET.RUBRO, ' ') <> ' ' THEN
                    MI_CAMPOS := MI_CAMPOS ||' ,CUENTAPPTAL
                                               ,EJECUCION_DEBITO
                                               ,EJECUCION_CREDITO ';        
                    MI_VALORES := MI_VALORES || ' ,'''|| DET.RUBRO            ||'''
                                                  ,  '|| NVL(DET.DEBITO ,'0') ||'
                                                  ,  '|| NVL(DET.CREDITO,'0') ||'  ';        
                END IF;
                BEGIN
                    MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                          UN_ACCION  => 'I',
                                                          UN_CAMPOS  => MI_CAMPOS,
                                                          UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                --Se presentó error al insertar el tipo de comprobante: --TIPO-- ,Comprobante: --COMPROBANTE-- en plan ajustes.
                MI_MSGERROR(1).CLAVE := 'TIPO';
                MI_MSGERROR(1).VALOR := RS.TIPO;
                MI_MSGERROR(2).CLAVE := 'COMPROBANTE';
                MI_MSGERROR(2).VALOR := MI_NUMERO;
                MI_MSGERROR(3).CLAVE := 'LINEA';
                MI_MSGERROR(3).VALOR := MI_CONSECUTIVODET;
                PCK_ERR_MSG.RAISE_WITH_MSG
                          ( UN_EXC_COD => SQLCODE
                          , UN_TABLAERROR => MI_TABLA
                          , UN_ERROR_COD  => PCK_ERRORES.ERRR_PLANOINSPLANAAJUSTE
                          , UN_REEMPLAZOS => MI_MSGERROR
                          );
            END;
        END LOOP DETALLE;
        --SE VALIDAN LAS AUXILIARES
        <<DATOSINCONSISTENTES>>
        FOR MI_RS IN
            (SELECT TP.COMPANIA,  TP.ANO,  TP.TIPO_CPTE, TP.TERCERO, TP.SUCURSAL, 
                TP.CENTRO_COSTO, TP.AUXILIAR, TP.FUENTE_RECURSOS, TP.REFERENCIA,
                TC.CODIGO TIPOCOMPROBANTE
              , T.NIT     NIT
              , CCO.CODIGO  CENTROCODIGO
              , AX.CODIGO   AUXILIARCODIGO
              , REFE.CODIGO REFERENCIACODIGO
              , FUENTE.CODIGO FUENTECODIGO
            FROM TEMP_PLANA_AJUSTES TP
            LEFT JOIN TIPO_COMPROBANTE TC
                      ON TP.COMPANIA   = TC.COMPANIA
                      AND TP.TIPO_CPTE = TC.CODIGO
            LEFT JOIN TERCERO T
                      ON  TP.COMPANIA = T.COMPANIA
                      AND TP.TERCERO  = T.NIT
                      AND TP.SUCURSAL = T.SUCURSAL
            LEFT JOIN CENTRO_COSTO CCO
                      ON  TP.COMPANIA     = CCO.COMPANIA
                      AND TP.ANO          = CCO.ANO
                      AND TP.CENTRO_COSTO = CCO.CODIGO
            LEFT JOIN AUXILIAR AX
                      ON  TP.COMPANIA = AX.COMPANIA
                      AND TP.ANO      = AX.ANO
                      AND TP.AUXILIAR = AX.CODIGO
            LEFT JOIN REFERENCIA REFE
                      ON  TP.COMPANIA   = REFE.COMPANIA
                      AND TP.ANO        = REFE.ANO
                      AND TP.REFERENCIA = REFE.CODIGO
            LEFT JOIN FUENTE_RECURSOS FUENTE
                      ON  TP.COMPANIA        = FUENTE.COMPANIA
                      AND TP.ANO             = FUENTE.ANO
                      AND TP.FUENTE_RECURSOS = FUENTE.CODIGO

            WHERE TC.CODIGO IS NULL
               OR T.NIT IS NULL
               OR CCO.CODIGO IS NULL
               OR AX.CODIGO IS NULL)
        LOOP
            IF MI_RS.TIPOCOMPROBANTE IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El tipo de comprobante '|| MI_RS.TIPO_CPTE || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
            END IF;
            IF MI_RS.NIT IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El tercero '|| MI_RS.TERCERO || ' sucursal '|| MI_RS.SUCURSAL ||' NO está creado en el sistema.' || CHR(13) || CHR(10));
            END IF;

            IF MI_RS.CENTROCODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El centro de costo '|| MI_RS.CENTRO_COSTO || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
            END IF;
            IF MI_RS.AUXILIARCODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('El auxiliar '|| MI_RS.AUXILIAR || ' NO está creado en el sistema.' || CHR(13) || CHR(10));
            END IF;
            IF MI_RS.REFERENCIACODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('La referencia '|| MI_RS.REFERENCIA || ' NO está creada en el sistema.' || CHR(13) || CHR(10));
            END IF;
            IF MI_RS.FUENTECODIGO IS NULL THEN
                MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('La fuente de Recursos '|| MI_RS.FUENTE_RECURSOS || ' NO está creada en el sistema.' || CHR(13) || CHR(10));
            END IF;
        END LOOP DATOSINCONSISTENTES;


        IF MI_PINCONSISTENCIAS<> ' ' THEN
            MI_PINCONSISTENCIAS := MI_PINCONSISTENCIAS || TO_CLOB('Proceso de contabilizar no realizado' || CHR(13) || CHR(10));
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_MSGERROR(1).CLAVE := 'MENSAJE';
                MI_MSGERROR(1).VALOR := MI_PINCONSISTENCIAS;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE
                                  ,UN_ERROR_COD=>PCK_ERRORES.ERRR_PERSONALIZADO
                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;
        END IF;        

        IF MI_CONSECUTIVODET <> 0 THEN
            --JM CC 1429  INI esta llegando mal la variable del json se reeemplaza por parametro de la B
            MI_SIMPLIFICA := CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR
                        (UN_COMPANIA    => RS.COMPANIA
                        ,UN_NOMBRE      => 'SIMPLIFICAR INTERFACE GENERAL'
                        ,UN_MODULO      => PCK_DATOS.MODULOCONTABILIDAD
                        ,UN_FECHA_PAR   => SYSDATE ), 'NO') = 'SI' THEN TRUE ELSE FALSE END;
            --JM CC 1429  FIN
            -- INI JM CC 1846 18/06/2025
            MI_TIPO := RS.TIPO;

            MI_PARINDYCOM  := NVL(PCK_SYSMAN_UTL.FC_PAR
                                (UN_COMPANIA    => RS.COMPANIA
                                ,UN_NOMBRE      => 'TIPO COMPROBANTE INTERFAZ RECAUDO INDUSTRIA Y COMERCIO'
                                ,UN_MODULO      => -1
                                ,UN_FECHA_PAR   => SYSDATE ), '');
            MI_PARRETEICA := NVL(PCK_SYSMAN_UTL.FC_PAR
                                (UN_COMPANIA    => RS.COMPANIA
                                ,UN_NOMBRE      => 'TIPO COMPROBANTE INTERFAZ RECAUDO RETEICA'
                                ,UN_MODULO      => -1
                                ,UN_FECHA_PAR   => SYSDATE ), '');
            MI_PARPREDIAL:= NVL(PCK_SYSMAN_UTL.FC_PAR
                                (UN_COMPANIA    => RS.COMPANIA
                                ,UN_NOMBRE      => 'TIPO COMPROBANTE INTERFAZ RECAUDO IMPUESTO PREDIAL'
                                ,UN_MODULO      => -1
                                ,UN_FECHA_PAR   => SYSDATE ), ''); --JM CC 2285
            MI_PARSERPUBLICOR:= NVL(PCK_SYSMAN_UTL.FC_PAR
                                (UN_COMPANIA    => RS.COMPANIA
                                ,UN_NOMBRE      => 'TIPO COMPROBANTE INTERFAZ SERVICIOS RECAUDADO'
                                ,UN_MODULO      => -1
                                ,UN_FECHA_PAR   => SYSDATE ), ''); --JM CC 2477

            MI_PARSERPUBLICOC:= NVL(PCK_SYSMAN_UTL.FC_PAR
                                (UN_COMPANIA    => RS.COMPANIA
                                ,UN_NOMBRE      => 'TIPO COMPROBANTE INTERFAZ SERVICIOS FACTURADO'
                                ,UN_MODULO      => -1
                                ,UN_FECHA_PAR   => SYSDATE ), ''); --JM CC 2477

          IF MI_PARSERPUBLICOR IN (MI_TIPO) THEN
                        IF NVL(PCK_SYSMAN_UTL.FC_PAR
                                (UN_COMPANIA    => RS.COMPANIA
                                ,UN_NOMBRE      => 'MANEJA INTERFAZ DE SERVICIOS PUBLICOS RECAUDADO POR USUARIO'
                                ,UN_MODULO      => -1
                                ,UN_FECHA_PAR   => SYSDATE ), 'NO') = 'NO' THEN
                                MI_SIMPLIFICA := TRUE;
                        ELSE
                                MI_SIMPLIFICA := FALSE;
                        END IF;
          END IF; -- JM CC 2477

             IF MI_PARSERPUBLICOC IN (MI_TIPO) THEN
                        IF NVL(PCK_SYSMAN_UTL.FC_PAR
                                (UN_COMPANIA    => RS.COMPANIA
                                ,UN_NOMBRE      => 'MANEJA INTERFAZ DE SERVICIOS PUBLICOS CAUSADO POR USUARIO'
                                ,UN_MODULO      => -1
                                ,UN_FECHA_PAR   => SYSDATE ), 'NO') = 'NO' THEN
                                MI_SIMPLIFICA := TRUE;
                        ELSE
                                MI_SIMPLIFICA := FALSE;
                        END IF;
          END IF; -- JM CC 2477

          IF MI_PARPREDIAL IN (MI_TIPO) THEN
                        IF NVL(PCK_SYSMAN_UTL.FC_PAR
                                (UN_COMPANIA    => RS.COMPANIA
                                ,UN_NOMBRE      => 'MANEJA INTERFAZ DE PREDIAL POR USUARIO'
                                ,UN_MODULO      => -1
                                ,UN_FECHA_PAR   => SYSDATE ), 'NO') = 'NO' THEN
                                MI_SIMPLIFICA := TRUE;
                        ELSE
                                MI_SIMPLIFICA := FALSE;
                        END IF;
          END IF; -- JM CC 2285
            
          IF MI_PARINDYCOM IN (MI_TIPO) THEN
                        
                        IF NVL(PCK_SYSMAN_UTL.FC_PAR
                                (UN_COMPANIA    => RS.COMPANIA
                                ,UN_NOMBRE      => 'MANEJA INTERFAZ DE COMERCIO POR USUARIO'
                                ,UN_MODULO      => -1
                                ,UN_FECHA_PAR   => SYSDATE ), 'NO') = 'NO' THEN
                                
                                MI_SIMPLIFICA := TRUE;
                        ELSE
                                MI_SIMPLIFICA := FALSE;
                        END IF;            
          END IF;
          
          
          IF  MI_PARRETEICA IN (MI_TIPO) THEN
                        
                        IF NVL(PCK_SYSMAN_UTL.FC_PAR
                                (UN_COMPANIA    => RS.COMPANIA
                                ,UN_NOMBRE      => 'MANEJA INTERFAZ DE RETEICA POR USUARIO'
                                ,UN_MODULO      => -1
                                ,UN_FECHA_PAR   => SYSDATE ), 'NO') = 'NO' THEN
                                
                                MI_SIMPLIFICA := TRUE;
                        ELSE
                                MI_SIMPLIFICA := FALSE;
                        END IF;            
          END IF;
          -- FIN JM CC 1846 18/06/2025

            MI_RTA:= PCK_CONTABILIZAR.FC_CONTABILIZAR(
                               UN_COMPANIA         => RS.COMPANIA
                              ,UN_TIPOCOMPROBANTE  => RS.TIPO
                              ,UN_NUMERO           => MI_NUMERO
                              ,UN_ANO              => TO_NUMBER(RS.ANO)
                              ,UN_FECHA            => MI_FECHA
                              ,UN_TERCERO          => MI_TERCERO
                              ,UN_SUCURSAL         => MI_SUCURSAL
                              ,UN_DESCRIPCION      => RS.DESCRIPCION
                              ,UN_SIMPLE           => CASE WHEN MI_SIMPLIFICA THEN -1 ELSE 0 END --JM CC 1429  
                              ,UN_INDIMPRESION     => 0
                              ,UN_INTOMITIRPPTAL   => CASE WHEN UPPER(RS.OMITIRPPTAL) = 'TRUE' THEN -1 ELSE 0 END 
                              ,UN_CONCILIAR        => CASE WHEN UPPER(RS.CONCILIAR  ) = 'TRUE' THEN -1 ELSE 0 END  
                              ,UN_PLANO            => 0
                              ,UN_NONETEA          => CASE WHEN UPPER(RS.NONETEA    ) = 'TRUE' THEN -1 ELSE 0 END   
                              ,UN_CONTRATISTA      => CASE WHEN UPPER(RS.CONTRATISTA) = 'TRUE' THEN -1 ELSE 0 END    
                              ,UN_TEXTO            => RS.TEXTO
                              ,UN_CONTRATO         => TO_NUMBER(RS.CONTRATO)
                              ,UN_TIPOCONTRATO     => RS.TIPOCONTRATO
                              ,UN_NRO_DOCUMENTO    => RS.NRODOCUMENTO
                              ,UN_ALMDEP           => CASE WHEN UPPER(RS.ALMDEP         ) = 'TRUE' THEN -1 ELSE 0 END
                              ,UN_TERCE            => CASE WHEN UPPER(RS.RESPETATERCERO ) = 'TRUE' THEN -1 ELSE 0 END 
                              ,UN_RESAUXGEN        => CASE WHEN UPPER(RS.RESPETAAUXILIAR) = 'TRUE' THEN -1 ELSE 0 END
                              ,UN_USUARIO          => RS.USUARIO
                            );
        ELSE
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;           
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                MI_MSGERROR(1).CLAVE := 'COMPANIA';
                MI_MSGERROR(1).VALOR := RS.COMPANIA;
                MI_MSGERROR(2).CLAVE := 'TIPO';
                MI_MSGERROR(2).VALOR := RS.TIPO;
                MI_MSGERROR(3).CLAVE := 'NUMERO';
                MI_MSGERROR(3).VALOR := MI_NUMERO;
                PCK_ERR_MSG.RAISE_WITH_MSG(
                                   UN_EXC_COD =>SQLCODE
                                  ,UN_ERROR_COD=>PCK_ERRORES.ERR_CERODETALLESJSON
                                  ,UN_REEMPLAZOS  => MI_MSGERROR);
            END;
        END IF;
    END LOOP COMPROBANTE;
    RETURN MI_NUMERO;
END FC_CONTABILIZARJSON;

END PCK_CONTABILIZAR_JSON;