CREATE OR REPLACE TRIGGER "CIUD_DETALLE_COMPR_CNT_AFE"  
FOR INSERT OR DELETE OR UPDATE OF  
                             COMPANIA
                           , ANO
                           , TIPO_CPTE
                           , COMPROBANTE
                           , CUENTA
                           , ANO_AFECT
                           , TIPO_CPTE_AFECT
                           , CMPTE_AFECTADO 
                           , CONSECUTIVO
                           , CONSECUTIVOAFECTADO
                           , VALOR_DEBITO
                           , VALOR_CREDITO
ON DETALLE_COMPROBANTE_CNT
REFERENCING OLD AS OLD NEW AS NEW
COMPOUND TRIGGER 
MI_RTA                 PCK_SUBTIPOS.TI_RTA_ACME;
MI_EXISTE              PCK_SUBTIPOS.TI_LOGICO;
MI_PARAMETRO           PCK_SUBTIPOS.TI_PARAMETRO;
  TYPE REGISTRO IS RECORD 
  (
  COMPANIA            PCK_SUBTIPOS.TI_COMPANIA,
  ANO                 PCK_SUBTIPOS.TI_ANIO,
  TIPO_CPTE           PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  COMPROBANTE         PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
  CUENTA              PCK_SUBTIPOS.TI_CODIGOCONTA,
  ANO_AFEC            PCK_SUBTIPOS.TI_ANIO,
  TIPO_CPTE_AFECT     PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  COMPROBANTE_AFECT   PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
  CONSECUTIVO         PCK_SUBTIPOS.TI_CONSECUTIVOCNT,
  CONSECUTIVOAFECT    PCK_SUBTIPOS.TI_CONSECUTIVOCNT,
  VALOR_DEBITONUE     PCK_SUBTIPOS.TI_DOBLE,
  VALOR_CREDITONUE    PCK_SUBTIPOS.TI_DOBLE,
  VALOR_DEBITOANT     PCK_SUBTIPOS.TI_DOBLE,
  VALOR_CREDITOANT    PCK_SUBTIPOS.TI_DOBLE,
  USUARIO             PCK_SUBTIPOS.TI_USUARIO,
  ELIMINA_AFECTADOS   PCK_SUBTIPOS.TI_LOGICO,
  FECHA               DATE
	);  
  TYPE REGISTROS IS TABLE OF REGISTRO INDEX BY BINARY_INTEGER ;
  TABLACT REGISTROS;
  MI_POSACT NUMBER DEFAULT 0;
  RTA    NUMBER;
  ACTUALIZA  NUMBER;
AFTER EACH ROW IS
BEGIN
  IF NVL(SYS_CONTEXT('SYSMAN_CTX','CIERRECONTABLE'), '0') <> '1' THEN
  ACTUALIZA:=0;
  IF UPDATING THEN
    IF :OLD.COMPANIA            <>:NEW.COMPANIA OR
       :OLD.ANO                 <>:NEW.ANO OR
       :OLD.TIPO_CPTE           <>:NEW.TIPO_CPTE OR
       :OLD.COMPROBANTE         <>:NEW.COMPROBANTE OR
       :OLD.CUENTA              <>:NEW.CUENTA OR
       :OLD.CONSECUTIVO         <>:NEW.CONSECUTIVO OR
       :OLD.CONSECUTIVOAFECTADO <>:NEW.CONSECUTIVOAFECTADO OR
       :OLD.ANO_AFECT           <>:NEW.ANO_AFECT OR
       :OLD.TIPO_CPTE_AFECT     <>:NEW.TIPO_CPTE_AFECT OR
       :OLD.CMPTE_AFECTADO      <>:NEW.CMPTE_AFECTADO THEN
      ACTUALIZA:=1;
    ELSE
      MI_POSACT := MI_POSACT+1;
      TABLACT(MI_POSACT).COMPANIA            := :NEW.COMPANIA;
      TABLACT(MI_POSACT).ANO                 := :NEW.ANO;
      TABLACT(MI_POSACT).TIPO_CPTE           := :NEW.TIPO_CPTE;
      TABLACT(MI_POSACT).COMPROBANTE         := :NEW.COMPROBANTE;
      TABLACT(MI_POSACT).CUENTA              := :NEW.CUENTA;
      TABLACT(MI_POSACT).CONSECUTIVO         := :NEW.CONSECUTIVO;
      TABLACT(MI_POSACT).CONSECUTIVOAFECT    := :NEW.CONSECUTIVOAFECTADO;
      TABLACT(MI_POSACT).VALOR_DEBITONUE     := :NEW.VALOR_DEBITO;
      TABLACT(MI_POSACT).VALOR_CREDITONUE    := :NEW.VALOR_CREDITO;
      TABLACT(MI_POSACT).VALOR_DEBITOANT     := :OLD.VALOR_DEBITO;
      TABLACT(MI_POSACT).VALOR_CREDITOANT    := :OLD.VALOR_CREDITO;
      TABLACT(MI_POSACT).ANO_AFEC            := :NEW.ANO_AFECT;
      TABLACT(MI_POSACT).TIPO_CPTE_AFECT     := :NEW.TIPO_CPTE_AFECT;
      TABLACT(MI_POSACT).COMPROBANTE_AFECT   := :NEW.CMPTE_AFECTADO;
      TABLACT(MI_POSACT).USUARIO             := :NEW.MODIFIED_BY;
      TABLACT(MI_POSACT).ELIMINA_AFECTADOS   := 1;
    END IF;
   END IF;
   IF DELETING OR ACTUALIZA<>0 THEN
       MI_POSACT := MI_POSACT+1;
       TABLACT(MI_POSACT).COMPANIA            := :OLD.COMPANIA;
       TABLACT(MI_POSACT).ANO                 := :OLD.ANO;
       TABLACT(MI_POSACT).TIPO_CPTE           := :OLD.TIPO_CPTE;
       TABLACT(MI_POSACT).COMPROBANTE         := :OLD.COMPROBANTE;
       TABLACT(MI_POSACT).CUENTA              := :OLD.CUENTA;
       TABLACT(MI_POSACT).CONSECUTIVO         := :OLD.CONSECUTIVO;
       TABLACT(MI_POSACT).CONSECUTIVOAFECT    := :OLD.CONSECUTIVOAFECTADO;
       TABLACT(MI_POSACT).VALOR_DEBITONUE     := 0;
       TABLACT(MI_POSACT).VALOR_CREDITONUE    := 0;
       TABLACT(MI_POSACT).VALOR_DEBITOANT     := :OLD.VALOR_DEBITO;
       TABLACT(MI_POSACT).VALOR_CREDITOANT    := :OLD.VALOR_CREDITO;
       TABLACT(MI_POSACT).ANO_AFEC            := :OLD.ANO_AFECT;
       TABLACT(MI_POSACT).TIPO_CPTE_AFECT     := :OLD.TIPO_CPTE_AFECT;
       TABLACT(MI_POSACT).COMPROBANTE_AFECT   := :OLD.CMPTE_AFECTADO;
       TABLACT(MI_POSACT).USUARIO             := :OLD.MODIFIED_BY;
       TABLACT(MI_POSACT).ELIMINA_AFECTADOS   := 1;
   END IF;
   
    IF INSERTING  OR ACTUALIZA<>0 THEN
       MI_POSACT := MI_POSACT+1;
       TABLACT(MI_POSACT).COMPANIA            := :NEW.COMPANIA;
       TABLACT(MI_POSACT).ANO                 := :NEW.ANO;
       TABLACT(MI_POSACT).TIPO_CPTE           := :NEW.TIPO_CPTE;
       TABLACT(MI_POSACT).COMPROBANTE         := :NEW.COMPROBANTE;
       TABLACT(MI_POSACT).CUENTA              := :NEW.CUENTA;
       TABLACT(MI_POSACT).CONSECUTIVO         := :NEW.CONSECUTIVO;
       TABLACT(MI_POSACT).CONSECUTIVOAFECT    := :NEW.CONSECUTIVOAFECTADO;
       TABLACT(MI_POSACT).VALOR_DEBITONUE     := :NEW.VALOR_DEBITO;
       TABLACT(MI_POSACT).VALOR_CREDITONUE    := :NEW.VALOR_CREDITO;
       TABLACT(MI_POSACT).VALOR_DEBITOANT     := 0;
       TABLACT(MI_POSACT).VALOR_CREDITOANT    := 0;
       TABLACT(MI_POSACT).ANO_AFEC            := :NEW.ANO_AFECT;
       TABLACT(MI_POSACT).TIPO_CPTE_AFECT     := :NEW.TIPO_CPTE_AFECT;
       TABLACT(MI_POSACT).COMPROBANTE_AFECT   := :NEW.CMPTE_AFECTADO;
       TABLACT(MI_POSACT).USUARIO             := :NEW.CREATED_BY;
       TABLACT(MI_POSACT).ELIMINA_AFECTADOS   := 0;
    END IF;
    END IF;
END AFTER EACH ROW;
AFTER STATEMENT IS
BEGIN
  IF NVL(SYS_CONTEXT('SYSMAN_CTX','CIERRECONTABLE'), '0') <> '1' THEN
  FOR i IN 1..MI_POSACT LOOP
       PCK_CONTABILIDAD6.PR_ACTUALIZARAFECTADOS( UN_COMPANIA            => TABLACT(i).COMPANIA
                                                ,UN_ANO_AFECT           => TABLACT(i).ANO_AFEC
                                                ,UN_ANO                 => TABLACT(i).ANO
                                                ,UN_COMPROBANTE         => TABLACT(i).COMPROBANTE
                                                ,UN_TIPO_CPTE           => TABLACT(i).TIPO_CPTE
                                                ,UN_CTIPOCPTEAFECT      => TABLACT(i).TIPO_CPTE_AFECT
                                                ,UN_CCMPTEAFECTADO      => TABLACT(i).COMPROBANTE_AFECT
                                                ,UN_CVALORDEBITO        => TABLACT(i).VALOR_DEBITOANT
                                                ,UN_CVALORDEBITOACT     => TABLACT(i).VALOR_DEBITONUE
                                                ,UN_CVALORCREDITO       => TABLACT(i).VALOR_CREDITOANT
                                                ,UN_CVALORCREDITOACT    => TABLACT(i).VALOR_CREDITONUE 
                                                ,UN_CUENTA              => TABLACT(i).CUENTA
                                                ,UN_CONSECUTIVO         => TABLACT(i).CONSECUTIVOAFECT
                                                ,UN_USUARIO             => TABLACT(i).USUARIO);      
                                                
       PCK_CONTABILIDAD6.PR_CALCULAR_VALORAGIRAR( UN_COMPANIA            => TABLACT(i).COMPANIA
                                                ,UN_ANIO                 => TABLACT(i).ANO
                                                ,UN_TIPO                 => TABLACT(i).TIPO_CPTE
                                                ,UN_NUMERO               => TABLACT(i).COMPROBANTE
                                                ,UN_VLRDOCUMENTO         => 0
                                                ,UN_VLRGIRARDG           => 0);
                                                
                                                
        IF TABLACT(i).ELIMINA_AFECTADOS <>0 THEN
          IF TABLACT(i).ANO_AFEC          IS NOT NULL OR 
             TABLACT(i).TIPO_CPTE_AFECT   IS NOT NULL OR
             TABLACT(i).COMPROBANTE_AFECT IS NOT NULL  THEN
            PCK_CONTABILIDAD6.PR_ELIMINARAFECTADOS( UN_COMPANIA            => TABLACT(i).COMPANIA
                                                   ,UN_ANO                 => TABLACT(i).ANO
                                                   ,UN_TIPO_CPTE           => TABLACT(i).TIPO_CPTE
                                                   ,UN_COMPROBANTE         => TABLACT(i).COMPROBANTE
                                                   ,UN_ANO_AFEC            => TABLACT(i).ANO_AFEC
                                                   ,UN_TIPO_CPTE_AFECT     => TABLACT(i).TIPO_CPTE_AFECT
                                                   ,UN_COMPROBANTE_AFECT   => TABLACT(i).COMPROBANTE_AFECT);
          END IF;
      END IF;
      SELECT COUNT(*) INTO MI_EXISTE 
                      FROM SF_FACTURA 
                      WHERE TIPOCOBRO IN (TABLACT(i).TIPO_CPTE_AFECT) 
                      AND CODIGO_COBRO IN(NVL(TABLACT(i).COMPROBANTE_AFECT,0)); 
      MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(TABLACT(i).COMPANIA,'MANEJA RECAUDO PARCIAL DESDE TESORERIA',PCK_DATOS.MODULOCONTABILIDAD,SYSDATE),'NO');
      IF MI_PARAMETRO = 'SI' AND MI_EXISTE <> 0 THEN
          IF TABLACT(i).TIPO_CPTE_AFECT IS NOT NULL OR TABLACT(i).COMPROBANTE_AFECT IS NOT NULL THEN 
           PCK_CONTABILIDAD3.PR_ACTPAGOS_FACT(
                  UN_COMPANIA          =>TABLACT(i).COMPANIA, 
                  UN_FECHA_INI         =>'', 
                  UN_FECHA_FIN         =>'', 
                  UN_TIPO_COBRO_INI    =>TABLACT(i).TIPO_CPTE_AFECT, 
                  UN_TIPO_COBRO_FIN    =>TABLACT(i).TIPO_CPTE_AFECT, 
                  UN_FACTURA_INI       =>TABLACT(i).COMPROBANTE_AFECT, 
                  UN_FACTURA_FIN       =>TABLACT(i).COMPROBANTE_AFECT,
                  UN_GENERAL           =>'0');
            END IF;
     END IF;
     
      MI_PARAMETRO := NVL(PCK_SYSMAN_UTL.FC_PAR(TABLACT(i).COMPANIA,'MANEJA FACTURACIÓN CON IVA',PCK_DATOS.MODULOCONTABILIDAD,SYSDATE),'NO');
     IF MI_PARAMETRO = 'SI' THEN
        PCK_CONTABILIDAD7.PR_ACT_VALOR_IVA(UN_COMPANIA         => TABLACT(i).COMPANIA,
                                           UN_TIPO             => TABLACT(i).TIPO_CPTE,
                                           UN_ANIO             => TABLACT(i).ANO,
                                           UN_COMPROBANTE      => TABLACT(i).COMPROBANTE,
                                           UN_CONSECUTIVO      => TABLACT(i).CONSECUTIVO,
                                           UN_TIPO_AFEC        => TABLACT(i).TIPO_CPTE_AFECT,
                                           UN_ANIO_AFEC        => TABLACT(i).ANO_AFEC,
                                           UN_COMPROBANTE_AFEC => TABLACT(i).COMPROBANTE_AFECT,
                                           UN_VALOR_NUEVO      => TABLACT(i).VALOR_CREDITONUE,
                                           UN_CUENTA           => TABLACT(i).CUENTA);
     END IF;
     
          PCK_CONTABILIDAD6.PR_AFECTARADICIONES( UN_COMPANIA             => TABLACT(i).COMPANIA
                                                ,UN_ANO                  => TABLACT(i).ANO
                                                ,UN_TIPO_CPTE            => TABLACT(i).TIPO_CPTE
                                                ,UN_COMPROBANTE          => TABLACT(i).COMPROBANTE
                                                ,UN_ANO_AFEC             => TABLACT(i).ANO_AFEC
                                                ,UN_TIPO_CPTE_AFECT      => TABLACT(i).TIPO_CPTE_AFECT
                                                ,UN_COMPROBANTE_AFECT    => TABLACT(i).COMPROBANTE_AFECT
                                                ,UN_CONSECUTIVOAFECTADO  => TABLACT(i).CONSECUTIVOAFECT
                                                ,UN_CVALORDEBITO         => TABLACT(i).VALOR_DEBITOANT
                                                ,UN_CVALORDEBITOACT      => TABLACT(i).VALOR_DEBITONUE
                                                ,UN_CVALORCREDITO        => TABLACT(i).VALOR_CREDITOANT
                                                ,UN_CVALORCREDITOACT     => TABLACT(i).VALOR_CREDITONUE
                                                ,UN_USUARIO              => TABLACT(i).USUARIO);
     
  END LOOP;                                   
  END IF;
  END AFTER STATEMENT;
END;