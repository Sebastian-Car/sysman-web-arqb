package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.FrmautorizaabonosControladorEnum;
import com.sysman.serviciospublicos.enums.FrmautorizaabonosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author NGOMEZ
 * @version 1, 29/08/2016
 *
 * @author eamaya
 * @version 2, 11/05/2017 Proceso de Refactoring DSS, Correcciones
 * SonarLint y Manejo de EJBs
 *
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el código: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 * 
 * @author eamaya
 * @version 3.1, 13/09/2017, Correccion de conexiones y cambio de
 * numero de formulario por enum
 * 
 */
@ManagedBean
@ViewScoped

public class FrmautorizaabonosControlador extends BeanBaseModal {
    private final String compania;
    private final String codigoInterno;

    // <DECLARAR_ATRIBUTOS>
    private String ciclo;
    private String ruta;
    private String interno;
    private String anio;
    private String periodo;
    private String nombre;
    private String deuda;
    private String abono;
    private String user;
    private String factura;
    private String borrado;
    private String procBanco;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listatxtCiclo;
    private RegistroDataModelImpl listacmbRuta;
    private RegistroDataModelImpl listacmbInterno;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbConsecutivo;

    /**
     * Creates a new instance of FrmautorizaabonosControlador
     */
    public FrmautorizaabonosControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoInterno = "CODIGOINTERNO";
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMAUTORIZAABONOS_CONTROLADOR
                            .getCodigo();
            // 1074
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmautorizaabonosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListatxtCiclo();
        cargarListacmbRuta();
        cargarListacmbInterno();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        user = SessionUtil.getUser().getCodigo();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListatxtCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmautorizaabonosControladorUrlEnum.URL3372
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listatxtCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");
    }

    public void cargarListacmbRuta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmautorizaabonosControladorUrlEnum.URL3844
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmautorizaabonosControladorEnum.PARAM0.getValue(),
                        ciclo);

        listacmbRuta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGORUTA.getName());
    }

    public void cargarListacmbInterno() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmautorizaabonosControladorUrlEnum.URL4702
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(FrmautorizaabonosControladorEnum.PARAM0.getValue(),
                        ciclo);

        listacmbInterno = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoInterno);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCmdRegistrar() {

        try {
            // <CODIGO_DESARROLLADO>
            if (!validarRegistro()) {
                return;
            }
            Map<String, Object> parameter = new TreeMap<>();

            parameter.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parameter.put(GeneralParameterEnum.CICLO.getName(),
                            ciclo);

            parameter.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            ruta);

            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmautorizaabonosControladorUrlEnum.URL5166
                                                                            .getValue())
                                            .getUrl(), parameter));

            if (!"0".equals(regAux.getCampos().get("CUENTA").toString())) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1394"));
                return;
            }
            else {

                HashMap<String, Object> parame = new HashMap<>();
                parame.put(FrmautorizaabonosControladorEnum.PARAM2.getValue(),
                                abono);
                parame.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                parame.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                parame.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                parame.put(GeneralParameterEnum.CICLO.getName(), ciclo);

                parame.put(GeneralParameterEnum.ANO.getName(), anio);

                parame.put(GeneralParameterEnum.CODIGORUTA.getName(), ruta);

                parame.put(GeneralParameterEnum.PERIODO.getName(), periodo);

                Parameter para = new Parameter();

                para.setFields(parame);

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmautorizaabonosControladorUrlEnum.URL2857
                                                                .getValue());
                requestManager.update(urlUpdate.getUrl(),
                                urlUpdate.getMetodo(),
                                para);

                long intConsec = ejbConsecutivo
                                .generarSiguienteConsecutivo("SP_ABONOS",
                                                "COMPANIA=" + compania
                                                    + "AND CICLO = " + ciclo
                                                    + " AND CODIGORUTA= " + ruta
                                                    + "AND ANO=" + anio
                                                    + " AND PERIODO=" + periodo,
                                                "CONSECUTIVO");

                HashMap<String, Object> parSet = new HashMap<>();

                parSet.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parSet.put(GeneralParameterEnum.CICLO.getName(),
                                ciclo);

                parSet.put(GeneralParameterEnum.CODIGORUTA.getName(),
                                ruta);
                parSet.put(GeneralParameterEnum.ANO.getName(),
                                anio);

                parSet.put(GeneralParameterEnum.PERIODO.getName(),
                                periodo);

                parSet.put(FrmautorizaabonosControladorEnum.PARAM2.getValue(),
                                abono);

                parSet.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                intConsec);

                parSet.put(FrmautorizaabonosControladorEnum.PARAM1.getValue(),
                                interno);

                parSet.put(GeneralParameterEnum.USUARIO.getName(),
                                user);

                parSet.put(FrmautorizaabonosControladorEnum.PARAM3.getValue(),
                                new Date());

                parSet.put(FrmautorizaabonosControladorEnum.PARAM4.getValue(),
                                factura);

                parSet.put(FrmautorizaabonosControladorEnum.PARAM5.getValue(),
                                deuda);

                parSet.put(GeneralParameterEnum.CREATED_BY
                                .getName(),
                                SessionUtil.getUser()
                                                .getCodigo());

                parSet.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());

                Parameter paramter = new Parameter();
                paramter.setFields(parSet);

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmautorizaabonosControladorUrlEnum.URL1234
                                                                .getValue());
                requestManager.save(urlCreate.getUrl(),
                                urlCreate.getMetodo(),
                                paramter);

                Map<String, Object> parametro = new TreeMap<>();

                parametro.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                regAux = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmautorizaabonosControladorUrlEnum.URL6581
                                                                                .getValue())
                                                .getUrl(), parametro));

                String strReciboInicial = "0";

                if (regAux != null) {
                    strReciboInicial = SysmanFunciones.strZero(
                                    regAux.getCampos().get("CONSECUTIVOREAL")
                                                    .toString(),
                                    10);
                }

                Map<String, Object> param = new TreeMap<>();

                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
                param.put(GeneralParameterEnum.ANO.getName(), anio);
                param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

                regAux = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FrmautorizaabonosControladorUrlEnum.URL6969
                                                                                .getValue())
                                                .getUrl(), param));

                Map<String, Object> parametrosP = new HashMap<>();
                if (!"0".equals(regAux.getCampos().get("CUENTA"))) {
                    Map<String, Object> rid = new HashMap<>();
                    rid.put("COMPANIA", compania);
                    rid.put("CICLO", ciclo);
                    rid.put("ANO", anio);
                    rid.put("PERIODO", periodo);
                    parametrosP.put("rid", rid);
                }
                parametrosP.put("ciclo", ciclo);
                parametrosP.put("ano", anio);
                parametrosP.put("periodo", periodo);
                parametrosP.put("codigoInicial", ruta);
                parametrosP.put("codigoFinal", ruta);
                parametrosP.put("marca", "3");
                parametrosP.put("marcaIni", "3");
                parametrosP.put("noReciboInicial", strReciboInicial);
                Direccionador direccionador = new Direccionador();
                direccionador.setNumForm("1076");
                direccionador.setParametros(parametrosP);

                RequestContext.getCurrentInstance().closeDialog(direccionador);

            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    private boolean validarRegistro() {
        if ("0".equals(factura)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1391"));
            return false;
        }
        if (!"false".equals(borrado)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1392"));
            return false;
        }
        if ((procBanco != null) && !procBanco.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1393"));
            return false;
        }
        return true;
    }

    public void oprimirCmdNuevo() {
        // <CODIGO_DESARROLLADO>
        ciclo = null;
        ruta = null;
        interno = null;
        anio = null;
        periodo = null;
        nombre = null;
        deuda = null;
        abono = null;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiartxtAbono() {
        // <CODIGO_DESARROLLADO>
        if ((deuda != null) && !"".equals(deuda)) {
            if (Double.parseDouble(abono) >= Double.parseDouble(deuda)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1372"));
                abono = "0";
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1381"));
            abono = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilatxtCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = SysmanFunciones.nvl(registroAux.getCampos().get("NUMERO"), "")
                        .toString();
        anio = SysmanFunciones.nvl(registroAux.getCampos().get("ANO"), "")
                        .toString();
        periodo = SysmanFunciones
                        .nvl(registroAux.getCampos().get("PERIODO"), "")
                        .toString();
        ruta = null;
        interno = null;
        nombre = null;
        deuda = null;
        abono = null;
        cargarListacmbRuta();
        cargarListacmbInterno();
    }

    public void seleccionarFilacmbRuta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ruta = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGORUTA
                                                        .getName()),
                                        "")
                        .toString();

        nombre = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMUSUARIO"), "")
                        .toString();

        interno = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoInterno), "")
                        .toString();

        deuda = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("TOTFACTURAPERACTUAL"), "").toString();

        factura = SysmanFunciones
                        .nvl(registroAux.getCampos().get("FACTURA"), "")
                        .toString();

        borrado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("AUTORIZARBORRADO"),
                                        "")
                        .toString();

        procBanco = SysmanFunciones
                        .nvl(registroAux.getCampos().get("BANCOPERPROCESO"), "")
                        .toString();
    }

    public void seleccionarFilacmbInterno(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        interno = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigoInterno), "")
                        .toString();

        nombre = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMUSUARIO"), "")
                        .toString();

        ruta = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGORUTA
                                                        .getName()),
                                        "")
                        .toString();

        deuda = SysmanFunciones
                        .nvl(registroAux.getCampos().get("TOTFACTURAPERACTUAL"),
                                        "")
                        .toString();

        factura = SysmanFunciones
                        .nvl(registroAux.getCampos().get("FACTURA"), "")
                        .toString();

        borrado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("AUTORIZARBORRADO"),
                                        "")
                        .toString();

        procBanco = SysmanFunciones
                        .nvl(registroAux.getCampos().get("BANCOPERPROCESO"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getInterno() {
        return interno;
    }

    public void setInterno(String interno) {
        this.interno = interno;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDeuda() {
        return deuda;
    }

    public void setDeuda(String deuda) {
        this.deuda = deuda;
    }

    public String getAbono() {
        return abono;
    }

    public void setAbono(String abono) {
        this.abono = abono;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public String getBorrado() {
        return borrado;
    }

    public void setBorrado(String borrado) {
        this.borrado = borrado;
    }

    public String getProcBanco() {
        return procBanco;
    }

    public void setProcBanco(String procBanco) {
        this.procBanco = procBanco;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListatxtCiclo() {
        return listatxtCiclo;
    }

    public void setListatxtCiclo(RegistroDataModelImpl listatxtCiclo) {
        this.listatxtCiclo = listatxtCiclo;
    }

    public RegistroDataModelImpl getListacmbRuta() {
        return listacmbRuta;
    }

    public void setListacmbRuta(RegistroDataModelImpl listacmbRuta) {
        this.listacmbRuta = listacmbRuta;
    }

    public RegistroDataModelImpl getListacmbInterno() {
        return listacmbInterno;
    }

    public void setListacmbInterno(RegistroDataModelImpl listacmbInterno) {
        this.listacmbInterno = listacmbInterno;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
