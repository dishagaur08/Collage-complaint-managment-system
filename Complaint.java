public class Complaint {
    public int id;
    public String name;
    public String contact;
    public String issue;
    public String status;
    public Complaint(String name, String contact, String issue) {
        this.name = name;
        this.contact = contact;
        this.issue = issue;
        this.status = "Pending";
    }

    public Complaint(int id, String name, String contact, String issue, String status) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.issue = issue;
        this.status = status;
    }

    @Override
    public String toString() {
        return "ID: " + id +
               " | Name: " + name +
               " | Contact: " + contact +
               " | Issue: " + issue +
               " | Status: " + status;
    }
}
