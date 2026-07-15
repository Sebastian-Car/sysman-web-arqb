package com.sysman.contabilidad.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbContabilidadCuatroLocal {

    String revisarDeterioroDeCartera(
        String compania,
        Date fechacorte,
        int ano,
        String terceroinicial,
        String tercerofinal,
        int mesesvencidos, String usuario)
                    throws SystemException;

    boolean reversarCierreContable(
        String compania,
        int ano,
        String tipocpte,
        BigInteger nrocpte,
        String modulo)
                    throws SystemException;

    String generarComprobanteContableDeterioroDeCartera(
        String companiaSeleccionada, String compania, int ano, Date fechaCorte,
        String descripcion, int mesesVencidos, String terceroInicial,
        String terceroFinal, String usuario) throws SystemException;

    BigDecimal consultarTasaDeInteres(
        Date fecha)
                    throws SystemException;

    BigInteger generarConsecutivoComprobanteDeterioro(
        String compania,
        String tipo,
        int anio)
                    throws SystemException;

    String revisarFacturasCanceladasDeterioroDeCartera(
        String companiaseleccionada,
        String compania,
        Date fechacorte,
        int ano,
        int mesesvencidos,
        String terceroinicial,
        String tercerofinal,
        String descripcion,
        String usuario)
                    throws SystemException;

    boolean GenerarPacProgramado(
        String compania,
        int ano,
        String tipo,
        BigInteger numero,
        Date fechacomprobante)
                    throws SystemException;

    boolean generarPacProporcionalAlGiro(
        String compania,
        int anio,
        String tipo,
        BigInteger numero,
        int mes,
        Date datfechacomprobante,
        BigDecimal dbltotalgiro,
        String usuario)
                    throws SystemException;

    boolean generarPacProporcionalAlGiroSinOrdenDePago(
        String compania,
        int anio,
        String tipo,
        BigInteger numero,
        Date fechacomprobante,
        BigDecimal totalgiro,
        Date fechainicial,
        Date fechafinal,
        String tercero,
        String sucursal)
                    throws SystemException;

    String generarPacTesoreria(
        String compania,
        int anio,
        String tipo,
        BigInteger numero,
        Date fecha,
        String usuario)
                    throws SystemException;

    String crearPacDeIngresos(
        String compania,
        int ano,
        String tipo,
        BigInteger numero,
        Date fechacpte,
        String usuario)
                    throws SystemException;

    void actualizaRetencionesBancos(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String bancoinicial,
        String bancofinal, String usuario)
                    throws SystemException;

    String cierreContable(
        String compania,
        String tipocomprobante,
        BigInteger numero,
        int anio,
        Date fecha,
        int mes,
        String centroCosto,
        String usuario,
        boolean generaCompDesc)
                    throws SystemException;

    String interfazContableAct(
        String compania,
        String tipocomprobante,
        BigInteger numero,
        int anio,
        int mes,
        Date fecha,
        String tercero,
        String sucursal,
        String centroCosto,
        String descripcion,
        String creador,
        boolean simple,
        boolean indimpresion,
        boolean plano,
        boolean generaCompDesc,
        String texto)
                    throws SystemException;

    String interfazContable(
        String compania,
        String tipocomprobante,
        BigInteger numero,
        int anio,
        int mes,
        Date fecha,
        String tercero,
        String sucursal,
        String descripcion,
        String creador,
        boolean simple,
        boolean verificaauxiliar,
        String texto)
                    throws SystemException;

    String cierreContabledeImpuestos(
        String compania,
        String tipocomprobante,
        BigInteger numero,
        int anio,
        Date fecha,
        int mes,
        String centroCosto,
        String usuario)
                    throws SystemException;

    boolean verificarPeriodo(String compania, int ano, int mes)
                    throws SystemException;

    boolean verificarCuentas(String compania, int ano,
        String tipocomprobante) throws SystemException;

    boolean cierreContableValidado(String compania, String tipocomprobante,
        BigInteger numero, int anio, Date fecha, int mes, String centroCosto,
        boolean generaCompDesc, boolean cierreutilidad, boolean cierreimpuestos,
        String usuario) throws SystemException;

    String deterioroCuentaH(String compania, int ano, int mes, Date fechacorte,
        String usuario, String funcion) throws SystemException;

    String contabilizarDeterioro(String compania, int ano, int mes,
        Date fechacorte, String usuario) throws SystemException;

    String reversarDeterioro(String compania, int ano, int mes, Date fechacorte,
        String usuario) throws SystemException;

    void actualizar3110(String compania, int aniotrabajo, int aniocomparar,
        int mestrabajo, int mescomparar, boolean ajustando, String usuario)
                    throws SystemException;
}