package teamcubation;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static ArrayList<Accion> acciones = new ArrayList<>();
	public static ArrayList<Bono> bonos = new ArrayList<>();
	public static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {

		int opcion = 0;
		do {
			try {
				mostrarMenu();
				opcion = Integer.parseInt(scanner.nextLine());

				switch (opcion) {
				case 1 -> registrarInstrumento();
				case 2 -> consultarInstrumentos();
				case 3 -> editarAtributo();
				case 4 -> eliminar();
				case 5 -> System.out.println("Saliendo del programa");
				default -> System.out.println("Opción inválida");
				}
			} catch (Exception e) {
				System.out.println("Opción inválida");
			}

		} while (opcion != 5);

		scanner.close();
	}

	private static void mostrarMenu() {
		System.out.println("1. Registrar instrumento");
		System.out.println("2. Consultar instrumentos");
		System.out.println("3. Editar atributo");
		System.out.println("4. Eliminar instrumento");
		System.out.println("5. Salir");
		System.out.print("Seleccione una opción: ");
	}

	private static void consultarInstrumentos() {
		int opcion;
		System.out.println("1. Consultar todos los instrumentos");
		System.out.println("2. Consultar por nombre");
		opcion = Integer.parseInt(scanner.nextLine());

		switch (opcion) {
		case 1 -> {
			if (acciones.isEmpty()) {
				System.out.println("La lista de acciones está vacía");
			} else {
				System.out.println(acciones);
			}

			if (bonos.isEmpty()) {
				System.out.println("La lista de bonos está vacía");
			} else {
				System.out.println(bonos);
			}
		}
		case 2 -> System.out.println(consultarPorNombre());
		default -> System.out.println("Opción inválida");
		}
	}

	private static void registrarInstrumento() {
		String nombre = "";
		while (nombre == "") {
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
					acciones.add(new Accion(nombre, precio, tipo));
				} else if (tipo == TipoInstrumento.BONO) {
					bonos.add(new Bono(nombre, precio, tipo));
				}
				System.out.println("Instrumento registrado");
			} catch (IllegalArgumentException e) {
				System.out.println("Tipo inválido");
			}
		}

	}

	private static InstrumentoFinanciero consultarPorNombre() {
		String nombre = "";

		while (nombre == "") {
			System.out.println("Ingrese el nombre del instrumento a consultar: ");
			nombre = scanner.nextLine();
			System.out.println("Buscando por nombre");
			InstrumentoFinanciero instrumento = buscarInstrumento(nombre);
			if (instrumento == null) {
				System.out.println("No se encontró instrumento con nombre " + nombre);
			}
			return instrumento;
		}
		return null;
	}

	private static void editarAtributo() {
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
		while (atributo == "") {
			System.out.println("Atributo a modificar (nombre, precio o tipo): ");
			atributo = scanner.nextLine().toLowerCase();
		}

		switch (atributo) {
		case "nombre" -> {
			String nuevoNombre = "";
			do {
				System.out.println("Nuevo nombre: ");
				nuevoNombre = scanner.nextLine();
			} while (nuevoNombre == "");

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

	private static void eliminar() {
		String nombre = "";
		do {
			System.out.print("Ingrese nombre del instrumento a eliminar: ");
			nombre = scanner.nextLine();

			InstrumentoFinanciero instrumento = buscarInstrumento(nombre);
			if (instrumento == null) {
				System.out.println("Instrumento no encontrado");
			} else {
				if (instrumento.getTipo().equals(TipoInstrumento.ACCION)) {
					acciones.remove(instrumento);
					System.out.println("Acción eliminada");
				}
				if (instrumento.getTipo().equals(TipoInstrumento.BONO)) {
					bonos.remove(instrumento);
					System.out.println("Bono eliminado");
				}
			}
		} while (nombre == "");
	}

	private static InstrumentoFinanciero buscarInstrumento(String nombre) {
		for (Accion a : acciones) {
			if (a.getNombre().equalsIgnoreCase(nombre)) {
				return a;
			}
		}
		for (Bono b : bonos) {
			if (b.getNombre().equalsIgnoreCase(nombre)) {
				return b;
			}
		}
		return null;
	}

}
