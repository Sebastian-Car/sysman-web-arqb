package com.sysman.recursos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbGeneralesRemote {

    void calcularPaag(
        String compania,
        int ano)
                    throws SystemException;

    String consultarNombreElemento(
        String codelemento,
        String compania,
        Boolean opcion)
                    throws SystemException;

    String consultarEstadoDeVigencia(
        String compania,
        int anio,
        String parametro)
                    throws SystemException;

    BigDecimal actualizarCampoEnInventario(
        String compania,
        String elemento,
        BigDecimal cantidad,
        String campo)
                    throws SystemException;

    BigDecimal consultarEjecucionPresupuestal(
        String compania,
        String columna,
        int anio,
        String id,
        int mes)
                    throws SystemException;

    boolean afectarNovedadEnContratacion(
        String compania,
        String smodulo,
        String stiponovedad,
        int lano,
        long dnumero,
        Date dfechainicial,
        Date dfechafinal,
        Date dfechavencimiento,
        BigDecimal dvalor,
        String stipocontrato,
        long dnumerocontrato,
        String usuario)
                    throws SystemException;

    String retornarNombreCompania(
        String compania)
                    throws SystemException;

    int actDatosErrorGeneral(
        String tabla,
        String campo,
        String usuario)
                    throws SystemException;

    void registrarSolicitud(
        String compania,
        long orden,
        String campo,
        String claseBodegaAlmacen,
        String claseBodega)
                    throws SystemException;

    boolean cambiarRequisicion(
        String compania,
        long orden,
        long numeroAnterior,
        String usuario,
        Date fecha,
        String dependencia,
        String tercero,
        String sucursal,
        BigDecimal valorestimado,
        String descripcion,
        String observaciones,
        long plazo,
        String unidadTiempo,
        String periodicidad,
        long numeroEntregas,
        String claseBodega,
        String auxiliar)
                    throws SystemException;

    void registrarDetalleOrdenDeCompra(
        String compania,
        int orden,
        String claseorden,
        BigDecimal porcdescglobal,
        BigDecimal porcivaglobal,
        boolean esModificacionContratos,
        String usuario)
                    throws SystemException;

    void registrarDetalleRequisicion(
        String compania,
        BigInteger codRequisicion,
        BigInteger codDetalle,
        String usuario)
                    throws SystemException;

    void validarOrdenesDeSuministroVacias(
        String compania)
                    throws SystemException;
    
    void copiarTerceroXCompania(
    		String companiaOrigen, 
    		String companiaDestino) 
    					throws SystemException;
}