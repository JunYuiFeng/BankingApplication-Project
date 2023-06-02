package nl.inholland.bankingapplication.models.dto;

import lombok.AllArgsConstructor;

import java.sql.Timestamp;
@AllArgsConstructor
public class TransactionResponseDTO {
    public Long id;
    public Double amount;
    public Integer madeBy;
    public String accountFrom;
    public String accountTo;
    public String description;
    public Timestamp occurredAt;
}
