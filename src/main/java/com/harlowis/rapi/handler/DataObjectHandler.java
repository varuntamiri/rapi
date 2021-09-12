package com.harlowis.rapi.handler;

import com.harlowis.data.DataObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.logging.Logger;

@Component
public class DataObjectHandler {

    private final static Logger LOGGER = Logger.getLogger(DataObjectHandler.class.getName());

    public Mono<ServerResponse> getColdefs(ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Flux.fromIterable(getColDefList()), DataObject.ColDef.class);
    }

    private List<DataObject.ColDef> getColDefList() {
        DataObject.ColDef colDef = DataObject.ColDef.newBuilder()
                .setField("Code").setHeadername("Code")
                .setCheckBoxSelection(true).setColmove(true)
                .setResizable(true).setColspan(true)
                .setSortable(true).setFilter(true)
                .setEnableRowGroup(true).setPinned(true)
                .setType(DataObject.Type.Number).build();
        DataObject.ColDef colDef1 = DataObject.ColDef.newBuilder().setTableID(1).setTableName("Country").setField("Name").setHeadername("Name").setType(DataObject.Type.Text).build();
        return Arrays.asList(colDef, colDef1);
    }

    public Mono<ServerResponse> getData(ServerRequest serverRequest) {
        Map<String, Object> objectMap = new HashMap<>(1);
        objectMap.put("Code", "IN");
        objectMap.put("Name", "India");
        Map<String, Object> objectMap1 = new HashMap<>(1);
        objectMap1.put("Code", "US");
        objectMap1.put("Name", "United States");

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Flux.just(objectMap, objectMap1), List.class);
    }

    public Mono<ServerResponse> genColDefs(ServerRequest serverRequest) {
        List<DataObject.ColDef> colDefs = new ArrayList<>();
        return serverRequest.bodyToMono(String.class).flatMap(value -> {
            LOGGER.info("Fetching Url: "+value);
            return WebClient.builder().baseUrl(value)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build().get()
                    .exchange().flatMap(res -> res.bodyToMono(Object[].class));
        }).flatMap(x -> {
            LOGGER.info("Retrieved object first line: " + x);
            Map<String, Object> mapper = (Map<String, Object>) x[0];
            for (String key : mapper.keySet()) {
                DataObject.ColDef colDef = DataObject.ColDef.newBuilder()
                        .setField(key)
                        .setFilter(true)
                        .setColmove(true).setEnablePivot(true)
                        .setEnableRowGroup(true)
                        .build();
                colDefs.add(colDef);
            }
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Flux.fromIterable(colDefs), DataObject.ColDef.class);
        });
    }
}
