package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LtotalizadorControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author pespitia
 * @version 1, 12/09/2016
 * 
 * -- Modificado por asana 08/06/2017. Refactorizacion de codigo de
 * las listas para utilizar dss. Reemplazo de llamados a la clase
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */

@ManagedBean
@ViewScoped
public class LtotalizadorControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Util para conocer el ciclo seleccionado en el formulario.
     */
    private String ciclo;
    /**
     * Util para conocer el CheckPoint seleccionado.
     */
    private String opciones;
    /**
     * Determina si se ha marcado la casilla opcional.
     */
    private boolean ckManual;
    private StreamedContent archivoDescarga;
    /**
     * Oculta o muestra la casilla opcional, dependiendo del CHECK
     * seleccionado.
     */
    private boolean checkVisible;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCiclo;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    /**
     * Creates a new instance of LtotalizadorControlador
     */
    public LtotalizadorControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            /*
             * Por omisi�n seleccione el primer checkpoint.
             */
            opciones = "1";
            checkVisible = false; /* Oculte la casilla opcional. */
            numFormulario = GeneralCodigoFormaEnum.LTOTALIZADOR_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>

            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LtotalizadorControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCiclo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1092-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 74, Me.Name DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCiclo() {

        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LtotalizadorControladorUrlEnum.URL3677.getValue());   
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,true, "NUMERO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirexcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getReporte(FORMATOS.EXCEL97);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirpdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    private void getReporte(FORMATOS formato) {
        try {
            String reporte = "";

            switch (opciones) {
            case "1":
                reporte = "001068LTotalizador";
                break;
            case "2":
                reporte = ckManual ? "001075LTotalizadorCodigoProm"
                    : "001074LTotalizadorCodigo";
                break;
            case "3":
                reporte = "001080LTotalizadorIncons";
                break;
            default:
                break;
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("ciclo", ciclo);
            parametros.put("PR_CICLO", ciclo);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);


            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = registroAux.getCampos().get("NUMERO").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isCheckVisible() {
        return checkVisible;
    }

    public void setCheckVisible(boolean checkVisible) {
        this.checkVisible = checkVisible;
    }

    public String getOpciones() {
        return opciones;
    }

    public void setOpciones(String opciones) {
        this.opciones = opciones;
    }

    public boolean isCkManual() {
        return ckManual;
    }

    public void setCkManual(boolean ckManual) {
        this.ckManual = ckManual;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>


    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    public void cambiaropciones() {
        // <CODIGO_DESARROLLADO>
        try {
            if (("SI").equals(SysmanFunciones
                            .nvl(ejbSysmanUtilRemote.consultarParametro(compania, "MANEJA CODIGO TOTALIZADOR", SessionUtil.getModulo(), new Date(), true),"NO"))
                            && ("2").equals(opciones)) {

                /*
                 * Muestre la casilla opcional.
                 */
                checkVisible = true;
            }
            else {
                checkVisible = false;
            }
        }
        catch (SystemException e) {
            Logger.getLogger(LtotalizadorControlador.class.getName())
            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
