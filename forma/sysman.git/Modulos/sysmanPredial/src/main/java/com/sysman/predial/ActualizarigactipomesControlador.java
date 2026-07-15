package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialSeisRemote;
import com.sysman.predial.ejb.EjbPredialSieteRemote;
import com.sysman.predial.enums.ActualizarigactipomesControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 2, 16/03/2017 08:27:53 -- Modificado por jrodriguezr
 * @version 2, 02/08/2016 12:26:48 -- Modificado por acaceres
 * @version 3, 02/03/2017 -- Modificado por jrodriguezr
 * @version 4, 13/06/2017 -- Modificado por eamaya<br>
 * Se cambió el llamado del código del formulario y actualización de
 * ConnectorPool
 * @version 5, 21/06/2017 -- Modificado por amonroy<br>
 * Se realiza el Proceso de Refactoring e implementacion de EJBs para
 * las funciones y procedimientos que son llamadas en el controlador.
 * Se realiza la migracion del metodo resolucion123() al procedimiento
 * PCK_PREDIAL_COM7.PR_RESOLUCIONES123 y unifica la logica de
 * Actualizacion de la informacion Igac por mes en la funcion
 * PCK_PREDIAL_COM7.FC_ACTUALIZARIGACMES
 * 
 */
@ManagedBean
@ViewScoped
public class ActualizarigactipomesControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String ano;
    private String ruta;
    private long longitudArchivo;
    private boolean bloqueaAceptar;
    Registro rsUsuarios;
    Registro rsResolucionDet;
    int contU = 0;
    private StreamedContent archivoDescarga;
    /**
     * Implementacion del EJB de EjbPredialSeisRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y
     * se encuentran almacenadas en el paquete PCK_PREDIAL_COM6
     */
    @EJB
    private EjbPredialSeisRemote ejbPredialSeis;
    /**
     * Implementacion del EJB de EjbPredialSieteRemote para hacer el
     * llamado a las funciones que se invocan dentro del Controlador y
     * se encuentran almacenadas en el paquete PCK_PREDIAL_COM7
     */
    @EJB
    private EjbPredialSieteRemote ejbPredialSiete;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaCmbAno;
    private BufferedReader brArchivo;
    private boolean resultado;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of ActualizarigactipomesControlador
     */
    public ActualizarigactipomesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.ACTUALIZARIGACTIPOMES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ActualizarigactipomesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaCmbAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        ano = String.valueOf(SysmanFunciones.ano(new Date()));
        bloqueaAceptar = false;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCmbAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCmbAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ActualizarigactipomesControladorUrlEnum.URL5174
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Realiza el paso del contenido del archivo plano a formato CLOB
     * utilizando "@" como separador de linea, realiza el llamado a la
     * funcion PCK_PREDIAL_COM7.FC_ACTUALIZARIGACMES y la generacion
     * del reporte
     */
    public void oprimirBT1376() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(ano)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1024"));
            return;
        }
        bloqueaAceptar = true;
        try {
            String lineaArchivo;
            /* 159 es el tamanio definido para la linea del archivo */
            long tam = longitudArchivo / 159;
            int lineas = 0;
            StringBuilder textoArchivo = new StringBuilder();

            while ((lineaArchivo = brArchivo.readLine()) != null) {

                textoArchivo.append(lineaArchivo);

                if (lineas != tam - 1) {
                    textoArchivo.append("@");
                }
                lineas++;
            }

            String planoClob = Acciones
                            .getClobConcatenado(textoArchivo.toString());
            if (planoClob.startsWith("TO_CLOB")) {
                planoClob = planoClob.substring(0, planoClob.length() - 1);
            }

            resultado = ejbPredialSiete.actualizarIgacMes(compania,
                            planoClob,
                            longitudArchivo,
                            Integer.parseInt(ano),
                            SessionUtil.getUser().getCodigo(),
                            Integer.parseInt(SessionUtil.getCompaniaIngreso()
                                            .getCodigoPais()));

            if (resultado) {
                generarReporteIgac(FORMATOS.PDF);
            }
        }
        catch (IOException | OutOfMemoryError | NumberFormatException
                        | SystemException e) {
            ruta = null;
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {
            bloqueaAceptar = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    private void generarReporteIgac(FORMATOS formato) {
        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        try {
            reemplazar.put("condicion", "");

            Reporteador.resuelveConsulta("000809INFIGACREPORTE",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000809INFIGACREPORTE",
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SysmanException | JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void ejecutarmostrarMensaje() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("MSM_PROCESO_EJECUTADO"));
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    public void cargarArchivolector(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>

        InputStream is;

        try {
            is = event.getFile().getInputstream();
            longitudArchivo = event.getFile().getSize();
            InputStreamReader r = new InputStreamReader(is);
            brArchivo = new BufferedReader(r);
            String lineaArchivo = brArchivo.readLine();
            if (!validarTipoMes(lineaArchivo)) {
                ruta = null;
                brArchivo = null;
                return;
            }
            ruta = event.getFile().getFileName();
            brArchivo = new BufferedReader(new InputStreamReader(
                            event.getFile().getInputstream()));
        }
        catch (IOException e) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1320"));
            Logger.getLogger(ActualizarigactipomesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarTipoMes(String archivoTexto) {

        if (!"Tipo Mes".equals(validarTipoResolucion(archivoTexto))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1321"));
            return false;
        }
        if (!SessionUtil.getCompaniaIngreso().getCodigoDepartamento()
                        .equals(archivoTexto.substring(0, 2))) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1224"));
            return false;
        }
        if (!SessionUtil.getCompaniaIngreso().getCodigoCiudad()
                        .equals(archivoTexto.substring(2, 5))) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1225"));
            return false;
        }
        return true;
    }

    /**
     * @param primeraLinea:
     * Metodo que valida la primera linea del archivo que se desea
     * subir: Si posiciones 25, 26, 27 y 28 = ej: 003S = Tipo Uno Si
     * la posicion 31 = C ï¿½ I y posicion 32 = # = Tipo mes.
     * @return: String
     */
    private String validarTipoResolucion(String primeraLinea) {
        String validaTipoResolucionU = "";
        try {
            validaTipoResolucionU = ejbPredialSeis
                            .validarTipoResolucion(primeraLinea);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return validaTipoResolucionU;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public boolean isBloqueaAceptar() {
        return bloqueaAceptar;
    }

    public void setBloqueaAceptar(boolean bloqueaAceptar) {
        this.bloqueaAceptar = bloqueaAceptar;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isResultado() {
        return resultado;
    }

    public void setResultado(boolean resultado) {
        this.resultado = resultado;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaCmbAno() {
        return listaCmbAno;
    }

    public void setListaCmbAno(List<Registro> listaCmbAno) {
        this.listaCmbAno = listaCmbAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

}
