package com.sysman.almacen.ejb;

import com.sysman.exception.SystemException;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Local;

@Local
public interface EjbAlmacenCeroLocal
{

    String hallarPredecesor(
        String compania,
        String campo,
        String valorCampo,
        String tabla,
        int opcion)
                    throws SystemException;

    String verificarNombreDevolutivo(
        int opcion,
        int concodigo,
        String codigo,
        String compania)
                    throws SystemException;

    String verificarPlaca(String compania)
                    throws SystemException;

    String generarConsecutivoPolizas(
        String nombretabla,
        String condicion,
        String nombrecampo,
        String inicial)
                    throws SystemException;

    BigDecimal guardarPoliza(
        String compania,
        String aseguradora,
        String numpoliza,
        String sucursal,
        Date fechai,
        Date fechaf,
        String grupo,
        String elemento,
        String placa,
        String riesgo,
        String usuario)
                    throws SystemException;

    String actualizarElementoInventario(
        String compania,
        String codigoant,
        String codigonuevo,
        String tipocambio)
                    throws SystemException;

    void revisarAfectacionMovimiento(
        String compania,
        String tipo,
        long numero)
                    throws SystemException;

    void reversarRequisicion(
        String compania,
        long numero,
        String usuario)
                    throws SystemException;

    void cambiarHoraMovimiento(
        String compania,
        String tipomovimiento,
        long numero,
        String horanueva,
        String formatohora,
        String usuario)
                    throws SystemException;

    void cambiarFechaMovimiento(
        String compania,
        String tipomovimiento,
        long numero,
        Date fechaactual,
        String usuario)
                    throws SystemException;

    void revisarDevolutivo(
        String compania,
        long placainicial,
        long placafinal)
                    throws SystemException;

    String retornarResonsable(
        String compania,
        String dependencia,
        int modulo)
                    throws SystemException;

    String consultarNombreInventario(
        String compania,
        String elemento,
        boolean opcion,
        boolean nomCorto)
                    throws SystemException;

    Date consultarUltimoDeFechaSalida(
        String compania,
        String elemento,
        Date fecha)
                    throws SystemException;

    boolean validarTransaccionAlmHW(
        String compania,
        String bodegaorigen,
        String bodegadestino)
                    throws SystemException;

    void grabarProveedoresGeneral(
        String compania,
        String tipomovimiento,
        String numero)
                    throws SystemException;

    boolean crearInterfaceAlmacenUnoAUno(
        String compania,
        String tipomovimiento,
        long numero,
        Date fecha,
        String descripcion,
        int digitos,
        String tercero,
        String sucursal,
        String centrocosto,
        String creador,
        BigDecimal valordocumento)
                    throws SystemException;

    boolean grabarDevolutivos(
        String compania,
        String tipomovimiento,
        long numero,
        String tipo,
        String dependenciaDestino,
        String responsableDestino,
        String sucursalResponsable,
        boolean inventarioInicial,
        Date fecha)
                    throws SystemException;

    String revisarPEPs(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String elementoinicial,
        long placainicial,
        boolean actualizar,
        boolean informar)
                    throws SystemException;

    boolean revisarSiHayMovimiento(
        String compania,
        String tipomovimiento,
        String tipomov,
        long movimiento)
                    throws SystemException;

    String consultarDatosBodegaH(
        String compania,
        int opcion)
                    throws SystemException;

    String consultarAlCambiarValorMovimiento(
        String compania,
        String tipomov,
        long nummov)
                    throws SystemException;

    void revisarPEPsPRocedimiento(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String elementoinicial,
        BigDecimal saldoq)
                    throws SystemException;

    String insertarDetalles(
        String compania,
        Date fechainicial,
        Date fechafinal,
        String elementoinicial,
        BigDecimal cantidad,
        long codigo,
        long serie,
        String tipomovimiento,
        long movimiento,
        String especificacion,
        String tercero,
        String sucursal,
        BigDecimal valorunitario,
        BigDecimal valortotal,
        BigDecimal porciva,
        BigDecimal valorunitarioAntesiva)
                    throws SystemException;

    BigDecimal buscarExistencia(
        String compania,
        String elemento)
                    throws SystemException;

    String devolverSaldoPEPS(
        String compania,
        String tipomovimiento,
        String tipomovimientoAfect,
        String elemento,
        long movimiento,
        long codigo,
        BigDecimal cantidadNueva,
        BigDecimal cantidadAfectada,
        long serie)
                    throws SystemException;

    String ejecutarCorreccionDeValor(
        String compania,
        String tipomovimiento,
        long movimiento)
                    throws SystemException;

    void generarKardexPorSaldoPEPS(String compania)
                    throws SystemException;

    boolean controlarMovimientosPosteriores(
        String compania,
        String tipomovimiento,
        Date fecha,
        String elemento,
        long serie)
                    throws SystemException;

    boolean actualizarDevolutivo(
        String compania,
        String elemento,
        long serie)
                    throws SystemException;

    BigDecimal actualizarValorTotal(
        String compania,
        String tipomovimiento,
        long movimiento,
        BigDecimal porciva,
        BigDecimal valorunitarioAntesiva,
        BigDecimal cantidadNueva,
        long codigo)
                    throws SystemException;

    String reversarMovimiento(
        String compania,
        String tipomovimiento,
        long movimiento)
                    throws SystemException;

    String verificarSaldoDisponible(
        String clase,
        String compania,
        int anio,
        Date fecha,
        String codigo,
        String tercero,
        String sucursal,
        String auxiliar,
        String centro,
        String referencia,
        String fuenterecurso,
        BigDecimal debitoant,
        BigDecimal creditoant,
        BigDecimal debito,
        BigDecimal credito)
                    throws SystemException;

    boolean verificarEstadoAlmacen(String compania, int ano, int mes)
                    throws SystemException;

    String actualizarSaldoPeps(
        String compania,
        int anioinicial,
        int mesinicial,
        int aniofinal,
        int mesfinal,
        String elementoinicial,
        String elementofinal)
                    throws SystemException;
}