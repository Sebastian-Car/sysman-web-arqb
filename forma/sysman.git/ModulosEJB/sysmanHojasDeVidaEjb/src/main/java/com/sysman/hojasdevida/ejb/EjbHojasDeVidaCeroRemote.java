package com.sysman.hojasdevida.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbHojasDeVidaCeroRemote {

    void validarFiltrosImpresionHV(
        boolean indListado,
        boolean indFechas,
        boolean indEstado,
        boolean indConsolidado,
        boolean indHistorial,
        Date fechainicial,
        Date fechafinal,
        String empleadoiniNum,
        String empleadofinNum,
        String informe,
        String estado)
                    throws SystemException;

    void validarEstudiosSuperiores(
        String compania,
        String numeroDcto,
        String sucursal)
                    throws SystemException;

    void actualizarConsecutivoEvaluacion(
        String compania,
        String numeroDctoInicial,
        String numeroDctoFinal,
        Date fechaInicial,
        Date fechaFinal,
        String usuario) throws SystemException;

    void calificarEvaluacion(
        String compania,
        int idEmpleado,
        BigInteger evaluacion,
        int claseEvaluacion,
        String usuario)
                    throws SystemException;

    void actualizarCamposNulosPersonal(
        String compania,
        String numeroDcto)
                    throws SystemException;

    void actualizarCamposNuevos(
        String compania,
        String numeroDcto,
        String sucursal,
        String usuario) throws SystemException;

    void registrarDetallesEvaluacion(
        String compania,
        BigInteger evaluacion,
        int claseEvaluacion,
        String cedulaEvaluado,
        String cedulaEvaluador,
        String sucursalEvaluado,
        String sucursalEvaluador,
        String escalafonEvaluador,
        String escalafonEvaluado,
        String tipoEvaluacion,
        String cargoEvaluador,
        String cargoEvaluado,
        String codigoEvaluador,
        String codigoEvaluado,
        String evaluadorComision,
        String usuario) throws SystemException;

    void ActualizarDetallesProfesiones(
        String compania,
        String numeroDcto,
        String sucursal,
        BigDecimal idDeEmpleado,
        String usuario) throws SystemException;

    void actualizarPuntaje(
        String compania,
        BigInteger evaluacion,
        int claseEvaluacion,
        String tipoEvaluacion,
        String criterioEvaluado,
        String criterioSeleccionado,
        String cedulaEvaluado,
        String cedulaEvaluador,
        String sucursalEvaluado,
        String sucursalEvaluador,
        boolean escompromiso,
        String usuario) throws SystemException;

    void actualizaractividadesinscritos(
        String compania,
        String evento,
        String tipoEv,
        Date fechaEv,
        String numeroDcto,
        String sucursal,
        String usuario) throws SystemException;

    void insertarActividades(
        String compania,
        int tipoTransaccion,
        long transaccion,
        String usuario) throws SystemException;

    void documentosPresentados(
        String compania,
        String sucursal,
        String tercero,
        String usuario) throws SystemException;

    void experienciaLaboral(
        String compania,
        String tercero,
        String sucursal,
        String usuario) throws SystemException;

    int registrarPersonal(
        String compania,
        String tercero,
        String sucursal,
        String usuario) throws SystemException;

    void actualizarEnvioCorreos(String compania,
        String nroConvocatoria,
        int opcion) throws SystemException;

    void cargarConvocatoriaManual(
        String compania,
        String numeroManual,
        String version,
        String nroConvocatoria,
        String usuario)
                    throws SystemException;

    void generarCompromisos(
        String compania,
        BigInteger evaluacion,
        int clase,
        int ano,
        String evaluado,
        String sucursalevaluado,
        int periodo,
        String tipo,
        String usuario)
                    throws SystemException;

    void heredarEvidencias(
        String compania,
        BigInteger evaluacion,
        String cedulaEvaluado,
        String cedulaEvaluador,
        int clase,
        String tipo,
        int ano,
        int opcion,
        String usuario)
                    throws SystemException;

    String cerrarConvocatoria(
        String compania,
        String convocatoria,
        String usuario) throws SystemException;

    void actualizarEnvioCorreosAutoservicio(
        String compania,
        long consecutivo,
        int clase,
        int opcion) throws SystemException;

    String crearRutaAnexos(String compania, int modulo, String cedula,
        String codigoruta) throws SystemException;
    
    void actualizarExpLaboral(
            String compania,
            String numeroDcto,
            String codigoPersona,
            String sucursal,
            String usuario) throws SystemException;
    
	void actualizarFondosHV(
			String compania, 
			long idDeEmpleado) throws SystemException;

}
