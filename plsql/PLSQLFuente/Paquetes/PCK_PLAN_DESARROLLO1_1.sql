CREATE OR REPLACE PACKAGE BODY PCK_PLAN_DESARROLLO1 AS

--1
FUNCTION FC_ACTUALIZARPLANINDICATIVO
(
 /*
      NAME              : FC_ACTUALIZARPLANINDICATIVO --> EN ACCESS validarSaldoFuentes()
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 02/03/2018
      TIME              : 15:40 PM
      SOURCE MODULE     : SysmanDES2018.01.01
      MODIFIER          : GUSTAVO ANDRÉS FIGUEREDO ÁVILA
      DATE MODIFIED     : 11/11/2021
      TIME              : 16:11
      MODIFICATIONS		: Ticket 7700529. Se añade filtro AND VIGENCIA_META = '||MI_RS.VIGENCIA_META||', debido a que en el ciclo
        				  actualiza los valores meta y avance con el valor del ultimo registro. 
      DESCRIPTION       : Funcion que lleva los datos de las tablas PI a las BP para actualizar las novedades por transacción                               

      @NAME:  actualizarPlanIndicativo
      @METHOD:  GET
    */

  UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_VIGENCIA            IN PI_PLAN_INDICATIVO.VIGENCIA_INICIAL%TYPE ,
  UN_TIPOT               IN PI_PLAN_INDICATIVO.TIPO%TYPE, 
  UN_NUMEROT             IN PI_PLAN_INDICATIVO.NUMERO%TYPE,
  UN_NOMBREDEPENDENCIA   IN PI_PLAN_INDICATIVO.DESCRIPCION%TYPE,
  UN_ACCION              IN PCK_SUBTIPOS.TI_ENTERO, 
  UN_USUARIO             IN PCK_SUBTIPOS.TI_USUARIO 

)RETURN CLOB

AS 

  MI_RS                     SYS_REFCURSOR;
  MI_RSGASTADO              SYS_REFCURSOR;
  MI_RSPROYECTADO           SYS_REFCURSOR;
  MI_RS1                    SYS_REFCURSOR;
  MI_SALDODISPONIBLE        NUMBER(20,2);
  MI_PROYECTADO             NUMBER(20,2);
  MI_CONSECUTIVO            VARCHAR2(50 CHAR);
  MI_MERGEUSING             PCK_SUBTIPOS.TI_MERGEUSING;
  MI_MERGEENLACE            PCK_SUBTIPOS.TI_MERGEENLACE;
  MI_MERGEEXISTE            PCK_SUBTIPOS.TI_MERGEEXISTE;
  MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
  MI_CONDICION              PCK_SUBTIPOS.TI_CONDICION; 
  MI_RETORNO                CLOB;
  MI_MSGERROR               PCK_SUBTIPOS.TI_CLAVEVALOR;


   TYPE VECT_PLAN IS RECORD 
   (
     INICIAL              VARCHAR2(100 CHAR),
     FINAL                VARCHAR2(100 CHAR),
     DEFINITIVO           VARCHAR2(100 CHAR),
     DEFECTO              VARCHAR2(100 CHAR)
   );  
   TYPE TI_VECTPLAN IS TABLE OF VECT_PLAN INDEX BY BINARY_INTEGER ;

   MI_VECT_PLAN TI_VECTPLAN;

BEGIN

  MI_RETORNO := PCK_PLAN_DESARROLLO.FC_VALIDARSALDOFUENTES(UN_COMPANIA  => UN_COMPANIA,
                                                             UN_VIGENCIA  => UN_VIGENCIA,
                                                             UN_TIPOT     => UN_TIPOT,
                                                             UN_NUMEROT   => UN_NUMEROT);


  IF MI_RETORNO <> '' THEN

    MI_RETORNO := ' REGISTRO DE INCONSISTENCIAS ' ||(CHR(10))||(CHR(13))||
                      '       FECHA : '||TO_CHAR(SYSDATE,'DD/MM/YYYY')||'
                            USUARIO : '||UN_USUARIO||'
                        DEPENDENCIA : '||UN_NOMBREDEPENDENCIA||'
                        TRANSACCIÓN : '||UN_NUMEROT||(CHR(10))||(CHR(13))||
                        MI_RETORNO ;

  END IF;


        MI_MERGEUSING := 'SELECT COMPANIA,
                          ID_PLAN,
                          VIGENCIA_INICIAL,
                          VALOR_PRESUPUESTO_FIN  - VALOR_PRESUPUESTO_INI PRESUPUESTO,
                          VALOR_COMPROMETIDO_FIN - VALOR_COMPROMETIDO_INI COMPROMETIDO,
                          VALOR_PAGADO_FIN       - VALOR_PAGADO_INI PAGADO,
                          VALOR_OBLIGACIONES_FIN - VALOR_OBLIGACIONES_INI OBLIGACIONES,
                          CODIGOBPIM,
                          DESCRIPCION,
                          RESPONSABLE
                        FROM PI_PLAN_INDICATIVO
                        WHERE COMPANIA         = '''||UN_COMPANIA||'''
                          AND VIGENCIA_INICIAL = '||UN_VIGENCIA||'
                          AND TIPO             = '''||UN_TIPOT||'''
                          AND NUMERO           = '||UN_NUMEROT||'
                          AND ESNUEVO          = 0 ';


        MI_MERGEENLACE := 'TABLA.COMPANIA         = VISTA.COMPANIA   
                       AND TABLA.ID               = VISTA.ID_PLAN         
                       AND TABLA.VIGENCIA_INICIAL = VISTA.VIGENCIA_INICIAL';   


        MI_MERGEEXISTE   :='UPDATE SET  PRESUPUESTO        = TABLA.PRESUPUESTO  + VISTA.PRESUPUESTO,  
                                        COMPROMETIDO       = TABLA.COMPROMETIDO + VISTA.COMPROMETIDO, 
                                        PAGADO             = TABLA.PAGADO       + VISTA.PAGADO,       
                                        VALOR_OBLIGACIONES = TABLA.VALOR_OBLIGACIONES + VISTA.OBLIGACIONES, 
                                        CODIGOBPIM         = VISTA.CODIGOBPIM, 
                                        DESCRIPCION        = VISTA.DESCRIPCION,                
                                        RESPONSABLE        = VISTA.RESPONSABLE ';                   


      BEGIN
         BEGIN
             PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PLAN_INDICATIVO',
                                                        UN_ACCION      =>'MM', 
                                                        UN_MERGEUSING  =>MI_MERGEUSING, 
                                                        UN_MERGEENLACE =>MI_MERGEENLACE,
                                                        UN_MERGEEXISTE => MI_MERGEEXISTE
                                                        );

          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PLANDES;
         END;
      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                  MI_MSGERROR(1).CLAVE := 'NUMERO';
                  MI_MSGERROR(1).VALOR := UN_NUMEROT;
                  MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                  MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                PCK_ERR_MSG.RAISE_WITH_MSG(
                                        UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTBPINDICATIVOVAL
                                        ,UN_REEMPLAZOS  => MI_MSGERROR  
                                        );     
      END; 

    --Para actualizar los datos en las demas transacciones activas. 

     MI_MERGEUSING := 'SELECT DISTINCT PI_TRANSACCION.COMPANIA,
                          PI_TRANSACCION.ANO,
                          PI_TRANSACCION.TIPO,
                          BP_PLAN_INDICATIVO.DESCRIPCION,
                          BP_PLAN_INDICATIVO.RESPONSABLE,
                          PI_PLAN_INDICATIVO.VIGENCIA_INICIAL,
                          PI_PLAN_INDICATIVO.ID_PLAN
                        FROM PI_PLAN_INDICATIVO
                          INNER JOIN PI_TRANSACCION
                            ON PI_PLAN_INDICATIVO.COMPANIA          = PI_TRANSACCION.COMPANIA
                            AND PI_PLAN_INDICATIVO.VIGENCIA_INICIAL = PI_TRANSACCION.ANO
                            AND PI_PLAN_INDICATIVO.TIPO             = PI_TRANSACCION.TIPO
                            AND PI_PLAN_INDICATIVO.NUMERO           = PI_TRANSACCION.NUMERO
                          INNER JOIN BP_PLAN_INDICATIVO
                            ON PI_PLAN_INDICATIVO.COMPANIA          = BP_PLAN_INDICATIVO.COMPANIA
                            AND PI_PLAN_INDICATIVO.ID_PLAN          = BP_PLAN_INDICATIVO.ID
                            AND PI_PLAN_INDICATIVO.VIGENCIA_INICIAL = BP_PLAN_INDICATIVO.VIGENCIA_INICIAL
                        WHERE PI_TRANSACCION.COMPANIA             = '''||UN_COMPANIA||'''
                          AND PI_TRANSACCION.ESTADO               =''A'' 
                          AND PI_PLAN_INDICATIVO.ESNUEVO          IN (0)
                          AND PI_PLAN_INDICATIVO.VIGENCIA_INICIAL =  '||UN_VIGENCIA||'
                          AND (PI_PLAN_INDICATIVO.DESCRIPCION <> BP_PLAN_INDICATIVO.DESCRIPCION 
                              OR PI_PLAN_INDICATIVO.RESPONSABLE <> BP_PLAN_INDICATIVO.RESPONSABLE)'; 


     MI_MERGEENLACE := 'TABLA.COMPANIA            = VISTA.COMPANIA   
                       AND TABLA.ID_PLAN          = VISTA.ID_PLAN         
                       AND TABLA.VIGENCIA_INICIAL = VISTA.VIGENCIA_INICIAL';   


     MI_MERGEEXISTE   :='UPDATE SET DESCRIPCION    = VISTA.DESCRIPCION,  
                                    RESPONSABLE    = VISTA.RESPONSABLE';                                  


     BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'PI_PLAN_INDICATIVO',
                                                    UN_ACCION      =>'MM', 
                                                    UN_MERGEUSING  =>MI_MERGEUSING, 
                                                    UN_MERGEENLACE =>MI_MERGEENLACE,
                                                    UN_MERGEEXISTE =>MI_MERGEEXISTE
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := UN_NUMEROT;
                        MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                        MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTDATOSRESPINDI
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END; 

    --Se eliminan los que se quitaron en la transacción actual.              
    <<RECORRER_PINDICATIVO>>
    FOR MI_RS IN (SELECT ID_PLAN                                   
                 FROM   PI_PLAN_INDICATIVO                        
                 WHERE  COMPANIA         = UN_COMPANIA       
                   AND  TIPO             = UN_TIPOT
                   AND  NUMERO           = UN_NUMEROT
                   AND  VIGENCIA_INICIAL = UN_VIGENCIA  
                   AND  ESNUEVO          = 0                      
                   AND  DEBE_ELIMINAR NOT IN(0))LOOP

          MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA||'''
                   AND ID_PLAN       = '''||MI_RS.ID_PLAN||'''
                   AND VIGENCIA_PLAN = '||UN_VIGENCIA||' ';


     BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PLAN_INDICATIVO_FUENTES',
                                                    UN_ACCION      =>'E', 
                                                    UN_CONDICION   =>MI_CONDICION
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'PLAN';
                        MI_MSGERROR(1).VALOR := MI_RS.ID_PLAN;
                        MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                        MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ELIFUENTESPI
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;       

      MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA||'''
                   AND ID_PLAN       = '''||MI_RS.ID_PLAN||'''
                   AND VIGENCIA_PLAN = '||UN_VIGENCIA||' ';


     BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PLAN_INDICATIVO_METAS',
                                                    UN_ACCION      =>'E', 
                                                    UN_CONDICION   =>MI_CONDICION
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'PLAN';
                        MI_MSGERROR(1).VALOR := MI_RS.ID_PLAN;
                        MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                        MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ELIBPINDICATIVO
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;       



      MI_CONDICION := 'COMPANIA         = '''||UN_COMPANIA||'''
                   AND ID               = '''||MI_RS.ID_PLAN||'''
                   AND VIGENCIA_INICIAL = '||UN_VIGENCIA||' ';

     BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PLAN_INDICATIVO',
                                                    UN_ACCION      =>'E', 
                                                    UN_CONDICION   =>MI_CONDICION
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'PLAN';
                        MI_MSGERROR(1).VALOR := MI_RS.ID_PLAN;
                        MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                        MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ELIBPINDICATIVO
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;           

    END LOOP RECORRER_PINDICATIVO;




  --Para insertar los nuevos registros se debe generar el ID de acuerdo al plan indicativo real y actualizar todas las tablas.  
  <<RECORRER_PINDICATIVO>>
  FOR MI_RS IN (SELECT COMPANIA,                               
                       ID_PLAN,                                
                       TIPO,                                   
                       NUMERO,                                 
                       VIGENCIA_INICIAL,                       
                       VIGENCIA_FINAL,                         
                       DESCRIPCION,                            
                       DEPENDENCIA_FIN,                        
                       RESPONSABLE,                            
                       SUCURSAL,                               
                       TIPO_META_PLAN,                         
                       VALOR_PRESUPUESTO_FIN,                  
                       VALOR_COMPROMETIDO_FIN,                 
                       VALOR_PAGADO_FIN,                       
                       VALOR_OBLIGACIONES_FIN,                 
                       PREDECESOR,                             
                       CODIGOBPIM                              
                FROM   PI_PLAN_INDICATIVO                      
                WHERE  COMPANIA         = UN_COMPANIA     
                  AND  VIGENCIA_INICIAL = UN_VIGENCIA 
                  AND  TIPO             = UN_TIPOT      
                  AND  NUMERO           = UN_NUMEROT
                  AND  ESNUEVO NOT IN (0)                      
                ORDER  BY ID_PLAN DESC )LOOP


  --Genero ID real de BP_PLAN_INDICATIVO para realizar el insert del HEADER
   MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_CONSECUTIVONIVEL(UN_COMPANIA   => UN_COMPANIA,
                                                        UN_NIVELINI   => UN_ACCION,
                                                        UN_PREDECESOR => MI_RS.PREDECESOR);


   MI_CAMPOS := 'COMPANIA,
                ID,
                VIGENCIA_INICIAL,
                VIGENCIA_FINAL,
                DESCRIPCION,
                DEPENDENCIA,
                RESPONSABLE,
                SUCURSAL,
                TIPO_META_PLAN,
                PRESUPUESTO,
                COMPROMETIDO,
                PAGADO,
                VALOR_OBLIGACIONES,
                PREDECESOR,
                CODIGOBPIM,
                CREATED_BY,
                DATE_CREATED';    

  MI_VALORES := ' '''||MI_RS.COMPANIA||''',
                  '''||MI_CONSECUTIVO||''', 
                  '||MI_RS.VIGENCIA_INICIAL||',
                  '||MI_RS.VIGENCIA_FINAL||',
                  '''||MI_RS.DESCRIPCION|| ''',
                  '''||MI_RS.DEPENDENCIA_FIN|| ''',
                  '''||MI_RS.RESPONSABLE|| ''', 
                  '''||MI_RS.SUCURSAL|| ''' ,
                  '''||MI_RS.TIPO_META_PLAN|| ''' ,
                  '||NVL(MI_RS.VALOR_PRESUPUESTO_FIN,0)|| ',
                  '||NVL(MI_RS.VALOR_COMPROMETIDO_FIN,0)|| ',
                  '||NVL(MI_RS.VALOR_PAGADO_FIN,0)|| ',
                  '||NVL(MI_RS.VALOR_OBLIGACIONES_FIN,0)|| ',
                  '||MI_RS.PREDECESOR||',
                  '''||MI_RS.CODIGOBPIM||''',
                  '''||UN_USUARIO||''',
                  SYSDATE';              


      BEGIN
          BEGIN
             PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA   =>'BP_PLAN_INDICATIVO',
                                                        UN_ACCION  =>'I', 
                                                        UN_CAMPOS  =>MI_CAMPOS, 
                                                        UN_VALORES =>MI_VALORES
                                                        );

          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PLANDES;
          END;
      EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSIDPINDICATIVO
                                              );     
      END; 


  --Duplico PI_PLAN_INDICATIVO y actualizo en PI_PLAN_INDICATIVO_FUENTES, PI_PLAN_INDICATIVO_METAS con el nuevo ID      

  MI_CAMPOS := 'COMPANIA,
                TIPO,
                NUMERO,
                ID_PLAN,
                VIGENCIA_INICIAL,
                VIGENCIA_FINAL,
                DESCRIPCION,
                DEPENDENCIA_FIN,
                RESPONSABLE,
                SUCURSAL,
                TIPO_META_PLAN,
                VALOR_PRESUPUESTO_FIN,
                VALOR_COMPROMETIDO_FIN,
                VALOR_OBLIGACIONES_FIN,
                VALOR_PAGADO_FIN';

  MI_VALORES := ' '''||MI_RS.COMPANIA||''',                   
                  '''||MI_RS.TIPO||''',
                 '||MI_RS.NUMERO||',
                 '||MI_CONSECUTIVO||',
                 '||MI_RS.VIGENCIA_INICIAL||',
                 '||MI_RS.VIGENCIA_FINAL||',
                 '''||MI_RS.DESCRIPCION||''',
                 '''||MI_RS.DEPENDENCIA_FIN||''',
                 '''||MI_RS.RESPONSABLE||''',
                 '''||MI_RS.SUCURSAL||''',                    
                 '''||MI_RS.TIPO_META_PLAN||''',               
                 '||NVL(MI_RS.VALOR_PRESUPUESTO_FIN, 0)||',
                 '||NVL(MI_RS.VALOR_COMPROMETIDO_FIN, 0)||', 
                 '||NVL(MI_RS.VALOR_OBLIGACIONES_FIN, 0)||', 
                 '||NVL(MI_RS.VALOR_PAGADO_FIN, 0)||'  ';                

    BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA   =>'PI_PLAN_INDICATIVO',
                                                    UN_ACCION  =>'I', 
                                                    UN_CAMPOS  =>MI_CAMPOS, 
                                                    UN_VALORES =>MI_VALORES
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSIDPINDICATIVO
                                              );     
      END; 


  MI_CAMPOS := 'ID_PLAN = '''||MI_CONSECUTIVO||''' ';  

  MI_CONDICION := 'COMPANIA      = '''||MI_RS.COMPANIA||'''
              AND  TIPO          = '''||MI_RS.TIPO||'''
              AND  NUMERO        = '||MI_RS.NUMERO||'
              AND  ID_PLAN       = '||MI_RS.ID_PLAN||'
              AND  VIGENCIA_PLAN = '||MI_RS.VIGENCIA_INICIAL||' ';

      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA   =>'PI_PLAN_INDICATIVO_FUENTES',
                                                    UN_ACCION  =>'M', 
                                                    UN_CAMPOS     =>MI_CAMPOS, 
                                                    UN_CONDICION  =>MI_CONDICION
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
              EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := MI_RS.NUMERO;
                        MI_MSGERROR(2).CLAVE := 'PLAN';
                        MI_MSGERROR(2).VALOR := MI_RS.ID_PLAN;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTIDPINDIFUENTES
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );    
      END;             

  MI_CAMPOS := 'ID_PLAN = '''||MI_CONSECUTIVO||''' ';  

  MI_CONDICION := 'COMPANIA      = '''||MI_RS.COMPANIA||'''
              AND  TIPO          = '''||MI_RS.TIPO||'''
              AND  NUMERO        = '||MI_RS.NUMERO||'
              AND  ID_PLAN       = '||MI_RS.ID_PLAN||'
              AND  VIGENCIA_PLAN = '||MI_RS.VIGENCIA_INICIAL||' ';

      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA   =>'PI_PLAN_INDICATIVO_METAS',
                                                    UN_ACCION  =>'M', 
                                                    UN_CAMPOS     =>MI_CAMPOS, 
                                                    UN_CONDICION  =>MI_CONDICION
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
              EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := MI_RS.NUMERO;
                        MI_MSGERROR(2).CLAVE := 'PLAN';
                        MI_MSGERROR(2).VALOR := MI_RS.ID_PLAN;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTIDPINDIMETAS
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );    
      END; 
   --Como el ID de la acción cambió también se debe actualizar dicho valor en el plan de adquisiciones para dicha accion(proyecto)

  MI_CAMPOS := 'ID_PLAN = '''||MI_CONSECUTIVO||''' ';  

  MI_CONDICION := 'COMPANIA      = '''||MI_RS.COMPANIA||'''
              AND  TIPO          = '''||MI_RS.TIPO||'''
              AND  NUMERO        = '||MI_RS.NUMERO||'
              AND  ID_PLAN       = '||MI_RS.ID_PLAN||'
              AND  VIGENCIA_INICIAL = '||MI_RS.VIGENCIA_INICIAL||' ';

      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA   =>'PI_PLAN_ADQUISICIONES',
                                                    UN_ACCION  =>'M', 
                                                    UN_CAMPOS     =>MI_CAMPOS, 
                                                    UN_CONDICION  =>MI_CONDICION
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
              EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := MI_RS.NUMERO;
                        MI_MSGERROR(2).CLAVE := 'PLAN';
                        MI_MSGERROR(2).VALOR := MI_RS.ID_PLAN;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTIDPINDIADQUI
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );    
      END;    

   --Se actualiza también los valores estimados por vigencia

  MI_CAMPOS := 'ID_PLAN = '''||MI_CONSECUTIVO||''' ';  

  MI_CONDICION := 'COMPANIA      = '''||MI_RS.COMPANIA||'''
              AND  TIPO          = '''||MI_RS.TIPO||'''
              AND  NUMERO        = '||MI_RS.NUMERO||'
              AND  ID_PLAN       = '||MI_RS.ID_PLAN||'
              AND  VIGENCIA_INICIAL = '||MI_RS.VIGENCIA_INICIAL||' ';

      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA      =>'PI_PLAN_ADQUISICIONES_VIGENCIA',
                                                    UN_ACCION     =>'M', 
                                                    UN_CAMPOS     =>MI_CAMPOS, 
                                                    UN_CONDICION  =>MI_CONDICION
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
              EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := MI_RS.NUMERO;
                        MI_MSGERROR(2).CLAVE := 'PLAN';
                        MI_MSGERROR(2).VALOR := MI_RS.ID_PLAN;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTIDPINDIADQUIVI
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );    
      END;  

   --Se actualizan tambien los valores en la programación física.

  MI_CAMPOS := ' ID_PLAN = '''||MI_CONSECUTIVO||''' ';  

  MI_CONDICION := ' COMPANIA      = '''||MI_RS.COMPANIA||'''
              AND  TIPO          = '''||MI_RS.TIPO||'''
              AND  NUMERO        = '||MI_RS.NUMERO||'
              AND  ID_PLAN       = '||MI_RS.ID_PLAN||'
              AND  VIGENCIA_INICIAL = '||MI_RS.VIGENCIA_INICIAL||'  ';

      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'PI_PROGRAMACION_FISICA',
                                                    UN_ACCION      =>'M', 
                                                    UN_CAMPOS  => MI_CAMPOS, --MI_MERGEUSING, 
                                                    UN_CONDICION => MI_CONDICION--MI_MERGEENLACE
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
              EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := MI_RS.NUMERO;
                        MI_MSGERROR(2).CLAVE := 'PLAN';
                        MI_MSGERROR(2).VALOR := MI_RS.ID_PLAN;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTIDPIPROGRFISICA
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );    
      END; 

  --Se insertan los registros de PI_PLAN_INDICATIVO_FUENTES Y PI_PLAN_INDICATIVO_METAS con los nuevos ID's


  MI_CAMPOS := 'COMPANIA,                         
            ID_PLAN,                          
            VIGENCIA_PLAN,                    
            VIGENCIA_META,
            DEPENDENCIA, 
            PRESUPUESTO,                      
            COMPROMETIDO,                     
            PAGADO,                           
            VALOR_DISPONIBLE,                 
            VALOR_OBLIGACIONES'; 

  MI_VALORES := 'SELECT COMPANIA,                         
                    ID_PLAN,                          
                    VIGENCIA_PLAN,                    
                    VIGENCIA_META,
                    DEPENDENCIA_FIN, 
                    VALOR_PRESUPUESTO_FIN,            
                    VALOR_COMPROMETIDO_FIN,           
                    VALOR_PAGADO_FIN,                 
                    VALOR_DISPONIBLE_FIN,             
                    VALOR_OBLIGACIONES_FIN            
             FROM   PI_PLAN_INDICATIVO_METAS       
             WHERE  COMPANIA = '''||MI_RS.COMPANIA||''' 
               AND  TIPO     = '''||MI_RS.TIPO||'''      
               AND  NUMERO   = '||MI_RS.NUMERO||'     
               AND  ID_PLAN  = '''||MI_CONSECUTIVO||'''              
               AND  ESNUEVO NOT IN(0)
               ';            

      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PLAN_INDICATIVO_METAS',
                                                    UN_ACCION      =>'IS', 
                                                    UN_CAMPOS  =>MI_CAMPOS, 
                                                    UN_VALORES =>MI_VALORES
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
              EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := MI_RS.NUMERO;
                        MI_MSGERROR(2).CLAVE := 'PLAN';
                        MI_MSGERROR(2).VALOR := MI_CONSECUTIVO;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSBPINDIMETAREGIS
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );    
      END;   


  MI_CAMPOS := 'COMPANIA,                         
            ID_PLAN,                          
            VIGENCIA_PLAN,                    
            VIGENCIA_META,                    
            FUENTE,                           
            PRESUPUESTO,                      
            COMPROMETIDO,                     
            PAGADO,                           
            VALOR_DISPONIBLE,                 
            VALOR_OBLIGACIONES'; 

  MI_VALORES := 'SELECT COMPANIA,                         
                    ID_PLAN,                          
                    VIGENCIA_PLAN,                    
                    VIGENCIA_META,                    
                    FUENTE,                           
                    VALOR_PRESUPUESTO_FIN,            
                    VALOR_COMPROMETIDO_FIN,           
                    VALOR_PAGADO_FIN,                 
                    VALOR_DISPONIBLE_FIN,             
                    VALOR_OBLIGACIONES_FIN            
             FROM   PI_PLAN_INDICATIVO_FUENTES       
             WHERE  COMPANIA = '''||MI_RS.COMPANIA||''' 
               AND  TIPO     = '''||MI_RS.TIPO||'''      
               AND  NUMERO   = '||MI_RS.NUMERO||'     
               AND  ID_PLAN  = '''||MI_CONSECUTIVO||'''              
               AND  ESNUEVO NOT IN(0)';            

      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PLAN_INDICATIVO_FUENTES',
                                                    UN_ACCION      =>'IS', 
                                                    UN_CAMPOS  =>MI_CAMPOS, 
                                                    UN_VALORES =>MI_VALORES
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
              EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := MI_RS.NUMERO;
                        MI_MSGERROR(2).CLAVE := 'PLAN';
                        MI_MSGERROR(2).VALOR := MI_CONSECUTIVO;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSBPINDIFUENREGIS
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );    
      END;       


   --Se elimina el registro duplicado en PI_PLAN_INDICATIVO   

   MI_CONDICION := 'COMPANIA = '''||MI_RS.COMPANIA||'''  
               AND  TIPO     = '''||MI_RS.TIPO||'''
               AND  NUMERO   =  '||MI_RS.NUMERO||'
               AND  ID_PLAN  = '''||MI_RS.ID_PLAN||''' ';

      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'PI_PLAN_INDICATIVO',
                                                    UN_ACCION      =>'E', 
                                                    UN_CONDICION   =>MI_CONDICION

                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
              EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := MI_RS.NUMERO;
                        MI_MSGERROR(2).CLAVE := 'PLAN';
                        MI_MSGERROR(2).VALOR := MI_RS.ID_PLAN;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ELIDUPLIPINDICATIVO
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );    
      END;                 

  END LOOP RECORRER_PINDICATIVO;  

  MI_MERGEUSING := 'SELECT   COMPANIA,                                                                             
                             ID_PLAN ,                                                                              
                             VIGENCIA_PLAN,                                                                         
                             VIGENCIA_META,
                             (VALOR_PRESUPUESTO_FIN  - VALOR_PRESUPUESTO_INI) PRESUPUESTO,
                             (VALOR_COMPROMETIDO_FIN - VALOR_COMPROMETIDO_INI) COMPROMETIDO,
                             (VALOR_PAGADO_FIN       - VALOR_PAGADO_INI) PAGADO,      
                             (VALOR_OBLIGACIONES_FIN - VALOR_OBLIGACIONES_INI) OBLIGACIONES,
                             (VALOR_DISPONIBLE_FIN   - VALOR_DISPONIBLE_INI) DISPONIBLE
                            FROM PI_PLAN_INDICATIVO_METAS
                       WHERE  COMPANIA      = '''||UN_COMPANIA||'''                                                                                    
                         AND  VIGENCIA_PLAN = '||UN_VIGENCIA||'                                                                                
                         AND  TIPO          = '''||UN_TIPOT||'''
                         AND  NUMERO        = '||UN_NUMEROT||' 
                         --AND  ESNUEVO       = 0
                         '; 


     MI_MERGEENLACE := 'TABLA.COMPANIA         = VISTA.COMPANIA   
                       AND TABLA.ID_PLAN       = VISTA.ID_PLAN         
                       AND TABLA.VIGENCIA_PLAN = VISTA.VIGENCIA_PLAN                                                                         
                       AND TABLA.VIGENCIA_META = VISTA.VIGENCIA_META';   


     MI_MERGEEXISTE   :='UPDATE SET PRESUPUESTO        = TABLA.PRESUPUESTO + VISTA.PRESUPUESTO,  
                                    COMPROMETIDO       = TABLA.COMPROMETIDO + VISTA.COMPROMETIDO,
                                    PAGADO             = TABLA.PAGADO       + VISTA.PAGADO,
                                    VALOR_OBLIGACIONES = TABLA.VALOR_OBLIGACIONES + VISTA.OBLIGACIONES,
                                    VALOR_DISPONIBLE   = TABLA.VALOR_DISPONIBLE + VISTA.DISPONIBLE';  

     BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PLAN_INDICATIVO_METAS',
                                                    UN_ACCION      =>'MM', 
                                                    UN_MERGEUSING  =>MI_MERGEUSING, 
                                                    UN_MERGEENLACE =>MI_MERGEENLACE,
                                                    UN_MERGEEXISTE =>MI_MERGEEXISTE
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := UN_NUMEROT;
                        MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                        MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTPIMETASVALORES
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END; 



       MI_MERGEUSING := 'SELECT   COMPANIA,
                             ID_PLAN,
                             VIGENCIA_PLAN,
                             VIGENCIA_META,
                             FUENTE,
                             (VALOR_PRESUPUESTO_FIN  - VALOR_PRESUPUESTO_INI) PRESUPUESTO,
                             (VALOR_COMPROMETIDO_FIN - VALOR_COMPROMETIDO_INI) COMPROMETIDO,
                             (VALOR_PAGADO_FIN       - VALOR_PAGADO_INI) PAGADO,      
                             (VALOR_OBLIGACIONES_FIN - VALOR_OBLIGACIONES_INI) OBLIGACIONES,
                             (VALOR_DISPONIBLE_FIN   - VALOR_DISPONIBLE_INI) DISPONIBLE
                            FROM PI_PLAN_INDICATIVO_FUENTES
                       WHERE  COMPANIA      = '''||UN_COMPANIA||'''                                                                                 
                         AND  VIGENCIA_PLAN = '||UN_VIGENCIA||'                                                                                
                         AND  TIPO          = '''||UN_TIPOT||'''                                                                                     
                         AND  NUMERO        = '||UN_NUMEROT||'
                         AND  ESNUEVO       = 0'; 


     MI_MERGEENLACE := 'TABLA.COMPANIA         = VISTA.COMPANIA   
                       AND TABLA.ID_PLAN       = VISTA.ID_PLAN         
                       AND TABLA.VIGENCIA_PLAN = VISTA.VIGENCIA_PLAN                                                                         
                       AND TABLA.VIGENCIA_META = VISTA.VIGENCIA_META
                       AND TABLA.FUENTE        = VISTA.FUENTE';   


     MI_MERGEEXISTE   :='UPDATE SET PRESUPUESTO        = TABLA.PRESUPUESTO + VISTA.PRESUPUESTO,  
                                    COMPROMETIDO       = TABLA.COMPROMETIDO + VISTA.COMPROMETIDO,
                                    PAGADO             = TABLA.PAGADO       + VISTA.PAGADO,
                                    VALOR_OBLIGACIONES = TABLA.VALOR_OBLIGACIONES + VISTA.OBLIGACIONES,
                                    VALOR_DISPONIBLE   = TABLA.VALOR_DISPONIBLE + VISTA.DISPONIBLE';  

     BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PLAN_INDICATIVO_FUENTES',
                                                    UN_ACCION      =>'MM', 
                                                    UN_MERGEUSING  =>MI_MERGEUSING, 
                                                    UN_MERGEENLACE =>MI_MERGEENLACE,
                                                    UN_MERGEEXISTE =>MI_MERGEEXISTE
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := UN_NUMEROT;
                        MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                        MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTPIFUENTESVALORES
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;         


    MI_CAMPOS := 'COMPANIA,                         
                  ID_PLAN,                          
                  VIGENCIA_PLAN,                    
                  VIGENCIA_META,                    
                  FUENTE,                           
                  PRESUPUESTO,                      
                  COMPROMETIDO,                     
                  PAGADO,                           
                  VALOR_DISPONIBLE,                 
                  VALOR_OBLIGACIONES ';

    MI_VALORES := 'SELECT COMPANIA,                         
                    ID_PLAN,                          
                    VIGENCIA_PLAN,                    
                    VIGENCIA_META,                    
                    FUENTE,                           
                    VALOR_PRESUPUESTO_FIN,            
                    VALOR_COMPROMETIDO_FIN,           
                    VALOR_PAGADO_FIN,                 
                    VALOR_DISPONIBLE_FIN,             
                    VALOR_OBLIGACIONES_FIN            
             FROM   PI_PLAN_INDICATIVO_FUENTES        
             WHERE  COMPANIA      = '''||UN_COMPANIA||'''   
               AND  ESNUEVO NOT IN(0)                     
               AND  TO_NUMBER(ID_PLAN) > 0
               AND  VIGENCIA_PLAN =  '||UN_VIGENCIA||' 
               AND  TIPO          = '''||UN_TIPOT||'''     
               AND  NUMERO        = '||UN_NUMEROT||'  ';                

    --BEGIN
     --  BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PLAN_INDICATIVO_FUENTES',
                                                    UN_ACCION      =>'IS', 
                                                    UN_CAMPOS  =>MI_CAMPOS, 
                                                    UN_VALORES =>MI_VALORES
                                                    );

           /*EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := UN_NUMEROT;
                        MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                        MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSPIFUENTESBP
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;  */

  --Aqui hacer arreglo y recorrer por cada campo que tenga diferencias.
  MI_VECT_PLAN(1).INICIAL    := 'F_ESTUDIO_PLAN_INI';
  MI_VECT_PLAN(1).FINAL      := 'F_ESTUDIO_PLAN_FIN';
  MI_VECT_PLAN(1).DEFINITIVO := 'F_ESTUDIO_PLAN';
  MI_VECT_PLAN(1).DEFECTO    := 'TO_DATE(''01/01/1900'',''DD/MM/YYYY'')';  

  MI_VECT_PLAN(2).INICIAL    := 'F_CONTRATO_PLAN_INI';
  MI_VECT_PLAN(2).FINAL      := 'F_CONTRATO_PLAN_FIN';
  MI_VECT_PLAN(2).DEFINITIVO := 'F_CONTRATO_PLAN';
  MI_VECT_PLAN(2).DEFECTO    := 'TO_DATE(''01/01/1900'',''DD/MM/YYYY'')';

  MI_VECT_PLAN(3).INICIAL    := 'FECHA_INI_ANT';
  MI_VECT_PLAN(3).FINAL      := 'FECHA_INI_ACT';
  MI_VECT_PLAN(3).DEFINITIVO := 'FECHA_INI';
  MI_VECT_PLAN(3).DEFECTO    := 'TO_DATE(''01/01/1900'',''DD/MM/YYYY'')';

  MI_VECT_PLAN(4).INICIAL    := 'FECHA_FIN_ANT';
  MI_VECT_PLAN(4).FINAL      := 'FECHA_FIN_ACT';
  MI_VECT_PLAN(4).DEFINITIVO := 'FECHA_FIN';
  MI_VECT_PLAN(4).DEFECTO    := 'TO_DATE(''01/01/1900'',''DD/MM/YYYY'')';  

  MI_VECT_PLAN(5).INICIAL    := 'CONTRATO_INI';
  MI_VECT_PLAN(5).FINAL      := 'CONTRATO_FIN';
  MI_VECT_PLAN(5).DEFINITIVO := 'CONTRATO';
  MI_VECT_PLAN(5).DEFECTO    := ''''''; 

  MI_VECT_PLAN(6).INICIAL    := 'EST_PREVIO_INI';
  MI_VECT_PLAN(6).FINAL      := 'EST_PREVIO_FIN';
  MI_VECT_PLAN(6).DEFINITIVO := 'EST_PREVIO';
  MI_VECT_PLAN(6).DEFECTO    := '''''';   

  MI_VECT_PLAN(7).INICIAL    := 'F_ESTUDIO_EJEC_INI';
  MI_VECT_PLAN(7).FINAL      := 'F_ESTUDIO_EJEC_FIN';
  MI_VECT_PLAN(7).DEFINITIVO := 'F_ESTUDIO_EJEC';
  MI_VECT_PLAN(7).DEFECTO    := 'TO_DATE(''01/01/1900'',''DD/MM/YYYY'')';     

  MI_VECT_PLAN(8).INICIAL    := 'F_CONTRATO_EJEC_INI';
  MI_VECT_PLAN(8).FINAL      := 'F_CONTRATO_EJEC_FIN';
  MI_VECT_PLAN(8).DEFINITIVO := 'F_CONTRATO_EJEC';
  MI_VECT_PLAN(8).DEFECTO    := 'TO_DATE(''01/01/1900'',''DD/MM/YYYY'')';   

  MI_VECT_PLAN(9).INICIAL    := 'F_INICIO_EJECUTADA_INI';
  MI_VECT_PLAN(9).FINAL      := 'F_INICIO_EJECUTADA_FIN';
  MI_VECT_PLAN(9).DEFINITIVO := 'F_INICIO_EJECUTADA';
  MI_VECT_PLAN(9).DEFECTO    := 'TO_DATE(''01/01/1900'',''DD/MM/YYYY'')';   

  MI_VECT_PLAN(10).INICIAL    := 'F_FIN_EJECUTADA_INI';
  MI_VECT_PLAN(10).FINAL      := 'F_FIN_EJECUTADA_FIN';
  MI_VECT_PLAN(10).DEFINITIVO := 'F_FIN_EJECUTADA';
  MI_VECT_PLAN(10).DEFECTO    := 'TO_DATE(''01/01/1900'',''DD/MM/YYYY'')';

  MI_VECT_PLAN(11).INICIAL    := 'CODIGO_UNSPSC_INI';
  MI_VECT_PLAN(11).FINAL      := 'CODIGO_UNSPSC_FIN';
  MI_VECT_PLAN(11).DEFINITIVO := 'CODIGO_UNSPSC';
  MI_VECT_PLAN(11).DEFECTO    := 'TO_DATE(''01/01/1900'',''DD/MM/YYYY'')';  

  MI_VECT_PLAN(12).INICIAL    := 'MODALIDAD_INI';
  MI_VECT_PLAN(12).FINAL      := 'MODALIDAD_FIN';
  MI_VECT_PLAN(12).DEFINITIVO := 'MODALIDAD';
  MI_VECT_PLAN(12).DEFECTO    := '''''';   

  MI_VECT_PLAN(13).INICIAL    := 'VALOR_ESTIMADO_INI';
  MI_VECT_PLAN(13).FINAL      := 'VALOR_ESTIMADO_FIN';
  MI_VECT_PLAN(13).DEFINITIVO := 'VALOR_ESTIMADO';
  MI_VECT_PLAN(13).DEFECTO    := '0';  

  MI_VECT_PLAN(14).INICIAL    := 'VALOR_ESTIMADO_VIG_INI';
  MI_VECT_PLAN(14).FINAL      := 'VALOR_ESTIMADO_VIG_FIN';
  MI_VECT_PLAN(14).DEFINITIVO := 'VALOR_ESTIMADO_VIG';
  MI_VECT_PLAN(14).DEFECTO    := '0';

  MI_VECT_PLAN(15).INICIAL    := 'VIGENCIAS_FUTURAS_INI';
  MI_VECT_PLAN(15).FINAL      := 'VIGENCIAS_FUTURAS_FIN';
  MI_VECT_PLAN(15).DEFINITIVO := 'VIGENCIAS_FUTURAS';
  MI_VECT_PLAN(15).DEFECTO    := '0';  

  MI_VECT_PLAN(16).INICIAL    := 'ESTADO_SOLICITUD_INI';
  MI_VECT_PLAN(16).FINAL      := 'ESTADO_SOLICITUD_FIN';
  MI_VECT_PLAN(16).DEFINITIVO := 'ESTADO_SOLICITUD';
  MI_VECT_PLAN(16).DEFECTO    := '''''';    

  MI_VECT_PLAN(17).INICIAL    := 'DESCRIPCION_INI';
  MI_VECT_PLAN(17).FINAL      := 'DESCRIPCION_FIN';
  MI_VECT_PLAN(17).DEFINITIVO := 'DESCRIPCION';
  MI_VECT_PLAN(17).DEFECTO    := '''''';  


  --Actualizacion de campos generales.

     MI_MERGEUSING := 'SELECT PI_PLAN_ADQUISICIONES.COMPANIA ,
                          PI_PLAN_ADQUISICIONES.VIGENCIA_INICIAL ,
                          PI_PLAN_ADQUISICIONES.ID_PLAN ,
                          PI_PLAN_ADQUISICIONES.CODIGO ,
                          PI_PLAN_ADQUISICIONES.VALOR_ESTIMADO_FIN     - PI_PLAN_ADQUISICIONES.VALOR_ESTIMADO_INI ESTIMADO ,
                          PI_PLAN_ADQUISICIONES.VALOR_ESTIMADO_VIG_FIN - PI_PLAN_ADQUISICIONES.VALOR_ESTIMADO_VIG_INI ESTIMADO_VIG
                        FROM PI_PLAN_ADQUISICIONES
                        WHERE PI_PLAN_ADQUISICIONES.COMPANIA       = '''||UN_COMPANIA||'''
                        AND PI_PLAN_ADQUISICIONES.TIPO             = '''||UN_TIPOT||'''
                        AND PI_PLAN_ADQUISICIONES.NUMERO           = '||UN_NUMEROT||'
                        AND PI_PLAN_ADQUISICIONES.VIGENCIA_INICIAL = '||UN_VIGENCIA||'
                        AND PI_PLAN_ADQUISICIONES.ESNUEVO          = 0 ';                            

      MI_MERGEENLACE := 'TABLA.COMPANIA          = VISTA.COMPANIA   
                     AND TABLA.VIGENCIA_INICIAL  = VISTA.VIGENCIA_INICIAL         
                     AND TABLA.ID_PLAN           = VISTA.ID_PLAN
                     AND TABLA.CODIGO            = VISTA.CODIGO';   


        MI_MERGEEXISTE   :='UPDATE SET VALOR_ESTIMADO      = TABLA.VALOR_ESTIMADO  + VISTA.ESTIMADO,  
                                       VALOR_ESTIMADO_VIG  = TABLA.VALOR_ESTIMADO_VIG + VISTA.ESTIMADO_VIG, 
                                       MODIFIED_BY         = '''||UN_USUARIO||''',       
                                       DATE_MODIFIED       = SYSDATE ';               


      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PLAN_ADQUISICIONES',
                                                    UN_ACCION      =>'MM', 
                                                    UN_MERGEUSING  =>MI_MERGEUSING, 
                                                    UN_MERGEENLACE =>MI_MERGEENLACE,
                                                    UN_MERGEEXISTE => MI_MERGEEXISTE
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := UN_NUMEROT;
                        MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                        MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTBPADQUIVALEST
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;


--Programacion fisica

      MI_MERGEUSING := 'SELECT COMPANIA ,
                            VIGENCIA_INICIAL ,
                            ID_PLAN ,
                            VIGENCIA ,
                            FECHA_INICIAL_FIN,
                            FECHA_FINAL_FIN,
                            NVL(PROGRAMADO_TR1_FIN,0) - NVL(PROGRAMADO_TR1_INI,0) PROGRAMADOTR1,
                            NVL(EJECUTADO_TR1_FIN ,0) - NVL(EJECUTADO_TR1_INI ,0) EJECUTADOTR1,
                            NVL(PROGRAMADO_TR1_FIN,0) - NVL(PROGRAMADO_TR1_INI,0) PROGRAMADOTR2,
                            NVL(EJECUTADO_TR1_FIN ,0) - NVL(EJECUTADO_TR1_INI ,0) EJECUTADOTR2,
                            NVL(PROGRAMADO_TR1_FIN,0) - NVL(PROGRAMADO_TR1_INI,0) PROGRAMADOTR3,
                            NVL(EJECUTADO_TR1_FIN ,0) - NVL(EJECUTADO_TR1_INI ,0) EJECUTADOTR3,
                            NVL(PROGRAMADO_TR1_FIN,0) - NVL(PROGRAMADO_TR1_INI,0) PROGRAMADOTR4,
                            NVL(EJECUTADO_TR1_FIN ,0) - NVL(EJECUTADO_TR1_INI ,0) EJECUTADOTR4,
                            POBLACION_OBJETIVO,
                            UBICACION_GEOGRAFICA,
                            IMPACTO_SOCIAL
                          FROM PI_PROGRAMACION_FISICA
                          WHERE COMPANIA       = '''||UN_COMPANIA||'''
                          AND TIPO             = '''||UN_TIPOT||'''
                          AND NUMERO           = '||UN_NUMEROT||'
                          AND VIGENCIA_INICIAL = '||UN_VIGENCIA||'';                            

      MI_MERGEENLACE := 'TABLA.COMPANIA          = VISTA.COMPANIA   
                     AND TABLA.VIGENCIA_INICIAL  = VISTA.VIGENCIA_INICIAL         
                     AND TABLA.ID_PLAN           = VISTA.ID_PLAN
                     AND TABLA.VIGENCIA          = VISTA.VIGENCIA';   


      MI_MERGEEXISTE   :='UPDATE SET FECHA_INICIAL             = VISTA.FECHA_INICIAL_FIN
                                    ,FECHA_FINAL              = VISTA.FECHA_FINAL_FIN
                                    ,PROGRAMADO_TR1           = NVL(TABLA.PROGRAMADO_TR1,0)  +  VISTA.PROGRAMADOTR1
                                    ,EJECUTADO_TR1            = NVL(TABLA.EJECUTADO_TR1,0)   +  VISTA.EJECUTADOTR1
                                    ,PROGRAMADO_TR2           = NVL(TABLA.PROGRAMADO_TR2,0)  +  VISTA.PROGRAMADOTR2
                                    ,EJECUTADO_TR2            = NVL(TABLA.EJECUTADO_TR2,0)   +  VISTA.EJECUTADOTR2
                                    ,PROGRAMADO_TR3           = NVL(TABLA.PROGRAMADO_TR3,0)  +  VISTA.PROGRAMADOTR3
                                    ,EJECUTADO_TR3            = NVL(TABLA.EJECUTADO_TR3,0)   +  VISTA.EJECUTADOTR3
                                    ,PROGRAMADO_TR4           = NVL(TABLA.PROGRAMADO_TR4,0)  +  VISTA.PROGRAMADOTR4
                                    ,EJECUTADO_TR4            = NVL(TABLA.EJECUTADO_TR4,0)   +  VISTA.EJECUTADOTR4
                                    ,POBLACION_OBJETIVO       = VISTA.POBLACION_OBJETIVO
                                    ,UBICACION_GEOGRAFICA     = VISTA.UBICACION_GEOGRAFICA
                                    ,IMPACTO_SOCIAL           = VISTA.IMPACTO_SOCIAL 
                                    ,MODIFIED_BY              = '''||UN_USUARIO||'''
                                    ,DATE_MODIFIED            = SYSDATE ';               


      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PROGRAMACION_FISICA',
                                                    UN_ACCION      =>'MM', 
                                                    UN_MERGEUSING  =>MI_MERGEUSING, 
                                                    UN_MERGEENLACE =>MI_MERGEENLACE,
                                                    UN_MERGEEXISTE => MI_MERGEEXISTE
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := UN_NUMEROT;
                        MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                        MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTBPROFISVALS
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;

  --Insertar nuevos registros de programacion fisica

  MI_CAMPOS := 'COMPANIA
              ,ID_PLAN
              ,VIGENCIA_INICIAL
              ,VIGENCIA
              ,FECHA_INICIAL
              ,FECHA_FINAL
              ,PROGRAMADO_TR1
              ,EJECUTADO_TR1
              ,PROGRAMADO_TR2
              ,EJECUTADO_TR2
              ,PROGRAMADO_TR3
              ,EJECUTADO_TR3
              ,PROGRAMADO_TR4
              ,EJECUTADO_TR4
              ,POBLACION_OBJETIVO
              ,UBICACION_GEOGRAFICA
              ,IMPACTO_SOCIAL
              ,CREATED_BY
              ,DATE_CREATED ';

  MI_VALORES := 'SELECT COMPANIA
                  ,ID_PLAN
                  ,VIGENCIA_INICIAL
                  ,VIGENCIA
                  ,FECHA_INICIAL_FIN
                  ,FECHA_FINAL_FIN
                  ,PROGRAMADO_TR1_FIN
                  ,EJECUTADO_TR1_FIN
                  ,PROGRAMADO_TR2_FIN
                  ,EJECUTADO_TR2_FIN
                  ,PROGRAMADO_TR3_FIN
                  ,EJECUTADO_TR3_FIN
                  ,PROGRAMADO_TR4_FIN
                  ,EJECUTADO_TR4_FIN
                  ,POBLACION_OBJETIVO
                  ,UBICACION_GEOGRAFICA
                  ,IMPACTO_SOCIAL
                  ,'''||UN_USUARIO||'''
                  ,SYSDATE         
             FROM   PI_PROGRAMACION_FISICA 
            WHERE COMPANIA       = '''||UN_COMPANIA||'''
            AND TIPO             = '''||UN_TIPOT||'''
            AND NUMERO           = '||UN_NUMEROT||'
            AND VIGENCIA_INICIAL = '||UN_VIGENCIA||'
            AND  ES_NUEVO NOT IN(0)';              

      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PROGRAMACION_FISICA',
                                                    UN_ACCION      =>'IS', 
                                                    UN_CAMPOS  =>MI_CAMPOS, 
                                                    UN_VALORES =>MI_VALORES
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
              EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := UN_NUMEROT;
                        MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                        MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSPROFISREGIS
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );    
      END;   

 --Actualización de los campos con cambios 
  FOR i IN NVL(MI_VECT_PLAN.FIRST,0).. NVL(MI_VECT_PLAN.LAST,0)LOOP
    DBMS_OUTPUT.PUT_LINE('MI CONTADOR---->' || I);
      MI_MERGEUSING := 'SELECT PI_PLAN_ADQUISICIONES.COMPANIA ,
                          PI_PLAN_ADQUISICIONES.VIGENCIA_INICIAL ,
                          PI_PLAN_ADQUISICIONES.ID_PLAN ,
                          PI_PLAN_ADQUISICIONES.CODIGO ,
                          '||MI_VECT_PLAN(i).FINAL||' FINAL
                        FROM PI_PLAN_ADQUISICIONES
                        WHERE PI_PLAN_ADQUISICIONES.COMPANIA       = '''||UN_COMPANIA||'''
                        AND PI_PLAN_ADQUISICIONES.TIPO             = '''||UN_TIPOT||'''
                        AND PI_PLAN_ADQUISICIONES.NUMERO           = '||UN_NUMEROT||'
                        AND PI_PLAN_ADQUISICIONES.VIGENCIA_INICIAL = '||UN_VIGENCIA||'
                        AND PI_PLAN_ADQUISICIONES.ESNUEVO          = 0 
                        AND NVL('||MI_VECT_PLAN(i).INICIAL||','||MI_VECT_PLAN(i).DEFECTO||') <> '||MI_VECT_PLAN(i).FINAL||'';                            

      MI_MERGEENLACE := 'TABLA.COMPANIA          = VISTA.COMPANIA   
                     AND TABLA.VIGENCIA_INICIAL  = VISTA.VIGENCIA_INICIAL         
                     AND TABLA.ID_PLAN           = VISTA.ID_PLAN
                     AND TABLA.CODIGO            = VISTA.CODIGO';   


      MI_MERGEEXISTE   :='UPDATE SET  TABLA.'||MI_VECT_PLAN(i).DEFINITIVO||' = VISTA.FINAL,
                                      TABLA.MODIFIED_BY                      = '''||UN_USUARIO||''',       
                                      TABLA.DATE_MODIFIED                    = SYSDATE ';  

      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PLAN_ADQUISICIONES',
                                                    UN_ACCION      =>'MM', 
                                                    UN_MERGEUSING  =>MI_MERGEUSING, 
                                                    UN_MERGEENLACE =>MI_MERGEENLACE,
                                                    UN_MERGEEXISTE => MI_MERGEEXISTE
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := UN_NUMEROT;
                        MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                        MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTBPADQUIVALEST
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END; 

  END LOOP;

  --Se actualiza el nombre en las demas transacciones activas para evitar que se pierda el cambio.    

        MI_MERGEUSING := 'SELECT DISTINCT PI_TRANSACCION.COMPANIA,
                            BP_PLAN_ADQUISICIONES.VIGENCIA_INICIAL,
                            BP_PLAN_ADQUISICIONES.ID_PLAN,
                            BP_PLAN_ADQUISICIONES.CODIGO,
                            BP_PLAN_ADQUISICIONES.DESCRIPCION,
                            PI_TRANSACCION.ANO,
                            PI_TRANSACCION.TIPO
                          FROM BP_PLAN_ADQUISICIONES
                          INNER JOIN PI_PLAN_ADQUISICIONES
                          ON PI_PLAN_ADQUISICIONES.COMPANIA          = BP_PLAN_ADQUISICIONES.COMPANIA
                          AND PI_PLAN_ADQUISICIONES.VIGENCIA_INICIAL = BP_PLAN_ADQUISICIONES.VIGENCIA_INICIAL
                          AND PI_PLAN_ADQUISICIONES.ID_PLAN          = BP_PLAN_ADQUISICIONES.ID_PLAN
                          AND PI_PLAN_ADQUISICIONES.CODIGO           = BP_PLAN_ADQUISICIONES.CODIGO
                          INNER JOIN PI_TRANSACCION
                          ON PI_PLAN_ADQUISICIONES.COMPANIA          = PI_TRANSACCION.COMPANIA
                          AND PI_PLAN_ADQUISICIONES.VIGENCIA_INICIAL = PI_TRANSACCION.ANO
                          AND PI_PLAN_ADQUISICIONES.TIPO             = PI_TRANSACCION.TIPO
                          AND PI_PLAN_ADQUISICIONES.NUMERO           = PI_TRANSACCION.NUMERO
                          WHERE BP_PLAN_ADQUISICIONES.COMPANIA       = '''||UN_COMPANIA||'''
                          AND BP_PLAN_ADQUISICIONES.VIGENCIA_INICIAL = '||UN_VIGENCIA||'
                          AND PI_TRANSACCION.ESTADO                  = ''A''
                          AND BP_PLAN_ADQUISICIONES.DESCRIPCION     <> PI_PLAN_ADQUISICIONES.DESCRIPCION_FIN ';


        MI_MERGEENLACE := 'TABLA.COMPANIA         = VISTA.COMPANIA   
                       AND TABLA.VIGENCIA_INICIAL = VISTA.VIGENCIA_INICIAL         
                       AND TABLA.ID_PLAN          = VISTA.ID_PLAN
                       AND TABLA.CODIGO          = VISTA.CODIGO';   


        MI_MERGEEXISTE   :='UPDATE SET TABLA.DESCRIPCION_FIN   =  VISTA.DESCRIPCION ,
                                       TABLA.MODIFIED_BY       = '''||UN_USUARIO||''',       
                                       TABLA.DATE_MODIFIED     = SYSDATE '; 

      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'PI_PLAN_ADQUISICIONES',
                                                    UN_ACCION      =>'MM', 
                                                    UN_MERGEUSING  =>MI_MERGEUSING, 
                                                    UN_MERGEENLACE =>MI_MERGEENLACE,
                                                    UN_MERGEEXISTE => MI_MERGEEXISTE
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'VIGENCIA';
                        MI_MSGERROR(1).VALOR := UN_VIGENCIA;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTPIADQUIDESCR
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;         

 --Actualización de plan de adquisiciones por vigencia.  

        MI_MERGEUSING := 'SELECT COMPANIA,
                              VIGENCIA_INICIAL,
                              ID_PLAN,
                              CODIGO,
                              VALOR_ESTIMADO_FIN  - VALOR_ESTIMADO_INI ESTIMADO,
                              VALOR_EJECUTADO_FIN - VALOR_EJECUTADO_INI EJECUTADO
                            FROM PI_PLAN_ADQUISICIONES_VIGENCIA
                            WHERE COMPANIA           = '''||UN_COMPANIA||'''
                            AND TIPO                 = '''||UN_TIPOT||'''
                            AND NUMERO               = '||UN_NUMEROT||'
                            AND VIGENCIA_INICIAL     = '||UN_VIGENCIA||'
                            AND ESNUEVO              = 0
                            AND (VALOR_ESTIMADO_FIN <> VALOR_ESTIMADO_INI
                                  OR VALOR_EJECUTADO_FIN  <> VALOR_EJECUTADO_INI) ';


        MI_MERGEENLACE := 'TABLA.COMPANIA         = VISTA.COMPANIA   
                       AND TABLA.VIGENCIA_INICIAL = VISTA.VIGENCIA_INICIAL         
                       AND TABLA.ID_PLAN          = VISTA.ID_PLAN
                       AND TABLA.CODIGO           = VISTA.CODIGO';   


        MI_MERGEEXISTE   :='UPDATE SET TABLA.VALOR_ESTIMADO    = TABLA.VALOR_ESTIMADO  + VISTA.ESTIMADO,
                                       TABLA.VALOR_EJECUTADO   = TABLA.VALOR_EJECUTADO + VISTA.EJECUTADO,
                                       TABLA.MODIFIED_BY       = '''||UN_USUARIO||''',       
                                       TABLA.DATE_MODIFIED     = SYSDATE ';  

      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PLAN_ADQUISICIONES_VIGENCIA',
                                                    UN_ACCION      =>'MM', 
                                                    UN_MERGEUSING  =>MI_MERGEUSING, 
                                                    UN_MERGEENLACE =>MI_MERGEENLACE,
                                                    UN_MERGEEXISTE => MI_MERGEEXISTE
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := UN_NUMEROT;
                        MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                        MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTBPADQUIVIVALEST
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END; 

 --Insertar nuevos registros de planAquisiciones  

   <<CREAR_PLANADQUISICONES>>

   FOR MI_RS IN (SELECT ID_PLAN,
                      CODIGO,
                      DESCRIPCION_FIN,
                      F_ESTUDIO_PLAN_FIN,
                      F_CONTRATO_PLAN_FIN,
                      FECHA_INI_ACT,
                      FECHA_FIN_ACT,
                      CONTRATO_FIN,
                      EST_PREVIO_FIN,
                      F_ESTUDIO_EJEC_FIN,
                      F_CONTRATO_EJEC_FIN,
                      CODIGO_UNSPSC_FIN,
                      MODALIDAD_FIN,
                      VALOR_ESTIMADO_FIN,
                      VALOR_ESTIMADO_VIG_FIN,
                      VIGENCIAS_FUTURAS_FIN,
                      ESTADO_SOLICITUD_FIN,
                      F_INICIO_EJECUTADA_FIN,
                      F_FIN_EJECUTADA_FIN
                    FROM PI_PLAN_ADQUISICIONES
                    WHERE COMPANIA         = UN_COMPANIA
                      AND TIPO             = UN_TIPOT
                      AND NUMERO           = UN_NUMEROT
                      AND VIGENCIA_INICIAL = UN_VIGENCIA
                      AND ESNUEVO NOT     IN(0)
                      AND DEBE_ELIMINAR    = 0) LOOP
        --(YBECERRA:10/12/2018) Se agregan comillas en MI_RS.ID_PLAN
        MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO (UN_TABLA    =>'BP_PLAN_ADQUISICIONES',
                                                            UN_CRITERIO => 'COMPANIA= '''||UN_COMPANIA||''' 
                                                                            AND VIGENCIA_INICIAL='||UN_VIGENCIA||' 
                                                                            AND ID_PLAN='''||MI_RS.ID_PLAN||'''',
                                                            UN_CAMPO    => 'CODIGO',
                                                            UN_INICIAL  => 1);      

        MI_CAMPOS := 'COMPANIA,
                      VIGENCIA_INICIAL,
                      ID_PLAN,
                      CODIGO,
                      DESCRIPCION,
                      F_ESTUDIO_PLAN,
                      F_CONTRATO_PLAN,
                      FECHA_INI,
                      FECHA_FIN,
                      CONTRATO,
                      EST_PREVIO,
                      F_ESTUDIO_EJEC,
                      F_CONTRATO_EJEC,
                      CODIGO_UNSPSC,
                      MODALIDAD,
                      VALOR_ESTIMADO,
                      VALOR_ESTIMADO_VIG,
                      VIGENCIAS_FUTURAS,
                      ESTADO_SOLICITUD,
                      F_INICIO_EJECUTADA,
                      F_FIN_EJECUTADA,
                      CREATED_BY,
                      DATE_CREATED';


        MI_VALORES := ' '''||UN_COMPANIA||''',
                        '||UN_VIGENCIA||',
                        '''||MI_RS.ID_PLAN||''',
                        '||MI_CONSECUTIVO||',
                        '''||MI_RS.DESCRIPCION_FIN||''',
                        '''||MI_RS.F_ESTUDIO_PLAN_FIN||''',
                        '''||MI_RS.F_CONTRATO_PLAN_FIN||''',
                        '''||MI_RS.FECHA_INI_ACT||''',
                        '''||MI_RS.FECHA_FIN_ACT||''',
                        '''||MI_RS.CONTRATO_FIN||''',
                        '''||MI_RS.EST_PREVIO_FIN||''',
                        '''||MI_RS.F_ESTUDIO_EJEC_FIN||''',
                        '''||MI_RS.F_CONTRATO_EJEC_FIN||''',
                        '''||MI_RS.CODIGO_UNSPSC_FIN||''',
                        '''||MI_RS.MODALIDAD_FIN||''',
                        '''||MI_RS.VALOR_ESTIMADO_FIN||''',
                        '''||MI_RS.VALOR_ESTIMADO_VIG_FIN||''',
                        '''||MI_RS.VIGENCIAS_FUTURAS_FIN||''',
                        '''||MI_RS.ESTADO_SOLICITUD_FIN||''',
                        '''||MI_RS.F_INICIO_EJECUTADA_FIN||''',
                        '''||MI_RS.F_FIN_EJECUTADA_FIN||''',
                        '''||UN_USUARIO||''',
                        SYSDATE';              

      BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PLAN_ADQUISICIONES',
                                                    UN_ACCION      =>'I', 
                                                    UN_CAMPOS  =>MI_CAMPOS, 
                                                    UN_VALORES =>MI_VALORES
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
              EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'PLAN';
                        MI_MSGERROR(1).VALOR := MI_RS.ID_PLAN;
                        MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                        MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSPADQUINUEVREGIS
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );    
      END;

   END LOOP CREAR_PLANADQUISICONES;

   --Una vez que se insertan todos los nuevos se eliminan los que se quitaron en la transacción actual.
    <<ELIMINAR_PLANADQUI>> 
   FOR MI_RS IN (SELECT ID_PLAN,                                  
                        CODIGO                                    
                 FROM   PI_PLAN_ADQUISICIONES                     
                 WHERE  COMPANIA         = UN_COMPANIA     
                   AND  TIPO             = UN_TIPOT
                   AND  NUMERO           = UN_NUMEROT
                   AND  VIGENCIA_INICIAL = UN_VIGENCIA
                   AND  ESNUEVO          = 0                      
                   AND  DEBE_ELIMINAR NOT IN(0) )LOOP

      --Registros hijos por vigencia

       MI_CONDICION := 'COMPANIA         = '''||UN_COMPANIA||'''
                  AND  VIGENCIA_INICIAL =  '||UN_VIGENCIA||'
                  AND  ID_PLAN          = '''||MI_RS.ID_PLAN||'''
                  AND  CODIGO           = '''||MI_RS.CODIGO||''' ';

           BEGIN
             BEGIN
               PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PLAN_ADQUISICIONES_VIGENCIA',
                                                          UN_ACCION      =>'E', 
                                                          UN_CONDICION   =>MI_CONDICION
                                                          );

                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_PLANDES;
                  END;
                      EXCEPTION
                           WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                              MI_MSGERROR(1).CLAVE := 'PLAN';
                              MI_MSGERROR(1).VALOR := MI_RS.ID_PLAN;
                              MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                              MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ELIPADQUIVIGE
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR  
                                                    );     
            END;      

    --Registro principal.            

       MI_CONDICION := 'COMPANIA        = '''||UN_COMPANIA||'''
                  AND  VIGENCIA_INICIAL =  '||UN_VIGENCIA||'
                  AND  ID_PLAN          = '''||MI_RS.ID_PLAN||'''
                  AND  CODIGO           = '''||MI_RS.CODIGO||''' ';

           BEGIN
             BEGIN
               PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'BP_PLAN_ADQUISICIONES',
                                                          UN_ACCION      =>'E', 
                                                          UN_CONDICION   =>MI_CONDICION
                                                          );

                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_PLANDES;
                  END;
                      EXCEPTION
                           WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                              MI_MSGERROR(1).CLAVE := 'PLAN';
                              MI_MSGERROR(1).VALOR := MI_RS.ID_PLAN;
                              MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                              MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ELIPlANADQUI
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR  
                                                    );     
            END;              

   END LOOP ELIMINAR_PLANADQUI;

   --Registra los nuevos detalles por vigencia

   FOR MI_RS IN (SELECT ID_PLAN,                                  
                        CODIGO,
                        VIGENCIA,
                        VALOR_ESTIMADO_FIN,
                        VALOR_EJECUTADO_FIN
                 FROM   PI_PLAN_ADQUISICIONES_VIGENCIA                     
                 WHERE  COMPANIA         = UN_COMPANIA     
                   AND  TIPO             = UN_TIPOT
                   AND  NUMERO           = UN_NUMEROT
                   AND  VIGENCIA_INICIAL = UN_VIGENCIA
                   AND  ESNUEVO          NOT IN(0)   )LOOP

      MI_CAMPOS := ' COMPANIA,                           
                    VIGENCIA_INICIAL,                   
                    ID_PLAN,                            
                    CODIGO,                             
                    VIGENCIA,                           
                    VALOR_ESTIMADO,                     
                    VALOR_EJECUTADO,                    
                    CREATED_BY,                         
                    DATE_CREATED ';

     MI_VALORES := ''''||UN_COMPANIA||''',
                      '||UN_VIGENCIA||',
                    '''||MI_RS.ID_PLAN||''',
                      '||MI_RS.CODIGO||',
                      '||MI_RS.VIGENCIA||',
                      '||MI_RS.VALOR_ESTIMADO_FIN||',
                      '||MI_RS.VALOR_EJECUTADO_FIN||',
                    '''||UN_USUARIO||''',
                     SYSDATE';      

           BEGIN
             BEGIN
               PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       => 'BP_PLAN_ADQUISICIONES_VIGENCIA',
                                                          UN_ACCION      => 'I', 
                                                          UN_CAMPOS      => MI_CAMPOS,
                                                          UN_VALORES     => MI_VALORES
                                                          );

                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_PLANDES;
                  END;
                      EXCEPTION
                           WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                              MI_MSGERROR(1).CLAVE := 'PLAN';
                              MI_MSGERROR(1).VALOR := MI_RS.ID_PLAN;
                              MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                              MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSDETPORVIGEN
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR  
                                                    );     
            END;                       

  END LOOP;                   

  --Actualizacion de indicadores

      MI_MERGEUSING :='SELECT COMPANIA,
                        VIGENCIA_INICIAL,
                        ID_PLAN,
                        INDICADOR,
                        LB_FIN,
                        REFERENTE_FIN
                      FROM PI_INDICADOR_META_TRA
                      WHERE  PI_INDICADOR_META_TRA.COMPANIA         ='''||UN_COMPANIA||'''                 
                        AND  PI_INDICADOR_META_TRA.TIPO             ='''||UN_TIPOT||'''
                        AND  PI_INDICADOR_META_TRA.NUMERO           ='||UN_NUMEROT||'        
                        AND  PI_INDICADOR_META_TRA.VIGENCIA_INICIAL ='||UN_VIGENCIA||' ';

     MI_MERGEENLACE := 'TABLA.COMPANIA      = VISTA.COMPANIA          
                AND  TABLA.VIGENCIA_INICIAL = VISTA.VIGENCIA_INICIAL  
                AND  TABLA.ID_PLAN          = VISTA.ID_PLAN           
                AND  TABLA.CODIGO           = VISTA.INDICADOR';



     MI_MERGEEXISTE := 'UPDATE SET TABLA.LB            = VISTA.LB_FIN,                       
                                   TABLA.REFERENTE     = VISTA.REFERENTE_FIN,
                                   TABLA.MODIFIED_BY   = '''||UN_USUARIO||''',       
                                   TABLA.DATE_MODIFIED = SYSDATE';                
         BEGIN
           BEGIN
             PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       => 'PI_INDICADOR',
                                                        UN_ACCION      => 'MM', 
                                                        UN_MERGEUSING  => MI_MERGEUSING, 
                                                        UN_MERGEENLACE => MI_MERGEENLACE,
                                                        UN_MERGEEXISTE => MI_MERGEEXISTE
                                                        );

                EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PLANDES;
                END;
                    EXCEPTION
                         WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                            MI_MSGERROR(1).CLAVE := 'NUMERO';
                            MI_MSGERROR(1).VALOR := UN_NUMEROT;
                            MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                            MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTPIINDICADORTRA
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR  
                                                  );     
          END; 

    <<RECORRER_METATRA>>  
    FOR MI_RS1 IN (SELECT ID_PLAN ,
                        INDICADOR ,
                        META1_INI ,
                        META1_FIN ,
                        AVANCE1_FIN,
                        AVANCE1_INI,
                        META2_FIN,
                        META2_INI,
                        AVANCE2_FIN,
                        AVANCE2_INI,
                        META3_FIN,
                        META3_INI,
                        AVANCE3_FIN,
                        AVANCE3_INI,
                        META4_FIN,
                        META4_INI,
                        AVANCE4_FIN,
                        AVANCE4_INI
                   FROM PI_INDICADOR_META_TRA
                   WHERE COMPANIA           = UN_COMPANIA
                      AND  TIPO             = UN_TIPOT
                      AND  NUMERO           = UN_NUMEROT
                      AND  VIGENCIA_INICIAL = UN_VIGENCIA)LOOP

      <<RECORRER_INDMETA>>
      FOR MI_RS IN (SELECT COMPANIA,
                          VIGENCIA_INICIAL,
                          ID_PLAN,
                          INDICADOR,
                          VIGENCIA_META,
                          META,
                          AVANCE
                    FROM PI_INDICADOR_META
                    WHERE COMPANIA         = UN_COMPANIA
                      AND VIGENCIA_INICIAL = UN_VIGENCIA
                      AND ID_PLAN          = MI_RS1.ID_PLAN 
                      AND INDICADOR        = MI_RS1.INDICADOR)LOOP

           MI_CAMPOS := '';

           IF UN_VIGENCIA =   MI_RS.VIGENCIA_META THEN
              MI_CAMPOS := 'META = '||MI_RS.META||' + ('||TO_CHAR(MI_RS1.META1_FIN - MI_RS1.META1_INI)||'),
                          AVANCE = '||MI_RS.AVANCE||' + ('||TO_CHAR(MI_RS1.AVANCE1_FIN - MI_RS1.AVANCE1_INI)||')';

           ELSIF UN_VIGENCIA + 1 =   MI_RS.VIGENCIA_META THEN
              MI_CAMPOS := 'META = '||MI_RS.META||' + ('||TO_CHAR(MI_RS1.META2_FIN - MI_RS1.META2_INI)||'),
                            AVANCE = '||MI_RS.AVANCE||' + ('||TO_CHAR(MI_RS1.AVANCE2_FIN - MI_RS1.AVANCE2_INI)||')';

           ELSIF UN_VIGENCIA + 2 =   MI_RS.VIGENCIA_META THEN
              MI_CAMPOS := 'META = '||MI_RS.META||' + ('||TO_CHAR(MI_RS1.META3_FIN - MI_RS1.META3_INI)||'),
                            AVANCE = '||MI_RS.AVANCE||' + ('||TO_CHAR(MI_RS1.AVANCE3_FIN - MI_RS1.AVANCE3_INI)||')';

           ELSIF UN_VIGENCIA + 3 =   MI_RS.VIGENCIA_META THEN
              MI_CAMPOS := 'META = '||MI_RS.META||' + ('||TO_CHAR(MI_RS1.META4_FIN - MI_RS1.META4_INI)||'),
                            AVANCE = '||MI_RS.AVANCE||' + ('||TO_CHAR(MI_RS1.AVANCE4_FIN - MI_RS1.AVANCE4_INI)||')';

           END IF;

        MI_CAMPOS := MI_CAMPOS ||',MODIFIED_BY                      = '''||UN_USUARIO||''',       
                                  DATE_MODIFIED                    = SYSDATE ';

       /*Ticket 7700529  
        *gfigueredo
        *11/11/2021
        *Se añade filtro AND VIGENCIA_META = '||MI_RS.VIGENCIA_META||', debido a que en el ciclo
        *actualiza los valores meta y avance con el valor del ultimo registro. 
        */                        
        MI_CONDICION := 'COMPANIA         = '''||UN_COMPANIA||'''
                     AND VIGENCIA_INICIAL = '||UN_VIGENCIA||'
 					 AND VIGENCIA_META 	  = '||MI_RS.VIGENCIA_META||'
                     AND ID_PLAN          = '''||MI_RS1.ID_PLAN||''' 
                     AND INDICADOR        = '''||MI_RS1.INDICADOR||''' ';

         BEGIN
           BEGIN
             PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'PI_INDICADOR_META',
                                                        UN_ACCION      =>'M', 
                                                        UN_CAMPOS     =>MI_CAMPOS, 
                                                        UN_CONDICION  =>MI_CONDICION
                                                        );

                EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PLANDES;
                END;
                    EXCEPTION
                         WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                            MI_MSGERROR(1).CLAVE := 'PLAN';
                            MI_MSGERROR(1).VALOR := MI_RS1.ID_PLAN;
                            MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                            MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTPIMETAAVANCE
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR  
                                                  );     
          END;                                 

      END LOOP RECORRER_INDMETA;
    END LOOP RECORRER_METATRA;

 --Se modifican las consultas de anexos para actualizar los registros ya existentes,insertar los nuevos y eliminar los que corresponda

      MI_MERGEUSING := 'SELECT COMPANIA,
                          VIGENCIA_INICIAL,
                          VIGENCIA_META,
                          ID_PLAN,
                          INDICADOR,
                          CODIGO,
                          RUTA_FIN
                        FROM PI_INDICADOR_META_TRA_ANEXO
                        WHERE  COMPANIA           = '''||UN_COMPANIA||'''                     
                          AND  TIPO               = '''||UN_TIPOT||'''                      
                          AND  NUMERO             = '||UN_NUMEROT||'                        
                          AND  VIGENCIA_INICIAL   = '||UN_VIGENCIA||'                 
                          AND  ES_NUEVO           IN (0) ';

      MI_MERGEENLACE :='TABLA.COMPANIA         = VISTA.COMPANIA
                    AND TABLA.VIGENCIA_INICIAL = VISTA.VIGENCIA_INICIAL
                    AND TABLA.VIGENCIA_META    = VISTA.VIGENCIA_META
                    AND TABLA.ID_PLAN          = VISTA.ID_PLAN';                          

     MI_MERGEEXISTE := 'UPDATE SET RUTA          = VISTA.RUTA_FIN,                                  
                                   MODIFIED_BY   = '''||UN_USUARIO||''',   
                                   DATE_MODIFIED = SYSDATE';
         BEGIN
           BEGIN
             PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'PI_INDICADOR_META_ANEXO',
                                                        UN_ACCION      =>'MM', 
                                                        UN_MERGEUSING  =>MI_MERGEUSING, 
                                                        UN_MERGEENLACE =>MI_MERGEENLACE,
                                                        UN_MERGEEXISTE => MI_MERGEEXISTE
                                                        );

                EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                           RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                    EXCEPTION
                         WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                            MI_MSGERROR(1).CLAVE := 'NUMERO';
                            MI_MSGERROR(1).VALOR := UN_NUMEROT;
                            MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                            MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                          PCK_ERR_MSG.RAISE_WITH_MSG(
                                                  UN_EXC_COD =>SQLCODE
                                                  ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTRUTAPIMETAANEXO
                                                  ,UN_REEMPLAZOS  => MI_MSGERROR  
                                                  );     
          END;  

  FOR MI_RS IN (SELECT ID_PLAN,                                
                        INDICADOR,                              
                        CODIGO,                                 
                        RUTA_FIN,
                        VIGENCIA_META                 
                FROM PI_INDICADOR_META_TRA_ANEXO             
                WHERE COMPANIA         = UN_COMPANIA    
                  AND TIPO             = UN_TIPOT      
                  AND NUMERO           = UN_NUMEROT        
                  AND VIGENCIA_INICIAL = UN_VIGENCIA 
                  AND ES_NUEVO         NOT IN(0)                      
                  AND DEBE_ELIMINAR    IN (0)  )LOOP

    MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO (UN_TABLA    =>'PI_INDICADOR_META_ANEXO',
                                                        UN_CRITERIO => 'COMPANIA        = '''||UN_COMPANIA||''' 
                                                                    AND VIGENCIA_INICIAL='||UN_VIGENCIA||' 
                                                                    AND ID_PLAN         ='''||MI_RS.ID_PLAN||'''
                                                                    AND INDICADOR       ='''||MI_RS.INDICADOR||''' ',
                                                        UN_CAMPO    => 'CODIGO',
                                                        UN_INICIAL  => 1);           

      --Pasar los archivos anexos a la tabla final

      MI_CAMPOS := 'COMPANIA,
                    VIGENCIA_INICIAL,
                    VIGENCIA_META,
                    ID_PLAN,
                    INDICADOR,
                    CODIGO,
                    RUTA,
                    CREATED_BY,
                    DATE_CREATED';

      MI_VALORES := ''''||UN_COMPANIA||''',
                     '||UN_VIGENCIA||',
                     '||MI_RS.VIGENCIA_META||',
                     '''||MI_RS.ID_PLAN||''',
                     '''||MI_RS.INDICADOR||''',
                     '''||MI_RS.CODIGO||''',
                     '''||MI_RS.RUTA_FIN||''',
                     '''||UN_USUARIO||''',
                     SYSDATE';     

           BEGIN
             BEGIN
               PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       => 'PI_INDICADOR_META_ANEXO',
                                                          UN_ACCION      => 'I', 
                                                          UN_CAMPOS      => MI_CAMPOS,
                                                          UN_VALORES     => MI_VALORES
                                                          );

                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_INSERTAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_PLANDES;
                  END;
                      EXCEPTION
                           WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                              MI_MSGERROR(1).CLAVE := 'PLAN';
                              MI_MSGERROR(1).VALOR := MI_RS.ID_PLAN;
                              MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                              MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_INSPIMETANEXOREGIS
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR  
                                                    );     
            END;                      

  END LOOP;                  

  --Una vez que se insertan todos los nuevos se eliminan los que se quitaron en la transacción actual

  FOR MI_RS IN (SELECT ID_PLAN,                                
                    VIGENCIA_META,                          
                    INDICADOR,                              
                    CODIGO,                                 
                    RUTA_FIN                                
            FROM   PI_INDICADOR_META_TRA_ANEXO             
            WHERE  COMPANIA         = UN_COMPANIA     
              AND  TIPO             = UN_TIPOT      
              AND  NUMERO           = UN_NUMEROT       
              AND  VIGENCIA_INICIAL = UN_VIGENCIA
              AND  DEBE_ELIMINAR NOT IN(0) )LOOP

    MI_CONDICION := 'COMPANIA          = '''||UN_COMPANIA||''' 
                    ,VIGENCIA_INICIAL = '||UN_VIGENCIA||'
                    ,VIGENCIA_META    = '||MI_RS.VIGENCIA_META||'
                    ,ID_PLAN          = '''||MI_RS.ID_PLAN||'''
                    ,INDICADOR        = '''||MI_RS.INDICADOR||'''
                    ,CODIGO           = '||MI_RS.CODIGO||' ';


           BEGIN
             BEGIN
               PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'PI_INDICADOR_META_ANEXO',
                                                          UN_ACCION      =>'E', 
                                                          UN_CONDICION   =>MI_CONDICION
                                                          );

                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_PLANDES;
                  END;
                      EXCEPTION
                           WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                              MI_MSGERROR(1).CLAVE := 'PLAN';
                              MI_MSGERROR(1).VALOR := MI_RS.ID_PLAN;
                              MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                              MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ELIPIMETANEXO
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR  
                                                    );     
            END;     

    --Se elimina de las demas transacciones activas

    MI_CONDICION := 'COMPANIA         = '''||UN_COMPANIA||''' 
                    ,VIGENCIA_INICIAL = '||UN_VIGENCIA||'
                    ,VIGENCIA_META    = '||MI_RS.VIGENCIA_META||'
                    ,ID_PLAN          = '''||MI_RS.ID_PLAN||'''
                    ,INDICADOR        = '''||MI_RS.INDICADOR||'''
                    ,CODIGO           = '||MI_RS.CODIGO||'
                    ,TIPO||NUMERO     IN(SELECT TIPO|| NUMERO
                                        FROM PI_TRANSACCION
                                        WHERE COMPANIA = '''||UN_COMPANIA||'''
                                        AND ESTADO     = ''A'')';            
           BEGIN
             BEGIN
               PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'PI_INDICADOR_META_TRA_ANEXO',
                                                          UN_ACCION      =>'E', 
                                                          UN_CONDICION   =>MI_CONDICION
                                                          );

                  EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                             RAISE PCK_EXCEPCIONES.EXC_PLANDES;
                  END;
                      EXCEPTION
                           WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                              MI_MSGERROR(1).CLAVE := 'PLAN';
                              MI_MSGERROR(1).VALOR := MI_RS.ID_PLAN;
                              MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                              MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                            PCK_ERR_MSG.RAISE_WITH_MSG(
                                                    UN_EXC_COD =>SQLCODE
                                                    ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ELIPIMETATRANEXO
                                                    ,UN_REEMPLAZOS  => MI_MSGERROR  
                                                    );     
            END;                   


  END LOOP;

  --Una vez que se actualiza el plan indicativo real, se cierra la transacción   

  MI_CAMPOS := 'ESTADO         = ''C'',         
                FECHA_APROBADO = SYSDATE,
                MODIFIED_BY    = '''||UN_USUARIO||''',
                DATE_MODIFIED  = SYSDATE';                  

  MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||'''                           
              AND  ANO      = '||UN_VIGENCIA||'                    
              AND  TIPO     = '''||UN_TIPOT||'''                        
              AND  NUMERO   = '||UN_NUMEROT||' ';              

     BEGIN
       BEGIN
         PCK_DATOS.GL_RTA      := PCK_DATOS.FC_ACME(UN_TABLA       =>'PI_TRANSACCION',
                                                    UN_ACCION      =>'M', 
                                                    UN_CAMPOS      =>MI_CAMPOS,
                                                    UN_CONDICION   =>MI_CONDICION
                                                    );

            EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                       RAISE PCK_EXCEPCIONES.EXC_PLANDES;
            END;
                EXCEPTION
                     WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
                        MI_MSGERROR(1).CLAVE := 'NUMERO';
                        MI_MSGERROR(1).VALOR := UN_NUMEROT;
                        MI_MSGERROR(2).CLAVE := 'VIGENCIA';
                        MI_MSGERROR(2).VALOR := UN_VIGENCIA;

                      PCK_ERR_MSG.RAISE_WITH_MSG(
                                              UN_EXC_COD =>SQLCODE
                                              ,UN_ERROR_COD=>PCK_ERRORES.ER_PLANDES_ACTPITRANSCERRAR
                                              ,UN_REEMPLAZOS  => MI_MSGERROR  
                                              );     
      END;     


 DBMS_OUTPUT.PUT_LINE(MI_RETORNO);
  RETURN MI_RETORNO;

END FC_ACTUALIZARPLANINDICATIVO;


PROCEDURE PR_CALAVANCEINDICADORES(
    /*
    NAME              : PR_CALAVANCEINDICADORES --> EN ACCESS calcularAvanceIndicadores()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 09/07/2018
    TIME              : 12:00 PM
    SOURCE MODULE     : SysmanDES2018.01.01
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Función que Genera los predecesores de acuerdo a la configuración de los niveles
    PARAMETROS        : UN_COMPANIA    => COMPANIA DE INGRESO A LA COMPANIA
                        UN_VIGENCIA    => VIGENCIA GUBERNAMENTAL SELECCIONADA
                        UN_TIPOT       => TIPO INDICADOR
                        UN_NUMEROT     => NUMERO INDICADOR
                        UN_USUARIO     => USUARIO DE INGRESO AL MODULO

    @NAME: calcularAvanceIndicadores
    */
    UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_VIGENCIA IN PCK_SUBTIPOS.TI_ANIO,
    UN_TIPOT    IN PI_INDICADOR_META_TRA.TIPO%TYPE,
    UN_NUMEROT  IN PI_INDICADOR_META_TRA.NUMERO%TYPE,
    UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO )
AS
  MI_IDPLAN       VARCHAR2(60 CHAR);
  MI_PORCENTAJE1  NUMBER(20,2);
  MI_PORCENTAJE2  NUMBER(20,2); 
  MI_PORCENTAJE3  NUMBER(20,2);
  MI_PORCENTAJE4  NUMBER(20,2);
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_DIGITOS      NUMBER(5,0);
  MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

  MI_DIGITOS := PCK_PLAN_DESARROLLO.FC_CARGAR_NIVEL(UN_COMPANIA  => UN_COMPANIA,
                                                    UN_VIGENCIA  => UN_VIGENCIA,
                                                    UN_CODIGO    => '');

  MI_DIGITOS := PCK_PLAN_DESARROLLO.FC_GETM_PRO();

 BEGIN


 FOR MI_RS3 IN (SELECT ID_PLAN,
    CASE
      WHEN P1 < 0
      THEN 0
      ELSE P1
    END /
    CASE
      WHEN SUM(CNT1) = 0
      THEN 1
      ELSE SUM(CNT1)
    END PORCENTAJE1 ,
    CASE
      WHEN P2 < 0
      THEN 0
      ELSE P2
    END /
    CASE
      WHEN SUM(CNT2) = 0
      THEN 1
      ELSE SUM(CNT2)
    END PORCENTAJE2,
    CASE
      WHEN P3 < 0
      THEN 0
      ELSE P3
    END /
    CASE
      WHEN SUM(CNT3) = 0
      THEN 1
      ELSE SUM(CNT3)
    END PORCENTAJE3,
    CASE
      WHEN P4 < 0
      THEN 0
      ELSE P4
    END /
    CASE
      WHEN SUM(CNT4) = 0
      THEN 1
      ELSE SUM(CNT4)
    END PORCENTAJE4    
    INTO MI_IDPLAN,MI_PORCENTAJE1,MI_PORCENTAJE2,MI_PORCENTAJE3,MI_PORCENTAJE4
  FROM
    (SELECT ID_PLAN,
      CASE
        WHEN
          CASE
            WHEN METAS.TIPO_INDICADOR = 'MM'
            OR METAS.TIPO_INDICADOR   = 'MIA'
            OR METAS.TIPO_INDICADOR   = 'MRA'
            THEN METAS.META1_FIN
            ELSE
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MR'
                THEN METAS.LB_FIN    - METAS.META1_FIN
                ELSE METAS.META1_FIN - METAS.LB_FIN
              END
          END = 0
        THEN 0
        ELSE
          CASE
            WHEN
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MM'
                OR METAS.TIPO_INDICADOR   = 'MIA'
                OR METAS.TIPO_INDICADOR   = 'MRA'
                THEN METAS.AVANCE1_FIN
                ELSE
                  CASE
                    WHEN METAS.TIPO_INDICADOR = 'MR'
                    THEN METAS.LB_FIN      - METAS.AVANCE1_FIN
                    ELSE METAS.AVANCE1_FIN - METAS.LB_FIN
                  END
              END >
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MM'
                OR METAS.TIPO_INDICADOR   = 'MIA'
                OR METAS.TIPO_INDICADOR   = 'MRA'
                THEN METAS.META1_FIN
                ELSE
                  CASE
                    WHEN METAS.TIPO_INDICADOR = 'MR'
                    THEN METAS.LB_FIN    - METAS.META1_FIN
                    ELSE METAS.META1_FIN - METAS.LB_FIN
                  END
              END
            THEN 100
            ELSE
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MM'
                OR METAS.TIPO_INDICADOR   = 'MIA'
                OR METAS.TIPO_INDICADOR   = 'MRA'
                THEN METAS.AVANCE1_FIN
                ELSE
                  CASE
                    WHEN METAS.TIPO_INDICADOR = 'MR'
                    THEN METAS.LB_FIN      - METAS.AVANCE1_FIN
                    ELSE METAS.AVANCE1_FIN - METAS.LB_FIN
                  END
              END *100/
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MM'
                OR METAS.TIPO_INDICADOR   = 'MIA'
                OR METAS.TIPO_INDICADOR   = 'MRA'
                THEN METAS.META1_FIN
                ELSE
                  CASE
                    WHEN METAS.TIPO_INDICADOR = 'MR'
                    THEN METAS.LB_FIN    - METAS.META1_FIN
                    ELSE METAS.META1_FIN - METAS.LB_FIN
                  END
              END
          END
      END P1,
      --
      CASE
        WHEN
          CASE
            WHEN METAS.TIPO_INDICADOR = 'MM'
            OR METAS.TIPO_INDICADOR   = 'MIA'
            OR METAS.TIPO_INDICADOR   = 'MRA'
            THEN METAS.META2_FIN
            ELSE
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MR'
                THEN METAS.META1_FIN - METAS.META2_FIN
                ELSE METAS.META2_FIN - METAS.META1_FIN
              END
          END = 0
        THEN 0
        ELSE
          CASE
            WHEN
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MM'
                OR METAS.TIPO_INDICADOR   = 'MIA'
                OR METAS.TIPO_INDICADOR   = 'MRA'
                THEN METAS.AVANCE2_FIN
                ELSE
                  CASE
                    WHEN METAS.TIPO_INDICADOR = 'MR'
                    THEN METAS.AVANCE1_FIN - METAS.AVANCE2_FIN
                    ELSE METAS.AVANCE2_FIN - METAS.AVANCE1_FIN
                  END
              END >
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MM'
                OR METAS.TIPO_INDICADOR   = 'MIA'
                OR METAS.TIPO_INDICADOR   = 'MRA'
                THEN METAS.META2_FIN
                ELSE
                  CASE
                    WHEN METAS.TIPO_INDICADOR = 'MR'
                    THEN METAS.META1_FIN - METAS.META2_FIN
                    ELSE METAS.META2_FIN - METAS.META1_FIN
                  END
              END
            THEN 100
            ELSE
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MM'
                OR METAS.TIPO_INDICADOR   = 'MIA'
                OR METAS.TIPO_INDICADOR   = 'MRA'
                THEN METAS.AVANCE2_FIN
                ELSE
                  CASE
                    WHEN METAS.TIPO_INDICADOR = 'MR'
                    THEN METAS.AVANCE1_FIN - METAS.AVANCE2_FIN
                    ELSE METAS.AVANCE2_FIN - METAS.AVANCE1_FIN
                  END
              END *100 /
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MM'
                OR METAS.TIPO_INDICADOR   = 'MIA'
                OR METAS.TIPO_INDICADOR   = 'MRA'
                THEN METAS.META2_FIN
                ELSE
                  CASE
                    WHEN METAS.TIPO_INDICADOR = 'MR'
                    THEN METAS.META1_FIN - METAS.META2_FIN
                    ELSE METAS.META2_FIN - METAS.META1_FIN
                  END
              END
          END
      END P2,
      CASE
        WHEN
          CASE
            WHEN METAS.TIPO_INDICADOR = 'MM'
            OR METAS.TIPO_INDICADOR   = 'MIA'
            OR METAS.TIPO_INDICADOR   = 'MRA'
            THEN METAS.META3_FIN
            ELSE
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MR'
                THEN METAS.META2_FIN - METAS.META3_FIN
                ELSE METAS.META3_FIN - METAS.META2_FIN
              END
          END = 0
        THEN 0
        ELSE
          CASE
            WHEN
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MM'
                OR METAS.TIPO_INDICADOR   = 'MIA'
                OR METAS.TIPO_INDICADOR   = 'MRA'
                THEN METAS.AVANCE3_FIN
                ELSE
                  CASE
                    WHEN METAS.TIPO_INDICADOR = 'MR'
                    THEN METAS.AVANCE2_FIN - METAS.AVANCE3_FIN
                    ELSE METAS.AVANCE3_FIN - METAS.AVANCE2_FIN
                  END
              END >
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MM'
                OR METAS.TIPO_INDICADOR   = 'MIA'
                OR METAS.TIPO_INDICADOR   = 'MRA'
                THEN METAS.META3_FIN
                ELSE
                  CASE
                    WHEN METAS.TIPO_INDICADOR = 'MR'
                    THEN METAS.META2_FIN - METAS.META3_FIN
                    ELSE METAS.META3_FIN - METAS.META2_FIN
                  END
              END
            THEN 100
            ELSE
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MM'
                OR METAS.TIPO_INDICADOR   = 'MIA'
                OR METAS.TIPO_INDICADOR   = 'MRA'
                THEN METAS.AVANCE3_FIN
                ELSE
                  CASE
                    WHEN METAS.TIPO_INDICADOR = 'MR'
                    THEN METAS.AVANCE2_FIN - METAS.AVANCE3_FIN
                    ELSE METAS.AVANCE3_FIN - METAS.AVANCE2_FIN
                  END
              END * 100 /
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MM'
                OR METAS.TIPO_INDICADOR   = 'MIA'
                OR METAS.TIPO_INDICADOR   = 'MRA'
                THEN METAS.META3_FIN
                ELSE
                  CASE
                    WHEN METAS.TIPO_INDICADOR = 'MR'
                    THEN METAS.META2_FIN - METAS.META3_FIN
                    ELSE METAS.META3_FIN - METAS.META2_FIN
                  END
              END
          END
      END P3,
      CASE
        WHEN
          CASE
            WHEN METAS.TIPO_INDICADOR = 'MM'
            OR METAS.TIPO_INDICADOR   = 'MIA'
            OR METAS.TIPO_INDICADOR   = 'MRA'
            THEN METAS.META4_FIN
            ELSE
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MR'
                THEN METAS.META3_FIN - METAS.META4_FIN
                ELSE METAS.META4_FIN - METAS.META3_FIN
              END
          END = 0
        THEN 0
        ELSE
          CASE
            WHEN
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MM'
                OR METAS.TIPO_INDICADOR   = 'MIA'
                OR METAS.TIPO_INDICADOR   = 'MRA'
                THEN METAS.AVANCE4_FIN
                ELSE
                  CASE
                    WHEN METAS.TIPO_INDICADOR = 'MR'
                    THEN METAS.AVANCE3_FIN - METAS.AVANCE4_FIN
                    ELSE METAS.AVANCE4_FIN - METAS.AVANCE3_FIN
                  END
              END >
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MM'
                OR METAS.TIPO_INDICADOR   = 'MIA'
                OR METAS.TIPO_INDICADOR   = 'MRA'
                THEN METAS.META4_FIN
                ELSE
                  CASE
                    WHEN METAS.TIPO_INDICADOR = 'MR'
                    THEN METAS.META3_FIN - METAS.META4_FIN
                    ELSE METAS.META4_FIN - METAS.META3_FIN
                  END
              END
            THEN 100
            ELSE
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MM'
                OR METAS.TIPO_INDICADOR   = 'MIA'
                OR METAS.TIPO_INDICADOR   = 'MRA'
                THEN METAS.AVANCE4_FIN
                ELSE
                  CASE
                    WHEN METAS.TIPO_INDICADOR = 'MR'
                    THEN METAS.AVANCE3_FIN - METAS.AVANCE4_FIN
                    ELSE METAS.AVANCE4_FIN - METAS.AVANCE3_FIN
                  END
              END * 100 /
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MM'
                OR METAS.TIPO_INDICADOR   = 'MIA'
                OR METAS.TIPO_INDICADOR   = 'MRA'
                THEN METAS.META4_FIN
                ELSE
                  CASE
                    WHEN METAS.TIPO_INDICADOR = 'MR'
                    THEN METAS.META3_FIN - METAS.META4_FIN
                    ELSE METAS.META4_FIN - METAS.META3_FIN
                  END
              END
          END
      END P4,
      CASE
        WHEN
          CASE
            WHEN METAS.TIPO_INDICADOR = 'MM'
            OR METAS.TIPO_INDICADOR   = 'MIA'
            OR METAS.TIPO_INDICADOR   = 'MRA'
            THEN METAS.META1_FIN
            ELSE
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MR'
                THEN METAS.LB_FIN    - METAS.META1_FIN
                ELSE METAS.META1_FIN - METAS.LB_FIN
              END
          END > 0
        THEN 1
        ELSE 0
      END CNT1,
      CASE
        WHEN
          CASE
            WHEN METAS.TIPO_INDICADOR = 'MM'
            OR METAS.TIPO_INDICADOR   = 'MIA'
            OR METAS.TIPO_INDICADOR   = 'MRA'
            THEN METAS.META2_FIN
            ELSE
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MR'
                THEN METAS.META1_FIN - METAS.META2_FIN
                ELSE METAS.META2_FIN - METAS.META1_FIN
              END
          END > 0
        THEN 1
        ELSE 0
      END CNT2,
      CASE
        WHEN
          CASE
            WHEN METAS.TIPO_INDICADOR = 'MM'
            OR METAS.TIPO_INDICADOR   = 'MIA'
            OR METAS.TIPO_INDICADOR   = 'MRA'
            THEN METAS.META3_FIN
            ELSE
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MR'
                THEN METAS.META2_FIN - METAS.META3_FIN
                ELSE METAS.META3_FIN - METAS.META2_FIN
              END
          END > 0
        THEN 1
        ELSE 0
      END CNT3,
      CASE
        WHEN
          CASE
            WHEN METAS.TIPO_INDICADOR = 'MM'
            OR METAS.TIPO_INDICADOR   = 'MIA'
            OR METAS.TIPO_INDICADOR   = 'MRA'
            THEN METAS.META4_FIN
            ELSE
              CASE
                WHEN METAS.TIPO_INDICADOR = 'MR'
                THEN METAS.META3_FIN - METAS.META4_FIN
                ELSE METAS.META4_FIN - METAS.META3_FIN
              END
          END > 0
        THEN 1
        ELSE 0
      END CNT4
    FROM PI_INDICADOR_META_TRA METAS
    INNER JOIN BP_NIVEL_PLAN_IND NIVEL
    ON METAS.COMPANIA          = NIVEL.COMPANIA
    AND METAS.VIGENCIA_INICIAL = NIVEL.VIGENCIA
    AND LENGTH(METAS.ID_PLAN)  = NIVEL.DIGITOS
    WHERE METAS.COMPANIA       = UN_COMPANIA
    AND METAS.TIPO             = UN_TIPOT
    AND METAS.NUMERO           = UN_NUMEROT
    AND METAS.VIGENCIA_INICIAL = UN_VIGENCIA
    AND NIVEL.META_PRODUC NOT IN(0)
    )
  GROUP BY ID_PLAN,
    P1,
    P2,
    P3,
    P4) LOOP
    
    BEGIN

    MI_CAMPOS := 'POR_AVANCE_INDICADOR = 0';

    MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA||'''         
                AND  TIPO          = '''||UN_TIPOT||'''            
                AND  NUMERO        = '||UN_NUMEROT||'           
                AND  VIGENCIA_PLAN = '||UN_VIGENCIA||'';
                        BEGIN 
                              BEGIN
                                      PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(   UN_TABLA       => 'PI_PLAN_INDICATIVO_METAS',
                               UN_ACCION      => 'M',
                               UN_CAMPOS      => MI_CAMPOS,
                               UN_CONDICION   => MI_CONDICION
                                                                              );

                              EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_PLANDES;
                              END;

          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
            MI_MSGERROR(1).CLAVE := 'TIPO';
            MI_MSGERROR(1).VALOR := UN_TIPOT;
            MI_MSGERROR(2).CLAVE := 'NUMERO';
            MI_MSGERROR(2).VALOR := UN_NUMEROT;
            MI_MSGERROR(3).CLAVE := 'VIGENCIA';
            MI_MSGERROR(3).VALOR := UN_VIGENCIA;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                                 UN_EXC_COD     =>  SQLCODE
                                ,UN_ERROR_COD   =>  PCK_ERRORES.ER_ACTAVANCEINDICADOR
                                ,UN_REEMPLAZOS  =>  MI_MSGERROR );         

                        END;      

   --   se actualizan los subprogramas

    MI_CAMPOS := 'POR_AVANCE_INDICADOR = CASE WHEN VIGENCIA_PLAN = VIGENCIA_META THEN '||MI_RS3.PORCENTAJE1||' ELSE     
                  CASE WHEN VIGENCIA_PLAN + 1 = VIGENCIA_META THEN '||MI_RS3.PORCENTAJE2||' ELSE
                  CASE WHEN VIGENCIA_PLAN + 2 = VIGENCIA_META THEN '||MI_RS3.PORCENTAJE3||' ELSE '||MI_RS3.PORCENTAJE4||' END END END ';



    MI_CONDICION := 'COMPANIA      = '''||UN_COMPANIA||'''         
                AND  TIPO          = '''||UN_TIPOT||'''            
                AND  NUMERO        = '||UN_NUMEROT||'           
                AND  VIGENCIA_PLAN = '||UN_VIGENCIA||'
                AND ID_PLAN        = '''||MI_IDPLAN||''' ';
                        BEGIN 
                              BEGIN
                                      PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(   UN_TABLA       => 'PI_PLAN_INDICATIVO_METAS',
                               UN_ACCION      => 'M',
                               UN_CAMPOS      => MI_CAMPOS,
                               UN_CONDICION   => MI_CONDICION
                                                                              );

                              EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_PLANDES;
                              END;

          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
            MI_MSGERROR(1).CLAVE := 'TIPO';
            MI_MSGERROR(1).VALOR := UN_TIPOT;
            MI_MSGERROR(2).CLAVE := 'NUMERO';
            MI_MSGERROR(2).VALOR := UN_NUMEROT;
            MI_MSGERROR(3).CLAVE := 'VIGENCIA';
            MI_MSGERROR(3).VALOR := UN_VIGENCIA;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                                 UN_EXC_COD     =>  SQLCODE
                                ,UN_ERROR_COD   =>  PCK_ERRORES.ER_ACTAVANCEINDICADOR
                                ,UN_REEMPLAZOS  =>  MI_MSGERROR );         

                        END;
                
    END;
 END LOOP;
  --Se promedia hacia arriba en los niveles  

    FOR MI_RS IN (SELECT DIGITOS
                  FROM BP_NIVEL_PLAN_IND
                  WHERE COMPANIA = UN_COMPANIA
                  AND VIGENCIA   = UN_VIGENCIA
                  AND DIGITOS   <= MI_DIGITOS
                  ORDER BY BP_NIVEL_PLAN_IND.DIGITOS DESC
    )LOOP

      --Actualizar por id

      FOR MI_RS2 IN (SELECT 
                        SUBSTR(ID_PLAN,1,MI_RS.DIGITOS) ID,                        
                        AVG(
                        CASE
                          WHEN POR_AVANCE_INDICADOR <0
                          THEN 0
                          ELSE POR_AVANCE_INDICADOR
                        END) AVANCE
                      FROM PI_PLAN_INDICATIVO_METAS
                      WHERE COMPANIA      = UN_COMPANIA
                      AND TIPO            = UN_TIPOT
                      AND NUMERO          = UN_NUMEROT
                      AND VIGENCIA_PLAN   = UN_VIGENCIA
                      AND LENGTH(ID_PLAN) = MI_RS.DIGITOS
                      GROUP BY 
                        SUBSTR(ID_PLAN,1,MI_RS.DIGITOS))LOOP

        MI_CAMPOS := 'POR_AVANCE_INDICADOR = '||MI_RS2.AVANCE||'';

        MI_CONDICION := 'COMPANIA        ='''||UN_COMPANIA||'''
                     AND TIPO            ='''||UN_TIPOT||'''
                     AND NUMERO          ='||UN_NUMEROT||'
                     AND VIGENCIA_PLAN   ='||UN_VIGENCIA||'
                     AND ID_PLAN         ='''||MI_RS2.ID||''' ';


       BEGIN 
                              BEGIN
                                      PCK_DATOS.GL_RTA:= PCK_DATOS.FC_ACME(UN_TABLA     => 'PI_PLAN_INDICATIVO_METAS',
                                                   UN_ACCION    => 'M',
                                                   UN_CAMPOS    => MI_CAMPOS,
                                                   UN_CONDICION => MI_CONDICION);

                              EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                                    RAISE PCK_EXCEPCIONES.EXC_PLANDES;
                              END;

          EXCEPTION WHEN  PCK_EXCEPCIONES.EXC_PLANDES THEN
            MI_MSGERROR(1).CLAVE := 'TIPO';
            MI_MSGERROR(1).VALOR := UN_TIPOT;
            MI_MSGERROR(2).CLAVE := 'NUMERO';
            MI_MSGERROR(2).VALOR := UN_NUMEROT;
            MI_MSGERROR(3).CLAVE := 'VIGENCIA';
            MI_MSGERROR(3).VALOR := UN_VIGENCIA;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                                 UN_EXC_COD     =>  SQLCODE
                                ,UN_ERROR_COD   =>  PCK_ERRORES.ER_ACTAVANCEINDICADOR
                                ,UN_REEMPLAZOS  =>  MI_MSGERROR );         

                        END;  

      END LOOP;

    END LOOP;
    END;

END PR_CALAVANCEINDICADORES;


--3
PROCEDURE PR_MANTENIMIENTOPLAN
(
/*
    NAME              : PR_MANTENIMIENTOPLAN --> EN ACCESS mantenimientoPlanIndicativo()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
    DATE MIGRADOR     : 07/09/2018
    TIME              : 11:51 AM
    SOURCE MODULE     : SysmanDES2018.01.01
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    DESCRIPTION       : Función que Inserta datos a la tabla BP_PLAN_INDICATIVO_METAS
    PARAMETROS        : UN_COMPANIA    => COMPANIA DE INGRESO A LA COMPANIA
                        UN_VIGENCIA    => VIGENCIA GUBERNAMENTAL SELECCIONADA
                        UN_USUARIO     => USUARIO DE INGRESO AL MODULO

    @NAME: generarMantenimientoPlan
    */
      UN_COMPANIA IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_VIGENCIA IN PCK_SUBTIPOS.TI_ANIO,
      UN_USUARIO  IN PCK_SUBTIPOS.TI_USUARIO
)
      AS 
            MI_VIGENCIAFINAL  PCK_SUBTIPOS.TI_ANIO;
            MI_CAMPOS                 PCK_SUBTIPOS.TI_CAMPOS;
            MI_VALORES                PCK_SUBTIPOS.TI_VALORES;
            MI_TABLA                    PCK_SUBTIPOS.TI_TABLA;
            MI_ERROR          PCK_SUBTIPOS.TI_CLAVEVALOR;  

      BEGIN       

            MI_VIGENCIAFINAL := UN_VIGENCIA + 3;
            <<VIGENCIAS>> 

            FOR MI_I IN UN_VIGENCIA .. MI_VIGENCIAFINAL LOOP
      MI_TABLA := 'BP_PLAN_INDICATIVO_METAS';
                  MI_CAMPOS := '    COMPANIA,
                                          ID_PLAN,
                                          VIGENCIA_PLAN,
                                          VIGENCIA_META,
                                          DESCRIPCION,
                                          DEPENDENCIA,
                                          PRESUPUESTO,
                                          COMPROMETIDO,
                                          PAGADO,
                                          CREATED_BY,
                                          DATE_CREATED';
                  MI_VALORES := '   SELECT 
                                                COMPANIA,                             
                                                ID,                                   
                                                VIGENCIA_INICIAL,                      
                                                '||MI_I||' ,                                 
                                                DESCRIPCION,                           
                                                DEPENDENCIA,                            
                                                PRESUPUESTO,                            
                                                COMPROMETIDO,                          
                                                PAGADO,
                                                '''||UN_USUARIO||''' CREATED_BY,
                                                SYSDATE DATE_CREATED
                                          FROM   BP_PLAN_INDICATIVO                     
                                          WHERE  COMPANIA               ='''||UN_COMPANIA||'''              
                                                AND  VIGENCIA_INICIAL   = '||UN_VIGENCIA||'
                                                AND  BP_PLAN_INDICATIVO.ID NOT IN          
                                                (                                            
                                                      SELECT ID_PLAN                              
                                                      FROM   BP_PLAN_INDICATIVO_METAS             
                                                      WHERE  COMPANIA      = '''||UN_COMPANIA||'''     
                                                            AND  VIGENCIA_PLAN =  '||UN_VIGENCIA||' 
                                                            AND  VIGENCIA_META =  '||MI_I||')'; 
                  BEGIN 
                        BEGIN 
                              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA,
                                                                                          UN_ACCION   => 'IS',
                                                                                          UN_CAMPOS   => MI_CAMPOS,
                                                                                          UN_VALORES  => MI_VALORES);

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                              RAISE PCK_EXCEPCIONES.EXC_PLANDES;
                        END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANDES THEN 
                        MI_ERROR(1).CLAVE := 'TABLA';
                MI_ERROR(1).VALOR :=  MI_TABLA;
                        MI_ERROR(2).CLAVE := 'VIGENCIA';
                MI_ERROR(2).VALOR :=  UN_VIGENCIA;
                        MI_ERROR(3).CLAVE := 'VIGENCIAMETA';
                MI_ERROR(3).VALOR :=  MI_I;


      PCK_ERR_MSG.RAISE_WITH_MSG(
                                  UN_EXC_COD =>SQLCODE,
                                  UN_ERROR_COD=>PCK_ERRORES.ERR_INSMETAS,
                                  UN_REEMPLAZOS  => MI_ERROR  
                                );  
                  END;  

                  MI_TABLA := 'PI_INDICADOR_META';

                  MI_CAMPOS := '    COMPANIA,
                                          VIGENCIA_INICIAL,
                                          ID_PLAN,
                                          INDICADOR,
                                          VIGENCIA_META,
                                          META,
                                          AVANCE,
                                          CREATED_BY,
                                          DATE_CREATED';
                  MI_VALORES := '   SELECT 
                                                PI_INDICADOR.COMPANIA,  
                                                PI_INDICADOR.VIGENCIA_INICIAL,                          
                                                PI_INDICADOR.ID_PLAN,                                 
                                                PI_INDICADOR.CODIGO,                                
                                                '||MI_I||',                                
                                                0,                                        
                                                0,
                                                '''||UN_USUARIO||''' CREATED_BY,
                                                SYSDATE DATE_CREATED                                              
                                          FROM   PI_INDICADOR  
              INNER JOIN BP_PLAN_INDICATIVO
                        ON PI_INDICADOR.COMPANIA = BP_PLAN_INDICATIVO.COMPANIA
                        AND PI_INDICADOR.ID_PLAN = BP_PLAN_INDICATIVO.ID
                        AND PI_INDICADOR.VIGENCIA_INICIAL = BP_PLAN_INDICATIVO.VIGENCIA_INICIAL
                                          WHERE  PI_INDICADOR.COMPANIA              = '''||UN_COMPANIA||'''              
                                                AND  PI_INDICADOR.VIGENCIA_INICIAL  = '||UN_VIGENCIA||'
                                                AND  PI_INDICADOR.ID_PLAN NOT IN                            
                                                      (                                             
                                                            SELECT ID_PLAN                                
                                                            FROM   PI_INDICADOR_META 
                    WHERE  COMPANIA         = '''||UN_COMPANIA||'''     
                                                                  AND  VIGENCIA_INICIAL =  '||UN_VIGENCIA||' 
                                                                  AND  VIGENCIA_META    =  '||MI_I||') ';

                  BEGIN 
                        BEGIN 
                              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA       => MI_TABLA,
                                                                                          UN_ACCION   => 'IS',
                                                                                          UN_CAMPOS   => MI_CAMPOS,
                                                                                          UN_VALORES  => MI_VALORES);

                        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN 
                              RAISE PCK_EXCEPCIONES.EXC_PLANDES;
                        END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PLANDES THEN 
                        MI_ERROR(1).CLAVE := 'TABLA';
                MI_ERROR(1).VALOR :=  MI_TABLA;
                        MI_ERROR(2).CLAVE := 'VIGENCIA';
                MI_ERROR(2).VALOR :=  UN_VIGENCIA;
                        MI_ERROR(3).CLAVE := 'VIGENCIAMETA';
                MI_ERROR(3).VALOR :=  MI_I;


      PCK_ERR_MSG.RAISE_WITH_MSG(
                                  UN_EXC_COD =>SQLCODE,
                                  UN_ERROR_COD=>PCK_ERRORES.ERR_INSMETAS,
                                  UN_REEMPLAZOS  => MI_ERROR  
                                );  
                  END;

            END LOOP VIGENCIAS;
END PR_MANTENIMIENTOPLAN;           

END PCK_PLAN_DESARROLLO1;