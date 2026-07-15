package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadCeroRemote;
import com.sysman.contabilidad.enums.ContabilizarBalanceInicialControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.Formulario;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 *
 * @author jrodrigueza
 * @version 1, 04/03/2016
 *
 * Revision Sonar y Refactoring
 * @author ybecerra
 * @version 2, 07/04/2017
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped

public class ContabilizarBalanceInicialControlador extends BeanBaseModal
{

    private final String compania;
    private int ano;
    private List<Registro> listaAno;
    private String headerTG_SOSFTWARE;
    @EJB
    private EjbContabilidadCeroRemote ejbContabilidadCero;

    /**
     * Creates a new instance of ContabilizarBalanceInicialControlador
     */
    public ContabilizarBalanceInicialControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        headerTG_SOSFTWARE = JsfUtil.getTituloMensajes();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.CONTABILIZAR_BALANCE_INICIAL_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(ContabilizarBalanceInicialControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init()
    {
        if (permisos == null)
        {
            Formulario form = SessionUtil.cargarFormulario(
                            numFormulario + "," + SessionUtil.getModulo());
            if (form == null)
            {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
            permisos = form.getPermisos();
            if ((permisos == null) || !permisos[3])
            {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
        }
        cargarListaAno();
        abrirFormulario();
    }

    public void cargarListaAno()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ContabilizarBalanceInicialControladorUrlEnum.URL2491
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirAceptar()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        Registro reg;
        try
        {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ContabilizarBalanceInicialControladorUrlEnum.URL111
                                                                            .getValue())
                                            .getUrl(), param));
            if (reg != null)
            {
                RequestContext.getCurrentInstance()
                                .execute("PF('DG43').show()");
            }
            else
            {
                aceptarMsgBox();
            }
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirCancelar()
    {
        // RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void aceptarMsgBox()
    {
        contabilizarBalanceInicial();
    }

    private void contabilizarBalanceInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        try
        {
            ejbContabilidadCero.cargarSaldosIniciales(compania, ano,
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB542"));
        }
        catch (SystemException ex)
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
            Logger.getLogger(ContabilizarBalanceInicialControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void abrirFormulario()
    {
        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
    }

    public int getAno()
    {
        return ano;
    }

    public void setAno(int ano)
    {
        this.ano = ano;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

}
