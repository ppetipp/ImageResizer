package hu.ponte.image_resizer.dto.incoming;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResizeRequest {
    @Min(1)
    @Max(10000)
    private Integer width;

    @Min(1)
    @Max(10000)
    private Integer height;
}
