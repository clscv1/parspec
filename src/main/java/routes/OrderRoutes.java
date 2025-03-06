package routes;

import com.google.gson.Gson;
import commons.ApiResponse;
import pojos.Order;
import services.OrderService;

import java.util.Map;

import static spark.Spark.*;



public class OrderRoutes {
    private final OrderService orderService;
    private final Gson gson;

    public OrderRoutes(OrderService orderService, Gson gson) {
        this.orderService = orderService;
        this.gson = gson;

    }

    public void establishRoutes() {
        post("/order/accept",(req, res) -> {
            Order order = gson.fromJson(req.body(), Order.class);
            if (!order.isValid()) throw new RuntimeException("Order has missing properties");
            order = orderService.acceptOrder(order);
            res.status(201);
            return gson.toJson(new ApiResponse(true, Map.of("id", order.getOrderId())));

        });

        get("/order/status/:orderId",(req, res) -> {
            String orderId = req.params("orderId");
            if (orderId == null) throw new RuntimeException("orderId is required");
            Order order = orderService.getOrderById(orderId);
            if (order == null) throw new RuntimeException("Order not found");
            res.status(200);
            return gson.toJson(new ApiResponse(true, Map.of("status", order.getStatus())));

        });

    }

}
