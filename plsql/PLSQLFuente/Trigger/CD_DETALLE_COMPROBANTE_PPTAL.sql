create or replace TRIGGER CD_DETALLE_COMPROBANTE_PPTAL  
/*
      NAME              : CD_DETALLE_COMPROBANTE_PPTAL
      AUTHORS           : SYSMAN  SAS
      AUTHOR MIGRACION  : JULIO CESAR REINA PANCHE
      DATE              : 13/03/2019
      TIME              : 02:00 PM
      MODIFIER          : 
      DESCRIPTION       : TRIGGER que actualiza los check de con comprobante presupuestal y afectado 
                          de la solicitud de banco de proyectos cuando se elimina el registro y la disponibilidad
                          
  */
FOR DELETE ON DETALLE_COMPROBANTE_PPTAL 
COMPOUND TRIGGER
  MI_COMPANIA               PCK_SUBTIPOS.TI_COMPANIA;
  MI_ANO                    PCK_SUBTIPOS.TI_ANIO;
  MI_TIPO                   PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL;
  MI_COMPROBANTE            DETALLE_COMPROBANTE_PPTAL.COMPROBANTE%TYPE;
  MI_ANO_AFECT              PCK_SUBTIPOS.TI_ANIO;
  MI_TIPO_AFECT             PCK_SUBTIPOS.TI_TIPOCOMPROBANTEPPTAL;
  MI_CPTE_AFECT             DETALLE_COMPROBANTE_PPTAL.COMPROBANTE%TYPE;
  MI_TIPOT                  BPNOVEDADPROYECTO.TIPOT%TYPE;
  MI_CLASET                 BPNOVEDADPROYECTO.CLASET%TYPE;
  MI_CODIGOT                BPNOVEDADPROYECTO.CODIGO%TYPE;
  MI_DEPENDENCIAT           BPNOVEDADPROYECTO.DEPENDENCIA%TYPE;
  MI_TIPOSOLICITUD          DETALLE_COMPROBANTE_PPTAL.TIPO_SOLICITUD%TYPE;
  MI_NUMEROSOLICITUD        DETALLE_COMPROBANTE_PPTAL.NUMERO_SOLICITUD%TYPE;
BEFORE EACH ROW IS
BEGIN
  MI_COMPANIA        := :OLD.COMPANIA;
  MI_ANO             := :OLD.ANO;
  MI_TIPO            := :OLD.TIPO_CPTE;
  MI_COMPROBANTE     := :OLD.COMPROBANTE;
  MI_ANO_AFECT       := :OLD.ANO_AFECT;
  MI_TIPO_AFECT      := :OLD.TIPO_CPTE_AFECT;
  MI_CPTE_AFECT      := :OLD.CMPTE_AFECTADO;
  MI_TIPOT           := :OLD.TIPOT;
  MI_CLASET          := :OLD.CLASET;
  MI_CODIGOT         := :OLD.CMPTE_SOLICI_AFECTADO;
  MI_DEPENDENCIAT    := :OLD.DEPENDENCIA;
  MI_TIPOSOLICITUD   := :OLD.TIPO_SOLICITUD;
  MI_NUMEROSOLICITUD := :OLD.NUMERO_SOLICITUD;
  
  -- ECABRERA T:7708444 Se valida que el tipo de disponibilidad no este nula para actualizar check
  IF ( MI_TIPO = 'DIS' AND MI_NUMEROSOLICITUD IS NULL ) THEN
  	BEGIN
		MI_NUMEROSOLICITUD := TO_NUMBER(:OLD.NRO_DOCUMENTO);
	EXCEPTION WHEN OTHERS THEN
		NULL;
	END;  
  END IF;

     IF (MI_TIPO = 'CDP' ) THEN
  	BEGIN
		MI_NUMEROSOLICITUD := TO_NUMBER(:OLD.NRO_DOCUMENTO);
	EXCEPTION WHEN OTHERS THEN
		NULL;
	END;
   END IF;
  -- T:7708444
END BEFORE EACH ROW;

AFTER STATEMENT IS 
BEGIN
    PCK_PRESUPUESTO3.PR_VALIDARSOLICITUDDIS(UN_COMPANIA         => MI_COMPANIA,
                                            UN_ANO              => MI_ANO,
                                            UN_TIPO_CPTE        => MI_TIPO,
                                            UN_COMPROBANTE      => MI_COMPROBANTE,
                                            UN_ANO_AFECT        => MI_ANO_AFECT,
                                            UN_TIPOCPTE_AFECT   => MI_TIPO_AFECT,
                                            UN_CMPTE_AFECTADO   => MI_CPTE_AFECT,
                                            UN_TIPOT            => MI_TIPOT, 
                                            UN_CLASET           => MI_CLASET,     
                                            UN_CODIGOT          => MI_CODIGOT,    
                                            UN_DEPENDENCIAT     => MI_DEPENDENCIAT  
                                            );
                                            
    PCK_PRESUPUESTO3.PR_ACTINDAFECDIS(UN_COMPANIA           => MI_COMPANIA,
                                      UN_ANO                => MI_ANO,
                                      UN_TIPO_CPTE          => MI_TIPO,
                                      UN_COMPROBANTE        => MI_COMPROBANTE,
                                      UN_TIPOSOLICITUD      => MI_TIPOSOLICITUD,
                                      UN_NUMEROSOLICITUD    => MI_NUMEROSOLICITUD)  ;   
    IF (MI_TIPO = 'CDP' ) THEN                                 

    PCK_PRESUPUESTO3.PR_VALIDARSOLICITUDCDP(UN_COMPANIA         => MI_COMPANIA,
                                            UN_ANO              => MI_ANO,
                                            UN_TIPO_CPTE        => MI_TIPO,
                                            UN_COMPROBANTE      => MI_COMPROBANTE,
                                            UN_NUMEROSOLICITUD  => MI_NUMEROSOLICITUD);
      END IF;      
END AFTER STATEMENT  ;
END;