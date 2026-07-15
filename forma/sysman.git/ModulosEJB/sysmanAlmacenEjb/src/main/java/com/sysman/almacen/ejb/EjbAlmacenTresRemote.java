package com.sysman.almacen.ejb;

import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbAlmacenTresRemote {
    String analizarInconsistenciasKardex(
        String compania,
        String tipomovimiento,
        long movimiento,
        String elemento,
        long codigo,
        String actpromedio,
        int ano,
        int mes)
                    throws SystemException;

    String obtenerInconsistenciasKardex(
        String compania,
        int intanoinicial,
        int intmesinicial,
        int intanofinal,
        int intmesfinal,
        String strelementoinicial,
        String strelementofinal,
        int kardexgeneral,
        Date fechaInicial,
        Date fechaFinal,
        String tipoMov,
        long movimiento)
                    throws SystemException;

    String obtenerNumeroRequisiciones(
        String compania,
        String tipoorden,
        long numorden)
                    throws SystemException;

    boolean revisarHoras(
        String compania,
        String elementoini,
        String elementofin,
        long placainicial,
        long placafinal)
                    throws SystemException;

    void revisarHoraH(
        String compania)
                    throws SystemException;

    String obtenerCedulaResponsable(
        String compania)
                    throws SystemException;

    void revisarFechasSalidaServicio(
        String compania,
        String elementoinicial,
        String elementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException;

    String revisarRegistroDepreciacion(
        String compania,
        int anioinicial,
        int mesinicial,
        int aniofinal,
        int mesfinal,
        String elementoinicial,
        String elementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException;

    String revisarPlacasSinMovimiento(
        String compania,
        int anioinicial,
        int mesinicial,
        int aniofinal,
        int mesfinal,
        String elementoinicial,
        String elementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException;

    String revisarDevolutivos(
        String compania,
        int anioinicial,
        int mesinicial,
        String elementoinicial,
        long serieinicial,
        int aniofinal,
        int mesfinal,
        String elementofinal,
        long seriefinal,
        String usuario)
                    throws SystemException;

    void rectificarDevolutivos(
        String compania,
        long placainicial,
        long placafinal)
                    throws SystemException;

    void actualizarValor(
        String compania,
        String elementoinicial,
        String elementofinal,
        long placainicial,
        long placafinal)
                    throws SystemException;

    void actualizarDependencia(
        String compania,
        String elementoinicial,
        String elementofinal,
        long placainicial,
        long placafinal,
        int mesfinal,
        int aniofinal)
                    throws SystemException;
}
