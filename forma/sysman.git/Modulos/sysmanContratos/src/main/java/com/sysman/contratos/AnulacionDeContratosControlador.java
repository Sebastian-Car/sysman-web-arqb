package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.ejb.EjbContratosUnoRemote;
import com.sysman.contratos.enums.AnulacionDeContratosControladorEnum;
import com.sysman.contratos.enums.AnulacionDeContratosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

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

/**
 *
 * @author ngomez
 * @version 1, 11/11/2015
 * 
 * @author asana
 * @version 2, 03/08/2017 Se realiza refactoring de controlador, se
 * modifica llamado a funcion por EJB.
 */
@ManagedBean
@ViewScoped
public class AnulacionDeContratosControlador extends BeanBaseModal {

    private final String compania;
    private String tipo;
    private String numero;
    private String auxTipo;
    private String auxTipoNombre;
    private boolean cuadroVisible = false;
    private List<Registro> listaTipo;
    private RegistroDataModelImpl listaNumero;
    private String mensaje;

    @EJB
    private EjbContratosUnoRemote ejbContratosUnoRemote;

    /**
     * Creates a new instance of AnulacionDeContratosControlador
     */
    public AnulacionDeContratosControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.ANULACION_DE_CONTRATOS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        try {
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(AnulacionDeContratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListatipo();
        cargarListanumero();
        abrirFormulario();
    }

    public void cargarListatipo() {

        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            listaTipo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AnulacionDeContratosControladorUrlEnum.URL2158
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListanumero() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(AnulacionDeContratosControladorEnum.PARAM1.getValue(),
                        auxTipo);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnulacionDeContratosControladorUrlEnum.URL2589
                                                        .getValue());
        listaNumero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");

    }

    public void oprimirAnular() {
        // <CODIGO_DESARROLLADO>

        Registro aux;
        try {
            HashMap<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(AnulacionDeContratosControladorEnum.PARAM0.getValue(),
                            tipo);
            param.put(AnulacionDeContratosControladorEnum.PARAM2.getValue(),
                            numero);

            aux = RegistroConverter
                            .toRegistro(requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            AnulacionDeContratosControladorUrlEnum.URL4681
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
            if (aux != null && aux.getCampos().get("ESTADO") != null) {
                if ("A".equals(aux.getCampos().get("ESTADO"))) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2026"));
                }
                else {
                    cuadroVisible = true;
                    mensaje = idioma.getString("TB_TB3358")
                                    .replace("auxTipoNombre", auxTipoNombre)
                                    .replace("numero", numero);
                }
            }
            else {
                cuadroVisible = true;
                mensaje = idioma.getString("TB_TB3358")
                                .replace("auxTipoNombre", auxTipoNombre)
                                .replace("numero", numero);
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTipo() {
        // <CODIGO_DESARROLLADO>
        auxTipo = "".equals(tipo) || null == tipo ? "" : tipo;
        numero = null;
        if (!"".equals(auxTipo)) {
            auxTipoNombre = service.buscarEnLista(tipo, "CODIGO", "NOMBRE",
                            listaTipo);
        }
        cargarListanumero();
        // </CODIGO_DESARROLLADO>
    }

    public void aceptarMensaje() {
        cuadroVisible = false;
        try {
            String aux = ejbContratosUnoRemote.anularOrdendeCompra(compania,
                            tipo, Long.parseLong(numero),
                            SessionUtil.getUser().getCodigo());

            if ("1".equals(aux)) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3368"));

            }

        }
        catch (NumberFormatException | SystemException ex) {
            Logger.getLogger(AnulacionDeContratosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void seleccionarFilaNumero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numero = (registroAux.getCampos().get("NUMERO"))
                        .toString();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public List<Registro> getListaTipo() {
        return listaTipo;
    }

    public void setListaTipo(List<Registro> listaTipo) {
        this.listaTipo = listaTipo;
    }

    public String getCompania() {
        return compania;
    }

    public RegistroDataModelImpl getListaNumero() {
        return listaNumero;
    }

    public void setListaNumero(RegistroDataModelImpl listaNumero) {
        this.listaNumero = listaNumero;
    }

    public String getAuxTipo() {
        return auxTipo;
    }

    public void setAuxTipo(String auxTipo) {
        this.auxTipo = auxTipo;
    }

    public String getAuxTipoNombre() {
        return auxTipoNombre;
    }

    public void setAuxTipoNombre(String auxTipoNombre) {
        this.auxTipoNombre = auxTipoNombre;
    }

    public boolean isCuadroVisible() {
        return cuadroVisible;
    }

    public void setCuadroVisible(boolean cuadroVisible) {
        this.cuadroVisible = cuadroVisible;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
