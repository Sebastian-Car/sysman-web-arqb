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
import com.sysman.predial.enums.FrmMorososPersonaControladorEnum;
import com.sysman.predial.enums.FrmMorososPersonaControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;

import java.io.FileNotFoundException;
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
 * @author apineda
 * @version 1, 17/06/2016
 * @author jcrodriguez=>Depuracion del controlador y Refactoring
 * @version 1, 17/06/2016
 */
@ManagedBean
@ViewScoped
public class FrmMorososPersonaControlador extends BeanBaseModal
{
    /**
     * variable que alamcena la compañia
     */
    private final String compania;
    private final String modulo;
    private Boolean resumen;
    private String personaInicial;
    private String personaFinal;
    private String nombrepersonaInicial;
    private String nombrepersonaFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listacmbPersonaInicial;
    private RegistroDataModelImpl listacmbPersonaFinal;

    /**
     * Creates a new instance of FrmMorososPersonaControlador
     */
    public FrmMorososPersonaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRM_MOROSOS_PERSONA_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmMorososPersonaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {

        cargarListacmbPersonaInicial();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListacmbPersonaInicial()
    {
        HashMap<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmMorososPersonaControladorUrlEnum.URL2864.getValue());
        listacmbPersonaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, FrmMorososPersonaControladorEnum.CODIGO.getValue());

    }

    public void cargarListacmbPersonaFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmMorososPersonaControladorUrlEnum.URL3781.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(), SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(FrmMorososPersonaControladorEnum.NIT_INICIAL.getValue(), personaInicial);

        listacmbPersonaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, FrmMorososPersonaControladorEnum.CODIGO.getValue());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimircmdgenerarPDF()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generaInforme(ReportesBean.FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimircmdGenerarExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generaInforme(ReportesBean.FORMATOS.EXCEL);

        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato)
    {

        String informe = FrmMorososPersonaControladorEnum.REPORTE000913.getValue();
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("numeroOrden",
                            "'" + SysmanConstantes.NUMERO_ORDEN_PREDIAL + "'");
            reemplazar.put("personaInicial", "'" + personaInicial + "'");
            reemplazar.put("personaFinal", "'" + personaFinal + "'");
            reemplazar.put("nombreInicial", "'" + nombrepersonaInicial + "'");
            reemplazar.put("nombreFinal", "'" + nombrepersonaFinal + "'");

            parametros.put(FrmMorososPersonaControladorEnum.PR_MOSTRARDETALLE.getValue(), resumen ? false : true);
            parametros.put(FrmMorososPersonaControladorEnum.PR_NOMBRECOMPANIA.getValue(),
                            SessionUtil.getCompaniaIngreso().getNombre());

            Reporteador.resuelveConsulta(informe, Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex)
        {
            StringBuilder concatenar = new StringBuilder();
            concatenar.append(idioma.getString("MSM_INFORME_NO_EXISTE"));
            concatenar.append(" ");
            concatenar.append(ex.getMessage());
            concatenar.append(" ");
            concatenar.append(informe);
            JsfUtil.agregarMensajeInformativo(concatenar.toString());
            Logger.getLogger(FrmMorososPersonaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        catch (JRException | IOException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrmMorososPersonaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + idioma.getString("MSM_INFORME_NO_EXISTE"));
        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilacmbPersonaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        personaInicial = registroAux.getCampos().get(FrmMorososPersonaControladorEnum.NIT.getValue()).toString();
        nombrepersonaInicial = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
        personaFinal = "";
        nombrepersonaFinal = "";
        cargarListacmbPersonaFinal();
    }

    public void seleccionarFilacmbPersonaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        personaFinal = registroAux.getCampos().get(FrmMorososPersonaControladorEnum.NIT.getValue()).toString();
        nombrepersonaFinal = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getPersonaInicial()
    {
        return personaInicial;
    }

    public Boolean getResumen()
    {
        return resumen;
    }

    public void setResumen(Boolean resumen)
    {
        this.resumen = resumen;
    }

    public void setPersonaInicial(String personaInicial)
    {
        this.personaInicial = personaInicial;
    }

    public String getPersonaFinal()
    {
        return personaFinal;
    }

    public void setPersonaFinal(String personaFinal)
    {
        this.personaFinal = personaFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public String getNombrepersonaInicial()
    {
        return nombrepersonaInicial;
    }

    public void setNombrepersonaInicial(String nombrepersonaInicial)
    {
        this.nombrepersonaInicial = nombrepersonaInicial;
    }

    public String getNombrepersonaFinal()
    {
        return nombrepersonaFinal;
    }

    public void setNombrepersonaFinal(String nombrepersonaFinal)
    {
        this.nombrepersonaFinal = nombrepersonaFinal;
    }

    public RegistroDataModelImpl getListacmbPersonaInicial()
    {
        return listacmbPersonaInicial;
    }

    public void setListacmbPersonaInicial(
        RegistroDataModelImpl listacmbPersonaInicial)
    {
        this.listacmbPersonaInicial = listacmbPersonaInicial;
    }

    public RegistroDataModelImpl getListacmbPersonaFinal()
    {
        return listacmbPersonaFinal;
    }

    public void setListacmbPersonaFinal(
        RegistroDataModelImpl listacmbPersonaFinal)
    {
        this.listacmbPersonaFinal = listacmbPersonaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
