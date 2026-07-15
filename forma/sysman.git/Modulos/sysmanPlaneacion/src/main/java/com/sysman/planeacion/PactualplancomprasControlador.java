package com.sysman.planeacion;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.planeacion.ejb.EjbPlaneacionCeroRemote;
import com.sysman.planeacion.enums.PactualplancomprasControladorEnum;
import com.sysman.planeacion.enums.PactualplancomprasControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
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
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 07/01/2016
 * 
 * @version 2, 12/09/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla, de datos y en el
 * subformulario.
 */

@ManagedBean
@ViewScoped
public class PactualplancomprasControlador extends BeanBaseDatosAcmeImpl {
 
    private final String compania;
    private final String modulo;
    private final String consActualizacion;
    private final String consRealizada;
    private final String consNombreCorto;
    private final String consCodigoDependencia;
    
    private boolean texto50Visible;
    private boolean texto46Visible;
    private boolean actualizacionPCVisible;
    private boolean bloqueaActualizacionPC;
    private boolean editarFormulario;
    private boolean eliminarFormulario;
    private boolean bloqueaNumeroOficio;
    private boolean insertarRegistroSub;
    private Registro registroSub;
    private List<Registro> listaSubproyecto;
    private List<Registro> listacmbAno;
    private List<Registro> listaSubdactualizacionpc;
    private RegistroDataModelImpl listaDependencia;
    private RegistroDataModelImpl listaDependenciaE;
    private RegistroDataModelImpl listaRubro;
    private RegistroDataModelImpl listaRubroE;
    private RegistroDataModelImpl listaElemento;
    private RegistroDataModelImpl listaElementoE;
    private List<Registro> listaMes;
    private List<Registro> listaMesE;
    private List<Registro> listaActividad;
    private String auxiliar;
    private String nombreRubro;
    private String nombreRubroAux;
    private String nombreElemento;
    private StreamedContent archivoDescarga;
    private String rubroSub;
    private String cmbAno;
    private String codigoSub;
    private String dependencia;
    private String auxiliarC;
    private Object centroCosto;
    private int indiceSubdactualizacionpc;

    @EJB
    private EjbPlaneacionCeroRemote ejbPlaneacionCero;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    public PactualplancomprasControlador() {

        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        consActualizacion="ACTUALIZACION";
        consRealizada="REALIZADA";
        consNombreCorto="NOMBRECORTO";
        consCodigoDependencia="CODIGODEPENDENCIA";
        try {

            numFormulario = GeneralCodigoFormaEnum.PACTUALPLANCOMPRAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registroSub = new Registro(new HashMap<String, Object>());
            actualizacionPCVisible = true;
            texto46Visible = true;
            editarFormulario = true;
            eliminarFormulario = true;
            bloqueaNumeroOficio = false;
            insertarRegistroSub = false;
        }
        catch (SysmanException ex) {
            Logger.getLogger(PactualplancomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
    }

    @Override
    public void iniciarListas() {

        cargarListaActividad();
        cargarListaElemento();
        cargarListaElementoE();
        cargarListaDependencia();
        cargarListaDependenciaE();
        cargarListaSubproyecto();
        cargarListacmbAno();
        cargarListaMes();
        cargarListaMesE();
        bloqueaNumeroOficio = true;
    }

    @Override
    public void iniciarListasSub() {
        cargarListaSubdactualizacionpc();

    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubdactualizacionpc = null;
        bloqueaNumeroOficio = false;

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ACTPLAN_DE_COMPRAS;
        buscarLlave();
        asignarOrigenDatos();
    }

    public void activarEdicionSubdactualizacionpc(Registro r) {
        indiceSubdactualizacionpc = listaSubdactualizacionpc.indexOf(r);
        registroSub = r;
    }

    public void cargarListaSubdactualizacionpc() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PactualplancomprasControladorEnum.PARAM0.getValue(),
                        registro.getCampos().get(consActualizacion));
        param.put(GeneralParameterEnum.ANO.getName(),
                        registro.getCampos().get("ANO"));
        try {
            listaSubdactualizacionpc = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PactualplancomprasControladorUrlEnum.URL7839
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "ACTDETPLAN_COMPRAS"));
        }
        catch (SystemException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaRubro() {
        cmbAno = registro.getCampos().get("ANO").toString();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PactualplancomprasControladorUrlEnum.URL7138
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cmbAno);

        listaRubro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaRubroE() {
        cmbAno = registro.getCampos().get("ANO").toString();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PactualplancomprasControladorUrlEnum.URL7138
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), cmbAno);

        listaRubroE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaElemento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PactualplancomprasControladorUrlEnum.URL8489
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.RUBRO.getName(),
                        registroSub.getCampos().get(GeneralParameterEnum.RUBRO.getName()));

        listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void cargarListaElementoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PactualplancomprasControladorUrlEnum.URL8489
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.RUBRO.getName(), rubroSub);

        listaElementoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void cargarListaDependencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PactualplancomprasControladorUrlEnum.URL9961
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registroSub.getCampos().get(GeneralParameterEnum.RUBRO.getName()));

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigoDependencia);
    }

    public void cargarListaDependenciaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PactualplancomprasControladorUrlEnum.URL9961
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(),
                        registroSub.getCampos().get(GeneralParameterEnum.RUBRO.getName()));

        listaDependenciaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigoDependencia);
    }

    public void cargarListaActividad() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaActividad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PactualplancomprasControladorUrlEnum.URL10385
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaSubproyecto() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaSubproyecto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PactualplancomprasControladorUrlEnum.URL10385
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacmbAno() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listacmbAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PactualplancomprasControladorUrlEnum.URL11032
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.RUBRO.getName(),
                            registroSub.getCampos().get(GeneralParameterEnum.RUBRO.getName()));
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registroSub.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
            param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                            registroSub.getCampos().get(GeneralParameterEnum.DEPENDENCIA.getName()));

            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PactualplancomprasControladorUrlEnum.URL11340
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    public void cargarListaMesE() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.RUBRO.getName(),
                            registroSub.getCampos().get(GeneralParameterEnum.RUBRO.getName()));
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registroSub.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
            param.put(GeneralParameterEnum.DEPENDENCIA.getName(),
                            registroSub.getCampos().get(GeneralParameterEnum.DEPENDENCIA.getName()));

            listaMesE = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PactualplancomprasControladorUrlEnum.URL11340
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void agregarRegistroSubSubdactualizacionpc() {
        try {
            registroSub.getCampos().put(consActualizacion,
                            registro.getCampos().get(consActualizacion));
            registroSub.getCampos().put("COMPANIA", compania);
            registroSub.getCampos().put("ANO", registro.getCampos().get("ANO"));
            registroSub.getCampos().put("AUXILIAR", auxiliarC);
            registroSub.getCampos().put("CENTRO_COSTO", centroCosto);
            registroSub.getCampos().remove("ACTDETPLAN_COMPRAS_SUBPROYECTO");
            registroSub.getCampos().remove("NOMBRETIPO");
            registroSub.getCampos().remove(consNombreCorto);
            registroSub.getCampos().remove("NOMBREELEMENTO");
            registroSub.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
            registroSub.getCampos().remove("NOMBREMES");
            registroSub.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
            registroSub.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.ACTDETPLAN_COMPRAS
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());

            cargarListaSubdactualizacionpc();
            registroSub = new Registro(new HashMap<String, Object>());
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(Constantes.MSM_REGISTRO_INGRESADO));
        }
        catch (SystemException ex) {
            Logger.getLogger(PactualplancomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void editarRegSubSubdactualizacionpc(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove("ACTDETPLAN_COMPRAS_SUBPROYECTO");
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().remove(GeneralParameterEnum.REFERENCIA.getName());
            reg.getCampos().remove(PactualplancomprasControladorEnum.PARAM1.getValue());
            reg.getCampos().remove("NOMBRETIPO");
            reg.getCampos().remove(consNombreCorto);
            reg.getCampos().remove("nombreElemento");
            reg.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
            reg.getCampos().remove("NOMBREMES");
            reg.getCampos().remove("NOMBRESUBPROYECTO");
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.ACTDETPLAN_COMPRAS
                                                            .getUpdateKey());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(Constantes.MSM_REGISTRO_MODIFICADO));
            cargarListaSubdactualizacionpc();
            registroSub = new Registro(new HashMap<String, Object>());
        }
        catch (SystemException ex) {
            Logger.getLogger(PactualplancomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void eliminarRegSubSubdactualizacionpc(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.ACTDETPLAN_COMPRAS
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(idioma
                            .getString(Constantes.MSM_REGISTRO_ELIMINADO));
            cargarListaSubdactualizacionpc();
        }
        catch (SystemException ex) {
            Logger.getLogger(PactualplancomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionSubdactualizacionpc() {
        cargarListaSubdactualizacionpc();
    }

    public void oprimirRegistrar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {
            if(!(boolean) registro.getCampos().get(consRealizada)){
                ejbPlaneacionCero.registrarActualizacionPlanAdquisiciones(compania,
                                true,
                                new BigInteger(registro.getCampos()
                                                .get(consActualizacion)
                                                .toString()),
                                Integer.parseInt(registro.getCampos().get("ANO")
                                                .toString()),
                                SessionUtil.getUser().getCodigo());
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(Constantes.MSM_PROCESO_EJECUTADO));
                insertarRegistroSub = false;
                editarFormulario = false;
                eliminarFormulario = false;
            }else{
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3550"));
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        try {
            String parametro = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA SUBPROYECTOS EN PLAN DE COMPRAS", modulo,
                            new Date(), true);
            if ("NO".equals(parametro)) {
                obtenerActualPlanDeCompras(FORMATOS.PDF);
            }
            else {
                obtenerActualPlanComprasSubp(FORMATOS.PDF);
            }
        }
        catch (SystemException ex) {
            Logger.getLogger(PactualplancomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        try {
            // <CODIGO_DESARROLLADO>
            String parametro = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA SUBPROYECTOS EN PLAN DE COMPRAS", modulo,
                            new Date(), true);

            if ("NO".equals(parametro)) {
                obtenerActualPlanDeCompras(FORMATOS.EXCEL97);
            }
            else {
                obtenerActualPlanComprasSubp(FORMATOS.EXCEL97);
            }
            // </CODIGO_DESARROLLADO>
        }
        catch (SystemException ex) {
            Logger.getLogger(PactualplancomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void obtenerActualPlanDeCompras(FORMATOS formatos) {
        // <CODIGO_DESARROLLADO>

        try {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("actualizacion",
                            registro.getCampos().get(consActualizacion));
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000450ICActualPlanCompras";
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_STRSQL", strSql);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);

        }
        catch (FileNotFoundException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
        }
        catch (JRException | IOException
                        | SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
        }
        // <CODIGO_DESARROLLADO>
    }

    public void obtenerActualPlanComprasSubp(FORMATOS formatos) {
        // <CODIGO_DESARROLLADO>
        try {
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("actualizacion",
                            registro.getCampos().get(consActualizacion));
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000451ICActualPlanComprasSubp";
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_STRSQL", strSql);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);

        }
        catch (FileNotFoundException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeInformativo(ex.getMessage());
        }
        catch (JRException | IOException
                        | SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA)
                                + ex.getMessage());
        }
        // <CODIGO_DESARROLLADO>
    }

    public void cambiarcmbAno() {
        // <CODIGO_DESARROLLADO>
        cargarListaRubro();
        cargarListaRubroE();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarRubroC(int rowNum) {
     // <CODIGO_DESARROLLADO>
        listaSubdactualizacionpc.get(rowNum).getCampos().put(GeneralParameterEnum.RUBRO.getName(), rubroSub);
        cargarListaElementoE();
     // </CODIGO_DESARROLLADO>
    }

    public void cambiarElementoC(int rowNum) {
     // <CODIGO_DESARROLLADO>
        listaSubdactualizacionpc.get(rowNum).getCampos().put(GeneralParameterEnum.RUBRO.getName(), rubroSub);
        cargarListaDependenciaE();
     // </CODIGO_DESARROLLADO>
    }

    public void cambiarDependenciaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaSubdactualizacionpc.get(rowNum).getCampos().put(GeneralParameterEnum.RUBRO.getName(), rubroSub);
        listaSubdactualizacionpc.get(rowNum).getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                        codigoSub);
        cargarListaMes();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaRubro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(GeneralParameterEnum.RUBRO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        nombreRubro = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
        rubroSub = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        registroSub.getCampos().put(GeneralParameterEnum.CODIGO.getName(), null);
        registroSub.getCampos().put(consNombreCorto, null);
        registroSub.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(), null);
        registroSub.getCampos().put(GeneralParameterEnum.NOMBRE.getName(), null);
        registroSub.getCampos().put("MES", null);
        dependencia="";
        cargarListaElemento();
        cargarListaDependencia();

    }

    public void seleccionarFilaRubroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreRubro = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
        nombreRubroAux = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
        rubroSub = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        cargarListaElementoE();
        cargarListaDependenciaE();

    }

    public void seleccionarFilaElemento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO.getName()));
        nombreElemento = registroAux.getCampos().get(consNombreCorto).toString();
        registroSub.getCampos().put(consNombreCorto, nombreElemento);
        cargarListaDependencia();

    }

    public void seleccionarFilaElementoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO.getName()).toString();
        nombreElemento = registroAux.getCampos().get(consNombreCorto).toString();
        codigoSub = registroAux.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO.getName()).toString();
        cargarListaDependenciaE();

    }

    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(GeneralParameterEnum.DEPENDENCIA.getName(),
                        registroAux.getCampos().get(consCodigoDependencia));
        registroSub.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
        cargarListaMes();
    }

    public void seleccionarFilaDependenciaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(consCodigoDependencia).toString();
        dependencia = registroAux.getCampos().get(consCodigoDependencia)
                        .toString();
        cargarListaMesE();
    }

    public void cambiarCantidad() {
        // <CODIGO_DESARROLLADO>
        double valorTotalSub;
        double cantidad = registroSub.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()) == null ? 0.0
            : Double.parseDouble(
                            (String) registroSub.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()));
        double valorUnitario = registroSub.getCampos()
                        .get(PactualplancomprasControladorEnum.PARAM2.getValue()) == null ? 0.0
                            : Double.parseDouble((String) registroSub
                                            .getCampos().get(PactualplancomprasControladorEnum.PARAM2.getValue()));
        valorTotalSub = cantidad * valorUnitario;
        registroSub.getCampos().put(GeneralParameterEnum.VALORTOTAL.getName(), valorTotalSub);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCantidadC(int rowNum) {
        Registro reg = listaSubdactualizacionpc.get(rowNum);
        double valorTotalSub;
        double cantidad = reg.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()) == null ? 0.0
            : Double.parseDouble(reg.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()).toString());
        double valorUnitario = reg.getCampos()
                        .get(PactualplancomprasControladorEnum.PARAM2.getValue()) == null ? 0.0
                            : Double.parseDouble(reg.getCampos().get(PactualplancomprasControladorEnum.PARAM2.getValue()).toString());
        valorTotalSub = cantidad * valorUnitario;
        listaSubdactualizacionpc.get(rowNum).getCampos().put(GeneralParameterEnum.VALORTOTAL.getName(),valorTotalSub);
    }
    
    public void cambiarValorUnitarioC(int rowNum){
        Registro reg = listaSubdactualizacionpc.get(rowNum);
        double valorTotalSub;
        double cantidad = reg.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()) == null ? 0.0
            : Double.parseDouble(reg.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()).toString());
        double valorUnitario = reg.getCampos()
                        .get(PactualplancomprasControladorEnum.PARAM2.getValue()) == null ? 0.0
                            : Double.parseDouble(reg.getCampos().get(PactualplancomprasControladorEnum.PARAM2.getValue()).toString());
        valorTotalSub = cantidad * valorUnitario;
        listaSubdactualizacionpc.get(rowNum).getCampos().put(GeneralParameterEnum.VALORTOTAL.getName(),valorTotalSub);
    }

    public void cambiarValorUnitario() {
        // <CODIGO_DESARROLLADO>
        double valorTotalSub;
        double cantidad = registroSub.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()) == null ? 0.0
            : Double.parseDouble(registroSub.getCampos().get(GeneralParameterEnum.CANTIDAD.getName()).toString());
        double valorUnitario = registroSub.getCampos()
                        .get(PactualplancomprasControladorEnum.PARAM2.getValue()) == null ? 0.0
                            : Double.parseDouble(registroSub.getCampos().get(PactualplancomprasControladorEnum.PARAM2.getValue()).toString());
        valorTotalSub = cantidad * valorUnitario;
        registroSub.getCampos().put(GeneralParameterEnum.VALORTOTAL.getName(), valorTotalSub);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if ("i".equals(accion)) {
            registro.getCampos().put("FECHA", new Date());
            cmbAno = (String) registro.getCampos().put("ANO",
                            String.valueOf(SysmanFunciones.ano(new Date())
                                - 1));
        }
        boolean realizada = registro.getCampos().get(consRealizada) == null
            ? false
            : (boolean) registro.getCampos().get(consRealizada);

        if (realizada) {
            bloqueaActualizacionPC = false;
            insertarRegistroSub = false;
            editarFormulario = false;
            eliminarFormulario = false;
        }
        else {
            bloqueaActualizacionPC = true;
            insertarRegistroSub = true;
            editarFormulario = true;
            eliminarFormulario = true;
        }
        cargarListaRubro();
        cargarListaRubroE();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        String strCondicion = "COMPANIA = ''" + compania + "'' ";
        try {
            registro.getCampos().put(consActualizacion,
                            ejbSysmanUtil.generarSiguienteConsecutivo(
                                            "ACTPLAN_DE_COMPRAS", strCondicion,
                                            consActualizacion));
        }
        catch (SystemException ex) {
            Logger.getLogger(PactualplancomprasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

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

        // </CODIGO_DESARROLLADO>
        return true;
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

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    public List<Registro> getListaActividad() {
        return listaActividad;
    }

    public void setListaActividad(List<Registro> listaActividad) {
        this.listaActividad = listaActividad;
    }

    public List<Registro> getListaSubproyecto() {
        return listaSubproyecto;
    }

    public void setListaSubproyecto(List<Registro> listaSubproyecto) {
        this.listaSubproyecto = listaSubproyecto;
    }

    public List<Registro> getListacmbAno() {
        return listacmbAno;
    }

    public void setListacmbAno(List<Registro> listacmbAno) {
        this.listacmbAno = listacmbAno;
    }

    public List<Registro> getListaSubdactualizacionpc() {
        return listaSubdactualizacionpc;
    }

    public void setListaSubdactualizacionpc(
        List<Registro> listaSubdactualizacionpc) {
        this.listaSubdactualizacionpc = listaSubdactualizacionpc;
    }

    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    public RegistroDataModelImpl getListaDependenciaE() {
        return listaDependenciaE;
    }

    public void setListaDependenciaE(RegistroDataModelImpl listaDependenciaE) {
        this.listaDependenciaE = listaDependenciaE;
    }

    public RegistroDataModelImpl getListaRubro() {
        return listaRubro;
    }

    public void setListaRubro(RegistroDataModelImpl listaRubro) {
        this.listaRubro = listaRubro;
    }

    public RegistroDataModelImpl getListaRubroE() {
        return listaRubroE;
    }

    public void setListaRubroE(RegistroDataModelImpl listaRubroE) {
        this.listaRubroE = listaRubroE;
    }

    public RegistroDataModelImpl getListaElemento() {
        return listaElemento;
    }

    public void setListaElemento(RegistroDataModelImpl listaElemento) {
        this.listaElemento = listaElemento;
    }

    public RegistroDataModelImpl getListaElementoE() {
        return listaElementoE;
    }

    public void setListaElementoE(RegistroDataModelImpl listaElementoE) {
        this.listaElementoE = listaElementoE;
    }

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
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

    public String getNombreRubro() {
        return nombreRubro;
    }

    public void setNombreRubro(String nombreRubro) {
        this.nombreRubro = nombreRubro;
    }

    public String getNombreRubroAux() {
        return nombreRubroAux;
    }

    public void setNombreRubroAux(String nombreRubroAux) {
        this.nombreRubroAux = nombreRubroAux;
    }

    public String getNombreElemento() {
        return nombreElemento;
    }

    public void setNombreElemento(String nombreElemento) {
        this.nombreElemento = nombreElemento;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isTexto50Visible() {
        return texto50Visible;
    }

    public void setTexto50Visible(boolean texto50Visible) {
        this.texto50Visible = texto50Visible;
    }

    public boolean isTexto46Visible() {
        return texto46Visible;
    }

    public void setTexto46Visible(boolean texto46Visible) {
        this.texto46Visible = texto46Visible;
    }

    public boolean isActualizacionPCVisible() {
        return actualizacionPCVisible;
    }

    public void setActualizacionPCVisible(boolean actualizacionPCVisible) {
        this.actualizacionPCVisible = actualizacionPCVisible;
    }

    public boolean isBloqueaActualizacionPC() {
        return bloqueaActualizacionPC;
    }

    public void setBloqueaActualizacionPC(boolean bloqueaActualizacionPC) {
        this.bloqueaActualizacionPC = bloqueaActualizacionPC;
    }

    public String getRubroSub() {
        return rubroSub;
    }

    public void setRubroSub(String rubroSub) {
        this.rubroSub = rubroSub;
    }

    public String getCmbAno() {
        return cmbAno;
    }

    public void setCmbAno(String cmbAno) {
        this.cmbAno = cmbAno;
    }

    public String getCodigoSub() {
        return codigoSub;
    }

    public void setCodigoSub(String codigoSub) {
        this.codigoSub = codigoSub;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getAuxiliarNombre() {
        return auxiliarC;
    }

    public void setAuxiliarNombre(String auxiliarNombre) {
        this.auxiliarC = auxiliarNombre;
    }

    public Object getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(Object centroCosto) {
        this.centroCosto = centroCosto;
    }

    public int getIndiceSubdactualizacionpc() {
        return indiceSubdactualizacionpc;
    }

    public void setIndiceSubdactualizacionpc(int indiceSubdactualizacionpc) {
        this.indiceSubdactualizacionpc = indiceSubdactualizacionpc;
    }

    public boolean isEditarFormulario() {
        return editarFormulario;
    }

    public void setEditarFormulario(boolean editarFormulario) {
        this.editarFormulario = editarFormulario;
    }

    public boolean isEliminarFormulario() {
        return eliminarFormulario;
    }

    public void setEliminarFormulario(boolean eliminarFormulario) {
        this.eliminarFormulario = eliminarFormulario;
    }

    public boolean isBloqueaNumeroOficio() {
        return bloqueaNumeroOficio;
    }

    public void setBloqueaNumeroOficio(boolean bloqueaNumeroOficio) {
        this.bloqueaNumeroOficio = bloqueaNumeroOficio;
    }

    public boolean isInsertarRegistroSub() {
        return insertarRegistroSub;
    }

    public void setInsertarRegistroSub(boolean insertarRegistroSub) {
        this.insertarRegistroSub = insertarRegistroSub;
    }

    public List<Registro> getListaMesE() {
        return listaMesE;
    }

    public void setListaMesE(List<Registro> listaMesE) {
        this.listaMesE = listaMesE;
    }
    
    

}
