
1. 배치 테스트 - 결과 문서화
2. 데이터 삭제 구현 
3. 프론트 화면 재구성
4. 배포 - 간단하게 

----------------------------------------------------------

SessionCreationPolicy.IF_REQUIRED 로 설정해야 뷰 화면에 로그인 정상 반영됨

---------------------------------------------------------

업비트 wss url로 시도했을 때 아래의 오류 발생
main.js:74 Uncaught SyntaxError: The URL's scheme must be either 'http:' or 'https:'. 'wss:' is not allowed

아래의 글을 참고하여 브라우저에서 SockJS로 받지 않고 WebSocket으로 받음
https://github.com/sockjs/sockjs-client/issues/452

해결 방법
:  let socket = new WebSocket('wss://api.upbit.com/websocket/v1');

--------------------------------------------------------------------------------

**"Unexpected token 'o', '[object ArrayBuffer]' is not valid JSON"** 오류 발생

WebSocket으로부터 수신한 데이터가 JSON 형식이 아니라 ArrayBuffer 형식이라는 의미

[ 문제의 코드 ]
// 서버에서 메시지를 받았을 때
socket.onmessage = function(event) {
    var data = event.data;
    var jsonData = JSON.parse(data);

    // 받은 메시지 데이터 중 현재가 정보만 표시
    if (jsonData.type === 'ticker') {
        var price = jsonData.trade_price;
        document.getElementById("price").innerText = "Current BTC Price: " + price;
    }

    console.log("Received message:", jsonData);
};

[ 문제 해결 ]
socket.onmessage = function(event) {
    // 서버에서 수신한 데이터가 ArrayBuffer로 올 수 있으므로 처리 필요
    if (event.data instanceof ArrayBuffer) {
        // ArrayBuffer를 텍스트로 변환
        var text = new TextDecoder().decode(event.data);

        // 텍스트를 JSON으로 파싱
        var jsonData = JSON.parse(text);

        // 받은 메시지를 처리 (현재가 정보 표시)
        if (jsonData.type === 'ticker') {
            var price = jsonData.trade_price;
            document.getElementById("price").innerText = "Current BTC Price: " + price;
        }

        console.log("Received message:", jsonData);
    } else {
        console.log("Received unknown data type");
    }
};

1. ArrayBuffer 처리:
event.data가 ArrayBuffer 형식으로 전달되는지 확인하기 위해 **instanceof ArrayBuffer**를 사용합니다.
만약 ArrayBuffer 데이터가 들어오면, **TextDecoder**를 사용해 바이너리 데이터를 텍스트로 변환합니다.

2. 텍스트를 JSON으로 변환:
변환된 텍스트를 **JSON.parse()**를 사용해 JSON 객체로 파싱합니다.

3. JSON 데이터 처리:
업비트 API에서 ticker 메시지 타입의 데이터가 수신되었을 때 BTC 가격 정보를 가져와서 화면에 표시합니다.

4. TextDecoder의 역할:
**TextDecoder**는 **ArrayBuffer**와 같은 바이너리 데이터를 텍스트로 변환하는 데 사용됩니다.
WebSocket에서 바이너리 데이터를 수신했을 때, 이를 UTF-8 등의 인코딩 방식으로 텍스트로 변환해 줌

결론:
업비트 WebSocket API에서 수신한 데이터가 바이너리(ArrayBuffer) 형식일 수 있으므로,
이를 먼저 텍스트로 변환한 후 JSON으로 파싱해야 합니다.
이 방식으로 데이터를 처리하면 WebSocket에서 수신한 바이너리 데이터를 정상적으로 JSON으로 변환하여 사용할 수 있습니다.
