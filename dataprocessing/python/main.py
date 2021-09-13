import json
from elasticsearch import Elasticsearch

class MyElastic:

    #connection Elasticsearch
    def __init__(self):
        self.es_client = Elasticsearch(hosts="localhost:9200")

    #create Index 
    def createIndex(self, Index) -> None:
        if not self.es_client.indices.exists(index=Index):
            return self.es_client.indices.create(index=Index)
    
    #insert Index+data
    def insert(data):
        return es.index(index=index, doc_type=doc_type, body=data)

    '''
        data = {
            'shopInfo' : 'A',
            'datetime' : '2020-06-01 00', ...
        }

    '''

    #search index return (json)
    def search(self,Index,data=None):
        if data is None:
            data = {"match_all":{}}
        else:
            data = {"match": data}
        body = {"track_total_hits": True,
                "query":
                    data}
        res = self.es_client.search(index=Index,body=body)
        json_res = json.dumps(res, indent=4)
        
        print("GOT %d Hits" % res['hits']['total']['value'])
        for hit in res['hits']['hits']:
            print("%(message)s:" % hit["_source"])

        return json_res

    #index List return (List)
    def searchIndexList(self):
        res = self.es_client.indices.get_alias('*')
        sort_res = sorted(res.keys())
        print("index Count: ",len(sort_res))
        for k in sort_res:
            print(k)
        return sort_res
    
    #delete index
    def deleteIndex(self, Index):
        if not self.es_client.indices.exists(index=Index):
            print("No Input")
            return
        self.es_client.indices.delete(index=Index)

    #index content delete
    def deleteQuery(self, Index, data=None):
        if data is None:
            self.es_client.delete(Index)
            return
        else:
            body = {
                    "query" : {
                        "match" : data 
                        }
                    }
            return self.es_client.delete_by_query(Index, body=body)

   #
   def update(self,Id, data):
       body = {
               'doc' : data}
       res = self.es_client.update(index=Index, id=Id, body=body, doc_type=doc_type)
       return res


if __name__ == "__main__":
    client = MyElastic()
    
    #client.createIndex("log-2020-09-12")
    #client.search("log-2020-09-12")
    #client.searchIndexList()
    #client.deleteQuery("log-2021-09-13",{"IoTType":"0"})
    #client.deleteIndex("log-2021-09-13")
    #client.update("id","index",{'var':'update_var'})
