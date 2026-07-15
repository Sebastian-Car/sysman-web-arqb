package com.sysman.recursos.ejb;

import com.sysman.exception.SystemException;

import javax.ejb.Remote;

@Remote
public interface EjbPrepararAnoRemote {

    void copiarAuxiliar(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException;

    void copiarCentroCosto(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException;

    void copiarReferencia(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException;

    void copiarFuenteRecurso(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException;

    void prepararAnoSiguiente(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException;

    void copiarPlanContable(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException;

    void copiarCuentaBanco(
        String compania,
        int anoDestino,
        int anoOrigen,
        String companiaDestino)
                    throws SystemException;

    void copiarParametro(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarTipoComprobante(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarConsecutivoTc(
        String compania,
        int anio,
        String companiaDestino)
                    throws SystemException;

    void copiarTercero(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarTipoComprobantePptal(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarConsecutivoTcp(
        String compania,
        int anio,
        String companiaDestino)
                    throws SystemException;

    void crearDatosContables(
        String compania,
        int ano,
        String companiaDestino)
                    throws SystemException;

    void crearRegistrosBasicos(
        String compania,
        int anio)
                    throws SystemException;

    void copiarTiposDocumentos(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarTipoOrdenDeCompra(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarAno(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarEstadoCivil(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void coipiarUnidadAv(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarGastoDeInversion(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarClaseGastoInversion(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiaBpActividades(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarTiposComponentes(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarUnidadProyectos(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarSerctorDnp(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarClaseProblema(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarServicio(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarCodigosCiiu(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarEstadosCobro(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiaSolicitudServicio(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarServiciosPublicos(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarBanco(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarTiposEmpleados(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarCausaRetiro(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarFestivos(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarParentesco(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarProcesosNomina(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarReporteFormateado(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarTipoActivo(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarTipoNovedad(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarJuzgados(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void copiarRetefuenteUvt(
        String compania,
        String companiaDestino)
                    throws SystemException;

    void prepararInicioVigencia(String compania, int anoDestino, int anoOrigen,
        String companiaDestino, boolean copiarnomina,
        boolean copiarcontabilidad, boolean copiarfactgeneral)
                    throws SystemException;
    
    void copiarPlanFlujoEfectivo(
            String compania,
            int anoDestino,
            int anoOrigen,
            String companiaDestino)
                        throws SystemException;
    
    void crearDatosNomina(
			String compania, 
			int anoDestino, 
			String companiaDestino) 
					throws SystemException;
    
    void copiarConceptoCentroCosto(
            String compania,
            int anoDestino,
            int anoOrigen,
            int concepto,
            String companiaDestino)
                        throws SystemException;
    String validarRefyCC(
			String compania, 
			int ano_i, 
			int ano_f)throws SystemException;
    
    void copiarUnidadEjecutora(
            String compania,
            int anoDestino,
            int anoOrigen,
            String companiaDestino)
                        throws SystemException;
    
	String obtenerEquivalencias(
			String compania,
			int anoDestino,
			int anoOrigen,
			String companiaDestino)throws SystemException;
}