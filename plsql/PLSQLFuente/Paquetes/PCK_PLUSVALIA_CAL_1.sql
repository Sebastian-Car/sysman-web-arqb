create or replace PACKAGE BODY PCK_PLUSVALIA_CAL AS

    FUNCTION FC_CALCULAR
/*
    NAME              : FC_CALCULAR
    AUTHOR MIGRACION  : JOSEPASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 13/05/2019
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PROCESO DE FACTURACION CON FORMULAS EN EL MODULO DE PLUSVALIA Y VALORIZACIÓN
    @NAME:    procesoFacturacion
    @METHOD:  POST
    */
    (UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
     UN_ID_PROYECTO IN VP_PROYECTOS.ID%TYPE,
     UN_BENEFICIARIO_INICIAL IN VP_BENEFICIARIOS.ID%TYPE,
     UN_BENEFICIARIO_FINAL IN VP_BENEFICIARIOS.ID%TYPE,
     UN_ETAPA IN VP_PROCESO_FACTURACION.CODIGOETAPA%TYPE,
     UN_USUARIO IN PCK_SUBTIPOS.TI_USUARIO) RETURN VARCHAR2
    AS

        TYPE TI_BENEFICIARIO IS TABLE OF VP_BENEFICIARIOS%ROWTYPE INDEX BY BINARY_INTEGER;
        TYPE TI_CONCEPTOS IS TABLE OF VP_CONCEPTOS%ROWTYPE INDEX BY BINARY_INTEGER;
        TYPE TI_FACTURAS IS TABLE OF VP_FACTURA%ROWTYPE INDEX BY BINARY_INTEGER;
        TYPE TYP_CONCEPTOS IS TABLE OF NUMBER INDEX BY PLS_INTEGER;
        CN                   TYP_CONCEPTOS;--   :=  CONCEPTO;
        MI_BENEFICIARIOS     TI_BENEFICIARIO;
        MI_CONCEPTOS         TI_CONCEPTOS;
        MI_FACTURAS_ANULADAS TI_FACTURAS;
        MI_PROYECTO          VP_PROYECTOS%ROWTYPE;
        MI_FACTURA_ANULA     VP_FACTURA%ROWTYPE;
        MI_BENEFICIARIO      VP_BENEFICIARIOS%ROWTYPE;
        MI_CONCEPTO          VP_CONCEPTOS%ROWTYPE;
        MI_CON_FACT          PCK_SUBTIPOS.TI_LONG;
        MI_CON_PROC          PCK_SUBTIPOS.TI_LONG;
        MI_CON_DETA          PCK_SUBTIPOS.TI_LONG;
        MI_ID_PROC           VARCHAR2(32000 CHAR);
        MI_ID_FACT           VARCHAR2(32000 CHAR);
        MI_ID_RECIBO         NUMBER;
        MI_CON_VAL           PCK_SUBTIPOS.TI_DOBLE;
        MI_CLASE_NOMBRE      VARCHAR2(32000 CHAR);
        MI_I_ANULADA         PCK_SUBTIPOS.TI_ENTERO := 0;
        MI_I_CN              VARCHAR2(320);
        MI_RTA               VARCHAR2(32000 CHAR);
        MI_MSGERROR          PCK_SUBTIPOS.TI_CLAVEVALOR;
        MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
        MI_VALORES           PCK_SUBTIPOS.TI_VALORES;
        MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;
    BEGIN
        /*
        * SE CONSULTAN LOS DATOS PROPIOS DEL PROYECTO POR EFECTOS DEL DOBLE INDICE UNICO
        * Y SE TOMAN LOS DATOS EN UN TYPE PARA LUEGO REALIZAR LAS FUNCIONES DE CONSULTA
        */
        BEGIN
            SELECT *
            INTO MI_PROYECTO
            FROM VP_PROYECTOS
            WHERE COMPANIA = UN_COMPANIA
              AND ID = UN_ID_PROYECTO;
            /*
            * SE VALIDA SI EXISTE PROYECTO
            */
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
                EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                        MI_MSGERROR(1).CLAVE := 'PROYECTO';
                        MI_MSGERROR(1).VALOR := UN_ID_PROYECTO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                            , UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_NO_PROYECTO
                            , UN_REEMPLAZOS => MI_MSGERROR
                            );
                END;
        END;
        /*
        * SE TOMA EL NOMBRE DE LA CLASE PARA EFECTOS DE LOS MENSAJES DE ERROR
        */
        SELECT NOMBRE
        INTO MI_CLASE_NOMBRE
        FROM TIPOS
        WHERE ID = MI_PROYECTO.CLASE;
        /*
        * SE INCLUYEN LOS VALORES DE LOS BENIFICIARIOS POR EL PROYECTO
        * SE REALIZA ACA CON EL FIN DE QUE SE CREE EL REGISTRO DE PROCESO SIEMPRE Y CUANDO EXISTAN BENEFICIARIOS
        */
        MI_BENEFICIARIOS.DELETE;
        SELECT *
            BULK COLLECT
        INTO MI_BENEFICIARIOS
        FROM VP_BENEFICIARIOS
        WHERE COMPANIA = UN_COMPANIA
          AND ID_PROYECTO = UN_ID_PROYECTO
          AND ID BETWEEN UN_BENEFICIARIO_INICIAL AND UN_BENEFICIARIO_FINAL
          AND EN_ACUERDO IN (0)
        ORDER BY ID;
        IF MI_BENEFICIARIOS.COUNT = 0 THEN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                    MI_MSGERROR(1).CLAVE := 'PROYECTO';
                    MI_MSGERROR(1).VALOR := MI_PROYECTO.CODIGO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                        , UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_NO_BENEFICIARIOS
                        , UN_REEMPLAZOS => MI_MSGERROR
                        );
            END;
        END IF;
        /*
        * SE INCORPORAN LOS CONCEPTOS A CALCULAR POR EL PROYECTO ESTO PARA NO IR
        * A LA BASE DE DATOS DENTRO DEL RECORRIDO DE LOS BENEFICIARIOS
        */
        MI_CONCEPTOS.DELETE;
        SELECT *
            BULK COLLECT
        INTO MI_CONCEPTOS
        FROM VP_CONCEPTOS
        WHERE COMPANIA = UN_COMPANIA
          AND ID_PROYECTO = UN_ID_PROYECTO
          AND CLASE = MI_PROYECTO.CLASE
        ORDER BY PRIORIDAD;
        IF MI_CONCEPTOS.COUNT = 0 THEN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                    MI_MSGERROR(1).CLAVE := 'PROYECTO';
                    MI_MSGERROR(1).VALOR := MI_PROYECTO.CODIGO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                        , UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_NO_CONCEPTOS
                        , UN_REEMPLAZOS => MI_MSGERROR
                        );
            END;
        END IF;
        /*
        * SE CONSULTA EL CONSECUTIVO DEL PROCESO POR CADA CLASE DE PROYECTO
        */
        MI_CON_PROC := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA => 'VP_PROCESO_FACTURACION',
                                                        UN_CRITERIO => 'COMPANIA       =''' || UN_COMPANIA || '''
                                                                    AND CLASE_PROYECTO =''' || MI_PROYECTO.CLASE ||
                                                                       '''    ',
                                                        UN_CAMPO => 'NUMERO',
                                                        UN_INICIAL => 1);
        /*
        * SE CREA EL REGISTRO DE PROCESO DE FACTURACIÓN
        */
        MI_CAMPOS := 'COMPANIA,
                      ID_PROYECTO,
                      CODIGO_PROYECTO,
                      CLASE_PROYECTO,
                      NUMERO,
                      FECHA,
                      CODIGOETAPA,
                      FECHA_LIMITE,
                      CREATED_BY,
                      DATE_CREATED';
        MI_VALORES := '''' || UN_COMPANIA || ''',
                         ' || UN_ID_PROYECTO || ' ,
                       ''' || MI_PROYECTO.CODIGO || ''',
                       ''' || MI_PROYECTO.CLASE || ''',
                         ' || MI_CON_PROC || ',
                       ''' || SYSDATE || ''',
                         ' || UN_ETAPA || ' ,
                       ''' || (SYSDATE + 3) || ''',
                       ''' || UN_USUARIO || ''',
                       ''' || SYSDATE || ''' ';
        BEGIN
            BEGIN
                MI_ID_PROC := PCK_DATOS.FC_ACME(UN_TABLA => 'VP_PROCESO_FACTURACION',
                                                UN_ACCION => 'I',
                                                UN_CAMPOS => MI_CAMPOS,
                                                UN_VALORES => MI_VALORES,
                                                UN_LLAVE => 'ID'
                    );
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
            END;
        EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                MI_MSGERROR(1).CLAVE := 'PROYECTO';
                MI_MSGERROR(1).VALOR := MI_PROYECTO.CODIGO;
                MI_MSGERROR(2).CLAVE := 'CLASE';
                MI_MSGERROR(2).VALOR := MI_CLASE_NOMBRE;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                    , UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_PROCESOFACTURAR
                    , UN_REEMPLAZOS => MI_MSGERROR
                    );
        END;

        <<BENEFICIARIOS>>
            IF (MI_BENEFICIARIOS.COUNT > 0) THEN
            FOR I_BEN IN MI_BENEFICIARIOS.FIRST .. MI_BENEFICIARIOS.LAST
                LOOP
                    MI_BENEFICIARIO := MI_BENEFICIARIOS(I_BEN);
                    /*
                    * IDENTIFICA SI SE DEBE ANULAR ALGUNA FACTURA DEL MISMO PROYECTO Y EL MISMO PREDIO O BENEFICIARIO
                    */
                    MI_FACTURA_ANULA := NULL;
                    BEGIN
                        BEGIN
                            SELECT *
                            INTO MI_FACTURA_ANULA
                            FROM VP_FACTURA
                            WHERE COMPANIA = UN_COMPANIA
                              AND CODIGO_PROYECTO = MI_PROYECTO.CODIGO
                              AND CLASE_PROYECTO = MI_PROYECTO.CLASE
                              AND ID_BENEFICIARIOS = MI_BENEFICIARIO.ID
                              AND ESTADO IN ('A');
                        EXCEPTION
                            WHEN TOO_MANY_ROWS THEN
                                RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
                            WHEN NO_DATA_FOUND THEN
                                MI_FACTURA_ANULA := NULL;
                        END;
                    EXCEPTION
                        WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                            MI_MSGERROR(1).CLAVE := 'PROYECTO';
                            MI_MSGERROR(1).VALOR := MI_PROYECTO.CODIGO;
                            MI_MSGERROR(2).CLAVE := 'BENEFICIARIO';
                            MI_MSGERROR(2).VALOR := MI_BENEFICIARIO.TERCERO;
                            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                                , UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_MUCHAS_FACT_ACTI
                                , UN_REEMPLAZOS => MI_MSGERROR
                                );
                    END;
                    /*
                    * ANULA LA FACTURA ANTERIOR
                    */
                    IF MI_FACTURA_ANULA.ID IS NOT NULL THEN
                        MI_I_ANULADA := MI_I_ANULADA + 1;
                        MI_FACTURAS_ANULADAS(MI_I_ANULADA) := MI_FACTURA_ANULA;
                        BEGIN
                            BEGIN
                                MI_CAMPOS := 'ESTADO = ''I'',
                                         FECHA_ANULADA = TO_DATE(''' || TO_CHAR(SYSDATE, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ,
                                         MODIFIED_BY = ''' || UN_USUARIO || ''',
                                         DATE_MODIFIED = SYSDATE ';
                                MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''
                                         AND ID       =   ' || MI_FACTURA_ANULA.ID;
                                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA => 'VP_FACTURA'
                                    , UN_ACCION => 'M'
                                    , UN_CAMPOS => MI_CAMPOS
                                    , UN_CONDICION => MI_CONDICION);
                            EXCEPTION
                                WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
                            END;
                        EXCEPTION
                            WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                                MI_MSGERROR(1).CLAVE := 'PROYECTO';
                                MI_MSGERROR(1).VALOR := MI_PROYECTO.CODIGO;
                                MI_MSGERROR(2).CLAVE := 'BENEFICIARIO';
                                MI_MSGERROR(2).VALOR := MI_BENEFICIARIO.TERCERO;
                                MI_MSGERROR(3).CLAVE := 'FACTURA';
                                MI_MSGERROR(3).VALOR := MI_FACTURA_ANULA.NUMERO_FACTURA;
                                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                                    , UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_ANULANDO_FACTURA
                                    , UN_REEMPLAZOS => MI_MSGERROR
                                    );
                        END;
                    END IF;
                    /*
                    * CREAR REGISTRO DE LA FACTURA
                    */
                    BEGIN
                        MI_CON_FACT := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA => 'VP_FACTURA',
                                                                        UN_CRITERIO => 'COMPANIA       = ''' ||
                                                                                       UN_COMPANIA || '''
                                                                                AND CLASE_PROYECTO = ''' ||
                                                                                       MI_PROYECTO.CLASE || ''' ',
                                                                        UN_CAMPO => 'NUMERO_FACTURA',
                                                                        UN_INICIAL => EXTRACT(YEAR FROM SYSDATE) || '000001');
                        MI_CAMPOS := 'COMPANIA,
                                ID_PROCESO_FACTURACION,
                                NUMERO_PROCESO,
                                CODIGO_PROYECTO,
                                CLASE_PROYECTO,
                                NUMERO_FACTURA,
                                FECHA,
                                FECHA_LIMITE_PAGO,
                                ID_BENEFICIARIOS,
                                IP_CODIGO,
                                IP_NUMERO_ORDEN,
                                ESTADO,
                                CREATED_BY,
                                DATE_CREATED,
                                ID_PROCESO_AFECT,
                                ID_FACTURA_AFECT';
                        MI_VALORES := ' ''' || UN_COMPANIA || ''',
                                       ' || MI_ID_PROC || ' ,
                                       ' || MI_CON_PROC || ',
                                     ''' || MI_PROYECTO.CODIGO || ''',
                                     ''' || MI_PROYECTO.CLASE || ''',
                                       ' || MI_CON_FACT || ',
                                     ''' || SYSDATE || ''',
                                     ''' || (SYSDATE + 30) || ''',
                                       ' || MI_BENEFICIARIO.ID || ',
                                     ''' || MI_BENEFICIARIO.IP_CODIGO || ''',
                                     ''' || MI_BENEFICIARIO.IP_NUMERO_ORDEN || ''' ,
                                     ''' || 'A' || ''',
                                     ''' || UN_USUARIO || ''',
                                     ''' || SYSDATE || ''',
                                       ' || CASE
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    WHEN MI_FACTURA_ANULA.ID IS NOT NULL
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        THEN TO_CHAR(MI_FACTURA_ANULA.ID_PROCESO_FACTURACION)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    ELSE 'NULL' END || ',
                                       ' || CASE
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       WHEN MI_FACTURA_ANULA.ID IS NOT NULL
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           THEN TO_CHAR(MI_FACTURA_ANULA.ID)
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       ELSE 'NULL' END;
                        BEGIN
                            MI_ID_FACT := PCK_DATOS.FC_ACME(UN_TABLA => 'VP_FACTURA',
                                                            UN_ACCION => 'I',
                                                            UN_CAMPOS => MI_CAMPOS,
                                                            UN_VALORES => MI_VALORES,
                                                            UN_LLAVE => 'ID'
                                );
                        EXCEPTION
                            WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
                        END;
                    EXCEPTION
                        WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                            MI_MSGERROR(1).CLAVE := 'PROYECTO';
                            MI_MSGERROR(1).VALOR := MI_PROYECTO.CODIGO;
                            MI_MSGERROR(2).CLAVE := 'BENEFICIARIO';
                            MI_MSGERROR(2).VALOR := MI_BENEFICIARIO.TERCERO;
                            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                                , UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_CREANDO_FACTURA
                                , UN_REEMPLAZOS => MI_MSGERROR
                                );
                    END;
                    <<CONCEPTOS>>
                        CN.DELETE;
                    FOR I IN 1 .. 1000
                        LOOP
                            CN(I) := 0;
                        END LOOP;
                    IF (MI_CONCEPTOS.COUNT > 0) THEN
                        FOR I_CON IN MI_CONCEPTOS.FIRST .. MI_CONCEPTOS.LAST
                            LOOP
                                MI_CONCEPTO := MI_CONCEPTOS(I_CON);
                                MI_CON_DETA := I_CON;
                                /*
                                * REEMPLAZO DE CONCEPTOS DESDE ACA PARA EVITAR CREAR VARIABLES GLOBALES
                                */
                                FOR RS IN (SELECT DISTINCT LEVEL,
                                                           REGEXP_SUBSTR(MI_CONCEPTO.FORMULA, '(FC_CN\()(\d+)(\))', 1, LEVEL) FUNCION
                                           FROM VP_PROYECTOS
                                           WHERE ID = MI_PROYECTO.ID
                                           CONNECT BY LEVEL <= REGEXP_COUNT(MI_CONCEPTO.FORMULA, '(FC_CN\()(\d+)(\))')
                                    )
                                    LOOP
                                        IF RS.FUNCION IS NOT NULL THEN
                                            MI_I_CN := SUBSTR(RS.FUNCION, 7, LENGTH(RS.FUNCION) - 7);
                                            BEGIN
                                                BEGIN
                                                    MI_CONCEPTO.FORMULA :=
                                                            REPLACE(MI_CONCEPTO.FORMULA, RS.FUNCION, CN(MI_I_CN));
                                                EXCEPTION
                                                    WHEN OTHERS THEN
                                                        RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
                                                END;
                                            EXCEPTION
                                                WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                                                    MI_MSGERROR(1).CLAVE := 'PROYECTO';
                                                    MI_MSGERROR(1).VALOR := MI_PROYECTO.CODIGO;
                                                    MI_MSGERROR(2).CLAVE := 'BENEFICIARIO';
                                                    MI_MSGERROR(2).VALOR := MI_BENEFICIARIO.TERCERO;
                                                    MI_MSGERROR(3).CLAVE := 'FUNCION';
                                                    MI_MSGERROR(3).VALOR := MI_CONCEPTO.FORMULA;
                                                    MI_MSGERROR(4).CLAVE := 'CONCEPTO';
                                                    MI_MSGERROR(4).VALOR := MI_CONCEPTO.CODIGO;
                                                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                                                        , UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_FORMULA
                                                        , UN_REEMPLAZOS => MI_MSGERROR
                                                        );
                                            END;
                                        END IF;
                                    END LOOP;
                                MI_CON_VAL := FC_RESOLVER_FUNCION(UN_COMPANIA => UN_COMPANIA,
                                                                  UN_CONCEPTO => MI_CONCEPTO,
                                                                  UN_BENEFICIARIO => MI_BENEFICIARIO,
                                                                  UN_PROYECTO => MI_PROYECTO);
                                CN(MI_CONCEPTO.CODIGO) := MI_CON_VAL;
                                BEGIN
                                    MI_CAMPOS := 'COMPANIA,
                                          CODIGO_PROYECTO,
                                          CLASE,
                                          ID_FACTURA,
                                          NUMERO,
                                          ID_CONCEPTO,
                                          CONCEPTO,
                                          CONSECUTIVO,
                                          IP_CODIGO,
                                          IP_NUMERO_ORDEN,
                                          VALOR,
                                          CREATED_BY,
                                          DATE_CREATED';
                                    MI_VALORES := ' ''' || UN_COMPANIA || ''',
                                             ''' || MI_PROYECTO.CODIGO || ''' ,
                                             ''' || MI_PROYECTO.CLASE || ''',
                                               ' || MI_ID_FACT || ',
                                               ' || MI_CON_PROC || ',
                                               ' || MI_CONCEPTO.ID || ',
                                             ''' || MI_CONCEPTO.CODIGO || ''',
                                               ' || MI_CON_FACT || ',
                                             ''' || MI_BENEFICIARIO.IP_CODIGO || ''' ,
                                             ''' || MI_BENEFICIARIO.IP_NUMERO_ORDEN || ''',
                                               ' || MI_CON_VAL || ',
                                             ''' || UN_USUARIO || ''',
                                             ''' || SYSDATE || ''' ';
                                    BEGIN
                                        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA => 'VP_DETALLE_FACTURA',
                                                                    UN_ACCION => 'I',
                                                                    UN_CAMPOS => MI_CAMPOS,
                                                                    UN_VALORES => MI_VALORES
                                            );
                                    EXCEPTION
                                        WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                            RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
                                    END;
                                EXCEPTION
                                    WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                                            , UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_NODATA_CONCEPTOS
                                            );
                                END;
                            END LOOP CONCEPTOS;
                        MI_ID_RECIBO := FC_GENERAR_FACTURA(UN_COMPANIA => UN_COMPANIA,
                                                           UN_ID_FACTURA => MI_ID_FACT,
                                                           UN_USUARIO => UN_USUARIO);

                    END IF;
                    /*
                    * SE ACTUALIZA EL BENEFICIARIO CON LA FACTURA INICIAL PARA TOMARLA COMO REFERENCIA DE PAGO
                    */
                    IF MI_BENEFICIARIO.FACTURA_INICIAL IS NULL THEN
                        BEGIN
                            BEGIN
                                MI_CAMPOS := 'FACTURA_INICIAL = ' || MI_CON_FACT;
                                MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA || '''
                                         AND ID       =   ' || MI_BENEFICIARIO.ID;
                                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA => 'VP_BENEFICIARIOS'
                                    , UN_ACCION => 'M'
                                    , UN_CAMPOS => MI_CAMPOS
                                    , UN_CONDICION => MI_CONDICION);
                            EXCEPTION
                                WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
                            END;
                        EXCEPTION
                            WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                                MI_MSGERROR(1).CLAVE := 'PROYECTO';
                                MI_MSGERROR(1).VALOR := MI_PROYECTO.CODIGO;
                                MI_MSGERROR(2).CLAVE := 'BENEFICIARIO';
                                MI_MSGERROR(2).VALOR := MI_BENEFICIARIO.TERCERO;
                                MI_MSGERROR(3).CLAVE := 'FACTURA';
                                MI_MSGERROR(3).VALOR := MI_FACTURA_ANULA.NUMERO_FACTURA;
                                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                                    , UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_ANULANDO_FACTURA
                                    , UN_REEMPLAZOS => MI_MSGERROR
                                    );
                        END;
                    END IF;
                END LOOP BENEFICIARIOS;
        END IF;
        RETURN MI_ID_PROC;
    END FC_CALCULAR;


    FUNCTION FC_RESOLVER_FUNCION(UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
                                 UN_CONCEPTO IN VP_CONCEPTOS%ROWTYPE,
                                 UN_BENEFICIARIO IN VP_BENEFICIARIOS%ROWTYPE,
                                 UN_PROYECTO IN VP_PROYECTOS%ROWTYPE)
/*
    NAME              : FC_RESEOLVER_FUNCION
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 13/05/2019
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :CESAR OCHOA
    DATE MODIFIED     :08/01/2020
    TIME              :5:00 PM
    MODIFICATIONS     :Se corrige el valor enviado en el campo MI_FORMULA, cuando se reemplaza el valor
    FC_PORCDESCUENTO(). Se añadio una variable llamada MI_FORMULA_PUNTOS, que permite realizar reemplazos en los 
    decimales de coma a punto para corregir errores generados porque no se ejecutaban operaciones matematicas cuando se 
    ejecutaban consultas.
    DESCRIPTION       : PERMITE OBTENER LOS VALOR DE ACUERDO A LOS REGISTROS QUE SE ENVIAN
    @NAME:    resolverFuncion
    @METHOD:  POST
    */
        RETURN NUMBER
    AS
        MI_FORMULA  VARCHAR2(32000);
        MI_BUSQUEDA VARCHAR2(200);
        MI_SALIDA   VARCHAR2(200);
        MI_RETORNO  NUMBER;
        MI_FACTOR   NUMBER;
        MI_MSGERROR PCK_SUBTIPOS.TI_CLAVEVALOR;
        MI_FORMULA_PUNTOS VARCHAR2(3000);
    BEGIN
        MI_BUSQUEDA := '(\s*FC_)(\d+|\w+)(\(\))';
        MI_FORMULA := UN_CONCEPTO.FORMULA;
        <<FUNCIONES>>
        FOR RS IN (SELECT DISTINCT LEVEL, REGEXP_SUBSTR(MI_FORMULA, MI_BUSQUEDA, 1, LEVEL) FUNCION
                   FROM VP_PROYECTOS
                   WHERE ID = UN_PROYECTO.ID
                   CONNECT BY LEVEL <= REGEXP_COUNT(MI_FORMULA, MI_BUSQUEDA)
            )
            LOOP
                BEGIN
                    BEGIN
                        CASE TRIM(RS.FUNCION)
                            WHEN 'FC_FACTORUSO()' THEN SELECT SUM(FACTOR)
                                                       INTO MI_FACTOR
                                                       FROM VP_FACTORES
                                                       WHERE ID = UN_BENEFICIARIO.DESTINACION_ECONOMICA;
                                                       MI_FORMULA :=
                                                               REPLACE(MI_FORMULA, RS.FUNCION, TO_CHAR(NVL(MI_FACTOR, 0)));
                            WHEN 'FC_FACTORBENEFICIO()' THEN SELECT SUM(FACTOR)
                                                             INTO MI_FACTOR
                                                             FROM VP_FACTORES
                                                             WHERE ID = UN_BENEFICIARIO.GRADO_BENEFICIO;
                                                             MI_FORMULA :=
                                                                     REPLACE(MI_FORMULA, RS.FUNCION, TO_CHAR(NVL(MI_FACTOR, 0)));
                            WHEN 'FC_AREAGRAVADA()' THEN MI_FORMULA :=
                                    REPLACE(MI_FORMULA, RS.FUNCION, TO_CHAR(NVL(UN_BENEFICIARIO.AREA_GRAVADA, 0)));
                            WHEN 'FC_AREAVIRTUAL()' THEN MI_FORMULA :=
                                    REPLACE(MI_FORMULA, RS.FUNCION, TO_CHAR(NVL(UN_PROYECTO.AREA_TOTAL, 0)));
                            WHEN 'FC_MONTODISTRIBUIBLE()' THEN MI_FORMULA :=
                                    REPLACE(MI_FORMULA, RS.FUNCION, TO_CHAR(NVL(UN_PROYECTO.VALOR_DISTRIBUIBLE, 0)));
                            WHEN 'FC_FECHALIMITE()' THEN MI_FORMULA := REPLACE(MI_FORMULA, RS.FUNCION, 'TO_DATE(''' ||
                                                                                                       TO_CHAR(FC_FECHA_PROYECTO(UN_PROYECTO.ID, 'LIMITE'), 'DD/MM/YYYY') ||
                                                                                                       ''',''DD/MM/YYYY'')');
                            WHEN 'FC_FECHAAMNISTIA()' THEN IF UN_PROYECTO.APLICA_AMNISTIA <> 0 THEN
                                MI_FORMULA := REPLACE(MI_FORMULA, RS.FUNCION, 'TO_DATE(''' || TO_CHAR(
                                        FC_FECHA_PROYECTO(UN_PROYECTO.ID, 'AMNISTIA'), 'DD/MM/YYYY') ||
                                                                              ''',''DD/MM/YYYY'')');
                            END IF;
                            WHEN 'FC_PORCDESCUENTO()' THEN MI_FORMULA := REPLACE(MI_FORMULA, RS.FUNCION,
                                                                                 '0'||TO_CHAR(NVL(UN_PROYECTO.PORCENTAJE_DESCUENTO, 0) / 100));
                            ELSE MI_FORMULA := MI_FORMULA;
                            END CASE;
                    EXCEPTION
                        WHEN OTHERS THEN
                            RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
                    END;
                EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                        MI_MSGERROR(1).CLAVE := 'PROYECTO';
                        MI_MSGERROR(1).VALOR := UN_PROYECTO.CODIGO;
                        MI_MSGERROR(2).CLAVE := 'BENEFICIARIO';
                        MI_MSGERROR(2).VALOR := UN_BENEFICIARIO.TERCERO;
                        MI_MSGERROR(3).CLAVE := 'FUNCION';
                        MI_MSGERROR(3).VALOR := RS.FUNCION;
                        MI_MSGERROR(4).CLAVE := 'CONCEPTO';
                        MI_MSGERROR(4).VALOR := UN_CONCEPTO.CODIGO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                            , UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_FORMULA
                            , UN_REEMPLAZOS => MI_MSGERROR
                            );
                END;
            END LOOP FUNCIONES;
            
        BEGIN
        MI_FORMULA_PUNTOS := REGEXP_REPLACE(MI_FORMULA,'([[:digit:]]+)'||CHR(44)||'([[:digit:]]+)', '\1.\2');
            BEGIN

                EXECUTE IMMEDIATE 'SELECT ' || MI_FORMULA_PUNTOS ||
                                  ' FROM VP_PROYECTOS WHERE ID =' || UN_PROYECTO.ID INTO MI_RETORNO;
            EXCEPTION
                WHEN OTHERS THEN
                    RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
            END;
        EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                MI_MSGERROR(1).CLAVE := 'PROYECTO';
                MI_MSGERROR(1).VALOR := UN_PROYECTO.CODIGO;
                MI_MSGERROR(2).CLAVE := 'BENEFICIARIO';
                MI_MSGERROR(2).VALOR := UN_BENEFICIARIO.TERCERO;
                MI_MSGERROR(3).CLAVE := 'FUNCION';
                MI_MSGERROR(3).VALOR := MI_FORMULA;
                MI_MSGERROR(4).CLAVE := 'CONCEPTO';
                MI_MSGERROR(4).VALOR := UN_CONCEPTO.CODIGO;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                    , UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_FORMULA
                    , UN_REEMPLAZOS => MI_MSGERROR
                    );
        END;
        RETURN MI_RETORNO;

    END FC_RESOLVER_FUNCION;


    PROCEDURE PR_CAUSAR_FACTURA
/*
    NAME              : PR_CAUSAR_FACTURA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 14/05/2019
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PERMITE CAUSAR LA FACTURA POR ID DE ACUERDO A LA CONFIGURACION DE LOS CONCEPTOS
    @NAME:    causarFactura
    @METHOD:  POST
    */
    (UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
     UN_ID_FACTURA IN VP_FACTURA.ID%TYPE,
     UN_USUARIO IN PCK_SUBTIPOS.TI_USUARIO)
    AS
        MI_FECHA           DATE;
        MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
        MI_PLANA           TEMP_PLANA_AJUSTES%ROWTYPE;
        MI_PLANAFINAL      TEMP_PLANA_AJUSTES%ROWTYPE;
        MI_NUMREGISTRO     TEMP_PLANA_AJUSTES.CONSECUTIVO%TYPE;
        MI_TIPOCOM         TEMP_PLANA_AJUSTES.TIPO_CPTE%TYPE;
        MI_NUMERO          TEMP_PLANA_AJUSTES.COMPROBANTE%TYPE;
        MI_TERCERO         TEMP_PLANA_AJUSTES.TERCERO%TYPE;
        MI_SUCURSAL        TEMP_PLANA_AJUSTES.SUCURSAL%TYPE;
        MI_ANIO            TEMP_PLANA_AJUSTES.ANO%TYPE;
        MI_NRO_DOCUMENTO   TEMP_PLANA_AJUSTES.NRO_DOCUMENTO%TYPE;
        MI_NRO_DOCUMENTO_2 TEMP_PLANA_AJUSTES.NRO_DOCUMENTO_2%TYPE;
        MI_TIPO_RECAUDO    TIPO_COMPROBANTE.TIPO_RECAUDO%TYPE;
        MI_RTA             VARCHAR2(200);
    BEGIN
        MI_FECHA := SYSDATE;
        MI_ANIO := PCK_SYSMAN_UTL.FC_ANIO(MI_FECHA);
        MI_NUMREGISTRO := 0;
        /*
        * VALIDAR EL PERIODO CONTABLE
        */
        IF PCK_CONTABILIDAD4.FC_VERIFICAPERIODO(UN_COMPANIA => UN_COMPANIA
               , UN_ANO => MI_ANIO
               , UN_MES => PCK_SYSMAN_UTL.FC_MES(MI_FECHA)) = 0 THEN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                    MI_MSGERROR(1).CLAVE := 'ANO';
                    MI_MSGERROR(1).VALOR := MI_ANIO;
                    MI_MSGERROR(2).CLAVE := 'MES';
                    MI_MSGERROR(2).VALOR := PCK_SYSMAN_UTL.FC_MES(MI_FECHA);
                    PCK_ERR_MSG.RAISE_WITH_MSG
                        (UN_EXC_COD => SQLCODE
                        , UN_TABLAERROR => 'ANO'
                        , UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_MESCERRADO
                        , UN_REEMPLAZOS => MI_MSGERROR
                        );
            END;
        END IF;
        /*
        * VALIDAR EL TIPO DE COMPROBANTE
        */
        BEGIN
            SELECT VP_PROYECTOS.TIPO_COMPROBANTE,
                   VP_BENEFICIARIOS.TERCERO,
                   VP_BENEFICIARIOS.SUCURSAL,
                   VP_PROYECTOS.CODIGO || '-' || VP_BENEFICIARIOS.FACTURA_INICIAL NRO_DOCUMENTO,
                   VP_FACTURA.NUMERO_FACTURA                                      NRO_DOCUMENTO_2
            INTO MI_TIPOCOM,
                MI_TERCERO,
                MI_SUCURSAL,
                MI_NRO_DOCUMENTO,
                MI_NRO_DOCUMENTO_2
            FROM VP_FACTURA
                     INNER JOIN VP_PROCESO_FACTURACION
                                ON VP_FACTURA.ID_PROCESO_FACTURACION = VP_PROCESO_FACTURACION.ID
                     INNER JOIN VP_PROYECTOS
                                ON VP_PROCESO_FACTURACION.ID_PROYECTO = VP_PROYECTOS.ID
                     INNER JOIN VP_BENEFICIARIOS
                                ON VP_FACTURA.ID_BENEFICIARIOS = VP_BENEFICIARIOS.ID
            WHERE VP_FACTURA.COMPANIA = UN_COMPANIA
              AND VP_FACTURA.ID = UN_ID_FACTURA;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
                EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG
                            (UN_EXC_COD => SQLCODE
                            , UN_TABLAERROR => 'VP_PROYECTOS'
                            , UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_DATOS
                            );
                END;
        END;
        /*
        * GENERAR EL CODIGO DEL COMPROBANTE
        */
        MI_NUMERO := PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT(
                UN_COMPANIA => UN_COMPANIA,
                UN_ANIO => MI_ANIO,
                UN_TIPO => MI_TIPOCOM,
                UN_NUMERO => 0,
                UN_CENTRO_COSTO => PCK_DATOS.CONS_CENTRO);
        MI_PLANA.COMPANIA := UN_COMPANIA;
        MI_PLANA.ANO := MI_ANIO;
        MI_PLANA.TIPO_CPTE := MI_TIPOCOM;
        MI_PLANA.COMPROBANTE := MI_NUMERO;
        MI_PLANA.FECHA := MI_FECHA;
        MI_PLANA.NRO_DOCUMENTO := MI_NRO_DOCUMENTO;
        MI_PLANA.NRO_DOCUMENTO_2 := MI_NRO_DOCUMENTO_2;
        MI_PLANA.TERCERO := MI_TERCERO;
        MI_PLANA.SUCURSAL := MI_SUCURSAL;
        /*
        * DETALLES
        */
        FOR RS IN ( SELECT VP_FACTURA.NUMERO_FACTURA NRO_DOCUMENTO_2,
                           VALOR,
                           VP_CONCEPTOS.CUENTA_DEBITO_CAUSACION,
                           VP_CONCEPTOS.CUENTA_CREDITO_CAUSACION,
                           VP_CONCEPTOS.NOMBRE
                    FROM VP_FACTURA
                             INNER JOIN VP_PROCESO_FACTURACION
                                        ON VP_FACTURA.ID_PROCESO_FACTURACION = VP_PROCESO_FACTURACION.ID
                             INNER JOIN VP_BENEFICIARIOS
                                        ON VP_FACTURA.ID_BENEFICIARIOS = VP_BENEFICIARIOS.ID
                             INNER JOIN VP_DETALLE_FACTURA
                                        ON VP_DETALLE_FACTURA.ID_FACTURA = VP_FACTURA.ID
                             INNER JOIN VP_CONCEPTOS
                                        ON VP_DETALLE_FACTURA.ID_CONCEPTO = VP_CONCEPTOS.ID
                    WHERE VP_FACTURA.COMPANIA = UN_COMPANIA
                      AND VP_FACTURA.ID = UN_ID_FACTURA
            )
            LOOP
                BEGIN
                    /*
                    * REGISTRA EL DEBITO
                    */
                    MI_NUMREGISTRO := MI_NUMREGISTRO + 1;
                    MI_PLANAFINAL := MI_PLANA;
                    MI_PLANAFINAL.DESCRIPCION := 'Causación de Concepto ' || RS.NOMBRE;
                    MI_PLANAFINAL.CONSECUTIVO := MI_NUMREGISTRO;
                    MI_PLANAFINAL.CUENTA := RS.CUENTA_DEBITO_CAUSACION;
                    IF RS.VALOR > 0 THEN
                        MI_PLANAFINAL.VALOR_DEBITO := RS.VALOR;
                        MI_PLANAFINAL.VALOR_CREDITO := 0;
                    ELSE
                        MI_PLANAFINAL.VALOR_CREDITO := RS.VALOR * -1;
                        MI_PLANAFINAL.VALOR_DEBITO := 0;
                    END IF;
                    PR_REGISTRARPLANA(REGISTRO => MI_PLANAFINAL);
                    /*
                    * REGISTRA EL CREDITO
                    */
                    MI_NUMREGISTRO := MI_NUMREGISTRO + 1;
                    MI_PLANAFINAL := MI_PLANA;
                    MI_PLANAFINAL.DESCRIPCION := 'Causación de Concepto ' || RS.NOMBRE;
                    MI_PLANAFINAL.CONSECUTIVO := MI_NUMREGISTRO;
                    MI_PLANAFINAL.CUENTA := RS.CUENTA_CREDITO_CAUSACION;
                    IF RS.VALOR < 0 THEN
                        MI_PLANAFINAL.VALOR_DEBITO := RS.VALOR * -1;
                        MI_PLANAFINAL.VALOR_CREDITO := 0;
                    ELSE
                        MI_PLANAFINAL.VALOR_CREDITO := RS.VALOR;
                        MI_PLANAFINAL.VALOR_DEBITO := 0;
                    END IF;
                    PR_REGISTRARPLANA(REGISTRO => MI_PLANAFINAL);
                EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                        --Se presentó error al insertar el tipo de comprobante: --TIPO-- ,Comprobante: --COMPROBANTE-- en plan ajustes.
                        MI_MSGERROR(1).CLAVE := 'TIPO';
                        MI_MSGERROR(1).VALOR := MI_TIPOCOM;
                        MI_MSGERROR(2).CLAVE := 'COMPROBANTE';
                        MI_MSGERROR(2).VALOR := MI_NUMERO;
                        MI_MSGERROR(3).CLAVE := 'LINEA';
                        MI_MSGERROR(3).VALOR := MI_NUMREGISTRO;
                        PCK_ERR_MSG.RAISE_WITH_MSG
                            (UN_EXC_COD => SQLCODE
                            , UN_TABLAERROR => 'TEMP_PLANA_AJUSTES'
                            , UN_ERROR_COD => PCK_ERRORES.ERRR_PLANOINSPLANAAJUSTE
                            , UN_REEMPLAZOS => MI_MSGERROR
                            );
                END;
            END LOOP;
        /*
        * EJECUTAR INTERFAZ
        */
        IF MI_NUMREGISTRO <> 0 THEN
            MI_RTA := PCK_CONTABILIZAR.FC_CONTABILIZAR(
                    UN_COMPANIA => UN_COMPANIA
                , UN_TIPOCOMPROBANTE => MI_TIPOCOM
                , UN_NUMERO => MI_NUMERO
                , UN_ANO => MI_ANIO
                , UN_FECHA => MI_FECHA
                , UN_TERCERO => MI_TERCERO
                , UN_SUCURSAL => MI_SUCURSAL
                , UN_DESCRIPCION => 'Causación de Factura '
                , UN_SIMPLE => 0
                , UN_INDIMPRESION => 0
                , UN_INTOMITIRPPTAL => -1
                , UN_CONCILIAR => 0
                , UN_PLANO => 0
                , UN_NONETEA => 0
                , UN_TEXTO => 'Causación de Factura '
                , UN_NRO_DOCUMENTO => MI_NRO_DOCUMENTO
                , UN_NRO_DOCUMENTO_2 => MI_NRO_DOCUMENTO_2
                , UN_USUARIO => UN_USUARIO
                );
        ELSE
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_MSGERROR(1).CLAVE := 'COMPANIA';
                    MI_MSGERROR(1).VALOR := un_COMPANIA;
                    MI_MSGERROR(2).CLAVE := 'TIPO';
                    MI_MSGERROR(2).VALOR := MI_TIPOCOM;
                    MI_MSGERROR(3).CLAVE := 'NUMERO';
                    MI_MSGERROR(3).VALOR := MI_NUMERO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                            UN_EXC_COD =>SQLCODE
                        , UN_ERROR_COD=>PCK_ERRORES.ERR_CERODETALLESJSON
                        , UN_REEMPLAZOS => MI_MSGERROR);
            END;
        END IF;
    END PR_CAUSAR_FACTURA;

    PROCEDURE PR_REGISTRARPLANA
/*
    NAME              : PR_REGISTRARPLANA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 15/05/2019
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PERMITE REGISTRAR LOS DATOS EN LA TEMPORAL PARA LUEGO HACER LA INTERFAZ
    @NAME:    causarFactura
    @METHOD:  POST
    */
    (
        REGISTRO IN TEMP_PLANA_AJUSTES%ROWTYPE
    )
    AS
        MI_NATURALEZA TEMP_PLANA_AJUSTES.NATURALEZA%TYPE;
        MI_MSGERROR   PCK_SUBTIPOS.TI_CLAVEVALOR;
        MI_TABLA      VARCHAR2(32);
        MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
        MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
        MI_FILAS      PCK_SUBTIPOS.TI_ENTERO;
    BEGIN
        BEGIN
            MI_NATURALEZA := PCK_CONTABILIDAD2.FC_NATURALEZACUENTA(UN_CUENTA => REGISTRO.CUENTA);
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
                     ,NRO_DOCUMENTO_2
                     ,TERCERO
                     ,SUCURSAL';

            MI_VALORES := '  ''' || REGISTRO.COMPANIA || '''
                       ,   ' || REGISTRO.ANO || '
                       , ''' || REGISTRO.TIPO_CPTE || '''
                       ,   ' || REGISTRO.COMPROBANTE || '
                       ,   ' || REGISTRO.CONSECUTIVO || '
                       , ''' || REGISTRO.CUENTA || '''
                       , ''' || MI_NATURALEZA || '''
                       , TO_DATE(''' || TO_CHAR(REGISTRO.FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'')
                       ,''' || REGISTRO.DESCRIPCION || '''
                       ,  ' || REGISTRO.VALOR_DEBITO || '
                       ,  ' || REGISTRO.VALOR_CREDITO || '
                       ,''' || REGISTRO.NRO_DOCUMENTO || '''
                       ,''' || REGISTRO.NRO_DOCUMENTO_2 || '''
                       ,''' || REGISTRO.TERCERO || '''
                       ,''' || REGISTRO.SUCURSAL || '''';
            BEGIN
                MI_FILAS := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA,
                                              UN_ACCION => 'I',
                                              UN_CAMPOS => MI_CAMPOS,
                                              UN_VALORES => MI_VALORES);
            EXCEPTION
                WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;
            END;
        EXCEPTION
            WHEN PCK_EXCEPCIONES.EXC_INTERFAZ THEN
                --Se presentó error al insertar el tipo de comprobante: --TIPO-- ,Comprobante: --COMPROBANTE-- en plan ajustes.
                MI_MSGERROR(1).CLAVE := 'TIPO';
                MI_MSGERROR(1).VALOR := REGISTRO.TIPO_CPTE;
                MI_MSGERROR(2).CLAVE := 'COMPROBANTE';
                MI_MSGERROR(2).VALOR := REGISTRO.COMPROBANTE;
                MI_MSGERROR(3).CLAVE := 'LINEA';
                MI_MSGERROR(3).VALOR := REGISTRO.CONSECUTIVO;
                PCK_ERR_MSG.RAISE_WITH_MSG
                    (UN_EXC_COD => SQLCODE
                    , UN_TABLAERROR => MI_TABLA
                    , UN_ERROR_COD => PCK_ERRORES.ERRR_PLANOINSPLANAAJUSTE
                    , UN_REEMPLAZOS => MI_MSGERROR
                    );
        END;
    END PR_REGISTRARPLANA;

    FUNCTION FC_PREPARARACUERDO
/*
    NAME              : FC_PREPARARACUERDO
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 17/05/2019
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PERMITE CREAR UN ACUERDO DE PAGO O PRELIQUIDAR DEPENDIENDO DEL PARAMETRO PRELIQUIDAR
    @NAME:    crearAcuerdo
    @METHOD:  POST
    */
    (UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
     UN_ID_PROYECTO IN VP_PROYECTOS.ID%TYPE,
     UN_ID_FACTURA IN VP_FACTURA.ID%TYPE,
     UN_ID_BENEFICIARIO IN VP_BENEFICIARIOS.ID%TYPE,
     UN_CUOTAINICIAL IN PCK_SUBTIPOS.TI_DOBLE,
     UN_NUMEROCUOTA IN PCK_SUBTIPOS.TI_ENTERO,
     UN_INTERESACUERDO IN PCK_SUBTIPOS.TI_DOBLE,
     UN_RESOLUCION IN GN_ACUERDO.REFERENCIA%TYPE,
     UN_MODELOINTERESDEUDA IN GN_ACUERDO.MODELOINTERESDEUDA%TYPE,
     UN_USUARIO IN PCK_SUBTIPOS.TI_USUARIO,
     UN_PRELIQUIDAR IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0) RETURN CLOB
    AS
        MI_BENEFICIARIO VP_BENEFICIARIOS%ROWTYPE;
        MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN
        BEGIN
            SELECT *
            INTO MI_BENEFICIARIO
            FROM VP_BENEFICIARIOS
            WHERE COMPANIA = UN_COMPANIA
              AND ID = UN_ID_BENEFICIARIO
              AND EN_ACUERDO IN (0);
            /*
            * SE VALIDA SI EXISTE BENEFICIARIO
            */
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
                EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                            , UN_ERROR_COD => PCK_ERRORES.ERR_VALIDABENEFICIARIOACUER
                            , UN_REEMPLAZOS => MI_MSGERROR
                            );
                END;
        END;
        /*
        * NO SE CALCULA PARA TENER EL VALOR REAL PUES CAMBIARIA LA FACTURA INICIAL
        * SE DEBE REALIZAR EL CALCULO ANTES
        */
        /*
        MI_SALIDA:= FC_CALCULAR (UN_COMPANIA              => UN_COMPANIA,
                                 UN_ID_PROYECTO           => UN_ID_PROYECTO,
                                 UN_BENEFICIARIO_INICIAL  => UN_ID_BENEFICIARIO,
                                 UN_BENEFICIARIO_FINAL    => UN_ID_BENEFICIARIO,
                                 UN_ETAPA                 => MI_BENEFICIARIO.CODIGOETAPA,
                                 UN_USUARIO               => UN_USUARIO);
        */
        RETURN PCK_ACUERDO_GEN.FC_CREAACUERDO_PLUSVA(UN_COMPANIA => UN_COMPANIA,
                                                     UN_ID_FACTURA => UN_ID_FACTURA,
                                                     UN_RESOLUCION => UN_RESOLUCION,
                                                     UN_CUOTAINICIAL => UN_CUOTAINICIAL,
                                                     UN_NCUOTAS => UN_NUMEROCUOTA,
                                                     UN_FECHA => SYSDATE,
                                                     UN_INTERES => UN_INTERESACUERDO,
                                                     UN_MODELOINTERESDEUDA => UN_MODELOINTERESDEUDA,
                                                     UN_USUARIO => UN_USUARIO,
                                                     UN_PRELIQUIDAR => UN_PRELIQUIDAR);
    END FC_PREPARARACUERDO;

    FUNCTION FC_GENERAR_FACTURA
/*
    NAME              : FC_GENERAR_FACTURA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 14/05/2019
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : FUNCION QUE CREA EL RECIBO DE PAGO
    @NAME:    causarFactura
    @METHOD:  POST
    */
    (UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
     UN_ID_FACTURA IN VP_FACTURA.ID%TYPE,
     UN_USUARIO IN PCK_SUBTIPOS.TI_USUARIO) RETURN GN_RECIBOS.ID%TYPE
    AS
        MI_FECHA          DATE;
        MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
        MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
        MI_RECIBO         GN_RECIBOS%ROWTYPE;
        MI_DETALLE        GN_RECIBOS_DET%ROWTYPE;
        MI_DETALLES       PCK_ACUERDO_GEN.TI_GN_RECIBOS_DET;
        MI_RECIBO_ANULADO GN_RECIBOS.ID%TYPE;
        MI_NUMREGISTRO    NUMBER(5, 0);
        MI_RTA            VARCHAR2(200);
        MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
        MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
        MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
    BEGIN
        /*
        * SE DEFINE QUE LOS PAGOS NORMALES SON TIPO N
        */
        MI_RECIBO.TIPORECIBO := 'N';
        MI_RECIBO.COMPANIA := UN_COMPANIA;
        MI_RECIBO.FECHA := SYSDATE;
        MI_RECIBO.CREATED_BY := UN_USUARIO;
        MI_FECHA := SYSDATE;
        MI_NUMREGISTRO := 0;
        /*
        * VALIDAR EL TIPO DE COMPROBANTE
        */
        BEGIN
            SELECT CASE
                       WHEN VP_FACTURA.CLASE_PROYECTO = 44
                           THEN 61
                       ELSE CASE
                                WHEN VP_FACTURA.CLASE_PROYECTO = 45
                                    THEN 62
                                ELSE NULL
                           END
                       END                   APLICACION,
                   VP_FACTURA.ID             REFERENCIA,
                   VP_FACTURA.CLASE_PROYECTO REFERENCIA2,
                   CASE
                       WHEN VP_PROYECTOS.APLICA_AMNISTIA NOT IN (0) AND VP_PROYECTOS.FECHA_FINAL_AMNISTIA > SYSDATE
                           THEN VP_PROYECTOS.FECHA_FINAL_AMNISTIA
                       ELSE
                           CASE
                               WHEN VP_PROYECTOS.FECHA_FINAL_PROYECTO > SYSDATE THEN VP_PROYECTOS.FECHA_FINAL_PROYECTO
                               ELSE SYSDATE END
                       END                   FECHALIMITE,
                   CASE
                       WHEN VP_PROYECTOS.FECHA_FINAL_PROYECTO > SYSDATE THEN VP_PROYECTOS.FECHA_FINAL_PROYECTO
                       ELSE SYSDATE END      FECHA_LIMITE1,
                   CASE
                       WHEN VP_PROYECTOS.APLICA_AMNISTIA NOT IN (0) AND VP_PROYECTOS.FECHA_FINAL_AMNISTIA > SYSDATE
                           THEN VP_PROYECTOS.FECHA_FINAL_AMNISTIA
                       ELSE NULL
                       END                   FECHA_LIMITE2,
                   VP_BENEFICIARIOS.TERCERO,
                   VP_BENEFICIARIOS.SUCURSAL
            INTO MI_RECIBO.APLICACION,
                MI_RECIBO.REFERENCIA,
                MI_RECIBO.REFERENCIA2,
                MI_RECIBO.FECHALIMITE,
                MI_RECIBO.FECHA_LIMITE1,
                MI_RECIBO.FECHA_LIMITE2,
                MI_RECIBO.TERCERO,
                MI_RECIBO.SUCURSAL
            FROM VP_FACTURA
                     INNER JOIN VP_PROCESO_FACTURACION
                                ON VP_FACTURA.ID_PROCESO_FACTURACION = VP_PROCESO_FACTURACION.ID
                     INNER JOIN VP_PROYECTOS
                                ON VP_PROCESO_FACTURACION.ID_PROYECTO = VP_PROYECTOS.ID
                     INNER JOIN VP_BENEFICIARIOS
                                ON VP_FACTURA.ID_BENEFICIARIOS = VP_BENEFICIARIOS.ID
            WHERE VP_FACTURA.COMPANIA = UN_COMPANIA
              AND VP_FACTURA.ID = UN_ID_FACTURA
              AND VP_BENEFICIARIOS.EN_ACUERDO IN (0);
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
                EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                        PCK_ERR_MSG.RAISE_WITH_MSG
                            (UN_EXC_COD => SQLCODE
                            , UN_TABLAERROR => 'VP_PROYECTOS'
                            , UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_DATOS
                            );
                END;
        END;
        MI_TABLA := 'GN_RECIBOS';
        /*
        * DETALLES
        */
        FOR RS IN ( SELECT VP_FACTURA.ID   REFERENCIA,
                           VP_CONCEPTOS.ID CONCEPTO,
                           VALOR,
                           VP_CONCEPTOS.NOMBRE,
                           VP_CONCEPTOS.CLASE_CONCEPTO
                    FROM VP_FACTURA
                             INNER JOIN VP_DETALLE_FACTURA
                                        ON VP_DETALLE_FACTURA.ID_FACTURA = VP_FACTURA.ID
                             INNER JOIN VP_CONCEPTOS
                                        ON VP_DETALLE_FACTURA.ID_CONCEPTO = VP_CONCEPTOS.ID
                    WHERE VP_FACTURA.COMPANIA = UN_COMPANIA
                      AND VP_FACTURA.ID = UN_ID_FACTURA
                    ORDER BY VP_CONCEPTOS.PRIORIDAD
            )
            LOOP
                MI_NUMREGISTRO := MI_NUMREGISTRO + 1;
                MI_DETALLE := NULL;
                /*
                * PARA EL PAGO NORMAL LA CUOTA SERA -1
                */
                MI_DETALLE.CUOTA := -1;
                MI_DETALLE.REFERENCIA := RS.REFERENCIA;
                MI_DETALLE.CONCEPTO := RS.CONCEPTO;
                MI_DETALLE.CONCEPTO_NOM := RS.NOMBRE;
                MI_DETALLE.VALOR := RS.VALOR;
                MI_RECIBO.TOTALAPAGAR := NVL(MI_RECIBO.TOTALAPAGAR, 0) + RS.VALOR;
                MI_RECIBO.TOTAL1 := NVL(MI_RECIBO.TOTAL1, 0) + RS.VALOR;
                /*
                * PARA CALCULAR EL SEGUNDO VALOR DE PAGO QUE ES EL DE AMNISTIA ES DECIR PLENO SIN APLICAR DE DESCUENTOS
                */
                IF RS.CLASE_CONCEPTO <> 49 AND MI_RECIBO.FECHA_LIMITE2 IS NOT NULL THEN
                    MI_RECIBO.TOTAL2 := NVL(MI_RECIBO.TOTAL2, 0) + RS.VALOR;
                END IF;
                MI_DETALLE.CLASE := RS.CLASE_CONCEPTO;
                MI_DETALLES(MI_NUMREGISTRO) := MI_DETALLE;
            END LOOP;

        MI_RECIBO := PCK_ACUERDO_GEN.FC_INSERTARRECIBO(UN_RECIBO => MI_RECIBO,
                                                       UN_DETALLES => MI_DETALLES,
                                                       UN_USUARIO => UN_USUARIO);
        RETURN MI_RECIBO.ID;
    END FC_GENERAR_FACTURA;

    FUNCTION FC_FECHA_PROYECTO(UN_ID_PROYECTO IN VP_PROYECTOS.ID%TYPE,
                               UN_TIPO IN VARCHAR2) RETURN DATE
    AS
        MI_FECHA    DATE;
        MI_MSGERROR PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN
        BEGIN
            SELECT CASE UN_TIPO
                       WHEN 'LIMITE' THEN FECHA_FINAL_PROYECTO
                       WHEN 'AMNISTIA' THEN FECHA_FINAL_AMNISTIA
                       ELSE NULL END
            INTO MI_FECHA
            FROM VP_PROYECTOS
            WHERE ID = UN_ID_PROYECTO;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;
                EXCEPTION
                    WHEN PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
                        MI_MSGERROR(1).CLAVE := 'PROYECTO';
                        MI_MSGERROR(1).VALOR := UN_ID_PROYECTO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                            , UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_NO_PROYECTO
                            , UN_REEMPLAZOS => MI_MSGERROR
                            );
                END;
        END;
        IF MI_FECHA IS NULL THEN
            MI_MSGERROR(1).CLAVE := 'TIPO';
            MI_MSGERROR(1).VALOR := UN_TIPO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE
                , UN_ERROR_COD => PCK_ERRORES.ERR_PLUSVALIA_FECHA_PROY
                , UN_REEMPLAZOS => MI_MSGERROR
                );
        END IF;
        RETURN MI_FECHA;
    END FC_FECHA_PROYECTO;


END PCK_PLUSVALIA_CAL;