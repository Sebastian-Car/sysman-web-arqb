
package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.SubproyectorubrosControladorEnum;
import com.sysman.bancoproyectos.enums.SubproyectorubrosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;

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

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 23/09/2015
 *
 * @author lcortes
 * @version 2, 28/09/2017, 10,11,17,18/10/2017. Refactorizacion de
 * codigo, reemplazo de llamado a la clase Acciones y revision de
 * observaciones de la herramienta SonarLint.
 */
@ManagedBean
@ViewScoped
public class SubproyectorubrosControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String modulo;
    private final String vigenciaCons;
    private final String dimensionCons;
    private final String sectorCons;
    private final String programaCons;
    private final String subprogramaCons;
    private final String rubroCons;
    private final String nombreCons;
    private final String codigoCons;
    private boolean cargar;
    private RegistroDataModelImpl listaDimension;
    private RegistroDataModelImpl listaSector;
    private RegistroDataModelImpl listaPrograma;
    private RegistroDataModelImpl listaSubprograma;
    private RegistroDataModelImpl listaFuenteRecursosRubro;
    private RegistroDataModelImpl listaRubroPptales;
    List<Registro> listaVigencia;
    private String codigoProyecto;
    private String vigencia;
    private String rangoDimension;
    private String rangoSector;
    private String rangoPrograma;
    private String rangoSubPrograma;
    private String menuActual;
    private boolean muestraRegistro;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    public SubproyectorubrosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        vigenciaCons = GeneralParameterEnum.VIGENCIA.getName();
        dimensionCons = SubproyectorubrosControladorEnum.DIMENSION.getValue();
        sectorCons = GeneralParameterEnum.SECTOR.getName();
        programaCons = SubproyectorubrosControladorEnum.PROGRAMA.getValue();
        subprogramaCons = SubproyectorubrosControladorEnum.SUBPROGRAMA
                        .getValue();
        rubroCons = SubproyectorubrosControladorEnum.RUBROPPTALES.getValue();
        nombreCons = GeneralParameterEnum.NOMBRE.getName();
        codigoCons = GeneralParameterEnum.CODIGO.getName();

        try {
            // 246
            numFormulario = GeneralCodigoFormaEnum.SUBPROYECTORUBROS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            menuActual = SessionUtil.getMenuActual();
            menuActual = menuActual == null ? "NULL" : menuActual;
            switch (menuActual) {
            case "52020102":
            case "52020402":
                muestraRegistro = false;
                break;
            case "52020101":
                muestraRegistro = true;
                break;
            case "NULL":
                SessionUtil.redireccionarMenu();
                break;
            default:
                break;
            }

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            String action;
            if (parametrosEntrada != null) {
                codigoProyecto = parametrosEntrada
                                .get("codigoProyecto").toString();
                action = parametrosEntrada.get("accion").toString();
                if (("v").equals(action)) {
                    muestraRegistro = false;
                }
            }

            registro = new Registro(new HashMap<String, Object>());

        }
        catch (Exception ex) {
            Logger.getLogger(SubproyectorubrosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            cerrarFormulario();
        }
        finally {
            SessionUtil.cleanFlash();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.BP_PROYECTOSRUBROS;
        buscarLlave();
        asignarOrigenDatos();
        obtenerRangos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        codigoProyecto);

    }

    public void obtenerRangos() {
        try {
            rangoDimension = ejbSysmanUtil.consultarParametro(compania,
                            "RANGO DE NIVEL DE DIGITOS EN DIMENSION", modulo,
                            new Date(), true);
            rangoSector = ejbSysmanUtil.consultarParametro(compania,
                            "RANGO DE NIVEL DE DIGITOS EN SECTOR", modulo,
                            new Date(), true);
            rangoPrograma = ejbSysmanUtil.consultarParametro(compania,
                            "RANGO DE NIVEL DE DIGITOS EN PROGRAMA", modulo,
                            new Date(), true);
            rangoSubPrograma = ejbSysmanUtil.consultarParametro(compania,
                            "RANGO DE NIVEL DE DIGITOS EN SUBPROGRAMA", modulo,
                            new Date(), true);
        }

        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            if (rangoDimension == null || rangoSector == null || rangoPrograma == null || rangoSubPrograma == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2464"));
            }
        }
    }

    @Override
    public void iniciarListas() {
        cargarListaVigencia();
    }

    @Override
    public void iniciarListasSub() {
        //
    }

    @Override
    public void iniciarListasSubNulo() {
        // NO ESTA IMPLEMENTADO
    }

    public void cargarListaVigencia() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubproyectorubrosControladorUrlEnum.URL8386
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaDimension() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubproyectorubrosControladorUrlEnum.URL8880
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), vigencia);

        String[] subRango = rangoDimension.split(" ");
        param.put(SubproyectorubrosControladorEnum.RANGOA.getValue(),
                        subRango[0]);

        param.put(SubproyectorubrosControladorEnum.RANGOB.getValue(),
                        subRango[2]);

        listaDimension = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListasector() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubproyectorubrosControladorUrlEnum.URL12638
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), vigencia);
        param.put(SubproyectorubrosControladorEnum.DIMENSION.getValue(),
                        registro.getCampos().get(SubproyectorubrosControladorEnum.DIMENSION.getValue()));
        String[] subRango = rangoSector.split(" ");
        param.put(SubproyectorubrosControladorEnum.RANGOA.getValue(),
                        subRango[0]);

        param.put(SubproyectorubrosControladorEnum.RANGOB.getValue(),
                        subRango[2]);

        listaSector = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaPrograma() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubproyectorubrosControladorUrlEnum.URL12638
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), vigencia);
        param.put(SubproyectorubrosControladorEnum.DIMENSION.getValue(),
                        registro.getCampos().get("SECTOR"));
        String[] subRango = rangoPrograma.split(" ");
        param.put(SubproyectorubrosControladorEnum.RANGOA.getValue(),
                        subRango[0]);

        param.put(SubproyectorubrosControladorEnum.RANGOB.getValue(),
                        subRango[2]);

        listaPrograma = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaSubprograma() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubproyectorubrosControladorUrlEnum.URL12638
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), vigencia);
        param.put(SubproyectorubrosControladorEnum.DIMENSION.getValue(),
                        registro.getCampos().get("PROGRAMA"));
        String[] subRango = rangoSubPrograma.split(" ");
        param.put(SubproyectorubrosControladorEnum.RANGOA.getValue(),
                        subRango[0]);

        param.put(SubproyectorubrosControladorEnum.RANGOB.getValue(),
                        subRango[2]);

        listaSubprograma = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaRubroPptales() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubproyectorubrosControladorUrlEnum.URL12639
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), vigencia);
        param.put(SubproyectorubrosControladorEnum.DIMENSION.getValue(),
                        registro.getCampos().get(SubproyectorubrosControladorEnum.SUBPROGRAMA.getValue()));

        listaRubroPptales = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cambiarVigencia() {

        vigencia = registro.getCampos().get(vigenciaCons).toString();
        registro.getCampos().put(dimensionCons, null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_DIMENSION.getValue(), null);
        registro.getCampos().put(sectorCons, null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_SECTOR.getValue(), null);
        registro.getCampos().put(programaCons, null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_PROGRAMA.getValue(), null);
        registro.getCampos().put(subprogramaCons, null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_SUB_PROGRAMA.getValue(), null);
        registro.getCampos().put(rubroCons, null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_RUBRO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.CENTRO_COSTO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_CENTRO_COSTO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.FUENTERECURSOSRUBRO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_FUENTE_RECURSOS.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.REFERENCIA.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_REFERENCIA.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.AUXILIAR.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_AUXILIAR_GENERAL.getValue(), null);
        cargarListaDimension();
    }

    public void seleccionarFilaDimension(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(dimensionCons,
                        registroAux.getCampos().get(codigoCons));
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_DIMENSION.getValue(),
                        registroAux.getCampos().get(nombreCons).toString());

        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_SECTOR.getValue(), null);
        registro.getCampos().put(sectorCons, null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_PROGRAMA.getValue(), null);
        registro.getCampos().put(programaCons, null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_SUB_PROGRAMA.getValue(), null);
        registro.getCampos().put(subprogramaCons, null);
        registro.getCampos().put(rubroCons, null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_RUBRO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.CENTRO_COSTO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_CENTRO_COSTO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.FUENTERECURSOSRUBRO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_FUENTE_RECURSOS.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.REFERENCIA.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_REFERENCIA.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.AUXILIAR.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_AUXILIAR_GENERAL.getValue(), null);
        cargarListasector();
    }

    public void seleccionarFilaSector(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(sectorCons,
                        registroAux.getCampos().get(codigoCons));
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_SECTOR.getValue(),
                        registroAux.getCampos().get(nombreCons).toString());

        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_PROGRAMA.getValue(), null);
        registro.getCampos().put(programaCons, null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_SUB_PROGRAMA.getValue(), null);
        registro.getCampos().put(subprogramaCons, null);
        registro.getCampos().put(rubroCons, null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_RUBRO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.CENTRO_COSTO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_CENTRO_COSTO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.FUENTERECURSOSRUBRO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_FUENTE_RECURSOS.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.REFERENCIA.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_REFERENCIA.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.AUXILIAR.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_AUXILIAR_GENERAL.getValue(), null);
        cargarListaPrograma();
    }

    public void seleccionarFilaPrograma(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(programaCons,
                        registroAux.getCampos().get(codigoCons));
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_PROGRAMA.getValue(),
                        registroAux.getCampos().get(nombreCons).toString());

        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_SUB_PROGRAMA.getValue(), null);
        registro.getCampos().put(subprogramaCons, null);
        registro.getCampos().put(rubroCons, null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_RUBRO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.CENTRO_COSTO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_CENTRO_COSTO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.FUENTERECURSOSRUBRO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_FUENTE_RECURSOS.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.REFERENCIA.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_REFERENCIA.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.AUXILIAR.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_AUXILIAR_GENERAL.getValue(), null);
        cargarListaSubprograma();
    }

    public void seleccionarFilaSubprograma(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(subprogramaCons,
                        registroAux.getCampos().get(codigoCons));
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_SUB_PROGRAMA.getValue(),
                        registroAux.getCampos().get(nombreCons).toString());

        registro.getCampos().put(rubroCons, null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_RUBRO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.CENTRO_COSTO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_CENTRO_COSTO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.FUENTERECURSOSRUBRO.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_FUENTE_RECURSOS.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.REFERENCIA.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_REFERENCIA.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.AUXILIAR.getValue(), null);
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_AUXILIAR_GENERAL.getValue(), null);
        cargarListaRubroPptales();
    }

    public void seleccionarFilaRubroPptales(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(rubroCons,
                        registroAux.getCampos().get(codigoCons).toString());
        registro.getCampos().put(SubproyectorubrosControladorEnum.CENTRO_COSTO.getValue(),
                        registroAux.getCampos().get(SubproyectorubrosControladorEnum.CENTRO_COSTO.getValue()).toString());
        registro.getCampos().put(SubproyectorubrosControladorEnum.REFERENCIA.getValue(),
                        registroAux.getCampos().get(SubproyectorubrosControladorEnum.REFERENCIA.getValue()).toString());
        registro.getCampos().put(SubproyectorubrosControladorEnum.FUENTERECURSOSRUBRO.getValue(),
                        registroAux.getCampos().get("FUENTE_RECURSO").toString());
        registro.getCampos().put(SubproyectorubrosControladorEnum.AUXILIAR.getValue(),
                registroAux.getCampos().get(SubproyectorubrosControladorEnum.AUXILIAR.getValue()).toString());
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_RUBRO.getValue(),
                        registroAux.getCampos().get(nombreCons).toString());
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_FUENTE_RECURSOS.getValue(),
                        registroAux.getCampos().get(SubproyectorubrosControladorEnum.NOMBRE_FUENTE_RECURSOS.getValue()).toString());
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_CENTRO_COSTO.getValue(),
                        registroAux.getCampos().get(SubproyectorubrosControladorEnum.NOMBRE_CENTRO_COSTO.getValue()).toString());
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_REFERENCIA.getValue(),
                        registroAux.getCampos().get(SubproyectorubrosControladorEnum.NOMBRE_REFERENCIA.getValue()).toString());
        registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_AUXILIAR_GENERAL.getValue(),
                registroAux.getCampos().get(SubproyectorubrosControladorEnum.NOMBRE_AUXILIAR_GENERAL.getValue()).toString());
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>

        if (ACCION_INSERTAR.equals(accion)) {
            listaDimension = null;
            listaSector = null;
            listaPrograma = null;
            listaSubprograma = null;
            listaFuenteRecursosRubro = null;
            listaRubroPptales = null;
            registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_DIMENSION.getValue(), null);
            registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_SECTOR.getValue(), null);
            registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_PROGRAMA.getValue(), null);
            registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_SUB_PROGRAMA.getValue(), null);
            registro.getCampos().put("NOMBRE_FUENTE", null);
            registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_RUBRO.getValue(), null);
            registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_CENTRO_COSTO.getValue(), null);
            registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_REFERENCIA.getValue(), null);
            registro.getCampos().put(SubproyectorubrosControladorEnum.NOMBRE_AUXILIAR_GENERAL.getValue(), null);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo invocado al ejecutar el comando remoto rcListaDimension
     * en la vista
     *
     */
    public void ejecutarrcListaDimension() {
        //
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.PROYECTO.getName(),
                        codigoProyecto);
        registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_DIMENSION.getValue());
        registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_SECTOR.getValue());
        registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_PROGRAMA.getValue());
        registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_SUB_PROGRAMA.getValue());
        registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_FUENTE_RECURSOS.getValue());
        registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_RUBRO.getValue());
        registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_REFERENCIA.getValue());
        registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_CENTRO_COSTO.getValue());
        registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_AUXILIAR_GENERAL.getValue());
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
        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos()
                            .remove(GeneralParameterEnum.PROYECTO.getName());
            registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_DIMENSION.getValue());
            registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_SECTOR.getValue());
            registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_PROGRAMA.getValue());
            registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_SUB_PROGRAMA.getValue());
            registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_FUENTE_RECURSOS.getValue());
            registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_RUBRO.getValue());
            registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_REFERENCIA.getValue());
            registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_CENTRO_COSTO.getValue());
            registro.getCampos().remove(SubproyectorubrosControladorEnum.NOMBRE_AUXILIAR_GENERAL.getValue());
        }
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
        cargar = true;
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public List<Registro> getListaVigencia() {
        return listaVigencia;
    }

    public void setListaVigencia(List<Registro> listaVigencia) {
        this.listaVigencia = listaVigencia;
    }

    public RegistroDataModelImpl getListaDimension() {
        return listaDimension;
    }

    public void setListaDimension(RegistroDataModelImpl listaDimension) {
        this.listaDimension = listaDimension;
    }

    public RegistroDataModelImpl getListaSector() {
        return listaSector;
    }

    public void setListaSector(RegistroDataModelImpl listaSector) {
        this.listaSector = listaSector;
    }

    public RegistroDataModelImpl getListaPrograma() {
        return listaPrograma;
    }

    public void setListaPrograma(RegistroDataModelImpl listaPrograma) {
        this.listaPrograma = listaPrograma;
    }

    public RegistroDataModelImpl getListaSubprograma() {
        return listaSubprograma;
    }

    public void setListaSubprograma(RegistroDataModelImpl listaSubprograma) {
        this.listaSubprograma = listaSubprograma;
    }

    public RegistroDataModelImpl getListaFuenteRecursosRubro() {
        return listaFuenteRecursosRubro;
    }

    public void setListaFuenteRecursosRubro(
        RegistroDataModelImpl listaFuenteRecursosRubro) {
        this.listaFuenteRecursosRubro = listaFuenteRecursosRubro;
    }

    public RegistroDataModelImpl getListaRubroPptales() {
        return listaRubroPptales;
    }

    public void setListaRubroPptales(RegistroDataModelImpl listaRubroPptales) {
        this.listaRubroPptales = listaRubroPptales;
    }

    public String getCodigoProyecto() {
        return codigoProyecto;
    }

    public void setCodigoProyecto(String codigoProyecto) {
        this.codigoProyecto = codigoProyecto;
    }

    public boolean isMuestraRegistro() {
        return muestraRegistro;
    }

    public void setMuestraRegistro(boolean muestraRegistro) {
        this.muestraRegistro = muestraRegistro;
    }

    public boolean isCargar() {
        return cargar;
    }

    public void setCargar(boolean cargar) {
        this.cargar = cargar;
    }

}