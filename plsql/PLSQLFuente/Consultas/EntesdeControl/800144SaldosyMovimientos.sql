SELECT
  ID CODIGO,
  NOMBRE,
  SUM(CASE s$trimestre$s 
        WHEN 1 THEN SALDO0
        WHEN 2 THEN SALDO3
        WHEN 3 THEN SALDO6
        WHEN 4 THEN SALDO9
      END) SALDOINICIAL,
  SUM(CASE s$trimestre$s 
        WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
        WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
        WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
        WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
      END) DEBITO,
  SUM(CASE s$trimestre$s 
        WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
        WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
        WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
        WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
      END) CREDITO,
  SUM(CASE WHEN NATURALEZA IN('D')
        THEN 
          CASE s$trimestre$s  
            WHEN 1 THEN SALDO0  
            WHEN 2 THEN SALDO3 
            WHEN 3 THEN SALDO6 
            WHEN 4 THEN SALDO9 
          END +
          CASE s$trimestre$s 
            WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
            WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
            WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
            WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
          END -
          CASE s$trimestre$s  
            WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
            WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
            WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
            WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
          END
      ELSE 
         CASE s$trimestre$s  
            WHEN 1 THEN SALDO0  
            WHEN 2 THEN SALDO3 
            WHEN 3 THEN SALDO6 
            WHEN 4 THEN SALDO9 
          END +
          CASE s$trimestre$s  
            WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
            WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
            WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
            WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
          END
           -
          CASE s$trimestre$s 
            WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
            WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
            WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
            WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
          END
        END)SALDOFINAL,
  SUM(CASE WHEN  CORRIENTE IN(0)
        THEN 
          CASE WHEN NATURALEZA IN('D')
            THEN 
              CASE s$trimestre$s  
                WHEN 1 THEN SALDO0  
                WHEN 2 THEN SALDO3 
                WHEN 3 THEN SALDO6 
                WHEN 4 THEN SALDO9 
              END +
              CASE s$trimestre$s 
                WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
                WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
                WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
                WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
              END -
              CASE s$trimestre$s
                WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
                WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
                WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
                WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
              END
          ELSE 
             CASE s$trimestre$s 
                WHEN 1 THEN SALDO0  
                WHEN 2 THEN SALDO3 
                WHEN 3 THEN SALDO6 
                WHEN 4 THEN SALDO9 
              END +
              CASE s$trimestre$s 
                WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
                WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
                WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
                WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
              END
               -
              CASE s$trimestre$s
                WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
                WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
                WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
                WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
              END
            END
        ELSE 
          0
        END) SALDOFINALCORRIENTE,
        SUM(CASE WHEN  CORRIENTE NOT IN(0)
        THEN 
          CASE WHEN NATURALEZA IN('D')
            THEN 
              CASE s$trimestre$s 
                WHEN 1 THEN SALDO0  
                WHEN 2 THEN SALDO3 
                WHEN 3 THEN SALDO6 
                WHEN 4 THEN SALDO9 
              END +
              CASE s$trimestre$s
                WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
                WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
                WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
                WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
              END -
              CASE s$trimestre$s
                WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
                WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
                WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
                WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
              END
          ELSE 
             CASE s$trimestre$s
                WHEN 1 THEN SALDO0  
                WHEN 2 THEN SALDO3 
                WHEN 3 THEN SALDO6 
                WHEN 4 THEN SALDO9 
              END +
              CASE s$trimestre$s
                WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
                WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
                WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
                WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
              END
               -
              CASE s$trimestre$s
                WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
                WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
                WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
                WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
              END
            END
        ELSE 
          0
        END) SALDOFINALNOCORRIENTE
FROM V_PLAN_CONTABLE
WHERE COMPANIA    = s$compania$s
  AND ANO       = s$ano$s
  AND LENGTH(ID)<= 6
GROUP BY  
  ID,
  NOMBRE,
  NATURALEZA,
  CORRIENTE    
HAVING SUM( CASE s$trimestre$s
              WHEN 1 THEN SALDO0
              WHEN 2 THEN SALDO3
              WHEN 3 THEN SALDO6
              WHEN 4 THEN SALDO9
            END) <> 0   
       OR 
       SUM( CASE s$trimestre$s 
              WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
              WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
              WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
              WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
            END) <> 0 
       OR 
       SUM( CASE s$trimestre$s 
              WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
              WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
              WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
              WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
            END) <> 0
       OR
       SUM(CASE WHEN NATURALEZA IN('D')
        THEN 
          CASE s$trimestre$s 
            WHEN 1 THEN SALDO0  
            WHEN 2 THEN SALDO3 
            WHEN 3 THEN SALDO6 
            WHEN 4 THEN SALDO9 
          END +
          CASE s$trimestre$s
            WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
            WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
            WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
            WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
          END -
          CASE s$trimestre$s 
            WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
            WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
            WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
            WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
          END
      ELSE 
         CASE s$trimestre$s 
            WHEN 1 THEN SALDO0  
            WHEN 2 THEN SALDO3 
            WHEN 3 THEN SALDO6 
            WHEN 4 THEN SALDO9 
          END +
          CASE s$trimestre$s 
            WHEN 1 THEN CREDITO1 + CREDITO2 + CREDITO3
            WHEN 2 THEN CREDITO4 + CREDITO5 + CREDITO6
            WHEN 3 THEN CREDITO7 + CREDITO8 + CREDITO9
            WHEN 4 THEN CREDITO10 + CREDITO11 + CREDITO12
          END
           -
          CASE s$trimestre$s
            WHEN 1 THEN DEBITO1 + DEBITO2 + DEBITO3
            WHEN 2 THEN DEBITO4 + DEBITO5 + DEBITO6
            WHEN 3 THEN DEBITO7 + DEBITO8 + DEBITO9
            WHEN 4 THEN DEBITO10 + DEBITO11 + DEBITO12
          END
        END) <> 0 
    ORDER BY ID 
