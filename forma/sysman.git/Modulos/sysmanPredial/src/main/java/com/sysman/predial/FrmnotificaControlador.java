package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.enums.FrmnotificaControladorEnum;
import com.sysman.predial.enums.FrmnotificaControladorUrlEnum;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

/**
 *
 * @author acaceres
 * @version 1, 26/05/2016
 *
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 *
 * @author spina
 * @version 3, 05/07/2017 - se refactoriza dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class FrmnotificaControlador extends BeanBaseModal
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String titulo;
    private String txtObsNueva;
    private int consecutivo;
    private String codPredio;
    private Date fechaNotificacion;
    private String docSoporte;
    private String notificador;
    private String ano;
    private String codigo;
    private boolean dialogoVisible;
    private String textoDialog;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FrmnotificaControlador
     */
    public FrmnotificaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        Map<String, Object> parametros = SessionUtil.getFlash();

        if (parametros != null)
        {
            codigo = parametros.get("codigo").toString();
        }
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMNOTIFICA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmnotificaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        titulo = idioma.getString("TB_TB3282").replace("s$codigo$s", codigo);
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        fechaNotificacion = new Date();
        // </CODIGO_DESARROLLADO>
    }

    public void cargarModal()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirBtnAceptar()
    {
        // <CODIGO_DESARROLLADO>
        traerConsecutivoIpNotificaciones();
        int anio = SysmanFunciones.getParteFecha(new Date(), Calendar.YEAR);

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try
        {
            List<Registro> reg = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmnotificaControladorUrlEnum.URL4409
                                                                            .getValue())
                                            .getUrl(),
                                            param));

            if (reg.isEmpty())
            {
                registrar();
            }
            else
            {

                StringBuilder bld = new StringBuilder();
                bld.append(idioma.getString("TB_TB1170") + " " + codigo + " "
                    + idioma.getString("TB_TB1171") + "\n");
                for (Registro registroN : reg)
                {
                    bld.append(registroN.getCampos().get("DOCSOPORTE")
                        + "    "
                        + SysmanFunciones.convertirAFechaCadena(
                                        (Date) registroN
                                                        .getCampos()
                                                        .get("FECHANOTIFICACION"))
                        + " <br>");
                }

                textoDialog = bld.toString() + "\n\n"
                    + idioma.getString("TB_TB3281");
                dialogoVisible = true;
            }
        }
        catch (SystemException | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </CODIGO_DESARROLLADO>
    public void oprimirCmdSalir()
    {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void aceptarRegistrarNotificacionNueva()
    {
        // <CODIGO_DESARROLLADO>
        registrar();
        dialogoVisible = false;
        txtObsNueva = "";
        notificador = "";
        docSoporte = "";

        // </CODIGO_DESARROLLADO>
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void registrar()
    {
        int anio = SysmanFunciones.getParteFecha(new Date(), Calendar.YEAR);

        if ((txtObsNueva != null) && (txtObsNueva != ""))
        {

            try
            {
                Registro registro = new Registro();
                registro.getCampos().put(
                                GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                registro.getCampos().put(
                                GeneralParameterEnum.NUMERO_ORDEN.getName(),
                                SysmanConstantes.NUMERO_ORDEN_PREDIAL);
                registro.getCampos().put(
                                GeneralParameterEnum.CONSECUTIVO.getName(),
                                consecutivo);
                registro.getCampos().put(
                                FrmnotificaControladorEnum.CODPREDIO.getValue(),
                                codigo);
                registro.getCampos()
                                .put(FrmnotificaControladorEnum.FECHANOTIFICACION
                                                .getValue(),
                                                fechaNotificacion);
                registro.getCampos().put(FrmnotificaControladorEnum.DOCSOPORTE
                                .getValue(), docSoporte);
                registro.getCampos()
                                .put(FrmnotificaControladorEnum.OBSERVACIONES
                                                .getValue(), txtObsNueva);
                registro.getCampos().put(FrmnotificaControladorEnum.NOTIFICADOR
                                .getValue(), notificador);
                registro.getCampos().put(GeneralParameterEnum.ANO.getName(),
                                anio);
                registro.getCampos().put(
                                GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());
                registro.getCampos().put(
                                GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser()
                                                .getCodigo());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmnotificaControladorUrlEnum.URL5371
                                                                .getValue());

                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                registro.getCampos());

                Registro registro2 = new Registro();
                registro2.getCampos()
                                .put(FrmnotificaControladorEnum.PROCESO_DE_COBRO
                                                .getValue(), "5");
                registro2.getCampos().put(
                                GeneralParameterEnum.DATE_MODIFIED.getName(),
                                new Date());
                registro2.getCampos()
                                .put(GeneralParameterEnum.MODIFIED_BY
                                                .getName(),
                                                SessionUtil.getUser()
                                                                .getCodigo());
                registro2.getLlave().put(FrmnotificaControladorEnum.KEY_COMPANIA
                                .getValue(), compania);
                registro2.getLlave().put(FrmnotificaControladorEnum.KEY_CODIGO
                                .getValue(), codigo);
                registro2.getLlave()
                                .put(FrmnotificaControladorEnum.KEY_NUMERO_ORDEN
                                                .getValue(),
                                                SysmanConstantes.NUMERO_ORDEN_PREDIAL);

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmnotificaControladorUrlEnum.URL5372
                                                                .getValue());
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                registro2.getCampos(), registro2.getLlave());

                txtObsNueva = "";
                consecutivo = consecutivo + 1;
                notificador = "";
                docSoporte = "";
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));
            }
            catch (SystemException e)
            {
                Logger.getLogger(FrmnotificaControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
    }

    public void traerConsecutivoIpNotificaciones()
    {
        Registro regConsecutivo;
        try
        {
            regConsecutivo = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmnotificaControladorUrlEnum.URL5373
                                                                            .getValue())
                                            .getUrl(), null));
            if ((regConsecutivo != null)
                && (regConsecutivo.getCampos().get("CON") != null))
            {
                consecutivo = Integer.parseInt(
                                regConsecutivo.getCampos().get("CON")
                                                .toString())
                    + 1;
            }
            else
            {
                consecutivo = 1;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cancelarRegistrarNotificacionNueva()
    {
        // <CODIGO_DESARROLLADO>
        dialogoVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public String getTxtObsNueva()
    {
        return txtObsNueva;
    }

    public void setTxtObsNueva(String txtObsNueva)
    {
        this.txtObsNueva = txtObsNueva;
    }

    public int getConsecutivo()
    {
        return consecutivo;
    }

    public void setConsecutivo(int consecutivo)
    {
        this.consecutivo = consecutivo;
    }

    public String getCodPredio()
    {
        return codPredio;
    }

    public void setCodPredio(String codPredio)
    {
        this.codPredio = codPredio;
    }

    public Date getFechaNotificacion()
    {
        return fechaNotificacion;
    }

    public void setFechaNotificacion(Date fechaNotificacion)
    {
        this.fechaNotificacion = fechaNotificacion;
    }

    public String getCodigo()
    {
        return codigo;
    }

    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    public String getDocSoporte()
    {
        return docSoporte;
    }

    public void setDocSoporte(String docSoporte)
    {
        this.docSoporte = docSoporte;
    }

    public String getNotificador()
    {
        return notificador;
    }

    public void setNotificador(String notificador)
    {
        this.notificador = notificador;
    }

    public String getAno()
    {
        return ano;
    }

    public void setAno(String ano)
    {
        this.ano = ano;
    }

    public boolean isDialogoVisible()
    {
        return dialogoVisible;
    }

    public void setDialogoVisible(boolean dialogoVisible)
    {
        this.dialogoVisible = dialogoVisible;
    }

    public String getTextoDialog()
    {
        return textoDialog;
    }

    public void setTextoDialog(String textoDialog)
    {
        this.textoDialog = textoDialog;
    }

}
