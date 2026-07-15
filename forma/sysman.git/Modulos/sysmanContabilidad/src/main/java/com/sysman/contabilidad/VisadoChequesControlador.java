package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.VisadoChequesControladorEnum;
import com.sysman.contabilidad.enums.VisadoChequesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
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
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author apineda
 * @version 1, 10/05/2016
 * @version 2, 12/04/2017 modificado por jcrodriguez descripcion:--
 * depuracion del controlador --creacion de dss o servicios para el
 * formulario
 * @version 3, 21/04/2017 mzanguna, cambio Ejb.
 */
@ManagedBean
@ViewScoped
public class VisadoChequesControlador extends BeanBaseModal {
    /**
     * variable que almacena la compa�ia
     */
    private final String compania;
    /**
     * variable que almacena el modulo
     */
    private final String modulo;
    /**
     * variable que almacena el cheque
     */
    private String cheque;
    /**
     * variable que almacena la cuenta bancaria
     */
    private String cuentaBancaria;
    /**
     * variable que almacena el a�o actual
     */
    private int anio = SysmanFunciones.getParteFecha(
                    new Date(),
                    Calendar.YEAR);
    /**
     * variable que almacena el archivo de descarga
     */
    private StreamedContent archivoDescarga;
    /**
     * lista cheque inicial
     */
    private List<Registro> listaChequeInicial;
    /**
     * lista las cuentas
     */
    private RegistroDataModelImpl listaCuenta;

    /**
     * Creates a new instance of VisadoChequesControlador
     */
    public VisadoChequesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.VISADO_CHEQUES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(VisadoChequesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * metodo que se llama al iniciar el formulario
     */
    @PostConstruct
    public void inicializar() {
        cargarListaCuenta();
        abrirFormulario();
    }

    /**
     * metodo que se llama al abrir el formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que carga la lista de cheques inicial
     */
    public void cargarListaChequeInicial() {
        Map<String, Object> param = new TreeMap<>();

        param.put(VisadoChequesControladorEnum.CUENTABANCARIA.getValue(),
                        cuentaBancaria);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaChequeInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            VisadoChequesControladorUrlEnum.URL4324
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que carga la lista de cuentas
     */
    public void cargarListaCuenta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        VisadoChequesControladorUrlEnum.URL4368
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(VisadoChequesControladorEnum.ANIO.getValue(), anio);

        listaCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * metodo que se llama cuando se oprime el boton pdf
     */
    public void oprimirImprimir() {
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
    }

    /**
     * metodo que se llama cuando se oprime el boton excel
     */
    public void oprimirExcel() {
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
    }

    /**
     * metodo que contiene la logica para genera los reportes en
     * formato excel y pdf
     *
     * @param formato
     */
    public void generaInforme(ReportesBean.FORMATOS formato) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put(VisadoChequesControladorEnum.CUENTABANCARIAS
                            .getValue(), cuentaBancaria);
            reemplazar.put(VisadoChequesControladorEnum.CHEQUE.getValue(),
                            cheque);
            parametros.put(VisadoChequesControladorEnum.PR_NRODOCUMENTO
                            .getValue(), cheque);

            Reporteador.resuelveConsulta(
                            VisadoChequesControladorEnum.NOMBREINFORME
                                            .getValue(),
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            VisadoChequesControladorEnum.NOMBREINFORME
                                            .getValue(),
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo apra seleccionar un registro de un combo grande
     *
     * @param event
     */
    public void seleccionarFilaCuenta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaBancaria = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        cheque = "";
        cargarListaChequeInicial();
    }

    /**
     * metodos get y set
     *
     * @return
     */
    public String getCheque() {
        return cheque;
    }

    public void setCheque(String cheque) {
        this.cheque = cheque;
    }

    public String getCuentaBancaria() {
        return cuentaBancaria;
    }

    public void setCuentaBancaria(String cuentaBancaria) {
        this.cuentaBancaria = cuentaBancaria;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public List<Registro> getListaChequeInicial() {
        return listaChequeInicial;
    }

    public void setListaChequeInicial(List<Registro> listaChequeInicial) {
        this.listaChequeInicial = listaChequeInicial;
    }

    public RegistroDataModelImpl getListaCuenta() {
        return listaCuenta;
    }

    public void setListaCuenta(RegistroDataModelImpl listaCuenta) {
        this.listaCuenta = listaCuenta;
    }
}
