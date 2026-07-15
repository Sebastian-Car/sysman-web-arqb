package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.serviciospublicos.enums.PedirciclonuevosControladorEnum;
import com.sysman.serviciospublicos.enums.PedirciclonuevosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 *
 * @author jguerrero
 * @version 1, 09/09/2016
 * 
 * @author eamaya
 * @version 2.0 , 15/06/2017 Proceso de Refactoring,Se cambi� el
 * llamado del c�digo del formulario
 * 
 */
@ManagedBean
@ViewScoped

public class PedirciclonuevosControlador extends BeanBaseModal {
    private final String compania;

    /**
     * Constante que almacenara la cadena "ANOINICIAL"
     */
    private final String anoInicialC;

    /**
     * Constante que almacenara la cadena "CODIGOFRAUDE"
     */
    private final String codigoFraudeC;

    /**
     * Constante que almacenara la cadena "ESTRATO"
     */
    private final String estratoC;

    /**
     * Constante que almacenara la cadena "NOMBRECONCEPTO"
     */
    private final String nombreConceptoC;

    /**
     * Constante que almacenara la cadena "NUMERO"
     */
    private final String numeroC;

    /**
     * Constante que almacenara la cadena "PERIODOINICIAL"
     */
    private final String periodoInicialC;

    /**
     * Constante que almacenara la cadena "SECTOR"
     */
    private final String sectorC;
    // <DECLARAR_ATRIBUTOS>
    private String ciclo;
    private String claseSolicitud;
    private String solicitud;
    private String ano;
    private String periodo;
    private String anoInicial;
    private String periodoInicial;
    private Map<String, Object> rid;
    private String codigoFraude;
    private Map<String, Object> registroSolicitudServicio;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listatxtCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of PedirciclonuevosControlador
     */
    public PedirciclonuevosControlador() {
        super();
        compania = SessionUtil.getCompania();
        anoInicialC = "ANOINICIAL";
        codigoFraudeC = "CODIGOFRAUDE";
        estratoC = "ESTRATO";
        nombreConceptoC = "NOMBRECONCEPTO";
        numeroC = "NUMERO";
        periodoInicialC = "PERIODOINICIAL";
        sectorC = "SECTOR";

        try {
            numFormulario = GeneralCodigoFormaEnum.PEDIRCICLONUEVOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                if (parametros.get("REGISTROSOLICITUDSERVICIO") != null) {

                    registroSolicitudServicio = (Map<String, Object>) parametros
                                    .get("REGISTROSOLICITUDSERVICIO");
                }
                claseSolicitud = registroSolicitudServicio.get("CLASESOLICITUD")
                                .toString();

                solicitud = registroSolicitudServicio.get(numeroC).toString();
                rid = (Map<String, Object>) parametros.get("CSS");

                if (parametros.get(codigoFraudeC) != null) {

                    codigoFraude = parametros.get(codigoFraudeC).toString();
                }

            }

            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PedirciclonuevosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListatxtCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListatxtCiclo() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listatxtCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PedirciclonuevosControladorUrlEnum.URL5255
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>

        if (validarAnoFinanciables() && validarPeriodoFinanciable()) {
            try {

                HashMap<String, Object> param = new HashMap<>();

                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

                param.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());

                param.put(GeneralParameterEnum.NUMERO.getName(),
                                rid.get("KEY_NUMERO"));

                param.put(GeneralParameterEnum.CLASE.getName(),
                                rid.get("KEY_CLASESOLICITUD"));

                Parameter parameter = new Parameter();

                parameter.setFields(param);

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                PedirciclonuevosControladorUrlEnum.URL5853
                                                                .getValue());
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                parameter);
                datosUsuario();

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1567"));

            }
            catch (SystemException e) {
                Logger.getLogger(PedirciclonuevosControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiartxtCiclo() {
        // <CODIGO_DESARROLLADO>
        ano = service.buscarEnLista(ciclo, numeroC, "ANO", listatxtCiclo);
        anoInicial = service.buscarEnLista(ciclo, numeroC, anoInicialC,
                        listatxtCiclo);
        periodo = service.buscarEnLista(ciclo, numeroC, "PERIODO",
                        listatxtCiclo);
        periodoInicial = service.buscarEnLista(ciclo, numeroC,
                        periodoInicialC, listatxtCiclo);

        // </CODIGO_DESARROLLADO>
    }

    private boolean validarAnoFinanciables() {
        // aqui hace la consulta de los a�os validos en los que
        // puede ingresar un ciclo
        String anoInicialPeriodo = null;
        String anoFinalPeriodo = null;
        try {
            HashMap<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);

            Registro registroPeriodo;

            registroPeriodo = RegistroConverter
                            .toRegistro(requestManager
                                            .get(UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PedirciclonuevosControladorUrlEnum.URL7149
                                                                                            .getValue())
                                                            .getUrl(),
                                                            param));

            if (registroPeriodo != null) {
                anoInicialPeriodo = registroPeriodo.getCampos().get(anoInicialC)
                                .toString();
                anoFinalPeriodo = registroPeriodo.getCampos().get("ANOFINAL")
                                .toString();
            }

            // aqui consulta los a�os registrados en el formulario
            // solicitud servicio en la pesta�a presupuesto
            // subformulario financiables y luego valida que los
            // a�os
            // registrados
            // pertenescan al ciclo valido que esta ingresando

            HashMap<String, Object> params = new HashMap<>();

            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(GeneralParameterEnum.CLASE.getName(), claseSolicitud);
            params.put(PedirciclonuevosControladorEnum.SOLICITUD.getValue(),
                            solicitud);

            List<Registro> registroFinanciables = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            PedirciclonuevosControladorUrlEnum.URL7361
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            params));

            if (registroFinanciables != null
                && !validarSolicitudFinanciables(anoInicialPeriodo,
                                anoFinalPeriodo, registroFinanciables)) {
                return false;
            }

        }
        catch (

        SystemException e)

        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;

    }

    private boolean validarSolicitudFinanciables(String anoInicialPeriodo,
        String anoFinalPeriodo, List<Registro> registroFinanciables) {

        for (Registro registroAno : registroFinanciables) {
            int anoV;
            String concepto = registroAno.getCampos()
                            .get(nombreConceptoC)
                            .toString();
            if (registroAno.getCampos().get(anoInicialC) != null) {

                anoV = Integer.parseInt(registroAno.getCampos()
                                .get(anoInicialC).toString());
            }
            else {
                JsfUtil.agregarMensajeAlerta(SysmanFunciones.concatenar(
                                idioma.getString("TB_TB1504"), " ", concepto));
                return false;
            }

            if ((anoV < Integer.parseInt(anoInicialPeriodo))
                || (anoV > Integer.parseInt(anoFinalPeriodo))) {

                JsfUtil.agregarMensajeAlerta(SysmanFunciones.concatenar(
                                idioma.getString("TB_TB1504"), " ", concepto));
                return false;
            }
        }

        return true;
    }

    private boolean validarPeriodoFinanciable() {
        try {

            HashMap<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CLASE.getName(), claseSolicitud);
            param.put(PedirciclonuevosControladorEnum.SOLICITUD.getValue(),
                            solicitud);
            List<Registro> registroPeriodoFinanciables;

            registroPeriodoFinanciables = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            PedirciclonuevosControladorUrlEnum.URL1313
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            param));

            if (registroPeriodoFinanciables != null
                && !validarRegistroPFinanciable(registroPeriodoFinanciables)) {

                return false;
            }

        }
        catch (SystemException e)

        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;

    }

    private boolean validarRegistroPFinanciable(
        List<Registro> registroPeriodoFinanciables) {

        for (Registro registro : registroPeriodoFinanciables) {
            String concepto = "";
            if (registro.getCampos().get(nombreConceptoC) != null) {
                concepto = registro.getCampos().get(nombreConceptoC)
                                .toString();
            }

            if (registro.getCampos().get(periodoInicialC) != null) {

                if (!validaMesPerInicial(registro, concepto)) {

                    return false;
                }
            }
            else {
                JsfUtil.agregarMensajeAlerta(SysmanFunciones.concatenar(
                                idioma.getString("TB_TB1505"), " ", concepto));
                return false;
            }

        }

        return true;
    }

    public boolean validaMesPerInicial(Registro registro, String concepto) {
        int mesValidoPeriodo = 0;
        int anoInicialVp = Integer.parseInt(registro.getCampos()
                        .get(anoInicialC).toString());

        try {
            HashMap<String, Object> param = new HashMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anoInicialVp);
            param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

            Registro mesValido;

            mesValido = RegistroConverter
                            .toRegistro(requestManager
                                            .get(UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            PedirciclonuevosControladorUrlEnum.URL6969
                                                                                            .getValue())
                                                            .getUrl(),
                                                            param));

            if (mesValido != null) {
                mesValidoPeriodo = Integer.parseInt(mesValido
                                .getCampos().get("MES").toString());
            }
            if (Integer.parseInt(registro.getCampos()
                            .get(periodoInicialC)
                            .toString()) < mesValidoPeriodo) {
                JsfUtil.agregarMensajeAlerta(SysmanFunciones.concatenar(
                                idioma.getString("TB_TB1505"), " ", concepto));
                return false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    private void datosUsuario() {

        HashMap<String, Object> usuario;
        usuario = new HashMap<>();
        usuario.put("PRIMERAPELLIDO",
                        registroSolicitudServicio.get("PRIMERAPELLIDO"));
        usuario.put("SEGUNDOAPELLIDO",
                        registroSolicitudServicio.get("SEGUNDOAPELLIDO"));
        usuario.put("NOMBRES", registroSolicitudServicio.get("NOMBRES"));
        usuario.put("TIPODOCUMENTO",
                        registroSolicitudServicio.get("TIPODOCUMENTO"));
        usuario.put("CODIGORUTA", registroSolicitudServicio.get("CODIGORUTA"));
        usuario.put("NIT", registroSolicitudServicio.get("NIT"));
        usuario.put("TELEFONO", registroSolicitudServicio.get("TELEFONO"));
        usuario.put("DIRGUIA", registroSolicitudServicio.get("DIRGUIA"));
        usuario.put("USO", registroSolicitudServicio.get("USO"));
        usuario.put(estratoC, registroSolicitudServicio.get(estratoC));
        usuario.put("ESTRATOALUMBRADO",
                        registroSolicitudServicio.get("ESTRATOALUMBRADO"));
        usuario.put("PERIODOSNOCOBROFAC",
                        registroSolicitudServicio.get("PerNoCobro"));
        usuario.put("CODIGOCATASTRAL",
                        registroSolicitudServicio.get("CODIGOCATASTRAL"));
        usuario.put("TIPOPREDIO",
                        registroSolicitudServicio.get("TIPODEPREDIO"));
        usuario.put("ESTRATOASEO", registroSolicitudServicio.get(estratoC));
        usuario.put("NUMERODIGITOS", registroSolicitudServicio.get("DIGITOS"));
        usuario.put(sectorC, registroSolicitudServicio.get(sectorC));
        usuario.put("SECCION", registroSolicitudServicio.get("SECCION"));
        usuario.put("MANZANA", registroSolicitudServicio.get("MANZANA"));
        usuario.put("SUBSECTOR", registroSolicitudServicio.get("SUBSECTOR"));
        usuario.put("REDHIDRAULICA",
                        registroSolicitudServicio.get("REDHIDRAULICA"));
        usuario.put("CODIGODANE", registroSolicitudServicio.get("CODIGODANE"));
        usuario.put(sectorC, registroSolicitudServicio.get(sectorC));
        usuario.put("LADO", registroSolicitudServicio.get("LADO"));
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getClaseSolicitud() {
        return claseSolicitud;
    }

    public void setClaseSolicitud(String claseSolicitud) {
        this.claseSolicitud = claseSolicitud;
    }

    public String getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(String solicitud) {
        this.solicitud = solicitud;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getAnoInicial() {
        return anoInicial;
    }

    public void setAnoInicial(String anoInicial) {
        this.anoInicial = anoInicial;
    }

    public String getPeriodoInicial() {
        return periodoInicial;
    }

    public void setPeriodoInicial(String periodoInicial) {
        this.periodoInicial = periodoInicial;
    }

    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    public String getCodigoFraude() {
        return codigoFraude;
    }

    public void setCodigoFraude(String codigoFraude) {
        this.codigoFraude = codigoFraude;
    }

    public Map<String, Object> getRegistroSolicitudServicio() {
        return registroSolicitudServicio;
    }

    public void setRegistroSolicitudServicio(
        Map<String, Object> registroSolicitudServicio) {
        this.registroSolicitudServicio = registroSolicitudServicio;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListatxtCiclo() {
        return listatxtCiclo;
    }

    public void setListatxtCiclo(List<Registro> listatxtCiclo) {
        this.listatxtCiclo = listatxtCiclo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
