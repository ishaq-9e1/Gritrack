let categories = [];
let grievances = [];

const $ = id => document.getElementById(id);

async function api(url, options = {}) {
    const response = await fetch(url, {
        headers: { "Content-Type": "application/json" },
        ...options
    });
    const data = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(data.message || "Request failed");
    return data;
}

function showToast(message, error = false) {
    const toast = $("toast");
    toast.textContent = message;
    toast.className = `toast show${error ? " error" : ""}`;
    setTimeout(() => toast.className = "toast", 2800);
}

function formatDate(value) {
    if (!value) return "-";
    return new Date(value).toLocaleString();
}

function labelStatus(status) {
    return status.replace("_", " ").toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
}

async function loadCategories() {
    categories = await api("/api/categories");
    $("categoryId").innerHTML = `<option value="">Select category</option>` + categories.map(c =>
        `<option value="${c.id}">${c.name}</option>`
    ).join("");
}

function updateRoutePreview() {
    const selected = categories.find(c => c.id === Number($("categoryId").value));
    $("routePreview").textContent = selected
        ? `Routes to ${selected.department.name} • SLA ${selected.slaHours} hours • Officer ${selected.department.officerName}`
        : "Choose a category to see department and SLA.";
}

async function loadData() {
    const [grievanceData, escalationData] = await Promise.all([
        api("/api/grievances"),
        api("/api/escalations")
    ]);
    grievances = grievanceData;
    renderStats();
    renderGrievances();
    renderEscalations(escalationData);
}

function renderStats() {
    const open = grievances.filter(g => g.status !== "CLOSED").length;
    const inProgress = grievances.filter(g => g.status === "IN_PROGRESS").length;
    const closed = grievances.filter(g => g.status === "CLOSED").length;
    const escalated = grievances.filter(g => g.escalated).length;
    $("stats").innerHTML = [
        [grievances.length, "Total grievances"],
        [open, "Open"],
        [inProgress, "In progress"],
        [closed + " / " + escalated, "Closed / Escalated"]
    ].map(([value, label]) => `<div class="stat"><span>${label}</span><strong>${value}</strong></div>`).join("");
}

function nextAction(g) {
    if (g.status === "SUBMITTED") return `<button onclick="changeStatus(${g.id}, 'IN_PROGRESS')">Start work</button>`;
    if (g.status === "IN_PROGRESS") return `<button onclick="resolveGrievance(${g.id})">Resolve</button>`;
    if (g.status === "RESOLVED") return `<button onclick="changeStatus(${g.id}, 'CLOSED')">Close</button>`;
    if (g.status === "CLOSED" && g.rating == null) return `<button class="rating" onclick="rateGrievance(${g.id})">Rate</button>`;
    if (g.status === "CLOSED") return `<span class="sub">Rated ${g.rating}/5</span>`;
    return "";
}

function renderGrievances() {
    const rows = grievances.map(g => {
        const overdue = new Date(g.slaDeadline) < new Date() && !["RESOLVED", "CLOSED"].includes(g.status);
        return `<tr>
            <td>#${g.id}</td>
            <td class="complaint"><strong>${escapeHtml(g.subject)}</strong><span class="sub">${escapeHtml(g.citizenName)} • ${escapeHtml(g.category.name)}</span></td>
            <td class="route"><strong>${escapeHtml(g.department.name)}</strong><span class="sub">${escapeHtml(g.department.officerName)}</span></td>
            <td><span class="badge ${g.status}">${labelStatus(g.status)}</span>${g.escalated ? `<br><span class="badge overdue">Escalated</span>` : ""}</td>
            <td>${formatDate(g.slaDeadline)}${overdue ? `<div class="sub" style="color:#b42318">Overdue</div>` : ""}</td>
            <td><div class="actions">${nextAction(g)}</div></td>
        </tr>`;
    }).join("");
    $("grievanceRows").innerHTML = rows || `<tr><td colspan="6" class="empty">No grievances submitted yet.</td></tr>`;
}

function renderEscalations(items) {
    $("escalationList").innerHTML = items.length ? items.map(e => `
        <div class="escalation-item">
            <div><strong>#${e.grievance.id} ${escapeHtml(e.grievance.subject)}</strong><p>${escapeHtml(e.reason)} • Assigned to ${escapeHtml(e.officerName)}</p></div>
            <div class="sub">${formatDate(e.escalatedAt)}</div>
        </div>`).join("") : `<div class="empty">No SLA escalations recorded.</div>`;
}

async function changeStatus(id, status, resolutionNote = null) {
    try {
        await api(`/api/grievances/${id}/status`, {
            method: "PUT",
            body: JSON.stringify({ status, resolutionNote })
        });
        showToast(`Grievance moved to ${labelStatus(status)}`);
        await loadData();
    } catch (e) {
        showToast(e.message, true);
    }
}

async function resolveGrievance(id) {
    const note = prompt("Enter resolution note:");
    if (note == null) return;
    await changeStatus(id, "RESOLVED", note);
}

async function rateGrievance(id) {
    const value = prompt("Rate this closed grievance from 1 to 5:");
    if (value == null) return;
    const rating = Number(value);
    if (!Number.isInteger(rating) || rating < 1 || rating > 5) {
        showToast("Rating must be a whole number from 1 to 5", true);
        return;
    }
    try {
        await api(`/api/grievances/${id}/rating`, {
            method: "POST",
            body: JSON.stringify({ rating })
        });
        showToast("Rating saved");
        await loadData();
    } catch (e) {
        showToast(e.message, true);
    }
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

$("categoryId").addEventListener("change", updateRoutePreview);
$("refreshBtn").addEventListener("click", loadData);
$("slaCheckBtn").addEventListener("click", async () => {
    try {
        const result = await api("/api/escalations/check", { method: "POST" });
        showToast(`${result.newEscalations} new escalation(s) recorded`);
        await loadData();
    } catch (e) {
        showToast(e.message, true);
    }
});

$("grievanceForm").addEventListener("submit", async event => {
    event.preventDefault();
    const payload = {
        citizenName: $("citizenName").value,
        citizenEmail: $("citizenEmail").value,
        subject: $("subject").value,
        description: $("description").value,
        categoryId: Number($("categoryId").value)
    };
    try {
        const saved = await api("/api/grievances", { method: "POST", body: JSON.stringify(payload) });
        event.target.reset();
        updateRoutePreview();
        showToast(`Grievance #${saved.id} submitted and routed to ${saved.department.name}`);
        await loadData();
    } catch (e) {
        showToast(e.message, true);
    }
});

(async function start() {
    try {
        await loadCategories();
        await loadData();
    } catch (e) {
        showToast("Backend/database connection is not ready: " + e.message, true);
    }
})();
