package pl.rkuba.drivinglicencetest.model.dto;

import java.time.LocalDate;

public interface UserStats {
    LocalDate getDate();
    Long getTotal();
}
