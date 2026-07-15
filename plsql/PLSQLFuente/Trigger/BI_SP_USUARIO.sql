CREATE OR REPLACE TRIGGER "BI_SP_USUARIO" 
  /*
      NAME              : BI_SP_USUARIO
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : MIGUEL ANGEL ZANGUÑA HURTADO
      DATE MIGRADOR     : 05/10/2017
      TIME              : 2:15 PM
      SOURCE MODULE     :
      MODIFIER          :
      DATE MODIFIED     :
      TIME              :
      DESCRIPTION       :

  */
FOR INSERT ON SP_USUARIO
REFERENCING OLD AS OLD NEW AS NEW
COMPOUND TRIGGER
   MI_RTA                PCK_SUBTIPOS.TI_LOGICO DEFAULT 0;
   MI_TOTFACTURA         PCK_SUBTIPOS.TI_DOBLE DEFAULT 0;
   MI_CODINTERNO         SP_USUARIO.CODIGOINTERNO%TYPE;
   MI_NUMREG             SP_USUARIO.NUMEROREGISTRO%TYPE DEFAULT 0;

BEFORE EACH ROW IS
BEGIN
    MI_RTA := 0;
    IF :NEW.CAMBIOCICLORUTA = 0 THEN

        MI_CODINTERNO := PCK_SERVICIOS_PUBLICOS_COM7.FC_CREARCODIGOINTERNO
                            (UN_COMPANIA     => :NEW.COMPANIA
                            ,UN_CICLO        => :NEW.CICLO
                            ,UN_NUMREGISTRO  => MI_NUMREG);

        :NEW.NUMEROREGISTRO := MI_NUMREG;
        :NEW.CODIGOINTERNO := MI_CODINTERNO;

        MI_RTA := PCK_SERVICIOS_PUBLICOS_COM7.FC_SUSCRIPTORTGR
                    (UN_COMPANIA            => :NEW.COMPANIA
                    ,UN_CODIGOINTERNO       => :NEW.CODIGOINTERNO
                    ,UN_CODIGORUTA          => :NEW.CODIGORUTA
                    ,UN_CICLO               => :NEW.CICLO
                    ,UN_ANO                 => :NEW.ANO
                    ,UN_PERIODO             => :NEW.PERIODO
                    ,UN_CLASESOLICITUD      => :NEW.CLASESOLICITUD
                    ,UN_SOLICITUD           => :NEW.SOLICITUD
                    ,UN_USO                 => :NEW.USO
                    ,UN_ESTRATO             => :NEW.ESTRATO
                    ,UN_TIPOPREDIO          => :NEW.TIPOPREDIO
                    ,UN_ACCION              => 'INSERTAR'
                    ,UN_USUARIO             => :NEW.CREATED_BY
                    ,UN_INSERTAFACTUSUARIO  => 0
                    ,UN_DBLTOTAL            => MI_TOTFACTURA);
    END IF;
    :NEW.TOTFACTURAPERACTUAL := MI_TOTFACTURA;

END BEFORE EACH ROW;

AFTER EACH ROW IS
BEGIN
    IF MI_RTA <> 0 THEN
        MI_RTA := PCK_SERVICIOS_PUBLICOS_COM7.FC_SUSCRIPTORTGR
                    (UN_COMPANIA            => :NEW.COMPANIA
                    ,UN_CICLO               => :NEW.CICLO
                    ,UN_CODIGORUTA          => :NEW.CODIGORUTA
                    ,UN_CODIGOINTERNO       => :NEW.CODIGOINTERNO
                    ,UN_ANO                 => :NEW.ANO
                    ,UN_PERIODO             => :NEW.PERIODO
                    ,UN_SOLICITUD           => :NEW.SOLICITUD
                    ,UN_CLASESOLICITUD      => :NEW.CLASESOLICITUD
                    ,UN_INSERTAFACTUSUARIO  => -1
                    ,UN_USUARIO             => :NEW.CREATED_BY
                    ,UN_ACCION              => 'INSERTAR'
                    ,UN_DBLTOTAL            => MI_TOTFACTURA);
    END IF;

END AFTER EACH ROW;

END;
