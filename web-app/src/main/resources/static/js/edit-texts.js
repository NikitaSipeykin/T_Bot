async function loadTexts() {
    const resp = await fetch("/api/texts");
    const data = await resp.json();

    const list = document.getElementById("list");
    list.innerHTML = "";

    data.forEach(item => {
        const block = document.createElement("div");
        block.className = "text-item";

        block.innerHTML = `
            <h3>${item.id}</h3>
            <textarea id="txt_${item.id}">${item.value}</textarea>
            <button onclick="saveText('${item.id}')">Save</button>
        `;

        list.appendChild(block);
    });
}

async function saveText(id) {
    const value = document.getElementById("txt_" + id).value;

    await fetch(`/api/texts/${id}`, {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({ id: id, value: value })
    });

    alert("Saved!");
}

loadTexts();
