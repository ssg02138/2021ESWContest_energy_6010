package gasproject.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
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
    RestHighLevelClient restHighLevelClient;
    String index = "test1";
    String type = "info";

    public void init() {
        restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                )
        );
    }

    public boolean putData(String index, String type) {
        boolean acknowledged = false;

        init();
        if (restHighLevelClient == null) return false;

        try {
            XContentBuilder indexBuilder = XContentFactory.jsonBuilder()
                    .startObject()
                    .startObject(type)
                    .startObject("properties")
                    .startObject("question")
                    .field("type", "text")
                    .endObject()
                    .startObject("answer")
                    .field("type", "keyword")
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject();

            //인덱스생성 요청 객체
            CreateIndexRequest request = new CreateIndexRequest(index);
            //매핑 정보
            request.mapping(type, indexBuilder);

            //별칭설정
            String aliasName = "chatbotInstance";
            request.alias(new Alias(aliasName));

            //인덱스생성
            CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);

            acknowledged = response.isAcknowledged();
            ActionListener<CreateIndexResponse> listener = new ActionListener<CreateIndexResponse>() {
                @Override
                public void onResponse(CreateIndexResponse createIndexResponse) {
                }

                @Override
                public void onFailure(Exception e) {
                    return;
                }
            };

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


        return acknowledged;
    }

    public Object create(String index, String type, String franchiseeID ,String IoTID, int IoTType, double date,int IoTState) {

        init();
        //index에 해당 동일한 hash 값이 있는지 확인
//        List<Map<String, Object>> result = search_basic(index, hash);
//        if(result.size()>0|| result == null){
//            return "already";
//        }
        //문서 ID(대게 RDB에 문서 키값정도는 가져와서 저장한다. 만약 자동생성을 하게 된다면 엘라스틱서치는 UUID로 ID를 생성)
        String docId = "DOC_1";

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

    private List<Map<String, Object>> search_basic(String index,String hash)
    {
        //make Result Set
        List <Map<String, Object>> arrList = new ArrayList<>();
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
        }catch (Exception e){
            return null;
        }
        //Execution(Sync)
        try {

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            for(SearchHit s:searchResponse.getHits().getHits())
            {
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

    public String getid(String hash){
        String id;
        SearchRequest searchRequest = new SearchRequest("test1");
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

    public void delete(String state ,String category,String hash){
        init();
        String id = getid(hash);
        DeleteRequest deleteRequest = new DeleteRequest("test1","info",id);
        try {
            restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void closeRest() {
        if (restHighLevelClient != null) {
            try {
                restHighLevelClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}




// backup
//XContentBuilder settingsBuilder = XContentFactory.jsonBuilder()
//        .startObject()
//        .field("number_of_shards", 5)
//        .field("number_of_replicas", 1)
//
//        .startObject("analysis")
//        .startObject("tokenizer")
//        .startObject("sample-nori-tokenizer")
//        .field("type", "nori_tokenizer")
//        .field("decompound_mode", "mixed")
//        .field("user_dictionary", "user_dictionary.txt")
//        .endObject()
//        .endObject()
//
//        .startObject("analyzer")
//        .startObject("sample-nori-analyzer")
//        .field("type", "custom")
//        .field("tokenizer", "sample-nori-tokenizer")
//        .array("filter", new String[]{
//                        "sample-nori-posfilter",
//                        "nori_readingform",
//                        "sample-synonym-filter",
//                        "sample-stop-filter"
//                }
//        )
//        .endObject()
//        .endObject()
//
//        .startObject("filter")
//        .startObject("sample-nori-posfilter")
//        .field("type", "nori_part_of_speech")
//        .array("stoptaags", new String[]{
//                        "E", "IC", "J", "MAG", "MM", "NA", "NR", "SC",
//                        "SE", "SF", "SH", "SL", "SN", "SP", "SSC", "SSO",
//                        "SY", "UNA", "UNKNOWN", "VA", "VCN", "VCP", "VSV",
//                        "VV", "VX", "XPN", "XR", "XSA", "XSN", "XSV"
//                }
//        )
//        .endObject()
//
//        .startObject("sample-synonym-filter")
//        .field("type", "synonym")
//        .field("synonyms_path", "synonymsFilter.txt")
//        .endObject()
//
//        .startObject("sample-stop-filter")
//        .field("type", "stop")
//        .field("stopwords_path", "stopFilter.txt")
//        .endObject()
//        .endObject()
//        .endObject()
//        .endObject();
//
//    XContentBuilder indexBuilder = XContentFactory.jsonBuilder()
//            .startObject()
//            .startObject(type)
//            .startObject("properties")
//            .startObject("question")
//            .field("type", "text")
//            .field("analyzer", "sample-nori-analyzer")
//            .endObject()
//            .startObject("answer")
//            .field("type", "keyword")
//            .endObject()
//            .endObject()
//            .endObject()
//            .endObject();