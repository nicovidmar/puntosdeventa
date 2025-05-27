package com.cavallaro.kafka.controller;

import com.cavallaro.kafka.dto.AccreditationRequest;
import com.cavallaro.kafka.model.Accreditation;
import com.cavallaro.kafka.service.impl.AccreditationService;
import com.cavallaro.kafka.service.impl.NotifServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping(value = "/api/v1/accreditations",  produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class AccreditationsController {


    private final AccreditationService service;
    private final CircuitBreakerFactory cBreakerFactory;
    private final NotifServiceImpl notifyService;

    public AccreditationsController(AccreditationService service,
                                    CircuitBreakerFactory cBreakerFactory,
                                    NotifServiceImpl notifyService) {
        this.service = service;
        this.cBreakerFactory = cBreakerFactory;
        this.notifyService =notifyService;
    }

    @GetMapping
    public ResponseEntity<List<Accreditation>> findAll() {
        log.info("llamada a un metodo del controller AccreditationsController::findAll ");

        log.info("Request Parameter: {}", "NA");

        ResponseEntity<List<Accreditation>>  responseOk = ResponseEntity.ok(service.findAll());
        log.info("Response Parameter: {}", responseOk);

        return responseOk;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Accreditation> findById(@PathVariable String id) {

        log.info("llamada a un metodo del controller AccreditationsController::findById");

        log.info("Request Parameter: id {}", id);

        ResponseEntity<Accreditation> accreditationResponseEntity = service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

        log.info("Response Parameter: {}", accreditationResponseEntity);

        return accreditationResponseEntity;
    }

    @PostMapping
    public ResponseEntity<Accreditation> save(@RequestBody AccreditationRequest accreditation) {

        log.info("llamada a un metodo del controller AccreditationsController::save");

        log.info("Request Parameter: accreditation{}", accreditation);


        ResponseEntity<Accreditation> response = cBreakerFactory.create("save")
                .run(()-> new ResponseEntity<>(service.save(accreditation), HttpStatus.CREATED),
                        e->{
                                this.notifyService.sendNotif("cache:empty:selling-points");
                                log.info("Llamar el servicio kafka para token carga la cache: {}", accreditation.toString());
                             return fallbackMethod(accreditation.getSellingPointId().toString(), e);
                        });


        log.info("Response Parameter: {}", response);

        return  response;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Accreditation> update(@PathVariable String id, @RequestBody Accreditation accreditation) {

        log.info("llamada a un metodo del controller AccreditationsController::update");

        log.info("Request Parameter: accreditation{}", accreditation);

        ResponseEntity<Accreditation> accreditationResponseEntity = ResponseEntity.ok(service.update(id, accreditation));

        log.info("Response Parameter: {}", accreditationResponseEntity);

        return accreditationResponseEntity;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {

        log.info("llamada a un metodo del controller AccreditationsController::deleteById");

        log.info("Request Parameter: id{}", id);

        service.deleteById(id);
        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        log.info("Response Parameter: {}", response);

        return response;
    }

    private ResponseEntity<Accreditation> fallbackMethod(String id, Throwable throwable) {
        // Lógica de fallback
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
