package com.sysman.contratos.ejb;

import com.sysman.exception.SystemException;

import java.util.Date;

import javax.ejb.Remote;

@Remote
public interface EjbContratosCeroRemote {

    String getPolizaNumero(
        String claseorden,
        long ordendecompra,
        String compania)
                    throws SystemException;

    String getPolizaFecha(
        String claseorden,
        long ordendecompra,
        int nufecha,
        String compania)
                    throws SystemException;

    String getPolizaEstado(
        String claseorden,
        long ordendecompra,
        String compania)
                    throws SystemException;

    String getPivotDependencias(
        String compania,
        int anioinicial,
        int aniofinal,
        String dependencia)
                    throws SystemException;

    String getPlanoComercio(String compania, String cargo, int semestre,
        int anio, String camComercio, int conProponentes, int secXReg,
        String nit, String nombre, String ciudad, String direccion,
        String funcionario, Date fecha, int modulo, String usuario)
                    throws SystemException;

    String getNovedadDatoContratos(
        String claseorden,
        long ordendecompra,
        int opcion,
        String tiponovedad,
        String compania)
                    throws SystemException;

    String actualizaFuentesOrdenCompra(
        String compania,
        int anio,
        String usuario)
                    throws SystemException;

    String actualizarPagosOrdenCompra(
        String compania,
        Date fechacorte,
        String tipocontratoini,
        String tipocontratofin,
        int modulo,
        Date fechapar,
        String usuario)
                    throws SystemException;

    void actualizarPlanDeCompras(
        String compania,
        String claseorden,
        long numeroini,
        long numerofin,
        int modulo,
        int ano,
        String usuario)
                    throws SystemException;

    void actualizarImpresoPlanDeComprasOrdenDeCompra(
        String compania,
        String claseorden,
        long numeroini,
        long numerofin,
        String usuario)
                    throws SystemException;

    void afectarItems(String compania, long numeroAfectado, String tipoAfectado,
        long numeroAfectar, String tipoAfectar, String usuario)
                    throws SystemException;

    void importarPagos(String compania, String claseOrden, long ordenDeCompra,
        String tipoAfectado, long numeroAfectado, String usuario)
                    throws SystemException;

    void afectarPagos(String compania, String claseOrden, long ordenDeCompra,
        String tipoAfectado, long numeroAfectado, String usuario)
                    throws SystemException;

    boolean afectarPolizas(String compania, long numeroAfectado,
        String tipoAfectado, long numeroAfectar, String tipoAfectar,
        String usuario) throws SystemException;

    void cambiarConsecutivoContrato(String compania, String claseContrato,
        int anioVigencia, long anteriorConsec, long nuevoConsec, String usuario)
                    throws SystemException;
}