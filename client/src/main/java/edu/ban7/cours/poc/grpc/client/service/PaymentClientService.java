package edu.ban7.cours.poc.grpc.client.service;

import edu.ban7.cours.poc.grpc.client.model.CartValidationRequest;
import edu.ban7.cours.poc.grpc.client.model.CartValidationResponse;
import edu.ban7.cours.poc.grpc.payment.CustomerInfo;
import edu.ban7.cours.poc.grpc.payment.PaymentMethod;
import edu.ban7.cours.poc.grpc.payment.PaymentRequest;
import edu.ban7.cours.poc.grpc.payment.PaymentResponse;
import edu.ban7.cours.poc.grpc.payment.PaymentServiceGrpc;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentClientService {

    @GrpcClient("payment-service")
    private PaymentServiceGrpc.PaymentServiceBlockingStub paymentServiceStub;

    public CartValidationResponse processPayment(CartValidationRequest request) {
        try {
            log.info("Envoi de la requête de paiement pour le panier: {}", request.getCartId());

            // Conversion du modèle REST vers le modèle gRPC
            PaymentRequest grpcRequest = PaymentRequest.newBuilder()
                    .setCartId(request.getCartId())
                    .setAmount(request.getAmount())
                    .setCurrency(request.getCurrency())
                    .setPaymentMethod(convertPaymentMethod(request.getPaymentMethod()))
                    .setCustomer(CustomerInfo.newBuilder()
                            .setCustomerId(request.getCustomer().getCustomerId())
                            .setEmail(request.getCustomer().getEmail())
                            .setName(request.getCustomer().getName())
                            .build())
                    .build();

            // Appel gRPC
            PaymentResponse grpcResponse = paymentServiceStub.processPayment(grpcRequest);

            log.info("Réponse reçue du service de paiement: transactionId={}, status={}",
                    grpcResponse.getTransactionId(), grpcResponse.getStatus());

            // Conversion de la réponse gRPC vers le modèle REST
            return CartValidationResponse.builder()
                    .success(grpcResponse.getSuccess())
                    .transactionId(grpcResponse.getTransactionId())
                    .status(grpcResponse.getStatus().name())
                    .message(grpcResponse.getMessage())
                    .timestamp(grpcResponse.getTimestamp())
                    .build();

        } catch (StatusRuntimeException e) {
            log.error("Erreur lors de l'appel gRPC: {}", e.getStatus());
            return CartValidationResponse.builder()
                    .success(false)
                    .status("ERROR")
                    .message("Erreur de communication avec le service de paiement: " + e.getStatus().getDescription())
                    .timestamp(System.currentTimeMillis())
                    .build();
        } catch (Exception e) {
            log.error("Erreur inattendue lors du traitement du paiement", e);
            return CartValidationResponse.builder()
                    .success(false)
                    .status("ERROR")
                    .message("Erreur inattendue: " + e.getMessage())
                    .timestamp(System.currentTimeMillis())
                    .build();
        }
    }

    private PaymentMethod convertPaymentMethod(String method) {
        try {
            return PaymentMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Méthode de paiement inconnue: {}, utilisation de CREDIT_CARD par défaut", method);
            return PaymentMethod.CREDIT_CARD;
        }
    }
}