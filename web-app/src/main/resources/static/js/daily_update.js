async function triggerDailyUpdate() {
    try {
      const resp = await fetch("http://localhost:8080/internal/daily_update", { method: "GET" });
      if (resp.ok) {
        alert("Daily update запущен!");
      } else {
        const text = await resp.text();
        alert("Ошибка: " + resp.status + " — " + text);
      }
    } catch (e) {
      alert("Ошибка подключения к серверу: " + e.message);
      console.error(e);
    }
  }

  // подгружаем header и вешаем слушатель
  fetch("/common/header.html")
    .then(res => res.text())
    .then(html => {
      document.getElementById("header").innerHTML = html;
      const btn = document.getElementById("dailyBtn");
      if (btn) {
        btn.addEventListener("click", function (e) {
          e.preventDefault();
          triggerDailyUpdate();
        });
      } else {
        console.warn("dailyBtn не найден в header");
      }
    });