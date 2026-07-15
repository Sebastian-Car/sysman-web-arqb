CREATE OR REPLACE TRIGGER "BIUD_VI_DETALLE_VIATICOS"  
/*
      NAME              : BIUD_VI_DETALLE_VIATICOS
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : ELKIN GEOVANNY AMAYA SILVA
      DATE MIGRADOR     : 27/01/2018
      TIME              : 14:45 PM
      SOURCE MODULE     : 
      MODIFIER          : 
      DATE MODIFIED     : 
      TIME              : 
      DESCRIPTION       : ACTUALIZA LOS TOTALES DE LOS DETALLES DE VIATICO Y EL TOTAL DEL VIATICO
                          
  */
BEFORE INSERT OR UPDATE OR DELETE ON VI_DETALLE_VIATICOS
FOR EACH ROW

DECLARE

  MI_RSCONCEPTO           SYS_REFCURSOR;
  MI_RSSOLICITUD          SYS_REFCURSOR;
  MI_NODIAS               NUMBER;
  MI_NUMDIASSINPER        NUMBER;
  MI_NUMDIASPER           NUMBER;
  MI_VALDIASPER           NUMBER;
  MI_VALDIASSINPER        NUMBER;
  MI_VALORVIATICO         NUMBER;
  MI_VALORABONADO         VI_DETALLE_VIATICOS.VALOR_ABONADO%TYPE;
  MI_TOTAL                VI_DETALLE_VIATICOS.TOTAL%TYPE;
  MI_CAMPOS               PCK_SUBTIPOS.TI_CAMPOS;
  MI_CONDICION            PCK_SUBTIPOS.TI_CONDICION;
  MI_MSGERROR             PCK_SUBTIPOS.TI_CLAVEVALOR;
BEGIN

 IF INSERTING OR UPDATING THEN
   --ACTUALIZAR DETALLES VIATICOS 
    <<CONSULTAR_CONCEPTOS>> 
    FOR MI_RSCONCEPTO IN (
                        SELECT CODIGO_CONCEPTO,
                               TARIFA,
                               TIPO_CONCEPTO
                        FROM(SELECT VI_DETALLE_CATEGORIA_CONCEPTO.CODIGO_CONCEPTO,
                                    VI_DETALLE_CATEGORIA_CONCEPTO.TARIFA,
                                    VI_CONCEPTO_VIATICOS.TIPO_CONCEPTO
                             FROM VI_DETALLE_CATEGORIA_CONCEPTO
                             INNER JOIN VI_CONCEPTO_VIATICOS
                                 ON VI_DETALLE_CATEGORIA_CONCEPTO.COMPANIA        = VI_CONCEPTO_VIATICOS.COMPANIA
                                AND VI_DETALLE_CATEGORIA_CONCEPTO.ANO             = VI_CONCEPTO_VIATICOS.ANO
                                AND VI_DETALLE_CATEGORIA_CONCEPTO.CODIGO_CONCEPTO = VI_CONCEPTO_VIATICOS.CODIGO_CONCEPTO
                             INNER JOIN VI_VIATICOS
                                 ON VI_DETALLE_CATEGORIA_CONCEPTO.COMPANIA            = VI_VIATICOS.COMPANIA
                                AND VI_DETALLE_CATEGORIA_CONCEPTO.ANO                 = VI_VIATICOS.ANO
                                AND VI_DETALLE_CATEGORIA_CONCEPTO.PAISORIGEN          = VI_VIATICOS.PAIS_ORIGEN
                                AND VI_DETALLE_CATEGORIA_CONCEPTO.DEPARTAMENTOORIGEN  = VI_VIATICOS.DEPARTAMENTO_ORIGEN
                                AND VI_DETALLE_CATEGORIA_CONCEPTO.CIUDADORIGEN        = VI_VIATICOS.CIUDAD_ORIGEN
                                AND VI_DETALLE_CATEGORIA_CONCEPTO.PAISDESTINO         = VI_VIATICOS.PAIS_DESTINO
                                AND VI_DETALLE_CATEGORIA_CONCEPTO.DEPARTAMENTODESTINO = VI_VIATICOS.DEPARTAMENTO_DESTINO
                                AND VI_DETALLE_CATEGORIA_CONCEPTO.CIUDADDESTINO       = VI_VIATICOS.CIUDAD_DESTINO
                                AND VI_DETALLE_CATEGORIA_CONCEPTO.ESCALAFON           = VI_VIATICOS.ESCALAFON
                                AND VI_DETALLE_CATEGORIA_CONCEPTO.ID_CATEGORIA        = VI_VIATICOS.ID_CATEGORIA
                                WHERE VI_VIATICOS.COMPANIA                              = :NEW.COMPANIA
                                  AND VI_VIATICOS.ANO                                   = :NEW.ANO
                                  AND VI_VIATICOS.TIPO_VIATICO                          = :NEW.TIPO_VIATICO
                                  AND VI_VIATICOS.CODSOLICITUD                          = :NEW.NUMERO
                                  AND VI_DETALLE_CATEGORIA_CONCEPTO.CODIGO_CONCEPTO     = :NEW.CONCEPTO
                                  AND VI_DETALLE_CATEGORIA_CONCEPTO.CONSECUTIVO         = :NEW.CONSECUTIVO
                          )DC )LOOP 
                                                    
        
            <<RECORRER_SOLICITUD>>             
            FOR MI_RSSOLICITUD IN (SELECT NODIAS
                                          ,TRSNESPECIAL
                                          ,VEHICULO
                                  FROM VI_VIATICOS
                                  WHERE COMPANIA   = :NEW.COMPANIA
                                  AND CODSOLICITUD = :NEW.NUMERO)LOOP 
                                
                  
                  MI_NODIAS := MI_RSSOLICITUD.NODIAS;            
                  
                  MI_NUMDIASSINPER := PCK_VIATICOS.FC_DIASPERNOCTANDOVIATICO(UN_COMPANIA        => :NEW.COMPANIA,
                                                                             UN_PERNOCTANDO     => 0,
                                                                             UN_CODIGOSOLICITUD => :NEW.NUMERO ); 
                          
                  MI_NUMDIASPER := PCK_VIATICOS.FC_DIASPERNOCTANDOVIATICO(UN_COMPANIA        => :NEW.COMPANIA,
                                                                          UN_PERNOCTANDO     => -1,
                                                                          UN_CODIGOSOLICITUD => :NEW.NUMERO);                  
                  
                 IF MI_RSCONCEPTO.TIPO_CONCEPTO = 'T' THEN
                    MI_VALDIASPER := 0; 
                    MI_VALDIASSINPER := 0;
                 ELSE
                    MI_VALDIASPER :=  MI_RSCONCEPTO.TARIFA  * MI_NUMDIASPER;  
                    MI_VALDIASSINPER := ROUND((MI_RSCONCEPTO.TARIFA * (PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA  => :NEW.COMPANIA,
                                                                           UN_NOMBRE    => 'PORCENTAJE ULTIMO DIA PERNOCTANDO',
                                                                           UN_MODULO 	  => PCK_DATOS.FC_MODULONOMINA,
                                                                           UN_FECHA_PAR => SYSDATE,
                                                                           UN_IND_MAYUS => 0)/100))* MI_NUMDIASSINPER,0);  
                 END IF;                 
                 
                IF MI_RSSOLICITUD.TRSNESPECIAL = -1 AND  MI_RSSOLICITUD.NODIAS IS NOT NULL THEN
                  
                  IF MI_RSCONCEPTO.TIPO_CONCEPTO = 'T' THEN                    
                    MI_VALORVIATICO := MI_RSCONCEPTO.TARIFA * ((MI_NUMDIASSINPER * 2) - MI_NODIAS);                  
                  END IF;
                END IF;
                
                
                IF MI_RSSOLICITUD.VEHICULO = 0 AND MI_RSSOLICITUD.TRSNESPECIAL = 0 THEN
                   IF MI_RSCONCEPTO.TIPO_CONCEPTO = 'T' THEN                     
                    MI_VALORVIATICO := MI_RSCONCEPTO.TARIFA * ((MI_NUMDIASSINPER * 2));
                   END IF; 
                END IF;
                
                
                IF MI_RSSOLICITUD.VEHICULO = -1 THEN
                  MI_VALORVIATICO := 0;
                END IF;
                
                IF MI_RSCONCEPTO.TIPO_CONCEPTO = 'H' THEN                     
                  MI_VALORVIATICO := (MI_VALDIASPER +  MI_VALDIASSINPER);
                END IF;        


           IF UPDATING OR INSERTING THEN
               :NEW.NUMDIASPER := NVL(MI_NUMDIASPER,0);
               :NEW.NUMDIASSINPER := NVL(MI_NUMDIASSINPER,0);                                
               :NEW.VALDIASPER := NVL(MI_VALDIASPER,0);
               :NEW.VALDIASSINPER := NVL(MI_VALDIASSINPER,0);
               :NEW.TOTAL := NVL(MI_VALORVIATICO,0);
               :NEW.SALDO := NVL(MI_VALORVIATICO,0) - NVL(:NEW.VALOR_ABONADO,0);              
            
            END IF;
            
    
      END LOOP RECORRER_SOLICITUD;
         
    END LOOP CONSULTAR_CONCEPTOS; 
    
  END IF;
  
  IF INSERTING OR UPDATING THEN  
    PCK_VIATICOS.PR_ACTTOTALDETALLEVIATICOS(UN_COMPANIA        => :NEW.COMPANIA,
                                            UN_ANO             => :NEW.ANO,
                                            UN_TIPOVIATICO     => :NEW.TIPO_VIATICO, 
                                            UN_CODIGOSOLICITUD => :NEW.NUMERO,
                                            UN_VALOR           => :NEW.TOTAL,
                                            UN_USUARIO         => :NEW.CREATED_BY);   
  END IF;  
  IF UPDATING OR DELETING THEN
    PCK_VIATICOS.PR_ACTTOTALDETALLEVIATICOS(UN_COMPANIA        => :OLD.COMPANIA,
                                            UN_ANO             => :OLD.ANO,
                                            UN_TIPOVIATICO     => :OLD.TIPO_VIATICO, 
                                            UN_CODIGOSOLICITUD => :OLD.NUMERO,
                                            UN_VALOR           => :OLD.TOTAL * -1,
                                            UN_USUARIO         => :OLD.CREATED_BY);
  END IF;
       

END;
