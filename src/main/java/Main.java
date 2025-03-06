import com.google.gson.Gson;
import commons.ApiResponse;
import db.DbConfig;
import de.huxhorn.sulky.ulid.ULID;
import pojos.Order;
import repository.OrderRepository;
import routes.MetricsRoutes;
import routes.OrderRoutes;
import services.MetricService;
import services.WorkerService;
import services.OrderService;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static spark.Spark.*;

public class Main {
    public static void main(String args[]) {
        port(4567);
        threadPool(200, 10, 30000);

        DbConfig dbConfig = new DbConfig();
        OrderRepository orderRepository = new OrderRepository(dbConfig);
        MetricService metricService = MetricService.getInstance();
        ULID ulid = new ULID();
        Gson gson = new Gson();
        BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>();
        OrderService orderService = new OrderService(orderRepository, ulid, metricService, orderQueue);

        new OrderRoutes(orderService, gson).establishRoutes();
        new MetricsRoutes(gson, metricService).establishRoutes();

        exception(RuntimeException.class, (ex, req, res) -> {
            res.status(400);
            res.body(gson.toJson(new ApiResponse(false, Map.of("error", ex.getMessage()))));
        });

        int threads = Integer.parseInt(System.getenv("THREAD_POOL_SIZE"));
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        WorkerService workerService = new WorkerService(orderService, metricService, orderQueue, executorService);
        workerService.start(threads);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook triggered.");
            workerService.shutdown();
        }));

        Signal.handle(new Signal("TERM"), sig -> {
            System.out.println("Received SIGTERM, shutting down.");
            workerService.shutdown();
        });


    }
}
