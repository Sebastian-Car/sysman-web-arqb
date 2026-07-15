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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosUnoRemote;
import com.sysman.serviciospublicos.enums.CambiorutaControladorEnum;
import com.sysman.serviciospublicos.enums.CambiorutaControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

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

/**
 *
 * @author acaceres
 * @version 1, 29/08/2016
 * @version 2.0, 16/05/2017 modificado por jcrodriguez
 * Descripcion:*Depuaracion del controlador *Refactoring y creacion de
 * dss
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class CambiorutaControlador extends BeanBaseModal {
    /**
     * variable que almacena la compa�ia
     */
    private final String compania;
    /**
     * variable que almacena el modulo
     */
    private final String modulo;
    /**
     * variable que almacena el codigo ruta
     */
    private String codigoRuta;
    /**
     * variable que almacena el ciclo
     */
    private String ciclo;
    /**
     * variable que almacena el nombre del codigo ruta
     */
    private String nombreCodigoRuta;
    /**
     * variable que almacena el nuevo codigo ruta
     */
    private String nuevoCodigoRuta;
    /**
     * variable que almacena el prefacturado
     */
    private String prefacturado;
    /**
     * variable que almacena una cadena
     */
    private String fimm;
    /**
     * variable que almacena una lista de ciclos
     */
    private List<Registro> listaCiclo;
    /**
     * variable que almacena la lista de codigos de ruta
     */
    private RegistroDataModelImpl listaCodigoRuta;
    /**
     * EJB
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbServiciosPublicosUnoRemote ejbServiciosPublicosUno;

    /**
     * Creates a new instance of CambiorutaControlador
     */
    public CambiorutaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            numFormulario = GeneralCodigoFormaEnum.CAMBIORUTA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(CambiorutaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * metodo heredado del bean padre
     */
    @PostConstruct
    public void inicializar() {
        cargarListaCiclo();
        abrirFormulario();
    }

    /**
     * metodo heredado del bean padre
     */
    @Override
    public void abrirFormulario() {
        // metodo que se utiliza al abrir el formulario
    }

    /**
     * metodo que se llama al cargar la lista de ciclos
     */
    public void cargarListaCiclo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiorutaControladorUrlEnum.URL3050
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que se llama para carga la lista de codigo ruta
     */
    public void cargarListaCodigoRuta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CambiorutaControladorUrlEnum.URL3484
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCodigoRuta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGORUTA.getName());
    }

    /**
     * metodo que se llama al oprimir el boton
     */
    public void oprimirCmdCambiar() {
        int cont = 0;
        if (SysmanFunciones.validarVariableVacio(nuevoCodigoRuta)
            && "P".equals(fimm)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            CambiorutaControladorEnum.TB_TB1373.getValue()));
            return;
        }

        List<Registro> rs;
        try {
            rs = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiorutaControladorUrlEnum.URL4633
                                                                            .getValue())
                                            .getUrl(), null));

            if (!rs.isEmpty()) {
                JsfUtil.agregarMensajeError(idioma.getString(
                                CambiorutaControladorEnum.TB_TB1374.getValue())
                    + " "
                    + rs.get(cont).getCampos().get(
                                    GeneralParameterEnum.CICLO.getName())
                    + " "
                    + idioma.getString(CambiorutaControladorEnum.TB_TB1375
                                    .getValue())
                    + " "
                    + rs.get(cont).getCampos().get(
                                    GeneralParameterEnum.CODIGORUTA.getName())
                    + " "
                    + idioma.getString(CambiorutaControladorEnum.TG_NOMBRE5
                                    .getValue())
                    + " "
                    + rs.get(cont).getCampos()
                                    .get(CambiorutaControladorEnum.SEGUNDOAPELLIDO
                                                    .getValue())
                    + "' '"
                    + rs.get(cont).getCampos()
                                    .get(CambiorutaControladorEnum.PRIMERAPELLIDO
                                                    .getValue())
                    + "' '"
                    + rs.get(cont).getCampos()
                                    .get(CambiorutaControladorEnum.NOMBRES
                                                    .getValue())
                    + "' ");
                return;
            }
            HashMap<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.CODIGORUTA.getName(), codigoRuta);
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            rs = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiorutaControladorUrlEnum.URL5469
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs.isEmpty()) {
                JsfUtil.agregarMensajeError(idioma
                                .getString(CambiorutaControladorEnum.TB_TB1376
                                                .getValue()));
                return;
            }
            param = new HashMap<>();
            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            nuevoCodigoRuta);
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            rs = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiorutaControladorUrlEnum.URL5469
                                                                            .getValue())
                                            .getUrl(), param));

            if (!rs.isEmpty()) {
                JsfUtil.agregarMensajeError(idioma
                                .getString(CambiorutaControladorEnum.TB_TB1377
                                                .getValue()));
                return;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        cambioDeRuta();
    }

    /**
     * metodo que obtiene un parametro de acuerdo al nombre
     *
     * @param nombre
     * @param indMayus
     * @return
     */
    private String getParametro(String nombre, boolean indMayus) {
        try {
            return ejbSysmanUtil.consultarParametro(compania, nombre, modulo,
                            new Date(), indMayus);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    /**
     * metodo que se llama al cambiar la ruta
     */
    private void cambioDeRuta() {
        try {

            ejbServiciosPublicosUno.cambiarRuta(compania,
                            compania,
                            Integer.parseInt(ciclo),
                            Integer.parseInt(ciclo),
                            nuevoCodigoRuta,
                            codigoRuta,
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(idioma.getString(
                            CambiorutaControladorEnum.TB_TB1379.getValue()));
            // Actualizar Rangos
            HashMap<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            Registro rsA = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CambiorutaControladorUrlEnum.URL5429
                                                                            .getValue())
                                            .getUrl(), param));
            if (rsA != null) {
                param.put(CambiorutaControladorEnum.CODIGOINICIAL.getValue(),
                                rsA.getCampos().get(
                                                CambiorutaControladorEnum.PRIMERO
                                                                .getValue()));
                param.put(CambiorutaControladorEnum.CODIGOFINAL.getValue(),
                                rsA.getCampos().get(
                                                CambiorutaControladorEnum.ULTIMO
                                                                .getValue()));
                param.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                param.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                CambiorutaControladorUrlEnum.URL7588
                                                                .getValue());
                Parameter parameter = new Parameter();
                parameter.setFields(param);
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                parameter);
            }

        }
        catch (NumberFormatException | SystemException e) {
            Logger.getLogger(CambiorutaControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que se llama al cambiar el ciclo
     */
    public void cambiarCiclo() {
        // <CODIGO_DESARROLLADO>
        codigoRuta = "";
        nombreCodigoRuta = "";
        String parametro;

        prefacturado = "false".equals(
                        service.buscarEnLista(ciclo,
                                        GeneralParameterEnum.NUMERO.getName(),
                                        CambiorutaControladorEnum.PREFACTURANDO
                                                        .getValue(),
                                        listaCiclo)) ? "0" : "-1";
        parametro = getParametro(parametros
                        .getString(CambiorutaControladorEnum.PR_MANEJA_PREFACTURACION
                                        .getValue()),
                        true);
        if (SysmanFunciones.validarVariableVacio(parametro)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            CambiorutaControladorEnum.TB_TB1380.getValue()));
            return;
        }
        if (CambiorutaControladorEnum.SI.getValue().equals(parametro)
            && "0".equals(prefacturado)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            CambiorutaControladorEnum.TB_TB1378.getValue()));
            return;
        }

        cargarListaCodigoRuta();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que se utiliza para seleccionar un registro o fila de un
     * combo grande
     */
    public void seleccionarFilaCodigoRuta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoRuta = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGORUTA.getName())
                        .toString();
        nombreCodigoRuta = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        fimm = registroAux.getCampos()
                        .get(CambiorutaControladorEnum.FIMM.getValue()) == null
                            ? ""
                            : registroAux.getCampos()
                                            .get(CambiorutaControladorEnum.FIMM
                                                            .getValue())
                                            .toString();
    }

    /**
     * metodos get y set
     *
     * @return
     */
    public String getCodigoRuta() {
        return codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this.codigoRuta = codigoRuta;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getNombreCodigoRuta() {
        return nombreCodigoRuta;
    }

    public void setNombreCodigoRuta(String nombreCodigoRuta) {
        this.nombreCodigoRuta = nombreCodigoRuta;
    }

    public String getNuevoCodigoRuta() {
        return nuevoCodigoRuta;
    }

    public void setNuevoCodigoRuta(String nuevoCodigoRuta) {
        this.nuevoCodigoRuta = nuevoCodigoRuta;
    }

    public String getPrefacturado() {
        return prefacturado;
    }

    public void setPrefacturado(String prefacturado) {
        this.prefacturado = prefacturado;
    }

    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    public RegistroDataModelImpl getListaCodigoRuta() {
        return listaCodigoRuta;
    }

    public void setListaCodigoRuta(RegistroDataModelImpl listaCodigoRuta) {
        this.listaCodigoRuta = listaCodigoRuta;
    }
}
