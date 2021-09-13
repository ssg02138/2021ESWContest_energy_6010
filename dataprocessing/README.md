## python - elasticsearch 
#### elasticsearch의 데이터 경우 csv파일에서 추출하여 사용
#### 데이터 적재: logstash -> elasticsearch

 환경 설정
- elasticsearch 7.3.0 / kibana 7.3.0 / logstash 7.3.0 / python 3.6.9


 - /setting : elasticsearch 및 kibana 환경 설정 파일
 - /python : elasticsearch 기본 사용 (CRUD) 
 - /python/exampleFile.csv : 데이터 샘플
 
#### exampleFile.csv 샘플 데이터 형식
```
{	
	{
		'가맹점식별정보': 'A',	# 가맹점 식별정보, 그냥 A 표시
		'IoT식별정보': '70-85-C2-51-14-C7',	# 라즈베리파이 mac addr
		'IoT타입': 0,	# 0:가스누출감지기, 1:가스계량기
		'데이터발생시간': 2020-06-01 00
		'IoT상태값': 0,	# 0:경보작동 X, 1:경보작동 O 
	},
	{
		'가맹점식별정보': 'A',
		'IoT식별정보': '70-85-C2-51-14-C7',
		'IoT타입': 0,
		'데이터발생시간': 2020-06-01 01
		'IoT상태값': 0,
	}, ...
}
```  

#### Logstash.conf 설정파일 (Elasticsearch 적재)
- logstash.conf 로 샘플데이터를 일자별로 index하여 elasticsearch에 적재(ex: "log-2020-06-01" (00~23시 데이터 존재) )    
 ```
input {
        file{
                path => "/home/sunmi/Downloads/exampleFile.csv"
                start_position => "beginning"
                sincedb_path => "/dev/null"
}
filter{
        csv{
                separator => ","
                columns => ["shopInfo","datetime","IoTInfo","IoTType","IoTStat"]
        }
        
	date{
                match => ["datetime","yyyy-MM-dd HH"]
                timezone => "Asia/Seoul"
		locale => "ko"
                target => "@timestamp"
        }

	mutate{
		convert => {
			"IoTType" => "integer"
			"IoTStat" => "integer"
		}
	}
}
output {
	elasticsearch {
        	hosts => ["localhost:9200"]
                index => "log-%{+YYYY-MM-dd}"
                document_type => "_doc"
	}
  stdout { }
}
 ```  
 --------------
 #### elasticsearch 데이터 확인
 <img width="359" alt="elasticsearchEx" src="https://user-images.githubusercontent.com/42822870/133070523-87f457c4-acc4-4565-a5a1-213f9937d7f2.png">

 
 
 #### kibana 시각화 결과  
 
 <img width="769" alt="kibanaExample" src="https://user-images.githubusercontent.com/42822870/133065449-6c920928-1fea-4089-8dfa-ce8e4e343e6e.png">

 
