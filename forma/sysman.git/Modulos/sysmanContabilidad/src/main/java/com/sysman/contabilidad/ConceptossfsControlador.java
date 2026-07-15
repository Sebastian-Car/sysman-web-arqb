package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.contabilidad.enums.ConceptossfsControladorEnum;
import com.sysman.contabilidad.enums.ConceptossfsControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author ybecerra
 * @version 1, 15/03/2016
 * 
 * @author eamaya
 * @version 2, 07/04/2017 Proceso de Refactoring y Correciones
 * SonarLint
 * 
 * @version 3.0, 12/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Remover y/o reemplazar los llamados a ConectorPool por el esquema.
 */
@ManagedBean
@ViewScoped
public class ConceptossfsControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private String anio;
    private String tipoCobro;
    private String nombre;
    private String titulo;
    private String auxTipo;
    private Registro registroSub;
    private boolean regimenVisible;
    private RegistroDataModelImpl listaCUENTADEBITOBASE;
    private RegistroDataModelImpl listaCUENTACREDITOBASE;
    private RegistroDataModelImpl listaCUENTADEBITOUTILIDAD;
    private RegistroDataModelImpl listaCUENTACREDITOUTILIDAD;
    private RegistroDataModelImpl listaCuadroCombinado167;
    private RegistroDataModelImpl listaCuadroCombinado169;
    private List<Registro> listaTIPO;
    private List<Registro> listaRetencionesconcepto;
    private List<Registro> listaComprobante;
    private RegistroDataModelImpl listaCODIGO;
    private RegistroDataModelImpl listaCODIGOE;
    private String auxiliar;
    private int indiceRetencionesconcepto;

    public ConceptossfsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 574
            numFormulario = GeneralCodigoFormaEnum.CONCEPTOSSFS_CONTROLADOR
                            .getCodigo();

            validarPermisos();

            registroSub = new Registro(new HashMap<String, Object>());

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                anio = (String) parametrosEntrada.get("anio");
                tipoCobro = (String) parametrosEntrada.get("tipoCobro");
                nombre = (String) parametrosEntrada.get("nombre");
            }
        }
        catch (Exception ex) {
            Logger.getLogger(ConceptossfsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        cargarListaCUENTADEBITOBASE();
        cargarListaCUENTACREDITOBASE();
        cargarListaCUENTADEBITOUTILIDAD();
        cargarListaCUENTACREDITOUTILIDAD();
        cargarListaCuadroCombinado167();
        cargarListaCuadroCombinado169();
        cargarListaTIPO();
    }

    @Override
    public void iniciarListasSub() {
        cargarListaRetencionesconcepto();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaRetencionesconcepto = null;
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.CONCEPTOS_SF;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("ANOCONCEPTO", anio);
        parametrosListado.put(ConceptossfsControladorEnum.PARAM3.getValue(),
                        tipoCobro);

    }

    public void cargarListaRetencionesconcepto() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get("ANO"));
        param.put(ConceptossfsControladorEnum.PARAM3.getValue(),
                        registro.getCampos()
                                        .get(ConceptossfsControladorEnum.PARAM3
                                                        .getValue()));
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        try {
            listaRetencionesconcepto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            GenericUrlEnum.RETENCIONES_CONCEPTO
                                                                            .getGridKey())
                                            .getUrl(), param),

                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "RETENCIONESCONCEPTO"));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCUENTADEBITOBASE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptossfsControladorUrlEnum.URL1666
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCUENTADEBITOBASE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaCUENTACREDITOBASE() {
        listaCUENTACREDITOBASE = listaCUENTADEBITOBASE;
    }

    public void cargarListaCUENTADEBITOUTILIDAD() {
        listaCUENTADEBITOUTILIDAD = listaCUENTADEBITOBASE;
    }

    public void cargarListaCUENTACREDITOUTILIDAD() {
        listaCUENTACREDITOUTILIDAD = listaCUENTADEBITOBASE;
    }

    public void cargarListaCuadroCombinado167() {
        listaCuadroCombinado167 = listaCUENTADEBITOBASE;
    }

    public void cargarListaCuadroCombinado169() {
        listaCuadroCombinado169 = listaCUENTADEBITOBASE;
    }

    public void cargarListaTIPO() {
        try {
            listaTIPO = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConceptossfsControladorUrlEnum.URL5857
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCODIGO() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptossfsControladorUrlEnum.URL6227
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        param.put(ConceptossfsControladorEnum.PARAM2.getValue(),
                        registroSub.getCampos().get("TIPO"));

        listaCODIGO = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCODIGOE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptossfsControladorUrlEnum.URL6227
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        param.put(ConceptossfsControladorEnum.PARAM2.getValue(),
                        auxTipo);

        listaCODIGOE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void agregarRegistroSubRetencionesconcepto() {
        try {
            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            registroSub.getCampos().put(GeneralParameterEnum.ANO.getName(),
                            registro.getCampos().get("ANO"));

            registroSub.getCampos().put(
                            ConceptossfsControladorEnum.PARAM3.getValue(),
                            registro.getCampos()
                                            .get(ConceptossfsControladorEnum.PARAM3
                                                            .getValue()));

            registroSub.getCampos().put(GeneralParameterEnum.CONCEPTO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));

            registroSub.getCampos().remove(
                            ConceptossfsControladorEnum.PARAM1.getValue());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.RETENCIONES_CONCEPTO
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            cargarListaRetencionesconcepto();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(ConceptossfsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubRetencionesconcepto(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove("NOMBRE");
            reg.getCampos().remove("COMPANIA");
            reg.getCampos().remove(
                            ConceptossfsControladorEnum.PARAM1.getValue());

            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.RETENCIONES_CONCEPTO
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(ConceptossfsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaRetencionesconcepto();
        }
    }

    public void eliminarRegSubRetencionesconcepto(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.RETENCIONES_CONCEPTO
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaRetencionesconcepto();
        }
        catch (SystemException ex) {
            Logger.getLogger(ConceptossfsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionRetencionesconcepto() {
        cargarListaRetencionesconcepto();
    }

    public boolean validarRegimen() {

        try {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));

            Registro auxReg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConceptossfsControladorUrlEnum.URL6969
                                                                            .getValue())
                                            .getUrl(), param));

            if (auxReg != null) {

                JsfUtil.agregarMensajeError(idioma.getString("TB_TB535") + ""
                    + idioma.getString("TB_TB536"));

                return true;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return false;

    }

    public void cambiarTIPO() {
        registroSub.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                        null);
        registroSub.getCampos().put(
                        ConceptossfsControladorEnum.PARAM1.getValue(), null);
        cargarListaCODIGO();

    }

    public void cambiarTIPOC(int rowNum) {

        setAuxTipo((String) listaRetencionesconcepto.get(rowNum).getCampos()
                        .put("TIPO", registroSub.getCampos().get("TIPO")));
        listaRetencionesconcepto.get(rowNum).getCampos()
                        .put(GeneralParameterEnum.CODIGO.getName(), null);
        cargarListaCODIGOE();

    }

    public void seleccionarFilaCODIGO(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registroSub.getCampos().put(
                        ConceptossfsControladorEnum.PARAM1.getValue(),
                        registroAux.getCampos().get("NOMBRE"));

    }

    public void seleccionarFilaCODIGOE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), " ")
                        .toString();

    }

    public void seleccionarFilaCUENTADEBITOBASE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITOBASE",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaCUENTACREDITOBASE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITOBASE",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaCUENTADEBITOUTILIDAD(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTAORDENDEUDORA_DEBITO",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaCUENTACREDITOUTILIDAD(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CTAORDENDEUDORA_CREDITO",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaCuadroCombinado167(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTADEBITOUTILIDAD",
                        registroAux.getCampos().get("ID"));
    }

    public void seleccionarFilaCuadroCombinado169(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CUENTACREDITOUTILIDAD",
                        registroAux.getCampos().get("ID"));
    }

    @Override
    public void abrirFormulario() {

        titulo = idioma.getString("TB_TB537") + " " + nombre;

    }

    public void activarEdicionRetencionesconcepto(Registro reg) {
        indiceRetencionesconcepto = listaRetencionesconcepto.indexOf(reg);

    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ANO", anio);
        registro.getCampos().put(ConceptossfsControladorEnum.PARAM3.getValue(),
                        tipoCobro);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (validarRegimen()) {
            return false;
        }

        return true;

        // </CODIGO_DESARROLLADO>

    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public RegistroDataModelImpl getListaCUENTADEBITOBASE() {
        return listaCUENTADEBITOBASE;
    }

    public void setListaCUENTADEBITOBASE(
        RegistroDataModelImpl listaCUENTADEBITOBASE) {
        this.listaCUENTADEBITOBASE = listaCUENTADEBITOBASE;
    }

    public RegistroDataModelImpl getListaCUENTACREDITOBASE() {
        return listaCUENTACREDITOBASE;
    }

    public void setListaCUENTACREDITOBASE(
        RegistroDataModelImpl listaCUENTACREDITOBASE) {
        this.listaCUENTACREDITOBASE = listaCUENTACREDITOBASE;
    }

    public RegistroDataModelImpl getListaCUENTADEBITOUTILIDAD() {
        return listaCUENTADEBITOUTILIDAD;
    }

    public void setListaCUENTADEBITOUTILIDAD(
        RegistroDataModelImpl listaCUENTADEBITOUTILIDAD) {
        this.listaCUENTADEBITOUTILIDAD = listaCUENTADEBITOUTILIDAD;
    }

    public RegistroDataModelImpl getListaCUENTACREDITOUTILIDAD() {
        return listaCUENTACREDITOUTILIDAD;
    }

    public void setListaCUENTACREDITOUTILIDAD(
        RegistroDataModelImpl listaCUENTACREDITOUTILIDAD) {
        this.listaCUENTACREDITOUTILIDAD = listaCUENTACREDITOUTILIDAD;
    }

    public RegistroDataModelImpl getListaCuadroCombinado167() {
        return listaCuadroCombinado167;
    }

    public void setListaCuadroCombinado167(
        RegistroDataModelImpl listaCuadroCombinado167) {
        this.listaCuadroCombinado167 = listaCuadroCombinado167;
    }

    public RegistroDataModelImpl getListaCuadroCombinado169() {
        return listaCuadroCombinado169;
    }

    public void setListaCuadroCombinado169(
        RegistroDataModelImpl listaCuadroCombinado169) {
        this.listaCuadroCombinado169 = listaCuadroCombinado169;
    }

    public List<Registro> getListaTIPO() {
        return listaTIPO;
    }

    public void setListaTIPO(List<Registro> listaTIPO) {
        this.listaTIPO = listaTIPO;
    }

    public List<Registro> getListaRetencionesconcepto() {
        return listaRetencionesconcepto;
    }

    public void setListaRetencionesconcepto(
        List<Registro> listaRetencionesconcepto) {
        this.listaRetencionesconcepto = listaRetencionesconcepto;
    }

    public RegistroDataModelImpl getListaCODIGO() {
        return listaCODIGO;
    }

    public List<Registro> getListaComprobante() {
        return listaComprobante;
    }

    public void setListaComprobante(List<Registro> listaComprobante) {
        this.listaComprobante = listaComprobante;
    }

    public void setListaCODIGO(RegistroDataModelImpl listaCODIGO) {
        this.listaCODIGO = listaCODIGO;
    }

    public RegistroDataModelImpl getListaCODIGOE() {
        return listaCODIGOE;
    }

    public void setListaCODIGOE(RegistroDataModelImpl listaCODIGOE) {
        this.listaCODIGOE = listaCODIGOE;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public boolean isRegimenVisible() {
        return regimenVisible;
    }

    public void setRegimenVisible(boolean regimenVisible) {
        this.regimenVisible = regimenVisible;
    }

    public String getAuxTipo() {
        return auxTipo;
    }

    public void setAuxTipo(String auxTipo) {
        this.auxTipo = auxTipo;
    }

    public int getIndiceRetencionesconcepto() {
        return indiceRetencionesconcepto;
    }

    public void setIndiceRetencionesconcepto(int indiceRetencionesconcepto) {
        this.indiceRetencionesconcepto = indiceRetencionesconcepto;
    }

}
