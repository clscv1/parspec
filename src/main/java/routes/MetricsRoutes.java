package routes;

import com.google.gson.Gson;
import services.MetricService;

import static spark.Spark.get;

public class MetricsRoutes {
    private final Gson gson;
    private final MetricService metricService;

    public MetricsRoutes(Gson gson, MetricService metricService) {
        this.gson = gson;
        this.metricService = metricService;

    }

    public void establishRoutes() {
        get("/metrics",(req, res)-> gson.toJson(metricService.getMetrics()));

    }
}
