package bte.sgrc.SpringBackend.api.dto;

import lombok.Getter;
import lombok.Setter;

public class SummaryData {
    @Getter @Setter int amountApproved=0;
    @Getter @Setter int amountDisaproved=0;
    public void addApproved(){
        this.amountApproved++;
    }
    public void addDisaproved(){
        this.amountDisaproved++;
    }
}