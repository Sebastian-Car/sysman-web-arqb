package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.InfResolucionesControladorEnum;
import com.sysman.nomina.enums.InfResolucionesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
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

import org.primefaces.event.SelectEvent;
import com.sysman.nomina.ejb.EjbNominaDosRemote;

/**
 *
 * @author sdaza
 * @version 1, 15/12/2015
 *
 * -- Modificado por lcortes 17/03/2017 12:11. --> Ajustes de buenas
 * practicas SonarLint.
 * 
 * @author asana
 * @version 3, 09/10/2017 Se realiza refactoring de controlador.
 */
@ManagedBean
@ViewScoped
public class InfResolucionesControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private String nombreEntidad;
    private String nombreCiudad;
    private final String procesoNomina;
    private final String anoNomina;
    private final String mesNomina;
    private final String periodoNomina;
    /**
     * Constante a nivel de clase que identifica el campo CODIGO
     */
    private final String codigo;
    private String indIndemnizada;
    private String ano;
    private String mes;
    private String periodo;
    private String idProceso;
    private String tip;
    private String idEmpleado;
    private String plantilla;
    private String tipo;
    private String fechaConsulta;
    private String quienAutoriza;
    private String interesado;
    private String num;
    private String extras;
    private String extrasC;
    private Date fechaReporte;
    private String nomEmpleado;
    private String opcion;
    private String codPlantilla;
    private String nombrePlantilla;
    private Date fechaPlantilla;
    private List<Registro> listaAno;
    private List<Registro> listaMes;
    private List<Registro> listaPeriodo;
    private List<Registro> listaProceso;
    private RegistroDataModelImpl listaEmpleado;
    private RegistroDataModelImpl listaPlantilla;
    
    @EJB
    private EjbNominaDosRemote ejbNominaDos;

    /**
     * Creates a new instance of InfResolucionesControlador
     */
    public InfResolucionesControlador() {
        super();
        compania = SessionUtil.getCompania();
        procesoNomina = (String) SessionUtil.getSessionVar("procesoNomina");
        anoNomina = (String) SessionUtil.getSessionVar("anioNomina");
        mesNomina = (String) SessionUtil.getSessionVar("mesNomina");
        periodoNomina = (String) SessionUtil.getSessionVar("periodoNomina");
        codigo = "CODIGO";

        try {
            idProceso = procesoNomina;
            ano = anoNomina;
            mes = mesNomina;
            periodo = periodoNomina;
            fechaReporte = new Date();
            opcion = "1";
            tipo = "3";
            extras = "0";
            extrasC = "0";

            fechaConsulta = SysmanFunciones.formatearFecha(SysmanFunciones
                            .convertirAFecha(SysmanFunciones.concatenar("31/",
                                            SysmanFunciones.padl(mes, 2, "0"),
                                            "/",
                                            SysmanFunciones.padl(ano, 4,
                                                            "0"))));

            nombreEntidad = SessionUtil.getCompaniaIngreso().getNombre();
            nombreCiudad = SessionUtil.getCompaniaIngreso().getCiudad();
            numFormulario = GeneralCodigoFormaEnum.INF_RESOLUCIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InfResolucionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaAno();
        cargarListaMes();
        cargarListaPeriodo();
        cargarListaProceso();
        cargarListaEmpleado();
        cargarListaPlantilla();
        abrirFormulario();
    }

    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InfResolucionesControladorEnum.PARAM1.getValue(), idProceso);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfResolucionesControladorUrlEnum.URL3927
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaMes() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InfResolucionesControladorEnum.PARAM3.getValue(), idProceso);
        param.put(InfResolucionesControladorEnum.PARAM4.getValue(), ano);

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfResolucionesControladorUrlEnum.URL4541
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaPeriodo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InfResolucionesControladorEnum.PARAM2.getValue(), idProceso);
        param.put(InfResolucionesControladorEnum.PARAM4.getValue(), ano);
        param.put(InfResolucionesControladorEnum.PARAM0.getValue(), mes);

        try {
            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfResolucionesControladorUrlEnum.URL3928
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaProceso() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InfResolucionesControladorUrlEnum.URL3929
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaEmpleado() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfResolucionesControladorUrlEnum.URL7027
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID_DE_EMPLEADO");

    }

    public void cargarListaPlantilla() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InfResolucionesControladorUrlEnum.URL7836
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(InfResolucionesControladorEnum.PARAM6.getValue(), tipo);
        param.put(InfResolucionesControladorEnum.PARAM5.getValue(),
                        fechaConsulta);

        listaPlantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);

        if (listaPlantilla.getDatasource().isEmpty()) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2561"));
        }
    }

    public void oprimirPreliminar() {

        if (validarFormulario()) {
            try {
                // <CODIGO_DESARROLLADO>
            	
            	 if (("4").equals(opcion)) {
            		 
            		 try {
            			 ejbNominaDos.getActualizarVacaPeriodo(compania,
            				        Integer.parseInt(idProceso), Integer.parseInt(ano),
            				        Integer.parseInt(mes), Integer.parseInt(periodo),
            				        Integer.parseInt("0"),
            				        SessionUtil.getUser().getCodigo());
					} catch (NumberFormatException | SystemException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                 }

                String[] campos = new String[3];
                String[] valores = new String[3];

                campos[0] = "codigoPlantilla";
                campos[1] = "fechaPlantilla";
                campos[2] = "nombreDocDescarga";

                valores[0] = codPlantilla;
                valores[1] = SysmanFunciones.formatearFecha(fechaPlantilla);
                valores[2] = nombrePlantilla;

                HashMap<String, String> variablesConsultaW = new HashMap<>();
                variablesConsultaW.put("s$companiaNombre$s", nombreEntidad);
                variablesConsultaW.put("s$companiaCiudad$s", nombreCiudad);
                variablesConsultaW.put("s$compania$s", compania);
                variablesConsultaW.put("s$idProceso$s", idProceso);
                variablesConsultaW.put("s$ano$s", ano);
                variablesConsultaW.put("s$mes$s", mes);
                variablesConsultaW.put("s$periodo$s", periodo);
                variablesConsultaW.put("s$idEmpleado$s",
                                SysmanFunciones.nvlStr(idEmpleado, "-1"));
                variablesConsultaW.put("s$fechaReporte$s",
                                SysmanFunciones.concatenar("'",
                                                SysmanFunciones.convertirAFechaCadena(
                                                                fechaReporte),
                                                "'"));
                if (("7").equals(opcion)) {
                    NumberFormat formatoMoneda = NumberFormat.getInstance();
                    String cadenaExtras = idioma.getString("TB_TB3711")
                                    .replace("s$extras$s", formatoMoneda
                                                    .format(Double.parseDouble(
                                                                    extrasC)))
                                    .replace("s$extrasc$s", SysmanFunciones
                                                    .moneda(Double.parseDouble(
                                                                    extrasC),
                                                                    0));

                    variablesConsultaW.put("s$vlrExtras$s", SysmanFunciones
                                    .concatenar("'", cadenaExtras, "'"));
                }

                SessionUtil.setSessionVar("variablesConsultaWord",
                                variablesConsultaW);

                SessionUtil.cargarModalDatosFlash(Integer.toString(
                                GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                                .getCodigo()),
                                SessionUtil.getModulo(), campos, valores);             
                // </CODIGO_DESARROLLADO>
            }
            catch (ParseException ex) {
                Logger.getLogger(InfResolucionesControlador.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        mes = "";
        periodo = "";
        plantilla = "";
        cargarListaMes();
        cargarListaPeriodo();
        cargarListaPlantilla();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        periodo = "";
        plantilla = "";
        cargarListaPeriodo();
        try {
            fechaConsulta = SysmanFunciones.formatearFecha(
                            SysmanFunciones.ultimoDiaDate(SysmanFunciones
                                            .convertirAFecha(SysmanFunciones
                                                            .concatenar("01/",
                                                                            SysmanFunciones.padl(
                                                                                            mes,
                                                                                            2,
                                                                                            "0"),
                                                                            "/",
                                                                            SysmanFunciones.padl(
                                                                                            ano,
                                                                                            4,
                                                                                            "0")))));
        }
        catch (ParseException ex) {
            Logger.getLogger(InfResolucionesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        cargarListaPlantilla();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPeriodo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarProceso() {
        // <CODIGO_DESARROLLADO>
        ano = "";
        mes = "";
        periodo = "";
        plantilla = "";
        cargarListaAno();
        cargarListaPlantilla();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        idEmpleado = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("ID_DE_EMPLEADO"), "").toString();
        nomEmpleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRECOMPLETO"), "")
                        .toString();
    }

    public void cambiarextras6meses() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(extras)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2562"));
        }
        else {
            DecimalFormat formato = new DecimalFormat("$#,###.###");
            extrasC = extras;
            extras = formato.format(Double.parseDouble(extras));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaPlantilla(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codPlantilla = SysmanFunciones
                        .nvl(registroAux.getCampos().get(codigo), "")
                        .toString();
        plantilla = SysmanFunciones.nvl(registroAux.getCampos().get(codigo), "")
                        .toString();
        nombrePlantilla = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        try {
            fechaPlantilla = SysmanFunciones.convertirAFecha(SysmanFunciones
                            .nvl(registroAux.getCampos().get("FECHA"), "")
                            .toString());
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiaropcion() {
        plantilla = "";

        switch (opcion) {
        case "1":
            tipo = "3";
            break;
        case "2":
            tipo = "4";
            break;
        case "3":
            tipo = "5";
            break;
        case "4":
            tipo = "6";
            break;
        case "5":
            tipo = "7";
            break;
        case "6":
            tipo = "8";
            break;
        case "7":
            tipo = "15";
            break;
        case "8":
            tipo = "57";
            break;
        default:
            break;
        }

        cargarListaPlantilla();

    }

    public String getIndIndemnizada() {
        return indIndemnizada;
    }

    public void setIndIndemnizada(String indIndemnizada) {
        this.indIndemnizada = indIndemnizada;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getIdProceso() {
        return idProceso;
    }

    public void setIdProceso(String idProceso) {
        this.idProceso = idProceso;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getQuienAutoriza() {
        return quienAutoriza;
    }

    public void setQuienAutoriza(String quienAutoriza) {
        this.quienAutoriza = quienAutoriza;
    }

    public String getInteresado() {
        return interesado;
    }

    public void setInteresado(String interesado) {
        this.interesado = interesado;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getExtrasC() {
        return extrasC;
    }

    public void setExtrasC(String extrasC) {
        this.extrasC = extrasC;
    }

    public Date getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(Date fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public String getNomEmpleado() {
        return nomEmpleado;
    }

    public void setNomEmpleado(String nomEmpleado) {
        this.nomEmpleado = nomEmpleado;
    }

    public void renderizar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    public RegistroDataModelImpl getListaEmpleado() {
        return listaEmpleado;
    }

    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado) {
        this.listaEmpleado = listaEmpleado;
    }

    public String getPlantilla() {
        return plantilla;
    }

    public void setPlantilla(String plantilla) {
        this.plantilla = plantilla;
    }

    public RegistroDataModelImpl getListaPlantilla() {
        return listaPlantilla;
    }

    public void setListaPlantilla(RegistroDataModelImpl listaPlantilla) {
        this.listaPlantilla = listaPlantilla;
    }

    public String getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(String fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCodPlantilla() {
        return codPlantilla;
    }

    public void setCodPlantilla(String codPlantilla) {
        this.codPlantilla = codPlantilla;
    }

    public String getNombrePlantilla() {
        return nombrePlantilla;
    }

    public void setNombrePlantilla(String nombrePlantilla) {
        this.nombrePlantilla = nombrePlantilla;
    }

    public Date getFechaPlantilla() {
        return fechaPlantilla;
    }

    public void setFechaPlantilla(Date fechaPlantilla) {
        this.fechaPlantilla = fechaPlantilla;
    }

    public String getCompania() {
        return compania;
    }

    public String getNombreEntidad() {
        return nombreEntidad;
    }

    public String getNombreCiudad() {
        return nombreCiudad;
    }

    public String getProcesoNomina() {
        return procesoNomina;
    }

    public String getAnoNomina() {
        return anoNomina;
    }

    public String getPeriodoNomina() {
        return periodoNomina;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarFormulario() {
        if (("5".equals(opcion) || "6".equals(opcion))
            && SysmanFunciones.validarVariableVacio(idEmpleado)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3743"));
            return false;
        }
        if (("4").equals(opcion)
            && SysmanFunciones.validarVariableVacio(idEmpleado)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2563"));
            return false;
        }

        if (("7").equals(opcion) && !validarCamposVacios()) {
            return false;
        }
        return true;
    }

    private boolean validarCamposVacios() {
        if (SysmanFunciones.validarVariableVacio(idEmpleado)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3743"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(extras)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2562"));
            return false;
        }
        return true;
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListasSub() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void iniciarListas() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarOrigenDatos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
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
