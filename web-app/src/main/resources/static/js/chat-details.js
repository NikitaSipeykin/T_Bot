const messagesDiv = document.getElementById('messages');

const conversationId = location.pathname.split('/').pop();

fetch(`/api/admin/chats/${conversationId}/messages`)
  .then(r => r.json())
  .then(renderMessages);

function renderMessages(messages) {
  messagesDiv.innerHTML = '';

  messages.forEach(m => {
    const msg = document.createElement('div');
    msg.className = `message ${m.senderType.toLowerCase()}`;

    msg.innerHTML = `
      <div class="bubble">
        ${m.text || ''}
      </div>
      <div class="time">
        ${new Date(m.createdAt).toLocaleTimeString()}
      </div>
    `;

    messagesDiv.appendChild(msg);

    if (m.buttons && m.buttons.length) {
      const buttons = document.createElement('div');
      buttons.className = 'buttons';

      m.buttons.forEach(b => {
        const btn = document.createElement('button');
        btn.textContent = b.text;
        btn.disabled = true;
        buttons.appendChild(btn);
      });

      messagesDiv.appendChild(buttons);
    }
  });

  messagesDiv.scrollTop = messagesDiv.scrollHeight;
}
