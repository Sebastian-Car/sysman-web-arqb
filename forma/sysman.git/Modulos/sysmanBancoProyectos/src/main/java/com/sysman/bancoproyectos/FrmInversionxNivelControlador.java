package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmInversionxNivelControladorEnum;
import com.sysman.bancoproyectos.enums.FrmInversionxNivelControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 26/08/2015
 * 
 * @author jcrodriguez,Refactoring y creacion de Dss
 * @version 2, 20/09/2017
 */
@ManagedBean
@ViewScoped
public class FrmInversionxNivelControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private String anio;
    private String anio1;
    private String nivel;
    private String descripcion;
    private String nombreDescripcion;
    private List<Registro> listaAno;
    private List<Registro> listaAno1;
    private List<Registro> listaNIVEL;
    private List<Registro> listaDESCRIPCION;
    private StreamedContent archivoDescarga;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of FrmInversionxNivelControlador
     */
    public FrmInversionxNivelControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRM_INVERSIONX_NIVEL_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmInversionxNivelControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    private String getParametro(String nombre, boolean indMayus)
    {
        try
        {
            return ejbSysmanUtil.consultarParametro(compania, nombre, modulo, new Date(), indMayus);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return "";
    }

    @PostConstruct
    public void inicializar()
    {

        anio = getParametro("VIGENCIA GUBERNAMENTAL ACTUAL", true);

        anio1 = String.valueOf(Integer.parseInt(anio) + 3);

        cargarlistaAno();
        cargarlistaAno1();
        cargarListaNIVEL();
        cargarListaDESCRIPCION();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // heredado del bean base
    }

    public void cargarlistaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmInversionxNivelControladorUrlEnum.URL3606.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarlistaAno1()
    {
        listaAno1 = listaAno;
    }

    public void cargarListaNIVEL()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaNIVEL = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmInversionxNivelControladorUrlEnum.URL4368.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaDESCRIPCION()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(FrmInversionxNivelControladorEnum.NIVEL.getValue(), nivel);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmInversionxNivelControladorEnum.ANIO.getValue(), anio);
        param.put(FrmInversionxNivelControladorEnum.ANIO_FINAL.getValue(), anio1);

        try
        {
            listaDESCRIPCION = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(FrmInversionxNivelControladorUrlEnum.URL5239.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarVacios()
    {
        if (anio == null || "".equals(anio))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2324"));
            return false;
        }
        if (anio1 == null || "".equals(anio1))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2326"));
            return false;
        }
        return true;
    }

    private boolean validarVaciosUno()
    {
        if (SysmanFunciones.validarVariableVacio(nivel))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2327"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(descripcion))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2328"));
            return false;
        }
        return true;
    }

    private void generarInforme(FORMATOS formato)
    {

        if (!validarVacios() || !validarVaciosUno())
        {
            return;
        }

        archivoDescarga = null;
        Map<String, Object> parametros = new HashMap<>();

        try
        {
            HashMap<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("anioIncial", anio);
            reemplazos.put("anioFinal", anio1);
            reemplazos.put("nivel", nivel);
            reemplazos.put("descripcion", nombreDescripcion);

            Reporteador.resuelveConsulta(FrmInversionxNivelControladorEnum.REPORTE000190.getValue(), Integer.parseInt(modulo),
                            reemplazos,
                            parametros);
            parametros.put("PR_ANIO", anio);
            parametros.put("PR_ANIO1",
                            String.valueOf(Integer.parseInt(anio) + 3));
            Long contar = service.getConteoConsulta(parametros.get("PR_STRSQL").toString());
            if (contar > 0)
            {
                archivoDescarga = JsfUtil.exportarStreamed(FrmInversionxNivelControladorEnum.REPORTE000190.getValue(),
                                parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            else
            {
                JsfUtil.agregarMensajeError(idioma.getString("TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS2"));
            }

        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ", ex.getMessage(), " ",
                            FrmInversionxNivelControladorEnum.REPORTE000190.getValue()));
            Logger.getLogger(FrmInversionxNivelControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException | JRException | IOException ex)
        {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA), ex.getMessage()));
            Logger.getLogger(FrmInversionxNivelControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirComando82()
    {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>
        descripcion = null;
        if (!SysmanFunciones.validarVariableVacio(anio))
        {
            anio1 = String.valueOf(Integer.parseInt(anio) + 3);
        }
        cargarListaDESCRIPCION();
        if (!SysmanFunciones.validarVariableVacio(anio))
        {
            anio1 = String.valueOf(Integer.parseInt(anio) + 3);
        }
        if (Integer.parseInt(anio) > Integer.parseInt(anio1))
        {
            anio1 = "";
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB723"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno1()
    {
        // <CODIGO_DESARROLLADO>

        descripcion = null;
        cargarListaDESCRIPCION();
        if (!SysmanFunciones.validarVariableVacio(anio))
        {
            anio1 = String.valueOf(Integer.parseInt(anio) + 3);
        }
        if (Integer.parseInt(anio) > Integer.parseInt(anio1))
        {
            anio1 = "";
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB723"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarNIVEL()
    {
        // <CODIGO_DESARROLLADO>
        descripcion = null;
        cargarListaDESCRIPCION();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarDESCRIPCION()
    {

        // <CODIGO_DESARROLLADO>

        nombreDescripcion = descripcion;

        // </CODIGO_DESARROLLADO>
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getAnio1()
    {
        return anio1;
    }

    public void setAnio1(String anio1)
    {
        this.anio1 = anio1;
    }

    public String getNivel()
    {
        return nivel;
    }

    public void setNivel(String nivel)
    {
        this.nivel = nivel;
    }

    public String getDescripcion()
    {
        return descripcion;
    }

    public void setDescripcion(String descripcion)
    {
        this.descripcion = descripcion;
    }

    public List<Registro> getlistaAno()
    {
        return listaAno;
    }

    public void setlistaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    public List<Registro> getlistaAno1()
    {
        return listaAno1;
    }

    public void setlistaAno1(List<Registro> listaAno1)
    {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaNIVEL()
    {
        return listaNIVEL;
    }

    public void setListaNIVEL(List<Registro> listaNIVEL)
    {
        this.listaNIVEL = listaNIVEL;
    }

    public List<Registro> getListaDESCRIPCION()
    {
        return listaDESCRIPCION;
    }

    public void setListaDESCRIPCION(List<Registro> listaDESCRIPCION)
    {
        this.listaDESCRIPCION = listaDESCRIPCION;
    }

    public String getNombreDescripcion()
    {
        return nombreDescripcion;
    }

    public void setNombreDescripcion(String nombreDescripcion)
    {
        this.nombreDescripcion = nombreDescripcion;
    }

}