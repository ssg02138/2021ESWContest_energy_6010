package gasproject.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElasticSearch {
    private RestHighLevelClient restHighLevelClient;

    private void init() {
        restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                )
        );
    }

    public Object create(String index, String type, String franchiseeID, String IoTID, int IoTType, double date, int IoTState) {

        init();
        //index에 해당 동일한 hash 값이 있는지 확인
//        List<Map<String, Object>> result = searchByHash(index, hash);
//        if(result.size()>0|| result == null){
//            return "already";
//        }

        //문서 색인
        IndexRequest request = new IndexRequest(index, type);

        try {
            request.source(
                    XContentFactory.jsonBuilder()
                            .startObject()
                            .field("franchiseeID", franchiseeID)
                            .field("IoTID", IoTID)
                            .field("IoTType", IoTType)
                            .field("date", date)
                            .field("IoTState", IoTState)

                            .endObject()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        IndexResponse response = null;

        try {
            response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            return response.status();

        } catch (ElasticsearchException e) {
            // TODO: handle exception
            e.printStackTrace();
            if (e.status().equals(RestStatus.CONFLICT)) {
                return "동일한 DOC_ID 문서가 존재합니다.";
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return "문서 생성에 실패하였습니다.";
        }

        return null;
    }

    private List<Map<String, Object>> searchByHash(String index, String hash) {
        //make Result Set
        List<Map<String, Object>> arrList = new ArrayList<>();
        SearchRequest searchRequest = null;
        try {
            //Create Search Request
            searchRequest = new SearchRequest(index);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.termQuery("hash", hash));
            sourceBuilder.from(0);
            sourceBuilder.size(10);
            //Add Builder to Search Request
            searchRequest.source(sourceBuilder);
        } catch (Exception e) {
            return null;
        }
        try {

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            for (SearchHit s : searchResponse.getHits().getHits()) {
                Map<String, Object>
                        sourceMap = s.getSourceAsMap();
                arrList.add(sourceMap);
                System.out.println(s.getId());

            }
            return arrList;

        } catch (IOException e) {
            System.err.println("Elastic search fail");
            return null;
        }
    }

    public String getId(String index,String hash) {
        String id;
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("hash", hash));
        sourceBuilder.from(0);
        sourceBuilder.size(10);
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit searchHit = searchResponse.getHits().getHits()[0];
            id = searchHit.getId();
            return id;
        } catch (IOException e) {
            System.err.println("Elastic search fail");
        }
        return null;
    }

    public void deleteById(String index, String type,String state, String category, String hash) {
        init();
        String id = getId(index,hash);
        DeleteRequest deleteRequest = new DeleteRequest(index, type, id);
        try {
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeClient() {
        if (restHighLevelClient != null) {
            try {
                restHighLevelClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}