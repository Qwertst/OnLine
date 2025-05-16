let stompClient = null;
let currentUser = null;

function connect() {
    const username = document.getElementById('username').value.trim();

    if (!username) {
        alert('Please enter your username first!');
        return;
    }

    const socket = new WebSocket('ws://localhost:8080/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        document.getElementById('disconnectBtn').disabled = false;
        document.getElementById('connectBtn').disabled = true;


        stompClient.subscribe('/user/queue/startWalk', (message) => {
            logMessage(`Walk ID assigned: ${JSON.parse(message.body)}`);
        });

        stompClient.subscribe('/user/queue/endWalk', (message) => {
            logMessage(`Walk ID assigned: ${JSON.parse(message.body)}`);
        });

        stompClient.subscribe(`/user/${username}/msg`, (message) => {
            const content = JSON.parse(message.body);
            if (content.lat) {
                logMessage(`Location update: ${content.lat}, ${content.lng}`);
            } else if (content.from) {
                logMessage(`Invite from: ${content.from}`);
            }
        });

        registerUser();
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    document.getElementById('disconnectBtn').disabled = true;
    document.getElementById('connectBtn').disabled = false;
    logMessage("Disconnected");
}

function registerUser() {
    currentUser = document.getElementById('username').value;
    stompClient.send('/app/start', {}, JSON.stringify(currentUser));
    logMessage(`Registration sent for: ${currentUser}`);
}

function sendInvite() {
    const friend = document.getElementById('friendName').value;
    stompClient.send('/app/invite', {}, JSON.stringify({
        fromWho: currentUser,
        toWho: friend
    }));
    logMessage(`Invite sent to: ${friend}`);
}

function updateLocation() {
    const lat = parseFloat(document.getElementById('lat').value);
    const lng = parseFloat(document.getElementById('lng').value);
    stompClient.send('/app/updateLocation', {}, JSON.stringify({
        from: currentUser,
        location: { lat, lng }
    }));
    logMessage(`Location updated: ${lat}, ${lng}`);
}

function joinGroup() {
    const friend = document.getElementById('friendName').value;
    stompClient.send('/app/joinGroup', {}, JSON.stringify({
        fromWho: currentUser,
        toWho: friend
    }));
    logMessage(`Joining group of: ${friend}`);
}

function quitGroup() {
    stompClient.send('/app/quitGroup', {}, JSON.stringify(currentUser));
    logMessage(`Left current group`);
}

function logMessage(message) {
    const messagesDiv = document.getElementById('messages');
    messagesDiv.innerHTML += `<div>${new Date().toLocaleTimeString()}: ${message}</div>`;
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

