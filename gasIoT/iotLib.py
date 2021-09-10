import re, datetime, time, json
from uuid import getnode
import RPi.GPIO as GPIO
import socket

sbId = 'A'
macAddr = re.findall('..', '%012x' % getnode())

# JSON 데이터 샘플
# [
# 	{
# 		'가맹점식별정보': 'A',	# 가맹점 식별정보, 그냥 A 표시
# 		'IoT식별정보': '70-85-C2-51-14-C7',	# 라즈베리파이 mac addr
# 		'IoT타입': 0,	# 0:가스누출감지기, 1:가스계량기
# 		'데이터발생시간': 44378.17459	# 2021-07-01  4:11:25 AM
# 		'IoT상태값': 0,	# 0:경보작동 X, 1:경보작동 O 
# 	},
# 	{
# 		'가맹점식별정보': 'A',
# 		'IoT식별정보': '70-85-C2-51-14-C7',
# 		'IoT타입': 0,
# 		'데이터발생시간': 44378.17529	# 2021-07-01  4:12:25 AM
# 		'IoT상태값': 0,
# 	}, ...
# ]
def gasDetection():
	iotType = 0
	GPIO.setmode(GPIO.BOARD)
	GPIO.setup(6, GPIO.IN)	# Ground Pin
	GPIO.setup(8, GPIO.OUT)

	try:
		while 1:
			time.sleep(1)

			clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			clientSocket.connect(("IP Addr", 10000))
			response = {}

			if GPIO.input(6):	# Not Input
				now = datetime.datetime.now()
				inputData = 0
				
				response['가맹점식별정보'] = sbId
				response['IoT식별정보'] = macAddr
				response['IoT타입'] = iotType
				response['데이터발생시간'] = now
				response['IoT상태값'] = inputData

			else:	# Input
				now = datetime.datetime.now()
				inputData = 1
				
				response['가맹점식별정보'] = sbId
				response['IoT식별정보'] = macAddr
				response['IoT타입'] = iotType
				response['데이터발생시간'] = now
				response['IoT상태값'] = inputData

			resData = json.dumps(response)

			try:
				clientSocket.send(resData)

			finally:
				clientSocket.close()

	finally:
		GPIO.cleanup()

def gasMeter():
	iotType = 1
	gasStatus = 0
	GPIO.setmode(GPIO.BOARD)
	GPIO.setup(14, GPIO.IN)
	GPIO.setup(16, GPIO.OUT)

	try:
		while 1:
			time.sleep(0)

			clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			clientSocket.connect(("IP Addr", 10000))
			response = {}

			if GPIO.input(14):	# Not Input
				continue

			else:	# Input
				now = datetime.datetime.now()
				gasStatus += 1
				
				response['가맹점식별정보'] = sbId
				response['IoT식별정보'] = macAddr
				response['IoT타입'] = iotType
				response['데이터발생시간'] = now
				response['IoT상태값'] = gasStatus

			resData = json.dumps(response)

			try:
				clientSocket.send(resData)

			finally:
				clientSocket.close()

	finally:
		GPIO.cleanup()

if __name__ == '__main__':
	gasDetection()
	gasMeter()