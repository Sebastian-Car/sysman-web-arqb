package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosOchoRemote;
import com.sysman.serviciospublicos.enums.FrmconceptoespecialControladorEnum;
import com.sysman.serviciospublicos.enums.FrmconceptoespecialControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author acaceres
 * @version 1, 24/08/2016
 *
 * -- Modificado por lcortes 26,30,31/05/2017. Refactorizacion de
 * codigo de las listas para utilizar dss. Reemplazo de llamados a la
 * clase Acciones.
 */
@ManagedBean
@ViewScoped
public class FrmconceptoespecialControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private String codigoInicial;
    private String codigoFinal;
    private String cmbCiclo;
    private String cmbConcepto;
    private String uso;
    private String estratos;
    private String nombreCodInicial;
    private String nomCodFinal;
    private String txtValor;
    private String nombreConcepto;
    private String txtAno;
    private String txtPeriodo;
    private String txtCalculado;
    private String conceptos;
    private boolean dialogoVisible = false;
    private RegistroDataModelImpl listaMiCodigoInicial;
    private RegistroDataModelImpl listaMiCodigoFinal;
    private RegistroDataModelImpl listaCmbCiclo;
    private RegistroDataModelImpl listacmbConcepto;
    private RegistroDataModelImpl listaUso;
    private RegistroDataModelImpl listaEstrato;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosOchoRemote ejbServPublicosOcho;

    /**
     * Creates a new instance of FrmconceptoespecialControlador
     */
    public FrmconceptoespecialControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            numFormulario = GeneralCodigoFormaEnum.FRMCONCEPTOESPECIAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FrmconceptoespecialControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        mensajesInicioModal();
        // </CODIGO_DESARROLLADO>
    }

    public void mensajesInicioModal() {
        try {
            conceptos = ejbSysmanUtil.consultarParametro(compania,
                            "CONCEPTOS MANUALES", modulo, new Date(), true);
            if (SysmanFunciones.validarVariableVacio(conceptos)) {
                JsfUtil.agregarMensajeErrorDialogo(
                                idioma.getString("TB_TB3200"));
                return;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        cargarListaMiCodigoInicial();
        cargarListaMiCodigoFinal();
        cargarListaCmbCiclo();
        cargarListacmbConcepto();
        cargarListaUso();
        // </CARGAR_LISTA_COMBO_GRANDE>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaMiCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoespecialControladorUrlEnum.URL3466
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), cmbCiclo);

        listaMiCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmconceptoespecialControladorEnum.CODIGORUTA
                                        .getValue());
    }

    public void cargarListaMiCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoespecialControladorUrlEnum.URL4207
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), cmbCiclo);
        param.put(FrmconceptoespecialControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);

        listaMiCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, FrmconceptoespecialControladorEnum.CODIGORUTA
                                        .getValue());
    }

    public void cargarListaCmbCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoespecialControladorUrlEnum.URL4914
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.NUMERO.getName());
    }

    public void cargarListacmbConcepto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoespecialControladorUrlEnum.URL5718
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CONCEPTO.getName(), conceptos);

        listacmbConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaUso() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoespecialControladorUrlEnum.URL6356
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaUso = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaEstrato() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmconceptoespecialControladorUrlEnum.URL6978
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmconceptoespecialControladorEnum.USO.getValue(), uso);

        listaEstrato = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        dialogoVisible = true;
        // </CODIGO_DESARROLLADO>

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public void aceptarfacturacion() {
        // <CODIGO_DESARROLLADO>

        if (!SysmanFunciones.validarVariableVacio(cmbCiclo)
            || !SysmanFunciones.validarVariableVacio(txtValor)
            || !SysmanFunciones.validarVariableVacio(uso)
            || !SysmanFunciones.validarVariableVacio(estratos)) {

            if (SessionUtil.getNivelUsuario(modulo) != 9) {

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1351"));
                dialogoVisible = false;
                return;
            }

            try {
                int num = ejbServPublicosOcho.asignarConceptosGrupoUsuarios(
                                compania,
                                Integer.parseInt(cmbCiclo), codigoInicial,
                                codigoFinal, txtPeriodo,
                                Integer.parseInt(txtAno), cmbConcepto,
                                new BigDecimal(txtValor),
                                "T".equals(uso) ? ""
                                    : " AND USO    = ''" + uso +
                                        "'' ",
                                "T".equals(estratos) ? ""
                                    : " AND ESTRATO    = ''" + estratos
                                        + "'' ",
                                SessionUtil.getUser().getCodigo());

                if (num > 0) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1352").replace(
                                                    "s$numero$s",
                                                    String.valueOf(num))
                                        + " "
                                        + cmbConcepto + " "
                                        + idioma.getString("TB_TB76") + " "
                                        + txtValor
                                        + ".");
                }
                else {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1354").replace(
                                                    "s$numero$s",
                                                    String.valueOf(num)));
                }
            }
            catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
        else {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1353"));
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaMiCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = validarRegistroFila(registroAux,
                        FrmconceptoespecialControladorEnum.CODIGORUTA
                                        .getValue());
        nombreCodInicial = validarRegistroFila(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
        cargarListaMiCodigoFinal();
    }

    public void seleccionarFilaMiCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = validarRegistroFila(registroAux,
                        FrmconceptoespecialControladorEnum.CODIGORUTA
                                        .getValue());
        nomCodFinal = validarRegistroFila(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
    }

    public void seleccionarFilaCmbCiclo(SelectEvent event) {
        codigoInicial = null;
        codigoFinal = null;
        nombreCodInicial = null;
        nomCodFinal = null;
        Registro registroAux = (Registro) event.getObject();
        cmbCiclo = validarRegistroFila(registroAux,
                        GeneralParameterEnum.NUMERO.getName());
        codigoInicial = validarRegistroFila(registroAux,
                        FrmconceptoespecialControladorEnum.CODIGOINICIAL
                                        .getValue());
        codigoFinal = validarRegistroFila(registroAux,
                        FrmconceptoespecialControladorEnum.CODIGOFINAL
                                        .getValue());
        txtAno = validarRegistroFila(registroAux,
                        GeneralParameterEnum.ANO.getName());
        txtPeriodo = validarRegistroFila(registroAux,
                        GeneralParameterEnum.PERIODO.getName());
        txtCalculado = validarRegistroFila(registroAux,
                        FrmconceptoespecialControladorEnum.INDCALCULADO
                                        .getValue());
        cargarListaMiCodigoInicial();
        try {
            if (SysmanFunciones.validarVariableVacio(codigoInicial)) {
                nombreCodInicial = "";
            }
            else {

                Map<String, Object> fieldsLi = new TreeMap<>();
                fieldsLi.put(GeneralParameterEnum.CODIGORUTA.getName(),
                                codigoInicial);
                nombreCodInicial = listaMiCodigoInicial
                                .getRegistroUnico(fieldsLi)
                                .getCampos()
                                .get(GeneralParameterEnum.NOMBRE.getName())
                                .toString();

            }
            cargarListaMiCodigoFinal();
            if (SysmanFunciones.validarVariableVacio(codigoFinal)) {
                nomCodFinal = "";
            }
            else {
                Map<String, Object> fieldsLf = new TreeMap<>();
                fieldsLf.put(GeneralParameterEnum.CODIGORUTA.getName(),
                                codigoFinal);
                nomCodFinal = listaMiCodigoFinal.getRegistroUnico(fieldsLf)
                                .getCampos()
                                .get(GeneralParameterEnum.NOMBRE.getName())
                                .toString();
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilacmbConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cmbConcepto = validarRegistroFila(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        nombreConcepto = validarRegistroFila(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());

    }

    public void seleccionarFilaUso(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        uso = validarRegistroFila(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        estratos = null;
        cargarListaEstrato();
    }

    public void seleccionarFilaEstrato(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        estratos = validarRegistroFila(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
    }

    private String validarRegistroFila(Registro reg, String nombre) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), nombre) ? ""
            : reg.getCampos().get(nombre).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getCmbCiclo() {
        return cmbCiclo;
    }

    public void setCmbCiclo(String cmbCiclo) {
        this.cmbCiclo = cmbCiclo;
    }

    public String getCmbConcepto() {
        return cmbConcepto;
    }

    public void setCmbConcepto(String cmbConcepto) {
        this.cmbConcepto = cmbConcepto;
    }

    public String getUso() {
        return uso;
    }

    public void setUso(String uso) {
        this.uso = uso;
    }

    public String getEstratos() {
        return estratos;
    }

    public void setEstratos(String estratos) {
        this.estratos = estratos;
    }

    public String getNombreCodInicial() {
        return nombreCodInicial;
    }

    public void setNombreCodInicial(String nombreCodInicial) {
        this.nombreCodInicial = nombreCodInicial;
    }

    public String getNomCodFinal() {
        return nomCodFinal;
    }

    public void setNomCodFinal(String nomCodFinal) {
        this.nomCodFinal = nomCodFinal;
    }

    public String getTxtValor() {
        return txtValor;
    }

    public void setTxtValor(String txtValor) {
        this.txtValor = txtValor;
    }

    public String getNombreConcepto() {
        return nombreConcepto;
    }

    public void setNombreConcepto(String nombreConcepto) {
        this.nombreConcepto = nombreConcepto;
    }

    public String getTxtAno() {
        return txtAno;
    }

    public void setTxtAno(String txtAno) {
        this.txtAno = txtAno;
    }

    public String getTxtPeriodo() {
        return txtPeriodo;
    }

    public void setTxtPeriodo(String txtPeriodo) {
        this.txtPeriodo = txtPeriodo;
    }

    public String getTxtCalculado() {
        return txtCalculado;
    }

    public void setTxtCalculado(String txtCalculado) {
        this.txtCalculado = txtCalculado;
    }

    public boolean isDialogoVisible() {
        return dialogoVisible;
    }

    public void setDialogoVisible(boolean dialogoVisible) {
        this.dialogoVisible = dialogoVisible;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaMiCodigoInicial() {
        return listaMiCodigoInicial;
    }

    public void setListaMiCodigoInicial(
        RegistroDataModelImpl listaMiCodigoInicial) {
        this.listaMiCodigoInicial = listaMiCodigoInicial;
    }

    public RegistroDataModelImpl getListaMiCodigoFinal() {
        return listaMiCodigoFinal;
    }

    public void setListaMiCodigoFinal(
        RegistroDataModelImpl listaMiCodigoFinal) {
        this.listaMiCodigoFinal = listaMiCodigoFinal;
    }

    public RegistroDataModelImpl getListaCmbCiclo() {
        return listaCmbCiclo;
    }

    public void setListaCmbCiclo(RegistroDataModelImpl listaCmbCiclo) {
        this.listaCmbCiclo = listaCmbCiclo;
    }

    public RegistroDataModelImpl getListacmbConcepto() {
        return listacmbConcepto;
    }

    public void setListacmbConcepto(RegistroDataModelImpl listacmbConcepto) {
        this.listacmbConcepto = listacmbConcepto;
    }

    public RegistroDataModelImpl getListaUso() {
        return listaUso;
    }

    public void setListaUso(RegistroDataModelImpl listaUso) {
        this.listaUso = listaUso;
    }

    public RegistroDataModelImpl getListaEstrato() {
        return listaEstrato;
    }

    public void setListaEstrato(RegistroDataModelImpl listaEstrato) {
        this.listaEstrato = listaEstrato;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
