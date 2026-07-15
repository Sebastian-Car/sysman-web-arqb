package com.sysman.precontractual;

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
import com.sysman.precontractual.enums.FrmInfEtapasControladorEnum;
import com.sysman.precontractual.enums.FrmInfEtapasControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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
 * @version 1, 26/02/2016
 * @author jcrodriguez,Refactoring y Depuracion
 * @version 2, 28/08/2017
 */
@ManagedBean
@ViewScoped

public class FrmInfEtapasControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private String estudioIni;
    private String estudioFin;
    private String tipoestudio;
    private String nombreIni;
    private String nombreFin;
    private RegistroDataModelImpl listaCmbEstudio1;
    private RegistroDataModelImpl listaCmbEstudio2;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of FrmInfEtapasControlador
     */
    public FrmInfEtapasControlador()
    {
        super();
        numFormulario = GeneralCodigoFormaEnum.FRM_INF_ETAPAS_CONTROLADOR.getCodigo();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try
        {
            validarPermisos();
        }
        catch (SysmanException ex)
        {
            Logger.getLogger(FrmInfEtapasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init()
    {
        cargarListaCmbEstudio1();
        cargarListaCmbEstudio2();
        abrirFormulario();
    }

    public void cargarListaCmbEstudio1()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmInfEtapasControladorUrlEnum.URL2707.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmInfEtapasControladorEnum.TIPO_DIA.getValue(), tipoestudio);

        listaCmbEstudio1 = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, FrmInfEtapasControladorEnum.COD_ESTUDIO.getValue());
    }

    public void cargarListaCmbEstudio2()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(FrmInfEtapasControladorUrlEnum.URL3280.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmInfEtapasControladorEnum.TIPO_DIA.getValue(), tipoestudio);
        param.put(FrmInfEtapasControladorEnum.ESTUDIO_INI.getValue(), estudioIni);
        listaCmbEstudio2 = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, FrmInfEtapasControladorEnum.COD_ESTUDIO.getValue());

    }

    public void oprimirbtPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL);

        // </CODIGO_DESARROLLADO>
    }

    private String validarCadena(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    public void generaInforme(ReportesBean.FORMATOS formato)
    {
        String informe = "";
        try
        {
            HashMap<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("estudioIni", estudioIni);
            reemplazar.put("estudioFin", estudioFin);
            reemplazar.put(FrmInfEtapasControladorEnum.TIPOESTUDIO.getValue().toLowerCase(), tipoestudio);

            informe = FrmInfEtapasControladorEnum.REPORTE000548.getValue();

            Reporteador.resuelveConsulta(informe, Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(informe,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(
                            SysmanFunciones.concatenar(idioma.getString("MSM_INFORME_NO_EXISTE"), " ", ex.getMessage(), " ", informe));
            Logger.getLogger(FrmInfEtapasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FrmInfEtapasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void cambiarCmbTipoestudio()
    {
        // <CODIGO_DESARROLLADO>
        estudioIni = "";
        estudioFin = "";
        nombreIni = "";
        nombreFin = "";
        cargarListaCmbEstudio1();

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCmbEstudio1(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        estudioFin = "";
        nombreFin = "";
        estudioIni = validarCadena(registroAux.getCampos(), FrmInfEtapasControladorEnum.COD_ESTUDIO.getValue());

        nombreIni = validarCadena(registroAux.getCampos(), FrmInfEtapasControladorEnum.NOMBRE_ESTUDIO.getValue());
        cargarListaCmbEstudio2();
    }

    public void seleccionarFilaCmbEstudio2(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        estudioFin = validarCadena(registroAux.getCampos(), FrmInfEtapasControladorEnum.COD_ESTUDIO.getValue());

        nombreFin = validarCadena(registroAux.getCampos(), FrmInfEtapasControladorEnum.NOMBRE_ESTUDIO.getValue());
    }

    @Override
    public void abrirFormulario()
    {
        // HEREDADO DEL BEAN BASE
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getEstudioIni()
    {
        return estudioIni;
    }

    public void setEstudioIni(String estudioIni)
    {
        this.estudioIni = estudioIni;
    }

    public String getEstudioFin()
    {
        return estudioFin;
    }

    public void setEstudioFin(String estudioFin)
    {
        this.estudioFin = estudioFin;
    }

    public String getTipoestudio()
    {
        return tipoestudio;
    }

    public void setTipoestudio(String tipoestudio)
    {
        this.tipoestudio = tipoestudio;
    }

    public String getNombreIni()
    {
        return nombreIni;
    }

    public void setNombreIni(String nombreIni)
    {
        this.nombreIni = nombreIni;
    }

    public String getNombreFin()
    {
        return nombreFin;
    }

    public void setNombreFin(String nombreFin)
    {
        this.nombreFin = nombreFin;
    }

    public RegistroDataModelImpl getListaCmbEstudio1()
    {
        return listaCmbEstudio1;
    }

    public void setListaCmbEstudio1(RegistroDataModelImpl listaCmbEstudio1)
    {
        this.listaCmbEstudio1 = listaCmbEstudio1;
    }

    public RegistroDataModelImpl getListaCmbEstudio2()
    {
        return listaCmbEstudio2;
    }

    public void setListaCmbEstudio2(RegistroDataModelImpl listaCmbEstudio2)
    {
        this.listaCmbEstudio2 = listaCmbEstudio2;
    }

}
