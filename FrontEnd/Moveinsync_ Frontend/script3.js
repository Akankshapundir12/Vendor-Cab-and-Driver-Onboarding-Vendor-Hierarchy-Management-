function navigateTo(event, page) {
  event.preventDefault();
  window.location.href = page;
}

let driverData = [];


function fetchAndRenderDrivers() {
  fetch('http://localhost:8080/api/driver/getdrivers')
    .then(res => res.json())
    .then(updatedDrivers => {
      driverData = updatedDrivers;
      renderDrivers(driverData);
    })
    .catch(err => {
      console.error("Failed to load drivers on page load:", err);
      renderDrivers(driverData); // fallback to default data if needed
    });
}

fetchAndRenderDrivers();
function renderDrivers(data) {
  const tbody = document.getElementById("driverTable");
  tbody.innerHTML = "";
  data.forEach(driver => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td><div><strong>${driver.driver}</strong><br><span style="color:gray;font-size:13px">${driver.driverNumber}</span></div></td>
      <td>${driver.license}</td>
      <td>${driver.vehicle}</td>
      <td><span class="doc-icon">📄</span>${driver.documents}</td>
      <td><span class="status-${driver.status}">${driver.status}</span></td>
      <td><span class="onboarding-${driver.onboarding}">${driver.onboarding}</span></td>
      <td class="manage">Manage</td>
    `;
    tbody.appendChild(tr);
  });
}

function filterDrivers() {
  const search = document.getElementById("searchDriver").value.toLowerCase();
  const status = document.getElementById("statusFilter").value.toLowerCase();
  const verify = document.getElementById("verificationFilter").value.toLowerCase();

  const filtered = driverData.filter(d => {
    const matchSearch = d.name.toLowerCase().includes(search) || d.phone.includes(search);
    const matchStatus = status === "all status" || d.status === status;
    const matchVerify = verify === "pending verifications" || d.onboard === verify;
    return matchSearch && matchStatus && matchVerify;
  });

  renderDrivers(filtered);
}
 
document.getElementById("searchDriver").addEventListener("input", filterDrivers);
document.getElementById("statusFilter").addEventListener("change", filterDrivers);
document.getElementById("verificationFilter").addEventListener("change", filterDrivers);

// Initial render
renderDrivers(driverData);

// Add Driver Logic
document.getElementById('submitDriver').addEventListener('click', () => {
  const driver = document.getElementById('driverName').value;
  const license = document.getElementById('licenseNumber').value;
  const vehicle = document.getElementById('vehicleAssigned').value;
  const driverNumber = document.getElementById('phonenumber').value;

  const documents = Array.from(
    document.querySelectorAll('#documentChecklist input[type="checkbox"]:checked')
  ).map(cb => cb.value);

  const newDriver = {
    driver: driver,
    license,
    vehicle: vehicle || "Not assigned",
    driverNumber: driverNumber,
    documents: `${documents.length}/3`,
  };

  console.log("Submitting driver:", newDriver);

  fetch('http://localhost:8080/api/driver/updatedriver', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(newDriver)
  })
  .then(res => res.json())
  .then(data => {
    alert("Driver added successfully!");
    // 🔄 Fetch updated driver data from server
    fetch('http://localhost:8080/api/driver/getdrivers') // Replace with your actual GET endpoint
    .then(res => res.json())
    .then(updatedDrivers => {
      driverData = updatedDrivers; // update global variable
      renderDrivers(driverData);   // refresh table
      document.getElementById('addDriverModal').classList.add('hidden');
    })
    .catch(err => {
      console.error("Failed to fetch updated drivers:", err);
      alert("Driver added, but failed to refresh list");
    });
  });
});

// ✅ Move this outside
function openAddDriverModal() {
  document.getElementById('addDriverModal').classList.remove('hidden');
}

// Optional: Close modal on cancel button click
document.getElementById('closeModal').addEventListener('click', () => {
  document.getElementById('addDriverModal').classList.add('hidden');
});