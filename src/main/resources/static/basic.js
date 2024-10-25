document.addEventListener('DOMContentLoaded', (event) => {
    const socket = new SockJS('/ws');  // WebSocket 서버 엔드포인트 설정
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        // WebSocket 연결이 성공한 후, 사용자 개인 큐에 대한 구독 설정
        stompClient.subscribe('/user/queue/notifications', function (message) {
            showNotification(JSON.parse(message.body));
        });
    });

    function showNotification(message) {
        // 알림을 화면에 표시하는 로직
        const notificationDiv = document.getElementById('notification');
        notificationDiv.innerHTML = `<p>New Notification: ${message}</p>`;
        alert("New Notification: " + message);
    }
});
