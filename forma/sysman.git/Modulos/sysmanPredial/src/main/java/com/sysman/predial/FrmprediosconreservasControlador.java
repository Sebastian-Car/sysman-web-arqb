package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.FrmprediosconreservasControladorEnum;
import com.sysman.predial.enums.FrmprediosconreservasControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;

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
 * @author ybecerra
 * @version 1, 25/05/2016
 *
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 *
 * @author spina
 * @version 3, 05/07/2017 - refactorizo dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class FrmprediosconreservasControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String codigoInicial;
    private String codigoFinal;
    private String nombreCodigoInicial;
    private String nombreCodigoFinal;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCODIGOINICIAL;
    private RegistroDataModelImpl listaCODIGOFINAL;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmprediosconreservasControlador
     */
    public FrmprediosconreservasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMPREDIOSCONRESERVAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmprediosconreservasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCODIGOINICIAL();

        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCODIGOINICIAL()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmprediosconreservasControladorUrlEnum.URL4409
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listaCODIGOINICIAL = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaCODIGOFINAL()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmprediosconreservasControladorUrlEnum.URL5371
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(FrmprediosconreservasControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);

        listaCODIGOFINAL = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
            reemplazar.put("codigoFinal", "'" + codigoFinal + "'");

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_CODIGOS", idioma.getString("TB_TB3112")
                            .replace("s$codigoInicial$s", codigoInicial)
                            .replace("s$codigoFinal$s", codigoFinal));

            Reporteador.resuelveConsulta("000820LISTADOPREDIOSRESERVA",
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000820LISTADOPREDIOSRESERVA", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrmprediosconreservasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCODIGOINICIAL(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreCodigoInicial = registroAux.getCampos().get("NOMBRE").toString();
        codigoFinal = null;
        nombreCodigoFinal = null;
        cargarListaCODIGOFINAL();
    }

    public void seleccionarFilaCODIGOFINAL(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        nombreCodigoFinal = registroAux.getCampos().get("NOMBRE").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCODIGOINICIAL()
    {
        return listaCODIGOINICIAL;
    }

    public void setListaCODIGOINICIAL(RegistroDataModelImpl listaCODIGOINICIAL)
    {
        this.listaCODIGOINICIAL = listaCODIGOINICIAL;
    }

    public RegistroDataModelImpl getListaCODIGOFINAL()
    {
        return listaCODIGOFINAL;
    }

    public void setListaCODIGOFINAL(RegistroDataModelImpl listaCODIGOFINAL)
    {
        this.listaCODIGOFINAL = listaCODIGOFINAL;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
