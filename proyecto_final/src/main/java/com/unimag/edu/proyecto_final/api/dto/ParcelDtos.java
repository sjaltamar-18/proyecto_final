package com.unimag.edu.proyecto_final.api.dto;

import java.io.Serializable;

public class ParcelDtos {
    public record ParcelCreateRequest(String code, String senderName, String senderPhone, String receiverName, String receiverPhone, Long fromStopId, Long toStopId, Double price) implements Serializable {}
    public record ParcelResponse(Long id, String code, String senderName, String senderPhone, String receiverName, String receiverPhone, Long fromStopId, Long toStopId, Double price, String status, String proofPhotoUrl, String deliveryOtp) implements Serializable {}
    public record ParcelUpdateRequest(String status, String proofPhotoUrl, String deliveryOtp) implements Serializable {}

}
