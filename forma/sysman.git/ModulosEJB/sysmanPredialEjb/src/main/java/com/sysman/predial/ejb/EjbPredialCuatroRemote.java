package com.sysman.predial.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbPredialCuatroRemote {

    String getRegistroPagoCuotaInicial(
        String compania,
        String numeroorden,
        String usuario,
        String observacion,
        Date fechacorte,
        String codpredio,
        String codigobanco,
        String nroacuerdo,
        String nrorecibo,
        BigDecimal totalpagado,
        int nrocuota,
        String trpcod)
                    throws SystemException;

    String getRegistroPagoCuotaInicial(
        String compania,
        String nrorecibo,
        String codbanco,
        Date fechacorte,
        String usuario,
        String nroacuerdo,
        String codpredio,
        int nrocuota,
        BigDecimal totalpagado,
        String obspagos)
                    throws SystemException;

    String getAnularRegistroPago(
        String compania,
        String numeroorden,
        String numfactura,
        String codpredio,
        String pagban,
        String paquete,
        Date fechapago,
        String acuerdo,
        String usuario)
                    throws SystemException;

    String getReactivarPredio(
        String compania,
        int nivel,
        String codigo,
        String noresolucion,
        Date fecresolucion,
        String elabresolucion,
        String firmaresolucion,
        String usuario)
                    throws SystemException;

    String getAnularPredio(
        String compania,
        int nivel,
        String codigo,
        String noresolucion,
        Date fecresolucion,
        String elabresolucion,
        String firmaresolucion,
        String usuario)
                    throws SystemException;

    void registrarPreinscripcion(
        int anioprescripcion,
        String codigousuario,
        String compania,
        String codigopred,
        String fechaprescripcion,
        String resolucion,
        String observacion)
                    throws SystemException;

    String getRecibosExcedentesUno(
        String compania,
        String numfacturaex,
        String codigopredioex,
        String numeroorden,
        String banco,
        String paquete,
        Date fecha,
        String usuario,
        String codigopredio,
        String numordenex,
        String barras,
        String facturapago,
        String excedentes,
        String docnum,
        String rppredio,
        int rpanio,
        String rptotal,
        String rpavaluo,
        boolean insrecibopago,
        boolean actualiza)
                    throws SystemException;

    boolean registrarExoneracionVigencias(
        String compania,
        String codigo,
        Date fecresolucion,
        String numresolucion,
        String observacion,
        int viginicial,
        int vigfinal,
        String elabresolucion,
        String firmaresolucion,
        String usuario,
        boolean indaplicar)
                    throws SystemException;

    void anulaRecibosExcedentes(
        String compania,
        String numeroorden,
        String numerofactura,
        String usuario)
                    throws SystemException;

    String arreglarCupones(
        String compania,
        String fechainicial,
        String fechafinal,
        String bancoinicial,
        String bancofinal,
        String paqueteinicial,
        String paquetefinal,
        String nombrebancoini,
        String usuario)
                    throws SystemException;

    String consultarCodigoAuditoria(
        String compania,
        String movimiento)
                    throws SystemException;

    String generarFacturaExcedente(
        String compania,
        String codigoPredio,
        String numeroOrden,
        String usuario,
        String nombre)
                    throws SystemException;

    void imprimirVigenciaCaja(
        String compania,
        String codigopredial,
        String numeroorden,
        BigDecimal avaluoano,
        Date fechacorte,
        boolean aplicaley1066,
        boolean aplicaley1175,
        boolean aniounico,
        int aniofinal,
        int pagoano,
        String usuario,
        String nompropietario,
        String nitpropfact,
        String banco)
                    throws SystemException;

    void incautarPredio(
        String compania,
        String codigopred,
        String codigousuario,
        String observacion,
        int opcion,
        String camposAux,
        String noresolucion)
                    throws SystemException;

    String getNombreConceptoPredial(
        String compania,
        int codigo,
        int anio)
                    throws SystemException;

    String armarConsultaMorososPredial(
        String compania,
        int anioInicial,
        int anioFinal,
        boolean checkacuerdos)
                    throws SystemException;

    String armarConsultaFacturadosPredial(
        String compania,
        int anioInicial,
        int anioFinal)
                    throws SystemException;

    void copiarConceptos(
        String compania,
        String codigopredial,
        String numeroorden,
        int pagoano,
        String usuario)
                    throws SystemException;

    String obtenerAvaluoVigencia(
        String compania,
        String predio,
        int anio,
        String numeroOrden)
                    throws SystemException;

    String obtenerTarifaVigencia(
        String compania,
        String predio,
        String numeroOrden,
        int anio)
                    throws SystemException;
}