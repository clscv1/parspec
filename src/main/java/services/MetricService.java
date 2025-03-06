package services;

import pojos.Metric;
import pojos.OrderStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class MetricService {
    private static volatile MetricService instance;

    private final AtomicLong totalOrdersProcessed = new AtomicLong(0);
    private final AtomicLong totalProcessingTime = new AtomicLong(0);
    private final ConcurrentHashMap<OrderStatus, AtomicLong> orderStatusCounts = new ConcurrentHashMap<>();

    private MetricService() {
        for (OrderStatus status : OrderStatus.values()) {
            orderStatusCounts.put(status, new AtomicLong(0));
        }
    }

    public static MetricService getInstance() {
        if (instance == null) {
            synchronized (MetricService.class) {
                if (instance == null) {
                    instance = new MetricService();
                }
            }
        }
        return instance;
    }

    public Long getTotalProcessingTime() {
        return totalProcessingTime.get();

    }

    public void recordTime(long start, long end) {
        long duration = end - start;
        System.out.println("Duration: "+ duration);
        totalProcessingTime.addAndGet(duration);
    }

    public void updateStatusCount(OrderStatus oldStatus, OrderStatus newStatus) {
        if (oldStatus != null) {
            orderStatusCounts.get(oldStatus).decrementAndGet();
        }
        orderStatusCounts.get(newStatus).incrementAndGet();
    }

    public void incrementTotalOrdersProcessed() {
        totalOrdersProcessed.incrementAndGet();
    }

    public Metric getMetrics() {
        Map<OrderStatus, Long> countByStatus = orderStatusCounts.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().get()

        ));
        return new Metric(
                totalOrdersProcessed.get(),
                totalOrdersProcessed.get() > 0 ? (double) (totalProcessingTime.get() / totalOrdersProcessed.get()) : 0,
                countByStatus
        );

    }

}
