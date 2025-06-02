package teamcubation;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import teamcubation.Accion;
import teamcubation.Bono;
import teamcubation.InstrumentoFinanciero;
import teamcubation.TipoInstrumento;
 
public class InstrumentoService {

	public static List<InstrumentoFinanciero> instrumentos = new ArrayList<>();
	public static Scanner scanner = new Scanner(System.in);

	public void consultarInstrumentos() {
		int opcion;
		System.out.println("1. Consultar todos los instrumentos");
		System.out.println("2. Consultar por nombre");
		opcion = Integer.parseInt(scanner.nextLine());

		switch (opcion) {
		case 1 -> {
			if (instrumentos.isEmpty()) {
				System.out.println("La lista de instrumentos está vacía");
			} else {
				System.out.println(instrumentos);
			}
		}
		case 2 -> System.out.println(consultarPorNombre());
		default -> System.out.println("Opción inválida");
		}
	}

	public void registrarInstrumento() {
		String nombre = "";
		while (nombre.isBlank()) {
			System.out.println("Nombre: ");
			nombre = scanner.nextLine();
			if (nombre.isBlank()) {
				System.out.println("El nombre no puede estar vacío");
			}
		}

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
			System.out.println("Tipo (ACCION o BONO): ");
			String input = scanner.nextLine().toUpperCase();
			try {
				tipo = TipoInstrumento.valueOf(input);

				if (tipo == TipoInstrumento.ACCION) {
					instrumentos.add(new Accion(nombre, precio, tipo));
				} else if (tipo == TipoInstrumento.BONO) {
					instrumentos.add(new Bono(nombre, precio, tipo));
				}
				System.out.println("Instrumento registrado");
			} catch (IllegalArgumentException e) {
				System.out.println("Tipo inválido");
			}
		}

	}

	public InstrumentoFinanciero consultarPorNombre() {
		String nombre = pedirInput("Ingrese el nombre del instrumento a consultar:");
		InstrumentoFinanciero instrumento = buscarInstrumento(nombre);
		if (instrumento == null) {
			System.out.println("No se encontró instrumento con nombre " + nombre);
		}
		return instrumento;
	}

	public void editarAtributo() {
		String nombre = "";

		while (nombre == "") {
			System.out.println("Nombre del instrumento a editar: ");
			nombre = scanner.nextLine();
		}

		InstrumentoFinanciero instrumento = buscarInstrumento(nombre);
		if (instrumento == null) {
			System.out.println("Instrumento no encontrado");
			return;
		}

		String atributo = "";
		while (atributo.isBlank()) {
			System.out.println("Atributo a modificar (nombre, precio o tipo): ");
			atributo = scanner.nextLine().toLowerCase();
		}

		switch (atributo) {
		case "nombre" -> {
			String nuevoNombre = "";
			do {
				System.out.println("Nuevo nombre: ");
				nuevoNombre = scanner.nextLine();
			} while (nuevoNombre.isBlank());

			instrumento.setNombre(nuevoNombre);
			System.out.println("Nombre actualizado");
		}
		case "precio" -> {
			double nuevoPrecio = 0;
			do {
				System.out.println("Nuevo precio: ");

				try {
					nuevoPrecio = Double.parseDouble(scanner.nextLine());
					instrumento.setPrecio(nuevoPrecio);
					System.out.println("Precio actualizado");
				} catch (NumberFormatException e) {
					System.out.println("Precio inválido");
				}
			} while (nuevoPrecio == 0);

		}
		case "tipo" -> {
			TipoInstrumento nuevoTipo = null;
			do {
				System.out.print("Nuevo tipo (ACCION o BONO): ");
				String input = scanner.nextLine().toUpperCase();

				try {
					nuevoTipo = TipoInstrumento.valueOf(input);
					instrumento.setTipo(nuevoTipo);
					System.out.println("Tipo actualizado");
				} catch (IllegalArgumentException e) {
					System.out.println("Tipo inválido");
				}
			} while (nuevoTipo == null);
		}
		default -> System.out.println("Atributo no válido");
		}
	}

	public void eliminar() {
		String nombre = "";
		do {
			System.out.print("Ingrese nombre del instrumento a eliminar: ");
			nombre = scanner.nextLine();

			InstrumentoFinanciero instrumento = buscarInstrumento(nombre);
			if (instrumento == null) {
				System.out.println("Instrumento no encontrado");
			} else {
				instrumentos.remove(instrumento);
				System.out.println("Instrumento eliminado");
			}
		} while (nombre.isBlank());
	}

	public InstrumentoFinanciero buscarInstrumento(String nombre) {
		for (InstrumentoFinanciero i : instrumentos) {
			if (i.getNombre().equalsIgnoreCase(nombre)) {
				return i;
			}
		}
		return null;
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
