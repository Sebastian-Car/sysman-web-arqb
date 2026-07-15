package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.contabilidad.ejb.EjbContabilidadSeisRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadUnoRemote;
import com.sysman.contabilidad.enums.ComprobantecntbancosControladorEnum;
import com.sysman.contabilidad.enums.ComprobantecntbancosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author esarmiento
 * @version 1, 04/03/2016
 * @modified jguerrero
 * @version 2. 12/05/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 *
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio c�digo formulario
 * 
 * @modifier jgomez
 * @version 4, 02/08/2018 Se ajusta para que los totales del giro sean
 * de acuerdo al indicador que dice si el giro se realiza por todos
 * los terceros o por el tercero del comprobante a crear. Se ajusta
 * para que permite generar el comprobante por el tercero del header
 * sin necesidad de seleccionarlo en tercero.
 * 
 * @modifie gfigueredo
 * @version 4. 14/07/2021, Integrar controlador debido a que no se cargar 
 * correctamente la lista de cuentas.
 * @see #cargarListaCuenta()
 */
@ManagedBean
@ViewScoped

public class ComprobantecntbancosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String modulo;
    private final String ano;
    private String titulo;
    private String anoEgreso;
    private String tipoEgreso;
    private String numeroEgreso;
    private Map<String, Object> rowIdComprobante;
    private List<Registro> listaAfectar;

    String listaTerceros;
    String valorTerSeleccionados;
    private boolean editaCheque;
    private boolean visiblePorcentaje;
    private boolean visibleValor;
    private String porcentaje;
    private String valor;
    private String porcentajeRet;
    private String nombreCuenta;
    private String terceroComprobante;

    private String fechaComprobante;
    private String sucursalComprobante;
    private String tercero;
    private String mensajeDialogo;

    private double valorGiro;
    private int indice;
    private double subtotalValor;
    private double subPorcentaje;

    private String claseComprobante;
    private boolean visibleOrdenador;

    private RegistroDataModelImpl listaCuenta;
    private RegistroDataModelImpl listaCuentaE;
    private RegistroDataModelImpl listaReferencia;
    private RegistroDataModelImpl listaReferenciaE;
    private RegistroDataModelImpl listaCentroCosto;
    private RegistroDataModelImpl listaCentroCostoE;
    private String auxiliarComprobante;

    private final String visibleOrdenadorCons;
    private final String numeroCons;
    private final String anoEgresoCons;
    private final String codigoCons;

    private final String disponiblesCons;

    private final String numChequeraCons;
    private final String numActualCons;

    private final String cuentaCons;
    private final String valorCons;
    
    private final String codigoRefCons = "CODIGO";
    private final String nombreRefCons = "REFERENCIA";
    
    private final String codigoCcCons = "CODIGO";
    private final String nombreCcCons = "CENTRO_COSTO";

    private final String numChequeCons;
    private final String mensajeErrorCons;
    private final String nombreCons;
    private final String porcentajeRetencionCons;
    private final String porcentajeCons;
    private final String numeroEgresoCons;
    private final String tipoEgresoCons;
    private final String numeroMayusCons;
    private final String saldoCons;

    private String listaAfectados;
    private String listaComprobantes;
    private boolean visibleEliminarCompPptal;
    private boolean visibleRefCausaAuto;
    private String saldoCuenta; // CC 1390
    private String causacionAuto;
    private boolean bloqRefCausaAuto;
    private boolean bloqCenCausaAuto;

    /**
     * Variable con el fin de definir si el formulario fue abierto
     * desde el bot�n "Bancos" del formulario Comprobante_Cnt.
     */
    private boolean botonBancos;
    private boolean permiteAcme;
    private boolean cerrarSaldos;
    private int generoEgreso;

    private int contador;
    private boolean visibleValidaciones;
    private String mensajeVal;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbContabilidadSeisRemote ejbContabilidadSeis;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbContabilidadUnoRemote ejbContabilidadUno;

    /**
     * Creates a new instance of ComprobantecntbancosControlador
     */
    @SuppressWarnings("unchecked")
    public ComprobantecntbancosControlador() {
        super();
        saldoCons = "SALDO";
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        ano = String.valueOf(SysmanFunciones.ano(new Date()));
        editaCheque = false;
        porcentaje = "0";
        porcentajeRet = "0";
        valor = "0";
        tercero = null;

        visibleOrdenadorCons = "visibleOrdenador";
        numeroCons = "numero";
        anoEgresoCons = "ANOEGRESO";
        codigoCons = "CODIGO";

        disponiblesCons = "DISPONIBLES";

        numChequeraCons = "NUMCHEQUERA";
        numActualCons = "NUMACTUAL";

        cuentaCons = "CUENTA";
        valorCons = "VALOR";

        numChequeCons = "NUMEROCHEQUE";
        mensajeErrorCons = "MSM_TRANS_INTERRUMPIDA";
        nombreCons = "NOMBRE";
        porcentajeRetencionCons = "PORCENTAJERETENCION";
        porcentajeCons = "PORCENTAJE";
        numeroEgresoCons = "NUMEROEGRESO";
        tipoEgresoCons = "TIPOEGRESO";
        numeroMayusCons = "NUMERO";
        
        visibleRefCausaAuto = false;
        bloqRefCausaAuto = false;
        bloqCenCausaAuto = false;

        try {
            numFormulario = GeneralCodigoFormaEnum.COMPROBANTECNTBANCOS_CONTROLADOR
                            .getCodigo();

            Map<String, Object> parametros = SessionUtil.getFlashLocal();
            if (parametros != null) {
                anoEgreso = (String) parametros.get("anoEgreso");
                tipoEgreso = (String) parametros.get("tipoEgreso");
                numeroEgreso = (String) parametros.get("numeroEgreso");
                listaAfectar = (List<Registro>) parametros.get("listaAfectar");
                rowIdComprobante = (Map<String, Object>) parametros
                                .get("rowIdComprobante");

                terceroComprobante = (String) parametros
                                .get("terceroComprobante");

                auxiliarComprobante = (String) parametros
                                .get("auxiliarComprobante");
                fechaComprobante = (String) parametros.get("fechaComprobante");
                sucursalComprobante = (String) parametros
                                .get("sucursalComprobante");
                visibleOrdenador = (boolean) parametros
                                .get(visibleOrdenadorCons);
                claseComprobante = (String) parametros.get("claseComprobante");

                titulo = tipoEgreso + " No. " + numeroEgreso;
                botonBancos = (boolean) SysmanFunciones
                                .nvl(parametros.get("botonBancos"), false);

            }
            listaAfectados = listaComprobanteAfectar(listaAfectar);
            listaComprobantes = listaAfectados(listaAfectar);
            validarPermisos();

        }
        catch (NumberFormatException | SysmanException ex) {
            Logger.getLogger(ComprobantecntbancosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.COMPROBANTE_CNTBANCOS;
        tabla = "COMPROBANTE_CNTBANCOS";
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        cargarListaCuenta();
        cargarListaCuentaE();
        cargarListaCentroCosto();
        cargarListaCentroCostoE();
        cargarListaReferencia();
        cargarListaReferenciaE();
        abrirFormulario();
        calcularTotales();

    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(anoEgresoCons, anoEgreso);
        parametrosListado.put(tipoEgresoCons, tipoEgreso);
        parametrosListado.put(numeroEgresoCons, numeroEgreso);

    }

    public void oprimirbtnCargaTercero() {
        String parametros = calcularParametros();
        String[] campos = { "comprobantes" };
        String[] valores = { parametros };
        SessionUtil.cargarModalDatosFlashCerrar(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.TERCERO_EGRESO_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos,
                        valores);
    }

    private String calcularParametros() {

        StringBuilder parametros = new StringBuilder();

        String rta;
        for (Registro reg : listaAfectar) {

            parametros.append(reg.getCampos().get("ano"))
                            .append(reg.getCampos().get("tipo"))
                            .append(reg.getCampos().get(numeroCons))
                            .append(",");

        }
        rta = parametros.toString().substring(0,
                        parametros.toString().length() - 1);
        return rta;
    }

    public void cargarListaCuenta() {

        UrlBean urlBean = null;
        Map<String, Object> param = new TreeMap<>();

        try {
            String cuentasBancarias = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "CONTROLA CUENTA BANCARIA Y RUBRO PRESUPUESTAL",
                                            modulo, new Date(), true), "NO");

            if ("SI".equals(cuentasBancarias)) {
                urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                ComprobantecntbancosControladorUrlEnum.URL295
                                                                .getValue());
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.ANO.getName(), anoEgreso);
                param.put("LISTAAFECTAR", listaComprobantes);

            }
            else {

                urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                ComprobantecntbancosControladorUrlEnum.URL4980
                                                                .getValue());

                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(anoEgresoCons, anoEgreso);
            }

            listaCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, codigoCons);
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }

    public void cargarListaCuentaE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComprobantecntbancosControladorUrlEnum.URL4980
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(anoEgresoCons, anoEgreso);
        listaCuentaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }
    
    public void cargarListaReferencia() {
		Map<String, Object> param = new TreeMap<>();
		param.put(ComprobantecntbancosControladorEnum.PARAM0.getValue(),
				compania);
		param.put(ComprobantecntbancosControladorEnum.ESCENTRO.getValue(),
				"N");
		param.put(ComprobantecntbancosControladorEnum.LISTAAFECTAR.getValue(),
				listaAfectados);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ComprobantecntbancosControladorUrlEnum.URL39128
						.getValue());

		listaReferencia = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, codigoCons);
	}

	public void cargarListaReferenciaE() {
		listaReferenciaE = listaReferencia;
	}
	
	public void cargarListaCentroCosto() {
			UrlBean urlBean = UrlServiceUtil.getInstance()
	                .getUrlServiceByUrlByEnumID(
	                		ComprobantecntbancosControladorUrlEnum.URL39128
			                                                .getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(ComprobantecntbancosControladorEnum.PARAM0.getValue(),
					compania);
			param.put(ComprobantecntbancosControladorEnum.ESCENTRO.getValue(),
					"S");
			param.put(ComprobantecntbancosControladorEnum.LISTAAFECTAR.getValue(),
					listaAfectados);
			
			listaCentroCosto = new RegistroDataModelImpl(urlBean.getUrl(),
			                urlBean.getUrlConteo().getUrl(), param,
			                true, GeneralParameterEnum.CODIGO.getName());
	}
	
	public void cargarListaCentroCostoE() {
		listaCentroCostoE = listaCentroCosto;
	}

    private void validarCuentas() {
        try {
            Map<String, Object> paramentro = new TreeMap<>();
            paramentro.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            paramentro.put("ANO", anoEgreso);
            paramentro.put("TIPO", tipoEgreso);
            paramentro.put(numeroMayusCons, numeroEgreso);

            List<Registro> saldos;
            saldos = RegistroConverter.toListRegistro(
                            requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ComprobantecntbancosControladorUrlEnum.URL4374
                                                                                            .getValue())
                                                            .getUrl(),
                                            paramentro));

            for (Registro registro : saldos) {
                if (Double.parseDouble(registro.getCampos()
                                .get("VALORPAGAR")
                                .toString()) > Double.parseDouble(
                                                registro
                                                                .getCampos()
                                                                .get(saldoCons)
                                                                .toString())) {
                    String mensajeAlerta = idioma.getString("TB_TB3144");
                    mensajeAlerta = mensajeAlerta.replace("s$cuenta$s",
                                    registro.getCampos().get(cuentaCons)
                                                    .toString());
                    mensajeAlerta = mensajeAlerta.replace("s$saldo$s",
                                    String
                                                    .valueOf(Double.parseDouble(
                                                                    registro.getCampos()
                                                                                    .get(saldoCons)
                                                                                    .toString())));

                    JsfUtil.agregarMensajeAlerta(mensajeAlerta);

                }

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirAceptar() {
        try {
        	archivoDescarga = null;
            if (!validaciones()) {
                return;
            }


            int rta;

            rta = ejecutargenerarEgresos();

            if (rta == 2) {

                validarCuentas();

                return;
            }

            if (rta == 3) {
                visibleEliminarCompPptal = true;
                return;
            }

            if (rta == -1) {
                cerrarSaldos = true;
            }

            Map<String, Object> parametros = new HashMap<>();
            parametros.put(visibleOrdenadorCons, visibleOrdenador);
            SessionUtil.setFlash(parametros);
            JsfUtil.ejecutarJavaScript(
                            "$('#FRFR549\\\\:TBFR549\\\\:btCerrar').click();");
        }
        catch (NumberFormatException | SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * Validaciones en la vista
     *
     *
     */
    public void aceptarValidaciones() {
        // <CODIGO_DESARROLLADO>

        try {

            int rta = ejecutargenerarEgresos();

            if (rta == 3) {
                Registro reg;
                Map<String, Object> paramentro = new TreeMap<>();
                paramentro.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                paramentro.put("ANO", anoEgreso);
                paramentro.put("TIPO", tipoEgreso);
                paramentro.put(numeroMayusCons, numeroEgreso);

                reg = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                ComprobantecntbancosControladorUrlEnum.URL4374
                                                                                .getValue())
                                                .getUrl(), paramentro));

                String mensajeAlerta = idioma.getString("TB_TB3144");
                mensajeAlerta = mensajeAlerta.replace("s$cuenta$s",
                                reg.getCampos().get(cuentaCons).toString());
                mensajeAlerta = mensajeAlerta.replace("s$saldo$s", String
                                .valueOf(Double.parseDouble(reg.getCampos()
                                                .get(saldoCons).toString())));

                JsfUtil.agregarMensajeAlerta(mensajeAlerta);

            }
            if (rta == -1) {
                cerrarSaldos = true;
            }

        }
        catch (NumberFormatException | SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        visibleValidaciones = false;
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(visibleOrdenadorCons, visibleOrdenador);
        SessionUtil.setFlash(parametros);

        JsfUtil.ejecutarJavaScript("cerrarModalDefault();");

        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * EliminarComprobantePptal en la vista
     *
     *
     */
    public void aceptarEliminarComprobantePptal() {
        // <CODIGO_DESARROLLADO>

        try {
            ejbContabilidadUno.eliminarComprobantePresupuestal(compania,
                            Integer.parseInt(anoEgreso),
                            tipoEgreso,
                            new BigInteger(numeroEgreso),
                            SessionUtil.getUser().getCodigo());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        visibleEliminarCompPptal = false;
        // </CODIGO_DESARROLLADO>
    }

    public void retornarFormulariobtnCargaTercero() {
        /**
         * Obtener lista de terceros seleccionados
         */
        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null) {
            listaTerceros = (String) parametros.get("listaTercero");
            valorTerSeleccionados = parametros.get("valorTercero").toString();
            registro.getCampos().put(valorCons, valorTerSeleccionados);
            SessionUtil.cleanFlash();
        }
    }

    public void seleccionarFilaCuenta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cuentaCons,
                        registroAux.getCampos().get(codigoCons));
        registro.getCampos().put(nombreCons,
                        registroAux.getCampos().get(nombreCons));
        
        saldoCuenta = registroAux.getCampos().get(saldoCons).toString(); //CC 1390
        
        actualizar(registro);
        
        if(causacionAuto.equals("SI")) {
        	try {
        		bloqRefCausaAuto = false;
        		bloqCenCausaAuto = false;
        		Registro reg;
        		Map<String, Object> param = new TreeMap<>();
        		param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        		param.put(GeneralParameterEnum.ANO.getName(), anoEgreso);
        		param.put(GeneralParameterEnum.CODIGO.getName(), 
        				registroAux.getCampos().get(codigoCons));

        		reg = RegistroConverter.toRegistro(
        				requestManager.get(UrlServiceUtil.getInstance()
        						.getUrlServiceByUrlByEnumID(
        								ComprobantecntbancosControladorUrlEnum.URL16237
        								.getValue())
        						.getUrl(), param));

        		boolean referencia = (boolean) reg.getCampos().get("MAN_AUX_REF");
        		boolean centro = (boolean) reg.getCampos().get("MAN_CEN_CTO");

        		if (!referencia) {
        			bloqRefCausaAuto = true;
        			registro.getCampos().put(nombreRefCons,SysmanConstantes.CONS_REFERENCIA);
        		}
        		if (!centro) {
        			bloqCenCausaAuto = true;
        			registro.getCampos().put(nombreCcCons,SysmanConstantes.CONS_CENTRO);
        		}

        	}
        	catch (NumberFormatException | SystemException e) {
        		logger.error(e.getMessage(), e);
        		JsfUtil.agregarMensajeError(e.getMessage());
        	}
        }
    }

    public void seleccionarFilaCuentaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliarComprobante = registroAux.getCampos().get(codigoCons)
                        .toString();
        nombreCuenta = registroAux.getCampos().get(nombreCons).toString();
        
        saldoCuenta = registroAux.getCampos().get(saldoCons).toString(); //CC 1390
        
        if(causacionAuto.equals("SI")) {
        	try {
        		bloqRefCausaAuto = false;
        		bloqCenCausaAuto = false;
        		Registro reg;
        		Map<String, Object> param = new TreeMap<>();
        		param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        		param.put(GeneralParameterEnum.ANO.getName(), anoEgreso);
        		param.put(GeneralParameterEnum.CODIGO.getName(), 
        				registroAux.getCampos().get(codigoCons));

        		reg = RegistroConverter.toRegistro(
        				requestManager.get(UrlServiceUtil.getInstance()
        						.getUrlServiceByUrlByEnumID(
        								ComprobantecntbancosControladorUrlEnum.URL16237
        								.getValue())
        						.getUrl(), param));

        		boolean referencia = (boolean) reg.getCampos().get("MAN_AUX_REF");
        		boolean centro = (boolean) reg.getCampos().get("MAN_CEN_CTO");

        		if (!referencia) {
        			bloqRefCausaAuto = true;
        			registro.getCampos().put(nombreRefCons,SysmanConstantes.CONS_REFERENCIA);
        		}
        		if (!centro) {
        			bloqCenCausaAuto = true;
        			registro.getCampos().put(nombreCcCons,SysmanConstantes.CONS_CENTRO);
        		}

        	}
        	catch (NumberFormatException | SystemException e) {
        		logger.error(e.getMessage(), e);
        		JsfUtil.agregarMensajeError(e.getMessage());
        	}
        }
    }
    
    public void seleccionarFilareferencia(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
    	registro.getCampos().put(nombreRefCons,
    			registroAux.getCampos().get(codigoRefCons));
    	if(causacionAuto.equals("SI")) {
    		actualizarValoraux();
    	}

    }
    
    public void seleccionarFilaCentroCosto(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nombreCcCons,
                        registroAux.getCampos().get(codigoCcCons));
        if(causacionAuto.equals("SI")) {
    		actualizarValoraux();
    	}
    }
    
    public void seleccionarFilareferenciaE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nombreRefCons,
                        registroAux.getCampos().get(codigoRefCons));
        if(causacionAuto.equals("SI")) {
    		actualizarValoraux();
    	}
    }
    
    public void seleccionarFilaCentroCostoE(SelectEvent event) {
    	Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(nombreCcCons,
                        registroAux.getCampos().get(codigoCcCons));
        if(causacionAuto.equals("SI")) {
    		actualizarValoraux();
    	}
    }
    
    public void cambiarCuentaC(int rowNum) {
        Registro aux = listaInicial.getDatasource().get(rowNum % 10);
        actualizar(aux);
        aux.getCampos().put(
                        nombreCons,
                        nombreCuenta);
    }

    public void cambiarValorC(int rowNum) {
        double valorT = Double.parseDouble(listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos().get(valorCons)
                        .toString());
        if (Double.doubleToRawLongBits(valorT) != 0) {
            double porcentajeCal = SysmanFunciones
                            .redondear((valorT * 100) / valorGiro, 6);

            if (porcentajeCal > 100) {
                return;
            }

            listaInicial.getDatasource().get(rowNum %
                10).getCampos()
                            .put(porcentajeCons, porcentajeCal);
            listaInicial.getDatasource().get(rowNum %
                10).getCampos()
                            .put(porcentajeRetencionCons, porcentajeCal);
        }
    }

    public void cambiarPorcentaje() {
        double nuevoPorcentaje = Double.parseDouble(
                        registro.getCampos().get(porcentajeCons).toString());
        double valorCal = SysmanFunciones
                        .redondear((valorGiro * nuevoPorcentaje) / 100, 1);
        registro.getCampos().put(valorCons, valorCal);
        registro.getCampos().put(porcentajeRetencionCons, nuevoPorcentaje);

    }

    public void cambiarValor() {
        double nuevoValor = Double.parseDouble(
                        registro.getCampos().get(valorCons).toString());
        if (nuevoValor > valorGiro) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB391"));
            registro.getCampos().put(valorCons, valorGiro);
        }
        else {
            if (Double.doubleToRawLongBits(nuevoValor) != 0) {
                double porcentajeCal = SysmanFunciones
                                .redondear((nuevoValor * 100) / valorGiro, 6);
                registro.getCampos().put(porcentajeCons, porcentajeCal);
                registro.getCampos().put(porcentajeRetencionCons,
                                porcentajeCal);
            }
        }
    }

    public void cambiarPorcentajeC(int rowNum) {
        double porcent = Double.parseDouble(listaInicial.getDatasource()
                        .get(rowNum % 10).getCampos().get(porcentajeCons)
                        .toString());
        double valorCal;
        if (Double.doubleToRawLongBits(porcent) != 0) {
            valorCal = SysmanFunciones
                            .redondear(valorGiro * porcent, 1)
                / 100;
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(valorCons, valorCal);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(porcentajeRetencionCons, porcent);
        }
    }

    private void manejaChequera(Registro registro) {
        try {
            Registro reg;
            Map<String, Object> paramentro = new TreeMap<>();
            paramentro.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            paramentro.put(anoEgresoCons, anoEgreso);
            paramentro.put(GeneralParameterEnum.CUENTA.getName(),
                            registro.getCampos().get(cuentaCons));

            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ComprobantecntbancosControladorUrlEnum.URL4375
                                                                            .getValue())
                                            .getUrl(), paramentro));

            if (reg == null) {

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB399") + " "
                                    + registro.getCampos().get(cuentaCons)
                                    + " "
                                    + idioma.getString("TB_TB400"));
                registro.getCampos().put(numChequeCons, null);
                return;

            }
            if (reg.getCampos().get(numActualCons) == null) {
                registro.getCampos().put(numChequeCons,
                                reg.getCampos().get("NUMINICIAL"));
            }
            else {
                registro.getCampos().put(numChequeCons,
                                Integer.parseInt(reg.getCampos()
                                                .get(numActualCons)
                                                .toString())
                                    + 1);
                boolean chequeAnulado;
                do {

                    Map<String, Object> paramentros = new TreeMap<>();
                    paramentros.put(GeneralParameterEnum.COMPANIA
                                    .getName(),
                                    compania);
                    paramentros.put(anoEgresoCons, anoEgreso);
                    paramentros.put(GeneralParameterEnum.CUENTA
                                    .getName(),
                                    registro.getCampos()
                                                    .get(cuentaCons));
                    paramentros.put(numChequeraCons,
                                    reg.getCampos().get(
                                                    numChequeraCons));
                    paramentros.put(numChequeCons, reg.getCampos()
                                    .get(numChequeCons));

                    Registro regg = RegistroConverter.toRegistro(
                                    requestManager.get(UrlServiceUtil
                                                    .getInstance()
                                                    .getUrlServiceByUrlByEnumID(
                                                                    ComprobantecntbancosControladorUrlEnum.URL4376
                                                                                    .getValue())
                                                    .getUrl(),
                                                    paramentros));

                    chequeAnulado = verificarChequesAnulados(regg);
                }
                while (chequeAnulado);

            }
            editaCheque = true;
            String minimoCheque = ejbSysmanUtil.consultarParametro(compania,
                            "MINIMO NUMERO DE CHEQUES", modulo,
                            new Date(), true);

            if ((minimoCheque != null) && (Integer
                            .parseInt(minimoCheque) >= (Integer
                                            .parseInt(reg.getCampos()
                                                            .get(disponiblesCons)
                                                            .toString())
                                - 1))) {
                JsfUtil.agregarMensajeInformativo(idioma
                                .getString("TB_TB394")
                    + reg.getCampos().get(disponiblesCons));
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private boolean verificarChequesAnulados(Registro regg) {
        boolean rta = false;
        if (regg != null) {
            registro.getCampos().put(numChequeCons,
                            Integer.parseInt(
                                            registro.getCampos()
                                                            .get(numChequeCons)
                                                            .toString())
                                + 1);
            rta = true;
        }

        return rta;
    }

    public void actualizar(Registro registro) {
        try {

            String manejaChequera = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA CONTROL DE CHEQUERAS",
                                            modulo,
                                            new Date(), true), "NO")
                            .toString();

            if (!"SI".equals(manejaChequera)) {
                String condicion = "COMPANIA=''" + compania + "''  AND ANO= "
                    + anoEgreso + " AND CODIGO =''"
                    + registro.getCampos().get(cuentaCons) + "''";
                Long cheque = ejbSysmanUtil.generarConsecutivoConValorInicial(
                                "V_PLAN_CONTABLE", condicion, "CHEQUE", "0");

                registro.getCampos().put(numChequeCons, cheque);

            }
            else {

                manejaChequera(registro);
            }
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(mensajeErrorCons)
                                + ex.getMessage());
            Logger
                            .getLogger(ComprobantecntbancosControlador.class
                                            .getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    private void permisos() {

        Registro reg;
        Map<String, Object> paramentro = new TreeMap<>();
        paramentro.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        paramentro.put(anoEgresoCons, anoEgreso);
        paramentro.put(tipoEgresoCons, tipoEgreso);
        paramentro.put(numeroEgresoCons, numeroEgreso);

        try {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ComprobantecntbancosControladorUrlEnum.URL7075
                                                                            .getValue())
                                            .getUrl(), paramentro));
            if (reg == null) {
                permisos[1] = true;
                permisos[2] = true;
                permisos[3] = true;
            }
            /**
             * else { permisos[1] = false; permisos[2] = false;
             * permisos[3] = false; }*
             */
            permiteAcme = true;
            if (botonBancos) {
                permiteAcme = false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void giroBancoMundial() {
        String girarBancosMundial;
        try {
            girarBancosMundial = ejbSysmanUtil.consultarParametro(
                            compania,
                            "GIROS BANCO MUNDIAL", SessionUtil.getModulo(),
                            new Date(), true);

            if ((girarBancosMundial == null)
                || "NO".equals(girarBancosMundial)) {
                registro.getCampos().put(valorCons, valorGiro);
                registro.getCampos().put(porcentajeCons, "100");
                registro.getCampos().put(porcentajeRetencionCons, "100");
                visibleValor = true;
            }
            else {
                registro.getCampos().put(porcentajeCons, "50");
                registro.getCampos().put(porcentajeRetencionCons, "50");
                visibleValor = false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void abrirFormulario() {
        cerrarSaldos = true;
        tercero = "1";
        try {
            // <CODIGO_DESARROLLADO>

            permisos();

            String manejaRetenciones = ejbSysmanUtil.consultarParametro(
                            compania,
                            "MANEJA PAGO DE RETENCIONES POR BANCO", modulo,
                            new Date(), true);
            if ((manejaRetenciones == null) || "NO".equals(manejaRetenciones)) {
                visiblePorcentaje = false;
            }
            else {
                visiblePorcentaje = true;
            }
            registro.getCampos().put(porcentajeRetencionCons, "100");
            cambiarAltercero();

            giroBancoMundial();

            /**
             * Validar para que solo muestre los bancos que
             * corresponden al rubro presupuestal
             */
            String presupuestoBanco = ejbSysmanUtil.consultarParametro(compania,
                            "PRESUPUESTO CON EQUIVALENTE DE BANCOS EN EGRESO",
                            modulo,
                            new Date(), true);
            if ((presupuestoBanco != null)
                && "SI".equals(presupuestoBanco)) {
                String cuentas = null;
                Registro regCuentas;
                Map<String, Object> paramCuenta = new TreeMap<>();

                paramCuenta.put("ANO",
                                listaAfectar.get(0).getCampos().get("ano"));
                paramCuenta.put("TIPOCOMPROBANTE",
                                listaAfectar.get(0).getCampos().get("tipo"));
                paramCuenta.put("COMPROBANTE", listaAfectar.get(0).getCampos()
                                .get(numeroCons));

                regCuentas = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                ComprobantecntbancosControladorUrlEnum.URL4373
                                                                                .getValue())
                                                .getUrl(), paramCuenta));

                if (regCuentas.getCampos().get("LISTADO") != null) {
                    cuentas = SysmanFunciones.colocarComillas(regCuentas
                                    .getCampos().get("LISTADO").toString());
                }
                if (cuentas != null) {

                    UrlBean urlBean = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    ComprobantecntbancosControladorUrlEnum.URL7958
                                                                    .getValue());
                    Map<String, Object> param = new TreeMap<>();

                    param.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    param.put("CUENTAS", cuentas);
                    param.put(anoEgresoCons, anoEgreso);

                    listaCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                                    urlBean.getUrlConteo().getUrl(), param,
                                    true, codigoCons);

                    listaCuentaE = new RegistroDataModelImpl(urlBean.getUrl(),
                                    urlBean.getUrlConteo().getUrl(), param,
                                    true, codigoCons);

                }
            }
            causacionAuto = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
                    "MANEJA REFERENCIADO EN CAUSACION AUTOMATICA",
                    modulo,
                    new Date(), true), "NO");
            if(causacionAuto.equals("SI")) {
            	visibleRefCausaAuto = true;
            }
        }
        catch (SystemException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString(mensajeErrorCons)
                                + ex.getMessage());
            Logger.getLogger(ComprobantecntbancosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void ejecutarrcCerrar() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(visibleOrdenadorCons, visibleOrdenador);
        JsfUtil.ejecutarJavaScript("cambiarVisibilidadOrdenadorCompCnt("
            + !visibleOrdenador + ");");
        cerrarSaldos = true;
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
    	
        ///JM INI 14.04.2025 CC 1390
        String controlaSaldoEgreso = "";
		try {
			controlaSaldoEgreso = SysmanFunciones
			        .nvlStr(ejbSysmanUtil.consultarParametro(compania,
			                        "CONTROLAR SALDO DE CUENTA EN EGRESO",
			                        modulo, new Date(), true), "NO");
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        if ("SI".equals(controlaSaldoEgreso)) {
        	
        	double valorIngresar = Double.parseDouble(
                    registro.getCampos().get(valorCons).toString());
        	
        	double saldoCuentadb = Double.parseDouble(saldoCuenta.replace(",", "").trim());
        	
        	if( valorIngresar > saldoCuentadb) {
        	      String mensajeAlerta = idioma.getString("TB_TB4466");
                  mensajeAlerta = mensajeAlerta.replace("s$cuenta$s", registro.getCampos().get(cuentaCons).toString());
                  mensajeAlerta = mensajeAlerta.replace("s$saldo$s", String.valueOf(saldoCuenta));
                  JsfUtil.agregarMensajeAlerta(mensajeAlerta);
        		return false;
        	}
        	
        }
        //JM FIN 14.04.2025 CC 1390

    	registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ANO", anoEgreso);
        registro.getCampos().put("TIPO", tipoEgreso);
        registro.getCampos().put(numeroMayusCons, numeroEgreso);
        registro.getCampos().put("DESEMBOLSO", "0");
        
    	if (!validarTotal(false)) {
            return false;
        }
        if(visibleRefCausaAuto)
        {
        	try {
        		double valorIngresar = Double.parseDouble(
                        registro.getCampos().get(valorCons).toString());
    			String reporte = ejbContabilidadSeis.validarAuxiliaresEgresos(compania, Integer.parseInt(anoEgreso),
    					tipoEgreso, Long.parseLong(numeroEgreso), valorIngresar, 
    					registro.getCampos().get(GeneralParameterEnum.REFERENCIA.getName()).toString(),
    					registro.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()).toString(),
    					listaAfectados);
    			
    			if(reporte.isEmpty() || reporte.equals(" "))
    			{
    				return true;
    			}
    			else
    			{
    				JsfUtil.agregarMensajeErrorDialogo(reporte);
	    			return false;
    			}
    			
    		} catch (SystemException e) {
    			logger.error(e.getMessage(), e);
    			JsfUtil.agregarMensajeError(e.getMessage());
    		} 
        }  
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo permite validar que los datos registrados no
     * sobrepasen los datos del giro
     */
    private boolean validarTotal(boolean conSubtotal) {
        double valorIngresar = Double.parseDouble(
                        registro.getCampos().get(valorCons).toString());
        if (((valorGiro - Double.parseDouble(valor))
            + (conSubtotal ? subtotalValor : 0)) < valorIngresar) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB408"));
            return false;
        }

        if (registro.getCampos()
                        .get(porcentajeRetencionCons) != null) {
            double porcRet = Double.parseDouble(registro.getCampos()
                            .get(porcentajeRetencionCons).toString());
            if (((Double.parseDouble(porcentajeRet) + porcRet)
                - subPorcentaje) > 100) {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB410"));
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean insertarDespues() {
        calcularTotales();

        return true;
    }

    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        subtotalValor = Double.parseDouble(
                        registro.getCampos().get(valorCons).toString());
        subPorcentaje = Double.parseDouble(registro.getCampos()
                        .get(porcentajeRetencionCons).toString());
    }

    @Override
    public boolean actualizarAntes() {
        if (!validarTotal(true)) {
            return false;
        }
        registro.getCampos().remove(nombreCons);
        
        if(visibleRefCausaAuto)
        {
        	try {
        		double valorIngresar = Double.parseDouble(
                        registro.getCampos().get(valorCons).toString());
        		
        		valorIngresar = valorIngresar - subtotalValor;
    			String reporte = ejbContabilidadSeis.validarAuxiliaresEgresos(compania, Integer.parseInt(anoEgreso),
    					tipoEgreso, Long.parseLong(numeroEgreso), valorIngresar, 
    					registro.getCampos().get(GeneralParameterEnum.REFERENCIA.getName()).toString(),
    					registro.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()).toString(),
    					listaAfectados);
    			
    			if(reporte.isEmpty() || reporte.equals(" "))
    			{
    				return true;
    			}
    			else
    			{
    				JsfUtil.agregarMensajeErrorDialogo(reporte);
	    			return false;
    			}
    			
    		} catch (SystemException e) {
    			logger.error(e.getMessage(), e);
    			JsfUtil.agregarMensajeError(e.getMessage());
    		} 
        }  

        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        calcularTotales();

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
        calcularTotales();

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove("TIPO");

        registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());

    }

    @Override
    public void asignarValoresRegistro() {
        // Metodo heredado

        registro = new Registro();
        // cambiarAltercero();
        giroBancoMundial();

    }

    private void calcularTotales() {

        Map<String, Object> paramentro = new TreeMap<>();
        paramentro.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        paramentro.put(anoEgresoCons, anoEgreso);
        paramentro.put(tipoEgresoCons, tipoEgreso);
        paramentro.put(numeroEgresoCons, numeroEgreso);
        // cambiarAltercero();
        Registro reg;
        try {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ComprobantecntbancosControladorUrlEnum.URL4372
                                                                            .getValue())
                                            .getUrl(), paramentro));

            if (reg.getCampos() == null) {
                porcentaje = "0";
                porcentajeRet = "0";
                valor = "0";
            }
            else {
                porcentaje = reg.getCampos().get(porcentajeCons) == null ? "0"
                    : reg.getCampos().get(porcentajeCons).toString();
                porcentajeRet = reg.getCampos()
                                .get(porcentajeRetencionCons) == null
                                    ? "0"
                                    : reg.getCampos()
                                                    .get(porcentajeRetencionCons)
                                                    .toString();

                valor = reg.getCampos().get(valorCons) == null ? "0"
                    : reg.getCampos().get(valorCons).toString();

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarAltercero() {

        try {
            String clase;
            if ("I".equals(claseComprobante)) {
                clase = "(''C'')";
            }
            else {
                clase = "(''P'',''E'')";
            }
            if ("2".equals(tercero)) {
                valorGiro = (ejbContabilidadSeis.crearTotalListaAfec(compania,
                                listaAfectados, clase))
                                                .doubleValue();
            }
            else {
                valorGiro = (ejbContabilidadSeis.crearTotalListaAfecTer(
                                compania,
                                listaAfectados, terceroComprobante,
                                sucursalComprobante, clase))
                                                .doubleValue();
            }
            registro.getCampos().put(valorCons, valorGiro);
            calcularTotales();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public int getValorGiro() {
        return (int) (((valorGiro * 100) + 0.501) / 100);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public RegistroDataModelImpl getListaCuenta() {
        return listaCuenta;
    }

    public void setListaCuenta(RegistroDataModelImpl listaCuenta) {
        this.listaCuenta = listaCuenta;
    }

    public RegistroDataModelImpl getListaCuentaE() {
        return listaCuentaE;
    }

    public void setListaCuentaE(RegistroDataModelImpl listaCuentaE) {
        this.listaCuentaE = listaCuentaE;
    }

    public String getAuxiliar() {
        return auxiliarComprobante;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliarComprobante = auxiliar;
    }

    public String getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(String porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getPorcentajeRet() {
        return porcentajeRet;
    }

    public void setPorcentajeRet(String porcentajeRet) {
        this.porcentajeRet = porcentajeRet;
    }

    public boolean isEditaCheque() {
        return editaCheque;
    }

    public void setEditaCheque(boolean editaCheque) {
        this.editaCheque = editaCheque;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public boolean isVisiblePorcentaje() {
        return visiblePorcentaje;
    }

    public void setVisiblePorcentaje(boolean visiblePorcentaje) {
        this.visiblePorcentaje = visiblePorcentaje;
    }

    public boolean isVisibleValor() {
        return visibleValor;
    }

    public void setVisibleValor(boolean visibleValor) {
        this.visibleValor = visibleValor;
    }

    public String getTercero() {
        return tercero;
    }

    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    public String getMensajeDialogo() {
        return mensajeDialogo;
    }

    public void setMensajeDialogo(String mensajeDialogo) {
        this.mensajeDialogo = mensajeDialogo;
    }

    public boolean isPermiteAcme() {
        return permiteAcme;
    }

    public void setPermiteAcme(boolean permiteAcme) {
        this.permiteAcme = permiteAcme;
    }

    public boolean isCerrarSaldos() {
        return cerrarSaldos;
    }

    public void setCerrarSaldos(boolean cerrarSaldos) {
        this.cerrarSaldos = cerrarSaldos;
    }

    public boolean isVisibleValidaciones() {
        return visibleValidaciones;
    }

    public void setVisibleValidaciones(boolean visibleValidaciones) {
        this.visibleValidaciones = visibleValidaciones;
    }

    public String getMensajeVal() {
        return mensajeVal;
    }

    public void setMensajeVal(String mensajeVal) {
        this.mensajeVal = mensajeVal;
    }

    public boolean isVisibleEliminarCompPptal() {
        return visibleEliminarCompPptal;
    }

    public void setVisibleEliminarCompPptal(boolean visibleEliminarCompPptal) {
        this.visibleEliminarCompPptal = visibleEliminarCompPptal;
    }

    /**
     *
     * Metodo invocado al ejecutar el comando remoto
     * cerrarSaldosRemoto en la vista
     *
     *
     */
    public void ejecutarcerrarSaldosRemoto() {
        // <CODIGO_DESARROLLADO>
        cerrarSaldos = true;
        // </CODIGO_DESARROLLADO>
    }

    private String listaComprobanteAfectar(List<Registro> listaAfectar) {
        String comprobante;
        StringBuilder rta = new StringBuilder();

        for (Registro reg : listaAfectar) {
            rta.append("(''" + reg.getCampos().get("ano") + "'',")
                            .append("''" + reg.getCampos().get("tipo") + "'',")
                            .append("''" + reg.getCampos().get(numeroCons)
                                + "'')")
                            .append(",");
            contador++;
        }
        comprobante = rta.toString();
        comprobante = comprobante.substring(0, comprobante.length() - 1);

        return comprobante;
    }

    private String listaAfectados(List<Registro> listaAfectar) {
        String comprobante;
        StringBuilder rta = new StringBuilder();

        for (Registro reg : listaAfectar) {
            rta.append("#" + reg.getCampos().get("ano") + ",")
                            .append("" + reg.getCampos().get("tipo") + ",")
                            .append("" + reg.getCampos().get(numeroCons)
                                + "#")
                            .append(",");
            contador++;
        }
        comprobante = rta.toString();
        comprobante = comprobante.substring(0, comprobante.length() - 1);

        return comprobante;
    }

    private boolean validaciones() {
        boolean rta = true;

        if ((contador > 1) && (Double.parseDouble(valor) > valorGiro)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3189"));
            return false;
        }

        if (Double.parseDouble(valor) > valorGiro) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3138"));
            return false;
        }
        if (Double.parseDouble(valor) < valorGiro) {
            mensajeVal = idioma.getString("TB_TB3139");
            visibleValidaciones = true;
            return false;
        } 
        return rta;
    } 
    
    private int ejecutargenerarEgresos()
                    throws SystemException, ParseException {
        int egreso;

        if (listaTerceros != null) {
            generoEgreso = ejbContabilidadSeis.generarEgresos(compania,
                            Integer.parseInt(anoEgreso), tipoEgreso,
                            Long.parseLong(numeroEgreso),
                            listaAfectados,
                            listaTerceros,
                            true,
                            SysmanFunciones.convertirAFecha(
                                            fechaComprobante),
                            claseComprobante, terceroComprobante,
                            sucursalComprobante,
                            BigDecimal.valueOf(Double
                                            .parseDouble(valor)),
                            SessionUtil.getUser().getCodigo(), contador);

        }
        else {
            generoEgreso = ejbContabilidadSeis.generarEgresos(compania,
                            Integer.parseInt(anoEgreso), tipoEgreso,
                            Long.parseLong(numeroEgreso),
                            listaAfectados,
                            "(''" + terceroComprobante + "'',''"
                                + sucursalComprobante + "'')",
                            false,
                            SysmanFunciones.convertirAFecha(
                                            fechaComprobante),
                            claseComprobante, terceroComprobante,
                            sucursalComprobante,
                            BigDecimal.valueOf(Double
                                            .parseDouble(valor)),

                            SessionUtil.getUser().getCodigo(), contador);

        }

        egreso = generoEgreso;
        return egreso;

    }

    public void cerrarFormulario() {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("visibleOrdenador", visibleOrdenador);
        SessionUtil.setFlash(parametros);

        cerrarSaldos = true;
    }
    
    public void actualizarValoraux() {
        try {

            String referencia = registro.getCampos().get(GeneralParameterEnum.REFERENCIA.getName()) != null
                    ? registro.getCampos().get(GeneralParameterEnum.REFERENCIA.getName()).toString()
                    : "";

            String centroCosto = registro.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()) != null
                    ? registro.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()).toString()
                    : "";

            BigDecimal valor = ejbContabilidadSeis.validarSaldoAuxCxp(
                    compania,
                    Integer.parseInt(anoEgreso),
                    tipoEgreso,
                    Long.parseLong(numeroEgreso),
                    referencia,
                    centroCosto,
                    listaAfectados
            );

            registro.getCampos().put(GeneralParameterEnum.VALOR.getName(), valor);

            double valorDouble = valor.doubleValue();

            double porcentajeCal = SysmanFunciones
            		.redondear((valorDouble * 100) / valorGiro, 6);
            registro.getCampos().put(porcentajeCons, porcentajeCal);
            registro.getCampos().put(porcentajeRetencionCons,
            		porcentajeCal);

        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

	public RegistroDataModelImpl getListareferencia() {
		return listaReferencia;
	}

	public void setListareferencia(RegistroDataModelImpl listareferencia) {
		this.listaReferencia = listareferencia;
	}

	public RegistroDataModelImpl getListareferenciaE() {
		return listaReferenciaE;
	}

	public void setListareferenciaE(RegistroDataModelImpl listareferenciaE) {
		this.listaReferenciaE = listareferenciaE;
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

	public boolean isVisibleRefCausaAuto() {
		return visibleRefCausaAuto;
	}

	public void setVisibleRefCausaAuto(boolean visibleRefCausaAuto) {
		this.visibleRefCausaAuto = visibleRefCausaAuto;
	}
	
	public boolean isBloqRefCausaAuto() {
        return bloqRefCausaAuto;
    }

    public void setBloqRefCausaAuto(boolean bloqRefCausaAuto) {
        this.bloqRefCausaAuto = bloqRefCausaAuto;
    }

    public boolean isBloqCenCausaAuto() {
        return bloqCenCausaAuto;
    }

    public void setBloqCenCausaAuto(boolean bloqCenCausaAuto) {
        this.bloqCenCausaAuto = bloqCenCausaAuto;
    }   

}
