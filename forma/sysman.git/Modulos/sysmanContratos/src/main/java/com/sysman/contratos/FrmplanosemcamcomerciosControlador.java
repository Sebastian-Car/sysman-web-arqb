package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.ejb.EjbContratosCeroRemote;
import com.sysman.contratos.enums.FrmplanosemcamcomerciosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.ArchivosBean;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author ybecerra
 * @version 1, 05/11/2015
 *
 * @author spina
 * @version 2, 08/08/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class FrmplanosemcamcomerciosControlador extends BeanBaseModal
{

    private final String compania;
    private final String nombre;
    private final String nit;
    private final String ciudad;
    private final String direccion;
    private final String modulo;
    private boolean secxreg;
    private String semestre;
    private String conproponentes;
    private String anio;
    private String camComercio;
    private Date fecha;

    private String funcionario;
    private String cargo;
    private List<Registro> listaAnio;
    private String usuario;

    @EJB
    private EjbContratosCeroRemote ejbContratosCero;

    /**
     * Creates a new instance of FrmplanosemcamcomerciosControlador
     */
    public FrmplanosemcamcomerciosControlador()
    {
        super();
        numFormulario = GeneralCodigoFormaEnum.FRMPLANOSEMCAMCOMERCIOS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        nombre = SessionUtil.getCompaniaIngreso().getNombre();
        nit = SessionUtil.getCompaniaIngreso().getNit();
        ciudad = SessionUtil.getCompaniaIngreso().getCodigoCiudad();
        direccion = SessionUtil.getCompaniaIngreso().getDireccion();
        modulo = SessionUtil.getModulo();
        usuario = SessionUtil.getUser().getCodigo();

        try
        {
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmplanosemcamcomerciosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        fecha = new Date();
        cargarListaAnio();
        abrirFormulario();
    }

    public void cargarListaAnio()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmplanosemcamcomerciosControladorUrlEnum.URL3214
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirImpresora()
    {
        // <CODIGO_DESARROLLADO>
        String archivoNuevo;
        try
        {
            archivoNuevo = ejbContratosCero.getPlanoComercio(compania, cargo,
                            Integer.parseInt(semestre), Integer.parseInt(anio),
                            camComercio,
                            Integer.parseInt(conproponentes),
                            secxreg ? 1 : 0, nit, nombre, ciudad, direccion,
                            funcionario, fecha, Integer.parseInt(modulo),
                            usuario);

            ArchivosBean.generarPlano("SIC.Dat", archivoNuevo);
        }
        catch (NumberFormatException | SystemException | IOException ex)
        {
            Logger.getLogger(FrmplanosemcamcomerciosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(
                                            idioma.getString(
                                                            "MSM_TRANS_INTERRUMPIDA"),
                                            ex.getMessage()));

        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public boolean getSecxreg()
    {
        return secxreg;
    }

    public void setSecxreg(boolean secxreg)
    {
        this.secxreg = secxreg;
    }

    public String getSemestre()
    {
        return semestre;
    }

    public void setSemestre(String semestre)
    {
        this.semestre = semestre;
    }

    public String getConproponentes()
    {
        return conproponentes;
    }

    public void setConproponentes(String conproponentes)
    {
        this.conproponentes = conproponentes;
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getCamComercio()
    {
        return camComercio;
    }

    public void setCamComercio(String camComercio)
    {
        this.camComercio = camComercio;
    }

    public Date getFecha()
    {
        return fecha;
    }

    public void setFecha(Date fecha)
    {
        this.fecha = fecha;
    }

    public String getFuncionario()
    {
        return funcionario;
    }

    public void setFuncionario(String funcionario)
    {
        this.funcionario = funcionario;
    }

    public String getCargo()
    {
        return cargo;
    }

    public void setCargo(String cargo)
    {
        this.cargo = cargo;
    }

    public List<Registro> getListaAnio()
    {
        return listaAnio;
    }

    public void setListaAnio(List<Registro> listaAnio)
    {
        this.listaAnio = listaAnio;
    }
}
