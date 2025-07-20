function navigateTo(event,page) {
  event.preventDefault(); // Prevent the default anchor behavior
  window.location.href = page; // Redirect to the desired page
}
function toggleDropdown() {
  const dropdown = document.getElementById("dropdown");
  dropdown.style.display = dropdown.style.display === "block" ? "none" : "block";
}

document.addEventListener("click", function (e) {
  const avatar = document.querySelector(".avatar");
  const dropdown = document.getElementById("dropdown");
  if (!avatar.contains(e.target) && !dropdown.contains(e.target)) {
    dropdown.style.display = "none";
  }
});

// Fetch dashboard stats
async function fetchDashboardData() {
  try {
    const username = sessionStorage.getItem("username");
    const res = await fetch("http://localhost:8080/api/dashboard?email="+username);
    const data = await res.json();
    document.getElementById("total-drivers").textContent = data.totalDrivers;
    document.getElementById("pending-approvals").textContent = data.pendingApprovals;
    document.getElementById("active-vehicles").textContent = data.activeVehicles;
  } catch (err) {
    console.error("Failed to fetch dashboard stats", err);
  }
}

// Load user info from sessionStorage
function fetchUserInfo() {
  const name = sessionStorage.getItem("name");
  const username = sessionStorage.getItem("username");

  if (!name || !username) {
    alert("Session expired. Please log in again.");
    location.href = "auth.html";
    return;
  }

  document.querySelector(".avatar").textContent = name[0].toUpperCase();
  document.querySelector(".welcome-box h2").textContent = `Welcome, ${name}!`;

  const emailLink = document.querySelector(".welcome-box a");
  emailLink.textContent = username;
  emailLink.href = `mailto:${username}`;

  document.querySelector(".dropdown strong").textContent = name;
  document.querySelector(".dropdown span").textContent = username;
}

// Load performance chart from server
async function loadChartData() {
  try {
    const username = sessionStorage.getItem("username");
    const res = await fetch("http://localhost:8080/api/performance?email="+username);
    const result = await res.json();

    new Chart(document.getElementById("performanceChart"), {
      type: "bar",
      data: {
        labels: result.labels,
        datasets: [{
          label: "Performance Index",
          data: result.values,
          backgroundColor: ["#4f46e5", "#6366f1", "#818cf8", "#a5b4fc"],
          borderRadius: 10,
          borderSkipped: false
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: {
            labels: { color: "#374151", font: { weight: "bold" } }
          },
          tooltip: {
            backgroundColor: "#111",
            titleColor: "#fff",
            bodyColor: "#eee",
            borderColor: "#4f46e5",
            borderWidth: 1
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: { color: "#4b5563" },
            grid: { color: "#e5e7eb" }
          },
          x: {
            ticks: { color: "#4b5563" },
            grid: { display: false }
          }
        }
      }
    });
  } catch (err) {
    console.error("Failed to load performance chart", err);
  }
}

// Logout handler
function logout() {
  sessionStorage.clear();
  alert("Logged out successfully.");
  location.href = "auth.html";
}

window.onload = () => {
  fetchDashboardData();
  fetchUserInfo();
  loadChartData();
};


