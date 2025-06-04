package teamcubation.repository;

import java.util.ArrayList;
import java.util.List;

import teamcubation.entity.InstrumentoFinanciero;

public class Repositorio<T extends InstrumentoFinanciero> {

	private List<T> elementos = new ArrayList<>();
	
	public void agregar(T objeto) {
		elementos.add(objeto);
	}
	
	public boolean eliminarPorNombre(String nombre) {
		T elemento = buscarPorNombre(nombre);
		if(elemento != null) {
			elementos.remove(elemento);
			return true;
		} else {
			System.out.println("Elemento no encontrado");
			return false;
		}
	}
	
	public T buscarPorNombre(String nombre) {
	    for (T e : elementos) {
	        if (e.getNombre().equalsIgnoreCase(nombre)) {
	            return e;
	        }
	    }
	    return null;
	}
	
	public List<T> listarTodos(){
		System.out.println(elementos);
		return elementos;
	}
	
	public boolean editar(String nombre, T nuevoObjeto) {
		T elemento = buscarPorNombre(nombre);
		boolean seEdito = false;
		if(elemento != null) {
			elementos.remove(elemento);
			elementos.add(nuevoObjeto);
			seEdito = true;
		} else {
			System.out.println("Elemento no encontrado");
		}
		
		return seEdito;
	}
}
