package bte.sgrc.SpringBackend.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class UserSummary implements Serializable {
    private static final long serialVersionUID = 1L;
    @Getter @Setter SummaryData october;
    @Getter @Setter SummaryData november;
    @Getter @Setter SummaryData july;
    @Getter @Setter SummaryData june;
    @Getter @Setter SummaryData april;
    @Getter @Setter SummaryData may;
    @Getter @Setter SummaryData september;
    @Getter @Setter SummaryData december;
    @Getter @Setter SummaryData august;
    @Getter @Setter SummaryData march;
    @Getter @Setter SummaryData february;
    @Getter @Setter SummaryData january;
}