package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.BpplanindicativosControladorEnum;
import com.sysman.bancoproyectos.enums.BpplanindicativosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author jrodrigueza
 * @version 1, 15/09/2015
 * @modified jguerrero
 * @version 2. 11/09/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar.
 */
@ManagedBean
@ViewScoped

public class BpplanindicativosControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private final String dependenciaCons;
    private final String tipoMetaPlanCons;
    private List<Registro> listaVigenciaInicial;
    private List<Registro> listaVigenciaFinal;
    private List<Registro> listaUnidadMedida;
    private List<Registro> listaSector;
    private RegistroDataModelImpl listaDependencia;
    private String vigencia;
    private String nombreDependencia;
    private boolean bloqueadoTipoMeta;
    private boolean bloqueadoMetaIndicador;
    private boolean bloqueadoUnidadMedida;
    private boolean bloqueadoDependencia;
    private boolean esMeta;
    private boolean activado;
    private final String vigenciaFinalCons;
    private final String modulo;

    /**
     * Inicializaciï¿½n de variables booleanas para los controles que
     * se bloquearan dependiendo de una acciï¿½n.
     */
    @SuppressWarnings("unchecked")
    public BpplanindicativosControlador() {
        super();
        modulo = SessionUtil.getModulo();
        vigenciaFinalCons = "VIGENCIA_FINAL";
        compania = SessionUtil.getCompania();
        dependenciaCons = GeneralParameterEnum.DEPENDENCIA.getName();
        tipoMetaPlanCons = BpplanindicativosControladorEnum.TIPO_META_PLAN
                        .getValue();
        try {
            numFormulario = GeneralCodigoFormaEnum.BPPLANINDICATIVOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            registro = new Registro(new HashMap<String, Object>());
            bloqueadoTipoMeta = true;
            bloqueadoMetaIndicador = true;
            bloqueadoUnidadMedida = true;
            bloqueadoDependencia = true;
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {
                vigencia = (String) parametros
                                .get(BpplanindicativosControladorEnum.VIGENCIA_LOWER
                                                .getValue());
                rid = (HashMap<String, Object>) parametros
                                .get(BpplanindicativosControladorEnum.RID_LOWER
                                                .getValue());
            }
        }
        catch (Exception ex) {
            Logger.getLogger(BpplanindicativosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.BP_PLAN_INDICATIVO;

        buscarLlave();
        asignarOrigenDatos();

    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.VIGENCIA.getName(),
                        vigencia);

    }

    @Override
    public void iniciarListas() {
        cargarListaVigenciaInicial();
        cargarListaVigenciaFinal();
        cargarListaUnidadMedida();
        cargarListaDependencia();
        cargarListasector();
    }

    @Override
    public void iniciarListasSub() {
        // METODO NO IMPLEMENTADO
    }

    @Override
    public void iniciarListasSubNulo() {
        // METODO NO IMPLEMENTADO
    }

    public void cargarListaVigenciaInicial() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaVigenciaInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BpplanindicativosControladorUrlEnum.URL6037
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaVigenciaFinal() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaVigenciaFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BpplanindicativosControladorUrlEnum.URL6590
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaUnidadMedida() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaUnidadMedida = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BpplanindicativosControladorUrlEnum.URL7140
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // 553001
    }

    public void cargarListaDependencia() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BpplanindicativosControladorUrlEnum.URL7829
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaDependencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

        // 62005
    }

    public void cargarListasector() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaSector = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BpplanindicativosControladorUrlEnum.URL8303
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // 203001
    }
    
    public void cargarProgramar() {
    	String[] campos = { "vigenciaActual" };
		Object[] valores = { vigencia };

		SessionUtil.cargarModalDatosFlash(
				Integer.toString(
						GeneralCodigoFormaEnum.PROGRAMAR_META_PRODUCTO
						.getCodigo()),
				modulo, campos,
				valores);
    }

    @Override
    public void abrirFormulario() {
        // METODO NO IMPLEMENTADO
    }

    /**
     * Cuando la acciï¿½n es VER, bloquea los controles necesarios y
     * ademï¿½s verifica que estï¿½n configuradas los diferentes tipos
     * de metas. Cuando la acciï¿½n es INSERTAR, desbloquea los
     * controles necesarios. Cuando la acciï¿½n es MODIFICAR, verifica
     * que el nivel estï¿½ configurado correctamente.
     */
    @Override
    public void cargarRegistro() {
        precargarRegistro();

        if (css != null) {

            verificarNivel();
            validarFisicoFinanciero();
        }
        else {
            seleccionarOpcionI();
        }

        cargarNombreDependencia(retornarString(registro, dependenciaCons));
        String tipoMetaPlan = retornarString(registro, tipoMetaPlanCons);

        if (tipoMetaPlan != null) {
            activado = "002".equals(registro.getCampos().get(tipoMetaPlanCons));
        }
        else {
            activado = false;
        }
    }

    public void seleccionarOpcionV() {
        bloqueadoTipoMeta = true;
        bloqueadoMetaIndicador = true;
        bloqueadoUnidadMedida = true;
        bloqueadoDependencia = true;
        verificarNivelMetas();
    }

    public void seleccionarOpcionI() {
        registro.getCampos()
                        .put(BpplanindicativosControladorEnum.VIGENCIA_INICIAL
                                        .getValue(), vigencia);

        bloqueadoTipoMeta = true;
        bloqueadoMetaIndicador = false;
        bloqueadoUnidadMedida = false;
        bloqueadoDependencia = false;
        nombreDependencia = "";
        inicializarCampos();
    }

    /**
     * Verifica que el nivel ingresado estï¿½ configurado en el plan
     * indicativo. Configura los controles segï¿½n el tipo de meta.
     */
    public void verificarNivel() {
        try {
            String id = retornarString(registro,
                            BpplanindicativosControladorEnum.ID.getValue());
            if (id == null) {

                return;
            }

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
            param.put(BpplanindicativosControladorEnum.ID.getValue(), id);

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BpplanindicativosControladorUrlEnum.URL7141
                                                                            .getValue())
                                            .getUrl(), param));

            if (reg == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2296")
                                .replace("#$tamanio#$",
                                                String.valueOf(id.length())));
            }
            else {
                // Verifica si el nivel maneja meta
                if (Boolean.parseBoolean(
                                reg.getCampos().get(
                                                BpplanindicativosControladorEnum.META_RESUL
                                                                .getValue())
                                                .toString())) {
                    registro.getCampos().put(tipoMetaPlanCons,
                                    "001");
                    bloqueadoTipoMeta = true;
                    bloqueadoMetaIndicador = false;
                    bloqueadoUnidadMedida = false;
                    esMeta = true;
                }
                else if (Boolean.parseBoolean(reg.getCampos()
                                .get(BpplanindicativosControladorEnum.META_PRODUC
                                                .getValue())
                                .toString())) {
                    registro.getCampos().put(tipoMetaPlanCons,
                                    "002");
                    bloqueadoTipoMeta = true;
                    bloqueadoMetaIndicador = false;
                    bloqueadoUnidadMedida = false;
                    esMeta = true;
                }
                else {
                    registro.getCampos().put(tipoMetaPlanCons,
                                    null);
                    bloqueadoTipoMeta = true;
                    bloqueadoMetaIndicador = true;
                    bloqueadoUnidadMedida = true;
                    esMeta = false;
                }
                // Verifica si tiene configurada la dependencia

                bloqueadoDependencia = !Boolean.parseBoolean(
                                reg.getCampos().get(
                                                BpplanindicativosControladorEnum.MANEJA_DEPEN
                                                                .getValue())
                                                .toString());
            }
        }
        catch (SystemException e)

        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Verifica que estï¿½n configuradas los diferentes tipos de metas
     * en los niveles del plan indicativo. En caso de que no estï¿½n
     * configuradas muestra un mensaje de alerta.
     */
    private void verificarNivelMetas() {
        // Meta Resultado
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BpplanindicativosControladorUrlEnum.URL7142
                                                                            .getValue())
                                            .getUrl(), param));

            if (reg == null) {
                // Se alerta que no existe meta de resultado
                // configurado
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2305"));
            }
            // Meta Producto

            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BpplanindicativosControladorUrlEnum.URL7143
                                                                            .getValue())
                                            .getUrl(), param));

            if (reg == null) {
                // Se alerta que no existe meta de producto
                // configurado
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2303"));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirabrirProgramacion() {

        String codigo = (String) registro.getCampos()
                        .get(BpplanindicativosControladorEnum.ID.getValue());

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(BpplanindicativosControladorEnum.DEPENDIENTE_LOWER
                        .getValue(),
                        BpplanindicativosControladorEnum.TRUE_LOWER
                                        .getValue());
        parametros.put(BpplanindicativosControladorEnum.VIGENCIA_LOWER
                        .getValue(), vigencia);
        parametros.put(BpplanindicativosControladorEnum.CODIGO_META_LOWER
                        .getValue(), codigo);
        parametros.put(BpplanindicativosControladorEnum.RID_LOWER
                        .getValue(), css);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.SUBBPPLANINDICATIVOMETAS_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());

    }
    

    /**
     * Abre el formulario modal de carga de plantilla.
     * 
     *
     */
    public void oprimirCargarPlantilla() {
    	String[] campos = { "vigencia" };
    	Object[] valores = { vigencia };

    	SessionUtil.cargarModalDatosFlash(
    			Integer.toString(GeneralCodigoFormaEnum.FRM_SUBIR_PLAN_INDICATIVO_CONTROLADOR.getCodigo()),
    			modulo,
    			campos,
    			valores
    			);
    }

    /**
     * Se ejecuta al cerrar el modal de carga.
     * Recarga la lista inicial para actualizar la informacion en el formulario padre.
     */
    public void retornarFormularioCargarPlantilla(SelectEvent event) {
    	listaInicial.load();
    }

    /**
     * Metodo ejecutado al cambiar el control VigenciaFinal
     * 
     */
    public void cambiarVigenciaFinal() {
        // <CODIGO_DESARROLLADO>
        if (registro.getCampos().get(vigenciaFinalCons) != null) {
            int fechaIni = Integer.parseInt(registro.getCampos()
                            .get("VIGENCIA_INICIAL").toString());
            int fechaFin = Integer.parseInt(registro.getCampos()
                            .get(vigenciaFinalCons).toString());
            if (fechaFin < fechaIni) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3735"));
                registro.getCampos().put(vigenciaFinalCons, null);
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Luego de haber ingresado el cï¿½digo del plan realiza el
     * proceso de verificar nivel.
     *
     * @see #verificarNivel()
     */
    public void cambiarId() {
        if (existePlan()) {
            String id = (String) registro.getCampos().get(
                            BpplanindicativosControladorEnum.ID.getValue());
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2302").replace("#$id#$", id)
                                            .replace("#$vigencia#$", vigencia));
            registro.getCampos().put(
                            BpplanindicativosControladorEnum.ID.getValue(),
                            null);
            return;
        }
        verificarNivel();
    }

    public void seleccionarFilaDependencia(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(dependenciaCons,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        cargarNombreDependencia(retornarString(registro, dependenciaCons));
    }

    /**
     * Carga el nombre de la dependencia
     *
     * @param codigo
     * cï¿½digo de la dependencia
     */
    public void cargarNombreDependencia(String codigo) {
        try {
            if (codigo == null) {
                return;
            }

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CODIGO.getName(), codigo);

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BpplanindicativosControladorUrlEnum.URL7144
                                                                            .getValue())
                                            .getUrl(), param));

            if (reg != null) {

                nombreDependencia = retornarString(reg,
                                GeneralParameterEnum.NOMBRE.getName());
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Verifica en la base de datos si ya existe un plan cï¿½n el
     * cï¿½digo ingresado.
     *
     * @return true si hay registros, false si no se encontraron
     * registros
     */
    private boolean existePlan() {
        String id = (String) registro.getCampos()
                        .get(BpplanindicativosControladorEnum.ID.getValue());
        boolean rta;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
        param.put(BpplanindicativosControladorEnum.ID.getValue(), id);

        Registro reg = null;
        try {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BpplanindicativosControladorUrlEnum.URL7145
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (reg != null) {
            rta = true;
        }
        else {
            rta = false;
        }
        return rta;

    }

    /**
     * Inicializa los campos que tienen valores predeterminados.
     */
    private void inicializarCampos() {
        registro.getCampos().put(
                        BpplanindicativosControladorEnum.PONDERACION.getValue(),
                        0);
        registro.getCampos().put(
                        BpplanindicativosControladorEnum.AVANCE.getValue(),
                        new BigDecimal(BigInteger.ZERO));
        registro.getCampos()
                        .put(BpplanindicativosControladorEnum.AVANCE_FINANCIERO
                                        .getValue(),
                                        new BigDecimal(BigInteger.ZERO));
        registro.getCampos()
                        .put(BpplanindicativosControladorEnum.UNIDAD_MEDIDA
                                        .getValue(),
                                        BpplanindicativosControladorEnum.No
                                                        .getValue());
        registro.getCampos().put(
                        BpplanindicativosControladorEnum.META.getValue(), 0);
        registro.getCampos().put(BpplanindicativosControladorEnum.LB.getValue(),
                        0);
    }

    public List<Registro> getListaVigenciaInicial() {
        return listaVigenciaInicial;
    }

    public void setListaVigenciaInicial(List<Registro> listaVigenciaInicial) {
        this.listaVigenciaInicial = listaVigenciaInicial;
    }

    public List<Registro> getListaVigenciaFinal() {
        return listaVigenciaFinal;
    }

    public void setListaVigenciaFinal(List<Registro> listaVigenciaFinal) {
        this.listaVigenciaFinal = listaVigenciaFinal;
    }

    public List<Registro> getListaUnidadMedida() {
        return listaUnidadMedida;
    }

    public void setListaUnidadMedida(List<Registro> listaUnidadMedida) {
        this.listaUnidadMedida = listaUnidadMedida;
    }

    public RegistroDataModelImpl getListaDependencia() {
        return listaDependencia;
    }

    public void setListaDependencia(RegistroDataModelImpl listaDependencia) {
        this.listaDependencia = listaDependencia;
    }

    public List<Registro> getListaSector() {
        return listaSector;
    }

    public void setListaSector(List<Registro> listaSector) {
        this.listaSector = listaSector;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public boolean isBloqueadoTipoMeta() {
        return bloqueadoTipoMeta;
    }

    public boolean isBloqueadoMetaIndicador() {
        return bloqueadoMetaIndicador;
    }

    public boolean isBloqueadoUnidadMedida() {
        return bloqueadoUnidadMedida;
    }

    public boolean isBloqueadoDependencia() {
        return bloqueadoDependencia;
    }

    public String getNombreDependencia() {
        return nombreDependencia;
    }

    public boolean isActivado() {
        return activado;
    }

    public void setActivado(boolean activado) {
        this.activado = activado;
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        boolean respuesta = true;
        if (esMeta) {
            String tipoIndicador = (String) registro.getCampos()
                            .get(BpplanindicativosControladorEnum.TIPO_META_INDICADOR
                                            .getValue());
            if (tipoIndicador == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2298"));
                respuesta = false;
            }
            String dependencia = (String) registro.getCampos()
                            .get(dependenciaCons);
            if ("".equals(dependencia)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2297"));
                respuesta = false;
            }
        }
        return respuesta;
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

    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    private void validarFisicoFinanciero() {
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(), "AVANCE")) {
            registro.getCampos().put("AVANCE", 0);

        }
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        "AVANCE_FINANCIERO")) {
            registro.getCampos().put("AVANCE_FINANCIERO", 0);

        }

    }

}