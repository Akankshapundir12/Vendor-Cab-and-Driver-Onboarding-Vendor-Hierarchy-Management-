// 📊 Chart rendering
function navigateTo(event, page) {
  event.preventDefault();
  window.location.href = page;
}

const ctx = document.getElementById('chart')?.getContext('2d');
if (ctx) {
  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
      datasets: [{
        label: 'Activity',
        data: [10, 20, 15, 25, 24, 30, 28],
        backgroundColor: '#3b82f6',
        borderRadius: 5
      }]
    },
    options: {
      plugins: {
        legend: { display: false }
      },
      scales: {
        y: { beginAtZero: true }
      }
    }
  });
}

// 📥 Login Function
async function login() {
  const email = document.getElementById("email")?.value.trim();
  const password = document.getElementById("password")?.value.trim();

  if (!email || !password) {
    alert("Please fill in both email and password.");
    return;
  }

  try {
    const res = await fetch("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: email, password })
    });

    const result = await res.json();

    if (res.ok) {
      alert("Login successful!");
      location.href = "index4.html";
    } else {
      alert("Login failed: " + (result.message || "Invalid credentials"));
    }
  } catch (err) {
    console.error("❌ Error hitting API:", err);
    alert("Error: " + err.message);
  }
}

// 📈 Fetch Dashboard Stats
async function fetchDashboardStats() {
  try {
    const res = await fetch("http://localhost:8080/gethomepagedata");
    const data = await res.json();

    if (res.ok) {
      document.getElementById("totalVehicle").textContent = data.totalVehicle;
      document.getElementById("activeDrivers").textContent = data.activeDrivers;
      document.getElementById("pendingApproval").textContent = data.pendingApproval;
    } else {
      console.error("Failed to load stats:", data.message || "Unknown error");
    }
  } catch (err) {
    console.error("❌ Error fetching dashboard stats:", err);
  }
}

// ⏳ On page load, fetch dashboard stats
window.addEventListener("DOMContentLoaded", fetchDashboardStats);
