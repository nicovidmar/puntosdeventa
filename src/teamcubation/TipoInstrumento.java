package teamcubation;

public enum TipoInstrumento {
	ACCION("Accion"),
	BONO("Bono");

	private final String descripcion;

	TipoInstrumento(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
