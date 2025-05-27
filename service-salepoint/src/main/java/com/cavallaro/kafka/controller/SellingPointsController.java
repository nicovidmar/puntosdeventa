package com.cavallaro.kafka.controller;


import com.cavallaro.kafka.dto.ApiResponse;
import com.cavallaro.kafka.dto.SellingPointDTO;
import com.cavallaro.kafka.service.SellingPointService;
import jakarta.validation.Valid;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/selling-points",  produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor


public class SellingPointsController implements SellingPointsApi {
    private  final SellingPointService sellingPointService;

    @GetMapping()
    @Override
    public ResponseEntity<ApiResponse<List<SellingPointDTO>>> getSellingPoints() {

        ResponseEntity<ApiResponse<List<SellingPointDTO>>> response;
        log.info("llamada a un metodo del controller SellingPointsController::getSellingPoints ");

        log.info("Request Parameter: {}", "NA");
        ApiResponse<List<SellingPointDTO>>   sellingPoint =  sellingPointService.findAll();

        if (sellingPoint == null || sellingPoint.getData() == null || sellingPoint.getData().isEmpty()) {
            response = ResponseEntity.noContent().build();
        } else  {
            response=  ResponseEntity.ok(sellingPoint);
        }

        log.info("Response Parameter: {}", sellingPoint);

        return response;
    }

    @PostMapping()
    @Override
    public ResponseEntity<ApiResponse<SellingPointDTO>> addSellingPoint(@Valid @RequestBody SellingPointDTO sellingPointDTO) {

        ResponseEntity<ApiResponse<SellingPointDTO>> response;

        log.info("llamada a un metodo del controller SellingPointsController::addSellingPoint ");

        log.info("Request Parameter: {}", sellingPointDTO);

        if (sellingPointDTO == null || sellingPointDTO.getId() <= 0) {
            response = ResponseEntity.badRequest().build();
        } else {
            ApiResponse<SellingPointDTO> createdSellingPoint = sellingPointService.save(sellingPointDTO);

            response = ResponseEntity.status(HttpStatus.CREATED)
                    .body(createdSellingPoint);
        }

        log.info("Response Parameter: {}", response);


        return  response;
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<ApiResponse<SellingPointDTO>> updateSellingPoint(@Valid @PathVariable Integer id, @Valid @RequestBody String name) {

        ResponseEntity<ApiResponse<SellingPointDTO>> response;
        log.info("llamada  a metodo del controller SellingPointsController::updateSellingPoint ");

        log.info("Request Parameter: {}", id);
        log.info("Request Parameter: {}", name);


        if (id == null || id <= 0 || StringUtil.isBlank(name)) {
            response = ResponseEntity.badRequest().build();
        } else {

            ApiResponse<SellingPointDTO> sellingPoint = sellingPointService.update(id, name);

            response = ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(sellingPoint);
        }

        log.info("Response Parameter: {}", response);

        return response;

    }

    @DeleteMapping("{id}")
    @Override
    public ResponseEntity<ApiResponse<Boolean>> deleteSellingPoint(@Valid @PathVariable Integer id) {

        ResponseEntity<ApiResponse<Boolean>> response;

        log.info("llamada  a metodo del controller SellingPointsController::deleteSellingPoint ");

        log.info("Request Parameter: {}", id);


        if (id == null || id <= 0 ) {
            response = ResponseEntity.badRequest().build();
        } else {

            ApiResponse<Boolean> sellingPoint = sellingPointService.remove(id);

            if (HttpStatusCode.valueOf(sellingPoint.getHeader().getCode()).is2xxSuccessful()) {
                response = ResponseEntity.status(HttpStatus.ACCEPTED)
                        .body(sellingPoint);

            } else if (HttpStatusCode.valueOf(sellingPoint.getHeader().getCode()).is4xxClientError()) {
                response = ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(sellingPoint);

            } else {
                response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(sellingPoint);
            }
        }

        log.info("Response Parameter: {}", response);

        return response;
    }


}
