package sysman.util.consumo.pojo;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class RespuestaConsultaAud {
	 /**
     * Código de la respuesta del servicio que debe ser de negocio,
     * por defecto se deja 0 indicando que no se genero error alguno y
     * si envia el cuerpo de la respuesta
     */
    private long codigo;
    /**
     * Mensaje del error de negocio, por defecto se deja OK; lo que
     * indica que no hay error y el código es 0
     */
    private String mensaje;
    /**
     * Se incluye la respuesta del servicio para cuado el codigo es
     * diferente de 0
     */
    private RespuestaCuerpoConsultarAud cuerpo;
    
	public RespuestaConsultaAud() {
		cuerpo = null;
        codigo = 0;
        mensaje = "OK";
	}
	/**
	 * @return the codigo
	 */
	public long getCodigo() {
		return codigo;
	}
	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(long codigo) {
		this.codigo = codigo;
	}
	/**
	 * @return the mensaje
	 */
	public String getMensaje() {
		return mensaje;
	}
	/**
	 * @param mensaje the mensaje to set
	 */
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	/**
	 * @return the cuerpo
	 */
	public RespuestaCuerpoConsultarAud getCuerpo() {
		return cuerpo;
	}
	/**
	 * @param cuerpo the cuerpo to set
	 */
	public void setCuerpo(RespuestaCuerpoConsultarAud cuerpo) {
		this.cuerpo = cuerpo;
	}
}
