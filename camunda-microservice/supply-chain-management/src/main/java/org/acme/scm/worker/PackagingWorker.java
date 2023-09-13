package org.acme.scm.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.quarkiverse.zeebe.JobWorker;
import lombok.extern.slf4j.Slf4j;
import org.acme.scm.constants.StatusType;

import java.util.Map;

import static org.acme.scm.constants.SCMConstants.MANUFACTURING_STATUS;
import static org.acme.scm.constants.SCMConstants.PACKAGING_STATUS;

/**
 * @author irfan.nagoo
 */

@Slf4j
public class PackagingWorker {

    /**
     * This mock worker does the packaging of the manufactured product.
     *
     * @param jobClient - Camunda job client
     * @param job - Current job
     * @return - Map of response
     */
    @JobWorker(type = "package", fetchAllVariables = true)
    public Map<String, Object> executePackagingTask(final JobClient jobClient, final ActivatedJob job) {
        Map<String, Object> variables = job.getVariablesAsMap();
        log.info("Received Packaging request for {} [{}] with quantity [{}] and Due Date[{}]", variables.get("product_type"), variables.get("model"),
                variables.get("batch"), variables.get("due_date"));
        if (variables.get(MANUFACTURING_STATUS).toString().equalsIgnoreCase(StatusType.COMPLETED.toString())) {
            log.info("Mocking products received from Depot");
            log.info("Packaging in PROGRESS");
            log.info("Packaging COMPLETED");
            return Map.of(PACKAGING_STATUS, StatusType.COMPLETED);
        } else {
            log.error("Packaging cannot be performed since previous action did not complete");
            jobClient.newFailCommand(job.getKey())
                    .retries(0)
                    .send();
            return Map.of(PACKAGING_STATUS, StatusType.CANCELLED);
        }
    }

}
