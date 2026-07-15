create or replace PACKAGE BODY "PCK_CONTABILIDAD4" AS
/**@package:  Contabilidad **/
--1
FUNCTION FC_REVISAAFECTACIONESDETERIORO
/*
  NAME              : FC_REVISAAFECTACIONESDETERIORO nombre en access RevisarAfectacionesdeterioro
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
  DATE MIGRADOR     : 15/04/2016
  TIME              : 02:15 PM
  SOURCE MODULE     : Contabilidad 
  DESCRIPTION       : Aplica cuando la entidad tiene activo el parametro ENTIDAD APLICA NIIF 
  MODIFIER          : JUAN CAMILO RODRIGUEZ DIAZ
  DATE MODIFIED     : 13/06/2017
  TIME              : 13:00 PM
  MODIFICATIONS     : DEPURACION DE LA FUNCION DE ACUERDO AL ESTANDAR 
  PARAMETERS        : 
                      UN_COMPANIA              => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                      UN_FECHACORTE            => FECHA DE CORTE DEL DETERIORO
                      UN_ANO                   => ANIO POR EL QUE SE FILTRA LOS DETERIOROS                     
                      UN_TERCEROINICIAL        => EL TERCERO INICIAL
                      UN_TERCEROFINAL          => EL TERCERO FINAL
                      UN_MESESVENCIDOS         => MESES VENCIDOS
                      UN_USUARIO               => USUARIO QUE INGRESO EN LA SESIÓN
  @NAME:  revisarDeterioroDeCartera
  @METHOD:  GET  
*/
(
  UN_COMPANIA       IN  PCK_SUBTIPOS.TI_COMPANIA,
  UN_FECHACORTE     IN  PCK_SUBTIPOS.TI_PARAMETRO,
  UN_ANO            IN  PCK_SUBTIPOS.TI_ANIO,
  UN_TERCEROINICIAL IN  PCK_SUBTIPOS.TI_TERCERO,
  UN_TERCEROFINAL   IN  PCK_SUBTIPOS.TI_TERCERO,
  UN_MESESVENCIDOS  IN  PCK_SUBTIPOS.TI_ENTERO,
  UN_USUARIO        IN  PCK_SUBTIPOS.TI_USUARIO
)
RETURN VARCHAR2 AS 
  MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION      PCK_SUBTIPOS.TI_CONDICION;
  MI_ERROR_FUN      NUMBER:=GL_ERROR_NUM + 1;
  MI_REGISTRO       VARCHAR2(1);
  MI_RESPUESTA      PCK_SUBTIPOS.TI_RTA_ACME;
  MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
  MI_CONSULTA       PCK_SUBTIPOS.TI_CONSULTA;
  MI_ENLACE         PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_EXISTE         PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_RTA            PCK_SUBTIPOS.TI_RTA_ACME;
  MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

  --UPDATE 1 
  BEGIN          
        MI_TABLA:='COMPROBANTE_CNT';   

        MI_CAMPOS:='DEBITOSAFECTADOS_CXP  = 0,'||
                   'CREDITOSAFECTADOS_CXP = 0,'||
                   'MODIFIED_BY           ='''||UN_USUARIO||''','||
                   'DATE_MODIFIED         =SYSDATE';

        MI_CONDICION:=' COMPANIA = ''' || UN_COMPANIA ||''''||
                      ' AND (DEBITOSAFECTADOS_CXP NOT IN (0) OR CREDITOSAFECTADOS_CXP NOT IN (0))'||
                      ' AND TERCERO BETWEEN ''' || UN_TERCEROINICIAL ||''''||
                      ' AND ''' || UN_TERCEROFINAL ||'''';


        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA    =>MI_TABLA,
                                    UN_ACCION   =>'M',
                                    UN_CAMPOS   =>MI_CAMPOS,            
                                    UN_CONDICION=>MI_CONDICION);      
        EXCEPTION
             WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE  PCK_EXCEPCIONES.EXC_ACTUALIZAR;
  END;
  --UPDATE 2 
BEGIN
    MI_TABLA := 'DETALLE_COMPROBANTE_CNT';
    MI_CONSULTA := '( SELECT DISTINCT'||
                   ' PC.COMPANIA,'||
                   ' PC.CODIGO,'||
                   ' PC.ANO'||
                   ' FROM V_PLAN_CONTABLE PC'||  
                   ' WHERE PC.COMPANIA = '''||UN_COMPANIA||''''||                  
                   '  AND PC.APLICA_DETERIORO NOT IN (0))';

    MI_ENLACE := ' TABLA.COMPANIA = VISTA.COMPANIA'||  
                  ' AND TABLA.CUENTA = VISTA.CODIGO'||
                  ' AND TABLA.ANO = VISTA.ANO';

    MI_EXISTE := 	' UPDATE SET '|| 
                  ' TABLA.DEBITOSAFECTADOS_CXP = 0,'||                
                  ' TABLA.CREDITOSAFECTADOS_CXP = 0,'||
                  ' TABLA.MODIFIED_BY           ='''||UN_USUARIO||''','||
                  ' TABLA.DATE_MODIFIED         =SYSDATE'||
                  ' WHERE (TABLA.DEBITOSAFECTADOS_CXP NOT IN 0'|| 
                  '  OR TABLA.DEBITOSAFECTADOS_CXP IS NULL'|| 
                  '  OR TABLA.CREDITOSAFECTADOS_CXP NOT IN 0'|| 
                  '  OR TABLA.CREDITOSAFECTADOS_CXP IS NULL) '||
                  '  AND TABLA.TERCERO BETWEEN ''' || UN_TERCEROINICIAL ||''' AND ''' || UN_TERCEROFINAL ||'''';  			

    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA      =>MI_TABLA,
                                 UN_ACCION     =>'MM',
                                 UN_MERGEUSING =>MI_CONSULTA,
                                 UN_MERGEENLACE=>MI_ENLACE,
                                 UN_MERGEEXISTE=>MI_EXISTE);	            
    EXCEPTION
         WHEN  PCK_EXCEPCIONES.EXC_MERGE THEN
        RAISE  PCK_EXCEPCIONES.EXC_MERGE;
  END;

  --UPDATE 3 
  BEGIN                              
      MI_TABLA    := 'COMPROBANTE_CNT';

      MI_CONSULTA := '(SELECT DISTINCT '||
                    ' D.CMPTE_AFECTADO,'||
                    ' D.COMPANIA,'||
                    ' D.TIPO_CPTE_AFECT,'||
                    ' T.CLASE_CONTABLE,'||
                    ' D.VALOR_DEBITO,'||
                    ' D.VALOR_CREDITO'||
                    ' FROM DETALLE_COMPROBANTE_CNT D '||            
                    ' INNER JOIN V_PLAN_CONTABLE PC '||  
                    ' ON (D.COMPANIA = PC.COMPANIA) '||
                    ' AND (D.CUENTA = PC.ID) '||
                    ' AND (D.ANO = PC.ANO) '||
                    ' INNER JOIN TIPO_COMPROBANTE T '||
                    ' ON (D.COMPANIA = T.COMPANIA) '||
                    ' AND (D.TIPO_CPTE = T.CODIGO) '||
                    ' WHERE D.COMPANIA = '''||UN_COMPANIA||''''|| 
                    ' AND D.TIPO_CPTE_AFECT IS NOT NULL'||
                    ' AND D.CMPTE_AFECTADO <> 0'||
                    ' AND D.FECHA BETWEEN TO_DATE(''01/01/1900'',''DD/MM/YYYY'') AND TO_DATE(''' || UN_FECHACORTE || ''',''DD/MM/YYYY'')'||
                    ' AND T.CLASE_CONTABLE IS NOT NULL '||
                    ' AND PC.CLASECUENTA IN (''P'',''C'',''E'',''Y'',''N'',''G'')'||								  
                    ' AND PC.APLICA_DETERIORO NOT IN (0))';

      MI_ENLACE :=  ' TABLA.COMPANIA = VISTA.COMPANIA '||
                    ' AND TABLA.TIPO = VISTA.TIPO_CPTE_AFECT '||
                    ' AND TABLA.NUMERO = VISTA.CMPTE_AFECTADO';

      --Campos usados para UPDATE 3 y 4
      MI_EXISTE := 	' UPDATE SET '||
                    ' TABLA.DEBITOSAFECTADOS_CXP          = (CASE WHEN (VISTA.CLASE_CONTABLE = ''A'' OR VISTA.CLASE_CONTABLE = ''E'' OR VISTA.CLASE_CONTABLE = ''R'' OR VISTA.CLASE_CONTABLE = ''G'') THEN NVL(TABLA.DEBITOSAFECTADOS_CXP,0) + VISTA.VALOR_DEBITO ELSE 0 END)'||
                    ' + (CASE WHEN (VISTA.CLASE_CONTABLE  = ''S'' OR VISTA.CLASE_CONTABLE = ''I'' OR VISTA.CLASE_CONTABLE = ''N'' OR VISTA.CLASE_CONTABLE = ''J'') AND VISTA.VALOR_CREDITO<> 0 THEN NVL(TABLA.DEBITOSAFECTADOS_CXP,0) + VISTA.VALOR_CREDITO ELSE 0 END),'||
                    ' TABLA.CREDITOSAFECTADOS_CXP         = (CASE WHEN (VISTA.CLASE_CONTABLE = ''E'' OR VISTA.CLASE_CONTABLE = ''R''  OR VISTA.CLASE_CONTABLE = ''G'') THEN NVL(TABLA.CREDITOSAFECTADOS_CXP,0) + VISTA.VALOR_CREDITO ELSE 0 END),'||
                    ' TABLA.MODIFIED_BY                   ='''||UN_USUARIO||''','||
                    ' TABLA.DATE_MODIFIED                 =SYSDATE'||
                    ' WHERE TABLA.TERCERO BETWEEN ''' || UN_TERCEROINICIAL ||''' AND ''' || UN_TERCEROFINAL ||'''';  			

      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       =>MI_TABLA,
                                   UN_ACCION      =>'MM',
                                   UN_MERGEUSING  =>MI_CONSULTA,
                                   UN_MERGEENLACE =>MI_ENLACE,
                                   UN_MERGEEXISTE =>MI_EXISTE);	   	            
       EXCEPTION
            WHEN  PCK_EXCEPCIONES.EXC_MERGE THEN
           RAISE  PCK_EXCEPCIONES.EXC_MERGE;
  END;          
  --UPDATE 4          
 BEGIN
      MI_TABLA := 'DETALLE_COMPROBANTE_CNT';
      MI_CONSULTA := '(SELECT DISTINCT'||
                      ' D.COMPANIA,'||
                      ' D.TIPO_CPTE_AFECT,'||
                      ' D.CMPTE_AFECTADO,'||
                      ' T.CLASE_CONTABLE,'||
                      ' D.VALOR_DEBITO,'||
                      ' D.VALOR_CREDITO,'||
                      ' D.CUENTA'||
                      ' FROM DETALLE_COMPROBANTE_CNT D'||           
                      ' INNER JOIN V_PLAN_CONTABLE PC '||
                      ' ON (D.ID = PC.ID)'|| 
                      ' AND (D.ANO = PC.ANO) '||
                      ' AND (D.COMPANIA = PC.COMPANIA)'||
                      ' LEFT JOIN TIPO_COMPROBANTE T '||
                      ' ON (D.TIPO_CPTE = T.CODIGO) '||
                      ' AND (D.COMPANIA = T.COMPANIA)'||
                      ' WHERE D.COMPANIA = '''||UN_COMPANIA||''''||
                      ' AND D.TIPO_CPTE_AFECT IS NOT NULL'||
                      ' AND D.CMPTE_AFECTADO <> 0	'||						                  
                      ' AND D.FECHA BETWEEN TO_DATE(''01/01/1900'',''DD/MM/YYYY'') AND TO_DATE(''' || UN_FECHACORTE || ''',''DD/MM/YYYY'') '||
                      ' AND PC.CLASECUENTA IN (''P'',''C'',''E'',''Y'',''N'',''G'') '||
                      ' AND D.CONSECUTIVOAFECTADO IS NULL '||            
                      ' AND PC.APLICA_DETERIORO NOT IN (0))';

      MI_ENLACE :=  ' TABLA.COMPANIA = VISTA.COMPANIA '||  
    				        ' AND TABLA.TIPO_CPTE = VISTA.TIPO_CPTE_AFECT '|| 
    				        ' AND TABLA.COMPROBANTE = VISTA.CMPTE_AFECTADO '||
    				        ' AND TABLA.CUENTA = VISTA.CUENTA';

      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>MI_TABLA,
                                  UN_CONDICION  =>'MM',
                                  UN_MERGEUSING => MI_CONSULTA,
                                  UN_MERGEENLACE=>  MI_ENLACE,
                                  UN_MERGEEXISTE=>  MI_EXISTE );	
      EXCEPTION
           WHEN  PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE  PCK_EXCEPCIONES.EXC_MERGE;	            
	END;		
  --UPDATE 5
  BEGIN

      MI_TABLA := 'DETALLE_COMPROBANTE_CNT';

      MI_CONSULTA := '(SELECT DISTINCT '||
                    ' D.COMPANIA,'||
                    ' D.TIPO_CPTE_AFECT,'||
                    ' D.CMPTE_AFECTADO,'||
                    ' T.CLASE_CONTABLE,'||
                    ' D.CONSECUTIVOAFECTADO,'||
                    ' D.VALOR_DEBITO,'||
                    ' D.VALOR_CREDITO,'||
                    ' D.CUENTA'||
                    ' FROM DETALLE_COMPROBANTE_CNT D'||
                    ' INNER JOIN V_PLAN_CONTABLE PC'|| 
                    ' ON (D.CUENTA = PC.ID) '||
                    ' AND (D.ANO = PC.ANO) '||
                    ' AND (D.COMPANIA = PC.COMPANIA)'||
                    ' LEFT JOIN TIPO_COMPROBANTE T '||
                    ' ON (D.TIPO_CPTE = T.CODIGO) '||
                    ' AND (D.COMPANIA = T.COMPANIA)'||
                    '  WHERE D.COMPANIA = '''||UN_COMPANIA||''''||
                    '   AND D.TIPO_CPTE_AFECT IS NOT NULL'||
                    '   AND D.CMPTE_AFECTADO <> 0	'||						
                    '   AND D.FECHA BETWEEN TO_DATE(''01/01/1900'',''DD/MM/YYYY'') AND TO_DATE(''' || UN_FECHACORTE || ''',''DD/MM/YYYY'')'||
                    '   AND T.CLASE_CONTABLE IS NOT NULL'||
                    '   AND PC.CLASECUENTA IN (''P'',''C'',''E'',''Y'',''N'',''G'') '||
                    '   AND D.CONSECUTIVOAFECTADO IS NOT NULL'||
                    '   AND D.TERCERO BETWEEN ''' || UN_TERCEROINICIAL ||''' AND ''' || UN_TERCEROFINAL ||'''	'||		
                    '   AND PC.APLICA_DETERIORO NOT IN (0))';

      MI_ENLACE := ' TABLA.COMPANIA = VISTA.COMPANIA  '||
                   ' AND (TABLA.TIPO_CPTE = VISTA.TIPO_CPTE_AFECT) '||
                   ' AND (TABLA.COMPROBANTE = VISTA.CMPTE_AFECTADO) '||
                   ' AND (TABLA.CONSECUTIVO = VISTA.CONSECUTIVOAFECTADO)'||  
                   ' AND (TABLA.CUENTA = VISTA.CUENTA)';

      MI_EXISTE := 	' UPDATE SET '||
                    ' TABLA.DEBITOSAFECTADOS_CXP       = (CASE WHEN (VISTA.CLASE_CONTABLE = ''A'' OR VISTA.CLASE_CONTABLE = ''E'' OR VISTA.CLASE_CONTABLE = ''R'' OR VISTA.CLASE_CONTABLE = ''G'') THEN NVL(TABLA.DEBITOSAFECTADOS_CXP,0) + VISTA.VALOR_DEBITO ELSE 0 END) +'||
                    ' (CASE WHEN (VISTA.CLASE_CONTABLE = ''S'' OR VISTA.CLASE_CONTABLE = ''I'' OR VISTA.CLASE_CONTABLE = ''N'' OR VISTA.CLASE_CONTABLE = ''J'') AND VISTA.VALOR_CREDITO<> 0 THEN NVL(TABLA.DEBITOSAFECTADOS_CXP,0) + VISTA.VALOR_CREDITO ELSE 0 END),'||
                    ' TABLA.CREDITOSAFECTADOS_CXP      = (CASE WHEN (VISTA.CLASE_CONTABLE = ''E'' OR VISTA.CLASE_CONTABLE = ''R''  OR VISTA.CLASE_CONTABLE = ''G'') THEN NVL(TABLA.CREDITOSAFECTADOS_CXP,0) + VISTA.VALOR_CREDITO ELSE 0 END),'||
                    ' TABLA.MODIFIED_BY                ='''||UN_USUARIO||''','||
                    ' TABLA.DATE_MODIFIED              =SYSDATE';  			

      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       =>MI_TABLA,
                                   UN_ACCION      =>'MM',
                                   UN_MERGEUSING  =>MI_CONSULTA,
                                   UN_MERGEENLACE =>MI_ENLACE,
                                   UN_MERGEEXISTE =>MI_EXISTE);	
       EXCEPTION
            WHEN  PCK_EXCEPCIONES.EXC_MERGE THEN
           RAISE  PCK_EXCEPCIONES.EXC_MERGE;
  END;
  --UPDATE 6                       
 BEGIN
     MI_TABLA    := 'COMPROBANTE_CNT';
     MI_CONSULTA := '(SELECT DISTINCT '||
    			           ' D.COMPANIA, '||
    			           ' D.TIPO_CPTE_AFECT,'||
    			           ' D.CMPTE_AFECTADO,'||
    			           ' D.VALOR_DEBITO,'||
    			           ' D.VALOR_CREDITO'||
                     ' FROM DETALLE_COMPROBANTE_CNT DA'|| 
                     ' INNER JOIN DETALLE_COMPROBANTE_CNT D '||
                     ' ON  DA.COMPANIA = D.COMPANIA'||
                     ' AND DA.TIPO_CPTE = D.TIPO_CPTE_AFECT'||
                     ' AND DA.COMPROBANTE = D.CMPTE_AFECTADO'||
                     ' AND DA.CUENTA = D.CUENTA'||					
                     ' INNER JOIN TIPO_COMPROBANTE T'||
                     ' ON D.COMPANIA = T.COMPANIA'||
                     '  AND D.TIPO_CPTE = T.CODIGO'||
                     '  LEFT JOIN V_PLAN_CONTABLE PC'||
                     '  ON D.ID = PC.ID '||
                     '  AND D.ANO = PC.ANO '||
                     '  AND D.COMPANIA = PC.COMPANIA'||
                     '  WHERE D.COMPANIA = '''||UN_COMPANIA||''' '||
                     '   AND D.TIPO_CPTE_AFECT IS NOT NULL'||
                     '   AND D.CMPTE_AFECTADO <> 0 '||                 
                     '   AND D.FECHA BETWEEN TO_DATE(''01/01/1900'',''DD/MM/YYYY'') AND TO_DATE(''' || UN_FECHACORTE || ''',''DD/MM/YYYY'') '||
                     '   AND T.CLASE_CONTABLE IN (''A'',''J'')'||
                     '   AND PC.CLASECUENTA IN (''P'',''C'',''E'',''Y'',''N'',''G'')  '||                
                     '   AND PC.APLICA_DETERIORO NOT IN (0))';

      MI_ENLACE :=  ' TABLA.COMPANIA = VISTA.COMPANIA '||
                    ' AND TABLA.TIPO = VISTA.TIPO_CPTE_AFECT '||
                    ' AND TABLA.NUMERO = VISTA.CMPTE_AFECTADO';

      MI_EXISTE := 	' UPDATE SET '|| 
                    ' TABLA.DEBITOSAFECTADOS_CXP  = TABLA.DEBITOSAFECTADOS_CXP - VISTA.VALOR_CREDITO,'||
                    ' TABLA.CREDITOSAFECTADOS_CXP = TABLA.CREDITOSAFECTADOS_CXP - VISTA.VALOR_DEBITO ,'||
                    ' TABLA.MODIFIED_BY           ='''||UN_USUARIO||''','||
                    ' TABLA.DATE_MODIFIED         =SYSDATE'||
                    ' WHERE TABLA.TERCERO BETWEEN ''' || UN_TERCEROINICIAL ||''' AND ''' || UN_TERCEROFINAL ||'''';  			

      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       =>MI_TABLA,
                                   UN_ACCION      =>'MM',
                                   UN_MERGEUSING  =>MI_CONSULTA,
                                   UN_MERGEENLACE =>MI_ENLACE,
                                   UN_MERGEEXISTE =>MI_EXISTE);
      EXCEPTION
           WHEN  PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE  PCK_EXCEPCIONES.EXC_MERGE;
  END;
  --UPDATE 7 actualizar afectaciones para comprobantes con consecutivo afectado
  BEGIN
      MI_TABLA := 'DETALLE_COMPROBANTE_CNT';

      MI_CONSULTA := '(SELECT DISTINCT '||
                      ' D1.COMPANIA,'||
                      ' D1.TIPO_CPTE_AFECT,'||
                      ' D1.CMPTE_AFECTADO,'||
                      ' D1.CONSECUTIVOAFECTADO,'||
                      ' D2.VALOR_CREDITO,'||
                      '  D2.VALOR_DEBITO'||
                      '  FROM DETALLE_COMPROBANTE_CNT  D1 	'||
                      '  LEFT JOIN DETALLE_COMPROBANTE_CNT D2'||
                      ' ON D1.COMPANIA = D2.COMPANIA '||
                      ' AND D1.TIPO_CPTE = D2.TIPO_CPTE_AFECT '||
                      ' AND D1.COMPROBANTE = D2.CMPTE_AFECTADO '||
                      ' AND D1.CUENTA = D2.CUENTA'||
                      ' LEFT JOIN TIPO_COMPROBANTE T '||
                      ' ON D2.TIPO_CPTE = T.CODIGO'||
                      ' AND D2.COMPANIA = T.COMPANIA'||
                      ' LEFT JOIN V_PLAN_CONTABLE PC '||
                      ' ON D2.COMPANIA = PC.COMPANIA'||
                      '  AND D2.ANO = PC.ANO '||
                      ' AND D2.ID = PC.ID'||
                      ' WHERE D2.COMPANIA = '''||UN_COMPANIA||''' '||
                      '  AND D2.TIPO_CPTE_AFECT IS NOT NULL'||
                      '  AND D2.CMPTE_AFECTADO <> 0'||
                      '  AND D2.FECHA BETWEEN TO_DATE(''01/01/1900'',''DD/MM/YYYY'') AND TO_DATE(''' || UN_FECHACORTE || ''',''DD/MM/YYYY'')'||
                      '  AND T.CLASE_CONTABLE IN (''A'',''J'')'||
                      '  AND PC.CLASECUENTA IN (''P'',''C'',''E'',''Y'',''N'',''G'') '||			  
                      '  AND D2.CONSECUTIVOAFECTADO IS NULL'||			  
                      '  AND PC.APLICA_DETERIORO NOT IN (0))';


        MI_ENLACE := ' TABLA.COMPANIA = VISTA.COMPANIA '||
                     ' AND TABLA.TIPO_CPTE = VISTA.TIPO_CPTE_AFECT'|| 
                     ' AND TABLA.COMPROBANTE = VISTA.CMPTE_AFECTADO'||
                     ' AND TABLA.CONSECUTIVO = VISTA.CONSECUTIVOAFECTADO';

        MI_EXISTE := ' UPDATE SET '||
                     ' TABLA.DEBITOSAFECTADOS_CXP  = TABLA.DEBITOSAFECTADOS_CXP - VISTA.VALOR_CREDITO,'||
                     ' TABLA.CREDITOSAFECTADOS_CXP = TABLA.CREDITOSAFECTADOS_CXP - VISTA.VALOR_DEBITO,'||
                     ' TABLA.MODIFIED_BY           ='''||UN_USUARIO||''','||
                     ' TABLA.DATE_MODIFIED         =SYSDATE'||
                     ' WHERE TABLA.COMPANIA IS NOT NULL'||
                     ' AND TABLA.TERCERO BETWEEN ''' || UN_TERCEROINICIAL ||''' AND ''' || UN_TERCEROFINAL ||'''';  			

        MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA,
                                    UN_ACCION     => 'MM',
                                    UN_MERGEUSING => MI_CONSULTA,
                                    UN_MERGEENLACE=> MI_ENLACE,
                                    UN_MERGEEXISTE=> MI_EXISTE);	
       EXCEPTION
            WHEN  PCK_EXCEPCIONES.EXC_MERGE THEN
           RAISE  PCK_EXCEPCIONES.EXC_MERGE;
  END;  
--UPDATE 8
 BEGIN

     MI_TABLA := 'DETALLE_COMPROBANTE_CNT';

     MI_CONSULTA := '( SELECT DISTINCT'||
                    ' C.COMPANIA,'||
                    ' D2.VALOR_CREDITO,'||
                    ' D2.VALOR_DEBITO,'||
                    ' C.ANO,'||
                    ' C.TIPO,'||
                    ' C.NUMERO'||
                    ' FROM COMPROBANTE_CNT C '||			
                    ' LEFT JOIN DETALLE_COMPROBANTE_CNT D1 '||
                    ' ON C.COMPANIA = D1.COMPANIA'||
                    ' AND C.TIPO = D1.TIPO_CPTE_AFECT'||
                    ' AND C.NUMERO = D1.CMPTE_AFECTADO '||
                    ' LEFT JOIN DETALLE_COMPROBANTE_CNT D2'||
                    ' ON D1.CONSECUTIVO = D2.CONSECUTIVOAFECTADO'||
                    ' AND D1.COMPANIA = D2.COMPANIA'||
                    ' AND D1.CUENTA = D2.CUENTA'||
                    ' AND D1.TIPO_CPTE = D2.TIPO_CPTE_AFECT'||
                    ' AND D1.COMPROBANTE = D2.CMPTE_AFECTADO'||
                    ' LEFT JOIN TIPO_COMPROBANTE T'||
                    ' ON D2.COMPANIA = T.COMPANIA '||
                    ' AND D2.TIPO_CPTE = T.CODIGO'||
                    ' LEFT JOIN V_PLAN_CONTABLE PC'||
                    ' ON D2.ID = PC.ID '||
                    ' AND D2.ANO = PC.ANO '||
                    ' AND D2.COMPANIA = PC.COMPANIA'||
                    ' WHERE D2.COMPANIA = '''||UN_COMPANIA||''' '||
                    '  AND D2.TIPO_CPTE_AFECT IS NOT NULL'||
                    '  AND D2.CMPTE_AFECTADO <> 0			'||  
                    '  AND D2.FECHA BETWEEN TO_DATE(''01/01/1900'',''DD/MM/YYYY'') AND TO_DATE(''' || UN_FECHACORTE || ''',''DD/MM/YYYY'')'||
                    '  AND T.CLASE_CONTABLE IN (''A'',''J'') '||
                    '  AND PC.CLASECUENTA IN (''P'',''C'',''E'',''Y'',''N'',''G'') '||
                    '  AND C.COMPANIA IS NOT NULL'||
                    '  AND D2.CONSECUTIVOAFECTADO IS NOT NULL'||
                    '  AND C.TERCERO BETWEEN ''' || UN_TERCEROINICIAL ||''' AND ''' || UN_TERCEROFINAL ||''''||
                    '  AND PC.APLICA_DETERIORO NOT IN (0))';

      MI_ENLACE := ' TABLA.COMPANIA = VISTA.COMPANIA '||
    				       ' AND TABLA.ANO = VISTA.ANO '||
    				       ' AND TABLA.TIPO_CPTE = VISTA.TIPO '||
    				       ' AND TABLA.COMPROBANTE = VISTA.NUMERO ';

    	MI_EXISTE := ' UPDATE SET '||
    				       ' TABLA.DEBITOSAFECTADOS_CXP  = TABLA.DEBITOSAFECTADOS_CXP - VISTA.VALOR_CREDITO,'||
    				       ' TABLA.CREDITOSAFECTADOS_CXP = TABLA.CREDITOSAFECTADOS_CXP - VISTA.VALOR_DEBITO,'||
                   ' TABLA.MODIFIED_BY           ='''||UN_USUARIO||''','||
                   ' TABLA.DATE_MODIFIED         =SYSDATE';

      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA,
                                   UN_ACCION      =>'MM',
                                   UN_MERGEUSING  => MI_CONSULTA,
                                   UN_MERGEENLACE => MI_ENLACE,
                                   UN_MERGEEXISTE => MI_EXISTE);		

        EXCEPTION
             WHEN  PCK_EXCEPCIONES.EXC_MERGE THEN
            RAISE  PCK_EXCEPCIONES.EXC_MERGE;
 END;
    --SELECT 1 
     FOR RSTIPO IN(  
        SELECT CODIGO 
        FROM TIPO_COMPROBANTE 
        WHERE CLASE_CONTABLE = 'V'
        AND COMPANIA = UN_COMPANIA         
     )
     LOOP
        BEGIN       

       --UPDATE 9 

            MI_TABLA:='COMPROBANTE_CNT';

            MI_CAMPOS := ' ABONOCXP       = ABONADO ,'||
                         ' MODIFIED_BY   ='''||UN_USUARIO||''','||
                         ' DATE_MODIFIED =SYSDATE';

            MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''''|| 
                            ' AND FECHA_ABONO <= TO_DATE(''' || UN_FECHACORTE || ''',''DD/MM/YYYY'')'||
                            ' AND ABONADO <> 0 '||
                            ' AND ABONOCXP <> ABONADO'||
                            ' AND TIPO = ''' || RSTIPO.CODIGO ||'''';

            MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => MI_TABLA,
                                        UN_ACCION   => 'M',
                                        UN_CAMPOS   => MI_CAMPOS,
                                        UN_CONDICION=> MI_CONDICION);								                            
             EXCEPTION
                  WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                 RAISE  PCK_EXCEPCIONES.EXC_ACTUALIZAR;                      
        END;
     END LOOP;

--SELECT 2
  BEGIN
        SELECT DISTINCT 'X'
        INTO MI_REGISTRO
        FROM V_PLAN_CONTABLE PC 
        INNER JOIN DETALLE_COMPROBANTE_CNT D
        ON PC.ANO = D.ANO
        AND PC.COMPANIA = D.COMPANIA
        AND PC.ID = D.CUENTA
        INNER JOIN TIPO_COMPROBANTE T 
        ON D.TIPO_CPTE = T.CODIGO 
        AND D.COMPANIA = T.COMPANIA 
        INNER JOIN COMPROBANTE_CNT COM
        ON D.COMPROBANTE = COM.NUMERO 
        AND D.TIPO_CPTE = COM.TIPO
        AND D.ANO = COM.ANO
        AND D.COMPANIA = COM.COMPANIA 
        WHERE D.COMPANIA= UN_COMPANIA
        AND T.CLASE_CONTABLE IN ('V') 
        AND D.TERCERO BETWEEN UN_TERCEROINICIAL AND UN_TERCEROFINAL    
        AND (D.VALOR_DEBITO - (D.DEBITOSAFECTADOS_CXP + D.CREDITOSAFECTADOS_CXP) - COM.ABONADO) > 0
        AND COM.FECHA_VCN_DOC <= TO_DATE(UN_FECHACORTE,'DD/MM/YYYY')
        AND MONTHS_BETWEEN(TO_DATE(UN_FECHACORTE,'DD/MM/YYYY'),COM.FECHA_VCN_DOC) >= UN_MESESVENCIDOS
        AND (CASE WHEN D.FECHA_CALC_DET IS NOT NULL THEN MONTHS_BETWEEN (TO_DATE(UN_FECHACORTE,'DD/MM/YYYY'),D.FECHA_CALC_DET) ELSE MONTHS_BETWEEN(TO_DATE(UN_FECHACORTE,'DD/MM/YYYY'), COM.FECHA_VCN_DOC) END) > 0
        AND PC.APLICA_DETERIORO NOT IN (0);

        EXCEPTION

              WHEN NO_DATA_FOUND THEN
                   MI_REGISTRO:=NULL;

              WHEN PCK_EXCEPCIONES.EXC_MERGE THEN  

                   MI_REEMPLAZOS (1).CLAVE := 'CAMPO';
                   MI_REEMPLAZOS (1).VALOR :=  MI_CAMPOS;
                   MI_REEMPLAZOS (2).CLAVE := 'TABLA';
                   MI_REEMPLAZOS (2).VALOR :=  MI_TABLA;

                   PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE, 
                                              UN_TABLAERROR  => MI_TABLA,
                                              UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_UPDATE_DETERCARTERA,                                   
                                              UN_REEMPLAZOS  => MI_REEMPLAZOS); 

              WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN  

                   MI_REEMPLAZOS (0).CLAVE := 'CAMPO';
                   MI_REEMPLAZOS (0).VALOR :=  MI_CAMPOS;
                   MI_REEMPLAZOS (1).CLAVE := 'TABLA';
                   MI_REEMPLAZOS (1).VALOR :=  MI_TABLA;

                   PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE, 
                                              UN_TABLAERROR  => MI_TABLA,
                                              UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_UPDATE_DETERCARTERA,                                   
                                              UN_REEMPLAZOS  => MI_REEMPLAZOS); 
    END;

    IF(MI_REGISTRO IS NOT NULL) THEN
      MI_RESPUESTA:='OK';
    ELSE
      MI_RESPUESTA:='NO HAY DATOS';
    END IF;

RETURN MI_RESPUESTA;

END FC_REVISAAFECTACIONESDETERIORO;

--2
FUNCTION FC_REVERSARCIERRE
/*
  NAME              : REVERSARCIERRE - FORM_REVERSARCIERRE
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE MIGRADOR     : 28/04/2016
  TIME              : 12:30 PM
  SOURCE MODULE     : Contabilidad
  MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS
  DATE MODIFIED     : 16/01/2017
  TIME              : 04:13 PM
  DESCRIPTION       : Proceso de reversar un cierre contable. 16/01/2017 Se ajusta al estandár. 
  @NAME:  reversarCierreContable
  @METHOD:  GET  
*/
(
  UN_COMPANIA      IN    PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO           IN    PCK_SUBTIPOS.TI_ANIO,
  UN_TIPOCPTE      IN    PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
  UN_NROCPTE       IN    PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT, 
  UN_MODULO        IN    VARCHAR2
)
RETURN PCK_SUBTIPOS.TI_LOGICO 
AS 
  --MI_ERROR_FUN      NUMBER:=GL_ERROR_NUM + 2;
  MI_RTA            VARCHAR2(3000);
  MI_EXISTE         VARCHAR2(3000);
  MI_CONDICION      VARCHAR2(3000);
  MI_REGMODIF       NUMBER;
  MI_TABLE_NAME      PCK_SUBTIPOS.TI_TABLA;
BEGIN 
    MI_TABLE_NAME := 'DETALLE_COMPROBANTE_CNT';
    --VERIFICAR EL ESTADO DEL MES  DE DICIEMBRE PARA LA VIGENCIA EN LA CUAL SE DESEA REVERSAR EL CIERRE
    MI_RTA := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOMES(UN_COMPANIA=> UN_COMPANIA, 
                                                    UN_ANO     => UN_ANO, 
                                                    UN_MES     => 12, 
                                                    UN_MODULO  => UN_MODULO,
                                                    UN_PROCESO => 1);
    IF MI_RTA = 'A' THEN 
      BEGIN 
        SELECT    'X'
        INTO      MI_EXISTE
        FROM      DETALLE_COMPROBANTE_CNT
        WHERE     COMPANIA     = UN_COMPANIA 
          AND     ANO          = UN_ANO
          AND     TIPO_CPTE    = UN_TIPOCPTE
          AND     COMPROBANTE  = UN_NROCPTE
        GROUP BY    COMPANIA
                  , ANO
                  , TIPO_CPTE
                  , COMPROBANTE;
      EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_EXISTE := 'N';
      END;
      IF MI_EXISTE <> 'N' THEN
        -- se elimina el detalle y header del comprobante especificado. Se omite el llamado al procedimiento ACTCONTA
        -- debido a que este se llama en el trigger de las tablas relacionadas
        BEGIN 
        PCK_GENERALES.GL_DELETE_CPTE := 1;--TICKET 7734212 (19/07/2023 lvega)
        PCK_SYSMAN_UTL.PR_DISABLE_KEY_IN_DELETE( UN_TABLE_NAME        => MI_TABLE_NAME);
            MI_CONDICION := '       COMPANIA    = '''|| UN_COMPANIA ||
                           ''' AND  ANO         = '|| UN_ANO ||
                             ' AND  TIPO_CPTE   = '''|| UN_TIPOCPTE ||
                           ''' AND  COMPROBANTE = '|| UN_NROCPTE ||'';   
            MI_REGMODIF := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETALLE_COMPROBANTE_CNT',
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION );
        PCK_SYSMAN_UTL.PR_ENABLE_KEY_IN_DELETE( UN_TABLE_NAME        => MI_TABLE_NAME);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
        PCK_GENERALES.GL_DELETE_CPTE := 0;
            MI_REGMODIF := 0;
        END;
        IF MI_REGMODIF <> 0 THEN
            BEGIN
              MI_CONDICION := '  COMPANIA  = '''|| UN_COMPANIA ||
                           ''' AND  ANO    = '|| UN_ANO ||  
                             ' AND  TIPO   = '''|| UN_TIPOCPTE ||
                           ''' AND  NUMERO = '|| UN_NROCPTE ||'';                       
              MI_REGMODIF := PCK_DATOS.FC_ACME(UN_TABLA     => 'COMPROBANTE_CNT', 
                                               UN_ACCION    => 'E',
                                               UN_CONDICION => MI_CONDICION );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN 
              MI_REGMODIF := 0;
            END;
            IF MI_REGMODIF = 0 THEN 
               -- PCK_CONTABILIDAD.PR_CUADRECONTA_AUX(UN_COMPANIA => UN_COMPANIA, UN_ANIO     => UN_ANO);
                --PCK_CONTABILIDAD.PR_CUADRECONTA(UN_COMPANIA => UN_COMPANIA, UN_ANO      => UN_ANO);
                --Transacción finalizada. Se eliminó el comprobante de cierre.
            --ELSE
                --Transacción cancelada. se eliminaron correctamente los detalles del movimiento pero el header no se eliminó. 
                BEGIN 
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG(    
                                  UN_EXC_COD    => SQLCODE
                                 ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTAB_REVERSAR_CIERRE_1
                                 ,UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT'

                                 );
                END;
            END IF;
        ELSE
            --Transacción cancelada. No se eliminaron correctamente los detalles del movimiento.
            BEGIN 
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(    
                              UN_EXC_COD    => SQLCODE
                             ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTAB_REVERSAR_CIERRE_2
                             ,UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT'

                             );
            END;
        END IF;
      ELSE
        --El comprobante de cierre especificado no se ha encontrado. Verifique los datos e intente el proceso nuevamente.
        BEGIN 
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(    
                          UN_EXC_COD    => SQLCODE
                         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTAB_REVERSAR_CIERRE_3
                         ,UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT'

                         );
        END;
      END IF;       
    ELSIF MI_RTA = 'C' THEN
      --El periodo ya está cerrado. Para ejecutar el proceso debe desbloquear el mes 12 del año respectivo.
      BEGIN 
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(    
                        UN_EXC_COD    => SQLCODE
                       ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTAB_REVERSAR_CIERRE_4
                       ,UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT');
      END;
    ELSE 
        --No se ha determinado el  estado del periodo. Por favor verificar la configuración de bloqueo de periodos.
        BEGIN 
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(    
                          UN_EXC_COD    => SQLCODE
                         ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTAB_REVERSAR_CIERRE_5
                         ,UN_TABLAERROR => 'DETALLE_COMPROBANTE_CNT');
        END;
    END IF;
    PCK_GENERALES.GL_DELETE_CPTE := 0;
    RETURN -1;
END FC_REVERSARCIERRE;

--3
FUNCTION FC_CREARCOMPROBANTEDETERIORO
/*
  NAME              : FC_CREARCOMPROBANTEDETERIORO nombre en access crearcomprobantedeterioro
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
  DATE MIGRADOR     : 28/04/2016
  TIME              : 04:26 PM
  SOURCE MODULE     : Contabilidad
  DESCRIPTION       : Aplica cuando la entidad tiene activo el parametro ENTIDAD APLICA NIIF, crea comprobantes de deterioro. Esta relacionada con la función FC_REVISAAFECTACIONESDETERIORO 
  MODIFIER          : JUAN CAMILO RODRIGUEZ DIAZ
  DATE MODIFIED     : 09/06/2017
  TIME              : 13:00 PM
  MODIFICATIONS     : DEPURACION DE LA FUNCION DE ACUERDO AL ESTANDAR 
  PARAMETERS        : UN_COMPANIASELECCIONADA  => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                      UN_COMPANIA              => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                      UN_FECHACORTE            => FECHA DE CORTE DEL DETERIORO
                      UN_ANO                   => ANIO POR EL QUE SE FILTRA LOS DETERIOROS
                      UN_MESESVENCIDOS         => MESES VENCIDOS 
                      UN_TERCEROINICIAL        => EL TERCERO INICIAL
                      UN_TERCEROFINAL          => EL TERCERO FINAL
                      UN_DESCRIPCION           => DESCRIPCION DEL DETERIORO NIIF
                      UN_USUARIO               => USUARIO ACTIVO EN LA SESION
   @NAME:  generarComprobanteContableDeterioroDeCartera
  @METHOD:  GET  
*/
(
  UN_COMPANIASELECCIONADA IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ANO                  IN PCK_SUBTIPOS.TI_ANIO,
  UN_FECHACORTE           IN PCK_SUBTIPOS.TI_FECHA,
  UN_DESCRIPCION          IN PCK_SUBTIPOS.TI_DESCRIPCION,
  UN_MESESVENCIDOS        IN PCK_SUBTIPOS.TI_MES,
  UN_TERCEROINICIAL       IN PCK_SUBTIPOS.TI_TERCERO,
  UN_TERCEROFINAL         IN PCK_SUBTIPOS.TI_TERCERO,
  UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN VARCHAR2 AS 

  MI_TOTAL             NUMBER := 0;
  MI_SQL               PCK_SUBTIPOS.TI_STRSQL;
  MI_VALORPRESENTE     NUMBER := 0;
  MI_ERROR_FUN         NUMBER:=GL_ERROR_NUM + 3;
  MI_TIPOANTERIORD     VARCHAR2(200):= NULL;
  MI_NUMEROANTERIOR    NUMBER := 0;
  MI_CONSECUTIVOGEN    NUMBER;
  MI_FILTRO            VARCHAR2(200);
  MI_RSDATO            SYS_REFCURSOR;
  MI_CUENTA            VARCHAR2(32);
  MI_NATURALEZA        VARCHAR2(1);
  MI_CONSECUTIVODET    NUMBER;
  MI_TIPO              VARCHAR2(40);
  MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES           PCK_SUBTIPOS.TI_VALORES;
  MI_RTA               PCK_SUBTIPOS.TI_PARAMETRO;
  MI_RESPUESTA         PCK_SUBTIPOS.TI_PARAMETRO:='OK';
  MI_EXISTE            VARCHAR2(1);
  MI_TABLA             PCK_SUBTIPOS.TI_TABLA;
  MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;
  MI_REEMPLAZOS        PCK_SUBTIPOS.TI_CLAVEVALOR;

BEGIN

  MI_TIPO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>UN_COMPANIASELECCIONADA,
                                   UN_NOMBRE    =>'TIPO DE COMPROBANTE DETERIORO NIIF',
                                   UN_MODULO    =>1,
                                   UN_FECHA_PAR =>SYSDATE,
                                   UN_IND_MAYUS =>-1);
   BEGIN
   FOR RS IN  (
      SELECT 
          D.CUENTA,
          D.TERCERO,
          D.SUCURSAL,
          D.AUXILIAR,
          D.CENTRO_COSTO,
          D.COMPROBANTE NUMERO,
          D.TIPO_CPTE TIPO,
          D.ANO,
          D.FECHA_CALC_DET,
          (D.VALOR_DEBITO - (D.DEBITOSAFECTADOS_CXP + D.CREDITOSAFECTADOS_CXP) - COM.ABONADO) AS SALDO,
          COM.FECHA_VCN_DOC,
          FLOOR(CASE WHEN D.FECHA_CALC_DET IS NOT NULL THEN MONTHS_BETWEEN (TRUNC(TO_DATE(UN_FECHACORTE,'DD/MM/YYYY HH24:MI:SS')), TRUNC(D.FECHA_CALC_DET)) ELSE  MONTHS_BETWEEN (TRUNC(TO_DATE(UN_FECHACORTE,'DD/MM/YYYY HH24:MI:SS')), TRUNC(COM.FECHA_VCN_DOC)) END) AS DIFERENCIAENMESES,
          COM.VALORPRESENTE 
        FROM V_PLAN_CONTABLE PC 
        INNER JOIN DETALLE_COMPROBANTE_CNT D
          ON PC.ANO = D.ANO
          AND PC.COMPANIA = D.COMPANIA
          AND PC.ID = D.CUENTA
        INNER JOIN TIPO_COMPROBANTE T 
          ON D.TIPO_CPTE = T.CODIGO 
          AND D.COMPANIA = T.COMPANIA 
        INNER JOIN COMPROBANTE_CNT COM
          ON D.COMPROBANTE = COM.NUMERO 
          AND D.TIPO_CPTE = COM.TIPO
          AND D.ANO = COM.ANO
          AND D.COMPANIA = COM.COMPANIA 
        WHERE D.COMPANIA= UN_COMPANIASELECCIONADA
          AND T.CLASE_CONTABLE IN ('V') 
          AND D.TERCERO BETWEEN UN_TERCEROINICIAL AND UN_TERCEROFINAL    
          AND (D.VALOR_DEBITO - (D.DEBITOSAFECTADOS_CXP + D.CREDITOSAFECTADOS_CXP) - COM.ABONADO) > 0
          AND TRUNC(COM.FECHA_VCN_DOC) <= TRUNC(TO_DATE(UN_FECHACORTE,'DD/MM/YYYY HH24:MI:SS'))
          AND MONTHS_BETWEEN(TRUNC(TO_DATE(UN_FECHACORTE,'DD/MM/YYYY HH24:MI:SS')),TRUNC(COM.FECHA_VCN_DOC)) >= UN_MESESVENCIDOS
          AND (CASE WHEN D.FECHA_CALC_DET IS NOT NULL 
                THEN MONTHS_BETWEEN (TRUNC(TO_DATE(UN_FECHACORTE,'DD/MM/YYYY HH24:MI:SS')), TRUNC(D.FECHA_CALC_DET))
                ELSE MONTHS_BETWEEN (TRUNC(TO_DATE(UN_FECHACORTE,'DD/MM/YYYY HH24:MI:SS')), TRUNC(COM.FECHA_VCN_DOC)) END) > 0
          AND PC.APLICA_DETERIORO NOT IN (0)
    )

    LOOP  
          MI_CONSECUTIVODET := 0;
          MI_FILTRO := ' FROM V_PLAN_CONTABLE WHERE COMPANIA = ''' || UN_COMPANIASELECCIONADA ||''''||
                       ' AND ANO = ' || UN_ANO || ''||
                       ' AND ID = ''' || RS.CUENTA || '''';

          IF RS.FECHA_CALC_DET IS NULL THEN                
            MI_SQL := ' SELECT DEB_RECO_DET AS CUENTA, ''D'' AS NATURALEZA'  || MI_FILTRO || 
                      ' UNION SELECT CRE_RECO_DET AS CUENTA, ''C'' AS NATURALEZA' || MI_FILTRO;    

            MI_VALORPRESENTE := ROUND(NVL(RS.SALDO,0) / POWER((1 + PCK_CONTABILIDAD4.FC_TASAI(UN_FECHA=>RS.FECHA_VCN_DOC)),RS.DIFERENCIAENMESES),0);
            MI_TOTAL := NVL(RS.SALDO,0) - MI_VALORPRESENTE;             
          ELSE 
            MI_SQL := ' SELECT DEB_CAUS_DET AS CUENTA, ''D'' AS NATURALEZA'  || MI_FILTRO || 
                      ' UNION SELECT CRE_CAUS_DET AS CUENTA, ''C'' AS NATURALEZA' || MI_FILTRO;                                       

            MI_VALORPRESENTE := ROUND(NVL(RS.VALORPRESENTE, 0) / POWER((1 + PCK_CONTABILIDAD4.FC_TASAI(UN_FECHA=>UN_FECHACORTE)), RS.DIFERENCIAENMESES) ,0);
            MI_TOTAL := NVL(RS.VALORPRESENTE, 0) - MI_VALORPRESENTE;                
          END IF;                         

          OPEN MI_RSDATO FOR MI_SQL; 
          LOOP
          FETCH MI_RSDATO INTO MI_CUENTA , MI_NATURALEZA;
           EXIT WHEN MI_RSDATO%NOTFOUND;
             IF (MI_CUENTA IS NULL) THEN
                RETURN 'No se encuentra configuración de cuentas contables en el plan de cuentas';                
             END IF;          
             IF (MI_CONSECUTIVODET = 0) THEN
                --En access ingresa a función crearcomprobantedeterioro
                BEGIN 
                    SELECT 'X'
                    INTO   MI_EXISTE
                    FROM   TIPO_COMPROBANTE
                    WHERE  COMPANIA     = UN_COMPANIA 
                      AND  CODIGO       = 'DCN'
                      AND  CLASE_CONTABLE  = 'C';

                    EXCEPTION WHEN NO_DATA_FOUND THEN
                      MI_EXISTE := NULL;
                END;
                IF MI_EXISTE IS NULL THEN
                BEGIN
                      MI_TABLA :='TIPO_COMPROBANTE';

                      MI_CAMPOS:='COMPANIA,'||
                                 'CODIGO,'||
                                 'NOMBRE,'||
                                 'PIDE_RETENCION,'||
                                 'CLASE_CONTABLE,'||
                                 'FORMATO';

                      MI_VALORES:=''''|| UN_COMPANIA ||''','||
                                  '''DCN'','||
                                  '''DETERIORO CARTERA NIIF'','||
                                  '0,'||
                                  '''C'','||
                                  '''CDC''';

                      MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA    =>MI_TABLA,
                                                  UN_ACCION   =>'I',
                                                  UN_CAMPOS   =>MI_CAMPOS,
                                                  UN_VALORES  =>MI_VALORES);  
                   EXCEPTION
                        WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                       RAISE  PCK_EXCEPCIONES.EXC_INSERTAR;
               END;
                END IF;

                IF MI_TIPOANTERIORD <> RS.TIPO OR MI_NUMEROANTERIOR <> RS.NUMERO THEN
                BEGIN
                    MI_TIPOANTERIORD  := RS.TIPO;
                    MI_NUMEROANTERIOR := RS.NUMERO;                  

                    MI_CONSECUTIVOGEN := PCK_CONTABILIDAD4.FC_ENUMERARCOMPRO_DETERIORO(UN_COMPANIA=>UN_COMPANIA,
                                                                                       UN_TIPO    =>MI_TIPO,
                                                                                       UN_ANIO    =>UN_ANO);                

                    MI_CAMPOS:='COMPANIA,'||
                               'ANO,'||
                               'TIPO,'||
                               'NUMERO,'||
                               'FECHA,'||
                               'DESCRIPCION,'||
                               'VLR_BASE,'||
                               'FECHA_VCN_DOC,'||
                               'VLR_DOCUMENTO,'||
                               'NRO_DOCUMENTO,'||
                               'TERCERO,'||
                               'SUCURSAL,'||
                               'DEBITO,'||
                               'CREDITO,'||
                               'ABONADO,'||
                               'VLR_BASEIVA,'||
                               'VLRAGIRAR,'||
                               'CENTRO_COSTO,'||
                               'AUXILIAR,'||
                               'CREATED_BY,'||
                               'DATE_CREATED';

                          MI_VALORES := ''''||UN_COMPANIA||''','||
                                  UN_ANO||','||
                                  ''''||MI_TIPO||''','||
                                  MI_CONSECUTIVOGEN||','||
                                  'TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY HH24:MI:SS''),'||
                                  ''''|| UN_DESCRIPCION || ''','||
                                  MI_TOTAL || ','||
                                  'ADD_MONTHS(TO_DATE(TRUNC(TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY HH24:MI:SS'')),''DD/MM/YYYY''),1),'||
                                  MI_TOTAL || ','||
                                  '''' || RS.TIPO || '-' || TO_CHAR(RS.NUMERO) || ''','||
                                  '''' || RS.TERCERO || ''','||
                                  '''' || RS.SUCURSAL || ''',' ||
                                  MI_TOTAL || ',' ||
                                  MI_TOTAL || ','||
                                  '0,'||
                                  '0,'||
                                  '0,'||
                                  '''' || RS.CENTRO_COSTO || ''','||
                                  '''' || RS.AUXILIAR || ''','||
                                  '''' || UN_USUARIO ||''','||
                                  'SYSDATE';

                    MI_TABLA:='COMPROBANTE_CNT';    

                    MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>MI_TABLA,
                                                 UN_ACCION  =>'I',
                                                 UN_CAMPOS  =>MI_CAMPOS,
                                                 UN_VALORES =>MI_VALORES);    
                   EXCEPTION
                        WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                       RAISE  PCK_EXCEPCIONES.EXC_INSERTAR;
                END;
                END IF;
             END IF;    

             MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;
             BEGIN
                    MI_TABLA:='DETALLE_COMPROBANTE_CNT';

                    MI_CAMPOS:='COMPANIA,'||
                               'ANO,'||
                               'TIPO_CPTE,'||
                               'COMPROBANTE,'||
                               'CONSECUTIVO,'||
                               'CUENTA,'||
                               'FECHA,'||
                               'NATURALEZA,'||
                               'DESCRIPCION,'||
                               'VALOR_DEBITO,'||
                               'VALOR_CREDITO,'||
                               'EJECUCION_DEBITO,'||
                               'BASE_GRAVABLE,'||
                               'CENTRO_COSTO,'||
                               'TERCERO,'||
                               'SUCURSAL,'||
                               'AUXILIAR,'||
                               'CMPTE_AFECTADO,'||
                               'ABONOINICIAL,'||
                               'TIPO_GENERADIF,'||
                               'NRO_GENERADIF,'||
                               'CREATED_BY,'||
                               'DATE_CREATED';

                    MI_VALORES:=''''||UN_COMPANIA ||''',' ||
                                UN_ANO || ','||
                                '''' || MI_TIPO ||''','||
                                MI_CONSECUTIVOGEN || ','||
                                MI_CONSECUTIVODET || ','||
                                ''''|| MI_CUENTA || ''','||
                                'TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY HH24:MI:SS''),'||                          
                                ''''|| MI_NATURALEZA||''','||
                                '''' || UN_DESCRIPCION || ''','||
                                'CASE WHEN '''||MI_NATURALEZA ||'''= ''D'' THEN '||MI_TOTAL||' ELSE 0  END,'||
                                'CASE WHEN '''||MI_NATURALEZA ||'''= ''C'' THEN '||MI_TOTAL||' ELSE 0  END,'|| 
                                '0,'||
                                 MI_TOTAL || ','||
                                 '''' || RS.CENTRO_COSTO || ''','||
                                 '''' || RS.TERCERO || ''','||
                                 '''' || RS.SUCURSAL || ''','||
                                 '''' || RS.AUXILIAR || ''',' ||
                                 'NULL,'||
                                 '0,'||
                                 '''' || RS.TIPO || ''','||
                                 RS.NUMERO || ','||
                                 ''''|| UN_USUARIO || ''','||
                                 'SYSDATE';

                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA    =>MI_TABLA,
                                                UN_ACCION   =>'I',
                                                UN_CAMPOS   =>MI_CAMPOS,
                                                UN_VALORES  =>MI_VALORES);            
                   EXCEPTION
                        WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                       RAISE  PCK_EXCEPCIONES.EXC_INSERTAR;
              END;
          END LOOP;
          CLOSE MI_RSDATO;          
          BEGIN
              MI_TABLA:='COMPROBANTE_CNT';

              MI_CAMPOS:='VALORPRESENTE   ='|| MI_VALORPRESENTE||','||
                         'MODIFIED_BY     ='''||UN_USUARIO||''','||
                         'DATE_MODIFIED   =SYSDATE';

              MI_CONDICION:=' COMPANIA    = ''' || UN_COMPANIASELECCIONADA ||''''||
                            ' AND ANO     = ' || RS.ANO || ''||
                            ' AND TIPO    = ''' || RS.TIPO ||''''|| 
                            ' AND NUMERO  = ' || RS.NUMERO;

              MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     =>MI_TABLA,            
                                          UN_ACCION    =>'M',  
                                          UN_CAMPOS    =>MI_CAMPOS, 
                                          UN_CONDICION =>MI_CONDICION);
              EXCEPTION
                   WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE  PCK_EXCEPCIONES.EXC_ACTUALIZAR;
          END;
    END LOOP;

     EXCEPTION
             WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN  

                  MI_REEMPLAZOS (0).CLAVE := 'CAMPO';
                  MI_REEMPLAZOS (0).VALOR :=  MI_CAMPOS;
                  MI_REEMPLAZOS (1).CLAVE := 'TABLA';
                  MI_REEMPLAZOS (1).VALOR :=  MI_TABLA;

                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE, 
                                             UN_TABLAERROR  => MI_TABLA,
                                             UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_INSERT_DETERCARTERA,                                   
                                             UN_REEMPLAZOS  => MI_REEMPLAZOS
                                            );  
             WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN  

                  MI_REEMPLAZOS (0).CLAVE := 'CAMPO';
                  MI_REEMPLAZOS (0).VALOR :=  MI_CAMPOS;
                  MI_REEMPLAZOS (1).CLAVE := 'TABLA';
                  MI_REEMPLAZOS (1).VALOR :=  MI_TABLA;

                  PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE, 
                                             UN_TABLAERROR  => MI_TABLA,
                                             UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_UPDATE_DETERCARTERA,                                   
                                             UN_REEMPLAZOS  => MI_REEMPLAZOS
                                            );   
    END;        
  RETURN MI_RESPUESTA;

END FC_CREARCOMPROBANTEDETERIORO;

--4
FUNCTION FC_TASAI
/*
  NAME              : FC_TASAI nombre en access TASAI
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
  DATE MIGRADOR     : 29/04/2016
  TIME              : 11:26 AM
  SOURCE MODULE     : Contabilidad
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Retorna la tasa de interes correspondiente a una fecha determinada
  @NAME:  consultarTasaDeInteres
  @METHOD:  GET  
*/
(
  UN_FECHA  IN  PCK_SUBTIPOS.TI_FECHA
)
RETURN FLOAT AS 
  MI_TASA     FLOAT := 0;
  MI_TASARTA  FLOAT := 0;
BEGIN

   BEGIN
     SELECT TASA
      INTO MI_TASA
     FROM TASAS_INTERES
     WHERE TRUNC(TO_DATE(UN_FECHA,'DD/MM/YYYY HH24:MI:SS')) BETWEEN TRUNC(FECHA_INICIAL) AND TRUNC(FECHA_FINAL);

   EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_TASARTA:=0;
   END;   
    IF MI_TASA <> 0 THEN
       MI_TASARTA := (POWER((1 + (MI_TASA / 100)), (1 / 12)) - 1);
    END IF;

  RETURN MI_TASARTA;
END FC_TASAI;

--5
FUNCTION FC_ENUMERARCOMPRO_DETERIORO
/*
  NAME              : FC_ENUMERARCOMPRO_DETERIORO nombre en access Enumerarcompro_deterioro
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
  DATE MIGRADOR     : 29/04/2016
  TIME              : 13:04 PM
  SOURCE MODULE     : Contabilidad
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Retorna el consecutivo correspondiente para creación de comprobante de deterioro
  @NAME:  generarConsecutivoComprobanteDeterioro
  @METHOD:  GET
*/
(
  UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_TIPO     IN PCK_SUBTIPOS.TI_CAMPOS,
  UN_ANIO     IN PCK_SUBTIPOS.TI_ANIO
)
RETURN NUMBER AS 
  MI_CONSECUTIVO     NUMBER := 0;
  MI_RTA             PCK_SUBTIPOS.TI_CAMPOS;
  MI_CRITERIO        PCK_SUBTIPOS.TI_CAMPOS;
BEGIN

   IF UN_TIPO <> '' OR UN_TIPO IS NOT NULL THEN     
      MI_CRITERIO:='COMPANIA  = '''|| UN_COMPANIA ||''''||
                   ' AND TIPO = ''' || UN_TIPO ||''''||
                   ' AND ANO  = ' || UN_ANIO ||'';
      MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO(UN_TABLA     =>'COMPROBANTE_CNT', 
                                                         UN_CRITERIO  =>MI_CRITERIO,
                                                         UN_CAMPO     =>'NUMERO');
                                                         --UN_INICIAL   =>TO_CHAR(UN_ANIO)||LPAD('1',6,'0'));

      IF (MI_CONSECUTIVO=0 OR MI_CONSECUTIVO = 1) THEN
        MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO('CONSECUTIVOTC', 
                    'COMPANIA = '''|| UN_COMPANIA ||''' 
                    AND TIPOCOMPROBANTE = ''' || UN_TIPO ||'''
                    AND ANO = ' || UN_ANIO ||'',
                    'CONSECUTIVO');
        MI_CONSECUTIVO := TO_NUMBER(TO_CHAR(UN_ANIO) || LPAD(TO_CHAR(MI_CONSECUTIVO), 6, 0));    

          IF (MI_CONSECUTIVO=0 OR MI_CONSECUTIVO = 1) THEN
              MI_CONSECUTIVO := TO_NUMBER(TO_CHAR(UN_ANIO) || LPAD(TO_CHAR(1), 6, 0));    

               MI_RTA := PCK_DATOS.FC_ACME('CONSECUTIVOTC'
                                  ,'I'
                                  ,'COMPANIA,TIPOCOMPROBANTE,ANO,CONSECUTIVO'
                                  ,''''|| UN_COMPANIA     ||'''
                                  ,''' || UN_TIPO         ||'''
                                  ,'   || UN_ANIO         ||'
                                  ,'   || MI_CONSECUTIVO  ||''
                                  ,NULL,NULL,NULL,NULL,NULL,NULL);            
          END IF;
      END IF;

   END IF;  

    RETURN MI_CONSECUTIVO;  

END FC_ENUMERARCOMPRO_DETERIORO;

--6 
FUNCTION FC_REVISARFACTURASCANCELADAS
/*
  NAME              : FC_REVISARFACTURASCANCELADAS nombre en access RevisarFacturascanceladas
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : ANDREA CAROLINA PINEDA OVALLE
  DATE MIGRADOR     : 02/05/2016
  TIME              : 02:30 PM
  SOURCE MODULE     : Contabilidad 
  DESCRIPTION       : Calculo de las facturas que ya se pagaron
  MODIFIER          : JUAN CAMILO RODRIGUEZ DIAZ
  DATE MODIFIED     : 13/06/2017
  TIME              : 13:00 PM
  MODIFICATIONS     : DEPURACION DE LA FUNCION DE ACUERDO AL ESTANDAR 
  PARAMETERS        : UN_COMPANIASELECCIONADA  => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                      UN_COMPANIA              => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                      UN_FECHACORTE            => FECHA DE CORTE DEL DETERIORO
                      UN_ANO                   => ANIO POR EL QUE SE FILTRA LOS DETERIOROS
                      UN_MESESVENCIDOS         => MESES VENCIDOS
                      UN_TERCEROINICIAL        => EL TERCERO INICIAL
                      UN_TERCEROFINAL          => EL TERCERO FINAL
                      UN_DESCRIPCION           => DESCRIPCION DEL DETERIORO NIIF
                      UN_USUARIO               => USUARIO ACTIVO EN LA SESION
  @NAME:  revisarFacturasCanceladasDeterioroDeCartera
  @METHOD:  GET  
*/
(
  UN_COMPANIASELECCIONADA IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_FECHACORTE           IN PCK_SUBTIPOS.TI_FECHA,
  UN_ANO                  IN PCK_SUBTIPOS.TI_ANIO,
  UN_MESESVENCIDOS        IN PCK_SUBTIPOS.TI_MES,
  UN_TERCEROINICIAL       IN PCK_SUBTIPOS.TI_TERCERO,
  UN_TERCEROFINAL         IN PCK_SUBTIPOS.TI_TERCERO,
  UN_DESCRIPCION          IN PCK_SUBTIPOS.TI_DESCRIPCION,
  UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO  
)
RETURN VARCHAR2 AS 
  MI_RESPUESTA       VARCHAR2(32000) :='OK';
  MI_TIPOANTERIORD   VARCHAR2(200):= NULL;
  MI_NUMEROANTERIOR  NUMBER := 0;  
  MI_ERROR_FUN       NUMBER:=GL_ERROR_NUM + 6;
  MI_RTA             VARCHAR2(3200);
  MI_TIPO            VARCHAR2(40);
  MI_CONSECUTIVOGEN  NUMBER;
  MI_CAMPOS          PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES         PCK_SUBTIPOS.TI_VALORES;
  MI_FILTRO          VARCHAR2(200);
  MI_RSDATO          SYS_REFCURSOR;
  MI_CUENTA          VARCHAR2(32);
  MI_NATURALEZA      PCK_SUBTIPOS.TI_NATURALEZA;
  MI_CONSEC          NUMBER := 0;
  MI_EXISTE          VARCHAR2(1);
  MI_TABLA           PCK_SUBTIPOS.TI_TABLA;
  MI_CONSULTA        PCK_SUBTIPOS.TI_STRSQL;
  MI_ENLACE          VARCHAR2(32000);
  MI_CONDICION       PCK_SUBTIPOS.TI_CONDICION;
  MI_REEMPLAZOS      PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

  BEGIN
       FOR RS IN  (SELECT FACTURAS_CONDETERIORO.* 
      FROM
      (
        SELECT D.COMPANIA,
          D.ANO,
          D.TIPO_CPTE AS TIPO,
          D.COMPROBANTE,
          COM.TERCERO,
          COM.SUCURSAL,
          D.AUXILIAR,
          D.CENTRO_COSTO,
          D.CUENTA
        FROM COMPROBANTE_CNT COM
        INNER JOIN DETALLE_COMPROBANTE_CNT D
        ON (COM.NUMERO = D.COMPROBANTE)
          AND (COM.TIPO = D.TIPO_CPTE)
          AND (COM.ANO = D.ANO)
          AND (COM.COMPANIA = D.COMPANIA)
        WHERE D.COMPANIA = UN_COMPANIA
          AND TRUNC(D.FECHA_CALC_DET) BETWEEN TRUNC(D.FECHA_CALC_DET) AND TRUNC(TO_DATE(UN_FECHACORTE,'DD/MM/YYYY HH24:MI:SS'))
          AND (COM.REC_DETERIORO IS NULL
          OR COM.REC_DETERIORO=0)
      ) FACTURAS_CONDETERIORO 
      LEFT JOIN (
        SELECT D.COMPANIA, 
          D.ANO, 
          D.TIPO_CPTE AS TIPO, 
          D.COMPROBANTE AS NUMERO
        FROM V_PLAN_CONTABLE PC 
        INNER JOIN DETALLE_COMPROBANTE_CNT D
          ON PC.ANO = D.ANO
          AND PC.COMPANIA = D.COMPANIA
          AND PC.ID = D.CUENTA
          INNER JOIN TIPO_COMPROBANTE T 
          ON D.TIPO_CPTE = T.CODIGO 
          AND D.COMPANIA = T.COMPANIA 
          INNER JOIN COMPROBANTE_CNT COM
          ON D.COMPROBANTE = COM.NUMERO 
          AND D.TIPO_CPTE = COM.TIPO
          AND D.ANO = COM.ANO
          AND D.COMPANIA = COM.COMPANIA 
        WHERE D.COMPANIA= UN_COMPANIA
          AND T.CLASE_CONTABLE IN ('V') 
          AND D.TERCERO BETWEEN UN_TERCEROINICIAL AND UN_TERCEROFINAL
          AND (D.VALOR_DEBITO - (D.DEBITOSAFECTADOS_CXP + D.CREDITOSAFECTADOS_CXP) - COM.ABONADO) > 0
          AND TRUNC(COM.FECHA_VCN_DOC) <= TRUNC(TO_DATE(UN_FECHACORTE,'DD/MM/YYYY HH24:MI:SS'))
          AND MONTHS_BETWEEN(TRUNC(TO_DATE(UN_FECHACORTE,'DD/MM/YYYY HH24:MI:SS')),TRUNC(COM.FECHA_VCN_DOC)) >= UN_MESESVENCIDOS
          AND (CASE WHEN D.FECHA_CALC_DET IS NOT NULL THEN MONTHS_BETWEEN (TRUNC(TO_DATE(UN_FECHACORTE,'DD/MM/YYYY HH24:MI:SS')),TRUNC(D.FECHA_CALC_DET)) ELSE MONTHS_BETWEEN(TRUNC(TO_DATE(UN_FECHACORTE,'DD/MM/YYYY HH24:MI:SS')), TRUNC(COM.FECHA_VCN_DOC)) END) > 0
          AND PC.APLICA_DETERIORO NOT IN (0)
        ) ACTFECHACALCULO
        ON (FACTURAS_CONDETERIORO.COMPANIA       = ACTFECHACALCULO.COMPANIA)
          AND (FACTURAS_CONDETERIORO.ANO         = ACTFECHACALCULO.ANO)
          AND (FACTURAS_CONDETERIORO.TIPO        = ACTFECHACALCULO.TIPO)
          AND (FACTURAS_CONDETERIORO.COMPROBANTE = ACTFECHACALCULO.NUMERO)
        WHERE ACTFECHACALCULO.COMPANIA IS NULL)

    LOOP
     BEGIN
      IF (MI_TIPOANTERIORD <> RS.TIPO) OR (MI_NUMEROANTERIOR <> RS.COMPROBANTE) THEN

        MI_TIPO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  =>UN_COMPANIASELECCIONADA,
                                         UN_NOMBRE     =>'TIPO DE COMPROBANTE DETERIORO NIIF',
                                         UN_MODULO     =>1,
                                         UN_FECHA_PAR  =>SYSDATE,
                                         UN_IND_MAYUS  =>-1); 
        BEGIN 
          SELECT 'X'
          INTO   MI_EXISTE
          FROM   TIPO_COMPROBANTE
          WHERE  COMPANIA     = UN_COMPANIA 
            AND  CODIGO       = 'DCN'
            AND  CLASE_CONTABLE  = 'C';

          EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_EXISTE := NULL;
        END;
        IF MI_EXISTE IS NULL THEN

            BEGIN

                  MI_TABLA:='TIPO_COMPROBANTE';

                  MI_CAMPOS:='COMPANIA,'||
                             'CODIGO,'||
                             'NOMBRE,'||
                             'PIDE_RETENCION,'||
                             'CLASE_CONTABLE,'||
                             'FORMATO,'||
                             'DATE_CREATED,'||
                             'CREATED_BY';

                  MI_VALORES:=''''||UN_COMPANIA ||''','||
                              '''DCN'','||
                              '''DETERIORO CARTERA NIIF'','||
                              '0,'||
                              '''C'','||
                              '''CDC'','||
                              'SYSDATE,'||
                              ''''||UN_USUARIO||'''';
                  MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA  =>MI_TABLA,
                                              UN_ACCION =>'I',
                                              UN_CAMPOS =>MI_CAMPOS,
                                              UN_VALORES=>MI_VALORES);    
                  EXCEPTION
                       WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                      RAISE  PCK_EXCEPCIONES.EXC_INSERTAR;

            END;

        END IF;

        FOR MI_RS1 IN
          (
              SELECT 
                D.VALOR_DEBITO AS VLR_ACUMULADO,
                D.TIPO_GENERADIF,
                D.NRO_GENERADIF,
                PC.CODIGO AS CUENTA
              FROM DETALLE_COMPROBANTE_CNT D
              INNER JOIN PLAN_CONTABLE PC
                ON D.CUENTA = PC.DEB_RECO_DET
              WHERE D.VALOR_DEBITO <> 0
                AND D.TIPO_GENERADIF = RS.TIPO
                AND D.NRO_GENERADIF = RS.COMPROBANTE
          )
        LOOP
          --CREAR COMPROBANTE FACTURAS CANCELADAS   

           IF MI_TIPOANTERIORD <> MI_RS1.TIPO_GENERADIF OR MI_NUMEROANTERIOR <> MI_RS1.NRO_GENERADIF THEN

               BEGIN 

                   MI_TIPOANTERIORD := MI_RS1.TIPO_GENERADIF;
                   MI_NUMEROANTERIOR := MI_RS1.NRO_GENERADIF;

                   MI_CONSECUTIVOGEN := PCK_CONTABILIDAD4.FC_ENUMERARCOMPRO_DETERIORO(UN_COMPANIA =>UN_COMPANIA,
                                                                                      UN_TIPO     =>MI_TIPO,
                                                                                      UN_ANIO     =>UN_ANO);                                                                        

                  MI_CAMPOS:='COMPANIA,'||
                             'ANO,'||
                             'TIPO,'||
                             'NUMERO,'||
                             'FECHA,'||
                             'DESCRIPCION,'||
                             'VLR_BASE,'||
                             'FECHA_VCN_DOC,'||
                             'VLR_DOCUMENTO,'||
                             'NRO_DOCUMENTO,'||
                             'TERCERO,'||
                             'SUCURSAL,'||
                             'DEBITO,'||
                             'CREDITO,'||
                             'ABONADO,'||
                             'VLR_BASEIVA,'||
                             'VLRAGIRAR,'||
                             'CENTRO_COSTO,'||
                             'AUXILIAR,'||
                             'CREATED_BY,'||
                             'DATE_CREATED';

                  MI_VALORES := ''''||UN_COMPANIA||''','||
                                UN_ANO||','||
                                ''''||MI_TIPO||''','||
                                MI_CONSECUTIVOGEN||','||
                                'TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY HH24:MI:SS''),'||
                                '''' || UN_DESCRIPCION || ''',' ||
                                MI_RS1.VLR_ACUMULADO || ','||
                                'ADD_MONTHS(TO_DATE(TRUNC(TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY HH24:MI:SS'')),''DD/MM/YYYY''),1),'||                             
                                MI_RS1.VLR_ACUMULADO || ','||
                                '''' || MI_TIPO || '-' || MI_CONSECUTIVOGEN || ''','||
                                '''' || RS.TERCERO || ''','||
                                '''' || RS.SUCURSAL || ''',' ||
                                MI_RS1.VLR_ACUMULADO || ',' ||
                                MI_RS1.VLR_ACUMULADO || ','||
                                '0,'||
                                '0,'||
                                '0,'||
                                '''' || RS.CENTRO_COSTO || ''','||
                                '''' || RS.AUXILIAR || ''','||
                                '''' || UN_USUARIO ||''','||
                                'SYSDATE';

                  MI_TABLA:='COMPROBANTE_CNT';     

                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   =>MI_TABLA,
                                               UN_ACCION  =>'I',
                                               UN_CAMPOS  =>MI_CAMPOS,
                                               UN_VALORES =>MI_VALORES);      
                  MI_CONSEC := 0;

                  EXCEPTION
                       WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                      RAISE  PCK_EXCEPCIONES.EXC_INSERTAR;

               END;

           END IF;

            MI_FILTRO := ' FROM V_PLAN_CONTABLE WHERE COMPANIA = ''' || UN_COMPANIASELECCIONADA ||
                         ''' AND ANO = ' || UN_ANO || 
                         '   AND ID = ''' || MI_RS1.CUENTA || '''';

            MI_CAMPOS := ' SELECT DEB_REC_DET AS CUENTA,'||
                         '''D'' AS NATURALEZA'  || MI_FILTRO || 
                         ' UNION SELECT CRE_REC_DET AS CUENTA,'||
                         '''C'' AS NATURALEZA' || MI_FILTRO; 

            OPEN MI_RSDATO FOR MI_CAMPOS; 
            LOOP
            FETCH MI_RSDATO INTO MI_CUENTA , MI_NATURALEZA;
            EXIT WHEN MI_RSDATO%NOTFOUND;
              IF MI_CUENTA IS NOT NULL THEN
                    BEGIN

                        MI_CONSEC := MI_CONSEC + 1;     

                        MI_TABLA:='DETALLE_COMPROBANTE_CNT'; 

                        MI_CAMPOS:='COMPANIA,'||
                                   'ANO,'||
                                   'TIPO_CPTE,'||
                                   'COMPROBANTE,'||
                                   'CONSECUTIVO,'||
                                   'CUENTA,'||
                                   'FECHA,'||
                                   'NATURALEZA,'||
                                   'DESCRIPCION,'||
                                   'VALOR_DEBITO,'||
                                   'VALOR_CREDITO,'||
                                   'EJECUCION_DEBITO,'||
                                   'BASE_GRAVABLE,'||
                                   'CENTRO_COSTO,'||
                                   'TERCERO,'||
                                   'SUCURSAL,'||
                                   'AUXILIAR,'||
                                   'CMPTE_AFECTADO,'||
                                   'ABONOINICIAL,'||
                                   'TIPO_GENERADIF,'||
                                   'NRO_GENERADIF,'||
                                   'CREATED_BY,'||
                                   'DATE_CREATED';

                        MI_VALORES:=''''  || UN_COMPANIA ||''',' ||
                                    UN_ANO || ','||
                                    '''' || MI_TIPO ||''',' ||
                                    MI_CONSECUTIVOGEN || ',' ||
                                    MI_CONSEC || ','||
                                    '''' || MI_CUENTA || ''','||
                                    'TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY HH24:MI:SS''),'||
                                    ''''|| MI_NATURALEZA||''','||
                                    ''''|| UN_DESCRIPCION || ''','||
                                    'CASE WHEN '''||MI_NATURALEZA||''' = ''D'' THEN  '||MI_RS1.VLR_ACUMULADO||' ELSE 0  END '|| ',' ||
                                    'CASE WHEN '''||MI_NATURALEZA||''' = ''C'' THEN  '||MI_RS1.VLR_ACUMULADO||' ELSE 0  END '|| ','||
                                    '0,'||
                                    MI_RS1.VLR_ACUMULADO || ','||
                                    '''' || RS.CENTRO_COSTO || ''', '||
                                    '''' || RS.TERCERO || ''','||
                                    '''' || RS.SUCURSAL || ''','||
                                    '''' || RS.AUXILIAR || ''','||
                                    'NULL,'||
                                    '0,'||
                                    '''' || MI_RS1.TIPO_GENERADIF || ''',' ||
                                    MI_RS1.NRO_GENERADIF || ','||
                                    ''''|| UN_USUARIO || ''','||
                                    'SYSDATE';

                          MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA  =>MI_TABLA,
                                                      UN_ACCION =>'I',
                                                      UN_CAMPOS =>MI_CAMPOS,
                                                      UN_VALORES=>MI_VALORES);
                          EXCEPTION
                               WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                              RAISE  PCK_EXCEPCIONES.EXC_INSERTAR;

                    END;
                END IF;
            END LOOP;
            CLOSE MI_RSDATO; 

                BEGIN

                    MI_TABLA:='COMPROBANTE_CNT';

                    MI_CONDICION:='COMPANIA   = ''' || UN_COMPANIASELECCIONADA ||''''||
                                 ' AND ANO    = ' || UN_ANO ||
                                 ' AND TIPO   = ''' || MI_RS1.TIPO_GENERADIF || ''''|| 
                                 ' AND NUMERO = ' ||  MI_RS1.NRO_GENERADIF || '';

                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA    =>'COMPROBANTE_CNT',
                                                UN_ACCION   =>'M',
                                                UN_CAMPOS   =>'REC_DETERIORO = -1',   
                                                UN_CONDICION=>MI_CONDICION);   
                    EXCEPTION
                         WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                        RAISE  PCK_EXCEPCIONES.EXC_ACTUALIZAR;

                END;

                MI_TIPOANTERIORD := RS.TIPO;
                MI_NUMEROANTERIOR := RS.COMPROBANTE;

        END LOOP; 

      END IF;

      END;

    END LOOP;

  --ACTUALIZO LA FECHA DE CALCULO DETERIORO    
  BEGIN

      MI_TABLA := 'DETALLE_COMPROBANTE_CNT';

       MI_CONSULTA := ' (SELECT DISTINCT 
                        D.COMPANIA,
                        D.CUENTA,
                        D.COMPROBANTE NUMERO,
                        D.TIPO_CPTE TIPO,
                        D.ANO,
                        TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY HH24:MI:SS'') FECHACALCULO
                        FROM V_PLAN_CONTABLE PC 
                        INNER JOIN DETALLE_COMPROBANTE_CNT D
                        ON PC.ANO = D.ANO
                        AND PC.COMPANIA = D.COMPANIA
                        AND PC.ID = D.CUENTA
                        INNER JOIN TIPO_COMPROBANTE T 
                        ON D.TIPO_CPTE = T.CODIGO 
                        AND D.COMPANIA = T.COMPANIA 
                        INNER JOIN COMPROBANTE_CNT COM
                        ON D.COMPROBANTE = COM.NUMERO 
                        AND D.TIPO_CPTE = COM.TIPO
                        AND D.ANO = COM.ANO
                        AND D.COMPANIA = COM.COMPANIA 
                        WHERE D.COMPANIA= '''||UN_COMPANIASELECCIONADA||'''
                        AND T.CLASE_CONTABLE IN (''V'') 
                        AND D.TERCERO BETWEEN ''' || UN_TERCEROINICIAL || ''' AND ''' || UN_TERCEROFINAL ||'''  
                        AND (D.VALOR_DEBITO - (D.DEBITOSAFECTADOS_CXP + D.CREDITOSAFECTADOS_CXP) - COM.ABONADO) > 0
                        AND TRUNC(COM.FECHA_VCN_DOC) <= TRUNC(TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY HH24:MI:SS''))
                        AND MONTHS_BETWEEN(TRUNC(TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY HH24:MI:SS'')),TRUNC(COM.FECHA_VCN_DOC)) >= '|| UN_MESESVENCIDOS ||'
                        AND (CASE WHEN D.FECHA_CALC_DET IS NOT NULL THEN MONTHS_BETWEEN(TRUNC(TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY HH24:MI:SS'')),TRUNC(D.FECHA_CALC_DET)) ELSE MONTHS_BETWEEN(TRUNC(TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY HH24:MI:SS'')), TRUNC(COM.FECHA_VCN_DOC)) END) > 0
                        AND PC.APLICA_DETERIORO NOT IN (0)) ';

      MI_ENLACE := ' TABLA.COMPANIA = VISTA.COMPANIA'||
                   ' AND TABLA.CUENTA = VISTA.CUENTA '|| 
                   ' AND TABLA.COMPROBANTE = VISTA.NUMERO'||
                   ' AND TABLA.TIPO_CPTE = VISTA.TIPO'||
                   ' AND TABLA.ANO = VISTA.ANO';

      MI_CAMPOS := 	' UPDATE SET TABLA.FECHA_CALC_DET = VISTA.FECHACALCULO ';  			

      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA       =>MI_TABLA,
                                   UN_ACCION      =>'MM',
                                   UN_MERGEUSING  =>MI_CONSULTA,
                                   UN_MERGEENLACE =>MI_ENLACE,
                                   UN_MERGEEXISTE =>MI_CAMPOS);                                   
      EXCEPTION
           WHEN  PCK_EXCEPCIONES.EXC_MERGE THEN
          RAISE  PCK_EXCEPCIONES.EXC_MERGE;
  END;           

  EXCEPTION
       WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN  
            MI_REEMPLAZOS (0).CLAVE := 'CAMPO';
            MI_REEMPLAZOS (0).VALOR :=  MI_CAMPOS;
            MI_REEMPLAZOS (1).CLAVE := 'TABLA';
            MI_REEMPLAZOS (1).VALOR :=  MI_TABLA;

            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD   => SQLCODE, 
                                       UN_TABLAERROR  => MI_TABLA,
                                       UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_INSERT_DETERCARTERA,                                   
                                       UN_REEMPLAZOS  => MI_REEMPLAZOS);

       WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN  
            MI_REEMPLAZOS (0).CLAVE := 'CAMPO';
            MI_REEMPLAZOS (0).VALOR :=  MI_CAMPOS;
            MI_REEMPLAZOS (1).CLAVE := 'TABLA';
            MI_REEMPLAZOS (1).VALOR :=  MI_TABLA;

            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE, 
                                       UN_TABLAERROR  => MI_TABLA,
                                       UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_UPDATE_DETERCARTERA,                                   
                                       UN_REEMPLAZOS  => MI_REEMPLAZOS); 

         WHEN PCK_EXCEPCIONES.EXC_MERGE THEN  
            MI_REEMPLAZOS (0).CLAVE := 'CAMPO';
            MI_REEMPLAZOS (0).VALOR :=  MI_CAMPOS;
            MI_REEMPLAZOS (1).CLAVE := 'TABLA';
            MI_REEMPLAZOS (1).VALOR :=  MI_TABLA;

            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD     => SQLCODE, 
                                       UN_TABLAERROR  => MI_TABLA,
                                       UN_ERROR_COD   => PCK_ERRORES.ERR_CONTAB_UPDATE_DETERCARTERA,                                   
                                       UN_REEMPLAZOS  => MI_REEMPLAZOS);
  END;  
  RETURN MI_RESPUESTA;  
END FC_REVISARFACTURASCANCELADAS;

--7


FUNCTION FC_GENERAPACPPCIONALALGIRO_COM
/*
  NAME              : FC_GENERAPACPPCIONALALGIRO_COM nombre en access GenerarPACPRoporcionalAlGiro_COM
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
  DATE MIGRADOR     : 31/08/2016          
  TIME              : 12:04 PM
  SOURCE MODULE     : Contabilidad
  MODIFIER          : JOSE PASCUAL GÓMEZ
  DATE MODIFIED     : 20/04/2017 
  TIME              : 
  DESCRIPTION       : Realiza el proceso de generar PAC programado. El numero 9 agreagdo a los mensajes se utiliza
                      desde el bean para saber si se ejecutara una alerta u 8 si es informativo.
                    : Se ajusta para que el retorno sea logico y los mensajes de error se vayan por control de erorres  
  MODIFIER          : SERGIO ESTEBAN PIÑA VARGAS
  DATE MODIFIED     : 17/08/2017 
  TIME              : 
  DESCRIPTION       : Se agrega el parametro UN_USUARIO para campos de auditoria

  @NAME:  GenerarPacProgramado
  @METHOD:  GET                      
*/
(
    UN_COMPANIA          IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO               IN  PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO              IN  VARCHAR2,
    UN_NUMERO            IN  PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_FECHACOMPROBANTE  IN  DATE,
    UN_USUARIO           IN  PCK_SUBTIPOS.TI_USUARIO)
  RETURN PCK_SUBTIPOS.TI_LOGICO
AS
    MI_DBLBASEPPTAL      NUMBER;
    MI_CUENTA            VARCHAR2(50);
    MI_DBLTOTAL          NUMBER;
    MI_DBLPROPORCION     NUMBER;
    MI_DBLTOTALGIROPPTAL NUMBER;
    MI_DBLSUMAPROPORCION NUMBER;
    MI_INTCONSECUTIVO    NUMBER;
    MI_INTCONSECUTIVOANT NUMBER;
    MI_DATFECHA          DATE;
    MI_TABLA             PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES           PCK_SUBTIPOS.TI_VALORES;
    MI_PORC              NUMBER;
    MI_ERROR_FUN         NUMBER:=GL_ERROR_NUM + 7;
    MI_RESPUESTA         VARCHAR2(500);
    INTI                 NUMBER;
    MI_EXISTE            NUMBER;
BEGIN
  --'Aquí revisa si se creó el header del comprobante pptal
  BEGIN
    SELECT 
      COUNT(COMPANIA)
    INTO MI_EXISTE
    FROM COMPROBANTE_PPTAL
    WHERE COMPANIA = UN_COMPANIA
      AND ANO      = UN_ANO
      AND TIPO     = UN_TIPO
      AND NUMERO   = UN_NUMERO;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_EXISTE:=0;
  END;
  --'Si está creado avisa que existe y termina
  IF MI_EXISTE <= 0 THEN
      BEGIN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                       ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_PAC_EXISTE_PPTAL
                                       ,UN_TABLAERROR => 'COMPROBANTE_PPTAL');              
      END;
    -- GoTo Fin
    --'Si existe pasa a revisar si tiene detalles pptales creados
  ELSE
    --'Aquí se revisa si la imputación pptal relacionada está creada
    BEGIN 
    SELECT 
      COUNT(*) EXISTE
    INTO MI_EXISTE
    FROM DETALLE_COMPROBANTE_PPTAL
    WHERE COMPANIA    = UN_COMPANIA
      AND ANO         = UN_ANO
      AND TIPO_CPTE   = UN_TIPO
      AND COMPROBANTE = UN_NUMERO
    GROUP BY COMPANIA,
      ANO,
      TIPO_CPTE,
      COMPROBANTE;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_EXISTE:=0;
    END;
    --SET rspptal = db.OpenRecordset(strSql)
    --'Si no está creado avisa que debe crearlo y termina
    IF MI_EXISTE <= 0 THEN
      BEGIN
        BEGIN
          RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                       ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_GENCPTEPPTALVARIOS2
                                       ,UN_TABLAERROR => 'DETALLE_COMPROBANTE_PPTAL');              
      END;
      --GoTo Fin
    ELSE
      --' 2006/09/06 el valor total sobre el que se calcula la proporcionalidad corresponde al gasto,
      --' se  tenia como el valor a girar pero para el caso de los aportes del empleado en seguridad social, no afecta presupuestalmente.
      --' por lo tanto, se debe calcular con las  smatoria de las cuentas de gasto de los cOM afectados
      BEGIN
        SELECT 
          SUM(VALOR_DEBITO-VALOR_CREDITO) VLRGIROPPTAL
        INTO MI_DBLTOTALGIROPPTAL
        FROM DETALLE_COMPROBANTE_CNT
         LEFT JOIN V_PLAN_CONTABLE PLAN_CONTABLE
          ON DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA
          AND DETALLE_COMPROBANTE_CNT.ANO     = PLAN_CONTABLE.ANO
          AND DETALLE_COMPROBANTE_CNT.ID      = PLAN_CONTABLE.ID
         LEFT JOIN COMPROBANTE_CNT
          ON DETALLE_COMPROBANTE_CNT.COMPANIA     = COMPROBANTE_CNT.COMPANIA
          AND DETALLE_COMPROBANTE_CNT.ANO         = COMPROBANTE_CNT.ANO
          AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   = COMPROBANTE_CNT.TIPO
          AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = COMPROBANTE_CNT.NUMERO
        WHERE DETALLE_COMPROBANTE_CNT.COMPANIA               = UN_COMPANIA
          AND DETALLE_COMPROBANTE_CNT.ANO                    = UN_ANO
          AND DETALLE_COMPROBANTE_CNT.ANO||TIPO_CPTE||NUMERO IN (SELECT ANO_AFECT||TIPO_CPTE_AFECT||COMPROBANTE_AFECT
                                                                   FROM COMPROBANTE_CNTAFECTADOS
                                                                  WHERE COMPANIA    = UN_COMPANIA
                                                                    AND ANO         = UN_ANO
                                                                    AND TIPO_CPTE   = UN_TIPO
                                                                    AND COMPROBANTE = UN_NUMERO)
          AND PLAN_CONTABLE.CLASECUENTA='G'
        GROUP BY 
          DETALLE_COMPROBANTE_CNT.COMPANIA,
          DETALLE_COMPROBANTE_CNT.ANO;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_DBLTOTALGIROPPTAL:= 0;
      END;
      --'Si está creado trae la sumatoria de las cuentas pptales para hacer la proporcionalidad
      BEGIN
        SELECT 
          SUM(VALOR_DEBITO) TOTALDEBITO
        INTO MI_DBLTOTAL
        FROM DETALLE_COMPROBANTE_PPTAL
        WHERE COMPANIA  = UN_COMPANIA
        AND ANO         = UN_ANO
        AND TIPO_CPTE   = UN_TIPO
        AND COMPROBANTE = UN_NUMERO
        GROUP BY 
          COMPANIA,
          ANO,
          TIPO_CPTE,
          COMPROBANTE
        ORDER BY 
          COMPANIA,
          ANO,
          TIPO_CPTE,
          COMPROBANTE ;
        EXCEPTION WHEN NO_DATA_FOUND THEN
          MI_DBLTOTAL:= 0;
      END;
    END IF;
    --'Aquí recorre cada cuenta pptal para calcular la proporcionalidad de cada una
    --'y crear cada ejecución de pac respectiva
    MI_DBLPROPORCION    := 0;
    MI_DBLSUMAPROPORCION:= 0;
    MI_INTCONSECUTIVO   := 0;
    MI_INTCONSECUTIVOANT:= 0;
    --'Aquí recorre los ítems pptales para calcular la proporcionalidad
    --'e insertar la respectiva ejecución de pac
    FOR RS IN(SELECT 
               COMPANIA, 
               ANO, 
               TIPO_CPTE, 
               COMPROBANTE, 
               CONSECUTIVO, 
               CUENTA, 
               FECHA, 
               VALOR_DEBITO, 
               LEAD(DETALLE_COMPROBANTE_PPTAL.COMPANIA) OVER (ORDER BY DETALLE_COMPROBANTE_PPTAL.COMPANIA) DESPUES
              FROM DETALLE_COMPROBANTE_PPTAL 
              WHERE COMPANIA = UN_COMPANIA
                AND ANO        =UN_ANO
                AND TIPO_CPTE  =UN_TIPO
                AND COMPROBANTE=UN_NUMERO
              ORDER BY COMPANIA,
                       ANO,
                       TIPO_CPTE,
                       COMPROBANTE)
    LOOP
      MI_INTCONSECUTIVO := MI_INTCONSECUTIVO + 1;
      MI_DATFECHA       := RS.FECHA;
      IF MI_DBLTOTAL     = 0 THEN
        MI_DBLPROPORCION:= 0;
      ELSE
        MI_PORC         := MI_DBLTOTALGIROPPTAL / MI_DBLTOTAL;
        MI_DBLBASEPPTAL := NVL(RS.VALOR_DEBITO, 0);
        MI_DBLPROPORCION:= PCK_SYSMAN_UTL.FC_ROUND(MI_PORC * MI_DBLBASEPPTAL, 2);
      END IF;
      MI_DBLSUMAPROPORCION:= MI_DBLSUMAPROPORCION + MI_DBLPROPORCION;
      MI_CUENTA           := RS.CUENTA;
      MI_INTCONSECUTIVOANT:= RS.CONSECUTIVO;
      -- rspptal.MoveNext
      --'Acá se asegura que los totales cuadren con el valor a girar
      IF RS.DESPUES             IS NULL THEN
        IF MI_DBLSUMAPROPORCION <> MI_DBLTOTALGIROPPTAL THEN
          MI_DBLPROPORCION      := PCK_SYSMAN_UTL.FC_ROUND(MI_DBLTOTALGIROPPTAL - (MI_DBLSUMAPROPORCION - MI_DBLPROPORCION), 2);
        END IF;
      END IF;
      --'Aquí se inserta la respectiva ejecución de pac
      MI_TABLA  :='PACEJECUTADO';
      MI_CAMPOS :='COMPANIA, '||
                  'ANO, '||
                  'TIPO_CPTE, '||
                  'COMPROBANTE, '||
                  'CONSECUTIVO, '||
                  'CUENTA, '||
                  'FECHA, '||
                  'MOV_DEBITO, '||
                  'MOV_CREDITO,
                  CREATED_BY,
                  DATE_CREATED
                  '
                  /*||'TIPO_ORIGEN,'|| --Estos campos no existen en el nuevo modelo.
                  'COMPROBANTE_ORIGEN,'||
                  'CUENTA_CNT,'||
                  'PORC_PPTAL,'||
                  'VALOR_BASE_PPTAL'*/
                  ;
      MI_VALORES:=''''||UN_COMPANIA||''','
                      ||UN_ANO||','''
                      ||UN_TIPO||''','''
                      ||UN_NUMERO||''','
                      ||MI_INTCONSECUTIVOANT||','''
                      ||MI_CUENTA||''',TO_DATE('''
                      ||MI_DATFECHA||''', ''DD/MM/YYYY''),'
                      ||MI_DBLPROPORCION
                      ||',0,
                      '''|| UN_USUARIO ||''',
                      SYSDATE
                      '
                      /*||','''||UN_TIPO||''',''' --Estos campos no existen en el nuevo modelo.
                      ||UN_NUMERO
                      ||''',NULL,'
                      ||MI_PORC
                      ||','||MI_DBLBASEPPTAL||''*/
                      ;
      PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                           UN_ACCION  => 'I', 
                                           UN_CAMPOS  => MI_CAMPOS, 
                                           UN_VALORES => MI_VALORES);
      --strClaseMov = cnsPACEjecutado
      --xx = ActPto0(strClaseMov, StrCompania, intAno, Month(MI_DATFECHA), MI_CUENTA, 0, 0, 0, MI_DBLPROPORCION, 0, MI_DBLPROPORCION, "C")
    END LOOP;
    RETURN -1;
  END IF;
/*
EXCEPTION  WHEN OTHERS THEN
  PCK_DATOS.GL_ERROR_MSG:= 'Error al realizar el proceso.';
  MI_RESPUESTA := SQLERRM;
  PCK_DATOS.GL_ERROR_MSG:= PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'FC_GENERAPACPPIONALALGIRO_COM','',SQLERRM );
  RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );*/


END FC_GENERAPACPPCIONALALGIRO_COM;


--8

FUNCTION FC_GENERARPACPPCIONALALGIRO
/*
  NAME              : FC_GENERAPACPPCIONALALGIRO_COM nombre en access GenerarPACProporcionalAlGiro
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
  DATE MIGRADOR     : 31/08/2016
  TIME              : 2:00 PM
  SOURCE MODULE     : Contabilidad
  MODIFIER          : JOSE PASCUAL GOMEZ
  DATE MODIFIED     : 20/04/2017
  TIME              : 
  DESCRIPTION       : Realiza el proceso de generar PAC proporcional al giro.El numero 9 agreagdo a los mensajes se utliza
                      desde el bean para saber si se ejecutara una alerta u 2 si es necesario abrir un formulario, los párametros para abrir el formulario van entre numerales.
                    : Se ajusta para que el retorno sea logico y los mensajes de error se vayan por control de erorres   
  MODIFIER          : SERGIO ESTEBAN PIÑA VARGAS
  DATE MODIFIED     : 17/08/2017
  TIME              : 
  DESCRIPTION       : Se agrega el parametro UN_USUARIO para campos de auditoria 
  @NAME:  generarPacProporcionalAlGiro
  @METHOD:  GET                      
*/
(
    UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIO                IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO                IN VARCHAR2,
    UN_NUMERO              IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_MES                 IN PCK_SUBTIPOS.TI_MES, 
    UN_DATFECHACOMPROBANTE IN DATE,
    UN_DBLTOTALGIRO        IN NUMBER,
    UN_USUARIO             IN PCK_SUBTIPOS.TI_USUARIO)
  RETURN PCK_SUBTIPOS.TI_LOGICO
AS
    MI_EXISTE    NUMBER;
    MI_ERROR_FUN NUMBER:=GL_ERROR_NUM + 8;
    MI_RESPUESTA VARCHAR2(500);
    MI_MSGERROR      PCK_SUBTIPOS.TI_CLAVEVALOR;  
BEGIN
  --'Se revisa si ya se calculó el pac ejecutado
  BEGIN
    SELECT 
      COUNT(*) EXISTE
    INTO MI_EXISTE
    FROM PACEJECUTADO
    WHERE COMPANIA    = UN_COMPANIA
      AND ANO         = UN_ANIO
      AND TIPO_CPTE   = UN_TIPO
      AND COMPROBANTE = UN_NUMERO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_EXISTE:=0;
  END;
  IF MI_EXISTE > 0 THEN
    BEGIN
      BEGIN
        RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
          PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                     ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_PAC_YA_EJECUTADO
                                     ,UN_TABLAERROR => 'PACEJECUTADO');              
    END;
    --GoTo Fin
  END IF;
  --'Se revisa si el comprobante tiene com relacionados
  BEGIN
    SELECT COUNT(*) CONTEO
    INTO MI_EXISTE
    FROM DETALLE_COMPROBANTE_CNT
    WHERE COMPANIA                 =UN_COMPANIA
    AND ANO                        =UN_ANIO
    AND TIPO_CPTE                  =UN_TIPO
    AND COMPROBANTE                =UN_NUMERO
    AND TIPO_CPTE_AFECT           IS NOT NULL
    AND NVL(CMPTE_AFECTADO,0) NOT IN (0) ;
  EXCEPTION
  WHEN NO_DATA_FOUND THEN
    MI_EXISTE:=0;
  END;

  --'Si tiene com relacionados se calcula la proporcionalidad con base en las cuentas del egreso presupuestal y ya
  IF MI_EXISTE>0 THEN
   RETURN PCK_CONTABILIDAD4.FC_GENERAPACPPCIONALALGIRO_COM(UN_COMPANIA, 
                                                          UN_ANIO, 
                                                          UN_TIPO, 
                                                          UN_NUMERO, 
                                                          UN_DATFECHACOMPROBANTE,
                                                          UN_USUARIO);
  ELSE
    --'Si no tiene com relacionados se revisan los com de un periodo dado y con base en ellos se averiguan
    --'los com presupuestales para seleccionar las cuentas pptales y sacar la proporcionalidad
    --'Aquí se revisa si el comprobante pptal relacionado no está creado
    --'Si está creado no sigue el proceso
    BEGIN
      SELECT 
        COUNT(*) MI_EXISTE
      INTO MI_EXISTE
      FROM COMPROBANTE_PPTAL
      WHERE COMPANIA  = UN_COMPANIA
        AND ANO       = UN_ANIO
        AND TIPO      = UN_TIPO
        AND NUMERO    = UN_NUMERO;
    EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_EXISTE:=0;
    END;
    --'Si está creado avisa que existe y termina
    IF MI_EXISTE > 0 THEN
        BEGIN
          BEGIN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
              PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE
                                         ,UN_ERROR_COD  => PCK_ERRORES.ERR_CONTAB_PAC_PRESUPUESTO
                                         ,UN_TABLAERROR => 'COMPROBANTE_PPTAL');              
        END;      
      ELSE
        -- GoTo Fin
        --'Aquí seleccionan las cuentas contrapartidas de bancos y las guarda en un vector
        --'para buscarlas en las cuentas por pagar de un periodo dado
        /* EL VECTOR DE CONTRAPARTIDAS SE CAMBIO CON UNA CONSULTA---
        Wend*/
        --DoCmd.OpenForm "PedirFechasPACProporcional"
        --Forms!PedirFechasPACProporcional.Tag = dblTotalGiro
        --Forms!PedirFechasPACProporcional!FechaInicial = "01/" & Format(GetMonth(), "00") & "/" & intAno
        --Forms!PedirFechasPACProporcional!FechaFinal = UltimoDia(datFechaComprobante)
        RETURN -1;
       /* RETURN '2#'||UN_DBLTOTALGIRO||
               '#01/'||UN_MES||'/'||UN_ANIO||
               '#'||TO_CHAR(LAST_DAY(UN_DATFECHACOMPROBANTE),'DD/MM/YYYY');
      */
    END IF;
  END IF;

END FC_GENERARPACPPCIONALALGIRO;

--9
FUNCTION FC_GENPACPPNALALGIRO_SINCOM
/*
  NAME              : FC_GENPACPPNALALGIRO_SINCOM nombre en access GENERARPACPROPORCIONALALGIRO_SINCOM
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
  DATE MIGRADOR     : 01/09/2016
  TIME              : 4:00 PM
  SOURCE MODULE     : Contabilidad
  MODIFIER          : 
  DATE MODIFIED     : 
  TIME              : 
  DESCRIPTION       : Realiza el proceso de generar PAC proporcional al giro sin COM.
  @NAME:  generarPacProporcionalAlGiroSinOrdenDePago
  @METHOD:  GET  
*/
(
    UN_COMPANIA         IN VARCHAR2,
    UN_ANIO             IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO             IN VARCHAR2,
    UN_NUMERO           IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_FECHACOMPROBANTE IN DATE,
    UN_TOTALGIRO        IN NUMBER,
    UN_FECHAINICIAL     IN DATE,
    UN_FECHAFINAL       IN DATE,
    UN_TERCERO          IN VARCHAR2,
    UN_SUCURSAL         IN VARCHAR2  )
  RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_INTCONSECUTIVO          NUMBER;
  DBLSUMAPROPORCION          NUMBER;
  MI_DBLVALORNOCUENTASXPAGAR NUMBER;
  MI_TOTALCAUSADO            NUMBER;
  MI_TABLAINSERT             VARCHAR2(20);
  MI_CAMPOSINSERT            VARCHAR2(1000);
  MI_VALORESINSERT           VARCHAR2(1000);
  MI_DESCRIPCION             VARCHAR2(1000);
  MI_INSERT                  NUMBER:=0;
  MI_DBLSUMAPROPORCION       NUMBER;
  MI_PORCPPTAL               NUMBER;
  MI_DBLBASEPAGO             NUMBER;
  MI_DBLBASEPPTO             NUMBER;
  MI_DBLPROPORCION           NUMBER;
  MI_PORCPAGO                NUMBER;
  MI_NUMRUBROS               NUMBER;
  MI_ERROR_FUN               NUMBER:=GL_ERROR_NUM + 9;
  MI_RESPUESTA               VARCHAR2(500);
  -- ReDim ComprobantesPAC(0)
  --'Aquí recorre el vector de contrapartidas para buscar las CXP en las que están en el periodo especificado
  --'Aquí recorre el vector de contrapartidas para buscar las CXP en las que están en el periodo especificado
BEGIN


  FOR RS IN (
           WITH CONTRAPARTIDAS AS(
           SELECT DISTINCT CUENTA       CUENTACONTRAPARTIDA,
                           VALOR_DEBITO VALOR
           FROM DETALLE_COMPROBANTE_CNT
           WHERE COMPANIA    = UN_COMPANIA
             AND ANO         = UN_ANIO
             AND TIPO_CPTE   = UN_TIPO
             AND COMPROBANTE = UN_NUMERO
             AND NVL(VALOR_DEBITO,0) NOT IN (0)
             AND (SELECT COUNT(*) NUM
                    FROM PACEJECUTADO
                   WHERE COMPANIA    = UN_COMPANIA
                     AND ANO         = UN_ANIO
                     AND TIPO_CPTE   = UN_TIPO
                     AND COMPROBANTE = UN_NUMERO )IN (0)
             AND (SELECT COUNT(*) CONTEO
                    FROM DETALLE_COMPROBANTE_CNT
                   WHERE COMPANIA        = UN_COMPANIA
                     AND ANO             = UN_ANIO
                     AND TIPO_CPTE       = UN_TIPO
                     AND COMPROBANTE     = UN_NUMERO
                     AND TIPO_CPTE_AFECT IS NOT NULL
                     AND CMPTE_AFECTADO  IS NOT NULL ) IN (0)
             AND (SELECT COUNT(*) MI_EXISTE
                    FROM COMPROBANTE_PPTAL
                   WHERE COMPANIA = UN_COMPANIA
                     AND ANO      = UN_ANIO
                     AND TIPO     = UN_TIPO
                     AND NUMERO   = UN_NUMERO ) IN (0))
            SELECT DISTINCT DETALLE_COMPROBANTE_CNT.COMPANIA,
                            DETALLE_COMPROBANTE_CNT.ANO,
                            DETALLE_COMPROBANTE_CNT.TIPO_CPTE,
                            DETALLE_COMPROBANTE_CNT.COMPROBANTE,
                            DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                            DETALLE_COMPROBANTE_CNT.FECHA,
                            DETALLE_COMPROBANTE_CNT.CUENTA,
                            TIPO_COMPROBANTE.CLASE_CONTABLE,
                            DETALLE_COMPROBANTE_CNT.VALOR_CREDITO,
                            CNTCXP.VLRCAUSADO-NVL(CUENTASPORPAGAR.VLRCAUSADO,0) VLRCAUSADO,
                            CASE WHEN NVL(CUENTASPORPAGAR.VLRCAUSADO,0)> 0 
                                 THEN PCK_SYSMAN_UTL.FC_ROUND(NVL(CONTRAPART.VALOR, 0)*
                                      (1-(NVL(CUENTASPORPAGAR.VLRCAUSADO,0)/NVL(CNTCXP.VLRCAUSADO, 0))),2)
                                 ELSE ROUND(NVL(CONTRAPART.VALOR, 0), 2)
                            END VLRGIRO,
                            CONTRAPART.VALOR
            FROM CONTRAPARTIDAS CONTRAPART, 
                 DETALLE_COMPROBANTE_CNT
                  LEFT JOIN (SELECT 
                               SUM(NVL(SUMDET.VALOR_CREDITO,0)) VLRCAUSADO,
                               SUMDET.CUENTA,
                               SUMDET.COMPANIA
                             FROM DETALLE_COMPROBANTE_CNT SUMDET
                               INNER JOIN TIPO_COMPROBANTE
                                ON SUMDET.COMPANIA    = TIPO_COMPROBANTE.COMPANIA
                                AND SUMDET.TIPO_CPTE  = TIPO_COMPROBANTE.CODIGO
                             WHERE SUMDET.COMPANIA                 = UN_COMPANIA
                               AND SUMDET.FECHA                    BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
                               AND TIPO_COMPROBANTE.CLASE_CONTABLE NOT IN ('E') 
                             GROUP BY 
                               SUMDET.CUENTA, 
                               SUMDET.COMPANIA) CNTCXP 
                    ON  DETALLE_COMPROBANTE_CNT.COMPANIA   = CNTCXP.COMPANIA
                    AND DETALLE_COMPROBANTE_CNT.CUENTA     = CNTCXP.CUENTA
                  LEFT JOIN (SELECT 
                               SUM(NVL(SUMDET.VALOR_CREDITO,0)) VLRCAUSADO,
                               SUMDET.CUENTA,
                               SUMDET.COMPANIA
                             FROM DETALLE_COMPROBANTE_CNT SUMDET
                               INNER JOIN TIPO_COMPROBANTE
                                ON SUMDET.COMPANIA    = TIPO_COMPROBANTE.COMPANIA
                                AND SUMDET.TIPO_CPTE  = TIPO_COMPROBANTE.CODIGO
                             WHERE SUMDET.COMPANIA                 = UN_COMPANIA
                               AND SUMDET.FECHA                    BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
                               AND TIPO_COMPROBANTE.CLASE_CONTABLE NOT IN ('P') 
                             GROUP BY 
                               SUMDET.CUENTA, 
                               SUMDET.COMPANIA) CUENTASPORPAGAR 
                    ON  DETALLE_COMPROBANTE_CNT.COMPANIA   = CUENTASPORPAGAR.COMPANIA
                    AND DETALLE_COMPROBANTE_CNT.CUENTA     = CUENTASPORPAGAR.CUENTA
                  INNER JOIN TIPO_COMPROBANTE 
                    ON  DETALLE_COMPROBANTE_CNT.COMPANIA  = TIPO_COMPROBANTE.COMPANIA
                    AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
            WHERE DETALLE_COMPROBANTE_CNT.COMPANIA = UN_COMPANIA
              AND DETALLE_COMPROBANTE_CNT.CUENTA   = CONTRAPART.CUENTACONTRAPARTIDA
              AND DETALLE_COMPROBANTE_CNT.FECHA    BETWEEN UN_FECHAINICIAL AND UN_FECHAFINAL
              AND TIPO_COMPROBANTE.CLASE_CONTABLE  IN ('P'))
  LOOP
      --'Antes de insertarlo en el vector de comprobantes de pac busca si ya lo tenía
    /*
    'Ahora recorre las cxp que están en el vector de comprobantespac
    'pero presupuestalmente. Y ahí ver las cuentas pptales con sus valores y generar la ejecución de pac
    'proporcionalmente
    'Para ello voy recorriendo los comprobantes pptales y voy insertandolos
    'en otro vector para guardar el rubro y su valor
    'Aquí se crea el comprobante prespuestal header y el detalle con valores en ceros
    'Para así poder crear pac ejecutado con los valores proporcionales
    'Si el arreglo con los comprobantes y valores tiene algo*/
   IF MI_INSERT =0 THEN 
      MI_DESCRIPCION   := 'PAC Ejecutado'; 
      MI_TABLAINSERT   := 'COMPROBANTE_PPTAL';
      MI_CAMPOSINSERT  := 'COMPANIA,ANO,TIPO,NUMERO,FECHA,FECHA_VCN_DOC,TERCERO,SUCURSAL,DESCRIPCION';
      MI_VALORESINSERT :=   ''''||UN_COMPANIA||''','
                                ||UN_ANIO||','''
                                ||UN_TIPO||''','''
                                ||UN_NUMERO||''',TO_DATE('''
                                ||UN_FECHACOMPROBANTE||''',''DD/MM/YYYY''),TO_DATE('''
                                ||UN_FECHACOMPROBANTE||''',''DD/MM/YYYY''),'''
                                ||UN_TERCERO||''','''
                                ||UN_SUCURSAL||''','''
                                ||MI_DESCRIPCION||'''';
      MI_INSERT:= PCK_DATOS.FC_ACME(MI_TABLAINSERT, 'I', MI_CAMPOSINSERT, MI_VALORESINSERT, NULL, NULL);  
   END IF;                           
   IF MI_INSERT > 0 THEN
        --'Si insertó el encabezado del comprobante pptal
        --'sigue con los detalles pptales y sus respectivos PAC Ejecutados proporcionales
        --FOR intI                       = 0 TO UBound(ComprobantesPAC) - 1
    MI_DBLSUMAPROPORCION:= 0;
    FOR RS2 IN (SELECT 
                    DETALLE_COMPROBANTE_PPTAL.COMPANIA,
                    DETALLE_COMPROBANTE_PPTAL.ANO,
                    TIPO_CPTE,
                    COMPROBANTE,
                    CONSECUTIVO,
                    DETALLE_COMPROBANTE_PPTAL.CUENTA,
                    VALOR_DEBITO,
                    COMPROBANTE_PPTAL.VLR_DOCUMENTO,
                    LEAD(DETALLE_COMPROBANTE_PPTAL.COMPANIA) OVER (ORDER BY DETALLE_COMPROBANTE_PPTAL.COMPANIA) DESPUES
                 FROM DETALLE_COMPROBANTE_PPTAL
                  LEFT JOIN COMPROBANTE_PPTAL
                    ON  DETALLE_COMPROBANTE_PPTAL.COMPANIA     = COMPROBANTE_PPTAL.COMPANIA
                    AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE   = COMPROBANTE_PPTAL.TIPO
                    AND DETALLE_COMPROBANTE_PPTAL.ANO         = COMPROBANTE_PPTAL.ANO
                    AND DETALLE_COMPROBANTE_PPTAL.COMPROBANTE = COMPROBANTE_PPTAL.NUMERO
                 WHERE DETALLE_COMPROBANTE_PPTAL.COMPANIA  = RS.COMPANIA
                   AND DETALLE_COMPROBANTE_PPTAL.ANO         = RS.ANO 
                   AND DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE   = RS.TIPO_CPTE
                   AND DETALLE_COMPROBANTE_PPTAL.COMPROBANTE = RS.COMPROBANTE 
                 ORDER BY 
                    DETALLE_COMPROBANTE_PPTAL.COMPANIA, 
                    DETALLE_COMPROBANTE_PPTAL.ANO,
                    DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE,
                    DETALLE_COMPROBANTE_PPTAL.COMPROBANTE,
                    DETALLE_COMPROBANTE_PPTAL.CUENTA,
                    VALOR_DEBITO)
    LOOP 
        --'Aquí inserta la cta pptal en detalle_pptal con valor cero
        --'para poder insertar su respectivo pac ejecutado
     MI_INSERT:=0;
     MI_TABLAINSERT  := 'DETALLE_COMPROBANTE_PPTAL';
     MI_CAMPOSINSERT := 'COMPANIA,ANO,TIPO_CPTE,COMPROBANTE,CONSECUTIVO,CUENTA,FECHA,TERCERO,SUCURSAL,DESCRIPCION,VALOR_DEBITO,VALOR_CREDITO,CENTRO_COSTO,AUXILIAR';
     MI_VALORESINSERT:= ''''||UN_COMPANIA||''',' 
                            ||UN_ANIO||',''' 
                            ||UN_TIPO||''',''' 
                            ||UN_NUMERO||''',' 
                            ||MI_INTCONSECUTIVO||',''' 
                            ||RS2.CUENTA||''', TO_DATE(''' 
                            ||UN_FECHACOMPROBANTE||''',''DD/MM/YYYY'') ,''' 
                            ||UN_TERCERO||''',''' 
                            ||UN_SUCURSAL||''',''' 
                            ||MI_DESCRIPCION||''',0,0,'
                            ||PCK_DATOS.FC_CONS_CENTRO||','
                            ||PCK_DATOS.FC_CONS_AUXILIAR||'''';
     MI_INSERT:= PCK_DATOS.FC_ACME(MI_TABLAINSERT, 'I', MI_CAMPOSINSERT, MI_VALORESINSERT, NULL, NULL);   
        --'Si inserta el detalle pptal inserta el pac ejecutado
     IF MI_INSERT > 0 THEN
          --'proporcionalidad presupuestal
          --' calcula el procentaje pptal
      MI_PORCPPTAL:= CASE 
                      WHEN NVL(RS2.VLR_DOCUMENTO, 0) IN (0)
                      THEN 0
                      ELSE NVL(RS2.VALOR_DEBITO, 0)/NVL(RS2.VLR_DOCUMENTO, 0)
                     END; 
      MI_DBLBASEPAGO:= RS.VALOR;
      MI_DBLBASEPPTO:= PCK_SYSMAN_UTL.FC_ROUND(MI_DBLBASEPAGO * MI_PORCPPTAL, 2);
      --' proporcionalidad contable 
      --' a la proporcionalidad anterior. le calcula el valor correspondiente
      --' el porcentaje de pago
      MI_DBLPROPORCION:= PCK_SYSMAN_UTL.FC_ROUND(MI_DBLBASEPPTO * MI_PORCPAGO, 2);
      MI_DBLSUMAPROPORCION:= MI_DBLSUMAPROPORCION + MI_DBLPROPORCION;
      --'Acá se asegura que los totales cuadren con el valor a girar  
      IF RS2.DESPUES IS NULL THEN
       IF DBLSUMAPROPORCION <> NVL(RS.VLRGIRO, 0) THEN  
        IF RS.VLRGIRO <= MI_DBLSUMAPROPORCION THEN
         MI_DBLPROPORCION:= PCK_SYSMAN_UTL.FC_ROUND(NVL(RS.VLRGIRO, 0) - (MI_DBLSUMAPROPORCION - MI_DBLPROPORCION), 2);
        END IF; 
       END IF;
      END IF;
      --'Aquí se inserta la respectiva ejecución de pac
      MI_TABLAINSERT  :='PACEJECUTADO';
      MI_CAMPOSINSERT :='COMPANIA, ANO, TIPO_CPTE, COMPROBANTE, CONSECUTIVO, CUENTA, FECHA, MOV_DEBITO, MOV_CREDITO,TIPO_ORIGEN,COMPROBANTE_ORIGEN,CONSECUTIVO_ORIGEN,CUENTA_CNT,PORC_PPTAL,PORC_PAGO,VALOR_BASE_PPTAL,VALOR_BASE_PAGO';
      MI_VALORESINSERT:=''''||UN_COMPANIA||''','
                            ||UN_ANIO||','''
                            ||UN_TIPO||''','''
                            ||UN_NUMERO||''','
                            ||MI_INTCONSECUTIVO||','''
                            ||RS2.CUENTA||''',TO_DATE('''
                            ||UN_FECHACOMPROBANTE||''',''DD/MM/YYYY''),'
                            ||MI_DBLPROPORCION||',0,'''
                            ||RS.TIPO_CPTE||''','''
                            ||RS.COMPROBANTE||''','
                            ||RS.CONSECUTIVO||','''
                            ||RS.CUENTA||''','
                            ||MI_PORCPPTAL||','
                            ||MI_PORCPAGO||','
                            ||MI_DBLBASEPPTO||','
                            ||MI_DBLBASEPAGO||'''';
      MI_INSERT:= PCK_DATOS.FC_ACME(MI_TABLAINSERT, 'I', MI_CAMPOSINSERT, MI_VALORESINSERT, NULL, NULL);   
           -- strClaseMov = cnsPACEjecutado          
           --bolXX = ActPto0(strClaseMov, StrCompania, intAno, Month(datFechaComprobante), rs!CUENTA, 0, 0, 0, dblProporcion, 0, dblProporcion, "C")       
      END IF;
    END LOOP;      
  MI_DESCRIPCION:= 'PAC Ejecutado';        
       --   DoCmd.Close acForm, "PedirFechasPACProporcional"   
  END IF;
 END LOOP;
  RETURN -1;
  --RETURN '9Ejecución de PAC realizada exitosamente!';    
  EXCEPTION  WHEN OTHERS THEN
  PCK_DATOS.GL_ERROR_MSG:= 'Error al realizar el proceso.';
  MI_RESPUESTA := SQLERRM;
  PCK_DATOS.GL_ERROR_MSG:= PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, PCK_DATOS.GL_ERROR_MSG,  'FC_GENPACPPNALALGIRO_SINCOM','',SQLERRM );
  RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END FC_GENPACPPNALALGIRO_SINCOM;



--10
FUNCTION FC_GENERARPACTESORERIA
/*
  NAME              : FC_GENERARPACTESORERIA nombre en Access GenerarPacTesoreria
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
  DATE MIGRADOR     : 06/09/2016
  TIME              : 8:50 AM
  SOURCE MODULE     : Contabilidad
  MODIFIER          : SERGIO ESTEBAN PIÑA VARGAS
  DATE MODIFIED     : 17/08/2017
  DESCRIPTION       : Se agrega parametro UN_USUARIO y campos auditoria
  TIME              : 
  DESCRIPTION       : Realiza el proceso de generar PAC proporcional al giro sin COM.
  @NAME:  generarPacTesoreria
  @METHOD:  GET
*/
(
   UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA, 
   UN_ANIO     IN PCK_SUBTIPOS.TI_ANIO,
   UN_TIPO     IN VARCHAR2, 
   UN_NUMERO   IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
   UN_FECHA    IN DATE,
   UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO
   ) RETURN VARCHAR2
AS 
    MI_EXISTE        NUMBER;
    MI_RTA           VARCHAR2(500);
    MI_TABLA         PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS        PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES       PCK_SUBTIPOS.TI_VALORES;
    MI_ERROR_FUN     NUMBER:=GL_ERROR_NUM + 10;
BEGIN
 BEGIN 
     SELECT COUNT(*) EXISTE
     INTO MI_EXISTE
     FROM DETALLE_COMPROBANTE_PPTAL 
     WHERE COMPANIA    = UN_COMPANIA 
       AND ANO         = UN_ANIO 
       AND TIPO_CPTE   = UN_TIPO 
       AND COMPROBANTE = UN_NUMERO 
     ORDER BY COMPANIA, 
              ANO, 
              TIPO_CPTE, 
              COMPROBANTE, 
              CONSECUTIVO;
     EXCEPTION WHEN NO_DATA_FOUND THEN 
      MI_EXISTE:=0;
 END;
 IF MI_EXISTE = 0 THEN
      MI_RTA:='El comprobante presupuestal no está creado.'; 
 ELSIF MI_EXISTE>0 THEN 
   FOR RS IN (SELECT CONSECUTIVO, 
                     (VALOR_DEBITO-VALOR_CREDITO) TOTAL,
                     CUENTA
              FROM DETALLE_COMPROBANTE_PPTAL 
              WHERE COMPANIA    = UN_COMPANIA 
                AND ANO         = UN_ANIO 
                AND TIPO_CPTE   = UN_TIPO 
                AND COMPROBANTE = UN_NUMERO 
              ORDER BY COMPANIA, 
                       ANO, 
                       TIPO_CPTE, 
                       COMPROBANTE, 
                       CONSECUTIVO)
   LOOP
      MI_TABLA      := 'PACTESORERIA' ;
      MI_CAMPOS     := 'COMPANIA,
                        ANO,
                        TIPO,
                        NUMERO,
                        CONSECUTIVO,
                        CUENTA,
                        FECHA,
                        VALOR_CREDITO,
                        VALOR_DEBITO,
                        CREATED_BY,
                        DATE_CREATED';
      MI_VALORES    := ''''||UN_COMPANIA||''','
                             ||UN_ANIO||','''
                             ||UN_TIPO||''','''
                             ||UN_NUMERO||''','
                             ||RS.CONSECUTIVO||','''
                             ||RS.CUENTA||''',TO_DATE('''
                             ||UN_FECHA||''',''DD/MM/YYYY''),0,'
                             ||NVL(RS.TOTAL,0)||',
                             '''|| UN_USUARIO ||''',
                             SYSDATE
                             ';
      PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA, 
                                           UN_ACCION  => 'I', 
                                           UN_CAMPOS  => MI_CAMPOS, 
                                           UN_VALORES => MI_VALORES);   
      /*If db.RecordsAffected > 0 Then
         bolControl = ActPto0("EJE", StrCompania, intAno, Month(dtmFecha), rs!CUENTA, 0, 0, 0, dblNeto, 0, dblNeto)
      End If*/
   END LOOP;
   MI_RTA:='Proceso terminado.';
  END IF;
   RETURN MI_RTA;
   EXCEPTION  WHEN OTHERS THEN
  PCK_DATOS.GL_ERROR_MSG:= 'Error al realizar el proceso.';
  MI_RTA := SQLERRM;
  PCK_DATOS.GL_ERROR_MSG:= PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, 
                                                           PCK_DATOS.GL_ERROR_MSG,  
                                                           'FC_GENERARPACTESORERIA',
                                                           '',
                                                           SQLERRM );
  RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );

END FC_GENERARPACTESORERIA;
--11
FUNCTION FC_GENERARPACINGRESOS
/*
  NAME              : FC_GENERARPACINGRESOS nombre en access GenerarPACIngresos
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRACION  : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE MIGRADOR     : 31/08/2016
  TIME              : 12:10 PM
  SOURCE MODULE     : Contabilidad
  MODIFIER          : SANDRA MILENA DAZA LEGUIZAMÓN
  DATE MODIFIED     : 07/09/2016
  TIME              : 08:45 a.m
  DESCRIPTION       : Verifica si existe el PAC de ingresos, de no ser así lo crea. 
  MODIFIER          : SERGIO ESTEBAN PIÑA VARGAS
  DATE MODIFIED     : 17/08/2017
  DESCRIPTION       : Agrego parametro UN_USUARIO y campos de auditoria
  @NAME:  crearPacDeIngresos
  @METHOD:  GET
*/
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO         IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPO        IN VARCHAR2,
    UN_NUMERO      IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_FECHACPTE   IN DATE,
    UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
  )
RETURN VARCHAR2
AS
  MI_ERROR_FUN          NUMBER:=GL_ERROR_NUM + 11;
  MI_EXISTE             VARCHAR2(1) := 'N';
  MI_MSG                VARCHAR2(32000);
  MI_CSC                NUMBER := 0;
  MI_PROPORCION         NUMBER:=0;
  MI_SUMAPROPORCION     NUMBER:=0;
  MI_CAMPOS             VARCHAR2(32000);
  MI_VALORES            VARCHAR2(32000);
  MI_RTA                VARCHAR2(32000);
BEGIN
  BEGIN
    SELECT  DISTINCT 'X'
    INTO    MI_EXISTE
    FROM    PACEJECUTADO
    WHERE   COMPANIA    = UN_COMPANIA
      AND   ANO         = UN_ANO
      AND   TIPO_CPTE   = UN_TIPO
      AND   COMPROBANTE = UN_NUMERO;

    EXCEPTION WHEN NO_DATA_FOUND  THEN
      MI_EXISTE := 'N';
  END ;
  --Verifica si ya existe el PAC para finalizar la función
  IF MI_EXISTE NOT IN ('N') THEN
    MI_MSG := 'El PAC ejecutado ya fue calculado. Por favor verificar e intentar nuevamente';
    RETURN MI_MSG;
  END IF;
  -- Se revisa si se creó el header del comprobante pptal
  BEGIN
    SELECT  DISTINCT 'X'
    INTO    MI_EXISTE
    FROM    COMPROBANTE_PPTAL
    WHERE   COMPANIA    = UN_COMPANIA
      AND   ANO         = UN_ANO
      AND   TIPO        = UN_TIPO
      AND   NUMERO      = UN_NUMERO;

     EXCEPTION WHEN NO_DATA_FOUND THEN
      MI_EXISTE := 'N';
  END;

  IF MI_EXISTE = 'N' THEN
    MI_MSG := 'El comprobante presupuestal no existe. Primero se debe generar el comprobante presupuestal asociado. Verificar el proceso para continuar';
    RETURN MI_MSG;
  ELSE  -- Se debe verificar los detalles presupuestales
    MI_EXISTE := 'N';
    FOR RSDETPPTAL IN ( 
            SELECT  COMPANIA, 
                    ANO, 
                    TIPO_CPTE, 
                    COMPROBANTE, 
                    CONSECUTIVO, 
                    CUENTA, 
                    FECHA, 
                    VALOR_CREDITO
            FROM    DETALLE_COMPROBANTE_PPTAL
            WHERE   COMPANIA    = UN_COMPANIA
              AND   ANO         = UN_ANO
              AND   TIPO_CPTE   = UN_TIPO
              AND   COMPROBANTE = UN_NUMERO
              )
        LOOP
          MI_EXISTE := 'S';          
          MI_CSC := MI_CSC + 1;
          MI_PROPORCION := PCK_SYSMAN_UTL.FC_ROUND(RSDETPPTAL.VALOR_CREDITO,2);
          MI_SUMAPROPORCION := MI_SUMAPROPORCION + MI_PROPORCION;
          --- Crear los registro del PAC ejecutado
          MI_CAMPOS:='COMPANIA, 
                      ANO, 
                      TIPO_CPTE, 
                      COMPROBANTE, 
                      CONSECUTIVO,
                      CUENTA,
                      FECHA, 
                      MOV_DEBITO,
                      MOV_CREDITO,
                      CREATED_BY,
                      DATE_CREATED
                      ';
          MI_VALORES := ''''||UN_COMPANIA||''',
                        '||UN_ANO||',
                        '''||UN_TIPO||''',
                        '|| UN_NUMERO ||',
                        '|| RSDETPPTAL.CONSECUTIVO ||',
                        '''|| RSDETPPTAL.CUENTA ||''',
                        ''' ||RSDETPPTAL.FECHA ||''',
                        0,
                        ' || MI_PROPORCION || ',
                        '''|| UN_USUARIO ||''',
                        SYSDATE
                        ';                    
          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA   => 'PACEJECUTADO', 
                                       UN_ACCION  => 'I', 
                                       UN_CAMPOS  => MI_CAMPOS, 
                                       UN_VALORES => MI_VALORES);
          -- En la función de access después de insertar se usaba la función ActPto0 la cual se omite porque se usará desde un trigger
    END LOOP;

    IF MI_EXISTE = 'N' THEN
      MI_MSG := 'El comprobante está creado pero no contiene imputación presupuestal. Por favor verificar';
      RETURN MI_MSG;
    ELSE  
      MI_MSG := 'Proceso ejecutado';
    END IF;
  END IF;
  RETURN MI_MSG;

  EXCEPTION  WHEN OTHERS THEN
    PCK_DATOS.GL_ERROR_MSG:= 'Se detuvo el proceso de Generar Pac Ingresos';
    MI_RTA := SQLERRM;
    PCK_DATOS.GL_ERROR_MSG:= PCK_SYSMAN_UTL.FC_EVALUAR_ERROR(MI_ERROR_FUN, 
                                                            PCK_DATOS.GL_ERROR_MSG,  
                                                            'PACEJECUTADO',
                                                            '',
                                                            SQLERRM );
    RAISE_APPLICATION_ERROR (-20000, PCK_DATOS.GL_ERROR_MSG || CHR(10) || PCK_DATOS.GL_ERROR_MSG );
END FC_GENERARPACINGRESOS;

PROCEDURE PR_RETENCIONBANCO
  (

  /*
    NAME              : PR_RETENCIONBANCO
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 23/11/2016
    TIME              : 12:16 PM
    MODIFIER          : JAVIER ANDRES RODRIGUEZ RIOS 
    DATE MODIFIED     : 26/05/2017
    TIME              : 
    DESCRIPTION       : Actualiza los campos BANCO_RETENCION , FECHA_PAGO_RETENCION , PAGO_RETENCION de detalle_comprobante_cnt
    MODIFICATIONS     : Se agrega el parametro UN_USUARIO para llevar la auditoria del proceso de actualización. 
                        Al revisar el proceso se encuentran incosistencias se debio revisar el proceso de access y ajustar todos los merge.
    @NAME:  actualizaRetencionesBancos
    @METHOD:  POST
  */ 
    UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_FECHAINICIAL IN VARCHAR2,
    UN_FECHAFINAL   IN VARCHAR2,
    UN_BANCOINICIAL IN PCK_SUBTIPOS.TI_CODIGOCONTA,
    UN_BANCOFINAL   IN PCK_SUBTIPOS.TI_CODIGOCONTA,
    UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
  MI_MERGETABLA   PCK_SUBTIPOS.TI_TABLA := 'DETALLE_COMPROBANTE_CNT';
  MI_MERGEUSING   PCK_SUBTIPOS.TI_MERGEUSING ;
  MI_MERGEENLACE  PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE  PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
  MI_ANO          PCK_SUBTIPOS.TI_ANIO;
  MI_TIPO_CPTE    PCK_SUBTIPOS.TI_TIPOCOMPROBANTE;
  MI_COMPROBANTE  PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
  MI_RTA          PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN
    BEGIN
        MI_MERGEUSING := ' SELECT DETALLE_COMPROBANTE_CNT_1.COMPANIA
                                 ,DETALLE_COMPROBANTE_CNT_1.ANO
                                 ,DETALLE_COMPROBANTE_CNT_1.TIPO_CPTE
                                 ,DETALLE_COMPROBANTE_CNT_1.COMPROBANTE
                                 ,DETALLE_COMPROBANTE_CNT_1.CONSECUTIVO 
                                 ,DETALLE_COMPROBANTE_CNT.CUENTA
                                 ,DETALLE_COMPROBANTE_CNT.FECHA
                          FROM DETALLE_COMPROBANTE_CNT         
                              INNER JOIN PLAN_CONTABLE 
                                  ON  DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA 
                                  AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO 
                                  AND DETALLE_COMPROBANTE_CNT.CUENTA   = PLAN_CONTABLE.CODIGO
                              INNER JOIN TIPO_COMPROBANTE 
                                  ON  DETALLE_COMPROBANTE_CNT.COMPANIA  = TIPO_COMPROBANTE.COMPANIA 
                                  AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
                              INNER JOIN DETALLE_COMPROBANTE_CNT DETALLE_COMPROBANTE_CNT_1 
                                  ON  DETALLE_COMPROBANTE_CNT.COMPANIA    = DETALLE_COMPROBANTE_CNT_1.COMPANIA 
                                  AND DETALLE_COMPROBANTE_CNT.ANO         = DETALLE_COMPROBANTE_CNT_1.ANO
                                  AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = DETALLE_COMPROBANTE_CNT_1.COMPROBANTE
                                  AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   = DETALLE_COMPROBANTE_CNT_1.TIPO_CPTE
                              INNER JOIN PLAN_CONTABLE PLAN_CONTABLE_1 
                                  ON  DETALLE_COMPROBANTE_CNT_1.COMPANIA = PLAN_CONTABLE_1.COMPANIA 
                                  AND DETALLE_COMPROBANTE_CNT_1.ANO      = PLAN_CONTABLE_1.ANO 
                                  AND DETALLE_COMPROBANTE_CNT_1.CUENTA   = PLAN_CONTABLE_1.CODIGO
                            WHERE  DETALLE_COMPROBANTE_CNT.COMPANIA = '''||UN_COMPANIA||'''
                              AND  TRUNC(DETALLE_COMPROBANTE_CNT.FECHA) BETWEEN TO_DATE('''||UN_FECHAINICIAL||''', ''DD/MM/YYYY'')  AND TO_DATE('''||UN_FECHAFINAL||''', ''DD/MM/YYYY'')
                              AND  TIPO_COMPROBANTE.CLASE_CONTABLE IN (''E'',''S'',''I'',''NBA'')
                              AND  PLAN_CONTABLE.CLASECUENTA IN (''B'')
                              AND  PLAN_CONTABLE_1.CLASECUENTA IN(''I'')
                              AND  DETALLE_COMPROBANTE_CNT_1.BANCO_RETENCION IS NULL';
        MI_MERGEENLACE := '       TABLA.COMPANIA    = VISTA.COMPANIA
                              AND TABLA.ANO         = VISTA.ANO
                              AND TABLA.TIPO_CPTE   = VISTA.TIPO_CPTE
                              AND TABLA.COMPROBANTE = VISTA.COMPROBANTE
                              AND TABLA.CONSECUTIVO = VISTA.CONSECUTIVO';   
        MI_MERGEEXISTE := ' UPDATE SET  
                              TABLA.DATE_MODIFIED         = SYSDATE,
                              TABLA.MODIFIED_BY           = '''||UN_USUARIO||''',
                              TABLA.BANCO_RETENCION       = VISTA.CUENTA,
                              TABLA.PAGO_RETENCION        = -1,
                              TABLA.FECHA_PAGO_RETENCION  = VISTA.FECHA';          
        BEGIN
          MI_RTA := PCK_DATOS.FC_ACME(
                                        UN_TABLA       => MI_MERGETABLA
                                       ,UN_ACCION      => 'MM' 
                                       ,UN_MERGEUSING  => MI_MERGEUSING 
                                       ,UN_MERGEENLACE => MI_MERGEENLACE 
                                       ,UN_MERGEEXISTE => MI_MERGEEXISTE);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
        <<RETENCIONBANCO>>  
        FOR RS IN (SELECT DETALLE_COMPROBANTE_CNT.ANO, 
                          DETALLE_COMPROBANTE_CNT.TIPO_CPTE, 
                          DETALLE_COMPROBANTE_CNT.COMPROBANTE
                     FROM DETALLE_COMPROBANTE_CNT
                        INNER JOIN PLAN_CONTABLE PLAN_CONTABLE
                          ON  DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA 
                          AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO 
                          AND DETALLE_COMPROBANTE_CNT.CUENTA   = PLAN_CONTABLE.CODIGO
                        INNER JOIN TIPO_COMPROBANTE 
                          ON DETALLE_COMPROBANTE_CNT.COMPANIA         = TIPO_COMPROBANTE.COMPANIA 
                          AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE       = TIPO_COMPROBANTE.CODIGO
                        INNER JOIN DETALLE_COMPROBANTE_CNT DETALLE_COMPROBANTE_CNT_1 
                          ON DETALLE_COMPROBANTE_CNT.COMPANIA         = DETALLE_COMPROBANTE_CNT_1.COMPANIA 
                          AND DETALLE_COMPROBANTE_CNT.ANO_AFECT       = DETALLE_COMPROBANTE_CNT_1.ANO
                          AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE_AFECT = DETALLE_COMPROBANTE_CNT_1.TIPO_CPTE 
                          AND DETALLE_COMPROBANTE_CNT.CMPTE_AFECTADO  = DETALLE_COMPROBANTE_CNT_1.COMPROBANTE
                        INNER JOIN PLAN_CONTABLE PLAN_CONTABLE_1 
                          ON  DETALLE_COMPROBANTE_CNT_1.COMPANIA = PLAN_CONTABLE_1.COMPANIA 
                          AND DETALLE_COMPROBANTE_CNT_1.ANO      = PLAN_CONTABLE_1.ANO 
                          AND DETALLE_COMPROBANTE_CNT_1.CUENTA   = PLAN_CONTABLE_1.CODIGO
                      WHERE  DETALLE_COMPROBANTE_CNT.COMPANIA    = UN_COMPANIA
                        AND  DETALLE_COMPROBANTE_CNT.FECHA          BETWEEN  UN_FECHAINICIAL AND UN_FECHAFINAL
                        AND  PLAN_CONTABLE.CODIGO                   BETWEEN  UN_BANCOINICIAL AND UN_BANCOFINAL 
                        AND  DETALLE_COMPROBANTE_CNT.CMPTE_AFECTADO IS NOT NULL
                        AND  PLAN_CONTABLE.CLASECUENTA              IN ('B') 
                        AND  TIPO_COMPROBANTE.CLASE_CONTABLE        IN ('E') 
                        AND  PLAN_CONTABLE_1.CLASECUENTA            IN ('I') 
                        AND  NVL(DETALLE_COMPROBANTE_CNT_1.PAGO_RETENCION,0) IN (0) 
                        AND DETALLE_COMPROBANTE_CNT_1.BANCO_RETENCION IS NULL
                      GROUP BY  DETALLE_COMPROBANTE_CNT.ANO, 
                                DETALLE_COMPROBANTE_CNT.TIPO_CPTE, 
                                DETALLE_COMPROBANTE_CNT.COMPROBANTE,
                                DETALLE_COMPROBANTE_CNT.CONSECUTIVO,
                                DETALLE_COMPROBANTE_CNT.CUENTA,
                                DETALLE_COMPROBANTE_CNT_1.PAGO_RETENCION,
                                DETALLE_COMPROBANTE_CNT.FECHA,
                                DETALLE_COMPROBANTE_CNT_1.BANCO_RETENCION)
        LOOP
            MI_MERGEUSING := 'SELECT DETALLE_COMPROBANTE_CNT_1.COMPANIA,
                                     DETALLE_COMPROBANTE_CNT_1.ANO,
                                     DETALLE_COMPROBANTE_CNT_1.TIPO_CPTE,
                                     DETALLE_COMPROBANTE_CNT_1.COMPROBANTE,
                                     DETALLE_COMPROBANTE_CNT_1.CONSECUTIVO,
                                     DETALLE_COMPROBANTE_CNT.CUENTA,
                                     DETALLE_COMPROBANTE_CNT.FECHA
                              FROM DETALLE_COMPROBANTE_CNT 
                                  INNER JOIN PLAN_CONTABLE 
                                       ON  DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA 
                                       AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO 
                                       AND DETALLE_COMPROBANTE_CNT.CUENTA   = PLAN_CONTABLE.CODIGO
                                  INNER JOIN TIPO_COMPROBANTE 
                                       ON  DETALLE_COMPROBANTE_CNT.COMPANIA  = TIPO_COMPROBANTE.COMPANIA 
                                       AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
                                  INNER JOIN DETALLE_COMPROBANTE_CNT DETALLE_COMPROBANTE_CNT_1 
                                      ON  DETALLE_COMPROBANTE_CNT.COMPANIA        = DETALLE_COMPROBANTE_CNT_1.COMPANIA 
                                      AND DETALLE_COMPROBANTE_CNT.ANO_AFECTADO    = DETALLE_COMPROBANTE_CNT_1.ANO 
                                      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE_AFECT = DETALLE_COMPROBANTE_CNT_1.TIPO_CPTE
                                      AND DETALLE_COMPROBANTE_CNT.CMPTE_AFECTADO  = DETALLE_COMPROBANTE_CNT_1.COMPROBANTE
                                  INNER JOIN PLAN_CONTABLE PLAN_CONTABLE_1 
                                      ON  DETALLE_COMPROBANTE_CNT_1.COMPANIA = PLAN_CONTABLE_1.COMPANIA 
                                      AND DETALLE_COMPROBANTE_CNT_1.ANO      = PLAN_CONTABLE_1.ANO 
                                      AND DETALLE_COMPROBANTE_CNT_1.CUENTA   = PLAN_CONTABLE_1.CODIGO
                                WHERE  DETALLE_COMPROBANTE_CNT.COMPANIA    = '''||UN_COMPANIA||'''
                                  AND  DETALLE_COMPROBANTE_CNT.ANO         = ' ||RS.ANO||' 
                                  AND  DETALLE_COMPROBANTE_CNT.TIPO_CPTE   = '''||RS.TIPO_CPTE||'''
                                  AND  DETALLE_COMPROBANTE_CNT.COMPROBANTE = '||RS.COMPROBANTE||'
                                  AND  DETALLE_COMPROBANTE_CNT.FECHA       BETWEEN TO_DATE('''||UN_FECHAINICIAL||''', ''DD/MM/YYYY'') AND TO_DATE('''||UN_FECHAFINAL||''', ''DD/MM/YYYY'')
                                  AND  PLAN_CONTABLE.CLASECUENTA           IN (''B'') 
                                  AND  TIPO_COMPROBANTE.CLASE_CONTABLE     IN (''E'') 
                                  AND  PLAN_CONTABLE_1.CLASECUENTA         IN (''I'') 
                                  AND  NVL(DETALLE_COMPROBANTE_CNT_1.BANCO_RETENCION,'') <> DETALLE_COMPROBANTE_CNT.CUENTA 
                                  AND  NVL(DETALLE_COMPROBANTE_CNT_1.PAGO_RETENCION,0) IN (0)';
            BEGIN
              MI_RTA := PCK_DATOS.FC_ACME(
                                            UN_TABLA       => MI_MERGETABLA
                                           ,UN_ACCION      => 'MM' 
                                           ,UN_MERGEUSING  => MI_MERGEUSING 
                                           ,UN_MERGEENLACE => MI_MERGEENLACE 
                                           ,UN_MERGEEXISTE => MI_MERGEEXISTE);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
            MI_MERGEUSING := 'SELECT DETALLE_COMPROBANTE_CNT_1.COMPANIA,
                                     DETALLE_COMPROBANTE_CNT_1.ANO,
                                     DETALLE_COMPROBANTE_CNT_1.TIPO_CPTE,
                                     DETALLE_COMPROBANTE_CNT_1.COMPROBANTE,
                                     DETALLE_COMPROBANTE_CNT_1.CONSECUTIVO,
                                     DETALLE_COMPROBANTE_CNT_2.CUENTA,
                                     DETALLE_COMPROBANTE_CNT.FECHA 
                              FROM DETALLE_COMPROBANTE_CNT 
                                  INNER JOIN PLAN_CONTABLE 
                                      ON  DETALLE_COMPROBANTE_CNT.COMPANIA = PLAN_CONTABLE.COMPANIA 
                                      AND DETALLE_COMPROBANTE_CNT.ANO      = PLAN_CONTABLE.ANO 
                                      AND DETALLE_COMPROBANTE_CNT.CUENTA   = PLAN_CONTABLE.CODIGO
                                  INNER JOIN TIPO_COMPROBANTE 
                                      ON DETALLE_COMPROBANTE_CNT.COMPANIA   = TIPO_COMPROBANTE.COMPANIA 
                                      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE = TIPO_COMPROBANTE.CODIGO
                                  INNER JOIN DETALLE_COMPROBANTE_CNT DETALLE_COMPROBANTE_CNT_1 
                                      ON  DETALLE_COMPROBANTE_CNT.COMPANIA        = DETALLE_COMPROBANTE_CNT_1.COMPANIA 
                                      AND DETALLE_COMPROBANTE_CNT.ANO_AFECTADO    = DETALLE_COMPROBANTE_CNT_1.ANO 
                                      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE_AFECT = DETALLE_COMPROBANTE_CNT_1.TIPO_CPTE 
                                      AND DETALLE_COMPROBANTE_CNT.CMPTE_AFECTADO  = DETALLE_COMPROBANTE_CNT_1.COMPROBANTE 
                                  INNER JOIN PLAN_CONTABLE PLAN_CONTABLE_1 
                                      ON  DETALLE_COMPROBANTE_CNT_1.COMPANIA = PLAN_CONTABLE_1.COMPANIA 
                                      AND DETALLE_COMPROBANTE_CNT_1.ANO      = PLAN_CONTABLE_1.ANO 
                                      AND DETALLE_COMPROBANTE_CNT_1.CUENTA   = PLAN_CONTABLE_1.CODIGO 
                                  INNER JOIN DETALLE_COMPROBANTE_CNT DETALLE_COMPROBANTE_CNT_2 
                                      ON  DETALLE_COMPROBANTE_CNT.COMPANIA    = DETALLE_COMPROBANTE_CNT_2.COMPANIA 
                                      AND DETALLE_COMPROBANTE_CNT.ANO         = DETALLE_COMPROBANTE_CNT_2.ANO 
                                      AND DETALLE_COMPROBANTE_CNT.TIPO_CPTE   = DETALLE_COMPROBANTE_CNT_2.TIPO_CPTE 
                                      AND DETALLE_COMPROBANTE_CNT.COMPROBANTE = DETALLE_COMPROBANTE_CNT_2.COMPROBANTE
                                  INNER JOIN PLAN_CONTABLE PLAN_CONTABLE_2 
                                      ON  DETALLE_COMPROBANTE_CNT_2.COMPANIA = PLAN_CONTABLE_2.COMPANIA 
                                      AND DETALLE_COMPROBANTE_CNT_2.ANO      = PLAN_CONTABLE_2.ANO 
                                      AND DETALLE_COMPROBANTE_CNT_2.CUENTA   = PLAN_CONTABLE_2.CODIGO 
                               WHERE  DETALLE_COMPROBANTE_CNT.COMPANIA          ='''||UN_COMPANIA||'''
                                 AND  DETALLE_COMPROBANTE_CNT.ANO               = ' ||MI_ANO||' 
                                 AND  DETALLE_COMPROBANTE_CNT.TIPO_CPTE         = '''||MI_TIPO_CPTE||'''
                                 AND  DETALLE_COMPROBANTE_CNT.COMPROBANTE       = ' ||MI_COMPROBANTE||
                                 ' AND  DETALLE_COMPROBANTE_CNT.FECHA           BETWEEN TO_DATE('''||UN_FECHAINICIAL||''') AND TO_DATE('''||UN_FECHAFINAL||''')
                                 AND  DETALLE_COMPROBANTE_CNT_1.BANCO_RETENCION IS NULL
                                 AND  DETALLE_COMPROBANTE_CNT.CMPTE_AFECTADO    NOT IN(0)
                                 AND  PLAN_CONTABLE_2.CLASECUENTA               IN (''B'')
                                 AND  TIPO_COMPROBANTE.CLASE_CONTABLE           IN (''E'')
                                 AND  PLAN_CONTABLE_1.CLASECUENTA               IN (''I'')
                                 AND  DETALLE_COMPROBANTE_CNT_1.BANCO_RETENCION <> DETALLE_COMPROBANTE_CNT_2.CUENTA 
                                 AND  DETALLE_COMPROBANTE_CNT_1.PAGO_RETENCION  IN(0)';
            BEGIN
                MI_RTA := PCK_DATOS.FC_ACME(
                                              UN_TABLA       => MI_MERGETABLA
                                             ,UN_ACCION      => 'MM' 
                                             ,UN_MERGEUSING  => MI_MERGEUSING 
                                             ,UN_MERGEENLACE => MI_MERGEENLACE 
                                             ,UN_MERGEEXISTE => MI_MERGEEXISTE);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN 
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;
      END LOOP RETENCIONBANCO;
   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
     PCK_ERR_MSG.RAISE_WITH_MSG(
                 UN_TABLAERROR => MI_MERGETABLA,
                 UN_EXC_COD    => SQLCODE,
                 UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTABILIDAD_RETENCION
                 );
   END;
END PR_RETENCIONBANCO;
FUNCTION FC_CIERRECONTABLE(
  /*
    NAME              : FC_CIERRECONTABLE --> en Access cierreContable
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 29/11/2016
    TIME              : 09:00 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Realiza el proceso de cierre contable llama a la funcion FC_INTERFAZ_CONTABLE_HACT.

    MODIFICATIONS     : 
    @NAME:  cierreContable
    @METHOD:  put
  */ 
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOCOMPROBANTE  IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_NUMERO           IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_ANIO             IN PCK_SUBTIPOS.TI_ANIO,
    UN_FECHA            IN DATE,
    UN_MES              IN PCK_SUBTIPOS.TI_MES,
    UN_CENTRO_COSTO     IN PCK_SUBTIPOS.TI_CENTRO_COSTO,
    UN_USUARIO          IN VARCHAR2,
    UN_GENERA_COMP_DESC IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0)
  RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_RTA    NUMBER;
  MI_EXISTE PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN
    BEGIN
        BEGIN
            SELECT COUNT(*) EXISTE
            INTO MI_EXISTE
            FROM HEADERCIERRE
            WHERE COMPANIA       = UN_COMPANIA
              AND ANO              = UN_ANIO
              AND CIERREIMPUESTOS  IN (0)
              AND CONDICION        IN ('U','P');
        EXCEPTION WHEN NO_DATA_FOUND THEN
            MI_EXISTE :=0;
        END;
        IF MI_EXISTE = 0 THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD; 
        END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
       PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERRR_CONTABILIDAD_CONFCUENTAS
            );
    END;
    BEGIN 
        BEGIN
            SELECT COUNT(*) MI_EXISTE 
             INTO MI_EXISTE
            FROM HEADERCIERRE 
            WHERE COMPANIA  = UN_COMPANIA 
              AND ANO       = UN_ANIO 
              AND CONDICION = 'P' 
              AND CIERREIMPUESTOS IN (0)  
              AND CUENTAACERRAR IN (SELECT CUENTAACERRAR 
                                      FROM HEADERCIERRE 
                                     WHERE COMPANIA        = UN_COMPANIA 
                                       AND ANO             = UN_ANIO 
                                       AND CIERREIMPUESTOS IN (0) 
                                       AND CONDICION       IN ('U','P'));
        EXCEPTION WHEN NO_DATA_FOUND THEN      
            MI_EXISTE:=0; 
        END;
        IF MI_EXISTE =0 THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END IF; 
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERRR_CONTABILIDAD_CUECIEPER
            );
    END;

    BEGIN
        BEGIN
            SELECT COUNT(*) MI_EXISTE 
             INTO MI_EXISTE
            FROM HEADERCIERRE 
            WHERE COMPANIA = UN_COMPANIA 
              AND ANO      = UN_ANIO 
              AND CIERREIMPUESTOS IN (0)  
              AND CONDICION ='U' 
              AND CUENTAACERRAR IN (SELECT CUENTAACERRAR 
                                      FROM HEADERCIERRE 
                                     WHERE COMPANIA = UN_COMPANIA 
                                       AND ANO      = UN_ANIO 
                                       AND CIERREIMPUESTOS IN (0) 
                                       AND CONDICION IN ('U','P'));
        EXCEPTION WHEN NO_DATA_FOUND THEN 
            MI_EXISTE:= 0;
        END;
        IF MI_EXISTE = 0 THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERRR_CONTABILIDAD_CUECIEUTI
              );
    END;
    MI_RTA:=  FC_INTERFAZ_CONTABLE_HACT(  UN_COMPANIA        => UN_COMPANIA,
                                          UN_TIPOCOMPROBANTE => UN_TIPOCOMPROBANTE,
                                          UN_NUMERO          => UN_NUMERO,
                                          UN_ANIO            => UN_ANIO,
                                          UN_MES             => UN_MES,
                                          UN_FECHA           => UN_FECHA,
                                          UN_TERCERO         => PCK_DATOS.FC_CONS_TERCERO,
                                          UN_SUCURSAL        => PCK_DATOS.FC_CONS_SUCURSAL,
                                          UN_CENTRO_COSTO    => UN_CENTRO_COSTO,
                                          UN_DESCRIPCION     => 'Interface Cierre de '||PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_MES)||' de '||UN_ANIO,
                                          UN_CREADOR         => UN_USUARIO,
                                          UN_SIMPLE          => CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'SIMPLIFICAR INTERFACE CIERRE',PCK_DATOS.FC_MODULOCONTABILIDAD,SYSDATE),'SI') = 'SI'
                                                                      THEN -1
                                                                      ELSE 0
                                                                      END,
                                          UN_INDIMPRESION    => 0,
                                          UN_GENERA_COMP_DESC => UN_GENERA_COMP_DESC);
    RETURN MI_RTA;
END FC_CIERRECONTABLE;

FUNCTION FC_INTERFAZ_CONTABLE_HACT(
   /*
    NAME              : FC_INTERFAZ_CONTABLE_HACT
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
    DATE MIGRADOR     : 29/11/2016
    TIME              : 10:42 AM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : Realiza el proceso de interfaz a traves de una consulta que 
                        reeemplaza la tabla temporal.

    MODIFICATIONS     : 
    @NAME:  cierreContable
    @METHOD:  put
  */ 
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOCOMPROBANTE  IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_NUMERO           IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_ANIO             IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES              IN PCK_SUBTIPOS.TI_MES,
    UN_FECHA            IN DATE,
    UN_TERCERO          IN PCK_SUBTIPOS.TI_TERCERO,
    UN_SUCURSAL         IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_CENTRO_COSTO     IN PCK_SUBTIPOS.TI_CENTRO_COSTO,
    UN_DESCRIPCION      IN VARCHAR2,
    UN_CREADOR          IN VARCHAR2,
    UN_SIMPLE           IN PCK_SUBTIPOS.TI_LOGICO,
    UN_INDIMPRESION     IN PCK_SUBTIPOS.TI_LOGICO,
    UN_PLANO            IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0,
    UN_GENERA_COMP_DESC IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0,
    UN_TEXTO            IN VARCHAR2 DEFAULT 'Interfase' 
    )
  RETURN PCK_SUBTIPOS.TI_LOGICO
AS
  MI_CLASE                PCK_SUBTIPOS.TI_CLASECUENTACONTA;
  MI_EXISTE               PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_DIFERENCIA           PCK_SUBTIPOS.TI_DOBLE;
  MI_RTA_ACME             NUMBER;
  MI_STRSQL               PCK_SUBTIPOS.TI_STRSQL;
  MI_PLANAJUSTES          PCK_SUBTIPOS.TI_STRSQL;
  MI_PLANACIERREMES       PCK_SUBTIPOS.TI_STRSQL;
  MI_STRCUENTA            PCK_SUBTIPOS.TI_STRSQL;
  MI_ACTUALIZAR           PCK_SUBTIPOS.TI_LOGICO;
  MI_SVALOR_DEBITO        PCK_SUBTIPOS.TI_DOBLE; 
  MI_SVALOR_CREDITO       PCK_SUBTIPOS.TI_DOBLE;
  MI_DBLVALORDOCUMENTO    PCK_SUBTIPOS.TI_DOBLE;
  MI_TABLA                PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES              PCK_SUBTIPOS.TI_VALORES;
  MI_DBLGIRAR             PCK_SUBTIPOS.TI_DOBLE;
  MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
  MI_RS                   SYS_REFCURSOR;
  MI_COMPANIA             PCK_SUBTIPOS.TI_COMPANIA;
  MI_ANO                  PCK_SUBTIPOS.TI_ANIO;
  MI_TIPO_CPTE            PCK_SUBTIPOS.TI_TIPOCOMPROBANTE; 
  MI_COMPROBANTE          PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
  MI_CUENTA               PCK_SUBTIPOS.TI_CODIGOCONTA;
  MI_CONSECUTIVO          PCK_SUBTIPOS.TI_CONSECUTIVOCNT;
  MI_FECHA                DATE;
  MI_NATURALEZA           PCK_SUBTIPOS.TI_NATURALEZACONTA;
  MI_CUENTAPPTAL          DETALLE_COMPROBANTE_CNT.CUENTAPPTAL%TYPE;
  MI_VALOR_DEBITO         PCK_SUBTIPOS.TI_DOBLE;
  MI_VALOR_CREDITO        PCK_SUBTIPOS.TI_DOBLE;
  MI_DEBITO_EQUIV         PCK_SUBTIPOS.TI_DOBLE;
  MI_EJECUCION_DEBITO     PCK_SUBTIPOS.TI_DOBLE;
  MI_CREDITO_EQUIV        PCK_SUBTIPOS.TI_DOBLE;
  MI_EJECUCION_CREDITO    PCK_SUBTIPOS.TI_DOBLE;
  MI_CENTRO_COSTO         PCK_SUBTIPOS.TI_CENTRO_COSTO;
  MI_TERCERO              PCK_SUBTIPOS.TI_TERCERO; 
  MI_SUCURSAL             PCK_SUBTIPOS.TI_SUCURSAL;  
  MI_AUXILIAR             PCK_SUBTIPOS.TI_AUXILIAR;
  MI_CIERRE               PCK_SUBTIPOS.TI_LOGICO;
  MI_PAIS                 DETALLE_COMPROBANTE_CNT.PAIS%TYPE;
  MI_DEPARTAMENTO         DETALLE_COMPROBANTE_CNT.DEPARTAMENTO%TYPE;
  MI_CIUDAD               DETALLE_COMPROBANTE_CNT.CIUDAD%TYPE;  
  
   -- MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    --MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_RTAPLANO       CLOB;
    MI_RTA            PCK_SUBTIPOS.TI_RTA_ACME;
  
BEGIN
    BEGIN 
        SELECT LISTAGG(''''||CUENTAACERRAR||'''', ',') WITHIN GROUP (ORDER BY CUENTAACERRAR) CUENTAACERRAR
        INTO MI_STRCUENTA 
        FROM HEADERCIERRE 
        WHERE COMPANIA        = UN_COMPANIA 
          AND ANO             = UN_ANIO  
          AND CIERREIMPUESTOS IN (0)
          AND CONDICION IN ('U','P');
    EXCEPTION WHEN NO_DATA_FOUND THEN  
        PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERRR_CONTABILIDAD_CONFCUENTAS
              );
    END;
  
      --Se eliminan datos temporales  de las tablas rs,rs1,rs2,rs3,PLANAAJUSTESCONSULTA
      MI_CONDICION :=' COMPANIA          = ''' || UN_COMPANIA || ''' ';
       
      BEGIN 
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'RS'
                        ,UN_ACCION  => 'E'
                        ,UN_CONDICION => '1 = 1');   --JM 16/01/2025 CC656
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                 

      END;  
        BEGIN 
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'RS1'
                        ,UN_ACCION  => 'E'
                        ,UN_CONDICION => '1 = 1');   --JM 16/01/2025 CC656
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                 

      END;  
        BEGIN 
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'RS2'
                        ,UN_ACCION  => 'E'
                        ,UN_CONDICION => '1 = 1');   --JM 16/01/2025 CC656
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                 

      END;
    BEGIN 
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'RS3'
                        ,UN_ACCION  => 'E'
                        ,UN_CONDICION => '1 = 1');   --JM 16/01/2025 CC656
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                 

      END;
        BEGIN 
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'PLANAAJUSTESCONSULTA'
                        ,UN_ACCION  => 'E'
                        ,UN_CONDICION => '1 = 1');   
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                 

      END;
  
  MI_VALORES:= ' SELECT 
                        ROW_NUMBER() OVER (PARTITION BY CIERREDEMES.COMPANIA ORDER BY CIERREDEMES.COMPANIA) CONSECUTIVO,
              CIERREDEMES.COMPANIA,
              CIERREDEMES.ANO,
              CIERREDEMES.CUENTAACERRAR,
              CIERREDEMES.CONTRACUENTA,
              PLAN_CONTABLE.NATURALEZA,
              PLAN_CONTABLE.TERCERO,
              PLAN_CONTABLE.SUCURSAL,
              PLAN_CONTABLE.AUXILIAR,
              PLAN_CONTABLE.CENTRO_COSTO 
           FROM  HEADERCIERRE  CIERREDEMES
            INNER JOIN V_PLAN_CONTABLE PLAN_CONTABLE
            ON CIERREDEMES.COMPANIA      = PLAN_CONTABLE.COMPANIA
            AND CIERREDEMES.ANO          = PLAN_CONTABLE.ANO
            AND CIERREDEMES.CONTRACUENTA = PLAN_CONTABLE.ID
           WHERE CIERREDEMES.COMPANIA          = ''' || UN_COMPANIA || '''
           AND CIERREDEMES.ANO               = '   || UN_ANIO     || '
           AND CIERREDEMES.TIPOCOMPROBANTE   = ''' || UN_TIPOCOMPROBANTE || '''
           AND CIERREDEMES.CIERREIMPUESTOS IN (0) 
           AND CIERREDEMES.CONDICION IS NULL
           ORDER BY CIERREDEMES.COMPANIA,
              CIERREDEMES.ANO,
              CIERREDEMES.TIPOCOMPROBANTE,
              CIERREDEMES.CUENTAACERRAR ';
  MI_CAMPOS := '      CONSECUTIVO
                       ,COMPANIA 
                       ,ANO
                       ,CUENTAACERRAR
                       ,CONTRACUENTA
                       ,NATURALEZA
             ,TERCERO
             ,SUCURSAL
             ,AUXILIAR
                       ,CENTRO_COSTO
                ';        
  
  MI_RTA  := PCK_DATOS.FC_ACME(UN_TABLA   => 'RS',
                                     UN_ACCION  => 'IS',
                                     UN_CAMPOS  => MI_CAMPOS,
                                     UN_VALORES => MI_VALORES);
                   
    MI_VALORES :=  'SELECT           ROW_NUMBER() OVER (PARTITION BY PLAN_CONTABLE.COMPANIA ORDER BY PLAN_CONTABLE.COMPANIA) CONSECUTIVO,
                                   PLAN_CONTABLE.COMPANIA,
                                     PLAN_CONTABLE.ANO,
                                     PLAN_CONTABLE.CODIGO,
                                     '||UN_MES||' MES,
                                     PLAN_CONTABLE.SALDO'||UN_MES||' SALDO,
                                     PLAN_CONTABLE.NATURALEZA, 
                                     PLAN_CONTABLE.CENTRO_COSTO,
                                     PLAN_CONTABLE.TERCERO,
                                     PLAN_CONTABLE.SUCURSAL,
                                     PLAN_CONTABLE.AUXILIAR,
                                     RS.NATURALEZA NATURALEZA_RS,
                                     RS.CENTRO_COSTO CENTRO_COSTO_RS,
                                     RS.TERCERO TERCERO_RS,
                                     RS.SUCURSAL SUCURSAL_RS,
                                     RS.AUXILIAR AUXILIAR_RS,
                                     RS.CONTRACUENTA CUENTA,
                                     RS.CONTRACUENTA NUMERO
                             FROM V_PLAN_CONTABLE PLAN_CONTABLE
                               INNER JOIN (SELECT
                        ROW_NUMBER() OVER (PARTITION BY CIERREDEMES.COMPANIA ORDER BY CIERREDEMES.COMPANIA) CONSECUTIVO,
              CIERREDEMES.COMPANIA,
              CIERREDEMES.ANO,
              CIERREDEMES.CUENTAACERRAR,
              CIERREDEMES.CONTRACUENTA,
              PLAN_CONTABLE.NATURALEZA,
              PLAN_CONTABLE.TERCERO,
              PLAN_CONTABLE.SUCURSAL,
              PLAN_CONTABLE.AUXILIAR,
              PLAN_CONTABLE.CENTRO_COSTO
           FROM  HEADERCIERRE  CIERREDEMES
            INNER JOIN V_PLAN_CONTABLE PLAN_CONTABLE
            ON CIERREDEMES.COMPANIA      = PLAN_CONTABLE.COMPANIA
            AND CIERREDEMES.ANO          = PLAN_CONTABLE.ANO
            AND CIERREDEMES.CONTRACUENTA = PLAN_CONTABLE.ID
           WHERE CIERREDEMES.COMPANIA          = ''' || UN_COMPANIA || '''
           AND CIERREDEMES.ANO               = '   || UN_ANIO     || '
           AND CIERREDEMES.TIPOCOMPROBANTE   = ''' || UN_TIPOCOMPROBANTE || '''
           AND CIERREDEMES.CIERREIMPUESTOS IN (0)
           AND CIERREDEMES.CONDICION IS NULL
           ORDER BY CIERREDEMES.COMPANIA,
              CIERREDEMES.ANO,
              CIERREDEMES.TIPOCOMPROBANTE,
              CIERREDEMES.CUENTAACERRAR ) RS
                                 ON  PLAN_CONTABLE.COMPANIA                               = RS.COMPANIA
                                 AND PLAN_CONTABLE.ANO                                    = RS.ANO
                                 AND SUBSTR(PLAN_CONTABLE.ID,0, LENGTH(RS.CUENTAACERRAR)) = RS.CUENTAACERRAR
                             WHERE PLAN_CONTABLE.COMPANIA   = '''||UN_COMPANIA||'''
                               AND PLAN_CONTABLE.ANO        = '||UN_ANIO||'
                               AND PLAN_CONTABLE.MOVIMIENTO NOT IN (0)
                               AND PLAN_CONTABLE.SALDO'||UN_MES||'    NOT IN (0)'||
                            CASE WHEN PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'MANEJA CIERRE POR CENTRO DE COSTO',PCK_DATOS.FC_MODULOCONTABILIDAD,SYSDATE)='SI'
                                 THEN CHR(10)||CHR(13)||' AND PLAN_CONTABLE.CENTRO_COSTO = '''||UN_CENTRO_COSTO||''''
                                 ELSE ''
                                 END||
                           ' ORDER BY PLAN_CONTABLE.COMPANIA,
                                      PLAN_CONTABLE.ANO,
                                      PLAN_CONTABLE.CODIGO';
   MI_CAMPOS := ' CONSECUTIVO
                 ,COMPANIA
                   ,ANO    
                   ,CODIGO
             ,MES
           ,SALDO
           ,NATURALEZA         
           ,CENTRO_COSTO
                 ,TERCERO
           ,SUCURSAL
           ,AUXILIAR
           ,NATURALEZA_RS
           ,CENTRO_COSTO_RS
           ,TERCERO_RS
           ,SUCURSAL_RS
           ,AUXILIAR_RS
           ,CUENTA
           ,NUMERO';
           
  MI_RTA  := PCK_DATOS.FC_ACME(UN_TABLA   => 'RS1',
                                     UN_ACCION  => 'IS',
                                     UN_CAMPOS  => MI_CAMPOS,
                                     UN_VALORES => MI_VALORES);
                   
  
    MI_VALORES := 'SELECT ROW_NUMBER() OVER (PARTITION BY CIERREDEMES.COMPANIA ORDER BY CIERREDEMES.COMPANIA) CONSECUTIVO,
                                 CIERREDEMES.COMPANIA,
                                   CIERREDEMES.ANO,
                                   CIERREDEMES.CUENTAACERRAR,
                                   CIERREDEMES.CONTRACUENTA 
                            FROM HEADERCIERRE CIERREDEMES
                            WHERE CIERREDEMES.COMPANIA        =''' || UN_COMPANIA || '''
                              AND CIERREDEMES.ANO             = '  || UN_ANIO     || '
                              AND CIERREDEMES.CONDICION       = ''U''
                              AND CIERREDEMES.CUENTAACERRAR   IN ('||MI_STRCUENTA||')
                              AND CIERREDEMES.CIERREIMPUESTOS IN (0) ';
  
    MI_CAMPOS := '    CONSECUTIVO
                   ,COMPANIA
                   ,ANO
                   ,CUENTAACERRAR
           ,CONTRACUENTA
              ';
  MI_RTA  := PCK_DATOS.FC_ACME(UN_TABLA   => 'RS2',
                                     UN_ACCION  => 'IS',
                                     UN_CAMPOS  => MI_CAMPOS,
                                     UN_VALORES => MI_VALORES);
  
    MI_VALORES :=   'SELECT         ROW_NUMBER() OVER (PARTITION BY CIERREDEMES.COMPANIA ORDER BY CIERREDEMES.COMPANIA) CONSECUTIVO,
                                 CIERREDEMES.COMPANIA,
                                   CIERREDEMES.ANO,
                                   CIERREDEMES.CUENTAACERRAR, 
                                   CIERREDEMES.CONTRACUENTA
                            FROM HEADERCIERRE CIERREDEMES
                            WHERE CIERREDEMES.COMPANIA        ='''||UN_COMPANIA||'''
                              AND CIERREDEMES.ANO             = '||UN_ANIO||'
                              AND CIERREDEMES.CONDICION       = ''P''
                              AND CIERREDEMES.CUENTAACERRAR   IN ('||MI_STRCUENTA||')
                              AND CIERREDEMES.CIERREIMPUESTOS IN (0)' ;
  
    MI_CAMPOS := '    CONSECUTIVO
                   ,COMPANIA
                   ,ANO
                   ,CUENTAACERRAR
           ,CONTRACUENTA
              ';
  MI_RTA  := PCK_DATOS.FC_ACME(UN_TABLA   => 'RS3',
                                     UN_ACCION  => 'IS',
                                     UN_CAMPOS  => MI_CAMPOS,
                                     UN_VALORES => MI_VALORES);             
  MI_VALORES :=  '
  
                           SELECT RS1.COMPANIA,
                                    RS1.ANO,
                                    '''||UN_TIPOCOMPROBANTE||''' TIPO_CPTE,
                                    '||UN_NUMERO||' COMPROBANTE,
                                    CASE WHEN ROWNUM= 1
                                         THEN 1
                                         ELSE 2*(ROWNUM-1)+1
                                    END CONSECUTIVO,
                                    RS1.CODIGO CUENTA,
                                    RS1.CODIGO CODIGO_CUENTA,
                                    TO_DATE('''||TO_CHAR(UN_FECHA,'DD/MM/YYYY')||''',''DD/MM/YYYY'') FECHA,
                                    RS1.NATURALEZA,
                                    TRUNC(CASE RS1.NATURALEZA WHEN ''D''
                                                              THEN (CASE WHEN RS1.SALDO >= 0
                                                                         THEN NVL(RS1.SALDO,0)
                                                                         ELSE 0
                                                                    END)
                                                              WHEN ''C'' 
                                                              THEN (CASE WHEN RS1.SALDO >= 0
                                                                         THEN 0
                                                                         ELSE NVL(-RS1.SALDO,0)
                                                                    END)
                                         END*100+0.001)/100 VALOR_CREDITO,
                                    TRUNC(CASE RS1.NATURALEZA WHEN ''D'' THEN (CASE WHEN RS1.SALDO >= 0 
                                                                                  THEN 0
                                                                                  ELSE NVL(-RS1.SALDO,0)
                                                                             END)
                                                              WHEN ''C'' THEN (CASE WHEN RS1.SALDO >= 0
                                                                                  THEN NVL(RS1.SALDO,0)
                                                                                  ELSE 0
                                                                             END)
                                         END*100+0.001)/100 VALOR_DEBITO,
                                    NVL(RS1.CENTRO_COSTO,PCK_DATOS.FC_CONS_CENTRO) CENTRO_COSTO,
                                    NVL(RS1.TERCERO,PCK_DATOS.FC_CONS_TERCERO) TERCERO,
                                    NVL(RS1.SUCURSAL,PCK_DATOS.FC_CONS_SUCURSAL) SUCURSAL,
                                    NVL(RS1.AUXILIAR,PCK_DATOS.FC_CONS_AUXILIAR) AUXILIAR,
                                    CASE WHEN '||UN_MES||'=12
                                         THEN -1
                                         ELSE 0
                                    END CIERRE
                             FROM RS1
                            UNION ALL
                             SELECT RS1.COMPANIA,
                                    RS1.ANO,
                                    '''||UN_TIPOCOMPROBANTE||''' TIPO_CPTE,
                                    '||UN_NUMERO||' COMPROBANTE,
                                    2*ROWNUM CONSECUTIVO,
                                    RS1.CUENTA,
                                    RS1.NUMERO CODIGO_CUENTA,
                                    TO_DATE('''||TO_CHAR(UN_FECHA,'DD/MM/YYYY')||''',''DD/MM/YYYY'') FECHA,
                                    RS1.NATURALEZA_RS NATURALEZA,
                                    TRUNC(CASE RS1.NATURALEZA WHEN ''D'' THEN (CASE WHEN RS1.SALDO >= 0 
                                                                                    THEN 0
                                                                                    ELSE NVL(-RS1.SALDO,0)
                                                                               END)
                                                              WHEN ''C'' THEN (CASE WHEN RS1.SALDO >= 0
                                                                                    THEN NVL(RS1.SALDO,0)
                                                                                    ELSE 0
                                                                               END)
                                          END*100+0.001)/100 VALOR_DEBITO,
                                    TRUNC(CASE RS1.NATURALEZA WHEN ''D''
                                                              THEN (CASE WHEN RS1.SALDO >= 0
                                                                         THEN NVL(RS1.SALDO,0)
                                                                         ELSE 0
                                                                    END)
                                                              WHEN ''C'' 
                                                              THEN (CASE WHEN RS1.SALDO >= 0
                                                                         THEN 0
                                                                         ELSE NVL(-SALDO,0)
                                                                    END) 
                                          END*100+0.001)/100 VALOR_CREDITO,
                                    NVL(RS1.CENTRO_COSTO_RS,PCK_DATOS.FC_CONS_CENTRO) CENTRO_COSTO,
                                    NVL(RS1.TERCERO_RS,PCK_DATOS.FC_CONS_TERCERO) TERCERO,
                                    NVL(RS1.SUCURSAL_RS,PCK_DATOS.FC_CONS_SUCURSAL) SUCURSAL,
                                    NVL(RS1.AUXILIAR_RS,PCK_DATOS.FC_CONS_AUXILIAR) AUXILIAR,
                                    CASE WHEN '||UN_MES||'=12
                                         THEN -1
                                         ELSE 0
                                    END CIERRE
                             FROM  RS1 
  ';
  
  
  
    MI_CAMPOS := '  COMPANIA
                    ,ANO
                    ,TIPO_CPTE
                    ,COMPROBANTE
                    ,CONSECUTIVO
                    ,CUENTA
                    ,CODIGO_CUENTA
                    ,FECHA
                    ,NATURALEZA
                    ,VALOR_CREDITO
                    ,VALOR_DEBITO
                    ,CENTRO_COSTO
                    ,TERCERO
                    ,SUCURSAL
                    ,AUXILIAR
                    ,CIERRE
  
  ';
  MI_RTA  := PCK_DATOS.FC_ACME(UN_TABLA   => 'PLANAAJUSTESCONSULTA',
                                     UN_ACCION  => 'IS',
                                     UN_CAMPOS  => MI_CAMPOS,
                                     UN_VALORES => MI_VALORES); 

  
           
    MI_PLANAJUSTES:='           SELECT * 
                                FROM 
                                (SELECT COMPANIA
                                        ,ANO
                                        ,TIPO_CPTE
                                        ,COMPROBANTE
                                        ,CONSECUTIVO
                                        ,CUENTA
                                        ,CODIGO_CUENTA
                                        ,FECHA
                                        ,NATURALEZA
                                        ,VALOR_CREDITO
                                        ,VALOR_DEBITO
                                        ,CENTRO_COSTO
                                        ,TERCERO
                                        ,SUCURSAL
                                        ,AUXILIAR
                                        ,CIERRE 
                                 FROM PLANAAJUSTESCONSULTA 
                                 UNION ALL 
                                 (SELECT * 
                                 FROM (SELECT  PLANAAJUSTESCONSULTA.COMPANIA,
                                               PLANAAJUSTESCONSULTA.ANO,
                                               PLANAAJUSTESCONSULTA.TIPO_CPTE,
                                               PLANAAJUSTESCONSULTA.COMPROBANTE,
                                               MAX(PLANAAJUSTESCONSULTA.CONSECUTIVO)+1 CONSECUTIVO,
                                               RS2.CUENTAACERRAR CUENTA,
                                               RS2.CUENTAACERRAR CODIGO_CUENTA,
                                               PLANAAJUSTESCONSULTA.FECHA,
                                               ''D'' NATURALEZA,
                                               0 VALOR_CREDITO,
                                               PCK_SYSMAN_UTL.FC_ROUND(NVL(SUM(PLANAAJUSTESCONSULTA.VALOR_CREDITO-PLANAAJUSTESCONSULTA.VALOR_DEBITO),0),2) VALOR_DEBITO,
                                               PCK_DATOS.FC_CONS_CENTRO CENTRO_COSTO,
                                               PCK_DATOS.FC_CONS_TERCERO TERCERO,
                                               PCK_DATOS.FC_CONS_SUCURSAL SUCURSAL,
                                               PCK_DATOS.FC_CONS_AUXILIAR AUXILIAR,
                                               CASE WHEN '||UN_MES||' = 12
                                                    THEN -1
                                                    ELSE 0
                                               END CIERRE
                                       FROM PLANAAJUSTESCONSULTA INNER JOIN  RS2
                                         ON PLANAAJUSTESCONSULTA.COMPANIA =  RS2.COMPANIA
                                         AND PLANAAJUSTESCONSULTA.ANO =  RS2.ANO
                                         AND PLANAAJUSTESCONSULTA.CODIGO_CUENTA =  RS2.CUENTAACERRAR
                                       WHERE PLANAAJUSTESCONSULTA.COMPANIA     = '''||UN_COMPANIA||'''
                                         AND PLANAAJUSTESCONSULTA.ANO            = '||UN_ANIO||'
                                         AND PLANAAJUSTESCONSULTA.CODIGO_CUENTA IN ('||MI_STRCUENTA||')
                                       GROUP BY PLANAAJUSTESCONSULTA.COMPANIA,
                                                PLANAAJUSTESCONSULTA.ANO,
                                                PLANAAJUSTESCONSULTA.TIPO_CPTE,
                                                PLANAAJUSTESCONSULTA.COMPROBANTE,
                                                RS2.CUENTAACERRAR,
                                                PLANAAJUSTESCONSULTA.FECHA,
                                                ''D'',
                                                0,
                                                PCK_DATOS.FC_CONS_CENTRO,
                                                PCK_DATOS.FC_CONS_TERCERO,
                                                PCK_DATOS.FC_CONS_SUCURSAL,
                                                PCK_DATOS.FC_CONS_AUXILIAR,
                                                CASE WHEN '||UN_MES||'=12
                                                     THEN -1
                                                     ELSE 0
                                                END 
                                       UNION ALL 
                           SELECT '''||UN_COMPANIA||''' COMPANIA,
                                 '||UN_ANIO||' ANO,
                                 '''||UN_TIPOCOMPROBANTE||''' TIPO_CPTE,
                                 '||UN_NUMERO||' COMPROBANTE,
                                 MAX(PLANAAJUSTESCONSULTA.CONSECUTIVO)+2 CONSECUTIVO,
                                 RS2.CONTRACUENTA CUENTA,
                                 RS2.CONTRACUENTA CODIGO_CUENTA,
                                 TO_DATE('''||TO_CHAR(UN_FECHA,'DD/MM/YYYY')||''',''DD/MM/YYYY'') FECHA,
                                 ''C'' NATURALEZA,
                                 PCK_SYSMAN_UTL.FC_ROUND(NVL(SUM(VALOR_CREDITO-VALOR_DEBITO),0),2) VALOR_CREDITO,
                                 0 VALOR_DEBITO,
                                 PCK_DATOS.FC_CONS_CENTRO CENTRO_COSTO,
                                 PCK_DATOS.FC_CONS_TERCERO TERCERO,
                                 PCK_DATOS.FC_CONS_SUCURSAL SUCURSAL,
                                 PCK_DATOS.FC_CONS_AUXILIAR AUXILIAR,
                                 CASE WHEN '||UN_MES||' = 12
                                      THEN -1
                                      ELSE 0
                                 END CIERRE
                          FROM PLANAAJUSTESCONSULTA INNER JOIN  RS2
                                         ON PLANAAJUSTESCONSULTA.COMPANIA =  RS2.COMPANIA
                                         AND PLANAAJUSTESCONSULTA.ANO =  RS2.ANO
                                         AND PLANAAJUSTESCONSULTA.CODIGO_CUENTA =  RS2.CUENTAACERRAR
                          WHERE PLANAAJUSTESCONSULTA.COMPANIA     = '''||UN_COMPANIA||'''
                            AND PLANAAJUSTESCONSULTA.ANO            = '||UN_ANIO||'
                            AND PLANAAJUSTESCONSULTA.CODIGO_CUENTA IN ('||MI_STRCUENTA||')
                          GROUP BY '''||UN_COMPANIA||''',
                                   '||UN_ANIO||',
                                   '''||UN_TIPOCOMPROBANTE||''',
                                   '||UN_NUMERO||',
                                   RS2.CONTRACUENTA,
                                   TO_DATE('''||TO_CHAR(UN_FECHA,'DD/MM/YYYY')||''',''DD/MM/YYYY''),
                                   ''C'',
                                   0,
                                   PCK_DATOS.FC_CONS_CENTRO,
                                   PCK_DATOS.FC_CONS_TERCERO,
                                   PCK_DATOS.FC_CONS_SUCURSAL,
                                   PCK_DATOS.FC_CONS_AUXILIAR,
                                   CASE WHEN '||UN_MES||' = 12
                                        THEN -1
                                        ELSE 0
                                   END)
                     WHERE (SELECT PCK_SYSMAN_UTL.FC_ROUND(NVL(SUM(VALOR_CREDITO-VALOR_DEBITO),0),2) UTILIDAD
                            FROM PLANAAJUSTESCONSULTA
                            WHERE COMPANIA      = '''||UN_COMPANIA||'''
                              AND ANO           = '||UN_ANIO||'
                              AND CODIGO_CUENTA IN ('||MI_STRCUENTA||'))>0)
                     UNION ALL 
                     (SELECT * 
                     FROM (SELECT  PLANAAJUSTESCONSULTA.COMPANIA,
                                   PLANAAJUSTESCONSULTA.ANO,
                                   PLANAAJUSTESCONSULTA.TIPO_CPTE,
                                   PLANAAJUSTESCONSULTA.COMPROBANTE,
                                   MAX(PLANAAJUSTESCONSULTA.CONSECUTIVO)+1 CONSECUTIVO,
                                   RS3.CUENTAACERRAR CUENTA,
                                   RS3.CUENTAACERRAR CODIGO_CUENTA,
                                   PLANAAJUSTESCONSULTA.FECHA,
                                   ''D'' NATURALEZA,
                                   PCK_SYSMAN_UTL.FC_ROUND(NVL(SUM(PLANAAJUSTESCONSULTA.VALOR_CREDITO-PLANAAJUSTESCONSULTA.VALOR_DEBITO),0),2)*-1 VALOR_CREDITO,
                                   0 VALOR_DEBITO,
                                   PCK_DATOS.FC_CONS_CENTRO CENTRO_COSTO,
                                   PCK_DATOS.FC_CONS_TERCERO TERCERO,
                                   PCK_DATOS.FC_CONS_SUCURSAL SUCURSAL,
                                   PCK_DATOS.FC_CONS_AUXILIAR AUXILIAR,
                                   CASE WHEN '||UN_MES||'=12
                                        THEN -1
                                        ELSE 0
                                   END CIERRE
                            FROM PLANAAJUSTESCONSULTA INNER JOIN  RS3
                                         ON PLANAAJUSTESCONSULTA.COMPANIA =  RS3.COMPANIA
                                         AND PLANAAJUSTESCONSULTA.ANO =  RS3.ANO
                                         AND PLANAAJUSTESCONSULTA.CODIGO_CUENTA =  RS3.CUENTAACERRAR
                            WHERE PLANAAJUSTESCONSULTA.COMPANIA     = '''||UN_COMPANIA||'''
                              AND PLANAAJUSTESCONSULTA.ANO            = '||UN_ANIO||'
                              AND PLANAAJUSTESCONSULTA.CODIGO_CUENTA IN ('||MI_STRCUENTA||')
                            GROUP BY PLANAAJUSTESCONSULTA.COMPANIA,
                                     PLANAAJUSTESCONSULTA.ANO,
                                     PLANAAJUSTESCONSULTA.TIPO_CPTE,
                                     PLANAAJUSTESCONSULTA.COMPROBANTE,
                                     RS3.CUENTAACERRAR,
                                     PLANAAJUSTESCONSULTA.FECHA,
                                     ''D'',
                                     0,
                                     PCK_DATOS.FC_CONS_CENTRO,
                                     PCK_DATOS.FC_CONS_TERCERO,
                                     PCK_DATOS.FC_CONS_SUCURSAL,
                                     PCK_DATOS.FC_CONS_AUXILIAR,
                                     CASE WHEN '||UN_MES||'=12
                                          THEN -1
                                          ELSE 0
                                     END 
                           UNION ALL 
                           SELECT '''||UN_COMPANIA||''' COMPANIA,
                                  '||UN_ANIO||' ANO,
                                  '''||UN_TIPOCOMPROBANTE||''' TIPO_CPTE,
                                  '||UN_NUMERO||' COMPROBANTE,
                                  MAX(PLANAAJUSTESCONSULTA.CONSECUTIVO)+2 CONSECUTIVO,
                                  RS3.CONTRACUENTA CUENTA,
                                  RS3.CONTRACUENTA CODIGO_CUENTA,
                                  TO_DATE('''||TO_CHAR(UN_FECHA,'DD/MM/YYYY')||''',''DD/MM/YYYY'') FECHA,
                                  ''C'' NATURALEZA,
                                  0 VALOR_CREDITO,
                                  PCK_SYSMAN_UTL.FC_ROUND(NVL(SUM(VALOR_CREDITO-VALOR_DEBITO),0),2)*-1 VALOR_DEBITO,
                                  PCK_DATOS.FC_CONS_CENTRO CENTRO_COSTO,
                                  PCK_DATOS.FC_CONS_TERCERO TERCERO,
                                  PCK_DATOS.FC_CONS_SUCURSAL SUCURSAL,
                                  PCK_DATOS.FC_CONS_AUXILIAR AUXILIAR,
                                  CASE WHEN '||UN_MES||'=12
                                       THEN -1
                                       ELSE 0
                                  END CIERRE
                           FROM PLANAAJUSTESCONSULTA INNER JOIN  RS3
                                         ON PLANAAJUSTESCONSULTA.COMPANIA =  RS3.COMPANIA
                                         AND PLANAAJUSTESCONSULTA.ANO =  RS3.ANO
                                         AND PLANAAJUSTESCONSULTA.CODIGO_CUENTA =  RS3.CUENTAACERRAR
                           WHERE PLANAAJUSTESCONSULTA.COMPANIA     = '''||UN_COMPANIA||'''
                             AND PLANAAJUSTESCONSULTA.ANO            = '||UN_ANIO||'
                             AND PLANAAJUSTESCONSULTA.CODIGO_CUENTA IN ('||MI_STRCUENTA||')
                           GROUP BY '''||UN_COMPANIA||''',
                                    '||UN_ANIO||',
                                    '''||UN_TIPOCOMPROBANTE||''',
                                    '||UN_NUMERO||',
                                    RS3.CONTRACUENTA,
                                    TO_DATE('''||TO_CHAR(UN_FECHA,'DD/MM/YYYY')||''',''DD/MM/YYYY''),
                                    ''C'',
                                    0,
                                    PCK_DATOS.FC_CONS_CENTRO,
                                    PCK_DATOS.FC_CONS_TERCERO,
                                    PCK_DATOS.FC_CONS_SUCURSAL,
                                    PCK_DATOS.FC_CONS_AUXILIAR,
                                    CASE WHEN '||UN_MES||'=12
                                         THEN -1
                                         ELSE 0
                                    END)
                     WHERE (SELECT PCK_SYSMAN_UTL.FC_ROUND(NVL(SUM(VALOR_CREDITO-VALOR_DEBITO),0),2) UTILIDAD
                            FROM PLANAAJUSTESCONSULTA
                            WHERE COMPANIA      = '''||UN_COMPANIA||'''
                              AND ANO           = '||UN_ANIO||'
                              AND CODIGO_CUENTA IN ('||MI_STRCUENTA||'))<=0)
                    ) PLANAAJUSTESCONSULTAFINAL
                    ORDER BY PLANAAJUSTESCONSULTAFINAL.CONSECUTIVO';
                    
    BEGIN 
        SELECT CLASE_CONTABLE 
          INTO MI_CLASE 
        FROM TIPO_COMPROBANTE    
        WHERE COMPANIA = UN_COMPANIA 
          AND CODIGO   = UN_TIPOCOMPROBANTE;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_CLASE:=NULL;
    END;

    BEGIN  
        MI_STRSQL:='SELECT COUNT(*) EXISTE  
                    FROM ('||MI_PLANAJUSTES||')PLANAAJUSTES 
                    WHERE COMPANIA    = ''' || UN_COMPANIA ||''' 
                     AND ANO          = '   || UN_ANIO ||' 
                     AND TIPO_CPTE    = ''' || UN_TIPOCOMPROBANTE ||''' 
                     AND COMPROBANTE  = '   || UN_NUMERO;
        EXECUTE IMMEDIATE MI_STRSQL INTO MI_EXISTE;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD   => SQLCODE,
                  UN_ERROR_COD => PCK_ERRORES.ERRR_CONTABILIDAD_INTNORMAL
                );
    END;
    MI_ACTUALIZAR:=-1;
    IF UN_PLANO NOT IN (0) THEN 
        IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'MANTENER TERCERO INTERFAZ PLANO',PCK_DATOS.FC_MODULOCONTABILIDAD,SYSDATE) ='SI' THEN 
            MI_ACTUALIZAR:= 0;
        END IF;
    END IF;
     
     MI_CONDICION :=' 1 = 0 ';
      BEGIN 
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'RS'
                        ,UN_ACCION  => 'E'
                        ,UN_CONDICION => MI_CONDICION);   
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                 

      END;  
        BEGIN 
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'RS1'
                        ,UN_ACCION  => 'E'
                        ,UN_CONDICION => MI_CONDICION);   
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                 

      END;  
        BEGIN 
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'RS2'
                        ,UN_ACCION  => 'E'
                        ,UN_CONDICION => MI_CONDICION);   
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                 

      END;
    BEGIN 
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'RS3'
                        ,UN_ACCION  => 'E'
                        ,UN_CONDICION => MI_CONDICION);   
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                 

      END;
        BEGIN 
       PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'PLANAAJUSTESCONSULTA'
                        ,UN_ACCION  => 'E'
                        ,UN_CONDICION => MI_CONDICION);   
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_INTERFAZ;                                 

      END;
    
    
    
                              
  
    IF MI_ACTUALIZAR NOT IN (0) THEN 
        MI_PLANAJUSTES:='SELECT  PLANAAJUSTESSIMPLE.COMPANIA,
                                 PLANAAJUSTESSIMPLE.ANO,
                                 PLANAAJUSTESSIMPLE.TIPO_CPTE,
                                 PLANAAJUSTESSIMPLE.COMPROBANTE,
                                 PLANAAJUSTESSIMPLE.CONSECUTIVO,
                                 PLANAAJUSTESSIMPLE.CUENTA,
                                 PLANAAJUSTESSIMPLE.CODIGO_CUENTA,
                                 PLANAAJUSTESSIMPLE.FECHA,
                                 PLANAAJUSTESSIMPLE.NATURALEZA,
                                 PLANAAJUSTESSIMPLE.VALOR_CREDITO, 
                                 PLANAAJUSTESSIMPLE.VALOR_DEBITO,
                                 PLANAAJUSTESSIMPLE.CIERRE,
                                 CASE WHEN PLAN_CONTABLE.MAN_CEN_CTO IN (0) AND '||UN_SIMPLE||' NOT IN (0) 
                                      THEN PCK_DATOS.FC_CONS_CENTRO
                                      ELSE NVL(PLANAAJUSTESSIMPLE.CENTRO_COSTO,PCK_DATOS.FC_CONS_CENTRO)
                                 END CENTRO_COSTO, 
                                 CASE WHEN PLAN_CONTABLE.MAN_AUX_TER IN (0) AND '||UN_SIMPLE||' NOT IN (0) 
                                      THEN PCK_DATOS.FC_CONS_TERCERO
                                      ELSE NVL(PLANAAJUSTESSIMPLE.TERCERO,PCK_DATOS.FC_CONS_TERCERO)
                                 END TERCERO,
                                 CASE WHEN PLAN_CONTABLE.MAN_AUX_TER IN (0) AND '||UN_SIMPLE||' NOT IN (0) 
                                      THEN PCK_DATOS.FC_CONS_SUCURSAL
                                      ELSE NVL(PLANAAJUSTESSIMPLE.SUCURSAL,PCK_DATOS.FC_CONS_SUCURSAL) 
                                 END SUCURSAL,
                                 CASE WHEN PLAN_CONTABLE.MAN_AUX_GEN IN (0) AND '||UN_SIMPLE||' NOT IN (0) 
                                      THEN PCK_DATOS.FC_CONS_AUXILIAR
                                      ELSE NVL(PLANAAJUSTESSIMPLE.AUXILIAR,PCK_DATOS.FC_CONS_AUXILIAR) 
                                 END AUXILIAR,
                                 PLAN_CONTABLE.CUENTA_PPTAL  
                          FROM ('||MI_PLANAJUSTES||' ) PLANAAJUSTESSIMPLE
                            LEFT JOIN  PLAN_CONTABLE 
                              ON  PLANAAJUSTESSIMPLE.COMPANIA      = PLAN_CONTABLE.COMPANIA
                              AND PLANAAJUSTESSIMPLE.ANO           = PLAN_CONTABLE.ANO 
                              AND PLANAAJUSTESSIMPLE.CODIGO_CUENTA = PLAN_CONTABLE.CODIGO 
                          WHERE PLANAAJUSTESSIMPLE.COMPANIA    = '''||UN_COMPANIA||''' 
                            AND PLANAAJUSTESSIMPLE.ANO         = '||UN_ANIO||' 
                            AND PLANAAJUSTESSIMPLE.TIPO_CPTE   = '''||UN_TIPOCOMPROBANTE||''' 
                            AND PLANAAJUSTESSIMPLE.COMPROBANTE = '||UN_NUMERO;
    ELSE
        MI_PLANAJUSTES:='SELECT  PLANAAJUSTESSIMPLE.COMPANIA,
                                PLANAAJUSTESSIMPLE.ANO,
                                PLANAAJUSTESSIMPLE.TIPO_CPTE,
                                PLANAAJUSTESSIMPLE.COMPROBANTE,
                                PLANAAJUSTESSIMPLE.CONSECUTIVO,
                                PLANAAJUSTESSIMPLE.CUENTA,
                                PLANAAJUSTESSIMPLE.CODIGO_CUENTA,
                                PLANAAJUSTESSIMPLE.FECHA,
                                PLANAAJUSTESSIMPLE.NATURALEZA,
                                PLANAAJUSTESSIMPLE.VALOR_CREDITO, 
                                PLANAAJUSTESSIMPLE.VALOR_DEBITO,
                                PLANAAJUSTESSIMPLE.CIERRE,
                                PLANAAJUSTESSIMPLE.CENTRO_COSTO, 
                                PLANAAJUSTESSIMPLE.TERCERO,
                                PLANAAJUSTESSIMPLE.SUCURSAL,
                                PLANAAJUSTESSIMPLE.AUXILIAR,
                                NULL CUENTA_PPTAL 
                          FROM ('||MI_PLANAJUSTES||' ) PLANAAJUSTESSIMPLE
                          WHERE PLANAAJUSTESSIMPLE.COMPANIA    = '''||UN_COMPANIA||''' 
                            AND PLANAAJUSTESSIMPLE.ANO         = '||UN_ANIO||' 
                            AND PLANAAJUSTESSIMPLE.TIPO_CPTE   = '''||UN_TIPOCOMPROBANTE||''' 
                            AND PLANAAJUSTESSIMPLE.COMPROBANTE = '||UN_NUMERO;

    END IF;
    MI_PLANACIERREMES:=' SELECT  PLANACIERREMES.COMPANIA COMPANIA,
                               PLANACIERREMES.ANO,
                               PLANACIERREMES.TIPO_CPTE,
                               PLANACIERREMES.COMPROBANTE,
                               PLANACIERREMES.CUENTA, 
                               MAX(PLANACIERREMES.CONSECUTIVO) CONSECUTIVO,
                               TO_DATE(TO_CHAR(PLANACIERREMES.FECHA,''DD/MM/YYYY''),''DD/MM/YYYY'') FECHA,
                               PLANACIERREMES.NATURALEZA,
                               PLANACIERREMES.CUENTA_PPTAL CUENTAPPTAL,
                               SUM(PLANACIERREMES.VALOR_DEBITO) VALOR_DEBITO,
                               SUM(PLANACIERREMES.VALOR_CREDITO) VALOR_CREDITO,
                               CASE WHEN SUM(PLANACIERREMES.VALOR_DEBITO-PLANACIERREMES.VALOR_CREDITO)>0
                                    THEN SUM(PLANACIERREMES.VALOR_DEBITO-PLANACIERREMES.VALOR_CREDITO)
                                    ELSE 0
                               END DEBITO_EQUIV,
                               CASE WHEN CUENTA_PPTAL IS NULL
                                    THEN 0
                                    ELSE CASE WHEN SUM(PLANACIERREMES.VALOR_DEBITO-PLANACIERREMES.VALOR_CREDITO)>0
                                              THEN SUM(PLANACIERREMES.VALOR_DEBITO-PLANACIERREMES.VALOR_CREDITO)
                                              ELSE 0
                                         END 
                               END EJECUCION_DEBITO,
                               CASE WHEN SUM(PLANACIERREMES.VALOR_CREDITO-PLANACIERREMES.VALOR_DEBITO) >0
                                    THEN SUM(PLANACIERREMES.VALOR_CREDITO-PLANACIERREMES.VALOR_DEBITO)
                                    ELSE 0
                               END CREDITO_EQUIV,
                               CASE WHEN CUENTA_PPTAL IS NULL
                                    THEN 0
                                    ELSE CASE WHEN SUM(PLANACIERREMES.VALOR_CREDITO-PLANACIERREMES.VALOR_DEBITO) >0
                                              THEN SUM(PLANACIERREMES.VALOR_CREDITO-PLANACIERREMES.VALOR_DEBITO)
                                              ELSE 0
                                         END 
                               END EJECUCION_CREDITO,
                               PLANACIERREMES.CENTRO_COSTO,
                               PLANACIERREMES.TERCERO,   
                               PLANACIERREMES.SUCURSAL, 
                               PLANACIERREMES.AUXILIAR, 
                               PLANACIERREMES.CIERRE,
                               COMPANIA.PAIS,
                               COMPANIA.DEPARTAMENTO,
                               COMPANIA.CIUDAD
                        FROM ('||MI_PLANAJUSTES||')PLANACIERREMES
                          INNER JOIN COMPANIA 
                            ON  PLANACIERREMES.COMPANIA = COMPANIA.CODIGO
                        WHERE PLANACIERREMES.COMPANIA    = '''||UN_COMPANIA||''' 
                          AND PLANACIERREMES.ANO         = '||UN_ANIO||'
                          AND PLANACIERREMES.TIPO_CPTE   = '''||UN_TIPOCOMPROBANTE||'''
                          AND PLANACIERREMES.COMPROBANTE = '||UN_NUMERO||'
                          AND PLANACIERREMES.CONSECUTIVO >= 0 
                        GROUP BY  PLANACIERREMES.COMPANIA,  
                                  PLANACIERREMES.ANO,  
                                  PLANACIERREMES.TIPO_CPTE,   
                                  PLANACIERREMES.COMPROBANTE,  
                                  PLANACIERREMES.CUENTA, 
                                  PLANACIERREMES.CODIGO_CUENTA,
                                  PLANACIERREMES.FECHA,  
                                  PLANACIERREMES.NATURALEZA, 
                                  CASE WHEN SUBSTR(PLANACIERREMES.CODIGO_CUENTA,0,2)=''11''
                                       THEN '' ''
                                       ELSE (CASE WHEN PLANACIERREMES.VALOR_DEBITO NOT IN (0)
                                                  THEN ''D'' 
                                                  ELSE ''C''
                                             END)
                                  END,
                                  PLANACIERREMES.CUENTA_PPTAL,
                                  PLANACIERREMES.CENTRO_COSTO, 
                                  PLANACIERREMES.TERCERO, 
                                  PLANACIERREMES.SUCURSAL, 
                                  PLANACIERREMES.AUXILIAR, 
                                  PLANACIERREMES.CIERRE,
                                  COMPANIA.PAIS,
                                  COMPANIA.DEPARTAMENTO,
                                  COMPANIA.CIUDAD ';
    BEGIN 
        MI_STRSQL:= 'SELECT SUM(VALOR_DEBITO) SVALOR_DEBITO,
                            SUM(VALOR_CREDITO) SVALOR_CREDITO 
                     FROM ('||MI_PLANACIERREMES||') PLANACIERREMES 
                     WHERE COMPANIA    = '''||UN_COMPANIA||''' 
                       AND ANO         = '||UN_ANIO||' 
                       AND TIPO_CPTE   = '''||UN_TIPOCOMPROBANTE||'''  
                       AND COMPROBANTE = '||UN_NUMERO;
        EXECUTE IMMEDIATE MI_STRSQL INTO MI_SVALOR_DEBITO, MI_SVALOR_CREDITO ;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
        MI_SVALOR_DEBITO :=0;
        MI_SVALOR_CREDITO :=0;
    END;
    IF ABS(TRUNC(MI_SVALOR_DEBITO*100)/100-TRUNC(MI_SVALOR_CREDITO*100)/100)>2 
        AND PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'INTERFAZ PERMITE GENERAR COMPROBANTE DESCUADRADO',PCK_DATOS.FC_MODULOCONTABILIDAD,SYSDATE) = 'NO' 
        AND UN_GENERA_COMP_DESC IN (0) THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
    ELSE
        BEGIN
            IF ABS(TRUNC(MI_SVALOR_DEBITO*100)/100-TRUNC(MI_SVALOR_CREDITO*100)/100)>2 THEN 
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERRR_CONTABILIDAD_COMPDESC
                    );
        END; 
        MI_DBLVALORDOCUMENTO:= NVL(TRUNC(MI_SVALOR_DEBITO*100)/100,0);
        IF UN_TERCERO IS NULL THEN 
            MI_TERCERO := PCK_DATOS.FC_CONS_TERCERO;
        ELSE 
            MI_TERCERO := UN_TERCERO;
        END IF;
        IF UN_SUCURSAL IS NULL THEN 
            MI_SUCURSAL := PCK_DATOS.FC_CONS_SUCURSAL;
        ELSE 
            MI_SUCURSAL := UN_SUCURSAL;
        END IF;
        BEGIN  
            BEGIN 
                MI_TABLA:='COMPROBANTE_CNT';
                MI_CAMPOS:='COMPANIA,
                            ANO,
                            TIPO,
                            NUMERO,
                            FECHA,
                            DESCRIPCION,
                            TERCERO,
                            SUCURSAL,
                            VLR_BASE,
                            CREATED_BY,
                            DATE_CREATED,
                            ANULADO,
                            VLR_DOCUMENTO,
                            TEXTO,
                            NUMEROCONTRATO,
                            VLRAGIRAR,
                            NRO_DOCUMENTO,
                            FECHA_VCN_DOC';
                MI_VALORES := ''''||UN_COMPANIA||''','
                                  ||UN_ANIO||','''
                                  ||UN_TIPOCOMPROBANTE||''','
                                  ||UN_NUMERO||',TO_DATE('''
                                  ||TO_CHAR(UN_FECHA,'DD/MM/YYYY')||''',''DD/MM/YYYY''),'''
                                  ||SUBSTR(UN_DESCRIPCION, 1, 255)||''','''
                                  ||MI_TERCERO||''','''
                                  ||MI_SUCURSAL||''',0,'''
                                  ||UN_CREADOR||''',SYSDATE,0,TO_NUMBER('''
                                  ||MI_DBLVALORDOCUMENTO||''',''999999999999999999.99''),'''
                                  ||UN_TEXTO||''',NULL,TO_NUMBER('''
                                  ||MI_DBLVALORDOCUMENTO||''',''999999999999999999.99''),0,TO_DATE('''
                                  ||TO_CHAR(UN_FECHA+NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'DIAS VENCIMIENTO COMPROBANTE CONTABLE',PCK_DATOS.FC_MODULOCONTABILIDAD,SYSDATE),0),'DD/MM/YYYY')||''',''DD/MM/YYYY'')';
                MI_RTA_ACME:= PCK_DATOS.FC_ACME(UN_TABLA    => MI_TABLA,
                                                UN_ACCION   => 'I', 
                                                UN_CAMPOS   => MI_CAMPOS, 
                                                UN_VALORES  => MI_VALORES);
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END;                                
            IF MI_RTA_ACME <=0 THEN 
                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
            END IF;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTABILIDAD_HEADCOMP,
                    UN_TABLAERROR => MI_TABLA
                  );
        END;
    END IF;
    MI_DBLGIRAR := PCK_CONTABILIDAD1.FC_CALCULARVLRGIRAR(UN_COMPANIA => UN_COMPANIA,
                                                          UN_ANIO     => UN_ANIO,
                                                          UN_TIPO     => UN_TIPOCOMPROBANTE,
                                                          UN_NUMERO   => UN_NUMERO,
                                                          UN_CLASE    => MI_CLASE,
                                                          UN_VALORAGIRAR => 0);
    MI_DBLGIRAR  := PCK_SYSMAN_UTL.FC_ROUND(NVL(MI_DBLGIRAR,0),2);
    MI_TABLA     := 'COMPROBANTE_CNT';
    MI_CAMPOS    :=   ' VLRAGIRAR = '||MI_DBLGIRAR
                   ||' ,DATE_MODIFIED = SYSDATE
                       ,MODIFIED_BY   = '''||UN_CREADOR||''' ';
    MI_CONDICION := '    COMPANIA = '''||UN_COMPANIA ||'''
                     AND ANO      = '||UN_ANIO||' 
                     AND TIPO     = '''||UN_TIPOCOMPROBANTE||'''
                     AND NUMERO   = '||UN_NUMERO;
    MI_RTA_ACME:= PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                    UN_ACCION    => 'M', 
                                    UN_CAMPOS    => MI_CAMPOS, 
                                    UN_CONDICION => MI_CONDICION);                                 
    BEGIN  
        MI_STRSQL:='SELECT COUNT(*) EXISTE  
                     FROM ('||MI_PLANACIERREMES||')PLANACIERRE 
                    WHERE COMPANIA     = '''||UN_COMPANIA||''' 
                      AND ANO          = '||UN_ANIO||' 
                      AND TIPO_CPTE    = '''||UN_TIPOCOMPROBANTE||''' 
                      AND COMPROBANTE  = '||UN_NUMERO;
        EXECUTE IMMEDIATE MI_STRSQL INTO MI_EXISTE;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_EXISTE:=0;
    END;                            
    MI_TABLA    := 'DETALLE_COMPROBANTE_CNT';
    MI_RTA_ACME := 0;
    
        BEGIN
            MI_CAMPOS :=   'COMPANIA,
                          ANO,
                          TIPO_CPTE, 
                          COMPROBANTE,
                          CUENTA,
                          CONSECUTIVO,
                          FECHA,
                          NATURALEZA,
                          CUENTAPPTAL,
                          VALOR_DEBITO,
                          VALOR_CREDITO,
                          DEBITO_EQUIV,
                          EJECUCION_DEBITO,
                          CREDITO_EQUIV,
                          EJECUCION_CREDITO,
                          CENTRO_COSTO,
                          TERCERO,
                          SUCURSAL,
                          AUXILIAR,
                          CIERRE,
                          PAIS,
                          DEPARTAMENTO,
                          CIUDAD,
                          DATE_CREATED,
                          CREATED_BY';
                          
            MI_STRSQL := 'SELECT COMPANIA, ANO, TIPO_CPTE, COMPROBANTE, CUENTA, CONSECUTIVO, FECHA, NATURALEZA, 
                            CUENTAPPTAL, VALOR_DEBITO, VALOR_CREDITO, DEBITO_EQUIV, EJECUCION_DEBITO, CREDITO_EQUIV, 
                            EJECUCION_CREDITO, CENTRO_COSTO, TERCERO, SUCURSAL, AUXILIAR, CIERRE, PAIS, DEPARTAMENTO, 
                            CIUDAD, SYSDATE, '''||UN_CREADOR||''' 
                            FROM ('||MI_PLANACIERREMES||') PLANACIERREMES' ;
                            
       PCK_SYSMAN_CTX.SET_CIERRECONTABLE(1);
            MI_RTA_ACME:= MI_RTA_ACME +
                        PCK_DATOS.FC_ACME( UN_TABLA     => MI_TABLA,
                                           UN_ACCION    => 'IS', 
                                           UN_CAMPOS    => MI_CAMPOS, 
                                           UN_VALORES   => MI_STRSQL);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
            -- Siempre apagar variable de contexto en caso de error
            PCK_SYSMAN_CTX.RESET_CIERRECONTABLE;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE,
                    UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTABILIDAD_HEADCOMP,
                    UN_TABLAERROR => MI_TABLA
                  );
        END;
        -- Importante: apagar variable ANTES de ejecutar los siguientes procesos
        PCK_SYSMAN_CTX.RESET_CIERRECONTABLE;
        PCK_CONTABILIDAD6.PR_CALCULAR_VALORAGIRAR( UN_COMPANIA           => UN_COMPANIA
                                                ,UN_ANIO             => UN_ANIO
                                                ,UN_TIPO             => UN_TIPOCOMPROBANTE
                                                ,UN_NUMERO           => UN_NUMERO
                                                ,UN_VLRDOCUMENTO     => 0
                                                ,UN_VLRGIRARDG       => 0);
                                                
        PCK_CONTABILIDAD.PR_CUADRECONTA_AUX(UN_COMPANIA          => UN_COMPANIA
                                            ,UN_ANIO             => UN_ANIO
                                            ,UN_MES_INI          => UN_MES
                                            ,UN_MES_FIN          => UN_MES+1);                                            
                                                
    BEGIN 
        IF MI_EXISTE <> MI_RTA_ACME THEN 
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END IF;
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN 
       -- Asegurar que vuelva a 0 si se levanta este error
       PCK_SYSMAN_CTX.RESET_CIERRECONTABLE;
       PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERRR_CONTABILIDAD_CREADETCIE
            );
    END; 
 RETURN -1;
END FC_INTERFAZ_CONTABLE_HACT;

FUNCTION FC_CIERRECONTABLEIMPUESTOS
   /*
    NAME              : FC_CIERRECONTABLEIMPUESTOS
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : 
    DATE MIGRADOR     : 
    TIME              : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : .

    MODIFICATIONS     : 
    @NAME:  cierreContabledeImpuestos
    @METHOD:  put
  */ 
(
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOCOMPROBANTE IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_NUMERO          IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_ANIO            IN PCK_SUBTIPOS.TI_ANIO,
    UN_FECHA           IN DATE,
    UN_MES             IN PCK_SUBTIPOS.TI_MES,
    UN_CENTRO_COSTO    IN PCK_SUBTIPOS.TI_CENTRO_COSTO,
    UN_USUARIO         IN VARCHAR2 
)RETURN PCK_SUBTIPOS.TI_LOGICO
AS
    MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR; 
    MI_CAMPOS         PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES        PCK_SUBTIPOS.TI_VALORES;
    MI_RTAPLANO       CLOB;
    MI_RTA            PCK_SUBTIPOS.TI_RTA_ACME;
BEGIN
 IF PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'PERMITE CIERRE DE CUENTAS DE IMPUESTOS',PCK_DATOS.FC_MODULOCONTABILIDAD,SYSDATE) = 'SI' THEN 

    BEGIN
      MI_VALORES:= ' SELECT COMPANIA, ANO, 
                              ''' || UN_TIPOCOMPROBANTE || ''' TIPO_CPTE, 
                              '   || UN_NUMERO          || ' COMPROBANTE, 
                              CUENTA, 
                              ROWNUM CONSECUTIVO,
                              TO_DATE(''' || TO_CHAR(UN_FECHA,'DD/MM/YYYY') || ''',''DD/MM/YYYY'') FECHA,
                              NATURALEZA,
                              TERCERO,
                              SUCURSAL, 
                              CENTRO_COSTO, 
                              AUXILIAR, 
                              REFERENCIA, 
                              FUENTE_RECURSO,
                              -1 CIERRE,
                              CASE WHEN CLASE =''CONTRACUENTA'' AND SALDO<0 
                                   THEN ABS(SALDO)
                                   ELSE CASE WHEN CLASE=''CUENTA'' AND SALDO>0
                                             THEN SALDO
                                             ELSE 0
                                        END
                              END VALOR_DEBITO,
                              CASE WHEN CLASE =''CUENTA'' AND SALDO<0 
                                   THEN ABS(SALDO)
                                   ELSE CASE WHEN CLASE=''CONTRACUENTA'' AND SALDO>0
                                             THEN SALDO
                                             ELSE 0
                                        END
                              END VALOR_CREDITO
                       FROM 
                            (
                            SELECT ''CONTRACUENTA'' CLASE,
                                   CIERREDEMES.COMPANIA,
                                   CIERREDEMES.ANO, 
                                   CIERREDEMES.CONTRACUENTA CUENTA,
                                   CIERREDEMES.TERCEROIMPUESTO TERCERO,
                                   CASE WHEN NVL(CIERREDEMES.TERCEROIMPUESTO,''999999999999999999'')=''999999999999999999''
                                        THEN ''999''
                                        ELSE ''001''
                                    END SUCURSAL,
                                    SALDO_AUX_CONTABLE.CENTRO_COSTO,
                                    SALDO_AUX_CONTABLE.AUXILIAR,
                                    SALDO_AUX_CONTABLE.REFERENCIA,
                                    SALDO_AUX_CONTABLE.FUENTE_RECURSO,   
                                    SALDO_AUX_CONTABLE.NATURALEZA,
                                    SUM(SALDO_AUX_CONTABLE.SALDO12) SALDO
                            FROM HEADERCIERRE CIERREDEMES
                            INNER JOIN SALDO_AUX_CONTABLE
                                ON CIERREDEMES.COMPANIA       = SALDO_AUX_CONTABLE.COMPANIA
                                AND CIERREDEMES.ANO           = SALDO_AUX_CONTABLE.ANO
                                AND CIERREDEMES.CUENTAACERRAR = SALDO_AUX_CONTABLE.CODIGO
                            WHERE CIERREDEMES.COMPANIA        = ''' || UN_COMPANIA || '''
                              AND CIERREDEMES.ANO             = '   || UN_ANIO     || '
                              AND CIERREDEMES.CIERREIMPUESTOS NOT IN (0)
                              AND TIPOCOMPROBANTE             = ''' || UN_TIPOCOMPROBANTE || '''
                              AND CONDICION                   IS NULL
                            GROUP BY ''CONTRACUENTA'',
                                   CIERREDEMES.COMPANIA,
                                   CIERREDEMES.ANO, 
                                   CIERREDEMES.CONTRACUENTA ,
                                   CIERREDEMES.TERCEROIMPUESTO,
                                   CASE WHEN NVL(CIERREDEMES.TERCEROIMPUESTO,''999999999999999999'')=''999999999999999999''
                                        THEN ''999''
                                        ELSE ''001''
                                    END ,
                                    SALDO_AUX_CONTABLE.CENTRO_COSTO,
                                    SALDO_AUX_CONTABLE.AUXILIAR,
                                    SALDO_AUX_CONTABLE.REFERENCIA,
                                    SALDO_AUX_CONTABLE.FUENTE_RECURSO,
                                    SALDO_AUX_CONTABLE.NATURALEZA
                        UNION ALL
                            SELECT ''CUENTA'' CLASE,
                                   CIERREDEMES.COMPANIA,
                                   CIERREDEMES.ANO, 
                                   CIERREDEMES.CUENTAACERRAR CUENTA,
                                    SALDO_AUX_CONTABLE.TERCERO,
                                    SALDO_AUX_CONTABLE.SUCURSAL,
                                    SALDO_AUX_CONTABLE.CENTRO_COSTO,
                                    SALDO_AUX_CONTABLE.AUXILIAR,
                                    SALDO_AUX_CONTABLE.REFERENCIA,
                                    SALDO_AUX_CONTABLE.FUENTE_RECURSO,
                                    SALDO_AUX_CONTABLE.NATURALEZA,
                                    SALDO_AUX_CONTABLE.SALDO12 SALDO             
                            FROM HEADERCIERRE CIERREDEMES
                            INNER JOIN SALDO_AUX_CONTABLE
                                ON CIERREDEMES.COMPANIA       = SALDO_AUX_CONTABLE.COMPANIA
                                AND CIERREDEMES.ANO           = SALDO_AUX_CONTABLE.ANO
                                AND CIERREDEMES.CUENTAACERRAR = SALDO_AUX_CONTABLE.CODIGO
                            WHERE CIERREDEMES.COMPANIA        = ''' || UN_COMPANIA || '''
                              AND CIERREDEMES.ANO             = '   || UN_ANIO     || '
                              AND CIERREDEMES.CIERREIMPUESTOS NOT IN (0)
                              AND TIPOCOMPROBANTE             = ''' || UN_TIPOCOMPROBANTE || '''
                              AND CONDICION                   IS NULL
                            )';

        MI_CAMPOS := ' COMPANIA, 
                       ANO, 
                       TIPO_CPTE, 
                       COMPROBANTE,
                       CUENTA, 
                       CONSECUTIVO,                            
                       FECHA,
                       NATURALEZA,
                       TERCERO, 
                       SUCURSAL, 
                       CENTRO_COSTO,
                       AUXILIAR,
                       REFERENCIA,
                       FUENTE_RECURSOS,
                       CIERRE,
                       VALOR_DEBITO,
                       VALOR_CREDITO';

        MI_RTA  := PCK_DATOS.FC_ACME(UN_TABLA   => 'TEMP_PLANA_AJUSTES',
                                     UN_ACCION  => 'IS',
                                     UN_CAMPOS  => MI_CAMPOS,
                                     UN_VALORES => MI_VALORES);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD => SQLCODE
                  , UN_TABLAERROR => 'TEMP_PLANA_AJUSTES'
                  , UN_ERROR_COD => PCK_ERRORES.ERR_CONTA_CIERREIMPUESTO
                  , UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;

    MI_RTAPLANO:=  TO_CLOB(PCK_CONTABILIZAR.FC_CONTABILIZAR( UN_COMPANIA         => UN_COMPANIA
                                                            ,UN_TIPOCOMPROBANTE  => UN_TIPOCOMPROBANTE
                                                            ,UN_NUMERO           => UN_NUMERO 
                                                            ,UN_ANO              => UN_ANIO
                                                            ,UN_FECHA            => UN_FECHA
                                                            ,UN_TERCERO          => PCK_DATOS.CONS_TERCERO
                                                            ,UN_SUCURSAL         => PCK_DATOS.CONS_SUCURSAL
                                                            ,UN_DESCRIPCION      => 'Interface Cierre de Impuestos de '||PCK_SYSMAN_UTL.FC_NOMBRE_MES(UN_NUMERO_MES => UN_MES) ||' de '||UN_ANIO
                                                            ,UN_USUARIO          => UN_USUARIO 
                                                            ,UN_SIMPLE           => CASE WHEN NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => UN_COMPANIA, 
                                                                                                                        UN_NOMBRE    => 'SIMPLIFICAR INTERFACE CIERRE' ,
                                                                                                                        UN_MODULO    => PCK_DATOS.FC_MODULOCONTABILIDAD, 
                                                                                                                        UN_FECHA_PAR => SYSDATE ), 'SI') = 'SI' THEN -1 ELSE 0 END
                                                            ,UN_INDIMPRESION     => 0
                                                            ,UN_INTOMITIRPPTAL   => -1
                                                             )) ;
  END IF;
 RETURN -1;
END FC_CIERRECONTABLEIMPUESTOS;

FUNCTION FC_VERIFICAPERIODO
    /*
      NAME              : FC_VERIFICAPERIODO En Access --> FC_VERIFICAPERIODOPPTAL
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : NICOLÁS GÓMEZ BARBOSA
      DATE MIGRADOR     : 28/06/2016
      TIME              : 10:30 AM
      SOURCE MODULE     : PRESUPUESTO
      MODIFIER          : YEISSON ALEJANDRO ROJAS RUIZ
      DATE MODIFIED     : 11/01/2017
      TIME              : 10:00 AM
      MODIFICATIONS     : CORRECCIÓN SEGÚN ESTÁNDAR DE PROGRAMACIÓN.
      DESCRIPTION       : Devuelve -1 si un determinado periodo pptal está activo y 0 si no existe o está cerrado.
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                          UN_ANO      => AÑO POR EL QUE SE VA A FILTRAR EN LAS CONSULTAS.
                          UN_MES      => MES POR EL QUE SE VA A FILTRAR EN LAS CONSULTAS.

      @NAME:    verificarPeriodo
      @METHOD:  GET
    */
  (
    UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO         IN PCK_SUBTIPOS.TI_ANIO,
    UN_MES         IN PCK_SUBTIPOS.TI_MES DEFAULT NULL
  )RETURN PCK_SUBTIPOS.TI_LOGICO 
AS
    MI_I           PCK_SUBTIPOS.TI_ENTERO;
    MI_ESTADO      VARCHAR2(2 CHAR);
    MI_REEMPLAZOS  PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
    BEGIN
      SELECT  COUNT(*) 
      INTO    MI_I
      FROM    ANO
      WHERE   COMPANIA = UN_COMPANIA
        AND   NUMERO   = UN_ANO;

      EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_REEMPLAZOS(1).CLAVE := 'ANO';
        MI_REEMPLAZOS(1).VALOR := UN_ANO;
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD => SQLCODE
                  , UN_TABLAERROR => 'ANO'
                  , UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_NOEXISTEANO
                  , UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;
    --VALIDA EL AÑO DE CONTABILIDAD POR QUE NO SE
    MI_ESTADO := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOANO(UN_COMPANIA=> UN_COMPANIA, 
                                                       UN_ANO     => UN_ANO, 
                                                       UN_MODULO  => PCK_DATOS.FC_MODULOCONTABILIDAD, 
                                                       UN_PROCESO => 1);
    IF MI_ESTADO <>'A' THEN 
    --10/07/2018 Se documenta excepcion, porque se necesita recibir en la funcion 0 o -1 
     /* BEGIN
        RAISE  PCK_EXCEPCIONES.EXC_GENERAL;  
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN 
          MI_REEMPLAZOS(1).CLAVE := 'ANO';
          MI_REEMPLAZOS(1).VALOR := UN_ANO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD => SQLCODE
                      , UN_TABLAERROR => 'ANO'
                      , UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_ANOCERRADO
                      , UN_REEMPLAZOS => MI_REEMPLAZOS
                      );   
      END;*/
      RETURN 0;
    END IF;
    IF UN_MES IS NULL THEN
      RETURN -1;
    END IF;
    BEGIN
        SELECT COUNT(*) 
          INTO MI_I
          FROM MES
         WHERE COMPANIA = UN_COMPANIA
           AND ANO      = UN_ANO
           AND NUMERO   = UN_MES;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_REEMPLAZOS(1).CLAVE := 'ANO';
        MI_REEMPLAZOS(1).VALOR := UN_ANO;
        MI_REEMPLAZOS(2).CLAVE := 'MES';
        MI_REEMPLAZOS(2).VALOR := UN_MES;
        PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD => SQLCODE
                  , UN_TABLAERROR => 'MES'
                  , UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_NOEXISTEMES
                  , UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
    END;
    MI_ESTADO := PCK_SYSMAN_UTL.FC_VERIFICAR_ESTADOMES(UN_COMPANIA=> UN_COMPANIA, 
                                                       UN_ANO     => UN_ANO, 
                                                       UN_MES     => UN_MES, 
                                                       UN_MODULO  => PCK_DATOS.FC_MODULOCONTABILIDAD, 
                                                       UN_PROCESO => 1);
    IF MI_ESTADO <>'A' THEN 
       --10/07/2018 Se documenta excepcion, porque se necesita recibir en la funcion 0 o -1 
      /*  BEGIN
          RAISE  PCK_EXCEPCIONES.EXC_GENERAL;  
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_GENERAL THEN 
            MI_REEMPLAZOS(1).CLAVE := 'ANO';
            MI_REEMPLAZOS(1).VALOR := UN_ANO;
            MI_REEMPLAZOS(2).CLAVE := 'MES';
            MI_REEMPLAZOS(2).VALOR := UN_MES;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD => SQLCODE
                      , UN_TABLAERROR => 'ANO'
                      , UN_ERROR_COD => PCK_ERRORES.ERROR_GRAL_MESCERRADO
                      , UN_REEMPLAZOS => MI_REEMPLAZOS
                      ); 
        END;*/
        RETURN 0;
    END IF; 
    RETURN -1;
END FC_VERIFICAPERIODO;

FUNCTION FC_VERIFICARCUENTAS
    /*
      NAME              : FC_VERIFICARCUENTAS 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 10/05/2017
      TIME              : 09:20 AM
      SOURCE MODULE     : CONTABILIDAD
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     : 
      DESCRIPTION       : 
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                          UN_ANO      => AÑO POR EL QUE SE VA A FILTRAR EN LAS CONSULTAS.
                          UN_MES      => MES POR EL QUE SE VA A FILTRAR EN LAS CONSULTAS.

      @NAME:    verificarCuentas
      @METHOD:  GET
    */
(
    UN_COMPANIA        IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO             IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPOCOMPROBANTE IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE
) RETURN PCK_SUBTIPOS.TI_LOGICO 
AS
    MI_REEMPLAZOS    PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_CUENTAACERRAR NUMBER; 
BEGIN
    BEGIN
      SELECT COUNT(CIERREDEMES.CUENTAACERRAR)  
        INTO MI_CUENTAACERRAR   
        FROM HEADERCIERRE CIERREDEMES    
            LEFT JOIN PLAN_CONTABLE     
                ON  CIERREDEMES.COMPANIA      = PLAN_CONTABLE.COMPANIA     
                AND CIERREDEMES.ANO           = PLAN_CONTABLE.ANO     
                AND CIERREDEMES.CUENTAACERRAR = PLAN_CONTABLE.CODIGO  
       WHERE CIERREDEMES.COMPANIA  = UN_COMPANIA    
         AND CIERREDEMES.ANO       = UN_ANO      
         AND PLAN_CONTABLE.CODIGO  IS NULL;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CUENTAACERRAR:=-1;
    END;
    IF MI_CUENTAACERRAR NOT IN (0) THEN 
        BEGIN 
            MI_REEMPLAZOS(0).CLAVE:='CUENTA';
            MI_REEMPLAZOS(0).VALOR:=MI_CUENTAACERRAR;
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                  , UN_TABLAERROR => 'HEADERCIERRE'
                  , UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTABILIDAD_VERIFCUENT1 
                  , UN_REEMPLAZOS => MI_REEMPLAZOS
                  );
        END;
    END IF;
    BEGIN 
        SELECT COUNT (CIERREDEMES.CUENTAACERRAR)
          INTO MI_CUENTAACERRAR  
          FROM HEADERCIERRE CIERREDEMES    
              LEFT JOIN V_PLAN_CONTABLE PLAN_CONTABLE      
                  ON  CIERREDEMES.COMPANIA     = PLAN_CONTABLE.COMPANIA       
                  AND CIERREDEMES.ANO          = PLAN_CONTABLE.ANO       
                  AND CIERREDEMES.CONTRACUENTA = PLAN_CONTABLE.ID   
         WHERE CIERREDEMES.COMPANIA        = UN_COMPANIA    
           AND CIERREDEMES.ANO             = UN_ANO    
           AND CIERREDEMES.TIPOCOMPROBANTE = UN_TIPOCOMPROBANTE 
           AND (PLAN_CONTABLE.ID IS NULL 
                OR (    PLAN_CONTABLE.MOVIMIENTO  IN(0)         
                    AND PLAN_CONTABLE.MAN_AUX_TER IN(0)         
                    AND PLAN_CONTABLE.MAN_AUX_GEN IN(0)         
                    AND PLAN_CONTABLE.MAN_CEN_CTO IN(0)         
                    AND PLAN_CONTABLE.MAN_AUX_REF IN(0)         
                    AND PLAN_CONTABLE.MAN_AUX_FUE IN(0)
                   )
               );
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_CUENTAACERRAR:=-1;
    END;
    IF MI_CUENTAACERRAR  NOT IN(0) THEN 
        BEGIN 
            MI_REEMPLAZOS(0).CLAVE:='CUENTA';
            MI_REEMPLAZOS(0).VALOR:=MI_CUENTAACERRAR;
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD    => SQLCODE
                      , UN_TABLAERROR => 'HEADERCIERRE'
                      , UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTABILIDAD_VERIFCUENT2 
                      , UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
        END;
    END IF;
    RETURN -1;
END FC_VERIFICARCUENTAS;


FUNCTION FC_CIERRECONTABLE_VALIDADO
    /*
      NAME              : FC_CIERRECONTABLE_VALIDADO 
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JAVIER ANDRES RODRIGUEZ RIOS
      DATE MIGRADOR     : 10/05/2017
      TIME              : 10:30 AM
      SOURCE MODULE     : CONTABILIDAD
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     : 
      DESCRIPTION       : 
      PARAMETERS        : UN_COMPANIA => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                          UN_ANO      => AÑO POR EL QUE SE VA A FILTRAR EN LAS CONSULTAS.
                          UN_MES      => MES POR EL QUE SE VA A FILTRAR EN LAS CONSULTAS.

      @NAME:    cierreContableValidado
      @METHOD:  GET
    */
(
    UN_COMPANIA         IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOCOMPROBANTE  IN PCK_SUBTIPOS.TI_TIPOCOMPROBANTE,
    UN_NUMERO           IN PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT,
    UN_ANIO             IN PCK_SUBTIPOS.TI_ANIO,
    UN_FECHA            IN DATE,
    UN_MES              IN PCK_SUBTIPOS.TI_MES,
    UN_CENTRO_COSTO     IN PCK_SUBTIPOS.TI_CENTRO_COSTO,
    UN_GENERA_COMP_DESC IN PCK_SUBTIPOS.TI_LOGICO DEFAULT 0,
    UN_CIERREUTILIDAD   IN PCK_SUBTIPOS.TI_LOGICO,
    UN_CIERREIMPUESTOS  IN PCK_SUBTIPOS.TI_LOGICO,
    UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO 
)RETURN PCK_SUBTIPOS.TI_LOGICO
AS 
    MI_GENEROCONTABLE PCK_SUBTIPOS.TI_LOGICO;
    MI_GENEROIMPUESTO PCK_SUBTIPOS.TI_LOGICO;
    MI_RTA            PCK_SUBTIPOS.TI_LOGICO;
BEGIN
    PCK_SYSMAN_CTX.SET_CIERRECONTABLE(0);
    IF FC_VERIFICAPERIODO(UN_COMPANIA => UN_COMPANIA
                         ,UN_ANO      => UN_ANIO
                         ,UN_MES      => UN_MES) NOT IN (0) 
       AND FC_VERIFICARCUENTAS(UN_COMPANIA        => UN_COMPANIA
                              ,UN_ANO             => UN_ANIO
                              ,UN_TIPOCOMPROBANTE => UN_TIPOCOMPROBANTE) NOT IN (0) THEN 
        IF UN_CIERREUTILIDAD NOT IN (0) THEN 
            MI_GENEROCONTABLE:=FC_CIERRECONTABLE(
                                    UN_COMPANIA         => UN_COMPANIA,
                                    UN_TIPOCOMPROBANTE  => UN_TIPOCOMPROBANTE,
                                    UN_NUMERO           => UN_NUMERO,
                                    UN_ANIO             => UN_ANIO,
                                    UN_FECHA            => UN_FECHA,
                                    UN_MES              => UN_MES,
                                    UN_CENTRO_COSTO     => UN_CENTRO_COSTO,
                                    UN_USUARIO          => UN_USUARIO,
                                    UN_GENERA_COMP_DESC => 0);
        END IF;
        IF UN_CIERREIMPUESTOS NOT IN (0) THEN
            MI_GENEROIMPUESTO:=FC_CIERRECONTABLEIMPUESTOS(
                                    UN_COMPANIA         => UN_COMPANIA,
                                    UN_TIPOCOMPROBANTE  => UN_TIPOCOMPROBANTE,
                                    UN_NUMERO           => CASE WHEN UN_CIERREUTILIDAD NOT IN (0) THEN UN_NUMERO+1 ELSE UN_NUMERO END,
                                    UN_ANIO             => UN_ANIO,
                                    UN_FECHA            => UN_FECHA,
                                    UN_MES              => UN_MES,
                                    UN_CENTRO_COSTO     => UN_CENTRO_COSTO,
                                    UN_USUARIO          => UN_USUARIO);
        END IF;
    END IF;
    IF MI_GENEROIMPUESTO NOT IN (0) 
       OR MI_GENEROCONTABLE NOT IN (0) THEN
         IF UN_MES = 12 THEN 
             MI_RTA:=PCK_SYSMAN_UTL.FC_CAMBIARESTADOANO(
                                    UN_COMPANIA    => UN_COMPANIA
                                   ,UN_ANO         => UN_ANIO
                                   ,UN_MODULO      => PCK_DATOS.FC_MODULOCONTABILIDAD
                                   ,UN_PROCESO     => 1 
                                   ,UN_ESTADO      => 'C' 
                                   ,UN_MODIFIED_BY => UN_USUARIO);
         ELSE 
             MI_RTA:=PCK_SYSMAN_UTL.FC_CAMBIARESTADOMES(
                                    UN_COMPANIA    => UN_COMPANIA
                                   ,UN_ANO         => UN_ANIO
                                   ,UN_MES         => UN_MES
                                   ,UN_MODULO      => PCK_DATOS.FC_MODULOCONTABILIDAD
                                   ,UN_PROCESO     => 1
                                   ,UN_ESTADO      => 'C'
                                   ,UN_MODIFIED_BY => UN_USUARIO);
         END IF;        
    END IF;
        PCK_SYSMAN_CTX.SET_CIERRECONTABLE(0);
    RETURN MI_RTA;
        EXCEPTION WHEN OTHERS THEN
            -- Siempre resetear el contexto ante error
            PCK_SYSMAN_CTX.RESET_CIERRECONTABLE;
        RAISE;
END FC_CIERRECONTABLE_VALIDADO;

FUNCTION FC_DETERIOROCUENTAH
/*
  NAME              : FC_DETERIOROCUENTAH  En Access deterioroCuentaH
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRATION    : 21/04/2021
  TIME              : 10:40 AM
  SOURCE MODULE     : SysmanCT2021.04.07
  MODIFIED BY       :
  MODIFICATIONS     :
  DESCRIPTION       : Función genera el deterioro de cuentas
  PARAMETERS        :
  --Name             :deterioroCuentaH
  --Method           :GET
*/
(
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO          IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MES          IN  PCK_SUBTIPOS.TI_MES,
    UN_FECHACORTE   IN  DATE,
    UN_USUARIO      IN  PCK_SUBTIPOS.TI_USUARIO
)
RETURN CLOB AS
    MI_RETORNO      CLOB;
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_MESES        NUMBER(4,0);
    MI_DIAS         NUMBER(10);
    MI_TIPO         VARCHAR2(5 CHAR);
    MI_STRCUENTAS   VARCHAR2(4000 CHAR) := '';
    MI_STRANTERIOR  VARCHAR2(4000 CHAR) := '**';
    MI_TASA         NUMBER(10,2);
    MI_NUMANOINICIAL NUMBER(4,0) := 1973;
    MI_CUNETASNOCONFIGURADAS BOOLEAN := FALSE;
    MI_CAMPOS  PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES PCK_SUBTIPOS.TI_VALORES;
    MI_MERGEUSING   PCK_SUBTIPOS.TI_MERGEUSING ;
    MI_MERGEENLACE  PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE  PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
BEGIN
     MI_MESES := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                         ,UN_NOMBRE     => 'NUMERO DE MESES VENCIDOS QUE APLICAN DETERIORO NIIF'
                                         ,UN_MODULO     => PCK_DATOS.FC_MODULOCONTABILIDAD
                                         ,UN_FECHA_PAR  => SYSDATE);
     MI_DIAS := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                         ,UN_NOMBRE     => 'NUMERO DE DIAS VENCIDOS QUE APLICAN DETERIORO NIIF'
                                         ,UN_MODULO     => PCK_DATOS.FC_MODULOCONTABILIDAD
                                         ,UN_FECHA_PAR  => SYSDATE);
     MI_TIPO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                         ,UN_NOMBRE     => 'TIPO DE COMPROBANTE DETERIORO NIIF'
                                         ,UN_MODULO     => PCK_DATOS.FC_MODULOCONTABILIDAD
                                         ,UN_FECHA_PAR  => SYSDATE);
    FOR MI_RS IN (SELECT  PLAN_CONTABLE.COMPANIA,
                        PLAN_CONTABLE.ANO,
                        PLAN_CONTABLE.APLICA_DETERIORO,
                        PLAN_CONTABLE.CODIGO,
                        PLAN_CONTABLE.NOMBRE,
                        PLAN_CONTABLE.CODIGO   CUENTA
         FROM PLAN_CONTABLE
         WHERE PLAN_CONTABLE.COMPANIA =UN_COMPANIA
               AND PLAN_CONTABLE.ANO =UN_ANO
               AND PLAN_CONTABLE.APLICA_DETERIORO NOT IN (0)
               AND PLAN_CONTABLE.CLASECUENTA = 'C'
               AND MAN_CEN_CTO + MAN_AUX_TER + MAN_AUX_GEN <> 0
         GROUP BY PLAN_CONTABLE.COMPANIA,
                  PLAN_CONTABLE.ANO,
                  PLAN_CONTABLE.APLICA_DETERIORO,
                  PLAN_CONTABLE.CODIGO,
                  PLAN_CONTABLE.NOMBRE) LOOP
        IF MI_RS.CUENTA <>  MI_STRANTERIOR THEN
             IF LENGTH(MI_STRCUENTAS) > 0 THEN
                MI_STRCUENTAS := MI_STRCUENTAS || ',';
            END IF;
            MI_STRCUENTAS := MI_STRCUENTAS ||MI_RS.CUENTA;
            MI_STRANTERIOR := MI_RS.CUENTA;
        END IF;
    END LOOP;
          MI_CONDICION := ' COMPANIA='''||UN_COMPANIA||'''
                           AND ANO='||UN_ANO||'
                           AND MES='||UN_MES||''  ;
     BEGIN
        BEGIN
            PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETERIORO_CARTERA',
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION );
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_DELETEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;
    MI_TASA := 0;
     MI_CAMPOS := 'COMPANIA,ANO,MES,TIPO_FACTURA,NUMERO_FACTURA,EDAD,TASA,DESCRIPCION,CENTRO_COSTO,TERCERO,SUCURSAL,AUXILIAR,REFERENCIA, MES_FACTURA, CUENTA,
                       CONSECUTIVO, ANO_FACTURA, FECHA_FACTURA, FECHA_VENCIMIENTO,DATE_CREATED,CREATED_BY ';

    IF(NVL(PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'MANEJA CALCULO DETERIORO POR TIPO ENTIDAD',PCK_DATOS.FC_MODULOCONTABILIDAD,SYSDATE),'NO') = 'SI')THEN
      MI_VALORES := 'SELECT C.COMPANIA,
                 '||UN_ANO||' ANO1,
                 '||UN_MES||' MES1,
                 C.TIPO,
                 C.NUMERO,
                 NVL(CASE WHEN TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY'') - (C.FECHA_VCN_DOC + CD.DIAS_VENCIMIENTO) < 0 THEN 0
                       ELSE TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY'') - (C.FECHA_VCN_DOC + CD.DIAS_VENCIMIENTO )
                 END,0) EDAD,
                 '||MI_TASA||'    TASA,
                 NVL(C.DESCRIPCION,''.'') DESCRIPCION1,
                 NVL(C.CENTRO_COSTO,''9999999999'') CENTRO_COSTO1,
                 NVL(C.TERCERO,''99999999999'') TERCERO1,
                 NVL(C.SUCURSAL,''999'') SUCURSAL1,
                 NVL(C.AUXILIAR,''9999999999999999'') AUXILIAR1,
                 NVL(C.REFERENCIA,''9999999999999999'') REFERENCIA1,
                 EXTRACT(MONTH FROM C.FECHA) MES_FACTURA,
                 D.CUENTA,
                 D.CONSECUTIVO,
                 C.ANO   ANO_FACTURA,
                 C.FECHA,
                 C.FECHA_VCN_DOC,
                 CURRENT_DATE,
                 '''||UN_USUARIO||'''
           FROM COMPROBANTE_CNT C
           INNER JOIN TIPO_COMPROBANTE TC ON C.COMPANIA = TC.COMPANIA
                                             AND C.TIPO = TC.CODIGO
           INNER JOIN DETALLE_COMPROBANTE_CNT D ON C.NUMERO = D.COMPROBANTE
                                                   AND C.TIPO = D.TIPO_CPTE
                                                   AND C.ANO = D.ANO
                                                   AND C.COMPANIA = D.COMPANIA
           INNER JOIN TERCERO T ON D.COMPANIA = T.COMPANIA
                                                  AND D.TERCERO = T.NIT
                                                  AND D.SUCURSAL= T.SUCURSAL
           LEFT JOIN CONFIGURACION_DETERIORO CD ON T.TIPOENTIDAD = CD.TIPO_ENTIDAD     
                                                  AND T.COMPANIA = CD.COMPANIA
                                                  AND C.ANO = CD.ANO
           WHERE C.COMPANIA = '''||UN_COMPANIA||'''
                 AND MONTHS_BETWEEN  (TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY''),C.FECHA_VCN_DOC)  >= '||MI_MESES||'
                 AND C.ANO BETWEEN 0 AND EXTRACT(YEAR FROM TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY''))
                 AND C.FECHA <=  TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY'')
                 AND TC.CLASE_CONTABLE IN (''V'')
                 AND  D.CUENTA IN(' || PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(MI_STRCUENTAS)  || ')
                 AND D.VALOR_DEBITO <> 0'   ;  
    ELSE
      MI_VALORES := 'SELECT C.COMPANIA,
                 '||UN_ANO||' ANO1,
                 '||UN_MES||' MES1,
                 C.TIPO,
                 C.NUMERO,
                 NVL(CASE WHEN TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY'') - (C.FECHA_VCN_DOC + '||MI_DIAS||') < 0 THEN 0
                       ELSE TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY'') - (C.FECHA_VCN_DOC + '||MI_DIAS||' )
                 END,0) EDAD,
                 '||MI_TASA||'    TASA,
                 NVL(C.DESCRIPCION,''.'') DESCRIPCION1,
                 NVL(C.CENTRO_COSTO,''9999999999'') CENTRO_COSTO1,
                 NVL(C.TERCERO,''99999999999'') TERCERO1,
                 NVL(C.SUCURSAL,''999'') SUCURSAL1,
                 NVL(C.AUXILIAR,''9999999999999999'') AUXILIAR1,
                 NVL(C.REFERENCIA,''9999999999999999'') REFERENCIA1,
                 EXTRACT(MONTH FROM C.FECHA) MES_FACTURA,
                 D.CUENTA,
                 D.CONSECUTIVO,
                 C.ANO   ANO_FACTURA,
                 C.FECHA,
                 C.FECHA_VCN_DOC,
                 CURRENT_DATE,
                 '''||UN_USUARIO||'''
           FROM COMPROBANTE_CNT C
           INNER JOIN TIPO_COMPROBANTE TC ON C.COMPANIA = TC.COMPANIA
                                             AND C.TIPO = TC.CODIGO
           INNER JOIN DETALLE_COMPROBANTE_CNT D ON C.NUMERO = D.COMPROBANTE
                                                   AND C.TIPO = D.TIPO_CPTE
                                                   AND C.ANO = D.ANO
                                                   AND C.COMPANIA = D.COMPANIA
           WHERE C.COMPANIA = '''||UN_COMPANIA||'''
                 AND MONTHS_BETWEEN  (TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY''),C.FECHA_VCN_DOC)  >= '||MI_MESES||'
                 AND C.ANO BETWEEN 0 AND EXTRACT(YEAR FROM TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY''))
                 AND C.FECHA <=  TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY'')
                 AND TC.CLASE_CONTABLE IN (''V'')
                 AND  D.CUENTA IN(' || PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(MI_STRCUENTAS)  || ')
                 AND D.VALOR_DEBITO <> 0'   ;
      END IF;
     BEGIN
        BEGIN
            PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA   => 'DETERIORO_CARTERA',
                                     UN_ACCION  => 'IS',
                                     UN_CAMPOS  => MI_CAMPOS,
                                     UN_VALORES => MI_VALORES);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_INSERTDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;
      -- actualizar tasa por cuenta
     MI_MERGEUSING := 'SELECT DISTINCT '''||UN_COMPANIA||''' COMPANIA,
                EXTRACT(YEAR FROM FECHA_FINAL) ANOTASA,
                TO_CHAR(TASAS_INTERES.FECHA_FINAL,''DD/MM/YYYY'')FECHA_FINAL,
                TASAS_INTERES.CUENTA_CONTABLE,
                TASAS_INTERES.TASA
                FROM TASAS_INTERES
                WHERE FECHA_FINAL IS NOT NULL
                   AND TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY'') BETWEEN FECHA_INICIAL AND FECHA_FINAL';
     MI_MERGEENLACE := ' TABLA.COMPANIA = VISTA.COMPANIA
                     AND TABLA.CUENTA = VISTA.CUENTA_CONTABLE
                     AND TABLA.ANO = VISTA.ANOTASA
                     AND TABLA.ANO = '||UN_ANO||'
                     AND TABLA.MES = '||UN_MES||' ';
     MI_MERGEEXISTE := ' UPDATE SET TABLA.TASA = VISTA.TASA,
                              TABLA.DATE_MODIFIED         = SYSDATE,
                              TABLA.MODIFIED_BY           = '''||UN_USUARIO||''' ';
     BEGIN
      BEGIN
     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'DETERIORO_CARTERA'
                                   ,UN_ACCION      => 'MM'
                                   ,UN_MERGEUSING  => MI_MERGEUSING
                                   ,UN_MERGEENLACE => MI_MERGEENLACE
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;
    -- 'CONSULTO SI ALGUNA CUENTA QUEDO CON TASA 0
    MI_RETORNO := 'CUENTAS CONTABLES QUE NO TIENEN CONFIGURACION DE TASA DE INTERES' || CHR(13) || CHR(10) ||
                       'VIGENCIA'||CHR(9)||'CUENTA CONTABLE'|| CHR(13) || CHR(10) ;
    FOR MI_RS IN (SELECT   DISTINCT COMPANIA, ANO, MES, CUENTA, TASA
                  FROM  DETERIORO_CARTERA
                 WHERE COMPANIA= UN_COMPANIA
                   AND ANO= UN_ANO
                   AND MES= UN_MES
                   AND TASA=0
    )LOOP
        MI_CUNETASNOCONFIGURADAS := TRUE;
        MI_RETORNO :=MI_RETORNO || MI_RS.ANO||CHR(9)|| MI_RS.CUENTA|| CHR(13) || CHR(10);
    END LOOP;
    IF NOT MI_CUNETASNOCONFIGURADAS THEN
         MI_RETORNO := '';
    END IF;
    --Actualizando valor factura
      MI_MERGEUSING := 'SELECT DISTINCT D.COMPANIA,
                   DC.ANO,
                   DC.MES,
                   D.FECHA,
                   TC.CLASE_CONTABLE,
                   D.CUENTA,
                   DC.VALOR_FACTURA,
                   D.VALOR_DEBITO,
                   D.VALOR_CREDITO,
                   CASE WHEN D.FECHA_ABONOINICIAL<=  TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY'') THEN NVL(DC.ABONOINICIAL,0) ELSE 0 END ABONOINICIAL,
                   CASE WHEN D.FECHA_ABONOINICIAL<=  TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY'') THEN NVL(D.ABONOINICIAL,0) ELSE 0 END ABONOINICIALD,
                   DC.NUMERO_FACTURA,
                   DC.TIPO_FACTURA,
                   DC.CONSECUTIVO,
                   D.TERCERO,
                   DC.ANO_FACTURA
             FROM DETALLE_COMPROBANTE_CNT D
             INNER JOIN DETERIORO_CARTERA DC ON D.COMPANIA = DC.COMPANIA
                                                AND D.ANO = DC.ANO_FACTURA
                                                AND D.TIPO_CPTE = DC.TIPO_FACTURA
                                                AND D.COMPROBANTE = DC.NUMERO_FACTURA
                                                AND D.CUENTA = DC.CUENTA
                                                AND D.TERCERO = DC.TERCERO
                                                AND D.CONSECUTIVO = DC.CONSECUTIVO
             INNER JOIN TIPO_COMPROBANTE TC ON D.COMPANIA = TC.COMPANIA
                                               AND D.TIPO_CPTE = TC.CODIGO
                WHERE D.COMPANIA =  '''||UN_COMPANIA||'''
                    AND DC.ANO ='||UN_ANO||'
                     AND DC.MES='||UN_MES||'
                     AND D.FECHA <=  TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY'')
                     AND TC.CLASE_CONTABLE IN (''V'')
                     AND  D.CUENTA IN(' || PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(MI_STRCUENTAS)  || ')
                      AND D.VALOR_DEBITO<>0 ';
      MI_MERGEENLACE := 'TABLA.COMPANIA=VISTA.COMPANIA
                     AND VISTA.CUENTA = TABLA.CUENTA
                     AND VISTA.NUMERO_FACTURA = TABLA.NUMERO_FACTURA
                     AND VISTA.TIPO_FACTURA = TABLA.TIPO_FACTURA
                     AND VISTA.CONSECUTIVO = TABLA.CONSECUTIVO
                     AND VISTA.TERCERO = TABLA.TERCERO
                     AND TABLA.ANO =VISTA.ANO
                     AND TABLA.MES=VISTA.MES
                     AND TABLA.ANO_FACTURA = VISTA.ANO_FACTURA';
      MI_MERGEEXISTE := ' UPDATE SET  TABLA.VALOR_FACTURA=ABS(VISTA.VALOR_FACTURA+VISTA.VALOR_DEBITO-VISTA.VALOR_CREDITO),
                            TABLA.ABONOINICIAL=NVL(VISTA.ABONOINICIAL,0)+NVL(VISTA.ABONOINICIALD,0),
                            TABLA.DATE_MODIFIED         = SYSDATE,
                            TABLA.MODIFIED_BY           = '''||UN_USUARIO||''' ';
    BEGIN
     BEGIN
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'DETERIORO_CARTERA'
                                   ,UN_ACCION      => 'MM'
                                   ,UN_MERGEUSING  => MI_MERGEUSING
                                   ,UN_MERGEENLACE => MI_MERGEENLACE
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;
    --Actualizando saldo factura
       MI_MERGEUSING := 'SELECT D.COMPANIA,
                           D.CUENTA,
                           DC.ANO,
                           DC.MES,
                           SUM (DC.VALOR_AFECTACION) VALOR_AFECTACION,
                           SUM (D.VALOR_CREDITO) VALOR_CREDITO,
                           SUM (D.VALOR_DEBITO) VALOR_DEBITO,
                           DC.NUMERO_FACTURA,
                           DC.TIPO_FACTURA,
                           DC.ANO_FACTURA  ,
                           DC.CONSECUTIVO
                         FROM DETALLE_COMPROBANTE_CNT   D
                                INNER JOIN DETERIORO_CARTERA DC ON D.COMPANIA = DC.COMPANIA
                                  AND D.TIPO_CPTE_AFECT = DC.TIPO_FACTURA
                                  AND D.CMPTE_AFECTADO = DC.NUMERO_FACTURA
                                   AND D.ANO_AFECT = DC.ANO_FACTURA
                                  AND D.CUENTA = DC.CUENTA
                                INNER JOIN TIPO_COMPROBANTE TC ON D.COMPANIA = TC.COMPANIA
                                 AND D.TIPO_CPTE = TC.CODIGO
                     WHERE D.COMPANIA =  '''||UN_COMPANIA||'''
                        AND DC.ANO ='||UN_ANO||'
                        AND DC.MES='||UN_MES||'
                        AND D.FECHA <=  TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY'')
                        AND  D.CUENTA IN(' || PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(MI_STRCUENTAS)  || ')
                        AND TC.CLASE_CONTABLE IN(''I'',''B'',''N'',''C'',''R'')
                    GROUP BY D.COMPANIA,
                           D.CUENTA,
                           DC.ANO,
                           DC.MES,
                           DC.NUMERO_FACTURA,
                           DC.TIPO_FACTURA,
                           DC.ANO_FACTURA,
                           DC.CONSECUTIVO                        ';
   MI_MERGEENLACE := 'TABLA.COMPANIA=VISTA.COMPANIA
                     AND VISTA.CUENTA = TABLA.CUENTA
                     AND VISTA.NUMERO_FACTURA = TABLA.NUMERO_FACTURA
                     AND VISTA.TIPO_FACTURA = TABLA.TIPO_FACTURA
                     AND VISTA.CONSECUTIVO = TABLA.CONSECUTIVO
                     AND TABLA.ANO_FACTURA = VISTA.ANO_FACTURA
                     AND TABLA.ANO =VISTA.ANO
                     AND TABLA.MES=VISTA.MES';
     MI_MERGEEXISTE := ' UPDATE SET   TABLA.VALOR_AFECTACION=ABS(NVL(VISTA.VALOR_AFECTACION,0)+VISTA.VALOR_CREDITO-VISTA.VALOR_DEBITO),
                            TABLA.DATE_MODIFIED         = SYSDATE,
                            TABLA.MODIFIED_BY           = '''||UN_USUARIO||''' ';
    BEGIN
     BEGIN
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'DETERIORO_CARTERA'
                                   ,UN_ACCION      => 'MM'
                                   ,UN_MERGEUSING  => MI_MERGEUSING
                                   ,UN_MERGEENLACE => MI_MERGEENLACE
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;
     MI_MERGEUSING := 'SELECT D.COMPANIA ,
                       D.CUENTA,
                       DC.ANO,
                       DC.MES,
                       VALOR_FACTURA,
                       VALOR_AFECTACION,
                       CASE WHEN D.FECHA_ABONOINICIAL<=  TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY'') THEN NVL(DC.ABONOINICIAL,0) ELSE 0 END ABONOINICIAL,
                       DC.TIPO_FACTURA,
                       DC.NUMERO_FACTURA,
                       DC.CONSECUTIVO,
                       DC.ANO_FACTURA
                 FROM DETALLE_COMPROBANTE_CNT D
                 INNER JOIN DETERIORO_CARTERA DC ON D.CONSECUTIVO = DC.CONSECUTIVO
                                                    AND D.ANO = DC.ANO_FACTURA
                                                    AND D.COMPROBANTE = DC.NUMERO_FACTURA
                                                    AND D.TIPO_CPTE = DC.TIPO_FACTURA
                                                    AND D.CUENTA = DC.CUENTA
                                                    AND D.COMPANIA = DC.COMPANIA
                  WHERE D.COMPANIA =  '''||UN_COMPANIA||'''
                        AND DC.ANO ='||UN_ANO||'
                        AND DC.MES='||UN_MES||'                                                    ';
    MI_MERGEENLACE := 'TABLA.COMPANIA=VISTA.COMPANIA
                     AND TABLA.ANO =VISTA.ANO
                     AND TABLA.MES=VISTA.MES
                     AND TABLA.CUENTA = VISTA.CUENTA
                     AND  TABLA.TIPO_FACTURA =VISTA.TIPO_FACTURA
                     AND TABLA.NUMERO_FACTURA = VISTA.NUMERO_FACTURA
                     AND TABLA.CONSECUTIVO = VISTA.CONSECUTIVO
                     AND TABLA.ANO_FACTURA = VISTA.ANO_FACTURA';
     MI_MERGEEXISTE := ' UPDATE SET TABLA.SALDO=NVL(TABLA.VALOR_FACTURA,0)-NVL(TABLA.VALOR_AFECTACION,0)-NVL(TABLA.ABONOINICIAL,0),
                            TABLA.DATE_MODIFIED         = SYSDATE,
                            TABLA.MODIFIED_BY           = '''||UN_USUARIO||''' ';
    BEGIN
     BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'DETERIORO_CARTERA'
                                   ,UN_ACCION      => 'MM'
                                   ,UN_MERGEUSING  => MI_MERGEUSING
                                   ,UN_MERGEENLACE => MI_MERGEENLACE
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
     END;
          MI_CONDICION := ' COMPANIA='''||UN_COMPANIA||'''
                           AND ANO='||UN_ANO||'
                           AND MES='||UN_MES||'
                           AND (SALDO<=0 OR TASA = 0) ';
     BEGIN
        BEGIN
           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETERIORO_CARTERA',
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION );
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_DELETEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;
   --Actualizando valor del periodo
  MI_MERGEUSING := 'SELECT D.COMPANIA,  D.CUENTA,
                           DC.ANO,
                           DC.MES,
                           VALOR_FACTURA,
                           VALOR_AFECTACION,
                          CASE WHEN D.FECHA_ABONOINICIAL<=  TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YYYY'') THEN NVL(DC.ABONOINICIAL,0) ELSE 0 END ABONOINICIAL,
                           DC.TASA,
                           D.TIPO_CPTE,
                           D.COMPROBANTE,
                           DC.EDAD,
                           DC.SALDO,
                           DC.TIPO_FACTURA,
                           DC.NUMERO_FACTURA,
                           DC.CONSECUTIVO,
                           DC.ANO_FACTURA
                     FROM DETALLE_COMPROBANTE_CNT D
                     INNER JOIN DETERIORO_CARTERA DC ON D.COMPANIA = DC.COMPANIA
                                                        AND D.ANO = DC.ANO_FACTURA
                                                        AND D.COMPROBANTE = DC.NUMERO_FACTURA
                                                        AND D.TIPO_CPTE = DC.TIPO_FACTURA
                                                        AND D.CUENTA = DC.CUENTA
                                                        AND D.CONSECUTIVO = DC.CONSECUTIVO
             WHERE DC.COMPANIA =  '''||UN_COMPANIA||'''
                        AND DC.ANO ='||UN_ANO||'
                        AND DC.MES='||UN_MES||' ';
    MI_MERGEENLACE := 'TABLA.COMPANIA= VISTA.COMPANIA
                     AND TABLA.ANO = VISTA.ANO
                     AND TABLA.MES= VISTA.MES
                     AND TABLA.CUENTA = VISTA.CUENTA
                     AND TABLA.TIPO_FACTURA =VISTA.TIPO_CPTE
                     AND TABLA.NUMERO_FACTURA = VISTA.COMPROBANTE
                     AND TABLA.CONSECUTIVO = VISTA.CONSECUTIVO';
     MI_MERGEEXISTE := ' UPDATE SET TABLA.VALOR      =  FLOOR(VISTA.SALDO * (POWER((1+VISTA.TASA/100),(1/365))-1) *';
     --<TICKET:7727585_DETERIORO AUTOR:CPEREZ FECHA:2023-03-22> Se ajusta para que calculo la edad solo del mes calculado no de todo el periodo
     MI_MERGEEXISTE := MI_MERGEEXISTE || '( TO_DATE(''' || EXTRACT(DAY FROM UN_FECHACORTE) || '/' || EXTRACT(MONTH FROM UN_FECHACORTE) || '/' || EXTRACT(YEAR FROM UN_FECHACORTE) ||  ''',''DD/MM/YYYY'') - TO_DATE(''01/'  || EXTRACT(MONTH FROM UN_FECHACORTE) || '/' || EXTRACT(YEAR FROM UN_FECHACORTE) || ''',''DD/MM/YYYY'') +1 )' ;
     MI_MERGEEXISTE := MI_MERGEEXISTE || ' *100+0.501)/100,
                            TABLA.DETERIORO_ACUMULADO=   FLOOR(VISTA.SALDO * (POWER((1+VISTA.TASA/100),(1/365))-1) * VISTA.EDAD*100+0.501)/100,
                            TABLA.DATE_MODIFIED         = SYSDATE,
                            TABLA.MODIFIED_BY           = '''||UN_USUARIO||''' ';
     BEGIN
      BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'DETERIORO_CARTERA'
                                   ,UN_ACCION      => 'MM'
                                   ,UN_MERGEUSING  => MI_MERGEUSING
                                   ,UN_MERGEENLACE => MI_MERGEENLACE
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;
  MI_MERGEUSING := 'SELECT D.COMPANIA,
                             D.CUENTA,D.ANO ,
                             D.MES,
                             NVL(D.DETERIORO_ACUMULADO ,0)DETERIORO_ACUMULADO,
                             NVL(A.DETERIORO_ACUMULADO,0) DETERIORO_ACUMULADOA,
                             D.TIPO_FACTURA,
                             D.NUMERO_FACTURA,
                             D.CONSECUTIVO,
                             D.SALDO
                     FROM DETERIORO_CARTERA  D LEFT JOIN DETERIORO_CARTERA  A
                                   ON      D.COMPANIA = A.COMPANIA
                                   AND     D.TIPO_FACTURA = A.TIPO_FACTURA
                                   AND     D.NUMERO_FACTURA = A.NUMERO_FACTURA
                                   AND     D.CUENTA = A.CUENTA
                                   AND     D.CONSECUTIVO = A.CONSECUTIVO
                                   AND    A.ANO = CASE WHEN D.MES=1 THEN D.ANO-1 ELSE D.ANO   END
                                   AND    A.MES = CASE WHEN D.MES=1 THEN D.MES+11  ELSE D.MES -1 END
                     WHERE D.COMPANIA =  '''||UN_COMPANIA||'''
                        AND D.ANO ='||UN_ANO||'
                        AND D.MES='||UN_MES||'                                     ';
    MI_MERGEENLACE := 'TABLA.COMPANIA=VISTA.COMPANIA
                     AND TABLA.ANO = VISTA.ANO
                     AND TABLA.MES=VISTA.MES
                     AND TABLA.TIPO_FACTURA = VISTA.TIPO_FACTURA
                     AND TABLA.NUMERO_FACTURA = VISTA.NUMERO_FACTURA
                     AND TABLA.CUENTA = VISTA.CUENTA
                     AND TABLA.CONSECUTIVO = VISTA.CONSECUTIVO';
     --<TICKET:7727585_DETERIORO AUTOR:CPEREZ FECHA:2023-03-22> se cambia para que tome el acumulado cuando es el primer registro ya que  no aplica el calculo del mes
     MI_MERGEEXISTE := ' UPDATE SET  TABLA.VALOR = CASE WHEN (VISTA.DETERIORO_ACUMULADOA -SALDO)=0 THEN 0 ELSE CASE WHEN VISTA.DETERIORO_ACUMULADOA = 0 THEN VISTA.DETERIORO_ACUMULADO ELSE  TABLA.VALOR END  END,
                            TABLA.DATE_MODIFIED         = SYSDATE,
                            TABLA.MODIFIED_BY           = '''||UN_USUARIO||''' ';
    BEGIN
      BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'DETERIORO_CARTERA'
                                   ,UN_ACCION      => 'MM'
                                   ,UN_MERGEUSING  => MI_MERGEUSING
                                   ,UN_MERGEENLACE => MI_MERGEENLACE
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;
   /* MI_CONDICION := '   (  COMPANIA
                            || ANO
                            || MES
                            || TIPO_FACTURA
                            || NUMERO_FACTURA
                            || CUENTA
                            || CONSECUTIVO )
         IN(
         SELECT (  COMPANIA
                 || ' || UN_ANO || '
                 || ' || UN_MES || '
                 || TIPO_FACTURA
                 || NUMERO_FACTURA
                 || CUENTA
                 || CONSECUTIVO ) AS LLAVE
          FROM DETERIORO_CARTERA
          WHERE COMPANIA = '''||UN_COMPANIA||'''
           AND ANO =  CASE WHEN ' || UN_MES || ' = 1  THEN ' || UN_ANO || ' - 1 ELSE ' || UN_ANO|| '  END
           AND MES =  CASE WHEN ' || UN_MES || ' = 1  THEN 12 ELSE ' || UN_MES || '  - 1  END
           AND (VALOR_FACTURA - DETERIORO_ACUMULADO)  <= 0
         )';
   BEGIN
        BEGIN
           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETERIORO_CARTERA',
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION );
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_DELETEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;    */
    MI_CAMPOS        := 'VALOR = SALDO - (DETERIORO_ACUMULADO -VALOR)';
   MI_CONDICION     := '    COMPANIA =  '''||UN_COMPANIA||'''
                        AND ANO ='||UN_ANO||'
                        AND MES='||UN_MES|| '
                        AND (SALDO - (DETERIORO_ACUMULADO -VALOR))- VALOR < 0 ';
   BEGIN
      BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETERIORO_CARTERA',
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;
   MI_CAMPOS        := 'VALOR = 0';
   MI_CONDICION     := '    COMPANIA =  '''||UN_COMPANIA||'''
                        AND ANO ='||UN_ANO||'
                        AND MES='||UN_MES|| '
                        AND ((VALOR_FACTURA - DETERIORO_ACUMULADO) < 0 AND VALOR <0)' ;
   BEGIN
      BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETERIORO_CARTERA',
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;
 /*  MI_CAMPOS        := 'VALOR = VALOR ';
   MI_CONDICION     := '    COMPANIA =  '''||UN_COMPANIA||'''
                        AND ANO ='||UN_ANO||'
                        AND MES='||UN_MES|| '
                        AND (SALDO - DETERIORO_ACUMULADO)- VALOR < 0 ';
   BEGIN
      BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETERIORO_CARTERA',
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;  */
   MI_CAMPOS        := 'VALOR = 0';
   MI_CONDICION     := '    COMPANIA =  '''||UN_COMPANIA||'''
                        AND ANO ='||UN_ANO||'
                        AND MES='||UN_MES|| '
                        AND ((SALDO - DETERIORO_ACUMULADO) < 0 AND VALOR <0)' ;
   BEGIN
      BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETERIORO_CARTERA',
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;
 /*  MI_CAMPOS        := 'VALOR = VALOR ';
   MI_CONDICION     := '    COMPANIA =  '''||UN_COMPANIA||'''
                        AND ANO ='||UN_ANO||'
                        AND MES='||UN_MES|| '
                        AND (SALDO - DETERIORO_ACUMULADO)- VALOR < 0 ';
   BEGIN
      BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETERIORO_CARTERA',
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;  */
   MI_CAMPOS        := ' DETERIORO_ACUMULADO = SALDO ';
   MI_CONDICION     := '    COMPANIA =  '''||UN_COMPANIA||'''
                        AND ANO ='||UN_ANO||'
                        AND MES='||UN_MES|| '
                        AND (SALDO - DETERIORO_ACUMULADO) < 0 ';
   BEGIN
      BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETERIORO_CARTERA',
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;
  RETURN MI_RETORNO;
END FC_DETERIOROCUENTAH;

FUNCTION FC_CONTABILIZARDETERIORO
/*
  NAME              : FC_CONTABILIZARDETERIORO  En Access Contabilizar_ClickFormulario COMPROBANTE_DIFERIDO
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRATION    : 22/04/2021
  TIME              : 03:00 PM
  SOURCE MODULE     : SysmanCT2021.04.07
  MODIFIED BY       : 
  MODIFICATIONS     : 
  DESCRIPTION       : Función crea los comprobantes de deterioro
  PARAMETERS        : 
  @Name             :contabilizarDeterioro
  @Method           :GET
*/
(
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO          IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MES          IN  PCK_SUBTIPOS.TI_MES,    
    UN_FECHACORTE   IN  DATE,
    UN_USUARIO      IN  PCK_SUBTIPOS.TI_USUARIO              

)
RETURN CLOB AS 

    MI_RETORNO               CLOB;
    MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_MESES                 NUMBER(4,0);
    MI_DIAS                  NUMBER(10);    
    MI_STRCUENTAS            VARCHAR2(255 CHAR) := '';
    MI_STRANTERIOR           VARCHAR2(20 CHAR) := '*';
    MI_TASA                  NUMBER(10,2);
    MI_NUMANOINICIAL         NUMBER(4,0) := 1973;
    MI_CUENTADEBITO          VARCHAR2(32 CHAR);
    MI_CUENTACREDITO         VARCHAR2(32 CHAR);
    MI_NATURALEZADEBITO      VARCHAR2(2CHAR);
    MI_NATURALEZACREDITO     VARCHAR2(2CHAR);
    MI_TIPO                  COMPROBANTE_CNT.TIPO%TYPE;   
    MI_NUMERO                DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE;
    MI_FECHACORTE            DATE;
    MI_CUNETASNOCONFIGURADAS BOOLEAN := FALSE;
    MI_RTAINTERFAZ           PCK_SUBTIPOS.TI_RTA_ACME;

    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_CONSECUTIVO  PCK_SUBTIPOS.TI_ENTERO;
    
BEGIN

    MI_TIPO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                         ,UN_NOMBRE     => 'TIPO DE COMPROBANTE DETERIORO NIIF'
                                         ,UN_MODULO     => PCK_DATOS.FC_MODULOCONTABILIDAD
                                         ,UN_FECHA_PAR  => SYSDATE);
    
     MI_NUMERO := PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT(UN_COMPANIA     => UN_COMPANIA
                                                               ,UN_ANIO         => UN_ANO
                                                               ,UN_TIPO         => MI_TIPO
                                                               ,UN_NUMERO       => 0 
                                                               ,UN_CENTRO_COSTO => '');       
    
    
    MI_CONSECUTIVO := 1;

    MI_RETORNO := 'CUENTAS CONTABLES QUE NO TIENEN CONFIGURACION' || CHR(13) || CHR(10) ||
                  '   DE CUENTAS PARA DETERIORO DE CARTERA' || CHR(13) || CHR(10) || CHR(13) || CHR(10) ||
                  'VIGENCIA'||CHR(9)||'CUENTA CONTABLE'||CHR(9)||' NOMBRE CUENTA' ;


    FOR MI_RS IN (SELECT DISTINCT DETERIORO_CARTERA.COMPANIA,
                                DETERIORO_CARTERA.ANO,
                                DETERIORO_CARTERA.CUENTA,
                                PLAN_CONTABLE.NOMBRE
                 FROM DETERIORO_CARTERA
                 INNER JOIN PLAN_CONTABLE ON DETERIORO_CARTERA.COMPANIA = PLAN_CONTABLE.COMPANIA
                                             AND DETERIORO_CARTERA.ANO = PLAN_CONTABLE.ANO
                                             AND DETERIORO_CARTERA.CUENTA = PLAN_CONTABLE.CODIGO
                 WHERE (DETERIORO_CARTERA.COMPANIA = UN_COMPANIA
                       AND DETERIORO_CARTERA.ANO = UN_ANO
                       AND PLAN_CONTABLE.CREDITO_REVERSION_DET_ACTUAL IS NULL)
                       OR (DETERIORO_CARTERA.COMPANIA = UN_COMPANIA
                       AND DETERIORO_CARTERA.ANO = UN_ANO
                       AND PLAN_CONTABLE.DEBITO_REVERSION_DET_ACTUAL IS NULL)
    )LOOP
        MI_CUNETASNOCONFIGURADAS := TRUE;
        MI_RETORNO :=MI_RETORNO||'   '||MI_RS.ANO||'    '||CHR(9)|| MI_RS.CUENTA||CHR(9)||MI_RS.NOMBRE|| CHR(13) || CHR(10);

    END LOOP;

    IF MI_CUNETASNOCONFIGURADAS THEN
        RETURN MI_RETORNO;
    END IF;

    MI_RETORNO := '';   
    
    
    
    FOR MI_RS IN (SELECT DISTINCT COMPANIA,
                                    ANO,
                                    MES,
                                    CUENTA,
                                    TIPO_FACTURA,
                                    NUMERO_FACTURA,
                                    VALOR,
                                    CENTRO_COSTO,
                                    TERCERO,
                                    SUCURSAL,
                                    AUXILIAR,
                                    REFERENCIA,
                                    DESCRIPCION
                     FROM DETERIORO_CARTERA
                     WHERE COMPANIA = UN_COMPANIA
                           AND ANO = UN_ANO
                           AND MES = UN_MES
                           AND REVERSADO IN (0)) LOOP
                           
                           
        FOR MI_RS2 IN(SELECT DEB_CAUS_DET   CUENTADEBITO,
                           'D' NATURALEZADEBITO,
                           CRE_CAUS_DET   CUENTACREDITO,
                           'C' NATURALEZACREDITO,
                           PLAN_CONTABLE.CODIGO
                     FROM PLAN_CONTABLE
                     WHERE COMPANIA = UN_COMPANIA
                           AND ANO = UN_ANO
                           AND CODIGO = MI_RS.CUENTA
                            )LOOP
            
            
          MI_CUENTADEBITO   := NVL(MI_RS2.CUENTADEBITO,'');
          MI_CUENTACREDITO  := NVL(MI_RS2.CUENTACREDITO,''); 
          MI_NATURALEZADEBITO := NVL(MI_RS2.NATURALEZADEBITO,'');
          MI_NATURALEZACREDITO := NVL(MI_RS2.NATURALEZACREDITO,'');
        
         IF MI_CUENTADEBITO IS NOT NULL THEN           
           
                       MI_CAMPOS := 'COMPANIA,
                                     ANO,
                                     TIPO_CPTE,
                                     COMPROBANTE,
                                     CONSECUTIVO,
                                     CUENTA,                                     
                                     FECHA,
                                     NATURALEZA,
                                     VALOR_DEBITO,
                                     VALOR_CREDITO,
                                     EJECUCION_DEBITO,
                                     EJECUCION_CREDITO,
                                     CENTRO_COSTO,
                                     TERCERO,
                                     SUCURSAL,
                                     AUXILIAR,
                                     REFERENCIA,
                                     DESCRIPCION,
                                     TIPO_DOCUMENTO,
                                     NRO_DOCUMENTO'; 
                                     
                       MI_VALORES := ' '''||UN_COMPANIA||''',
                                     '||UN_ANO||',
                                     '''||MI_TIPO||''',
                                     '||MI_NUMERO||',
                                     '||MI_CONSECUTIVO||' ,
                                     '''||MI_CUENTADEBITO||''',                                     
                                     '''||UN_FECHACORTE||''',
                                     '''||MI_NATURALEZADEBITO||''',
                                     '||CASE WHEN MI_RS.VALOR > 0 THEN  MI_RS.VALOR ELSE  0 END||',
                                     '||CASE WHEN MI_RS.VALOR > 0 THEN  0  ELSE ABS(MI_RS.VALOR) END||',
                                     0 ,
                                     0,
                                     '''||MI_RS.CENTRO_COSTO||''',
                                     '''||MI_RS.TERCERO||''',
                                     '''||MI_RS.SUCURSAL||''',
                                     '''||MI_RS.AUXILIAR||''',
                                     '''||MI_RS.REFERENCIA||''',
                                     '''||MI_RS.DESCRIPCION||''',
                                     '''||MI_RS.TIPO_FACTURA||''',
                                     '||MI_RS.NUMERO_FACTURA||' ';                     
                        
                      BEGIN    
                        BEGIN
            
                              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TEMP_PLANA_AJUSTES'
                                                          ,UN_ACCION  => 'I'
                                                          ,UN_CAMPOS  => MI_CAMPOS
                                                          ,UN_VALORES => MI_VALORES);    
            
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;                                   
            
                         END;
            
                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                           MI_MSGERROR(1).CLAVE := 'NUMERO';
                           MI_MSGERROR(1).VALOR := MI_NUMERO;
                           MI_MSGERROR(2).CLAVE := 'TIPO';
                           MI_MSGERROR(2).VALOR := MI_TIPO;
            
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR);
            
                      END; 
                      
                      MI_CONSECUTIVO :=MI_CONSECUTIVO + 1;         
            
             END IF;
             
             
             IF MI_CUENTACREDITO IS NOT NULL THEN
                
                      MI_CAMPOS := 'COMPANIA,
                                     ANO,
                                     TIPO_CPTE,
                                     COMPROBANTE,
                                     CONSECUTIVO,
                                     CUENTA,                                    
                                     FECHA,
                                     NATURALEZA,
                                     VALOR_DEBITO,
                                     VALOR_CREDITO,
                                     EJECUCION_DEBITO,
                                     EJECUCION_CREDITO,
                                     CENTRO_COSTO,
                                     TERCERO,
                                     SUCURSAL,
                                     AUXILIAR,
                                     REFERENCIA,
                                     DESCRIPCION,
                                     TIPO_DOCUMENTO,
                                     NRO_DOCUMENTO';     
                                     
                                     
                    MI_VALORES := ''''||UN_COMPANIA||''',
                                     '||UN_ANO||',
                                     '''||MI_TIPO||''',
                                     '||MI_NUMERO||',
                                     '||MI_CONSECUTIVO||' ,
                                     '''||MI_CUENTACREDITO||''',                                     
                                     '''||UN_FECHACORTE||''',
                                     '''||MI_NATURALEZACREDITO||''',
                                     '||CASE WHEN MI_RS.VALOR > 0 THEN  0  ELSE ABS(MI_RS.VALOR) END||',
                                     '||CASE WHEN MI_RS.VALOR > 0 THEN  MI_RS.VALOR ELSE  0 END||',
                                     0,
                                     0,
                                     '''||MI_RS.CENTRO_COSTO||''',
                                     '''||MI_RS.TERCERO||''',
                                     '''||MI_RS.SUCURSAL||''',
                                     '''||MI_RS.AUXILIAR||''',
                                     '''||MI_RS.REFERENCIA||''',
                                     '''||MI_RS.DESCRIPCION||''',
                                     '''||MI_RS.TIPO_FACTURA||''',
                                     '||MI_RS.NUMERO_FACTURA||' ';                                       
                                     
                                     
                       BEGIN    
                         BEGIN
            
                              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TEMP_PLANA_AJUSTES'
                                                          ,UN_ACCION  => 'I'
                                                          ,UN_CAMPOS  => MI_CAMPOS
                                                          ,UN_VALORES => MI_VALORES);    
            
                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;                                   
            
                         END;
            
                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                           MI_MSGERROR(1).CLAVE := 'NUMERO';
                           MI_MSGERROR(1).VALOR := MI_NUMERO;
                           MI_MSGERROR(2).CLAVE := 'TIPO';
                           MI_MSGERROR(2).VALOR := MI_TIPO;
            
                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR);
            
                      END; 
                      
                      MI_CONSECUTIVO :=MI_CONSECUTIVO + 1;                                         
                
             
             END IF;
        
        END LOOP;   
    
    END LOOP;      
    
       MI_RTAINTERFAZ := PCK_CONTABILIZAR.FC_CONTABILIZAR( UN_COMPANIA         => UN_COMPANIA
                                              ,UN_TIPOCOMPROBANTE  => MI_TIPO
                                              ,UN_NUMERO           => MI_NUMERO
                                              ,UN_ANO              => UN_ANO
                                              ,UN_FECHA            => UN_FECHACORTE
                                              ,UN_TERCERO          => '999999999999999999'
                                              ,UN_SUCURSAL         => '999'
                                              ,UN_DESCRIPCION      => 'Deterioro cartera fecha '||TO_CHAR(UN_FECHACORTE,'DD/MM/YYYY')                                        
                                              ,UN_SIMPLE           => -1
                                              ,UN_INDIMPRESION     => 0                                            
                                              ,UN_INTOMITIRPPTAL   => -1
                                              ,UN_CONCILIAR        => 0
                                              ,UN_PLANO            => 0
                                              ,UN_NONETEA          => -1
                                              ,UN_CONTRATISTA      => 0
                                              ,UN_TEXTO            => ''
                                              ,UN_CONTRATO         => 0
                                              ,UN_TIPOCONTRATO     => ''
                                              ,UN_NRO_DOCUMENTO    => ''
                                              ,UN_ALMDEP           => 0
                                              ,UN_TERCE            => 0
                                              ,UN_RESAUXGEN        => 0
                                              ,UN_USUARIO          => UN_USUARIO);          

 RETURN MI_RETORNO;
END FC_CONTABILIZARDETERIORO;

FUNCTION FC_REVERSIONDETERIORO
/*
  NAME              : FC_REVERSIONDETERIORO  En Access Aceptar_Click() RECUPERACION_CARTERA_DETERIRO
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : ELKIN GEOVANNY AMAYA SILVA
  DATE MIGRATION    : 23/04/2021
  TIME              : 12:00 PM
  SOURCE MODULE     : SysmanCT2021.04.07
  MODIFIED BY       : 
  MODIFICATIONS     : 
  DESCRIPTION       : Función que crea los comprobantes de reversion
  PARAMETERS        : 
  @Name             :reversarDeterioro
  @Method           :GET
*/
(
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO          IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MES          IN  PCK_SUBTIPOS.TI_MES,    
    UN_FECHACORTE   IN  DATE,
    UN_USUARIO      IN  PCK_SUBTIPOS.TI_USUARIO              

)
RETURN CLOB AS 

    MI_RETORNO               CLOB;
    MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_MESES                 NUMBER(4,0);
    MI_DIAS                  NUMBER(10);    
    MI_STRCUENTAS            VARCHAR2(255 CHAR) := '';
    MI_STRANTERIOR           VARCHAR2(20 CHAR) := '*';
    MI_TASA                  NUMBER(10,2);
    MI_NUMANOINICIAL         NUMBER(4,0) := 1973;
    MI_CUENTADEBITO          VARCHAR2(32 CHAR);
    MI_CUENTACREDITO         VARCHAR2(32 CHAR);
    MI_NATURALEZADEBITO      VARCHAR2(2CHAR);
    MI_NATURALEZACREDITO     VARCHAR2(2CHAR);
    MI_TIPO                  COMPROBANTE_CNT.TIPO%TYPE;   
    MI_NUMERO                DETALLE_COMPROBANTE_CNT.COMPROBANTE%TYPE;
    MI_FECHACORTE            DATE;
    MI_CUNETASNOCONFIGURADAS BOOLEAN := FALSE;
    MI_RTAINTERFAZ           PCK_SUBTIPOS.TI_RTA_ACME;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
    MI_CONSECUTIVO  PCK_SUBTIPOS.TI_ENTERO;
    MI_CONTADOR     PCK_SUBTIPOS.TI_ENTERO;

BEGIN

     MI_TIPO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                         ,UN_NOMBRE     => 'COMPROBANTE DE REVERSION DE DETERIORO POR RECUPERACION DE CARTERA NIIF'
                                         ,UN_MODULO     => PCK_DATOS.FC_MODULOCONTABILIDAD
                                         ,UN_FECHA_PAR  => SYSDATE);

    MI_CONSECUTIVO := 1;


     SELECT COUNT(*)
     INTO MI_CONTADOR
     FROM TIPO_COMPROBANTE
     WHERE COMPANIA = UN_COMPANIA
           AND CODIGO = MI_TIPO;           

    IF MI_CONTADOR =0 THEN
       BEGIN
            BEGIN 

                 RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;                                   

            END;

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                      MI_MSGERROR(1).CLAVE := 'TIPO';
                      MI_MSGERROR(1).VALOR := MI_TIPO;

                       PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_CNT_TIPONOEXISTE
                                                        ,UN_REEMPLAZOS  => MI_MSGERROR);            
       END; 
    END IF;

     MI_NUMERO := PCK_CONTABILIDAD_CPTE.FC_ENUMERARCOMPROBANTECNT(UN_COMPANIA     => UN_COMPANIA
                                                               ,UN_ANIO         => UN_ANO
                                                               ,UN_TIPO         => MI_TIPO
                                                               ,UN_NUMERO       => 0 
                                                               ,UN_CENTRO_COSTO => '');        


    MI_RETORNO := '  CUENTAS CONTABLES QUE NO TIENEN CONFIGURACION' || CHR(13) || CHR(10) ||
                  'DE CUENTAS PARA REVERSION DE DETERIORO DE CARTERA ' || CHR(13) || CHR(10) || CHR(13) || CHR(10) ||
                  'VIGENCIA'||CHR(9)||'CUENTA CONTABLE'||CHR(9)||' NOMBRE CUENTA'|| CHR(13) || CHR(10) ;


    FOR MI_RS IN (SELECT DISTINCT DETERIORO_CARTERA.COMPANIA,
                                DETERIORO_CARTERA.ANO,
                                DETERIORO_CARTERA.CUENTA,
                                PLAN_CONTABLE.NOMBRE
                 FROM DETERIORO_CARTERA
                 INNER JOIN PLAN_CONTABLE ON DETERIORO_CARTERA.COMPANIA = PLAN_CONTABLE.COMPANIA
                                             AND DETERIORO_CARTERA.ANO = PLAN_CONTABLE.ANO
                                             AND DETERIORO_CARTERA.CUENTA = PLAN_CONTABLE.CODIGO
                 WHERE ( DETERIORO_CARTERA.COMPANIA = UN_COMPANIA
                         AND DETERIORO_CARTERA.ANO = UN_ANO
                         AND PLAN_CONTABLE.DEBITO_REVERSION_DET_ANTERIOR IS NULL )
                       OR ( DETERIORO_CARTERA.COMPANIA = UN_COMPANIA
                            AND DETERIORO_CARTERA.ANO = UN_ANO
                            AND PLAN_CONTABLE.CREDITO_REVERSION_DET_ANTERIOR IS NULL )
    )LOOP
        MI_CUNETASNOCONFIGURADAS := TRUE;
        MI_RETORNO :=MI_RETORNO||'   '||MI_RS.ANO||'    '||CHR(9)|| MI_RS.CUENTA||CHR(9)||MI_RS.NOMBRE || CHR(13) || CHR(10);

    END LOOP;

    IF MI_CUNETASNOCONFIGURADAS THEN
        RETURN MI_RETORNO;
    END IF;

    MI_RETORNO := '';   



    FOR MI_RS IN (SELECT DISTINCT COMPANIA,
                                    ANO,
                                    MES,
                                    CUENTA,
                                    TIPO_FACTURA,
                                    NUMERO_FACTURA,
                                    VALOR,
                                    CENTRO_COSTO,
                                    TERCERO,
                                    SUCURSAL,
                                    AUXILIAR,
                                    REFERENCIA,
                                    DESCRIPCION
                     FROM DETERIORO_CARTERA
                     WHERE COMPANIA = UN_COMPANIA
                           AND ANO = UN_ANO
                           AND MES = UN_MES
                           AND REVERSADO IN (0)) LOOP


        FOR MI_RS2 IN(SELECT DEBITO_REVERSION_DET_ANTERIOR   CUENTADEBITO,
                           'D' NATURALEZADEBITO,
                           CREDITO_REVERSION_DET_ANTERIOR   CUENTACREDITO,
                           'C' NATURALEZACREDITO,
                           PLAN_CONTABLE.CODIGO
                     FROM PLAN_CONTABLE
                     WHERE COMPANIA = UN_COMPANIA
                           AND ANO = UN_ANO
                           AND CODIGO = MI_RS.CUENTA
                            )LOOP


          MI_CUENTADEBITO   := NVL(MI_RS2.CUENTADEBITO,'');
          MI_CUENTACREDITO  := NVL(MI_RS2.CUENTACREDITO,''); 
          MI_NATURALEZADEBITO := NVL(MI_RS2.NATURALEZADEBITO,'');
          MI_NATURALEZACREDITO := NVL(MI_RS2.NATURALEZACREDITO,'');

         IF MI_CUENTADEBITO IS NOT NULL THEN           

                       MI_CAMPOS := 'COMPANIA,
                                     ANO,
                                     TIPO_CPTE,
                                     COMPROBANTE,
                                     CONSECUTIVO,
                                     CUENTA,                                     
                                     FECHA,
                                     NATURALEZA,
                                     VALOR_DEBITO,
                                     VALOR_CREDITO,
                                     EJECUCION_DEBITO,
                                     EJECUCION_CREDITO,
                                     CENTRO_COSTO,
                                     TERCERO,
                                     SUCURSAL,
                                     AUXILIAR,
                                     REFERENCIA,
                                     DESCRIPCION,
                                     TIPO_DOCUMENTO,
                                     NRO_DOCUMENTO'; 

                       MI_VALORES := ' '''||UN_COMPANIA||''',
                                     '||UN_ANO||',
                                     '''||MI_TIPO||''',
                                     '||MI_NUMERO||',
                                     '||MI_CONSECUTIVO||' ,
                                     '''||MI_CUENTADEBITO||''',                                     
                                     '''||UN_FECHACORTE||''',
                                     '''||MI_NATURALEZADEBITO||''',
                                     '||CASE WHEN MI_RS.VALOR > 0 THEN  MI_RS.VALOR ELSE  0 END||',
                                     '||CASE WHEN MI_RS.VALOR > 0 THEN  0  ELSE ABS(MI_RS.VALOR) END||',
                                     0 ,
                                     0,
                                     '''||MI_RS.CENTRO_COSTO||''',
                                     '''||MI_RS.TERCERO||''',
                                     '''||MI_RS.SUCURSAL||''',
                                     '''||MI_RS.AUXILIAR||''',
                                     '''||MI_RS.REFERENCIA||''',
                                     '''||MI_RS.DESCRIPCION||''',
                                     '''||MI_RS.TIPO_FACTURA||''',
                                     '||MI_RS.NUMERO_FACTURA||' ';                     

                      BEGIN    
                        BEGIN

                              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TEMP_PLANA_AJUSTES'
                                                          ,UN_ACCION  => 'I'
                                                          ,UN_CAMPOS  => MI_CAMPOS
                                                          ,UN_VALORES => MI_VALORES);    

                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;                                   

                         END;

                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                           MI_MSGERROR(1).CLAVE := 'NUMERO';
                           MI_MSGERROR(1).VALOR := MI_NUMERO;
                           MI_MSGERROR(2).CLAVE := 'TIPO';
                           MI_MSGERROR(2).VALOR := MI_TIPO;

                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR);

                      END; 

                      MI_CONSECUTIVO :=MI_CONSECUTIVO + 1;         

             END IF;


             IF MI_CUENTACREDITO IS NOT NULL THEN

                      MI_CAMPOS := 'COMPANIA,
                                     ANO,
                                     TIPO_CPTE,
                                     COMPROBANTE,
                                     CONSECUTIVO,
                                     CUENTA,                                    
                                     FECHA,
                                     NATURALEZA,
                                     VALOR_DEBITO,
                                     VALOR_CREDITO,
                                     EJECUCION_DEBITO,
                                     EJECUCION_CREDITO,
                                     CENTRO_COSTO,
                                     TERCERO,
                                     SUCURSAL,
                                     AUXILIAR,
                                     REFERENCIA,
                                     DESCRIPCION,
                                     TIPO_DOCUMENTO,
                                     NRO_DOCUMENTO';     


                    MI_VALORES := ''''||UN_COMPANIA||''',
                                     '||UN_ANO||',
                                     '''||MI_TIPO||''',
                                     '||MI_NUMERO||',
                                     '||MI_CONSECUTIVO||' ,
                                     '''||MI_CUENTACREDITO||''',                                     
                                     '''||UN_FECHACORTE||''',
                                     '''||MI_NATURALEZACREDITO||''',
                                     '||CASE WHEN MI_RS.VALOR > 0 THEN  0  ELSE ABS(MI_RS.VALOR) END||',
                                     '||CASE WHEN MI_RS.VALOR > 0 THEN  MI_RS.VALOR ELSE  0 END||',
                                     0,
                                     0,
                                     '''||MI_RS.CENTRO_COSTO||''',
                                     '''||MI_RS.TERCERO||''',
                                     '''||MI_RS.SUCURSAL||''',
                                     '''||MI_RS.AUXILIAR||''',
                                     '''||MI_RS.REFERENCIA||''',
                                     '''||MI_RS.DESCRIPCION||''',
                                     '''||MI_RS.TIPO_FACTURA||''',
                                     '||MI_RS.NUMERO_FACTURA||' ';                                       


                       BEGIN    
                         BEGIN

                              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'TEMP_PLANA_AJUSTES'
                                                          ,UN_ACCION  => 'I'
                                                          ,UN_CAMPOS  => MI_CAMPOS
                                                          ,UN_VALORES => MI_VALORES);    

                              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                                RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;                                   

                         END;

                         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
                           MI_MSGERROR(1).CLAVE := 'NUMERO';
                           MI_MSGERROR(1).VALOR := MI_NUMERO;
                           MI_MSGERROR(2).CLAVE := 'TIPO';
                           MI_MSGERROR(2).VALOR := MI_TIPO;

                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ERR_SYSMANSF_INSDETACNT
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR);

                      END; 

                      MI_CONSECUTIVO :=MI_CONSECUTIVO + 1;                                         


             END IF;

        END LOOP;   

    END LOOP;      

       MI_RTAINTERFAZ := PCK_CONTABILIZAR.FC_CONTABILIZAR( UN_COMPANIA         => UN_COMPANIA
                                              ,UN_TIPOCOMPROBANTE  => MI_TIPO
                                              ,UN_NUMERO           => MI_NUMERO
                                              ,UN_ANO              => UN_ANO
                                              ,UN_FECHA            => UN_FECHACORTE
                                              ,UN_TERCERO          => '999999999999999999'
                                              ,UN_SUCURSAL         => '999'
                                              ,UN_DESCRIPCION      => 'Reversión deterioro de cartera fecha '||TO_CHAR(UN_FECHACORTE,'DD/MM/YYYY')                                        
                                              ,UN_SIMPLE           => -1
                                              ,UN_INDIMPRESION     => 0                                            
                                              ,UN_INTOMITIRPPTAL   => -1
                                              ,UN_CONCILIAR        => 0
                                              ,UN_PLANO            => 0
                                              ,UN_NONETEA          => -1
                                              ,UN_CONTRATISTA      => 0
                                              ,UN_TEXTO            => ''
                                              ,UN_CONTRATO         => 0
                                              ,UN_TIPOCONTRATO     => ''
                                              ,UN_NRO_DOCUMENTO    => ''
                                              ,UN_ALMDEP           => 0
                                              ,UN_TERCE            => 0
                                              ,UN_RESAUXGEN        => 0
                                              ,UN_USUARIO          => UN_USUARIO);  
                                              
                                              
      BEGIN          
         BEGIN       
    
            MI_CAMPOS:='REVERSADO=-1,
                       MODIFIED_BY           ='''||UN_USUARIO||''',
                       DATE_MODIFIED         =SYSDATE';
    
            MI_CONDICION:=' COMPANIA='''||UN_COMPANIA||''' 
                        AND ANO= '||UN_ANO||'
                        AND MES= '||UN_MES||' 
                        AND REVERSADO IN(0)';
    
    
             PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA    =>'DETERIORO_CARTERA',
                                        UN_ACCION   =>'M',
                                        UN_CAMPOS   =>MI_CAMPOS,            
                                        UN_CONDICION=>MI_CONDICION);      
            EXCEPTION
                 WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE  PCK_EXCEPCIONES.EXC_ACTUALIZAR;
     END;     
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN          
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);      
   END;                                                       

 RETURN MI_RETORNO;
END FC_REVERSIONDETERIORO;

PROCEDURE PR_ACTUALIZAR3110 (
  /*
    NAME              : PR_ACTUALIZAR3110
    AUTHORS           : SYSMAN LTDA
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 03/05/2021
    TIME              : 04:00 PM
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    DESCRIPTION       : 
    MODIFICATIONS     : 
    @NAME:  actualizar3110
    @METHOD:  POST
  */ 
    UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANIOTRABAJO   IN  PCK_SUBTIPOS.TI_ANIO,
    UN_ANIOCOMPARAR  IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MESTRABAJO    IN PCK_SUBTIPOS.TI_MES,
    UN_MESCOMPARAR   IN PCK_SUBTIPOS.TI_MES,
    UN_AJUSTANDO     IN PCK_SUBTIPOS.TI_LOGICO:= 0,
    UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO
    )
  AS
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;
  MI_ANO          PCK_SUBTIPOS.TI_ANIO;
  MI_TIPO_CPTE    PCK_SUBTIPOS.TI_TIPOCOMPROBANTE;
  MI_COMPROBANTE  PCK_SUBTIPOS.TI_NUMEROCOMPROBANTECNT;
  MI_RTA          PCK_SUBTIPOS.TI_ENTERO_LARGO; 
  MI_RSDATO          SYS_REFCURSOR;
  MI_VALOR        NUMBER(20,2):=0;
  MI_CUENTAUTILIDADES   VARCHAR2(32 CHAR);
  MI_CUENTAPERDIDAS   VARCHAR2(32 CHAR);
BEGIN

 MI_CUENTAUTILIDADES := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'CUENTA DE UTILIDADES',PCK_DATOS.FC_MODULOCONTABILIDAD,SYSDATE);
 MI_CUENTAPERDIDAS :=PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA,'CUENTA DE PERDIDAS',PCK_DATOS.FC_MODULOCONTABILIDAD,SYSDATE);

  IF UN_AJUSTANDO NOT IN (0) THEN
 --ANIO ACTUAL   
      MI_STRSQL := 'SELECT SUM ( CASE WHEN PLAN_CONTABLE.CODIGO=''4'' THEN PLAN_CONTABLE.SALDO'||UN_MESCOMPARAR||' ELSE 0 END)-
                    SUM ( CASE WHEN PLAN_CONTABLE.CODIGO=''5'' THEN PLAN_CONTABLE.SALDO'||UN_MESCOMPARAR||' ELSE 0 END) - 
                    SUM ( CASE WHEN PLAN_CONTABLE.CODIGO=''6'' THEN PLAN_CONTABLE.SALDO'||UN_MESCOMPARAR||' ELSE 0 END) - 
                    SUM ( CASE WHEN PLAN_CONTABLE.CODIGO=''7'' THEN PLAN_CONTABLE.SALDO'||UN_MESCOMPARAR||' ELSE 0 END) TOTAL
        FROM PLAN_CONTABLE
        WHERE PLAN_CONTABLE.COMPANIA = '''||UN_COMPANIA||'''
              AND ( PLAN_CONTABLE.CODIGO = ''4''
                    OR PLAN_CONTABLE.CODIGO = ''5''
                       OR PLAN_CONTABLE.CODIGO = ''6''
                          OR PLAN_CONTABLE.CODIGO = ''7'' )
                  AND PLAN_CONTABLE.ANO = '||UN_ANIOCOMPARAR||'
        GROUP BY PLAN_CONTABLE.COMPANIA,
                 PLAN_CONTABLE.ANO';

     OPEN MI_RSDATO FOR MI_STRSQL; 
     LOOP
     FETCH MI_RSDATO INTO MI_VALOR;
       EXIT WHEN MI_RSDATO%NOTFOUND;
              IF MI_VALOR > 0 THEN

                  MI_CAMPOS:='SALDO'||UN_MESCOMPARAR||'='||MI_VALOR||',
                       MODIFIED_BY           ='''||UN_USUARIO||''',
                       DATE_MODIFIED         =SYSDATE';

                   MI_CONDICION:=' COMPANIA='''||UN_COMPANIA||''' 
                        AND ANO= '||UN_ANIOCOMPARAR||'                        
                        AND CODIGO ='''|| MI_CUENTAUTILIDADES||''' ';

                           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA    =>'PLAN_CONTABLE',
                                        UN_ACCION   =>'M',
                                        UN_CAMPOS   =>MI_CAMPOS,            
                                        UN_CONDICION=>MI_CONDICION); 

                    MI_CONDICION:=' COMPANIA='''||UN_COMPANIA||''' 
                        AND ANO= '||UN_ANIOCOMPARAR||'                        
                        AND CODIGO =''3110'' ';

                           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA    =>'PLAN_CONTABLE',
                                        UN_ACCION   =>'M',
                                        UN_CAMPOS   =>MI_CAMPOS,            
                                        UN_CONDICION=>MI_CONDICION);                                         

              ELSE

                   MI_CAMPOS:='SALDO'||UN_MESCOMPARAR||'='||MI_VALOR||',
                       MODIFIED_BY           ='''||UN_USUARIO||''',
                       DATE_MODIFIED         =SYSDATE';

                   MI_CONDICION:=' COMPANIA='''||UN_COMPANIA||''' 
                        AND ANO= '||UN_ANIOCOMPARAR||'                     
                        AND CODIGO ='''|| MI_CUENTAPERDIDAS||''' ';

                           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA    =>'PLAN_CONTABLE',
                                        UN_ACCION   =>'M',
                                        UN_CAMPOS   =>MI_CAMPOS,            
                                        UN_CONDICION=>MI_CONDICION); 

                    MI_CONDICION:=' COMPANIA='''||UN_COMPANIA||''' 
                        AND ANO= '||UN_ANIOCOMPARAR||' 
                        AND CODIGO =''3110'' ';

                           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA    =>'PLAN_CONTABLE',
                                        UN_ACCION   =>'M',
                                        UN_CAMPOS   =>MI_CAMPOS,            
                                        UN_CONDICION=>MI_CONDICION);                                         



              END IF;  
         END LOOP;
     CLOSE MI_RSDATO; 

 --ANIO ANTERIOR    
      MI_STRSQL := 'SELECT SUM ( CASE WHEN PLAN_CONTABLE.CODIGO=''4'' THEN PLAN_CONTABLE.SALDO'||UN_MESTRABAJO||' ELSE 0 END)-
                    SUM ( CASE WHEN PLAN_CONTABLE.CODIGO=''5'' THEN PLAN_CONTABLE.SALDO'||UN_MESTRABAJO||' ELSE 0 END) - 
                    SUM ( CASE WHEN PLAN_CONTABLE.CODIGO=''6'' THEN PLAN_CONTABLE.SALDO'||UN_MESTRABAJO||' ELSE 0 END) - 
                    SUM ( CASE WHEN PLAN_CONTABLE.CODIGO=''7'' THEN PLAN_CONTABLE.SALDO'||UN_MESTRABAJO||' ELSE 0 END) TOTAL
        FROM PLAN_CONTABLE
        WHERE PLAN_CONTABLE.COMPANIA = '''||UN_COMPANIA||'''
              AND ( PLAN_CONTABLE.CODIGO = ''4''
                    OR PLAN_CONTABLE.CODIGO = ''5''
                       OR PLAN_CONTABLE.CODIGO = ''6''
                          OR PLAN_CONTABLE.CODIGO = ''7'' )
                  AND PLAN_CONTABLE.ANO = '||UN_ANIOTRABAJO||'
        GROUP BY PLAN_CONTABLE.COMPANIA,
                 PLAN_CONTABLE.ANO';

     OPEN MI_RSDATO FOR MI_STRSQL; 
     LOOP
     FETCH MI_RSDATO INTO MI_VALOR;
       EXIT WHEN MI_RSDATO%NOTFOUND;
              IF MI_VALOR > 0 THEN

                  MI_CAMPOS:='SALDO'||UN_MESTRABAJO||'='||MI_VALOR||',
                       MODIFIED_BY           ='''||UN_USUARIO||''',
                       DATE_MODIFIED         =SYSDATE';

                   MI_CONDICION:=' COMPANIA='''||UN_COMPANIA||''' 
                        AND ANO= '||UN_ANIOTRABAJO||'                        
                        AND CODIGO ='''|| MI_CUENTAUTILIDADES||''' ';

                           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA    =>'PLAN_CONTABLE',
                                        UN_ACCION   =>'M',
                                        UN_CAMPOS   =>MI_CAMPOS,            
                                        UN_CONDICION=>MI_CONDICION); 

                    MI_CONDICION:=' COMPANIA='''||UN_COMPANIA||''' 
                        AND ANO= '||UN_ANIOTRABAJO||'                       
                        AND CODIGO =''3110'' ';

                           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA    =>'PLAN_CONTABLE',
                                        UN_ACCION   =>'M',
                                        UN_CAMPOS   =>MI_CAMPOS,            
                                        UN_CONDICION=>MI_CONDICION);                                         

              ELSE

                   MI_CAMPOS:='SALDO'||UN_MESTRABAJO||'='||MI_VALOR||',
                       MODIFIED_BY           ='''||UN_USUARIO||''',
                       DATE_MODIFIED         =SYSDATE';

                   MI_CONDICION:=' COMPANIA='''||UN_COMPANIA||''' 
                        AND ANO= '||UN_ANIOCOMPARAR||'                     
                        AND CODIGO ='''|| MI_CUENTAPERDIDAS||''' ';

                           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA    =>'PLAN_CONTABLE',
                                        UN_ACCION   =>'M',
                                        UN_CAMPOS   =>MI_CAMPOS,            
                                        UN_CONDICION=>MI_CONDICION); 

                    MI_CONDICION:=' COMPANIA='''||UN_COMPANIA||''' 
                        AND ANO= '||UN_ANIOTRABAJO||'                   
                        AND CODIGO =''3110'' ';

                           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA    =>'PLAN_CONTABLE',
                                        UN_ACCION   =>'M',
                                        UN_CAMPOS   =>MI_CAMPOS,            
                                        UN_CONDICION=>MI_CONDICION);                                         



              END IF;  
         END LOOP;
     CLOSE MI_RSDATO; 

    ELSE

    --Borrar3110
         MI_CAMPOS:='SALDO'||UN_MESCOMPARAR||'=0,
                       MODIFIED_BY           ='''||UN_USUARIO||''',
                       DATE_MODIFIED         =SYSDATE';

                   MI_CONDICION:=' COMPANIA='''||UN_COMPANIA||''' 
                        AND ANO= '||UN_ANIOCOMPARAR||'                        
                        AND CODIGO ='''|| MI_CUENTAUTILIDADES||''' ';

                           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA    =>'PLAN_CONTABLE',
                                        UN_ACCION   =>'M',
                                        UN_CAMPOS   =>MI_CAMPOS,            
                                        UN_CONDICION=>MI_CONDICION); 

                 MI_CONDICION:=' COMPANIA='''||UN_COMPANIA||''' 
                        AND ANO= '||UN_ANIOCOMPARAR||'                       
                        AND CODIGO =''3110'' ';

                           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA    =>'PLAN_CONTABLE',
                                        UN_ACCION   =>'M',
                                        UN_CAMPOS   =>MI_CAMPOS,            
                                        UN_CONDICION=>MI_CONDICION);                                          

                    MI_CONDICION:=' COMPANIA='''||UN_COMPANIA||''' 
                        AND ANO= '||UN_ANIOCOMPARAR||'                     
                        AND CODIGO =''3110'' ';

                           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA    =>'PLAN_CONTABLE',
                                        UN_ACCION   =>'M',
                                        UN_CAMPOS   =>MI_CAMPOS,            
                                        UN_CONDICION=>MI_CONDICION);     


                   MI_CAMPOS:='SALDO'||UN_MESTRABAJO||'=0,
                       MODIFIED_BY           ='''||UN_USUARIO||''',
                       DATE_MODIFIED         =SYSDATE';

                   MI_CONDICION:=' COMPANIA='''||UN_COMPANIA||''' 
                        AND ANO= '||UN_ANIOTRABAJO ||'                        
                        AND CODIGO ='''|| MI_CUENTAUTILIDADES||''' ';

                           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA    =>'PLAN_CONTABLE',
                                        UN_ACCION   =>'M',
                                        UN_CAMPOS   =>MI_CAMPOS,            
                                        UN_CONDICION=>MI_CONDICION); 

                 MI_CONDICION:=' COMPANIA='''||UN_COMPANIA||''' 
                        AND ANO= '||UN_ANIOTRABAJO ||'                        
                        AND CODIGO ='''||MI_CUENTAPERDIDAS||''' ';

                           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA    =>'PLAN_CONTABLE',
                                        UN_ACCION   =>'M',
                                        UN_CAMPOS   =>MI_CAMPOS,            
                                        UN_CONDICION=>MI_CONDICION);                                          

                    MI_CONDICION:=' COMPANIA='''||UN_COMPANIA||''' 
                         AND ANO= '||UN_ANIOTRABAJO ||'                    
                        AND CODIGO =''3110'' ';

                           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA    =>'PLAN_CONTABLE',
                                        UN_ACCION   =>'M',
                                        UN_CAMPOS   =>MI_CAMPOS,            
                                        UN_CONDICION=>MI_CONDICION);                                           

    END IF;
END PR_ACTUALIZAR3110;

FUNCTION FC_DETERIOROCUENTASEMESTRE
/*
  NAME              : FC_DETERIOROCUENTASEMESTRE 
  AUTHORS           : SYSMAN  SAS
  AUTHOR MIGRATION  : RAUL FERNANDO MEDINA SANDOVAL
  DATE MIGRATION    : 16/11/2023
  TIME              : 10:40 AM
  SOURCE MODULE     : SysmanCT2021.04.07
  MODIFIED BY       :
  MODIFICATIONS     :
  DESCRIPTION       : Funcion genera el deterioro de cuentas teniendo en cuenta periodicidad
  PARAMETERS        :
  --Name             :deterioroCuentaSemestre
  --Method           :GET
*/
(
    UN_COMPANIA     IN  PCK_SUBTIPOS.TI_COMPANIA,
    UN_ANO          IN  PCK_SUBTIPOS.TI_ANIO,
    UN_MES          IN  PCK_SUBTIPOS.TI_MES,
    UN_FECHACORTE   IN  DATE,
    UN_USUARIO      IN  PCK_SUBTIPOS.TI_USUARIO

)
RETURN CLOB AS

    MI_RETORNO      CLOB;
    MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_MESES        NUMBER(4,0);
    MI_DIAS         NUMBER(10);
    MI_TIPO         VARCHAR2(5 CHAR);
    MI_STRCUENTAS   VARCHAR2(4000 CHAR) := '';
    MI_STRANTERIOR  VARCHAR2(4000 CHAR) := '**';
    MI_TASA         NUMBER(10,2);
    MI_NUMANOINICIAL NUMBER(4,0) := 1973;

    MI_CUNETASNOCONFIGURADAS BOOLEAN := FALSE;

    MI_CAMPOS  PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES PCK_SUBTIPOS.TI_VALORES;
    MI_MERGEUSING   PCK_SUBTIPOS.TI_MERGEUSING ;
    MI_MERGEENLACE  PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE  PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
BEGIN

     MI_MESES := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                         ,UN_NOMBRE     => 'NUMERO DE MESES VENCIDOS QUE APLICAN DETERIORO NIIF'
                                         ,UN_MODULO     => PCK_DATOS.FC_MODULOCONTABILIDAD
                                         ,UN_FECHA_PAR  => SYSDATE);

     MI_DIAS := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                         ,UN_NOMBRE     => 'NUMERO DE DIAS VENCIDOS QUE APLICAN DETERIORO NIIF'
                                         ,UN_MODULO     => PCK_DATOS.FC_MODULOCONTABILIDAD
                                         ,UN_FECHA_PAR  => SYSDATE);


     MI_TIPO := PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA   => UN_COMPANIA
                                         ,UN_NOMBRE     => 'TIPO DE COMPROBANTE DETERIORO NIIF'
                                         ,UN_MODULO     => PCK_DATOS.FC_MODULOCONTABILIDAD
                                         ,UN_FECHA_PAR  => SYSDATE);

    FOR MI_RS IN (SELECT  PLAN_CONTABLE.COMPANIA,
                        PLAN_CONTABLE.ANO,
                        PLAN_CONTABLE.APLICA_DETERIORO,
                        PLAN_CONTABLE.CODIGO,
                        PLAN_CONTABLE.NOMBRE,
                        PLAN_CONTABLE.CODIGO   CUENTA
         FROM PLAN_CONTABLE
         WHERE PLAN_CONTABLE.COMPANIA =UN_COMPANIA
               AND PLAN_CONTABLE.ANO =UN_ANO
               AND PLAN_CONTABLE.APLICA_DETERIORO NOT IN (0)
               AND PLAN_CONTABLE.CLASECUENTA = 'C'
               AND MAN_CEN_CTO + MAN_AUX_TER + MAN_AUX_GEN <> 0
         GROUP BY PLAN_CONTABLE.COMPANIA,
                  PLAN_CONTABLE.ANO,
                  PLAN_CONTABLE.APLICA_DETERIORO,
                  PLAN_CONTABLE.CODIGO,
                  PLAN_CONTABLE.NOMBRE) LOOP

        IF MI_RS.CUENTA <>  MI_STRANTERIOR THEN
             IF LENGTH(MI_STRCUENTAS) > 0 THEN
                MI_STRCUENTAS := MI_STRCUENTAS || ',';
            END IF;
            MI_STRCUENTAS := MI_STRCUENTAS ||MI_RS.CUENTA;
            MI_STRANTERIOR := MI_RS.CUENTA;
        END IF;
    END LOOP;


          MI_CONDICION := ' COMPANIA='''||UN_COMPANIA||'''
                           AND ANO='||UN_ANO||'
                           AND MES='||UN_MES||''  ;
     BEGIN
        BEGIN
            PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETERIORO_CARTERA',
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION );

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_DELETEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;

    MI_TASA := 0;

     MI_CAMPOS := 'COMPANIA,ANO,MES,TIPO_FACTURA,NUMERO_FACTURA,EDAD,TASA,DESCRIPCION,CENTRO_COSTO,TERCERO,SUCURSAL,AUXILIAR,REFERENCIA, MES_FACTURA, CUENTA,
                       CONSECUTIVO, ANO_FACTURA, FECHA_FACTURA, FECHA_VENCIMIENTO,DATE_CREATED,CREATED_BY ';

    MI_VALORES := 'SELECT C.COMPANIA,
               '||UN_ANO||' ANO1,
               '||UN_MES||' MES1,
               C.TIPO,
               C.NUMERO,
               NVL(CASE WHEN TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YY'') - (C.FECHA_VCN_DOC + '||MI_DIAS||') < 0 THEN 0
                     ELSE TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YY'') - (C.FECHA_VCN_DOC + '||MI_DIAS||' )
               END,0) EDAD,
               '||MI_TASA||'    TASA,
               NVL(C.DESCRIPCION,''.'') DESCRIPCION1,
               NVL(C.CENTRO_COSTO,''9999999999'') CENTRO_COSTO1,
               NVL(C.TERCERO,''99999999999'') TERCERO1,
               NVL(C.SUCURSAL,''999'') SUCURSAL1,
               NVL(C.AUXILIAR,''9999999999999999'') AUXILIAR1,
               NVL(C.REFERENCIA,''9999999999999999'') REFERENCIA1,
               EXTRACT(MONTH FROM C.FECHA) MES_FACTURA,
               D.CUENTA,
               D.CONSECUTIVO,
               C.ANO   ANO_FACTURA,
               C.FECHA,
               C.FECHA_VCN_DOC,
               CURRENT_DATE,
               '''||UN_USUARIO||'''
         FROM COMPROBANTE_CNT C
         INNER JOIN TIPO_COMPROBANTE TC ON C.COMPANIA = TC.COMPANIA
                                           AND C.TIPO = TC.CODIGO
         INNER JOIN DETALLE_COMPROBANTE_CNT D ON C.NUMERO = D.COMPROBANTE
                                                 AND C.TIPO = D.TIPO_CPTE
                                                 AND C.ANO = D.ANO
                                                 AND C.COMPANIA = D.COMPANIA
         WHERE C.COMPANIA = '''||UN_COMPANIA||'''
               AND MONTHS_BETWEEN  (TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YY''),C.FECHA_VCN_DOC)  >= '||MI_MESES||'
               AND C.ANO BETWEEN 0 AND EXTRACT(YEAR FROM TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YY''))
               AND C.FECHA <=  TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YY'')
               AND TC.CLASE_CONTABLE IN (''V'')
               AND  D.CUENTA IN(' || PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(MI_STRCUENTAS)  || ')
               AND D.VALOR_DEBITO <> 0'   ;

     BEGIN
        BEGIN
            PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA   => 'DETERIORO_CARTERA',
                                     UN_ACCION  => 'IS',
                                     UN_CAMPOS  => MI_CAMPOS,
                                     UN_VALORES => MI_VALORES);


              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_INSERTDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;


      -- actualizar tasa TES
     MI_MERGEUSING := 'SELECT DISTINCT '''||UN_COMPANIA||''' COMPANIA,
                EXTRACT(YEAR FROM FECHA_FINAL) ANOTASA,
                TO_CHAR(TASAS_INTERES.FECHA_FINAL,''DD/MM/YY'')FECHA_FINAL,
                TASAS_INTERES.TASA_TES TASA
                FROM TASAS_INTERES
                WHERE FECHA_FINAL IS NOT NULL
                   AND TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YY'') BETWEEN FECHA_INICIAL AND FECHA_FINAL';

     MI_MERGEENLACE := ' TABLA.COMPANIA = VISTA.COMPANIA
                     AND TABLA.ANO = VISTA.ANOTASA
                     AND TABLA.MES = '||UN_MES||' ';

     MI_MERGEEXISTE := ' UPDATE SET TABLA.TASA = VISTA.TASA,
                              TABLA.DATE_MODIFIED         = SYSDATE,
                              TABLA.MODIFIED_BY           = '''||UN_USUARIO||''' ';

     BEGIN
      BEGIN
     PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'DETERIORO_CARTERA'
                                   ,UN_ACCION      => 'MM'
                                   ,UN_MERGEUSING  => MI_MERGEUSING
                                   ,UN_MERGEENLACE => MI_MERGEENLACE
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;

    -- 'CONSULTO SI ALGUNA CUENTA QUEDO CON TASA 0
    MI_RETORNO := 'CUENTAS CONTABLES QUE NO TIENEN CONFIGURACION DE TASA DE INTERES' || CHR(13) || CHR(10) ||
                       'VIGENCIA'||CHR(9)||'CUENTA CONTABLE'|| CHR(13) || CHR(10) ;

    FOR MI_RS IN (SELECT   DISTINCT COMPANIA, ANO, MES, CUENTA, TASA
                  FROM  DETERIORO_CARTERA
                 WHERE COMPANIA= UN_COMPANIA
                   AND ANO= UN_ANO
                   AND MES= UN_MES
                   AND TASA=0
    )LOOP
        MI_CUNETASNOCONFIGURADAS := TRUE;
        MI_RETORNO :=MI_RETORNO || MI_RS.ANO||CHR(9)|| MI_RS.CUENTA|| CHR(13) || CHR(10);

    END LOOP;

    IF NOT MI_CUNETASNOCONFIGURADAS THEN
         MI_RETORNO := '';
    END IF;

    --Actualizando valor factura
      MI_MERGEUSING := 'SELECT DISTINCT D.COMPANIA,
                   DC.ANO,
                   DC.MES,
                   D.FECHA,
                   TC.CLASE_CONTABLE,
                   D.CUENTA,
                   DC.VALOR_FACTURA,
                   D.VALOR_DEBITO,
                   D.VALOR_CREDITO,
                   NVL(DC.ABONOINICIAL,0) ABONOINICIAL,
                   NVL(D.ABONOINICIAL,0) ABONOINICIALD,
                   DC.NUMERO_FACTURA,
                   DC.TIPO_FACTURA,
                   DC.CONSECUTIVO,
                   D.TERCERO,
                   DC.ANO_FACTURA
             FROM DETALLE_COMPROBANTE_CNT D
             INNER JOIN DETERIORO_CARTERA DC ON D.COMPANIA = DC.COMPANIA
                                                AND D.ANO = DC.ANO_FACTURA
                                                AND D.TIPO_CPTE = DC.TIPO_FACTURA
                                                AND D.COMPROBANTE = DC.NUMERO_FACTURA
                                                AND D.CUENTA = DC.CUENTA
                                                AND D.TERCERO = DC.TERCERO
                                                AND D.CONSECUTIVO = DC.CONSECUTIVO
             INNER JOIN TIPO_COMPROBANTE TC ON D.COMPANIA = TC.COMPANIA
                                               AND D.TIPO_CPTE = TC.CODIGO
                WHERE D.COMPANIA =  '''||UN_COMPANIA||'''
                    AND DC.ANO ='||UN_ANO||'
                     AND DC.MES='||UN_MES||'
                     AND D.FECHA <=  TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YY'')
                     AND TC.CLASE_CONTABLE IN (''V'')
                     AND  D.CUENTA IN(' || PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(MI_STRCUENTAS)  || ')
                      AND D.VALOR_DEBITO<>0 ';

      MI_MERGEENLACE := 'TABLA.COMPANIA=VISTA.COMPANIA
                     AND VISTA.CUENTA = TABLA.CUENTA
                     AND VISTA.NUMERO_FACTURA = TABLA.NUMERO_FACTURA
                     AND VISTA.TIPO_FACTURA = TABLA.TIPO_FACTURA
                     AND VISTA.CONSECUTIVO = TABLA.CONSECUTIVO
                     AND VISTA.TERCERO = TABLA.TERCERO
                     AND TABLA.ANO =VISTA.ANO
                     AND TABLA.MES=VISTA.MES
                     AND TABLA.ANO_FACTURA = VISTA.ANO_FACTURA';

      MI_MERGEEXISTE := ' UPDATE SET  TABLA.VALOR_FACTURA=ABS(VISTA.VALOR_FACTURA+VISTA.VALOR_DEBITO-VISTA.VALOR_CREDITO),
                            TABLA.ABONOINICIAL=NVL(VISTA.ABONOINICIAL,0)+NVL(VISTA.ABONOINICIALD,0),
                            TABLA.DATE_MODIFIED         = SYSDATE,
                            TABLA.MODIFIED_BY           = '''||UN_USUARIO||''' ';

    BEGIN
     BEGIN
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'DETERIORO_CARTERA'
                                   ,UN_ACCION      => 'MM'
                                   ,UN_MERGEUSING  => MI_MERGEUSING
                                   ,UN_MERGEENLACE => MI_MERGEENLACE
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);


        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;

    --Actualizando saldo factura

       MI_MERGEUSING := 'SELECT D.COMPANIA,
                           D.CUENTA,
                           DC.ANO,
                           DC.MES,
                           SUM (DC.VALOR_AFECTACION) VALOR_AFECTACION,
                           SUM (D.VALOR_CREDITO) VALOR_CREDITO,
                           SUM (D.VALOR_DEBITO) VALOR_DEBITO,
                           DC.NUMERO_FACTURA,
                           DC.TIPO_FACTURA,
                           DC.ANO_FACTURA  ,
                           DC.CONSECUTIVO
                         FROM DETALLE_COMPROBANTE_CNT   D
                                INNER JOIN DETERIORO_CARTERA DC ON D.COMPANIA = DC.COMPANIA
                                  AND D.TIPO_CPTE_AFECT = DC.TIPO_FACTURA
                                  AND D.CMPTE_AFECTADO = DC.NUMERO_FACTURA
                                   AND D.ANO_AFECT = DC.ANO_FACTURA
                                  AND D.CUENTA = DC.CUENTA
                                INNER JOIN TIPO_COMPROBANTE TC ON D.COMPANIA = TC.COMPANIA
                                 AND D.TIPO_CPTE = TC.CODIGO
                     WHERE D.COMPANIA =  '''||UN_COMPANIA||'''
                        AND DC.ANO ='||UN_ANO||'
                        AND DC.MES='||UN_MES||'
                        AND D.FECHA <=  TO_DATE('''||UN_FECHACORTE||''',''DD/MM/YY'')
                        AND  D.CUENTA IN(' || PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS(MI_STRCUENTAS)  || ')
                        AND TC.CLASE_CONTABLE IN(''I'',''B'',''N'',''C'')
                    GROUP BY D.COMPANIA,
                           D.CUENTA,
                           DC.ANO,
                           DC.MES,
                           DC.NUMERO_FACTURA,
                           DC.TIPO_FACTURA,
                           DC.ANO_FACTURA,
                           DC.CONSECUTIVO                        ';

   MI_MERGEENLACE := 'TABLA.COMPANIA=VISTA.COMPANIA
                     AND VISTA.CUENTA = TABLA.CUENTA
                     AND VISTA.NUMERO_FACTURA = TABLA.NUMERO_FACTURA
                     AND VISTA.TIPO_FACTURA = TABLA.TIPO_FACTURA
                     AND VISTA.CONSECUTIVO = TABLA.CONSECUTIVO
                     AND TABLA.ANO_FACTURA = VISTA.ANO_FACTURA
                     AND TABLA.ANO =VISTA.ANO
                     AND TABLA.MES=VISTA.MES';

     MI_MERGEEXISTE := ' UPDATE SET   TABLA.VALOR_AFECTACION=ABS(NVL(VISTA.VALOR_AFECTACION,0)+VISTA.VALOR_CREDITO-VISTA.VALOR_DEBITO),
                            TABLA.DATE_MODIFIED         = SYSDATE,
                            TABLA.MODIFIED_BY           = '''||UN_USUARIO||''' ';

    BEGIN
     BEGIN
    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'DETERIORO_CARTERA'
                                   ,UN_ACCION      => 'MM'
                                   ,UN_MERGEUSING  => MI_MERGEUSING
                                   ,UN_MERGEENLACE => MI_MERGEENLACE
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);


        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;

     MI_MERGEUSING := 'SELECT D.COMPANIA ,
                       D.CUENTA,
                       DC.ANO,
                       DC.MES,
                       VALOR_FACTURA,
                       VALOR_AFECTACION,
                       DC.ABONOINICIAL,
                       DC.TIPO_FACTURA,
                       DC.NUMERO_FACTURA,
                       DC.CONSECUTIVO,
                       DC.ANO_FACTURA
                 FROM DETALLE_COMPROBANTE_CNT D
                 INNER JOIN DETERIORO_CARTERA DC ON D.CONSECUTIVO = DC.CONSECUTIVO
                                                    AND D.ANO = DC.ANO_FACTURA
                                                    AND D.COMPROBANTE = DC.NUMERO_FACTURA
                                                    AND D.TIPO_CPTE = DC.TIPO_FACTURA
                                                    AND D.CUENTA = DC.CUENTA
                                                    AND D.COMPANIA = DC.COMPANIA
                  WHERE D.COMPANIA =  '''||UN_COMPANIA||'''
                        AND DC.ANO ='||UN_ANO||'
                        AND DC.MES='||UN_MES||'                                                    ';

    MI_MERGEENLACE := 'TABLA.COMPANIA=VISTA.COMPANIA
                     AND TABLA.ANO =VISTA.ANO
                     AND TABLA.MES=VISTA.MES
                     AND TABLA.CUENTA = VISTA.CUENTA
                     AND  TABLA.TIPO_FACTURA =VISTA.TIPO_FACTURA
                     AND TABLA.NUMERO_FACTURA = VISTA.NUMERO_FACTURA
                     AND TABLA.CONSECUTIVO = VISTA.CONSECUTIVO
                     AND TABLA.ANO_FACTURA = VISTA.ANO_FACTURA';

     MI_MERGEEXISTE := ' UPDATE SET TABLA.SALDO=NVL(TABLA.VALOR_FACTURA,0)-NVL(TABLA.VALOR_AFECTACION,0)-NVL(TABLA.ABONOINICIAL,0),
                            TABLA.DATE_MODIFIED         = SYSDATE,
                            TABLA.MODIFIED_BY           = '''||UN_USUARIO||''' ';

    BEGIN
     BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'DETERIORO_CARTERA'
                                   ,UN_ACCION      => 'MM'
                                   ,UN_MERGEUSING  => MI_MERGEUSING
                                   ,UN_MERGEENLACE => MI_MERGEENLACE
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);


        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
     END;

          MI_CONDICION := ' COMPANIA='''||UN_COMPANIA||'''
                           AND ANO='||UN_ANO||'
                           AND MES='||UN_MES||'
                           AND (SALDO<=0 OR TASA = 0) ';
     BEGIN
        BEGIN
           PCK_DATOS.GL_RTA  := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETERIORO_CARTERA',
                                             UN_ACCION    => 'E',
                                             UN_CONDICION => MI_CONDICION );

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_DELETEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;

   --Actualizando valor del periodo

  MI_MERGEUSING := 'SELECT D.COMPANIA,  D.CUENTA,
                           DC.ANO,
                           DC.MES,
                           VALOR_FACTURA,
                           VALOR_AFECTACION,
                           DC.ABONOINICIAL,
                           DC.TASA,
                           D.TIPO_CPTE,
                           D.COMPROBANTE,
                           DC.EDAD,
                           DC.SALDO,
                           DC.TIPO_FACTURA,
                           DC.NUMERO_FACTURA,
                           DC.CONSECUTIVO,
                           DC.ANO_FACTURA
                     FROM DETALLE_COMPROBANTE_CNT D
                     INNER JOIN DETERIORO_CARTERA DC ON D.COMPANIA = DC.COMPANIA
                                                        AND D.ANO = DC.ANO_FACTURA
                                                        AND D.COMPROBANTE = DC.NUMERO_FACTURA
                                                        AND D.TIPO_CPTE = DC.TIPO_FACTURA
                                                        AND D.CUENTA = DC.CUENTA
                                                        AND D.CONSECUTIVO = DC.CONSECUTIVO
             WHERE DC.COMPANIA =  '''||UN_COMPANIA||'''
                        AND DC.ANO ='||UN_ANO||'
                        AND DC.MES='||UN_MES||' ';


    MI_MERGEENLACE := 'TABLA.COMPANIA= VISTA.COMPANIA
                     AND TABLA.ANO = VISTA.ANO
                     AND TABLA.MES= VISTA.MES
                     AND TABLA.CUENTA = VISTA.CUENTA
                     AND TABLA.TIPO_FACTURA =VISTA.TIPO_CPTE
                     AND TABLA.NUMERO_FACTURA = VISTA.COMPROBANTE
                     AND TABLA.CONSECUTIVO = VISTA.CONSECUTIVO';

     MI_MERGEEXISTE := ' UPDATE SET TABLA.VALOR      =  FLOOR(VISTA.SALDO * (POWER((1+VISTA.TASA/100),(1/365))-1) *';
     --<TICKET:7727585_DETERIORO AUTOR:CPEREZ FECHA:2023-03-22> Se ajusta para que calculo la edad solo del mes calculado no de todo el periodo
     MI_MERGEEXISTE := MI_MERGEEXISTE || '( TO_DATE(''' || EXTRACT(DAY FROM UN_FECHACORTE) || '/' || EXTRACT(MONTH FROM UN_FECHACORTE) || '/' || EXTRACT(YEAR FROM UN_FECHACORTE) ||  ''',''DD/MM/YY'') - TO_DATE(''01/'  || EXTRACT(MONTH FROM UN_FECHACORTE) || '/' || EXTRACT(YEAR FROM UN_FECHACORTE) || ''',''DD/MM/YY'') +1 )' ;

     MI_MERGEEXISTE := MI_MERGEEXISTE || ' *100+0.501)/100,
                            TABLA.DETERIORO_ACUMULADO=   FLOOR(VISTA.SALDO * (POWER((1+VISTA.TASA/100),(1/365))-1) * VISTA.EDAD*100+0.501)/100,
                            TABLA.DATE_MODIFIED         = SYSDATE,
                            TABLA.MODIFIED_BY           = '''||UN_USUARIO||''' ';



     BEGIN
      BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'DETERIORO_CARTERA'
                                   ,UN_ACCION      => 'MM'
                                   ,UN_MERGEUSING  => MI_MERGEUSING
                                   ,UN_MERGEENLACE => MI_MERGEENLACE
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);


        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;

  MI_MERGEUSING := 'SELECT D.COMPANIA,
                             D.CUENTA,D.ANO ,
                             D.MES,
                             NVL(D.DETERIORO_ACUMULADO ,0)DETERIORO_ACUMULADO,
                             NVL(A.DETERIORO_ACUMULADO,0) DETERIORO_ACUMULADOA,
                             D.TIPO_FACTURA,
                             D.NUMERO_FACTURA,
                             D.CONSECUTIVO,
                             D.SALDO
                     FROM DETERIORO_CARTERA  D LEFT JOIN DETERIORO_CARTERA  A
                                   ON      D.COMPANIA = A.COMPANIA
                                   AND     D.TIPO_FACTURA = A.TIPO_FACTURA
                                   AND     D.NUMERO_FACTURA = A.NUMERO_FACTURA
                                   AND     D.CUENTA = A.CUENTA
                                   AND     D.CONSECUTIVO = A.CONSECUTIVO
                                   AND    A.ANO = CASE WHEN D.MES=12 THEN D.ANO ELSE D.ANO-1   END
                                   AND    A.MES = CASE WHEN D.MES=12 THEN D.MES-6  ELSE D.MES +6 END
                     WHERE D.COMPANIA =  '''||UN_COMPANIA||'''
                        AND D.ANO ='||UN_ANO||'
                        AND D.MES='||UN_MES||'                                     ';

    MI_MERGEENLACE := 'TABLA.COMPANIA=VISTA.COMPANIA
                     AND TABLA.ANO = VISTA.ANO
                     AND TABLA.MES=VISTA.MES
                     AND TABLA.TIPO_FACTURA = VISTA.TIPO_FACTURA
                     AND TABLA.NUMERO_FACTURA = VISTA.NUMERO_FACTURA
                     AND TABLA.CUENTA = VISTA.CUENTA
                     AND TABLA.CONSECUTIVO = VISTA.CONSECUTIVO';
     MI_MERGEEXISTE := ' UPDATE SET  TABLA.VALOR = CASE WHEN (VISTA.DETERIORO_ACUMULADOA -SALDO)=0 THEN 0 ELSE CASE WHEN VISTA.DETERIORO_ACUMULADOA = 0 THEN VISTA.DETERIORO_ACUMULADO ELSE  TABLA.VALOR END  END,
                            TABLA.DATE_MODIFIED         = SYSDATE,
                            TABLA.MODIFIED_BY           = '''||UN_USUARIO||''' ';

    BEGIN
      BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA       => 'DETERIORO_CARTERA'
                                   ,UN_ACCION      => 'MM'
                                   ,UN_MERGEUSING  => MI_MERGEUSING
                                   ,UN_MERGEENLACE => MI_MERGEENLACE
                                   ,UN_MERGEEXISTE => MI_MERGEEXISTE);


        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
     END;
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;

   MI_CAMPOS        := 'VALOR = SALDO - (DETERIORO_ACUMULADO -VALOR)';
   MI_CONDICION     := '    COMPANIA =  '''||UN_COMPANIA||'''
                        AND ANO ='||UN_ANO||'
                        AND MES='||UN_MES|| '
                        AND (SALDO - (DETERIORO_ACUMULADO -VALOR))- VALOR < 0 ';
   BEGIN
      BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETERIORO_CARTERA',
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;

   MI_CAMPOS        := 'VALOR = 0';
   MI_CONDICION     := '    COMPANIA =  '''||UN_COMPANIA||'''
                        AND ANO ='||UN_ANO||'
                        AND MES='||UN_MES|| '
                        AND ((VALOR_FACTURA - DETERIORO_ACUMULADO) < 0 AND VALOR <0)' ;
   BEGIN
      BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETERIORO_CARTERA',
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;

   MI_CAMPOS        := 'VALOR = 0';
   MI_CONDICION     := '    COMPANIA =  '''||UN_COMPANIA||'''
                        AND ANO ='||UN_ANO||'
                        AND MES='||UN_MES|| '
                        AND ((SALDO - DETERIORO_ACUMULADO) < 0 AND VALOR <0)' ;
   BEGIN
      BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETERIORO_CARTERA',
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;

   MI_CAMPOS        := ' DETERIORO_ACUMULADO = SALDO ';
   MI_CONDICION     := '    COMPANIA =  '''||UN_COMPANIA||'''
                        AND ANO ='||UN_ANO||'
                        AND MES='||UN_MES|| '
                        AND (SALDO - DETERIORO_ACUMULADO) < 0 ';
   BEGIN
      BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'DETERIORO_CARTERA',
                                                    UN_ACCION    => 'M',
                                                    UN_CAMPOS    => MI_CAMPOS,
                                                    UN_CONDICION => MI_CONDICION);
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTABILIDAD;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTABILIDAD THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                       UN_ERROR_COD  => PCK_ERRORES.ERR_CNT_UPDATEDETERIORO
                                      ,UN_REEMPLAZOS  => MI_MSGERROR);
   END;

  RETURN MI_RETORNO;
END FC_DETERIOROCUENTASEMESTRE;

END PCK_CONTABILIDAD4;