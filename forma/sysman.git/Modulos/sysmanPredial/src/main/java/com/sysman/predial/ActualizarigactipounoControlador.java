package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialOchoRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
 * @version 1, 20/05/2016 14:27:55 -- Modificado por acaceres
 * @version 2, 16/03/2016 10:00 am -- Modificado por jrodriguezr
 * @version 3, 13/06/2017 jrodriguezr Se refactoriza el código: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 * @version 4, asana, 29/06/2017 Se realiza refactoring de
 * controlador.
 */
@ManagedBean
@ViewScoped
public class ActualizarigactipounoControlador extends BeanBaseModal {

    /**
     * Constante que almacenara el numero de la entidad a la cual se
     * le va a actualizar el archivo de IGAC
     */
    private final String compania;

    /**
     * Atributo que almacenara el numero de la resolucion
     */
    private String resolucion;

    /**
     * Atributo que almacenara la ruta en donde se encuentra
     * almacenada la resolucion
     */
    private String ruta;

    /**
     * Atributo que almacenara el archivo
     */
    private BufferedReader brArchivo;

    private StreamedContent archivoDescarga;
    @EJB
    private EjbPredialOchoRemote ejbPredialOcho;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of ActualizarigactipounoControlador
     */
    public ActualizarigactipounoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.ACTUALIZARIGACTIPOUNO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            resolucion = "1";
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ActualizarigactipounoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        if (ingresarIgacTipoUno()) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(Constantes.MSM_PROCESO_EJECUTADO));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPantalla() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void getInforme(FORMATOS formato) {
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000796CambiosIGACTIPO1";
            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (OutOfMemoryError | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /*
     * Metodo que realiza el cargue del archivo y la lectura del
     * mismo.
     */
    public void cargarArchivolector(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        InputStream is;
        ruta = event.getFile().getFileName();
        String formato = ruta.substring(ruta.length() - 4, ruta.length());
        if (!(".txt".equals(formato))) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3554"));
        }
        else {
            try {
                is = event.getFile().getInputstream();
                InputStreamReader r = new InputStreamReader(is);
                brArchivo = new BufferedReader(r);

                ruta = event.getFile().getFileName();
                brArchivo = new BufferedReader(new InputStreamReader(
                                event.getFile().getInputstream()));
            }
            catch (IOException e) {
                Logger.getLogger(ActualizarigactipounoControlador.class
                                .getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    private boolean ingresarIgacTipoUno() {
        boolean resultado = false;
        try {
            StringBuilder textoArchivo = new StringBuilder();
            String lineaArchivo;

            while ((lineaArchivo = brArchivo.readLine()) != null) {
                textoArchivo.append(lineaArchivo).append("@");
            }
            if (textoArchivo.toString().equals("")) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1326"));
            }
            else {

                String planoClob = Acciones
                                .getClobConcatenado(textoArchivo.toString());
                if (planoClob.startsWith("TO_CLOB")) {
                    planoClob = planoClob.substring(0, planoClob.length() - 1);
                }

                resultado = ejbPredialOcho.insertarRegistroIgacUno(compania,
                                planoClob,
                                SysmanFunciones.ano(new Date()),
                                SessionUtil.getUser().getCodigo(),
                                SessionUtil.getCompaniaIngreso()
                                                .getCodigoPais(),
                                SessionUtil.getCompaniaIngreso()
                                                .getCodigoDepartamento(),
                                SessionUtil.getCompaniaIngreso()
                                                .getCodigoCiudad(),
                                "1".equals(resolucion),
                                SessionUtil.getCompaniaIngreso().getNombre());

            }

        }
        catch (SystemException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return resultado;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getResolucion() {
        return resolucion;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
