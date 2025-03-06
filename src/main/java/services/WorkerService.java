package services;

import pojos.Order;
import pojos.OrderStatus;
import repository.OrderRepository;

import java.util.concurrent.*;

public class WorkerService {
    private final BlockingQueue<Order> q;
    private final OrderService orderService;
    private final MetricService metricService;
    private final ExecutorService executorService;

    public WorkerService(OrderService orderService, MetricService metricService, BlockingQueue<Order> q, ExecutorService executorService) {
        this.orderService = orderService;
        this.metricService = metricService;
        this.q = q;
        this.executorService = executorService;

    }

    public void start(int threads) {
        for (int i =0;i < threads;i++) {
            executorService.execute(()-> {
                while (true) {
                    try {
                        Order order = q.take();
                        long startTime = System.currentTimeMillis();
                        System.out.println("Received Order: "+order.getOrderId() + " on thread: "+ Thread.currentThread().getName());
                        order.setStatus(OrderStatus.PROCESSING);
                        orderService.updateOrder(order);
                        System.out.println("Order marked as processing: "+ order.getOrderId());
                        metricService.updateStatusCount(OrderStatus.PENDING, OrderStatus.PROCESSING);
                        processOrder(order);
                        long endTime = System.currentTimeMillis();
                        metricService.recordTime(startTime,endTime);
                        metricService.updateStatusCount(OrderStatus.PROCESSING, OrderStatus.COMPLETED);
                        metricService.incrementTotalOrdersProcessed();
                        System.out.println("Total time to process Orders: "+ metricService.getTotalProcessingTime());

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    private void processOrder(Order order) {
        order.setStatus(OrderStatus.COMPLETED);
        orderService.updateOrder(order);
        System.out.println("Order marked as completed: "+ order.getOrderId());
    }

    public void shutdown() {
        System.out.println("Shutting down executor...");
        executorService.shutdown();
    }
}
