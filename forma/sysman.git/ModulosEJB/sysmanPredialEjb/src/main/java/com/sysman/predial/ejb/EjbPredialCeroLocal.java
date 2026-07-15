package com.sysman.predial.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbPredialCeroLocal {

    String consultarEncabezadoDeColumna(
        String compania,
        int concepto)
                    throws SystemException;

    boolean consultarNombreUsuarioEnParametro(
        String compania,
        int modulo,
        String accion,
        String usuario)
                    throws SystemException;

    int consultarVigenciaValidaParaPago(
        String compania,
        String predio)
                    throws SystemException;

    void actualizarUltimaVigenciaCancelada(
        String compania,
        String codigo)
                    throws SystemException;

    String reversarPagoPredio(
        String compania,
        String numFactura,
        String codPredio,
        String pagBan,
        String paquete,
        Date fechapago,
        String codAcuerdo,
        String usuario)
                    throws SystemException;

    void insertarCambiosEnAuditoria(
        String compania,
        String codmod,
        String opemod,
        String ccomod,
        String vanmod,
        String vnumod,
        String descripcion,
        Date fecmod,
        Date hormod,
        String numeroOrden)
                    throws SystemException;

    String actualizarAvaluo(
        String compania,
        String codigopred,
        String vigencia,
        int modulo,
        BigDecimal avaluoigag,
        BigDecimal avaluo,
        String trpcodAnt,
        String trpcod,
        int preano,
        int ano,
        String consecutivorad,
        String usuario)
                    throws SystemException;

    long consultarAbonoEnRecibosDePago(
        String compania,
        String codigopred,
        int vigencia)
                    throws SystemException;

    String consultarNumeroDeCuotasPorUsuario(
        String compania,
        String acuerdo,
        String predio,
        String recibo)
                    throws SystemException;

    void generarFacturaVigencia(
        String compania,
        String codpredio,
        String numorden,
        int menorvigencia,
        int mayorvigencia,
        int anocorte,
        String nombrepropfact,
        String nitpropfact,
        String numordenfacturar,
        Date fechalimite,
        boolean imprimirTotalcero,
        int modulo,
        String usuario)
                    throws SystemException;

    void crearDetalleFactura(
        String compania,
        String codpredio,
        String numorden,
        String docnum,
        String tipofra,
        int menorvigencia,
        int mayorvigencia,
        String usuario)
                    throws SystemException;

    void realizarPagoPazySalvo(String compania, String referencia,
        Date fechaPago, String bancoPago, String paquete, String usuario,
        String nroCuponesAcu, String vlacumaldo) throws SystemException;
}