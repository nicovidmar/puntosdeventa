package com.accreditationservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;

import com.accreditationservice.dto.AccreditationRequest;
import com.accreditationservice.dto.PointOfSaleDTO;
import com.accreditationservice.entity.Accreditation;
import com.accreditationservice.repository.AccreditationRepository;
import com.accreditationservice.service.AccreditationService;
import com.accreditationservice.webclient.WebClientPosService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

class AccreditationServiceTests {

    @Mock
    private AccreditationRepository repo;

    @Mock
    private WebClientPosService posService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AccreditationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Forzar la clave secreta (necesaria para parsear JWT en extractUsername)
        service = spy(service);
        doReturn("test@test.com").when(service).extractUsername(any());
    }

    @Test
    void testCreate_OK() {
        AccreditationRequest req = new AccreditationRequest(100.0, 1);
        PointOfSaleDTO posDto = new PointOfSaleDTO(1, "POS 1");
        Accreditation saved = new Accreditation(1L, 100.0, 1, "POS 1", new Date());

        when(posService.findById(1, "jwt")).thenReturn(posDto);
        when(repo.save(any())).thenReturn(saved);

        Accreditation result = service.create(req, "jwt");

        assertNotNull(result);
        assertEquals(100.0, result.getAmount());
        assertEquals("POS 1", result.getPointOfSaleName());

        verify(repo).save(any());
    }

    @Test
    void testCreate_Error() {
        AccreditationRequest request = new AccreditationRequest(100.0, 1);
        String jwt = "token";

        PointOfSaleDTO pos = new PointOfSaleDTO(1, "POS 1");

        when(posService.findById(1, jwt)).thenReturn(pos);
        when(repo.save(any())).thenThrow(new RuntimeException("Error en BBDD"));

        assertThrows(RuntimeException.class, () -> service.create(request, jwt));
    }

    @Test
    void testFindAll_OK() {
        Accreditation acc1 = new Accreditation(1L, 100.0, 1, "POS 1", new Date());
        Accreditation acc2 = new Accreditation(2L, 200.0, 2, "POS 2", new Date());

        when(repo.findAll()).thenReturn(List.of(acc1, acc2));

        List<Accreditation> result = service.findAll();

        assertEquals(2, result.size());
        verify(repo).findAll();
    }

    @Test
    void testFindAll_Error() {
        when(repo.findAll()).thenThrow(new RuntimeException("Error en BBDD"));

        assertThrows(RuntimeException.class, () -> service.findAll());
    }

}
