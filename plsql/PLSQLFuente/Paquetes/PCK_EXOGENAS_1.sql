create or replace PACKAGE BODY PCK_EXOGENAS AS

PROCEDURE PR_CONCEPTOSPORFORMATO
(
  /*
    NAME              : PR_CONCEPTOSPORFORMATO 
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : JOSÉ PASCUAL GÓMEZ BLANCO
    DATE MIGRADOR     : 19/03/2019
    TIME              : 12:30 PM
    SOURCE MODULE     : Entes de Control - Exogenas
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Procedimiento que actualiza el concepto configurado a las cuentas que tengan el indicador de MOSTRARF1001
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : 
    @NAME:  actualizarConceptoExogena
    @METHOD:  PUT
  */
	UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA, 
	UN_ANO 		IN PCK_SUBTIPOS.TI_ANIO,
	UN_FORMATO 	IN VARCHAR2,
	UN_USUARIO 	IN PCK_SUBTIPOS.TI_USUARIO
)
	AS 
    MI_TABLA 		  PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS 		  PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION 	  PCK_SUBTIPOS.TI_CONDICION;
    MI_MSGERROR       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGENOEXISTE  PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_PREFIJO        VARCHAR2(20);
BEGIN
    -- SE IDENTIFICA SI ES NACIONAL O DISTRITAL A TRAVES DEL PREFIJO DEL INFORME
    MI_PREFIJO:='';
    SELECT PREFIJO
    INTO MI_PREFIJO
    FROM FORMATOS
    WHERE CODIGO = UN_FORMATO;

    MI_TABLA := 'DETALLE_COMPROBANTE_CNT'; 
    --SE LIMPIA LA CONFIGURACION QUE PUEDE ESTAR ERRADA
    BEGIN
        BEGIN        
            MI_CAMPOS       := 'CONCEPTO_DIST         = NULL ,
                                CONCEPTO_EX           = NULL ,
                                FORMATO_CONCEPTO_EX   = NULL ,
                                FORMATO_CONCEPTO_DIST = NULL ,
                                MODIFIED_BY       = '''|| UN_USUARIO ||''' , 
                                DATE_MODIFIED     = SYSDATE ';                    
            MI_CONDICION := ' COMPANIA = ''' || UN_COMPANIA || '''
                          AND ANO      = '   || UN_ANO || ' 
                          AND (CONCEPTO_DIST         IS NOT NULL
                            OR CONCEPTO_EX           IS NOT NULL
                            OR FORMATO_CONCEPTO_EX   IS NOT NULL
                            OR FORMATO_CONCEPTO_DIST IS NOT NULL)';
            PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'DETALLE_COMPROBANTE_CNT', 
                                                 UN_ACCION    => 'M', 
                                                 UN_CAMPOS    => MI_CAMPOS, 
                                                 UN_CONDICION => MI_CONDICION );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD   => SQLCODE, 
                                    UN_ERROR_COD => PCK_ERRORES.ERR_CONTA_LIMPIAEXOGENA);
    END;        
    --SE ACTUALIZA EL CONCEPTO DE LAS RETENCIONES EN BASE A LA CONFIGURACION DE LAS CUENTAS DE CLASE IMPUESTO
    BEGIN
        BEGIN
            MI_MERGEUSING:= '   SELECT DETALLE_COMPROBANTE_CNT.COMPANIA,
                                       DETALLE_COMPROBANTE_CNT.ANO,
                                       DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                                       DETALLE_COMPROBANTE_CNT.COMPROBANTE,
                                       DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                                       DETALLE_COMPROBANTE_CNT.CUENTA,
                                       CONFIGURACION_EXOGENA.CONCEPTO,
                                       CONFIGURACION_EXOGENA.FORMATO
                                 FROM DETALLE_COMPROBANTE_CNT INNER JOIN PLAN_CONTABLE
                                       ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
                                      AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO             
                                      AND DETALLE_COMPROBANTE_CNT.CUENTA   = PLAN_CONTABLE.CODIGO   
                                 INNER JOIN TIPO_COMPROBANTE 
                                       ON DETALLE_COMPROBANTE_CNT.COMPANIA  = TIPO_COMPROBANTE.COMPANIA
                                      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
                                 INNER JOIN TERCERO 
                                       ON TERCERO.COMPANIA  = DETALLE_COMPROBANTE_CNT.COMPANIA
                                      AND TERCERO.SUCURSAL  = DETALLE_COMPROBANTE_CNT.SUCURSAL
                                      AND TERCERO.NIT       = DETALLE_COMPROBANTE_CNT.TERCERO
                                 INNER JOIN CONFIGURACION_EXOGENA
                                       ON CONFIGURACION_EXOGENA.COMPANIA = PLAN_CONTABLE.COMPANIA
                                      AND CONFIGURACION_EXOGENA.ANO      = PLAN_CONTABLE.ANO             
                                      AND CONFIGURACION_EXOGENA.CUENTA   = PLAN_CONTABLE.CODIGO     
                                 WHERE PLAN_CONTABLE.COMPANIA = ''' || UN_COMPANIA || '''
                                   AND PLAN_CONTABLE.ANO      = '   || UN_ANO || '
                                   AND CONFIGURACION_EXOGENA.FORMATO = ''' || UN_FORMATO || '''
                                   AND PLAN_CONTABLE.CLASECUENTA IN(''I'')
                                   AND PLAN_CONTABLE.IVACOMUN                 IN(0)
                                   AND PLAN_CONTABLE.IVASIMPLIFICADO          IN(0)
                                   AND PLAN_CONTABLE.MOSTRARF1001         NOT IN(0)
                                   AND TERCERO.IND_PAGO_RETENCION             IN(0)
                                   AND TIPO_COMPROBANTE.IND_REPORTAR_1001 NOT IN(0)
                                   AND TIPO_COMPROBANTE.CLASE_CONTABLE NOT IN(''Z'')';
            MI_MERGEENLACE := 'TABLA.COMPANIA       = VISTA.COMPANIA 
                           AND TABLA.ANO            = VISTA.ANO 
                           AND TABLA.TIPO_CPTE      = VISTA.TIPO_CPTE 
                           AND TABLA.COMPROBANTE    = VISTA.COMPROBANTE
                           AND TABLA.CONSECUTIVO    = VISTA.CONSECUTIVO';
            MI_MERGEEXISTE := 'UPDATE SET ' || CASE WHEN MI_PREFIJO = 'N' 
                                                    THEN 'TABLA.CONCEPTO_EX'         
                                                    ELSE CASE WHEN MI_PREFIJO = 'D' 
                                                              THEN 'TABLA.CONCEPTO_DIST'  
                                                              ELSE ''
                                                              END
                                                    END || ' = VISTA.CONCEPTO,
                                          ' || CASE WHEN MI_PREFIJO = 'N' 
                                                    THEN 'TABLA.FORMATO_CONCEPTO_EX'         
                                                    ELSE CASE WHEN MI_PREFIJO = 'D' 
                                                              THEN 'TABLA.FORMATO_CONCEPTO_DIST'  
                                                              ELSE ''
                                                              END
                                                    END || ' = VISTA.FORMATO  ';
            PCK_DATOS.GL_RTA :=  PCK_DATOS.FC_ACME(  UN_TABLA       => 'DETALLE_COMPROBANTE_CNT',
                                                     UN_ACCION      => 'MM',
                                                     UN_MERGEUSING  => MI_MERGEUSING,
                                                     UN_MERGEENLACE => MI_MERGEENLACE,
                                                     UN_MERGEEXISTE => MI_MERGEEXISTE); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD   => SQLCODE, 
                                    UN_ERROR_COD => PCK_ERRORES.ERR_CONTA_LIMPIAEXOGENA);        
    END;
    /*
    * ACTUALIZAR PAGOS DE ACUERDO A LAS RETENCIONES CUANDO LA RETENCIÓN ESTA DENTRO DEL MISMO COMPROBANTE     
    * SE INCLUYEN LAS TIPO IMPUESTOS PUES LOS VALORES DEL IVA DEBEN ESTAR CON LA CONFIGURACIÓN DE LA RETENCIÓN 
    * PRINCIPAL
    */
    BEGIN
        BEGIN
            MI_MERGEUSING:= 'SELECT DETA.COMPANIA, 
                                    DETA.ANO, 
                                    DETA.TIPO_CPTE, 
                                    DETA.COMPROBANTE, 
                                    DETA.CONSECUTIVO, 
                                    IMPUESTO.CONCEPTO_EX CONCEPTO,
                                    IMPUESTO.FORMATO
                             FROM DETALLE_COMPROBANTE_CNT DETA INNER JOIN PLAN_CONTABLE
                               ON DETA.COMPANIA = PLAN_CONTABLE.COMPANIA
                              AND DETA.ANO      = PLAN_CONTABLE.ANO             
                              AND DETA.CUENTA   = PLAN_CONTABLE.CODIGO   
                              INNER JOIN    
                                 (SELECT COMPANIA, ANO, TIPO_CPTE, COMPROBANTE, MAX(' || CASE WHEN MI_PREFIJO = 'N' 
                                                                                         THEN 'CONCEPTO_EX'         
                                                                                         ELSE CASE WHEN MI_PREFIJO = 'D' 
                                                                                                   THEN 'CONCEPTO_DIST'  
                                                                                                   ELSE ''
                                                                                                   END
                                                                                         END || ') CONCEPTO_EX,
                                                                             MAX(' || CASE WHEN MI_PREFIJO = 'N' 
                                                                                           THEN 'FORMATO_CONCEPTO_EX'         
                                                                                           ELSE CASE WHEN MI_PREFIJO = 'D' 
                                                                                                     THEN 'FORMATO_CONCEPTO_DIST'  
                                                                                                     ELSE ''
                                                                                                     END
                                                                                           END || ') FORMATO
                                   FROM DETALLE_COMPROBANTE_CNT
                                   WHERE COMPANIA = ''' || UN_COMPANIA || '''
                                     AND ANO      = '   || UN_ANO || '
                                     AND ' || CASE WHEN MI_PREFIJO = 'N' 
                                              THEN 'CONCEPTO_EX'         
                                              ELSE CASE WHEN MI_PREFIJO = 'D' 
                                                        THEN 'CONCEPTO_DIST'  
                                                        ELSE ''
                                                        END
                                              END || ' IS NOT NULL 
                                   GROUP BY COMPANIA, ANO, TIPO_CPTE, COMPROBANTE             
                                  ) IMPUESTO
                               ON IMPUESTO.COMPANIA    = DETA.COMPANIA
                              AND IMPUESTO.ANO         = DETA.ANO
                              AND IMPUESTO.TIPO_CPTE   = DETA.TIPO_CPTE
                              AND IMPUESTO.COMPROBANTE = DETA.COMPROBANTE  
                             LEFT JOIN (SELECT DISTINCT AFEC.COMPANIA, AFEC.ANO, AFEC.TIPO_CPTE, AFEC.COMPROBANTE,
                                                       AFEC.ANO_AFECT, AFEC.TIPO_CPTE_AFECT, AFEC.COMPROBANTE_AFECT
                                        FROM COMPROBANTE_CNTAFECTADOS AFEC INNER JOIN DETALLE_COMPROBANTE_CNT DET
                                          ON AFEC.COMPANIA    = DET.COMPANIA
                                         AND AFEC.ANO         = DET.ANO
                                         AND AFEC.TIPO_CPTE   = DET.TIPO_CPTE
                                         AND AFEC.COMPROBANTE = DET.COMPROBANTE
                                        INNER JOIN PLAN_CONTABLE PLAN
                                          ON DET.COMPANIA = PLAN.COMPANIA
                                         AND DET.ANO      = PLAN.ANO             
                                         AND DET.CUENTA   = PLAN.CODIGO   
                                        INNER JOIN TIPO_COMPROBANTE 
                                          ON DET.COMPANIA  = TIPO_COMPROBANTE.COMPANIA
                                         AND DET.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
                                       INNER JOIN TERCERO 
                                          ON TERCERO.COMPANIA  = DET.COMPANIA
                                         AND TERCERO.SUCURSAL  = DET.SUCURSAL
                                         AND TERCERO.NIT       = DET.TERCERO                                              
                                        WHERE AFEC.COMPANIA   = ''' || UN_COMPANIA || '''
                                          AND AFEC.ANO        = '   || UN_ANO || '
                                          AND PLAN.MOSTRARF1001                  NOT IN(0)
                                          AND TERCERO.IND_PAGO_RETENCION             IN(0)
                                         AND TIPO_COMPROBANTE.IND_REPORTAR_1001 NOT IN(0)
                                          AND TIPO_COMPROBANTE.CLASE_CONTABLE NOT IN(''Z'')
                                        ) COMPROBANTE_CNTAFECTADOS  
                                   ON IMPUESTO.COMPANIA    = COMPROBANTE_CNTAFECTADOS.COMPANIA
                                  AND IMPUESTO.ANO         = COMPROBANTE_CNTAFECTADOS.ANO
                                  AND IMPUESTO.TIPO_CPTE   = COMPROBANTE_CNTAFECTADOS.TIPO_CPTE
                                  AND IMPUESTO.COMPROBANTE = COMPROBANTE_CNTAFECTADOS.COMPROBANTE
                             WHERE DETA.COMPANIA = ''' || UN_COMPANIA || '''
                               AND DETA.ANO      = '   || UN_ANO || '
                               AND ' || CASE WHEN MI_PREFIJO = 'N' 
                                                    THEN 'DETA.CONCEPTO_EX'         
                                                    ELSE CASE WHEN MI_PREFIJO = 'D' 
                                                              THEN 'DETA.CONCEPTO_DIST'  
                                                              ELSE ''
                                                              END
                                                    END || '  IS NULL
                               AND PLAN_CONTABLE.MOSTRARF1001        NOT IN(0)
                               AND PLAN_CONTABLE.CLASECUENTA         NOT IN(''I'')
                               AND COMPROBANTE_CNTAFECTADOS.COMPANIA IS NULL';
            MI_MERGEENLACE := 'TABLA.COMPANIA       = VISTA.COMPANIA 
                           AND TABLA.ANO            = VISTA.ANO 
                           AND TABLA.TIPO_CPTE      = VISTA.TIPO_CPTE 
                           AND TABLA.COMPROBANTE    = VISTA.COMPROBANTE
                           AND TABLA.CONSECUTIVO    = VISTA.CONSECUTIVO';
            MI_MERGEEXISTE := 'UPDATE SET ' || CASE WHEN MI_PREFIJO = 'N' 
                                                    THEN 'TABLA.CONCEPTO_EX'         
                                                    ELSE CASE WHEN MI_PREFIJO = 'D' 
                                                              THEN 'TABLA.CONCEPTO_DIST'  
                                                              ELSE ''
                                                              END
                                                    END || ' = VISTA.CONCEPTO,
                                          ' || CASE WHEN MI_PREFIJO = 'N' 
                                                    THEN 'TABLA.FORMATO_CONCEPTO_EX'         
                                                    ELSE CASE WHEN MI_PREFIJO = 'D' 
                                                              THEN 'TABLA.FORMATO_CONCEPTO_DIST'  
                                                              ELSE ''
                                                              END
                                                    END || ' = VISTA.FORMATO  ';
            PCK_DATOS.GL_RTA :=  PCK_DATOS.FC_ACME(  UN_TABLA       => 'DETALLE_COMPROBANTE_CNT',
                                                     UN_ACCION      => 'MM',
                                                     UN_MERGEUSING  => MI_MERGEUSING,
                                                     UN_MERGEENLACE => MI_MERGEENLACE,
                                                     UN_MERGEEXISTE => MI_MERGEEXISTE); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD   => SQLCODE, 
                                    UN_ERROR_COD => PCK_ERRORES.ERR_CONTA_LIMPIAEXOGENA);        
    END;

    --ACTUALIZAR PAGOS DE ACUERDO A LAS RETENCIONES CUANDO LA RETENCIÓN ESTA EN UN COMPROBANTE AFECTADO
    BEGIN
        BEGIN
            MI_MERGEUSING:= 'SELECT DETA.COMPANIA, 
                                    DETA.ANO, 
                                    DETA.TIPO_CPTE, 
                                    DETA.COMPROBANTE, 
                                    DETA.CONSECUTIVO, 
                                    DETA.CUENTA, 
                                    COMPROBANTE_CNTAFECTADOS.CONCEPTO_EX CONCEPTO,
                                    COMPROBANTE_CNTAFECTADOS.FORMATO
                             FROM DETALLE_COMPROBANTE_CNT DETA INNER JOIN PLAN_CONTABLE
                               ON DETA.COMPANIA = PLAN_CONTABLE.COMPANIA
                              AND DETA.ANO      = PLAN_CONTABLE.ANO             
                              AND DETA.CUENTA   = PLAN_CONTABLE.CODIGO   
                            INNER JOIN (SELECT  AFEC.COMPANIA, AFEC.ANO, AFEC.TIPO_CPTE, AFEC.COMPROBANTE,
                                                       AFEC.ANO_AFECT, AFEC.TIPO_CPTE_AFECT, AFEC.COMPROBANTE_AFECT,
                                                       MAX(' || CASE WHEN MI_PREFIJO = 'N' 
                                                                    THEN 'CONCEPTO_EX'         
                                                                    ELSE CASE WHEN MI_PREFIJO = 'D' 
                                                                            THEN 'CONCEPTO_DIST'  
                                                                            ELSE ''
                                                                            END
                                                                    END || ') CONCEPTO_EX,
                                                        MAX(' || CASE WHEN MI_PREFIJO = 'N' 
                                                                    THEN 'FORMATO_CONCEPTO_EX'         
                                                                    ELSE CASE WHEN MI_PREFIJO = 'D' 
                                                                                THEN 'FORMATO_CONCEPTO_DIST'  
                                                                                ELSE ''
                                                                                END
                                                                    END || ') FORMATO
                                        FROM COMPROBANTE_CNTAFECTADOS AFEC INNER JOIN DETALLE_COMPROBANTE_CNT DET
                                          ON AFEC.COMPANIA    = DET.COMPANIA
                                         AND AFEC.ANO         = DET.ANO
                                         AND AFEC.TIPO_CPTE   = DET.TIPO_CPTE
                                         AND AFEC.COMPROBANTE = DET.COMPROBANTE
                                        INNER JOIN PLAN_CONTABLE PLAN
                                          ON DET.COMPANIA = PLAN.COMPANIA
                                         AND DET.ANO      = PLAN.ANO             
                                         AND DET.CUENTA   = PLAN.CODIGO   
                                        INNER JOIN TIPO_COMPROBANTE 
                                          ON DET.COMPANIA  = TIPO_COMPROBANTE.COMPANIA
                                         AND DET.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
                                       INNER JOIN TERCERO 
                                          ON TERCERO.COMPANIA  = DET.COMPANIA
                                         AND TERCERO.SUCURSAL  = DET.SUCURSAL
                                         AND TERCERO.NIT       = DET.TERCERO                                              
                                        WHERE AFEC.COMPANIA   = ''' || UN_COMPANIA || '''
                                          AND AFEC.ANO        = '   || UN_ANO || '
                                          AND PLAN.MOSTRARF1001                  NOT IN(0)
                                          AND PLAN.CLASECUENTA                       IN(''I'')
                                          AND TERCERO.IND_PAGO_RETENCION             IN(0)
                                          AND TIPO_COMPROBANTE.IND_REPORTAR_1001 NOT IN(0)
                                          AND TIPO_COMPROBANTE.CLASE_CONTABLE NOT IN(''Z'')
                                        GROUP BY AFEC.COMPANIA,  AFEC.ANO, AFEC.TIPO_CPTE, AFEC.COMPROBANTE,
                                                 AFEC.ANO_AFECT, AFEC.TIPO_CPTE_AFECT, AFEC.COMPROBANTE_AFECT  
                                        ) COMPROBANTE_CNTAFECTADOS  
                                   ON DETA.COMPANIA    = COMPROBANTE_CNTAFECTADOS.COMPANIA
                                  AND DETA.ANO         = COMPROBANTE_CNTAFECTADOS.ANO_AFECT
                                  AND DETA.TIPO_CPTE   = COMPROBANTE_CNTAFECTADOS.TIPO_CPTE_AFECT
                                  AND DETA.COMPROBANTE = COMPROBANTE_CNTAFECTADOS.COMPROBANTE_AFECT
                              INNER JOIN TIPO_COMPROBANTE 
                                      ON DETA.COMPANIA  = TIPO_COMPROBANTE.COMPANIA
                                     AND DETA.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
                              INNER JOIN TERCERO 
                                      ON TERCERO.COMPANIA  = DETA.COMPANIA
                                     AND TERCERO.SUCURSAL  = DETA.SUCURSAL
                                     AND TERCERO.NIT       = DETA.TERCERO 
                             WHERE DETA.COMPANIA = ''' || UN_COMPANIA || '''
                               AND DETA.ANO      = '   || UN_ANO || '
                               AND PLAN_CONTABLE.MOSTRARF1001         NOT IN(0)
                               AND PLAN_CONTABLE.CLASECUENTA              IN(''I'')
                               AND TERCERO.IND_PAGO_RETENCION             IN(0)
                               AND TIPO_COMPROBANTE.IND_REPORTAR_1001 NOT IN(0)
                               AND TIPO_COMPROBANTE.CLASE_CONTABLE NOT IN(''Z'')';
            MI_MERGEENLACE := 'TABLA.COMPANIA       = VISTA.COMPANIA 
                           AND TABLA.ANO            = VISTA.ANO 
                           AND TABLA.TIPO_CPTE      = VISTA.TIPO_CPTE 
                           AND TABLA.COMPROBANTE    = VISTA.COMPROBANTE
                           AND TABLA.CONSECUTIVO    = VISTA.CONSECUTIVO';
            MI_MERGEEXISTE := 'UPDATE SET ' || CASE WHEN MI_PREFIJO = 'N' 
                                                    THEN 'TABLA.CONCEPTO_EX'         
                                                    ELSE CASE WHEN MI_PREFIJO = 'D' 
                                                              THEN 'TABLA.CONCEPTO_DIST'  
                                                              ELSE ''
                                                              END
                                                    END || ' = VISTA.CONCEPTO,
                                          ' || CASE WHEN MI_PREFIJO = 'N' 
                                                    THEN 'TABLA.FORMATO_CONCEPTO_EX'         
                                                    ELSE CASE WHEN MI_PREFIJO = 'D' 
                                                              THEN 'TABLA.FORMATO_CONCEPTO_DIST'  
                                                              ELSE ''
                                                              END
                                                    END || ' = VISTA.FORMATO  ';
            PCK_DATOS.GL_RTA :=  PCK_DATOS.FC_ACME(  UN_TABLA       => 'DETALLE_COMPROBANTE_CNT',
                                                     UN_ACCION      => 'MM',
                                                     UN_MERGEUSING  => MI_MERGEUSING,
                                                     UN_MERGEENLACE => MI_MERGEENLACE,
                                                     UN_MERGEEXISTE => MI_MERGEEXISTE); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD   => SQLCODE, 
                                    UN_ERROR_COD => PCK_ERRORES.ERR_CONTA_LIMPIAEXOGENA);        
    END;

    --ACTUALIZAR LOS SOBRANTES CON LOS DATOS TOMADOS DE LA CONFIGURACION INICIAL DEL PLAN CONTABLE
    BEGIN
        BEGIN

            MI_MERGEUSING:='SELECT DETALLE_COMPROBANTE_CNT.COMPANIA,
                                   DETALLE_COMPROBANTE_CNT.ANO,
                                   DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                                   DETALLE_COMPROBANTE_CNT.COMPROBANTE,
                                   DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                                   CONFIGURACION_EXOGENA.CONCEPTO,
                                   CONFIGURACION_EXOGENA.FORMATO
                             FROM DETALLE_COMPROBANTE_CNT INNER JOIN PLAN_CONTABLE
                                   ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
                                  AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO             
                                  AND DETALLE_COMPROBANTE_CNT.CUENTA   = PLAN_CONTABLE.CODIGO   
                             INNER JOIN TIPO_COMPROBANTE 
                                   ON DETALLE_COMPROBANTE_CNT.COMPANIA  = TIPO_COMPROBANTE.COMPANIA
                                  AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
                             INNER JOIN TERCERO 
                                   ON TERCERO.COMPANIA  = DETALLE_COMPROBANTE_CNT.COMPANIA
                                  AND TERCERO.SUCURSAL  = DETALLE_COMPROBANTE_CNT.SUCURSAL
                                  AND TERCERO.NIT       = DETALLE_COMPROBANTE_CNT.TERCERO
                             INNER JOIN CONFIGURACION_EXOGENA
                                   ON CONFIGURACION_EXOGENA.COMPANIA = PLAN_CONTABLE.COMPANIA
                                  AND CONFIGURACION_EXOGENA.ANO      = PLAN_CONTABLE.ANO             
                                  AND CONFIGURACION_EXOGENA.CUENTA   = PLAN_CONTABLE.CODIGO     
                             WHERE PLAN_CONTABLE.COMPANIA = ''' || UN_COMPANIA || '''
                               AND PLAN_CONTABLE.ANO      = '   || UN_ANO || '
                               AND PLAN_CONTABLE.MOSTRARF1001         NOT IN(0)
                               AND CONFIGURACION_EXOGENA.FORMATO = ''' || UN_FORMATO || '''
                               AND TERCERO.IND_PAGO_RETENCION             IN(0)
                               AND TIPO_COMPROBANTE.IND_REPORTAR_1001 NOT IN(0)
                               AND TIPO_COMPROBANTE.CLASE_CONTABLE    NOT IN(''Z'')
                               AND DETALLE_COMPROBANTE_CNT.' || CASE WHEN MI_PREFIJO = 'N' 
                                                                     THEN 'CONCEPTO_EX'         
                                                                     ELSE CASE WHEN MI_PREFIJO = 'D' 
                                                                               THEN 'CONCEPTO_DIST'  
                                                                               ELSE ''
                                                                               END
                                                                     END || ' IS NULL';
            MI_MERGEENLACE := 'TABLA.COMPANIA       = VISTA.COMPANIA 
                           AND TABLA.ANO            = VISTA.ANO 
                           AND TABLA.TIPO_CPTE      = VISTA.TIPO_CPTE 
                           AND TABLA.COMPROBANTE    = VISTA.COMPROBANTE
                           AND TABLA.CONSECUTIVO    = VISTA.CONSECUTIVO';
            MI_MERGEEXISTE := 'UPDATE SET ' || CASE WHEN MI_PREFIJO = 'N' 
                                                    THEN 'TABLA.CONCEPTO_EX'         
                                                    ELSE CASE WHEN MI_PREFIJO = 'D' 
                                                              THEN 'TABLA.CONCEPTO_DIST'  
                                                              ELSE ''
                                                              END
                                                    END || ' = VISTA.CONCEPTO,
                                          ' || CASE WHEN MI_PREFIJO = 'N' 
                                                    THEN 'TABLA.FORMATO_CONCEPTO_EX'         
                                                    ELSE CASE WHEN MI_PREFIJO = 'D' 
                                                              THEN 'TABLA.FORMATO_CONCEPTO_DIST'  
                                                              ELSE ''
                                                              END
                                                    END || ' = VISTA.FORMATO  ';
            PCK_DATOS.GL_RTA :=  PCK_DATOS.FC_ACME(  UN_TABLA       => 'DETALLE_COMPROBANTE_CNT',
                                                     UN_ACCION      => 'MM',
                                                     UN_MERGEUSING  => MI_MERGEUSING,
                                                     UN_MERGEENLACE => MI_MERGEENLACE,
                                                     UN_MERGEEXISTE => MI_MERGEEXISTE); 
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
            RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
        END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD   => SQLCODE, 
                                    UN_ERROR_COD => PCK_ERRORES.ERR_CONTA_LIMPIAEXOGENA);        
    END;
END PR_CONCEPTOSPORFORMATO;

PROCEDURE PR_MIGRAR_PLAN_DE_CUENTAS (
/*
    NAME              : MIGRAR_PLAN_DE_CUENTAS 
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : CESAR LEONARDO OCHOA CONTRERAS
    DATE MIGRADOR     : 13/02/2020
    TIME              : 12:30 PM
    SOURCE MODULE     : Entes de Control - Exogenas
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Procedimiento que actualiza la informacion de la tabla plan_contable de un año a otro
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : 
    @NAME:  MIGRAR_PLAN_DE_CUENTAS
    @METHOD:  POST
    */
PRM_ANO_INICIAL NUMBER,
PRM_COMPANIA VARCHAR2, 
PRM_FORMATO VARCHAR2) 
AS
MI_NUM_REGISTROS_PLAN_CONTABLE NUMBER;
MI_NUM_REGISTROS_ANO NUMBER;
BEGIN
    --HAGO LA CONSULTA Y VALIDO QUE NO HAYAN DATOS
    SELECT COUNT(*) INTO MI_NUM_REGISTROS_PLAN_CONTABLE FROM (
    SELECT PLAN_CONTABLE.CODIGO CUENTA, PLAN_CONTABLE.NOMBRE, CLASECUENTA.DESCRIPCION CLASECUENTA,
    CASE WHEN CONFIGURACION_EXOGENA.FORMATO = PRM_FORMATO
    THEN CONFIGURACION_EXOGENA.CONCEPTO ELSE NULL END CONCEPTO, 
    CASE WHEN PLAN_CONTABLE.MOVIMIENTO IN(0) THEN 'false' ELSE 'true' END MOVIMIENTO,
    (CASE PLAN_CONTABLE.EXDISTRITAL WHEN 0 THEN 'false' ELSE 'true' END) EXDISTRITAL, 
    CASE WHEN PLAN_CONTABLE.RETEPRACTICADA IN(0) THEN 'false' ELSE 'true' END RETEPRACTICADA , 
    CASE WHEN PLAN_CONTABLE.RETEASUMIDA IN (0) THEN 'false' ELSE 'true' END RETEASUMIDA , 
    CASE WHEN PLAN_CONTABLE.IVACOMUN IN(0) THEN 'false' ELSE 'true' END IVACOMUN , 
    CASE WHEN PLAN_CONTABLE.IVASIMPLIFICADO IN(0) THEN 'false' ELSE 'true' END IVASIMPLIFICADO , 
    CASE WHEN PLAN_CONTABLE.RETEICA IN(0) THEN 'false' ELSE 'true' END RETEICA ,
    (CASE PLAN_CONTABLE.MOSTRARF1001 WHEN 0 THEN 'false' ELSE 'true' END) MOSTRARF1001,
    (CASE PLAN_CONTABLE.IND_AGENTE_RETENCION WHEN 0 THEN 'false' ELSE 'true' END) IND_AGENTE_RETENCION,
    (CASE PLAN_CONTABLE.IND_SUJETO_RETENCION WHEN 0 THEN 'false' ELSE 'true' END) IND_SUJETO_RETENCION 
    FROM PLAN_CONTABLE 
    INNER JOIN CLASECUENTA ON PLAN_CONTABLE.CLASECUENTA = CLASECUENTA.CODIGO 
    LEFT JOIN CONFIGURACION_EXOGENA ON PLAN_CONTABLE.COMPANIA = CONFIGURACION_EXOGENA.COMPANIA AND PLAN_CONTABLE.ANO = CONFIGURACION_EXOGENA.ANO 
    AND PLAN_CONTABLE.CODIGO = CONFIGURACION_EXOGENA.CUENTA AND PRM_FORMATO = CONFIGURACION_EXOGENA.FORMATO
    WHERE PLAN_CONTABLE.COMPANIA = PRM_COMPANIA AND PLAN_CONTABLE.ANO = (PRM_ANO_INICIAL + 1) );
     --SI NO HAY DATOS INSERTO EL PLAN CONTABLE
    IF MI_NUM_REGISTROS_PLAN_CONTABLE = 0 THEN    
        FOR RS IN (SELECT PLAN_CONTABLE.ANO , PLAN_CONTABLE.CODIGO , PLAN_CONTABLE.NOMBRE, PLAN_CONTABLE.MOVIMIENTO , PLAN_CONTABLE.EXDISTRITAL, PLAN_CONTABLE.RETEPRACTICADA , 
        PLAN_CONTABLE.RETEASUMIDA ,  PLAN_CONTABLE.IVACOMUN ,  PLAN_CONTABLE.IVASIMPLIFICADO  ,  PLAN_CONTABLE.RETEICA , PLAN_CONTABLE.MOSTRARF1001, PLAN_CONTABLE.IND_AGENTE_RETENCION ,
        PLAN_CONTABLE.IND_SUJETO_RETENCION , CLASECUENTA.DESCRIPCION, PLAN_CONTABLE.NATURALEZA, PLAN_CONTABLE.CLASECUENTA,
        CASE WHEN CONFIGURACION_EXOGENA.FORMATO = PRM_FORMATO  THEN CONFIGURACION_EXOGENA.CONCEPTO ELSE NULL END CONCEPTO
        FROM PLAN_CONTABLE 
        INNER JOIN CLASECUENTA ON PLAN_CONTABLE.CLASECUENTA = CLASECUENTA.CODIGO 
        LEFT JOIN CONFIGURACION_EXOGENA ON PLAN_CONTABLE.COMPANIA = CONFIGURACION_EXOGENA.COMPANIA AND PLAN_CONTABLE.ANO = CONFIGURACION_EXOGENA.ANO 
        AND PLAN_CONTABLE.CODIGO = CONFIGURACION_EXOGENA.CUENTA AND PRM_FORMATO = CONFIGURACION_EXOGENA.FORMATO
        WHERE PLAN_CONTABLE.COMPANIA = PRM_COMPANIA AND PLAN_CONTABLE.ANO = PRM_ANO_INICIAL) 
        LOOP 
            --VALIDO LOS REGISTROS DE LA TABLA ANO Y COPIO LOS REGISTROS SI NO EXISTEN, DEL ANO ANTERIOR QUE SE RELACIONAN CON EL PLAN CONTABLE
            FOR RSANO IN (SELECT DISTINCT ANO.ASOCIADOPC, ANO.CEDULAFIRMACERT_DIAN, ANO.COMPANIA, ANO.COMPRASYCONSUMO_UVT, ANO.CONSIGNACIONESBANCARIAS_UVT, 
            ANO.CONSUMOTARJETACRE_UVT, ANO.CREATED_BY, ANO.CUANTIAMINIMA, ANO.CUANTIAMINIMADEPRE, ANO.DATE_CREATED, ANO.DATE_MODIFIED, ANO.FECHA_CIERRE_ADQUISICIONES, 
            ANO.FECHA_CIERRE_ADQUI_EJEC, ANO.FECHA_CIERRE_PLAN_ACCION, ANO.FECHA_CIERRE_PLAN_ACCION_EJEC, ANO.FECHA_CIERRE_PLAN_INDICATIVO, ANO.FECHA_CIERRE_PLAN_INDI_EJEC, 
            ANO.FORMATO_DIAN, ANO.FORMULA, ANO.INGRESOSTOTALES_UVT, ANO.INICIA_CONTABILIDAD, ANO.IPC, ANO.LIM_AUTORETENEDOR, ANO.MAXIMOUVT, ANO.MESESAMNISTIA_IMPUESTOS, 
            ANO.MESESAMNISTIA_PREDIAL, ANO.MODIFIED_BY, ANO.NOMBREFIRMACERT_DIAN, ANO.NUMERO, ANO.PATRIMONIOBRUTO_UVT, ANO.PERIODICIDAD, ANO.PLANAPROBADO,
            ANO.PORCENTAJEPENSION, ANO.PORCENTAJESALUD, ANO.SALARIOMINIMO, ANO.VALORUVT, ANO.VLRFONDOSOL_PENSIONAL
            FROM ANO INNER JOIN PLAN_CONTABLE ON PLAN_CONTABLE.ANO = ANO.NUMERO AND PLAN_CONTABLE.COMPANIA = ANO.COMPANIA WHERE PLAN_CONTABLE.CODIGO = RS.CODIGO)
            LOOP
                INSERT INTO ANO (ASOCIADOPC, CEDULAFIRMACERT_DIAN, COMPANIA, COMPRASYCONSUMO_UVT, CONSIGNACIONESBANCARIAS_UVT, CONSUMOTARJETACRE_UVT, CREATED_BY, CUANTIAMINIMA, 
                CUANTIAMINIMADEPRE, DATE_CREATED, DATE_MODIFIED, FECHA_CIERRE_ADQUISICIONES, FECHA_CIERRE_ADQUI_EJEC, FECHA_CIERRE_PLAN_ACCION, FECHA_CIERRE_PLAN_ACCION_EJEC, 
                FECHA_CIERRE_PLAN_INDICATIVO, FECHA_CIERRE_PLAN_INDI_EJEC, FORMATO_DIAN, FORMULA, INGRESOSTOTALES_UVT, INICIA_CONTABILIDAD, IPC, LIM_AUTORETENEDOR, MAXIMOUVT, 
                MESESAMNISTIA_IMPUESTOS, MESESAMNISTIA_PREDIAL, MODIFIED_BY, NOMBREFIRMACERT_DIAN, NUMERO, PATRIMONIOBRUTO_UVT, PERIODICIDAD, PLANAPROBADO, PORCENTAJEPENSION, 
                PORCENTAJESALUD, SALARIOMINIMO, VALORUVT, VLRFONDOSOL_PENSIONAL)
                SELECT DISTINCT 
                RSANO.ASOCIADOPC, RSANO.CEDULAFIRMACERT_DIAN, RSANO.COMPANIA, RSANO.COMPRASYCONSUMO_UVT, RSANO.CONSIGNACIONESBANCARIAS_UVT, RSANO.CONSUMOTARJETACRE_UVT,
                RSANO.CREATED_BY, RSANO.CUANTIAMINIMA, RSANO.CUANTIAMINIMADEPRE, RSANO.DATE_CREATED, NULL, RSANO.FECHA_CIERRE_ADQUISICIONES, RSANO.FECHA_CIERRE_ADQUI_EJEC, 
                RSANO.FECHA_CIERRE_PLAN_ACCION, RSANO.FECHA_CIERRE_PLAN_ACCION_EJEC, RSANO.FECHA_CIERRE_PLAN_INDICATIVO, RSANO.FECHA_CIERRE_PLAN_INDI_EJEC, 
                RSANO.FORMATO_DIAN, RSANO.FORMULA, RSANO.INGRESOSTOTALES_UVT, RSANO.INICIA_CONTABILIDAD, RSANO.IPC, RSANO.LIM_AUTORETENEDOR, RSANO.MAXIMOUVT, 
                RSANO.MESESAMNISTIA_IMPUESTOS, RSANO.MESESAMNISTIA_PREDIAL, NULL, RSANO.NOMBREFIRMACERT_DIAN, RSANO.NUMERO + 1, RSANO.PATRIMONIOBRUTO_UVT, RSANO.PERIODICIDAD,
                RSANO.PLANAPROBADO, RSANO.PORCENTAJEPENSION, RSANO.PORCENTAJESALUD, RSANO.SALARIOMINIMO, RSANO.VALORUVT, RSANO.VLRFONDOSOL_PENSIONAL
                FROM DUAL 
                WHERE NOT EXISTS(SELECT *
                FROM ANO WHERE ANO.NUMERO = (RSANO.NUMERO  + 1) AND ANO.COMPANIA = RSANO.COMPANIA);
            END LOOP;
            --INSERTO DESPUES DE VALIDAR LA INFORMACION DE ANIOS
            INSERT INTO PLAN_CONTABLE 
            (ANO, CODIGO, NOMBRE, MOVIMIENTO, EXDISTRITAL, RETEPRACTICADA, RETEASUMIDA, IVACOMUN, IVASIMPLIFICADO, RETEICA, MOSTRARF1001, IND_AGENTE_RETENCION,
            IND_SUJETO_RETENCION, COMPANIA, NATURALEZA, CLASECUENTA)
            SELECT ANO, CODIGO, NOMBRE, MOVIMIENTO, EXDISTRITAL, RETEPRACTICADA, RETEASUMIDA, IVACOMUN, IVASIMPLIFICADO, 
            RETEICA, MOSTRARF1001, IND_AGENTE_RETENCION, IND_SUJETO_RETENCION, COMPANIA, NATURALEZA, CLASECUENTA
            FROM (SELECT PLAN_CONTABLE.ANO + 1 AS ANO,
            PLAN_CONTABLE.CODIGO , PLAN_CONTABLE.NOMBRE,
            PLAN_CONTABLE.MOVIMIENTO ,
            PLAN_CONTABLE.EXDISTRITAL,
            PLAN_CONTABLE.RETEPRACTICADA , 
            PLAN_CONTABLE.RETEASUMIDA , 
            PLAN_CONTABLE.IVACOMUN , 
            PLAN_CONTABLE.IVASIMPLIFICADO  , 
            PLAN_CONTABLE.RETEICA ,
            PLAN_CONTABLE.MOSTRARF1001,
            PLAN_CONTABLE.IND_AGENTE_RETENCION ,
            PLAN_CONTABLE.IND_SUJETO_RETENCION ,
            CLASECUENTA.DESCRIPCION,
            PLAN_CONTABLE.COMPANIA,
            PLAN_CONTABLE.NATURALEZA,
            PLAN_CONTABLE.CLASECUENTA,
            CASE WHEN CONFIGURACION_EXOGENA.FORMATO = PRM_FORMATO 
            THEN CONFIGURACION_EXOGENA.CONCEPTO ELSE NULL END CONCEPTO
            FROM PLAN_CONTABLE 
            INNER JOIN CLASECUENTA ON PLAN_CONTABLE.CLASECUENTA = CLASECUENTA.CODIGO 
            LEFT JOIN CONFIGURACION_EXOGENA ON PLAN_CONTABLE.COMPANIA = CONFIGURACION_EXOGENA.COMPANIA AND PLAN_CONTABLE.ANO = CONFIGURACION_EXOGENA.ANO 
            AND PLAN_CONTABLE.CODIGO = CONFIGURACION_EXOGENA.CUENTA AND PRM_FORMATO = CONFIGURACION_EXOGENA.FORMATO
            WHERE PLAN_CONTABLE.COMPANIA = PRM_COMPANIA AND PLAN_CONTABLE.ANO = PRM_ANO_INICIAL)
            WHERE NOT EXISTS (
            SELECT * FROM PLAN_CONTABLE WHERE ANO = (PRM_ANO_INICIAL + 1));  
        END LOOP;
    END IF;
END PR_MIGRAR_PLAN_DE_CUENTAS;		


PROCEDURE PR_ACTUALIZAR_CONF_EXOGENAS (
/*
    NAME              : PR_ACTUALIZAR_CONF_EXOGENAS 
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : CESAR LEONARDO OCHOA CONTRERAS
    DATE MIGRADOR     : 14/02/2020
    TIME              : 5:30 PM
    SOURCE MODULE     : Entes de Control - Exogenas
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Procedimiento que actualiza la 
    informacion de la configuracion exogena de un anio al siguiente
    MODIFIER          : 
    DATE MODIFIED     : 
    DESCRIPTION       : 
    @NAME:  MIGRAR_PLAN_DE_CUENTAS
    @METHOD:  POST
    */
UN_ANO NUMBER,
UN_COMPANIA VARCHAR2, 
UN_FORMATO VARCHAR2,
UN_USUARIO VARCHAR2) 
AS
BEGIN
INSERT INTO CONFIGURACION_EXOGENA (COMPANIA, ANO, FORMATO, CONCEPTO, CUENTA, CREATED_BY, DATE_CREATED, CLASE_EXOGENA)
(SELECT COMPANIA, ANO + 1, FORMATO, CONCEPTO, CUENTA, UN_USUARIO, SYSDATE, CLASE_EXOGENA FROM CONFIGURACION_EXOGENA WHERE
ANO = UN_ANO AND COMPANIA = UN_COMPANIA AND FORMATO = UN_FORMATO AND NOT EXISTS(
SELECT * FROM CONFIGURACION_EXOGENA WHERE ANO = (UN_ANO + 1)  AND COMPANIA = UN_COMPANIA AND FORMATO = UN_FORMATO ) );
END PR_ACTUALIZAR_CONF_EXOGENAS;
				
FUNCTION FC_ACT_TERCEROS_EXOGENAS

/*
    NAME              : FC_ACT_CLA_APROPIACIONES
    AUTHOR MIGRACION  : LUIS JACOBO DIAZ MUÃ‘OZ
    DATE MIGRADOR     : 15/07/2022                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          : LUIS JACOBO DIAZ M
    DATE MODIFIED     : 06/09/
    TIME              :
    MODIFICATIONS     : 
    DESCRIPTION       : PROCEDIMIENTO QUE ACTUALIZA LOS  TIPOS CLASIFICADORES DESDE UN EXCEL A LAS APROPIACIONES INICIALES

    @NAME:    FC_ACT_CLA_APROPIACIONES
    @METHOD:  POST
    */
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CADENAPLAN  IN CLOB,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)  
RETURN CLOB AS
MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
MI_ANIO               PCK_SUBTIPOS.TI_ANIO;
MI_EXISTE             NUMBER := 0;
MI_EXISTE_ERROR       NUMBER := 0;
MI_APLICACION         NUMBER := 0;
MI_APLICACIONINGRESOS NUMBER := 0;
MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
MI_RETORNO            CLOB := '';
MI_RTA                VARCHAR2(10); 
MI_NUMERO_COD         VARCHAR2(1000);
MI_EXISTE_CCPET       VARCHAR2(100);
MI_EXISTE_PROGRAMA    VARCHAR2(100);
MI_EXISTE_UNIEJE      VARCHAR2(100);
MI_EXISTE_BPIN        VARCHAR2(100);

BEGIN

  MI_ANIO := EXTRACT(YEAR FROM SYSDATE);
  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAPLAN,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

  <<CREAR_TIPOCLASIFICADORES>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                      UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);

        --ACTUALIZA TIPO_CLASIFICADORES  EN APROPIACIONESINICIALES
       MI_EXISTE := '';
       MI_EXISTE_ERROR := '';
       MI_CAMPOS := '';
            IF(MI_DATOS_COLUMNAS(1) IS NOT NULL AND MI_DATOS_COLUMNAS(2) IS NOT NULL AND MI_DATOS_COLUMNAS(3) IS NOT NULL) THEN 
               -- VALIDAMOS QUE EXISTA EL TERCERO
               SELECT COUNT('X') EXISTE
                   INTO MI_EXISTE                                            
                   FROM TERCERO
                   WHERE COMPANIA = UN_COMPANIA
                      AND NIT = MI_DATOS_COLUMNAS(1);               -- OJO AQUI QUEDE 
                   IF (MI_EXISTE NOT IN (0)) THEN
                        -- LO ACTUALIZA
                        BEGIN
                             MI_CAMPOS := 'IND_REPORTAR_2276 = '||MI_DATOS_COLUMNAS(3)||'
                                            ,MODIFIED_BY       =  ''' || UN_USUARIO           || '''
                                            ,DATE_MODIFIED     =          SYSDATE';
                            MI_CONDICION := '   COMPANIA      =  ''' || UN_COMPANIA || '''
                               AND NIT        =  ''' || MI_DATOS_COLUMNAS(1) || '''';
    
    
                            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'TERCERO'
                                                               ,UN_ACCION    => 'M'
                                                               ,UN_CAMPOS    => MI_CAMPOS
                                                               ,UN_CONDICION => MI_CONDICION);
                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                            ROLLBACK;
                            RAISE PCK_EXCEPCIONES.EXC_ENTESDECONTROL; 
                        END;
                   ELSIF MI_EXISTE IN (0) THEN
                     MI_RETORNO := MI_RETORNO|| CHR(9)||CHR(13)||'EL TERCERO: '||MI_DATOS_COLUMNAS(1)||'NO EXISTE' ;
                   END IF;
            END IF;
  END LOOP CREAR_TIPOCLASIFICADORES;
RETURN MI_RETORNO;

END FC_ACT_TERCEROS_EXOGENAS;

FUNCTION FC_ACT_MASIVA_CONCEPTOS_EXO
/*
    NAME              : FC_ACT_MASIVA_CONCEPTOS_EXO
    AUTHOR MIGRACION  : MARIA ALEJANDRA PEREZ SALAZAR
    DATE MIGRADOR     : 17/11/2023 
    TIME              : 8:00 AM
    DESCRIPTION       : FUNCION QUE ACTUALIZA LOS CONCEPTOS EXOGENA DEL DETALLE CNT MASIVAMENTE

    @NAME:    FC_ACT_MASIVA_CONCEPTOS_EXO
    @METHOD:  POST
    */
( 
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANIO        IN PCK_SUBTIPOS.TI_ANIO,
  UN_CADENAPLAN  IN CLOB,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
)  
RETURN CLOB 
AS 
    MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
    MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_EXISTENDATOS       NUMBER := 0;

    MI_CUENTA             DETALLE_COMPROBANTE_CNT.CUENTA%TYPE; 
    MI_COD_TIPO_CPTE      DETALLE_COMPROBANTE_CNT.TIPO_CPTE%TYPE;  
    MI_COD_COMPROBANTE    DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE;  
    MI_VALOR_DEBITO       DETALLE_COMPROBANTE_CNT.VALOR_DEBITO%TYPE;
    MI_VALOR_CREDITO      DETALLE_COMPROBANTE_CNT.VALOR_CREDITO%TYPE;
    MI_CONCEPTO_EX        DETALLE_COMPROBANTE_CNT.CONCEPTO_EX%TYPE;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION; 
    MI_RETORNO            CLOB := '';
    MI_RTA                VARCHAR2(10); 
     
    BEGIN

      MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENAPLAN,
                                                   UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
      <<ACTUALIZARCONCEPTOS>>
      FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
      LOOP
        MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                          UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);    
        
        MI_COD_TIPO_CPTE            := MI_DATOS_COLUMNAS(1);
        MI_COD_COMPROBANTE          := MI_DATOS_COLUMNAS(2);
        MI_CUENTA                   := MI_DATOS_COLUMNAS(6);
        MI_CONCEPTO_EX              := MI_DATOS_COLUMNAS(7);
        MI_VALOR_DEBITO             := TO_NUMBER(REPLACE(MI_DATOS_COLUMNAS(8),',',''));
        MI_VALOR_CREDITO            := TO_NUMBER(REPLACE(MI_DATOS_COLUMNAS(9),',',''));
        
        IF MI_CONCEPTO_EX IS NOT NULL THEN
            BEGIN
                SELECT COUNT('X') EXISTE
                  INTO MI_EXISTENDATOS 
                  FROM CONCEPTOSEX 
                 WHERE FORMATO = '1001'
                   AND CODIGO = MI_CONCEPTO_EX;              
            END; 
           
            IF MI_EXISTENDATOS > 0 THEN
                MI_CAMPOS := ' CONCEPTO_EX = ''' || MI_CONCEPTO_EX ||'''' ; 
                MI_CONDICION := 'COMPANIA          = ''' || UN_COMPANIA             || '''
                             AND ANO               =   ' || UN_ANIO                  || '
                             AND TIPO_CPTE         = ''' || MI_COD_TIPO_CPTE        || '''
                             AND COMPROBANTE       = ''' || MI_COD_COMPROBANTE      || '''
                             AND VALOR_DEBITO      = ''' || MI_VALOR_DEBITO         || '''
                             AND VALOR_CREDITO     = ''' || MI_VALOR_CREDITO        || '''
                             AND CUENTA            = ''' || MI_CUENTA               || ''' ';
                             
                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => 'DETALLE_COMPROBANTE_CNT'
                                             ,UN_ACCION    => 'M'
                                             ,UN_CAMPOS    => MI_CAMPOS
                                             ,UN_CONDICION => MI_CONDICION); 
            ELSE
                IF LENGTH(MI_RETORNO) > 0 THEN
                    MI_RETORNO:=MI_RETORNO||CHR(13)|| 'El concepto: ' || MI_CONCEPTO_EX ||' no se encuentra configurado.';
                ELSE
                    MI_RETORNO:='El concepto: ' || MI_CONCEPTO_EX ||' no se encuentra configurado.';
                END IF;
            END IF;
        END IF;
        MI_EXISTENDATOS := 0;
      END LOOP ACTUALIZARCONCEPTOS;
     
    RETURN MI_RETORNO;
END FC_ACT_MASIVA_CONCEPTOS_EXO;				

FUNCTION FC_COPIAR_CONCEPTO_EXOGENA
/*
    NAME        : FC_COPIAR_CONCEPTO_EXOGENA
    AUTHOR      : CFBARRERA
    DATE        : 26/05/2026
    DESCRIPTION : Funcion que copia la configuracion exogena de un año origen a un año destino.
                  Inserta unicamente los registros de CONFIGURACION_EXOGENA que cumplan las
                  siguientes condiciones:
                    - La cuenta existe en el plan contable del año destino.
                    - La cuenta es de movimiento en el año destino (no es cuenta padre).
                    - La cuenta no existe previamente en la configuracion exogena del año destino.
                  Si se presentan novedades (cuentas no encontradas, ya existentes o que pasaron
                  a ser padre), retorna un reporte detallado con el total de novedades encontradas.
                  Si el proceso termina sin novedades, retorna unicamente un mensaje de exito.
    --METHOD:   POST
    --NAME:  copiarConceptoExogenas
*/
(
    UN_COMPANIA          IN  VARCHAR2,
    UN_ANO_DESTINO       IN  INTEGER,
    UN_ANO_ORIGEN        IN  INTEGER,
    UN_COMPANIA_DESTINO  IN  VARCHAR2,
    UN_FORMATO           IN  VARCHAR2
)
RETURN CLOB
AS
  MI_TEXTO_SALIDA   CLOB   := '';
  MI_MENSAJE        CLOB   := '';
  MI_EXCLUIDOS      VARCHAR2(32000);
  MI_CAMPOS         VARCHAR2(32000);
  MI_VALORES        CLOB;
  MI_STRSQL         VARCHAR2(32000);
  MI_SUMA_IND       NUMBER;
  MI_CUENTA         VARCHAR2(50);
  MI_CONCEPTO       VARCHAR2(50);
  MI_CUENTA_HIJA    VARCHAR2(50);
  MI_CONT_HIJAS     NUMBER;
  MI_EXISTE_DESTINO NUMBER;
  MI_CONT_ERRORES   NUMBER := 0;
  MI_CAMPOS_SELECT VARCHAR2(32000);

  RSCUENTA     SYS_REFCURSOR;
  RSHIJAS      SYS_REFCURSOR;
BEGIN
  -- ============================================================
  -- 0) Reporta cuentas del año origen que NO existen
  --    en PLAN_CONTABLE del año destino (solo para informar)
  -- ============================================================
  FOR REG IN (
    SELECT EXO_ORIGEN.CUENTA
    FROM   CONFIGURACION_EXOGENA EXO_ORIGEN
    WHERE  EXO_ORIGEN.COMPANIA = UN_COMPANIA
      AND  EXO_ORIGEN.ANO      = UN_ANO_ORIGEN
      AND  EXO_ORIGEN.FORMATO  = UN_FORMATO
      AND  EXO_ORIGEN.CONCEPTO IS NOT NULL
      AND  NOT EXISTS (
             SELECT 1
             FROM   PLAN_CONTABLE PL
             WHERE  PL.COMPANIA = UN_COMPANIA_DESTINO
               AND  PL.ANO      = UN_ANO_DESTINO
               AND  PL.CODIGO   = EXO_ORIGEN.CUENTA
           )
  ) LOOP
    MI_CONT_ERRORES := MI_CONT_ERRORES + 1;
    MI_MENSAJE := MI_MENSAJE || 'Cuenta ' || REG.CUENTA ||
                  ' no existe en el plan contable del año destino.' || CHR(10);
  END LOOP;

  -- ============================================================
  -- 1) Trae cuentas que tengan concepto en año origen,
  --    filtradas por formato y que existan en PLAN_CONTABLE
  --    del año destino
  -- ============================================================
  MI_STRSQL :=
    ' SELECT EXO_ORIGEN.CUENTA, EXO_ORIGEN.CONCEPTO '    ||
    ' FROM CONFIGURACION_EXOGENA EXO_ORIGEN '             ||
    ' INNER JOIN PLAN_CONTABLE PL '                       ||
    '   ON EXO_ORIGEN.COMPANIA = PL.COMPANIA '           ||
    '  AND EXO_ORIGEN.CUENTA   = PL.CODIGO '             ||
    '  AND PL.ANO              = ' || UN_ANO_DESTINO      ||
    ' WHERE EXO_ORIGEN.COMPANIA = ''' || UN_COMPANIA || '''' ||
    '   AND EXO_ORIGEN.ANO      = '   || UN_ANO_ORIGEN   ||
    '   AND EXO_ORIGEN.FORMATO  = ''' || UN_FORMATO || '''' ||
    '   AND EXO_ORIGEN.CONCEPTO IS NOT NULL';

  OPEN RSCUENTA FOR MI_STRSQL;
  LOOP
    FETCH RSCUENTA INTO MI_CUENTA, MI_CONCEPTO;
    EXIT WHEN RSCUENTA%NOTFOUND;

    -- ============================================================
    -- 2) Valida si la cuenta sigue siendo de movimiento en año destino.
    -- ============================================================
    SELECT (PL.MOVIMIENTO
          + PL.MAN_CEN_CTO
          + PL.MAN_AUX_TER
          + PL.MAN_AUX_GEN
          + PL.MAN_AUX_REF
          + PL.MAN_AUX_FUE)
    INTO MI_SUMA_IND
    FROM PLAN_CONTABLE PL
    WHERE PL.COMPANIA = UN_COMPANIA_DESTINO
      AND PL.ANO      = UN_ANO_DESTINO
      AND PL.CODIGO   = MI_CUENTA;

    IF MI_SUMA_IND <> 0 THEN
      -- ============================================================
      -- 3) Cuenta es de movimiento. Valida si YA existe en destino.
      -- ============================================================
      SELECT COUNT(*)
      INTO MI_EXISTE_DESTINO
      FROM CONFIGURACION_EXOGENA
      WHERE COMPANIA = UN_COMPANIA_DESTINO
        AND ANO      = UN_ANO_DESTINO
        AND CUENTA   = MI_CUENTA
        AND FORMATO  = UN_FORMATO;

      IF MI_EXISTE_DESTINO > 0 THEN
        -- Ya existe: informa y omite
        MI_CONT_ERRORES := MI_CONT_ERRORES + 1;
        MI_MENSAJE := MI_MENSAJE || 'Cuenta ' || MI_CUENTA ||
                      ' ya existe en exogenas del año destino, se omite.' || CHR(10);
      ELSE
        -- No existe: inserta desde origen con compania/ano destino
        MI_EXCLUIDOS := 'COMPANIA,ANO';
        MI_CAMPOS    := PCK_SYSMAN_UTL.FC_LISTA_CAMPOS('CONFIGURACION_EXOGENA', MI_EXCLUIDOS);
        
        --alias para el Select
        MI_CAMPOS_SELECT := 'EXO_ORIGEN.' || REPLACE(MI_CAMPOS, ',', ', EXO_ORIGEN.');
        
        MI_VALORES :=
          ' SELECT ' || MI_CAMPOS_SELECT || ',' ||   -- <-- USA MI_CAMPOS_SELECT
          '        ''' || UN_COMPANIA_DESTINO || ''',' || UN_ANO_DESTINO ||
          ' FROM   CONFIGURACION_EXOGENA EXO_ORIGEN '                   ||
          ' INNER JOIN PLAN_CONTABLE PL '                                ||
          '   ON  EXO_ORIGEN.COMPANIA = PL.COMPANIA '                   ||
          '  AND  EXO_ORIGEN.CUENTA   = PL.CODIGO '                     ||
          '  AND  PL.ANO              = ' || UN_ANO_DESTINO              ||
          ' WHERE EXO_ORIGEN.COMPANIA = ''' || UN_COMPANIA || '''' ||
          '   AND EXO_ORIGEN.ANO      = '   || UN_ANO_ORIGEN             ||
          '   AND EXO_ORIGEN.FORMATO  = ''' || UN_FORMATO || '''' ||
          '   AND EXO_ORIGEN.CUENTA   = ''' || MI_CUENTA || '''' ||
          '   AND (PL.MOVIMIENTO + PL.MAN_CEN_CTO + PL.MAN_AUX_TER'    ||
          '      + PL.MAN_AUX_GEN + PL.MAN_AUX_REF + PL.MAN_AUX_FUE)' ||
          '      <> 0';
        
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
          'CONFIGURACION_EXOGENA', 'IS',
          MI_CAMPOS || ',COMPANIA,ANO',    -- <-- Sin alias para columnas del INSERT
          MI_VALORES,
          NULL, NULL
        );
      END IF;

    ELSE
      -- ============================================================
      -- 4) La cuenta pasó a ser cuenta PADRE en el año destino.
      --    Solo informa sus cuentas hijas.
      -- ============================================================
      SELECT COUNT(*)
      INTO MI_CONT_HIJAS
      FROM PLAN_CONTABLE PL
      WHERE PL.COMPANIA = UN_COMPANIA_DESTINO
        AND PL.ANO      = UN_ANO_DESTINO
        AND PL.CODIGO   LIKE MI_CUENTA || '%'
        AND PL.CODIGO   != MI_CUENTA;

      MI_CONT_ERRORES := MI_CONT_ERRORES + 1;

      IF MI_CONT_HIJAS > 0 THEN
        MI_MENSAJE := MI_MENSAJE || 'Cuenta ' || MI_CUENTA ||
                      ' paso a ser cuenta padre en el año destino.' ||
                      ' Sus cuentas hijas son:' || CHR(10);

        MI_STRSQL :=
          ' SELECT PL.CODIGO '                                     ||
          ' FROM PLAN_CONTABLE PL '                                ||
          ' WHERE PL.COMPANIA = ''' || UN_COMPANIA_DESTINO || '''' ||
          '   AND PL.ANO      = '   || UN_ANO_DESTINO              ||
          '   AND PL.CODIGO   LIKE ''' || MI_CUENTA || '%'''       ||
          '   AND PL.CODIGO   != ''' || MI_CUENTA || '''';

        OPEN RSHIJAS FOR MI_STRSQL;
        LOOP
          FETCH RSHIJAS INTO MI_CUENTA_HIJA;
          EXIT WHEN RSHIJAS%NOTFOUND;
          MI_MENSAJE := MI_MENSAJE || '   - Hija: ' || MI_CUENTA_HIJA || CHR(10);
        END LOOP;
        CLOSE RSHIJAS;

      ELSE
        MI_MENSAJE := MI_MENSAJE || 'Cuenta ' || MI_CUENTA ||
                      ' paso a ser cuenta padre en el año destino y no tiene subcuentas.' || CHR(10);
      END IF;

    END IF;

  END LOOP;
  CLOSE RSCUENTA;

  -- ============================================================
  -- 5) Retorno: TXT solo si hubo novedades, de lo contrario mensaje simple
  -- ============================================================
  IF MI_CONT_ERRORES > 0 THEN
    MI_TEXTO_SALIDA :=
      '============================================================' || CHR(10) ||
      'CONFIGURACION EXOGENA'                                         || CHR(10) ||
      'Fecha de Proceso  : ' || TO_CHAR(SYSDATE, 'DD/MM/YYYY HH24:MI:SS') || CHR(10) ||
      'Año Origen        : ' || UN_ANO_ORIGEN                        || CHR(10) ||
      'Año Destino       : ' || UN_ANO_DESTINO                       || CHR(10) ||
      'Total novedades   : ' || MI_CONT_ERRORES                      || CHR(10) ||
      '============================================================'  || CHR(10) ||
      CHR(10) || 'Registros con novedades:'                           || CHR(10) ||
      MI_MENSAJE                                                       ||
      '============================================================';
  ELSE
    MI_TEXTO_SALIDA := 'Proceso termino exitosamente.';
  END IF;

  RETURN MI_TEXTO_SALIDA;

EXCEPTION
  WHEN OTHERS THEN
    RETURN 'Error: ' || SQLERRM;
END FC_COPIAR_CONCEPTO_EXOGENA;

END PCK_EXOGENAS;