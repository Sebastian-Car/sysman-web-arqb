create or replace PACKAGE BODY PCK_CONTRATOS_COM2 AS

--1
PROCEDURE PR_REGISTRARCESION
(
   /*
      NAME              : PR_REGISTRARCESION 
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 11/08/2017
      TIME              : 14:15 PM
      SOURCE MODULE     : 
      DESCRIPTION       : Se pasa logica del Controlador Adicionespcontratos
                          Panel Principal\Control de Contratos\Procesos\Registro de novedades\Modificaciones a contratos
      MODIFIER          :
      DATE MODIFIED     :
      TIME MODIFIED     :
      MODIFICATIONS     :
      PARAMETERS        : 	UN_COMPANIA 		    =>  Compañia de ingreso a la aplicación
                            UN_NUMERO        	  => Numero seleccionado en el formulario 
                            UN_TIPOCONTRATO 		=> Codigo seleccionado en el combo Tipo de Modificacion del formulario que abre el Modificaciones a contratos
                            UN_NUMEROAFECTADO 	=> Codigo seleccionado en el combo Contrato afectar del formulario 
                            UN_TIPOAFECTADO 	  => Codigo seleccionado en el combo Tipo de Contrato Afectado del formulario que abre el Modificaciones a contratos
                            UN_USUARIO          => Usuario por la cual se loggeo en la aplicacion
                            UN_MENSAJE          => Mensaje creadro en el metodo registrarCesion del bean 
                            UN_NITTERCERO       => Nit Cesion del tercero 

    @NAME:  registrarCesion
    @METHOD:  PUT
    */
  UN_COMPANIA 		  IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_NUMERO		 	    IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
	UN_TIPOCONTRATO		IN VARCHAR2,
	UN_NUMEROAFECTADO IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
	UN_TIPOAFECTADO	 	IN VARCHAR2,
	UN_USUARIO 			  IN PCK_SUBTIPOS.TI_USUARIO,
	UN_MENSAJE        IN VARCHAR2,
	UN_NITTERCERO     IN VARCHAR2,
  UN_SUCURSALCESION IN VARCHAR2
) 
	AS
		MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
		MI_CAMPOS  		  PCK_SUBTIPOS.TI_CAMPOS;
		MI_CONDICION	  PCK_SUBTIPOS.TI_CONDICION;
		MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TERCERO      TERCERO.NOMBRE%TYPE; 

	BEGIN 
		BEGIN 
			BEGIN 
				MI_TABLA := 'ORDENDECOMPRA';
				MI_CAMPOS := 'OBJETOCONTRATO = '''||UN_MENSAJE||''' ,MODIFIED_BY = '''||UN_USUARIO||''' ,DATE_MODIFIED =  SYSDATE  ';
				MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND CLASEORDEN = '''||UN_TIPOCONTRATO||''' AND NUMERO = '||UN_NUMERO||'';

				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                        UN_ACCION    	=> 'M',
                                        UN_CAMPOS 		=> MI_CAMPOS,
                                        UN_CONDICION 	=> MI_CONDICION); 
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
					RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
			END;						
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
              			MI_REEMPLAZOS(0).CLAVE 	:= 'CAMPO';
                    MI_REEMPLAZOS(0).VALOR	:= 'OBJETOCONTRATO';
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERRR_ACTORDENDECOMPRA,
                        UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
		END;
		BEGIN 
      BEGIN 
        SELECT NOMBRE
        INTO MI_TERCERO
        FROM TERCERO
        WHERE COMPANIA  = UN_COMPANIA
          AND NIT       = UN_NITTERCERO
          AND SUCURSAL  = UN_SUCURSALCESION;
      EXCEPTION WHEN NO_DATA_FOUND THEN  
				MI_TERCERO:='';
			END;    
			BEGIN 
				MI_TABLA := 'ORDENDECOMPRA';
				MI_CAMPOS := 'NOMBRECONTRATISTA = '''||MI_TERCERO||''' ,TERCERO = '''||UN_NITTERCERO||''' ,SUCURSAL = '''||UN_SUCURSALCESION||''',MODIFIED_BY = '''||UN_USUARIO||''' ,DATE_MODIFIED =  SYSDATE  ';
				MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND CLASEORDEN = '''||UN_TIPOAFECTADO||''' AND NUMERO = '||UN_NUMEROAFECTADO||'';

				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA,
                                        UN_ACCION    	=> 'M',
                                        UN_CAMPOS 		=> MI_CAMPOS,
                                        UN_CONDICION 	=> MI_CONDICION); 
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
					RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
			END;						
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
              			MI_REEMPLAZOS(0).CLAVE 	:= 'CAMPO';
                    MI_REEMPLAZOS(0).VALOR	:= 'TERCERO';
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERRR_ACTORDENDECOMPRA,
                        UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
		END;
END PR_REGISTRARCESION;
--2
PROCEDURE PR_AFECTARITEM
(
  /*
      NAME              : PR_REGISTRARCESION 
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 11/08/2017
      TIME              : 14:15 PM
      SOURCE MODULE     : 
      DESCRIPTION       : Se pasa logica del Controlador Adicionespcontratos
                          Panel Principal\Control de Contratos\Procesos\Registro de novedades\Modificaciones a contratos
      MODIFIER          :
      DATE MODIFIED     :
      TIME MODIFIED     :
      MODIFICATIONS     :
      PARAMETERS        : 	UN_COMPANIA 		    =>  Compañia de ingreso a la aplicación
                            UN_NUMERO        	  => Numero seleccionado en el formulario 
                            UN_TIPOCONTRATO 		=> Codigo seleccionado en el combo Tipo de Modificacion del formulario que abre el Modificaciones a contratos
                            UN_NUMEROAFECTADO 	=> Codigo seleccionado en el combo Contrato afectar del formulario 
                            UN_TIPOAFECTADO 	  => Codigo seleccionado en el combo Tipo de Contrato Afectado del formulario que abre el Modificaciones a contratos
                            UN_USUARIO          => Usuario por la cual se loggeo en la aplicacion


    @NAME:  afectarItem
    @METHOD:  GET
    */

	UN_COMPANIA 		  IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_NUMERO		 	    IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
	UN_TIPOCONTRATO		IN VARCHAR2,
	UN_NUMEROAFECTADO IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
	UN_TIPOAFECTADO	 	IN VARCHAR2,
	UN_USUARIO 			  IN PCK_SUBTIPOS.TI_USUARIO
) 
	AS
		MI_TABLA          PCK_SUBTIPOS.TI_TABLA;
		MI_CAMPOS  		    PCK_SUBTIPOS.TI_CAMPOS;
		MI_VALORES  	    PCK_SUBTIPOS.TI_VALORES;
		MI_CONDICION	    PCK_SUBTIPOS.TI_CONDICION;
		MI_REEMPLAZOS     PCK_SUBTIPOS.TI_CLAVEVALOR;
		MI_VALORTOTAL	    PCK_SUBTIPOS.TI_DOBLE;
		MI_MERGEUSING     PCK_SUBTIPOS.TI_MERGEUSING; --JM 22/10/2024 7800516 
    MI_MERGEENLACE    PCK_SUBTIPOS.TI_MERGEENLACE;
    MI_MERGEEXISTE    PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_MERGENOEXIS    PCK_SUBTIPOS.TI_MERGEEXISTE;
    MI_RTA            PCK_SUBTIPOS.TI_RTA_ACME;

	BEGIN 
		BEGIN 
			BEGIN 
				MI_TABLA := 'D_ORDENDECOMPRA';
				MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND CLASEORDEN = '''||UN_TIPOAFECTADO||''' AND ORDENDECOMPRA = '||UN_NUMEROAFECTADO||' AND CANTIDAD = 0 AND VALORUNITARIO = 0 AND NUMEROORIGEN = '||UN_NUMERO; --JM 22/10/2024 7800516 

				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                        UN_TABLA      => MI_TABLA,
                                        UN_ACCION    	=> 'E',
                                        UN_CONDICION 	=> MI_CONDICION); 
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
					RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
			END;						
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
              			MI_REEMPLAZOS(0).CLAVE 	:= 'CLASEORDEN';
                    MI_REEMPLAZOS(0).VALOR	:= UN_TIPOAFECTADO;
                    MI_REEMPLAZOS(1).CLAVE 	:= 'ORDENDECOMPRA';
                    MI_REEMPLAZOS(1).VALOR	:= UN_NUMEROAFECTADO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERRR_CONTRATOS_ORDCOMPRA,
                        UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
		END;
		BEGIN 
			BEGIN 
				MI_TABLA := 'D_ORDENDECOMPRA';
				MI_CAMPOS := 'COMPANIA, CLASEORDEN, ORDENDECOMPRA, CODIGO, ORDENDESUMINISTRO, DEPENDENCIA, ELEMENTO, 
                      ESPECIFICACION, CENTRODECOSTO, CANTIDAD, SALDOCANT, VALORUNITARIO, PORCIVA, PORCDESC, 
                      VLRTOTAL, MARCA, VALORUNITARIODI, VLRIVA, VLRDESCUENTO, VALORENTREGADO, CANTENTREGADA, SERIE, 
                      SERIEDEVOLUTIVO, ESTADO, IMPRESO, RUBROCOMPRAS, FECHAADQUISICION, FECHASALIDASERVICIO, FECHABODEGA, 
                      ANOPPTO, TIPOPPTO, NUMEROPPTO, CONSECUTIVOPPTO, RUBROPPTO , VALORPPTO, BIENSERVICIO,  NUMEROAFECTADO,  
                      TIPOAFECTADO,  ITEMAFECTADO, NUMEROORIGEN, TIPOORIGEN, CANTIDADMODIFICADO, VALORTOTALMODIFICADO, 
                      VALORUNITARIOMODIFICADO, CREATED_BY, DATE_CREATED';

				MI_MERGEUSING := '	SELECT
									'''||UN_COMPANIA||''' COMPANIA,
									'''||UN_TIPOAFECTADO||''' CLASEORDEN,
									'||UN_NUMEROAFECTADO||' ORDENDECOMPRA,
									D_ORDENDECOMPRA.CODIGO,
									D_ORDENDECOMPRA.ORDENDESUMINISTRO,
									D_ORDENDECOMPRA.DEPENDENCIA,
									D_ORDENDECOMPRA.ELEMENTO,
									D_ORDENDECOMPRA.ESPECIFICACION,
									D_ORDENDECOMPRA.CENTRODECOSTO,
									D_ORDENDECOMPRA.CANTIDAD,
									D_ORDENDECOMPRA.SALDOCANT,
									D_ORDENDECOMPRA.VALORUNITARIO,
									D_ORDENDECOMPRA.PORCIVA,
									D_ORDENDECOMPRA.PORCDESC,
									D_ORDENDECOMPRA.VLRTOTAL,
									D_ORDENDECOMPRA.MARCA,
									D_ORDENDECOMPRA.VALORUNITARIODI,
									D_ORDENDECOMPRA.VLRIVA,
									D_ORDENDECOMPRA.VLRDESCUENTO,
									D_ORDENDECOMPRA.VALORENTREGADO,
									D_ORDENDECOMPRA.CANTENTREGADA,
									D_ORDENDECOMPRA.SERIE,
									D_ORDENDECOMPRA.SERIEDEVOLUTIVO,
									D_ORDENDECOMPRA.ESTADO,
									D_ORDENDECOMPRA.IMPRESO,
									D_ORDENDECOMPRA.RUBROCOMPRAS,
									D_ORDENDECOMPRA.FECHAADQUISICION,
									D_ORDENDECOMPRA.FECHASALIDASERVICIO,
									D_ORDENDECOMPRA.FECHABODEGA,
									D_ORDENDECOMPRA.ANOPPTO,
									D_ORDENDECOMPRA.TIPOPPTO,
									D_ORDENDECOMPRA.NUMEROPPTO,
									D_ORDENDECOMPRA.CONSECUTIVOPPTO,
									D_ORDENDECOMPRA.RUBROPPTO ,
									D_ORDENDECOMPRA.VALORPPTO,
									D_ORDENDECOMPRA.BIENSERVICIO,
									D_ORDENDECOMPRA.ORDENDECOMPRA NUMEROAFECTADO,
									D_ORDENDECOMPRA.CLASEORDEN TIPOAFECTADO,
									D_ORDENDECOMPRA.CODIGO ITEMAFECTADO,
                  D_ORDENDECOMPRA.NUMEROORIGEN,
                  D_ORDENDECOMPRA.TIPOORIGEN,
                  D_ORDENDECOMPRA.CANTIDADMODIFICADO,
                  D_ORDENDECOMPRA.VALORTOTALMODIFICADO,
                  D_ORDENDECOMPRA.VALORUNITARIOMODIFICADO,        
									'''||UN_USUARIO||''' CREATED_BY,
									SYSDATE DATE_CREATED
								  FROM D_ORDENDECOMPRA
								  WHERE D_ORDENDECOMPRA.COMPANIA 		= '''||UN_COMPANIA||'''
								  AND D_ORDENDECOMPRA.CLASEORDEN	='''||UN_TIPOCONTRATO||'''
								  AND D_ORDENDECOMPRA.ORDENDECOMPRA	= '||UN_NUMERO; --MOD JM 22/10/2024 
				
				MI_MERGEENLACE := 'TABLA.COMPANIA 		= VISTA.COMPANIA 
								           AND TABLA.CLASEORDEN	= VISTA.CLASEORDEN
								           AND TABLA.ORDENDECOMPRA	= VISTA.ORDENDECOMPRA
                           AND TABLA.CODIGO = VISTA.CODIGO';    
                
        MI_MERGEEXISTE := 'UPDATE SET   TABLA.ESPECIFICACION = VISTA.ESPECIFICACION,
                                  TABLA.CENTRODECOSTO = VISTA.CENTRODECOSTO,
                                  TABLA.CANTIDAD = VISTA.CANTIDAD,
                                  TABLA.SALDOCANT = VISTA.SALDOCANT,
                                  TABLA.VALORUNITARIO = VISTA.VALORUNITARIO,
                                  TABLA.PORCIVA = VISTA.PORCIVA,
                                  TABLA.PORCDESC = VISTA.PORCDESC,
                                  TABLA.VLRTOTAL = VISTA.VLRTOTAL,
                                  TABLA.MARCA = VISTA.MARCA,
                                  TABLA.VALORUNITARIODI = VISTA.VALORUNITARIODI,
                                  TABLA.VLRIVA = VISTA.VLRIVA,
                                  TABLA.VLRDESCUENTO = VISTA.VLRDESCUENTO,
                                  TABLA.VALORENTREGADO = VISTA.VALORENTREGADO,
                                  TABLA.CANTENTREGADA = VISTA.CANTENTREGADA,
                                  TABLA.SERIE = VISTA.SERIE,
                                  TABLA.SERIEDEVOLUTIVO = VISTA.SERIEDEVOLUTIVO,
                                  TABLA.ESTADO = VISTA.ESTADO,
                                  TABLA.IMPRESO = VISTA.IMPRESO,
                                  TABLA.RUBROCOMPRAS = VISTA.RUBROCOMPRAS,
                                  TABLA.FECHAADQUISICION = VISTA.FECHAADQUISICION,
                                  TABLA.FECHASALIDASERVICIO = VISTA.FECHASALIDASERVICIO,
                                  TABLA.FECHABODEGA = VISTA.FECHABODEGA,
                                  TABLA.ANOPPTO = VISTA.ANOPPTO,
                                  TABLA.TIPOPPTO = VISTA.TIPOPPTO,
                                  TABLA.NUMEROPPTO = VISTA.NUMEROPPTO,
                                  TABLA.CONSECUTIVOPPTO = VISTA.CONSECUTIVOPPTO,
                                  TABLA.RUBROPPTO  = VISTA.RUBROPPTO ,
                                  TABLA.VALORPPTO = VISTA.VALORPPTO,
                                  TABLA.BIENSERVICIO = VISTA.BIENSERVICIO,
                                  TABLA.NUMEROAFECTADO = VISTA.NUMEROAFECTADO,
                                  TABLA.TIPOAFECTADO = VISTA.TIPOAFECTADO,
                                  TABLA.ITEMAFECTADO = VISTA.ITEMAFECTADO,
                                  TABLA.NUMEROORIGEN = VISTA.NUMEROORIGEN,
                                  TABLA.TIPOORIGEN = VISTA.TIPOORIGEN,
                                  TABLA.CANTIDADMODIFICADO = VISTA.CANTIDADMODIFICADO,
                                  TABLA.VALORTOTALMODIFICADO = VISTA.VALORTOTALMODIFICADO,
                                  TABLA.VALORUNITARIOMODIFICADO = VISTA.VALORUNITARIOMODIFICADO';   

        MI_MERGENOEXIS := ' INSERT ( '||MI_CAMPOS||') 
                            VALUES (VISTA.COMPANIA,
                            VISTA.CLASEORDEN,
                            VISTA.ORDENDECOMPRA,
                            VISTA.CODIGO,
                            VISTA.ORDENDESUMINISTRO,
                            VISTA.DEPENDENCIA,
                            VISTA.ELEMENTO,
                            VISTA.ESPECIFICACION,
                            VISTA.CENTRODECOSTO,
                            VISTA.CANTIDAD,
                            VISTA.SALDOCANT,
                            VISTA.VALORUNITARIO,
                            VISTA.PORCIVA,
                            VISTA.PORCDESC,
                            VISTA.VLRTOTAL,
                            VISTA.MARCA,
                            VISTA.VALORUNITARIODI,
                            VISTA.VLRIVA,
                            VISTA.VLRDESCUENTO,
                            VISTA.VALORENTREGADO,
                            VISTA.CANTENTREGADA,
                            VISTA.SERIE,
                            VISTA.SERIEDEVOLUTIVO,
                            VISTA.ESTADO,
                            VISTA.IMPRESO,
                            VISTA.RUBROCOMPRAS,
                            VISTA.FECHAADQUISICION,
                            VISTA.FECHASALIDASERVICIO,
                            VISTA.FECHABODEGA,
                            VISTA.ANOPPTO,
                            VISTA.TIPOPPTO,
                            VISTA.NUMEROPPTO,
                            VISTA.CONSECUTIVOPPTO,
                            VISTA.RUBROPPTO ,
                            VISTA.VALORPPTO,
                            VISTA.BIENSERVICIO,
                            VISTA.NUMEROAFECTADO,
                            VISTA.TIPOAFECTADO,
                            VISTA.ITEMAFECTADO,
                            VISTA.NUMEROORIGEN,
                            VISTA.TIPOORIGEN,
                            VISTA.CANTIDADMODIFICADO,
                            VISTA.VALORTOTALMODIFICADO,
                            VISTA.VALORUNITARIOMODIFICADO,
                            VISTA.CREATED_BY,
                            VISTA.DATE_CREATED)';
        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA => MI_TABLA, UN_ACCION => 'IM' ,
                                    UN_MERGEUSING => MI_MERGEUSING,
                                    UN_MERGEENLACE => MI_MERGEENLACE ,
                                    UN_MERGEEXISTE => MI_MERGEEXISTE,
                                    UN_MERGENOEXIS => MI_MERGENOEXIS);
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
						      RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
					END;

				/* PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                        UN_TABLA     => MI_TABLA,
                                        UN_ACCION    	=> 'IS',
                                        UN_CAMPOS 		=> MI_CAMPOS,
                                        UN_VALORES 		=> MI_VALORES); 
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
					RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
			END; */--comentado por JM 22/10/2024						
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
              			MI_REEMPLAZOS(0).CLAVE 	:= 'CLASEORDEN';
                    MI_REEMPLAZOS(0).VALOR	:= UN_TIPOAFECTADO;
                    MI_REEMPLAZOS(1).CLAVE 	:= 'ORDENDECOMPRA';
                    MI_REEMPLAZOS(1).VALOR	:= UN_NUMEROAFECTADO;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERRR_CONTRATOS_ORDCOMPRAIN,
                        UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
		END;

		SELECT NVL(SUM(VLRTOTAL),0) AS VALORTOTAL 
		INTO MI_VALORTOTAL
		FROM D_ORDENDECOMPRA 
		WHERE D_ORDENDECOMPRA.COMPANIA        = UN_COMPANIA
			AND D_ORDENDECOMPRA.CLASEORDEN 	  = UN_TIPOAFECTADO
			AND D_ORDENDECOMPRA.ORDENDECOMPRA =  UN_NUMEROAFECTADO;

		BEGIN 
			BEGIN 
				MI_TABLA := 'ORDENDECOMPRA';	
				MI_CAMPOS := 'VALORTOTAL = '|| MI_VALORTOTAL ||',DATE_MODIFIED = SYSDATE , MODIFIED_BY = '''||UN_USUARIO||'''';
				MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND CLASEORDEN = '''||UN_TIPOCONTRATO||''' AND NUMERO = '||UN_NUMERO;
			PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                        UN_TABLA     => MI_TABLA,
                                        UN_ACCION    	=> 'M',
                                        UN_CAMPOS 		=> MI_CAMPOS,
                                        UN_CONDICION 	=> MI_CONDICION); 
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
				RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
			END;						
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
              			MI_REEMPLAZOS(0).CLAVE 	:= 'CAMPO';
                    MI_REEMPLAZOS(0).VALOR	:= 'VALORTOTAL';
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERRR_ACTORDENDECOMPRA,
                        UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
		END;
        --Ticket#7736622: Proceso que permite actualizar el contrato para ser llamado en las entradas cuando hay afectación de items
        BEGIN 
				MI_TABLA := 'ORDENDECOMPRA';	
				MI_CAMPOS := ' VACIA = ''N''';
				MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND CLASEORDEN = '''||UN_TIPOAFECTADO||''' AND NUMERO = '||UN_NUMEROAFECTADO;
			PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                        UN_TABLA     => MI_TABLA,
                                        UN_ACCION    	=> 'M',
                                        UN_CAMPOS 		=> MI_CAMPOS,
                                        UN_CONDICION 	=> MI_CONDICION); 
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
				RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
			END;
        
END PR_AFECTARITEM;
--3
FUNCTION FC_NOMINACESION
(
  /*
      NAME              : FC_NOMINACESION 
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 11/08/2017
      TIME              : 14:15 PM
      SOURCE MODULE     : 
      DESCRIPTION       : Se pasa logica del Controlador Adicionespcontratos
                          Panel Principal\Control de Contratos\Procesos\Registro de novedades\Modificaciones a contratos
      MODIFIER          :
      DATE MODIFIED     :
      TIME MODIFIED     :
      MODIFICATIONS     :
      PARAMETERS        : 	UN_COMPANIA 		    => Compañia de ingreso a la aplicación,
                            UN_NUMEROAFECTADO 	=>  Codigo seleccionado en el combo Contrato afectar del formulario ,
                            UN_TIPOAFECTADO     => Codigo seleccionado en el combo Tipo de Contrato Afectado del formulario que abre el Modificaciones a contratos,
                            UN_NITCESION        =>  Codigo seleccionado en el combo Nit Cesion del formulario ,
                            UN_FECHAINICIAL     => Fecha Inicial del formulario ,
                            UN_FECHAFINAL       => Fecha Final del formulario ,
                            UN_VALORTOTAL       =>  Valor Total del formulario ,
                            UN_NOMBRECESION     => Nombre de Tercero seleccionado en el combo nit cesion 
                            UN_USUARIO          =>  Usuario por la cual se loggeo en la aplicacion

    @NAME:  enviarNominaCesion
    @METHOD:  GET
    */
  UN_COMPANIA 		    IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_NUMEROAFECTADO 	IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
	UN_TIPOAFECTADO     IN VARCHAR2,
	UN_NITCESION        IN VARCHAR2,
	UN_FECHAINICIAL     IN TIMESTAMP,
	UN_FECHAFINAL       IN TIMESTAMP,
	UN_VALORTOTAL       IN PCK_SUBTIPOS.TI_DOBLE,
	UN_NOMBRECESION     IN VARCHAR2,
	UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)
	RETURN PCK_SUBTIPOS.TI_LOGICO
	AS
		MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
		MI_CAMPOS  		  PCK_SUBTIPOS.TI_CAMPOS;
		MI_VALORES 		  PCK_SUBTIPOS.TI_VALORES;
		MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
		MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
		MI_EMPLEADO 	  PCK_SUBTIPOS.TI_ENTERO;
		MI_ID_EMPLEADO 	PCK_SUBTIPOS.TI_ENTERO;
		MI_REGISTROS    PCK_SUBTIPOS.TI_ENTERO_LARGO;
		MI_DIFERENCIA   PCK_SUBTIPOS.TI_ENTERO;
		MI_COND         VARCHAR2(20 CHAR);
    MI_STRSQL       PCK_SUBTIPOS.TI_STRSQL;

	BEGIN 
		BEGIN
			BEGIN 
				MI_TABLA := 'PERSONAL';
				MI_CAMPOS := 'FECHA_FINAL_CT = '''||UN_FECHAFINAL||''' , DATE_MODIFIED = SYSDATE , MODIFIED_BY = '''||UN_USUARIO||'''';
				MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND APELLIDO1 = ''NO APLICA'' AND TIPO_CONTRATO = '''||UN_TIPOAFECTADO||'''
                          AND NUMERO_CONTRATO = '||UN_NUMEROAFECTADO;
        PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                        UN_TABLA     	=> MI_TABLA,
                                        UN_ACCION    	=> 'M',
                                        UN_CAMPOS 		=> MI_CAMPOS,
                                        UN_CONDICION 	=> MI_CONDICION); 		

			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
				RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
			END;	
		EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
              			MI_REEMPLAZOS(0).CLAVE 	:= 'TABLA';
                    MI_REEMPLAZOS(0).VALOR	:= MI_TABLA;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERRR_ACTUALIZORDCOMP,
                        UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
		END;	

		BEGIN
    MI_STRSQL := 'SELECT ID_DE_EMPLEADO 
              		FROM (  SELECT ID_DE_EMPLEADO 
                          FROM PERSONAL 
                          WHERE COMPANIA 			=  '''||UN_COMPANIA||'''
                            AND TIPO_CONTRATO 	= '''||UN_TIPOAFECTADO||'''
                            AND NUMERO_CONTRATO	= '||UN_NUMEROAFECTADO||')
                  WHERE ROWNUM=1';
    EXECUTE IMMEDIATE MI_STRSQL INTO MI_EMPLEADO ;
    EXCEPTION WHEN NO_DATA_FOUND THEN 
    MI_EMPLEADO := NULL;
    END;

		IF MI_EMPLEADO IS NOT NULL 
		THEN 
			BEGIN 
				BEGIN 
					MI_TABLA := 'PAGOS_CONTRATOS';
					MI_CAMPOS := '	VALOR_APLICAR = NULL , SALDO = PCK_SYSMAN_UTL.FC_ROUND(VALOR_CUOTA / 30 * EXTRACT(DAY FROM SYSDATE),0 ) ,
									VALPAGO = PCK_SYSMAN_UTL.FC_ROUND(VALOR_CUOTA / 30 * EXTRACT(DAY FROM SYSDATE),0 ), 
									DATE_MODIFIED = SYSDATE , MODIFIED_BY = '''||UN_USUARIO||'''';
					MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' 
									AND ROWID =(SELECT MAX(ROWID) 
												FROM 	PAGOS_CONTRATOS
												WHERE 	PAGOS_CONTRATOS.COMPANIA = '''||UN_COMPANIA||'''
													AND PAGOS_CONTRATOS.ID_DE_EMPLEADO = '||MI_EMPLEADO||'
													AND PAGOS_CONTRATOS.TIPO_CONTRATO='''||UN_TIPOAFECTADO||'''
													AND PAGOS_CONTRATOS.NUMERO_CONTRATO='||UN_NUMEROAFECTADO||')';

					PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                        UN_TABLA     	=> MI_TABLA,
                                        UN_ACCION    	=> 'M',
                                        UN_CAMPOS 		=> MI_CAMPOS,
                                        UN_CONDICION 	=> MI_CONDICION); 		

				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
					RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
				END;
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
              			MI_REEMPLAZOS(0).CLAVE 	:= 'TABLA';
                    MI_REEMPLAZOS(0).VALOR	:= MI_TABLA;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERRR_ACTUALIZORDCOMP,
                        UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
			END;
		END IF;

		SELECT COUNT(*) N
		INTO MI_REGISTROS
		FROM PERSONAL 
		WHERE PERSONAL.COMPANIA			= 	UN_COMPANIA
			AND PERSONAL.NUMERO_DCTO	=	UN_NITCESION
			AND PERSONAL.NUMERO_CONTRATO=	UN_NUMEROAFECTADO;

		IF MI_REGISTROS = 0 
		THEN 
			IF UN_FECHAINICIAL IS NULL OR UN_FECHAFINAL IS NULL 
			THEN MI_DIFERENCIA := 0;
			ELSE 
				SELECT TO_DATE(TO_CHAR(UN_FECHAFINAL,'DD/MM/YYYY')) - TO_DATE(TO_CHAR(UN_FECHAINICIAL,'DD/MM/YYYY')) 
				INTO MI_DIFERENCIA
				FROM DUAL;
			END IF;
			SELECT  PERSONAL.ID_DE_EMPLEADO + 1 AS ID_DE_EMPLEADO
			INTO MI_ID_EMPLEADO
			FROM PERSONAL 
			WHERE ID_DE_EMPLEADO = ( 	SELECT MAX(ID_DE_EMPLEADO)
										FROM PERSONAL
										WHERE COMPANIA = UN_COMPANIA
									);

			IF MI_EMPLEADO IS NULL 
			THEN MI_COND := 'IS NULL';
			ELSE MI_COND := MI_EMPLEADO;
			END IF;						

			BEGIN 
				BEGIN
					MI_TABLA := 'PERSONAL';
					MI_CAMPOS:= '	COMPANIA,ID_DE_EMPLEADO,APELLIDO1,NOMBRES,NUMERO_DCTO,ID_DE_CARGO,ESCALAFON,ID_DE_TIPO,ESTADO_ACTUAL,
									SALARIO_BASE_IBC,VALOR_CONTRATO,FECHA_INICIO_CT,FECHA_FINAL_CT,TIPO_CONTRATO,NUMERO_CONTRATO,
									DATE_CREATED,CREATED_BY';
                  --,DIAS_CONTRATO
					MI_VALORES := '	SELECT 	COMPANIA,
											'||MI_ID_EMPLEADO||',
											APELLIDO1,
											'''||UN_NOMBRECESION||''',
											'||UN_NITCESION||',
											ID_DE_CARGO,
											ESCALAFON,
											ID_DE_TIPO,
											ESTADO_ACTUAL,
											SALARIO_BASE_IBC,
											'||UN_VALORTOTAL||',
											'''||UN_FECHAINICIAL||''',
											'''||UN_FECHAFINAL||''',
											TIPO_CONTRATO,
											NUMERO_CONTRATO,
										  SYSDATE,
											'''||UN_USUARIO||'''
									FROM PERSONAL 
									WHERE PERSONAL.COMPANIA 		= '''||UN_COMPANIA||'''
										AND PERSONAL.ID_DE_EMPLEADO '||MI_COND||'';
										--	'||MI_DIFERENCIA||',
					PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                        UN_TABLA      => MI_TABLA,
                                        UN_ACCION    	=> 'IS',
                                        UN_CAMPOS 		=> MI_CAMPOS,
                                        UN_VALORES 		=> MI_VALORES);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
					RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
				END;	
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
              			MI_REEMPLAZOS(0).CLAVE 	:= 'EMPLEADO';
                    MI_REEMPLAZOS(0).VALOR	:= MI_ID_EMPLEADO;
                    MI_REEMPLAZOS(1).CLAVE 	:= 'TABLA';
                    MI_REEMPLAZOS(1).VALOR	:= MI_TABLA;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERRR_PERSONAL,
                        UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
			END;	
			BEGIN
				BEGIN 
					MI_TABLA := 'PAGOS_CONTRATOS';
					MI_CAMPOS := '	COMPANIA, ID_DE_PROCESO, ANO,MES,PERIODO, ID_DE_EMPLEADO,ID_DE_CONCEPTO, TIPO_CONTRATO, 
									NUMERO_CONTRATO, MONTO_INICIAL ,SALDO ,VALOR_CUOTA,OBSERVACIONES,VALPAGO,PERIODOCOBRO,PAGO,
									DATE_CREATED,CREATED_BY';
					MI_VALORES := '	SELECT  COMPANIA,
											''90'',
											TO_CHAR('''||UN_FECHAINICIAL||''',''YYYY''),
											TO_CHAR('''||UN_FECHAINICIAL||''',''MM''),
											''03'',
											'||MI_ID_EMPLEADO||',
											''002'',
											TIPO_CONTRATO,
											NUMERO_CONTRATO,
											'||UN_VALORTOTAL||',
											'||UN_VALORTOTAL||',
											SALARIO_BASE_IBC,
											'''',
											SALARIO_BASE_IBC,
											''03'',
											0,
											SYSDATE,
											'''||UN_USUARIO||'''
									FROM PERSONAL        
									WHERE PERSONAL.COMPANIA = '''||UN_COMPANIA||'''
										AND PERSONAL.ID_DE_EMPLEADO '||MI_COND;

					PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
										UN_TABLA     => MI_TABLA,
                                        UN_ACCION    	=> 'IS',
                                        UN_CAMPOS 		=> MI_CAMPOS,
                                        UN_VALORES 		=> MI_VALORES);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_MERGE THEN
					RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
				END;
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
              			MI_REEMPLAZOS(0).CLAVE 	:= 'EMPLEADO';
                    MI_REEMPLAZOS(0).VALOR	:= MI_ID_EMPLEADO;
                    MI_REEMPLAZOS(1).CLAVE 	:= 'TABLA';
                    MI_REEMPLAZOS(1).VALOR	:= MI_TABLA;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERRR_PERSONAL,
                        UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
			END;	
    RETURN -1;
		ELSE 
		RETURN 0;
		END IF;
	END FC_NOMINACESION;

--4  

  FUNCTION FC_ACTUADESPNOVEDADCONTR(
  /*
      NAME              : FC_ACTUADESPNOVEDADCONTR 
      AUTHORS           : SYSMAN
      AUTHOR MIGRACION  : YESSICA SANA ROJAS
      DATE MIGRADOR     : 14/08/2017
      TIME              : 05:00 PM
      SOURCE MODULE     : 
      DESCRIPTION       : Según el tipo de comprobante se actualizan campos condicionados al tipo en la tabla ORDENDECOMPRA
                          CONTROL CONTRATOS/PROCESOS/REGISTRO DE NOVEDADES/OTRAS/Boton Aceptar/Boton Novedades
      MODIFIER          :
      DATE MODIFIED     :
      TIME MODIFIED     :
      MODIFICATIONS     :
      PARAMETERS        : 	UN_COMPANIA 		    =>  Compañia de ingreso a la aplicación,
                            UN_STRTIPOT         =>  Tipo de comprobante,
                            UN_CLASEORDEN       =>  Clase de orden formulario,
                            UN_NUMERO           =>  Numero novedad del formulario ,
                            UN_NVLPEJECUCION    =>  Porcentaje ejecucion del formulario ,
                            UN_FECHAINICIAL     =>  Fecha Inicial del formulario ,
                            UN_FECHAFINAL       =>  Fecha Inicial del formulario ,
                            UN_VALORTOTAL       =>  Valor diligenciado en formulario, 
                            UN_DIASCONTRATO     =>  Días de formulario

    @NAME:  actDespNovedadContr
    @METHOD:  GET
    */

    UN_COMPANIA       	IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_STRTIPOT         IN CLASETRANSACCIONC.TIPOT%TYPE,
    UN_CLASEORDEN       IN ORDENDECOMPRA.CLASEORDEN%TYPE,
    UN_NOVEDAD          IN NOVEDADCONTRATO.NOVEDAD%TYPE,
    UN_NUMERO           IN ORDENDECOMPRA.NUMERO%TYPE,
    UN_NVLPEJECUCION    IN NOVEDADCONTRATO.POREJECUCION%TYPE,
    UN_FECHAINICIAL     IN DATE,
    UN_FECHAFINAL       IN DATE,
    UN_FECHAVENCIMIENTO IN DATE,
    UN_VALORTOTAL       IN NOVEDADCONTRATO.VALORTOTAL%TYPE,
    UN_DIASCONTRATO     IN NOVEDADCONTRATO.DIAS_CONTRATO%TYPE
)RETURN PCK_SUBTIPOS.TI_LOGICO AS 
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;  
    MI_NVLPEJECUCION    NOVEDADCONTRATO.POREJECUCION%TYPE;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_RTA              PCK_SUBTIPOS.TI_ENTERO_LARGO; 
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALORTOTAL       ORDENDECOMPRA.VALORTOTAL%TYPE;
    MI_DURACION         ORDENDECOMPRA.DURACION%TYPE;
    MI_PLAZODEENTREGA   ORDENDECOMPRA.PLAZODEENTREGA%TYPE;
    MI_VALORTOTALFORM   ORDENDECOMPRA.VALORTOTAL%TYPE;
    MI_DURACIONFORM     ORDENDECOMPRA.DURACION%TYPE;
    MI_DIASCONTRATO     NOVEDADCONTRATO.DIAS_CONTRATO%TYPE;
    MI_VALOR            PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_VALORTOT         PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN
   /* MI_CONDICION := '     ORDENDECOMPRA.COMPANIA   = ''' || UN_COMPANIA   ||  ''''  || 
                    ' AND ORDENDECOMPRA.CLASEORDEN = ''' || UN_CLASEORDEN ||  ''''  ||
                    ' AND ORDENDECOMPRA.NUMERO     = ''' || UN_NUMERO     ||  '''';
    MI_TABLA := 'ORDENDECOMPRA';
        MI_NVLPEJECUCION := NVL(UN_NVLPEJECUCION,'');
        IF MI_NVLPEJECUCION IS NOT NULL THEN 
            MI_CAMPOS := ' PORCESTADOEJECUCION = ' || MI_NVLPEJECUCION;
            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                               ,UN_ACCION    => 'M' 
                                               ,UN_CAMPOS    => MI_CAMPOS
                                               ,UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                    THEN RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                MI_REEMPLAZOS(0).CLAVE := 'TABLA';
                MI_REEMPLAZOS(0).VALOR := MI_TABLA;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTUALIZORDCOMP
                                          ,UN_TABLAERROR => MI_TABLA
                                          ,UN_REEMPLAZOS => MI_REEMPLAZOS);    
            END;	    
        END IF;*/

          PCK_CONTRATOS_COM2.PR_CALCVALTOTCONTRATO(
        UN_COMPANIA,
        UN_STRTIPOT,
        UN_CLASEORDEN,
        UN_NUMERO,
        UN_NOVEDAD,
        UN_NVLPEJECUCION,
        UN_VALORTOTAL
  );
    SELECT A.VALORFINAL INTO MI_VALORTOT from ORDENDECOMPRA A WHERE A.COMPANIA=UN_COMPANIA AND A.NUMERO=UN_NUMERO AND A.CLASEORDEN=UN_CLASEORDEN;--(CC:3099 Se toma el valor final no el total)
      SELECT SUM(VALORTOTAL)INTO MI_VALOR  FROM NOVEDADCONTRATO WHERE COMPANIA=UN_COMPANIA 
                    
                    AND CLASEORDEN=UN_CLASEORDEN
                    AND ORDENDECOMPRA=UN_NUMERO
                    
                    ;

     BEGIN--(CC:3099_INI)
    IF MI_VALORTOT = 0 THEN
       RAISE PCK_EXCEPCIONES.EXC_INTERFAZ; 
    END IF;
    
    EXCEPTION 
    WHEN PCK_EXCEPCIONES.EXC_INTERFAZ  THEN
        MI_TABLA := 'ORDENDECOMPRA';
        MI_REEMPLAZOS(1).CLAVE := 'CONTRATO';
        MI_REEMPLAZOS(1).VALOR := UN_NUMERO;
        MI_REEMPLAZOS(2).CLAVE := 'TIPO';
        MI_REEMPLAZOS(2).VALOR := UN_CLASEORDEN;
        
        PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                    UN_ERROR_COD  => PCK_ERRORES.ERR_CONTRATO_VALORCERO,
                                    UN_TABLAERROR => MI_TABLA,
                                    UN_REEMPLAZOS => MI_REEMPLAZOS);

                                     RETURN 0;

       END;--(CC:3099_FIN) 

    MI_NVLPEJECUCION :=ROUND(MI_VALOR/MI_VALORTOT *100,2);
                
    MI_CONDICION := '     ORDENDECOMPRA.COMPANIA   = ''' || UN_COMPANIA   ||  ''''  || 
                    ' AND ORDENDECOMPRA.CLASEORDEN = ''' || UN_CLASEORDEN ||  ''''  ||
                    ' AND ORDENDECOMPRA.NUMERO     = ''' || UN_NUMERO     ||  '''';
    MI_TABLA := 'ORDENDECOMPRA';
      
        IF MI_NVLPEJECUCION IS NOT NULL THEN 
            MI_CAMPOS := ' PORCESTADOEJECUCION = ' || MI_NVLPEJECUCION    ;
            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                               ,UN_ACCION    => 'M' 
                                               ,UN_CAMPOS    => MI_CAMPOS
                                               ,UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                    THEN RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                MI_REEMPLAZOS(0).CLAVE := 'TABLA';
                MI_REEMPLAZOS(0).VALOR := MI_TABLA;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTUALIZORDCOMP
                                          ,UN_TABLAERROR => MI_TABLA
                                          ,UN_REEMPLAZOS => MI_REEMPLAZOS);    
            END;	    
        END IF;
        IF UN_STRTIPOT IN ('ACS') THEN
            MI_CAMPOS := 'ESTADO = ' || '''S''' ||  ', FECHAPSUSPENSION = ''' || UN_FECHAINICIAL || '''';
            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                               ,UN_ACCION    => 'M' 
                                               ,UN_CAMPOS    => MI_CAMPOS
                                               ,UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                    THEN RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                MI_REEMPLAZOS(0).CLAVE := 'TABLA';
                MI_REEMPLAZOS(0).VALOR := MI_TABLA;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTUALIZORDCOMP
                                          ,UN_TABLAERROR => MI_TABLA
                                          ,UN_REEMPLAZOS => MI_REEMPLAZOS);    
            END;
        END IF;  
        IF UN_STRTIPOT IN ('ARI') THEN 
            MI_CAMPOS := 'ESTADO = ' || '''V''' || ', FECHAFINALIZACION = ' || '''' || UN_FECHAFINAL || ''''  ||
                         ', FECHAREINICIO = ' || '''' || UN_FECHAINICIAL || '''';
            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA,
                                                UN_ACCION     => 'M',
                                                UN_CAMPOS     => MI_CAMPOS,
                                                UN_CONDICION  => MI_CONDICION
                                                );
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR
                    THEN RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                MI_REEMPLAZOS(0).CLAVE := 'TABLA';
                MI_REEMPLAZOS(0).VALOR := MI_TABLA;
                PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTUALIZORDCOMP,
                                            UN_TABLAERROR => MI_TABLA,
                                            UN_REEMPLAZOS => MI_REEMPLAZOS);
            END;
        END IF;
        IF UN_STRTIPOT IN ('PRO') THEN--- Si es comprobante prorroga actualice la fecha de finalizacion:(CFBARRERA_TIKCET:7801813_FECHA:12-12-2024)
            MI_CAMPOS :=   ' FECHAFINALIZACION = ' || '''' || UN_FECHAVENCIMIENTO || '''';
            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA,
                                                UN_ACCION     => 'M',
                                                UN_CAMPOS     => MI_CAMPOS,
                                                UN_CONDICION  => MI_CONDICION);
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR
                    THEN RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                MI_REEMPLAZOS(0).CLAVE := 'TABLA';
                MI_REEMPLAZOS(0).VALOR := MI_TABLA;
                PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTUALIZORDCOMP,
                                            UN_TABLAERROR => MI_TABLA,
                                            UN_REEMPLAZOS => MI_REEMPLAZOS);
            END;
        END IF;
        IF UN_STRTIPOT IN ('ATM') THEN 
            MI_CAMPOS :=  'ESTADO = ' || '''M''' || ', FECHALIQUIDACION = ' || '''' || UN_FECHAFINAL || '''' ||
                          ', FECHAFINALIZACION = ' || '''' || UN_FECHAFINAL || '''' || ', VALORTOTAL = ' || '''' || NVL(UN_VALORTOTAL,0) || '''' ;
            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA,
                                                UN_ACCION     => 'M',
                                                UN_CAMPOS     => MI_CAMPOS,
                                                UN_CONDICION  => MI_CONDICION);
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR
                    THEN RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                MI_REEMPLAZOS(0).CLAVE := 'TABLA';
                MI_REEMPLAZOS(0).VALOR := MI_TABLA;
                PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTUALIZORDCOMP,
                                            UN_TABLAERROR => MI_TABLA,
                                            UN_REEMPLAZOS => MI_REEMPLAZOS);
            END;
        END IF;
        IF UN_STRTIPOT IN ('ACL') THEN 
            MI_CAMPOS := 'ESTADO = ' || '''L''' || '' || ', FECHALIQUIDACION = ' || '''' || UN_FECHAFINAL || '''';
            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA,
                                                UN_ACCION     => 'M',
                                                UN_CAMPOS     => MI_CAMPOS,
                                                UN_CONDICION  => MI_CONDICION);
               EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR
                    THEN RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                MI_REEMPLAZOS(0).CLAVE := 'TABLA';
                MI_REEMPLAZOS(0).VALOR := MI_TABLA;
                PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTUALIZORDCOMP,
                                            UN_TABLAERROR => MI_TABLA,
                                            UN_REEMPLAZOS => MI_REEMPLAZOS);
            END;
        END IF;
        IF UN_STRTIPOT IN ('ACI') THEN 
            IF UN_VALORTOTAL IS NOT NULL THEN
                MI_CAMPOS :=  'FECHA = ' || '''' || UN_FECHAINICIAL || '''' || ', FECHAFINALIZACION = ' || '''' || UN_FECHAFINAL || '''' ||
                              ', FECHAINICIO = ' || '''' || UN_FECHAINICIAL || '''';
                BEGIN
                    BEGIN
                        MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA,
                                                    UN_ACCION     => 'M',
                                                    UN_CAMPOS     => MI_CAMPOS,
                                                    UN_CONDICION  => MI_CONDICION);
                   EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR
                        THEN RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                    END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                    MI_REEMPLAZOS(0).CLAVE := 'TABLA';
                    MI_REEMPLAZOS(0).VALOR := MI_TABLA;
                    PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                                UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTUALIZORDCOMP,
                                                UN_TABLAERROR => MI_TABLA,
                                                UN_REEMPLAZOS => MI_REEMPLAZOS);
                END;
            END IF;
        END IF;
        IF UN_STRTIPOT IN ('ADI') THEN
            BEGIN
                SELECT VALORTOTAL, DURACION, PLAZODEENTREGA 
                  INTO MI_VALORTOTAL , MI_DURACION, MI_PLAZODEENTREGA
                  FROM ORDENDECOMPRA WHERE COMPANIA = UN_COMPANIA   
                   AND CLASEORDEN = UN_CLASEORDEN   AND NUMERO = UN_NUMERO;
            EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_VALORTOTALFORM := 0;
                MI_DURACIONFORM := 0;
                MI_PLAZODEENTREGA := 0;
            END;
            MI_VALORTOTALFORM := UN_VALORTOTAL;
            MI_DURACIONFORM := UN_DIASCONTRATO;

            IF MI_PLAZODEENTREGA IS NULL THEN 
                MI_DURACION := 0;
                ELSE MI_DURACION := MI_PLAZODEENTREGA;
            END IF;
             MI_VALOR := TO_NUMBER(MI_DURACION) + TO_NUMBER(MI_DURACIONFORM);
             MI_VALORTOT := MI_VALORTOTAL + MI_VALORTOTALFORM;
            BEGIN
                MI_CAMPOS :=  'FECHAFINALIZACION = ''' || UN_FECHAFINAL || ''', 
                               DURACION = ''' ||  MI_VALOR || ''''; 
                    -- VALORTOTAL = ' || MI_VALORTOT || 
                    --MOD JM CC 927 27/03/2025 no debe afectar al valor del contrato

                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA      => MI_TABLA,
                                                UN_ACCION     => 'M',
                                                UN_CAMPOS     => MI_CAMPOS,
                                                UN_CONDICION  => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR
                     THEN RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                MI_REEMPLAZOS(0).CLAVE := 'TABLA';
                MI_REEMPLAZOS(0).VALOR := MI_TABLA;
                PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                            UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTUALIZORDCOMP,
                                            UN_TABLAERROR => MI_TABLA,
                                            UN_REEMPLAZOS => MI_REEMPLAZOS);
            END;
        END IF;

     RETURN 1;
END FC_ACTUADESPNOVEDADCONTR;

--5

FUNCTION FC_ELIMINNOVEDCONTRAT (
 /*
      NAME              : FC_ELIMINNOVEDCONTRAT 
      AUTHORS           : SYSMAN
      AUTHOR MIGRACION  : YESSICA SANA ROJAS
      DATE MIGRADOR     : 15/08/2017
      TIME              : 12:00 PM
      SOURCE MODULE     : 
      DESCRIPTION       : Se verifica que contrato no cuenta con novedades, para permitir eliminar
                          CONTROL CONTRATOS/PROCESOS/REGISTRO DE NOVEDADES/OTRAS/Boton Aceptar/Boton Novedades
      MODIFIER          :
      DATE MODIFIED     :
      TIME MODIFIED     :
      MODIFICATIONS     :
      PARAMETERS        : 	UN_COMPANIA 		    =>  Compañia de ingreso a la aplicación,
                            UN_CTIPOT           =>  Tipo transaccion dependiendo de formulario,
                            UN_CCLASET          =>  Clase transaccion dependiendo de formulario,
                            UN_CLASEORDEN       =>  Clase de orden formulario,
                            UN_NUMERO           =>  Numero de orden de compra del formulario,
                            UN_NOVEDAD          =>  Numero de la novedad de la orden de compra,
                            UN_ORDENCOMPRA      =>  Numero de ordendecompra
    @NAME:  eliminarNovedadContrato
    @METHOD:  GET
    */
  UN_COMPANIA   IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CTIPOT     IN CLASETRANSACCIONC.TIPOT%TYPE,
  UN_CCLASET    IN CLASETRANSACCIONC.CLASET%TYPE,
  UN_CLASEORDEN IN D_NOVEDADCONTRATO.CLASEORDEN%TYPE,
  UN_NUMERO     IN D_NOVEDADCONTRATO.ORDENDECOMPRA%TYPE,
  UN_NOVEDAD    IN D_NOVEDADCONTRATO.NOVEDAD%TYPE,
  UN_ORDENCOMPRA IN NOVEDADCONTRATO.ORDENDECOMPRA%TYPE
)RETURN PCK_SUBTIPOS.TI_LOGICO AS
  MI_AFECTAITEMS  PCK_SUBTIPOS.TI_LOGICO;
  MI_NUMERO       PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_TIPOT        NOVEDADCONTRATO.TIPOT%TYPE;
  MI_NIVELTIPOT   NOVEDADCONTRATO.TIPOT%TYPE;
  MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA          PCK_SUBTIPOS.TI_ENTERO_LARGO;
  MI_TIPOTR       NOVEDADCONTRATO.TIPOT%TYPE;
  MI_NOMBRE       CLASETRANSACCIONC.NOMBRE%TYPE;
  MI_TABLA        PCK_SUBTIPOS.TI_TABLA;
  MI_CADENA       PCK_SUBTIPOS.TI_VALORES;
  MI_REEMPLAZOS   PCK_SUBTIPOS.TI_CLAVEVALOR;
  MI_AUX          PCK_SUBTIPOS.TI_ENTERO_LARGO;
BEGIN
    MI_AUX := '';
    BEGIN 
        SELECT AFECTAITEMS
          INTO MI_AFECTAITEMS
          FROM CLASETRANSACCIONC
         WHERE TIPOT = UN_CTIPOT
           AND CLASET  = UN_CCLASET;
    END;
    IF MI_AFECTAITEMS NOT IN (0) THEN
        BEGIN
            SELECT COUNT(*) NUMERO
              INTO  MI_NUMERO
              FROM D_NOVEDADCONTRATO
             WHERE COMPANIA    = UN_COMPANIA
               AND CLASEORDEN    = UN_CLASEORDEN
               AND ORDENDECOMPRA = UN_NUMERO
               AND NOVEDAD       = UN_NOVEDAD;
        END;
        IF MI_NUMERO NOT IN (0) THEN 
            BEGIN 
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                    PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                              ,UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_NOVEDADITEMREL
                                              ,UN_TABLAERROR => 'D_NOVEDADCONTRATO');    
            END;	    
        END IF;
    END IF;
    BEGIN
         PR_CALCULAR_PORC_ORDCOMPRA(
            UN_COMPANIA,
            UN_CLASEORDEN,
            UN_NUMERO);
    END;
    BEGIN 
        SELECT TIPOT
          INTO  MI_TIPOT
          FROM NOVEDADCONTRATO
         WHERE COMPANIA      = UN_COMPANIA
           AND CLASEORDEN    = UN_CLASEORDEN
           AND ORDENDECOMPRA = UN_ORDENCOMPRA
           AND NOVEDAD       = UN_NOVEDAD;
    EXCEPTION WHEN NO_DATA_FOUND THEN
        MI_TIPOT := '';
    END;
    IF MI_TIPOT IS NULL THEN
        MI_TIPOT := '';
    END IF;
    IF MI_TIPOT IN ('ACI') THEN
        MI_TABLA := 'ORDENDECOMPRA';
        MI_CADENA := '';

        FOR RS IN (
        	  SELECT NOVEDADCONTRATO.TIPOT, CLASETRANSACCIONC.NOMBRE
              FROM NOVEDADCONTRATO
             INNER JOIN CLASETRANSACCIONC
                ON NOVEDADCONTRATO.TIPOT = CLASETRANSACCIONC.TIPOT
             WHERE NOVEDADCONTRATO.CLASET       = 'N'
               AND NOVEDADCONTRATO.TIPOT        <>'ACI'
               AND NOVEDADCONTRATO.COMPANIA     = UN_COMPANIA 
               AND NOVEDADCONTRATO.CLASEORDEN   = UN_CLASEORDEN
               AND NOVEDADCONTRATO.ORDENDECOMPRA= UN_NUMERO)
        LOOP 
            MI_AUX := 1;
            IF  MI_AUX = 1 THEN
                MI_CADENA := RS.TIPOT || ' - ' || RS.NOMBRE;
                 MI_AUX := 2;
            ELSE 
                MI_CADENA := MI_CADENA || ',' || RS.TIPOT || ' - ' || RS.NOMBRE || ' ';
            END IF;

        END LOOP; 

        IF  MI_AUX IS NOT NULL THEN 
            BEGIN 
                    MI_REEMPLAZOS (0).CLAVE := 'CADENA';
                    MI_REEMPLAZOS (0).VALOR := MI_CADENA;
                    RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
                    PCK_ERR_MSG.RAISE_WITH_MSG (UN_EXC_COD    => SQLCODE,
                                                UN_ERROR_COD  => PCK_ERRORES.ERRR_CONTRATOS_SINELIMINARACTA,
                                                UN_TABLAERROR => MI_TABLA,
                                                UN_REEMPLAZOS => MI_REEMPLAZOS);
            END;   
        ELSE
            MI_CAMPOS    :='FECHAINICIO = ' || '''' || '''' ;
            MI_CONDICION :='COMPANIA = '''    || UN_COMPANIA   || '''
                            AND CLASEORDEN='''|| UN_CLASEORDEN || '''
                            AND NUMERO ='''   || UN_NUMERO || '''';
            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                               ,UN_ACCION    => 'M' 
                                               ,UN_CAMPOS    => MI_CAMPOS
                                               ,UN_CONDICION => MI_CONDICION);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                    THEN RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTUALIZORDCOMP
                                          ,UN_TABLAERROR => MI_TABLA);   
            END;
        END IF;   
    END IF;    
  RETURN -1;
END FC_ELIMINNOVEDCONTRAT;

--6
FUNCTION FC_ACTUALIZAR_PSUBCONTRATO
/*
    NAME              : PR_ACTUALIZAR_PSUBCONTRATO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 15/08/2017
    TIME              : 10:15 AM  
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : 
    PARAMETERS        : UN_COMPANIA           => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_ORDENDESUMINISTRO  => PARAMETRO QUE REPRESENTA LA ORDEN DE SUMINISTRO PARA REALIZAR EL PROCESO. 
                        UN_CODIGO             => CODIGO DE LA ORDEN DE SUMINISTRO.  
                        UN_CANTIDADANTERIOR   => CANTIDAD ANTERIOR DE ITEMS.
                        UN_CANTIDAD           => CANTIDAD DE ITEMS. 
                        UN_ORDENDECOMPRA      => ORDEN DE COMPRA A ACTUALIZAR.
                        UN_CODIGOORDEN        => PARAMETRO PARA FILTRAR LA TABLA D_ORDENDECOMPRA.
                        UN_TIPOAFECTADO       => CLASE DE ORDEN DE LA ORDEN DE COMPRA.
                        UN_NUMEROAFECTADO     => NUMERO DE LA ORDEN DE COMPRA.
                        UN_CLASEORDEN         => CLASE ORDEN PARA FILTRAR LA TABLA D_ORDENDECOMPRA.
                        UN_CLASEORDENANT      => CLASE DE ORDEN ANTERIOR.
                        UN_NUMEROORDENANT     => NUMERO DE LA ORDEN DE COMPRA.
                        UN_USUARIO            => PARAMETRO QUE DETERMINA EL USUARIO AL INSERTAR O MODIFICAR. 
    @NAME:    actualizarPSubcontrato 
    @METHOD:  GET

  */
(
  UN_COMPANIA            IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_ORDENDESUMINISTRO   IN D_ORDENDESUMINISTRO.ORDENDESUMINISTRO%TYPE,
  UN_CODIGO              IN D_ORDENDESUMINISTRO.CODIGO%TYPE,
  UN_CANTIDADANTERIOR    IN D_ORDENDECOMPRA.CANTIDAD%TYPE,
  UN_CANTIDAD            IN D_ORDENDECOMPRA.CANTIDAD%TYPE,
  UN_ORDENDECOMPRA       IN D_ORDENDECOMPRA.ORDENDECOMPRA%TYPE,
  UN_CODIGOORDEN         IN D_ORDENDECOMPRA.CODIGO%TYPE,
  UN_TIPOAFECTADO        IN D_ORDENDECOMPRA.CLASEORDEN%TYPE,
  UN_NUMEROAFECTADO      IN D_ORDENDECOMPRA.ORDENDECOMPRA%TYPE,
  UN_CLASEORDEN          IN D_ORDENDECOMPRA.CLASEORDEN%TYPE,
  UN_CLASEORDENANT       IN D_ORDENDECOMPRA.CLASEORDEN%TYPE,
  UN_NUMEROORDENANT      IN D_ORDENDECOMPRA.ORDENDECOMPRA%TYPE,
  UN_USUARIO             IN PCK_SUBTIPOS.TI_USUARIO
)
RETURN PCK_SUBTIPOS.TI_DOBLE
AS
  MI_CANTPENT           D_ORDENDESUMINISTRO.CANTIDADPORENTREGAR%TYPE;
  MI_CONSECUTIVO        D_ORDENDECOMPRA.CODIGO%TYPE;
  MI_SALDO              D_ORDENDESUMINISTRO.CANTIDADPORENTREGAR%TYPE;
  MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;
  MI_NUMERO             PCK_SUBTIPOS.TI_LONG;
  MI_CRITERIO           PCK_SUBTIPOS.TI_CONDICION;
  MI_VALORES            PCK_SUBTIPOS.TI_VALORES; 
  MI_VALORTOTAL         ORDENDECOMPRA.VALORTOTAL%TYPE;
  MI_VLRTOTAL           ORDENDECOMPRA.VALORTOTAL%TYPE;
  MI_SUBTOTAL           D_ORDENDECOMPRA.VALORUNITARIO%TYPE;
  MI_VALORIVA           D_ORDENDECOMPRA.VLRIVA%TYPE;
  MI_VALORDESCUENTO     D_ORDENDECOMPRA.VLRDESCUENTO%TYPE;
BEGIN
  EXECUTE IMMEDIATE 'ALTER SESSION SET NLS_NUMERIC_CHARACTERS=''.,''';
  IF  UN_CANTIDAD <> UN_CANTIDADANTERIOR AND UN_CODIGO <> -1 THEN
    FOR RS IN ( SELECT D_ORDENDESUMINISTRO.COMPANIA, 
                     D_ORDENDESUMINISTRO.ORDENDESUMINISTRO, 
                     D_ORDENDESUMINISTRO.CODIGO,
                     D_ORDENDESUMINISTRO.CANTIDAD,
                     D_ORDENDESUMINISTRO.CANTIDADAPROBADA,
                     D_ORDENDESUMINISTRO.VACIA,
                     CANTIDADPORENTREGAR  
               FROM D_ORDENDESUMINISTRO
               WHERE D_ORDENDESUMINISTRO.COMPANIA           = UN_COMPANIA
                 AND D_ORDENDESUMINISTRO.ORDENDESUMINISTRO  = UN_ORDENDESUMINISTRO  
                 AND D_ORDENDESUMINISTRO.CODIGO             = UN_CODIGO)
    LOOP
      MI_CANTPENT := NVL(RS.CANTIDADPORENTREGAR,0);
      MI_SALDO := MI_CANTPENT + UN_CANTIDADANTERIOR + UN_CANTIDAD;

      IF MI_SALDO > 0 THEN
        MI_TABLA := 'D_ORDENDESUMINISTRO';
        MI_CAMPOS := '  VACIA               = 0,
                        CANTIDADPORENTREGAR = ' ||UN_CANTIDAD || ',
                        DATE_MODIFIED = SYSDATE,
                        MODIFIED_BY = '''||UN_USUARIO||'''';
        MI_CONDICION := 'COMPANIA               = '''||UN_COMPANIA||'''
                         AND ORDENDESUMINISTRO  = '''||UN_ORDENDESUMINISTRO||'''
                         AND CODIGO             = '  ||UN_CODIGO||'';
        BEGIN
          BEGIN
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                         UN_ACCION    =>  'M', 
                                         UN_CAMPOS    =>  MI_CAMPOS,
                                         UN_CONDICION =>  MI_CONDICION);                 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
          END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERRR_CONTRATOS_ACTCANTENTREGA
              );
        END;

        IF MI_RTA > 0 THEN
           MI_TABLA := 'ORDENDESUMINISTRO';
           MI_CAMPOS := ' VACIA         = 0,
                          DATE_MODIFIED = SYSDATE,
                          MODIFIED_BY   = '''||UN_USUARIO||'''';
           MI_CONDICION := 'COMPANIA               = '''||UN_COMPANIA||'''
                            AND NUMERO  = '||UN_ORDENDESUMINISTRO||'';
            BEGIN
              BEGIN
                MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                             UN_ACCION    =>  'M', 
                                             UN_CAMPOS    =>  MI_CAMPOS,
                                             UN_CONDICION =>  MI_CONDICION);                 
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
              END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
                  PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD   => SQLCODE,
                    UN_ERROR_COD => PCK_ERRORES.ERRR_CONTRATOS_ACTORDENSUMIN
                  );
            END;
        END IF;  
      ELSE 
        MI_TABLA := 'D_ORDENDESUMINISTRO';
        MI_CAMPOS := '  VACIA               = -1,
                        CANTIDADPORENTREGAR = ' ||UN_CANTIDAD ||',
                        DATE_MODIFIED = SYSDATE,
                        MODIFIED_BY = '''||UN_USUARIO||'''';
        MI_CONDICION := 'COMPANIA               = '''||UN_COMPANIA||'''
                         AND ORDENDESUMINISTRO  = '''||UN_ORDENDESUMINISTRO||'''
                         AND CODIGO             = '||UN_CODIGO||'';                   
        BEGIN
          BEGIN
            MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                         UN_ACCION    =>  'M', 
                                         UN_CAMPOS    =>  MI_CAMPOS,
                                         UN_CONDICION =>  MI_CONDICION);                 
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
          END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
              PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD   => SQLCODE,
                UN_ERROR_COD => PCK_ERRORES.ERRR_CONTRATOS_ACTCANTENTREGA
              );
        END;
        IF MI_RTA > 0 THEN

           SELECT COUNT(1) NUMERO
           INTO MI_NUMERO
           FROM D_ORDENDESUMINISTRO
           WHERE D_ORDENDESUMINISTRO.COMPANIA= UN_COMPANIA
           AND D_ORDENDESUMINISTRO.ORDENDESUMINISTRO= UN_ORDENDESUMINISTRO;

           IF MI_NUMERO <> 0 THEN 
             MI_TABLA := 'ORDENDESUMINISTRO';
             MI_CAMPOS := ' VACIA          = -1,
                            DATE_MODIFIED  = SYSDATE,
                            MODIFIED_BY    = '''||UN_USUARIO||'''';
             MI_CONDICION := 'COMPANIA               = '''||UN_COMPANIA||'''
                              AND NUMERO  = '||UN_ORDENDESUMINISTRO||'';
              BEGIN
                BEGIN
                  MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                               UN_ACCION    =>  'M', 
                                               UN_CAMPOS    =>  MI_CAMPOS,
                                               UN_CONDICION =>  MI_CONDICION);                 
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                      RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                END;
                  EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                      UN_EXC_COD   => SQLCODE,
                      UN_ERROR_COD => PCK_ERRORES.ERRR_CONTRATOS_ACTORDENSUMIN
                    );
              END;
           END IF;
        END IF;  
      END IF;
    END LOOP;
  END IF;
  MI_TABLA:='D_ORDENDECOMPRA';
  MI_CRITERIO := 'COMPANIA          = ''' ||  UN_COMPANIA     ||''' 
                  AND CLASEORDEN    = ''' ||  UN_TIPOAFECTADO ||''' 
                  AND ORDENDECOMPRA = '   ||  UN_NUMEROAFECTADO;
  MI_CONSECUTIVO := PCK_SYSMAN_UTL.FC_GENCONSECUTIVO( UN_TABLA    => MI_TABLA,
                                                      UN_CRITERIO => MI_CRITERIO,
                                                      UN_CAMPO    => 'CODIGO');


  MI_CAMPOS := 'COMPANIA, CLASEORDEN, 
                ORDENDECOMPRA , CODIGO, 
                ORDENDESUMINISTRO, DEPENDENCIA, 
                ELEMENTO, ESPECIFICACION, 
                CENTRODECOSTO, CANTIDAD, 
                SALDOCANT, VALORUNITARIO, 
                PORCIVA, PORCDESC, 
                VLRTOTAL, MARCA, 
                VALORUNITARIODI, VLRIVA, 
                VLRDESCUENTO, VALORENTREGADO, 
                CANTENTREGADA, SERIE, 
                SERIEDEVOLUTIVO, ESTADO, 
                IMPRESO, RUBROCOMPRAS, 
                FECHAADQUISICION, FECHASALIDASERVICIO, 
                FECHABODEGA, ANOPPTO, 
                TIPOPPTO, NUMEROPPTO, 
                CONSECUTIVOPPTO, RUBROPPTO, 
                VALORPPTO, BIENSERVICIO, 
                NUMEROAFECTADO, TIPOAFECTADO, 
                ITEMAFECTADO, CANTIDADMODIFICADO, 
                VALORUNITARIOMODIFICADO, VALORTOTALMODIFICADO, 
                NUMEROORIGEN, TIPOORIGEN, 
                ITEMORIGEN,DATE_CREATED,
                CREATED_BY';

  MI_VALORES := 'SELECT D_ORDENDECOMPRA.COMPANIA,
                 '''||  UN_TIPOAFECTADO   ||''', 
                 '  ||  UN_NUMEROAFECTADO ||',
                 '  ||  MI_CONSECUTIVO    ||',
                 D_ORDENDECOMPRA.ORDENDESUMINISTRO,
                 D_ORDENDECOMPRA.DEPENDENCIA,
                 D_ORDENDECOMPRA.ELEMENTO,
                 D_ORDENDECOMPRA.ESPECIFICACION,
                 D_ORDENDECOMPRA.CENTRODECOSTO, 
                 0 CANTIDAD, 
                 0 SALDOCANT, 
                 0 VALORUNITARIO, 
                 0 PORCIVA, 
                 0 PORCDESC, 
                 0 VLRTOTAL, 
                 D_ORDENDECOMPRA.MARCA,
                 D_ORDENDECOMPRA.VALORUNITARIODI, 
                 D_ORDENDECOMPRA.VLRIVA,
                 D_ORDENDECOMPRA.VLRDESCUENTO,
                 D_ORDENDECOMPRA.VALORENTREGADO,
                 D_ORDENDECOMPRA.CANTENTREGADA, 
                 D_ORDENDECOMPRA.SERIE, 
                 D_ORDENDECOMPRA.SERIEDEVOLUTIVO,
                 D_ORDENDECOMPRA.ESTADO, 
                 D_ORDENDECOMPRA.IMPRESO, 
                 D_ORDENDECOMPRA.RUBROCOMPRAS, 
                 D_ORDENDECOMPRA.FECHAADQUISICION, 
                 D_ORDENDECOMPRA.FECHASALIDASERVICIO, 
                 D_ORDENDECOMPRA.FECHABODEGA, 
                 D_ORDENDECOMPRA.ANOPPTO, 
                 D_ORDENDECOMPRA.TIPOPPTO,
                 D_ORDENDECOMPRA.NUMEROPPTO,
                 D_ORDENDECOMPRA.CONSECUTIVOPPTO,
                 D_ORDENDECOMPRA.RUBROPPTO,
                 D_ORDENDECOMPRA.VALORPPTO ,
                 D_ORDENDECOMPRA.BIENSERVICIO, 
                 ORDENDECOMPRA,
                 CLASEORDEN, 
                 CODIGO,
                 CANTIDAD, 
                 CASE WHEN NVL(CANTIDAD,0)=0 THEN 0 ELSE VLRTOTAL/CANTIDAD END VALORUNITARIOS,
                 VLRTOTAL,
                 '  ||  UN_ORDENDECOMPRA  ||',
                 '''||  UN_CLASEORDEN     ||''', 
                 '  ||  UN_CODIGOORDEN    ||',
                 SYSDATE,
                 '''|| UN_USUARIO || '''
              FROM D_ORDENDECOMPRA 
              WHERE D_ORDENDECOMPRA.COMPANIA      =  '''||  UN_COMPANIA       ||'''  
                AND D_ORDENDECOMPRA.CLASEORDEN    =  '''||  UN_CLASEORDEN     ||'''
                AND D_ORDENDECOMPRA.ORDENDECOMPRA =  '  ||  UN_ORDENDECOMPRA  ||'
                AND D_ORDENDECOMPRA.CODIGO        =  '  ||  UN_CODIGOORDEN    ||'';
  BEGIN
    BEGIN
      PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    =>  MI_TABLA,
                                              UN_ACCION   =>  'IS', 
                                              UN_CAMPOS   =>  MI_CAMPOS, 
                                              UN_VALORES  =>  MI_VALORES);
    EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                          RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
    END;
       EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
                        PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERRR_CONTRATOS_INSORDENCOMP
                      );
  END;

  BEGIN 
    SELECT  NVL(SUM(VALORUNITARIO*CANTIDAD),0) SUBTOTAL, 
            NVL(SUM(VLRIVA),0) VLRIVAF,
            NVL(SUM(VLRDESCUENTO),0) VLRDESCUENTOF
    INTO    MI_SUBTOTAL,
            MI_VALORIVA,
            MI_VALORDESCUENTO
    FROM D_ORDENDECOMPRA 
    WHERE D_ORDENDECOMPRA.COMPANIA      = UN_COMPANIA
     AND  D_ORDENDECOMPRA.CLASEORDEN    = UN_CLASEORDENANT 
     AND  D_ORDENDECOMPRA.ORDENDECOMPRA = UN_NUMEROORDENANT
     GROUP BY D_ORDENDECOMPRA.ORDENDECOMPRA, 
              D_ORDENDECOMPRA.CLASEORDEN;
    EXCEPTION WHEN NO_DATA_FOUND THEN
    MI_VALORTOTAL:=0;
  END;

  IF MI_VALORTOTAL IS NULL THEN
    MI_VALORTOTAL := MI_SUBTOTAL-MI_VALORDESCUENTO+MI_VALORIVA;
  END IF;

  MI_TABLA := 'ORDENDECOMPRA';
  MI_CAMPOS :=    'VALORTOTAL = '|| MI_VALORTOTAL ||',
                   DATE_MODIFIED = SYSDATE,
                   MODIFIED_BY = '''||UN_USUARIO||'''';
  MI_CONDICION := 'ORDENDECOMPRA.COMPANIA      = '''||  UN_COMPANIA      ||'''
                  AND ORDENDECOMPRA.CLASEORDEN = '''||  UN_CLASEORDENANT ||'''
                  AND ORDENDECOMPRA.NUMERO     = '  ||  UN_NUMEROORDENANT;
  BEGIN
    BEGIN
      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                   UN_ACCION    =>  'M', 
                                   UN_CAMPOS    =>  MI_CAMPOS,
                                   UN_CONDICION =>  MI_CONDICION);                 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
    END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERRR_CONTRATOS_ACTVALORTOTAL
        );
  END;

    SELECT NVL(SUM(VALORTOTAL),0) VALORTOTALF
    INTO   MI_VLRTOTAL
    FROM( SELECT ORDENDECOMPRA.NUMERO,
          NOVEDADCONTRATO.VALORTOTAL
          FROM (ORDENDECOMPRA 
            LEFT JOIN (NOVEDADCONTRATO 
              LEFT JOIN CLASETRANSACCIONC 
                ON NOVEDADCONTRATO.TIPOT = CLASETRANSACCIONC.TIPOT 
                AND NOVEDADCONTRATO.CLASET = CLASETRANSACCIONC.CLASET) 
              ON ORDENDECOMPRA.COMPANIA = NOVEDADCONTRATO.COMPANIA 
              AND ORDENDECOMPRA.CLASEORDEN = NOVEDADCONTRATO.CLASEORDEN 
              AND ORDENDECOMPRA.NUMERO = NOVEDADCONTRATO.ORDENDECOMPRA) 
            LEFT JOIN TIPOORDENDECOMPRA 
              ON ORDENDECOMPRA.COMPANIA = TIPOORDENDECOMPRA.COMPANIA
              AND ORDENDECOMPRA.CLASEORDEN = TIPOORDENDECOMPRA.CODIGO
          WHERE ORDENDECOMPRA.COMPANIA          = UN_COMPANIA 
            AND TIPOORDENDECOMPRA.CLASE         <> 'M' 
            AND CLASETRANSACCIONC.CLASENOVEDAD  = 'D' 
            AND ORDENDECOMPRA.CLASEORDEN        = UN_TIPOAFECTADO
            AND ORDENDECOMPRA.NUMERO            = UN_NUMEROAFECTADO  
          UNION 
          SELECT ORDENDECOMPRA_1.NUMERO, ORDENDECOMPRA_1.VALORTOTAL
          FROM ((TIPOORDENDECOMPRA 
              INNER JOIN ORDENDECOMPRA 
                ON TIPOORDENDECOMPRA.COMPANIA = ORDENDECOMPRA.COMPANIA
                AND TIPOORDENDECOMPRA.CODIGO = ORDENDECOMPRA.CLASEORDEN) 
              INNER JOIN TERCERO 
                ON ORDENDECOMPRA.COMPANIA = TERCERO.COMPANIA 
                AND ORDENDECOMPRA.TERCERO = TERCERO.NIT) 
            INNER JOIN ORDENDECOMPRA ORDENDECOMPRA_1 
              ON ORDENDECOMPRA.NUMEROAFECTADO = ORDENDECOMPRA_1.NUMERO
              AND ORDENDECOMPRA.TIPOAFECTADO = ORDENDECOMPRA_1.CLASEORDEN 
              AND ORDENDECOMPRA.COMPANIA = ORDENDECOMPRA_1.COMPANIA
          WHERE TIPOORDENDECOMPRA.COMPANIA  = UN_COMPANIA 
            AND TIPOORDENDECOMPRA.CLASE     = 'M' 
            AND ORDENDECOMPRA.CLASENOVEDAD  = 'D' 
            AND ORDENDECOMPRA_1.CLASEORDEN  = UN_TIPOAFECTADO
            AND ORDENDECOMPRA_1.NUMERO      = UN_NUMEROAFECTADO);


  IF MI_VLRTOTAL IS NOT NULL THEN
     MI_TABLA := 'ORDENDECOMPRA';
     MI_CAMPOS :=    'VALORTOTAL = '|| MI_VLRTOTAL ||',
                       DATE_MODIFIED = SYSDATE,
                       MODIFIED_BY = '''||UN_USUARIO||'''';
     MI_CONDICION := 'ORDENDECOMPRA.COMPANIA      = '''||  UN_COMPANIA      ||'''
                      AND ORDENDECOMPRA.CLASEORDEN = '''||  UN_TIPOAFECTADO ||'''
                      AND ORDENDECOMPRA.NUMERO     = '  ||  UN_NUMEROAFECTADO;
     BEGIN
       BEGIN
         MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                       UN_ACCION    =>  'M', 
                                       UN_CAMPOS    =>  MI_CAMPOS,
                                       UN_CONDICION =>  MI_CONDICION);                 
         EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
              RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
       END;
          EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
            PCK_ERR_MSG.RAISE_WITH_MSG(
              UN_EXC_COD   => SQLCODE,
              UN_ERROR_COD => PCK_ERRORES.ERRR_CONTRATOS_ACTVALORTOTAL
            );
     END;
  END IF; 
  RETURN MI_VALORTOTAL;
END FC_ACTUALIZAR_PSUBCONTRATO;

--7
PROCEDURE PR_MODCANTIDAD_CONTRATO
/*
    NAME              : PR_MODCANTIDAD_CONTRATO
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
    DATE MIGRADOR     : 16/08/2017
    TIME              : 10:15 AM  
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : 
    PARAMETERS        : UN_COMPANIA       => COMPANIA EN LA QUE SE ESTÁ TRABAJANDO.
                        UN_CLASEORDEN     => PARAMETRO PARA FILTRAR LA TABLA D_ORDENDECOMPRA. 
                        UN_ORDENDECOMPRA  => PARAMETRO PARA FILTRAR LA TABLA D_ORDENDECOMPRA.  
                        UN_CODIGO         => PARAMETRO PARA FILTRAR LA TABLA D_ORDENDECOMPRA.
                        UN_CAMPO          => NOMBRE DEL CAMPO A ACTUALIZAR. 
                        UN_VALORANTERIOR  => CANTIDAD QUE SERA RESTADA DEL CAMPO A ACTUALIZAR.
                        UN_VALORAFECTAR   => CANTIDAD QUE SE SUMARA AL CAMPO A ACTUALIZAR.
                        UN_USUARIO        => PARAMETRO QUE DETERMINA EL USUARIO AL INSERTAR O MODIFICAR. 
    @NAME:    modificarCantidadContrato 
    @METHOD:  PUT
  */
(
  UN_COMPANIA             IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CLASEORDEN           IN D_ORDENDECOMPRA.CLASEORDEN%TYPE,
  UN_ORDENDECOMPRA        IN D_ORDENDECOMPRA.ORDENDECOMPRA%TYPE,
  UN_CODIGO               IN D_ORDENDECOMPRA.CODIGO%TYPE,
  UN_CAMPO                IN PCK_SUBTIPOS.TI_CAMPOS,
  UN_VALORANTERIOR        IN D_ORDENDECOMPRA.VALORTOTALMODIFICADO%TYPE,
  UN_VALORAFECTAR         IN D_ORDENDECOMPRA.VALORTOTALMODIFICADO%TYPE,
  UN_USUARIO              IN PCK_SUBTIPOS.TI_USUARIO
)
AS
  MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
  MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
  MI_RTA                PCK_SUBTIPOS.TI_RTA_ACME;
  MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
  MI_TABLA := 'D_ORDENDECOMPRA';
  MI_CAMPOS := UN_CAMPO || '= '||UN_CAMPO||'-'||UN_VALORANTERIOR||'+'||UN_VALORAFECTAR||',
                  DATE_MODIFIED = SYSDATE,
                  MODIFIED_BY = '''||UN_USUARIO||'''';
  MI_CONDICION := 'D_ORDENDECOMPRA.COMPANIA      = '''||UN_COMPANIA||'''
                   AND D_ORDENDECOMPRA.CLASEORDEN= '''||UN_CLASEORDEN||'''
                   AND D_ORDENDECOMPRA.ORDENDECOMPRA= '||UN_ORDENDECOMPRA||'
                   AND D_ORDENDECOMPRA.CODIGO ='||UN_CODIGO||'';
  BEGIN
    BEGIN
      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                   UN_ACCION    =>  'M', 
                                   UN_CAMPOS    =>  MI_CAMPOS,
                                   UN_CONDICION =>  MI_CONDICION);                 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
    END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
      MI_MSGERROR (1).CLAVE := 'VALOR';
      IF INSTR(UN_CAMPO,'VALOR') <> 0 THEN
        MI_MSGERROR (1).VALOR := 'Valor Total';
      ELSE
        MI_MSGERROR (1).VALOR := 'Cantidad';
      END IF;

        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERRR_CONTRATOS_ACTVALORORDEN,
          UN_REEMPLAZOS => MI_MSGERROR
        );
  END;


  MI_CAMPOS :=  'VALORUNITARIOMODIFICADO = VALORTOTALMODIFICADO/CANTIDADMODIFICADO,
                  DATE_MODIFIED = SYSDATE,
                  MODIFIED_BY = '''||UN_USUARIO||'''';
  BEGIN
    BEGIN
      MI_RTA := PCK_DATOS.FC_ACME (UN_TABLA     =>  MI_TABLA, 
                                   UN_ACCION    =>  'M', 
                                   UN_CAMPOS    =>  MI_CAMPOS,
                                   UN_CONDICION =>  MI_CONDICION);                 
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
          RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
    END;
      EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
        PCK_ERR_MSG.RAISE_WITH_MSG(
          UN_EXC_COD   => SQLCODE,
          UN_ERROR_COD => PCK_ERRORES.ERRR_CONTRATOS_ACTUNIVALORDEN
        );
  END;
END PR_MODCANTIDAD_CONTRATO;

--8
 PROCEDURE PR_INSERTARRUBROSPPTO
(

	/*
      NAME              : PR_CONFIRMARTRANSACCION 
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 18/08/2017
      TIME              : 4:55 PM
      SOURCE MODULE     : 
      DESCRIPTION       : Se pasa logica del Controlador Adicionespcontratos
                          Panel Principal\Control de Contratos\Procesos\Registro de novedades\Modificaciones a contratos
      MODIFIER          :
      DATE MODIFIED     :
      TIME MODIFIED     :
      MODIFICATIONS     :
      PARAMETERS        : 	UN_COMPANIA 		    =>  Compañia de ingreso a la aplicación
                            UN_NUMERO        	  	=> Numero seleccionado en el formulario 
                            UN_CLASE 				=> Codigo seleccionado en el combo Tipo de Modificacion del formulario que abre el Modificaciones a contratos
                            UN_TIPOPPTO 			=> Codigo seleccionado en el combo Clase presupuestal
                            UN_USUARIO          	=> Usuario por la cual se loggeo en la aplicacion
							UN_CONTADOR 			=> Se utiliza para validar si trae uno o más registros seleccionados en la lista.
                            UN_LISTACOMP          	=> Lista con las llaves de los registros seleccionado.
                            UN_TERCERO       		=> Tercero del registro
							UN_SUCURSAL				=> Sucursal del Tercero

    @NAME:  insertarRubrosOrdenPpto
    @METHOD:  GET
    */
  UN_COMPANIA     	  IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CLASE        	  IN ORDENDECOMPRAPPTO.CLASEORDEN%TYPE,
	UN_NUMERO       	  IN ORDENDECOMPRA.NUMERO%TYPE,
	UN_TIPOPPTO     	  IN ORDENDECOMPRAPPTO.TIPOPPTO%TYPE,
  UN_NUMEROPPTO    	  IN ORDENDECOMPRAPPTO.NUMEROPPTO%TYPE  ,
  UN_FECHASELEC       IN VARCHAR2,
	UN_USUARIO      	  IN PCK_SUBTIPOS.TI_USUARIO,
	UN_CONTADOR     	  IN PCK_SUBTIPOS.TI_ENTERO,
	UN_TERCERO          IN PCK_SUBTIPOS.TI_TERCERO,
	UN_SUCURSAL         IN PCK_SUBTIPOS.TI_SUCURSAL

)

	AS
		MI_TABLA   		      PCK_SUBTIPOS.TI_TABLA;
		MI_VALORES 		      PCK_SUBTIPOS.TI_VALORES;
		MI_CAMPOS  		      PCK_SUBTIPOS.TI_CAMPOS;
		MI_CONDICION 	      PCK_SUBTIPOS.TI_CONDICION;
    RS                  SYS_REFCURSOR;

	BEGIN
		IF UN_CONTADOR = 1 
		THEN
        <<RECORRECPTES>>
         FOR MI_RS IN (SELECT  
                          TIPO_CPTE,
                          COMPROBANTE,
                          CONSECUTIVO,
                          CUENTA,
                          VALORPPTO
                        FROM(
                          SELECT  
                            DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE ,
                            DETALLE_COMPROBANTE_PPTAL.CUENTA,
                            DETALLE_COMPROBANTE_PPTAL.COMPROBANTE,
                            (VALOR_DEBITO-VALOR_CREDITO)-(DEBITO_AFECTADO-CREDITO_AFECTADO)+(MODIFICACION_DEBITO-MODIFICACION_CREDITO) AS VALORPPTO,
                            DETALLE_COMPROBANTE_PPTAL.CONSECUTIVO  
                          FROM DETALLE_COMPROBANTE_PPTAL
                          WHERE COMPANIA 	=	UN_COMPANIA
                            AND ANO 	= EXTRACT(YEAR FROM SYSDATE)
                            AND TIPO_CPTE = UN_TIPOPPTO
                            AND COMPROBANTE = UN_NUMEROPPTO))
        LOOP
          MI_CAMPOS := 'D_ORDENDECOMPRA.TIPOPPTO        = '''||MI_RS.TIPO_CPTE||''',
                        D_ORDENDECOMPRA.NUMEROPPTO      = '||MI_RS.COMPROBANTE||',
                        D_ORDENDECOMPRA.CONSECUTIVOPPTO = '||MI_RS.CONSECUTIVO||',
                        D_ORDENDECOMPRA.RUBROPPTO       = '''||MI_RS.CUENTA||''',
                        D_ORDENDECOMPRA.VALORPPTO       = '||MI_RS.VALORPPTO||',
                        D_ORDENDECOMPRA.ANOPPTO         = EXTRACT(YEAR FROM SYSDATE),
                        DATE_MODIFIED = SYSDATE ,
                        MODIFIED_BY                           = '''||UN_USUARIO||'''';
        MI_CONDICION := '     D_ORDENDECOMPRA.COMPANIA        = '''||UN_COMPANIA||'''  
                          AND D_ORDENDECOMPRA.CLASEORDEN      = '''||UN_CLASE||'''
                          AND  D_ORDENDECOMPRA.ORDENDECOMPRA  = '||UN_NUMERO||' 
                          AND  D_ORDENDECOMPRA.TIPOPPTO IS NULL';	          
      	BEGIN 
					BEGIN 

						PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
												UN_TABLA     	=> 'D_ORDENDECOMPRA',
												UN_ACCION    	=> 'M',
												UN_CAMPOS 		=> MI_CAMPOS,
												UN_CONDICION 	=> MI_CONDICION); 
					EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
						RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
					END;	
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERRR_CONTRATOS_ACTPPTO
                      );
				END;	
     END LOOP RECORRECPTES;   
		ELSE
     PCK_CONTRATOS_COM1.PR_INSERTAPPTO(UN_COMPANIA      =>  UN_COMPANIA,
                                       UN_CLASEORDEN    =>  UN_CLASE,
                                       UN_NUMERO        =>  UN_NUMERO,
                                       UN_CLASEDISP     =>  UN_TIPOPPTO, 
                                       UN_NUMERODISPSEL =>  UN_NUMEROPPTO,
                                       UN_FECHASELEC    =>  UN_FECHASELEC,
                                       UN_TERCERO       =>  UN_TERCERO,
                                       UN_SUCURSAL      =>  UN_SUCURSAL,
                                       UN_USUARIO       =>  UN_USUARIO );
    END IF;	


END PR_INSERTARRUBROSPPTO;		

PROCEDURE PR_CAMBIARCONCADI
(
/*
      NAME              : PR_CAMBIARCONCADI 
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : YESIKA PAOLA BECERRA CASTRO
      DATE MIGRADOR     : 27/11/2017
      TIME              : 3:21 PM
      SOURCE MODULE     : 
      DESCRIPTION       : Al darle clic en el boton cambiar del formulario modificaciones a contratos, actualiza el numero de la tabla orden de compra
      MODIFIER          :
      DATE MODIFIED     :
      TIME MODIFIED     :
      MODIFICATIONS     :
      PARAMETERS        : 	UN_COMPANIA 		  =>  Compañia de ingreso a la aplicación
                            UN_NUMERO        	=> Numero seleccionado en el formulario 
                            UN_CLASE 				  => Codigo seleccionado en el combo Tipo de Modificacion del formulario que abre el Modificaciones a contratos
                            UN_NUEVOCON 			=> nuevo consecutivo de la adicion
                            UN_USUARIO        => Usuario por la cual se loggeo en la aplicacion

    @NAME:  cambiarNumeroAdicionContrato
    @METHOD:  PUT
    */
	UN_COMPANIA 		IN PCK_SUBTIPOS.TI_COMPANIA,
	UN_NUEVOCON         IN PCK_SUBTIPOS.TI_ENTERO_LARGO,
	UN_NUMERO 			IN ORDENDECOMPRA.NUMERO%TYPE,
	UN_CLASEORDEN 		IN ORDENDECOMPRA.CLASEORDEN%TYPE,
	UN_USUARIO          IN PCK_SUBTIPOS.TI_USUARIO
)
AS
	MI_STRSQL 		PCK_SUBTIPOS.TI_STRSQL;
	MI_NUMERO     	ORDENDECOMPRA.NUMERO%TYPE;
	MI_CAMPOS       PCK_SUBTIPOS.TI_CAMPOS;
	MI_CONDICION    PCK_SUBTIPOS.TI_CONDICION;
	MI_TABLA        PCK_SUBTIPOS.TI_TABLA;

BEGIN
	BEGIN
		MI_STRSQL := '	SELECT NUMERO 
						FROM ORDENDECOMPRA 
						WHERE COMPANIA 		= '''||UN_COMPANIA||'''
							AND CLASEORDEN 	= '''||UN_CLASEORDEN||'''
							AND NUMERO 		= '''||UN_NUEVOCON||'''';

		EXECUTE IMMEDIATE MI_STRSQL INTO MI_NUMERO;
		EXCEPTION WHEN NO_DATA_FOUND THEN	
			BEGIN 
				BEGIN 
					MI_TABLA := 'ORDENDECOMPRA';
					MI_CAMPOS := 'NUMERO = '''||UN_NUEVOCON||''', MODIFIED_BY = '''||UN_USUARIO||''' , DATE_MODIFIED = SYSDATE';
					MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND CLASEORDEN = '''||UN_CLASEORDEN||''' AND NUMERO = '''||UN_NUMERO||'''';

				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA     => MI_TABLA,
														UN_ACCION    => 'M',
														UN_CAMPOS    => MI_CAMPOS,
														UN_CONDICION => MI_CONDICION);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
					RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS; 
				END;	
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
       		PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_CAMBIARCONADICION
                                ,UN_TABLAERROR => MI_TABLA);	

			END;
			BEGIN 
				BEGIN 
					MI_TABLA := 'ORDENDECOMPRAPPTO';
					MI_CAMPOS := 'NUMERO = '''||UN_NUEVOCON||''', MODIFIED_BY = '''||UN_USUARIO||''' , DATE_MODIFIED = SYSDATE';
					MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND CLASEORDEN = '''||UN_CLASEORDEN||''' AND NUMERO = '''||UN_NUMERO||'''';

				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA     => MI_TABLA,
														UN_ACCION    => 'M',
														UN_CAMPOS    => MI_CAMPOS,
														UN_CONDICION => MI_CONDICION);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
					RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS; 
				END;	
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
       		PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_CAMBIARCONADICION
                                ,UN_TABLAERROR => MI_TABLA);	

			END;	
			BEGIN 
				BEGIN 
					MI_TABLA := 'D_ORDENDECOMPRA';
					MI_CAMPOS := 'ORDENDECOMPRA = '''||UN_NUEVOCON||''', MODIFIED_BY = '''||UN_USUARIO||''' , DATE_MODIFIED = SYSDATE';
					MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND CLASEORDEN = '''||UN_CLASEORDEN||''' AND ORDENDECOMPRA = '''||UN_NUMERO||'''';

				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA     => MI_TABLA,
														UN_ACCION    => 'M',
														UN_CAMPOS    => MI_CAMPOS,
														UN_CONDICION => MI_CONDICION);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
					RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS; 
				END;	
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
       		PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_CAMBIARCONADICION
                                ,UN_TABLAERROR => MI_TABLA);	

			END;	
			BEGIN 
				BEGIN 
					MI_TABLA := 'POLIZAS';
					MI_CAMPOS := 'ORDENDECOMPRA = '''||UN_NUEVOCON||''', MODIFIED_BY = '''||UN_USUARIO||''' , DATE_MODIFIED = SYSDATE';
					MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND CLASEORDEN = '''||UN_CLASEORDEN||''' AND ORDENDECOMPRA = '''||UN_NUMERO||'''';

				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA     => MI_TABLA,
														UN_ACCION    => 'M',
														UN_CAMPOS    => MI_CAMPOS,
														UN_CONDICION => MI_CONDICION);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
					RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS; 
				END;	
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
       		PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_CAMBIARCONADICION
                                ,UN_TABLAERROR => MI_TABLA);	

			END;
			BEGIN 
				BEGIN 
					MI_TABLA := 'MULTAS_SANCIONES';
					MI_CAMPOS := 'NUMERO = '''||UN_NUEVOCON||''', MODIFIED_BY = '''||UN_USUARIO||''' , DATE_MODIFIED = SYSDATE';
					MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND CLASEORDEN = '''||UN_CLASEORDEN||''' AND NUMERO = '''||UN_NUMERO||'''';

				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA     => MI_TABLA,
														UN_ACCION    => 'M',
														UN_CAMPOS    => MI_CAMPOS,
														UN_CONDICION => MI_CONDICION);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
					RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS; 
				END;	
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
       		PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_CAMBIARCONADICION
                                ,UN_TABLAERROR => MI_TABLA);	

			END;
			BEGIN 
				BEGIN 
					MI_TABLA := 'NOVEDADCONTRATO';
					MI_CAMPOS := 'ORDENDECOMPRA = '''||UN_NUEVOCON||''', MODIFIED_BY = '''||UN_USUARIO||''' , DATE_MODIFIED = SYSDATE';
					MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND CLASEORDEN = '''||UN_CLASEORDEN||''' AND ORDENDECOMPRA = '''||UN_NUMERO||'''';

				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA     => MI_TABLA,
														UN_ACCION    => 'M',
														UN_CAMPOS    => MI_CAMPOS,
														UN_CONDICION => MI_CONDICION);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
					RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS; 
				END;	
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
       		PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_CAMBIARCONADICION
                                ,UN_TABLAERROR => MI_TABLA);	

			END;
			BEGIN 
				BEGIN 
					MI_TABLA := 'ORDENDECOMPRA_AUXILIAR';
					MI_CAMPOS := 'ORDENDECOMPRA = '''||UN_NUEVOCON||''', MODIFIED_BY = '''||UN_USUARIO||''' , DATE_MODIFIED = SYSDATE';
					MI_CONDICION := 'COMPANIA = '''||UN_COMPANIA||''' AND CLASEORDEN = '''||UN_CLASEORDEN||''' AND ORDENDECOMPRA = '''||UN_NUMERO||'''';

				PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(	UN_TABLA     => MI_TABLA,
														UN_ACCION    => 'M',
														UN_CAMPOS    => MI_CAMPOS,
														UN_CONDICION => MI_CONDICION);
				EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN 
					RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS; 
				END;	
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
       		PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                ,UN_ERROR_COD  => PCK_ERRORES.ERR_CAMBIARCONADICION
                                ,UN_TABLAERROR => MI_TABLA);	

			END;
		END;
		IF	MI_NUMERO IS NOT NULL THEN
			BEGIN 
				RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
			EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN 
            PCK_ERR_MSG.RAISE_WITH_MSG(
                    UN_EXC_COD    => SQLCODE
                   ,UN_TABLAERROR => 'ORDENDECOMPRA'
                   ,UN_ERROR_COD  => PCK_ERRORES.ERR_CAMBIARNUMADICION
                    );
			END;	
		END IF;
END PR_CAMBIARCONCADI	;	

PROCEDURE PR_ACTUALIZARCUMPLIMIENTOACT
/*
    NAME              : PR_ACTUALIZARCUMPLIMIENTOACT
    AUTHORS           : SYSMAN  SAS
    AUTHOR MIGRACION  : JONATHAN LEONARDO MALAVER JIMÉNEZ
    DATE MIGRADOR     : 28/09/2018
    TIME              : 12:00 PM  
    SOURCE MODULE     : 
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              : 
    MODIFICATIONS     : 
    DESCRIPTION       : Permite actualizar las actividades de las actas

      @NAME:    actualizarCumplimientoActividades
      @METHOD:  POST
  */
    (
      UN_COMPANIA       IN PCK_SUBTIPOS.TI_COMPANIA,
      UN_COD_CONTRATO   IN DETALLE_ACTIVIDADES.COD_CONTRATO%TYPE,
      UN_TIPO_CONTRATO  IN ACTIVIDADES_NOVEDAD.TIPO_CONTRATO%TYPE,
      UN_CODIGO_NOVEDAD IN ACTIVIDADES_NOVEDAD.CODIGO_NOVEDAD%TYPE,
      UN_TIPO_NOVEDAD   IN ACTIVIDADES_NOVEDAD.TIPO_NOVEDAD%TYPE,
      UN_CODIGO_ACTA    IN ACTIVIDADES_NOVEDAD.CODIGO_ACTA%TYPE,
      UN_USUARIO        IN PCK_SUBTIPOS.TI_USUARIO
    )     
  AS 
      MI_CAMPOS                     PCK_SUBTIPOS.TI_CAMPOS;
      MI_VALORES                    PCK_SUBTIPOS.TI_VALORES;
      MI_EXISTE                     PCK_SUBTIPOS.TI_ENTERO;

    BEGIN

   FOR RS IN 
    (
        SELECT  CODIGO, 
                COD_CONTRATO,
                TIPO_CONTRATO
        FROM  DETALLE_ACTIVIDADES
        WHERE COMPANIA      = UN_COMPANIA
        AND   COD_CONTRATO  = UN_COD_CONTRATO
        AND   TIPO_CONTRATO = UN_TIPO_CONTRATO  
    )
    LOOP
      SELECT COUNT (1)
      INTO MI_EXISTE
      FROM ACTIVIDADES_NOVEDAD
      WHERE COMPANIA        = UN_COMPANIA
      AND CODIGO_ACTIVIDAD  = RS.CODIGO
      AND COD_CONTRATO      = RS.COD_CONTRATO
      AND TIPO_CONTRATO     = RS.TIPO_CONTRATO
      AND CODIGO_ACTA       = UN_CODIGO_ACTA
      AND CODIGO_NOVEDAD    = UN_CODIGO_NOVEDAD 
      AND TIPO_NOVEDAD      = UN_TIPO_NOVEDAD;

      IF MI_EXISTE       = 0 THEN

        MI_CAMPOS      :=   ' COMPANIA, 
                              CODIGO_ACTIVIDAD,
                              CODIGO_ACTA,
                              CODIGO_NOVEDAD,  
                              TIPO_NOVEDAD,  
                              COD_CONTRATO,
                              TIPO_CONTRATO,
                              PORCENTAJE_CUMPLIMIENTO,
                              PORCENTAJE_ACUMULADO,
                              DATE_CREATED,
                              CREATED_BY';

        MI_VALORES     :=''''||UN_COMPANIA||''',                                          
                            '||RS.CODIGO||',
                            '||UN_CODIGO_ACTA||',
                            '||UN_CODIGO_NOVEDAD||', 
                            '''||UN_TIPO_NOVEDAD||''',
                            '||UN_COD_CONTRATO||',                                          
                            '''||UN_TIPO_CONTRATO||''',                                          
                            0,                                          
                            0,                   
                            SYSDATE,                                          
                            ''' || UN_USUARIO ||'''' ; 

      BEGIN     
       BEGIN
              PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME ( UN_TABLA    => 'ACTIVIDADES_NOVEDAD', 
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
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_TRANSFERIRDETACTIVIDADES
                                        );
       END;   
      END IF; 

    END LOOP; 

END PR_ACTUALIZARCUMPLIMIENTOACT;							
--11
PROCEDURE PR_CALCVALTOTCONTRATO
(
   /*
      NAME              : PR_CALCVALTOTCONTRATO 
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : JOSE CAMILO HENAO
      DATE MIGRADOR     : 19/11/2021
      TIME              : 14:15 PM
      SOURCE MODULE     : 
      DESCRIPTION       : Se pasa logica del Controlador Adicionespcontratos
                          CONTROL CONTRATOS/PROCESOS/REGISTRO DE NOVEDADES/OTRAS/Boton Aceptar/Boton Novedades
      MODIFIER          :
      DATE MODIFIED     :
      TIME MODIFIED     :
      MODIFICATIONS     :
      PARAMETERS        : 	UN_COMPANIA 		    =>  CompaÃ±ia de ingreso a la aplicaciÃ³n
                            UN_NUMERO        	  => Numero seleccionado en el formulario 
                            UN_TIPOCONTRATO 		=> Codigo seleccionado en el combo Tipo de Modificacion del formulario que abre el Modificaciones a contratos


    @NAME:  registrarCesion
    @METHOD:  PUT
    */
    UN_COMPANIA       	IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_STRTIPOT         IN CLASETRANSACCIONC.TIPOT%TYPE,
    UN_CLASEORDEN       IN ORDENDECOMPRA.CLASEORDEN%TYPE,
    UN_NUMERO           IN ORDENDECOMPRA.NUMERO%TYPE,
    UN_NOVEDAD          IN NOVEDADCONTRATO.NOVEDAD%TYPE,
    UN_NVLPEJECUCION    IN NOVEDADCONTRATO.POREJECUCION%TYPE,
    UN_VALORTOTAL       IN NOVEDADCONTRATO.VALORTOTAL%TYPE
) 
	AS
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;  
    MI_NVLPEJECUCION    NOVEDADCONTRATO.POREJECUCION%TYPE;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_RTA              PCK_SUBTIPOS.TI_ENTERO_LARGO; 
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALORTOTAL       ORDENDECOMPRA.VALORTOTAL%TYPE;
    MI_DURACION         ORDENDECOMPRA.DURACION%TYPE;
    MI_PLAZODEENTREGA   ORDENDECOMPRA.PLAZODEENTREGA%TYPE;
    MI_VALORTOTALFORM   ORDENDECOMPRA.VALORTOTAL%TYPE:=0;
    MI_DURACIONFORM     ORDENDECOMPRA.DURACION%TYPE;
    MI_DIASCONTRATO     NOVEDADCONTRATO.DIAS_CONTRATO%TYPE;
    MI_VALOR            PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_VALORTOT         PCK_SUBTIPOS.TI_ENTERO_LARGO;

	BEGIN 
     IF UN_VALORTOTAL>0 THEN
    select A.valortotal INTO MI_VALORTOT from ORDENDECOMPRA A WHERE A.COMPANIA=UN_COMPANIA AND A.NUMERO=UN_NUMERO AND A.CLASEORDEN=UN_CLASEORDEN;
      SELECT SUM(VALORTOTAL)INTO MI_VALOR  FROM NOVEDADCONTRATO WHERE COMPANIA=UN_COMPANIA 
                    AND TIPOT=UN_STRTIPOT 
                    AND CLASEORDEN=UN_CLASEORDEN
                    AND ORDENDECOMPRA=UN_NUMERO
                    AND NOVEDAD<=UN_NOVEDAD
                    ;
    MI_NVLPEJECUCION :=ROUND(MI_VALOR/MI_VALORTOT *100,2);
    MI_CAMPOS := ' POREJECUCION = '|| MI_NVLPEJECUCION ;

		BEGIN
                BEGIN
                
                
                    MI_TABLA := 'NOVEDADCONTRATO';
                    MI_CONDICION := '  COMPANIA   = ''' || UN_COMPANIA   ||  ''''  || 
                    ' AND CLASEORDEN = ''' || UN_CLASEORDEN ||  ''''  ||
                    ' AND TIPOT = ''' || UN_STRTIPOT ||  ''''  ||
                    ' AND NOVEDAD     = ''' || UN_NOVEDAD  ||  ''''  ||
                    ' AND ORDENDECOMPRA     = ''' || UN_NUMERO     ||  '''';

                    PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME(
                                        UN_TABLA     	=> MI_TABLA,
                                        UN_ACCION    	=> 'M',
                                        UN_CAMPOS 		=> MI_CAMPOS,
                                        UN_CONDICION 	=> MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                    THEN RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                END;
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN    
              			MI_REEMPLAZOS(0).CLAVE 	:= 'TABLA';
                    MI_REEMPLAZOS(0).VALOR	:= MI_TABLA;
                    PCK_ERR_MSG.RAISE_WITH_MSG(
                        UN_EXC_COD   => SQLCODE,
                        UN_ERROR_COD => PCK_ERRORES.ERRR_ACTUALIZORDCOMP,
                        UN_REEMPLAZOS => MI_REEMPLAZOS
                      );
                END;
    END IF ;

END PR_CALCVALTOTCONTRATO;
--12
PROCEDURE PR_CALCULAR_PORC_ORDCOMPRA
(
   /*
      NAME              : PR_CALCULAR_PORC_ORDCOMPRA 
      AUTHORS           : STEFANINI SYSMAN
      AUTHOR MIGRACION  : JOSE CAMILO HENAO
      DATE MIGRADOR     : 04/18/2021
      TIME              : 17:15 PM
      SOURCE MODULE     : 
      DESCRIPTION       : Se pasa logica del Controlador Adicionespcontratos
                          CONTROL CONTRATOS/PROCESOS/REGISTRO DE NOVEDADES/OTRAS/Boton Aceptar/Boton Novedades
      MODIFIER          :
      DATE MODIFIED     :
      TIME MODIFIED     :
      MODIFICATIONS     :
      PARAMETERS        : 	UN_COMPANIA 		    =>  CompaÃ±ia de ingreso a la aplicaciÃ³n
                            UN_NUMERO        	  => Numero seleccionado en el formulario 
                            UN_TIPOCONTRATO 		=> Codigo seleccionado en el combo Tipo de Modificacion del formulario que abre el Modificaciones a contratos


    @NAME:  registrarCesion
    @METHOD:  PUT
    */
    UN_COMPANIA       	IN PCK_SUBTIPOS.TI_COMPANIA,
    UN_CLASEORDEN       IN ORDENDECOMPRA.CLASEORDEN%TYPE,
    UN_NUMERO           IN ORDENDECOMPRA.NUMERO%TYPE
   
) 
	AS
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;  
    MI_NVLPEJECUCION    NOVEDADCONTRATO.POREJECUCION%TYPE;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_RTA              PCK_SUBTIPOS.TI_ENTERO_LARGO; 
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_VALORTOTAL       ORDENDECOMPRA.VALORTOTAL%TYPE;
    MI_DURACION         ORDENDECOMPRA.DURACION%TYPE;
    MI_PLAZODEENTREGA   ORDENDECOMPRA.PLAZODEENTREGA%TYPE;
    MI_VALORTOTALFORM   ORDENDECOMPRA.VALORTOTAL%TYPE:=0;
    MI_DURACIONFORM     ORDENDECOMPRA.DURACION%TYPE;
    MI_DIASCONTRATO     NOVEDADCONTRATO.DIAS_CONTRATO%TYPE;
    MI_VALOR            PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_VALORTOT         PCK_SUBTIPOS.TI_ENTERO_LARGO;

BEGIN
     /*porcentaje de ejecucion orden de compra con respecto a las novedades*/
      
      SELECT A.VALORFINAL INTO MI_VALORTOT from ORDENDECOMPRA A WHERE --(CC:3099 Se toma el valor final no el total)
            A.COMPANIA=UN_COMPANIA 
            AND A.NUMERO=UN_NUMERO 
            AND A.CLASEORDEN=UN_CLASEORDEN;
            
            begin
                SELECT SUM(VALORTOTAL)INTO MI_VALOR  FROM NOVEDADCONTRATO WHERE 
                    COMPANIA=UN_COMPANIA 
                    AND CLASEORDEN=UN_CLASEORDEN
                    AND ORDENDECOMPRA=UN_NUMERO;
                    
                     EXCEPTION WHEN NO_DATA_FOUND THEN
                MI_VALOR:=0;
            END;

    BEGIN--(CC:3099_INI)
    IF MI_VALORTOT = 0 THEN
       RAISE PCK_EXCEPCIONES.EXC_INTERFAZ; 
    END IF;
    
    EXCEPTION 
        WHEN PCK_EXCEPCIONES.EXC_INTERFAZ  THEN
            MI_TABLA := 'ORDENDECOMPRA';
            MI_REEMPLAZOS(1).CLAVE := 'CONTRATO';
            MI_REEMPLAZOS(1).VALOR := UN_NUMERO;
            MI_REEMPLAZOS(2).CLAVE := 'TIPO';
            MI_REEMPLAZOS(2).VALOR := UN_CLASEORDEN;
            
            PCK_ERR_MSG.RAISE_WITH_MSG( UN_EXC_COD    => SQLCODE,
                                        UN_ERROR_COD  => PCK_ERRORES.ERR_CONTRATO_VALORCERO,
                                        UN_TABLAERROR => MI_TABLA,
                                        UN_REEMPLAZOS => MI_REEMPLAZOS);
                                        
                                        RETURN;
    END;--(CC:3099_FIN)                 
    MI_NVLPEJECUCION :=ROUND(MI_VALOR/MI_VALORTOT *100,2);
                
    MI_CONDICION := '     ORDENDECOMPRA.COMPANIA   = ''' || UN_COMPANIA   ||  ''''  || 
                    ' AND ORDENDECOMPRA.CLASEORDEN = ''' || UN_CLASEORDEN ||  ''''  ||
                    ' AND ORDENDECOMPRA.NUMERO     = ''' || UN_NUMERO     ||  '''';
    MI_TABLA := 'ORDENDECOMPRA';
       /* MI_NVLPEJECUCION := NVL(UN_NVLPEJECUCION,'');*/
        IF MI_NVLPEJECUCION IS NOT NULL THEN 
            MI_CAMPOS := ' PORCESTADOEJECUCION = ' || MI_NVLPEJECUCION    ;
            BEGIN
                BEGIN
                    MI_RTA := PCK_DATOS.FC_ACME(UN_TABLA     => MI_TABLA
                                               ,UN_ACCION    => 'M' 
                                               ,UN_CAMPOS    => MI_CAMPOS
                                               ,UN_CONDICION => MI_CONDICION);

                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR 
                    THEN RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
                END;
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
                MI_REEMPLAZOS(0).CLAVE := 'TABLA';
                MI_REEMPLAZOS(0).VALOR := MI_TABLA;
                PCK_ERR_MSG.RAISE_WITH_MSG(UN_EXC_COD    => SQLCODE
                                          ,UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTUALIZORDCOMP
                                          ,UN_TABLAERROR => MI_TABLA
                                          ,UN_REEMPLAZOS => MI_REEMPLAZOS);    
            END;	    
        END IF;
     
END PR_CALCULAR_PORC_ORDCOMPRA;   

PROCEDURE PR_RECALCULAR_AIU(
  UN_COMPANIA    IN PCK_SUBTIPOS.TI_COMPANIA,
  UN_CLASEORDEN  IN ORDENDECOMPRA.CLASEORDEN%TYPE,
  UN_NUMERO      IN ORDENDECOMPRA.NUMERO%TYPE,
  UN_PORC_ADMIN  IN ORDENDECOMPRA.PORADMINISTRACION%TYPE,
  UN_PORC_IMPRE  IN ORDENDECOMPRA.PORINPREVISTOS%TYPE,
  UN_PORC_UTILI  IN ORDENDECOMPRA.PORUTILIDADES%TYPE
) AS
    MI_PORC_AIU         NUMBER;
    MI_FACTOR_DESC      NUMBER;
    MI_VLRDESCUENTO     NUMBER;
    MI_VALOR_AIU        NUMBER;
    MI_VALOR_IVA        NUMBER;
    MI_VALOR_IMPCONSUMO NUMBER;
    MI_VALOR_TOTAL      NUMBER;
    MI_CONDICION        PCK_SUBTIPOS.TI_CONDICION;
    MI_CAMPOS           PCK_SUBTIPOS.TI_CAMPOS;
    MI_RTA              PCK_SUBTIPOS.TI_ENTERO_LARGO;
    MI_TABLA            PCK_SUBTIPOS.TI_TABLA;
    MI_REEMPLAZOS       PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN
    -- Porcentaje AIU total
    MI_PORC_AIU := UN_PORC_ADMIN + UN_PORC_IMPRE + UN_PORC_UTILI;

    FOR RS IN (
        SELECT COMPANIA, CLASEORDEN, ORDENDECOMPRA, CODIGO,
               VALORUNITARIO,
               CANTIDAD,
               PORCIVA,
               PORCDESC,
               IMPCONSUMO
        FROM D_ORDENDECOMPRA
        WHERE COMPANIA      = UN_COMPANIA
          AND CLASEORDEN    = UN_CLASEORDEN
          AND ORDENDECOMPRA = UN_NUMERO
    ) LOOP

        -- Factor descuento: (1 - PORCDESC/100)
        MI_FACTOR_DESC := 1 - (RS.PORCDESC / 100);

        -- Valor descuento recalculado
        -- = ValorUnitario * Cantidad * (PORCDESC/100)
        MI_VLRDESCUENTO := PCK_SYSMAN_UTL.FC_ROUND(
            RS.VALORUNITARIO * RS.CANTIDAD * (RS.PORCDESC / 100)
        , 2);

        -- Valor AIU
        -- = ValorUnitario * (1-(PORCDESC/100)) * Cantidad * (PORC_AIU/100)
        MI_VALOR_AIU := PCK_SYSMAN_UTL.FC_ROUND(
            RS.VALORUNITARIO * MI_FACTOR_DESC * RS.CANTIDAD * (MI_PORC_AIU / 100)
        , 2);

        -- IVA sobre AIU
        -- = VALOR_AIU * (PORCIVA/100)
        MI_VALOR_IVA := PCK_SYSMAN_UTL.FC_ROUND(
            (MI_VALOR_AIU * RS.PORCIVA) / 100
        , 2);

        -- Impuesto Consumo sobre AIU
        -- = VALOR_AIU * (IMPCONSUMO/100)
        MI_VALOR_IMPCONSUMO := PCK_SYSMAN_UTL.FC_ROUND(
            (MI_VALOR_AIU * RS.IMPCONSUMO) / 100
        , 2);

        -- Valor Total
        -- = (ValorUnitario * Cantidad) - Descuento + AIU + IVA + ImpConsumo
        MI_VALOR_TOTAL := (RS.VALORUNITARIO * RS.CANTIDAD)
                        - MI_VLRDESCUENTO
                        + MI_VALOR_AIU
                        + MI_VALOR_IVA
                        + MI_VALOR_IMPCONSUMO;

        MI_TABLA     := 'D_ORDENDECOMPRA';
        MI_CONDICION := 'COMPANIA      = ''' || RS.COMPANIA      || ''' '
                     || 'AND CLASEORDEN    = ''' || RS.CLASEORDEN    || ''' '
                     || 'AND ORDENDECOMPRA = ''' || RS.ORDENDECOMPRA || ''' '
                     || 'AND CODIGO        = ''' || RS.CODIGO        || ''' ';

        MI_CAMPOS := 'PORC_AIU        = '  || MI_PORC_AIU         || ', '
                  || 'VALOR_AIU       = '  || MI_VALOR_AIU        || ', '
                  || 'VLRIVA          = '  || MI_VALOR_IVA        || ', '
                  || 'VALORIMPCONSUMO = '  || MI_VALOR_IMPCONSUMO || ', '
                  || 'VLRDESCUENTO    = '  || MI_VLRDESCUENTO     || ', '
                  || 'VLRTOTAL        = '  || MI_VALOR_TOTAL;

        BEGIN
            BEGIN
                MI_RTA := PCK_DATOS.FC_ACME(
                    UN_TABLA     => MI_TABLA,
                    UN_ACCION    => 'M',
                    UN_CAMPOS    => MI_CAMPOS,
                    UN_CONDICION => MI_CONDICION
                );
            EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ACTUALIZAR THEN
                RAISE PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS;
            END;
        EXCEPTION WHEN PCK_EXCEPCIONES.EXC_CONTROLCONTRATOS THEN
            MI_REEMPLAZOS(0).CLAVE := 'TABLA';
            MI_REEMPLAZOS(0).VALOR := MI_TABLA;
            PCK_ERR_MSG.RAISE_WITH_MSG(
                UN_EXC_COD    => SQLCODE,
                UN_ERROR_COD  => PCK_ERRORES.ERRR_ACTUALIZORDCOMP,
                UN_TABLAERROR => MI_TABLA,
                UN_REEMPLAZOS => MI_REEMPLAZOS
            );
        END;

    END LOOP;
END PR_RECALCULAR_AIU;

END PCK_CONTRATOS_COM2;