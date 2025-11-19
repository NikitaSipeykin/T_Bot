document.getElementById("broadcastForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const text = document.getElementById("text").value.trim();
    if (!text) {
        alert("Введите текст перед отправкой.");
        return;
    }

    const response = await fetch("/send", {
        method: "POST",
        headers: {
            "Content-Type": "application/json; charset=UTF-8"
        },
        body: JSON.stringify({ text })
    });

    if (response.ok) {
        alert("Отправлено!");
        document.getElementById("broadcastForm").reset();
    } else {
        alert("Ошибка при отправке.");
    }
});
