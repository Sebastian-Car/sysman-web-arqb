package com.sysman.serviciospublicos.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbServiciosPublicosCeroRemote {
    String asignarNombrePeriodo(
        String compania,
        int ano,
        String periodo,
        String frecuencia)
                    throws SystemException;

    String cambiarFechaPago(
        String compania,
        String bancoant,
        Date fechaant,
        String paqueteant,
        String banconue,
        Date fechanue,
        String paquetenue,
        String usuario)
                    throws SystemException;

    String insertarAuditoriaSinLectura(
        String compania,
        String usuario,
        String proceso,
        String ciclo)
                    throws SystemException;

    void reasignarConceptoDiferenteDeDoce(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String usuario)
                    throws SystemException;

    void actualizarDetalladeDeAbonos(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String usuario)
                    throws SystemException;

    boolean insertarAuditoriaDelUsuario(
        String compania,
        int ciclo,
        String codigo,
        boolean valfinal,
        boolean valinicial,
        String campoActual,
        String campoAnterior,
        String periodo, String usuario)
                    throws SystemException;

    void actualizarDetalleDeRecaudo(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String usuario)
                    throws SystemException;

    void reasignarDetalleDelRecaudo(
        String compania,
        Date fechainicial,
        Date fechafinal, String usuario)
                    throws SystemException;

    void actualizarDetalleDelRecuadoEntreFechas(
        String compania,
        Date fechainicial,
        Date fechafinal, String usuario)
                    throws SystemException;

    void pasarAbonosDeRecuados(
        String compania,
        Date fecha,
        String banco,
        String usuario)
                    throws SystemException;

    void validarBancoAbonos(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String usuario)
                    throws SystemException;

    void ajustarPesoDeRecaudo(
        String compania,
        Date fechainicial,
        Date fechafinal, String usuario)
                    throws SystemException;

    String escribirPeriodo(
        String compania,
        BigDecimal periodo,
        int ano)
                    throws SystemException;

    void controlarCopia(
        String compania,
        int ciclo,
        String codigo,
        String tipo,
        String aplica,
        String timpresion,
        String user)
                    throws SystemException;

    boolean autorizarMicromedicion(
        String compania,
        String nit)
                    throws SystemException;

    boolean generarMicroconsumos(
        String compania,
        int ciclo,
        String strperiodo,
        int intano)
                    throws SystemException;

    String prepararAnoPeriodoSiguiente(
        String compania,
        int ano,
        String periodo,
        String tipoRetorno,
        String frecuencia)
                    throws SystemException;

    String prepararAnoPeriodoAnterior(
        String compania,
        int ano,
        String periodo,
        String tipoRetorno,
        String frecuencia)
                    throws SystemException;

    String prepararPeriodoSiguiente(
        String compania,
        int intano,
        String strperiodo,
        int numperiodos,
        String frecuencia,
        int modulo)
                    throws SystemException;

    String prepararAnoSiguiente(
        String compania,
        int intano,
        String strperiodo,
        int numperiodos,
        String frecuencia,
        int modulo)
                    throws SystemException;

    String prepararCritica(
        String compania,
        int modulo,
        int ciclo,
        String strcodigoini,
        String strcodigofin,
        String consumoMenor,
        int ano,
        int periodo,
        double porcMenor,
        double porcMayor,
        boolean normales,
        boolean manual,
        boolean iguales,
        boolean desviacion,
        String usuario,
        boolean reporte)
                    throws SystemException;

    String asignarNombreUso(
        String compania,
        String codigo)
                    throws SystemException;

    void actualizarFrecuencia(String compania, int ciclo, String codini,
        String codfin, String actividad, BigDecimal frecuencia, String usuario)
                    throws SystemException;

    void desactivarServicioAseoUrbano(String compania, int ciclo,
        String codigoruta, int anio, String periodo, String usuario)
                    throws SystemException;

    void activarServicioAseoUrbano(String compania, int ciclo,
        String codigoruta, int anio, String periodo, String usuario)
                    throws SystemException;

    int actualizarChapetas(String compania, int ciclo, String codigoinicial,
        String codigofinal, String usuario) throws SystemException;
}
