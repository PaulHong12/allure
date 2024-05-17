$(function () {
    var stompClient = null;
    connect();

    function connect() {
        stompClient = new StompJs.Client({
            brokerURL: 'ws://localhost:8080/ws-stomp'
        });

        stompClient.onConnect = function (frame) {
            setConnected(true);
            console.log('Connected: ' + frame);

            const roomUrl = '/sub/chat/room/' + $("#roomId").val();
            stompClient.subscribe(roomUrl, function (chatDto) {
                showMessage(JSON.parse(chatDto.body).message);
            });
        };

        stompClient.onWebSocketError = function (error) {
            console.error('Error with websocket', error);
        };

        stompClient.onStompError = function (frame) {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
        };

        stompClient.activate();
    }

    function disconnect() {
        if (stompClient) {
            stompClient.deactivate();
        }
        setConnected(false);
        console.log("Disconnected");
    }

    function setConnected(connected) {
        $("#connect").prop("disabled", connected);
        $("#disconnect").prop("disabled", !connected);
        $("#conversation").toggle(connected);
        $("#greetings").html("");
    }

    function sendMsg() {
        var message = $("#message").val();
        var roomId = $("#roomId").val();
        var username = $("#name").val();

        if (stompClient && roomId && username && message) {
            stompClient.publish({
                destination: "/pub/chat/sendMessage",
                body: JSON.stringify({'roomId': roomId, 'sender': username, 'message': message})
            });
            $("#message").val("");
        }
    }

    function showMessage(message) {
        $("#greetings").append("<tr><td>" + message + "</td></tr>");
    }

    $("form").on('submit', function (e) {
        e.preventDefault();
        sendMsg();
    });

    $("#connect").click(connect);
    $("#disconnect").click(disconnect);
    $("#send").click(function(e) {
        e.preventDefault();
        sendMsg();
    });
});
