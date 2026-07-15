package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.predial.ejb.EjbPredialSieteRemote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
 * @version 1, 20/05/2016 14:27:55 -- Modificado por dsuesca
 * 
 * @version 2, 21/06/2017
 * @author jreina se realizaron los cambios de refactoring, cambios a ejb y eliminacion de llamdos a acciones.
 */


@ManagedBean
@ViewScoped
public class ActualizarigactipodosControlador extends BeanBaseModal {
    private final String compania;
    private String ruta;
    private boolean mostrarDialogo;
    private InputStream is;
    private StreamedContent archivoDescarga;
    private long longitudArchivo;
    private String nombreArchivo;
    
    @EJB
    private EjbPredialSieteRemote ejbPredialSiete;

    /**
     * Creates a new instance of ActualizarigactipodosControlador
     */
    public ActualizarigactipodosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.ACTUALIZARIGACTIPODOS_CONTROLADOR.getCodigo();
            validarPermisos();

        }
        catch (Exception ex) {
            Logger.getLogger(ActualizarigactipodosControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {     
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        mostrarDialogo = true;
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalDefault()");
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cargarArchivolector(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
        ruta = event.getFile().getFileName();
        try {
            is = event.getFile().getInputstream();
            nombreArchivo=event.getFile().getFileName().substring(event.getFile().getFileName().length()-4, event.getFile().getFileName().length());
            longitudArchivo = event.getFile().getSize();
        }
        catch (IOException e) {
            Logger.getLogger(ActualizarigactipodosControlador.class.getName())
            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void aceptardialogoConfirmar() {
        archivoDescarga = null;
        try {
            if (is != null) {
                InputStreamReader r = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(r);
                String linea;
                long tam = longitudArchivo / 151;
                int lineas = 0;
                StringBuilder textoArchivo = new StringBuilder();

                if(nombreArchivo.equals(".txt")){
                    while ((linea = br.readLine()) != null) {
                        textoArchivo.append(linea);
                        if (lineas != tam - 1) {
                            textoArchivo.append("@");
                        }
                    }

                    String planoClob = Acciones
                                    .getClobConcatenado(textoArchivo.toString());

                    if (planoClob.startsWith("TO_CLOB")) {
                        planoClob = planoClob.substring(0, planoClob.length() - 1);
                    }

                    String resultado = ejbPredialSiete.actualizarIgacDos(compania,
                                    planoClob, longitudArchivo,
                                    SessionUtil.getCompaniaIngreso()
                                    .getCodigoDepartamento(),
                                    SessionUtil.getCompaniaIngreso()
                                    .getCodigoPais(),
                                    SessionUtil.getCompaniaIngreso()
                                    .getCodigoCiudad(),
                                    SessionUtil.getUser().getCodigo());

                    if (!resultado.isEmpty()){
                        resultado=idioma.getString("TB_TB1222")
                                        + resultado;
                        archivoDescarga = JsfUtil.getArchivoDescarga(
                                        JsfUtil.serializarPlano(resultado),
                                        "INCONSISTENCIAS ACTUALIZAR IGAC TIPO DOS.txt");
                    }
                    ruta=null;
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("MSM_PROCESO_EJECUTADO"));
                }else{
                    JsfUtil.agregarMensajeInformativo("El archivo no corresponde al formato requerido.");
                }
            }
            mostrarDialogo = false;
        }
        catch (IOException
                        | SystemException | JRException e) {
            mostrarDialogo = false;
            ruta=null;
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    
    /**
     * 
     * Metodo invocado al ejecutar el comando remoto mostrarMensaje
     * en la vista
     *
     *
     */
    public void ejecutarmostrarMensaje() {
        //<CODIGO_DESARROLLADO>
        ruta=null;
        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("MSM_PROCESO_EJECUTADO"));
        //</CODIGO_DESARROLLADO>
    }
    
    // <SET_GET_ATRIBUTOS>
    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public boolean isMostrarDialogo() {
        return mostrarDialogo;
    }

    public void setMostrarDialogo(boolean mostrarDialogo) {
        this.mostrarDialogo = mostrarDialogo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }
}
