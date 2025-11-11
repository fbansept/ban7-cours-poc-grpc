package edu.ban7.cours.poc.grpc.server.service;

import edu.ban7.cours.poc.grpc.payment.*;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@GrpcService
public class PaymentServiceImpl extends PaymentServiceGrpc.PaymentServiceImplBase {

    // Stockage en mémoire des transactions (pour la démo)
    private final Map<String, PaymentTransaction> transactions = new ConcurrentHashMap<>();

    @Override
    public void processPayment(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        log.info("Réception d'une demande de paiement: cartId={}, amount={}, currency={}, method={}",
                request.getCartId(),
                request.getAmount(),
                request.getCurrency(),
                request.getPaymentMethod());

        try {
            // Génération d'un ID de transaction
            String transactionId = UUID.randomUUID().toString();

            // Simulation de la logique de paiement
            PaymentStatus status;
            String message;
            boolean success;

            // Validation basique
            if (request.getAmount() <= 0) {
                status = PaymentStatus.ERROR;
                message = "Montant invalide";
                success = false;
            } else if (request.getAmount() > 10000) {
                // Simulation: les paiements > 10000 sont refusés
                status = PaymentStatus.DECLINED;
                message = "Montant trop élevé pour un paiement automatique";
                success = false;
            } else if (request.getCustomer().getEmail() == null || request.getCustomer().getEmail().isEmpty()) {
                status = PaymentStatus.ERROR;
                message = "Email client requis";
                success = false;
            } else {
                // Simulation: 90% de réussite
                double random = Math.random();
                if (random < 0.9) {
                    status = PaymentStatus.APPROVED;
                    message = "Paiement approuvé avec succès";
                    success = true;
                } else {
                    status = PaymentStatus.DECLINED;
                    message = "Paiement refusé par la banque";
                    success = false;
                }
            }

            // Stockage de la transaction
            PaymentTransaction transaction = new PaymentTransaction(
                    transactionId,
                    request.getCartId(),
                    request.getAmount(),
                    status,
                    System.currentTimeMillis()
            );
            transactions.put(transactionId, transaction);

            // Construction de la réponse
            PaymentResponse response = PaymentResponse.newBuilder()
                    .setSuccess(success)
                    .setTransactionId(transactionId)
                    .setStatus(status)
                    .setMessage(message)
                    .setTimestamp(System.currentTimeMillis())
                    .build();

            log.info("Paiement traité: transactionId={}, status={}, success={}",
                    transactionId, status, success);

            // Envoi de la réponse
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Erreur lors du traitement du paiement", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Erreur interne lors du traitement du paiement: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void checkPaymentStatus(PaymentStatusRequest request, StreamObserver<PaymentStatusResponse> responseObserver) {
        log.info("Vérification du statut de transaction: {}", request.getTransactionId());

        try {
            PaymentTransaction transaction = transactions.get(request.getTransactionId());

            PaymentStatusResponse.Builder responseBuilder = PaymentStatusResponse.newBuilder()
                    .setTransactionId(request.getTransactionId());

            if (transaction != null) {
                responseBuilder
                        .setStatus(transaction.getStatus())
                        .setAmount(transaction.getAmount())
                        .setMessage("Transaction trouvée");
            } else {
                responseBuilder
                        .setStatus(PaymentStatus.ERROR)
                        .setAmount(0.0)
                        .setMessage("Transaction introuvable");
            }

            PaymentStatusResponse response = responseBuilder.build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Statut de transaction retourné: {}", response.getStatus());

        } catch (Exception e) {
            log.error("Erreur lors de la vérification du statut", e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Erreur lors de la vérification du statut")
                    .asRuntimeException());
        }
    }

    // Classe interne pour stocker les transactions
    private static class PaymentTransaction {
        private final String transactionId;
        private final String cartId;
        private final double amount;
        private final PaymentStatus status;
        private final long timestamp;

        public PaymentTransaction(String transactionId, String cartId, double amount,
                                  PaymentStatus status, long timestamp) {
            this.transactionId = transactionId;
            this.cartId = cartId;
            this.amount = amount;
            this.status = status;
            this.timestamp = timestamp;
        }

        public String getTransactionId() { return transactionId; }
        public String getCartId() { return cartId; }
        public double getAmount() { return amount; }
        public PaymentStatus getStatus() { return status; }
        public long getTimestamp() { return timestamp; }
    }
}