function navigateTo(event, page) {
  event.preventDefault();
  window.location.href = page;
}

let vehicles = [];
let currentEditIndex = null;
let lastSortKey = null;
let lastSortAsc = true;

// Load from localStorage on page load
window.onload = function () {
  const data = localStorage.getItem("vehicles");
  if (data) {
    vehicles = JSON.parse(data);
  }
  renderTable();

  // Sorting headers
  const keys = ["registration", "model", "type", "capacity", "fuel", "compliance", "status"];
  document.querySelectorAll("th").forEach((th, i) => {
    const sortKey = keys[i];
    if (!sortKey) return;
    th.style.cursor = "pointer";
    th.addEventListener("click", () => {
      if (lastSortKey === sortKey) lastSortAsc = !lastSortAsc;
      else {
        lastSortKey = sortKey;
        lastSortAsc = true;
      }
      renderTable(lastSortKey, lastSortAsc);
    });
  });
};

// Save vehicles to localStorage
function saveToStorage() {
  localStorage.setItem("vehicles", JSON.stringify(vehicles));
}

// Render vehicle table with filtering, sorting, and highlighting
function renderTable(sortKey = null, sortAsc = true) {
  const tbody = document.getElementById("vehicleTable");
  tbody.innerHTML = "";

  const searchQuery = document.getElementById("searchInput").value.toLowerCase();
  const statusFilter = document.getElementById("statusFilter").value;

  let filtered = vehicles.filter(v => {
    const combined = `${v.registration} ${v.model} ${v.type}`.toLowerCase();
    const matchesSearch = combined.includes(searchQuery);
    const matchesStatus = statusFilter === "All Status" || v.status.toLowerCase() === statusFilter.toLowerCase();
    return matchesSearch && matchesStatus;
  });

  if (sortKey) {
    filtered.sort((a, b) => {
      if (a[sortKey] < b[sortKey]) return sortAsc ? -1 : 1;
      if (a[sortKey] > b[sortKey]) return sortAsc ? 1 : -1;
      return 0;
    });
  }

  filtered.forEach((v, i) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${highlight(v.registration, searchQuery)}</td>
      <td>${highlight(v.model, searchQuery)}</td>
      <td>${highlight(v.type, searchQuery)}</td>
      <td>${v.capacity}</td>
      <td>${v.fuel}</td>
      <td>${v.compliance}</td>
      <td>${v.status}</td>
      <td>
        <a href="#" class="manage-btn" data-index="${i}">Manage</a> |
        <a href="#" class="remove-btn" data-index="${i}" style="color:red;">Remove</a>
      </td>
    `;
    tbody.appendChild(row);
  });
}

// Highlight search match
function highlight(text, query) {
  if (!query) return text;
  const re = new RegExp(`(${query})`, "gi");
  return text.replace(re, `<mark>$1</mark>`);
}

// Add new vehicle
document.getElementById("vehicleForm").onsubmit = function (e) {
  e.preventDefault();

  const newReg = document.getElementById("reg").value.trim();

  const isDuplicate = vehicles.some(v => v.registration.toLowerCase() === newReg.toLowerCase());
  if (isDuplicate) {
    alert("❌ A vehicle with this registration already exists.");
    return;
  }

  const newVehicle = {
    registration: newReg,
    model: document.getElementById("model").value,
    type: document.getElementById("type").value,
    capacity: document.getElementById("capacity").value,
    fuel: document.getElementById("fuel").value,
    compliance: document.getElementById("compliance").value,
    status: document.getElementById("status").value
  };

  vehicles.push(newVehicle);
  saveToStorage();
  renderTable();

  document.getElementById("vehicleForm").reset();
  document.getElementById("vehicleModal").style.display = "none";
};

// Manage/Edit vehicle
document.getElementById("vehicleTable").addEventListener("click", function (e) {
  if (e.target.classList.contains("manage-btn")) {
    e.preventDefault();
    currentEditIndex = parseInt(e.target.dataset.index);
    const v = vehicles[currentEditIndex];

    document.getElementById("mReg").value = v.registration;
    document.getElementById("mModel").value = v.model;
    document.getElementById("mType").value = v.type;
    document.getElementById("mCapacity").value = v.capacity;
    document.getElementById("mFuel").value = v.fuel;
    document.getElementById("mCompliance").value = v.compliance;
    document.getElementById("mStatus").value = v.status;

    document.getElementById("manageModal").style.display = "block";
  }

  // Remove vehicle
  if (e.target.classList.contains("remove-btn")) {
    e.preventDefault();
    const index = parseInt(e.target.dataset.index);
    const confirmDelete = confirm("Are you sure you want to delete this vehicle?");
    if (confirmDelete) {
      vehicles.splice(index, 1);
      saveToStorage();
      renderTable();
    }
  }
});

// Save changes from manage form
document.getElementById("manageForm").onsubmit = function (e) {
  e.preventDefault();
  if (currentEditIndex === null) return;

  const updatedReg = document.getElementById("mReg").value.trim();

  const isDuplicate = vehicles.some((v, i) =>
    i !== currentEditIndex && v.registration.toLowerCase() === updatedReg.toLowerCase()
  );
  if (isDuplicate) {
    alert("❌ Another vehicle with this registration already exists.");
    return;
  }

  vehicles[currentEditIndex] = {
    registration: updatedReg,
    model: document.getElementById("mModel").value,
    type: document.getElementById("mType").value,
    capacity: document.getElementById("mCapacity").value,
    fuel: document.getElementById("mFuel").value,
    compliance: document.getElementById("mCompliance").value,
    status: document.getElementById("mStatus").value
  };

  saveToStorage();
  renderTable();
  document.getElementById("manageModal").style.display = "none";
};

// Modal open/close controls
document.getElementById("addVehicleBtn").onclick = () => {
  document.getElementById("vehicleModal").style.display = "block";
};
document.getElementById("closeModal").onclick = () => {
  document.getElementById("vehicleModal").style.display = "none";
};
document.getElementById("closeManageModal").onclick = () => {
  document.getElementById("manageModal").style.display = "none";
};

// Search and status filter
document.getElementById("searchInput").addEventListener("input", () => renderTable(lastSortKey, lastSortAsc));
document.getElementById("statusFilter").addEventListener("change", () => renderTable(lastSortKey, lastSortAsc));