package src.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class TareaProgramadaService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;
    private boolean isRunning = false;
    private LocalDateTime startTime;
    private LocalDateTime simulatedStartTime = LocalDateTime.of(2024, Month.JULY, 22, 5, 45);

    public void iniciarTareaProgramada(Runnable tarea) {
        if (isRunning) {
            return;
        }

        long initialDelay = calcularDelayInicial(simulatedStartTime);
        scheduledFuture = scheduler.scheduleAtFixedRate(tarea, initialDelay, 600, TimeUnit.SECONDS); // 600 segundos = 10 minutos
        isRunning = true;
        startTime = LocalDateTime.now();
    }

    public void detenerTareaProgramada() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            isRunning = false;
            startTime = null;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public LocalDateTime getHoraSimulada() {
        if (startTime == null) {
            return simulatedStartTime;
        }
        Duration elapsedRealTime = Duration.between(startTime, LocalDateTime.now());
        return simulatedStartTime.plus(elapsedRealTime);
    }

    private long calcularDelayInicial(LocalDateTime horaInicio) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextExecutionTime = horaInicio.truncatedTo(ChronoUnit.HOURS)
            .plusMinutes(((horaInicio.getMinute() / 10) + 1) * 10);
        
        if (nextExecutionTime.isBefore(now)) {
            nextExecutionTime = nextExecutionTime.plusHours(1);
        }
        
        return ChronoUnit.SECONDS.between(now, nextExecutionTime);
    }
}