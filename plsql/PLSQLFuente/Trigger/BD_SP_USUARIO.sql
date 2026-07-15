CREATE OR REPLACE TRIGGER "BD_SP_USUARIO" 
  /*
      NAME              : BD_SP_USUARIO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 23/10/2017
      TIME              : 11:40 AM
      SOURCE MODULE     :
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       : CONTROLA NO BORRAR LOS USUARIOS QUE NO ESTEN AUTORIZADOS

  */

BEFORE DELETE ON SP_USUARIO
FOR EACH ROW
BEGIN
    BEGIN

        IF :OLD.AUTORIZARBORRADO = 0 THEN
            RAISE PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS;
        END IF;

        PCK_SERVICIOS_PUBLICOS_COM3.PR_AUDITARMODIF
            ( UN_COMPANIA  	 => :OLD.COMPANIA
             ,UN_FORMORIGEN  => 'USUARIO'
             ,UN_INTTIPO	 => 2
             ,UN_CAMPO  	 => 'Compañía '|| :OLD.COMPANIA ||', Uso '|| :OLD.USO ||', Estrato '|| :OLD.ESTRATO ||', Año '|| :OLD.ANO ||', Periodo '|| :OLD.PERIODO ||'  '
             ,UN_USUARIO 	 => :OLD.CREATED_BY );
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_FACTURACION_SERV_PUBLICOS THEN
        --No se encuentra autorizado para borrar el usuario.
        PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                  ,UN_TABLAERROR => 'SP_USUARIO'
                                  ,UN_ERROR_COD  => PCK_ERRORES.ERR_BORRARUSUARIONOAUTO);

    END;
END;
