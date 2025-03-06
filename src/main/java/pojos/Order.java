package pojos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Order {
    private String userId;
    private String orderId;
    private List<String> itemIds;
    private Double totalAmount;
    private OrderStatus status=OrderStatus.PENDING;

    public Order(String  userId, String orderId, List<String> itemIds, double totalAmount, OrderStatus orderStatus) {
        this.userId = userId;
        this.orderId = orderId;
        this.itemIds = itemIds;
        this.totalAmount = totalAmount;
        this.status = orderStatus;
    }

    public boolean isValid() {
        return userId != null && totalAmount != null && (itemIds==null || !itemIds.isEmpty());

    }



}
