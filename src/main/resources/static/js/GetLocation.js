document.addEventListener('DOMContentLoaded', () => {
    /**
     * 좌표 받아와서 주소로 변환
     * 주소 변환 성공 시 : 버튼 비활성화, 버튼 인증상태로 변경
     */
    document.getElementById('getLocation').addEventListener('click', () => {
        const getLocationButton = document.getElementById("getLocation");
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition((position) => {
                const latitude = position.coords.latitude;
                const longitude = position.coords.longitude;

                fetch('/auth/areas', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        latitude: latitude,
                        longitude: longitude
                    })
                })
                    .then(response => {
                        if (response.ok) {
                            getLocationButton.textContent = '인증 완료';
                            getLocationButton.dataset.verified = "true";
                            getLocationButton.disabled = true;
                            return response.json();
                        } else {
                            return response.json().then(errorData => {
                                alert(errorData.message);
                            });
                        }
                    })
                    .then(data => {
                        document.getElementById('address').value = data.data;
                    })
                    .catch(error => console.log(error));
            })
        }
    })
})