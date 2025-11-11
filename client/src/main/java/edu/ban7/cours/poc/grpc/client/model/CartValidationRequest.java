package edu.ban7.cours.poc.grpc.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartValidationRequest {
    private String cartId;
    private Double amount;
    private String currency;
    private String paymentMethod;
    private CustomerInfo customer;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private String customerId;
        private String email;
        private String name;
    }
}