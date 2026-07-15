package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCuatroRemote;
import com.sysman.bancoproyectos.enums.FrmrubroinversionsControladorEnum;
import com.sysman.bancoproyectos.enums.FrmrubroinversionsControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.jsfutil.ReportesBean.FORMATOS;

import java.io.IOException;
import java.sql.SQLException;
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

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
/**
 *
 * @author NGOMEZ
 * @version 1, 29/02/2016
 *
 * @author jguerrero
 * @version 1, 21/09/2017
 * 
 * @version 3, 12/10/2017, <strong>pespitia</strong>:
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Verificacion y ajustes del Refactoring de sentencias SQL.
 */
@ManagedBean
@ViewScoped
public class FrmrubroinversionsControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * inicio sesion
     */
    private final String usuario = SessionUtil.getUser().getCodigo();

    /**
     * Constante nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante nivel de clase que aloja la cadena:
     * <code>DESTINO</code>
     */
    private final String cDestino = GeneralParameterEnum.DESTINO.getName();

    /** Constante nivel de clase que aloja el valor VIGENCIA */
    private final String cVigencia = GeneralParameterEnum.VIGENCIA.getName();

    /** Constante nivel de clase que aloja el valor RUBRO */
    private final String cRubro = GeneralParameterEnum.RUBRO.getName();

    /**
     * Constante nivel de clase que aloja la cadena:
     * <code>RUBRO_INVER</code>
     */
    private final String cRubroInver = FrmrubroinversionsControladorEnum.RUBRO_INVER
                    .getValue();

    /** Constante nivel de clase que aloja el valor NOMBRE */
    private final String cNombre = GeneralParameterEnum.NOMBRE.getName();

    /** Constante nivel de clase que aloja el valor CODIGO */
    private final String cCodigo = GeneralParameterEnum.CODIGO.getName();

    /** Constante nivel de clase que aloja el valor ID */
    private final String cId = FrmrubroinversionsControladorEnum.ID.getValue();

    /** Constante nivel de clase que aloja el valor AUXILIAR */
    private final String cAuxiliar = GeneralParameterEnum.AUXILIAR.getName();

    /**
     * Variable que almacena el año seleccionado en el combo Vigencia.
     */
    private int vigencia;

    /**
     * Variable a nivel de clase que guarda el rubro seleccionado en
     * el combo Rubro.
     */
    private String codRubro;

    /**
     * Variable que contiene el texto que se debe mostrar en el
     * dialogo mensaje confirmar. DG202.
     */
    private String mensajeConfirmar;

    /**
     * Variable que contiene el texto que se debe mostrar en el
     * dialogo mensaje secundario. DG203.
     */
    private String mensajeSecundario;

    /**
     * Variable que controla la visibilidad del dialogo mensaje
     * confirmar. DG202.
     */
    private boolean verMensajeConfirmar;

    /**
     * Variable que controla la visibilidad del dialogo mensaje
     * secundario. DG203.
     */
    private boolean verMensajeSecundario;

    private Registro registroSub;
    private List<Registro> listaVigencia;
    private RegistroDataModelImpl listaSubrubroinversiondet;
    private RegistroDataModelImpl listaRubro;
    private RegistroDataModelImpl listaRubroSub;
    private RegistroDataModelImpl listaRubroSubE;
    private String auxiliar;
    private String auxiliarAux;
    
    private StreamedContent archivoDescarga;

    /**
     * Atributo que almacena el nombre del rubro seleccionado en el
     * combo Rubro
     */
    private String nombreRubro;

    private String auxiliarRubro;
    private String auxiliarVigencia;
    private boolean boton;

    private boolean bloqueaRubro;

    @EJB
    private EjbBancoProyectoCuatroRemote ejbBancoProyectoCuatro;

    public FrmrubroinversionsControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 538
            numFormulario = GeneralCodigoFormaEnum.FRMRUBROINVERSIONS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmrubroinversionsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

            SessionUtil.redireccionarMenuPermisos();
        }

        registro = new Registro(new HashMap<String, Object>());
        registroSub = new Registro(new HashMap<String, Object>());
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.BP_RUBRO_INVERSION;

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
        cargarListaRubroSub();
        cargarListaRubroSubE();
        cargarListaVigencia();
    }

    @Override
    public void iniciarListasSub() {
        auxiliarRubro = registro.getCampos().get(cRubro).toString();
        auxiliarVigencia = registro.getCampos().get(cVigencia).toString();

        cargarListaRubroSub();
        cargarListaRubroSubE();

        nombreRubro = registro.getCampos().get(cNombre).toString();
        cargarListaSubrubroinversiondet();
    }

    @Override
    public void iniciarListasSubNulo() {
        listaSubrubroinversiondet = null;
    }

    public void cargarListaSubrubroinversiondet() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmrubroinversionsControladorUrlEnum.URL0004
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cRubroInver, registro.getCampos().get(cRubro));
        param.put(cVigencia, registro.getCampos().get(cVigencia));

        try {
            String[] rowKey = CacheUtil.getLlaveServicio(urlConexionCache,
                            GenericUrlEnum.BP_RUBRO_INVERSION_DET.getTable());

            listaSubrubroinversiondet = new RegistroDataModelImpl(
                            urlBean.getUrl(), urlBean.getUrlConteo().getUrl(),
                            param, rowKey);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaVigencia() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listaVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmrubroinversionsControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaRubro() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmrubroinversionsControladorUrlEnum.URL0010
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cVigencia, vigencia);
        param.put(cDestino, "I");

        listaRubro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cCodigo);
    }

    public void cargarListaRubroSub() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaRubroSubE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmrubroinversionsControladorUrlEnum.URL0013
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cVigencia, registro.getCampos().get(cVigencia));

        listaRubroSubE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cId);
    }

    public void agregarRegistroSubSubrubroinversiondet() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void editarRegSubSubrubroinversiondet(RowEditEvent event) {
        Registro reg = (Registro) event.getObject();

        reg.getCampos().put("DATE_MODIFIED", new Date());
        reg.getCampos().put("MODIFIED_BY", usuario);

        reg.getCampos().remove(cCompania);
        reg.getCampos().remove(cVigencia);
        reg.getCampos().remove(cRubroInver);
        reg.getCampos().remove("NIVEL_NUM");

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmrubroinversionsControladorUrlEnum.URL0011
                                                        .getValue());
        try {
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void eliminarRegSubSubrubroinversiondet(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmrubroinversionsControladorUrlEnum.URL0012
                                                            .getValue());

            requestManager.delete(urlDelete.getUrl(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));

            listaSubrubroinversiondet.load();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cancelarEdicionSubrubroinversiondet() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que desencadena los eventos de presionar el boton
     * Ingresar Rubros.
     */
    public void oprimirIngresarRubros() {
        // <CODIGO_DESARROLLADO>
        ingresarRubros();
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirGenerar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirVerifNuevos() {
        // <CODIGO_DESARROLLADO>
        try {
    	archivoDescarga=null;
    	generarReporte();

        String contador = ejbBancoProyectoCuatro.verificarNuevosRubros(
                            compania, codRubro,
                            vigencia, usuario);

            JsfUtil.agregarMensajeInformativo(idioma.getString(
                            "0".equals(contador) ? "TB_TB2435" : "TB_TB2437")
                            .replace("#CANT#", contador));
            
        }
        catch (SystemException  e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaRubro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codRubro = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();

        validarRubro();

        if (verMensajeConfirmar) {
            nombreRubro = SysmanFunciones
                            .nvl(registroAux.getCampos().get(cNombre), "")
                            .toString();
        }
    }

    public void seleccionarFilaRubroSub(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registroSub.getCampos().put(cRubro,
                        registroAux.getCampos().get(cCodigo));
        registroSub.getCampos().put(cAuxiliar,
                        registroAux.getCampos().get(cAuxiliar));
    }

    public void seleccionarFilaRubroSubE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cCodigo).toString();
        auxiliarAux = registroAux.getCampos().get(cAuxiliar).toString();
    }

    public void cambiarRubroSubC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        listaSubrubroinversiondet.getDatasource().get(rowNum).getCampos()
                        .put(cAuxiliar, auxiliarAux);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVigencia() {
        // <CODIGO_DESARROLLADO>
        vigencia = Integer.parseInt(
                        registro.getCampos().get(cVigencia).toString());

        registro.getCampos().put(cRubro, "");
        nombreRubro = null;

        cargarListaRubro();
        cargarListaSubrubroinversiondet();
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

        // Nuevo Registro
        if (css == null) {
            nombreRubro = null;
        }
        else {
            vigencia = Integer.parseInt(
                            registro.getCampos().get(cVigencia).toString());

            codRubro = registro.getCampos().get("RUBRO").toString();

            cargarListaRubro();
        }
        // </CODIGO_DESARROLLADO>
    }
    
    
    public void generarReporte() {
    	try {
        HashMap<String, Object> reemplazos = new HashMap<>();
        
        reemplazos.put("codRubro", codRubro);
        reemplazos.put("vigencia", vigencia);
      
        String sql = Reporteador.resuelveConsulta(
                "800377RubrosInversion",
                Integer.parseInt(SessionUtil.getModulo()),
                reemplazos);
        
      
			archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
			        ConectorPool.ESQUEMA_SYSMAN, 
			        FORMATOS.EXCEL,"800377RubrosInversion");
		} catch (JRException | IOException | SQLException | DRException | SysmanException e) {

		}
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove(cNombre);

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

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * DgMensajeSecundario (DG203) en la vista.
     */
    public void aceptarDgMensajeSecundario() {
        // <CODIGO_DESARROLLADO>
        verMensajeSecundario = false;

        borrarRubros();

        registro.getCampos().put(cRubro, codRubro);
        registro.getCampos().remove(cNombre);

        agregarRegistroNuevo(false);

        cargarListaSubrubroinversiondet();

        return;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * DialogoMensajeConfirmar (DG202) en la vista.
     */
    public void aceptarDialogoMensajeConfirmar() {
        // <CODIGO_DESARROLLADO>
        verMensajeConfirmar = false;

        mensajeSecundario = idioma.getString("TB_TB3728")
                        .replace("#VIGENCIA#", Integer.toString(vigencia));

        verMensajeSecundario = true;

        return;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * DgMensajeSecundario (DG203) en la vista.
     */
    public void cancelarDgMensajeSecundario() {
        // <CODIGO_DESARROLLADO>
        verMensajeSecundario = false;
        return;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * DialogoMensajeConfirmar (DG202) en la vista.
     */
    public void cancelarDialogoMensajeConfirmar() {
        // <CODIGO_DESARROLLADO>
        verMensajeConfirmar = false;
        return;
        // </CODIGO_DESARROLLADO>
    }

    public void borrarRubros() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cVigencia, vigencia);
        param.put(cRubroInver, registro.getCampos().get(cRubro));

        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmrubroinversionsControladorUrlEnum.URL0006
                                                            .getValue());

            requestManager.delete(urlDelete.getUrl(), param);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * <li>Al insertar valida que el rubro de la vigencia seleccionada
     * no este asociado a una vigencia que ya tenga un rubro de
     * inversion.
     */
    private void validarRubro() {
        boolean configurar = false;

        try {
            configurar = ejbBancoProyectoCuatro.validarRubroInversion(compania,
                            vigencia, codRubro, accion.toUpperCase());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (configurar) {
            mensajeConfirmar = idioma.getString("TB_TB3722")
                            .replace("#CODRUBRO#", codRubro)
                            .replace("#ANO#", Integer.toString(vigencia));

            verMensajeConfirmar = true;
        }
    }

    /**
     * Metodo que ejecuta el proceso para ingresar los rubros del plan
     * presupuestal al rubro de inversion.
     */
    private void ingresarRubros() {
        try {
            ejbBancoProyectoCuatro.ingresarRubros(compania, codRubro, vigencia,
                            usuario);

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public List<Registro> getListaVigencia() {
        return listaVigencia;
    }

    public void setListaVigencia(List<Registro> listaVigencia) {
        this.listaVigencia = listaVigencia;
    }

    public RegistroDataModelImpl getListaSubrubroinversiondet() {
        return listaSubrubroinversiondet;
    }

    public void setListaSubrubroinversiondet(
        RegistroDataModelImpl listaSubrubroinversiondet) {
        this.listaSubrubroinversiondet = listaSubrubroinversiondet;
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

    public boolean isBoton() {
        return boton;
    }

    public void setBoton(boolean boton) {
        this.boton = boton;
    }

    public String getAuxiliarRubro() {
        return auxiliarRubro;
    }

    public void setAuxiliarRubro(String auxiliarRubro) {
        this.auxiliarRubro = auxiliarRubro;
    }

    public String getAuxiliarVigencia() {
        return auxiliarVigencia;
    }

    public void setAuxiliarVigencia(String auxiliarVigencia) {
        this.auxiliarVigencia = auxiliarVigencia;
    }

    public RegistroDataModelImpl getListaRubroSub() {
        return listaRubroSub;
    }

    public void setListaRubroSub(RegistroDataModelImpl listaRubroSub) {
        this.listaRubroSub = listaRubroSub;
    }

    public RegistroDataModelImpl getListaRubroSubE() {
        return listaRubroSubE;
    }

    public void setListaRubroSubE(RegistroDataModelImpl listaRubroSubE) {
        this.listaRubroSubE = listaRubroSubE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getAuxiliarAux() {
        return auxiliarAux;
    }

    public void setAuxiliarAux(String auxiliarAux) {
        this.auxiliarAux = auxiliarAux;
    }

    public RegistroDataModelImpl getListaRubro() {
        return listaRubro;
    }

    public void setListaRubro(RegistroDataModelImpl listaRubro) {
        this.listaRubro = listaRubro;
    }

    public boolean getBloqueaRubro() {
        return bloqueaRubro;
    }

    public void setBloqueaRubro(boolean bloqueaRubro) {
        this.bloqueaRubro = bloqueaRubro;
    }

    public boolean isVerMensajeConfirmar() {
        return verMensajeConfirmar;
    }

    public void setVerMensajeConfirmar(boolean verMensajeConfirmar) {
        this.verMensajeConfirmar = verMensajeConfirmar;
    }

    public String getMensajeConfirmar() {
        return mensajeConfirmar;
    }

    public void setMensajeConfirmar(String mensajeConfirmar) {
        this.mensajeConfirmar = mensajeConfirmar;
    }

    public boolean isVerMensajeSecundario() {
        return verMensajeSecundario;
    }

    public void setVerMensajeSecundario(boolean verMensajeSecundario) {
        this.verMensajeSecundario = verMensajeSecundario;
    }

    public String getMensajeSecundario() {
        return mensajeSecundario;
    }

    public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setMensajeSecundario(String mensajeSecundario) {
        this.mensajeSecundario = mensajeSecundario;
    }

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}    
    
}