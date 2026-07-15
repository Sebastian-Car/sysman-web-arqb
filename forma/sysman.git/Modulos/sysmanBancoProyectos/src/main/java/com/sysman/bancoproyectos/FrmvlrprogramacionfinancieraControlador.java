
package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoCeroRemote;
import com.sysman.bancoproyectos.enums.FrmvlrprogramacionfinancieraControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
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

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 19/09/2015
 * 
 * @author asana
 * @version 2, 26/09/2017 Se realiza proceso de refactoring de
 * controlador.
 */
@ManagedBean
@ViewScoped
public class FrmvlrprogramacionfinancieraControlador extends BeanBaseModal {

    private final String compania;
    private StreamedContent archivoDescarga;
    private List<Registro> listaVigenciaInicial;
    private Registro registro;
    @EJB
    private EjbBancoProyectoCeroRemote ejbBancoProyectoCeroRemote;

    /**
     * Creates a new instance of
     * FrmvlrprogramacionfinancieraControlador
     */
    public FrmvlrprogramacionfinancieraControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.FRMVLRPROGRAMACIONFINANCIERA_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();

        try {
            registro = new Registro(new HashMap<String, Object>());
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmvlrprogramacionfinancieraControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaVigenciaInicial();
        abrirFormulario();
    }

    public void cargarListaVigenciaInicial() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaVigenciaInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmvlrprogramacionfinancieraControladorUrlEnum.URL2454
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        String[] subCadena;
        archivoDescarga = null;

        if (registro.getCampos().get("NUMERO") == (null)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("MSM_DEBE_ANO"));
            return;
        }
        try {
            String cadenaArchivo = ejbBancoProyectoCeroRemote
                            .actualizarProgramado(
                                            compania,
                                            Integer.parseInt(registro
                                                            .getCampos()
                                                            .get("NUMERO")
                                                            .toString()),
                                            "0", "99999999999", 0,
                                            SessionUtil.getUser().getCodigo());

            subCadena = cadenaArchivo.split("<error>");

            ByteArrayInputStream incActividades;
            ByteArrayInputStream incComponentes;
            ByteArrayInputStream incProyectos;
            ByteArrayInputStream[] archivos = new ByteArrayInputStream[3];
            int indice = 0;
            String[] archivosNombres = new String[3];
            if (subCadena == null) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1354"));
            }
            else {
                if (!subCadena[0].isEmpty()) {
                    String inconsistenciasActividades = subCadena[0];
                    incActividades = JsfUtil.serializarPlano(
                                    inconsistenciasActividades);
                    archivos[indice] = incActividades;
                    archivosNombres[0] = "inconsistencias_actividades.log";
                    indice++;
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2465")
                                    .replace("#$subCadena[3]$#", subCadena[3]));
                }
                if (!subCadena[1].isEmpty()) {
                    String inconsistenciasComponentes = subCadena[1];
                    incComponentes = JsfUtil.serializarPlano(
                                    inconsistenciasComponentes);
                    archivos[indice] = incComponentes;
                    indice++;
                    archivosNombres[1] = "inconsistencias_componentes.log";
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2466")
                                    .replace("#$subCadena[4]$#", subCadena[4]));
                }
                if (!subCadena[2].isEmpty()) {
                    String inconsistenciasProyectos = subCadena[2];
                    incProyectos = JsfUtil
                                    .serializarPlano(inconsistenciasProyectos);
                    archivos[indice] = incProyectos;
                    archivosNombres[2] = "inconsistencias_proyectos.log";
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2467")
                                    .replace("#$subCadena[5]$#", subCadena[5]));
                }
                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                archivos, archivosNombres);
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1323"));
            }
        }
        catch (DRException | SQLException | IOException
                        | JRException | NumberFormatException
                        | SystemException ex) {
            Logger.getLogger(FrmvlrprogramacionfinancieraControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaVigenciaInicial() {
        return listaVigenciaInicial;
    }

    public void setListaVigenciaInicial(List<Registro> listaVigenciaInicial) {
        this.listaVigenciaInicial = listaVigenciaInicial;
    }

    public Registro getRegistro() {
        return registro;
    }

    public void setRegistro(Registro registro) {
        this.registro = registro;
    }
}