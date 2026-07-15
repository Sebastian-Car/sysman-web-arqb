package com.sysman.contratos.ejb.impl;

import com.sysman.contratos.ejb.EjbContratosUnoGeneralRemote;
import com.sysman.contratos.ejb.EjbContratosUnoLocal;
import com.sysman.contratos.ejb.EjbContratosUnoRemote;
import com.sysman.exception.SystemException;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class ContratosUno
 */
@Stateless
@LocalBean
public class EjbContratosUno
                implements EjbContratosUnoRemote, EjbContratosUnoLocal {

    @EJB
    private EjbContratosUnoGeneralRemote ejbContratosUnoGeneralRemote;

    /**
     * Default constructor.
     */
    public EjbContratosUno() {
    }

    @Override
    public void insertarSectoresDefault(
        String compania,
        String usuario)
                    throws SystemException {
        ejbContratosUnoGeneralRemote.insertarSectoresDefault(compania, usuario);
    }

    @Override
    public BigDecimal getTotalValorNovedad(
        String compania,
        String claseorden,
        long ordendecompra,
        String clasenovedad)
                    throws SystemException {
        return ejbContratosUnoGeneralRemote.getTotalValorNovedad(compania,
                        claseorden, ordendecompra, clasenovedad);
    }

    @Override
    public String anularOrdendeCompra(
        String compania,
        String tipo,
        long numero,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPO              =>'", tipo, "', ",
                                "UN_NUMERO            =>",
                                Long.toString(numero), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_VERIFICACON_ANULACION",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String enviarNovedadesaNomina(
        String compania,
        String novedad,
        String ordenDeCompra,
        BigDecimal valorTotalNovedad,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_NOVEDAD           =>'", novedad, "', ",
                                "UN_ORDENDECOMPRA     =>'", ordenDeCompra,
                                "', ", "UN_VALORTOTALNOVEDAD =>",
                                valorTotalNovedad.toString(), ", ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_ENVIAR_NOVEDADES_A_NOMINA",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getDiasMinuta(
        Date fecha)
                    throws SystemException {
        try {
            String[] parametros = { "UN_FECHA             =>TO_DATE('",
                                    SysmanFunciones.convertirAFechaCadena(
                                                    fecha),
                                    "','DD/MM/YYYY')"
            };
            return (String) AccionesImp.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_CONTRATOS_COM1.FC_FORMAT_FECHA_MINUTAS",
                            SysmanFunciones.concatenar(parametros),
                            Types.VARCHAR);
        }
        catch (ParseException e) {
            throw new SystemException(e);
        }
    }

    @Override
    public String getPoliza(
        String compania,
        String claseorden,
        long ordendecompra,
        long tipo)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseorden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordendecompra), ", ",
                                "UN_TIPO              =>", Long.toString(tipo),
                                ""
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_POLIZAS",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getPolizaResolucion(
        String compania,
        String claseorden,
        long ordendecompra)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseorden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordendecompra)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_POLIZASRESOLUCIONAPRO",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getPolizaResolucion2(
        String compania,
        String claseorden,
        long ordendecompra)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseorden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordendecompra)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_POLIZASRESOLUCIONAPRO2",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getDetalleSupervisorCargo(
        String compania,
        String claseorden,
        long ordendecompra)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseorden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordendecompra)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_DETALLECARGOSUPERVISORES",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getDetalleSupervisorCedula(
        String compania,
        String claseorden,
        long ordendecompra)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseorden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordendecompra)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_DETALLECEDSUPERVISORES",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getDetalleSupervisorNombre(
        String compania,
        String claseorden,
        long ordendecompra)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseorden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordendecompra)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_DETALLENOMBRESUPERVISORES",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public BigDecimal getConsecutivoOrdenCompra(
        String compania,
        String tipoafectado,
        long numeroafectado)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOAFECTADO      =>'", tipoafectado, "', ",
                                "UN_NUMEROAFECTADO    =>",
                                Long.toString(numeroafectado)
        };
        return (BigDecimal) AccionesImp.ejecutarFuncion(
                        ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_CONSEC_ADICIONES",
                        SysmanFunciones.concatenar(parametros),
                        Types.DECIMAL);
    }

    @Override
    public String getResolucionIndividual(
        String compania,
        String claseorden,
        long ordendecompra)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseorden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordendecompra)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_POLIZASRESOLUCIONINDIV",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getDetalleSupervisorContrato(
        String compania,
        String claseorden,
        long ordendecompra)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseorden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordendecompra)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_DETALLECONTSUPERVISORES",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getDetalleSupervisorProfesion(
        String compania,
        String claseorden,
        long ordendecompra)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_CLASEORDEN        =>'", claseorden, "', ",
                                "UN_ORDENDECOMPRA     =>",
                                Long.toString(ordendecompra)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_DETALLEPROFSUPERVISORES",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public String getDetalleResultado(
        String compania,
        String tipocontrato,
        long numero)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_TIPOCONTRATO      =>'", tipocontrato, "', ",
                                "UN_NUMERO            =>",
                                Long.toString(numero)
        };
        return (String) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.FC_DETALLERESULTADOS(",
                        SysmanFunciones.concatenar(parametros),
                        Types.VARCHAR);
    }

    @Override
    public BigDecimal ConsecContrato(
        String compania,
        String clasecontrato,
        int aniovigencia,
        String usuario)
                    throws SystemException {
        return ejbContratosUnoGeneralRemote.ConsecContrato(compania,
                        clasecontrato, aniovigencia, usuario);
    }

    @Override
    public void importarPrecontractual(
        String compania,
        long numeroOrden,
        String claseOrden,
        long estudioPrevio,
        String usuario,
        String numProceso)
                    throws SystemException {
        ejbContratosUnoGeneralRemote.importarPrecontractual(compania,
                        numeroOrden, claseOrden, estudioPrevio, usuario,
                        numProceso);
    }

    @Override
    public String extraerValores(
        String compania,
        String claseorden,
        long numero,
        BigDecimal valorfinal,
        String usuario)
                    throws SystemException {

        return ejbContratosUnoGeneralRemote.extraerValores(compania, claseorden,
                        numero, valorfinal, usuario);

    }

    @Override
    public String copiarContrato(
        String compania,
        String claseorden,
        long copiarde,
        int vigencia,
        long numero,
        String usuario)
                    throws SystemException {

        return ejbContratosUnoGeneralRemote.copiarContrato(compania, claseorden,
                        copiarde,
                        vigencia, numero, usuario);

    }

    @Override
    public void seleccionarRequisiciones(
        String compania,
        String usuario)
                    throws SystemException {
        String[] parametros = { "UN_COMPANIA          =>'", compania, "', ",
                                "UN_USUARIO           =>'", usuario, "'"
        };
        AccionesImp.ejecutarProcedimiento(ConectorPool.ESQUEMA_SYSMAN,
                        "PCK_CONTRATOS_COM1.PR_SELECCIONARREQUISICIONES",
                        SysmanFunciones.concatenar(parametros));
    }

    @Override
    public void insertaPpto(
        String compania,
        String claseorden,
        long numero,
        String clasedisp,
        long numerodispsel,
        String fechaselec,
        String tercero,
        String sucursal,
        String usuario)
                    throws SystemException {
        ejbContratosUnoGeneralRemote.insertaPpto(compania, claseorden, numero,
                        clasedisp,
                        numerodispsel, fechaselec, tercero, sucursal, usuario);
    }

    @Override
    public boolean actualizaIvaDetalle(
        String compania,
        String claseOrden,
        long numero,
        BigDecimal porcIvaGlobal,
        String roundValorIvaoc,
        String roundVlrTotaloc,
        String roundValorUnioc,
        BigDecimal digRedoVluniIva,
        BigDecimal digRoundVlrIva,
        BigDecimal digRedonTotal,
        String usuario)
                    throws SystemException {
        return ejbContratosUnoGeneralRemote.actualizaIvaDetalle(compania,
                        claseOrden,
                        numero, porcIvaGlobal, roundValorIvaoc, roundVlrTotaloc,
                        roundValorUnioc, digRedoVluniIva, digRoundVlrIva,
                        digRedonTotal, usuario);
    }

    @Override
    public BigDecimal calculartotalpagos(
        String compania,
        String claseorden,
        long numero,
        String clasecontable)
                    throws SystemException {

        return ejbContratosUnoGeneralRemote.calculartotalpagos(compania,
                        claseorden, numero, clasecontable);

    }

    @Override
    public boolean eliminarOrdendeCompra(
        String compania,
        String claseorden,
        long numero)
                    throws SystemException {

        return ejbContratosUnoGeneralRemote.eliminarOrdendeCompra(compania,
                        claseorden, numero);

    }

}