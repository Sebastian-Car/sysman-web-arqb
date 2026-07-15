package com.sysman.contratos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbContratosUnoRemote {

    void insertarSectoresDefault(
        String compania,
        String usuario)
                    throws SystemException;

    BigDecimal getTotalValorNovedad(
        String compania,
        String claseorden,
        long ordendecompra,
        String clasenovedad)
                    throws SystemException;

    String anularOrdendeCompra(
        String compania,
        String tipo,
        long numero,
        String usuario)
                    throws SystemException;

    String enviarNovedadesaNomina(String compania, String novedad,
        String ordenDeCompra, BigDecimal valorTotalNovedad, String usuario)
                    throws SystemException;

    String getDiasMinuta(
        Date fecha)
                    throws SystemException;

    String getPoliza(
        String compania,
        String claseorden,
        long ordendecompra,
        long tipo)
                    throws SystemException;

    String getPolizaResolucion(
        String compania,
        String claseorden,
        long ordendecompra)
                    throws SystemException;

    String getPolizaResolucion2(
        String compania,
        String claseorden,
        long ordendecompra)
                    throws SystemException;

    String getDetalleSupervisorCargo(
        String compania,
        String claseorden,
        long ordendecompra)
                    throws SystemException;

    String getDetalleSupervisorCedula(
        String compania,
        String claseorden,
        long ordendecompra)
                    throws SystemException;

    String getDetalleSupervisorNombre(
        String compania,
        String claseorden,
        long ordendecompra)
                    throws SystemException;

    BigDecimal getConsecutivoOrdenCompra(
        String compania,
        String tipoafectado,
        long numeroafectado)
                    throws SystemException;

    String getResolucionIndividual(
        String compania,
        String claseorden,
        long ordendecompra)
                    throws SystemException;

    String getDetalleSupervisorContrato(
        String compania,
        String claseorden,
        long ordendecompra)
                    throws SystemException;

    String getDetalleSupervisorProfesion(
        String compania,
        String claseorden,
        long ordendecompra)
                    throws SystemException;

    String getDetalleResultado(
        String compania,
        String tipocontrato,
        long numero)
                    throws SystemException;

    BigDecimal ConsecContrato(
        String compania,
        String clasecontrato,
        int aniovigencia,
        String usuario)
                    throws SystemException;

    void importarPrecontractual(String compania, long numeroOrden,
        String claseOrden, long estudioPrevio, String usuario,
        String numProceso) throws SystemException;

    String extraerValores(
        String compania,
        String claseorden,
        long numero,
        BigDecimal valorfinal,
        String usuario)
                    throws SystemException;

    String copiarContrato(
        String compania,
        String claseorden,
        long copiarde,
        int vigencia,
        long numero,
        String usuario)
                    throws SystemException;

    void seleccionarRequisiciones(
        String compania,
        String usuario)
                    throws SystemException;

    void insertaPpto(
        String compania,
        String claseorden,
        long numero,
        String clasedisp,
        long numerodispsel,
        String fechaselec,
        String tercero,
        String sucursal,
        String usuario)
                    throws SystemException;

    boolean actualizaIvaDetalle(
			String compania, 
			String claseOrden, 
			long numero, 
			BigDecimal porcIvaGlobal,
			String roundValorIvaoc, 
			String roundVlrTotaloc, 
			String roundValorUnioc, 
			BigDecimal digRedoVluniIva,
			BigDecimal digRoundVlrIva, 
			BigDecimal digRedonTotal, 
			String usuario) throws SystemException;

    BigDecimal calculartotalpagos(
        String compania,
        String claseorden,
        long numero,
        String clasecontable)
                    throws SystemException;

    boolean eliminarOrdendeCompra(
        String compania,
        String claseorden,
        long numero)
                    throws SystemException;
}