

create or replace PACKAGE BODY               "PCK_CONTABILIDAD7" 

/**@package: Contabilidad **/

AS

FUNCTION FC_CALCULORETENCIONLEY1819(
    /*
    NAME                    :FC_CALCULORETENCIONLEY1819 (En access CalculoRetencionley1819)     
    AUTHORS                 :STEFANINI SYSMAN SAS
    AUTHORS MIGRACION       :LAURA MELIZA BOTIA PEREZ    DATE MIGRADOR           :22/08/2018
    TIME                    :08:00 AM
    SOURCE MODULE           : CONTABILIDAD (1) - SysmanCT2018.06.07
    MODIFIER                :
    DATA MODIFIED           :MROSERO
    TIME                    :
    DESCRIPCION             :MANEJO DE NUEVA FUENCION CAUSACION AUTOMATICA
    MODIFICATIONS           :
    MODIFIER                :
    TIME                    :
    MODIFICATIONS           :
    */
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_MODULO             IN PCK_SUBTIPOS.TI_MODULO,
    UN_CONSECMENSAJES     IN NUMBER,
    UN_ANO                IN PCK_SUBTIPOS.TI_ANIO,
    UN_FECHA              IN DATE,
    UN_TIPO               IN COMPROBANTE_CNT.TIPO%TYPE,
    UN_NUMERO             IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_TERCERO            IN PCK_SUBTIPOS.TI_TERCERO,
    UN_SUCURSAL           IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_NOMBRETERCERO      IN PLAN_CONTABLE.NOMBRE%TYPE,
    UN_VALORBASE          IN NUMBER,
    UN_VALORBASEIVA       IN PCK_SUBTIPOS.TI_DOBLE,
    UN_CONSECUTIVO        IN NUMBER,
    UN_TIENECONTENIDO     IN NUMBER,
    UN_CUENTA_DEBITO1     IN VARCHAR2,
    UN_CUENTA_CREDITO1    IN VARCHAR2,
    UN_TIPORETENCION      IN COMPROBANTE_CNTRETENCION.TIPORETENCION%TYPE,
    UN_CUENTA_DEBITO      IN VARCHAR2,
    UN_CUENTA_CREDITO     IN VARCHAR2,
    UN_FACTORREDONDEO     IN NUMBER,
    UN_CODIGORETENCION    IN COMPROBANTE_CNTRETENCION.CODIGORETENCION%TYPE,
    UN_DESCRIPCION        IN DETALLE_COMPROBANTE_CNT.DESCRIPCION%TYPE,
    UN_STRCENTRO_COSTO    IN PCK_SUBTIPOS.TI_CENTRO_COSTO,
    UN_NRO_DOCUMENTO      IN DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE,
    UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO,
    UN_CONCEPTO           IN VARCHAR2 DEFAULT '0',
    UN_REFERENCIA         IN PCK_SUBTIPOS.TI_REFERENCIA      DEFAULT PCK_DATOS.CONS_REFERENCIA,
    UN_PROVEEDOR          IN VARCHAR2 DEFAULT '0'										 
)RETURN NUMBER
    AS
    -- MI_ERROR_FUN                    PCK_SUBTIPOS.TI_ERROR_FUN:=GL_ERROR_NUM + 4;
    MI_CALCULORETENCIONESLEY18        NUMBER;
    MI_STRSQL                       PCK_SUBTIPOS.TI_STRSQL;
    MI_RS                           SYS_REFCURSOR;
    MI_RSRESUMEN                    SYS_REFCURSOR;
    MI_RSCONCEPTOS                  SYS_REFCURSOR;
    MI_IBC                          PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_INGRESOBRUTO                 PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_AFC                          PCK_SUBTIPOS.TI_DOBLE := 0.0;
    MI_APORTES                      PCK_SUBTIPOS.TI_DOBLE := 0.0;
    MI_UVT                          PCK_SUBTIPOS.TI_DOBLE := 0.0;
    MI_PORCENSALUD                  PCK_SUBTIPOS.TI_PORCENTAJE := 0.0;
    MI_PORCENPENSION                PCK_SUBTIPOS.TI_PORCENTAJE := 0.0;
    MI_VLRFONDO_PENSIONAL           PCK_SUBTIPOS.TI_DOBLE := 0.0;
    MI_RSVALOR                      SYS_REFCURSOR;
    MI_VALORB                       PCK_SUBTIPOS.TI_DOBLE := 0.0;
    MI_VALORBG                      NUMBER := 0;
    MI_VALORRENTA                   NUMBER := 0;
    MI_RSDET                        SYS_REFCURSOR;
    MI_CUENTA                       VARCHAR2(4000 CHAR);
    MI_SALUD                        NUMBER := 0;
    MI_PORC_RENTAS                  NUMBER := 0;
    MI_PENSION                      NUMBER := 0;
    MI_VALOR                        NUMBER := 0;
    MI_PORCENTAJE                   NUMBER := 0;
    MI_RETENCION                    NUMBER := 0;
    MI_CANTIDAD                     NUMBER := 0;
    MI_VALORRETENER                 NUMBER := 0;
    MI_SALMINIMO                    NUMBER := 0;
    MI_CODIGO_CUENTA                VARCHAR2(4000 CHAR);
    MI_STRNAT                       VARCHAR2(4000 CHAR);
    MI_RSCUENTAPPTAL                SYS_REFCURSOR;
    MI_CUENTAPPTAL                  VARCHAR2(4000 CHAR);
    MI_MES                          PCK_SUBTIPOS.TI_MES;
    MI_VALORACUMULADO               NUMBER := 0;
    MI_FONDOSPENSIONAL              NUMBER := 0;
    MI_BASERETENCION                NUMBER := 0;
    MI_DEPENDIENTES                 NUMBER := 0;
    MI_MEDICINAPREPAGADAYSEGUROS    NUMBER := 0;
    MI_DEPENDIENTE                  NUMBER := 0;
    MI_DEDUCIBLEVIVIENDA            NUMBER := 0;
    MI_APORTESARL                   NUMBER := 0;
    MI_INGRESOBASEDERETENCION       NUMBER := 0;
    MI_INGRESOBASEDERETENCION1      NUMBER := 0;
    MI_INGRESOBASEDERETENCION2      NUMBER := 0;
    MI_RENTASEXENTAS                NUMBER := 0;
    MI_RENTASEXENTASDETRABAJO       NUMBER := 0;
    MI_RENTAEXENTADETRABAJO         NUMBER := 0;
    MI_VALORUVTRENTASEXENTAS        NUMBER := 0;
    MI_VALORRETEFUENTE              NUMBER := 0;
    MI_DECLARARENTA                 NUMBER := 0;
    MI_VALORRETENER2                NUMBER := 0;
    MI_BASEREAL                     NUMBER := 0;
    MI_ANO1                         VARCHAR2(4000 CHAR);
    MI_PORCENTAJEREAL               NUMBER := 0;
    MI_LIMITE_RENTA_EXENTA          NUMBER := 0;
    MI_TIENECONTENIDO               NUMBER := 0;
    MI_APORTESALUD                  NUMBER := 0;
    MI_APORTEPENSION                NUMBER := 0;
    MI_RSDETCOMPANIA                DETALLE_COMPROBANTE_CNT.COMPANIA%TYPE;
    MI_RSDETANO                     NUMBER := 0;
    MI_RSDETTIPO                    DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE;
    MI_RSDETCOMPROBANTE             NUMBER := 0;
    MI_RSDETCONSECUTIVO             NUMBER := 0;
    MI_DATORETENCION                NUMBER := 0;
    MI_CONSECUTIVO                  NUMBER := UN_CONSECUTIVO;
    MI_RTA                          VARCHAR2(4000 CHAR);
    MI_CAMPOS                       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                      PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION                    PCK_SUBTIPOS.TI_CONDICION;
    MI_RTAMSG                       VARCHAR2(200 CHAR);
    MI_DEDUCCIONES                  NUMBER := 0;
    MI_VALORCUARENTABASE            NUMBER := 0;
    MI_DEDUCCIONYRENTAS             NUMBER := 0;
    MI_NOAPORTASALUD                NUMBER:=0;
    MI_NOAPORTAPENSION              NUMBER:=0;
    MI_TABLA                        PCK_SUBTIPOS.TI_TABLA;
    MI_MSGERROR                     PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_MANEJA_CAUSACION             VARCHAR2(2 CHAR);
    MI_FACTORREDONDEO               NUMBER;
    MI_LIMT_VALOR_UVT               NUMBER;--(CFBARRERA_CC:1183_Almacena el valor maximo UVT mensual)
    MI_LIM_25_RE                    NUMBER;--(CFBARRERA_CC:1183_Almacena el valor renta exenta del 25%)
    
BEGIN

      MI_MANEJA_CAUSACION := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>  UN_COMPANIA,
                                                      UN_NOMBRE    => 'MANEJA CAUSACION AUTOMATICA',
                                                      UN_MODULO    =>  PCK_DATOS.MODULOCONTABILIDAD,
                                                      UN_FECHA_PAR =>  SYSDATE),'NO'); 
                                                      
    --TRAIGO EL VALOR IBC Y APORTES VOLUNTARIOS DEL TERCERO
  BEGIN    
  
  IF PCK_CONTABILIDAD3.GL_VARCAUSACION <> 0  THEN  
     --TRAIGO EL VALOR IBC Y APORTES VOLUNTARIOS DE CAUSACION AUTOMATICA 
        SELECT  NVL(CAUSACION_AUTOMATICA.IBC,0),
                NVL(CAUSACION_AUTOMATICA.APORTESVOLUNTARIOS,0),
                NVL(CAUSACION_AUTOMATICA.AFC,0),
                NVL(CAUSACION_AUTOMATICA.DEDUCIBLE_VIVIENDA,0),
                NVL(CAUSACION_AUTOMATICA.MEDPREPAGADA,0),
                NVL(CAUSACION_AUTOMATICA.APORTESARL,0),         
                NVL(TERCERO.DEPENDIENTE,0),
                NVL(TERCERO.NOAPORTASALUD,0),
                NVL(TERCERO.NOAPORTAPENSION,0) 
        INTO MI_IBC,
             MI_APORTES,
             MI_AFC,
             MI_DEDUCIBLEVIVIENDA,
             MI_MEDICINAPREPAGADAYSEGUROS,
             MI_APORTESARL,
             MI_DEPENDIENTE,
             MI_NOAPORTASALUD,
             MI_NOAPORTAPENSION 
         FROM CAUSACION_AUTOMATICA 
            INNER JOIN COMPROBANTE_CNT
            ON COMPROBANTE_CNT.COMPANIA =CAUSACION_AUTOMATICA.COMPANIA
            AND COMPROBANTE_CNT.ANO =CAUSACION_AUTOMATICA.ANO
            AND COMPROBANTE_CNT.NUMERO =CAUSACION_AUTOMATICA.NUMERO_COMPROBANTE
            AND COMPROBANTE_CNT.TIPO =CAUSACION_AUTOMATICA.TIPO_COMPROBANTE
            LEFT JOIN TERCERO ON COMPROBANTE_CNT.COMPANIA = TERCERO.COMPANIA
             AND COMPROBANTE_CNT.TERCERO = TERCERO.NIT
             AND COMPROBANTE_CNT.SUCURSAL = TERCERO.SUCURSAL
        WHERE COMPROBANTE_CNT.COMPANIA    = UN_COMPANIA
          AND COMPROBANTE_CNT.TERCERO     = UN_TERCERO 
          AND COMPROBANTE_CNT.SUCURSAL    = UN_SUCURSAL 
          AND COMPROBANTE_CNT.NRO_DOCUMENTO = UN_NRO_DOCUMENTO
          AND COMPROBANTE_CNT.NUMERO = UN_NUMERO
          AND CAUSACION_AUTOMATICA.REFERENCIADO=UN_REFERENCIA
          AND CAUSACION_AUTOMATICA.CONCEPTO=UN_CONCEPTO
          AND CAUSACION_AUTOMATICA.CENTROCOSTO=UN_STRCENTRO_COSTO
          AND CAUSACION_AUTOMATICA.PROVEEDOR=UN_PROVEEDOR;          
 ELSE
    --TRAIGO EL VALOR IBC Y APORTES VOLUNTARIOS DEL TERCERO
        SELECT  NVL(TERCERO_OPS.IBC,0),
                NVL(TERCERO_OPS.AFC,0),
                NVL(TERCERO_OPS.VALOR,0),
                NVL(TERCERO_OPS.APORTESVOLUNTARIOS,0),
                NVL(TERCERO_OPS.MEDPREPAGADA,0),
                NVL(TERCERO.DEPENDIENTE,0),
                NVL(TERCERO_OPS.DEDUCIBLE_VIVIENDA,0),
                NVL(TERCERO_OPS.APORTESARL,0),
                NVL(TERCERO.NOAPORTASALUD,0),
                NVL(TERCERO.NOAPORTAPENSION,0) 
        INTO MI_IBC,
             MI_AFC,
             MI_VALOR,
             MI_APORTES, 
             MI_MEDICINAPREPAGADAYSEGUROS, 
             MI_DEPENDIENTE, 
             MI_DEDUCIBLEVIVIENDA,                                         
             MI_APORTESARL,
             MI_NOAPORTASALUD,
             MI_NOAPORTAPENSION 
        FROM TERCERO_OPS INNER JOIN TERCERO
          ON TERCERO_OPS.COMPANIA  = TERCERO.COMPANIA
         AND TERCERO_OPS.NIT       = TERCERO.NIT
         AND TERCERO_OPS.SUCURSAL  = TERCERO.SUCURSAL
        WHERE TERCERO_OPS.COMPANIA        = UN_COMPANIA
          AND TERCERO_OPS.NIT             = UN_TERCERO 
          AND TERCERO_OPS.SUCURSAL        = UN_SUCURSAL 
          AND TERCERO_OPS.NUMERO_CONTRATO = UN_NRO_DOCUMENTO;
    END IF;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        BEGIN
            MI_VALORES := UN_CONSECMENSAJES||' , '||
                          PCK_CONTABILIDAD2.FC_ORDENMENSAJES(UN_CONSECMENSAJES )
                                                             || ', ''No se encuentra configurado el contrato número.' 
                                                             || UN_NRO_DOCUMENTO 
                                                             || ' en la información de terceros ' || CHR(10) || CHR(13) 
                                                             || 'Por lo tanto los valores de aportes y deducciones estarán en cero(0)''';
            MI_TABLA  := 'TEMP_CALCULO_RETENCIONES';
            MI_CAMPOS := 'CODIGO,ORDEN,MENSAJE';
            MI_RTAMSG := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                           UN_ACCION  => 'I', 
                                           UN_CAMPOS  => MI_CAMPOS,
                                           UN_VALORES => MI_VALORES
                                           ); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_MENSAJETEMP,
                                        UN_TABLAERROR => MI_TABLA);
        END;
        MI_IBC                        := 0;
        MI_AFC                        := 0;
        MI_VALOR                      := 0;
        MI_APORTES                    := 0;
        MI_MEDICINAPREPAGADAYSEGUROS  := 0;
        MI_DEPENDIENTE                := 0;
        MI_DEDUCIBLEVIVIENDA          := 0;               
        MI_APORTESARL                 := 0;
        MI_NOAPORTASALUD              := 0;
        MI_NOAPORTAPENSION            := 0;
    END;
    MI_INGRESOBRUTO := UN_VALORBASE;  
    BEGIN
        SELECT PORCENTAJESALUD,
               PORCENTAJEPENSION,
               VALORUVT,
               VLRFONDOSOL_PENSIONAL, 
               LIM_25_RE
        INTO MI_PORCENSALUD, 
             MI_PORCENPENSION, 
             MI_UVT, 
             MI_VLRFONDO_PENSIONAL,
             MI_LIM_25_RE
        FROM ANO
        WHERE COMPANIA = UN_COMPANIA
          AND NUMERO   = UN_ANO;      
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_PORCENSALUD        := 0;
        MI_PORCENPENSION      := 0; 
        MI_UVT                := 0; 
        MI_VLRFONDO_PENSIONAL := 0;
    END;

    MI_PORC_RENTAS          := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PORCENTAJE DE RENTAS DE TRABAJO EXENTAS', UN_MODULO, SYSDATE);
    IF MI_PORC_RENTAS IS NULL THEN
        PCK_SYSMAN_UTL.INSERTARINCONSISTENCIAPAR(UN_PAR =>'PORCENTAJE DE RENTAS DE TRABAJO EXENTAS');        
    END IF;

    MI_LIMITE_RENTA_EXENTA  := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'LIMITE DEDUCCIONES MAS RENTAS EXENTAS LEY 1819', UN_MODULO, SYSDATE);
    IF MI_LIMITE_RENTA_EXENTA IS NULL THEN
        PCK_SYSMAN_UTL.INSERTARINCONSISTENCIAPAR(UN_PAR =>'LIMITE DEDUCCIONES MAS RENTAS EXENTAS LEY 1819');        
    END IF;
    MI_SALMINIMO            := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'VALOR SALARIO MINIMO', UN_MODULO, SYSDATE);
    IF NVL(MI_SALMINIMO,0)=0 THEN
        PCK_SYSMAN_UTL.INSERTARINCONSISTENCIAPAR(UN_PAR =>'VALOR SALARIO MINIMO');        
    END IF;

    IF MI_DATORETENCION = 0 AND MI_CONSECUTIVO = 0 THEN
        IF UN_TIENECONTENIDO <> 0 THEN
            IF UN_CUENTA_DEBITO1 IS NOT NULL OR UN_CUENTA_CREDITO1 IS NOT NULL THEN            
                MI_DATORETENCION := MI_DATORETENCION + 1;
                FOR MI_RSDET IN (SELECT DETALLE_COMPROBANTE_CNT.COMPANIA,
                                        DETALLE_COMPROBANTE_CNT.ANO,
                                        TIPO_CPTE,
                                        COMPROBANTE,
                                        CONSECUTIVO
                                 FROM   DETALLE_COMPROBANTE_CNT LEFT JOIN V_PLAN_CONTABLE 
                                   ON DETALLE_COMPROBANTE_CNT.COMPANIA = V_PLAN_CONTABLE.COMPANIA 
                                  AND DETALLE_COMPROBANTE_CNT.ANO      = V_PLAN_CONTABLE.ANO 
                                  AND DETALLE_COMPROBANTE_CNT.ID       = V_PLAN_CONTABLE.ID
                                 WHERE  DETALLE_COMPROBANTE_CNT.COMPANIA    = UN_COMPANIA
                                    AND DETALLE_COMPROBANTE_CNT.ANO         = UN_ANO
                                    AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   = UN_TIPO
                                    AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = UN_NUMERO
                                    AND DETALLE_COMPROBANTE_CNT.CUENTA IN (UN_CUENTA_DEBITO1,UN_CUENTA_CREDITO1)                                
                                 ORDER BY DETALLE_COMPROBANTE_CNT.COMPANIA,
                                          DETALLE_COMPROBANTE_CNT.ANO,
                                          TIPO_CPTE,
                                          COMPROBANTE,
                                          CONSECUTIVO
                                )
                LOOP 
                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => 'DETALLE_COMPROBANTE_CNT', 
                                                UN_ACCION     => 'E', 
                                                UN_CONDICION  =>  'COMPANIA    =''' || MI_RSDET.COMPANIA    || 
                                                           ''' AND ANO         ='   || MI_RSDET.ANO         ||
                                                             ' AND TIPO_CPTE   =''' || MI_RSDET.TIPO_CPTE   || 
                                                           ''' AND COMPROBANTE ='   || MI_RSDET.COMPROBANTE || 
                                                             ' AND CONSECUTIVO ='   || MI_RSDET.CONSECUTIVO);
                END LOOP;
            END IF;
        END IF;
    END IF;

    --SI HAY RETENCIONES ENTRA A CALCULAR LOS VALORES;
    MI_CUENTA  := '';
    IF MI_NOAPORTASALUD IN (0) THEN 
        MI_SALUD   := ROUND((MI_IBC * MI_PORCENSALUD) / 100, 2);
    ELSE 
        MI_SALUD   :=0 ;
    END IF;

    IF MI_NOAPORTAPENSION IN (0) THEN 
        MI_PENSION := ROUND((MI_IBC * MI_PORCENPENSION) / 100, 2);

        IF MI_IBC >= MI_SALMINIMO * 4 THEN
            MI_FONDOSPENSIONAL := ROUND((MI_IBC * MI_VLRFONDO_PENSIONAL) / 100, 2);
        ELSE
            MI_FONDOSPENSIONAL := 0;
        END IF;
     ELSE 
        MI_PENSION:=0 ;
        MI_FONDOSPENSIONAL := 0;
    END IF;

    MI_VALORB := ROUND(MI_INGRESOBRUTO - (NVL(MI_SALUD,0)+NVL(MI_PENSION,0)+NVL(MI_FONDOSPENSIONAL,0)+NVL(MI_APORTESARL,0)),2);
    MI_VALORBG := MI_VALORB;
    IF MI_DEPENDIENTE <> 0 THEN
        IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'VALOR PORCENTAJE DEDUCIBLE POR DEPENDIENTES', UN_MODULO, SYSDATE) IS NULL   THEN
            PCK_SYSMAN_UTL.INSERTARINCONSISTENCIAPAR(UN_PAR =>'VALOR PORCENTAJE DEDUCIBLE POR DEPENDIENTES');        
        END IF;
        MI_DEPENDIENTES := ROUND((MI_INGRESOBRUTO * PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'VALOR PORCENTAJE DEDUCIBLE POR DEPENDIENTES', UN_MODULO, SYSDATE)) / 100, 2);

        IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'MAXIMO DEDUCIBLE POR DEPENDIENTES', UN_MODULO, SYSDATE) IS NULL   THEN
            PCK_SYSMAN_UTL.INSERTARINCONSISTENCIAPAR(UN_PAR =>'MAXIMO DEDUCIBLE POR DEPENDIENTES');        
        END IF;
        IF MI_DEPENDIENTES > (PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'MAXIMO DEDUCIBLE POR DEPENDIENTES', UN_MODULO, SYSDATE) * MI_UVT) THEN
            MI_DEPENDIENTES := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'MAXIMO DEDUCIBLE POR DEPENDIENTES', UN_MODULO, SYSDATE) * MI_UVT;
        END IF;
    ELSE
        MI_DEPENDIENTE := 0;
    END IF;
    IF MI_MEDICINAPREPAGADAYSEGUROS > PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'M�?XIMO DEDUCIBLE MEDICINA PEPAGADA Y SEGUROS', UN_MODULO, SYSDATE) * MI_UVT THEN
            MI_MEDICINAPREPAGADAYSEGUROS := ROUND(TRUNC(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'M�?XIMO DEDUCIBLE MEDICINA PEPAGADA Y SEGUROS', UN_MODULO, SYSDATE)  * MI_UVT / NVL(UN_FACTORREDONDEO, 0) + 0.5001) * NVL(UN_FACTORREDONDEO, 0), 2);
        END IF;

   IF MI_DEDUCIBLEVIVIENDA > PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'M�?XIMO DEDUCIBLE POR INTERESES DE VIVIENDA', UN_MODULO, SYSDATE) * MI_UVT THEN
            MI_DEDUCIBLEVIVIENDA := ROUND(TRUNC(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'M�?XIMO DEDUCIBLE POR INTERESES DE VIVIENDA', UN_MODULO, SYSDATE)  * MI_UVT / NVL(UN_FACTORREDONDEO, 0) + 0.5001) * NVL(UN_FACTORREDONDEO, 0), 2);
        END IF;   

    --RENTAS EXCENTAS
    MI_RENTASEXENTAS           := MI_APORTES + MI_AFC;
    MI_INGRESOBASEDERETENCION2 := MI_VALORB - MI_RENTASEXENTAS;
    --DEDUCCIONES
    MI_DEDUCCIONES := MI_DEDUCIBLEVIVIENDA + MI_MEDICINAPREPAGADAYSEGUROS + MI_DEPENDIENTES;
    MI_BASEREAL    := MI_VALORB - MI_RENTASEXENTAS - MI_DEDUCCIONES;

    MI_VALORRENTA        := ROUND((MI_BASEREAL * MI_PORC_RENTAS)/ 100,2);
    MI_VALORCUARENTABASE := ROUND((MI_VALORB * MI_LIMITE_RENTA_EXENTA)/ 100 ,2);

    MI_DEDUCCIONYRENTAS := MI_DEDUCCIONES + MI_VALORRENTA + MI_RENTASEXENTAS;
    
    -- (CFBARRERA_CC:1183_INI) Limita exento renta al tope de 790 UVT mensuales
    MI_LIMT_VALOR_UVT := ROUND((MI_LIM_25_RE / 12) * MI_UVT, 0);
    
    IF MI_VALORRENTA > MI_LIMT_VALOR_UVT THEN
        MI_VALORRENTA := MI_LIMT_VALOR_UVT;
    END IF;
    -- (CFBARRERA_CC:1183_FIN)

    IF MI_DEDUCCIONYRENTAS > MI_VALORCUARENTABASE THEN 
        MI_VALORES := UN_CONSECMENSAJES||' , '||
                      PCK_CONTABILIDAD2.FC_ORDENMENSAJES(UN_CONSECMENSAJES )
                                                         || ','' El valor de las deducciones más las rentas exentas excede el' 
                                                         || MI_LIMITE_RENTA_EXENTA 
                                                         || '% de la base' || CHR(10) || CHR(13) 
                                                         || 'Por lo tanto solo se aplicara el '
                                                         || MI_VALORCUARENTABASE || '''';
        MI_TABLA   := 'TEMP_CALCULO_RETENCIONES';
        MI_CAMPOS  := 'CODIGO,ORDEN,MENSAJE';
        BEGIN
            BEGIN
                MI_RTAMSG := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                               UN_ACCION  => 'I', 
                                               UN_CAMPOS  => MI_CAMPOS,
                                               UN_VALORES => MI_VALORES
                                               );                                            
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_MENSAJETEMP,
                                        UN_TABLAERROR => MI_TABLA);
        END;
        MI_BASEREAL := (MI_DEDUCCIONYRENTAS-MI_VALORCUARENTABASE) +(MI_VALORBG-MI_DEDUCCIONYRENTAS);
    ELSE
        MI_BASEREAL := MI_BASEREAL - MI_VALORRENTA;
    END IF;
    
    IF UN_FACTORREDONDEO =0    THEN 
        MI_FACTORREDONDEO := 1;
        ELSE 
          MI_FACTORREDONDEO:=UN_FACTORREDONDEO;
    END IF;
    
    MI_VALORRETENER    := ROUND(MI_BASEREAL ,0);
    MI_VALORRETEFUENTE := ROUND(TRUNC(PCK_CONTABILIDAD2.FC_RETEF2007(UN_COMPANIA => UN_COMPANIA,
                                                                     UN_ANIO     => UN_ANO,
                                                                     UN_BASE     => ROUND(MI_VALORRETENER / MI_UVT, 2), 
                                                                     UN_UVT      => MI_UVT) 
                                                                     / NVL(MI_FACTORREDONDEO, 0) + 0.5001) * NVL(MI_FACTORREDONDEO, 0), 2);
    MI_PORCENTAJEREAL  := ROUND(TRUNC(PCK_CONTABILIDAD2.FC_RETEF20071(UN_COMPANIA,UN_CONSECMENSAJES, UN_ANO, ROUND(MI_VALORRETENER / MI_UVT, 2))), 2);
    IF MI_VALORRETEFUENTE = 0 THEN
        GOTO SIGUE;
    END IF;
    MI_VALORRETENER := MI_VALORRETEFUENTE;
    IF NVL(UN_FACTORREDONDEO,0) <> 0 THEN 
        MI_VALORRETENER := ROUND(TRUNC(MI_VALORRETENER / NVL(UN_FACTORREDONDEO,0) + 0.5001)* NVL(UN_FACTORREDONDEO,0),2);
    END IF;
    IF NVL(UN_CUENTA_CREDITO1, ' ') <> ' ' THEN
        MI_CODIGO_CUENTA := UN_CUENTA_CREDITO1;
    ELSIF  NVL(UN_CUENTA_DEBITO1, ' ') <> ' ' THEN
        MI_CODIGO_CUENTA  := UN_CUENTA_DEBITO1;
    ELSE
        MI_VALORES := UN_CONSECMENSAJES||' , '||
                      PCK_CONTABILIDAD2.FC_ORDENMENSAJES(UN_CONSECMENSAJES )
                                                         || ',''El código de retención:' 
                                                         || NVL(UN_TIPO, '') || ' - ' || NVL(UN_NUMERO, '') 
                                                         || 'no tiene cuenta contable relacionada.''';
        MI_TABLA   := 'TEMP_CALCULO_RETENCIONES';
        MI_CAMPOS  := 'CODIGO,ORDEN,MENSAJE';
        BEGIN
            BEGIN
                MI_RTAMSG := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                               UN_ACCION  => 'I', 
                                               UN_CAMPOS  => MI_CAMPOS,
                                               UN_VALORES => MI_VALORES
                                               );                                            
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    =>  SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_MENSAJETEMP,
                                        UN_TABLAERROR => MI_TABLA);
        END;                           
        MI_CALCULORETENCIONESLEY18 := -1;
        RETURN MI_CALCULORETENCIONESLEY18;
    END IF;

    MI_STRNAT := SUBSTR('CDCCCDDDDC', TO_NUMBER(LPAD(MI_CODIGO_CUENTA,1))+1,1);
    IF MI_VALORRETENER <> 0 THEN 
        MI_CONSECUTIVO := MI_CONSECUTIVO + 1;
        -- AQUI SE AGREGA EL PARAMETRO PARA TRAER LA RETENCION CON EL CENTRO DE COSTO CODIFICADO EN LA CODIFICACION DE RETENCIONES           
        BEGIN
            SELECT CUENTA_PPTAL 
            INTO MI_CUENTAPPTAL
            FROM PLAN_CONTABLE 
            WHERE COMPANIA = UN_COMPANIA
              AND ANO      = UN_ANO 
              AND CODIGO   = MI_CODIGO_CUENTA;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CUENTAPPTAL := '';
        END; 
        BEGIN
            BEGIN

                IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 1 THEN 
                    MI_TABLA   := 'TEMP_DETALLE_COMPROBANTE_CNT';
                ELSE 
                    IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 0 OR PCK_CONTABILIDAD3.GL_VARCAUSACION = 2 THEN
                        MI_TABLA   := 'DETALLE_COMPROBANTE_CNT';
                    END IF;
                END IF;
                
             MI_CONSECUTIVO:= PCK_SYSMAN_UTL.FC_GENCONSECUTIVO( UN_TABLA    => MI_TABLA, 
                                                                UN_CRITERIO => 'COMPANIA    = '''||UN_COMPANIA||'''
                                                                            AND ANO         = '||UN_ANO||' 
                                                                            AND TIPO_CPTE   = '''||UN_TIPO||'''
                                                                            AND COMPROBANTE = '||UN_NUMERO||'', 
                                                                UN_CAMPO    => 'CONSECUTIVO');             

                MI_CAMPOS := 'COMPANIA,
                              ANO,
                              TIPO_CPTE,
                              COMPROBANTE,
                              CONSECUTIVO,
                              CUENTA,
                              FECHA,
                              NATURALEZA,
                              VALOR_DEBITO,
                              VALOR_CREDITO,
                              BASE_GRAVABLE,
                              CENTRO_COSTO,
                              TERCERO,
                              SUCURSAL,
                              AUXILIAR,
                              DESCRIPCION,
                              CUENTAPPTAL,
                              EJECUCION_DEBITO,
                              EJECUCION_CREDITO,
                              BASE_IVA,
                              NRO_DOCUMENTO,
                              CREATED_BY,
                              DATE_CREATED,
                              REFERENCIA,
                              CONCEPTO,
                              PROVEEDOR';	  
                MI_VALORES :='''' || UN_COMPANIA|| ''',
                                ' || UN_ANO|| ',
                              ''' || UN_TIPO|| ''',
                                ' || UN_NUMERO|| ',
                                ' || MI_CONSECUTIVO|| ',
                              ''' || MI_CODIGO_CUENTA|| ''',
                                TO_DATE(''' || UN_FECHA || ''',''DD/MM/YYYY''),
                              ''' || MI_STRNAT|| ''',
                                ' || (CASE WHEN NVL(UN_CUENTA_CREDITO1, ' ') <> ' ' THEN 0 ELSE MI_VALORRETENER END) || ', 
                                ' || (CASE WHEN NVL(UN_CUENTA_DEBITO1, ' ') <> ' ' THEN 0 ELSE MI_VALORRETENER END) || ',
                                ' || MI_INGRESOBRUTO|| ',
                              ''' || UN_STRCENTRO_COSTO|| ''',
                              ''' || NVL(UN_TERCERO, ''''||PCK_DATOS.FC_CONS_TERCERO()||'''') || ''', 
                              ''' || NVL(UN_SUCURSAL, ''''||PCK_DATOS.FC_CONS_SUCURSAL()||'''') || ''',
                              ''' || PCK_DATOS.FC_CONS_AUXILIAR()||''',
                              ''' || UN_DESCRIPCION || ''',
                              ''' || NVL(MI_CUENTAPPTAL, '') || ''',
                                ' || (CASE WHEN NVL(UN_CUENTA_CREDITO1, ' ') <> ' ' THEN 0 ELSE MI_VALORRETENER END) || ', 
                                ' || (CASE WHEN NVL(UN_CUENTA_DEBITO1, ' ') <> ' ' THEN 0 ELSE MI_VALORRETENER END) || ', 
                                ' || NVL(UN_VALORBASEIVA, 0) || ', 
                              ''' || UN_NRO_DOCUMENTO|| ''',
                              ''' || UN_USUARIO || ''',
                                     SYSDATE,
                              ''' || UN_REFERENCIA|| ''',
                              ''' || NVL(UN_CONCEPTO,'0') || ''',
                              ''' || NVL(UN_PROVEEDOR,'0') || '''';									 
                MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                               UN_ACCION  => 'I', 
                                               UN_CAMPOS  => MI_CAMPOS,
                                               UN_VALORES => MI_VALORES
                                               );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
				MI_CONSECUTIVO := CASE WHEN MI_CONSECUTIVO <> 0 THEN (MI_CONSECUTIVO - 1) ELSE 0 END;																					 
            END; 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            MI_MSGERROR(1).CLAVE := 'TIPO';
            MI_MSGERROR(1).VALOR := UN_TIPO;
            MI_MSGERROR(2).CLAVE := 'NUMERO';
            MI_MSGERROR(2).VALOR := UN_NUMERO;
            MI_MSGERROR(3).CLAVE := 'ANIO';
            MI_MSGERROR(3).VALOR := UN_ANO;
            MI_MSGERROR(4).CLAVE := 'CUENTA';
            MI_MSGERROR(4).VALOR := MI_CODIGO_CUENTA;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_LEY1819INSERTDET,
              UN_REEMPLAZOS => MI_MSGERROR,
              UN_TABLAERROR => MI_TABLA
            ); 
        END;
    END IF;

    <<SIGUE>>
    		    IF PCK_CONTABILIDAD3.GL_VARCAUSACION = 1 THEN        
            		GOTO TERMINE;
       			END IF;
    
    MI_CAMPOS := 'VALOR            = ' || (CASE WHEN MI_VALORRETEFUENTE <> 0 THEN MI_VALORRETENER   ELSE 0 END) ||',
                  VALORBASELEY1607 = ' || (CASE WHEN MI_VALORRETEFUENTE <> 0 THEN MI_BASEREAL       ELSE 0 END) ||',
                  PCT_APLICA1607   = ' || (CASE WHEN MI_PORCENTAJEREAL  <> 0 THEN MI_PORCENTAJEREAL ELSE 0 END) ||',
                  MODIFIED_BY      ='''|| UN_USUARIO || ''',
                  DATE_MODIFIED    = SYSDATE';
    MI_CONDICION := 'COMPANIA        =''' || UN_COMPANIA || '''
                 AND ANO             ='   || UN_ANO || '
                 AND TIPO            =''' || UN_TIPO || '''
                 AND NUMERO          ='   || UN_NUMERO || '
                 AND TIPORETENCION   =''' || UN_TIPORETENCION || '''
                 AND CODIGORETENCION =''' || UN_CODIGORETENCION || '''';    
    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_CNTRETENCION',
                                UN_ACCION    => 'M', 
                                UN_CAMPOS    => MI_CAMPOS,
                                UN_CONDICION => MI_CONDICION);
    MI_CAMPOS    := 'VDEPENDIENTE  = '   || NVL (MI_DEPENDIENTES, 0)||',
                     MODIFIED_BY   ='''|| UN_USUARIO || ''',
                     DATE_MODIFIED = SYSDATE';
    MI_CONDICION := 'COMPANIA     = ''' || UN_COMPANIA || '''
                 AND NIT          = ''' || NVL (UN_TERCERO, '' )|| '''
                 AND SUCURSAL     = ''' || NVL (UN_SUCURSAL, '' )|| '''';
    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'TERCERO',
                                UN_ACCION    => 'M', 
                                UN_CAMPOS    => MI_CAMPOS,
                                UN_CONDICION => MI_CONDICION);
    SELECT COUNT ('X') CANTIDAD
      INTO MI_CANTIDAD
    FROM RESUMEN_RENTAS
    WHERE COMPANIA = UN_COMPANIA
      AND ANO      = UN_ANO
      AND TIPO     = UN_TIPO
      AND NUMERO   = UN_NUMERO;

    IF MI_CANTIDAD = 0 THEN   
        BEGIN
            BEGIN
                MI_TABLA := 'RESUMEN_RENTAS';
                MI_CAMPOS := 'COMPANIA, 
                            ANO, 
                            TIPO, 
                            NUMERO, 
                            TERCERO, 
                            SUCURSAL, 
                            FECHA, 
                            TOTALINGRESO, 
                            DEDUCCION, 
                            PAGOS_SALUD, 
                            APORTES_PENSION, 
                            DEDUCCION_DEPENDIENTE, 
                            ARL, 
                            APORTE_VOLUNTARIO, 
                            DEDUCCION_PREPAGADA, 
                            EXENCION,
                            VALORUVT,
                            VALORRETENIDO,
                            APORTE_AFC,
                            FSP,
                            CREATED_BY, 
                            DATE_CREATED';

                  MI_VALORES := ''''||UN_COMPANIA||''',
                                    '||UN_ANO||',
                                    '''||UN_TIPO||''',
                                    '||UN_NUMERO||',
                                    '''||UN_TERCERO||''',
                                    '''||UN_SUCURSAL||''',
                                    TO_DATE(''' || TO_CHAR(UN_FECHA,'DD/MM/YYYY')|| ''',''DD/MM/YYYY''),
                                    NVL('||MI_VALORB||',0),
                                    NVL('||MI_DEDUCIBLEVIVIENDA||',0),
                                    NVL('||MI_SALUD||',0),
                                    NVL('||MI_PENSION||',0),
                                    NVL('||MI_DEPENDIENTES||',0),
                                    NVL('||MI_APORTESARL||',0),
                                    NVL('||MI_APORTES||',0),
                                    NVL('||MI_MEDICINAPREPAGADAYSEGUROS||',0),
                                    NVL('||MI_VALORRENTA||',0),
                                    NVL('||MI_UVT||',0),
                                (CASE 
                                    WHEN '||MI_VALORRETEFUENTE||'<> 0
                                    THEN '||MI_VALORRETENER||'
                                    ELSE 0
                                END),
                                '||MI_AFC||',
                                 NVL('||MI_FONDOSPENSIONAL||',0),
                                 '''||UN_USUARIO||''',
                                 SYSDATE';

                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    =>  MI_TABLA,
                                                            UN_ACCION   =>  'I', 
                                                            UN_CAMPOS   =>  MI_CAMPOS, 
                                                            UN_VALORES  =>  MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END; 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            MI_MSGERROR(1).CLAVE := 'TIPO';
            MI_MSGERROR(1).VALOR := UN_TIPO;
            MI_MSGERROR(2).CLAVE := 'NUMERO';
            MI_MSGERROR(2).VALOR := UN_NUMERO;
            MI_MSGERROR(3).CLAVE := 'ANIO';
            MI_MSGERROR(3).VALOR := UN_ANO;
            MI_MSGERROR(4).CLAVE := 'TERCERO';
            MI_MSGERROR(4).VALOR := UN_TERCERO;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_LEY1819RENTASINS,
              UN_REEMPLAZOS => MI_MSGERROR,
              UN_TABLAERROR => MI_TABLA
            ); 
        END;
    ELSE 
        BEGIN
            BEGIN    
                MI_TABLA := 'RESUMEN_RENTAS';
                MI_CAMPOS := 'TOTALINGRESO          = NVL('||MI_VALORB||',0), 
                              APORTE_AFC            = NVL('||MI_AFC||', 0) , 
                              DEDUCCION             = NVL('||MI_DEDUCIBLEVIVIENDA||', 0),
                              PAGOS_SALUD           = NVL('||MI_SALUD||', 0), 
                              APORTES_PENSION       = NVL('||MI_PENSION||', 0), 
                              DEDUCCION_DEPENDIENTE = NVL('||MI_DEPENDIENTES||', 0), 
                              ARL                   = NVL('||MI_APORTESARL||', 0),
                              FSP                   = NVL('||MI_FONDOSPENSIONAL||', 0),
                              APORTE_VOLUNTARIO     = NVL('||MI_APORTES||', 0), 
                              DEDUCCION_PREPAGADA   = NVL('||MI_MEDICINAPREPAGADAYSEGUROS||', 0), 
                              EXENCION              = NVL('||MI_VALORRENTA||', 0), 
                              VALORUVT              = NVL('||MI_UVT||', 0), 
                              VALORRETENIDO         = ' || CASE WHEN MI_VALORRETEFUENTE <> 0
                                                                THEN MI_VALORRETENER
                                                                ELSE NVL(MI_FONDOSPENSIONAL,0)
                                                            END||',
                              MODIFIED_BY           ='''|| UN_USUARIO || ''',
                              TERCERO               ='''|| UN_TERCERO || ''',
                              SUCURSAL              ='''|| UN_SUCURSAL || ''',
                              DATE_MODIFIED         = SYSDATE';
                MI_CONDICION :='COMPANIA = '''|| UN_COMPANIA || '''
                            AND ANO      ='   || UN_ANO      || '
                            AND TIPO     =''' || UN_TIPO     ||'''
                            AND NUMERO   ='   || UN_NUMERO;
                MI_RTA 		:= PCK_DATOS.FC_ACME ( UN_TABLA        => MI_TABLA,
                                                   UN_ACCION       => 'M',
                                                   UN_CAMPOS       => MI_CAMPOS,
                                                   UN_CONDICION    => MI_CONDICION
                                                );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            MI_MSGERROR(1).CLAVE := 'TIPO';
            MI_MSGERROR(1).VALOR := UN_TIPO;
            MI_MSGERROR(2).CLAVE := 'NUMERO';
            MI_MSGERROR(2).VALOR := UN_NUMERO;
            MI_MSGERROR(3).CLAVE := 'ANIO';
            MI_MSGERROR(3).VALOR := UN_ANO;
            MI_MSGERROR(4).CLAVE := 'TERCERO';
            MI_MSGERROR(4).VALOR := UN_TERCERO;
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_LEY1819RENTASUPD,
              UN_REEMPLAZOS => MI_MSGERROR,
              UN_TABLAERROR => MI_TABLA
            ); 
        END;                          

    END IF;
    <<TERMINE>> 
    RETURN -1;      
END FC_CALCULORETENCIONLEY1819;


FUNCTION FC_CALCULORETENCIONESLEY1819(

          /*
        NAME                    :FC_CALCULORETENCIONESLEY1819 (En access CalculoRetencionley1819)
        AUTHORS                 :STEFANINI SYSMAN SAS
        AUTHORS MIGRACION       :LAURA MELIZA BOTIA PEREZ
        DATE MIGRADOR           :22/08/2018
        TIME                    :08:00 AM
        SOURCE MODULE           : CONTABILIDAD (1) - SysmanCT2018.06.07
        MODIFIER                :
        DATA MODIFIED           :
        TIME                    :
        DESCRIPCION             :
        MODIFICATIONS           :
        MODIFIER                :
        TIME                    :
        MODIFICATIONS           :

        */
        UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
        UN_MODULO           IN PCK_SUBTIPOS.TI_MODULO,
        UN_CONSECMENSAJES   IN NUMBER,
        UN_ANO              IN PCK_SUBTIPOS.TI_ANIO,
        UN_FECHA            IN DATE,
        UN_TIPO             IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE,
        UN_NUMERO           IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
        UN_TERCERO          IN PCK_SUBTIPOS.TI_TERCERO,
        UN_SUCURSAL         IN PCK_SUBTIPOS.TI_SUCURSAL,
        UN_NOMBRETERCERO    IN PLAN_CONTABLE.NOMBRE%TYPE,
        UN_VALORBASE        IN NUMBER,
        UN_VALORBASEIVA     IN PCK_SUBTIPOS.TI_DOBLE,
        UN_DESCRIPCION      IN VARCHAR2 DEFAULT ' ',
        UN_STRCENTRO_COSTO  IN PCK_SUBTIPOS.TI_CENTRO_COSTO DEFAULT PCK_DATOS.FC_CONS_CENTRO(),
        UN_NRO_DOCUMENTO    IN DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE DEFAULT NULL,
        UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO

        )
    RETURN NUMBER AS

        MI_ERROR_FUN                  PCK_SUBTIPOS.TI_ERROR_FUN := PCK_DATOS.GL_ERROR_NUM + 10;
        MI_CALCULORETENCIONESLEY1819  NUMBER := 0;
        MI_RS                         SYS_REFCURSOR;
        MI_STRSQL                     PCK_SUBTIPOS.TI_STRSQL;
        MI_RTA                        VARCHAR2(4000 CHAR);
        MI_CONSECUTIVO                NUMBER;
        MI_STRCENTRO_COSTO            VARCHAR2(30 CHAR);
        MI_TIENECONTENIDO             NUMBER;
        MI_INDILEY                    NUMBER;
        MI_DESCRIPCION                VARCHAR2(4000 CHAR);
        MI_CONTEO                     NUMBER;
        MI_CONTADOR                   NUMBER;
        MI_RTAMSG                     VARCHAR2(200 CHAR);
        MI_MSGERROR                   PCK_SUBTIPOS.TI_CLAVEVALOR;
        MI_MANEJA_REF_FUEN            VARCHAR2(200 CHAR);
        MI_REFERENCIA                 PCK_SUBTIPOS.TI_REFERENCIA;
        MI_FUENTE_RECURSO             PCK_SUBTIPOS.TI_FUENTE_RECURSOS;
    BEGIN

        MI_DESCRIPCION := UN_DESCRIPCION;
                
         MI_MANEJA_REF_FUEN := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'MANEJA REFERENCIA Y FUENTE EN RETENCIONES',
                                              UN_MODULO    => PCK_DATOS.FC_MODULOCONTABILIDAD,
                                              UN_FECHA_PAR => SYSDATE);

        MI_CONSECUTIVO   := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO( UN_TABLA    => 'DETALLE_COMPROBANTE_CNT', 
                                                                UN_CRITERIO => 'COMPANIA = '''||UN_COMPANIA||'''
                                                                            AND ANO = '||UN_ANO||' 
                                                                            AND TIPO_CPTE = '''||UN_TIPO||'''
                                                                            AND COMPROBANTE = '||UN_NUMERO||'', 
                                                                UN_CAMPO    => 'CONSECUTIVO');

          SELECT COUNT(*)
          INTO MI_CONTADOR
          FROM DETALLE_COMPROBANTE_CNT
          WHERE COMPANIA  = UN_COMPANIA
          AND ANO         = UN_ANO
          AND TIPO_CPTE   = UN_TIPO
          AND COMPROBANTE = UN_NUMERO; 

        IF MI_CONTADOR <> 0 THEN
            MI_TIENECONTENIDO  := -1;
        ELSE
            MI_TIENECONTENIDO := 0;
        END IF;

        BEGIN
            BEGIN
                SELECT COUNT('X')
                INTO MI_CONTEO
                FROM COMPROBANTE_CNTRETENCION LEFT JOIN RETENCIONES
                  ON COMPROBANTE_CNTRETENCION.COMPANIA        = RETENCIONES.COMPANIA
                 AND COMPROBANTE_CNTRETENCION.ANO             = RETENCIONES.ANO
                 AND COMPROBANTE_CNTRETENCION.TIPORETENCION   = RETENCIONES.TIPO
                 AND COMPROBANTE_CNTRETENCION.CODIGORETENCION = RETENCIONES.CODIGO
                WHERE COMPROBANTE_CNTRETENCION.COMPANIA  = UN_COMPANIA
                    AND COMPROBANTE_CNTRETENCION.ANO      = UN_ANO
                    AND COMPROBANTE_CNTRETENCION.TIPO     = UN_TIPO
                    AND COMPROBANTE_CNTRETENCION.NUMERO   = UN_NUMERO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            MI_MSGERROR(1).CLAVE := 'TIPO';
            MI_MSGERROR(1).VALOR := UN_TIPO;
            MI_MSGERROR(2).CLAVE := 'NROCOMPROBANTE';
            MI_MSGERROR(2).VALOR := UN_NUMERO;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD    => SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_LEY1819,
                                        UN_TABLAERROR => 'COMPROBANTE_CNTRETENCION',
                                        UN_REEMPLAZOS => MI_MSGERROR
                                        );
        END;

        <<VERIFICARETENCION>>
        FOR MI_RS IN (SELECT COMPROBANTE_CNTRETENCION.TIPORETENCION,
                          COMPROBANTE_CNTRETENCION.CODIGORETENCION,
                          COMPROBANTE_CNTRETENCION.VALOR,
                          COMPROBANTE_CNTRETENCION.VALORBASE,
                          COMPROBANTE_CNTRETENCION.PORCIVA,
                          RETENCIONES.CUENTA_DEBITO,
                          RETENCIONES.CUENTA_CREDITO,
                          RETENCIONES.PCT_BASE,
                          RETENCIONES.LIMITE_INF,
                          RETENCIONES.PCT_APLICAR,
                          RETENCIONES.VALOR_APLICAR,
                          RETENCIONES.FACTORREDONDEO,
                          RETENCIONES.PERMITEMODIFICAR,
                          RETENCIONES.CENTRO_COSTO ,
                          RETENCIONES.ALEY1450,
                          RETENCIONES.ALEY1819,
                          RETENCIONES.CUENTA_DEBITO1,
                          RETENCIONES.CUENTA_CREDITO1,
                          RETENCIONES.COD_AUXILIAR,
			              RETENCIONES.COD_REFERENCIA, 
			              RETENCIONES.COD_FUENTER
                      FROM COMPROBANTE_CNTRETENCION LEFT JOIN RETENCIONES
                        ON COMPROBANTE_CNTRETENCION.COMPANIA        = RETENCIONES.COMPANIA
                       AND COMPROBANTE_CNTRETENCION.ANO             = RETENCIONES.ANO
                       AND COMPROBANTE_CNTRETENCION.TIPORETENCION   = RETENCIONES.TIPO
                       AND COMPROBANTE_CNTRETENCION.CODIGORETENCION = RETENCIONES.CODIGO
                      WHERE COMPROBANTE_CNTRETENCION.COMPANIA = UN_COMPANIA
                        AND COMPROBANTE_CNTRETENCION.ANO      = UN_ANO
                        AND COMPROBANTE_CNTRETENCION.TIPO     = UN_TIPO
                        AND COMPROBANTE_CNTRETENCION.NUMERO   = UN_NUMERO
                      ORDER  BY COMPROBANTE_CNTRETENCION.COMPANIA,
                          COMPROBANTE_CNTRETENCION.ANO,
                          COMPROBANTE_CNTRETENCION.TIPO,
                          COMPROBANTE_CNTRETENCION.NUMERO,
                          COMPROBANTE_CNTRETENCION.TIPORETENCION,
                          COMPROBANTE_CNTRETENCION.CODIGORETENCION )
        LOOP
        
        IF MI_RS.CENTRO_COSTO IS NULL OR MI_RS.CENTRO_COSTO = '' OR PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA, 'MANEJA RETENCIONES POR CENTRO DE COSTO', UN_MODULO, SYSDATE) = 'NO' THEN -- MOD JM CC 3368
        MI_STRCENTRO_COSTO := UN_STRCENTRO_COSTO;
        ELSE 
        MI_STRCENTRO_COSTO := MI_RS.CENTRO_COSTO;
        END IF;
	  
	  IF MI_MANEJA_REF_FUEN = 'SI' THEN
		MI_FUENTE_RECURSO := MI_RS.COD_FUENTER;
        MI_REFERENCIA     := MI_RS.COD_REFERENCIA;
	  END IF;
      
            IF MI_RS.ALEY1819 <> 0 AND MI_RS.TIPORETENCION = 'FUE' THEN  --(MZANGUNA:21/12/2018)-Calcule solo loe que sean retencion en la fuente.
                MI_INDILEY := -1;
                MI_RTA     := PCK_CONTABILIDAD7.FC_CALCULORETENCIONLEY1819(UN_COMPANIA        => UN_COMPANIA,
                                                                           UN_MODULO          => UN_MODULO,
                                                                           UN_CONSECMENSAJES  => UN_CONSECMENSAJES,
                                                                           UN_ANO             => UN_ANO,
                                                                           UN_FECHA           => UN_FECHA,
                                                                           UN_TIPO            => UN_TIPO,
                                                                           UN_NUMERO          => UN_NUMERO,
                                                                           UN_TERCERO         => UN_TERCERO,
                                                                           UN_SUCURSAL        => UN_SUCURSAL,
                                                                           UN_NOMBRETERCERO   => UN_NOMBRETERCERO,
                                                                           UN_VALORBASE       => NVL(MI_RS.VALORBASE, 0),
                                                                           UN_VALORBASEIVA    => NVL(UN_VALORBASEIVA, 0),
                                                                           UN_CONSECUTIVO     => MI_CONSECUTIVO,
                                                                           UN_TIENECONTENIDO  => MI_TIENECONTENIDO,
                                                                           UN_CUENTA_DEBITO1  => NVL(MI_RS.CUENTA_DEBITO1, ''),
                                                                           UN_CUENTA_CREDITO1 => NVL(MI_RS.CUENTA_CREDITO1, ''),
                                                                           UN_TIPORETENCION   => NVL(MI_RS.TIPORETENCION, ''),
                                                                           UN_CUENTA_DEBITO   => NVL(MI_RS.CUENTA_DEBITO, ''),
                                                                           UN_CUENTA_CREDITO  => NVL(MI_RS.CUENTA_CREDITO, ''),
                                                                           UN_FACTORREDONDEO  => NVL(MI_RS.FACTORREDONDEO, 0),
                                                                           UN_CODIGORETENCION => NVL(MI_RS.CODIGORETENCION, ''),
                                                                           UN_DESCRIPCION     => MI_DESCRIPCION,
                                                                           UN_STRCENTRO_COSTO => MI_STRCENTRO_COSTO,
                                                                           UN_NRO_DOCUMENTO   => NVL(UN_NRO_DOCUMENTO, ''),
                                                                           UN_USUARIO         => UN_USUARIO
                                                                        );
            ELSE
                MI_CONSECUTIVO := MI_CONSECUTIVO + 1; 
                MI_INDILEY := 0;
                MI_RTA     := PCK_CONTABILIDAD2.FC_SINRETENCIONESLEY1450( UN_COMPANIA,  UN_CONSECMENSAJES,
                                                                          UN_ANO, UN_FECHA, UN_TIPO,
                                                                          UN_NUMERO, UN_TERCERO,
                                                                          UN_SUCURSAL,
                                                                          NVL(MI_RS.VALORBASE, 0),
                                                                          NVL(UN_VALORBASEIVA, 0),
                                                                          MI_CONSECUTIVO,
                                                                          MI_TIENECONTENIDO,
                                                                          NVL(MI_RS.CUENTA_DEBITO, ''),
                                                                          NVL(MI_RS.CUENTA_CREDITO, ''),
                                                                          NVL(MI_RS.TIPORETENCION, ''),
                                                                          NVL(MI_RS.LIMITE_INF, 0),
                                                                          NVL(MI_RS.PCT_BASE, 0),
                                                                          NVL(MI_RS.PCT_APLICAR, 0),
                                                                          NVL(MI_RS.VALOR_APLICAR, 0),
                                                                          NVL(MI_RS.FACTORREDONDEO, 0),
                                                                          NVL(MI_RS.CODIGORETENCION, ''),
                                                                          MI_INDILEY, MI_DESCRIPCION,
                                                                          MI_STRCENTRO_COSTO,
                                                                          NVL(UN_NRO_DOCUMENTO, ''),
                                                                          NVL(MI_RS.COD_AUXILIAR,''),
                                                                          NVL(MI_REFERENCIA,''),
                                                                          NVL(MI_FUENTE_RECURSO,''));
            END IF;
        END LOOP VERIFICARETENCION;
        MI_CALCULORETENCIONESLEY1819 := -1;
        RETURN MI_CALCULORETENCIONESLEY1819;
        --EXCEPTION WHEN OTHERS THEN
          --  PCK_DATOS.GL_ERROR_MSG := 'Interrupción durante el cálculo de retenciones con ley 1607.';
           -- PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'CONTABILIDAD2','',SQLERRM );
            --RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );


END FC_CALCULORETENCIONESLEY1819;


FUNCTION FC_CALCULARLEY1450 (

        UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA, 
        UN_MODULO           IN PCK_SUBTIPOS.TI_MODULO,
        UN_CONSECMENSAJES   IN NUMBER,
        UN_ANO              IN PCK_SUBTIPOS.TI_ANIO, 
        UN_FECHA            IN DATE,
        UN_TIPO             IN COMPROBANTE_CNT.TIPO%TYPE, 
        UN_NUMERO           IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT, 
        UN_TERCERO          IN PCK_SUBTIPOS.TI_TERCERO, 
        UN_SUCURSAL         IN PCK_SUBTIPOS.TI_SUCURSAL, 
      	UN_NOMBRETERCERO    IN PLAN_CONTABLE.NOMBRE%TYPE, 
        UN_VALORBASE        IN NUMBER, 
        UN_VALORBASEIVA     IN PCK_SUBTIPOS.TI_DOBLE, 
        UN_DESCRIPCION      IN VARCHAR2 DEFAULT NULL, 
        UN_STRCENTRO_COSTO  IN PCK_SUBTIPOS.TI_CENTRO_COSTO DEFAULT NULL, 
        UN_NRO_DOCUMENTO    IN DETALLE_COMPROBANTE_CNT.NRO_DOCUMENTO%TYPE DEFAULT NULL,
        UN_USUARIO            IN PCK_SUBTIPOS.TI_USUARIO

)RETURN NUMBER AS 

      MI_ERROR_FUN          PCK_SUBTIPOS.TI_ERROR_FUN := PCK_DATOS.GL_ERROR_NUM +11;
      MI_CALCULARLEY1450    NUMBER := 0;
      MI_RS                 SYS_REFCURSOR;
      MI_RTA                NUMBER;
      MI_LEY1450            TERCERO.LEY1450%TYPE;
      MI_LEY1819            TERCERO.LEY1819%TYPE;
      MI_RTAMSG             VARCHAR2(200 CHAR);
      MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
      MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;

   BEGIN
      BEGIN

                SELECT LEY1450, LEY1819
                INTO MI_LEY1450, MI_LEY1819
                FROM TERCERO
                WHERE COMPANIA = UN_COMPANIA
                AND NIT = UN_TERCERO
                AND SUCURSAL= UN_SUCURSAL
                AND NVL(LEY1450,0)=-1
                OR COMPANIA = UN_COMPANIA
                AND NIT = UN_TERCERO
                AND SUCURSAL = UN_SUCURSAL
                AND NVL(LEY1819,0)=-1;


                EXCEPTION WHEN NO_DATA_FOUND THEN 
                      MI_LEY1450 := NULL;
                      MI_LEY1819 := NULL;

     END;

     IF MI_LEY1450 <> 0 THEN 

            MI_TABLA   := 'TEMP_CALCULO_RETENCIONES';
            MI_CAMPOS  := 'CODIGO,ORDEN,MENSAJE';
            MI_VALORES := UN_CONSECMENSAJES||' , '||PCK_CONTABILIDAD2.FC_ORDENMENSAJES(UN_CONSECMENSAJES)||', ''Al tercero seleccionado se le aplicara retención según ley 1607.' || CHR(13) || CHR(13) || 'la retención de este pago será calculada basada en esta norma ''';

            MI_RTAMSG := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                           UN_ACCION  => 'I', 
                                           UN_CAMPOS  => MI_CAMPOS,
                                           UN_VALORES => MI_VALORES
                                           ) ;  




        MI_TABLA        := 'COMPROBANTE_CNT';     
        MI_CAMPOS       := 'LEY1450 = -1';
        MI_CONDICION := ' COMPANIA   =''' || UN_COMPANIA|| '''
                              AND ANO    =  ' || UN_ANO|| ' 
                              AND TIPO   =''' || UN_TIPO|| '''
                              AND NUMERO =  ' || UN_NUMERO;
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                    UN_ACCION    => 'M', 
                                    UN_CAMPOS    => MI_CAMPOS,
                                    UN_CONDICION => MI_CONDICION);

        MI_RTA := PCK_CONTABILIDAD2.FC_CALCULORETENCIONESLEY1607( UN_COMPANIA, 
                                                                  UN_MODULO, 
                                                                  UN_CONSECMENSAJES, 
                                                                  UN_ANO, 
                                                                  UN_FECHA, 
                                                                  UN_TIPO, 
                                                                  UN_NUMERO, 
                                                                  UN_TERCERO, 
                                                                  UN_SUCURSAL, 
                                                                  UN_NOMBRETERCERO, 
                                                                  NVL(UN_VALORBASE, 0), 
                                                                  NVL(UN_VALORBASEIVA, 0), 
                                                                  UN_DESCRIPCION, 
                                                                  NVL(UN_STRCENTRO_COSTO, 
                                                                  PCK_DATOS.FC_CONS_CENTRO()), 
                                                                  NVL(UN_NRO_DOCUMENTO, '') );    

        IF MI_RTA = 0 THEN
            MI_CALCULARLEY1450 := 0;
        ELSE
            MI_CALCULARLEY1450 := -1;
        END IF; 


    ELSE

       IF MI_LEY1819 <> 0 THEN 

        MI_TABLA   := 'TEMP_CALCULO_RETENCIONES';
            MI_CAMPOS  := 'CODIGO,ORDEN,MENSAJE';
            MI_VALORES := UN_CONSECMENSAJES||' , '||PCK_CONTABILIDAD2.FC_ORDENMENSAJES(UN_CONSECMENSAJES)||', ''Al tercero seleccionado se le aplicara retención según ley 1819.' || CHR(13) || CHR(13) || 'la retención de este pago será calculada basada en esta norma ''';

            MI_RTAMSG := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                           UN_ACCION  => 'I', 
                                           UN_CAMPOS  => MI_CAMPOS,
                                           UN_VALORES => MI_VALORES
                                           ) ; 


        MI_RTA := PCK_CONTABILIDAD7.FC_CALCULORETENCIONESLEY1819 (UN_COMPANIA, 
                                                                  UN_MODULO, 
                                                                  UN_CONSECMENSAJES, 
                                                                  UN_ANO, 
                                                                  UN_FECHA, 
                                                                  UN_TIPO, 
                                                                  UN_NUMERO, 
                                                                  UN_TERCERO, 
                                                                  UN_SUCURSAL, 
                                                                  UN_NOMBRETERCERO, 
                                                                  NVL(UN_VALORBASE, 0), 
                                                                  NVL(UN_VALORBASEIVA, 0), 
                                                                  UN_DESCRIPCION, 
                                                                  NVL(UN_STRCENTRO_COSTO, 
                                                                  PCK_DATOS.FC_CONS_CENTRO()), 
                                                                  NVL(UN_NRO_DOCUMENTO, ''),
                                                                  UN_USUARIO
                                                                );    

      IF MI_RTA = 0 THEN 
        MI_CALCULARLEY1450 := 0;
      ELSE
        MI_CALCULARLEY1450 := -1;
      END IF;  
    END IF;
    END IF;
    RETURN MI_CALCULARLEY1450; 
        EXCEPTION WHEN OTHERS THEN
            PCK_DATOS.GL_ERROR_MSG := 'Interrupción durante el cálculo de ley 1450.';
            PCK_DATOS.GL_ERROR_MSG := PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, 
                                                                      PCK_DATOS.GL_ERROR_MSG,  
                                                                      'CONTABILIDAD2',
                                                                      '',
                                                                      SQLERRM );
            --RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );

END FC_CALCULARLEY1450;

FUNCTION FC_GENERARARCPLANOS_BANCOS
(
  /*
  NAME                    : FC_GENERARARCPLANOS_BANCOS (En access GenerarArchivosPlanosOccidente)
  AUTHORS                 : STEFANINI SYSMAN SAS
  AUTHORS MIGRACION       : MIGUEL ANGEL VENEGAS RODRIGUEZ
  DATE MIGRADOR           : 18/10/2018
  TIME                    : 08:00 AM
  SOURCE MODULE           : CONTABILIDAD - SysmanCT2018.10.01
  MODIFIER                : CRISTIAN FERNEY SUESCUN BARRERA
  DATA MODIFIED           : 20/05/2021
  TIME                    : 2:50 PM
  DESCRIPCION             : Ticket 7704307. Se agrega filtro por CUENTA al actualizar en la tabla COMPROBANTE_CNT
  MODIFICATIONS           : Se modifican los SELECT de los recordsets para no permitir datos nulos. adicional se ajusto el valor de donde 
  MODIFIER                : se estaba consultando la informacion para la construccion del archivo plano.
  TIME                    :
  MODIFICATIONS           :
  MODIFIER                : MARIA ALEJANDRA PEREZ SALAZAR
  DATA MODIFIED           : 12/11/2025
  TIME                    : 2:50 PM
  DESCRIPCION             : CC1796. Se agrega parametro para validar si se toma fecha de pago del formulario o la fecha de pago del comprobante.
  --NAME  : generarBancosPlanos
  --METHOD  : GET
  */
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO             IN ANO.NUMERO%TYPE,
    UN_EGRESOINICIAL   IN COMPROBANTE_CNT.NUMERO%TYPE,
    UN_EGRESOFINAL     IN COMPROBANTE_CNT.NUMERO%TYPE,
    UN_FECHAPAGO       IN DATE,
    UN_CUENTACLIENTE   IN NUMBER,
    UN_CUENTAPRINCIPAL IN NUMBER,
    UN_NUMEROBANCO     IN BANCO.BANCO%TYPE,
    UN_TIPOEGRESO      IN VARCHAR2,
    UN_CUENTAINICIAL   IN PLAN_CONTABLE.CODIGO%TYPE,
    UN_CUENTAFINAL     IN PLAN_CONTABLE.CODIGO%TYPE,
    UN_IDENTIFICADOR   IN NUMBER,
    UN_DIGITOVERIFICACION IN NUMBER,
    UN_TIPO_CNTA_CLIENTE   IN VARCHAR2,
    UN_CLASE_TRANSACCION   IN VARCHAR2
)
    RETURN CLOB
IS
    -- Declaración de variables locales para asignar valores predeterminados a los parámetros opcionales,
    -- especialmente cuando el parámetro "MANEJA PLANO BANCO OCCIDENTE" está en "SI". Si algún parámetro no es enviado, se asigna un valor por defecto para evitar errores en el procesamiento.--(CFBARRERA_CC:1441)
    V_CUENTAPRINCIPAL NUMBER := UN_CUENTAPRINCIPAL;
    V_IDENTIFICADOR NUMBER := UN_IDENTIFICADOR;
    V_DIGITOVERIFICACION NUMBER := UN_DIGITOVERIFICACION;
    V_TIPO_CNTA_CLIENTE VARCHAR2(50) := UN_TIPO_CNTA_CLIENTE;
    V_CLASE_TRANSACCION VARCHAR2(50) := UN_CLASE_TRANSACCION;

    MI_NUMEROREGISTROS  NUMBER;
    MI_VALORTOTAL       NUMBER;
    MI_VALORT           NUMBER;
    MI_CONSECUTIVO      NUMBER;
    MI_BENEFICIARIOT    VARCHAR2(32000);
    MI_DESCRIPCION      VARCHAR2(32000);
    MI_SALIDA           CLOB;
    MI_NOMBRECOMPANIA   VARCHAR2(32000);
    MI_NITCOMPANIA      VARCHAR2(32000);
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    V_CUENTAORIGEN NUMBER := UN_CUENTACLIENTE;--(CFBARRERA_CC:1441)
BEGIN
    -- Asignación de valores predeterminados a las variables locales en caso de que los parámetros de entrada sean nulos.--(CFBARRERA_CC:1441)
    IF V_CUENTAPRINCIPAL IS NULL THEN
        V_CUENTAPRINCIPAL := 0; -- Valor predeterminado para cuentas principales
    END IF;

    IF V_IDENTIFICADOR IS NULL THEN
        V_IDENTIFICADOR := 0; -- Valor predeterminado para identificadores
    END IF;

    IF V_DIGITOVERIFICACION IS NULL THEN
        V_DIGITOVERIFICACION := 0; -- Valor predeterminado para dígitos de verificación
    END IF;

    IF V_TIPO_CNTA_CLIENTE IS NULL THEN
        V_TIPO_CNTA_CLIENTE := ' '; -- Cadena vacía como valor predeterminado
    END IF;

    IF V_CLASE_TRANSACCION IS NULL THEN
        V_CLASE_TRANSACCION := ' '; -- Cadena vacía como valor predeterminado
    END IF;

  MI_NUMEROREGISTROS := 0;
  MI_VALORTOTAL      := 0;
  MI_SALIDA          := '';
  IF UN_ANO IS NOT NULL AND UN_EGRESOINICIAL <> 0 AND UN_EGRESOFINAL <> 0 AND UN_FECHAPAGO IS NOT NULL AND UN_CUENTACLIENTE IS NOT NULL AND UN_CUENTAPRINCIPAL IS NOT NULL THEN
    IF PCK_SYSMAN_UTL.FC_ESNUMERO(TO_CHAR(UN_CUENTACLIENTE)) = 0 THEN
      BEGIN
        MI_SALIDA := 'X';
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
      UN_EXC_COD   => SQLCODE,
      UN_ERROR_COD => PCK_ERRORES.ERR_DATOS_INGRESO
      );
      END;
      --Mensaje "El dato de la cuenta cliente a debitar debe ser un número máximo de 10 dígitos. Revise e intente de nuevo."
    END IF;
  END IF;
  
  BEGIN
    SELECT COUNT(*)
    INTO MI_NUMEROREGISTROS
    FROM
      (SELECT UN_CUENTACLIENTE      AS CUENTAORIGEN,
        COMPROBANTE_CNT.CUENTABANCO AS CUENTADESTINO,
        CASE
          WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) NOT IN (0)
          THEN APODERADO.NOMBRE
          ELSE TERCERO.NOMBRE
        END AS NOMBREBENEFICIARIO,
        CASE
          WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) NOT IN (0)
          THEN APODERADO.NIT
          ELSE TERCERO.NIT
        END AS NIT,
        CASE
          WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) NOT IN (0)
          THEN APODERADO.SUCURSAL
          ELSE TERCERO.SUCURSAL
        END AS SUCURSAL,
        CASE
          WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) NOT IN (0)
          THEN APODERADO.DIGITOVERIFICACION
          ELSE TERCERO.DIGITOVERIFICACION
        END AS DIGITOVERIFICA,
        CASE
          WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) NOT IN (0)
          THEN APODERADO.TIPOID
          ELSE TERCERO.TIPOID
        END AS TIPODOC,
        CASE
          WHEN (
            CASE
              WHEN NVL(PAGO_EFECTIVO,0) <> 0
              THEN '4'
              ELSE
                CASE
                  WHEN NRO_DOCUMENTO IS NOT NULL
                  THEN '1'
                  ELSE
                    CASE
                      WHEN COMPROBANTE_CNT.BANCO IN ('023')
                      THEN '2'
                      ELSE '3'
                    END
                END
            END) IN ('1','2','4')
          THEN '023'
          ELSE TO_CHAR(COMPROBANTE_CNT.BANCO)
        END AS BANCODESTINO,
        CASE
          WHEN (
            CASE
              WHEN NVL(PAGO_EFECTIVO,0) <> 0
              THEN '4'
              ELSE
                CASE
                  WHEN NRO_DOCUMENTO IS NOT NULL
                  THEN '1'
                  ELSE
                    CASE
                      WHEN COMPROBANTE_CNT.BANCO IN ('023')
                      THEN '2'
                      ELSE '3'
                    END
                END
            END) IN ('1: Pago en Cheque ','2: Pago abono a cuenta  - Banco de Occidente ','4: Pago en efectivo')
          THEN '023'
          ELSE TO_CHAR(COMPROBANTE_CNT.BANCO)
        END                           AS BANCODESTINOINF,
        COMPROBANTE_CNT.FECHAPAGADOGN AS FECHADEPAGO,
        CASE
          WHEN NVL(PAGO_EFECTIVO,0) <> 0
          THEN '4'
          ELSE
            CASE
              WHEN NRO_DOCUMENTO IS NOT NULL
              THEN '1'
              ELSE
                CASE
                  WHEN COMPROBANTE_CNT.BANCO IN ('023')
                  THEN '2'
                  ELSE '3'
                END
            END
        END AS FORMAPAGO,
        CASE
          WHEN NVL(PAGO_EFECTIVO,0) <> 0
          THEN '4: Pago en efectivo'
          ELSE
            CASE
              WHEN NVL(NRO_DOCUMENTO,'')<>''
              THEN '1: Pago en Cheque '
              ELSE
                CASE
                  WHEN NVL(CUENTABANCO,'') IN ('')
                  THEN ''
                  ELSE
                    CASE
                      WHEN COMPROBANTE_CNT.BANCO IN(UN_NUMEROBANCO)
                      THEN '2: Pago abono a cuenta  - Banco de Occidente '
                      ELSE
                        CASE
                          WHEN UN_CUENTACLIENTE <> CUENTABANCO
                          THEN '3: Abono a cuenta otras entidades'
                          ELSE ''
                        END
                    END
                END
            END
        END                       AS FORMAPAGOINF,
        COMPROBANTE_CNT.VLRAGIRAR AS VALORTRANSACCION,
        COMPROBANTE_CNT.NUMERO    AS COMPROBANTE,
        COMPROBANTE_CNT.DESCRIPCION,
        COMPROBANTE_CNT.NRO_DOCUMENTO,
        COMPROBANTE_CNT.PAGOENPLANO,
        COMPROBANTE_CNT.ENVIADO,
        COMPROBANTE_CNT.COMPANIA,
        COMPROBANTE_CNT.BANCO,
        COMPROBANTE_CNT.CUENTABANCO
      FROM COMPROBANTE_CNT
      LEFT JOIN TERCERO
      ON COMPROBANTE_CNT.COMPANIA  = TERCERO.COMPANIA
      AND COMPROBANTE_CNT.TERCERO  = TERCERO.NIT
      AND COMPROBANTE_CNT.SUCURSAL = TERCERO.SUCURSAL
      LEFT JOIN TERCERO APODERADO
      ON TERCERO.COMPANIA            = APODERADO.COMPANIA
      AND TERCERO.NITAPODERADO       = APODERADO.NIT
      AND TERCERO.SUCURSALAPODERADO  = APODERADO.SUCURSAL
      WHERE COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
      AND COMPROBANTE_CNT.ANO        = UN_ANO
      AND COMPROBANTE_CNT.TIPO       = UN_TIPOEGRESO
      AND COMPROBANTE_CNT.CUENTA BETWEEN UN_CUENTAINICIAL AND UN_CUENTAFINAL
      AND COMPROBANTE_CNT.NUMERO BETWEEN UN_EGRESOINICIAL AND UN_EGRESOFINAL
      AND COMPROBANTE_CNT.PAGOENPLANO NOT IN (0)
      AND COMPROBANTE_CNT.ENVIADO         IN (0)
      ORDER BY COMPROBANTE_CNT.TIPO
      ) CONSULTAPLANOSOCCIDENTE
    LEFT JOIN TERCEROPAGOS
    ON CONSULTAPLANOSOCCIDENTE.NIT          = TERCEROPAGOS.NIT
    AND CONSULTAPLANOSOCCIDENTE.SUCURSAL    = TERCEROPAGOS.SUCURSAL
    AND CONSULTAPLANOSOCCIDENTE.COMPANIA    = TERCEROPAGOS.COMPANIA
    AND CONSULTAPLANOSOCCIDENTE.BANCO       = TERCEROPAGOS.BANCO
    AND CONSULTAPLANOSOCCIDENTE.CUENTABANCO = TERCEROPAGOS.CUENTA
    WHERE
      CONSULTAPLANOSOCCIDENTE.CUENTAORIGEN IS NOT NULL AND
      CONSULTAPLANOSOCCIDENTE.CUENTADESTINO IS NOT NULL AND
      CONSULTAPLANOSOCCIDENTE.NOMBREBENEFICIARIO IS NOT NULL AND
      CONSULTAPLANOSOCCIDENTE.NIT IS NOT NULL AND
      CONSULTAPLANOSOCCIDENTE.BANCODESTINO IS NOT NULL AND
      CONSULTAPLANOSOCCIDENTE.FECHADEPAGO IS NOT NULL AND
      CONSULTAPLANOSOCCIDENTE.FORMAPAGO IS NOT NULL AND
      CONSULTAPLANOSOCCIDENTE.VALORTRANSACCION IS NOT NULL AND
      CONSULTAPLANOSOCCIDENTE.COMPROBANTE IS NOT NULL AND
      CONSULTAPLANOSOCCIDENTE.DESCRIPCION IS NOT NULL;--(CFBARRERA_CC:1441)
  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_NUMEROREGISTROS:=0;
    MI_VALORTOTAL := 0;
  END;
  
  IF MI_NUMEROREGISTROS > 0 THEN
    FOR MI_RS IN
    (SELECT CONSULTAPLANOSOCCIDENTE.CUENTAORIGEN,
      CONSULTAPLANOSOCCIDENTE.BANCODESTINO,
      CONSULTAPLANOSOCCIDENTE.CUENTADESTINO,
      CONSULTAPLANOSOCCIDENTE.NOMBREBENEFICIARIO,
      CONSULTAPLANOSOCCIDENTE.NIT,
      CONSULTAPLANOSOCCIDENTE.SUCURSAL ,
      CONSULTAPLANOSOCCIDENTE.DIGITOVERIFICA,
      CONSULTAPLANOSOCCIDENTE.TIPODOC,
      CONSULTAPLANOSOCCIDENTE.BANCODESTINOINF,
      CONSULTAPLANOSOCCIDENTE.FECHADEPAGO,
      CONSULTAPLANOSOCCIDENTE.FORMAPAGO,
      CONSULTAPLANOSOCCIDENTE.FORMAPAGOINF,
      CONSULTAPLANOSOCCIDENTE.VALORTRANSACCION,
      CONSULTAPLANOSOCCIDENTE.COMPROBANTE,
      CONSULTAPLANOSOCCIDENTE.DESCRIPCION,
      CONSULTAPLANOSOCCIDENTE.NRO_DOCUMENTO,
      CONSULTAPLANOSOCCIDENTE.PAGOENPLANO,
      CONSULTAPLANOSOCCIDENTE.ENVIADO,
      CONSULTAPLANOSOCCIDENTE.COMPANIA,
      CONSULTAPLANOSOCCIDENTE.BANCO,
      CONSULTAPLANOSOCCIDENTE.CUENTABANCO,
      TERCEROPAGOS.TIPOCUENTA
    FROM
      (SELECT UN_CUENTACLIENTE      AS CUENTAORIGEN,
        COMPROBANTE_CNT.CUENTABANCO AS CUENTADESTINO,
        CASE
          WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) NOT IN (0)
          THEN APODERADO.NOMBRE
          ELSE TERCERO.NOMBRE
        END AS NOMBREBENEFICIARIO,
        CASE
          WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) NOT IN (0)
          THEN APODERADO.NIT
          ELSE TERCERO.NIT
        END AS NIT,
        CASE
          WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) NOT IN (0)
          THEN APODERADO.SUCURSAL
          ELSE TERCERO.SUCURSAL
        END AS SUCURSAL,
        CASE
          WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) NOT IN (0)
          THEN APODERADO.DIGITOVERIFICACION
          ELSE TERCERO.DIGITOVERIFICACION
        END AS DIGITOVERIFICA,
        CASE
          WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) NOT IN (0)
          THEN APODERADO.TIPOID
          ELSE TERCERO.TIPOID
        END AS TIPODOC,
        CASE
          WHEN (
            CASE
              WHEN NVL(PAGO_EFECTIVO,0) <> 0
              THEN '4'
              ELSE
                CASE
                  WHEN NRO_DOCUMENTO IS NOT NULL
                  THEN '1'
                  ELSE
                    CASE
                      WHEN COMPROBANTE_CNT.BANCO IN ('023')
                      THEN '2'
                      ELSE '3'
                    END
                END
            END) IN ('1','2','4')
          THEN '023'
          ELSE TO_CHAR(COMPROBANTE_CNT.BANCO)
        END AS BANCODESTINO,
        CASE
          WHEN (
            CASE
              WHEN NVL(PAGO_EFECTIVO,0) <> 0
              THEN '4'
              ELSE
                CASE
                  WHEN NRO_DOCUMENTO IS NOT NULL
                  THEN '1'
                  ELSE
                    CASE
                      WHEN COMPROBANTE_CNT.BANCO IN ('023')
                      THEN '2'
                      ELSE '3'
                    END
                END
            END) IN ('1: Pago en Cheque ','2: Pago abono a cuenta  - Banco de Occidente ','4: Pago en efectivo')
          THEN '023'
          ELSE TO_CHAR(COMPROBANTE_CNT.BANCO)
        END                           AS BANCODESTINOINF,
        COMPROBANTE_CNT.FECHAPAGADOGN AS FECHADEPAGO,--(CFBARRERA_CC:1441)
        CASE
          WHEN NVL(PAGO_EFECTIVO,0) <> 0
          THEN '4'
          ELSE
            CASE
              WHEN NRO_DOCUMENTO IS NOT NULL
              THEN '1'
              ELSE
                CASE
                  WHEN COMPROBANTE_CNT.BANCO IN ('023')
                  THEN '2'
                  ELSE '3'
                END
            END
        END AS FORMAPAGO,
        CASE
          WHEN NVL(PAGO_EFECTIVO,0) <> 0
          THEN '4: Pago en efectivo'
          ELSE
            CASE
              WHEN NVL(NRO_DOCUMENTO,'')<>''
              THEN '1: Pago en Cheque '
              ELSE
                CASE
                  WHEN NVL(CUENTABANCO,'') IN ('')
                  THEN ''
                  ELSE
                    CASE
                      WHEN COMPROBANTE_CNT.BANCO IN(UN_NUMEROBANCO)
                      THEN '2: Pago abono a cuenta  - Banco de Occidente '
                      ELSE
                        CASE
                          WHEN UN_CUENTACLIENTE <> CUENTABANCO
                          THEN '3: Abono a cuenta otras entidades'
                          ELSE ''
                        END
                    END
                END
            END
        END                       AS FORMAPAGOINF,
        COMPROBANTE_CNT.VLRAGIRAR AS VALORTRANSACCION,
        COMPROBANTE_CNT.NUMERO    AS COMPROBANTE,
        COMPROBANTE_CNT.DESCRIPCION,
        COMPROBANTE_CNT.NRO_DOCUMENTO,
        COMPROBANTE_CNT.PAGOENPLANO,
        COMPROBANTE_CNT.ENVIADO,
        COMPROBANTE_CNT.COMPANIA,
        COMPROBANTE_CNT.BANCO,
        COMPROBANTE_CNT.CUENTABANCO
      FROM COMPROBANTE_CNT
      LEFT JOIN TERCERO
      ON COMPROBANTE_CNT.COMPANIA  = TERCERO.COMPANIA
      AND COMPROBANTE_CNT.TERCERO  = TERCERO.NIT
      AND COMPROBANTE_CNT.SUCURSAL = TERCERO.SUCURSAL
      LEFT JOIN TERCERO APODERADO
      ON TERCERO.COMPANIA            = APODERADO.COMPANIA
      AND TERCERO.NITAPODERADO       = APODERADO.NIT
      AND TERCERO.SUCURSALAPODERADO  = APODERADO.SUCURSAL
      WHERE COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
      AND COMPROBANTE_CNT.ANO        = UN_ANO
      AND COMPROBANTE_CNT.TIPO       = UN_TIPOEGRESO
      AND COMPROBANTE_CNT.CUENTA BETWEEN UN_CUENTAINICIAL AND UN_CUENTAFINAL
      AND COMPROBANTE_CNT.NUMERO BETWEEN UN_EGRESOINICIAL AND UN_EGRESOFINAL
      AND COMPROBANTE_CNT.PAGOENPLANO NOT IN (0)
      AND COMPROBANTE_CNT.ENVIADO         IN (0)
      ORDER BY COMPROBANTE_CNT.TIPO
      ) CONSULTAPLANOSOCCIDENTE
    LEFT JOIN TERCEROPAGOS
    ON CONSULTAPLANOSOCCIDENTE.NIT          = TERCEROPAGOS.NIT
    AND CONSULTAPLANOSOCCIDENTE.SUCURSAL    = TERCEROPAGOS.SUCURSAL
    AND CONSULTAPLANOSOCCIDENTE.COMPANIA    = TERCEROPAGOS.COMPANIA
    AND CONSULTAPLANOSOCCIDENTE.BANCO       = TERCEROPAGOS.BANCO
    AND CONSULTAPLANOSOCCIDENTE.CUENTABANCO = TERCEROPAGOS.CUENTA
    )
    LOOP
    -- Esto asegura que solo se procesen e impriman registros completos y válidos en el archivo plano.
  IF MI_RS.CUENTAORIGEN IS NULL OR
     MI_RS.CUENTADESTINO IS NULL OR
     MI_RS.NOMBREBENEFICIARIO IS NULL OR
     MI_RS.NIT IS NULL OR
     MI_RS.BANCODESTINO IS NULL OR
     MI_RS.FECHADEPAGO IS NULL OR
     MI_RS.FORMAPAGO IS NULL OR
     MI_RS.VALORTRANSACCION IS NULL OR
     MI_RS.COMPROBANTE IS NULL OR
     MI_RS.DESCRIPCION IS NULL THEN
    CONTINUE;
  END IF;
     -- Solo aquí suma y cuenta los registros válidos
      MI_VALORTOTAL      := MI_VALORTOTAL      + MI_RS.VALORTRANSACCION;
    END LOOP;
  ELSE
  --En este caso como el for no trae datos, se retorna un mensaje que dice
  --ERROR para realizar el tratamiento en el controlador
  MI_SALIDA := 'ERROR';
  RETURN MI_SALIDA;
  END IF;
  BEGIN
    SELECT UPPER(NOMBRE), REPLACE(NITCOMPANIA, '-', '')
    INTO MI_NOMBRECOMPANIA, MI_NITCOMPANIA
        FROM COMPANIA
        WHERE CODIGO = UN_COMPANIA;
    EXCEPTION
        WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
        MI_NOMBRECOMPANIA := '';
        MI_NITCOMPANIA := '';
  END;
  --- MPEREZ 7741569: Ajuste para generar el plano con la estructura correcta.
  MI_VALORT  := PCK_SYSMAN_UTL.FC_ROUND(MI_VALORTOTAL,2)*100;
  
    --Construccion Encabezado:
    -- Generar la primera línea del archivo plano (Tipo de registro 1)
    -- Posición 1: Tipo de registro (Valor fijo 1)
    MI_SALIDA := '1'; 
    
    -- Posición 2-5: Consecutivo (Campo lleno de ceros, longitud 4)
    MI_SALIDA := MI_SALIDA || '0000';
    
    -- Posición 6-13: Fecha de Pago (Formato AAAAMMDD, longitud 8)
    MI_SALIDA := MI_SALIDA || TO_CHAR(UN_FECHAPAGO, 'YYYYMMDD');
    
    -- Posición 14-17: Número de registros (Justificado a la derecha con ceros, longitud 4)
    MI_SALIDA := MI_SALIDA || LPAD(MI_NUMEROREGISTROS, 4, '0');
    
    -- Posición 18-35: Valor Total (16 enteros y 2 decimales, justificado a la derecha con ceros, longitud 18)
    MI_SALIDA := MI_SALIDA || LPAD(MI_VALORT, 18, '0');
    
    -- Posición 36-51: Cuenta Principal (Justificada a la derecha con ceros, longitud 16)
    MI_SALIDA := MI_SALIDA || LPAD(V_CUENTAORIGEN, 16, '0');
    
    -- Posición 52-57: Identificación del archivo (Campo lleno de ceros, longitud 6)
    MI_SALIDA := MI_SALIDA || LPAD('0', 6, '0');
    
    -- Posición 58-199: Ceros (Campo lleno de ceros, longitud 142)
    MI_SALIDA := MI_SALIDA || LPAD('0', 142, '0');

  MI_CONSECUTIVO := 0;
  IF MI_NUMEROREGISTROS > 0 THEN
    FOR MI_RS2 IN
    (SELECT CONSULTAPLANOSOCCIDENTE.CUENTAORIGEN,
      CONSULTAPLANOSOCCIDENTE.CUENTADESTINO,
      CONSULTAPLANOSOCCIDENTE.NOMBREBENEFICIARIO,
      CONSULTAPLANOSOCCIDENTE.NIT,
      CONSULTAPLANOSOCCIDENTE.SUCURSAL ,
      CONSULTAPLANOSOCCIDENTE.DIGITOVERIFICA,
      CONSULTAPLANOSOCCIDENTE.TIPODOC,
      CONSULTAPLANOSOCCIDENTE.BANCODESTINOINF,
      CONSULTAPLANOSOCCIDENTE.FECHADEPAGO,
      CONSULTAPLANOSOCCIDENTE.FORMAPAGO,
      CONSULTAPLANOSOCCIDENTE.FORMAPAGOINF,
      CONSULTAPLANOSOCCIDENTE.VALORTRANSACCION,
      CONSULTAPLANOSOCCIDENTE.COMPROBANTE,
      CONSULTAPLANOSOCCIDENTE.DESCRIPCION,
      CONSULTAPLANOSOCCIDENTE.NRO_DOCUMENTO,
      CONSULTAPLANOSOCCIDENTE.PAGOENPLANO,
      CONSULTAPLANOSOCCIDENTE.ENVIADO,
      CONSULTAPLANOSOCCIDENTE.COMPANIA,
      CONSULTAPLANOSOCCIDENTE.BANCO,
      CONSULTAPLANOSOCCIDENTE.CUENTABANCO,
      CONSULTAPLANOSOCCIDENTE.BANCODESTINO,
      TERCEROPAGOS.TIPOCUENTA
    FROM
      (SELECT UN_CUENTACLIENTE      AS CUENTAORIGEN,
        COMPROBANTE_CNT.CUENTABANCO AS CUENTADESTINO,
        CASE
          WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) NOT IN (0)
          THEN APODERADO.NOMBRE
          ELSE TERCERO.NOMBRE
        END AS NOMBREBENEFICIARIO,
        CASE
          WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) NOT IN (0)
          THEN APODERADO.NIT
          ELSE TERCERO.NIT
        END AS NIT,
        CASE
          WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) NOT IN (0)
          THEN APODERADO.SUCURSAL
          ELSE TERCERO.SUCURSAL
        END AS SUCURSAL,
        CASE
          WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) NOT IN (0)
          THEN APODERADO.DIGITOVERIFICACION
          ELSE TERCERO.DIGITOVERIFICACION
        END AS DIGITOVERIFICA,
        CASE
          WHEN NVL(COMPROBANTE_CNT.PAGOAPODERADO,0) NOT IN (0)
          THEN APODERADO.TIPOID
          ELSE TERCERO.TIPOID
        END AS TIPODOC,
        CASE
          WHEN COMPROBANTE_CNT.BANCO = '023'
            THEN '023'
          ELSE TO_CHAR(COMPROBANTE_CNT.BANCO)
        END AS BANCODESTINO,--(CFBARRERA_CC:1441)
        CASE
          WHEN (
            CASE
              WHEN NVL(PAGO_EFECTIVO,0) <> 0
              THEN '4'
              ELSE
                CASE
                  WHEN NRO_DOCUMENTO IS NOT NULL
                  THEN '1'
                  ELSE
                    CASE
                      WHEN COMPROBANTE_CNT.BANCO IN ('023')
                      THEN '2'
                      ELSE '3'
                    END
                END
            END) IN ('1: Pago en Cheque ','2: Pago abono a cuenta  - Banco de Occidente ','4: Pago en efectivo')
          THEN '023'
          ELSE TO_CHAR(COMPROBANTE_CNT.BANCO)
        END                           AS BANCODESTINOINF,
        COMPROBANTE_CNT.FECHAPAGADOGN AS FECHADEPAGO,
        CASE
            WHEN COMPROBANTE_CNT.BANCO IN ('023')
            THEN '2'
            ELSE '3'
        END AS FORMAPAGO,--(CFBARRERA_CC:1441)
        CASE
          WHEN NVL(PAGO_EFECTIVO,0) <> 0
          THEN '4: Pago en efectivo'
          ELSE
            CASE
              WHEN NVL(NRO_DOCUMENTO,'')<>''
              THEN '1: Pago en Cheque '
              ELSE
                CASE
                  WHEN NVL(CUENTABANCO,'') IN ('')
                  THEN ''
                  ELSE
                    CASE
                      WHEN COMPROBANTE_CNT.BANCO IN(UN_NUMEROBANCO)
                      THEN '2: Pago abono a cuenta  - Banco de Occidente '
                      ELSE
                        CASE
                          WHEN UN_CUENTACLIENTE <> CUENTABANCO
                          THEN '3: Abono a cuenta otras entidades'
                          ELSE ''
                        END
                    END
                END
            END
        END                       AS FORMAPAGOINF,
        COMPROBANTE_CNT.VLRAGIRAR AS VALORTRANSACCION,
        COMPROBANTE_CNT.NUMERO    AS COMPROBANTE,
        COMPROBANTE_CNT.DESCRIPCION,
        COMPROBANTE_CNT.NRO_DOCUMENTO,
        COMPROBANTE_CNT.PAGOENPLANO,
        COMPROBANTE_CNT.ENVIADO,
        COMPROBANTE_CNT.COMPANIA,
        COMPROBANTE_CNT.BANCO,
        COMPROBANTE_CNT.CUENTABANCO
      FROM COMPROBANTE_CNT
      LEFT JOIN TERCERO
      ON COMPROBANTE_CNT.COMPANIA  = TERCERO.COMPANIA
      AND COMPROBANTE_CNT.TERCERO  = TERCERO.NIT
      AND COMPROBANTE_CNT.SUCURSAL = TERCERO.SUCURSAL
      LEFT JOIN TERCERO APODERADO
      ON TERCERO.COMPANIA            = APODERADO.COMPANIA
      AND TERCERO.NITAPODERADO       = APODERADO.NIT
      AND TERCERO.SUCURSALAPODERADO  = APODERADO.SUCURSAL
      WHERE COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
      AND COMPROBANTE_CNT.ANO        = UN_ANO
      AND COMPROBANTE_CNT.TIPO       = UN_TIPOEGRESO
      AND COMPROBANTE_CNT.CUENTA BETWEEN UN_CUENTAINICIAL AND UN_CUENTAFINAL
      AND COMPROBANTE_CNT.NUMERO BETWEEN UN_EGRESOINICIAL AND UN_EGRESOFINAL
      AND COMPROBANTE_CNT.PAGOENPLANO NOT IN (0)
      AND COMPROBANTE_CNT.ENVIADO         IN (0)
      ORDER BY COMPROBANTE_CNT.TIPO
      ) CONSULTAPLANOSOCCIDENTE
    LEFT JOIN TERCEROPAGOS
    ON CONSULTAPLANOSOCCIDENTE.NIT          = TERCEROPAGOS.NIT
    AND CONSULTAPLANOSOCCIDENTE.SUCURSAL    = TERCEROPAGOS.SUCURSAL
    AND CONSULTAPLANOSOCCIDENTE.COMPANIA    = TERCEROPAGOS.COMPANIA
    AND CONSULTAPLANOSOCCIDENTE.BANCO       = TERCEROPAGOS.BANCO
    AND CONSULTAPLANOSOCCIDENTE.CUENTABANCO = TERCEROPAGOS.CUENTA
    )
    LOOP
  -- Esto asegura que solo se procesen e impriman registros completos y válidos en el archivo plano.
  IF MI_RS2.CUENTAORIGEN IS NULL OR
     MI_RS2.CUENTADESTINO IS NULL OR
     MI_RS2.NOMBREBENEFICIARIO IS NULL OR
     MI_RS2.NIT IS NULL OR
     MI_RS2.BANCODESTINO IS NULL OR
     MI_RS2.FECHADEPAGO IS NULL OR
     MI_RS2.FORMAPAGO IS NULL OR
     MI_RS2.VALORTRANSACCION IS NULL OR
     MI_RS2.COMPROBANTE IS NULL OR
     MI_RS2.DESCRIPCION IS NULL THEN
    CONTINUE;
  END IF;
    --Construccion Detalle:
    -- Incrementa el consecutivo de registros detalle
    MI_CONSECUTIVO          := MI_CONSECUTIVO + 1;
    
    -- Posición 1: Tipo de registro (Numérico, 1, valor fijo 2)
    MI_SALIDA := MI_SALIDA || CHR(10) || '2';

    -- Posición 2-5: Consecutivo (Numérico, 4, justificado a la derecha con ceros)
    MI_SALIDA := MI_SALIDA || LPAD(MI_CONSECUTIVO, 4, '0');

    -- Posición 6-21: Cuenta Origen (Numérico, 16, justificado a la derecha con ceros)
    MI_SALIDA := MI_SALIDA || LPAD(MI_RS2.CUENTAORIGEN, 16, '0');

    -- Posición 22-51: Beneficiario (Texto, 30, justificado a la izquierda con espacios)
    MI_BENEFICIARIOT := REPLACE(MI_RS2.NOMBREBENEFICIARIO,CHR(13) || CHR(10),' ');
    MI_BENEFICIARIOT := REPLACE(MI_BENEFICIARIOT,CHR(13),' ');
    MI_BENEFICIARIOT := REPLACE(MI_BENEFICIARIOT,CHR(10),' ');
    MI_BENEFICIARIOT := REPLACE(MI_BENEFICIARIOT,'Á','A');
    MI_BENEFICIARIOT := REPLACE(MI_BENEFICIARIOT,'É','E');
    MI_BENEFICIARIOT := REPLACE(MI_BENEFICIARIOT,'Í','I');
    MI_BENEFICIARIOT := REPLACE(MI_BENEFICIARIOT,'Ó','O');
    MI_BENEFICIARIOT := REPLACE(MI_BENEFICIARIOT,'Ú','U');
    MI_BENEFICIARIOT := REPLACE(MI_BENEFICIARIOT,'Ñ','N');
    MI_BENEFICIARIOT := REPLACE(MI_BENEFICIARIOT,'á','a');
    MI_BENEFICIARIOT := REPLACE(MI_BENEFICIARIOT,'é','e');
    MI_BENEFICIARIOT := REPLACE(MI_BENEFICIARIOT,'í','i');
    MI_BENEFICIARIOT := REPLACE(MI_BENEFICIARIOT,'ó','o');
    MI_BENEFICIARIOT := REPLACE(MI_BENEFICIARIOT,'ú','u');
    MI_BENEFICIARIOT := REPLACE(MI_BENEFICIARIOT,'ñ','n');
    MI_SALIDA := MI_SALIDA || RPAD(MI_BENEFICIARIOT, 30, ' ');

    -- Posición 52-62: NIT/CC (Numérico, 11, justificado a la derecha con ceros)
    IF UN_DIGITOVERIFICACION NOT IN (0) THEN
        IF MI_RS2.TIPODOC IN ('N') THEN
            MI_SALIDA := MI_SALIDA || LPAD(MI_RS2.NIT || MI_RS2.DIGITOVERIFICA, 11, '0');
        ELSE
            MI_SALIDA := MI_SALIDA || LPAD(MI_RS2.NIT, 11, '0');
        END IF;
    ELSE
        MI_SALIDA := MI_SALIDA || LPAD(MI_RS2.NIT, 11, '0');
    END IF;
    
    -- Posición 63-66: Código del Banco (Numérico, 4, justificado a la derecha con ceros)
    MI_SALIDA := MI_SALIDA || LPAD(MI_RS2.BANCODESTINO, 4, '0');

    -- Posición 67-74: Fecha de Pago (Numérico, 8, formato AAAAMMDD)
    IF (PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                              UN_NOMBRE    => 'PERMITE MODIFICAR FECHA DE PAGO EN ARCHIVOS BANCO OCCIDENTE',
                                              UN_MODULO    => PCK_DATOS.FC_MODULOCONTABILIDAD,
                                              UN_FECHA_PAR => SYSDATE) = 'SI') THEN
        MI_SALIDA := MI_SALIDA || TO_CHAR(UN_FECHAPAGO, 'YYYYMMDD');
    ELSE
        MI_SALIDA := MI_SALIDA || TO_CHAR(MI_RS2.FECHADEPAGO, 'YYYYMMDD');
    END IF;

    -- Posición 75: Forma de Pago (Numérico, 1)
    MI_SALIDA := MI_SALIDA || MI_RS2.FORMAPAGO;

    -- Posición 76-90: Valor (Numérico, 13 enteros y 2 decimales, justificado a la derecha con ceros)
    MI_SALIDA := MI_SALIDA || LPAD(PCK_SYSMAN_UTL.FC_ROUND(MI_RS2.VALORTRANSACCION,2)*100, 15, '0');

    -- Posición 91-106: Cuenta destino (Numérico, 16, justificada a la izquierda con espacios a la derecha)
    MI_SALIDA := MI_SALIDA || RPAD(MI_RS2.CUENTADESTINO, 16, ' ');

    -- Posición 107-118: Comprobante (Numérico, 12, justificado a la derecha con espacios a la izquierda)
    MI_SALIDA := MI_SALIDA || LPAD(MI_RS2.COMPROBANTE, 12, ' ');

    -- Posición 119: Tipo de Cuenta Destino (Carácter, 1)
    MI_SALIDA := MI_SALIDA || CASE 
        WHEN MI_RS2.TIPOCUENTA = 'C' THEN 'C'
        WHEN MI_RS2.TIPOCUENTA = 'A' THEN 'A'
        ELSE ' '
    END;

    -- Posición 120-199: Concepto (Carácter, 80, espacio adicional para detallar el concepto)
    MI_DESCRIPCION := REPLACE(MI_RS2.DESCRIPCION,CHR(13) || CHR(10),' ');
    MI_DESCRIPCION := REPLACE(MI_DESCRIPCION,CHR(13),' ');
    MI_DESCRIPCION := REPLACE(MI_DESCRIPCION,CHR(10),' ');
    MI_DESCRIPCION := REPLACE(MI_DESCRIPCION,'Á','A');
    MI_DESCRIPCION := REPLACE(MI_DESCRIPCION,'É','E');
    MI_DESCRIPCION := REPLACE(MI_DESCRIPCION,'Í','I');
    MI_DESCRIPCION := REPLACE(MI_DESCRIPCION,'Ó','O');
    MI_DESCRIPCION := REPLACE(MI_DESCRIPCION,'Ú','U');
    MI_DESCRIPCION := REPLACE(MI_DESCRIPCION,'Ñ','N');
    MI_DESCRIPCION := REPLACE(MI_DESCRIPCION,'á','a');
    MI_DESCRIPCION := REPLACE(MI_DESCRIPCION,'é','e');
    MI_DESCRIPCION := REPLACE(MI_DESCRIPCION,'í','i');
    MI_DESCRIPCION := REPLACE(MI_DESCRIPCION,'ó','o');
    MI_DESCRIPCION := REPLACE(MI_DESCRIPCION,'ú','u');
    MI_DESCRIPCION := REPLACE(MI_DESCRIPCION,'ñ','n');
    MI_SALIDA := MI_SALIDA || RPAD(SUBSTR(MI_DESCRIPCION, 0, 80), 80, ' ');--(CFBARRERA_1441)
    END LOOP;
  END IF;
    --Construccion Pie de archivo
    -- Tipo de registro (Numérico, 1, posición 1, valor fijo 3)
    MI_SALIDA := MI_SALIDA || CHR(10) || '3';
    
    -- Secuencia (Numérico, 4, posiciones 2-5, valor fijo 9999)
    MI_SALIDA := MI_SALIDA || '9999';
    
    -- Número de Registros (Numérico, 4, posiciones 6-9, justificado a la derecha con ceros)
    MI_SALIDA := MI_SALIDA || LPAD(MI_NUMEROREGISTROS, 4, '0');
    
    -- Valor Total (Numérico, 16 enteros y 2 decimales, posiciones 10-27, justificado a la derecha con ceros)
    MI_SALIDA := MI_SALIDA || LPAD(MI_VALORT, 18, '0');
    
    -- Ceros (Numérico, 172, posiciones 28-199, campo lleno de ceros)
    MI_SALIDA := MI_SALIDA || LPAD(0, 172, '0');--(CFBARRERA_1441)
  BEGIN
    BEGIN
      MI_TABLA := 'COMPROBANTE_CNT';
      MI_CAMPOS := 'ENVIADO = -1';
      MI_CONDICION := '   COMPANIA        = '''  || UN_COMPANIA ||
                      ''' AND         ANO = '    || UN_ANO ||
                      '   AND        TIPO = '''  || UN_TIPOEGRESO ||
                      ''' AND PAGOENPLANO NOT IN (0)
                          AND ENVIADO     IN (0)
                          AND NUMERO      BETWEEN '    || UN_EGRESOINICIAL || ' AND ' || UN_EGRESOFINAL ||
            '   AND CUENTA      BETWEEN '    || UN_CUENTAINICIAL || ' AND ' || UN_CUENTAFINAL || '' ; --Ticket 7704307 - gfigueredo
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                            UN_ACCION    => 'M',
                                            UN_CAMPOS    => MI_CAMPOS,
                                            UN_CONDICION => MI_CONDICION);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
      RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
  END;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
    PCK_ERR_MSG.RAISE_WITH_MSG(
      UN_EXC_COD   => SQLCODE,
      UN_ERROR_COD => PCK_ERRORES.ERR_REGISTROREALIZADO
    );
  END;
RETURN MI_SALIDA;
END FC_GENERARARCPLANOS_BANCOS;

PROCEDURE PR_DEPURARPAGORETENCIONES
(
  /*
  NAME                    : PR_DEPURARPAGORETENCIONES     
  AUTHORS                 : STEFANINI SYSMAN SAS
  AUTHORS MIGRACION       : JOSE PASCUAL GOMEZ BLANCO
  DATE MIGRADOR           : 13/11/2018
  TIME                    : 08:00 AM
  SOURCE MODULE           : 
  MODIFIER                :
  DATA MODIFIED           :
  TIME                    :
  DESCRIPCION             : Permite identificar el movimiento y cuenta por el cual se paga la retención de un movimiento,
                            siempre y cuando solo tenga una movimiento (EGR) y una sola cuenta por la cual se paga de acuerdo
                            a las afectaciones del movimiento donde reposa la retención.
                            Se tienen en cuenta tambien los movimientos (EGR) que tienen el pago y la retención en el mismo
  MODIFICATIONS           :
  MODIFIER                :
  TIME                    :
  MODIFICATIONS           :
  @NAME  : depurarPagoRetenciones
  @METHOD  : GET
  */
    UN_COMPANIA  IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES       IN PCK_SUBTIPOS.TI_MES
)
AS
    MI_CAMPOS                     PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES                    PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION                  PCK_SUBTIPOS.TI_CONDICION;
    MI_MERGEUSING                 PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE                PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE                PCK_SUBTIPOS.TI_MERGEEXISTE;
BEGIN
    /*
        PRIMER SELECT: Contiene todos los movimientos(COM,NOM, Cuentas por pagar) 
                       que ya tienen un movimiento afectado(EGR, pagos) y no a sido actualizado
        SEGUNDO SELECT: Contiene los movimeintos (EGR, pagos) que tienen en el mismo 
                        la cuenta de banco y la cuenta de retención.
        HAVING COUNT(COMPANIA)=1 Esta condición se deja para que solo se actualice los movimientos que tienen
                                 un solo pago ( comprobante y cuenta), los demas el usuario los debe depurar a mano
                                 para que el sea conciente de a que banco se debe llevar pues el pago según los EGRs se 
                                 realiza por más de una cuenta bancaria o por más de un movimiento.
    */
    MI_MERGEUSING := ' SELECT COMPANIA,
                               ANO,
                               TIPO_CPTE,
                               COMPROBANTE,
                               CONSECUTIVO,
                               CUENTA,
                               MIN(TIPO_CPTEPAGO || '';'' || COMPROBANTEPAGO || '';'' || ANOPAGO || '';'' || CUENTAPAGO || '';'' || FECHAPAGO) REFER,
                               COUNT(COMPANIA)
                        FROM (  SELECT RELA.COMPANIA, 
                                       RELA.ANO_AFECT ANO, 
                                       RELA.TIPO_CPTE_AFECT TIPO_CPTE, 
                                       RELA.COMPROBANTE_AFECT COMPROBANTE,
                                       RETE.CONSECUTIVO,
                                       RETE.CUENTA,
                                       EGR.TIPO_CPTE TIPO_CPTEPAGO,
                                       EGR.COMPROBANTE COMPROBANTEPAGO,
                                       EGR.ANO    ANOPAGO,
                                       EGR.CUENTA CUENTAPAGO,
                                       EGR.FECHA  FECHAPAGO
                                FROM COMPROBANTE_CNTAFECTADOS RELA INNER JOIN TIPO_COMPROBANTE 
                                  ON RELA.COMPANIA  = TIPO_COMPROBANTE.COMPANIA 
                                 AND RELA.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
                                INNER JOIN DETALLE_COMPROBANTE_CNT EGR 
                                   ON RELA.COMPANIA     = EGR.COMPANIA
                                  AND RELA.ANO          = EGR.ANO
                                  AND RELA.TIPO_CPTE    = EGR.TIPO_CPTE
                                  AND RELA.COMPROBANTE  = EGR.COMPROBANTE
                                INNER JOIN PLAN_CONTABLE
                                        ON EGR.COMPANIA = PLAN_CONTABLE.COMPANIA
                                       AND EGR.ANO      = PLAN_CONTABLE.ANO
                                       AND EGR.CUENTA   = PLAN_CONTABLE.CODIGO
                                INNER JOIN DETALLE_COMPROBANTE_CNT RETE 
                                   ON RELA.COMPANIA          = RETE.COMPANIA
                                  AND RELA.ANO_AFECT         = RETE.ANO
                                  AND RELA.TIPO_CPTE_AFECT   = RETE.TIPO_CPTE
                                  AND RELA.COMPROBANTE_AFECT = RETE.COMPROBANTE
                                INNER JOIN PLAN_CONTABLE CUENTARETE
                                        ON RETE.COMPANIA = CUENTARETE.COMPANIA
                                       AND RETE.ANO      = CUENTARETE.ANO
                                       AND RETE.CUENTA   = CUENTARETE.CODIGO  
                                WHERE EGR.COMPANIA               = ''' || UN_COMPANIA || '''
                                  AND EGR.ANO                    = '   || UN_ANIO     || ' 
                                  AND TO_NUMBER(TO_CHAR(EGR.FECHA,''MM''))   = ' || UN_MES || '
                                  AND RETE.PAGO_RETENCION        IN(0)  
                                  AND PLAN_CONTABLE.CLASECUENTA IN(''B'')  
                                  AND TIPO_COMPROBANTE.CLASE_CONTABLE IN(''E'',''G'')   
                                  AND CUENTARETE.CLASECUENTA IN(''I'')
                                UNION
                                SELECT EGR.COMPANIA, 
                                       EGR.ANO, 
                                       EGR.TIPO_CPTE, 
                                       EGR.COMPROBANTE,
                                       RETE.CONSECUTIVO,
                                       RETE.CUENTA,
                                       EGR.TIPO_CPTE TIPO_CPTEPAGO,
                                       EGR.COMPROBANTE COMPROBANTEPAGO,
                                       EGR.ANO    ANOPAGO,
                                       EGR.CUENTA CUENTAPAGO,
                                       EGR.FECHA  FECHAPAGO
                                FROM DETALLE_COMPROBANTE_CNT EGR INNER JOIN TIPO_COMPROBANTE 
                                  ON EGR.COMPANIA  = TIPO_COMPROBANTE.COMPANIA 
                                 AND EGR.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
                                INNER JOIN PLAN_CONTABLE
                                        ON EGR.COMPANIA = PLAN_CONTABLE.COMPANIA
                                       AND EGR.ANO      = PLAN_CONTABLE.ANO
                                       AND EGR.CUENTA   = PLAN_CONTABLE.CODIGO
                                INNER JOIN (SELECT DET.COMPANIA, DET.ANO, DET.TIPO_CPTE, DET.COMPROBANTE, DET.CUENTA, DET.CONSECUTIVO
                                            FROM DETALLE_COMPROBANTE_CNT DET INNER JOIN PLAN_CONTABLE
                                              ON PLAN_CONTABLE.COMPANIA   = DET.COMPANIA
                                             AND PLAN_CONTABLE.ANO        = DET.ANO
                                             AND PLAN_CONTABLE.CODIGO     = DET.CUENTA
                                            WHERE DET.COMPANIA    = ''' || UN_COMPANIA || '''
                                              AND DET.ANO         = '   || UN_ANIO     || '
                                              AND TO_NUMBER(TO_CHAR(DET.FECHA,''MM''))   = ' || UN_MES || '
                                              AND DET.PAGO_RETENCION        IN(0) 
                                              AND PLAN_CONTABLE.CLASECUENTA IN(''I'')
                                              ) RETE
                                       ON EGR.COMPANIA     = RETE.COMPANIA
                                      AND EGR.ANO          = RETE.ANO
                                      AND EGR.TIPO_CPTE    = RETE.TIPO_CPTE
                                      AND EGR.COMPROBANTE  = RETE.COMPROBANTE
                                WHERE EGR.COMPANIA    = ''' || UN_COMPANIA || '''
                                  AND EGR.ANO         = '   || UN_ANIO     || '
                                  AND TO_NUMBER(TO_CHAR(EGR.FECHA,''MM''))   = ' || UN_MES || '
                                  AND PLAN_CONTABLE.CLASECUENTA IN(''B'')  
                                  AND TIPO_COMPROBANTE.CLASE_CONTABLE IN(''E'',''G'',''I'',''S'')            
                        )
                        HAVING COUNT(COMPANIA)=1
                        GROUP BY COMPANIA,
                               ANO,
                               TIPO_CPTE,
                               COMPROBANTE,
                               CONSECUTIVO,
                               CUENTA';
         MI_MERGEENLACE := 'VISTA.COMPANIA     = TABLA.COMPANIA 
                        AND VISTA.ANO          = TABLA.ANO 
                        AND VISTA.TIPO_CPTE    = TABLA.TIPO_CPTE 
                        AND VISTA.COMPROBANTE  = TABLA.COMPROBANTE
                        AND VISTA.CONSECUTIVO  = TABLA.CONSECUTIVO';
         MI_MERGEEXISTE := ' UPDATE SET TABLA.PAGO_RETENCION             = -1
                                      , TABLA.TIPO_PAGO_RETENCION        = SUBSTR(VISTA.REFER, 1, INSTR(VISTA.REFER,'';'',1,1)-1)
                                      , TABLA.COMPROBANTE_PAGO_RETENCION = SUBSTR(VISTA.REFER,    INSTR(VISTA.REFER,'';'',1,1)+1 ,(INSTR(VISTA.REFER,'';'',1,2)-1)- INSTR(VISTA.REFER,'';'',1,1)) 
                                      , TABLA.ANO_RETENCION              = SUBSTR(VISTA.REFER,    INSTR(VISTA.REFER,'';'',1,2)+1 ,(INSTR(VISTA.REFER,'';'',1,3)-1)- INSTR(VISTA.REFER,'';'',1,2)) 
                                      , TABLA.BANCO_RETENCION            = SUBSTR(VISTA.REFER,    INSTR(VISTA.REFER,'';'',1,3)+1 ,(INSTR(VISTA.REFER,'';'',1,4)-1)- INSTR(VISTA.REFER,'';'',1,3))
                                      , TABLA.FECHA_PAGO_RETENCION       = TO_DATE(SUBSTR(VISTA.REFER,    INSTR(VISTA.REFER,'';'',1,4)+1),''DD/MM/YYYY HH24:MI:SS'')';  
    BEGIN
        BEGIN  
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA       => 'DETALLE_COMPROBANTE_CNT'
                                                   ,UN_ACCION      => 'MM'
                                                   ,UN_MERGEUSING  => MI_MERGEUSING
                                                   ,UN_MERGEENLACE => MI_MERGEENLACE
                                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERR_DETALLE_AFECTADO
           );
    END;    
END PR_DEPURARPAGORETENCIONES;



FUNCTION FC_CODIGORETENCIONTERCERO 
(
  /*
  NAME                    :  FC_CODIGORETENCIONTERCERO    
  AUTHORS                 : STEFANINI SYSMAN SAS
  AUTHORS MIGRACION       : JOSE PASCUAL GOMEZ BLANCO
  DATE MIGRADOR           : 24/11/2018
  TIME                    : 11:50 AM
  SOURCE MODULE           : 
  MODIFIER                :
  DATA MODIFIED           :
  TIME                    :
  DESCRIPCION             : Permite identificar el codigo de una retención de acuerdo al tercero y sus caracteristicas de
                            de REGIMEN, TIPO DE PERSONA y AUTORETENEDOR
                          La salida esta separada por punto y coma así: COMPANIA,TIPO,ANO,CODIGO,CUENTA_DEBITO,
                                                                       CUENTA_CREDITO,PCT_BASE,LIMITE_INF,PCT_APLICAR,
                                                                       FACTORREDONDEO, CIIU
                              Para efectos de incosistencias la ultima posición guardara el mensaje del mismo si hay lugar
  MODIFICATIONS           :
  MODIFIER                :
  TIME                    :
  MODIFICATIONS           :
  @NAME  : depurarPagoRetenciones
  @METHOD  : GET
  */
  UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA
, UN_ANIO       IN PCK_SUBTIPOS.TI_ANIO  
, UN_TERCERO    IN PCK_SUBTIPOS.TI_TERCERO 
, UN_SUCURSAL   IN PCK_SUBTIPOS.TI_SUCURSAL
, UN_TIPOFUENTE IN VARCHAR2 
, UN_CONCEPTO   IN VARCHAR2  
) RETURN VARCHAR2 AS 
    MI_REGIMEN        VARCHAR2(2);
    MI_TIPOPERSONA    VARCHAR2(2);
    MI_CIIU           VARCHAR2(20);
    MI_AUTORRETENEDOR PCK_SUBTIPOS.TI_LOGICO;
    MI_CONTADOR       PCK_SUBTIPOS.TI_ENTERO;
    MI_RETECODIGO     VARCHAR2(5);
    MI_SALIDA         VARCHAR2(32000);
    MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
    --SE INICIALIZA PARA CUANDO NO APLICA ENVIAR LA MISMA ESTRUCTURA
    MI_SALIDA :=';'
             || ';'
             || ';'
             || ';'
             || ';'
             || ';'
             || ';'
             || ';'
             || ';'
             || ';';
    --NO SE GENERA RETENCION CUANDO EL TERCERO ES VARIOS
    IF UN_TERCERO = PCK_DATOS.FC_CONS_TERCERO THEN
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_RETETERCEVARIOS,
                                       UN_TABLAERROR => 'TERCERO' );
        END;
    END IF;
    --CONSULTA LOS DATOS DEL TERCERO PARA VER EN CUAL GRUPO SE CLASIFICA
    BEGIN
        SELECT REGIMEN, NATURALEZA, AUTORETENEDOR, CODIGOICA
        INTO MI_REGIMEN, MI_TIPOPERSONA, MI_AUTORRETENEDOR, MI_CIIU
        FROM TERCERO
        WHERE COMPANIA = UN_COMPANIA
          AND NIT      = UN_TERCERO
          AND SUCURSAL = UN_SUCURSAL;
    EXCEPTION WHEN NO_DATA_FOUND THEN
         BEGIN
            RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN 
            MI_REEMPLAZOS (1).CLAVE :='TERCERO';
            MI_REEMPLAZOS (1).VALOR :=UN_TERCERO;
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_RETETERCENULO,
                                       UN_TABLAERROR => 'TERCERO',
                                       UN_REEMPLAZOS => MI_REEMPLAZOS);
        END;
    END;    
    IF MI_AUTORRETENEDOR NOT IN(0) THEN
        RETURN MI_SALIDA || ';Autorretenedor;';
    END IF;
    --SE GENERA LA CONSULTA PARA VALIDAR QUE SOLO EXISTA UNA RETENCION VALIDA PARA EL DETALLE DE LA TRANSACCIÓN
    BEGIN
        SELECT CODIGO, COUNT(COMPANIA) CONTADOR
        INTO MI_RETECODIGO, MI_CONTADOR
        FROM RETENCIONES_REGIMEN
        WHERE COMPANIA          = UN_COMPANIA
          AND TIPO              = UN_TIPOFUENTE
          AND ANO               = UN_ANIO
          AND RETENCIONCONCEPTO = UN_CONCEPTO
          AND REGIMEN           = MI_REGIMEN
          AND (NATURALEZA       = MI_TIPOPERSONA
            OR NATURALEZA IS NULL)
        GROUP BY CODIGO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_REEMPLAZOS (1).CLAVE := 'TERCERO';
        MI_REEMPLAZOS (1).VALOR := UN_TERCERO;
        MI_REEMPLAZOS (2).CLAVE := 'TIPO';
        MI_REEMPLAZOS (2).VALOR := UN_TIPOFUENTE;
        MI_REEMPLAZOS (3).CLAVE := 'REGIMEN';
        MI_REEMPLAZOS (3).VALOR := MI_REGIMEN;
        MI_REEMPLAZOS (4).CLAVE := 'NATURALEZA';
        MI_REEMPLAZOS (4).VALOR := CASE WHEN MI_TIPOPERSONA='J' THEN 'JURIDICA' ELSE CASE WHEN MI_TIPOPERSONA='N' THEN 'NATURAL' ELSE 'DESCONOCIDO' END END;
        MI_SALIDA := MI_SALIDA || ';' || REPLACE(NVL(PCK_ERR_MSG.FC_MENSAJE(UN_MENSAJE_COD => PCK_ERRORES.ERR_RETESINCONFI,
                                                             UN_REEMPLAZOS => MI_REEMPLAZOS)
                                      , ' '),';',',') || ';';
       RETURN MI_SALIDA;
    END;   
    IF MI_CONTADOR<>1 THEN
        BEGIN
            RAISE PCK_EXCEPCIONES.EXC_TRANSAUTOMAT;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_TRANSAUTOMAT THEN 
            MI_REEMPLAZOS (1).CLAVE := 'TERCERO';
            MI_REEMPLAZOS (1).VALOR := UN_TERCERO;
            MI_REEMPLAZOS (2).CLAVE := 'TIPO';
            MI_REEMPLAZOS (2).VALOR := UN_TIPOFUENTE;            
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_RETESINCONFI1,
                                       UN_TABLAERROR => 'RETENCIONES_REGIMEN' );
        END;
    END IF;

    BEGIN
        IF UN_TIPOFUENTE = 'ICA' THEN
            SELECT RETENCIONES.COMPANIA
                    || ';' || RETENCIONES.TIPO
                    || ';' || RETENCIONES.ANO
                    || ';' || RETENCIONES.CODIGO
                    || ';' || RETENCIONES.CUENTA_DEBITO
                    || ';' || RETENCIONES.CUENTA_CREDITO
                    || ';' || RETENCIONES.PCT_BASE
                    || ';' || RETENCIONES.LIMITE_INF
                    || ';' || RETENCIONES.PCT_APLICAR 
                    || ';' || RETENCIONES.FACTORREDONDEO
                    || ';' || '' || MI_CIIU || ''
            INTO MI_SALIDA
            FROM RETENCIONES_CIIU INNER JOIN RETENCIONES
              ON RETENCIONES_CIIU.COMPANIA = RETENCIONES.COMPANIA
             AND RETENCIONES_CIIU.TIPO     = RETENCIONES.TIPO
             AND RETENCIONES_CIIU.ANO      = RETENCIONES.ANO
             AND RETENCIONES_CIIU.CODIGO   = RETENCIONES.CODIGO
            WHERE RETENCIONES_CIIU.COMPANIA = UN_COMPANIA
              AND RETENCIONES_CIIU.CIIU     = MI_CIIU
              AND RETENCIONES_CIIU.TIPO     = UN_TIPOFUENTE
              AND RETENCIONES_CIIU.ANO      = UN_ANIO;
        ELSE 
            SELECT COMPANIA
                    || ';' || TIPO
                    || ';' || ANO
                    || ';' || CODIGO
                    || ';' || CUENTA_DEBITO
                    || ';' || CUENTA_CREDITO
                    || ';' || PCT_BASE
                    || ';' || LIMITE_INF
                    || ';' || PCT_APLICAR 
                    || ';' || FACTORREDONDEO
                    || ';' 
            INTO MI_SALIDA
            FROM RETENCIONES
            WHERE COMPANIA          = UN_COMPANIA
              AND TIPO              = UN_TIPOFUENTE
              AND ANO               = UN_ANIO
              AND CODIGO            = MI_RETECODIGO;
        END IF;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        IF UN_TIPOFUENTE ='ICA' THEN
            RETURN MI_SALIDA || ';No se encontro configuración valida de Reteica para el tercero ' || UN_TERCERO || ';';
        ELSE
            RETURN MI_SALIDA || ';No se encontro configuración valida;';
        END IF;
    END;
    RETURN MI_SALIDA || ';OK;';
END FC_CODIGORETENCIONTERCERO;

FUNCTION FC_UPD_ESTADOCONSOLID
(   
/*
    NAME                    : PR_UPD_ESTADOCONSOLID    
    AUTHORS                 : STEFANINI SYSMAN SAS
    AUTHORS MIGRACION       : YESSICA SANA ROJAS
    DATE MIGRADOR           : 26/11/2018
    TIME                    : 11:30 AM
    SOURCE MODULE           : Contablidad
    MODIFIER                :
    DATA MODIFIED           :
    TIME                    :
    DESCRIPCION             : Permite actualizar el estado de las compañías consolidadas
    MODIFICATIONS           :
    @NAME                   : actualizarEstadoConsol
    @METHOD                 : PUT
  */
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO          IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MESINICIAL   IN  PCK_SUBTIPOS.TI_MES,
    UN_MESFINAL     IN  PCK_SUBTIPOS.TI_MES,
    UN_USUARIO      IN  PCK_SUBTIPOS.TI_USUARIO,
    UN_MODULO       IN  PCK_SUBTIPOS.TI_MODULO,
    UN_ESTADO       IN  DIA_BLOQUEO.ESTADO%TYPE
) RETURN NUMBER AS
    MI_CAMPOS       PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
    MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
    MI_RTA          PCK_SUBTIPOS.TI_LOGICO;
    MI_COMPAN       MI_COMPANIA;
    MI_CONTADOR     PCK_SUBTIPOS.TI_ENTERO;
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    MI_CONTADOR := 0;    
    MI_TABLA := 'CONSOLIDADA';

    FOR MI_RS IN (SELECT NITCOMPANIA
                    FROM CONSOLIDADA 
                   WHERE COMPANIACON = UN_COMPANIA ) 
    LOOP
        MI_CONTADOR := MI_CONTADOR + 1;
        FOR MI_MES IN UN_MESINICIAL .. UN_MESFINAL LOOP

            MI_RTA := PCK_SYSMAN_UTL.FC_CAMBIARESTADOMES (UN_COMPANIA => MI_RS.NITCOMPANIA,
                                                UN_ANO          => UN_ANO,
                                                UN_MES          => MI_MES,
                                                UN_MODULO       => UN_MODULO,
                                                UN_PROCESO      => '1',
                                                UN_ESTADO       => UN_ESTADO,
                                                UN_MODIFIED_BY  => UN_USUARIO
                                                );

        END LOOP;
    END LOOP;


    RETURN MI_CONTADOR;
END FC_UPD_ESTADOCONSOLID;

FUNCTION FC_DIFERENCIAS_POR_MES 
  (  
/*
      NAME             : FC_DIFERENCIAS_POR_MES
      AUTHORS          : SYSMAN SAS
      AUTHOR MIGRACION : BRAYAN SEBASTIAN CARDENAS AGUILAR
      DATE MIGRADOR    : 14/12/2018
      TIME             : 09:00 AM
      MODULO ORIGEN    : 
      MODIFIER         : 
      DATE MODIFIED    : 
      TIME             : 
      DESCRIPTION      : Función que calcula la diferencia entre valor debito y valor credito por mes
      MODIFICATIONS    : 


     @NAME: diferenciasPorMes  

    */
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA, 
    UN_ANO                 IN PCK_SUBTIPOS.TI_ANIO
  )
  RETURN CLOB
  AS
    MI_MENSAJE                CLOB:='';
    MI_ERRORES                PCK_SUBTIPOS.TI_ENTERO:=0;
    MI_RS                     SYS_REFCURSOR;

    BEGIN

    MI_MENSAJE:=MI_MENSAJE||'*** INFORME DE DIFERENCIAS POR CADA MES';
    MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
    MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
    FOR MI_RS IN( SELECT  1 ORDEN, COMPANIA.NOMBRE COMPANIA, 
                        TO_NUMBER(TO_CHAR(FECHA,'MM')) MES
                        ,TIPO_CPTE
                        ,COMPROBANTE
                        ,SUM(VALOR_DEBITO) DEBITO
                        ,SUM(VALOR_CREDITO) CREDITO
                        ,SUM(VALOR_DEBITO - VALOR_CREDITO) DIFERENCIA
                  FROM DETALLE_COMPROBANTE_CNT INNER JOIN COMPANIA 
                    ON DETALLE_COMPROBANTE_CNT.COMPANIA = COMPANIA.CODIGO
                  WHERE COMPANIA IN (SELECT NITCOMPANIA
                                     FROM   CONSOLIDADA 
                                     WHERE  COMPANIACON = UN_COMPANIA) 
                    AND ANO = UN_ANO
                  GROUP BY 1,
                           COMPANIA.NOMBRE, 
                           TO_NUMBER(TO_CHAR(FECHA,'MM')),
                           TIPO_CPTE,
                           COMPROBANTE
                  HAVING SUM(VALOR_DEBITO - VALOR_CREDITO)<>0
                  UNION ALL 
                  SELECT  -1 ORDEN, COMPANIA.NOMBRE COMPANIA, 
                         TO_NUMBER(TO_CHAR(FECHA,'MM')) MES
                        ,TIPO_CPTE
                        ,COMPROBANTE
                        ,SUM(VALOR_DEBITO) DEBITO
                        ,SUM(VALOR_CREDITO) CREDITO
                        ,SUM(VALOR_DEBITO - VALOR_CREDITO) DIFERENCIA
                  FROM DETALLE_COMPROBANTE_CNT INNER JOIN COMPANIA 
                    ON DETALLE_COMPROBANTE_CNT.COMPANIA = COMPANIA.CODIGO
                  WHERE COMPANIA IN (UN_COMPANIA) 
                    AND ANO      IN (UN_ANO)
                  GROUP BY -1,
                           COMPANIA.NOMBRE, 
                           TO_NUMBER(TO_CHAR(FECHA,'MM')),
                           TIPO_CPTE,
                           COMPROBANTE
                  HAVING SUM(VALOR_DEBITO - VALOR_CREDITO)<>0
                  ORDER BY MES,
                           ORDEN,
                           COMPANIA,
                           TIPO_CPTE,
                           COMPROBANTE)
      LOOP
        MI_MENSAJE:=MI_MENSAJE||'En el mes de '|| RPAD(PCK_SYSMAN_UTL.FC_NOMBRE_MES(MI_RS.MES),11,' ') 
                              ||' Para la compañia: '       || RPAD(MI_RS.COMPANIA,50,' ')      
                              ||' El tipo de Comprobante: ' || RPAD(MI_RS.TIPO_CPTE,5,' ')
                              ||' Comprobante: '            || RPAD(TO_CHAR(MI_RS.COMPROBANTE),15,' ')
                              ||' Tiene una diferencia de: '|| TO_CHAR (MI_RS.DIFERENCIA) ||CHR(10)||CHR(13);
        MI_ERRORES:=MI_ERRORES+1;
      END LOOP;
       IF MI_ERRORES>0 THEN

          MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
          MI_MENSAJE:=MI_MENSAJE||'Por favor revise las compañías a consolidar';
          MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
          MI_MENSAJE:=MI_MENSAJE||'Proceso terminado ';
          MI_MENSAJE:=MI_MENSAJE||CHR(10)||CHR(13);
          MI_MENSAJE:=MI_MENSAJE||'********************* FIN DEL INFORME *********************'||CHR(10)||CHR(13);
          RETURN MI_MENSAJE;
       END IF;

       MI_MENSAJE := 'TRUE';


      RETURN MI_MENSAJE;
    END FC_DIFERENCIAS_POR_MES;

PROCEDURE PR_ACTUALIZAR_FECHA_PAGO 
(
/*
    NAME                    : PR_ACTUALIZAR_FECHA_PAGO    
    AUTHORS                 : STEFANINI SYSMAN SAS
    AUTHORS MIGRACION       : YESIKA PAOLA BECERRA CASTRO 
    DATE MIGRADOR           : 14/12/2018
    TIME                    : 09:36 PM
    SOURCE MODULE           : Contablidad
    MODIFIER                :
    DATA MODIFIED           :
    TIME                    :
    DESCRIPCION             : Permite actualizar el los campos de FECHAPAGADOGN y IND_PAGADO de la tabla COMPROBANTE_CNT
    MODIFICATIONS           :
    @NAME                   : actualizarFechaPago
    @METHOD                 : PUT
  */
	UN_COMPANIA    		IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_ANO 			      IN PCK_SUBTIPOS.TI_ANIO,
	UN_MES 			      IN PCK_SUBTIPOS.TI_MES,
	UN_FECHA 		      IN DATE, 
	UN_USUARIO 		  	IN PCK_SUBTIPOS.TI_USUARIO,
	UN_SELECCION	  	IN PCK_SUBTIPOS.TI_LOGICO
)
	AS 
		MI_TABLA   		PCK_SUBTIPOS.TI_TABLA;
		MI_CAMPOS 		PCK_SUBTIPOS.TI_CAMPOS;
		MI_CONDICION 	PCK_SUBTIPOS.TI_CONDICION;

	BEGIN 
		<<FECHA_PAGADA>>
		FOR MI_RS_PAGADO IN (SELECT 	
							COMPROBANTE_CNT.COMPANIA,
							COMPROBANTE_CNT.ANO,
							COMPROBANTE_CNT.TIPO,
							COMPROBANTE_CNT.NUMERO,
							COMPROBANTE_CNT.FECHAPAGADOGN,
							COMPROBANTE_CNT.IND_PAGADO
						FROM COMPROBANTE_CNT
							INNER JOIN TIPO_COMPROBANTE
								ON COMPROBANTE_CNT.TIPO      = TIPO_COMPROBANTE.CODIGO
								AND COMPROBANTE_CNT.COMPANIA = TIPO_COMPROBANTE.COMPANIA
							INNER JOIN TERCERO
								ON COMPROBANTE_CNT.COMPANIA  = TERCERO.COMPANIA
								AND COMPROBANTE_CNT.TERCERO  = TERCERO.NIT
								AND COMPROBANTE_CNT.SUCURSAL = TERCERO.SUCURSAL
						WHERE COMPROBANTE_CNT.COMPANIA       = UN_COMPANIA
							AND COMPROBANTE_CNT.ANO          = UN_ANO
							AND COMPROBANTE_CNT.MES          = UN_MES
							AND TIPO_COMPROBANTE.CLASE_CONTABLE IN ('P')
						ORDER BY COMPROBANTE_CNT.TIPO,
							  COMPROBANTE_CNT.NUMERO)
		LOOP 
			BEGIN 
				MI_TABLA := 'COMPROBANTE_CNT';
				IF UN_SELECCION NOT IN(0) THEN 
					MI_CAMPOS := 'IND_PAGADO    = -1,
                        FECHAPAGADOGN = '''||UN_FECHA||''',
                        MODIFIED_BY   = '''||UN_USUARIO||''',
                        DATE_MODIFIED = SYSDATE';
				ELSE 
					MI_CAMPOS := 'IND_PAGADO    = 0,
                        FECHAPAGADOGN = NULL,
                        MODIFIED_BY   = '''||UN_USUARIO||''',
                        DATE_MODIFIED = SYSDATE';	
				END IF;
				MI_CONDICION := '     COMPANIA = '''||UN_COMPANIA      		||
                            	''' AND    ANO = '  ||MI_RS_PAGADO.ANO      ||
                            	'   AND   TIPO = '''||MI_RS_PAGADO.TIPO  	||
                            	''' AND NUMERO = '  ||MI_RS_PAGADO.NUMERO;		
                BEGIN 
                	PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                                    	UN_ACCION    => 'M',
                                                    	UN_CAMPOS    => MI_CAMPOS,
                                                    	UN_CONDICION => MI_CONDICION);      
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
             	END;                                    	       
         	EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
         	 	PCK_ERR_MSG.RAISE_WITH_MSG(	UN_EXC_COD    => SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_ACTPAGADO);       
   			END;
		END LOOP;	
	END PR_ACTUALIZAR_FECHA_PAGO;	



FUNCTION FC_GENERAR_ARCHIVOVERDE
 /*
      NAME 			       : FC_GENERACIONARCHIVOVERDE
      AUTHORS 			   : SYSMAN SAS
      AUTHOR MIGRACION : JULIO CESAR REINA PANCHE
      DATE MIGRADOR	   : 10/01/2019
      TIME				     : 02:00 PM
      MODULO ORIGEN	   : 
      DESCRIPTION		   : FUNCION QUE RETORNA LOS DATOS PARA LA PLANTILLA DEL ARCHIVO VERDE

      MODIFIER			   : 
      DATE MODIFIED	   : 
      TIME				     : 
      MODIFICATIONS	   : 

      @NAME:   generarArchivoVerde
      @METHOD: GET
    */
(
  UN_COMPANIA               IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO                    IN PCK_SUBTIPOS.TI_ANIO,
  UN_MESINICIAL             IN PCK_SUBTIPOS.TI_MES,
  UN_MESFINAL               IN PCK_SUBTIPOS.TI_MES
)
RETURN CLOB
AS
  MI_DEBITOANT                PCK_SUBTIPOS.TI_VALORES;
  MI_CREDITOANT               PCK_SUBTIPOS.TI_VALORES;
  MI_DEBITOPERIODO            PCK_SUBTIPOS.TI_VALORES;
  MI_CREDITOPERIODO           PCK_SUBTIPOS.TI_VALORES;
  MI_STRSQL                   PCK_SUBTIPOS.TI_CONSULTA;
  MI_RETORNO                  CLOB;
  MI_RS                       SYS_REFCURSOR;
  MI_CODIGO                   PLAN_CONTABLE.CODIGO%TYPE;
  MI_NOMBRE                   PLAN_CONTABLE.NOMBRE%TYPE;
  MI_SALDOIDEBITO             PCK_SUBTIPOS.TI_DOBLE;
  MI_SALDOICREDITO            PCK_SUBTIPOS.TI_DOBLE;  
  MI_PERIODODEBITO            PCK_SUBTIPOS.TI_DOBLE;  
  MI_PERIODOCREDITO           PCK_SUBTIPOS.TI_DOBLE; 
  MI_SALDODEBITO              PCK_SUBTIPOS.TI_DOBLE;    
  MI_SALDOCREDITO             PCK_SUBTIPOS.TI_DOBLE;

BEGIN
  FOR I IN 0..UN_MESINICIAL-1 
  LOOP
    MI_DEBITOANT  := MI_DEBITOANT || 'DEBITO' || I || '+';
    MI_CREDITOANT := MI_CREDITOANT || 'CREDITO' || I || '+';
  END LOOP;


  FOR I IN UN_MESINICIAL..UN_MESFINAL 
  LOOP
    MI_DEBITOPERIODO  := MI_DEBITOPERIODO || 'DEBITO' || I || '+';
    MI_CREDITOPERIODO := MI_CREDITOPERIODO || 'CREDITO' || I || '+';
  END LOOP;

  MI_DEBITOANT := SUBSTR(MI_DEBITOANT,0, LENGTH(MI_DEBITOANT)-1);
  MI_CREDITOANT := SUBSTR(MI_CREDITOANT,0, LENGTH(MI_CREDITOANT)-1);
  MI_DEBITOPERIODO := SUBSTR(MI_DEBITOPERIODO,0, LENGTH(MI_DEBITOPERIODO)-1);
  MI_CREDITOPERIODO := SUBSTR(MI_CREDITOPERIODO,0, LENGTH(MI_CREDITOPERIODO)-1);


   --'|| MI_DEBITOANT       ||'  AS DEBITOANT,
   --'|| MI_CREDITOANT      ||'  AS CREDITOANT,
   /*CASE WHEN NATURALEZA=''D'' 
                             THEN  CASE WHEN SALDO0>0 
                                        THEN SALDO0 
                                        ELSE 0 END ELSE CASE WHEN SALDO0<0 
                                                             THEN SALDO0*-1 ELSE 0 END END  SALDOIDEBITO, 
                        CASE WHEN NATURALEZA=''C'' 
                             THEN  CASE WHEN SALDO0>0 
                                        THEN SALDO0 
                                        ELSE 0 END ELSE CASE WHEN SALDO0<0 
                                                             THEN SALDO0*-1 
                                                             ELSE 0 END END  SALDOICREDITO,*/
  MI_STRSQL := ' SELECT PLAN_CONTABLE.CODIGO, 
                        PLAN_CONTABLE.NOMBRE, 
                        '|| MI_DEBITOANT       ||'  AS DEBITOANT,
                        '|| MI_CREDITOANT      ||'  AS CREDITOANT,
                        '|| MI_DEBITOPERIODO   ||'  AS DEBITOPERIODO,
                        '|| MI_CREDITOPERIODO  ||'  AS CREDITOPERIODO,
                        CASE WHEN  ('||MI_DEBITOANT||') +('||MI_DEBITOPERIODO||')-('||MI_CREDITOANT||')-('||MI_CREDITOPERIODO||')>0 
                             THEN ('||MI_DEBITOANT||')+('||MI_DEBITOPERIODO||')-('||MI_CREDITOANT||')-('||MI_CREDITOPERIODO||') 
                             ELSE 0 END AS SALDODEBITO,
                        CASE WHEN ('||MI_CREDITOANT||')+('||MI_CREDITOPERIODO||')-('||MI_DEBITOANT||')-('||MI_DEBITOPERIODO||') >0 
                             THEN ('||MI_CREDITOANT||')+('||MI_CREDITOPERIODO||')-('||MI_DEBITOANT||')-('||MI_DEBITOPERIODO||') 
                             ELSE 0 END  AS SALDOCREDITO
                FROM PLAN_CONTABLE
                WHERE PLAN_CONTABLE.COMPANIA = '''|| UN_COMPANIA || ''' 
                AND PLAN_CONTABLE.ANO=' || UN_ANO ||'
                AND LENGTH(CODIGO)<=6';


    OPEN MI_RS FOR MI_STRSQL;
    LOOP
    FETCH MI_RS INTO MI_CODIGO,MI_NOMBRE,
                     MI_SALDOIDEBITO, MI_SALDOICREDITO,
                     MI_PERIODODEBITO, MI_PERIODOCREDITO,
                     MI_SALDODEBITO, MI_SALDOCREDITO;
    EXIT WHEN MI_RS%NOTFOUND;

      MI_RETORNO := MI_RETORNO        || TO_CLOB(   
                    MI_CODIGO         || PCK_DATOS.GL_SEPARADOR_COL ||
                    MI_NOMBRE         || PCK_DATOS.GL_SEPARADOR_COL ||
                    MI_SALDOIDEBITO   || PCK_DATOS.GL_SEPARADOR_COL || 
                    MI_SALDOICREDITO  || PCK_DATOS.GL_SEPARADOR_COL ||
                    MI_PERIODODEBITO  || PCK_DATOS.GL_SEPARADOR_COL ||
                    MI_PERIODOCREDITO || PCK_DATOS.GL_SEPARADOR_COL ||
                    MI_SALDODEBITO    || PCK_DATOS.GL_SEPARADOR_COL ||
                    MI_SALDOCREDITO   || PCK_DATOS.GL_SEPARADOR_REG);

    END LOOP;
    CLOSE MI_RS;

  RETURN MI_RETORNO;
END FC_GENERAR_ARCHIVOVERDE;

PROCEDURE PR_CUENTASEQUIVALENTES
(
  /*
      NAME                  : PR_CUENTASEQUIVALENTES
      AUTHORS               : SYSMAN SAS
      AUTHOR MIGRACION      : YESIKA PAOLA BECERRA CASTRO 
      DATE MIGRADOR         : 05/02/2019
      TIME                  : 12:15 PM
      MODULO ORIGEN         : 
      DESCRIPTION           : Procedimiento que valida la equivalencia de las cuentas contables 
                              con las presupuestales al momento de realizar comprobante de egreso
      MODIFIER              : 
      DATE MODIFIED         : 
      TIME                  : 
      MODIFICATIONS         : 

      @NAME:   validarCuentasEquivalentes
      @METHOD: GET
    */
    UN_COMPANIA      IN  	PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO          IN  	PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO_CPTE     IN  	PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_COMPROBANTE   IN   PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT
)
AS 
  MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
  MI_CUENTA       PCK_SUBTIPOS.TI_CODIGOCONTA;
  MI_RUBROCON     VARCHAR2(500 CHAR);
  MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN 
    --Se hallan las cuentas contables del comprobante recibido por parametro 
    <<CUENTAS_PPTALES>>
    FOR RS_CUENTAS_PPTALES IN (   
        SELECT DETALLE_COMPROBANTE_CNT.CUENTA 
				FROM DETALLE_COMPROBANTE_CNT 
					INNER JOIN PLAN_CONTABLE 
						ON DETALLE_COMPROBANTE_CNT.COMPANIA   = PLAN_CONTABLE.COMPANIA 
						AND DETALLE_COMPROBANTE_CNT.ANO       = PLAN_CONTABLE.ANO
						AND DETALLE_COMPROBANTE_CNT.CUENTA    = PLAN_CONTABLE.CODIGO
					INNER JOIN CLASECUENTA
						ON PLAN_CONTABLE.CLASECUENTA = CLASECUENTA.CODIGO
				WHERE DETALLE_COMPROBANTE_CNT.COMPANIA 		  = UN_COMPANIA
					AND DETALLE_COMPROBANTE_CNT.ANO 			    = UN_ANIO
					AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE 		= UN_TIPO_CPTE
					AND DETALLE_COMPROBANTE_CNT.COMPROBANTE 	= UN_COMPROBANTE
					AND CLASECUENTA.CODIGO NOT IN('B'))
    LOOP
      MI_STRSQL := 'SELECT LISTAGG(RUBRO,'','') WITHIN GROUP (ORDER BY COMPANIA,ANO,CUENTA_CONTABLE)RUBRO
              			FROM PLAN_PPTAL_CUENTACNT
                    WHERE COMPANIA 			= '''||UN_COMPANIA||'''
                      AND ANO 			= '||UN_ANIO||'
                      AND CUENTA_CONTABLE = '||RS_CUENTAS_PPTALES.CUENTA;
      EXECUTE IMMEDIATE MI_STRSQL INTO MI_RUBROCON;

      IF MI_RUBROCON IS NOT NULL THEN 
        BEGIN 
          MI_STRSQL := 'SELECT LISTAGG(CUENTA_CONTABLE,'','')WITHIN GROUP(ORDER BY COMPANIA,ANO,RUBRO_PPTAL) CUENTA_CONTABLE
                        FROM PLAN_PPTAL_CTABANCARIA 
                        WHERE COMPANIA           = '''||UN_COMPANIA||'''
                          AND ANO                =   '||UN_ANIO||'
                          AND RUBRO_PPTAL       IN('||MI_RUBROCON||')';
          BEGIN 
            EXECUTE IMMEDIATE MI_STRSQL INTO MI_CUENTA;
            EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END; 
          IF MI_CUENTA IS NULL THEN
             RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
            MI_REEMPLAZOS(1).CLAVE := 'CUENTA_CONTABLE';
            MI_REEMPLAZOS(1).VALOR := RS_CUENTAS_PPTALES.CUENTA;
            MI_REEMPLAZOS(2).CLAVE := 'RUBRO';
            MI_REEMPLAZOS(2).VALOR := MI_RUBROCON;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                       UN_EXC_COD =>SQLCODE,
                       UN_ERROR_COD=>PCK_ERRORES.ERR_CONTAB_CUENTAEQUI,
                       UN_REEMPLAZOS => MI_REEMPLAZOS);  
        END;
        BEGIN 
          MI_STRSQL := 'SELECT LISTAGG(DETALLE_COMPROBANTE_CNT.CUENTA,'','') WITHIN GROUP (ORDER BY DETALLE_COMPROBANTE_CNT.COMPANIA,DETALLE_COMPROBANTE_CNT.ANO,DETALLE_COMPROBANTE_CNT.TIPO_CPTE,DETALLE_COMPROBANTE_CNT.COMPROBANTE)CUENTA 
                        FROM DETALLE_COMPROBANTE_CNT 
                          INNER JOIN PLAN_CONTABLE 
                            ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA 
                            AND DETALLE_COMPROBANTE_CNT.ANO     = PLAN_CONTABLE.ANO
                            AND DETALLE_COMPROBANTE_CNT.CUENTA  = PLAN_CONTABLE.CODIGO
                          INNER JOIN CLASECUENTA
                            ON PLAN_CONTABLE.CLASECUENTA = CLASECUENTA.CODIGO
                        WHERE DETALLE_COMPROBANTE_CNT.COMPANIA 	    = '''||UN_COMPANIA||'''
                          AND DETALLE_COMPROBANTE_CNT.ANO 			    =   '||UN_ANIO||'
                          AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE 		= '''||UN_TIPO_CPTE||'''
                          AND DETALLE_COMPROBANTE_CNT.COMPROBANTE 	=   '||UN_COMPROBANTE||'
                          AND DETALLE_COMPROBANTE_CNT.CUENTA IN ( '||MI_CUENTA||')
                          AND CLASECUENTA.CODIGO IN(''B'')';
          BEGIN 
            EXECUTE IMMEDIATE MI_STRSQL INTO MI_CUENTA;
            EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END;
          IF MI_CUENTA IS NULL THEN 
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END IF;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
            MI_REEMPLAZOS(1).CLAVE := 'CUENTA_CONTABLE';
            MI_REEMPLAZOS(1).VALOR := RS_CUENTAS_PPTALES.CUENTA;
            MI_REEMPLAZOS(2).CLAVE := 'RUBRO';
            MI_REEMPLAZOS(2).VALOR := MI_RUBROCON;
            MI_REEMPLAZOS(3).CLAVE := 'COMPROBANTE';
            MI_REEMPLAZOS(3).VALOR := UN_COMPROBANTE;
                       PCK_ERR_MSG.RAISE_WITH_MSG(
                       UN_EXC_COD =>SQLCODE,
                       UN_ERROR_COD=>PCK_ERRORES.ERR_CONTA_CTABAN,
                       UN_REEMPLAZOS => MI_REEMPLAZOS);  
        END;
      END IF;
    END LOOP ; 
END PR_CUENTASEQUIVALENTES;			
FUNCTION FC_CUENTASBANCEGR
(
   /*
      NAME                  : FC_CUENTASBANCEGR
      AUTHORS               : SYSMAN SAS
      AUTHOR MIGRACION      : YESIKA PAOLA BECERRA CASTRO 
      DATE MIGRADOR         : 14/02/2019
      TIME                  : 03:42 PM
      MODULO ORIGEN         : 
      DESCRIPTION           : Procedimiento que valida la equivalencia de las cuentas contables 
                              con las presupuestales al momento de realizar comprobante de egreso
      MODIFIER              : 
      DATE MODIFIED         : 
      TIME                  : 
      MODIFICATIONS         : 

      @NAME:   validarCuentasEquivalentesEgr
      @METHOD: GET
    */
    UN_COMPANIA             IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO                  IN  PCK_SUBTIPOS.TI_ANIO,
    UN_LISTANUMEROAFECTAR   IN  PCK_SUBTIPOS.TI_STRSQL
)
RETURN CLOB
    AS 
        MI_MENSAJE      CLOB := '';
        MI_CUENTA       PCK_SUBTIPOS.TI_STRSQL;
        MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
        MI_CTA_CONT     VARCHAR2(500 CHAR);
        MI_RUBRO_PPTAL  PCK_SUBTIPOS.TI_CODIGOPPTAL;
        RSRUBRO         SYS_REFCURSOR;
    BEGIN 
        MI_MENSAJE := '*Los siguientes rubros presupuestales no tienen cuenta bancaria equivalente*';
        --Se hallan las cuentas presupuestales para hallar las cuentas bancarias relacionadas
        MI_STRSQL := '  SELECT DISTINCT CUENTA
                        FROM DETALLE_COMPROBANTE_PPTAL
                        WHERE COMPANIA = '''||UN_COMPANIA||'''
                            AND (ANO,TIPO_CPTE,COMPROBANTE) IN ('||UN_LISTANUMEROAFECTAR||')';
        OPEN RSRUBRO FOR MI_STRSQL;
        LOOP 
        FETCH RSRUBRO INTO MI_RUBRO_PPTAL;
        EXIT WHEN RSRUBRO%NOTFOUND;
        BEGIN 
            MI_STRSQL := '  SELECT LISTAGG(CUENTA_CONTABLE,'','') WITHIN GROUP (ORDER BY COMPANIA,ANO,RUBRO_PPTAL)
                            FROM PLAN_PPTAL_CTABANCARIA
                            WHERE COMPANIA = '''||UN_COMPANIA||'''
                                AND ANO = '||UN_ANO||'
                                AND RUBRO_PPTAL = '''||MI_RUBRO_PPTAL||'''';
            EXECUTE IMMEDIATE MI_STRSQL INTO MI_CTA_CONT;
          IF MI_CTA_CONT IS NULL THEN 
            MI_CUENTA := MI_CUENTA || CHR(10)||CHR(13)||MI_RUBRO_PPTAL;
            CONTINUE;
          END IF;
        END;

      END LOOP;
      MI_MENSAJE := MI_MENSAJE || MI_CUENTA;
    RETURN MI_MENSAJE ;
END FC_CUENTASBANCEGR;

FUNCTION FC_CARTERA_CUENTA 
/*
      NAME                  : FC_CARTERA_CUENTA
      AUTHORS               : 
      AUTHOR MIGRACION      : BRAYAN SEBASTIAN CARDENAS AGUILAR 
      DATE MIGRADOR         : 16/04/2019
      TIME                  : 02:50 PM
      MODULO ORIGEN         : 
      DESCRIPTION           : Función que valida solo agregar cuentas contables del mismo modulo 
      MODIFIER              : 
      DATE MODIFIED         : 
      TIME                  : 
      MODIFICATIONS         : 

      @NAME:   carteraCuenta
      @METHOD: GET
    */
 (
	UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_MODULO     IN CARTERA_CUENTA.MODULO%TYPE,
	UN_CUENTA     IN CARTERA_CUENTA.CUENTA%TYPE
  )
RETURN VARCHAR2
  AS
    MI_RTA        VARCHAR2(200 CHAR);
    MI_EXISTE     PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_CUENTA     CARTERA_CUENTA.CUENTA%TYPE;
    MI_MODULO     CARTERA_CUENTA.MODULO%TYPE;
    MI_NOMBRE     APLICACIONES.NOMBRE%TYPE;
BEGIN

            SELECT   COUNT(1) EXISTE
            INTO MI_EXISTE
              FROM CARTERA_CUENTA C
              INNER JOIN APLICACIONES A
              ON C.MODULO = A.APLICACION
              WHERE COMPANIA = UN_COMPANIA
              AND CUENTA = UN_CUENTA;
    IF MI_EXISTE NOT IN (0)THEN

    SELECT DISTINCT C.CUENTA,
                     C.MODULO,
                     A.NOMBRE
              INTO MI_CUENTA,
              MI_MODULO,
              MI_NOMBRE
              FROM CARTERA_CUENTA C
              INNER JOIN APLICACIONES A
              ON C.MODULO = A.APLICACION
              WHERE COMPANIA = UN_COMPANIA
              AND CUENTA = UN_CUENTA;
              IF UN_CUENTA = MI_CUENTA  AND UN_MODULO != MI_MODULO THEN


     MI_RTA := 'La cuenta N° '|| MI_CUENTA ||' ya se encuentra en el modulo de '|| MI_NOMBRE ; 
     ELSE 
      MI_RTA := 'FALSE';
            END IF;
      ELSE
           MI_RTA := 'FALSE';
    END IF;




  RETURN MI_RTA;
END FC_CARTERA_CUENTA;

PROCEDURE PR_CARGAR_PLAN_CONTABLE
/*
    NAME              : PR_CARGAR_PLAN_CONTABLE
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 26/04/2019                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PROCEDIMIENTO QUE CARGA LOS DATOS DE PLANES CONTABLES O SALDOS INICIALES DESDE UN EXCEL

    @NAME:    cargarPlanContable
    @METHOD:  POST
    */
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENAPLAN  IN CLOB,
  UN_OPCION      IN PCK_SUBTIPOS.TI_ENTERO,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)  
AS 
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_FILA               PCK_SUBTIPOS.TI_ENTERO := 0;
MI_CON                PCK_SUBTIPOS.TI_ENTERO := 0;
MI_RETORNO            CLOB := '';
MI_CUENTA             PLAN_CONTABLE.CODIGO%TYPE;
MI_MOVIMIENTO         PLAN_CONTABLE.MOVIMIENTO%TYPE;
MI_MAN_CEN_CTO        PLAN_CONTABLE.MAN_CEN_CTO%TYPE;
MI_MAN_AUX_TER        PLAN_CONTABLE.MAN_AUX_TER%TYPE;
MI_MAN_AUX_GEN        PLAN_CONTABLE.MAN_AUX_GEN%TYPE;
MI_MAN_AUX_REF        PLAN_CONTABLE.MAN_AUX_REF%TYPE;
MI_MAN_AUX_FUE        PLAN_CONTABLE.MAN_AUX_FUE%TYPE;
MI_NATURALEZA         PLAN_CONTABLE.NATURALEZA%TYPE;
BEGIN

  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAPLAN,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

  <<CREAR_PLAN_CONTABLE>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
    MI_FILA := MI_FILA + 1;
    IF UN_OPCION = 1 THEN
    
      MI_CUENTA := TRIM(MI_DATOS_COLUMNAS(3));
      MI_NATURALEZA := TRIM(MI_DATOS_COLUMNAS(5));
      MI_MOVIMIENTO := MI_DATOS_COLUMNAS(7);
      MI_MAN_CEN_CTO := MI_DATOS_COLUMNAS(8);
      MI_MAN_AUX_TER := MI_DATOS_COLUMNAS(9);
      MI_MAN_AUX_GEN := MI_DATOS_COLUMNAS(10);
      MI_MAN_AUX_REF := MI_DATOS_COLUMNAS(11);
      MI_MAN_AUX_FUE := MI_DATOS_COLUMNAS(12);
      
      IF MI_MOVIMIENTO NOT IN(0) AND (MI_MAN_CEN_CTO + MI_MAN_AUX_TER + MI_MAN_AUX_GEN + MI_MAN_AUX_REF + MI_MAN_AUX_FUE) NOT IN(0) THEN
            BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_RETORNO := 'En la fila ' || MI_FILA || ' con la cuenta  ' || MI_DATOS_COLUMNAS(3) || '
                                    No se permite tener movimiento y auxiliares';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'PLAN_CONTABLE',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
      END IF;
      
      IF LENGTH(MI_CUENTA) < 6 AND (MI_MOVIMIENTO + MI_MAN_CEN_CTO + MI_MAN_AUX_TER
              + MI_MAN_AUX_GEN + MI_MAN_AUX_REF + MI_MAN_AUX_FUE) NOT IN(0)
      THEN
        BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_RETORNO := 'En la fila ' || MI_FILA || ' con la cuenta  ' || MI_CUENTA || '
                                    No se permite tener movimiento o auxiliares a cuentas con menos de 6 digitos';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'PLAN_CONTABLE',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
      END IF;
      
      BEGIN
          --que no permita ingresar hijas si hay padres con indicadores
          MI_CON:=0;
          SELECT COUNT(CODIGO)
          INTO MI_CON
          FROM PLAN_CONTABLE
          WHERE COMPANIA = MI_DATOS_COLUMNAS(1)
            AND ANO      = MI_DATOS_COLUMNAS(2)
            AND CODIGO   <> MI_CUENTA
            AND CODIGO   BETWEEN SUBSTR(MI_CUENTA,1,1) AND MI_CUENTA
            AND CODIGO   = SUBSTR(MI_CUENTA,1,LENGTH(CODIGO))
            AND (MOVIMIENTO + MAN_CEN_CTO + MAN_AUX_TER + MAN_AUX_GEN + MAN_AUX_REF + MAN_AUX_FUE) NOT IN(0);
          IF MI_CON NOT IN (0) THEN
            BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_RETORNO := 'En la fila ' || MI_FILA || ' la cuenta  ' || MI_CUENTA || '
                                    No se permite tener movimiento o auxiliares teniendo cuentas a nivel superior con indicadores';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'PLAN_CONTABLE',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
          END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
          PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE
                     ,UN_TABLAERROR => 'PLAN_CONTABLE'
                     ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTABILIDAD_PLANCNT_IND_7
               );
        END;
        
        BEGIN
          MI_CON:=0;
          SELECT COUNT(CODIGO)
          INTO MI_CON
          FROM CLASECUENTA
          WHERE CODIGO   = MI_DATOS_COLUMNAS(6);
          IF MI_CON IN (0) THEN
            BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_RETORNO := 'En la fila ' || MI_FILA || ' la cuenta ' || MI_DATOS_COLUMNAS(3) || ' la clase cuenta configurada no existe.';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'PLAN_CONTABLE',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
          END IF;
        END;
        
        BEGIN
          
          IF MI_NATURALEZA NOT IN ('D','C') THEN
            BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_RETORNO := 'En la fila ' || MI_FILA || ' la cuenta ' || MI_DATOS_COLUMNAS(3) || ' la naturaleza ' || MI_NATURALEZA || ' no corresponde.';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'PLAN_CONTABLE',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
          END IF;
        END;

      MI_CAMPOS := 'COMPANIA
                  ,ANO
                  ,CODIGO
                  ,NOMBRE
                  ,NATURALEZA
                  ,CLASECUENTA
                  ,MOVIMIENTO
                  ,MAN_CEN_CTO
                  ,MAN_AUX_TER
                  ,MAN_AUX_GEN                  
                  ,MAN_AUX_FUE
                  ,MAN_AUX_REF
                  ,CREATED_BY
                  ,DATE_CREATED';

      MI_VALORES := ''''|| MI_DATOS_COLUMNAS(1) ||'''
                    ,'|| MI_DATOS_COLUMNAS(2) ||'
                  ,'''|| MI_CUENTA            ||'''
                  ,'''|| MI_DATOS_COLUMNAS(4) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(5) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(6) ||'''
                  ,  '|| MI_DATOS_COLUMNAS(7) ||'
                  ,  '|| MI_DATOS_COLUMNAS(8) ||'
                  ,  '|| MI_DATOS_COLUMNAS(9) ||'
                  ,  '|| MI_DATOS_COLUMNAS(10) ||'
                  ,  '|| MI_DATOS_COLUMNAS(11) ||'
                  ,  '|| MI_DATOS_COLUMNAS(12) ||'
                  ,'''|| UN_USUARIO ||'''
                  ,SYSDATE';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'PLAN_CONTABLE'
                                               ,UN_ACCION  => 'I'
                                               ,UN_CAMPOS  => MI_CAMPOS
                                               ,UN_VALORES => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
           RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
              MI_MSGERROR(1).CLAVE := 'CUENTA';
              MI_MSGERROR(1).VALOR :=  MI_DATOS_COLUMNAS(3);
              MI_MSGERROR(2).CLAVE := 'ANIO';
              MI_MSGERROR(2).VALOR :=  MI_DATOS_COLUMNAS(2);
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_CONT_INS_PLAN_CON,
              UN_REEMPLAZOS => MI_MSGERROR
               );
    END;          

    ELSIF UN_OPCION = 2 THEN
      
      BEGIN
          MI_CON:=0;
          SELECT COUNT(CODIGO)
          INTO MI_CON
          FROM PLAN_CONTABLE
          WHERE COMPANIA = MI_DATOS_COLUMNAS(1)
            AND ANO      = MI_DATOS_COLUMNAS(2)
            AND CODIGO   = MI_DATOS_COLUMNAS(3);
          IF MI_CON IN (0) THEN
            BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_RETORNO := 'En la fila ' || MI_FILA || ' la cuenta ' || MI_DATOS_COLUMNAS(3) || ' No esta creada en el plan contable';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'SALDOSINICIALES',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
          END IF;
        END;
        
      BEGIN
          MI_CON:=0;
          SELECT COUNT(NIT)
          INTO MI_CON
          FROM TERCERO
          WHERE COMPANIA = MI_DATOS_COLUMNAS(1)
            AND NIT      = MI_DATOS_COLUMNAS(4)
            AND SUCURSAL = MI_DATOS_COLUMNAS(5);
          IF MI_CON IN (0) THEN
            BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_RETORNO := 'En la fila ' || MI_FILA || ' con la cuenta  ' || MI_DATOS_COLUMNAS(3) || ' El tercero ' || MI_DATOS_COLUMNAS(4) || ' no se encuentra creado.';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'SALDOSINICIALES',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
          END IF;
        END;
    
        BEGIN
             MI_CON:=0;
             SELECT COUNT(CODIGO)
                INTO MI_CON
            FROM CENTRO_COSTO
            WHERE COMPANIA  = MI_DATOS_COLUMNAS(1)
            AND ANO         = MI_DATOS_COLUMNAS(2)
            AND CODIGO      = MI_DATOS_COLUMNAS(7)
            AND MOVIMIENTO  = -1;
            IF MI_CON IN (0) THEN
               BEGIN
                   RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                   MI_RETORNO := 'En la fila ' || MI_FILA || ' con la cuenta  ' || MI_DATOS_COLUMNAS(3) || ' El centro de costo ' || MI_DATOS_COLUMNAS(7) || ' no existe o no tiene movimiento.';
                   MI_MSGERROR (1).CLAVE := 'MENSAJE';
                   MI_MSGERROR (1).VALOR := MI_RETORNO;
                   PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                              UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                              UN_TABLAERROR => 'CENTRO_COSTO',
                                              UN_REEMPLAZOS => MI_MSGERROR);
                END;
             END IF;
        END;
        
        BEGIN
             MI_CON:=0;
             SELECT COUNT(CODIGO)
                INTO MI_CON
            FROM FUENTE_RECURSOS
            WHERE COMPANIA  = MI_DATOS_COLUMNAS(1)
            AND ANO         = MI_DATOS_COLUMNAS(2)
            AND CODIGO      = MI_DATOS_COLUMNAS(8);
            IF MI_CON IN (0) THEN
               BEGIN
                   RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                   MI_RETORNO := 'En la fila ' || MI_FILA || ' con la cuenta  ' || MI_DATOS_COLUMNAS(3) || ' la fuente de recursos ' || MI_DATOS_COLUMNAS(8) || ' no existe.';
                   MI_MSGERROR (1).CLAVE := 'MENSAJE';
                   MI_MSGERROR (1).VALOR := MI_RETORNO;
                   PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                              UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                              UN_TABLAERROR => 'FUENTE_RECURSOS',
                                              UN_REEMPLAZOS => MI_MSGERROR);
                END;
             END IF;
        END; 
        
      BEGIN
             MI_CON:=0;
             SELECT COUNT(CODIGO)
                INTO MI_CON
            FROM AUXILIAR
            WHERE COMPANIA  = MI_DATOS_COLUMNAS(1)
            AND ANO         = MI_DATOS_COLUMNAS(2)
            AND CODIGO      = MI_DATOS_COLUMNAS(6)
            AND MOVIMIENTO  = -1;
            IF MI_CON IN (0) THEN
               BEGIN
                   RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                   MI_RETORNO := 'En la fila ' || MI_FILA || ' con la cuenta  ' || MI_DATOS_COLUMNAS(3) || ' el auxiliar ' || MI_DATOS_COLUMNAS(6) || ' no existe o no tiene movimiento';
                   MI_MSGERROR (1).CLAVE := 'MENSAJE';
                   MI_MSGERROR (1).VALOR := MI_RETORNO;
                   PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                              UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                              UN_TABLAERROR => 'AUXILIAR',
                                              UN_REEMPLAZOS => MI_MSGERROR);
                END;
             END IF;
        END;  
            
      BEGIN
             MI_CON:=0;
             SELECT COUNT(CODIGO)
                INTO MI_CON
            FROM REFERENCIA
            WHERE COMPANIA  = MI_DATOS_COLUMNAS(1)
            AND ANO         = MI_DATOS_COLUMNAS(2)
            AND CODIGO      = MI_DATOS_COLUMNAS(9)
            AND MOVIMIENTO  = -1;
            IF MI_CON IN (0) THEN
               BEGIN
                   RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                   MI_RETORNO := 'En la fila ' || MI_FILA || ' con la cuenta  ' || MI_DATOS_COLUMNAS(3) || ' la referencia ' || MI_DATOS_COLUMNAS(9) || ' no existe o no tiene movimiento';
                   MI_MSGERROR (1).CLAVE := 'MENSAJE';
                   MI_MSGERROR (1).VALOR := MI_RETORNO;
                   PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                              UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                              UN_TABLAERROR => 'REFERENCIA',
                                              UN_REEMPLAZOS => MI_MSGERROR);
                END;
             END IF;
        END;  
      
      MI_CAMPOS := 'COMPANIA
                   ,ANO
                   ,CODIGO
                   ,TERCERO
                   ,SUCURSAL
                   ,AUXILIAR
                   ,CENTRO_COSTO                   
                   ,FUENTE_RECURSO
                   ,REFERENCIA
                   ,SALDOINICIAL
                   ,DEBITO
                   ,CREDITO                   
                   ,CREATED_BY
                   ,DATE_CREATED';

      MI_VALORES := ''''|| MI_DATOS_COLUMNAS(1) ||'''
                    ,  '|| MI_DATOS_COLUMNAS(2) ||'
                    ,'''|| MI_DATOS_COLUMNAS(3) ||'''
                    ,'''|| MI_DATOS_COLUMNAS(4) ||'''
                    ,'''|| MI_DATOS_COLUMNAS(5) ||'''
                    ,'''|| MI_DATOS_COLUMNAS(6) ||'''
                    ,'''|| MI_DATOS_COLUMNAS(7) ||'''
                    ,'''|| MI_DATOS_COLUMNAS(8) ||'''
                    ,'''|| MI_DATOS_COLUMNAS(9) ||'''
                    ,'||MI_DATOS_COLUMNAS(10)|| '
                    ,'||MI_DATOS_COLUMNAS(11)|| '
                    ,'||MI_DATOS_COLUMNAS(12)|| '
                    ,'''|| UN_USUARIO ||'''
                    ,SYSDATE';
    BEGIN
      BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'SALDOSINICIALES'
                                               ,UN_ACCION  => 'I'
                                               ,UN_CAMPOS  => MI_CAMPOS
                                               ,UN_VALORES => MI_VALORES);

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
           RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
              MI_MSGERROR(1).CLAVE := 'CUENTA';
              MI_MSGERROR(1).VALOR :=  MI_DATOS_COLUMNAS(3);
              MI_MSGERROR(2).CLAVE := 'ANIO';
              MI_MSGERROR(2).VALOR :=  MI_DATOS_COLUMNAS(2);
              PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_INS_SALDO_INICIAL,
              UN_REEMPLAZOS => MI_MSGERROR
               );
    END;

    END IF;


  END LOOP CREAR_PLAN_CONTABLE;
END PR_CARGAR_PLAN_CONTABLE;


PROCEDURE PR_CAUSAR_HEREDANDO_CONCI
  /*
  NAME              : causarHeredandoConciliacion
  AUTHORS           : SYSMAN LTDA
  AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRADOR     : 13/05/2019
  TIME              : 04:34 PM
  MODIFIER          :
  DATE MODIFIED     :
  TIME              :
  DESCRIPTION       :Procedimiento que permite heredar heredar la información desde un comprobante seleccionado,
                     heredando los registros de la cuenta reportada en el parámetro CUENTAS MEDICAS - CUENTA A COPIAR y 
                     creando la contrapartida con la cuenta reportada en el parámetro CUENTAS MEDICAS – CONTRACUENTA
  MODIFICATIONS     :

  @NAME: causarHeredandoConciliacion
  */
  (
    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO,
    UN_NUMERO         IN COMPROBANTE_CNT.NUMERO%TYPE,
    UN_TIPOMOVIMIENTO IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE ,    
    UN_NUMEROACOPIAR  IN COMPROBANTE_CNT.NUMERO%TYPE,
    UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
  )
AS
  MI_RTA            PCK_SUBTIPOS.TI_ENTERO := 0;
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
  MI_CUENTAACOPIAR  VARCHAR2(15 CHAR);
  MI_CONTRACUENTA   VARCHAR2(15 CHAR);
  MI_TIPOACOPIAR    VARCHAR2(10 CHAR);
  MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CONSECUTIVO    DETALLE_COMPROBANTE_CNT.CONSECUTIVO%TYPE;
  MI_VLRAGIRAR      NUMBER(20,2);
BEGIN

  MI_CUENTAACOPIAR :=  PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'CUENTAS MEDICAS - CUENTA A COPIAR',
                                              UN_MODULO    => PCK_DATOS.MODULOCONTABILIDAD,
                                              UN_FECHA_PAR => SYSDATE,
                                              UN_IND_MAYUS => 0);

  MI_CONTRACUENTA := PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'CUENTAS MEDICAS - CONTRACUENTA',
                                              UN_MODULO    => PCK_DATOS.MODULOCONTABILIDAD,
                                              UN_FECHA_PAR => SYSDATE,
                                              UN_IND_MAYUS => 0);

  MI_TIPOACOPIAR := PCK_SYSMAN_UTL.FC_PAR( UN_COMPANIA  => UN_COMPANIA,
                                              UN_NOMBRE    => 'CUENTAS MEDICAS - TIPO A COPIAR',
                                              UN_MODULO    => PCK_DATOS.MODULOCONTABILIDAD,
                                              UN_FECHA_PAR => SYSDATE,
                                              UN_IND_MAYUS => 0);                                              

--Se hereda la información de la cuenta a copiar

  MI_CAMPOS := 'COMPANIA,
                ANO,
                TIPO_CPTE,
                COMPROBANTE ,
                CONSECUTIVO,
                CUENTA,
                FECHA,
                NATURALEZA ,
                CODIGO_PPTAL,
                TIPOPPTAL,
                NUMEROPPTAL,
                CUENTAPPTAL ,
                DESCRIPCION,
                VALOR_DEBITO,
                VALOR_CREDITO,
                DEBITO_EQUIV ,
                CREDITO_EQUIV,
                EJECUCION_DEBITO,
                EJECUCION_CREDITO ,
                BASE_GRAVABLE,
                TIPO_DOCUMENTO,
                NRO_DOCUMENTO ,
                CENTRO_COSTO,
                TERCERO,
                SUCURSAL,
                AUXILIAR ,
                TIPO_CPTE_AFECT,
                CMPTE_AFECTADO,
                CHEQUEPARAANULAR ,
                CIERRE,
                BASE_IVA,
                PAGADOBANCO,
                CONSECUTIVOPPTO ,
                DESEMBOLSO,
                TIPOCONTRATO,
                NUMEROCONTRATO,
                SALDOCUENTA ,
                DEBITO_AFECTADO,
                CREDITO_AFECTADO,
                CONSECUTIVOAFECTADO ,
                FECHACONCILIA,
                FECHA_CONCILIA,
                REFERENCIA,
                FECHA_CONSIGNACIONPLANO ,
                TEXTOD,
                FECHA_CONCILIACION_PLANO,
                D_DEPENDENCIACNT,
                FUENTE_RECURSO ,
                CREATED_BY,
                DATE_CREATED';

  MI_VALORES := 'SELECT COMPANIA,
                        '||UN_ANIO||',
                        '''||UN_TIPOMOVIMIENTO||''',
                        '||UN_NUMERO||' ,
                        CONSECUTIVO,
                        CUENTA,
                        FECHA,
                        NATURALEZA ,
                        CODIGO_PPTAL,
                        TIPOPPTAL,
                        NUMEROPPTAL,
                        CUENTAPPTAL ,
                        DESCRIPCION,                        
                        VALOR_CREDITO,
                        VALOR_DEBITO,
                        DEBITO_EQUIV ,
                        CREDITO_EQUIV,
                        EJECUCION_DEBITO,
                        EJECUCION_CREDITO ,
                        BASE_GRAVABLE,
                        TIPO_DOCUMENTO,
                        NRO_DOCUMENTO ,
                        CENTRO_COSTO,
                        TERCERO,
                        SUCURSAL,
                        AUXILIAR ,
                        TIPO_CPTE_AFECT,
                        CMPTE_AFECTADO,
                        CHEQUEPARAANULAR ,
                        CIERRE,
                        BASE_IVA,
                        PAGADOBANCO,
                        CONSECUTIVOPPTO ,
                        DESEMBOLSO,
                        TIPOCONTRATO,
                        NUMEROCONTRATO,
                        SALDOCUENTA ,
                        DEBITO_AFECTADO,
                        CREDITO_AFECTADO,
                        CONSECUTIVOAFECTADO ,
                        FECHACONCILIA,
                        FECHA_CONCILIA,
                        REFERENCIA,
                        FECHA_CONSIGNACIONPLANO ,
                        TEXTOD,
                        FECHA_CONCILIACION_PLANO,
                        D_DEPENDENCIACNT,
                        FUENTE_RECURSO ,
                        '''||UN_USUARIO||''',
                        '''||SYSDATE||'''
                 FROM DETALLE_COMPROBANTE_CNT
                 WHERE COMPANIA    = '''||UN_COMPANIA||'''
                   AND TIPO_CPTE   = '''||MI_TIPOACOPIAR||'''
                   AND COMPROBANTE = '||UN_NUMEROACOPIAR||'
                   AND CUENTA      = '||MI_CUENTAACOPIAR||'';

    BEGIN
       BEGIN 

               PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME (UN_TABLA   => 'DETALLE_COMPROBANTE_CNT'
                                                               ,UN_ACCION  => 'IS'
                                                               ,UN_CAMPOS  => MI_CAMPOS
                                                               ,UN_VALORES => MI_VALORES);    

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
       END ;

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_REEMPLAZOS(0).CLAVE:='COMPROBANTE';
                    MI_REEMPLAZOS(0).VALOR:=UN_NUMERO;      
                    MI_REEMPLAZOS(1).CLAVE:='TIPO';
                    MI_REEMPLAZOS(1).VALOR:=UN_TIPOMOVIMIENTO;                      
                           PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE,
                                                      UN_ERROR_COD=>PCK_ERRORES.ER_CONTAB_INSDETALLE,
                                                      UN_REEMPLAZOS => MI_REEMPLAZOS);
    END;                    


--Se crea la informacion de contracuenta

FOR MI_RS IN ( SELECT COMPANIA ,
                      UN_ANIO ANO,
                      UN_TIPOMOVIMIENTO TIPO,
                      UN_NUMERO NUMERO,                        
                      MI_CONTRACUENTA CUENTA,
                      FECHA,
                      NATURALEZA ,
                      CODIGO_PPTAL,
                      TIPOPPTAL,
                      NUMEROPPTAL,
                      CUENTAPPTAL ,
                      DESCRIPCION,
                      VALOR_CREDITO,
                      VALOR_DEBITO,                        
                      DEBITO_EQUIV ,
                      CREDITO_EQUIV,
                      EJECUCION_DEBITO,
                      EJECUCION_CREDITO ,
                      BASE_GRAVABLE,
                      TIPO_DOCUMENTO,
                      NRO_DOCUMENTO ,
                      CENTRO_COSTO,
                      TERCERO,
                      SUCURSAL,
                      AUXILIAR ,
                      TIPO_CPTE_AFECT,
                      CMPTE_AFECTADO,
                      CHEQUEPARAANULAR ,
                      CIERRE,
                      BASE_IVA,
                      PAGADOBANCO,
                      CONSECUTIVOPPTO ,
                      DESEMBOLSO,
                      TIPOCONTRATO,
                      NUMEROCONTRATO,
                      SALDOCUENTA ,
                      DEBITO_AFECTADO,
                      CREDITO_AFECTADO,
                      CONSECUTIVOAFECTADO ,
                      FECHACONCILIA,
                      FECHA_CONCILIA,
                      REFERENCIA,
                      FECHA_CONSIGNACIONPLANO ,
                      TEXTOD,
                      FECHA_CONCILIACION_PLANO,
                      D_DEPENDENCIACNT,
                      FUENTE_RECURSO ,
                      UN_USUARIO CREATED_BY,
                      SYSDATE DATE_CREATED                
             FROM DETALLE_COMPROBANTE_CNT
             WHERE COMPANIA    = UN_COMPANIA
               AND TIPO_CPTE   = MI_TIPOACOPIAR
               AND COMPROBANTE = UN_NUMEROACOPIAR
               AND CUENTA      = MI_CUENTAACOPIAR

  )LOOP


  MI_CONDICION := 'DETALLE_COMPROBANTE_CNT.COMPANIA    ='''||UN_COMPANIA||'''
               AND DETALLE_COMPROBANTE_CNT.ANO         = '||MI_RS.ANO||'
               AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   = '''||MI_RS.TIPO||'''
               AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = '||MI_RS.NUMERO||' ';

  MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO( UN_TABLA    =>'DETALLE_COMPROBANTE_CNT',
                                                      UN_CRITERIO => MI_CONDICION,
                                                      UN_CAMPO    =>'CONSECUTIVO' ,
                                                      UN_INICIAL  =>'1');

  MI_CAMPOS := 'COMPANIA,
                ANO,
                TIPO_CPTE,
                COMPROBANTE,
                CONSECUTIVO,
                CUENTA,
                FECHA,
                NATURALEZA ,
                CODIGO_PPTAL,
                TIPOPPTAL,
                NUMEROPPTAL,
                CUENTAPPTAL ,
                DESCRIPCION,                
                VALOR_CREDITO,
                VALOR_DEBITO,
                DEBITO_EQUIV ,
                CREDITO_EQUIV,
                EJECUCION_DEBITO,
                EJECUCION_CREDITO ,
                BASE_GRAVABLE,
                TIPO_DOCUMENTO,
                NRO_DOCUMENTO ,
                CENTRO_COSTO,
                TERCERO,
                SUCURSAL,
                AUXILIAR ,
                TIPO_CPTE_AFECT,
                CMPTE_AFECTADO,
                CHEQUEPARAANULAR ,
                CIERRE,
                BASE_IVA,
                PAGADOBANCO,
                CONSECUTIVOPPTO ,
                DESEMBOLSO,
                TIPOCONTRATO,
                NUMEROCONTRATO,
                SALDOCUENTA ,
                DEBITO_AFECTADO,
                CREDITO_AFECTADO,
                CONSECUTIVOAFECTADO ,
                FECHACONCILIA,
                FECHA_CONCILIA,
                REFERENCIA,
                FECHA_CONSIGNACIONPLANO ,
                TEXTOD,
                FECHA_CONCILIACION_PLANO,
                D_DEPENDENCIACNT,
                FUENTE_RECURSO ,
                CREATED_BY,
                DATE_CREATED';

  MI_VALORES := ' '''||MI_RS.COMPANIA||''',
                  '||MI_RS.ANO||',
                  '''||MI_RS.TIPO||''',
                  '||MI_RS.NUMERO||' ,  
                  '||MI_CONSECUTIVO||',
                  '||MI_RS.CUENTA||',
                  '''||MI_RS.FECHA||''',
                  '''||MI_RS.NATURALEZA||''' ,
                  '''||MI_RS.CODIGO_PPTAL||''',
                  '''||MI_RS.TIPOPPTAL||''',
                  '''||MI_RS.NUMEROPPTAL||''',
                  '''||MI_RS.CUENTAPPTAL||''' ,
                  '''||MI_RS.DESCRIPCION||''',
                  '||MI_RS.VALOR_CREDITO||',
                  '||MI_RS.VALOR_DEBITO||',                        
                  '||MI_RS.DEBITO_EQUIV ||',
                  '||MI_RS.CREDITO_EQUIV||',
                  '||MI_RS.EJECUCION_DEBITO||',
                  '||MI_RS.EJECUCION_CREDITO||' ,
                  '||MI_RS.BASE_GRAVABLE||',
                  '''||MI_RS.TIPO_DOCUMENTO||''',
                  '''||MI_RS.NRO_DOCUMENTO||''' ,
                  '''||MI_RS.CENTRO_COSTO||''',
                  '''||MI_RS.TERCERO||''',
                  '''||MI_RS.SUCURSAL||''',
                  '''||MI_RS.AUXILIAR||''' ,
                  '''||MI_RS.TIPO_CPTE_AFECT||''',
                  '''||MI_RS.CMPTE_AFECTADO||''',
                  '''||MI_RS.CHEQUEPARAANULAR||''' ,
                  '||MI_RS.CIERRE||',
                  '||MI_RS.BASE_IVA||',
                  '||MI_RS.PAGADOBANCO||',
                  '''||MI_RS.CONSECUTIVOPPTO||''' ,
                  '||MI_RS.DESEMBOLSO||',
                  '''||MI_RS.TIPOCONTRATO||''',
                  '||MI_RS.NUMEROCONTRATO||',
                  '||MI_RS.SALDOCUENTA||' ,
                  '||MI_RS.DEBITO_AFECTADO||',
                  '||MI_RS.CREDITO_AFECTADO||',
                  '''||MI_RS.CONSECUTIVOAFECTADO||''' ,
                  '''||MI_RS.FECHACONCILIA||''',
                  '''||MI_RS.FECHA_CONCILIA||''',
                  '''||MI_RS.REFERENCIA||''',
                  '''||MI_RS.FECHA_CONSIGNACIONPLANO||''' ,
                  '''||MI_RS.TEXTOD||''',
                  '''||MI_RS.FECHA_CONCILIACION_PLANO||''',
                  '''||MI_RS.D_DEPENDENCIACNT||''',
                  '''||MI_RS.FUENTE_RECURSO||''' ,
                  '''||UN_USUARIO||''',
                  '''||SYSDATE||'''';                   

    BEGIN
       BEGIN 

               PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME (UN_TABLA   => 'DETALLE_COMPROBANTE_CNT'
                                                               ,UN_ACCION  => 'I'
                                                               ,UN_CAMPOS  => MI_CAMPOS
                                                               ,UN_VALORES => MI_VALORES);    

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
       END ;

                 EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_REEMPLAZOS(0).CLAVE:='COMPROBANTE';
                    MI_REEMPLAZOS(0).VALOR:=UN_NUMERO;      
                    MI_REEMPLAZOS(1).CLAVE:='TIPO';
                    MI_REEMPLAZOS(1).VALOR:=UN_TIPOMOVIMIENTO;                      
                           PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE,
                                                      UN_ERROR_COD=>PCK_ERRORES.ER_CONTAB_INSDETALLE,
                                                      UN_REEMPLAZOS => MI_REEMPLAZOS);
    END; 

  END LOOP;


 SELECT VLRAGIRAR
 INTO MI_VLRAGIRAR    
 FROM COMPROBANTE_CNT
 WHERE COMPANIA = UN_COMPANIA
   AND TIPO = MI_TIPOACOPIAR
   AND NUMERO = UN_NUMEROACOPIAR;


   MI_CAMPOS := 'VLRAGIRAR = '||MI_VLRAGIRAR;

   MI_CONDICION := 'COMPANIA  = '''||UN_COMPANIA||'''
                   AND ANO    = '||UN_ANIO||'
                   AND NUMERO = '||UN_NUMERO||'
                   AND TIPO   = '''||UN_TIPOMOVIMIENTO||''' ';

  BEGIN
     BEGIN
             PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME (UN_TABLA     => 'COMPROBANTE_CNT'
                                                    ,UN_ACCION    => 'M'
                                                    ,UN_CAMPOS    => MI_CAMPOS
                                                    ,UN_CONDICION => MI_CONDICION);    

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;             


              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                    MI_REEMPLAZOS(0).CLAVE:='COMPROBANTE';
                    MI_REEMPLAZOS(0).VALOR:=UN_NUMERO;      
                    MI_REEMPLAZOS(1).CLAVE:='ANIO';
                    MI_REEMPLAZOS(1).VALOR:=UN_ANIO;                      
                           PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE,
                                                      UN_ERROR_COD=>PCK_ERRORES.ERR_ENTES_M_PRT_BANPAGFEC,
                                                      UN_REEMPLAZOS => MI_REEMPLAZOS);                   
  END;

END PR_CAUSAR_HEREDANDO_CONCI;

FUNCTION FC_PLANOH
  (
  /*
    NAME                    :FC_PLANOH (En SQL SERVER GENERAR_PLANO_HACIENDA)     
    AUTHORS                 :STEFANINI SYSMAN SAS
    AUTHORS MIGRACION       :ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR           :12/01/2021
    TIME                    :02:00 PM
    SOURCE MODULE           : 
    MODIFIER                :GUSTAVO ANDRÉS FIGUEREDO �?VILA
    DATA MODIFIED           :14/07/2021
    TIME                    :09:20 AM
    DESCRIPCION             :Se realizan ajustes según tar 1000108794
    MODIFICATIONS           :Se elimina el primer espacio del archivo.
							 Se trae el valor de la cuenta cuando no se encuentra el equivalente.
							 Se deja vacio el valor de indicador de retenciones cuando no existe en el campo
							 codigo_hacienda.
							 Se relacionan cambios identificados con numero de tar.
    MODIFIER                :GUSTAVO ANDRÉS FIGUEREDO �?VILA
    DATA MODIFIED			:23/08/2021
    TIME                    :12:00 PM
    DESCRIPCION             :Se realizan ajustes según tar 1000109672 
    MODIFICATIONS           :Se ajusta la consulta proncipal para anñadir filtros por el cheque
    						 y comprobante.
    
    @NAME  : generarPlanoH
    @METHOD:  GET    
    */
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIAL       IN VARCHAR2,
    UN_FECHAFINAL         IN VARCHAR2,
    UN_FUENTEINICIAL      IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS,
    UN_FUENTEFINAL        IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS,
	UN_CHEQUEI			  IN PCK_SUBTIPOS.TI_NRODOCUMENTO,
	UN_CHEQUEF			  IN PCK_SUBTIPOS.TI_NRODOCUMENTO,
	UN_COMPROBANTEI		  IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
	UN_COMPROBANTEF		  IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT


)RETURN CLOB
    AS    
    MI_SESSION            NUMBER;
    MI_FECHAINI           DATE        :=TO_DATE(UN_FECHAINICIAL,'DD/MM/YYYY');
    MI_FECHAFIN           DATE        :=TO_DATE(UN_FECHAFINAL,'DD/MM/YYYY');
    MI_FUENTEINI          PCK_SUBTIPOS.TI_FUENTE_RECURSOS :=NVL(UN_FUENTEINICIAL,' ');
    MI_FUENTEFIN          PCK_SUBTIPOS.TI_FUENTE_RECURSOS :=NVL(UN_FUENTEFINAL ,'99');
    MI_COMPANIA           VARCHAR2(11):= NVL(UN_COMPANIA,'001');
    MI_STRSQL             PCK_SUBTIPOS.TI_STRSQL;
    MI_CUENTA             VARCHAR2(50);
    MI_CUENTABANCARIA     VARCHAR2(32);
    MI_TIPOCUENTABANCARIA VARCHAR2(32);
    MI_BANCOMEN           VARCHAR2(32);
    MI_TIPO_AFECT         VARCHAR(3);
    MI_NUMERO_AFECT       NUMBER;
    MI_CONTRATO           NUMBER(15,2);
    MI_TIPO_REGISTRO      VARCHAR2(3);
    MI_NUMERO_REGISTRO    NUMBER;
    MI_NRO_DOCUMENTO      NUMBER(15,2); 
    MI_TIPOVIGENCIA       VARCHAR2(2);
    MI_VIGENCIA           VARCHAR2(4);
    MI_VALORPRESUPUESTO   NUMBER;    
    MI_ANO                NUMBER;
    MI_TIPO               VARCHAR2(3);
    MI_NUMERO             NUMBER;   
    MI_FECHAEGR           DATE;    
    MI_TIPO_EGRESO        VARCHAR2(3);
    MI_NUMERO_EGRESO      NUMBER;    
    MI_CUENTAEGR          VARCHAR2(16);
    MI_BANCO              VARCHAR2(50);
    MI_CUENTABANCO        VARCHAR2(50);   
    MI_CONSECUTIVO        NUMBER        :=0;   
    MI_STR                VARCHAR2(32000):=''; -- TICKET7737931 MPEREZ
    MI_CONTADOR           NUMBER        :=1;
    MI_STRCUARENTA        VARCHAR2(32000);     -- TICKET7737931 MPEREZ
    MI_CONTADOR1          NUMBER        :=2;   -- TICKET7737931 MPEREZ
    MI_RETORNO            CLOB          :='';
    MI_REGISTROS          NUMBER        :=0;  
    MI_CONTARETENCION     NUMBER;
    MI_J                  NUMBER        :=1;
    MI_RS                 SYS_REFCURSOR;
    MI_RS2                SYS_REFCURSOR;
    MI_RS3                SYS_REFCURSOR;

    MI_VALOR_MAX          DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO%TYPE; -- TICKET7737931 MPEREZ
    MI_VALOR_MIN          DETALLE_COMPROBANTE_PPTAL.VALOR_DEBITO%TYPE; -- TICKET7737931 MPEREZ
    MI_DOCENTIDAD         VARCHAR2(4000 CHAR); -- TICKET7737931 MPEREZ
    MI_TIPOCC             VARCHAR2(6);         -- TICKET7737931 MPEREZ 
    
    

  BEGIN 
  
  --INICIO  TICKET7737931 MPEREZ 
    MI_DOCENTIDAD     := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA, UN_NOMBRE =>'DOCUMENTO DE IDENTIDAD DE LA COMPANIA', 
                                UN_MODULO =>PCK_DATOS.MODULOCONTABILIDAD, UN_FECHA_PAR=>SYSDATE ); 

    BEGIN
      SELECT TIPOID  
      INTO MI_TIPOCC
      FROM TERCERO
      WHERE COMPANIA = UN_COMPANIA AND NIT = MI_DOCENTIDAD;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_TIPOCC := '';                
    END;

    --FIN TICKET7737931 MPEREZ  
   FOR MI_RS IN(SELECT CC.COMPANIA,
                   CC.ANO,
                   CC.TIPO,
                   CC.NUMERO,
                   'C' C,
                   '1'  TIPO_REGISTRO,
                   CC.FECHA,
                   'KR' CLASE,
                   '1001' SOCIEDAD,
                   CC.FECHA   FECHA2,
                   EXTRACT(MONTH FROM CC.FECHA) MES,
                   'COP' COP,
                   TRIM(CC.DESCRIPCION) DESCRIPCION,
                   'P' TIPOP,
                   '31' NUMEROR,
                   CASE
                       WHEN T.TIPOID = 'N' THEN 'NIT'
                       ELSE ''
                   END
                   || CASE
                       WHEN T.TIPOID = 'C' THEN 'CC'
                       ELSE ''
                   END
                   || CASE
                       WHEN T.TIPOID = 'T' THEN 'TI'
                       ELSE ''
                   END TIPODOCIDENTIFICACION,
                   T.NIT,
                   T.SUCURSAL,
                   SUM(CASE
                       WHEN P.CLASECUENTA IN(
                           'G','V','R','A'
                       ) THEN DC.VALOR_DEBITO - DC.VALOR_CREDITO
                       ELSE 0
                   END) VLR_REGISTRO_AFECTADO,
                   CC.TIPOCONTRATO,
                   CC.NUMEROCONTRATO,
                   DECODE(TRIM(P.COD_EQUIV),'',P.CODIGO,P.COD_EQUIV) CODIGO_CUENTA,
                   '2141' CONDICION_PAGO,
                   CC.BANCO,
                   CC.CUENTABANCO,
                   CC.TIPODEPRESUPUESTO,
                   CC.REGISTRO
             FROM COMPROBANTE_CNT CC
             INNER JOIN COMPANIA C ON CC.COMPANIA = C.CODIGO
             INNER JOIN TIPO_COMPROBANTE TC ON CC.COMPANIA = TC.COMPANIA
                                               AND CC.TIPO = TC.CODIGO
             INNER JOIN DETALLE_COMPROBANTE_CNT DC ON CC.COMPANIA = DC.COMPANIA
                                                      AND CC.TIPO = DC.TIPO_CPTE
                                                      AND CC.NUMERO = DC.COMPROBANTE
             INNER JOIN PLAN_CONTABLE P ON P.COMPANIA = DC.COMPANIA
                                           AND P.ANO = DC.ANO
                                           AND P.CODIGO = DC.CUENTA
             INNER JOIN TERCERO T ON T.COMPANIA = CC.COMPANIA
                                     AND T.NIT = CC.TERCERO
                                     AND T.SUCURSAL = CC.SUCURSAL
             LEFT JOIN DETALLE_COMPROBANTE_CNT DCE ON DCE.COMPANIA = CC.COMPANIA
                                     AND DCE.TIPO_CPTE_AFECT = CC.TIPO
                                     AND DCE.CMPTE_AFECTADO = CC.NUMERO
             INNER JOIN COMPROBANTE_CNT CCE ON DCE.COMPANIA = CCE.COMPANIA
                                                   AND DCE.TIPO_CPTE = CCE.TIPO
                                                   AND DCE.COMPROBANTE = CCE.NUMERO  
             INNER JOIN TIPO_COMPROBANTE TCE ON CCE.COMPANIA = TCE.COMPANIA
                                                    AND CCE.TIPO = TCE.CODIGO
                                                    
             WHERE CC.COMPANIA = MI_COMPANIA
                   AND TC.CLASE_CONTABLE IN ('P')
                   AND DCE.TIPO_CPTE_AFECT = CC.TIPO
                   AND DCE.CMPTE_AFECTADO = CC.NUMERO
                   AND TCE.CLASE_CONTABLE IN ('E')                        
                   AND CCE.FECHA BETWEEN MI_FECHAINI AND MI_FECHAFIN
				   AND CCE.NUMERO BETWEEN UN_COMPROBANTEI AND UN_COMPROBANTEF
				   AND CCE.FUENTE_RECURSO BETWEEN MI_FUENTEINI AND MI_FUENTEFIN
				   AND CCE.NRO_DOCUMENTO BETWEEN UN_CHEQUEI AND UN_CHEQUEF
                   AND P.CLASECUENTA IN ('G','A','N')
                   AND CCE.GIROELECTRONICO NOT IN (0)              
             GROUP BY CC.COMPANIA,
                      CC.ANO,
                      CC.TIPO,
                      CC.NUMERO,
             'C',
             '1',
                      CC.FECHA,
             'KR',
             '1001',
             EXTRACT(MONTH FROM CC.FECHA),
             'COP',
             TRIM(CC.DESCRIPCION),
             'P',
             '31',
             CASE
                 WHEN T.TIPOID = 'N' THEN 'NIT'
                 ELSE ''
             END
             || CASE
                 WHEN T.TIPOID = 'C' THEN 'CC'
                 ELSE ''
             END
             || CASE
                 WHEN T.TIPOID = 'T' THEN 'TI'
                 ELSE ''
             END,
                      T.NIT,
                      T.SUCURSAL,
                      CC.TIPOCONTRATO,
             DECODE(TRIM(P.COD_EQUIV),'',P.CODIGO,P.COD_EQUIV),
             '2141',
                      CC.NUMEROCONTRATO,
                      CC.BANCO,
                      CC.CUENTABANCO,
                      CC.TIPODEPRESUPUESTO,
                      CC.REGISTRO
             ORDER BY CC.TIPO,
                      CC.NUMERO,
                      CC.FECHA)LOOP

    BEGIN

     SELECT  CNT.CUENTABANCO
     INTO   MI_CUENTA 
     FROM COMPROBANTE_CNT CNT
      INNER JOIN DETALLE_COMPROBANTE_CNT DCNT
       ON  CNT.COMPANIA  = DCNT.COMPANIA
       AND CNT.TIPO      =  DCNT.TIPO_CPTE
       AND CNT.NUMERO    =  DCNT.COMPROBANTE
     WHERE DCNT.COMPANIA        = MI_RS.COMPANIA
       AND DCNT.TIPO_CPTE_AFECT = MI_RS.TIPO
       AND DCNT.CMPTE_AFECTADO  = MI_RS.NUMERO
       AND ROWNUM       <= 1;

    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_CUENTA := 0;
    END;

    BEGIN
      SELECT TP.CUENTA
            , CASE WHEN TP.TIPOCUENTA='A'
               THEN '02'
               ELSE '01'
              END TIPOCUENTA
            , BN.BANCOMEN
      INTO MI_CUENTABANCARIA
         , MI_TIPOCUENTABANCARIA
         , MI_BANCOMEN
      FROM TERCERO T1
        LEFT JOIN TERCEROPAGOS TP
          ON  T1.COMPANIA  = TP.COMPANIA
          AND T1.NIT       = TP.NIT
          AND T1.SUCURSAL  = TP.SUCURSAL
        LEFT JOIN BANCO BN
          ON  TP.BANCO     = BN.BANCO
      WHERE T1.COMPANIA     = MI_RS.COMPANIA
        AND T1.NIT          = MI_RS.NIT
        AND T1.SUCURSAL     = MI_RS.SUCURSAL
        AND TP.CUENTA       = MI_CUENTA
        AND TP.ACTIVA NOT IN (0) -- EN JULIO 13 DE 2017 SE DEFINIO TOMAR SOLAMENTE LAS CUENTAS ACTIVAS POR HPV
        AND ROWNUM         <= 1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_CUENTABANCARIA    := '';
      MI_TIPOCUENTABANCARIA:= '';
      MI_BANCOMEN          := '';
    END;          

    BEGIN
      SELECT TIPO_CPTE_AFECT
           , CMPTE_AFECTADO
           , CP.NUMEROCONTRATO
      INTO MI_TIPO_AFECT
         , MI_NUMERO_AFECT
         , MI_CONTRATO
      FROM COMPROBANTE_PPTAL CP
      WHERE CP.COMPANIA = MI_RS.COMPANIA
        AND CP.TIPO     = MI_RS.TIPO
        AND CP.NUMERO   = MI_RS.NUMERO
        AND ROWNUM       <= 1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_TIPO_AFECT    := '';
      MI_NUMERO_AFECT  := 0;
      MI_CONTRATO      := 0;
    END;

    MI_TIPO_REGISTRO   := MI_TIPO_AFECT;
    MI_NUMERO_REGISTRO := MI_NUMERO_AFECT;
    
    BEGIN
     SELECT  CP.NRO_GENERA
     INTO   MI_NRO_DOCUMENTO --Posc Doc Pres
     FROM DETALLE_COMPROBANTE_PPTAL DCP
      INNER JOIN COMPROBANTE_PPTAL CP
       ON  DCP.COMPANIA        = CP.COMPANIA
       AND DCP.TIPO_CPTE_AFECT =  CP.TIPO
       AND DCP.CMPTE_AFECTADO  =  CP.NUMERO
     WHERE DCP.COMPANIA      = MI_RS.COMPANIA
       AND DCP.TIPO_CPTE     = MI_RS.TIPO
       AND DCP.COMPROBANTE   = MI_RS.NUMERO
       AND ROWNUM       <= 1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_NRO_DOCUMENTO := 0;
    END;
    
    BEGIN
      SELECT TIPOVIGENCIA
           , VIGENCIA
           , VALORBRUTO
      INTO MI_TIPOVIGENCIA
          , MI_VIGENCIA
          , MI_VALORPRESUPUESTO
      FROM
        (SELECT P.TIPOVIGENCIA,
          P.VIGENCIA,
          SUM(D.VALOR_DEBITO-D.VALOR_CREDITO) VALORBRUTO
         FROM DETALLE_COMPROBANTE_PPTAL D
          INNER JOIN PLAN_PRESUPUESTAL P
            ON  D.COMPANIA       = P.COMPANIA
            AND D.ANO            = P.ANO
            AND D.CUENTA         = P.CODIGO
         WHERE D.COMPANIA        = MI_RS.COMPANIA
           AND D.TIPO_CPTE       = MI_TIPO_AFECT
           AND D.COMPROBANTE     = MI_NUMERO_AFECT
        GROUP BY P.TIPOVIGENCIA,
          P.VIGENCIA
        )
      WHERE ROWNUM<=1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_TIPOVIGENCIA     := '';
      MI_VALORPRESUPUESTO := 0;
      MI_VIGENCIA         := '';
    END;
    --TRAE EL BANCO Y LA CUENTA BANCO DEL EGRESO    

    BEGIN 
       SELECT DISTINCT CC.COMPANIA
           , CC.ANO
           , CC.TIPO
           , CC.NUMERO
           , CC.FECHA
      INTO  MI_COMPANIA
         , MI_ANO
         , MI_TIPO
         , MI_NUMERO
         , MI_FECHAEGR
      FROM COMPROBANTE_CNT CC
        INNER JOIN DETALLE_COMPROBANTE_CNT DC
          ON  CC.COMPANIA       = DC.COMPANIA
          AND CC.TIPO           = DC.TIPO_CPTE
          AND CC.NUMERO         = DC.COMPROBANTE
      WHERE DC.COMPANIA         = MI_RS.COMPANIA
        AND DC.TIPO_CPTE_AFECT  = MI_RS.TIPO
        AND DC.CMPTE_AFECTADO   = MI_RS.NUMERO
        AND ROWNUM             <= 1;
     EXCEPTION WHEN NO_DATA_FOUND THEN
           MI_COMPANIA := '';
           MI_ANO := 0;
           MI_TIPO := '';
          MI_NUMERO := 0;
          MI_FECHAEGR:=MI_RS.FECHA;
         
    END;    
    
    MI_TIPO_EGRESO   := MI_TIPO;
    MI_NUMERO_EGRESO := MI_NUMERO;
    
    BEGIN 
	  SELECT DECODE(TRIM(PL.COD_EQUIV), '', PL.CODIGO, PL.COD_EQUIV) --Se agrega validación para cuando el COD_EQUIV sea nulo deja CODIGO Tar 1000108794
      INTO MI_CUENTAEGR
      FROM DETALLE_COMPROBANTE_CNT DC 
      INNER JOIN PLAN_CONTABLE PL 
         ON PL.COMPANIA = DC.COMPANIA 
         AND PL.ANO    = DC.ANO         
         AND PL.CODIGO = DC.CUENTA
       WHERE DC.COMPANIA         = MI_RS.COMPANIA
         AND DC.TIPO_CPTE_AFECT  = MI_RS.TIPO
         AND DC.CMPTE_AFECTADO   = MI_RS.NUMERO
         AND PL.CLASECUENTA IN('P','E')
         AND ROWNUM             <= 1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CUENTAEGR :=  '';
    END;    

    BEGIN
      SELECT BANCO
           , CUENTABANCO
      INTO MI_BANCO
        , MI_CUENTABANCO
      FROM
          ( SELECT DISTINCT BANCO 
                 , CUENTABANCO
            FROM COMPROBANTE_CNT C
            WHERE C.COMPANIA =MI_COMPANIA
              AND C.ANO        =MI_ANO
              AND C.TIPO       =MI_TIPO
              AND C.NUMERO     =MI_NUMERO
          )
      WHERE ROWNUM<=1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_BANCO      :='';
      MI_CUENTABANCO:='';
    END;

    MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
    --<<ENCABEZADO>> MI_FECHAEGR
    MI_STR        := MI_RS.C                                                       || CHR(09) 
                  || SUBSTR(MI_CONTADOR,1,10)                                       || CHR(09) 
                  || SUBSTR(REPLACE(TO_CHAR(MI_FECHAEGR,'YYYY/MM/DD'),'/',''),1,8) || CHR(09) 
                  || SUBSTR(MI_RS.CLASE,1,2)                                       || CHR(09) 
                  || SUBSTR(MI_RS.SOCIEDAD,1,4)                                    || CHR(09) 
                  || SUBSTR(REPLACE(TO_CHAR(MI_FECHAEGR,'YYYY/MM/DD'),'/',''),1,8)  || CHR(09) 
                  || SUBSTR( EXTRACT (MONTH FROM MI_FECHAEGR),1,2)                  || CHR(09) 
                  || SUBSTR(MI_RS.COP,1,3)                                         || CHR(09) 
                  || SUBSTR('',1,10)                                                || CHR(09)
                  || SUBSTR(MI_RS.NUMEROCONTRATO,1,16)                             || CHR(09) 
                  || SUBSTR(REPLACE(REPLACE(MI_RS.DESCRIPCION,';',','),CHR(39),''),1,25)  ;
    --ENCABEZADO

  --'40'
     -- BARRE CURSOR 3 DETALLES
    MI_STRCUARENTA := '';
    <<POSICIONGASTO>>
    FOR MI_RS3 IN
    (SELECT DISTINCT 'P' TIPO_REGISTRO
          , '40' CODIGO_REGISTRO
          , DCNT.ANO VIGENCIA
          , DPPTAL.COMPROBANTE
          ,'0214-01' CENTRO_GESTOR
          , DECODE(TRIM(P.COD_EQUIV), '', P.CODIGO, P.COD_EQUIV) CUENTA_CONTABLE
          , DPPTAL.VALOR_DEBITO-DPPTAL.VALOR_CREDITO VALOR
          , DPPTAL.CUENTA
          , PP.CODIGO_EQUIV
          , DPPTAL.DESCRIPCION
     FROM DETALLE_COMPROBANTE_CNT DCNT
      INNER JOIN DETALLE_COMPROBANTE_PPTAL DPPTAL
        ON  DCNT.COMPANIA        = DPPTAL.COMPANIA
        AND DCNT.TIPO_CPTE       = DPPTAL.TIPO_CPTE
        AND DCNT.COMPROBANTE     = DPPTAL.COMPROBANTE
      INNER JOIN PLAN_CONTABLE P
        ON  P.COMPANIA  = DCNT.COMPANIA
        AND P.ANO       = DCNT.ANO
        AND P.CODIGO        = DCNT.CUENTA
      INNER JOIN PLAN_PRESUPUESTAL PP
         ON  PP.COMPANIA =  DPPTAL.COMPANIA
         AND PP.ANO      =  DPPTAL.ANO
         AND PP.CODIGO       =  DPPTAL.CUENTA 
     WHERE DCNT.COMPANIA    = MI_RS.COMPANIA
       AND DCNT.TIPO_CPTE   = MI_RS.TIPO
       AND DCNT.COMPROBANTE = MI_RS.NUMERO
       AND P.CLASECUENTA    IN('C','A','N','G')
       AND PP.NATURALEZA    IN('D')
     GROUP BY
            'P' 
          , '40'
          , DCNT.ANO 
          , DPPTAL.COMPROBANTE
          ,'0214-01' 
          , DECODE(TRIM(P.COD_EQUIV), '', P.CODIGO, P.COD_EQUIV) 
          , DPPTAL.VALOR_DEBITO-DPPTAL.VALOR_CREDITO
          , DPPTAL.CUENTA
          , PP.CODIGO_EQUIV
          , DPPTAL.DESCRIPCION
    ORDER BY DPPTAL.CUENTA, DPPTAL.VALOR_DEBITO-DPPTAL.VALOR_CREDITO DESC
     )LOOP
        
        SELECT MAX(VALOR), 
               MIN(VALOR) 
          INTO MI_VALOR_MAX, 
               MI_VALOR_MIN
        FROM ( SELECT DISTINCT 'P' TIPO_REGISTRO
              , '40' CODIGO_REGISTRO
              , DCNT.ANO VIGENCIA
              , DPPTAL.COMPROBANTE
              ,'0214-01' CENTRO_GESTOR
              , DECODE(TRIM(P.COD_EQUIV), '', P.CODIGO, P.COD_EQUIV) CUENTA_CONTABLE
              , DPPTAL.VALOR_DEBITO-DPPTAL.VALOR_CREDITO VALOR
              , DPPTAL.CUENTA
              , PP.CODIGO_EQUIV
              , DPPTAL.DESCRIPCION
         FROM DETALLE_COMPROBANTE_CNT DCNT
          INNER JOIN DETALLE_COMPROBANTE_PPTAL DPPTAL
            ON  DCNT.COMPANIA        = DPPTAL.COMPANIA
            AND DCNT.TIPO_CPTE       = DPPTAL.TIPO_CPTE
            AND DCNT.COMPROBANTE     = DPPTAL.COMPROBANTE
          INNER JOIN PLAN_CONTABLE P
            ON  P.COMPANIA  = DCNT.COMPANIA
            AND P.ANO       = DCNT.ANO
            AND P.CODIGO        = DCNT.CUENTA
          INNER JOIN PLAN_PRESUPUESTAL PP
             ON  PP.COMPANIA =  DPPTAL.COMPANIA
             AND PP.ANO      =  DPPTAL.ANO
             AND PP.CODIGO       =  DPPTAL.CUENTA 
         WHERE DCNT.COMPANIA    = MI_RS.COMPANIA
           AND DCNT.TIPO_CPTE   = MI_RS.TIPO
           AND DCNT.COMPROBANTE = MI_RS.NUMERO
           AND P.CLASECUENTA    IN('C','A','N','G')
           AND PP.NATURALEZA    IN('D')
         GROUP BY
                'P' 
              , '40'
              , DCNT.ANO 
              , DPPTAL.COMPROBANTE
              ,'0214-01' 
              , DECODE(TRIM(P.COD_EQUIV), '', P.CODIGO, P.COD_EQUIV) 
              , DPPTAL.VALOR_DEBITO-DPPTAL.VALOR_CREDITO
              , DPPTAL.CUENTA
              , PP.CODIGO_EQUIV
              , DPPTAL.DESCRIPCION
        ORDER BY DPPTAL.CUENTA, DPPTAL.VALOR_DEBITO-DPPTAL.VALOR_CREDITO);        

        MI_STRCUARENTA := MI_STRCUARENTA ||CHR(13)             ||CHR(10)
                ||  SUBSTR(MI_RS3.TIPO_REGISTRO,1,1)           || CHR(09) 
                || SUBSTR(MI_RS3.CODIGO_REGISTRO,1,2)          || CHR(09) 
                || SUBSTR(MI_RS3.CUENTA_CONTABLE,1,10)         || CHR(09) 
                || SUBSTR('',1,2)                              || CHR(09) 
                || SUBSTR('',1,12)                             || CHR(09) 
                || SUBSTR('',1,1)                              || CHR(09) 
                || SUBSTR('',1,10)                             || CHR(09) 
                || SUBSTR( MI_RS3.VALOR,1,13)                  || CHR(09) 
                || SUBSTR('WB',1,2)                            || CHR(09) -- TICKET7737931 MPEREZ
                || SUBSTR(MI_NRO_DOCUMENTO,1,10)               || CHR(09) 
                || CASE WHEN MI_VALOR_MAX = MI_RS3.VALOR THEN SUBSTR(1,1,15) ELSE 
                   CASE WHEN MI_VALOR_MIN = MI_RS3.VALOR THEN SUBSTR(2,1,15) ELSE SUBSTR(MI_CONTADOR1+1,1,15) END END || CHR(09) -- TICKET7737931 MPEREZ
                || SUBSTR('',1,14)                             || CHR(09) 
                || SUBSTR('',1,24)                             || CHR(09) 
                || SUBSTR('',1,10)                             || CHR(09) 
                || SUBSTR('',1,10)                             || CHR(09) 
                || SUBSTR('',1,10)                             || CHR(09) 
                || SUBSTR('',1,10)                             || CHR(09) 
                || SUBSTR('',1,12)                             || CHR(09) 
                || SUBSTR('',1,24)                             || CHR(09) 
                || SUBSTR('',1,12)                             || CHR(09) 
                || SUBSTR('',1,4)                              || CHR(09) 
                || SUBSTR('',1,10)                             || CHR(09) 
                || SUBSTR('',1,8)                              || CHR(09) 
                || SUBSTR('',1,4)                              || CHR(09)
                || SUBSTR('',1,18)                             || CHR(09)
                || SUBSTR('',1,50)                             || CHR(09)
                || SUBSTR('',1,1)                              || CHR(09)
                || SUBSTR('',1,10)                             || CHR(09)
                || SUBSTR(CASE WHEN MI_TIPOCC =  'C' THEN 'CC' ELSE CASE WHEN MI_TIPOCC =  'T' THEN 'TI' ELSE  CASE WHEN  MI_TIPOCC  =  'N' THEN 'NIT' ELSE MI_TIPOCC END  END END,1,6) || CHR(09) -- TICKET7737931 MPEREZ
                || SUBSTR(MI_DOCENTIDAD,1,15)                  || CHR(09) -- TICKET7737931 MPEREZ
                || SUBSTR('',1,1)                              || CHR(09)
                || SUBSTR('',1,5)                              || CHR(09)
                || SUBSTR('',1,5)                              || CHR(09)
                || SUBSTR('',1,12)                             || CHR(09)
                || SUBSTR('',1,12)                             || CHR(09)
                || SUBSTR('',1,20)                             || CHR(09)
                || SUBSTR('',1,3)                              || CHR(09)
                || SUBSTR('',1,20)                             || CHR(09)
                || SUBSTR('',1,2)                              || CHR(09)
                || SUBSTR('',1,2)                              || CHR(09)
                || SUBSTR('',1,2)                              || CHR(09)
                || SUBSTR('',1,23)                             || CHR(09)
                || SUBSTR('',1,23)                             || CHR(09)
                /*|| SUBSTR('',1,23)                           || CHR(09)
                || SUBSTR('TERMONO',1,23)                      || CHR(09) 
                || SUBSTR('',1,23)                             || CHR(09)
                || SUBSTR(MI_RS3.VIGENCIA,1,23)                || CHR(09)
                || SUBSTR(MI_RS3.COMPROBANTE,1,23)             || CHR(09)
                || SUBSTR(MI_RS3.CENTRO_GESTOR,1,23)*/;     
     
     END LOOP POSICIONGASTO;

     MI_STR := MI_STR || MI_STRCUARENTA;  
	  IF MI_CONSECUTIVO =  1 THEN  --Se agrega validación para eliminar el primer espacio del archivo según Tar 1000108794
           MI_RETORNO:=MI_RETORNO || MI_STR;
      ELSE 
         MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
      END IF ;
     --MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
    --40
  --'31'
    MI_STR := '';
       --<<CLAVECONTABILIZACION>>
    MI_STR        :=  MI_RS.TIPOP                                                        || CHR(09) 
                  || SUBSTR(MI_RS.NUMEROR,1,2)                                           || CHR(09) 
                  || SUBSTR('',1,10)                                                      || CHR(09) 
                  || SUBSTR(MI_RS.TIPODOCIDENTIFICACION,1,3)                             || CHR(09) 
                  || SUBSTR(MI_RS.NIT,1,12)                                              || CHR(09) 
                  || SUBSTR('',1,1)                                                       || CHR(09)
                  || SUBSTR(MI_CUENTAEGR,1,10)                                    || CHR(09) 
                  || SUBSTR(MI_RS.VLR_REGISTRO_AFECTADO,1,13)                            || CHR(09) 
                  || SUBSTR('',1,2)                                                       || CHR(09) 
                  || SUBSTR('',1,10)                                                      || CHR(09) 
                  || SUBSTR('',1,3)                                                       || CHR(09) 
                  || SUBSTR('',1,14)                                                      || CHR(09) 
                  || SUBSTR('',1,24)                                                      || CHR(09) 
                  || SUBSTR('',1,10)                                                      || CHR(09) 
                  || SUBSTR('',1,10)                                                      || CHR(09) 
                  || SUBSTR('',1,10)                                                      || CHR(09) 
                  || SUBSTR('',1,10)                                                      || CHR(09) 
                  || SUBSTR('',1,12)                                                      || CHR(09) 
                  || SUBSTR('',1,24)                                                      || CHR(09) 
                  || SUBSTR('',1,12)                                                      || CHR(09) 
                  || SUBSTR('',1,4)                                                       || CHR(09) 
                  || SUBSTR('',1,10)                                                      || CHR(09) 
                  || SUBSTR('',1,8)                                                       || CHR(09) 
                  || SUBSTR(MI_RS.CONDICION_PAGO,1,4)                                    || CHR(09) 
                  || SUBSTR(MI_CONTRATO,1,18)                                             || CHR(09) 
                  || SUBSTR('',1,50)                                                      || CHR(09) 
                  || SUBSTR('',1,1)                                                       || CHR(09) 
                  || SUBSTR('',1,10)                                                      || CHR(09) 
                  || SUBSTR(CASE WHEN MI_TIPOCC =  'C' THEN 'CC' ELSE CASE WHEN MI_TIPOCC =  'T' THEN 'TI' ELSE  CASE WHEN  MI_TIPOCC  =  'N' THEN 'NIT' ELSE MI_TIPOCC END  END END,1,6) || CHR(09) -- TICKET7737931 MPEREZ
                  || SUBSTR(MI_DOCENTIDAD,1,15)                                           || CHR(09) -- TICKET7737931 MPEREZ 
                  || SUBSTR('',1,1)                                                       || CHR(09) 
                  || SUBSTR('',1,5)                                                       || CHR(09)
                  || SUBSTR('',1,5)                                                       || CHR(09) 
                  || SUBSTR('',1,12)                                                      || CHR(09) 
                  || SUBSTR('',1,12)                                                      || CHR(09) 
                  || SUBSTR('',1,20)                                                      || CHR(09) 
                  || SUBSTR(MI_BANCOMEN,1,7)                                              || CHR(09) 
                  || SUBSTR(MI_CUENTABANCARIA,1,20)                                       || CHR(09) 
                  || SUBSTR(MI_TIPOCUENTABANCARIA,1,2); 

    --CLAVECONTABILIZACION
    
    MI_RETORNO  :=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
    MI_REGISTROS:=MI_REGISTROS+1;
    MI_CONTADOR :=MI_CONTADOR +1;
    MI_CONTADOR1 := 2; -- TICKET7737931 MPEREZ
    --'31'                  
    
   -- BARRE CURSOR 2 RETENCIONES
    MI_STR := ''; 
    MI_CONTARETENCION := 0;
    --RETENCION
        FOR MI_RS2 IN
    (SELECT VALORBASE
           , DECODE(COMPRO.VALOR,0, DCT.VALOR_CREDITO,COMPRO.VALOR) VALOR
           , SUBSTR(NVL(VALOR,0) *100/VALORBASE,1,2) PCT_APLICAR
           , CODIGO_HACIENDA
           , RETE.CODIGO
           , TRIM(RETE.CODIGO_HACIENDA) INDICADOR_RETENCIONES --Se deja solo el valor de CODIGO_HACIENDA según Tar 1000108794
     FROM COMPROBANTE_CNTRETENCION COMPRO
      INNER JOIN RETENCIONES RETE
       ON  COMPRO.COMPANIA        = RETE.COMPANIA
       AND COMPRO.ANO             = RETE.ANO
       AND COMPRO.TIPORETENCION   = RETE.TIPO
       AND COMPRO.CODIGORETENCION = RETE.CODIGO
      INNER JOIN DETALLE_COMPROBANTE_CNT DCT
        ON  COMPRO.COMPANIA     =  DCT.COMPANIA 
        AND COMPRO.ANO          = DCT.ANO
        AND COMPRO.TIPO         =  DCT.TIPO_CPTE
        AND COMPRO.NUMERO       =  DCT.COMPROBANTE
        AND RETE.CUENTA_CREDITO =  DCT.CUENTA
     WHERE COMPRO.COMPANIA        = MI_RS.COMPANIA
       AND COMPRO.ANO             = MI_RS.ANO
       AND COMPRO.TIPO            = MI_RS.TIPO
       AND COMPRO.NUMERO          = MI_RS.NUMERO
    )LOOP
    
      MI_STR := ''; 
  --    <<VACIO>>
      --Se dejan 39 vac¿os porque la parte de retenciones va desde la posici¿n 40 en el  documento  relacionado en el TAR 1000092967
      --Fue necesario agregar 3 posic?ones  TAR:1000098111    
      IF MI_CONTARETENCION =  0 THEN 
        MI_STR :=   CHR(09);      
      ELSE 
          FOR MI_I IN 1..39 LOOP  
            MI_STR:= MI_STR
                  || '' || CHR(09);      
          END LOOP  VACIO;  
      END IF ;    

        MI_STR:= MI_STR
            || SUBSTR(MI_RS2.INDICADOR_RETENCIONES,1,2)                         || CHR(09)
            || SUBSTR(MI_RS2.INDICADOR_RETENCIONES,1,2)                         || CHR(09) 
            || SUBSTR(MI_RS2.VALORBASE,1,23)                                    || CHR(09) 
            || SUBSTR(MI_RS2.VALOR,1,23);    

      MI_J :=  MI_J + 1;
      IF MI_CONTARETENCION =  0 THEN 
           MI_RETORNO:=MI_RETORNO || MI_STR;
      ELSE 
         MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
      END IF ;
         
         MI_CONTARETENCION :=  MI_CONTARETENCION + 1;    
    
    
    END LOOP RETENCION;
    
   END LOOP;       

  RETURN MI_RETORNO;

END FC_PLANOH;

FUNCTION FC_PLANOH_2018
  (
  /*
    NAME                    :FC_PLANOH_2018 (En SQL SERVER GENERAR_PLANO_HACIENDA_2018)     
    AUTHORS                 :STEFANINI SYSMAN SAS
    AUTHORS MIGRACION       :ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR           :12/01/2021
    TIME                    :05:30 PM
    SOURCE MODULE           : 
    MODIFIER                :
    DATA MODIFIED           :
    TIME                    :
    DESCRIPCION             :
    MODIFICATIONS           :
    MODIFIER                :
    TIME                    :
    MODIFICATIONS           :
	
    @NAME  : generarPlanoH2018
    @METHOD:  GET

    */
    UN_COMPANIA           IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIAL       IN VARCHAR2,
    UN_FECHAFINAL         IN VARCHAR2,
    UN_FUENTEINICIAL      IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS,
    UN_FUENTEFINAL        IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS

)RETURN CLOB
 AS     
    MI_TIPO_COMPROMISO   NUMBER;
    MI_NUMERO_COMPROMISO NUMBER;
    MI_DETALLES          VARCHAR2(32000);
    MI_ORIGENFDL         VARCHAR2(3);
    MI_RUBRO             VARCHAR2(254);
    MI_FUENTE            VARCHAR2(64);

    MI_TIPO_CPTE_AFECT   VARCHAR2(3);
    MI_CMPTE_AFECTADO    NUMBER;
    MI_CONSECUTIVOPPTO   NUMBER;

    MI_TIPO_CPTE_AFECT1   VARCHAR2(3);
    MI_CMPTE_AFECTADO1    NUMBER;
    MI_CONSECUTIVOPPTO1   NUMBER;
    MI_CUENTAPPTO        VARCHAR2(32);

    MI_CODIGO_CUENTA     VARCHAR2(64);
    MI_TIPOVIGENCIA     VARCHAR2(2);
    MI_VIGENCIA         VARCHAR2(4);
    MI_VALORPRESUPUESTO NUMBER;
    MI_SITUACIONFONDOS   VARCHAR2(1);

    MI_TIPOCONTRATO      VARCHAR2(3);
    MI_NUMEROCONTRATO    NUMBER;

    MI_CODIGO_SGC        VARCHAR2(64);
    MI_INTERVENTOR       VARCHAR2(230);

    MI_CUENTABANCARIA     VARCHAR2(32);
    MI_NOMBRECIUDAD       VARCHAR2(32);
    MI_TIPOCUENTABANCARIA VARCHAR2(32);
    MI_BANCOMEN           VARCHAR2(32);

    MI_TIPO_E            VARCHAR2(3);
    MI_COMPROBANTE_E     NUMBER;
    MI_CONSECUTIVO_E     NUMBER;
    MI_FECHA_E           DATE;
    MI_FECHA             DATE;

    MI_CODIGOTIPO        VARCHAR2(10);
    MI_COD_COMPONENTE    VARCHAR2(10);                  
    MI_COD_OBJETO        VARCHAR2(10);
    MI_DETALLEFUENTE     NUMBER;
    MI_VALOR_FUENTE      NUMBER;
    MI_RUBROFUT          VARCHAR2(254);
    MI_INTI              NUMBER;
    MI_INTJ              NUMBER;
    MI_RETORNO           CLOB:='';
    MI_STR               VARCHAR2(3200):='';

    MI_TIPO_AFECT        VARCHAR(3);
    MI_NUMERO_AFECT      NUMBER;
    MI_RUBROFUT1         VARCHAR2(254);
    MI_TIPO_REGISTRO     VARCHAR2(3);
    MI_NUMERO_REGISTRO   NUMBER;
    MI_TIPO_DISPON       VARCHAR2(3);
    MI_NUMERO_DISPON     NUMBER;
    MI_FUENTES           NUMBER:=0;

    MI_TIPO_EGRESO       VARCHAR2(3);
    MI_NUMERO_EGRESO     NUMBER;

    MI_S_ARCHIVO        UTL_FILE.FILE_TYPE;
    MI_L_ARCHIVO        UTL_FILE.FILE_TYPE;
    MI_V_LINEA          VARCHAR2(32000);
    MI_SESSION          NUMBER;
    MI_ENTIDAD          VARCHAR2(3):='214';
    MI_UNIDADEJECUTORA  VARCHAR2(2):='01';
    MI_FECHAINI         DATE :=TO_DATE(UN_FECHAINICIAL,'DD/MM/RRRR');
    MI_FECHAFIN         DATE :=TO_DATE(UN_FECHAFINAL,'DD/MM/RRRR');

    MI_FUENTEINI          PCK_SUBTIPOS.TI_FUENTE_RECURSOS :=NVL(UN_FUENTEINICIAL,' ');
    MI_FUENTEFIN          PCK_SUBTIPOS.TI_FUENTE_RECURSOS :=NVL(UN_FUENTEFINAL ,'99');

    MI_COMPANIA         VARCHAR2(11):=  NVL(UN_COMPANIA,'001');

    MI_ETAPA            VARCHAR2(3200);
    MI_RUTASALIDA       VARCHAR2(3200):='IDIPRON_'||TO_CHAR(MI_FECHAINI,'DDMMRRRR')||'_'||TO_CHAR(MI_FECHAFIN,'DDMMRRRR')||'.TXT';
    MI_RUTALOG          VARCHAR2(3200):='LOG_'||MI_RUTASALIDA;

    MI_VERSION          VARCHAR2(3200):='GENERACION DE ARCHIVO PLANO HACIENDA DISTRITAL - VERSION 2019 RELEASE 22 ';

    MI_TI               DATE:=SYSDATE;
    MI_TF               DATE:=SYSDATE;

    MI_CONSECUTIVO     NUMBER:=0;
    MI_REGISTROS       NUMBER:=0;

    MI_RS1             SYS_REFCURSOR;
    MI_RS2             SYS_REFCURSOR;
    MI_RS3             SYS_REFCURSOR;
    MI_RS4             SYS_REFCURSOR;
    MI_RS5             SYS_REFCURSOR;
    MI_RST             SYS_REFCURSOR;

BEGIN 

-- VAMOS CON REGISTRO TIPO 1
    FOR MI_RS1 IN
           (SELECT CC.COMPANIA,
                CC.ANO, 
                CC.TIPO, 
                CC.NUMERO, 
                C.ENTIDAD, 
                C.UNIDAD_EJECUTORA, 
                'OP'  TIPODOCPAGO,
                 CASE WHEN T.TIPOID = 'N' THEN  'NIT' ELSE '' END 
                 || CASE WHEN T.TIPOID = 'C' THEN 'CC' ELSE '' END 
                 || CASE WHEN T.TIPOID = 'T' THEN 'TI' ELSE '' END TIPODOCIDENTIFICACION,
                 T.NIT,
                '' ORIGENFDL, 
                CASE WHEN NVL(CC.PAGOTOTAL,0) = 0 THEN 'P' ELSE  'T' END DESC_EJECUTADO,
                ''  ACTARECIBO,
                SUM(CASE WHEN P.CLASECUENTA IN ('P','E')     THEN DC.VALOR_CREDITO-DC.VALOR_DEBITO   ELSE 0 END)   CODCONT_VLRNETO,
                SUM(CASE WHEN P.CLASECUENTA IN ('G','V','R') THEN DC.VALOR_DEBITO -DC.VALOR_CREDITO  ELSE  0 END)  CODCONT_VLRBRUTO,
                CASE WHEN NVL(CC.GIROELECTRONICO,0) <> 0 THEN 'A' ELSE 'A' END  FORMAPAGO,
                CASE WHEN T.REGIMEN = 'C' THEN 'COMUN' ELSE CASE WHEN T.REGIMEN = 'S' THEN  'SIMPLIFICADO' ELSE CASE WHEN T.REGIMEN = 'E' THEN  'ESPECIAL' ELSE CASE WHEN T.REGIMEN = 'N' THEN  'NO REPORTA' ELSE  'AUTORRETENEDOR' END END END END  TREGIMEN, 
                CC.DESCRIPCION,
                T.DIRECCION, 
                CASE WHEN T.NATURALEZA = 'N' THEN  'Persona Natural' ELSE CASE WHEN T.NATURALEZA = 'J' THEN 'Persona Juridica' ELSE '' END END  TNATURALEZA,
                T.NOMBRE MUNICIPIO,
                T.TELEFONOS  TELEFONO, 
                1 AS TIPO_REGISTRO,
                CC.FECHA,
                CC.TIPOCONTRATO,
                CC.NUMEROCONTRATO,
                CC.TIPODEPRESUPUESTO,
                CC.REGISTRO,
                T.REGIMEN,
                TRIM(LPAD(CC.TEXTO,32)) OBSERVAC,
                '1' TIPODEORDEN,
                T.SUCURSAL,
                CC.NRO_DOCUMENTO,
                T.ACTIVIDAD,
                T.CODIGOICA
         FROM COMPROBANTE_CNT   CC 
           INNER JOIN COMPANIA          C   
              ON CC.COMPANIA=C.CODIGO			  
           INNER JOIN TIPO_COMPROBANTE  TC  
              ON CC.COMPANIA = TC.COMPANIA 
             AND CC.TIPO = TC.CODIGO
           INNER JOIN DETALLE_COMPROBANTE_CNT DC ON CC.COMPANIA=DC.COMPANIA 
             AND CC.TIPO=DC.TIPO_CPTE 
             AND CC.NUMERO=DC.COMPROBANTE
           INNER JOIN PLAN_CONTABLE P 
              ON P.COMPANIA=DC.COMPANIA 
             AND P.ANO=DC.ANO
             AND P.CODIGO=DC.CUENTA
           INNER JOIN TERCERO T 
              ON T.COMPANIA=CC.COMPANIA 
             AND T.NIT=CC.TERCERO 
             AND T.SUCURSAL=CC.SUCURSAL
         WHERE CC.COMPANIA =MI_COMPANIA --AND CC.FECHA BETWEEN MI_FECHAINI AND MI_FECHAFIN
                  AND TC.CLASE_CONTABLE IN('P')
                  AND  (SELECT DISTINCT NVL(CCE.GIROELECTRONICO,0) 
                        FROM DETALLE_COMPROBANTE_CNT   DCE
                             INNER JOIN COMPROBANTE_CNT CCE 
                                ON DCE.COMPANIA=CCE.COMPANIA 
                                AND DCE.TIPO_CPTE=CCE.TIPO 
                                AND DCE.COMPROBANTE=CCE.NUMERO
                             INNER JOIN TIPO_COMPROBANTE  TCE  
                                ON CCE.COMPANIA = TCE.COMPANIA 
                               AND CCE.TIPO = TCE.CODIGO
                        WHERE  DCE.COMPANIA       =CC.COMPANIA 
                          AND  DCE.TIPO_CPTE_AFECT=CC.TIPO
                          AND  DCE.CMPTE_AFECTADO =CC.NUMERO
                          AND  TCE.CLASE_CONTABLE IN('E')
                          AND  CCE.FECHA BETWEEN MI_FECHAINI AND MI_FECHAFIN
                        ) NOT IN (0)
         GROUP BY 
                CC.COMPANIA, CC.ANO, CC.TIPO, CC.NUMERO, C.ENTIDAD, 
                C.UNIDAD_EJECUTORA, 'OP', 
                CASE WHEN T.TIPOID = 'N' THEN 'NIT' ELSE '' END || CASE WHEN T.TIPOID = 'C' THEN 'CC' ELSE '' END || CASE WHEN T.TIPOID = 'T' THEN 'TI' ELSE '' END, 
                T.TIPOID, 
                'N', 
                'NIT', '', 
                CASE WHEN T.TIPOID = 'C' THEN 'CC' ELSE '' END, T.TIPOID, 'C', 
                'CC', '', 
                CASE WHEN T.TIPOID = 'T' THEN 'TI' ELSE '' END, T.TIPOID, 'T', 
                'TI', '', T.NIT, '', 
                CASE WHEN NVL(CC.PAGOTOTAL,0) = 0 THEN 'P' ELSE 'T' END, 
                NVL(CC.PAGOTOTAL,0), CC.PAGOTOTAL, 0, 0, 'P', 
                'T', '', 
                CASE WHEN NVL(CC.GIROELECTRONICO,0) <> 0 THEN 'A' ELSE 'A' END, NVL(CC.GIROELECTRONICO,0), CC.GIROELECTRONICO, 
                0, 0, 'A', 'A', 
                CASE WHEN T.REGIMEN = 'C' THEN 'COMUN' ELSE CASE WHEN T.REGIMEN = 'S' THEN 'SIMPLIFICADO' ELSE CASE WHEN T.REGIMEN = 'E' THEN 'ESPECIAL' ELSE CASE WHEN T.REGIMEN = 'N' THEN 'NO REPORTA' ELSE 'AUTORRETENEDOR' END END END END, 
                T.REGIMEN, 'C', 'COMUN', CASE WHEN T.REGIMEN = 'S' THEN 'SIMPLIFICADO' ELSE CASE WHEN T.REGIMEN = 'E' THEN 'ESPECIAL' ELSE CASE WHEN T.REGIMEN = 'N' THEN 'NO REPORTA' ELSE 'AUTORRETENEDOR' END END END, T.REGIMEN, 
                'S', 'SIMPLIFICADO', CASE WHEN T.REGIMEN = 'E' THEN 'ESPECIAL' ELSE CASE WHEN T.REGIMEN = 'N' THEN 'NO REPORTA' ELSE 'AUTORRETENEDOR' END END, T.REGIMEN, 'E', 
                'ESPECIAL', CASE WHEN T.REGIMEN = 'N' THEN 'NO REPORTA' ELSE 'AUTORRETENEDOR' END, T.REGIMEN, 'N', 'NO REPORTA', 
                'AUTORRETENEDOR', CC.DESCRIPCION, T.DIRECCION, CASE WHEN T.NATURALEZA = 'N' THEN 'Persona Natural' ELSE CASE WHEN T.NATURALEZA = 'J' THEN 'Persona Juridica' ELSE '' END END, T.NATURALEZA, 
                'N', 'Persona Natural', CASE WHEN T.NATURALEZA = 'J' THEN 'Persona Juridica' ELSE '' END, T.NATURALEZA, 'J', 
                'Persona Juridica', '', T.NOMBRE, T.TELEFONOS, 1, 
                CC.FECHA, CC.TIPOCONTRATO, CC.NUMEROCONTRATO, CC.TIPODEPRESUPUESTO, CC.REGISTRO, 
                T.REGIMEN, TRIM(LPAD(CC.TEXTO,32)), LPAD(CC.TEXTO,32), CC.TEXTO, 32, 
                '', T.SUCURSAL, CC.NRO_DOCUMENTO, T.ACTIVIDAD, T.CODIGOICA, '1'
          ORDER BY CC.TIPO,CC.NUMERO,CC.FECHA
            )
    LOOP

       BEGIN
          SELECT TOC.CODIGO_SGC,
                 OC.INTERVENTOR
          INTO
               MI_CODIGO_SGC,
               MI_INTERVENTOR
          FROM 
               ORDENDECOMPRA OC
               LEFT JOIN  TIPOORDENDECOMPRA TOC ON (TOC.COMPANIA=OC.COMPANIA) AND (TOC.CODIGO=OC.CLASEORDEN) 
         WHERE 
                OC.COMPANIA  =MI_RS1.COMPANIA 
            AND OC.CLASEORDEN=MI_RS1.TIPOCONTRATO
            AND OC.NUMERO    =MI_RS1.NUMEROCONTRATO;
        EXCEPTION WHEN  NO_DATA_FOUND  THEN
               MI_CODIGO_SGC:='';
               MI_INTERVENTOR:='';
        END;  

        BEGIN        
            SELECT  TP.CUENTA,
                    CASE WHEN TP.TIPOCUENTA='A' THEN 'AHORROS' ELSE 'CORRIENTE' END TIPOCUENTA,
                    BN.BANCOMEN,
                    CI.NOMBRE
             INTO
                    MI_CUENTABANCARIA,
                    MI_TIPOCUENTABANCARIA,
                    MI_BANCOMEN,
                    MI_NOMBRECIUDAD
             FROM TERCERO T1
                LEFT  JOIN CIUDAD  CI  ON (T1.PAIS = CI.PAIS) AND (T1.DEPARTAMENTO = CI.DEPARTAMENTO) AND (T1.CIUDAD = CI.CODIGO)
                LEFT  JOIN TERCEROPAGOS TP  ON (T1.COMPANIA = TP.COMPANIA) AND (T1.NIT = TP.NIT) AND (T1.SUCURSAL = TP.SUCURSAL)
                LEFT  JOIN BANCO BN  ON (TP.BANCO=BN.BANCO)
             WHERE  T1.COMPANIA=MI_RS1.COMPANIA 
                AND T1.NIT     =MI_RS1.NIT 
                AND T1.SUCURSAL=MI_RS1.SUCURSAL
                AND TP.ACTIVA NOT IN (0) -- EN JULIO 13 DE 2017 SE DEFINIO TOMAR SOLAMENTE LAS CUENTAS ACTIVAS POR HPV
                AND ROWNUM<=1;
         EXCEPTION  WHEN NO_DATA_FOUND  THEN
                    MI_CUENTABANCARIA:='';
                    MI_TIPOCUENTABANCARIA:='';
                    MI_BANCOMEN:='';
                    MI_NOMBRECIUDAD:='';
         END;  

		 BEGIN
               SELECT 
                     TIPO_CPTE_AFECT,
     		         CMPTE_AFECTADO
               INTO
                     MI_TIPO_AFECT,
                     MI_NUMERO_AFECT
               FROM 
                    COMPROBANTE_PPTAL CP 
               WHERE CP.COMPANIA    =MI_RS1.COMPANIA
                 AND CP.TIPO        =MI_RS1.TIPO 
                 AND CP.NUMERO      =MI_RS1.NUMERO 
   				       AND ROWNUM<=1;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                 MI_TIPO_AFECT:='';
                 MI_NUMERO_AFECT:=0;
        END;

       MI_TIPO_REGISTRO  :=MI_TIPO_AFECT;
       MI_NUMERO_REGISTRO:=MI_NUMERO_AFECT;    

          BEGIN
               SELECT TIPO,
                      NUMERO
               INTO   MI_TIPO_EGRESO,
                      MI_NUMERO_EGRESO
               FROM 
               (
                   SELECT DISTINCT TIPO_CPTE   TIPO,
                                   COMPROBANTE NUMERO
                   FROM
                          DETALLE_COMPROBANTE_CNT C
                   WHERE  C.COMPANIA         =MI_RS1.COMPANIA
                     AND  C.TIPO_CPTE_AFECT  =MI_RS1.TIPO
                     AND  C.CMPTE_AFECTADO   =MI_RS1.NUMERO
               )
               WHERE ROWNUM<=1;
          EXCEPTION WHEN NO_DATA_FOUND THEN   
              MI_TIPO_EGRESO:='';
              MI_NUMERO_EGRESO:=0;
          END;   

       BEGIN
             SELECT  TIPOVIGENCIA,
                     VIGENCIA,
                     VALORBRUTO
               INTO
                     MI_TIPOVIGENCIA,
                     MI_VIGENCIA,
                     MI_VALORPRESUPUESTO
              FROM (             
               SELECT 
                     P.TIPOVIGENCIA,
                     P.VIGENCIA,
                     SUM(D.VALOR_DEBITO-D.VALOR_CREDITO) VALORBRUTO
               FROM 
                    DETALLE_COMPROBANTE_PPTAL D 
                    INNER JOIN PLAN_PRESUPUESTAL P 
                            ON D.COMPANIA=P.COMPANIA 
                           AND D.ANO=P.ANO 
                           AND D.CUENTA=P.CODIGO
               WHERE D.COMPANIA    =MI_RS1.COMPANIA
                 AND D.TIPO_CPTE   =MI_TIPO_AFECT
                 AND D.COMPROBANTE =MI_NUMERO_AFECT
               GROUP BY
                  P.TIPOVIGENCIA,
                  P.VIGENCIA
              )
               WHERE ROWNUM<=1;
          EXCEPTION WHEN NO_DATA_FOUND THEN
                 MI_TIPOVIGENCIA:='';
                 MI_VALORPRESUPUESTO:=0;
                 MI_VIGENCIA:=' ';
 		 END;       


         SELECT  COUNT(*)
         INTO    MI_FUENTES
         FROM  DETALLE_COMPROBANTE_PPTAL D 
            LEFT JOIN  PLAN_PPTAL_CONFIG P ON
             D.COMPANIA = P.COMPANIA
             AND D.ANO = P.ANO
             AND D.CUENTA=P.CODIGO            
         WHERE D.COMPANIA    =MI_RS1.COMPANIA
           AND D.TIPO_CPTE   =MI_TIPO_AFECT
           AND D.COMPROBANTE =MI_NUMERO_AFECT
           AND P.FUENTE_RECURSO   BETWEEN MI_FUENTEINI AND MI_FUENTEFIN;

          MI_FUENTES:=NVL(MI_FUENTES,0);

         BEGIN
           SELECT  P.FUENTE_RECURSO
           INTO    MI_FUENTE
             FROM  DETALLE_COMPROBANTE_PPTAL D 
                LEFT JOIN  PLAN_PPTAL_CONFIG P ON
                 D.COMPANIA = P.COMPANIA
                 AND D.ANO = P.ANO
                 AND D.CUENTA=P.CODIGO       
           WHERE D.COMPANIA    =MI_RS1.COMPANIA
             AND D.TIPO_CPTE   =MI_TIPO_AFECT
             AND D.COMPROBANTE =MI_NUMERO_AFECT
             AND P.FUENTE_RECURSO IS NOT NULL
             AND ROWNUM<=1;
         EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_FUENTE:='';
         END;       

        --      IF MI_FUENTE NOT BETWEEN MI_FUENTEINI AND MI_FUENTEFIN THEN   
             IF MI_FUENTES <=0 THEN
                     CONTINUE;
              END IF;   

          MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
   		  MI_SITUACIONFONDOS:='S';   

         MI_STR:=MI_RS1.TIPO_REGISTRO || ';' 
            || TRIM(TO_CHAR(MI_CONSECUTIVO,'000000')) || ';' 
            || MI_VIGENCIA || ';' 
            || NVL(MI_RS1.ENTIDAD, MI_ENTIDAD) || ';' 
            || NVL(MI_RS1.UNIDAD_EJECUTORA, MI_UNIDADEJECUTORA) || ';' 
            || MI_RS1.TIPODOCPAGO || ';'
            || MI_RS1.TIPODOCIDENTIFICACION || ';' 
            || MI_RS1.NIT || ';' 
            || NVL(MI_CODIGO_SGC,'') || ';' 
            || MI_RS1.NUMEROCONTRATO || ';'  --|| MI_RS1.REGISTRO || ';'
            || CASE WHEN MI_TIPOVIGENCIA='VA' THEN 'V' ELSE '' END || CASE WHEN MI_TIPOVIGENCIA='RA' THEN 'R' ELSE '' END  || CASE WHEN MI_TIPOVIGENCIA='RC' THEN 'C' ELSE '' END || ';'
            || MI_SITUACIONFONDOS || ';' 
            || REPLACE(REPLACE(MI_RS1.DESCRIPCION,';',','),CHR(39),'') || ';' 
            || MI_RS1.ORIGENFDL || ';' 
            || MI_RS1.DESC_EJECUTADO || ';' 
            || MI_RS1.ACTARECIBO || ';'
            || NVL(MI_INTERVENTOR,'') || ';' 
            || MI_RS1.CODCONT_VLRNETO      || ';' 
            || MI_RS1.CODCONT_VLRBRUTO || ';' 
            || MI_RS1.FORMAPAGO || ';' 
            || NVL(MI_CUENTABANCARIA,'') || ';' 
            || NVL(MI_BANCOMEN,'') || ';'
            || MI_TIPOCUENTABANCARIA || ';' 
            || MI_RS1.TREGIMEN || ';' 
            || MI_RS1.OBSERVAC || ';' 
            || MI_RS1.DIRECCION || ';' 
            || MI_RS1.TNATURALEZA || ';'
            || NVL(MI_NOMBRECIUDAD,'') || ';' 
            || MI_RS1.TELEFONO || ';' 
            || MI_RS1.TIPODEORDEN || ';' ;          

              MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
              MI_REGISTROS:=MI_REGISTROS+1;

              -- BARRE CURSOR 2

            FOR MI_RS2 IN (SELECT 2 TIPO_REGISTRO,
                              DCP.COMPANIA, 
                              PP.TIPOVIGENCIA, 
                              PP.VIGENCIA, 
                              C.ENTIDAD, 
                              C.UNIDAD_EJECUTORA, 
                              'OP' TIPODOCPAGO, 
                              DCP.TIPO_CPTE_AFECT, 
                              DCP.CMPTE_AFECTADO, 
                              CP.FECHA, 
                              CP.TIPO,
                              CP.NUMERO,
                              --RPAD(P.RUBRO_GUION,32) RUBRO_GUIONR,
                              CASE WHEN P.ANO<2022 THEN RPAD(P.RUBRO_GUION,32) ELSE RPAD(P.RUBRO_GUION,100) END RUBRO_GUIONR,
                              P.FUENTE_RECURSO       FUENTER,
                              --RPAD(P.CODIGO,32) RUBROR,
                              CASE WHEN P.ANO<2022 THEN RPAD(P.CODIGO,32) ELSE RPAD(P.CODIGO,100) END  RUBROR,
                              SUM(DCP.VALOR_DEBITO-DCP.VALOR_CREDITO) VALORNETO
                           FROM DETALLE_COMPROBANTE_PPTAL  DCP 
                              INNER JOIN TIPO_COMPROBPP TC 
                                    ON  DCP.COMPANIA = TC.COMPANIA 
                                   AND DCP.TIPO_CPTE = TC.CODIGO
                              INNER JOIN PLAN_PRESUPUESTAL  PP 
                                    ON DCP.COMPANIA = PP.COMPANIA 
                                   AND DCP.ANO = PP.ANO 
                                   AND DCP.CUENTA = PP.CODIGO 
                              INNER JOIN COMPANIA  C 
                                     ON DCP.COMPANIA = C.CODIGO
                              INNER JOIN COMPROBANTE_PPTAL  CP 
                                     ON DCP.COMPANIA = CP.COMPANIA 
                                    AND DCP.ANO = CP.ANO  
                                    AND DCP.TIPO_CPTE = CP.TIPO 
                                    AND DCP.COMPROBANTE = CP.NUMERO
                              LEFT JOIN  PLAN_PPTAL_CONFIG P 
                                     ON DCP.COMPANIA = P.COMPANIA
                                     AND DCP.ANO = P.ANO
                                     AND DCP.CUENTA=P.CODIGO   
                           WHERE DCP.COMPANIA    =MI_RS1.COMPANIA
                             AND DCP.TIPO_CPTE   =MI_RS1.TIPO   --TIPODEPRESUPUESTO  --MI_TIPO_AFECT
                             AND DCP.COMPROBANTE =MI_RS1.NUMERO    --REGISTRO   --MI_NUMERO_AFECT
                           GROUP BY
                              2,
                              DCP.COMPANIA, 
                              PP.TIPOVIGENCIA, 
                              PP.VIGENCIA, 
                              C.ENTIDAD, 
                              C.UNIDAD_EJECUTORA, 
                              'OP', 
                              DCP.TIPO_CPTE_AFECT, 
                              DCP.CMPTE_AFECTADO, 
                              CP.FECHA, 
                              CP.TIPO,
                              CP.NUMERO,
                              --RPAD(P.RUBRO_GUION,32),
                              CASE WHEN P.ANO<2022 THEN RPAD(P.RUBRO_GUION,32) ELSE RPAD(P.RUBRO_GUION,100) END,
                              P.FUENTE_RECURSO   ,
                              CASE WHEN P.ANO<2022 THEN RPAD(P.CODIGO,32) ELSE RPAD(P.CODIGO,100) END                               
            --          ORDER BY TIPO,NUMERO,FECHA 
                  ) LOOP

            -- TRAE LA DISPONIBILIDAD
                MI_TIPO_CPTE_AFECT  :=MI_RS2.TIPO_CPTE_AFECT;
                MI_CMPTE_AFECTADO   :=MI_RS2.CMPTE_AFECTADO;
            --    MI_CONSECUTIVOPPTO  :=MI_RS2.CONSECUTIVOPPTO; 

             BEGIN
                 SELECT TIPO_CPTE_AFECT,
                       CMPTE_AFECTADO
                 INTO MI_TIPO_DISPON,
                      MI_NUMERO_DISPON
                FROM  DETALLE_COMPROBANTE_PPTAL DCP  -- SE CAMBIO LA TABLA COMPROBANTE_PPTAL POR DETALLE_COMPROBANTE_PPTAL DEBIDO A QUE CUANDO ES RESERVA DE CAJA O DE VIGENCIA ANTERIOR NO HEREDA EN EL COMPROBANTE EL TIPO AFECTADO
                WHERE DCP.COMPANIA    =MI_RS2.COMPANIA
                  AND DCP.TIPO_CPTE   =MI_RS2.TIPO_CPTE_AFECT
                  AND DCP.COMPROBANTE =MI_RS2.CMPTE_AFECTADO
                  AND DCP.TIPO_CPTE_AFECT IS NOT NULL
                  AND DCP.CMPTE_AFECTADO  IS NOT NULL
                  AND ROWNUM<=1;
           EXCEPTION WHEN NO_DATA_FOUND THEN
                       MI_TIPO_DISPON:='';
                       MI_NUMERO_DISPON:=0;
          END;  

        -- TRAE EL NUMERO DEL EGRESO DE GIRO
               BEGIN
                   SELECT   DCP.TIPO_CPTE, 
                            DCP.COMPROBANTE, 
                            DCP.CONSECUTIVO,
                            DCP.FECHA
                   INTO 
                            MI_TIPO_E,
                            MI_COMPROBANTE_E,
                            MI_CONSECUTIVO_E,
                            MI_FECHA_E
                   FROM     DETALLE_COMPROBANTE_PPTAL DCP
                   WHERE    DCP.COMPANIA        =MI_RS2.COMPANIA
                       AND  DCP.TIPO_CPTE_AFECT =MI_RS2.TIPO
                        AND DCP.CMPTE_AFECTADO  =MI_RS2.NUMERO
        --                AND DCP.CONSECUTIVOPPTO=MI_RS2.CONSECUTIVO
                        AND ROWNUM<=1;
               EXCEPTION WHEN NO_DATA_FOUND THEN
                            MI_TIPO_E:='';
                            MI_COMPROBANTE_E:='';
                            MI_CONSECUTIVO_E:='';
                            MI_FECHA_E:=SYSDATE();
               END;   

    -- TRAE EL RUBRO EQUIVALENTE
    /*
        IF MI_RS2.TIPOVIGENCIA='VA' THEN
             BEGIN
                  SELECT RUBRO_GUION,
                            FUENTE,
                            RUBRO
                  INTO   MI_RUBRO,
                            MI_FUENTE,
                            MI_RUBROFUT
                  FROM   RUBRO
                  WHERE  RUBRO=MI_RS2.CUENTA
                    AND ROWNUM<=1;
                  EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_RUBRO:='';
                    MI_RUBROFUT:='';
              END;
          ELSE  
              BEGIN
                  SELECT RUBRO_GUION,
                   FUENTE,
                   RUBRO
                  INTO   MI_RUBRO,
                   MI_FUENTE,
                   MI_RUBROFUT
                  FROM   RUBRO_RESERVA
                  WHERE  RUBRO=MI_RS2.CUENTA
                    AND ROWNUM<=1;
          EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_RUBRO:='';
                    MI_RUBROFUT:='';
              END;
          END IF;	  
    */               

		MI_RUBRO   :=NVL(MI_RS2.RUBRO_GUIONR,'');
      	MI_FUENTE  :=NVL(MI_RS2.FUENTER,'');
      	MI_RUBROFUT:=NVL(MI_RS2.RUBROR,'');
        MI_STR:=
              MI_RS2.TIPO_REGISTRO || ';' 
             || TRIM(TO_CHAR(MI_CONSECUTIVO,'000000')) || ';' 
             || MI_VIGENCIA || ';' 
             || NVL(MI_RS2.ENTIDAD, MI_ENTIDAD) || ';' 
             || NVL(MI_RS2.UNIDAD_EJECUTORA, MI_UNIDADEJECUTORA) || ';'
             || MI_RS2.TIPODOCPAGO || ';' 
             || TRIM(NVL(MI_RUBRO,' ')) || ';' 
             || MI_RS2.VIGENCIA || ';' 
             || SUBSTR(LPAD(MI_NUMERO_DISPON, 12),7,6) || ';' 
             || SUBSTR(LPAD(MI_NUMERO_REGISTRO, 12),7,6) || ';' 
             || TRIM(TO_CHAR(NVL(MI_FECHA_E,MI_RS2.FECHA),'RRRR')) || ';'
             || TRIM(TO_CHAR(NVL(MI_FECHA_E,MI_RS2.FECHA),'MM')) || ';' 
             || NVL(MI_CODIGO_SGC,'') || ';' 
             || MI_RS1.NUMEROCONTRATO||';' --|| MI_RS2.NUMERO || ';' 
             || MI_RS2.VALORNETO || ';';   

      MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
      MI_REGISTROS:=MI_REGISTROS+1;

    --|| SUBSTR(LPAD(MI_RS2.CMPTE_AFECTADO, 12),7,6) || ';'                     

        END LOOP;

-- BARRE CURSOR 3    

    FOR MI_RS3 IN (
           SELECT 3 TIPO_REGISTRO,
                  DCP.COMPANIA, 
                  PP.TIPOVIGENCIA, 
                  PP.VIGENCIA, 
                  C.ENTIDAD, 
                  C.UNIDAD_EJECUTORA, 
                  'OP' TIPODOCPAGO, 
                  DCP.TIPO_CPTE_AFECT, 
                  DCP.CMPTE_AFECTADO, 
                  CP.FECHA, 
                  CP.TIPO,
                  CP.NUMERO,
                  'C' TIPODOCPAGA,
                  1 AS ITEM,
                  --RPAD(P.RUBRO_GUION ,32) RUBRO_GUIONR,
                  CASE WHEN P.ANO<2022 THEN RPAD(P.RUBRO_GUION,32) ELSE RPAD(P.RUBRO_GUION,100) END RUBRO_GUIONR,
                  P.FUENTE_RECURSO      FUENTER,
                  --RPAD(P.CODIGO,32) RUBROR,
                  CASE WHEN P.ANO<2022 THEN RPAD(P.CODIGO,32) ELSE RPAD(P.CODIGO,100) END RUBROR,
                  SUM(DCP.VALOR_DEBITO-DCP.VALOR_CREDITO) VALORNETO
                FROM DETALLE_COMPROBANTE_PPTAL  DCP 
                  INNER JOIN TIPO_COMPROBPP TC 
                          ON  DCP.COMPANIA = TC.COMPANIA 
                         AND  DCP.TIPO_CPTE = TC.CODIGO  
                  INNER JOIN PLAN_PRESUPUESTAL  PP 
                          ON DCP.COMPANIA = PP.COMPANIA 
                         AND DCP.ANO = PP.ANO 
                         AND DCP.CUENTA = PP.CODIGO
                  INNER JOIN COMPANIA  C 
                          ON DCP.COMPANIA = C.CODIGO
                  INNER JOIN COMPROBANTE_PPTAL  CP 
                          ON DCP.COMPANIA = CP.COMPANIA 
                         AND DCP.ANO = CP.ANO  
                         AND DCP.TIPO_CPTE = CP.TIPO 
                         AND DCP.COMPROBANTE = CP.NUMERO
                   LEFT JOIN PLAN_PPTAL_CONFIG P 
                          ON DCP.COMPANIA = P.COMPANIA
                         AND DCP.ANO = P.ANO
                         AND DCP.CUENTA=P.CODIGO  
               WHERE DCP.COMPANIA    =MI_RS1.COMPANIA
                 AND DCP.TIPO_CPTE   =MI_RS1.TIPO   --TIPODEPRESUPUESTO --MI_TIPO_AFECT
                 AND DCP.COMPROBANTE =MI_RS1.NUMERO    --REGISTRO  --MI_NUMERO_AFECT  
              GROUP BY
                  3 ,
                  DCP.COMPANIA, 
                  PP.TIPOVIGENCIA, 
                  PP.VIGENCIA, 
                  C.ENTIDAD, 
                  C.UNIDAD_EJECUTORA, 
                  'OP', 
                  DCP.TIPO_CPTE_AFECT, 
                  DCP.CMPTE_AFECTADO, 
                  CP.FECHA, 
                  CP.TIPO,
                  CP.NUMERO,
                  'C' ,
                  1 ,
                  CASE WHEN P.ANO<2022 THEN RPAD(P.RUBRO_GUION,32) ELSE RPAD(P.RUBRO_GUION,100) END,
                  P.FUENTE_RECURSO      ,
                  CASE WHEN P.ANO<2022 THEN RPAD(P.CODIGO,32) ELSE RPAD(P.CODIGO,100) END
--          ORDER BY TIPO,NUMERO,FECHA 
      )
      LOOP
            -- TRAE LA DISPONIBILIDAD
            /*
                   BEGIN
                       SELECT   DCP.TIPO_CPTE_AFECT, 
                                DCP.CMPTE_AFECTADO, 
                                DCP.CONSECUTIVOPPTO
                       INTO 
                                MI_TIPO_CPTE_AFECT,
                                MI_CMPTE_AFECTADO,
                                MI_CONSECUTIVOPPTO

                       FROM     DETALLE_COMPROBANTE_PPTAL DCP
                       WHERE    DCP.COMPANIA    =MI_RS3.COMPANIA
                           AND  DCP.TIPO_CPTE   =MI_RS3.TIPO_CPTE_AFECT
                            AND DCP.COMPROBANTE =MI_RS3.CMPTE_AFECTADO
                            AND DCP.CONSECUTIVO =MI_RS3.CONSECUTIVOPPTO;
                   EXCEPTION WHEN NO_DATA_FOUND THEN
                                MI_TIPO_CPTE_AFECT:='';
                                MI_CMPTE_AFECTADO:=0;
                                MI_CONSECUTIVOPPTO:=0;
                   END;
            */
            -- TRAE EL NUMERO DEL EGRESO DE GIRO
               BEGIN
                     SELECT   DCP.TIPO_CPTE, 
                              DCP.COMPROBANTE, 
                              DCP.CONSECUTIVO,
                              DCP.FECHA
                     INTO 
                              MI_TIPO_E,
                              MI_COMPROBANTE_E,
                              MI_CONSECUTIVO_E,
                              MI_FECHA_E
                     FROM     DETALLE_COMPROBANTE_PPTAL DCP
                     WHERE    DCP.COMPANIA        =MI_RS3.COMPANIA
                          AND  DCP.TIPO_CPTE_AFECT =MI_RS3.TIPO
                          AND DCP.CMPTE_AFECTADO  =MI_RS3.NUMERO
        --                  AND DCP.CONSECUTIVOPPTO=MI_RS3.CONSECUTIVO
                          AND ROWNUM<=1;
               EXCEPTION WHEN NO_DATA_FOUND THEN
                              MI_TIPO_E:='';
                              MI_COMPROBANTE_E:=0;
                              MI_CONSECUTIVO_E:=0;
                              MI_FECHA_E:=CURRENT_DATE;
               END;

        -- TRAE EL RUBRO EQUIVALENTE
        /*
            IF MI_RS3.TIPOVIGENCIA='VA' THEN
                 BEGIN
                      SELECT RUBRO_GUION,
                                 FUENTE,
                                 RUBRO
                      INTO   MI_RUBRO,
                                MI_FUENTE,
                                MI_RUBROFUT
                      FROM   RUBRO
                      WHERE  RUBRO=MI_RS3.CUENTA
                        AND ROWNUM<=1;
                      EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_RUBRO:='';
                        MI_RUBROFUT:='';
                  END;
              ELSE  
                  BEGIN
                      SELECT RUBRO_GUION,
                       FUENTE,
                       RUBRO
                      INTO   MI_RUBRO,
                       MI_FUENTE,
                       MI_RUBROFUT
                      FROM   RUBRO_RESERVA
                      WHERE  RUBRO=MI_RS3.CUENTA
                        AND ROWNUM<=1;
              EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_RUBRO:='';
                        MI_RUBROFUT:='';
                  END;
              END IF;	  
        */          

             MI_RUBRO   :=NVL(MI_RS3.RUBRO_GUIONR,'');
             MI_FUENTE  :=NVL(MI_RS3.FUENTER,'');
             MI_RUBROFUT:=NVL(MI_RS3.RUBROR,'');     

         BEGIN
                SELECT TIPO_CPTE_AFECT,
                    CMPTE_AFECTADO
                INTO
                    MI_TIPO_DISPON,
                    MI_NUMERO_DISPON
                FROM 
                    DETALLE_COMPROBANTE_PPTAL DCP 
                WHERE DCP.COMPANIA    =MI_RS3.COMPANIA
                  AND DCP.TIPO_CPTE   =MI_RS3.TIPO_CPTE_AFECT
                  AND DCP.COMPROBANTE =MI_RS3.CMPTE_AFECTADO
                  AND DCP.TIPO_CPTE_AFECT IS NOT NULL
                  AND DCP.CMPTE_AFECTADO  IS NOT NULL
                 AND ROWNUM<=1;
           EXCEPTION WHEN NO_DATA_FOUND THEN
                       MI_TIPO_DISPON:='';
                       MI_NUMERO_DISPON:=0;
         END;             

         MI_STR:=
              MI_RS3.TIPO_REGISTRO || ';' 
             || TRIM(TO_CHAR(MI_CONSECUTIVO,'000000')) || ';' 
             || MI_VIGENCIA || ';' 
             || NVL(MI_RS3.ENTIDAD, MI_ENTIDAD) || ';' 
             || NVL(MI_RS3.UNIDAD_EJECUTORA, MI_UNIDADEJECUTORA) || ';'
             || MI_RS3.TIPODOCPAGO || ';' 
             || TRIM(NVL(MI_RUBRO,' ')) || ';' 
             || MI_RS3.VIGENCIA || ';' 
             || SUBSTR(LPAD(MI_RS3.CMPTE_AFECTADO, 12),7,6) || ';' 
             || SUBSTR(LPAD(MI_NUMERO_DISPON, 12),7,6) || ';' 
             || MI_RS3.TIPODOCPAGA || ';' 
             || MI_RS1.NUMERO || ';' 
             || MI_RS3.ITEM || ';' 
             || MI_RS1.TIPODOCIDENTIFICACION || ';' 
             || MI_RS1.NIT || ';'
             || CASE WHEN INSTR(MI_RS1.CODIGOICA,',')>0 THEN TRIM(SUBSTR(MI_RS1.CODIGOICA,1,INSTR(MI_RS1.CODIGOICA,',')-1)) ELSE  TRIM(MI_RS1.CODIGOICA) END || ';' 
             || TO_CHAR(MI_RS3.FECHA,'DD/MM/RRRR') || ';' 
             || MI_RS1.CODCONT_VLRBRUTO || ';' 
             || '' || ';' 
             || '' || ';' 
             || '' || ';' 
             || '' || ';' 
             || 'N' || ';';

        --             || SUBSTR(LPAD(MI_RS3.CMPTE_AFECTADO, 12),7,6) || ';'          

          MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;

          MI_REGISTROS:=MI_REGISTROS+1;              

       END LOOP;

-- BARRE CURSOR 4
       FOR MI_RS4 IN 
                  (SELECT
                   4 TIPO_REGISTRO,
                   1 ITEM,
                   CC.COMPANIA,
                   CC.ANO, 
                   CC.TIPO, 
                   CC.NUMERO, 
                   C.ENTIDAD, 
                   C.UNIDAD_EJECUTORA, 
                   'OP' TIPODOCPAGO,
                   CC.FECHA,
                   P.COD_EQUIV CUENTA,
                   CASE WHEN P.CLASECUENTA IN ('I') THEN DC.VALOR_CREDITO-DC.VALOR_DEBITO   ELSE 0 END  DESCUENTO_IMPUESTO,
                   CASE WHEN P.CLASECUENTA IN ('I') THEN DC.BASE_GRAVABLE   ELSE 0 END   TBASE_GRAVABLE,
                   CASE WHEN P.CLASECUENTA IN ('I') AND DC.BASE_GRAVABLE NOT IN (0) THEN ROUND((DC.VALOR_CREDITO-DC.VALOR_DEBITO)/DC.BASE_GRAVABLE,5)   ELSE 0 END TPORCENTAJE                     
                   FROM COMPROBANTE_CNT   CC 
               INNER JOIN COMPANIA          C   
                       ON CC.COMPANIA=C.CODIGO
               INNER JOIN TIPO_COMPROBANTE  TC  
                       ON CC.COMPANIA = TC.COMPANIA 
                      AND CC.TIPO = TC.CODIGO
               INNER JOIN DETALLE_COMPROBANTE_CNT DC 
                       ON CC.COMPANIA=DC.COMPANIA 
                      AND CC.TIPO=DC.TIPO_CPTE 
                      AND CC.NUMERO=DC.COMPROBANTE
               INNER JOIN PLAN_CONTABLE P 
                       ON P.COMPANIA=DC.COMPANIA 
                      AND P.ANO=DC.ANO 
                      AND P.CODIGO=DC.CUENTA
             WHERE  CC.COMPANIA   =MI_RS1.COMPANIA 
               AND  DC.TIPO_CPTE  =MI_RS1.TIPO
               AND  DC.COMPROBANTE=MI_RS1.NUMERO
               AND  P.CLASECUENTA IN ('I')
               AND  DC.VALOR_DEBITO-DC.VALOR_CREDITO NOT IN (0)
            )LOOP       
                -- TRAE LOS DATOS DEL REGISTRO
                /*
                        BEGIN
                               SELECT DCP.TIPO_CPTE_AFECT, 
                                      DCP.CMPTE_AFECTADO, 
                                      DCP.CONSECUTIVOPPTO,
                                      DCP.CODIGO_CUENTA          
                               INTO
                                    MI_TIPO_CPTE_AFECT1,
                                    MI_CMPTE_AFECTADO1,
                                    MI_CONSECUTIVOPPTO1,
                                    MI_CUENTAPPTO 
                               FROM DETALLE_COMPROBANTE_PPTAL  DCP 
                               WHERE DCP.COMPANIA    =MI_RS4.COMPANIA
                                 AND DCP.TIPO_CPTE   =MI_RS4.TIPO  
                                 AND DCP.COMPROBANTE =MI_RS4.NUMERO
                                 AND ROWNUM<=1;
                       EXCEPTION WHEN NO_DATA_FOUND THEN
                                    MI_TIPO_CPTE_AFECT1:='';
                                    MI_CMPTE_AFECTADO1:=0;
                                    MI_CONSECUTIVOPPTO1:=0;
                                    MI_CUENTAPPTO:='';
                       END;

                -- TRAE LA DISPONIBILIDAD
                       BEGIN
                           SELECT   DCP.TIPO_CPTE_AFECT, 
                                    DCP.CMPTE_AFECTADO, 
                                    DCP.CONSECUTIVOPPTO,
                                    DCP.CODIGO_CUENTA
                           INTO 
                                    MI_TIPO_CPTE_AFECT,
                                    MI_CMPTE_AFECTADO,
                                    MI_CONSECUTIVOPPTO,
                                    MI_CUENTAPPTO
                           FROM     DETALLE_COMPROBANTE_PPTAL DCP
                           WHERE    DCP.COMPANIA    =MI_RS4.COMPANIA
                               AND  DCP.TIPO_CPTE   =MI_TIPO_CPTE_AFECT1
                                AND DCP.COMPROBANTE =MI_CMPTE_AFECTADO1
                                AND DCP.CONSECUTIVO =MI_CONSECUTIVOPPTO1;
                        EXCEPTION WHEN NO_DATA_FOUND THEN
                                    MI_TIPO_CPTE_AFECT:='';
                                    MI_CMPTE_AFECTADO:=0;
                                    MI_CONSECUTIVOPPTO:=0;
                                    MI_CUENTAPPTO:='';
                        END;
                */
                -- TRAE TIPO DE VIGENCIA
                /*
                        BEGIN
                           SELECT 
                --                 P.VIGENCIA,
                                 P.TIPOVIGENCIA
                           INTO
                                 MI_TIPOVIGENCIA
                           FROM 
                                DETALLE_COMPROBANTE_PPTAL DCP 
                                INNER JOIN PLAN_PRESUPUESTAL P ON (DCP.COMPANIA=P.COMPANIA) AND (DCP.ANO=P.ANO) AND (DCP.CUENTA=P.ID)
                           WHERE DCP.COMPANIA    =MI_RS1.COMPANIA
                             AND DCP.TIPO_CPTE   =MI_RS1.TIPO  --TIPODEPRESUPUESTO
                             AND DCP.COMPROBANTE =MI_RS1.NUMERO  --REGISTRO
                             AND ROWNUM <=1;
                        EXCEPTION WHEN NO_DATA_FOUND THEN
                                 MI_TIPOVIGENCIA:='';
                       END;        
                */
                -- TRAE EL RUBRO EQUIVALENTE
                /*
                      BEGIN
                          SELECT RUBRO_GUION
                          INTO   MI_RUBRO
                          FROM   RUBRO
                          WHERE  RUBRO=MI_CUENTAPPTO
                            AND ROWNUM<=1;
                      EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_RUBRO:='';
                      END;
                */      

           MI_STR:=
              MI_RS4.TIPO_REGISTRO || ';' 
             || TRIM(TO_CHAR(MI_CONSECUTIVO,'000000')) || ';' 
             || NVL(MI_VIGENCIA,'') || ';' 
             || NVL(MI_RS4.ENTIDAD, MI_ENTIDAD) || ';' 
             || NVL(MI_RS4.UNIDAD_EJECUTORA, MI_UNIDADEJECUTORA) || ';'
             || MI_RS4.TIPODOCPAGO || ';' 
             || TRIM(NVL(MI_RUBRO,' ')) || ';' 
             || NVL(MI_VIGENCIA,'') || ';' 
             || SUBSTR(LPAD(MI_NUMERO_DISPON, 12),7,6) || ';' 
             || SUBSTR(LPAD(MI_NUMERO_REGISTRO, 12),7,6) || ';' 
             || MI_RS1.NUMERO || ';' 
             || MI_RS4.ITEM || ';' 
             || TRIM(MI_RS4.CUENTA)|| ';' 
             || MI_RS4.TBASE_GRAVABLE || ';' 
             || TRIM(TO_CHAR(ROUND(MI_RS4.TPORCENTAJE*100,3),'990.999')) || ';' 
             || MI_RS4.DESCUENTO_IMPUESTO || ';';

        --|| TRIM(REPLACE(TO_CHAR(LPAD(MI_RS4.CUENTA,16),'0,0,00,00,000,000,0000'),',','-')) || ';' 

              MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
              MI_REGISTROS:=MI_REGISTROS+1;            

       END LOOP; 

            -- BARRE CURSOR 5

            FOR MI_RS5 IN 
                  (SELECT 5 TIPO_REGISTRO,
                      DCP.COMPANIA, 
                      PP.VIGENCIA, 
                      PP.TIPOVIGENCIA, 
                      C.ENTIDAD, 
                      C.UNIDAD_EJECUTORA, 
                      'OP' TIPODOCPAGO, 
                      DCP.TIPO_CPTE_AFECT, 
                      DCP.CMPTE_AFECTADO, 
                      CP.FECHA, 
                      CP.TIPO,
                      CP.NUMERO,
                      0 CODIGOTIPO,
                      0 COD_COMPONENTE,                  
                      0 COD_OBJETO,
                      '     ' ENDOSO,
                      0 VALORENDOSO,
                      '     ' DETALLE_FUENTE,
                      --RPAD(P.RUBRO_GUION ,32) RUBRO_GUIONR,
                      CASE WHEN P.ANO<2022 THEN RPAD(P.RUBRO_GUION,32) ELSE RPAD(P.RUBRO_GUION,100) END  RUBRO_GUIONR,
                      P.FUENTE_RECURSO FUENTER,
                      --RPAD(P.CODIGO,32) RUBROR,
                      CASE WHEN P.ANO<2022 THEN RPAD(P.CODIGO,32) ELSE RPAD(P.CODIGO,100) END RUBROR,
                      TRIM(SUBSTR(CASE WHEN P.ANO<2022 THEN RPAD(P.RUBRO_GUION,32) ELSE RPAD(P.RUBRO_GUION,100) END ,1,INSTR(CASE WHEN P.ANO<2022 THEN RPAD(P.RUBRO_GUION,32) ELSE RPAD(P.RUBRO_GUION,100) END ,'-',-1))) 
                      ||SUBSTR(DCP.CUENTA,CASE WHEN INSTR(TRIM(DCP.CUENTA),' ',-1)>INSTR(TRIM(DCP.CUENTA),'-',-1) THEN INSTR(TRIM(DCP.CUENTA),' ',-1) ELSE INSTR(TRIM(DCP.CUENTA),'-',-1) END+1,LENGTH(TRIM(DCP.CUENTA))) RUBROFUT1,
    --                  TRIM(SUBSTR(RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN RUBRO.RUBRO_GUION ELSE RUBRO_RESERVA.RUBRO_GUION END,32),1,INSTR(RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN RUBRO.RUBRO_GUION ELSE RUBRO_RESERVA.RUBRO_GUION END,32),'-',-1))) ABC,
    --                   SUBSTR(DCP.CUENTA,CASE WHEN INSTR(TRIM(DCP.CUENTA),' ',-1)>INSTR(TRIM(DCP.CUENTA),'-',-1) THEN INSTR(TRIM(DCP.CUENTA),' ',-1) ELSE INSTR(TRIM(DCP.CUENTA),'-',-1) END+1,LENGTH(TRIM(DCP.CUENTA))) XYZ,
    --                  DCP.CUENTA,
                      SUM(DCP.VALOR_DEBITO-DCP.VALOR_CREDITO) VALORNETO
    --                ,DCP.CONSECUTIVO
                    FROM DETALLE_COMPROBANTE_PPTAL  DCP 
                      INNER JOIN TIPO_COMPROBPP TC 
                              ON DCP.COMPANIA = TC.COMPANIA 
                             AND DCP.TIPO_CPTE = TC.CODIGO
                      INNER JOIN PLAN_PRESUPUESTAL  PP 
                              ON DCP.COMPANIA = PP.COMPANIA 
                              AND DCP.ANO = PP.ANO 
                              AND DCP.CUENTA = PP.CODIGO
                      INNER JOIN COMPANIA  C 
                              ON DCP.COMPANIA = C.CODIGO
                      INNER JOIN COMPROBANTE_PPTAL  CP 
                              ON DCP.COMPANIA = CP.COMPANIA 
                             AND DCP.ANO = CP.ANO  
                             AND DCP.TIPO_CPTE = CP.TIPO 
                             AND DCP.COMPROBANTE = CP.NUMERO
                       LEFT JOIN PLAN_PPTAL_CONFIG P 
                              ON DCP.COMPANIA = P.COMPANIA
                             AND DCP.ANO = P.ANO
                             AND DCP.CUENTA=P.CODIGO  
                   WHERE DCP.COMPANIA    =MI_RS1.COMPANIA
                     AND DCP.TIPO_CPTE   =MI_RS1.TIPO   --TIPODEPRESUPUESTO MI_TIPO_AFECT
                     AND DCP.COMPROBANTE =MI_RS1.NUMERO    --REGISTRO  MI_NUMERO_AFECT
                   GROUP BY
                       5 ,
                      DCP.COMPANIA, 
                      PP.VIGENCIA, 
                      PP.TIPOVIGENCIA, 
                      C.ENTIDAD, 
                      C.UNIDAD_EJECUTORA, 
                      'OP' , 
                      DCP.TIPO_CPTE_AFECT, 
                      DCP.CMPTE_AFECTADO, 
                      CP.FECHA, 
                      CP.TIPO,
                      CP.NUMERO,
                      0 ,
                      0 ,                  
                      0 ,
                      '     ' ,
                      0 ,
                      '     ' ,
                      CASE WHEN P.ANO<2022 THEN RPAD(P.RUBRO_GUION,32) ELSE RPAD(P.RUBRO_GUION,100) END ,
                      P.FUENTE_RECURSO ,
                      CASE WHEN P.ANO<2022 THEN RPAD(P.CODIGO,32) ELSE RPAD(P.CODIGO,100) END ,
                      TRIM(SUBSTR(CASE WHEN P.ANO<2022 THEN RPAD(P.RUBRO_GUION,32) ELSE RPAD(P.RUBRO_GUION,100) END ,1,INSTR(CASE WHEN P.ANO<2022 THEN RPAD(P.RUBRO_GUION,32) ELSE RPAD(P.RUBRO_GUION,100) END ,'-',-1))) 
                      ||SUBSTR(DCP.CUENTA,CASE WHEN INSTR(TRIM(DCP.CUENTA),' ',-1)>INSTR(TRIM(DCP.CUENTA),'-',-1) THEN INSTR(TRIM(DCP.CUENTA),' ',-1) ELSE INSTR(TRIM(DCP.CUENTA),'-',-1) END+1,LENGTH(TRIM(DCP.CUENTA)))
    --                ,  TRIM(SUBSTR(RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN RUBRO.RUBRO_GUION ELSE RUBRO_RESERVA.RUBRO_GUION END,32),1,INSTR(RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN RUBRO.RUBRO_GUION ELSE RUBRO_RESERVA.RUBRO_GUION END,32),'-',-1))),
    --                  SUBSTR(DCP.CUENTA,CASE WHEN INSTR(TRIM(DCP.CUENTA),' ',-1)>INSTR(TRIM(DCP.CUENTA),'-',-1) THEN INSTR(TRIM(DCP.CUENTA),' ',-1) ELSE INSTR(TRIM(DCP.CUENTA),'-',-1) END+1,LENGTH(TRIM(DCP.CUENTA)))

    --          ORDER BY TIPO,NUMERO,FECHA 
                  )LOOP    
                -- TRAE LA DISPONIBILIDAD
                /*
                       BEGIN
                           SELECT   DCP.TIPO_CPTE_AFECT, 
                                    DCP.CMPTE_AFECTADO, 
                                    DCP.CONSECUTIVOPPTO
                           INTO 
                                    MI_TIPO_CPTE_AFECT,
                                    MI_CMPTE_AFECTADO,
                                    MI_CONSECUTIVOPPTO

                           FROM     DETALLE_COMPROBANTE_PPTAL DCP
                           WHERE    DCP.COMPANIA    =MI_RS5.COMPANIA
                               AND  DCP.TIPO_CPTE   =MI_RS5.TIPO_CPTE_AFECT
                                AND DCP.COMPROBANTE =MI_RS5.CMPTE_AFECTADO
                                AND DCP.CONSECUTIVO =MI_RS5.CONSECUTIVOPPTO;
                        EXCEPTION WHEN NO_DATA_FOUND THEN
                                MI_TIPO_CPTE_AFECT:='';
                                MI_CMPTE_AFECTADO:=0;
                                MI_CONSECUTIVOPPTO:=0;
                        END;
                */
                      MI_RUBRO   :=NVL(MI_RS5.RUBRO_GUIONR,'');
                      MI_FUENTE  :=NVL(MI_RS5.FUENTER,'');
                      MI_RUBROFUT:=NVL(MI_RS5.RUBROR,'');

                BEGIN
                     SELECT TIPO_CPTE_AFECT,
                            CMPTE_AFECTADO
                     INTO MI_TIPO_DISPON,
                           MI_NUMERO_DISPON
                    FROM DETALLE_COMPROBANTE_PPTAL DCP 
                    WHERE DCP.COMPANIA    =MI_RS5.COMPANIA
                      AND DCP.TIPO_CPTE   =MI_RS5.TIPO_CPTE_AFECT
                      AND DCP.COMPROBAnte =MI_RS5.CMPTE_AFECTADO
                      AND DCP.TIPO_CPTE_AFECT IS NOT NULL
                      AND DCP.CMPTE_AFECTADO  IS NOT NULL
                     AND ROWNUM<=1;
               EXCEPTION WHEN NO_DATA_FOUND THEN
                      MI_TIPO_DISPON:='';
                       MI_NUMERO_DISPON:=0;
                  END; 

        -- TRAE EL NUMERO DEL EGRESO DE GIRO

              BEGIN
               SELECT   DCP.TIPO_CPTE, 
                        DCP.COMPROBANTE, 
                        DCP.CONSECUTIVO,
                        DCP.FECHA
               INTO 
                        MI_TIPO_E,
                        MI_COMPROBANTE_E,
                        MI_CONSECUTIVO_E,
                        MI_FECHA_E
               FROM     DETALLE_COMPROBANTE_PPTAL DCP
               WHERE    DCP.COMPANIA        =MI_RS5.COMPANIA
                   AND  DCP.TIPO_CPTE_AFECT =MI_RS5.TIPO
                    AND DCP.CMPTE_AFECTADO  =MI_RS5.NUMERO
        --            AND DCP.CONSECUTIVOPPTO= MI_RS5.CONSECUTIVO
                    AND ROWNUM<=1;
              EXCEPTION WHEN NO_DATA_FOUND THEN
                     MI_TIPO_E:='';
                     MI_COMPROBANTE_E:='';
                     MI_CONSECUTIVO_E:='';
                     MI_FECHA_E:=SYSDATE();
              END;  


        -- TRAE EL RUBRO EQUIVALENTE
        /*
            IF MI_RS5.TIPOVIGENCIA='VA' THEN
                 BEGIN
                      SELECT RUBRO_GUION,
                                FUENTE,
                                RUBRO
                      INTO   MI_RUBRO,
                                MI_FUENTE,
                                MI_RUBROFUT
                      FROM   RUBRO
                      WHERE  RUBRO=MI_RS5.CUENTA
                        AND ROWNUM<=1;
                      EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_RUBRO:='';
                        MI_RUBROFUT:='';
                  END;
              ELSE  
                  BEGIN
                      SELECT RUBRO_GUION,
                       FUENTE,
                       RUBRO
                      INTO   MI_RUBRO,
                       MI_FUENTE,
                       MI_RUBROFUT
                      FROM   RUBRO_RESERVA
                      WHERE  RUBRO=MI_RS5.CUENTA
                        AND ROWNUM<=1;
              EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_RUBRO:='';
                        MI_RUBROFUT:='';
                  END;
              END IF;	  
        */
        -- TRAE EL RUBRO FUT -- SE CAMBIO EN MARZO 1 POR SOLICITUD DE IDIPRON
        --      MI_INTI:=INSTR(TRIM(MI_RUBRO),'-',-1);
        --      MI_INTJ:=CASE WHEN INSTR(TRIM(MI_RS5.CUENTA),' ',-1)>INSTR(TRIM(MI_RS5.CUENTA),'-',-1) THEN INSTR(TRIM(MI_RS5.CUENTA),' ',-1) ELSE INSTR(TRIM(MI_RS5.CUENTA),'-',-1) END;
        --      MI_INTJ:=INSTR(MI_RUBROFUT,'-',-1);
        --      MI_RUBROFUT1:=TRIM(SUBSTR(MI_RUBRO,1,MI_INTI)||SUBSTR(MI_RS5.CUENTA,MI_INTJ+1,LENGTH(MI_RS5.CUENTA)));

        --      MI_RUBROFUT1:=TRIM(SUBSTR(RUBRO_GUIONR,1,INSTR(RUBRO_GUIONR,'-',-1))||MI_RS5.RRRR);

        --      MI_RUBROFUT1:=TRIM(SUBSTR(MI_RUBRO,1,MI_INTI-1)); --||SUBSTR(MI_RUBROFUT,MI_INTJ+1,LENGTH(MI_RUBROFUT)));             


          --      IF MI_RS5.TIPOVIGENCIA='VA' THEN

                  BEGIN
                        SELECT TIPO_GASTO,
                               COMPONENTE,
                               CONCEPTO,
                               DETALLE
                        INTO   MI_CODIGOTIPO        ,
                               MI_COD_COMPONENTE    ,                  
                               MI_COD_OBJETO        ,
                               MI_DETALLEFUENTE     
                        FROM   PLAN_PPTAL_CONFIG
                        WHERE  CODIGO=MI_RS5.RUBROFUT1   --MI_RUBROFUT1 -- RUBRO FUT MODIFICADO
                          AND ROWNUM<=1;
                  EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_CODIGOTIPO:='';
                            MI_COD_COMPONENTE:='';
                            MI_COD_OBJETO:=0;
                            MI_DETALLEFUENTE:=0;
                  END;

                  MI_STR:=
                  MI_RS5.TIPO_REGISTRO || ';' 
                 || TRIM(TO_CHAR(MI_CONSECUTIVO,'000000')) || ';' 
                 || MI_VIGENCIA || ';' 
                 || NVL(MI_RS5.ENTIDAD, MI_ENTIDAD) || ';' 
                 || NVL(MI_RS5.UNIDAD_EJECUTORA, MI_UNIDADEJECUTORA) || ';'
                 || MI_RS5.TIPODOCPAGO || ';' 
                 || TRIM(NVL(MI_RUBRO,' ')) || ';' 
                 || SUBSTR(LPAD(MI_NUMERO_DISPON, 12),7,6) || ';' 
                 || SUBSTR(LPAD(MI_RS5.CMPTE_AFECTADO, 12),7,6) || ';' 
                 || MI_CODIGOTIPO || ';' 
                 || MI_COD_COMPONENTE || ';' 
                 || MI_COD_OBJETO || ';' 
                 || NVL(MI_FUENTE,'') || ';' 
                 || MI_DETALLEFUENTE || ';'       
                 || MI_RS5.VALORNETO || ';';
    --             || '<RUBRO.->'||MI_RUBRO||'>'
    --             || '<RUBROFUT->'||MI_RUBROFUT||'>'
    --             || '<RUBROFUT1->'||MI_RS5.RUBROFUT1||'>';
    --             || '<RUBROFUT1->'||MI_RUBROFUT1||'>'
    --             || '<ABC->'||MI_RS5.ABC||'>'
    --             || '<XYZ->'||MI_RS5.XYZ||'>'
    --             || '<ABCXYZ->'||MI_RS5.ABC||MI_RS5.XYZ||'>';
    --             || SUBSTR(LPAD(MI_RS5.CMPTE_AFECTADO, 12),7,6) || ';' 

             MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
             MI_REGISTROS:=MI_REGISTROS+1;




            END LOOP;

           MI_ETAPA:='7.1';  
    END LOOP;
      MI_TF:=CURRENT_DATE;

     RETURN MI_RETORNO;

END FC_PLANOH_2018;

FUNCTION FC_PLANOH_ADI 
 (

    /*
    NAME                    : FC_PLANOH_ADI (En PLSQL ACCES  GENERAR_PLANO_HACIENDA_ADI)     
    AUTHORS                 : STEFANINI SYSMAN SAS
    AUTHORS MIGRACION       : CAMILO ANDRES PEREZ DUEÑAS 
    DATE MIGRADOR           : 12/01/2021
    TIME                    : 12:00 PM
    SOURCE MODULE           : 
    MODIFIER                :
    DATA MODIFIED           :
    TIME                    :
    DESCRIPCION             :
    MODIFICATIONS           :
    MODIFIER                :
    TIME                    :
    MODIFICATIONS           :
    
    
    @NAME  : generarPlanoHAdi
    @METHOD:  GET    
    */
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIAL  IN DATE, 
    UN_FECHAFINAL    IN DATE,
    UN_FUENTEINICIAL IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS, 
    UN_FUENTEFINAL   IN PCK_SUBTIPOS.TI_FUENTE_RECURSOS
 )
 RETURN CLOB AS 
  MI_V_LINEA            VARCHAR2(32000);
  MI_SESSION            NUMBER;
  MI_ENTIDAD            VARCHAR2(3)          :='214';
  MI_UNIDADEJECUTORA    VARCHAR2(2)          :='01';
  MI_FECHAINI           DATE                 :=UN_FECHAINICIAL;
  MI_FECHAFIN           DATE                 :=UN_FECHAFINAL;
  MI_FUENTEINI          PCK_SUBTIPOS.TI_FUENTE_RECURSOS :=NVL(UN_FUENTEINICIAL,' ');
  MI_FUENTEFIN          PCK_SUBTIPOS.TI_FUENTE_RECURSOS :=NVL(UN_FUENTEFINAL ,'99');
  MI_COMPANIA           VARCHAR2(11)         :=  NVL(UN_COMPANIA,'001');MI_ETAPA            VARCHAR2(3200);
  MI_RUTASALIDA         VARCHAR2(3200)       :='IDIPRON_'||TO_CHAR(MI_FECHAINI,'DDMMRRRR')||'_'||TO_CHAR(MI_FECHAFIN,'DDMMRRRR')||'.TXT';
  MI_RUTALOG            VARCHAR2(3200)       :='LOG_'||MI_RUTASALIDA;
  MI_VERSION            VARCHAR2(3200)       :='GENERACION DE ARCHIVO PLANO HACIENDA DISTRITAL - VERSION 2019 RELEASE 22 ';
  MI_TI                 DATE                 :=SYSDATE;
  MI_TF                 DATE                 :=SYSDATE;
  MI_CONSECUTIVO        NUMBER               :=0;
  MI_REGISTROS          NUMBER               :=0;
  MI_RS1                NUMBER;
  MI_RS2                NUMBER;
  MI_RS3                NUMBER;
  MI_RS4                NUMBER;
  MI_RS5                NUMBER;
  MI_RST                NUMBER;
  MI_TIPO_COMPROMISO    NUMBER;
  MI_NUMERO_COMPROMISO  NUMBER;
  MI_DETALLES           VARCHAR2(32000);
  MI_ORIGENFDL          VARCHAR2(3);
  MI_RUBRO              VARCHAR2(254);
  MI_FUENTE             VARCHAR2(64);
  MI_TIPO_CPTE_AFECT    VARCHAR2(3);
  MI_CMPTE_AFECTADO     NUMBER;
  MI_CONSECUTIVOPPTO    NUMBER;
  MI_TIPO_CPTE_AFECT1   VARCHAR2(3);
  MI_CMPTE_AFECTADO1    NUMBER;
  MI_CONSECUTIVOPPTO1   NUMBER;
  MI_CUENTAPPTO         VARCHAR2(32);
  MI_CODIGO_CUENTA      VARCHAR2(64);
  MI_TIPOVIGENCIA       VARCHAR2(2);
  MI_VIGENCIA           VARCHAR2(4);
  MI_VALORPRESUPUESTO   NUMBER;
  MI_SITUACIONFONDOS    VARCHAR2(1);
  MI_TIPOCONTRATO       VARCHAR2(3);
  MI_NUMEROCONTRATO     NUMBER;
  MI_CODIGO_SGC         VARCHAR2(64);
  MI_INTERVENTOR        VARCHAR2(230);
  MI_CUENTABANCARIA     VARCHAR2(32);
  MI_NOMBRECIUDAD       VARCHAR2(32);
  MI_TIPOCUENTABANCARIA VARCHAR2(32);
  MI_BANCOMEN           VARCHAR2(32);
  MI_TIPO_E             VARCHAR2(3);
  MI_COMPROBANTE_E      NUMBER;
  MI_CONSECUTIVO_E      NUMBER;
  MI_FECHA_E            DATE;
  MI_FECHA              DATE;
  MI_CODIGOTIPO         VARCHAR2(10);
  MI_COD_COMPONENTE     VARCHAR2(10);                  
  MI_COD_OBJETO         VARCHAR2(10);
  MI_DETALLEFUENTE      NUMBER;
  MI_VALOR_FUENTE       NUMBER;
  MI_RUBROFUT           VARCHAR2(254);
  MI_INTI               NUMBER;
  MI_INTJ               NUMBER;
  MI_RETORNO            CLOB                 :='';
  MI_STR                VARCHAR2(3200)       :='';
  MI_TIPO_AFECT         VARCHAR(3);
  MI_NUMERO_AFECT       NUMBER;
  MI_RUBROFUT1          VARCHAR2(254);
  MI_TIPO_REGISTRO      VARCHAR2(3);
  MI_NUMERO_REGISTRO    NUMBER;
  MI_TIPO_DISPON        VARCHAR2(3);
  MI_NUMERO_DISPON      NUMBER;
  MI_FUENTES            NUMBER               :=0;
  MI_TIPO_EGRESO        VARCHAR2(3);
  MI_NUMERO_EGRESO      NUMBER;
  MI_TIPO_AFECT1        VARCHAR(3);
  MI_NUMERO_AFECT1      NUMBER;
  MI_VALOR_P            VARCHAR2(3200)       := '';
  BEGIN
    MI_ETAPA:='1';
       
    SELECT SYS_CONTEXT('USERENV','SESSIONID') 
    INTO MI_SESSION
    FROM DUAL;
       -- VAMOS CON REGISTRO TIPO 1
    FOR MI_RS1 IN
           ( 
                    SELECT 
                            CC.COMPANIA,
                            CC.ANO, 
                            CC.TIPO, 
                            CC.NUMERO, 
                            C.ENTIDAD, 
                            C.UNIDAD_EJECUTORA, 
                            'OP'  TIPODOCPAGO,
                             CASE WHEN T.TIPOID = 'N' THEN  'NIT' ELSE '' END 
                             || CASE WHEN T.TIPOID = 'C' THEN 'CC' ELSE '' END 
                             || CASE WHEN T.TIPOID = 'T' THEN 'TI' ELSE '' END TIPODOCIDENTIFICACION,
                             T.NIT,
                            '' ORIGENFDL, 
                            CASE WHEN NVL(CC.PAGOTOTAL,0) = 0 THEN 'P' ELSE  'T' END DESC_EJECUTADO,
                            ''  ACTARECIBO,
                            SUM(CASE WHEN P.CLASECUENTA IN ('P','E')     THEN DC.VALOR_CREDITO-DC.VALOR_DEBITO   ELSE 0 END)   CODCONT_VLRNETO,
                            SUM(CASE WHEN P.CLASECUENTA IN ('G','V','R') THEN DC.VALOR_DEBITO -DC.VALOR_CREDITO  ELSE  0 END)  CODCONT_VLRBRUTO,
                            CASE WHEN NVL(CC.GIROELECTRONICO,0) <> 0 THEN 'A' ELSE 'A' END  FORMAPAGO,
                            CASE WHEN T.REGIMEN = 'C' THEN 'COMUN' ELSE CASE WHEN T.REGIMEN = 'S' THEN  'SIMPLIFICADO' ELSE CASE WHEN T.REGIMEN = 'E' THEN  'ESPECIAL' ELSE CASE WHEN T.REGIMEN = 'N' THEN  'NO REPORTA' ELSE  'AUTORRETENEDOR' END END END END  TREGIMEN, 
                            CC.DESCRIPCION,
                            T.DIRECCION, 
                            CASE WHEN T.NATURALEZA = 'N' THEN  'Persona Natural' ELSE CASE WHEN T.NATURALEZA = 'J' THEN 'Persona Juridica' ELSE '' END END  TNATURALEZA,
                            T.NOMBRE MUNICIPIO,
                            T.TELEFONOS  TELEFONO, 
                            1 AS TIPO_REGISTRO,
                            CC.FECHA,
                            CC.TIPOCONTRATO,
                            CC.NUMEROCONTRATO,
                            CC.TIPODEPRESUPUESTO,
                            CC.REGISTRO,
                            T.REGIMEN,
                            TRIM(LPAD(CC.TEXTO,32)) OBSERVAC,
                            '1' TIPODEORDEN,
                            T.SUCURSAL,
                            CC.NRO_DOCUMENTO,
                            T.ACTIVIDAD,
                            T.CODIGOICA
                     FROM COMPROBANTE_CNT   CC 
                       INNER JOIN COMPANIA          C   ON (CC.COMPANIA=C.CODIGO) 
                       INNER JOIN TIPO_COMPROBANTE  TC  ON (CC.COMPANIA = TC.COMPANIA) AND (CC.TIPO = TC.CODIGO) 
                       INNER JOIN DETALLE_COMPROBANTE_CNT DC ON (CC.COMPANIA=DC.COMPANIA) AND (CC.TIPO=DC.TIPO_CPTE) AND (CC.NUMERO=DC.COMPROBANTE) 
                       INNER JOIN PLAN_CONTABLE P ON (P.COMPANIA=DC.COMPANIA) AND (P.ANO=DC.ANO) AND (P.CODIGO=DC.CUENTA) 
                       INNER JOIN TERCERO       T ON (T.COMPANIA=CC.COMPANIA) AND (T.NIT=CC.TERCERO) AND (T.SUCURSAL=CC.SUCURSAL) 
                     WHERE CC.COMPANIA =MI_COMPANIA --AND CC.FECHA BETWEEN MI_FECHAINI AND MI_FECHAFIN 
                              AND TC.CLASE_CONTABLE IN('P') 
                              AND  (SELECT DISTINCT NVL(CCE.GIROELECTRONICO,0)   
                                    FROM DETALLE_COMPROBANTE_CNT   DCE 
                                         INNER JOIN COMPROBANTE_CNT CCE ON (DCE.COMPANIA=CCE.COMPANIA) AND (DCE.TIPO_CPTE=CCE.TIPO) AND (DCE.COMPROBANTE=CCE.NUMERO)
                                         INNER JOIN TIPO_COMPROBANTE  TCE  ON (CCE.COMPANIA = TCE.COMPANIA) AND (CCE.TIPO = TCE.CODIGO)
                                    WHERE  DCE.COMPANIA       =CC.COMPANIA  
                                      AND  DCE.TIPO_CPTE_AFECT=CC.TIPO  
                                      AND  DCE.CMPTE_AFECTADO =CC.NUMERO  
                                      AND  TCE.CLASE_CONTABLE IN('E')  
                                      AND  CCE.FECHA BETWEEN MI_FECHAINI AND MI_FECHAFIN  
                                    ) NOT IN (0) 
                     GROUP BY 
                            CC.COMPANIA,
                            CC.ANO, 
                            CC.TIPO, 
                            CC.NUMERO, 
                            C.ENTIDAD, 
                            C.UNIDAD_EJECUTORA, 
                            'OP' ,
                             CASE WHEN T.TIPOID = 'N' THEN  'NIT' ELSE '' END 
                             || CASE WHEN T.TIPOID = 'C' THEN 'CC' ELSE '' END 
                             || CASE WHEN T.TIPOID = 'T' THEN 'TI' ELSE '' END,
                            T.NIT,
                            '' , 
                            CASE WHEN NVL(CC.PAGOTOTAL,0) = 0 THEN 'P' ELSE 'T' END,
                            '' ,
                            CASE WHEN NVL(CC.GIROELECTRONICO,0) <> 0 THEN 'A' ELSE 'A' END ,
                            CASE WHEN T.REGIMEN = 'C' THEN 'COMUN' ELSE CASE WHEN T.REGIMEN = 'S' THEN  'SIMPLIFICADO' ELSE CASE WHEN T.REGIMEN = 'E' THEN  'ESPECIAL' ELSE CASE WHEN T.REGIMEN = 'N' THEN  'NO REPORTA' ELSE  'AUTORRETENEDOR' END END END END, 
                            CC.DESCRIPCION,
                            T.DIRECCION, 
                            CASE WHEN T.NATURALEZA = 'N' THEN  'Persona Natural' ELSE CASE WHEN T.NATURALEZA = 'J' THEN 'Persona Juridica' ELSE '' END END,
                            T.NOMBRE ,
                            T.TELEFONOS  , 
                            1,
                            CC.FECHA,
                            CC.TIPOCONTRATO,
                            CC.NUMEROCONTRATO,
                            CC.TIPODEPRESUPUESTO,
                            CC.REGISTRO,
                            T.REGIMEN,
                            TRIM(LPAD(CC.TEXTO,32)),
                            '',
                            T.SUCURSAL,
                            CC.NRO_DOCUMENTO,
                            T.ACTIVIDAD,
                            T.CODIGOICA
                      ORDER BY CC.TIPO,CC.NUMERO,CC.FECHA
            )
    LOOP
        BEGIN
          SELECT TOC.CODIGO_SGC,
                 OC.INTERVENTOR
          INTO
               MI_CODIGO_SGC,
               MI_INTERVENTOR
          FROM 
               ORDENDECOMPRA OC
               LEFT JOIN  TIPOORDENDECOMPRA TOC ON (TOC.COMPANIA=OC.COMPANIA) AND (TOC.CODIGO=OC.CLASEORDEN) 
            WHERE 
                OC.COMPANIA  =MI_RS1.COMPANIA 
            AND OC.CLASEORDEN=MI_RS1.TIPOCONTRATO
            AND OC.NUMERO    =MI_RS1.NUMEROCONTRATO;
        EXCEPTION WHEN  NO_DATA_FOUND  THEN
               MI_CODIGO_SGC:='';
               MI_INTERVENTOR:='';
        END;
        BEGIN        
            SELECT  TP.CUENTA,
                    CASE WHEN TP.TIPOCUENTA='A' THEN 'AHORROS' ELSE 'CORRIENTE' END TIPOCUENTA,
                    BN.BANCOMEN,
                    CI.NOMBRE
             INTO
                    MI_CUENTABANCARIA,
                    MI_TIPOCUENTABANCARIA,
                    MI_BANCOMEN,
                    MI_NOMBRECIUDAD
             FROM TERCERO T1
                LEFT  JOIN CIUDAD  CI  ON (T1.PAIS = CI.PAIS) AND (T1.DEPARTAMENTO = CI.DEPARTAMENTO) AND (T1.CIUDAD = CI.CODIGO)
                LEFT  JOIN TERCEROPAGOS TP  ON (T1.COMPANIA = TP.COMPANIA) AND (T1.NIT = TP.NIT) AND (T1.SUCURSAL = TP.SUCURSAL)
                LEFT  JOIN BANCO BN  ON (TP.BANCO=BN.BANCO)
             WHERE  T1.COMPANIA=MI_RS1.COMPANIA 
                AND T1.NIT     =MI_RS1.NIT 
                AND T1.SUCURSAL=MI_RS1.SUCURSAL
                AND TP.ACTIVA NOT IN (0) -- EN JULIO 13 DE 2017 SE DEFINIO TOMAR SOLAMENTE LAS CUENTAS ACTIVAS POR HPV
                AND ROWNUM<=1;
         EXCEPTION  WHEN NO_DATA_FOUND  THEN
                    MI_CUENTABANCARIA:='';
                    MI_TIPOCUENTABANCARIA:='';
                    MI_BANCOMEN:='';
                    MI_NOMBRECIUDAD:='';
         END;
         BEGIN
               SELECT 
                     TIPO_CPTE_AFECT,
                 CMPTE_AFECTADO
               INTO
                     MI_TIPO_AFECT,
                     MI_NUMERO_AFECT
               FROM 
                    COMPROBANTE_PPTAL CP 
               WHERE CP.COMPANIA    =MI_RS1.COMPANIA
                 AND CP.TIPO        =MI_RS1.TIPO 
                 AND CP.NUMERO      =MI_RS1.NUMERO 
                 AND ROWNUM<=1;
          EXCEPTION WHEN NO_DATA_FOUND THEN
                 MI_TIPO_AFECT:='';
                 MI_NUMERO_AFECT:=0;
       END;

       MI_TIPO_REGISTRO  :=MI_TIPO_AFECT;
       MI_NUMERO_REGISTRO:=MI_NUMERO_AFECT;
       BEGIN
           SELECT TIPO,
                  NUMERO
           INTO   MI_TIPO_EGRESO,
                  MI_NUMERO_EGRESO
           FROM 
           (
               SELECT DISTINCT TIPO_CPTE   TIPO,
                               COMPROBANTE NUMERO
               FROM
                      DETALLE_COMPROBANTE_CNT C
               WHERE  C.COMPANIA         =MI_RS1.COMPANIA
                 AND  C.TIPO_CPTE_AFECT  =MI_RS1.TIPO
                 AND  C.CMPTE_AFECTADO   =MI_RS1.NUMERO
           )
           WHERE ROWNUM<=1;
      EXCEPTION WHEN NO_DATA_FOUND THEN   
          MI_TIPO_EGRESO:='';
          MI_NUMERO_EGRESO:=0;
      END;

       BEGIN
             SELECT  TIPOVIGENCIA,
                     VIGENCIA,
                     VALORBRUTO
               INTO
                     MI_TIPOVIGENCIA,
                     MI_VIGENCIA,
                     MI_VALORPRESUPUESTO
              FROM (             
               SELECT 
                     P.TIPOVIGENCIA,
                     P.VIGENCIA,
                     SUM(D.VALOR_DEBITO-D.VALOR_CREDITO ) VALORBRUTO
               FROM 
                    DETALLE_COMPROBANTE_PPTAL D    
                    INNER JOIN PLAN_PRESUPUESTAL P ON (D.COMPANIA=P.COMPANIA) AND (D.ANO=P.ANO) AND (D.CUENTA=P.CODIGO)
               WHERE D.COMPANIA    =MI_RS1.COMPANIA
                 AND D.TIPO_CPTE   =MI_TIPO_AFECT
                 AND D.COMPROBANTE =MI_NUMERO_AFECT
               GROUP BY
                  P.TIPOVIGENCIA,
                  P.VIGENCIA
              )
               WHERE ROWNUM<=1;
          EXCEPTION WHEN NO_DATA_FOUND THEN
                 MI_TIPOVIGENCIA:='';
                 MI_VALORPRESUPUESTO:=0;
                 MI_VIGENCIA:=' ';
     END;           
     
     SELECT  COUNT(*)
     INTO  MI_FUENTES
     FROM  DETALLE_COMPROBANTE_PPTAL D
     LEFT JOIN PLAN_PPTAL_CONFIG
       ON PLAN_PPTAL_CONFIG.COMPANIA        = D.COMPANIA
       AND PLAN_PPTAL_CONFIG.ANO            = D.ANO
       AND PLAN_PPTAL_CONFIG.CODIGO         = D.CUENTA
       AND PLAN_PPTAL_CONFIG.CENTRO_COSTO   = D.CENTRO_COSTO
       AND PLAN_PPTAL_CONFIG.AUXILIAR       = D.AUXILIAR
       AND PLAN_PPTAL_CONFIG.FUENTE_RECURSO = D.FUENTE_RECURSO
     WHERE D.COMPANIA    =MI_RS1.COMPANIA
       AND D.TIPO_CPTE   =MI_TIPO_AFECT
       AND D.COMPROBANTE =MI_NUMERO_AFECT
       AND CASE WHEN MI_TIPOVIGENCIA='VA' 
            THEN PLAN_PPTAL_CONFIG.FUENTE 
            ELSE PLAN_PPTAL_CONFIG.FUENTE 
           END  
       BETWEEN MI_FUENTEINI AND MI_FUENTEFIN;
     
     MI_FUENTES:=NVL(MI_FUENTES,0);  

     BEGIN
       SELECT CASE WHEN MI_TIPOVIGENCIA='VA' 
                  THEN PLAN_PPTAL_CONFIG.FUENTE 
                  ELSE PLAN_PPTAL_CONFIG.FUENTE 
              END 
       INTO    MI_FUENTE
       FROM DETALLE_COMPROBANTE_PPTAL D 
       LEFT JOIN PLAN_PPTAL_CONFIG
         ON  PLAN_PPTAL_CONFIG.COMPANIA       = D.COMPANIA
         AND PLAN_PPTAL_CONFIG.ANO            = D.ANO
         AND PLAN_PPTAL_CONFIG.CODIGO         = D.CUENTA
         AND PLAN_PPTAL_CONFIG.CENTRO_COSTO   = D.CENTRO_COSTO
         AND PLAN_PPTAL_CONFIG.AUXILIAR       = D.AUXILIAR
         AND PLAN_PPTAL_CONFIG.FUENTE_RECURSO = D.FUENTE_RECURSO
       WHERE D.COMPANIA    = MI_RS1.COMPANIA
         AND D.TIPO_CPTE   = MI_TIPO_AFECT
         AND D.COMPROBANTE = MI_NUMERO_AFECT
         AND CASE WHEN MI_TIPOVIGENCIA='VA' 
                THEN PLAN_PPTAL_CONFIG.FUENTE 
                ELSE PLAN_PPTAL_CONFIG.FUENTE 
             END IS NOT NULL
         AND ROWNUM<=1;
     EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_FUENTE:='';
     END;

--      IF MI_FUENTE NOT BETWEEN MI_FUENTEINI AND MI_FUENTEFIN THEN   
      IF MI_FUENTES <=0 THEN
         CONTINUE;
      END IF;
      MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
      MI_SITUACIONFONDOS:='S';
      MI_STR:=
            MI_RS1.TIPO_REGISTRO || ';' 
            || TRIM(TO_CHAR(MI_CONSECUTIVO,'000000')) || ';' 
            || MI_VIGENCIA || ';' 
            || NVL(MI_RS1.ENTIDAD, MI_ENTIDAD) || ';' 
            || NVL(MI_RS1.UNIDAD_EJECUTORA, MI_UNIDADEJECUTORA) || ';' 
            || MI_RS1.TIPODOCPAGO || ';'
            || MI_RS1.TIPODOCIDENTIFICACION || ';' 
            || MI_RS1.NIT || ';' 
            || NVL(MI_CODIGO_SGC,'') || ';' 
            || MI_RS1.NUMEROCONTRATO || ';'  --|| MI_RS1.REGISTRO || ';'
            || CASE WHEN MI_TIPOVIGENCIA='VA' THEN 'V' ELSE '' END || CASE WHEN MI_TIPOVIGENCIA='RA' THEN 'R' ELSE '' END  || CASE WHEN MI_TIPOVIGENCIA='RC' THEN 'C' ELSE '' END || ';'
            || MI_SITUACIONFONDOS || ';' 
            || REPLACE(REPLACE(MI_RS1.DESCRIPCION,';',','),CHR(39),'') || ';' 
            || MI_RS1.ORIGENFDL || ';' 
            || MI_RS1.DESC_EJECUTADO || ';' 
            || MI_RS1.ACTARECIBO || ';'
            || NVL(MI_INTERVENTOR,'') || ';' 
            || MI_RS1.CODCONT_VLRNETO      || ';' 
            || MI_RS1.CODCONT_VLRBRUTO || ';' 
            || MI_RS1.FORMAPAGO || ';' 
            || NVL(MI_CUENTABANCARIA,'') || ';' 
            || NVL(MI_BANCOMEN,'') || ';'
            || MI_TIPOCUENTABANCARIA || ';' 
            || MI_RS1.TREGIMEN || ';' 
            || MI_RS1.OBSERVAC || ';' 
            || MI_RS1.DIRECCION || ';' 
            || MI_RS1.TNATURALEZA || ';'
            || NVL(MI_NOMBRECIUDAD,'') || ';' 
            || MI_RS1.TELEFONO || ';' 
            || MI_RS1.TIPODEORDEN || ';' ;
      MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
      MI_REGISTROS:=MI_REGISTROS+1;
      MI_VALOR_P := MI_RS1.TIPO;
      MI_VALOR_P := MI_RS1.NUMERO;
      BEGIN 
        SELECT MAX(DCP.CMPTE_AFECTADO)
              ,DCP.TIPO_CPTE_AFECT 
        INTO MI_NUMERO_AFECT1
            ,MI_TIPO_AFECT1
        FROM DETALLE_COMPROBANTE_PPTAL  DCP 
             INNER JOIN TIPO_COMPROBPP TC ON  (DCP.COMPANIA = TC.COMPANIA) AND (DCP.TIPO_CPTE = TC.CODIGO)   
             INNER JOIN PLAN_PRESUPUESTAL  PP ON (DCP.COMPANIA = PP.COMPANIA) AND (DCP.ANO = PP.ANO) AND (DCP.CUENTA = PP.CODIGO) 
             INNER JOIN COMPANIA  C ON DCP.COMPANIA = C.CODIGO
             INNER JOIN COMPROBANTE_PPTAL  CP ON (DCP.COMPANIA = CP.COMPANIA) AND (DCP.ANO = CP.ANO)  AND (DCP.TIPO_CPTE = CP.TIPO) AND (DCP.COMPROBANTE = CP.NUMERO) 
             LEFT JOIN PLAN_PPTAL_CONFIG
                ON  PLAN_PPTAL_CONFIG.COMPANIA       = DCP.COMPANIA
                AND PLAN_PPTAL_CONFIG.ANO            = DCP.ANO
                AND PLAN_PPTAL_CONFIG.CODIGO         = DCP.CUENTA
                AND PLAN_PPTAL_CONFIG.CENTRO_COSTO   = DCP.CENTRO_COSTO
                AND PLAN_PPTAL_CONFIG.AUXILIAR       = DCP.AUXILIAR
                AND PLAN_PPTAL_CONFIG.FUENTE_RECURSO = DCP.FUENTE_RECURSO 
        WHERE DCP.COMPANIA    = MI_RS1.COMPANIA
          AND DCP.TIPO_CPTE   = MI_RS1.TIPO
          AND DCP.COMPROBANTE = MI_RS1.NUMERO 
        GROUP BY DCP.TIPO_CPTE_AFECT;        
      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_NUMERO_AFECT1 := 0;
        MI_TIPO_AFECT1   := '';
      END;
-- BARRE CURSOR 2
FOR MI_RS2 IN 
      (
           SELECT 2 TIPO_REGISTRO,
                  DCP.COMPANIA, 
                  PP.TIPOVIGENCIA, 
                  PP.VIGENCIA, 
                  C.ENTIDAD, 
                  C.UNIDAD_EJECUTORA, 
                  'OP' TIPODOCPAGO, 
                  DCP.TIPO_CPTE_AFECT, 
                  MAX(DCP.CMPTE_AFECTADO) AS CMPTE_AFECTADO, 
                  CP.FECHA, 
                  CP.TIPO,
                  CP.NUMERO,
                  RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.RUBRO_GUION ELSE PLAN_PPTAL_CONFIG.RUBRO_GUION END,CASE WHEN PLAN_PPTAL_CONFIG.ANO<2022 THEN 32 ELSE 100 END) RUBRO_GUIONR,
                  CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.FUENTE      ELSE PLAN_PPTAL_CONFIG.FUENTE      END FUENTER,
                  --RPAD(PLAN_PPTAL_CONFIG.CODIGO,32) RUBROR,
                  CASE WHEN PLAN_PPTAL_CONFIG.ANO<2022 THEN RPAD(PLAN_PPTAL_CONFIG.CODIGO,32) ELSE RPAD(PLAN_PPTAL_CONFIG.CODIGO,100) END RUBROR,
                  SUM(DCP.VALOR_DEBITO-DCP.VALOR_CREDITO) VALORNETO
               FROM DETALLE_COMPROBANTE_PPTAL  DCP 
                  INNER JOIN TIPO_COMPROBPP TC ON  (DCP.COMPANIA = TC.COMPANIA) AND (DCP.TIPO_CPTE = TC.CODIGO)  
                  INNER JOIN PLAN_PRESUPUESTAL  PP ON (DCP.COMPANIA = PP.COMPANIA) AND (DCP.ANO = PP.ANO) AND (DCP.CUENTA = PP.CODIGO) 
                  INNER JOIN COMPANIA  C ON DCP.COMPANIA = C.CODIGO
                  INNER JOIN COMPROBANTE_PPTAL  CP ON (DCP.COMPANIA = CP.COMPANIA) AND (DCP.ANO = CP.ANO)  AND (DCP.TIPO_CPTE = CP.TIPO) AND (DCP.COMPROBANTE = CP.NUMERO) 
                  LEFT JOIN PLAN_PPTAL_CONFIG
                    ON  PLAN_PPTAL_CONFIG.COMPANIA       = DCP.COMPANIA
                    AND PLAN_PPTAL_CONFIG.ANO            = DCP.ANO
                    AND PLAN_PPTAL_CONFIG.CODIGO         = DCP.CUENTA
                    AND PLAN_PPTAL_CONFIG.CENTRO_COSTO   = DCP.CENTRO_COSTO
                    AND PLAN_PPTAL_CONFIG.AUXILIAR       = DCP.AUXILIAR
                    AND PLAN_PPTAL_CONFIG.FUENTE_RECURSO = DCP.FUENTE_RECURSO 
               WHERE DCP.COMPANIA    =MI_RS1.COMPANIA
                 AND DCP.TIPO_CPTE   =MI_RS1.TIPO   --TIPODEPRESUPUESTO  --MI_TIPO_AFECT
                 AND DCP.COMPROBANTE =MI_RS1.NUMERO    --REGISTRO   --MI_NUMERO_AFECT
                 AND DCP.CMPTE_AFECTADO IN(MI_NUMERO_AFECT1)
                 AND DCP.TIPO_CPTE_AFECT IN(MI_TIPO_AFECT1)
               GROUP BY
                  2,
                  DCP.COMPANIA, 
                  PP.TIPOVIGENCIA, 
                  PP.VIGENCIA, 
                  C.ENTIDAD, 
                  C.UNIDAD_EJECUTORA, 
                  'OP', 
                  DCP.TIPO_CPTE_AFECT,
                  CP.FECHA, 
                  CP.TIPO,
                  CP.NUMERO,
                  RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.RUBRO_GUION ELSE PLAN_PPTAL_CONFIG.RUBRO_GUION END,CASE WHEN PLAN_PPTAL_CONFIG.ANO<2022 THEN 32 ELSE 100 END) ,
                  CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.FUENTE      ELSE PLAN_PPTAL_CONFIG.FUENTE      END ,
                  CASE WHEN PLAN_PPTAL_CONFIG.ANO<2022 THEN RPAD(PLAN_PPTAL_CONFIG.CODIGO,32) ELSE RPAD(PLAN_PPTAL_CONFIG.CODIGO,100) END
--          ORDER BY TIPO,NUMERO,FECHA 
      )
LOOP
-- TRAE LA DISPONIBILIDAD
    MI_TIPO_CPTE_AFECT  :=MI_RS2.TIPO_CPTE_AFECT;
    MI_CMPTE_AFECTADO   :=MI_RS2.CMPTE_AFECTADO;
--    MI_CONSECUTIVOPPTO  :=MI_RS2.CONSECUTIVOPPTO;
   BEGIN
         SELECT 
               TIPO_CPTE_AFECT,
               CMPTE_AFECTADO
         INTO
               MI_TIPO_DISPON,
               MI_NUMERO_DISPON
        FROM  DETALLE_COMPROBANTE_PPTAL DCP  -- SE CAMBIO LA TABLA COMPROBANTE_PPTAL POR DETALLE_COMPROBANTE_PPTAL DEBIDO A QUE CUANDO ES RESERVA DE CAJA O DE VIGENCIA ANTERIOR NO HEREDA EN EL COMPROBANTE EL TIPO AFECTADO
    WHERE DCP.COMPANIA    =MI_RS2.COMPANIA
          AND DCP.TIPO_CPTE   =MI_RS2.TIPO_CPTE_AFECT
          AND DCP.COMPROBANTE =MI_RS2.CMPTE_AFECTADO
      AND DCP.TIPO_CPTE_AFECT IS NOT NULL
          AND DCP.CMPTE_AFECTADO  IS NOT NULL
      AND ROWNUM<=1;
   EXCEPTION WHEN NO_DATA_FOUND THEN
               MI_TIPO_DISPON:='';
               MI_NUMERO_DISPON:=0;
  END;
-- TRAE EL NUMERO DEL EGRESO DE GIRO
       BEGIN
           SELECT   DCP.TIPO_CPTE, 
                    DCP.COMPROBANTE, 
                    DCP.CONSECUTIVO,
                    DCP.FECHA
           INTO 
                    MI_TIPO_E,
                    MI_COMPROBANTE_E,
                    MI_CONSECUTIVO_E,
                    MI_FECHA_E
           FROM     DETALLE_COMPROBANTE_PPTAL DCP
           WHERE    DCP.COMPANIA        =MI_RS2.COMPANIA
                AND  DCP.TIPO_CPTE_AFECT =MI_RS2.TIPO
                AND DCP.CMPTE_AFECTADO  =MI_RS2.NUMERO
--              AND DCP.CONSECUTIVOPPTO=MI_RS2.CONSECUTIVO
                AND ROWNUM<=1;
       EXCEPTION WHEN NO_DATA_FOUND THEN
                    MI_TIPO_E:='';
                    MI_COMPROBANTE_E:='';
                    MI_CONSECUTIVO_E:='';
                    MI_FECHA_E:=SYSDATE();
       END;
       MI_RUBRO   :=NVL(MI_RS2.RUBRO_GUIONR,'');
       MI_FUENTE  :=NVL(MI_RS2.FUENTER,'');
       MI_RUBROFUT:=NVL(MI_RS2.RUBROR,'');
       MI_STR     :=
              MI_RS2.TIPO_REGISTRO || ';' 
             || TRIM(TO_CHAR(MI_CONSECUTIVO,'000000')) || ';' 
             || MI_VIGENCIA || ';' 
             || NVL(MI_RS2.ENTIDAD, MI_ENTIDAD) || ';' 
             || NVL(MI_RS2.UNIDAD_EJECUTORA, MI_UNIDADEJECUTORA) || ';'
             || MI_RS2.TIPODOCPAGO || ';' 
             || TRIM(NVL(MI_RUBRO,' ')) || ';' 
             || MI_RS2.VIGENCIA || ';' 
             || SUBSTR(LPAD(MI_NUMERO_DISPON, 12),7,6) || ';' 
             || SUBSTR(LPAD(MI_NUMERO_REGISTRO, 12),7,6) || ';' 
             || TRIM(TO_CHAR(NVL(MI_FECHA_E,MI_RS2.FECHA),'RRRR')) || ';'
             || TRIM(TO_CHAR(NVL(MI_FECHA_E,MI_RS2.FECHA),'MM')) || ';' 
             || NVL(MI_CODIGO_SGC,'') || ';' 
             || MI_RS1.NUMEROCONTRATO||';' --|| MI_RS2.NUMERO || ';' 
             || MI_RS2.VALORNETO || ';';
      MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
      MI_REGISTROS:=MI_REGISTROS+1;

--             || SUBSTR(LPAD(MI_RS2.CMPTE_AFECTADO, 12),7,6) || ';' 

  END LOOP;
-- BARRE CURSOR 3
FOR MI_RS3 IN 
      (
            SELECT 3 TIPO_REGISTRO,
                  DCP.COMPANIA, 
                  PP.TIPOVIGENCIA, 
                  PP.VIGENCIA, 
                  C.ENTIDAD, 
                  C.UNIDAD_EJECUTORA, 
                  'OP' TIPODOCPAGO, 
                  DCP.TIPO_CPTE_AFECT, 
                  MAX(DCP.CMPTE_AFECTADO) AS CMPTE_AFECTADO, 
                  CP.FECHA, 
                  CP.TIPO,
                  CP.NUMERO,
                  'C' TIPODOCPAGA,
                  1 AS ITEM,
                  RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.RUBRO_GUION ELSE PLAN_PPTAL_CONFIG.RUBRO_GUION END,CASE WHEN PLAN_PPTAL_CONFIG.ANO<2022 THEN 32 ELSE 100 END) RUBRO_GUIONR,
                  CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.FUENTE      ELSE PLAN_PPTAL_CONFIG.FUENTE     END           FUENTER,
                  RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.CODIGO       ELSE PLAN_PPTAL_CONFIG.CODIGO       END,CASE WHEN PLAN_PPTAL_CONFIG.ANO<2022 THEN 32 ELSE 100 END) RUBROR,
                  SUM(DCP.VALOR_DEBITO-DCP.VALOR_CREDITO) VALORNETO  
            FROM DETALLE_COMPROBANTE_PPTAL  DCP 
              INNER JOIN TIPO_COMPROBPP TC ON  (DCP.COMPANIA = TC.COMPANIA) AND (DCP.TIPO_CPTE = TC.CODIGO)    
              INNER JOIN PLAN_PRESUPUESTAL  PP ON (DCP.COMPANIA = PP.COMPANIA) AND (DCP.ANO = PP.ANO) AND (DCP.CUENTA = PP.CODIGO) 
              INNER JOIN COMPANIA  C ON DCP.COMPANIA = C.CODIGO
              INNER JOIN COMPROBANTE_PPTAL  CP ON (DCP.COMPANIA = CP.COMPANIA) AND (DCP.ANO = CP.ANO)  AND (DCP.TIPO_CPTE = CP.TIPO) AND (DCP.COMPROBANTE = CP.NUMERO) 
              LEFT JOIN PLAN_PPTAL_CONFIG
                    ON  PLAN_PPTAL_CONFIG.COMPANIA       = DCP.COMPANIA
                    AND PLAN_PPTAL_CONFIG.ANO            = DCP.ANO
                    AND PLAN_PPTAL_CONFIG.CODIGO         = DCP.CUENTA
                    AND PLAN_PPTAL_CONFIG.CENTRO_COSTO   = DCP.CENTRO_COSTO
                    AND PLAN_PPTAL_CONFIG.AUXILIAR       = DCP.AUXILIAR
                    AND PLAN_PPTAL_CONFIG.FUENTE_RECURSO = DCP.FUENTE_RECURSO 
            WHERE DCP.COMPANIA    =MI_RS1.COMPANIA
                 AND DCP.TIPO_CPTE   =MI_RS1.TIPO   --TIPODEPRESUPUESTO --MI_TIPO_AFECT
                 AND DCP.COMPROBANTE =MI_RS1.NUMERO    --REGISTRO  --MI_NUMERO_AFECT  
                 AND DCP.CMPTE_AFECTADO IN(MI_NUMERO_AFECT1)
                 AND DCP.TIPO_CPTE_AFECT IN(MI_TIPO_AFECT1)
            GROUP BY
                  3 ,
                  DCP.COMPANIA, 
                  PP.TIPOVIGENCIA, 
                  PP.VIGENCIA, 
                  C.ENTIDAD, 
                  C.UNIDAD_EJECUTORA, 
                  'OP', 
                  DCP.TIPO_CPTE_AFECT, 
                  CP.FECHA, 
                  CP.TIPO,
                  CP.NUMERO,
                  'C' ,
                  1 ,
                  RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.RUBRO_GUION ELSE PLAN_PPTAL_CONFIG.RUBRO_GUION END,CASE WHEN PLAN_PPTAL_CONFIG.ANO<2022 THEN 32 ELSE 100 END) ,
                  CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.FUENTE      ELSE PLAN_PPTAL_CONFIG.FUENTE      END ,
                  RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.CODIGO       ELSE PLAN_PPTAL_CONFIG.CODIGO      END,CASE WHEN PLAN_PPTAL_CONFIG.ANO<2022 THEN 32 ELSE 100 END)
--          ORDER BY TIPO,NUMERO,FECHA 
      )
      LOOP
       BEGIN
             SELECT   DCP.TIPO_CPTE, 
                      DCP.COMPROBANTE, 
                      DCP.CONSECUTIVO,
                      DCP.FECHA
             INTO 
                      MI_TIPO_E,
                      MI_COMPROBANTE_E,
                      MI_CONSECUTIVO_E,
                      MI_FECHA_E
             FROM     DETALLE_COMPROBANTE_PPTAL DCP
             WHERE    DCP.COMPANIA        =MI_RS3.COMPANIA
                  AND  DCP.TIPO_CPTE_AFECT =MI_RS3.TIPO
                  AND DCP.CMPTE_AFECTADO  =MI_RS3.NUMERO
--                  AND DCP.CONSECUTIVOPPTO=MI_RS3.CONSECUTIVO
                  AND ROWNUM<=1;
       EXCEPTION WHEN NO_DATA_FOUND THEN
                      MI_TIPO_E:='';
                      MI_COMPROBANTE_E:=0;
                      MI_CONSECUTIVO_E:=0;
                      MI_FECHA_E:=SYSDATE();
       END;
-- TRAE EL RUBRO EQUIVALENTE

   MI_RUBRO   :=NVL(MI_RS3.RUBRO_GUIONR,'');
     MI_FUENTE  :=NVL(MI_RS3.FUENTER,'');
     MI_RUBROFUT:=NVL(MI_RS3.RUBROR,'');
   BEGIN
        SELECT 
            TIPO_CPTE_AFECT,
          CMPTE_AFECTADO
        INTO
            MI_TIPO_DISPON,
            MI_NUMERO_DISPON
        FROM 
            DETALLE_COMPROBANTE_PPTAL DCP 
        WHERE DCP.COMPANIA    =MI_RS3.COMPANIA
          AND DCP.TIPO_CPTE   =MI_RS3.TIPO_CPTE_AFECT
          AND DCP.COMPROBANTE =MI_RS3.CMPTE_AFECTADO
        AND DCP.TIPO_CPTE_AFECT IS NOT NULL
          AND DCP.CMPTE_AFECTADO  IS NOT NULL
       AND ROWNUM<=1;
   EXCEPTION WHEN NO_DATA_FOUND THEN
               MI_TIPO_DISPON:='';
               MI_NUMERO_DISPON:=0;
  END;



      BEGIN
           MI_STR:=
              MI_RS3.TIPO_REGISTRO || ';' 
             || TRIM(TO_CHAR(MI_CONSECUTIVO,'000000')) || ';' 
             || MI_VIGENCIA || ';' 
             || NVL(MI_RS3.ENTIDAD, MI_ENTIDAD) || ';' 
             || NVL(MI_RS3.UNIDAD_EJECUTORA, MI_UNIDADEJECUTORA) || ';'
             || MI_RS3.TIPODOCPAGO || ';' 
             || TRIM(NVL(MI_RUBRO,' ')) || ';' 
             || MI_RS3.VIGENCIA || ';' 
             || SUBSTR(LPAD(MI_RS3.CMPTE_AFECTADO, 12),7,6) || ';' 
             || SUBSTR(LPAD(MI_NUMERO_DISPON, 12),7,6) || ';' 
             || MI_RS3.TIPODOCPAGA || ';' 
             || MI_RS1.NUMERO || ';' 
             || MI_RS3.ITEM || ';' 
             || MI_RS1.TIPODOCIDENTIFICACION || ';' 
             || MI_RS1.NIT || ';'
             || CASE WHEN INSTR(MI_RS1.CODIGOICA,',')>0 THEN TRIM(SUBSTR(MI_RS1.CODIGOICA,1,INSTR(MI_RS1.CODIGOICA,',')-1)) ELSE  TRIM(MI_RS1.CODIGOICA) END || ';' 
             || TO_CHAR(MI_RS3.FECHA,'DD/MM/RRRR') || ';' 
             || MI_RS3.VALORNETO || ';' 
             || '' || ';' 
             || '' || ';' 
             || '' || ';' 
             || '' || ';' 
             || 'N' || ';';
             
--             || SUBSTR(LPAD(MI_RS3.CMPTE_AFECTADO, 12),7,6) || ';' 

      EXCEPTION WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE(TRIM(TO_CHAR(MI_CONSECUTIVO,'000000'))); 
                     DBMS_OUTPUT.PUT_LINE( MI_RS3.VIGENCIA ); 
                     DBMS_OUTPUT.PUT_LINE( NVL(MI_RS3.ENTIDAD, MI_ENTIDAD));
                     DBMS_OUTPUT.PUT_LINE( NVL(MI_RS3.UNIDAD_EJECUTORA, MI_UNIDADEJECUTORA)) ;
                     DBMS_OUTPUT.PUT_LINE( MI_RS3.TIPODOCPAGO); 
                     DBMS_OUTPUT.PUT_LINE( NVL(MI_RUBRO,' ')) ;
                     DBMS_OUTPUT.PUT_LINE( MI_RS3.VIGENCIA) ; 
                     DBMS_OUTPUT.PUT_LINE( SUBSTR(LPAD(MI_RS3.CMPTE_AFECTADO, 12),7,6)); 
                     DBMS_OUTPUT.PUT_LINE( SUBSTR(LPAD(MI_CMPTE_AFECTADO, 12),7,6)); 
                     DBMS_OUTPUT.PUT_LINE( MI_RS3.TIPODOCPAGA);
                     DBMS_OUTPUT.PUT_LINE( MI_RS1.NRO_DOCUMENTO) ;
                     DBMS_OUTPUT.PUT_LINE( MI_RS3.ITEM); 
                     DBMS_OUTPUT.PUT_LINE( MI_RS1.TIPODOCIDENTIFICACION );
                     DBMS_OUTPUT.PUT_LINE( MI_RS1.NIT);
                     DBMS_OUTPUT.PUT_LINE( MI_RS1.CODIGOICA) ;
                     DBMS_OUTPUT.PUT_LINE( MI_RS3.FECHA); 
                     DBMS_OUTPUT.PUT_LINE( MI_RS1.CODCONT_VLRBRUTO);
      END;
      MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;

      MI_REGISTROS:=MI_REGISTROS+1;

      END LOOP;
-- BARRE CURSOR 4
FOR MI_RS4 IN 
      (              SELECT
                           4 TIPO_REGISTRO,
                           1 ITEM,
                           CC.COMPANIA,
                           CC.ANO, 
                           CC.TIPO, 
                           CC.NUMERO, 
                           C.ENTIDAD, 
                           C.UNIDAD_EJECUTORA, 
                           'OP' TIPODOCPAGO,
                           CC.FECHA,
                           P.COD_EQUIV CUENTA,
                           CASE WHEN P.CLASECUENTA IN ('I') THEN DC.VALOR_CREDITO-DC.VALOR_DEBITO   ELSE 0 END  DESCUENTO_IMPUESTO,
                           CASE WHEN P.CLASECUENTA IN ('I') THEN DC.BASE_GRAVABLE   ELSE 0 END   TBASE_GRAVABLE,
                           CASE WHEN P.CLASECUENTA IN ('I') AND DC.BASE_GRAVABLE NOT IN (0) THEN ROUND((DC.VALOR_CREDITO-DC.VALOR_DEBITO)/DC.BASE_GRAVABLE,5)   ELSE 0 END TPORCENTAJE                     
                           FROM COMPROBANTE_CNT   CC 
                       INNER JOIN COMPANIA          C   ON (CC.COMPANIA=C.CODIGO)       
                       INNER JOIN TIPO_COMPROBANTE  TC  ON (CC.COMPANIA = TC.COMPANIA) AND (CC.TIPO = TC.CODIGO)
                       INNER JOIN DETALLE_COMPROBANTE_CNT DC ON (CC.COMPANIA=DC.COMPANIA) AND (CC.TIPO=DC.TIPO_CPTE) AND (CC.NUMERO=DC.COMPROBANTE)
                       INNER JOIN PLAN_CONTABLE P ON (P.COMPANIA=DC.COMPANIA) AND (P.ANO=DC.ANO) AND (P.CODIGO=DC.CUENTA)
                     WHERE  CC.COMPANIA   =MI_RS1.COMPANIA 
                       AND  DC.TIPO_CPTE  =MI_RS1.TIPO
                       AND  DC.COMPROBANTE=MI_RS1.NUMERO
                       AND  P.CLASECUENTA IN ('I')
                       AND  DC.VALOR_DEBITO-DC.VALOR_CREDITO NOT IN (0)
)
LOOP
-- TRAE LOS DATOS DEL REGISTRO

      MI_STR:=
              MI_RS4.TIPO_REGISTRO || ';' 
             || TRIM(TO_CHAR(MI_CONSECUTIVO,'000000')) || ';' 
             || NVL(MI_VIGENCIA,'') || ';' 
             || NVL(MI_RS4.ENTIDAD, MI_ENTIDAD) || ';' 
             || NVL(MI_RS4.UNIDAD_EJECUTORA, MI_UNIDADEJECUTORA) || ';'
             || MI_RS4.TIPODOCPAGO || ';' 
             || TRIM(NVL(MI_RUBRO,' ')) || ';' 
             || NVL(MI_VIGENCIA,'') || ';' 
             || SUBSTR(LPAD(MI_NUMERO_DISPON, 12),7,6) || ';' 
             || SUBSTR(LPAD(MI_NUMERO_REGISTRO, 12),7,6) || ';' 
             || MI_RS1.NUMERO || ';' 
             || MI_RS4.ITEM || ';' 
             || TRIM(MI_RS4.CUENTA)|| ';' 
             || MI_RS4.TBASE_GRAVABLE || ';' 
             || TRIM(TO_CHAR(ROUND(MI_RS4.TPORCENTAJE*100,3),'990.999')) || ';' 
             || MI_RS4.DESCUENTO_IMPUESTO || ';';

--             || TRIM(REPLACE(TO_CHAR(LPAD(MI_RS4.CUENTA,16),'0,0,00,00,000,000,0000'),',','-')) || ';' 
      MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
      MI_REGISTROS:=MI_REGISTROS+1;

 END LOOP;
-- BARRE CURSOR 5

FOR MI_RS5 IN 
      (
           SELECT 5 TIPO_REGISTRO,
                  DCP.COMPANIA, 
                  PP.VIGENCIA, 
                  PP.TIPOVIGENCIA, 
                  C.ENTIDAD, 
                  C.UNIDAD_EJECUTORA, 
                  'OP' TIPODOCPAGO, 
                  DCP.TIPO_CPTE_AFECT, 
                  DCP.CMPTE_AFECTADO, 
                  CP.FECHA, 
                  CP.TIPO,
                  CP.NUMERO,
                  0 CODIGOTIPO,
                  0 COD_COMPONENTE,                  
                  0 COD_OBJETO,
                  '     ' ENDOSO,
                  0 VALORENDOSO,
                  '     ' DETALLE_FUENTE,
                  RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.RUBRO_GUION ELSE PLAN_PPTAL_CONFIG.RUBRO_GUION END,CASE WHEN PLAN_PPTAL_CONFIG.ANO<2022 THEN 32 ELSE 100 END) RUBRO_GUIONR,
                  CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.FUENTE      ELSE PLAN_PPTAL_CONFIG.FUENTE      END FUENTER,
                  RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.CODIGO       ELSE PLAN_PPTAL_CONFIG.CODIGO       END,CASE WHEN PLAN_PPTAL_CONFIG.ANO<2022 THEN 32 ELSE 100 END) RUBROR,
                  TRIM(SUBSTR(RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.RUBRO_GUION ELSE PLAN_PPTAL_CONFIG.RUBRO_GUION END,CASE WHEN PLAN_PPTAL_CONFIG.ANO<2022 THEN 32 ELSE 100 END),1,INSTR(RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.RUBRO_GUION ELSE PLAN_PPTAL_CONFIG.RUBRO_GUION END,CASE WHEN PLAN_PPTAL_CONFIG.ANO<2022 THEN 32 ELSE 100 END),'-',-1))) 
                  ||SUBSTR(DCP.CUENTA,CASE WHEN INSTR(TRIM(DCP.CUENTA),' ',-1)>INSTR(TRIM(DCP.CUENTA),'-',-1) THEN INSTR(TRIM(DCP.CUENTA),' ',-1) ELSE INSTR(TRIM(DCP.CUENTA),'-',-1) END+1,LENGTH(TRIM(DCP.CUENTA))) RUBROFUT1,
--                  TRIM(SUBSTR(RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN RUBRO.RUBRO_GUION ELSE RUBRO_RESERVA.RUBRO_GUION END,32),1,INSTR(RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN RUBRO.RUBRO_GUION ELSE RUBRO_RESERVA.RUBRO_GUION END,32),'-',-1))) ABC,
--                   SUBSTR(DCP.CUENTA,CASE WHEN INSTR(TRIM(DCP.CUENTA),' ',-1)>INSTR(TRIM(DCP.CUENTA),'-',-1) THEN INSTR(TRIM(DCP.CUENTA),' ',-1) ELSE INSTR(TRIM(DCP.CUENTA),'-',-1) END+1,LENGTH(TRIM(DCP.CUENTA))) XYZ,
--                  DCP.CUENTA,
                  SUM(DCP.VALOR_DEBITO-DCP.VALOR_CREDITO) VALORNETO
--                ,DCP.CONSECUTIVO
                FROM DETALLE_COMPROBANTE_PPTAL  DCP 
                  INNER JOIN TIPO_COMPROBPP TC ON  (DCP.COMPANIA = TC.COMPANIA) AND (DCP.TIPO_CPTE = TC.CODIGO)  
                  INNER JOIN PLAN_PRESUPUESTAL  PP ON (DCP.COMPANIA = PP.COMPANIA) AND (DCP.ANO = PP.ANO) AND (DCP.CUENTA = PP.CODIGO) 
                  INNER JOIN COMPANIA  C ON DCP.COMPANIA = C.CODIGO
                  INNER JOIN COMPROBANTE_PPTAL  CP ON (DCP.COMPANIA = CP.COMPANIA) AND (DCP.ANO = CP.ANO)  AND (DCP.TIPO_CPTE = CP.TIPO) AND (DCP.COMPROBANTE = CP.NUMERO) 
                  LEFT JOIN PLAN_PPTAL_CONFIG
                    ON  PLAN_PPTAL_CONFIG.COMPANIA       = DCP.COMPANIA
                    AND PLAN_PPTAL_CONFIG.ANO            = DCP.ANO
                    AND PLAN_PPTAL_CONFIG.CODIGO         = DCP.CUENTA
                    AND PLAN_PPTAL_CONFIG.CENTRO_COSTO   = DCP.CENTRO_COSTO
                    AND PLAN_PPTAL_CONFIG.AUXILIAR       = DCP.AUXILIAR
                    AND PLAN_PPTAL_CONFIG.FUENTE_RECURSO = DCP.FUENTE_RECURSO 
               WHERE DCP.COMPANIA    =MI_RS1.COMPANIA
                 AND DCP.TIPO_CPTE   =MI_RS1.TIPO   --TIPODEPRESUPUESTO MI_TIPO_AFECT
                 AND DCP.COMPROBANTE =MI_RS1.NUMERO    --REGISTRO  MI_NUMERO_AFECT
                 AND DCP.CMPTE_AFECTADO  IN(MI_NUMERO_AFECT1)
                 AND DCP.TIPO_CPTE_AFECT IN(MI_TIPO_AFECT1)
               GROUP BY
                   5 ,
                  DCP.COMPANIA, 
                  PP.VIGENCIA, 
                  PP.TIPOVIGENCIA, 
                  C.ENTIDAD, 
                  C.UNIDAD_EJECUTORA, 
                  'OP' , 
                  DCP.TIPO_CPTE_AFECT, 
                  DCP.CMPTE_AFECTADO, 
                  CP.FECHA, 
                  CP.TIPO,
                  CP.NUMERO,
                  0 ,
                  0 ,                  
                  0 ,
                  '     ' ,
                  0 ,
                  '     ' ,
                  RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.RUBRO_GUION ELSE PLAN_PPTAL_CONFIG.RUBRO_GUION END,CASE WHEN PLAN_PPTAL_CONFIG.ANO<2022 THEN 32 ELSE 100 END),
                  CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.FUENTE      ELSE PLAN_PPTAL_CONFIG.FUENTE      END ,
                  RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.CODIGO       ELSE PLAN_PPTAL_CONFIG.CODIGO       END,CASE WHEN PLAN_PPTAL_CONFIG.ANO<2022 THEN 32 ELSE 100 END),
                  TRIM(SUBSTR(RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.RUBRO_GUION ELSE PLAN_PPTAL_CONFIG.RUBRO_GUION END,CASE WHEN PLAN_PPTAL_CONFIG.ANO<2022 THEN 32 ELSE 100 END),1,INSTR(RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN PLAN_PPTAL_CONFIG.RUBRO_GUION ELSE PLAN_PPTAL_CONFIG.RUBRO_GUION END,CASE WHEN PLAN_PPTAL_CONFIG.ANO<2022 THEN 32 ELSE 100 END),'-',-1))) 
                  ||SUBSTR(DCP.CUENTA,CASE WHEN INSTR(TRIM(DCP.CUENTA),' ',-1)>INSTR(TRIM(DCP.CUENTA),'-',-1) THEN INSTR(TRIM(DCP.CUENTA),' ',-1) ELSE INSTR(TRIM(DCP.CUENTA),'-',-1) END+1,LENGTH(TRIM(DCP.CUENTA)))
--                ,  TRIM(SUBSTR(RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN RUBRO.RUBRO_GUION ELSE RUBRO_RESERVA.RUBRO_GUION END,32),1,INSTR(RPAD(CASE WHEN PP.TIPOVIGENCIA='VA' THEN RUBRO.RUBRO_GUION ELSE RUBRO_RESERVA.RUBRO_GUION END,32),'-',-1))),
--                  SUBSTR(DCP.CUENTA,CASE WHEN INSTR(TRIM(DCP.CUENTA),' ',-1)>INSTR(TRIM(DCP.CUENTA),'-',-1) THEN INSTR(TRIM(DCP.CUENTA),' ',-1) ELSE INSTR(TRIM(DCP.CUENTA),'-',-1) END+1,LENGTH(TRIM(DCP.CUENTA)))
--          ORDER BY TIPO,NUMERO,FECHA 
      )
LOOP
-- TRAE LA DISPONIBILIDAD
        MI_RUBRO   :=NVL(MI_RS5.RUBRO_GUIONR,'');
        MI_FUENTE  :=NVL(MI_RS5.FUENTER,'');
        MI_RUBROFUT:=NVL(MI_RS5.RUBROR,'');
   BEGIN
         SELECT 
               TIPO_CPTE_AFECT,
               CMPTE_AFECTADO
         INTO
               MI_TIPO_DISPON,
               MI_NUMERO_DISPON
        FROM 
              DETALLE_COMPROBANTE_PPTAL DCP 
        WHERE DCP.COMPANIA    =MI_RS5.COMPANIA
          AND DCP.TIPO_CPTE   =MI_RS5.TIPO_CPTE_AFECT
          AND DCP.COMPROBAnte =MI_RS5.CMPTE_AFECTADO
      AND DCP.TIPO_CPTE_AFECT IS NOT NULL
          AND DCP.CMPTE_AFECTADO  IS NOT NULL
       AND ROWNUM<=1;
   EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_TIPO_DISPON:='';
           MI_NUMERO_DISPON:=0;
  END;

-- TRAE EL NUMERO DEL EGRESO DE GIRO

      BEGIN
       SELECT   DCP.TIPO_CPTE, 
                DCP.COMPROBANTE, 
                DCP.CONSECUTIVO,
                DCP.FECHA
       INTO 
                MI_TIPO_E,
                MI_COMPROBANTE_E,
                MI_CONSECUTIVO_E,
                MI_FECHA_E
       FROM     DETALLE_COMPROBANTE_PPTAL DCP
       WHERE    DCP.COMPANIA        =MI_RS5.COMPANIA
           AND  DCP.TIPO_CPTE_AFECT =MI_RS5.TIPO
            AND DCP.CMPTE_AFECTADO  =MI_RS5.NUMERO
--            AND DCP.CONSECUTIVOPPTO= MI_RS5.CONSECUTIVO
            AND ROWNUM<=1;
      EXCEPTION WHEN NO_DATA_FOUND THEN
             MI_TIPO_E:='';
             MI_COMPROBANTE_E:='';
             MI_CONSECUTIVO_E:='';
             MI_FECHA_E:=SYSDATE();
      END;
-- TRAE EL RUBRO EQUIVALENTE

-- TRAE EL RUBRO FUT -- SE CAMBIO EN MARZO 1 POR SOLICITUD DE IDIPRON


    IF MI_RS5.TIPOVIGENCIA='VA' THEN
      BEGIN
            SELECT TIPO_GASTO,
                   COMPONENTE,
                   CONCEPTO,
                   DETALLE
            INTO   MI_CODIGOTIPO        ,
                   MI_COD_COMPONENTE    ,                  
                   MI_COD_OBJETO        ,
                   MI_DETALLEFUENTE     
            FROM   PLAN_PPTAL_CONFIG RUBRO_FUT
            WHERE  CODIGO=MI_RS5.RUBROFUT1   --MI_RUBROFUT1 -- RUBRO FUT MODIFICADO
              AND ROWNUM<=1;
      EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CODIGOTIPO:='';
            MI_COD_COMPONENTE:='';
            MI_COD_OBJETO:=0;
            MI_DETALLEFUENTE:=0;
      END;
   ELSE
     BEGIN
            SELECT TIPO_GASTO,
                   COMPONENTE,
                   CONCEPTO,
                   DETALLE
            INTO   MI_CODIGOTIPO        ,
                   MI_COD_COMPONENTE    ,                  
                   MI_COD_OBJETO        ,
                   MI_DETALLEFUENTE     
            FROM   PLAN_PPTAL_CONFIG RUBRO_FUT_RESERVA
            WHERE  CODIGO=MI_RS5.RUBROFUT1--MI_RUBROFUT1 -- RUBRO FUT MODIFICADO
              AND ROWNUM<=1;
      EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_CODIGOTIPO:='';
            MI_COD_COMPONENTE:='';
            MI_COD_OBJETO:=0;
            MI_DETALLEFUENTE:=0;
      END;
   END IF;

           MI_STR:=
              MI_RS5.TIPO_REGISTRO || ';' 
             || TRIM(TO_CHAR(MI_CONSECUTIVO,'000000')) || ';' 
             || MI_VIGENCIA || ';' 
             || NVL(MI_RS5.ENTIDAD, MI_ENTIDAD) || ';' 
             || NVL(MI_RS5.UNIDAD_EJECUTORA, MI_UNIDADEJECUTORA) || ';'
             || MI_RS5.TIPODOCPAGO || ';' 
             || TRIM(RPAD(MI_RUBRO,' ')) || ';' 
             || SUBSTR(LPAD(MI_NUMERO_DISPON, 12),7,6) || ';' 
             || SUBSTR(LPAD(MI_RS5.CMPTE_AFECTADO, 12),7,6) || ';' 
             || MI_CODIGOTIPO || ';' 
             || MI_COD_COMPONENTE || ';' 
             || MI_COD_OBJETO || ';' 
             || NVL(MI_FUENTE,'') || ';' 
             || MI_DETALLEFUENTE || ';'       
             || MI_RS5.VALORNETO || ';';
         MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
         MI_REGISTROS:=MI_REGISTROS+1;
    END LOOP;
      MI_ETAPA:='7.1';
    END LOOP;
    MI_TF:=SYSDATE;

    RETURN MI_RETORNO;
END;


FUNCTION  FC_PLANOH_NOMINA 
(
  /*
    NAME                    : FC_PLANOH_NOMINA (En PLSQL ACCES  GENERAR_PLANO_HACIENDA_NOMINA)     
    AUTHORS                 : STEFANINI SYSMAN SAS
    AUTHORS MIGRACION       : CAMILO ANDRES PEREZ DUEÑAS 
    DATE MIGRADOR           : 13/01/2021
    TIME                    : 12:00 PM
    SOURCE MODULE           : 
    MODIFIER                : MARIA ALEJANDRA PEREZ SALAZAR
    DATA MODIFIED           : 05/12/2023
    TIME                    : 12:00 PM
    DESCRIPCION             : Se ajusta para agregar nit y documento de la compañia desde parametro 
    MODIFICATIONS           :
    MODIFIER                :
    TIME                    :
    MODIFICATIONS           :
    
    @NAME  : generarPlanoHNomina
    @METHOD:  GET    
    */
    UN_COMPANIA      PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIAL  VARCHAR2,
    UN_FECHAFINAL    VARCHAR2,
    UN_FUENTEINICIAL PCK_SUBTIPOS.TI_FUENTE_RECURSOS,
    UN_FUENTEFINAL   PCK_SUBTIPOS.TI_FUENTE_RECURSOS 
) RETURN CLOB
AS
  MI_SESSION            NUMBER;
  MI_FECHAINI           DATE        :=TO_DATE(UN_FECHAINICIAL,'DD/MM/RRRR');
  MI_FECHAFIN           DATE        :=TO_DATE(UN_FECHAFINAL,'DD/MM/RRRR');
  MI_FUENTEINI          PCK_SUBTIPOS.TI_FUENTE_RECURSOS :=NVL(UN_FUENTEINICIAL,' ');
  MI_FUENTEFIN          PCK_SUBTIPOS.TI_FUENTE_RECURSOS :=NVL(UN_FUENTEFINAL ,'99');
  MI_COMPANIA           VARCHAR2(11):= NVL(UN_COMPANIA,'001');
  MI_ETAPA              VARCHAR2(3200);
  MI_RUTASALIDA         VARCHAR2(3200):='IDIPRON_'||TO_CHAR(MI_FECHAINI,'DDMMRRRR')||'_'||TO_CHAR(MI_FECHAFIN,'DDMMRRRR')||'.TXT';
  MI_RUTALOG            VARCHAR2(3200):='LOG_'||MI_RUTASALIDA;
  MI_VERSION            VARCHAR2(3200):='GENERACION DE ARCHIVO PLANO HACIENDA DISTRITAL - VERSION 2019 RELEASE 22 ';
  MI_TI                 DATE          :=SYSDATE;
  MI_TF                 DATE          :=SYSDATE;
  MI_CONSECUTIVO        NUMBER        :=0;
  MI_REGISTROS          NUMBER        :=0;
  MI_CONTADOR           NUMBER        :=1;
  MI_CONTADOR1          NUMBER        :=1;
  MI_RETORNO            CLOB      :='';
  MI_STR                VARCHAR2(3200):='';
  MI_FECHAEGR           DATE;
  MI_STRCUARENTA        VARCHAR2(3200);
  MI_IMPORTE            NUMBER(15,2); 
  MI_COMPROBANTE_ANT    NUMBER(15,2); 
  MI_AUXCONT1           NUMBER(15,0) :=0;

  MI_DOCENTIDAD         VARCHAR2(4000 CHAR); -- TICKET7738649 MPEREZ
  MI_TIPOCC             VARCHAR2(6);         -- TICKET7738649 MPEREZ

BEGIN
  --INICIO  TICKET7738649 MPEREZ 
    MI_DOCENTIDAD     := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA, UN_NOMBRE =>'DOCUMENTO DE IDENTIDAD DE LA COMPANIA', 
                                UN_MODULO =>PCK_DATOS.MODULOCONTABILIDAD, UN_FECHA_PAR=>SYSDATE ); 

    BEGIN
      SELECT TIPOID  
      INTO MI_TIPOCC
      FROM TERCERO
      WHERE COMPANIA = UN_COMPANIA AND NIT = MI_DOCENTIDAD;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_TIPOCC := '';                
    END;
    --FIN TICKET7738649 MPEREZ  

  MI_ETAPA:='1';
  SELECT SYS_CONTEXT('USERENV','SESSIONID')
     INTO MI_SESSION
  FROM DUAL;
  MI_COMPROBANTE_ANT := 0;
  MI_ETAPA:='2' ;

  -- VAMOS CON REGISTRO TIPO 1
  <<PLANO>>
      
  FOR MI_RS1 IN
  (  SELECT  CC.COMPANIA 
        , CC.ANO 
        , CC.TIPO 
        , CC.NUMERO 
        , 'C' TIPO_REGISTRO_C
        , CC.FECHA FECHA_DOC
        , 'KR' CLASE
        , '1001' SOCIEDAD
        , CC.FECHA FECHA_CONTAB
        , EXTRACT(MONTH FROM CC.FECHA) PERIODO
        , 'COP' MONEDA
        ,'' TIPO_CAMBIO
        ,'NOMINA' REFERENCIA
        , CASE WHEN EXTRACT (MONTH FROM CC.FECHA) =  '1' 
               THEN 'PAGO NOMINA ENERO'
            ELSE 
          CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '2'
                THEN 'PAGO NOMINA FEBRERO'
            ELSE  
          CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '3'
                THEN 'PAGO NOMINA MARZO'
            ELSE 
          CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '4'
                THEN 'PAGO NOMINA ABRIL'
            ELSE  
          CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '5'
                THEN 'PAGO NOMINA MAYO'
            ELSE  
           CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '6'
                THEN 'PAGO NOMINA JUNIO'
             ELSE 
           CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '7'
                THEN 'PAGO NOMINA JULIO'
             ELSE 
           CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '8'
                THEN 'PAGO NOMINA AGOSTO'
             ELSE 
           CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '9'
                THEN 'PAGO NOMINA SEPTIEMBRE'
             ELSE  
           CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '10'
                THEN 'PAGO NOMINA OCTUBRE'
             ELSE  
           CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '11'
                THEN 'PAGO NOMINA NOVIEMBRE'
             ELSE 
                    'PAGO NOMINA DICIEMBRE'
            END 
            END 
            END 
            END 
            END 
            END 
            END 
            END 
            END  
            END 
            END TXT_CABECERA
        , TRIM(CC.DESCRIPCION) DESCRIPCION
        , 'P' TIPO_REGISTRO_P
        , '31' CLAVE_CONTAB
        , '' CODIGO_CUENTA
        , 'NIT' TIPO_IDENT
        , SUBSTR(C.NITCOMPANIA,1,instr(C.NITCOMPANIA,'-')-1) NO_IDENTIFICACION
        ,'' IDENTIFICADOR_CME
        ,'2511010001' CUENTA_CONTABLE
        ,'' IMPORTE 
        ,'' INDICADOR_IVA
        ,'' RP_DOC_PRESUPUESTAL
        ,'' POSC_DOC_PRES
        ,'' POS_PRES
        , '' PROGRAMACION_FINANCIACION
        ,'' FONDO
        ,'' CENTRO_GESTO
        ,'' CENTRO_COSTO
        ,'' CENTRO_BENEFICIO
        ,'' ORDEN_CO
        ,'' ELEMENTO_PEP
        ,'' GRAFO
        ,'' AREA_FUNCIONAL
        ,'' SEGMENTO
        ,'' FECHA_BASE
        ,'2141' CONDICION_PAGO
        ,'' ASIGNACION
        ,'' TEXTO
        ,'' BLOQUEO_PAGO
        ,'' RECEPTOR_ALTERNATIVO
        ,'' VIA_PAGO
        ,'' BANCO_PROPIO
        ,'' ID_CTA
        ,'' REF1
        ,'' REF2
        ,'' REFERENCIA_PAGO
        ,'051' CODIGO_BCO
        ,'006600658154' NO_CUENTA
        ,'02' TIPO_CTA
        , T.SUCURSAL
        ,'' TIPO_RETENCIONES
        ,'' INDICADOR_RETENCIONES
        ,'' BASE_IMPONIBLE_RETENCIONES
        ,'' IMPORTE_RETENCIONES
        , CC.TIPOCONTRATO
        , CC.NUMEROCONTRATO
        , CC.NRO_DOCUMENTO
        , CC.BANCO
        , CC.CUENTABANCO
        , CC.TIPODEPRESUPUESTO
        , CC.REGISTRO
   FROM COMPROBANTE_CNT CC
      INNER JOIN COMPANIA C
        ON  CC.COMPANIA  = C.CODIGO
      INNER JOIN TIPO_COMPROBANTE TC
        ON  CC.COMPANIA  = TC.COMPANIA
        AND CC.TIPO      = TC.CODIGO
      INNER JOIN DETALLE_COMPROBANTE_CNT DC
        ON  CC.COMPANIA  = DC.COMPANIA
        AND CC.TIPO      = DC.TIPO_CPTE
        AND CC.NUMERO    = DC.COMPROBANTE
      INNER JOIN DETALLE_COMPROBANTE_PPTAL DPPTAL
        ON  DC.COMPANIA        = DPPTAL.COMPANIA
        AND DC.TIPO_CPTE       = DPPTAL.TIPO_CPTE
        AND DC.COMPROBANTE     = DPPTAL.COMPROBANTE
      INNER JOIN PLAN_CONTABLE P
        ON  P.COMPANIA  = DC.COMPANIA
        AND P.ANO       = DC.ANO
        AND P.CODIGO        = DC.CUENTA
      INNER JOIN TERCERO T
        ON  T.COMPANIA  = CC.COMPANIA
        AND T.NIT       = CC.TERCERO
        AND T.SUCURSAL  = CC.SUCURSAL
   WHERE CC.COMPANIA     = MI_COMPANIA
      AND TC.CLASE_CONTABLE IN('P')
      AND CC.FECHA BETWEEN MI_FECHAINI AND MI_FECHAFIN
       AND  CC.PLANO_NOMINA <> '0'
       GROUP BY CC.COMPANIA
          , CC.ANO
          , CC.TIPO
          , CC.NUMERO
          , 'C'
          , CC.FECHA
          , 'NE'
          , '1001'
          , EXTRACT (MONTH FROM CC.FECHA)
          , 'COP'
          ,'NOMINA'
          , CASE WHEN EXTRACT (MONTH FROM CC.FECHA) =  '1' 
               THEN 'PAGO NOMINA ENERO'
            ELSE 
          CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '2'
                THEN 'PAGO NOMINA FEBRERO'
            ELSE  
          CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '3'
                THEN 'PAGO NOMINA MARZO'
            ELSE 
          CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '4'
                THEN 'PAGO NOMINA ABRIL'
            ELSE  
          CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '5'
                THEN 'PAGO NOMINA MAYO'
            ELSE  
           CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '6'
                THEN 'PAGO NOMINA JUNIO'
             ELSE 
           CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '7'
                THEN 'PAGO NOMINA JULIO'
             ELSE 
           CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '8'
                THEN 'PAGO NOMINA AGOSTO'
             ELSE 
           CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '9'
                THEN 'PAGO NOMINA SEPTIEMBRE'
             ELSE  
           CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '10'
                THEN 'PAGO NOMINA OCTUBRE'
             ELSE  
           CASE WHEN EXTRACT (MONTH FROM CC.FECHA) = '11'
                THEN 'PAGO NOMINA NOVIEMBRE'
             ELSE 
                    'PAGO NOMINA DICIEMBRE'
            END 
            END 
            END 
            END 
            END 
            END 
            END 
            END 
            END  
            END 
            END
          , TRIM(CC.DESCRIPCION)
          , 'P'
          , '31'
          , 'NIT'
          , SUBSTR(C.NITCOMPANIA,1,instr(C.NITCOMPANIA,'-')-1)
          ,'2511010001'
          , '2141'
          ,'051' 
          ,'006600658154' 
          ,'02'
          , T.SUCURSAL
          , CC.TIPOCONTRATO
          , CC.NUMEROCONTRATO
          , CC.NRO_DOCUMENTO
          , CC.BANCO
          , CC.CUENTABANCO
          , CC.TIPODEPRESUPUESTO
          , CC.REGISTRO
   ORDER BY CC.TIPO
          , CC.NUMERO
          , CC.FECHA
          ,SUM(
              DC.VALOR_DEBITO
              )
  )
  LOOP
 
    BEGIN 
      SELECT SUM(
                  DPPTAL.VALOR_DEBITO
                  )
      INTO MI_IMPORTE
      FROM DETALLE_COMPROBANTE_PPTAL DPPTAL 
       WHERE DPPTAL.COMPANIA   = MI_RS1.COMPANIA
         AND DPPTAL.TIPO_CPTE  = MI_RS1.TIPO
         AND DPPTAL.COMPROBANTE   = MI_RS1.NUMERO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_IMPORTE :=  0;
    END;
    
    MI_FECHAEGR:= MI_RS1.FECHA_DOC;
    
    
    MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
    
    IF MI_COMPROBANTE_ANT <> MI_RS1.NUMERO THEN
    
    --<<ENCABEZADO>> MI_FECHAEGR
    MI_STR        := SUBSTR(MI_RS1.TIPO_REGISTRO_C,1,10)                                    || CHR(09) 
                  || SUBSTR(MI_CONTADOR,1,10)                                               || CHR(09) 
                  || SUBSTR(REPLACE(TO_CHAR(MI_RS1.FECHA_DOC,'YYYY/MM/DD'),'/',''),1,8)     || CHR(09) 
                  || SUBSTR(MI_RS1.CLASE,1,2)                                               || CHR(09) 
                  || SUBSTR(MI_RS1.SOCIEDAD,1,4)                                            || CHR(09) 
                  || SUBSTR(REPLACE(TO_CHAR(MI_RS1.FECHA_CONTAB,'YYYY/MM/DD'),'/',''),1,8)  || CHR(09) 
                  || SUBSTR(MI_RS1.PERIODO,1,2)                                             || CHR(09) 
                  || SUBSTR(MI_RS1.MONEDA,1,3)                                              || CHR(09) 
                  || SUBSTR(MI_RS1.TIPO_CAMBIO,1,10)                                        || CHR(09)
                  || SUBSTR(MI_RS1.REFERENCIA,1,20)                                         || CHR(09) 
                  || SUBSTR(MI_RS1.TXT_CABECERA,1,50);
    --ENCABEZADO
    
    --'40'
     -- BARRE CURSOR 3 DETALLES
    MI_STRCUARENTA := '';
    <<POSICIONGASTO>>
    FOR MI_RS3 IN
    (SELECT DISTINCT 'P' TIPO_REGISTRO_P
          , '40' CLAVE_CONTAB
          , DCNT.ANO VIGENCIA
          , DPPTAL.COMPROBANTE
          ,'5101010000' CODIGO_CUENTA
          ,'' TIPO_IDENT
          ,'' NO_IDENTIFICACION
          ,'' IDENTIFICADOR_CME
          ,'' CUENTA_CONTABLE
          , DPPTAL.VALOR_DEBITO-DPPTAL.VALOR_CREDITO IMPORTE
          ,'' INDICADOR_IVA
          ,'5000065326' RP_DOC_PRESUPUESTAL
          , TO_CHAR(DPPTAL.CONSECUTIVO) POSC_DOC_PRES --- TICKET7738649 MPEREZ
          , '' POS_PRES
          , '' PROGRAMACION_FINANCIACION
          ,'' FONDO
          ,'' CENTRO_GESTOR
          ,'' CENTRO_COSTO
          ,'' CENTRO_BENEFICIO
          ,'' ORDEN_CO
          ,'' ELEMENTO_PEP
          ,'' GRAFO
          ,'' AREA_FUNCIONAL
          ,'' SEGMENTO
          ,'' FECHA_BASE
          ,'' CONDICION_PAGO
          ,'' ASIGNACION
          ,'' TEXTO
          ,'' BLOQUEO_PAGO
          ,'' RECEPTOR_ALTERNATIVO
          ,'' VIA_PAGO
          ,'' BANCO_PROPIO
          ,'' ID_CTA
          ,'' REF1
          ,'' REF2
          ,'' REFERENCIA_PAGO
          ,'' CODIGO_BCO
          ,'' NO_CUENTA
          ,'' TIPO_CTA
          ,'' TIPO_RETENCIONES
          ,'' INDICADOR_RETENCIONES
          ,'' BASE_IMPONIBLE_RETENCIONES
          ,'' IMPORTE_RETENCIONES
          , PP.CODIGO_EQUIV
          , DPPTAL.DESCRIPCION  
          , DPPTAL.CONSECUTIVO
     FROM DETALLE_COMPROBANTE_CNT DCNT
     INNER JOIN DETALLE_COMPROBANTE_PPTAL DPPTAL
        ON  DCNT.COMPANIA        = DPPTAL.COMPANIA
        AND DCNT.TIPO_CPTE       = DPPTAL.TIPO_CPTE
        AND DCNT.COMPROBANTE     = DPPTAL.COMPROBANTE
      INNER JOIN PLAN_CONTABLE P
        ON  P.COMPANIA  = DCNT.COMPANIA
        AND P.ANO       = DCNT.ANO
        AND P.CODIGO        = DCNT.CUENTA
      INNER JOIN PLAN_PRESUPUESTAL PP
         ON  PP.COMPANIA =  DPPTAL.COMPANIA
         AND PP.ANO      =  DPPTAL.ANO
         AND PP.CODIGO        =  DPPTAL.CUENTA 
     WHERE DCNT.COMPANIA    = MI_RS1.COMPANIA
       AND DCNT.TIPO_CPTE   = MI_RS1.TIPO
       AND DCNT.COMPROBANTE = MI_RS1.NUMERO
       AND P.CLASECUENTA    IN('C','A','N','G')
       AND PP.NATURALEZA    IN('D')
     GROUP BY
            'P' 
          , '40'
          , DCNT.ANO 
          , DPPTAL.COMPROBANTE
          ,'0214-01' 
          , '5101010000'
          , DPPTAL.VALOR_DEBITO-DPPTAL.VALOR_CREDITO
          , PP.CODIGO_EQUIV
          , DPPTAL.DESCRIPCION
          , CASE WHEN DPPTAL.CONSECUTIVO  >  10
             THEN '0' || TO_CHAR(DPPTAL.CONSECUTIVO)
             ELSE TO_CHAR(DPPTAL.CONSECUTIVO)
             END
           ,DPPTAL.CONSECUTIVO
     ORDER BY DPPTAL.COMPROBANTE, DPPTAL.CONSECUTIVO, DPPTAL.VALOR_DEBITO-DPPTAL.VALOR_CREDITO
     )
     LOOP
        BEGIN
          MI_STRCUARENTA := MI_STRCUARENTA ||CHR(13)||CHR(10);
          MI_STRCUARENTA := MI_STRCUARENTA 
                ||  SUBSTR(MI_RS3.TIPO_REGISTRO_P,1,1)            || CHR(09) 
                || SUBSTR(MI_RS3.CLAVE_CONTAB,1,2)                || CHR(09) 
                || SUBSTR(MI_RS3.CODIGO_CUENTA,1,10)              || CHR(09) 
                || SUBSTR(MI_RS3.TIPO_IDENT,1,2)                  || CHR(09) 
                || SUBSTR(MI_RS3.NO_IDENTIFICACION,1,12)          || CHR(09) 
                || SUBSTR(MI_RS3.IDENTIFICADOR_CME,1,1)           || CHR(09) 
                || SUBSTR(MI_RS3.CUENTA_CONTABLE,1,10)            || CHR(09) 
                || SUBSTR(MI_RS3.IMPORTE,1,13)                    || CHR(09) 
                || SUBSTR(MI_RS3.INDICADOR_IVA,1,2)               || CHR(09) 
                || SUBSTR(MI_RS3.RP_DOC_PRESUPUESTAL,1,20)        || CHR(09) 
                || SUBSTR(MI_RS3.POSC_DOC_PRES,1,15)              || CHR(09) 
                || SUBSTR(MI_RS3.POS_PRES,1,14)                   || CHR(09) 
                || SUBSTR(MI_RS3.PROGRAMACION_FINANCIACION,1,14)  || CHR(09) 
                || SUBSTR(MI_RS3.FONDO,1,10)                      || CHR(09) 
                || SUBSTR(MI_RS3.CENTRO_GESTOR,1,10)              || CHR(09) 
                || SUBSTR(MI_RS3.CENTRO_COSTO,1,10)               || CHR(09) 
                || SUBSTR(MI_RS3.CENTRO_BENEFICIO,1,10)           || CHR(09) 
                || SUBSTR(MI_RS3.ORDEN_CO,1,12)                   || CHR(09) 
                || SUBSTR(MI_RS3.ELEMENTO_PEP,1,24)               || CHR(09) 
                || SUBSTR(MI_RS3.GRAFO,1,12)                      || CHR(09) 
                || SUBSTR(MI_RS3.AREA_FUNCIONAL,1,4)              || CHR(09) 
                || SUBSTR(MI_RS3.SEGMENTO,1,10)                   || CHR(09) 
                || SUBSTR(MI_RS3.FECHA_BASE,1,8)                  || CHR(09) 
                || SUBSTR(MI_RS3.CONDICION_PAGO,1,4)              || CHR(09)
                || SUBSTR(MI_RS3.ASIGNACION,1,18)                 || CHR(09)
                || SUBSTR(MI_RS3.TEXTO,1,50)                      || CHR(09)
                || SUBSTR(MI_RS3.BLOQUEO_PAGO,1,1)                || CHR(09)
                || SUBSTR(MI_RS3.RECEPTOR_ALTERNATIVO,1,10)       || CHR(09)
                || SUBSTR(CASE WHEN MI_TIPOCC =  'C' THEN 'CC' ELSE CASE WHEN MI_TIPOCC =  'T' THEN 'TI' ELSE  CASE WHEN  MI_TIPOCC  =  'N' THEN 'NIT' ELSE MI_TIPOCC END  END END,1,6) || CHR(09) --- TICKET7738649 MPEREZ
                || SUBSTR(MI_DOCENTIDAD,1,15)                     || CHR(09) -- TICKET7738649 MPEREZ
                || SUBSTR(MI_RS3.VIA_PAGO,1,2)                    || CHR(09)
                || SUBSTR(MI_RS3.BANCO_PROPIO,1,2)                || CHR(09)
                || SUBSTR(MI_RS3.ID_CTA,1,2)                      || CHR(09)
                || SUBSTR(MI_RS3.REF1,1,2)                        || CHR(09)
                || SUBSTR(MI_RS3.REF2,1,2)                        || CHR(09)
                || SUBSTR(MI_RS3.REFERENCIA_PAGO,1,2)             || CHR(09)
                || SUBSTR(MI_RS1.CODIGO_BCO,1,7)                  || CHR(09) --- TICKET7738649 MPEREZ
                || SUBSTR(MI_RS1.NO_CUENTA,1,20)                  || CHR(09) --- TICKET7738649 MPEREZ
                || SUBSTR(MI_RS1.TIPO_CTA,1,2)                    || CHR(09) --- TICKET7738649 MPEREZ
                || SUBSTR(MI_RS3.TIPO_RETENCIONES,1,2)            || CHR(09)
                || SUBSTR(MI_RS3.INDICADOR_RETENCIONES,1,2)       || CHR(09)
                || SUBSTR(MI_RS3.BASE_IMPONIBLE_RETENCIONES,1,2)  || CHR(09)
                || SUBSTR(MI_RS3.IMPORTE_RETENCIONES,1,2);
        
         MI_CONTADOR1 :=MI_CONTADOR1 +1;
        EXCEPTION WHEN OTHERS THEN
        MI_STRCUARENTA := '';
        END;        
     END LOOP POSICIONGASTO;
     MI_STR := MI_STR || MI_STRCUARENTA;
     --<TAR:105576 FECHA:11/05/2021 AUTOR:CP>--Se agrega condición para que no deje una salto de carro al comienzo del archivo plano
      MI_AUXCONT1 :=  MI_AUXCONT1 + 1 ;
      IF NOT MI_AUXCONT1  =  1 THEN 
        MI_RETORNO := MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
      ELSE
        MI_RETORNO := MI_STR;
      END IF ;
     --</TAR>
    --40
     MI_CONTADOR :=MI_CONTADOR +1;
   END IF;
   
  
    --'31'
      MI_STR := '';   
     
   
       --<<CLAVECONTABILIZACION>>
    MI_STR        :=  MI_RS1.TIPO_REGISTRO_P                                          || CHR(09) 
                  || SUBSTR(MI_RS1.CLAVE_CONTAB,1,2)                                  || CHR(09) 
                  || SUBSTR(MI_RS1.CODIGO_CUENTA,1,10)                                || CHR(09) 
                  || SUBSTR(MI_RS1.TIPO_IDENT,1,3)                                    || CHR(09) 
                  || SUBSTR(MI_RS1.NO_IDENTIFICACION,1,12)                            || CHR(09) 
                  || SUBSTR(MI_RS1.IDENTIFICADOR_CME,1,1)                             || CHR(09)
                  || SUBSTR(MI_RS1.CUENTA_CONTABLE,1,10)                              || CHR(09) 
                  || SUBSTR(MI_IMPORTE,1,20)                                          || CHR(09) 
                  || SUBSTR(MI_RS1.INDICADOR_IVA,1,2)                                 || CHR(09) 
                  || SUBSTR(MI_RS1.RP_DOC_PRESUPUESTAL,1,10)                          || CHR(09) 
                  || SUBSTR(MI_RS1.POSC_DOC_PRES,1,3)                                 || CHR(09) 
                  || SUBSTR(MI_RS1.POS_PRES,1,14)                                     || CHR(09) 
                  || SUBSTR(MI_RS1.PROGRAMACION_FINANCIACION,1,24)                    || CHR(09) 
                  || SUBSTR(MI_RS1.FONDO,1,10)                                        || CHR(09) 
                  || SUBSTR(MI_RS1.CENTRO_GESTO,1,10)                                 || CHR(09) 
                  || SUBSTR(MI_RS1.CENTRO_COSTO,1,10)                                 || CHR(09) 
                  || SUBSTR(MI_RS1.CENTRO_BENEFICIO,1,10)                             || CHR(09) 
                  || SUBSTR(MI_RS1.ORDEN_CO,1,12)                                     || CHR(09) 
                  || SUBSTR(MI_RS1.ELEMENTO_PEP,1,24)                                 || CHR(09) 
                  || SUBSTR(MI_RS1.GRAFO,1,12)                                        || CHR(09) 
                  || SUBSTR(MI_RS1.AREA_FUNCIONAL,1,4)                                || CHR(09) 
                  || SUBSTR(MI_RS1.SEGMENTO,1,10)                                     || CHR(09) 
                  || SUBSTR(MI_RS1.FECHA_BASE,1,8)                                    || CHR(09) 
                  || SUBSTR(MI_RS1.CONDICION_PAGO,1,4)                                || CHR(09) 
                  || SUBSTR(MI_RS1.ASIGNACION,1,18)                                   || CHR(09) 
                  || SUBSTR(MI_RS1.TEXTO,1,50)                                        || CHR(09) 
                  || SUBSTR(MI_RS1.BLOQUEO_PAGO,1,1)                                  || CHR(09) 
                  || SUBSTR(MI_RS1.RECEPTOR_ALTERNATIVO,1,10)                         || CHR(09) 
                  || SUBSTR(CASE WHEN MI_TIPOCC =  'C' THEN 'CC' ELSE CASE WHEN MI_TIPOCC =  'T' THEN 'TI' ELSE  CASE WHEN  MI_TIPOCC  =  'N' THEN 'NIT' ELSE MI_TIPOCC END  END END,1,6) || CHR(09) -- TICKET7737931 MPEREZ 
                  || SUBSTR(MI_DOCENTIDAD,1,15)                                       || CHR(09) -- TICKET7737931 MPEREZ 
                  || SUBSTR(MI_RS1.VIA_PAGO,1,1)                                      || CHR(09) 
                  || SUBSTR(MI_RS1.BANCO_PROPIO,1,5)                                  || CHR(09)
                  || SUBSTR(MI_RS1.ID_CTA,1,5)                                        || CHR(09) 
                  || SUBSTR(MI_RS1.REF1,1,12)                                         || CHR(09) 
                  || SUBSTR(MI_RS1.REF2,1,12)                                         || CHR(09) 
                  || SUBSTR(MI_RS1.REFERENCIA_PAGO,1,20)                              || CHR(09) 
                  || SUBSTR(MI_RS1.CODIGO_BCO,1,7)                                    || CHR(09) 
                  || SUBSTR(MI_RS1.NO_CUENTA,1,20)                                    || CHR(09) 
                  || SUBSTR(MI_RS1.TIPO_CTA,1,2)                                      || CHR(09) 
                  || SUBSTR(MI_RS1.TIPO_RETENCIONES,1,12)                             || CHR(09) 
                  || SUBSTR(MI_RS1.INDICADOR_RETENCIONES,1,12)                        || CHR(09) 
                  || SUBSTR(MI_RS1.BASE_IMPONIBLE_RETENCIONES,1,12)                   || CHR(09) 
                  || SUBSTR(MI_RS1.IMPORTE_RETENCIONES,1,20);
    --CLAVECONTABILIZACION
    --'31'
       MI_RETORNO  := MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
  
     
    MI_COMPROBANTE_ANT:= MI_RS1.NUMERO;
    
    MI_REGISTROS:=MI_REGISTROS+1;
    MI_CONTADOR1 := 1;
    --'31'
    --<TAR:105576 FECHA:11/05/2021 AUTOR:CP> ''Se comenta la linea ya que esta duplicando la linea 31
      -- MI_RETORNO:=MI_RETORNO || MI_STR;
    --</TAR>
     MI_ETAPA:='7.1';
  END LOOP PLANO;
  MI_TF:=SYSDATE;
  RETURN MI_RETORNO;
END;



FUNCTION FC_PREPARARPIVOT_INFORMEAUXGC /*
    NAME              : FC_PREPARARPIVOT_INFORMEAUXGC
    AUTHORS           : SYSMAN  SAS
    AUTHOR            : GUSTAVO ANDRÉS FIGUEREDO �?VILA
    DATE              : 02/06/2021
    TIME              : 03:20 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Retorna la cadena del PIVOT para consultar los auxiliares por auxiliar general y cuenta.
    @NAME             : getPrepararPivotInformeAuxGC
    @METHOD           : GET                    
  */
( UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA, UN_ANO IN PCK_SUBTIPOS.TI_ANIO, UN_AUXILIAR_I IN PCK_SUBTIPOS.TI_AUXILIAR, UN_AUXILIAR_F IN PCK_SUBTIPOS.TI_AUXILIAR, UN_CUENTA_I IN varchar2, UN_CUENTA_F IN varchar2, UN_FECHA_I IN PCK_SUBTIPOS.TI_FECHA, UN_FECHA_F IN PCK_SUBTIPOS.TI_FECHA ) RETURN VARCHAR2 AS MI_RESULTADO PCK_SUBTIPOS.TI_STRSQL;
BEGIN
BEGIN
		<<RECORREACUMULADOS>> FOR RS IN (
		SELECT
			DISTINCT '''' || DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO || '-' || MAX(REPLACE(REPLACE(FUENTE_RECURSOS.NOMBRE,'(',''),')','')) AS NOMBREAUXILIAR
		FROM
			(COMPROBANTE_CNT
		INNER JOIN ((PLAN_CONTABLE
		INNER JOIN DETALLE_COMPROBANTE_CNT ON
			(PLAN_CONTABLE.ANO = DETALLE_COMPROBANTE_CNT.ANO)
			AND (PLAN_CONTABLE.COMPANIA = DETALLE_COMPROBANTE_CNT.COMPANIA)
			AND (PLAN_CONTABLE.CODIGO = DETALLE_COMPROBANTE_CNT.CUENTA))
		INNER JOIN FUENTE_RECURSOS ON
			(DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO = FUENTE_RECURSOS.CODIGO)
			AND (DETALLE_COMPROBANTE_CNT.COMPANIA = FUENTE_RECURSOS.COMPANIA)
			AND (DETALLE_COMPROBANTE_CNT.ANO = FUENTE_RECURSOS.ANO)) ON
			(COMPROBANTE_CNT.NUMERO = DETALLE_COMPROBANTE_CNT.COMPROBANTE)
			AND (COMPROBANTE_CNT.TIPO = DETALLE_COMPROBANTE_CNT.TIPO_CPTE)
			AND (COMPROBANTE_CNT.ANO = DETALLE_COMPROBANTE_CNT.ANO)
			AND (COMPROBANTE_CNT.COMPANIA = DETALLE_COMPROBANTE_CNT.COMPANIA))
		INNER JOIN TIPO_COMPROBANTE ON
			(COMPROBANTE_CNT.TIPO = TIPO_COMPROBANTE.CODIGO)
			AND (COMPROBANTE_CNT.COMPANIA = TIPO_COMPROBANTE.COMPANIA)
		WHERE
			DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
			AND DETALLE_COMPROBANTE_CNT.ANO = UN_ANO
			AND DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO BETWEEN UN_AUXILIAR_I AND UN_AUXILIAR_F
			AND DETALLE_COMPROBANTE_CNT.CUENTA BETWEEN UN_CUENTA_I AND UN_CUENTA_F
			AND DETALLE_COMPROBANTE_CNT.FECHA BETWEEN UN_FECHA_I AND UN_FECHA_F
			AND TIPO_COMPROBANTE.CLASE_CONTABLE NOT IN ('Z')
		GROUP BY
			DETALLE_COMPROBANTE_CNT.FUENTE_RECURSO ) LOOP MI_RESULTADO := MI_RESULTADO || RS.NOMBREAUXILIAR || ''',' ;
END LOOP RECORREACUMULADOS;

MI_RESULTADO := SUBSTR(MI_RESULTADO, 1, LENGTH(MI_RESULTADO)-1);

MI_RESULTADO := REPLACE(MI_RESULTADO, '/', '');

MI_RESULTADO := REPLACE(MI_RESULTADO, '%', '');

MI_RESULTADO := REPLACE(MI_RESULTADO, '.', '');

EXCEPTION
WHEN NO_DATA_FOUND THEN MI_RESULTADO := '';
END;

RETURN MI_RESULTADO;
END FC_PREPARARPIVOT_INFORMEAUXGC;

FUNCTION FC_PREPARARPIVOT_INFORMEAUXCCT /*
    NAME              : FC_PREPARARPIVOT_INFORMEAUXCCT
    AUTHORS           : SYSMAN  SAS
    AUTHOR            : GUSTAVO ANDRÉS FIGUEREDO �?VILA
    DATE              : 02/06/2021
    TIME              : 05:20 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Retorna la cadena del PIVOT para consultar los auxiliares
    por centro de costo, tercero y auxiliar general.
    @NAME             : getPrepararPivotInformeAuxCCT
    @METHOD           : GET                    
  */
    (UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA, 
     UN_ANO IN PCK_SUBTIPOS.TI_ANIO, 
     UN_CUENTA_I IN varchar2, 
     UN_CUENTA_F IN varchar2, 
     UN_TERCERO_I IN PCK_SUBTIPOS.TI_TERCERO, 
     UN_TERCERO_F IN PCK_SUBTIPOS.TI_TERCERO, 
     UN_CENTRO_I IN PCK_SUBTIPOS.TI_CENTRO_COSTO, 
     UN_CENTRO_F IN PCK_SUBTIPOS.TI_CENTRO_COSTO, 
     UN_AUXILIAR_I IN PCK_SUBTIPOS.TI_AUXILIAR, 
     UN_AUXILIAR_F IN PCK_SUBTIPOS.TI_AUXILIAR, 
     UN_COMPROBANTE_I IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE, 
     UN_COMPROBANTE_F IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
     UN_MES_I IN PCK_SUBTIPOS.TI_MES,
     UN_MES_F IN PCK_SUBTIPOS.TI_MES
     ) 
RETURN VARCHAR2 AS MI_RESULTADO PCK_SUBTIPOS.TI_STRSQL;
BEGIN 
    MI_RESULTADO:='''Total Cuenta''';
    BEGIN 
        <<RECORREACUMULADOS>> 
        FOR RS IN (
                SELECT DISTINCT dcc.CENTRO_COSTOI || ' - ' || cc.NOMBRE AS CENTRO_COSTO
                FROM DETALLE_COMPROBANTE_CNT dcc
                INNER JOIN CENTRO_COSTO cc 
                   ON dcc.COMPANIA = cc.COMPANIA 
                  AND dcc.ANO = cc.ANO 
                  AND dcc.CENTRO_COSTOI = cc.CODIGO 
                WHERE dcc.COMPANIA = UN_COMPANIA
                  AND dcc.ANO = UN_ANO
                  AND dcc.TIPO_CPTE BETWEEN UN_COMPROBANTE_I AND UN_COMPROBANTE_F
                  AND dcc.CUENTA BETWEEN UN_CUENTA_I AND UN_CUENTA_F
                  AND dcc.TERCEROI BETWEEN UN_TERCERO_I AND UN_TERCERO_F
                  AND dcc.CENTRO_COSTOI BETWEEN UN_CENTRO_I AND UN_CENTRO_F
                  AND dcc.FUENTE_RECURSOI BETWEEN UN_AUXILIAR_I AND UN_AUXILIAR_F
				 AND dcc.MES BETWEEN UN_MES_I AND UN_MES_F
                ORDER BY  dcc.CENTRO_COSTOI || ' - ' || cc.NOMBRE
                ) 
        LOOP 
            MI_RESULTADO := MI_RESULTADO || ',''' || RS.CENTRO_COSTO || '''';
        END LOOP RECORREACUMULADOS;
    EXCEPTION
    WHEN NO_DATA_FOUND THEN MI_RESULTADO := '';
    END;
    
    RETURN MI_RESULTADO;
END FC_PREPARARPIVOT_INFORMEAUXCCT;



PROCEDURE PR_CAMBIOABONO(
  /*
  NAME              : PR_VALOR_CAMBIOABONO
  AUTHORS           : ELKIN  GEOVANNY AMAYA SILVA
  DATE              : 13/07/2021
  TIME              : 08:40 AM
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : 
  MODIFICATIONS     : 
  PARAMETERS        : 

  @NAME: actualizarAbono
  @METHOD: put
  */
   UN_COMPANIA	        IN PCK_SUBTIPOS.TI_COMPANIA,
   UN_ANO               IN PCK_SUBTIPOS.TI_ANIO,
   UN_TIPO_CPTE         IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
   UN_COMPROBANTE       IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
   UN_CONSECUTIVO       IN PCK_SUBTIPOS.TI_CONSECUTIVOCNT,
   UN_ABONO             IN PCK_SUBTIPOS.TI_DOBLE    ,
   UN_FECHAABONO        IN PCK_SUBTIPOS.TI_FECHA,
   UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO
   )
AS
  MI_CAMPOS    PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION  PCK_SUBTIPOS.TI_CONDICION;
BEGIN
  BEGIN 
    
     PCK_GENERALES.GL_CAMBIONIT := 1;
         MI_CAMPOS := 'ABONOINICIAL = '||UN_ABONO || '
                      ,FECHA_ABONOINICIAL = '''|| UN_FECHAABONO ||''' 
                      ,MODIFIED_BY = '''||UN_USUARIO||''' 
                      ,DATE_MODIFIED = CURRENT_DATE ';

         MI_CONDICION := '      COMPANIA    = '''||UN_COMPANIA||
                         ''' AND ANO         = '||UN_ANO||
                          '  AND TIPO_CPTE        = '''||UN_TIPO_CPTE||
                         ''' AND COMPROBANTE      = '||UN_COMPROBANTE||'
                             AND CONSECUTIVO = '||UN_CONSECUTIVO||' ' ;
                          
         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETALLE_COMPROBANTE_CNT',
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);    
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END; 

    PCK_GENERALES.GL_CAMBIONIT := 0;
   
END PR_CAMBIOABONO;

FUNCTION FC_ACT_TOTAL_COMP_FISCAL(
  /*
  * AUTOR: LUIS JACOBO DIAZ MUÑOZ
  * FECHA: 09/02/2023
  * DESCRIPCION:  SE ACUTALIZA LOS VALORES DEL COMPROBANTE FISCAL CUANDOS AGREGA UN NUEVO DETALLE.
  */
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO      IN PCK_SUBTIPOS.TI_ANIO,
    UN_COMPROBANTE  IN VARCHAR2)
    RETURN VARCHAR2 AS
       MI_DEBITOS    NUMBER;
       MI_CREDITOS   NUMBER;
       MI_CAMPOS     VARCHAR2(1000);
       MI_CONDICION  VARCHAR2(1000);
    BEGIN
        BEGIN
            SELECT SUM(SALDO_DEBITO), SUM(SALDO_CREDITO) INTO MI_DEBITOS, MI_CREDITOS FROM DETALLE_BALANCE_FISCAL WHERE COMPANIA = UN_COMPANIA AND ANO = UN_ANO AND COMPROBANTE = UN_COMPROBANTE;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_DEBITOS :=0;
            MI_CREDITOS := 0;
        END;
    BEGIN 
         MI_CAMPOS := 'DEBITO = '||MI_DEBITOS || '
                      ,CREDITO = '|| MI_CREDITOS ||'';

         MI_CONDICION := '   COMPANIA    = '''||UN_COMPANIA||
                         ''' AND ANO         = '||UN_ANO||
                         ' AND COMPROBANTE      = '''||UN_COMPROBANTE||'''';

         PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'BALANCE_FISCAL',
                                                  UN_ACCION    => 'M',
                                                  UN_CAMPOS    => MI_CAMPOS,
                                                  UN_CONDICION => MI_CONDICION);    
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
              RETURN '0';
        END; 
    RETURN '1';
END FC_ACT_TOTAL_COMP_FISCAL;

FUNCTION FC_GENERAR_PLANO_CNT 

   /*
    NAME              : FC_GENERAR_PLANO_CNT
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 26/06/2024
    TIME              : 10:47 AM    
    @NAME:   generarPlanoCnt
    @METHOD:  GET
    */
(
     UN_COMPANIA      IN    PCK_SUBTIPOS.TI_COMPANIA,
     UN_ANO           IN    PCK_SUBTIPOS.TI_ANIO,
     UN_TIPO_CPTE     IN    PCK_SUBTIPOS.TI_CLASECOMPROPPTO,
     UN_COMPROBANTE   IN    PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL
)
RETURN CLOB AS 
    MI_RS1              NUMBER;
    MI_RETORNO          CLOB:='';
    MI_STR              VARCHAR2(32000):='';
    MI_STR1             VARCHAR2(32000) := '';

BEGIN
     
     FOR MI_RS1 IN
           ( 
      SELECT   COMPROBANTE_CNT.TIPO, 
               COMPROBANTE_CNT.NUMERO,
               DETALLE_COMPROBANTE_CNT.CUENTA,
               TO_CHAR(COMPROBANTE_CNT.FECHA,'DD/MM/YYYY') FECHA,
               DETALLE_COMPROBANTE_CNT.VALOR_DEBITO,
               DETALLE_COMPROBANTE_CNT.VALOR_CREDITO,
               DETALLE_COMPROBANTE_CNT.TERCERO,
               DETALLE_COMPROBANTE_CNT.SUCURSAL,
               DETALLE_COMPROBANTE_CNT.CENTRO_COSTO,
               DETALLE_COMPROBANTE_CNT.AUXILIAR,
               NVL(DETALLE_COMPROBANTE_CNT.TIPO_CPTE_AFECT||DETALLE_COMPROBANTE_CNT.CMPTE_AFECTADO, ' ') NRO_DOCUMENTO,
               DETALLE_COMPROBANTE_CNT.DESCRIPCION,
               NVL(DETALLE_COMPROBANTE_CNT.CUENTAPPTAL,' ') CUENTAPPTAL,
               DETALLE_COMPROBANTE_CNT.REFERENCIA,
               '9999999999999999' UNIDAD_EJECUTORA       
          FROM COMPROBANTE_CNT
    INNER JOIN DETALLE_COMPROBANTE_CNT 
            ON COMPROBANTE_CNT.COMPANIA = DETALLE_COMPROBANTE_CNT.COMPANIA
           AND COMPROBANTE_CNT.ANO = DETALLE_COMPROBANTE_CNT.ANO
           AND COMPROBANTE_CNT.TIPO = DETALLE_COMPROBANTE_CNT.TIPO_CPTE
           AND COMPROBANTE_CNT.NUMERO = DETALLE_COMPROBANTE_CNT.COMPROBANTE
         WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
           AND DETALLE_COMPROBANTE_CNT.ANO = UN_ANO
           AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = UN_TIPO_CPTE
           AND COMPROBANTE_CNT.NUMERO = UN_COMPROBANTE
      ORDER BY DETALLE_COMPROBANTE_CNT.CONSECUTIVO
            )
    LOOP    
            MI_STR :=  '' ;
            MI_STR := RPAD(SUBSTR(MI_RS1.TIPO,1,3),3)
            || RPAD(SUBSTR(MI_RS1.NUMERO,1,10),10) 
            || RPAD(SUBSTR(MI_RS1.CUENTA,1,16),16) 
            || RPAD(SUBSTR(MI_RS1.FECHA,1,10),10)
            || LPAD(SUBSTR(MI_RS1.VALOR_DEBITO,1,18),18,0)
            || LPAD(SUBSTR(MI_RS1.VALOR_CREDITO,1,18),18,0)
            || RPAD(SUBSTR(MI_RS1.TERCERO,1,11),11) 
            || RPAD(SUBSTR(MI_RS1.SUCURSAL,1,3),3)
            || RPAD(SUBSTR(MI_RS1.CENTRO_COSTO,1,10),10)
            || RPAD(SUBSTR(MI_RS1.AUXILIAR,1,16),16)
            || RPAD(SUBSTR(MI_RS1.NRO_DOCUMENTO,1,30),30)  
            || RPAD(SUBSTR(MI_RS1.DESCRIPCION,1,64),64) 
            || RPAD(SUBSTR(MI_RS1.CUENTAPPTAL,1,16),16)   
            || RPAD(SUBSTR(MI_RS1.REFERENCIA,1,16),16)    
            || RPAD(SUBSTR(MI_RS1.UNIDAD_EJECUTORA,1,16),16);             

        IF LENGTH(MI_RETORNO) > 0 THEN
            MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
        ELSE
            MI_RETORNO:=MI_STR;
        END IF;   
    END LOOP;
    RETURN MI_RETORNO;
END FC_GENERAR_PLANO_CNT;

PROCEDURE PR_ACT_VALOR_IVA 
 /*
    NAME              : PR_ACT_VALOR_IVA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : SEBASTIAN CARDENAS
    DATE MIGRADOR     : 01/07/2025
    TIME              : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       :
    MODIFICATIONS     : 

  --NAME:
  */
(
  UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPO                  IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE,
  UN_ANIO                  IN DETALLE_COMPROBANTE_CNT.ANO%TYPE,
  UN_COMPROBANTE           IN DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE,
  UN_CONSECUTIVO           IN PCK_SUBTIPOS.TI_ENTERO,
  UN_TIPO_AFEC             IN DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE,
  UN_ANIO_AFEC             IN DETALLE_COMPROBANTE_CNT.ANO%TYPE,
  UN_COMPROBANTE_AFEC      IN DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE,
  UN_VALOR_NUEVO           IN PCK_SUBTIPOS.TI_DOBLE,
  UN_CUENTA                IN DETALLE_COMPROBANTE_CNT.CUENTA%TYPE
)AS
  MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION             PCK_SUBTIPOS.TI_CONDICION;
  MI_CRITERIO              PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
  MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
  MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_VALOR                 PCK_SUBTIPOS.TI_DOBLE;
  MI_VALOR_IVA             PCK_SUBTIPOS.TI_DOBLE;
  MI_IVA                   PCK_SUBTIPOS.TI_DOBLE;
  MI_CONSECUTIVOAFECT      DETALLE_COMPROBANTE_CNT.CONSECUTIVOAFECTADO%TYPE;
BEGIN
 IF UN_VALOR_NUEVO <> 0 THEN
 
    BEGIN
        SELECT CONSECUTIVOAFECTADO
            INTO MI_CONSECUTIVOAFECT
        FROM DETALLE_COMPROBANTE_CNT
            WHERE COMPANIA = UN_COMPANIA
            AND ANO = UN_ANIO
            AND TIPO_CPTE = UN_TIPO
            AND COMPROBANTE = UN_COMPROBANTE
            AND CONSECUTIVO = UN_CONSECUTIVO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CONSECUTIVOAFECT := 0;
    END;
 
                  BEGIN
                    SELECT NVL(VALOR_DEBITO,0) VALOR_DEBITO,
                           VALOR_IVA
                      INTO MI_VALOR,
                           MI_IVA
                    FROM DETALLE_COMPROBANTE_CNT
                    WHERE COMPANIA = UN_COMPANIA
                    AND TIPO_CPTE = UN_TIPO_AFEC
                    AND ANO = UN_ANIO_AFEC
                    AND COMPROBANTE = UN_COMPROBANTE_AFEC
                    AND CUENTA = UN_CUENTA
                    AND CONSECUTIVO = MI_CONSECUTIVOAFECT;
                EXCEPTION WHEN NO_DATA_FOUND THEN
                   MI_VALOR := 0;
                   MI_IVA := 0;
                END;
        IF MI_VALOR <> 0 THEN --MOD JM CC 2686        
            MI_VALOR_IVA := (MI_IVA * UN_VALOR_NUEVO) / MI_VALOR;
         ELSE 
            MI_VALOR_IVA := MI_IVA;
        END IF;
                
   BEGIN
      MI_TABLA     := 'DETALLE_COMPROBANTE_CNT';
      MI_CAMPOS    := 'VALOR_IVA = '''|| MI_VALOR_IVA ||'''';
      MI_CONDICION := '    COMPANIA   = ''' || UN_COMPANIA||'''
                       AND TIPO_CPTE = ''' || UN_TIPO ||'''
                       AND COMPROBANTE     = '''  || UN_COMPROBANTE ||'''
                       AND ANO = ''' || UN_ANIO ||'''
                       AND CONSECUTIVO = ''' || UN_CONSECUTIVO ||''' ';
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                   UN_ACCION     => 'M',
                                   UN_CAMPOS     => MI_CAMPOS,
                                   UN_CONDICION  => MI_CONDICION);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
   END;
 END IF;
END PR_ACT_VALOR_IVA;

FUNCTION FC_GETCONCILIA_NUM
(
    UN_COMPANIA IN VARCHAR2,
    UN_CUENTA IN VARCHAR2,
    UN_CLASESCONTABLES IN VARCHAR2,
    UN_ULTIMODIA IN VARCHAR2,
    UN_MES IN NUMBER,
    UN_NUMERO_ANO IN NUMBER,
    UN_TIPO_CPTE IN VARCHAR2 DEFAULT NULL,
    UN_COMPROBANTE IN VARCHAR2 DEFAULT NULL,
    UN_CONSECUTIVO IN VARCHAR2 DEFAULT NULL,
    UN_NRO_DOCUMENTO IN VARCHAR2 DEFAULT NULL,
    UN_VALOR_DEBITO IN VARCHAR2 DEFAULT NULL,
    UN_VALOR_CREDITO IN VARCHAR2 DEFAULT NULL,
    UN_TERCERO IN VARCHAR2 DEFAULT NULL,
    UN_DESCRIPCION IN VARCHAR2 DEFAULT NULL,
    UN_PAGADOBANCO IN VARCHAR2 DEFAULT NULL,
    UN_FECHA_CONCILIA IN VARCHAR2 DEFAULT NULL,
    UN_TIPO_CPTE_AFECT IN VARCHAR2 DEFAULT NULL,
    UN_CMPTE_AFECTADO IN VARCHAR2 DEFAULT NULL,
    UN_SINIDENTIFICAR IN VARCHAR2 DEFAULT NULL,
    UN_FECHA_IDENTIFICACION IN VARCHAR2 DEFAULT NULL,
    UN_CONCILIADOR IN VARCHAR2 DEFAULT NULL,
    UN_IMPRESO IN VARCHAR2 DEFAULT NULL,
    UN_ENTREGADO IN VARCHAR2 DEFAULT NULL,
    UN_FECHA IN VARCHAR2 DEFAULT NULL
) RETURN NUMBER
IS
    MI_RTA NUMBER := 0;
BEGIN
    SELECT COUNT(1) INTO MI_RTA
    FROM (
        SELECT D.TIPO_CPTE, D.COMPROBANTE, D.CONSECUTIVO, D.NRO_DOCUMENTO, 
               D.VALOR_DEBITO, D.VALOR_CREDITO, D.TERCERO, D.DESCRIPCION,
               TO_CHAR(D.FECHA, 'DD/MM/YYYY HH24:MI:SS') AS FECHA_STR
        FROM DETALLE_COMPROBANTE_CNT D
        INNER JOIN TIPO_COMPROBANTE T ON T.COMPANIA = D.COMPANIA AND T.CODIGO = D.TIPO_CPTE
        WHERE D.COMPANIA = UN_COMPANIA
          AND D.CUENTA = UN_CUENTA
          AND INSTR(REPLACE(CONCAT(CONCAT('''', REPLACE(UN_CLASESCONTABLES, ' ', '')), ''''), ',', ''','''), T.CLASE_CONTABLE, 1) > 0
          AND ( ( TRUNC(D.FECHA) <= TO_DATE(UN_ULTIMODIA, 'DD/MM/YYYY') AND D.PAGADOBANCO = 0 ) 
                OR ( TO_NUMBER(TO_CHAR(D.FECHA_CONCILIA, 'MM')) = UN_MES AND TO_NUMBER(TO_CHAR(D.FECHA_CONCILIA, 'YYYY')) = UN_NUMERO_ANO ) )
    ) VistaFiltro
    WHERE (UN_TIPO_CPTE IS NULL OR UPPER(TIPO_CPTE) LIKE UPPER('%' || UN_TIPO_CPTE || '%'))
      AND (UN_COMPROBANTE IS NULL OR UPPER(COMPROBANTE) LIKE UPPER('%' || UN_COMPROBANTE || '%'))
      AND (UN_CONSECUTIVO IS NULL OR UPPER(TO_CHAR(CONSECUTIVO)) LIKE UPPER('%' || UN_CONSECUTIVO || '%'))
      AND (UN_NRO_DOCUMENTO IS NULL OR UPPER(NRO_DOCUMENTO) LIKE UPPER('%' || UN_NRO_DOCUMENTO || '%'))
      AND (UN_VALOR_DEBITO IS NULL OR TO_CHAR(NVL(VALOR_DEBITO, 0)) LIKE UN_VALOR_DEBITO || '%')
      AND (UN_VALOR_CREDITO IS NULL OR TO_CHAR(NVL(VALOR_CREDITO, 0)) LIKE UN_VALOR_CREDITO || '%')
      AND (UN_TERCERO IS NULL OR UPPER(TERCERO) LIKE UPPER('%' || UN_TERCERO || '%'))
      AND (UN_DESCRIPCION IS NULL OR UPPER(DESCRIPCION) LIKE UPPER('%' || UN_DESCRIPCION || '%'));

    RETURN NVL(MI_RTA, 0);
EXCEPTION WHEN OTHERS THEN
    RETURN 0;
END FC_GETCONCILIA_NUM;

FUNCTION FC_GETCONCILIA_PAG
(
    UN_COMPANIA IN VARCHAR2,
    UN_CUENTA IN VARCHAR2,
    UN_CLASESCONTABLES IN VARCHAR2,
    UN_ULTIMODIA IN VARCHAR2,
    UN_MES IN NUMBER,
    UN_NUMERO_ANO IN NUMBER,
    UN_PAGTAMANIO IN NUMBER,
    UN_PAGINICIO IN NUMBER,
    UN_TIPO_CPTE IN VARCHAR2 DEFAULT NULL,
    UN_COMPROBANTE IN VARCHAR2 DEFAULT NULL,
    UN_CONSECUTIVO IN VARCHAR2 DEFAULT NULL,
    UN_NRO_DOCUMENTO IN VARCHAR2 DEFAULT NULL,
    UN_VALOR_DEBITO IN VARCHAR2 DEFAULT NULL,
    UN_VALOR_CREDITO IN VARCHAR2 DEFAULT NULL,
    UN_TERCERO IN VARCHAR2 DEFAULT NULL,
    UN_DESCRIPCION IN VARCHAR2 DEFAULT NULL,
    UN_PAGADOBANCO IN VARCHAR2 DEFAULT NULL,
    UN_FECHA_CONCILIA IN VARCHAR2 DEFAULT NULL,
    UN_TIPO_CPTE_AFECT IN VARCHAR2 DEFAULT NULL,
    UN_CMPTE_AFECTADO IN VARCHAR2 DEFAULT NULL,
    UN_SINIDENTIFICAR IN VARCHAR2 DEFAULT NULL,
    UN_FECHA_IDENTIFICACION IN VARCHAR2 DEFAULT NULL,
    UN_CONCILIADOR IN VARCHAR2 DEFAULT NULL,
    UN_IMPRESO IN VARCHAR2 DEFAULT NULL,
    UN_ENTREGADO IN VARCHAR2 DEFAULT NULL,
    UN_FECHA IN VARCHAR2 DEFAULT NULL
) RETURN CLOB
IS
    MI_RTA CLOB := '';
BEGIN
    FOR MI_RS IN (
        WITH DataBase AS (
            SELECT 
                D.COMPANIA, D.ANO, D.TIPO_CPTE, D.COMPROBANTE, D.CONSECUTIVO, PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(D.NRO_DOCUMENTO,' ')) AS NRO_DOCUMENTO,
                TO_CHAR(D.FECHA, 'DD/MM/YYYY HH24:MI:SS') AS FECHA, PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(D.CONCILIADOR,' ')) AS CONCILIADOR,
                D.VALOR_DEBITO, D.VALOR_CREDITO,TO_CHAR(D.FECHA_CONSIGNACIONPLANO, 'DD/MM/YYYY HH24:MI:SS') AS FECHA_CONSIGNACIONPLANO,
                D.TIPO_CPTE_AFECT,D.CMPTE_AFECTADO,TO_CHAR(D.FECHA_IDENTIFICACION, 'DD/MM/YYYY HH24:MI:SS') AS FECHA_IDENTIFICACION,
                (CASE D.SINIDENTIFICAR WHEN 0 THEN 'false' ELSE 'true' END) SINIDENTIFICAR,
                (CASE D.PAGADOBANCO WHEN 0 THEN 'false' ELSE 'true' END) AS PAGADOBANCO,
                TO_CHAR(D.FECHA_CONCILIA, 'DD/MM/YYYY HH24:MI:SS') AS FECHA_CONCILIA,
                PCK_NOMINA_COM5.FC_QUITAESPECIALES(NVL(D.DESCRIPCION,' ')) as DESCRIPCION, D.TERCERO,
                (CASE C.IMPRESO WHEN 0 THEN 'false' ELSE 'true' END) AS IMPRESO,
                (CASE C.ENTREGADO WHEN 0 THEN 'false' ELSE 'true' END) AS ENTREGADO
            FROM DETALLE_COMPROBANTE_CNT D
            INNER JOIN COMPROBANTE_CNT C ON D.COMPANIA = C.COMPANIA AND D.ANO = C.ANO AND D.TIPO_CPTE = C.TIPO AND D.COMPROBANTE = C.NUMERO
            INNER JOIN TIPO_COMPROBANTE T ON T.COMPANIA = D.COMPANIA AND T.CODIGO = D.TIPO_CPTE
            WHERE D.COMPANIA = UN_COMPANIA
              AND D.CUENTA = UN_CUENTA
              AND INSTR(REPLACE(CONCAT(CONCAT('''', REPLACE(UN_CLASESCONTABLES, ' ', '')), ''''), ',', ''','''), T.CLASE_CONTABLE, 1) > 0
              AND ( ( TRUNC(D.FECHA) <= TO_DATE(UN_ULTIMODIA, 'DD/MM/YYYY') AND D.PAGADOBANCO = 0 ) 
              OR ( TO_NUMBER(TO_CHAR(D.FECHA_CONCILIA, 'MM')) = UN_MES AND TO_NUMBER(TO_CHAR(D.FECHA_CONCILIA, 'YYYY')) = UN_NUMERO_ANO ) )
            ORDER BY D.FECHA, D.COMPROBANTE, D.CONSECUTIVO
        ),
        FilteredData AS (
            SELECT b.*, ROWNUM AS RNUM_INTERNAL
            FROM DataBase b
            WHERE (UN_TIPO_CPTE IS NULL OR UPPER(TIPO_CPTE) LIKE UPPER('%' || UN_TIPO_CPTE || '%'))
                  AND (UN_COMPROBANTE IS NULL OR UPPER(COMPROBANTE) LIKE UPPER('%' || UN_COMPROBANTE || '%'))
                  AND (UN_CONSECUTIVO IS NULL OR UPPER(TO_CHAR(CONSECUTIVO)) LIKE UPPER('%' || UN_CONSECUTIVO || '%'))
                  AND (UN_NRO_DOCUMENTO IS NULL OR UPPER(NRO_DOCUMENTO) LIKE UPPER('%' || UN_NRO_DOCUMENTO || '%'))
                  AND (UN_VALOR_DEBITO IS NULL OR TO_CHAR(NVL(VALOR_DEBITO, 0)) LIKE UN_VALOR_DEBITO || '%')
                  AND (UN_VALOR_CREDITO IS NULL OR TO_CHAR(NVL(VALOR_CREDITO, 0)) LIKE UN_VALOR_CREDITO || '%')
                  AND (UN_TERCERO IS NULL OR UPPER(TERCERO) LIKE UPPER('%' || UN_TERCERO || '%'))
                  AND (UN_DESCRIPCION IS NULL OR UPPER(DESCRIPCION) LIKE UPPER('%' || UN_DESCRIPCION || '%'))
                  AND (UN_PAGADOBANCO IS NULL OR PAGADOBANCO = UN_PAGADOBANCO )
                  AND (UN_CONCILIADOR IS NULL OR UPPER(CONCILIADOR) LIKE UPPER('%' || UN_CONCILIADOR || '%'))
                  AND (UN_FECHA IS NULL OR UPPER(FECHA) LIKE UPPER('%' || UN_FECHA || '%'))
                  AND (UN_FECHA_CONCILIA IS NULL OR UPPER(FECHA_CONCILIA) LIKE UPPER('%' || UN_FECHA_CONCILIA || '%'))
        )
        SELECT JSON_OBJECT(
            'COMPANIA' VALUE COMPANIA,  
            'ANO' VALUE ANO, 
            'SINIDENTIFICAR' VALUE SINIDENTIFICAR, 
            'TIPO_CPTE_AFECT' VALUE TIPO_CPTE_AFECT, 
            'FECHA_CONSIGNACIONPLANO' VALUE FECHA_CONSIGNACIONPLANO,
            'FECHA_IDENTIFICACION' VALUE FECHA_IDENTIFICACION, 
            'CMPTE_AFECTADO'   VALUE CMPTE_AFECTADO,
            'TIPO_CPTE' VALUE TIPO_CPTE,
            'COMPROBANTE' VALUE COMPROBANTE,
            'CONSECUTIVO' VALUE CONSECUTIVO,
            'NRO_DOCUMENTO' VALUE NRO_DOCUMENTO,
            'FECHA' VALUE FECHA,
            'VALOR_DEBITO' VALUE VALOR_DEBITO,
            'VALOR_CREDITO' VALUE VALOR_CREDITO,
            'PAGADOBANCO' VALUE PAGADOBANCO,
            'TERCERO' VALUE TERCERO,
            'DESCRIPCION' VALUE DESCRIPCION,
            'IMPRESO' VALUE IMPRESO,
            'ENTREGADO' VALUE ENTREGADO,
            'CONCILIADOR' VALUE CONCILIADOR,
            'FECHA_CONCILIA' VALUE FECHA_CONCILIA
        ) AS JSONOBJ, RNUM_INTERNAL AS RNUM
        FROM FilteredData
        WHERE RNUM_INTERNAL <= (UN_PAGINICIO + UN_PAGTAMANIO)
    )
    LOOP
        IF MI_RS.RNUM > UN_PAGINICIO THEN
            MI_RTA := MI_RTA || MI_RS.JSONOBJ || ',';
        END IF;
    END LOOP;

    IF LENGTH(MI_RTA) > 0 THEN
        MI_RTA := SUBSTR(MI_RTA, 1, LENGTH(MI_RTA) - 1);
    END IF;

    RETURN NVL(MI_RTA, '[]');
END FC_GETCONCILIA_PAG;

PROCEDURE PR_COPIAR_CONFIG_CONCEPTOS(
  UN_COMPANIA          IN VARCHAR2,
  UN_ANO_DESTINO       IN NUMBER,
  UN_ANO_ORIGEN        IN NUMBER
)
AS
  MI_CAMPOS     VARCHAR2(32000);
  MI_VALORES    VARCHAR2(32000);
  MI_EXCLUIDOS  VARCHAR2(32000);
BEGIN

  MI_EXCLUIDOS := 'COMPANIA,ANO';

  MI_CAMPOS := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS('CONFIGURAR_CONCEPTOS', MI_EXCLUIDOS);

  MI_VALORES := 
        ' SELECT ' || MI_CAMPOS || 
        ',''' || UN_COMPANIA || ''',' || UN_ANO_DESTINO ||
        ' FROM CONFIGURAR_CONCEPTOS ORI ' ||
        ' WHERE ORI.COMPANIA = ''' || UN_COMPANIA || '''' ||
        ' AND ORI.ANO = ' || UN_ANO_ORIGEN ||
        ' AND NOT EXISTS ( ' ||
        '   SELECT 1 FROM CONFIGURAR_CONCEPTOS DES ' ||
        '   WHERE DES.COMPANIA = ''' || UN_COMPANIA || '''' ||
        '     AND DES.ANO = ' || UN_ANO_DESTINO ||
        '     AND DES.CODIGO = ORI.CODIGO ' ||
        ' )';

  MI_CAMPOS := MI_CAMPOS || ',' || MI_EXCLUIDOS;

  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                        'CONFIGURAR_CONCEPTOS',
                        'IS',
                        MI_CAMPOS,
                        MI_VALORES,
                        NULL,
                        NULL
                      );

END PR_COPIAR_CONFIG_CONCEPTOS;

PROCEDURE PR_COPIAR_PLAN_CUENTAS(
  UN_COMPANIA     IN VARCHAR2,
  UN_ANO_DESTINO  IN NUMBER,
  UN_ANO_ORIGEN   IN NUMBER
)
AS
  MI_CONDICION  VARCHAR2(32000);
  MI_CAMPOS     VARCHAR2(32000);
  MI_VALORES    VARCHAR2(32000);
  MI_RTA        NUMBER;
BEGIN

  MI_CONDICION :=
        'SELECT CODIGO,
                COMPANIA,
                CONCEPTOS_FINANCIEROS,
                CONCEPTOS_PATRIMONIO,
                COLUMNA_REPORTE_PATR,
                CONCEPTOS_INTEGRAL
         FROM PLAN_CONTABLE
         WHERE COMPANIA = ''' || UN_COMPANIA || '''
           AND ANO = ' || UN_ANO_ORIGEN;

  MI_CAMPOS :=
        'TABLA.COMPANIA = VISTA.COMPANIA
         AND TABLA.CODIGO = VISTA.CODIGO
         AND TABLA.ANO = ' || UN_ANO_DESTINO;

  MI_VALORES :=
        'UPDATE SET TABLA.CONCEPTOS_FINANCIEROS   = VISTA.CONCEPTOS_FINANCIEROS,
                    TABLA.CONCEPTOS_PATRIMONIO = VISTA.CONCEPTOS_PATRIMONIO,
                    TABLA.CONCEPTOS_INTEGRAL = VISTA.CONCEPTOS_INTEGRAL,
                    TABLA.COLUMNA_REPORTE_PATR = VISTA.COLUMNA_REPORTE_PATR';

  MI_RTA := PCK_DATOS.FC_ACME(
                UN_TABLA       => 'PLAN_CONTABLE',
                UN_ACCION      => 'MM',
                UN_MERGEUSING  => MI_CONDICION,
                UN_MERGEENLACE => MI_CAMPOS,
                UN_MERGEEXISTE => MI_VALORES
            );

END PR_COPIAR_PLAN_CUENTAS;

FUNCTION FC_CARGAR_CONCILIAR(
/*
    NAME              :cargarInterfazporXls
    AUTHORS           : NCARDENAS
    AUTHOR MIGRACION  : NCARDENAS
    DATE MIGRADOR     :27/05/2026
    TIME              :5:21
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : VALIDACIÓN Y CARGUE DE  INFORMACIÓN DE CONCILIACION
    PARAMETERS        :
      --NAME:    cargarInterfazporXls
      --METHOD:  GET
  */
    UN_TABLA        IN VARCHAR2,
    UN_CADENA       IN CLOB,
    UN_USUARIO      IN VARCHAR2,
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO          IN ANO.NUMERO%TYPE
  )  RETURN CLOB
AS
MI_DATOS_FILA           PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS       PCK_SYSMAN_UTL.T_SPLIT;
MI_MENSAJE              CLOB;
MI_ERRORES              PCK_SUBTIPOS.TI_ENTERO_LARGO:= 0;
MI_TOTERRORES           PCK_SUBTIPOS.TI_ENTERO_LARGO:= 0;
--MI_CUENTA               VARCHAR2(4000);   
MI_FECHA                DATE;
MI_PARTIDADEBITO        NUMBER(20,2);
MI_PARTIDACREDITO       NUMBER(20,2);
MI_MES                  NUMBER(5,0);
MI_COMPANIA             VARCHAR2(11):=  NVL(UN_COMPANIA,'001');
MI_CONDICION            CLOB;
MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES              CLOB;
MI_RTA                  PCK_SUBTIPOS.TI_ENTERO;
MI_EXISTEMSG            BOOLEAN:= FALSE;
MI_CONSECUTIVO          NUMBER(5) := 0;
BEGIN

    MI_MENSAJE := MI_MENSAJE 
        || '*** INFORME DE ERRORES PARA LA PLANTILLA DE '|| REPLACE(UN_TABLA, '_', ' ') || ' ***'
        || CHR(10) || CHR(13)
        || CHR(10) || CHR(13); 
                  
    MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENA,
                                                       UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
    FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST
        LOOP
        MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                         UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
        FOR I IN MI_DATOS_COLUMNAS.FIRST .. MI_DATOS_COLUMNAS.LAST LOOP
            IF TRIM(UPPER(MI_DATOS_COLUMNAS(I))) = 'NODATO' THEN
                MI_DATOS_COLUMNAS(I) := NULL;
            END IF;
    END LOOP; 
    MI_ERRORES          := 0;

    -----       VALIDACION CAMPOS OBLIGATORIOS      -----
    
    IF TRIM(MI_DATOS_COLUMNAS(1)) IS NULL THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '||( RS + 1)|| ' La CUENTA es NULA, este campo es obligatorio y necesaria para realizar las demás  validaciones.'|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        MI_ERRORES := MI_ERRORES + 1;
    END IF;
    
    IF MI_DATOS_COLUMNAS(6) IS NULL THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El MES es NULO, este campo es obligatorio y necesaria para realizar las demás  validaciones. '|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        MI_ERRORES := MI_ERRORES + 1;
    END IF;
    
    IF MI_DATOS_COLUMNAS(4)IS NULL THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '||( RS + 1)|| ' La PARTIDA DEBITO es NULA, este campo es obligatorio y necesaria para realizar las demás  validaciones.'|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        MI_ERRORES := MI_ERRORES + 1;
    END IF;
    
    IF MI_DATOS_COLUMNAS(5) IS NULL THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El PARTIDA CREDITO es NULO, este campo es obligatorio y necesaria para realizar las demás  validaciones. '|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        MI_ERRORES := MI_ERRORES + 1;
    END IF;
    
    -----       Validaciones CUENTA        -----

    IF MI_DATOS_COLUMNAS(1) IS NOT NULL AND NOT REGEXP_LIKE(MI_DATOS_COLUMNAS(1), '^[0-9]+$') THEN
        MI_MENSAJE := MI_MENSAJE || ' En la fila ' || (RS + 1) || ' la cuenta [' || MI_DATOS_COLUMNAS(1) || '] contiene caracteres inválidos.' || CHR(10);
        MI_ERRORES := MI_ERRORES + 1;
        MI_TOTERRORES := MI_TOTERRORES + 1;
    ELSE
        IF LENGTH(MI_DATOS_COLUMNAS(1)) > 32 THEN 
            MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El código '|| MI_DATOS_COLUMNAS(1) ||' Supera el tamaño requerido. ' || CHR(10);
            MI_ERRORES := MI_ERRORES + 1;
            MI_TOTERRORES:= MI_TOTERRORES + 1;
        END IF;
    END IF;
    
    -- Validacion FECHA
    IF MI_DATOS_COLUMNAS(2) IS NOT NULL THEN
        BEGIN
            BEGIN
                MI_FECHA := TO_DATE(MI_DATOS_COLUMNAS(2),'DD/MM/RR');
            EXCEPTION
                WHEN OTHERS THEN
                    MI_FECHA := TO_DATE(MI_DATOS_COLUMNAS(2),'DD/MM/YYYY');
            END;
    
        EXCEPTION
            WHEN OTHERS THEN
                MI_MENSAJE := MI_MENSAJE ||' En la fila ' || (RS + 1) ||' la fecha [' || MI_DATOS_COLUMNAS(2) ||'] no tiene un formato válido.' || CHR(10);
                MI_ERRORES := MI_ERRORES + 1;
                MI_TOTERRORES := MI_TOTERRORES + 1;  
        END;    
    END IF;
    
    --Validacion Concepto    
    IF LENGTH(MI_DATOS_COLUMNAS(3)) > 64 THEN 
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El Concepto '|| MI_DATOS_COLUMNAS(3) ||' Supera el tamaño requerido. ' || CHR(10);
        MI_ERRORES := MI_ERRORES + 1;
        MI_TOTERRORES:= MI_TOTERRORES + 1;
    END IF;
        
    -- PARTIDACREDITO
        -- VALIDAR FORMATO NUMERICO

    IF NOT REGEXP_LIKE(MI_DATOS_COLUMNAS(4),'^-?[0-9]+(\.[0-9]+)?$') THEN

        MI_MENSAJE := MI_MENSAJE ||' En la fila ' || (RS + 1) ||' el valor [' || MI_DATOS_COLUMNAS(4) ||'] en PARTIDA_DEBITO no es numérico.' ||CHR(10);
        MI_ERRORES := MI_ERRORES + 1;
        MI_TOTERRORES := MI_TOTERRORES + 1;

    ELSE
        -- VALIDAR TAMAÑO NUMBER(20,2)
        IF LENGTH(REPLACE(REPLACE(MI_DATOS_COLUMNAS(4),'.',''),'-','')) > 20 THEN
    
            MI_MENSAJE := MI_MENSAJE ||' En la fila ' || (RS + 1) || ' PARTIDA_DEBITO supera el tamaño permitido.' || CHR(10);
            MI_ERRORES := MI_ERRORES + 1;
            MI_TOTERRORES := MI_TOTERRORES + 1;

        ELSE
            MI_PARTIDADEBITO := TO_NUMBER(MI_DATOS_COLUMNAS(4));
             -- VALIDAR DECIMALES

            IF ROUND(MI_PARTIDADEBITO, 2) != MI_PARTIDADEBITO THEN
                MI_MENSAJE := MI_MENSAJE ||' En la fila ' || (RS + 1) ||' PARTIDA_DEBITO tiene más de 2 decimales.' || CHR(10);
                MI_ERRORES := MI_ERRORES + 1;
                MI_TOTERRORES := MI_TOTERRORES + 1;
            END IF;
        END IF;
    END IF;
      
    -- PARTIDACREDITO
    
    IF NOT REGEXP_LIKE(MI_DATOS_COLUMNAS(5),'^-?[0-9]+(\.[0-9]+)?$') THEN

        MI_MENSAJE := MI_MENSAJE ||' En la fila ' || (RS + 1) ||' el valor [' || MI_DATOS_COLUMNAS(5) ||'] en PARTIDA_DEBITO no es numérico.' ||CHR(10);
        MI_ERRORES := MI_ERRORES + 1;
        MI_TOTERRORES := MI_TOTERRORES + 1;

    ELSE
        -- VALIDAR TAMAÑO NUMBER(20,2)
        IF LENGTH(REPLACE(REPLACE(MI_DATOS_COLUMNAS(5),'.',''),'-','')) > 20 THEN
    
            MI_MENSAJE := MI_MENSAJE ||' En la fila ' || (RS + 1) || ' PARTIDA_DEBITO supera el tamaño permitido.' || CHR(10);
            MI_ERRORES := MI_ERRORES + 1;
            MI_TOTERRORES := MI_TOTERRORES + 1;

        ELSE
            MI_PARTIDACREDITO := TO_NUMBER(MI_DATOS_COLUMNAS(5));
             -- VALIDAR DECIMALES

            IF ROUND(MI_PARTIDACREDITO, 2) != MI_PARTIDACREDITO THEN
                MI_MENSAJE := MI_MENSAJE ||' En la fila ' || (RS + 1) ||' PARTIDA_DEBITO tiene más de 2 decimales.' || CHR(10);
                MI_ERRORES := MI_ERRORES + 1;
                MI_TOTERRORES := MI_TOTERRORES + 1;
            END IF;
        END IF;
    END IF;
    
    -- MI_MES
    IF MI_DATOS_COLUMNAS(6) IS NOT NULL THEN

        BEGIN
    
            MI_MES := TO_NUMBER(MI_DATOS_COLUMNAS(6));
    
            IF MI_MES != TRUNC(MI_MES) THEN
    
                MI_MENSAJE := MI_MENSAJE || ' En la fila ' || (RS + 1) || ' el MES [' || MI_DATOS_COLUMNAS(6) ||'] no puede tener decimales.' || CHR(10);
                MI_ERRORES := MI_ERRORES + 1;
                MI_TOTERRORES := MI_TOTERRORES + 1;
    
            ELSIF MI_MES NOT BETWEEN 1 AND 12 THEN
    
                MI_MENSAJE := MI_MENSAJE || ' En la fila ' || (RS + 1) ||' el MES [' || MI_DATOS_COLUMNAS(6) ||'] debe estar entre 1 y 12.' || CHR(10);
                MI_ERRORES := MI_ERRORES + 1;
                MI_TOTERRORES := MI_TOTERRORES + 1;
    
            END IF;
    
        EXCEPTION
            WHEN OTHERS THEN
    
                MI_MENSAJE := MI_MENSAJE ||' En la fila ' || (RS + 1) ||' el valor [' || MI_DATOS_COLUMNAS(6) ||'] en MES no es numérico.' || CHR(10);
                MI_ERRORES := MI_ERRORES + 1;
                MI_TOTERRORES := MI_TOTERRORES + 1;
    
        END;

    END IF;
    
    IF MI_ERRORES > 0 THEN
        CONTINUE;
    END IF;
    
    MI_CONSECUTIVO   := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO( UN_TABLA    => UN_TABLA, 
                                                                UN_CRITERIO => 'COMPANIA = '''||UN_COMPANIA||''' ', 
                                                                UN_CAMPO    => 'CONSECUTIVO');
    
    -----       Cargue de Información       ------
    
    MI_VALORES := 'INSERT (COMPANIA, ANO, CUENTA, CONSECUTIVO, PARTIDA_DEBITO, PARTIDA_CREDITO, OBSERVACIONES, FECHA, MES_PARTIDA ) 
                            VALUES (VISTA.COMPANIA, VISTA.ANO, VISTA.CUENTA, VISTA.CONSECUTIVO, VISTA.PARTIDA_DEBITO, VISTA.PARTIDA_CREDITO, VISTA.OBSERVACIONES, VISTA.FECHA, VISTA.MES_PARTIDA)';
    MI_CONDICION:='SELECT  '''|| UN_COMPANIA|| ''' COMPANIA, ' || UN_ANO|| ' ANO,
                           '''|| MI_DATOS_COLUMNAS(1)|| ''' CUENTA, '|| MI_CONSECUTIVO|| ' CONSECUTIVO,
                           '|| MI_DATOS_COLUMNAS(4)|| ' PARTIDA_DEBITO,
                           '|| MI_DATOS_COLUMNAS(5)|| ' PARTIDA_CREDITO,
                           '''|| MI_DATOS_COLUMNAS(3)|| ''' OBSERVACIONES,
                           '''|| MI_DATOS_COLUMNAS(2)|| ''' FECHA,
                           '|| MI_DATOS_COLUMNAS(6)|| ' MES_PARTIDA  FROM DUAL';
    
        BEGIN
            MI_CAMPOS   := 'TABLA.COMPANIA = VISTA.COMPANIA
                            AND TABLA.ANO = VISTA.ANO
                            AND TABLA.CUENTA = VISTA.CUENTA
                            AND TABLA.CONSECUTIVO = VISTA.CONSECUTIVO';
            MI_RTA  := PCK_DATOS.FC_ACME (UN_TABLA       => UN_TABLA
                                         ,UN_ACCION      => 'IN'
                                         ,UN_MERGEUSING  =>  MI_CONDICION
                                         ,UN_MERGEENLACE =>  MI_CAMPOS
                                             ,UN_MERGENOEXIS =>  MI_VALORES);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE PCK_EXCEPCIONES.EXC_ALMACEN;
        END;
           
    END LOOP;
     MI_MENSAJE := MI_MENSAJE ||
                  'TOTAL DE ERRORES: ' || MI_TOTERRORES || CHR(10);
    
    RETURN MI_MENSAJE;

END FC_CARGAR_CONCILIAR;

FUNCTION FC_GET_REF_CC_NUM
(
    UN_COMPANIA IN VARCHAR2,
    UN_CENTRO        IN VARCHAR2,
    UN_LISTAAFECTAR  IN VARCHAR2,
    UN_CODIGO        IN VARCHAR2,
    UN_NOMBRE        IN VARCHAR2    
) RETURN NUMBER
IS
    MI_RTA           NUMBER := 0;
    MI_CONSULTA      PCK_SUBTIPOS.TI_STRSQL;
    MI_LISTAAFECTAR  VARCHAR2(32000);
BEGIN
    MI_LISTAAFECTAR := REPLACE(UN_LISTAAFECTAR, '''''', '''');
    MI_CONSULTA := '
    SELECT COUNT(1) 
    FROM (
        SELECT DISTINCT 
                CASE WHEN '''||UN_CENTRO||''' = ''S'' THEN D.CENTRO_COSTO ELSE D.REFERENCIA END CODIGO,
                CASE WHEN '''||UN_CENTRO||''' = ''S'' THEN CC.NOMBRE ELSE R.NOMBRE END NOMBRE
            FROM DETALLE_COMPROBANTE_CNT D
            INNER JOIN PLAN_CONTABLE P
                ON D.COMPANIA = P.COMPANIA
               AND D.ANO = P.ANO
               AND D.CUENTA = P.CODIGO
            LEFT JOIN CENTRO_COSTO CC
                ON '''||UN_CENTRO||''' = ''S''
               AND CC.COMPANIA = D.COMPANIA
               AND CC.ANO = D.ANO
               AND CC.CODIGO = D.CENTRO_COSTO
            LEFT JOIN REFERENCIA R
                ON '''||UN_CENTRO||''' = ''N''
               AND R.COMPANIA = D.COMPANIA
               AND R.ANO = D.ANO
               AND R.CODIGO = D.REFERENCIA
            WHERE D.COMPANIA = '''||UN_COMPANIA||'''
              AND P.CLASECUENTA = ''P''
              AND (D.ANO, D.TIPO_CPTE, D.COMPROBANTE) IN ('||MI_LISTAAFECTAR||')
            ORDER BY CODIGO
    ) VistaFiltro
    WHERE 1=1';
    
    IF UN_CODIGO IS NOT NULL THEN
        MI_CONSULTA := MI_CONSULTA || 
        ' AND UPPER(CODIGO) LIKE UPPER(''%'||UN_CODIGO||'%'')';
    END IF;
    
    IF UN_NOMBRE IS NOT NULL THEN
        MI_CONSULTA := MI_CONSULTA || 
        ' AND UPPER(NOMBRE) LIKE UPPER(''%'||UN_NOMBRE||'%'')';
    END IF;
      
    EXECUTE IMMEDIATE MI_CONSULTA INTO MI_RTA;

    RETURN NVL(MI_RTA, 0);
EXCEPTION WHEN OTHERS THEN
    RETURN 0;
END FC_GET_REF_CC_NUM;

FUNCTION FC_GET_REF_CC_PAG
(
    UN_COMPANIA      IN VARCHAR2,
    UN_CENTRO        IN VARCHAR2,
    UN_LISTAAFECTAR  IN VARCHAR2,
    UN_CODIGO        IN VARCHAR2,
    UN_NOMBRE        IN VARCHAR2,
    UN_PAGTAMANIO    IN NUMBER,
    UN_PAGINICIO     IN NUMBER
) RETURN CLOB
IS
    MI_RTA       CLOB := '';
    MI_CONSULTA  CLOB;
    MI_RS        SYS_REFCURSOR;
    V_JSON       VARCHAR2(4000);
    MI_LISTAAFECTAR  VARCHAR2(32000);
BEGIN
    MI_LISTAAFECTAR := REPLACE(UN_LISTAAFECTAR, '''''', '''');
    MI_CONSULTA := '
    SELECT JSON_OBJECT(
        ''CODIGO'' VALUE CODIGO,
        ''NOMBRE'' VALUE NOMBRE
    )
    FROM (
        SELECT t.*, ROWNUM rnum
        FROM (
            SELECT DISTINCT 
                CASE WHEN '''||UN_CENTRO||''' = ''S'' THEN D.CENTRO_COSTO ELSE D.REFERENCIA END CODIGO,
                CASE WHEN '''||UN_CENTRO||''' = ''S'' THEN CC.NOMBRE ELSE R.NOMBRE END NOMBRE
            FROM DETALLE_COMPROBANTE_CNT D
            INNER JOIN PLAN_CONTABLE P
                ON D.COMPANIA = P.COMPANIA
               AND D.ANO = P.ANO
               AND D.CUENTA = P.CODIGO
            LEFT JOIN CENTRO_COSTO CC
                ON '''||UN_CENTRO||''' = ''S''
               AND CC.COMPANIA = D.COMPANIA
               AND CC.ANO = D.ANO
               AND CC.CODIGO = D.CENTRO_COSTO
            LEFT JOIN REFERENCIA R
                ON '''||UN_CENTRO||''' = ''N''
               AND R.COMPANIA = D.COMPANIA
               AND R.ANO = D.ANO
               AND R.CODIGO = D.REFERENCIA
            WHERE D.COMPANIA = '''||UN_COMPANIA||'''
              AND P.CLASECUENTA = ''P''
              AND (D.ANO, D.TIPO_CPTE, D.COMPROBANTE) IN ('||MI_LISTAAFECTAR||')';

    IF UN_CODIGO IS NOT NULL THEN
        MI_CONSULTA := MI_CONSULTA || 
        ' AND UPPER(
            CASE WHEN '''||UN_CENTRO||''' = ''S'' THEN D.CENTRO_COSTO ELSE D.REFERENCIA END
          ) LIKE UPPER(''%'||UN_CODIGO||'%'')';
    END IF;

    IF UN_NOMBRE IS NOT NULL THEN
        MI_CONSULTA := MI_CONSULTA || 
        ' AND UPPER(
            CASE WHEN '''||UN_CENTRO||''' = ''S'' THEN CC.NOMBRE ELSE R.NOMBRE END
          ) LIKE UPPER(''%'||UN_NOMBRE||'%'')';
    END IF;

    MI_CONSULTA := MI_CONSULTA || '
            ORDER BY CODIGO
        ) t
        WHERE ROWNUM <= '||(UN_PAGINICIO + UN_PAGTAMANIO)||'
    )
    WHERE rnum > '||UN_PAGINICIO;

    OPEN MI_RS FOR MI_CONSULTA;

    LOOP
        FETCH MI_RS INTO V_JSON;
        EXIT WHEN MI_RS%NOTFOUND;

        MI_RTA := MI_RTA || V_JSON || ',';
    END LOOP;

    CLOSE MI_RS;

    IF LENGTH(MI_RTA) > 0 THEN
        MI_RTA := SUBSTR(MI_RTA, 1, LENGTH(MI_RTA) - 1);
    END IF;

    RETURN NVL(MI_RTA, '{}');

EXCEPTION 
    WHEN OTHERS THEN
        RETURN '{}';
END FC_GET_REF_CC_PAG;
END PCK_CONTABILIDAD7;