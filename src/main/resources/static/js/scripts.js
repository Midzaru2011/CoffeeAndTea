console.log("scripts.js загружен!");
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
    const coffeeInput = document.getElementById("coffee_name");
    const infoButton = document.getElementById("get-info-btn");
    const coffeeDetails = document.getElementById("coffee-details");
    console.log("Coffee Input:", coffeeInput);
    console.log("Info Button:", infoButton);
    console.log("Coffee Details:", coffeeDetails);
    if (infoButton && coffeeInput && coffeeDetails) {
        infoButton.addEventListener("click", function () {
            console.log("Кнопка 'Узнать подробности' нажата");
            const name = coffeeInput.value.trim();
            if (!name) {
                coffeeDetails.textContent = "Пожалуйста, введите название кофе.";
                coffeeDetails.style.backgroundColor = "#fff3cd";
                coffeeDetails.style.borderLeftColor = "#ffc107";
                return;
            }

            coffeeDetails.textContent = "Загрузка информации...";
            coffeeDetails.style.backgroundColor = "#e3f2fd";
            coffeeDetails.style.borderLeftColor = "#2196f3";

            fetch(`/api/coffees/info/${encodeURIComponent(name)}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Ошибка сети или неизвестный кофе");
                    }
                    return response.text();
                })
                .then(data => {
                    coffeeDetails.textContent = data;
                    coffeeDetails.style.backgroundColor = "#e8f5e9";
                    coffeeDetails.style.borderLeftColor = "#4CAF50";
                })
                .catch(err => {
                    coffeeDetails.textContent = "Не удалось получить информацию. Попробуйте позже.";
                    coffeeDetails.style.backgroundColor = "#ffebee";
                    coffeeDetails.style.borderLeftColor = "#f44336";
                    console.error("Ошибка:", err);
                });
        });
    }
});