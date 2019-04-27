package bte.sgrc.SpringBackend.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class Summary implements Serializable{
    private static final long serialVersionUID = 1L;
    @Getter @Setter private Integer amountNew;
    @Getter @Setter private Integer amountResolved;
    @Getter @Setter private Integer amountApproved;
    @Getter @Setter private Integer amountDisapproved;
    @Getter @Setter private Integer amountAssigned;
    @Getter @Setter private Integer amountClosed;

}