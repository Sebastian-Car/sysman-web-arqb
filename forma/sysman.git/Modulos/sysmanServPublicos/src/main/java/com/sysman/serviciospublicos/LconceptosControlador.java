package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LconceptosControladorEnum;
import com.sysman.serviciospublicos.enums.LconceptosControladorUrlEnum;

import java.io.IOException;
import java.util.HashMap;
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
 * @author amonroy
 * @version 1, 16/08/2016
 * @author jcrodriguez
 * @version 2, 05/06/2017=>Refactoring, creacion de DSS y depuracion del controlador
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class LconceptosControlador extends BeanBaseModal
{
    /**
     * variable que almacena la compa�ia
     */
    private final String compania;
    /**
     * variable que almacena el codigo inicial
     */
    private String codigoInicial;
    /**
     * variable uqe lamcena el codigo final
     */
    private String codigoFinal;
    /**
     * variable que almacena el nombre del codigo inicial
     */
    private String nombreCodigoInicial;
    /**
     * variable que almacena el nombre del codigo final
     */
    private String nombreCodigoFinal;
    /**
     * variable que lista el codigo inicial
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * variable que lista el codigo final
     */
    private RegistroDataModelImpl listaCodigoFinal;
    /**
     * variable que almacena el archivo de descarga
     */
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of LconceptosControlador
     */
    public LconceptosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.LCONCEPTOS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(LconceptosControlador.class.getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init()
    {
        cargarListaCodigoInicial();
        abrirFormulario();
    }

    /**
     * metodo al abrir el formulario
     */
    @Override
    public void abrirFormulario()
    {
        // heredado del bean padre
    }

    /**
     * cargar la lista de codigo inicial
     */
    public void cargarListaCodigoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LconceptosControladorUrlEnum.URL4403.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * metodo que carga la lista de codigo final
     */
    public void cargarListaCodigoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LconceptosControladorUrlEnum.URL5084.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(LconceptosControladorEnum.CODIGOINICIAL.getValue(), codigoInicial);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * metodo que se llama al oprimir el boton pdf
     */
    public void oprimirImprimir()
    {
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
    }

    /**
     * metodo que se llama al oprimir el boton excel
     */
    public void oprimirImpresora()
    {
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
    }

    /**
     * metodo que contiene la logica para generar el reporte en formato pdf y excel
     */
    public void generarInforme(FORMATOS formato)
    {
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(LconceptosControladorEnum.REPORTE001039.getValue(), Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_CODIGOINICIAL", codigoInicial);
            parametros.put("PR_CODIGOFINAL", codigoFinal);
            archivoDescarga = JsfUtil.exportarStreamed(LconceptosControladorEnum.REPORTE001039.getValue(), parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (SysmanException | JRException | IOException ex)
        {
            JsfUtil.agregarMensajeError(idioma.getString("MSM_TRANS_INTERRUMPIDA") + " " + ex.getMessage());
            Logger.getLogger(LconceptosControlador.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * metodo que se llama al seleccionar un registro fila de un combo grande
     * 
     * @param event
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreCodigoInicial = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
        codigoFinal = nombreCodigoFinal = null;
        cargarListaCodigoFinal();

    }

    /**
     * metodo que se llama al seleccionar un registro fila de un combo grande
     * 
     * @param event
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreCodigoFinal = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    /**
     * METODOS GET Y SET
     * 
     * @return
     */
    public String getCodigoInicial()
    {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial)
    {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal()
    {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal)
    {
        this.codigoFinal = codigoFinal;
    }

    public String getNombreCodigoInicial()
    {
        return nombreCodigoInicial;
    }

    public void setNombreCodigoInicial(String nombreCodigoInicial)
    {
        this.nombreCodigoInicial = nombreCodigoInicial;
    }

    public String getNombreCodigoFinal()
    {
        return nombreCodigoFinal;
    }

    public void setNombreCodigoFinal(String nombreCodigoFinal)
    {
        this.nombreCodigoFinal = nombreCodigoFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public RegistroDataModelImpl getListaCodigoInicial()
    {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(RegistroDataModelImpl listaCodigoInicial)
    {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal()
    {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal)
    {
        this.listaCodigoFinal = listaCodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
