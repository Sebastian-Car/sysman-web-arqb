create or replace PACKAGE BODY PCK_ACUERDO_GEN AS

FUNCTION FC_CREAACUERDO_PLUSVA
/*
    NAME              : FC_CREAACUERDO_PLUSVA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 17/05/2019                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PERMITE CREAR UN ACUERDO DE PAGO
    @NAME:    crearAcuerdo
    @METHOD:  POST
    */
(
    UN_COMPANIA              IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ID_FACTURA            IN  GN_ACUERDO.REFERENCIA%TYPE,
    UN_RESOLUCION            IN  GN_ACUERDO.RESOLUCION%TYPE,
    UN_CUOTAINICIAL          IN  GN_ACUERDO.CUOTAINICIAL%TYPE DEFAULT 0,
    UN_NCUOTAS               IN  GN_ACUERDO.NCUOTAS%TYPE,
    UN_FECHA                 IN  GN_ACUERDO.FECHA%TYPE,
    UN_INTERES               IN  GN_ACUERDO.INTERES%TYPE,
    UN_MODELOINTERESDEUDA    IN  GN_ACUERDO.MODELOINTERESDEUDA%TYPE,
    UN_USUARIO               IN  PCK_SUBTIPOS.TI_USUARIO,
    UN_PRELIQUIDAR           IN  PCK_SUBTIPOS.TI_LOGICO DEFAULT 0
)RETURN CLOB
AS
    MI_MSGERROR          PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_ACUERDO           TI_ACUERDO;
    MI_DETALLE           TI_ACUERDOFAC;
    MI_I                 PCK_SUBTIPOS.TI_ENTERO;
    MI_BENEFICIARIO      VP_BENEFICIARIOS.ID%TYPE;
    MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES           PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;     
    MI_SALIDA            CLOB;
BEGIN
    /*
    * ARMAR ACUERDO
    */
    MI_ACUERDO.COMPANIA     := UN_COMPANIA;
    MI_ACUERDO.REFERENCIA   := UN_ID_FACTURA;
    MI_ACUERDO.RESOLUCION   := UN_RESOLUCION;
    MI_ACUERDO.CUOTAINICIAL := UN_CUOTAINICIAL;
    MI_ACUERDO.NCUOTAS      := UN_NCUOTAS;
    MI_ACUERDO.FECHA        := UN_FECHA;
    MI_ACUERDO.INTERES      := UN_INTERES;
    MI_ACUERDO.CUOTAINICIAL := NVL(UN_CUOTAINICIAL,0);    
    MI_ACUERDO.MODELOINTERESDEUDA := UN_MODELOINTERESDEUDA;
    MI_ACUERDO.USUARIO            := UN_USUARIO;
    /*
    * SI SE DESEA QUE EN EL ACUERDO SE INCLUYA EL DESCUENTO SE DEBERIA ACTIVAR LA SIGUIENTE LINEA
    * MI_ACUERDO.INCLUYEDESCUENTO   :=-1;
    */

    BEGIN
        /*
        * SE IDENTIFICA LA REFERENCIA2 COMO LA CLASE DEL PROYECTO Y DE ACUERDO A LA MISMA SE DETERMINA LA APLICACION
        */
        SELECT CLASE_PROYECTO, 
               CASE WHEN CLASE_PROYECTO = 44 THEN 61 ELSE CASE WHEN CLASE_PROYECTO = 45 THEN 62 ELSE NULL END END,
               VP_BENEFICIARIOS.TERCERO,
               VP_BENEFICIARIOS.SUCURSAL,
               VP_BENEFICIARIOS.ID
        INTO MI_ACUERDO.REFERENCIA2, 
             MI_ACUERDO.APLICACION,
             MI_ACUERDO.TERCERO,
             MI_ACUERDO.SUCURSAL,
             MI_BENEFICIARIO
        FROM VP_FACTURA INNER JOIN VP_BENEFICIARIOS
          ON VP_FACTURA.ID_BENEFICIARIOS    = VP_BENEFICIARIOS.ID 
        WHERE VP_FACTURA.COMPANIA = MI_ACUERDO.COMPANIA
          AND VP_FACTURA.ID       = MI_ACUERDO.REFERENCIA
          AND VP_BENEFICIARIOS.EN_ACUERDO IN(0);    
        /*
        * SE VALIDA SI EXISTE BENEFICIARIO Y NO TIENE ACUERDO DE PAGO
        */
    EXCEPTION WHEN NO_DATA_FOUND THEN
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_PLUSVALIA;            
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PLUSVALIA THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                      ,UN_ERROR_COD   => PCK_ERRORES.ERR_VALIDABENEFICIARIOACUER
                                      ,UN_REEMPLAZOS  => MI_MSGERROR  
                                      );     
        END; 
    END; 

    MI_I              := 0;
    IF MI_ACUERDO.APLICACION IN (61, 62) THEN        
        FOR RS IN(  SELECT MI_ACUERDO.REFERENCIA, VP_CONCEPTOS.ID, 
                           VP_DETALLE_FACTURA.VALOR, VP_CONCEPTOS.CLASE_CONCEPTO,
                           VP_CONCEPTOS.NOMBRE
                    FROM VP_CONCEPTOS INNER JOIN VP_DETALLE_FACTURA
                      ON VP_CONCEPTOS.ID = VP_DETALLE_FACTURA.ID_CONCEPTO
                    INNER JOIN VP_FACTURA
                       ON VP_FACTURA.ID     = VP_DETALLE_FACTURA.ID_FACTURA 
                    INNER JOIN VP_BENEFICIARIOS
                       ON VP_FACTURA.ID_BENEFICIARIOS    = VP_BENEFICIARIOS.ID  
                    WHERE VP_DETALLE_FACTURA.COMPANIA    = MI_ACUERDO.COMPANIA
                      AND VP_DETALLE_FACTURA.ID_FACTURA  = MI_ACUERDO.REFERENCIA
                      AND VP_BENEFICIARIOS.EN_ACUERDO IN(0)
                    ORDER BY VP_CONCEPTOS.CLASE_CONCEPTO, VP_CONCEPTOS.PRIORIDAD
        ) LOOP
            MI_DETALLE.REFERENCIA    := MI_ACUERDO.REFERENCIA;
            MI_DETALLE.CONCEPTO      := RS.ID;
            MI_DETALLE.CONCEPTO_NOM  := RS.NOMBRE;
            MI_DETALLE.CLASE         := RS.CLASE_CONCEPTO;
            MI_DETALLE.VALOR         := RS.VALOR;            
            MI_DETALLE.PORDISTRIBUIR := RS.VALOR; 
            MI_I := MI_I + 1;
            MI_ACUERDO.DETALLE(MI_I) := MI_DETALLE;
        END LOOP;        
    END IF;   

    IF UN_PRELIQUIDAR = 0 THEN
        MI_SALIDA:= TO_CLOB(TO_CHAR(FC_CREARACUERDO(UN_ACUERDO         => MI_ACUERDO)));
        BEGIN
            BEGIN

                MI_CAMPOS:= 'EN_ACUERDO = -1 ,'
                         || 'MODIFIED_BY = '''|| UN_USUARIO ||''',
                             DATE_MODIFIED = SYSDATE ';
                MI_CONDICION := 'ID = ' || MI_BENEFICIARIO ;
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'VP_BENEFICIARIOS'
                                                      ,UN_ACCION    => 'M'
                                                      ,UN_CAMPOS    => MI_CAMPOS
                                                      ,UN_CONDICION => MI_CONDICION);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_GENERAL;
            END;   
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                      ,UN_ERROR_COD   => PCK_ERRORES.ERR_ACTUALIZACUOTASINRECIBO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR  
                                        );    
        END;
    ELSE
        MI_SALIDA := FC_PRELIQUIDAACUERDO(UN_ACUERDO         => MI_ACUERDO); 
    END IF;    

    RETURN MI_SALIDA;    
END FC_CREAACUERDO_PLUSVA;


FUNCTION FC_CREARACUERDO
/*
    NAME              : FC_CREARACUERDO
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 21/05/2019                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PERMITE CREAR UN ACUERDO DE PAGO UTILIZANDO LA FUNCIÓN DE FC_PREPARARACUERDO
                        LA PREPARACION SE REALIZA POR SEPARADO PARA SACAR EL TEMA DE PREVISUALIZAR EL ACUERDO 
                        ANTES DE HACERLO VALIDO
    @NAME:    crearAcuerdo
    @METHOD:  POST
    */
(
    UN_ACUERDO         IN TI_ACUERDO
)RETURN NUMBER
AS
    MI_FACTURADOS       TI_GN_FACTURADO;
    MI_ACUERDO          TI_ACUERDO;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;   
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION; 
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA; 
    MI_RTA              VARCHAR2(32000 CHAR);

BEGIN
    MI_TABLA   := 'GN_ACUERDO';

    MI_ACUERDO := FC_PREPARARACUERDO(UN_ACUERDO         => UN_ACUERDO);
    /*
    * SE CONSULTA EL CONSECUTIVO DEL ACUERDO
    */
    MI_ACUERDO.ID := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => MI_TABLA,
                                                      UN_CRITERIO => '',  
                                                      UN_CAMPO    => 'ID',
                                                      UN_INICIAL  => 1);

    /*
    * SE CONSULTA EL CONSECUTIVO DEL ACUERDO
    */
    MI_ACUERDO.CODIGO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => MI_TABLA,
                                                          UN_CRITERIO => 'COMPANIA    = ''' || MI_ACUERDO.COMPANIA    || '''
                                                                      AND APLICACION  = ''' || MI_ACUERDO.APLICACION  || '''
                                                                      AND REFERENCIA2 = ''' || MI_ACUERDO.REFERENCIA2 || '''',  
                                                          UN_CAMPO    => 'CODIGO',
                                                          UN_INICIAL  => TO_NUMBER(TO_CHAR(MI_ACUERDO.FECHA,'YYYY') || '000001'));

    /*
    * SE CREA EL REGISTRO DEL ACUERDO DE PAGO
    */        
    BEGIN  
        MI_CAMPOS := 'ID,
                      COMPANIA,
                      APLICACION,
                      REFERENCIA,
                      REFERENCIA2,
                      CODIGO,
                      RESOLUCION,
                      CUOTAINICIAL,
                      NCUOTAS,
                      FECHA,
                      INTERES,
                      MODELOINTERESDEUDA,
                      TERCERO,
                      SUCURSAL,
                      CREATED_BY,
                      DATE_CREATED';
        MI_VALORES := MI_ACUERDO.ID              || ' ,
               ''' || MI_ACUERDO.COMPANIA        || ''',
                 ' || MI_ACUERDO.APLICACION      || '  ,
               ''' || MI_ACUERDO.REFERENCIA      || ''',
               ''' || MI_ACUERDO.REFERENCIA2     || ''',
                 ' || MI_ACUERDO.CODIGO          || '  ,
               ''' || MI_ACUERDO.RESOLUCION      || ''',
                 ' || MI_ACUERDO.CUOTAINICIAL    || '  ,
                 ' || MI_ACUERDO.NCUOTAS         || '  ,
                 TO_DATE(''' || TO_CHAR(MI_ACUERDO.FECHA, 'DD/MM/YYYY')   || ''', ''DD/MM/YYYY'') ,
                 ' || MI_ACUERDO.INTERES         ||'  ,
               ''' || MI_ACUERDO.MODELOINTERESDEUDA || ''',   
               ''' || MI_ACUERDO.TERCERO            || ''',   
               ''' || MI_ACUERDO.SUCURSAL           || ''',   
               ''' || MI_ACUERDO.USUARIO         || ''',
                   SYSDATE' ;    
        BEGIN
            MI_RTA :=   PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                          UN_ACCION    => 'I', 
                                          UN_CAMPOS    => MI_CAMPOS, 
                                          UN_VALORES   => MI_VALORES
                                          );
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_GENERAL;
        END;
    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_GENERAL THEN
        MI_MSGERROR(1).CLAVE := 'REFERENCIA';
        MI_MSGERROR(1).VALOR := MI_ACUERDO.REFERENCIA;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                  ,UN_ERROR_COD   => PCK_ERRORES.ERR_CREARACUERDO
                                  ,UN_REEMPLAZOS  => MI_MSGERROR  
                                  );             
    END;  

    /*
    * SE CREA LOS REGISTROS DE ACUERDOCUOTA
    */        
    BEGIN  
        MI_TABLA  := 'GN_ACUERDOCUOTA';
        MI_CAMPOS := 'ACUERDO,
                      CUOTA,
                      FECHACUOTA,
                      CAPITAL,
                      INTERESES,
                      TOTAL,    
                      INTERES_ACUERDO,
                      CREATED_BY,
                      DATE_CREATED';
        <<CUOTAS>>
        IF (MI_ACUERDO.CUOTA.COUNT > 0) THEN  
            FOR I IN MI_ACUERDO.CUOTA.FIRST .. MI_ACUERDO.CUOTA.LAST LOOP                       
                MI_VALORES := MI_ACUERDO.ID                 || ' ,
                         ' || MI_ACUERDO.CUOTA(I).CUOTA     || ' ,
                         TO_DATE(''' || TO_CHAR(MI_ACUERDO.CUOTA(I).FECHACUOTA, 'DD/MM/YYYY')   || ''', ''DD/MM/YYYY'') ,                 
                         ' || MI_ACUERDO.CUOTA(I).CAPITAL   || '  ,
                         ' || MI_ACUERDO.CUOTA(I).INTERESES || '  ,
                         ' || MI_ACUERDO.CUOTA(I).TOTAL     || '  , 
                         ' || MI_ACUERDO.CUOTA(I).INTERES_ACUERDO || '  ,                          
                       ''' || MI_ACUERDO.USUARIO            || ''',
                              SYSDATE  ' ;    
                BEGIN
                    MI_RTA :=   PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'I', 
                                                  UN_CAMPOS    => MI_CAMPOS, 
                                                  UN_VALORES   => MI_VALORES
                                                  );
                EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_GENERAL;
                END;
            END LOOP CUOTAS;
        END IF;
    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_GENERAL THEN
        MI_MSGERROR(1).CLAVE := 'REFERENCIA';
        MI_MSGERROR(1).VALOR := MI_ACUERDO.REFERENCIA;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                  ,UN_ERROR_COD   => PCK_ERRORES.ERR_CREARACUERDO
                                  ,UN_REEMPLAZOS  => MI_MSGERROR  
                                  );
    END; 

        /*
    * SE CREA LOS REGISTROS DE ACUERDOFACTURADO
    */        
    BEGIN 
        MI_TABLA  := 'GN_ACUERDOFACTURADO';
        MI_CAMPOS := 'ACUERDO,
                      CUOTA,
                      REFERENCIA,
                      CONCEPTO,
                      CONCEPTO_NOM,
                      VALOR,
                      INTERES_ACUERDO,
                      DESCUENTO,                      
                      CLASE,                      
                      CREATED_BY,
                      DATE_CREATED';
        <<FACTURADO>>
        IF (MI_ACUERDO.FACTURADO.COUNT > 0) THEN  
            FOR I IN MI_ACUERDO.FACTURADO.FIRST .. MI_ACUERDO.FACTURADO.LAST LOOP                       
                MI_VALORES := MI_ACUERDO.ID                             || ' ,
                         ' || MI_ACUERDO.FACTURADO(I).CUOTA             || ' ,
                       ''' || MI_ACUERDO.FACTURADO(I).REFERENCIA        || ''',
                         ' || MI_ACUERDO.FACTURADO(I).CONCEPTO          || '  ,
                       ''' || MI_ACUERDO.FACTURADO(I).CONCEPTO_NOM      || ''',
                         ' || MI_ACUERDO.FACTURADO(I).VALOR             || '  ,
                         ' || MI_ACUERDO.FACTURADO(I).INTERES_ACUERDO   || '  ,
                         ' || NVL(MI_ACUERDO.FACTURADO(I).DESCUENTO,0)  || '  ,
                       ''' || MI_ACUERDO.FACTURADO(I).CLASE             || ''',
                       ''' || MI_ACUERDO.USUARIO            || ''',
                              SYSDATE  ' ;    
                BEGIN
                    MI_RTA :=   PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'I', 
                                                  UN_CAMPOS    => MI_CAMPOS, 
                                                  UN_VALORES   => MI_VALORES
                                                  );
                EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_GENERAL;
                END;
            END LOOP FACTURADO;
        END IF;
    EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_GENERAL THEN
        MI_MSGERROR(1).CLAVE := 'REFERENCIA';
        MI_MSGERROR(1).VALOR := MI_ACUERDO.REFERENCIA;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                  ,UN_ERROR_COD   => PCK_ERRORES.ERR_CREARACUERDO
                                  ,UN_REEMPLAZOS  => MI_MSGERROR  
                                  );
    END; 
    RETURN MI_ACUERDO.ID;                    
END FC_CREARACUERDO;


FUNCTION FC_PREPARARACUERDO
/*
    NAME              : FC_PREPARARACUERDO
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 21/05/2019                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : REALIZA LA PREPARACION POR SEPARADO PARA SACAR EL TEMA DE PREVISUALIZAR EL ACUERDO 
                        ANTES DE HACERLO VALIDO
    @NAME:    prepararAcuerdo
    @METHOD:  POST
    */
(
    UN_ACUERDO         IN TI_ACUERDO
)RETURN TI_ACUERDO
AS
    MI_FACTURADOS       TI_GN_FACTURADO;
    MI_ACUERDO          TI_ACUERDO;
    MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;   
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION; 
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA; 
    MI_RTA              VARCHAR2(32000 CHAR);
    MI_ACUERDOCREADO    GN_ACUERDO.ID%TYPE; 
BEGIN
    MI_TABLA   := 'GN_ACUERDO';
    MI_ACUERDO := UN_ACUERDO;
    /*
    * SE VALIDA SI EXISTE UN ACUERDO CREADO PARA LAS REFERENCIAS DADAS
    */
    BEGIN
        SELECT CODIGO 
        INTO MI_ACUERDOCREADO
        FROM GN_ACUERDO
        WHERE COMPANIA    = MI_ACUERDO.COMPANIA 
          AND APLICACION  = MI_ACUERDO.APLICACION
          AND REFERENCIA  = MI_ACUERDO.REFERENCIA
          AND REFERENCIA2 = MI_ACUERDO.REFERENCIA2
          AND ANULADO     IN(0);            
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_ACUERDOCREADO := -99;
    END;
    IF MI_ACUERDOCREADO <> -99  THEN
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_GENERAL;            
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_GENERAL THEN
            MI_MSGERROR(1).CLAVE := 'ACUERDO';
            MI_MSGERROR(1).VALOR := MI_ACUERDOCREADO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                      ,UN_ERROR_COD   => PCK_ERRORES.ERR_VALIDAEXISTENCIAACUERDO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR  
                                      );     
        END; 
    END IF;

    /*
    * VALIDA QUE SE TENGA UNA DISTRIBUCION CONOCIDA PARA LOS INTERES ADEUDADOS AL MOMENTO DE REALIZAR EL ACUERDO
    * LOS VALORES VALIDOS SON:
    *                        IGUAL        : QUE IMPLICA QUE EN CADA CUOTA SE COBRARA EL MISMO VALOR (INTERESADEUDADO/N CUOTAS)
    *                        PROPORCIONAL : QUE IMPLICA QUE EN CADA CUOTA SE COBRARA EN LA MISMA PROPORCION QUE SE HACE EL COBRO DE CAPITAL
    */
    IF TRIM(NVL(MI_ACUERDO.MODELOINTERESDEUDA,' ')) = NULL  THEN         
        BEGIN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_GENERAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE
                                      ,UN_ERROR_COD   => PCK_ERRORES.ERR_VALIDATIPODEDISTRACUERDO
                                        );           
        END;
    END IF;
    /*
    * DE ACUERDO A LAS CLASES DE IDENTIFICAN EL TOTAL CAPITAL E INTERES PARA 
    * LUEGO UTILIZAR EN LOS PRORRATEOS
    */
    IF (MI_ACUERDO.DETALLE.COUNT > 0) THEN  
        FOR I IN MI_ACUERDO.DETALLE.FIRST .. MI_ACUERDO.DETALLE.LAST LOOP
            IF MI_ACUERDO.DETALLE(I).CLASE = 47 THEN
                MI_ACUERDO.TOTALCAPITAL := NVL(MI_ACUERDO.TOTALCAPITAL,0) + MI_ACUERDO.DETALLE(I).VALOR;
                MI_ACUERDO.TOTALDISTRIBUIR := NVL(MI_ACUERDO.TOTALDISTRIBUIR,0) + (MI_ACUERDO.DETALLE(I).VALOR);
            ELSIF MI_ACUERDO.DETALLE(I).CLASE = 48 THEN
                MI_ACUERDO.TOTALINTERES := NVL(MI_ACUERDO.TOTALINTERES,0) + MI_ACUERDO.DETALLE(I).VALOR;
                MI_ACUERDO.TOTALDISTRIBUIR := NVL(MI_ACUERDO.TOTALDISTRIBUIR,0) + (MI_ACUERDO.DETALLE(I).VALOR);
            ELSIF MI_ACUERDO.DETALLE(I).CLASE = 49 AND MI_ACUERDO.INCLUYEDESCUENTO NOT IN(0) THEN
                MI_ACUERDO.TOTALDESCUENTO := NVL(MI_ACUERDO.TOTALDESCUENTO,0) + MI_ACUERDO.DETALLE(I).VALOR;
                MI_ACUERDO.TOTALCAPITAL := NVL(MI_ACUERDO.TOTALCAPITAL,0) + MI_ACUERDO.DETALLE(I).VALOR;
                MI_ACUERDO.TOTALDISTRIBUIR := NVL(MI_ACUERDO.TOTALDISTRIBUIR,0) + (MI_ACUERDO.DETALLE(I).VALOR);                
            END IF;
        END LOOP;     
    END IF;
    /*
    * CONTROLA QUE LA CUOTA INICIAL NO SEA SUPERIOR AL VALOR A DISTRIBUIR
    */
    IF MI_ACUERDO.TOTALDISTRIBUIR < MI_ACUERDO.CUOTAINICIAL THEN
        BEGIN
            BEGIN
                RAISE PCK_EXCEPCIONES.EXC_GENERAL;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN
            MI_MSGERROR(1).CLAVE := 'INICIAL';
            MI_MSGERROR(1).VALOR := MI_ACUERDO.CUOTAINICIAL;
            MI_MSGERROR(2).CLAVE := 'TOTAL';
            MI_MSGERROR(2).VALOR := MI_ACUERDO.TOTALDISTRIBUIR;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE
                                      ,UN_ERROR_COD   => PCK_ERRORES.ERR_VALIDACUOTAINICIALMAYOR
                                      ,UN_REEMPLAZOS  => MI_MSGERROR  
                                        );           
        END;
    END IF;
    MI_ACUERDO := FC_PRORRATEACUOTAFIJA(UN_ACUERDO         => MI_ACUERDO);

    RETURN MI_ACUERDO;                    
END FC_PREPARARACUERDO;


FUNCTION FC_PRORRATEACUOTAFIJA
/*
    NAME              : FC_PRORRATEACUOTAFIJA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 27/05/2019                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PERMITE PRORRATEAR EN CUOTAS IGUALES NO SE DEBERIA UTILIZAR
    @NAME:    prorrateoGeneral
    @METHOD:  POST
    */
(
    UN_ACUERDO         IN TI_ACUERDO
)RETURN TI_ACUERDO
AS
    --MI_ACUERDOFACTURADO   GN_ACUERDOFACTURADO%ROWTYPE;
    --MI_FACTURADOS         TI_GN_FACTURADO;    
    MI_ACUERDO            TI_ACUERDO;
    MI_CUOTA_TOT          PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_CUOTA_TOT_PROP     PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_CUOTA_INTERESIGUAL PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_PORDISTRIBUIR_ABS  PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_PORCENTAJE         NUMBER:=0;
    MI_CUOTA_INI          NUMBER;
    MI_REG                NUMBER :=0;
    MI_SALDO_CAPITAL      NUMBER :=0;
    MI_SALDO_INTERES      NUMBER :=0;    
BEGIN
    MI_ACUERDO := UN_ACUERDO;    
    /*
    * CUOTA INICIAL
    */
    MI_CUOTA_INI :=1;
    IF MI_ACUERDO.CUOTAINICIAL>0 THEN
        MI_CUOTA_INI :=0;
    END IF;

    MI_SALDO_CAPITAL := MI_ACUERDO.TOTALCAPITAL;
    MI_SALDO_INTERES := MI_ACUERDO.TOTALINTERES;
    MI_ACUERDO.TOTALINTERESRECARGO := 0; 
    MI_ACUERDO.TOTALAPAGAR         := 0;
    /*
    * SE EXCLUYE LOS VALORES DE LA CUOTA INICIAL PARA AMORTIZAR SOBRE EL SALDO REAL
    */
    IF MI_CUOTA_INI = 0 THEN
        MI_ACUERDO.CUOTA(MI_CUOTA_INI).TOTAL           := MI_ACUERDO.CUOTAINICIAL;
        MI_ACUERDO.CUOTA(MI_CUOTA_INI).CAPITAL         := ROUND(MI_ACUERDO.CUOTAINICIAL * (NVL(MI_ACUERDO.TOTALCAPITAL, 0) / MI_ACUERDO.TOTALDISTRIBUIR),0);
        MI_ACUERDO.CUOTA(MI_CUOTA_INI).INTERES_ACUERDO := 0;
        MI_ACUERDO.CUOTA(MI_CUOTA_INI).INTERESES       := MI_ACUERDO.CUOTA(MI_CUOTA_INI).TOTAL - (MI_ACUERDO.CUOTA(MI_CUOTA_INI).INTERES_ACUERDO + MI_ACUERDO.CUOTA(MI_CUOTA_INI).CAPITAL);             
        MI_ACUERDO.TOTALINTERESACUERDO := MI_ACUERDO.TOTALINTERESACUERDO +MI_ACUERDO.CUOTA(MI_CUOTA_INI).INTERES_ACUERDO; 
        MI_SALDO_CAPITAL              := MI_SALDO_CAPITAL - MI_ACUERDO.CUOTA(MI_CUOTA_INI).CAPITAL;
        MI_SALDO_INTERES              := MI_SALDO_INTERES - MI_ACUERDO.CUOTA(MI_CUOTA_INI).INTERESES;           
        MI_ACUERDO.CUOTA(MI_CUOTA_INI).INTERES_RECARGO := 0;
        MI_ACUERDO.TOTALAPAGAR   := MI_ACUERDO.CUOTA(MI_CUOTA_INI).TOTAL;
    END IF;
    /*
    * SE CALCULA LA CUOTA FIJA DE LA AMORTIZACION
    */
    IF MI_ACUERDO.INTERES <> 0 THEN
        MI_CUOTA_TOT := ROUND(MI_SALDO_CAPITAL*((MI_ACUERDO.INTERES*POWER(1 + MI_ACUERDO.INTERES, MI_ACUERDO.NCUOTAS))/(POWER(1+MI_ACUERDO.INTERES, MI_ACUERDO.NCUOTAS)-1)),0); 
    ELSE
        MI_CUOTA_TOT          := ROUND((MI_SALDO_CAPITAL) / MI_ACUERDO.NCUOTAS,0);        
    END IF;
    /*
    * SE SUMA LA PROOPORCION DE LOS INTERESES QUE ESTAN EN LA DEUDA
    * OJO NO SON DEL ACUERDO NI DEL RECARGO POR MORA EN LAS CUOTAS
    */
    MI_CUOTA_INTERESIGUAL := ROUND(MI_SALDO_INTERES / MI_ACUERDO.NCUOTAS ,0);
    MI_CUOTA_TOT_PROP     := ROUND(MI_CUOTA_TOT + MI_CUOTA_INTERESIGUAL  ,0);

    <<CUOTAS>>
    FOR MI_CUO IN MI_CUOTA_INI .. MI_ACUERDO.NCUOTAS LOOP
        MI_ACUERDO.CUOTA(MI_CUO).CUOTA      := MI_CUO;
        MI_ACUERDO.CUOTA(MI_CUO).FECHACUOTA := ADD_MONTHS(MI_ACUERDO.FECHA, MI_CUO);
        IF MI_CUO <> 0 THEN
            MI_ACUERDO.CUOTA(MI_CUO).INTERES_RECARGO := 0;
            MI_ACUERDO.CUOTA(MI_CUO).TOTAL           := MI_CUOTA_TOT_PROP;
            MI_ACUERDO.CUOTA(MI_CUO).INTERES_ACUERDO := ROUND(MI_SALDO_CAPITAL * MI_ACUERDO.INTERES,0);
            IF MI_ACUERDO.MODELOINTERESDEUDA ='PROPORCIONAL' THEN
                MI_ACUERDO.CUOTA(MI_CUO).CAPITAL         := ROUND((MI_CUOTA_TOT_PROP - MI_ACUERDO.CUOTA(MI_CUO).INTERES_ACUERDO) * (MI_ACUERDO.TOTALCAPITAL / MI_ACUERDO.TOTALDISTRIBUIR),0);
                --SE REALIZA POR DIFERENCIA PARA EVITAR DIFERENCIAS POR EL REDONDEO
                MI_ACUERDO.CUOTA(MI_CUO).INTERESES       := MI_CUOTA_TOT_PROP - (MI_ACUERDO.CUOTA(MI_CUO).INTERES_ACUERDO + MI_ACUERDO.CUOTA(MI_CUO).CAPITAL);             
            ELSIF MI_ACUERDO.MODELOINTERESDEUDA = 'IGUAL' THEN
                MI_ACUERDO.CUOTA(MI_CUO).CAPITAL         := ROUND((MI_CUOTA_TOT - MI_ACUERDO.CUOTA(MI_CUO).INTERES_ACUERDO),0);
                MI_ACUERDO.CUOTA(MI_CUO).INTERESES       := MI_CUOTA_INTERESIGUAL;
            END IF;
            /*
            * SUBSANA LOS PROBLEMAS DE REDONDEOS EN LA ULTIMA CUOTA
            */
            IF MI_CUO = MI_ACUERDO.NCUOTAS  AND MI_ACUERDO.CUOTA(MI_CUO).CAPITAL <> MI_SALDO_CAPITAL THEN
                MI_ACUERDO.CUOTA(MI_CUO).TOTAL   := MI_ACUERDO.CUOTA(MI_CUO).TOTAL + (MI_SALDO_CAPITAL - MI_ACUERDO.CUOTA(MI_CUO).CAPITAL);
                MI_ACUERDO.CUOTA(MI_CUO).CAPITAL := MI_SALDO_CAPITAL;                
            END IF;
            IF MI_CUO = MI_ACUERDO.NCUOTAS  AND MI_ACUERDO.CUOTA(MI_CUO).INTERESES <> MI_SALDO_INTERES THEN
                MI_ACUERDO.CUOTA(MI_CUO).TOTAL     := MI_ACUERDO.CUOTA(MI_CUO).TOTAL + (MI_SALDO_INTERES - MI_ACUERDO.CUOTA(MI_CUO).INTERESES );
                MI_ACUERDO.CUOTA(MI_CUO).INTERESES := MI_SALDO_INTERES;
            END IF;            
            MI_SALDO_CAPITAL           := MI_SALDO_CAPITAL           - MI_ACUERDO.CUOTA(MI_CUO).CAPITAL;
            MI_SALDO_INTERES           := MI_SALDO_INTERES           - MI_ACUERDO.CUOTA(MI_CUO).INTERESES;     
            MI_ACUERDO.TOTALINTERESACUERDO := MI_ACUERDO.TOTALINTERESACUERDO + MI_ACUERDO.CUOTA(MI_CUO).INTERES_ACUERDO;     
            MI_ACUERDO.TOTALAPAGAR         := MI_ACUERDO.TOTALAPAGAR + MI_ACUERDO.CUOTA(MI_CUO).TOTAL;
        END IF;

        IF (MI_ACUERDO.DETALLE.COUNT > 0) THEN  
            <<CONCEPTOS>>
            FOR I IN MI_ACUERDO.DETALLE.FIRST .. MI_ACUERDO.DETALLE.LAST LOOP
                IF (MI_ACUERDO.DETALLE(I).CLASE = 49 AND MI_ACUERDO.INCLUYEDESCUENTO NOT IN(0)) OR MI_ACUERDO.DETALLE(I).CLASE <> 49 THEN 
                    MI_REG := MI_REG + 1;                        
                    MI_ACUERDO.FACTURADO(MI_REG).CUOTA        := MI_CUO;
                    MI_ACUERDO.FACTURADO(MI_REG).REFERENCIA   := MI_ACUERDO.DETALLE(I).REFERENCIA;
                    MI_ACUERDO.FACTURADO(MI_REG).CONCEPTO     := MI_ACUERDO.DETALLE(I).CONCEPTO;
                    MI_ACUERDO.FACTURADO(MI_REG).CONCEPTO_NOM := MI_ACUERDO.DETALLE(I).CONCEPTO_NOM;
                    MI_ACUERDO.FACTURADO(MI_REG).CLASE        := MI_ACUERDO.DETALLE(I).CLASE;
                    /*
                    * PORCENTAJE DE DISTRIBICIÓN ENTRE CAPITAL E INTERES
                    */
                    MI_ACUERDO.FACTURADO(MI_REG).INTERES_ACUERDO := 0;
                    IF MI_ACUERDO.DETALLE(I).CLASE IN(47, 49) THEN 
                        MI_ACUERDO.FACTURADO(MI_REG).VALOR := ROUND(MI_ACUERDO.CUOTA(MI_CUO).CAPITAL * (MI_ACUERDO.DETALLE(I).VALOR / MI_ACUERDO.TOTALCAPITAL),0);
                        --DISTRIBUYE EL INTERES DE ACUERDO AQUI PUEDE DAR DIFERENCIA DE UN PESO
                        MI_ACUERDO.FACTURADO(MI_REG).INTERES_ACUERDO := ROUND(MI_ACUERDO.CUOTA(MI_CUO).INTERES_ACUERDO * (MI_ACUERDO.FACTURADO(MI_REG).VALOR / MI_ACUERDO.CUOTA(MI_CUO).CAPITAL), 0);                    
                    ELSIF MI_ACUERDO.DETALLE(I).CLASE = 48 THEN 
                        MI_ACUERDO.FACTURADO(MI_REG).VALOR := ROUND(MI_ACUERDO.CUOTA(MI_CUO).INTERESES * (MI_ACUERDO.DETALLE(I).VALOR / MI_ACUERDO.TOTALINTERES),0);
                    END IF;
                    /* 
                    * DIFERENCIAS EN LA ULTIMA CUOTA POR EFECTO DE REDONDEOS 
                    */
                    IF  MI_CUO = MI_ACUERDO.NCUOTAS THEN
                        MI_ACUERDO.FACTURADO(MI_REG).VALOR      := MI_ACUERDO.DETALLE(I).PORDISTRIBUIR;                
                    END IF;
                    MI_ACUERDO.DETALLE(I).PORDISTRIBUIR   := MI_ACUERDO.DETALLE(I).PORDISTRIBUIR - MI_ACUERDO.FACTURADO(MI_REG).VALOR;                                      
                END IF;
            END LOOP CONCEPTOS;     
        END IF;        
    END LOOP CUOTAS;

    RETURN MI_ACUERDO;
END FC_PRORRATEACUOTAFIJA;


FUNCTION FC_FACTURARCUOTA
/*
    NAME              : FC_FACTURARCUOTA
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 22/05/2019                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PERMITE GENERAR LA FACTURA DE UNA O MAS CUOTAS DE UN ACUERDO
    @NAME:    prorrateoGeneral
    @METHOD:  POST
    */
(
    UN_ACUERDO         IN GN_ACUERDO.ID%TYPE,
    UN_CUOTA_FINAL     IN GN_ACUERDOCUOTA.CUOTA%TYPE,
    UN_USUARIO         IN PCK_SUBTIPOS.TI_USUARIO    
)RETURN NUMBER
AS 
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA; 
    MI_RECIBO         GN_RECIBOS%ROWTYPE;    
    MI_RECIBO_DET     GN_RECIBOS_DET%ROWTYPE;    
    MI_RECIBOS_DET    TI_GN_RECIBOS_DET;
    MI_RECIBO_ANULADO GN_RECIBOS.ID%TYPE;
    MI_NUMREGISTRO    NUMBER(5,0):=0;
    MI_RTA            VARCHAR2(200);
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION; 
BEGIN
    /*
    * SE DEFINE QUE LOS PAGOS DE ACUERDO SON TIPO A
    */
    MI_RECIBO.TIPORECIBO := 'A';
    MI_RECIBO.FECHA      := SYSDATE;
    MI_RECIBO.ID_ACUERDO := UN_ACUERDO;

    BEGIN 
        /*
        * ARMAR ESTRUCTURA DEL RECIBO DE PAGO
        */
        SELECT ACU.ID ACUERDO, 
               ACU.APLICACION, 
               ACU.REFERENCIA, 
               ACU.REFERENCIA2,
               ACU.COMPANIA,
               ACU.TERCERO,
               ACU.SUCURSAL,
               MIN(CUO.FECHACUOTA),
               SUM(FAC.VALOR + FAC.INTERES_ACUERDO + FAC.INTERES_RECARGO) TOTAL
        INTO MI_RECIBO.ID_ACUERDO,
             MI_RECIBO.APLICACION,
             MI_RECIBO.REFERENCIA,
             MI_RECIBO.REFERENCIA2,
             MI_RECIBO.COMPANIA,
             MI_RECIBO.TERCERO,
             MI_RECIBO.SUCURSAL,
             MI_RECIBO.FECHALIMITE,
             MI_RECIBO.TOTALAPAGAR
        FROM GN_ACUERDO ACU INNER JOIN GN_ACUERDOCUOTA CUO 
          ON ACU.ID      = CUO.ACUERDO
       INNER JOIN GN_ACUERDOFACTURADO FAC
          ON CUO.ACUERDO = FAC.ACUERDO
         AND CUO.CUOTA   = FAC.CUOTA 
       WHERE ACU.ID     = UN_ACUERDO
         AND FAC.CUOTA <= UN_CUOTA_FINAL
         AND ACU.ANULADO IN(0)
         AND CUO.PAGO    IN(0)
       GROUP BY ACU.ID, 
                ACU.APLICACION, 
                ACU.REFERENCIA, 
                ACU.REFERENCIA2,
                ACU.COMPANIA,
                ACU.TERCERO,
                ACU.SUCURSAL;
        MI_RECIBO.FECHALIMITE :=  CASE WHEN  MI_RECIBO.FECHALIMITE > SYSDATE THEN  MI_RECIBO.FECHALIMITE ELSE SYSDATE END; 
        /*
        * SE VALIDA SI EXISTE EL ACUERDO
        */        
    EXCEPTION WHEN NO_DATA_FOUND THEN
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_GENERAL;            
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_GENERAL THEN
            MI_MSGERROR(1).CLAVE := 'ACUERDO';
            MI_MSGERROR(1).VALOR := UN_ACUERDO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                      ,UN_ERROR_COD   => PCK_ERRORES.ERR_EXISTE_ACUERDO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR  
                                      );     
        END; 
    END;
    <<DETALLES>>
    FOR RS IN ( SELECT FAC.CUOTA, 
                       FAC.REFERENCIA, 
                       FAC.CONCEPTO, 
                       FAC.CONCEPTO_NOM,
                       FAC.VALOR,
                       FAC.CLASE,
                       FAC.INTERES_ACUERDO,
                       FAC.INTERES_RECARGO
                FROM GN_ACUERDO ACU INNER JOIN GN_ACUERDOCUOTA CUO 
                  ON ACU.ID   = CUO.ACUERDO
                INNER JOIN GN_ACUERDOFACTURADO FAC
                  ON CUO.ACUERDO   = FAC.ACUERDO
                 AND CUO.CUOTA     = FAC.CUOTA 
                WHERE ACU.ID = UN_ACUERDO
                  AND FAC.CUOTA <= UN_CUOTA_FINAL
                  AND ACU.ANULADO IN(0)
                  AND CUO.PAGO IN(0)
                ORDER BY FAC.CUOTA
    ) LOOP 
        MI_NUMREGISTRO := MI_NUMREGISTRO + 1;
        MI_RECIBO_DET := NULL;
        MI_RECIBO_DET.CUOTA        := RS.CUOTA;
        MI_RECIBO_DET.REFERENCIA   := RS.REFERENCIA;
        MI_RECIBO_DET.CONCEPTO     := RS.CONCEPTO;
        MI_RECIBO_DET.CONCEPTO_NOM := RS.CONCEPTO_NOM;
        MI_RECIBO_DET.VALOR        := RS.VALOR;
        MI_RECIBO_DET.INTERES_ACUERDO        := NVL(RS.INTERES_ACUERDO,0);
        MI_RECIBO_DET.INTERES_RECARGO        := NVL(RS.INTERES_RECARGO,0);
        MI_RECIBO_DET.CLASE        := RS.CLASE;
        MI_RECIBOS_DET(MI_NUMREGISTRO) :=MI_RECIBO_DET;
    END LOOP DETALLES;       
    MI_RECIBO := FC_INSERTARRECIBO(UN_RECIBO    => MI_RECIBO,
                                   UN_DETALLES  => MI_RECIBOS_DET,
                                   UN_USUARIO   => UN_USUARIO);     
    RETURN MI_RECIBO.ID;
END FC_FACTURARCUOTA;

FUNCTION FC_INSERTARRECIBO
/*
    NAME              : FC_INSERTARRECIBO
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 27/05/2019                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PERMITE REGISTRAR LOS DATOS DE UN RECIBO EN BASE A UN 
                        REGISTRO DE RECIBO DE PAGO BIEN SEA DE UNA FACTURA O DE UN ACUERDO DE PAGO
                        SOLO DEBERA SER INVOCADO DESDE PLSQL PUES SU RETORNO ES UNA ESTRUCTURA
    @NAME:    insertarRecibo
    @METHOD:  POST
    */
(
    UN_RECIBO    IN  GN_RECIBOS%ROWTYPE,
    UN_DETALLES  IN  PCK_ACUERDO_GEN.TI_GN_RECIBOS_DET,
    UN_USUARIO   IN  PCK_SUBTIPOS.TI_USUARIO
)RETURN GN_RECIBOS%ROWTYPE
AS 
    MI_FECHA DATE;
    MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_TABLA         PCK_SUBTIPOS.TI_TABLA; 
    MI_RECIBO        GN_RECIBOS%ROWTYPE;
    MI_DETALLE       GN_RECIBOS_DET%ROWTYPE;
    MI_DETALLES      PCK_ACUERDO_GEN.TI_GN_RECIBOS_DET;
    MI_RECIBO_ANULADO GN_RECIBOS.ID%TYPE;
    MI_NUMREGISTRO   NUMBER(5,0);
    MI_RTA           VARCHAR2(200);
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION; 
BEGIN
    MI_RECIBO   := UN_RECIBO;
    MI_DETALLES := UN_DETALLES; 
    MI_TABLA    := 'GN_RECIBOS';
    IF (MI_DETALLES.COUNT > 0) THEN  
        /*
        * SE CONSULTA EL ID DEL ACUERDO
        */
        MI_RECIBO.ID := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => MI_TABLA,
                                                         UN_CRITERIO => '',  
                                                         UN_CAMPO    => 'ID',
                                                         UN_INICIAL  => '1');
        /*
        * SE CONSULTA EL CONSECUTIVO DEL ACUERDO
        */
        MI_RECIBO.CODIGO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA    => MI_TABLA,
                                                              UN_CRITERIO => 'COMPANIA    = ''' || MI_RECIBO.COMPANIA    || '''
                                                                          AND APLICACION  = ''' || MI_RECIBO.APLICACION  || '''
                                                                          AND REFERENCIA2 = ''' || MI_RECIBO.REFERENCIA2 || '''',  
                                                              UN_CAMPO    => 'CODIGO',
                                                              UN_INICIAL  => TO_NUMBER(TO_CHAR(MI_RECIBO.FECHA,'YYYY') || '000001'));
        /*
        * SE CREA LOS REGISTROS DE RECIBOS
        */        
        BEGIN  
            BEGIN
                MI_CAMPOS := 'ID,
                              COMPANIA,
                              APLICACION,
                              REFERENCIA,
                              REFERENCIA2,
                              CODIGO,
                              FECHA,
                              FECHALIMITE,
                              TOTALAPAGAR,
                              TIPORECIBO,
                              FECHA_LIMITE1,
                              TOTAL1,
                              FECHA_LIMITE2,
                              TOTAL2,
                              ID_ACUERDO,
                              TERCERO,
                              SUCURSAL,
                              CREATED_BY,
                              DATE_CREATED';
                MI_VALORES := MI_RECIBO.ID           || ' ,
                       ''' || MI_RECIBO.COMPANIA     || ''' ,
                         ' || MI_RECIBO.APLICACION   || '  ,
                       ''' || MI_RECIBO.REFERENCIA   || ''' ,
                       ''' || MI_RECIBO.REFERENCIA2  || ''' ,
                         ' || MI_RECIBO.CODIGO       || '  ,
                       TO_DATE(''' || TO_CHAR(MI_RECIBO.FECHA,       'DD/MM/YYYY')   || ''', ''DD/MM/YYYY'') ,
                       TO_DATE(''' || TO_CHAR(MI_RECIBO.FECHALIMITE, 'DD/MM/YYYY')   || ''', ''DD/MM/YYYY'') ,
                         ' || MI_RECIBO.TOTALAPAGAR  || '  ,
                       ''' || MI_RECIBO.TIPORECIBO   || ''' ,
                         ' || CASE WHEN MI_RECIBO.FECHA_LIMITE1 IS NULL 
                                   THEN 'NULL' 
                                   ELSE 'TO_DATE(''' || TO_CHAR(MI_RECIBO.FECHA_LIMITE1, 'DD/MM/YYYY')   || ''', ''DD/MM/YYYY'')'
                                   END || ' ,
                         ' || NVL(MI_RECIBO.TOTAL1,0)|| '  ,  
                         ' || CASE WHEN MI_RECIBO.FECHA_LIMITE2 IS NULL 
                                   THEN 'NULL' 
                                   ELSE 'TO_DATE(''' || TO_CHAR(MI_RECIBO.FECHA_LIMITE2, 'DD/MM/YYYY')   || ''', ''DD/MM/YYYY'')'
                                   END || ' ,
                         ' || NVL(MI_RECIBO.TOTAL2,0)|| '  ,  
                         ' || CASE WHEN MI_RECIBO.ID_ACUERDO IS NOT NULL THEN TO_CHAR(MI_RECIBO.ID_ACUERDO) ELSE 'NULL' END || '  ,  
                       ''' || MI_RECIBO.TERCERO   || ''',  
                       ''' || MI_RECIBO.SUCURSAL  || ''',  
                       ''' || MI_RECIBO.CREATED_BY   || ''',
                                  SYSDATE  ' ;                  

                MI_RTA :=   PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                              UN_ACCION    => 'I', 
                                              UN_CAMPOS    => MI_CAMPOS, 
                                              UN_VALORES   => MI_VALORES
                                              );
            EXCEPTION WHEN OTHERS THEN
                RAISE PCK_EXCEPCIONES.EXC_GENERAL;              
            END;              
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_GENERAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                      ,UN_ERROR_COD   => PCK_ERRORES.ERR_INSERTARRECIBO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR  
                                      );
        END;                   
        <<CONCEPTOS>> 
        /*
        * SE CREA LOS REGISTROS DE LOS CONCEPTOS POR RECIBOS
        */        
        BEGIN  
            MI_TABLA  := 'GN_RECIBOS_DET';
            MI_CAMPOS := 'ID_RECIBO,
                          CUOTA,
                          REFERENCIA,
                          CONCEPTO,
                          CONCEPTO_NOM,
                          VALOR,
                          INTERES_ACUERDO,
                          INTERES_RECARGO,
                          CLASE,
                          CREATED_BY,
                          DATE_CREATED';
            FOR I IN MI_DETALLES.FIRST .. MI_DETALLES.LAST LOOP                       
                MI_VALORES := MI_RECIBO.ID                   || ' ,
                         ' || MI_DETALLES(I).CUOTA           || ' ,
                       ''' || MI_DETALLES(I).REFERENCIA      || ''' ,
                         ' || MI_DETALLES(I).CONCEPTO        || ' ,
                       ''' || MI_DETALLES(I).CONCEPTO_NOM    || ''' ,
                         ' || MI_DETALLES(I).VALOR           || ' ,
                         ' || NVL(MI_DETALLES(I).INTERES_ACUERDO, 0) || ' ,
                         ' || NVL(MI_DETALLES(I).INTERES_RECARGO, 0) || ' ,
                         ' || MI_DETALLES(I).CLASE           || ' ,                
                       ''' || MI_RECIBO.CREATED_BY   || ''',
                              SYSDATE  ' ;    
                BEGIN
                    MI_RTA :=   PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                  UN_ACCION    => 'I', 
                                                  UN_CAMPOS    => MI_CAMPOS, 
                                                  UN_VALORES   => MI_VALORES
                                                  );
                EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_GENERAL;
                END;
            END LOOP CONCEPTOS;
        EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_GENERAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                      ,UN_ERROR_COD   => PCK_ERRORES.ERR_INSERTARRECIBODETALLE
                                      ,UN_REEMPLAZOS  => MI_MSGERROR  
                                      );
        END;   
    END IF;   

    MI_RECIBO_ANULADO:= FC_ANULAR_RECIBO(UN_ID_RECIBO  => MI_RECIBO.ID,
                                         UN_USUARIO    => UN_USUARIO);   
    RETURN MI_RECIBO;
END FC_INSERTARRECIBO;


FUNCTION FC_ANULAR_RECIBO 
/*
    NAME              : FC_ANULAR_RECIBO
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 27/05/2019                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PERMITE ANULAR UNA RECIBO EN BASE A UNO CREADO; ES DECIR;
                        ELIMINA EL ANTERIOR SI SE CREO UNO NUEVO
    @NAME:    anularRecibo
    @METHOD:  POST
    */
(
  UN_ID_RECIBO IN  NUMBER,
  UN_USUARIO   IN  PCK_SUBTIPOS.TI_USUARIO
) RETURN NUMBER AS 
    MI_RECIBONUEVO   GN_RECIBOS%ROWTYPE;    
    MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;   
    MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES       PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION     PCK_SUBTIPOS.TI_CONDICION;    
    MI_ID_ANULADO    GN_RECIBOS.ID%TYPE;    
    MI_CUOTAMAXIMA   NUMBER;
BEGIN
    MI_ID_ANULADO:=-1;
    SELECT *
    INTO MI_RECIBONUEVO
    FROM GN_RECIBOS
    WHERE ID = UN_ID_RECIBO;

    SELECT MAX(CUOTA)
    INTO MI_CUOTAMAXIMA
    FROM GN_RECIBOS_DET
    WHERE ID_RECIBO = UN_ID_RECIBO;

    /*
    * ANULA EL RECIBO DE PAGO DE UNA FACTURA DE PLUSVALIA O VALIORIZACION
    */
    IF MI_RECIBONUEVO.APLICACION IN(61, 62) AND MI_RECIBONUEVO.ID_ACUERDO IS NULL THEN
        FOR RS IN ( SELECT REC_ANU.ID, REC_ANU.CODIGO
                    FROM  VP_FACTURA VF 
                    INNER JOIN VP_FACTURA VA
                      ON VF.COMPANIA         = VA.COMPANIA
                     AND VF.CODIGO_PROYECTO  = VA.CODIGO_PROYECTO
                     AND VF.CLASE_PROYECTO   = VA.CLASE_PROYECTO
                     AND VF.ID_FACTURA_AFECT = VA.ID
                     INNER JOIN GN_RECIBOS REC_ANU 
                      ON REC_ANU.REFERENCIA  = VA.ID
                     AND REC_ANU.REFERENCIA2 = VA.CLASE_PROYECTO 
                    WHERE VF.ID             = MI_RECIBONUEVO.REFERENCIA
                      AND VF.CLASE_PROYECTO = MI_RECIBONUEVO.REFERENCIA2
                      AND VF.COMPANIA       = MI_RECIBONUEVO.COMPANIA
                      AND REC_ANU.TIPORECIBO= MI_RECIBONUEVO.TIPORECIBO
                      AND REC_ANU.ANULADO IN(0)
                      AND REC_ANU.PAGO IN(0)
                      AND REC_ANU.ID NOT IN(UN_ID_RECIBO)
                )
        LOOP
            MI_ID_ANULADO := RS.ID;
            PR_ACTUALIZAR_ANULA_RECIBO(UN_ID_RECIBO  => UN_ID_RECIBO,
                                       MI_ID_ANULADO => MI_ID_ANULADO,
                                       UN_USUARIO    => UN_USUARIO);   
        END LOOP;
    END IF;
    /*
    * ANULA LOS RECIBOS DE UN ACUERDO 
    */
    IF MI_RECIBONUEVO.ID_ACUERDO IS NOT NULL THEN
        FOR RS IN ( SELECT REC_ANU.ID, REC_ANU.CODIGO
                    FROM  GN_RECIBOS REC_ANU 
                    WHERE REC_ANU.ID_ACUERDO = MI_RECIBONUEVO.ID_ACUERDO
                      AND REC_ANU.COMPANIA   = MI_RECIBONUEVO.COMPANIA
                      AND REC_ANU.APLICACION = MI_RECIBONUEVO.APLICACION
                      AND REC_ANU.REFERENCIA = MI_RECIBONUEVO.REFERENCIA
                      AND REC_ANU.REFERENCIA2= MI_RECIBONUEVO.REFERENCIA2
                      AND REC_ANU.TIPORECIBO = MI_RECIBONUEVO.TIPORECIBO
                      AND REC_ANU.ANULADO  IN(0)
                      AND REC_ANU.PAGO     IN(0)
                      AND REC_ANU.ID NOT IN(UN_ID_RECIBO)  
                )
        LOOP
            MI_ID_ANULADO := RS.ID;            
            PR_ACTUALIZAR_ANULA_RECIBO(UN_ID_RECIBO  => UN_ID_RECIBO,
                                       MI_ID_ANULADO => MI_ID_ANULADO,
                                       UN_USUARIO    => UN_USUARIO);                
        END LOOP;

        /*
        * LIMPIAR LAS CUOTAS SIN PAGAR QUE TIENE FACTURA ASIGNADA
        */
        FOR RS IN ( SELECT CUO.ACUERDO, 
                           CUO.CUOTA, 
                           CUO.RECIBO_DE_PAGO
                    FROM GN_ACUERDO ACU INNER JOIN  GN_ACUERDOCUOTA CUO
                      ON ACU.ID          = CUO.ACUERDO 
                    WHERE ACU.ID         = MI_RECIBONUEVO.ID_ACUERDO
                      AND ACU.COMPANIA   = MI_RECIBONUEVO.COMPANIA
                      AND ACU.APLICACION = MI_RECIBONUEVO.APLICACION
                      AND ACU.REFERENCIA = MI_RECIBONUEVO.REFERENCIA
                      AND ACU.REFERENCIA2= MI_RECIBONUEVO.REFERENCIA2
                      AND ACU.PAGO     IN(0)
                      AND ACU.ANULADO  IN(0)
                      AND CUO.PAGO     IN(0)
                )
        LOOP
            BEGIN
                BEGIN
                    IF RS.CUOTA <= MI_CUOTAMAXIMA THEN                        
                        MI_CAMPOS:= 'RECIBO_DE_PAGO =' || MI_RECIBONUEVO.ID || ',';
                    ELSE
                        MI_CAMPOS:= 'RECIBO_DE_PAGO = NULL ,';
                    END IF;
                    MI_CAMPOS:= MI_CAMPOS || 'MODIFIED_BY = '''|| UN_USUARIO ||''',
                                              DATE_MODIFIED = SYSDATE ';
                    MI_CONDICION := 'ACUERDO = ' || RS.ACUERDO || '
                                 AND CUOTA   = ' || RS.CUOTA;
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'GN_ACUERDOCUOTA'
                                                          ,UN_ACCION    => 'M'
                                                          ,UN_CAMPOS    => MI_CAMPOS
                                                          ,UN_CONDICION => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_GENERAL;
                END;   
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                          ,UN_ERROR_COD   => PCK_ERRORES.ERR_ACTUALIZACUOTASINRECIBO
                                          ,UN_REEMPLAZOS  => MI_MSGERROR  
                                            );    
            END;
        END LOOP;        
    END IF;  
    RETURN MI_ID_ANULADO;
END FC_ANULAR_RECIBO;

PROCEDURE PR_ACTUALIZAR_ANULA_RECIBO 
/*
    NAME              : PR_ACTUALIZAR_ANULA_RECIBO
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 27/05/2019                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PERMITE ACTUALIAR LOS DATOS DE ANULACION DE UN REGISTRO DE RECIBO DE PAGO 
                        SE LLAMA DESDE FC_ANULAR_RECIBO, PARA NO DUPLICAR EL UPDATE
    @NAME:    actualizarAnularRecibo
    @METHOD:  POST
    */
(
  UN_ID_RECIBO  IN  NUMBER,
  MI_ID_ANULADO IN  NUMBER,
  UN_USUARIO    IN  PCK_SUBTIPOS.TI_USUARIO
)
AS 
    MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;   
    MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES       PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION     PCK_SUBTIPOS.TI_CONDICION; 
    MI_CODIGONUEVO   GN_RECIBOS.CODIGO%TYPE; 
    MI_CODIGOANULADO GN_RECIBOS.CODIGO%TYPE; 
BEGIN

    SELECT CODIGO
    INTO MI_CODIGONUEVO
    FROM GN_RECIBOS
    WHERE ID = UN_ID_RECIBO;

    SELECT CODIGO
    INTO MI_CODIGOANULADO
    FROM GN_RECIBOS
    WHERE ID = MI_ID_ANULADO;

    BEGIN
        BEGIN
            MI_CAMPOS:= 'ANULADO = -1,
                         FECHAANULADO  = TO_DATE(''' || TO_CHAR(SYSDATE,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ,
                         ANULADO_POR   = ''' || UN_USUARIO   || ''', 
                         ID_QUEANULA   = '   || UN_ID_RECIBO || '  ,
                         MODIFIED_BY   = ''' || UN_USUARIO   || ''',
                         DATE_MODIFIED = SYSDATE ';
            MI_CONDICION := ' ID       = ' || MI_ID_ANULADO;
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'GN_RECIBOS'
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_GENERAL;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN
        MI_MSGERROR(1).CLAVE := 'ANULADO';
        MI_MSGERROR(1).VALOR := MI_CODIGOANULADO;
        MI_MSGERROR(2).CLAVE := 'NUEVO';
        MI_MSGERROR(2).VALOR := MI_CODIGONUEVO;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD      => SQLCODE
                                  ,UN_ERROR_COD   => PCK_ERRORES.ERR_ANULAGENRECIBO
                                  ,UN_REEMPLAZOS  => MI_MSGERROR  
                                    );    
    END;   
END PR_ACTUALIZAR_ANULA_RECIBO;


FUNCTION FC_PRELIQUIDAACUERDO
/*
    NAME              : FC_PRELIQUIDAACUERDO
    AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
    DATE MIGRADOR     : 05/06/2019                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PERMITE GENERAR UN ARCHIVO PARA SER EXPUESTO EN EXCEL QUE INDICA COMO SERIA LA LIQUIDACION
                        DE ACUERDO
    @NAME:    preliquidarAcuerdo
    @METHOD:  GET
    */
(
    UN_ACUERDO         IN TI_ACUERDO
)RETURN CLOB
AS
    MI_ACUERDO   TI_ACUERDO;
    MI_SALIDA    CLOB;
    MI_DOSPUNTOS VARCHAR2(5);
    MI_COMA      VARCHAR2(5);
    MI_DOSPUNTOSNUM VARCHAR2(5);
    MI_COMANUM      VARCHAR2(5);
    MI_APLICACION VARCHAR2(200);
    MI_COMPLEMENTO  VARCHAR2(200);
    MI_COMPLEMENTO1 VARCHAR2(200);
    MI_COMPLEMENTO2 VARCHAR2(200);
BEGIN 
    MI_ACUERDO := FC_PREPARARACUERDO(UN_ACUERDO         => UN_ACUERDO);

    /*
    * ESTA PARTE ES PARA ENVIAR DATOS MUY PROPIOS DE CADA MODULO POR TAL MOTIVO SE MANEJA 
    * MI_COMPLEMENTO Y SE ENVIAN AL JSON DE LA MISMA FORMA
    */
    MI_COMPLEMENTO  :='';
    MI_COMPLEMENTO1 :='';
    MI_COMPLEMENTO2 :='';
    IF MI_ACUERDO.APLICACION IN(62, 61) THEN
        /*
        * PARA VALORIZACION Y PLUSVALIA SE TOMA EL CODIGO CATASTRAL, DIRECCION, Y NOMBRE DEL TERCERO
        */
        SELECT VP_BENEFICIARIOS.IP_CODIGO, 
               VP_BENEFICIARIOS.DIRECCION,
               TERCERO.NOMBRE
        INTO MI_COMPLEMENTO,
             MI_COMPLEMENTO1,
             MI_COMPLEMENTO2
        FROM VP_FACTURA INNER JOIN VP_BENEFICIARIOS
          ON VP_FACTURA.ID_BENEFICIARIOS = VP_BENEFICIARIOS.ID
         INNER JOIN TERCERO
          ON VP_BENEFICIARIOS.COMPANIA    = TERCERO.COMPANIA 
         AND VP_BENEFICIARIOS.SUCURSAL    = TERCERO.SUCURSAL
         AND VP_BENEFICIARIOS.TERCERO     = TERCERO.NIT
        WHERE VP_FACTURA.ID  = MI_ACUERDO.REFERENCIA; 
    END IF;

    SELECT NOMBRE
    INTO MI_APLICACION
    FROM APLICACIONES
    WHERE APLICACION = MI_ACUERDO.APLICACION;

    MI_DOSPUNTOS := CHR(34) || ':' || CHR(34);
    MI_COMA      := CHR(34) || ',' || CHR(34); 
    MI_DOSPUNTOSNUM := CHR(34) || ':';
    MI_COMANUM      := ',' || CHR(34); 
    MI_SALIDA  := '{' || CHR(34) || 'acuerdo'   || CHR(34) || ':{' 
                      || CHR(34) || 'id'                  || MI_DOSPUNTOS || '0'
                      || MI_COMA || 'aplicacion'          || MI_DOSPUNTOS || (MI_APLICACION)
                      || MI_COMA || 'referencia2'         || MI_DOSPUNTOS || REPLACE(MI_ACUERDO.REFERENCIA2 , CHR(34), '\' || CHR(34))
                      || MI_COMA || 'referencia'          || MI_DOSPUNTOS || REPLACE(MI_ACUERDO.REFERENCIA  , CHR(34), '\' || CHR(34))
                      || MI_COMA || 'codigo'              || MI_DOSPUNTOS || (MI_ACUERDO.CODIGO)
                      || MI_COMA || 'cuotainical'         || MI_DOSPUNTOS || (MI_ACUERDO.CUOTAINICIAL)
                      || MI_COMA || 'ncuota'              || MI_DOSPUNTOS || (MI_ACUERDO.NCUOTAS)
                      || MI_COMA || 'fecha'               || MI_DOSPUNTOS || TO_CHAR(MI_ACUERDO.FECHA,'DD/MM/YYYY')
                      || MI_COMA || 'porcinteres'         || MI_DOSPUNTOS || (MI_ACUERDO.INTERES)
                      || MI_COMA || 'totalapagar'         || MI_DOSPUNTOS || (MI_ACUERDO.TOTALAPAGAR)
                      || MI_COMA || 'totalcapital'        || MI_DOSPUNTOS || (MI_ACUERDO.TOTALCAPITAL)
                      || MI_COMA || 'totalinteres'        || MI_DOSPUNTOS || (MI_ACUERDO.TOTALINTERES)
                      || MI_COMA || 'totalinteresacuerdo' || MI_DOSPUNTOS || (MI_ACUERDO.TOTALINTERESACUERDO)
                      || MI_COMA || 'totalrecargo'        || MI_DOSPUNTOS || (MI_ACUERDO.TOTALINTERESRECARGO)
                      || MI_COMA || 'incluyedescuento'    || MI_DOSPUNTOS || CASE WHEN MI_ACUERDO.INCLUYEDESCUENTO IN(0) THEN 'NO' ELSE 'SI' END
                      || MI_COMA || 'modelointeres'       || MI_DOSPUNTOS || MI_ACUERDO.MODELOINTERESDEUDA
                      || MI_COMA || 'tercero'             || MI_DOSPUNTOS || MI_ACUERDO.TERCERO
                      || MI_COMA || 'sucursal'            || MI_DOSPUNTOS || MI_ACUERDO.SUCURSAL                      
                      || MI_COMA || 'complemento'         || MI_DOSPUNTOS || REPLACE(MI_COMPLEMENTO , CHR(34), '\' || CHR(34))
                      || MI_COMA || 'complemento1'        || MI_DOSPUNTOS || REPLACE(MI_COMPLEMENTO1, CHR(34), '\' || CHR(34))
                      || MI_COMA || 'complemento2'        || MI_DOSPUNTOS || REPLACE(MI_COMPLEMENTO2, CHR(34), '\' || CHR(34))
                      || MI_COMA || 'cuotas'              || CHR(34) || ':[';

    <<CUOTAS>>
    IF (MI_ACUERDO.CUOTA.COUNT > 0) THEN  
        FOR I IN MI_ACUERDO.CUOTA.FIRST .. MI_ACUERDO.CUOTA.LAST LOOP                       
            MI_SALIDA  := MI_SALIDA  
                          || '{' || CHR(34) || 'numero'         || MI_DOSPUNTOS || (MI_ACUERDO.CUOTA(I).CUOTA)
                                 || MI_COMA || 'fechacuota'     || MI_DOSPUNTOS || TO_CHAR(MI_ACUERDO.CUOTA(I).FECHACUOTA, 'DD/MM/YYYY')
                                 || MI_COMA || 'capital'        || MI_DOSPUNTOS || (MI_ACUERDO.CUOTA(I).CAPITAL)
                                 || MI_COMA || 'interes'        || MI_DOSPUNTOS || (MI_ACUERDO.CUOTA(I).INTERESES)
                                 || MI_COMA || 'interesacuedo'  || MI_DOSPUNTOS || (MI_ACUERDO.CUOTA(I).INTERES_ACUERDO)
                                 || MI_COMA || 'interesrecargo' || MI_DOSPUNTOS || (MI_ACUERDO.CUOTA(I).INTERES_RECARGO)
                                 || MI_COMA || 'total'          || MI_DOSPUNTOS || (MI_ACUERDO.CUOTA(I).TOTAL)
                                 || CHR(34) || '}';  
            IF I = MI_ACUERDO.CUOTA.LAST THEN
                MI_SALIDA  := MI_SALIDA || ']';
            ELSE
                MI_SALIDA  := MI_SALIDA || ',';
            END IF;
        END LOOP CUOTAS;
    END IF;  
    MI_SALIDA  := MI_SALIDA || '}}';
    RETURN MI_SALIDA;
END FC_PRELIQUIDAACUERDO;

END PCK_ACUERDO_GEN;