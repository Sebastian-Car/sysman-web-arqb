CREATE OR REPLACE TRIGGER "BIU_APROPIACIONINICIAL"  
/*
      NAME              : BIU_APROPIACIONINICIAL
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 
      TIME              : 
      SOURCE MODULE     : 
      MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MODIFIED     : 31/01/2017
      TIME              : 09:05 AM
      DESCRIPTION       : SE AJUSTA AL ESTANDAR                          
*/ 
BEFORE INSERT OR UPDATE OF COMPANIA, ANO, CODIGO, TERCERO, SUCURSAL, AUXILIAR, CENTRO_COSTO, REFERENCIA, FUENTE_RECURSO, APROPIACIONINICIAL 
ON APROPIACIONESINICIALES
FOR EACH ROW
DECLARE
  MI_NATURALEZA VARCHAR2(1);
  MI_VALOR      NUMBER;
  MI_REEMPLAZOS PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
  BEGIN 
    PCK_PRESUPUESTO.PR_VALIDARDISPONIBLE (UN_COMPANIA           => :NEW.COMPANIA, 
                                          UN_ANIO               => :NEW.ANO, 
                                          UN_CODIGO             => :NEW.CODIGO,   
                                          UN_APROPIACIONINICIAL => :NEW.APROPIACIONINICIAL,
                                          UN_TERCERO            => :NEW.TERCERO,
                                          UN_SUCURSAL           => :NEW.SUCURSAL,
                                          UN_AUXILIAR           => :NEW.AUXILIAR,
                                          UN_CENTRO_COSTO       => :NEW.CENTRO_COSTO,
                                          UN_REFERENCIA         => :NEW.REFERENCIA,
                                          UN_FUENTE_RECURSO     => :NEW.FUENTE_RECURSO);
    SELECT NATURALEZA
      INTO MI_NATURALEZA
      FROM PLAN_PRESUPUESTAL
     WHERE COMPANIA = :NEW.COMPANIA
       AND ANO      = :NEW.ANO
       AND CODIGO   = :NEW.CODIGO;
  EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_NATURALEZA := NULL;
  END;  
  MI_VALOR     := :NEW.APROPIACIONINICIAL;
  BEGIN                   
    IF MI_NATURALEZA IS NOT NULL THEN
      IF MI_NATURALEZA = 'D' THEN
        :NEW.DEBITO  := MI_VALOR ;
        :NEW.CREDITO := 0;
      ELSIF MI_NATURALEZA = 'C' THEN
        :NEW.CREDITO := MI_VALOR ;
        :NEW.DEBITO  := 0;
      END IF;
    ELSE 
      RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
      --RAISE_APPLICATION_ERROR(-20000, 'No es posible realizar esta modificación debido a que no es posible determinar la naturaleza de la cuenta.'  );
    END IF;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRESUPUESTO THEN  
      MI_REEMPLAZOS(1).CLAVE := 'RUBRO';
      MI_REEMPLAZOS(1).VALOR := :NEW.CODIGO;
      MI_REEMPLAZOS(2).CLAVE := 'ANIO';
      MI_REEMPLAZOS(2).VALOR := :NEW.ANO;
    PCK_ERR_MSG.RAISE_WITH_MSG( 
                UN_EXC_COD    => SQLCODE
               ,UN_ERROR_COD  => PCK_ERRORES.ERR_PPTO_NODETENATURA
               ,UN_TABLAERROR => 'APROPIACIONINICIAL' 
               ,UN_REEMPLAZOS => MI_REEMPLAZOS
               );  
  END;
END;