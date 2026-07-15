/*-
 * TarifasincsControlador.java
 *
 * 1.0
 *
 * 16 de sept. de 2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyac�.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCincoRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosDosRemote;
import com.sysman.serviciospublicos.enums.TarifasincsControladorUrlEnum;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
 * @author ybecerra
 * @version 1, 16/09/2016
 *
 * @author jguerrero
 * @version 2.0, 16/06/2017 Se implementa refactoring en la clase, ajustando las consultas y los llamados a las funciones.
 */
@ManagedBean
@ViewScoped
public class TarifasincsControlador extends BeanBaseModal
{
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Almacena el valor ingresado en el campo ano del el formulario
     */
    private int ano;

    /**
     * Almacen el valor ingresado en el campo periodo del formulario
     */
    private String periodo;
    /**
     * Almacena el nombre del usuario ingresado al abrir la aplicacion
     */
    private String usuario;
    /**
     * Almacena el valor retornado de la funcion PCK_SERVICIOS_PUBLICOS_COM2.FC_ANOSIGUIENTE
     */
    private int intAno1;
    /**
     * Almacena el valor retornado de la funcion PCK_SERVICIOS_PUBLICOS_COM2.FC_PERIODOSIGUIENTE
     */
    private String strPeriodo1;
    /**
     * Permite hacer visible o no, el Dialogo TARIFAS, al generar el evento del boton aceptar del formulario
     */
    private boolean dialogoTarifas;

    /**
     * Permite hacer visible o no, el Dialogo FACTURADOS, en el metodo aceptarDG_TARIFAS
     */
    private boolean dialogoFacturado;
    /**
     * Texto que se hace visible en la parte superior del Dialogo TARIFAS
     */
    private String digTarifas;
    /**
     * Texto que se hace visible en la parte superior del Dialogo FACTURADOS
     */
    private String digFacturados;

    /**
     * Variable local para asignar los campos a insertar en la tabla SP_TARIFAS
     */
    String camposTarifas;

    /**
     * Almacena el valor del parametro INCREMENTA TARIFAS DE ASEO POR UTILITARIO
     */
    String tarifasAseo;

    /**
     * Variable para asignar los valores a insertar en la tabla SP_TARIFAS
     */

    String valoresTarifas;

    /**
     * Almacena el valor del parametro DESCONTAR METRAJE ACUEDUCTO
     */
    String acueducto;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbServiciosPublicosDosRemote ejbServiciosPublicosDos;

    @EJB
    private EjbServiciosPublicosCincoRemote ejbServiciosPublicosCinco;

    /**
     * Creates a new instance of TarifasincsControlador
     */
    public TarifasincsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.TARIFASINCS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(TarifasincsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        usuario = SessionUtil.getUser().getCodigo();

        /*
         * Registro que recibe el valor maximo del ano concatenado con el periodo de la tabla SP_CICLO
         */

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        Registro rs = null;

        try
        {
            rs = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(TarifasincsControladorUrlEnum.URL0001.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (rs != null)
        {
            String per = rs.getCampos().get("PER").toString();

            if (per.length() == 6)
            {

                String anio = per.substring(0, 4);
                ano = Integer.valueOf(anio);
                periodo = per.substring(4, 6);
            }
        }
        // <CODIGO_DESARROLLADO>
        /*
         * FR1108-AL_ABRIR Private Sub Form_Open(Cancel As Integer) DoCmd.Restore formularioAbrir 74, Me.Name Dim Strsql As String Dim rs As DAO.Recordset Strsql = " SELECT MAX(ANO & PERIODO) AS PER "
         * & _ " FROM CICLO " & _ " WHERE COMPANIA IN('" & Getcompany() & "') " Set rs = CurrentDb.OpenRecordset(Strsql) If Not rs.EOF Then If Len(rs!PER) = 6 Then Me!ANO = Mid(rs!PER, 1, 4)
         * Me!PERIODO = Mid(rs!PER, 5, 2) Forms!IDENTIFICACION!ANO = Me!ANO Forms!IDENTIFICACION!PERIODO = Me!PERIODO Else Me!ANO = GetAno() Me!PERIODO = GetPeriodo() End If End If rs.Close End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    public void mensajesInicioModal()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            intAno1 = ejbServiciosPublicosDos.generarAnoSiguiente(compania, ano, periodo, "");

            strPeriodo1 = ejbServiciosPublicosDos.generarPeriodoSiguiente(compania, ano, periodo, "");

            if (strPeriodo1.length() <= 1)
            {
                strPeriodo1 = "0" + strPeriodo1;
            }

            /*
             * Registro para verificar si trae o no datos de la tabla SP_PERIODO, con los filtros suministrados
             */
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), intAno1);
            param.put(GeneralParameterEnum.PERIODO.getName(), strPeriodo1);

            Registro rsPeriodo = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TarifasincsControladorUrlEnum.URL0002
                                                                            .getValue())
                                            .getUrl(), param));

            if (rsPeriodo == null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1583"));
                return;
            }
            /*
             * Registro para verificar si trae o no datos de la tabla SP_TARIFAS, con los filtros suministrados
             */

            Registro rsTarifas = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            TarifasincsControladorUrlEnum.URL0003
                                                                            .getValue())
                                            .getUrl(), param));

            if (rsTarifas != null)
            {
                digTarifas = idioma.getString("TB_TB1586");
                digTarifas = digTarifas.replace("s$ano$s",
                                Integer.toString(intAno1));
                digTarifas = digTarifas.replace("s$periodo$s",
                                strPeriodo1 + "<br>");

                /*
                 * Se hace visible el Dialogo TARIFAS, si se selecciona "Si" se ejecutara el metodo aceptarDG_TARIFAS
                 */
                dialogoTarifas = true;
            }
            else
            {
                facturado(false);
            }

        }
        catch (SystemException ex)
        {

            Logger.getLogger(TarifasincsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirSalir()
    {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalDefault();");
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    /**
     * Metodo que se ejecuta al darle clic en Si del dialogo TARIFAS visualizado en el formulario
     */
    public void aceptarDGTARIFAS()
    {
        int cont = 0;
        /*
         * Lista que recibe los ano , periodos y ciclos de la SP_FACTURADO, con los filtros suministrados
         */
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), intAno1);
        param.put(GeneralParameterEnum.PERIODO.getName(), strPeriodo1);

        List<Registro> rsFacturado;
        try
        {
            rsFacturado = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(TarifasincsControladorUrlEnum.URL0004.getValue())
                                            .getUrl(),
                            param));

            /*
             * Esta variable se utiliza en el metodo Facturados para validar la funcion AuditoriaGeneral
             */
            boolean indReEscribir = false;
            if (rsFacturado.isEmpty())
            {
                facturado(indReEscribir);
                return;
            }

            int rsAno = Integer.parseInt(rsFacturado.get(cont).getCampos()
                            .get("ANO").toString());

            String rsPeriodo = rsFacturado.get(cont).getCampos()
                            .get("PERIODO").toString();

            if (rsPeriodo.length() <= 1)
            {
                rsPeriodo = "0" + rsPeriodo;
            }

            if ((rsAno == intAno1) && (rsPeriodo.equals(strPeriodo1))
                && (rsFacturado.size() == 1))
            {

                digFacturados = idioma.getString("TB_TB1589");
                digFacturados = digFacturados.replace("s$ano$s",
                                Integer.toString(intAno1));
                digFacturados = digFacturados.replace("s$periodo$s",
                                strPeriodo1 + "<br>");
                /*
                 * Hace visible el dialogo FACTURADOS, ocultando el dialogo TARIFAS y si se selecciona "SI" se ejecutara el metodo aceptarDG_FACTURADO
                 */
                dialogoFacturado = true;
                dialogoTarifas = false;
            }
            else
            {
                String msg;
                StringBuilder rsMsj = new StringBuilder();
                /*
                 * Se recorre la lista para traer la concatenacion en la variable rsMsj
                 */
                for (int i = 0; i < rsFacturado.size(); i++)
                {

                    int rsAnoF = Integer.parseInt(rsFacturado.get(i).getCampos()
                                    .get("ANO").toString());
                    String rsPeriodoF = rsFacturado.get(i).getCampos()
                                    .get("PERIODO").toString();
                    String rsCicloF = rsFacturado.get(i).getCampos()
                                    .get("CICLO").toString();

                    msg = idioma.getString("TB_TB1590");
                    msg = msg.replace("s$rsAno$s", Integer.toString(rsAnoF));
                    msg = msg.replace("s$rsPeriodo$s", rsPeriodoF);
                    msg = msg.replace("s$rsCiclo$s", rsCicloF);

                    rsMsj.append(msg);
                    rsMsj.append("\r\n ");

                }

                String msj = idioma.getString("TB_TB1591");
                msj = msj.replace("s$msg$s", rsMsj.toString() + "\r\n");
                dialogoTarifas = false;
                ByteArrayInputStream streamTexto;

                streamTexto = JsfUtil.serializarPlano(msj);
                archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
                                "PeriodosFacturados.txt");
                return;

            }
        }
        catch (SystemException | JRException | IOException e1)
        {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
    }

    /**
     * Metodo que se ejecuta al momento de darle clic al boton No del Dialogo TARIFAS
     */
    public void cancelarDGTARIFAS()
    {
        dialogoTarifas = false;
    }

    /**
     * Metodo que se ejecuta al darle clic en Si del dialogo FACTURADOS visualizado en el formulario
     */
    public void aceptarDGFACTURADO()
    {

        facturado(true);
    }

    /**
     * Metodo que se ejecuta al momento de darle clic al boton No del Dialogo FACTURADOS
     */

    public void cancelarDGFACTURADO()
    {
        dialogoTarifas = false;
        dialogoFacturado = false;
    }

    /**
     * Metodo que se llama en los metodos aceptarDG_TARIFAS , aceptarDG_FACTURADO, realiza inserciones a las tablas SP_TARIFAS, SP_TARIFA_INT_RECA
     */

    public void facturado(boolean indReEscribir)
    {
        try
        {
            ejbServiciosPublicosCinco.incrementarTarifas(compania, intAno1, strPeriodo1, ano, periodo, indReEscribir,
                            SessionUtil.getUser().getCodigo());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
            dialogoFacturado = false;
            dialogoTarifas = false;
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            dialogoTarifas = false;
            dialogoFacturado = false;
        }

    }

    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public int getAno()
    {
        return ano;
    }

    public void setAno(int ano)
    {
        this.ano = ano;
    }

    public String getPeriodo()
    {
        return periodo;
    }

    public void setPeriodo(String periodo)
    {
        this.periodo = periodo;
    }

    public String getUsuario()
    {
        return usuario;
    }

    public void setUsuario(String usuario)
    {
        this.usuario = usuario;
    }

    public boolean isDialogoTarifas()
    {
        return dialogoTarifas;
    }

    public void setDialogoTarifas(boolean dialogoTarifas)
    {
        this.dialogoTarifas = dialogoTarifas;
    }

    public String getDigTarifas()
    {
        return digTarifas;
    }

    public void setDigTarifas(String digTarifas)
    {
        this.digTarifas = digTarifas;
    }

    public boolean isDialogoFacturado()
    {
        return dialogoFacturado;
    }

    public void setDialogoFacturado(boolean dialogoFacturado)
    {
        this.dialogoFacturado = dialogoFacturado;
    }

    public String getDigFacturados()
    {
        return digFacturados;
    }

    public void setDigFacturados(String digFacturados)
    {
        this.digFacturados = digFacturados;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }
}
