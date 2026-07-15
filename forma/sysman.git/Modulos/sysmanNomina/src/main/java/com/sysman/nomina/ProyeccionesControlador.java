package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
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
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.enums.ProyeccionesControladorEnum;
import com.sysman.nomina.enums.ProyeccionesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
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
 * @author cmanrique
 * 
 * @version 2, 23/10/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla, de datos y en el
 * subformulario.
 * 
 */

@ManagedBean
@ViewScoped
public class ProyeccionesControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;

    private String auxNombre;
    private boolean perActivo;
    private Registro registroSub;

    private List<Registro> listaProyecciones;
    private RegistroDataModelImpl listaIdConcepto;
    private RegistroDataModelImpl listaIdConceptoE;
    private String auxiliar;
    private String titulo;
    private final String anio;
    private final String mes;
    private final String periodo;
    private final int proceso;

    private final String idEmpleadoCons;
    private final String nombreConceptoCons;
    private final String proyeccionesCons;
    private final String mensajeInterrumCons;
    private final String idConecptoCons;
    
    @EJB
    private EjbNominaCeroRemote ejbNominaCero;

    public ProyeccionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        anio = (String) SessionUtil.getSessionVar("anioNomina");
        mes = (String) SessionUtil.getSessionVar("mesNomina");
        periodo = (String) SessionUtil.getSessionVar("periodoNomina");
        proceso = Integer.parseInt(
                        (String) SessionUtil.getSessionVar("procesoNomina"));

        idEmpleadoCons = "ID_DE_EMPLEADO";
        nombreConceptoCons = "NOMBRE_CONCEPTO";
        proyeccionesCons = "PROYECCIONES";
        mensajeInterrumCons = "MSM_TRANS_INTERRUMPIDA";
        idConecptoCons = "ID_DE_CONCEPTO";

        try {
            numFormulario = GeneralCodigoFormaEnum.PROYECCIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            titulo = idioma.getString("TB_TB3736").replace("#$mes#$",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes)])
                            .replace("#$anio#$", anio)
                            .replace("#$periodo#$", periodo);
            registro = new Registro(new HashMap<String, Object>());
            registroSub = new Registro(new HashMap<String, Object>());
        }
        catch (SysmanException ex) {
            Logger.getLogger(ProyeccionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = GenericUrlEnum.PERSONAL.getTable();
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        ProyeccionesControladorUrlEnum.URL7541.getValue());
        urlLectura = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        ProyeccionesControladorUrlEnum.URL6145.getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }


    @Override
    public void iniciarListas() {
        cargarListaIdConcepto();
        cargarListaIdConceptoE();
    }

    @Override
    public void iniciarListasSub() {
        cargarListaProyecciones();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaProyecciones = null;
    }

    public void agregarRegistroSubProyecciones() {
        try {

            registroSub.getCampos().put("COMPANIA", compania);
            registroSub.getCampos().put(idEmpleadoCons,
                            registro.getCampos().get(idEmpleadoCons));
            registroSub.getCampos().put("ID_DE_PROCESO", proceso);
            registroSub.getCampos().put("ANO", anio);
            registroSub.getCampos().put("MES", mes);
            registroSub.getCampos().put("PERIODO", periodo);
            registroSub.getCampos().remove(nombreConceptoCons);
            registroSub.getCampos().remove("RANGO1");
            registroSub.getCampos().remove("RANGO2");
            registroSub.getCampos().put("CREATED_BY",
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put("DATE_CREATED", new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PROYECCIONES
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
            cargarListaProyecciones();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(idioma.getString(mensajeInterrumCons)
                                , ex.getMessage()));
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubProyecciones(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove(nombreConceptoCons);
            reg.getCampos().remove("RANGO1");
            reg.getCampos().remove("RANGO2");
            reg.getCampos().put("ID_DE_PROCESO", proceso);
            reg.getCampos().put("ANO", anio);
            reg.getCampos().put("MES", mes);
            reg.getCampos().put("PERIODO", periodo);
            reg.getCampos().put("MODIFIED_BY",
                            SessionUtil.getUser().getCodigo());
            reg.getCampos().put("DATE_MODIFIED", new Date());
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.PROYECCIONES
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(idioma.getString(mensajeInterrumCons)
                                ,ex.getMessage()));
        }
        finally {
            cargarListaProyecciones();
        }
    }

    public void eliminarRegSubProyecciones(Registro reg) {
        try {
            if (Integer.parseInt(
                            reg.getCampos().get("VALOR").toString()) == 0) {
                UrlBean urlDelete = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.PROYECCIONES
                                                                .getDeleteKey());
                requestManager.delete(urlDelete.getUrl(),
                                reg.getLlave());

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
                cargarListaProyecciones();
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2597"));
            }

        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(idioma.getString(mensajeInterrumCons)
                                ,ex.getMessage()));
        }
    }

    public void seleccionarFilaIdConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.MES.getName(), mes);
            param.put(GeneralParameterEnum.PERIODO.getName(), periodo);
            param.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                            registro.getCampos().get(idEmpleadoCons));
            param.put(GeneralParameterEnum.ID_DE_CONCEPTO.getName(),
                            registroAux.getCampos().get(idConecptoCons));
            List<Registro> auxLista = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProyeccionesControladorUrlEnum.URL5889
                                                                            .getValue())
                                            .getUrl(), param));
            String aux = auxLista.get(0).getCampos()
                            .get("CUENTA").toString();

            if (!"0".equals(aux)) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB2594").replace("#$mes#$",
                                                SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                                .parseInt(mes)])
                                                .replace("#$anio#$", anio)
                                                .replace("#$periodo#$",
                                                                periodo));
            }
            else {
                registroSub.getCampos().put(idConecptoCons,
                                registroAux.getCampos().get(idConecptoCons));
                registroSub.getCampos().put(nombreConceptoCons,
                                registroAux.getCampos()
                                                .get(nombreConceptoCons));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaIdConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(idConecptoCons).toString();
        auxNombre = registroAux.getCampos().get(nombreConceptoCons).toString();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            if (proceso < 20) {
                SessionUtil.agregarMensajeErrorMenu(
                                idioma.getString("TB_TB2592"));
                SessionUtil.redireccionarMenu();
                return;
            }
            perActivo = ejbNominaCero.validarPeriodoActivoNomina(compania,
                            proceso, Integer.parseInt(anio),
                            Integer.parseInt(mes), Integer.parseInt(periodo));
        }
        catch (NumberFormatException | SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(idioma.getString(mensajeInterrumCons)
                                ,ex.getMessage()));
        }

    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaProyecciones() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.MES.getName(), mes);
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
            param.put(GeneralParameterEnum.PERIODO.getName(), periodo);
            param.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                            registro.getCampos().get(idEmpleadoCons));

            listaProyecciones = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ProyeccionesControladorUrlEnum.URL13110
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            proyeccionesCons));

        }
        catch (SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaIdConcepto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProyeccionesControladorUrlEnum.URL14482
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ProyeccionesControladorEnum.PARAM0.getValue(), "2");

        listaIdConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, idConecptoCons);
    }

    public void cargarListaIdConceptoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ProyeccionesControladorUrlEnum.URL14482
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ProyeccionesControladorEnum.PARAM0.getValue(), "2");

        listaIdConceptoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, idConecptoCons);
    }

    public void cambiarIdConceptoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaProyecciones.get(rowNum).getCampos().put(nombreConceptoCons,
                        auxNombre);
        // </CODIGO_DESARROLLADO>
    }

    public void cancelarEdicionProyecciones() {
        cargarListaProyecciones();
    }

    // ---------------------------------------
    // ------------ GET AND SET --------------
    // ---------------------------------------
    public List<Registro> getListaProyecciones() {
        return listaProyecciones;
    }

    public void setListaProyecciones(List<Registro> listaProyecciones) {
        this.listaProyecciones = listaProyecciones;
    }

    public RegistroDataModelImpl getListaIdConcepto() {
        return listaIdConcepto;
    }

    public void setListaIdConcepto(RegistroDataModelImpl listaIdConcepto) {
        this.listaIdConcepto = listaIdConcepto;
    }

    public RegistroDataModelImpl getListaIdConceptoE() {
        return listaIdConceptoE;
    }

    public void setListaIdConceptoE(RegistroDataModelImpl listaIdConceptoE) {
        this.listaIdConceptoE = listaIdConceptoE;
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

    public boolean isPerActivo() {
        return perActivo;
    }

    public void setPerActivo(boolean perActivo) {
        this.perActivo = perActivo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAuxNombre() {
        return auxNombre;
    }

    public void setAuxNombre(String auxNombre) {
        this.auxNombre = auxNombre;
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put("COMPANIA", compania);
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
