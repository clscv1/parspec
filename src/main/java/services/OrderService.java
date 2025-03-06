package services;

import de.huxhorn.sulky.ulid.ULID;
import pojos.Order;
import pojos.OrderStatus;
import repository.OrderRepository;

import java.util.concurrent.BlockingQueue;

public class OrderService {

    private final OrderRepository orderRepository;
    private final ULID ulid;
    private final MetricService metricService;
    private final BlockingQueue<Order> orderQueue;

    public OrderService(OrderRepository orderRepository, ULID ulid, MetricService metricService, BlockingQueue<Order> orderQueue) {
        this.orderRepository = orderRepository;
        this.ulid = ulid;
        this.metricService = metricService;
        this.orderQueue = orderQueue;

    }


    public Order acceptOrder(Order order) {
        // save the pending order in db;
        order.setOrderId(ulid.nextULID());
        order.setStatus(OrderStatus.PENDING);
        orderRepository.create(order);
        orderQueue.add(order);
        metricService.updateStatusCount(null, OrderStatus.PENDING);
        return order;
    }

    public Order getOrderById(String orderId) {
        return orderRepository.getById(orderId);
    }

    public void updateOrder(Order order) {
        orderRepository.update(order);
    }

}
