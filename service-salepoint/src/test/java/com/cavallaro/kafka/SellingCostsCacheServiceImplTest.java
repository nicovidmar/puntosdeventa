package com.cavallaro.kafka;



import com.cavallaro.kafka.cache.CacheableService;
import com.cavallaro.kafka.dto.SellingCostDTO;
import com.cavallaro.kafka.exception.SellingCostsServiceException;
import com.cavallaro.kafka.model.SellingCost;
import com.cavallaro.kafka.model.SellingCostId;
import com.cavallaro.kafka.repository.SellingCostsRepository;
import com.cavallaro.kafka.service.impl.SellingCostsCacheServiceImpl;
import com.cavallaro.kafka.validation.SellingCostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class SellingCostsCacheServiceImplTest {

    @Mock
    private SellingCostsRepository sellingCostsRepository;

    @Mock
    private CacheableService<SellingCostDTO, SellingCostId> sellingCostCache;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private SellingCostValidator sellingCostValidator;

    @InjectMocks
    private SellingCostsCacheServiceImpl sellingCostsCacheService;

    private SellingCostDTO sellingCostDTO;
    private SellingCost sellingCost;
    private SellingCostId sellingCostId;

    @BeforeEach
    public void setup() {
        sellingCostId = new SellingCostId(1, 2);
        sellingCostDTO = new SellingCostDTO(sellingCostId, 100.0);
        sellingCost = new SellingCost(sellingCostId, 100.0);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindAll() {
        when(sellingCostsRepository.findAll()).thenReturn(List.of(sellingCost));
        when(modelMapper.map(any(SellingCost.class), eq(SellingCostDTO.class))).thenReturn(sellingCostDTO);
        List<SellingCostDTO> result = sellingCostsCacheService.findAll();
        assertEquals(1, result.size());
        assertEquals(sellingCostDTO, result.get(0));
    }

    @Test
    public void testFindId() {
        when(sellingCostsRepository.findById(sellingCostId)).thenReturn(Optional.of(sellingCost));
        when(modelMapper.map(sellingCost, SellingCostDTO.class)).thenReturn(sellingCostDTO);
        Optional<SellingCostDTO> result = sellingCostsCacheService.findId(1, 2);
        assertTrue(result.isPresent());
        assertEquals(sellingCostDTO, result.get());
    }

    @Test
    public void testSave() {
        when(sellingCostsRepository.existsById(sellingCostId)).thenReturn(false);
        when(sellingCostsRepository.save(any(SellingCost.class))).thenReturn(sellingCost);
        when(modelMapper.map(any(SellingCostDTO.class), eq(SellingCost.class))).thenReturn(sellingCost);
        when(modelMapper.map(any(SellingCost.class), eq(SellingCostDTO.class))).thenReturn(sellingCostDTO);

        SellingCostDTO result = sellingCostsCacheService.save(sellingCostDTO);
        assertEquals(sellingCostDTO, result);
        verify(sellingCostCache).save(sellingCostDTO);
    }

    @Test
    public void testRemove() {
        when(sellingCostsRepository.findById(sellingCostId)).thenReturn(Optional.of(sellingCost));

        boolean result = sellingCostsCacheService.remove(1, 2);
        assertTrue(result);
        verify(sellingCostsRepository).deleteById(sellingCostId);
        verify(sellingCostCache).remove(sellingCostId);
    }

    @Test
    public void testUpdate() {
        when(sellingCostsRepository.findById(sellingCostId)).thenReturn(Optional.of(sellingCost));
        when(sellingCostsRepository.save(any(SellingCost.class))).thenReturn(sellingCost);
        when(modelMapper.map(sellingCost, SellingCostDTO.class)).thenReturn(sellingCostDTO);

        Optional<SellingCostDTO> result = sellingCostsCacheService.update(1, 2, 150.0);
        assertTrue(result.isPresent());
        assertEquals(sellingCostDTO, result.get());
        verify(sellingCostCache).update(sellingCostId, sellingCostDTO);
    }

    @Test
    public void testSave_ExistingCost() {
        when(sellingCostsRepository.existsById(any(SellingCostId.class))).thenReturn(true);
        when(modelMapper.map(sellingCost.getId(), SellingCostId.class)).thenReturn(sellingCostId);
        assertThrows(SellingCostsServiceException.class, () -> sellingCostsCacheService.save(sellingCostDTO));
    }

    @Test
    public void testRemove_NonExistent() {
        when(sellingCostsRepository.findById(sellingCostId)).thenReturn(Optional.empty());

        boolean result = sellingCostsCacheService.remove(1, 2);
        assertFalse(result);
    }

    @Test
    public void testUpdate_NonExistent() {
        when(sellingCostsRepository.findById(sellingCostId)).thenReturn(Optional.empty());
        assertThrows(SellingCostsServiceException.class, () -> sellingCostsCacheService.update(1, 2, 150.0));
    }
}

