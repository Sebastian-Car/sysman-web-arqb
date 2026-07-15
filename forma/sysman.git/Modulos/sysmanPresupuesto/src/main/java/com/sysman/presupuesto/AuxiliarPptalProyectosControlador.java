package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.AuxiliarPptalProyectosControladorEnum;
import com.sysman.presupuesto.enums.AuxiliarPptalProyectosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 3, 07/07/2016 10:31:34 -- Modificado por jrodriguezr
 * @author lcortes
 * @version 4, 06/12/2016 12:14:21 -- Modificado por lcortes
 * 
 * @version 5, 17/04/2017, pespitia: <br>
 * Se aplicaron las recomendaciones de SonarLint.<br>
 * Se aplico Refactoring.<br>
 * Manejo de EJBs.
 * 
 * @author asana
 * @version 6, 13/06/2017 se implementa enum en formulario y se
 * modifica Conexión
 */
@ManagedBean
@ViewScoped
public class AuxiliarPptalProyectosControlador extends BeanBaseModal {
    private final String compania;

    /**
     * Constante a nivel de clase que aloja el modulo con el cual el
     * usuario esta interactuando.
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * <code>GeneralParameterEnum.CODIGO</code>
     */
    private final String cCodigo;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * <code>GeneralParameterEnum.ANO</code>
     */
    private final String cAnio;

    // <DECLARAR_ATRIBUTOS>
    private String tipoInicial;
    private String tipoFinal;
    private String cuentaInicial;
    private String cuentaFinal;
    private String terceroInicial;
    private String terceroFinal;
    private String proyectoInicial;
    private String proyectoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private RegistroDataModelImpl listaproyectoInicial;
    private RegistroDataModelImpl listaproyectoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // </DECLARAR_EJBs>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_EJBs>

    /**
     * Creates a new instance of AuxiliarPptalProyectosControlador
     */
    public AuxiliarPptalProyectosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        cCodigo = GeneralParameterEnum.CODIGO.getName();
        cAnio = GeneralParameterEnum.ANO.getName();

        try {
            numFormulario = GeneralCodigoFormaEnum.AUXILIAR_PPTAL_PROYECTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(AuxiliarPptalProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        fechaInicial = fechaFinal = new Date();
        cargarListaTipoInicial();
        cargarListaCuentaInicial();
        cargarListaTerceroInicial();
        cargarListaproyectoInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTipoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarPptalProyectosControladorUrlEnum.URL4198
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    public void cargarListaTipoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarPptalProyectosControladorUrlEnum.URL4885
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AuxiliarPptalProyectosControladorEnum.CODIGOINICIAL
                        .getValue(), tipoInicial);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarPptalProyectosControladorUrlEnum.URL5910
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(cAnio, SysmanFunciones.ano(fechaInicial));

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarPptalProyectosControladorUrlEnum.URL7252
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(cAnio, SysmanFunciones.ano(fechaInicial));
        param.put(AuxiliarPptalProyectosControladorEnum.CUENTAINICIAL
                        .getValue(), cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarPptalProyectosControladorUrlEnum.URL8370
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarPptalProyectosControladorUrlEnum.URL9084
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AuxiliarPptalProyectosControladorEnum.NITINICIAL.getValue(),
                        terceroInicial);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void cargarListaproyectoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarPptalProyectosControladorUrlEnum.URL10312
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(cAnio, SysmanFunciones.ano(fechaInicial));

        listaproyectoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaproyectoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AuxiliarPptalProyectosControladorUrlEnum.URL11516
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(cAnio, SysmanFunciones.ano(fechaInicial));
        param.put(AuxiliarPptalProyectosControladorEnum.CODIGOINI.getValue(),
                        proyectoInicial);

        listaproyectoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirNotas() {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;

        if (!validarVacios() || !validarVacios2() || !validarFechas()) {
            return;
        }

        HashMap<String, Object> reemplazar = new HashMap<>();

        String fechaIni;
        String fechaFin;
        String tipos = asignarTipos();

        if (tipos == null) {
            return;
        }

        fechaIni = SysmanFunciones.formatearFecha(fechaInicial);
        fechaFin = SysmanFunciones.formatearFecha(fechaFinal);

        reemplazar.put("cuentaInicial", cuentaInicial);
        reemplazar.put("cuentaFinal", cuentaFinal);
        reemplazar.put("fechaInicial", fechaIni);
        reemplazar.put("fechaFinal", fechaFin);
        reemplazar.put("tipoInicial", tipoInicial);
        reemplazar.put("tipoFinal", tipoFinal);
        reemplazar.put("proyectoInicial", proyectoInicial);
        reemplazar.put("proyectoFinal", proyectoFinal);
        reemplazar.put("terceroInicial", terceroInicial);
        reemplazar.put("terceroFinal", terceroFinal);
        reemplazar.put("tipos", tipos);

        String strSql = Reporteador.resuelveConsulta(
                        "800057AuxProyectosPptalPlano",
                        Integer.parseInt(SessionUtil.getModulo()), reemplazar);

        List<Registro> rs = service.getListado(ConectorPool.ESQUEMA_SYSMAN,
                        strSql);
        if (rs.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB812"));
        }
        else {
            String sNombre = SysmanFunciones.nvl(SysmanFunciones.padl(
                            SessionUtil.getCompaniaIngreso().getNombre(), 21,
                            " "), "Plano")
                + "_BPI.TXT";
            String contenido;
            StringBuilder bld = new StringBuilder();

            for (Registro registro : rs) {
                bld.append(registro.getCampos().get("CODPROYECTO_PPTAL")
                    + "\t" + registro.getCampos().get("TIPO_CPTE") + "\t"
                    + registro.getCampos().get("COMPROBANTE") + "\t"
                    + registro.getCampos().get("FECHA") + "\t"
                    + registro.getCampos().get("NIT") + "\t"
                    + registro.getCampos().get("NOMBRETERCERO") + "\t"
                    + registro.getCampos().get("CODIGO_CUENTA") + "\t"
                    + registro.getCampos().get("NOMBRERUBRO") + "\t"
                    + registro.getCampos().get("DESCRIPCION") + "\t"
                    + registro.getCampos().get("VALOR_COMPROBANTE") + "\t"
                    + registro.getCampos().get("VALOR_AFECTADO") + "\t"
                    + registro.getCampos().get("SALDO") + "\t"
                    + registro.getCampos().get("NOMBREBANCOO") + "\t"
                    + registro.getCampos().get("NRO_CHEQUE") + "\t"
                    + registro.getCampos().get("NOMASIGNACION") + "\t"
                    + registro.getCampos().get("NOMBRE") + "\t"
                    + registro.getCampos().get("TIPO_CPTE_AFECT") + "\t"
                    + registro.getCampos().get("CMPTE_AFECTADO") + "\t"
                    + registro.getCampos().get("VLR_PROYECTO") + "\t"
                    + registro.getCampos().get("NOMBRE_PROYECTO") + "\r\n");
            }
            try {
                contenido = bld.toString();
                archivoDescarga = JsfUtil.getArchivoDescarga(
                                JsfUtil.serializarPlano(contenido), sNombre);
            }
            catch (JRException | IOException e) {
                Logger.getLogger(AuxiliarPptalProyectosControlador.class
                                .getName()).log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        // </CODIGO_DESARROLLADO>
    }

    public String asignarTipos() {
        String tipos = "";
        try {
            tipos = ejbSysmanUtil.consultarParametro(compania,
                            "TIPOS DE COMPROBANTES EN ARCHIVO PLANO BPI",
                            modulo, new Date(), false);
        }
        catch (SystemException e1) {
            Logger.getLogger(AuxiliarPptalProyectosControlador.class.getName())
                            .log(Level.SEVERE, null, e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        if (tipos == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB811"));
            return null;
        }
        else {
            tipos = SysmanFunciones.colocarComillas(tipos);
        }
        return tipos;
    }

    private void generaReporte(FORMATOS formato) {
        try {
            if (!validarVacios() || !validarVacios2() || !validarFechas()) {
                return;
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000973LisAuxProyectosPptales";

            String fechaIni = "";
            String fechaFin = "";
            fechaIni = SysmanFunciones.formatearFecha(fechaInicial);
            fechaFin = SysmanFunciones.formatearFecha(fechaFinal);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("fechaInicial", fechaIni);
            reemplazar.put("fechaFinal", fechaFin);
            reemplazar.put("tipoInicial", tipoInicial);
            reemplazar.put("tipoFinal", tipoFinal);
            reemplazar.put("proyectoInicial", proyectoInicial);
            reemplazar.put("proyectoFinal", proyectoFinal);
            reemplazar.put("terceroInicial", terceroInicial);
            reemplazar.put("terceroFinal", terceroFinal);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_CUENTAINICIAL", cuentaInicial);
            parametros.put("PR_CUENTAFINAL", cuentaFinal);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            parametros.put("PR_FECHAFINAL",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put("PR_PROYECTOINICIAL", proyectoInicial);
            parametros.put("PR_PROYECTOFINAL", proyectoFinal);
            parametros.put("PR_TERCEROINICIAL", terceroInicial);
            parametros.put("PR_TERCEROFINAL", terceroFinal);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public boolean validarVacios() {
        if (SysmanFunciones.validarVariableVacio(tipoInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB270"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(tipoFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB271"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(terceroInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB618"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(terceroFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB620"));
            return false;
        }

        return true;
    }

    public boolean validarVacios2() {
        if (SysmanFunciones.validarVariableVacio(cuentaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB195"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB367"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(proyectoInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB809"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(proyectoFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB810"));
            return false;
        }

        return true;
    }

    /**
     * Establece las condiciones que se deben cumplir con las fechas
     * de entrada.
     * 
     * @return <code>true</code> si cumple las condiciones
     * predeterminadas.
     */
    public boolean validarFechas() {
        if (SysmanFunciones.calcularDiferenciaAnios(fechaInicial,
                        fechaFinal) != 0) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3083"));

            return false;
        }

        return true;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = cuentaFinal = proyectoInicial = proyectoFinal = null;
        fechaFinal = null;

        listaCuentaFinal = listaCuentaInicial = null;
        listaproyectoFinal = listaproyectoInicial = null;

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaFinal() {
        // </CODIGO_DESARROLLADO>
        if (validarFechas()) {
            cargarListaCuentaInicial();
            cargarListaproyectoInicial();
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaTipoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = asignarValorCampo(registroAux, cCodigo);
        tipoFinal = null;

        cargarListaTipoFinal();

    }

    public void seleccionarFilaTipoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = asignarValorCampo(registroAux, cCodigo);
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = asignarValorCampo(registroAux, cCodigo);
        cuentaFinal = null;

        cargarListaCuentaFinal();

    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = asignarValorCampo(registroAux, cCodigo);
    }

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = asignarValorCampo(registroAux, "NIT");
        terceroFinal = null;

        cargarListaTerceroFinal();

    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = asignarValorCampo(registroAux, "NIT");
    }

    public void seleccionarFilaproyectoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proyectoInicial = asignarValorCampo(registroAux, cCodigo);
        proyectoFinal = null;

        cargarListaproyectoFinal();

    }

    public void seleccionarFilaproyectoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        proyectoFinal = asignarValorCampo(registroAux, cCodigo);
    }

    /**
     * Verifica que el registro <code>reg</code> tenga una coleccion
     * de campos que no sea nula.
     * 
     * @param reg
     * @param campo
     * El campo a evaluar en la coleccion.
     * @return El valor del campo segun la coleccion.
     */
    private String asignarValorCampo(Registro reg, String campo) {
        return reg.getCampos().isEmpty() ? ""
            : SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
                : reg.getCampos().get(campo).toString();
    }
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getTipoInicial() {
        return tipoInicial;
    }

    public void setTipoInicial(String tipoInicial) {
        this.tipoInicial = tipoInicial;
    }

    public String getTipoFinal() {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal) {
        this.tipoFinal = tipoFinal;
    }

    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal() {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }

    public String getTerceroInicial() {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal() {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    public String getProyectoInicial() {
        return proyectoInicial;
    }

    public void setProyectoInicial(String proyectoInicial) {
        this.proyectoInicial = proyectoInicial;
    }

    public String getProyectoFinal() {
        return proyectoFinal;
    }

    public void setProyectoFinal(String proyectoFinal) {
        this.proyectoFinal = proyectoFinal;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaTipoInicial() {
        return listaTipoInicial;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial) {
        this.listaTipoInicial = listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal() {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal) {
        this.listaTipoFinal = listaTipoFinal;
    }

    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }

    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    public RegistroDataModelImpl getListaproyectoInicial() {
        return listaproyectoInicial;
    }

    public void setListaproyectoInicial(
        RegistroDataModelImpl listaproyectoInicial) {
        this.listaproyectoInicial = listaproyectoInicial;
    }

    public RegistroDataModelImpl getListaproyectoFinal() {
        return listaproyectoFinal;
    }

    public void setListaproyectoFinal(
        RegistroDataModelImpl listaproyectoFinal) {
        this.listaproyectoFinal = listaproyectoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
