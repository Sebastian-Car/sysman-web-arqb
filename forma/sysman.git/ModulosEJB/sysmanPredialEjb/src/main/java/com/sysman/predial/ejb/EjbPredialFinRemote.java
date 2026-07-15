package com.sysman.predial.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbPredialFinRemote {
    void crearAcuedoDePago(
        String compania,
        String acuerdo,
        int periodo,
        String tAcuerdo,
        String tFacturadosacu)
                    throws SystemException;

    void liquidaInteresRecargo(
        String compania,
        String codigoacuerdo)
                    throws SystemException;

    void liquidarInteresAcuerdo(
        String compania,
        String codigoacuerdo)
                    throws SystemException;

    void revertirInteresPagoAnticipado(
        String compania,
        String codigoacuerdo)
                    throws SystemException;

    String calcularCuotasAcuerdo(
        String compania,
        String codigoacuerdo,
        boolean anulado,
        String usuario,
        Date fechacorte,
        String codigo)
                    throws SystemException;

    int calcularRecargosAcuerdo(
        String compania,
        String codigoacuerdo,
        String interescompuesto,
        Date fechacorte)
                    throws SystemException;

    String consultarTasaInteresVigente(
        String compania)
                    throws SystemException;

    void manejarPagoAnticipado(
        String compania,
        String codigoAcuerdo,
        int acuerdo,
        int recargo)
                    throws SystemException;

    int crearAcuerdo(
        String compania,
        String nombrecompania,
        String predio,
        int periodo,
        String numeroorden,
        String idres,
        String nombreres,
        String direccionres,
        String telefonores,
        int ncuotas,
        BigDecimal interes,
        BigDecimal recargo,
        String resolucion,
        String usuario,
        String recsoporte,
        boolean aplicadscesp,
        boolean preeliminar,
        boolean indabonoinicial,
        String vlrabonoinicial,
        String nitcompania,
        boolean acuerdopasto)
                    throws SystemException;

    void calcularAcuerdo(
        String compania,
        String acuerdo,
        int periodo,
        String tacuerdo,
        String tfacturadosacu,
        String interescompuesto,
        String llamadopor,
        boolean indabonoinicial,
        String vlrabonoinicial,
        String nitcompania,
        boolean acuerdopasto,
        int anomenor,
        int anomayor)
                    throws SystemException;

    void reCalcularCapitalAc(
        String compania,
        String codigoacuerdo,
        int periodo,
        String tacuerdo,
        String tfacturadosacu,
        boolean manejacompuesto)
                    throws SystemException;

    Date optenerFechaFinalAcuerdo(
        String compania,
        Date fechaacuerdo,
        BigDecimal ncuotas,
        BigDecimal periodicidad)
                    throws SystemException;

    BigDecimal obtenerFechaFinalAcuerdo()
                    throws SystemException;

    void distribuirAcuerdo(
        String compania,
        String predio,
        String acuerdo,
        int preanoi,
        int preano,
        String tFacturadosacu)
                    throws SystemException;

    int distribuirAcuerdoCapitalInteres(
        String compania,
        String predio,
        String acuerdo,
        int preanoi,
        int preano,
        String tFacturadosacu,
        boolean escapital)
                    throws SystemException;

    void cargarInformacionExcedentes(
        String compania,
        String usuario,
        String numeroorden,
        String factura,
        String predio,
        String anocausoexcedente,
        String anoaplicarexcedente,
        String banco,
        String observaciones,
        String c1,
        String c2,
        String c3,
        String c4,
        String c13,
        String c14,
        String c15,
        String c16,
        String c17,
        String c18,
        String c19,
        String c20)
                    throws SystemException;
}
