package com.sysman.predial.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbPredialSeisRemote {
    String imprimirCuotaAcuerdoDePago(
        String codigoacuerdo,
        String compania,
        String usuario,
        int fechavencida,
        boolean anulado,
        boolean cancelado,
        int pagocuotaanterior,
        long cuota,
        int facturacioncuotaanterior,
        int controlarrecibos,
        String codigopredio)
                    throws SystemException;

    int verificarFechaLimiteCuota(
        String codigoacuerdo,
        long cuota)
                    throws SystemException;

    int verificarPagoCuotaAnterior(
        String codigoacuerdo)
                    throws SystemException;

    int verificarFacCuotaAnterior(
        String codigoacuerdo,
        long cuota)
                    throws SystemException;

    int verificarAnulacionReciboPendiente(
        String codigoacuerdo,
        String codigopredio,
        String compania,
        long cuota)
                    throws SystemException;

    String obtenerCuotasRecibos(
        String acuerdo,
        String predio,
        String recibo)
                    throws SystemException;

    long reemplazarTarifas(
        String compania,
        int anoremplazado,
        int anoanterior,
        String usuario,
        BigDecimal incremento)
                    throws SystemException;

    String importarIGACTipoUno(
        String compania,
        String usuario,
        Date fechacorte,
        boolean indTotal,
        String nombrecompania)
                    throws SystemException;

    String validarTipoResolucion(
        String primeralinea)
                    throws SystemException;

    void distribuirAcuerdoAcacias(
        String compania,
        String codigopredio,
        String codigoacuerdo,
        int preanoi,
        int preano,
        String tabla)
                    throws SystemException;

    void actualizarAcuerdosFacturados(
        String compania,
        String tabla,
        int codigo,
        String codigoacuerdo,
        String campos,
        BigInteger cuota,
        String condicion)
                    throws SystemException;

    long activarReserva(
        String compania,
        String codigo,
        double porcentaje,
        int vigencia,
        String ordenpredial,
        String usuario,
        String resolucion)
                    throws SystemException;

    void cancelarReserva(
        String compania,
        String codigo,
        int vigencia,
        String ordenpredial,
        String usuario,
        String resolucion)
                    throws SystemException;

    void reversarPagosExcedentes(
        String compania,
        String numFactura,
        String codPredio,
        String codAcuerdo,
        String usuario)
                    throws SystemException;

    void reversarPagosCuotasAcuerdos(
        String compania,
        String numFactura,
        String codPredio,
        String codAcuerdo,
        String usuario,
        boolean abonoaacuerdo)
                    throws SystemException;

    void reversarPagoFinal(
        String compania,
        String numFactura,
        String codPredio,
        int preano,
        int preanoi,
        String pagBan,
        String paquete,
        Date fechapago,
        String usuario)
                    throws SystemException;

    void reversarAbonos(
        String compania,
        String numFactura,
        String codPredio,
        String usuario,
        int preano,
        int preanoi)
                    throws SystemException;

    void cancelarReservasUsuario(
        String compania,
        String codigo,
        int vigencia,
        String ordenpredial,
        String usuario,
        String resolucion)
                    throws SystemException;

    BigDecimal reversarPagoPazYSalvo(
        String compania,
        String referencia,
        Date fecha,
        String paquete,
        String banco,
        String numcupones,
        String acumulado,
        String usuario)
                    throws SystemException;

    void habilitarExento(String compania, int nivelUsuario, String usuario,
        boolean indExeImpuesto, boolean indExeCar, boolean indExeOtros,
        int anioDesde, int anioHasta, String codpredio, String numeroOrden,
        String codResolucion, Date fecResolucion, String elaboradaPor,
        String firmadaPor, String observacion) throws SystemException;

    void deshabilitarExento(String compania, int nivelUsuario, String usuario,
        boolean indExeImpuesto, boolean indExeCar, boolean indExeOtros,
        int anioDesde, int anioHasta, String codPredio, String numeroOrden,
        String codResolucion, Date fecResolucion, String elaboradapor,
        String firmadapor, String observacion) throws SystemException;
}
