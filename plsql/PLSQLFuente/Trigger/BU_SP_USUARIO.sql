CREATE OR REPLACE TRIGGER "BU_SP_USUARIO" 
/*
    NAME              : BU_SP_USUARIO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
    DATE MIGRADOR     : 26/10/2017
    TIME              : 05:09 PM
    SOURCE MODULE     :
    <MODIFIER         :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Trigger que se encarga de actualizar los campos de cambio de estado y estrato.

*/
--BEFORE UPDATE OF ESTADO ON SP_USUARIO
BEFORE UPDATE ON SP_USUARIO
FOR EACH ROW
BEGIN
    IF :NEW.CAMBIOCICLORUTA = 0 THEN
        IF NVL(:OLD.ESTADO,' ') <> NVL(:NEW.ESTADO,' ') THEN
            :NEW.FECHACAMBIOEST := TO_DATE(TO_CHAR(SYSDATE, 'DD/MM/YYYY'),'DD/MM/YYYY');

            IF :NEW.ESTADO = 'S' OR :NEW.ESTADO = 'R' THEN
                :NEW.FECHASALIOUSUARIO := TO_DATE(TO_CHAR(SYSDATE, 'DD/MM/YYYY'),'DD/MM/YYYY');
            END IF;

            IF :NEW.ESTADO ='A' THEN
                IF PCK_SERVICIOS_PUBLICOS_COM7.FC_ACTUALIZARSUSPENDIDOS
                        ( UN_COMPANIA         => :OLD.COMPANIA
                         ,UN_CICLO            => :NEW.CICLO
                         ,UN_CODIGORUTA       => :OLD.CODIGORUTA
                         ,UN_CODIGOINTERNO    => :NEW.CODIGOINTERNO
                         ,UN_ANO              => :NEW.ANO
                         ,UN_PERIODO          => :NEW.PERIODO
                         ,UN_BANCOPAGO        => :NEW.BANCOPERPROCESO
                         ,UN_DESDETRIGGER     => -1
                         ,UN_USUARIO          => :NEW.MODIFIED_BY
                        ) <> 0 THEN
                    :NEW.BANCOPERPROCESO := NULL;
                    :NEW.FECHAPAGOPERPROCESO := NULL;
                    :NEW.PAQUETEPAGOPERPROCESO := NULL;
                    :NEW.NOFECHAPAGOPERPROCESO := NULL;
                    :NEW.RECAUDADOPROCESO := 0;
                    :NEW.TOTFACTURAPERACTUAL := 0    ;
                END IF;
            END IF;
        END IF;

        IF NVL(:OLD.ESTRATO,' ') <> NVL(:NEW.ESTRATO, ' ') THEN
            :NEW.ESTRATOASEO := :NEW.ESTRATO;
            :NEW.ESTRATOALUMBRADO := :NEW.ESTRATO;
        END IF;
    END IF;

END;
