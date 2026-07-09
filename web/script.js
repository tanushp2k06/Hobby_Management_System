document.getElementById("todayDate").innerText = new Date().toLocaleDateString("en-IN", {
    weekday: "long",
    day: "numeric",
    month: "short",
    year: "numeric"
});

function safeValue(id) {
    return encodeURIComponent(document.getElementById(id).value.trim());
}

function addHobby() {
    let title = safeValue("title");
    let category = safeValue("category");
    let time = safeValue("time");

    if (!title || !category || !time) {
        alert("Please fill all fields.");
        return;
    }

    fetch(`/add?title=${title}&category=${category}&time=${time}`)
        .then(response => response.text())
        .then(data => {
            alert(data);
            document.getElementById("title").value = "";
            document.getElementById("category").value = "";
            document.getElementById("time").value = "";
            loadHobbies();
        });
}

function loadHobbies() {
    fetch("/view")
        .then(response => response.json())
        .then(data => {
            let hobbyList = document.getElementById("hobbyList");
            hobbyList.innerHTML = "";

            document.getElementById("totalCount").innerText = data.length;

            let done = data.filter(h => h.practiced).length;
            let pending = data.length - done;
            let best = data.length === 0 ? 0 : Math.max(...data.map(h => h.streak));

            document.getElementById("doneCount").innerText = done;
            document.getElementById("pendingCount").innerText = pending;
            document.getElementById("bestStreak").innerText = best;

            if (data.length === 0) {
                hobbyList.innerHTML = `<div class="empty">No hobbies added yet.</div>`;
                return;
            }

            data.forEach(hobby => {
                let status = hobby.practiced ? "Practiced" : "Not Practiced";
                let statusClass = hobby.practiced ? "done" : "pending";

                hobbyList.innerHTML += `
                    <div class="hobby-card">
                        <h3>${hobby.title}</h3>

                        <div class="meta">
                            <span>Category: ${hobby.category}</span>
                            <span>Reminder: ${hobby.reminderTime}</span>
                            <span>Streak: ${hobby.streak} days</span>
                        </div>

                        <span class="badge ${statusClass}">${status}</span>

                        <div class="actions">
                            <button class="practice" onclick="practiceHobby(${hobby.id})">Mark Practiced</button>
                            <button class="delete" onclick="deleteHobby(${hobby.id})">Delete</button>
                        </div>
                    </div>
                `;
            });
        });
}

function practiceHobby(id) {
    fetch(`/practice?id=${id}`)
        .then(response => response.text())
        .then(data => {
            alert(data);
            loadHobbies();
        });
}

function deleteHobby(id) {
    fetch(`/delete?id=${id}`)
        .then(response => response.text())
        .then(data => {
            alert(data);
            loadHobbies();
        });
}

function loadWeekly() {
    fetch("/weekly")
        .then(response => response.json())
        .then(data => {
            let weeklyList = document.getElementById("weeklyList");
            weeklyList.innerHTML = "";

            if (data.length === 0) {
                weeklyList.innerHTML = `<div class="empty">No weekly progress yet. It will appear after daily reset.</div>`;
                return;
            }

            data.forEach(item => {
                weeklyList.innerHTML += `<div class="week-item">${item}</div>`;
            });
        });
}

function scrollToSection(id) {
    document.getElementById(id).scrollIntoView({
        behavior: "smooth",
        block: "start"
    });
}

loadHobbies();