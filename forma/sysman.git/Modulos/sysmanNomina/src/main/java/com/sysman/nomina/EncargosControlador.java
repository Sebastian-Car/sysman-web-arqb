package com.sysman.nomina;

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
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.ejb.EjbNominaDosRemote;
import com.sysman.nomina.enums.EncargosControladorEnum;
import com.sysman.nomina.enums.EncargosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
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

import org.apache.commons.lang3.text.WordUtils;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author jgomez
 * @version 1, 17/11/2015
 *
 * Revision Sonar
 *
 * -- Modificado por ybecerra 16/03/2017
 * 
 * @author jcrodriguez, Refactoring y depuracion
 * @version 2, 03/10/2017
 * 
 */
@ManagedBean
@ViewScoped
public class EncargosControlador extends BeanBaseDatosAcmeImpl {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private String periodoNomina;
    private String procesoNomina;
    private String mesNomina;
    private String anioNomina;
    private String idEmpleado;
    private String nombreEmpleado;
    private String cedula;
    private String tituloForm;
    private String categoria;
    private double porcGastos = 0.0;
    private int dias = 0;
    private int diasIni = 0;
    private int rta;
    private List<Registro> listaEscalafon;
    private List<Registro> listaIDdeCargo;
    private RegistroDataModelImpl listaIDdeCategoria;
    private RegistroDataModelImpl listaFechaPago;
    private Map<String, Object> parametrosEntrada;
    private Date fechaPago;
    private boolean vacaciones;
    private boolean cesantias;
    private Date fechaFinalActual;
    private boolean cargaAscenso;
    @EJB
    private EjbNominaCeroRemote ejbNominaCero;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbNominaDosRemote ejbNominaDos;

    public EncargosControlador() {
        super();
        compania = SessionUtil.getCompania();
        parametrosEntrada = SessionUtil.getFlash();

        try {
            periodoNomina = validarCadena(
                            SessionUtil.getSessionVar("periodoNomina"));
            procesoNomina = validarCadena(
                            SessionUtil.getSessionVar("procesoNomina"));
            mesNomina = validarCadena(SessionUtil.getSessionVar("mesNomina"));
            anioNomina = validarCadena(SessionUtil.getSessionVar("anioNomina"));
            idEmpleado = validarCampoCadena(parametrosEntrada, "idEmpleado");
            nombreEmpleado = validarCampoCadena(parametrosEntrada,
                            "nombreEmpleado");
            cedula = validarCampoCadena(parametrosEntrada,
                            EncargosControladorEnum.CEDULA.getValue()
                                            .toLowerCase());
            numFormulario = GeneralCodigoFormaEnum.ENCARGOS_CONTROLADOR
                            .getCodigo();
            vacaciones = (boolean) SysmanFunciones
                            .nvl(parametrosEntrada.get("vacaciones"),
                                            false);
            cesantias = (boolean) SysmanFunciones
                            .nvl(parametrosEntrada.get("cesantias"),
                                            false);
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(EncargosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    private String validarCampoCadena(Map<String, Object> campos, String var) {
        return SysmanFunciones.validarCampoVacio(campos, var) ? ""
            : campos.get(var).toString();
    }

    private String validarCadena(Object var) {
        return SysmanFunciones.validarVariableVacio(var.toString()) ? ""
            : var.toString();
    }

    @Override
    public void iniciarListas() {

        cargarListaFechaPago();
        cargarListaEscalafon();

    }

    @Override
    public void iniciarListasSub() {
        cargarListaIDdeCategoria();
        cargarListaIDdeCargo();
    }

    @Override
    public void iniciarListasSubNulo() {
        // heredado del bean base
    }

    /**
     * metodo que se llama al inicializar el formulario
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.ENCARGOS;
        buscarLlave();
        asignarOrigenDatos();
        
        cargaAscenso = "SI".equals(getParametro("ACTIVAR INDICADOR DE ASCENSO EN ENCARGOS","NO"));
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(EncargosControladorEnum.ID_DE_EMPLEADO.getValue(),
                        idEmpleado);
    }
    
    private String getParametro(String nombreParametro, String valorDefault) {
    	String parametro = null;
    	
		try {
			parametro = ejbSysmanUtil.consultarParametro(compania, nombreParametro, SessionUtil.getModulo(), new Date(),
					true);
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
		return parametro != null ? parametro : valorDefault;
	}

    public void cargarListaEscalafon() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaEscalafon = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EncargosControladorUrlEnum.URL10148
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaIDdeCargo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EncargosControladorEnum.ESCALAFON.getValue(), registro
                        .getCampos()
                        .get(EncargosControladorEnum.ESCALAFON.getValue()));

        try {
            listaIDdeCargo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EncargosControladorUrlEnum.URL9626
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaIDdeCategoria() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EncargosControladorEnum.ESCALAFON.getValue(), registro
                        .getCampos()
                        .get(EncargosControladorEnum.ESCALAFON.getValue()));
        param.put(GeneralParameterEnum.ANO.getName(), anioNomina);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EncargosControladorUrlEnum.URL9246
                                                        .getValue());
        listaIDdeCategoria = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        EncargosControladorEnum.ID_DE_CATEGORIA.getValue());

    }

    public void cargarListaFechaPago() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EncargosControladorEnum.ID_DE_PROCESO.getValue(),
                        procesoNomina);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EncargosControladorUrlEnum.URL9229
                                                        .getValue());
        listaFechaPago = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, EncargosControladorEnum.FECHAFINAL.getValue());

    }

    public void cambiarEscalafon() {
        cargarListaIDdeCargo();
        registro.getCampos().put(EncargosControladorEnum.ID_DE_CARGO.getValue(),
                        null);
        cargarListaIDdeCategoria();
        registro.getCampos().put(
                        EncargosControladorEnum.ID_DE_CATEGORIA.getValue(),
                        null);
        categoria = null;
    }

    public void cambiarIDdeCargo() {
        // heredado del bean base
    }

    public void cambiarFechaInicio() {

        try {
            fechaPago = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(procesoNomina),
                            Integer.parseInt(anioNomina),
                            Integer.parseInt(mesNomina),
                            Integer.parseInt(periodoNomina), false, true);
            registro.getCampos().put(
                            EncargosControladorEnum.FECHAPAGO.getValue(),
                            SysmanFunciones.convertirAFechaCadena(fechaPago,
                                            "dd/MM/yyyy"));
            registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                            SysmanFunciones.ano(SysmanFunciones.convertirAFecha(
                                            registro.getCampos()
                                                            .get(EncargosControladorEnum.FECHAPAGO
                                                                            .getValue())
                                                            .toString())));
            registro.getCampos().put(GeneralParameterEnum.MES.getName(),
                            SysmanFunciones.mes(SysmanFunciones.convertirAFecha(
                                            registro.getCampos()
                                                            .get(EncargosControladorEnum.FECHAPAGO
                                                                            .getValue())
                                                            .toString())));

            registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                            periodoNomina);
            registro.getCampos().put(
                            EncargosControladorEnum.ID_DE_PROCESO.getValue(),
                            procesoNomina);
            calDias();
        }
        catch (ParseException | NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            EncargosControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(EncargosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        if (Integer.parseInt(mesNomina) > SysmanFunciones
                        .mes((Date) registro.getCampos().get("FECHAINICIO"))) {
            try {
                JsfUtil.agregarMensajeInformativo(idioma.getString("").replace(
                                "s$mes$s", ejbSysmanUtil.mostrarNombreDeMes(
                                                Integer.parseInt(mesNomina))));
            }
            catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

    }

    public boolean validarFechaIniciaFinal() {

        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        EncargosControladorEnum.FECHAINICIO.getValue())
            && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            EncargosControladorEnum.FECHAFINAL.getValue())) {

            Date fechaInicial = (Date) registro.getCampos().get(
                            EncargosControladorEnum.FECHAINICIO.getValue());
            Date fechaFinal = (Date) registro.getCampos()
                            .get(EncargosControladorEnum.FECHAFINAL.getValue());

            return fechaInicial.before(fechaFinal) || fechaInicial.equals(fechaFinal) ? false : true; //MOD JM CC1910

        }
        return false;
    }

    public void cambiarFechaFinal() {
		if(accion.equals("m")) {
			Date fechaFinal = (Date) registro.getCampos()
					.get(EncargosControladorEnum.FECHAFINAL.getValue());
			Date fechaActual = (Date) registro.getCampos().get(EncargosControladorEnum.FECHAINICIO.getValue());
		
			if(fechaFinal.before(fechaActual) ){
				registro.getCampos()
					.put(EncargosControladorEnum.FECHAFINAL.getValue(), fechaFinalActual);
			}
	    }
        
        calDias();
    }

    public void calDias() {
        try {
            dias = 0;
            if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                            EncargosControladorEnum.FECHAINICIO.getValue())
                && !SysmanFunciones.validarCampoVacio(registro.getCampos(),
                                EncargosControladorEnum.FECHAFINAL
                                                .getValue())) {

                dias = Integer.parseInt(
                                ejbSysmanUtil.calcularDiferenciaEntreFechas(
                                                (Date) registro.getCampos()
                                                                .get(EncargosControladorEnum.FECHAINICIO
                                                                                .getValue()),
                                                (Date) registro.getCampos()
                                                                .get(EncargosControladorEnum.FECHAFINAL
                                                                                .getValue()),
                                                3, 0));

            }
        }
        catch (NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            EncargosControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
            Logger.getLogger(EncargosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void seleccionarFilaIDdeCategoria(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        EncargosControladorEnum.ID_DE_CATEGORIA.getValue(),
                        registroAux.getCampos()
                                        .get(EncargosControladorEnum.ID_DE_CATEGORIA
                                                        .getValue()));
        categoria = SysmanFunciones.concatenar(validarCampoCadena(
                        registroAux.getCampos(),
                        EncargosControladorEnum.CODIGOGRADO.getValue()),
                        " - ",
                        validarCampoCadena(registroAux.getCampos(),
                                        EncargosControladorEnum.NOMBRE_CATEGORIA
                                                        .getValue()));
        registro.getCampos().put(
                        EncargosControladorEnum.SUELDOMENSUAL.getValue(),
                        registroAux.getCampos()
                                        .get(EncargosControladorEnum.SALARIO_BASE
                                                        .getValue()));
        porcGastos = Double
                        .parseDouble(validarCampoCadena(registroAux.getCampos(),
                                        EncargosControladorEnum.GASTOSREPRESENTACION
                                                        .getValue()));
    }

    public void seleccionarFilaFechaPago(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        try {
            registro.getCampos().put(
                            EncargosControladorEnum.FECHAPAGO.getValue(),
                            SysmanFunciones.convertirAFechaCadena(
                                            (Date) registroAux.getCampos()
                                                            .get(EncargosControladorEnum.FECHAFINAL
                                                                            .getValue()),
                                            "dd/MM/yyyy"));
        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), registroAux
                        .getCampos().get(GeneralParameterEnum.ANO.getName()));
        registro.getCampos().put(GeneralParameterEnum.MES.getName(), registroAux
                        .getCampos().get(GeneralParameterEnum.MES.getName()));
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                        registroAux.getCampos().get(GeneralParameterEnum.PERIODO
                                        .getName()));
        registro.getCampos().put(
                        EncargosControladorEnum.ID_DE_PROCESO.getValue(),
                        procesoNomina);
    }

    @Override
    public void abrirFormulario() {
        tituloForm = SysmanFunciones.concatenar(idioma.getString("TT_BT155"),
                        " ",
                        WordUtils.capitalize(nombreEmpleado.toLowerCase()));

    }

    @Override
    public void cargarRegistro() {
        precargarRegistro();
        cargarListaEscalafon();
        cargarListaIDdeCargo();
        cargarListaIDdeCategoria();
        cargarListaFechaPago();
        calDias();        
        categoria = null;
        fechaFinalActual = (Date) registro.getCampos().get(EncargosControladorEnum.FECHAFINAL.getValue());
        diasIni = dias;
        if (registro.getCampos().get(EncargosControladorEnum.ID_DE_CATEGORIA
                        .getValue()) != null) {
            Map<String, Object> param = new HashMap<>();
            param.put(EncargosControladorEnum.ID_DE_CATEGORIA.getValue(),
                            registro.getCampos()
                                            .get(EncargosControladorEnum.ID_DE_CATEGORIA
                                                            .getValue()));

            Registro regCate;
            try {
                regCate = listaIDdeCategoria.getRegistroUnico(param);
                categoria = SysmanFunciones.concatenar(
                                validarCampoCadena(regCate.getCampos(),
                                                EncargosControladorEnum.CODIGOGRADO
                                                                .getValue()),
                                " - ",
                                validarCampoCadena(regCate.getCampos(),
                                                EncargosControladorEnum.NOMBRE_CATEGORIA
                                                                .getValue()));
                porcGastos = Double.parseDouble(
                                validarCampoCadena(regCate.getCampos(),
                                                EncargosControladorEnum.GASTOSREPRESENTACION
                                                                .getValue()));
            }
            catch (SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }

        if ("i".equals(accion)) {
            registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                            anioNomina);
            registro.getCampos().put(GeneralParameterEnum.MES.getName(),
                            mesNomina);
            registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                            periodoNomina);
            registro.getCampos().put(
                            EncargosControladorEnum.ID_DE_PROCESO.getValue(),
                            procesoNomina);
            registro.getCampos().put(
                            EncargosControladorEnum.ID_DE_EMPLEADO.getValue(),
                            idEmpleado);
            registro.getCampos().put(
                            EncargosControladorEnum.SUELDOMENSUAL.getValue(),
                            0);
        }
    }

    public boolean diferirEnc(String opcion) {

        boolean bolRta = false;
        try {
        	calDias();
        	int diasAux = dias;
            
            if(opcion.equals("BORRAR") ) {
            	if ("m".equals(accion)) {
            		recalcularDias();
            		diasAux = diasIni;
            	}
            	 
            }

            boolean rpta = ejbNominaDos.getDiferirEnc(compania,
                            Integer.parseInt(procesoNomina),
                            Integer.parseInt(idEmpleado), diasAux,
                            SysmanFunciones.validarVariableVacio(opcion) ? null
                                : opcion,
                            (Date) registro.getCampos()
                                            .get(EncargosControladorEnum.FECHAINICIO
                                                            .getValue()),
                            porcGastos,
                            new BigDecimal(registro.getCampos()
                                            .get(EncargosControladorEnum.SUELDOMENSUAL
                                                            .getValue())
                                            .toString()),
                            SessionUtil.getUser().getCodigo());
            
            
            
            //if (!opcion.equals("BORRAR") && !"m".equals(accion)) {
            		ejbNominaDos.getIncluirNovedadEncargos(
            		Integer.parseInt(mesNomina),
            		Integer.parseInt(periodoNomina),
            		Integer.parseInt(anioNomina),
                    Integer.parseInt(procesoNomina), compania, 
                    (Date) registro.getCampos().get(EncargosControladorEnum.FECHAINICIO.getValue()),
                    (Date) registro.getCampos().get(EncargosControladorEnum.FECHAFINAL.getValue()), 
                    SessionUtil.getUser().getCodigo(),Integer.parseInt(idEmpleado));
            //}
            		
            		

            setRta(rpta ? 1 : 0);
            bolRta = true;
            diasIni = dias;
        }
        catch (NumberFormatException | SystemException ex) {
            Logger.getLogger(EncargosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            EncargosControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
        }

        return bolRta;
    }

    private boolean validarFechaFinalActo() {
        Date fechaActo = (Date) registro.getCampos()
                        .get(EncargosControladorEnum.FECHAACTO.getValue());
        Date fechaFinal = (Date) registro.getCampos()
                        .get(EncargosControladorEnum.FECHAFINAL.getValue());
        
        return !(fechaActo.before(fechaFinal) || fechaActo.equals(fechaFinal)) ? true : false; //JM MOD CC 2906
    }

    private boolean validarFechaIniFinEntrePago() {

        try {
            Date fechaInicial = (Date) registro.getCampos().get(
                            EncargosControladorEnum.FECHAINICIO.getValue());

            Date fechaPagoAux = SysmanFunciones
                            .convertirAFecha(registro.getCampos()
                                            .get(EncargosControladorEnum.FECHAPAGO
                                                            .getValue())
                                            .toString());

            return fechaPagoAux.compareTo(fechaInicial) > 0;

        }
        catch (ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return false;

    }
    
    public void recalcularDias() {
    	
    	 try {
    	
    	Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(), registro.getCampos().get("ID_DE_EMPLEADO"));
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), registro.getCampos().get("ID_DE_PROCESO"));
        param.put(GeneralParameterEnum.ESCALAFON.getName(), registro.getCampos().get("ESCALAFON"));
        param.put("ID_DE_CARGO", registro.getCampos().get("ID_DE_CARGO"));
        param.put("ID_DE_CATEGORIA", registro.getCampos().get("ID_DE_CATEGORIA"));
        param.put(GeneralParameterEnum.FECHAINICIO.getName(), SysmanFunciones.convertirAFechaCadena((Date)registro.getCampos().get("FECHAINICIO"),
                "dd/MM/yyyy"));

      
		Registro rs = RegistroConverter
		                    .toRegistro(requestManager.get(
		                                    UrlServiceUtil.getInstance()
		                                                    .getUrlServiceByUrlByEnumID(EncargosControladorUrlEnum.URL613007.getValue()
		                                                                    )
		                                                    .getUrl(),
		                                    param));
		
		if(rs != null) {
			
			dias = Integer.parseInt(rs.getCampos().get("DIAS").toString());
		}
		
	} catch (SystemException | ParseException e) {
		logger.error(e.getMessage(), e);
        JsfUtil.agregarMensajeError(e.getMessage());
	}
    	
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        if (validarFechaIniciaFinal()) {
            registro.getCampos().put(
                            EncargosControladorEnum.FECHAINICIO.getValue(),
                            null);
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            EncargosControladorEnum.IDIOMATB_TB75.getValue()));
            return false;
        }
        if (validarFechaFinalActo()) {
            registro.getCampos().put(
                            EncargosControladorEnum.FECHAACTO.getValue(), null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3727"));
            return false;
        }
        if (!validarFechaIniFinEntrePago()) {
            registro.getCampos().put(
                            EncargosControladorEnum.FECHAPAGO.getValue(), null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3729"));
            return false;
        }
        // Tipo Novedad --> 1;Encargo;2;Traslado
        registro.getCampos().put(
                        EncargosControladorEnum.TIPO_NOVEDAD.getValue(), "1");
        // Clase Encargo --> 1;Encargo De Funciones;2;Encargo De Cargo
        registro.getCampos().put(
                        EncargosControladorEnum.CLASE_ENCARGO.getValue(), "2");
        return true;
    }

    @Override
    public boolean insertarDespues() {
        try {
            accion = "v";
            Map<String, Object> parametros = new HashMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                            idEmpleado);
            parametros.put(GeneralParameterEnum.CARGO.getName(),
                            registro.getCampos().get("ID_DE_CARGO"));
            parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            Parameter parameter = new Parameter();
            parameter.setFields(parametros);
            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            EncargosControladorUrlEnum.URL7548
                                                            .getValue());

            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            parameter);
            diasIni = dias;

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove(EncargosControladorEnum.VALORGR.getValue());
        registro.getCampos().remove(
                        EncargosControladorEnum.DETALLE_ENCARGOC.getValue());

        if (ACCION_MODIFICAR.equals(accion)) {
            registro.getCampos().remove(
                            EncargosControladorEnum.TIPO_NOVEDAD.getValue());
            registro.getCampos().remove(
                            EncargosControladorEnum.CLASE_ENCARGO.getValue());
        }

        if (validarFechaIniciaFinal()) {
            registro.getCampos().put(
                            EncargosControladorEnum.FECHAINICIO.getValue(),
                            null);
            JsfUtil.agregarMensajeAlerta(idioma.getString(
                            EncargosControladorEnum.IDIOMATB_TB75.getValue()));
            return false;
        }
        if (validarFechaFinalActo()) {
            registro.getCampos().put(
                            EncargosControladorEnum.FECHAACTO.getValue(), null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3727"));
            return false;
        }
        if (!validarFechaIniFinEntrePago()) {
            registro.getCampos().put(
                            EncargosControladorEnum.FECHAPAGO.getValue(), null);
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3729"));
            return false;
        }
        if(accion.equals("m")) {
            diferirEnc(EncargosControladorEnum.BORRAR.getValue());
           
        	}
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return diferirEnc("");
    }

    @Override
    public boolean eliminarAntes() {

        boolean bolRta = false;
        try {

            boolean activo = ejbNominaCero.validarPeriodoActivoNomina(compania,
                            Integer.parseInt(procesoNomina),
                            Integer.parseInt(registro.getCampos()
                                            .get(GeneralParameterEnum.ANO
                                                            .getName())
                                            .toString()),
                            Integer.parseInt(registro.getCampos()
                                            .get(GeneralParameterEnum.MES
                                                            .getName())
                                            .toString()),
                            Integer.parseInt(registro.getCampos()
                                            .get(GeneralParameterEnum.PERIODO
                                                            .getName())
                                            .toString()));

            if (!activo) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB2553"));
                return bolRta;
            }
            bolRta = diferirEnc(EncargosControladorEnum.BORRAR.getValue());
            return bolRta;
        }
        catch (NumberFormatException | SystemException ex) {
            Logger.getLogger(EncargosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(
                                            EncargosControladorEnum.MSM_TRANS_INTERRUMPIDA
                                                            .getValue()),
                            ex.getMessage()));
        }

        return bolRta;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    public void ejecutarrcCerrar() {

        HashMap<String, Object> parametros = new HashMap<>();

        parametros.put("idEmpleado", idEmpleado);
        parametros.put("cedula", cedula);
        parametros.put("nombreEmpleado", nombreEmpleado);
        parametros.put("vacaciones", vacaciones);
        parametros.put("cesantias", cesantias);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(parametros);
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.NOVEDADES_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }

    public int getRta() {
        return rta;
    }

    public void setRta(int rta) {
        this.rta = rta;
    }

    public List<Registro> getListaEscalafon() {
        return listaEscalafon;
    }

    public void setListaEscalafon(List<Registro> listaEscalafon) {
        this.listaEscalafon = listaEscalafon;
    }

    public List<Registro> getListaIDdeCargo() {
        return listaIDdeCargo;
    }

    public void setListaIDdeCargo(List<Registro> listaIDdeCargo) {
        this.listaIDdeCargo = listaIDdeCargo;
    }

    public RegistroDataModelImpl getListaIDdeCategoria() {
        return listaIDdeCategoria;
    }

    public void setListaIDdeCategoria(
        RegistroDataModelImpl listaIDdeCategoria) {
        this.listaIDdeCategoria = listaIDdeCategoria;
    }

    public RegistroDataModelImpl getListaFechaPago() {
        return listaFechaPago;
    }

    public void setListaFechaPago(RegistroDataModelImpl listaFechaPago) {
        this.listaFechaPago = listaFechaPago;
    }

    public String getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(String idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getTituloForm() {
        return tituloForm;
    }

    public void setTituloForm(String tituloForm) {
        this.tituloForm = tituloForm;
    }

    public double getPorcGastos() {
        return porcGastos;
    }

    public void setPorcGastos(double porcGastos) {
        this.porcGastos = porcGastos;
    }

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

	public Date getFechaFinalActual() {
		return fechaFinalActual;
	}

	public void setFechaFinalActual(Date fechaFinalActual) {
		this.fechaFinalActual = fechaFinalActual;
	}

	public int getDiasIni() {
		return diasIni;
	}

	public void setDiasIni(int diasIni) {
		this.diasIni = diasIni;
	}
	
    public boolean isCargaAscenso() {
        return cargaAscenso;
    }

    public void setCargaAscenso(boolean cargaAscenso) {
        this.cargaAscenso = cargaAscenso;
    }

}
