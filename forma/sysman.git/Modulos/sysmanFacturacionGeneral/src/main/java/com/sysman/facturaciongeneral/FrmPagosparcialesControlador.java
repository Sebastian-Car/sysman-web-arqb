/*-
 * FrmPagosparcialesControlador.java
 *
 * 1.0
 *
 * 14/10/2021
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralABParcialesRemote;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCeroRemote;
import com.sysman.facturaciongeneral.enums.FrmPagosparcialesControladorEnum;
import com.sysman.facturaciongeneral.enums.FrmPagosparcialesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @version 1.0, 14/10/2021
 * @author kmartinez
 */
@ManagedBean
@ViewScoped
public class FrmPagosparcialesControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String modulo;

    private final String ano;

    private String tipoCobro;

    private String tipoFactura;

    private int diasMora;

    private double valorIntereses;

    private BigDecimal tasaInteres;

    private long codigoAbono;

    private String nroFactura;

    private String plantilla;
    
    private boolean abonosActivoPdf;

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;

    @EJB
    private EjbFacturacionGeneralCeroRemote ejbFacturacionGeneral;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbFacturacionGeneralABParcialesRemote ejbFacturacionGeneralABParciales;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaFactura;
    private RegistroDataModel listaFacturaE;
    private RegistroDataModelImpl listaPlantilla;
    /**
     * Esta variable se usa como auxiliar para subformularios y en esta se alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;

    private String nombrePlantilla;

    private Date fechaPlantilla;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmPagosparcialesControlador
     */
    public FrmPagosparcialesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        ano = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.ANIO.getValue());
        tipoCobro = (String) SessionUtil.getSessionVar(
                        ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());
        abonosActivoPdf = false;
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRM_PAGOSPARCIALES_CONTROLADOR
                            .getCodigo();
            ;
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {

        tabla = FrmPagosparcialesControladorEnum.SF_DETALLE_FACTURA.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaFactura();
        cargarListaFacturaE();
        cargarListaPlantilla();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base el valor de la consulta del formulario. Tambien carga la lista del formulario por primera vez
     */
    @Override
    public void reasignarOrigen()
    {

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPagosparcialesControladorUrlEnum.URL23222
                                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(FrmPagosparcialesControladorEnum.TIPOABONO.getValue(), tipoCobro);
        parametrosListado.put(FrmPagosparcialesControladorEnum.NROABONO.getValue(), String.valueOf(codigoAbono));
        parametrosListado.put(FrmPagosparcialesControladorEnum.TIPOFACTURA.getValue(), tipoFactura);
        parametrosListado.put(FrmPagosparcialesControladorEnum.NROFACTURA.getValue(), String.valueOf(nroFactura));       
        
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaFactura
     *
     */
    public void cargarListaFactura()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPagosparcialesControladorUrlEnum.URL23221.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmPagosparcialesControladorEnum.ANOCOBRO.getValue(), ano);
        param.put(FrmPagosparcialesControladorEnum.TIPOCOBRO.getValue(), tipoCobro);
        listaFactura = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.FACTURA.getName());
    }

    /**
     *
     * Carga la lista listaPlantilla
     *
     */
    public void cargarListaPlantilla()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmPagosparcialesControladorUrlEnum.URL104019.getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.TIPO.getName(), modulo);

        listaPlantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     *
     * Carga la lista listaFactura
     *
     */
    public void cargarListaFacturaE()
    {

    }
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    public void oprimircmdRegistrarPago()
    {
        try
        {
            long factura = Long.parseLong(SysmanFunciones.nvl(registro.getCampos()
                            .get(GeneralParameterEnum.FACTURA
                                            .getName()),
                            "0").toString());

            double vlrrAbono = Double.parseDouble(SysmanFunciones.nvl(registro.getCampos()
                            .get(FrmPagosparcialesControladorEnum.ABONO
                                            .getValue()),
                            "0").toString());
            Date fechaCorte = (Date) registro.getCampos().get(FrmPagosparcialesControladorEnum.FECHACORTE.getValue());
            
            Date fechaResolucion = (Date) registro.getCampos().get(FrmPagosparcialesControladorEnum.FECHARESOLUCION.getValue());
            
            Date fechaEjecutora = (Date) registro.getCampos().get(FrmPagosparcialesControladorEnum.FECHAEJECUTORA.getValue());
            
            String numeroResolucion = (String)registro.getCampos().get(FrmPagosparcialesControladorEnum.NUMERORESOLUCION.getValue());
            
            String numeroExpediente = (String)registro.getCampos().get(FrmPagosparcialesControladorEnum.NUMEROEXPEDIENTE.getValue());

            if (vlrrAbono > 0)
            {
                tasaInteres = ejbFacturacionGeneral.retonarTasaAnual(compania, Integer.parseInt(ano), tipoCobro, fechaCorte);
                ejbFacturacionGeneralABParciales.registrarPagoParcial(compania, Integer.parseInt(ano), tipoCobro,
                                BigInteger.valueOf(factura), fechaCorte, BigDecimal.valueOf(valorIntereses),
                                diasMora, tasaInteres, BigDecimal.valueOf(vlrrAbono), SessionUtil.getUser().getCodigo(),fechaResolucion,
                                fechaEjecutora, numeroResolucion, numeroExpediente);

                reasignarOrigen();
                generarReporte();
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));

            }
            else
            {
                JsfUtil.agregarMensajeInformativo("El valor del abono debe ser mayor a 0");
            }

        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    public void oprimirPagoPersuasivo()
    {
        try
        {
            long factura = Long.parseLong(SysmanFunciones.nvl(registro.getCampos()
                            .get(GeneralParameterEnum.FACTURA
                                            .getName()),
                            "0").toString());

            double vlrrAbono = Double.parseDouble(SysmanFunciones.nvl(registro.getCampos()
                            .get(FrmPagosparcialesControladorEnum.ABONO
                                            .getValue()),
                            "0").toString());
            Date fechaCorte = (Date) registro.getCampos().get(FrmPagosparcialesControladorEnum.FECHACORTE.getValue());
            
            Date fechaResolucion = (Date) registro.getCampos().get(FrmPagosparcialesControladorEnum.FECHARESOLUCION.getValue());
            
            Date fechaEjecutora = (Date) registro.getCampos().get(FrmPagosparcialesControladorEnum.FECHAEJECUTORA.getValue());
            
            String numeroResolucion = (String)registro.getCampos().get(FrmPagosparcialesControladorEnum.NUMERORESOLUCION.getValue());
            
            String numeroExpediente = (String)registro.getCampos().get(FrmPagosparcialesControladorEnum.NUMEROEXPEDIENTE.getValue());

			tasaInteres = ejbFacturacionGeneral.retonarTasaAnual(compania, Integer.parseInt(ano), tipoCobro,
					fechaCorte);
			ejbFacturacionGeneralABParciales.registrarPagoParcial(compania, Integer.parseInt(ano), tipoCobro,
					BigInteger.valueOf(factura), fechaCorte, BigDecimal.valueOf(valorIntereses), diasMora, tasaInteres,
					BigDecimal.valueOf(vlrrAbono), SessionUtil.getUser().getCodigo(), fechaResolucion, fechaEjecutora,
					numeroResolucion, numeroExpediente);
			
			BigInteger facturaAbono = new BigInteger(nroFactura);
			codigoAbono = ejbFacturacionGeneralABParciales.consultarCodigoAbono(compania, tipoCobro, facturaAbono).longValue();

			reasignarOrigen();
			
			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));            

        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    /**
     * 
     * Metodo ejecutado al oprimir el boton generarReporte
     * en la vista
     *
     * TODO DOCUMENTACION ADICIONAL
     *
     */
    public void oprimirgenerarReporte() {
    	//<CODIGO_DESARROLLADO>
    	try
        {
    		if (nroFactura == null || nroFactura.trim().isEmpty()) {
                JsfUtil.agregarMensajeAlerta("Debe seleccionar una factura antes de generar el reporte.");
                return;
            }

            BigInteger factura = new BigInteger(nroFactura.trim());

            boolean abonosActivo = ejbFacturacionGeneralABParciales.verificarAbonoActivo(
                    compania, Integer.parseInt(ano), tipoCobro, factura);

            if (abonosActivo) {
                generarReporte();
            } else {
                JsfUtil.agregarMensajeAlerta("Esta factura no cuenta con abonos activos");
                return;
            }
    	
    	
        }catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    	//</CODIGO_DESARROLLADO>
    }

    private void generarReporte()
    {
        try
        {
            String reporte = "002303LiquidacionMultasCorpo";
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("tipoCobro", tipoCobro);
            reemplazar.put("numero_factura", registro.getCampos().get(GeneralParameterEnum.FACTURA.getName()).toString());
            reemplazar.put("deudaCapital", registro.getCampos().get(FrmPagosparcialesControladorEnum.VALOR_ACTUALIZADO.getValue()).toString());
            reemplazar.put("interesCalculado", registro.getCampos().get(FrmPagosparcialesControladorEnum.INTERESES.getValue()).toString());
            reemplazar.put("diasMora", registro.getCampos().get(FrmPagosparcialesControladorEnum.DIASMORA.getValue()).toString());
            reemplazar.put("fechaPago", SysmanFunciones.convertirAFechaCadena((Date) 
                                            registro.getCampos().get(FrmPagosparcialesControladorEnum.FECHACORTE.getValue())));
            reemplazar.put("fechaVencimientoPago",
                            SysmanFunciones.convertirAFechaCadena((Date) SysmanFunciones.nvl(
                                            registro.getCampos().get(FrmPagosparcialesControladorEnum.FECHAULTIMOABONO.getValue()),
                                            registro.getCampos().get(FrmPagosparcialesControladorEnum.FECHAVENCIMIENTO.getValue()))));

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_ELABORO",
                            SysmanFunciones.concatenar(SessionUtil.getUser().getNombre1(), " ", SessionUtil.getUser().getApellido1()));
            parametros.put("PR_CARGOELABORO",
                            SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(
                                            compania, "CARGO ELABORO", SessionUtil.getModulo(), new Date(), true), " "));
            parametros.put("PR_REVISO",
                            SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(
                                            compania, "NOMBRE DE QUIEN REVISA", SessionUtil.getModulo(), new Date(), true), " "));
            parametros.put("PR_CARGOREVISO",
                            SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(
                                            compania, "CARGO DE QUIEN REVISA", SessionUtil.getModulo(), new Date(), true), " "));
            parametros.put("PR_NOMBRE REVISO EN FACTURA",
                            SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(
                                            compania, "SF NOMBRE REVISO EN FACTURA", SessionUtil.getModulo(), new Date(), true), " "));
            parametros.put("PR_CARGO VO.BO",
                            SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(
                                            compania, "CARGO Vo.Bo", SessionUtil.getModulo(), new Date(), true), " "));
            
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            
            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton PrepararPlantilla en la vista
     *
     *
     */
    public void oprimirPrepararPlantilla()
    {

        try
        {
            Map<String, Object> param = new HashMap<>();
            param.put("s$compania$s", compania);
            param.put("s$tipoAbono$s", tipoCobro);
            param.put("s$codigo$s", codigoAbono);
            param.put("s$anio$s", ano);
            param.put("s$interes$s", valorIntereses);
            param.put("s$diasMora$s", diasMora);
            param.put("s$factura$s", nroFactura);
            param.put("s$fechaVencimiento$s", SysmanFunciones.convertirAFechaCadena((Date) registro.getCampos().get("FECHA_VENCIMIENTO")));
            param.put("s$fechaCorte$s", SysmanFunciones.convertirAFechaCadena((Date) registro.getCampos().get("FECHA_CORTE")));

            /* Reemplazos de la consulta asociada a la plantilla */
            SessionUtil.setSessionVar("variablesConsultaWord", param);

            String[] claves = new String[3];
            claves[0] = "codigoPlantilla";
            claves[1] = "fechaPlantilla";
            claves[2] = "nombreDocDescarga";

            String[] valores = new String[3];
            valores[0] = plantilla;

            valores[1] = SysmanFunciones.formatearFecha(fechaPlantilla);

            valores[2] = nombrePlantilla;

            // RequestContext.getCurrentInstance().closeDialog(null);

            SessionUtil.cargarModalDatosFlashCerrar(Integer
                            .toString(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo()),
                            SessionUtil.getModulo(), claves, valores);

        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control fechaCorte
     *
     *
     */
    public void cambiarfechaCorte()
    {
        calcularValores();
    }   
    

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaFactura
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFactura(SelectEvent event)
    {
    	Registro registroAux = (Registro) event.getObject();

        try
        {

            registro.getCampos().put(GeneralParameterEnum.FACTURA.getName(),
                            registroAux.getCampos().get(GeneralParameterEnum.FACTURA.getName()));
            registro.getCampos().put(FrmPagosparcialesControladorEnum.FECHAEXPEDICION.getValue(),
                            registroAux.getCampos().get(FrmPagosparcialesControladorEnum.FECHAEXPEDICION.getValue()));
            registro.getCampos().put(FrmPagosparcialesControladorEnum.FECHAVENCIMIENTO.getValue(),
                            registroAux.getCampos().get(FrmPagosparcialesControladorEnum.FECHAVENCIMIENTO.getValue()));
            registro.getCampos().put(FrmPagosparcialesControladorEnum.VALOR.getValue(),
                            registroAux.getCampos().get(FrmPagosparcialesControladorEnum.VALORTOTAL.getValue()));
            registro.getCampos().put(FrmPagosparcialesControladorEnum.NUMERORESOLUCION.getValue(),
                    registroAux.getCampos().get(FrmPagosparcialesControladorEnum.NUMERORESOLUCION.getValue()));
            registro.getCampos().put(FrmPagosparcialesControladorEnum.NUMEROEXPEDIENTE.getValue(),
                    registroAux.getCampos().get(FrmPagosparcialesControladorEnum.NUMEROEXPEDIENTE.getValue()));
            registro.getCampos().put(FrmPagosparcialesControladorEnum.FECHARESOLUCION.getValue(),
                    registroAux.getCampos().get(FrmPagosparcialesControladorEnum.FECHARESOLUCION.getValue()));
            registro.getCampos().put(FrmPagosparcialesControladorEnum.FECHAEJECUTORA.getValue(),
                    registroAux.getCampos().get(FrmPagosparcialesControladorEnum.FECHAEJECUTORA.getValue()));

            tipoFactura = tipoCobro;
            nroFactura = SysmanFunciones
                            .nvl(registroAux.getCampos().get(GeneralParameterEnum.FACTURA.getName()), "")
                            .toString();
            BigInteger factura = new BigInteger(nroFactura);
            boolean abonosActivo = ejbFacturacionGeneralABParciales.verificarAbonoActivo(compania, Integer.parseInt(ano), tipoCobro,
                            factura);
            if (abonosActivo)
            {
            	JsfUtil.agregarMensajeInformativoDialogo(idioma.getString("TB_TB4396"));
            }
            if (!SysmanFunciones.validarCampoVacio(registroAux.getCampos(), GeneralParameterEnum.FACTURA.getName()))
            {
                
                Date fechaCorte;
                
                if ("".equals(SysmanFunciones
                                .nvl(registroAux.getCampos().get(FrmPagosparcialesControladorEnum.FECHACORTE.getValue()), ""))) {
                         fechaCorte = SysmanFunciones.hoy().getTime();
                }else {
                         fechaCorte = (Date) registroAux.getCampos().get(FrmPagosparcialesControladorEnum.FECHACORTE.getValue());
                }
                
                registro.getCampos().put(FrmPagosparcialesControladorEnum.FECHACORTE.getValue(), fechaCorte);

                Map<String, Object> params = new TreeMap<>();
                params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                params.put(GeneralParameterEnum.ANO.getName(), ano);
                params.put(GeneralParameterEnum.TIPO.getName(), tipoCobro);
                params.put(GeneralParameterEnum.FACTURA.getName(), nroFactura);

                Parameter parameter = new Parameter();
                parameter.setFields(params);

                Registro fact_abono = new Registro();
                fact_abono = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmPagosparcialesControladorUrlEnum.URL23223
                                                                .getValue())
                                .getUrl(), params));

                if (Objects.isNull(fact_abono))
                {
                    registro.getCampos().put(FrmPagosparcialesControladorEnum.FECHAULTIMOABONO.getValue(), null);
                }
                else
                {
                    registro.getCampos().put(FrmPagosparcialesControladorEnum.FECHAULTIMOABONO.getValue(),
                                    SysmanFunciones.convertirAFecha(fact_abono.getCampos().get("FECHA_ULTIMOABONO").toString()));
                }
                codigoAbono = ejbFacturacionGeneralABParciales.consultarCodigoAbono(compania, tipoCobro, factura).longValue();
                calcularValores();
                reasignarOrigen();

            }

        }
        catch (NumberFormatException | SystemException | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaFactura
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFacturaE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("FACTURA");
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaPlantilla
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPlantilla(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();
        plantilla = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO"));
        nombrePlantilla = SysmanFunciones.toString(registroAux.getCampos().get("NOMBRE"));
        fechaPlantilla = (Date) registroAux.getCampos().get("FECHAAUX");

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaPlantilla
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaPlantillaE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("CODIGO");
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    private void calcularValores()
    {
        
		// CC_1197 MROSERO Se extrae el año de la fecha de vencimineto y se evalua si
		// toma el año de la fecha de vencimiento
		// o de la fecha de solicitud para el proceso de la tasa de interes
        try
        {
			
			Calendar cal = Calendar.getInstance();
			int anio;
			String parametro = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"CALCULAR PAGO PARCIAL SOBRE FECHA DE CORTE", SessionUtil.getModulo(), new Date(), true),"NO"); // MOD JM CC 2158
        	
        	Date fechaVencimiento = (Date) registro.getCampos().get(FrmPagosparcialesControladorEnum.FECHAVENCIMIENTO.getValue());
            Date fechaCorte = (Date) registro.getCampos().get(FrmPagosparcialesControladorEnum.FECHACORTE.getValue());
            Date fechaUltimoAbono = (Date) registro.getCampos().get(FrmPagosparcialesControladorEnum.FECHAULTIMOABONO.getValue());
            Object valorObj = registro.getCampos().get(FrmPagosparcialesControladorEnum.VALOR.getValue());
            BigDecimal valorFactura = valorObj != null ? new BigDecimal(valorObj.toString()) : BigDecimal.ZERO;
			cal.setTime(fechaCorte);		//MOD JM CC 2158

			if (parametro.equals("SI")) {
				anio = cal.get(Calendar.YEAR);
			} else {
				anio = Integer.parseInt(ano);
			}

			tasaInteres = ejbFacturacionGeneral.retonarTasaDiaria(compania, anio, tipoCobro, fechaCorte);

            diasMora = 0;
            valorIntereses = 0;

            if (Objects.nonNull(fechaUltimoAbono))
            {
                if (!SysmanFunciones.comparaFechas(fechaCorte, fechaUltimoAbono))
                {
                    diasMora = SysmanFunciones.calcularDiferenciaDias(fechaUltimoAbono, fechaCorte);
                }
            }
            else
            {
                if (!SysmanFunciones.comparaFechas(fechaCorte, fechaVencimiento))
                {
                    diasMora = SysmanFunciones.calcularDiferenciaDias(fechaVencimiento, fechaCorte);
                }
            }
            
            valorIntereses = diasMora * tasaInteres.doubleValue() * valorFactura.doubleValue() + 50;
            valorIntereses = SysmanFunciones.redondear(valorIntereses, -2);
            
            double valorActualizado = 0;
            
            if (Objects.isNull(fechaUltimoAbono)) {
                
                     valorActualizado = valorFactura.doubleValue()+valorIntereses;
            }else {
                
                Map<String, Object> params = new TreeMap<>();
               
                params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                params.put(FrmPagosparcialesControladorEnum.TIPOABONO.getValue(), tipoCobro);
                params.put(FrmPagosparcialesControladorEnum.NROABONO.getValue(), String.valueOf(codigoAbono));
                params.put(FrmPagosparcialesControladorEnum.TIPOFACTURA.getValue(), tipoFactura);
                params.put(FrmPagosparcialesControladorEnum.NROFACTURA.getValue(), String.valueOf(nroFactura)); 

                Parameter parameter = new Parameter();
                parameter.setFields(params);

                Registro saldoAbono = new Registro();
                saldoAbono = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmPagosparcialesControladorUrlEnum.URL669006
                                                                .getValue())
                                .getUrl(), params));
                
                    double saldo = (double) saldoAbono.getCampos().get(FrmPagosparcialesControladorEnum.SALDO.getValue());
                    double saldoIntereses = (double) saldoAbono.getCampos().get("SALDOINTERESES");
                    
                    if (saldo > valorFactura.doubleValue()) {
                    
                        valorActualizado = valorFactura.doubleValue()+valorIntereses+(saldo - valorFactura.doubleValue());
                    
                    }else {
                        
                    	valorIntereses = diasMora * tasaInteres.doubleValue() * saldo + 50;
                        valorIntereses = SysmanFunciones.redondear(valorIntereses, -2);
                        valorActualizado = saldo + valorIntereses + saldoIntereses ;
                        
                    }
            }
            
            // DecimalFormat df = new DecimalFormat("###.##");
            // valorIntereses = Double.valueOf(df.format(valorIntereses));
            registro.getCampos().put(FrmPagosparcialesControladorEnum.DIASMORA.getValue(), diasMora);
            registro.getCampos().put(FrmPagosparcialesControladorEnum.INTERESES.getValue(), valorIntereses);
            registro.getCampos().put(FrmPagosparcialesControladorEnum.VALOR_ACTUALIZADO.getValue(), valorActualizado);

        }
        catch (ParseException | NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     *
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     *
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     *
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     *
     *
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se pueden remover valores auxiliares que no se desee o se deban enviar en el registro
     */
    @Override
    public void removerCombos()
    {
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del registro se usa cuando se desean agregar valores al registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro()
    {
        // TODO Auto-generated method stub
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaFactura
     *
     * @return listaFactura
     */
    public RegistroDataModelImpl getListaFactura()
    {
        return listaFactura;
    }

    /**
     * Asigna la lista listaFactura
     *
     * @param listaFactura
     * Variable a asignar en listaFactura
     */
    public void setListaFactura(RegistroDataModelImpl listaFactura)
    {
        this.listaFactura = listaFactura;
    }

    /**
     * Retorna la lista listaFactura
     *
     * @return listaFactura
     */
    public RegistroDataModel getListaFacturaE()
    {
        return listaFacturaE;
    }

    /**
     * Asigna la lista listaFactura
     *
     * @param listaFactura
     * Variable a asignar en listaFactura
     */
    public void setListaFacturaE(RegistroDataModel listaFacturaE)
    {
        this.listaFacturaE = listaFacturaE;
    }

    /**
     * Retorna la variable auxiliar
     *
     * @return auxiliar
     */
    public String getAuxiliar()
    {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     *
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaPlantilla()
    {
        return listaPlantilla;
    }

    public void setListaPlantilla(RegistroDataModelImpl listaPlantilla)
    {
        this.listaPlantilla = listaPlantilla;
    }

    public String getPlantilla()
    {
        return plantilla;
    }

    public void setPlantilla(String plantilla)
    {
        this.plantilla = plantilla;
    }

    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }
    
    public boolean getAbonosActivoPdf()
    {
        return abonosActivoPdf;
    }

    /**
     * Asigna la variable auxiliar
     *
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAbonosActivoPdf(boolean abonosActivoPdf)
    {
        this.abonosActivoPdf = abonosActivoPdf;
    }
}
