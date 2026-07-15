create or replace PACKAGE BODY PCK_PRECONTRACTUAL1 AS

  --1
  FUNCTION FC_ACTUALIZARFORMULAS
    /*
      NAME              : FC_ACTUALIZARFORMULAS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : LEYDI MILENA CORTES FORERO
      DATE MIGRADOR     : 22/02/2016
      TIME              : 4:56 PM
      SOURCE MODULE     : SysmanTTP2015.11.01 --> Precontractual
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : PERMITE ACTUALIZAR LOS CAMPOS VALOR Y VLR_TEXTO DE LA TABLA VARIABLETRANSACCION EN EL MOMENTO QUE SE ACTUALIZAN LAS FORMULAS 
                          DE UNA DETERMINADA VARIABLE.
      MODIFICATIONS     : 
      PARAMETERS        : UN_COMPANIA       	=> Compañia de ingreso a la aplicación
                          UN_CAMPO          	=> Valor que se desea modificar dentro de una variable
                          UN_VALOR          	=> Valor por el que se desea reemplazar el valor ingresado en el parámetro UN_CAMPO
                          UN_TIPOCONTRATO   	=> Tipo de contrato en el que se desea reemplazar la variable 
                          UN_CONSECUTIVO    	=> Transacción en la que se desea reemplazar la variable 
                          UN_USUARIO 			    => Usuario que accede al sistema 

      @NAME  :  actualizarFormulas
      @METHOD:  GET  						  

    */
    (
      UN_COMPANIA       	IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_CAMPO          	IN VARIABLETRANSACCION.VALOR%TYPE,
      UN_VALOR          	IN VARIABLETRANSACCION.VALOR%TYPE,
      UN_TIPOCONTRATO   	IN VARIABLETRANSACCION.TIPOCONTRATO%TYPE,
      UN_CONSECUTIVO    	IN VARIABLETRANSACCION.TRANSACCION%TYPE,
      UN_USUARIO 			    IN PCK_SUBTIPOS.TI_USUARIO
    ) 
    RETURN VARCHAR2 
    AS
      MI_PCKDATOS         PCK_SUBTIPOS.TI_RTA_ACME;
      MI_NUEVOVALOR       VARIABLETRANSACCION.VALOR%TYPE;
      MI_TABLA            PCK_SUBTIPOS.TI_TABLA; 
      MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
      MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS; 
      MI_RTA              PCK_SUBTIPOS.TI_RTA_ACME;
      MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
    BEGIN
      <<ACTUALIZA_VALOR>>
      FOR RS_SQL IN (
        SELECT CODIGO, 
               VRPREDETERMINADO 
          FROM VARIABLE 
         WHERE COMPANIA = UN_COMPANIA 
           AND INSTR(VRPREDETERMINADO,'Â«' || UN_CAMPO || 'Â»') > 0
      )
      LOOP 
        MI_NUEVOVALOR := REPLACE(RS_SQL.VRPREDETERMINADO, 'Â«' || UN_CAMPO || 'Â»', UN_VALOR);
        MI_NUEVOVALOR := REPLACE(SUBSTR(MI_NUEVOVALOR,12), '"'' )"');
        MI_NUEVOVALOR := PCK_SYSMAN_UTL.FC_EVAL(UN_CADENA => MI_NUEVOVALOR);											

        MI_TABLA      := 'VARIABLETRANSACCION';
        MI_CAMPOS     := 'VALOR 			      = ''' || MI_NUEVOVALOR || ''', ' || 
                         'VLR_TEXTO 		    = ''' || MI_NUEVOVALOR || ''', ' || 
                         'DATE_MODIFIED     = SYSDATE, '||
                         'MODIFIED_BY       = ''' || UN_USUARIO || ''' ';    
        MI_CONDICION  := '  COMPANIA         = ''' || UN_COMPANIA || ''' ' || 
                         '  AND TIPOCONTRATO = ''' || UN_TIPOCONTRATO || ''' ' || 
                         '  AND TRANSACCION  = ''' || UN_CONSECUTIVO || ''' ' || 
                         '  AND VARIABLE     = ''' || RS_SQL.CODIGO || '''';

        BEGIN
          BEGIN
          MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                       UN_ACCION    =>  'M', 
                                       UN_CAMPOS    =>  MI_CAMPOS,
                                       UN_CONDICION =>  MI_CONDICION);                 
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
            RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN    
          MI_MSGERROR (1).CLAVE := 'VARIABLE';
          MI_MSGERROR (1).VALOR := RS_SQL.CODIGO;
          PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE,
                                     UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACTUAL_ACTVALOR,
                                     UN_TABLAERROR => MI_TABLA,
                                     UN_REEMPLAZOS => MI_MSGERROR);
        END;											

      END LOOP ACTUALIZA_VALOR;

      RETURN MI_NUEVOVALOR;
    END FC_ACTUALIZARFORMULAS;

  --2
  PROCEDURE PR_COPIARESTUDIOPREVIO
    /*
    NAME              : PR_IMPUTACIONPPTAL En Access --> comb_estudio_AfterUpdate()
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
    DATE MIGRADOR     : 28/08/2017
    TIME              : 14:30 AM
    MODIFIER          :
    DATE MODIFIED     :
    TIME              :
    SOURCE MODULE     : PRECONTRACTUAL
    DESCRIPTION       :COPIA LAS DATOS DE UN ESTUDIO PREVIO
    @NAME:  copiarDatosEstudioPrevio
    @METHOD:  POST
    */
    (
      UN_COMPANIA     IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_CODESTUDIO   IN ES_EST_PROY.COD_ESTUDIO%TYPE,
      UN_USUARIO      IN PCK_SUBTIPOS.TI_USUARIO,
      UN_CONSECUTIVO  IN ES_EST_PROY.COD_ESTUDIO%TYPE)
  AS
      MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
      MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN

   --Actualizando proyecto asociado al estudio previo

    MI_CAMPOS  := 'COMPANIA,       
            COD_ESTUDIO,       
            CANTIDAD_PLAN,       
            VALOR,       
            CANTIDAD_PRODUCTO_C,      
            FUENTE_RECURSOS,      
            META_PROD,      
            SOLICITUD_SCD_BP,      
            DESTINORECURSOS,      
            AUXILIAR,       
            COD_PROY,       
            T_COMPONENTE,      
            COMPONENTE,       
            ACTIVIDAD,       
            CODIGOBPPIM,       
            COSTOUNITARIO,      
            COSTOTOTAL,      
            PORCEJECUTADO,      
            PRIORIDAD,      
            CANTIDAD,       
            VALORPROGRAMADO,      
            VALOREJECUTADO,      
            DESCRIPCION,       
            VIGENCIA,       
            CONPROGRAMACION,      
            VALOR_SOLICITADO_ACTIV,      
            CANTIDAD_EJE,      
            CREATED_BY,      
            DATE_CREATED';

    MI_VALORES := 'SELECT ES_EST_PROY.COMPANIA,       
                          '''||UN_CONSECUTIVO||''',       
                          ES_EST_PROY.CANTIDAD_PLAN,       
                          VALOR,       
                          CANTIDAD_PRODUCTO_C,      
                          FUENTE_RECURSOS,      
                          META_PROD,      
                          SOLICITUD_SCD_BP,      
                          DESTINORECURSOS,      
                          AUXILIAR,       
                          ES_EST_PROY.COD_PROY,       
                          ES_EST_PROY.T_COMPONENTE,      
                          ES_EST_PROY.COMPONENTE,       
                          ES_EST_PROY.ACTIVIDAD,       
                          ES_EST_PROY.CODIGOBPPIM,       
                          ES_EST_PROY.COSTOUNITARIO,      
                          ES_EST_PROY.COSTOTOTAL,      
                          ES_EST_PROY.PORCEJECUTADO,      
                          ES_EST_PROY.PRIORIDAD,      
                          ES_EST_PROY.CANTIDAD,       
                          ES_EST_PROY.VALORPROGRAMADO,      
                          ES_EST_PROY.VALOREJECUTADO,      
                          ES_EST_PROY.DESCRIPCION,       
                          ES_EST_PROY.VIGENCIA,       
                          ES_EST_PROY.CONPROGRAMACION,      
                          ES_EST_PROY.VALOR_SOLICITADO_ACTIV,      
                          ES_EST_PROY.CANTIDAD_EJE ,
                          '''|| UN_USUARIO ||''',
                          SYSDATE                  
                     FROM ES_EST_PROY
                    WHERE ES_EST_PROY.COMPANIA    = '''||UN_COMPANIA||'''
                      AND ES_EST_PROY.COD_ESTUDIO = '''||UN_CODESTUDIO||''' ';

    BEGIN 
      BEGIN              

      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'ES_EST_PROY',
                                           UN_ACCION    => 'IS',
                                           UN_CAMPOS    => MI_CAMPOS, 
                                           UN_VALORES   => MI_VALORES); 

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                         
      END; 

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
              MI_MSGERROR(1).CLAVE := 'ESTUDIO';
              MI_MSGERROR(1).VALOR := UN_CODESTUDIO;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_PRECONTRACTUAL_ACTPROYASO
                                        ,UN_REEMPLAZOS => MI_MSGERROR);

    END;  

   --Actualizando items del estudio previo 
     MI_CAMPOS := 'COMPANIA,
                   COD_ESTUDIO,
                   COD_ITEM, 
                   TERCERO, 
                   DEPENDENCIA, 
                   FECHA,
                   DESCRIPCION,
                   RESPONSABLE,
                   SUCURSAL_RESPONSABLE,
                   PORCIVAGLOBAL,
                   PORCDESCGLOBAL,
                   SUBTOTAL,
                   VALORIVA,
                   VALORDESCUENTO, 
                   VALORTOTAL,
                   VACIA,
                   IMPRESA, 
                   VALORESTIMADO, 
                   SELECCIONADA, 
                   RUBROCOMPRAS,
                   OBSERVACIONES,
                   NSOLICITUDDISPON, 
                   CONCEPTO,
                   PLACA, 
                   KILOMETRAJE, 
                   PERIORICIDAD, 
                   NUMERO_ENTREGAS,
                   UNIDAD_TIEMPO,
                   CANTIDAD, 
                   PLAZO, 
                   A_GLOBAL,
                   I_GLOBAL, 
                   U_GLOBAL,
                   VALOR_A,
                   VALOR_I,
                   VALOR_U,
                   CREATED_BY,
                   DATE_CREATED';

   MI_VALORES := 'SELECT ES_ITEMS_E.COMPANIA,
                         '''||UN_CONSECUTIVO||''',
                         ES_ITEMS_E.COD_ITEM, 
                         ES_ITEMS_E.TERCERO, 
                         ES_ITEMS_E.DEPENDENCIA, 
                         ES_ITEMS_E.FECHA,
                         ES_ITEMS_E.DESCRIPCION,
                         ES_ITEMS_E.RESPONSABLE,
                         ES_ITEMS_E.SUCURSAL_RESPONSABLE,
                         ES_ITEMS_E.PORCIVAGLOBAL,
                         ES_ITEMS_E.PORCDESCGLOBAL,
                         ES_ITEMS_E.SUBTOTAL,
                         ES_ITEMS_E.VALORIVA,
                         ES_ITEMS_E.VALORDESCUENTO, 
                         ES_ITEMS_E.VALORTOTAL,
                         ES_ITEMS_E.VACIA,
                         ES_ITEMS_E.IMPRESA, 
                         ES_ITEMS_E.VALORESTIMADO, 
                         ES_ITEMS_E.SELECCIONADA, 
                         ES_ITEMS_E.RUBROCOMPRAS,
                         ES_ITEMS_E.OBSERVACIONES,
                         ES_ITEMS_E.NSOLICITUDDISPON, 
                         ES_ITEMS_E.CONCEPTO,
                         ES_ITEMS_E.PLACA, 
                         ES_ITEMS_E.KILOMETRAJE, 
                         ES_ITEMS_E.PERIORICIDAD, 
                         ES_ITEMS_E.NUMERO_ENTREGAS,
                         ES_ITEMS_E.UNIDAD_TIEMPO,
                         ES_ITEMS_E.CANTIDAD, 
                         ES_ITEMS_E.PLAZO, 
                         ES_ITEMS_E.A_GLOBAL,
                         ES_ITEMS_E.I_GLOBAL, 
                         ES_ITEMS_E.U_GLOBAL,
                         ES_ITEMS_E.VALOR_A,
                         ES_ITEMS_E.VALOR_I,
                         ES_ITEMS_E.VALOR_U,
                         '''|| UN_USUARIO ||''',
                          SYSDATE 
                   FROM  ES_ITEMS_E
                   WHERE ES_ITEMS_E.COMPANIA    = '''||UN_COMPANIA||'''   
                     AND ES_ITEMS_E.COD_ESTUDIO = '''||UN_CODESTUDIO||''' ';


     BEGIN 
      BEGIN              

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'ES_ITEMS_E',
                                                UN_ACCION    => 'IS',
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_VALORES   => MI_VALORES);                    

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
               RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                         
      END; 

      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
              MI_MSGERROR(1).CLAVE := 'ESTUDIO';
              MI_MSGERROR(1).VALOR := UN_CODESTUDIO;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_PRECONTRACTUAL_ACTITEMESTP
                                        ,UN_REEMPLAZOS => MI_MSGERROR);

     END;                       

   <<ACTUALIZAR_ITEMS>>
   FOR RS IN(SELECT ES_DITEM_E.COMPANIA,
                     ES_DITEM_E.COD_DITEM, 
                     ES_DITEM_E.COD_ITEM, 
                     ES_DITEM_E.COD_ESTUDIO, 
                     ES_DITEM_E.ELEMENTO, 
                     ES_DITEM_E.DEPENDENCIA,
                     ES_DITEM_E.ESPECIFICACION,
                     ES_DITEM_E.CANTIDAD,
                     ES_DITEM_E.CANTIDADAPROBADA, 
                     ES_DITEM_E.CANTIDADPORENTREGAR, 
                     ES_DITEM_E.VALORUNITARIO,
                     ES_DITEM_E.PORCIVA, 
                     ES_DITEM_E.PORCDESCUENTO, 
                     ES_DITEM_E.VLRTOTAL, 
                     ES_DITEM_E.VLRAPROBADO,
                     ES_DITEM_E.VACIA, 
                     ES_DITEM_E.IND_REG, 
                     ES_DITEM_E.SELECCIONADA, 
                     ES_DITEM_E.OBSERVACIONES, 
                     ES_DITEM_E.NSOLICITUDDISPON, 
                     ES_DITEM_E.CONCEPTO, 
                     ES_DITEM_E.PLACA, 
                     ES_DITEM_E.SUBTOTAL, 
                     ES_DITEM_E.CREATED_BY, 
                     ES_DITEM_E.MODIFIED_BY, 
                     ES_DITEM_E.DATE_CREATED,
                     ES_DITEM_E.DATE_MODIFIED, 
                     ES_DITEM_E.ADMINISTRACION, 
                     ES_DITEM_E.IMPREVISTOS, 
                     ES_DITEM_E.UTILIDADES,
                     ES_DITEM_E.CODIGOCUBS, 
                     INVENTARIO.TIENEMOVIMIENTO 
              FROM ES_DITEM_E 
              INNER JOIN INVENTARIO 
                 ON ES_DITEM_E.COMPANIA = INVENTARIO.COMPANIA
                AND ES_DITEM_E.ELEMENTO = INVENTARIO.CODIGOELEMENTO
              WHERE ES_DITEM_E.COMPANIA = UN_COMPANIA
                AND ES_DITEM_E.COD_ESTUDIO = UN_CODESTUDIO)

  LOOP                       

    MI_CAMPOS := 'COMPANIA,
                  COD_DITEM,
                  COD_ITEM,
                  COD_ESTUDIO,
                  ELEMENTO,
                  DEPENDENCIA,
                  ESPECIFICACION, 
                  CANTIDAD,
                  CANTIDADAPROBADA,
                  CANTIDADPORENTREGAR,
                  VALORUNITARIO,
                  PORCIVA, 
                  PORCDESCUENTO,
                  VLRTOTAL,
                  VLRAPROBADO,
                  VACIA,
                  IND_REG,
                  SELECCIONADA,
                  OBSERVACIONES,
                  NSOLICITUDDISPON,
                  CONCEPTO,
                  PLACA,
                  SUBTOTAL,
                  ADMINISTRACION,
                  IMPREVISTOS,
                  UTILIDADES,
                  CODIGOCUBS,
                  CREATED_BY,
                  DATE_CREATED';


      MI_VALORES := ' '''||RS.COMPANIA||''',
                      '''||RS.COD_DITEM||''',
                      '''||RS.COD_ITEM||''',
                      '''||UN_CONSECUTIVO||''',
                      '''||RS.ELEMENTO||''',
                      '''||RS.DEPENDENCIA||''',
                      '''||RS.ESPECIFICACION||''', 
                      '''||RS.CANTIDAD||''',
                      '''||RS.CANTIDADAPROBADA||''',
                      '''||RS.CANTIDADPORENTREGAR||''',
                      '''||RS.VALORUNITARIO||''',
                      '''||RS.PORCIVA||''', 
                      '''||RS.PORCDESCUENTO||''',
                      '''||RS.VLRTOTAL||''',
                      '''||RS.VLRAPROBADO||''',
                      '''||RS.VACIA||''',
                      '''||RS.IND_REG||''',
                      '''||RS.SELECCIONADA||''',
                      '''||RS.OBSERVACIONES||''',
                      '''||RS.NSOLICITUDDISPON||''',
                      '''||RS.CONCEPTO||''',
                      '''||RS.PLACA||''',
                      '''||RS.SUBTOTAL||''',
                      '''||RS.ADMINISTRACION||''',
                      '''||RS.IMPREVISTOS||''',
                      '''||RS.UTILIDADES||''',
                      '''||RS.CODIGOCUBS||''',
                      '''|| UN_USUARIO ||''',
                          SYSDATE ';   

      BEGIN
       BEGIN
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA    => 'ES_DITEM_E',
                                             UN_ACCION    => 'I',
                                             UN_CAMPOS    => MI_CAMPOS, 
                                             UN_VALORES   => MI_VALORES);   

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                         
       END; 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
              MI_MSGERROR(1).CLAVE := 'ESTUDIO';
              MI_MSGERROR(1).VALOR := UN_CODESTUDIO;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_PRECONTRACTUAL_ACTITEMESTP
                                        ,UN_REEMPLAZOS => MI_MSGERROR);

     END;  

     MI_CAMPOS := 'TIENEMOVIMIENTO   = '''||RS.TIENEMOVIMIENTO||''', 
                   MODIFIED_BY       =  ''' || UN_USUARIO ||''', 
                   DATE_MODIFIED     =  SYSDATE  ';

     MI_CONDICION := 'COMPANIA       = '''||UN_COMPANIA||'''
                  AND CODIGOELEMENTO = '''||RS.ELEMENTO||''' ';

     BEGIN 
      BEGIN 
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA      => 'INVENTARIO'
                                               ,UN_ACCION    => 'M'
                                               ,UN_CAMPOS    => MI_CAMPOS
                                               ,UN_CONDICION => MI_CONDICION);                 

           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;  
      END; 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
              MI_MSGERROR(1).CLAVE := 'ELEMENTO';
              MI_MSGERROR(1).VALOR := RS.ELEMENTO;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_PRECONTRACTUAL_ACTMOVELEM
                                        ,UN_REEMPLAZOS => MI_MSGERROR);

     END;  
    END LOOP ACTUALIZAR_ITEMS; 

   --Actualizando costos del personal del estudio previo

   MI_CAMPOS := 'COMPANIA,
                ID_COSTO,
                COD_ESTUDIO,
                DESCRIPCION,
                CANTIDAD,
                SUELDOBASICO,
                TIEMPO,
                DEDICACION,
                FM,
                VALORPARCIAL,
                SUBTOTALVALOR,
                SUBTOTALCANT,
                SUBTOTALSUELDO,
                CONTRATO,
                SUBTOTALTIEMPO,
                FORMACION,
                CREATED_BY,
                DATE_CREATED';


      MI_VALORES := 'SELECT ES_COSTO_PER.COMPANIA,
                            ES_COSTO_PER.ID_COSTO,
                             '''||UN_CONSECUTIVO||''',
                            ES_COSTO_PER.DESCRIPCION,
                            ES_COSTO_PER.CANTIDAD,
                            ES_COSTO_PER.SUELDOBASICO,
                            ES_COSTO_PER.TIEMPO,
                            ES_COSTO_PER.DEDICACION,
                            ES_COSTO_PER.FM,
                            ES_COSTO_PER.VALORPARCIAL,
                            ES_COSTO_PER.SUBTOTALVALOR,
                            ES_COSTO_PER.SUBTOTALCANT,
                            ES_COSTO_PER.SUBTOTALSUELDO,
                            ES_COSTO_PER.CONTRATO,
                            ES_COSTO_PER.SUBTOTALTIEMPO,
                            ES_COSTO_PER.FORMACION,
                            '''||UN_USUARIO||''',
                            SYSDATE
                       FROM ES_COSTO_PER
                       WHERE ES_COSTO_PER.COMPANIA    = '''||UN_COMPANIA||'''
                         AND ES_COSTO_PER.COD_ESTUDIO = '''||UN_CODESTUDIO||''''; 



     BEGIN
      BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'ES_COSTO_PER',
                                               UN_ACCION    => 'IS',
                                               UN_CAMPOS    => MI_CAMPOS, 
                                               UN_VALORES   => MI_VALORES);                        

            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                         
       END; 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
              MI_MSGERROR(1).CLAVE := 'ESTUDIO';
              MI_MSGERROR(1).VALOR := UN_CODESTUDIO;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_PRECONTRACTUAL_ACTCOSTPERS
                                        ,UN_REEMPLAZOS => MI_MSGERROR);
    END; 

  --Actualizando los rubros del estudio previo

    MI_CAMPOS := 'COMPANIA, 
                 ID_RUBRO, 
                 COD_ESTUDIO, 
                 COD_PROYECTO, 
                 NOMBRE,
                 DESTINO, 
                 VIGENCIA,
                 FUENTE_RECURSOS,
                 VLR_SOLICITADO,
                 CREATED_BY,
                 DATE_CREATED';

    MI_VALORES := 'SELECT ES_RUBROS_PROY.COMPANIA, 
                          ES_RUBROS_PROY.ID_RUBRO, 
                          '''||UN_CONSECUTIVO||''', 
                          ES_RUBROS_PROY.COD_PROYECTO, 
                          ES_RUBROS_PROY.NOMBRE,
                          ES_RUBROS_PROY.DESTINO, 
                          ES_RUBROS_PROY.VIGENCIA,
                          ES_RUBROS_PROY.FUENTE_RECURSOS,
                          ES_RUBROS_PROY.VLR_SOLICITADO,
                          '''||UN_USUARIO||''',
                          SYSDATE
                    FROM  ES_RUBROS_PROY
                   WHERE  ES_RUBROS_PROY.COMPANIA    ='''||UN_COMPANIA||'''
                     AND  ES_RUBROS_PROY.COD_ESTUDIO ='''||UN_CODESTUDIO||'''';  


     BEGIN
      BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'ES_RUBROS_PROY',
                                                UN_ACCION    => 'IS',
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_VALORES   => MI_VALORES);                        

         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                         
       END; 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
              MI_MSGERROR(1).CLAVE := 'ESTUDIO';
              MI_MSGERROR(1).VALOR := UN_CODESTUDIO;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_PRECONTRACTUAL_ACTRUBPROY
                                        ,UN_REEMPLAZOS => MI_MSGERROR);
    END;                    

   --Actualizando las disponibilidades del estudio previo
/* CC_1646_GROJAS: Se comenta el actualizar las disponibilidades dado que para cada estudio previo tiene una disponibilidad diferente.
   MI_CAMPOS := 'COMPANIA, 
                 COD_ESTUDIO,
                 ANO, 
                 TIPO_CPTE,
                 COMPROBANTE, 
                 CONSECUTIVO, 
                 CODIGO_CUENTA, 
                 DEBITO, 
                 CREDITO, 
                 AUXILIAR,
                 CENTRO_COSTO,
                 DESTINO,
                 FECHA_DIS,
                 CREATED_BY,
                 DATE_CREATED';               

   MI_VALORES := 'SELECT ES_RUBROS_EST_DIS.COMPANIA, 
                         '''||UN_CONSECUTIVO||''',
                         ES_RUBROS_EST_DIS.ANO, 
                         ES_RUBROS_EST_DIS.TIPO_CPTE,
                         ES_RUBROS_EST_DIS.COMPROBANTE, 
                         ES_RUBROS_EST_DIS.CONSECUTIVO, 
                         ES_RUBROS_EST_DIS.CODIGO_CUENTA, 
                         ES_RUBROS_EST_DIS.DEBITO, 
                         ES_RUBROS_EST_DIS.CREDITO, 
                         ES_RUBROS_EST_DIS.AUXILIAR,
                         ES_RUBROS_EST_DIS.CENTRO_COSTO,
                         ES_RUBROS_EST_DIS.DESTINO,
                         ES_RUBROS_EST_DIS.FECHA_DIS,
                         '''||UN_USUARIO||''',
                          SYSDATE
                  FROM ES_RUBROS_EST_DIS
                  WHERE ES_RUBROS_EST_DIS.COMPANIA  ='''||UN_COMPANIA||'''
                  AND ES_RUBROS_EST_DIS.COD_ESTUDIO ='''||UN_CODESTUDIO||'''';   

    BEGIN
      BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'ES_RUBROS_EST_DIS',
                                                UN_ACCION    => 'IS',
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_VALORES   => MI_VALORES);                        

             EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                         
       END; 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
              MI_MSGERROR(1).CLAVE := 'ESTUDIO';
              MI_MSGERROR(1).VALOR := UN_CODESTUDIO;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_PRECONTRACTUAL_ACTRUBDIS
                                        ,UN_REEMPLAZOS => MI_MSGERROR);
    END;               
*/
  --Actualizando el análisis de riesgos del estudio previo 

    MI_CAMPOS := 'COMPANIA,
                  COD_T_RIESGO,
                  COD_RIESGO,
                  COD_ESTUDIO,
                  DOC_RESPALDO,
                  FECHA_RECIBIDO,
                  FECHA_ENTREGA,
                  RESP_ENTREGA,
                  RESP_RECIBIDO,
                  SUCURSAL_RESP_RECIBIDO,
                  ANALIZADO,
                  VERIFICADO,
                  OBSERVACIONES,
                  DETALLE,
                  IMPACTO,
                  ESTIMACION,
                  TIPO_OPCION_FUENTE,
                  FUENTE,
                  ETAPA,
                  TIPO,
                  CONSECUENCIA_OCURRENCIA,
                  PROBABILIDAD,
                  VALORACION,
                  CATEGORIA,
                  TRATAMIENTO,
                  PROBABILIDAD_DESPUES,
                  IMPACTO_DESPUES,
                  VALORACION_DESPUES,
                  CATEGORIA_DESPUES,
                  AFECTA_EJECUCION_CONTRATO,
                  RESPONBABLE_IMPLEMENTACION,
                  REALIZACION_MONITOREO,
                  PERIODICIDAD,
                  TIPO_OPCION_ETAPA,
                  TIPO_OPCION_TIPO,
                  TIPO_OPCION_PROBABILIDAD,
                  TIPO_OPCION_PROBABILIDAD_DES,
                  TIPO_OPCION_IMPACTO,
                  TIPO_OPCION_IMPACTO_DES,
                  CREATED_BY,
                  DATE_CREATED';


      MI_VALORES:= 'SELECT ES_RIES_ESTPR.COMPANIA,
                          ES_RIES_ESTPR.COD_T_RIESGO,
                          ES_RIES_ESTPR.COD_RIESGO,
                          '''||UN_CONSECUTIVO||''',
                          ES_RIES_ESTPR.DOC_RESPALDO,
                          ES_RIES_ESTPR.FECHA_RECIBIDO,
                          ES_RIES_ESTPR.FECHA_ENTREGA,
                          ES_RIES_ESTPR.RESP_ENTREGA,
                          ES_RIES_ESTPR.RESP_RECIBIDO,
                          ES_RIES_ESTPR.SUCURSAL_RESP_RECIBIDO,
                          ES_RIES_ESTPR.ANALIZADO,
                          ES_RIES_ESTPR.VERIFICADO,
                          ES_RIES_ESTPR.OBSERVACIONES,
                          ES_RIES_ESTPR.DETALLE,
                          ES_RIES_ESTPR.IMPACTO,
                          ES_RIES_ESTPR.ESTIMACION,
                          ES_RIES_ESTPR.TIPO_OPCION_FUENTE,
                          ES_RIES_ESTPR.FUENTE,
                          ES_RIES_ESTPR.ETAPA,
                          ES_RIES_ESTPR.TIPO,
                          ES_RIES_ESTPR.CONSECUENCIA_OCURRENCIA,
                          ES_RIES_ESTPR.PROBABILIDAD,
                          ES_RIES_ESTPR.VALORACION,
                          ES_RIES_ESTPR.CATEGORIA,
                          ES_RIES_ESTPR.TRATAMIENTO,
                          ES_RIES_ESTPR.PROBABILIDAD_DESPUES,
                          ES_RIES_ESTPR.IMPACTO_DESPUES,
                          ES_RIES_ESTPR.VALORACION_DESPUES,
                          ES_RIES_ESTPR.CATEGORIA_DESPUES,
                          ES_RIES_ESTPR.AFECTA_EJECUCION_CONTRATO,
                          ES_RIES_ESTPR.RESPONBABLE_IMPLEMENTACION,
                          ES_RIES_ESTPR.REALIZACION_MONITOREO,
                          ES_RIES_ESTPR.PERIODICIDAD,
                          ES_RIES_ESTPR.TIPO_OPCION_ETAPA,
                          ES_RIES_ESTPR.TIPO_OPCION_TIPO,
                          ES_RIES_ESTPR.TIPO_OPCION_PROBABILIDAD,
                          ES_RIES_ESTPR.TIPO_OPCION_PROBABILIDAD_DES,
                          ES_RIES_ESTPR.TIPO_OPCION_IMPACTO,
                          ES_RIES_ESTPR.TIPO_OPCION_IMPACTO_DES,
                          '''||UN_USUARIO||''',
                          SYSDATE
                     FROM ES_RIES_ESTPR
                     WHERE ES_RIES_ESTPR.COMPANIA   ='''||UN_COMPANIA||'''
                      AND ES_RIES_ESTPR.COD_ESTUDIO = '''||UN_CODESTUDIO||''''; 


    BEGIN
      BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'ES_RIES_ESTPR',
                                                UN_ACCION    => 'IS',
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_VALORES   => MI_VALORES);                        

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                         
       END; 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
              MI_MSGERROR(1).CLAVE := 'ESTUDIO';
              MI_MSGERROR(1).VALOR := UN_CODESTUDIO;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_PRECONTRACTUAL_ACTANRIESGO
                                        ,UN_REEMPLAZOS => MI_MSGERROR);
    END;      

  --Actualizando cumplimiento del estudio previo

    MI_CAMPOS := 'COMPANIA,
                  COD_D_ETAPA,
                  COD_ETAPA,
                  COD_ESTUDIO,
                  COD_T_CONTRATO,
                  FECHA_RECIBIDO,
                  FECHA_ENTREGA,
                  RESP_ENTREGA,
                  SUCURSAL_ENTREGA,
                  RESP_RECIBIDO,
                  SUCURSAL_RECIBIDO,
                  REALIZADO,
                  VERIFICADO,
                  OBSERVACIONES,
                  DETALLE,
                  CREATED_BY,
                  DATE_CREATED';


    MI_VALORES := 'SELECT ES_DETA_ESTPR.COMPANIA,
                          ES_DETA_ESTPR.COD_D_ETAPA,
                          ES_DETA_ESTPR.COD_ETAPA,
                          '''||UN_CONSECUTIVO||''',
                          ES_DETA_ESTPR.COD_T_CONTRATO,
                          ES_DETA_ESTPR.FECHA_RECIBIDO,
                          ES_DETA_ESTPR.FECHA_ENTREGA,
                          ES_DETA_ESTPR.RESP_ENTREGA,
                          ES_DETA_ESTPR.SUCURSAL_ENTREGA,
                          ES_DETA_ESTPR.RESP_RECIBIDO,
                          ES_DETA_ESTPR.SUCURSAL_RECIBIDO,
                          ES_DETA_ESTPR.REALIZADO,
                          ES_DETA_ESTPR.VERIFICADO,
                          ES_DETA_ESTPR.OBSERVACIONES,
                          ES_DETA_ESTPR.DETALLE,
                          '''||UN_USUARIO||''',
                          SYSDATE
                     FROM ES_DETA_ESTPR
                    WHERE ES_DETA_ESTPR.COMPANIA    = '''||UN_COMPANIA||'''
                      AND ES_DETA_ESTPR.COD_ESTUDIO = '''||UN_CODESTUDIO||'''';   


    BEGIN
      BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'ES_DETA_ESTPR',
                                                UN_ACCION    => 'IS',
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_VALORES   => MI_VALORES);    

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                         
       END; 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
              MI_MSGERROR(1).CLAVE := 'ESTUDIO';
              MI_MSGERROR(1).VALOR := UN_CODESTUDIO;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_PRECONTRACTUAL_ACTDETAPROY
                                        ,UN_REEMPLAZOS => MI_MSGERROR);
    END;                      

  --Actualizando el soporte ténico del estudio previo

    MI_CAMPOS := 'COMPANIA, 
                  COD_ASPTEC,
                  COD_T_ASPTEC,
                  COD_ESTUDIO, 
                  NOMBRENOVEDAD,
                  ESTADO,
                  FECHA,
                  ENTIDAD,
                  RESPONSABLE,
                  SUCURSAL_RESPONSABLE,
                  CREATED_BY,
                  DATE_CREATED';


    MI_VALORES := 'SELECT ES_ASP_ESTPR.COMPANIA, 
                          ES_ASP_ESTPR.COD_ASPTEC,
                          ES_ASP_ESTPR.COD_T_ASPTEC,
                          '''||UN_CONSECUTIVO||''', 
                          ES_ASP_ESTPR.NOMBRENOVEDAD,
                          ES_ASP_ESTPR.ESTADO,
                          ES_ASP_ESTPR.FECHA,
                          ES_ASP_ESTPR.ENTIDAD,
                          ES_ASP_ESTPR.RESPONSABLE,
                          ES_ASP_ESTPR.SUCURSAL_RESPONSABLE,
                          '''||UN_USUARIO||''',
                          SYSDATE
                    FROM  ES_ASP_ESTPR
                    WHERE ES_ASP_ESTPR.COMPANIA    ='''||UN_COMPANIA||'''
                      AND ES_ASP_ESTPR.COD_ESTUDIO ='''||UN_CODESTUDIO||'''';                

      BEGIN
      BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'ES_ASP_ESTPR',
                                                UN_ACCION    => 'IS',
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_VALORES   => MI_VALORES);                        

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                         
       END; 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
              MI_MSGERROR(1).CLAVE := 'ESTUDIO';
              MI_MSGERROR(1).VALOR := UN_CODESTUDIO;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_PRECONTRACTUAL_ACTDETAPROY
                                        ,UN_REEMPLAZOS => MI_MSGERROR);
    END; 

  --Actualizando las polizas del estudio previo

    MI_CAMPOS:= 'COMPANIA,
                 COD_POLIZA,
                 COD_ESTUDIO,
                 TIPOPOLIZA,
                 AMPARO,
                 PORCENTAJE,
                 CREATED_BY,
                 DATE_CREATED';


    MI_VALORES:= 'SELECT ES_POLIZA_EST.COMPANIA,
                          ES_POLIZA_EST.COD_POLIZA,
                          '''||UN_CONSECUTIVO||''', 
                          ES_POLIZA_EST.TIPOPOLIZA,
                          ES_POLIZA_EST.AMPARO,
                          ES_POLIZA_EST.PORCENTAJE,
                          '''||UN_USUARIO||''',
                          SYSDATE
                   FROM  ES_POLIZA_EST
                   WHERE ES_POLIZA_EST.COMPANIA ='''||UN_COMPANIA||'''
                     AND ES_POLIZA_EST.COD_ESTUDIO='''||UN_CODESTUDIO||''''; 


     BEGIN
      BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'ES_POLIZA_EST',
                                                UN_ACCION    => 'IS',
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_VALORES   => MI_VALORES);                        

          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                         
      END; 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
              MI_MSGERROR(1).CLAVE := 'ESTUDIO';
              MI_MSGERROR(1).VALOR := UN_CODESTUDIO;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_PRECONTRACTUAL_ACTPOLIZA
                                        ,UN_REEMPLAZOS => MI_MSGERROR);
    END;                   

  --Actualizando localización del estudio previo

    MI_CAMPOS := 'COMPANIA,
                  COD_ESTUDIO,
                  PAIS,
                  DEPARTAMENTO,
                  CIUDAD,
                  BARRIO,
                  CODIGO,
                  UBICACION,
                  CREATED_BY,
                 DATE_CREATED';


    MI_VALORES:= 'SELECT ES_LOCALIZA.COMPANIA,
                          '''||UN_CONSECUTIVO||''', 
                         ES_LOCALIZA.PAIS,
                         ES_LOCALIZA.DEPARTAMENTO,
                         ES_LOCALIZA.CIUDAD,
                         ES_LOCALIZA.BARRIO,
                         ES_LOCALIZA.CODIGO,
                         ES_LOCALIZA.UBICACION,
                          '''||UN_USUARIO||''',
                          SYSDATE
                    FROM ES_LOCALIZA
                   WHERE ES_LOCALIZA.COMPANIA    = '''||UN_COMPANIA||'''
                     AND ES_LOCALIZA.COD_ESTUDIO = '''||UN_CODESTUDIO||'''';   


   BEGIN
      BEGIN
          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => 'ES_LOCALIZA',
                                                UN_ACCION    => 'IS',
                                                UN_CAMPOS    => MI_CAMPOS, 
                                                UN_VALORES   => MI_VALORES);                        

              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                  RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                         
      END; 

        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
              MI_MSGERROR(1).CLAVE := 'ESTUDIO';
              MI_MSGERROR(1).VALOR := UN_CODESTUDIO;
              PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD =>SQLCODE
                                        ,UN_ERROR_COD=>PCK_ERRORES.ERR_PRECONTRACTUAL_ACTLOCALIZA
                                        ,UN_REEMPLAZOS => MI_MSGERROR);
    END;                    



  END PR_COPIARESTUDIOPREVIO;

  --3
  PROCEDURE PR_PROPONENTE_ETAPAS
    /*
      NAME              : PR_PROPONENTE_ETAPAS
      AUTHORS           : STEFANINI SYSMAN  SAS
      AUTHOR MIGRACION  : JONATHAN ENRIQUE  GUERRERO TORRES
      DATE MIGRADOR     : 04/09/2016
      TIME              : 10:40 AM
      SOURCE MODULE     : SysmanTTP2015.11.01 --> Precontractual
      MODIFIER          : AURA LILIANA MONROY GARCIA
      DATE MODIFIED     : 15/09/2017
      TIME              : 05:54 PM
      DESCRIPTION       : PERMITE INSERTAR  LOS CAMPOS TIPOCONTRATO,TRANSACCION,CONSECUTIVODETALLE,PROPONENTE,SUCURSAL,CODIGO 
                          DE LA TABLA PRERREQUISITOS_PROPONENTE Y PROPONENTE_ITEMINVENTARIO DESPUES DE INSERTAR EN EL FORMULARIO PROPONENTES.
                          DE UNA DETERMINADA VARIABLE.
      MODIFICATIONS     : SE REALIZAN AJUSTES EN EL MANEJO DE EXCEPCIONES Y SE ADICIONA EL REEMPLAZO DE VALORES PARA MI_MSGERROR 
      PARAMETERS        : UN_COMPANIA       	=> Compañia de ingreso a la aplicación
                          UN_TIPOCONTRATO    	=> tipo de contrato que se ingresa al formulario principal de trancciones de Precontractual
                          UN_TRANSACCION      => Es el numero de la transaccion del formulario transacciones
                          UN_CONSECUTIVO     	=> Numero consecutivo que lleva la tabla de  proponentes. 
                          UN_PROPONENTE     	=> Es el Nit de la persona encargada de la transaccion.
                          UN_SUCURSAL        	=> Sucursal de la persona encargada de la transaccion. 
                          UN_COTIZAINVENTARIO => parametro de entrada del formulario. Insertar en la tabla PROPONENTE_ITEMINVENTARIO si es true

      @NAME  :  insertProponentesEtapas
      @METHOD:  POST  						  

    */
  ( UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOCONTRATO      IN PCK_SUBTIPOS.TI_STRSQL,
    UN_TRANSACCION       IN PCK_SUBTIPOS.TI_STRSQL,
    UN_CONSECUTIVO       IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
    UN_PROPONENTE        IN PCK_SUBTIPOS.TI_USUARIO,
    UN_SUCURSAL          IN PCK_SUBTIPOS.TI_SUCURSAL,
    UN_COTIZAINVENTARIO  IN PCK_SUBTIPOS.TI_LOGICO
    )
  AS 
      MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
      MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES          PCK_SUBTIPOS.TI_VALORES;
      MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
      MI_MSGERROR         PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN
    BEGIN 
      BEGIN 
          MI_TABLA   := 'PRERREQUISITOS_PROPONENTE';

          MI_CAMPOS  := 'COMPANIA,TIPOCONTRATO,TRANSACCION,CONSECUTIVODETALLE,PROPONENTE,SUCURSAL,CODIGO';

          MI_VALORES := 'SELECT COMPANIA,
                                TIPOCONTRATO,
                                TRANSACCION,
                                CONSECUTIVODETALLE,
                                '''||UN_PROPONENTE||''' PROPONENTE,
                                '''||UN_SUCURSAL||''' SUCURSAL,
                                PRERREQUISITO CODIGO
                           FROM PRE_REQUISITOS_ETAPA
                          WHERE COMPANIA           = '''||UN_COMPANIA||'''
                            AND TIPOCONTRATO       = '''||UN_TIPOCONTRATO||'''
                            AND TRANSACCION        = '''||UN_TRANSACCION||'''
                            AND CONSECUTIVODETALLE = '''||UN_CONSECUTIVO||'''';

          PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                UN_ACCION  => 'IS',
                                                UN_CAMPOS  => MI_CAMPOS,
                                                UN_VALORES => MI_VALORES);                    

           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              MI_MSGERROR(1).CLAVE := 'TIPO_CONTRATO';
              MI_MSGERROR(1).VALOR := UN_TIPOCONTRATO;
              MI_MSGERROR(2).CLAVE := 'TRANSACCION';
              MI_MSGERROR(2).VALOR := UN_TRANSACCION;
              MI_MSGERROR(3).CLAVE := 'CONSECUTIVO';
              MI_MSGERROR(3).VALOR := UN_CONSECUTIVO;
              RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACT_INSERT_PRERE
            );
        END;  

       BEGIN 
        BEGIN 
            IF UN_COTIZAINVENTARIO = -1 THEN 
              MI_TABLA   := 'PROPONENTE_ITEMINVENTARIO';

              MI_CAMPOS  := 'COMPANIA, TIPOCONTRATO, TRANSACCION, CONSECUTIVODETALLE,
                             PROPONENTE, SUCURSAL, IDELEMENTO, VLRTOTAL, VLRDESCUENTO, 
                             VLRIVA, PORCDESC, CANTIDAD, VALORUNITARIO, VALORUNITARIODI, PORCIVA';

              MI_VALORES :='SELECT COMPANIA,
                            TIPOCONTRATO,
                            CONSECUTIVO TRANSACCION,
                            '''||UN_CONSECUTIVO||''' CONSECUTIVODETALLE,
                            '''||UN_PROPONENTE||''' PROPONENTE,
                            '''||UN_SUCURSAL||''' SUCURSAL,
                            IDELEMENTO,
                            0 VLRTOTAL,
                            0 VLRDESCUENTO,
                            0 VLRIVA,
                            0 PORCDESC,
                            0 CANTIDAD,
                            0 VALORUNITARIO,
                            0 VALORUNITARIODI,
                            0 PORCIVA
                       FROM TRANSACCIONITEMINVENTARIO
                      WHERE COMPANIA   = '''||UN_COMPANIA||'''
                        AND TIPOCONTRATO = '''||UN_TIPOCONTRATO||'''
                        AND CONSECUTIVO  = '''||UN_TRANSACCION||'''';

           PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA   => MI_TABLA,
                                                UN_ACCION  => 'IS',
                                                UN_CAMPOS  => MI_CAMPOS,
                                                UN_VALORES => MI_VALORES);      

            END IF;

           EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              MI_MSGERROR(1).CLAVE := 'TIPO_CONTRATO';
              MI_MSGERROR(1).VALOR := UN_TIPOCONTRATO;
              MI_MSGERROR(2).CLAVE := 'TRANSACCION';
              MI_MSGERROR(2).VALOR := UN_TRANSACCION;
              MI_MSGERROR(3).CLAVE := 'CONSECUTIVO';
              MI_MSGERROR(3).VALOR := UN_CONSECUTIVO;
              RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;
          END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD    => SQLCODE,
              UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACT_INSERT_ITEMI
            );
        END;  


    END PR_PROPONENTE_ETAPAS;

  --4
  FUNCTION FC_CAMBIARESTUDIOPREVIO 
  /*
    NAME              : PR_CAMBIARESTUDIOPREVIO
    AUTHORS           : STEFANINI SYSMAN  SAS
    AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
    DATE MIGRADOR     : 06/09/2017
    TIME              : 16:17 
    SOURCE MODULE     : SysmanTTP2015.11.01 - Precontractual
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Registra en TRANSACCIONITEMINVENTARIO los items de inventario relacionados con el 
                        estudio previo que se está asociando a la transacción ingresada por parámetro
    PARAMETERS        : UN_COMPANIA          => Compañia de ingreso a la aplicación
                        UN_TIPOCONTRATO      => Tipo de contrato al que pertenece la transacción a modificar
                        UN_TRANSACCION       => Transacción a la que se desea asociar el estudio previo 
                        UN_ESTUDIOPREVIO     =>	Estudio previo que se desea asociar a la transacción		
                        UN_USUARIO 			     => Usuario que accede al sistema 

    @NAME:    cambiarEstudioPrevio
    @METHOD:  GET    
  */  
  (
    UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOCONTRATO      IN TIPOCONTRATO_PR.TIPOCONTRATO%TYPE,
    UN_TRANSACCION       IN TRANSACCION.CONSECUTIVO%TYPE,
    UN_ESTUDIOPREVIO     IN TRANSACCION.ESTUDIOPREVIO%TYPE,
    UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO
  )
  RETURN PCK_SUBTIPOS.TI_LOGICO
  AS 
    MI_ELEMENTOS          PCK_SUBTIPOS.TI_ENTERO;
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_RETORNO            PCK_SUBTIPOS.TI_LOGICO;
  BEGIN
    -- Elimina los registros asociados al proceso precontractual que se está trabajando en la tabla TRANSACCIONITEMINVENTARIO
    MI_TABLA     := 'TRANSACCIONITEMINVENTARIO';

    MI_CONDICION := '     COMPANIA     = ''' || UN_COMPANIA || ''' ' ||
                    ' AND TIPOCONTRATO = ''' || UN_TIPOCONTRATO ||  ''' ' ||
                    ' AND CONSECUTIVO  = '   || UN_TRANSACCION  ||' ';

    BEGIN
      BEGIN
        MI_RTA      := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                         UN_ACCION    => 'E',
                                         UN_CONDICION => MI_CONDICION);
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
            MI_MSGERROR(1).CLAVE := 'TRANSACCION';
            MI_MSGERROR(1).VALOR := UN_TRANSACCION;
            RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD    => SQLCODE,
          UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACT_ELITXINV, 
          UN_TABLAERROR => MI_TABLA,
          UN_REEMPLAZOS =>  MI_MSGERROR
        );
    END;     

   SELECT COUNT (ES_DITEM_E.ELEMENTO) ELEMENTO
     INTO MI_ELEMENTOS
     FROM ES_DITEM_E
      INNER JOIN INVENTARIO
         ON ES_DITEM_E.COMPANIA    = INVENTARIO.COMPANIA
        AND ES_DITEM_E.ELEMENTO    = INVENTARIO.CODIGOELEMENTO
    WHERE ES_DITEM_E.COMPANIA      = UN_COMPANIA
      AND ES_DITEM_E.COD_ESTUDIO   = UN_ESTUDIOPREVIO;

    IF MI_ELEMENTOS NOT IN (0) THEN

      MI_CAMPOS   := ' COMPANIA, '||
                     ' TIPOCONTRATO, '||
                     ' CONSECUTIVO, '||
                     ' IDELEMENTO, '||
                     ' CANTIDAD, '||
                     ' VALORUNITARIO, '||
                     ' VALORUNITARIODI, '||
                     ' PORCIVA, '||
                     ' PORCDESC, '||
                     ' VLRIVA, '||
                     ' VLRDESCUENTO, '||
                     ' VLRTOTAL, '||
                     ' ESPECIFICACIONES, '||
                     ' UNIDADMEDIDA, '||
                     ' DESCRIPCION, '||
                     ' CREATED_BY, '||
                     ' DATE_CREATED';

       MI_VALORES := ' SELECT ES_DITEM_E.COMPANIA, ' ||
                      '      ''' || UN_TIPOCONTRATO || '''                     TIPOCONTRATO, ' ||
                      '      '   ||  UN_TRANSACCION || '                       CONSECUTIVO, ' ||
                      '      ES_DITEM_E.ELEMENTO                               IDELEMENTO, ' ||
                      '      SUM(ES_DITEM_E.CANTIDAD)                          CANTIDAD, ' ||
                      '      SUM(ES_DITEM_E.VALORUNITARIO)                     VALORUNITARIO, ' ||
                      '      ''0''                                             VALORUNITARIODI, ' ||
                      '      SUM(ES_DITEM_E.PORCIVA)                           PORCIVA, ' ||
                      '      SUM(ES_DITEM_E.PORCDESCUENTO)                     PORCDESC, ' ||
                      '      SUM(ES_DITEM_E.VALORIVA)                          VLRIVA, ' ||
                      '      SUM(ES_DITEM_E.VALORDESCUENTO)                    VLRDESCUENTO, ' ||
                      '      SUM(ES_DITEM_E.VLRTOTAL)                          VLRTOTAL, ' ||
                      '      ES_DITEM_E.OBSERVACIONES                          ESPECIFICACIONES, ' ||
                      '      ''ND''                                            UNIDADMEDIDA, ' ||
                      '      TO_CHAR(SUBSTR(ES_DITEM_E.ESPECIFICACION,1,255))  DESCRIPCION , ' ||
                      '  ''' || UN_USUARIO || ''',   ' ||
                      '      SYSDATE ' ||
                      '    FROM ES_DITEM_E ' ||
                      '      INNER JOIN INVENTARIO ' ||
                      '         ON ES_DITEM_E.COMPANIA     = INVENTARIO.COMPANIA ' ||
                      '        AND ES_DITEM_E.ELEMENTO     = INVENTARIO.CODIGOELEMENTO ' ||
                      '   WHERE ES_DITEM_E.COD_ESTUDIO = ' || UN_ESTUDIOPREVIO || ' ' ||
                      '    GROUP BY ES_DITEM_E.COMPANIA, ' ||
                      '      ES_DITEM_E.ELEMENTO, ' ||  
                      '      ES_DITEM_E.OBSERVACIONES, ' ||  
                      '      TO_CHAR(SUBSTR(ES_DITEM_E.ESPECIFICACION,1,255)) ' ;

      BEGIN
        BEGIN
          MI_RTA    := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                         UN_ACCION    => 'IS',
                                         UN_CAMPOS    => MI_CAMPOS,
                                         UN_VALORES   => MI_VALORES);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              MI_MSGERROR(1).CLAVE := 'TRANSACCION';
              MI_MSGERROR(1).VALOR := UN_TRANSACCION;
              RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACT_INSTXINV, 
            UN_TABLAERROR => MI_TABLA,
            UN_REEMPLAZOS =>  MI_MSGERROR
          );
      END;                       

      MI_RETORNO := -1;    
    ELSE 
      MI_RETORNO := 0;    
    END IF ;  


    PCK_PRECONTRACTUAL1.PR_ACTUALIZARESTUDIOPREVIO
    (
      UN_COMPANIA      => UN_COMPANIA,
      UN_TIPOCONTRATO  => UN_TIPOCONTRATO,
      UN_TRANSACCION   => UN_TRANSACCION,
      UN_ESTUDIOPREVIO => UN_ESTUDIOPREVIO,
      UN_USUARIO       => UN_USUARIO
    );

    RETURN MI_RETORNO;
  END FC_CAMBIARESTUDIOPREVIO;

  --5
  PROCEDURE PR_ACTUALIZARESTUDIOPREVIO 
   /*
      NAME              : PR_ACTUALIZARESTUDIOPREVIO
      AUTHORS           : STEFANINI SYSMAN  SAS
      AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
      DATE MIGRADOR     : 06/09/2017
      TIME              : 16:45 
      SOURCE MODULE     : SysmanTTP2015.11.01 - Precontractual
      MODIFIER          : AURA LILIANA MONROY GARCÍA
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     : 
      DESCRIPTION       : Actualiza el número de proceso asociado al estudio previos, también actualiza el estudio
                          previo asociado a una transacción
                          estudio previo que se está asociando a la transacción que se imgresa por parámetro
      PARAMETERS        : UN_COMPANIA          => Compañia de ingreso a la aplicación
                          UN_TIPOCONTRATO      => Tipo de contrato al que pertenece la transacción a modificar
                          UN_TRANSACCION       => Transacción a la que se desea asociar el estudio previo 
                          UN_ESTUDIOPREVIO     =>	Estudio previo que se desea asociar a la transacción			
                          UN_USUARIO 			     => Usuario que accede al sistema 

      @NAME:    actualizarEstudioPrevio
      @METHOD:  PUT   
    */
  (
    UN_COMPANIA          IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOCONTRATO      IN TIPOCONTRATO_PR.TIPOCONTRATO%TYPE,
    UN_TRANSACCION       IN TRANSACCION.CONSECUTIVO%TYPE,
    UN_ESTUDIOPREVIO     IN TRANSACCION.ESTUDIOPREVIO%TYPE,
    UN_USUARIO           IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS
    MI_TABLA             PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS            PCK_SUBTIPOS.TI_CAMPOS;
    MI_CONDICION         PCK_SUBTIPOS.TI_CONDICION;
    MI_RTA               PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MSGERROR          PCK_SUBTIPOS.TI_CLAVEVALOR;
  BEGIN

    MI_TABLA 	   := 'ES_ESTPREVIO';

    MI_CAMPOS 	 := 'NUM_PROCESO      = ' || UN_TRANSACCION || ', '||
                    'DATE_MODIFIED    = SYSDATE, '||
                    'MODIFIED_BY      = ''' || UN_USUARIO || ''' ';  

    MI_CONDICION := '    COMPANIA    = ''' || UN_COMPANIA || ''' ' ||
                    'AND COD_ESTUDIO = ' || UN_ESTUDIOPREVIO || ' ';

    BEGIN
      BEGIN
        MI_RTA     := PCK_DATOS.FC_ACME (UN_TABLA     => MI_TABLA, 
                                         UN_ACCION    => 'M', 
                                         UN_CAMPOS    => MI_CAMPOS, 
                                         UN_CONDICION => MI_CONDICION); 		
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
           MI_MSGERROR(1).CLAVE := 'ESTUDIO';
           MI_MSGERROR(1).VALOR :=  UN_ESTUDIOPREVIO;
           RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD 		=>	SQLCODE,
          UN_ERROR_COD	=>	PCK_ERRORES.ERR_PRECONTRACT_ACTNUMPRO, 
          UN_TABLAERROR  =>	MI_TABLA,
          UN_REEMPLAZOS =>  MI_MSGERROR );
    END;


    MI_TABLA 	   := 'TRANSACCION';

    MI_CAMPOS 	 := 'ESTUDIOPREVIO     = ' || UN_ESTUDIOPREVIO || ', '||
                    'DATE_MODIFIED     = SYSDATE, '||
                    'MODIFIED_BY       = ''' || UN_USUARIO || ''' ';  

    MI_CONDICION := '    COMPANIA     = ''' || UN_COMPANIA || ''' '||
                    'AND TIPOCONTRATO = ''' || UN_TIPOCONTRATO || ''' '||
                    'AND CONSECUTIVO  =   ' || UN_TRANSACCION || ' ';

    BEGIN
      BEGIN
        MI_RTA   := PCK_DATOS.FC_ACME   (UN_TABLA     => MI_TABLA, 
                                         UN_ACCION    => 'M', 
                                         UN_CAMPOS    => MI_CAMPOS, 
                                         UN_CONDICION => MI_CONDICION); 		
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
           MI_MSGERROR(1).CLAVE := 'TRANSACCION';
           MI_MSGERROR(1).VALOR :=  UN_TRANSACCION;
           RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD 		=>	SQLCODE,
          UN_ERROR_COD	=>	PCK_ERRORES.ERR_PRECONTRACT_ACTESTPREV, 
          UN_TABLAERROR =>	MI_TABLA,
          UN_REEMPLAZOS =>  MI_MSGERROR);
    END;

  END PR_ACTUALIZARESTUDIOPREVIO;  

  --6
  PROCEDURE PR_ACTUALIZARINFOPROPONENTES
   /*
      NAME              : PR_ACTUALIZARINFOPROPONENTES
      AUTHORS           : STEFANINI SYSMAN SAS
      AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
      DATE MIGRADOR     : 08/09/2017
      TIME              : 12:29 PM  
      SOURCE MODULE     : SysmanTTP2015.11.01 - Precontractual
      MODIFIER          : AURA LILIANA MONROY GARCÍA
      DATE MODIFIED     : 
      TIME              : 
      MODIFICATIONS     : 
      DESCRIPTION       : Actualiza la información de los proponentes en las tablas PRE_REQUISITOS_ETAPA,
                          PROPONENTE, PRERREQUISITOS_PROPONENTE y PROPONENTE_ITEMINVENTARIO. Inserta o 
                          actualiza la información con el nuevo consecutivo.
      PARAMETERS        : UN_COMPANIA          => Compañia de ingreso a la aplicación
                          UN_TIPOCONTRATO      => Tipo de contrato al que pertenece la transacción a modificar
                          UN_TRANSACCION       => Transacción en la que se está trabajando 
                          UN_CONSECACTUAL      =>	Identificador de la etapa en la que se están realizando los ajustes
                          UN_CONSECSIGUIENTE   => identificador de la etapa en la cual se desea actualizar la información
                          UN_USUARIO 			     => Usuario que accede al sistema 

      @NAME:    actualizarInfoProponentes
      @METHOD:  POST   
    */
  (
    UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_TIPOCONTRATO          IN TIPOCONTRATO_PR.TIPOCONTRATO%TYPE,
    UN_TRANSACCION           IN TRANSACCION.CONSECUTIVO%TYPE,
    UN_CONSECACTUAL          IN PRE_REQUISITOS_ETAPA.CONSECUTIVODETALLE%TYPE,
    UN_CONSECSIGUIENTE       IN PRE_REQUISITOS_ETAPA.CONSECUTIVODETALLE%TYPE,
    UN_USUARIO               IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS 
    MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
    MI_MERGEUSING            PCK_SUBTIPOS.TI_MERGEUSING;
    MI_MERGEENLACE           PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE           PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGENOEXIS           PCK_SUBTIPOS.TI_MERGENOEXISTE;
    MI_RTA                   PCK_SUBTIPOS.TI_RTA_ACME;
    MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN

    -- Configuración de los prerrequisitos de la etapa
    MI_TABLA      := 'PRE_REQUISITOS_ETAPA';

    MI_MERGEUSING := ' SELECT COMPANIA, '||
                     '       TIPOCONTRATO, '||
                     '       TRANSACCION, '||
                     '   '|| UN_CONSECSIGUIENTE ||' CONSDETALLESIG, '||
                     '       PRERREQUISITO, '||
                     '       TIPO, '||
                     '       VALOR1, '||
                     '       VALOR2 '||
                     ' FROM PRE_REQUISITOS_ETAPA ' ||
                     'WHERE COMPANIA          = ''' || UN_COMPANIA || ''' '|| 
                     ' AND TIPOCONTRATO       = ''' || UN_TIPOCONTRATO  || ''' '|| 
                     ' AND TRANSACCION        =   ' || UN_TRANSACCION   || ' '||
                     ' AND CONSECUTIVODETALLE =   ' || UN_CONSECACTUAL  || ' ';

    MI_MERGEENLACE := '    TABLA.COMPANIA           = VISTA.COMPANIA       '||
                      'AND TABLA.TIPOCONTRATO       = VISTA.TIPOCONTRATO   '||
                      'AND TABLA.TRANSACCION        = VISTA.TRANSACCION    '||
                      'AND TABLA.CONSECUTIVODETALLE = VISTA.CONSDETALLESIG '||
                      'AND TABLA.PRERREQUISITO      = VISTA.PRERREQUISITO  ';                   

    MI_MERGEEXISTE := ' UPDATE SET ' ||
                      '  TABLA.TIPO           = VISTA.TIPO, '||
                      '  TABLA.VALOR1         = VISTA.VALOR1, '||
                      '  TABLA.VALOR2         = VISTA.VALOR2, '||
                      '  TABLA.MODIFIED_BY    = ''' || UN_USUARIO || ''', '||
                      '  TABLA.DATE_MODIFIED  = SYSDATE';                      

    MI_MERGENOEXIS := ' INSERT  ( '||
                      '    COMPANIA, '||
                      '    TIPOCONTRATO, '||
                      '    TRANSACCION, '||
                      '    CONSECUTIVODETALLE, '||
                      '    PRERREQUISITO, '||
                      '    TIPO, '||
                      '    VALOR1, '||
                      '    VALOR2, '||
                      '    CREATED_BY, '||
                      '    DATE_CREATED)  '||                     
                      ' VALUES ( '||
                      '    VISTA.COMPANIA, '||
                      '    VISTA.TIPOCONTRATO, '||
                      '    VISTA.TRANSACCION, '||
                      '    VISTA.CONSDETALLESIG, '||
                      '    VISTA.PRERREQUISITO, '||
                      '    VISTA.TIPO, '||
                      '    VISTA.VALOR1, '||
                      '    VISTA.VALOR2, '||
                      ' ''' || UN_USUARIO || ''', '||
                      '    SYSDATE)';                          

      BEGIN
        BEGIN
          MI_RTA    := PCK_DATOS.FC_ACME(UN_TABLA         => MI_TABLA,
                                         UN_ACCION        => 'IM',
                                         UN_MERGEUSING    => MI_MERGEUSING,
                                         UN_MERGEENLACE   => MI_MERGEENLACE,
                                         UN_MERGEEXISTE   => MI_MERGEEXISTE,
                                         UN_MERGENOEXIS   => MI_MERGENOEXIS);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
              MI_MSGERROR(1).CLAVE := 'TRANSACCION';
              MI_MSGERROR(1).VALOR := UN_TRANSACCION;
              RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACT_MERGEPREET,
            UN_TABLAERROR => MI_TABLA,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;     

      -- Configuración de información del PROPONENTE
      MI_TABLA       := 'PROPONENTE';

      MI_MERGEUSING  := 'SELECT COMPANIA, ' ||
                        '        TIPOCONTRATO, ' ||
                        '        TRANSACCION, ' ||
                        '    '|| UN_CONSECSIGUIENTE || ' CONSDETALLESIG, ' ||
                        '        PROPONENTE, ' ||
                        '        SUCURSAL, ' ||
                        '        TO_DATE(SYSDATE, ''DD/MM/YYYY'') FECHAINSCRIPCION, ' ||
                        '        DOCUMENTOSOPORTE, ' ||
                        '        ESTADO, ' ||
                        '        VALORP, ' ||
                        '        FOLIO '||
                        '   FROM PROPONENTE '||
                        '  WHERE COMPANIA           = ''' || UN_COMPANIA     || ''' '|| 
                        '    AND TIPOCONTRATO       = ''' || UN_TIPOCONTRATO || ''' '|| 
                        '    AND TRANSACCION        =   ' || UN_TRANSACCION  || ' '|| 
                        '    AND CONSECUTIVODETALLE =   ' || UN_CONSECACTUAL || ' ';

      MI_MERGEENLACE := '     TABLA.COMPANIA           = VISTA.COMPANIA  ' ||
                        ' AND TABLA.TIPOCONTRATO       = VISTA.TIPOCONTRATO ' ||
                        ' AND TABLA.TRANSACCION        = VISTA.TRANSACCION ' ||
                        ' AND TABLA.CONSECUTIVODETALLE = VISTA.CONSDETALLESIG ' ||
                        ' AND TABLA.PROPONENTE         = VISTA.PROPONENTE ' ||
                        ' AND TABLA.SUCURSAL           = VISTA.SUCURSAL ';

      MI_MERGEEXISTE := ' UPDATE SET '||
                        '  TABLA.FECHAINSCRIPCION   = VISTA.FECHAINSCRIPCION, '||
                        '  TABLA.DOCUMENTOSOPORTE   = VISTA.DOCUMENTOSOPORTE, '||
                        '  TABLA.ESTADO             = VISTA.ESTADO, '||
                        '  TABLA.VALORP             = VISTA.VALORP, '||
                        '  TABLA.FOLIO              = VISTA.FOLIO, '||
                        '  TABLA.MODIFIED_BY        = ''' || UN_USUARIO || ''', '||
                        '  TABLA.DATE_MODIFIED      = SYSDATE';                          

      MI_MERGENOEXIS := ' INSERT ( '||
                        '     COMPANIA, '||
                        '     TIPOCONTRATO, '||
                        '     TRANSACCION, '||
                        '     CONSECUTIVODETALLE, '||
                        '     PROPONENTE, '||
                        '     SUCURSAL, '||
                        '     FECHAINSCRIPCION, '||
                        '     DOCUMENTOSOPORTE, '||
                        '     ESTADO, '||
                        '     VALORP, '||
                        '     FOLIO, '||
                        '     CREATED_BY, '||
                        '     DATE_CREATED)  '||                           
                        ' VALUES ( '||
                        '     VISTA.COMPANIA, '||
                        '     VISTA.TIPOCONTRATO, '||
                        '     VISTA.TRANSACCION, '||
                        '     VISTA.CONSDETALLESIG, '||
                        '     VISTA.PROPONENTE, '||
                        '     VISTA.SUCURSAL, '||
                        '     VISTA.FECHAINSCRIPCION, '||
                        '     VISTA.DOCUMENTOSOPORTE, '||
                        '     VISTA.ESTADO, '||
                        '     VISTA.VALORP, '||
                        '     VISTA.FOLIO, ' ||
                        ' ''' || UN_USUARIO || ''', '||
                        '     SYSDATE)';                            

      BEGIN
        BEGIN
          MI_RTA    := PCK_DATOS.FC_ACME(UN_TABLA         => MI_TABLA,
                                         UN_ACCION        => 'IM',
                                         UN_MERGEUSING    => MI_MERGEUSING,
                                         UN_MERGEENLACE   => MI_MERGEENLACE,
                                         UN_MERGEEXISTE   => MI_MERGEEXISTE,
                                         UN_MERGENOEXIS   => MI_MERGENOEXIS);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
              MI_MSGERROR(1).CLAVE := 'TRANSACCION';
              MI_MSGERROR(1).VALOR := UN_TRANSACCION;
              RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACT_MERGEPROP, 
            UN_TABLAERROR => MI_TABLA,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;    

      --Configuración de los Prerrquisitos del Proponente
      MI_TABLA       := 'PRERREQUISITOS_PROPONENTE';

      MI_MERGEUSING  := ' SELECT COMPANIA,  '||
                        '         TIPOCONTRATO,  '||
                        '         TRANSACCION,  '||
                        '    ' || UN_CONSECSIGUIENTE || '  CONSDETALLESIG,  '||
                        '         PROPONENTE,  '||
                        '         SUCURSAL,  '||
                        '         CODIGO,  '||
                        '         VALOR,  '||
                        '         CUMPLE,  '||
                        '         OBSERVACION,  '||
                        '         SUBSANABLE  '||
                        '    FROM PRERREQUISITOS_PROPONENTE  '||
                        '   WHERE COMPANIA           = ''' || UN_COMPANIA     || ''' '|| 
                        '     AND TIPOCONTRATO       = ''' || UN_TIPOCONTRATO || ''' '|| 
                        '     AND TRANSACCION        =   ' || UN_TRANSACCION  || ' '|| 
                        '     AND CONSECUTIVODETALLE =   ' || UN_CONSECACTUAL || ' ';

      MI_MERGEENLACE := '     TABLA.COMPANIA           = VISTA.COMPANIA  '||
                        ' AND TABLA.TIPOCONTRATO       = VISTA.TIPOCONTRATO '||
                        ' AND TABLA.TRANSACCION        = VISTA.TRANSACCION '||
                        ' AND TABLA.CONSECUTIVODETALLE = VISTA.CONSDETALLESIG '||
                        ' AND TABLA.PROPONENTE         = VISTA.PROPONENTE '||
                        ' AND TABLA.SUCURSAL           = VISTA.SUCURSAL '||
                        ' AND TABLA.CODIGO             = VISTA.CODIGO ';

      MI_MERGEEXISTE := ' UPDATE SET '||
                        '    TABLA.VALOR          = VISTA.VALOR, '||
                        '    TABLA.CUMPLE         = VISTA.CUMPLE, '||
                        '    TABLA.OBSERVACION    = VISTA.OBSERVACION, '||
                        '    TABLA.SUBSANABLE     = VISTA.SUBSANABLE, ' ||
                        '    TABLA.MODIFIED_BY    = ''' || UN_USUARIO || ''', '||
                        '    TABLA.DATE_MODIFIED  = SYSDATE';                          

      MI_MERGENOEXIS := ' INSERT ( '||
                        '     COMPANIA, '||
                        '     TIPOCONTRATO, '||
                        '     TRANSACCION, '||
                        '     CONSECUTIVODETALLE, '||
                        '     PROPONENTE, '||
                        '     SUCURSAL, '||
                        '     CODIGO, '||
                        '     VALOR, '||
                        '     CUMPLE, '||
                        '     OBSERVACION, '||
                        '     SUBSANABLE, '||
                        '     CREATED_BY, '||
                        '     DATE_CREATED)  '||                           
                        '   VALUES ( '||
                        '     VISTA.COMPANIA, '||
                        '     VISTA.TIPOCONTRATO, '||
                        '     VISTA.TRANSACCION, '||
                        '     VISTA.CONSDETALLESIG, '||
                        '     VISTA.PROPONENTE, '||
                        '     VISTA.SUCURSAL, '||
                        '     VISTA.CODIGO, '||
                        '     VISTA.VALOR, '||
                        '     VISTA.CUMPLE, '||
                        '     VISTA.OBSERVACION, '||
                        '     VISTA.SUBSANABLE,  '||
                        ' ''' || UN_USUARIO || ''', '||
                        '     SYSDATE)';                            

      BEGIN
        BEGIN
          MI_RTA    := PCK_DATOS.FC_ACME(UN_TABLA         => MI_TABLA,
                                         UN_ACCION        => 'IM',
                                         UN_MERGEUSING    => MI_MERGEUSING,
                                         UN_MERGEENLACE   => MI_MERGEENLACE,
                                         UN_MERGEEXISTE   => MI_MERGEEXISTE,
                                         UN_MERGENOEXIS   => MI_MERGENOEXIS);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
              MI_MSGERROR(1).CLAVE := 'TRANSACCION';
              MI_MSGERROR(1).VALOR := UN_TRANSACCION;
              RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACT_MERGEPREPROP, 
            UN_TABLAERROR => MI_TABLA,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;                       

      -- Configuración de los ítems de inventario del Proponente
      MI_TABLA       := 'PROPONENTE_ITEMINVENTARIO';

      MI_MERGEUSING  := ' SELECT COMPANIA, ' ||
                        '         TIPOCONTRATO, ' ||
                        '         TRANSACCION, ' ||
                        '    ' || UN_CONSECSIGUIENTE ||' CONSDETALLESIG, ' ||
                        '         PROPONENTE, ' ||
                        '         SUCURSAL, ' ||
                        '         IDELEMENTO, ' ||
                        '         CANTIDAD, ' ||
                        '         VALORUNITARIO, ' ||
                        '         VALORUNITARIODI, ' ||
                        '         PORCDESC, ' ||
                        '         PORCIVA, ' ||
                        '         VLRDESCUENTO, ' ||
                        '         VLRIVA, ' ||
                        '         VLRTOTAL, ' ||
                        '         DESCRIPCION ' ||
                        '    FROM PROPONENTE_ITEMINVENTARIO ' ||
                        '  WHERE COMPANIA           = ''' || UN_COMPANIA || ''' '|| 
                        '    AND TIPOCONTRATO       = ''' || UN_TIPOCONTRATO || ''' '|| 
                        '    AND TRANSACCION        =   ' || UN_TRANSACCION || ' '|| 
                        '    AND CONSECUTIVODETALLE =   ' || UN_CONSECACTUAL || ' ';

      MI_MERGEENLACE := '     TABLA.COMPANIA           = VISTA.COMPANIA  '||
                        ' AND TABLA.TIPOCONTRATO       = VISTA.TIPOCONTRATO '||
                        ' AND TABLA.TRANSACCION        = VISTA.TRANSACCION '||
                        ' AND TABLA.CONSECUTIVODETALLE = VISTA.CONSDETALLESIG '||
                        ' AND TABLA.PROPONENTE         = VISTA.PROPONENTE '||
                        ' AND TABLA.SUCURSAL           = VISTA.SUCURSAL '||
                        ' AND TABLA.IDELEMENTO         = VISTA.IDELEMENTO ';

      MI_MERGEEXISTE := ' UPDATE SET '||
                        '   TABLA.CANTIDAD        = VISTA.CANTIDAD,  '||
                        '   TABLA.VALORUNITARIO   = VISTA.VALORUNITARIO, '||
                        '   TABLA.VALORUNITARIODI = VISTA.VALORUNITARIODI, '||
                        '   TABLA.PORCDESC        = VISTA.PORCDESC, '||
                        '   TABLA.PORCIVA         = VISTA.PORCIVA, '||
                        '   TABLA.VLRDESCUENTO    = VISTA.VLRDESCUENTO, '||
                        '   TABLA.VLRIVA          = VISTA.VLRIVA, '||
                        '   TABLA.VLRTOTAL        = VISTA.VLRTOTAL, '||
                        '   TABLA.DESCRIPCION     = VISTA.DESCRIPCION, '||
                        '   TABLA.MODIFIED_BY     = ''' || UN_USUARIO || ''', '||
                        '   TABLA.DATE_MODIFIED   = SYSDATE';                          

      MI_MERGENOEXIS := ' INSERT ('||
                        '     COMPANIA,'||
                        '     TIPOCONTRATO,'||
                        '     TRANSACCION,'||
                        '     CONSECUTIVODETALLE,'||
                        '     PROPONENTE,'||
                        '     SUCURSAL,'||
                        '     IDELEMENTO,'||
                        '     CANTIDAD,'||
                        '     VALORUNITARIO,'||
                        '     VALORUNITARIODI,'||
                        '     PORCDESC,'||
                        '     PORCIVA,'||
                        '     VLRDESCUENTO,'||
                        '     VLRIVA,'||
                        '     VLRTOTAL,'||
                        '     DESCRIPCION, '||
                        '     CREATED_BY, '||
                        '     DATE_CREATED)  '||                           
                        '   VALUES ('||
                        '     VISTA.COMPANIA,'||
                        '     VISTA.TIPOCONTRATO,'||
                        '     VISTA.TRANSACCION,'||
                        '     VISTA.CONSDETALLESIG,'||
                        '     VISTA.PROPONENTE,'||
                        '     VISTA.SUCURSAL,'||
                        '     VISTA.IDELEMENTO,'||
                        '     VISTA.CANTIDAD,'||
                        '     VISTA.VALORUNITARIO,'||
                        '     VISTA.VALORUNITARIODI,'||
                        '     VISTA.PORCDESC,'||
                        '     VISTA.PORCIVA,'||
                        '     VISTA.VLRDESCUENTO,'||
                        '     VISTA.VLRIVA,'||
                        '     VISTA.VLRTOTAL,'||
                        '     VISTA.DESCRIPCION, '||
                        ' ''' || UN_USUARIO || ''', '||
                        '     SYSDATE)';                        

      BEGIN
        BEGIN
          MI_RTA    := PCK_DATOS.FC_ACME(UN_TABLA         => MI_TABLA,
                                         UN_ACCION        => 'IM',
                                         UN_MERGEUSING    => MI_MERGEUSING,
                                         UN_MERGEENLACE   => MI_MERGEENLACE,
                                         UN_MERGEEXISTE   => MI_MERGEEXISTE,
                                         UN_MERGENOEXIS   => MI_MERGENOEXIS);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
              MI_MSGERROR(1).CLAVE := 'TRANSACCION';
              MI_MSGERROR(1).VALOR := UN_TRANSACCION;
              RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                
        END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
          PCK_ERR_MSG.RAISE_WITH_MSG(
            UN_EXC_COD    => SQLCODE,
            UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACT_MERGEITEM,
            UN_TABLAERROR => MI_TABLA,
            UN_REEMPLAZOS => MI_MSGERROR
          );
      END;                       

  END PR_ACTUALIZARINFOPROPONENTES;

  -- 7
  PROCEDURE PR_CREARDETALLESTX
  /*
    NAME              : PR_CREARDETALLESTX
    AUTHORS           : STEFANINI SYSMAN SAS
    AUTHOR MIGRACION  : AURA LILIANA MONROY GARCIA
    DATE MIGRADOR     : 27/10/2017
    TIME              : 11:42 AM 
    SOURCE MODULE     : SysmanTTP2015.11.01 - Precontractual
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Registra en D_TRANSACCION las etapas relacionadas con el proceso precontractual
                        ingresado por parámetro (UN_TRANSACCION) 
    PARAMETERS        : UN_COMPANIA          => Compañia de ingreso a la aplicación
                        UN_TIPOCONTRATO      => Tipo de contrato al que pertenece el proceso precontractual a trabajar
                        UN_TRANSACCION       => Proceso precontractual al que se le adicionarán las respectivas etapas (Detalles de Transaccion) 
                        UN_FECHAINICIO       => Es la fecha de inicio asignada al proceso precontractual 
                        UN_USUARIO           => Usuario que se encuentra ejecutando este proceso

    @NAME:    crearDetallesProceso
    @METHOD:  POST   
  */  
  (
      UN_COMPANIA              IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_TIPOCONTRATO          IN TIPOCONTRATO_PR.TIPOCONTRATO%TYPE,
      UN_TRANSACCION           IN TRANSACCION.CONSECUTIVO%TYPE,
      UN_FECHAINICIO           IN DATE,
      UN_USUARIO               IN PCK_SUBTIPOS.TI_USUARIO
  )
  AS 
      MI_SQLRS                 PCK_SUBTIPOS.TI_STRSQL;
      MI_RS                    SYS_REFCURSOR;
      MI_ETAPAS                PCK_SUBTIPOS.TI_ENTERO;
      MI_FECHAINI              DATE;
      MI_FECHAFIN              DATE;
      MI_DIAS                  ETAPA_PR.DIAS%TYPE;
      MI_TIPODIAS              PCK_SUBTIPOS.TI_TEXTO1;
      MI_IDETAPA               ETAPA_PR.IDETAPA%TYPE;
      MI_ORDEN                 PCK_SUBTIPOS.TI_ERROR_FUN;
      MI_CONSECUTIVODET        PCK_SUBTIPOS.TI_ENTERO := 1;
      MI_DESCRIPCION           ETAPA_PR.DESCRIPCION%TYPE;

      MI_TABLA                 PCK_SUBTIPOS.TI_TABLA;
      MI_CAMPOS                PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES               PCK_SUBTIPOS.TI_VALORES;
      MI_RTA                   PCK_SUBTIPOS.TI_CONSULTA;
      MI_MSGERROR              PCK_SUBTIPOS.TI_CLAVEVALOR;

  BEGIN
    -- COMPRUEBA LA EXISTENCIA DE ETAPAS PARA EL TIPO DE PRECONTRATO 
    BEGIN
      BEGIN 
       SELECT COUNT(IDETAPA) ETAPAS
         INTO MI_ETAPAS
         FROM ETAPA_PR
        WHERE COMPANIA     = UN_COMPANIA 
          AND TIPOCONTRATO = UN_TIPOCONTRATO 
          AND HEREDABLE    NOT IN(0)
        ORDER BY ORDEN;   

        IF MI_ETAPAS IN(0)  
          THEN RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;
        END IF;        
      END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN
      PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACT_NOETAPAS); 
    END;

    MI_FECHAINI := UN_FECHAINICIO;

    MI_SQLRS := 'SELECT DIAS '||
                ' ,TIPODIAS '||
                ' ,IDETAPA '||
                ' ,ORDEN '||
                ' ,DESCRIPCION '||
                'FROM ETAPA_PR '||
                'WHERE COMPANIA     = ''' || UN_COMPANIA || ''' '||
                '  AND TIPOCONTRATO = ''' || UN_TIPOCONTRATO  || ''' '||
                '  AND HEREDABLE    NOT IN(0) '||
                'ORDER BY ORDEN ';              

    OPEN MI_RS FOR MI_SQLRS;
        LOOP
          FETCH MI_RS INTO MI_DIAS,MI_TIPODIAS,MI_IDETAPA,MI_ORDEN,MI_DESCRIPCION;
          EXIT WHEN MI_RS%NOTFOUND; 

          /* En la función FC_FECHACIERRE el número 1 es asigando para días hábiles mientras que 
             en la tabla ETAPA_PR los días hábiles se identifican con el número 2 */
            MI_TIPODIAS := CASE WHEN MI_TIPODIAS = 2 THEN 1 ELSE 2 END;

            MI_FECHAFIN := PCK_PRECONTRACTUAL.FC_FECHACIERRE(UN_COMPANIA      => UN_COMPANIA,
                                                             UN_FECHAINICIAL  => MI_FECHAINI,
                                                             UN_NUMDIAS       => MI_DIAS,
                                                             UN_TIPO          => MI_TIPODIAS);
            MI_TABLA   := 'D_TRANSACCION';

            MI_CAMPOS  := ' COMPANIA, '||
                          ' TIPOCONTRATO, '||
                          ' TRANSACCION, '||
                          ' CONSECUTIVODETALLE, '||
                          ' IDETAPA, '||
                          ' FECHAINICIAL, '||
                          ' FECHAFINAL, '||
                          ' ESTADO, '||
                          ' ORDEN, '||
                          ' DATE_CREATED, '||
                          ' CREATED_BY ';

            MI_VALORES := '  ''' || UN_COMPANIA || '''  '||
                          ', ''' || UN_TIPOCONTRATO || '''  '||
                          ',   ' || UN_TRANSACCION || ' '||
                          ',   ' || MI_CONSECUTIVODET || ' ' ||
                          ',   ' || MI_IDETAPA || ' ' ||
                          ',   TO_DATE(''' || MI_FECHAINI || ''', ''DD/MM/YYYY HH24:MI:SS'') ' ||
                          ',   TO_DATE(''' || MI_FECHAFIN || ''', ''DD/MM/YYYY HH24:MI:SS'') ' ||
                          ',  ''P'' '||
                          ',   ' || MI_ORDEN || ' ' ||
                          ',   SYSDATE ' ||
                          ', ''' || UN_USUARIO ||''' ';

            BEGIN
              BEGIN          
                MI_RTA     :=  PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA,
                                                 UN_ACCION     => 'I', 
                                                 UN_CAMPOS     => MI_CAMPOS,
                                                 UN_VALORES    => MI_VALORES);     
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    MI_MSGERROR(1).CLAVE := 'TRANSACCION';
                    MI_MSGERROR(1).VALOR := UN_TRANSACCION;
                    MI_MSGERROR(2).CLAVE := 'DESCRIPCION';
                    MI_MSGERROR(2).VALOR := MI_DESCRIPCION; 
                    RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;                                
              END;
              EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN 
                PCK_ERR_MSG.RAISE_WITH_MSG(
                  UN_EXC_COD    => SQLCODE,
                  UN_ERROR_COD  => PCK_ERRORES.ERR_PRECONTRACT_INSDETALLE, 
                  UN_TABLAERROR => MI_TABLA,
                  UN_REEMPLAZOS => MI_MSGERROR
                );
            END;           

            MI_CONSECUTIVODET := MI_CONSECUTIVODET + 1;                                                      
            MI_FECHAINI       := MI_FECHAFIN;

        END LOOP;
    CLOSE MI_RS;   

  END PR_CREARDETALLESTX;

PROCEDURE PR_CARGAR_CODIGO_UNSPSC
/*
    NAME              : PR_CARGAR_CODIGO_UNSPSC
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL VENEGAS RODRIGUEZ
    DATE MIGRADOR     : 18/07/2018
    TIME              : 09:00 AM  
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Permite subir los codigo UNSPSC desde un archivo plano

      @NAME:    subirCodigosUnspsc 
      @METHOD:  POST
  */
(
 UN_COMPANIA      IN PCK_SUBTIPOS.TI_COMPANIA,
 UN_CAMBIOS       IN CLOB,
 UN_USUARIO       IN PCK_SUBTIPOS.TI_USUARIO
)
AS
MI_DATOS_FILA          PCK_SYSMAN_UTL.T_SPLIT;
MI_DATOS_COLUMNAS      PCK_SYSMAN_UTL.T_SPLIT;
MI_COLUM_ACTUAL_CODIGO NUMBER (10);
MI_COLUM_ACTUAL_NOMBRE NUMBER (10);
MI_CANTIDAD            NUMBER (10);
MI_CONTADOR            NUMBER (10) := 0;
MI_TABLA               PCK_SUBTIPOS.TI_TABLA;
MI_CAMPOS              PCK_SUBTIPOS.TI_CAMPOS;
MI_VALORES             PCK_SUBTIPOS.TI_VALORES;
MI_MSGERROR            PCK_SUBTIPOS.TI_CLAVEVALOR;
MI_NOMBRE_NIVEL        VARCHAR2(15 CHAR);
BEGIN
     ----------------------------------------------------------------------------------------------
     --La estructura del archivo de los datos a cargar es:
     --Los datos empiezan en la fila 2
     --Columna 1: codigo Segmento
     --Columna 2: Nombre Segmento
     --Columna 3: Codigo Familia
     --Columna 4: Nombre Familia
     --Columna 5: Codigo Clase
     --Columna 6: Nombre Clase
     --Columna 7: Codigo Producto
     --Columna 8: Nombre Producto
     ----------------------------------------------------------------------------------------------
  MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CAMBIOS,
                                               UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);

  <<CARGA_CODIGOS>>
  FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
  LOOP
    MI_CONTADOR := MI_CONTADOR + 1;
    MI_COLUM_ACTUAL_CODIGO := 1;
    MI_COLUM_ACTUAL_NOMBRE := 2;
    MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                     UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
      <<NIVELES_DINAMICOS>>
      FOR RS_NIVELES IN 1..4 LOOP

        SELECT NVL(COUNT(*),0) INTO MI_CANTIDAD
        FROM CODIGOS_UNSPSC
        WHERE COMPANIA = UN_COMPANIA
        AND   CODIGO   = TO_CHAR(MI_DATOS_COLUMNAS(MI_COLUM_ACTUAL_CODIGO));

        IF MI_CANTIDAD = 0 THEN

        IF  RS_NIVELES   = 1 THEN
        MI_NOMBRE_NIVEL  := 'SEGMENTO';
        ELSIF RS_NIVELES = 2 THEN
        MI_NOMBRE_NIVEL  := 'FAMILIA';
        ELSIF RS_NIVELES = 3 THEN
        MI_NOMBRE_NIVEL  := 'CLASE';
        ELSE
        MI_NOMBRE_NIVEL  := 'PRODUCTO';
        END IF;

          MI_CAMPOS:='COMPANIA,
                      CODIGO,
                      NOMBRE,
                      NIVEL,
                      NOMBRE_NIVEL,
                      CREATED_BY,
                      DATE_CREATED';

           MI_VALORES:= ' ''' ||  UN_COMPANIA                                ||''',
                          ''' ||  MI_DATOS_COLUMNAS(MI_COLUM_ACTUAL_CODIGO)  ||''',
                          ''' ||  MI_DATOS_COLUMNAS(MI_COLUM_ACTUAL_NOMBRE)  ||''',
                            ' ||  RS_NIVELES                                 ||  ',
                          ''' ||  MI_NOMBRE_NIVEL                            ||''',
                          ''' ||  UN_USUARIO                                 ||''',
                                  SYSDATE                                        ';
           BEGIN
              BEGIN
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => 'CODIGOS_UNSPSC'
                                                       ,UN_ACCION  => 'I'
                                                       ,UN_CAMPOS  => MI_CAMPOS
                                                       ,UN_VALORES => MI_VALORES);

                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                   RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;
              END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN
                      PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD    => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERR_ALM_INCONSISTENCIASPLACA5
                       );
            END;                         
        END IF;

      MI_COLUM_ACTUAL_CODIGO := MI_COLUM_ACTUAL_CODIGO + 2;
      MI_COLUM_ACTUAL_NOMBRE := MI_COLUM_ACTUAL_NOMBRE + 2;
      END LOOP NIVELES_DINAMICOS;

  END LOOP CARGA_CODIGOS; 

END PR_CARGAR_CODIGO_UNSPSC;

PROCEDURE PR_INSERTAR_ACUERDOSCOMERP3
/*
    NAME              : PR_INSERTAR_ACUERDOSCOMERP3
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : MIGUEL ANGEL VENEGAS RODRIGUEZ
    DATE MIGRADOR     : 26/07/2018
    TIME              : 09:00 AM  
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Inserta los acuerdos comerciales para cada uno de los estudios previos

      @NAME:    insertarAcuerdos 
      @METHOD:  POST
  */
(
UN_COMPANIA      IN   ES_ESTPREVIO.COMPANIA%TYPE,
UN_NRO_ESTUDIO   IN   ES_ESTPREVIO.COD_ESTUDIO%TYPE,
UN_TIPO_CONTRATO IN   ES_ESTPREVIO.TIPO_CONTRATO%TYPE,
UN_USUARIO       IN   VARCHAR2
)
AS
  MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_VALORES      PCK_SUBTIPOS.TI_VALORES;
  MI_RTA          PCK_SUBTIPOS.TI_ENTERO;
  MI_INDICADOR_SI VARCHAR2(2) := 'SI';
  MI_MSGERROR     PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_CANTIDAD     NUMBER(10);
  MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
BEGIN
<<ACUERDOS_COMERCIALES>>
FOR MI_RS IN (
              SELECT CODIGO,PAIS,DESCRIPCION_ACUERDO 
              FROM ACUERDOS_COMERCIALES 
              WHERE VIGENCIA NOT IN (0)
              ) LOOP

   SELECT COUNT(*) INTO MI_CANTIDAD
   FROM ACUERDOS_COMERCIALES_P3
   WHERE NUMERO_ESTUDIO = UN_NRO_ESTUDIO
   AND   TIPOCONTRATO   = UN_TIPO_CONTRATO
   AND   CODIGO         = MI_RS.CODIGO;

    IF MI_CANTIDAD = 0 THEN 
    BEGIN
      MI_TABLA:='ACUERDOS_COMERCIALES_P3';

      MI_CAMPOS:= ' COMPANIA
                   ,TIPOCONTRATO
                   ,NUMERO_ESTUDIO
                   ,CODIGO
                   ,PAIS
                   ,DESCRIPCION_ACUERDO
                   ,CREATED_BY
                   ,DATE_CREATED
                   ,ENTIDAD_ESTATAL
                   ,PRESUPUESTO_MAYOR_AC
                   ,EXPEDICION_APLICABLE
                   ,CONTRATACION_CUBIERTA ';

      MI_VALORES:= ' '''||UN_COMPANIA              ||'''
                    ,'''||UN_TIPO_CONTRATO         ||'''
                    ,'  ||UN_NRO_ESTUDIO           ||  '
                    ,'  ||MI_RS.CODIGO             ||  '
                    ,'''||MI_RS.PAIS               ||'''
                    ,'''||MI_RS.DESCRIPCION_ACUERDO||'''
                    ,'''||UN_USUARIO               ||'''
                    ,SYSDATE
                    ,'''||MI_INDICADOR_SI          ||'''
                    ,'''||MI_INDICADOR_SI          ||'''
                    ,'''||MI_INDICADOR_SI          ||'''
                    ,'''||MI_INDICADOR_SI          ||''' ';
          BEGIN
              MI_RTA:=PCK_DATOS.FC_ACME(
                                UN_TABLA   => MI_TABLA
                               ,UN_ACCION  => 'I'
                               ,UN_CAMPOS  => MI_CAMPOS
                               ,UN_VALORES => MI_VALORES );
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_PRECONTRACTUAL;
          END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_PRECONTRACTUAL THEN
              MI_MSGERROR (1).CLAVE := 'ACUERDO';
              MI_MSGERROR (1).VALOR := MI_RS.CODIGO;
          PCK_ERR_MSG.RAISE_WITH_MSG(
                          UN_EXC_COD    => SQLCODE,
                          UN_REEMPLAZOS => MI_MSGERROR,
                          UN_ERROR_COD => PCK_ERRORES.ERR_PRECONTRACT_ACUERDOSP3
                         );
  END;
  END IF;
END LOOP ACUERDOS_COMERCIALES;

END PR_INSERTAR_ACUERDOSCOMERP3;

PROCEDURE PR_CREARRIESGO_DEFECTO
/*
    NAME              : PR_CREARRIESGO_DEFECTO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN LEONARDO MALAVER JIMÉNEZ
    DATE MIGRADOR     : 01/10/2018
    TIME              : 08:30 aM  
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Permite la creación de un riesgo con valores código 999 y 
                        nombre VARIOS, por defecto, por cada tipo de riesgo insertado.

      @NAME:    crearRiesgoPorDefecto
      @METHOD:  POST
  */
    (
      UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_T_RIESGO       IN ES_RIESGO.COD_T_RIESGO%TYPE,
      UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
    )     
  AS 
      MI_CAMPOS                     PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES                    PCK_SUBTIPOS.TI_VALORES;
      MI_EXISTE                     PCK_SUBTIPOS.TI_ENTERO;

    BEGIN

        MI_CAMPOS      :=   ' COMPANIA,
                              COD_RIESGO, 
                              COD_T_RIESGO,
                              NOMBRE,                              
                              DATE_CREATED,
                              CREATED_BY';

        MI_VALORES     :=''''||UN_COMPANIA||''',                                          
                            '||999||',
                            '||UN_T_RIESGO||',
                            ''VARIOS'',                 
                            SYSDATE,                                          
                            ''' || UN_USUARIO ||'''' ; 
      BEGIN     
       BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => 'ES_RIESGO', 
                                                      UN_ACCION   => 'I', 
                                                      UN_CAMPOS   => MI_CAMPOS, 
                                                      UN_VALORES  => MI_VALORES);
       EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
       END;


       EXCEPTION
        WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE, 
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_CREARRIESGODEFECTO
                                        );
       END;     
END PR_CREARRIESGO_DEFECTO;	

END PCK_PRECONTRACTUAL1;