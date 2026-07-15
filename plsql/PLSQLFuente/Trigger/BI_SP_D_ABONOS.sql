CREATE OR REPLACE TRIGGER "BI_SP_D_ABONOS" 
  /*
      NAME              : BI_SP_D_ABONOS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 05/06/2017
      TIME              : 09:48 PM
      SOURCE MODULE     :
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : Inserta o actualiza los valores en las tablas SP_FACTURADO
                          SP_D_RECAUDOS_USUARIO, SP_D_RECAUDOS.

  */
FOR INSERT ON SP_D_ABONOS
REFERENCING OLD AS OLD NEW AS NEW
COMPOUND TRIGGER

MI_RTA             NUMBER DEFAULT 0;
MI_TABLA           PCK_SUBTIPOS.TI_STRSQL;
MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
MI_MSGERROR        PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_CAMBIOCICLO     SP_USUARIO.CAMBIOCICLORUTA%TYPE;

BEFORE EACH ROW IS  --Ejecución antes de cada fila
BEGIN

    SELECT CAMBIOCICLORUTA
    INTO   MI_CAMBIOCICLO
    FROM   SP_USUARIO
    WHERE  COMPANIA   = :NEW.COMPANIA
      AND  CICLO      = :NEW.CICLO
      AND  CODIGORUTA = :NEW.CODIGORUTA;
    --VALIDA POR CADA FILA QUE SE PUEDA INSERTAR
    MI_RTA :=0;
    IF MI_CAMBIOCICLO = 0 THEN
      MI_RTA := PCK_SERVICIOS_PUBLICOS_ABONOS.FC_VALIDARABONO
                 (UN_COMPANIA     =>  :NEW.COMPANIA
                 ,UN_CODIGORUTA   =>  :NEW.CODIGORUTA
                 ,UN_CICLO        =>  :NEW.CICLO
                 ,UN_ANO          =>  :NEW.ANO
                 ,UN_PERIODO      =>  :NEW.PERIODO
                 ,UN_ACCION       =>  'INSERTA DETALLE');
    END IF;
    
END BEFORE EACH ROW;

AFTER EACH ROW IS --Ejecución despues de cada fila,
BEGIN
    IF MI_RTA <>0 THEN  --Si se permite Insertar.
        --Toma el valor que se está registrado por concepto y lo actualiza en facturado.
        PCK_SERVICIOS_PUBLICOS_ABONOS.PR_REGISTRARABONODETALLE
          ( UN_COMPANIA       =>  :NEW.COMPANIA
           ,UN_CICLO          =>  :NEW.CICLO
           ,UN_CODIGORUTA     =>  :NEW.CODIGORUTA
           ,UN_ANO            =>  :NEW.ANO
           ,UN_PERIODO        =>  :NEW.PERIODO
           ,UN_CONSECUTIVO    =>  :NEW.CONSECUTIVO
           ,UN_CONCEPTO       =>  :NEW.CONCEPTO
           ,UN_VALORACT       =>  :NEW.VALORACT
           ,UN_VALORANT       =>  :NEW.VALORANT
           ,UN_USUARIO        =>  :NEW.CREATED_BY );

        --Toma el valor que se esta´registrando y actualiza D_RECAUDOS_ABONO.

    END IF;

END AFTER EACH ROW;


END;
