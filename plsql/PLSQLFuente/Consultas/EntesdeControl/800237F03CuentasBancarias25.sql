SELECT PLAN_CONTABLE.CODBANCO_SIA||'-'||BN.NOMBREBANCO                                                          "Banco O Entidad Financiera",                         
       PLAN_CONTABLE.CODIGO                                                                                     "Código Contable",
       CUENTABANCOS.CUENTANUMERO                                                                                "Cuenta No",
       FUENTE_RECURSOS.NOMBRE                                                                                   "Destinación De La Cuenta",
       NVL(INGRESOS.INGRESOS,0)                                                                                 "Ingresos",
       (CASE WHEN 0  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO0  ELSE 0 END
                           + CASE WHEN 1  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO1  ELSE 0 END
                           + CASE WHEN 2  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO2  ELSE 0 END
                           + CASE WHEN 3  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO3  ELSE 0 END
                           + CASE WHEN 4  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO4  ELSE 0 END
                           + CASE WHEN 5  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO5  ELSE 0 END
                           + CASE WHEN 6  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO6  ELSE 0 END
                           + CASE WHEN 7  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO7  ELSE 0 END
                           + CASE WHEN 8  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO8  ELSE 0 END
                           + CASE WHEN 9  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO9  ELSE 0 END
                           + CASE WHEN 10 =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO10 ELSE 0 END
                           + CASE WHEN 11 =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO11 ELSE 0 END
                           + CASE WHEN 12 =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO12 ELSE 0 END
                           + CASE WHEN 13 =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO13 ELSE 0 END
                           )                                                                      "Saldo Libro De Contabilidad",
       (CASE WHEN 0  =s$mesFinal$s   THEN PLAN_CONTABLE.AJUSTE0  ELSE 0 END
                           + CASE WHEN 1  =s$mesFinal$s   THEN PLAN_CONTABLE.AJUSTE1  ELSE 0 END
                           + CASE WHEN 2  =s$mesFinal$s   THEN PLAN_CONTABLE.AJUSTE2  ELSE 0 END
                           + CASE WHEN 3  =s$mesFinal$s   THEN PLAN_CONTABLE.AJUSTE3  ELSE 0 END
                           + CASE WHEN 4  =s$mesFinal$s   THEN PLAN_CONTABLE.AJUSTE4  ELSE 0 END
                           + CASE WHEN 5  =s$mesFinal$s   THEN PLAN_CONTABLE.AJUSTE5  ELSE 0 END
                           + CASE WHEN 6  =s$mesFinal$s   THEN PLAN_CONTABLE.AJUSTE6  ELSE 0 END
                           + CASE WHEN 7  =s$mesFinal$s   THEN PLAN_CONTABLE.AJUSTE7  ELSE 0 END
                           + CASE WHEN 8  =s$mesFinal$s   THEN PLAN_CONTABLE.AJUSTE8  ELSE 0 END
                           + CASE WHEN 9  =s$mesFinal$s   THEN PLAN_CONTABLE.AJUSTE9  ELSE 0 END
                           + CASE WHEN 10 =s$mesFinal$s   THEN PLAN_CONTABLE.AJUSTE10 ELSE 0 END
                           + CASE WHEN 11 =s$mesFinal$s   THEN PLAN_CONTABLE.AJUSTE11 ELSE 0 END
                           + CASE WHEN 12 =s$mesFinal$s   THEN PLAN_CONTABLE.AJUSTE12 ELSE 0 END
                           + CASE WHEN 13 =s$mesFinal$s   THEN PLAN_CONTABLE.AJUSTE13 ELSE 0 END
                           )                                                                      "Saldo Extracto Bancario",
       (CASE WHEN 0  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO0  ELSE 0 END
                           + CASE WHEN 1  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO1  ELSE 0 END
                           + CASE WHEN 2  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO2  ELSE 0 END
                           + CASE WHEN 3  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO3  ELSE 0 END
                           + CASE WHEN 4  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO4  ELSE 0 END
                           + CASE WHEN 5  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO5  ELSE 0 END
                           + CASE WHEN 6  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO6  ELSE 0 END
                           + CASE WHEN 7  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO7  ELSE 0 END
                           + CASE WHEN 8  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO8  ELSE 0 END
                           + CASE WHEN 9  =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO9  ELSE 0 END
                           + CASE WHEN 10 =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO10 ELSE 0 END
                           + CASE WHEN 11 =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO11 ELSE 0 END
                           + CASE WHEN 12 =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO12 ELSE 0 END
                           + CASE WHEN 13 =s$mesFinal$s   THEN PLAN_CONTABLE.SALDO13 ELSE 0 END
                           )                                                                        "Saldo Libro Tesorería"
  FROM PLAN_CONTABLE  LEFT JOIN CUENTABANCOS 
      ON PLAN_CONTABLE.COMPANIA=CUENTABANCOS.COMPANIA
      AND PLAN_CONTABLE.ANO=CUENTABANCOS.ANO
      AND PLAN_CONTABLE.CODIGO=CUENTABANCOS.IDCONTABLE
    LEFT JOIN BANCO BN
      ON CUENTABANCOS.COMPANIA=BN.COMPANIA
      AND CUENTABANCOS.BANCO=BN.BANCO
    LEFT JOIN   FUENTE_RECURSOS 
      ON  CUENTABANCOS.COMPANIA=FUENTE_RECURSOS.COMPANIA
      AND CUENTABANCOS.ANO=FUENTE_RECURSOS.ANO
      AND CUENTABANCOS.RECURSOS=FUENTE_RECURSOS.CODIGO

LEFT JOIN (SELECT DETALLE_COMPROBANTE_CNT.COMPANIA,DETALLE_COMPROBANTE_CNT.ANO,DETALLE_COMPROBANTE_CNT.CODIGO_CUENTA,SUM(DETALLE_COMPROBANTE_CNT.VALOR_DEBITO) INGRESOS
            FROM V_DETALLE_AUXILIAR_CNT  DETALLE_COMPROBANTE_CNT 
            WHERE DETALLE_COMPROBANTE_CNT.COMPANIA=s$compania$s
                  AND DETALLE_COMPROBANTE_CNT.ANO=s$ano$s
                  AND DETALLE_COMPROBANTE_CNT.MES BETWEEN  s$mesInicial$s AND s$mesFinal$s 
                  AND DETALLE_COMPROBANTE_CNT.CLASE_CONTABLE In ('I','B','S')
                  AND DETALLE_COMPROBANTE_CNT.CLASECUENTA In ('B')
                  AND INSTR(PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(PCK_SYSMAN_UTL.FC_PAR(DETALLE_COMPROBANTE_CNT.COMPANIA,'TIPO COMPROBANTE DE TRASLADO',1,DETALLE_COMPROBANTE_CNT.FECHA)),
                      '' || DETALLE_COMPROBANTE_CNT.TIPO_CPTE || '',1)=0
            GROUP BY DETALLE_COMPROBANTE_CNT.COMPANIA,DETALLE_COMPROBANTE_CNT.ANO, DETALLE_COMPROBANTE_CNT.CODIGO_CUENTA
            )INGRESOS
    ON  PLAN_CONTABLE.COMPANIA=INGRESOS.COMPANIA
    AND PLAN_CONTABLE.ANO=INGRESOS.ANO
    AND PLAN_CONTABLE.CODIGO=INGRESOS.CODIGO_CUENTA
            
WHERE     PLAN_CONTABLE.COMPANIA=s$compania$s
      AND PLAN_CONTABLE.ANO=s$ano$s 
      AND PLAN_CONTABLE.CLASECUENTA IN ('B')
      AND (PLAN_CONTABLE.MOVIMIENTO NOT IN (0) OR PLAN_CONTABLE.MAN_AUX_TER NOT IN (0)
                OR  PLAN_CONTABLE.MAN_AUX_GEN NOT IN (0) OR PLAN_CONTABLE.MAN_AUX_FUE NOT IN (0)
                OR PLAN_CONTABLE.MAN_CEN_CTO NOT IN(0) OR PLAN_CONTABLE.MAN_AUX_REF NOT IN (0)) 
