WITH QRY_NOVE_BANCO AS
  (SELECT  
      BPNOVEDADPROYECTO.COMPANIA,  
      BPNOVEDADPROYECTO.TIPOT,  
      BPNOVEDADPROYECTO.CLASET,  
      BPNOVEDADPROYECTO.CODIGO,  
      BPNOVEDADPROYECTO.DEPENDENCIA,  
      BPNOVEDADPROYECTO.DOCUMENTO_AFECTAR,  
      BPNOVEDADPROYECTO.DEPENDENCIA_AFECTAR,  
      BPNOVEDADPROYECTO.TIPOT_AFECTAR,  
      BPNOVEDADPROYECTO.CLASET_AFECTAR 
    FROM BPNOVEDADPROYECTO 
    WHERE BPNOVEDADPROYECTO.CLASET In ('B')  
      AND  
    BPNOVEDADPROYECTO.ESTADO Not In ('A','N')
  ), QRY_NOVE_PRESU AS
  (SELECT  
        BPNOVEDADPROYECTO.COMPANIA,  
        BPNOVEDADPROYECTO.TIPOT,  
        BPNOVEDADPROYECTO.CLASET,  
        BPNOVEDADPROYECTO.CODIGO,  
        BPNOVEDADPROYECTO.DEPENDENCIA,  
        BPNOVEDADPROYECTO.DOCUMENTO_AFECTAR,  
        BPNOVEDADPROYECTO.DEPENDENCIA_AFECTAR,  
        BPNOVEDADPROYECTO.TIPOT_AFECTAR,  
        BPNOVEDADPROYECTO.CLASET_AFECTAR 
      FROM BPNOVEDADPROYECTO  
          INNER JOIN COMPROBANTE_PPTAL  
          ON  
            BPNOVEDADPROYECTO.COMPANIA = COMPROBANTE_PPTAL.COMPANIA  
              AND  
            BPNOVEDADPROYECTO.VIGENCIA = COMPROBANTE_PPTAL.ANO 
              AND  
            BPNOVEDADPROYECTO.TIPOT    = COMPROBANTE_PPTAL.TIPO   
              AND  
            BPNOVEDADPROYECTO.CODIGO   =  COMPROBANTE_PPTAL.NUMERO 
    WHERE BPNOVEDADPROYECTO.CLASET In ('P')  
      AND  
    BPNOVEDADPROYECTO.ESTADO Not In ('A','N')
  )
SELECT DISTINCT  
    DN.COMPANIA, 
    DN.PROYECTO, 
    N.CODIGO, 
    N.VIGENCIA, 
    DEP.NOMBRE, 
    CASE N.ESTADO WHEN 'V' THEN  'VIGENTE' 
    ELSE 
        CASE WHEN  
           N.ESTADO = 'A' 
       THEN  
           'ANULADA' 
       ELSE 
           CASE WHEN  
              N.ESTADO = 'N' 
          THEN  
              'NO APROBADO' 
          ELSE 
              CASE WHEN  
                 N.ESTADO = 'C' 
             THEN  
                 'CAMBIO' 
             ELSE 
                 CASE WHEN  
                    N.ESTADO = 'AP' 
                THEN  
                    'APROBADA' 
                ELSE 
                    'NO ESTA CONFIGURADA' 
                END 
             END 
          END 
       END 
    END  ESTADO1,  
    N.FECHA, 
    N.OBJETO,  
    CASE WHEN  
        N.CONCOMPROBANTEPPTAL = - 1 
    THEN  
        'SI' 
    ELSE 
        CASE WHEN  
           N.CONCOMPROBANTEPPTAL = 0 
       THEN  
           'NO' 
       ELSE 
           'N/A' 
       END 
    END  COMPPTAL,  
    N.OBSERVACIONES,  
    N.TIPOT,  
    CASE WHEN  
        N.AFECTADO = - 1 
    THEN  
        'AFECTADAS' 
    ELSE 
        CASE WHEN  
           N.AFECTADO = 0  
       THEN  
           'NO AFECTADAS' 
       ELSE 
           'N/A'  
       END 
    END  AFECTADO,  
    N.CLASET,  
    N.DEPENDENCIA,  
    CASE WHEN  
        N.VOBOBP = 0 
    THEN  
        'NO' 
    ELSE 
        CASE WHEN  
           N.VOBOBP = - 1 
       THEN  
           'SI' 
       ELSE 
           'FALTA' 
       END 
    END  VOBOBP1,  
    QRY_NOVE_BANCO.CODIGO  B_AFEC_C,  
    QRY_NOVE_BANCO.TIPOT  B_AFEC_T,  
    QRY_NOVE_PRESU.CODIGO  P_AFEC_C,  
    QRY_NOVE_PRESU.TIPOT  P_AFEC_T
FROM  
        BP_D_NOVEDADPROYECTO  DN  
            INNER JOIN BPNOVEDADPROYECTO  N  
            ON  
                DN.COMPANIA = N.COMPANIA
                    AND  
                DN.NOVEDAD = N.CODIGO  
                    AND  
                DN.CLASET  = N.CLASET  
                    AND  
                DN.TIPOT   = N.TIPOT 
                    AND  
                DN.DEPENDENCIA = N.DEPENDENCIA   
        LEFT JOIN QRY_NOVE_BANCO  
          ON  
              N.COMPANIA = QRY_NOVE_BANCO.COMPANIA  
                  AND  
              N.DEPENDENCIA = QRY_NOVE_BANCO.DEPENDENCIA_AFECTAR  
                  AND  
              N.CODIGO = QRY_NOVE_BANCO.DOCUMENTO_AFECTAR 
                  AND  
              N.CLASET = QRY_NOVE_BANCO.CLASET_AFECTAR  
                  AND  
              N.TIPOT = QRY_NOVE_BANCO.TIPOT_AFECTAR 
        LEFT JOIN QRY_NOVE_PRESU  
    ON  
        N.COMPANIA = QRY_NOVE_PRESU.COMPANIA
            AND  
        N.CODIGO = QRY_NOVE_PRESU.DOCUMENTO_AFECTAR  
            AND  
        N.CLASET = QRY_NOVE_PRESU.CLASET_AFECTAR  
            AND  
        N.TIPOT = QRY_NOVE_PRESU.TIPOT_AFECTAR 
            AND  
        N.DEPENDENCIA = QRY_NOVE_PRESU.DEPENDENCIA_AFECTAR    
    LEFT JOIN (SELECT DEPENDENCIA.CODIGO, DEPENDENCIA.NOMBRE 
                FROM DEPENDENCIA) DEP 
      ON N.DEPENDENCIA = DEP.CODIGO  
WHERE 
      N.COMPANIA = s$compania$s
          AND  
      N.TIPOT = 'SCD'
     s$condicion$s
