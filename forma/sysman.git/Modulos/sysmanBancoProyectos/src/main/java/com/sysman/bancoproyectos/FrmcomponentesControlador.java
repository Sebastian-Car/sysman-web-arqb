package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmcomponentesControladorEnum;
import com.sysman.bancoproyectos.enums.FrmcomponentesControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author dmaldonado
 * @version 1, 31/08/2015
 * 
 * @author eamaya
 * @version 2.0, 14/09/2017, Proceso de Refactoring DSS, Manejo de
 * EJBs, cambio de numero de formulario por enum y cambio de
 * redireccionamiento
 * 
 */
@ManagedBean
@ViewScoped

public class FrmcomponentesControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String modulo;

    private List<Registro> listaUnidad;
    private List<Registro> listaTipoComponente;
    private List<Registro> listaVigencia;
    private String codigoProy;
    private String saldoEjecutar;
    private String saldoProgramar;
    private double valorProgramado;
    private double valorTotal;
    private double valorUnitario;
    private double saldoComponente;
    private double valorEjecutado;
    private double valorTotalSolicitado;
    private boolean conProgramacion;
    private Map<String, Object> ridProyecto;
    private double totalProyecto;
    private double totalComponentePrevio;
    private double validacion;
    private String anoIni;
    private String anoFin;
    private boolean muestraRegistro;
    private String menuActual;
    private String proyectoMonitor;
    private String dependenciaMonitor;
    private String vigenciaMonitor;
    private String estadoMonitor;
    private String idDependenciaMonitor;
    private String accionProyecto;
    private Map<String, Object> parametrosEntrada;
    private static final String VALORTOTALESS = "VALORTOTAL";
    private static final String VALORPROGRAMADOS = "VALORPROGRAMADO";
    private static final String VALORUNITARIOS = "VALORUNITARIO";
    private static final String CANTIDAD = "CANTIDAD";
    private static final String VALOREJECUTADOS = "VALOREJECUTADO";
    private static final String CODIGO = "CODIGO";

    /**
     * Constante que almacena la cadena "codigoProy"
     */
    private static final String CODIPROY = "codigoProy";

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    public FrmcomponentesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        menuActual = SessionUtil.getMenuActual();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMCOMPONENTES_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            menuActual();

            conProgramacion = false;
            registro = new Registro(new HashMap<String, Object>());

            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                codigoProy = (String) parametrosEntrada.get(CODIPROY);
                ridProyecto = (Map<String, Object>) parametrosEntrada
                                .get("ridProyecto");
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                anoIni = (String) parametrosEntrada.get("anoIni");
                anoFin = (String) parametrosEntrada.get("anoFin");
                proyectoMonitor = (String) parametrosEntrada
                                .get("proyectoMonitor");
                dependenciaMonitor = (String) parametrosEntrada
                                .get("dependenciaMonitor");
                vigenciaMonitor = (String) parametrosEntrada
                                .get("vigenciaMonitor");
                estadoMonitor = (String) parametrosEntrada.get("estadoMonitor");
                idDependenciaMonitor = (String) parametrosEntrada
                                .get("idDependenciaMonitor");
                accionProyecto = (String) parametrosEntrada.get("accion");
                parametrosEntrada.put("rid", ridProyecto);
                parametrosEntrada.remove(CODIPROY);
                parametrosEntrada.remove("ridProyecto");

                if (ridProyecto == null) {
                    SessionUtil.redireccionarMenuPermisos();
                    return;
                }

                if ("v".equals(accionProyecto) && (rid != null)) {
                    muestraRegistro = false;
                    accion = "v";

                }

            }
            else {
                ejecutarrcCerrar();
            }
        }
        catch (Exception ex) {
            Logger.getLogger(FrmcomponentesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    private void menuActual() {

        menuActual = menuActual == null ? "NULL" : menuActual;

        if ("52020102".equals(menuActual)) {
            muestraRegistro = false;
        }
        if ("52020101".equals(menuActual)) {
            muestraRegistro = true;
        }
        if ("52020402".equals(menuActual)) {
            muestraRegistro = false;
        }
        if ("NULL".equals(menuActual)) {
            SessionUtil.redireccionarMenu();
        }

    }

    @Override
    public void abrirFormulario() {

        cargarListaUnidad();
        cargarListatipoComponente();
        cargarListaVigencia();
        cargarValores();

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.COMPONENTES;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void iniciarListas() {
        // HEREDADO DEL BEAN BASE
    }

    @Override
    public void iniciarListasSub() {
        totalComponentePrevio = Double.valueOf(
                        registro.getCampos().get(VALORTOTALESS).toString());
        cambiarcantidad();
    }

    @Override
    public void iniciarListasSubNulo() {
        saldoEjecutar = null;
        saldoProgramar = null;
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(
                        FrmcomponentesControladorEnum.CODIGOPROYECTO.getValue(),
                        codigoProy);

    }

    public void cargarListaUnidad() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaUnidad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcomponentesControladorUrlEnum.URL8556
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListatipoComponente() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaTipoComponente = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcomponentesControladorUrlEnum.URL8991
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaVigencia() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        codigoProy);

        try {
            listaVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcomponentesControladorUrlEnum.URL9703
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private double cargarValores() {
        double res = 0;
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.CODIGO.getName(), codigoProy);

        List<Registro> listaValor;
        try {
            listaValor = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcomponentesControladorUrlEnum.URL10244
                                                                            .getValue())
                                            .getUrl(), param));

            valorProgramado = Double.parseDouble(
                            SysmanFunciones.nvl(listaValor.get(0).getCampos()
                                            .get(VALORPROGRAMADOS), "0")
                                            .toString());
            valorTotal = Double.parseDouble(
                            SysmanFunciones.nvl(listaValor.get(0).getCampos()
                                            .get(VALORTOTALESS), "0")
                                            .toString());
            valorUnitario = Double.parseDouble(
                            SysmanFunciones.nvl(listaValor.get(0).getCampos()
                                            .get(VALORUNITARIOS), "0")
                                            .toString());
            saldoComponente = Double.parseDouble(
                            SysmanFunciones.nvl(listaValor.get(0).getCampos()
                                            .get("SALDOCOMPONENTE"), "0")
                                            .toString());
            valorEjecutado = Double.parseDouble(
                            SysmanFunciones.nvl(listaValor.get(0).getCampos()
                                            .get(VALOREJECUTADOS), "0")
                                            .toString());
            valorTotalSolicitado = Double
                            .parseDouble(SysmanFunciones.nvl(listaValor.get(0)
                                            .getCampos()
                                            .get("VALORTOTALSOLICITADO"), "0")
                                            .toString());

            Registro registroTot;

            registroTot = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmcomponentesControladorUrlEnum.URL1111
                                                                            .getValue())
                                            .getUrl(), param));

            totalProyecto = Double
                            .parseDouble(SysmanFunciones
                                            .nvl(registroTot.getCampos()
                                                            .get(VALORTOTALESS),
                                                            "0")
                                            .toString());

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return res;
    }

    public void oprimirAsignarActividades1() {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> parametros = new TreeMap<>();

        parametros.put("ridProyecto", ridProyecto);
        parametros.put("ridComponente", registro.getLlave());
        parametros.put("anoIni", anoIni);
        parametros.put("anoFin", anoFin);
        parametros.put("proyectoMonitor", proyectoMonitor);
        parametros.put("dependenciaMonitor", dependenciaMonitor);
        parametros.put("vigenciaMonitor", vigenciaMonitor);
        parametros.put("estadoMonitor", estadoMonitor);
        parametros.put("idDependenciaMonitor", idDependenciaMonitor);
        parametros.put("accion", accionProyecto);
        parametros.put(CODIPROY, codigoProy);

        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMCOMPONENTESACTIVIDADES_CONTROLADOR
                                        .getCodigo()));

        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador, modulo);

    }

    public void cambiarVigencia() {
        if ((boolean) SysmanFunciones.nvl(
                        registro.getCampos().get("CONPROGRAMACION"), false)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2396"));
        }
    }

    public void cambiarVALORPROGRAMADO() {
        // <CODIGO_DESARROLLADO>
        double total = Double.parseDouble(
                        registro.getCampos().get(VALORTOTALESS).toString());
        double prog = Double.parseDouble(
                        registro.getCampos().get(VALORPROGRAMADOS).toString());
        saldoProgramar = String.valueOf(total - prog);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVALORUNITARIO() {
        // <CODIGO_DESARROLLADO>
        Object uni = registro.getCampos().get(VALORUNITARIOS);
        if (SysmanFunciones.validarVariableVacio(uni.toString())) {
            registro.getCampos().put(VALORUNITARIOS, 0);
        }

        double cantidad = Double.parseDouble(
                        registro.getCampos().get(CANTIDAD).toString());
        double unitario = Double.parseDouble(
                        registro.getCampos().get(VALORUNITARIOS).toString());
        double multiplicacion = cantidad * unitario;
        if (multiplicacion > 999999999999999999.99) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2397"));
            registro.getCampos().put(VALORUNITARIOS, 0);
            registro.getCampos().put(VALORTOTALESS, 0);
        }
        else {
            registro.getCampos().put(VALORTOTALESS, cantidad * unitario);
        }
        cambiarValorTotal();

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorEjecutado() {
        // <CODIGO_DESARROLLADO>
        double total = Double.parseDouble(
                        registro.getCampos().get(VALORTOTALESS).toString());
        double ejec = Double.parseDouble(
                        registro.getCampos().get(VALOREJECUTADOS).toString());
        saldoEjecutar = String.valueOf(total - ejec);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorTotal() {
        // <CODIGO_DESARROLLADO>

        double total = Double.parseDouble(
                        registro.getCampos().get(VALORTOTALESS).toString());
        double ejec = Double.parseDouble(
                        registro.getCampos().get(VALOREJECUTADOS).toString());
        saldoEjecutar = String.valueOf(total - ejec);
        double prog = Double.parseDouble(
                        registro.getCampos().get(VALORPROGRAMADOS).toString());
        saldoProgramar = String.valueOf(total - prog);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcantidad() {
        // <CODIGO_DESARROLLADO>
        Object cant = registro.getCampos().get(CANTIDAD);
        if (SysmanFunciones.validarVariableVacio(cant.toString())) {
            registro.getCampos().put(CANTIDAD, 0);
        }
        double cantidad = Double.parseDouble(
                        registro.getCampos().get(CANTIDAD).toString());
        double unitario = Double.parseDouble(
                        registro.getCampos().get(VALORUNITARIOS).toString());
        double multiplicacion = cantidad * unitario;
        if (multiplicacion > 999999999999999999.99) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2397"));
            registro.getCampos().put(CANTIDAD, 0);
            registro.getCampos().put(VALORTOTALESS, 0);
        }
        else {
            registro.getCampos().put(VALORTOTALESS, cantidad * unitario);
        }
        cambiarValorTotal();
        // </CODIGO_DESARROLLADO>
    }

    public void comprobarProgramacion(Registro reg) {

        try {

            String parametro = ejbSysmanUtl.consultarParametro(compania,
                            "PERMITE AJUSTAR COMPONENTES Y ACTIVIDADES PROGRAMADAS",
                            modulo, new Date(), false);

            if (Double.valueOf(reg.getCampos().get(VALORPROGRAMADOS)
                            .toString()) > 0
                && parametro.equals("NO")) {
                conProgramacion = true;
            }
            else {
                conProgramacion = false;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if ("i".equals(accion)) {
            valoresPorDefecto();
        }

        comprobarProgramacion(registro);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put(
                        FrmcomponentesControladorEnum.CODIGOPROYECTO.getValue(),
                        codigoProy);

        if (SysmanFunciones
                        .validarVariableVacio(SysmanFunciones
                                        .nvl(registro.getCampos()
                                                        .get(VALORTOTALESS), "")
                                        .toString())
            || (Double.parseDouble(SysmanFunciones
                            .nvl(registro.getCampos()
                                            .get(VALORTOTALESS), "0")
                            .toString()) <= 0)) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3764"));

            return false;

        }
        try {

            registro.getCampos().put(CODIGO,
                            ejbSysmanUtl.generarConsecutivoConValorInicial(
                                            "COMPONENTES",
                                            " COMPANIA = ''" + compania
                                                + "'' AND CODIGOPROYECTO = ''"
                                                + codigoProy + "'' ",
                                            CODIGO, "1")

            );
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmcomponentesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        boolean validaTotales;
        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove("NOMBRETIPO");
        }

        try {
            if (!validarTotales()) {
                validaTotales = false;
                DecimalFormat num = new DecimalFormat("#,###.00");
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2398"));
                JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                                idioma.getString("TG_VALOR_TOTAL_PROYECTO"),
                                " ",
                                num.format(totalProyecto)));
                JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                                idioma.getString("TB_TB2399"), " ",
                                num.format(valorTotal)));
                JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                                idioma.getString("TB_TB2400"), " ",
                                num.format(validacion)));
            }
            else {
                validaTotales = true;
            }

            if (!validaTotales) {
                registro.getCampos().put(VALORUNITARIOS, 0);
                valorUnitario = 0;
                registro.getCampos().put(CANTIDAD, 0);
                valorTotal = 0;
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2402"));
                return false;
            }
        }
        catch (IllegalArgumentException ex) {
            Logger.getLogger(FrmcomponentesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
            return false;
        }
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    private void valoresPorDefecto() {

        registro.getCampos().put(CANTIDAD, 0);
        registro.getCampos().put(VALORUNITARIOS, 0);
        registro.getCampos().put(VALORTOTALESS, 0);
        registro.getCampos().put(VALORPROGRAMADOS, 0);
        registro.getCampos().put(VALOREJECUTADOS, 0);
        registro.getCampos().put("VALORTOTALSOLICITADO", 0);
        registro.getCampos().put("SALDOCOMPONENTE", 0);
        registro.getCampos().put("VALORRECURSOSPROPIOS", 0);
        registro.getCampos().put("VALORSGP", 0);
        registro.getCampos().put("VALORREGALIAS", 0);
        registro.getCampos().put("VALORRECURSOSESPECIALES", 0);
        registro.getCampos().put("VALOR_OTROS", 0);
        registro.getCampos().put("VALORPORFINANCIAR", 0);
        totalComponentePrevio = 0;
    }

    public void ejecutarrcCerrar() {
        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametrosEntrada);

        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMPROYECTOS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, modulo);
    }

    public boolean validarTotales() {
        validacion = (valorTotal - totalComponentePrevio)
            + Double.valueOf(
                            registro.getCampos().get(VALORTOTALESS).toString());
        return totalProyecto >= validacion;
    }

    public double getTotalProyecto() {
        return totalProyecto;
    }

    public void setTotalProyecto(double totalProyecto) {
        this.totalProyecto = totalProyecto;
    }

    public Map<String, Object> getRidProyecto() {
        return ridProyecto;
    }

    public void setRidProyecto(Map<String, Object> ridProyecto) {
        this.ridProyecto = ridProyecto;
    }

    public boolean isConProgramacion() {
        return conProgramacion;
    }

    public void setConProgramacion(boolean conProgramacion) {
        this.conProgramacion = conProgramacion;
    }

    public List<Registro> getListaUnidad() {
        return listaUnidad;
    }

    public void setListaUnidad(List<Registro> listaUnidad) {
        this.listaUnidad = listaUnidad;
    }

    public List<Registro> getListaTipoComponente() {
        return listaTipoComponente;
    }

    public void setListaRipoComponente(List<Registro> listaTipoComponente) {
        this.listaTipoComponente = listaTipoComponente;
    }

    public List<Registro> getListaVigencia() {
        return listaVigencia;
    }

    public void setListaVigencia(List<Registro> listaVigencia) {
        this.listaVigencia = listaVigencia;
    }

    public String getCodigoProy() {
        return codigoProy;
    }

    public void setCodigoProy(String codigoProy) {
        this.codigoProy = codigoProy;
    }

    public String getSaldoEjecutar() {
        return saldoEjecutar;
    }

    public void setSaldoEjecutar(String saldoEjecutar) {
        this.saldoEjecutar = saldoEjecutar;
    }

    public String getSaldoProgramar() {
        return saldoProgramar;
    }

    public void setSaldoProgramar(String saldoProgramar) {
        this.saldoProgramar = saldoProgramar;
    }

    public double getValorProgramado() {
        return valorProgramado;
    }

    public void setValorProgramado(double valorProgramado) {
        this.valorProgramado = valorProgramado;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public double getSaldoComponente() {
        return saldoComponente;
    }

    public void setSaldoComponente(double saldoComponente) {
        this.saldoComponente = saldoComponente;
    }

    public double getValorEjecutado() {
        return valorEjecutado;
    }

    public void setValorEjecutado(double valorEjecutado) {
        this.valorEjecutado = valorEjecutado;
    }

    public double getValorTotalSolicitado() {
        return valorTotalSolicitado;
    }

    public void setValorTotalSolicitado(double valorTotalSolicitado) {
        this.valorTotalSolicitado = valorTotalSolicitado;
    }

    public double getTotalComponentePrevio() {
        return totalComponentePrevio;
    }

    public void setTotalComponentePrevio(double totalComponentePrevio) {
        this.totalComponentePrevio = totalComponentePrevio;
    }

    public String getAnoIni() {
        return anoIni;
    }

    public void setAnoIni(String anoIni) {
        this.anoIni = anoIni;
    }

    public String getAnoFin() {
        return anoFin;
    }

    public void setAnoFin(String anoFin) {
        this.anoFin = anoFin;
    }

    public boolean isMuestraRegistro() {
        return muestraRegistro;
    }

    public void setMuestraRegistro(boolean muestraRegistro) {
        this.muestraRegistro = muestraRegistro;
    }

}
