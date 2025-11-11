package edu.ban7.cours.poc.grpc.client.controller;

import edu.ban7.cours.poc.grpc.client.model.CartValidationRequest;
import edu.ban7.cours.poc.grpc.client.model.CartValidationResponse;
import edu.ban7.cours.poc.grpc.client.service.PaymentClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final PaymentClientService paymentClientService;

    @PostMapping("/validate")
    public ResponseEntity<CartValidationResponse> validateCart(
            @RequestBody CartValidationRequest request) {

        log.info("Réception de la demande de validation du panier: {}", request.getCartId());

        // Validation basique
        if (request.getCartId() == null || request.getAmount() == null || request.getAmount() <= 0) {
            return ResponseEntity.badRequest()
                    .body(CartValidationResponse.builder()
                            .success(false)
                            .status("ERROR")
                            .message("Données du panier invalides")
                            .timestamp(System.currentTimeMillis())
                            .build());
        }

        // Appel du service de paiement via gRPC
        CartValidationResponse response = paymentClientService.processPayment(request);

        // Retour de la réponse avec le statut HTTP approprié
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Cart API is running");
    }
}