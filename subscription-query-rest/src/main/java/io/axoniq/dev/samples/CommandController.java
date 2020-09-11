package io.axoniq.dev.samples;

import io.axoniq.dev.samples.api.CreateMyEntityCommand;
import io.axoniq.dev.samples.api.GetMyEntityByIdQuery;
import io.axoniq.dev.samples.query.MyEntity;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

/**
 * @author Sara Pellegrini
 */
@RestController
public class CommandController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public CommandController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    public <U> Mono<U> sendAndReturnUpdate(Object command, SubscriptionQueryResult<?, U> result) {
        // TODO add comments to understand why we need initial result
        return result.initialResult()
                     .then(Mono.fromCompletionStage(commandGateway.send(command)))
                     .flatMapMany(unused -> result.updates())
                     .timeout(Duration.ofSeconds(5))
                     .next()
                     .doFinally(unused -> result.cancel());
    }

    @PostMapping("/entities")
    public Mono<MyEntity> myApi() {
        String entityId = UUID.randomUUID().toString();
        GetMyEntityByIdQuery query = new GetMyEntityByIdQuery(entityId);
        SubscriptionQueryResult<MyEntity, MyEntity> response = queryGateway.subscriptionQuery(query,
                                                                                              MyEntity.class,
                                                                                              MyEntity.class);
        CreateMyEntityCommand command = new CreateMyEntityCommand(entityId);
        return sendAndReturnUpdate(command, response);
    }
}


