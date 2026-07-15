package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCincoRemote;
import com.sysman.bancoproyectos.enums.SubbpplanindicativometasControladorEnum;
import com.sysman.bancoproyectos.enums.SubbpplanindicativometasControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.FormContinuoService;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
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
 * @author jrodrigueza
 * @version 1, 22/09/2015
 * 
 * @modifier amonroy
 * @version 2, 28/09/2017 Se realiza el Proceso de Refactory e implementacion de
 *          EJBs para las funciones y procedimientos que son llamadas en el
 *          controlador
 */
@ManagedBean
@ViewScoped
public class SubbpplanindicativometasControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String moduloBancos;
    private final String cCantidadProgramada;
    private final String cPonderacionMeta;
    private final String cVigenciaMeta;
    /**
     * Constante definida por el numero de veces que se realiza el llamado al campo
     * DEPENDENCIA en el formulario, almacena el texto DEPENDENCIA el cual es un
     * campo del registro
     */
    private final String cDependencia;
    /**
     * Constante definida por el numero de veces que se realiza el llamado al campo
     * META_BRUTA en el formulario, almacena el texto META_BRUTA el cual es un campo
     * del registro
     */
    private final String cMetaBruta;
    /**
     * Constante definida por el numero de veces que se realiza el llamado al campo
     * DESCRIPCION_TEXTO_BP en el formulario, almacena el texto DESCRIPCION_TEXTO_BP
     * el cual es un campo del registro
     */
    private final String cDescripcionTextoBp;
    /**
     * Constante definida por el numero de veces que se realiza el llamado al campo
     * TIPO_META en el formulario, almacena el texto TIPO_META el cual es un campo
     * del registro
     */
    private final String cTipoMeta;
    /**
     * Constante definida por el numero de veces que se realiza el llamado al campo
     * META en el formulario, almacena el texto META el cual es un campo del
     * registro
     */
    private final String cMeta;
    /**
     * Constante definida por el numero de veces que se realiza el llamado al campo
     * LB en el formulario
     */
    private final String cLb;
    /**
     * Constante definida por el numero de veces que se realiza el llamado al campo
     * CANT_PROG en el formulario
     */
    private final String cCantProg;
    /**
     * Constante definida por el numero de veces que se realiza el llamado al campo
     * DESCRIPCION_INDICADOR en el formulario
     */
    private final String cDescripcionIndicador;
    /**
     * Constante definida por el numero de veces que se realiza el llamado al campo
     * VIGENCIA_FINAL en el formulario
     */
    private final String cVigenciaFinal;
    /**
     * Constante definida por el numero de veces que se realiza el llamado al campo
     * CANTIDAD_EJECUTADA en el formulario
     */
    private final String cCantidadEjecutada;
    /**
     * Constante definida por el numero de veces que se realiza el llamado al campo
     * VALOR_EJECUTADO_META en el formulario
     */
    private final String cValorEjecutadoMeta;
    /**
     * Constante definida por el numero de veces que se realiza el llamado al
     * reemplazo #$codigoMeta#$ en el formulario
     */
    private final String rCodigoMeta;

    private int indice;
    private boolean dependiente;
    private String vigencia;
    private String codigoMeta;
    private RegistroDataModelImpl listaMetaProducto;
    private String auxiliar;
    private List<Registro> listaVigencia;
    private List<Registro> listaCodigoFut;
    private List<Registro> listaVigenciaMeta;
    private String cantProgramar;
    private String tipoMeta;
    private String descripcionMeta;
    private String lineaBase;
    private String valorMeta;
    private String dependencia;
    private String descripcionIndicador;
    private int vigenciaFinal;
    private int vigenciaMeta;
    private double cantidadAnterior;
    private double ponderacionAnterior;
    private String totalPonderacion;
    private String totalMetaBruta;
    private String totalCantidadProgramada;
    private String totalCantidadEjecutada;
    private String totalValorProgramado;
    private String totalValorProgOtros;
    private String totalValorEjecutado;
    private Map<String, Object> rid;
    private boolean revisado;
    private boolean actualizar;
    /**
     * Implementacion del EJB de SysmanUtil para obtener el valor de un parametro
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Implementacion del EJB de EjbBancoProyectoCincoRemote para hacer el llamado a
     * las funciones y procedimientos que se invocan dentro del Controlador y se
     * encuentran almacenadas en el paquete PCK_BANCOS_PROY5
     */
    @EJB
    private EjbBancoProyectoCincoRemote ejbBancoProyectoCinco;

    /**
     * Crea una nueva instancia de SubbpplanindicativometasControlador
     */
    public SubbpplanindicativometasControlador() {
        super();
        compania = SessionUtil.getCompania();
        moduloBancos = SessionUtil.getModulo();
        cCantidadProgramada = SubbpplanindicativometasControladorEnum.CANTIDAD_PROGRAMADA.getValue();
        cPonderacionMeta = SubbpplanindicativometasControladorEnum.PONDERACION_META.getValue();
        cVigenciaMeta = SubbpplanindicativometasControladorEnum.VIGENCIA_META.getValue();
        cDependencia = GeneralParameterEnum.DEPENDENCIA.getName();
        cMetaBruta = SubbpplanindicativometasControladorEnum.META_BRUTA.getValue();
        cDescripcionTextoBp = SubbpplanindicativometasControladorEnum.DESCRIPCION_TEXTO_BP.getValue();
        cTipoMeta = SubbpplanindicativometasControladorEnum.TIPO_META.getValue();
        cMeta = SubbpplanindicativometasControladorEnum.META.getValue();
        cLb = SubbpplanindicativometasControladorEnum.LB.getValue();
        cCantProg = SubbpplanindicativometasControladorEnum.CANT_PROG.getValue();
        cDescripcionIndicador = SubbpplanindicativometasControladorEnum.DESCRIPCION_INDICADOR.getValue();
        cVigenciaFinal = SubbpplanindicativometasControladorEnum.VIGENCIA_FINAL.getValue();
        cCantidadEjecutada = SubbpplanindicativometasControladorEnum.CANTIDAD_EJECUTADA.getValue();
        cValorEjecutadoMeta = SubbpplanindicativometasControladorEnum.VALOR_EJECUTADO_META.getValue();
        rCodigoMeta = "#$codigoMeta#$";
        rid = new HashMap<>();
        try {
            // 234
            numFormulario = GeneralCodigoFormaEnum.SUBBPPLANINDICATIVOMETAS_CONTROLADOR.getCodigo();
            validarPermisos();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                vigencia = (String) parametrosEntrada.get("vigencia");
                codigoMeta = (String) parametrosEntrada.get("codigoMeta");
                dependiente = Boolean.valueOf((String) parametrosEntrada.get("dependiente"));
                rid = (HashMap<String, Object>) parametrosEntrada.get("rid");
            }
        } catch (Exception ex) {
            Logger.getLogger(SubbpplanindicativometasControlador.class.getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        } finally {
            /**
             * No se limpia los parametros del Flash para regresar al formulario que lo
             * invoca (bpplanindicativo).
             */
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.BP_PLAN_INDICATIVO_METAS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        revisado = true;
        cargarVigencia();
        cargarListaVigencia();
        cargarListaMetaProducto();
        cargarListaVigenciaMeta();
        cargarListaCodigoFut();
        cargarDatosMeta();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(), codigoMeta);
    }

    public void cargarListaVigencia() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaVigencia = RegistroConverter
                    .toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                            SubbpplanindicativometasControladorUrlEnum.URL5845.getValue())
                                    .getUrl(),
                            param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigoFut() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(), vigenciaMeta);

        try {
            listaCodigoFut = RegistroConverter
                    .toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                            SubbpplanindicativometasControladorUrlEnum.URL6268.getValue())
                                    .getUrl(),
                            param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaVigenciaMeta() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(SubbpplanindicativometasControladorEnum.ANOINICIAL.getValue(), vigencia);
        param.put(SubbpplanindicativometasControladorEnum.ANIOFINAL.getValue(), vigenciaFinal);

        try {
            listaVigenciaMeta = RegistroConverter
                    .toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                            SubbpplanindicativometasControladorUrlEnum.URL6708.getValue())
                                    .getUrl(),
                            param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMetaProducto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(SubbpplanindicativometasControladorUrlEnum.URL7145.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);

        listaMetaProducto = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                "ID");
    }

    public void oprimirBtnAgregarFuente(Registro reg, int indice) {
        String[] campos = { "idPlan", "vigenciaPlan", "vigenciaMeta" };
        String vigMeta = reg.getCampos().get(cVigenciaMeta).toString();
        String[] valores = { codigoMeta, vigencia, vigMeta };
        SessionUtil.cargarModalDatosFlash(
                String.valueOf(GeneralCodigoFormaEnum.FRMVALORFUENTES_CONTROLADOR.getCodigo()), moduloBancos, campos,
                valores);// 239
    }

    /**
     * Luego de seleccionar la vigencia actualiza las listas y el origen de datos.
     */
    public void cambiarVigencia() {
        codigoMeta = descripcionMeta = lineaBase = valorMeta = cantProgramar = null;
        cargarListaMetaProducto();
        cargado = false;
        cargarDatos();
        calcularTotales();
    }

    /**
     * Luego de seleccionar la meta de producto verifica que se haya seleccionado
     * una vigencia, actualiza el origen de datos y actualiza los datos b�sicos que
     * se visualizan de la meta.
     *
     * @param event
     */
    public void seleccionarFilaMetaProducto(SelectEvent event) {
        if (vigencia == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2446"));
            return;
        }
        Registro registroAux = (Registro) event.getObject();
        codigoMeta = retornarString(registroAux, "ID");
        if ("".equals(codigoMeta)) {
            return;
        }
        descripcionMeta = retornarString(registroAux, cDescripcionTextoBp);
        tipoMeta = retornarString(registroAux, cTipoMeta) + ":";
        valorMeta = retornarString(registroAux, cMeta);
        lineaBase = retornarString(registroAux, cLb);
        cantProgramar = retornarString(registroAux, cCantProg);
        dependencia = retornarString(registroAux, cDependencia);
        descripcionIndicador = retornarString(registroAux, cDescripcionIndicador);
        vigenciaFinal = Integer.parseInt(retornarString(registroAux, cVigenciaFinal));

        cargarDatos();
        cargarForma();
        cargarListaVigenciaMeta();
        revisado = true;
        cargado = false;
    }

    private void cargarDatos() {
        cargado = false;
        reasignarOrigen();
        if (codigoMeta != null) {
            registro.getCampos().put(cCantidadEjecutada, 0);
            registro.getCampos().put(cValorEjecutadoMeta, 0);
            cargarDatosMeta();
            calcularTotales();
            cargarListaVigenciaMeta();
        }
    }

    @Override
    public void abrirFormulario() {
        tipoMeta = SysmanFunciones.validarVariableVacio(tipoMeta) ? idioma.getString("TB_TB3658") : tipoMeta;
        vigenciaFinal = 0;
        cantidadAnterior = 0;
        ponderacionAnterior = 0;
        vigenciaMeta = 0;
        cargarDatos();
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    /**
     * Verifica en la base de datos si ya se existe una programacion de la meta para
     * la vigencia seleccionada.
     *
     * @return true si encuentra registros; false si no hay registros
     */
    private boolean existeProgramacion() {
        boolean respuesta = false;

        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.CODIGO.getName(), codigoMeta);
        params.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
        params.put(SubbpplanindicativometasControladorEnum.VIGENCIAMETA.getValue(), vigenciaMeta);

        try {
            Registro rs = RegistroConverter
                    .toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                            SubbpplanindicativometasControladorUrlEnum.URL001.getValue())
                                    .getUrl(),
                            params));
            respuesta = rs != null ? true : false;
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return respuesta;
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        if (codigoMeta == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2447"));
            return false;
        } else if (existeProgramacion()) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2448")
                    .replace("#$vigenciaMeta#$", String.valueOf(vigenciaMeta)).replace(rCodigoMeta, codigoMeta));
            return false;
        } else {
            return actualizarAntes();
        }
    }

    @Override
    public boolean insertarDespues() {
        calcularTotales();
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        if (isRevisado()) {
            registro.getCampos().put(SubbpplanindicativometasControladorEnum.VIGENCIA_PLAN.getValue(), vigencia);
            registro.getCampos().put(SubbpplanindicativometasControladorEnum.ID_PLAN.getValue(), codigoMeta);
            registro.getCampos().put(GeneralParameterEnum.DESCRIPCION.getName(), descripcionIndicador);
            String descripcionDos = SysmanFunciones.concatenar(descripcionIndicador, " (",
                    SysmanFunciones.nvl(registro.getCampos().get(cVigenciaMeta), "").toString(), ")");
            registro.getCampos().put(SubbpplanindicativometasControladorEnum.DESCRIPCION2.getValue(), descripcionDos);
            registro.getCampos().put(cDependencia, dependencia);

            return true;
        } else {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2449"));
            return false;
        }

    }

    @Override
    public boolean actualizarDespues() {
        actualizarMetaBruta(vigenciaMeta);
        cargarDatos();
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
        actualizar = true;
        actualizarMetaBruta(Integer.parseInt(vigencia));
        calcularTotales();
        return true;
    }

    /**
     * Trae la vigencia gubernamental actual que esta configurada en la base de
     * datos.
     */
    private void cargarVigencia() {

        try {
            vigencia = ejbSysmanUtil.consultarParametro(compania, "VIGENCIA GUBERNAMENTAL ACTUAL", moduloBancos,
                    new Date(), true);
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Trae los datos baicos de la meta.
     *
     */
    private void cargarDatosMeta() {
        if (codigoMeta == null) {
            return;
        }
        try {
            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
            params.put(GeneralParameterEnum.CODIGO.getName(), codigoMeta);

            Registro rs = RegistroConverter
                    .toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                            SubbpplanindicativometasControladorUrlEnum.URL005.getValue())
                                    .getUrl(),
                            params));

            if (rs != null) {
                descripcionMeta = retornarString(rs, cDescripcionTextoBp);
                tipoMeta = retornarString(rs, cTipoMeta) + ":";
                valorMeta = retornarString(rs, cMeta);
                lineaBase = retornarString(rs, cLb);
                cantProgramar = retornarString(rs, cCantProg);
                dependencia = retornarString(rs, cDependencia);
                descripcionIndicador = retornarString(rs, cDescripcionIndicador);
                vigenciaFinal = retornarEntero(rs, cVigenciaFinal);
            }

        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public double cantNueva(int rowNum) {
        double cantidadNueva;
        if (rowNum >= 0) {
            cantidadNueva = retornarDoble(listaInicial.getDatasource().get(rowNum % 10), cCantidadProgramada);
        } else {
            cantidadNueva = retornarDoble(registro, cCantidadProgramada);
        }
        return cantidadNueva;
    }

    public void valProgramar(int rowNum) {
        if (rowNum >= 0) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(cCantidadProgramada, cantidadAnterior);
        } else {
            registro.getCampos().put(cCantidadProgramada, cantidadAnterior);
        }

    }

    public void ponderacionMeta(int rowNum, double valor) {
        if (rowNum >= 0) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(cPonderacionMeta, valor);
        } else {
            registro.getCampos().put(cPonderacionMeta, valor);
        }
    }

    public void cantidadProgramada(int rowNum, double valor) {
        if (rowNum >= 0) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(cCantidadProgramada, valor);
        } else {
            registro.getCampos().put(cCantidadProgramada, valor);
        }
    }

    public void pondNuevaA(int rowNum) {
        if (rowNum >= 0) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(cPonderacionMeta, ponderacionAnterior);
        } else {
            registro.getCampos().put(cPonderacionMeta, ponderacionAnterior);
        }
    }

    public void validarCantProgramada(int rowNum, double valor, double valorAProgramar) {
        if (valor > valorAProgramar) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2454"));
            valProgramar(rowNum);
            revisado = false;
            return;
        }
    }

    public void validarPondProgramada(int rowNum, double valor) {
        if (valor > 1) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2455"));
            pondNuevaA(rowNum);
            revisado = false;
            return;
        }
    }

    public double pondNueva(int rowNum) {
        double ponderacionNueva;
        if (rowNum >= 0) {
            ponderacionNueva = retornarDoble(listaInicial.getDatasource().get(rowNum % 10), cPonderacionMeta);
        } else {
            ponderacionNueva = retornarDoble(registro, cPonderacionMeta);
        }

        return ponderacionNueva;
    }

    /**
     * Verifica que los valores ingresados en el porcentaje de ponderacion y la
     * cantidad programada.
     *
     * @param campo  P: para el campo PONDERACION_META, C: para el campo
     *               CANTIDAD_PROGRAMADA
     * @param rowNum numero de registro en el formulario continuo, el cual debe
     *               tener valores mayores que cero.
     */
    public void reprogramar(String campo, int rowNum) {
        if (codigoMeta == null) {
            return;
        }
        revisado = true;

        try {
            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(GeneralParameterEnum.CODIGO.getName(), codigoMeta);
            params.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);

            Registro rs = RegistroConverter
                    .toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                            SubbpplanindicativometasControladorUrlEnum.URL003.getValue())
                                    .getUrl(),
                            params));

            if (rs != null) {

                double valorAProgramar = Double.parseDouble(rs.getCampos().get("A_PROGRAMAR").toString());

                if (Double.doubleToRawLongBits(valorAProgramar) == 0) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2453"));
                    revisado = false;
                    return;
                }
                casosCampo(rs, campo, rowNum, valorAProgramar);
            }
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void casosCampo(Registro rs, String campo, int rowNum, double valorAProgramar) {
        
        if (tipoMeta.toUpperCase().contains(GeneralParameterEnum.MANTENIMIENTO.getName())) {
            switch(campo){
                case "C":
                validarCantProgramada(rowNum, cantNueva(rowNum), valorAProgramar);
                ponderacionMeta(rowNum, cantNueva(rowNum) / valorAProgramar);
                break;

            case "P":
                validarPondProgramada(rowNum, pondNueva(rowNum));
                cantidadProgramada(rowNum, pondNueva(rowNum) * valorAProgramar);
                break;

            default:
                break;
            }
            
        } else {
            Double cntProgramada = retornarDoble(rs, cCantidadProgramada);
            Double ponderacion = retornarDoble(rs, cPonderacionMeta);
            switch (campo) {
            case "C":
                validarCantProgramada(rowNum, (cntProgramada + cantNueva(rowNum)) - cantidadAnterior, valorAProgramar);
                ponderacionMeta(rowNum, cantNueva(rowNum) / valorAProgramar);
                break;

            case "P":
                validarPondProgramada(rowNum, (ponderacion + pondNueva(rowNum)) - ponderacionAnterior);
                cantidadProgramada(rowNum, pondNueva(rowNum) * valorAProgramar);
                break;

            default:
                break;
            }
        }
    }

    /**
     * Calcula el valor de la MetaBruta al cambiar la "Ponderacion Programada" o el
     * valor de la "Meta Programada"
     * 
     * @param rowNum Registro en el que se realiza el cambio de la ponderacion
     */
    private void generarMeta(int rowNum) {
        try {

            double metaBruta;
            BigDecimal aProgramar;

            if (rowNum >= 0) {
                aProgramar = BigDecimal
                        .valueOf(retornarDoble(listaInicial.getDatasource().get(rowNum % 10), cCantidadProgramada));
            } else {
                aProgramar = BigDecimal.valueOf(retornarDoble(registro, cCantidadProgramada));
            }

            String auxMetaBruta = ejbBancoProyectoCinco.generarMetaBruta(compania, codigoMeta,
                    Integer.parseInt(vigencia), vigenciaMeta, aProgramar);
            String resultadoMeta = auxMetaBruta.substring(0, auxMetaBruta.length() - 3);

            metaBruta = !SysmanFunciones.validarVariableVacio(resultadoMeta) ? Double.parseDouble(resultadoMeta) : 0;
            if (rowNum >= 0) {
                listaInicial.getDatasource().get(rowNum % 10).getCampos().put(cMetaBruta, metaBruta);
            } else {
                registro.getCampos().put(cMetaBruta, metaBruta);
            }

            actualizar = "SI".equals(auxMetaBruta.substring(auxMetaBruta.length() - 2)) ? true : false;

        } catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Realiza el llamado al procedimiento PCK_BANCOS_PROY5.PR_ACTUALIZARMETABRUTA
     * que ajusta el valor de "Meta Bruta" en las metas posteriores a una Meta
     * registrada o actualizada
     */
    private void actualizarMetaBruta(int vigenciaDesde) {
        if (actualizar) {
            try {
                ejbBancoProyectoCinco.actualizarMetaBruta(compania, codigoMeta, Integer.parseInt(vigencia),
                        vigenciaDesde, SessionUtil.getUser().getCodigo());
            } catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
    }

    public void activarEdicion(Registro registro) {
        BigDecimal valor = BigDecimal.valueOf(retornarDoble(registro, cCantidadProgramada));
        cantidadAnterior = valor.doubleValue();
        valor = BigDecimal.valueOf(retornarDoble(registro, cPonderacionMeta));
        ponderacionAnterior = valor.doubleValue();
        indice = listaInicial.getRowIndex();
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el formulario
     * 
     * Realiza la redireccion al formulario "Bpplanindicativo" y envia los
     * parametros necesarios paa cargar el resgistro especifico en el que se esta
     * trabajando
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        if ("52010201".equals(SessionUtil.getMenuActual())) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("vigencia", vigencia);
            parametros.put("rid", rid);
            Direccionador direccionador = new Direccionador();
            direccionador
                    .setNumForm(Integer.toString(GeneralCodigoFormaEnum.BPPLANINDICATIVOS_CONTROLADOR.getCodigo()));
            direccionador.setParametros(parametros);
            SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        } else {
            SessionUtil.redireccionarMenu();
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCantidadProgramada() {
        cantidadAnterior = 0;
        ponderacionAnterior = 0;
        reprogramar("C", -1);
        generarMeta(-1);
    }

    public void cambiarPonderacionMeta() {
        cantidadAnterior = 0;
        ponderacionAnterior = 0;
        reprogramar("P", -1);
        generarMeta(-1);
    }

    public void cambiarCantidadProgramadaC(int rowNum) {
        reprogramar("C", rowNum);
        generarMeta(rowNum);
    }

    public void cambiarPonderacionMetaC(int rowNum) {
        reprogramar("P", rowNum);
        generarMeta(rowNum);
    }

    public void cambiarVigenciaMeta() {
        vigenciaMeta = retornarEntero(registro, cVigenciaMeta);
        cargarListaCodigoFut();
        if (existeProgramacion()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3766"));
            registro.getCampos().put(cVigenciaMeta, null);
        }

    }

    public void cambiarVigenciaMetaC(int rowNum) {
        vigenciaMeta = retornarEntero(listaInicial.getDatasource().get(rowNum % 10), cVigenciaMeta);
        cargarListaCodigoFut();
    }

    /**
     * Realiza la sumatoria de los valores asignados en la programaci�n de las
     * metas.
     */
    public void calcularTotales() {
        if (vigencia == null) {
            return;
        }
        // 433014 COMPANIA VIGENCIA CODIGO
        try {
            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(GeneralParameterEnum.VIGENCIA.getName(), vigencia);
            params.put(GeneralParameterEnum.CODIGO.getName(), codigoMeta);

            Registro rs = RegistroConverter
                    .toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                            SubbpplanindicativometasControladorUrlEnum.URL004.getValue())
                                    .getUrl(),
                            params));
            Registro rsMetaBruta = RegistroConverter
                    .toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                            SubbpplanindicativometasControladorUrlEnum.URL006.getValue())
                                    .getUrl(),
                            params));

            if (rs != null && rsMetaBruta != null) {
                totalPonderacion = retornarConFormato(retornarDoble(rs, "PONDERACION_META"));
                totalMetaBruta = retornarConFormato(retornarDoble(rsMetaBruta, cMetaBruta));
                totalCantidadProgramada = retornarConFormato(retornarDoble(rs, "CANTIDAD_PROGRAMADA"));
                totalCantidadEjecutada = retornarConFormato(retornarDoble(rs, cCantidadEjecutada));
                totalValorProgramado = retornarConFormato(retornarDoble(rs, "VALOR_PROGRAMADO_META"));
                totalValorProgOtros = retornarConFormato(retornarDoble(rs, "VALOR_PROGRAMADO_META_OTROS"));
                totalValorEjecutado = retornarConFormato(retornarDoble(rs, cValorEjecutadoMeta));

            }
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String retornarConFormato(double valor) {
        return new java.text.DecimalFormat("#,##0.00").format(valor);
    }

    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo dentro del
     * registro que tambien ha sido ingresado por parametro
     * 
     * @param rs    Registro en el que se desea evaluar el campo
     * @param campo Campo que se desea consultar
     * @return Valor en cero (0) o el valor del campo
     */
    private double retornarDoble(Registro rs, String campo) {
        return SysmanFunciones.validarCampoVacio(rs.getCampos(), campo) ? 0.0
                : Double.parseDouble(rs.getCampos().get(campo).toString());
    }

    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo dentro del
     * registro que tambien ha sido ingresado por parametro
     * 
     * @param rs    Registro en el que se desea evaluar el campo
     * @param campo Campo que se desea consultar
     * @return Valor en cero (0) o el valor del campo
     */
    private int retornarEntero(Registro rs, String campo) {
        return SysmanFunciones.validarCampoVacio(rs.getCampos(), campo) ? 0
                : Integer.parseInt(rs.getCampos().get(campo).toString());
    }

    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo dentro del
     * registro que tambien ha sido ingresado por parametro
     * 
     * @param reg   Registro en el que se desea evaluar el campo
     * @param campo Campo que se desea consultar
     * @return Cadena vacia o el valor del campo
     */
    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? "" : reg.getCampos().get(campo).toString();
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    @Override
    public FormContinuoService getService() {
        return service;
    }

    @Override
    public void setService(FormContinuoService service) {

        this.service = service;
    }

    public List<Registro> getListaVigencia() {
        return listaVigencia;
    }

    public void setListaVigencia(List<Registro> listaVigencia) {
        this.listaVigencia = listaVigencia;
    }

    public List<Registro> getListaCodigoFut() {
        return listaCodigoFut;
    }

    public void setListaCodigoFut(List<Registro> listaCodigoFut) {
        this.listaCodigoFut = listaCodigoFut;
    }

    public List<Registro> getListaVigenciaMeta() {
        return listaVigenciaMeta;
    }

    public void setListaVigenciaMeta(List<Registro> listaVigenciaMeta) {
        this.listaVigenciaMeta = listaVigenciaMeta;
    }

    public RegistroDataModelImpl getListaMetaProducto() {
        return listaMetaProducto;
    }

    public void setListaMetaProducto(RegistroDataModelImpl listaMetaProducto) {
        this.listaMetaProducto = listaMetaProducto;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }

    public String getCodigoMeta() {
        return codigoMeta;
    }

    public void setCodigoMeta(String codigoMeta) {
        this.codigoMeta = codigoMeta;
    }

    public String getCantProgramar() {
        return cantProgramar;
    }

    public String getTipoMeta() {
        return tipoMeta;
    }

    public String getDescripcionMeta() {
        return descripcionMeta;
    }

    public String getLineaBase() {
        return lineaBase;
    }

    public String getValorMeta() {
        return valorMeta;
    }

    public String getTotalPonderacion() {
        return totalPonderacion;
    }

    public void setTotalPonderacion(String totalPonderacion) {
        this.totalPonderacion = totalPonderacion;
    }

    public String getTotalMetaBruta() {
        return totalMetaBruta;
    }

    public void setTotalMetaBruta(String totalMetaBruta) {
        this.totalMetaBruta = totalMetaBruta;
    }

    public String getTotalCantidadProgramada() {
        return totalCantidadProgramada;
    }

    public void setTotalCantidadProgramada(String totalCantidadProgramada) {
        this.totalCantidadProgramada = totalCantidadProgramada;
    }

    public String getTotalCantidadEjecutada() {
        return totalCantidadEjecutada;
    }

    public void setTotalCantidadEjecutada(String totalCantidadEjecutada) {
        this.totalCantidadEjecutada = totalCantidadEjecutada;
    }

    public String getTotalValorProgOtros() {
        return totalValorProgOtros;
    }

    public void setTotalValorProgOtros(String totalValorProgOtros) {
        this.totalValorProgOtros = totalValorProgOtros;
    }

    public String getTotalValorEjecutado() {
        return totalValorEjecutado;
    }

    public void setTotalValorEjecutado(String totalValorEjecutado) {
        this.totalValorEjecutado = totalValorEjecutado;
    }

    public String getTotalValorProgramado() {
        return totalValorProgramado;
    }

    public void setTotalValorProgramado(String totalValorProgramado) {
        this.totalValorProgramado = totalValorProgramado;
    }

    public boolean isDependiente() {
        return dependiente;
    }

    public void setDependiente(boolean dependiente) {
        this.dependiente = dependiente;
    }

    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    public boolean isRevisado() {
        return revisado;
    }

    public void setRevisado(boolean revisado) {
        this.revisado = revisado;
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
}