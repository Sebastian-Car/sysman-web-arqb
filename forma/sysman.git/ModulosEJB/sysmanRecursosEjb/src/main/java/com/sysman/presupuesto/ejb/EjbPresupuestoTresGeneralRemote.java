package com.sysman.presupuesto.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbPresupuestoTresGeneralRemote {

	int actualizarValorSolicitado(
	        String compania,
	        String itemAfectado,
	        int vigenciaItemAfectado,
	        BigDecimal valorDigitado,
	        int codigoItem,
	        int tipoSolicitud,
	        String accion,
	        BigDecimal valorAntiguo,
	        String codigo,
	        int ano,
	        BigDecimal valorRubro,
	        String fuente,
	        String centroCosto,
	        String referencia,
	        int solicitudAfect,
	        int numeroSDP) throws SystemException;

    void afectarSolicitud(
        String compania,
        long solicitudAfectada,
        long solicitudNueva,
        int tipoSolicitudNueva) throws SystemException;

    void actualizarTerDeoSolicitud(
        String compania,
        long numero,
        String tercero,
        String dependencia) throws SystemException;

    boolean esOrdenador(
        String compania,
        String cedula) throws SystemException;

    boolean actualizarSolicitudesNoAprobadas(
        String compania,
        long solicitud,
        String aprobacion)
                    throws SystemException;

    String generarPlanoSecretaria(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String tipocomp,
        String cptInicial,
        String cptFinal,

        String responsableppto)
                    throws SystemException;

    String generarPlanoOrdenPago(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String tipocomp,
        String cptInicial,
        String cptFinal,
        String vigencia,

        String responsableppto)
                    throws SystemException;

    String generarPlanoOrdenPagoReserva(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String tipocomp,
        String vigencia,
        String cptInicial,
        String cptFinal,

        String responsableppto)
                    throws SystemException;

    String actualizaSituacionFondos(
        String compania,
        int ano);

    void actualizarFuenteDetalle(
        String compania,
        int anio,
        String usuario)
                    throws SystemException;

    BigDecimal actualizarClasificadoresPptal(
        String compania,
        int anio,
        String usuario) throws SystemException;

    BigDecimal actualizaClasificadorCadenaPptal(
        String compania,
        int anio,
        String usuario) throws SystemException;
    
    String cargarReclasificacionCierre(
			String compania,
			int anio,
			String clase, 
			String cadena, 
			String usuario)	throws SystemException;
}