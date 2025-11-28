let settings = {};

async function loadVideoSettings() {
    const resp = await fetch("/media/settings");
    settings = await resp.json();

    loadSelectKeys();
    renderVideoList();
}

function loadSelectKeys() {
    const sel = document.getElementById("keySelect");
    sel.innerHTML = "";

    Object.keys(settings).forEach(key => {
        const opt = document.createElement("option");
        opt.value = key;
        opt.textContent = key;
        sel.appendChild(opt);
    });
}

function renderVideoList() {
    const list = document.getElementById("videoList");
    list.innerHTML = "";

    Object.entries(settings).forEach(([key, fileName]) => {
        const fileUrl = `/media/url/${fileName}`;

        const block = document.createElement("div");
        block.className = "text-item";

        block.innerHTML = `
            <div>
                <b>Key:</b> ${key}<br>
                <b>File:</b> ${fileName}<br><br>

                <button onclick="deleteVideo('${fileName}', '${key}')">Delete</button>
            </div>
        `;

        list.appendChild(block);
    });
}

function prepareReplace(key) {
    document.getElementById("keySelect").value = key;
    alert("Выбран ключ: " + key);
}

async function uploadVideo() {
    const key = document.getElementById("keySelect").value;
    const fileInput = document.getElementById("fileInput");

    if (!fileInput.files.length) {
        alert("Выберите файл");
        return;
    }

    const form = new FormData();
    form.append("key", key);
    form.append("file", fileInput.files[0]);

    await fetch("/media/upload", {
        method: "POST",
        body: form
    });

    fileInput.value = "";
    loadVideoSettings();
}

async function deleteVideo(fileName, key) {
    const yes = confirm(`Удалить файл "${fileName}"? Ключ "${key}" останется.`);
    if (!yes) return;

    await fetch(`/media/file/${encodeURIComponent(fileName)}`, {
        method: "DELETE"
    });

    loadVideoSettings();
}

loadVideoSettings();
