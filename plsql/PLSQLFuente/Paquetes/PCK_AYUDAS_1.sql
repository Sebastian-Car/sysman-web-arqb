create or replace PACKAGE BODY PCK_AYUDAS AS

  FUNCTION FC_CARGAR_TAREAS
  
  /*
    NAME              : FC_CARGAR_TAREAS
    AUTHOR MIGRACION  : GERMAN DAVID ROJAS
    DATE MIGRADOR     : 26/06/2023                                                                                             
    TIME              :
    SOURCE MODULE     :
    MODIFIER          : 
    DATE MODIFIED     : 
    TIME              :
    MODIFICATIONS     : 
    DESCRIPTION       : FUNCION PARA CARGAR LOS DATOS DE LAS TAREAS DE LA AYUDA.

    @NAME:    cargarTarea
    @METHOD:  POST
    */  
  
( 
  UN_PROCESO     IN PCK_SUBTIPOS.TI_ENTERO,
  UN_CADENA      IN CLOB,
  UN_USUARIO     IN PCK_SUBTIPOS.TI_USUARIO
    )RETURN CLOB AS
    
    MI_DATOS_FILA         PCK_SYSMAN_UTL.T_SPLIT;
    MI_DATOS_COLUMNAS     PCK_SYSMAN_UTL.T_SPLIT;
    MI_MSGERROR           PCK_SUBTIPOS.TI_CLAVEVALOR;
    MI_TABLA              PCK_SUBTIPOS.TI_TABLA;
    MI_CAMPOS             PCK_SUBTIPOS.TI_CAMPOS;
    MI_VALORES            PCK_SUBTIPOS.TI_VALORES;
    MI_CONDICION          PCK_SUBTIPOS.TI_CONDICION;
    MI_CONTADOR           NUMBER := 0;
    MI_EXISTE             NUMBER := 0;
    MI_RETORNO            CLOB := '';
    MI_RTAACME            VARCHAR2(100 CHAR);
      
      BEGIN
            
      MI_DATOS_FILA := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        => UN_CADENA,
                                                   UN_DELIMITADOR  => PCK_DATOS.GL_SEPARADOR_REG);
                                                   
      <<CREAR_TAREAS>>
      FOR RS IN MI_DATOS_FILA.FIRST..MI_DATOS_FILA.LAST 
      LOOP
        MI_DATOS_COLUMNAS := PCK_SYSMAN_UTL.FC_SPLIT_SYS(UN_LISTA        =>  MI_DATOS_FILA(RS),
                                                          UN_DELIMITADOR  =>  PCK_DATOS.GL_SEPARADOR_COL);
                                                          
        IF UN_PROCESO = MI_DATOS_COLUMNAS(1)  THEN                                                 
    
          MI_TABLA  := 'AYUDA_TAREAS';
      
          BEGIN
               SELECT COUNT('X') EXISTE
               INTO MI_EXISTE 
               FROM AYUDA_TAREAS
               WHERE ID_PROCESO = UN_PROCESO;
               EXCEPTION WHEN NO_DATA_FOUND THEN
               MI_EXISTE :=0 ;
          END;
                   
          IF (MI_EXISTE NOT IN (0) AND (MI_CONTADOR = 0)) THEN         
          
            BEGIN
                    
                    MI_RTAACME := PCK_DATOS.FC_ACME
                                  (UN_TABLA  => MI_TABLA
                                  ,UN_ACCION => 'E'
                                  ,UN_CONDICION => 'ID_PROCESO = '''||UN_PROCESO||''' ');
                        
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_ELIMINAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                    
            END ;
            
         MI_CONTADOR := 1;
        
         END IF;
           
          BEGIN
             
             MI_CAMPOS :='ID_PROCESO
                         ,ID_TAREA
                         ,TIPO
                         ,ORDEN
                         ,DESCRIPCION
                         ,FORMULARIO
                         ,CONTROL
                         ,URL_VIDEO
                         ,RUTA
                         ,TENERCUENTA
                         ';
                         
            MI_VALORES := ''''|| MI_DATOS_COLUMNAS(1) ||'''
                          ,'''|| MI_DATOS_COLUMNAS(2)  ||'''
                          ,'''|| MI_DATOS_COLUMNAS(3)  ||'''
                          ,'''|| MI_DATOS_COLUMNAS(4)  ||'''
                          ,'''|| MI_DATOS_COLUMNAS(5)  ||'''
                          ,'''|| CASE WHEN MI_DATOS_COLUMNAS(6) <> 'null' THEN MI_DATOS_COLUMNAS(6) ELSE '' END   ||'''
                          ,'''|| CASE WHEN MI_DATOS_COLUMNAS(7) <> 'null' THEN MI_DATOS_COLUMNAS(7) ELSE '' END    ||'''
                          ,'''|| CASE WHEN MI_DATOS_COLUMNAS(8) <> 'null' THEN MI_DATOS_COLUMNAS(8) ELSE '' END    ||'''
                          ,'''|| CASE WHEN MI_DATOS_COLUMNAS(9) <> 'null' THEN MI_DATOS_COLUMNAS(9) ELSE '' END    ||'''
                          ,'''|| CASE WHEN MI_DATOS_COLUMNAS(10) <> 'null' THEN MI_DATOS_COLUMNAS(10) ELSE '' END  ||'''
                          ';
                        
            BEGIN               
                PCK_DATOS.GL_RTA := PCK_DATOS.FC_ACME (UN_TABLA    => MI_TABLA
                                               ,UN_ACCION  => 'I'
                                               ,UN_CAMPOS  => MI_CAMPOS
                                               ,UN_VALORES => MI_VALORES);
                EXCEPTION WHEN PCK_EXCEPCIONES.EXC_INSERTAR THEN
                    RAISE PCK_EXCEPCIONES.EXC_PRESUPUESTO;
                END;                               
            END ;
        MI_RETORNO := 'Proceso correcto. '||CHR(13);    
        ELSE
        MI_RETORNO := 'El proceso actualizar no corresponde con los datos a cargar. '||CHR(13);    
        END IF;
    END LOOP CREAR_TAREAS;
    
    RETURN MI_RETORNO;
END FC_CARGAR_TAREAS;

END PCK_AYUDAS;