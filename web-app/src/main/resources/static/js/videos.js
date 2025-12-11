let settings = {};
let videoKeys = {};
let docKeys = {};

async function loadVideoSettings() {
    const resp = await fetch("/media/settings");
    settings = await resp.json();

    splitKeysByType();

    loadSelectVideoKeys();
    loadSelectDocKeys();
    loadSelectAudioKeys();

    renderVideoList();
    renderDocList();
    renderAudioList();
}

function splitKeysByType() {
    videoKeys = {};
    docKeys = {};
    audioKeys = {};

    Object.entries(settings).forEach(([key, file]) => {
        if (key.startsWith("VIDEO_")) {
            videoKeys[key] = file;
        } else if (key.startsWith("DOC_")) {
            docKeys[key] = file;
        } else if (key.startsWith("PROGRAM_")) {   // ← НОВОЕ
            audioKeys[key] = file;
        }
    });
}


/* ------------------------ VIDEO ------------------------ */

function loadSelectVideoKeys() {
    const sel = document.getElementById("keySelect");
    sel.innerHTML = "";

    Object.keys(videoKeys).forEach(key => {
        const opt = document.createElement("option");
        opt.value = key;
        opt.textContent = key;
        sel.appendChild(opt);
    });
}

function renderVideoList() {
    const list = document.getElementById("videoList");
    list.innerHTML = "";

    Object.entries(videoKeys).forEach(([key, fileName]) => {
        const block = document.createElement("div");
        block.className = "text-item";

        block.innerHTML = `
            <div>
                <b>Key:</b> ${key}<br>
                <b>File:</b> ${fileName}<br><br>

                <button onclick="deleteFile('${fileName}', '${key}')">Delete</button>
            </div>
        `;

        list.appendChild(block);
    });
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

/* ------------------------ DOCUMENTS ------------------------ */

function loadSelectDocKeys() {
    const sel = document.getElementById("docKeySelect");
    sel.innerHTML = "";

    Object.keys(docKeys).forEach(key => {
        const opt = document.createElement("option");
        opt.value = key;
        opt.textContent = key;
        sel.appendChild(opt);
    });
}

function renderDocList() {
    const list = document.getElementById("docList");
    list.innerHTML = "";

    Object.entries(docKeys).forEach(([key, fileName]) => {
        const block = document.createElement("div");
        block.className = "text-item";

        block.innerHTML = `
            <div>
                <b>Key:</b> ${key}<br>
                <b>File:</b> ${fileName}<br><br>

                <button onclick="deleteFile('${fileName}', '${key}')">Delete</button>
            </div>
        `;

        list.appendChild(block);
    });
}

async function uploadDocument() {
    const key = document.getElementById("docKeySelect").value;
    const fileInput = document.getElementById("docFileInput");

    if (!fileInput.files.length) {
        alert("Выберите документ");
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

/* ------------------------ AUDIO ------------------------ */

function loadSelectAudioKeys() {
    const sel = document.getElementById("audioKeySelect");
    sel.innerHTML = "";

    Object.keys(audioKeys).forEach(key => {
        const opt = document.createElement("option");
        opt.value = key;
        opt.textContent = key;
        sel.appendChild(opt);
    });
}

function renderAudioList() {
    const list = document.getElementById("audioList");
    list.innerHTML = "";

    Object.entries(audioKeys).forEach(([key, fileName]) => {
        const block = document.createElement("div");
        block.className = "text-item";

        block.innerHTML = `
            <div>
                <b>Key:</b> ${key}<br>
                <b>File:</b> ${fileName}<br><br>
                <audio controls src="/media/${fileName}" style="width:200px"></audio><br><br>
                <button onclick="deleteFile('${fileName}', '${key}')">Delete</button>
            </div>
        `;

        list.appendChild(block);
    });
}

async function uploadAudio() {
    const key = document.getElementById("audioKeySelect").value;
    const fileInput = document.getElementById("audioFileInput");

    if (!fileInput.files.length) {
        alert("Выберите аудиофайл");
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


/* ------------------------ COMMON (DELETE) ------------------------ */

async function deleteFile(fileName, key) {
    const yes = confirm(`Удалить файл "${fileName}"? Ключ "${key}" останется.`);
    if (!yes) return;

    await fetch(`/media/file/${encodeURIComponent(fileName)}`, {
        method: "DELETE"
    });

    loadVideoSettings();
}

loadVideoSettings();
