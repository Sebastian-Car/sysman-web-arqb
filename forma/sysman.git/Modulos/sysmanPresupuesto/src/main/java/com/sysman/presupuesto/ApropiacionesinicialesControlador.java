package com.sysman.presupuesto;


import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
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
import com.sysman.presupuesto.ejb.EjbPresupuestoCeroRemote;
import com.sysman.presupuesto.enums.ApropiacionesinicialesControladorEnum;
import com.sysman.presupuesto.enums.ApropiacionesinicialesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

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
 * @author acaceres
 * @version 1, 16/06/2016
 * @author lcortes
 * @version 2, 24/08/2016 11:41:15 -- Modificado por lcortes
 * @author yrojas
 * @version 3, 21/04/2017 Se cambiaron las consultas por la invocacion
 * de los DSS. Se cambio controlador segun especificaciones del
 * SonarLint.
 *
 * @author jlramirez
 * @version 4, 24/04/2017, Manejo de EJBs
 *
 * -- Modificado por lcortes 02/05/2017. Se cambia el procedimiento
 * actualizar apropia
 *
 * @author eamaya
 * @version 5.0, 13/06/2017 Se cambiï¿½ el llamado del cï¿½digo del
 * formulario y actualizaciï¿½n de ConnectorPool
 *
 * -- Modificado por lcortes 09,14/06/2017. Se modifican los metodos
 * seleccionarFilaCodigo y activarEdicion para verificar las
 * condiciones de bloqueados de los campos al insertar y editar. En el
 * metodo actualizarAntes para permitir editar los campos TERCERO,
 * SUCURSAL, AUXILIAR, CENTRO_COSTO, REFERENCIA y FUENTE_RECURSO
 *
 */
@ManagedBean
@ViewScoped
public class ApropiacionesinicialesControlador
                extends BeanBaseContinuoAcmeImpl {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String nombreCuenta;
    private String anoVigencia;
    private String auxiliar;
    private String movimiento;
    private boolean bloqueaTercero;
    private boolean bloqueaCentroCosto;
    private boolean bloqueaAuxiliar;
    private boolean bloqueaReferencia;
    private boolean bloqueaFuente;
    private boolean manCenCto = true;
    private boolean manAuxTer;
    private boolean manAuxGen;
    private boolean manReferencia = true;
    private boolean manFuente = true;
    private boolean manAuxiliar = true;
    private boolean insertar;
    private String naturaleza;
    private int indice;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTercero;
    private RegistroDataModelImpl listaTerceroE;
    private RegistroDataModelImpl listaCentroCosto;
    private RegistroDataModelImpl listaCentroCostoE;
    private RegistroDataModelImpl listaAuxiliar;
    private RegistroDataModelImpl listaAuxiliarE;
    private RegistroDataModelImpl listaCodigo;
    private RegistroDataModelImpl listaCodigoE;
    private RegistroDataModelImpl listaFuente;
    private RegistroDataModelImpl listaFuenteE;
    private RegistroDataModelImpl listaReferencia;
    private RegistroDataModelImpl listaReferenciaE;
    private RegistroDataModelImpl listacodigoCCPETCuipo;
    private RegistroDataModelImpl listacodigoCCPETCuipoE;
	private RegistroDataModelImpl listaprograma;
    private RegistroDataModelImpl listaprogramaE;
    private RegistroDataModelImpl listacodigoBpin;
    private RegistroDataModelImpl listacodigoBpinE;
    private RegistroDataModelImpl listacodigoUnidadEjecutora;
    private RegistroDataModelImpl listacodigoUnidadEjecutoraE;
    private RegistroDataModelImpl listaDetalleSectorial;
    private RegistroDataModelImpl listaDetalleSectorialE;
    private RegistroDataModelImpl listaTipoRecursoSGR;
    private RegistroDataModelImpl listaTipoRecursoSGRE;
    private RegistroDataModelImpl listaSector;
    private RegistroDataModelImpl listaSectorE;
  //indican si los campos se habilitan o no, de acuerdo a la lista de clasificacion
  	private boolean campobloqueadocodigoCPC;
  	

	private boolean campobloqueadoFuenteCuipo;
  	private boolean campobloqueadoProductoCuipo;
  	private boolean campobloqueadoCodigoBPINCampo;
  	private boolean campobloqueadocodunidadejecutora = false;
	

	// indican si los campos se muestran o no de acuerdo a el modelo del perdiodo del año
	private boolean sectorComboMostar = false;
	private boolean programaMostar = true;
	private boolean subprogramaMostar = false;
	

	private boolean codigoProductoMostar = true;
	private boolean codigoBpinMostar = true;
	private boolean codigoCCPETMostar = true;
	private boolean codigoCPCDANEMostar = true;
	private boolean codigoUnidadEjecutoraMostar = true;
	private boolean codigoFuenteMostar = true;
	private boolean codigoCCPETRegaliasMostar = true;
	// indican si los campos se muestran o no de acuerdo a el modelo del perdiodo del año
	private boolean codigoCPCMostar = true;
	private boolean fuenteCuipoMostar = true;
	private boolean productoCuipoMostar = true;
	private boolean codigoCCPETCuipoMostar = true;
	private boolean codigoBpinCampoMostrar = true;
	private boolean campobloqueadoprograma = false;
	private boolean campobloqueadocodbpin = false;
	
	private boolean campobloqueadoCodigoCCPETCuipo = false;
	
	
    private Registro registroAux;
    private static final String NOMBRE_TERCERO = "NOMBRE_TERCERO";
    private static final String NOMBRE_CENTROCOSTO = "NOMBRE_CENTROCOSTO";
    private static final String MAN_AUX_GEN = "MAN_AUX_GEN";
    private static final String VALOR = "$ #,##0.00";
    private static final String APROPIACIONINICIAL = "APROPIACIONINICIAL";
    private static final String NOMBRE_AUXILIAR = "NOMBRE_AUXILIAR";
    private static final String NOMBRE_FUENTERECURSOS = "NOMBRE_FUENTERECURSOS";
    private static final String MAN_AUX_FUE = "MAN_AUX_FUE";
    private static final String NOMBRE_REFERENCIA = "NOMBRE_REFERENCIA";
    private static final String MAN_AUX_REF = "MAN_AUX_REF";
    private static final String MAN_CEN_CTO = "MAN_CEN_CTO";
    private static final String NOMBRE_CODIGO = "NOMBRE_CODIGO";
    @EJB
    private EjbPresupuestoCeroRemote presupuesto;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of ApropiacionesinicialesControlador
     */
    public ApropiacionesinicialesControlador() {
        super();
        manAuxTer = true;
        manAuxGen = true;
        insertar = false;
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.APROPIACIONESINICIALES_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                anoVigencia = (String) parametrosEntrada.get("anoVigencia");

            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ApropiacionesinicialesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        manAuxTer = true;
        manAuxGen = true;
        tabla = ApropiacionesinicialesControladorEnum.PARAM0.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTercero();
        cargarListaTerceroE();
        cargarListaCentroCosto();
        cargarListaCentroCostoE();
        cargarListaAuxiliar();
        cargarListaAuxiliarE();
        cargarListaCodigo();
        cargarListaCodigoE();
        cargarListaFuente();
        cargarListaFuenteE();
        cargarListaReferencia();
        cargarListaReferenciaE();
        cargarListacodigoCCPETCuipo();
        cargarListacodigoCCPETCuipoE();
        cargarListaprograma();
        cargarListaprogramaE();
        cargarListacodigoBpin();
        cargarListacodigoBpinE();
        cargarListacodigoUnidadEjecutora();
        cargarListacodigoUnidadEjecutoraE();
        cargarListaTipoRecursoSGR();
        cargarListaTipoRecursoSGRE();
        cargarListaSector();
        cargarListaSectorE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionesinicialesControladorUrlEnum.URL11959
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionesinicialesControladorUrlEnum.URL12000
                                                        .getValue());
        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionesinicialesControladorUrlEnum.URL12003
                                                        .getValue());
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionesinicialesControladorUrlEnum.URL12012
                                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anoVigencia);

    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTercero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionesinicialesControladorUrlEnum.URL10742
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NIT");
    }

    public void cargarListaTerceroE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionesinicialesControladorUrlEnum.URL11370
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaTerceroE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "NIT");
    }

    public void cargarListaCentroCosto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionesinicialesControladorUrlEnum.URL12002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);

        listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCentroCostoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionesinicialesControladorUrlEnum.URL12983
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);

        listaCentroCostoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaAuxiliar() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionesinicialesControladorUrlEnum.URL13959
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);

        listaAuxiliar = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaAuxiliarE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionesinicialesControladorUrlEnum.URL14862
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);

        listaAuxiliarE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCodigo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionesinicialesControladorUrlEnum.URL15763
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);

        listaCodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCodigoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionesinicialesControladorUrlEnum.URL17439
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);

        listaCodigoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaFuente() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionesinicialesControladorUrlEnum.URL19105
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);

        listaFuente = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaFuenteE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionesinicialesControladorUrlEnum.URL19968
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);

        listaFuenteE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaReferencia() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionesinicialesControladorUrlEnum.URL20835
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);

        listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaReferenciaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ApropiacionesinicialesControladorUrlEnum.URL21893
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);

        listaReferenciaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }
    
    /**
     * 
     * Carga la lista listaDetalleSectorial
     *
     */
    public void cargarListaDetalleSectorial(){

    	UrlBean urlBean = UrlServiceUtil.getInstance()
    			.getUrlServiceByUrlByEnumID(
    					ApropiacionesinicialesControladorUrlEnum.URL45117
    					.getValue());
    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	param.put(GeneralParameterEnum.ANIO.getName(), anoVigencia);
    	param.put(GeneralParameterEnum.VALOR.getName(), naturaleza.equals("D")?"-1":"0");
    	

    	listaDetalleSectorial = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param, true,
    			GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * 
     * Carga la lista listaDetalleSectorial
     *
     */
    public void  cargarListaDetalleSectorialE(){

    	UrlBean urlBean = UrlServiceUtil.getInstance()
    	.getUrlServiceByUrlByEnumID(
    			ApropiacionesinicialesControladorUrlEnum.URL45117
    			.getValue());
    Map<String, Object> param = new TreeMap<>();
    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    param.put(GeneralParameterEnum.ANIO.getName(), anoVigencia);
    param.put(GeneralParameterEnum.VALOR.getName(), naturaleza.equals("D")?"-1":"0");


    listaDetalleSectorialE = new RegistroDataModelImpl(urlBean.getUrl(),
    		urlBean.getUrlConteo().getUrl(), param, true,
    		GeneralParameterEnum.CODIGO.getName());

    }



    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>

    // <METODOS_COMBOS_GRANDES>
    public void onRowSelectTercero(SelectEvent event) {
        Registro registroAux1 = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        registroAux1.getCampos().get("NIT"));
        registro.getCampos().put(NOMBRE_TERCERO, registroAux1.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux1.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));

    }

    public void onRowSelectTerceroE(SelectEvent event) {
        Registro registroAux1 = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux1.getCampos().get("NIT"), "")
                        .toString();
        registro.getCampos().put(NOMBRE_TERCERO, registroAux1.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux1.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));

    }

    public void cambiarTerceroC(int rowNum) {

        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        NOMBRE_TERCERO,
                        registro.getCampos().get(NOMBRE_TERCERO));

    }

    public void seleccionarFilaCentroCosto(SelectEvent event) {

        if (manCenCto) {
            Registro registroAux1 = (Registro) event.getObject();
            if ((boolean) registroAux1.getCampos()
                            .get(GeneralParameterEnum.MOVIMIENTO.getName())) {
                registro.getCampos().put(
                                GeneralParameterEnum.CENTRO_COSTO.getName(),
                                registroAux1.getCampos()
                                                .get(GeneralParameterEnum.CODIGO
                                                                .getName()));
                registro.getCampos().put(NOMBRE_CENTROCOSTO,
                                registroAux1.getCampos()
                                                .get(GeneralParameterEnum.NOMBRE
                                                                .getName()));

            }
            else {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB326"));
            }
        }
        else {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB327"));
        }

    }

    public void seleccionarFilaCentroCostoE(SelectEvent event) {
        Registro registroAux1 = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux1.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "")
                        .toString();
        registro.getCampos().put(NOMBRE_CENTROCOSTO,
                        registroAux1.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    public void cambiarCentroCostoC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        NOMBRE_CENTROCOSTO,
                        registro.getCampos().get(NOMBRE_CENTROCOSTO));

    }

    public void seleccionarFilaAuxiliar(SelectEvent event) {
        Registro registroAux1 = (Registro) event.getObject();
        if ((boolean) registroAux1.getCampos()
                        .get(GeneralParameterEnum.MOVIMIENTO.getName())) {
            registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                            registroAux1.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName()));
            registro.getCampos().put(NOMBRE_AUXILIAR,
                            registroAux1.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName()));
        }
        else {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB328"));
        }

    }

    public void seleccionarFilaAuxiliarE(SelectEvent event) {
        Registro registroAux1 = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux1.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "")
                        .toString();
        listaInicial.getDatasource().get(indice).getCampos().put(
                        NOMBRE_AUXILIAR,
                        registroAux1.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    public void cambiarAuxiliarC(int rowNum) {
        // heredado del bean base

    }

    public void seleccionarFilaCodigo(SelectEvent event) {
        bloqueaFuente = bloqueaAuxiliar = bloqueaCentroCosto = bloqueaReferencia = bloqueaTercero = true;
        registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(NOMBRE_CODIGO, registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));
        naturaleza = registroAux.getCampos()
                        .get(GeneralParameterEnum.NATURALEZA.getName())
                        .toString();
        registro.getCampos().put(GeneralParameterEnum.NATURALEZA.getName(),
                        naturaleza);

        try {
            Map<String, Object> parametros = new HashMap<>();
            if ((Boolean) registroAux.getCampos().get(MAN_AUX_GEN)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB330"));
                bloqueaAuxiliar = false;
            }
            else {
                registro.getCampos().put(
                                GeneralParameterEnum.AUXILIAR.getName(),
                                SysmanConstantes.CONS_AUXILIAR);
                parametros.put(GeneralParameterEnum.CODIGO.getName(),
                                SysmanConstantes.CONS_AUXILIAR);
                Registro auxa = listaAuxiliar.getRegistroUnico(parametros);
                registro.getCampos().put(NOMBRE_AUXILIAR, auxa.getCampos()
                                .get(GeneralParameterEnum.NOMBRE.getName()));
            }
            if ((Boolean) registroAux.getCampos().get(MAN_CEN_CTO)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB331"));
                bloqueaCentroCosto = false;
            }
            else {
                registro.getCampos()
                                .put(GeneralParameterEnum.CENTRO_COSTO
                                                .getName(),
                                                SysmanConstantes.CONS_CENTRO);
                parametros.put(GeneralParameterEnum.CODIGO.getName(),
                                SysmanConstantes.CONS_CENTRO);
                Registro auxc = listaCentroCosto.getRegistroUnico(parametros);
                registro.getCampos().put(NOMBRE_CENTROCOSTO,
                                auxc.getCampos().get(GeneralParameterEnum.NOMBRE
                                                .getName()));
            }

            if ((Boolean) registroAux.getCampos().get(MAN_AUX_REF)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB332"));
                bloqueaReferencia = false;
            }
            else {
                registro.getCampos()
                                .put(GeneralParameterEnum.REFERENCIA
                                                .getName(),
                                                SysmanConstantes.CONS_REFERENCIA);
                parametros.put(GeneralParameterEnum.CODIGO.getName(),
                                SysmanConstantes.CONS_REFERENCIA);
                Registro auxr = listaReferencia.getRegistroUnico(parametros);
                registro.getCampos().put(NOMBRE_REFERENCIA,
                                auxr.getCampos().get(GeneralParameterEnum.NOMBRE
                                                .getName()));
            }

            if ((Boolean) registroAux.getCampos().get(MAN_AUX_FUE)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB333"));
                bloqueaFuente = false;
            }
            else {
                registro.getCampos()
                                .put(GeneralParameterEnum.FUENTE_RECURSO
                                                .getName(),
                                                SysmanConstantes.CONS_FUENTE);
                parametros.put(GeneralParameterEnum.CODIGO.getName(),
                                SysmanConstantes.CONS_FUENTE);
                Registro auxf;
                auxf = listaFuente.getRegistroUnico(parametros);
                registro.getCampos().put(NOMBRE_FUENTERECURSOS,
                                auxf.getCampos().get(GeneralParameterEnum.NOMBRE
                                                .getName()));
            }
            cargarListaDetalleSectorial();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
    }

    public void seleccionarFilaCodigoE(SelectEvent event) {
        registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(),
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(NOMBRE_CODIGO, registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()));
        naturaleza = registroAux.getCampos()
                        .get(GeneralParameterEnum.NATURALEZA.getName())
                        .toString();

        if ((Boolean) registroAux.getCampos().get(MAN_AUX_GEN)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB335"));
            bloqueaAuxiliar = false;
        }
        else {
            registro.getCampos().put(GeneralParameterEnum.AUXILIAR.getName(),
                            SysmanConstantes.CONS_AUXILIAR);
        }
        if ((Boolean) registroAux.getCampos().get(MAN_CEN_CTO)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB336"));
            bloqueaCentroCosto = false;
        }
        else {
            registro.getCampos().put(
                            GeneralParameterEnum.CENTRO_COSTO.getName(),
                            SysmanConstantes.CONS_CENTRO);
        }

        if ((Boolean) registroAux.getCampos().get(MAN_AUX_REF)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB338"));
            bloqueaReferencia = false;
        }
        else {
            registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
                            SysmanConstantes.CONS_REFERENCIA);
        }

        if ((Boolean) registroAux.getCampos().get(MAN_AUX_FUE)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB339"));
            bloqueaFuente = false;
        }
        else {
            registro.getCampos().put(
                            GeneralParameterEnum.FUENTE_RECURSO.getName(),
                            SysmanConstantes.CONS_FUENTE);
        }
    }
    
    public void seleccionarFilaDetalleSectorial(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	        registro.getCampos().put("DETALLESECTORIAL", registroAux.getCampos().get("CODIGO"));
    	}

    public void seleccionarFilaDetalleSectorialE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	       auxiliar =   SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
    	}

    public void cambiarCodigoC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                        NOMBRE_CODIGO,
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

    }

    public void seleccionarFilaFuente(SelectEvent event) {
        Registro registroAux1 = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.FUENTE_RECURSO.getName(),
                        registroAux1.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos().put(NOMBRE_FUENTERECURSOS,
                        registroAux1.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));

    }

    public void seleccionarFilaFuenteE(SelectEvent event) {
        Registro registroAux1 = (Registro) event.getObject();
        auxiliar = registroAux1.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        listaInicial.getDatasource().get(indice).getCampos().put(
                        NOMBRE_FUENTERECURSOS,
                        registroAux1.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    public void cambiarFuenteC(int rowNum) {
        // heredado del bean base
    }

    public void seleccionarFilaReferencia(SelectEvent event) {
        Registro registroAux1 = (Registro) event.getObject();
        if ((boolean) registroAux1.getCampos()
                        .get(GeneralParameterEnum.MOVIMIENTO.getName())) {
            registro.getCampos().put(GeneralParameterEnum.REFERENCIA.getName(),
                            registroAux1.getCampos()
                                            .get(GeneralParameterEnum.CODIGO
                                                            .getName()));
            registro.getCampos().put(NOMBRE_REFERENCIA,
                            registroAux1.getCampos()
                                            .get(GeneralParameterEnum.NOMBRE
                                                            .getName()));

        }
        else {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB340"));
        }

    }

    public void seleccionarFilaReferenciaE(SelectEvent event) {
        Registro registroAux1 = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(
                        registroAux1.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "")
                        .toString();
        listaInicial.getDatasource().get(indice).getCampos().put(
                        NOMBRE_REFERENCIA,
                        registroAux1.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()));
    }

    public void cambiarReferenciaC(int rowNum) {

        // heredado del bean base

    }

    
    public void cargarListacodigoCCPETCuipo(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ApropiacionesinicialesControladorUrlEnum.URL1870006
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);
		
		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "006");

		listacodigoCCPETCuipo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
    	}

    	public void cargarListacodigoCCPETCuipoE(){
    		UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						ApropiacionesinicialesControladorUrlEnum.URL1870006
    						.getValue());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);
    		
    		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "006");


    		listacodigoCCPETCuipoE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
    				GeneralParameterEnum.CODIGO.getName());
    	}

    	public void cargarListaprograma(){

    		UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						ApropiacionesinicialesControladorUrlEnum.URL1870006
    						.getValue());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);
    		
    		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "002");


	                listaprograma = new RegistroDataModelImpl(urlBean.getUrl(),
	                                urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());

    	}

    	public void cargarListaprogramaE(){

    		UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						ApropiacionesinicialesControladorUrlEnum.URL1870006
    						.getValue());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);
    		
    		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "002");
    	

		listaprogramaE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
    	}

    	public void cargarListacodigoBpin(){
    		UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						ApropiacionesinicialesControladorUrlEnum.URL1870006
    						.getValue());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);
    		
    		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "005");

    		listacodigoBpin = new RegistroDataModelImpl(urlBean.getUrl(),
    				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
    	}

    	public void cargarListacodigoBpinE(){
    		UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						ApropiacionesinicialesControladorUrlEnum.URL1870006
    						.getValue());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);
    		
    		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "005");

    		listacodigoBpinE = new RegistroDataModelImpl(urlBean.getUrl(),
    				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
    	}

    	public void cargarListacodigoUnidadEjecutora(){
    			UrlBean urlBean = UrlServiceUtil.getInstance()
    					.getUrlServiceByUrlByEnumID(
    							ApropiacionesinicialesControladorUrlEnum.URL1870006
    							.getValue());
    			Map<String, Object> param = new TreeMap<>();
    			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    			param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);

    			param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "008");


    			listacodigoUnidadEjecutora = new RegistroDataModelImpl(urlBean.getUrl(),
    					urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
    	}

    	public void cargarListacodigoUnidadEjecutoraE(){
    		UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						ApropiacionesinicialesControladorUrlEnum.URL1870006
    						.getValue());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);

    		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "008");


    		listacodigoUnidadEjecutoraE = new RegistroDataModelImpl(urlBean.getUrl(),
    				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
    	}
    	
    	public void cargarListaTipoRecursoSGR() {
    		UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						ApropiacionesinicialesControladorUrlEnum.URL1870006
    						.getValue());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);		
    		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "013");

    		listaTipoRecursoSGR = new RegistroDataModelImpl(urlBean.getUrl(),
    				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
    	}

    	public void cargarListaTipoRecursoSGRE() {
    		UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						ApropiacionesinicialesControladorUrlEnum.URL1870006
    						.getValue());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);		
    		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "013");

    		listaTipoRecursoSGRE = new RegistroDataModelImpl(urlBean.getUrl(),
    				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
    	}
    	
    	public void cargarListaSector(){
    		UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						ApropiacionesinicialesControladorUrlEnum.URL1870006
    						.getValue());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);		
    		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "001");
    		
    		listaSector = new RegistroDataModelImpl(urlBean.getUrl(),
    				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
    	}

    	public void cargarListaSectorE(){
    		UrlBean urlBean = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(
    						ApropiacionesinicialesControladorUrlEnum.URL1870006
    						.getValue());
    		Map<String, Object> param = new TreeMap<>();
    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    		param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);		
    		param.put(GeneralParameterEnum.TIPOCLASIFICADOR.getName(), "001");
    		
    		listaSectorE = new RegistroDataModelImpl(urlBean.getUrl(),
    				urlBean.getUrlConteo().getUrl(), param, true, GeneralParameterEnum.CODIGO.getName());
    	}
    	

    	public void seleccionarFilacodigoCCPETCuipo(SelectEvent event) {
    		Registro registroAux = ((Registro) event.getObject());
    		registro.getCampos().put("CODIGO_CCPET", registroAux.getCampos().get("CODIGO"));
    	}

    	public void seleccionarFilacodigoCCPETCuipoE(SelectEvent event) {
    		Registro registroAux = ((Registro) event.getObject());
    		auxiliar =  SysmanFunciones.nvl( registroAux.getCampos().get("CODIGO"),"").toString();
    	}

    	public void seleccionarFilaprograma(SelectEvent event) {
    		Registro registroAux = ((Registro) event.getObject());
    		registro.getCampos().put("PROGRAMA", registroAux.getCampos().get("CODIGO"));
    	}

    	public void seleccionarFilaprogramaE(SelectEvent event) {
    		Registro registroAux = ((Registro) event.getObject());
    		auxiliar =  SysmanFunciones.nvl( registroAux.getCampos().get("CODIGO"),"").toString();
    	}

    	public void seleccionarFilacodigoBpin(SelectEvent event) {
    		Registro registroAux = ((Registro) event.getObject());
    		registro.getCampos().put("CODIGOBPIN", registroAux.getCampos().get("CODIGO"));
    	}

    	public void seleccionarFilacodigoBpinE(SelectEvent event) {
    		Registro registroAux = ((Registro) event.getObject());
    		auxiliar = SysmanFunciones.nvl( registroAux.getCampos().get("CODIGO"),"").toString();
    	}

    	public void seleccionarFilacodigoUnidadEjecutora(SelectEvent event) {
    		Registro registroAux = ((Registro) event.getObject());
    		registro.getCampos().put("CODIGOUNIDADEJE", registroAux.getCampos().get("CODIGO"));
    	}

    	public void seleccionarFilacodigoUnidadEjecutoraE(SelectEvent event) {
    		Registro registroAux = ((Registro) event.getObject());
    		auxiliar =  SysmanFunciones.nvl( registroAux.getCampos().get("CODIGO"),"").toString();
    	}

    	public void seleccionarFilaTipoRecursoSGR(SelectEvent event) {
    		Registro registroAux = ((Registro) event.getObject());
    		registro.getCampos().put("RECURSO_SGR", registroAux.getCampos().get("CODIGO"));
    	}

    	public void seleccionarFilaTipoRecursoSGRE(SelectEvent event) {
    		Registro registroAux = ((Registro) event.getObject());
    		auxiliar = (String) registroAux.getCampos().get("CODIGO");
    	}
    	
    	public void seleccionarFilaSector(SelectEvent event) {
    		Registro registroAux = ((Registro) event.getObject());
    		registro.getCampos().put("SECTOR", registroAux.getCampos().get("CODIGO"));
    	}

    	public void seleccionarFilaSectorE(SelectEvent event) {
    		Registro registroAux = ((Registro) event.getObject());
    		auxiliar =  (String) registroAux.getCampos().get("CODIGO");
    	}
    	    // </METODOS_CARGAR_LISTA>
    	    // <METODOS_BOTONES>
    	    // </METODOS_BOTONES>
    	    // <METODOS_CAMBIAR>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        bloqueaTercero = true;
        bloqueaCentroCosto = true;
        bloqueaAuxiliar = true;
        bloqueaReferencia = true;
        bloqueaFuente = true;
        registro.getCampos().put(APROPIACIONINICIAL, "0.0");

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        SysmanConstantes.CONS_TERCERO);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        SysmanConstantes.CONS_SUCURSAL);
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        anoVigencia);
        registro.getCampos().remove(NOMBRE_AUXILIAR);
        registro.getCampos().remove(NOMBRE_CODIGO);
        registro.getCampos().remove(NOMBRE_TERCERO);
        registro.getCampos().remove(NOMBRE_CENTROCOSTO);
        registro.getCampos().remove(NOMBRE_REFERENCIA);
        registro.getCampos().remove(NOMBRE_FUENTERECURSOS);

        insertar = true;
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        insertar = false;
        // </CODIGO_DESARROLLADO>
        return true;
    }

    private boolean agregarAlerta(boolean str, String campo, String idiomas) {
        if (str && (registro.getCampos().get(campo) == null)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(idiomas));
            return true;
        }
        return false;
    }

    private boolean mensaje() {
        if (agregarAlerta(manAuxiliar, GeneralParameterEnum.AUXILIAR.getName(),
                        "TB_TB342")) {
            return true;
        }
        if (agregarAlerta(manCenCto,
                        GeneralParameterEnum.CENTRO_COSTO.getName(),
                        "TB_TB343")) {
            return true;
        }
        if (agregarAlerta(manReferencia,
                        GeneralParameterEnum.REFERENCIA.getName(),
                        "TB_TB344")) {
            return true;
        }
        if (agregarAlerta(manFuente,
                        GeneralParameterEnum.FUENTE_RECURSO.getName(),
                        "TB_TB345")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        anoVigencia);
        if (mensaje()) {
            return false;
        }

        try {

            if (!insertar) {
                registro.getCampos().remove(
                                GeneralParameterEnum.COMPANIA.getName());
                registro.getCampos()
                                .remove(GeneralParameterEnum.CODIGO.getName());
                registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
            }

            registro.getCampos()
                            .remove(GeneralParameterEnum.NATURALEZA.getName());

        }
        catch (NumberFormatException e) {
            Logger.getLogger(PlanpresupuestalptosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
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

    public double camposD(int i, List<Registro> regAux, String campo) {
        return regAux.get(i).getCampos().get(campo) == null ? 0
            : Double.valueOf(regAux.get(i).getCampos().get(campo).toString());
    }

    private boolean agregarMensajeEliminarAntes(double str, String idiomas) {
        if (str > 0) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString(idiomas) + " "
                                + new java.text.DecimalFormat(VALOR)
                                                .format(str));
            return true;
        }
        return false;
    }

    public boolean mensajeEliminarAntes(double sumaDisp, double reg, double reo,
        double egr) {
        if (agregarMensajeEliminarAntes(sumaDisp, "TB_TB1258")) {
            return true;
        }
        if (agregarMensajeEliminarAntes(reg, "TB_TB1259")) {
            return true;
        }
        if (agregarMensajeEliminarAntes(reo, "TB_TB1260")) {
            return true;
        }

        if (agregarMensajeEliminarAntes(egr, "TB_TB1261")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean eliminarAntes() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);
            param.put(GeneralParameterEnum.CODIGO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()));

            List<Registro> regAux = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ApropiacionesinicialesControladorUrlEnum.URL37984
                                                                                            .getValue())
                                                            .getUrl(),
                                            param),
                                            CacheUtil.getLlaveServicio(
                                                            urlConexionCache,
                                                            "PLAN_PRESUPUESTAL"));

            if (!validarListaRegistro(regAux)) {
                return false;
            }
        }
        catch (SystemException | SysmanException e) {
            Logger.getLogger(PlanpresupuestalptosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        return true;
    }

    public boolean validarListaRegistro(List<Registro> regAux) {
        if (!regAux.isEmpty()) {
            for (int i = 0; i < regAux.size(); i++) {
                double pac = camposD(i, regAux, "PAC");
                double sumaDisp = camposD(i, regAux, "SUMADISP");
                double reg = camposD(i, regAux, "REG");
                double reo = camposD(i, regAux, "REO");
                double egr = camposD(i, regAux, "EGR");

                if (pac > 0) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1257"));
                    return false;
                }
                if (mensajeEliminarAntes(sumaDisp, reg, reo, egr)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(NOMBRE_CODIGO);
        registro.getCampos().remove(NOMBRE_TERCERO);
        registro.getCampos().remove(NOMBRE_CENTROCOSTO);
        registro.getCampos().remove(NOMBRE_AUXILIAR);
        registro.getCampos().remove(NOMBRE_REFERENCIA);
        registro.getCampos().remove(NOMBRE_FUENTERECURSOS);
    }

    public void activarEdicion(Registro registro) {
        bloqueaFuente = bloqueaAuxiliar = bloqueaCentroCosto = bloqueaReferencia = bloqueaTercero = true;
        indice = listaInicial.getRowIndex();
        String codigo = SysmanFunciones.nvl(
                        registro.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "")
                        .toString();
        
        naturaleza = SysmanFunciones.nvl(
                registro.getCampos().get(
                                GeneralParameterEnum.NATURALEZA.getName()),
                "")
                .toString();
        cargarListaDetalleSectorialE();

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anoVigencia);
            param.put(GeneralParameterEnum.CODIGO.getName(), codigo);

            Registro rAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ApropiacionesinicialesControladorUrlEnum.URL23456
                                                                            .getValue())
                                            .getUrl(), param));

            if ((boolean) rAux.getCampos().get(MAN_AUX_FUE)) {
                bloqueaFuente = false;
            }
            if ((boolean) rAux.getCampos().get(MAN_AUX_GEN)) {
                bloqueaAuxiliar = false;
            }
            if ((boolean) rAux.getCampos().get(MAN_AUX_REF)) {
                bloqueaReferencia = false;
            }

            if ((boolean) rAux.getCampos().get(MAN_CEN_CTO)) {
                bloqueaCentroCosto = false;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(ApropiacionesinicialesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        
        
    }

    @Override
    public void asignarValoresRegistro() {
        registro.getCampos().put(APROPIACIONINICIAL, "0");

    }

    // <SET_GET_ATRIBUTOS>
    public String getNombreCuenta() {
        return nombreCuenta;
    }

    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
    }

    // </SET_GET_ATRIBUTOS>

    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    public boolean isBloqueaTercero() {
        return bloqueaTercero;
    }

    public String getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }

    public void setBloqueaTercero(boolean bloqueaTercero) {
        this.bloqueaTercero = bloqueaTercero;
    }

    public boolean isBloqueaCentroCosto() {
        return bloqueaCentroCosto;
    }

    public void setBloqueaCentroCosto(boolean bloqueaCentroCosto) {
        this.bloqueaCentroCosto = bloqueaCentroCosto;
    }

    public boolean isBloqueaAuxiliar() {
        return bloqueaAuxiliar;
    }

    public void setBloqueaAuxiliar(boolean bloqueaAuxiliar) {
        this.bloqueaAuxiliar = bloqueaAuxiliar;
    }

    public String getAnoVigencia() {
        return anoVigencia;
    }

    public void setAnoVigencia(String anoVigencia) {
        this.anoVigencia = anoVigencia;
    }

    public boolean isManReferencia() {
        return manReferencia;
    }

    public void setManReferencia(boolean manReferencia) {
        this.manReferencia = manReferencia;
    }

    public boolean isManFuente() {
        return manFuente;
    }

    public void setManFuente(boolean manFuente) {
        this.manFuente = manFuente;
    }

    public boolean isManAuxiliar() {
        return manAuxiliar;
    }

    public void setManAuxiliar(boolean manAuxiliar) {
        this.manAuxiliar = manAuxiliar;
    }

    public void setManCenCto(boolean manCenCto) {
        this.manCenCto = manCenCto;
    }

    public void setManAuxTer(boolean manAuxTer) {
        this.manAuxTer = manAuxTer;
    }

    public void setManAuxGen(boolean manAuxGen) {
        this.manAuxGen = manAuxGen;
    }

    public String getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public boolean isBloqueaReferencia() {
        return bloqueaReferencia;
    }

    public void setBloqueaReferencia(boolean bloqueaReferencia) {
        this.bloqueaReferencia = bloqueaReferencia;
    }

    public boolean isBloqueaFuente() {
        return bloqueaFuente;
    }

    public void setBloqueaFuente(boolean bloqueaFuente) {
        this.bloqueaFuente = bloqueaFuente;
    }

    public boolean isManAuxTer() {
        return manAuxTer;
    }

    public boolean isManAuxGen() {
        return manAuxGen;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }

    public RegistroDataModelImpl getListaTerceroE() {
        return listaTerceroE;
    }

    public void setListaTerceroE(RegistroDataModelImpl listaTerceroE) {
        this.listaTerceroE = listaTerceroE;
    }

    public RegistroDataModelImpl getListaCentroCosto() {
        return listaCentroCosto;
    }

    public void setListaCentroCosto(RegistroDataModelImpl listaCentroCosto) {
        this.listaCentroCosto = listaCentroCosto;
    }

    public RegistroDataModelImpl getListaCentroCostoE() {
        return listaCentroCostoE;
    }

    public void setListaCentroCostoE(RegistroDataModelImpl listaCentroCostoE) {
        this.listaCentroCostoE = listaCentroCostoE;
    }

    public RegistroDataModelImpl getListaAuxiliar() {
        return listaAuxiliar;
    }

    public void setListaAuxiliar(RegistroDataModelImpl listaAuxiliar) {
        this.listaAuxiliar = listaAuxiliar;
    }

    public RegistroDataModelImpl getListaAuxiliarE() {
        return listaAuxiliarE;
    }

    public void setListaAuxiliarE(RegistroDataModelImpl listaAuxiliarE) {
        this.listaAuxiliarE = listaAuxiliarE;
    }

    public RegistroDataModelImpl getListaCodigo() {
        return listaCodigo;
    }

    public void setListaCodigo(RegistroDataModelImpl listaCodigo) {
        this.listaCodigo = listaCodigo;
    }

    public RegistroDataModelImpl getListaCodigoE() {
        return listaCodigoE;
    }

    public void setListaCodigoE(RegistroDataModelImpl listaCodigoE) {
        this.listaCodigoE = listaCodigoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public RegistroDataModelImpl getListaFuente() {
        return listaFuente;
    }

    public void setListaFuente(RegistroDataModelImpl listaFuente) {
        this.listaFuente = listaFuente;
    }

    public RegistroDataModelImpl getListaFuenteE() {
        return listaFuenteE;
    }

    public void setListaFuenteE(RegistroDataModelImpl listaFuenteE) {
        this.listaFuenteE = listaFuenteE;
    }

    public RegistroDataModelImpl getListaReferencia() {
        return listaReferencia;
    }

    public void setListaReferencia(RegistroDataModelImpl listaReferencia) {
        this.listaReferencia = listaReferencia;
    }

    public RegistroDataModelImpl getListaReferenciaE() {
        return listaReferenciaE;
    }

    public void setListaReferenciaE(RegistroDataModelImpl listaReferenciaE) {
        this.listaReferenciaE = listaReferenciaE;
    }
    public RegistroDataModelImpl getListacodigoCCPETCuipo() {
 		return listacodigoCCPETCuipo;
 	}

 	public void setListacodigoCCPETCuipo(RegistroDataModelImpl listacodigoCCPETCuipo) {
 		this.listacodigoCCPETCuipo = listacodigoCCPETCuipo;
 	}

 	public RegistroDataModelImpl getListacodigoCCPETCuipoE() {
 		return listacodigoCCPETCuipoE;
 	}

 	public void setListacodigoCCPETCuipoE(RegistroDataModelImpl listacodigoCCPETCuipoE) {
 		this.listacodigoCCPETCuipoE = listacodigoCCPETCuipoE;
 	}

 	public RegistroDataModelImpl getListaprograma() {
 		return listaprograma;
 	}

 	public void setListaprograma(RegistroDataModelImpl listaprograma) {
 		this.listaprograma = listaprograma;
 	}

 	public RegistroDataModelImpl getListaprogramaE() {
 		return listaprogramaE;
 	}

 	public void setListaprogramaE(RegistroDataModelImpl listaprogramaE) {
 		this.listaprogramaE = listaprogramaE;
 	}

 	public RegistroDataModelImpl getListacodigoBpin() {
 		return listacodigoBpin;
 	}

 	public void setListacodigoBpin(RegistroDataModelImpl listacodigoBpin) {
 		this.listacodigoBpin = listacodigoBpin;
 	}

 	public RegistroDataModelImpl getListacodigoBpinE() {
 		return listacodigoBpinE;
 	}

 	public void setListacodigoBpinE(RegistroDataModelImpl listacodigoBpinE) {
 		this.listacodigoBpinE = listacodigoBpinE;
 	}

 	public RegistroDataModelImpl getListacodigoUnidadEjecutora() {
 		return listacodigoUnidadEjecutora;
 	}

 	public void setListacodigoUnidadEjecutora(RegistroDataModelImpl listacodigoUnidadEjecutora) {
 		this.listacodigoUnidadEjecutora = listacodigoUnidadEjecutora;
 	}

 	public RegistroDataModelImpl getListacodigoUnidadEjecutoraE() {
 		return listacodigoUnidadEjecutoraE;
 	}

 	public void setListacodigoUnidadEjecutoraE(RegistroDataModelImpl listacodigoUnidadEjecutoraE) {
 		this.listacodigoUnidadEjecutoraE = listacodigoUnidadEjecutoraE;
 	}
 	public boolean isCampobloqueadocodigoCPC() {
		return campobloqueadocodigoCPC;
	}

	public void setCampobloqueadocodigoCPC(boolean campobloqueadocodigoCPC) {
		this.campobloqueadocodigoCPC = campobloqueadocodigoCPC;
	}

	public boolean isCampobloqueadoFuenteCuipo() {
		return campobloqueadoFuenteCuipo;
	}

	public void setCampobloqueadoFuenteCuipo(boolean campobloqueadoFuenteCuipo) {
		this.campobloqueadoFuenteCuipo = campobloqueadoFuenteCuipo;
	}

	public boolean isCampobloqueadoProductoCuipo() {
		return campobloqueadoProductoCuipo;
	}

	public void setCampobloqueadoProductoCuipo(boolean campobloqueadoProductoCuipo) {
		this.campobloqueadoProductoCuipo = campobloqueadoProductoCuipo;
	}

	public boolean isCampobloqueadoCodigoCCPETCuipo() {
		return campobloqueadoCodigoCCPETCuipo;
	}

	public void setCampobloqueadoCodigoCCPETCuipo(boolean campobloqueadoCodigoCCPETCuipo) {
		this.campobloqueadoCodigoCCPETCuipo = campobloqueadoCodigoCCPETCuipo;
	}

	public boolean isCampobloqueadoCodigoBPINCampo() {
		return campobloqueadoCodigoBPINCampo;
	}

	public void setCampobloqueadoCodigoBPINCampo(boolean campobloqueadoCodigoBPINCampo) {
		this.campobloqueadoCodigoBPINCampo = campobloqueadoCodigoBPINCampo;
	}
	public boolean isSectorComboMostar() {
		return sectorComboMostar;
	}

	public void setSectorComboMostar(boolean sectorComboMostar) {
		this.sectorComboMostar = sectorComboMostar;
	}

	public boolean isProgramaMostar() {
		return programaMostar;
	}

	public void setProgramaMostar(boolean programaMostar) {
		this.programaMostar = programaMostar;
	}

	public boolean isSubprogramaMostar() {
		return subprogramaMostar;
	}

	public void setSubprogramaMostar(boolean subprogramaMostar) {
		this.subprogramaMostar = subprogramaMostar;
	}

	public boolean isCodigoProductoMostar() {
		return codigoProductoMostar;
	}

	public void setCodigoProductoMostar(boolean codigoProductoMostar) {
		this.codigoProductoMostar = codigoProductoMostar;
	}

	public boolean isCodigoBpinMostar() {
		return codigoBpinMostar;
	}

	public void setCodigoBpinMostar(boolean codigoBpinMostar) {
		this.codigoBpinMostar = codigoBpinMostar;
	}

	public boolean isCodigoCCPETMostar() {
		return codigoCCPETMostar;
	}

	public void setCodigoCCPETMostar(boolean codigoCCPETMostar) {
		this.codigoCCPETMostar = codigoCCPETMostar;
	}

	public boolean isCodigoCPCDANEMostar() {
		return codigoCPCDANEMostar;
	}

	public void setCodigoCPCDANEMostar(boolean codigoCPCDANEMostar) {
		this.codigoCPCDANEMostar = codigoCPCDANEMostar;
	}

	public boolean isCodigoUnidadEjecutoraMostar() {
		return codigoUnidadEjecutoraMostar;
	}

	public void setCodigoUnidadEjecutoraMostar(boolean codigoUnidadEjecutoraMostar) {
		this.codigoUnidadEjecutoraMostar = codigoUnidadEjecutoraMostar;
	}

	public boolean isCodigoFuenteMostar() {
		return codigoFuenteMostar;
	}

	public void setCodigoFuenteMostar(boolean codigoFuenteMostar) {
		this.codigoFuenteMostar = codigoFuenteMostar;
	}

	public boolean isCodigoCCPETRegaliasMostar() {
		return codigoCCPETRegaliasMostar;
	}

	public void setCodigoCCPETRegaliasMostar(boolean codigoCCPETRegaliasMostar) {
		this.codigoCCPETRegaliasMostar = codigoCCPETRegaliasMostar;
	}
	public boolean isCodigoCPCMostar() {
		return codigoCPCMostar;
	}

	public void setCodigoCPCMostar(boolean codigoCPCMostar) {
		this.codigoCPCMostar = codigoCPCMostar;
	}

	public boolean isFuenteCuipoMostar() {
		return fuenteCuipoMostar;
	}

	public void setFuenteCuipoMostar(boolean fuenteCuipoMostar) {
		this.fuenteCuipoMostar = fuenteCuipoMostar;
	}

	public boolean isProductoCuipoMostar() {
		return productoCuipoMostar;
	}

	public void setProductoCuipoMostar(boolean productoCuipoMostar) {
		this.productoCuipoMostar = productoCuipoMostar;
	}

	public boolean isCodigoCCPETCuipoMostar() {
		return codigoCCPETCuipoMostar;
	}

	public void setCodigoCCPETCuipoMostar(boolean codigoCCPETCuipoMostar) {
		this.codigoCCPETCuipoMostar = codigoCCPETCuipoMostar;
	}

	public boolean isCodigoBpinCampoMostrar() {
		return codigoBpinCampoMostrar;
	}

	public void setCodigoBpinCampoMostrar(boolean codigoBpinCampoMostrar) {
		this.codigoBpinCampoMostrar = codigoBpinCampoMostrar;
	}
	public boolean isCampobloqueadoprograma() {
		return campobloqueadoprograma;
	}

	public void setCampobloqueadoprograma(boolean campobloqueadoprograma) {
		this.campobloqueadoprograma = campobloqueadoprograma;
	}

	public boolean isCampobloqueadocodbpin() {
		return campobloqueadocodbpin;
	}

	public void setCampobloqueadocodbpin(boolean campobloqueadocodbpin) {
		this.campobloqueadocodbpin = campobloqueadocodbpin;
	}

	public boolean isCampobloqueadocodunidadejecutora() {
		return campobloqueadocodunidadejecutora;
	}

	public void setCampobloqueadocodunidadejecutora(boolean campobloqueadocodunidadejecutora) {
		this.campobloqueadocodunidadejecutora = campobloqueadocodunidadejecutora;
	}

	public RegistroDataModelImpl getListaDetalleSectorial() {
		return listaDetalleSectorial;
	}

	public void setListaDetalleSectorial(RegistroDataModelImpl listaDetalleSectorial) {
		this.listaDetalleSectorial = listaDetalleSectorial;
	}

	public RegistroDataModelImpl getListaDetalleSectorialE() {
		return listaDetalleSectorialE;
	}

	public void setListaDetalleSectorialE(RegistroDataModelImpl listaDetalleSectorialE) {
		this.listaDetalleSectorialE = listaDetalleSectorialE;
	}

	public RegistroDataModelImpl getListaTipoRecursoSGR() {
		return listaTipoRecursoSGR;
	}

	public void setListaTipoRecursoSGR(RegistroDataModelImpl listaTipoRecursoSGR) {
		this.listaTipoRecursoSGR = listaTipoRecursoSGR;
	}

	public RegistroDataModelImpl getListaTipoRecursoSGRE() {
		return listaTipoRecursoSGRE;
	}

	public void setListaTipoRecursoSGRE(RegistroDataModelImpl listaTipoRecursoSGRE) {
		this.listaTipoRecursoSGRE = listaTipoRecursoSGRE;
	}
	
	public RegistroDataModelImpl getListaSector(){
		return listaSector;
	}

	public void setListaSector(RegistroDataModelImpl listaSector){
		this.listaSector = listaSector;
	}

	public RegistroDataModelImpl getListaSectorE(){
		return listaSectorE;
	}

	public void setListaSectorE(RegistroDataModelImpl listaSectorE){
		this.listaSectorE = listaSectorE;
	}
	
	
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
