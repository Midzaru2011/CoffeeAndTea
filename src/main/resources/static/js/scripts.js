document.addEventListener("DOMContentLoaded", function () {
    // Добавляем класс "clicked" для кнопок "Добавить"
    const addButtons = document.querySelectorAll(".add-button");
    addButtons.forEach(button => {
        button.addEventListener("click", function () {
            this.classList.add("clicked"); // Добавляем класс "clicked"
        });
    });

    // Обработка клика на кнопку "Выйти"
    const logoutButton = document.getElementById("logout-button");
    if (logoutButton) {
        logoutButton.addEventListener("click", function (event) {
            event.preventDefault(); // Предотвращаем стандартное поведение
            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
//
//            let csrfToken = null;
//            let csrfHeader = null;
//            if (csrfMetaToken && csrfMetaToken.content) {
//                csrfToken = csrfMetaToken.content;
//            }
//
//            if (csrfMetaHeader && csrfMetaHeader.content) {
//                csrfHeader = csrfMetaHeader.content;
//            }
//
//            // Если CSRF-токен не найден, показываем сообщение об ошибке
//            if (!csrfToken || !csrfHeader) {
//                alert("CSRF-токен не найден. Пожалуйста, перезагрузите страницу.");
//                return;
//            }
            // Показываем подтверждение перед выходом
            if (confirm("Вы уверены, что хотите выйти?")) {
                // Отправляем POST-запрос на /logout
                fetch("/logout", {
                    method: "POST",
                    headers: {
                        [csrfHeader]: csrfToken, // Добавляем CSRF-токен в заголовки
                    },
                    credentials: "include", // Включаем куки для аутентификации
                })
                .then(response => {
                    if (response.ok) {
                        // После успешного выхода перенаправляем на страницу входа
                        window.location.href = "/login?logout";
                    } else {
                        alert("Ошибка при выходе из системы.");
                    }
                })
                .catch(error => {
                    console.error("Ошибка:", error);
                    alert("Произошла ошибка. Пожалуйста, попробуйте позже.");
                });
            }
        });
    }
});