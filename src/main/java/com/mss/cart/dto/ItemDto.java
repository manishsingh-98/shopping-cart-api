package com.mss.cart.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {
    @NotBlank(message = "name can not be blank")
    private String name;

    @NotNull(message = "please provide a valid price")
    private Double price;

    @NotNull(message = "please provide with a valid quantity")
    @Min(1)
    private Long quantity;
}
