package com.harlowis.rapi;

import com.harlowis.rapi.handler.DataObjectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ReactRouter {

    @Bean
    public RouterFunction<ServerResponse> productsRoute(DataObjectHandler dataObjectHandler) {
        return RouterFunctions.route(GET("/GetColumnAttributes").and(accept(MediaType.APPLICATION_JSON)), dataObjectHandler::getColdefs)
                .andRoute(POST("/GenColDefs").and(accept(MediaType.TEXT_PLAIN)), dataObjectHandler::genColDefs)
                .andRoute(GET("/GetData").and(accept(MediaType.APPLICATION_JSON)), dataObjectHandler::getData);
    }

}
