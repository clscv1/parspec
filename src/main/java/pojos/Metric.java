package pojos;

import java.util.Map;

public record Metric(
        Long totalOrdersProcessed,
        Double averageOrderProcessingTime,
        Map<OrderStatus, Long> orderStatusCount

) {
}
