package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.ejb.EjbPrecontractualCeroRemote;
import com.sysman.precontractual.enums.TipocontratosControladorEnum;
import com.sysman.precontractual.enums.TipocontratosControladorUrlEnum;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author esarmiento
 * @version 1, 09/12/2015
 * 
 * @version 2, 04/09/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla, de datos y en los
 * subformulario.
 */

@ManagedBean
@ViewScoped
public class TipocontratosControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String modulo;
    private final String consValorPredet;
    private List<Registro> listaModalidad;
    private String modelo;
    private String nombreModelo;
    private String valorPredeterminado;
    private boolean tipoTexto;
    private Registro registroSubetapaSub;
    private Registro registroSubSubVariables;
    private Registro registroSubModeloContrato;
    private List<Registro> listacuadroModelo;
    private List<Registro> listaModelo;
    private List<Registro> listaEtapasub;
    private List<Registro> listaSubvariables;
    private List<Registro> listaModelocontrato;
    private boolean bloqueadoTipo;
    private String mascaraValor;
    private boolean bloqueadoValor;
    private int numFila;

    @EJB
    private EjbPrecontractualCeroRemote ejbPrecontractualCero;

    public TipocontratosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        consValorPredet="VRPREDETERMINADO";
        numFormulario = GeneralCodigoFormaEnum.TIPOCONTRATOS_CONTROLADOR.getCodigo();
        try {
            registro = new Registro(new HashMap<String, Object>());
            registroSubetapaSub = new Registro(new HashMap<String, Object>());
            registroSubSubVariables = new Registro(
                            new HashMap<String, Object>());

            validarPermisos();
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(TipocontratosControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase= GenericUrlEnum.TIPOCONTRATO_PR;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    }

    public void oprimirbntGenerar() {
        //METODO NO IMPLEMENTADO
    }

    @Override
    public void iniciarListas() {

        cargarListaTexto16();
    }

    @Override
    public void iniciarListasSub() {
        cargarListaEtapasub();
        cargarListaSubvariables();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaEtapasub = null;
        listaSubvariables = null;
        listaModelocontrato = null;
    }

    public void cargarListaTexto16() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

            listaModalidad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipocontratosControladorUrlEnum.URL3624
                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaEtapasub() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.TIPOCONTRATO.getName(),registro.getCampos().get(GeneralParameterEnum.TIPOCONTRATO.getName()));

            listaEtapasub = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipocontratosControladorUrlEnum.URL3991
                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "ETAPA_PR"));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaSubvariables() {
        try {
            Map<String,Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
            param.put(GeneralParameterEnum.TIPOCONTRATO.getName(),registro.getCampos().get(GeneralParameterEnum.TIPOCONTRATO.getName()));

            listaSubvariables = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TipocontratosControladorUrlEnum.URL8972
                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "VARIABLE"));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void agregarRegistroSubEtapasub() {
        try {
            registroSubetapaSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
            registroSubetapaSub.getCampos().put(GeneralParameterEnum.TIPOCONTRATO.getName(),registro.getCampos().get(GeneralParameterEnum.TIPOCONTRATO.getName()));
            registroSubetapaSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubetapaSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            UrlBean urlCreate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GenericUrlEnum.ETAPA_PR.getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubetapaSub.getCampos());
            cargarListaEtapasub();
            JsfUtil.agregarMensajeInformativo(idioma.getString(Constantes.MSM_REGISTRO_INGRESADO));
        }
        catch (SystemException ex) {
            Logger.getLogger(TipocontratosControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubetapaSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubEtapasub(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove("TIPOD");
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(TipocontratosControladorEnum.PARAM0.getValue());
            reg.getCampos().put(
                            GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(
                            GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            
            UrlBean urlUpdate = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GenericUrlEnum.ETAPA_PR.getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(TipocontratosControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaEtapasub();
        }
    }

    public void eliminarRegSubEtapasub(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GenericUrlEnum.ETAPA_PR.getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaEtapasub();
        }
        catch (SystemException ex) {
            Logger.getLogger(TipocontratosControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionEtapasub() {
        cargarListaEtapasub();
        cargarListaSubvariables();
    }

    public void ejecutarformula() {
        if ("5".equals(SysmanFunciones.nvl(
                        registroSubSubVariables.getCampos().get("TIPO"), "")
                        .toString())) {
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.INPUTFORMULAVAR_CONTROLADOR
                                            .getCodigo()));
            SessionUtil.redireccionarForma(direccionador, modulo);
        }
    }
    
    public void ejecutarformulaContinuo(){
        if ("5".equals(SysmanFunciones
                        .nvl(listaSubvariables.get(numFila % 10).getCampos().get("TIPO"), "")
                        .toString())) {
            Direccionador direccionador = new Direccionador();
            direccionador.setNumForm(Integer
                            .toString(GeneralCodigoFormaEnum.INPUTFORMULAVAR_CONTROLADOR
                                            .getCodigo()));
            SessionUtil.redireccionarForma(direccionador, modulo);
        }
    }
    
    public boolean validarCampo(String tipo, String obj){
        boolean verificar = true;
        if ("3".equals(tipo)) {
            verificar = validarValorDouble(consValorPredet,
                            obj);
        }
        return verificar;
    }

    public void agregarRegistroSubSubvariables() {
        try {
            registroSubSubVariables.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
            registroSubSubVariables.getCampos().put(GeneralParameterEnum.TIPOCONTRATO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.TIPOCONTRATO.getName()));
            registroSubSubVariables.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSubSubVariables.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            if("5".equals(SysmanFunciones.nvl(
                            registroSubSubVariables.getCampos().get("TIPO"), "")
                            .toString())){
                registroSubSubVariables.getCampos().put(consValorPredet,
                                SessionUtil.getSessionVar("variable"));                
            }
            
            
            if (validarCampo(SysmanFunciones.nvl(
                            registroSubSubVariables.getCampos().get("TIPO"), "")
                            .toString(), registroSubSubVariables.getCampos()
                                            .get(consValorPredet)
                                            .toString())) {
                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.VARIABLE
                                                                .getCreateKey());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                registroSubSubVariables.getCampos());

                cargarListaSubvariables();
                JsfUtil.agregarMensajeInformativo(idioma
                                .getString(Constantes.MSM_REGISTRO_INGRESADO));
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(TipocontratosControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubSubVariables = new Registro(
                            new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubvariables(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove("TIPOV");
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(GeneralParameterEnum.TIPOCONTRATO.getName());
            
            if ("5".equals(SysmanFunciones
                            .nvl(reg.getCampos().get("TIPO"), "")
                            .toString())) {
                reg.getCampos().put(consValorPredet,
                                SessionUtil.getSessionVar("variable"));
            }
            
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            
            if (validarCampo(SysmanFunciones.nvl(
                            reg.getCampos()
                                            .get("TIPO"),
                            "")
                            .toString(),
                            reg.getCampos()
                                            .get(consValorPredet)
                                            .toString())) {

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.VARIABLE
                                                                .getUpdateKey());
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                reg.getCampos(), reg.getLlave());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(TipocontratosControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaSubvariables();
        }
    }

    public void eliminarRegSubSubvariables(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(GenericUrlEnum.VARIABLE.getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaSubvariables();
        }
        catch (SystemException ex) {
            Logger.getLogger(TipocontratosControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionSubvariables() {
        cargarListaSubvariables();
    }

    public void oprimirDoc(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(TipocontratosControladorEnum.PARAM1.getValue(), reg.getCampos().get(GeneralParameterEnum.TIPOCONTRATO.getName()).toString());
        parametros.put(TipocontratosControladorEnum.PARAM2.getValue(), reg.getCampos().get("IDETAPA").toString());
        parametros.put(TipocontratosControladorEnum.PARAM3.getValue(), reg.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName()) == null ? ""
            : reg.getCampos().get(GeneralParameterEnum.DESCRIPCION.getName())
            .toString());
        
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.ETAPA_PRE_DOC_CONTROLADOR.getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirActualizarEtapas() {
        try {
            // <CODIGO_DESARROLLADO>
            ejbPrecontractualCero.actualizarEtapas(compania,
                            registro.getCampos().get(GeneralParameterEnum.TIPOCONTRATO.getName()).toString(),
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2204"));
            // </CODIGO_DESARROLLADO>
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                            + ex.getMessage());
            Logger.getLogger(TipocontratosControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        }
    }

    public void oprimirActualizarVariables() {
        // <CODIGO_DESARROLLADO>
        try {
            // <CODIGO_DESARROLLADO>
            ejbPrecontractualCero.actualizarVariables(compania, registro.getCampos()
                            .get(GeneralParameterEnum.TIPOCONTRATO.getName()).toString(), SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB2205"));
            // </CODIGO_DESARROLLADO>
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA") + ex.getMessage());
            Logger.getLogger(TipocontratosControlador.class.getName())
            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirDisenarModelo(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirarchivo(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirguardar(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        tipoTexto = false;

    }

    @Override
    public void cargarRegistro() {
        precargarRegistro();
        switch (accion) {
        case "v":
        case "m":
            bloqueadoTipo = true;
            break;
        case "i":
            bloqueadoTipo = false;
            break;
        default:
            break;
        }

    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        return true;
    }

    public void aceptardgTexto() {
        //METODO NO IMPLEMENTADO
    }

    public void cambiarvrPredeterminadoC(int rowNum) {
        valorPredeterminado = (String) listaSubvariables.get(rowNum % 10)
                        .getCampos().get(consValorPredet);
    }
    
    public void cambiarTipo() {
        bloqueadoValor=false;
        switch (SysmanFunciones
                        .nvl(registroSubSubVariables.getCampos().get("TIPO"), "")
                        .toString()) {
        case "4":
            mascaraValor = "99/99/9999";
            break;
        case "6":
            mascaraValor = "99:99:99";
            break;
        case "5":
            bloqueadoValor=true;
            break;
        default:
            break;
        }
    }
    
    public void cambiarTipoC(int rowNum){
        bloqueadoValor=false;
        numFila=rowNum;
        switch (SysmanFunciones
                        .nvl(listaSubvariables.get(rowNum % 10).getCampos().get("TIPO"), "")
                        .toString()) {
        case "4":
            mascaraValor = "99/99/9999";
            break;
        case "6":
            mascaraValor = "99:99:99";
            break;
        case "5":
            bloqueadoValor=true;
            break;
        default:
            break;
        }
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        return true;
    }
    
    private boolean validarValorDouble(String campo, String valor) {
        try {
            double mValor = Double.parseDouble(valor.contains(",")
                ? valor.replace(",", ".") : valor);

            registro.getCampos().put(campo, mValor);
        }
        catch (NumberFormatException nfex) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3639")
                            .replace("#CAMPO#", campo));
            return false;
        }

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


    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void onRowSelectmodelos(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        modelo = (String) registroAux.getCampos().get("CODIGO");
    }

    public String getNombreModelo() {
        return nombreModelo;
    }

    public void setNombreModelo(String nombreModelo) {
        this.nombreModelo = nombreModelo;
    }

    public List<Registro> getListaModalidad() {
        return listaModalidad;
    }

    public void setListaModalidad(List<Registro> listaModalidad) {
        this.listaModalidad = listaModalidad;
    }

    public boolean isTipoTexto() {
        return tipoTexto;
    }

    public void setTipoTexto(boolean tipoTexto) {
        this.tipoTexto = tipoTexto;
    }

    public List<Registro> getListacuadroModelo() {
        return listacuadroModelo;
    }

    public void setListacuadroModelo(List<Registro> listacuadroModelo) {
        this.listacuadroModelo = listacuadroModelo;
    }

    public List<Registro> getListaModelo() {
        return listaModelo;
    }

    public void setListaModelo(List<Registro> listaModelo) {
        this.listaModelo = listaModelo;
    }

    public List<Registro> getListaEtapasub() {
        return listaEtapasub;
    }

    public void setListaEtapasub(List<Registro> listaEtapasub) {
        this.listaEtapasub = listaEtapasub;
    }

    public List<Registro> getListaSubvariables() {
        return listaSubvariables;
    }

    public void setListaSubvariables(List<Registro> listaSubvariables) {
        this.listaSubvariables = listaSubvariables;
    }

    public List<Registro> getListaModelocontrato() {
        return listaModelocontrato;
    }

    public void setListaModelocontrato(List<Registro> listaModelocontrato) {
        this.listaModelocontrato = listaModelocontrato;
    }

    public Registro getRegistroSubetapaSub() {
        return registroSubetapaSub;
    }

    public void setRegistroSubetapaSub(Registro registroSubetapaSub) {
        this.registroSubetapaSub = registroSubetapaSub;
    }

    public Registro getRegistroSubSubVariables() {
        return registroSubSubVariables;
    }

    public void setRegistroSubSubVariables(Registro registroSubSubVariables) {
        this.registroSubSubVariables = registroSubSubVariables;
    }

    public Registro getRegistroSubModeloContrato() {
        return registroSubModeloContrato;
    }

    public void setRegistroSubModeloContrato(
        Registro registroSubModeloContrato) {
        this.registroSubModeloContrato = registroSubModeloContrato;
    }

    public boolean isBloqueadoTipo() {
        return bloqueadoTipo;
    }

    public void setBloqueadoTipo(boolean bloqueadoTipo) {
        this.bloqueadoTipo = bloqueadoTipo;
    }

    public String getMascaraValor() {
        return mascaraValor;
    }

    public void setMascaraValor(String mascaraValor) {
        this.mascaraValor = mascaraValor;
    }

    public boolean isBloqueadoValor() {
        return bloqueadoValor;
    }

    public void setBloqueadoValor(boolean bloqueadoValor) {
        this.bloqueadoValor = bloqueadoValor;
    }
    
    
    
    

}
