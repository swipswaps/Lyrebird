package moe.lyrebird.model.interrupts;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class CleanupService {

    private static final Logger LOG = LoggerFactory.getLogger(CleanupService.class);

    private final Queue<CleanupOperation> onShutdownHooks;

    public CleanupService() {
        onShutdownHooks = new LinkedList<>();
    }

    public void registerCleanupOperation(final CleanupOperation cleanupOperation) {
        LOG.debug("Registering cleanup operation : {}", cleanupOperation.getName());
        onShutdownHooks.add(cleanupOperation);
    }

    public void executeCleanupOperations() {
        LOG.debug("Executing cleanup hooks !");
        onShutdownHooks.forEach(this::executeCleanupOperationWithTimeout);
        LOG.debug("All cleanup hooks have been executed!");
    }

    private void executeCleanupOperationWithTimeout(final CleanupOperation cleanupOperation) {
        LOG.debug("\t-> {}", cleanupOperation.getName());
        try {
            CompletableFuture.runAsync(cleanupOperation.getOperation()).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error("Could not actually call the following hook [{}] !", cleanupOperation.getName(), e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            LOG.error("The hook [{}] encountered an exception while executing!", cleanupOperation.getName(), e);
        } catch (TimeoutException e) {
            LOG.error("The hook [{}] could not finish in the given time!", cleanupOperation.getName(), e);
        }
    }

}