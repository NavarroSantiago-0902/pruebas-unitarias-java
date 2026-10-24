package co.edu.notas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * ENTREGA DEL TALLER
 * Nombre completo: Tu nombre real
 * 1. Bug encontrado (método y línea de CarritoCompras.java):
 *    agregarProducto: la validación usaba "cantidad < 0" y debía ser "cantidad <= 0".
 *    La regla R1 exige cantidad mayor a 0, así que 0 se aceptaba.
 * 2. Prueba que lo detectó (nombre del método):
 *    agregarProducto_cantidadCero_lanzaExcepcion
 * 3. Sugerencia de la IA que rechacé o corregí, y por qué
 *    (si todas eran correctas: cómo verifiqué una de ellas con las reglas):
 *    Escribe aquí lo que realmente te pasó con la IA, con un argumento basado en R1 a R6.
 */
class CarritoComprasTest {

    private CarritoCompras carrito;

    @BeforeEach
    void setUp() { carrito = new CarritoCompras(); }

    @Test
    @DisplayName("Subtotal = precio × cantidad de cada producto")
    void calcularSubtotal_variosProductos_sumaCorrecta() {
        // Arrange
        carrito.agregarProducto("Cuaderno", 8_000, 3);
        carrito.agregarProducto("Esfero", 2_000, 5);
        // Act
        double subtotal = carrito.calcularSubtotal();
        // Assert
        assertEquals(34_000, subtotal, 0.001);
    }

    // ---------- PARTE A: R1 y R2 ----------

    @Test
    @DisplayName("Carrito vacío: subtotal 0")
    void calcularSubtotal_carritoVacio_retornaCero() {
        assertEquals(0, carrito.calcularSubtotal(), 0.001);
    }

    @Test
    @DisplayName("Carrito vacío: total 0 (sin envío)")
    void calcularTotal_carritoVacio_retornaCero() {
        assertEquals(0, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Nombre nulo lanza excepción")
    void agregarProducto_nombreNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto(null, 1_000, 1));
    }

    @Test
    @DisplayName("Nombre vacío lanza excepción")
    void agregarProducto_nombreVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("", 1_000, 1));
    }

    @Test
    @DisplayName("Nombre en blanco lanza excepción")
    void agregarProducto_nombreEnBlanco_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("   ", 1_000, 1));
    }

    @Test
    @DisplayName("Precio 0 lanza excepción")
    void agregarProducto_precioCero_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Lápiz", 0, 1));
    }

    @Test
    @DisplayName("Precio negativo lanza excepción")
    void agregarProducto_precioNegativo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Lápiz", -500, 1));
    }

    @Test
    @DisplayName("Cantidad 0 lanza excepción (caso límite)")
    void agregarProducto_cantidadCero_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Lápiz", 1_000, 0));
    }

    @Test
    @DisplayName("Cantidad negativa lanza excepción")
    void agregarProducto_cantidadNegativa_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.agregarProducto("Lápiz", 1_000, -1));
    }

    @Test
    @DisplayName("Producto válido se agrega al carrito")
    void agregarProducto_datosValidos_incrementaCantidadDeItems() {
        carrito.agregarProducto("Lápiz", 1_000, 1);
        assertEquals(1, carrito.cantidadDeItems());
    }

    // ---------- PARTE B: R3 a R6 ----------

    @Test
    @DisplayName("SENA10 descuenta 10% del subtotal")
    void calcularDescuento_cuponSena10_retornaDiezPorCiento() {
        carrito.agregarProducto("Cuaderno", 10_000, 4); // subtotal 40.000
        carrito.aplicarCupon("SENA10");
        assertEquals(4_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("BIENVENIDO con subtotal exactamente 50.000 descuenta 5.000")
    void calcularDescuento_bienvenidoEnFrontera50000_descuentaCincoMil() {
        carrito.agregarProducto("Kit", 50_000, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(5_000, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("BIENVENIDO con subtotal 49.999 no descuenta")
    void calcularDescuento_bienvenidoBajoFrontera_noDescuenta() {
        carrito.agregarProducto("Kit", 49_999, 1);
        carrito.aplicarCupon("BIENVENIDO");
        assertEquals(0, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("Sin cupón no hay descuento")
    void calcularDescuento_sinCupon_retornaCero() {
        carrito.agregarProducto("Kit", 60_000, 1);
        assertEquals(0, carrito.calcularDescuento(), 0.001);
    }

    @Test
    @DisplayName("Cupón inválido lanza excepción")
    void aplicarCupon_codigoInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.aplicarCupon("FALSO123"));
    }

    @Test
    @DisplayName("Cupón nulo lanza excepción")
    void aplicarCupon_codigoNulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> carrito.aplicarCupon(null));
    }

    @Test
    @DisplayName("Total con envío pagado: 100.000 → 131.000")
    void calcularTotal_bajoMinimoEnvioGratis_sumaEnvio() {
        carrito.agregarProducto("Libro", 100_000, 1);
        // 100.000 × 1,19 = 119.000 (< 150.000) + 12.000 = 131.000
        assertEquals(131_000, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Total con envío gratis: 130.000 → 154.700")
    void calcularTotal_sobreMinimoEnvioGratis_noSumaEnvio() {
        carrito.agregarProducto("Impresora", 130_000, 1);
        // 130.000 × 1,19 = 154.700 (≥ 150.000) → envío gratis
        assertEquals(154_700, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Total con SENA10: monitor 500.000 → 535.500")
    void calcularTotal_conCuponSena10_aplicaDescuentoEIva() {
        carrito.agregarProducto("Monitor", 500_000, 1);
        carrito.aplicarCupon("SENA10");
        // (500.000 − 50.000) × 1,19 = 535.500, envío gratis
        assertEquals(535_500, carrito.calcularTotal(), 0.001);
    }

    @Test
    @DisplayName("Total con BIENVENIDO: 60.000 → 77.450")
    void calcularTotal_conCuponBienvenido_aplicaDescuentoIvaYEnvio() {
        carrito.agregarProducto("Kit", 60_000, 1);
        carrito.aplicarCupon("BIENVENIDO");
        // (60.000 − 5.000) × 1,19 = 65.450 + 12.000 = 77.450
        assertEquals(77_450, carrito.calcularTotal(), 0.001);
    }
}