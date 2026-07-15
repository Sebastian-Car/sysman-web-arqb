create or replace PACKAGE BODY               PCK_PRESUPUESTO_COM4 AS

  FUNCTION FC_AFECTACIONES_A_FECHA_PPTO
     /*  
        NAME              : FC_AFECTACIONES_A_FECHA_PPTO
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 03/07/2019
        TIME              : 09:15 AM
        SOURCE MODULE     : 
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite generar el saldo de un detalle de movimiento
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:afectacionesFechaPpto
        @METHOD:Get
*/    
  (UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA 
  ,UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO
  ,UN_TIPO           IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL
  ,UN_COMPROBANTE    IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL
  ,UN_CONSECUTIVO    IN PCK_SUBTIPOS.TI_CONSECUTIVOPPTAL
  ,UN_FECHA          IN DATE 
)
RETURN NUMBER AS
    MI_VALOR   PCK_SUBTIPOS.TI_DOBLE;
  BEGIN
     BEGIN
        SELECT (MOV.VALOR_DEBITO - MOV.VALOR_CREDITO) - NVL(SUM(DEBITOS - CREDITOS),0) + NVL(SUM(MDEBITOS - MCREDITOS),0) SALDO 
        INTO MI_VALOR
        FROM DETALLE_COMPROBANTE_PPTAL MOV LEFT JOIN 
            (
                --IDENTIFICA LOS VALORES DE LAS AFECTACIONES DIRECTAS SEPARANDO DE ACUERDO A LA AFECTACION SI SON MODIFICACIONES
                SELECT X.COMPANIA,
                       X.ANO_AFECT,
                       X.TIPO_CPTE_AFECT,
                       X.CMPTE_AFECTADO, 
                       X.CONSECUTIVOPPTO,
                       SUM(CASE WHEN C.AFECTACION     IN('A','R') THEN 0 ELSE VALOR_DEBITO  END) DEBITOS,
                       SUM(CASE WHEN C.AFECTACION     IN('A','R') THEN 0 ELSE VALOR_CREDITO END) CREDITOS,
                       SUM(CASE WHEN C.AFECTACION NOT IN('A','R') THEN 0 ELSE VALOR_DEBITO  END) MDEBITOS,
                       SUM(CASE WHEN C.AFECTACION NOT IN('A','R') THEN 0 ELSE VALOR_CREDITO END) MCREDITOS       
                FROM DETALLE_COMPROBANTE_PPTAL X INNER JOIN TIPO_COMPROBPP  T 
                  ON X.COMPANIA  = T.COMPANIA 
                 AND X.TIPO_CPTE = T.CODIGO
                INNER JOIN CLASECNTPRES C 
                  ON T.CLASE=C.CODIGO
                WHERE X.COMPANIA        = UN_COMPANIA
                  AND X.ANO_AFECT       = UN_ANIO
                  AND X.TIPO_CPTE_AFECT = UN_TIPO
                  AND X.CMPTE_AFECTADO  = UN_COMPROBANTE
                  AND X.CONSECUTIVOPPTO = UN_CONSECUTIVO
                  AND X.FECHA          <= UN_FECHA
                  AND X.NATURALEZA      = 'D'  
                GROUP BY X.COMPANIA,
                         X.ANO_AFECT,
                         X.TIPO_CPTE_AFECT,
                         X.CMPTE_AFECTADO, 
                         X.CONSECUTIVOPPTO
            UNION ALL    
                -- IDENTIFICA LOS VALORES DE LAS AFECTACIONES INDIRECTAS(SEGUNDO GRADO) LAS CUALES AFECTAN DIRECTAMENTE EL DEBITO Y CREDITO
                SELECT NIVEL1.COMPANIA,
                       NIVEL1.ANO,
                       NIVEL1.TIPO_CPTE,
                       NIVEL1.COMPROBANTE, 
                       NIVEL1.CONSECUTIVO,
                       SUM(NIVEL3.VALOR_DEBITO)  DEBITOS,
                       SUM(NIVEL3.VALOR_CREDITO) CREDITOS,
                       0  MDEBITOS,
                       0  MCREDITOS
                FROM (
                    SELECT COMPANIA,
                           ANO ,
                           TIPO_CPTE ,
                           COMPROBANTE,
                           CONSECUTIVO, 
                           ANO_AFECT,
                           TIPO_CPTE_AFECT,
                           CMPTE_AFECTADO,
                           CONSECUTIVOPPTO,
                           NATURALEZA
                    FROM DETALLE_COMPROBANTE_PPTAL
                    WHERE COMPANIA        = UN_COMPANIA
                      AND ANO             = UN_ANIO
                      AND TIPO_CPTE       = UN_TIPO
                      AND COMPROBANTE     = UN_COMPROBANTE
                      AND CONSECUTIVO     = UN_CONSECUTIVO
                      AND FECHA          <= UN_FECHA
                ) NIVEL1 
                INNER JOIN (SELECT COMPANIA,
                                   ANO,
                                   TIPO_CPTE ,
                                   COMPROBANTE,
                                   CONSECUTIVO, 
                                   ANO_AFECT,
                                   TIPO_CPTE_AFECT,
                                   CMPTE_AFECTADO,
                                   CONSECUTIVOPPTO
                            FROM DETALLE_COMPROBANTE_PPTAL
                            WHERE COMPANIA        = UN_COMPANIA
                              AND ANO_AFECT       = UN_ANIO
                              AND TIPO_CPTE_AFECT = UN_TIPO
                              AND CMPTE_AFECTADO  = UN_COMPROBANTE
                              AND CONSECUTIVOPPTO = UN_CONSECUTIVO
                              AND FECHA          <= UN_FECHA
                ) NIVEL2
                      ON NIVEL1.COMPANIA    = NIVEL2.COMPANIA
                     AND NIVEL1.ANO         = NIVEL2.ANO_AFECT
                     AND NIVEL1.TIPO_CPTE   = NIVEL2.TIPO_CPTE_AFECT
                     AND NIVEL1.COMPROBANTE = NIVEL2.CMPTE_AFECTADO
                     AND NIVEL1.CONSECUTIVO = NIVEL2.CONSECUTIVOPPTO 
                INNER JOIN (    
                            SELECT X.COMPANIA,
                                   X.ANO_AFECT,
                                   X.TIPO_CPTE_AFECT,
                                   X.CMPTE_AFECTADO,
                                   X.CONSECUTIVOPPTO,
                                   X.VALOR_DEBITO,
                                   X.VALOR_CREDITO,
                                   C.AFECTACION
                            FROM DETALLE_COMPROBANTE_PPTAL X INNER JOIN TIPO_COMPROBPP  T 
                              ON X.COMPANIA=T.COMPANIA 
                             AND X.TIPO_CPTE=T.CODIGO
                            INNER JOIN CLASECNTPRES C 
                              ON T.CLASE=C.CODIGO
                            WHERE X.COMPANIA        = UN_COMPANIA
                              AND X.ANO_AFECT       = UN_ANIO
                              AND X.FECHA          <= UN_FECHA
                              AND C.AFECTACION      IN('A','R')  
                              AND X.TIPO_CPTE_AFECT IS NOT NULL 
                              AND X.CMPTE_AFECTADO  IS NOT NULL
                ) NIVEL3
                      ON NIVEL2.COMPANIA    = NIVEL3.COMPANIA
                     AND NIVEL2.TIPO_CPTE   = NIVEL3.TIPO_CPTE_AFECT
                     AND NIVEL2.COMPROBANTE = NIVEL3.CMPTE_AFECTADO
                     AND NIVEL2.CONSECUTIVO = NIVEL3.CONSECUTIVOPPTO 
                WHERE NIVEL1.COMPANIA    = UN_COMPANIA
                  AND NIVEL1.ANO         = UN_ANIO
                  AND NIVEL1.TIPO_CPTE   = UN_TIPO
                  AND NIVEL1.COMPROBANTE = UN_COMPROBANTE
                  AND NIVEL1.NATURALEZA = 'D'
                GROUP BY NIVEL1.COMPANIA,
                         NIVEL1.ANO,
                         NIVEL1.TIPO_CPTE,
                         NIVEL1.COMPROBANTE, 
                         NIVEL1.CONSECUTIVO,
                         0
            ) AFECTA
              ON MOV.COMPANIA    = AFECTA.COMPANIA
             AND MOV.ANO         = AFECTA.ANO_AFECT
             AND MOV.TIPO_CPTE   = AFECTA.TIPO_CPTE_AFECT
             AND MOV.COMPROBANTE = AFECTA.CMPTE_AFECTADO
             AND MOV.CONSECUTIVO = AFECTA.CONSECUTIVOPPTO 
        WHERE MOV.COMPANIA    = UN_COMPANIA
          AND MOV.ANO         = UN_ANIO
          AND MOV.TIPO_CPTE   = UN_TIPO
          AND MOV.COMPROBANTE = UN_COMPROBANTE
          AND MOV.CONSECUTIVO = UN_CONSECUTIVO
          AND MOV.FECHA      <= UN_FECHA
          AND MOV.NATURALEZA = 'D'     
        GROUP BY MOV.COMPANIA,
                 MOV.ANO,
                 MOV.TIPO_CPTE,
                 MOV.COMPROBANTE, 
                 MOV.CONSECUTIVO,
                 MOV.VALOR_DEBITO,
                 MOV.VALOR_CREDITO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_VALOR :=0;
    END;
    RETURN MI_VALOR;     
  END FC_AFECTACIONES_A_FECHA_PPTO;


  FUNCTION FC_CADENAAFECTACION
     /*  
        NAME              : FC_CADENAAFECTACION
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 03/07/2019
        TIME              : 09:15 AM
        SOURCE MODULE     : 
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite generar el saldo de un detalle de movimiento
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:cadenaAfectacion
        @METHOD:Get
*/    
  (UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA 
  ,UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO
  ,UN_TIPO           IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL
  ,UN_COMPROBANTE    IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL
  ,UN_CONSECUTIVO    IN PCK_SUBTIPOS.TI_CONSECUTIVOPPTAL
  ,UN_FECHA          IN DATE 
)
RETURN VARCHAR2 AS
    MI_SALIDA VARCHAR2(100);
    MI_EGR    PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_REO    PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_RES    PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_DIS    PCK_SUBTIPOS.TI_DOBLE :=0;
    MI_TIPO   PCK_SUBTIPOS.TI_DOBLE :=0;
BEGIN

    FOR RS IN(WITH MOV AS (
                        SELECT X.COMPANIA,
                               X.ANO ANO,
                               X.TIPO_CPTE ,
                               X.COMPROBANTE,
                               X.CONSECUTIVO,       
                               X.ANO_AFECT,
                               X.TIPO_CPTE_AFECT,
                               X.CMPTE_AFECTADO,
                               X.CONSECUTIVOPPTO,
                               X.FECHA,
                               T.CLASE
                        FROM DETALLE_COMPROBANTE_PPTAL X INNER JOIN TIPO_COMPROBPP  T 
                          ON X.COMPANIA        = T.COMPANIA 
                         AND X.TIPO_CPTE_AFECT = T.CODIGO
                        INNER JOIN CLASECNTPRES C 
                          ON T.CLASE =C.CODIGO
                        WHERE X.COMPANIA    = UN_COMPANIA
                          AND X.ANO         = UN_ANIO
                          AND X.FECHA      <= UN_FECHA       
                )
                SELECT NIVEL1.ANO_AFECT        NIVEL1_ANO,
                       NIVEL1.TIPO_CPTE_AFECT  NIVEL1_TIPO_CPTE,
                       NIVEL1.CMPTE_AFECTADO   NIVEL1_COMPROBANTE,
                       NIVEL1.CONSECUTIVOPPTO  NIVEL1_CONSECUTIVO,
                       NIVEL1.CLASE            NIVEL1_CLASE ,
                       NIVEL2.ANO_AFECT         NIVEL2_ANO,
                       NIVEL2.TIPO_CPTE_AFECT   NIVEL2_TIPO_CPTE,
                       NIVEL2.CMPTE_AFECTADO    NIVEL2_COMPROBANTE,
                       NIVEL2.CONSECUTIVOPPTO   NIVEL2_CONSECUTIVO,
                       NIVEL2.CLASE             NIVEL2_CLASE,
                       NIVEL3.ANO_AFECT           NIVEL3_ANO ,
                       NIVEL3.TIPO_CPTE_AFECT     NIVEL3_TIPO_CPTE,
                       NIVEL3.CMPTE_AFECTADO      NIVEL3_COMPROBANTE,
                       NIVEL3.CONSECUTIVOPPTO     NIVEL3_CONSECUTIVO ,
                       NIVEL3.CLASE               NIVEL3_CLASE,
                       NIVEL4.ANO_AFECT           NIVEL4_ANO ,
                       NIVEL4.TIPO_CPTE_AFECT     NIVEL4_TIPO_CPTE,
                       NIVEL4.CMPTE_AFECTADO      NIVEL4_COMPROBANTE,
                       NIVEL4.CONSECUTIVOPPTO     NIVEL4_CONSECUTIVO ,
                       NIVEL4.CLASE               NIVEL4_CLASE 
                FROM MOV NIVEL1 LEFT JOIN MOV NIVEL2
                  ON NIVEL1.COMPANIA        = NIVEL2.COMPANIA
                 AND NIVEL1.ANO_AFECT       = NIVEL2.ANO 
                 AND NIVEL1.TIPO_CPTE_AFECT = NIVEL2.TIPO_CPTE
                 AND NIVEL1.CMPTE_AFECTADO  = NIVEL2.COMPROBANTE
                 AND NIVEL1.CONSECUTIVOPPTO = NIVEL2.CONSECUTIVO
                LEFT JOIN MOV NIVEL3
                  ON NIVEL2.COMPANIA        = NIVEL3.COMPANIA
                 AND NIVEL2.ANO_AFECT       = NIVEL3.ANO 
                 AND NIVEL2.TIPO_CPTE_AFECT = NIVEL3.TIPO_CPTE
                 AND NIVEL2.CMPTE_AFECTADO  = NIVEL3.COMPROBANTE
                 AND NIVEL2.CONSECUTIVOPPTO = NIVEL3.CONSECUTIVO 
                LEFT JOIN MOV NIVEL4
                  ON NIVEL3.COMPANIA        = NIVEL4.COMPANIA
                 AND NIVEL3.ANO_AFECT       = NIVEL4.ANO 
                 AND NIVEL3.TIPO_CPTE_AFECT = NIVEL4.TIPO_CPTE
                 AND NIVEL3.CMPTE_AFECTADO  = NIVEL4.COMPROBANTE
                 AND NIVEL3.CONSECUTIVOPPTO = NIVEL4.CONSECUTIVO 
                WHERE NIVEL1.COMPANIA    = UN_COMPANIA
                  AND NIVEL1.ANO         = UN_ANIO
                  AND NIVEL1.TIPO_CPTE   = UN_TIPO
                  AND NIVEL1.COMPROBANTE = UN_COMPROBANTE
                  AND NIVEL1.CONSECUTIVO = UN_CONSECUTIVO
                  AND NIVEL1.FECHA      <= UN_FECHA
    )
    LOOP
        IF RS.NIVEL1_CLASE IS NOT NULL THEN
            MI_TIPO := FC_AFECTACIONES_A_FECHA_PPTO(UN_COMPANIA    => UN_COMPANIA 
                                                   ,UN_ANIO        => RS.NIVEL1_ANO
                                                   ,UN_TIPO        => RS.NIVEL1_TIPO_CPTE
                                                   ,UN_COMPROBANTE => RS.NIVEL1_COMPROBANTE
                                                   ,UN_CONSECUTIVO => RS.NIVEL1_CONSECUTIVO
                                                   ,UN_FECHA       => UN_FECHA
                                                   );
            IF RS.NIVEL1_CLASE = 'EGR' THEN
                MI_EGR := MI_TIPO;
            ELSIF RS.NIVEL1_CLASE = 'REO' THEN
                MI_REO := MI_TIPO;
            ELSIF RS.NIVEL1_CLASE = 'RES' THEN
                MI_RES := MI_TIPO;
            ELSIF RS.NIVEL1_CLASE = 'DIS' THEN
                MI_DIS := MI_TIPO;
            END IF;    
        END IF;

        IF RS.NIVEL2_CLASE IS NOT NULL THEN
            MI_TIPO := FC_AFECTACIONES_A_FECHA_PPTO(UN_COMPANIA    => UN_COMPANIA 
                                                   ,UN_ANIO        => RS.NIVEL2_ANO
                                                   ,UN_TIPO        => RS.NIVEL2_TIPO_CPTE
                                                   ,UN_COMPROBANTE => RS.NIVEL2_COMPROBANTE
                                                   ,UN_CONSECUTIVO => RS.NIVEL2_CONSECUTIVO
                                                   ,UN_FECHA       => UN_FECHA
                                                   );
            IF RS.NIVEL2_CLASE = 'EGR' THEN
                MI_EGR := MI_TIPO;
            ELSIF RS.NIVEL2_CLASE = 'REO' THEN
                MI_REO := MI_TIPO;
            ELSIF RS.NIVEL2_CLASE = 'RES' THEN
                MI_RES := MI_TIPO;
            ELSIF RS.NIVEL2_CLASE = 'DIS' THEN
                MI_DIS := MI_TIPO;
            END IF;    
        END IF;

        IF RS.NIVEL3_CLASE IS NOT NULL THEN
            MI_TIPO := FC_AFECTACIONES_A_FECHA_PPTO(UN_COMPANIA    => UN_COMPANIA 
                                                   ,UN_ANIO        => RS.NIVEL3_ANO
                                                   ,UN_TIPO        => RS.NIVEL3_TIPO_CPTE
                                                   ,UN_COMPROBANTE => RS.NIVEL3_COMPROBANTE
                                                   ,UN_CONSECUTIVO => RS.NIVEL3_CONSECUTIVO
                                                   ,UN_FECHA       => UN_FECHA
                                                   );
            IF RS.NIVEL3_CLASE = 'EGR' THEN
                MI_EGR := MI_TIPO;
            ELSIF RS.NIVEL3_CLASE = 'REO' THEN
                MI_REO := MI_TIPO;
            ELSIF RS.NIVEL3_CLASE = 'RES' THEN
                MI_RES := MI_TIPO;
            ELSIF RS.NIVEL3_CLASE = 'DIS' THEN
                MI_DIS := MI_TIPO;
            END IF;    
        END IF;

        IF RS.NIVEL4_CLASE IS NOT NULL THEN
            MI_TIPO := FC_AFECTACIONES_A_FECHA_PPTO(UN_COMPANIA    => UN_COMPANIA 
                                                   ,UN_ANIO        => RS.NIVEL4_ANO
                                                   ,UN_TIPO        => RS.NIVEL4_TIPO_CPTE
                                                   ,UN_COMPROBANTE => RS.NIVEL4_COMPROBANTE
                                                   ,UN_CONSECUTIVO => RS.NIVEL4_CONSECUTIVO
                                                   ,UN_FECHA       => UN_FECHA
                                                   );
            IF RS.NIVEL4_CLASE = 'EGR' THEN
                MI_EGR := MI_TIPO;
            ELSIF RS.NIVEL4_CLASE = 'REO' THEN
                MI_REO := MI_TIPO;
            ELSIF RS.NIVEL4_CLASE = 'RES' THEN
                MI_RES := MI_TIPO;
            ELSIF RS.NIVEL4_CLASE = 'DIS' THEN
                MI_DIS := MI_TIPO;
            END IF;    
        END IF;
    END LOOP;
    MI_SALIDA := TO_CHAR(MI_DIS) || ';' || TO_CHAR(MI_RES) || ';' || TO_CHAR(MI_REO) || ';' || TO_CHAR(MI_EGR);
    RETURN MI_SALIDA;  
END FC_CADENAAFECTACION;


PROCEDURE PR_ACTUALIZAR_SALDOPPTAL_MOV
     /*  
        NAME              : PR_ACTUALIZAR_SALDOPPTAL_MOV
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 03/07/2019
        TIME              : 09:15 AM
        SOURCE MODULE     : 
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite actualizar los campos de la tabla detalle comprobante pptal de los afectados hacia arriba
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:actualizarSalodPptalMov
        @METHOD:Post
*/ 
(UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA 
,UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO
,UN_TIPO           IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL
,UN_COMPROBANTE    IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL
,UN_CONSECUTIVO    IN PCK_SUBTIPOS.TI_CONSECUTIVOPPTAL
,UN_FECHA          IN DATE 
)
AS
    MI_SALDOS      VARCHAR2(200);
    MI_DATOS_FILA  PCK_SYSMAN_UTL.T_SPLIT;
    MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
    MI_MSGERROR    PCK_SUBTIPOS.TI_CLAVEVALOR;   
BEGIN 
    MI_SALDOS := FC_CADENAAFECTACION(UN_COMPANIA    => UN_COMPANIA 
                                    ,UN_ANIO        => UN_ANIO
                                    ,UN_TIPO        => UN_TIPO
                                    ,UN_COMPROBANTE => UN_COMPROBANTE
                                    ,UN_CONSECUTIVO => UN_CONSECUTIVO
                                    ,UN_FECHA       => UN_FECHA
                                    );

    MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA       => MI_SALDOS,
                                                 UN_DELIMITADOR => ';');

    BEGIN
        BEGIN
            IF MI_DATOS_FILA(1) >= 0 AND MI_DATOS_FILA(2) >= 0 AND MI_DATOS_FILA(3) >= 0 AND MI_DATOS_FILA(4) >= 0  THEN
                MI_CAMPOS:= 'SALDO_DIS = ' || MI_DATOS_FILA(1) ||',
                             SALDO_RES = ' || MI_DATOS_FILA(2) ||',
                             SALDO_REO = ' || MI_DATOS_FILA(3) ||',
                             SALDO_EGR = ' || MI_DATOS_FILA(4);        
                MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA    || '''
                             AND ANO         = ''' || UN_ANIO        || '''
                             AND TIPO_CPTE   = ''' || UN_TIPO        || '''
                             AND COMPROBANTE =   ' || UN_COMPROBANTE || ' 
                             AND CONSECUTIVO =   ' || UN_CONSECUTIVO || '
                             AND SALDO_DIS   = 0
                             AND SALDO_RES   = 0
                             AND SALDO_REO   = 0
                             AND SALDO_EGR   = 0';
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                      ,UN_ACCION    => 'M'
                                                      ,UN_CAMPOS    => MI_CAMPOS
                                                      ,UN_CONDICION => MI_CONDICION);        
            END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
        MI_MSGERROR(1).CLAVE := 'COMPROBANTE';
        MI_MSGERROR(1).VALOR := UN_COMPROBANTE;
        MI_MSGERROR(2).CLAVE := 'TIPO';
        MI_MSGERROR(2).VALOR := UN_TIPO;
        MI_MSGERROR(3).CLAVE := 'CONSECUTIVO';
        MI_MSGERROR(3).VALOR := UN_CONSECUTIVO;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD => PCK_ERRORES.ERR_PPTOACTUALIZASALDOS
                                ,UN_REEMPLAZOS  => MI_MSGERROR  );
    END;    
END PR_ACTUALIZAR_SALDOPPTAL_MOV;

PROCEDURE PR_CONGELARSALDOSMAN
      /*  
        NAME              : PR_CONGELARSALDOSMAN
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 04/07/2019
        TIME              : 10:05 AM
        SOURCE MODULE     : 
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite realizar mantenimiento para asignar los saldos a la fecha de creación del mismo
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:congelarSaldosMan
        @METHOD:Post
*/ 
(
 UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA, 
 UN_ANIO            IN PCK_SUBTIPOS.TI_ANIO,
 UN_TIPO            IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL,
 UN_COMPROBANTE_INI IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL,
 UN_COMPROBANTE_FIN IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL
)AS
   MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;  
 BEGIN
    MI_CAMPOS := UN_COMPANIA;
    <<COMPROBANTES>>
    FOR RS IN(SELECT COMPANIA, 
                     ANO, 
                     TIPO_CPTE,
                     COMPROBANTE,
                     CONSECUTIVO,
                     FECHA
              FROM DETALLE_COMPROBANTE_PPTAL
              WHERE COMPANIA = UN_COMPANIA
                AND ANO      = UN_ANIO
                AND TIPO_CPTE= UN_TIPO
                AND COMPROBANTE BETWEEN UN_COMPROBANTE_INI AND UN_COMPROBANTE_FIN
                AND SALDO_DIS   = 0
                AND SALDO_RES   = 0
                AND SALDO_REO   = 0
                AND SALDO_EGR   = 0)
    LOOP    
        PR_ACTUALIZAR_SALDOPPTAL_MOV(UN_COMPANIA    => UN_COMPANIA 
                                    ,UN_ANIO        => UN_ANIO
                                    ,UN_TIPO        => RS.TIPO_CPTE
                                    ,UN_COMPROBANTE => RS.COMPROBANTE
                                    ,UN_CONSECUTIVO => RS.CONSECUTIVO
                                    ,UN_FECHA       => RS.FECHA);
    END LOOP COMPROBANTES;

END PR_CONGELARSALDOSMAN;

FUNCTION FC_GENERAR_PLANO_SECRETARIA 

   /*
    NAME              : FC_GENERAR_PLANO_SECRETARIA
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÑAS
    DATE MIGRADOR     : 02/10/2020
    TIME              : 10:47 AM
    SOURCE MODULE     : Presupuesto
    MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MODIFIED     : 12/04/2023
    TIME              : 8:00 AM
	MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MODIFIED     : 11/07/2023
    MODIFIER          : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MODIFIED     : 11/07/2023
    MODIFIED          : ASJUSTES A LAS COLUMNAS DEL PLANO TICKET7737728 
    DESCRIPTION       : GENERAR LOS PLANOS  PARA  SECRETARIA  DE HACIENDA  DE BOGOTA 
    @NAME:   generarPlanoSecretaria
    @METHOD:  GET
    */
(
 UN_COMPANIA         PCK_SUBTIPOS.TI_COMPANIA,
 UN_FECHAINICIAL     PCK_SUBTIPOS.TI_FECHA, 
 UN_FECHAFINAL       PCK_SUBTIPOS.TI_FECHA,  
 UN_TIPOCOMP         PCK_SUBTIPOS.TI_CLASECOMPROPPTO,
 UN_CPTEINICIAL      PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL,
 UN_CPTEFINAL        PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL,
 UN_RESPONSABLEPPTO  PCK_SUBTIPOS.TI_TERCERO
)
RETURN CLOB AS 
MI_SESSION          NUMBER;
MI_FECHAINI         DATE :=UN_FECHAINICIAL;
MI_FECHAFIN         DATE :=UN_FECHAFINAL;
MI_COMPANIA         VARCHAR2(11):=UN_COMPANIA;
MI_ETAPA            VARCHAR2(3200);
MI_VERSION          VARCHAR2(3200):='GENERACION DE ARCHIVO PLANO HACIENDA DISTRITAL - VERSION 2020 RELEASE 1 ';

MI_TERCERO_DIS       VARCHAR2(50);
MI_TIPOID_TER_DIS    VARCHAR2(10);
MI_CONSECUTIVO      NUMBER:=0;
MI_REGISTROS        NUMBER:=0;
MI_RS1              NUMBER;
MI_CMPTE_AFECTADO   NUMBER;
MI_CONSECUTIVOPPTO  NUMBER;
MI_FECHA            DATE;
MI_RETORNO          CLOB:='';
MI_STR              VARCHAR2(32000):='';
MI_RUBROFUT1        VARCHAR2(254);
MI_VALOR_P          VARCHAR2(32000) := '';
MI_ANTDCT           VARCHAR2(32000) := '';
MI_STR1             VARCHAR2(32000) := '';
MI_STR2             VARCHAR2(32000) := '';
MI_STR3             VARCHAR2(32000) := '';
MI_STR4             VARCHAR2(32000) := '';
MI_STRERROR         CLOB:='';
MI_TIPOCC           VARCHAR2(6);
MI_RESPONSABLE      VARCHAR2(30); -- CC4091

BEGIN
    MI_ETAPA:='1';
    SELECT SYS_CONTEXT('USERENV','SESSIONID') 
       INTO MI_SESSION
    FROM DUAL;
    MI_ETAPA:='7' ;   

	BEGIN
      SELECT TIPOID  
      INTO MI_TIPOCC
      FROM TERCERO
      WHERE COMPANIA = UN_COMPANIA AND NIT = UN_RESPONSABLEPPTO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_TIPOCC := '';                
    END;

    /*Obtiene el ID del Responsable 
    INI_CC4091_MPEREZ*/
    BEGIN
      SELECT ID_RESPONSABLE
        INTO MI_RESPONSABLE 
        FROM RESPONSABLE
       WHERE COMPANIA = UN_COMPANIA 
         AND CEDULA = UN_RESPONSABLEPPTO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_RESPONSABLE := ' ';                
    END;

    IF(MI_RESPONSABLE = '' OR MI_RESPONSABLE = NULL OR MI_RESPONSABLE = ' ') THEN
        MI_STRERROR := 'El responsable '||UN_RESPONSABLEPPTO|| ' asociado al documento no tiene configurado ID Responsable BOGDATA'||CHR(13)||CHR(10);
    END IF;
    --FIN_CC4091_MPEREZ
-- VAMOS CON REGISTRO TIPO 1
    FOR MI_RS1 IN
           ( 
      SELECT PPTAL.NUMERO
              ,'' DOCT_PRESUPUESTARIO
            ,TC.CLASE
            , CASE WHEN TC.CLASE  =  'DIS'
             THEN ''
             ELSE PPTAL.TIPO_CPTE_AFECT
             END CPTE_AFECT
            ,CASE WHEN TC.CLASE  =  'DIS'
             THEN ''
             ELSE TO_CHAR(PPTAL.CMPTE_AFECTADO)
             END CPTE_AFECTADO
            ,CASE WHEN TC.CLASE  =  'DIS' 
               THEN '030'
               ELSE '050'
             END TIPO_DOCUMENTO
            ,PPTAL.FECHA FECHADCTO
            ,PPTAL.FECHA FECHACONT
            ,PPTAL.FECHA_VENCIMIENTO FECHAVENC
            ,CASE WHEN TC.CLASE  =  'DIS'  
               THEN 'CP' 
               ELSE 'RP'
             END CLASE_DOCUMENTO
            ,'COP' MONEDA
            ,'1001' SOCIEDAD
           , PPTAL.NUMERO POSISICONDCTO 
            ,REPLACE(REPLACE(P.CODIGO_EQUIV, CHR(10),''),CHR(13),'') POSICICONPPTO -- TICKET7729430
            ,'0214-01' CENTROGESTOR
            ,P.FUENTERECURSOS FONDOS
          --  ,0 FONDOS
            ,'' CENTROCOSTE
            ,DPPTAL.VALOR_DEBITO IMPORMONEDADOCUMENTO 
            -- TICKET 7729430 MPEREZ
            ,NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA, UN_NOMBRE =>'PREFIJO PMR', UN_MODULO =>3, UN_FECHA_PAR=>SYSDATE ), 'PM/0214/') || DPPTAL.REFERENCIA ELEMENTOPEP
            ,'7990990000' CUENTA_MAYOR
            , '' RER_RECURSO_REFT_CDP
            , CASE WHEN TC.CLASE  =  'DIS'
               THEN ''
               ELSE TO_CHAR(DPPTAL.CONSECUTIVOPPTO)
              END POSICION_RESERVA
            , '' INDICADORCONCLUCION
            , '' POSICION_DCTO
            ,'0002-008' AREAFUNCIONAL
            , PPTAL.ANO PERIODOPRESUPUESTAL
            ,CASE WHEN TC.CLASE  =  'DIS'
               THEN ' '
               ELSE 
                 CASE WHEN LENGTH(NVL(PPTAL.TIPOCONTRATO, ' ')) < 2
                    THEN  TOC1.CODIGO_SGC
                    ELSE  TOC.CODIGO_SGC
                 END 
             END  TIPOCOMPROMISO
            ,CASE WHEN TC.CLASE  =  'DIS' 
               THEN ' '
               ELSE 
                  CASE WHEN LENGTH(NVL(PPTAL.TIPOCONTRATO, ' ')) < 2
                    THEN PPTAL.NRO_DOCUMENTO 
                    ELSE  TO_CHAR(PPTAL.NUMEROCONTRATO)
                  END  
             END NCOMPROMISO
            ,CASE WHEN LENGTH(NVL(PPTAL.TIPOCONTRATO, ' ')) < 2
                    THEN PPTAL.FECHA
                    ELSE OC.FECHA
                 END FECHAINI
             , CASE WHEN LENGTH(NVL(PPTAL.TIPOCONTRATO, ' ')) < 2
                    THEN PPTAL.FECHA_VCN_DOC
                    ELSE OC.FECHAFINALIZACION
                  END  FECHAFINAL  --- 7717223 -MPEREZ
            ,CASE WHEN TC.CLASE  =  'DIS' 
               THEN ''
               ELSE '02'
             END  FORMAPAGO
            ,CASE WHEN TC.CLASE  =  'DIS' 
               THEN ''
               ELSE 
                  CASE WHEN LENGTH(NVL(PPTAL.TIPOCONTRATO, ' ')) < 2
                    THEN TOC1.CODIGO_R6
                    ELSE OC.DESTINO 
                  END 
             END MODALIDADSELEC
            ,DPPTAL.DESCRIPCION OBJETO
            ,'' RESPONSABLE 
            ,CASE WHEN MI_TIPOCC =  'C' THEN 'CC' ELSE CASE WHEN MI_TIPOCC =  'T' THEN 'TI' ELSE  CASE WHEN  MI_TIPOCC  =  'N' THEN 'NIT' ELSE MI_TIPOCC END  END END  CLASE_DOC_IDENT
            ,MI_RESPONSABLE N_IDENT_RESPON --CC4091_MPEREZ
            ,CASE WHEN TC.CLASE  =  'DIS' 
                THEN PPTAL.NUMERO
                ELSE DPPTAL.CMPTE_AFECTADO
             END N_CDP_EXTERNO
            ,PPTAL.NRO_DOCUMENTO N_OFICIO    
            ,PPTAL.FECHA FECHA_IFICIO
            , '' SOLICITANTE 
            ,CASE WHEN TC.CLASE  =  'DIS' THEN
                CASE WHEN TRO.TIPOID =  'C' THEN 'CC' ELSE CASE WHEN TRO.TIPOID =  'T' THEN 'TI' ELSE  CASE WHEN  TRO.TIPOID  =  'N' THEN 'NIT' ELSE TRO.TIPOID END  END END  
            ELSE
                CASE WHEN TROAFECT.TIPOID =  'C' THEN 'CC' ELSE CASE WHEN TROAFECT.TIPOID =  'T' THEN 'TI' ELSE  CASE WHEN  TROAFECT.TIPOID  =  'N' THEN 'NIT' ELSE TROAFECT.TIPOID END  END END  
            END CLASE_DOCT_SOLICI
            ,CASE WHEN TC.CLASE  =  'DIS' THEN
                SD.TERCERO 
            ELSE
                SDAFECT.TERCERO
            END N_IDEN_DOLICITANTE
            ,CASE WHEN TC.CLASE  =  'DIS' THEN
                --INI_CC4091_MPEREZ
                NVL(SOLICITANTE.ID_SOLICITANTE,' ') 
            ELSE
                NVL(SOLIAFECT.ID_SOLICITANTE,' ')
                --FIN_CC4091_MPEREZ
            END ID_SOLICITANTE
            ,'' SOLICITANTE_ANULA
            ,'' CLASE_DOCT_SOLI_ANULA
            ,'' NUM_DOCT_SOLI_ANULA
            ,'' MOTIVO_CONCLUSION
            ,'' BENEFICIO_ACREEDOR
            ,CASE WHEN TC.CLASE  =  'DIS'
                THEN ''
                ELSE CASE WHEN T.TIPOID =  'C' THEN 'CC' ELSE CASE WHEN T.TIPOID =  'T' THEN 'TI' ELSE CASE WHEN  T.TIPOID   =  'N' THEN 'NIT' ELSE T.TIPOID  END END END 
				END CLASE_DCTO_IDENT
            ,CASE WHEN TC.CLASE  =  'DIS' 
                THEN ''
                ELSE PPTAL.TERCERO
             END N_IDENTIFI
            ,'' REDUCCION_PARCIAL
            ,'' IMPORTANTE_REDUCCION
            ,CASE WHEN TC.CLASE  =  'DIS' 
                THEN ''
                ELSE SUBSTR(PPTAL.NUMERO,-4)
            END NUMERO_CRP_EXTERNO
            ,'' REFERENCIA_FICHA
            ,'' N_OFICIO_ANULA
            ,'' FECHA_OFICIO_ANULA
            ,DPPTAL.DESCRIPCION 
FROM COMPROBANTE_PPTAL PPTAL
INNER JOIN TIPO_COMPROBPP TC
ON TC.COMPANIA = PPTAL.COMPANIA
AND TC.CODIGO  = PPTAL.TIPO
INNER JOIN TERCERO T
ON PPTAL.COMPANIA  = T.COMPANIA
AND PPTAL.TERCERO  = T.NIT
AND PPTAL.SUCURSAL = T.SUCURSAL
INNER JOIN DETALLE_COMPROBANTE_PPTAL DPPTAL
ON PPTAL.COMPANIA = DPPTAL.COMPANIA
AND PPTAL.ANO     = DPPTAL.ANO
AND PPTAL.TIPO    = DPPTAL.TIPO_CPTE
AND PPTAL.NUMERO  = DPPTAL.COMPROBANTE
INNER JOIN PLAN_PRESUPUESTAL P
ON DPPTAL.COMPANIA = P.COMPANIA
AND DPPTAL.ANO     = P.ANO
AND DPPTAL.CUENTA  = P.CODIGO
--INI_TICKET7729810 MPEREZ
LEFT JOIN SOLICITUDDISPONIBILIDAD SD
ON DPPTAL.COMPANIA = SD.COMPANIA
AND DPPTAL.ANO     = SD.ANO
AND DPPTAL.TIPO_SOLICITUD  = SD.TIPO_SOLICITUD
AND DPPTAL.NUMERO_SOLICITUD  = SD.NUMERO
LEFT JOIN TERCERO TRO
ON SD.COMPANIA = TRO.COMPANIA
AND SD.TERCERO = TRO.NIT
AND SD.SUCURSAL = TRO.SUCURSAL
--INI_CC4091_MPEREZ
LEFT JOIN RESPONSABLE SOLICITANTE
ON TRO.COMPANIA = SOLICITANTE.COMPANIA
AND TRO.NIT = SOLICITANTE.CEDULA
AND TRO.SUCURSAL = SOLICITANTE.SUCURSAL
--FIN_CC4091_MPEREZ
LEFT JOIN DETALLE_COMPROBANTE_PPTAL DPPTALAFECTADO
ON DPPTAL.COMPANIA = DPPTALAFECTADO.COMPANIA
AND DPPTAL.ANO_AFECT = DPPTALAFECTADO.ANO
AND DPPTAL.TIPO_CPTE_AFECT = DPPTALAFECTADO.TIPO_CPTE
AND DPPTAL.CMPTE_AFECTADO = DPPTALAFECTADO.COMPROBANTE
AND DPPTAL.CONSECUTIVOPPTO = DPPTALAFECTADO.CONSECUTIVO
LEFT JOIN SOLICITUDDISPONIBILIDAD SDAFECT
ON DPPTALAFECTADO.COMPANIA = SDAFECT.COMPANIA
AND DPPTALAFECTADO.ANO     = SDAFECT.ANO
AND DPPTALAFECTADO.TIPO_SOLICITUD  = SDAFECT.TIPO_SOLICITUD
AND DPPTALAFECTADO.NUMERO_SOLICITUD  = SDAFECT.NUMERO
LEFT JOIN TERCERO TROAFECT
ON SDAFECT.COMPANIA = TROAFECT.COMPANIA
AND SDAFECT.TERCERO = TROAFECT.NIT
AND SDAFECT.SUCURSAL = TROAFECT.SUCURSAL
--INI_CC4091_MPEREZ
LEFT JOIN RESPONSABLE SOLIAFECT
ON TROAFECT.COMPANIA = SOLIAFECT.COMPANIA
AND TROAFECT.NIT = SOLIAFECT.CEDULA
AND TROAFECT.SUCURSAL = SOLIAFECT.SUCURSAL
--FIN_CC4091_MPEREZ
--FIN_TICKET7729810 MPEREZ
LEFT JOIN TIPOORDENDECOMPRA TOC
ON PPTAL.COMPANIA      = TOC.COMPANIA
AND PPTAL.TIPOCONTRATO = TOC.CODIGO
LEFT JOIN TIPOORDENDECOMPRA TOC1
ON  PPTAL.COMPANIA  =  TOC1.COMPANIA 
AND PPTAL.TIPO_DOCUMENTO = TOC1.CODIGO  
LEFT JOIN ORDENDECOMPRA OC
ON PPTAL.COMPANIA        = OC.COMPANIA
AND PPTAL.TIPOCONTRATO   = OC.CLASEORDEN
AND PPTAL.NUMEROCONTRATO = OC.NUMERO
      WHERE PPTAL.COMPANIA = MI_COMPANIA  
        AND TC.CLASE IN (UN_TIPOCOMP)
        AND PPTAL.NUMERO BETWEEN UN_CPTEINICIAL AND UN_CPTEFINAL
            AND PPTAL.FECHA  BETWEEN MI_FECHAINI AND MI_FECHAFIN
       ORDER BY PPTAL.FECHA, PPTAL.NUMERO, DPPTAL.CONSECUTIVO
            )
    LOOP
       MI_VALOR_P:= MI_RS1.NUMERO;

 IF MI_RS1.CLASE = 'DIS' THEN
    MI_TERCERO_DIS := '';
  ELSE
    BEGIN

     SELECT  PPTAL1.TERCERO N_IDEN_DOLICITANTE_DIS, 
             CASE WHEN T.TIPOID =  'C' THEN 'CC' ELSE CASE WHEN T.TIPOID =  'T' THEN 'TI' ELSE  CASE WHEN  T.TIPOID  =  'N' THEN 'NIT' ELSE T.TIPOID  END  END END  CLASE_DOC_IDENT
     INTO   MI_TERCERO_DIS, --Posc Doc Pres
            MI_TIPOID_TER_DIS
     FROM COMPROBANTE_PPTAL PPTAL
      INNER JOIN COMPROBANTE_PPTAL PPTAL1
          ON PPTAL.COMPANIA = PPTAL1.COMPANIA
          AND PPTAL.ANO = PPTAL1.ANO
          AND PPTAL.TIPO_CPTE_AFECT = PPTAL1.TIPO
          AND PPTAL.CMPTE_AFECTADO = PPTAL1.NUMERO
    INNER JOIN TERCERO T
         ON PPTAL1.COMPANIA = T.COMPANIA
         AND PPTAL1.TERCERO = T.NIT
         AND PPTAL1.SUCURSAL = T.SUCURSAL
    INNER JOIN TIPO_COMPROBPP TC
          ON  TC.COMPANIA = PPTAL.COMPANIA
          AND TC.CODIGO = PPTAL.TIPO
     WHERE PPTAL.COMPANIA      = MI_COMPANIA
       AND PPTAL.TIPO_CPTE_AFECT     = MI_RS1.CPTE_AFECT
       AND PPTAL.CMPTE_AFECTADO   = MI_RS1.CPTE_AFECTADO
       AND ROWNUM       <= 1;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_TERCERO_DIS := '';
      MI_TIPOID_TER_DIS :='';
    END;
END IF; 

    IF(NVL(MI_RS1.ID_SOLICITANTE,' ') = ' ' OR NVL(MI_RS1.N_IDENT_RESPON,' ') = ' ') THEN
        IF(NVL(MI_RS1.ID_SOLICITANTE,' ') = ' ') THEN
            MI_STRERROR := MI_STRERROR || 'El solicitante ' ||MI_RS1.N_IDEN_DOLICITANTE||' asociado al documento no tiene configurado ID Solicitante BOGDATA'||CHR(13)||CHR(10);
        END IF;  
    ELSE
        IF MI_ANTDCT = NVL(MI_RS1.NUMERO,'') THEN 
           MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
        ELSE
           MI_CONSECUTIVO:=1;
        END IF;
         MI_ANTDCT := NVL(MI_RS1.NUMERO,'');
         MI_STR1 := '';
         MI_STR2 := '';
         MI_STR3 := '';                

        IF MI_RS1.CLASE = 'DIS' THEN
            MI_STR1:=
                LPAD(SUBSTR(MI_RS1.N_CDP_EXTERNO,-4),10,' ')                || CHR(09)
                || LPAD(SUBSTR(MI_CONSECUTIVO,1,3) ,10,' ')                 || CHR(09)                
                || SUBSTR(TO_CHAR(MI_RS1.FECHADCTO,'DD.MM.YYYY'),1,10)      || CHR(09)
                || SUBSTR(TO_CHAR(MI_RS1.FECHACONT,'DD.MM.YYYY'),1,10)      || CHR(09)
                || LPAD(SUBSTR(MI_RS1.CLASE_DOCUMENTO,1,2),2,' ')           || CHR(09)
                || LPAD(SUBSTR(MI_RS1.SOCIEDAD,1,4),4,' ')                  || CHR(09) 
                || LPAD(SUBSTR(MI_RS1.MONEDA,1,5),5,' ')                    || CHR(09);         
           
            MI_STR2 :=  
                LPAD(SUBSTR(MI_RS1.IMPORMONEDADOCUMENTO,1,13),15,' ')       || CHR(09)
                || LPAD(NVL(MI_RS1.POSICICONPPTO,' '),24,' ')               || CHR(09)
                || LPAD(NVL(MI_RS1.FONDOS,' '),16,' ')                      || CHR(09)
                || LPAD(NVL(MI_RS1.ELEMENTOPEP,' ') ,40,' ')                || CHR(09)
                || LPAD(MI_RS1.PERIODOPRESUPUESTAL,10,' ')                  || CHR(09)
                || LPAD(MI_RS1.CUENTA_MAYOR,10,' ')                         || CHR(09)
                || LPAD(NVL(MI_RS1.OBJETO,' '),200,' ')                     || CHR(09)
                || LPAD(NVL(MI_RS1.N_OFICIO,' '),20,' ')                    || CHR(09)
                || LPAD(NVL(TO_CHAR(MI_RS1.FECHA_IFICIO,'DD.MM.YYYY'),' '),10,' ')   || CHR(09)
                || LPAD(NVL(MI_RS1.ID_SOLICITANTE,' '),30,' ')              || CHR(09)
                || LPAD(NVL(MI_RS1.N_IDENT_RESPON,' '),30,' ')              || CHR(09)
                || LPAD(' ',10,' ');

        ELSE
            MI_STR1:=
                LPAD(SUBSTR(MI_RS1.NUMERO_CRP_EXTERNO,1,10),10,' ')         || CHR(09)
                || LPAD(NVL(MI_RS1.POSICICONPPTO,' '),10,' ')               || CHR(09)
                || SUBSTR(TO_CHAR(MI_RS1.FECHADCTO,'DD.MM.YYYY'),1,10)      || CHR(09)
                || SUBSTR(TO_CHAR(MI_RS1.FECHACONT,'DD.MM.YYYY'),1,10)      || CHR(09)
                || LPAD(SUBSTR(MI_RS1.SOCIEDAD,1,4),4,' ')                  || CHR(09)    
                || SUBSTR(MI_RS1.CLASE_DOCUMENTO,1,2)                       || CHR(09)
                || LPAD(SUBSTR(MI_RS1.MONEDA,1,5),5,' ')                    || CHR(09);         
           
            MI_STR2 :=  
                LPAD(SUBSTR(MI_RS1.IMPORMONEDADOCUMENTO,1,13),15,' ')       || CHR(09)
                || LPAD(NVL(MI_RS1.CPTE_AFECTADO,' '),10,' ')               || CHR(09)
                || LPAD(SUBSTR(MI_CONSECUTIVO,1,3),3,' ')                   || CHR(09)
                || LPAD(NVL(MI_RS1.OBJETO,' '),220,' ')                     || CHR(09)
                || LPAD(NVL(MI_RS1.TIPOCOMPROMISO,' '),30,' ')              || CHR(09)
                || LPAD(NVL(MI_RS1.NCOMPROMISO,' '),16,' ')                 || CHR(09)
                || LPAD(NVL(TO_CHAR(MI_RS1.FECHAINI,'DD.MM.YYYY'),' '),10,' ')       || CHR(09)
                || LPAD(NVL(TO_CHAR(MI_RS1.FECHAFINAL,'DD.MM.YYYY'),' '),10,' ')     || CHR(09)
                || LPAD(NVL(MI_RS1.CLASE_DCTO_IDENT,' '),6,' ')             || CHR(09)
                || LPAD(NVL(MI_RS1.N_IDENTIFI,' '),60,' ')                  || CHR(09)
                || LPAD(NVL(MI_RS1.ID_SOLICITANTE,' '),30,' ')              || CHR(09)
                || LPAD(NVL(MI_RS1.N_IDENT_RESPON,' '),30,' ')              || CHR(09)
                || LPAD(' ',10,' ');  
        END IF;        
        
        MI_STR  := MI_STR1 || CHR(09) ||  MI_STR2;   
    
        IF LENGTH(MI_RETORNO) > 0 THEN
          MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
        ELSE
          MI_RETORNO:=MI_STR;
        END IF;

        MI_REGISTROS:=MI_REGISTROS+1;
    END IF;
    END LOOP;

    IF LENGTH(MI_STRERROR) > 0 THEN
        MI_STRERROR := MI_STRERROR||CHR(13)||CHR(10)||'No es posible generar el archivo BOGDATA porque faltan datos de homologación en la ruta PRESUPUESTO/ARCHIVOS/RESPONSABLES';
        MI_RETORNO := MI_STRERROR;
    END IF;
    RETURN MI_RETORNO;
END FC_GENERAR_PLANO_SECRETARIA;




FUNCTION  FC_GENERAR_PLANO_ORDEN_PAGO
 /*
    NAME              : FC_ENUMERAR
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÑAS
    DATE MIGRADOR     : 02/10/2020
    TIME              : 10:47 AM
    SOURCE MODULE     : Presupuesto
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : GERERAR LOS PLANOS  PARA  SECRETARIA  DE HACIENDA  DE BOGOTA 
    @NAME:   generarPlanoOrdenPago
    @METHOD:  GET
    */
(
 UN_COMPANIA         PCK_SUBTIPOS.TI_COMPANIA,
 UN_FECHAINICIAL     PCK_SUBTIPOS.TI_FECHA, 
 UN_FECHAFINAL       PCK_SUBTIPOS.TI_FECHA,  
 UN_TIPOCOMP         PCK_SUBTIPOS.TI_CLASECOMPROPPTO,
 UN_RESPONSABLEPPTO  PCK_SUBTIPOS.TI_TERCERO,
 UN_CPTEINICIAL      PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL,
 UN_CPTEFINAL        PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL,
 UN_VIGENCIA         VARCHAR2
)
RETURN CLOB AS 

MI_SESSION          NUMBER;
MI_FECHAINI         DATE :=UN_FECHAINICIAL;
MI_FECHAFIN         DATE :=UN_FECHAFINAL;
MI_COMPANIA         VARCHAR2(11):=UN_COMPANIA;
MI_ETAPA            VARCHAR2(3200);
MI_VERSION          VARCHAR2(3200):='GENERACION ARCHIVO PLANO ORDEN PAGO - VERSION 2020 RELEASE 01 ';
MI_CONSECUTIVO     NUMBER:=0;
MI_REGISTROS       NUMBER:=0;
MI_RS1             NUMBER;
MI_RETORNO          CLOB:='';
MI_VALOR_P           VARCHAR2(32000) := '';
MI_ANTDCT            VARCHAR2(32000) := '';
MI_STR               VARCHAR2(32000):='';
MI_STR1              VARCHAR2(32000) := '';
MI_STR2              VARCHAR2(32000) := '';
MI_STR3              VARCHAR2(32000) := '';
MI_STR4              VARCHAR2(32000) := '';
MI_STR5              VARCHAR2(32000) := '';
MI_STR_2             VARCHAR2(32000):='';
MI_STR1_2            VARCHAR2(32000) := '';

BEGIN
    MI_ETAPA:='1';
    SELECT SYS_CONTEXT('USERENV','SESSIONID') 
    INTO MI_SESSION
    FROM DUAL;
    MI_ETAPA:='2' ;   
    MI_ETAPA:='4' ;   
-- VAMOS CON REGISTRO TIPO 1
    FOR MI_RS1 IN
           ( 
        SELECT PPTAL.NUMERO
      ,'1001' SOCIEDAD
      ,'' N_DOCUMENTO
      ,CNT.ANO EJERCICIO
      ,'OP' CLASE_DOCUMENTO
      ,TO_CHAR(SYSDATE ,'RRRRMMDD') FECHA_DOCUMENTO    --- TICKET7738447 MPEREZ
      ,TO_CHAR(SYSDATE ,'RRRRMMDD') FE_CONTABILIZACION --- TICKET7738447 MPEREZ
      ,EXTRACT (MONTH FROM CNT.FECHA) PERIODO_CONTABLE
      ,'' REFERENCIA
      ,'ORDEN DE PAGO' TXT_CAB_DOCUMENTO
      ,'COP' MONEDA
      ,'1000' ENTIDAD_CP
      ,'' MARCA_ANULACION
      ,PPTAL.NRO_AUTORIZACION_VF NUMERO_OP
      ,'02'  TIPO_PAGO
      ,REPLACE(REPLACE(CNT.DESCRIPCION,CHR(13),' '),CHR(10),' ') OBJETO      
      ,'7990990000' CUENTA_MAYOR
      ,DPPTAL.VALOR_DEBITO IMPORTE1
      , DPPTAL.VALOR_CREDITO IMPORTE2
       ,'' ASIGNACION
       ,'' TEXTO
       ,'1000' SOCIEDAD_CO
       ,'' CENTRO_COSTE
       ,'' CLIENTE
       ,'' PROVEEDOR
       ,'' VIA_PAGO
       ,'' CENTRO_BENEFICIO
       , DECODE(TRIM(P.CODIGO_EQUIV), '', P.CODIGO, P.CODIGO_EQUIV) POSPRE --Valido para la posiciÃ³n debito
      ,'0214-01' CENTROGESTOR
      ,''FONDOS 
      ,'' AREAFUNCIONAL
      ,PPTAL.ANO PERIODO_PRESUPUESTAL
      ,NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA, UN_NOMBRE =>'PREFIJO PMR', UN_MODULO =>3, UN_FECHA_PAR=>SYSDATE ), 'PM/0214/') || DPPTAL.REFERENCIA ELEMENTO_PEP1 --TICKET7729430
      ,'' DOC_PRESUPUESTARIO
      ,'' POSICION_DOCUMENTO
      ,'214' SEGMENTO
      ,'' NUMERO_CDP
      ,LTRIM(SUBSTR(DPPTAL.CMPTE_AFECTADO,5),0) NUMERO_CRP       
      ,P.TIPOVIGENCIA TV
FROM COMPROBANTE_PPTAL   PPTAL 
        INNER JOIN COMPROBANTE_CNT CNT
          ON PPTAL.COMPANIA = CNT.COMPANIA
         AND PPTAL.ANO = CNT.ANO
         AND PPTAL.TIPO = CNT.TIPO
         AND PPTAL.NUMERO = CNT.NUMERO
        INNER JOIN TIPO_COMPROBPP TC
          ON  TC.COMPANIA = PPTAL.COMPANIA
          AND TC.CODIGO = PPTAL.TIPO
        INNER JOIN DETALLE_COMPROBANTE_PPTAL DPPTAL
         ON PPTAL.COMPANIA=DPPTAL.COMPANIA
         AND PPTAL.ANO=DPPTAL.ANO
         AND PPTAL.TIPO=DPPTAL.TIPO_CPTE
         AND PPTAL.NUMERO=DPPTAL.COMPROBANTE
        INNER JOIN PLAN_PRESUPUESTAL P
          ON  DPPTAL.COMPANIA      = P.COMPANIA
          AND DPPTAL.ANO           = P.ANO
          AND DPPTAL.CUENTA = P.CODIGO
        LEFT JOIN PLAN_PPTAL_CONFIG R --nota: la tabla rubro no tiene compaÃ±ia
        ON PPTAL.COMPANIA        = R.COMPANIA
        AND PPTAL.ANO            =R.ANO
       AND DPPTAL.CUENTA         = R.CODIGO
       AND DPPTAL.CENTRO_COSTO   = R.CENTRO_COSTO
       AND DPPTAL.TERCERO        = R.TERCERO
       AND DPPTAL.SUCURSAL       = R.SUCURSAL
       AND DPPTAL.AUXILIAR       = R.AUXILIAR
       AND DPPTAL.REFERENCIA     = R.REFERENCIA
       AND DPPTAL.FUENTE_RECURSO = R.FUENTE_RECURSO
       WHERE PPTAL.COMPANIA = MI_COMPANIA
        AND TC.CLASE IN (UN_TIPOCOMP)
         AND PPTAL.NUMERO BETWEEN UN_CPTEINICIAL AND UN_CPTEFINAL
       AND PPTAL.FECHA  BETWEEN MI_FECHAINI  AND MI_FECHAFIN
       AND P.TIPOVIGENCIA = UN_VIGENCIA
      --- AND P.FUENTERECURSOS LIKE '3%' TICKET 7717223 - MPEREZ - Se comenta porque el filtro no es necesario 
      ORDER BY PPTAL.FECHA,PPTAL.CMPTE_AFECTADO
            )
    LOOP
       MI_VALOR_P:= MI_RS1.NUMERO;

--TRIM(TO_CHAR(MI_CONSECUTIVO,'000000'))
        -- EN SOLICITUD DEL TAR 1000102901 SE PIDE QUE EL EL CONSECUTIVO SIEMPRE SEA UNO 
        --IF MI_ANTDCT = NVL(MI_RS1.NUMERO,'') THEN 
        --   MI_CONSECUTIVO:=MI_CONSECUTIVO+1;

           MI_CONSECUTIVO:=1;
        --END IF;
         MI_ANTDCT := NVL(MI_RS1.NUMERO,'');
         MI_STR1 := '';
         MI_STR2 := '';
         MI_STR3 := '';
         MI_STR4 := '';
         MI_STR5 := '';

         MI_STR1_2 := '';

        MI_STR1:=
               CASE WHEN MI_CONSECUTIVO = 1 THEN   RPAD(SUBSTR(MI_RS1.SOCIEDAD,1,4),4)      ELSE '' END          || CHR(09)

            || CASE WHEN MI_CONSECUTIVO = 1 THEN   RPAD(SUBSTR(MI_RS1.N_DOCUMENTO,1,10),10) ELSE '' END          || CHR(09)
            || CASE WHEN MI_CONSECUTIVO = 1 THEN   RPAD(SUBSTR(MI_RS1.EJERCICIO,1,4),4)     ELSE '' END          || CHR(09)

            || CASE WHEN MI_CONSECUTIVO = 1 THEN   RPAD(SUBSTR(MI_RS1.CLASE_DOCUMENTO,1,2),2)      ELSE '' END          || CHR(09)
            || CASE WHEN MI_CONSECUTIVO = 1 THEN   RPAD(SUBSTR(MI_RS1.FECHA_DOCUMENTO,1,10),10)    ELSE '' END          || CHR(09)
            || CASE WHEN MI_CONSECUTIVO = 1 THEN   RPAD(SUBSTR(MI_RS1.FE_CONTABILIZACION,1,10),10) ELSE '' END          || CHR(09)    
            || CASE WHEN MI_CONSECUTIVO = 1 THEN   RPAD(SUBSTR(MI_RS1.PERIODO_CONTABLE,1,2),2)     ELSE '' END          || CHR(09)
            || CASE WHEN MI_CONSECUTIVO = 1 THEN   RPAD(SUBSTR(MI_RS1.REFERENCIA,1,10),10)         ELSE '' END          || CHR(09)
            || CASE WHEN MI_CONSECUTIVO = 1 THEN   RPAD(SUBSTR(MI_RS1.TXT_CAB_DOCUMENTO,1,10),10)  ELSE '' END          || CHR(09)
            || CASE WHEN MI_CONSECUTIVO = 1 THEN   RPAD(SUBSTR(MI_RS1.MONEDA,1,5),5)               ELSE '' END          || CHR(09)

            || CASE WHEN MI_CONSECUTIVO = 1 THEN   RPAD(SUBSTR(MI_RS1.ENTIDAD_CP,1,10),10)         ELSE '' END          || CHR(09)
            || CASE WHEN MI_CONSECUTIVO = 1 THEN   RPAD(SUBSTR(MI_RS1.MARCA_ANULACION,1,10),10)    ELSE '' END          || CHR(09) -- CAMPO NUEVO
            || CASE WHEN MI_CONSECUTIVO = 1 THEN   RPAD(SUBSTR(MI_RS1.NUMERO_OP,1,10),10)          ELSE '' END          || CHR(09)
            || CASE WHEN MI_CONSECUTIVO = 1 THEN   RPAD(SUBSTR(MI_RS1.TIPO_PAGO,1,10),10)          ELSE '' END          || CHR(09)
            || CASE WHEN MI_CONSECUTIVO = 1 THEN   RPAD(SUBSTR(REPLACE(MI_RS1.OBJETO, CHR(10), ' '),1,2000),2000)         ELSE '' END          || CHR(09);


        MI_STR1_2 := MI_STR1     
            || RPAD(SUBSTR('7990970000',1,10),10)                            || CHR(09);  

        MI_STR1 := MI_STR1 
            || RPAD(SUBSTR(MI_RS1.CUENTA_MAYOR,1,10),10)                     || CHR(09);


        MI_STR2 := 
               RPAD(SUBSTR(MI_RS1.ASIGNACION,1,10),10)                       || CHR(09)
            || RPAD(SUBSTR(MI_RS1.TEXTO,1,10),10)                            || CHR(09)
            || RPAD(SUBSTR(MI_RS1.SOCIEDAD_CO,1,10),10)                      || CHR(09)
            || RPAD(SUBSTR(MI_RS1.CENTRO_COSTE,1,10),10)                     || CHR(09)
            || RPAD(SUBSTR(MI_RS1.CLIENTE,1,10),10)                          || CHR(09)
            || RPAD(SUBSTR(MI_RS1.PROVEEDOR,1,10),10)                        || CHR(09)
            || RPAD(SUBSTR(MI_RS1.VIA_PAGO,1,10),10)                         || CHR(09)
            || RPAD(SUBSTR(MI_RS1.CENTRO_BENEFICIO,1,10),10);

            MI_STR3 :=
               RPAD(SUBSTR(MI_RS1.CENTROGESTOR,1,16),16)                 || CHR(09)
            || RPAD(SUBSTR(MI_RS1.FONDOS,1,10),10)                       || CHR(09)
            || RPAD(SUBSTR(MI_RS1.AREAFUNCIONAL,1,10),10)                || CHR(09)
            || RPAD(SUBSTR(MI_RS1.PERIODO_PRESUPUESTAL,1,4),4)           || CHR(09);


            MI_STR4 :=  
               RPAD(SUBSTR(MI_RS1.DOC_PRESUPUESTARIO,1,10),10)           || CHR(09);

            --Se agrega para llevarlos al final del plano. (TAR: ; FECHA: 23/06/2021; AUTOR: JEG)
             MI_STR5 :=                        
            RPAD(SUBSTR(MI_RS1.SEGMENTO,1,3),3)                          || CHR(09) 
            || RPAD(SUBSTR(MI_RS1.NUMERO_CDP,1,10),10)                   || CHR(09)
            || RPAD(SUBSTR(MI_RS1.NUMERO_CRP,1,10),10)                   || CHR(09);

         MI_STR   :=  '' ;  
         MI_STR_2 :=  '' ;

        MI_STR    := MI_STR1   || RPAD(SUBSTR('1',1,3),3) || CHR(09) || RPAD(SUBSTR('40',1,2),2)   || CHR(09) || RPAD(SUBSTR(' ' || MI_RS1.IMPORTE1,1,30),30)     || CHR(09) ||  MI_STR2  || CHR(09) || RPAD(SUBSTR('' ,1,14),4)||  CHR(09) ||  MI_STR3 || RPAD(SUBSTR( '' ,1,24),24)   || CHR(09) ||  MI_STR4 || RPAD(SUBSTR(MI_CONSECUTIVO ,1,10),10) || CHR(09) || MI_STR5 ;      
           -- MI_STR    := MI_STR1   || SUBSTR('1',1,3) || CHR(09) || SUBSTR('40',1,2)   || CHR(09) || SUBSTR(' ' || MI_RS1.IMPORTE1,1,10)     || CHR(09) ||  MI_STR2  || CHR(09) ||  CASE WHEN LENGTH (MI_RS1.POSPRE)> 12 THEN SUBSTR(MI_RS1.POSPRE,1,14) ELSE SUBSTR(MI_RS1.POSPRE,1,10) END  ||  CHR(09) ||  MI_STR3 || SUBSTR( '' ,1,24)   || CHR(09) ||  MI_STR4 || SUBSTR('1',1,10) || CHR(09) || MI_STR5 ;      

        MI_STR_2  := MI_STR1_2 || RPAD(SUBSTR('2',1,3),3) || CHR(09) || RPAD(SUBSTR('50',1,2),2)   || CHR(09) || RPAD(SUBSTR(' ' || (TO_NUMBER(MI_RS1.IMPORTE1)*-1),1,30),30)   || CHR(09) ||  MI_STR2  || CHR(09) || RPAD(SUBSTR('BANCOS',1,14),14) ||  CHR(09) ||  MI_STR3 ||  RPAD(SUBSTR( '' ,1,24),24)  || CHR(09) ||  MI_STR4 || RPAD(SUBSTR(MI_RS1.POSICION_DOCUMENTO,1,10),10) || CHR(09) ||  MI_STR5 ;      
           --MI_STR_2  := MI_STR1_2 || SUBSTR('2',1,3) || CHR(09) || SUBSTR('50',1,2)   || CHR(09) ||SUBSTR(' ' || (TO_NUMBER(MI_RS1.IMPORTE1)*-1),1,10)   || CHR(09) ||  MI_STR2  ||  SUBSTR('BANCOS',1,14)        ||  CHR(09) ||  MI_STR3 ||  SUBSTR( MI_RS1.ELEMENTO_PEP2 ,1,24)   || CHR(09) ||  MI_STR4 ;      


        IF LENGTH(MI_RETORNO) > 0 THEN
         MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;         
        ELSE
           MI_RETORNO:=MI_STR;         
        END IF;

        MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR_2;

          MI_REGISTROS:=MI_REGISTROS+1;

    END LOOP;
    RETURN MI_RETORNO;
END FC_GENERAR_PLANO_ORDEN_PAGO;

FUNCTION    FC_GENERAR_PLANO_OP_RESERVA
(
 UN_COMPANIA         PCK_SUBTIPOS.TI_COMPANIA,
 UN_FECHAINICIAL     PCK_SUBTIPOS.TI_FECHA, 
 UN_FECHAFINAL       PCK_SUBTIPOS.TI_FECHA,  
 UN_TIPOCOMP         PCK_SUBTIPOS.TI_CLASECOMPROPPTO,
 UN_VIGENCIA         VARCHAR2,   --- TICKET7738447 MPEREZ
 UN_RESPONSABLEPPTO  PCK_SUBTIPOS.TI_TERCERO,
 UN_CPTEINICIAL      PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL,
 UN_CPTEFINAL        PCK_SUBTIPOS.TI_NUMEROCOMPROBANTEPPTAL 
)
RETURN CLOB AS 
-- PROGRAM: GERERAR LOS PLANOS  ORDEN PAGO PARA  SECRETARIA  DE HACIENDA  DE BOGOTA 
-- AUTHOR : JOHANA AGUILAR RAMIREZ
-- DATE   : DICIEMBRE 14 de 2020
-- TAR :    1000103299


MI_SESSION          NUMBER;
MI_FECHAINI         DATE :=UN_FECHAINICIAL;
MI_FECHAFIN         DATE :=UN_FECHAFINAL;
MI_COMPANIA         VARCHAR2(11):=UN_COMPANIA;
MI_ETAPA            VARCHAR2(3200);
MI_VERSION          VARCHAR2(3200):='GENERACION ARCHIVO PLANO ORDEN PAGO - VERSION 2020 RELEASE 01 ';
MI_RETORNO          CLOB:='';
MI_CONSECUTIVO     NUMBER:=0;
MI_REGISTROS       NUMBER:=0;
MI_RS1             NUMBER;
MI_RS2             NUMBER;
MI_VALOR_P           VARCHAR2(32000) := '';
MI_ANTDCT            VARCHAR2(32000) := '';
MI_STR               VARCHAR2(32000):='';
MI_STR1              VARCHAR2(32000) := '';
MI_STR2              VARCHAR2(32000) := '';
MI_STR3              VARCHAR2(32000) := '';
MI_STR4              VARCHAR2(32000) := '';
MI_STR5              VARCHAR2(32000) := '';
MI_STR_2             VARCHAR2(32000):='';
MI_STR1_2            VARCHAR2(32000) := '';
BEGIN
    MI_ETAPA:='1';
    SELECT SYS_CONTEXT('USERENV','SESSIONID') 
    INTO MI_SESSION
    FROM DUAL;

    MI_ETAPA:='2' ;   

    MI_ETAPA:='4' ;   


    MI_ETAPA:='7' ;   


-- VAMOS CON REGISTRO TIPO 1
    FOR MI_RS1 IN
           ( 
       -- Se modifica la consulta para que obtenga adecuadamente la información de la reserva - TICKET 7717223 - MPEREZ    
       SELECT PPTAL.NUMERO
          ,'1001' SOCIEDAD
          ,'' N_DOCUMENTO
          ,PPTAL.ANO EJERCICIO --- TICKET7738447 MPEREZ
          ,'OP' CLASE_DOCUMENTO
          ,TO_CHAR(SYSDATE ,'RRRRMMDD') FECHA_DOCUMENTO --- TICKET7738447 MPEREZ
          ,TO_CHAR(SYSDATE ,'RRRRMMDD') FE_CONTABILIZACION --- TICKET7738447 MPEREZ
          ,EXTRACT (MONTH FROM SYSDATE) PERIODO_CONTABLE --- TICKET7738447 MPEREZ
          ,'' REFERENCIA
          ,'ORDEN DE PAGO' TXT_CAB_DOCUMENTO -- SE CAMBIA DE VACIO A VALOR FIJO ORDEN DE PAGO
          ,'COP' MONEDA
          ,'1000' ENTIDAD_CP
          ,'' MARCA_ANULACION
          ,PPTAL.NRO_AUTORIZACION_VF NUMERO_OP
          ,'02'  TIPO_PAGO  -- SE CAMBIA DE VACIO A VALOR FIJO 02
          ,PPTAL.DESCRIPCION OBJETO --- TICKET7738447 MPEREZ
          ,'7990990000' CUENTA_MAYOR
          , DPPTAL.VALOR_DEBITO IMPORTE1
          , DPPTAL.VALOR_CREDITO IMPORTE2
           ,'' ASIGNACION
           ,'' TEXTO
           ,'1000' SOCIEDAD_CO -- SE CAMBIA DE VACIO A VALOR FIJO 1000
           ,'' CENTRO_COSTE
           ,'' CLIENTE
           ,'' PROVEEDOR
           ,'' VIA_PAGO
           ,'' CENTRO_BENEFICIO
           , DECODE(TRIM(P.CODIGO_EQUIV), '', P.CODIGO, P.CODIGO_EQUIV) POSPRE --Valido para la posiciÃ³n debito
          ,'0214-01' CENTROGESTOR
          ,'' FONDOS --'R.FUENTERECURSOS
          ,'' AREAFUNCIONAL --'0002-008' AREAFUNCIONAL
          ,PPTAL.ANO-1 PERIODO_PRESUPUESTAL
          ,NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA, UN_NOMBRE =>'PREFIJO PMR', UN_MODULO =>3, UN_FECHA_PAR=>SYSDATE ), 'PM/0214/') || DPPTAL.REFERENCIA ELEMENTO_PEP1 --TICKET7729430 MPEREZ
          ,'' DOC_PRESUPUESTARIO -- PPTAL1.NRO_GENERA
          ,'' POSICION_DOCUMENTO
          ,'214' SEGMENTO
          ,'' NUMERO_CDP
          ,LTRIM(SUBSTR(DPPTAL.CMPTE_AFECTADO,5),0) NUMERO_CRP 
          ,P.TIPOVIGENCIA TV
      FROM COMPROBANTE_PPTAL PPTAL
INNER JOIN DETALLE_COMPROBANTE_PPTAL DPPTAL
        ON PPTAL.COMPANIA = DPPTAL.COMPANIA
       AND PPTAL.ANO     = DPPTAL.ANO
       AND PPTAL.TIPO    = DPPTAL.TIPO_CPTE
       AND PPTAL.NUMERO  = DPPTAL.COMPROBANTE
INNER JOIN DETALLE_COMPROBANTE_PPTAL DPPTAL1
        ON DPPTAL.COMPANIA = DPPTAL1.COMPANIA
       AND DPPTAL.ANO = DPPTAL1.ANO
       AND DPPTAL.TIPO_CPTE_AFECT = DPPTAL1.TIPO_CPTE
       AND DPPTAL.CMPTE_AFECTADO = DPPTAL1.COMPROBANTE
INNER JOIN TIPO_COMPROBPP TC
        ON  TC.COMPANIA = DPPTAL1.COMPANIA
       AND TC.CODIGO = DPPTAL1.TIPO_CPTE
INNER JOIN PLAN_PRESUPUESTAL P
        ON DPPTAL.COMPANIA      = P.COMPANIA
       AND DPPTAL.ANO           = P.ANO
       AND DPPTAL.CUENTA = P.CODIGO
     WHERE PPTAL.COMPANIA = MI_COMPANIA
       AND DPPTAL.TIPO_CPTE_AFECT IN (UN_TIPOCOMP)
       AND PPTAL.NUMERO BETWEEN UN_CPTEINICIAL AND UN_CPTEFINAL
       AND PPTAL.FECHA  BETWEEN TO_DATE(MI_FECHAINI,'DD/MM/YYYY') AND TO_DATE(MI_FECHAFIN,'DD/MM/YYYY')
       AND P.TIPOVIGENCIA = UN_VIGENCIA   --- TICKET7738447 MPEREZ    
      ORDER BY PPTAL.FECHA,PPTAL.CMPTE_AFECTADO
            )
    LOOP
       MI_VALOR_P:= MI_RS1.NUMERO;
  --TRIM(TO_CHAR(MI_CONSECUTIVO,'000000'))
        -- EN SOLICITUD DEL TAR 1000102901 SE PIDE QUE EL EL CONSECUTIVO SIEMPRE SEA UNO 
        --IF MI_ANTDCT = NVL(MI_RS1.NUMERO,'') THEN 
        --   MI_CONSECUTIVO:=MI_CONSECUTIVO+1;
         --ELSE
           MI_CONSECUTIVO:=1;
        --END IF;
         MI_ANTDCT := NVL(MI_RS1.NUMERO,'');
         MI_STR1 := '';
         MI_STR2 := '';
         MI_STR3 := '';
         MI_STR4 := '';
         MI_STR5 := '';
         MI_STR1_2 := '';
        MI_STR1:=
             CASE WHEN MI_CONSECUTIVO =  1 THEN RPAD(SUBSTR(MI_RS1.SOCIEDAD,1,4),4)                       ELSE '' END             || CHR(09)
          || CASE WHEN MI_CONSECUTIVO =  1 THEN RPAD(SUBSTR(MI_RS1.N_DOCUMENTO,1,10),10)                  ELSE '' END            || CHR(09)
          || CASE WHEN MI_CONSECUTIVO =  1 THEN RPAD(SUBSTR(MI_RS1.EJERCICIO,1,4),4)                      ELSE '' END            || CHR(09)
          || CASE WHEN MI_CONSECUTIVO =  1 THEN RPAD(SUBSTR(MI_RS1.CLASE_DOCUMENTO,1,2),2)                ELSE '' END            || CHR(09)  
          || CASE WHEN MI_CONSECUTIVO =  1 THEN RPAD(SUBSTR(MI_RS1.FECHA_DOCUMENTO,1,10),10)              ELSE '' END            || CHR(09)  
          || CASE WHEN MI_CONSECUTIVO =  1 THEN RPAD(SUBSTR(MI_RS1.FE_CONTABILIZACION,1,10),10)           ELSE '' END            || CHR(09)  
          || CASE WHEN MI_CONSECUTIVO =  1 THEN RPAD(SUBSTR(MI_RS1.PERIODO_CONTABLE,1,2),2)               ELSE '' END            || CHR(09)  
          || CASE WHEN MI_CONSECUTIVO =  1 THEN RPAD(SUBSTR(MI_RS1.REFERENCIA,1,10),10)                   ELSE '' END            || CHR(09)  
          || CASE WHEN MI_CONSECUTIVO =  1 THEN RPAD(SUBSTR(MI_RS1.TXT_CAB_DOCUMENTO,1,20),20)            ELSE '' END            || CHR(09)  
          || CASE WHEN MI_CONSECUTIVO =  1 THEN RPAD(SUBSTR(MI_RS1.MONEDA,1,5),5)                         ELSE '' END            || CHR(09)  
          || CASE WHEN MI_CONSECUTIVO =  1 THEN RPAD(SUBSTR(MI_RS1.ENTIDAD_CP,1,10),10)                   ELSE '' END            || CHR(09)  
          || CASE WHEN MI_CONSECUTIVO =  1 THEN RPAD(SUBSTR(MI_RS1.MARCA_ANULACION,1,10),10)              ELSE '' END            || CHR(09)  
          || CASE WHEN MI_CONSECUTIVO =  1 THEN RPAD(SUBSTR(MI_RS1.NUMERO_OP,1,10),10)                    ELSE '' END            || CHR(09)  
          || CASE WHEN MI_CONSECUTIVO =  1 THEN RPAD(SUBSTR(MI_RS1.TIPO_PAGO,1,10),10)                    ELSE '' END            || CHR(09)  
          || CASE WHEN MI_CONSECUTIVO =  1 THEN RPAD(SUBSTR(REPLACE(MI_RS1.OBJETO, CHR(10), ' '),1,2000),2000)  ELSE '' END      || CHR(09); 


        MI_STR1_2 := 
               MI_STR1     
            || RPAD(SUBSTR('7990970000',1,10),10)                        || CHR(09);  

        MI_STR1 := 
               MI_STR1 
            || RPAD(SUBSTR(MI_RS1.CUENTA_MAYOR,1,10),10)                 || CHR(09);


        MI_STR2 := 
               RPAD(SUBSTR(MI_RS1.ASIGNACION,1,10),10)                   || CHR(09)
            || RPAD(SUBSTR(MI_RS1.TEXTO,1,10),10)                        || CHR(09)
            || RPAD(SUBSTR(MI_RS1.SOCIEDAD_CO,1,10),10)                  || CHR(09)
            || RPAD(SUBSTR(MI_RS1.CENTRO_COSTE,1,10),10)                 || CHR(09)
            || RPAD(SUBSTR(MI_RS1.CLIENTE,1,10),10)                      || CHR(09)
            || RPAD(SUBSTR(MI_RS1.PROVEEDOR,1,10),10)                    || CHR(09)
            || RPAD(SUBSTR(MI_RS1.VIA_PAGO,1,10),10)                     || CHR(09)
            || RPAD(SUBSTR(MI_RS1.CENTRO_BENEFICIO,1,10),10)             ;

            MI_STR3 :=
               RPAD(SUBSTR(MI_RS1.CENTROGESTOR,1,16),16)                 || CHR(09)
            || RPAD(SUBSTR(MI_RS1.FONDOS,1,10),10)                       || CHR(09)
            || RPAD(SUBSTR(MI_RS1.AREAFUNCIONAL,1,10),10)                || CHR(09)
            || RPAD(SUBSTR(MI_RS1.PERIODO_PRESUPUESTAL,1,4),4)           || CHR(09) ;


            MI_STR4 :=  
               RPAD(SUBSTR(MI_RS1.DOC_PRESUPUESTARIO,1,10),10)           || CHR(09) ;


            MI_STR5 := 
               RPAD(SUBSTR(MI_RS1.SEGMENTO,1,3),3)                       || CHR(09)
            || RPAD(SUBSTR(MI_RS1.NUMERO_CDP,1,10),10)                   || CHR(09)
            || RPAD(SUBSTR(MI_RS1.NUMERO_CRP,1,10),10)                   || CHR(09) ;

         MI_STR   :=  '' ;  
         MI_STR_2 :=  '' ;

           MI_STR    := MI_STR1   || RPAD(SUBSTR('1',1,3),3) || CHR(09) || RPAD(SUBSTR('40',1,2),2)   || CHR(09) || RPAD(SUBSTR(' ' || MI_RS1.IMPORTE1,1,30),30)     || CHR(09) ||  MI_STR2  || CHR(09) || RPAD(SUBSTR('' ,1,14),14)||  CHR(09) ||  MI_STR3 || RPAD(SUBSTR( '' ,1,24),24)   || CHR(09) ||  MI_STR4 || RPAD(SUBSTR(MI_CONSECUTIVO ,1,10),10) || CHR(09) || MI_STR5 ;      
           -- MI_STR    := MI_STR1   || SUBSTR('1',1,3) || CHR(09) || SUBSTR('40',1,2)   || CHR(09) || SUBSTR(' ' || MI_RS1.IMPORTE1,1,10)     || CHR(09) ||  MI_STR2  || CHR(09) ||  CASE WHEN LENGTH (MI_RS1.POSPRE)> 12 THEN SUBSTR(MI_RS1.POSPRE,1,14) ELSE SUBSTR(MI_RS1.POSPRE,1,10) END  ||  CHR(09) ||  MI_STR3 || SUBSTR( '' ,1,24)   || CHR(09) ||  MI_STR4 || SUBSTR('1',1,10) || CHR(09) || MI_STR5 ;      
           MI_STR_2  := MI_STR1_2 || RPAD(SUBSTR('2',1,3),3) || CHR(09) || RPAD(SUBSTR('50',1,2),2)   || CHR(09) || RPAD(SUBSTR(' ' || (TO_NUMBER(MI_RS1.IMPORTE1)*-1),1,30),30)   || CHR(09) ||  MI_STR2  || CHR(09) || RPAD(SUBSTR('BANCOS',1,14),14) ||  CHR(09) ||  MI_STR3 ||  RPAD(SUBSTR( '' ,1,24),24)  || CHR(09) ||  MI_STR4 || RPAD(SUBSTR(MI_RS1.POSICION_DOCUMENTO,1,10),10) || CHR(09) ||  MI_STR5 ;      
           --MI_STR_2  := MI_STR1_2 || SUBSTR('2',1,3) || CHR(09) || SUBSTR('50',1,2)   || CHR(09) ||SUBSTR(' ' || (TO_NUMBER(MI_RS1.IMPORTE1)*-1),1,10)   || CHR(09) ||  MI_STR2  ||  SUBSTR('BANCOS',1,14)        ||  CHR(09) ||  MI_STR3 ||  SUBSTR( MI_RS1.ELEMENTO_PEP2 ,1,24)   || CHR(09) ||  MI_STR4 ;      

        IF LENGTH(MI_RETORNO) > 0 THEN
          MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR;
        ELSE
          MI_RETORNO:=MI_STR;
        END IF;
        MI_RETORNO:=MI_RETORNO||CHR(13)||CHR(10)||MI_STR_2;

         MI_REGISTROS:=MI_REGISTROS+1;


    END LOOP;

    RETURN MI_RETORNO;
END FC_GENERAR_PLANO_OP_RESERVA;

PROCEDURE PR_INSERTAPPTO
/*
      NAME              : PR_INSERTAPPTO 

      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JEIMMY CAROLINA ROJAS GUERRERO
      DATE MIGRADOR     : 11/02/2021
      TIME              : 
      SOURCE MODULE     : 
      DESCRIPTION       :     
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME MODIFIED     : 
      MODIFICATIONS     : Se crea proceso para insertar registro en tabla ORDENDECOMPRAPPTO

      PARAMETERS        : UN_COMPANIA      =>  Compañia de ingreso a la aplicación    
                          UN_CLASEORDEN    =>  Tipo de Contrato del formulario
                          UN_NUMERO        =>  Numero de contrato del formulario
                          UN_CLASEDISP     =>  Clase de disponibilidad del formulario
                          UN_NUMERODISPSEL =>  numero de disponibilidad  o rubro seleccionado
                          UN_FECHASELEC    =>  Fecha del rubro seleccionado
                          UN_TERCERO       =>  Tercero del formulario
                          UN_SUCURSAL      =>  Sucursal del formulario
                          UN_USUARIO       =>  Usuario que ingreso a la aplicación
    @NAME:   insertaPpto
    @METHOD: POST     
    */
(
     UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA
    ,UN_CLASEORDEN    IN COMPROBANTE_PPTAL.TIPOCONTRATO%TYPE
    ,UN_NUMERO        IN COMPROBANTE_PPTAL.NUMEROCONTRATO%TYPE
    ,UN_CLASEDISP     IN ORDENDECOMPRAPPTO.TIPOPPTO%TYPE 
    ,UN_NUMERODISPSEL IN ORDENDECOMPRAPPTO.NUMEROPPTO%TYPE 
    ,UN_TERCERO       IN PCK_SUBTIPOS.TI_TERCERO 
    ,UN_SUCURSAL      IN PCK_SUBTIPOS.TI_SUCURSAL  
    ,UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO

)
AS 
    MI_TABLA      PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS     PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES    PCK_SUBTIPOS.TI_VALORES;
    MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_NUMERO     ORDENDECOMPRA.NUMERO%TYPE;
    MI_STRSQL     PCK_SUBTIPOS.TI_STRSQL;
BEGIN   
    BEGIN  
      MI_STRSQL := 'SELECT DISTINCT NUMERO 
                    FROM ORDENDECOMPRAPPTO
                    WHERE COMPANIA    = '''||UN_COMPANIA||'''
                      AND CLASEORDEN  = '''||UN_CLASEORDEN||'''
                      AND NUMERO      =   '||UN_NUMERO||'
                      AND TIPOPPTO    = '''||UN_CLASEDISP||'''
                      AND NUMEROPPTO  =   '||UN_NUMERODISPSEL||'
                      AND TERCERO     = '''||UN_TERCERO||''' 
                      AND SUCURSAL    = '''||UN_SUCURSAL||'''';
    EXECUTE IMMEDIATE MI_STRSQL INTO MI_NUMERO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_TABLA:='ORDENDECOMPRAPPTO';
    <<RECORRECPTES>>
    FOR MI_RS IN (
          SELECT DISTINCT ORDENDECOMPRAPPTO.COMPANIA,
                         ORDENDECOMPRAPPTO.RUBRO,
                         ORDENDECOMPRAPPTO.ANOPPTO
                FROM ORDENDECOMPRAPPTO
                INNER JOIN TIPO_COMPROBPP
                ON ORDENDECOMPRAPPTO.COMPANIA = TIPO_COMPROBPP.COMPANIA
                AND ORDENDECOMPRAPPTO.TIPOPPTO =TIPO_COMPROBPP.CODIGO
                WHERE ORDENDECOMPRAPPTO.COMPANIA = UN_COMPANIA
                AND  TIPO_COMPROBPP.CLASE = 'DIS'
                AND ORDENDECOMPRAPPTO.NUMERO   = UN_NUMERO)
    LOOP  
        MI_CAMPOS:= ' COMPANIA
                     ,CLASEORDEN
                     ,NUMERO
                     ,ANOPPTO
                     ,TIPOPPTO
                     ,NUMEROPPTO
                     ,RUBRO
                     ,TERCERO
                     ,SUCURSAL
                     ,DATE_CREATED
                     ,CREATED_BY';
        MI_VALORES:= ''''||UN_COMPANIA||'''
                     ,'''||UN_CLASEORDEN||'''
                     ,'''||UN_NUMERO||'''
                     ,'||MI_RS.ANOPPTO||'
                     ,'''||UN_CLASEDISP||'''
                     ,'||UN_NUMERODISPSEL||'
                     ,'''||MI_RS.RUBRO||'''
                     ,'''||UN_TERCERO||'''
                     ,'''||UN_SUCURSAL||'''
                     ,SYSDATE
                     ,'''||UN_USUARIO||'''';
        BEGIN 
            BEGIN 
                PCK_DATOS.GL_RTA:=PCK_DATOS.FC_ACME(
                                            UN_TABLA => MI_TABLA
                                           ,UN_ACCION => 'I'
                                           ,UN_CAMPOS => MI_CAMPOS
                                           ,UN_VALORES => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                MI_REEMPLAZOS(0).CLAVE:='UN_NUMERODISPSEL';
                MI_REEMPLAZOS(0).VALOR:=UN_NUMERODISPSEL;
                MI_REEMPLAZOS(1).CLAVE:='UN_NUMERO';
                MI_REEMPLAZOS(1).VALOR:=UN_NUMERO;
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(    
                                UN_EXC_COD    => SQLCODE
                               ,UN_ERROR_COD  => PCK_ERRORES.ERRR_PRESUPUESTO_INSERTAPPTO
                               ,UN_TABLAERROR => MI_TABLA
                               ,UN_REEMPLAZOS => MI_REEMPLAZOS
                               );
        END;
    END LOOP RECORRECPTES; 
    END;
END PR_INSERTAPPTO;


PROCEDURE PR_CARGAR_COMP_DETALLE_PPTAL
/*
    NAME              : PR_CARGAR_COMP_DETALLE_PPTAL
    AUTHOR MIGRACION  : GUSTAVO ANDRÉS FIGUEREDO ??VILA
    DATE MIGRADOR     : 08/06/20121                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PROCEDIMIENTO QUE CARGA LOS DATOS DE COMPROBANTE Y DETALLE_COMPROBANTE PPTAL DE UN EXCEL

    @NAME:    cargarComprobanteDetallePptal
    @METHOD:  POST
    */
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENAPLAN  IN CLOB,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)  
AS 
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_CAMPOSCUIPO        PCK_SUBTIPOS.TI_CAMPOS;
MI_CUIPOVALORES       PCK_SUBTIPOS.TI_VALORES;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_ANIO                     PCK_SUBTIPOS.TI_ANIO;
MI_PARAMETRO          VARCHAR2(4000 CHAR);
MI_FECHA              DATE;                        
MI_NUMERO             NUMBER := 0;
MI_NATURALEZA        PLAN_PRESUPUESTAL.NATURALEZA%TYPE;
MI_CUENTA            DETALLE_COMPROBANTE_PPTAL.CUENTA%TYPE;
MI_COD_SECTOR                DETALLE_COMPROBANTE_PPTAL.SECTOR%TYPE; 
MI_COD_SECTORCOMP            DETALLE_COMPROBANTE_PPTAL.SECTOR%TYPE;         
MI_COD_PROGRAMA              DETALLE_COMPROBANTE_PPTAL.PROGRAMA%TYPE;   
MI_COD_PROGRAMACOMP          DETALLE_COMPROBANTE_PPTAL.PROGRAMA%TYPE;         
MI_COD_SUBPROGRAMA           DETALLE_COMPROBANTE_PPTAL.SUBPROGRAMA%TYPE;
MI_COD_SUBPROGRAMACOMP       DETALLE_COMPROBANTE_PPTAL.SUBPROGRAMA%TYPE;         
MI_COD_CODIGOUNIDADEJE       DETALLE_COMPROBANTE_PPTAL.CODIGOUNIDADEJE%TYPE;
MI_COD_CODIGOUNIDADEJECOMP   DETALLE_COMPROBANTE_PPTAL.CODIGOUNIDADEJE%TYPE;         
MI_COD_CODIGOCCPETREGA       DETALLE_COMPROBANTE_PPTAL.CODIGOCCPETREGA%TYPE;         
MI_COD_CODIGOCCPETREGACOMP   DETALLE_COMPROBANTE_PPTAL.CODIGOCCPETREGA%TYPE;         
MI_COD_CODIGO_CCPET          DETALLE_COMPROBANTE_PPTAL.CODIGO_CCPET%TYPE;         
MI_COD_CODIGO_CCPETCOMP      DETALLE_COMPROBANTE_PPTAL.CODIGO_CCPET%TYPE;         
MI_COD_SITUACION_FONDOS      DETALLE_COMPROBANTE_PPTAL.SITUACION_FONDOS%TYPE;         
MI_COD_POLITICA_PUBLICA      DETALLE_COMPROBANTE_PPTAL.POLITICA_PUBLICA%TYPE;         
MI_COD_POLITICA_PUBLICACOMP  DETALLE_COMPROBANTE_PPTAL.POLITICA_PUBLICA%TYPE;         
MI_COD_DETALLE_SECTORIAL     DETALLE_COMPROBANTE_PPTAL.DETALLE_SECTORIAL%TYPE;     
MI_COD_DETALLE_SECTORIALCOMP DETALLE_COMPROBANTE_PPTAL.DETALLE_SECTORIAL%TYPE;     
MI_COD_COD_PROD_CUIPO        DETALLE_COMPROBANTE_PPTAL.COD_PROD_CUIPO%TYPE;         
MI_COD_CODIGO_CPC            DETALLE_COMPROBANTE_PPTAL.CODIGO_CPC%TYPE;         
MI_COD_CODIGO_CPCCOMP        DETALLE_COMPROBANTE_PPTAL.CODIGO_CPC%TYPE;         
MI_COD_CODIGO_BPIN           DETALLE_COMPROBANTE_PPTAL.CODIGO_BPIN%TYPE;
MI_COD_CODIGO_BPINCOMP       DETALLE_COMPROBANTE_PPTAL.CODIGO_BPIN%TYPE; 
MI_COD_FUENTE_CUIPO          DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO%TYPE;
MI_COD_FUENTE_CUIPOCOMP      DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO%TYPE;
MI_COD_TIPO_CPTE             DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE%TYPE;  
MI_COD_COMPROBANTE           DETALLE_COMPROBANTE_PPTAL.COMPROBANTE%TYPE;  
MI_COD_CONSECUTIVO           DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO%TYPE;
MI_CONDICION                 PCK_SUBTIPOS.TI_CONDICION;  
MI_COD_FECHA                 DATE;
MI_RETORNO                   CLOB := '';
MI_RETORNOAUX                CLOB := '';




BEGIN

  MI_CAMPOSCUIPO               := '';
  MI_CUIPOVALORES              := '';
  MI_ANIO := EXTRACT(YEAR FROM SYSDATE);
  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAPLAN,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);


  <<CREAR_COMPROBANTE>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
       --INSERTA COMPROBANTE_PPTAL                                              


    MI_ANIO :=  EXTRACT(YEAR FROM  TO_DATE(MI_DATOS_COLUMNAS(4),'DD/MM/YYYY')  );
    
    MI_CUENTA                   := MI_DATOS_COLUMNAS(8);
    MI_COD_TIPO_CPTE            := MI_DATOS_COLUMNAS(2);
    MI_COD_COMPROBANTE          := MI_DATOS_COLUMNAS(3);
    MI_COD_CONSECUTIVO          := MI_DATOS_COLUMNAS(12);
    MI_COD_SECTOR               := MI_DATOS_COLUMNAS(14);
    MI_COD_PROGRAMA             := MI_DATOS_COLUMNAS(15);
    MI_COD_SUBPROGRAMA          := MI_DATOS_COLUMNAS(16);
    MI_COD_COD_PROD_CUIPO       := MI_DATOS_COLUMNAS(17);
    MI_COD_CODIGO_BPIN          := MI_DATOS_COLUMNAS(18);
    MI_COD_CODIGO_CCPET         := MI_DATOS_COLUMNAS(19);
    MI_COD_CODIGO_CPC           := MI_DATOS_COLUMNAS(20);
    MI_COD_CODIGOUNIDADEJE      := MI_DATOS_COLUMNAS(21);
    MI_COD_FUENTE_CUIPO         := MI_DATOS_COLUMNAS(22);
    MI_COD_CODIGOCCPETREGA      := MI_DATOS_COLUMNAS(23);
    MI_COD_POLITICA_PUBLICA     := MI_DATOS_COLUMNAS(24);
    MI_COD_DETALLE_SECTORIAL    := MI_DATOS_COLUMNAS(25);

    SELECT NATURALEZA
    INTO MI_NATURALEZA
    FROM PLAN_PRESUPUESTAL P
    WHERE  P.COMPANIA = UN_COMPANIA
       AND P.ANO      = MI_ANIO
       AND P.CODIGO   = MI_CUENTA;

    BEGIN
        SELECT   H.IDPADRE 
        INTO     MI_COD_PROGRAMACOMP 
        FROM     TIPOCLASIFICADOR T     INNER JOIN TIPOCLASIFICADORESHIJO H 
          ON T.COMPANIA = H.COMPANIA                                            
          AND T.ANO = H.ANO                                            
          AND T.ID = H.IDPADRE 
        INNER JOIN TIPOCLASIFICADOR TCH                                         
          ON H.COMPANIA =  TCH.COMPANIA                                        
          AND H.ANO =  TCH.ANO                                         
          AND H.IDHIJO =  TCH.ID  
        WHERE     T.COMPANIA = UN_COMPANIA     AND T.ANO = MI_ANIO     
          AND INSTR(          CONCAT(CONCAT(',',(
              SELECT     CLASEPADRE FROM     CLASECLASIFICADOR WHERE     COMPANIA = UN_COMPANIA     AND ANO = MI_ANIO     AND CODIGO = '004'     AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         APLICACION         ELSE         APLICACIONINGRESOS         END     ) = 2     
          AND CLASEPADRE IS NOT NULL )),',') , CONCAT(             CONCAT(                 ',', T.CLASECLASIFICADOR             ), ','         )     ) > 0    
          AND T.CODIGO IS NOT NULL
          AND TCH.CODIGO =  MI_COD_COD_PROD_CUIPO
        GROUP BY H.IDPADRE;
    EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
      MI_COD_PROGRAMACOMP := '';                
    END;


    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_SUBPROGRAMACOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANIO     
            AND CC.CODIGO = '003'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_SUBPROGRAMA
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
            MI_COD_SUBPROGRAMACOMP := '';                
    END;
 


    BEGIN
        SELECT     H.IDPADRE 
        INTO      MI_COD_SECTORCOMP
        FROM     TIPOCLASIFICADOR T     
        INNER JOIN TIPOCLASIFICADORESHIJO H 
            ON T.COMPANIA = H.COMPANIA                                            
            AND T.ANO = H.ANO                                            
            AND T.ID = H.IDPADRE 
        INNER JOIN TIPOCLASIFICADOR TCH                                         
            ON H.COMPANIA =  TCH.COMPANIA                                        
            AND H.ANO =  TCH.ANO                                         
            AND H.IDHIJO =  TCH.ID  
        WHERE     T.COMPANIA = UN_COMPANIA     
            AND T.ANO = MI_ANIO     
            AND INSTR(          CONCAT(CONCAT(',',(
               SELECT     CLASEPADRE FROM     CLASECLASIFICADOR 
               WHERE     COMPANIA = UN_COMPANIA   
                AND ANO = MI_ANIO     
                AND CODIGO = '002'     
                AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         APLICACION         ELSE         APLICACIONINGRESOS         END     ) = 2     
                AND CLASEPADRE IS NOT NULL )),',') , CONCAT(             CONCAT(                 ',', T.CLASECLASIFICADOR             ), ','         )     ) > 0      
            AND H.IDHIJO =  MI_COD_PROGRAMACOMP     
            AND T.CODIGO IS NOT NULL
        GROUP BY H.IDPADRE;
    EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
            MI_COD_SECTORCOMP := '';                
    END;   

    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_CODIGO_BPINCOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANIO     
            AND CC.CODIGO = '005'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_CODIGO_BPIN
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
            MI_COD_CODIGO_BPINCOMP := '';                
    END;

    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_CODIGO_CCPETCOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANIO     
            AND CC.CODIGO = '006'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_CODIGO_CCPET
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
            MI_COD_CODIGO_CCPETCOMP := '';                
    END;

    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_CODIGO_CPCCOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANIO     
            AND CC.CODIGO = '007'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_CODIGO_CPC
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
            MI_COD_CODIGO_CPCCOMP := '';                
    END;

    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_CODIGOUNIDADEJECOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANIO     
            AND CC.CODIGO = '008'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_CODIGOUNIDADEJE
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
            MI_COD_CODIGOUNIDADEJECOMP := '';                
    END;

    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_FUENTE_CUIPOCOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANIO     
            AND CC.CODIGO = '009'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_FUENTE_CUIPO
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
            MI_COD_FUENTE_CUIPOCOMP := '';                
    END;

    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_CODIGOCCPETREGACOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANIO     
            AND CC.CODIGO = '010'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_CODIGOCCPETREGA
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
            MI_COD_CODIGOCCPETREGACOMP := '';                
    END;

    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_POLITICA_PUBLICACOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANIO     
            AND CC.CODIGO = '011'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_POLITICA_PUBLICA
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
            MI_COD_POLITICA_PUBLICACOMP := '';                
    END;
     
    BEGIN
        SELECT  TC.CODIGO 
        INTO MI_COD_DETALLE_SECTORIALCOMP
        FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
            ON  CC.COMPANIA =  TC.COMPANIA
            AND CC.ANO      =  TC.ANO
            AND CC.CODIGO   =  TC.CLASECLASIFICADOR
        WHERE     CC.COMPANIA = UN_COMPANIA     
            AND CC.ANO = MI_ANIO     
            AND CC.CODIGO = '012'     
            AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
            AND TC.CODIGO =  MI_COD_DETALLE_SECTORIAL
        GROUP BY TC.CODIGO  ;
    EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
            MI_COD_DETALLE_SECTORIALCOMP := '';                
    END;

--CLASECLASIFICADOR -> APLICACION 0;No Aplica;1;Rubro;2;Detall

    MI_CAMPOSCUIPO :=  '';
    MI_CUIPOVALORES :=  '';
    IF '001' || MI_COD_SECTOR = MI_COD_SECTORCOMP  AND  MI_COD_SECTOR IS NOT NULL THEN 
      MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ' ,SECTOR             ';
      MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ''' || MI_COD_SECTOR          ||''' ' ;
    ELSIF MI_COD_SECTOR = 'NoDato' OR MI_COD_SECTOR IS NULL OR MI_COD_SECTOR = 'null' THEN
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Sector para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || ' 001 No esta configurado como detalle  o el codigo ' || MI_COD_SECTOR || ' No existe  en TIPOCLASIFICADOR '||CHR(13);
    END IF;
    IF '002' || MI_COD_PROGRAMA =  MI_COD_PROGRAMACOMP  AND  MI_COD_PROGRAMA IS NOT NULL THEN 
      MI_CAMPOSCUIPO := MI_CAMPOSCUIPO  || ' ,PROGRAMA           ';
      MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_PROGRAMA        ||''' ' ;  
    ELSIF MI_COD_PROGRAMA = 'NoDato' OR MI_COD_PROGRAMA IS NULL OR MI_COD_PROGRAMA = 'null' THEN
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Programa para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '002 No esta configurado como detalle  o el codigo ' || MI_COD_PROGRAMA || ' No existe  en TIPOCLASIFICADOR '||CHR(13);               
    END IF;
    IF MI_COD_SUBPROGRAMA =  MI_COD_SUBPROGRAMACOMP AND  MI_COD_SUBPROGRAMA IS NOT NULL THEN         
      MI_CAMPOSCUIPO := MI_CAMPOSCUIPO  || ' ,SUBPROGRAMA        ';
      MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_SUBPROGRAMA     ||''' ' ;       
    ELSIF MI_COD_SUBPROGRAMA = 'NoDato' OR MI_COD_SUBPROGRAMA IS NULL OR MI_COD_SUBPROGRAMA = 'null' THEN
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador SubPrograma para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '003 No esta configurado como detalle  o el codigo ' || MI_COD_SUBPROGRAMA || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                              
    END IF;
    IF  MI_COD_PROGRAMACOMP  IS NOT NULL THEN 
      MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ' ,COD_PROD_CUIPO     ';
      MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_COD_PROD_CUIPO  ||''' ' ;   
    ELSIF MI_COD_COD_PROD_CUIPO = 'NoDato' OR MI_COD_COD_PROD_CUIPO IS NULL OR MI_COD_COD_PROD_CUIPO = 'null' THEN
       MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Producto para la cuenta: ' || MI_CUENTA||CHR(13);  
    ELSE
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '004 No esta configurado como detalle  o el codigo ' || MI_COD_COD_PROD_CUIPO || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                                 
    END IF;
    IF MI_COD_CODIGO_BPIN =  MI_COD_CODIGO_BPINCOMP  AND  MI_COD_CODIGO_BPIN IS NOT NULL THEN 
      MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ', CODIGO_BPIN       ';
      MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_CODIGO_BPIN      ||''' ' ; 
    ELSIF MI_COD_CODIGO_BPIN = 'NoDato' OR MI_COD_CODIGO_BPIN IS NULL OR MI_COD_CODIGO_BPIN = 'null' THEN
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Bpin para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '005 No esta configurado como detalle  o el codigo ' || MI_COD_CODIGO_BPIN || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                                  
    END IF;
    IF MI_COD_CODIGO_CCPET =  MI_COD_CODIGO_CCPETCOMP  AND  MI_COD_CODIGO_CCPET IS NOT NULL THEN 
      MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ', CODIGO_CCPET      ';
      MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_CODIGO_CCPET     ||''' ' ;   
    ELSIF MI_COD_CODIGO_CCPET = 'NoDato' OR MI_COD_CODIGO_CCPET IS NULL OR MI_COD_CODIGO_CCPET = 'null' THEN
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Codigo CCPET para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '006 No esta configurado como detalle  o el codigo ' || MI_COD_CODIGO_CCPET || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                                
    END IF;
    IF MI_COD_CODIGO_CPC =  MI_COD_CODIGO_CPCCOMP  AND  MI_COD_CODIGO_CPC IS NOT NULL THEN 
      MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ' ,CODIGO_CPC        ';
      MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_CODIGO_CPC   ||''' ' ;       
    ELSIF MI_COD_CODIGO_CPC = 'NoDato' OR MI_COD_CODIGO_CPC IS NULL OR MI_COD_CODIGO_CPC = 'null' THEN
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Codigo CPC para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '007 No esta configurado como detalle  o el codigo ' || MI_COD_CODIGO_CPC || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                              
    END IF;
    IF MI_COD_CODIGOUNIDADEJE =  MI_COD_CODIGOUNIDADEJECOMP  AND  MI_COD_CODIGOUNIDADEJE IS NOT NULL THEN 
      MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ', CODIGOUNIDADEJE   ';
      MI_CUIPOVALORES:= MI_CUIPOVALORES || ',  ''' || MI_COD_CODIGOUNIDADEJE ||''' ' ; 
    ELSIF MI_COD_CODIGOUNIDADEJE = 'NoDato' OR MI_COD_CODIGOUNIDADEJE IS NULL OR MI_COD_CODIGOUNIDADEJE = 'null' THEN
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Unidad Ejecutora para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '008 No esta configurado como detalle  o el codigo ' || MI_COD_CODIGOUNIDADEJE || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                                    
    END IF;
    IF MI_COD_FUENTE_CUIPO =  MI_COD_FUENTE_CUIPOCOMP  AND  MI_COD_FUENTE_CUIPO IS NOT NULL THEN 
      MI_CAMPOSCUIPO := MI_CAMPOSCUIPO ||  ' ,FUENTE_CUIPO      ';
      MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_FUENTE_CUIPO    ||''' ' ;  
    ELSIF MI_COD_FUENTE_CUIPO = 'NoDato' OR MI_COD_FUENTE_CUIPO IS NULL OR MI_COD_FUENTE_CUIPO = 'null' THEN
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Fuente para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '009 No esta configurado como detalle  o el codigo ' || MI_COD_FUENTE_CUIPO || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                             
    END IF;
    IF  MI_COD_CODIGOCCPETREGA =  MI_COD_CODIGOCCPETREGACOMP  AND  MI_COD_CODIGOCCPETREGA IS NOT NULL THEN 
      MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ' ,CODIGOCCPETREGA   ';
      MI_CUIPOVALORES:= MI_CUIPOVALORES || '  ,''' || MI_COD_CODIGOCCPETREGA ||''' ' ;  
    ELSIF MI_COD_CODIGOCCPETREGA = 'NoDato' OR MI_COD_CODIGOCCPETREGA IS NULL OR MI_COD_CODIGOCCPETREGA = 'null' THEN
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Codigo CCPET Regalias para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE 
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '010 No esta configurado como detalle  o el codigo ' || MI_COD_CODIGOCCPETREGA || ' No existe  en TIPOCLASIFICADOR '||CHR(13);               
    END IF;
    IF MI_COD_POLITICA_PUBLICA =  MI_COD_POLITICA_PUBLICACOMP AND  MI_COD_POLITICA_PUBLICA IS NOT NULL THEN 
      MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ' ,POLITICA_PUBLICA   ';
      MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_POLITICA_PUBLICA    ||''' ' ;
    ELSIF MI_COD_POLITICA_PUBLICA = 'NoDato' OR MI_COD_POLITICA_PUBLICA IS NULL OR MI_COD_POLITICA_PUBLICA = 'null'THEN
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Politica Publica para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '011 No esta configurado como detalle  o el codigo ' || MI_COD_POLITICA_PUBLICA || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                               
    END IF;
    IF  MI_COD_DETALLE_SECTORIAL =  MI_COD_DETALLE_SECTORIALCOMP AND  MI_COD_DETALLE_SECTORIAL IS NOT NULL THEN 
      MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ' ,DETALLE_SECTORIAL ';
      MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_DETALLE_SECTORIAL ||''' ' ;  
    ELSIF MI_COD_DETALLE_SECTORIAL = 'NoDato' OR MI_COD_DETALLE_SECTORIAL IS NULL OR MI_COD_DETALLE_SECTORIAL = 'null' THEN
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador detalle Sectorial para la cuenta: ' || MI_CUENTA||CHR(13);
    ELSE
      MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '012 No esta configurado como detalle  o el codigo ' || MI_COD_DETALLE_SECTORIAL || ' No existe  en TIPOCLASIFICADOR '||CHR(13);               
    END IF;
    IF MI_RETORNOAUX IS NOT NULL THEN 
     MI_RETORNO := MI_RETORNO || MI_RETORNOAUX || CHR(10) || CHR(13);
    END IF;
    MI_RETORNOAUX :=  '';

   MI_CAMPOS := ' COMPANIA
                  ,ANO
                  ,TIPO
                  ,NUMERO
                  ,FECHA
                  ,DESTINO
                  ,DEPENDENCIA
                  ,DESCRIPCION
                  ,DEBITO
                  ,CREDITO                  
                  ,FUENTE_RECURSO
                  ,REFERENCIA
                  ,FECHA_VCN_DOC ';

    MI_CAMPOS :=  MI_CAMPOS|| '
                  ,CREATED_BY
                  ,DATE_CREATED';

      --<TAR:7700126  FECHA:21/12/2021 AUTOR:CP>
        MI_PARAMETRO     := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA, UN_NOMBRE =>'MANEJA VENCIMIENTO ULTIMO DIA DEL AÑO', UN_MODULO =>PCK_DATOS.MODULOCONTABILIDAD, UN_FECHA_PAR=>SYSDATE ), 'NO');
        IF MI_PARAMETRO = 'SI' THEN
            MI_FECHA   :=TO_DATE('31/12/'|| MI_ANIO ,'DD/MM/YYYY');
        ELSE
            MI_FECHA:= TO_DATE(MI_DATOS_COLUMNAS(4),'DD/MM/YYYY');
            MI_FECHA:= MI_FECHA +NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'DIAS VENCIMIENTO',PCK_DATOS.FC_MODULOCONTABILIDAD,SYSDATE),0);
        END IF;
      --</TAR>

      MI_VALORES := ''''|| MI_DATOS_COLUMNAS(1) ||'''
                  ,'||  MI_ANIO ||'
                  ,'''|| MI_DATOS_COLUMNAS(2) ||'''
                  ,'|| MI_DATOS_COLUMNAS(3) ||'
                  ,'''|| MI_DATOS_COLUMNAS(4) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(5) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(6) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(7) ||'''
                  ,  '|| MI_DATOS_COLUMNAS(9) ||'
                  ,  '|| MI_DATOS_COLUMNAS(10) ||'
                  ,'''|| MI_DATOS_COLUMNAS(11) ||'''
                  ,  '|| MI_DATOS_COLUMNAS(13) ||' ';
      MI_VALORES := MI_VALORES 
                 ||',TO_DATE(''' || TO_CHAR(MI_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ' ||'
                  ,'''|| UN_USUARIO ||'''
                  ,SYSDATE';
    BEGIN
        IF  NVL(MI_NUMERO,0) != MI_DATOS_COLUMNAS(3) THEN
          BEGIN
        
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'COMPROBANTE_PPTAL'
                                               ,UN_ACCION  => 'I'
                                               ,UN_CAMPOS  => MI_CAMPOS
                                               ,UN_VALORES => MI_VALORES);
                                
           
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
          RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
         END;
        END IF;
         MI_NUMERO := MI_DATOS_COLUMNAS(3);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
        MI_MSGERROR(1).CLAVE := 'TIPCPTE';
        MI_MSGERROR(1).VALOR := MI_DATOS_COLUMNAS(2);
        MI_MSGERROR(2).CLAVE := 'NROCPTE';
        MI_MSGERROR(2).VALOR := MI_DATOS_COLUMNAS(3);
        MI_MSGERROR(3).CLAVE := 'DESCRIPCION';
        MI_MSGERROR(3).VALOR := MI_DATOS_COLUMNAS(7);
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_CREARCPTERED, UN_TABLAERROR => 'COMPROBANTE_PPTAL', UN_REEMPLAZOS => MI_MSGERROR );
    END;    

   --INSERTA DETALLE_PRESUPUESTAL_PPTAL

         MI_CAMPOS := 'COMPANIA
                  ,ANO
                  ,TIPO_CPTE
                  ,COMPROBANTE
                  ,FECHA
                  ,DEPENDENCIA
                  ,DESCRIPCION
                  ,CUENTA
                  ,VALOR_DEBITO
                  ,VALOR_CREDITO                  
                  ,FUENTE_RECURSO
                  ,CONSECUTIVO
                  ,CONSECUTIVOPPTO
                  ,REFERENCIA
                  ';
                  MI_CAMPOS := MI_CAMPOS|| MI_CAMPOSCUIPO||'
                  ,CREATED_BY
                  ,DATE_CREATED';
      MI_ANIO :=  EXTRACT(YEAR FROM  TO_DATE(MI_DATOS_COLUMNAS(4),'DD/MM/YYYY')  );
      MI_VALORES := ''''|| MI_DATOS_COLUMNAS(1) ||'''
                    ,'||  MI_ANIO ||'
                  ,'''|| MI_DATOS_COLUMNAS(2) ||'''
                  ,'|| MI_DATOS_COLUMNAS(3) ||'
                  ,'''|| MI_DATOS_COLUMNAS(4) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(6) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(7) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(8) ||'''
                  ,  '|| MI_DATOS_COLUMNAS(9) ||'
                  ,  '|| MI_DATOS_COLUMNAS(10) ||'
                  ,'''|| MI_DATOS_COLUMNAS(11) ||'''
                  ,  '|| MI_DATOS_COLUMNAS(12) ||'
                  ,0
                  ,'''|| MI_DATOS_COLUMNAS(13) ||'''';
                  MI_VALORES:=MI_VALoRES||MI_CUIPOVALORES ||
                  ','''|| UN_USUARIO ||'''
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
        MI_MSGERROR(1).CLAVE := 'TIPCPTE';
        MI_MSGERROR(1).VALOR := MI_DATOS_COLUMNAS(2);
        MI_MSGERROR(2).CLAVE := 'NROCPTE';
        MI_MSGERROR(2).VALOR := MI_DATOS_COLUMNAS(3);
        MI_MSGERROR(3).CLAVE := 'CONSECUTIVO';
        MI_MSGERROR(3).VALOR := MI_DATOS_COLUMNAS(13);
        MI_MSGERROR(4).CLAVE := 'CUENTA';
        MI_MSGERROR(4).VALOR := MI_DATOS_COLUMNAS(8);
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_CREARDETREDUCCION, UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL', UN_REEMPLAZOS => MI_MSGERROR );
    END;
  
  
  END LOOP CREAR_COMPROBANTE;
 --RETURN MI_RETORNO;
END PR_CARGAR_COMP_DETALLE_PPTAL;

PROCEDURE PR_CARGAR_PMR_FUENTE 
/*
    NAME              : PR_CARGAR_PMR_FUENTE
    AUTHOR MIGRACION  : BRAYAN SEBASTIAN CARDENAS AGUILAR
    DATE MIGRADOR     :                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     :
    DESCRIPTION       : 

    @NAME: cargarPmrFuente
    @METHOD:  PUT
    */
( 
  UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENA       IN CLOB,
  UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
)  
AS 
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
 MI_RTA              PCK_SUBTIPOS.TI_RTA_ACME;
BEGIN

  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENA,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

  <<CARGAR_PMR_FUENTE>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                 UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
   BEGIN   
   BEGIN

                MI_TABLA  := 'PLAN_PRESUPUESTAL';

                MI_CAMPOS :=  'PMR1 = ''' || REPLACE(MI_DATOS_COLUMNAS(4),' ','') || ''', 
                               PMR2 = ''' || REPLACE(MI_DATOS_COLUMNAS(5),' ','') || ''',
                               FUENTERECURSOS = ''' || REPLACE(MI_DATOS_COLUMNAS(6),' ','') || ''',
                               MODIFIED_BY = ''' || UN_USUARIO || ''',
                               DATE_MODIFIED = SYSDATE' ;

                MI_CONDICION := 'COMPANIA           =   '''|| UN_COMPANIA ||'''
                                 AND ANO =   '''|| MI_DATOS_COLUMNAS(1) || '''
                                 AND CODIGO         =   '''|| MI_DATOS_COLUMNAS(2) ||'''';

                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => MI_TABLA,
                                             UN_ACCION     => 'M', 
                                             UN_CAMPOS     => MI_CAMPOS, 
                                             UN_CONDICION  => MI_CONDICION);



         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END;

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
             MI_MSGERROR(1).CLAVE := 'RUBRO';
             MI_MSGERROR(1).VALOR := MI_DATOS_COLUMNAS(2);
             MI_MSGERROR(2).CLAVE := 'ANIO';
             MI_MSGERROR(2).VALOR := MI_DATOS_COLUMNAS(1);
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD   => SQLCODE,
                  UN_REEMPLAZOS => MI_MSGERROR,
                  UN_ERROR_COD => PCK_ERRORES.ERR_CARGAR_PMR_FUENTE
                );
        END;

  END LOOP CARGAR_PMR_FUENTE;
END PR_CARGAR_PMR_FUENTE;



PROCEDURE PR_CARGAR_TIPO_CLASIFICADORES
/*
    NAME              : PR_CARGAR_TIPO_CLASIFICADORES
    AUTHOR MIGRACION  : CAMILO ANDRÃ‰S PEREZ DUEÃ‘AS
    DATE MIGRADOR     : 12/01/2022                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PROCEDIMIENTO QUE CARGA TIPOS CLASIFICADORES DE UN EXCEL

    @NAME:    PR_CARGAR_TIPO_CLASIFICADORES
    @METHOD:  POST
    */
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENAPLAN  IN CLOB,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)  
AS 
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_ANIO               PCK_SUBTIPOS.TI_ANIO;
MI_EXISTE             NUMBER := 0;
MI_USING              VARCHAR2(32000);
MI_MERGEENLACE        VARCHAR2(32000);
MI_MERGEEXISTE        VARCHAR2(32000);
MI_MERGENOEXIS        VARCHAR2(32000);
MI_VALORCCPET         VARCHAR2(32000);

BEGIN

  MI_ANIO := EXTRACT(YEAR FROM SYSDATE);
  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAPLAN,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
   MI_USING := ' SELECT 1 FROM DUAL ' ;
  <<CREAR_TIPOCLASIFICADORES>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_TABLA := 'TIPOCLASIFICADOR';
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
      IF MI_DATOS_COLUMNAS.EXISTS(10) THEN
        MI_VALORCCPET := MI_DATOS_COLUMNAS(10);
    ELSE
        MI_VALORCCPET := '0';
    END IF; 
       --INSERTA TIPO_CLASIFICADORES                                              

       MI_MERGEENLACE :=     '  TABLA.COMPANIA            = '''|| MI_DATOS_COLUMNAS(1)                          || '''
                            AND TABLA.ANO                 =   '|| MI_DATOS_COLUMNAS(2)                          || '
                            AND TABLA.ID                  = '''|| MI_DATOS_COLUMNAS(3) ||  MI_DATOS_COLUMNAS(4) || '''';

       MI_MERGEEXISTE := ' UPDATE SET                   
                              NOMBRE                = ''' || MI_DATOS_COLUMNAS(5)      || '''
                             ,SECCIONESADI          =   ' || MI_DATOS_COLUMNAS(6)      || '
                             ,MOVIMIENTO            =   ' || MI_DATOS_COLUMNAS(7)      || '
                             ,REGALIAS              =   ' || MI_DATOS_COLUMNAS(8)      || '
                             ,APLICAINGRESOS       =   ' || MI_DATOS_COLUMNAS(9)      || '
                             ,OBLIGACPCDANE        =   ' || MI_VALORCCPET      || '
                             ';



      MI_MERGENOEXIS := 'INSERT( COMPANIA
                  ,ANO
                  ,CLASECLASIFICADOR
                  ,CODIGO
                  ,NOMBRE
                  ,SECCIONESADI
                  ,MOVIMIENTO
                  ,REGALIAS
                  ,APLICAINGRESOS
                  ,OBLIGACPCDANE
                  ,CREATED_BY
                                ,DATE_CREATED) 
                       VALUES(
                           '''|| MI_DATOS_COLUMNAS(1) ||'''
                    ,'|| MI_DATOS_COLUMNAS(2) ||'
                  ,'''|| MI_DATOS_COLUMNAS(3) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(4) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(5) ||'''
                          ,  '|| MI_DATOS_COLUMNAS(6) ||'
                          ,  '|| MI_DATOS_COLUMNAS(7) ||'
                  ,  '|| MI_DATOS_COLUMNAS(8) ||'
                  ,  '|| MI_DATOS_COLUMNAS(9) ||'
                  ,  '|| MI_VALORCCPET ||'
                  ,'''|| UN_USUARIO ||'''
                          ,SYSDATE)';
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (MI_TABLA, 'IM',NULL, NULL, NULL, NULL, MI_USING, MI_MERGEENLACE, MI_MERGEEXISTE, MI_MERGENOEXIS);
  END LOOP CREAR_TIPOCLASIFICADORES;
END PR_CARGAR_TIPO_CLASIFICADORES;


FUNCTION FC_ACT_CLASIFICADORESPPTAL
     /*  
        NAME              : PR_ACTUALIZARCLASIFICADORESPPTAL
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 26/04/2022
        TIME              : 14:18 PM
        SOURCE MODULE     : 
        MODIFIER          : GERMAN DAVID ROJAS G
        DATE MODIFIED     : 26/06/2023
        TIME              :
        DESCRIPTION       : Permite actualizar los clasificares en la detalle presupuestal 
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:ACTUALIZARCLASIFICADORESPPTAL
*/    
(  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA 
  ,UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO
  ,UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN NUMBER AS
    MI_MODELO VARCHAR2(3);
BEGIN
    -- Ticket#7733294: Permite actualizar los clasificadores de configuración de Arbol a Horizontal.
        PCK_PRESUPUESTO_COM4.PR_ACT_CLASI_ARBOL
                                                     (UN_COMPANIA => UN_COMPANIA 
                                                     ,UN_ANIO     => UN_ANIO
                                                     ,UN_USUARIO  => UN_USUARIO);
    
    -- Ticket#7733294: Funcion que actauliza los clasificadores de horizontal a detalle de los comprobantes.
        PCK_DATOS.GL_RTA := PCK_PRESUPUESTO_COM4.FC_ACT_CLASI_AUX
                                                   (UN_COMPANIA => UN_COMPANIA 
                                                   ,UN_ANIO     => UN_ANIO
                                                   ,UN_USUARIO  => UN_USUARIO);
    -- CC_2315: Permite actualizar la información de las apropiaciones iniciales.                                               
        PCK_PRESUPUESTO_COM4.PR_ACT_ARBOL_APROINI (UN_COMPANIA => UN_COMPANIA
                                                 ,UN_ANIO     => UN_ANIO);
                                                 
        RETURN PCK_DATOS.GL_RTA;                                                 

END FC_ACT_CLASIFICADORESPPTAL;

PROCEDURE PR_ACT_CLASI_ARBOL
     /*  
        NAME              : PR_ACT_CLASI_ARBOL
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ
        DATE MIGRADOR     : 26/04/2024
        TIME              : 09:55 AM
        SOURCE MODULE     : 
        MODIFIER          : GERMAN DAVID ROJAS G
        DATE MODIFIED     : 26/06/2023
        TIME              :
        DESCRIPTION       : Ticket#7733294: Permite actualizar los clasificares que estan en Arbol a configuración horizontal.
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:FC_ACT_CLASI_ARBOL
*/    
(  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA 
  ,UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO  
  ,UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)
AS
    MI_CAMPOORIGEN    VARCHAR2(100);
    MI_CAMPODESTINO   VARCHAR2(100);
    MI_RTA_ACME       NUMBER(20,0);
    MI_CONDICION      VARCHAR2(500);
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGENOEXISTE  PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;

BEGIN

      FOR RS IN (
                  SELECT COMPANIA, ANO, CODIGO, NATURALEZA 
                  FROM PLAN_PRESUPUESTAL PL
                  WHERE PL.COMPANIA  = UN_COMPANIA  
                  AND PL.ANO =  UN_ANIO
                  AND (PL.MOVIMIENTO + PL.MAN_CEN_CTO + PL.MAN_AUX_TER + PL.MAN_AUX_GEN + PL.MAN_AUX_REF + PL.MAN_AUX_FUE) <> 0
                  ORDER BY PL.CODIGO)

      LOOP

      BEGIN
        FOR RS1 IN (
                    SELECT PL.TIPOCLASIFICADOR , CLASECLASIFICADOR   
                    FROM PLAN_PRESUPUESTAL PL   
                    WHERE PL.COMPANIA  = RS.COMPANIA   
                    AND PL.ANO =  RS.ANO 
                    AND  SUBSTR(RS.CODIGO,1,LENGTH(PL.CODIGO)) =  PL.CODIGO   
                    AND PL.CLASECLASIFICADOR IS NOT NULL)
          
            LOOP
          
                CASE RS1.CLASECLASIFICADOR
                WHEN '001' THEN
                      MI_CAMPOS := 'SECTOR = ''' || RS1.TIPOCLASIFICADOR || '''';
                WHEN '002' THEN
                      MI_CAMPOS := 'PROGRAMA = ''' || RS1.TIPOCLASIFICADOR || '''';
                WHEN '003' THEN
                      MI_CAMPOS := 'SUBPROGRAMA = ''' || RS1.TIPOCLASIFICADOR || '''';
                WHEN '004' THEN
                      MI_CAMPOS := 'CODIGOPRODUCTO = ''' || RS1.TIPOCLASIFICADOR || '''';
                WHEN '005' THEN
                      MI_CAMPOS := 'CODIGOBPIN = ''' || RS1.TIPOCLASIFICADOR || '''';
                WHEN '006' THEN
                      MI_CAMPOS := 'CODIGOCCPET = ''' || RS1.TIPOCLASIFICADOR || '''';
                WHEN '007' THEN
                      MI_CAMPOS := 'CODIGOCPCDANE = ''' || RS1.TIPOCLASIFICADOR || '''';
                WHEN '008' THEN
                      MI_CAMPOS := 'CODIGOUNIDADEJE = ''' || RS1.TIPOCLASIFICADOR || '''';
                WHEN '009' THEN
                      MI_CAMPOS := 'CODIGOFUENTE = ''' || RS1.TIPOCLASIFICADOR || '''';
                WHEN '010' THEN
                      MI_CAMPOS := 'CODIGOCCPETREGA = ''' || RS1.TIPOCLASIFICADOR || '''';
                WHEN '011' THEN
                      MI_CAMPOS := 'POLITCA_PUBLICA_CUIPO = ''' || RS1.TIPOCLASIFICADOR || '''';
                WHEN '012' THEN
                      MI_CAMPOS := 'DETALLE_SECTORIAL = ''' || RS1.TIPOCLASIFICADOR || '''';
                WHEN '013' THEN
                      MI_CAMPOS := 'TIPO_RECURSO = ''' || RS1.TIPOCLASIFICADOR || '''';
                ELSE
                    MI_CAMPOS := ' ';
                END CASE;
            
                MI_CONDICION := 'COMPANIA     = ''' || RS.COMPANIA    || '''
                                 AND ANO      =   ' || RS.ANO         || '
                                 AND CODIGO   = ''' || RS.CODIGO      || ''' 
                                 ';

                IF MI_CAMPOS <> ' '  THEN
              
                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PLAN_PRESUPUESTAL'
                                                        ,UN_ACCION    => 'M'
                                                        ,UN_CAMPOS    => MI_CAMPOS
                                                        ,UN_CONDICION => MI_CONDICION);
                END IF;
            END LOOP;
        END;
    END LOOP;
    
END PR_ACT_CLASI_ARBOL;

FUNCTION FC_ACT_CLASI_AUX
     /*  
        NAME              : PR_ACTUALIZARCLASIFICADORESPPTAL
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : CAMILO ANDRES PEREZ DUEÃ‘AS
        DATE MIGRADOR     : 15/03/2021
        TIME              : 09:15 AM
        SOURCE MODULE     : 
        MODIFIER          : GERMAN DAVID ROJAS G
        DATE MODIFIED     : 26/06/2023
        TIME              :
        DESCRIPTION       : Ticket#7733294: Permite actualizar los clasificares en la detalle presupuestal 
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:  actualizarClasificadoresPptal
*/    
(  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA 
  ,UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO
  ,UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN NUMBER AS
    MI_SALIDA VARCHAR2(100);
    MI_EGR               PCK_SUBTIPOS.TI_DOBLE             := 0 ;
    MI_SECTOR            PCK_SUBTIPOS.TI_SECTOR            := '';
    MI_PROGRAMA          PCK_SUBTIPOS.TI_PROGRAMA          := '';
    MI_SUBPROGRAMA       PCK_SUBTIPOS.TI_SUBPROGRAMA       := '';
    MI_COD_PROD_CUIPO    PCK_SUBTIPOS.TI_COD_PROD_CUIPO    := '';
    MI_CODIGO_BPIN       PCK_SUBTIPOS.TI_CODIGO_BPIN       := '';
    MI_CODIGO_CCPET      PCK_SUBTIPOS.TI_CODIGO_CCPET      := '';
    MI_CODIGO_CPC        PCK_SUBTIPOS.TI_CODIGO_CPC        := '';
    MI_CODIGOUNIDADEJE   PCK_SUBTIPOS.TI_CODIGOUNIDADEJE   := '';
    MI_FUENTE_CUIPO      PCK_SUBTIPOS.TI_FUENTE_CUIPO      := '';
    MI_FUENTE_CUIPOCUIPO PCK_SUBTIPOS.TI_FUENTE_CUIPO      := '';
    MI_CODIGOCCPETREGA   PCK_SUBTIPOS.TI_CODIGOCCPETREGA   := '';
    MI_POLITICA_PUB      DETALLE_COMPROBANTE_PPTAL.POLITICA_PUBLICA%TYPE;
    MI_DETALLE_SEC       DETALLE_COMPROBANTE_PPTAL.DETALLE_SECTORIAL%TYPE;
    MI_TIPORECURSO       DETALLE_COMPROBANTE_PPTAL.RECURSO_SGR%TYPE;

    -- VARIABLES PARA EL TIPO DE APLICAION GASTOS
    MI_APLI_SECTOR            PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_PROGRAMA          PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_SUBPROGRAMA       PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_COD_PROD_CUIPO    PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_CODIGO_BPIN       PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_CODIGO_CCPET      PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_CODIGO_CPC        PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_CODIGOUNIDADEJE   PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_CODIGO_CCPET_REG  PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_FUENTE_CUIPO      PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_POLITICA          PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_DETALLE_SEC       PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_TIPO_RECURSO      PCK_SUBTIPOS.TI_DOBLE        := 0;
    -- VARIABLES PARA EL TIPO DE APLICACION INGRESOS
    MI_APLI_ING_SECTOR            PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_ING_PROGRAMA          PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_ING_SUBPROGRAMA       PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_ING_COD_PROD_CUIPO    PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_ING_CODIGO_BPIN       PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_ING_CODIGO_CCPET      PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_ING_CODIGO_CPC        PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_ING_CODIGOUNIDADEJE   PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_ING_CODIGO_CCPET_REG  PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_ING_FUENTE_CUIPO      PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_ING_POLITICA          PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_ING_DETALLE_SEC       PCK_SUBTIPOS.TI_DOBLE        := 0;
    MI_APLI_ING_TIPO_RECURSO      PCK_SUBTIPOS.TI_DOBLE        := 0;

    MI_PARAMETRO   VARCHAR2(4000 CHAR);
    MI_DATOS_FILA  PCK_SYSMAN_UTL.T_SPLIT;
    MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
    MI_MSGERROR    PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN
    
    BEGIN
       SELECT APLICACION, APLICACIONINGRESOS
       INTO  MI_APLI_SECTOR, MI_APLI_ING_SECTOR
       FROM CLASECLASIFICADOR
       WHERE COMPANIA = UN_COMPANIA
        AND ANO       = UN_ANIO
        AND CODIGO    =  '001';
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_APLI_SECTOR := 0;
      MI_APLI_ING_SECTOR := 0;
    END;

    BEGIN
       SELECT APLICACION, APLICACIONINGRESOS
       INTO  MI_APLI_PROGRAMA, MI_APLI_ING_PROGRAMA
       FROM CLASECLASIFICADOR
       WHERE COMPANIA = UN_COMPANIA
        AND ANO       = UN_ANIO
        AND CODIGO    =  '002';
    EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_APLI_PROGRAMA := 0;    
       MI_APLI_ING_PROGRAMA := 0;
    END;

    BEGIN
       SELECT APLICACION, APLICACIONINGRESOS
       INTO  MI_APLI_SUBPROGRAMA, MI_APLI_ING_SUBPROGRAMA
       FROM CLASECLASIFICADOR
       WHERE COMPANIA = UN_COMPANIA
        AND ANO       = UN_ANIO
        AND CODIGO    =  '003';
    EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_APLI_SUBPROGRAMA := 0; 
       MI_APLI_ING_SUBPROGRAMA := 0;
    END;

    BEGIN
       SELECT APLICACION, APLICACIONINGRESOS
       INTO  MI_APLI_COD_PROD_CUIPO, MI_APLI_ING_COD_PROD_CUIPO
       FROM CLASECLASIFICADOR
       WHERE COMPANIA = UN_COMPANIA
        AND ANO       = UN_ANIO
        AND CODIGO    =  '004';
    EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_APLI_COD_PROD_CUIPO := 0;
       MI_APLI_ING_COD_PROD_CUIPO := 0;
    END;

    BEGIN
       SELECT APLICACION, APLICACIONINGRESOS
       INTO MI_APLI_CODIGO_BPIN, MI_APLI_ING_CODIGO_BPIN 
       FROM CLASECLASIFICADOR
       WHERE COMPANIA = UN_COMPANIA
        AND ANO       = UN_ANIO
        AND CODIGO    =  '005';
    EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_APLI_CODIGO_BPIN := 0; 
       MI_APLI_ING_CODIGO_BPIN := 0;
    END;

    BEGIN
       SELECT APLICACION, APLICACIONINGRESOS
       INTO  MI_APLI_CODIGO_CCPET, MI_APLI_ING_CODIGO_CCPET
       FROM CLASECLASIFICADOR
       WHERE COMPANIA = UN_COMPANIA
        AND ANO       = UN_ANIO
        AND CODIGO    =  '006';
    EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_APLI_CODIGO_CCPET := 0;  
       MI_APLI_ING_CODIGO_CCPET := 0;
    END;

    BEGIN
       SELECT APLICACION, APLICACIONINGRESOS
       INTO  MI_APLI_CODIGO_CPC, MI_APLI_ING_CODIGO_CPC
       FROM CLASECLASIFICADOR
       WHERE COMPANIA = UN_COMPANIA
        AND ANO       = UN_ANIO
        AND CODIGO    =  '007';
    EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_APLI_CODIGO_CPC := 0; 
       MI_APLI_ING_CODIGO_CPC := 0;
    END;

    BEGIN
       SELECT APLICACION, APLICACIONINGRESOS
       INTO  MI_APLI_CODIGOUNIDADEJE, MI_APLI_ING_CODIGOUNIDADEJE
       FROM CLASECLASIFICADOR
       WHERE COMPANIA = UN_COMPANIA
        AND ANO       = UN_ANIO
        AND CODIGO    =  '008';
    EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_APLI_CODIGOUNIDADEJE := 0;
       MI_APLI_ING_CODIGOUNIDADEJE := 0;
    END;

    BEGIN
       SELECT APLICACION, APLICACIONINGRESOS
       INTO  MI_APLI_FUENTE_CUIPO, MI_APLI_ING_FUENTE_CUIPO
       FROM CLASECLASIFICADOR
       WHERE COMPANIA = UN_COMPANIA
        AND ANO       = UN_ANIO
        AND CODIGO    =  '009';
    EXCEPTION WHEN NO_DATA_FOUND THEN
       MI_APLI_FUENTE_CUIPO := 0;
       MI_APLI_ING_FUENTE_CUIPO := 0;
    END;

    BEGIN
       SELECT APLICACION, APLICACIONINGRESOS
       INTO  MI_APLI_CODIGO_CCPET_REG, MI_APLI_ING_CODIGO_CCPET_REG
       FROM CLASECLASIFICADOR
       WHERE COMPANIA = UN_COMPANIA
        AND ANO       = UN_ANIO
        AND CODIGO    =  '010';
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_APLI_CODIGO_CCPET := 0;  
        MI_APLI_ING_CODIGO_CCPET_REG := 0;
    END;

    BEGIN
       SELECT APLICACION, APLICACIONINGRESOS
       INTO  MI_APLI_POLITICA, MI_APLI_ING_POLITICA
       FROM CLASECLASIFICADOR
       WHERE COMPANIA = UN_COMPANIA
        AND ANO       = UN_ANIO
        AND CODIGO    =  '011';
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_APLI_POLITICA := 0;
        MI_APLI_ING_POLITICA := 0;
    END;

    BEGIN
       SELECT APLICACION, APLICACIONINGRESOS
       INTO  MI_APLI_DETALLE_SEC, MI_APLI_ING_DETALLE_SEC
       FROM CLASECLASIFICADOR
       WHERE COMPANIA = UN_COMPANIA
        AND ANO       = UN_ANIO
        AND CODIGO    =  '012';
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_APLI_DETALLE_SEC := 0;
        MI_APLI_ING_DETALLE_SEC := 0;
    END;
    
    BEGIN
       SELECT APLICACION, APLICACIONINGRESOS
       INTO  MI_APLI_TIPO_RECURSO, MI_APLI_ING_TIPO_RECURSO
       FROM CLASECLASIFICADOR
       WHERE COMPANIA = UN_COMPANIA
        AND ANO       = UN_ANIO
        AND CODIGO    =  '013';
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_APLI_TIPO_RECURSO := 0;
        MI_APLI_ING_TIPO_RECURSO := 0;
    END;

    --CLASECLASIFICADOR -> APLICACION 0 No Aplica;
    --1 Rubro
    --2 Detalle

    <<VERIFICACUENTA>>
    FOR RS_VERIF_CUENTAS IN 
    (
        SELECT CODIGO, NATURALEZA
        FROM PLAN_PRESUPUESTAL PP 
        WHERE PP.COMPANIA = UN_COMPANIA
          AND PP.ANO  = UN_ANIO
          AND (PP.MOVIMIENTO<>0             
            OR PP.MAN_AUX_FUE<>0            
            OR PP.MAN_AUX_GEN<>0            
            OR PP.MAN_AUX_REF<>0           
            OR PP.MAN_AUX_TER<>0            
            OR PP.MAN_CEN_CTO<>0)
        ORDER BY CODIGO
    )
    LOOP
        MI_SECTOR           := '';
        MI_PROGRAMA         := '';
        MI_SUBPROGRAMA      := '';
        MI_COD_PROD_CUIPO   := '';
        MI_CODIGO_BPIN      := '';
        MI_CODIGO_CCPET     := '';
        MI_CODIGO_CPC       := '';
        MI_CODIGOUNIDADEJE  := '';
        MI_FUENTE_CUIPO     := '';
        MI_CODIGOCCPETREGA  := '';
        --INI JM CC620 08/01/2024
        MI_POLITICA_PUB     := '';
        MI_DETALLE_SEC      := '';
        MI_TIPORECURSO      := '';
        --FIN JM CC620 08/01/2024
        <<TIPOCLASIFICA>>
        FOR RS_TIPOCLASIFICA IN 
        (
          SELECT PL.SECTOR, PL.PROGRAMA, PL.SUBPROGRAMA, PL.CODIGOPRODUCTO, PL.CODIGOBPIN, PL.CODIGOCCPET, PL.CODIGOCPCDANE, PL.CODIGOUNIDADEJE,
            PL.CODIGOFUENTE, PL.CODIGOCCPETREGA, PL.POLITCA_PUBLICA_CUIPO, PL.DETALLE_SECTORIAL , PL.CONSITUACIONFONDOS, TIPO_RECURSO
          FROM PLAN_PRESUPUESTAL PL   
          WHERE PL.COMPANIA  = UN_COMPANIA   
            AND PL.ANO       =  UN_ANIO   
            AND PL.CODIGO = RS_VERIF_CUENTAS.CODIGO
        )LOOP

        IF RS_VERIF_CUENTAS.NATURALEZA = 'D' THEN 

                IF RS_TIPOCLASIFICA.SECTOR IS NOT NULL AND MI_APLI_SECTOR = 1 THEN 
                  MI_SECTOR            := RS_TIPOCLASIFICA.SECTOR;
                END IF ;
                IF RS_TIPOCLASIFICA.PROGRAMA IS NOT NULL AND MI_APLI_PROGRAMA =  1  THEN 
                  MI_PROGRAMA          := RS_TIPOCLASIFICA.PROGRAMA;
                END IF ;
                IF RS_TIPOCLASIFICA.SUBPROGRAMA IS NOT NULL AND MI_APLI_SUBPROGRAMA = 1  THEN 
                  MI_SUBPROGRAMA       := RS_TIPOCLASIFICA.SUBPROGRAMA;
                END IF ;
                IF RS_TIPOCLASIFICA.CODIGOPRODUCTO IS NOT NULL AND MI_APLI_COD_PROD_CUIPO =  1  THEN 
                  MI_COD_PROD_CUIPO    := RS_TIPOCLASIFICA.CODIGOPRODUCTO;
                END IF ;
                IF RS_TIPOCLASIFICA.CODIGOBPIN IS NOT NULL AND MI_APLI_CODIGO_BPIN =  1  THEN 
                  MI_CODIGO_BPIN       := RS_TIPOCLASIFICA.CODIGOBPIN;
                END IF ;
                IF RS_TIPOCLASIFICA.CODIGOCCPET IS NOT NULL AND MI_APLI_CODIGO_CCPET  =  1  THEN 
                  MI_CODIGO_CCPET      := RS_TIPOCLASIFICA.CODIGOCCPET;
                END IF ;
                IF RS_TIPOCLASIFICA.CODIGOCPCDANE IS NOT NULL AND MI_APLI_CODIGO_CPC =  1 THEN 
                  MI_CODIGO_CPC        := RS_TIPOCLASIFICA.CODIGOCPCDANE;
                END IF ;
                IF RS_TIPOCLASIFICA.CODIGOUNIDADEJE IS NOT NULL AND MI_APLI_CODIGOUNIDADEJE =  1  THEN 
                  MI_CODIGOUNIDADEJE   := RS_TIPOCLASIFICA.CODIGOUNIDADEJE;
                END IF ;
                IF RS_TIPOCLASIFICA.CODIGOFUENTE IS NOT NULL AND MI_APLI_FUENTE_CUIPO =  1 THEN 
                  MI_FUENTE_CUIPO      := RS_TIPOCLASIFICA.CODIGOFUENTE;
                END IF ;
                IF RS_TIPOCLASIFICA.CODIGOCCPETREGA IS NOT NULL AND MI_APLI_CODIGO_CCPET_REG  =  1 THEN 
                  MI_CODIGOCCPETREGA   := RS_TIPOCLASIFICA.CODIGOCCPETREGA;
                END IF ;
                IF RS_TIPOCLASIFICA.POLITCA_PUBLICA_CUIPO IS NOT NULL AND MI_APLI_POLITICA  =  1  THEN 
                  MI_POLITICA_PUB      := RS_TIPOCLASIFICA.POLITCA_PUBLICA_CUIPO;
                END IF ;
                IF RS_TIPOCLASIFICA.DETALLE_SECTORIAL IS NOT NULL AND MI_APLI_DETALLE_SEC  =  1  THEN 
                  MI_DETALLE_SEC       := RS_TIPOCLASIFICA.DETALLE_SECTORIAL;
                END IF ;
                IF RS_TIPOCLASIFICA.TIPO_RECURSO IS NOT NULL AND MI_APLI_TIPO_RECURSO  =  1  THEN 
                  MI_TIPORECURSO       := RS_TIPOCLASIFICA.TIPO_RECURSO;
                END IF ;
        
                MI_CAMPOS :=  '';
                IF MI_APLI_SECTOR = 1  THEN 
                  MI_CAMPOS := MI_CAMPOS || ' SECTOR           = ''' || MI_SECTOR          ||''',' ;
                END IF;
                IF MI_APLI_PROGRAMA =  1  THEN 
                  MI_CAMPOS := MI_CAMPOS || ' PROGRAMA         = ''' || MI_PROGRAMA        ||''',' ;                 
                END IF;
                IF MI_APLI_SUBPROGRAMA =  1  THEN         
                  MI_CAMPOS := MI_CAMPOS || ' SUBPROGRAMA      = ''' || MI_SUBPROGRAMA     ||''',' ;                      
                END IF;
                IF MI_APLI_COD_PROD_CUIPO =  1  THEN 
                  MI_CAMPOS := MI_CAMPOS || ' COD_PROD_CUIPO   = ''' || MI_COD_PROD_CUIPO  ||''',' ;                     
                END IF;
                IF MI_APLI_CODIGO_BPIN =  1  THEN 
                  MI_CAMPOS := MI_CAMPOS || ' CODIGO_BPIN      = ''' || MI_CODIGO_BPIN      ||''',' ;                    
                END IF;
                IF MI_APLI_CODIGO_CCPET =  1  THEN 
                  MI_CAMPOS := MI_CAMPOS || ' CODIGO_CCPET     = ''' || MI_CODIGO_CCPET     ||''',' ;                    
                END IF;
                IF MI_APLI_CODIGO_CPC =  1  THEN 
                  MI_CAMPOS := MI_CAMPOS || ' CODIGO_CPC       = ''' || MI_CODIGO_CPC   ||''',' ;                      
                END IF;
                IF MI_APLI_CODIGOUNIDADEJE =  1  THEN 
                  MI_CAMPOS := MI_CAMPOS || ' CODIGOUNIDADEJE  = ''' || MI_CODIGOUNIDADEJE ||''',' ;                      
                END IF;
                IF MI_APLI_FUENTE_CUIPO =  1  THEN 
                  MI_CAMPOS := MI_CAMPOS || ' FUENTE_CUIPO     = ''' || MI_FUENTE_CUIPO    ||''',' ;                
                END IF;
                IF  MI_APLI_CODIGO_CCPET_REG  =  1  THEN 
                  MI_CAMPOS := MI_CAMPOS || ' CODIGOCCPETREGA  = ''' || MI_CODIGOCCPETREGA ||''',' ;  
                END IF;
                IF MI_APLI_POLITICA =  1 THEN 
                  MI_CAMPOS := MI_CAMPOS || ' POLITICA_PUBLICA = ''' || MI_POLITICA_PUB    ||''',' ;                
                END IF;
                IF  MI_APLI_DETALLE_SEC =  1  THEN 
                  MI_CAMPOS := MI_CAMPOS || ' DETALLE_SECTORIAL  = ''' || MI_DETALLE_SEC ||''',' ;  
                END IF;
                IF  MI_APLI_DETALLE_SEC =  1  THEN 
                  MI_CAMPOS := MI_CAMPOS || ' RECURSO_SGR  = ''' || MI_TIPORECURSO ||''',' ;  
                END IF;

                MI_CONDICION := 'COMPANIA       = ''' || UN_COMPANIA             || '''
                                 AND ANO        =   ' || UN_ANIO                 || '
                                 AND CUENTA     = ''' || RS_VERIF_CUENTAS.CODIGO || ''' ';

                IF  MI_CAMPOS  IS NOT NULL THEN 
                    MI_CAMPOS := SUBSTR(MI_CAMPOS,1,LENGTH(MI_CAMPOS) -1 ) ;   
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                          ,UN_ACCION    => 'M'
                                                          ,UN_CAMPOS    => MI_CAMPOS
                                                          ,UN_CONDICION => MI_CONDICION);      
                END IF;

        ELSE

                IF RS_TIPOCLASIFICA.SECTOR IS NOT NULL AND MI_APLI_ING_SECTOR = 1 THEN 
                  MI_SECTOR            := RS_TIPOCLASIFICA.SECTOR;
                END IF ;
                IF RS_TIPOCLASIFICA.PROGRAMA IS NOT NULL AND MI_APLI_ING_PROGRAMA =  1 THEN 
                  MI_PROGRAMA          := RS_TIPOCLASIFICA.PROGRAMA;
                END IF ;
                IF RS_TIPOCLASIFICA.SUBPROGRAMA IS NOT NULL AND MI_APLI_ING_SUBPROGRAMA = 1 THEN 
                  MI_SUBPROGRAMA       := RS_TIPOCLASIFICA.SUBPROGRAMA;
                END IF ;
                IF RS_TIPOCLASIFICA.CODIGOPRODUCTO IS NOT NULL AND MI_APLI_ING_COD_PROD_CUIPO =  1 THEN 
                  MI_COD_PROD_CUIPO    := RS_TIPOCLASIFICA.CODIGOPRODUCTO;
                END IF ;
                IF RS_TIPOCLASIFICA.CODIGOBPIN IS NOT NULL AND MI_APLI_ING_CODIGO_BPIN =  1 THEN 
                  MI_CODIGO_BPIN       := RS_TIPOCLASIFICA.CODIGOBPIN;
                END IF ;
                IF RS_TIPOCLASIFICA.CODIGOCCPET IS NOT NULL AND MI_APLI_ING_CODIGO_CCPET  =  1 THEN 
                  MI_CODIGO_CCPET      := RS_TIPOCLASIFICA.CODIGOCCPET;
                END IF ;
                IF RS_TIPOCLASIFICA.CODIGOCPCDANE IS NOT NULL AND MI_APLI_ING_CODIGO_CPC =  1 THEN 
                  MI_CODIGO_CPC        := RS_TIPOCLASIFICA.CODIGOCPCDANE;
                END IF ;
                IF RS_TIPOCLASIFICA.CODIGOUNIDADEJE IS NOT NULL AND MI_APLI_ING_CODIGOUNIDADEJE =  1 THEN 
                  MI_CODIGOUNIDADEJE   := RS_TIPOCLASIFICA.CODIGOUNIDADEJE;
                END IF ;
                IF RS_TIPOCLASIFICA.CODIGOFUENTE IS NOT NULL AND MI_APLI_ING_FUENTE_CUIPO =  1 THEN 
                  MI_FUENTE_CUIPO      := RS_TIPOCLASIFICA.CODIGOFUENTE;
                END IF ;
                IF RS_TIPOCLASIFICA.CODIGOCCPETREGA IS NOT NULL AND MI_APLI_ING_CODIGO_CCPET_REG  =  1 THEN 
                  MI_CODIGOCCPETREGA   := RS_TIPOCLASIFICA.CODIGOCCPETREGA;
                END IF ;
                IF RS_TIPOCLASIFICA.POLITCA_PUBLICA_CUIPO IS NOT NULL AND MI_APLI_ING_POLITICA  =  1 THEN 
                  MI_POLITICA_PUB      := RS_TIPOCLASIFICA.POLITCA_PUBLICA_CUIPO;
                END IF ;
                IF RS_TIPOCLASIFICA.DETALLE_SECTORIAL IS NOT NULL AND MI_APLI_ING_DETALLE_SEC  =  1 THEN 
                  MI_DETALLE_SEC       := RS_TIPOCLASIFICA.DETALLE_SECTORIAL;
                END IF ;
                IF RS_TIPOCLASIFICA.TIPO_RECURSO IS NOT NULL AND MI_APLI_ING_TIPO_RECURSO  =  1  THEN 
                  MI_TIPORECURSO       := RS_TIPOCLASIFICA.TIPO_RECURSO;
                END IF ;
        
                MI_CAMPOS :=  '';
                IF MI_APLI_ING_SECTOR = 1 THEN 
                  MI_CAMPOS := MI_CAMPOS || ' SECTOR           = ''' || MI_SECTOR          ||''',' ;
                END IF;
                IF MI_APLI_ING_PROGRAMA =  1 THEN 
                  MI_CAMPOS := MI_CAMPOS || ' PROGRAMA         = ''' || MI_PROGRAMA        ||''',' ;                 
                END IF;
                IF MI_APLI_ING_SUBPROGRAMA =  1 THEN         
                  MI_CAMPOS := MI_CAMPOS || ' SUBPROGRAMA      = ''' || MI_SUBPROGRAMA     ||''',' ;                      
                END IF;
                IF MI_APLI_ING_COD_PROD_CUIPO =  1 THEN 
                  MI_CAMPOS := MI_CAMPOS || ' COD_PROD_CUIPO   = ''' || MI_COD_PROD_CUIPO  ||''',' ;                     
                END IF;
                IF MI_APLI_ING_CODIGO_BPIN =  1 THEN 
                  MI_CAMPOS := MI_CAMPOS || ' CODIGO_BPIN      = ''' || MI_CODIGO_BPIN      ||''',' ;                    
                END IF;
                IF MI_APLI_ING_CODIGO_CCPET =  1 THEN 
                  MI_CAMPOS := MI_CAMPOS || ' CODIGO_CCPET     = ''' || MI_CODIGO_CCPET     ||''',' ;                    
                END IF;
                IF MI_APLI_ING_CODIGO_CPC =  1 THEN 
                  MI_CAMPOS := MI_CAMPOS || ' CODIGO_CPC       = ''' || MI_CODIGO_CPC   ||''',' ;                      
                END IF;
                IF MI_APLI_ING_CODIGOUNIDADEJE =  1 THEN 
                  MI_CAMPOS := MI_CAMPOS || ' CODIGOUNIDADEJE  = ''' || MI_CODIGOUNIDADEJE ||''',' ;                      
                END IF;
                IF MI_APLI_ING_FUENTE_CUIPO =  1 THEN 
                  MI_CAMPOS := MI_CAMPOS || ' FUENTE_CUIPO     = ''' || MI_FUENTE_CUIPO    ||''',' ;                
                END IF;
                IF  MI_APLI_ING_CODIGO_CCPET_REG  =  1 THEN 
                  MI_CAMPOS := MI_CAMPOS || ' CODIGOCCPETREGA  = ''' || MI_CODIGOCCPETREGA ||''',' ;  
                END IF;
                IF MI_APLI_ING_POLITICA =  1 THEN 
                  MI_CAMPOS := MI_CAMPOS || ' POLITICA_PUBLICA = ''' || MI_POLITICA_PUB    ||''',' ;                
                END IF;
                IF  MI_APLI_ING_DETALLE_SEC =  1 THEN 
                  MI_CAMPOS := MI_CAMPOS || ' DETALLE_SECTORIAL = ''' || MI_DETALLE_SEC ||''',' ;  
                END IF;
                IF  MI_APLI_DETALLE_SEC =  1  THEN 
                  MI_CAMPOS := MI_CAMPOS || ' RECURSO_SGR  = ''' || MI_TIPORECURSO ||''',' ;  
                END IF;

                MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA             || '''
                            AND ANO         =   ' || UN_ANIO                 || '
                            AND CUENTA      = ''' || RS_VERIF_CUENTAS.CODIGO || ''' ';
                IF  MI_CAMPOS  IS NOT NULL THEN 
                    MI_CAMPOS := SUBSTR(MI_CAMPOS,1,LENGTH(MI_CAMPOS) -1 ) ;   
                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                          ,UN_ACCION    => 'M'
                                                          ,UN_CAMPOS    => MI_CAMPOS
                                                          ,UN_CONDICION => MI_CONDICION);      
                END IF;
    END IF;
    END LOOP TIPOCLASIFICA;
    END LOOP VERIFICACUENTA;
    
    RETURN -1;
END FC_ACT_CLASI_AUX ;

PROCEDURE PR_ACTLZ_SITUACION_FONDOS
(

    UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA 
    ,UN_ANIO          IN PCK_SUBTIPOS.TI_ANIO
    )AS
    MI_RESPUESTA    VARCHAR(255);
    MI_CODIGO       VARCHAR2(255); 
    MI_SITUACION    VARCHAR2(255);
    MI_RTA          VARCHAR(1);
    MI_PARAMETRO    VARCHAR2(4000 CHAR);
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;

    BEGIN
    
    MI_PARAMETRO     := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA
                                                    , UN_NOMBRE =>'ACTUALIZA SSF DESDE FUENTE'
                                                    , UN_MODULO =>PCK_DATOS.MODULOENTESDECONTROL
                                                    , UN_FECHA_PAR=>SYSDATE ), 'NO');
                                                
    IF MI_PARAMETRO =  'SI' THEN

            <<SITUACION>>
                FOR RS_SITUACION IN 
                (
                    SELECT COMPANIA, ANO, CODIGO, IND_SINSITUACIONFONDOS
                    FROM FUENTE_RECURSOS 
                    WHERE COMPANIA = UN_COMPANIA
                    AND ANO = UN_ANIO
                    AND IND_SINSITUACIONFONDOS = -1
                )
                LOOP
                
                MI_CAMPOS := 'SITUACION_FONDOS = ''' || RS_SITUACION.IND_SINSITUACIONFONDOS || '''';
                
                MI_CONDICION := 'COMPANIA           = ''' || UN_COMPANIA             || '''
                                 AND ANO            =   ' || UN_ANIO             || '
                                 AND FUENTE_RECURSO   = ''' || RS_SITUACION.CODIGO || ''' ';
                                 
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                                      ,UN_ACCION    => 'M'
                                                                      ,UN_CAMPOS    => MI_CAMPOS
                                                                      ,UN_CONDICION => MI_CONDICION);
                END LOOP SITUACION;
                
     ELSE
     
                <<SITUACION>>
                FOR RS_SITUACION IN 
                (
                    SELECT COMPANIA, ANO, CODIGO, CONSITUACIONFONDOS
                    FROM PLAN_PRESUPUESTAL PL 
                    WHERE COMPANIA = UN_COMPANIA
                    AND ANO = UN_ANIO
                    AND CONSITUACIONFONDOS = -1
                )
                LOOP
                
                MI_CAMPOS := 'SITUACION_FONDOS = ''' || RS_SITUACION.CONSITUACIONFONDOS || '''';
                
                MI_CONDICION := 'COMPANIA    = ''' || UN_COMPANIA             || '''
                                 AND ANO         =   ' || UN_ANIO             || '
                                 AND CUENTA      = ''' || RS_SITUACION.CODIGO || ''' ';
                                 
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                                      ,UN_ACCION    => 'M'
                                                                      ,UN_CAMPOS    => MI_CAMPOS
                                                                      ,UN_CONDICION => MI_CONDICION);
                END LOOP SITUACION;
     
    END IF;
     
END PR_ACTLZ_SITUACION_FONDOS;

PROCEDURE PR_ACT_FUENTE_DETALLE
     /*  
        NAME              : PR_ACT_FUENTE_DETALLE
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : SEBASTIAN CARDENAS
        DATE MIGRADOR     : 02/07/2022
        TIME              : 
        SOURCE MODULE     : 
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : 
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME: actualizarFuenteDetalle
*/ 

(  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA 
  ,UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO
  ,UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
) AS
    MI_PARAMETRO   VARCHAR2(4000 CHAR);
    MI_NATURALEZA   VARCHAR2(4000 CHAR);
    MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
 MI_TABLA       VARCHAR2(4000 CHAR);
    MI_PARAMETRO1   VARCHAR2(4000 CHAR);
BEGIN

    MI_PARAMETRO     := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA
                                                , UN_NOMBRE =>'HEREDA FUENTE CUIPO DESDE FUENTE DE RECURSO'
                                                , UN_MODULO =>PCK_DATOS.MODULOPRESUPUESTO
                                                , UN_FECHA_PAR=>SYSDATE ), 'NO');
                                                
    MI_PARAMETRO1    := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA
                                                , UN_NOMBRE =>'MANEJA EQUIVALENTE CUIPO EN FUENTES PARA VIGENCIA ANTERIORES'
                                                , UN_MODULO =>PCK_DATOS.MODULOPRESUPUESTO
                                                , UN_FECHA_PAR=>SYSDATE ), 'NO');

     IF MI_PARAMETRO =  'SI' THEN 
/*
     FOR MI_RS IN (SELECT  FUENTE_RECURSOS.CODIGOFUENTE_RECURSOS , 
                           FUENTE_RECURSOS.EQUIVALENTECUIPO, 
                           FUENTE_RECURSOS.ANO, 
                           TIPOCLASIFICADOR.CLASECLASIFICADOR,
                           CLASECLASIFICADOR.APLICACION,
                           CLASECLASIFICADOR.APLICACIONINGRESOS
                     FROM FUENTE_RECURSOS
                         INNER JOIN TIPOCLASIFICADOR
                         ON TIPOCLASIFICADOR.COMPANIA = FUENTE_RECURSOS.COMPANIA
                         AND TIPOCLASIFICADOR.ANO = FUENTE_RECURSOS.ANO
                         AND TIPOCLASIFICADOR.EQUIVALENTE = FUENTE_RECURSOS.EQUIVALENTECUIPO
                         INNER JOIN CLASECLASIFICADOR
                         ON CLASECLASIFICADOR.COMPANIA = TIPOCLASIFICADOR.COMPANIA
                         AND CLASECLASIFICADOR.ANO = TIPOCLASIFICADOR.ANO
                         AND CLASECLASIFICADOR.CODIGO = TIPOCLASIFICADOR.CLASECLASIFICADOR
                         WHERE FUENTE_RECURSOS.COMPANIA = UN_COMPANIA
                           AND FUENTE_RECURSOS.ANO      = UN_ANIO
                           AND EQUIVALENTECUIPO IS NOT NULL)
                           LOOP

        IF MI_RS.APLICACION = '2' OR MI_RS.APLICACIONINGRESOS = '2' THEN
        (CASE WHEN :NATURALEZA = 'D' THEN APLICACION ELSE APLICACIONINGRESOS END) =:APLICACION AND CLASEPADRE IS NOT NULL
          MI_NATURALEZA := CASE WHEN MI_RS.APLICACION = '2' THEN 'D' ELSE CASE WHEN MI_RS.APLICACIONINGRESOS = '2' THEN 'C' END END;
          --insercion en plan_presupuestal
          MI_CAMPOS := 'CODIGO_FUENTE  = ''' || MI_RS.EQUIVALENTECUIPO ||''', MODIFIED_BY = ''' || UN_USUARIO || ''', DATE_MODIFIED = SYSDATE' ;  

          MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA || '''
                       AND ANO             =   ' || UN_ANIO || '
                       AND FUENTE_RECURSO  = ''' || MI_RS.FUENTE_RECURSOS || '''
                       AND NATURALEZA      = '''|| MI_NATURALEZA ||'''
                     '; 
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'PLAN_PRESUPUESTAL'
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICION);

          -- insercion en detalle_comprobante_pptal
          MI_CAMPOS := 'FUENTE_CUIPO  = ''' || MI_RS.EQUIVALENTECUIPO ||''', MODIFIED_BY = ''' || UN_USUARIO || ''', DATE_MODIFIED = SYSDATE' ;  

          MI_CONDICION := 'COMPANIA        = ''' || UN_COMPANIA || '''
                       AND ANO             =   ' || UN_ANIO || '
                       AND FUENTE_RECURSO  = ''' || MI_RS.FUENTE_RECURSOS || '''
                       AND NATURALEZA      = '''|| MI_NATURALEZA ||'''
                     '; 
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ,UN_CONDICION => MI_CONDICION); 
        END IF;

END LOOP;
*/
-- <TICKET:43 AUTOR:CP FECHA:08/07/2022> cambio el proceso segun documentacion
-- Ticket#7723266 Se comenta este proceso debido a que solo es necesario actualizar al detalle de los movimientos
/*
            MI_TABLA := '(
                    SELECT  PL.FUENTE_RECURSOS 
                          , FR.CODIGO
                          , PL.CODIGOFUENTE
                          , FR.EQUIVALENTECUIPO
                    FROM PLAN_PRESUPUESTAL PL INNER JOIN  FUENTE_RECURSOS FR
                    ON PL.COMPANIA =  FR.COMPANIA
                    AND PL.ANO =  FR.ANO
                    AND PL.FUENTE_RECURSOS =  FR.CODIGO
                    INNER JOIN CLASECLASIFICADOR CLC
                    ON   PL.COMPANIA =  CLC.COMPANIA
                    AND  PL.ANO =  CLC.ANO
                    WHERE PL.COMPANIA =  ''' || UN_COMPANIA ||'''
                    AND PL.ANO  = ' || UN_ANIO || '
                    AND CLC.CODIGO = ''009''
                    AND (FR.EQUIVALENTECUIPO IS NOT NULL OR FR.EQUIVALENTECUIPO NOT IN(''''))
                    AND  FR.EQUIVALENTECUIPO NOT IN( CASE WHEN PL.CODIGOFUENTE IS NULL THEN  ''0'' ELSE PL.CODIGOFUENTE END )
                    AND (CASE WHEN PL.NATURALEZA = ''D'' THEN CLC.APLICACION ELSE CLC.APLICACIONINGRESOS END) =2 
                    )  DATOS ';
            MI_CAMPOS := 'DATOS.CODIGOFUENTE =  DATOS.EQUIVALENTECUIPO';
  
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ); 
                                                  
*/
-- Ticket#7723266 - Se crea parametro con la finalidad de controlar si el proceso actualiza del equivalente cuipo o de acuerdo a los equivalentes

     IF MI_PARAMETRO1 =  'NO' THEN

          MI_TABLA := '(
                       SELECT PL.CODIGO RUBRO 
                              , DCP.FUENTE_RECURSO 
                              , FR.CODIGO
                              , DCP.FUENTE_CUIPO
                              , FR.EQUIVALENTECUIPO
                        FROM PLAN_PRESUPUESTAL PL INNER JOIN  DETALLE_COMPROBANTE_PPTAL DCP 
                        ON PL.COMPANIA =  DCP.COMPANIA
                        AND PL.ANO =  DCP.ANO
                        AND PL.CODIGO =  DCP.CUENTA
                        INNER JOIN  FUENTE_RECURSOS FR
                        ON DCP.COMPANIA =  FR.COMPANIA
                        AND DCP.ANO =  FR.ANO
                        AND DCP.FUENTE_RECURSO =  FR.CODIGO
                        INNER JOIN CLASECLASIFICADOR CLC
                        ON   DCP.COMPANIA =  CLC.COMPANIA
                        AND  DCP.ANO =  CLC.ANO
                        WHERE PL.COMPANIA =  ''' || UN_COMPANIA ||'''
                        AND PL.ANO  = ' || UN_ANIO || '
                        AND CLC.CODIGO = ''009''
                        AND (FR.EQUIVALENTECUIPO IS NOT NULL OR FR.EQUIVALENTECUIPO NOT IN(''''))
                        AND  (DCP.FUENTE_CUIPO <> FR.EQUIVALENTECUIPO OR DCP.FUENTE_CUIPO IS NULL)
                        AND (CASE WHEN PL.NATURALEZA = ''D'' THEN CLC.APLICACION ELSE CLC.APLICACIONINGRESOS END) =2
                        )  DATOS ';
            MI_CAMPOS := 'DATOS.FUENTE_CUIPO =  DATOS.EQUIVALENTECUIPO ';
  
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ); 
            ELSE                               
-- Ticket#7723266 - Se crea procedimiento para actualizar las fuentes de acuerdo al tipo de vigencia de los rubros.                                                
            MI_TABLA := '(
                       SELECT PL.CODIGO RUBRO 
                              , DCP.FUENTE_RECURSO 
                              , FR.CODIGO
                              , DCP.FUENTE_CUIPO
                              ,CASE WHEN PL.TIPOVIGENCIA = ''RA''
                               THEN FR.EQUIVALENTEAPROPIACION
                               ELSE CASE WHEN PL.TIPOVIGENCIA= ''RC''
                               THEN FR.EQUIVALENTECAJA
                               ELSE CASE WHEN PL.TIPOVIGENCIA NOT IN (''RA'',''RC'')
                               THEN FR.EQUIVALENTECUIPO
                               END END END EQUIVALENTECUIPO
                        FROM PLAN_PRESUPUESTAL PL INNER JOIN  DETALLE_COMPROBANTE_PPTAL DCP 
                        ON PL.COMPANIA =  DCP.COMPANIA
                        AND PL.ANO =  DCP.ANO
                        AND PL.CODIGO =  DCP.CUENTA
                        INNER JOIN  FUENTE_RECURSOS FR
                        ON DCP.COMPANIA =  FR.COMPANIA
                        AND DCP.ANO =  FR.ANO
                        AND DCP.FUENTE_RECURSO =  FR.CODIGO
                        INNER JOIN CLASECLASIFICADOR CLC
                        ON   DCP.COMPANIA =  CLC.COMPANIA
                        AND  DCP.ANO =  CLC.ANO
                        WHERE PL.COMPANIA =  ''' || UN_COMPANIA ||'''
                        AND PL.ANO  = ' || UN_ANIO || '
                        AND CLC.CODIGO = ''009''
                        AND (FR.EQUIVALENTECUIPO IS NOT NULL OR FR.EQUIVALENTECUIPO NOT IN(''''))
                        AND (FR.EQUIVALENTEAPROPIACION IS NOT NULL OR FR.EQUIVALENTEAPROPIACION NOT IN(''''))
                        AND (FR.EQUIVALENTECAJA IS NOT NULL OR FR.EQUIVALENTECAJA NOT IN(''''))
                        AND  (DCP.FUENTE_CUIPO <> (CASE WHEN PL.TIPOVIGENCIA = ''RA''
                               THEN FR.EQUIVALENTEAPROPIACION
                               ELSE CASE WHEN PL.TIPOVIGENCIA= ''RC''
                               THEN FR.EQUIVALENTECAJA
                               ELSE CASE WHEN PL.TIPOVIGENCIA NOT IN (''RA'',''RC'')
                               THEN FR.EQUIVALENTECUIPO
                               END END END) OR DCP.FUENTE_CUIPO IS NULL)
                        AND (CASE WHEN PL.NATURALEZA = ''D'' THEN CLC.APLICACION ELSE CLC.APLICACIONINGRESOS END) =2
                        )  DATOS ';
            MI_CAMPOS := 'DATOS.FUENTE_CUIPO =  DATOS.EQUIVALENTECUIPO ';

            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA
                                                  ,UN_ACCION    => 'M'
                                                  ,UN_CAMPOS    => MI_CAMPOS
                                                  ); 

    END IF; 
    END IF;
END PR_ACT_FUENTE_DETALLE;

FUNCTION FC_ACT_CLAS_CADENA_PPTAL  

      /*NAME              : FC_ACT_CLAS_CADENA_PPTAL
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ
        DATE MIGRADOR     : 26/04/2024
        TIME              : 09:55 AM
        SOURCE MODULE     : 
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : 
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME: actualizaClasificadorCadenaPptal
*/    
(  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA 
  ,UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO      
  ,UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN NUMBER AS
    MI_CAMPO VARCHAR2(60);
    MI_RTA_ACME       NUMBER(20,0);
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGENOEXISTE  PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MSGERROR    PCK_SUBTIPOS.TI_CLAVEVALOR;   
    MI_CLASES  PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_INICIA    NUMBER(1) :=9;
    MI_CLASEINI         PCK_SYSMAN_UTL.T_SPLIT;
    MI_NATURALEZA VARCHAR2(1);
    MI_PAR_CLASES_COMP   VARCHAR2(4000 CHAR);
BEGIN
    /*<TICHET:7723789 FECHA:17/02/2023 AUTOR:CP>--Campo creado para reemplazar la funcionalidad del parametro CLASES COMPROBANTE CONFIGURAR CLASIFICADORES el cual identifica los detalles de movimiento a los cuales se les permitirá hacer configuración, basados en la configuración de la clase
    MI_PAR_CLASES_COMP := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA,
                                              UN_NOMBRE =>'CLASES COMPROBANTE CONFIGURAR CLASIFICADORES',
                                              UN_MODULO =>'99',
                                              UN_FECHA_PAR=>SYSDATE ), 'NO');
    */
    BEGIN                                          
        SELECT NVL(CLASE_CUIPO,'NO')
          INTO MI_PAR_CLASES_COMP
        FROM ANO 
        WHERE COMPANIA  = UN_COMPANIA 
          AND NUMERO    = UN_ANIO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_PAR_CLASES_COMP := 'NO';
    END;

    MI_CLASEINI := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => MI_PAR_CLASES_COMP,
                                              UN_DELIMITADOR  =>',');


  <<RECORRECLASEINICIAL>>
  FOR ICLASE IN MI_CLASEINI.FIRST..MI_CLASEINI.LAST 
  LOOP
    IF MI_CLASEINI(ICLASE) IN('DIS', 'RES','REO','EGR') THEN
        MI_NATURALEZA:='D';
    ELSIF MI_CLASEINI(ICLASE) IN('ING') THEN
        MI_NATURALEZA:='C';
    ELSE
        RETURN '';
    END IF;
     
    IF MI_NATURALEZA='D' THEN 
        MI_CLASES(1).CLAVE := '1';
        MI_CLASES(1).VALOR := 'DIS'; 
        
        MI_CLASES(2).CLAVE := '2';
        MI_CLASES(2).VALOR := 'RES'; 
        
        MI_CLASES(3).CLAVE := '3';
        MI_CLASES(3).VALOR := 'REO'; 
        
        MI_CLASES(4).CLAVE := '4';
        MI_CLASES(4).VALOR := 'EGR'; 
    ELSE    
        MI_CLASES(1).CLAVE := '1';
        MI_CLASES(1).VALOR := 'ING'; 
    END IF;
    
    IF MI_CLASES.COUNT>0 THEN
        FOR i IN MI_CLASES.FIRST..MI_CLASES.LAST
        LOOP
            IF MI_CLASES(I).VALOR= MI_CLASEINI(ICLASE) THEN
                MI_INICIA := MI_CLASES(I).CLAVE;
            END IF;
        END LOOP;
    END IF;
    IF MI_CLASES.COUNT>0 THEN
        <<CLASEFINAL>>
        FOR i IN MI_INICIA..MI_CLASES.LAST
        LOOP
            <<INICIO_RS_CLASE>>
            FOR RS_CLASE IN 
                (
                SELECT CODIGO
                FROM CLASECLASIFICADOR 
                WHERE COMPANIA = UN_COMPANIA
                  AND ANO      = UN_ANIO
                  AND 2 = CASE WHEN MI_NATURALEZA ='D' THEN APLICACION ELSE APLICACIONINGRESOS END
                ORDER BY CODIGO
                )
            LOOP
                CASE RS_CLASE.CODIGO
                    WHEN '001' THEN
                          MI_CAMPO := 'SECTOR';
                    WHEN '002' THEN
                          MI_CAMPO := 'PROGRAMA';
                    WHEN '003' THEN
                          MI_CAMPO := 'SUBPROGRAMA';
                    WHEN '004' THEN
                          MI_CAMPO := 'COD_PROD_CUIPO';
                    WHEN '005' THEN
                          MI_CAMPO := 'CODIGO_BPIN';
                    WHEN '006' THEN
                          MI_CAMPO := 'CODIGO_CCPET';
                    WHEN '007' THEN
                          MI_CAMPO := 'CODIGO_CPC';
                    WHEN '008' THEN
                          MI_CAMPO := 'CODIGOUNIDADEJE';
                    WHEN '009' THEN
                          MI_CAMPO := 'FUENTE_CUIPO';
                    WHEN '010' THEN
                          MI_CAMPO := 'CODIGOCCPETREGA';
                     WHEN '011' THEN
                          MI_CAMPO := 'POLITICA_PUBLICA';
                    WHEN '012' THEN
                          MI_CAMPO := 'DETALLE_SECTORIAL';
                    WHEN '013' THEN
                          MI_CAMPO := 'RECURSO_SGR';
                    ELSE
                        MI_CAMPO := ' ';
                END CASE;
                MI_MERGEUSING := 'SELECT DETALLE_COMPROBANTE_PPTAL.COMPANIA,
                                    DETALLE_COMPROBANTE_PPTAL.ANO,
                                    DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE,
                                    DETALLE_COMPROBANTE_PPTAL.COMPROBANTE,
                                    DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO,
                                    '||MI_CAMPO||'
                                    FROM DETALLE_COMPROBANTE_PPTAL INNER JOIN TIPO_COMPROBPP
                                    ON DETALLE_COMPROBANTE_PPTAL.COMPANIA=TIPO_COMPROBPP.COMPANIA
                                    AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE=TIPO_COMPROBPP.CODIGO
                                    WHERE TIPO_COMPROBPP.COMPANIA= '''|| UN_COMPANIA||'''
                                    AND ANO = '||UN_ANIO||'
                                    AND CLASE IN ('''|| MI_CLASES(i).VALOR||''')
                                    AND NATURALEZA = ''' || MI_NATURALEZA || '''' ;
                BEGIN
                    BEGIN 
                        MI_MERGEENLACE := ' VISTA.COMPANIA   = TABLA.COMPANIA '
                                   || ' AND VISTA.ANO    = TABLA.ANO_AFECT '
                                   || ' AND VISTA.TIPO_CPTE = TABLA.TIPO_CPTE_AFECT '
                                   || ' AND VISTA.COMPROBANTE = TABLA.CMPTE_AFECTADO '
                                   || ' AND VISTA.CONSECUTIVO = TABLA.CONSECUTIVOPPTO ';
                
                        MI_MERGEEXISTE := ' UPDATE SET TABLA.' || MI_CAMPO || '= VISTA.' || MI_CAMPO ;
                        MI_RTA_ACME := PCK_DATOS.FC_ACME (UN_TABLA       => 'DETALLE_COMPROBANTE_PPTAL'
                                                         ,UN_ACCION      => 'MM'
                                                         ,UN_MERGEUSING  => MI_MERGEUSING
                                                         ,UN_MERGEENLACE => MI_MERGEENLACE
                                                         ,UN_MERGEEXISTE => MI_MERGEEXISTE);        
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                    END;            
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                        MI_MSGERROR(1).CLAVE := 'ANIO';
                        MI_MSGERROR(1).VALOR := UN_ANIO;
                        
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                                ,UN_ERROR_COD => PCK_ERRORES.ERR_PPTOACTUALIZASALDOS
                                                ,UN_REEMPLAZOS  => MI_MSGERROR  );
                    
                END;
            END LOOP INICIO_RS_CLASE;
        END LOOP CLASEFINAL;
    END IF;
END LOOP RECORRECLASEINICIAL;
    RETURN -1;
END FC_ACT_CLAS_CADENA_PPTAL;

PROCEDURE PR_CARGAR_CLASIFICADORES_HIJO
/*
    NAME              : PR_CARGAR_CLASIFICADORES_HIJO
    AUTHOR MIGRACION  : LUIS JACOBO DIAZ MUÑOZ
    DATE MIGRADOR     : 02/09/2022                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PROCEDIMIENTO QUE CARGA CLASIFICADORES HIJO DE UN EXCEL

    @NAME:    PR_CARGAR_CLASIFICADORES_HIJO
    @METHOD:  POST
    */
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENAPLAN  IN CLOB,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)  
AS 
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_ANIO               PCK_SUBTIPOS.TI_ANIO;
MI_EXISTE              NUMBER := 0;

BEGIN

  MI_ANIO := EXTRACT(YEAR FROM SYSDATE);
  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAPLAN,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

  <<CREAR_CLASIFICADORESHIJO>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
       --INSERTA TIPO_CLASIFICADORES_HIJO                                              

      MI_CAMPOS := 'COMPANIA
                  ,ANO
                  ,IDPADRE
                  ,IDHIJO
                  ,CREATED_BY
                  ,DATE_CREATED';
    
      MI_VALORES := ''''|| MI_DATOS_COLUMNAS(1) ||'''
                    ,'|| MI_DATOS_COLUMNAS(2) ||'
                  ,'''|| MI_DATOS_COLUMNAS(3) ||'''
                  ,'''|| MI_DATOS_COLUMNAS(4) ||'''
                  ,'''|| UN_USUARIO ||'''
                  ,SYSDATE';
    
        BEGIN
           SELECT COUNT('X') EXISTE
           INTO MI_EXISTE 
           FROM TIPOCLASIFICADORESHIJO
           WHERE COMPANIA = MI_DATOS_COLUMNAS(1)
              AND ANO  =  MI_DATOS_COLUMNAS(2)
              AND IDPADRE = MI_DATOS_COLUMNAS(3)
              AND IDHIJO = MI_DATOS_COLUMNAS(4);
        EXCEPTION WHEN NO_DATA_FOUND THEN
                        MI_EXISTE :=0 ;
                    END;

       IF   MI_EXISTE = 0 THEN 
         BEGIN
             PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TIPOCLASIFICADORESHIJO'
                                               ,UN_ACCION  => 'I'
                                               ,UN_CAMPOS  => MI_CAMPOS
                                               ,UN_VALORES => MI_VALORES);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
         END;
      END IF;



  END LOOP CREAR_CLASIFICADORESHIJO;
END PR_CARGAR_CLASIFICADORES_HIJO;

FUNCTION FC_GENERALIBROREGISTROSSQL
/*
  NAME              : FC_GENERALIBROREGISTROSSQL
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : MARIA CAMILA ROSERO PAZOS
  DATE MIGRATION    : 30/08/2022
  TIME              : 12:00 PM
  SOURCE MODULE     : PRESUPUESTO ()
  DESCRIPTION       : Devuelve un string concatenado  de una consulta enviada por parámetro  la consulta debe tener una sola columna 

 @NAME: FC_GENERALIBROREGISTROSSQL
*/
( 
 UN_STRSQL CLOB--pence que era aca pero no 
)
RETURN CLOB
AS
  MI_RESPUESTA       CLOB := '';
  RESULTSET SYS_REFCURSOR;
  TYPE DEMO_RECTYPE IS RECORD ( CADENA CLOB );--de cuanto estaba aca ? ahi como est hay esta bien no es hay 
  RECORDSET DEMO_RECTYPE;
BEGIN
    OPEN RESULTSET FOR UN_STRSQL ;
    LOOP
        FETCH RESULTSET INTO RECORDSET;
        EXIT WHEN RESULTSET%NOTFOUND;
         MI_RESPUESTA :=  MI_RESPUESTA  || REPLACE(REPLACE(RECORDSET.CADENA,CHR(13),''),CHR(10),'') || CHR(13) || CHR(10);
    END LOOP;
    CLOSE RESULTSET;
    RETURN MI_RESPUESTA;
END FC_GENERALIBROREGISTROSSQL;

FUNCTION FC_CARGA_RECLASIFICA_CIERRE 
/*
    NAME              : FC_CARGA_RECLASIFICA_CIERRE
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 09/12/2022                                                                                              
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : CARGA LA RECLASIFICACION POR CIERRE PRESUPUESTAL
    @NAME:    cargarReclasificacionCierre
    @METHOD:  POST
    */
(
    UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO        IN PCK_SUBTIPOS.TI_ANIO,   
    UN_CLASE      IN PCK_SUBTIPOS.TI_CLASECOMPROPPTO,
    UN_CADENA     IN CLOB,    
    UN_USUARIO    IN PCK_SUBTIPOS.TI_USUARIO
 )
 RETURN VARCHAR2
 AS 
    MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
    MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_RETORNO            VARCHAR2(3000) := ' '; 
    
    MI_EXISTEDATOS        PCK_SUBTIPOS.TI_ENTERO;      
    MI_EXISTERUBRO        PCK_SUBTIPOS.TI_ENTERO;   
    MI_EXISTEFUENTER      PCK_SUBTIPOS.TI_ENTERO;   
    MI_EXISTEREFERENCIA   PCK_SUBTIPOS.TI_ENTERO;   
    MI_EXISTECENTROC      PCK_SUBTIPOS.TI_ENTERO;   
    MI_EXISTEAUXILIAR     PCK_SUBTIPOS.TI_ENTERO;   
BEGIN

  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENA,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
  <<ACTUALIZAR_CIERRE_PRESUPUESTAL>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
                                                      
    /*Verifica si el RUBRO cierre a actualizar se encuentra registrado en la BD*/
    IF(MI_DATOS_COLUMNAS(9) IS NOT NULL)THEN        
        SELECT COUNT(0) CODIGO 
          INTO MI_EXISTERUBRO
          FROM PLAN_PRESUPUESTAL 
         WHERE COMPANIA = UN_COMPANIA
           AND ANO = UN_ANO 
           AND CODIGO = MI_DATOS_COLUMNAS(9);
        IF(MI_EXISTERUBRO = 0)  THEN
            MI_RETORNO := MI_RETORNO||' Rubro '||MI_DATOS_COLUMNAS(9)||' no se inserto. No existe.'||chr(13);
        END IF;
    END IF;
    
    /*Verifica si la FUENTE DE RECURSOS cierre a actualizar se encuentra registrado en la BD*/
    IF(MI_DATOS_COLUMNAS(10) IS NOT NULL)THEN
        SELECT COUNT(0) CODIGO
          INTO MI_EXISTEFUENTER
          FROM FUENTE_RECURSOS 
         WHERE COMPANIA = UN_COMPANIA
           AND ANO = UN_ANO 
           AND CODIGO = MI_DATOS_COLUMNAS(10);
        IF(MI_EXISTEFUENTER = 0)THEN
            MI_RETORNO := MI_RETORNO||' Fuente Recurso '||MI_DATOS_COLUMNAS(10)||' no se inserto. No existe.'||chr(13);
        END IF;
    END IF;
    
     /*Verifica si la REFERENCIA cierre a actualizar se encuentra registrado en la BD*/
    IF(MI_DATOS_COLUMNAS(11) IS NOT NULL)THEN    
        SELECT COUNT(0) CODIGO
          INTO MI_EXISTEREFERENCIA
          FROM REFERENCIA 
         WHERE COMPANIA = UN_COMPANIA
           AND ANO = UN_ANO 
           AND CODIGO = MI_DATOS_COLUMNAS(11);
        IF(MI_EXISTEREFERENCIA = 0) THEN
            MI_RETORNO := MI_RETORNO|| ' Referencia '||MI_DATOS_COLUMNAS(11)||' no se inserto. No existe.'||chr(13);
        END IF;
    END IF;
    
    /*Verifica si la CENTRO DE COSTO cierre a actualizar se encuentra registrado en la BD*/
    IF(MI_DATOS_COLUMNAS(12) IS NOT NULL)THEN
        SELECT COUNT(0) CODIGO
          INTO MI_EXISTECENTROC
          FROM CENTRO_COSTO 
         WHERE COMPANIA = UN_COMPANIA
           AND ANO = UN_ANO 
           AND CODIGO = MI_DATOS_COLUMNAS(12);
        IF(MI_EXISTECENTROC = 0)THEN
            MI_RETORNO := MI_RETORNO||' Centro de costo '||MI_DATOS_COLUMNAS(12)||' no se inserto. No existe.'||chr(13);
        END IF;
    END IF;
    
    /*Verifica si la AUXILIAR cierre a actualizar se encuentra registrado en la BD*/
    IF(MI_DATOS_COLUMNAS(13) IS NOT NULL)THEN
        SELECT COUNT(0) CODIGO
          INTO MI_EXISTEAUXILIAR
          FROM AUXILIAR 
         WHERE COMPANIA = UN_COMPANIA
           AND ANO = UN_ANO 
           AND CODIGO = MI_DATOS_COLUMNAS(13);
        IF(MI_EXISTEAUXILIAR = 0)THEN
            MI_RETORNO := MI_RETORNO||' Auxiliar '||MI_DATOS_COLUMNAS(13)||' no se inserto. No existe.'||chr(13);
        END IF;
    END IF;    
    
    BEGIN
        SELECT COUNT('X') CODIGO
          INTO MI_EXISTEDATOS
          FROM PLAN_PPTAL_CONFIG 
         WHERE COMPANIA = UN_COMPANIA  
           AND ANO  = UN_ANO 
           AND CODIGO = MI_DATOS_COLUMNAS(1) 
           AND CENTRO_COSTO = MI_DATOS_COLUMNAS(5)
           AND AUXILIAR = MI_DATOS_COLUMNAS(6)
           AND REFERENCIA =  MI_DATOS_COLUMNAS(4)
           AND FUENTE_RECURSO = MI_DATOS_COLUMNAS(3);
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTEDATOS := 0;
    END;
    
    IF(MI_EXISTEDATOS > 0)THEN
        MI_CAMPOS    := '';
        IF(UN_CLASE = 'RES')THEN
            IF(MI_EXISTERUBRO > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' RES_CODIGO_CIERRE = '''||MI_DATOS_COLUMNAS(9)||''','||chr(13);
            END IF;
            IF(MI_EXISTEFUENTER > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' RES_FUENTE_RECURSO_CIERRE = '''||MI_DATOS_COLUMNAS(10)||''','||chr(13);
            END IF;
            IF(MI_EXISTEREFERENCIA > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' RES_REFERENCIA_CIERRE = '''||MI_DATOS_COLUMNAS(11)||''','||chr(13);
            END IF;
            IF(MI_EXISTECENTROC > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' RES_CENTRO_COSTO_CIERRE = '''||MI_DATOS_COLUMNAS(12)||''','||chr(13);
            END IF;
            IF(MI_EXISTEAUXILIAR > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' RES_AUXILIAR_CIERRE = '''||MI_DATOS_COLUMNAS(13)||''','||chr(13);
            END IF;
            
            MI_CAMPOS := MI_CAMPOS ||' MODIFIED_BY     = '''||UN_USUARIO||'''';
        ELSE
            IF(MI_EXISTERUBRO > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' REO_CODIGO_CIERRE = '''||MI_DATOS_COLUMNAS(9)||''','||chr(13);
            END IF;
            IF(MI_EXISTEFUENTER > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' REO_FUENTE_RECURSO_CIERRE = '''||MI_DATOS_COLUMNAS(10)||''','||chr(13);
            END IF;
            IF(MI_EXISTEREFERENCIA > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' REO_REFERENCIA_CIERRE = '''||MI_DATOS_COLUMNAS(11)||''','||chr(13);
            END IF;
            IF(MI_EXISTECENTROC > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' REO_CENTRO_COSTO_CIERRE = '''||MI_DATOS_COLUMNAS(12)||''','||chr(13);
            END IF;
            IF(MI_EXISTEAUXILIAR > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' REO_AUXILIAR_CIERRE = '''||MI_DATOS_COLUMNAS(13)||''','||chr(13);
            END IF;
            
            MI_CAMPOS := MI_CAMPOS ||' MODIFIED_BY     = '''||UN_USUARIO||'''';
            
        END IF;
        MI_CONDICION := 'COMPANIA = ''' || UN_COMPANIA ||''' 
                         AND ANO  = '   || UN_ANO  ||' 
                         AND CODIGO = ''' || MI_DATOS_COLUMNAS(1) ||''' 
                         AND CENTRO_COSTO = ''' || MI_DATOS_COLUMNAS(5) ||'''
                         AND AUXILIAR = ''' || MI_DATOS_COLUMNAS(6) ||'''
                         AND REFERENCIA = ''' || MI_DATOS_COLUMNAS(4) ||'''
                         AND FUENTE_RECURSO = ''' || MI_DATOS_COLUMNAS(3) ||''''; 
        BEGIN
          BEGIN
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    =>  'PLAN_PPTAL_CONFIG', 
                                                               UN_ACCION    =>  'M', 
                                                               UN_CAMPOS    =>  MI_CAMPOS, 
                                                               UN_CONDICION =>  MI_CONDICION );
    
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
          END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
            IF(MI_RETORNO = ' ')THEN
                MI_RETORNO := ''''||MI_DATOS_COLUMNAS(9)||'''';
            ELSE
                MI_RETORNO := MI_RETORNO||','''||MI_DATOS_COLUMNAS(9)||'''';
            END IF;
        END; 
    ELSE
        MI_CAMPOS := 'COMPANIA,
                      ANO,
                      CODIGO,
                      CENTRO_COSTO,
                      AUXILIAR,
                      REFERENCIA,
                      FUENTE_RECURSO,
                      NATURALEZA,'||chr(13);
        IF(UN_CLASE = 'RES')THEN
            IF(MI_EXISTERUBRO > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' RES_CODIGO_CIERRE,'||chr(13);
            END IF;
            IF(MI_EXISTEFUENTER > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' RES_FUENTE_RECURSO_CIERRE,'||chr(13);
            END IF;
            IF(MI_EXISTEREFERENCIA > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' RES_REFERENCIA_CIERRE,'||chr(13);
            END IF;
            IF(MI_EXISTECENTROC > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' RES_CENTRO_COSTO_CIERRE,'||chr(13);
            END IF;
            IF(MI_EXISTEAUXILIAR > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' RES_AUXILIAR_CIERRE,'||chr(13);
            END IF;             
        ELSE
            IF(MI_EXISTERUBRO > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' REO_CODIGO_CIERRE,'||chr(13);
            END IF;
            IF(MI_EXISTEFUENTER > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' REO_FUENTE_RECURSO_CIERRE,'||chr(13);
            END IF;
            IF(MI_EXISTEREFERENCIA > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' REO_REFERENCIA_CIERRE,'||chr(13);
            END IF;
            IF(MI_EXISTECENTROC > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' REO_CENTRO_COSTO_CIERRE,'||chr(13);
            END IF;
            IF(MI_EXISTEAUXILIAR > 0)THEN
                MI_CAMPOS    := MI_CAMPOS ||' REO_AUXILIAR_CIERRE,'||chr(13);
            END IF;
        END IF;
        
        MI_CAMPOS    := MI_CAMPOS || 'MODIFIED_BY,
                             DATE_MODIFIED';
        
        MI_VALORES:=''''||UN_COMPANIA||''',
                            '''||UN_ANO||''',
                            '||MI_DATOS_COLUMNAS(1)||',
                            '''||MI_DATOS_COLUMNAS(5)||''',
                            '''||MI_DATOS_COLUMNAS(6)||''',
                            '''||MI_DATOS_COLUMNAS(4)||''',
                            '''||MI_DATOS_COLUMNAS(3)||''',
                            D,'''||chr(13);
        IF(MI_EXISTERUBRO > 0)THEN
            MI_VALORES:= MI_VALORES|| ''''||MI_DATOS_COLUMNAS(9)||','||chr(13);
        END IF;
        IF(MI_EXISTEFUENTER > 0)THEN
            MI_VALORES:= MI_VALORES|| ''''||MI_DATOS_COLUMNAS(10)||','||chr(13);
        END IF;
        IF(MI_EXISTEREFERENCIA > 0)THEN
            MI_VALORES:= MI_VALORES|| ''''||MI_DATOS_COLUMNAS(11)||','||chr(13);
        END IF;
        IF(MI_EXISTECENTROC > 0)THEN
            MI_VALORES:= MI_VALORES|| ''''||MI_DATOS_COLUMNAS(12)||','||chr(13);
        END IF;
        IF(MI_EXISTEAUXILIAR > 0)THEN
            MI_VALORES:= MI_VALORES|| ''''||MI_DATOS_COLUMNAS(13)||','||chr(13);
        END IF;                    
        
        MI_VALORES:= MI_VALORES|| ''''||UN_USUARIO||''',
                            SYSDATE';        
        BEGIN
            BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'PLAN_PPTAL_CONFIG'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
            END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
            MI_RETORNO := MI_RETORNO ||chr(13)||'Se presentó error al intentar crear registro en la tabla PLAN_PPTAL_CONFIG';
         END; 
    END IF;
  END LOOP ACTUALIZAR_CIERRE_PRESUPUESTAL;

  RETURN MI_RETORNO;
  
END FC_CARGA_RECLASIFICA_CIERRE;

PROCEDURE PR_ACTUALIZAR_AUX_EN_INGRESO
     /*  
        NAME              : PR_ACTUALIZAR_AUX_EN_INGRESO
        AUTHORS           : SYSMAN  SAS
        AUTHOR MIGRACION  : JOSE PASCUAL GOMEZ BLANCO
        DATE MIGRADOR     : 07/06/2023
        TIME              : 09:15 AM
        SOURCE MODULE     : 
        MODIFIER          :
        DATE MODIFIED     :
        TIME              :
        DESCRIPTION       : Permite actualizar los auxiliares en ingreso
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:actualizarAuxiliaresIngreso
        @METHOD:Post
*/ 
(UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA 
,UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO
,UN_RUBRO    IN VARCHAR2
,UN_CENTROCOSTO_INI  IN VARCHAR2
,UN_CENTROCOSTO_FIN  IN VARCHAR2
,UN_AUXILIAR_INI      IN VARCHAR2
,UN_AUXILIAR_FIN      IN VARCHAR2
,UN_REFERENCIA_INI    IN VARCHAR2
,UN_REFERENCIA_FIN    IN VARCHAR2
,UN_FUENTERECURSO_INI IN VARCHAR2
,UN_FUENTERECURSO_FIN IN VARCHAR2
)
AS
    MI_SUBCONDICION  PCK_SUBTIPOS.TI_CONDICION;
    MI_CONDICION   PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS      PCK_SUBTIPOS.TI_CAMPOS;
    MI_MSGERROR    PCK_SUBTIPOS.TI_CLAVEVALOR;   
    MI_NATURALEZA   PCK_SUBTIPOS.TI_NATURALEZACONTA;  
    RS_TIPO_CPTE PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL; 
    
BEGIN 

PCK_GENERALES.GL_CAMBIOAUXMANT := -1;
MI_NATURALEZA := 'C';
MI_SUBCONDICION := '(';

  BEGIN   
        <<TIPO_CPTE_POR_CLASE>>
         FOR RS IN( SELECT  DISTINCT TIPO_CPTE FROM DETALLE_COMPROBANTE_PPTAL  
         LEFT JOIN  TIPO_COMPROBPP 
         ON DETALLE_COMPROBANTE_PPTAL.COMPANIA = TIPO_COMPROBPP.COMPANIA  AND
         DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE = TIPO_COMPROBPP.CODIGO 
         WHERE TIPO_COMPROBPP.CLASE IN ('ING','AIN','DIN','MOI','ICA','AIC','DIC','MIC') AND DETALLE_COMPROBANTE_PPTAL.COMPANIA= UN_COMPANIA
         AND ANO= UN_ANIO      
         AND DETALLE_COMPROBANTE_PPTAL.NATURALEZA= MI_NATURALEZA
            ) 
     LOOP   
        MI_SUBCONDICION := MI_SUBCONDICION || ' '''||RS.TIPO_CPTE||''', ';
    END LOOP TIPO_CPTE_POR_CLASE; 
END; 

MI_SUBCONDICION := MI_SUBCONDICION ||' ''LASTVALUE'' ) ';
  
  
IF UN_CENTROCOSTO_INI <> 'N/A' AND  UN_CENTROCOSTO_FIN <> 'N/A' THEN

    BEGIN
        BEGIN

                   MI_CAMPOS  := 'CENTRO_COSTO= '''||UN_CENTROCOSTO_FIN||''' ';
                   MI_CONDICION :=  'COMPANIA='''||UN_COMPANIA||''' '||
                                    'AND ANO='||UN_ANIO||' '||
                                    'AND CENTRO_COSTO  IN('''||UN_CENTROCOSTO_INI||''')   '||
                                    'AND CUENTA='''||UN_RUBRO||''' ' ||
                                    'AND TIPO_CPTE IN  '||MI_SUBCONDICION||' ' ||
                                    'AND NATURALEZA='''||MI_NATURALEZA||''' ';

                   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                      ,UN_ACCION    => 'M'
                                                      ,UN_CAMPOS    => MI_CAMPOS
                                                      ,UN_CONDICION => MI_CONDICION);              
           
       
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                END;
            
            
              
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
        MI_MSGERROR(1).CLAVE := 'RUBRO';
        MI_MSGERROR(1).VALOR := UN_RUBRO;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD => PCK_ERRORES.ERR_PPTOACTUALIZASALDOS
                                ,UN_REEMPLAZOS  => MI_MSGERROR  );
    END;   



END IF;

IF UN_AUXILIAR_INI  <> 'N/A' AND UN_AUXILIAR_FIN <> 'N/A' THEN
  
  BEGIN
        BEGIN
            
                   MI_CAMPOS  := 'AUXILIAR= '''||UN_AUXILIAR_FIN||''' ';
                   MI_CONDICION :=  'COMPANIA='''||UN_COMPANIA||''' '||
                                    'AND ANO='||UN_ANIO||' '||
                                    'AND AUXILIAR  IN('''||UN_AUXILIAR_INI||''')   '||
                                    'AND CUENTA='''||UN_RUBRO||''' ' ||
                                    'AND TIPO_CPTE IN  '||MI_SUBCONDICION||' ' ||
                                    'AND NATURALEZA='''||MI_NATURALEZA||''' ';
                                

                   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                      ,UN_ACCION    => 'M'
                                                      ,UN_CAMPOS    => MI_CAMPOS
                                                      ,UN_CONDICION => MI_CONDICION);              
           
           
  
           
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                END;
                 
              
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
        MI_MSGERROR(1).CLAVE := 'RUBRO';
        MI_MSGERROR(1).VALOR := UN_RUBRO;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD => PCK_ERRORES.ERR_PPTOACTUALIZASALDOS
                                ,UN_REEMPLAZOS  => MI_MSGERROR  );
    END;  

END IF;


IF UN_FUENTERECURSO_INI <> 'N/A' AND  UN_FUENTERECURSO_FIN <> 'N/A' THEN

  BEGIN
        BEGIN
   
                   MI_CAMPOS  := 'FUENTE_RECURSO= '''||UN_FUENTERECURSO_FIN||''' ';
           
                                    
                                    MI_CONDICION :=  'COMPANIA='''||UN_COMPANIA||''' '||
                                    'AND ANO='||UN_ANIO||' '||
                                    'AND FUENTE_RECURSO  IN('''||UN_FUENTERECURSO_INI||''')   '||
                                    'AND CUENTA='''||UN_RUBRO||''' ' ||
                                    'AND TIPO_CPTE IN  '||MI_SUBCONDICION||' ' ||
                                    'AND NATURALEZA='''||MI_NATURALEZA||''' ';

                   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                      ,UN_ACCION    => 'M'
                                                      ,UN_CAMPOS    => MI_CAMPOS
                                                      ,UN_CONDICION => MI_CONDICION);              
           
                    
                 
              
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                END;
            
            
              
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
        MI_MSGERROR(1).CLAVE := 'RUBRO';
        MI_MSGERROR(1).VALOR := UN_RUBRO;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD => PCK_ERRORES.ERR_PPTOACTUALIZASALDOS
                                ,UN_REEMPLAZOS  => MI_MSGERROR  );
    END;  
    
    
END IF;

IF UN_REFERENCIA_INI  <> 'N/A' AND UN_REFERENCIA_FIN <> 'N/A' THEN

  BEGIN
        BEGIN

                   
                   MI_CAMPOS  := 'REFERENCIA = '''||UN_REFERENCIA_FIN||''' ';
                   MI_CONDICION :=  'COMPANIA='''||UN_COMPANIA||''' '||
                                    'AND ANO='||UN_ANIO||' '||
                                    'AND REFERENCIA  IN('''||UN_REFERENCIA_INI||''')   '||
                                    'AND CUENTA='''||UN_RUBRO||''' ' ||
                                    'AND TIPO_CPTE IN  '||MI_SUBCONDICION||' ' ||
                                    'AND NATURALEZA='''||MI_NATURALEZA||''' ';


                   PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_PPTAL'
                                                      ,UN_ACCION    => 'M'
                                                      ,UN_CAMPOS    => MI_CAMPOS
                                                      ,UN_CONDICION => MI_CONDICION);              
           
              

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                END;
            
            
              
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
        MI_MSGERROR(1).CLAVE := 'RUBRO';
        MI_MSGERROR(1).VALOR := UN_RUBRO;
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD => PCK_ERRORES.ERR_PPTOACTUALIZASALDOS
                                ,UN_REEMPLAZOS  => MI_MSGERROR  );
    END;  

    
END IF;

PCK_GENERALES.GL_CAMBIOAUXMANT := 0;

END PR_ACTUALIZAR_AUX_EN_INGRESO;

PROCEDURE PR_CARGAR_MOV_PPTALES
/*
    NAME              : PR_CARGAR_MOV_PPTALES
    AUTHOR MIGRACION  : GERMAN DAVID ROJAS GONZALEZ
    DATE MIGRADOR     : 27/05/2024                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : PROCEDIMIENTO QUE CARGA LOS DATOS DE COMPROBANTE Y DETALLE_COMPROBANTE PPTAL DE UN EXCEL

    @NAME:    cargarmovimientospptales
    @METHOD:  POST
    */
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENAH     IN CLOB,
  UN_CADENAD     IN CLOB,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)  
AS 
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_CAMPOSCUIPO        PCK_SUBTIPOS.TI_CAMPOS;
MI_CUIPOVALORES       PCK_SUBTIPOS.TI_VALORES;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_ANIO               PCK_SUBTIPOS.TI_ANIO;
MI_PARAMETRO          VARCHAR2(4000 CHAR);
MI_FECHA              DATE;                        
MI_NUMERO             NUMBER := 0;
MI_NATURALEZA        PLAN_PRESUPUESTAL.NATURALEZA%TYPE;
MI_CLASE             TIPO_COMPROBPP.CODIGO%TYPE;
MI_CUENTA            DETALLE_COMPROBANTE_PPTAL.CUENTA%TYPE;
MI_COD_SECTOR                DETALLE_COMPROBANTE_PPTAL.SECTOR%TYPE; 
MI_COD_SECTORCOMP            DETALLE_COMPROBANTE_PPTAL.SECTOR%TYPE;         
MI_COD_PROGRAMA              DETALLE_COMPROBANTE_PPTAL.PROGRAMA%TYPE;   
MI_COD_PROGRAMACOMP          DETALLE_COMPROBANTE_PPTAL.PROGRAMA%TYPE;         
MI_COD_SUBPROGRAMA           DETALLE_COMPROBANTE_PPTAL.SUBPROGRAMA%TYPE;
MI_COD_SUBPROGRAMACOMP       DETALLE_COMPROBANTE_PPTAL.SUBPROGRAMA%TYPE;         
MI_COD_CODIGOUNIDADEJE       DETALLE_COMPROBANTE_PPTAL.CODIGOUNIDADEJE%TYPE;
MI_COD_CODIGOUNIDADEJECOMP   DETALLE_COMPROBANTE_PPTAL.CODIGOUNIDADEJE%TYPE;         
MI_COD_CODIGOCCPETREGA       DETALLE_COMPROBANTE_PPTAL.CODIGOCCPETREGA%TYPE;         
MI_COD_CODIGOCCPETREGACOMP   DETALLE_COMPROBANTE_PPTAL.CODIGOCCPETREGA%TYPE;         
MI_COD_CODIGO_CCPET          DETALLE_COMPROBANTE_PPTAL.CODIGO_CCPET%TYPE;         
MI_COD_CODIGO_CCPETCOMP      DETALLE_COMPROBANTE_PPTAL.CODIGO_CCPET%TYPE;         
MI_COD_SITUACION_FONDOS      DETALLE_COMPROBANTE_PPTAL.SITUACION_FONDOS%TYPE;         
MI_COD_POLITICA_PUBLICA      DETALLE_COMPROBANTE_PPTAL.POLITICA_PUBLICA%TYPE;         
MI_COD_POLITICA_PUBLICACOMP  DETALLE_COMPROBANTE_PPTAL.POLITICA_PUBLICA%TYPE;         
MI_COD_DETALLE_SECTORIAL     DETALLE_COMPROBANTE_PPTAL.DETALLE_SECTORIAL%TYPE;     
MI_COD_DETALLE_SECTORIALCOMP DETALLE_COMPROBANTE_PPTAL.DETALLE_SECTORIAL%TYPE;     
MI_COD_COD_PROD_CUIPO        DETALLE_COMPROBANTE_PPTAL.COD_PROD_CUIPO%TYPE;         
MI_COD_CODIGO_CPC            DETALLE_COMPROBANTE_PPTAL.CODIGO_CPC%TYPE;         
MI_COD_CODIGO_CPCCOMP        DETALLE_COMPROBANTE_PPTAL.CODIGO_CPC%TYPE;         
MI_COD_CODIGO_BPIN           DETALLE_COMPROBANTE_PPTAL.CODIGO_BPIN%TYPE;
MI_COD_CODIGO_BPINCOMP       DETALLE_COMPROBANTE_PPTAL.CODIGO_BPIN%TYPE; 
MI_COD_FUENTE_CUIPO          DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO%TYPE;
MI_COD_FUENTE_CUIPOCOMP      DETALLE_COMPROBANTE_PPTAL.FUENTE_CUIPO%TYPE;
MI_COD_TIPO_CPTE             DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE%TYPE;  
MI_COD_COMPROBANTE           DETALLE_COMPROBANTE_PPTAL.COMPROBANTE%TYPE;  
MI_COD_CONSECUTIVO           DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO%TYPE;
MI_CONDICION                 PCK_SUBTIPOS.TI_CONDICION;  
MI_COD_FECHA                 DATE;
MI_RETORNO                   CLOB := '';
MI_RETORNOAUX                CLOB := '';
MI_TERCERO                   DETALLE_COMPROBANTE_PPTAL.TERCERO%TYPE;
MI_SUCURSAL                  DETALLE_COMPROBANTE_PPTAL.SUCURSAL%TYPE;
MI_TIPOCONTRATO              DETALLE_COMPROBANTE_PPTAL.TIPOCONTRATO%TYPE; 
MI_NUMEROCONTRATO            DETALLE_COMPROBANTE_PPTAL.NUMEROCONTRATO%TYPE;
MI_COMPROBANTE_AFEC          DETALLE_COMPROBANTE_PPTAL.CMPTE_AFECTADO%TYPE;
MI_CENTROCOSTO               DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO%TYPE;
MI_FUENTE_RECURSO            DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO%TYPE;
MI_AUXILIAR                  DETALLE_COMPROBANTE_PPTAL.AUXILIAR%TYPE;
MI_REFERENCIA                DETALLE_COMPROBANTE_PPTAL.REFERENCIA%TYPE;


BEGIN

    MI_ANIO := EXTRACT(YEAR FROM SYSDATE);
        MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAH,
                                                  UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

    --INSERTA COMPROBANTE_PPTAL
      <<CREAR_HEADER>>
      FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
        LOOP
            MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                              UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);

            MI_ANIO :=  EXTRACT(YEAR FROM  TO_DATE(MI_DATOS_COLUMNAS(4),'DD/MM/YYYY')  );
            
            --Valida que el tercero este creado.
            BEGIN
                SELECT NIT, SUCURSAL
                    INTO MI_TERCERO, MI_SUCURSAL
                    FROM TERCERO
                    WHERE COMPANIA = UN_COMPANIA
                    AND NIT        = MI_DATOS_COLUMNAS(7)
                    AND SUCURSAL   = MI_DATOS_COLUMNAS(8);
            EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                    MI_MSGERROR (1).CLAVE := 'TERCERO';
                    MI_MSGERROR (1).VALOR := MI_DATOS_COLUMNAS(7);
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERR_RETETERCENULO,
                                            UN_TABLAERROR => 'TERCERO',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
            END; 

            MI_CAMPOS := 'COMPANIA
                        ,ANO
                        ,TIPO
                        ,NUMERO
                        ,FECHA
                        ,DEPENDENCIA
                        ,DESCRIPCION
                        ,TERCERO
                        ,SUCURSAL
                        ,TIPO_CPTE_AFECT
                        ,CMPTE_AFECTADO                  
                        ,TIPOCONTRATO
                        ,NUMEROCONTRATO
                        ,TIPO_DOCUMENTO
                        ,NRO_DOCUMENTO
                        ,FECHA_VCN_DOC
                        ,CREATED_BY
                        ,DATE_CREATED';

            MI_PARAMETRO     := NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA =>UN_COMPANIA, UN_NOMBRE =>'MANEJA VENCIMIENTO ULTIMO DIA DEL AÑO', UN_MODULO =>PCK_DATOS.MODULOCONTABILIDAD, UN_FECHA_PAR=>SYSDATE ), 'NO');
                IF MI_PARAMETRO = 'SI' THEN
                   MI_FECHA   :=TO_DATE('31/12/'|| MI_ANIO ,'DD/MM/YYYY');
                ELSE
                   MI_FECHA:= TO_DATE(MI_DATOS_COLUMNAS(4),'DD/MM/YYYY');
                   MI_FECHA:= MI_FECHA +NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'DIAS VENCIMIENTO',PCK_DATOS.FC_MODULOCONTABILIDAD,SYSDATE),0);
                END IF;
            
            MI_VALORES := ''''|| MI_DATOS_COLUMNAS(1) ||'''
                        ,'||  MI_ANIO ||'
                        ,'''|| MI_DATOS_COLUMNAS(2) ||'''
                        ,'|| MI_DATOS_COLUMNAS(3) ||'
                        ,'''|| MI_DATOS_COLUMNAS(4) ||'''
                        ,'''|| MI_DATOS_COLUMNAS(5) ||'''
                        ,'''|| MI_DATOS_COLUMNAS(6) ||'''
                        ,'''|| MI_DATOS_COLUMNAS(7) ||'''
                        ,'''|| MI_DATOS_COLUMNAS(8) ||'''
                        ,'''|| CASE WHEN MI_DATOS_COLUMNAS(9) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(9) END ||'''
                        ,'''|| CASE WHEN MI_DATOS_COLUMNAS(10) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(10) END ||'''
                        ,'''|| CASE WHEN MI_DATOS_COLUMNAS(11) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(11) END ||''' 
                        ,'''|| CASE WHEN MI_DATOS_COLUMNAS(12) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(12) END ||'''
                        ,'''|| CASE WHEN MI_DATOS_COLUMNAS(13) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(13) END ||''' 
                        ,'''|| CASE WHEN MI_DATOS_COLUMNAS(14) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(14) END ||''' 
                        ';
            MI_VALORES := MI_VALORES 
                      ||',TO_DATE(''' || TO_CHAR(MI_FECHA, 'DD/MM/YYYY') || ''',''DD/MM/YYYY'') ' ||'
                        ,'''|| UN_USUARIO ||'''
                        ,SYSDATE';
          BEGIN
              IF  NVL(MI_NUMERO,0) != MI_DATOS_COLUMNAS(3) THEN
                BEGIN

                  PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'COMPROBANTE_PPTAL'
                                                    ,UN_ACCION  => 'I'
                                                    ,UN_CAMPOS  => MI_CAMPOS
                                                    ,UN_VALORES => MI_VALORES);


                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                END;
              END IF;
              MI_NUMERO := MI_DATOS_COLUMNAS(3);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
              MI_MSGERROR(1).CLAVE := 'TIPCPTE';
              MI_MSGERROR(1).VALOR := MI_DATOS_COLUMNAS(2);
              MI_MSGERROR(2).CLAVE := 'NROCPTE';
              MI_MSGERROR(2).VALOR := MI_DATOS_COLUMNAS(3);
              MI_MSGERROR(3).CLAVE := 'DESCRIPCION';
              MI_MSGERROR(3).VALOR := MI_DATOS_COLUMNAS(7);
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_CREARCPTERED, UN_TABLAERROR => 'COMPROBANTE_PPTAL', UN_REEMPLAZOS => MI_MSGERROR );
          END;
      END LOOP CREAR_HEADER;

    --INSERTA DETALLE_PRESUPUESTAL_PPTAL
    
    MI_COD_CONSECUTIVO := 1;

    MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAD,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
        
        <<CREAR_DETALLES>>
        FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
        LOOP
            MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                            UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);

            MI_ANIO :=  EXTRACT(YEAR FROM  TO_DATE(MI_DATOS_COLUMNAS(4),'DD/MM/YYYY')  );
            

            MI_CUENTA                   := MI_DATOS_COLUMNAS(6);
            MI_COD_TIPO_CPTE            := MI_DATOS_COLUMNAS(2);
            MI_COD_COMPROBANTE          := MI_DATOS_COLUMNAS(3);
            MI_COD_SECTOR               := MI_DATOS_COLUMNAS(23);
            MI_COD_PROGRAMA             := MI_DATOS_COLUMNAS(24);
            MI_COD_SUBPROGRAMA          := MI_DATOS_COLUMNAS(25);
            MI_COD_COD_PROD_CUIPO       := MI_DATOS_COLUMNAS(26);
            MI_COD_CODIGO_BPIN          := MI_DATOS_COLUMNAS(27);
            MI_COD_CODIGO_CCPET         := MI_DATOS_COLUMNAS(28);
            MI_COD_CODIGO_CPC           := MI_DATOS_COLUMNAS(29);
            MI_COD_CODIGOUNIDADEJE      := MI_DATOS_COLUMNAS(30);
            MI_COD_FUENTE_CUIPO         := MI_DATOS_COLUMNAS(31);
            MI_COD_CODIGOCCPETREGA      := MI_DATOS_COLUMNAS(32);
            MI_COD_POLITICA_PUBLICA     := MI_DATOS_COLUMNAS(33);
            MI_COD_DETALLE_SECTORIAL    := MI_DATOS_COLUMNAS(34);

            --Valida que el rubro presupuestal este creado y tenga indicador de movimiento o auxiliares.
            BEGIN
                SELECT NATURALEZA
                    INTO MI_NATURALEZA
                    FROM PLAN_PRESUPUESTAL P
                    WHERE  P.COMPANIA = UN_COMPANIA
                    AND P.ANO      = MI_ANIO
                    AND P.CODIGO   = MI_CUENTA
                    AND (MOVIMIENTO + MAN_CEN_CTO + MAN_AUX_FUE + MAN_AUX_GEN + MAN_AUX_REF) <> 0;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                    MI_MSGERROR (1).CLAVE := 'CUENTA';
                    MI_MSGERROR (1).VALOR := MI_CUENTA;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PRESUPUESTO_SIN_IND,
                                            UN_TABLAERROR => 'PLAN_PRESUPUESTAL',
                                            UN_REEMPLAZOS => MI_MSGERROR);

                END;
            END;          

            --Valida que el tercero este creado.
            BEGIN
                SELECT NIT, SUCURSAL
                    INTO MI_TERCERO, MI_SUCURSAL
                    FROM TERCERO
                    WHERE COMPANIA = UN_COMPANIA
                    AND NIT        = MI_DATOS_COLUMNAS(9)
                    AND SUCURSAL   = MI_DATOS_COLUMNAS(10);
            EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
                    MI_MSGERROR (1).CLAVE := 'TERCERO';
                    MI_MSGERROR (1).VALOR := MI_DATOS_COLUMNAS(9);
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERR_RETETERCENULO,
                                            UN_TABLAERROR => 'TERCERO',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
            END;        

            --Valida que el tipo y número de contrato esten creados.
            IF MI_DATOS_COLUMNAS(19) <> 'NoDato' AND MI_DATOS_COLUMNAS(20) <> 'NoDato' THEN 
                BEGIN
                    SELECT CLASEORDEN, NUMERO 
                        INTO MI_TIPOCONTRATO, MI_NUMEROCONTRATO
                        FROM ORDENDECOMPRA
                        WHERE COMPANIA  = UN_COMPANIA
                        AND CLASEORDEN  = MI_DATOS_COLUMNAS(19)
                        AND NUMERO      = MI_DATOS_COLUMNAS(20);
                    EXCEPTION WHEN NO_DATA_FOUND THEN
                    BEGIN
                        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                        MI_RETORNO := 'El tipo de contrato ' || MI_DATOS_COLUMNAS(19) || ' y numero de contrato' || MI_DATOS_COLUMNAS(20) || ' no existe';
                        MI_MSGERROR (1).CLAVE := 'MENSAJE';
                        MI_MSGERROR (1).VALOR := MI_RETORNO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                                UN_TABLAERROR => 'ORDENDECOMPRA',
                                                UN_REEMPLAZOS => MI_MSGERROR);
                    END;
                END; 
            END IF;

            --valida que el comprobante afectar exista
            IF MI_DATOS_COLUMNAS(16) <> 'NoDato' AND MI_DATOS_COLUMNAS(17) <> 'NoDato' THEN 
                BEGIN
                        SELECT COMPROBANTE
                            INTO MI_COMPROBANTE_AFEC
                            FROM DETALLE_COMPROBANTE_PPTAL
                            WHERE COMPANIA  = UN_COMPANIA
                            AND ANO         = MI_DATOS_COLUMNAS(15)
                            AND TIPO_CPTE   = MI_DATOS_COLUMNAS(16)
                            AND COMPROBANTE = MI_DATOS_COLUMNAS(17)
                            AND CONSECUTIVO = MI_DATOS_COLUMNAS(18);
                        EXCEPTION WHEN NO_DATA_FOUND THEN
                        BEGIN
                            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                            MI_MSGERROR(1).CLAVE := 'TIPOCPTE';
                            MI_MSGERROR(1).VALOR := MI_DATOS_COLUMNAS(16);
                            MI_MSGERROR(2).CLAVE := 'NROCPTE';
                            MI_MSGERROR(2).VALOR := MI_DATOS_COLUMNAS(17);
                            MI_MSGERROR(3).CLAVE := 'CUENTA';
                            MI_MSGERROR(3).VALOR := MI_DATOS_COLUMNAS(6);
                            MI_MSGERROR(4).CLAVE := 'CONSECUTIVO';
                            MI_MSGERROR(4).VALOR := MI_DATOS_COLUMNAS(18);
                            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                    UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_CPTEAFECTAR_NE,
                                                    UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL',
                                                    UN_REEMPLAZOS => MI_MSGERROR);
                        END;
                    END;
                END IF;

            --Valida que el Centro de costos este creado.
            BEGIN
                SELECT CODIGO
                INTO MI_CENTROCOSTO
                FROM CENTRO_COSTO
                WHERE COMPANIA  = UN_COMPANIA
                AND ANO         = MI_ANIO
                AND CODIGO      = MI_DATOS_COLUMNAS(14)
                AND MOVIMIENTO  = -1;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    MI_RETORNO := 'El centro de costo ' || MI_DATOS_COLUMNAS(14) || ' no existe o no tiene movimiento';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'CENTRO_COSTO',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
            END; 

            --Valida que la fuente este creada.
            BEGIN
                SELECT CODIGO
                INTO MI_FUENTE_RECURSO
                FROM FUENTE_RECURSOS
                WHERE COMPANIA  = UN_COMPANIA
                AND ANO         = MI_ANIO
                AND CODIGO      = MI_DATOS_COLUMNAS(12);
            EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    MI_RETORNO := 'la fuente de recursos ' || MI_DATOS_COLUMNAS(12) || ' no existe';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'FUENTE_RECURSOS',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
            END;
                                        
            --Valida que el auxiliar este creado.
            BEGIN
                SELECT CODIGO
                INTO MI_AUXILIAR
                FROM AUXILIAR
                WHERE COMPANIA  = UN_COMPANIA
                AND ANO         = MI_ANIO
                AND CODIGO      = MI_DATOS_COLUMNAS(11)
                AND MOVIMIENTO  = -1;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    MI_RETORNO := 'El auxiliar ' || MI_DATOS_COLUMNAS(11) || ' no existe o no tiene movimiento';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'AUXILIAR',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
            END;

            --Valida que la referencia este creado.
            BEGIN
                SELECT CODIGO
                INTO MI_REFERENCIA
                FROM REFERENCIA
                WHERE COMPANIA  = UN_COMPANIA
                AND ANO         = MI_ANIO
                AND CODIGO      = MI_DATOS_COLUMNAS(13)
                AND MOVIMIENTO  = -1;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                    MI_RETORNO := 'la referencia ' || MI_DATOS_COLUMNAS(13) || ' no existe o no tiene movimiento';
                    MI_MSGERROR (1).CLAVE := 'MENSAJE';
                    MI_MSGERROR (1).VALOR := MI_RETORNO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                            UN_TABLAERROR => 'REFERENCIA',
                                            UN_REEMPLAZOS => MI_MSGERROR);
                END;
            END;  


            BEGIN
                SELECT   H.IDPADRE 
                INTO     MI_COD_PROGRAMACOMP 
                FROM     TIPOCLASIFICADOR T     INNER JOIN TIPOCLASIFICADORESHIJO H 
                ON T.COMPANIA = H.COMPANIA                                            
                AND T.ANO = H.ANO                                            
                AND T.ID = H.IDPADRE 
                INNER JOIN TIPOCLASIFICADOR TCH                                         
                ON H.COMPANIA =  TCH.COMPANIA                                        
                AND H.ANO =  TCH.ANO                                         
                AND H.IDHIJO =  TCH.ID  
                WHERE     T.COMPANIA = UN_COMPANIA     AND T.ANO = MI_ANIO     
                AND INSTR(          CONCAT(CONCAT(',',(
                    SELECT     CLASEPADRE FROM     CLASECLASIFICADOR WHERE     COMPANIA = UN_COMPANIA     AND ANO = MI_ANIO     AND CODIGO = '004'     AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         APLICACION         ELSE         APLICACIONINGRESOS         END     ) = 2     
                AND CLASEPADRE IS NOT NULL )),',') , CONCAT(             CONCAT(                 ',', T.CLASECLASIFICADOR             ), ','         )     ) > 0    
                AND T.CODIGO IS NOT NULL
                AND TCH.CODIGO =  MI_COD_COD_PROD_CUIPO
                GROUP BY H.IDPADRE;
            EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
            MI_COD_PROGRAMACOMP := '';                
            END;


            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_SUBPROGRAMACOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = MI_ANIO     
                    AND CC.CODIGO = '003'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
                    AND TC.CODIGO =  MI_COD_SUBPROGRAMA
                GROUP BY TC.CODIGO  ;
            EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                    MI_COD_SUBPROGRAMACOMP := '';                
            END;



            BEGIN
                SELECT     H.IDPADRE 
                INTO      MI_COD_SECTORCOMP
                FROM     TIPOCLASIFICADOR T     
                INNER JOIN TIPOCLASIFICADORESHIJO H 
                    ON T.COMPANIA = H.COMPANIA                                            
                    AND T.ANO = H.ANO                                            
                    AND T.ID = H.IDPADRE 
                INNER JOIN TIPOCLASIFICADOR TCH                                         
                    ON H.COMPANIA =  TCH.COMPANIA                                        
                    AND H.ANO =  TCH.ANO                                         
                    AND H.IDHIJO =  TCH.ID  
                WHERE     T.COMPANIA = UN_COMPANIA     
                    AND T.ANO = MI_ANIO     
                    AND INSTR(          CONCAT(CONCAT(',',(
                    SELECT     CLASEPADRE FROM     CLASECLASIFICADOR 
                    WHERE     COMPANIA = UN_COMPANIA   
                        AND ANO = MI_ANIO     
                        AND CODIGO = '002'     
                        AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         APLICACION         ELSE         APLICACIONINGRESOS         END     ) = 2     
                        AND CLASEPADRE IS NOT NULL )),',') , CONCAT(             CONCAT(                 ',', T.CLASECLASIFICADOR             ), ','         )     ) > 0      
                    AND H.IDHIJO =  MI_COD_PROGRAMACOMP     
                    AND T.CODIGO IS NOT NULL
                GROUP BY H.IDPADRE;
            EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                    MI_COD_SECTORCOMP := '';                
            END;   

            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_CODIGO_BPINCOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = MI_ANIO     
                    AND CC.CODIGO = '005'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
                    AND TC.CODIGO =  MI_COD_CODIGO_BPIN
                GROUP BY TC.CODIGO  ;
            EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                    MI_COD_CODIGO_BPINCOMP := '';                
            END;

            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_CODIGO_CCPETCOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = MI_ANIO     
                    AND CC.CODIGO = '006'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
                    AND TC.CODIGO =  MI_COD_CODIGO_CCPET
                GROUP BY TC.CODIGO  ;
            EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                    MI_COD_CODIGO_CCPETCOMP := '';                
            END;

            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_CODIGO_CPCCOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = MI_ANIO     
                    AND CC.CODIGO = '007'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
                    AND TC.CODIGO =  MI_COD_CODIGO_CPC
                GROUP BY TC.CODIGO  ;
            EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                    MI_COD_CODIGO_CPCCOMP := '';                
            END;

            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_CODIGOUNIDADEJECOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = MI_ANIO     
                    AND CC.CODIGO = '008'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
                    AND TC.CODIGO =  MI_COD_CODIGOUNIDADEJE
                GROUP BY TC.CODIGO  ;
            EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                    MI_COD_CODIGOUNIDADEJECOMP := '';                
            END;

            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_FUENTE_CUIPOCOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = MI_ANIO     
                    AND CC.CODIGO = '009'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
                    AND TC.CODIGO =  MI_COD_FUENTE_CUIPO
                GROUP BY TC.CODIGO  ;
            EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                    MI_COD_FUENTE_CUIPOCOMP := '';                
            END;

            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_CODIGOCCPETREGACOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = MI_ANIO     
                    AND CC.CODIGO = '010'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
                    AND TC.CODIGO =  MI_COD_CODIGOCCPETREGA
                GROUP BY TC.CODIGO  ;
            EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                    MI_COD_CODIGOCCPETREGACOMP := '';                
            END;

            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_POLITICA_PUBLICACOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = MI_ANIO     
                    AND CC.CODIGO = '011'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
                    AND TC.CODIGO =  MI_COD_POLITICA_PUBLICA
                GROUP BY TC.CODIGO  ;
            EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                    MI_COD_POLITICA_PUBLICACOMP := '';                
            END;

            BEGIN
                SELECT  TC.CODIGO 
                INTO MI_COD_DETALLE_SECTORIALCOMP
                FROM     CLASECLASIFICADOR  CC INNER JOIN TIPOCLASIFICADOR TC 
                    ON  CC.COMPANIA =  TC.COMPANIA
                    AND CC.ANO      =  TC.ANO
                    AND CC.CODIGO   =  TC.CLASECLASIFICADOR
                WHERE     CC.COMPANIA = UN_COMPANIA     
                    AND CC.ANO = MI_ANIO     
                    AND CC.CODIGO = '012'     
                    AND (         CASE         WHEN MI_NATURALEZA = 'D' THEN         CC.APLICACION         ELSE         CC.APLICACIONINGRESOS         END     ) = 2     
                    AND TC.CODIGO =  MI_COD_DETALLE_SECTORIAL
                GROUP BY TC.CODIGO  ;
            EXCEPTION WHEN NO_DATA_FOUND OR TOO_MANY_ROWS THEN
                    MI_COD_DETALLE_SECTORIALCOMP := '';                
            END;

        --CLASECLASIFICADOR -> APLICACION 0;No Aplica;1;Rubro;2;Detall

            MI_CAMPOSCUIPO :=  '';
            MI_CUIPOVALORES :=  '';
            
            IF '001' || MI_COD_SECTOR = MI_COD_SECTORCOMP  AND  MI_COD_SECTOR IS NOT NULL THEN 
            MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ' ,SECTOR             ';
            MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ''' || MI_COD_SECTOR          ||''' ' ;
            ELSIF MI_COD_SECTOR = 'NoDato' OR MI_COD_SECTOR IS NULL OR MI_COD_SECTOR = 'null' THEN
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Sector para la cuenta: ' || MI_CUENTA||CHR(13);
            ELSE
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || ' 001 No esta configurado como detalle  o el codigo ' || MI_COD_SECTOR || ' No existe  en TIPOCLASIFICADOR '||CHR(13);
            END IF;
            IF '002' || MI_COD_PROGRAMA =  MI_COD_PROGRAMACOMP  AND  MI_COD_PROGRAMA IS NOT NULL THEN 
            MI_CAMPOSCUIPO := MI_CAMPOSCUIPO  || ' ,PROGRAMA           ';
            MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_PROGRAMA        ||''' ' ;  
            ELSIF MI_COD_PROGRAMA = 'NoDato' OR MI_COD_PROGRAMA IS NULL OR MI_COD_PROGRAMA = 'null' THEN
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Programa para la cuenta: ' || MI_CUENTA||CHR(13);
            ELSE
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '002 No esta configurado como detalle  o el codigo ' || MI_COD_PROGRAMA || ' No existe  en TIPOCLASIFICADOR '||CHR(13);               
            END IF;
            IF MI_COD_SUBPROGRAMA =  MI_COD_SUBPROGRAMACOMP AND  MI_COD_SUBPROGRAMA IS NOT NULL THEN         
            MI_CAMPOSCUIPO := MI_CAMPOSCUIPO  || ' ,SUBPROGRAMA        ';
            MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_SUBPROGRAMA     ||''' ' ;       
            ELSIF MI_COD_SUBPROGRAMA = 'NoDato' OR MI_COD_SUBPROGRAMA IS NULL OR MI_COD_SUBPROGRAMA = 'null' THEN
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador SubPrograma para la cuenta: ' || MI_CUENTA||CHR(13);
            ELSE
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '003 No esta configurado como detalle  o el codigo ' || MI_COD_SUBPROGRAMA || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                              
            END IF;
            IF  MI_COD_PROGRAMACOMP  IS NOT NULL THEN 
            MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ' ,COD_PROD_CUIPO     ';
            MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_COD_PROD_CUIPO  ||''' ' ;   
            ELSIF MI_COD_COD_PROD_CUIPO = 'NoDato' OR MI_COD_COD_PROD_CUIPO IS NULL OR MI_COD_COD_PROD_CUIPO = 'null' THEN
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Producto para la cuenta: ' || MI_CUENTA||CHR(13);  
            ELSE
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '004 No esta configurado como detalle  o el codigo ' || MI_COD_COD_PROD_CUIPO || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                                 
            END IF;
            IF MI_COD_CODIGO_BPIN =  MI_COD_CODIGO_BPINCOMP  AND  MI_COD_CODIGO_BPIN IS NOT NULL THEN 
            MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ', CODIGO_BPIN       ';
            MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_CODIGO_BPIN      ||''' ' ; 
            ELSIF MI_COD_CODIGO_BPIN = 'NoDato' OR MI_COD_CODIGO_BPIN IS NULL OR MI_COD_CODIGO_BPIN = 'null' THEN
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Bpin para la cuenta: ' || MI_CUENTA||CHR(13);
            ELSE
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '005 No esta configurado como detalle  o el codigo ' || MI_COD_CODIGO_BPIN || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                                  
            END IF;
            IF MI_COD_CODIGO_CCPET =  MI_COD_CODIGO_CCPETCOMP  AND  MI_COD_CODIGO_CCPET IS NOT NULL THEN 
            MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ', CODIGO_CCPET      ';
            MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_CODIGO_CCPET     ||''' ' ;   
            ELSIF MI_COD_CODIGO_CCPET = 'NoDato' OR MI_COD_CODIGO_CCPET IS NULL OR MI_COD_CODIGO_CCPET = 'null' THEN
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Codigo CCPET para la cuenta: ' || MI_CUENTA||CHR(13);
            ELSE
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '006 No esta configurado como detalle  o el codigo ' || MI_COD_CODIGO_CCPET || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                                
            END IF;
            IF MI_COD_CODIGO_CPC =  MI_COD_CODIGO_CPCCOMP  AND  MI_COD_CODIGO_CPC IS NOT NULL THEN 
            MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ' ,CODIGO_CPC        ';
            MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_CODIGO_CPC   ||''' ' ;       
            ELSIF MI_COD_CODIGO_CPC = 'NoDato' OR MI_COD_CODIGO_CPC IS NULL OR MI_COD_CODIGO_CPC = 'null' THEN
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Codigo CPC para la cuenta: ' || MI_CUENTA||CHR(13);
            ELSE
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '007 No esta configurado como detalle  o el codigo ' || MI_COD_CODIGO_CPC || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                              
            END IF;
            IF MI_COD_CODIGOUNIDADEJE =  MI_COD_CODIGOUNIDADEJECOMP  AND  MI_COD_CODIGOUNIDADEJE IS NOT NULL THEN 
            MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ', CODIGOUNIDADEJE   ';
            MI_CUIPOVALORES:= MI_CUIPOVALORES || ',  ''' || MI_COD_CODIGOUNIDADEJE ||''' ' ; 
            ELSIF MI_COD_CODIGOUNIDADEJE = 'NoDato' OR MI_COD_CODIGOUNIDADEJE IS NULL OR MI_COD_CODIGOUNIDADEJE = 'null' THEN
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Unidad Ejecutora para la cuenta: ' || MI_CUENTA||CHR(13);
            ELSE
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '008 No esta configurado como detalle  o el codigo ' || MI_COD_CODIGOUNIDADEJE || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                                    
            END IF;
            IF MI_COD_FUENTE_CUIPO =  MI_COD_FUENTE_CUIPOCOMP  AND  MI_COD_FUENTE_CUIPO IS NOT NULL THEN 
            MI_CAMPOSCUIPO := MI_CAMPOSCUIPO ||  ' ,FUENTE_CUIPO      ';
            MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_FUENTE_CUIPO    ||''' ' ;  
            ELSIF MI_COD_FUENTE_CUIPO = 'NoDato' OR MI_COD_FUENTE_CUIPO IS NULL OR MI_COD_FUENTE_CUIPO = 'null' THEN
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Fuente para la cuenta: ' || MI_CUENTA||CHR(13);
            ELSE
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '009 No esta configurado como detalle  o el codigo ' || MI_COD_FUENTE_CUIPO || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                             
            END IF;
            IF  MI_COD_CODIGOCCPETREGA =  MI_COD_CODIGOCCPETREGACOMP  AND  MI_COD_CODIGOCCPETREGA IS NOT NULL THEN 
            MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ' ,CODIGOCCPETREGA   ';
            MI_CUIPOVALORES:= MI_CUIPOVALORES || '  ,''' || MI_COD_CODIGOCCPETREGA ||''' ' ;  
            ELSIF MI_COD_CODIGOCCPETREGA = 'NoDato' OR MI_COD_CODIGOCCPETREGA IS NULL OR MI_COD_CODIGOCCPETREGA = 'null' THEN
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Codigo CCPET Regalias para la cuenta: ' || MI_CUENTA||CHR(13);
            ELSE 
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '010 No esta configurado como detalle  o el codigo ' || MI_COD_CODIGOCCPETREGA || ' No existe  en TIPOCLASIFICADOR '||CHR(13);               
            END IF;
            IF MI_COD_POLITICA_PUBLICA =  MI_COD_POLITICA_PUBLICACOMP AND  MI_COD_POLITICA_PUBLICA IS NOT NULL THEN 
            MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ' ,POLITICA_PUBLICA   ';
            MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_POLITICA_PUBLICA    ||''' ' ;
            ELSIF MI_COD_POLITICA_PUBLICA = 'NoDato' OR MI_COD_POLITICA_PUBLICA IS NULL OR MI_COD_POLITICA_PUBLICA = 'null'THEN
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador Politica Publica para la cuenta: ' || MI_CUENTA||CHR(13);
            ELSE
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '011 No esta configurado como detalle  o el codigo ' || MI_COD_POLITICA_PUBLICA || ' No existe  en TIPOCLASIFICADOR '||CHR(13);                               
            END IF;
            IF  MI_COD_DETALLE_SECTORIAL =  MI_COD_DETALLE_SECTORIALCOMP AND  MI_COD_DETALLE_SECTORIAL IS NOT NULL THEN 
            MI_CAMPOSCUIPO := MI_CAMPOSCUIPO || ' ,DETALLE_SECTORIAL ';
            MI_CUIPOVALORES:= MI_CUIPOVALORES || ' ,''' || MI_COD_DETALLE_SECTORIAL ||''' ' ;  
            ELSIF MI_COD_DETALLE_SECTORIAL = 'NoDato' OR MI_COD_DETALLE_SECTORIAL IS NULL OR MI_COD_DETALLE_SECTORIAL = 'null' THEN
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) ||' No se diligencio el clasificador detalle Sectorial para la cuenta: ' || MI_CUENTA||CHR(13);
            ELSE
            MI_RETORNOAUX :=  MI_RETORNOAUX || CHR(9) || 'El tipo' || '012 No esta configurado como detalle  o el codigo ' || MI_COD_DETALLE_SECTORIAL || ' No existe  en TIPOCLASIFICADOR '||CHR(13);               
            END IF;
            IF MI_RETORNOAUX IS NOT NULL THEN 
            MI_RETORNO := MI_RETORNO || MI_RETORNOAUX || CHR(10) || CHR(13);
            END IF;
            MI_RETORNOAUX :=  '';

            MI_CAMPOS := 'COMPANIA
                  ,ANO
                  ,TIPO_CPTE
                  ,COMPROBANTE
                  ,CONSECUTIVO
                  ,FECHA
                  ,DESCRIPCION
                  ,CUENTA
                  ,VALOR_DEBITO
                  ,VALOR_CREDITO 
                  ,TERCERO
                  ,SUCURSAL
                  ,AUXILIAR                 
                  ,FUENTE_RECURSO
                  ,REFERENCIA
                  ,CENTRO_COSTO
                  ,ANO_AFECT
                  ,TIPO_CPTE_AFECT
                  ,CMPTE_AFECTADO
                  ,CONSECUTIVOPPTO
                  ,TIPOCONTRATO
                  ,NUMEROCONTRATO
                  ';

                  MI_CAMPOS := MI_CAMPOS|| MI_CAMPOSCUIPO||'
                  ,CREATED_BY
                  ,DATE_CREATED';

            MI_VALORES := ''''|| MI_DATOS_COLUMNAS(1) ||'''
                    ,  '||  MI_ANIO ||'
                    ,'''|| MI_DATOS_COLUMNAS(2) ||'''
                    ,  '|| MI_DATOS_COLUMNAS(3) ||'
                    ,  '|| MI_COD_CONSECUTIVO ||'
                    ,'''|| MI_DATOS_COLUMNAS(4) ||'''
                    ,'''|| MI_DATOS_COLUMNAS(5) ||'''
                    ,'''|| MI_DATOS_COLUMNAS(6) ||'''
                    ,  '|| MI_DATOS_COLUMNAS(7) ||'
                    ,  '|| MI_DATOS_COLUMNAS(8) ||'
                    ,'''|| MI_DATOS_COLUMNAS(9) ||'''
                    ,'''|| MI_DATOS_COLUMNAS(10) ||'''
                    ,'''|| MI_DATOS_COLUMNAS(11) ||'''
                    ,'''|| MI_DATOS_COLUMNAS(12) ||'''
                    ,'''|| MI_DATOS_COLUMNAS(13) ||'''
                    ,'''|| MI_DATOS_COLUMNAS(14) ||'''
                    ,'''|| CASE WHEN MI_DATOS_COLUMNAS(15) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(15) END ||'''
                    ,'''|| CASE WHEN MI_DATOS_COLUMNAS(16) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(16) END ||'''
                    ,'''|| CASE WHEN MI_DATOS_COLUMNAS(17) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(17) END ||'''
                    ,'''|| CASE WHEN MI_DATOS_COLUMNAS(18) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(18) END ||'''
                    ,'''|| CASE WHEN MI_DATOS_COLUMNAS(19) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(19) END ||'''
                    ,'''|| CASE WHEN MI_DATOS_COLUMNAS(20) = 'NoDato' THEN 0 ELSE MI_DATOS_COLUMNAS(20) END ||'''';
                    
                 MI_VALORES:=MI_VALORES||MI_CUIPOVALORES ||
                  ','''|| UN_USUARIO ||'''
                  ,SYSDATE';
            
            BEGIN
                SELECT CLASE
                    INTO MI_CLASE
                    FROM TIPO_COMPROBPP
                    WHERE COMPANIA = MI_DATOS_COLUMNAS(1)
                    AND CODIGO = MI_DATOS_COLUMNAS(2);
                EXCEPTION WHEN NO_DATA_FOUND THEN
                BEGIN
                        RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN
                        MI_RETORNO := 'El tipo de comprobante ' || MI_DATOS_COLUMNAS(2) || ' no existe';
                        MI_MSGERROR (1).CLAVE := 'MENSAJE';
                        MI_MSGERROR (1).VALOR := MI_RETORNO;
                        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                                UN_ERROR_COD  => PCK_ERRORES.ERRR_PERSONALIZADO,
                                                UN_TABLAERROR => 'ORDENDECOMPRA',
                                                UN_REEMPLAZOS => MI_MSGERROR);
                END;
            END;

            IF MI_CLASE = 'DIS' THEN
            
                MI_CAMPOS := MI_CAMPOS||'
                      ,TIPO_SOLICITUD
                      ,NUMERO_SOLICITUD';
                      
                MI_VALORES:=MI_VALORES||'
                      ,'''|| CASE WHEN MI_DATOS_COLUMNAS(21) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(21) END ||'''
                      ,'''|| CASE WHEN MI_DATOS_COLUMNAS(22) = 'NoDato' THEN '' ELSE MI_DATOS_COLUMNAS(22) END ||'''';
            END IF;
            
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
                MI_MSGERROR(1).VALOR := MI_DATOS_COLUMNAS(2);
                MI_MSGERROR(2).CLAVE := 'NROCPTE';
                MI_MSGERROR(2).VALOR := MI_DATOS_COLUMNAS(3);
                MI_MSGERROR(3).CLAVE := 'CONSECUTIVO';
                MI_MSGERROR(3).VALOR := MI_DATOS_COLUMNAS(13);
                MI_MSGERROR(4).CLAVE := 'CUENTA';
                MI_MSGERROR(4).VALOR := MI_DATOS_COLUMNAS(8);
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD => SQLCODE, UN_ERROR_COD => PCK_ERRORES.ERR_PPTO_CREARDETREDUCCION, UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL', UN_REEMPLAZOS => MI_MSGERROR );
        END;

        MI_COD_CONSECUTIVO := MI_COD_CONSECUTIVO + 1;
    
    END LOOP CREAR_DETALLES;

END PR_CARGAR_MOV_PPTALES;

PROCEDURE PR_ACT_ARBOL_APROINI
     /*  
        NAME              : PR_ACT_ARBOL_APROINI
        AUTHORS           : GERMAN DAVID ROJAS G
        AUTHOR MIGRACION  : GERMAN DAVID ROJAS G
        DATE MIGRADOR     : 26/08/2025
        TIME              : 09:55 AM
        SOURCE MODULE     : 
        MODIFIER          : 
        DATE MODIFIED     : 
        TIME              :
        DESCRIPTION       : CC_2315: Permite actualizar la información de las apropiaciones iniciales.
        PARAMETERS        :
        MODIFICATIONS     :

        @NAME:PR_ACT_ARBOL_APROINI
*/    
(  UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA 
  ,UN_ANIO           IN PCK_SUBTIPOS.TI_ANIO
)
AS
    MI_CONDICION      VARCHAR2(500);
    MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;

BEGIN
      --ACTUALIZAR DE PLAN PRESUPUESTAL A APROPIACIONES INICIALES.
      FOR RS IN (
                  SELECT COMPANIA,
                        ANO,
                        CODIGO,
                        CODIGOCCPET,
                        PROGRAMA,
                        CODIGOUNIDADEJE,
                        CODIGOBPIN,
                        DETALLE_SECTORIAL
                  FROM PLAN_PRESUPUESTAL
                  WHERE COMPANIA = UN_COMPANIA
                  AND ANO        = UN_ANIO
                  AND (NVL(MOVIMIENTO,0) + NVL(MAN_CEN_CTO,0) + NVL(MAN_AUX_TER,0) +
                       NVL(MAN_AUX_GEN,0) + NVL(MAN_AUX_REF,0) + NVL(MAN_AUX_FUE,0)) <> 0)

      LOOP

      MI_CAMPOS := 'CODIGO_CCPET     = NVL(''' || RS.CODIGOCCPET       || ''', NVL(CODIGO_CCPET,0)),' ||
                   'PROGRAMA         = NVL(''' || RS.PROGRAMA          || ''', NVL(PROGRAMA,0)),' ||
                   'CODIGOUNIDADEJE  = NVL(''' || RS.CODIGOUNIDADEJE   || ''', NVL(CODIGOUNIDADEJE,0)),' ||
                   'CODIGOBPIN       = NVL(''' || RS.CODIGOBPIN        || ''', NVL(CODIGOBPIN,0)),' ||
                   'DETALLESECTORIAL = NVL(''' || RS.DETALLE_SECTORIAL || ''', NVL(DETALLESECTORIAL,0))';


      MI_CONDICION := 'COMPANIA     = ''' || RS.COMPANIA    || '''
                       AND ANO      =   ' || RS.ANO         || '
                       AND CODIGO   = ''' || RS.CODIGO      || ''' ';

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'APROPIACIONESINICIALES'
                                                             ,UN_ACCION    => 'M'
                                                             ,UN_CAMPOS    => MI_CAMPOS
                                                             ,UN_CONDICION => MI_CONDICION);

    END LOOP;
    
    --ACTUALIZAR DE PLAN PRESUPUESTAL A DETALLE PPTAL PARA MOVIMIENTO DE MODIFICACION.
    FOR RS IN (
            SELECT PLAN_PRESUPUESTAL.COMPANIA,
                   PLAN_PRESUPUESTAL.ANO,
                   PLAN_PRESUPUESTAL.CODIGO,
                   DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE,
                   DETALLE_COMPROBANTE_PPTAL.COMPROBANTE,
                   DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO,
                   PLAN_PRESUPUESTAL.CODIGOCCPET,
                   PLAN_PRESUPUESTAL.PROGRAMA,
                   PLAN_PRESUPUESTAL.CODIGOUNIDADEJE,
                   PLAN_PRESUPUESTAL.CODIGOBPIN,
                   PLAN_PRESUPUESTAL.DETALLE_SECTORIAL
              FROM PLAN_PRESUPUESTAL
              INNER JOIN DETALLE_COMPROBANTE_PPTAL
              ON DETALLE_COMPROBANTE_PPTAL.COMPANIA = PLAN_PRESUPUESTAL.COMPANIA
              AND DETALLE_COMPROBANTE_PPTAL.ANO = PLAN_PRESUPUESTAL.ANO
              AND DETALLE_COMPROBANTE_PPTAL.CUENTA = PLAN_PRESUPUESTAL.CODIGO
              INNER JOIN TIPO_COMPROBPP
              ON DETALLE_COMPROBANTE_PPTAL.COMPANIA = TIPO_COMPROBPP.COMPANIA
              AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE = TIPO_COMPROBPP.CODIGO
             WHERE PLAN_PRESUPUESTAL.COMPANIA = UN_COMPANIA
               AND PLAN_PRESUPUESTAL.ANO      = UN_ANIO
               AND TIPO_COMPROBPP.CLASE IN ('ADC','TRA','RED')
               AND (NVL(MOVIMIENTO,0) + NVL(MAN_CEN_CTO,0) + NVL(MAN_AUX_TER,0) +
                    NVL(MAN_AUX_GEN,0) + NVL(MAN_AUX_REF,0) + NVL(MAN_AUX_FUE,0)) <> 0)
    LOOP
            MI_CAMPOS := 'CODIGO_CCPET      = NVL(''' || RS.CODIGOCCPET       || ''', NVL(CODIGO_CCPET,0)),' ||
                         'PROGRAMA          = NVL(''' || RS.PROGRAMA          || ''', NVL(PROGRAMA,0)),' ||
                         'CODIGOUNIDADEJE   = NVL(''' || RS.CODIGOUNIDADEJE   || ''', NVL(CODIGOUNIDADEJE,0)),' ||
                         'CODIGO_BPIN       = NVL(''' || RS.CODIGOBPIN        || ''', NVL(CODIGO_BPIN,0)),' ||
                         'DETALLE_SECTORIAL = NVL(''' || RS.DETALLE_SECTORIAL || ''', NVL(DETALLE_SECTORIAL,0))';
            
            MI_CONDICION := 'COMPANIA           = ''' || RS.COMPANIA    || '''
                             AND ANO            =   ' || RS.ANO         || '
                             AND TIPO_CPTE      = ''' || RS.TIPO_CPTE   || '''
                             AND COMPROBANTE    =   ' || RS.COMPROBANTE || '
                             AND CONSECUTIVO    =   ' || RS.CONSECUTIVO || '
                             ';
            
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'DETALLE_COMPROBANTE_PPTAL'
                                                             ,UN_ACCION    => 'M'
                                                             ,UN_CAMPOS    => MI_CAMPOS
                                                             ,UN_CONDICION => MI_CONDICION);
    END LOOP;
    
    --VALIDAR APROPIACIONES CONTRA DETALLES.
    FOR RS IN (
    SELECT DETALLE_COMPROBANTE_PPTAL.COMPANIA, 
    DETALLE_COMPROBANTE_PPTAL.ANO, 
    DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE, 
    DETALLE_COMPROBANTE_PPTAL.COMPROBANTE, 
    DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO, 
    DETALLE_COMPROBANTE_PPTAL.CUENTA,
    APROPIACIONESINICIALES.CODIGO_CCPET,
    APROPIACIONESINICIALES.PROGRAMA,
    APROPIACIONESINICIALES.CODIGOUNIDADEJE,
    APROPIACIONESINICIALES.CODIGOBPIN,
    APROPIACIONESINICIALES.DETALLESECTORIAL
    FROM APROPIACIONESINICIALES
    INNER JOIN DETALLE_COMPROBANTE_PPTAL
    ON APROPIACIONESINICIALES.COMPANIA = DETALLE_COMPROBANTE_PPTAL.COMPANIA
    AND APROPIACIONESINICIALES.ANO = DETALLE_COMPROBANTE_PPTAL.ANO
    AND APROPIACIONESINICIALES.CODIGO = DETALLE_COMPROBANTE_PPTAL.CUENTA
    AND APROPIACIONESINICIALES.CENTRO_COSTO = DETALLE_COMPROBANTE_PPTAL.CENTRO_COSTO
    AND APROPIACIONESINICIALES.AUXILIAR = DETALLE_COMPROBANTE_PPTAL.AUXILIAR
    AND APROPIACIONESINICIALES.REFERENCIA = DETALLE_COMPROBANTE_PPTAL.REFERENCIA
    AND APROPIACIONESINICIALES.FUENTE_RECURSO = DETALLE_COMPROBANTE_PPTAL.FUENTE_RECURSO
    INNER JOIN TIPO_COMPROBPP
              ON DETALLE_COMPROBANTE_PPTAL.COMPANIA = TIPO_COMPROBPP.COMPANIA
              AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE = TIPO_COMPROBPP.CODIGO
    WHERE APROPIACIONESINICIALES.COMPANIA = UN_COMPANIA 
    AND APROPIACIONESINICIALES.ANO        = UN_ANIO
    AND TIPO_COMPROBPP.CLASE IN ('ADC','TRA','RED')
    AND (DECODE(APROPIACIONESINICIALES.CODIGO_CCPET     , DETALLE_COMPROBANTE_PPTAL.CODIGO_CCPET     , 0, 1) +
          DECODE(APROPIACIONESINICIALES.PROGRAMA         , DETALLE_COMPROBANTE_PPTAL.PROGRAMA         , 0, 1) +
          DECODE(APROPIACIONESINICIALES.CODIGOUNIDADEJE  , DETALLE_COMPROBANTE_PPTAL.CODIGOUNIDADEJE  , 0, 1) +
          DECODE(APROPIACIONESINICIALES.CODIGOBPIN       , DETALLE_COMPROBANTE_PPTAL.CODIGO_BPIN      , 0, 1) +
          DECODE(APROPIACIONESINICIALES.DETALLESECTORIAL , DETALLE_COMPROBANTE_PPTAL.DETALLE_SECTORIAL, 0, 1)) > 0)
    LOOP
            
            MI_CAMPOS := 'CODIGO_CCPET      = NVL(''' || RS.CODIGO_CCPET      || ''', NVL(CODIGO_CCPET,0)),' ||
                         'PROGRAMA          = NVL(''' || RS.PROGRAMA          || ''', NVL(PROGRAMA,0)),' ||
                         'CODIGOUNIDADEJE   = NVL(''' || RS.CODIGOUNIDADEJE   || ''', NVL(CODIGOUNIDADEJE,0)),' ||
                         'CODIGO_BPIN       = NVL(''' || RS.CODIGOBPIN        || ''', NVL(CODIGO_BPIN,0)),' ||
                         'DETALLE_SECTORIAL = NVL(''' || RS.DETALLESECTORIAL  || ''', NVL(DETALLE_SECTORIAL,0))';
            
            MI_CONDICION := 'COMPANIA           = ''' || RS.COMPANIA    || '''
                             AND ANO            =   ' || RS.ANO         || '
                             AND TIPO_CPTE      = ''' || RS.TIPO_CPTE   || '''
                             AND COMPROBANTE    =   ' || RS.COMPROBANTE || '
                             AND CONSECUTIVO    =   ' || RS.CONSECUTIVO || '
                             ';
            
            PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA => 'DETALLE_COMPROBANTE_PPTAL'
                                                             ,UN_ACCION    => 'M'
                                                             ,UN_CAMPOS    => MI_CAMPOS
                                                             ,UN_CONDICION => MI_CONDICION);
            
    END LOOP;
    
END PR_ACT_ARBOL_APROINI;

FUNCTION FC_CARGAR_AUXILIARES(
/*
    NAME              :cargarInterfazporXls
    AUTHORS           : NCARDENAS
    AUTHOR MIGRACION  : NCARDENAS
    DATE MIGRADOR     : 26/03/2026
    TIME              : 2:47
    SOURCE MODULE     : 
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    MODIFICATIONS     :
    DESCRIPTION       : VALIDACIÓN Y CARGUE DE  INFORMACIÓN DE AUXILIARES PRESUPUESTO Y CONTABILIDAD 
    PARAMETERS        :
      --NAME:    cargarInterfazporXls
      --METHOD:  GET
  */
    UN_TABLA                  IN VARCHAR2,
    UN_CADENA                 IN CLOB,
    UN_USUARIO                IN VARCHAR2
  )  RETURN CLOB
AS
MI_DATOS_FILA           PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS       PCK_SYSMAN_UTL.T_SPLIT;
MI_MENSAJE              CLOB;
MI_EXISTE               NUMBER(4);
MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES              CLOB;
MI_RTA                  PCK_SUBTIPOS.TI_ENTERO;
MI_COMPANIA             VARCHAR2(11);   
MI_ANO                  NUMBER(4);
MI_CODIGO               VARCHAR2(25); 
MI_NOMBRE               VARCHAR2(500);
EXISTEVIGCUIPO          PCK_SUBTIPOS.TI_ENTERO_LARGO;
MI_ERRORES              PCK_SUBTIPOS.TI_ENTERO_LARGO:= 0;
MI_ANOSIG               BOOLEAN:= TRUE;
MI_SQL                  VARCHAR2(500);
MI_CODSIG               VARCHAR2(20);
MI_EXISTEMSG            BOOLEAN:= FALSE;
MI_TOTERRORES           PCK_SUBTIPOS.TI_ENTERO_LARGO:= 0;

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
    MI_ERRORES := 0;
    MI_COMPANIA := MI_DATOS_COLUMNAS(1);
    MI_ANO      := MI_DATOS_COLUMNAS(2);
    MI_CODIGO   := MI_DATOS_COLUMNAS(3); 
    MI_NOMBRE   := MI_DATOS_COLUMNAS(4);  
    

  
    -----       VALIDACION CAMPOS OBLIGATORIOS      -----

    IF TRIM(MI_COMPANIA) IS NULL THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '||( RS + 1)|| ' La COMPANIA es NULA, este campo es obligatorio y necesaria para realizar las demás  validaciones.'|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        CONTINUE;
    END IF;

    IF TRIM(MI_ANO) IS NULL THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El ANO  es NULO, este campo es obligatorio y necesaria para realizar las demás  validaciones. '|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        CONTINUE;
    END IF;

    IF TRIM(MI_CODIGO) IS NULL THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El CODIGO es NULO, este campo es obligatorio y necesaria para realizar las demás  validaciones. '|| CHR(10);
        MI_TOTERRORES:= MI_TOTERRORES + 1;
        CONTINUE;
    END IF;

    IF TRIM(MI_NOMBRE) IS NULL THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El NOMBRE es NULO, este campo es obligatorio.'|| CHR(10);
        MI_ERRORES := MI_ERRORES + 1;
        MI_TOTERRORES:= MI_TOTERRORES + 1;
    END IF;

              -----       VALIDACION DE EXISTENCIA DE INFORMACION PARA EL AÑO SIGUIENTE       -----
    BEGIN                                              
        MI_SQL :='SELECT COUNT(*)
                  FROM ' || UN_TABLA || ' 
                  WHERE COMPANIA = :1 
                        AND ANO = :2';
        EXECUTE IMMEDIATE MI_SQL
        INTO MI_EXISTE
        USING MI_COMPANIA, (MI_ANO + 1);
    
        IF MI_EXISTE = 0 THEN
            MI_ERRORES := MI_ERRORES + 1;
            MI_TOTERRORES:= MI_TOTERRORES + 1;
            MI_ANOSIG := FALSE;
            MI_EXISTEMSG := TRUE; 
            MI_MENSAJE := MI_MENSAJE  || ' En la Fila: '|| (RS + 1) || ' No EXISTE información de '|| UN_TABLA || ' para el año siguiente ' || '(' || (MI_ANO + 1) || ')' || ' por lo tanto no se puede validar la existencia de la columna CODIGO EQUIVALENTE SGTE VIGENCIA.Por favor revise la columna año ' ||  CHR(10);
               
        END IF;
        
        
    END;
    
    -----       VALIDACIONES REUQERIDAS EN EL CONTROLDE CAMBIOS     -----
    BEGIN                                              
        SELECT NUMERO
        INTO MI_EXISTE
        FROM ANO 
        WHERE COMPANIA = MI_COMPANIA 
        AND NUMERO = MI_ANO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El código' || MI_CODIGO || ' NO existe el año. ' ||  CHR(10);
        MI_ERRORES := MI_ERRORES + 1;
        MI_TOTERRORES:= MI_TOTERRORES + 1;
    END;
       
    IF LENGTH(MI_CODIGO) > 20 THEN 
        MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El código '|| MI_CODIGO ||' Supera el tamaño requerido. ' || CHR(10);
        MI_ERRORES := MI_ERRORES + 1;
        MI_TOTERRORES:= MI_TOTERRORES + 1;
    END IF;
       
   
-----       Validaciones Especificas        -----
                                            
    IF 'FUENTE_RECURSOS' = UN_TABLA THEN
        MI_CODSIG := MI_DATOS_COLUMNAS(13);
        IF TRIM(MI_DATOS_COLUMNAS(8)) IS NULL THEN
            MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El código '|| MI_CODIGO || ' El campo SIN SITUACIÓN FONDOS es NULO, este es obligatorio.'|| CHR(10);
            MI_ERRORES := MI_ERRORES + 1;
            MI_TOTERRORES:= MI_TOTERRORES + 1;
            END IF;
       
        IF MI_DATOS_COLUMNAS(9) IS NOT NULL THEN

            SELECT COUNT(*) AS TOTAL
            INTO EXISTEVIGCUIPO
            FROM TIPOCLASIFICADOR 
            WHERE COMPANIA =  MI_COMPANIA AND ANO = MI_ANO AND CLASECLASIFICADOR = '009' AND CODIGO = MI_DATOS_COLUMNAS(9);
            
            IF EXISTEVIGCUIPO = 0 THEN
                MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El código ' || MI_CODIGO ||' La VIGENCIA ACTUAL EQUIVALENTE CUIPO no existe esta fuente en  tipo clasificador. Por favor configúrela  en la ruta: Archivos<<Configuración clasificadores. ' ||  CHR(10);
                MI_ERRORES := MI_ERRORES + 1;
                MI_TOTERRORES:= MI_TOTERRORES + 1;
            END IF;
            
        END IF;
        IF MI_DATOS_COLUMNAS(10) IS NOT NULL THEN
           
            SELECT COUNT(*) AS TOTAL
            INTO EXISTEVIGCUIPO
            FROM TIPOCLASIFICADOR 
            WHERE COMPANIA =  MI_COMPANIA AND ANO = MI_ANO AND CLASECLASIFICADOR = '009' AND CODIGO = MI_DATOS_COLUMNAS(10);
            
            IF EXISTEVIGCUIPO = 0 THEN
                MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El código ' || MI_CODIGO ||' La RESERVA APROPIACIÓN EQUIVALENTE CUIPO no existe esta fuente en  tipo clasificador. Por favor configúrela  en la ruta: Archivos<<Configuración clasificadores. ' || CHR(10);
                MI_ERRORES := MI_ERRORES + 1;
                MI_TOTERRORES:= MI_TOTERRORES + 1;
            END IF;
        END IF;
        IF MI_DATOS_COLUMNAS(11) IS NOT NULL THEN
             
            SELECT COUNT(*) AS TOTAL
            INTO EXISTEVIGCUIPO
            FROM TIPOCLASIFICADOR 
            WHERE COMPANIA =  MI_COMPANIA AND ANO = MI_ANO AND CLASECLASIFICADOR = '009' AND CODIGO = MI_DATOS_COLUMNAS(11);
            
            IF EXISTEVIGCUIPO = 0 THEN
                MI_MENSAJE := MI_MENSAJE || ' En la Fila: '||(RS + 1) || ' El código ' || MI_CODIGO || ' La RESERVA CAJA EQUIVALENTE CUIPO no existe esta fuente en  tipo clasificador. Por favor configúrela  en la ruta: Archivos<<Configuración clasificadores. ' ||  CHR(10); 
                MI_ERRORES := MI_ERRORES + 1;
                MI_TOTERRORES:= MI_TOTERRORES + 1;
            END IF;

        END IF;
    END IF;

    -----       Validaciones CENTRO_COSTOS      -----
    IF 'CENTRO_COSTO' = UN_TABLA THEN
        MI_CODSIG := MI_DATOS_COLUMNAS(10);
        IF TRIM(MI_DATOS_COLUMNAS(5)) IS NULL THEN
            MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El código ' || MI_CODIGO || ' La columna TIPO es obligatoria. ' || CHR(10);
            MI_ERRORES := MI_ERRORES + 1;
            MI_TOTERRORES:= MI_TOTERRORES + 1;
        END IF;
        
        IF TRIM(MI_DATOS_COLUMNAS(7)) IS NULL THEN
            MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El código ' || MI_CODIGO || ' La columna MOVIMIENTO es obligatoria. ' ||  CHR(10);
            MI_ERRORES := MI_ERRORES + 1;
            MI_TOTERRORES:= MI_TOTERRORES + 1;
        END IF;
        
        IF TRIM(MI_DATOS_COLUMNAS(8)) IS NULL THEN
            MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El código ' || MI_CODIGO || ' La columna ACTIVO es obligatoria. ' ||  CHR(10);
            MI_ERRORES := MI_ERRORES + 1;
            MI_TOTERRORES:= MI_TOTERRORES + 1;
        END IF;
        
        IF TRIM(MI_DATOS_COLUMNAS(9)) IS NULL THEN
            MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El código ' || MI_CODIGO || ' La columna DISTRIBUIR es obligatoria. ' || CHR(10);
            MI_ERRORES := MI_ERRORES + 1;
            MI_TOTERRORES:= MI_TOTERRORES + 1;
        END IF;
        
    END IF; 

    ------      Validaciones REFERENCIA O AUXILIAR_GENERAL      -----
    IF 'REFERENCIA' = UN_TABLA  or 'AUXILIAR' = UN_TABLA  THEN
        MI_CODSIG := MI_DATOS_COLUMNAS(6);
        IF TRIM(MI_DATOS_COLUMNAS(5)) IS NULL THEN
            MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El código ' || MI_CODIGO || ' La columna MOVIMIENTO es obligatoria. ' || CHR(10);
            MI_ERRORES := MI_ERRORES + 1;
            MI_TOTERRORES:= MI_TOTERRORES + 1;
        END IF;
    END IF;

    

    IF MI_ANOSIG AND MI_CODSIG IS NOT NULL  THEN
   
       MI_SQL :='SELECT COUNT(*)
                  FROM ' || UN_TABLA || ' 
                  WHERE COMPANIA = :1 
                        AND ANO = :2 
                        AND CODIGO = :3';
        EXECUTE IMMEDIATE MI_SQL
        INTO MI_EXISTE
        USING MI_COMPANIA, (MI_ANO + 1),MI_CODSIG;
    
        IF MI_EXISTE = 0 THEN
            MI_MENSAJE := MI_MENSAJE || ' En la Fila: '|| (RS + 1) || ' El código ' || MI_CODIGO || ' En la columna CODIGO EQUIVALENTE SGTE VIGENCIA este codigo NO EXISTE en el años siguiente ' || '(' || (MI_ANO + 1) || ')' || CHR(10);
             MI_ERRORES := MI_ERRORES + 1;
             MI_TOTERRORES:= MI_TOTERRORES + 1;
        END IF;    
    END IF;

    IF MI_ERRORES > 0 THEN
        CONTINUE;
    END IF;
    
   

    -----       Cargue de Información       ------

    IF 'FUENTE_RECURSOS'  = UN_TABLA THEN 
        MI_VALORES := 'INSERT (COMPANIA, ANO, CODIGO, NOMBRE, TIPO, CODIGO_DNP, CODIGO_SIA,IND_SINSITUACIONFONDOS, EQUIVALENTECUIPO, EQUIVALENTEAPROPIACION, EQUIVALENTECAJA, CODIGO_CLEOPATRA,EQUIVALENTE_SIG_VIG ) 
                            VALUES (VISTA.COMPANIA, VISTA.ANO, VISTA.CODIGO, VISTA.NOMBRE, VISTA.TIPO, VISTA.CODIGO_DNP, VISTA.CODIGO_SIA, VISTA.IND_SINSITUACIONFONDOS, VISTA.EQUIVALENTECUIPO, VISTA.EQUIVALENTEAPROPIACION, VISTA.EQUIVALENTECAJA, VISTA.CODIGO_CLEOPATRA,VISTA.EQUIVALENTE_SIG_VIG)';
        MI_CONDICION:='SELECT  '''|| MI_DATOS_COLUMNAS(1)|| ''' COMPANIA, '''|| MI_DATOS_COLUMNAS(2)|| ''' ANO,
        '''|| MI_DATOS_COLUMNAS(3)|| ''' CODIGO, '''|| MI_DATOS_COLUMNAS(4)|| ''' NOMBRE,
        '''|| MI_DATOS_COLUMNAS(5)|| ''' TIPO,
        '''|| MI_DATOS_COLUMNAS(6)|| ''' CODIGO_DNP,
        '''|| MI_DATOS_COLUMNAS(7)|| ''' CODIGO_SIA,
        '''|| MI_DATOS_COLUMNAS(8)|| ''' IND_SINSITUACIONFONDOS,
        '''|| MI_DATOS_COLUMNAS(9)|| ''' EQUIVALENTECUIPO,
        '''|| MI_DATOS_COLUMNAS(10)|| ''' EQUIVALENTEAPROPIACION,
        '''|| MI_DATOS_COLUMNAS(11)|| ''' EQUIVALENTECAJA,
        '''|| MI_DATOS_COLUMNAS(12)|| ''' CODIGO_CLEOPATRA,
        '''|| MI_DATOS_COLUMNAS(13)|| ''' EQUIVALENTE_SIG_VIG FROM DUAL';
    ELSIF 'CENTRO_COSTO' = UN_TABLA THEN 
        MI_VALORES   :='INSERT (COMPANIA, ANO, CODIGO, NOMBRE, TIPO, CATEGORIAGASTO, MOVIMIENTO, ACTIVOCENTRO, DISTRIBUIR,EQUIVALENTE_SIG_VIG)
                    VALUES (VISTA.COMPANIA, VISTA.ANO, VISTA.CODIGO, VISTA.NOMBRE, VISTA.TIPO, VISTA.CATEGORIAGASTO, VISTA.MOVIMIENTO, VISTA.ACTIVOCENTRO, VISTA.DISTRIBUIR,VISTA.EQUIVALENTE_SIG_VIG)';
        MI_CONDICION:='SELECT  '''|| MI_DATOS_COLUMNAS(1)|| ''' COMPANIA, '''|| MI_DATOS_COLUMNAS(2)|| ''' ANO,
        '''|| MI_DATOS_COLUMNAS(3)|| ''' CODIGO, '''|| MI_DATOS_COLUMNAS(4)|| ''' NOMBRE,
        '''|| MI_DATOS_COLUMNAS(5)|| ''' TIPO,
        '''|| MI_DATOS_COLUMNAS(6)|| ''' CATEGORIAGASTO,
        '''|| MI_DATOS_COLUMNAS(7)|| ''' MOVIMIENTO,
        '''|| MI_DATOS_COLUMNAS(8)|| ''' ACTIVOCENTRO,
        '''|| MI_DATOS_COLUMNAS(9)|| ''' DISTRIBUIR,
        '''|| MI_DATOS_COLUMNAS(10)|| ''' EQUIVALENTE_SIG_VIG FROM DUAL';
    ELSIF 'REFERENCIA'  = UN_TABLA OR 'AUXILIAR'  = UN_TABLA THEN 
        MI_VALORES   :='INSERT (COMPANIA, ANO, CODIGO, NOMBRE, MOVIMIENTO,EQUIVALENTE_SIG_VIG)
                        VALUES (VISTA.COMPANIA, VISTA.ANO, VISTA.CODIGO, VISTA.NOMBRE, VISTA.MOVIMIENTO,VISTA.EQUIVALENTE_SIG_VIG)';
        MI_CONDICION:='SELECT '''|| MI_DATOS_COLUMNAS(1)|| ''' COMPANIA, '''|| MI_DATOS_COLUMNAS(2)|| ''' ANO,
        '''|| MI_DATOS_COLUMNAS(3)|| ''' CODIGO, '''|| MI_DATOS_COLUMNAS(4)|| ''' NOMBRE,
        '''|| MI_DATOS_COLUMNAS(5)|| ''' MOVIMIENTO,
        '''|| MI_DATOS_COLUMNAS(6)|| ''' EQUIVALENTE_SIG_VIG FROM DUAL';

    END IF;
        
    BEGIN

        MI_CAMPOS   := 'TABLA.COMPANIA = VISTA.COMPANIA
                        AND TABLA.CODIGO = VISTA.CODIGO
                        AND TABLA.ANO = VISTA.ANO';
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

END FC_CARGAR_AUXILIARES;

END PCK_PRESUPUESTO_COM4;