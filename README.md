# Gestión de Acciones y Bonos
# Contexto
Este proyecto fue desarrollado como parte de un ejercicio de Programación Orientada a Objetos (POO). El objetivo es simular un sistema que permita registrar, consultar, editar y eliminar instrumentos financieros como Acciones y Bonos mediante una interfaz de consola en Java.

# Descripción 
Permite a usuarios gestionar un portafolio básico de instrumentos financieros, brindando una forma sencilla de realizar operaciones CRUD sobre datos cargados por consola y almacenados temporalmente en memoria.

# Objetivos principales
Implementar el paradigma de POO.

Crear una jerarquía de clases con herencia y polimorfismo.

Permitir operaciones CRUD mediante menú por consola.

Validar entradas del usuario para evitar errores.

#  Estructura
teamcubation/

├── Main.java

├── InstrumentoFinanciero.java (abstracta)

├── Accion.java

├── Bono.java

├── InstrumentoService.java

├── MenuUI.java

└── TipoInstrumento.java (enum)

# Especificaciones Técnicas
Lenguaje: Java 17

# Conceptos de Java aplicados
Herencia: Accion y Bono extienden de InstrumentoFinanciero.

Polimorfismo: Métodos genéricos que manejan ambos tipos.

Enumeraciones: TipoInstrumento define constantes tipadas (ACCION, BONO).

Excepciones: Manejo de errores con try-catch para entradas inválidas.
