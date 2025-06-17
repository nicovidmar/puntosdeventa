package com.accreditationservice.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.accreditationservice.dto.AccreditationRequest;
import com.accreditationservice.dto.BillEvent;
import com.accreditationservice.dto.PointOfSaleDTO;
import com.accreditationservice.entity.Accreditation;
import com.accreditationservice.repository.AccreditationRepository;
import com.accreditationservice.webclient.WebClientPosService;

import io.jsonwebtoken.Jwts;

@Service
public class AccreditationService {

    private final AccreditationRepository repo;
    private final WebClientPosService pointOfSaleService;
    private final RabbitTemplate rabbitTemplate;
    private final Logger logger = LoggerFactory.getLogger(AccreditationService.class);

    @Value("${jwt.secret.key}")
    private String secretKey;

    public AccreditationService(AccreditationRepository repo, WebClientPosService pointOfSaleService,
            RabbitTemplate rabbitTemplate) {
        this.repo = repo;
        this.pointOfSaleService = pointOfSaleService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Accreditation create(AccreditationRequest request, String jwtToken) {
        PointOfSaleDTO pos = pointOfSaleService.findById(request.getPointOfSaleId(), jwtToken);

        Accreditation acc = new Accreditation();
        acc.setAmount(request.getAmount());
        acc.setPointOfSaleId(pos.getId());
        acc.setPointOfSaleName(pos.getName());
        acc.setDate(new Date());

        Accreditation saved = repo.save(acc);

        BillEvent event = new BillEvent();
        event.setEmail(this.extractUsername(jwtToken));
        event.setAmount(saved.getAmount());
        event.setPointOfSaleName(saved.getPointOfSaleName());
        event.setDate(saved.getDate());

        try {
            rabbitTemplate.convertAndSend("bill.queue", event);
        } catch (AmqpException e) {
            logger.info("No se pudo enviar el evento de factura: " + e.getMessage());
        }

        return saved;
    }

    public List<Accreditation> findAll() {
        return repo.findAll();
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
