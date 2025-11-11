package edu.ban7.cours.poc.grpc.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartValidationResponse {
    private boolean success;
    private String transactionId;
    private String status;
    private String message;
    private Long timestamp;
}