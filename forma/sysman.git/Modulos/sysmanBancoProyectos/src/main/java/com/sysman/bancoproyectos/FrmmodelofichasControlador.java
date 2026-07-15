package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmmodelofichasControladorEnum;
import com.sysman.bancoproyectos.enums.FrmmodelofichasControladorUrlEnum;
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import java.util.TreeMap;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 09/09/2015
 * 
 * @author eamaya
 * @version 2.0, 20/09/2017, Proceso de Refactoring DSS, Manejo de
 * EJBs , cambio de numero de formulario por enum y correcciones
 * SonarQube
 * 
 */
@ManagedBean
@ViewScoped

public class FrmmodelofichasControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String modulo;
    private final String usuario;
    private Registro registroSub;
    private List<Registro> listaSECCION;
    private List<Registro> listaFrmsubbpmodelofichatecnica;
    private RegistroDataModel listaCODIGO;
    private String seccion;
    private boolean existe;
    private boolean visible;
    private boolean visibleSeccion;
    private StreamedContent archivoDescarga;

    /**
     * Variable que guarda la cadena insertada
     */

    private String nombreSeccion;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    public FrmmodelofichasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();
        numFormulario = GeneralCodigoFormaEnum.FRMMODELOFICHAS_CONTROLADOR
                        .getCodigo();
        // 163
        try {
            registro = new Registro(new HashMap<String, Object>());
            registroSub = new Registro(new HashMap<String, Object>());
            validarPermisos();
        }
        catch (Exception ex) {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(FrmmodelofichasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void init() {
        enumBase = GenericUrlEnum.BP_SECTOR_FICHA_TECNICA;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    @Override
    public void iniciarListas() {
        cargarListaSECCION();
    }

    @Override
    public void iniciarListasSub() {
        cargarListaFrmsubbpmodelofichatecnica();
        cargarListaSECCION();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaFrmsubbpmodelofichatecnica = null;
    }

    public void cargarListaSECCION() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.SECTOR.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        try {
            listaSECCION = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmmodelofichasControladorUrlEnum.URL4838
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaFrmsubbpmodelofichatecnica() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(FrmmodelofichasControladorEnum.SECCION.getValue(),
                            seccion);
            param.put(GeneralParameterEnum.SECTOR.getName(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));

            listaFrmsubbpmodelofichatecnica = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmmodelofichasControladorUrlEnum.URL3781
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            "BP_MODELO_FICHA_TECNICA"));
        }
        catch (SysmanException | SystemException e) {
            Logger.getLogger(FrmmodelofichasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void agregarRegistroSubFrmsubbpmodelofichatecnica() {
        try {
            registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            registroSub.getCampos().put(GeneralParameterEnum.SECTOR.getName(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));
            registroSub.getCampos().put(
                            FrmmodelofichasControladorEnum.SECCION.getValue(),
                            seccion);
            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(), usuario);

            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            compararRegistro();
            if (!existe) {
                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.BP_MODELO_FICHA_TECNICA
                                                                .getCreateKey());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                registroSub.getCampos());
                cargarListaFrmsubbpmodelofichatecnica();
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1945"));
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmmodelofichasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
            cargarListaSECCION();
        }
    }

    public void editarRegSubFrmsubbpmodelofichatecnica(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            usuario);
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.BP_MODELO_FICHA_TECNICA
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmmodelofichasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            cargarListaFrmsubbpmodelofichatecnica();
            cargarListaSECCION();
        }
    }

    public void eliminarRegSubFrmsubbpmodelofichatecnica(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.BP_MODELO_FICHA_TECNICA
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            cargarListaFrmsubbpmodelofichatecnica();
        }
        catch (SystemException ex) {
            Logger.getLogger(FrmmodelofichasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void generaInformeModeloFicha(FORMATOS formato) {
        try {
            Map<String, Object> parametros = new TreeMap<>();
            Map<String, Object> reemplazar = new TreeMap<>();
            reemplazar.put("sector", registro.getCampos()
                            .get(GeneralParameterEnum.CODIGO.getName()));

            String responsable = SysmanFunciones
                            .nvl(ejbSysmanUtl.consultarParametro(compania,
                                            "NOMBRE DEL RESPONSABLE DEL FILTRO TECNICO EN BP",
                                            modulo, new Date(), false), "")
                            .toString();

            parametros.put("PR_NOMBRE_DEL_RESPONSABLE_DEL_FILTRO_TECNICO_EN_BP",
                            responsable);
            parametros.put("PR_COMPANIA", compania);
            Reporteador.resuelveConsulta("000202FICHATECNICA",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed("000202FICHATECNICA",
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException
                        | SysmanException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrmmodelofichasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cancelarEdicionFrmsubbpmodelofichatecnica() {
        cargarListaFrmsubbpmodelofichatecnica();
    }

    public void oprimirPRESENTAR() {
        archivoDescarga = null;
        generaInformeModeloFicha(FORMATOS.PDF);
    }

    public void oprimirBTExcel() {
        archivoDescarga = null;
        generaInformeModeloFicha(FORMATOS.EXCEL97);
    }

    public void oprimirAdicionarSeccion() {
        visible = true;
    }

    public void cambiarSECCION() {
        cargarListaFrmsubbpmodelofichatecnica();
    }

    public void cambiarCODIGO() {
        // METODO NO IMPLEMENTADO
    }

    public void cancelarNuevo() {
        visible = false;
    }

    public void aceptarNuevo() {
        Map<String, Object> param = new TreeMap<>();
        try {

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(GeneralParameterEnum.CODIGO.getName(),
                            ejbSysmanUtl.generarSiguienteConsecutivo(
                                            "BP_MODELO_FICHA_TECNICA",
                                            "COMPANIA =''" + compania + "''",
                                            "CODIGO"));

            param.put(GeneralParameterEnum.SECTOR.getName(),
                            registro.getCampos().get(
                                            GeneralParameterEnum.CODIGO
                                                            .getName()));

            param.put(FrmmodelofichasControladorEnum.ITEM.getValue(), "VARIOS");

            param.put(FrmmodelofichasControladorEnum.SECCION.getValue(),
                            nombreSeccion);

            param.put(GeneralParameterEnum.CREATED_BY.getName(), usuario);

            param.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());

            Parameter parameter = new Parameter();

            parameter.setFields(param);

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmmodelofichasControladorUrlEnum.URL15847
                                                            .getValue());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            parameter);

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1945"));

            cargarListaSECCION();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        visible = false;
    }

    /**
     * Permite consultar en la base de datos si existe un registro.
     * 
     * @return existe
     */

    private boolean compararRegistro() {
        //
        existe = false;

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(FrmmodelofichasControladorEnum.SECCION.getValue(),
                        registroSub.getCampos().get("SECCION"));

        param.put(FrmmodelofichasControladorEnum.ITEM.getValue(),
                        registroSub.getCampos().get("ITEM"));

        param.put(GeneralParameterEnum.SECTOR.getName(),
                        registro.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));

        List<Registro> auxiliar;
        try {
            auxiliar = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmmodelofichasControladorUrlEnum.URL5050
                                                                            .getValue())
                                            .getUrl(), param));

            if (auxiliar.isEmpty()) {
                existe = false;
            }
            else {
                existe = true;
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2344")
                                .replace("#$seccion#$",
                                                registroSub.getCampos()
                                                                .get("SECCION")
                                                                .toString())
                                .replace("#$item#$", registroSub
                                                .getCampos().get("ITEM")
                                                .toString()));
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return existe;
    }

    @Override
    public void abrirFormulario() {
        // METODO NO IMPLEMENTADO
    }

    public List<Registro> getListaSECCION() {
        return listaSECCION;
    }

    public void setListaSECCION(List<Registro> listaSECCION) {
        this.listaSECCION = listaSECCION;
    }

    public List<Registro> getListaFrmsubbpmodelofichatecnica() {
        return listaFrmsubbpmodelofichatecnica;
    }

    public void setListaFrmsubbpmodelofichatecnica(
        List<Registro> listaFrmsubbpmodelofichatecnica) {
        this.listaFrmsubbpmodelofichatecnica = listaFrmsubbpmodelofichatecnica;
    }

    public RegistroDataModel getListaCODIGO() {
        return listaCODIGO;
    }

    public void setListaCODIGO(RegistroDataModel listaCODIGO) {
        this.listaCODIGO = listaCODIGO;
    }

    public Registro getRegistroSub() {
        return registroSub;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    @Override
    public void cargarRegistro() {
        cargarListaSECCION();

        if (ACCION_INSERTAR.equals(accion)) {
            visibleSeccion = true;
        }
        else {

            visibleSeccion = false;
        }
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getNombreSeccion() {
        return nombreSeccion;
    }

    public void setNombreSeccion(String nombreSeccion) {
        this.nombreSeccion = nombreSeccion;
    }

    public boolean isVisibleSeccion() {
        return visibleSeccion;
    }

    public void setVisibleSeccion(boolean visibleSeccion) {
        this.visibleSeccion = visibleSeccion;
    }

}