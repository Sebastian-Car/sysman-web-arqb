CREATE OR REPLACE TRIGGER "AI_PAGOPROGRAMADO_HIS" 
 /*
      NAME              : AI_PAGOPROGRAMADO_HIS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JUAN SEBASTIAN FORERO NOGUERA
      DATE              : 03/04/2017
      TIME              : 10:00 AM
      MODIFIER          : 
      DESCRIPTION       : INSERTA LOS PAGOS PROGRAMADOS EN LA TABLA DE HISTORICOS
                          
  */

AFTER INSERT ON PAGOPROGRAMADO
FOR EACH ROW
DECLARE 
MI_RTA NUMBER;
MI_CAMPOS   VARCHAR2(32000);
MI_VALORES  VARCHAR2(32000);
BEGIN 
  BEGIN 
    BEGIN 
        MI_CAMPOS:='COMPANIA,
                    CLASEORDEN,
                    ORDENDECOMPRA,
                    VALORCONTRATO,
                    CODIGO,
                    PORCENTAJE,
                    VALOR,
                    DESCRIPCION,
                    CREATED_BY,
                    FECHA,
                    FECHAPLAZO,
                    DATE_CREATED';
                    
        MI_VALORES := ''''|| :NEW.COMPANIA || ''',
                      ''' || :NEW.CLASEORDEN || ''',
                      ' || :NEW.ORDENDECOMPRA || ',
                      ' || :NEW.VALORCONTRATO || ',
                      ' || PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(  UN_TABLA => 'PAGOPROGRAMADOHISTORICOS',
                                                              UN_CRITERIO => 'COMPANIA            = '''||:NEW.COMPANIA||'''
                                                                                AND CLASEORDEN    = '''||:NEW.CLASEORDEN||'''
                                                                                AND ORDENDECOMPRA =   '||:NEW.ORDENDECOMPRA||'',
                                                              UN_CAMPO => 'CODIGO' ,
                                                              UN_INICIAL => '1')|| ',
                      ' || :NEW.PORCENTAJE || ',
                      ' || :NEW.VALOR || ',
                      ''' || :NEW.DESCRIPCION || ''',
                      ''' || :NEW.CREATED_BY || ''',
                      TO_DATE('''||TO_CHAR(:NEW.FECHA,'DD/MM/YYYY')||''',''DD/MM/YYYY''),
                      TO_DATE('''||TO_CHAR(:NEW.FECHAPLAZO ,'DD/MM/YYYY')||''',''DD/MM/YYYY''),
                      TO_DATE('''||TO_CHAR(:NEW.DATE_CREATED,'DD/MM/YYYY')||''',''DD/MM/YYYY'')
                      '; 
         MI_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => 'PAGOPROGRAMADOHISTORICOS', 
                                    UN_ACCION  => 'I', 
                                    UN_CAMPOS  => MI_CAMPOS, 
                                    UN_VALORES => MI_VALORES);
    END;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN  
      PCK_ERR_MSG.RAISE_WITH_MSG(
                       UN_EXC_COD    => SQLCODE,
                       UN_TABLAERROR => 'PAGOPROGRAMADO',
                       UN_ERROR_COD  => PCK_ERRORES.ERR_INSER_PAGOPRO_HISTO
                       );    
  END;
END;