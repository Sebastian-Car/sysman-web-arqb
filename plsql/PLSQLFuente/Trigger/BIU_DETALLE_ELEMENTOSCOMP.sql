CREATE OR REPLACE TRIGGER "BIU_DETALLE_ELEMENTOSCOMP" 
/* 
  NAME              : BIU_DETALLE_ELEMENTOSCOMP
  AUTHORS           : SYSMAN  SAS
  AUTHOR            : YESSICA SANA
  DATE CREATED      : 19/09/2018
  TIME              : 5:00 PM
  DESCRIPTION       : Actualiza los meses vida util de la tabla DETALLE_ELEMENTOSCOMPONENTES a DEVOLUTIVO.
*/
BEFORE INSERT OR UPDATE OF  MESESVIDAUTIL
              ON DETALLE_ELEMENTOSCOMPONENTES
FOR EACH ROW 

    BEGIN
        IF :NEW.MATERIAL NOT IN (0) THEN
        
            BEGIN
                PCK_ALMACEN_COM5.FC_VIDAUTILCOMPONENTE(
                                UN_COMPANIA  => :NEW.COMPANIA, 
                                UN_ELEMENTO  => :NEW.CODIGO_COMPONENTE, 
                                UN_SERIE     => :NEW.SERIE_COMPONENTE, 
                                UN_MES       => :NEW.MESESVIDAUTIL); 
            END;  
       
                          
        END IF; 
    

END BIU_DETALLE_ELEMENTOSCOMP;
