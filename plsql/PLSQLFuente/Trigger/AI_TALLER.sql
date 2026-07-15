CREATE OR REPLACE TRIGGER "AI_TALLER"  
/*
      NAME              : AI_TALLER
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : 
      DATE MIGRADOR     : 
      TIME              : 
      SOURCE MODULE     : 
      MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MODIFIED     : 27/01/2017
      TIME              : 12:30 PM
      DESCRIPTION       : INSERTA EL RESPONSABLE DE UN TALLER. SE AJUSTA AL ESTANDAR
                          
  */
AFTER INSERT ON TALLER
FOR EACH ROW
DECLARE
  MI_RTA NUMBER;
  MI_CAMPOS   VARCHAR2(32000);
  MI_VALORES  VARCHAR2(32000);
BEGIN
IF PCK_GENERALES.FC_CONS_CAMBIONIT() IN (0) THEN --VALIDACION PARA CUANDO SE ESTA CAMBIANDO EL NIT AL TERCERO
  BEGIN
    BEGIN 
      MI_CAMPOS:='COMPANIA,NIT,NOMBRE,SUCURSAL,SUCURSAL_TALLER,TALLER';
      MI_VALORES := '''' || :NEW.COMPANIA 
                         || ''',''' ||PCK_DATOS.FC_CONS_TERCERO
                         || ''',''' ||'VARIOS' 
                         || ''',''' || PCK_DATOS.FC_CONS_SUCURSAL 
                         ||''','''||:NEW.SUCURSAL
                         ||''','''||:NEW.NIT||''''; 
      MI_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => 'RESPONSABLES_TALLERES', 
                                 UN_ACCION  => 'I', 
                                 UN_CAMPOS  => MI_CAMPOS, 
                                 UN_VALORES => MI_VALORES);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN  
      RAISE PCK_EXCEPCIONES.EXC_GENERAL;
    END;
  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN  
    PCK_ERR_MSG.RAISE_WITH_MSG(
                   UN_EXC_COD    => SQLCODE,
                   UN_TABLAERROR => 'TALLER',
                   UN_ERROR_COD  => PCK_ERRORES.ERROR_GRAL_INSTALLER
                 );
  END;
 END IF; 
END;