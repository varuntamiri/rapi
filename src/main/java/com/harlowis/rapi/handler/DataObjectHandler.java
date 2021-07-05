package com.harlowis.rapi.handler;

import com.harlowis.data.DataObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
}
