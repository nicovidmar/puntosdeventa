package teamcubation;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {

	public static List<InstrumentoFinanciero> instrumentos = new ArrayList<>();
	public static Scanner scanner = new Scanner(System.in);
	public static MenuUI menu = new MenuUI();
	public static InstrumentoService service = new InstrumentoService();
	public static final String OPCION_INVALIDA = "Opción inválida";
	public static final String SALIENDO_DEL_PROGRAMA = "Saliendo del programa";
	
	public static void main(String[] args) {

		int opcion = 0;
		do {
			try {
				menu.mostrarMenu();
				opcion = Integer.parseInt(scanner.nextLine());

				switch (opcion) {
				case 1 -> service.registrarInstrumento();
				case 2 -> service.consultarInstrumentos();
				case 3 -> service.editarAtributo();
				case 4 -> service.eliminar();
				case 5 -> System.out.println(SALIENDO_DEL_PROGRAMA);
				default -> System.out.println(OPCION_INVALIDA);
				}
			} catch (Exception e) {
				System.out.println(OPCION_INVALIDA);
			}

		} while (opcion != 5);

		scanner.close();
	}
		
}
