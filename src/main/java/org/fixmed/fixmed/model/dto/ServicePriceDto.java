package org.fixmed.fixmed.model.dto;
import java.math.BigDecimal;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class ServicePriceDto {
    private Long id;
    private String serviceName;
    private BigDecimal price;
    private Integer durationTime;

    public ServicePriceDto(Long id,String serviceName, BigDecimal price, Integer durationTime) {
        this.id = id;
        this.serviceName = serviceName;
        this.price = price;
        this.durationTime = durationTime;
    }
}