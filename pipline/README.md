# Pipline 개발가이드

[![standard-readme compliant](https://img.shields.io/badge/2021%20Contest%20Energy-standard-brightgreen.svg?style=flat-square)](https://github.com/ssg02138/2021ESWContest_energy_6010)

가스에너지 대회 개발 및 적용 가이드

## 개발환경:

- macOS Catalina 10.15.7
- Java 1.8.0_281
- Docker 3.5.1.7
- Redis 6.2.4
- Elasticsearch 7.9.1

## 적용 가이드

- Docker Container 생성

  ### Redis

  - Redis

    ```
    docker run (--rm) --name redis -p 10000:6000 -v /Users/hyunjaewook/Documents/redis/redis.conf:/usr/local/etc/redis/redis.conf -d redis:latest redis-server /usr/local/etc/redis/redis.conf --appendonly yes

  - Redis-cli

    ``` 
    docker run -it --link redis:latest --rm redis redis-cli -h redis -p 6000
    ```

  - auth 설정

    ``` 
    #daemonize no
    # bind 127.0.0.1
    protected-mode no
    
    port 6000 
    
    #logfile "redis.log"
    
    #dir /data 
    
    # SECURITY
    requirepass changeme
    
    # CLIENTS
    maxclients 10000
    
    Requirepass root1
    ```

  - (선택 사항)Redis-cli 확인 시 auth root1으로 인증 후 확인 가능

  

  ### ElasticSearch

  - ElasticSearch
    ```
    docker run -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" --name elasticsearch7 docker.elastic.co/elasticsearch/elasticsearch:7.9.1
    ```

  

## 프로젝트 설정

  - Build.gradle

    본인의 ElasticSearch 버전에 맞는 라이브로리 설치

    ``` xml
    implementation group: 'org.elasticsearch.client',name: 'elasticsearch-rest-high-level-client', version: '7.9.1'



## 개발가이드

 - Redis 설정

   ``` java
   //Redis 환경 설정
           String host = "172.30.1.15";
           int port = 10000;
           int timeout = 10000;
           String password = "root1";



- 더미데이터 생성

  ``` java
  // 가중치 랜덤 값 가져오기
          Map<Integer, Double> w = new HashMap<Integer, Double>();
          w.put(0, 99D);
          w.put(1, 1D);
  
          ArrayList<GasDTO> gasDTOArrayList = new ArrayList<>();
          String[] IoTIDarray = {
                  "70-85-C2-51-14-C0",
                  "70-85-C2-51-14-C1",
                  "70-85-C2-51-14-C2",
                  "70-85-C2-51-14-C3",
                  "70-85-C2-51-14-C4",
          };
          int[] IoTTypeDarray = {0,0,0,0,1};
          for(int i=0;i<5;i++){
              GasDTO _gasDTO = new GasDTO("A", IoTIDarray[i], IoTTypeDarray[i],44378.17459,getWeightedRandom(w,random));
              gasDTOArrayList.add(_gasDTO);
          }

- Redis 적재(파이프라인 방식)

  ``` java
  //다수 데이터 Set
          redis = new Redis(host, port, timeout, password);
          redis.pipelineSet(gasDTOArrayList); //파이프라인 사용하여 적재
          redis.closeRedis();

- Elasticsearch 적재

  ``` java
  for(int i = 0 ;i<gasDTOArrayList.size() ;i++){
              redis = new Redis(host, port, timeout, password);
              elasticSearch = new ElasticSearch();
  
              //String to JSON
              JSONObject jsonObject = new JSONObject(redis.getMessage(Integer.toString(i)));
  
              System.out.println(i+" franchiseeID : "+(String)jsonObject.get("franchiseeID"));
              System.out.println(i+" IoTID : "+(String)jsonObject.get("IoTID"));
              System.out.println(i+" IoTType : "+(int)jsonObject.get("IoTType"));
              System.out.println(i+" date : "+ jsonObject.getBigDecimal("date").doubleValue());
              System.out.println(i+" IoTState : "+(int)jsonObject.get("IoTState"));
  
              try {
                  Thread.sleep(1000);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
  
              //get Date From JSON
              elasticSearch.create("test","info",
                      (String)jsonObject.get("franchiseeID"),
                      (String)jsonObject.get("IoTID"),
                      (int)jsonObject.get("IoTType"),
                      jsonObject.getBigDecimal("date").doubleValue(),
                      (int)jsonObject.get("IoTState"));
  
  
              elasticSearch.closeRest();
              redis.closeRedis();
          }
  ```

  

## 테스트 결과 예제

  - 클라이언트 Log

    ![client](/Users/hyunjaewook/Documents/project/gradleproject/readme res/client.png)

  - Redis

     ![redis](/Users/hyunjaewook/Documents/project/gradleproject/readme res/redis.png)

  - Elasticsearch![elasticsearch](/Users/hyunjaewook/Documents/project/gradleproject/readme res/elasticsearch.png)