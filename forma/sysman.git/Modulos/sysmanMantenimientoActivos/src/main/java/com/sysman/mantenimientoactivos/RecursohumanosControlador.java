package com.sysman.mantenimientoactivos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.cache.enums.UrlServiceCache;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.mantenimientoactivos.enums.RecursohumanosControladorEnum;
import com.sysman.mantenimientoactivos.enums.RecursohumanosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
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

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * @author ngomez
 * @version 1, 14/10/2015
 * 
 * @author eamaya
 * @version 2.0, 17/08/2017, Proceso de Refactoring DSS, Manejo de DSS
 * y cambio de numero de formulario por enum
 * 
 */
@ManagedBean
@ViewScoped

public class RecursohumanosControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;

    private String auxOpcion;
    private String parametroRuta;
    private Registro registroSubSubformularioRestricciones;
    private Registro registroSubSubformularioFotografia;
    private List<Registro> listaVinculacion;
    private List<Registro> listaRestriccion;

    private List<Registro> listaRegimen;
    private List<Registro> listaSubformulariorestricciones;
    private List<Registro> listaSubformulariofotografia;
    private RegistroDataModelImpl listaDocumentoCampo;
    private String opcion;
    private StreamedContent archivoDescarga;

    private final String sucursalCons;
    private final String companiaCons;
    private final String nombreUnoCons;
    private final String nombreDosCons;
    private final String apellidoUnoCons;
    private final String apellidoDosCons;
    private final String archivoCons;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    public RecursohumanosControlador() {
        super();
        compania = SessionUtil.getCompania();
        sucursalCons = "SUCURSAL";
        companiaCons = "COMPANIA";
        nombreUnoCons = "NOMBRE1";
        nombreDosCons = "NOMBRE2";
        apellidoUnoCons = "APELLIDO1";
        apellidoDosCons = "APELLIDO2";
        archivoCons = "ARCHIVO";

        try {
            parametroRuta = "RUTA DIGITALIZADO CONDUCTORES";
            numFormulario = GeneralCodigoFormaEnum.RECURSOHUMANOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registro = new Registro(new HashMap<String, Object>());
            registroSubSubformularioRestricciones = new Registro(
                            new HashMap<String, Object>());
            registroSubSubformularioFotografia = new Registro(
                            new HashMap<String, Object>());

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                opcion = (String) parametrosEntrada.get("opcion");
            }
            SessionUtil.cleanFlash();
        }
        catch (Exception ex) {
            Logger.getLogger(RecursohumanosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.cleanFlash();
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        tabla = "TERCERO";
        buscarLlave();

        switch (opcion) {
        case "1":
            auxOpcion = "";
            break;
        case "2":
            // Activos
            auxOpcion = "A";
            break;
        case "3":
            // Inactivos
            auxOpcion = "I";
            break;
        case "4":
            // Retirados
            auxOpcion = "R";
            break;
        default:
        }

        asignarOrigenDatos();

    }

    @Override
    public void asignarOrigenDatos() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(RecursohumanosControladorEnum.OPCION.getValue(),
                        auxOpcion);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RecursohumanosControladorUrlEnum.URL001
                                                        .getValue());

        urlLectura = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        RecursohumanosControladorUrlEnum.URL002.getValue());

        urlCreacion = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        RecursohumanosControladorUrlEnum.URL003.getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RecursohumanosControladorUrlEnum.URL004
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RecursohumanosControladorUrlEnum.URL005
                                                        .getValue());
    }

    @Override
    public void iniciarListas() {
        cargarListaVinculacion();
        cargarListaRestriccion();
        cargarListaRegimen();
        cargarListaDocumentoCampo();
    }

    @Override
    public void iniciarListasSub() {

        cargarListaSubformulariorestricciones();
        cargarListaSubformulariofotografia();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubformulariorestricciones = null;
        listaSubformulariofotografia = null;
    }

    public void cargarListaSubformulariorestricciones() {
        try {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(RecursohumanosControladorEnum.NIT.getValue(),
                            registro.getCampos().get("NIT"));

            param.put(GeneralParameterEnum.SUCURSAL.getName(),
                            registro.getCampos().get(sucursalCons));

            listaSubformulariorestricciones = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecursohumanosControladorUrlEnum.URL7602
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            "RESTRICCIONES"));

        }
        catch (SysmanException | SystemException e) {
            Logger.getLogger(RecursohumanosControlador.class.getName())
                            .log(Level.SEVERE, null, e);

        }
    }

    public void cargarListaSubformulariofotografia() {
        try {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(RecursohumanosControladorEnum.NIT.getValue(),
                            registro.getCampos().get("NIT"));

            listaSubformulariofotografia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecursohumanosControladorUrlEnum.URL9692
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(
                                            UrlServiceCache.SYSMANDSUNIST,
                                            "FOTOSCONDUCTORES"));
        }
        catch (SysmanException | SystemException e) {
            Logger.getLogger(RecursohumanosControlador.class.getName())
                            .log(Level.SEVERE, null, e);

        }

    }

    public void cargarListaVinculacion() {

        try {
            listaVinculacion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecursohumanosControladorUrlEnum.URL9853
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaRestriccion() {

        try {
            listaRestriccion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecursohumanosControladorUrlEnum.URL10400
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaRegimen() {

        try {
            listaRegimen = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RecursohumanosControladorUrlEnum.URL1515
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaDocumentoCampo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RecursohumanosControladorUrlEnum.URL11177
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(RecursohumanosControladorEnum.OPCION.getValue(),
                        auxOpcion);

        listaDocumentoCampo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void agregarRegistroSubSubformulariorestricciones() {
        try {
            registroSubSubformularioRestricciones.getCampos()
                            .remove("FECHARESTRICCIONAUX");
            registroSubSubformularioRestricciones.getCampos()
                            .remove("DESCRIPCION");
            registroSubSubformularioRestricciones.getCampos().put(companiaCons,
                            compania);
            registroSubSubformularioRestricciones.getCampos().put(
                            "DOCUMENTO_IDENTIDAD",
                            registro.getCampos().get("NIT"));
            registroSubSubformularioRestricciones.getCampos().put(sucursalCons,
                            registro.getCampos().get(sucursalCons));

            registroSubSubformularioRestricciones.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSubSubformularioRestricciones.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            RecursohumanosControladorUrlEnum.URL14383
                                                            .getValue());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubSubformularioRestricciones.getCampos());

            cargarListaSubformulariorestricciones();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));
        }
        catch (SystemException ex) {
            Logger.getLogger(RecursohumanosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubSubformularioRestricciones = new Registro(
                            new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubformulariorestricciones(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();
        try {
            reg.getCampos().remove("FECHARESTRICCIONAUX");
            reg.getCampos().remove("DESCRIPCION");

            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());

            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());

            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            RecursohumanosControladorUrlEnum.URL19332
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            cargarListaSubformulariorestricciones();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void eliminarRegSubSubformulariorestricciones(Registro reg) {
        UrlBean urlDelete = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RecursohumanosControladorUrlEnum.URL17807
                                                        .getValue());

        try {
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            cargarListaSubformulariorestricciones();
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cancelarEdicionSubformulariorestricciones() {
        cargarListaSubformulariorestricciones();
        cargarListaSubformulariofotografia();
    }

    public void agregarRegistroSubSubformulariofotografia() {
        try {
            registroSubSubformularioFotografia.getCampos().put(companiaCons,
                            compania);
            registroSubSubformularioFotografia.getCampos().put("REGISTRO",
                            registro.getCampos().get("NIT"));
            registroSubSubformularioFotografia.getCampos().put(sucursalCons,
                            registro.getCampos().get(sucursalCons));
            registroSubSubformularioFotografia.getCampos().put("VIGENCIA",
                            SysmanFunciones.getParteFecha(new Date(),
                                            Calendar.YEAR));

            registroSubSubformularioFotografia.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            registroSubSubformularioFotografia.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            RecursohumanosControladorUrlEnum.URL18183
                                                            .getValue());

            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSubSubformularioFotografia.getCampos());

            cargarListaSubformulariofotografia();

        }
        catch (SystemException ex) {
            Logger.getLogger(RecursohumanosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSubSubformularioFotografia = new Registro(
                            new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubformulariofotografia(RowEditEvent event) {
        // METODO_NO_IMPLEMENTADO
    }

    public void eliminarRegSubSubformulariofotografia(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            RecursohumanosControladorUrlEnum.URL21293
                                                            .getValue());

            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));

            cargarListaSubformulariofotografia();
        }
        catch (SystemException ex) {
            Logger.getLogger(RecursohumanosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    public void cancelarEdicionSubformulariofotografia() {
        cargarListaSubformulariofotografia();
    }

    public void oprimirCargarRuta1(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirAbrir(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando12(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaNacimiento() {
        // <CODIGO_DESARROLLADO>

        try {

            registro.getCampos().put("EDAD",
                            ejbSysmanUtl.calcularEdadDelPersonal((Date) registro
                                            .getCampos()
                                            .get("FECHANACIMIENTO")));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // Metodo heredado de la clase BeanBase
    }

    @Override
    public void cargarRegistro() {
        precargarRegistro();

        if ("i".equals(accion)) {
            registro.getCampos().put(sucursalCons, "001");
            registro.getCampos().put("CONDUCTOR", "-1");
        }
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(companiaCons, compania);

        registro.getCampos().remove("EDAD");
        registro.getCampos().put(nombreUnoCons,
                        registro.getCampos().get(nombreUnoCons)
                                        .toString().toUpperCase());
        registro.getCampos().put(nombreDosCons,
                        registro.getCampos().get(nombreDosCons)
                                        .toString().toUpperCase());
        registro.getCampos().put(apellidoUnoCons, registro.getCampos()
                        .get(apellidoUnoCons).toString().toUpperCase());
        registro.getCampos().put(apellidoDosCons, registro.getCampos()
                        .get(apellidoDosCons).toString().toUpperCase());
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
        registro.getCampos().remove("EDAD");
        registro.getCampos().put(nombreUnoCons,
                        registro.getCampos().get(nombreUnoCons)
                                        .toString().toUpperCase());
        registro.getCampos().put(nombreDosCons,
                        SysmanFunciones.nvl(
                                        registro.getCampos().get(nombreDosCons),
                                        "")
                                        .toString().toUpperCase());
        registro.getCampos().put(apellidoUnoCons, registro.getCampos()
                        .get(apellidoUnoCons).toString().toUpperCase());
        registro.getCampos().put(apellidoDosCons,
                        SysmanFunciones.nvl(registro.getCampos()
                                        .get(apellidoDosCons), "").toString()
                                        .toUpperCase());

        if (accion.equals(ACCION_MODIFICAR)) {
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {

        return true;
    }

    @Override
    public boolean eliminarAntes() {

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return false;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void oprimirDescargar(Registro reg, int num) {

        archivoDescarga = null;

        File archivo = new File(generarRuta(
                        parametroRuta,
                        (String) reg.getCampos()
                                        .get("REGISTRO"))
            + (String) reg.getCampos().get(archivoCons));

        try (InputStream input = new FileInputStream(archivo);) {

            byte[] vec = new byte[(int) archivo.length()];
            input.read(vec, 0, vec.length);
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(vec), archivo.getName());

        }
        catch (IOException | JRException ex) {
            Logger.getLogger(RecursohumanosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void cargarArchivoLector(FileUploadEvent event) {
        try {
            // <CODIGO_DESARROLLADO>
            String nombreArch = event.getFile().getFileName();
            nombreArch = nombreArch.contains(File.separator)
                ? nombreArch.substring(
                                nombreArch.lastIndexOf(File.separator) + 1,
                                nombreArch.length())
                : nombreArch;
            String ruta = generarRuta(parametroRuta,
                            SysmanFunciones.nvl(registro.getCampos().get("NIT"),
                                            "").toString());
            if (ruta != null) {
                JsfUtil.upload(event.getFile().getInputstream(), nombreArch,
                                ruta);
                registroSubSubformularioFotografia.getCampos().put(archivoCons,
                                nombreArch);
                agregarRegistroSubSubformulariofotografia();
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2188")
                                .replace("$#parametrouta$# ", parametroRuta));
            }
            // </CODIGO_DESARROLLADO>
        }
        catch (IOException ex) {
            Logger.getLogger(RecursohumanosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public String generarRuta(String parametro, String documento) {
        String strRuta;
        String placa = documento.replace(" ", "");

        Object aux = null;
        try {

            aux = ejbSysmanUtl.consultarParametro(compania, parametro,
                            SessionUtil.getModulo(),
                            new Date(), false);

        }
        catch (SystemException ex) {
            Logger.getLogger(RecursohumanosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        if (aux != null) {
            strRuta = ((String) aux) + placa + "\\";

            File folder = new File(strRuta);
            folder.mkdirs();
            return strRuta;
        }
        else {
            return null;
        }
    }

    public void seleccionarFilaDocumentoCampo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        String nitAc = registroAux.getCampos().get("NIT").toString();

        if (!SysmanFunciones.validarVariableVacio(nitAc)) {

            registro.getCampos().put(nombreUnoCons,
                            SysmanFunciones.nvl(registroAux.getCampos()
                                            .get(nombreUnoCons), "")
                                            .toString());

            registro.getCampos().put(nombreDosCons,
                            SysmanFunciones.nvl(registroAux.getCampos()
                                            .get(nombreDosCons), "")
                                            .toString());

            registro.getCampos().put(apellidoUnoCons,
                            SysmanFunciones.nvl(registroAux.getCampos()
                                            .get(apellidoUnoCons), "")
                                            .toString());

            registro.getCampos().put(apellidoDosCons,
                            SysmanFunciones.nvl(registroAux.getCampos()
                                            .get(apellidoDosCons), "")
                                            .toString());
            registro.getCampos().put("NIT", nitAc);

            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            registro.asignarLlave(llave);

            cargarRegistro(registro.getLlave(), "m", -2);

            registro.getCampos().put("CONDUCTOR", -1);
        }

    }

    public List<Registro> getListaVinculacion() {
        return listaVinculacion;
    }

    public void setListaVinculacion(List<Registro> listaVinculacion) {
        this.listaVinculacion = listaVinculacion;
    }

    public List<Registro> getListaRestriccion() {
        return listaRestriccion;
    }

    public void setListaRestriccion(List<Registro> listaRestriccion) {
        this.listaRestriccion = listaRestriccion;
    }

    public List<Registro> getListaRegimen() {
        return listaRegimen;
    }

    public void setListaRegimen(List<Registro> listaRegimen) {
        this.listaRegimen = listaRegimen;
    }

    public List<Registro> getListaSubformulariorestricciones() {
        return listaSubformulariorestricciones;
    }

    public void setListaSubformulariorestricciones(
        List<Registro> listaSubformulariorestricciones) {
        this.listaSubformulariorestricciones = listaSubformulariorestricciones;
    }

    public List<Registro> getListaSubformulariofotografia() {
        return listaSubformulariofotografia;
    }

    public void setListaSubformulariofotografia(
        List<Registro> listaSubformulariofotografia) {
        this.listaSubformulariofotografia = listaSubformulariofotografia;
    }

    public Registro getRegistroSubSubformularioRestricciones() {
        return registroSubSubformularioRestricciones;
    }

    public void setRegistroSubSubformularioRestricciones(
        Registro registroSubSubformularioRestricciones) {
        this.registroSubSubformularioRestricciones = registroSubSubformularioRestricciones;
    }

    public Registro getRegistroSubSubformularioFotografia() {
        return registroSubSubformularioFotografia;
    }

    public void setRegistroSubSubformularioFotografia(
        Registro registroSubSubformularioFotografia) {
        this.registroSubSubformularioFotografia = registroSubSubformularioFotografia;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getAuxOpcion() {
        return auxOpcion;
    }

    public void setAuxOpcion(String auxOpcion) {
        this.auxOpcion = auxOpcion;
    }

    public String getParametroRuta() {
        return parametroRuta;
    }

    public void setParametroRuta(String parametroRuta) {
        this.parametroRuta = parametroRuta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public RegistroDataModelImpl getListaDocumentoCampo() {
        return listaDocumentoCampo;
    }

    public void setListaDocumentoCampo(
        RegistroDataModelImpl listaDocumentoCampo) {
        this.listaDocumentoCampo = listaDocumentoCampo;
    }

}
