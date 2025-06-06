package teamcubation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import teamcubation.TipoInstrumento;
import teamcubation.entity.Accion;
import teamcubation.entity.Bono;
import teamcubation.entity.InstrumentoFinanciero;
import teamcubation.repository.Repositorio;

public class InstrumentoService {

	private static final Repositorio<InstrumentoFinanciero> repositorio = new Repositorio<>();
	public static Scanner scanner = new Scanner(System.in);

	public void consultarInstrumentos() {
		int opcion;
		System.out.println("1. Consultar todos los instrumentos");
		System.out.println("2. Consultar por nombre");
		opcion = Integer.parseInt(scanner.nextLine());

		switch (opcion) {
		case 1 -> {
			List<InstrumentoFinanciero> instrumentos = repositorio.listarTodos();
			if (instrumentos.isEmpty()) {
				System.out.println("La lista de instrumentos está vacía");
			} else {
				System.out.println(instrumentos);
			}
		}
		case 2 -> {
			InstrumentoFinanciero instrumento = consultarPorNombre();
			if (instrumento != null) {
				System.out.println(instrumento);
			}
		}
		default -> System.out.println("Opción inválida");
		}
	}

	public void registrarInstrumento() {
		InstrumentoFinanciero instrumento = null;
		String nombre = "";
		do {
			nombre = pedirInput("Nombre: ");
			instrumento = repositorio.buscarPorNombre(nombre);
			if (instrumento != null) {
			    System.out.println("Ya existe un instrumento con ese nombre");
			}
		} while (instrumento != null);

		double precio = 0;
		while (precio == 0) {
			System.out.println("Precio: ");
			String input = scanner.nextLine();
			try {
				precio = Double.parseDouble(input);
				if (precio <= 0) {
					System.out.println("El precio debe ser mayor a cero");
				}
			} catch (NumberFormatException e) {
				System.out.println("Precio inválido");
			}
		}

		TipoInstrumento tipo = null;
		while (tipo == null) {
			String input = pedirInput("Nuevo tipo (ACCION O BONO): ").toUpperCase();
			try {
				tipo = TipoInstrumento.valueOf(input);

				if (tipo == TipoInstrumento.ACCION) {
					repositorio.agregar(new Accion(nombre, precio, tipo));
				} else if (tipo == TipoInstrumento.BONO) {
					repositorio.agregar(new Bono(nombre, precio, tipo));
				}
				System.out.println("Instrumento registrado");
			} catch (IllegalArgumentException e) {
				System.out.println("Tipo inválido");
			}
		}

	}

	public InstrumentoFinanciero consultarPorNombre() {
		String nombre = pedirInput("Ingrese el nombre del instrumento a consultar:");
		return repositorio.buscarPorNombre(nombre);
	}

	public void editarAtributo() {

		String nombre = pedirInput("Nombre del instrumento a editar: ");

		InstrumentoFinanciero instrumento = repositorio.buscarPorNombre(nombre);
		if (instrumento == null) {
			System.out.println("Instrumento no encontrado");
			return;
		}

		String atributo = pedirInput("Atributo a modificar (nombre, precio o tipo): ").toLowerCase();

		switch (atributo) {
		case "nombre" -> {
			boolean existe = false;
			while(!existe) {
				String nuevoNombre = pedirInput("Nuevo nombre: ");
				if (repositorio.buscarPorNombre(nuevoNombre) != null) {
					System.out.println("Ya existe un instrumento con ese nombre. Intente nuevamente.");
				} else {
					instrumento.setNombre(nuevoNombre);
					System.out.println("Nombre actualizado.");
					existe = true;
				}
			}
			
		}
		case "precio" -> {
			double nuevoPrecio = 0;
			do {
				System.out.println("Nuevo precio: ");

				try {
				    nuevoPrecio = Double.parseDouble(scanner.nextLine());
				    if (nuevoPrecio <= 0) {
				        System.out.println("El precio debe ser mayor a cero");
				    } else {
				        instrumento.setPrecio(nuevoPrecio);
				        System.out.println("Precio actualizado");
				    }
				} catch (NumberFormatException e) {
				    System.out.println("Precio inválido");
				}
			} while (nuevoPrecio == 0);

		}
		case "tipo" -> {
			TipoInstrumento nuevoTipo = null;
			while (nuevoTipo == null) {
				String input = pedirInput("Nuevo tipo (ACCION O BONO): ").toUpperCase();

				try {
					nuevoTipo = TipoInstrumento.valueOf(input);
					instrumento.setTipo(nuevoTipo);
					System.out.println("Tipo actualizado");
				} catch (IllegalArgumentException e) {
					System.out.println("Tipo inválido");
				}
			}
		}
		default -> System.out.println("Atributo no válido");
		}
	}

	public void eliminar() {
		String nombre = pedirInput("Nombre del instrumento a eliminar: ");
		boolean eliminado = repositorio.eliminarPorNombre(nombre);
		if (eliminado) {
			System.out.println("Instrumento eliminado correctamente");
		} else {
			System.out.println("No se encontró el instrumento");
		}
	}

	public String pedirInput(String mensaje) {
		String input = "";
		do {
			System.out.print(mensaje);
			input = scanner.nextLine();
			if (input.isEmpty()) {
				System.out.println("El valor no puede estar vacío");
			}
		} while (input.isEmpty());
		return input;
	}

}
