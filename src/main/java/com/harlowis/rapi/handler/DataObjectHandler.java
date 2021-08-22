package com.harlowis.rapi.handler;

import com.harlowis.data.DataObject;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

@Component
public class DataObjectHandler {

    public Mono<ServerResponse> getColdefs(ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Flux.fromIterable(getColDefList()), DataObject.ColDef.class);
    }

    private List<DataObject.ColDef> getColDefList() {
        DataObject.ColDef colDef = DataObject.ColDef.newBuilder()
                .setTableID(1).setTableName("Country")
                .setField("Code").setHeadername("Code")
                .setCheckBoxSelection(true).setColmove(true)
                .setResizable(true).setColspan(true)
                .setSortable(true).setFilter(true)
                .setRowGroup(true).setPinned(true)
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
        Optional<String> uri = serverRequest.queryParam("uri");
        List<DataObject.ColDef> colDefs = new ArrayList<>();
        if (uri.isPresent()) {
            HttpClient httpClient = HttpClient.create()
                    .tcpConfiguration(client ->
                            client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                                    .doOnConnected(conn -> conn
                                            .addHandlerLast(new ReadTimeoutHandler(10))
                                            .addHandlerLast(new WriteTimeoutHandler(10))));

            ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
            Mono<Object[]> response = WebClient.builder().baseUrl(uri.get())
                    .clientConnector(connector)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build().get()
                    .accept( MediaType.APPLICATION_JSON )
                    .retrieve()
                    .bodyToMono(Object[].class);
            Object[] responseBody = response.share().block();
            if (responseBody.length > 0) {
                Map<String, Object> mapper = (Map<String, Object>) responseBody[0];
                for (String key : mapper.keySet()) {
                    DataObject.ColDef colDef = DataObject.ColDef.newBuilder()
                            .setTableID(1).setTableName("Table")
                            .setField(key).setHeadername(key)
                            .setCheckBoxSelection(true).setColmove(true)
                            .setResizable(true).setColspan(true)
                            .setSortable(true).setFilter(true)
                            .setRowGroup(true).setPinned(true)
                            .setType(DataObject.Type.Number).build();
                    colDefs.add(colDef);
                }
            }
        }
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Flux.fromIterable(colDefs), DataObject.ColDef.class);
    }
}
