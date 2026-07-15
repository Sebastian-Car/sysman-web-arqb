package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.UsusariosconcodcatastralerrasControladorUrlEnum;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 23/08/2016
 * 
 * @author eamaya
 * @version 2.0, 13/06/2017 Se cambi� el llamado del c�digo del
 * formulario
 * 
 * @author asana
 * @version 3.0 21/06/2017 Se realiza proceso refactoring
 */
@ManagedBean
@ViewScoped

public class UsusariosconcodcatastralerrasControlador
                extends BeanBaseContinuoAcmeImpl
{
    private final String compania;
    private String ciclo;
    private String cicloTitulo;
    // <DECLARAR_ATRIBUTOS>
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of
     * UsusariosconcodcatastralerrasControlador
     */
    public UsusariosconcodcatastralerrasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.USUSARIOSCONCODCATASTRALERRAS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                ciclo = parametrosEntrada.get("CICLO").toString();
                cicloTitulo = "CICLO " + ciclo;
            }
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(UsusariosconcodcatastralerrasControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar()
    {

        enumBase = GenericUrlEnum.SP_USUARIO;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen()
    {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        UsusariosconcodcatastralerrasControladorUrlEnum.URL7563
                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsusariosconcodcatastralerrasControladorUrlEnum.URL5748
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UsusariosconcodcatastralerrasControladorUrlEnum.URL8348
                                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirEXCEL()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPDF()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(FORMATOS formato)
    {

        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ciclo", ciclo);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "001046Usuariosconcodcatastralerra";
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            parametros.put("PR_CICLO", ciclo);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1056-AL_ABRIR Private Sub Form_Open(Cancel As Integer) If
         * Estacargado("ActualizarCodcatastral") Then If
         * Nz([Forms]![actualizarcodcatastral]![Ciclo], "") = "" Then
         * MsgBox "Faltan datos para continuar el proceso",
         * vbInformation, "Sysman Software" Cancel = True Else
         * Me.RecordSource =
         * " SELECT usuario.COMPANIA, usuario.CICLO, usuario.CODIGOINTERNO, usuario.CODIGORUTA, Trim([USUARIO].[PRIMERAPELLIDO] & ' ' & [USUARIO].[SEGUNDOAPELLIDO] & ' ' & [USUARIO].[NOMBRES]) AS NOMBRES, usuario.CODIGOCATASTRAL, Len([CODIGOCATASTRAL]) AS largo "
         * & _ " FROM usuario " & _ " WHERE  usuario.COMPANIA='" &
         * Getcompany() & "' AND usuario.CICLO='" &
         * [Forms]![actualizarcodcatastral]![Ciclo] &
         * "' AND Len([CODIGOCATASTRAL]) <> 15" Me.Requery
         * DoCmd.Maximize End If Else MsgBox
         * "Faltan datos para continuar el proceso", vbInformation,
         * "Sysman Software" Cancel = True End If End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBRES");
        registro.getCampos().remove("COMPANIA");
        registro.getCampos().remove("CODIGORUTA");
        registro.getCampos().remove("CICLO");
        registro.getCampos().remove("CODIGOINTERNO");
        registro.getCampos().remove("LARGO");

        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        if ((registro.getCampos().get("CODIGOCATASTRAL") == null)
            || (registro.getCampos().get("CODIGOCATASTRAL").toString()
                            .length() != 15))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1282"));
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public String getCiclo()
    {
        return ciclo;
    }

    public void setCiclo(String ciclo)
    {
        this.ciclo = ciclo;
    }

    public String getCicloTitulo()
    {
        return cicloTitulo;
    }

    public void setCicloTitulo(String cicloTitulo)
    {
        this.cicloTitulo = cicloTitulo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
