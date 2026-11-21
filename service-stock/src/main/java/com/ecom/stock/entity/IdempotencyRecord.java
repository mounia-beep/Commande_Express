package com.ecom.stock.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_keys")
public class IdempotencyRecord {

    @Id
    @Column(name = "idem_key", length = 120)
    private String key;

    @Column(nullable = false)
    private Long stockId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected IdempotencyRecord() {}

    public IdempotencyRecord(String key, Long stockId) {
        this.key = key;
        this.stockId = stockId;
    }

    public String getKey() { return key; }
    public Long getStockId() { return stockId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}