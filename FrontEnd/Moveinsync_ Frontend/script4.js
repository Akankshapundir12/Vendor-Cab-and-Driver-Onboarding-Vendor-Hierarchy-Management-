function navigateTo(event, page) {
  event.preventDefault();
  window.location.href = page;
}

// Sample vendor hierarchy data
const vendorData = [
  {
    name: "Super Vendor Corp",
    level: "super",
    email: "super@vendor.com",
    phone: "9999999999",
    location: "HQ",
    status: "active",
    permissions: ["manage_drivers", "view_reports"],
    stats: { drivers: 0, vehicles: 0, activeVehicles: 0, pendingApprovals: 0 },
    children: [
      {
        name: "regional vendor South",
        level: "regional",
        email: "south@vendor.com",
        phone: "8888888888",
        location: "South Region",
        status: "active",
        permissions: ["manage_drivers"],
        stats: { drivers: 1, vehicles: 2, activeVehicles: 1, pendingApprovals: 1 },
        children: [
          {
            name: "vellore city vendor 1",
            level: "city",
            email: "cityvendor@gmail.com",
            phone: "1234567890",
            location: "vellore city",
            status: "active",
            permissions: ["manage_drivers", "view_reports"],
            stats: { drivers: 2, vehicles: 3, activeVehicles: 2, pendingApprovals: 1 },
            children: [
              {
                name: "vellore local vendor1",
                level: "local",
                email: "local@vendor.com",
                phone: "1111111111",
                location: "vellore",
                status: "active",
                permissions: ["manage_drivers"],
                stats: { drivers: 1, vehicles: 1, activeVehicles: 1, pendingApprovals: 0 },
                children: []
              }
            ]
          }
        ]
      },
      {
        name: "Regional Vendor East",
        level: "regional",
        email: "east@vendor.com",
        phone: "8888881111",
        location: "East",
        status: "active",
        permissions: ["manage_drivers"],
        stats: { drivers: 0, vehicles: 0, activeVehicles: 0, pendingApprovals: 0 },
        children: [
          {
            name: "Mumbai City Vendor",
            level: "city",
            email: "mumbai@vendor.com",
            phone: "5555555555",
            location: "Mumbai",
            status: "active",
            permissions: ["view_reports"],
            stats: { drivers: 2, vehicles: 2, activeVehicles: 2, pendingApprovals: 0 },
            children: [
              {
                name: "Mumbai Local Vendor 1",
                level: "local",
                email: "mumbailocal@vendor.com",
                phone: "3333333333",
                location: "Mumbai",
                status: "active",
                permissions: [],
                stats: { drivers: 0, vehicles: 0, activeVehicles: 0, pendingApprovals: 0 },
                children: []
              }
            ]
          }
        ]
      }
    ]
  }
];

// Renders vendor hierarchy tree
function renderTree(data, parentElement) {
  data.forEach(vendor => {
    const li = document.createElement("li");
    li.textContent = vendor.name;
    li.dataset.email = vendor.email;

    li.addEventListener("click", e => {
      e.stopPropagation();
      document.querySelectorAll("#vendorTree li").forEach(li => li.classList.remove("selected"));
      li.classList.add("selected");
      showVendorDetails(vendor);
    });

    parentElement.appendChild(li);

    if (vendor.children && vendor.children.length > 0) {
      const ul = document.createElement("ul");
      li.appendChild(ul);
      renderTree(vendor.children, ul);
    }
  });
}

// Shows selected vendor details
function showVendorDetails(vendor) {
  const panel = document.getElementById("vendorDetails");
  panel.innerHTML = `
    <h3>${vendor.name}</h3>
    <p><strong>Email:</strong> ${vendor.email}</p>
    <div class="info-grid">
      <div><strong>Level:</strong> <span class="badge ${vendor.level}">${vendor.level}</span></div>
      <div><strong>Location:</strong> ${vendor.location}</div>
      <div><strong>Phone:</strong> ${vendor.phone}</div>
      <div><strong>Status:</strong> <span class="badge active">${vendor.status}</span></div>
    </div>
    <p style="margin-top: 12px;"><strong>Permissions:</strong></p>
    <div class="permissions">
      ${vendor.permissions.map(p => `<span>${p}</span>`).join("")}
    </div>
    <div class="stats">
      <div><strong>${vendor.stats.drivers}</strong>Total Drivers</div>
      <div><strong>${vendor.stats.vehicles}</strong>Total Vehicles</div>
      <div><strong>${vendor.stats.activeVehicles}</strong>Active Vehicles</div>
      <div><strong>${vendor.stats.pendingApprovals}</strong>Pending Approvals</div>
    </div>
    <div class="action-buttons">
      <button class="btn purple">Edit Vendor</button>
      <button class="btn gray">Manage Permissions</button>
    </div>
  `;
}

// Initial load
document.addEventListener("DOMContentLoaded", () => {
  const treeRoot = document.getElementById("vendorTree");
  renderTree(vendorData, treeRoot);
});
