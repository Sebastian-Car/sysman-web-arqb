package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCeroRemote;
import com.sysman.bancoproyectos.ejb.EjbBancoProyectoTresRemote;
import com.sysman.bancoproyectos.enums.FrmprogramacionfinancieraControladorEnum;
import com.sysman.bancoproyectos.enums.FrmprogramacionfinancieraControladorUrlEnum;
import com.sysman.bancoproyectos.reportes.BancoProyectosReportes;
import com.sysman.beanbase.BeanBaseDatosAcme;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author dmaldonado
 * @version 1, 21/10/2015
 * 
 * @author eamaya
 * @version 2.0, 21/09/2017, Proceso de Refactoring DSS, Manejo de
 * EJBs, cambio de numero de formulario por enum, cambio de textos
 * quemados por textos en Bena y correcciones SonarQube
 */
@ManagedBean
@ViewScoped

public class FrmprogramacionfinancieraControlador extends BeanBaseDatosAcme {

    private final String compania;
    private final String modulo;
    private final String usuario;
    private final String valorEjecutadoCons;
    private final String valorProgramadoCons;
    private final String valorTotalCons;
    private final String menuActualCons;

    private DecimalFormat dblDF;
    private List<Registro> listaTipoEstado;
    private List<Registro> listaVigencia;
    private RegistroDataModelImpl listaComponente;
    private RegistroDataModelImpl listaProyecto;
    private RegistroDataModelImpl listaCodigoQueaprueba;
    private RegistroDataModelImpl listaCodigoitemAprueba;
    private RegistroDataModel listaDependencia;
    private String componente;
    private String tipoEstado;
    private String vigencia;
    private String proyecto;
    private String codigoAprueba;
    private String itemAprueba;
    private String dependencia;
    private String totalProyecto;
    private String valorTotal1;
    private String valorEjecutado1;
    private String valorProgramado1;
    private String bpim;
    private String valorTotalComp;
    private String valorEjecutadoComp;
    private String valorProgramadoComp;
    private String saldoComp;
    private String saldoProy;
    private String tipoTAprueba;
    private String claseTAprueba;
    private String dependenciaAprueba;
    private String descripcionNovedad;
    private String valorAprobado;
    private String valorProgramar;
    private String nombreDependencia;
    private String codigoDependencia;
    private String nombreProyecto;
    private String vigenciaFin;
    private String vigenciaInicial;
    private String periodicidad;
    private String nombreComponente;
    private String novedadVisible;
    private String colorTitulo;
    private String strControlaDependencia;
    private String tituloForm;
    private String tipoComponente;
    private double valorAprobadoItem;
    private double valorProgramadoItem;
    private String periodoProyecto;
    private String radicadoAprueba;
    private String objetoAprueba;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    @EJB
    private EjbBancoProyectoCeroRemote ejbBancoProyectoCero;

    @EJB
    private EjbBancoProyectoTresRemote ejbBancoProyectoTres;

    public FrmprogramacionfinancieraControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();
        valorEjecutadoCons = "VALOREJECUTADO";
        valorProgramadoCons = "VALORPROGRAMADO";
        valorTotalCons = "VALORTOTAL";
        menuActualCons = "52020202";

        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        dblDF = new DecimalFormat("#,##0.00", dfs);
        try {
            novedadVisible = "none";
            listaTipoEstado = new ArrayList<>();
            numFormulario = GeneralCodigoFormaEnum.FRMPROGRAMACIONFINANCIERA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            if (SessionUtil.getMenuActual() != null) {
                if ("52020201".equals(SessionUtil.getMenuActual())) {
                    tituloForm = idioma.getString("TB_TB3633");
                    colorTitulo = "#0000FF";
                    tipoEstado = "P";
                    novedadVisible = "none";
                }
                else if (menuActualCons.equals(SessionUtil.getMenuActual())) {
                    tituloForm = idioma.getString("TB_TB3633");
                    colorTitulo = "#FF0000";
                    tipoEstado = "E";
                    novedadVisible = "block";
                }
            }
        }
        catch (SysmanException ex) {
            Logger.getLogger(FrmprogramacionfinancieraControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {

        abrirFormulario();

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            strControlaDependencia = ejbSysmanUtl.consultarParametro(compania,
                            "CONTROLAR DEPENDENCIA EN BPPIM", modulo,
                            new Date(), false);

            if (strControlaDependencia == null) {
                JsfUtil.agregarMensajeFatal(idioma.getString("TB_TB2420"));
                strControlaDependencia = "";
            }

        }
        catch (SystemException ex) {
            Logger.getLogger(FrmprogramacionfinancieraControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }

        cargarListaProyecto();
        cargarListaTipoEstado();
        // </CODIGO_DESARROLLADO>
    }

    public void renderizar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaTipoEstado() {
        listaTipoEstado = null;
        listaTipoEstado = new ArrayList<>();
        HashMap<String, Object> aux = new HashMap<>();
        if ("52020201".equals(SessionUtil.getMenuActual())) {
            aux.put(GeneralParameterEnum.CODIGO.getName(), "P");
            aux.put(GeneralParameterEnum.NOMBRE.getName(), "Programado");
            listaTipoEstado.add(new Registro(0, aux));
            aux = new HashMap<>();
            aux.put(GeneralParameterEnum.CODIGO.getName(), "RP");
            aux.put(GeneralParameterEnum.NOMBRE.getName(), "Reprogramado");
            listaTipoEstado.add(new Registro(1, aux));
        }
        else if (menuActualCons.equals(SessionUtil.getMenuActual())) {
            aux.put(GeneralParameterEnum.CODIGO.getName(), "E");
            aux.put(GeneralParameterEnum.NOMBRE.getName(), "Ejecutado");
            listaTipoEstado.add(new Registro(2, aux));
        }

    }

    public void cargarListaProyecto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmprogramacionfinancieraControladorUrlEnum.URL1111
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        // j

        if ("SI".equals(strControlaDependencia)
            && (SessionUtil.getNivelUsuario(modulo) != ('9'))) {
            param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                            SessionUtil.getUser().getDependencia().getCodigo());

        }

        listaProyecto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaVigencia() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(FrmprogramacionfinancieraControladorEnum.VIGENCIAINICIAL
                        .getValue(),
                        vigenciaInicial);
        param.put(FrmprogramacionfinancieraControladorEnum.VIGENCIAFIN
                        .getValue(),
                        vigenciaFin);

        try {
            listaVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmprogramacionfinancieraControladorUrlEnum.URL7527
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaComponente() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmprogramacionfinancieraControladorUrlEnum.URL7993
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmprogramacionfinancieraControladorEnum.CODIGOPROYECTO
                        .getValue(),
                        proyecto);
        param.put(GeneralParameterEnum.VIGENCIA.getName(),
                        vigencia);

        listaComponente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaCodigoQueaprueba() {
        String aux = "E".equals(tipoEstado) ? "E" : "S";
        // String aux = "S".equals(tipoEstado) ? "E" : "S";

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmprogramacionfinancieraControladorUrlEnum.URL10154
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        vigencia);
        param.put(FrmprogramacionfinancieraControladorEnum.CODIGOPROYECTO
                        .getValue(),
                        proyecto);

        param.put(FrmprogramacionfinancieraControladorEnum.COMPONENTE
                        .getValue(),
                        componente);
        // param.put(GeneralParameterEnum.CLASE.getName(),
        // aux);

        listaCodigoQueaprueba = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCodigoitemAprueba() {
        // caceres
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmprogramacionfinancieraControladorUrlEnum.URL13385
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        vigencia);

        // listaCodigoitemAprueba = new
        // RegistroDataModelImpl(urlBean.getUrl(),
        // urlBean.getUrlConteo().getUrl(), param,
        // true, "CODIGODETALLE");

        try {
            listaCodigoitemAprueba = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, CacheUtil.getLlaveServicio(urlConexionCache,
                                            "BP_D_NOVEDADPROYECTO"));
        }
        catch (SysmanException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirExcel() {
        archivoDescarga = null;
        // <CODIGO_DESARROLLADO>
        try {
            archivoDescarga = BancoProyectosReportes.generarInformeFES(proyecto,
                            vigencia, Integer.valueOf(modulo), service,
                            ejbBancoProyectoTres, logger);
        }
        catch (NumberFormatException e) {
            Logger.getLogger(FrmprogramacionfinancieraControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2418"));
        }
        // </CODIGO_DESARROLLADO>

    }

    public void cambiarVigencia() {
        periodoProyecto = service.buscarEnLista(vigencia, "NUMERO", "ROWNUM",
                        listaVigencia);
        componente = null;
        valorTotalComp = null;
        nombreComponente = null;
        valorEjecutadoComp = null;
        valorProgramadoComp = null;
        saldoComp = null;
        cargarListaComponente();
    }

    public void cambiarTipoEstado() {
        if (tipoEstado != null) {
            if ("E".equals(tipoEstado)) {
                novedadVisible = "block";
                cargarListaCodigoQueaprueba();
                cargarListaCodigoitemAprueba();
            }
            else {
                novedadVisible = "none";
            }
        }
        else {
            novedadVisible = "none";
        }
    }

    public void oprimirBtnProgActividades() {
        // <CODIGO_DESARROLLADO>
        boolean estado = true;
        if ((SessionUtil.getMenuActual() != null)
            && menuActualCons.equals(SessionUtil.getMenuActual())
            && (SysmanFunciones.validarVariableVacio(codigoAprueba)
                || SysmanFunciones.validarVariableVacio(itemAprueba))) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2419"));
            estado = false;
        }

        if ((periodicidad == null) || "".equals(periodicidad)) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2422"));
            estado = false;
        }

        if (estado) {
            String[] campos = { "proyecto", "componente", "vigencia",
                                "tipoEstado", "periodicidad", "tipoComponente",
                                "tipoTApProg", "claseTApProg", "codigoApProg",
                                "itemApProg", "dependenciaApProg",
                                "valorAprobadoItem", "valorProgramadoItem",
                                "totalProyecto", "periodoProyecto" };

            String[] valores = { proyecto, componente, vigencia, tipoEstado,
                                 periodicidad, tipoComponente, tipoTAprueba,
                                 claseTAprueba, codigoAprueba, itemAprueba,
                                 dependenciaAprueba,
                                 String.valueOf(valorAprobadoItem),
                                 String.valueOf(valorProgramadoItem),
                                 totalProyecto, periodoProyecto };

            SessionUtil.cargarModalDatosFlash(
                            Integer.toString(
                                            GeneralCodigoFormaEnum.FRMPROGRAMACIONACTIVIDADES_CONTROLADOR
                                                            .getCodigo()),
                            modulo, campos, valores);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormularioBtnProgActividades() {
        try {
            // <CODIGO_DESARROLLADO>

            ejbBancoProyectoCero.actualizarProgramado(compania,
                            Integer.parseInt(vigenciaInicial),
                            proyecto, proyecto, 1, usuario);

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2421")
                            .replace("#$nombreProyecto#$", nombreProyecto)
                            .replace("#$nombreComponente#$", nombreComponente));

            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.CODIGO.getName(), proyecto);

            Registro registroProyecto = listaProyecto
                            .getRegistroUnico(params);
            selectProyecto(registroProyecto);

            Map<String, Object> parameters = new TreeMap<>();

            parameters.put(GeneralParameterEnum.CODIGO.getName(), componente);

            Registro registroComponente = listaComponente
                            .getRegistroUnico(parameters);
            selectComponente(registroComponente);

        }
        catch (SystemException ex) {
            Logger.getLogger(FrmprogramacionfinancieraControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            return;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaComponente(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        componente = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        nombreComponente = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRECOMPONENTE"),
                                        "")
                        .toString();
        tipoComponente = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TIPOCOMPONENTE"), "")
                        .toString();
        valorTotalComp = dblDF
                        .format(SysmanFunciones.nvl(registroAux.getCampos()
                                        .get(valorTotalCons), "0"));
        valorEjecutadoComp = dblDF.format(SysmanFunciones.nvl(
                        registroAux.getCampos().get(valorEjecutadoCons), "0"));
        valorProgramadoComp = dblDF.format(SysmanFunciones.nvl(
                        registroAux.getCampos().get(valorProgramadoCons), "0"));
        saldoComp = dblDF.format(Double
                        .valueOf(SysmanFunciones.nvl(registroAux.getCampos()
                                        .get(valorTotalCons), "0")
                                        .toString())
            - Double.valueOf(SysmanFunciones.nvl(
                            registroAux.getCampos().get(valorProgramadoCons),
                            "0")
                            .toString()));
        cargarListaCodigoQueaprueba();
        cargarListaCodigoitemAprueba();
    }

    public void selectComponente(Registro registroAux) {
        valorTotalComp = dblDF
                        .format(registroAux.getCampos().get(valorTotalCons));
        valorEjecutadoComp = dblDF.format(
                        registroAux.getCampos().get(valorEjecutadoCons));
        valorProgramadoComp = dblDF.format(
                        registroAux.getCampos().get(valorProgramadoCons));
        saldoComp = dblDF.format(Double
                        .valueOf(registroAux.getCampos().get(valorTotalCons)
                                        .toString())
            - Double.valueOf(registroAux.getCampos().get(valorProgramadoCons)
                            .toString()));
    }

    public void seleccionarFilaProyecto(SelectEvent event) {
        vigencia = null;
        saldoComp = null;
        componente = null;
        nombreComponente = null;
        valorEjecutadoComp = null;
        valorProgramadoComp = null;
        valorTotalComp = null;
        Registro registroAux = (Registro) event.getObject();
        proyecto = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        vigenciaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("VIGENCIAINICIO"), "")
                        .toString();
        vigenciaFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("VIGENCIAFIN"), "")
                        .toString();
        nombreProyecto = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBREPROYECTO"), "")
                        .toString();
        totalProyecto = SysmanFunciones
                        .nvl(registroAux.getCampos().get(valorTotalCons), "")
                        .toString();
        valorTotal1 = dblDF.format(SysmanFunciones
                        .nvl(registroAux.getCampos().get(valorTotalCons), "0"));
        valorEjecutado1 = dblDF.format(SysmanFunciones.nvl(
                        registroAux.getCampos().get(valorEjecutadoCons), "0"));
        valorProgramado1 = dblDF.format(SysmanFunciones.nvl(
                        registroAux.getCampos().get(valorProgramadoCons), "0"));
        periodicidad = SysmanFunciones
                        .nvl(registroAux.getCampos().get("PERIOCIDAD"), "")
                        .toString();

        bpim = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGOBPIM"), "")
                        .toString();
        codigoDependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get("DEPENDENCIA"), "")
                        .toString();
        nombreDependencia = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
        saldoProy = dblDF.format(Double
                        .valueOf(SysmanFunciones.nvl(registroAux.getCampos()
                                        .get(valorTotalCons), "0")
                                        .toString())
            - Double.valueOf(SysmanFunciones.nvl(
                            registroAux.getCampos().get(valorProgramadoCons),
                            "")
                            .toString()));
        cargarListaVigencia();
        cargarListaComponente();
    }

    public void selectProyecto(Registro registroAux) {
        totalProyecto = registroAux.getCampos().get(valorTotalCons).toString();
        valorTotal1 = dblDF.format(registroAux.getCampos().get(valorTotalCons));
        valorEjecutado1 = dblDF.format(
                        registroAux.getCampos().get(valorEjecutadoCons));
        valorProgramado1 = dblDF.format(
                        registroAux.getCampos().get(valorProgramadoCons));
        saldoProy = dblDF.format(Double
                        .valueOf(registroAux.getCampos().get(valorTotalCons)
                                        .toString())
            - Double.valueOf(registroAux.getCampos().get(valorProgramadoCons)
                            .toString()));
    }

    public void seleccionarFilaCodigoQueaprueba(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoAprueba = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
        radicadoAprueba = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERORADICADO"), "")
                        .toString();
        objetoAprueba = SysmanFunciones
                        .nvl(registroAux.getCampos().get("OBJETO"), "")
                        .toString();
        descripcionNovedad = itemAprueba == null
            ? (radicadoAprueba + " " + objetoAprueba)
            : (radicadoAprueba + " " + objetoAprueba + " " + itemAprueba);
        tipoTAprueba = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TIPOT"), "")
                        .toString();
    }

    public void seleccionarFilaCodigoitemAprueba(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        itemAprueba = registroAux.getCampos().get("CODIGODETALLE").toString();
        valorAprobadoItem = Double.valueOf(registroAux.getCampos()
                        .get("VALORAPROBADO").toString());
        valorProgramadoItem = Double.valueOf(registroAux.getCampos()
                        .get(valorProgramadoCons).toString());
        descripcionNovedad = codigoAprueba != null
            ? (radicadoAprueba + " " + objetoAprueba + " " + itemAprueba)
            : itemAprueba;
        valorAprobado = dblDF.format(valorAprobadoItem);
        valorProgramar = dblDF.format(valorAprobadoItem - valorProgramadoItem);
    }

    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        dependencia = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    public List<Registro> getListaTipoEstado() {
        return listaTipoEstado;
    }

    public void setListaTipoEstado(List<Registro> listaTipoEstado) {
        this.listaTipoEstado = listaTipoEstado;
    }

    public List<Registro> getListaVigencia() {
        return listaVigencia;
    }

    public void setListaVigencia(List<Registro> listaVigencia) {
        this.listaVigencia = listaVigencia;
    }

    public RegistroDataModelImpl getListaComponente() {
        return listaComponente;
    }

    public void setListaComponente(RegistroDataModelImpl listaComponente) {
        this.listaComponente = listaComponente;
    }

    public RegistroDataModelImpl getListaProyecto() {
        return listaProyecto;
    }

    public void setListaProyecto(RegistroDataModelImpl listaProyecto) {
        this.listaProyecto = listaProyecto;
    }

    public RegistroDataModelImpl getListaCodigoQueaprueba() {
        return listaCodigoQueaprueba;
    }

    public void setListaCodigoQueaprueba(
        RegistroDataModelImpl listaCodigoQueaprueba) {
        this.listaCodigoQueaprueba = listaCodigoQueaprueba;
    }

    public RegistroDataModelImpl getListaCodigoitemAprueba() {
        return listaCodigoitemAprueba;
    }

    public void setListaCodigoitemAprueba(
        RegistroDataModelImpl listaCodigoitemAprueba) {
        this.listaCodigoitemAprueba = listaCodigoitemAprueba;
    }

    public RegistroDataModel getListaDependencia() {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModel listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    public String getComponente() {
        return componente;
    }

    public void setComponente(String componente) {
        this.componente = componente;
    }

    public String getTipoEstado() {
        return tipoEstado;
    }

    public void setTipoEstado(String tipoEstado) {
        this.tipoEstado = tipoEstado;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public String getProyecto() {
        return proyecto;
    }

    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    public String getCodigoAprueba() {
        return codigoAprueba;
    }

    public void setCodigoAprueba(String codigoAprueba) {
        this.codigoAprueba = codigoAprueba;
    }

    public String getItemAprueba() {
        return itemAprueba;
    }

    public void setItemAprueba(String itemAprueba) {
        this.itemAprueba = itemAprueba;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getValorTotal1() {
        return valorTotal1;
    }

    public void setValorTotal1(String valorTotal1) {
        this.valorTotal1 = valorTotal1;
    }

    public String getValorEjecutado1() {
        return valorEjecutado1;
    }

    public void setValorEjecutado1(String valorEjecutado1) {
        this.valorEjecutado1 = valorEjecutado1;
    }

    public String getValorProgramado1() {
        return valorProgramado1;
    }

    public void setValorProgramado1(String valorProgramado1) {
        this.valorProgramado1 = valorProgramado1;
    }

    public String getBpim() {
        return bpim;
    }

    public void setBpim(String bpim) {
        this.bpim = bpim;
    }

    public String getValorTotalComp() {
        return valorTotalComp;
    }

    public void setValorTotalComp(String valorTotalComp) {
        this.valorTotalComp = valorTotalComp;
    }

    public String getValorEjecutadoComp() {
        return valorEjecutadoComp;
    }

    public void setValorEjecutadoComp(String valorEjecutadoComp) {
        this.valorEjecutadoComp = valorEjecutadoComp;
    }

    public String getValorProgramadoComp() {
        return valorProgramadoComp;
    }

    public void setValorProgramadoComp(String valorProgramadoComp) {
        this.valorProgramadoComp = valorProgramadoComp;
    }

    public String getSaldoComp() {
        return saldoComp;
    }

    public void setSaldoComp(String saldoComp) {
        this.saldoComp = saldoComp;
    }

    public String getSaldoProy() {
        return saldoProy;
    }

    public void setSaldoProy(String saldoProy) {
        this.saldoProy = saldoProy;
    }

    public String getTipoTAprueba() {
        return tipoTAprueba;
    }

    public void setTipoTAprueba(String tipoTAprueba) {
        this.tipoTAprueba = tipoTAprueba;
    }

    public String getClaseTAprueba() {
        return claseTAprueba;
    }

    public void setClaseTAprueba(String claseTAprueba) {
        this.claseTAprueba = claseTAprueba;
    }

    public String getDependenciaAprueba() {
        return dependenciaAprueba;
    }

    public void setDependenciaAprueba(String dependenciaAprueba) {
        this.dependenciaAprueba = dependenciaAprueba;
    }

    public String getDescripcionNovedad() {
        return descripcionNovedad;
    }

    public void setDescripcionNovedad(String descripcionNovedad) {
        this.descripcionNovedad = descripcionNovedad;
    }

    public String getValorAprobado() {
        return valorAprobado;
    }

    public void setValorAprobado(String valorAprobado) {
        this.valorAprobado = valorAprobado;
    }

    public String getValorProgramar() {
        return valorProgramar;
    }

    public void setValorProgramar(String valorProgramar) {
        this.valorProgramar = valorProgramar;
    }

    public String getNombreDependencia() {
        return nombreDependencia;
    }

    public void setNombreDependencia(String nombreDependencia) {
        this.nombreDependencia = nombreDependencia;
    }

    public String getCodigoDependencia() {
        return codigoDependencia;
    }

    public void setCodigoDependencia(String codigoDependencia) {
        this.codigoDependencia = codigoDependencia;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public void setNombreProyecto(String nombreProyecto) {
        this.nombreProyecto = nombreProyecto;
    }

    public String getPeriodicidad() {
        return periodicidad;
    }

    public void setPeriodicidad(String periodicidad) {
        this.periodicidad = periodicidad;
    }

    public String getNombreComponente() {
        return nombreComponente;
    }

    public void setNombreComponente(String nombreComponente) {
        this.nombreComponente = nombreComponente;
    }

    public String getNovedadVisible() {
        return novedadVisible;
    }

    public void setNovedadVisible(String novedadVisible) {
        this.novedadVisible = novedadVisible;
    }

    public String getColorTitulo() {
        return colorTitulo;
    }

    public void setColorTitulo(String colorTitulo) {
        this.colorTitulo = colorTitulo;
    }

    public String getTituloForm() {
        return tituloForm;
    }

    public void setTituloForm(String tituloForm) {
        this.tituloForm = tituloForm;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    @Override
    public void cargarRegistro() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public void iniciarListasSubNulo() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public void iniciarListasSub() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public void iniciarListas() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public void reasignarOrigenGrilla() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public void asignarOrigenDatos() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public boolean insertarAntes() {
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }
}