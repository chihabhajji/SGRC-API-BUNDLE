package bte.sgrc.SpringBackend.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class UserSummary implements Serializable {
    private static final long serialVersionUID = 1L;
    @Getter @Setter SummaryData october = new SummaryData();
    @Getter @Setter SummaryData november = new SummaryData();
    @Getter @Setter SummaryData july = new SummaryData();
    @Getter @Setter SummaryData june = new SummaryData();
    @Getter @Setter SummaryData april = new SummaryData();
    @Getter @Setter SummaryData may = new SummaryData();
    @Getter @Setter SummaryData september = new SummaryData();
    @Getter @Setter SummaryData december = new SummaryData();
    @Getter @Setter SummaryData august = new SummaryData();
    @Getter @Setter SummaryData march = new SummaryData();
    @Getter @Setter SummaryData february = new SummaryData();
    @Getter @Setter SummaryData january = new SummaryData();

}