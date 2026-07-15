CREATE OR REPLACE TRIGGER "AU_COMPROBANTE_PPTAL"  
/*
      NAME              : AU_COMPROBANTE_CNT
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER VILLATE
      DATE MIGRADOR     : 25/05/2017
      TIME              : 04:30 PM
      SOURCE MODULE     : 
      MODIFIER          : JOSE PASCUAL GOMEZ
      DATE MODIFIED     : 31/08/2017
      TIME              : 04:19 PM
      DESCRIPTION       : CONTROLA LOS COMPROBANTE PRESUPUESTALES. SE AJUSTA AL ESTANDAR
                         JOSE: Se ajusta para que solo se dispare cuando los campos involucrados cambien
                          
*/
AFTER UPDATE 
OF COMPANIA, ANO, TIPO, NUMERO, TERCERO, SUCURSAL, 
   DESCRIPCION, NRO_DOCUMENTO, REFERENCIA, AUXILIAR
ON COMPROBANTE_PPTAL 
REFERENCING OLD AS ANT NEW AS NUE 
FOR EACH ROW
DECLARE
  MI_RTA  NUMBER;
BEGIN 
IF PCK_GENERALES.FC_CONS_CAMBIONIT() IN (0) THEN --VALIDACION PARA CUANDO SE ESTA CAMBIANDO EL NIT AL TERCERO 
  MI_RTA := PCK_PRESUPUESTO2.FC_ACTUALIZARDETALLEPPTAL(
                   UN_COMPANIA     => :ANT.COMPANIA, 
                   UN_ANO          => :ANT.ANO, 
                   UN_TIPO         => :ANT.TIPO, 
                   UN_COMPROBANTE  => :ANT.NUMERO,
                   --UN_FECHAA       => 'TO_DATE('''||TO_CHAR(:ANT.FECHA, 'DD/MM/YYYY HH:mi:ss')|| q'[', 'DD/MM/YYYY HH:mi:ss')]', 
                   --UN_FECHAN       => 'TO_DATE('''||TO_CHAR(:NUE.FECHA, 'DD/MM/YYYY HH:mi:ss')|| q'[', 'DD/MM/YYYY HH:mi:ss')]', 
                   UN_TERCEROA     => :ANT.TERCERO, 
                   UN_TERCERON     => :NUE.TERCERO, 
                   UN_SUCURSALA    => :ANT.SUCURSAL, 
                   UN_SUCURSALN    => :NUE.SUCURSAL, 
                   UN_DESCRIPCIONA => :ANT.DESCRIPCION, 
                   UN_DESCRIPCIONN => :NUE.DESCRIPCION, 
                   UN_NUMERODOCA   => :ANT.NRO_DOCUMENTO, 
                   UN_NUMERODOCN   => :NUE.NRO_DOCUMENTO, 
                   UN_REFERENCIAA  => :ANT.REFERENCIA, 
                   UN_REFERENCIAN  => :NUE.REFERENCIA, 
                   UN_AUXILIARA    => :ANT.AUXILIAR,
                   UN_AUXILIARN    => :NUE.AUXILIAR);               
                   
  END IF;                   
END;