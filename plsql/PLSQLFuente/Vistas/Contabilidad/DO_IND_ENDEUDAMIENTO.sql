CREATE OR REPLACE VIEW DO_IND_ENDEUDAMIENTO AS
SELECT COMPANIA, 
       ANO, 
       LPAD(MES,2,0) || '-' || MES_NOMBRE MES_NOMBRE,
       SUM(CASE WHEN CLASE = '2' THEN SALDO ELSE 0 END) TOTALPASIVO,
       SUM(CASE WHEN CLASE = '1' THEN SALDO ELSE 0 END) TOTALACTIVO,
       SUM(CASE WHEN CLASE = '3' THEN SALDO ELSE 0 END) TOTALPATRIMONIO,
       SUM(CASE WHEN CLASE = '2' AND CORRIENTE NOT IN(0) THEN SALDO ELSE 0 END) PASIVOCORRIENTE,
       SUM(UTILIDADNETA) UTILIDADNETA,

       CASE WHEN SUM(CASE WHEN CLASE = '1' THEN SALDO ELSE 0 END) = 0
        THEN 0
        ELSE (SUM(CASE WHEN CLASE = '2' THEN SALDO ELSE 0 END) *100) /
              SUM(CASE WHEN CLASE = '1' THEN SALDO ELSE 0 END)
        END RAZON_DEUDA,

       CASE WHEN SUM(CASE WHEN CLASE = '2' THEN SALDO ELSE 0 END) = 0
        THEN 0
        ELSE (SUM(CASE WHEN CLASE = '2' AND CORRIENTE NOT IN(0) THEN SALDO ELSE 0 END) *100) /
             SUM(CASE WHEN CLASE = '2' THEN SALDO ELSE 0 END)
        END END_CORTOPLAZO,  

        CASE WHEN SUM(CASE WHEN CLASE = '3' THEN SALDO ELSE 0 END) = 0
        THEN 0
        ELSE (SUM(CASE WHEN CLASE = '2' THEN SALDO ELSE 0 END)) /
              SUM(CASE WHEN CLASE = '3' THEN SALDO ELSE 0 END)
        END APALANCAMIENTO,

        CASE WHEN SUM(CASE WHEN CLASE = '1' THEN SALDO ELSE 0 END) = 0
        THEN 0
        ELSE SUM(UTILIDADNETA) /
              SUM(CASE WHEN CLASE = '1' THEN SALDO ELSE 0 END)
        END ROA,

        CASE WHEN SUM(CASE WHEN CLASE = '3' THEN SALDO ELSE 0 END) = 0
        THEN 0
        ELSE SUM(UTILIDADNETA) /
              SUM(CASE WHEN CLASE = '3' THEN SALDO ELSE 0 END)
        END ROE
FROM
     (  SELECT PLAN_CONTABLE.COMPANIA, 
                PLAN_CONTABLE.ANO, 
                MES.NUMERO MES,
                MES.NOMBRE MES_NOMBRE,
                PLAN_CONTABLE.CORRIENTE,
                SUBSTR(PLAN_CONTABLE.CODIGO, 1, 1) CLASE, 
                CASE MES.NUMERO WHEN 0 THEN PLAN_CONTABLE.SALDO0
                                WHEN 1 THEN PLAN_CONTABLE.SALDO1
                                WHEN 2 THEN PLAN_CONTABLE.SALDO2
                                WHEN 3 THEN PLAN_CONTABLE.SALDO3
                                WHEN 4 THEN PLAN_CONTABLE.SALDO4 
                                WHEN 5 THEN PLAN_CONTABLE.SALDO5        
                                WHEN 6 THEN PLAN_CONTABLE.SALDO6 
                                WHEN 7 THEN PLAN_CONTABLE.SALDO7 
                                WHEN 8 THEN PLAN_CONTABLE.SALDO8 
                                WHEN 9 THEN PLAN_CONTABLE.SALDO9
                                WHEN 10 THEN PLAN_CONTABLE.SALDO10
                                WHEN 11 THEN PLAN_CONTABLE.SALDO11
                                WHEN 12 THEN PLAN_CONTABLE.SALDO12
                                WHEN 13 THEN PLAN_CONTABLE.SALDO13
                                ELSE 0 END SALDO,
                 CASE WHEN PLAN_CONTABLE.CODIGO = '311001' THEN 
                     CASE MES.NUMERO WHEN 0 THEN PLAN_CONTABLE.SALDO0
                                    WHEN 1 THEN PLAN_CONTABLE.SALDO1
                                    WHEN 2 THEN PLAN_CONTABLE.SALDO2
                                    WHEN 3 THEN PLAN_CONTABLE.SALDO3
                                    WHEN 4 THEN PLAN_CONTABLE.SALDO4 
                                    WHEN 5 THEN PLAN_CONTABLE.SALDO5        
                                    WHEN 6 THEN PLAN_CONTABLE.SALDO6 
                                    WHEN 7 THEN PLAN_CONTABLE.SALDO7 
                                    WHEN 8 THEN PLAN_CONTABLE.SALDO8 
                                    WHEN 9 THEN PLAN_CONTABLE.SALDO9
                                    WHEN 10 THEN PLAN_CONTABLE.SALDO10
                                    WHEN 11 THEN PLAN_CONTABLE.SALDO11
                                    WHEN 12 THEN PLAN_CONTABLE.SALDO12
                                    WHEN 13 THEN PLAN_CONTABLE.SALDO13
                                    ELSE 0 END
                    ELSE 0 END UTILIDADNETA
        FROM PLAN_CONTABLE INNER JOIN SALDO_AUX_CONTABLE
          ON PLAN_CONTABLE.COMPANIA = SALDO_AUX_CONTABLE.COMPANIA
         AND PLAN_CONTABLE.ANO      = SALDO_AUX_CONTABLE.ANO
         AND PLAN_CONTABLE.CODIGO   = SALDO_AUX_CONTABLE.CODIGO
         INNER JOIN MES
          ON PLAN_CONTABLE.COMPANIA = MES.COMPANIA
         AND PLAN_CONTABLE.ANO      = MES.ANO
        WHERE SUBSTR(PLAN_CONTABLE.CODIGO, 1, 1) IN('1', '2', '3')
   ) T
   GROUP BY COMPANIA, 
       ANO, 
       LPAD(MES,2,0) || '-' || MES_NOMBRE
;
COMMENT ON TABLE DO_IND_ENDEUDAMIENTO IS 'Permite generar reporte de DASHONE de los indicadores de razón de deuda, endeudamiento a corto plazo, ROA, ROE'
;
