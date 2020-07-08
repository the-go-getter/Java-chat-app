var ws;
var msgGroup = new Set();
var inviteIdSet = new Set();
var msgBoardText = "txt";
const inviteMsgType = "inviteMsg";
const groupMsgType = "groupMsg";
const inviteMsgElePrefix = "invite";


function connect() {
    var username = document.getElementById("username").value;
    var password = document.getElementById("password").value;
    
    var host = document.location.host;
    var pathname = document.location.pathname;
    
    ws = new WebSocket("ws://" +host  + pathname + "chat/" + username + "/" + password);


    ws.onmessage = handleOnMessage;
}

function send() {
    var content = document.getElementById("msg").value;
    var json = JSON.stringify({
        "@type": "msg",
        "content": content
    });
//    console.log("in send", json);

    ws.send(json);
}

function handleOnMessage(event) {
    var log = document.getElementById("log");
    console.log(event.data);
    var message = JSON.parse(event.data);
    if (message["@type"] == groupMsgType) {
        var parse = message.from.split("from Group");
        parse = parse.map(i => i.trim());
        console.log("parse", parse);
        handleGroupMessage(parse[1], parse[0], message.content);
    }
    else if (message.from == "Notification") {
        log.innerHTML += message.from + " : " + message.content + "\n";
    }
    else if (message["@type"] == inviteMsgType) {
        handleInviteMessage(message)
    }
    else {
        appendPersonMessage(message.from, message.content, message.timestamp);
    }
}

function handleInviteMessage(message) {
    newInvite(message);
}

function handleGroupMessage(groupName, sender, message) {
    var msg = sender + " : " + message;
    if (msgGroup.has(groupName)) {
        appendToGroupMessage(groupName, msg)
    } else {
        newGroup(groupName);
        appendToGroupMessage(groupName, msg);
        msgGroup.add(groupName);
    }
}

function appendPersonMessage(sender, message, time) {
    var msg = sender + " : " + message + " [" +new Date(time) + "]";
    $("#personalMsgTxt").append(msg + "\n");
}

function newGroup(groupName) {
    var groupId = groupName;
    var msgBoardId = groupName + "bd";
    var boardText = groupId + msgBoardText;
    console.log("new group", boardText);


    var group = $("<a>", { id: groupId, class: "nav-link", href: "#" + msgBoardId, "data-toggle": "pill", role: "tab", "aria-controls": msgBoardId, "aria-selected": "false" });
    group.html("Group " + groupName);
    $("div#v-pills-tab").append(group);

    var messages = $("<div>", { class: "tab-pane fade", id: msgBoardId, role: "tabpanel", "aria-labelledby": groupId });

    var textArea = $("<textarea>", { readonly: "true", class: "form-control col-12", rows: 10, id: boardText });
    messages.append(textArea);
    $("#v-pills-tabContent").append(messages);
}

function newInvite(message) {
    var inviteId = message.id;
    var invite = $("<p>", { id: inviteMsgElePrefix + inviteId });

    invite.html(message.from + " invite you to Group " + message.groupName);
    var accept = $("<button>", { class: "btn btn-success mx-1", type: "button" });
    accept.html("Accept");
    accept.on("click", () => acknowledgeInvite(message, true));
    var decline = $("<button>", { class: "btn btn-danger", type: "button", })
    decline.html("Decline");
    decline.on("click", () => acknowledgeInvite(message, false));
    invite.append(accept);
    invite.append(decline);

    $("#inviteBoard").append(invite);
}

function acknowledgeInvite(inviteMsg, status) {
    var respond = Object.assign({}, inviteMsg, { accepted: status, acknowledged: true });
    var json = JSON.stringify(respond);
    console.log(json);
    ws.send(json);
    removeInviteElement(inviteMsg.id);
}

function removeInviteElement(inviteId) {
    $("#" + inviteMsgElePrefix + inviteId).remove();
}

function appendToGroupMessage(groupName, message) {
    $("#" + groupName + msgBoardText).append(message + "\n");
}

function showCommands() {
    if (document.getElementById("commandInfo").className === "d-none") {
        document.getElementById("messenger").className = "col-9";
        document.getElementById("commandInfo").className = "col-3"
        document.getElementById("showBtn").className = "d-none"
    } else {
        document.getElementById("messenger").className = "col-12";
        document.getElementById("commandInfo").className = "d-none";
        document.getElementById("showBtn").className = "btn btn-info"
    }

}