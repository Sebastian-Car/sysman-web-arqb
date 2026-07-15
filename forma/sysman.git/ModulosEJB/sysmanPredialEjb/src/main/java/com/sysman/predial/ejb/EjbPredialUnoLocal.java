package com.sysman.predial.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;

import javax.ejb.Local;

@Local
public interface EjbPredialUnoLocal {

    void anularAbonoSaldoCredito(
        String compania,
        String docnum,
        String precod,
        String user,
        int preano,
        boolean pagado)
                    throws SystemException;

    int consultarVigenciaValidaParaPago(
        String compania,
        String predio)
                    throws SystemException;

    void actualizarUltimaVigenciaPaga(
        String compania,
        String codigoinicial,
        String codigofinal,
        int vigencia,
        String usuario)
                    throws SystemException;

    void distribuirCuotasAcuerdosyRecibosDePago(
        String compania,
        String codigoinicial,
        String codigofinal,
        String usuario)
                    throws SystemException;

    void actualizarAcuerdos(
        String compania,
        String codigoinicial,
        String codigofinal,
        String usuario)
                    throws SystemException;

    String verificarCuotasCanceladas(
        String compania,
        boolean indfechapago,
        String codigoinicial,
        String codigofinal,
        String usuario)
                    throws SystemException;

    void registrarRecaudoAbonos(
        String compania,
        String recibo,
        String predio,
        String banco,
        String fechaRecaudo,
        String numeroOrden,
        int modulo,
        String user,
        BigDecimal valorRecibo)
                    throws SystemException;

    void registrarRecaudoCuotas(
        String compania,
        String recibo,
        String predio,
        String banco,
        String fechaRecaudo,
        String numeroOrden,
        int modulo,
        String user,
        BigDecimal valorRecibo,
        BigDecimal nroCuota,
        int vigencia)
                    throws SystemException;

    void registrarRecaudoAcuerdos(
        String compania,
        String recibo,
        String predio,
        String banco,
        String fechaRecaudo,
        String numeroOrden,
        int modulo,
        String user,
        BigDecimal valorRecibo)
                    throws SystemException;

    void registrarReciboAbonoAAcuerdo(
        String compania,
        String recibo,
        String predio,
        String banco,
        String fechaRecaudo,
        String numeroOrden,
        int modulo,
        String user,
        BigDecimal totalRecibo,
        String paquete)
                    throws SystemException;

    void registrarRecaudoUnicoVigencia(
        String compania,
        String recibo,
        String predio,
        String banco,
        String fechaRecaudo,
        String numeroOrden,
        int modulo,
        BigDecimal valorRecibo,
        int vigencia)
                    throws SystemException;

    void registrarRecaudoEnVigencia(
        String compania,
        String recibo,
        String predio,
        String banco,
        String fechaRecaudo,
        String numeroOrden,
        BigDecimal valorRecibo,
        int vigenciaFinal)
                    throws SystemException;

    void registrarRecaudoUnicoVigencia(
        String compania,
        String recibo,
        String predio,
        String banco,
        String fechaRecaudo,
        String numeroOrden,
        int modulo,
        BigDecimal valorRecibo,
        int vigencia,
        String paquete,
        String user)
                    throws SystemException;

    void registrarRecaudo(
        String compania,
        String recibo,
        String predio,
        String banco,
        String fechaRecaudo,
        String numeroOrden,
        BigDecimal valorRecibo,
        int vigenciaFinal,
        String paquete,
        String user)
                    throws SystemException;

    void activarPredialEnCobro(
        String compania,
        String precod,
        String user,
        String numproceso,
        String numorden)
                    throws SystemException;

    long actFacturadosPrescripcion(
        String compania,
        String codigo,
        String numpredial,
        int prescripcion,
        String resolucion,
        int desde,
        int hasta,
        String observacion,
        String usuario)
                    throws SystemException;

    long modificarEstrato(
        String codigo,
        String estrato,
        String nuevoestrato,
        String formato,
        String nuevoformato,
        String tabla,
        String tablai,
        String compania,
        String usuario,
        String descripcion)
                    throws SystemException;

    long insertModifiEstraSocio(
        String tabla,
        String campos,
        String valoresinsert)
                    throws SystemException;

    int consultarEncabezadoDeColumna(String codigo, String estrato,
        String nuevoEstrato, String formato, String nuevoFormato, String tabla,
        String tablai, String compania, String usuario, String descripcion)
                    throws SystemException;

    String verificarPrediosFacturados(String compania, String codigoInicial,
        String codigoFinal) throws SystemException;
}