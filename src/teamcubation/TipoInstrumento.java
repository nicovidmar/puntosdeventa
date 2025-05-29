package teamcubation;

public enum TipoInstrumento {
	ACCION("Accion"),
	BONO("Bono");

	private final String tipo;

	TipoInstrumento(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
