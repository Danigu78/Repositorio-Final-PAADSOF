package test.tienda;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Excepcion.*;

public class TestExcepciones {

  
    @Test
    public void testCheckPointException() {
        CheckPointException e = new CheckPointException("Error base");
        assertEquals("Error base", e.getMessage());
        assertTrue(e instanceof RuntimeException);
    }

   
    @Test
    public void testAñoInvalidoException() {
        AñoInvalidoException e = new AñoInvalidoException(-5);
     
        assertEquals(-5, e.getAño()); 
        assertTrue(e.getMessage().contains("-5"));
        assertTrue(e instanceof CheckPointException);
    }

   
    @Test
    public void testFicheroFormatoInvalidoExceptionAtravesDeHija() {
      
        TipoProductoDesconocidoException e = 
            new TipoProductoDesconocidoException(10, "L;PRODUCTO;X", "X");
        
        
        assertTrue(e.getMessage().contains("Error en línea 10"));
        assertTrue(e.getMessage().contains("Tipo de producto desconocido: X"));
        assertTrue(e instanceof FicheroFormatoInvalidoException);
    }

   
    @Test
    public void testOfertaNoDisponibleException() {
        OfertaNoDisponibleException e = new OfertaNoDisponibleException("OFER-1");
     
        assertEquals("OFER-1", e.getIdOferta()); 
        assertTrue(e instanceof CheckPointException);
    }

   
    @Test
    public void testPesosInvalidosException() {
        PesosInvalidosException e = new PesosInvalidosException(-1.0, 0.5, 0.5);
      
        assertNotNull(e.getMessage());
        assertTrue(e instanceof CheckPointException);
    }

  
    @Test
    public void testProductoYaEnCategoriaException() {
      
        ProductoYaEnCategoriaException e1 = new ProductoYaEnCategoriaException();
        assertTrue(e1.getMessage().contains("categoría"));
        
     
        ProductoYaEnCategoriaException e2 = new ProductoYaEnCategoriaException("Error personalizado");
        assertEquals("Error personalizado", e2.getMessage());
    }

  
    @Test
    public void testReseñaDuplicadaException() {
        ReseñaDuplicadaException e = new ReseñaDuplicadaException("PROD-100");
        assertTrue(e.getMessage().contains("PROD-100"));
        assertTrue(e instanceof CheckPointException);
    }
  @Test
    public void testEmpleadoDadoDeBajaException() {
        EmpleadoDadoDeBajaException e = new EmpleadoDadoDeBajaException("juan_perez");
        assertTrue(e.getMessage().contains("juan_perez"));
        assertEquals("juan_perez", e.getNickname());
    }

  
    @Test
    public void testPagoFallidoException() {
        PagoFallidoException e = new PagoFallidoException();
        assertTrue(e.getMessage().contains("fallado"));
        assertTrue(e instanceof CheckPointException);
    }

    
    @Test
    public void testValoracionInvalidaException() {
        ValoracionInvalidaException e = new ValoracionInvalidaException("Nota 11 fuera de rango");
        assertTrue(e.getMessage().contains("11"));
        assertTrue(e instanceof CheckPointException);
    }



    @Test
    public void testLanzamientoAñoInvalido() {
     
        assertThrows(AñoInvalidoException.class, () -> {
            throw new AñoInvalidoException(-2026);
        });
    }

    @Test
    public void testLanzamientoProductoBloqueado() {
        assertThrows(ProductoBloqueadoException.class, () -> {
            throw new ProductoBloqueadoException("PROD-001");
        });
    }

    @Test
    public void testLanzamientoPagoFallido() {
        assertThrows(PagoFallidoException.class, () -> {
            throw new PagoFallidoException();
        });
    }

    @Test
    public void testLanzamientoValoracionInvalida() {
        assertThrows(ValoracionInvalidaException.class, () -> {
            throw new ValoracionInvalidaException("Nota 15 no permitida");
        });
    }

  
    @Test
    public void testLanzamientoReseñaDuplicada() {
        assertThrows(ReseñaDuplicadaException.class, () -> {
            throw new ReseñaDuplicadaException("ID-USER-123");
        });
    }
    
}