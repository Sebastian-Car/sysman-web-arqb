package com.sysman.transautomaticas;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.transautomaticas.ejb.EjbTransAutomaticasCeroRemote;
import com.sysman.transautomaticas.enums.ComprobantePptalAfectarControladorEnum;
import com.sysman.transautomaticas.enums.ComprobantePptalAfectarControladorUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

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

import org.primefaces.event.SelectEvent;

/**
 *
 * @modifier jgomez
 * @version 0, 07/11/2019 
 */
@ManagedBean
@ViewScoped
public class ComprobantePptalAfectarControlador extends BeanBaseDatosAcmeImpl
{

    private final String compania;
    private final String modulo;
    private String keyTipo;
    private String keyNumeroModelo;
    private int keyAnio;
    private BigInteger keyNumero;

    private RegistroDataModelImpl listaLista;

    /**
     * Crea una nueva instancia de DatostransaccionsControlador
     */
    @EJB
    private EjbTransAutomaticasCeroRemote ejbTransAutomaticas;

    /**
     * Identifica el registro seleccionado para cambiar el valor de saldo 
     */
    private Registro saldoRegistro;

    /**
     * Identifica si continua abriendo el formulario o no abre
     */
    private boolean vuelve;

    /**
     * parametro que guarda los datos de la transaccion
     */
    Map<String, Object> parametroTransaccion;

    /**
     * guarda el valor del documento de la transacci&oacute;n
     */
    double valor = 0;

    /**
     * Implementacion del EJB de SysmanUtil para obtener el valor de un parametro
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Define la URL que obtiene los registros que se cargan en la lista principal del formulario
     */
    UrlBean urlconsultaLista;
    /**
     * Almacena los parametros a enviar al servicio, dependiendo la URL que se defina en el atributo urlconsultaLista
     */
    Map<String, Object> paramConsultaLista;

    /**
     * Creates a new instance of ComprobantePptalAfectarControlador
     */
    public ComprobantePptalAfectarControlador()
    {
        super();
        saldoRegistro = new Registro();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            numFormulario = GeneralCodigoFormaEnum.COMPROBANTE_PPTAL_AFECTAR_CONTROLADOR
                            .getCodigo();

            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                parametroTransaccion = (Map<String, Object>) parametrosEntrada
                                .get("rowTransaccion");
                keyAnio = Integer.valueOf((String)parametrosEntrada
                                .get(GeneralParameterEnum.ANO.getName()));
                keyTipo = (String) parametrosEntrada
                                .get(GeneralParameterEnum.TIPO.getName());
                keyNumero = BigInteger.valueOf(Long.valueOf(parametrosEntrada
                                .get(GeneralParameterEnum.NUMERO.getName()).toString()));
                keyNumeroModelo = (String) parametrosEntrada.get("NUMEROMODELO");    
                valor = Double.valueOf((String)parametrosEntrada.get(GeneralParameterEnum.VALOR.getName()));
            }
            validarPermisos();
            paramConsultaLista = new TreeMap<>();

        }
        catch (Exception ex) {
            Logger.getLogger(ComprobantePptalAfectarControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            ejecutarrcCerrar();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        if (vuelve) {
            ejecutarrcCerrar();
        }
        if (inicializarComprobanteCntAfectarRES()) {
            cargarListaLista();
            abrirFormulario();
        }else {
            ejecutarrcCerrar();
        }
    }

    @Override
    public void abrirFormulario()
    {
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaLista()
    {
        try {
            listaLista = new RegistroDataModelImpl(urlconsultaLista.getUrl(),
                            urlconsultaLista.getUrlConteo().getUrl(),
                            paramConsultaLista,
                            false,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            ComprobantePptalAfectarControladorEnum.DETALLE_COMPROBANTE_PPTAL.getValue()),
                            true);     
            iniciarSeleccionados();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirAceptar()
    {
        List<Registro> listaSeleccionados = listaLista.getSeleccionados();
        String comprobante="";
        StringBuilder rta = new StringBuilder();
        if (listaSeleccionados!= null && (!(listaSeleccionados.isEmpty()))) {
            for (Registro reg : listaSeleccionados) {
                rta.append("''").append(reg.getCampos().get(ComprobantePptalAfectarControladorEnum.TIPO_CPTE
                                .getValue())).append("'',")
                .append(reg.getCampos().get(GeneralParameterEnum.COMPROBANTE.getName())).append(",")
                .append(reg.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName())).append(",")            
                .append(reg.getCampos().get(ComprobantePptalAfectarControladorEnum.SALDO
                                .getValue()))
                .append(SysmanConstantes.SEPARADOR_REG);

            }
            comprobante = rta.toString();
            comprobante = comprobante.substring(0, comprobante.length() - (SysmanConstantes.SEPARADOR_REG.length()));
        }
        try {
            ejbTransAutomaticas.guardarAfectacion(
                            compania,
                            keyAnio,
                            keyTipo,
                            keyNumeroModelo,
                            keyNumero,
                            comprobante,
                            SessionUtil.getUser().getCodigo());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Por defecto al seleccionar se asigna el valor total de la transacci&oacute;n 
     * @param event registro seleccionado desde la tabla
     */
    public void seleccionarFilaLista(SelectEvent event)
    {
        Registro registroSele= (Registro) event.getObject();
        registroSele.getCampos().put(ComprobantePptalAfectarControladorEnum.SALDO.getValue(), valor);        
    }

    /**
     * Metodo que se ejecuta al momento cambiar el saldo para guardar el registro a modificar
     * 
     * @param reg registro seleccionado para cambiar el saldo
     */
    public void cambiarSaldo(Registro reg) {        
        saldoRegistro = reg;        
    }

    /**
     * Actualiza el valor del saldo cambiado desde el dialog al la lista de seleccionados
     */
    public void aceptarCambiaSaldo() {
        List<Registro> listaSeleccion = listaLista.getSeleccionados();

        for (Registro registro : listaSeleccion) {
            if(registro.getLlave()== saldoRegistro.getLlave()) {
                registro.getCampos().put(ComprobantePptalAfectarControladorEnum.SALDO.getValue(), 
                                saldoRegistro.getCampos().get(ComprobantePptalAfectarControladorEnum.SALDO.getValue()));
            }
        }        
    }

    /**
     * Actualiza el valor del saldo cambiado desde el dialog al la lista de seleccionados
     */
    public void iniciarSeleccionados() {
        Map<String, Object> param = new TreeMap<>();
        param.put(ComprobantePptalAfectarControladorEnum.KEY_COMPANIA.getValue(), compania);
        param.put(ComprobantePptalAfectarControladorEnum.KEY_TIPO.getValue(), keyTipo);
        param.put(ComprobantePptalAfectarControladorEnum.KEY_NUMERO_MODELO.getValue(), keyNumeroModelo);
        param.put(ComprobantePptalAfectarControladorEnum.KEY_NUMERO.getValue(), keyNumero);
        param.put(ComprobantePptalAfectarControladorEnum.KEY_ANO.getValue(), keyAnio);


        listaLista.setSeleccionados(param,UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ComprobantePptalAfectarControladorUrlEnum.URL004
                                        .getValue())
                        .getUrl());


    }

    public RegistroDataModelImpl getListaLista()
    {
        return listaLista;
    }

    /**
     * Identifica la lista de detalles seleccionados
     * @param listaLista
     */
    public void setListaLista(RegistroDataModelImpl listaLista)
    {
        this.listaLista = listaLista;
    }

    /**
     * Trae el valor almacenado en la base de datos para el parametro ingresado.
     * 
     * @param nombreParametro
     * Nombre del parametro en la base de datos.
     * @param valorDefault
     * Valor por omision en caso de nulo.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, String valorDefault)
    {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, modulo, new Date(), true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

    /**
     * Define la consulta de la lista que debe cargarse para generar el comprobante presupuestal (Comprobante_cntAfectarRES).
     */
    private boolean inicializarComprobanteCntAfectarRES()
    {
        boolean salida= false;

        Map<String, Object> paramsReg = new TreeMap<>();
        paramsReg.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        paramsReg.put(GeneralParameterEnum.CODIGO.getName(), keyTipo);
        Registro reg;
        String claseAfectar = null;
        try {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ComprobantePptalAfectarControladorUrlEnum.URL009
                                                            .getValue())
                                            .getUrl(), paramsReg));

            if (reg != null) {
                claseAfectar = extraerString(reg.getCampos()
                                .get(ComprobantePptalAfectarControladorEnum.CLASEAFECTAR
                                                .getValue()));
            }
            else {
                return false;
            }

        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }

        String cadena = ComprobantePptalAfectarControladorEnum.HEREDAR_COMPROBANTES_VIGENCIA_FUTURA_ANIO_POSTERIOR.getValue();
        if (ComprobantePptalAfectarControladorEnum.NO.getValue().equals(getParametro(cadena, ComprobantePptalAfectarControladorEnum.NO.getValue()))) {
            cadena = ComprobantePptalAfectarControladorEnum.NO.getValue();
        }
        else {
            cadena = ComprobantePptalAfectarControladorEnum.NO.getValue();
        }
        try {
            if(claseAfectar!=null) {
                paramsReg.clear();
                paramsReg.put(ComprobantePptalAfectarControladorEnum.KEY_COMPANIA.getValue(), compania);
                paramsReg.put(ComprobantePptalAfectarControladorEnum.KEY_TIPO.getValue(), keyTipo);
                paramsReg.put(ComprobantePptalAfectarControladorEnum.KEY_NUMERO_MODELO.getValue(), keyNumeroModelo);
                paramsReg.put(ComprobantePptalAfectarControladorEnum.KEY_NUMERO.getValue(), keyNumero);
                paramsReg.put(ComprobantePptalAfectarControladorEnum.KEY_ANO.getValue(), keyAnio);

                Registro regTercero = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                ComprobantePptalAfectarControladorUrlEnum.URL008
                                                                .getValue())
                                                .getUrl(), paramsReg));            
                String tercero="";
                String sucursal="";
                if (regTercero != null) {
                    tercero = extraerString(regTercero.getCampos()
                                    .get(ComprobantePptalAfectarControladorEnum.TERCEROCOMPROBANTE
                                                    .getValue()));
                    sucursal = extraerString(regTercero.getCampos()
                                    .get(ComprobantePptalAfectarControladorEnum.SUCURSALCOMPROBANTE
                                                    .getValue()));                    
                }

                urlconsultaLista = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                ComprobantePptalAfectarControladorUrlEnum.URL010
                                                .getValue());
                paramConsultaLista.clear();
                paramConsultaLista.put(ComprobantePptalAfectarControladorEnum.KEY_COMPANIA.getValue(),
                                compania);
                paramConsultaLista.put(ComprobantePptalAfectarControladorEnum.KEY_ANO.getValue(), keyAnio);
                paramConsultaLista
                .put(ComprobantePptalAfectarControladorEnum.CLASEAFECTAR
                                .getValue(), claseAfectar);

                paramConsultaLista
                .put(ComprobantePptalAfectarControladorEnum.CADENA
                                .getValue(), cadena);
                paramConsultaLista
                .put(ComprobantePptalAfectarControladorEnum.FECHARES
                                .getValue(),
                                SysmanFunciones.convertirAFechaCadena(new Date()));
                paramConsultaLista.put(
                                ComprobantePptalAfectarControladorEnum.TERCEROCOMPROBANTE
                                .getValue(),
                                tercero);
                paramConsultaLista.put(
                                ComprobantePptalAfectarControladorEnum.SUCURSALCOMPROBANTE
                                .getValue(),
                                sucursal);                
                salida= true;                     
            }           
        }
        catch (SystemException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return salida;
    }

    public void ejecutarrcCerrar() {        
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("parametroTransaccion", parametroTransaccion);
        parametros.put(GeneralParameterEnum.ANO.getName(), keyAnio);
        parametros.put(GeneralParameterEnum.TIPO.getName(), keyTipo);
        parametros.put(GeneralParameterEnum.NUMERO.getName(), keyNumero.toString());

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(
                        GeneralCodigoFormaEnum.DATOS_TRANSACCIONS_CONTROLADOR
                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());        
    }


    /**
     * Extrae la cadena que representa al objeto, solo si es diferente de nulo.
     * 
     * @param object
     * Un Objeto
     * @return String que representa al objeto
     */
    private String extraerString(Object object)
    {
        return object != null ? object.toString() : null;
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
        return false;
    }

    @Override
    public boolean insertarDespues() {
        return false;
    }

    @Override
    public boolean actualizarAntes() {
        return false;
    }

    @Override
    public boolean actualizarDespues() {
        return false;
    }

    @Override
    public boolean eliminarAntes() {

        return false;
    }

    @Override
    public boolean eliminarDespues() {
        return false;
    }

    /**
     * @return the saldoRegistro
     */
    public Registro getSaldoRegistro() {
        return saldoRegistro;
    }

    /**
     * @param saldoRegistro the saldoRegistro to set
     */
    public void setSaldoRegistro(Registro saldoRegistro) {
        this.saldoRegistro = saldoRegistro;
    }

    /**
     * @return the valor
     */
    public double getValor() {
        return valor;
    }

    /**
     * @param valor the valor to set
     */
    public void setValor(double valor) {
        this.valor = valor;
    }


}
