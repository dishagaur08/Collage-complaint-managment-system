const BASE_URL = "http://localhost:8080";

function registerComplaint() {
    let data = {
        name: document.getElementById("name").value,
        contact: document.getElementById("contact").value,
        issue: document.getElementById("issue").value
    };

    fetch(BASE_URL + "/register", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    })
    .then(res => res.json())
    .then(response => {
        if (response.error) {
            document.getElementById("registerMsg").innerHTML = `<span style="color: red;">${response.error}</span>`;
        } else {
            document.getElementById("registerMsg").innerHTML = `<span style="color: green;">${response.message}</span>`;
        }
    })
    .catch(error => {
        console.error("Error registering complaint:", error);
        document.getElementById("registerMsg").innerHTML = `<span style="color: red;">Error registering complaint. Please try again.</span>`;
    });
}

function searchComplaint() {
    let id = document.getElementById("searchId").value;

    fetch(BASE_URL + "/search?id=" + id)
    .then(res => res.json())
    .then(data => {
        if (data.error) {
            document.getElementById("searchResult").textContent = data.error;
        } else if (data) {
            document.getElementById("searchResult").textContent = 
                `Complaint ID: ${data.id}\n` +
                `Name: ${data.name}\n` +
                `Contact: ${data.contact}\n` +
                `Issue: ${data.issue}\n` +
                `Status: ${data.status}`;
        } else {
            document.getElementById("searchResult").textContent = "Complaint not found.";
        }
    })
    .catch(error => {
        console.error("Error searching complaint:", error);
        document.getElementById("searchResult").textContent = "Error searching complaint. Please try again.";
    });
}

function getAllComplaints() {
    fetch(BASE_URL + "/all")
    .then(res => res.json())
    .then(data => {
        if (data && data.length > 0) {
            let complaintList = data.map(complaint => 
                `Complaint ID: ${complaint.id}\n` +
                `Name: ${complaint.name}\n` +
                `Contact: ${complaint.contact}\n` +
                `Issue: ${complaint.issue}\n` +
                `Status: ${complaint.status}\n`
            ).join("\n--------------------\n");
            document.getElementById("allComplaints").textContent = complaintList;
        } else {
            document.getElementById("allComplaints").textContent = "No complaints found.";
        }
    })
    .catch(error => {
        console.error("Error fetching all complaints:", error);
        document.getElementById("allComplaints").textContent = "Error fetching complaints. Please try again.";
    });
}
