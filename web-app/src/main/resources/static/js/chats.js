const chatList = document.getElementById('chat-list');
const searchInput = document.getElementById('search');

function loadChats() {
  fetch('/api/admin/chats')
    .then(r => r.json())
    .then(renderChats);
}

function renderChats(chats) {
  const query = searchInput.value.trim();

  chatList.innerHTML = '';

  chats
    .filter(c => !query || String(c.chatId).includes(query))
    .forEach(c => {
      const div = document.createElement('div');
      div.className = 'chat-row';
      div.onclick = () => location.href = `/chats/${c.conversationId}`;

      div.innerHTML = `
        <div class="chat-title">Chat ID: ${c.chatId}</div>
        <div class="chat-preview">${c.lastMessageText || ''}</div>
        <div class="chat-time">
          ${c.lastMessageAt ? new Date(c.lastMessageAt).toLocaleString() : ''}
        </div>
      `;

      chatList.appendChild(div);
    });
}

searchInput.addEventListener('input', loadChats);

loadChats();
