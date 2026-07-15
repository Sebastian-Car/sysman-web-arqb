CREATE OR REPLACE TRIGGER "BD_SP_ABONOS" 
  /*
      NAME              : BD_SP_ABONOS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 15/05/2017
      TIME              : 11:30 AM
      SOURCE MODULE     :
      MODIFIER          :
      DATE MODIFIED     :
      TIME              : 09:47 AM
      DESCRIPTION       : Verifica y elimina los detalles de los abonos.

  */
FOR DELETE ON SP_ABONOS
REFERENCING OLD AS OLD NEW AS NEW
COMPOUND TRIGGER

MI_POS          NUMBER DEFAULT 0;
MI_RTA          NUMBER DEFAULT 0;
MI_TABLA        PCK_SUBTIPOS.TI_STRSQL;
MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_CAMBIOCICLO  SP_USUARIO.CAMBIOCICLORUTA%TYPE;

BEFORE EACH ROW IS  --Ejecución antes de cada fila
BEGIN
    MI_POS := MI_POS +1;
    SELECT CAMBIOCICLORUTA
    INTO   MI_CAMBIOCICLO
    FROM   SP_USUARIO
    WHERE  COMPANIA   = :OLD.COMPANIA
      AND  CICLO      = :OLD.CICLO
      AND  CODIGORUTA = :OLD.CODIGORUTA;

    --VALIDA POR CADA FILA QUE SE PUEDA ELIMINAR
    MI_RTA :=0;
    IF MI_CAMBIOCICLO = 0 THEN
      MI_RTA := PCK_SERVICIOS_PUBLICOS_ABONOS.FC_VALIDARABONO
                  ( UN_COMPANIA        => :OLD.COMPANIA
                   ,UN_CODIGORUTA      => :OLD.CODIGORUTA
                   ,UN_CICLO           => :OLD.CICLO
                   ,UN_ANO             => :OLD.ANO
                   ,UN_PERIODO         => :OLD.PERIODO
                   ,UN_ABONOAUTORIZADO => :OLD.INDAUTORIZADO
                   ,UN_BANCOABONO      => :OLD.BANCO
                   ,UN_FECHAABONO      => :OLD.FECHA
                   ,UN_CONSECUTIVO     => :OLD.CONSECUTIVO
                   ,UN_PAGOCONVENIO    => :OLD.PAGOCONVENIOS
                   ,UN_PAGOTERCERIZADO => :OLD.PAGOTERCERIZADO
                   ,UN_ACCION          => 'ELIMINAR'
                  );
    END IF;

    IF MI_RTA <>0 THEN  --Si se permite borrar.
        PCK_SERVICIOS_PUBLICOS_ABONOS.PR_ELIMINARABONO
            ( UN_COMPANIA           => :OLD.COMPANIA
             ,UN_CODIGORUTA         => :OLD.CODIGORUTA
             ,UN_CICLO              => :OLD.CICLO
             ,UN_ANO                => :OLD.ANO
             ,UN_PERIODO            => :OLD.PERIODO
             ,UN_CONSECUTIVOABONO   => :OLD.CONSECUTIVO
             ,UN_FECHAABONO         => :OLD.FECHA
             ,UN_BANCOABONO         => :OLD.BANCO
             ,UN_VALORABONO         => :OLD.VALOR
             ,UN_PAGOCONVENIOS      => :OLD.PAGOCONVENIOS
             ,UN_PAGOTERCERIZADO    => :OLD.PAGOTERCERIZADO
             ,UN_USUARIO            => :OLD.CREATED_BY );


    END IF;
END BEFORE EACH ROW;




END;
