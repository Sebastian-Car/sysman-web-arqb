package com.sysman.recursos.ejb.impl;

import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbGeneralesLocal;
import com.sysman.recursos.ejb.EjbGeneralesRemote;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class EjbGenerales
 */
@Stateless
@LocalBean
public class EjbGenerales implements EjbGeneralesRemote, EjbGeneralesLocal {
    /**
     * Default constructor.
     */
    public EjbGenerales() {
    }

    @Override
    public void calcularPaag(
        String compania,
        int ano)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_GENERALES.PR_CALCULARPAGG",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ANO               =>'" + ano + "'");
    }

    @Override
    public String consultarNombreElemento(
        String codelemento,
        String compania,
        Boolean opcion)
                    throws SystemException {
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_GENERALES.FC_NOMBREINVENTARIO",
                        "UN_CODELEMENTO       =>'" + codelemento + "', "
                            + "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_OPCION            =>" + (opcion ? "-1" : "0")
                            + "",
                        Types.VARCHAR);
    }

    @Override
    public String consultarEstadoDeVigencia(
        String compania,
        int anio,
        String parametro)
                    throws SystemException {
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_GENERALES.FC_VERIFICAR_ESTADOVIGENCIA",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ANIO              =>" + anio + ", "
                            + "UN_PARAMETRO         =>'" + parametro + "'",
                        Types.VARCHAR);
    }

    @Override
    public BigDecimal actualizarCampoEnInventario(
        String compania,
        String elemento,
        BigDecimal cantidad,
        String campo)
                    throws SystemException {
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_GENERALES.FC_ACTRES",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ELEMENTO          =>'" + elemento + "', "
                            + "UN_CANTIDAD          =>" + cantidad + ", "
                            + "UN_CAMPO             =>'" + campo + "'",
                        Types.DECIMAL);
    }

    @Override
    public BigDecimal consultarEjecucionPresupuestal(
        String compania,
        String columna,
        int anio,
        String id,
        int mes)
                    throws SystemException {
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_GENERALES.FC_SALDOPPTAL",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COLUMNA           =>'" + columna + "', "
                            + "UN_ANIO              =>" + anio + ", "
                            + "UN_ID                =>'" + id + "', "
                            + "UN_MES               =>" + mes + "",
                        Types.DECIMAL);
    }

    @Override
    public boolean afectarNovedadEnContratacion(
        String compania,
        String smodulo,
        String stiponovedad,
        int lano,
        long dnumero,
        Date dfechainicial,
        Date dfechafinal,
        Date dfechavencimiento,
        BigDecimal dvalor,
        String stipocontrato,
        long dnumerocontrato,
        String usuario)
                    throws SystemException {
        int salida;

        try {
            salida = (int) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_GENERALES.FC_DISPARANOVEDAD",
                            "UN_COMPANIA          =>'" + compania + "', "
                                + "UN_SMODULO           =>'" + smodulo + "', "
                                + "UN_STIPONOVEDAD      =>'" + stiponovedad
                                + "', "
                                + "UN_LANO              =>" + lano + ", "
                                + "UN_DNUMERO           =>" + dnumero + ", "
                                + "UN_DFECHAINICIAL     =>'"
                                + SysmanFunciones.convertirAFechaCadena(
                                                dfechainicial)
                                + "', "
                                + "UN_DFECHAFINAL       =>'"
                                + SysmanFunciones.convertirAFechaCadena(
                                                dfechafinal)
                                + "', "
                                + "UN_DFECHAVENCIMIENTO =>'"
                                + SysmanFunciones.convertirAFechaCadena(
                                                dfechavencimiento)
                                + "', "
                                + "UN_DVALOR            =>" + dvalor + ", "
                                + "UN_STIPOCONTRATO     =>'" + stipocontrato
                                + "', "
                                + "UN_DNUMEROCONTRATO   =>" + dnumerocontrato
                                + ","
                                + "UN_USUARIO           =>'" + usuario
                                + "'" ,
                            Types.INTEGER);
            return salida == 0 ? false : true;
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String retornarNombreCompania(
        String compania)
                    throws SystemException {
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_GENERALES.FC_NOMBRECOMPANIA",
                        "UN_COMPANIA          =>'" + compania + "'",
                        Types.VARCHAR);
    }

    @Override
    public int actDatosErrorGeneral(
        String tabla,
        String campo,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_TABLA            =>'", tabla, "', ",
                                "UN_CAMPO            =>'", campo, "', ",
                                "UN_USUARIO          =>'", usuario, "'" };
        return (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_GENERALES.FC_ACT_DATOSERROR_GENERAL",
                        SysmanFunciones.concatenar(parametros),
                        Types.INTEGER);
    }

    @Override
    public void registrarSolicitud(
        String compania,
        long orden,
        String campo,
        String claseBodegaAlmacen,
        String claseBodega)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_GENERALES.PR_REGISTRAR_SOLICITUD",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ORDEN             =>" + orden + ", "
                            + "UN_CAMPO             =>'" + campo + "', "
                            + "UN_CLASE_BODEGA_ALMACEN =>'" + claseBodegaAlmacen
                            + "', "
                            + "UN_CLASE_BODEGA      =>'" + claseBodega + "'");
    }

    @Override
    public boolean cambiarRequisicion(
        String compania,
        long orden,
        long numeroAnterior,
        String usuario,
        Date fecha,
        String dependencia,
        String tercero,
        String sucursal,
        BigDecimal valorestimado,
        String descripcion,
        String observaciones,
        long plazo,
        String unidadTiempo,
        String periodicidad,
        long numeroEntregas,
        String claseBodega,
        String auxiliar)
                    throws SystemException {
        byte salida;
        try {
            salida = (byte) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_GENERALES.FC_CAMBIAR_REQUISICION",
                            "UN_COMPANIA          =>'" + compania + "', "
                                + "UN_ORDEN             =>" + orden + ", "
                                + "UN_NUMERO_ANTERIOR   =>'" + numeroAnterior
                                + "', "
                                + "UN_USUARIO           =>'" + usuario + "', "
                                + "UN_FECHA             =>'"
                                + SysmanFunciones.convertirAFechaCadena(fecha)
                                + "', "
                                + "UN_DEPENDENCIA       =>'" + dependencia
                                + "', "
                                + "UN_TERCERO           =>'" + tercero + "', "
                                + "UN_SUCURSAL          =>'" + sucursal + "', "
                                + "UN_VALORESTIMADO     =>" + valorestimado
                                + ", "
                                + "UN_DESCRIPCION       =>'" + descripcion
                                + "', "
                                + "UN_OBSERVACIONES     =>'" + observaciones
                                + "', "
                                + "UN_PLAZO             =>" + plazo + ", "
                                + "UN_UNIDAD_TIEMPO     =>'" + unidadTiempo
                                + "', "
                                + "UN_PERIODICIDAD      =>'" + periodicidad
                                + "', "
                                + "UN_NUMERO_ENTREGAS   =>" + numeroEntregas
                                + ", "
                                + "UN_CLASE_BODEGA      =>'" + claseBodega
                                + "', "
                                + "UN_AUXILIAR          =>'" + auxiliar + "'",
                            Types.TINYINT);
            return salida != 0;
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public void registrarDetalleOrdenDeCompra(
        String compania,
        int orden,
        String claseorden,
        BigDecimal porcdescglobal,
        BigDecimal porcivaglobal,
        boolean esModificacionContratos,
        String usuario)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_GENERALES.PR_REGISTRAR_DET_ORDENDECOMPRA",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_ORDEN             =>" + orden + ", "
                            + "UN_CLASEORDEN        =>'" + claseorden + "', "
                            + "UN_PORCDESCGLOBAL    =>" + porcdescglobal + ", "
                            + "UN_PORCIVAGLOBAL     =>" + porcivaglobal + ", "
                            + "UN_ES_MODIFICACION_CONTRATOS =>"
                            + (esModificacionContratos ? "-1" : "0") + ", "
                            + "UN_USUARIO           =>'" + usuario + "'");
    }

    @Override
    public void registrarDetalleRequisicion(
        String compania,
        BigInteger codRequisicion,
        BigInteger codDetalle,
        String usuario)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_GENERALES.PR_REGISTRAR_DETALLEREQUIS",
                        "UN_COMPANIA          =>'" + compania + "', "
                            + "UN_COD_REQUISICION   =>" + codRequisicion + ", "
                            + "UN_COD_DETALLE       =>" + codDetalle + ", "
                            + "UN_USUARIO           =>'" + usuario + "'");
    }

    @Override
    public void validarOrdenesDeSuministroVacias(
        String compania)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_GENERALES.PR_VALIDAR_ORDENDESUMIN_VACIAS",
                        "UN_COMPANIA          =>'" + compania + "'");
    }
    
    @Override
    public void copiarTerceroXCompania(
        String companiaOrigen,
        String companiaDestino)
                    throws SystemException {
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_GENERALES.PR_COPIAR_TERCEROSXCOMPANIA",
                        "UN_COMPANIA_ORIGEN     =>'" + companiaOrigen + "', "
                        + "UN_COMPANIA_DESTINO  =>'" + companiaDestino
                        + "'");
    }
}