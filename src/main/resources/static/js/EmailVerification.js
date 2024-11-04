document.addEventListener("DOMContentLoaded", function () {
    const sendCodeButton = document.getElementById("sendEmail");
    const verifyCodeButton = document.getElementById("verifyCode");

    /**
     * 이메일 인증 번호 발송
     * 발송 버튼을 누르면 -> 버튼 비활성화 -> 이메일 검증, 코드 발송 -> 3분 카운트다운 시작,
     * 이메일 필드 비활성화, 코드 입력 필드 활성화
     */
    sendCodeButton.addEventListener('click', function () {
        const emailInput = document.getElementById("email");
        const email = emailInput.value;

        const codeInput = document.getElementById("authCode");

        //버튼 비활성화
        sendCodeButton.disabled = true;
        sendCodeButton.textContent = "잠시만 기다려주세요...";

        //이메일 필드가 채워졌는지, 이메일 형식인지, 이미 등록된 유저인지 검증, 이메일 발송
        if (email) {
            fetch('/auth/email', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    email: email
                })
            })
                .then(response => {
                    if (response.ok) {
                        emailInput.readOnly = true;
                        codeInput.readOnly = false;
                        startCountdown(sendCodeButton);
                    } else {
                        return response.json().then(errorData => {
                            sendCodeButton.disabled = false;
                            sendCodeButton.textContent = "인증 번호 발송";
                            alert(errorData.message);
                        });
                    }
                })
                .catch(error => console.log(error));
        } else {
            sendCodeButton.disabled = false;
            sendCodeButton.textContent = "인증 번호 발송";
            alert('이메일을 입력해주세요.');
        }

    });

    //타이머 재발송까지 쿨타임 3분
    function startCountdown(button) {
        let timeLeft = 180;

        const timer = setInterval(function () {
            const minutes = Math.floor(timeLeft / 60);
            const seconds = timeLeft % 60;
            button.textContent = `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;

            timeLeft -= 1;

            if (timeLeft < 0) {
                clearInterval(timer);
                button.disabled = false;
                button.textContent = '인증 번호 발송';
            }
        }, 1000);
    }

    /**
     * 코드 인증
     * 코드 맞으면 코드 입력 필드 비활성화, 버튼 인증 완료 상태로 변경, -> 코드 입력 필드, 이메일 발송 버튼 숨기기
     */
    verifyCodeButton.addEventListener('click', function () {
        const emailInput = document.getElementById("email");
        const email = emailInput.value;

        const codeInput = document.getElementById("authCode");
        const code = codeInput.value;

        if (email && code) {
            fetch('/auth/email', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    email: email,
                    authNumber: code
                })
            })
                .then(response => {
                    if (response.ok) {
                        //버튼 인증 상태로 변경, 버튼 비활성화 , 코드 입력 필드 비활성화
                        verifyCodeButton.textContent = '인증 완료';
                        codeInput.readOnly = true;
                        verifyCodeButton.disabled = true;
                        sendCodeButton.disabled = true;
                        verifyCodeButton.dataset.verified = "true";

                        //코드 입력 필드, 이메일 발송 버튼 숨기기
                        codeInput.style.display = 'none';
                        sendCodeButton.style.display = 'none';
                    } else {
                        return response.json().then(errorData => {
                            alert(errorData.message);
                        });
                    }
                })
                .catch(error => console.log(error)
                );
        } else {
            alert('코드를 입력해주세요.');
        }
    })
});