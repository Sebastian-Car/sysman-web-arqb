package workflow.enums;

public enum SelecMonTramitesControladorUrlEnum {

	URL0001("SELECMONTRAMITESCONTROLADORURL0001", "4002");

	private final String key;
	private final String value;

	private SelecMonTramitesControladorUrlEnum(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}