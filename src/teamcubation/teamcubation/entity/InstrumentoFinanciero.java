package teamcubation.entity;

import teamcubation.TipoInstrumento;

public abstract class InstrumentoFinanciero {
	private String nombre;
	private double precio;
	private TipoInstrumento tipo;
	
	public InstrumentoFinanciero() {
		super();
	}

	public InstrumentoFinanciero(String nombre, double precio, TipoInstrumento tipo) {
		this.nombre = nombre;
		this.precio = precio;
		this.tipo = tipo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public TipoInstrumento getTipo() {
		return tipo;
	}

	public void setTipo(TipoInstrumento tipo) {
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		return "[nombre=" + nombre + ", precio=" + precio + ", tipo=" + tipo + "]";
	}
	
	
}
