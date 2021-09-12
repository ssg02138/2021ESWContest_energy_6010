package gasproject.JSON;

//as-ix
//[
//  {
//      '가맹점식별정보': 'A',	# 가맹점 식별정보, 그냥 A 표시
//      'IoT식별정보': '70-85-C2-51-14-C7',	# 라즈베리파이 mac addr
//      'IoT타입': 0,	# 0:가스누출감지기, 1:가스계량기
//      '데이터발생시간': 44378.17459	# 2021-07-01  4:11:25 AM
//      'IoT상태값': 0,	# 0:경보작동 X, 1:경보작동 O
//  },
//  {
//      '가맹점식별정보': 'A',
//      'IoT식별정보': '70-85-C2-51-14-C7',
//      'IoT타입': 0,
//      '데이터발생시간': 44378.17529	# 2021-07-01  4:12:25 AM
//      'IoT상태값': 0,
//   }, ...
//]

//to-be
//[
//  {
//      'franchiseeID': 'A',	# 가맹점 식별정보, 그냥 A 표시
//      'IoTID': '70-85-C2-51-14-C7',	# 라즈베리파이 mac addr
//      'IoTType': 0,	# 0:가스누출감지기, 1:가스계량기
//      'date': 44378.17459	# 2021-07-01  4:11:25 AM
//      'IoTState': 0,	# 0:경보작동 X, 1:경보작동 O
//  },
//  {
//      'franchiseeID': 'A',
//      'IoTID': '70-85-C2-51-14-C7',
//      'IoTType': 0,
//      'date': 44378.17529	# 2021-07-01  4:12:25 AM
//      'IoTState': 0,
//   }, ...
//]
public class GasDTO {
    String franchiseeID;
    String IoTID;
    int    IoTType;
    double date;
    int    IoTState;

    public GasDTO(){

    }
    public GasDTO( String franchiseeID, String IoTID, int IoTType, double date, int IoTState){
        this.franchiseeID=franchiseeID;
        this.IoTID=IoTID;
        this.IoTType=IoTType;
        this.date=date;
        this.IoTState=IoTState;
    }

    public String getFranchiseeID() {
        return franchiseeID;
    }

    public void setFranchiseeID(String franchiseeID) {
        this.franchiseeID = franchiseeID;
    }

    public String getIoTID() {
        return IoTID;
    }

    public void setIoTID(String ioTID) {
        IoTID = ioTID;
    }

    public int getIoTType() {
        return IoTType;
    }

    public void setIoTType(int ioTType) {
        IoTType = ioTType;
    }

    public double getDate() {
        return date;
    }

    public void setDate(double date) {
        this.date = date;
    }

    public int getIoTState() {
        return IoTState;
    }

    public void setIoTState(int ioTState) {
        IoTState = ioTState;
    }

    public String toString(){
        StringBuilder result = new StringBuilder();
        try {
            result.append("{\"franchiseeID\":\""+franchiseeID+"\",");
            result.append("\"IoTID\":\""+IoTID+"\",");
            result.append("\"IoTType\":"+IoTType+",");
            result.append("\"date\":"+date+",");
            result.append("\"IoTState\":"+IoTState);
            result.append("}");

//            result.append("{\"id\":\""+id+"\",");
//            result.append("{\"list\":\" [");
//            for (int i =0 ;i<list.size();i++){
//                TestDTO testDTO=(TestDTO) list.get(i);
//                result.append("{\"name\":\""+testDTO.getName()+"\",");
//                if(i==list.size()-1){
//                    result.append("\"id\":"+testDTO.getNum()+"}");
//                }else{
//                    result.append("\"id\":"+testDTO.getNum()+"},");
//                }
//            }
//            result.append("]}");


        }catch (Exception e ){
            e.printStackTrace();
            return null;
        }
        return result.toString();
    }


}

