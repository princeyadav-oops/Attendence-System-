import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class AttendanceSystem {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String EMPLOYEES_FILE = "employees.dat";
    private static final String ATTENDANCE_FILE = "attendance.dat";
    
    private static Map<String, Employee> employees = new HashMap<>();
    private static Map<String, List<AttendanceRecord>> attendanceRecords = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public static void main(String[] args) {
        loadData();
        
        while (true) {
            System.out.println("==== Company Employee Attendance System ====");
            System.out.println("1. Admin Login");
            System.out.println("2. Add New Employee");
            System.out.println("3. View All Employees");
            System.out.println("4. Mark Attendance");
            System.out.println("5. View Daily Report");
            System.out.println("6. View Monthly Report");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // clear invalid input
                continue;
            }
            
            switch (choice) {
                case 1:
                    adminLogin();
                    break;
                case 2:
                    addNewEmployee();
                    break;
                case 3:
                    viewAllEmployees();
                    break;
                case 4:
                    markAttendance();
                    break;
                case 5:
                    viewDailyReport();
                    break;
                case 6:
                    viewMonthlyReport();
                    break;
                case 7:
                    saveData();
                    System.out.println("Exiting system. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private static void adminLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            System.out.println("Admin login successful!");
            adminMenu();
        } else {
            System.out.println("Invalid credentials. Access denied.");
        }
    }
    
    private static void adminMenu() {
        while (true) {
            System.out.println("\n==== Admin Menu ====");
            System.out.println("1. Add New Employee");
            System.out.println("2. View All Employees");
            System.out.println("3. Generate Daily Report");
            System.out.println("4. Generate Monthly Report");
            System.out.println("5. Return to Main Menu");
            System.out.print("Enter your choice: ");
            
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // clear invalid input
                continue;
            }
            
            switch (choice) {
                case 1:
                    addNewEmployee();
                    break;
                case 2:
                    viewAllEmployees();
                    break;
                case 3:
                    viewDailyReport();
                    break;
                case 4:
                    viewMonthlyReport();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private static void addNewEmployee() {
        System.out.println("\n==== Add New Employee ====");
        System.out.print("Enter employee ID: ");
        String id = scanner.nextLine();
        
        if (employees.containsKey(id)) {
            System.out.println("Employee with this ID already exists.");
            return;
        }
        
        System.out.print("Enter employee name: ");
        String name = scanner.nextLine();
        System.out.print("Enter employee department: ");
        String department = scanner.nextLine();
        System.out.print("Enter employee position: ");
        String position = scanner.nextLine();
        
        Employee employee = new Employee(id, name, department, position);
        employees.put(id, employee);
        System.out.println("Employee added successfully!");
    }
    
    private static void viewAllEmployees() {
        System.out.println("\n==== All Employees ====");
        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }
        
        System.out.printf("%-10s %-20s %-15s %-15s\n", "ID", "Name", "Department", "Position");
        for (Employee employee : employees.values()) {
            System.out.printf("%-10s %-20s %-15s %-15s\n", 
                employee.getId(), 
                employee.getName(), 
                employee.getDepartment(), 
                employee.getPosition());
        }
    }
    
    private static void markAttendance() {
        System.out.println("\n==== Mark Attendance ====");
        System.out.print("Enter employee ID: ");
        String id = scanner.nextLine();
        
        if (!employees.containsKey(id)) {
            System.out.println("Employee not found.");
            return;
        }
        
        String today = dateFormat.format(new Date());
        System.out.print("Enter status (P for Present, A for Absent, L for Leave): ");
        String status = scanner.nextLine().toUpperCase();
        
        if (!status.equals("P") && !status.equals("A") && !status.equals("L")) {
            System.out.println("Invalid status. Please enter P, A, or L.");
            return;
        }
        
        AttendanceRecord record = new AttendanceRecord(id, today, status);
        
        if (!attendanceRecords.containsKey(today)) {
            attendanceRecords.put(today, new ArrayList<>());
        }
        
        // Check if attendance already marked for today
        for (AttendanceRecord r : attendanceRecords.get(today)) {
            if (r.getEmployeeId().equals(id)) {
                System.out.println("Attendance already marked for this employee today.");
                return;
            }
        }
        
        attendanceRecords.get(today).add(record);
        System.out.println("Attendance marked successfully!");
    }
    
    private static void viewDailyReport() {
        System.out.println("\n==== Daily Report ====");
        System.out.print("Enter date (yyyy-MM-dd) or leave blank for today: ");
        String dateStr = scanner.nextLine();
        
        String date;
        if (dateStr.isEmpty()) {
            date = dateFormat.format(new Date());
        } else {
            date = dateStr;
        }
        
        if (!attendanceRecords.containsKey(date)) {
            System.out.println("No attendance records found for " + date);
            return;
        }
        
        System.out.println("\nAttendance Report for " + date);
        System.out.printf("%-10s %-20s %-10s\n", "ID", "Name", "Status");
        
        int present = 0, absent = 0, leave = 0;
        
        for (AttendanceRecord record : attendanceRecords.get(date)) {
            Employee employee = employees.get(record.getEmployeeId());
            String status = record.getStatus().equals("P") ? "Present" : 
                           record.getStatus().equals("A") ? "Absent" : "Leave";
            
            System.out.printf("%-10s %-20s %-10s\n", 
                employee.getId(), 
                employee.getName(), 
                status);
            
            if (record.getStatus().equals("P")) present++;
            else if (record.getStatus().equals("A")) absent++;
            else leave++;
        }
        
        System.out.println("\nSummary:");
        System.out.println("Present: " + present);
        System.out.println("Absent: " + absent);
        System.out.println("Leave: " + leave);
        System.out.println("Total: " + (present + absent + leave));
    }
    
    private static void viewMonthlyReport() {
        System.out.println("\n==== Monthly Report ====");
        System.out.print("Enter year and month (yyyy-MM): ");
        String monthStr = scanner.nextLine() + "-";
        
        System.out.println("\nMonthly Attendance Report for " + monthStr.substring(0, 7));
        System.out.printf("%-10s %-20s %-10s %-10s %-10s\n", 
            "ID", "Name", "Present", "Absent", "Leave");
        
        Map<String, int[]> employeeStats = new HashMap<>();
        
        for (String date : attendanceRecords.keySet()) {
            if (date.startsWith(monthStr)) {
                for (AttendanceRecord record : attendanceRecords.get(date)) {
                    if (!employeeStats.containsKey(record.getEmployeeId())) {
                        employeeStats.put(record.getEmployeeId(), new int[3]);
                    }
                    
                    if (record.getStatus().equals("P")) employeeStats.get(record.getEmployeeId())[0]++;
                    else if (record.getStatus().equals("A")) employeeStats.get(record.getEmployeeId())[1]++;
                    else employeeStats.get(record.getEmployeeId())[2]++;
                }
            }
        }
        
        for (Map.Entry<String, int[]> entry : employeeStats.entrySet()) {
            Employee employee = employees.get(entry.getKey());
            int[] stats = entry.getValue();
            System.out.printf("%-10s %-20s %-10d %-10d %-10d\n", 
                employee.getId(), 
                employee.getName(), 
                stats[0], stats[1], stats[2]);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EMPLOYEES_FILE))) {
            employees = (Map<String, Employee>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No employee data found. Starting with empty database.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading employee data: " + e.getMessage());
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ATTENDANCE_FILE))) {
            attendanceRecords = (Map<String, List<AttendanceRecord>>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No attendance data found. Starting with empty database.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading attendance data: " + e.getMessage());
        }
    }
    
    private static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EMPLOYEES_FILE))) {
            oos.writeObject(employees);
        } catch (IOException e) {
            System.out.println("Error saving employee data: " + e.getMessage());
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ATTENDANCE_FILE))) {
            oos.writeObject(attendanceRecords);
        } catch (IOException e) {
            System.out.println("Error saving attendance data: " + e.getMessage());
        }
    }
    
    static class Employee implements Serializable {
        private String id;
        private String name;
        private String department;
        private String position;
        
        public Employee(String id, String name, String department, String position) {
            this.id = id;
            this.name = name;
            this.department = department;
            this.position = position;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public String getDepartment() { return department; }
        public String getPosition() { return position; }
    }
    
    static class AttendanceRecord implements Serializable {
        private String employeeId;
        private String date;
        private String status;
        
        public AttendanceRecord(String employeeId, String date, String status) {
            this.employeeId = employeeId;
            this.date = date;
            this.status = status;
        }
        
        public String getEmployeeId() { return employeeId; }
        public String getDate() { return date; }
        public String getStatus() { return status; }
    }
}