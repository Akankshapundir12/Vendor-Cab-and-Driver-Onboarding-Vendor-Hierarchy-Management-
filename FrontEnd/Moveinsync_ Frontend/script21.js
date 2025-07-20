function navigateTo(event, page) {
  event.preventDefault();
  window.location.href = page;
}

let vehicles = [];
let currentEditIndex = null;

// Load from server on page load
window.onload = async function () {
  try {
    const res = await fetch("http://localhost:8080/api/vehicle/all");
    if (!res.ok) throw new Error("Failed to fetch vehicles from server.");
    vehicles = await res.json();
  } catch (err) {
    console.error(err);
    alert("❌ Error fetching vehicle data from server.");
  }
  renderTable();
};

// Save to localStorage (optional – still keeping in case needed locally)
function saveToStorage() {
  localStorage.setItem("vehicles", JSON.stringify(vehicles));
}

// Render vehicle table
function renderTable() {
  const tbody = document.getElementById("vehicleTable");
  tbody.innerHTML = "";

  vehicles.forEach((v, i) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${v.registration}</td>
      <td>${v.model}</td>
      <td>${v.type}</td>
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

// Add Vehicle
document.getElementById("vehicleForm").onsubmit = async function (e) {
  e.preventDefault();

  const newVehicle = {
    registration: document.getElementById("reg").value.trim(),
    model: document.getElementById("model").value,
    type: document.getElementById("type").value,
    capacity: document.getElementById("capacity").value,
    fuel: document.getElementById("fuel").value,
    compliance: document.getElementById("compliance").value,
    status: document.getElementById("status").value
  };

  // Duplicate check (frontend only)
  const isDuplicate = vehicles.some(v => v.registration.toLowerCase() === newVehicle.registration.toLowerCase());
  if (isDuplicate) {
    alert("❌ A vehicle with this registration already exists.");
    return;
  }

  try {
    const res = await fetch("http://localhost:8080/api/vehicle/add", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(newVehicle)
    });

    if (!res.ok) throw new Error("Failed to add vehicle to server.");

    const refreshed = await fetch("http://localhost:8080/api/vehicle/all");
    vehicles = await refreshed.json();


    vehicles.push(refreshed); // Use the response if backend returns full object
    renderTable();
    saveToStorage();

    document.getElementById("vehicleForm").reset();
    document.getElementById("vehicleModal").style.display = "none";
  } catch (err) {
    console.error(err);
    alert("❌ Error adding vehicle to server.");
  }
};

// Manage/Edit and Delete logic (same as before – still local only)
// You may also update these to sync with server if needed
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

// Save changes in Manage modal
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

// Modal controls
document.getElementById("addVehicleBtn").onclick = () => {
  document.getElementById("vehicleModal").style.display = "block";
};
document.getElementById("closeModal").onclick = () => {
  document.getElementById("vehicleModal").style.display = "none";
};
document.getElementById("closeManageModal").onclick = () => {
  document.getElementById("manageModal").style.display = "none";
};
